---
title: "Quarkus"
description: "Точка входа в раздел по Quarkus: Kubernetes-native Java фреймворк с быстрым стартом, GraalVM native image, reactive-стеком и набором extensions."
tags:
  - meta
  - index
  - quarkus
  - java-frameworks
type: "index"
updated: "2026-04-17"
---
# Quarkus

Quarkus — Kubernetes-native Java фреймворк от Red Hat, ориентированный на supersonic/subatomic Java: минимальное потребление памяти, быстрый старт в JVM-режиме и компиляция в native image через GraalVM/Mandrel. Строится вокруг стандартов Jakarta EE и MicroProfile, использует compile-time DI (Arc) и build-time-обработку bytecode, что делает приложения особенно удобными для serverless и контейнеров.

Для кого: Java-разработчики, которым важны cold start и memory footprint (FaaS, k8s, edge), команды, переходящие с Spring Boot на более легковесный стек, и энтузиасты reactive/Mutiny.

## Полезные ссылки

### Основные документы
- [[quarkus-basics|Quarkus: основы]] — точка входа, архитектура, первый проект
- [[quarkus-core|Quarkus Core]] — DI, конфигурация, расширения
- [[quarkus-rest|Quarkus REST]] — RESTEasy Reactive, JSON-сериализация
- [[quarkus-reactive|Quarkus Reactive]] — Mutiny, reactive endpoints
- [[quarkus-data|Quarkus Data]] — Hibernate ORM with Panache
- [[quarkus-graalvm|Quarkus GraalVM]] — native image, reflection, resources
- [[quarkus-security|Quarkus Security]] — JWT, OIDC, RBAC
- [[quarkus-kafka|Quarkus Kafka]], [[quarkus-redis|Quarkus Redis]], [[quarkus-mongodb|Quarkus MongoDB]]
- [[quarkus-testing|Quarkus Testing]] — `@QuarkusTest`, DevServices
- [[quarkus-dev-services|Quarkus Dev Services]], [[quarkus-cloud|Quarkus Cloud]], [[quarkus-actuator|Quarkus Actuator]]
- [[quarkus-cache|Quarkus Cache]], [[quarkus-validation|Quarkus Validation]], [[quarkus-logging|Quarkus Logging]]
- [[quarkus-openapi|Quarkus OpenAPI]], [[quarkus-scheduling|Quarkus Scheduling]], [[quarkus-mail|Quarkus Mail]]
- [[quarkus-grpc|Quarkus gRPC]], [[quarkus-websocket|Quarkus WebSocket]], [[quarkus-qute|Quarkus Qute]]

### Соседние разделы
- [[README|Java Frameworks]] — обзор и сравнение JVM-фреймворков
- [[README|Spring]] — основной конкурент на JVM
- [[README|Micronaut]] — альтернатива с compile-time DI
- [[README|Vert.x]] — reactive-toolkit, на котором стоит Quarkus Reactive

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
| Архитектура и build-time | [[quarkus-basics]], [[quarkus-core]] |
| HTTP/REST | [[quarkus-rest]], [[quarkus-openapi]] |
| Reactive | [[quarkus-reactive]], [[quarkus-websocket]], [[quarkus-grpc]] |
| Persistence | [[quarkus-data]], [[quarkus-mongodb]], [[quarkus-redis]] |
| Messaging | [[quarkus-kafka]] |
| Security | [[quarkus-security]] |
| Observability | [[quarkus-actuator]], [[quarkus-logging]] |
| Native & Cloud | [[quarkus-graalvm]], [[quarkus-cloud]] |
| DX и тесты | [[quarkus-dev-services]], [[quarkus-testing]] |

## Маршруты чтения

- **Быстрый старт (1 день):** `quarkus-basics.md` → `quarkus-rest.md` → `quarkus-data.md` → `quarkus-testing.md`.
- **Переход со Spring Boot:** `quarkus-basics.md` → сравнение в [[README]] → `quarkus-core.md` → `quarkus-data.md` → `quarkus-security.md`.
- **Native image для прода:** `quarkus-graalvm.md` → `quarkus-cloud.md` → `quarkus-actuator.md`.
- **Reactive-пайплайны:** `quarkus-reactive.md` → `quarkus-kafka.md` → `quarkus-grpc.md`.

## Куда идти дальше

- Общее сравнение с Spring/Micronaut/Vert.x — [[README]]
- GraalVM и native image — [[quarkus-graalvm]]
- Kubernetes-деплой — [[README]]
- Наблюдаемость — [[README]]
