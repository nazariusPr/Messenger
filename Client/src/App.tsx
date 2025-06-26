import AppRoute from "./routes/AppRoute";
import AuthRoute from "./routes/AuthRoute";
import AuthProvider from "./contexts/AuthContext";
import { WebSocketProvider } from "./contexts/WebSocketContext";
import NotFoundPage from "./pages/NotFoundPage";
import RoutesConstant from "./constants/client/RoutesConstant";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ThemeProvider } from "styled-components";
import { theme } from "./theme";

function App() {
  return (
    <AuthProvider>
      <WebSocketProvider>
        <ThemeProvider theme={theme}>
          <Router>
            <Routes>
              <Route path={RoutesConstant.AUTH} element={<AuthRoute />} />
              <Route path={RoutesConstant.HOME} element={<AppRoute />} />
              <Route path="*" element={<NotFoundPage />} />
            </Routes>
          </Router>
        </ThemeProvider>
      </WebSocketProvider>
    </AuthProvider>
  );
}

export default App;
