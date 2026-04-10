---
type: component
title: "API Endpoints"
created: 2026-04-10
updated: 2026-04-10
tags:
  - component
  - api
  - rest
status: mature
related:
  - "[[Quiz App]]"
  - "[[Interview Flow]]"
  - "[[Security]]"
---

# API Endpoints

REST API через `InterviewApiController`. Swagger UI: `/swagger-ui.html`.

## Training

| Method | Path | Описание |
|--------|------|---------|
| POST | `/api/answer` | Submit answer → AnswerResponse (correct, explanations, SM-2 state) |
| GET | `/api/next` | Next question (filters: topic, group, important, onlyWrong, shuffle, weakTopics, ordered, excludeQuestionId) |
| POST | `/api/hint` | 3-level progressive hint |
| POST | `/api/confidence` | Update confidence grade (1-5) |
| POST | `/api/favorite` | Toggle favorite |

## AI Insights

| Method | Path | Описание |
|--------|------|---------|
| GET | `/api/takeaway` | Key insight для вопроса |
| GET | `/api/comparison` | Selected vs correct option (criterion-by-criterion) |
| GET | `/api/code-trace` | Пошаговое выполнение кода |
| POST | `/api/wrong-feedback` | Почему выбранный ответ неправильный |

## Stats

| Method | Path | Описание |
|--------|------|---------|
| GET | `/api/stats` | Общая статистика (total, due, learned, accuracy) |
| GET | `/api/topic-stats` | Статистика по темам |
| GET | `/api/streak` | Текущая серия |

## Admin (requires X-Admin-Token)

| Method | Path | Описание |
|--------|------|---------|
| POST | `/api/admin/options/clear` | Очистить все AI-generated options |
| GET | `/api/admin/senior-rules` | Priority overrides |
| GET | `/api/admin/senior-rules/help` | Описания правил |
| GET | `/api/admin/senior-rules/keys` | Доступные ключи |

## Sensitive (requires X-Admin-Token, rate-limited)

| Method | Path | Описание |
|--------|------|---------|
| POST | `/api/regenerate` | Force regeneration (20 req/min per IP) |

## Export

| Method | Path | Описание |
|--------|------|---------|
| GET | `/export` | CSV/JSON экспорт |

## Связи

- Usecase-сервисы: `AnswerApiService`, `NextQuestionApiService`, `HintApiService`, `QuestionInsightsApiService`
- Защита: [[Security]] (admin token, rate limiter)
- Логика: [[Interview Flow]], [[AI Pipeline]]
