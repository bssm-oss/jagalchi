import { Download } from 'lucide-react';

const EXPORT_ITEMS = ['마크다운', 'PDF', 'JSON'] as const;

export function HeaderExportMenu() {
  return (
    <div className="inline-block min-h-[36px] rounded-md border border-white/10 bg-[#020617] shadow-[0_1px_0_rgba(15,23,42,0.35)]">
      {EXPORT_ITEMS.map((label, index) => (
        <button
          type="button"
          key={label}
          className={`inline-flex h-[36px] w-[160px] items-center gap-2 px-3 text-[12px] font-medium text-slate-100${index < EXPORT_ITEMS.length - 1 ? 'border-b border-white/10' : ''}`}
        >
          <Download className="h-3.5 w-3.5 text-slate-300" strokeWidth={2} />
          {label}
        </button>
      ))}
    </div>
  );
}
