# Code Quality Analysis

Final analysis covering code readability, maintainability, and overall quality.

## Context

- 144 TSX components
- Team project (multiple contributors)
- Need high maintainability
- Following project conventions in .claude/

## Analysis Focus

### 1. Readability Issues

- Overly long className strings (100+ chars)
- Complex nested conditionals
- Unclear class combinations
- Missing comments on complex logic

### 2. Consistency

- Naming conventions (camelCase, PascalCase, kebab-case)
- File organization
- Import order
- Export patterns

**Check:**

- Named vs default exports
- Barrel file usage
- Feature isolation

### 3. Convention Violations

From project rules (.claude/):

- Named exports only (no default)
- No wildcard exports
- Boolean naming (is/has/can/should)
- Feature isolation (no cross-imports)

**Find:**

- Default exports
- Wildcard re-exports
- Boolean variables without prefix
- Cross-feature imports

### 4. Comments and Documentation

- Missing JSDoc on complex logic
- Over-commenting obvious code
- Outdated comments
- TODO comments

### 5. Magic Values

Already found many:

```
#F3F5F7, #1F2937 (colors)
400px, 240px (widths)
14px, 13px, 11px (font sizes)
```

**Categorize:**

- Should be constants
- Should be config
- Should be CSS variables
- Actually fine (one-off values)

### 6. Code Duplication

- Repeated className patterns
- Copied component logic
- Similar but not identical components
- Utility function opportunities

### 7. Type Safety

- Missing types
- `any` usage
- Type assertions
- Implicit types that should be explicit

### 8. Component Design

- Components too large (> 300 lines)
- Components too small (over-abstraction)
- Mixed concerns
- Single Responsibility violations

### 9. Error Handling

- Missing error boundaries
- Unhandled edge cases
- No loading states
- No empty states

### 10. Testing Gaps

- Components without tests
- Missing edge case tests
- Test quality issues

## Output Format

### 1. Convention Violations

List all violations of project rules:

- Rule violated
- Files affected
- Example
- Fix required

### 2. Readability Score

Rate components by readability:

- Excellent (clear, simple)
- Good (minor issues)
- Fair (needs improvement)
- Poor (hard to understand)

Provide examples from each category.

### 3. Duplication Report

Code that's repeated:

- Pattern description
- Occurrences count
- Files affected
- Extraction recommendation

### 4. Magic Value Audit

All hardcoded values:

- Value
- Usage count
- Category (color/size/etc)
- Recommended constant name
- Where to define it

### 5. Refactoring Priorities

Components needing attention:

- Reason (too long, duplicated, complex)
- Impact (how many places affected)
- Effort (easy/medium/hard)
- Suggested approach

### 6. Quick Wins

Top 10 code quality improvements:

- Extract repeated classNames
- Add missing types
- Fix naming conventions
- Add JSDoc to complex logic

### 7. Overall Assessment

- Strengths of the codebase
- Major weaknesses
- Technical debt areas
- Recommended next steps

### 8. Metrics Summary

```
Total Issues: X
Critical: X (must fix)
High: X (should fix soon)
Medium: X (plan to fix)
Low: X (nice to have)
```

Be thorough but focus on actionable feedback.
