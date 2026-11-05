# Source review: контракты платформы метрик, ID и object storage

Дата: 2026-11-05. Авторская проверка теории с известными ключами MCQ,
не независимая слепая приёмка и не `FINAL`. Проверены только шесть
изменённых секций из 12 вопросов трёх тем; остальные шесть вопросов
**NOT REVIEWED**. В парных JSON полностью прочитаны девять блоков:
metrics Q1:B0/Q2:B0/B1, unique ID Q2:B0/B1/Q4:B0,
object storage Q1:B0/Q3:B0/B1 — вопрос, все A/B/C/D и sections.
Остальные шесть блоков **NOT REVIEWED**; их приёмка не заявляется.

## Снимок

- `cheatsheets/interview/system-design/design-metrics-platform-interview.md`: Q1/Q2
  - MD SHA-256: `3f44fdee7ebb7b2a590dffaabb4c6b697a3b0c0fe255fabb6cea810b6d08af3e`
  - seed SHA-256: `54b85dae7dd575a63f7e7e2a81f56942579415d70610b8015ddfa194ace198d2`
- `cheatsheets/interview/system-design/design-unique-id-generator-interview.md`: Q2/Q4
  - MD SHA-256: `1a9338a5ab7bbf46e768aac4ee511d9775ad897a711f87a61b0635d4dd153eb9`
  - seed SHA-256: `044b0964f937a21f24acb3f037a1160b4af125fc5bd95e644c7cccab3d94dc1c`
- `cheatsheets/interview/system-design/design-object-storage-interview.md`: Q1/Q3
  - MD SHA-256: `69563d7e6423723df4fe2f6bf6d08207d85bbb50f8add0db60f5d33802644d7b`
  - seed SHA-256: `52fd1c3543909fce53f7f608ff23560de26689d11d169d31455299d8b2f11ef0`

Парные JSON находятся в
`modules/quiz-app/src/main/resources/seed/mcq/system-design/` с теми
же именами. Это ссылки для сверки, JSON не редактировался.

## Платформа метрик: Q1/Q2

Q1 сохраняет границу доверенного proxy, который проверяет отправителя
и устанавливает tenant header на чтении и записи. Samples rate/burst
и предел series защищают разные ресурсы. Уточнено: Mimir документирует
предел in-memory series; active series и churn наблюдаются отдельно.
Owned series tracking меняет счётчик при соответствующей настройке,
а не является синонимом active series. Универсальный встроенный
per-tenant лимит скорости создания series не заявляется.

Q2 по источнику Thanos подтверждён: downsampling ускоряет длинные
запросы и может увеличить место; ранний retention лишает нужного
разрешения. Один активный Compactor обрабатывает один stream блоков;
разделение непересекающихся streams допускает масштабирование.
Живой halted-процесс не означает работающий retention.
Remote write WAL/queues не обещают бесконечного сохранения при сбое.

Связь с MCQ: три блока согласованы по этим принципам. В Q1:C.text
сказано «активные series», а example уже точнее называет in-memory.
При желании владельцу стоит унифицировать термин под механизм Mimir;
это **INFO**, ключ не меняется. B путает samples rate с полной ценой
cardinality; Q2:B1 отличает один активный stream от двух конкурирующих
compactor. Поверхностные признаки и blind acceptance не проверялись.

Источники открыты напрямую:

