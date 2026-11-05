# Source review теории FastAPI

Дата проверки: 2026-11-05. Проверены полный файл теории, его diff относительно HEAD и парный MCQ seed как read-only материалы.

- Theory: `cheatsheets/interview/frameworks/python/fastapi-interview.md`
- MCQ: `modules/quiz-app/src/main/resources/seed/mcq/frameworks/python/fastapi-interview.json`
- SHA-256 theory: `563bd06e4e995e357cf5b0b0aadee3a1ffa1c6e3875cfd36f2156c21754f84ed`
- SHA-256 MCQ seed: `923eb6d278707fca2187453e66d766263f4dd87a1664f7a0606c1cb6afa60fa0`
- Diff относительно HEAD содержит уточнения Q3–Q7; Q7 дополнительно обновлен после первоначального source review. Синхронизация оценена по текущим целым снимкам с указанными хешами.

## Матрица Q1–Q7

| Q | Проверенные утверждения | Результат по источникам | Синхронизация MCQ |
|---|---|---|---|
| Q1 | `async def` исполняется в event loop; вызванные FastAPI `def` handlers и dependencies — в thread pool; синхронная вспомогательная функция, вызванная напрямую из async handler, сама автоматически туда не переносится. Различены I/O и CPU-bound. | Подтверждено руководством FastAPI об async/sync path operations. | Q1:B0 согласован: ключ B и объяснения отделяют вызовы, сделанные фреймворком, от прямого синхронного вызова. Существенно отстающих полей нет. |
| Q2 | Рекурсивный граф зависимостей/sub-dependencies; повторное использование результата по умолчанию в пределах запроса; `use_cache=False` отключает это кеширование для зависимости; оно не является app-wide singleton. | Подтверждено FastAPI Dependencies и Sub-dependencies. | Q2:B0 согласован. Нет существенного пропуска в MCQ. |
| Q3 | `yield`-cleanup и scope: в 0.121.0 добавлен `scope="function"` (после handler, до отправки ответа); `scope="request"` по умолчанию выполняет cleanup после ответа; диапазон 0.106.0–0.117.x имел ранний cleanup; с 0.118.0 поведение после отправки ответа восстановлено. Для streaming ресурс должен жить достаточно долго; пойманное исключение следует повторно поднять, если его не обрабатывают намеренно. | Точная последовательность версий и scope прямо изложена в актуальном Advanced Dependencies FastAPI. | Q3:B0 согласован. Ключ C и его edge_cases содержат версии, streaming и ограничение транзакции. Не обнаружено отставших полей. |
| Q4 | Request-модель валидирует вход; `response_model` валидирует/сериализует и фильтрует обычный ответ. Некорректный ответ означает серверную ошибку. Прямой `Response` возвращается без Pydantic-преобразования и фильтрации тела. | Подтверждено FastAPI Response Model и Return a Response Directly. | Q4:B0 согласован. Edge cases правильного варианта отражают серверную ошибку и обход обработки прямым Response. |
| Q5 | Входная ошибка оформляется как `RequestValidationError`, стандартный обработчик FastAPI отвечает 422; обработчик можно переопределить. Прикладной 404 задаётся явным `HTTPException`; внутренние ошибки не следует автоматически преобразовывать в 400 или раскрывать клиенту. | Подтверждено FastAPI Handling Errors; документация показывает замену обработчика и другой статус. | Q5:B0 согласован. Корректный B отмечает 422 как значение по умолчанию и возможность override; ложные модели A/C/D опровергаются соответствующими sections. |
| Q6 | Lifespan — async context manager: до `yield` выполняется startup, после — shutdown; при заданном lifespan старые `on_event` startup/shutdown handlers не исполняются. Уместен для общих ресурсов приложения, а не per-request сессии. | Подтверждено FastAPI Lifespan Events. | Q6:B0 согласован. Ключ A и поля `explanation`, `example`, `when_to_apply`, `edge_cases`, `related` совпадают с теорией. |
| Q7 | `TestClient` работает с ASGI-приложением без отдельного внешнего TCP-сервера; контекстный менеджер запускает lifespan. `dependency_overrides` — mapping исходного callable к замене; подмена вызывается вместо оригинала. Словарь следует восстановить для изоляции, разделяемое mutable app не запускать параллельно без изоляции. Для async-тестов FastAPI рекомендует async client с ASGI transport. | FastAPI подтверждает устройство TestClient как Starlette-клиента поверх HTTPX, override semantics и async test path. Прямое открытие страниц Starlette дало 502; выводы о поведении Starlette здесь опираются на ссылки и описания в официальном руководстве FastAPI, а не на отдельно прочитанную страницу Starlette. | Q7:B0 частично отстал после новой редакции теории. Теория уточняет, что HTTPX `ASGITransport` не запускает lifespan и рекомендует `LifespanManager`, а для чтения тела HTTP 500 через `TestClient` нужен `raise_server_exceptions=False` (по умолчанию `True` пробрасывает исключение). Эти два caveat пока отсутствуют во всех тексте и sections MCQ Q7:B0. Поставить владельцу MCQ в очередь: добавить оба ограничения в `options.D.sections.edge_cases`/`example` и при необходимости точечно в дистракторные `what_actually` или `how_it_should_be`, сохранив различие `TestClient` и async `ASGITransport`. Исправленный пример overrides согласован с Q2 и восстанавливает исходный mapping через copy/clear/update; прежние замечания о параллельном использовании app и границах fake также остаются верны. |

