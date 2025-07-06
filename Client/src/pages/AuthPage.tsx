import RoutesConstant from "../constants/client/RoutesConstant";
import withLoading from "../hoc/withLoading";
import { GoogleLogin } from "@react-oauth/google";
import { message } from "antd";
import { useAuth } from "../contexts/AuthContext";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import type { WithLoadingProps } from "../types/hoc";

const AuthPage: React.FC<WithLoadingProps> = ({ setLoading }) => {
  const navigate = useNavigate();
  const { loginWithGoogle } = useAuth();

  const handleGoogleSuccess = async (response: { credential?: string }) => {
    if (!response.credential) {
      message.error("Google login failed: no credential");
      return;
    }

    try {
      setLoading(true);
      await loginWithGoogle(response.credential);
      message.success("Login successful!");
      navigate(RoutesConstant.HOME);
    } catch (error) {
      message.error("Google login failed");
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageWrapper>
      <Card>
        <Title>Welcome to Our App</Title>
        <Subtitle>Sign in to continue</Subtitle>
        <GoogleLogin
          onSuccess={handleGoogleSuccess}
          onError={() => message.error("Google login failed")}
        />
      </Card>
    </PageWrapper>
  );
};

const PageWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100dvw;
  max-width: 100%;
  height: 100dvh;
  padding: 2rem;
  overflow: hidden;
  background: linear-gradient(135deg, #ece9e6, #ffffff);
  box-sizing: border-box;
`;

const Card = styled.div`
  width: 65%;
  padding: 3rem 2.5rem;
  background-color: ${({ theme }) => theme.colors.background || "#fff"};
  border-radius: ${({ theme }) => theme.borderRadius?.lg || "1rem"};
  box-shadow: 0 0.625rem 2.5rem rgba(0, 0, 0, 0.08); /* ≈ 10px 40px */
  text-align: center;
  overflow: hidden; /* disables internal scrollbars */
`;

const Title = styled.h1`
  margin-bottom: 0.5rem;
  font-size: 2.25rem;
  color: ${({ theme }) => theme.colors.primary || "#1a1a1a"};
`;

const Subtitle = styled.p`
  margin-bottom: 2rem;
  font-size: 1.1rem;
  color: ${({ theme }) => theme.colors.secondary || "#666"};
`;

export default withLoading(AuthPage);
