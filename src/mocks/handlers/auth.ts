import { http, HttpResponse } from 'msw';

/** Auth API 핸들러 - US-003에서 구현 */
export const authHandlers = [
  // POST /api/auth/login
  http.post('/api/auth/login', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),

  // POST /api/auth/register
  http.post('/api/auth/register', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),

  // POST /api/auth/send-verification-code
  http.post('/api/auth/send-verification-code', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),

  // POST /api/auth/verify-code
  http.post('/api/auth/verify-code', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),

  // POST /api/auth/reset-password
  http.post('/api/auth/reset-password', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),
];
