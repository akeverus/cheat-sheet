# Независимая проверка теории RabbitMQ: Q39, Q40 и Q42

Область проверки ограничена Q39, а также изменёнными секциями Q40 и Q42 в `cheatsheets/interview/messaging/rabbitmq-interview.md`, их diff относительно `HEAD` и блоками Q39/Q40/Q42 paired MCQ seed. Теория перечитана после последней правки; seed просмотрен read-only и не менялся. Остальные 39 вопросов не проверялись. Это проверка теории и источников, не formal MCQ FINAL; RabbitMQ runtime не запускался.

## Результат

Повторная проверка после правки: Q39 теперь корректно указывает default memory threshold 0.6, отделяет resource alarm от transient flow control и не выводит рост backlog только из одной пары rate; исходные теоретические findings закрыты. Q40/Q42 подтверждены по RabbitMQ 4.3 docs, а Java API `queryStreamStats(...).firstOffset()` подтверждён по исходникам тега Stream Java Client v1.12.0, включая пустой stream / `NoOffsetException`. Теория по трём проверенным вопросам **PASS по источникам**. При этом MCQ Q39 в неизменённом seed всё ещё содержит прежнее безусловное утверждение в ключевом варианте C — это отдельный оставшийся MCQ-sync finding. Runtime остаётся **NOT_RUN**.

Текущая неверсированная документация RabbitMQ показывает **4.4** (проверено 2026-11-05). Для проверки требований именно 4.3 была отдельно открыта официальная страница `/docs/4.3/streams`; её версия страницы указана как 4.3 и перечисленные ниже положения совпадают. Java Stream Client `stable` сейчас показывает 1.13.1, поэтому подтверждение версии 1.12.0 сделано по tagged source `v1.12.0`, а не выведено из плавающей ссылки stable.

## Проверенная матрица

| Q | Проверяемое утверждение | Итог теории | Связанный MCQ |
|---:|---|---|---|
| 39 | `vm_memory_high_watermark`, memory alarm vs transient flow control, inference from publish/ack rates | **PASS after revision**: default 0.6 (60%); alarm blocks publishers; transient flow control is distinct; rate pair alone does not prove inevitable growth. | Theory/seed mismatch remains: unchanged key C still says inevitable and monotonic growth. |
| 40 | Classic/quorum queue и Streams имеют разную модель потребления; replay в Stream доступен до retention; Streams — реплицированный append-only log; сравнение с Kafka не сводится к универсальным throughput-числам | PASS по источникам | Ответ D и объяснения согласованы. Указание `RabbitMQ Streams 4.3` в `related` корректно для ветки 4.3, не является частью пользовательского утверждения о текущей версии |
| 42 / block 0 | retention, `x-stream-offset`, default `next`, отсутствие удаления по ack, super stream partition ordering и Stream protocol offset tracking | PASS по источникам | Ответ B согласован; дистракторы отражают проверенные различия |
| 42 / block 1 | возобновление позиции, обнаружение границы retention, API получения `firstOffset` | PASS по источникам | Ответ C согласован; проверка gap и восстановление из снимка/архива обоснованы |

## Findings по Q39 и recheck

1. **Q39-THEORY-MEMORY-ALARM — CLOSED in theory.** Ранее обнаруженная ошибка `40%` / отождествление memory alarm с flow control исправлена. Текущая формулировка подтверждена: RabbitMQ 4.3 default relative watermark `0.6`; resource alarm cluster-wide blocks publishing connections, оставляет consume-only connections рабочими и отличается от transient flow control, которое backpressures producers, когда внутренние компоненты не успевают за ingress.

2. **Q39-THEORY-RATE-INFERENCE — CLOSED in theory; MCQ sync OPEN.** Теория теперь говорит, что превышение publish rate над ack rate само по себе не доказывает неизбежный рост и перечисляет auto-ack, TTL, overflow, reject/dead-letter. Это верно. В seed JSON Q39 не менялся: ключ C всё ещё утверждает «неизбежно и монотонно растёт», а его explanation повторяет безусловный вывод. Это противоречит обновлённой теории; требуется отдельная коррекция MCQ до согласованной приёмки. Само это peer-review не является формальной MCQ FINAL.

