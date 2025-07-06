package com.example.Server.repository;

import com.example.Server.model.Participant;
import com.example.Server.model.id.ParticipantId;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {
  @Query(
      """
                SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
                FROM Participant p
                WHERE p.chat.id = :chatId AND p.user.email = :email
            """)
  boolean existsByChatIdAndUserEmail(UUID chatId, String email);
}
