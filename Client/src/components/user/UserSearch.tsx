import { List, Typography } from "antd";
import { search } from "../../api/api";
import type { UserDto } from "../../types/api";
import Search from "../UI/Search";

const UserSearch: React.FC = () => {
  const fetchUsers = async (query: string): Promise<UserDto[]> => {
    const result = await search(query, 0, 5);
    return result.elems;
  };

  const renderUser = (user: UserDto) => (
    <List.Item key={user.id}>
      <Typography.Text strong>{user.email}</Typography.Text> —{" "}
      {user.description || "No description"}
    </List.Item>
  );

  return (
    <Search<UserDto>
      onSearch={fetchUsers}
      renderItem={renderUser}
      placeholder="Search users by email"
    />
  );
};

export default UserSearch;
