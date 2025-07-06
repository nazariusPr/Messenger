package com.example.Server.utils;

import static com.example.Server.utils.SecurityUtils.getCurrentUserEmail;

import com.example.Server.dto.general.PageDto;
import com.example.Server.model.Chat;
import com.example.Server.model.Participant;
import com.example.Server.model.User;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class GeneralUtils {
  public static <T, R> PageDto<R> buildPageDto(Page<T> page, Function<T, R> toDto) {
    return new PageDto<>(
        page.getContent().stream().map(toDto).collect(Collectors.toList()),
        page.getNumber(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  public static String resolvePrivateChatField(Chat chat, Function<User, String> extractor) {
    String currentUserEmail = getCurrentUserEmail();
    return resolvePrivateChatField(chat, currentUserEmail, extractor);
  }

  public static String resolvePrivateChatField(
      Chat chat, String senderEmail, Function<User, String> extractor) {
    return chat.getParticipants().stream()
        .map(Participant::getUser)
        .filter(user -> !user.getEmail().equals(senderEmail))
        .map(extractor)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("");
  }
}
