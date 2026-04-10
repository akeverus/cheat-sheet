---
type: overview
title: "Cheat Sheet — Architecture Overview"
created: 2026-04-10
updated: 2026-04-10
tags:
  - overview
  - architecture
status: developing
---

# Cheat Sheet — Architecture Overview

Приложение для подготовки к техническим интервью с интервальным повторением (SM-2) и AI-генерацией ответов.

## Стек

| Компонент | Технология |
|-----------|-----------|
| Framework | Spring Boot 3.x, Java 17 |
| Web | Spring MVC + Thymeleaf |
| Persistence | Spring JDBC + Flyway |
| Database | SQLite (default), PostgreSQL (optional) |
| Search | SQLite FTS / PostgreSQL full-text search |
| Cache | Caffeine |
| AI | OpenAI (gpt-4.1-mini), DeepSeek |
| Build | Gradle Kotlin DSL (multi-module) |
| Testing | JUnit 5, MockMvc, Testcontainers, ArchUnit |

## Модули

```
cheat-sheet/
├── quiz-domain       — чистая доменная модель, без Spring
├── quiz-persistence  — JDBC-репозитории, Flyway-миграции
└── quiz-app          — Spring Boot: контроллеры, сервисы, AI, конфиг
```

- [[Quiz Domain]] — доменные сущности: Question, InterviewSession, ReviewState
- [[Quiz Persistence]] — репозитории и SQL: QuestionRepository, AnswerOptionRepository
- [[Quiz App]] — приложение: контроллеры, AI pipeline, spaced repetition

## Ключевые потоки

- [[Interview Flow]] — от старта сессии до ответа на вопрос
- [[AI Pipeline]] — генерация вопросов и вариантов ответов через LLM
- [[Spaced Repetition Flow]] — SM-2 алгоритм повторения
- [[Question Import Flow]] — парсинг markdown → загрузка в БД

## Зависимости между модулями

```
quiz-app → quiz-persistence → quiz-domain
quiz-app → quiz-domain
```

Правила: domain не зависит ни от чего. persistence зависит только от domain. app зависит от обоих.
