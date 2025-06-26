import { useEffect, useState, useRef } from "react";
import { getChats, getMessages } from "../api/api";
import { useWS } from "../contexts/WebSocketContext";
import type {
  ChatResponseDto as Chat,
  MessageResponseDto as Message,
  PageDto,
} from "../types/api";
import styled from "styled-components";
import UserSearch from "../components/user/UserSearch";
import ChatList from "../components/chat/ChatList";
import MessagePanel from "../components/message/MessagePanel";

const HomePage: React.FC = () => {
  const [chats, setChats] = useState<Chat[]>([]);
  const [selectedChat, setSelectedChat] = useState<Chat | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const subscriptionRef = useRef<any>(null);
  const { client } = useWS();

  useEffect(() => {
    const fetchChats = async () => {
      try {
        const data: PageDto<Chat> = await getChats(0, 100);
        setChats(data.elems);
        if (data.elems.length > 0 && !selectedChat) {
          setSelectedChat(data.elems[0]);
        }
      } catch (error) {
        console.error("Failed to fetch chats", error);
      }
    };

    fetchChats();
  }, []);

  useEffect(() => {
    const fetchMessages = async () => {
      if (!selectedChat) return;
      try {
        const data = await getMessages(selectedChat.id, 0, 100);
        setMessages(data.elems);
      } catch (error) {
        console.error("Failed to fetch messages", error);
      }
    };

    fetchMessages();
  }, [selectedChat]);

  useEffect(() => {
    if (subscriptionRef.current) {
      subscriptionRef.current.unsubscribe();
      subscriptionRef.current = null;
    }

    if (selectedChat && client && client.connected) {
      if (selectedChat.group) {
        subscriptionRef.current = client.subscribe(
          `/topic/chat/${selectedChat.id}`,
          (message) => {
            const newMessage: Message = JSON.parse(message.body);
            setMessages((prev) => [newMessage, ...prev]);
          }
        );
      } else {
        subscriptionRef.current = client.subscribe(
          `/user/queue/messages`,
          (message) => {
            const newMessage: Message = JSON.parse(message.body);
            setMessages((prev) => [newMessage, ...prev]);
          }
        );
      }
    }

    return () => {
      if (subscriptionRef.current) {
        subscriptionRef.current.unsubscribe();
        subscriptionRef.current = null;
      }
    };
  }, [selectedChat, client, client?.connected]);

  const handleSendMessage = (content: string) => {
    if (!selectedChat || !client || !client.connected) return;

    client.publish({
      destination: `/app/chat/sendMessage/${selectedChat.id}`,
      body: JSON.stringify({ content }),
    });
  };

  return (
    <Container>
      <SearchBarWrapper>
        <UserSearch />
      </SearchBarWrapper>
      <MainContent>
        <Sidebar>
          <ChatList
            chats={chats}
            selectedChatId={selectedChat}
            onSelectChat={setSelectedChat}
          />
        </Sidebar>

        <Content>
          {selectedChat && (
            <MessagePanel
              messages={messages}
              onSendMessage={handleSendMessage}
            />
          )}
        </Content>
      </MainContent>
    </Container>
  );
};

const Container = styled.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
`;

const SearchBarWrapper = styled.div`
  padding: ${({ theme }) => theme.spacing.xs};
  background-color: ${({ theme }) => theme.colors.background}; /
`;

const MainContent = styled.div`
  display: flex;
  flex: 1;
`;

const Sidebar = styled.div`
  flex: 1;
  max-width: 25%;
`;

const Content = styled.div`
  flex: 3;
`;

export default HomePage;
