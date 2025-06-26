export interface TokenDto {
  token: string;
}

export interface AccessTokenDto {
  accessToken: string;
}

export interface AccessRefreshTokenDto {
  accessToken: string;
  refreshToken: string;
}

export interface UserDto {
  id: string;
  email: string;
  description: string;
}

export interface EmailDto {
  email: string;
}

export interface ExceptionDto {
  message: string;
}

export interface PageDto<T> {
  elems: T[];
  currentPage: number;
  totalElems: number;
  totalPages: number;
}

export interface MessageResponseDto {
  id: string;
  email: string;
  content: string;
  createdAt: string;
  editedAt: string | null;
}

export interface ChatResponseDto {
  id: string;
  name: string;
  description: string | null;
  lastMessage: MessageResponseDto | null;
  group: boolean;
}
