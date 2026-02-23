import { RoadmapViewer } from '@/features/roadmap-viewer';

interface ViewerPageProps {
  params: Promise<{ id: string }>;
}

export default async function ViewerPage({ params }: ViewerPageProps) {
  const { id } = await params;

  return <RoadmapViewer roadmapId={id} />;
}
