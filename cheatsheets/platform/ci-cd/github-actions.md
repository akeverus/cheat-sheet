---
title: "GitHub Actions"
description: "GitHub Actions — встроенная CI/CD платформа GitHub: workflows, jobs, steps, runners, matrix-builds, reusable workflows, OIDC-деплой, кэширование, артефакты и security best practices."
tags:
  - platform
  - ci-cd
  - github-actions
difficulty: "intermediate"
updated: "2026-04-20"
---
# GitHub Actions

GitHub Actions — нативная CI/CD платформа внутри GitHub. Workflow описывается в YAML в `.github/workflows/*.yml`, запускается на event-триггерах (push, PR, cron, release, workflow_dispatch). Это самая распространённая CI для open-source и стартапов: бесплатные runners на публичных репо, огромный marketplace готовых actions, простая модель.

В этом документе: структура workflow, все ключевые триггеры, matrix/reusable-паттерны, кэширование, артефакты, secrets, OIDC-auth в облака, мониторинг и безопасность.

## Полезные ссылки

### Официальная документация
- [GitHub Actions docs](https://docs.github.com/en/actions)
- [Workflow syntax reference](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)
- [Events that trigger workflows](https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows)
- [Marketplace](https://github.com/marketplace?type=actions)
- [Security hardening](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)

### Полезные actions
- [actions/checkout](https://github.com/actions/checkout)
- [actions/setup-java](https://github.com/actions/setup-java) / [setup-node](https://github.com/actions/setup-node) / [setup-python](https://github.com/actions/setup-python)
- [actions/cache](https://github.com/actions/cache)
- [actions/upload-artifact](https://github.com/actions/upload-artifact) / [download-artifact](https://github.com/actions/download-artifact)
- [docker/build-push-action](https://github.com/docker/build-push-action)
- [aws-actions/configure-aws-credentials](https://github.com/aws-actions/configure-aws-credentials) — OIDC federated auth

### Соседние разделы
- [[README|CI/CD]]
- [[jenkins]], [[gitlab-ci|GitLab CI]], [[circleci]], [[azure-devops|Azure DevOps]], [[travis-ci|Travis CI]], [[tekton]]
- [Docker](../containers/docker/)
- [Kubernetes](../containers/kubernetes/)
- [[secrets-management|Secrets Management]]

## Содержание

- [Базовая структура workflow](#базовая-структура-workflow)
- [Триггеры (on)](#триггеры-on)
- [Jobs и зависимости](#jobs-и-зависимости)
- [Runners](#runners)
- [Matrix builds](#matrix-builds)
- [Переменные окружения и контексты](#переменные-окружения-и-контексты)
- [Secrets и переменные](#secrets-и-переменные)
- [Кэширование](#кэширование)
- [Артефакты](#артефакты)
- [Reusable workflows и composite actions](#reusable-workflows-и-composite-actions)
- [Деплой и OIDC в облака](#деплой-и-oidc-в-облака)
- [Сборка и публикация Docker-образов](#сборка-и-публикация-docker-образов)
- [Security hardening](#security-hardening)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [См. также](#см-также)

## Базовая структура workflow

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    name: Build & Test
    runs-on: ubuntu-latest
    timeout-minutes: 15

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: gradle

      - name: Build
        run: ./gradlew build --no-daemon

      - name: Upload test report
        if: failure()
        uses: actions/upload-artifact@v4
        with:
          name: test-report
          path: '**/build/reports/tests/**'
```

**Иерархия:** `workflow jobs steps`. Workflow — файл. Job — группа steps, запускается на одном runner. Step — одна команда или вызов action.

## Триггеры (on)

| Событие | Когда срабатывает | Частый use-case |
|---------|-------------------|-----------------|
| `push` | после пуша | CI на main |
| `pull_request` | открытие/обновление PR | CI на PR |
| `pull_request_target` | PR, но в контексте base | безопасный деплой preview |
| `schedule` | cron | еженочные билды, security-сканы |
| `workflow_dispatch` | ручной запуск | деплой кнопкой |
| `workflow_call` | вызов из другого workflow | reusable |
| `release` | создание релиза | публикация |
| `issue_comment` | комментарий в issue/PR | `/deploy` бот-команды |
| `repository_dispatch` | внешний webhook | trigger из другой системы |

```yaml
on:
  # Фильтры по веткам и путям
  push:
    branches: [main]
    paths-ignore: ['docs/**', '*.md']

  # Ручной запуск с параметрами
  workflow_dispatch:
    inputs:
      environment:
        type: choice
        options: [staging, production]
        required: true

  # Каждый день в 03:00 UTC
  schedule:
    - cron: '0 3 * * *'
```

## Jobs и зависимости

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps: [...]

  test:
    needs: build
    runs-on: ubuntu-latest
    steps: [...]

  deploy-staging:
    needs: [build, test]
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    environment: staging    # требует approval в настройках окружения
    steps: [...]
```

**Условное выполнение (`if`):**

```yaml
if: github.event_name == 'push' && github.ref == 'refs/heads/main'
if: github.actor != 'dependabot[bot]'
if: contains(github.event.pull_request.labels.*.name, 'deploy')
if: startsWith(github.event.head_commit.message, 'release:')
```

**Параллельные jobs по умолчанию.** Для последовательных — `needs`.

## Runners

| Runner | Когда |
|--------|-------|
| `ubuntu-latest` | дефолт, быстрее всех обновляется |
| `ubuntu-22.04`, `ubuntu-20.04` | фиксированная версия |
| `macos-latest` | iOS/macOS сборки (дорого, x10) |
| `windows-latest` | Windows |
| Self-hosted | корп. инфра, особое железо, приватная сеть |

**Self-hosted runners** — разверните за firewall, подключайте через `runs-on: [self-hosted, linux, gpu]`. Для K8s — [Actions Runner Controller (ARC)](https://github.com/actions/actions-runner-controller) с автоскейлингом.

Опасайтесь self-hosted на public репо — PR из форка может выполнить произвольный код.

## Matrix builds

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      fail-fast: false
      matrix:
        java: [17, 21]
        os: [ubuntu-latest, macos-latest]
        include:
          - java: 25
            os: ubuntu-latest
            experimental: true
        exclude:
          - java: 17
            os: macos-latest
    continue-on-error: ${{ matrix.experimental == true }}
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '${{ matrix.java }}', distribution: temurin }
      - run: ./gradlew test
```

`fail-fast: false` — не прерывать другие комбинации при падении одной.

## Переменные окружения и контексты

**Контексты:** `github`, `env`, `vars`, `secrets`, `job`, `steps`, `runner`, `needs`, `matrix`, `strategy`, `inputs`.

```yaml
env:
  GLOBAL_VAR: value

jobs:
  build:
    env:
      JOB_VAR: value
    steps:
      - name: Print
        env:
          STEP_VAR: value
        run: |
          echo "commit=${{ github.sha }}"
          echo "repo=${{ github.repository }}"
          echo "actor=${{ github.actor }}"
          echo "ref=${{ github.ref_name }}"

      # Запись в output для других steps/jobs
      - id: version
        run: echo "tag=v1.2.3" >> $GITHUB_OUTPUT
      - run: echo "${{ steps.version.outputs.tag }}"
```

## Secrets и переменные

| Scope | Типы |
|-------|------|
| Repository | `secrets`, `vars` |
| Organization | `secrets`, `vars` (с selective access) |
| Environment | `secrets`, `vars` (с approval-gate) |

```yaml
jobs:
  deploy:
    environment: production  # требует approval
    steps:
      - run: ./deploy.sh
        env:
          API_KEY: ${{ secrets.PROD_API_KEY }}
          REGION: ${{ vars.AWS_REGION }}
```

**Правила:**
- Secrets маскируются в логах, но `echo`-ом можно утечь через Base64/hex.
- `GITHUB_TOKEN` — автогенерируемый токен с правами по умолчанию на текущий репо. Ограничивайте: `permissions: read-all` на уровне workflow.
- Для cross-repo используйте PAT с fine-grained правами или GitHub App.
- НЕ используйте `pull_request_target` с чужим кодом + secrets — инъекция кода.

## Кэширование

Каждый setup-action умеет кэшировать (pass `cache: gradle|maven|pip|npm`). Для кастомного кэша:

```yaml
- uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle*', 'gradle/wrapper/gradle-wrapper.properties') }}
    restore-keys: |
      gradle-${{ runner.os }}-
```

**Лимит:** 10 GB на репо, LRU. Cache scoped по ветке; PR читает кэш base ветки.

## Артефакты

```yaml
- uses: actions/upload-artifact@v4
  with:
    name: build-output
    path: build/libs/*.jar
    retention-days: 14

# в другом job
- uses: actions/download-artifact@v4
  with:
    name: build-output
```

Артефакты отличаются от кэша: долговременное хранение, доступ после завершения run.

## Reusable workflows и composite actions

**Reusable workflow (callable):**

```yaml
# .github/workflows/build.yml
on:
  workflow_call:
    inputs:
      java-version:
        type: string
        default: '21'
    secrets:
      SONAR_TOKEN:
        required: true
jobs:
  build:
    runs-on: ubuntu-latest
    steps: [...]
```

```yaml
# .github/workflows/ci.yml
jobs:
  call-build:
    uses: ./.github/workflows/build.yml
    with:
      java-version: '21'
    secrets:
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

**Composite action (`action.yml`):**

```yaml
# .github/actions/gradle-build/action.yml
name: Gradle Build
inputs:
  task:
    required: true
runs:
  using: composite
  steps:
    - run: ./gradlew ${{ inputs.task }}
      shell: bash
```

Используется: `uses: ./.github/actions/gradle-build`.

## Деплой и OIDC в облака

**OIDC** — предпочтительный способ auth в AWS/GCP/Azure/HCP Vault. Не нужно хранить долгоживущие ключи.

```yaml
permissions:
  id-token: write   # для OIDC
  contents: read

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: aws-actions/configure-aws-credentials@v4
        with:
          role-to-assume: arn:aws:iam::123456789:role/github-actions
          aws-region: eu-central-1

      - run: aws s3 sync ./dist s3://my-bucket/
```

На стороне AWS — `AssumeRoleWithWebIdentity` с IAM-trust-policy, где `sub` проверяет repo/branch.

## Сборка и публикация Docker-образов

```yaml
jobs:
  docker:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write    # для ghcr.io
    steps:
      - uses: actions/checkout@v4

      - uses: docker/setup-buildx-action@v3

      - uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - uses: docker/metadata-action@v5
        id: meta
        with:
          images: ghcr.io/${{ github.repository }}
          tags: |
            type=sha
            type=semver,pattern={{version}}
            type=raw,value=latest,enable={{is_default_branch}}

      - uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
          platforms: linux/amd64,linux/arm64
```

## Security hardening

- **Pin actions по SHA**, а не по тегу: `uses: actions/checkout@b4ffde65f46336ab88eb53be808477a3936bae11 # v4.1.1`. Тег может быть переведён на вредоносный commit.
- **Минимальные permissions** на уровне workflow:
  ```yaml
  permissions:
    contents: read
  ```
- **Не доверяйте PR из форков** — `pull_request` по умолчанию не получает secrets. Используйте `pull_request_target` только для безопасных задач (labels, комментарии), НЕ для билда кода из форка.
- **Dependabot** для обновления actions и зависимостей.
- **CodeQL** / **Trivy** сканы в CI.
- **gitleaks** / **trufflehog** для секретов.
- **Protected branches** + required status checks.
- **Environment secrets** + reviewers для prod-деплоя.
- Аудит через `actions/runs/<id>` API.

## Best practices

- **Fast feedback:** lint/compile first, tests потом, heavy-сборка в конце.
- **Fail fast в PR, без fail fast в matrix-night-run** (full coverage).
- **Timeout** на каждом job (`timeout-minutes`).
- **Concurrency group** чтобы не гонять лишнее:
  ```yaml
  concurrency:
    group: ${{ github.workflow }}-${{ github.ref }}
    cancel-in-progress: true
  ```
- **Reusable workflows** для DRY между репо.
- **Self-hosted runners** в K8s через ARC с автоскейлингом.
- **Environment protection rules**: required reviewers, wait timer, deployment branches.
- **Release Drafter** / **Release Please** для автогенерации changelog.
- **act** ([nektos/act](https://github.com/nektos/act)) для локального запуска workflow без push.

## Troubleshooting

| Симптом | Диагностика | Фикс |
|---------|-------------|------|
| Job не стартует | Проверить триггер и фильтры | `paths`/`branches` могут исключать |
| `Permission denied` при push | GITHUB_TOKEN не имеет прав | `permissions: contents: write` |
| Кэш не попадает | Разные ключи на разных ветках | `restore-keys` с префиксом |
| Утечка secret в логе | Был ли echo/base64/env-dump | re-rotate secret, audit logs |
| Matrix-job висит | `fail-fast` или long-running step | `timeout-minutes` |
| Self-hosted runner не регистрируется | Firewall, proxy | check `--url` и network из pod |
| Docker push ошибка `unauthorized` | `packages: write` отсутствует | перечислить в permissions |
| `Resource not accessible` в API | GITHUB_TOKEN scope | PAT или GitHub App |
| Workflow не запускается на fork PR | Требуется approve от maintainer | Settings Actions Fork PR approvals |

## См. также

- [[azure-devops|Azure DevOps]]
- [[circleci|CircleCI]]
- [[gitlab-ci|GitLab CI/CD]]
- [[jenkins|Jenkins]]
- [[tekton|Tekton]]
