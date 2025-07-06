import {
  createContext,
  useContext,
  useState,
  useEffect,
  useLayoutEffect,
} from "react";
import { refreshToken, googleOAuth2, logout as serverLogout } from "../api/api";
import type { ReactNode } from "react";
import type { AxiosRequestConfig } from "axios";
import { jwtDecode } from "jwt-decode";
import { apiClient } from "../api/axiosInstance";

interface AuthProviderProps {
  children: ReactNode;
}

interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  _retry: boolean;
}

interface AuthContextType {
  email: string;
  getAccessToken: () => string | null;
  setAccessToken: (token: string) => void;
  loginWithGoogle: (googleToken: string) => Promise<void>;
  logout: () => void;
  authReady: boolean;
}

interface JwtPayload {
  sub: string;
  iat: number;
  exp: number;
}

const AuthContext = createContext({} as AuthContextType);

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [authReady, setAuthReady] = useState(false);
  const [email, setEmail] = useState<string>("");
  const [accessToken, setAccessToken] = useState<string | null>(null);

  const getAccessToken = (): string | null => {
    return accessToken;
  };

  const loginWithGoogle = async (googleToken: string): Promise<void> => {
    try {
      const { accessToken } = await googleOAuth2(googleToken);

      setAccessToken(accessToken);

      const payload = jwtDecode<JwtPayload>(accessToken);
      setEmail(payload.sub);
    } catch (err) {
      await logout();
      throw err;
    }
  };

  const logout = async (): Promise<void> => {
    setAccessToken(null);
    setEmail("");
    await serverLogout();
  };

  useEffect(() => {
    const initializeAuth = async () => {
      if (!accessToken) {
        try {
          const token = await refreshToken();
          setAccessToken(token);

          const payload = jwtDecode<JwtPayload>(token);
          setEmail(payload.sub);
        } catch (err) {
          setAuthReady(true);

          await logout();
        }
      }
      setAuthReady(true);
    };

    initializeAuth();
  }, []);

  useLayoutEffect(() => {
    const requestInterceptor = apiClient.interceptors.request.use((config) => {
      const customConfig = config as CustomAxiosRequestConfig;

      if (typeof customConfig._retry === "undefined") {
        customConfig._retry = false;
      }

      if (!customConfig._retry && accessToken) {
        config.headers = config.headers || {};
        config.headers.Authorization = `Bearer ${accessToken}`;
      }

      return config;
    });

    const responseInterceptor = apiClient.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config as CustomAxiosRequestConfig;

        if (
          error.response?.status === 401 &&
          error.response.data?.message === "NOT VALID JWT" &&
          !originalRequest._retry
        ) {
          originalRequest._retry = true;

          try {
            const newAccessToken = await refreshToken();
            setAccessToken(newAccessToken);

            originalRequest.headers = originalRequest.headers ?? {};
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

            const payload = jwtDecode<JwtPayload>(newAccessToken);
            setEmail(payload.sub);

            return apiClient(originalRequest);
          } catch (err) {
            await logout();
            return Promise.reject(err);
          }
        }

        return Promise.reject(error);
      }
    );

    return () => {
      apiClient.interceptors.request.eject(requestInterceptor);
      apiClient.interceptors.response.eject(responseInterceptor);
    };
  }, [accessToken]);

  return (
    <AuthContext.Provider
      value={{
        email,
        getAccessToken,
        setAccessToken,
        loginWithGoogle,
        logout,
        authReady,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export default AuthProvider;
