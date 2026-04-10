---
type: decision
title: "ADR-001 Multi-Module Gradle"
created: 2026-04-10
updated: 2026-04-10
tags:
  - decision
  - architecture
  - gradle
status: active
---

# ADR-001: Multi-Module Gradle Structure

## Решение

Проект разделён на 3 Gradle-модуля: `quiz-domain`, `quiz-persistence`, `quiz-app`.

## Контекст

Нужно чёткое разделение ответственности и защита от нарушения архитектурных границ.

## Последствия

- Domain модуль не имеет Spring-зависимостей — чистая модель
- Persistence изолирован от бизнес-логики
- ArchUnit дополнительно проверяет границы внутри quiz-app
- Gradle Kotlin DSL с version catalog (`libs.versions.toml`)
