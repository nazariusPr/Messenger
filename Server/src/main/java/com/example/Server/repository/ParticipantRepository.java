package com.example.Server.repository;

import com.example.Server.model.Participant;
import com.example.Server.model.id.ParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {}
