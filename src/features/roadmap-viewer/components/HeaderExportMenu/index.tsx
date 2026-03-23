'use client';

// Export action handlers for use in HeaderMenu's 내보내기 submenu.
// The standalone dropdown UI has been merged into HeaderMenu.

export function handleExportMarkdown() {
  // TODO: implement Markdown export
}

export function handleExportPdf() {
  // TODO: implement PDF export
}

export function handleExportJson() {
  // TODO: implement JSON export
}

// Re-export a no-op component for backward compatibility.
export function HeaderExportMenu() {
  return null;
}
