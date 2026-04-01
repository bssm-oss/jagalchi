import { http, HttpResponse } from 'msw';

/** Roadmap API 핸들러 - US-004에서 구현 */
export const roadmapHandlers = [
  // GET /api/roadmaps
  http.get('/api/roadmaps', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),

  // GET /api/roadmaps/:id
  http.get('/api/roadmaps/:id', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),

  // POST /api/roadmaps
  http.post('/api/roadmaps', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),

  // PUT /api/roadmaps/:id
  http.put('/api/roadmaps/:id', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),

  // DELETE /api/roadmaps/:id
  http.delete('/api/roadmaps/:id', () => {
    return HttpResponse.json({ message: 'not implemented' }, { status: 501 });
  }),
];
