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
status: seed
---

# Prompt Engineering

Промпты для генерации вопросов и ответов через LLM.

## Файлы промптов

`src/main/resources/prompts/general.txt` — основной промпт.

## Компоненты

- `QuestionPromptBuilder` — сборка промпта для генерации вопросов
- `QuestionPromptBuilderForOptions` — промпт для вариантов ответов
- `PromptLoader` — загрузка файлов промптов
- `AiPrompts` — шаблоны промптов

## Принцип

Prompt-first single-call: собрать полный промпт → один вызов LLM → strict JSON response → парсинг.

## Связи

- Используется в [[AI Pipeline]]
- Парсинг через `QuestionGeneratedJsonMapper`, `AiResponseParser`
