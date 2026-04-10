---
type: decision
title: "ADR-002 SQLite Default"
created: 2026-04-10
updated: 2026-04-10
tags:
  - decision
  - database
status: active
---

# ADR-002: SQLite по умолчанию

## Решение

SQLite как основная БД. PostgreSQL через профиль `--spring.profiles.active=postgres`.

## Контекст

Приложение для индивидуального использования. SQLite: zero-config, файл `interview.db`. PostgreSQL — для Docker/production.

## Последствия

- Два набора Flyway-миграций: `db/migration/` (SQLite), `db/migration-postgres/` (PostgreSQL)
- Два FTS-репозитория: `SqliteFtsRepository`, `PostgresFullTextSearchRepository`
- Testcontainers для PostgreSQL-тестов
