---
title: "Java Libraries"
description: "Точка входа в раздел Java-библиотек: HTTP-клиенты, пулы, метрики, resilience, ORM/JDBC, трейсинг, тесты и функциональные расширения."
tags:
  - meta
  - index
  - java
  - libraries
type: "index"
updated: "2026-04-20"
---
# Java Libraries

Раздел объединяет боевые Java-библиотеки, которые встречаются почти в каждом production-сервисе: HTTP-клиенты (Apache HttpClient, OkHttp, Retrofit), пулы (HikariCP), ORM/SQL DSL (jOOQ), метрики и трейсинг (Micrometer, OpenTelemetry, Sleuth), resilience-паттерны (Resilience4j), тестовая инфраструктура (Testcontainers, WireMock, REST-Assured), сериализация и DTO (Jackson, MapStruct, Lombok, Bean Validation), функциональные утилиты (Vavr) и офисные форматы (Apache POI).

Для кого: Java-разработчики, выбирающие библиотеку под конкретную задачу; архитекторы, проектирующие наблюдаемость и отказоустойчивость; инженеры, которые хотят систематизировать набор зависимостей в `gradle/libs.versions.toml`.

## Полезные ссылки

### HTTP / RPC
- [HTTP-клиенты в Java — обзор и сравнение](java-http-clients.md)
- [Apache HttpClient](java-apache-httpclient.md)
- [OkHttp](java-okhttp.md)
- [Retrofit](java-retrofit.md)

### Persistence
- [HikariCP](java-hikaricp.md) — connection pool
- [jOOQ](java-jooq.md) — type-safe SQL DSL

### Observability
- [Micrometer](java-micrometer.md)
- [OpenTelemetry](java-opentelemetry.md)
- [Spring Cloud Sleuth](java-spring-cloud-sleuth.md)

### Resilience
- [Resilience4j](java-resilience4j.md) — Circuit Breaker, Retry, Rate Limiter

### Testing
- [Testcontainers](java-testcontainers.md)
- [WireMock](java-wiremock.md)
- [REST-Assured](java-rest-assured.md)

### Serialization / DTO
- [Jackson](java-jackson.md) — JSON сериализация/десериализация
- [MapStruct](java-mapstruct.md) — compile-time маппинг DTO ↔ entity
- [Lombok](java-lombok.md) — кодогенерация (getters/setters/builder/logger)
- [Bean Validation](java-bean-validation.md) — JSR-380, аннотации валидации

### Другое
- [Vavr](java-vavr.md) — функциональные коллекции и Try/Either
- [Apache POI](java-apache-poi.md) — Excel/Word
- [Protobuf](java-protobuf.md) — бинарная сериализация

### Соседние разделы
- [Libraries](../README.md)
- [Utility libraries](../utility-libraries/README.md), [Serialization](../serialization/README.md), [Testing libraries](../testing-libraries/README.md), [Code generation](../code-generation/README.md)
- [Spring](../../frameworks/java-frameworks/spring/README.md)
- [Monitoring](../../monitoring/README.md), [Testing](../../testing/README.md)

## Содержание

- [Карта по задачам](#карта-по-задачам)
- [Когда выбирать что](#когда-выбирать-что)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта по задачам

| Задача | Библиотека |
|--------|------------|
| Блокирующий HTTP | Apache HttpClient, OkHttp |
| Типизированный HTTP-клиент | Retrofit |
| JDBC pool | HikariCP |
| Type-safe SQL | jOOQ |
| Метрики | Micrometer |
| Трейсинг | OpenTelemetry, Sleuth (legacy) |
| Circuit Breaker / Retry | Resilience4j |
| Интеграционные тесты с БД/Kafka | Testcontainers |
| HTTP mock в тестах | WireMock |
| REST API acceptance tests | REST-Assured |
| FP-коллекции | Vavr |
| Office-форматы | Apache POI |
| Бинарная сериализация | Protobuf |
| JSON сериализация | Jackson |
| DTO ↔ entity маппинг | MapStruct |
| Кодогенерация POJO / logger | Lombok |
| Валидация DTO (JSR-380) | Bean Validation (Hibernate Validator) |

## Когда выбирать что

- **HTTP:** обзор всех вариантов — [java-http-clients](java-http-clients.md). OkHttp — default для Android/Kotlin; Apache HttpClient — enterprise/прокси/NTLM; Retrofit — нужен типизированный DSL; Spring `RestClient`/`WebClient` — Spring-сервисы.
- **Pool:** HikariCP — индустриальный стандарт; альтернативы (Tomcat JDBC, DBCP2) — legacy.
- **ORM vs SQL:** jOOQ — когда нужен SQL-контроль и реляционная модель; Hibernate — для богатых агрегатов (см. [databases/orm](../../databases/orm/README.md)).
- **Observability:** Micrometer → Prometheus/VictoriaMetrics; OpenTelemetry — единый стандарт trace/metric/log (Sleuth deprecated в Spring Boot 3).
- **Resilience:** Resilience4j заменил Hystrix; Spring Cloud CircuitBreaker — это abstraction поверх него.

## Маршруты чтения

- **Production-чеклист сервиса:** HikariCP → Micrometer → Resilience4j → OpenTelemetry.
- **Test pyramid:** REST-Assured (E2E) → WireMock (контрактные) → Testcontainers (интеграционные).
- **Legacy-миграция:** Sleuth → OpenTelemetry; Hystrix → Resilience4j; Apache HttpClient 4 → 5.

## Куда идти дальше

- Утилитарные библиотеки — [utility-libraries](../utility-libraries/README.md)
- Сериализация — [serialization](../serialization/README.md)
- Наблюдаемость — [monitoring](../../monitoring/README.md)
- Тестирование — [testing](../../testing/README.md)
