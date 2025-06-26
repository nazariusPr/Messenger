import axiosInstance from "./axiosInstance";
import type {
  TokenDto,
  AccessTokenDto,
  AccessRefreshTokenDto,
  PageDto,
  UserDto,
  ChatResponseDto as Chat,
  MessageResponseDto as Message,
} from "../types/api";

// Auth
export const refreshToken = async (token: string): Promise<string> => {
  const payload: TokenDto = { token };

  const response = await axiosInstance.post<AccessTokenDto>(
    "/auth/refresh-token",
    payload
  );
  return response.data.accessToken;
};

export const googleOAuth2 = async (
  token: string
): Promise<AccessRefreshTokenDto> => {
  const payload: TokenDto = { token };

  const response = await axiosInstance.post<AccessRefreshTokenDto>(
    "/auth/google",
    payload
  );
  return response.data;
};

// User
export const search = async (
  email: string,
  page: number,
  size: number
): Promise<PageDto<UserDto>> => {
  const response = await axiosInstance.get<PageDto<UserDto>>("/users/search", {
    params: { email, page, size },
  });
  return response.data;
};

// Chat
export const getChats = async (
  page: number,
  size: number
): Promise<PageDto<Chat>> => {
  const response = await axiosInstance.get<PageDto<Chat>>("/chats", {
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
  const response = await axiosInstance.get<PageDto<Message>>(url, {
    params: { page, size },
  });
  return response.data;
};
