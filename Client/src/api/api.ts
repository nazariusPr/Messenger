import { apiClient, authClient } from "./axiosInstance";
import type {
  TokenDto,
  AccessTokenDto,
  PageDto,
  UserDto,
  ChatResponseDto as Chat,
  MessageResponseDto as Message,
} from "../types/api";

// Auth
export const refreshToken = async (): Promise<string> => {
  const response = await authClient.post<AccessTokenDto>(
    "/auth/refresh-token",
    {}
  );
  return response.data.accessToken;
};

export const googleOAuth2 = async (token: string): Promise<AccessTokenDto> => {
  const payload: TokenDto = { token };

  const response = await authClient.post<AccessTokenDto>(
    "/auth/google",
    payload
  );
  return response.data;
};

export const logout = async (): Promise<void> => {
  await authClient.post("/auth/logout", {});
};

// User
export const search = async (
  email: string,
  page: number,
  size: number
): Promise<PageDto<UserDto>> => {
  const response = await apiClient.get<PageDto<UserDto>>("/users/search", {
    params: { email, page, size },
  });
  return response.data;
};

// Chat
export const getChats = async (
  page: number,
  size: number
): Promise<PageDto<Chat>> => {
  const response = await apiClient.get<PageDto<Chat>>("/chats", {
    params: { page, size },
  });
  return response.data;
};

// Message
export const getMessages = async (
  chatId: string,
  page: number,
  size: number
): Promise<PageDto<Message>> => {
  const url = `/chats/${chatId}/messages`;
  const response = await apiClient.get<PageDto<Message>>(url, {
    params: { page, size },
  });
  return response.data;
};
