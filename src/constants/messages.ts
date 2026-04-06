export const AUTH_MESSAGES = {
  LOGIN_EMAIL_NOT_FOUND: '존재하지 않는 이메일입니다',
  LOGIN_PASSWORD_MISMATCH: '비밀번호가 일치하지 않습니다',
  LOGIN_FAILED: '로그인에 실패했습니다',
  VERIFICATION_CODE_SENDING: '전송 중...',
  VERIFICATION_CODE_SEND: '인증번호 전송',
  VERIFICATION_CODE_RESEND: '재전송',
  VERIFICATION_CODE_RESEND_COOLDOWN: '초 후 재전송',
  LOGOUT: '로그아웃',
} as const;

export const COMMUNITY_MESSAGES = {
  NOT_FOUND: '로드맵을 찾을 수 없습니다.',
  VIEW_ROADMAP: '로드맵 보기',
  ADD_TO_MY_ROADMAPS: '내 로드맵에 추가',
  LOGIN_REQUIRED: '로그인 후 이용 가능합니다',
  LIKE: '좋아요',
  ABOUT: 'About',
  MADE_BY: 'Made by',
  LAST_UPDATED: '마지막 업데이트',
} as const;

export const MY_ROADMAPS_MESSAGES = {
  SEARCH_PLACEHOLDER: '로드맵 검색',
  NEW_ROADMAP: '로드맵',
  NEW_DIRECTORY: '디렉토리',
  SHARE_COPIED: '링크가 클립보드에 복사되었습니다',
  SHARE_FAILED: '링크 복사에 실패했습니다',
  // Delete dialog
  DELETE_TITLE: '정말 삭제하시겠습니까?',
  DELETE_DESCRIPTION: '이 작업은 되돌릴 수 없습니다.',
  DELETE_CANCEL: '취소',
  DELETE_CONFIRM: '삭제',
  // Rename dialog
  RENAME_TITLE: '이름 수정',
  RENAME_CANCEL: '취소',
  RENAME_CONFIRM: '확인',
  RENAME_PLACEHOLDER: '새 이름을 입력하세요',
} as const;

export const PROFILE_MESSAGES = {
  BIO_TITLE: '자기소개',
  COMPLETED_ROADMAP: '완주한 로드맵',
  IN_PROGRESS_ROADMAP: '진행중인 로드맵',
  MADE_ROADMAP: '만든 로드맵',
} as const;

export const VIEWER_MESSAGES = {
  // Header menus
  MENU_STATISTICS: '로드맵 통계',
  MENU_DARK_MODE: '다크모드 전환',
  MENU_EXPORT: '내보내기',
  MENU_SAVE_IMAGE: '이미지로 저장',
  MENU_IMPORT_JSON: 'JSON으로 가져오기',
  MENU_VERSION: '버전 보기',
  EXPORT_MARKDOWN: 'Markdown',
  EXPORT_PDF: 'PDF',
  EXPORT_JSON: 'JSON',
  IMAGE_PNG: 'PNG',
  IMAGE_JPG: 'JPG',
  IMAGE_SVG: 'SVG',
  // Sidebar
  SIDEBAR_TITLE: '노드 목록',
  SIDEBAR_SEARCH_PLACEHOLDER: '노드 검색',
  SIDEBAR_EMPTY: '노드가 없습니다',
  SIDEBAR_DETAIL_DESCRIPTION: '설명',
  SIDEBAR_DETAIL_RESOURCES: '첨부 자료',
  SIDEBAR_TOTAL_COUNT: '총 {count}개 노드',
  // Loading/Error
  LOADING: '로드맵을 불러오는 중...',
  ERROR_NOT_FOUND: '로드맵을 찾을 수 없습니다',
  ERROR_LOAD_FAILED: '로드맵을 불러오는데 실패했습니다',
  // View mode
  VIEW_CANVAS: '캔버스',
  VIEW_CARDS: '카드',
} as const;

