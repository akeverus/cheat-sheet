---
type: component
title: "Security"
created: 2026-04-10
updated: 2026-04-10
tags:
  - component
  - security
  - spring-security
status: mature
related:
  - "[[Quiz App]]"
  - "[[API Endpoints]]"
---

# Security

Spring Security с token-based доступом к admin-эндпоинтам. Без user authentication.

## SecurityConfig

| Правило | Описание |
|---------|---------|
| CORS | Только localhost:8080 (настраивается через `app.security.cors`) |
| CSRF | CookieCsrfTokenRepository, отключён для `/api/**`, `/export`, `/actuator/**` |
| Sessions | IF_REQUIRED |
| CSP | Content Security Policy headers |
| X-Frame-Options | SAME_ORIGIN |
| Permissions | geolocation/camera/microphone отключены |

**Public endpoints**: CSS/JS/images, `/`, `/settings`, `/stats`, `/review`, Swagger UI.
Все остальные — permitted by default (нет role-based auth).

## SensitiveEndpointAccessService

Защита admin-эндпоинтов (`/api/admin/*`, `/api/regenerate`, `/export`):

- **Token validation** — constant-time comparison (`MessageDigest.isEqual`), защита от timing attacks
- **Rate limiting** — sliding window, 20 req/min per client IP (настраивается `REGENERATE_RATE_LIMIT_PER_MINUTE`)
- **Production enforcement** — обязательный admin token в prod-профиле

## RequestRateLimiter

In-memory rate limiter:
- Caffeine cache: max 10,000 entries, auto-eviction через 5 минут
- Synchronized deque per key
- Без внешних зависимостей (Redis не нужен)

## Headers

- `X-Admin-Token` — обязателен для sensitive endpoints
- `X-Forwarded-For` — опционально (отключён по умолчанию, небезопасен в prod)

## Связи

- Конфигурация: `SecurityConfig`, `AppProperties.Security`
- Защищает: [[API Endpoints]] (admin, regenerate, export)
- Exception handlers: `SensitiveEndpointAccessDeniedHandler` (403), `SensitiveEndpointAuthenticationEntryPoint` (401)
