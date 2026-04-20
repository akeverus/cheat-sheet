---
title: "Scala Frameworks"
description: "Точка входа в раздел Scala-фреймворков: Play, Akka, ZIO, http4s, Cats Effect, Tapir, reactive и FP-практики."
tags:
  - meta
  - index
  - scala-frameworks
type: "index"
updated: "2026-04-20"
---
# Scala Frameworks

Раздел собирает основные фреймворки и библиотеки Scala-экосистемы: Play (MVC/REST), Akka и Akka HTTP (akторы и HTTP), ZIO и ZIO HTTP (functional effects), http4s (FP-first HTTP на Cats Effect), Tapir (endpoint descriptions). Большинство стеков — функциональные: effect types (IO, ZIO, Task), type classes, no-reflection DI через implicits/`given`.

Для кого: Scala-инженеры, выбирающие стек для нового сервиса, и Java/Kotlin-разработчики, желающие понять, чем functional Scala отличается от Spring/WebFlux.

## Полезные ссылки

### Основные документы
- [Scala Frameworks — обзор](scala-frameworks-overview.md) — карта экосистемы и сравнение

### Соседние разделы
- [Frameworks](../../basics/README.md)
- [Java Frameworks](../../basics/README.md)
- [Scala (язык)](../../basics/README.md)
- [Scala-библиотеки](../../basics/README.md) — Akka, Cats, ZIO, Play, Slick, Doobie, Circe

### Внешние ресурсы
- [Play Framework](https://www.playframework.com/)
- [Akka](https://akka.io/)
- [ZIO](https://zio.dev/)
- [http4s](https://http4s.org/)
- [Cats Effect](https://typelevel.org/cats-effect/)
- [Tapir](https://tapir.softwaremill.com/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда выбирать Scala-стек](#когда-выбирать-scala-стек)
- [Сравнение подходов](#сравнение-подходов)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Play Framework: MVC, async actions, WS-клиент
- Akka и Akka HTTP: акторная модель, Streams
- ZIO: effects, dependency injection через layers
- http4s + Cats Effect: server-as-function, FP pipeline
- Tapir: единый DSL для REST-контрактов + OpenAPI
- Сравнение паттернов: effect system vs reactive streams

## Когда выбирать Scala-стек

- Команда уже пишет на Scala и хочет идиоматичный FP-подход.
- Нужна строгая типизация эффектов (ZIO/Cats Effect) с явным разделением pure и side-effect кода.
- Проект — data-intensive pipeline (Akka Streams, fs2).
- **Не подходит**, если команда не готова к функциональной парадигме — кривая обучения крутая; для большинства enterprise-задач проще Spring или Kotlin.

## Сравнение подходов

| Стек | Парадигма | Effect system | Сильная сторона |
|------|-----------|---------------|-----------------|
| Play | MVC/reactive | Future | батарейки включены, шаблоны |
| Akka HTTP | actor-based streams | Future | высокий throughput, Streams |
| ZIO HTTP | functional effects | ZIO[R, E, A] | type-safe effects, modularity |
| http4s | functional, server-as-function | Cats Effect IO | минимализм, composability |
| Tapir | описание эндпоинтов | любой (pluggable) | single source of truth + OpenAPI |

## Маршруты чтения

- **Обзор экосистемы:** `scala-frameworks-overview.md` целиком.
- **FP-путь:** `scala-frameworks-overview.md` (ZIO/http4s) [scala-cats](../../libraries/scala/scala-cats.md) [scala-zio](../../libraries/scala/scala-zio.md).
- **Enterprise/реактивный путь:** `scala-frameworks-overview.md` (Play/Akka) [scala-akka](../../libraries/scala/scala-akka.md).

## Куда идти дальше

- Библиотеки Scala — [libraries/scala](../../basics/README.md)
- Язык Scala — [languages/scala](../../basics/README.md)
- Сравнение с Java-стеком — [java-frameworks](../../basics/README.md)
