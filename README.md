# Jagalchi Monorepo

This repository is a history-preserving monorepo assembled from the original Jagalchi sibling repositories.

## Layout

- `apps/client` - imported from `jagalchi-client` branch `develop`
- `services/ai` - imported from `jagalchi-server-AI` branch `master`
- `services/api-gateway` - imported from `jagalchi-server-api-gateway` branch `main`
- `services/node` - imported from `jagalchi-server-node` branch `main`
- `services/roadmap` - imported from `jagalchi-server-roadmap` branch `master`
- `services/user` - imported from `jagalchi-server-user` branch `main`

## Migration method

Each source repository was imported with `git subtree add --prefix <target-path> <source-path> <branch>` without `--squash`, preserving the original commit history in this repository. The original repositories were left untouched.
