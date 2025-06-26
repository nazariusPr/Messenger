package com.example.Server.security.service.impl;

import com.example.Server.enums.EToken;
import com.example.Server.exception.exceptions.InvalidTokenException;
import com.example.Server.model.User;
import com.example.Server.security.dto.AccessRefreshTokenDto;
import com.example.Server.security.dto.AccessTokenDto;
import com.example.Server.security.dto.TokenDto;
import com.example.Server.security.service.AuthService;
import com.example.Server.security.service.JwtService;
import com.example.Server.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserService userService;
  private final JwtService jwtService;
  private final GoogleIdTokenVerifier googleVerifier;

  @Override
  public AccessRefreshTokenDto googleAuth(TokenDto authDto) {
    User user = processGoogleAuth(authDto.getToken());
    return grantTokens(user);
  }

  @Override
  public AccessTokenDto refreshToken(String refreshToken) {
    EToken type = EToken.REFRESH;

    if (jwtService.isTokenExpired(refreshToken, type)) {
      throw new InvalidTokenException("Refresh token has expired");
    }

    String email = jwtService.extractUsername(refreshToken, type);
    User user = userService.findByEmail(email);

    String accessToken = jwtService.generateToken(user, EToken.ACCESS);

    return new AccessTokenDto(accessToken);
  }

  private AccessRefreshTokenDto grantTokens(User user) {
    String accessToken = jwtService.generateToken(user, EToken.ACCESS);
    String refreshToken = jwtService.generateToken(user, EToken.REFRESH);

    AccessRefreshTokenDto tokenDto = new AccessRefreshTokenDto();
    tokenDto.setAccessToken(accessToken);
    tokenDto.setRefreshToken(refreshToken);

    return tokenDto;
  }

  private GoogleIdToken.Payload getGooglePayload(String token) {
    if (token == null || token.isEmpty()) {
      throw new InvalidTokenException("Google token must not be null or empty");
    }
    try {
      GoogleIdToken googleIdToken = googleVerifier.verify(token);
      if (googleIdToken == null) {
        throw new InvalidTokenException("Invalid Google token");
      }
      return googleIdToken.getPayload();

    } catch (GeneralSecurityException | IOException ex) {
      throw new InvalidTokenException("Failed to verify Google token", ex);
    }
  }

  private User processGoogleAuth(String token) {
    User user;
    GoogleIdToken.Payload payload = getGooglePayload(token);
    String email = payload.getEmail();

    if (userService.doesUserExist(email)) {
      user = userService.findByEmail(email);

    } else {
      user = userService.create(email);
    }
    return user;
  }
}
