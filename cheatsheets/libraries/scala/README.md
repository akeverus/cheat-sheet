---
title: "Scala Libraries"
description: "Точка входа в раздел Scala-библиотек: Akka, Cats, ZIO, Play, Slick, Doobie, Circe, ScalaTest."
tags:
  - meta
  - index
  - scala
  - libraries
type: "index"
updated: "2026-04-20"
---
# Scala Libraries

Раздел собирает ключевые Scala-библиотеки: Akka (actor model + streams), Cats и Cats Effect (type classes + effect system), ZIO (functional effects с R-каналом), Play (web framework), Slick (FRM) и Doobie (pure-FP JDBC), Circe (JSON) и ScalaTest (тесты). Большинство библиотек делятся на два лагеря — Akka/Play (императивный/reactive) и Typelevel/ZIO (функциональный).

Для кого: Scala-инженеры, подбирающие зависимости по задачам; инженеры других JVM-языков, которые хотят понять «функциональный JVM-стек» и его различия с Java-аналогами.

## Полезные ссылки

### Concurrency и эффекты
- [Akka](scala-akka.md)
- [Cats / Cats Effect](scala-cats.md)
- [ZIO](scala-zio.md)

### Web / REST
- [Play](scala-play.md)

### Persistence
- [Slick](scala-slick.md)
- [Doobie](scala-doobie.md)

### JSON
- [Circe](scala-circe.md)

### Testing
- [ScalaTest](scala-scalatest.md)

### Соседние разделы
- [Libraries](../../basics/README.md)
- [Scala Frameworks](../../basics/README.md)
- [Scala (язык)](../../basics/README.md)
- [Java-библиотеки](../../basics/README.md)

## Содержание

- [Карта по задачам](#карта-по-задачам)
- [Две экосистемы](#две-экосистемы)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта по задачам

| Задача | Библиотека |
|--------|------------|
| Actor model | Akka |
| Effect system | Cats Effect, ZIO |
| Type classes | Cats |
| Web framework | Play |
| FRM (functional-relational) | Slick |
| Pure JDBC | Doobie |
| JSON | Circe |
| Тесты | ScalaTest |

## Две экосистемы

- **Typelevel (Cats, Cats Effect, http4s, Doobie, Circe)** — чистый FP, pure-IO, type classes, implicits.
- **ZIO** — альтернативная FP-экосистема: `ZIO[R, E, A]` (окружение, ошибка, значение), свой набор интеграций (ZIO HTTP, ZIO JSON, Quill).
- **Akka** — не-FP; императивные актёры, Streams, Cluster, Persistence.
- Смешивать можно, но осторожно: ZIO и Cats Effect имеют бридж, Akka живёт отдельно.

## Маршруты чтения

- **FP-путь (Typelevel):** `scala-cats.md` `scala-circe.md` `scala-doobie.md`.
- **FP-путь (ZIO):** `scala-zio.md` модули ZIO (HTTP, JSON, Quill).
- **Enterprise/reactive:** `scala-akka.md` `scala-play.md` `scala-slick.md`.
- **Тесты:** `scala-scalatest.md` применим везде.

## Куда идти дальше

- Scala-фреймворки — [frameworks/scala-frameworks](../../basics/README.md)
- Язык Scala — [languages/scala](../../basics/README.md)
- Смежные JVM-библиотеки — [libraries/java](../../basics/README.md)
