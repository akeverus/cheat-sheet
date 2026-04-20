---
title: "Jest"
description: "Точка входа в раздел Jest: unit- и интеграционные тесты для JavaScript/TypeScript."
tags:
  - meta
  - index
  - testing
  - unit-testing
  - jest
type: "index"
updated: "2026-04-20"
---
# Jest

Jest — фреймворк для юнит- и интеграционных тестов в экосистеме JavaScript/TypeScript от Meta. Включает встроенные моки (`jest.fn()`, `jest.mock()`), снапшот-тестирование, покрытие (Istanbul), поддержку ES Modules и TypeScript. Часто используется с React (React Testing Library), Vue, Node.js.

Применяйте в JS/TS-проектах, где хочется «батарейки в комплекте»: тест-раннер, ассерты, моки, покрытие и watch-режим без сборки зоопарка библиотек. Для чистого Node или Vite-стека иногда выбирают Vitest (Jest-совместимый API, быстрее), но Jest остаётся стандартом де-факто в React-экосистеме.

## Полезные ссылки

### Основные документы
- [jest](jest.md) — ассерты, моки, снапшоты, покрытие, CI/CD

### Соседние разделы
- [Unit Testing](../../../basics/README.md)
- [JUnit](../../../basics/README.md)
- [pytest](../../../basics/README.md)
- [xUnit.net](../../../basics/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)

### Внешние ресурсы
- [Jest Docs](https://jestjs.io/docs/getting-started)
- [Expect API](https://jestjs.io/docs/expect)
- [Testing Library (React)](https://testing-library.com/docs/react-testing-library/intro/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: сравнение unit-фреймворков](#когда-использовать-сравнение-unit-фреймворков)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| `describe`, `test`, `expect` и матчеры | [jest](jest.md) |
| Моки и шпионы: `jest.fn()`, `jest.mock()`, `spyOn` | [jest](jest.md) |
| Snapshot-тестирование | [jest](jest.md) |
| Таймеры, асинхронность, `async/await` | [jest](jest.md) |
| Покрытие (Istanbul), CI/CD | [jest](jest.md) |

## Когда использовать: сравнение unit-фреймворков

| Фреймворк | Язык | Стиль ассертов | Mocking |
|-----------|------|----------------|---------|
| **Jest** | JavaScript/TypeScript | `expect(x).toBe(y)` (fluent, матчеры) | встроенный (`jest.fn`, `jest.mock`) |
| JUnit (5) | Java | `assertEquals`, `assertThat` (+AssertJ) | Mockito (внешняя) |
| pytest | Python | plain `assert` + introspection | pytest-mock, unittest.mock |
| xUnit.net | C#/.NET | `Assert.Equal` (+ FluentAssertions) | Moq / NSubstitute (внешние) |
| TestNG | Java | `Assert.assertEquals` (JUnit-style) | Mockito (внешняя) |

Jest уникален тем, что моки и снапшоты «из коробки» — в большинстве других стеков это внешние библиотеки.

## Маршруты чтения

- **Быстрый старт (1 ч):** введение установка первый тест моки.
- **Боевой проект (1 день):** весь документ + покрытие + CI + React Testing Library.

## Куда идти дальше

- Обзор unit-тестирования — [README](../../../basics/README.md)
- Java-аналог — [README](../../../basics/README.md)
- Python-аналог — [README](../../../basics/README.md)
