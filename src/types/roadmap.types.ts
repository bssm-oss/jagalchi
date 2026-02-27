import type { Edge, Node } from '@xyflow/react';

// === Node Data Types ===

export type NodeColorVariant = 'white' | 'black' | 'blue' | 'purple' | 'red' | 'orange';
export type TextColorVariant = 'gray' | 'black' | 'blue' | 'purple' | 'red' | 'orange';
export type NodeState = 'default' | 'focus';

interface BaseNodeData {
  variant: NodeColorVariant;
  isLocked: boolean;
  [key: string]: unknown;
}

export interface JagalchiNodeData extends BaseNodeData {
  label: string;
  description: string;
  resources: string[]; // Phase 1에서는 간단히 URL 배열
}

export interface JagalchiSectionData extends BaseNodeData {
  title?: string;
}

export interface JagalchiTextData {
  variant: TextColorVariant;
  content?: string;
  fontSize: number;
  fontWeight: 'normal' | 'bold';
  isLocked: boolean;
  [key: string]: unknown;
}

// === React Flow Node Types ===

export type JagalchiNodeType = Node<JagalchiNodeData, 'jagalchi-node'>;
export type JagalchiSectionType = Node<JagalchiSectionData, 'jagalchi-section'>;
export type JagalchiTextType = Node<JagalchiTextData, 'jagalchi-text'>;

export type RoadmapNode = JagalchiNodeType | JagalchiSectionType | JagalchiTextType;

// === Roadmap Entity Types ===

export interface RoadmapAuthor {
  id: string;
  name: string;
}

export interface Roadmap {
  id: string;
  title: string;
  description?: string;
  nodes: RoadmapNode[];
  edges: Edge[];
  author?: RoadmapAuthor;
  isPublic: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateRoadmapInput {
  title?: string;
  description?: string;
  isPublic?: boolean;
}

export interface UpdateRoadmapInput {
  title?: string;
  description?: string;
  nodes?: RoadmapNode[];
  edges?: Edge[];
  isPublic?: boolean;
}
