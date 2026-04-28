---
title: "xUnit.net"
description: "Точка входа в раздел xUnit.net: современный фреймворк тестирования для .NET (C#, F#, VB.NET)."
tags:
  - meta
  - index
  - testing
  - unit-testing
  - xunit
type: "index"
aliases:
  - "xUnit.net"
prerequisites: []
next: []
updated: "2026-04-20"
---
# xUnit.net

xUnit.net — современный фреймворк тестирования для .NET (C#, F#, VB.NET) от создателей NUnit v2. Модель «один экземпляр класса на тест» (без shared state), атрибуты `[Fact]` и `[Theory]`, параметризация через `[InlineData]` / `[MemberData]` / `[ClassData]`, фикстуры через `IClassFixture<T>` и `ICollectionFixture<T>`, встроенные ассерты, параллельный запуск по умолчанию.

Применяйте в новых .NET-проектах как стандарт де-факто: xUnit интегрирован в `dotnet test`, Visual Studio, Rider и большинство CI-систем. Альтернативы — NUnit (ближе к JUnit, зрелый экосистема) и MSTest (Microsoft default, слабее сообществом).

## Полезные ссылки

### Основные документы
- [xUnit.net](xunit.md) — Fact/Theory, фикстуры, параллельность, CI/CD

### Соседние разделы
- [Unit Testing](../../../basics/README.md)
- [JUnit](../../../basics/README.md)
- [pytest](../../../basics/README.md)
- [Jest](../../../basics/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)

### Внешние ресурсы
- [xUnit.net Getting Started](https://xunit.net/docs/getting-started/netcore/cmdline)
- [Writing Tests](https://xunit.net/docs/writing-tests)
- [xUnit vs NUnit vs MSTest](https://xunit.net/docs/comparisons)
- [FluentAssertions](https://fluentassertions.com/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: сравнение unit-фреймворков](#когда-использовать-сравнение-unit-фреймворков)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| `[Fact]` и `[Theory]`, параметризация | [xunit](xunit.md) |
| Фикстуры: `IClassFixture<T>`, `ICollectionFixture<T>` | [xunit](xunit.md) |
| Ассерты: `Assert.*` и FluentAssertions | [xunit](xunit.md) |
| Параллельность и изоляция | [xunit](xunit.md) |
| CI/CD, `dotnet test` | [xunit](xunit.md) |

## Когда использовать: сравнение unit-фреймворков

| Фреймворк | Язык | Стиль ассертов | Mocking |
|-----------|------|----------------|---------|
| **xUnit.net** | C#/.NET | `Assert.Equal` + FluentAssertions | Moq / NSubstitute (внешние) |
| NUnit | C#/.NET | `Assert.That(actual, Is.EqualTo(expected))` | Moq / NSubstitute |
| MSTest | C#/.NET | `Assert.AreEqual` | Moq |
| JUnit 5 | Java | `assertEquals` + AssertJ | Mockito (внешняя) |
| pytest | Python | plain `assert` | `pytest-mock` |

xUnit — оптимален для новых .NET-проектов: чистая модель изоляции, параллельный запуск по умолчанию, активное сообщество.

## Маршруты чтения

- **Быстрый старт (1 ч):** введение `dotnet test` Fact/Theory FluentAssertions.
- **Боевой проект (1 день):** весь документ + фикстуры + parallel + CI/CD.

## Куда идти дальше

- Обзор unit-тестирования — [README](../../../basics/README.md)
- Java-аналог — [README](../../../basics/README.md)
- JS-аналог — [README](../../../basics/README.md)