- [Mimir authentication/authorization](https://grafana.com/docs/mimir/latest/manage/secure/authentication-and-authorization/),
  [distributor](https://grafana.com/docs/mimir/latest/references/architecture/components/distributor/),
  [runtime overrides](https://grafana.com/docs/mimir/latest/configure/about-runtime-configuration/).
- [Mimir configuration parameters](https://grafana.com/docs/mimir/latest/configure/configuration-parameters/)
  (`max_global_series_per_user`),
  [owned series tracking](https://grafana.com/docs/mimir/latest/configure/configure-owned-series/).
  Последний механизм помечен экспериментальным; доступность и флаги
  нужно сверять с выбранной версией/архитектурой.
- [Prometheus remote write tuning](https://prometheus.io/docs/practices/remote_write/).
- [Thanos Compactor, путь документации v41.0](https://thanos.io/v41.0/components/compact.md/):
  downsampling, streams, retention, halting. Ссылка проверена, не заменена
  произвольно на другой похожий номер версии.

## Генерация ID: Q2/Q4

Q2 подтверждён по PostgreSQL 18 и историческому примеру Flickr.
Atomic nextval не означает gapless/commit order; cache, rollback и
crash различаются. Внешняя постоянная публикация требует commit
вызова; UNLOGGED sequence сбрасывается после аварии и не реплицируется.
Добавлены границы NO CYCLE и запрета сброса счётчика назад для внешнего
пространства ID; исчерпание требует обработки. Защита владения ranges
остаётся отдельным контрактом failover.

Q4 сверяет v4/v7 и Snowflake без обещания глобального commit order.
Уточнены старшие 48 бит UUIDv7 и опциональные fraction/counter поля;
PostgreSQL 18 действительно добавил uuidv7 и alias uuidv4.
Исторический IdWorker использует Long, worker/datacenter bits и
отказывает при движении часов назад; это пример реализации, а не
универсальная политика всех Snowflake-подобных генераторов.

Три парных блока содержательно согласованы в проверенном scope.
Уточнения CYCLE/сброса и fraction дополняют теорию; явного противоречия
ключам C/C/D не найдено. Это не слепая независимая приёмка.

Источники:

- [PostgreSQL 18 sequence functions](https://www.postgresql.org/docs/18/functions-sequence.html),
  [CREATE SEQUENCE](https://www.postgresql.org/docs/18/sql-createsequence.html),
  [UUID functions](https://www.postgresql.org/docs/18/functions-uuid.html),
  [release 18](https://www.postgresql.org/docs/18/release-18.html).
- [Flickr Ticket Servers, 2010](https://code.flickr.net/2010/02/08/ticket-servers-distributed-unique-primary-keys-on-the-cheap/).
- [RFC 9562, UUIDv4/v7](https://datatracker.ietf.org/doc/html/rfc9562#section-5.7).
- [Архивный Snowflake IdWorker, ветка snowflake-2010](https://github.com/twitter-archive/snowflake/blob/snowflake-2010/src/main/scala/com/twitter/service/snowflake/IdWorker.scala).

## Object storage: Q1/Q3

Q1 ограничивает атомарность одним ключом Amazon S3. Проверены conditional
writes: CompleteMultipartUpload после 409 требует новой multipart
и повторной загрузки частей;
404 при If-Match означает отсутствие текущего объекта. Эти случаи
разделены в тексте. If-None-Match проверяет current version: текущий
delete marker допускает новую запись при наличии старых версий.

Q3 подтверждён: multipart завершается проверенным списком частей,
алгоритм/type checksum выбирают при инициализации, ETag не служит
универсальным MD5. Для части алгоритмов checksum, впервые посланная
только на Complete, не проверяется/не сохраняется. Прямой REST-клиент
должен разбирать XML даже при статусе 200; потерянный ответ оставляет
исход неопределённым. SDK handling проверено по документации, не в исполнении.

Три парных блока согласованы по основной модели; detail 409/404 и
current delete marker в MCQ не обязателен для их текущих вопросов.
Существенного рассинхрона не найдено. Качество формы не принималось.

Источники:

- [S3 consistency](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html#ConsistencyModel),
  [conditional writes](https://docs.aws.amazon.com/AmazonS3/latest/userguide/conditional-writes.html),
  [versioning](https://docs.aws.amazon.com/AmazonS3/latest/userguide/versioning-workflows.html).
- [Multipart overview](https://docs.aws.amazon.com/AmazonS3/latest/userguide/mpuoverview.html),
  [integrity/checksums](https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity-upload.html),
  [CompleteMultipartUpload API](https://docs.aws.amazon.com/AmazonS3/latest/API/API_CompleteMultipartUpload.html).

## Проверки и ограничения

verify-mcq-json: **PASS**, 3 файла / 12 вопросов / 15 блоков. Это
структурный прогон, из него не следует принятие остальных вопросов.
Проверены номера Q, scope новых/прежних изменений, сохранность IDs,
локальные файловые ссылки/anchors, fences и отсутствие inline MCQ.
Frontmatter трёх файлов разобран Ruby Psych; адресный diff --check PASS.

Mimir, Prometheus, Thanos, PostgreSQL, Snowflake и AWS S3/SDK runtime,
failover, загрузка/удаление реальных объектов и нагрузка **NOT RUN**.
Сервисы не запускались; настройки и credentials не использовались.
Docker отказ не обходился. Sources latest у Mimir/Prometheus/S3
подвижны, проверка относится к открытому тексту на 2026-11-05;
PostgreSQL закреплён на 18, Snowflake — на исторической ветке,
Thanos — на указанном пути документации.

Три MD и этот отчёт проверены после передачи индекса перед адресным
коммитом. Список четырёх файлов и SHA:
`/tmp/prepared-system-design-contracts-theory-20261105.json`.
Общие планы, registries/checkpoints, JSON и чужие файлы не изменялись.
