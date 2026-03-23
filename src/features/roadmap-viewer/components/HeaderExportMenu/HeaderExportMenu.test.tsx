import { describe, it, expect } from 'vitest';

import { HeaderExportMenu, handleExportMarkdown, handleExportPdf, handleExportJson } from './index';

describe('HeaderExportMenu', () => {
  it('renders null (merged into HeaderMenu)', () => {
    const result = HeaderExportMenu();
    expect(result).toBeNull();
  });

  it('exports handleExportMarkdown as a function', () => {
    expect(typeof handleExportMarkdown).toBe('function');
  });

  it('exports handleExportPdf as a function', () => {
    expect(typeof handleExportPdf).toBe('function');
  });

  it('exports handleExportJson as a function', () => {
    expect(typeof handleExportJson).toBe('function');
  });
});
