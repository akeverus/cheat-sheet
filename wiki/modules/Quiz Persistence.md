---
type: module
title: "Quiz Persistence"
created: 2026-04-10
updated: 2026-04-10
tags:
  - module
  - persistence
  - jdbc
status: mature
path: modules/quiz-persistence
language: Java 17
purpose: JDBC-репозитории и Flyway-миграции для SQLite и PostgreSQL
depends_on:
  - "[[Quiz Domain]]"
used_by:
  - "[[Quiz App]]"
---

# Quiz Persistence

Слой данных. Spring JDBC (не JPA). Поддержка SQLite (default) и PostgreSQL.

## Репозитории

### QuestionRepository

Самый большой репозиторий. JdbcTemplate-based.

**SELECT (session init & question selection):**
- `findDueQuestions(topic, important, onlyWrong, nowEpoch, limit)` — JOIN review_state, фильтр next_review_at ≤ now, ORDER BY due-time ASC
- `findDueQuestionsByTopics(topicList, ...)` — CASE-based topic ordering для учебной последовательности
- `findNextQuestions(...)` — ORDER BY next_review_at ASC, RANDOM()
- `findQuestionIdsForSession(...)` — IDs для новой сессии
- `findQuestionIdsExcluding(excludeIds, ...)` — penalty questions для EXAM
- `findShuffledQuestionIds(...)` — ORDER BY RANDOM(), игнорирует topic/spaced-rep

**SELECT (lookup):**
- `findBySlug(slug)`, `findById(id)` → Optional\<Question\>
- `findByIds(List<Long>)` → batch load (решает N+1)
- `findRelatedByTopic(excludeId, topic, limit)` — same-topic, приоритет wrong_count DESC
- `findAllQuestionIdsWithoutOptions()` → для preload

**WRITE:**
- `insert(Question)` → returns generated id, устанавливает sourceSlug = slug if null
- `update(Question)` → slug, content, metadata
- `updateTakeaway(id, text)`, `updateDiagram(id, mermaid)`, `updateCodeSnippet(id, code)`
- `deleteByFilePath(filePath)` → CASCADE к answer_options, hints, review_state

### AnswerOptionRepository

- `findByQuestionId(id)` → List\<AnswerOption\> ORDER BY display_order
- `insertAll(questionId, List<AnswerOptionCreate>)` → batch insert via batchUpdate
- `deleteByQuestionId(id)` → purge before regen

### ReviewStateRepository

- `findByQuestionId(id)` → Optional\<ReviewState\>
- `insertIfAbsent(questionId, nowEpoch)` → ON CONFLICT DO NOTHING
- `update(ReviewState)` → SM-2 state atomically
- `reset(questionId, nowEpoch)` → repetitions=0, easeFactor=2.5, result=RESET

### Другие

| Репозиторий | Назначение |
|-------------|-----------|
| `QuestionStatsRepository` | Статистика по вопросу |
| `HintRepository` | Хранение подсказок (3 уровня) |
| `UserTopicStatsRepository` | Статистика по темам (mastery) |
| `DailyActivityRepository` | Дневная серия |
| `SqliteFtsRepository` | SQLite FTS5 |
| `PostgresFullTextSearchRepository` | PostgreSQL tsvector |

## Утилиты

- `InterviewFilterSql` — динамический WHERE: topic, is_important, wrong_count
- `FtsQueryUtils` — парсинг FTS-запросов

## Паттерны

- **Parameterized SQL** — StringBuilder + List\<Object\> params (защита от SQL injection)
- **Batch operations** — batchUpdate для AnswerOptionCreate
- **ON CONFLICT DO NOTHING** — duplicate-key safety для review_state
- **CASCADE deletes** — FK ON DELETE CASCADE при re-import

## Связи

- Маппит из БД в доменные модели из [[Quiz Domain]]
- Используется сервисами в [[Quiz App]]
- Схема описана в [[Database Schema]]
