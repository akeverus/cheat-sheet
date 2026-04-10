---
type: concept
title: "AI Pipeline"
created: 2026-04-10
updated: 2026-04-10
tags:
  - flow
  - ai
  - llm
status: mature
related:
  - "[[Quiz App]]"
  - "[[Prompt Engineering]]"
---

# AI Pipeline

Генерация вопросов и вариантов ответов через LLM-провайдеры.

## Архитектура клиентов

```
AiQuestionClient (entry point)
  └── AbstractAiClient (base: retry, timeout, error handling)
        ├── ConfigurableAiClient (parametric, eliminates duplication)
        ├── OpenAiClient (GPT-4.1-mini)
        └── DeepSeekClient (deepseek-chat)
```

**AbstractAiClient** — Template Method:
- `sendChatRequest(ChatRequest, Duration)` → reactive WebClient с `.block(timeout)`
- `generateStructuredJson(prompt)` → strict JSON contract
- Error handling: WebClientResponseException (HTTP), TimeoutException, WebClientRequestException

**AiClientConfig** создаёт WebClient instances с timeout и Bearer auth. Выбор провайдера через `app.ai-provider`.

## Два пути генерации

### 1. Генерация вопроса

```
QuestionGenerationService
  → QuestionPromptBuilder (assembles prompt)
  → AiQuestionClient.generateStructuredJson()
  → QuestionGeneratedJsonMapper (strict JSON parse)
  → QuestionGeneratedMetadataSupplier (enrich metadata)
  → QuestionGeneratedSlugFactory (slug)
```

### 2. Генерация вариантов ответов

```
AIQuestionService
  → QuestionPromptBuilderForOptions (prompt)
  → AiQuestionClient (LLM call)
  → AiResponseParser (parse)
  → OptionCache (Caffeine, 500 items, 1h TTL)
```

## AI Insight Services

| Сервис | Trigger | Кэш | Описание |
|--------|---------|-----|---------|
| `HintService` | POST /api/hint | DB (3 уровня) | Генерирует все 3 уровня за 1 вызов, сохраняет в БД |
| `WrongAnswerFeedbackService` | AnswerEvent (async) | Caffeine (500, 30m) | Observer pattern: слушает AnswerEvent, pre-generates при ошибке |
| `TakeawayService` | GET /api/takeaway | DB (question.takeaway) | One-time генерация, сохраняет в колонку |
| `DiagramService` | On demand | DB (question.diagram_mermaid) | Mermaid + валидация + санитизация |

### WrongAnswerFeedbackService (детали)

- `@EventListener @Async` — слушает `AnswerEvent`
- При неправильном ответе: запускает async AI генерацию, кэширует `CompletableFuture` (key: "questionId:optionId")
- `getFeedback()` — poll cache с 15-sec timeout
- `generateComparison()` — side-by-side selected vs correct

## Конфигурация

| Параметр | Значение |
|----------|---------|
| `app.aiProvider` | `spring-ai` (default) |
| `app.aiFallbackProvider` | `spring-ai` |
| `app.ai.timeoutSeconds` | 60 |
| `app.ai.maxRetries` | 3 |
| `app.ai.maxInputLength` | 10KB |
| `app.openai.model` | gpt-4.1-mini |
| `app.deepseek.model` | deepseek-chat |
| Temperature | 0.4 |

## Preload

`PreloadService` — фоновая предзагрузка вариантов:
- Thread pool: core 4, max 8
- Batch size: 5
- Triggerится при старте сессии и после ответа

## Связи

- Вызывается из [[Interview Flow]]
- Промпты: [[Prompt Engineering]]
- Кэш: [[Caffeine]]
- Провайдеры: [[ADR-003 AI Provider Abstraction]]
