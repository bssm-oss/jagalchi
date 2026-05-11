set shell := ["bash", "-euo", "pipefail", "-c"]

java_services := "api-gateway node roadmap user"

path-check:
    test -d apps/client
    test -f apps/client/package.json
    test -f apps/client/pnpm-lock.yaml
    test -d services/ai
    test -f services/ai/requirements.txt
    test -f services/ai/manage.py
    for service in {{java_services}}; do \
      test -d "services/$service"; \
      test -f "services/$service/gradlew"; \
      test -f "services/$service/build.gradle"; \
      test -f "services/$service/settings.gradle"; \
    done

client-install:
    pnpm --dir apps/client install --frozen-lockfile

client-lint:
    pnpm --dir apps/client lint

client-test:
    pnpm --dir apps/client test:run

client-build:
    pnpm --dir apps/client build

ai-install:
    cd services/ai && python -m venv .venv
    cd services/ai && . .venv/bin/activate && python -m pip install --upgrade pip
    cd services/ai && . .venv/bin/activate && python -m pip install -r requirements.txt

ai-compile:
    cd services/ai && . .venv/bin/activate && python -m compileall manage.py jagalchi_ai

ai-check:
    cd services/ai && . .venv/bin/activate && python manage.py check

api-gateway-test:
    cd services/api-gateway && ./gradlew test

api-gateway-build:
    cd services/api-gateway && ./gradlew build

node-test:
    cd services/node && ./gradlew test

node-build:
    cd services/node && ./gradlew build

roadmap-test:
    cd services/roadmap && ./gradlew test

roadmap-build:
    cd services/roadmap && ./gradlew build

user-test:
    cd services/user && ./gradlew test

user-build:
    cd services/user && ./gradlew build

java-test: api-gateway-test node-test roadmap-test user-test

java-build: api-gateway-build node-build roadmap-build user-build

validate: path-check client-lint client-test client-build ai-compile ai-check java-test java-build
