---
type: concept
title: "Interview Flow"
created: 2026-04-10
updated: 2026-04-10
tags:
  - flow
  - interview
  - core
status: developing
related:
  - "[[Quiz App]]"
  - "[[Spaced Repetition Flow]]"
  - "[[AI Pipeline]]"
---

# Interview Flow

Основной поток от старта сессии до завершения.

## Последовательность

1. **Старт сессии** — `TrainingSessionService` создаёт `InterviewSession` с фильтрами (тема, режим, shuffle)
2. **Выбор вопроса** — `QuestionSelectionStrategy` выбирает следующий вопрос:
   - `DefaultSelectionStrategy` — последовательно
   - `ShuffleSelectionStrategy` — случайный порядок
   - `WeakTopicsSelectionStrategy` — фокус на слабых темах
3. **Отображение** — `InterviewMvcController` рендерит через Thymeleaf или `InterviewApiController` возвращает JSON
4. **Ответ** — пользователь отвечает, `AnswerApiService` обрабатывает
5. **Feedback** — `WrongAnswerFeedbackService` (AI), `HintService` (AI), `TakeawayService`
6. **SM-2 обновление** — `SpacedRepetitionService` обновляет `ReviewState`
7. **Следующий вопрос** или **завершение** — `SessionSummaryService`

## Ключевые сервисы

- `InterviewFacade` — точка входа, оркестрация
- `SessionFlowService` — управление переходами
- `PreloadService` — асинхронная предзагрузка ответов

## Режимы

| Режим | Описание |
|-------|---------|
| TRAINING | Обучение с подсказками |
| EXAM | Экзамен с штрафами |
| MARATHON | Все вопросы подряд |
