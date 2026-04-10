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

## ArchUnit Rules (LayeredArchitectureTest)

1. **domainIsIndependent** — domain классы НЕ зависят от api, service, persistence, config. Domain — чистое ядро без инфраструктурных связей.
2. **serviceDoesNotDependOnApi** — service слой НЕ зависит от api.controller, api.security, api.exception. Допускаются только api.dto response DTOs.
3. **persistenceDoesNotDependOnApi** — persistence слой НЕ зависит от любого api пакета. БД-слой не знает о HTTP/REST.

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
