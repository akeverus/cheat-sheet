---
type: concept
title: "AI Pipeline"
created: 2026-04-10
updated: 2026-04-10
tags:
  - flow
  - ai
  - llm
status: developing
related:
  - "[[Quiz App]]"
  - "[[Prompt Engineering]]"
---

# AI Pipeline

Генерация вопросов и вариантов ответов через LLM-провайдеры.

## Архитектура

```
AiQuestionClient (entry point)
  └── AbstractAiClient (retry: 429/5xx, до maxRetries)
        ├── OpenAiClient (GPT-4.1-mini)
        └── DeepSeekClient (deepseek-chat)
```

## Два пути генерации

### 1. Генерация вопроса
```
QuestionGenerationService
  → QuestionPromptBuilder (assembles prompt)
  → AiQuestionClient (single LLM call)
  → QuestionGeneratedJsonMapper (strict JSON parse)
  → QuestionGeneratedMetadataSupplier (enrich metadata)
```

### 2. Генерация вариантов ответов
```
AIQuestionService
  → QuestionPromptBuilderForOptions (prompt for options)
  → AiQuestionClient (LLM call)
  → AiResponseParser (parse response)
  → OptionCache (Caffeine, 500 items, 1h TTL)
```

## Конфигурация

| Параметр | Значение |
|----------|---------|
| `app.aiProvider` | `spring-ai` (default) |
| `app.aiFallbackProvider` | `spring-ai` |
| `app.ai.timeoutSeconds` | 30 |
| `app.ai.maxRetries` | 3 |

## Промпты

Файлы в `src/main/resources/prompts/` (основной: `general.txt`). Загружаются через `PromptLoader`.

## Связи

- Вызывается из [[Interview Flow]] при генерации ответов
- Использует [[Prompt Engineering]] для качества генерации
- Кэширует результаты в [[Caffeine]]
