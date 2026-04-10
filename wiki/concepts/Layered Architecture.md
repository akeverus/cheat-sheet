---
type: concept
title: "Layered Architecture"
created: 2026-04-10
updated: 2026-04-10
tags:
  - concept
  - architecture
  - archunit
complexity: basic
domain: architecture
status: developing
---

# Layered Architecture

Архитектурные правила, проверяемые через ArchUnit (`LayeredArchitectureTest`).

## Правила

- `domain` — не зависит от api/service/persistence/config
- `service` — не зависит от контроллеров или security
- `persistence` — не зависит от api

## Структура

```
api/          → presentation layer (controllers, DTOs)
service/      → application layer (business logic)
feature/      → vertical slices (interview, question, admin, export)
domain/       → domain layer (models, enums, exceptions)
persistence/  → data layer (repositories, migrations)
config/       → infrastructure (Spring config)
```

## Связи

- Проверяется в `LayeredArchitectureTest`
- Определяет зависимости между [[Quiz Domain]], [[Quiz Persistence]], [[Quiz App]]
