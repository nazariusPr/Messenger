package com.example.Server.security.controller;

import static com.example.Server.constant.AppConstants.AUTH_LINK;

import com.example.Server.security.dto.AccessRefreshTokenDto;
import com.example.Server.security.dto.AccessTokenDto;
import com.example.Server.security.dto.TokenDto;
import com.example.Server.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(AUTH_LINK)
public class AuthController {
  private final AuthService authService;

  @PostMapping("/google")
  public ResponseEntity<AccessRefreshTokenDto> googleOAuth2(@Valid @RequestBody TokenDto tokenDto) {
    log.info("**/ Google auth");
    return ResponseEntity.ok(authService.googleAuth(tokenDto));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AccessTokenDto> refreshToken(@RequestBody TokenDto tokenDto) {
    String token = tokenDto.getToken();
    log.info("**/ Refreshing access token");
    return ResponseEntity.ok(authService.refreshToken(token));
  }
}
