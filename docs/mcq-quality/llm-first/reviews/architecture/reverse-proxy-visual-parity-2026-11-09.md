# Reverse Proxy — ревизия визуальной симметрии A–D

Дата: 2026-11-09. Авторский статус: **VISUAL_PARITY_REVIEWED**, Q1:B0–Q30:B0. Это не независимое ревью и не FINAL PASS.

Охват: все 30 видимых вопросов, 120 вариантов A–D, их пояснения и парная теория. В каждом блоке сравнены backticks, курсив, жирный, тире, знаки, число предложений, синтаксический скелет и приблизительная длина. После редакции повторно оценены ключ, ошибочная модель каждого дистрактора и соответствие пояснений. Ключи и Q/B-идентификаторы сохранены.

Исправления: Q1 устраняет уникальное отсутствие выделения и избыточную длину правильного C; аналогичная разница оформления устранена в Q2–Q30 без механического выравнивания текста. В Q4, Q9 и Q29 неверные ответы теперь раскрывают все части видимого вопроса. В Q17 правильный ответ больше не выделяется единственным перечислением через четыре тире. Сопоставлены длина и число предложений; у всех вариантов A–D нет уникального оформления backticks/курсивом/жирным, способного указать на ключ.

При повторной фактической проверке найдены и исправлены дефекты предыдущего авторского прохода: HAProxy имеет кэш в памяти, Envoy предоставляет HTTP cache filter, поэтому утверждение об отсутствии у них кэша было неверным. `worker_connections` считает и клиентские, и upstream-соединения, но не задаёт RPS фиксированной формулой. Исправлены устаревшие пояснения к Q6 и оценка видимости трафика прокси в Q30.

Проверенные первичные источники: [Nginx core module](https://nginx.org/en/docs/ngx_core_module.html), [Nginx limit_req](https://nginx.org/en/docs/http/ngx_http_limit_req_module.html), [HAProxy caching](https://www.haproxy.com/documentation/haproxy-configuration-tutorials/performance/caching/) и [Envoy cache filter](https://www.envoyproxy.io/docs/envoy/v1.38.0/configuration/http/http_filters/cache_filter).

Текущие SHA-256:

- `cheatsheets/interview/architecture/reverse-proxy-interview.md` — `171d8f87ef7033903328585d8b20b903bb7560858ca8a851004fe4bb1ccf7aff`.
- `modules/quiz-app/src/main/resources/seed/mcq/architecture/reverse-proxy-interview.json` — `79f2960dc09026c425d51a8d6f25395591373f68aa63b536c571ff398de636f5`.
- `docs/mcq-quality/fact-freshness/reverse-proxy-interview.json` — `c9a08732c97ea5d8974efbe8e746d00d959286334515611ed45aa79d78105027`.
- `docs/mcq-quality/llm-first-pilot.md` — `8486b80335eb5f118c5d1a55c91e51c3067a71ec47e1e369e2eb7088cbdea590`.

Адресная структурная проверка: `PASS: files=1 questions=30 blocks=30; semantic review NOT_RUN`. `git diff --check` — без ошибок. Открытых критических сомнений по ключам нет. Конфигурационные примеры не запускались на конкретных версиях продуктов; независимая проверка и прежний FINAL PASS не проводились.

## Фиксация проверенного пакета

Содержательный коммит: `155dc9fa8b3cc8264c06392e297801c4c13f46cd` (`docs(mcq): review reverse proxy answer parity`). Его точный manifest:

- `cheatsheets/interview/architecture/reverse-proxy-interview.md`;
- `modules/quiz-app/src/main/resources/seed/mcq/architecture/reverse-proxy-interview.json`;
- `docs/mcq-quality/fact-freshness/reverse-proxy-interview.json`;
- `docs/mcq-quality/llm-first-pilot.md`;
- `docs/mcq-quality/checkpoint.json` — только запись о новой ревизии поверх базового checkpoint; остальные локальные изменения checkpoint не включены.

После содержательного коммита индекс пуст. Отчёты и очередь сохраняются отдельным документационным коммитом; после него индекс проверяется повторно. Обычный push не выполнялся: по локальной ссылке origin/master он опубликовал бы тысячи прежних коммитов; решение владельца репозитория ещё не получено.
