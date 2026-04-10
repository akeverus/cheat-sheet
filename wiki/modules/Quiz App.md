---
type: module
title: "Quiz App"
created: 2026-04-10
updated: 2026-04-10
tags:
  - module
  - spring-boot
  - application
status: developing
path: modules/quiz-app
language: Java 17
purpose: Spring Boot приложение — контроллеры, сервисы, AI-интеграция
depends_on:
  - "[[Quiz Domain]]"
  - "[[Quiz Persistence]]"
used_by: []
---

# Quiz App

Основной модуль. Содержит всю бизнес-логику, UI, AI pipeline.

## Пакетная структура

```
com.cheatsheet.quiz
├── api/              — REST контроллеры, security, mappers
├── feature/
│   ├── interview/    — основной поток интервью
│   ├── question/     — engine генерации вопросов
│   ├── admin/        — администрирование
│   └── export/       — экспорт данных
├── service/
│   ├── ai/           — AI клиенты и промпты
│   ├── cache/        — Caffeine кэш
│   ├── strategy/     — стратегии выбора вопросов
│   └── imports/      — импорт из markdown
├── config/           — Spring конфигурация
├── infrastructure/   — рендеринг, поиск, startup
├── llm/              — LLM абстракции
└── common/           — утилиты, константы, exceptions
```

## Контроллеры

- `InterviewApiController` — REST API (next question, submit answer, hints, stats)
- `InterviewMvcController` — Thymeleaf UI
- `AdminController` — защищённые эндпоинты (regenerate, reset)
- `ExportController` — CSV/JSON экспорт

## Ключевые сервисы

- `InterviewFacade` — фасад оркестрации интервью
- `InterviewService` — основная логика
- `SpacedRepetitionService` — [[SM-2 Algorithm]]
- `AIQuestionService` — генерация ответов через [[AI Pipeline]]
- `MarkdownQuestionParser` — [[Question Import Flow]]

## Конфигурация

Порт 8080. Swagger UI: `/swagger-ui.html`. Профили: `postgres`, `prod`.

## Связи

- Использует [[Quiz Domain]] для моделей
- Использует [[Quiz Persistence]] для доступа к данным
- AI через [[AI Pipeline]] (OpenAI, DeepSeek)
