package com.example.Server.service;

import com.example.Server.dto.general.PageDto;
import com.example.Server.dto.message.MessageRequestDto;
import com.example.Server.dto.message.MessageResponseDto;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {
  MessageResponseDto create(MessageRequestDto message, String senderEmail, UUID chatId);

  PageDto<MessageResponseDto> read(UUID chatId, Pageable pageable);
}
