---
title: "pytest"
description: "Точка входа в раздел pytest: основной фреймворк юнит- и интеграционного тестирования для Python."
tags:
  - meta
  - index
  - testing
  - unit-testing
  - pytest
type: "index"
updated: "2026-04-20"
---
# pytest

pytest — основной фреймворк для тестирования приложений на Python. Минимальный синтаксис (plain `assert` с детальной интроспекцией при падении), фикстуры (`@pytest.fixture`), параметризация (`@pytest.mark.parametrize`), маркеры, огромная экосистема плагинов (pytest-cov, pytest-mock, pytest-asyncio, pytest-django). Поддерживает unittest-совместимость и doctest.

Применяйте как стандарт de-facto в Python-проектах. Альтернатива `unittest` из стандартной библиотеки нужна, только если запрещены внешние зависимости; во всех остальных случаях pytest выигрывает по краткости, фикстурам и экосистеме.

## Полезные ссылки

### Основные документы
- [pytest](pytest.md) — фикстуры, параметризация, маркеры, плагины

### Соседние разделы
- [Unit Testing](../../../basics/README.md)
- [Jest](../../../basics/README.md)
- [JUnit](../../../basics/README.md)
- [xUnit.net](../../../basics/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)

### Внешние ресурсы
- [pytest Docs](https://docs.pytest.org/)
- [Good Practices](https://docs.pytest.org/en/stable/goodpractices.html)
- [Fixtures](https://docs.pytest.org/en/stable/fixture.html)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: сравнение unit-фреймворков](#когда-использовать-сравнение-unit-фреймворков)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Структура теста, обнаружение тестов | [pytest](pytest.md) |
| Фикстуры: области видимости, autouse, параметризация | [pytest](pytest.md) |
| `parametrize`, маркеры, skip/xfail | [pytest](pytest.md) |
| Плагины: pytest-cov, pytest-mock, pytest-asyncio | [pytest](pytest.md) |
| CI/CD, `conftest.py`, отчёты | [pytest](pytest.md) |

## Когда использовать: сравнение unit-фреймворков

| Фреймворк | Язык | Стиль ассертов | Mocking |
|-----------|------|----------------|---------|
| **pytest** | Python | plain `assert` + introspection | `pytest-mock` / `unittest.mock` |
| unittest | Python (stdlib) | `self.assertEqual` (xUnit-style) | `unittest.mock` |
| Jest | JavaScript/TypeScript | `expect(x).toBe(y)` | встроенный |
| JUnit 5 | Java | `assertEquals` (+AssertJ) | Mockito (внешняя) |
| xUnit.net | C#/.NET | `Assert.Equal` | Moq/NSubstitute |

pytest — выбор по умолчанию для Python: самый короткий синтаксис, мощные фикстуры, широкая экосистема плагинов.

## Маршруты чтения

- **Быстрый старт (1 ч):** введение установка первый тест фикстуры.
- **Боевой проект (1 день):** весь документ + pytest-cov + pytest-asyncio + conftest + CI.

## Куда идти дальше

- Обзор unit-тестирования — [README](../../../basics/README.md)
- JS-аналог — [README](../../../basics/README.md)
- Java-аналог — [README](../../../basics/README.md)