3. **Q39-THEORY-LINKS — CLOSED.** Ссылки `/docs/4.3/maxlength` и `/docs/4.3/confirms` не открылись при прямом `open` инструмента, но были разрешены переходом с versioned `/docs/4.3/queues`; обе страницы доступны и показывают RabbitMQ 4.3. Это ограничение инструмента, не дефект ссылок.

4. **Q42-JAVA-API-VERSION — CLOSED.** Новая ссылка закреплена на source tag v1.12.0. В `Environment.java` v1.12.0 присутствует `queryStreamStats(String)` (requires broker 3.11+); `StreamStats.java` содержит `firstOffset()` и объявляет `NoOffsetException`, если first offset отсутствует. В stable API теперь была бы другая версия, но текущий текст больше не использует floating `stable` ссылку.

### Остальные подтверждённые положения

Q39 memory/disk alarms: memory threshold default and non-hard-limit behavior, publishing-only block, cluster-wide alarm, consume-only connections continue. Transient flow-control state can occur without alarm. Rate signals are now qualified by the metric scope, ack mode, and actual ready/unacked change.

Q40/Q42: Streams are persistent replicated logs with non-destructive reads/replay until retention; AMQP 0-9-1 no-offset starts at next written offset; unavailable numeric offset clamps to start/end, and an old offset therefore clamps to the first available boundary; stream-protocol broker-side offset tracking is persisted separately from AMQP ack; super streams preserve order within each partition; `queryStreamStats(...).firstOffset()` is present in v1.12.0 and may raise `NoOffsetException` on an empty stream.

## История версионных ссылок

**Q40/Q42 version-link finding — CLOSED.** В предыдущем срезе Q40/Q42 использовали unversioned RabbitMQ docs, которые на дату проверки показывали 4.4, и Q42 ссылался на плавающую Java API stable (1.13.1). В текущей версии все RabbitMQ ссылки для проверенных тезисов закреплены на `/docs/4.3/...`, а API `firstOffset()` — на source tag `v1.12.0`. Проверенные тезисы сверены с этими закреплёнными источниками; предыдущая рекомендация зафиксировать версии исполнена.

## Проверка первичных источников

