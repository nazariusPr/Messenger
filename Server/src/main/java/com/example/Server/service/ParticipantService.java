package com.example.Server.service;

import com.example.Server.dto.participant.ParticipantDto;
import com.example.Server.model.Chat;
import com.example.Server.model.Participant;
import java.util.List;
import java.util.UUID;

public interface ParticipantService {
  List<Participant> create(List<ParticipantDto> emails, Chat chat);

  boolean isUserParticipant(UUID chatId, String email);
}
