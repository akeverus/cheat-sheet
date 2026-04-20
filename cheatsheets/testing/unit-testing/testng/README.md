---
title: "TestNG"
description: "Точка входа в раздел TestNG: JVM-фреймворк с группами, зависимостями, XML-suite и встроенной параллельностью."
tags:
  - meta
  - index
  - testing
  - unit-testing
  - testng
type: "index"
updated: "2026-04-17"
---
# TestNG

TestNG — фреймворк для тестирования на JVM (Java и другие языки), расширяющий идеи JUnit: группы тестов, зависимости между методами (`dependsOnMethods`), параметризация через `@DataProvider` и XML-`@Parameters`, конфигурация как через аннотации, так и через XML-suite, параллельный запуск из коробки, встроенные HTML/XML-отчёты.

Применяйте там, где нужны сложные сценарии UI-автоматизации (часто с Selenium), гибкий порядок выполнения, группы smoke/regression, data-driven тесты и отчёты «как есть» для QA без внешних библиотек. В чистых backend-проектах обычно выбирают JUnit 5 как более стандартный и лучше интегрированный со Spring.

## Полезные ссылки

### Основные документы
- [[testng]] — аннотации, группы, DataProvider, параллельный запуск

### Соседние разделы
- [[README|Unit Testing]]
- [[README|JUnit]]
- [[README|Selenium]]
- [[testing-tools-overview|Testing Tools Overview]]
- [[java-mockito|Mockito]]

### Внешние ресурсы
- [TestNG Official](https://testng.org/doc/documentation-main.html)
- [Parameters and DataProviders](https://testng.org/doc/documentation-main.html#parameters)
- [Allure TestNG](https://docs.qameta.io/allure/#_testng)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: TestNG vs JUnit](#когда-использовать-testng-vs-junit)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Аннотации `@Test`, `@Before*`/`@After*` | [[testng]] |
| Группы и зависимости методов | [[testng]] |
| Параметры: `@Parameters` из XML, `@DataProvider` | [[testng]] |
| Параллельное выполнение, XML-suite | [[testng]] |
| Assertions, retry, listeners, Allure | [[testng]] |

## Когда использовать: TestNG vs JUnit

| Аспект | **TestNG** | JUnit 5 |
|--------|------------|---------|
| Язык | Java (JVM) | Java (JVM) |
| Стиль ассертов | `Assert.assertEquals` (JUnit-style) | `assertEquals` + AssertJ/Hamcrest |
| Mocking | Mockito (внешняя) | Mockito (внешняя) |
| Группы / зависимости | встроенные (`groups`, `dependsOnMethods`) | через теги и условия |
| Параметризация | `@DataProvider`, XML | `@ParameterizedTest` |
| Параллельность | из коробки (XML) | через `junit-platform.properties` |
| Отчёты | встроенные HTML/XML | нужны внешние (Surefire HTML) |
| Экосистема | Selenium-автоматизация, QA | стандарт в Spring/backend |

TestNG лучше для UI/QA-автоматизации с группами, порядком и встроенными отчётами; JUnit 5 — для backend и чистых модульных тестов.

## Маршруты чтения

- **Быстрый старт (1 ч):** `@Test` → `@BeforeClass/@AfterClass` → `@DataProvider` → XML-suite.
- **UI-автоматизация (1 день):** весь документ + Selenium + Allure + parallel.

## Куда идти дальше

- Обзор unit-тестирования — [[README]]
- JUnit 5 — [[README]]
- Selenium — [[README]]
