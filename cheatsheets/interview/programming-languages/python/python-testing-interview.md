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
updated: "2026-11-08"
---
# Вопросы на собеседовании: Тестирование в Python

Тема — конкретные инструменты Python для проверяемого backend-кода. Общие уровни тестирования и дизайн test pyramid остаются в соседних шпаргалках.

## Полезные ссылки

- [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html) и [parametrize](https://docs.pytest.org/en/stable/how-to/parametrize.html).
- [pytest monkeypatch](https://docs.pytest.org/en/stable/how-to/monkeypatch.html) — временная замена окружения и объектов.
- [unittest](https://docs.python.org/3/library/unittest.html) и [unittest.mock](https://docs.python.org/3/library/unittest.mock.html).
- [pytest-asyncio](https://pytest-asyncio.readthedocs.io/en/stable/) — отдельный плагин для async-тестов в pytest.

## Содержание

- [Q1. Чем различаются pytest и unittest?](#q1-чем-различаются-pytest-и-unittest)
- [Q2. Когда выполняется teardown yield-fixture pytest?](#q2-когда-выполняется-teardown-yield-fixture-pytest)
- [Q3. Что даёт `pytest.mark.parametrize` при проверке граничных случаев?](#q3-что-даёт-pytestmarkparametrize-при-проверке-граничных-случаев)
- [Q4. Где patch должен заменить зависимость?](#q4-где-patch-должен-заменить-зависимость)
- [Q5. Чем отличаются monkeypatch и autospec?](#q5-чем-отличаются-monkeypatch-и-autospec)
- [Q6. Как тестировать async-код и отмену?](#q6-как-тестировать-async-код-и-отмену)
- [Q7. Как изолировать БД и внешние сервисы?](#q7-как-изолировать-бд-и-внешние-сервисы)
- [See also](#see-also)

## Q1. Чем различаются pytest и unittest?

`unittest` входит в стандартную библиотеку, использует `TestCase`, методы `test_*`, setup/teardown и assertions. `pytest` — отдельная зависимость с обычными функциями `test_*`, обычным `assert` с подробным отчётом, fixtures и параметризацией. Pytest умеет запускать многие `unittest.TestCase` тесты, но методы `TestCase` нельзя напрямую параметризовать и передавать им fixtures как аргументы. Для постепенной миграции доступны `autouse` и `@pytest.mark.usefixtures`; они не превращают методы `TestCase` в обычные pytest-функции.

Выбирайте по правилам репозитория и нужной экосистеме. Для маленькой библиотеки без сторонних test-зависимостей подходит `unittest`; для большого набора сценариев и fixture-композиции часто удобен `pytest`. [unittest](https://docs.python.org/3/library/unittest.html), [pytest и `unittest.TestCase`](https://docs.pytest.org/en/stable/how-to/unittest.html).

## Q2. Когда выполняется teardown yield-fixture pytest?

Pytest подставляет fixture по имени аргумента. Обычный scope — `function`: pytest заново вызывает fixture для каждого тестового случая, которому она нужна. Если fixture возвращает общий объект, этот объект всё равно разделяется между случаями. Более широкий `module`/`session` scope экономит создание дорогого ресурса, но увеличивает риск общего изменяемого состояния. Fixture может зависеть от другой fixture; внутри одного теста её результат кешируется.

```python
import pytest

@pytest.fixture
def client():
    resource = create_client()
    yield resource
    resource.close()
```

Код после `yield` выполняет teardown при завершении активного экземпляра fixture. Для обычной fixture с областью `function` это происходит после теста, а с областью `session` — в конце сессии. Параметризованная fixture может завершить экземпляр раньше, при смене параметра. Если подготовка падает **до** `yield`, этот teardown ещё не зарегистрирован, но pytest завершит уже успешно созданные другие fixtures. Разбивайте многошаговое создание на небольшие fixtures; если ресурс создан и возможна ошибка до `yield`, регистрируйте `request.addfinalizer` сразу после успешного создания. [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html#teardown-cleanup-aka-fixture-finalization).
`try/finally` внутри fixture даёт другую гарантию: если подготовка падает внутри `try` ещё до `yield`, Python выполняет `finally` при выходе из функции. Например, клиент уже создан, но проверка доступности сервиса выбрасывает исключение. Клиент закроется сразу, а pytest сообщит об ошибке setup и пропустит тело теста.

```python
@pytest.fixture
def ready_client():
    resource = create_client()
    try:
        resource.check_ready()
        yield resource
    finally:
        resource.close()
```

Если `create_client()` сам выбрасывает исключение после частичного выделения ресурсов, освобождать их должен он сам: fixture ещё не получила объект, который можно закрыть.

**Параметризованная fixture.** Scope задаёт границу переиспользования, но не гарантирует один вызов на весь scope. Например, `@pytest.fixture(scope="module", params=["sqlite", "postgresql"])` позволяет тестам модуля переиспользовать ресурс для текущего параметра. Pytest хранит один активный экземпляр этой fixture за раз. Когда нужен другой параметр, pytest завершает предыдущий экземпляр перед созданием нового. Поэтому teardown может выполниться ещё до конца модуля. [pytest fixtures: scope](https://docs.pytest.org/en/stable/how-to/fixtures.html#fixture-scopes).

## Q3. Что даёт `pytest.mark.parametrize` при проверке граничных случаев?

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

**Изоляция входных данных.** Pytest передаёт параметры как есть. Если два случая получают один и тот же словарь, первый тест может изменить вход второго. Например, нормализация запроса удаляет ключ из переданного словаря, и следующий случай со ссылкой на тот же словарь получает уже изменённые данные. Передавайте неизменяемое описание случая и собирайте новый словарь внутри теста либо передавайте фабрику данных. Если тест меняет вложенные списки или словари, поверхностная копия внешнего словаря оставит их общими. [pytest: Parametrizing test functions](https://docs.pytest.org/en/stable/how-to/parametrize.html#pytest-mark-parametrize-parametrizing-test-functions).

## Q4. Где patch должен заменить зависимость?

`unittest.mock.patch` временно подменяет **имя в namespace, где код под тестом его ищет**, а не обязательно там, где объект первоначально определён. Если `service.py` сделал `from gateway import send`, патчите `service.send`: `gateway.send` уже не та ссылка, которую вызывает `service`.

```python
from unittest.mock import patch

with patch("service.send", autospec=True) as send:
    process_order(order)
    send.assert_called_once()
```

Если `service.py` использует `import gateway` и затем `gateway.send`, патчите атрибут `gateway.send` импортированного модуля: место поиска теперь иное. `autospec=True` ограничивает интерфейс mock, но не исправляет неверный target `patch`. Патч неправильного места может оставить реальный внешний вызов. Предпочитайте явное внедрение зависимостей там, где это делает контракт яснее, и проверяйте наблюдаемое поведение, а не каждый внутренний вызов. [Where to patch](https://docs.python.org/3/library/unittest.mock.html#where-to-patch).

Если в `service.py` уже определена функция `def process_order(order, sender=send): ...` и она вызывает `sender(order)`, даже `patch("service.send")` не заменит отправитель для вызова без аргумента `sender`. Python вычислил значение по умолчанию при определении функции. В тесте передайте подмену явно: `process_order(order, sender=fake_send)`. Если контракт допускает отложенный выбор зависимости, задайте `sender=None` и выбирайте `send` внутри функции при `sender is None`. Тогда подмена `service.send` действует в момент вызова. [Значения параметров по умолчанию в Python 3.14](https://docs.python.org/3.14/reference/compound_stmts.html#function-definitions).

## Q5. Чем отличаются monkeypatch и autospec?

Pytest `monkeypatch` временно меняет атрибут, ключ словаря, переменную окружения или cwd и восстанавливает исходное значение после завершения теста или запросившей его fixture. `unittest.mock` `autospec=True` создаёт mock с видимым API и сигнатурой реального объекта, чтобы опечатка в методе не проходила незамеченной. Это разные задачи: область действия подмены и проверка интерфейса. При autospec **по классу** атрибуты, созданные только в `__init__`, могут отсутствовать в спецификации. Например, если `Gateway.__init__` присваивает `self.region`, чтение `region` у `create_autospec(Gateway, instance=True)` вызовет `AttributeError`, когда такого атрибута нет на классе. `instance=True` описывает mock как экземпляр и не вызывает конструктор. Если есть безопасно созданный экземпляр, `create_autospec(gateway, spec_set=True)` увидит его `region`. Обычный autospec позволяет вручную присвоить отсутствующий атрибут. `spec_set=True` также запрещает такие присваивания и обнаружит опечатку вроде `mock_gateway.regoin = "test"`.

```python
def test_region(monkeypatch):
    monkeypatch.setenv("REGION", "test")
    assert current_region() == "test"
```

Не патчите глобальные функции стандартной библиотеки без необходимости: это может затронуть сам pytest. Если такая подмена нужна лишь для части теста, ограничьте её `monkeypatch.context()`. Для важных внешних контрактов mock не заменяет интеграционный тест. [monkeypatch](https://docs.pytest.org/en/stable/how-to/monkeypatch.html), [autospeccing](https://docs.python.org/3/library/unittest.mock.html#autospeccing).

## Q6. Как тестировать async-код и отмену?

В стандартной библиотеке `unittest.IsolatedAsyncioTestCase` запускает каждый async test method в отдельном event loop и поддерживает `asyncSetUp`/`asyncTearDown`. Обычный pytest без async-плагина не исполняет `async def` test. С `pytest-asyncio` в режиме `strict` тест помечают `@pytest.mark.asyncio`; в режиме `auto` плагин добавляет marker сам. По умолчанию у каждого теста свой loop, но `loop_scope` может расширить его область. Проверяйте режим и версию плагина в проекте.

У async-fixture есть две независимые области. `scope` задаёт, сколько тестов переиспользуют результат fixture, а `loop_scope` выбирает цикл для её setup и teardown. Цикл fixture должен жить как минимум столько же, сколько её кешированный результат. Цикл самого теста задаётся отдельно. Например, один незавершённый `Future` из module-fixture нельзя ожидать в тесте, который выполняется в другом, function-scoped цикле. Если тесты должны пользоваться ресурсом, привязанным к циклу модуля, настройте `@pytest_asyncio.fixture(scope="module", loop_scope="module")` и `@pytest.mark.asyncio(loop_scope="module")` у потребителей в том же модуле. Если ресурс не нужно разделять, создавайте его заново в цикле каждого теста. [pytest-asyncio: области fixture](https://pytest-asyncio.readthedocs.io/en/stable/reference/decorators/index.html), [общий цикл fixture и теста](https://pytest-asyncio.readthedocs.io/en/stable/how-to-guides/change_fixture_loop.html), [asyncio.Future.get_loop](https://docs.python.org/3/library/asyncio-future.html#asyncio.Future.get_loop).

Тестируйте результат, timeout и cleanup при отмене. `Task.cancel()` запрашивает кооперативную отмену: coroutine получает `CancelledError` когда цикл сможет доставить задаче отмену и должна освободить ресурсы в `finally`. Тесту нужно дождаться завершения отменённой задачи и проверить cleanup; одного вызова `cancel()` недостаточно. События/barrier делают порядок предсказуемее, чем `sleep`. `IsolatedAsyncioTestCase` отменяет оставшиеся задачи при завершении теста, но это не доказывает корректный cleanup приложения. [unittest async tests](https://docs.python.org/3/library/unittest.html#unittest.IsolatedAsyncioTestCase), [pytest-asyncio modes](https://pytest-asyncio.readthedocs.io/en/v1.4.1/concepts.html), [asyncio cancellation](https://docs.python.org/3/library/asyncio-task.html#task-cancellation).

Для подмены async-зависимости используйте `AsyncMock`. При прямом вызове `AsyncMock` регистрация вызова и ожидание — разные события: `assert_called_once_with(...)` может пройти, даже если код забыл `await`. `assert_awaited_once_with(...)` проверяет однократное ожидание с нужными аргументами. Это ещё не доказывает успешный результат или реальный сетевой обмен: mock может выбросить исключение при ожидании. [Python 3.15 AsyncMock](https://docs.python.org/3.15/library/unittest.mock.html#unittest.mock.AsyncMock).

При подмене через autospec учитывайте обёртку. В CPython 3.14.4 `create_autospec(publish)` для функции `async def publish(...)` возвращает coroutine-обёртку. Сразу после вызова обёртки внутренний mock ещё не фиксирует вызов. После `await` его проверка `assert_awaited_once_with(...)` проходит. Поэтому для контракта «дождаться зависимости» используйте проверку ожидания и при прямом `AsyncMock`, и при autospec.

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

Отдельно проверьте дедлайн операции. В Python 3.11+ `asyncio.timeout()` отменяет текущую задачу при истечении срока и преобразует вызванный этим `CancelledError` в `TimeoutError` на выходе из блока. В тесте срок `0` назначает отмену на следующую итерацию цикла. Ожидание незаданного события даёт циклу возможность её доставить, а проверка после выхода из блока подтверждает, что отправитель успел выполнить очистку.

```python
import asyncio
from unittest import IsolatedAsyncioTestCase
from unittest.mock import AsyncMock

class DeadlineTest(IsolatedAsyncioTestCase):
    async def test_deadline_closes_sender(self):
        close_sender = AsyncMock()

        with self.assertRaises(TimeoutError):
            async with asyncio.timeout(0):
                try:
                    await asyncio.Event().wait()
                finally:
                    await close_sender()

        close_sender.assert_awaited_once_with()
```

Такой тест проверяет обработку дедлайна и очистку. Для измерения времени возврата учитывайте, что сама очистка может занять дополнительное время. Если код подавит `CancelledError` внутри блока, ожидаемый `TimeoutError` может не появиться. [Таймауты asyncio в Python 3.14](https://docs.python.org/3.14/library/asyncio-task.html#timeouts).

## Q7. Как изолировать БД и внешние сервисы?

Unit test отделяет SQL-код/HTTP-клиент через явный интерфейс и проверяет ветвления. Integration test запускает реальную БД или совместимое тестовое окружение, проверяя SQL, ограничения, транзакции и драйвер; mock здесь не докажет совместимость. Создавайте схему/данные перед тестом, удаляйте их после, делайте тесты независимыми от порядка запуска.

Rollback после каждого теста удобен, но если приложение работает внутри внешней тестовой транзакции, вызов ORM `commit()` может не дать настоящего `COMMIT` в БД. Такая схема не доказывает видимость изменений другим соединениям, конкуренцию и эффекты после фиксации. Для этих случаев нужен отдельный сценарий с настоящим commit и гарантированным cleanup.

Например, в SQLAlchemy 2.0 тест открывает транзакцию на `Connection` и передаёт это соединение приложению через `Session(bind=connection, join_transaction_mode="create_savepoint")`. При поддержке SAVEPOINT драйвером и СУБД запись с `session.commit()` освобождает точку сохранения, оставляя внешнюю транзакцию открытой. В PostgreSQL тест может прочитать добавленный заказ через то же соединение. Воркер на другом соединении не увидит его до фиксации внешней транзакции, а teardown, который закрывает сессию и откатывает внешнюю транзакцию, удалит тестовую запись. Если приложение само откроет другое соединение и выполнит на нём COMMIT, этот rollback его изменений не затронет. Для сценария «воркер видит заказ после commit» разрешите настоящую фиксацию в изолированной БД и проверяйте чтение с другого соединения после неё.

Внешний HTTP заменяйте управляемым тестовым сервером, когда важны формат запроса, timeout и статус; патч подходит для локальной логики без сети. [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html), [SQLAlchemy 2.0: тестовая внешняя транзакция](https://docs.sqlalchemy.org/en/20/orm/session_transaction.html#joining-a-session-into-an-external-transaction-such-as-for-test-suites), [PostgreSQL `COMMIT`](https://www.postgresql.org/docs/current/sql-commit.html).

---

## See also

- [Основы Python](python-interview.md) — модель объектов и ресурсов.
- [Конкурентность в Python](python-concurrency-interview.md) — задачи, отмена и event loop.
- [Unit testing](../../testing/unit-testing-interview.md) — общие принципы тестирования.
