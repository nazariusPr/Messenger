package com.example.Server.security.interceptor;

import com.example.Server.enums.EToken;
import com.example.Server.model.User;
import com.example.Server.security.service.JwtService;
import com.example.Server.service.ChatService;
import com.example.Server.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {
  private final UserService userService;
  private final ChatService chatService;
  private final JwtService jwtService;

  @NonNull
  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    String accessToken = resolveJwtFromAccessor(accessor);

    StompCommand command = accessor.getCommand();

    if (StompCommand.CONNECT == command && accessToken != null) {
      String email = jwtService.extractUsername(accessToken, EToken.ACCESS);
      User user = userService.findByEmail(email);

      if (jwtService.isTokenValid(accessToken, user, EToken.ACCESS)) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, null);
        accessor.setUser(authentication);
      }

    } else if (StompCommand.SUBSCRIBE == command) {
      UUID chatId = extractChatId(accessor.getDestination());
      String email = accessor.getUser().getName();

      boolean isInChat = chatService.isUserParticipant(chatId, email);
      System.out.println("Subscribing");
      if (!isInChat) {
        System.out.println("In error block");
        throw new IllegalStateException("User not authorized to subscribe to this chat");
      }
    }
    return message;
  }

  private String resolveJwtFromAccessor(StompHeaderAccessor accessor) {
    List<String> authHeaders = accessor.getNativeHeader("Authorization");
    if (authHeaders != null && !authHeaders.isEmpty()) {
      String bearer = authHeaders.getFirst();
      if (bearer.startsWith("Bearer ")) {
        return bearer.substring(7);
      }
    }
    return null;
  }

  private UUID extractChatId(String destination) {
    // Assumes format: /topic/chat/{uuid}
    try {
      String[] parts = destination.split("/");
      return UUID.fromString(parts[parts.length - 1]);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid destination (expected UUID): " + destination);
    }
  }
}
