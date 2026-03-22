'use client';

// Image format handlers for use in HeaderMenu's 내보내기 submenu.
// The standalone dropdown UI has been merged into HeaderMenu.

export function handleSaveAsPng() {
  // TODO: implement PNG export
}

export function handleSaveAsJpg() {
  // TODO: implement JPG export
}

export function handleSaveAsSvg() {
  // TODO: implement SVG export
}

// Re-export a no-op component for backward compatibility.
export function HeaderSaveAsImageMenu() {
  return null;
}
