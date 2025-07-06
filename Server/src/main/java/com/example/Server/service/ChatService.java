package com.example.Server.service;

import com.example.Server.dto.chat.ChatRequestDto;
import com.example.Server.dto.chat.ChatResponseDto;
import com.example.Server.dto.general.PageDto;
import com.example.Server.model.Chat;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ChatService {
  PageDto<ChatResponseDto> read(String email, Pageable pageable);

  ChatResponseDto create(String senderEmail, String receiverEmail);

  ChatResponseDto create(ChatRequestDto chatRequestDto, String creatorEmail);

  void delete(UUID id);

  Chat findById(UUID id);
}
