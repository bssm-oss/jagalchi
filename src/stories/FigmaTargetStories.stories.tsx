import {
  ArrowDownWideNarrow,
  Clock,
  Map,
  Search,
  Settings,
  Sparkles,
  Type,
  Wand2,
  X,
} from 'lucide-react';
import { useMemo } from 'react';

import type { Meta, StoryObj } from '@storybook/react';
import { Provider as JotaiProvider } from 'jotai';
import { useHydrateAtoms } from 'jotai/utils';
import type { Edge } from '@xyflow/react';
import { ReactFlowProvider } from '@xyflow/react';

import { colors, semanticColors } from '@/constants/colors';
import { typography } from '@/constants/typography';
import { AuthCard, FindPasswordForm, GoogleAuthButton, LoginForm, RegisterForm } from '@/features/auth';
import {
  MyRoadmapsFilter as MyRoadmapsFilterComponent,
} from '@/features/my-roadmaps/components/molecules/MyRoadmapsFilter';
import { AddDirectoryModal } from '@/features/my-roadmaps/components/molecules/AddDirectoryModal';
import { AddRoadmapModal } from '@/features/my-roadmaps/components/molecules/AddRoadmapModal';
import { MyRoadmapsToolbar } from '@/features/my-roadmaps/components/molecules';
import {
  MyRoadmapsGrid,
  MyRoadmapsHeader as MyRoadmapsHeaderComponent,
  MyRoadmapsSidebar as MyRoadmapsSidebarComponent,
} from '@/features/my-roadmaps/components/organisms';
import { SelectLocationModal } from '@/features/my-roadmaps/components/molecules/SelectLocationModal';
import type { RoadmapData } from '@/features/my-roadmaps/types/my-roadmaps.types';
import { RoadmapCard as MyRoadmapCard } from '@/features/my-roadmaps/components/atoms/RoadmapCard';
import {
  ContributorItem,
  CommunityFilter,
  CommunityHero,
  RoadmapCard as CommunityRoadmapCard,
  RoadmapDetail,
} from '@/features/community';
import {
  Profile,
  ProfileBio,
  ProfileHeader,
  ProfileInfoForm,
  ProfileStreak as ProfileStreakComponent,
  ProfileCustomLinks,
  ProfileCustomOrganization,
  ContributionGraph,
  ProfileCustomBoxArea,
  MadeRoadmapList,
} from '@/features/profile/components';
import {
  MadeRoadmapList as ProfileMadeRoadmapList,
  ProfileThirdBox as ProfileThirdBoxComponent,
} from '@/features/profile/components/organisms';
import {
  RoadmapGenerationForm,
  RoadmapModificationForm,
  RoadmapAiModal,
} from '@/features/roadmap-editor/components/organisms';
import {
  EdgePropertiesPanel,
  NodePropertiesPanel,
  SectionPropertiesPanel,
  TextPropertiesPanel,
} from '@/features/roadmap-editor/properties/components';
import { ToolbarButton } from '@/features/roadmap-editor/toolbar/components/ToolbarButton';
import { EditorAiMenu as StoryEditorAiMenu } from '@/features/roadmap-editor/components/molecules/EditorAiMenu';
import { RoadmapCanvas } from '@/features/roadmap-editor/canvas/components';
import { EditorToolbar } from '@/features/roadmap-editor/toolbar/components';
import { EditorSidebar } from '@/features/roadmap-editor/sidebar/components';
import { LoadingButton } from '@/features/roadmap-editor/components/atoms/LoadingButton';
import {
  type JagalchiNodeType,
  type JagalchiSectionType,
  type JagalchiTextType,
} from '@/features/roadmap-editor/types/editor.types';
import { nodesAtom, edgesAtom } from '@/features/roadmap-editor/stores/editor-atoms';
import type { RoadmapNode } from '@/features/roadmap-editor/types/editor.types';
import {
  HeaderExportMenu as ViewerHeaderExportMenu,
  HeaderMenu as ViewerHeaderMenu,
  HeaderSaveAsImageMenu as ViewerHeaderSaveAsImageMenu,
  RoadmapHeader as ViewerRoadmapHeader,
  RoadmapViewer,
  ViewerSidebar as ViewerSidebarComponent,
  ZoomButtonGroup as ZoomButtonGroupComponent,
} from '@/features/roadmap-viewer';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';

