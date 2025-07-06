package com.example.Server.service.impl;

import static com.example.Server.utils.GeneralUtils.buildPageDto;

import com.example.Server.dto.general.PageDto;
import com.example.Server.dto.message.MessageRequestDto;
import com.example.Server.dto.message.MessageResponseDto;
import com.example.Server.mapper.MessageMapper;
import com.example.Server.model.Chat;
import com.example.Server.model.Message;
import com.example.Server.model.User;
import com.example.Server.repository.MessageRepository;
import com.example.Server.service.ChatService;
import com.example.Server.service.MessageService;
import com.example.Server.service.UserService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
  private final UserService userService;
  private final ChatService chatService;
  private final MessageRepository repository;
  private final MessageMapper mapper;
  private final SimpMessagingTemplate template;

  @Override
  public MessageResponseDto create(
      MessageRequestDto messageRequestDto, String senderEmail, UUID chatId) {
    User sender = userService.findByEmail(senderEmail);
    Chat chat = chatService.findById(chatId);

    Message message = mapper.dtoToEntity(messageRequestDto, sender, chat);
    message = repository.save(message);

    MessageResponseDto messageResponseDto = mapper.entityToDto(message);

    template.convertAndSend("/topic/chat/" + chatId, messageResponseDto);

    return messageResponseDto;
  }

  @Override
  public PageDto<MessageResponseDto> read(UUID chatId, Pageable pageable) {
    Page<Message> messages = repository.findByChatId(chatId, pageable);
    return buildPageDto(messages, mapper::entityToDto);
  }
}
