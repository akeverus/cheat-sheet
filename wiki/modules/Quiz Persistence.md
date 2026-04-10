---
type: module
title: "Quiz Persistence"
created: 2026-04-10
updated: 2026-04-10
tags:
  - module
  - persistence
  - jdbc
status: developing
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

| Класс | Назначение |
|-------|-----------|
| `QuestionRepository` | CRUD вопросов, фильтрация по теме/важности/ошибкам |
| `AnswerOptionRepository` | Кэширование и версионирование вариантов ответов |
| `ReviewStateRepository` | Состояние SM-2 повторения |
| `QuestionStatsRepository` | Статистика по вопросу |
| `HintRepository` | Хранение подсказок |
| `UserTopicStatsRepository` | Статистика по темам |
| `DailyActivityRepository` | Дневная серия |
| `SqliteFtsRepository` | Full-text search для SQLite |
| `PostgresFullTextSearchRepository` | Full-text search для PostgreSQL |

## Утилиты

- `InterviewFilterSql` — динамический WHERE для фильтров
- `FtsQueryUtils` — парсинг FTS-запросов

## Миграции (Flyway)

14 миграций: от `V1__init.sql` до `V14__question_v2_and_user_topic_stats.sql`.
Отдельные файлы для SQLite (`db/migration/`) и PostgreSQL (`db/migration-postgres/`).

## Связи

- Маппит из БД в доменные модели из [[Quiz Domain]]
- Используется сервисами в [[Quiz App]]
