import { beforeEach, describe, expect, it, vi } from 'vitest';

import {
  createInitData,
  deleteInitData,
  getAiDemo,
  getAiDocs,
  getAiHealth,
  getAiRedoc,
  getAiSchema,
  getCommentDigest,
  getCommentDuplicates,
  getDocumentRoadmap,
  getGraphRag,
  getInitData,
  getInitDataList,
  getLearningCoach,
  getLearningPattern,
  getNodeDescription,
  getNodeGeneration,
  getNodeResourceRecommendation,
  getRecordCoach,
  getRelatedRoadmaps,
  getRoadmapGenerated,
  getRoadmapRecommendation,
  getResourceRecommendation,
  getTechCards,
  getTechFingerprint,
  getWebSearch,
  postDocumentRoadmap,
  saveNodeResource,
  updateInitData,
} from './ai';

vi.mock('./client', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import { apiClient } from './client';

describe('ai API exports', () => {
  it('exports all 29 API functions', () => {
    expect(typeof getAiHealth).toBe('function');
    expect(typeof getAiSchema).toBe('function');
    expect(typeof getAiDocs).toBe('function');
    expect(typeof getAiRedoc).toBe('function');
    expect(typeof getAiDemo).toBe('function');
    expect(typeof getRecordCoach).toBe('function');
    expect(typeof getLearningCoach).toBe('function');
    expect(typeof getLearningPattern).toBe('function');
    expect(typeof getRelatedRoadmaps).toBe('function');
    expect(typeof getRoadmapGenerated).toBe('function');
    expect(typeof getRoadmapRecommendation).toBe('function');
    expect(typeof getDocumentRoadmap).toBe('function');
    expect(typeof postDocumentRoadmap).toBe('function');
    expect(typeof getTechCards).toBe('function');
    expect(typeof getTechFingerprint).toBe('function');
    expect(typeof getCommentDigest).toBe('function');
    expect(typeof getCommentDuplicates).toBe('function');
    expect(typeof getResourceRecommendation).toBe('function');
    expect(typeof getWebSearch).toBe('function');
    expect(typeof getGraphRag).toBe('function');
    expect(typeof getInitDataList).toBe('function');
    expect(typeof createInitData).toBe('function');
    expect(typeof getInitData).toBe('function');
    expect(typeof updateInitData).toBe('function');
    expect(typeof deleteInitData).toBe('function');
    expect(typeof getNodeGeneration).toBe('function');
    expect(typeof getNodeDescription).toBe('function');
    expect(typeof getNodeResourceRecommendation).toBe('function');
    expect(typeof saveNodeResource).toBe('function');
  });
});

describe('getAiHealth', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('calls GET /ai/health/', () => {
    getAiHealth();
    expect(apiClient.get).toHaveBeenCalledWith('/ai/health/');
  });
});

describe('getAiDemo', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('calls GET /ai/demo with no params', () => {
    getAiDemo();
    expect(apiClient.get).toHaveBeenCalledWith('/ai/demo');
  });

  it('calls GET /ai/demo with query string when params provided', () => {
    getAiDemo({ roadmap_id: 'r1', user_id: 'u1' });
    const call = vi.mocked(apiClient.get).mock.calls[0][0] as string;
    expect(call).toContain('/ai/demo?');
    expect(call).toContain('roadmap_id=r1');
    expect(call).toContain('user_id=u1');
  });
});

describe('getRecordCoach', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('calls GET /ai/record-coach with required roadmap_id', () => {
    getRecordCoach({ roadmap_id: 'rm-1' });
    const call = vi.mocked(apiClient.get).mock.calls[0][0] as string;
    expect(call).toContain('/ai/record-coach?');
    expect(call).toContain('roadmap_id=rm-1');
  });

  it('includes optional node_id when provided', () => {
    getRecordCoach({ roadmap_id: 'rm-1', node_id: 'nd-2' });
    const call = vi.mocked(apiClient.get).mock.calls[0][0] as string;
    expect(call).toContain('node_id=nd-2');
  });
});

describe('getTechCards', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('calls GET /ai/tech-cards with encoded tech_slug', () => {
    getTechCards('react');
    expect(apiClient.get).toHaveBeenCalledWith('/ai/tech-cards?tech_slug=react');
  });
});

describe('postDocumentRoadmap', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('calls POST /ai/document-roadmap with body', () => {
    postDocumentRoadmap({ document: 'some doc', goal: 'learn react' });
    expect(apiClient.post).toHaveBeenCalledWith('/ai/document-roadmap', {
      document: 'some doc',
      goal: 'learn react',
    });
  });
});

describe('createInitData', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('calls POST /ai/init-data with body', () => {
    createInitData({ content: 'test content', data_type: 'text' });
    expect(apiClient.post).toHaveBeenCalledWith('/ai/init-data', {
      content: 'test content',
      data_type: 'text',
    });
  });
});

describe('deleteInitData', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('calls DELETE /ai/init-data/{id}', () => {
    deleteInitData('id-123');
    expect(apiClient.delete).toHaveBeenCalledWith('/ai/init-data/id-123');
  });
});
