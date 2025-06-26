package com.example.Server.mapper;

import static com.example.Server.util.GeneralUtil.resolvePrivateChatField;

import com.example.Server.dto.chat.ChatResponseDto;
import com.example.Server.dto.message.MessageResponseDto;
import com.example.Server.model.Chat;
import com.example.Server.model.Message;
import com.example.Server.model.User;
import java.util.Comparator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ChatMapper {
  protected MessageMapper messageMapper;

  @Autowired
  public void setMessageMapper(MessageMapper messageMapper) {
    this.messageMapper = messageMapper;
  }

  @Mapping(source = "chat", target = "name", qualifiedByName = "resolveChatName")
  @Mapping(source = "chat", target = "description", qualifiedByName = "resolveDescription")
  @Mapping(source = "chat", target = "lastMessage", qualifiedByName = "resolveLastMessage")
  public abstract ChatResponseDto entityToDto(Chat chat);

  @Named("resolveChatName")
  protected String resolveChatName(Chat chat) {
    return chat.isGroup() ? chat.getName() : resolvePrivateChatField(chat, User::getEmail);
  }

  @Named("resolveDescription")
  protected String resolveDescription(Chat chat) {
    return chat.isGroup()
        ? chat.getDescription()
        : resolvePrivateChatField(chat, User::getDescription);
  }

  @Named("resolveLastMessage")
  protected MessageResponseDto resolveLastMessage(Chat chat) {
    return chat.getMessages().stream()
        .max(Comparator.comparing(Message::getCreatedAt))
        .map(messageMapper::entityToDto)
        .orElse(null);
  }
}
