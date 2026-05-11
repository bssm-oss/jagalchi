import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

import { getAccessToken } from '@/api/client';

import type { IFrame, IMessage, StompSubscription } from '@stomp/stompjs';

const WS_URL =
  process.env.NEXT_PUBLIC_WS_URL ??
  (process.env.NODE_ENV === 'production' ? undefined : 'http://localhost:8082/ws/roadmap');

export function createStompSocketUrl(baseUrl: string, token: string | null): string {
  if (!token) return baseUrl;
  return appendQueryParams(baseUrl, { access_token: token });
}

function appendQueryParams(baseUrl: string, params: Record<string, string | undefined>): string {
  const entries = Object.entries(params).filter((entry): entry is [string, string] =>
    Boolean(entry[1]),
  );
  if (entries.length === 0) return baseUrl;

  const separator = baseUrl.includes('?') ? '&' : '?';
  const query = entries
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join('&');
  return `${baseUrl}${separator}${query}`;
}

if (!WS_URL && typeof window !== 'undefined') {
  // eslint-disable-next-line no-console
  console.warn('[stomp] NEXT_PUBLIC_WS_URL is not set. Real-time features will be disabled.');
}

let client: Client | null = null;
let isConnecting = false;
let consumerCount = 0;
let disconnectingPromise: Promise<void> | null = null;

type MessageHandler = (message: IMessage) => void;
type ErrorHandler = (frame: IFrame) => void;

interface StompClientOptions {
  onConnect?: () => void;
  onDisconnect?: () => void;
  onError?: ErrorHandler;
  /** CONNECT 시 전달할 사용자 정보 */
  roadmapId?: string;
}

/** STOMP 클라이언트 싱글톤 생성/재사용 */
export function getStompClient(options?: StompClientOptions): Client {
  if (client?.connected) return client;
  if (isConnecting && client) return client;

  isConnecting = true;

  client = new Client({
    webSocketFactory: () => {
      if (!WS_URL) {
        throw new Error('[stomp] WS_URL is not configured.');
      }

      const token = getAccessToken();
      const url = appendQueryParams(createStompSocketUrl(WS_URL, token), {
        roadmapId: options?.roadmapId,
      });

      return new SockJS(url);
    },
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
      const token = getAccessToken();
      if (client) {
        client.connectHeaders = {
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
          ...(options?.roadmapId ? { 'X-Roadmap-ID': options.roadmapId } : {}),
        };
      }
    },
  });

  return client;
}

/** STOMP 연결 활성화 — 진행 중인 disconnect가 있으면 완료 후 연결 */
export async function connectStomp(options?: StompClientOptions): Promise<void> {
  if (disconnectingPromise) {
    await disconnectingPromise;
  }
  const c = getStompClient(options);
  consumerCount++;

  try {
    if (!c.active) {
      c.activate();
    }
  } catch (error) {
    consumerCount = Math.max(0, consumerCount - 1);
    throw error;
  }
}

/** STOMP 연결 해제 — 마지막 소비자가 해제할 때만 실제 disconnect */
export function disconnectStomp(): Promise<void> {
  consumerCount = Math.max(0, consumerCount - 1);
  if (consumerCount > 0) return Promise.resolve();

  disconnectingPromise = (async () => {
    if (client?.active) {
      await client.deactivate();
    }
    client = null;
    isConnecting = false;
    disconnectingPromise = null;
  })();

  return disconnectingPromise;
}

/** 토픽 구독 */
export function subscribeStomp(
  destination: string,
  callback: MessageHandler,
): StompSubscription | null {
  if (!client?.connected) return null;
  return client.subscribe(destination, callback);
}

/** 메시지 전송 (헤더 지원) */
export function publishStomp(
  destination: string,
  body: Record<string, unknown>,
  headers?: Record<string, string>,
): void {
  if (!client?.connected) return;
  client.publish({
    destination,
    headers,
    body: JSON.stringify(body),
  });
}

export type { MessageHandler, StompClientOptions };
