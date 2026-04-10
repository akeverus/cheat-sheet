---
type: concept
title: "Strategy Pattern"
created: 2026-04-10
updated: 2026-04-10
tags:
  - concept
  - pattern
  - design
complexity: basic
domain: architecture
status: seed
---

# Strategy Pattern

Используется для выбора вопросов в интервью.

## Реализации

| Стратегия | Описание |
|-----------|---------|
| `DefaultSelectionStrategy` | Последовательный выбор |
| `ShuffleSelectionStrategy` | Случайный порядок |
| `WeakTopicsSelectionStrategy` | Фокус на слабых темах |

Интерфейс: `QuestionSelectionStrategy`

## Связи

- Используется в [[Interview Flow]]
- Выбирается на основе `InterviewFilter` из [[Quiz Domain]]
