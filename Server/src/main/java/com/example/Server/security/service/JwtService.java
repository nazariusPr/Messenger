package com.example.Server.security.service;

import com.example.Server.enums.EToken;
import com.example.Server.model.User;
import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
  <T> T extractClaims(String token, EToken type, Function<Claims, T> claimsResolver);

  String extractUsername(String token, EToken type);

  String generateToken(Map<String, Object> extraClaims, User user, EToken type);

  String generateToken(User user, EToken type);

  boolean isTokenExpired(String token, EToken type);

  boolean isTokenValid(String token, User user, EToken type);
}
