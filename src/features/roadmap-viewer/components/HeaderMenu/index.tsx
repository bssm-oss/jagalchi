const HEADER_MENU_ITEMS = ['로드맵 통계', '다크모드 전환', '내보내기', '이미지로 저장'] as const;

const HEADER_MENU_ITEM_CLASS =
  'flex h-[44px] w-[160px] items-center px-[12px] text-left text-[12px] font-medium text-slate-700';

export function HeaderMenu() {
  return (
    <div className="inline-block min-h-[44px] rounded-md border border-white/10 bg-[#020617] p-1 shadow-[0_1px_0_rgba(15,23,42,0.35)]">
      {HEADER_MENU_ITEMS.map((label, index) => (
        <button
          type="button"
          key={label}
          className={`${HEADER_MENU_ITEM_CLASS}${index < HEADER_MENU_ITEMS.length - 1 ? 'border-b border-white/10' : ''} text-slate-100`}
        >
          {label}
        </button>
      ))}
    </div>
  );
}