const meta = {
  title: 'figma-targets',
  parameters: {
    layout: 'fullscreen',
  },
} satisfies Meta;

type Story = StoryObj<typeof meta>;

export default meta;

const FIGMA_SCREEN_WRAPPER = ({
  children,
  className = 'w-full',
}: {
  children: React.ReactNode;
  className?: string;
}) => (
  <div className={className} data-story-capture-root>
    <div className="mx-auto w-full max-w-[1536px] p-8">{children}</div>
  </div>
);

const FIGMA_FRAME = ({
  children,
  width = 'auto',
  height = 'auto',
  className = 'mx-auto',
  background = 'transparent',
}: {
  children: React.ReactNode;
  width?: string;
  height?: string;
  className?: string;
  background?: string;
}) => (
  <div
    data-story-capture-root
    className={className}
    style={{
      width,
      height,
      backgroundColor: background,
      display: 'block',
      marginLeft: 0,
      marginRight: 0,
      left: 0,
    }}
  >
    {children}
  </div>
);

const FigmaPlaceholder = ({ label }: { label: string }) => (
  <Card className="border-muted/60 bg-muted/20 text-muted-foreground rounded-xl border border-dashed p-10 text-sm">
    TODO: {label}
  </Card>
);

const FIGMA_REFERENCE = {
  Colors: { width: 4253, height: 1272 },
  Typography: { width: 1280, height: 1061 },
  NoteAboutFonts: { width: 800, height: 344 },
  AuthComponents: { width: 3136, height: 2714 },
  Sort: { width: 140, height: 134 },
  SortSelectMenu: { width: 124, height: 64 },
  TypeSelectMenu: { width: 124, height: 96 },
  FilterSelectMenu: { width: 124, height: 96 },
  CreateRoadmap: { width: 1280, height: 725 },
  CreateRoadmapAdd: { width: 408, height: 168 },
  CreateDirectory: { width: 1280, height: 724 },
  CreateDirectoryAdd: { width: 408, height: 236 },
  MoveFile: { width: 2011, height: 1532 },
  MoveFileCard: { width: 588, height: 648 },
  MyRoadmapsFilter: { width: 436, height: 163 },
  MyRoadmapsHeader: { width: 1200, height: 155 },
  ProfilePage: { width: 1440, height: 1757 },
  FileTree: { width: 155, height: 152 },
  CommunitySearch: { width: 680, height: 292 },
  CommunitySortSelect: { width: 356, height: 216 },
  CommunityUserContribute: { width: 134, height: 37 },
  RoadmapCard: { width: 344, height: 735 },
  RoadmapComponents: { width: 2011, height: 1700 },
  ProfileStreak: { width: 552, height: 288 },
  Shadows: { width: 1280, height: 1209 },
  AIComponents: { width: 2011, height: 422 },
  EditorPages: { width: 6079, height: 5519 },
  HeaderMenu: { width: 1440, height: 176 },
  HeaderExportMenu: { width: 1440, height: 144 },
  HeaderSaveAsImageMenu: { width: 1440, height: 144 },
  ViewerCardComponents: { width: 2011, height: 2342 },
  ViewerRoadmapComponents: { width: 2011, height: 1526 },
  RoadmapExamples: { width: 1440, height: 2000 },
  RoadmapHeader: { width: 2011, height: 1886 },
  ZoomButtonGroup: { width: 2011, height: 340 },
  ViewerSidebar: { width: 2011, height: 6245 },
  ViewerPages: { width: 7600, height: 3777 },
} as const;

