# Source peer review: OpenAPI Q26/Q41 и REST maturity Q8/Q13

Дата обновления: 2026-11-05. Повторно прочитаны четыре секции теории Q26, Q41, Q8, Q13, их diff относительно HEAD и соответствующие блоки MCQ. Другие вопросы и поля остаются вне области проверки.

## Снимки

- Теория OpenAPI: `cheatsheets/interview/api/openapi-swagger-interview.md`
- Seed OpenAPI: `modules/quiz-app/src/main/resources/seed/mcq/api/openapi-swagger-interview.json`
- SHA-256 OpenAPI MD: `d540abd1d89a673c857ad7f478073a03837b68f46fd25935e26447650dd6ac18`
- SHA-256 OpenAPI seed: `8d6cb0704d99edb63d2cc85fde97b46c2e25e4ee4631553a1e35d11f6861a231`
- Теория REST maturity: `cheatsheets/interview/api/rest-maturity-interview.md`
- Seed REST maturity: `modules/quiz-app/src/main/resources/seed/mcq/api/rest-maturity-interview.json`
- SHA-256 REST MD: `6ce19e999c3348871e8ea57e8cd818a48ee30eadf8ffd0b8cb58d6439435d1f2`
- SHA-256 REST seed: `8a0786bae23abee2440315766409c1c26f3a8f5fdd370dd7e9aac75d49f60677`

В двух seed были изменения вне заданных Q; они не проверялись. Три JSON-примера Q8 и три YAML-примера Q41 прошли синтаксический разбор по результату отдельной проверки владельца. Содержимое и смысл примеров здесь проверены чтением и по источникам; HTTP/runtime и contract tests не выполнялись.

## Матрица четырёх вопросов

| Вопрос | Источниковый результат | MCQ synchronization |
|---|---|---|
| **OpenAPI Q26 — версионирование** | Путь и query меняют URI и тем самым обычный cache key. Новая формулировка рекомендует задавать `Vary` для кэшируемого ответа, зависящего от заголовка версии; это позволяет различать закэшированные варианты. OAS описывает header parameter/paths и не включает маршрутизацию или кэш-настройку. | Q26:B0 согласован в целом; `options.A.sections.edge_cases` упоминает `Vary`. Correct не обещает, что OAS реализует маршрутизацию или настройку кэша. |
| **OpenAPI Q41 — пагинация** | Границы `size`/`limit` 1–100 в YAML — допустимый пример контракта; OAS schema не гарантирует, что сервер валидирует запрос. Keyset pagination с устойчивым полным порядком `(created_at, id)` и подходящим индексом позволяет продолжать после уникального tie-breaker; это не создаёт неизменного снимка между запросами. `Link` по RFC 8288 передаёт ссылки и не предписывает алгоритм выборки. В ответе `"200"` теперь есть обязательное для OAS Response Object поле `description`. | Q41:B0:C согласован по смыслу. `options.C.sections.edge_cases` уже говорит о стабильном порядке и изменениях, но не упоминает tie-breaker или то, что сервер должен сам проверять bounds. Это пропущенная дополнительная деталь теории, не противоречие: опция говорит только о документировании параметров/заголовка. |
| **REST Q8 — HAL, JSON:API, Siren** | Новое вводное описание HAL как `_links`/`_embedded` поверх данных ресурса точнее прежнего «только ссылки». Все три JSON-примера в этой секции выглядят как корректные JSON-примеры без ellipsis placeholders. JSON:API структура с resource `type`, `id`, `attributes`, `relationships` соответствует формату; Siren `actions`/`links` и default GET при пропущенном `method` соответствуют описанию формата. В одном сервисе могут использоваться разные форматы/media types, если клиенты их понимают. HAL опирается на архивированный individual I-D, а не формальный IETF standard. | Q8:B0:C синхронизирован с новой формулировкой HAL; distractor D по-прежнему проверяет Siren default GET. Существенного отставания не нашёл. |
| **REST Q13 — идемпотентность и ключ** | Список теперь включает PUT, DELETE и все safe methods GET/HEAD/OPTIONS/TRACE; это соответствует RFC 9110. POST/PATCH сами по себе не гарантируют идемпотентность, но конкретный endpoint может определить повторяемую семантику. Эффект метода, а не совпадение response status/body, остаётся определением. Новое описание `Idempotency-Key` следует draft-08: ключ — строка Structured Field; его область уникальности и срок действия задаются контрактом ресурса; ресурс сопоставляет повторный запрос с payload/fingerprint; завершённый duplicate получает результат, согласованный с исходным действием; конкурентный запрос и повтор с иным payload обрабатываются по контракту. Draft рекомендует конфликт для concurrent duplicate и 422 для того же ключа с иным payload, не превращая оба кода в универсальную гарантию протокола. HTTP-пример содержит `Host` и `Content-Type`. | Q13:B0:C уже отделяет intended effect от ответа и говорит, что ключ может сделать конкретную операцию идемпотентной. Но `options.C.sections.edge_cases` и D объясняют ключ кратко и не отражают новую дополнительную глубину контракта: область ключа/payload, срок хранения, in-progress duplicates и конфликт при повторе с другим payload. Это необязательная деталь для текущего вопроса о семантике HTTP-методов, не содержательное противоречие; при желании owner может синхронизировать edge case. |

