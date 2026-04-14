# Tailwind Best Practices Analysis

Analyze Tailwind CSS usage patterns and best practices in this Next.js project.

## Context

- Tailwind CSS v4 (CSS variable-based)
- 144+ TSX components
- Custom theme defined in globals.css
- Mix of shadcn/ui and custom components

## Analysis Focus

### 1. @apply Usage

- Find all uses of `@apply` in CSS files
- Check if `@apply` is overused (should be minimal in v4)
- Identify cases where utility classes would be better
- Find complex `@apply` chains that hurt readability

### 2. Arbitrary Values Overuse

Pattern: `[value]` syntax

Already found (from previous Grep):

- Colors: `bg-[#F3F5F7]`, `text-[#1F2937]`, etc.
- Spacing: `w-[400px]`, `h-[180px]`, `gap-[10px]`, etc.
- Typography: `text-[14px]`, `tracking-[0.07px]`, etc.

**Check:**

- Are these values truly unique or should they be in config?
- Can they be replaced with standard Tailwind scale?
- Should they become CSS variables?

### 3. Config Extension Needed

Based on repeated arbitrary values, what should be added to theme?

**Potential additions:**

- Custom widths (240px sidebar, 400px modal)
- Custom heights
- Custom font sizes (11px, 13px)
- Custom colors (all hex codes)
- Custom tracking values

### 4. className Organization

- Find very long className strings (100+ chars)
- Check for logical grouping (layout → spacing → typography → colors)
- Find confusing class order
- Identify places where cn() utility isn't used

### 5. Responsive Design Patterns

- Check mobile-first approach (bare class should be mobile)
- Find desktop-first patterns (need refactoring)
- Inconsistent breakpoint usage
- Missing responsive variants

### 6. Dark Mode Support

- Components using explicit `bg-white`, `text-black`
- Missing `dark:` variants where needed
- Inconsistent dark mode patterns
- Components that break in dark mode

### 7. Performance Anti-patterns

- Overly specific selectors
- Unnecessary important modifiers
- Complex pseudo-selectors
- Nested `@apply` chains

### 8. Tailwind v4 Specific Issues

- Using v3 syntax in v4 project
- Not leveraging CSS variable features
- Missing theme inline usage
- Config that should be in CSS

## Output Format

### 1. @apply Analysis

- Total usage count
- Good uses (component base styles)
- Bad uses (should be utilities)
- Refactoring suggestions

### 2. Config Recommendations

Specific theme extensions needed:

```typescript
theme: {
  extend: {
    width: {
      'sidebar': '240px',
      'modal': '400px',
    },
    colors: {
      // ... specific mappings
    }
  }
}
```

### 3. Quick Wins

Top 10 easiest improvements:

- Replace `w-[400px]` with `w-modal`
- Remove unnecessary arbitrary values
- Add missing dark: variants

### 4. Breaking Issues

Problems that actually break functionality:

- Dark mode issues
- Responsive breakage
- Accessibility conflicts

### 5. Priority Matrix

Categorize by:

- Impact: High/Medium/Low
- Effort: Easy/Medium/Hard
- Category: Config/Refactor/Remove

Focus on High Impact + Easy Effort items first.
