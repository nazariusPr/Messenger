import {
  createContext,
  useContext,
  useState,
  useEffect,
  useLayoutEffect,
} from "react";
import { refreshToken, googleOAuth2 } from "../api/api";
import type { ReactNode } from "react";
import type { AxiosRequestConfig } from "axios";
import { jwtDecode } from "jwt-decode";
import axiosInstance from "../api/axiosInstance";

const REFRESH_TOKEN_KEY = "refresh_token";

interface AuthProviderProps {
  children: ReactNode;
}

interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  _retry?: boolean;
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

  const getRefreshToken = (): string | null => {
    try {
      return localStorage.getItem(REFRESH_TOKEN_KEY);
    } catch (err) {
      console.error("Error getting refresh token", err);
      return null;
    }
  };

  const setRefreshToken = (token: string | null): void => {
    try {
      if (token) {
        localStorage.setItem(REFRESH_TOKEN_KEY, token);
      } else {
        localStorage.removeItem(REFRESH_TOKEN_KEY);
      }
    } catch (err) {
      console.error("Error setting refresh token", err);
    }
  };

  const loginWithGoogle = async (googleToken: string): Promise<void> => {
    try {
      const { accessToken, refreshToken } = await googleOAuth2(googleToken);

      setAccessToken(accessToken);
      setRefreshToken(refreshToken);

      const payload = jwtDecode<JwtPayload>(accessToken);
      setEmail(payload.sub);
    } catch (err) {
      logout();
      throw err;
    }
  };

  const logout = (): void => {
    setAccessToken(null);
    setRefreshToken(null);
    setEmail("");
  };

  useEffect(() => {
    const initializeAuth = async () => {
      const storedRefreshToken = getRefreshToken();
      if (storedRefreshToken) {
        try {
          const token = await refreshToken(storedRefreshToken);
          setAccessToken(token);

          const payload = jwtDecode<JwtPayload>(token);
          setEmail(payload.sub);
        } catch (err) {
          logout();
        }
      } else {
        logout();
      }

      setAuthReady(true);
    };
    initializeAuth();
  }, []);

  useLayoutEffect(() => {
    const authInterceptor = axiosInstance.interceptors.request.use((config) => {
      const isRetry = (config as CustomAxiosRequestConfig)._retry;

      if (!isRetry && accessToken) {
        config.headers = config.headers || {};
        config.headers.Authorization = `Bearer ${accessToken}`;
      }

      return config;
    });

    return () => {
      axiosInstance.interceptors.request.eject(authInterceptor);
    };
  }, [accessToken]);

  useLayoutEffect(() => {
    const refreshInterceptor = axiosInstance.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config as CustomAxiosRequestConfig;

        if (
          error.response?.status === 403 &&
          error.response.data === "NOT VALID JWT" &&
          !originalRequest._retry
        ) {
          try {
            const storedRefreshToken = getRefreshToken();
            if (!storedRefreshToken) {
              logout();
              return Promise.reject(error);
            }

            const token = await refreshToken(storedRefreshToken);
            setAccessToken(token);

            originalRequest.headers = originalRequest.headers || {};
            originalRequest.headers.Authorization = `Bearer ${token}`;
            originalRequest._retry = true;

            return axiosInstance(originalRequest);
          } catch (err) {
            logout();
            return Promise.reject(err);
          }
        }

        return Promise.reject(error);
      }
    );

    return () => {
      axiosInstance.interceptors.response.eject(refreshInterceptor);
    };
  }, []);

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
