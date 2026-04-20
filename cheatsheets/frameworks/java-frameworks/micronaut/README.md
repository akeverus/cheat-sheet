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
- [[micronaut-basics|Micronaut: основы]] — введение, архитектура, первое приложение
- [[micronaut-core|Micronaut Core]] — DI, beans, AOP
- [[micronaut-http|Micronaut HTTP]], [[micronaut-openapi|Micronaut OpenAPI]]
- [[micronaut-data|Micronaut Data]], [[micronaut-mongodb|Micronaut MongoDB]], [[micronaut-flyway|Micronaut Flyway]]
- [[micronaut-reactive|Micronaut Reactive]], [[micronaut-websocket|Micronaut WebSocket]], [[micronaut-grpc|Micronaut gRPC]]
- [[micronaut-kafka|Micronaut Kafka]], [[micronaut-jms|Micronaut JMS]], [[micronaut-redis|Micronaut Redis]]
- [[micronaut-security|Micronaut Security]], [[micronaut-validation|Micronaut Validation]]
- [[micronaut-serialization|Micronaut Serialization]], [[micronaut-views|Micronaut Views]]
- [[micronaut-graalvm|Micronaut GraalVM]], [[micronaut-cloud|Micronaut Cloud]]
- [[micronaut-testing|Micronaut Testing]], [[micronaut-actuator|Micronaut Actuator]]
- [[micronaut-cache|Micronaut Cache]], [[micronaut-retry|Micronaut Retry]], [[micronaut-scheduling|Micronaut Scheduling]]
- [[micronaut-batch|Micronaut Batch]], [[micronaut-mail|Micronaut Mail]]
- [[micronaut-i18n|Micronaut i18n]], [[micronaut-multitenancy|Micronaut Multitenancy]], [[micronaut-logging|Micronaut Logging]]

### Соседние разделы
- [[README|Java Frameworks]] — обзор и сравнение
- [[README|Spring]], [[README|Quarkus]], [[README|Vert.x]]

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
| Ядро и DI | [[micronaut-basics]], [[micronaut-core]] |
| HTTP / REST | [[micronaut-http]], [[micronaut-openapi]], [[micronaut-views]] |
| Persistence | [[micronaut-data]], [[micronaut-mongodb]], [[micronaut-flyway]] |
| Messaging | [[micronaut-kafka]], [[micronaut-jms]] |
| Reactive / RPC | [[micronaut-reactive]], [[micronaut-grpc]], [[micronaut-websocket]] |
| Security & Validation | [[micronaut-security]], [[micronaut-validation]] |
| Native & Cloud | [[micronaut-graalvm]], [[micronaut-cloud]] |
| Ops | [[micronaut-actuator]], [[micronaut-logging]], [[micronaut-cache]], [[micronaut-retry]] |
| DX / Testing | [[micronaut-testing]] |

## Маршруты чтения

- **Быстрый старт:** `micronaut-basics.md` → `micronaut-http.md` → `micronaut-data.md` → `micronaut-testing.md`.
- **Переход со Spring Boot:** `micronaut-basics.md` (секция сравнения) → `micronaut-core.md` → `micronaut-security.md`.
- **Native image для FaaS:** `micronaut-graalvm.md` → `micronaut-cloud.md`.
- **Reactive и события:** `micronaut-reactive.md` → `micronaut-kafka.md` → `micronaut-grpc.md`.

## Куда идти дальше

- Сравнение с соседями — [[README]]
- GraalVM native image — [[micronaut-graalvm]]
- Kotlin-экосистема — [[README]]
