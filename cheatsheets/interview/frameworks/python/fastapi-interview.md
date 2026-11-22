---
title: "Вопросы на собеседовании: FastAPI"
description: "FastAPI для backend API: ASGI, зависимости, ресурсный цикл, схемы, ошибки, lifespan и тесты"
tags:
  - interview
  - frameworks
  - fastapi-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "FastAPI interview"
  - "FastAPI на собеседовании"
prerequisites:
  - "[[python-interview]]"
next: []
updated: "2026-11-05"
---
# Вопросы на собеседовании: FastAPI

Вопросы сверены с официальным руководством FastAPI 2026-11-05. Версионные границы для `yield`-dependencies указаны в Q3. Общую механику `asyncio` и SQL не дублируем; здесь важны решения самого фреймворка.

## Полезные ссылки

- [FastAPI dependencies](https://fastapi.tiangolo.com/tutorial/dependencies/) и [dependencies with yield](https://fastapi.tiangolo.com/tutorial/dependencies/dependencies-with-yield/).
- [Async and sync path operations](https://fastapi.tiangolo.com/async/) — исполнение обработчиков.
- [Lifespan](https://fastapi.tiangolo.com/advanced/events/) и [testing](https://fastapi.tiangolo.com/tutorial/testing/).

## Содержание

- [Q1. Как FastAPI исполняет def и async def handlers?](#q1-как-fastapi-исполняет-def-и-async-def-handlers)
- [Q2. Что делает Depends и как устроен граф зависимостей?](#q2-что-делает-depends-и-как-устроен-граф-зависимостей)
- [Q3. Как закрыть ресурс в зависимости с yield?](#q3-как-закрыть-ресурс-в-зависимости-с-yield)
- [Q4. Что проверяют request и response models?](#q4-что-проверяют-request-и-response-models)
- [Q5. Как разделить HTTPException и ошибку валидации?](#q5-как-разделить-httpexception-и-ошибку-валидации)
- [Q6. Для чего нужен lifespan?](#q6-для-чего-нужен-lifespan)
- [Q7. Как тестировать FastAPI с подменой зависимости?](#q7-как-тестировать-fastapi-с-подменой-зависимости)
- [See also](#see-also)

## Q1. Как FastAPI исполняет def и async def handlers?

FastAPI — ASGI-приложение на Starlette. `async def` path operation исполняется в event loop; его ожидающие операции должны использовать async API. Обычный `def` handler (и обычная `def` dependency) вызывается фреймворком во внешнем thread pool, чтобы не блокировать loop. Но **синхронную вспомогательную функцию, которую вы сами вызываете из `async def`, FastAPI автоматически в thread pool не переносит**.

Выбирайте `async def` для async HTTP/DB клиента, `def` для синхронного драйвера. Не называйте CPU-bound задачу «асинхронной» лишь из-за синтаксиса. Пул потоков и количество workers всё равно ограничивают пропускную способность. [FastAPI async](https://fastapi.tiangolo.com/async/#very-technical-details).


**Сценарий перегрузки.** Синхронная зависимость вызывает внешний сервис, который стал отвечать медленнее. Пока вызовы ждут ответа, они занимают токены стандартного лимитера AnyIO. Если токены закончились, другие операции, использующие тот же лимитер, ждут свободного места. Например, `async def` маршрут с синхронной зависимостью тоже может замедлиться, хотя его собственный код исполняется в event loop.

В стандартной конфигурации AnyIO доступно 40 токенов для таких вызовов. Это настраиваемый лимит одновременно выполняемой работы через `anyio.to_thread.run_sync`, а не лимит всех HTTP-запросов. При диагностике сопоставьте занятость лимитера с задержкой и таймаутами внешнего сервиса. Увеличение лимита может увеличить нагрузку на зависимость. [Starlette Thread Pool](https://starlette.dev/threadpool/) · [AnyIO Thread Limits](https://anyio.readthedocs.io/en/stable/threads.html#adjusting-the-default-maximum-worker-thread-count).

## Q2. Что делает Depends и как устроен граф зависимостей?

`Depends` объявляет входной ресурс/проверку в сигнатуре path operation. FastAPI разрешает зависимости рекурсивно, включая sub-dependencies, и может использовать их для авторизации, сессии БД, настроек и общих параметров. Типизацию удобно выражать через `Annotated`:

```python
from typing import Annotated
from fastapi import Depends, FastAPI

app = FastAPI()

def current_tenant() -> str:
    return "example"

@app.get("/orders")
def orders(tenant: Annotated[str, Depends(current_tenant)]):
    return {"tenant": tenant}
```

Результат dependency по умолчанию кешируется в рамках одного запроса, поэтому несколько потребителей одного узла не обязаны создавать ресурс заново. `use_cache=False` меняет это поведение для конкретной зависимости. Не путайте request-scoped dependency с глобальным singleton приложения. [Dependencies](https://fastapi.tiangolo.com/tutorial/dependencies/), [cache](https://fastapi.tiangolo.com/tutorial/dependencies/sub-dependencies/#using-the-same-dependency-multiple-times).


**Общая зависимость для двух проверок.** Допустим, обе проверки доступа объявляют `Depends(load_account)` с одинаковыми параметрами зависимости. В одном запросе FastAPI может передать им один результат загрузки аккаунта. Если второй проверке нужен новый вызов `load_account`, укажите `Depends(load_account, use_cache=False)` именно у неё. Этот вызов обойдёт чтение из кеша. Если у `load_account` есть своя зависимость, например `Depends(get_session)`, её кеширование по-прежнему определяется её собственным объявлением. Повторный вызов функции также не гарантирует чтения более свежих данных: видимость изменений зависит, например, от снимка транзакции. [FastAPI Depends](https://fastapi.tiangolo.com/reference/dependencies/#fastapi.Depends).

## Q3. Как закрыть ресурс в зависимости с yield?

Для ресурса запроса (например, временного каталога или сессии) зависимость может `yield` объект, а после него выполнить cleanup. FastAPI оформляет это как контекстный менеджер и закрывает ресурс после использования dependency; для ошибок внутри path operation можно обработать исключение вокруг `yield`, но не скрывать его случайно.

```python
from collections.abc import Iterator
from tempfile import TemporaryDirectory
from typing import Annotated
from fastapi import Depends

def get_workdir() -> Iterator[str]:
    with TemporaryDirectory() as path:
        yield path

WorkdirDep = Annotated[str, Depends(get_workdir)]
```

С FastAPI **0.121.0** `Depends(scope="function")` завершает `yield`-dependency после handler, но **до** отправки ответа. Scope `"request"` по умолчанию завершает её **после** отправки ответа. Для `StreamingResponse`, который читает ресурс во время передачи тела, нужен ресурс, живущий до конца передачи; ранний `function` cleanup закроет его слишком рано. В FastAPI **0.106.0–0.117.x** обычный cleanup выполнялся перед отправкой ответа, поэтому поведение старого приложения проверяйте по установленной версии. Если зависимость перехватывает исключение из handler, его обычно нужно повторно поднять. Транзакцию завершайте явно по бизнес-исходу; закрытие сессии не означает commit. [Dependencies with yield](https://fastapi.tiangolo.com/tutorial/dependencies/dependencies-with-yield/), [история `yield` и scope](https://fastapi.tiangolo.com/advanced/advanced-dependencies/#dependencies-with-yield-and-scope).

**Порядок cleanup в графе.** Пусть `get_cursor` получает соединение из `get_connection`. Обе зависимости используют `yield`. При завершении сначала закрывается курсор, затем соединение, чтобы cleanup курсора мог использовать открытое соединение. Если курсор нужен только обработчику, у него может быть `scope="function"`, даже если соединение живёт до конца запроса. Если курсор используется при отправке потокового ответа, обеим зависимостям нужен `scope="request"`. FastAPI отклоняет обратную комбинацию, в которой зависимость с `scope="request"` опирается на подзависимость с `scope="function"`. [Scope подзависимостей](https://fastapi.tiangolo.com/tutorial/dependencies/dependencies-with-yield/#scope-for-sub-dependencies).
**Граница транзакции при записи.** Пусть `POST /orders` возвращает 201, а `commit()` спрятан после `yield` в зависимости со стандартным scope `"request"`. Если БД отклонит коммит, клиент уже получит 201. Для этого маршрута завершите транзакцию в обработчике или сервисе до возврата ответа. Например, контекст `session.begin()` в SQLAlchemy 2.0 фиксирует транзакцию при успешном выходе из блока и откатывает её при исключении. В зависимости оставьте закрытие сессии. Если ответ потоковый, отдельно проверьте, какие ресурсы ещё нужны генератору тела. [Момент завершения зависимости FastAPI](https://fastapi.tiangolo.com/advanced/advanced-dependencies/#dependencies-with-yield-and-scope), [SQLAlchemy 2.0 — транзакции сессии](https://docs.sqlalchemy.org/en/20/orm/session_transaction.html).


**Ресурс для проверки и ресурс для передачи.** Маршрут выгружает готовый файл, а сессия БД нужна только для проверки прав. При медленной загрузке стандартный scope `"request"` удержит сессию до завершения ответа. Если эта зависимость никому не нужна при передаче тела и её граф допускает раннее завершение, задайте ей `scope="function"`. Передайте в генератор уже проверенные данные, например путь к файлу, а файл открывайте на время чтения тела. Если генератор действительно читает строки из БД, соединение должно оставаться доступным до окончания чтения. Тогда учитывайте, что медленный получатель может надолго занять соединение. [FastAPI: раннее завершение зависимости при потоковом ответе](https://fastapi.tiangolo.com/advanced/advanced-dependencies/#use-cases-with-early-exit-code).

## Q4. Что проверяют request и response models?

FastAPI использует аннотации и Pydantic-модели для разбора и валидации входа, генерации OpenAPI и сериализации выхода. `response_model` задаёт публичную форму ответа и фильтрует поля, не включённые в неё; это полезно, чтобы не вернуть внутренний секрет вместе с ORM-объектом.

```python
from pydantic import BaseModel

class UserOut(BaseModel):
    id: int
    name: str

@app.get("/users/{user_id}", response_model=UserOut)
def user(user_id: int):
    return {"id": user_id, "name": "Ada", "password_hash": "internal"}
```

Входную и выходную модели разделяйте по контракту. Если результат не соответствует `response_model`, FastAPI считает это ошибкой приложения и возвращает серверную ошибку. При прямом возврате `Response` фреймворк передаёт его без Pydantic-преобразования и фильтрации тела по `response_model`; ответственность за публичные поля остаётся у кода handler. Проверяйте фактический HTTP-ответ тестом. [Request body](https://fastapi.tiangolo.com/tutorial/body/), [response model](https://fastapi.tiangolo.com/tutorial/response-model/), [direct `Response`](https://fastapi.tiangolo.com/advanced/response-directly/).

**Проверка при смене формата ответа.** Если добавить в декоратор `response_class=JSONResponse` и оставить возврат словаря из примера, FastAPI по-прежнему применит `UserOut`. Если же вернуть готовый `JSONResponse(content=record)`, где `record` — тот же словарь, поле `password_hash` попадёт в тело ответа. Декларация модели при этом может оставаться в OpenAPI. Если нужен готовый объект ответа, сначала соберите публичные данные, например через `UserOut.model_validate(record).model_dump(mode="json")` в Pydantic 2. В тесте проверяйте набор ключей фактического JSON. [Custom Response](https://fastapi.tiangolo.com/advanced/custom-response/).

**Согласованность с OpenAPI.** Если маршрут объявляет модель с обязательным полем `email` и задаёт `response_model_exclude={"email"}`, поле исчезнет из фактического JSON, но останется в схеме ответа. Клиент, сгенерированный по OpenAPI, может ожидать обязательное поле, которого сервер не отправляет. Для публичного контракта задайте отдельную модель без `email`, как `UserOut` в примере. При изменении контракта проверьте и JSON ответа, и схему операции в `/openapi.json`. [FastAPI: response model include and exclude](https://fastapi.tiangolo.com/tutorial/response-model/#response-model-include-and-response-model-exclude).

## Q5. Как разделить HTTPException и ошибку валидации?

Неверные данные запроса FastAPI оформляет как `RequestValidationError`; стандартный обработчик возвращает HTTP **422** и описание ошибок по полям. Этот обработчик можно заменить, поэтому статус 422 — поведение по умолчанию, а не неизменяемый контракт. `HTTPException` используют для ожидаемого прикладного исхода (например, 404 при отсутствии ресурса) и задают статус/детали явно. Не превращайте любую ошибку БД в `HTTPException(400)`: внутренний сбой нужно зарегистрировать, а если ответ ещё не начат, клиенту можно вернуть безопасный 5xx.

```python
from fastapi import HTTPException

if record is None:
    raise HTTPException(status_code=404, detail="Order not found")
```

Для собственной формы ошибок регистрируют exception handler. Не отправляйте клиенту строковое представление `RequestValidationError` или внутреннего исключения без контроля: в нём могут быть детали реализации. Ошибка валидации **ответа** из Q4 означает сбой кода сервиса, её не следует выдавать за ошибочный запрос клиента. [Handling errors](https://fastapi.tiangolo.com/tutorial/handling-errors/), [response model](https://fastapi.tiangolo.com/tutorial/response-model/).


**Ошибка после отправки ответа.** Маршрут вернул `202 Accepted`, а зарегистрированная через `BackgroundTasks` задача завершилась исключением. Клиент уже получил 202. Даже если обработчик ошибки сформирует 500, этот ответ не будет отправлен. Сбой фоновой задачи нужно фиксировать отдельно. Если клиенту важно знать результат работы, 202 может содержать идентификатор операции, по которому клиент позже проверит её статус. [Starlette Exceptions](https://starlette.dev/exceptions/) · [FastAPI Background Tasks](https://fastapi.tiangolo.com/tutorial/background-tasks/).

**Если принятую задачу нельзя потерять.** Starlette запускает `BackgroundTasks` в процессе приложения после отправки ответа. Если процесс аварийно завершится сразу после 202, задача может так и не начаться. Например, для обязательного экспорта запишите задание со статусом `pending` в БД и подтвердите транзакцию до ответа с ID. Отдельный worker забирает задания из БД, повторяет их при сбое по заданной политике и сохраняет итоговый статус. Обработка должна учитывать повторный запуск, например записывать результат по стабильному ID задания. Если факт приёма и сообщение для брокера нужно сохранить вместе, запишите событие в outbox в той же транзакции. [Starlette Background Tasks](https://starlette.dev/background/) · [Transactional outbox](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html).

## Q6. Для чего нужен lifespan?

`FastAPI(lifespan=...)` принимает async context manager: код до `yield` исполняется перед обслуживанием запросов, после `yield` — при остановке. Это место для создания и закрытия разделяемых клиентских пулов и других ресурсов приложения, а не для сессии БД каждого запроса.

```python
from contextlib import asynccontextmanager
from fastapi import FastAPI
import httpx

@asynccontextmanager
async def lifespan(app: FastAPI):
    async with httpx.AsyncClient() as client:
        app.state.client = client
        yield

app = FastAPI(lifespan=lifespan)
```

Если указан `lifespan`, старые `@app.on_event("startup"/"shutdown")` handlers не вызываются; выбирайте один механизм и проверяйте запуск/остановку в тесте. Для async-клиента cleanup тоже должен быть асинхронным; `AsyncClient` закрывается при выходе из `async with`. [Lifespan](https://fastapi.tiangolo.com/advanced/events/).

**Граница ресурса в production.** При запуске Uvicorn с несколькими workers и включённым lifespan каждый worker выполняет этот код отдельно и получает свой клиент в `app.state`. Например, при двух репликах по четыре workers и одном клиенте с `httpx.Limits(max_connections=20)` на worker восемь пулов суммарно допускают до 160 соединений. Это верхняя граница для этих пулов, а фактическое число открытых соединений зависит от нагрузки. Если в startup запустить действие, которое должно произойти один раз на весь деплой, например миграцию схемы, каждый worker попытается его выполнить. Такую операцию можно вынести в отдельный шаг деплоя. [Lifespan в Uvicorn](https://uvicorn.dev/concepts/lifespan/), [лимиты пула HTTPX](https://www.python-httpx.org/advanced/resource-limits/).

**Подключённое приложение.** Если основное приложение монтирует отдельный экземпляр FastAPI через `app.mount("/reports", reports_app)`, ASGI-сервер запускает lifespan основного приложения. Lifespan `reports_app` автоматически не вызывается. Поэтому маршрут отчётов, который читает `reports_app.state.client`, не получит клиент, если тот создаётся только в дочернем lifespan. Основное приложение может открыть клиент в своём lifespan и явно передать его приложению отчётов до начала обработки запросов. Владелец клиента закроет его при выходе из контекстного менеджера. [FastAPI — lifespan подключённых приложений](https://fastapi.tiangolo.com/advanced/events/#sub-applications).

## Q7. Как тестировать FastAPI с подменой зависимости?

`TestClient` позволяет тестировать HTTP-контракт без отдельного сетевого сервера. `app.dependency_overrides` — словарь, который заменяет конкретную dependency в графе, например сервис или DB session. После теста восстановите исходные overrides, иначе следующий тест унаследует подмену или потеряет свою настройку. Пример ниже использует `app` и `current_tenant` из Q2.

```python
from fastapi.testclient import TestClient

saved_overrides = app.dependency_overrides.copy()
try:
    app.dependency_overrides[current_tenant] = lambda: "test"
    with TestClient(app) as client:
        response = client.get("/orders")
        assert response.status_code == 200
finally:
    app.dependency_overrides.clear()
    app.dependency_overrides.update(saved_overrides)
```

Контекстный менеджер `TestClient` запускает lifespan. Разделяемый объект `app` с изменяемым словарём overrides не используйте одновременно в параллельных тестах без изоляции. Отдельно проверьте ошибки и response model. Для реальной БД понадобится интеграционный тест, поскольку dependency override с fake не доказывает SQL и транзакции. Для самого async-теста используйте async-клиент с ASGI transport, а не синхронный `TestClient` внутри coroutine. HTTPX `ASGITransport` сам не запускает lifespan: если тесту нужны startup/shutdown-ресурсы, управляйте их циклом отдельно, например через `LifespanManager` из `asgi-lifespan`. [Testing](https://fastapi.tiangolo.com/tutorial/testing/), [dependency overrides](https://fastapi.tiangolo.com/advanced/testing-dependencies/), [testing lifespan](https://fastapi.tiangolo.com/advanced/testing-events/), [async tests](https://fastapi.tiangolo.com/advanced/async-tests/).

Чтобы проверить тело сгенерированного ответа 500, создайте `TestClient(app, raise_server_exceptions=False)`. По умолчанию клиент пробрасывает исключение приложения в тест, что удобно для поиска причины, но отличается от чтения HTTP-ответа. [TestClient API](https://fastapi.tiangolo.com/reference/testclient/), [HTTPX: ASGI startup/shutdown](https://www.python-httpx.org/advanced/transports/#asgi-startup-and-shutdown).

**Async-тест ресурса из lifespan.** Если lifespan возвращает через `yield` словарь с клиентом, обработчик может получить этот клиент из `request.state`. Для тестового запроса передайте в транспорт `manager.app`: эта обёртка передаёт состояние lifespan в ASGI scope запроса. При передаче исходного `app` startup уже мог выполниться, но ключ из возвращённого словаря будет недоступен через `request.state`. Пример Q6 хранит клиент в `app.state`, это отдельное хранилище. [Состояние lifespan в Starlette](https://www.starlette.dev/lifespan/#lifespan-state), [asgi-lifespan](https://github.com/florimondmanca/asgi-lifespan#accessing-state).

```python
from asgi_lifespan import LifespanManager
from httpx import ASGITransport, AsyncClient

# Приложение с маршрутом /orders и зависимостями, настроенными для теста.
async def check_orders(app):
    async with LifespanManager(app) as manager:
        transport = ASGITransport(app=manager.app)
        async with AsyncClient(transport=transport, base_url="http://test") as client:
            response = await client.get("/orders")
            assert response.status_code == 200
```

---

## See also

- [Основы Python](../../programming-languages/python/python-interview.md) — типы и data model.
- [Конкурентность в Python](../../programming-languages/python/python-concurrency-interview.md) — event loop и отмена.
- [Тестирование в Python](../../programming-languages/python/python-testing-interview.md) — fixtures и mock.
