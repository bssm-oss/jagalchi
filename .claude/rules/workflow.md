# Workflow

## Branch Naming

```
<type>/#<issue-number>-<short-description>
```

Example: `feat/#12-user-login`

## Commit Convention

```
<type>(<scope>): <subject>
```

### Types

| Type     | Usage                   |
| -------- | ----------------------- |
| feat     | New feature             |
| fix      | Bug fix                 |
| refactor | Code refactoring        |
| perf     | Performance improvement |
| format   | Code formatting         |
| docs     | Documentation           |
| action   | GitHub Actions          |
| test     | Test code               |
| ai       | AI coding work          |
| chore    | Miscellaneous           |
| revert   | Rollback                |
| wip      | Work in progress        |
| hotfix   | Emergency fix           |

Example: `feat(auth): implement login`

## PR Convention

```
<type>(#<issue-number>): <brief description>
```

Example: `feat(#12): add social login`

## Before Commit Checklist

- [ ] `pnpm lint` passes
- [ ] `pnpm build` passes
- [ ] Related issue linked
