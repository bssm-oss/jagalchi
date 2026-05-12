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

## Local setup

Recommended local prerequisites:

- `direnv` for project-scoped toolchain environment
- Node.js 25 and pnpm 10.33.2
- Python 3.12 for `services/ai`
- JDK 25 for Java services
- MySQL client development files for Python `mysqlclient`
- `just` for root validation commands

On macOS with Homebrew, the local setup used for this repository is:

```sh
brew install direnv openjdk python@3.12 mysql-client pkg-config just pnpm
```

Create a local `.envrc` in the repository root. This file is intentionally ignored by git:

```sh
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="/opt/homebrew/opt/python@3.12/libexec/bin:$JAVA_HOME/bin:/opt/homebrew/opt/mysql-client/bin:$PATH"
export PKG_CONFIG_PATH="/opt/homebrew/opt/mysql-client/lib/pkgconfig:${PKG_CONFIG_PATH:-}"
```

Then allow it once:

```sh
direnv allow
```

## Local validation from the root

The root `justfile` delegates into the existing project directories. Install dependencies before running full validation:

```sh
just client-install
just ai-install
just validate
```

Run narrower checks when you only need one stack:

```sh
just path-check
just client-lint
just client-test
just client-build
just ai-compile
just ai-check
just java-test
just java-build
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

## GitHub workflow

Use issues before implementation. Each issue should define purpose, scope, affected area, API or policy changes when relevant, edge cases, and definition of done. The repository provides issue forms under `.github/ISSUE_TEMPLATE` for bug reports, feature requests, and maintenance/docs tasks.

Branch names should stay issue-scoped. Existing project conventions use forms such as:

```text
feat/#12-user-login
feature/220/로드맵-생성
bugfix/305/트리조회-정렬버그
```

Commit messages should follow Conventional Commits where practical:

```text
feat(scope): add behavior refs #123
fix(scope): 버그 수정 refs #123
```

Pull requests should link the issue, summarize the implementation scope, list affected stacks, include the test plan, and call out API/data/policy changes or breaking changes. The default PR template lives at `.github/pull_request_template.md`.

## CI overview

GitHub Actions runs non-deployment validation in `.github/workflows/ci.yml`:

- Path check job: verifies the expected monorepo directories and project files exist.
- Client job: installs pnpm dependencies, then runs lint, unit tests, and build for `apps/client`.
- AI service job: installs Python dependencies, compiles Python files, and runs Django checks for `services/ai`.
- Java services job: uses a matrix for `api-gateway`, `node`, `roadmap`, and `user`, then runs each service Gradle test and build with Java 25.

The workflow is path-aware. Changes under root orchestration files can run all stacks, while stack-specific changes run only the matching validation jobs. It does not deploy or require secrets.

Additional repository hygiene runs in `.github/workflows/actionlint.yml`, which validates GitHub Actions workflow syntax when workflow files change.

## Optional GitHub settings

Recommended repository settings once the team is ready:

- Protect `main` and require the CI workflow before merge.
- Require at least one approving review for application changes.
- Keep branch deletion after merge enabled if the team prefers short-lived issue branches.
- Consider CodeQL and Dependency Review only if the repository plan supports GitHub code security features for private repositories.
