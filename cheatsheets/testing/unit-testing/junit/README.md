---
title: "JUnit"
description: "Точка входа в раздел JUnit: стандартный фреймворк для тестирования Java-приложений, а также AssertJ, Hamcrest и Mockito."
tags:
  - meta
  - index
  - testing
  - unit-testing
  - junit
type: "index"
aliases:
  - "JUnit"
prerequisites: []
next: []
updated: "2026-04-20"
---
# JUnit

JUnit 5 (Jupiter) — основной фреймворк для тестирования Java-приложений. Декларативные аннотации (`@Test`, `@BeforeEach`), богатые ассерты, параметризованные тесты, расширения (Extensions) для Spring, Mockito, Testcontainers. В этом разделе собраны сам JUnit, продвинутые приёмы, библиотеки ассертов (AssertJ, Hamcrest) и мок-фреймворк Mockito.

Применяйте как стартовый слой тестирования в любом Java-проекте. JUnit 5 — стандарт де-факто для JVM; альтернатива TestNG имеет смысл, когда нужны специфические фишки вроде групп тестов, зависимостей методов или встроенных HTML-отчётов для QA.

## Полезные ссылки

### Основные документы
- [JUnit 5](junit.md) — аннотации, ассерты, параметризация
- [JUnit Advanced](junit-advanced.md) — расширения, условия, порядок
- [assertj](assertj.md) — fluent-ассерты
- [hamcrest](hamcrest.md) — матчеры
- [mockito](mockito.md) — моки и стабы
- [Mockito Advanced](mockito-advanced.md) — аргументы, spy, verify

### Соседние разделы
- [Unit Testing](../../../basics/README.md)
- [TestNG](../../../basics/README.md)
- [Jest](../../../basics/README.md) / [pytest](../../../basics/README.md)
- [Testcontainers](../../../basics/README.md)
- [Spring Testing](../../../frameworks/java-frameworks/spring/spring-testing.md)

### Внешние ресурсы
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ](https://assertj.github.io/doc/)
- [Mockito](https://site.mockito.org/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: JUnit vs TestNG](#когда-использовать-junit-vs-testng)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Аннотации и жизненный цикл | [junit](junit.md) |
| Assertions, параметризация, dynamic tests | [junit](junit.md) |
| Extensions (Spring, Mockito, Testcontainers) | [junit-advanced](junit-advanced.md) |
| Fluent-ассерты и читаемость | [assertj](assertj.md), [hamcrest](hamcrest.md) |
| Мок-фреймворк: `mock`, `when`, `verify`, `@InjectMocks` | [mockito](mockito.md), [mockito-advanced](mockito-advanced.md) |

## Когда использовать: JUnit vs TestNG

| Аспект | **JUnit 5** | TestNG |
|--------|-------------|--------|
| Язык | Java (JVM) | Java (JVM) |
| Стиль ассертов | `assertEquals` + AssertJ/Hamcrest | `Assert.assertEquals` (JUnit-style) |
| Mocking | Mockito (внешняя) | Mockito (внешняя) |
| Параметризация | `@ParameterizedTest` + providers | `@DataProvider` |
| Группы / зависимости | через теги и условия | встроенные группы, `dependsOnMethods` |
| Конфигурация | аннотации + JUnit Platform | XML-suite + аннотации |
| Экосистема | стандарт в Spring, Maven Surefire | чаще в Selenium/UI-автоматизации |

JUnit 5 — выбор по умолчанию для большинства Java-проектов. TestNG — там, где нужны группы, сложные зависимости и XML-suites.

## Маршруты чтения

- **Быстрый старт (1 ч):** `junit.md` `assertj.md` `mockito.md`.
- **Боевой проект (1 день):** весь раздел + Testcontainers + Spring Testing.

## Куда идти дальше

- Обзор unit-тестирования — [README](../../../basics/README.md)
- Альтернативный JVM-фреймворк — [README](../../../basics/README.md)
- Интеграция с инфраструктурой — [README](../../../basics/README.md)
