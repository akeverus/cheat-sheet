---
type: module
title: "Quiz Domain"
created: 2026-04-10
updated: 2026-04-10
tags:
  - module
  - domain
status: mature
path: modules/quiz-domain
language: Java 17
purpose: Чистая доменная модель без инфраструктурных зависимостей
depends_on: []
used_by:
  - "[[Quiz Persistence]]"
  - "[[Quiz App]]"
---

# Quiz Domain

Чистый модуль доменной модели. Зависимости: только SLF4J и Lombok. Никакого Spring.

## Ключевые сущности

### InterviewSession (Serializable)

Мутабельное состояние сессии, хранится в HTTP-сессии.

| Поле | Тип | Описание |
|------|-----|---------|
| `mode` | InterviewMode | TRAINING, EXAM, MARATHON, STUDY, FLASHCARD |
| `questionIds` | List\<Long\> | Мутабельный список ID вопросов |
| `index` | int | Текущая позиция |
| `correct` / `wrong` | int | Счётчики ответов |
| `topic` / `group` | String | Фильтры |
| `studyPhase` | StudyPhase | LEARN / QUIZ |
| `flashcardPhase` | FlashcardPhase | QUESTION / REVEALED |

Методы: `registerAnswer(boolean)`, `addPenaltyQuestions()`, `currentQuestionId()`, `isFinished()`, `switchToQuizPhase()`, `revealFlashcard()`.

История: `AnswerRecord(questionId, correct, topic)`.

### Question (record, immutable)

20 полей. Ключевые:

| Поле | Тип | Описание |
|------|-----|---------|
| `id` | Long | PK, autoincrement |
| `slug` | String | Уникальный идентификатор |
| `sourceSlug` | String | FK к оригиналу (для вариантов) |
| `topic` | String | Тема |
| `questionText` / `answerMarkdown` | String | Контент |
| `questionType` | QuestionType | CONCEPT, CODE_ANALYSIS, DEBUGGING, etc. |
| `difficulty` | Difficulty | EASY, MEDIUM (default), HARD |
| `codeSnippet` / `diagramMermaid` | String | Код и диаграмма |
| `takeaway` | String | AI-generated инсайт |
| `options` | List\<QuestionOption\> | Inline-варианты |

Factory: `forImport()` — создание из markdown с дефолтами. Защитное копирование для tags/options.

### ReviewState (record with @With)

SM-2 состояние. Иммутабельный с non-destructive updates.

| Поле | Тип | Constraint |
|------|-----|-----------|
| `questionId` | long | PK |
| `repetitions` | int | ≥ 0 |
| `intervalDays` | int | ≥ 0 |
| `easeFactor` | double | ≥ 1.3 |
| `nextReviewAt` | long | epoch-seconds |
| `lastResult` | ReviewResult | NEW, CORRECT, WRONG, RESET |
| `correctCount` / `wrongCount` | int | ≥ 0 |

### Другие модели

| Модель | Описание |
|--------|---------|
| `AnswerOption` | Персистентный вариант: questionId, optionText, correct, source, explanation, promptVersion |
| `QuestionOption` | Inline-вариант: id, text, correct, explanation |
| `InterviewFilter` | topic, group, importantOnly, onlyWrong, shuffle, ordered. `effectiveTopic()` = null if shuffled |
| `DailyProgress` | questionsAnswered, correctCount, goal. `goalReached()` |
| `Hint` | questionId, level (1-3), text |

## Enums

| Enum | Значения | Default |
|------|---------|---------|
| `InterviewMode` | TRAINING, EXAM, MARATHON, STUDY, FLASHCARD | TRAINING |
| `QuestionType` | CONCEPT, CODE_ANALYSIS, DEBUGGING, ARCHITECTURE, TEXT, CODE | TEXT |
| `Difficulty` | EASY, MEDIUM, HARD | MEDIUM |
| `ReviewResult` | NEW, CORRECT, WRONG, RESET | NEW |
| `StudyPhase` | LEARN, QUIZ | — |
| `FlashcardPhase` | QUESTION, REVEALED | — |
| `AiProvider` | OPENAI, DEEPSEEK | — |

Все enums имеют safe `fromString()` — обрабатывают null, whitespace, invalid values.

## Паттерны

- **Immutable records** с `@With` для non-destructive updates (ReviewState)
- **Defensive copying** — `List.copyOf()` для tags/options
- **Serializable session** — InterviewSession для HTTP-сессии
- **Compact constructors** — валидация constraints (ReviewState: easeFactor ≥ 1.3)

## Связи

- Используется в [[Quiz Persistence]] для маппинга из БД
- Используется в [[Quiz App]] повсеместно
- Enforced by [[Layered Architecture]]: domain НЕ импортирует ничего из других слоёв
