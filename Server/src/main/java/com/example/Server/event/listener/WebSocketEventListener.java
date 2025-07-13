package com.example.Server.event.listener;

import com.example.Server.dto.user.UserStatusDto;
import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@AllArgsConstructor
public class WebSocketEventListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    Principal principal = accessor.getUser();

    String email = principal != null ? principal.getName() : null;
    if (email != null) {
      onlineUsers.add(email);
      broadcastUserStatus(email, true);

      log.info("User connected: {}", email);
    }
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    Principal principal = accessor.getUser();

    String email = principal != null ? principal.getName() : null;
    if (email != null) {
      onlineUsers.remove(email);
      broadcastUserStatus(email, false);

      log.info("User disconnected: {}", email);
    }
  }

  public boolean isUserOnline(String email) {
    return onlineUsers.contains(email);
  }

  private void broadcastUserStatus(String email, boolean isOnline) {
    messagingTemplate.convertAndSend(
        "/topic/user-status/" + email, new UserStatusDto(email, isOnline));
  }
}
