import { useState, useEffect, useRef } from "react";
import type { MessageResponseDto as Message } from "../../types/api";
import MessageItem from "./MessageItem";
import styled from "styled-components";

interface MessagePanelProps {
  messages: Message[];
  onSendMessage: (text: string) => void;
}

const MessagePanel: React.FC<MessagePanelProps> = ({
  messages,
  onSendMessage,
}) => {
  const [messageText, setMessageText] = useState("");

  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollTop = messagesEndRef.current.scrollHeight;
    }
  }, [messages]);

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
  height: 95vh;
  min-height: 0;
  background-color: ${({ theme }) => theme.colors.background};
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
  border-top: 1px solid ${({ theme }) => theme.colors.secondary};
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
