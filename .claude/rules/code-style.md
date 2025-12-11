# Code Style

## Naming Conventions

| Target              | Style                    | Example                   |
| ------------------- | ------------------------ | ------------------------- |
| Variables/Functions | camelCase                | `userName`, `getUserData` |
| Constants           | UPPER_SNAKE              | `MAX_COUNT`, `API_URL`    |
| Components/Types    | PascalCase               | `LoginButton`, `UserData` |
| Files (component)   | PascalCase               | `LoginButton.tsx`         |
| Files (general)     | kebab-case               | `use-auth.ts`             |
| Folders             | kebab-case               | `user-profile/`           |
| Boolean             | is/has/can/should prefix | `isLoading`, `hasError`   |

## Import Order

1. React/Next.js
2. External libraries
3. Internal modules (@/)
4. Relative paths
5. Types

## Component Rules

- Use arrow functions
- Define Props with interface
- Use default export
- One component per file

## Forbidden

- `any` type (add comment if unavoidable)
- `console.log` in production
- Cross-feature imports
