import { useState, useEffect, useRef } from "react";
import { isUserOnline } from "../../api/api";
import { useWS } from "../../contexts/WebSocketContext";
import type { StompSubscription, IMessage } from "@stomp/stompjs";
import type {
  MessageResponseDto as Message,
  ChatResponseDto as Chat,
  UserStatusDto as UserStatus,
} from "../../types/api";
import MessageItem from "./MessageItem";
import styled from "styled-components";

interface MessagePanelProps {
  chat: Chat;
  messages: Message[];
  onSendMessage: (text: string) => void;
}

const MessagePanel: React.FC<MessagePanelProps> = ({
  chat,
  messages,
  onSendMessage,
}) => {
  const [messageText, setMessageText] = useState("");
  const [userStatus, setUserStatus] = useState<boolean | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const { client } = useWS();
  const subscriptionRef = useRef<StompSubscription | null>(null);

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollTop = messagesEndRef.current.scrollHeight;
    }
  }, [messages]);

  useEffect(() => {
    const fetchUserStatus = async () => {
      try {
        const status = await isUserOnline(chat.name);
        setUserStatus(status.online);

        if (!client || !client.connected) return;
        subscriptionRef.current = null;

        const subscription = client.subscribe(
          `/topic/user-status/${chat.name}`,
          (message: IMessage) => {
            const parsed: UserStatus = JSON.parse(message.body);
            setUserStatus(parsed.online);
          }
        );

        subscriptionRef.current = subscription;
      } catch {
        console.error("Error with user ststus occurs");
      }
    };

    if (!chat.group) {
      fetchUserStatus();
    } else {
      setUserStatus(null);
    }

    return () => {
      subscriptionRef.current?.unsubscribe();
      subscriptionRef.current = null;
    };
  }, [chat]);

  const handleSend = () => {
    const trimmed = messageText.trim();
    if (trimmed) {
      onSendMessage(trimmed);
      setMessageText("");
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSend();
    }
  };

  return (
    <Container>
      <Header>
        <ChatNameRow>
          <ChatName>{chat.name}</ChatName>
          {!chat.group && userStatus !== null && (
            <>
              <StatusDot online={userStatus} />
              <StatusText>{userStatus ? "Online" : "Offline"}</StatusText>
            </>
          )}
        </ChatNameRow>
        <ChatDescription>{chat.description}</ChatDescription>
      </Header>
      <MessagesContainer ref={messagesEndRef}>
        {[...messages].reverse().map((msg) => (
          <MessageItem message={msg} key={msg.id} />
        ))}
      </MessagesContainer>

      <InputContainer>
        <MessageInput
          placeholder="Type a message..."
          value={messageText}
          onChange={(e) => setMessageText(e.target.value)}
          onKeyDown={handleKeyDown}
        />
        <SendButton onClick={handleSend}>Send</SendButton>
      </InputContainer>
    </Container>
  );
};

const Container = styled.div`
  display: flex;
  flex-direction: column;
  border-left: 1px solid ${({ theme }) => theme.colors.border};
  height: 95vh;
  min-height: 0;
  background-color: ${({ theme }) => theme.colors.background};
`;

const Header = styled.div`
  padding: 8px 16px;
  background-color: #ffffff;
  border-bottom: 1px solid ${({ theme }) => theme.colors.border};
  display: flex;
  flex-direction: column;
  gap: 2px;
`;

const ChatNameRow = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
`;

const ChatName = styled.h2`
  margin: 0;
  font-size: 1rem; /* slightly smaller */
  color: ${({ theme }) => theme.colors.text};
`;

const StatusDot = styled.span<{ online: boolean }>`
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: ${({ online, theme }) =>
    online ? theme.colors.success : theme.colors.muted};
`;

const StatusText = styled.span`
  font-size: 0.75rem;
  color: ${({ theme }) => theme.colors.muted};
`;

const ChatDescription = styled.p`
  margin: 0;
  font-size: 0.75rem;
  color: ${({ theme }) => theme.colors.muted};
`;

const MessagesContainer = styled.div`
  flex: 1 1 auto;
  overflow-y: auto;
  padding: ${({ theme }) => theme.spacing.md};
  min-height: 0;
`;

const InputContainer = styled.div`
  display: flex;
  padding: ${({ theme }) => theme.spacing.sm};
  border-top: 1px solid ${({ theme }) => theme.colors.border};
  background-color: ${({ theme }) => theme.colors.background};
`;

const MessageInput = styled.input`
  flex-grow: 1;
  padding: ${({ theme }) => theme.spacing.sm};
  font-size: ${({ theme }) => theme.fontSizes.medium};
  border: 1px solid ${({ theme }) => theme.colors.secondary};
  border-radius: ${({ theme }) => theme.borderRadius.sm};
  margin-right: ${({ theme }) => theme.spacing.sm};
  outline: none;
`;

const SendButton = styled.button`
  background-color: ${({ theme }) => theme.colors.primary};
  color: ${({ theme }) => theme.colors.background};
  padding: ${({ theme }) => theme.spacing.sm} ${({ theme }) => theme.spacing.md};
  font-size: ${({ theme }) => theme.fontSizes.medium};
  border: none;
  border-radius: ${({ theme }) => theme.borderRadius.sm};
  cursor: pointer;

  &:hover {
    background-color: ${({ theme }) => theme.colors.secondary};
  }
`;

export default MessagePanel;
