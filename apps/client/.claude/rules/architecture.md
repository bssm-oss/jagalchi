# Architecture

## Feature-based Structure

Each feature is isolated and self-contained:

```
features/
└── auth/
    ├── components/
    ├── hooks/
    ├── types/
    └── index.ts
```

## Dependency Rules

```
app (routing/pages)
  ↓ can import
features (isolated)
  ↓ can import
shared (components, hooks, lib, types, constants)
```

- `app/` → can import from anywhere
- `features/` → can only import from shared modules
- Features **CANNOT** import from other features

## Adding shadcn Components

```bash
pnpm dlx shadcn@latest add <component>
```

Components are installed to `src/components/ui/`.
