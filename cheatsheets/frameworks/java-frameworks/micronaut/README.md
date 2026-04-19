---
title: "Micronaut"
description: "Точка входа в раздел по Micronaut: JVM-фреймворк с compile-time DI, быстрым стартом, native-image поддержкой и богатой экосистемой для микросервисов."
tags:
  - meta
  - index
  - micronaut
  - java-frameworks
type: "index"
updated: "2026-04-17"
---
# Micronaut

Micronaut — JVM-фреймворк от команды Grails, ориентированный на микросервисы и serverless. Главная идея — compile-time DI и AOP: все бины, прокси и конфигурационные метаданные генерируются аннотационным процессором на этапе сборки, а не через reflection и classpath-сканирование. Это даёт быстрый старт, низкое потребление памяти и предсказуемый native image через GraalVM.

Для кого: команды, которым нужен lightweight-альтернатива Spring Boot без рантайм-магии, разработчики serverless-функций, а также те, кто пишет на Kotlin/Groovy с упором на AOT-компиляцию.

## Полезные ссылки

### Основные документы
- [Micronaut: основы](micronaut-basics.md) — введение, архитектура, первое приложение
- [Micronaut Core](micronaut-core.md) — DI, beans, AOP
- [Micronaut HTTP](micronaut-http.md), [Micronaut OpenAPI](micronaut-openapi.md)
- [Micronaut Data](micronaut-data.md), [Micronaut MongoDB](micronaut-mongodb.md), [Micronaut Flyway](micronaut-flyway.md)
- [Micronaut Reactive](micronaut-reactive.md), [Micronaut WebSocket](micronaut-websocket.md), [Micronaut gRPC](micronaut-grpc.md)
- [Micronaut Kafka](micronaut-kafka.md), [Micronaut JMS](micronaut-jms.md), [Micronaut Redis](micronaut-redis.md)
- [Micronaut Security](micronaut-security.md), [Micronaut Validation](micronaut-validation.md)
- [Micronaut Serialization](micronaut-serialization.md), [Micronaut Views](micronaut-views.md)
- [Micronaut GraalVM](micronaut-graalvm.md), [Micronaut Cloud](micronaut-cloud.md)
- [Micronaut Testing](micronaut-testing.md), [Micronaut Actuator](micronaut-actuator.md)
- [Micronaut Cache](micronaut-cache.md), [Micronaut Retry](micronaut-retry.md), [Micronaut Scheduling](micronaut-scheduling.md)
- [Micronaut Batch](micronaut-batch.md), [Micronaut Mail](micronaut-mail.md)
- [Micronaut i18n](micronaut-i18n.md), [Micronaut Multitenancy](micronaut-multitenancy.md), [Micronaut Logging](micronaut-logging.md)

### Соседние разделы
- [Java Frameworks](../README.md) — обзор и сравнение
- [Spring](../spring/README.md), [Quarkus](../quarkus/README.md), [Vert.x](../vertx/README.md)

### Внешние ресурсы
- [Micronaut Documentation](https://docs.micronaut.io/latest/guide/)
- [Micronaut Guides](https://guides.micronaut.io/)
- [Micronaut Launch](https://launch.micronaut.io/) — генератор проекта

## Содержание

- [Что такое Micronaut](#что-такое-micronaut)
- [Когда выбирать Micronaut](#когда-выбирать-micronaut)
- [Карта тем](#карта-тем)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что такое Micronaut

- **Compile-time DI/AOP** — зависимости и прокси собираются AP в момент javac; нет runtime-reflection и classpath-scan.
- **Low memory footprint** — типичный микросервис стартует за десятки миллисекунд.
- **Native image friendly** — минимум работы по reflection-config для GraalVM.
- **HTTP-клиент через интерфейс** — декларативные клиенты `@Client` подобно Feign/Retrofit.
- **Cloud-ready** — готовые интеграции с AWS, GCP, Azure, Oracle, Kubernetes.

## Когда выбирать Micronaut

- Хочется Spring-like DX, но без накладных расходов runtime-reflection.
- Цель — serverless/lambda, где важен cold start и RSS.
- Используется Kotlin/Groovy и нужна хорошая поддержка сразу нескольких JVM-языков.
- Не подходит: если команда сильно завязана на широкую Spring-экосистему (spring-security-oauth2-resource-server и т.п.), миграция потребует времени.

## Карта тем

| Слой | Документ |
|------|----------|
| Ядро и DI | [micronaut-basics.md](micronaut-basics.md), [micronaut-core.md](micronaut-core.md) |
| HTTP / REST | [micronaut-http.md](micronaut-http.md), [micronaut-openapi.md](micronaut-openapi.md), [micronaut-views.md](micronaut-views.md) |
| Persistence | [micronaut-data.md](micronaut-data.md), [micronaut-mongodb.md](micronaut-mongodb.md), [micronaut-flyway.md](micronaut-flyway.md) |
| Messaging | [micronaut-kafka.md](micronaut-kafka.md), [micronaut-jms.md](micronaut-jms.md) |
| Reactive / RPC | [micronaut-reactive.md](micronaut-reactive.md), [micronaut-grpc.md](micronaut-grpc.md), [micronaut-websocket.md](micronaut-websocket.md) |
| Security & Validation | [micronaut-security.md](micronaut-security.md), [micronaut-validation.md](micronaut-validation.md) |
| Native & Cloud | [micronaut-graalvm.md](micronaut-graalvm.md), [micronaut-cloud.md](micronaut-cloud.md) |
| Ops | [micronaut-actuator.md](micronaut-actuator.md), [micronaut-logging.md](micronaut-logging.md), [micronaut-cache.md](micronaut-cache.md), [micronaut-retry.md](micronaut-retry.md) |
| DX / Testing | [micronaut-testing.md](micronaut-testing.md) |

## Маршруты чтения

- **Быстрый старт:** `micronaut-basics.md` → `micronaut-http.md` → `micronaut-data.md` → `micronaut-testing.md`.
- **Переход со Spring Boot:** `micronaut-basics.md` (секция сравнения) → `micronaut-core.md` → `micronaut-security.md`.
- **Native image для FaaS:** `micronaut-graalvm.md` → `micronaut-cloud.md`.
- **Reactive и события:** `micronaut-reactive.md` → `micronaut-kafka.md` → `micronaut-grpc.md`.

## Куда идти дальше

- Сравнение с соседями — [../README.md](../README.md)
- GraalVM native image — [micronaut-graalvm.md](micronaut-graalvm.md)
- Kotlin-экосистема — [../../kotlin-frameworks/README.md](../../kotlin-frameworks/README.md)
