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
  padding: 2rem;
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #ece9e6, #ffffff);
`;

const Card = styled.div`
  width: 100%;
  max-width: 440px;
  padding: 3rem 2.5rem;
  background-color: ${({ theme }) => theme.colors.background || "#fff"};
  border-radius: ${({ theme }) => theme.borderRadius?.lg || "16px"};
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  text-align: center;
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