export const EDITOR_MESSAGES = {
  SAVE_SUCCESS: '저장됨',
  SAVE_FAILED: '저장 실패',
  // Phase 1: React Flow 노드 기본값
  FLOW_NODE_DEFAULT_LABEL: '새 노드',
  FLOW_SECTION_DEFAULT_TITLE: '빈 섹션',
  FLOW_TEXT_DEFAULT_CONTENT: '텍스트',
  // Phase 2: Sidebar 라벨
  SIDEBAR_EMPTY_STATE: '노드를 선택하세요',
  SIDEBAR_NODE_NAME_LABEL: '노드 이름',
  SIDEBAR_NODE_DESC_LABEL: '노드 설명',
  SIDEBAR_SECTION_NAME_LABEL: '섹션 이름',
  SIDEBAR_SECTION_SIZE_LABEL: '크기',
  SIDEBAR_TEXT_SIZE_LABEL: '텍스트 크기',
  SIDEBAR_EDGE_LABEL_LABEL: '라벨',
  SIDEBAR_EDGE_STYLE_LABEL: '스타일',
  SIDEBAR_EDGE_STYLE_SOLID: '실선',
  SIDEBAR_EDGE_STYLE_DASHED: '점선',
  SIDEBAR_EDGE_STYLE_WAVY: '꼬인선',
  SIDEBAR_EDGE_ARROW_LABEL: '화살표',
  SIDEBAR_EDGE_THICKNESS_LABEL: '두께',
  SIDEBAR_COLOR_PRESET_LABEL: '기본 컬러',
  SIDEBAR_COLOR_CUSTOM_LABEL: '커스텀',
  SIDEBAR_RESOURCES_LABEL: '첨부 자료',
  SIDEBAR_ADD_RESOURCE_BUTTON: '추가',
  // Phase 2: Toolbar 툴팁
  TOOLBAR_NODE_TOOLTIP: '노드 추가',
  TOOLBAR_LINE_TOOLTIP: '선 추가',
  TOOLBAR_SECTION_TOOLTIP: '섹션 추가',
  TOOLBAR_TEXT_TOOLTIP: '텍스트 추가',
  TOOLBAR_GEAR_TOOLTIP: '설정',
  // Phase 3: AI Menu
  AI_MENU_GENERATE_LABEL: '로드맵 생성',
  AI_MENU_MODIFY_LABEL: '로드맵 수정',
  // Phase 2: ColorPicker
  COLOR_PICKER_TITLE: '컬러 선택',
  COLOR_PICKER_CANCEL: '취소',
  COLOR_PICKER_APPLY: '적용',
  // Phase 3: Multi-select
  MULTI_SELECT_TITLE: '다중 선택',
  MULTI_SELECT_COUNT: '개 선택됨',
  MULTI_SELECT_ALIGN_LABEL: '정렬',
  MULTI_SELECT_SPACING_LABEL: '간격',
  MULTI_SELECT_NAME_MIXED: 'Mixed',
  MULTI_SELECT_DESC_MIXED: 'Mixed',
  MULTI_SELECT_ALIGN_LEFT: '왼쪽 정렬',
  MULTI_SELECT_ALIGN_CENTER: '가운데 정렬',
  MULTI_SELECT_ALIGN_RIGHT: '오른쪽 정렬',
  MULTI_SELECT_ALIGN_TOP: '위쪽 정렬',
  MULTI_SELECT_ALIGN_MIDDLE: '중간 정렬',
  MULTI_SELECT_ALIGN_BOTTOM: '아래쪽 정렬',
  // AI 기능
  AI_GENERATE_ROADMAP: '로드맵 생성',
  AI_MODIFY_ROADMAP: '로드맵 수정',
  AI_MENU_LABEL: 'AI 메뉴',
  RESOURCE_DELETE_CONFIRM: '자료를 삭제하시겠습니까?',
  // Phase 4: AI Modal
  AI_MODAL_GENERATE_TITLE: 'AI 로드맵 생성',
  AI_MODAL_MODIFY_TITLE: 'AI 로드맵 수정',
  AI_MODAL_GENERATE_PLACEHOLDER: '어떤 로드맵을 만들고 싶으신가요? (예: React 웹 개발 로드맵)',
  AI_MODAL_MODIFY_PLACEHOLDER: '로드맵을 어떻게 수정하고 싶으신가요? (예: 난이도를 낮춰주세요)',
  AI_MODAL_CANCEL: '취소',
  AI_MODAL_GENERATE_BUTTON: '생성',
  AI_MODAL_MODIFY_BUTTON: '수정',
  AI_MODAL_LOADING: 'AI가 작업 중입니다...',
  AI_RESOURCE_MODAL_TITLE: 'AI 자료 추천',
  AI_RESOURCE_MODAL_SUBTITLE: '선택한 노드에 적합한 학습 자료를 추천합니다',
  AI_RESOURCE_MODAL_RECOMMEND_BUTTON: '추천받기',
  AI_RESOURCE_MODAL_CLOSE: '닫기',
  AI_RESOURCE_MODAL_LOADING: '자료를 찾는 중...',
  AI_RESOURCE_MODAL_EMPTY: '추천할 자료가 없습니다',
} as const;
