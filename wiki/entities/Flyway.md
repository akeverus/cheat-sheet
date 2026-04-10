---
type: entity
title: "Flyway"
entity_type: library
created: 2026-04-10
updated: 2026-04-10
tags:
  - entity
  - migration
  - database
status: seed
---

# Flyway

Миграции БД. 14 версий, раздельные файлы для SQLite и PostgreSQL.

## Миграции

| Версия | Описание |
|--------|---------|
| V1 | Начальная схема |
| V2 | Full-text search |
| V3 | Расширение вопросов |
| V4 | Code-вопросы |
| V5 | Подсказки |
| V6 | Диаграммы |
| V7-V8 | Индексы и guard |
| V9 | Объяснения ответов |
| V10 | Дневная активность |
| V11 | Takeaway |
| V12-V13 | Версионирование ответов |
| V14 | Расширенная схема |

## Связи

- Используется в [[Quiz Persistence]]
- Поддерживает [[ADR-002 SQLite Default]]
