---
type: entity
title: "Thymeleaf"
entity_type: library
created: 2026-04-10
updated: 2026-04-10
tags:
  - entity
  - ui
  - template
status: seed
---

# Thymeleaf

Server-side шаблонизатор для UI.

## Шаблоны

- `index.html` — главная страница
- `result.html` — результат ответа
- `session-summary.html` — итоги сессии
- `stats.html` — статистика
- `focus-training.html` — фокусная тренировка
- `settings.html` — настройки

## Фрагменты

`fragments/` — переиспользуемые компоненты: header, head, controls, stats-grid.

## Связи

- Рендерится через `InterviewMvcController` в [[Quiz App]]
