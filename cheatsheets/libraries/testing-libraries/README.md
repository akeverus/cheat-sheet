---
title: "Testing Libraries"
description: "Точка входа в раздел тестовых библиотек JVM: JUnit 5 и Mockito."
tags:
  - meta
  - index
  - testing
  - libraries
type: "index"
updated: "2026-04-20"
---
# Testing Libraries

Раздел собирает базовые Java-библиотеки для unit-тестов: **JUnit 5** (Jupiter) — современный тестовый фреймворк JVM со своим расширяемым API, и **Mockito** — индустриальный стандарт для моков и стабов. Вместе они покрывают подавляющее большинство unit-тестов в Java-проектах; для Kotlin параллельно живёт [[kotlin-mockk|MockK]].

Для кого: Java-разработчики, пишущие unit/integration-тесты; инженеры, мигрирующие с JUnit 4 на Jupiter; те, кто хочет грамотно применять `@MockBean`, `@Spy`, parameterized/dynamic tests, Extensions.

## Полезные ссылки

### Основные документы
- [[java-junit5|JUnit 5]] — Jupiter, Assertions, Lifecycle, Extensions, Parameterized/Dynamic tests
- [[java-mockito|Mockito]] — моки, стабы, verify, `@Spy`, argument captors

### Соседние разделы
- [[README|Libraries]]
- [[README|Java-библиотеки]] — Testcontainers, WireMock, REST-Assured
- [[kotlin-mockk|MockK (Kotlin)]]
- [[README|Testing (методология)]]
- [[README|Unit testing]]

### Внешние ресурсы
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito](https://site.mockito.org/)

## Содержание

- [Что внутри](#что-внутри)
- [Границы библиотек](#границы-библиотек)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

- JUnit 5: Jupiter API, жизненный цикл, вложенные классы, conditional execution
- Parameterized tests (`@ParameterizedTest`, `@CsvSource`, `@MethodSource`)
- Dynamic tests (`@TestFactory`)
- Extensions API (`@ExtendWith`) и интеграция со Spring
- Mockito: `when/thenReturn`, `verify`, `ArgumentCaptor`, `@Spy` vs `@Mock`
- MockitoExtension для Jupiter
- Spring Boot интеграция (`@MockBean`, `@SpyBean`) — см. [[spring-testing]]

## Границы библиотек

- **JUnit 5** — запуск тестов, assertions, параметризация.
- **Mockito** — изоляция зависимостей через моки.
- **AssertJ / Hamcrest** — fluent-assertions поверх JUnit (из коробки нет в этой папке, но часто добавляется).
- **Testcontainers** — тесты с реальными зависимостями (см. [[java-testcontainers]]).
- **WireMock** — HTTP-моки (см. [[java-wiremock]]).
- **REST-Assured** — E2E для REST (см. [[java-rest-assured]]).

## Маршруты чтения

- **Быстрый старт:** `java-junit5.md` (Основы + Assertions) `java-mockito.md` (Основы + verify).
- **Миграция с JUnit 4:** секции "Jupiter vs Vintage" и таблица соответствий аннотаций.
- **Parameterized и dynamic:** соответствующие секции `java-junit5.md` — сильно сокращают дубли тестов.
- **Spring-контекст:** `java-mockito.md` [[spring-testing]].

## Куда идти дальше

- Интеграционные тесты — [[java-testcontainers|libraries/java/java-testcontainers.md]]
- Методология тестирования — [[README|testing/]]
- Kotlin-моки — [[kotlin-mockk|MockK]]
