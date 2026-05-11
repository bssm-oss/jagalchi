# Component Architecture Analysis

Analyze the component architecture and styling patterns in this React/Next.js codebase.

## Context

- 144 TSX components total
- Uses CVA (class-variance-authority) for some components
- Mix of shadcn/ui base components and custom feature components
- Tailwind CSS v4 with CSS variables

## Analysis Focus

### 1. Duplicate className Patterns

Find className combinations that are repeated across multiple files. Examples:

- Button styles repeated in different components
- Layout patterns (flex, gap, padding combos)
- Typography combinations
- Border/shadow patterns

### 2. CVA Variants Consistency

- Check which components use CVA and which don't
- Find components that should use CVA but have inline variants instead
- Identify inconsistent variant patterns
- Compare similar components (buttons, cards, modals)

### 3. Long className Strings

Find components with excessively long className attributes:

- Lines with 10+ utility classes
- Complex responsive modifiers
- Nested pseudo-selectors
- Data attribute selectors

### 4. Reusable Pattern Opportunities

Identify patterns that appear 3+ times and could be:

- Extracted to new components
- Converted to CVA variants
- Added to design system utilities

### 5. Component Complexity

- Components with too many style props
- Inline style logic that should be variants
- Complex conditional className logic
- Components that mix concerns

### 6. Shadcn/ui vs Custom Patterns

- Check consistency between shadcn/ui components
- Find custom components that reinvent shadcn patterns
- Identify gaps in shadcn/ui coverage

## Specific Issues Found (from Grep)

### Hardcoded Colors (30+ instances):

```
#020617, #1F2937, #4B5563, #6B7280
#F3F5F7, #E5E7EB, #E2E8F0, #F3F4F6
#2563EB, #1D4ED8
#64748B, #475569
#374151
#0f172a, #1e293b, #81868f
```

### Hardcoded Spacing (30+ instances):

```
w-[350px], w-[400px], w-[304px], w-[240px], w-[140px], w-[696px]
h-[400px], h-[200px], h-[180px], h-[36px], h-[32px]
gap-[10px], gap-[64px], gap-[40px], gap-[16px], gap-[8px]
```

### Hardcoded Typography (30+ instances):

```
text-[14px], text-[13px], text-[11px], text-[10px], text-[12px]
text-[30px], text-[20px], text-[18px], text-[16px], text-[24px]
tracking-[0.07px], tracking-[-1px], tracking-[-0.02em]
```

## Output Format

### 1. Duplicate Patterns Summary

- Pattern description
- Files affected (count + examples)
- Recommended extraction strategy
- Estimated complexity (Easy/Medium/Hard)

### 2. CVA Opportunities

- Components that need CVA
- Missing variants in existing CVA components
- Inconsistent variant naming

### 3. Refactoring Priorities

Rank by:

- Impact (how many files affected)
- Effort (complexity of fix)
- Value (maintainability gain)

### 4. Quick Wins

Top 10 easiest refactorings with high impact

### 5. Architecture Recommendations

- New components to create
- Existing components to merge
- Design system additions needed

Be specific with file names, line numbers, and exact code examples.
