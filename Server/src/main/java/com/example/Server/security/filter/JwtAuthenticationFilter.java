package com.example.Server.security.filter;

import com.example.Server.dto.general.ExceptionDto;
import com.example.Server.enums.EToken;
import com.example.Server.model.User;
import com.example.Server.security.service.JwtService;
import com.example.Server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final UserService userService;
  private final JwtService jwtService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      final String accessToken = authHeader.substring(7);
      final String email = jwtService.extractUsername(accessToken, EToken.ACCESS);

      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        User user = userService.findByEmail(email);

        if (jwtService.isTokenValid(accessToken, user, EToken.ACCESS)) {
          Authentication authentication =
              new UsernamePasswordAuthenticationToken(email, null, null);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }

      filterChain.doFilter(request, response);
    } catch (Exception e) {
      handleException(response, e);
    }
  }

  private void handleException(HttpServletResponse response, Exception e) throws IOException {
    String message =
        e instanceof JwtException
            ? "NOT VALID JWT"
            : "An unexpected error occurred: " + e.getMessage();

    ExceptionDto errorResponse = new ExceptionDto();
    errorResponse.setMessage(message);

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    response.getWriter().flush();
  }
}
