# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Run the application (no API keys needed — see "Источник вопросов" below)
docker compose up -d postgres
./gradlew bootRun

# Build without tests
./gradlew build -x test

# Run all tests
./gradlew test

# Run a single test class
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.SpacedRepetitionServiceTest"

# Run tests with coverage check
./gradlew check

# Generate coverage report
./gradlew jacocoTestReport
```

App runs at http://localhost:8080. Swagger UI at http://localhost:8080/swagger-ui.html.

## Module Structure

Three Gradle modules:
- **`quiz-domain`** — pure domain model (`InterviewQuestion`, `InterviewSession`, `QuestionOption`, etc.). No Spring, no infrastructure dependencies.
- **`quiz-persistence`** — JDBC repositories + Flyway migrations (`src/main/resources/db/migration/`). **PostgreSQL only** (since 2026-05-25); локально стартуется через `docker compose up -d postgres`.
- **`quiz-app`** — Spring Boot application: controllers, services, config.

Тесты обоих модулей крутятся на **Testcontainers PostgreSQL 16-alpine** (нужен Docker daemon). `quiz-persistence` тесты наследуются от `AbstractPostgresRepositoryTest` — один контейнер на JVM, TRUNCATE между тестами. `quiz-app` тесты подхватывают `jdbc:tc:postgresql:16-alpine:///` из `application-test.yml`.

## Architecture & Layer Rules

Enforced by ArchUnit (`LayeredArchitectureTest`):
- `domain` → no dependencies on api/service/persistence/config
- `service` → no dependencies on controllers or security classes
- `persistence` → no dependencies on api layer

Package layout in `quiz-app`:
```
com.cheatsheet.quiz
  feature/           # vertical slices: interview/, export/, admin/, auth/
    interview/       # controller/, dto/, mapper/, service/, usecase/
  service/           # cross-cutting services: admin/, cache/, diagram/, event/, imports/, strategy/
  api/               # REST API wiring: controller/, dto/, mapper/, security/, validation/, constants/
  common/            # exceptions, shared constants
  config/            # Spring configuration: app/, web/, runtime/
  infrastructure/    # bootstrap/, health/, render/, search/
```

## Источник вопросов (AI в проекте нет)

AI/LLM-код вырезан целиком: нет пакета `llm/`, нет `service/ai/`, нет `AIQuestionService`,
`AiQuestionClient`, `QuestionPromptBuilder`, каталога `prompts/`. **Нет и флага, которым
это можно было бы включить** — ни `app.ai.*`, ни `AI_FALLBACK_ENABLED`, ни `app.aiProvider`
в конфигурации не существует. Отсутствие генерации — свойство кода, а не настройки.
API-ключи не нужны ни для чего.

- Текст вопросов импортируется из `cheatsheets/interview/**/*.md` (`service/imports/`).
- MCQ-варианты грузятся из `seed/mcq/<category>/<topic>.json` через `McqJsonLoader` на старте.
- Для проверки и улучшения MCQ используй `/mcq-quality-fixer`: критерии в
  `docs/mcq-quality-standard.md`, команды и evidence в `docs/mcq-quality/workflow.md`.
  Полный проход ведётся по `PLAN_INTERVIEW.md` и `PROMPT_PLAN_INTERVIEW.md`;
  `scripts/mcq-workflow.py next` показывает актуальный следующий пакет.
- При проверке MCQ действуй как экзаменатор по
  `docs/mcq-quality/examiner-review.md`: все A/B/C/D должны полно отвечать на видимый
  вопрос, включая wrong. Нужен содержательный LLM-проход каждого Q/B; готовые файлы
  тоже включены в запрошенный повторный аудит. Schema/runtime PASS не заменяет его.
- `OptionLookupService` ходит только в БД/кэш. Нет сидера → пустой список → MVC форсит
  `flashcardMode=true`, вопрос показывается флешкартой. Это штатное поведение, не деградация.

## Key Configuration (`app.*` prefix in `application.yml`)

| Property | Default |
|----------|---------|
| `app.security.mode` | `local` (одиночный режим; `multi` включает аутентификацию) |
| `app.admin-token` | пусто → `/export` и `/settings/reset-options` закрыты |
| `app.sensitive-rate-limit-per-minute` | `20` |
| `app.scheduling.algorithm` | `sm2` (альтернатива — `fsrs`) |
| `app.interview.default-exam-count` | `20` |
| `app.interview.max-session-count` | `200` |
| `app.cache.option-max-size` / `ttl-hours` | `500` / `1` |
| `app.interview-reset-on-startup` | `false` (TRUNCATE + переимпорт из md/JSON) |

Production profile (`prod`): disables Swagger UI. PostgreSQL is the **default** (base `application.yml`) datastore — there is no separate `postgres` profile. Tests run under the `test` profile (Testcontainers).

## Testing Conventions

- Unit tests alongside their classes (standard Spring Boot test layout)
- `TestQuestionBuilder` — builder for domain test fixtures
- `TestInterviewPath` — helper for resolving test markdown question files
- Integration tests use Testcontainers for PostgreSQL (`PostgresIntegrationTest`)
- Architecture rules in `architecture/LayeredArchitectureTest`

