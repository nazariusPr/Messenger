package com.example.Server.controller;

import static com.example.Server.constant.AppConstants.CHAT_LINK;

import com.example.Server.dto.chat.ChatRequestDto;
import com.example.Server.dto.chat.ChatResponseDto;
import com.example.Server.dto.general.EmailDto;
import com.example.Server.dto.general.PageDto;
import com.example.Server.service.ChatService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(CHAT_LINK)
public class ChatController {
  private final ChatService chatService;

  @GetMapping
  public ResponseEntity<PageDto<ChatResponseDto>> read(
      Principal principal, @Parameter(hidden = true) Pageable pageable) {
    log.info("Getting user's chats");
    PageDto<ChatResponseDto> chats = chatService.read(principal.getName(), pageable);
    return ResponseEntity.ok(chats);
  }

  @PostMapping("/private")
  public ResponseEntity<ChatResponseDto> createPrivateChat(
      @Valid @RequestBody EmailDto emailDto, Principal principal) {
    String receiverEmail = emailDto.getEmail();
    log.info("Creating private chat between {} and {}", principal.getName(), receiverEmail);

    ChatResponseDto chat = chatService.create(principal.getName(), receiverEmail);
    return ResponseEntity.ok(chat);
  }

  @PostMapping("/group")
  public ResponseEntity<ChatResponseDto> createGroupChat(
      @Valid @RequestBody ChatRequestDto requestDto, Principal principal) {
    log.info("Creating group chat by user: {}", principal.getName());
    ChatResponseDto chat = chatService.create(requestDto, principal.getName());
    return ResponseEntity.ok(chat);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteChat(@PathVariable UUID id) {
    log.info("Deleting chat with id: {}", id);
    chatService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
