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
  const selectedChatRef = useRef<Chat | null>(null);
  const subscriptionsRef = useRef<Record<string, any>>({});
  const { client } = useWS();

  useEffect(() => {
    const fetchChats = async () => {
      try {
        const data: PageDto<Chat> = await getChats(0, 100);
        setChats(data.elems);
        if (data.elems.length > 0 && !selectedChat) {
          const chat = data.elems[0];

          setSelectedChat(chat);
          selectedChatRef.current = chat;
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

      selectedChatRef.current = selectedChat;
    };

    fetchMessages();
  }, [selectedChat]);

  useEffect(() => {
    if (!client || !client.connected || chats.length === 0) return;

    Object.values(subscriptionsRef.current).forEach((sub) => sub.unsubscribe());
    subscriptionsRef.current = {};

    chats.forEach((chat) => {
      const sub = client.subscribe(`/topic/chat/${chat.id}`, (message) => {
        const newMessage: Message = JSON.parse(message.body);

        if (selectedChatRef.current?.id === chat.id) {
          setMessages((prev) => [newMessage, ...prev]);
        }

        setChats((prev) =>
          prev.map((ch) =>
            ch.id === chat.id ? { ...ch, lastMessage: newMessage } : ch
          )
        );
      });

      subscriptionsRef.current[chat.id] = sub;
    });

    return () => {
      Object.values(subscriptionsRef.current).forEach((sub) =>
        sub.unsubscribe()
      );
      subscriptionsRef.current = {};
    };
  }, [client, client?.connected]);

  const handleSendMessage = (content: string) => {
    if (!selectedChat || !client || !client.connected) return;

    client.publish({
      destination: `/app/chat/send/${selectedChat.id}`,
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
            selectedChat={selectedChat}
            onSelectChat={setSelectedChat}
          />
        </Sidebar>

        <Content>
          {selectedChat && (
            <MessagePanel
              chat={selectedChat}
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
  background-color: ${({ theme }) => theme.colors.background};
`;

const MainContent = styled.div`
  display: flex;
  flex: 1;
`;

const Sidebar = styled.div`
  flex: 1;
  max-width: 30%;
`;

const Content = styled.div`
  flex: 3;
`;

export default HomePage;