## Proactive Code Edits

When a request is clear, proceed directly with changes — no confirmation needed before edits. Ask only when requirements are ambiguous or the action is destructive/irreversible.

## Subagent Permissions

When spawning subagents (Agent tool) for tasks in this project, always use `dangerouslyDisableSandbox: true` in Bash tool calls within subagents, or pass `--dangerously-skip-permissions` when launching Claude Code as a subprocess. This avoids permission prompts blocking autonomous batch operations like updating cheatsheets.

## graphify (ОБЯЗАТЕЛЬНО — НЕ ОБХОДИТЬ)

В проекте есть граф знаний `graphify-out/`. Граф — **первичный источник** для ответов про код, архитектуру, связи и навигацию. Эти правила имеют приоритет над любыми «быстрыми» подходами вроде сразу-grep.

### 1. Состояние графа — проверять перед использованием

В начале сессии (или перед первым кодовым вопросом) выполнить:

```bash
ls graphify-out/graph.json graphify-out/GRAPH_REPORT.md 2>/dev/null
```

- **Если оба файла существуют** → граф готов, можно использовать.
- **Если хотя бы одного нет** (только `.graphify_*` чанки/кэш — это незавершённый build) → ОБЯЗАТЕЛЬНО достроить:
  ```bash
  /graphify .
  ```
  И только после успешного завершения отвечать на содержательные вопросы про код. Сообщить пользователю одной строкой, что граф достраивается.

### 2. Поиск и навигация — граф ПЕРЕД raw-файлами

При вопросе про код/архитектуру/связи действовать в этом порядке (НЕ перепрыгивать шаги):

1. **`graphify-out/GRAPH_REPORT.md`** — god-nodes, communities, общая карта. Читать первым.
2. **`graphify-out/wiki/index.md`** (если есть) — навигация по сообществам. Читать вместо `Glob`/`Grep` по сырым файлам.
3. **`/graphify query "<вопрос>"`** — для архитектурных/cross-file вопросов («где используется X», «как связаны A и B», «что вызывает Y»).
4. **`/graphify path "A" "B"`** — кратчайший путь между сущностями.
5. **`/graphify explain "<node>"`** — объяснение конкретного узла.
6. Только если граф не дал ответа — переходить к `Grep`/`Read` по сырым файлам.

Хук `PreToolUse` на `Glob|Grep` напомнит про граф — это не шум, а сигнал «сначала граф».

### 3. Обновление графа — после изменений кода

После любых правок исходников в сессии (до завершения ответа пользователю) — инкрементальный rebuild:

```bash
$(cat graphify-out/.graphify_python) -c "from graphify.watch import _rebuild_code; from pathlib import Path; _rebuild_code(Path('.'))"
```

Если правок много или менялась структура каталогов — полный апдейт:

```bash
/graphify . --update
```

Не оставлять граф устаревшим к концу сессии.

### 4. Что НЕЛЬЗЯ делать

- НЕ начинать массовый `Grep`/`Glob` по проекту, пока не прочитан `GRAPH_REPORT.md`.
- НЕ отвечать «не знаю где это» без `/graphify query`.
- НЕ удалять `graphify-out/` без явной просьбы пользователя.
- НЕ игнорировать предупреждение хука про граф.

### 5. Когда граф НЕ нужен

Тривиальные правки в одном уже открытом файле, чисто косметические изменения (форматирование, опечатки в строке), вопросы не про код (про DevOps, инфру, шпаргалки в `cheatsheets/`). В этих случаях граф можно не трогать.

### 6. Текущее состояние графа (AST-only, обновлено 2026-04-27)

Граф построен **БЕЗ semantic-LLM** — только AST-извлечение по `modules/`. Это означает:

- **Scope:** только `modules/` (~328 java/kotlin/sql/yaml файлов). `cheatsheets/`, `scripts/`, корневые gradle-файлы и `prompts/` в графе НЕТ.
- **Что есть:** структурные рёбра (`extends`, `implements`, `calls`, `contains method`, `imports`), god-nodes, communities (321 шт.), token-benchmark (71.7×).
- **Чего НЕТ:** semantic-связи между классами (`semantically_similar_to`, `rationale_for`, `shares_data_with`), кросс-файловые «surprising connections», связи через docs/markdown.

**Особенность поиска:** граф богат тестовыми методами (`*Test.someTest()`), и наивный поиск `term in label` часто вытаскивает тесты вместо production-классов. При запросах:

- Сначала фильтровать по `source_file` (исключать `src/test/`) или искать по точному `id` узла, а не по label.
- При сомнениях смотреть `GRAPH_REPORT.md` → секция God Nodes — там сразу production-классы.
- `/graphify path "ClassA" "ClassB"` работает надёжнее, чем `query` для конкретных пар.

**Как обогатить графа semantic-связями (когда LLM разрешён):**

```bash
/graphify modules --update --mode deep
```

Это запустит параллельные subagent-ы Claude для извлечения cross-file семантики и заполнит пробелы. AST-кэш переиспользуется — заново парсить не будет.
