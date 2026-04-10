---
type: concept
title: "Interview Flow"
created: 2026-04-10
updated: 2026-04-10
tags:
  - flow
  - interview
  - core
status: mature
related:
  - "[[Quiz App]]"
  - "[[Spaced Repetition Flow]]"
  - "[[AI Pipeline]]"
  - "[[API Endpoints]]"
---

# Interview Flow

Основной поток от старта сессии до завершения.

## Архитектура сервисов

```
InterviewApiController / InterviewMvcController
  └── Usecase Services (AnswerApiService, NextQuestionApiService, ...)
        └── InterviewFacade (агрегирует 7+ сервисов)
              ├── InterviewService (core logic)
              ├── TrainingSessionService (lifecycle)
              ├── ReviewService / SpacedRepetitionService
              ├── HintService, TakeawayService, WrongAnswerFeedbackService
              ├── CodeTraceService, RelatedQuestionsService
              └── MarkdownRenderService
```

## 1. Старт сессии

`TrainingSessionService.startSession(mode, count, filter)`:

1. Выбор вопросов в зависимости от фильтров:
   - **Shuffled** → `findShuffledQuestionIds()` (ORDER BY RANDOM)
   - **Topic-specific** → `findDueQuestions()` по теме + dueDate
   - **Group-based** → topics для группы, приоритет по mastery (слабые темы первыми)
2. Создание `InterviewSession` с mode, questionIds, filter
3. Trigger `PreloadService` для фоновой генерации ответов

## 2. Выбор вопроса

`InterviewService.nextQuestion(filter, weakTopicsPriority, excludeId)`:

- [[Strategy Pattern]]:
  - `DefaultSelectionStrategy` — последовательно
  - `ShuffleSelectionStrategy` — случайный порядок
  - `WeakTopicsSelectionStrategy` — фокус на слабых темах
- Проверяет PreloadService cache → затем БД

## 3. Ответ

`InterviewService.submitAnswer(questionId, optionId, filter, confidence)`:

1. Валидация вопроса и варианта
2. `ReviewState` update через [[SM-2 Algorithm]]
3. Publish `AnswerEvent` (слушает WrongAnswerFeedbackService)
4. Trigger preload следующего вопроса
5. Return: correct answer, explanations, SM-2 repetitions, session progress

## 4. AI Feedback (lazy)

На странице результата — 4 параллельных async-запроса:
- Wrong feedback (pre-generated через AnswerEvent)
- Comparison table (on-demand)
- Takeaway (cached in DB)
- Code trace (on-demand)

## 5. EXAM Mode Penalty

`TrainingSessionService.addExamPenaltyQuestions()`:
- При ошибке в EXAM → добавляет `app.interview.examPenaltyQuestions` (default 5) дополнительных вопросов
- Исключает уже отвеченные

## 6. Завершение

`SessionSummaryService` → итоги: correct/wrong, accuracy, topics covered.

## Режимы

| Режим | Описание | Penalty |
|-------|---------|---------|
| TRAINING | Обучение с подсказками | Нет |
| EXAM | Экзамен | +5 вопросов за ошибку |
| MARATHON | Все вопросы подряд | Нет |
| STUDY | Изучение (LEARN → QUIZ) | Нет |
| FLASHCARD | Flashcard (QUESTION → REVEALED) | Нет |

## Связи

- API: [[API Endpoints]]
- SM-2: [[Spaced Repetition Flow]]
- AI: [[AI Pipeline]]
- UI: [[Frontend UI]]
- Данные: [[Quiz Persistence]]
