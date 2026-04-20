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
- [[java-http-clients|HTTP-клиенты в Java — обзор и сравнение]]
- [[java-apache-httpclient|Apache HttpClient]]
- [[java-okhttp|OkHttp]]
- [[java-retrofit|Retrofit]]

### Persistence
- [[java-hikaricp|HikariCP]] — connection pool
- [[java-jooq|jOOQ]] — type-safe SQL DSL

### Observability
- [[java-micrometer|Micrometer]]
- [[java-opentelemetry|OpenTelemetry]]
- [[java-spring-cloud-sleuth|Spring Cloud Sleuth]]

### Resilience
- [[java-resilience4j|Resilience4j]] — Circuit Breaker, Retry, Rate Limiter

### Testing
- [[java-testcontainers|Testcontainers]]
- [[java-wiremock|WireMock]]
- [[java-rest-assured|REST-Assured]]

### Serialization / DTO
- [[java-jackson|Jackson]] — JSON сериализация/десериализация
- [[java-mapstruct|MapStruct]] — compile-time маппинг DTO ↔ entity
- [[java-lombok|Lombok]] — кодогенерация (getters/setters/builder/logger)
- [[java-bean-validation|Bean Validation]] — JSR-380, аннотации валидации

### Другое
- [[java-vavr|Vavr]] — функциональные коллекции и Try/Either
- [[java-apache-poi|Apache POI]] — Excel/Word
- [[java-protobuf|Protobuf]] — бинарная сериализация

### Соседние разделы
- [[README|Libraries]]
- [[README|Utility libraries]], [[README|Serialization]], [[README|Testing libraries]], [[README|Code generation]]
- [[README|Spring]]
- [[README|Monitoring]], [[README|Testing]]

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

- **HTTP:** обзор всех вариантов — [[java-http-clients]]. OkHttp — default для Android/Kotlin; Apache HttpClient — enterprise/прокси/NTLM; Retrofit — нужен типизированный DSL; Spring `RestClient`/`WebClient` — Spring-сервисы.
- **Pool:** HikariCP — индустриальный стандарт; альтернативы (Tomcat JDBC, DBCP2) — legacy.
- **ORM vs SQL:** jOOQ — когда нужен SQL-контроль и реляционная модель; Hibernate — для богатых агрегатов (см. [[README|databases/orm]]).
- **Observability:** Micrometer → Prometheus/VictoriaMetrics; OpenTelemetry — единый стандарт trace/metric/log (Sleuth deprecated в Spring Boot 3).
- **Resilience:** Resilience4j заменил Hystrix; Spring Cloud CircuitBreaker — это abstraction поверх него.

## Маршруты чтения

- **Production-чеклист сервиса:** HikariCP → Micrometer → Resilience4j → OpenTelemetry.
- **Test pyramid:** REST-Assured (E2E) → WireMock (контрактные) → Testcontainers (интеграционные).
- **Legacy-миграция:** Sleuth → OpenTelemetry; Hystrix → Resilience4j; Apache HttpClient 4 → 5.

## Куда идти дальше

- Утилитарные библиотеки — [[README|utility-libraries]]
- Сериализация — [[README|serialization]]
- Наблюдаемость — [[README|monitoring]]
- Тестирование — [[README|testing]]
