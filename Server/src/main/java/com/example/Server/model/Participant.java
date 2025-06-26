package com.example.Server.model;

import com.example.Server.enums.EParticipantStatus;
import com.example.Server.model.id.ParticipantId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_participants")
public class Participant {
  @EmbeddedId private ParticipantId id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private EParticipantStatus status;

  @ManyToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "chat_id", insertable = false, updatable = false, nullable = false)
  private Chat chat;
}
