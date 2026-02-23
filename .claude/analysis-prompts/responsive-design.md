# Responsive Design Analysis

Analyze responsive design implementation and potential issues.

## Context

- Next.js 16 with Tailwind CSS v4
- Target devices: Mobile, Tablet, Desktop
- Standard breakpoints: sm (640px), md (768px), lg (1024px), xl (1280px)

## Analysis Focus

### 1. Mobile-First Violations

- Find desktop-first patterns (bare class = desktop, sm: = mobile)
- Components without mobile considerations
- Fixed widths that break on mobile
- Hardcoded pixel values that don't scale

### 2. Breakpoint Consistency

- Check if breakpoints are used consistently
- Find unusual breakpoint combinations
- Missing intermediate breakpoints
- Over-specific responsive patterns

### 3. Fixed Width/Height Issues

Already found:

```
w-[400px], w-[350px], w-[304px], w-[240px], w-[140px], w-[696px], w-[960px]
h-[400px], h-[200px], h-[180px], h-[36px], h-[32px], h-[676px]
```

**Check which ones:**

- Break on small screens
- Should use max-w- instead
- Should use responsive variants
- Are actually fine (modals, sidebars)

### 4. Layout Patterns

- Grid/flex layouts without responsive adjustments
- Container queries usage (or lack thereof)
- Sidebar/navigation responsiveness
- Card grid responsiveness

### 5. Typography Scaling

- Font sizes that don't scale
- Missing responsive typography
- Line-height issues on mobile
- Text overflow problems

### 6. Touch Targets

- Elements smaller than 44x44px (iOS guideline)
- Buttons too small for touch
- Interactive elements too close together
- Hit area improvements needed

### 7. Overflow Issues

- Horizontal scroll problems
- Text overflow
- Image overflow
- Container overflow

### 8. Critical Components

Check these specific components:

- RoadmapCard (300x180 aspect ratio)
- Modals (400px width)
- Sidebar (240px width)
- Toolbars and headers
- Forms and inputs

## Output Format

### 1. Breaking Issues

Components that are broken on mobile:

- Component name
- Issue description
- Breakpoint where it breaks
- Fix recommendation

### 2. Mobile-First Violations

Count and categorize:

- Desktop-first patterns
- Missing mobile variants
- Fixed widths without max-width

### 3. Touch Target Audit

List all interactive elements < 44x44px:

- Button sizes
- Icon-only buttons
- Links
- Form controls

### 4. Responsive Recommendations

- Components needing responsive refactor
- Breakpoint strategy improvements
- Container query opportunities

### 5. Quick Fixes

Top 10 easiest responsive improvements with high impact.

Be specific with component names and exact issues.
