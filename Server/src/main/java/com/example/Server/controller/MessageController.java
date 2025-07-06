package com.example.Server.controller;

import static com.example.Server.constant.AppConstants.MESSAGE_LINK;

import com.example.Server.dto.general.PageDto;
import com.example.Server.dto.message.MessageRequestDto;
import com.example.Server.dto.message.MessageResponseDto;
import com.example.Server.service.MessageService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(MESSAGE_LINK)
public class MessageController {
  private final MessageService messageService;

  @GetMapping
  public PageDto<MessageResponseDto> getMessages(
      @PathVariable UUID chatId, @Parameter(hidden = true) Pageable pageable) {
    log.info("Getting chat's messages");
    return messageService.read(chatId, pageable);
  }

  @MessageMapping("/chat/send/{convId}")
  public void sendMessageToConvId(
      @DestinationVariable UUID convId,
      @Valid @Payload MessageRequestDto message,
      Principal principal) {
    log.info("Sending message to chat");
    messageService.create(message, principal.getName(), convId);
  }
}
