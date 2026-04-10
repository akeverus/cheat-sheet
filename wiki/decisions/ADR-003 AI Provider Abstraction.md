---
type: decision
title: "ADR-003 AI Provider Abstraction"
created: 2026-04-10
updated: 2026-04-10
tags:
  - decision
  - ai
  - architecture
status: active
---

# ADR-003: AI Provider Abstraction

## Решение

Абстракция `AbstractAiClient` с реализациями `OpenAiClient` и `DeepSeekClient`. Провайдер выбирается через `app.aiProvider`.

## Контекст

Нужна гибкость: DeepSeek дешевле для разработки, OpenAI качественнее для продакшена. Оба совместимы с OpenAI API.

## Последствия

- Template Method: `AbstractAiClient` с retry-логикой (429/5xx)
- Fallback provider через `app.aiFallbackProvider`
- Spring AI для OpenAI-совместимых API
- `ConfigurableAiClient` для runtime-выбора
