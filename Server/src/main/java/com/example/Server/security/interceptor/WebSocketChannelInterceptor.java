package com.example.Server.security.interceptor;

import com.example.Server.enums.EToken;
import com.example.Server.model.User;
import com.example.Server.security.service.JwtService;
import com.example.Server.service.ParticipantService;
import com.example.Server.service.UserService;
import java.util.List;
import java.util.Set;
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
  private final ParticipantService participantService;
  private final JwtService jwtService;

  private static final Set<String> SKIPPED_DESTINATION_PREFIXES = Set.of("/topic/user-status/");

  @NonNull
  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null) return message;

    StompCommand command = accessor.getCommand();
    String destination = accessor.getDestination();
    String accessToken = resolveJwtFromAccessor(accessor);

    switch (command) {
      case CONNECT -> {
        authenticateUser(accessor, accessToken);
      }
      case SUBSCRIBE -> {
        if (!shouldSkipAuthorization(destination)) {
          authorizeUserForChat(accessor, destination);
        }
      }
      case SEND -> {
        authorizeUserForChat(accessor, destination);
      }
      default -> {
        // No action needed for other commands (e.g., DISCONNECT)
      }
    }

    return message;
  }

  private void authenticateUser(StompHeaderAccessor accessor, String accessToken) {
    if (accessToken == null) {
      return;
    }

    String email = jwtService.extractUsername(accessToken, EToken.ACCESS);
    User user = userService.findByEmail(email);

    if (jwtService.isTokenValid(accessToken, user, EToken.ACCESS)) {
      Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, null);
      accessor.setUser(authentication);
    }
  }

  private void authorizeUserForChat(StompHeaderAccessor accessor, String destination) {
    if (accessor.getUser() == null) {
      throw new IllegalStateException("User is not authenticated");
    }

    if (destination == null) {
      throw new IllegalArgumentException("Destination is required for authorization");
    }

    UUID chatId = extractChatId(destination);
    String email = accessor.getUser().getName();

    boolean isInChat = participantService.isUserParticipant(chatId, email);
    if (!isInChat) {
      throw new IllegalStateException("User " + email + " is not a participant in chat " + chatId);
    }
  }

  private boolean shouldSkipAuthorization(String destination) {
    if (destination == null) return false;
    return SKIPPED_DESTINATION_PREFIXES.stream().anyMatch(destination::startsWith);
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
    // Assumes format: /topic/chat/{uuid} or "/chat/send/{uuid}"
    try {
      String[] parts = destination.split("/");
      return UUID.fromString(parts[parts.length - 1]);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid destination (expected UUID): " + destination);
    }
  }
}
