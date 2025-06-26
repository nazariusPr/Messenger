package com.example.Server.repository;

import com.example.Server.model.Message;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
  @Query(
      """
           SELECT m
           FROM Message m
           WHERE m.chat.id = :chatId
           ORDER BY m.createdAt DESC
          """)
  Page<Message> findByChatId(UUID chatId, Pageable pageable);
}
