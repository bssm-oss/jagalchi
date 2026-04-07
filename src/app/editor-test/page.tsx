'use client';

import { notFound } from 'next/navigation';

import { RoadmapEditor } from '@/features/roadmap-editor';

export default function EditorTestPage() {
  if (process.env.NODE_ENV === 'production') {
    notFound();
  }

  return <RoadmapEditor />;
}
