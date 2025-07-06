package com.example.Server.utils;

import static com.example.Server.constant.AppConstants.AUTH_LINK;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
  private final Base64.Encoder encoder = Base64.getEncoder();
  private final Base64.Decoder decoder = Base64.getDecoder();

  @Value("${encryption.key}")
  private String key;

  @Value("${encryption.algo}")
  private String algo;

  private SecretKeySpec keySpec;

  @PostConstruct
  private void init() {
    if (!(key.length() == 16 || key.length() == 24 || key.length() == 32)) {
      throw new IllegalArgumentException("Invalid AES key length. Must be 16, 24, or 32 bytes.");
    }

    keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algo);
  }

  public static String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();

    if (principal instanceof String) {
      return (String) principal;
    }

    return "anonymous";
  }

  public String encrypt(String data) {
    try {
      Cipher cipher = Cipher.getInstance(algo);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec);

      byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return encoder.encodeToString(encryptedBytes);

    } catch (Exception e) {
      throw new IllegalStateException("Error occurred during encryption", e);
    }
  }

  public String decrypt(String encryptedData) {
    try {
      Cipher cipher = Cipher.getInstance(algo);
      cipher.init(Cipher.DECRYPT_MODE, keySpec);

      byte[] decodedBytes = decoder.decode(encryptedData);
      byte[] decryptedBytes = cipher.doFinal(decodedBytes);

      return new String(decryptedBytes, StandardCharsets.UTF_8);

    } catch (Exception e) {
      throw new IllegalStateException("Error occurred during decryption", e);
    }
  }

  public static ResponseCookie createRefreshTokenCookie(String token) {
    return ResponseCookie.from("refreshToken", token)
        .httpOnly(true)
        .secure(true)
        .path(AUTH_LINK)
        .maxAge(30 * 24 * 60 * 60) // 30 days
        .build();
  }

  public static ResponseCookie clearRefreshTokenCookie() {
    return ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .secure(true)
        .path(AUTH_LINK)
        .maxAge(0)
        .build();
  }
}
