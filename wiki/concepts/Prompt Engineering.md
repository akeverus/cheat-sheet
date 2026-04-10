---
type: concept
title: "Prompt Engineering"
created: 2026-04-10
updated: 2026-04-10
tags:
  - concept
  - ai
  - prompts
complexity: intermediate
domain: ai
status: mature
---

# Prompt Engineering

Промпты для генерации вопросов и ответов через LLM.

## Основной промпт (general.txt)

14-шаговая методология:

1. **Цель** — оценивать reasoning, не запоминание
2. **Signal requirements** — внутренние механизмы, runtime behaviour, trade-offs, failure modes, edge cases
3. **Уровни сложности**:
   - EASY — фундаментальные концепции
   - MEDIUM — распространённые заблуждения
   - HARD — edge cases, concurrency
4. **Конструкция вариантов** — ровно 4, 1 правильный, single-sentence, сбалансированная глубина
5. **Distractor patterns** — common misconception, partially correct reasoning, mechanism confusion
6. **Anti-patterns** — запрещены: version-specific trivia, vendor quirks, undocumented behaviour
7. **JSON schema** — два формата (legacy single + multi-question с difficulty)
8. **Язык** — русский
9. **Валидация** — единственный правильный ответ, ни один вариант визуально не выделяется

## Компоненты

| Класс | Назначение |
|-------|-----------|
| `QuestionPromptBuilder` | Сборка промпта для генерации вопросов |
| `QuestionPromptBuilderForOptions` | Промпт для вариантов ответов |
| `PromptLoader` | Загрузка файлов промптов из classpath |
| `AiPrompts` | System prompt и шаблоны |
| `LlmRequestBuilder` | `withStrictJsonContract(prompt)` — обёртка с JSON schema |
| `LlmResponseParser` | `extractContent()` → choices[0].message.content |

## Принцип

**Prompt-first single-call**: собрать полный промпт → один вызов LLM → strict JSON → парсинг. Без multi-turn.

## Связи

- Файлы: `src/main/resources/prompts/general.txt`
- Используется в [[AI Pipeline]]
- Парсинг через `QuestionGeneratedJsonMapper`, `AiResponseParser`
