import React, { useState } from "react";
import { Input, List, Alert, Button } from "antd";
import styled from "styled-components";

const { Search: AntdSearch } = Input;

interface SearchProps<T> {
  onSearch: (query: string) => Promise<T[]>;
  renderItem: (item: T) => React.ReactNode;
  placeholder?: string;
}

const Search = <T,>({
  onSearch,
  renderItem,
  placeholder = "Search...",
}: SearchProps<T>): React.ReactElement => {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSearch = async (value: string) => {
    if (!value.trim()) return;
    setLoading(true);
    setError("");
    try {
      const data = await onSearch(value);
      setResults(data);
    } catch {
      setError("Failed to fetch results.");
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setResults([]);
  };

  return (
    <SearchWrapper>
      <AntdSearch
        placeholder={placeholder}
        enterButton="Search"
        size="middle"
        loading={loading}
        onSearch={handleSearch}
        onChange={(e) => setQuery(e.target.value)}
        value={query}
      />

      {error && (
        <Alert
          message={error}
          type="error"
          showIcon
          style={{ marginTop: 16 }}
        />
      )}

      {results.length > 0 && (
        <ResultsContainer>
          <CloseButton
            type="text"
            onClick={handleClose}
            icon={<span style={{ fontSize: "16px" }}>×</span>}
          />
          <List
            bordered
            dataSource={results}
            renderItem={renderItem}
            style={{ paddingTop: 32 }}
          />
        </ResultsContainer>
      )}
    </SearchWrapper>
  );
};

const ResultsContainer = styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  z-index: ${({ theme }) => theme.zIndex.tooltip};
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  margin-top: 8px;
  border-radius: ${({ theme }) => theme.borderRadius.md};
  overflow: hidden;
`;

const SearchWrapper = styled.div`
  position: relative;
`;

const CloseButton = styled(Button)`
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 1;
`;

export default Search;
