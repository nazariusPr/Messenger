package com.example.Server.dto.chat;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class ChatRequestDto {
  @Size(max = 100, message = "Name must be at most 100 characters")
  private String name;

  @Size(max = 1000, message = "Description must be at most 1000 characters")
  private String description;
}
