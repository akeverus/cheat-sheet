# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Run the application (requires API key env var)
export DEEPSEEK_API_KEY=your_key   # or OPENAI_API_KEY / SPRING_AI_API_KEY
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
- **`quiz-persistence`** — JDBC repositories, Flyway migrations. Supports SQLite (default) and PostgreSQL (`--spring.profiles.active=postgres`).
- **`quiz-app`** — Spring Boot application: controllers, services, AI integration, config.

## Architecture & Layer Rules

Enforced by ArchUnit (`LayeredArchitectureTest`):
- `domain` → no dependencies on api/service/persistence/config
- `service` → no dependencies on controllers or security classes
- `persistence` → no dependencies on api layer

Package layout in `quiz-app`:
```
com.cheatsheet.quiz
  feature/           # vertical slices: interview/, question/, export/, admin/
    interview/       # controller/, dto/, mapper/, service/, usecase/
    question/        # engine/ (prompt/, model/, service/, mapper/, metadata/)
  service/           # cross-cutting services: ai/, cache/, diagram/, imports/, strategy/
  api/               # REST API wiring
  config/            # Spring configuration
  infrastructure/    # low-level infra
  llm/               # LLM abstractions
```

## AI / LLM Pipeline

- Three supported providers: `deepseek`, `openai`, `spring-ai` (configured via `app.aiProvider`)
- `AiQuestionClient` is the entry point; `AbstractAiClient` handles retry logic (429/5xx, up to `app.ai.maxRetries`)
- Question generation uses a **prompt-first single-call** approach: `QuestionPromptBuilder` assembles the prompt → AI returns strict JSON → `QuestionGeneratedJsonMapper` parses it
- `AIQuestionService` generates multiple-choice options; `QuestionPromptBuilderForOptions` builds the prompt for that path
- Prompts live in `src/main/resources/prompts/` (consolidated into `general.txt`)
- `AdaptiveDifficultyService` adjusts question difficulty based on session performance

## Key Configuration (`app.*` prefix in `application.yml`)

| Property | Default |
|----------|---------|
| `app.aiProvider` | `spring-ai` |
| `app.aiFallbackProvider` | `spring-ai` |
| `app.interview.optionsCount` | `4` |
| `app.ai.timeoutSeconds` | `30` |
| `app.ai.maxRetries` | `3` |
| `app.interview.resetOnStartup` | `false` (dev-only) |

Production profile (`prod`): disables Swagger UI. PostgreSQL profile: `postgres`.

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
