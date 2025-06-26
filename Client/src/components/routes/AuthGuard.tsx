import Loader from "../UI/Loader";
import RoutesConstant from "../../constants/client/RoutesConstant";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

interface AuthGuardProps {
  children: React.ReactNode;
  requireAuth: boolean;
}

const AuthGuard = ({ children, requireAuth }: AuthGuardProps) => {
  const { getAccessToken, authReady } = useAuth();

  if (!authReady) {
    return <Loader />;
  }

  const accessToken = getAccessToken();

  if (requireAuth) {
    return accessToken ? children : <Navigate to={RoutesConstant.AUTH} />;
  } else {
    return !accessToken ? children : <Navigate to={RoutesConstant.HOME} />;
  }
};

export default AuthGuard;
