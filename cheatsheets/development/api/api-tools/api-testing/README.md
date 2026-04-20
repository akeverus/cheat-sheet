---
title: "API Testing"
description: "Точка входа в раздел тестирования API: виды тестов, инструменты, чек-лист перед релизом."
tags:
  - meta
  - index
  - api
  - testing
type: "index"
updated: "2026-04-20"
---
# API Testing

API-тестирование проверяет поведение системы на уровне контракта: запрос-ответ, коды ошибок, идемпотентность, устойчивость под нагрузкой. Оно дешевле UI-тестов и ловит регрессии раньше, чем их увидит клиент. Раздел — карта видов тестов (функциональные, контрактные, интеграционные, нагрузочные, security) и инструментов.

Для кого: бэкенд-инженеры, QA-инженеры и все, кто отвечает за релизный чек-лист API. Раздел полезно читать, когда в проекте нет структуры тестов API и регрессии ловятся в продакшне.

## Полезные ссылки

### Основные документы
- [[api-testing-basics]] — виды тестов, инструменты, чек-лист перед релизом

### Соседние разделы
- [[README|Родительский раздел: API Tools]]
- [[README|Postman]] — сборка коллекций и Newman
- [[README|Insomnia]]
- [[README|Swagger / OpenAPI]] — контракт как источник тестов
- [[README|API Documentation]]
- [[README|REST API]]
- [REST Assured для Java](../../../../libraries/java/)
- [[README|Testing (общий раздел)]]

### Внешние ресурсы
- [REST Assured](https://rest-assured.io/)
- [Schemathesis](https://schemathesis.readthedocs.io/) — property-based contract testing
- [k6](https://k6.io/) — нагрузочное тестирование
- [OWASP API Security Top 10](https://owasp.org/API-Security/editions/2023/en/0x00-header/)

## Содержание

- [Виды тестов API](#виды-тестов-api)
- [Инструменты](#инструменты)
- [Чек-лист релиза API](#чек-лист-релиза-api)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Виды тестов API

| Уровень | Что проверяет | Инструменты |
|---------|---------------|-------------|
| Функциональные | Корректный ответ на валидный запрос | REST Assured, Postman, curl |
| Контрактные | Соответствие OpenAPI/GraphQL-схеме | Schemathesis, Dredd, Pact |
| Интеграционные | Взаимодействие с БД/очередями/внешними API | Testcontainers, WireMock |
| Нагрузочные | RPS, p95/p99 latency, устойчивость | k6, Gatling, JMeter |
| Security | OWASP API Top 10, auth bypass, injection | ZAP, Burp, Schemathesis |
| Smoke / Synthetic | Health/ready, критичные сценарии в проде | Pingdom, Grafana Synthetic |

## Инструменты

- **Постман-подобные:** Postman + Newman, Insomnia — быстрые мануальные проверки, CI-прогоны коллекций.
- **Кодовые:** REST Assured (Java), SuperTest (Node), Requests+pytest (Python) — включаются в стандартный test-suite.
- **Contract:** Pact (consumer-driven), Schemathesis (property-based по OAS).
- **Моки:** WireMock, MockServer — замена внешних зависимостей в integration-тестах.
- **Нагрузочные:** k6 (JS-скрипты), Gatling (Scala DSL), JMeter (GUI).

## Чек-лист релиза API

- [ ] OpenAPI актуален и проходит Spectral-линт
- [ ] Покрытие основных endpoint-ов функциональными тестами
- [ ] Контрактные тесты соответствия спеке в CI
- [ ] Проверка негативных сценариев (4xx, rate-limit, auth fail)
- [ ] Idempotency и retries работают как заявлено
- [ ] Нагрузочный тест подтверждает SLO (p95/p99)
- [ ] Security-скан (OWASP API Top 10)
- [ ] Логи/метрики содержат correlation-id и достаточный контекст

## Маршруты чтения

- **Бэкенд без тестов API (1 неделя):** `api-testing-basics.md` написать 10-20 REST Assured тестов добавить в CI.
- **Зрелость contract-first:** OpenAPI Schemathesis в PR Pact между сервисами.
- **Перед высокой нагрузкой:** k6 baseline выявить bottleneck профилирование БД/кода.

## Куда идти дальше

- Postman-коллекции и Newman — [[README]]
- Swagger/OpenAPI как источник тестов — [[README]]
- REST API design — [[README]]
- Общие практики тестирования — [[README]]
