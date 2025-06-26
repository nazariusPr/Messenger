package com.example.Server.controller;

import static com.example.Server.constant.AppConstants.USER_LINK;

import com.example.Server.dto.general.PageDto;
import com.example.Server.dto.user.UserDto;
import com.example.Server.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(USER_LINK)
public class UserController {
  private final UserService userService;

  @GetMapping("/search")
  public ResponseEntity<PageDto<UserDto>> search(
      @RequestParam String email, @Parameter(hidden = true) Pageable pageable) {
    log.info("Searching users by email: {}", email);
    PageDto<UserDto> users = userService.search(email, pageable);
    return ResponseEntity.ok(users);
  }
}
