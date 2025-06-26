package com.example.Server.security.service;

import com.example.Server.security.dto.AccessRefreshTokenDto;
import com.example.Server.security.dto.AccessTokenDto;
import com.example.Server.security.dto.TokenDto;

public interface AuthService {
  AccessRefreshTokenDto googleAuth(TokenDto authDto);

  AccessTokenDto refreshToken(String refreshToken);
}
