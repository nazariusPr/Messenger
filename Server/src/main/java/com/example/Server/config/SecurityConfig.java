package com.example.Server.config;

import static com.example.Server.constant.AppConstants.AUTH_LINK;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.example.Server.security.filter.JwtAuthenticationFilter;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
  private final JwtAuthenticationFilter authenticationFilter;

  @Value("${client.url}")
  private String CLIENT_URL;

  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String GOOGLE_CLIENT_ID;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(c -> c.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(AUTH_LINK + "/**", "/ws/**")
                    .permitAll()
                    .requestMatchers(
                        "/v2/api-docs/**",
                        "/v3/api-docs/**",
                        "/swagger.json",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/webjars/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(
                        (req, resp, exc) -> resp.sendError(SC_UNAUTHORIZED, "Authorize first."))
                    .accessDeniedHandler(
                        (req, resp, exc) ->
                            resp.sendError(SC_FORBIDDEN, "You don't have authorities.")))
        .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public GoogleIdTokenVerifier verifier() {
    NetHttpTransport transport = new NetHttpTransport();
    JsonFactory jsonFactory = new GsonFactory();

    return new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
        .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOrigins(Collections.singletonList(CLIENT_URL));
    config.setAllowedHeaders(Collections.singletonList("*"));
    config.setExposedHeaders(Collections.singletonList("Authorization"));
    config.setAllowedMethods(
        Arrays.asList("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
