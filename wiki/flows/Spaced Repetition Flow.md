---
type: concept
title: "Spaced Repetition Flow"
created: 2026-04-10
updated: 2026-04-10
tags:
  - flow
  - sm2
  - learning
status: developing
related:
  - "[[SM-2 Algorithm]]"
  - "[[Interview Flow]]"
---

# Spaced Repetition Flow

Реализация SM-2 для оптимального запоминания.

## Поток

1. Пользователь отвечает на вопрос
2. `SpacedRepetitionService` получает `ReviewState` из `ReviewStateRepository`
3. Вычисляет новый интервал по SM-2:
   - Качество ответа (0-5)
   - EaseFactor обновляется
   - Repetitions увеличивается или сбрасывается
4. `ReviewState` сохраняется с новой `nextReviewDate`
5. `DailyStreakService` обновляет серию

## Ключевые сущности

- `ReviewState` — EF, repetitions, interval, nextReviewDate
- `ReviewResult` — результат пересчёта
- `FlashcardPhase` — фаза обучения (NEW, LEARNING, REVIEW)
- `FlashcardGrade` — оценка (AGAIN, HARD, GOOD, EASY)

## Конфигурация

| Параметр | Значение |
|----------|---------|
| `app.interview.learned-repetitions` | 3 |
| `app.interview.exam-penalty` | 5 |

## Связи

- Основа — [[SM-2 Algorithm]]
- Встроен в [[Interview Flow]]
- Данные в `review_state` таблице (через [[Quiz Persistence]])
