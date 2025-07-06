import styled from "styled-components";
import type { ChatResponseDto as Chat } from "../../types/api";
interface ChatListProps {
  chats: Chat[];
  selectedChat: Chat | null;
  onSelectChat: (id: Chat) => void;
}

const ChatList: React.FC<ChatListProps> = ({
  chats,
  selectedChat,
  onSelectChat,
}) => {
  return (
    <Container>
      <List>
        {chats.map((chat) => (
          <ListItem
            key={chat.id}
            selected={chat.id === selectedChat?.id}
            onClick={() => onSelectChat(chat)}
          >
            <ChatName>{chat.name}</ChatName>
            {chat.lastMessage && (
              <LastMessage>
                {chat.group && <Sender>{chat.lastMessage.email}:</Sender>}
                {chat.lastMessage.content.length > 50
                  ? chat.lastMessage.content.slice(0, 50) + "..."
                  : chat.lastMessage.content}
              </LastMessage>
            )}
          </ListItem>
        ))}
      </List>
    </Container>
  );
};

const Container = styled.div`
  width: 100%;
  max-width: 100%;
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  scrollbar-width: thin;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background-color: ${({ theme }) => theme.colors.secondary};
    border-radius: 3px;
  }
`;

const List = styled.ul`
  list-style: none;
  padding: 0;
  margin: 0;
`;

const ListItem = styled.li<{ selected: boolean }>`
  padding: ${({ theme }) => theme.spacing.md};
  margin: ${({ theme }) => theme.spacing.xs} ${({ theme }) => theme.spacing.sm};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  cursor: pointer;
  background-color: ${({ selected, theme }) =>
    selected ? theme.colors.primary : "transparent"};
  color: ${({ selected, theme }) =>
    selected ? theme.colors.background : theme.colors.text};
  box-shadow: ${({ selected }) =>
    selected ? "0 2px 6px rgba(0,0,0,0.1)" : "none"};
  transition: background-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    background-color: ${({ theme }) => theme.colors.secondary};
    color: ${({ theme }) => theme.colors.background};
  }
`;

const ChatName = styled.span`
  font-size: ${({ theme }) => theme.fontSizes.medium};
  font-weight: 700;
`;

const LastMessage = styled.span`
  font-size: ${({ theme }) => theme.fontSizes.small};
  margin-top: 4px;
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`;

const Sender = styled.span`
  font-weight: 600;
  margin-right: 6px;
`;

export default ChatList;
