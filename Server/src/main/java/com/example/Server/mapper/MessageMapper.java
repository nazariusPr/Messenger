package com.example.Server.mapper;

import com.example.Server.dto.message.MessageResponseDto;
import com.example.Server.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
  @Mapping(source = "sender.email", target = "email")
  MessageResponseDto entityToDto(Message message);
}
