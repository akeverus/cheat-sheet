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
status: mature
---

# SM-2 Algorithm

Алгоритм интервального повторения SuperMemo 2. Определяет оптимальные интервалы между повторениями.

## Константы (SpacedRepetitionService)

| Константа | Значение | Описание |
|-----------|---------|---------|
| `GRADE_CORRECT` | 5 | Идеальный ответ |
| `GRADE_WRONG` | 2 | Неправильный ответ |
| `GRADE_THRESHOLD` | 3 | Минимальный "passing" grade |
| `INITIAL_INTERVAL_DAYS` | 1 | Первый интервал |
| `SECOND_INTERVAL_DAYS` | 6 | Второй интервал |
| `EASE_BASE` | 0.1 | Базовый коэффициент EF |
| `EASE_LINEAR` | 0.08 | Линейный коэффициент |
| `EASE_QUADRATIC` | 0.02 | Квадратичный коэффициент |

## Формула Ease Factor

```
easeDelta = 0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02)
newEase = max(1.3, oldEase + easeDelta)
```

## Алгоритм `applyAnswer(ReviewState, isCorrect, grade)`

```
if grade < 3:
    repetitions = 0
    interval = 1 day        // сброс прогресса
else:
    if repetitions == 0: interval = 1 day
    if repetitions == 1: interval = 6 days
    if repetitions >= 2: interval = round(interval × ease)
    repetitions++

ease = max(1.3, ease + easeDelta)
nextReviewAt = today + interval days

if isCorrect: correctCount++
else: wrongCount++
```

## Конфигурация

| Параметр | Значение | Описание |
|----------|---------|---------|
| `app.interview.learned-repetitions` | 3 | Порог "выучено" |
| `app.interview.exam-penalty` | 5 | Штрафных вопросов при ошибке в EXAM |

## Хранение

`ReviewState` (record, immutable с @With):
- `questionId` (PK), `repetitions`, `intervalDays`, `easeFactor` (≥1.3)
- `nextReviewAt` (epoch-sec), `lastResult` (NEW/CORRECT/WRONG/RESET)
- `correctCount`, `wrongCount`

Persistence: `ReviewStateRepository` с `insertIfAbsent` (ON CONFLICT DO NOTHING).

## Связи

- Реализован в `SpacedRepetitionService` → [[Quiz App]]
- Данные: `ReviewState` в [[Quiz Domain]]
- Хранение: `review_state` таблица в [[Database Schema]]
- Используется в [[Interview Flow]] при каждом ответе
