package com.example.Server.security.service.impl;

import com.example.Server.enums.EToken;
import com.example.Server.model.User;
import com.example.Server.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
  @Value("${jwt.secrets.access-token}")
  private String accessTokenSecret;

  @Value("${jwt.secrets.refresh-token}")
  private String refreshTokenSecret;

  @Value("${jwt.durations.access-token}")
  private Long accessTokenDuration;

  @Value("${jwt.durations.refresh-token}")
  private Long refreshTokenDuration;

  @Override
  public <T> T extractClaims(String token, EToken type, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token, type);
    return claimsResolver.apply(claims);
  }

  @Override
  public String extractUsername(String token, EToken type) {
    return extractClaims(token, type, Claims::getSubject);
  }

  @Override
  public String generateToken(Map<String, Object> extraClaims, User user, EToken type) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(user.getEmail())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + getTokenDuration(type)))
        .signWith(getSignInKey(type), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String generateToken(User user, EToken type) {
    return generateToken(new HashMap<>(), user, type);
  }

  @Override
  public boolean isTokenExpired(String token, EToken type) {
    return extractExpiration(token, type).before(new Date());
  }

  @Override
  public boolean isTokenValid(String token, User user, EToken type) {
    String email = extractUsername(token, type);
    return email.equals(user.getEmail()) && !isTokenExpired(token, type);
  }

  private Claims extractAllClaims(String token, EToken type) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey(type))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey(EToken type) {
    byte[] keyBytes = Decoders.BASE64.decode(getSecretKey(type));
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Date extractExpiration(String token, EToken type) {
    return extractClaims(token, type, Claims::getExpiration);
  }

  private String getSecretKey(EToken type) {
    return switch (type) {
      case ACCESS -> accessTokenSecret;
      case REFRESH -> refreshTokenSecret;
    };
  }

  public Long getTokenDuration(EToken type) {
    return switch (type) {
      case ACCESS -> accessTokenDuration;
      case REFRESH -> refreshTokenDuration;
    };
  }
}
