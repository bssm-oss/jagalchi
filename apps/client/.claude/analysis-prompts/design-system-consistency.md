# Design System Consistency Analysis

I need you to analyze the CSS and styling consistency in this React/Next.js project using Tailwind CSS v4.

## Context

This is a learning roadmap platform called Jagalchi. It uses:

- Tailwind CSS v4 (CSS variable-based config)
- shadcn/ui components
- CVA (class-variance-authority) for variants
- CSS variables defined in globals.css

## Analysis Task: Design System Consistency

Please analyze the following aspects and find ALL issues:

### 1. Hardcoded Color Values

- Find all hex colors (e.g., #F3F5F7, #1F2937)
- Find all rgb/rgba colors
- Find all oklch colors used directly in components (not from CSS variables)
- Check if they should use CSS variables from globals.css instead

### 2. Hardcoded Spacing/Sizing

- Find magic numbers for width/height (e.g., w-[400px], h-[180px], w-[240px])
- Find hardcoded padding/margin values that aren't Tailwind standard scale
- Check if they should use standard Tailwind spacing or CSS variables

### 3. Typography Inconsistency

- Find direct font-size usage (e.g., text-[14px], text-[11px])
- Compare with CSS variables in globals.css (--heading-_, --paragraph-_)
- Check line-height and letter-spacing consistency

### 4. Border Radius & Shadow

- Check if border-radius values use --radius-\* variables
- Find hardcoded border-radius values
- Check shadow usage (should use shadow-xs, shadow-sm, etc.)

### 5. Repeated Patterns

- Find duplicate className combinations that should be extracted
- Identify common patterns that could become reusable components or CVA variants

### 6. CSS Variables Usage

- Check if existing CSS variables are being used consistently
- Find places where CSS variables should be used but aren't

## Output Format

For each category, provide:

1. **Issue Count**: Number of violations found
2. **Examples**: 3-5 specific code examples with file:line references
3. **Recommendation**: How to fix (e.g., "Use bg-[#F3F5F7] → bg-muted")
4. **Priority**: High/Medium/Low based on impact

At the end, provide:

- **Summary Statistics**: Total issues by category
- **Quick Wins**: Top 5 easiest fixes with highest impact
- **Design System Gaps**: Missing CSS variables that should be added

Be thorough and critical. Find every inconsistency.
