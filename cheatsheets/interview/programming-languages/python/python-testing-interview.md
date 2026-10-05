---
title: "Вопросы на собеседовании: Тестирование в Python"
description: "pytest и unittest: fixtures, параметризация, mock, async-тесты и изоляция зависимостей backend-сервиса"
tags:
  - interview
  - programming-languages
  - python-testing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Python testing interview"
  - "pytest на собеседовании"
prerequisites:
  - "[[python-interview]]"
next: []
updated: "2026-10-05"
---
# Вопросы на собеседовании: Тестирование в Python

Тема — конкретные инструменты Python для проверяемого backend-кода. Общие уровни тестирования и дизайн test pyramid остаются в соседних шпаргалках.

## Полезные ссылки

- [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html) и [parametrize](https://docs.pytest.org/en/stable/how-to/parametrize.html).
- [pytest monkeypatch](https://docs.pytest.org/en/stable/how-to/monkeypatch.html) — временная замена окружения и объектов.
- [unittest](https://docs.python.org/3/library/unittest.html) и [unittest.mock](https://docs.python.org/3/library/unittest.mock.html).
- [pytest-asyncio](https://pytest-asyncio.readthedocs.io/en/stable/) — отдельный плагин для async-тестов в pytest.

## Содержание

- [Q1. Что выбрать: pytest или unittest?](#q1-что-выбрать-pytest-или-unittest)
- [Q2. Как fixture изолирует ресурсы и когда нужен yield?](#q2-как-fixture-изолирует-ресурсы-и-когда-нужен-yield)
- [Q3. Как параметризовать набор граничных случаев?](#q3-как-параметризовать-набор-граничных-случаев)
- [Q4. Где patch должен заменить зависимость?](#q4-где-patch-должен-заменить-зависимость)
- [Q5. Чем отличаются monkeypatch и autospec?](#q5-чем-отличаются-monkeypatch-и-autospec)
- [Q6. Как тестировать async-код и отмену?](#q6-как-тестировать-async-код-и-отмену)
- [Q7. Как изолировать БД и внешние сервисы?](#q7-как-изолировать-бд-и-внешние-сервисы)
- [See also](#see-also)

## Q1. Что выбрать: pytest или unittest?

`unittest` входит в стандартную библиотеку, использует `TestCase`, методы `test_*`, setup/teardown и assertions. `pytest` — отдельная зависимость с обычными функциями `test_*`, обычным `assert` с подробным отчётом, fixtures и параметризацией. Pytest умеет запускать многие `unittest.TestCase` тесты, но его fixtures не работают для методов `TestCase` так же, как для обычных pytest-функций.

Выбирайте по правилам репозитория и нужной экосистеме. Для маленькой библиотеки без сторонних test-зависимостей подходит `unittest`; для большого набора сценариев и fixture-композиции часто удобен `pytest`. [unittest](https://docs.python.org/3/library/unittest.html), [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html).

## Q2. Как fixture изолирует ресурсы и когда нужен yield?

Pytest подставляет fixture по имени аргумента. Обычный scope — `function`: каждый тест получает новый результат fixture. Более широкий `module`/`session` scope экономит создание дорогого ресурса, но увеличивает риск общего изменяемого состояния. Fixture может зависеть от другой fixture; внутри одного теста её результат кешируется.

```python
import pytest

@pytest.fixture
def client():
    resource = create_client()
    yield resource
    resource.close()
```

Код после `yield` выполняет teardown после теста. Если подготовка падает **до** `yield`, этот teardown ещё не зарегистрирован; разбивайте многошаговое создание на небольшие fixtures и сразу ставьте cleanup для каждого успешно созданного ресурса. [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html#teardown-cleanup-aka-fixture-finalization).

## Q3. Как параметризовать набор граничных случаев?

`@pytest.mark.parametrize` запускает тест отдельно для каждой строки входов, поэтому конкретный сбой виден в отчёте. Явные `id` полезны для сценариев вроде `empty`, `unicode`, `overflow`; они должны описывать поведение, а не повторять данные.

```python
import pytest

@pytest.mark.parametrize("raw, expected", [
    pytest.param("1", 1, id="integer"),
    pytest.param("", None, id="empty"),
])
def test_parse_id(raw, expected):
    assert parse_id(raw) == expected
```

Не сваливайте десятки несвязанных требований в одну таблицу: тест должен объяснять единый контракт. Для интеграционного теста отдельно проверьте, что комбинации fixtures не оставляют общий state. [pytest parametrization](https://docs.pytest.org/en/stable/how-to/parametrize.html).

## Q4. Где patch должен заменить зависимость?

`unittest.mock.patch` временно подменяет **имя в namespace, где код под тестом его ищет**, а не обязательно там, где объект первоначально определён. Если `service.py` сделал `from gateway import send`, патчите `service.send`: `gateway.send` уже не та ссылка, которую вызывает `service`.

```python
from unittest.mock import patch

with patch("service.send", autospec=True) as send:
    process_order(order)
    send.assert_called_once()
```

Патч неправильного места даёт ложный тест или реальный внешний вызов. Предпочитайте явное внедрение зависимостей там, где это делает контракт яснее, и проверяйте наблюдаемое поведение, а не каждый внутренний вызов. [Where to patch](https://docs.python.org/3/library/unittest.mock.html#where-to-patch).

## Q5. Чем отличаются monkeypatch и autospec?

Pytest `monkeypatch` временно меняет атрибут, ключ словаря, переменную окружения или cwd и восстанавливает исходное значение после теста. `unittest.mock` `autospec=True` создаёт mock с API и сигнатурой реального объекта, чтобы опечатка в методе не проходила незамеченной. Это разные задачи: область действия подмены и проверка интерфейса.

```python
def test_region(monkeypatch):
    monkeypatch.setenv("REGION", "test")
    assert current_region() == "test"
```

Не патчите глобальные функции стандартной библиотеки без необходимости: это может затронуть сам pytest. Для важных внешних контрактов mock не заменяет интеграционный тест. [monkeypatch](https://docs.pytest.org/en/stable/how-to/monkeypatch.html), [autospeccing](https://docs.python.org/3/library/unittest.mock.html#autospeccing).

## Q6. Как тестировать async-код и отмену?

В стандартной библиотеке `unittest.IsolatedAsyncioTestCase` запускает async test method в отдельном event loop и поддерживает `asyncSetUp`/`asyncTearDown`. Обычный pytest сам по себе не исполняет `async def` test; для этого используют плагин, например `pytest-asyncio`, и его правила/markers.

Тестируйте результат, timeout и cleanup при отмене. Не используйте реальный `sleep` как замену синхронизации: события/barrier делают порядок теста контролируемым. Закрывайте async-клиенты и задачи, иначе следующий тест увидит утечки. [unittest async tests](https://docs.python.org/3/library/unittest.html#unittest.IsolatedAsyncioTestCase), [pytest-asyncio](https://pytest-asyncio.readthedocs.io/en/stable/).

## Q7. Как изолировать БД и внешние сервисы?

Unit test отделяет SQL-код/HTTP-клиент через явный интерфейс и проверяет ветвления. Integration test запускает реальную БД или совместимое тестовое окружение, проверяя SQL, ограничения, транзакции и драйвер; mock здесь не докажет совместимость. Создавайте схему/данные перед тестом, удаляйте их после, делайте тесты независимыми от порядка запуска.

Rollback после каждого теста удобен, но может скрыть поведение `COMMIT`, конкуренцию и события после фиксации. Для таких случаев нужен тест с реальным commit и гарантированным cleanup. Внешний HTTP заменяйте управляемым тестовым сервером, когда важны формат запроса, timeout и статус; патч подходит для локальной логики без сети. [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html), [unittest.mock](https://docs.python.org/3/library/unittest.mock.html).

---

## See also

- [Основы Python](python-interview.md) — модель объектов и ресурсов.
- [Конкурентность в Python](python-concurrency-interview.md) — задачи, отмена и event loop.
- [Unit testing](../../testing/unit-testing-interview.md) — общие принципы тестирования.