- RabbitMQ [Streams and Superstreams, version 4.3](https://www.rabbitmq.com/docs/4.3/streams), открыта 2026-11-05: страница явно указывает Version 4.3. Stream описан как persistent replicated append-only log с non-destructive reads; чтение возможно до истечения retention. `x-stream-offset` без значения начинает с next offset; числовой offset, если он недоступен, clamp-ится к началу или концу лога. Это подтверждает старт со следующей записи и направление к первой доступной записи для offset ниже сохранённого окна.
- Та же [страница Streams версии 4.3](https://www.rabbitmq.com/docs/4.3/streams): consumer acknowledgements в AMQP 0-9-1 продвигают offset/кредиты, но не удаляют log record. Super streams разбивают лог на partition streams и с Single Active Consumer сохраняют порядок в пределах partition.
- RabbitMQ [Stream Plugin, version 4.3](https://www.rabbitmq.com/docs/4.3/stream), открыта 2026-11-05: специализированный Stream protocol предоставляется плагином. Для broker-provided offset tracking offsets хранятся в stream как non-message data; это отдельный механизм от AMQP ack.
- Для проверки актуальной версии также открыта текущая [unversioned Streams documentation](https://www.rabbitmq.com/docs/streams), страница сообщает Version 4.4. Проверенные положения по offset/retention/partition ordering на ней совпадают с версией 4.3.
- В официальном tagged source Java Stream Client [`Environment.java` v1.12.0](https://raw.githubusercontent.com/rabbitmq/rabbitmq-stream-java-client/v1.12.0/src/main/java/com/rabbitmq/stream/Environment.java) присутствует `queryStreamStats(String)`; его Javadoc требует broker RabbitMQ 3.11 или выше. В [`StreamStats.java` v1.12.0](https://raw.githubusercontent.com/rabbitmq/rabbitmq-stream-java-client/v1.12.0/src/main/java/com/rabbitmq/stream/StreamStats.java) присутствует `firstOffset()`, описанный как первый offset; он может бросить `NoOffsetException`, если первого offset пока нет. Источник проверен 2026-11-05.
- Текущие [stable `Environment` API](https://rabbitmq.github.io/rabbitmq-stream-java-client/stable/api/com/rabbitmq/stream/Environment.html) и [stable `StreamStats` API](https://rabbitmq.github.io/rabbitmq-stream-java-client/stable/api/com/rabbitmq/stream/StreamStats.html) отображают 1.13.1. Это объясняет замечание к плавающей ссылке в finding. Release page [`v1.12.0`](https://github.com/rabbitmq/rabbitmq-stream-java-client/releases/tag/v1.12.0) подтверждает тег клиентской версии 1.12.0.
- RabbitMQ [comparison with Kafka](https://www.rabbitmq.com/docs/compare/kafka), открыта 2026-11-05: таблица характеризует classic queue как destructive reads, Stream как replicated append-only log с non-destructive reads; super stream — логическая группировка партиций.
- RabbitMQ 4.3 [Memory Threshold and Limit](https://www.rabbitmq.com/docs/4.3/memory), открыта 2026-11-05: default relative watermark `0.6` (60% available/detected RAM); memory alarm блокирует publishing connections, не является жёстким пределом потребления памяти.
- RabbitMQ 4.3 [Memory and Disk Alarms](https://www.rabbitmq.com/docs/4.3/alarms), открыта 2026-11-05: memory/disk resource alarms блокируют publishing connections, а consume-only connections продолжают работать; при alarm применяется TCP back pressure. В той же документации раздел Transient Flow Control отделён и описывает ситуацию publisher быстрее принимающей стороны.
- RabbitMQ 4.3 [Flow Control](https://www.rabbitmq.com/docs/4.3/flow-control), открыта 2026-11-05: transient flow control возникает, когда publishers посылают быстрее, чем очереди/сервер способны обработать; publisher connection замедляется с переходами блокировки/разблокировки, состояние показывается как `flow`.
- RabbitMQ 4.3 [Queues](https://www.rabbitmq.com/docs/4.3/queues), [Quorum Queues](https://www.rabbitmq.com/docs/4.3/quorum-queues), [Configurable Limits](https://www.rabbitmq.com/docs/4.3/limits), [Dead Letter Exchanges](https://www.rabbitmq.com/docs/4.3/dlx), открыты 2026-11-05: automatic acknowledgement завершает доставку без consumer ack; message TTL, queue length overflow (drop-head/reject-publish), deletion/dead-lettering — другие пути, влияющие на message count/backlog.
- RabbitMQ 4.3 [`Queue Length Limit`](https://www.rabbitmq.com/docs/4.3/maxlength) and [`Consumer Acknowledgements and Publisher Confirms`](https://www.rabbitmq.com/docs/4.3/confirms) resolve through the 4.3 queues navigation; opened via the official page on recheck 2026-11-05. The former confirms ready-count limits and drop/reject behavior; the latter page is the official ack reference. Direct `open` did not resolve these routes in the web tool, so link verification was completed through internal navigation instead.

## Ограничения

- Верификация только документационная/исходниковая; runtime RabbitMQ и Java-код не запускались.
- Источники проверены 2026-11-05; текущая unversioned документация RabbitMQ и stable Java API меняются, тогда как выводы о 4.3/1.12.0 опираются на закреплённые versioned/tagged источники.
- Ревью не охватывает остальные вопросы файла, полное качество MCQ, поверхностные подсказки или formal MCQ FINAL.

## Хэши

- Теория `cheatsheets/interview/messaging/rabbitmq-interview.md` после правок Q39/Q40/Q42: SHA-256 `b19a1f25ca59f3dae5d32a7acc6cd49918b3d7b404f6f5a81e8401437f0f40a9`. Предыдущий hash: `f139566ebe24330c3a910fd6babe27945fba1f140dbec32b0d5805722c5fd010`.
- Paired MCQ seed `modules/quiz-app/src/main/resources/seed/mcq/messaging/rabbitmq-interview.json`: SHA-256 `8780e5fc2a83407efd8cd3b324ce6eb5aa1632d8be17004477daeeb67757fe90`.
