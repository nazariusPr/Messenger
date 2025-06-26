import AuthGuard from "../components/routes/AuthGuard";
import AuthPage from "../pages/AuthPage";
import { GoogleOAuthProvider } from "@react-oauth/google";

const AuthRoute: React.FC = () => (
  <AuthGuard requireAuth={false}>
    <GoogleOAuthProvider clientId={import.meta.env.VITE_GOOGLE_CLIENT_ID}>
      <AuthPage />
    </GoogleOAuthProvider>
  </AuthGuard>
);

export default AuthRoute;