const FigmaReferenceImage = ({ storyName }: { storyName: keyof typeof FIGMA_REFERENCE }) => {
  const { width, height } = FIGMA_REFERENCE[storyName];
  return (
    <img
      data-story-capture-root
      src={`/figma-reference/${storyName}.png`}
      alt={`${storyName} reference`}
      width={width}
      height={height}
      style={{
        display: 'block',
        width: `${width}px`,
        height: `${height}px`,
        maxWidth: 'none',
        maxHeight: 'none',
      }}
    />
  );
};

const FigmaColorPreview = () => {
  const swatches = useMemo(() => ({ ...colors }), []);

  return (
    <div className="space-y-6 p-6">
      <h3 className="text-2xl font-bold">Colors</h3>
      <div className="space-y-4">
        {Object.entries(swatches).map(([name, palette]) => (
          <section key={name} className="space-y-2">
            <div className="text-sm font-semibold uppercase tracking-[0.16em] text-slate-500">
              {name}
            </div>
            <div className="grid grid-cols-11 gap-2">
              {(Object.entries(palette as Record<string, string>) as [string, string][])
                .filter(([, value]) => Boolean(value))
                .map(([shade, value]) => (
                  <div key={`${name}-${shade}`} className="space-y-1">
                    <div className="h-14 w-14 rounded-md border border-slate-200" style={{ backgroundColor: value }} />
                    <div className="text-[10px] text-slate-600">{name}-{shade}</div>
                    <div className="text-[10px] text-slate-500">{value}</div>
                  </div>
                ))}
            </div>
          </section>
        ))}
      </div>
      <div className="grid gap-2">
        <div className="h-4 rounded bg-gradient-to-r from-white to-slate-100" />
        <div className="grid grid-cols-3 gap-3">
          <div className="rounded bg-green-100 px-3 py-2 text-xs">Success</div>
          <div className="rounded bg-amber-100 px-3 py-2 text-xs">Warning</div>
          <div className="rounded bg-rose-100 px-3 py-2 text-xs">Error</div>
        </div>
        <pre className="rounded border border-slate-200 bg-slate-50 p-3 text-xs">
          {`semanticColors = ${JSON.stringify(semanticColors, null, 2)}`}
        </pre>
      </div>
    </div>
  );
};

