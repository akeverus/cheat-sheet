---
title: "Kotlin Libraries"
description: "Точка входа в раздел Kotlin-библиотек: Ktor, Exposed, kotlinx (coroutines, serialization, datetime), Arrow, Koin, MockK."
tags:
  - meta
  - index
  - kotlin
  - libraries
type: "index"
updated: "2026-04-20"
---
# Kotlin Libraries

Раздел собирает идиоматичные Kotlin-библиотеки для backend, инфраструктуры и тестов: Ktor (HTTP server/client), Exposed (SQL DSL/DAO), `kotlinx.coroutines` (структурная конкурентность), `kotlinx.serialization` (kotlin-native JSON/CBOR/ProtoBuf), `kotlinx.datetime`, Arrow (FP), Koin/Kodein (DI), MockK (тесты), Klaxon (JSON legacy), Konfig (конфиг).

Для кого: Kotlin-разработчики, подбирающие стек под backend или multiplatform-проект; Java-разработчики, оценивающие переход на Kotlin-native библиотеки вместо Jackson/Mockito/Spring.

## Полезные ссылки

### HTTP
- [Ktor](kotlin-ktor.md)

### Persistence
- [Exposed](kotlin-exposed.md)

### Concurrency и serialization
- [kotlinx.coroutines](kotlin-kotlinx-coroutines.md)
- [kotlinx.serialization](kotlin-kotlinx-serialization.md)
- [kotlinx.datetime](kotlin-kotlinx-datetime.md)

### Функциональное программирование
- [Arrow](kotlin-arrow.md) — Either, Option, Resource

### DI
- [Koin / Kodein](kotlin-kodein.md)

### Конфигурация
- [Konfig](kotlin-konfig.md)

### JSON legacy
- [Klaxon](kotlin-klaxon.md)

### Тестирование
- [MockK](kotlin-mockk.md)

### Соседние разделы
- [Libraries](../../basics/README.md)
- [Kotlin Frameworks](../../basics/README.md)
- [Kotlin (язык)](../../basics/README.md)
- [Java-библиотеки](../../basics/README.md)

## Содержание

- [Карта по задачам](#карта-по-задачам)
- [Kotlin-native vs Java-эквивалент](#kotlin-native-vs-java-эквивалент)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта по задачам

| Задача | Kotlin-native | Java-альтернатива |
|--------|---------------|-------------------|
| HTTP-сервер | Ktor | Spring Boot, Javalin |
| HTTP-клиент | Ktor Client | OkHttp, HttpClient |
| SQL DSL | Exposed | jOOQ |
| Конкурентность | kotlinx.coroutines | CompletableFuture, Reactor |
| JSON | kotlinx.serialization | Jackson, Gson |
| Даты | kotlinx.datetime | java.time |
| FP | Arrow | Vavr |
| DI | Koin, Kodein | Spring IoC, Guice |
| Конфиг | Konfig | Typesafe Config, Spring |
| Моки | MockK | Mockito |

## Kotlin-native vs Java-эквивалент

- **Плюсы Kotlin-native:** идиоматичный API (suspend, DSL), multiplatform-совместимость, меньше boilerplate.
- **Минусы:** менее зрелая интеграция со Spring Boot (Arrow Effect vs Reactor), меньше готовых stack-overflow-рецептов.
- **Правило:** если проект чистый Kotlin/multiplatform — берём kotlinx/Ktor/Exposed. Если проект — Spring Boot с немного Kotlin-кода, берём Java-библиотеки (Jackson, Mockito, Spring WebClient).

## Маршруты чтения

- **Kotlin backend с нуля:** `kotlin-ktor.md` `kotlin-exposed.md` `kotlin-kotlinx-coroutines.md` `kotlin-mockk.md`.
- **Kotlin в Spring Boot:** `kotlin-mockk.md` + `kotlin-kotlinx-coroutines.md` + [spring-webflux](../../frameworks/java-frameworks/spring/spring-webflux.md).
- **FP-путь:** `kotlin-arrow.md` `kotlin-kotlinx-coroutines.md` (Flow + Arrow Effect).

## Куда идти дальше

- Kotlin-фреймворки — [frameworks/kotlin-frameworks](../../basics/README.md)
- Язык Kotlin — [languages/kotlin](../../basics/README.md)
- Java-библиотеки — [libraries/java](../../basics/README.md)
