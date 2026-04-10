---
type: component
title: "Configuration"
created: 2026-04-10
updated: 2026-04-10
tags:
  - component
  - config
  - spring
status: mature
related:
  - "[[Quiz App]]"
  - "[[Spring Boot]]"
---

# Configuration

`AppProperties` (`app.*` prefix) — центральная конфигурация.

## Основные свойства

| Секция | Свойства |
|--------|---------|
| **Server** | port: 8080 |
| **Interview** | learned-repetitions: 3, exam-penalty: 5, session max: 200, default exam/marathon counts |
| **AI global** | timeout: 60s, maxInputLength: 10KB |
| **OpenAI** | model: gpt-4.1-mini, temperature: 0.4 |
| **DeepSeek** | model: deepseek-chat, temperature: 0.4 |
| **Preload** | core: 4 threads, max: 8, batch-size: 5 |
| **Cache** | max: 500 entries, TTL: 1h |
| **Search** | max-limit: 100, preview: 300 chars |
| **Security** | CORS: localhost, rate-limit: 20/min |
| **Database** | SQLite (interview.db), WAL mode |

## Env Variables

| Variable | Назначение |
|----------|-----------|
| `DEEPSEEK_API_KEY` | API key DeepSeek |
| `OPENAI_API_KEY` | API key OpenAI |
| `APP_ADMIN_TOKEN` | Token для admin endpoints |
| `REGENERATE_RATE_LIMIT_PER_MINUTE` | Rate limit (default: 20) |

## Профили

| Профиль | Описание |
|---------|---------|
| (default) | SQLite, Swagger UI включён |
| `postgres` | PostgreSQL через Testcontainers/Docker |
| `prod` | Swagger UI отключён, admin token обязателен |

## Config Classes

| Класс | Назначение |
|-------|-----------|
| `AppProperties` | @ConfigurationProperties("app") — все app.* |
| `SecurityConfig` | Spring Security: CORS, CSRF, CSP |
| `AiClientConfig` | WebClient beans для OpenAI/DeepSeek |
| `CacheConfig` | Caffeine cache |
| `AsyncConfig` | Thread pool для preload |
| `InfrastructureConfig` | Bean registration |
| `OpenApiConfig` | Swagger/OpenAPI |

## Связи

- Используется всеми сервисами в [[Quiz App]]
- Определяет поведение [[AI Pipeline]], [[Security]], [[Interview Flow]]
