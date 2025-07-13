package com.example.Server.controller;

import static com.example.Server.constant.AppConstants.USER_STATUS_LINK;

import com.example.Server.dto.user.UserStatusDto;
import com.example.Server.service.UserStatusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(USER_STATUS_LINK)
public class UserStatusController {
  private final UserStatusService userStatusService;

  @GetMapping
  public ResponseEntity<UserStatusDto> getUserStatus(@RequestParam String email) {
    log.info("Checking online status for user with email: {}", email);
    UserStatusDto statusDto = userStatusService.isUserOnline(email);

    return ResponseEntity.ok(statusDto);
  }
}
