import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode } from "react";
import { Client } from "@stomp/stompjs";
import { useAuth } from "./AuthContext";
import createStompClient from "../utils/stompClient";

interface WSProviderProps {
  children: ReactNode;
}

interface WSContextType {
  client: Client | null;
}

const WSContext = createContext<WSContextType>({ client: null });

export function useWS() {
  return useContext(WSContext);
}

export const WebSocketProvider = ({ children }: WSProviderProps) => {
  const { getAccessToken } = useAuth();
  const accessToken = getAccessToken();

  const [client, setClient] = useState<Client | null>(null);

  useEffect(() => {
    if (!accessToken) return;

    const client = createStompClient(accessToken);
    setClient(client);

    return () => {
      client.deactivate();
      setClient(null);
    };
  }, [accessToken]);

  return <WSContext.Provider value={{ client }}>{children}</WSContext.Provider>;
};
