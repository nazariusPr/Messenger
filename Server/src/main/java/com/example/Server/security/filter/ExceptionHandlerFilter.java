package com.example.Server.security.filter;

import com.example.Server.dto.general.ExceptionDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      handleException(response, e);
    }
  }

  private void handleException(HttpServletResponse response, Exception e) throws IOException {
    HttpStatus status =
        e instanceof JwtException ? HttpStatus.FORBIDDEN : HttpStatus.INTERNAL_SERVER_ERROR;

    log.error(e.getMessage());
    String message =
        e instanceof JwtException
            ? "NOT VALID JWT"
            : "An unexpected error occurred : " + e.getMessage();

    ExceptionDto errorResponse = new ExceptionDto();
    errorResponse.setMessage(message);

    response.setStatus(status.value());
    response.setContentType("application/json");
    response.getWriter().write(convertObjectToJson(errorResponse));
    response.getWriter().flush();
  }

  private String convertObjectToJson(Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      return "{\"error\": \"Failed to serialize error response\"}";
    }
  }
}
