---
title: "Javalin"
description: "Точка входа в раздел Javalin: лёгкий Java/Kotlin веб-фреймворк для REST и WebSocket без аннотаций и автоконфигурации."
tags:
  - meta
  - index
  - javalin
  - java-frameworks
type: "index"
aliases:
  - "Javalin"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Javalin

Javalin — минималистичный веб-фреймворк для Java и Kotlin поверх Jetty. Его цель — быть "как Express/Koa, но для JVM": программный API, никаких аннотаций, минимум магии. Приложение запускается одним `Javalin.create().start(7070)`, маршруты объявляются как lambdas, а контекст (`Context`) инкапсулирует запрос/ответ.

Для кого: стартапы и прототипы на JVM, образовательные проекты, Kotlin-команды, которым не нужен полный Spring Boot, и инженеры, ценящие читаемый исходник (~6 тыс строк против сотен тысяч у Spring).

## Полезные ссылки

### Основные документы
- [Javalin: Основы](javalin-basics.md) — роуты, контекст, WebSocket, JSON

### Соседние разделы
- [Java Frameworks](../../../basics/README.md) — сравнение со Spring/Quarkus/Micronaut/Vert.x/Dropwizard
- [Spring](../../../basics/README.md), [Dropwizard](../../../basics/README.md)
- [Kotlin Frameworks](../../../basics/README.md) — Javalin часто используют с Kotlin
- [Ktor](../../../libraries/kotlin/kotlin-ktor.md) — ближайший kotlin-native аналог

### Внешние ресурсы
- [Javalin Official Site](https://javalin.io)
- [Documentation](https://javalin.io/documentation)
- [GitHub](https://github.com/javalin/javalin)
- [Tutorials](https://javalin.io/tutorials)

## Содержание

- [Что внутри](#что-внутри)
- [Когда выбирать Javalin](#когда-выбирать-javalin)
- [Сравнение с соседями](#сравнение-с-соседями)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- Базовый HTTP-сервер: `get`, `post`, `put`, `delete`
- Контекст: path params, query params, body, JSON
- Middleware через `before`/`after`/`exception`
- WebSocket (через те же lambdas)
- Интеграция с Jackson/Gson/Moshi
- OpenAPI-плагин, SSE, Jetty-настройки

## Когда выбирать Javalin

- Нужен **самый** быстрый старт (<0.5 c) и минимальный footprint среди JVM-стеков с удобным API.
- Команда пишет на Kotlin и хочет сохранить "fluent"-стиль (альтернатива — Ktor).
- Проект маленький: REST-сервис на десяток эндпоинтов, bot-webhook, admin-утилита.
- **Не подходит** для больших монолитов — нет встроенного DI, автоконфигурации, batch/scheduling; всё это приходится собирать самостоятельно.

## Сравнение с соседями

| Критерий | Javalin | Spring Boot | Ktor | Dropwizard |
|----------|---------|-------------|------|-----------|
| Cold start | очень быстрый (<0.5 с) | средний (3-5 с) | быстрый (~1 с) | средний (~2 с) |
| RAM | ~40-50 MB | 200-300 MB | ~60 MB | ~150 MB |
| Аннотации | нет | много | нет | JAX-RS |
| DI | нет (bring your own: Koin, Guice) | Spring IoC | нет / Koin | через Guice bundle |
| Reactive | частично (Kotlin coroutines) | WebFlux | coroutines (встроено) | нет |

## Маршруты чтения

- **Прототип за час:** `javalin-basics.md` разделы «Простой HTTP сервер» + «JSON» + «WebSocket».
- **Kotlin-проект:** `javalin-basics.md` + [Ktor](../../../libraries/kotlin/kotlin-ktor.md) для сравнения.
- **Миграция с Spring:** секции «Контекст» + "middleware" + чек-лист расхождений в `../README.md`.

## Куда идти дальше

- Kotlin-экосистема — [Kotlin Frameworks](../../../basics/README.md)
- Сравнение с остальными JVM-фреймворками — [README](../../../basics/README.md)
- REST-контракты — [development/api/rest](../../../basics/README.md)
