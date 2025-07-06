import { useAuth } from "../../contexts/AuthContext";
import type { MessageResponseDto as Message } from "../../types/api";
import styled from "styled-components";

interface MessageItemProps {
  message: Message;
}

const MessageItem: React.FC<MessageItemProps> = ({ message }) => {
  const { email } = useAuth();
  const isOwn = message.email === email;

  const userTimeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
  const formattedTime = new Date(message.createdAt).toLocaleTimeString([], {
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
    timeZone: userTimeZone,
  });

  return (
    <MessageItemWrapper $isOwn={isOwn}>
      <Bubble $isOwn={isOwn}>
        <Header>
          <SenderEmail>{message.email}</SenderEmail>
          <Timestamp>{formattedTime}</Timestamp>
        </Header>
        <Content>{message.content}</Content>
      </Bubble>
    </MessageItemWrapper>
  );
};

const MessageItemWrapper = styled.div.withConfig({
  shouldForwardProp: (prop) => prop !== "$isOwn",
})<{ $isOwn: boolean }>`
  display: flex;
  justify-content: ${({ $isOwn }) => ($isOwn ? "flex-end" : "flex-start")};
  margin-bottom: ${({ theme }) => theme.spacing.sm};
`;

const Bubble = styled.div.withConfig({
  shouldForwardProp: (prop) => prop !== "$isOwn",
})<{ $isOwn: boolean }>`
  background-color: ${({ $isOwn, theme }) =>
    $isOwn ? theme.colors.primary : theme.colors.secondary};
  color: ${({ theme }) => theme.colors.background};
  padding: ${({ theme }) => theme.spacing.xs} ${({ theme }) => theme.spacing.sm};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  max-width: 70%;
  word-wrap: break-word;
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  font-size: 0.75rem;
`;

const SenderEmail = styled.span`
  font-weight: 600;
  margin-right: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`;

const Timestamp = styled.span`
  font-style: italic;
  font-size: 0.7rem;
  white-space: nowrap;
`;

const Content = styled.div`
  white-space: pre-wrap;
`;

export default MessageItem;