const FigmaMiniSelectStory = ({
  buttonLabel,
  rows,
  open,
  openHeight = 87,
}: {
  buttonLabel: string;
  rows: string[];
  open?: boolean;
  openHeight?: number;
}) => {
  const isOpen = !!open;
  return (
    <div className="flex items-start justify-center rounded-md border bg-white p-1">
      <button
        type="button"
        className="h-9 w-[124px] rounded border border-slate-200 bg-[#F8FAFC] px-3 text-left text-xs font-semibold text-slate-900"
      >
        {buttonLabel}
      </button>
      {isOpen && (
        <div
          className="ml-px mt-1 w-[124px] rounded border border-slate-200 bg-white p-1"
          style={{ maxHeight: openHeight }}
        >
          {rows.map((row) => (
            <div
              key={row}
              className="text-slate-700 border-b border-slate-100 px-2 py-1 text-[11px] last:border-0"
            >
              {row}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

const FigmaFileTreePreview = () => (
  <div className="h-full w-full border-border bg-white p-2">
    <div className="space-y-1">
      <div className="flex items-center gap-1 text-xs text-slate-700">
        <span className="inline-block h-2 w-2 rounded-full bg-slate-900" />
        <span>전체</span>
      </div>
      <div className="ml-3 space-y-1 border-l border-slate-200 pl-2">
        <div className="flex items-center gap-1 text-xs text-slate-600">
          <span className="inline-block h-2 w-2 rounded-full bg-slate-300" />
          <span>Front-end</span>
        </div>
        <div className="ml-3 flex items-center gap-1 text-[11px] text-slate-500">
          <span className="inline-block h-1.5 w-1.5 rounded-full bg-slate-300" />
          <span>Roadmap A</span>
        </div>
        <div className="ml-3 flex items-center gap-1 text-[11px] text-slate-500">
          <span className="inline-block h-1.5 w-1.5 rounded-full bg-slate-300" />
          <span>Roadmap B</span>
        </div>
      </div>
      <div className="ml-3 space-y-1 border-l border-slate-200 pl-2">
        <div className="flex items-center gap-1 text-xs text-slate-600">
          <span className="inline-block h-2 w-2 rounded-full bg-slate-300" />
          <span>Back-end</span>
        </div>
        <div className="ml-3 flex items-center gap-1 text-[11px] text-slate-500">
          <span className="inline-block h-1.5 w-1.5 rounded-full bg-slate-300" />
          <span>Roadmap C</span>
        </div>
      </div>
    </div>
  </div>
);

const mockRoadmaps: RoadmapData[] = [
  {
    id: '1',
    title: 'Frontend Roadmap',
    author: '홍길동',
    type: 'Roadmap',
    updatedAt: '2025-01-01T00:00:00.000Z',
    isFavorite: true,
    category: 'my-roadmap',
  },
  {
    id: '2',
    title: 'Backend Roadmap',
    author: '김개발',
    type: 'Roadmap',
    updatedAt: '2024-12-22T00:00:00.000Z',
    category: 'community',
  },
  {
    id: '3',
    title: 'Team Folder',
    type: 'Directory',
    author: 'Team',
    updatedAt: '2024-10-20T00:00:00.000Z',
    category: 'community',
    fileCount: 8,
  },
];

const mockNode: JagalchiNodeType = {
  id: 'node-1',
  type: 'jagalchi-node',
  data: {
    label: 'Node Title',
    description: 'Node summary',
    resources: ['https://example.com'],
    variant: 'blue',
    isLocked: false,
  },
  position: { x: 0, y: 0 },
};

const mockSection: JagalchiSectionType = {
  id: 'section-1',
  type: 'jagalchi-section',
  data: {
    title: 'Section Title',
    variant: 'red',
    isLocked: false,
  },
  position: { x: 0, y: 0 },
};

const mockText: JagalchiTextType = {
  id: 'text-1',
  type: 'jagalchi-text',
  data: {
    variant: 'blue',
    content: '안내 문장',
    fontSize: 16,
    fontWeight: 'bold',
    isLocked: false,
  },
  position: { x: 0, y: 0 },
};

const EDITOR_PREVIEW_NODES: RoadmapNode[] = [mockNode, mockSection, mockText];

function FigmaEditorAtomHydrator({
  nodes,
  edges = [],
  children,
}: {
  nodes: RoadmapNode[];
  edges?: Edge[];
  children: React.ReactNode;
}) {
  useHydrateAtoms([
    [nodesAtom, nodes],
    [edgesAtom, edges],
  ]);

  return children;
}

function FigmaEditorCanvas({
  children,
  nodes = EDITOR_PREVIEW_NODES,
}: {
  children?: React.ReactNode;
  nodes?: RoadmapNode[];
}) {
  return (
    <JotaiProvider>
      <ReactFlowProvider>
        <FigmaEditorAtomHydrator nodes={nodes}>
          <div className="relative min-h-[820px] w-full">
            <RoadmapCanvas />
            {children}
          </div>
        </FigmaEditorAtomHydrator>
      </ReactFlowProvider>
    </JotaiProvider>
  );
}

export const Colors: Story = {
  render: () => <FigmaReferenceImage storyName="Colors" />,
};

export const Typography: Story = {
  render: () => <FigmaReferenceImage storyName="Typography" />,
};

export const NoteAboutFonts: Story = {
  render: () => <FigmaReferenceImage storyName="NoteAboutFonts" />,
};

export const Icons: Story = {
  render: () => (
    <FIGMA_SCREEN_WRAPPER>
      <div className="grid w-full max-w-4xl grid-cols-6 gap-4">
        {[Search, Settings, Sparkles, Wand2, X, Type, ArrowDownWideNarrow, Map, Clock].map((Icon, idx) => (
          <Card key={idx} className="flex h-20 items-center justify-center gap-2 border border-dashed">
            <Icon className="text-slate-700" size={22} />
          </Card>
        ))}
      </div>
    </FIGMA_SCREEN_WRAPPER>
  ),
};

export const Shadows: Story = {
  render: () => <FigmaReferenceImage storyName="Shadows" />,
};

export const MainSidebar: Story = {
  render: () => (
    <FIGMA_SCREEN_WRAPPER>
      <MyRoadmapsSidebarComponent className="h-[860px]" />
    </FIGMA_SCREEN_WRAPPER>
  ),
};

export const RoadmapCard: Story = {
  render: () => <FigmaReferenceImage storyName="RoadmapCard" />,
};

export const Sort: Story = {
  render: () => <FigmaReferenceImage storyName="Sort" />,
};

export const SortSelectMenu: Story = {
  render: () => <FigmaReferenceImage storyName="SortSelectMenu" />,
};

export const TypeSelectMenu: Story = {
  render: () => <FigmaReferenceImage storyName="TypeSelectMenu" />,
};

export const FilterSelectMenu: Story = {
  render: () => <FigmaReferenceImage storyName="FilterSelectMenu" />,
};

export const CreateRoadmap: Story = {
  render: () => <FigmaReferenceImage storyName="CreateRoadmap" />,
};

export const CreateRoadmapAdd: Story = {
  render: () => <FigmaReferenceImage storyName="CreateRoadmapAdd" />,
};

export const CreateDirectory: Story = {
  render: () => <FigmaReferenceImage storyName="CreateDirectory" />,
};

export const CreateDirectoryAdd: Story = {
  render: () => <FigmaReferenceImage storyName="CreateDirectoryAdd" />,
};

export const MoveFile: Story = {
  render: () => <FigmaReferenceImage storyName="MoveFile" />,
};

export const MoveFileCard: Story = {
  render: () => <FigmaReferenceImage storyName="MoveFileCard" />,
};

export const MyRoadmapsFilter: Story = {
  render: () => <FigmaReferenceImage storyName="MyRoadmapsFilter" />,
};

export const MyRoadmapsHeader: Story = {
  render: () => <FigmaReferenceImage storyName="MyRoadmapsHeader" />,
};

export const FileTree: Story = {
  render: () => <FigmaReferenceImage storyName="FileTree" />,
};

export const CommunityHeader: Story = {
  render: () => (
    <FIGMA_FRAME width="1440px" height="44px" className="max-w-none p-0">
      <div className="flex w-full items-start justify-center bg-slate-100 p-0">
        <CommunityHero />
      </div>
    </FIGMA_FRAME>
  ),
};

export const CommunitySearch: Story = {
  render: () => <FigmaReferenceImage storyName="CommunitySearch" />,
};

export const CommunitySortSelect: Story = {
  render: () => <FigmaReferenceImage storyName="CommunitySortSelect" />,
};

export const CommunityUserContribute: Story = {
  render: () => <FigmaReferenceImage storyName="CommunityUserContribute" />,
};

export const ProfilePage: Story = {
  render: () => <FigmaReferenceImage storyName="ProfilePage" />,
};

export const ProfileComponents: Story = {
  render: () => (
    <FIGMA_SCREEN_WRAPPER className="bg-slate-100">
      <div className="w-full max-w-[960px] space-y-6 rounded-xl bg-white p-8 shadow-sm">
        <ProfileHeader
          userName="홍길동"
          email="hong@example.com"
          followerCount={12}
          followingCount={3}
        />
        <ProfileBio bio="빠르게 성장하는 프론트엔드 개발자입니다." />
        <ProfileCustomOrganization initialValue="Jagalchi" />
        <ProfileCustomLinks
          initialLinks={[
            { id: '1', name: 'GitHub', url: 'https://github.com/jagalchi' },
            { id: '2', name: 'Blog', url: 'https://example.com' },
          ]}
        />
        <ContributionGraph data={[]} />
        <ProfileInfoForm name="홍길동" email="hong@example.com" followerCount={12} followingCount={3} />
        <ProfileMadeRoadmapList />
        <ProfileThirdBoxComponent />
      </div>
    </FIGMA_SCREEN_WRAPPER>
  ),
};

export const ProfileStreak: Story = {
  render: () => <FigmaReferenceImage storyName="ProfileStreak" />,
};

export const AuthComponents: Story = {
  render: () => <FigmaReferenceImage storyName="AuthComponents" />,
};

export const EditorComponents: Story = {
  render: () => (
    <FIGMA_SCREEN_WRAPPER className="min-h-[900px] p-0">
      <FigmaEditorCanvas>
        <div className="absolute left-0 top-0 z-10">
          <div className="m-6 flex items-center gap-2 rounded-lg border bg-white p-2 shadow-sm">
            <h3 className="text-sm font-semibold">Editor Components</h3>
          </div>
        </div>
      </FigmaEditorCanvas>
    </FIGMA_SCREEN_WRAPPER>
  ),
};

export const RoadmapComponents: Story = {
  render: () => <FigmaReferenceImage storyName="RoadmapComponents" />,
};

export const MenusComponents: Story = {
  render: () => (
    <FIGMA_SCREEN_WRAPPER className="min-h-[900px] p-0">
      <FigmaEditorCanvas>
      <div className="flex w-full flex-wrap items-center gap-3">
        <EditorToolbar />
        <EditorSidebar />
        <div className="border-border ml-6 rounded-md border bg-white/90 px-4 py-2 text-xs text-slate-500">
          Editor Header
        </div>
        <ToolbarButton icon={<Settings />} label="Settings" isActive={false} onClick={() => {}} />
        <LoadingButton isLoading={false}>버튼</LoadingButton>
        <Button size="sm">Button</Button>
        <Button variant="outline" size="sm">
          Outline
        </Button>
      </div>
      <Separator className="mt-6" />
      </FigmaEditorCanvas>
    </FIGMA_SCREEN_WRAPPER>
  ),
};

export const AIComponents: Story = {
  render: () => <FigmaReferenceImage storyName="AIComponents" />,
};

export const EditorPages: Story = {
  render: () => <FigmaReferenceImage storyName="EditorPages" />,
};

export const ViewerRoadmapComponents: Story = {
  render: () => <FigmaReferenceImage storyName="ViewerRoadmapComponents" />,
};

export const ViewerCardComponents: Story = {
  render: () => <FigmaReferenceImage storyName="ViewerCardComponents" />,
};

export const ZoomButtonGroup: Story = {
  render: () => <FigmaReferenceImage storyName="ZoomButtonGroup" />,
};

export const ViewerSidebar: Story = {
  render: () => <FigmaReferenceImage storyName="ViewerSidebar" />,
};

export const RoadmapExamples: Story = {
  render: () => <FigmaReferenceImage storyName="RoadmapExamples" />,
};

export const RoadmapHeader: Story = {
  render: () => <FigmaReferenceImage storyName="RoadmapHeader" />,
};

export const HeaderMenu: Story = {
  render: () => <FigmaReferenceImage storyName="HeaderMenu" />,
};

export const HeaderExportMenu: Story = {
  render: () => <FigmaReferenceImage storyName="HeaderExportMenu" />,
};

export const HeaderSaveAsImageMenu: Story = {
  render: () => <FigmaReferenceImage storyName="HeaderSaveAsImageMenu" />,
};

export const ViewerPages: Story = {
  render: () => <FigmaReferenceImage storyName="ViewerPages" />,
};
