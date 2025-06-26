package com.example.Server.dto.chat;

import com.example.Server.dto.message.MessageResponseDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class ChatResponseDto {
  private UUID id;
  private String name;
  private String description;
  private boolean isGroup;
  private MessageResponseDto lastMessage;
}
