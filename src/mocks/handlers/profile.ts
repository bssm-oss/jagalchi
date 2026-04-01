import { http, HttpResponse } from 'msw';

/** Profile API 핸들러 - 추후 구현 */
export const profileHandlers = [
  // GET /api/profile/:id
  http.get('/api/profile/:id', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),

  // PUT /api/profile/:id
  http.put('/api/profile/:id', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),
];
