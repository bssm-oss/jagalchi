import { authHandlers } from './auth';
import { profileHandlers } from './profile';
import { roadmapHandlers } from './roadmap';

export const handlers = [...authHandlers, ...roadmapHandlers, ...profileHandlers];
