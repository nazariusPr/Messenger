package com.example.Server.service;

import com.example.Server.dto.user.UserStatusDto;

public interface UserStatusService {
  UserStatusDto isUserOnline(String email);
}
