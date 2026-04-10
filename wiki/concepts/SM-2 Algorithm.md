---
type: concept
title: "SM-2 Algorithm"
created: 2026-04-10
updated: 2026-04-10
tags:
  - concept
  - algorithm
  - learning
complexity: intermediate
domain: learning
aliases:
  - SuperMemo 2
  - Spaced Repetition
status: developing
---

# SM-2 Algorithm

Алгоритм интервального повторения SuperMemo 2. Определяет оптимальные интервалы между повторениями.

## Принцип

- Каждый вопрос имеет EaseFactor (начальное: 2.5)
- При правильном ответе: интервал растёт, EF корректируется
- При неправильном: repetitions сбрасывается, интервал = 1 день
- Качество ответа (0-5) определяет корректировку EF

## Реализация

`SpacedRepetitionService` в [[Quiz App]]:
- Получает `ReviewState` (EF, repetitions, interval)
- Оценивает качество ответа
- Вычисляет новый интервал и nextReviewDate
- Сохраняет через `ReviewStateRepository`

## Связи

- Реализован в [[Spaced Repetition Flow]]
- Данные: `ReviewState`, `ReviewResult` в [[Quiz Domain]]
