---
title: "Web Backend"
description: "Точка входа в раздел backend: архитектура серверных приложений, API, БД-интеграция, безопасность, кэширование, очереди, развёртывание и мониторинг."
tags:
  - meta
  - index
  - web-backend
type: "index"
updated: "2026-04-17"
---
# Web Backend

Серверная часть веб-приложений: приём HTTP-запросов, бизнес-логика, работа с БД, интеграция с внешними системами, аутентификация, кэширование, очереди, наблюдаемость. Этот раздел — overview-документ, связывающий практики и инструменты из всех соседних доменов репозитория.

Раздел не заменяет углублённые материалы по Spring, REST, SQL — он описывает, как из этих кусочков собрать работающую систему и где читать про каждый слой.

## Полезные ссылки

### Основной документ
- [Основы backend-разработки](backend-basics.md)

### Смежные разделы репозитория

Backend — это мост между множеством доменов:

- [REST API](../api/rest/README.md), [GraphQL](../api/graphql/README.md), [gRPC](../api/grpc/README.md) — протоколы
- [Spring Boot](../../frameworks/java-frameworks/spring/README.md) — основной стек в репозитории
- [Базы данных](../../databases/README.md), [SQL](../../databases/sql/README.md), [ORM](../../databases/orm/README.md) — persistence
- [Messaging](../messaging/README.md) — асинхронная интеграция
- [Безопасность приложения](../../security/application/README.md), [OAuth2/OIDC](../../security/README.md)
- [Docker](../../platform/containers/docker/README.md), [Kubernetes](../../platform/containers/kubernetes/README.md), [CI/CD](../../platform/ci-cd/README.md)
- [Мониторинг](../../monitoring/README.md), [Логирование](../../monitoring/logging/README.md), [Трейсинг](../../monitoring/tracing/README.md)
- [Тестирование](../../testing/README.md)

### Внешние ресурсы
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [High Scalability](http://highscalability.com/)
- [12 Factor App](https://12factor.net/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)

## Содержание

- [Слои типичного backend](#слои-типичного-backend)
- [Карта тем и где читать](#карта-тем-и-где-читать)
- [Чек-лист production backend](#чек-лист-production-backend)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Слои типичного backend

```text
┌──────────────────────────────────────────────┐
│ Controllers / GraphQL resolvers / gRPC stubs │  <-- HTTP-слой, сериализация, валидация
├──────────────────────────────────────────────┤
│ Service layer / Use cases                    │  <-- бизнес-логика, транзакции
├──────────────────────────────────────────────┤
│ Repositories / DAO / Clients                 │  <-- persistence и внешние вызовы
├──────────────────────────────────────────────┤
│ Domain model                                 │  <-- чистые классы, инварианты
└──────────────────────────────────────────────┘
```

## Карта тем и где читать

| Слой / практика | Файл |
|-----------------|------|
| Архитектура backend, слои | [backend-basics.md](backend-basics.md#архитектура-типичного-backend) |
| REST API-контракт, OpenAPI | [development/api/rest/](../api/rest/README.md) |
| Spring Web, `@RestController` | [frameworks/java-frameworks/spring/](../../frameworks/java-frameworks/spring/README.md) |
| JDBC / JPA / Hibernate | [databases/orm/](../../databases/orm/README.md) |
| Аутентификация (JWT, OAuth2) | [security/application/](../../security/application/README.md) |
| Кэширование (Caffeine, Redis) | [databases/nosql/redis/](../../databases/nosql/redis/) |
| Очереди (Kafka, RabbitMQ) | [development/messaging/](../messaging/README.md) |
| Валидация (Bean Validation) | [backend-basics.md](backend-basics.md) |
| Логирование и метрики | [monitoring/](../../monitoring/README.md) |
| Контейнеризация и деплой | [platform/containers/](../../platform/containers/README.md) |
| Тестирование | [testing/](../../testing/README.md) |

## Чек-лист production backend

- [ ] OpenAPI-спецификация синхронизирована с кодом
- [ ] Health/readiness endpoints (Actuator)
- [ ] Structured logging (JSON, correlation-id)
- [ ] Метрики: latency p50/p95/p99, error rate, in-flight
- [ ] Distributed tracing (OpenTelemetry)
- [ ] Graceful shutdown и connection draining
- [ ] Rate limiting / circuit breaker
- [ ] Backups и миграции БД через Flyway/Liquibase
- [ ] Секреты не в git: vault / k8s secrets
- [ ] OWASP Top 10: XSS, SQLi, CSRF, SSRF, IDOR
- [ ] Load testing перед релизом
- [ ] Runbook для on-call

## Маршруты чтения

- **Junior backend (1 день):** `backend-basics.md` → `REST API` → `Spring Boot` → `SQL` → `Testing`.
- **Подготовка к system design интервью:** `backend-basics.md` → `architecture/system-design/` → `architecture/scalability-patterns`.
- **Production readiness review:** чек-лист выше + `monitoring/` + `security/application/`.

## Куда идти дальше

- Проектирование систем — [architecture/system-design/](../../architecture/system-design/README.md)
- Паттерны интеграции — [patterns/](../../patterns/README.md)
- Масштабирование и HA — [architecture/](../../architecture/README.md)
- DevOps-практики — [platform/ci-cd/](../../platform/ci-cd/README.md)
