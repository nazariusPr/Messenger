package com.example.Server.service.impl;

import com.example.Server.dto.user.UserStatusDto;
import com.example.Server.event.listener.WebSocketEventListener;
import com.example.Server.service.UserStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {
  private final WebSocketEventListener webSocketEventListener;

  @Override
  public UserStatusDto isUserOnline(String email) {
    boolean isOnline = webSocketEventListener.isUserOnline(email);

    UserStatusDto statusDto = new UserStatusDto();
    statusDto.setEmail(email);
    statusDto.setOnline(isOnline);

    return statusDto;
  }
}
