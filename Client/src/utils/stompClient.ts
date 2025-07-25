import { Client, StompHeaders } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const createStompClient = (accessToken: string | null): Client => {
  const baseURL = import.meta.env.VITE_BASE_URL;
  const socket = new SockJS(baseURL + "/ws");
  const headers: StompHeaders = {};

  if (accessToken) {
    headers.Authorization = `Bearer ${accessToken}`;
  }

  const client = new Client({
    webSocketFactory: (): WebSocket => socket as WebSocket,
    reconnectDelay: 5000,
    debug: (str: string) => {
      console.log("[STOMP]", str);
    },
    onStompError: (frame) => {
      console.error("Broker error:", frame.headers["message"], frame.body);
    },
    connectHeaders: headers,
  });

  client.activate();
  return client;
};

export default createStompClient;
