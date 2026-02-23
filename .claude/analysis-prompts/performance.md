# Performance Analysis

Analyze CSS and rendering performance issues.

## Context

- Next.js 16 App Router
- React 19
- Tailwind CSS v4
- Target: Lighthouse score > 90

## Analysis Focus

### 1. CSS Class Bloat

- Components with 100+ utility classes
- Excessive className strings
- Duplicate class combinations
- Opportunities for @apply or component extraction

**Examples to check:**

```tsx
// Long classNames in:
- Button variants
- Dialog/Modal styles
- Form components
```

### 2. Render Performance

- Inline style objects (cause re-renders)
- Dynamic className generation in render
- Unnecessary conditional classes
- className computation that should be memoized

### 3. Animation Performance

- Animations not using transform/opacity
- Missing will-change optimization
- Complex transitions
- Animation jank potential

**Check:**

```css
animate-in, fade-in, zoom-in (are they optimized?)
transition-all (should be specific properties)
```

### 4. Layout Shift (CLS)

- Missing width/height on images
- Dynamic content without space reservation
- Font loading causing shifts
- Lazy-loaded components without skeleton

### 5. Unused CSS

- Tailwind classes that are never used
- Dead code in components
- Over-specific variants
- Utility classes that could be purged

### 6. Critical CSS

- Above-the-fold styles
- CSS blocking render
- Inline critical styles
- Font loading strategy

### 7. Tailwind-Specific Performance

- JIT mode issues
- Safelist bloat
- Complex selectors
- Pseudo-class overuse

**Check patterns like:**

```
[&_svg]:size-4
[&>svg]:pointer-events-none
has-[>svg]:px-3
```

### 8. Component Rendering

- Components that re-render unnecessarily
- Missing React.memo opportunities
- Expensive className computations
- Style calculations in render

## Output Format

### 1. Performance Impact Matrix

Categorize by:

- Impact: Critical/High/Medium/Low
- Area: Rendering/CLS/Animation/Bundle
- Fix Effort: Easy/Medium/Hard

### 2. Critical Issues

Problems that cause:

- Layout shifts
- Slow renders
- Janky animations
- Large bundle size

### 3. className Optimization

Components needing refactor:

- Extract to CVA variants
- Convert to @apply
- Split into sub-components
- Simplify conditional logic

### 4. Animation Improvements

- Use transform instead of position
- Add will-change hints
- Simplify transitions
- Remove transition-all

### 5. Bundle Size Impact

- Unused Tailwind utilities
- Over-specific selectors
- Bloated component styles

### 6. Quick Wins

Top 10 performance improvements:

- Replace transition-all with specific properties
- Add image dimensions
- Memoize expensive className logic
- Extract repeated class combinations

### 7. Lighthouse Predictions

Estimate impact on:

- Performance score
- CLS score
- FCP/LCP
- TBT

Provide before/after predictions for major fixes.
