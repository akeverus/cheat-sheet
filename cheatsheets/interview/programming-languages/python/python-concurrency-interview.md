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

**Пример выбора в одном сервисе.** Для получения данных от нескольких HTTP-сервисов используйте асинхронный клиент с ограничением числа запросов. Если одна интеграция доступна только через блокирующий SDK, вынесите её вызовы в ограниченный пул потоков. Если после получения данных долго выполняется расчёт на чистом Python, измерьте его отдельно: в обычной сборке CPython такой расчёт можно вынести в процессный пул либо, с Python 3.14, проверить `InterpreterPoolExecutor` для независимых задач с компактными входами (Q8). В free-threaded сборке можно сравнить с обычным пулом потоков после проверки зависимостей и состояния GIL (Q9). При сравнении включите в p95 время ожидания места в пуле и передачи данных. [Executor implementations](https://docs.python.org/3.14/library/concurrent.futures.html), [Free-threaded Python](https://docs.python.org/3.14/howto/free-threading-python.html).

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

В Python 3.15 появился `TaskGroup.cancel()` для штатного досрочного завершения группы. Например, сервис ищет первое подходящее предложение у нескольких поставщиков. Когда одна задача сохраняет подходящий результат, она вызывает `group.cancel()`. Группа запрашивает отмену незавершённых задач и прерывает выполнение тела `async with`, если оно ещё работает. При выходе группа дожидается завершения дочерних задач. Если причиной выхода была только эта отмена, вызывающий код продолжает работу после блока и может использовать сохранённый результат. Внешняя отмена и обычные ошибки дочерних задач по-прежнему обрабатываются по правилам группы. В Python 3.11–3.14 метода нет. [TaskGroup.cancel](https://docs.python.org/3.15/library/asyncio-task.html#asyncio.TaskGroup.cancel).


## Q5. Как работает отмена и timeout в asyncio?

`task.cancel()` запрашивает отмену: `CancelledError` поступает в coroutine при следующей возможности исполнения. Это **кооперативная** отмена, поэтому `try/finally` должен закрыть ресурс, а затем ошибку обычно нужно пропустить дальше. Проглатывание `CancelledError` может ломать `TaskGroup` и `asyncio.timeout()`; это не обычный `Exception`, а подкласс `BaseException`, поэтому `except Exception` его не ловит.

```python
import asyncio

async def fetch_with_deadline():
    async with asyncio.timeout(2):  # Python 3.11+
        return await remote_call()
```

При истечении срока контекстный менеджер отменяет текущую Task и при выходе преобразует вызванную им отмену в `TimeoutError`, который вызывающий код ловит **снаружи** блока. Внешняя отмена задачи остаётся `CancelledError`. Долгий cleanup при отмене может задержать фактическое завершение после дедлайна. Сам по себе timeout не обязан остановить уже запущенный в другом потоке блокирующий вызов; задавайте также timeout в сетевом клиенте. [Task cancellation](https://docs.python.org/3.15/library/asyncio-task.html#task-cancellation), [`asyncio.timeout`](https://docs.python.org/3.15/library/asyncio-task.html#timeouts).

**Если перехватить отмену внутри блока.** Допустим, обработчик ловит `CancelledError` от своего дедлайна и продолжает выполнение. Если он выходит из `async with` штатно, снаружи не будет `TimeoutError`: менеджеру уже нечего преобразовывать. Для освобождения ресурсов используйте `finally` или после обработки снова поднимите `CancelledError` через `raise`. У `Task` есть счётчик запросов отмены: `cancelling()` показывает число вызовов `cancel()` за вычетом `uncancel()`, а `cancelled()` проверяет, завершилась ли задача отменой. Перехват исключения сам по себе не уменьшает счётчик. Если код намеренно подавляет отмену и должен полностью снять собственный запрос, у `Task` для этого есть `uncancel()` (Python 3.11+). Для обычной обработки дедлайна вручную вызывать его не нужно. [`Task.cancelling()`](https://docs.python.org/3.15/library/asyncio-task.html#asyncio.Task.cancelling), [`Task.uncancel()`](https://docs.python.org/3.15/library/asyncio-task.html#asyncio.Task.uncancel).

**Дедлайн ожидания одной операции.** `asyncio.wait_for(task, timeout=2)` при истечении срока запрашивает отмену самой ожидаемой задачи и дожидается её завершения. Например, если чтение ответа прекращается с закрытием соединения в `finally`, этот cleanup может отодвинуть возврат за пределы двух секунд. Если отмена обработана с возвратом значения, `wait_for` может вернуть это значение.

Если HTTP-обработчик только перестаёт ждать экспорт, а экспорт должен продолжиться, можно дождаться результата через `done, pending = await asyncio.wait({export_task}, timeout=2)`. По таймауту задача попадёт в `pending` и продолжит работу. Владелец фоновой задачи должен сохранить ссылку и обработать её результат или ошибку. Для экспорта, который должен пережить перезапуск сервиса, отслеживаемая задача в памяти не даёт такой гарантии. [`wait_for`](https://docs.python.org/3.14/library/asyncio-task.html#asyncio.wait_for), [`wait`](https://docs.python.org/3.14/library/asyncio-task.html#asyncio.wait).

## Q6. Что делать с блокирующим вызовом внутри event loop?

Блокирующий `time.sleep()` или синхронный сетевой клиент внутри coroutine останавливает весь event loop. Для I/O лучше async API библиотеки; если его нет, `await asyncio.to_thread(blocking_call, arg)` отправит функцию в поток и позволит loop обслуживать другие задачи.

`to_thread` не делает чисто Python CPU-код параллельным на ядрах в обычной сборке CPython из-за GIL. Отмена ожидающей coroutine не гарантирует принудительную остановку уже исполняющейся функции в потоке. [Running in Threads](https://docs.python.org/3/library/asyncio-task.html#running-in-threads).

**Контекст запроса при переносе I/O в поток.** В Python 3.9+ `asyncio.to_thread()` переносит текущий контекст `contextvars`. Если обработчик сохранил идентификатор запроса в `ContextVar`, синхронная функция в потоке сможет прочитать его для логов. Новое значение, установленное этой функцией через `ContextVar.set()`, останется в её контексте. При прямом вызове `loop.run_in_executor()` для обычного пула потоков перенос нужно задать явно, например `await loop.run_in_executor(pool, contextvars.copy_context().run, blocking_call)`. Копия контекста переносит ссылки на значения, поэтому общий изменяемый объект по-прежнему требует синхронизации. [Running in Threads](https://docs.python.org/3.14/library/asyncio-task.html#asyncio.to_thread), [Context Variables](https://docs.python.org/3.14/library/contextvars.html).

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

В Python 3.13+ при штатной остановке сервиса можно вызвать `queue.shutdown()`, когда приём новых заданий закрыт. Новые и уже ожидающие `put()` завершаются с `QueueShutDown`. Worker дочитывает принятые задания, для каждого вызывает `task_done()` и выходит, когда очередной `get()` получает `QueueShutDown`. Так можно дождаться обработки очереди через `join()`, а затем выхода worker-задач. При `shutdown(immediate=True)` очередь сбрасывает ещё не взятые задания. Это не отменяет уже работающие обработчики, а возврат из `join()` больше не означает, что для каждого принятого задания была попытка обработки. [Queue.shutdown](https://docs.python.org/3.14/library/asyncio-queue.html#asyncio.Queue.shutdown).

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

**Большой поток входных данных.** В примере выше вход маленький. Если сервис читает задания из большого файла, `pool.map(process_record, records, buffersize=8, chunksize=16)` в Python 3.14+ приостанавливает чтение входного итератора, когда буфер отправленных задач заполняется, и возобновляет его по мере выдачи результатов. Для пула процессов единица отправки — пачка, поэтому `buffersize=8` относится к задачам-пачкам, а не к отдельным записям или байтам. Обрабатывайте результаты по мере чтения вместо `list(...)`, если накапливать их не нужно. `map()` выдаёт результаты в порядке входа, поэтому медленная ранняя пачка может задержать выдачу уже готовых. Если порядок не нужен, можно держать ограниченное число `Future` и добавлять новые задания по мере завершения через `wait(..., return_when=FIRST_COMPLETED)`. [Executor.map](https://docs.python.org/3.14/library/concurrent.futures.html#concurrent.futures.Executor.map), [wait](https://docs.python.org/3.14/library/concurrent.futures.html#concurrent.futures.wait).

**Таймаут ожидания и остановка работы.** Если `future.result(timeout=1)` выбрасывает `TimeoutError`, клиент перестал ждать, но уже запущенный расчёт может продолжить занимать процесс. `future.cancel()` отменяет задачу, только пока она ещё допускает отмену. Если расчёты регулярно превышают дедлайн, ограничьте очередь и передавайте дедлайн в саму функцию, чтобы она могла завершаться в контрольных точках.

При зависании Python 3.14+ позволяет вызвать `terminate_workers()` или `kill_workers()`. Оба метода действуют на все живые процессы этого executor, поэтому вместе с зависшим расчётом прервутся и соседние задачи, выполняемые в пуле. После такого вызова для новых задач нужен новый executor. Перед повтором операций с внешними эффектами проверьте, что успело выполниться. [Future](https://docs.python.org/3.14/library/concurrent.futures.html#future-objects), [ProcessPoolExecutor](https://docs.python.org/3.14/library/concurrent.futures.html#processpoolexecutor).


**Альтернатива с Python 3.14 — `InterpreterPoolExecutor`.** Каждый worker работает в своём потоке и в отдельном интерпретаторе CPython со своим GIL. Поэтому вычисления на чистом Python могут идти параллельно на нескольких ядрах и в одном процессе. У интерпретаторов отдельное состояние модулей, а вызов, аргументы и результат передаются через `pickle`. Например, независимые расчёты по компактным порциям данных можно отправить в такой пул. Если расчёт зависит от общего изменяемого кеша или нативного модуля, сначала проверьте цену передачи данных и поддержку нескольких интерпретаторов. Для изоляции от аварии всего процесса по-прежнему нужна граница процессов. [InterpreterPoolExecutor](https://docs.python.org/3.14/library/concurrent.futures.html#interpreterpoolexecutor).

## Q9. Что меняет free-threaded CPython?

Отдельная **free-threaded сборка** CPython (доступна с Python 3.13) позволяет потокам параллельно исполнять Python-код без обычного GIL. Это не свойство всех установок Python 3.13+: режим зависит от сборки и runtime. `sysconfig.get_config_var("Py_GIL_DISABLED") == 1` проверяет поддержку режима сборкой, а `sys._is_gil_enabled()` — включён ли GIL сейчас. Даже в free-threaded сборке GIL можно включить через `PYTHON_GIL`/`-X gil`; импорт расширения без объявленной поддержки free-threading также может включить его с предупреждением.

Перед развёртыванием проверьте wheel каждой нативной зависимости под конкретные CPython, ОС и архитектуру, затем запустите приложение и проверьте состояние GIL **после импорта** зависимостей. С Python 3.15 для расширений появился отдельный Stable ABI `abi3t`: wheel с `abi3t` предназначен для free-threaded сборки, а `abi3.abi3t` сообщает о совместимости с обеими сборками при соответствующей компиляции. Например, `cp315-abi3` не подходит CPython 3.15t, а `cp315-abi3.abi3t` обозначает совместимость с обеими сборками. Тег wheel также не доказывает потокобезопасность логики расширения. [Free-threading guide](https://docs.python.org/3.15/howto/free-threading-python.html), [PEP 803](https://peps.python.org/pep-0803/). См. также [повторяемую установку зависимостей](python-packaging-interview.md#q4-как-сделать-установку-зависимостей-повторяемой-в-ci-и-deployment).

Stable ABI — один из вариантов поставки расширения. Wheel с тегами `cp315-cp315t-<platform>` также может подходить free-threaded CPython 3.15 на своей платформе; он не обещает совместимость с другими минорными версиями. Поэтому `abi3t` не следует считать единственным допустимым тегом бинарной зависимости. [Таблица совместимости PEP 803](https://peps.python.org/pep-0803/#compatibility-overview).

Отсутствие GIL увеличивает важность явной синхронизации общего изменяемого состояния: `lock` или один владелец состояния защищает весь бизнес-инвариант, а внутренние блокировки контейнеров этого не заменяют. Тестируйте совместимость расширений и производительность на реальной нагрузке, прежде чем менять архитектуру. [Free-threading guide](https://docs.python.org/3/howto/free-threading-python.html).

---

## See also

- [Основы Python](python-interview.md) — объекты, функции, итераторы и ресурсы.
- [Конкурентность в Go](../go/go-concurrency-interview.md) — другая модель планирования и отмены.
