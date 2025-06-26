import styled from "styled-components";
import { LoadingOutlined } from "@ant-design/icons";

const Loader: React.FC = () => {
  return (
    <SpinnerOverlay>
      <LoadingOutlined style={{ fontSize: "7rem" }} spin />
    </SpinnerOverlay>
  );
};

const SpinnerOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  color: ${({ theme }) => theme.colors.primary};
  background: ${({ theme }) => theme.colors.background}cc;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: ${({ theme }) => theme.zIndex.modalBackdrop};
`;

export default Loader;
