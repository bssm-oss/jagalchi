# Accessibility Analysis

Analyze accessibility compliance and issues (WCAG 2.1 AA standards).

## Context

- Learning platform targeting students
- Must support keyboard navigation
- Should support screen readers
- Needs high contrast for readability

## Analysis Focus

### 1. Color Contrast Issues

Check against WCAG 2.1 AA (4.5:1 for text, 3:1 for large text)

**Known problematic colors:**

```
text-[#6B7280] on bg-white
text-[#4B5563] on bg-[#F3F5F7]
text-slate-400 on various backgrounds
text-muted-foreground (check contrast)
```

Calculate and report:

- Contrast ratios
- Pass/Fail status
- Alternative color suggestions

### 2. Focus States

- Missing focus-visible styles
- Inconsistent focus indicators
- Elements with :focus removed
- Focus trap in modals/dialogs

**Check:**

- All interactive elements have visible focus
- Focus indicator has 3:1 contrast minimum
- Focus order is logical
- No focus on non-interactive elements

### 3. Keyboard Navigation

- Missing keyboard handlers
- Buttons that should be links
- Links that should be buttons
- Keyboard traps
- Missing Tab index management

**Test patterns:**

- Modals (focus trap + escape)
- Dropdowns (arrow keys)
- Forms (tab order)
- Custom components

### 4. ARIA Attributes

- Missing aria-label on icon-only buttons
- Missing aria-describedby
- Incorrect ARIA usage
- Redundant ARIA

**Examples found:**

```tsx
// Good: ProfileBio has aria-expanded, aria-controls, aria-label
// Check: Other buttons, modals, tooltips
```

### 5. Semantic HTML

- Divs that should be buttons
- Spans that should be text elements
- Missing heading hierarchy
- List markup issues

### 6. Form Accessibility

- Missing labels
- Placeholder as label
- Error message association
- Required field indication
- Input types

### 7. Image Accessibility

- Missing alt text
- Decorative images without alt=""
- Complex images without descriptions
- Icon accessibility

### 8. Interactive Elements

- Touch target size (44x44px minimum)
- Disabled state communication
- Loading state communication
- Error state communication

## Output Format

### 1. Critical Issues (WCAG A/AA failures)

- Issue type
- Affected components
- WCAG criterion violated
- Impact on users
- Fix required

### 2. Color Contrast Report

For each failing combination:

```
Background: #F3F5F7
Foreground: #6B7280
Contrast: 2.1:1 (FAIL)
Required: 4.5:1
Suggestion: Use #4B5563 (contrast: 5.2:1)
```

### 3. Focus State Audit

List all interactive elements:

- Has focus-visible: Yes/No
- Focus indicator contrast: Pass/Fail
- Focus trap handling: Good/Missing

### 4. ARIA Issues

- Missing ARIA labels
- Incorrect ARIA usage
- Redundant ARIA
- ARIA vs semantic HTML opportunities

### 5. Quick Wins

Top 10 easiest accessibility improvements:

- Add aria-label to icon buttons
- Fix color contrast on specific text
- Add focus-visible to custom buttons

### 6. Priority Matrix

- Critical: Blocks screen reader users
- High: Difficult for keyboard users
- Medium: Better UX needed
- Low: Nice to have

Focus on Critical + High priority items.