## Findings: закрытые и текущие

### F1 — Неполный список идемпотентных методов — RESOLVED

В предыдущем снимке теория Q13 пропускала TRACE. В текущей редакции поле `Q13` теперь перечисляет PUT, DELETE и safe methods GET, HEAD, OPTIONS, TRACE. RFC 9110 §9.2.2 говорит, что PUT, DELETE и все safe methods идемпотентны; §9.2.1 включает TRACE в safe methods. MCQ Q13:B0 не перечисляет методы исчерпывающе, а приводит GET/PUT/DELETE как корректные примеры; ключ не противоречит обновлённой теории.

### F2 — HAL «только ссылки и вложенные ресурсы» — RESOLVED

В предыдущем снимке вводное описание Q8 могло подразумевать, будто HAL запрещает поля данных. Текущая формулировка `HAL добавляет _links и _embedded к данным ресурса` снимает неоднозначность и согласуется с примером `id`, `name` и ссылок. Соответствующий MCQ Q8:B0 также не содержит утверждения об отсутствии иных resource properties.

### F3 — Нормативная сила формулировки о Vary — RESOLVED

Q26 теперь прямо ограничивает рекомендацию кэшируемыми ответами, выбранными по заголовку версии. Это снимает прежнюю неоднозначность о безусловной обязанности для любого ответа.

Точное поле: OpenAPI theory Q26, итоговый абзац о `Vary`. RFC 9110 §12.5.5 — источник механизма; это не гарантия OpenAPI.

## Источники

Первоисточники открыты напрямую 2026-11-05:

- [RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110.html), §§9.2.1–9.2.2, 9.3.5, 12.5.5 — safe/idempotent methods, отличие intended effect от ответов, DELETE request content, `Vary`.
- [RFC 9111 — HTTP Caching](https://www.rfc-editor.org/rfc/rfc9111.html), §§2–4 — URI/method cache key и `Vary` matching.
- [RFC 8288 — Web Linking](https://www.rfc-editor.org/rfc/rfc8288.html) — `Link` header сериализует links/link relations, но не алгоритм пагинации.
- [OpenAPI Specification 3.1.1](https://spec.openapis.org/oas/v3.1.1.html) — описание интерфейса HTTP API, path/query/header parameters, response headers и schema constraints; описание не включает runtime validation или server routing.
- [JSON Hypertext Application Language, draft-kelly-json-hal-11](https://datatracker.ietf.org/doc/html/draft-kelly-json-hal-11) — `_links`, `_embedded`; Datatracker маркирует как expired individual Internet-Draft вне формального IETF standards process.
- [JSON:API 1.1](https://jsonapi.org/format/) — media type и document/resource/relationship структура.
- [Siren canonical specification](https://github.com/kevinswiber/siren) — entities/actions, `method`, default GET при пропущенном методе и fields.
- [Idempotency-Key, draft-ietf-httpapi-idempotency-key-header-08](https://datatracker.ietf.org/doc/html/draft-ietf-httpapi-idempotency-key-header-08) — опубликован 2026-10-22 как active Internet-Draft, intended status Standards Track, RFC не опубликован. §2 описывает string syntax, уникальность ключа, payload/fingerprint, expiry; §§2.6–2.7 рекомендуют replay-consistent result для завершённого повтора, 409 для concurrent duplicate и 422 при ином payload с уже использованным ключом.

## Не проверялось

- Все остальные вопросы OpenAPI/Swagger и REST maturity, включая отдельный Q14 о safe methods, — **NOT REVIEWED**.
- Рабочая маршрутизация, применение `Vary` в CDN/proxy, серверная проверка `size`/`limit`, курсорный snapshot, транзакционная дедупликация, фактические ответы при concurrent retries — **NOT RUN**.
- Примеры прошли только синтаксический разбор: JSON Q8 и YAML Q41 проверены отдельной проверкой владельца; HTTP request Q13 проверен чтением. Смысл примеров проверен по источникам, API execution не запускался.
- Формальная MCQ-приёмка, surface/form acceptance и final acceptance не проводились.
