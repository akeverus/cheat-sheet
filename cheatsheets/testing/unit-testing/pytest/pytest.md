---
title: "pytest"
description: "Кратко: pytest — фреймворк для тестирования приложений на Python. Минимальный синтаксис (assert без обёрток), фикстуры (@pytest.fixture), параметризация (@pytest.mark.parametrize), маркеры, богатая экосистема плагинов (pytest-cov, pytest-mock, pytest-asyncio, pytest-django). Подд"
tags:
  - testing
  - unit-testing
  - pytest
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **pytest**

Кратко: **pytest** — фреймворк для тестирования приложений на **Python**. Минимальный синтаксис (`assert` без обёрток), фикстуры (`@pytest.fixture`), параметризация (`@pytest.mark.parametrize`), маркеры, богатая экосистема плагинов (**pytest-cov**, **pytest-mock**, **pytest-asyncio**, **pytest-django**). Поддерживает **unittest**-совместимость и **doctest**.

## Полезные ссылки

### Официальная документация
- [pytest — Documentation](https://docs.pytest.org/)
- [pytest — Getting Started](https://docs.pytest.org/en/stable/getting-started.html)
- [pytest — Reference](https://docs.pytest.org/en/stable/reference/reference.html)

### Ресурсы
- [pytest — Good Practices](https://docs.pytest.org/en/stable/goodpractices.html)
- [pytest — Parametrize](https://docs.pytest.org/en/stable/how-to/parametrize.html)
- [pytest — Fixtures](https://docs.pytest.org/en/stable/fixture.html)

### См. также
- [Unit Testing](../) — юнит-тестирование
- [Jest](../jest/jest.md) — фреймворк для **JavaScript**/**TypeScript**
- [JUnit](../junit/junit.md) — фреймворк для **Java**
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md) — обзор инструментов

## Содержание

- [**pytest**](#pytest)
- [Введение](#введение)
- [Установка и настройка](#установка-и-настройка)
- [Базовое использование](#базовое-использование)
- [Фикстуры](#фикстуры)
- [Параметризация](#параметризация)
- [Маркеры](#маркеры)
- [Покрытие и плагины](#покрытие-и-плагины)
- [Асинхронные тесты](#асинхронные-тесты)
- [Расширенные сценарии](#расширенные-сценарии)
- [Интеграция с CI/CD](#интеграция-с-cicd)
- [Лучшие практики](#лучшие-практики)
- [FAQ и решение проблем](#faq-и-решение-проблем)
- [Глоссарий](#глоссарий)
- [Итоговые таблицы](#итоговые-таблицы)
- [Заключение](#заключение)

---

## Введение

**pytest** — основной фреймворк для тестирования приложений на **Python**. Минимальный синтаксис (обычный `assert` без обёрток), мощные фикстуры (`@pytest.fixture`), параметризация (`@pytest.mark.parametrize`), маркеры, богатая экосистема плагинов (**pytest-cov**, **pytest-mock**, **pytest-asyncio**, **pytest-django**, **pytest-html** и др.). Поддерживает **unittest**-совместимость и **doctest**.

### Зачем pytest

- **Минимальный синтаксис** — тесты пишутся как обычные функции с `assert`; не нужны классы-наследники **unittest**.
- **Фикстуры** — подготовка данных и окружения через `@pytest.fixture` с scope (`function`, `class`, `module`, `session`) и зависимостями.
- **Параметризация** — один тест на множество наборов данных через `@pytest.mark.parametrize`.
- **Автообнаружение** — тесты находятся по префиксу `test_` в имени функции/файла.
- **Плагины** — покрытие (**pytest-cov**), моки (**pytest-mock**), асинхронность (**pytest-asyncio**), отчёты (**pytest-html**), интеграция с **TestRail** (**pytest-testrail**).
- **Хуки** — расширение через `conftest.py` и хуки **pytest** (сбор, запуск, отчёты).

### Основные концепции

- **Test** — функция с именем `test_*` или метод в классе `Test*`; один тест — одна проверка или один сценарий.
- **Fixture** — функция с декоратором `@pytest.fixture`, возвращающая объект для использования в тестах; scope задаёт время жизни.
- **Parametrize** — декоратор `@pytest.mark.parametrize` для запуска одного теста с разными аргументами.
- **Marker** — метка теста (`@pytest.mark.slow`, `@pytest.mark.skip`); кастомные маркеры в `pytest.ini`/`pyproject.toml`.
- **conftest.py** — файл с фикстурами и хуками, общими для каталога и подкаталогов.
- **Hook** — функция в `conftest.py` с именем `pytest_*` для расширения поведения **pytest** (сбор, запуск, отчёт).

---

## Установка и настройка

### Требования

- **Python** 3.7+ (рекомендуется 3.10+). **pip** или **poetry** / **pipenv**.

### Установка

```bash
pip install pytest
# или с плагинами
pip install pytest pytest-cov pytest-mock pytest-asyncio pytest-html
```

Проверка:

```bash
pytest --version
```

Ожидается вывод версии **pytest** (например, `pytest 7.4.0`).

### Конфигурация (pytest.ini, pyproject.toml, setup.cfg)

**pytest.ini**:

```ini
[pytest]
testpaths = tests
python_files = test_*.py *_test.py
python_functions = test_*
python_classes = Test*
addopts = -v --tb=short
markers =
    slow: marks tests as slow
    integration: marks tests as integration
```

**pyproject.toml**:

```toml
[tool.pytest.ini_options]
testpaths = ["tests"]
python_files = ["test_*.py", "*_test.py"]
python_functions = ["test_*"]
addopts = "-v --tb=short"
markers = [
    "slow: marks tests as slow",
    "integration: marks tests as integration"
]
```

### Виртуальное окружение

```bash
python -m venv venv
source venv/bin/activate   # Linux/macOS
# или: venv\Scripts\activate  # Windows
pip install pytest pytest-cov
pytest
```

---

## Базовое использование

### Простой тест

```python
# test_sum.py
def sum(a, b):
    return a + b

def test_sum_positive():
    assert sum(1, 2) == 3

def test_sum_zero():
    assert sum(0, 0) == 0
```

Запуск: `pytest test_sum.py` или `pytest` (из корня проекта).

### Assert и встроенные проверки

**pytest** перехватывает `assert` и выводит подробное сравнение при падении:

```python
def test_list():
    assert [1, 2] == [1, 3]  # pytest покажет различие
```

Для проверки исключений:

```python
import pytest

def test_raises():
    with pytest.raises(ValueError):
        int("not a number")
    with pytest.raises(ValueError, match="invalid literal"):
        int("x")
```

### Запуск тестов

```bash
pytest                    # все тесты
pytest path/to/test_file.py   # один файл
pytest path/to/test_file.py::test_func   # один тест
pytest -k "test_sum"      # по имени (substring)
pytest -v                 # подробный вывод
pytest -x                 # остановиться после первого падения
pytest --lf               # только последние упавшие
pytest --ff               # сначала последние упавшие
pytest -m "slow"          # только тесты с маркером slow
pytest --ignore=path      # игнорировать каталог
```

---

## Фикстуры

### Базовые фикстуры

```python
import pytest

@pytest.fixture
def sample_data():
    return [1, 2, 3]

def test_with_fixture(sample_data):
    assert len(sample_data) == 3
    assert sample_data[0] == 1
```

### Scope фикстур

- **function** (по умолчанию) — создаётся заново для каждого теста.
- **class** — один раз на класс тестов.
- **module** — один раз на модуль.
- **session** — один раз на всю сессию запуска.

```python
@pytest.fixture(scope="module")
def db_connection():
    conn = create_connection()
    yield conn
    conn.close()
```

### Зависимости между фикстурами

Фикстура может принимать другую фикстуру аргументом:

```python
@pytest.fixture
def user():
    return {"id": 1, "name": "Alice"}

@pytest.fixture
def client(user):
    return ApiClient(user=user)

def test_client(client):
    assert client.user["name"] == "Alice"
```

### autouse

Фикстура с `autouse=True` выполняется для всех тестов в scope без явного указания в аргументах:

```python
@pytest.fixture(autouse=True)
def reset_state():
    # выполняется перед каждым тестом
    yield
    # очистка после теста
```

---

## Параметризация

### @pytest.mark.parametrize

```python
import pytest

@pytest.mark.parametrize("a,b,expected", [
    (1, 2, 3),
    (0, 0, 0),
    (-1, 1, 0),
])
def test_sum(a, b, expected):
    assert sum(a, b) == expected
```

Один тест запускается три раза с разными аргументами.

### Множественные параметры

```python
@pytest.mark.parametrize("x", [1, 2])
@pytest.mark.parametrize("y", [10, 20])
def test_product(x, y):
    assert x * y in [10, 20, 40]
```

Комбинируются все пары (4 запуска).

### ids для читаемости

```python
@pytest.mark.parametrize("a,b,expected", [
    (1, 2, 3),
    (0, 0, 0),
], ids=["positive", "zeros"])
def test_sum(a, b, expected):
    assert sum(a, b) == expected
```

В выводе **pytest** будут подписи `positive`, `zeros`.

---

## Маркеры

### Встроенные маркеры

- **@pytest.mark.skip** — пропустить тест.
- **@pytest.mark.skipif(condition)** — пропустить при условии.
- **@pytest.mark.xfail** — тест ожидаемо падает (не считается провалом).
- **@pytest.mark.parametrize** — параметризация.

```python
@pytest.mark.skip(reason="not implemented")
def test_future():
    pass

@pytest.mark.skipif(sys.platform == "win32", reason="Unix only")
def test_unix_only():
    pass

@pytest.mark.xfail(reason="known bug")
def test_bug():
    assert False
```

### Кастомные маркеры

В **pytest.ini** или **pyproject.toml** зарегистрировать маркеры (см. раздел «Конфигурация»). В тестах:

```python
@pytest.mark.slow
def test_slow():
    time.sleep(10)
```

Запуск только быстрых: `pytest -m "not slow"`.

---

## Покрытие и плагины

### pytest-cov

```bash
pip install pytest-cov
pytest --cov=src --cov-report=html --cov-report=term
```

Пороги покрытия в **pyproject.toml**:

```toml
[tool.pytest.ini_options]
addopts = "--cov=src --cov-fail-under=80"
```

### pytest-mock

Обёртка над **unittest.mock** с удобным **mocker** fixture:

```python
def test_with_mock(mocker):
    mocker.patch("mymodule.fetch", return_value={"id": 1})
    result = mymodule.get_user(1)
    assert result["id"] == 1
```

### pytest-html

```bash
pip install pytest-html
pytest --html=report.html
```

### pytest-asyncio

Для асинхронных тестов:

```bash
pip install pytest-asyncio
```

```python
@pytest.mark.asyncio
async def test_async():
    result = await async_fetch()
    assert result is not None
```

В **pytest.ini**: `asyncio_mode = auto` (для **pytest-asyncio** 0.21+).

---

## Асинхронные тесты

### pytest-asyncio

Установка и маркер:

```python
import pytest

@pytest.mark.asyncio
async def test_async_fetch():
    data = await fetch_data()
    assert "key" in data
```

Фикстура для **event loop** при необходимости кастомной настройки.

### Таймауты

Плагин **pytest-timeout**: `pip install pytest-timeout`; затем `@pytest.mark.timeout(5)` или `pytest --timeout=5`.

---

## Расширенные сценарии

### conftest.py

Фикстуры и хуки в `conftest.py` доступны всем тестам в каталоге и подкаталогах без импорта:

```python
# conftest.py
import pytest

@pytest.fixture
def app():
    from myapp import create_app
    return create_app()
```

### Хуки pytest

Пример — добавление маркера по имени файла:

```python
# conftest.py
def pytest_collection_modifyitems(items):
    for item in items:
        if "integration" in item.nodeid:
            item.add_marker(pytest.mark.integration)
```

### Тесты с подклассами

Можно группировать тесты в классы (наследование не обязательно):

```python
class TestUserAPI:
    def test_create(self, client):
        r = client.post("/users", json={"name": "Alice"})
        assert r.status_code == 201

    def test_get(self, client):
        r = client.get("/users/1")
        assert r.status_code == 200
```

---

## Интеграция с CI/CD

### GitHub Actions

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-python@v5
        with:
          python-version: '3.11'
      - run: pip install -r requirements.txt pytest pytest-cov
      - run: pytest --cov=src --cov-report=xml
      - uses: codecov/codecov-action@v3
        with:
          files: ./coverage.xml
```

### Jenkins, GitLab CI

После `pip install -r requirements.txt` выполнить `pytest --cov=src --junitxml=report.xml`. Результаты **pytest** (exit code) определяют успех/провал пайплайна; **JUnit XML** для артефактов и отчётов.

---

## Лучшие практики

1. **Один тест — одна проверка** — не объединять несколько несвязанных проверок в один тест.
2. **Именование** — функции `test_*`; описательные имена: `test_user_creation_succeeds_with_valid_data`.
3. **Фикстуры для подготовки** — выносить общую подготовку данных и окружения в фикстуры; не дублировать код в тестах.
4. **Параметризация** — использовать `@pytest.mark.parametrize` для однотипных проверок с разными данными.
5. **Изоляция** — тесты не должны зависеть друг от друга; порядок выполнения не гарантируется.
6. **Маркеры** — помечать медленные и интеграционные тесты; в **CI** запускать быстрые тесты по умолчанию.
7. **conftest.py** — общие фикстуры и хуки держать в `conftest.py`; не импортировать фикстуры вручную.

---

## FAQ и решение проблем

| Проблема | Возможная причина | Решение |
|----------|-------------------|---------|
| Тесты не находятся | Неверный `testpaths` или имена | Проверить `python_files`, `python_functions` в конфиге; имена `test_*.py`, `test_*` |
| Фикстура не найдена | Имя с опечаткой, не в scope | Проверить имя аргумента; фикстура в том же файле или в `conftest.py` |
| Импорт модуля падает | Путь не в PYTHONPATH | `pip install -e .` или добавить путь в PYTHONPATH |
| Покрытие 0% | Неверный путь `--cov` | Указать `--cov=src`; проверить `omit` в конфиге |
| Асинхронные тесты падают | Нет pytest-asyncio | `pip install pytest-asyncio`; `asyncio_mode = auto` |
| Моки не применяются | Патч не там, где вызывается | Патчить где объект **используется**, не где определён |

**pytest или unittest?** pytest — минимальный синтаксис, фикстуры, плагины. unittest — стандартная библиотека. Для новых проектов — pytest.

**Передача данных в фикстуру?** Фабрика (фикстура возвращает функцию) или `@pytest.fixture(params=[...])` + `request.param`.

**Пропустить тест?** `@pytest.mark.skipif(condition, reason="...")`.

**Один тест?** `pytest path/file.py::test_name` или `pytest -k "test_name"`.

**Таймаут?** Плагин pytest-timeout: `@pytest.mark.timeout(30)` или `pytest --timeout=30`.

**Django?** Плагин pytest-django + `DJANGO_SETTINGS_MODULE`.

**Исключения?** `with pytest.raises(ValueError, match="regex"): ...`

**JUnit-отчёт для CI?** `pytest --junitxml=report.xml`.

---

## Глоссарий

| Термин | Описание |
|--------|----------|
| **Test** | Функция `test_*` или метод в классе `Test*` |
| **Fixture** | Функция с `@pytest.fixture`, возвращающая объект для тестов |
| **Scope** | Время жизни фикстуры: function, class, module, session |
| **Parametrize** | Декоратор `@pytest.mark.parametrize` для множества аргументов |
| **Marker** | Метка теста: `@pytest.mark.slow`, кастомные маркеры |
| **conftest.py** | Файл с фикстурами и хуками для каталога |
| **Hook** | Функция `pytest_*` в conftest для расширения **pytest** |
| **Plugin** | Расширение **pytest** (pytest-cov, pytest-mock и др.) |
| **autouse** | Фикстура выполняется автоматически без указания в аргументах |
| **xfail** | Ожидаемо падающий тест (не считается провалом) |

---

## Итоговые таблицы

### Команды запуска

| Команда | Описание |
|---------|----------|
| `pytest` | Запуск всех тестов |
| `pytest path/to/file.py` | Один файл |
| `pytest path/to/file.py::test_func` | Один тест |
| `pytest -k "name"` | По имени (substring) |
| `pytest -v` | Подробный вывод |
| `pytest -x` | Остановиться после первого падения |
| `pytest --lf` | Только последние упавшие |
| `pytest -m "slow"` | Только тесты с маркером |
| `pytest --cov=src` | С покрытием |
| `pytest --cov=src --cov-report=html` | Отчёт покрытия в HTML |

### Scope фикстур

| Scope | Когда создаётся |
|-------|-----------------|
| function | Для каждого теста |
| class | Один раз на класс |
| module | Один раз на модуль |
| session | Один раз на сессию |

---

## Заключение

**pytest** — мощный фреймворк для тестирования **Python** с минимальным синтаксисом, фикстурами, параметризацией и богатой экосистемой плагинов. Используйте фикстуры для подготовки данных, параметризацию для множества сценариев, маркеры для категоризации тестов. Для углублённого изучения см. [pytest Documentation](https://docs.pytest.org/), [Unit Testing](../), [Jest](../jest/jest.md), [JUnit](../junit/junit.md).

---

