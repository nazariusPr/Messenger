package com.example.Server.service.impl;

import com.example.Server.dto.participant.ParticipantDto;
import com.example.Server.enums.EParticipantStatus;
import com.example.Server.model.Chat;
import com.example.Server.model.Participant;
import com.example.Server.model.User;
import com.example.Server.model.id.ParticipantId;
import com.example.Server.repository.ParticipantRepository;
import com.example.Server.service.ParticipantService;
import com.example.Server.service.UserService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
  private final UserService userService;
  private final ParticipantRepository repository;

  @Override
  public List<Participant> create(List<ParticipantDto> participantDtos, Chat chat) {
    List<Participant> participants = new ArrayList<>();

    for (ParticipantDto participantDto : participantDtos) {
      String email = participantDto.getEmail();
      EParticipantStatus status = participantDto.getStatus();

      User user = userService.findByEmail(email);
      ParticipantId id = new ParticipantId(user.getId(), chat.getId());
      Participant participant = new Participant(id, status, user, chat);

      participants.add(participant);
    }

    return repository.saveAll(participants);
  }
}
