import { Client } from '@stomp/stompjs';

import { getAccessToken } from '@/api/client';

import type { IFrame, IMessage, StompSubscription } from '@stomp/stompjs';

const WS_URL = process.env.NEXT_PUBLIC_WS_URL ?? 'ws://localhost:8080/ws';

let client: Client | null = null;
let isConnecting = false;

type MessageHandler = (message: IMessage) => void;
type ErrorHandler = (frame: IFrame) => void;

interface StompClientOptions {
  onConnect?: () => void;
  onDisconnect?: () => void;
  onError?: ErrorHandler;
}

/** STOMP 클라이언트 싱글톤 생성/재사용 */
export function getStompClient(options?: StompClientOptions): Client {
  if (client?.connected) return client;
  if (isConnecting && client) return client;

  isConnecting = true;

  client = new Client({
    brokerURL: WS_URL,
    connectHeaders: {},
    reconnectDelay: 3000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,

    onConnect: () => {
      isConnecting = false;
      options?.onConnect?.();
    },

    onDisconnect: () => {
      isConnecting = false;
      options?.onDisconnect?.();
    },

    onStompError: (frame) => {
      isConnecting = false;
      options?.onError?.(frame);
    },

    beforeConnect: () => {
      // 연결 직전에 최신 토큰 + userId 주입
      const token = getAccessToken();
      if (client) {
        client.connectHeaders = {
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        };
      }
    },
  });

  return client;
}

/** STOMP 연결 활성화 */
export function connectStomp(options?: StompClientOptions): void {
  const c = getStompClient(options);
  if (!c.active) {
    c.activate();
  }
}

/** STOMP 연결 해제 */
export async function disconnectStomp(): Promise<void> {
  if (client?.active) {
    await client.deactivate();
  }
  client = null;
  isConnecting = false;
}

/** 토픽 구독 */
export function subscribeStomp(
  destination: string,
  callback: MessageHandler,
): StompSubscription | null {
  if (!client?.connected) return null;
  return client.subscribe(destination, callback);
}

/** 메시지 전송 */
export function publishStomp(destination: string, body: Record<string, unknown>): void {
  if (!client?.connected) return;
  client.publish({
    destination,
    body: JSON.stringify(body),
  });
}

export type { MessageHandler, StompClientOptions };
