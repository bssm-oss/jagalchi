# Jagalchi Monorepo

Remote: https://github.com/bssm-oss/jagalchi.git

This repository is a history-preserving monorepo assembled from the original Jagalchi sibling repositories. It adds root-level orchestration only; the client, AI service, and Java services keep their existing frameworks and project layouts.

## Layout

- `apps/client` - Next.js client imported from `jagalchi-client` branch `develop`
- `services/ai` - Django AI service imported from `jagalchi-server-AI` branch `master`
- `services/api-gateway` - Gradle service imported from `jagalchi-server-api-gateway` branch `main`
- `services/node` - Gradle service imported from `jagalchi-server-node` branch `main`
- `services/roadmap` - Gradle service imported from `jagalchi-server-roadmap` branch `master`
- `services/user` - Gradle service imported from `jagalchi-server-user` branch `main`

## Migration method

Each source repository was imported with `git subtree add --prefix <target-path> <source-path> <branch>` without `--squash`, preserving the original commit history in this repository. The original repositories were left untouched.

## Local validation from the root

The root `justfile` delegates into the existing project directories. Install `just` locally, then run the checks you need from the repository root:

```sh
just path-check
just client-install
just client-lint
just client-test
just client-build
just ai-install
just ai-compile
just ai-check
just java-test
just java-build
just validate
```

Java service checks can also be run individually:

```sh
just api-gateway-test
just api-gateway-build
just node-test
just node-build
just roadmap-test
just roadmap-build
just user-test
just user-build
```

## Stack notes

- `apps/client` uses pnpm and its existing `package.json` scripts.
- `services/ai` uses Python dependencies from `requirements.txt`; root `just` commands install them into `services/ai/.venv` before running Django checks through `manage.py`. Installing the full dependency set requires local MySQL/MariaDB client development libraries for `mysqlclient`.
- Java services remain independent Gradle roots for now. Each service keeps its own `gradlew`, `build.gradle`, and `settings.gradle`; there is intentionally no root Gradle composite, Nx workspace, or Turborepo setup. Local Java validation requires JDK 25 because the service builds declare Java 25 toolchains.

## CI overview

GitHub Actions runs non-deployment validation in `.github/workflows/ci.yml`:

- Path check job: verifies the expected monorepo directories and project files exist.
- Client job: installs pnpm dependencies, then runs lint, unit tests, and build for `apps/client`.
- AI service job: installs Python dependencies, compiles Python files, and runs Django checks for `services/ai`.
- Java services job: uses a matrix for `api-gateway`, `node`, `roadmap`, and `user`, then runs each service Gradle test and build with Java 25.

The workflow is path-aware. Changes under root orchestration files can run all stacks, while stack-specific changes run only the matching validation jobs. It does not deploy or require secrets.
