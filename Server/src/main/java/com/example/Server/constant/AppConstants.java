package com.example.Server.constant;

public class AppConstants {
  private AppConstants() {}

  public static final String AUTH_LINK = "/api/v1/auth";
  public static final String USER_LINK = "/api/v1/users";
  public static final String USER_STATUS_LINK = "/api/v1/user-status";
  public static final String CHAT_LINK = "/api/v1/chats";
  public static final String MESSAGE_LINK = CHAT_LINK + "/{chatId}/messages";
}
