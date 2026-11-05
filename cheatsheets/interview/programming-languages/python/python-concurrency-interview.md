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
updated: "2026-11-05"
---
# Вопросы на собеседовании: Конкурентность в Python

Ориентир — стандартная библиотека Python 3.11–3.15 и обычная сборка CPython; особенности free-threaded сборки оговорены отдельно. `asyncio` полезен для множества ожидающих I/O задач, а не автоматически ускоряет вычисления.

## Полезные ссылки

- [asyncio tasks](https://docs.python.org/3/library/asyncio-task.html) — задачи, TaskGroup, отмена и timeout.
- [asyncio queues](https://docs.python.org/3/library/asyncio-queue.html) и [sync primitives](https://docs.python.org/3/library/asyncio-sync.html) — backpressure и координация.
- [concurrent.futures](https://docs.python.org/3/library/concurrent.futures.html) — пулы потоков и процессов.
- [Free threading](https://docs.python.org/3/howto/free-threading-python.html) — особая сборка CPython.
- [PEP 803](https://peps.python.org/pep-0803/) — Stable ABI `abi3t` для free-threaded CPython 3.15+.

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

Вызов `async def` создаёт coroutine object; сам по себе он **не запускает** тело. `await coro` исполняет его в текущей задаче, а `asyncio.create_task(coro)` планирует отдельную Task в event loop. `await` может приостановить текущую задачу, когда ожидаемый результат ещё не готов; ожидание уже завершённого `Future` не обязано передавать управление другим задачам. Длинное синхронное вычисление блокирует event loop, даже если рядом в коде есть `await`.

```python
import asyncio

async def load():
    return 42

async def main():
    task = asyncio.create_task(load())
    return await task
```

У запущенной фоновой Task держите ссылку и обработайте результат/исключение. Не рассчитывайте, что вызов `create_task` всегда откладывает первый шаг до следующего оборота loop: с Python 3.14 параметр `eager_start=True` позволяет начать выполнение сразу, до первого блокирующего `await`; аналогичный режим может задавать фабрика задач. Сам вызов `async def` по-прежнему только создаёт coroutine object. Для связанных задач чаще удобнее `TaskGroup` (Q4). [Coroutines and Tasks](https://docs.python.org/3.15/library/asyncio-task.html#awaitables), [Creating tasks](https://docs.python.org/3.15/library/asyncio-task.html#creating-tasks).

## Q4. Чем TaskGroup отличается от gather?

`asyncio.gather(*aws)` собирает результаты в порядке аргументов; при обычном исключении одной задачи с `return_exceptions=False` исключение передаётся вызывающему, **но остальные awaitables автоматически не отменяются** и могут продолжить работу после выхода вызывающего из `await gather`. Если после получения ошибки вызвать `gather.cancel()`, уже завершённый `gather` не отменит их. Отмена самого `gather` до его завершения, напротив, передаётся незавершённым задачам. `TaskGroup` (с Python 3.11) владеет дочерними задачами: при первом исключении, отличном от `CancelledError`, отменяет ещё работающие задачи и дожидается их завершения. Обычные ошибки он объединяет в `ExceptionGroup` или `BaseExceptionGroup`; `KeyboardInterrupt` и `SystemExit` после ожидания соседей пробрасываются отдельно.

```python
import asyncio

async def load_context():
    async with asyncio.TaskGroup() as group:
        users = group.create_task(load_users())
        roles = group.create_task(load_roles())
    # Здесь обе задачи завершены; при ошибке одной ещё работающая задача отменена.
    return users.result(), roles.result()
```

Выбирайте `TaskGroup` для связанных операций с общим сроком жизни. Если нужна частичная успешность и независимое продолжение, явно продумайте обработку ошибок (`gather(..., return_exceptions=True)` — один из вариантов). [Task groups](https://docs.python.org/3.15/library/asyncio-task.html#task-groups), [`gather`](https://docs.python.org/3.15/library/asyncio-task.html#asyncio.gather).

## Q5. Как работает отмена и timeout в asyncio?

`task.cancel()` запрашивает отмену: `CancelledError` поступает в coroutine при следующей возможности исполнения. Это **кооперативная** отмена, поэтому `try/finally` должен закрыть ресурс, а затем ошибку обычно нужно пропустить дальше. Проглатывание `CancelledError` может ломать `TaskGroup` и `asyncio.timeout()`; это не обычный `Exception`, а подкласс `BaseException`, поэтому `except Exception` его не ловит.

```python
import asyncio

async def fetch_with_deadline():
    async with asyncio.timeout(2):  # Python 3.11+
        return await remote_call()
```

При истечении срока контекстный менеджер отменяет текущую Task и при выходе преобразует вызванную им отмену в `TimeoutError`, который вызывающий код ловит **снаружи** блока. Внешняя отмена задачи остаётся `CancelledError`. Долгий cleanup при отмене может задержать фактическое завершение после дедлайна. Сам по себе timeout не обязан остановить уже запущенный в другом потоке блокирующий вызов; задавайте также timeout в сетевом клиенте. [Task cancellation](https://docs.python.org/3.15/library/asyncio-task.html#task-cancellation), [`asyncio.timeout`](https://docs.python.org/3.15/library/asyncio-task.html#timeouts).

## Q6. Что делать с блокирующим вызовом внутри event loop?

Блокирующий `time.sleep()` или синхронный сетевой клиент внутри coroutine останавливает весь event loop. Для I/O лучше async API библиотеки; если его нет, `await asyncio.to_thread(blocking_call, arg)` отправит функцию в поток и позволит loop обслуживать другие задачи.

`to_thread` не делает чисто Python CPU-код параллельным на ядрах в обычной сборке CPython из-за GIL. Отмена ожидающей coroutine не гарантирует принудительную остановку уже исполняющейся функции в потоке. [Running in Threads](https://docs.python.org/3/library/asyncio-task.html#running-in-threads).

## Q7. Как ограничить число ожидающих задач?

Безграничный `create_task` может накопить память и перегрузить downstream. `asyncio.Queue(maxsize=N)` при `N > 0` заставляет producer ждать на `put`, когда очередь заполнена: это backpressure, если новую работу создают после ожидания, а не заранее в тысячах Task. Worker вызывает `task_done()` после обработки, а `join()` позволяет дождаться всех поставленных элементов. `asyncio.Semaphore` ограничивает число одновременно выполняющихся операций, но не ограничивает **число созданных ожидающих задач**.

Пример создаёт фиксированные четыре worker-задачи, а производитель ожидает место в очереди. `handle` — асинхронная функция обработки одного элемента:

```python
import asyncio

async def run_jobs(jobs, handle):
    queue = asyncio.Queue(maxsize=16)

    async def worker():
        while True:
            job = await queue.get()
            try:
                await handle(job)
            finally:
                queue.task_done()

    async with asyncio.TaskGroup() as group:
        workers = [group.create_task(worker()) for _ in range(4)]
        for job in jobs:
            await queue.put(job)
        await queue.join()
        for task in workers:
            task.cancel()
```

`task_done()` уравновешивает каждый успешный `get()` даже при исключении или отмене обработчика; это учёт завершения попытки, а не подтверждение бизнес-успеха. Ошибка `handle` выходит в `TaskGroup`, который отменяет соседей и прерывает ожидание производителя. Повторы, сохранение результата и обработку ошибки задаёт вызывающий код. Ограничены очередь и число worker-задач; уже материализованный входной список от этого не становится меньше.

Для backend-сервиса задавайте также таймауты, лимиты HTTP/DB-клиента и политику отказа при перегрузке. `asyncio.Queue` предназначена для задач одного event loop и не является thread-safe. [Queues](https://docs.python.org/3/library/asyncio-queue.html), [Semaphore](https://docs.python.org/3/library/asyncio-sync.html#semaphore).

## Q8. Какие ограничения есть у ProcessPoolExecutor?

`ProcessPoolExecutor` выполняет код в отдельных процессах и обходит GIL обычной сборки CPython. Цена — запуск процессов и сериализация аргументов/результатов: передаваемые объекты и функции должны быть picklable, а `__main__` должен импортироваться worker-процессом. Lambda/функции из REPL обычно не подходят.

В Python 3.14 `ProcessPoolExecutor` больше не выбирает `fork` по умолчанию. Не рассчитывайте на неявно унаследованные соединения и глобальное состояние родительского процесса; способ запуска задают через `mp_context`, когда он важен для приложения. Пример нужно запускать как импортируемый `.py`-модуль:

```python
from concurrent.futures import ProcessPoolExecutor
from multiprocessing import get_context

def sum_squares(values):
    return sum(value * value for value in values)

if __name__ == "__main__":
    with ProcessPoolExecutor(max_workers=2, mp_context=get_context("spawn")) as pool:
        print(list(pool.map(sum_squares, ([1, 2], [3, 4]))))  # [5, 25]
```

Функция объявлена на уровне модуля, а создание пула защищено `if __name__ == "__main__"`; worker может импортировать модуль без рекурсивного создания нового пула. Не вызывайте методы этого `Executor` или его `Future` из задачи, уже выполняющейся в пуле: документация предупреждает о deadlock. Для коротких операций с большими данными обмен может оказаться дороже вычисления; измеряйте end-to-end latency. [ProcessPoolExecutor](https://docs.python.org/3/library/concurrent.futures.html#processpoolexecutor).

## Q9. Что меняет free-threaded CPython?

Отдельная **free-threaded сборка** CPython (доступна с Python 3.13) позволяет потокам параллельно исполнять Python-код без обычного GIL. Это не свойство всех установок Python 3.13+: режим зависит от сборки и runtime. `sysconfig.get_config_var("Py_GIL_DISABLED") == 1` проверяет поддержку режима сборкой, а `sys._is_gil_enabled()` — включён ли GIL сейчас. Даже в free-threaded сборке GIL можно включить через `PYTHON_GIL`/`-X gil`; импорт расширения без объявленной поддержки free-threading также может включить его с предупреждением.

Перед развёртыванием проверьте wheel каждой нативной зависимости под конкретные CPython, ОС и архитектуру, затем запустите приложение и проверьте состояние GIL **после импорта** зависимостей. С Python 3.15 для расширений появился отдельный Stable ABI `abi3t`: wheel с `abi3t` предназначен для free-threaded сборки, а `abi3.abi3t` сообщает о совместимости с обеими сборками при соответствующей компиляции. Например, `cp315-abi3` не подходит CPython 3.15t, а `cp315-abi3.abi3t` обозначает совместимость с обеими сборками. Тег wheel также не доказывает потокобезопасность логики расширения. [Free-threading guide](https://docs.python.org/3.15/howto/free-threading-python.html), [PEP 803](https://peps.python.org/pep-0803/). См. также [повторяемую установку зависимостей](python-packaging-interview.md#q4-как-сделать-установку-зависимостей-повторяемой-в-ci-и-deployment).

Stable ABI — один из вариантов поставки расширения. Wheel с тегами `cp315-cp315t-<platform>` также может подходить free-threaded CPython 3.15 на своей платформе; он не обещает совместимость с другими минорными версиями. Поэтому `abi3t` не следует считать единственным допустимым тегом бинарной зависимости. [Таблица совместимости PEP 803](https://peps.python.org/pep-0803/#compatibility-overview).

Отсутствие GIL увеличивает важность явной синхронизации общего изменяемого состояния: `lock` или один владелец состояния защищает весь бизнес-инвариант, а внутренние блокировки контейнеров этого не заменяют. Тестируйте совместимость расширений и производительность на реальной нагрузке, прежде чем менять архитектуру. [Free-threading guide](https://docs.python.org/3/howto/free-threading-python.html).

---

## See also

- [Основы Python](python-interview.md) — объекты, функции, итераторы и ресурсы.
- [Конкурентность в Go](../go/go-concurrency-interview.md) — другая модель планирования и отмены.
