import * as matchers from '@testing-library/jest-dom/matchers';
import { cleanup } from '@testing-library/react';
import { afterEach, expect } from 'vitest';

expect.extend(matchers);

class TestStorage implements Storage {
  private readonly store = new Map<string, string>();

  get length(): number {
    return this.store.size;
  }

  clear(): void {
    this.store.clear();
  }

  getItem(key: string): string | null {
    return this.store.get(key) ?? null;
  }

  key(index: number): string | null {
    return Array.from(this.store.keys())[index] ?? null;
  }

  removeItem(key: string): void {
    this.store.delete(key);
  }

  setItem(key: string, value: string): void {
    this.store.set(key, value);
  }
}

if (typeof window !== 'undefined') {
  // Node 25 exposes an experimental global localStorage without browser
  // Storage methods when no --localstorage-file path is configured.
  let windowLocalStorage: Storage | undefined;
  let windowSessionStorage: Storage | undefined;

  try {
    windowLocalStorage = window.localStorage;
    windowSessionStorage = window.sessionStorage;
  } catch {
    windowLocalStorage = undefined;
    windowSessionStorage = undefined;
  }

  const shouldPolyfillStorage =
    typeof windowLocalStorage?.clear !== 'function' ||
    typeof windowSessionStorage?.clear !== 'function';

  const storageCtor = shouldPolyfillStorage ? TestStorage : window.Storage;
  const localStorage = shouldPolyfillStorage ? new TestStorage() : windowLocalStorage;
  const sessionStorage = shouldPolyfillStorage ? new TestStorage() : windowSessionStorage;

  Object.defineProperty(globalThis, 'Storage', {
    value: storageCtor,
    configurable: true,
  });
  Object.defineProperty(globalThis, 'localStorage', {
    value: localStorage,
    configurable: true,
  });
  Object.defineProperty(globalThis, 'sessionStorage', {
    value: sessionStorage,
    configurable: true,
  });
  Object.defineProperty(window, 'Storage', {
    value: storageCtor,
    configurable: true,
  });
  Object.defineProperty(window, 'localStorage', {
    value: localStorage,
    configurable: true,
  });
  Object.defineProperty(window, 'sessionStorage', {
    value: sessionStorage,
    configurable: true,
  });
}

afterEach(() => {
  cleanup();
});
