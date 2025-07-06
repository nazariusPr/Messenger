package com.example.Server.security.controller;

import static com.example.Server.constant.AppConstants.AUTH_LINK;
import static com.example.Server.utils.SecurityUtils.clearRefreshTokenCookie;
import static com.example.Server.utils.SecurityUtils.createRefreshTokenCookie;

import com.example.Server.security.dto.AccessRefreshTokenDto;
import com.example.Server.security.dto.AccessTokenDto;
import com.example.Server.security.dto.TokenDto;
import com.example.Server.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(AUTH_LINK)
public class AuthController {
  private final AuthService authService;

  @PostMapping("/google")
  public ResponseEntity<AccessTokenDto> googleOAuth2(@Valid @RequestBody TokenDto tokenDto) {
    log.info("**/ Google auth");

    AccessRefreshTokenDto tokens = authService.googleAuth(tokenDto);
    ResponseCookie refreshCookie = createRefreshTokenCookie(tokens.getRefreshToken());

    return ResponseEntity.ok()
        .header("Set-Cookie", refreshCookie.toString())
        .body(new AccessTokenDto(tokens.getAccessToken()));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AccessTokenDto> refreshToken(
      @CookieValue(name = "refreshToken") String refreshToken) {
    log.info("**/ Refreshing access token");
    return ResponseEntity.ok(authService.refreshToken(refreshToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    log.info("**/ Clearing refresh token");

    ResponseCookie clearCookie = clearRefreshTokenCookie();
    return ResponseEntity.noContent().header("Set-Cookie", clearCookie.toString()).build();
  }
}