## Findings

Блокирующих ошибок источниковой проверки не найдено. Ключевые точки из задания подтверждены первичными источниками:

- execution `def` / `async def` и прямые sync-вызовы — FastAPI async guide;
- кеш и граф dependency — FastAPI sub-dependencies;
- `yield`, cleanup, `StreamingResponse`, scope и версии 0.106/0.118/0.121 — FastAPI advanced dependencies;
- response validation/filtering и прямой `Response` — FastAPI response model / response directly;
- `RequestValidationError`, default 422 и возможность exception handler override — FastAPI handling errors;
- lifespan startup/shutdown и несовместимость композиции со старым `on_event` при заданном lifespan — FastAPI lifespan guide;
- overrides, TestClient, async tests — FastAPI testing guides.

**Отставший MCQ Q/field:** Q7:B0 — в `options.D.sections.edge_cases` (и, по выбору автора, `example`), не указаны: (1) `ASGITransport` не запускает lifespan, при необходимости нужен `LifespanManager`; (2) `TestClient` по умолчанию пробрасывает исключения, а для проверки тела HTTP 500 требуется `raise_server_exceptions=False`. Сейчас этих оговорок нет ни в одном section Q7:B0. Все остальные пункты исходной матрицы остаются синхронизированными. Независимый итог MCQ не заявляется.

## Источники

Официальные источники FastAPI проверены 2026-11-05:

- Async/sync path operations: https://fastapi.tiangolo.com/async/#very-technical-details
- Dependencies: https://fastapi.tiangolo.com/tutorial/dependencies/
- Sub-dependencies/cache: https://fastapi.tiangolo.com/tutorial/dependencies/sub-dependencies/#using-the-same-dependency-multiple-times
- `yield` dependencies: https://fastapi.tiangolo.com/tutorial/dependencies/dependencies-with-yield/
- `yield` scope and version history: https://fastapi.tiangolo.com/advanced/advanced-dependencies/#dependencies-with-yield-and-scope
- Response models: https://fastapi.tiangolo.com/tutorial/response-model/
- Direct Response: https://fastapi.tiangolo.com/advanced/response-directly/
- Error handling: https://fastapi.tiangolo.com/tutorial/handling-errors/
- Lifespan: https://fastapi.tiangolo.com/advanced/events/
- Testing: https://fastapi.tiangolo.com/tutorial/testing/
- Dependency overrides: https://fastapi.tiangolo.com/advanced/testing-dependencies/
- Testing lifespan: https://fastapi.tiangolo.com/advanced/testing-events/
- Async tests: https://fastapi.tiangolo.com/advanced/async-tests/
- TestClient reference: https://fastapi.tiangolo.com/reference/testclient/ — `raise_server_exceptions=True` по умолчанию; при `False` тест может прочитать сгенерированный 500 response.
- HTTPX ASGI transport lifecycle: https://www.python-httpx.org/advanced/transports/#asgi-startup-and-shutdown — HTTPX не запускает lifespan; рекомендует `LifespanManager`. ASGI transport для 500 использует `raise_app_exceptions=False` (отдельно от TestClient API-параметра `raise_server_exceptions`).
- Pydantic serialization reference: https://pydantic.dev/docs/validation/latest/concepts/serialization/

Starlette URLs attempted directly: https://www.starlette.io/threadpool/ and https://www.starlette.io/testclient/; both returned 502 during this review. FastAPI's own docs identify its `TestClient` as Starlette's HTTPX-backed client. No claim dependent on a directly inspected Starlette page is made.

## Runtime and review boundary

FastAPI, Starlette, Pydantic, and HTTPX are not installed in the local environment. No imports, API calls, TestClient/HTTP tests, streaming tests, or runtime validation tests were run, including the newly added Q7 cases for lifespan and 500 responses. All claims are checked against documentation only, not execution evidence. The updated theory and unchanged MCQ seed hashes are shown above. No changes were made to the theory, MCQ JSON, index, HEAD, or plans by this review. This report is not a formal independent MCQ review or final acceptance.
