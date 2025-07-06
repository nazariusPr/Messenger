package com.example.Server.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chats")
@Entity
public class Chat {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String name;

  private String description;

  @Column(name = "is_group", nullable = false)
  private boolean isGroup;

  @Column(
      name = "created_at",
      nullable = false,
      updatable = false,
      columnDefinition = "TIMESTAMPTZ")
  private OffsetDateTime createdAt;

  @OneToMany(mappedBy = "chat", cascade = CascadeType.REMOVE)
  private Set<Message> messages = new HashSet<>();

  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
  private Set<Participant> participants = new HashSet<>();

  @PrePersist
  public void prePersist() {
    createdAt = OffsetDateTime.now(ZoneOffset.UTC);
  }
}
