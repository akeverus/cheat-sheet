---
title: "Vert.x"
description: "Точка входа в раздел Vert.x: реактивный polyglot-toolkit для JVM с event-loop, Verticle и неблокирующими клиентами."
tags:
  - meta
  - index
  - vertx
  - java-frameworks
type: "index"
updated: "2026-04-17"
---
# Vert.x

Eclipse Vert.x — реактивный event-driven toolkit для JVM. Архитектура построена вокруг event loop (по ядру CPU) и легковесных Verticle — изолированных акторов, общающихся через Event Bus. Vert.x не навязывает фреймворковый каркас: это набор модулей (Web, SQL Client, Kafka, gRPC), которые можно комбинировать. Под капотом у Quarkus Reactive и частично у Spring WebFlux.

Для кого: команды, проектирующие high-throughput системы с I/O-bound нагрузкой (API gateway, прокси, стримы событий), инженеры, знакомые с Node.js event loop, и те, кому нужен polyglot (Java, Kotlin, Scala, Groovy, Ruby, JS).

## Полезные ссылки

### Основные документы
- [Vert.x: Основы](vertx-basics.md) — Verticle, Event Bus, Future, HTTP, JSON

### Соседние разделы
- [Java Frameworks](../README.md)
- [Quarkus](../quarkus/README.md) — построен на Vert.x
- [Spring WebFlux](../spring/spring-webflux.md)
- [Akka](../../../libraries/scala/scala-akka.md) — модель акторов в Scala

### Внешние ресурсы
- [Vert.x Docs](https://vertx.io/docs/)
- [Vert.x Java API](https://vertx.io/docs/apidocs/)
- [Vert.x GitHub](https://github.com/eclipse-vertx/vert.x)
- [Guide for Java Developers](https://vertx.io/docs/guide-for-java-devs/)
- [Examples](https://github.com/vert-x3/vertx-examples)

## Содержание

- [Что внутри](#что-внутри)
- [Когда выбирать Vert.x](#когда-выбирать-vertx)
- [Сравнение с соседями](#сравнение-с-соседями)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Модель **event loop**: golden rule "не блокируй event loop"
- **Verticle** и их deployment (standard vs worker)
- **Event Bus** — локальный и кластерный pub/sub
- **Future/Promise** и связки с Mutiny/RxJava/Kotlin coroutines
- Vert.x Web: роуты, middleware, body handler
- Клиенты: SQL, Kafka, Redis, gRPC, WebSocket

## Когда выбирать Vert.x

- Нужен максимальный throughput I/O-bound сервиса при малом числе потоков.
- Проектируется API gateway, шлюз протоколов, стриминговый прокси.
- Важна языковая гибкость (Kotlin/Scala на одном ядре).
- **Не подходит**, если команда хочет императивный blocking-стиль или много готовых starters → бери Spring Boot; если нужен полный AOT/native → Quarkus.

## Сравнение с соседями

| Критерий | Vert.x | Spring WebFlux | Quarkus Reactive | Akka |
|----------|--------|----------------|------------------|------|
| Cold start | быстрый (~1 с) | средний (3-5 с) | быстрый / native ~20 мс | медленный (~3-5 с) |
| RAM | ~80 MB | ~200 MB | ~80 MB / native ~40 MB | ~200 MB |
| Reactive | event loop + Future/Mutiny | Reactor Netty | Mutiny поверх Vert.x | Actors + Streams |
| Уровень абстракции | toolkit (низкий) | framework (средний) | framework (средний) | actor model (высокий) |
| Polyglot | Java, Kotlin, Scala, JS, Ruby, Groovy | Java, Kotlin | Java, Kotlin, Scala | Java, Scala |

## Маршруты чтения

- **Быстрый старт:** `vertx-basics.md` → "HTTP Server" + "Event Bus" + "AsyncResult и Future".
- **Проектирование API gateway:** `vertx-basics.md` → расширение через Vert.x Web + Kafka client.
- **Квест "а что под Quarkus":** `vertx-basics.md` → [Quarkus Reactive](../quarkus/quarkus-reactive.md).

## Куда идти дальше

- Сравнение JVM-фреймворков — [../README.md](../README.md)
- Reactive patterns — [patterns](../../../patterns/README.md)
- Messaging — [development/messaging](../../../development/messaging/README.md)
