package com.example.Server.repository;

import com.example.Server.model.Chat;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

  @Query(
      """
         SELECT c
         FROM Chat c
         WHERE c.isGroup = false
           AND SIZE(c.participants) = 2
           AND :senderEmail IN (
             SELECT p.user.email
             FROM c.participants p
           )
           AND :receiverEmail IN (
             SELECT p.user.email
             FROM c.participants p
           )
""")
  Optional<Chat> findPrivateChatByParticipants(String senderEmail, String receiverEmail);

  @Query(
      """
         SELECT DISTINCT c
         FROM Chat c
         WHERE :email IN (
           SELECT p.user.email
           FROM c.participants p
         )
 """)
  Page<Chat> findUserChats(String email, Pageable pageable);
}
