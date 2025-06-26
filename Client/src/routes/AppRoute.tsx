import AuthGuard from "../components/routes/AuthGuard";
import HomePage from "../pages/HomePage";

const AppRoute: React.FC = () => (
  <AuthGuard requireAuth={true}>
    <HomePage />
  </AuthGuard>
);

export default AppRoute;
