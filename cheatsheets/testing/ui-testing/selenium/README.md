---
title: "Selenium"
description: "Точка входа в раздел Selenium: автоматизация браузера через WebDriver для UI-тестирования."
tags:
  - meta
  - index
  - testing
  - ui-testing
  - selenium
type: "index"
aliases:
  - "Selenium"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Selenium

Selenium WebDriver — де-факто стандарт автоматизации браузера для UI-тестов. Поддерживает Chrome, Firefox, Edge, Safari, работает на Java, Python, JavaScript, C#, Ruby. Даёт низкоуровневый API над браузером: навигация, поиск элементов, действия мыши/клавиатуры, ожидания, JS-выполнение.

Применяйте для end-to-end UI-тестов веб-приложений с богатой клиентской логикой, а также там, где нужна кросс-браузерность через Selenium Grid. Для современных SPA часто используют паттерн Page Object, явные ожидания и data-driven запуски.

## Полезные ссылки

### Основные документы
- [Selenium для Java](selenium.md) — настройка, Page Object, ожидания, параллельный запуск

### Соседние разделы
- [UI Testing](../../../basics/README.md)
- [JUnit](../../../basics/README.md)
- [TestNG](../../../basics/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)
- [REST Assured](../../integration-testing/rest-assured.md)

### Внешние ресурсы
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [WebDriver API](https://www.selenium.dev/documentation/webdriver/)
- [Waits](https://www.selenium.dev/documentation/webdriver/waits/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: Selenium vs Playwright vs Cypress](#когда-использовать-selenium-vs-playwright-vs-cypress)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Настройка WebDriver и зависимости Maven | [selenium](selenium.md) |
| Взаимодействия: click, type, select, actions | [selenium](selenium.md) |
| Page Object Model | [selenium](selenium.md) |
| Ожидания: implicit/explicit/fluent | [selenium](selenium.md) |
| Data-driven, параллельный запуск, Spring Boot | [selenium](selenium.md) |

## Когда использовать: Selenium vs Playwright vs Cypress

| Инструмент | Языки | Архитектура | Параллелизация | Сильные стороны |
|-----------|-------|-------------|----------------|-----------------|
| **Selenium** | Java, Python, JS, C#, Ruby | WebDriver протокол, отдельный процесс браузера | Selenium Grid | кросс-браузерность, зрелость, любой язык |
| Playwright | TS/JS, Python, Java, .NET | CDP + встроенные браузеры | встроенная (workers) | авто-ожидания, быстрый, стабильный |
| Cypress | JS/TS | в том же процессе браузера (iframe) | cypress-parallel / Dashboard | отличный DX, time-travel, debug |

Selenium — лучший выбор, когда команда уже на JVM/Java, нужна кросс-браузерность и интеграция с корпоративной инфраструктурой (Grid, Docker, Selenoid).

## Маршруты чтения

- **Быстрый старт (1 ч):** введение WebDriver setup первый тест explicit waits.
- **Боевой suite (1 день):** весь документ + Page Object + parallel + Selenoid/Grid.

## Куда идти дальше

- Обзор UI-тестирования — [README](../../../basics/README.md)
- JUnit/TestNG runners — [README](../../../basics/README.md)
- API-тестирование — [rest-assured](../../integration-testing/rest-assured.md)
