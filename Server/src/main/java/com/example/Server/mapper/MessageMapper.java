package com.example.Server.mapper;

import com.example.Server.dto.message.MessageRequestDto;
import com.example.Server.dto.message.MessageResponseDto;
import com.example.Server.model.Chat;
import com.example.Server.model.Message;
import com.example.Server.model.User;
import com.example.Server.utils.SecurityUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MessageMapper {
  protected SecurityUtils securityUtils;

  @Autowired
  public void setSecurityUtils(SecurityUtils securityUtils) {
    this.securityUtils = securityUtils;
  }

  @Mapping(source = "sender.email", target = "email")
  @Mapping(source = "content", target = "content", qualifiedByName = "decryptContent")
  public abstract MessageResponseDto entityToDto(Message message);

  @Mapping(source = "content", target = "content", qualifiedByName = "encryptContent")
  public abstract Message dtoToEntity(
      MessageRequestDto dto, @Context User sender, @Context Chat chat);

  @Named("encryptContent")
  protected String encryptContent(String content) {
    return content != null ? securityUtils.encrypt(content) : null;
  }

  @Named("decryptContent")
  protected String decryptContent(String content) {
    return content != null ? securityUtils.decrypt(content) : null;
  }

  @BeforeMapping
  protected void setSenderAndChat(
      @MappingTarget Message message, @Context User sender, @Context Chat chat) {
    message.setSender(sender);
    message.setChat(chat);
  }
}
