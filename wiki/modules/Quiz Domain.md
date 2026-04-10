---
type: module
title: "Quiz Domain"
created: 2026-04-10
updated: 2026-04-10
tags:
  - module
  - domain
status: developing
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

| Класс | Назначение |
|-------|-----------|
| `InterviewSession` | Состояние сессии: режим, вопросы, фильтры |
| `Question` | Вопрос: тема, тип, сложность, markdown-контент |
| `QuestionOption` | Вариант ответа с источником и хэшем |
| `AnswerOption` | Сгенерированный или кэшированный вариант |
| `ReviewState` | Состояние SM-2 повторения |
| `DailyProgress` | Серия и дневная активность |
| `Hint` | Подсказка с AI-генерацией |

## Enums

| Enum | Значения |
|------|---------|
| `InterviewMode` | TRAINING, EXAM, MARATHON |
| `QuestionType` | MULTIPLE_CHOICE, CODE, FREE_TEXT |
| `Difficulty` | Уровень сложности |
| `FlashcardPhase` | Фаза обучения |
| `FlashcardGrade` | Оценка ответа |
| `AiProvider` | OPENAI, DEEPSEEK |

## Exceptions

- `QuestionNotFoundException` — вопрос не найден
- `OptionNotFoundException` — вариант ответа не найден

## Связи

- Используется в [[Quiz Persistence]] для маппинга из БД
- Используется в [[Quiz App]] повсеместно
- Enforced by [[Layered Architecture]] (ArchUnit): domain не импортирует ничего из других слоёв
