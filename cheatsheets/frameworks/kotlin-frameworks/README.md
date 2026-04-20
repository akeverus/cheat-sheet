---
title: "Kotlin Frameworks"
description: "Точка входа в раздел Kotlin-фреймворков: Ktor, Spring (Kotlin), Exposed, kotlinx, Koin — карта и маршруты."
tags:
  - meta
  - index
  - kotlin-frameworks
type: "index"
updated: "2026-04-20"
---
# Kotlin Frameworks

Раздел описывает ключевые фреймворки и библиотеки для Kotlin-бэкенда: Ktor (HTTP-сервер/клиент), Spring с Kotlin-DSL, Exposed (SQL DSL/DAO), `kotlinx.coroutines`, `kotlinx.serialization`, Koin/Kodein (DI). Основная идея — показать идиоматичные для Kotlin стеки и как они соотносятся с Java-аналогами.

Для кого: Kotlin-разработчики, выбирающие стек backend-сервиса, Java-инженеры, мигрирующие команды на Kotlin, и те, кто хочет использовать coroutines и type-safe DSL вместо Spring-аннотаций.

## Полезные ссылки

### Основные документы
- [Kotlin Frameworks — обзор](kotlin-frameworks-overview.md) — карта экосистемы

### Соседние разделы
- [Frameworks](../../basics/README.md)
- [Java Frameworks](../../basics/README.md) — Spring, Quarkus, Micronaut (работают и с Kotlin)
- [Kotlin (язык)](../../basics/README.md)
- [Ktor](../../libraries/kotlin/kotlin-ktor.md) — полное руководство
- [Exposed](../../libraries/kotlin/kotlin-exposed.md)
- [kotlinx.coroutines](../../libraries/kotlin/kotlin-kotlinx-coroutines.md)
- [kotlinx.serialization](../../libraries/kotlin/kotlin-kotlinx-serialization.md)
- [Koin](../../libraries/kotlin/kotlin-kodein.md)

### Внешние ресурсы
- [Kotlin Docs](https://kotlinlang.org/docs/)
- [Ktor](https://ktor.io/)
- [Exposed](https://github.com/JetBrains/Exposed)
- [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)

## Содержание

- [Что внутри](#что-внутри)
- [Когда выбирать kotlin-native стек](#когда-выбирать-kotlin-native-стек)
- [Сравнение вариантов](#сравнение-вариантов)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Ktor: маршрутизация, плагины, клиент, WebSocket
- Spring с Kotlin: DSL для Web, Beans, RouterFunction
- Exposed: DSL и DAO API
- kotlinx.coroutines, Flow
- kotlinx.serialization vs Jackson/Gson
- Koin / Kodein: легковесный DI

## Когда выбирать kotlin-native стек

- Команда полностью на Kotlin и хочет идиоматичный код: DSL, coroutines, data classes.
- Нужна лёгкая альтернатива Spring (Ktor + Koin + Exposed ≈ 40 MB RAM и <1 c cold start).
- Важна интеграция с Android/multiplatform.
- Альтернативы: **Spring Boot** (если нужна широкая экосистема), **Micronaut/Quarkus** (для AOT/native), **Javalin** (ещё легче, но без coroutines-first API).

## Сравнение вариантов

| Стек | Фокус | Cold start | DI | Идиоматичность для Kotlin |
|------|-------|------------|-----|---------------------------|
| Ktor + Koin + Exposed | lightweight HTTP + SQL DSL | ~1 с | Koin (runtime) | очень высокая |
| Spring Boot (Kotlin) | широкая экосистема, enterprise | 3-5 с | Spring IoC | средняя (много аннотаций) |
| Micronaut (Kotlin) | compile-time DI, native | <1 с | compile-time | высокая |
| Quarkus (Kotlin) | native, serverless | <1 с / ~30 мс native | Arc (CDI) | средняя |
| Http4k (Kotlin) | functional, server as function | <1 с | нет | высокая |

## Маршруты чтения

- **Kotlin-first backend за день:** `kotlin-frameworks-overview.md` [Ktor](../../libraries/kotlin/kotlin-ktor.md) [Exposed](../../libraries/kotlin/kotlin-exposed.md).
- **Kotlin поверх Spring:** раздел "Spring с Kotlin" в overview + [Spring](../../basics/README.md).
- **Мигрант с Java:** `kotlin-frameworks-overview.md` сравнение с Java Frameworks.

## Куда идти дальше

- Язык Kotlin — [languages/kotlin](../../basics/README.md)
- Kotlin-библиотеки — [libraries/kotlin](../../basics/README.md)
- Общее сравнение JVM-стеков — [java-frameworks](../../basics/README.md)
