package com.example.Server.service.impl;

import static com.example.Server.util.GeneralUtil.buildPageDto;

import com.example.Server.dto.chat.ChatRequestDto;
import com.example.Server.dto.chat.ChatResponseDto;
import com.example.Server.dto.general.PageDto;
import com.example.Server.dto.participant.ParticipantDto;
import com.example.Server.enums.EParticipantStatus;
import com.example.Server.mapper.ChatMapper;
import com.example.Server.model.Chat;
import com.example.Server.model.Participant;
import com.example.Server.repository.ChatRepository;
import com.example.Server.service.ChatService;
import com.example.Server.service.ParticipantService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
  private final ParticipantService participantService;
  private final ChatRepository repository;
  private final ChatMapper mapper;

  @Override
  public PageDto<ChatResponseDto> read(String email, Pageable pageable) {
    Page<Chat> chats = repository.findUserChats(email, pageable);
    return buildPageDto(chats, mapper::entityToDto);
  }

  @Override
  public ChatResponseDto create(String senderEmail, String receiverEmail) {
    Optional<Chat> existingChat =
        repository.findPrivateChatByParticipants(senderEmail, receiverEmail);

    if (existingChat.isPresent()) {
      return mapper.entityToDto(existingChat.get());
    }

    Chat chat = new Chat();
    chat.setGroup(false);

    chat = repository.save(chat);

    List<ParticipantDto> participantDtos =
        List.of(
            new ParticipantDto(null, senderEmail, EParticipantStatus.USER),
            new ParticipantDto(null, receiverEmail, EParticipantStatus.USER));
    List<Participant> participants = participantService.create(participantDtos, chat);

    // Is needed for correct mapping
    chat.getParticipants().addAll(participants);
    return mapper.entityToDto(chat);
  }

  @Override
  public ChatResponseDto create(ChatRequestDto chatRequestDto, String creatorEmail) {
    Chat chat = new Chat();
    chat.setName(chatRequestDto.getName());
    chat.setDescription(chatRequestDto.getDescription());
    chat.setGroup(true);

    chat = repository.save(chat);

    List<ParticipantDto> participantDtos =
        List.of(new ParticipantDto(null, creatorEmail, EParticipantStatus.OWNER));
    List<Participant> participants = participantService.create(participantDtos, chat);

    // Is needed for correct mapping
    chat.getParticipants().addAll(participants);
    return mapper.entityToDto(chat);
  }

  @Override
  public void delete(UUID id) {
    Chat chat = findById(id);
    repository.delete(chat);
  }

  @Override
  public Chat findById(UUID id) {
    return repository.findById(id).orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public boolean isUserParticipant(UUID chatId, String email) {
    Chat chat = findById(chatId);
    return chat.getParticipants().stream()
        .anyMatch(participant -> participant.getUser().getEmail().equalsIgnoreCase(email));
  }
}
