---
title: "Вопросы на собеседовании: Конкурентность в Python"
description: "asyncio, TaskGroup, отмена, backpressure, потоки, процессы, GIL и free-threaded CPython для backend-сервисов"
tags:
  - interview
  - programming-languages
  - python-concurrency-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Python concurrency interview"
  - "asyncio на собеседовании"
prerequisites:
  - "[[python-interview]]"
next: []
updated: "2026-10-05"
---
# Вопросы на собеседовании: Конкурентность в Python

Ориентир — стандартная библиотека Python 3.11–3.14 и обычная сборка CPython; особенности free-threaded сборки оговорены отдельно. `asyncio` полезен для множества ожидающих I/O задач, а не автоматически ускоряет вычисления.

## Полезные ссылки

- [asyncio tasks](https://docs.python.org/3/library/asyncio-task.html) — задачи, TaskGroup, отмена и timeout.
- [asyncio queues](https://docs.python.org/3/library/asyncio-queue.html) и [sync primitives](https://docs.python.org/3/library/asyncio-sync.html) — backpressure и координация.
- [concurrent.futures](https://docs.python.org/3/library/concurrent.futures.html) — пулы потоков и процессов.
- [Free threading](https://docs.python.org/3/howto/free-threading-python.html) — особая сборка CPython.

## Содержание

- [Q1. Когда выбрать asyncio, потоки или процессы?](#q1-когда-выбрать-asyncio-потоки-или-процессы)
- [Q2. Что ограничивает GIL в обычном CPython?](#q2-что-ограничивает-gil-в-обычном-cpython)
- [Q3. Чем coroutine отличается от Task?](#q3-чем-coroutine-отличается-от-task)
- [Q4. Чем TaskGroup отличается от gather?](#q4-чем-taskgroup-отличается-от-gather)
- [Q5. Как работает отмена и timeout в asyncio?](#q5-как-работает-отмена-и-timeout-в-asyncio)
- [Q6. Что делать с блокирующим вызовом внутри event loop?](#q6-что-делать-с-блокирующим-вызовом-внутри-event-loop)
- [Q7. Как ограничить число ожидающих задач?](#q7-как-ограничить-число-ожидающих-задач)
- [Q8. Какие ограничения есть у ProcessPoolExecutor?](#q8-какие-ограничения-есть-у-processpoolexecutor)
- [Q9. Что меняет free-threaded CPython?](#q9-что-меняет-free-threaded-cpython)
- [See also](#see-also)

## Q1. Когда выбрать asyncio, потоки или процессы?

Для большого числа **асинхронных I/O** операций (сетевые запросы, async-драйвер БД) выбирают `asyncio`, если используемые библиотеки действительно не блокируют loop. Потоки подходят для существующего блокирующего I/O или библиотек, отпускающих GIL. Процессы подходят для тяжёлого чисто Python CPU-кода в обычном CPython, если цена передачи данных и запуска окупается.

Для одного HTTP-запроса сначала оцените число одновременных операций, лимиты downstream и возможность отмены. «Async» не означает параллельное выполнение Python-кода на ядрах; процессный пул не бесплатен. [asyncio](https://docs.python.org/3/library/asyncio.html), [concurrent.futures](https://docs.python.org/3/library/concurrent.futures.html).

## Q2. Что ограничивает GIL в обычном CPython?

В стандартной сборке CPython GIL обычно не даёт нескольким потокам одновременно исполнять Python bytecode в одном интерпретаторе. Потоки при этом полезны для I/O; некоторые расширения освобождают GIL во время работы. GIL **не** делает составную операцию над общим состоянием логически атомарной: для инвариантов нужны синхронизация или владение состоянием.

Для CPU-нагрузки сравнивайте процессы, нативные расширения и free-threaded сборку с замером. Утверждение «Python никогда не использует несколько ядер» неверно: речь о конкретной сборке и типе работы. [threading](https://docs.python.org/3/library/threading.html#gil-and-performance-considerations).

## Q3. Чем coroutine отличается от Task?

Вызов `async def` создаёт coroutine object; сам по себе он **не запускает** тело. `await coro` исполняет его в текущей задаче, а `asyncio.create_task(coro)` планирует отдельную Task в event loop. `await` отдаёт управление loop в точках ожидания; длинное синхронное вычисление между ними блокирует остальные задачи.

```python
async def load():
    return 42

async def main():
    task = asyncio.create_task(load())
    return await task
```

У запущенной фоновой Task держите ссылку и обработайте результат/исключение. Для связанных задач чаще удобнее `TaskGroup` (Q4). [Coroutines and Tasks](https://docs.python.org/3/library/asyncio-task.html#awaitables).

## Q4. Чем TaskGroup отличается от gather?

`asyncio.gather(*aws)` собирает результаты в порядке аргументов; при обычном исключении одной задачи с `return_exceptions=False` исключение передаётся вызывающему, **но остальные awaitables автоматически не отменяются**. `TaskGroup` (с Python 3.11) владеет дочерними задачами: при первом исключении, отличном от `CancelledError`, отменяет остальные, дожидается их завершения и сообщает ошибки через `ExceptionGroup`.

```python
async with asyncio.TaskGroup() as group:
    users = group.create_task(load_users())
    roles = group.create_task(load_roles())
# Здесь обе задачи завершены; при ошибке одного запроса второй отменён.
return users.result(), roles.result()
```

Выбирайте `TaskGroup` для связанных операций с общим сроком жизни. Если нужна частичная успешность и независимое продолжение, явно продумайте обработку ошибок (`gather(..., return_exceptions=True)` — один из вариантов). [Task groups](https://docs.python.org/3/library/asyncio-task.html#task-groups).

## Q5. Как работает отмена и timeout в asyncio?

`task.cancel()` запрашивает отмену: `CancelledError` поступает в coroutine при следующей возможности исполнения. Это **кооперативная** отмена, поэтому `try/finally` должен закрыть ресурс, а затем ошибку обычно нужно пропустить дальше. Проглатывание `CancelledError` может ломать `TaskGroup` и `asyncio.timeout()`; это не обычный `Exception`, а подкласс `BaseException`.

```python
async with asyncio.timeout(2):  # Python 3.11+
    result = await remote_call()
```

За пределами блока таймаут виден как `TimeoutError`. Сам по себе timeout не обязан остановить уже запущенный в другом потоке блокирующий вызов; задавайте также timeout в сетевом клиенте. [Task cancellation](https://docs.python.org/3/library/asyncio-task.html#task-cancellation).

## Q6. Что делать с блокирующим вызовом внутри event loop?

Блокирующий `time.sleep()` или синхронный сетевой клиент внутри coroutine останавливает весь event loop. Для I/O лучше async API библиотеки; если его нет, `await asyncio.to_thread(blocking_call, arg)` отправит функцию в поток и позволит loop обслуживать другие задачи.

`to_thread` не делает чисто Python CPU-код параллельным на ядрах в обычной сборке CPython из-за GIL. Отмена ожидающей coroutine не гарантирует принудительную остановку уже исполняющейся функции в потоке. [Running in Threads](https://docs.python.org/3/library/asyncio-task.html#running-in-threads).

## Q7. Как ограничить число ожидающих задач?

Безграничный `create_task` может накопить память и перегрузить downstream. `asyncio.Queue(maxsize=N)` блокирует producer на `put`, когда очередь заполнена: это backpressure. Worker вызывает `task_done()` после обработки, а `join()` позволяет дождаться всех поставленных элементов. `asyncio.Semaphore` ограничивает число одновременно выполняющихся операций, но не ограничивает **число созданных ожидающих задач**.

Для backend-сервиса задавайте также таймауты, лимиты HTTP/DB-клиента и политику отказа при перегрузке. `asyncio.Queue` предназначена для задач одного event loop и не является thread-safe. [Queues](https://docs.python.org/3/library/asyncio-queue.html), [Semaphore](https://docs.python.org/3/library/asyncio-sync.html#semaphore).

## Q8. Какие ограничения есть у ProcessPoolExecutor?

`ProcessPoolExecutor` выполняет код в отдельных процессах и обходит GIL обычной сборки CPython. Цена — запуск процессов и сериализация аргументов/результатов: передаваемые объекты и функции должны быть picklable, а `__main__` должен импортироваться worker-процессом. Lambda/функции из REPL обычно не подходят.

Не вызывайте методы самого пула из задачи, уже выполняющейся в нём: документация предупреждает о deadlock. Для коротких операций с большими данными обмен может оказаться дороже вычисления; измеряйте end-to-end latency. [ProcessPoolExecutor](https://docs.python.org/3/library/concurrent.futures.html#processpoolexecutor).

## Q9. Что меняет free-threaded CPython?

Отдельная **free-threaded сборка** CPython (доступна с Python 3.13) позволяет потокам параллельно исполнять Python-код без обычного GIL. Это не свойство всех установок Python 3.13/3.14: режим зависит от сборки и runtime, а несовместимое расширение может вновь включить GIL. Наличие режима проверяют через `sys._is_gil_enabled()` там, где API доступен.

Отсутствие GIL увеличивает важность явной синхронизации общего изменяемого состояния; внутренние блокировки контейнеров не заменяют защиту бизнес-инварианта. Тестируйте совместимость расширений и производительность на реальной нагрузке, прежде чем менять архитектуру. [Free-threading guide](https://docs.python.org/3/howto/free-threading-python.html).

---

## See also

- [Основы Python](python-interview.md) — объекты, функции, итераторы и ресурсы.
- [Конкурентность в Go](../go/go-concurrency-interview.md) — другая модель планирования и отмены.
