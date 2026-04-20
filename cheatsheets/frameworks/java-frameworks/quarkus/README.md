---
title: "Quarkus"
description: "Точка входа в раздел по Quarkus: Kubernetes-native Java фреймворк с быстрым стартом, GraalVM native image, reactive-стеком и набором extensions."
tags:
  - meta
  - index
  - quarkus
  - java-frameworks
type: "index"
updated: "2026-04-20"
---
# Quarkus

Quarkus — Kubernetes-native Java фреймворк от Red Hat, ориентированный на supersonic/subatomic Java: минимальное потребление памяти, быстрый старт в JVM-режиме и компиляция в native image через GraalVM/Mandrel. Строится вокруг стандартов Jakarta EE и MicroProfile, использует compile-time DI (Arc) и build-time-обработку bytecode, что делает приложения особенно удобными для serverless и контейнеров.

Для кого: Java-разработчики, которым важны cold start и memory footprint (FaaS, k8s, edge), команды, переходящие с Spring Boot на более легковесный стек, и энтузиасты reactive/Mutiny.

## Полезные ссылки

### Основные документы
- [Quarkus: основы](quarkus-basics.md) — точка входа, архитектура, первый проект
- [Quarkus Core](quarkus-core.md) — DI, конфигурация, расширения
- [Quarkus REST](quarkus-rest.md) — RESTEasy Reactive, JSON-сериализация
- [Quarkus Reactive](quarkus-reactive.md) — Mutiny, reactive endpoints
- [Quarkus Data](quarkus-data.md) — Hibernate ORM with Panache
- [Quarkus GraalVM](quarkus-graalvm.md) — native image, reflection, resources
- [Quarkus Security](quarkus-security.md) — JWT, OIDC, RBAC
- [Quarkus Kafka](quarkus-kafka.md), [Quarkus Redis](quarkus-redis.md), [Quarkus MongoDB](quarkus-mongodb.md)
- [Quarkus Testing](quarkus-testing.md) — `@QuarkusTest`, DevServices
- [Quarkus Dev Services](quarkus-dev-services.md), [Quarkus Cloud](quarkus-cloud.md), [Quarkus Actuator](quarkus-actuator.md)
- [Quarkus Cache](quarkus-cache.md), [Quarkus Validation](quarkus-validation.md), [Quarkus Logging](quarkus-logging.md)
- [Quarkus OpenAPI](quarkus-openapi.md), [Quarkus Scheduling](quarkus-scheduling.md), [Quarkus Mail](quarkus-mail.md)
- [Quarkus gRPC](quarkus-grpc.md), [Quarkus WebSocket](quarkus-websocket.md), [Quarkus Qute](quarkus-qute.md)

### Соседние разделы
- [Java Frameworks](../../../basics/README.md) — обзор и сравнение JVM-фреймворков
- [Spring](../../../basics/README.md) — основной конкурент на JVM
- [Micronaut](../../../basics/README.md) — альтернатива с compile-time DI
- [Vert.x](../../../basics/README.md) — reactive-toolkit, на котором стоит Quarkus Reactive

### Внешние ресурсы
- [Официальные guides](https://quarkus.io/guides/)
- [Quarkus GitHub](https://github.com/quarkusio/quarkus)
- [Quarkiverse](https://github.com/quarkiverse) — community-extensions

## Содержание

- [Что такое Quarkus](#что-такое-quarkus)
- [Когда выбирать Quarkus](#когда-выбирать-quarkus)
- [Карта тем](#карта-тем)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что такое Quarkus

- **Build-time metadata** — DI-граф, конфигурация и reflection-метаданные вычисляются в момент сборки.
- **Arc** — CDI-lite контейнер без runtime-сканирования classpath.
- **RESTEasy Reactive** — нон-блокирующий JAX-RS поверх Vert.x.
- **Panache** — active record и repository-стиль поверх Hibernate ORM и MongoDB.
- **Native image** — компиляция в single-binary через GraalVM/Mandrel.
- **Dev Mode** — hot reload, continuous testing, Dev UI, Dev Services (автоматические Testcontainers).

## Когда выбирать Quarkus

- Нужен быстрый cold start в k8s/FaaS и низкий RSS (native image ~30-50 MB памяти).
- Команда знакома с Jakarta EE/MicroProfile, а не Spring.
- Требуется reactive-стек (Mutiny + Vert.x) из коробки.
- Не требуется (пока): максимально широкая экосистема готовых стартеров — Spring всё ещё шире.

## Карта тем

| Слой | Документ |
|------|----------|
| Архитектура и build-time | [quarkus-basics](quarkus-basics.md), [quarkus-core](quarkus-core.md) |
| HTTP/REST | [quarkus-rest](quarkus-rest.md), [quarkus-openapi](quarkus-openapi.md) |
| Reactive | [quarkus-reactive](quarkus-reactive.md), [quarkus-websocket](quarkus-websocket.md), [quarkus-grpc](quarkus-grpc.md) |
| Persistence | [quarkus-data](quarkus-data.md), [quarkus-mongodb](quarkus-mongodb.md), [quarkus-redis](quarkus-redis.md) |
| Messaging | [quarkus-kafka](quarkus-kafka.md) |
| Security | [quarkus-security](quarkus-security.md) |
| Observability | [quarkus-actuator](quarkus-actuator.md), [quarkus-logging](quarkus-logging.md) |
| Native & Cloud | [quarkus-graalvm](quarkus-graalvm.md), [quarkus-cloud](quarkus-cloud.md) |
| DX и тесты | [quarkus-dev-services](quarkus-dev-services.md), [quarkus-testing](quarkus-testing.md) |

## Маршруты чтения

- **Быстрый старт (1 день):** `quarkus-basics.md` `quarkus-rest.md` `quarkus-data.md` `quarkus-testing.md`.
- **Переход со Spring Boot:** `quarkus-basics.md` сравнение в [README](../../../basics/README.md) `quarkus-core.md` `quarkus-data.md` `quarkus-security.md`.
- **Native image для прода:** `quarkus-graalvm.md` `quarkus-cloud.md` `quarkus-actuator.md`.
- **Reactive-пайплайны:** `quarkus-reactive.md` `quarkus-kafka.md` `quarkus-grpc.md`.

## Куда идти дальше

- Общее сравнение с Spring/Micronaut/Vert.x — [README](../../../basics/README.md)
- GraalVM и native image — [quarkus-graalvm](quarkus-graalvm.md)
- Kubernetes-деплой — [README](../../../basics/README.md)
- Наблюдаемость — [README](../../../basics/README.md)
