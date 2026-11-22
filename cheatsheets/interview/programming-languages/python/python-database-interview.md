---
title: "Вопросы на собеседовании: Работа с БД в Python"
description: "Параметры SQL в DB-API, транзакции, пул и загрузка ORM SQLAlchemy, sqlite3, sync/async драйверы"
tags:
  - interview
  - programming-languages
  - python-database-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Python database interview"
  - "Работа с БД в Python на собеседовании"
prerequisites:
  - "[[python-interview]]"
next: []
updated: "2026-11-02"
---
# Вопросы на собеседовании: Работа с БД в Python

Ориентир — Python DB-API 2.0, стандартный `sqlite3`, SQLAlchemy 2.0 и FastAPI. Конкретный синтаксис параметров, транзакций и асинхронного доступа проверяйте по выбранному драйверу. Общие уровни изоляции и работа СУБД разобраны в соседних темах.

## Полезные ссылки

- [PEP 249: DB-API 2.0](https://peps.python.org/pep-0249/) — общий интерфейс драйверов и стили параметров.
- [Python `sqlite3`](https://docs.python.org/3.15/library/sqlite3.html) — привязка значений в стандартной библиотеке.
- [SQLAlchemy 2.0 Session Basics](https://docs.sqlalchemy.org/en/20/orm/session_basics.html) — транзакция, `flush`, `commit` и `close`.
- [SQLAlchemy asyncio](https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html) — async-драйвер и границы `AsyncSession`.

## Содержание

- [Q1. Как безопасно передавать значения в SQL через Python DB-API?](#q1-как-безопасно-передавать-значения-в-sql-через-python-db-api)
- [Q2. Чем flush, commit и close отличаются в SQLAlchemy Session?](#q2-чем-flush-commit-и-close-отличаются-в-sqlalchemy-session)
- [Q3. Как выбрать sync и async доступ к БД в Python-сервисе?](#q3-как-выбрать-sync-и-async-доступ-к-бд-в-python-сервисе)
- [Q4. Закрывает ли with con соединение sqlite3 и как влияет autocommit?](#q4-закрывает-ли-with-con-соединение-sqlite3-и-как-влияет-autocommit)
- [Q5. Как разделить жизненный цикл Engine и Session в SQLAlchemy, чтобы не исчерпать пул?](#q5-как-разделить-жизненный-цикл-engine-и-session-в-sqlalchemy-чтобы-не-исчерпать-пул)
- [Q6. Почему после commit или закрытия Session нельзя полагаться на ленивую загрузку?](#q6-почему-после-commit-или-закрытия-session-нельзя-полагаться-на-ленивую-загрузку)
- [Q7. Почему AsyncSession может дать MissingGreenlet при доступе к ORM-связи?](#q7-почему-asyncsession-может-дать-missinggreenlet-при-доступе-к-orm-связи)
- [Q8. Как выбрать selectinload и joinedload для списка ORM-объектов с N+1?](#q8-как-выбрать-selectinload-и-joinedload-для-списка-orm-объектов-с-n1)
- [See also](#see-also)

## Q1. Как безопасно передавать значения в SQL через Python DB-API?

Передавайте пользовательские **значения** отдельно от текста запроса через параметры `execute()`. Конкатенация строк и Python-форматирование SQL позволяют вводу изменить структуру запроса. В стандартном `sqlite3` работают `?` и именованные маркеры; например, одно значение передаётся кортежем с завершающей запятой:

```python
cursor.execute("SELECT id FROM orders WHERE status = ?", (status,))
```

DB-API 2.0 допускает несколько `paramstyle`; не переносите `?` без проверки на другой драйвер. Параметризация отделяет значения от SQL, но не выбирает за вас имена таблиц или колонок. Если таблицу можно выбрать динамически, проверьте её имя по разрешённому набору и используйте API драйвера для SQL-идентификаторов. Например, в Psycopg 3 имя таблицы собирает `sql.Identifier`, а значение `status` остаётся параметром:

```python
from psycopg import sql

allowed_tables = {"orders", "archived_orders"}
if table_name not in allowed_tables:
    raise ValueError("Недопустимая таблица")

query = sql.SQL("SELECT id FROM {} WHERE status = %s").format(sql.Identifier(table_name))
cursor.execute(query, (status,))
```

Такой синтаксис относится к Psycopg 3; для другого драйвера проверьте его стиль параметров и способ композиции идентификаторов. [PEP 249: `paramstyle`](https://peps.python.org/pep-0249/#paramstyle), [Python `sqlite3`: placeholders](https://docs.python.org/3.15/library/sqlite3.html#how-to-use-placeholders-to-bind-values-in-sql-queries), [Psycopg: SQL composition](https://www.psycopg.org/psycopg3/docs/basic/params.html).

## Q2. Чем flush, commit и close отличаются в SQLAlchemy Session?

В SQLAlchemy 2.0 `Session.flush()` отправляет накопленные изменения в БД **внутри текущей транзакции**. Это может получить сгенерированный ID и выявить ошибку ограничения, но само по себе не фиксирует транзакцию. `Session.commit()` выполняет `flush()` для оставшихся изменений даже при `autoflush=False`, затем завершает транзакцию с `COMMIT`; `rollback()` отменяет её изменения. Эти гарантии атомарности предполагают транзакционный режим БД и драйвера: при driver-level autocommit поведение записи и отката отличается.

```python
from sqlalchemy.orm import Session

with Session(engine) as session:  # engine и mapped-класс Order настроены ранее
    with session.begin():
        session.add(Order(status="new"))
        session.flush()  # SQL отправлен, но COMMIT ещё не выполнен
```

Внутренний `session.begin()` завершает транзакцию: `commit` при успехе, `rollback` при исключении, которое вышло из блока. Внешний `with Session(engine)` закрывает сессию и освобождает ресурсы; **одно закрытие не заменяет commit**. Если `flush()` упал, БД обычно уже откатила транзакцию, но `Session` остаётся неактивной: перед повторным использованием той же сессии нужен явный `rollback()`. В показанном примере можно дать исключению выйти из `begin()`: контекст выполнит откат, затем внешняя область закроет сессию. Если ошибка перехвачена внутри `begin()`, не продолжайте работу в том же блоке как после успешного `flush()`. Не храните одну изменяемую `Session` как общий объект для конкурентных запросов. [SQLAlchemy Session Basics: flush and rollback](https://docs.sqlalchemy.org/en/20/orm/session_basics.html#flushing), [Session FAQ: concurrency](https://docs.sqlalchemy.org/en/20/orm/session_basics.html#is-the-session-thread-safe-is-asyncsession-safe-to-share-in-concurrent-tasks).

**Состояние объектов после полного отката.** Допустим, в одной транзакции изменили статус существующего заказа, удалили второй заказ и вставили третий через `flush()`. После `session.rollback()` удалённый заказ снова становится управляемым объектом, а оставшиеся в сессии объекты помечаются как истёкшие даже при `expire_on_commit=False`. Следующее чтение их атрибутов может выполнить SQL и получить состояние из БД. Вставленный в этой транзакции объект исключается из сессии. Его Python-атрибуты могут сохранить назначенный при `flush()` ID, хотя вставка откачена. Поэтому наличие ID после отката не подтверждает, что заказ сохранён. [SQLAlchemy 2.0: Rolling Back](https://docs.sqlalchemy.org/en/20/orm/session_basics.html#rolling-back).

**Частичный откат при импорте.** В SQLAlchemy 2.0 с PostgreSQL и Psycopg 3 можно пропустить уже импортированный заказ и продолжить внешнюю транзакцию. Для этого вставку каждой записи выполняют внутри `begin_nested()`, который создаёт точку сохранения. Пусть `uq_orders_external_id` — обычное, неотложенное ограничение уникальности идентификатора заказа из внешней системы.

```python
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

with Session(engine) as session, session.begin():
    for external_id in external_ids:
        try:
            with session.begin_nested():
                session.add(Order(external_id=external_id, status="new"))
                session.flush()
        except IntegrityError as exc:
            if (
                exc.orig.sqlstate != "23505"
                or exc.orig.diag.constraint_name != "uq_orders_external_id"
            ):
                raise
```

Ошибка должна выйти из вложенного блока: к моменту обработки исключения блок уже выполнил откат к точке сохранения. Успешный выход освобождает точку сохранения, а все принятые записи фиксируются только при успешном выходе из внешнего `session.begin()`. Не вызывайте `session.rollback()` для пропуска строки: он затронет внешнюю транзакцию. `begin_nested()` сбрасывает накопленные изменения **до** создания точки сохранения даже при `autoflush=False`, поэтому добавление текущего заказа стоит внутри вложенного блока. Если внешний контекст уже содержит несброшенные изменения, сбросьте их отдельно перед циклом, чтобы ошибка этого сброса не попала в обработчик дубликата. [SQLAlchemy: Using SAVEPOINT](https://docs.sqlalchemy.org/en/20/orm/session_transaction.html#using-savepoint), [Psycopg: ошибки и диагностика](https://www.psycopg.org/psycopg3/docs/api/errors.html).

## Q3. Как выбрать sync и async доступ к БД в Python-сервисе?

Выбор определяется **реальным API драйвера** и способом исполнения обработчика. В FastAPI синхронный `def` handler запускается в thread pool, поэтому обычный блокирующий DB-API вызов не останавливает event loop. Если тот же синхронный вызов напрямую сделать внутри `async def`, он блокирует loop: объявление handler асинхронным не превращает драйвер в async-драйвер.

Для асинхронного пути нужны совместимый async DB-драйвер и, при использовании SQLAlchemy 2.0, `AsyncEngine`/`AsyncSession` с `await` на I/O. Одну `AsyncSession` нельзя одновременно использовать из нескольких задач `asyncio.gather()`; создавайте отдельную сессию для каждой конкурентной задачи.

Если маршрут должен остаться `async def`, а доступный драйвер синхронный, явно вынесите **всю** блокирующую операцию в рабочий поток, например `await anyio.to_thread.run_sync(read_from_db, order_id)`. Создавайте и закрывайте синхронную `Session` внутри `read_from_db`, не передавайте её между конкурентными задачами. Поток и соединение всё равно заняты до завершения запроса; отмена ожидающего вызова не обязана останавливать уже выполняющийся код в потоке. Учитывайте лимиты thread pool и пула БД. Ни sync, ни async API сами по себе не снимают ограничения БД: задавайте timeout, ограничивайте параллелизм и измеряйте задержку. [FastAPI async](https://fastapi.tiangolo.com/async/#very-technical-details), [SQLAlchemy asyncio](https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html#using-asyncsession-with-concurrent-tasks), [Session concurrency](https://docs.sqlalchemy.org/en/20/orm/session_basics.html#is-the-session-thread-safe-is-asyncsession-safe-to-share-in-concurrent-tasks), [Starlette thread pool](https://www.starlette.io/threadpool/), [AnyIO worker threads](https://anyio.readthedocs.io/en/stable/threads.html).

**Дедлайн обработчика и отмена запроса к БД.** В AnyIO 4.1+ при стандартном `abandon_on_cancel=False` отмена через cancel scope не прерывает ожидание уже запущенной функции в потоке. При `abandon_on_cancel=True` ожидающая задача может выйти раньше, а функция продолжит работу. Например, если обработчик перестал ждать запись заказа, рабочий поток всё ещё может выполнить `COMMIT`. Таймаут HTTP-обработчика сам по себе не подтверждает откат. Для ограничения времени выполнения SQL задайте отдельный таймаут на стороне БД, а повтор операции записи проектируйте с учётом возможного успешного первого вызова. [AnyIO: run_sync](https://anyio.readthedocs.io/en/stable/api.html#anyio.to_thread.run_sync).

## Q4. Закрывает ли with con соединение sqlite3 и как влияет autocommit?

`with con` для `sqlite3.Connection` **не закрывает соединение**. При выходе без исключения он фиксирует открытую транзакцию, при исключении — откатывает её; если открытой транзакции нет, завершать нечего. Закрывайте соединение отдельно, в том числе после ошибки:

```python
import sqlite3

con = sqlite3.connect(":memory:", autocommit=False)
try:
    with con:
        con.execute("CREATE TABLE orders (id INTEGER PRIMARY KEY)")
        con.execute("INSERT INTO orders (id) VALUES (?)", (1,))
finally:
    con.close()
```

Параметр `autocommit` появился в Python 3.12. В режиме `False` `sqlite3` поддерживает открытую транзакцию и после выхода из `with con` открывает следующую. При `autocommit=True` вызовы `con.commit()` и `con.rollback()`, как и `with con`, не управляют транзакцией. Если в этом режиме выполнить SQL `BEGIN`, транзакция SQLite останется открытой после выхода из `with con`; завершить её нужно SQL `COMMIT` или `ROLLBACK`. Состояние можно проверить через `con.in_transaction`. В Python 3.15 значение по умолчанию ещё `LEGACY_TRANSACTION_CONTROL`; не переносите его поведение на явные режимы и проверяйте установленную версию. Это контракт стандартного `sqlite3`, а не каждого DB-API драйвера. [Python `sqlite3`: connection context manager](https://docs.python.org/3.15/library/sqlite3.html#how-to-use-the-connection-context-manager), [transaction control](https://docs.python.org/3.15/library/sqlite3.html#transaction-control), [SQLite transactions](https://www.sqlite.org/lang_transaction.html).

## Q5. Как разделить жизненный цикл Engine и Session в SQLAlchemy, чтобы не исчерпать пул?

`Engine` хранит конфигурацию доступа к БД и управляет соединениями; обычно его создают один раз на URL БД **в пределах процесса**. `Session` — изменяемая область работы с ORM и транзакцией. Создавайте отдельную сессию на запрос или другую короткую логическую операцию и завершайте её явно. Не создавайте новый `Engine` для каждого HTTP-запроса и не делите одну `Session` между конкурентными запросами.

```python
from sqlalchemy import create_engine, select
from sqlalchemy.orm import sessionmaker

engine = create_engine(DB_URL)  # конфигурация сервиса; DB_URL и Order заданы отдельно
SessionFactory = sessionmaker(engine)

def list_order_ids():
    with SessionFactory() as session:
        return session.scalars(select(Order.id)).all()
```

Пример возвращает готовые значения, а не ORM-объекты с возможной ленивой загрузкой после закрытия сессии; эта граница разобрана в Q6. Создание `Session` само по себе не означает, что соединение уже взято из пула. При первом обращении к БД сессия получает соединение и начинает транзакцию. После успешного `commit()`, явного `rollback()` или `Session.close()` транзакционные ресурсы освобождаются; закрытие сессии с незавершённой транзакцией откатывает её, а соединение возвращается в пул. Это касается и чтения: не держите сессию и открытую транзакцию во время долгого внешнего вызова или ожидания клиента.

Если запросы ждут свободное соединение или получают timeout пула, сначала проверьте длительность транзакций, своевременное закрытие сессий и число конкурентных операций. Для `QueuePool` затем сверяйте `pool_size`, `max_overflow` и `pool_timeout` с нагрузкой и лимитом соединений самой БД. Тип пула и доступные настройки зависят от диалекта и конфигурации; увеличение лимита не исправляет утечку или долгую транзакцию. [SQLAlchemy: Engine usage](https://docs.sqlalchemy.org/en/20/core/connections.html#basic-usage), [Session lifecycle](https://docs.sqlalchemy.org/en/20/orm/session_basics.html#when-do-i-construct-a-session-when-do-i-commit-it-and-when-do-i-close-it), [pool timeout](https://docs.sqlalchemy.org/en/20/errors.html#queuepool-limit-of-size-x-overflow-y-reached-connection-timed-out-timeout-z).

**Соединение вышло из строя.** Если БД перезапустилась, пока соединение простаивало в пуле, `create_engine(..., pool_pre_ping=True)` позволяет обнаружить разрыв при выдаче соединения и заменить его. Если связь оборвалась уже во время транзакции, приложение получит ошибку. Когда повтор допустим, начните новую транзакцию и повторите операцию целиком. Если клиент потерял связь во время `COMMIT`, ошибка не показывает, успела ли БД зафиксировать изменения. Для записи используйте ключ идемпотентности или проверьте результат перед повтором. [SQLAlchemy 2.0: проверка соединений при выдаче из пула](https://docs.sqlalchemy.org/en/20/core/pooling.html#disconnect-handling-pessimistic).

## Q6. Почему после commit или закрытия Session нельзя полагаться на ленивую загрузку?

В SQLAlchemy 2.0 `Session.commit()` по умолчанию **истекает** загруженное состояние ORM-объектов (`expire_on_commit=True`). Следующий доступ к истёкшему атрибуту может потребовать SQL-запрос. `Session.close()` отсоединяет объекты от сессии; незагруженную связь или истёкший атрибут после этого уже нельзя дозагрузить через неё. Поэтому передача ORM-объекта в сериализатор после закрытия сессии может закончиться `DetachedInstanceError`.

Для ответа API определите нужные поля и связи, загрузите их и соберите обычные данные или DTO **пока сессия открыта**. Например, для `Order.items`:

```python
from sqlalchemy import select
from sqlalchemy.orm import selectinload

def order_response(order_id: int):
    with SessionFactory() as session:  # фабрика и модель Order настроены заранее
        order = session.scalars(
            select(Order)
            .options(selectinload(Order.items))
            .where(Order.id == order_id)
        ).one()
        return {"id": order.id, "item_ids": [item.id for item in order.items]}
```

Возвращается словарь, уже собранный до выхода из `with`; закрытие сессии не требует ленивой загрузки при последующей сериализации. `expire_on_commit=False` оставляет **уже загруженные** данные после фиксации, но не загружает отсутствующие связи и не отменяет отсоединение при `close()`. `rollback()` истекает состояние независимо от этого параметра. Если ответ должен включать значения, появившиеся только после фиксации, получите их при открытой сессии и проверьте транзакционный сценарий отдельно. [SQLAlchemy Session Basics: commit/close](https://docs.sqlalchemy.org/en/20/orm/session_basics.html), [DetachedInstanceError](https://docs.sqlalchemy.org/en/20/errors.html#parent-instance-x-is-not-bound-to-a-session-lazy-load-deferred-load-refresh-etc-operation-cannot-proceed), [relationship loading](https://docs.sqlalchemy.org/en/20/orm/queryguide/relationships.html).

## Q7. Почему AsyncSession может дать MissingGreenlet при доступе к ORM-связи?

`AsyncSession` не делает обычное чтение `order.items` асинхронной операцией. Если связь ещё не загружена, такое чтение пытается выполнить **неявный SQL I/O без `await`**. В SQLAlchemy 2.0 это может вызвать `MissingGreenlet` даже пока `AsyncSession` открыта. В отличие от Q6, здесь причина не в отсоединённом объекте, а в месте вызова I/O.

Когда набор связей известен для ответа, загрузите их в явном запросе. `selectinload()` выполняет дополнительный SELECT в пределах ожидаемой async-операции, после чего загруженную связь можно читать как обычное поле:

```python
from sqlalchemy import select
from sqlalchemy.orm import selectinload

async def order_item_ids(order_id: int):
    async with AsyncSessionFactory() as session:  # фабрика и Order настроены заранее
        statement = select(Order).options(selectinload(Order.items)).where(Order.id == order_id)
        result = await session.execute(statement)
        order = result.scalar_one()
        return [item.id for item in order.items]
```

Если отложенная загрузка действительно нужна, модель с `AsyncAttrs` (доступен с SQLAlchemy 2.0.13) позволяет явно написать `await order.awaitable_attrs.items`. В обычном доступе `order.items` оператора `await` нет. `lazy="raise"` может выявить случайную дозагрузку до запуска SQL. `expire_on_commit=False` помогает с уже загруженными атрибутами после commit, но **не загружает отсутствующие связи**. Сверяйте версии и конфигурацию ORM; этот ответ относится к async ORM SQLAlchemy, а не ко всем async-драйверам. [SQLAlchemy asyncio: preventing implicit I/O](https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html#preventing-implicit-io-when-using-asyncsession), [MissingGreenlet](https://docs.sqlalchemy.org/en/20/errors.html#missinggreenlet).

## Q8. Как выбрать selectinload и joinedload для списка ORM-объектов с N+1?

Если список `Order` загружен одним запросом, а чтение `order.items` в цикле вызывает отдельный SELECT для каждого заказа, проверьте фактическое число SQL-запросов: это типичный N+1. Запросите нужную связь заранее. Для коллекции `selectinload(Order.items)` сначала получает заказы, затем загружает элементы дополнительным SELECT с `IN` по ключам выбранных заказов. При большой выборке таких дополнительных запросов может быть несколько.

`joinedload(Order.items)` получает связи через JOIN в основном запросе. Строки заказа повторяются для каждого элемента коллекции, поэтому при загрузке коллекции в SQLAlchemy 2.x **обязательно вызовите `Result.unique()`** перед получением списка ORM-объектов. Без него ORM выдаст ошибку. Для скалярной связи это требование не распространяется.

```python
from sqlalchemy import select
from sqlalchemy.orm import joinedload, selectinload

with SessionFactory() as session:  # Order.items — коллекция
    orders = session.scalars(
        select(Order).options(selectinload(Order.items))
    ).all()

    orders_with_join = session.scalars(
        select(Order).options(joinedload(Order.items))
    ).unique().all()
```

Выбирайте по форме связи и реальному объёму данных: `selectinload()` обычно удобен для коллекций без размножения строк основного результата; `joinedload()` может сэкономить отдельный запрос, когда размер JOIN ограничен. При `LIMIT` или `OFFSET` SQLAlchemy оборачивает исходный запрос в подзапрос и применяет JOIN для `joinedload()` к нему: загрузка коллекции не должна менять набор родительских объектов на странице. При этом строки результата всё ещё размножаются и `unique()` остаётся обязательным. Это свойство loader option `joinedload()`, а не любого явного `join()` для фильтрации. Сравните число SQL-запросов, переданные строки и время на типичной странице. Ни один способ не быстрее всегда. Для составного первичного ключа проверьте поддержку tuple `IN` в СУБД: например, SQL Server не поддерживает нужную форму для `selectinload()`. [SQLAlchemy 2.0: relationship loading](https://docs.sqlalchemy.org/en/20/orm/queryguide/relationships.html).

**Сценарий для страницы заказов.** Нужно вернуть до двух заказов, в которых есть товар с заданным SKU, и показать все позиции каждого заказа. Если первый заказ содержит две подходящие позиции, явный `join(Order.items)` с `LIMIT 2` может израсходовать лимит на две строки одного заказа. `unique()` уберёт повтор ORM-объекта, но второй заказ уже не попадёт в результат. Отфильтруйте заказы через `Order.items.any(OrderItem.sku == sku)`: для коллекции SQLAlchemy строит коррелированный `EXISTS`. Затем примените лимит к заказам и загрузите их коллекции отдельно.

```python
stmt = (
    select(Order)
    .where(Order.items.any(OrderItem.sku == sku))
    .order_by(Order.id)
    .limit(2)
    .options(selectinload(Order.items))
)
orders = session.scalars(stmt).all()
```

Фильтр решает, какие заказы попадут в страницу. `selectinload()` в этом примере загружает все их позиции, включая позиции с другими SKU. [SQLAlchemy 2.0: EXISTS для ORM-связей](https://docs.sqlalchemy.org/en/20/orm/queryguide/select.html#exists-forms-has-any).

**Сценарий для большого экспорта.** Стратегия загрузки для страницы не всегда подходит для потока из сотен тысяч заказов. В SQLAlchemy 2.0 `yield_per` позволяет читать ORM-результат порциями. Обходите результат итератором или через `partitions()`: вызов `.all()` снова соберёт его целиком. С `yield_per` нельзя совместить `joinedload()` коллекции или `Result.unique()`. Для глобального удаления дублей пришлось бы помнить все уже встреченные ORM-идентичности. `selectinload()` может работать с `yield_per`, если драйвер поддерживает несколько независимых курсоров: во время чтения основной выборки нужно выполнить запрос за позициями заказов.

Если выбранный драйвер не позволяет держать такие курсоры, можно читать заказы страницами по стабильному уникальному ключу, например `WHERE id > :last_id ORDER BY id LIMIT :batch_size`, и отдельно загружать позиции для каждой страницы. При отдельных транзакциях страницы могут видеть разные снимки. Если экспорт требует единого снимка, это отдельное требование к транзакции и уровню изоляции. [SQLAlchemy 2.0: Fetching Large Result Sets with Yield Per](https://docs.sqlalchemy.org/en/20/orm/queryguide/api.html#fetching-large-result-sets-with-yield-per).

---

## See also

- [Основы Python](python-interview.md) — модель языка и управление ресурсами.
- [Конкурентность в Python](python-concurrency-interview.md) — event loop, потоки и отмена.
- [Упаковка Python-приложений](python-packaging-interview.md) — выбор и установка драйвера.
- [FastAPI](../../frameworks/python/fastapi-interview.md) — lifecycle зависимости и HTTP-обработчика.
- [Транзакции БД](../../databases/database-transactions-interview.md) — изоляция и поведение СУБД.
