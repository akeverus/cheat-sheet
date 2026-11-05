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
updated: "2026-11-05"
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

`unittest` входит в стандартную библиотеку, использует `TestCase`, методы `test_*`, setup/teardown и assertions. `pytest` — отдельная зависимость с обычными функциями `test_*`, обычным `assert` с подробным отчётом, fixtures и параметризацией. Pytest умеет запускать многие `unittest.TestCase` тесты, но методы `TestCase` нельзя напрямую параметризовать и передавать им fixtures как аргументы. Для постепенной миграции доступны `autouse` и `@pytest.mark.usefixtures`; они не превращают методы `TestCase` в обычные pytest-функции.

Выбирайте по правилам репозитория и нужной экосистеме. Для маленькой библиотеки без сторонних test-зависимостей подходит `unittest`; для большого набора сценариев и fixture-композиции часто удобен `pytest`. [unittest](https://docs.python.org/3/library/unittest.html), [pytest и `unittest.TestCase`](https://docs.pytest.org/en/stable/how-to/unittest.html).

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

Код после `yield` выполняет teardown **в конце scope**: для `function` — после теста, для `session` — в конце сессии. Если подготовка падает **до** `yield`, этот teardown ещё не зарегистрирован, но pytest завершит уже успешно созданные другие fixtures. Разбивайте многошаговое создание на небольшие fixtures; если ресурс создан и возможна ошибка до `yield`, регистрируйте `request.addfinalizer` сразу после успешного создания. [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html#teardown-cleanup-aka-fixture-finalization).

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

Не сваливайте десятки несвязанных требований в одну таблицу: тест должен объяснять единый контракт. `pytest.param` также позволяет поставить `skip` или `xfail` для отдельного случая; его `id` меняет только имя случая в отчёте, а не ожидаемый результат. Для интеграционного теста отдельно проверьте, что комбинации fixtures не оставляют общий state. [pytest parametrization](https://docs.pytest.org/en/stable/how-to/parametrize.html), [параметризация по случаям](https://docs.pytest.org/en/stable/example/parametrize.html).

## Q4. Где patch должен заменить зависимость?

`unittest.mock.patch` временно подменяет **имя в namespace, где код под тестом его ищет**, а не обязательно там, где объект первоначально определён. Если `service.py` сделал `from gateway import send`, патчите `service.send`: `gateway.send` уже не та ссылка, которую вызывает `service`.

```python
from unittest.mock import patch

with patch("service.send", autospec=True) as send:
    process_order(order)
    send.assert_called_once()
```

Если `service.py` использует `import gateway` и затем `gateway.send`, патчите атрибут `gateway.send` импортированного модуля: место поиска теперь иное. `autospec=True` ограничивает интерфейс mock, но не исправляет неверный target `patch`. Патч неправильного места может оставить реальный внешний вызов. Предпочитайте явное внедрение зависимостей там, где это делает контракт яснее, и проверяйте наблюдаемое поведение, а не каждый внутренний вызов. [Where to patch](https://docs.python.org/3/library/unittest.mock.html#where-to-patch).

## Q5. Чем отличаются monkeypatch и autospec?

Pytest `monkeypatch` временно меняет атрибут, ключ словаря, переменную окружения или cwd и восстанавливает исходное значение после завершения теста или запросившей его fixture. `unittest.mock` `autospec=True` создаёт mock с видимым API и сигнатурой реального объекта, чтобы опечатка в методе не проходила незамеченной. Это разные задачи: область действия подмены и проверка интерфейса. `autospec` не знает об атрибутах, которые объект создаёт только в `__init__`.

```python
def test_region(monkeypatch):
    monkeypatch.setenv("REGION", "test")
    assert current_region() == "test"
```

Не патчите глобальные функции стандартной библиотеки без необходимости: это может затронуть сам pytest. Если такая подмена нужна лишь для части теста, ограничьте её `monkeypatch.context()`. Для важных внешних контрактов mock не заменяет интеграционный тест. [monkeypatch](https://docs.pytest.org/en/stable/how-to/monkeypatch.html), [autospeccing](https://docs.python.org/3/library/unittest.mock.html#autospeccing).

## Q6. Как тестировать async-код и отмену?

В стандартной библиотеке `unittest.IsolatedAsyncioTestCase` запускает каждый async test method в отдельном event loop и поддерживает `asyncSetUp`/`asyncTearDown`. Обычный pytest без async-плагина не исполняет `async def` test. С `pytest-asyncio` в режиме `strict` тест помечают `@pytest.mark.asyncio`; в режиме `auto` плагин добавляет marker сам. По умолчанию у каждого теста свой loop, но `loop_scope` может расширить его область. Проверяйте режим и версию плагина в проекте.

Тестируйте результат, timeout и cleanup при отмене. `Task.cancel()` запрашивает кооперативную отмену: coroutine получает `CancelledError` на следующей точке ожидания и должна освободить ресурсы в `finally`. Тесту нужно дождаться завершения отменённой задачи и проверить cleanup; одного вызова `cancel()` недостаточно. События/barrier делают порядок предсказуемее, чем `sleep`. `IsolatedAsyncioTestCase` отменяет оставшиеся задачи при завершении теста, но это не доказывает корректный cleanup приложения. [unittest async tests](https://docs.python.org/3/library/unittest.html#unittest.IsolatedAsyncioTestCase), [pytest-asyncio modes](https://pytest-asyncio.readthedocs.io/en/v1.4.1/concepts.html), [asyncio cancellation](https://docs.python.org/3/library/asyncio-task.html#task-cancellation).

Для подмены async-зависимости используйте `AsyncMock`. Его вызов и ожидание — разные события: `assert_called_once_with(...)` может пройти, даже если код забыл `await`. `assert_awaited_once_with(...)` проверяет однократное ожидание с нужными аргументами. Это ещё не доказывает успешный результат или реальный сетевой обмен: mock может выбросить исключение при ожидании. [Python 3.15 AsyncMock](https://docs.python.org/3.15/library/unittest.mock.html#unittest.mock.AsyncMock).

```python
from unittest import IsolatedAsyncioTestCase
from unittest.mock import AsyncMock

class SendTest(IsolatedAsyncioTestCase):
    async def test_call_is_not_await(self):
        send = AsyncMock(return_value="accepted")
        pending = send("order-42")
        send.assert_called_once_with("order-42")
        send.assert_not_awaited()
        self.assertEqual(await pending, "accepted")
        send.assert_awaited_once_with("order-42")
```

При проверке отмены дождитесь, пока задача вошла в защищённый `try/finally`: отмена до начала выполнения coroutine не проверит этот путь очистки. В примере `started` задаёт порядок, а `cleaned` — наблюдаемый результат очистки; произвольная задержка не нужна.

```python
import asyncio
from unittest import IsolatedAsyncioTestCase

class CancellationTest(IsolatedAsyncioTestCase):
    async def test_cancel_runs_cleanup(self):
        started, cleaned = asyncio.Event(), asyncio.Event()

        async def worker():
            try:
                started.set()
                await asyncio.Event().wait()
            finally:
                cleaned.set()  # Здесь приложение закрывало бы свой ресурс.

        task = asyncio.create_task(worker())
        await asyncio.wait_for(started.wait(), timeout=1)
        task.cancel()
        with self.assertRaises(asyncio.CancelledError):
            await task
        self.assertTrue(cleaned.is_set())
```

В реальном тесте проверяйте нужное действие очистки, например ожидание `client.aclose()`, а не только выставленный флаг. `assert_awaited_once_with()` у такого mock проверит ожидание закрытия; отдельный интеграционный тест проверяет сам ресурс. Примеры используют стандартную библиотеку и запускаются командой `python -m unittest` после сохранения в `test_async.py`.

## Q7. Как изолировать БД и внешние сервисы?

Unit test отделяет SQL-код/HTTP-клиент через явный интерфейс и проверяет ветвления. Integration test запускает реальную БД или совместимое тестовое окружение, проверяя SQL, ограничения, транзакции и драйвер; mock здесь не докажет совместимость. Создавайте схему/данные перед тестом, удаляйте их после, делайте тесты независимыми от порядка запуска.

Rollback после каждого теста удобен, но если приложение работает внутри внешней тестовой транзакции, вызов ORM `commit()` может не дать настоящего `COMMIT` в БД. Такая схема не доказывает видимость изменений другим соединениям, конкуренцию и эффекты после фиксации. Для этих случаев нужен отдельный сценарий с настоящим commit и гарантированным cleanup. Внешний HTTP заменяйте управляемым тестовым сервером, когда важны формат запроса, timeout и статус; патч подходит для локальной логики без сети. [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html), [SQLAlchemy 2.0: тестовая внешняя транзакция](https://docs.sqlalchemy.org/en/20/orm/session_transaction.html#joining-a-session-into-an-external-transaction-such-as-for-test-suites), [PostgreSQL `COMMIT`](https://www.postgresql.org/docs/current/sql-commit.html).

---

## See also

- [Основы Python](python-interview.md) — модель объектов и ресурсов.
- [Конкурентность в Python](python-concurrency-interview.md) — задачи, отмена и event loop.
- [Unit testing](../../testing/unit-testing-interview.md) — общие принципы тестирования.
