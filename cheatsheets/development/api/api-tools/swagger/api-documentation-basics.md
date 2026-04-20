---
title: "API Documentation: Основы"
description: "Практическое руководство по API-документации: OpenAPI, lifecycle контракта, CI-валидация, versioning, troubleshooting и эксплуатационные правила."
tags:
  - development
  - api
  - api-documentation-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# API Documentation: Основы

Практическое руководство по API-документации: как поддерживать контракт в актуальном состоянии, снижать риски интеграций и встраивать документацию в процесс разработки.

## Полезные ссылки

### Официальная документация

- [OpenAPI Specification](https://spec.openapis.org/oas/latest.html) — стандарт описания API
- [Swagger Tools](https://swagger.io/tools/) — экосистема Swagger
- [Redocly OpenAPI Guide](https://redocly.com/docs/openapi-visual-reference/) — визуальный справочник по OpenAPI

### См. также

- [[rest-api-best-practices|REST API Best Practices]] — проектирование REST API
- [[openapi-swagger|OpenAPI/Swagger]] — инструменты и интеграция
- [[api-testing-basics|API Testing Basics]] — контрактные и интеграционные тесты
- [[graphql|GraphQL]] — альтернативный подход к API-контракту

- [[grpc|gRPC]]
## Содержание

- [Зачем документация API в production](#зачем-документация-api-в-production)
- [Что обязательно должно быть в API-документации](#что-обязательно-должно-быть-в-api-документации)
- [OpenAPI как единый источник истины](#openapi-как-единый-источник-истины)
- [Lifecycle контракта: от задачи до релиза](#lifecycle-контракта-от-задачи-до-релиза)
- [Versioning и совместимость](#versioning-и-совместимость)
- [Operational context для API-документации](#operational-context-для-api-документации)
- [Checklist quality gate для PR](#checklist-quality-gate-для-pr)
- [Инструменты и автоматизация](#инструменты-и-автоматизация)
- [Troubleshooting](#troubleshooting)
- [Практический шаблон раздела endpoint](#практический-шаблон-раздела-endpoint)

## Зачем документация API в production

Документация API — это не "текст для Wiki", а часть контракта между командами и сервисами.

Если документация слабая или устаревшая:
- растет число интеграционных дефектов,
- увеличивается время онбординга,
- rollback/release становится рискованнее,
- команды расходятся в трактовке бизнес-правил.

## Что обязательно должно быть в API-документации

Минимальный набор:
- назначение API и business context,
- аутентификация и авторизация,
- endpoint + метод + path params + query params,
- пример запроса/ответа (успешного и ошибочного),
- коды ошибок и их meaning,
- ограничения (rate limits, payload size, timeouts),
- idempotency и retry-поведение.

## OpenAPI как единый источник истины

`OpenAPI` стоит вести в репозитории рядом с кодом.
Документация, mock-серверы и клиентские SDK должны генерироваться из одного контракта, а не правиться вручную в разных местах.

Пример минимального фрагмента:

```yaml
openapi: 3.0.3
info:
  title: Payment API
  version: 1.2.0
paths:
  /payments/{id}:
    get:
      summary: Get payment by id
      parameters:
        - in: path
          name: id
          required: true
          schema:
            type: string
      responses:
        "200":
          description: Success
```

## Lifecycle контракта: от задачи до релиза

Рекомендуемый поток:
1. сначала изменение контракта,
2. review контракта (архитектор/consumer команда),
3. реализация и тесты,
4. contract tests в CI,
5. публикация документации.

Такой порядок снижает «тихие» breaking changes.

## Versioning и совместимость

Рабочие правила:
- не ломать существующие поля/семантику без deprecation window,
- добавлять новые поля backward-compatible способом,
- фиксировать политику versioning (URL/header/media-type),
- указывать EOL старых версий.

Антипаттерн: менять смысл поля без изменения версии API.

## Operational context для API-документации

Сильная документация включает не только схему, но и эксплуатационные детали:
- expected latency SLO (например, p95 < 200ms),
- допустимый throughput и burst-профиль,
- retry/backoff политика,
- idempotency-key правила,
- поведение при частичных сбоях внешних зависимостей.

Это критично для команд, которые интегрируются с вашим API в production.

## Checklist quality gate для PR

Перед merge:
- [ ] обновлен OpenAPI контракт;
- [ ] добавлены примеры для happy path и error path;
- [ ] описаны security требования (auth scopes, headers);
- [ ] проверены коды ошибок и их consistency;
- [ ] добавлены/обновлены contract tests;
- [ ] обновлен changelog для consumer-команд.

## Инструменты и автоматизация

| Инструмент | Роль |
|------------|------|
| `OpenAPI` / `Swagger` | контракт и визуализация |
| `Spectral` | линтинг контракта и style guide |
| `OpenAPI Generator` | генерация SDK/stubs |
| `Schemathesis` | property-based тесты API по контракту |
| `Dredd` | валидация реализации против спецификации |

Минимум в CI:
- `lint` контракта,
- `breaking change check`,
- `contract tests`.

## Troubleshooting

| Симптом | Частая причина | Что делать |
|--------|----------------|-----------|
| Клиенты ломаются после релиза | скрытая breaking change | включить diff-check контракта в PR |
| Swagger UI не соответствует runtime | контракт не из кода и не из одного источника | перейти на spec-first или code-first с автоматической генерацией |
| Много "непонятных 4xx" у потребителей | слабо описаны ошибки | стандартизировать error response и добавить примеры |
| Долгий онбординг новых команд | нет operational контекста | добавить SLA, rate limits, retry policy и runbook ссылки |

## Практический шаблон раздела endpoint

Минимальная форма для каждого endpoint:

1. **Назначение**: что делает endpoint и в каком бизнес-процессе.
2. **Контракт**: метод, путь, параметры, схема.
3. **Примеры**: запрос/ответ + error cases.
4. **Надежность**: idempotency, retry, timeout.
5. **Эксплуатация**: SLO, лимиты, мониторинг.

Этот шаблон делает документацию инженерным инструментом, а не формальностью.
