# План покрытия interview-шпаргалок

Состояние репозитория сверено **2026-10-07**. Этот документ ведёт очередь тем и
фактический прогресс авторинга. При написании вопросов синхронизировать
шпаргалки и парные MCQ. [План MCQ](PLAN_INTERVIEW.md) ведёт отдельную независимую
приёмку; автоматический PASS не даёт статус `FINAL`.

## Основание и предел проверки

- Текущий корпус: **337** тематических Markdown-файлов, **9 923** заголовка
  Q в **25** разделах. [Инвентарь](docs/interview-coverage/inventory.tsv)
  обновлён после восемнадцати новых шпаргалок и добавленных Q. **336** файлов имеют парный production
  JSON; отдельная тема подготовки остаётся без пары.
- `З` — пути и заголовки; `О` — точечная проверка ответа; `П` — полная
  содержательная проверка всех ответов существующего файла. **П в этом
  пакете не проводилась.** Все Q новых файлов написаны и авторски сверены с
  официальными источниками, но независимая редакторская проверка не заявлена.
- В [чате «Улучшение MCQ»](https://chatgpt.com/s/cx_6abe50333f00819191e0e08d7c49cddc)
  согласован принцип переносимого процесса: видимый вопрос и теория проверяются
  отдельно от JSON, статусы выводятся из проверяемой работы, выборка не заменяет
  полный охват, а другая LLM должна продолжать без истории диалога. Здесь этот
  принцип применён к поиску пробелов **по файлам и заголовкам**.
- Для ориентира по подаче использована [подборка Baeldung](https://www.baeldung.com/java-interview-questions).
  Baeldung — один из источников идей наряду с датированными публикациями самих
  проектов: [Go Blog](https://go.dev/blog/), [Spring Blog](https://spring.io/blog/),
  [Python Insider](https://blog.python.org/) и release notes. Новые фактические
  ответы сверены с [Python](https://docs.python.org/3/),
  [Go](https://go.dev/doc/), [Java](https://docs.oracle.com/en/java/docs/api/)
  и другими первичными источниками. Внешняя подборка — источник кандидатов,
  не автоматическое основание добавлять каждый пункт.
- Профиль расширен пользователем за пределы backend: искать полезные interview-вопросы
  также в AI/ML, тестировании, архитектуре, безопасности и инфраструктуре.
  Прежнее явное исключение Rust, JavaScript, Node.js, TypeScript и фронтенда
  сохраняется до уточнения его отмены; вопрос задан пользователю.

## Выполнено в текущем этапе

| Приоритет / исход | Изменённый Q и ближайший сосед | Конкретный пробел и первичный источник | Проверка MCQ |
|---|---|---|---|
| P0 · исправлено · О | `go-generics-interview.md` Q10/Q15/Q18–Q20 | Старый запрет generic methods и слишком узкий вывод типов; [Go 1.27](https://go.dev/doc/go1.27). | JSON изменён, структура PASS; parity всего файла блокируют старые Q. Независимая приёмка ожидается. |
| P0 · исправлено · О | `spring-security-interview.md` Q30 | Утверждение об отсутствии встроенного MFA устарело для [Spring Security 7](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/mfa.html). | JSON Q30, структура/parity PASS; независимая приёмка ожидается. |
| P0 · исправлено · О | `owasp-top10-interview.md` Q34; `application-security-interview.md` Q13 | Отдельная DNS-проверка допускает повторное разрешение HTTP-клиентом; [Spring Boot HTTP client](https://docs.spring.io/spring-boot/reference/io/rest-client.html), [OWASP SSRF](https://cheatsheetseries.owasp.org/cheatsheets/Server_Side_Request_Forgery_Prevention_Cheat_Sheet.html). | Оба JSON Q синхронизированы, структура/parity PASS; независимая приёмка ожидается. |
| P1 · новый файл | `python-interview.md` Q1–Q9; ранее только вне-interview обзор | Объекты, коллекции, функции, области видимости, итерация, ресурсы, dataclass; [Python data model](https://docs.python.org/3/reference/datamodel.html). | 9/9 JSON, структура/parity PASS; независимая приёмка ожидается. |
| P1 · новый файл | `python-concurrency-interview.md` Q1–Q9; рядом Python core | asyncio/threads/processes, TaskGroup/отмена, backpressure, free-threaded CPython; [asyncio](https://docs.python.org/3/library/asyncio-task.html), [free-threading](https://docs.python.org/3/howto/free-threading-python.html). | 9/9 JSON, структура/parity PASS; независимая приёмка ожидается. |
| P1 · углублено · О | `go-stdlib-interview.md` Q22–Q25; рядом Q1–Q5 HTTP | `database/sql` уже имел маршрут; добавлены context, `Rows.Err`, `DB.Stats()`, границы `LastInsertId` и транзакций по [Go database docs](https://go.dev/doc/database/). Новый файл отклонён. | JSON Q22–Q25, структура PASS; parity всего файла блокируют старые Q. Независимая приёмка ожидается. |
| P2 · новый файл | `python-testing-interview.md` Q1–Q7; рядом `property-based-testing-interview.md` Q9 | Fixtures, параметризация, mock, async tests; [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html), [unittest.mock](https://docs.python.org/3/library/unittest.mock.html). | 7/7 JSON, структура/parity PASS; независимая приёмка ожидается. |
| P2 · новый файл | `fastapi-interview.md` Q1–Q7; рядом Python concurrency | Depends, yield cleanup, схемы, lifespan, тесты по [FastAPI docs](https://fastapi.tiangolo.com/tutorial/dependencies/). | 7/7 JSON, структура/parity PASS; независимая приёмка ожидается. |
| P2 · новый файл | `mysql-interview.md` Q1–Q6; рядом `sql-interview.md` Q16/Q45 и `database-transactions-interview.md` Q9 | InnoDB, clustered index, план, MVCC, charset, разрыв связи по [MySQL 8.4](https://dev.mysql.com/doc/refman/8.4/en/) и [Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html). | 6/6 JSON, структура/parity PASS; независимая приёмка ожидается. |
| P2 · новый файл | `jdbc-connection-pool-interview.md` Q1–Q6; рядом `sql-interview.md` Q45, `database-transactions-interview.md` Q33–Q34, `spring-data-jdbc-interview.md` Q1/Q16 | `DataSource`, ресурсы, autocommit, batch, fetch и диагностика пула по [Java SE 25 JDBC API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/javax/sql/DataSource.html) и [HikariCP](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby). | 6/6 JSON, структура/parity PASS; независимая приёмка ожидается. |
| P0 · исправлено · О | `ai-ml/mcp-interview.md` Q6/Q8/Q13/Q14/Q28–Q30 | Ответы смешивали ревизии MCP: обязательный handshake/session ID, старые подписки и Sampling выдавались за универсальные; Q30 содержал неподтверждённую хронологию внедрения. Исправлено по [релизу 2026-07-28](https://blog.modelcontextprotocol.io/posts/2026-07-28/) и [changelog](https://modelcontextprotocol.io/specification/2026-07-28/changelog). | JSON Q6/Q8/Q13/Q14/Q28–Q30 обновлён; структура PASS. Parity всего старого файла FAIL из-за 24/30 длинных correct и других старых блоков; свежесть всего файла TODO, независимая приёмка ожидается. |
| P2 · углублено · О | `frameworks/spring/spring-ai-interview.md` Q14 | Кеш ответа был смешан с prompt caching провайдера; добавлены префикс, стратегии, TTL, предел breakpoint и показатели cache write/read по [Spring AI Anthropic reference](https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html#prompt-caching). | Добавлен JSON Q14:B1; структура/parity всего файла PASS, независимая приёмка ожидается. |
| P2 · `ADD_Q`, выполнено · О | `frameworks/spring/spring-data-jdbc-interview.md` Q17; рядом Q12–Q16 и `databases/sql-interview.md` Q16 | Общий SQL upsert не объяснял API `JdbcAggregateTemplate.upsert`: назначенный ID, однокомандный диалект, отсутствие optimistic locking. Сверено с [Spring Data JDBC reference](https://docs.spring.io/spring-data/relational/reference/jdbc/entity-persistence.html#upsert) и [Javadoc 4.1.1](https://docs.spring.io/spring-data/relational/reference/api/java/org/springframework/data/jdbc/core/JdbcAggregateOperations.html#upsert(T)). | JSON Q17 добавлен; структура/parity всего файла PASS, независимая приёмка ожидается. |

Новые файлы авторски сверены по каждому Q, но ни один production MCQ этого
пакета не получил независимый blind review. Остальные Q изменённых старых
файлов содержательно не проверены полностью.

Spring Data JDBC Q17 закрывает ранее открытый кандидат `upsert`; исходное
сравнение с SQL Q16 и официальная семантика теперь проверены. Независимая
приёмка нового MCQ ещё не выполнена.

Группа Java/JDK 26–27 выполнена **05.10.2026** тремя тематическими коммитами:
`99970c22`, `95c046ca`, `02c4493d`. После сверки соседних ответов
`java-22-25-interview.md` Q8/Q14/Q22 создан отдельный
`java-26-27-interview.md` Q1–Q4 (`NEW_TOPIC`, P2 · О): новые default G1 и
compact headers, седьмой preview structured concurrency, ограниченное
редактирование JFR и гибридный TLS. Ближайшие [JDK 26 release notes](https://www.oracle.com/java/technologies/javase/26all-relnotes.html),
[JDK 27 release notes](https://www.oracle.com/java/technologies/javase/27all-relnotes.html)
и JDK 27 API подтверждают границы версий. Новая пара — 4 Q/4 MCQ,
структура и полный pre-check PASS, независимой blind-приёмки нет.

Для HTTP/3 добавлен `java-io-nio-interview.md` Q41 (`ADD_Q`, P2 · О), рядом с
Q40: предпочтение `HTTP_3`, режимы discovery, HTTPS/proxy и проверка
`response.version()` по [JDK 26 HttpClient API](https://docs.oracle.com/en/java/javase/26/docs/api/java.net.http/java/net/http/HttpClient.html).
Q40 уточнён по версии. MCQ Q40/Q41 синхронизированы, структура 41/41 PASS;
parity всего старого файла FAIL (38/41 correct самые длинные), новый Q41
проверен локально, blind review нет. Для Jackson 3 добавлен
`java-jackson-interview.md` Q32 (`ADD_Q`, P2 · О) и уточнены Q28/Q24:
Boot 3/Jackson 2 и Boot 4/Jackson 3 разведены, ошибка про причину массива
`LocalDateTime` исправлена по [официальному migration guide](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md)
и [Spring Boot 4 JSON reference](https://docs.spring.io/spring-boot/reference/features/json.html).
MCQ Q24/Q28/Q32 синхронизированы, структура 32/32 PASS; parity всего
старого файла FAIL (29/32 correct самые длинные), новые/затронутые блоки
проверены локально, blind review нет. **Локально есть только JDK 22**:
примеры JDK 26/27 сверены по API, но не запускались. Ни один старый Java-файл
не получил полный уровень `П`.

Группа Python typing/packaging выполнена **05.10.2026** коммитом `12411235`.
После проверки Python core Q1/Q9, concurrency Q1–Q9, testing Q1–Q7,
FastAPI и смежных CI/CD-заголовков созданы две самостоятельные темы:
`python-typing-interview.md` Q1–Q4 (`NEW_TOPIC`, P2 · О) и
`python-packaging-interview.md` Q1–Q4 (`NEW_TOPIC`, P2 · О). Первый файл
разделяет статический контракт и runtime-валидацию, generic-связь,
`Protocol` и `TypedDict` по [Python typing](https://docs.python.org/3/library/typing.html)
и [спецификации](https://typing.python.org/en/latest/spec/). Второй разбирает
`pyproject.toml`, wheel/sdist, extras/groups и повторяемую установку по
[PyPA packaging](https://packaging.python.org/en/latest/) и
[pip Repeatable Installs](https://pip.pypa.io/en/stable/topics/repeatable-installs/).
Обе пары имеют 4 Q/4 MCQ; структура и полный pre-check PASS. Python-примеры
выполнены на локальном 3.14.4, TOML-примеры разобраны `tomllib`. Это
авторская проверка, **не независимая blind-приёмка** и не уровень `П` для
старых соседних файлов. Инвентарь и навигация обновлены (+2 файла, +8 Q).

Первый P2-кандидат из источника `system-design-notes` выполнен **05.10.2026**
коммитом `97c1a764`: создан `design-unique-id-generator-interview.md`
Q1–Q4 (`NEW_TOPIC`, P2 · О) с парным MCQ. Соседи `system-design-interview.md`
Q7, `design-url-shortener-interview.md` Q19 и `sql-interview.md` Q13 уже
объясняли URL-код и базовый sequence, но не выбор ID по области уникальности,
сбои Snowflake/диапазонов и границы UUIDv4/v7. Сверено по
[RFC 9562](https://datatracker.ietf.org/doc/html/rfc9562),
[PostgreSQL 18 sequence](https://www.postgresql.org/docs/18/functions-sequence.html),
[Flickr ticket servers](https://code.flickr.net/2010/02/08/ticket-servers-distributed-unique-primary-keys-on-the-cheap/)
и [исходному описанию Twitter](https://blog.x.com/engineering/en_us/a/2010/announcing-snowflake).
В URL Shortener Q19 исправлены ошибочный общий throughput и строгое
глобальное упорядочивание; MCQ Q19 переписан в согласии с ответом. Новая
пара: структура/pre-check 4/4 PASS. URL Shortener: структура/parity 30/30
PASS, но **полный pre-check FAIL** из-за старых сигналов формы (24 блока);
обновлённый Q19 не даёт жёсткого локального сигнала. Независимой blind-
приёмки нет, старый файл не получил уровень `П`. Инвентарь: +1 файл, +4 Q.

Следующий P2-кандидат `Notification System` выполнен **05.10.2026**
коммитом `4441fc17`:
`design-notification-system-interview.md` Q1–Q4 (`NEW_TOPIC`, P2 · О) и
парный MCQ. Базовый `system-design-interview.md` Q10 уже покрывал каналы,
предпочтения и очереди; `message-brokers-comparison-interview.md` Q24 —
fan-out. Новый маршрут отделён по статусам принятия/доставки, дублям на
границе провайдера, повторной проверке предпочтений и сроков, классификации
ошибок. Сверено по [FCM lifespan](https://firebase.google.com/docs/cloud-messaging/customize-messages/setting-message-lifespan),
[FCM scale/error codes](https://firebase.google.com/docs/cloud-messaging/scale-fcm),
[APNs responses](https://developer.apple.com/documentation/usernotifications/handling-notification-responses-from-apns),
[SES send process](https://docs.aws.amazon.com/ses/latest/dg/send-email-concepts-process.html)
и [AWS outbox](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/transactional-outbox.html).
Новая пара: структура/parity/pre-check 4/4 PASS, авторская проверка вариантов
и ссылок завершена; **независимого blind review нет**. Существующий Q10
точечно прочитан (`О`), весь старый файл не проверялся (`П`). Инвентарь:
+1 файл, +4 Q. Политики consent/quiet hours требуют продуктового решения,
а не объявлены универсальным правилом провайдеров.

P2-кандидат `Distributed Message Queue` проверен **05.10.2026** и закрыт
как новый файл: `kafka-interview.md` Q6–Q24 уже разбирает partition,
offset, репликацию, ack, replay и retention; `rabbitmq-interview.md`
Q18–Q30 — подтверждения, повторы и отказ узла; `system-design-interview.md`
Q21 — применение очереди. Отдельный общий файл повторил бы эти маршруты.
Вместо него коммитом `a894e224` исправлена устаревшая граница «RabbitMQ
не умеет replay»: RabbitMQ Q40/Q41, Kafka Q42,
`message-brokers-comparison-interview.md` Q15 и
`event-driven-patterns-interview.md` Q3 точечно обновлены (`DEEPEN`, P2 · О),
добавлен RabbitMQ Q42 (`ADD_Q`, P2 · О) и парный MCQ. По
[RabbitMQ Streams 4.3](https://www.rabbitmq.com/docs/streams),
[официальному сравнению](https://www.rabbitmq.com/docs/compare/kafka)
и [Kafka topic configs 4.3](https://kafka.apache.org/43/configuration/topic-configs/)
Streams дают неразрушающее чтение по offset до retention, в отличие от
classic/quorum queue после ack; super stream делится на партиции.
Все четыре JSON прошли структуру; RabbitMQ/Kafka/сравнение брокеров —
parity PASS, event-driven — parity FAIL из-за старых блоков. Полный pre-check
всех четырёх JSON остаётся FAIL на прежних блоках: RabbitMQ 27, Kafka 36,
сравнение 1, event-driven 24 hard-сигнала; **новые/изменённые Q локально
без hard-сигналов**, независимого blind review нет. `CHECKED` sidecar
сравнения брокеров сброшен в `TODO` после изменения Q15. Ни один из четырёх
старых файлов не получил уровень `П`. Инвентарь: +1 Q, файлов без изменений.

## Отсутствующие файлы: исходная очередь и оставшиеся кандидаты

Исходный срез до авторинга: Java — **28 файлов / 910 Q**, Kotlin — **12 / 370**,
Go — **7 / 209**, Scala — **2 / 56**. Тогда в interview-разделе не было Python.
Отдельный Go `database/sql`-файл не понадобился: stdlib Q22–Q25 углублены.

В исходной очереди `КАНДИДАТ` означал: на дату среза файла не было, область
определена по названиям и Q, но требовала проверки соседей и источников.
Строки ниже сохраняют исходное решение о границах; актуальный статус
реализации — в таблице «Выполнено» выше.

| Приоритет | Статус исходного среза | Кандидат и граница |
|---|---|---|
| P1 | ПРОВЕРЕНО по файлам/Q; `NEW_TOPIC` | `programming-languages/python/python-interview.md`: модель имён и объектов, изменяемость, коллекции, функции, области видимости, итераторы/генераторы, исключения, классы и data model. В interview нет Python core; единичные Python-примеры в AI/ML не заменяют тему. Не смешивать с Django/ML. |
| P1 | ПРОВЕРЕНО по файлам/Q; `NEW_TOPIC` | `programming-languages/python/python-concurrency-interview.md`: `asyncio`/`TaskGroup`/отмена, потоки/процессы, GIL и free-threaded CPython, выбор для I/O/CPU. Отдельный механизм от Python core; фиксировать версию и сборку CPython. |
| P1 | ОТКЛОНЕНО как новый файл; `DEEPEN` | `programming-languages/go/go-stdlib-interview.md` Q22–Q25 уже образуют маршрут по `database/sql`; отдельный `go-database-interview.md` сейчас раздробит его. Точечные пробелы: `QueryContext`/отмена, `Rows.Err`, диагностика ожидания пула через `DB.Stats()`. [Раздел Go](https://go.dev/doc/database/). |
| P2 | КАНДИДАТ: два файла | Python typing и packaging имеют разные проверяемые задачи: контракт типов и воспроизводимая поставка. Соседей и источники см. в пакете 05.10. [Packaging guide](https://packaging.python.org/en/latest/). |
| P2 | ПРОВЕРЕНО по файлам/Q; `NEW_TOPIC` | `programming-languages/python/python-testing-interview.md`: `pytest`/`unittest`, fixtures, параметризация, mock, async-тесты и изоляция. `cheatsheets/testing/unit-testing/pytest/` — справочник вне interview; общие interview-тесты не дают Python-маршрут. |
| P2 | ПРОВЕРЕНО по файлам/Q; `NEW_TOPIC` | `frameworks/python/fastapi-interview.md`: ASGI, зависимости, lifespan, валидация, ошибки, тесты и эксплуатация API. Общий [обзор](cheatsheets/frameworks/python-frameworks/python-frameworks-overview.md) вне interview; не копировать общий API/asyncio материал. |
| P2 | ПРОВЕРЕНО по файлам/Q; `NEW_TOPIC` | `databases/mysql-interview.md`: InnoDB, индекс/план, `utf8mb4` и charset соединения, диагностика `Communications link failure`; общие SQL/репликацию оставить в соседях. Источники и пересечения — в пакете 05.10. |
| P2 | ПРОВЕРЕНО по файлам/Q; `NEW_TOPIC` | `databases/jdbc-connection-pool-interview.md`: `DataSource`, ресурсный цикл `Connection`/`ResultSet`, autocommit, batch/streaming и диагностика пула. `sql-interview.md` Q45 сравнивает JDBC/JPA, а Spring Data JDBC — другой уровень абстракции. Источники и пересечения — в пакете 05.10. |
| P3 | ОТКЛОНЕНО как общая новая Go-тема; КАНДИДАТ для Python | Go HTTP уже имеет `go-stdlib-interview.md` Q1–Q5 и `go-testing-interview.md` Q14–Q15; профилирование есть в `go-memory-gc-interview.md` Q15–Q16. Django/Flask пока не приоритетнее Python core и FastAPI; проверять лишь при подтверждённом профиле вакансий. |

**Критерий нового файла:** самостоятельный вопросный маршрут с несколькими
неповторяющимися сценариями, ясная граница с соседями, первичные источники и
ценность для backend-собеседования. Если идея укладывается в один-два Q, выбрать
`ADD_Q` или `DEEPEN`, не новый файл. Очередь — гипотезы по карте тем;
содержательное решение принимается после точечной проверки.

### Размер корпуса по разделам

| Раздел | Файлов | Q | Раздел | Файлов | Q |
|---|---:|---:|---|---:|---:|
| AI/ML | 26 | 776 | Algorithms | 18 | 536 |
| API | 8 | 270 | Architecture | 24 | 858 |
| Behavioral | 6 | 148 | CI/CD | 2 | 77 |
| Cloud | 6 | 177 | Code quality | 7 | 227 |
| Data engineering | 8 | 236 | Databases | 23 | 737 |
| Design patterns | 1 | 48 | DevOps | 13 | 450 |
| Frameworks | 37 | 886 | JVM | 3 | 71 |
| Leadership | 7 | 187 | Logging | 1 | 42 |
| Messaging | 9 | 238 | Monitoring | 9 | 285 |
| Performance | 8 | 253 | Preparation | 1 | 42 |
| Programming languages | 57 | 1 652 | Reactive | 6 | 206 |
| Security | 10 | 351 | System design | 31 | 681 |
| Testing | 16 | 489 | **Итого** | **337** | **9 923** |

## Срез публикаций за последние полгода

Дополнительная точечная выборка сентябрьского архива Baeldung выявила идеи
**вне узкого backend-списка**. Статус ниже отделяет чтение заголовка и соседнего
Q (`З`) от проверки ответа по официальному источнику (`О`); полного аудита
существующих файлов (`П`) не было. Это не исчерпывающий просмотр сайта.

| Приоритет / исход | Публикация и официальный источник | Ближайший файл и Q; конкретный пробел | Проверка |
|---|---|---|---|
| P0 · `DEEPEN`, выполнено | [Baeldung: MCP Logging in Spring AI](https://www.baeldung.com/spring-ai-mcp-logging), 06.09; [MCP 2026-07-28](https://blog.modelcontextprotocol.io/posts/2026-07-28/) | `ai-ml/mcp-interview.md` Q6/Q8/Q13/Q14/Q28–Q30: версия протокола и deprecated-механизмы не были разграничены. Новая Logging-тема пока не нужна: Q уже охватывают протокол, а Spring AI logging требует отдельного сравнения с `spring-ai-interview.md` Q15. | `О` по указанным Q и changelog; JSON обновлён, структура PASS, файл целиком не принят. |
| P2 · `DEEPEN`, выполнено | [Baeldung: Anthropic prompt caching in Spring AI](https://www.baeldung.com/spring-ai-anthropic-claude-prompt-cache), 16.09; [Spring AI Anthropic reference](https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html#prompt-caching) | `frameworks/spring/spring-ai-interview.md` Q14 упоминал кеширование, но не отделял кеш префикса от готового ответа, write/read usage, TTL и ограничений провайдера. | `О`; Q14 и MCQ B1 обновлены, структура/parity PASS, blind review ожидается. |
| P3 · `DEEPEN`, выполнено | [Baeldung: Hibernate @Find](https://www.baeldung.com/java-hibernate-find-annotation), 26.09; [Hibernate 7.1 Javadoc](https://docs.hibernate.org/orm/7.1/javadocs/org/hibernate/annotations/processing/Find.html) и [Hibernate Processor](https://docs.hibernate.org/orm/7.1/introduction/html_single/) | `databases/hibernate-jpql-criteria-interview.md` Q1 перечисляет базовые способы запросов, Q6 описывает metamodel, но не generated finder. Углублён Q6: `@Find` с 6.3, в 7.1 incubating, параметры соответствуют полям, имя метода произвольно; артефакт процессора 7.1 исправлен. | `О` по Q1/Q6; Q6 и MCQ B1 написаны. JSON structure PASS, parity **всего старого файла FAIL**; новый блок проверен вручную, независимая приёмка не выполнена. |
| `COVERED` по заголовку | FitNesse acceptance testing в [архиве Baeldung](https://www.baeldung.com/full_archive) | `testing/test-strategies-interview.md` Q8 уже объясняет ATDD и называет FitNesse; по одной публикации отдельный Q не оправдан. | `З`; содержательный аудит ответа не проводился. |


Текущее окно на **2026-10-05**: **2026-04-05 — 2026-10-05**. Предыдущий обзор
за 03.04—03.10 сохранён как отдельный снимок: тогда просмотрены заголовки
**119 записей** [архива Baeldung](https://www.baeldung.com/full_archive):
118 за апрель—сентябрь (25 + 21 + 18 + 23 + 13 + 18) и одна за октябрь.
В счёт входят еженедельные дайджесты, индексы серий и нерелевантные статьи;
это **охват заголовков архива, не прочтение 119 полных статей**. Отобранные
статьи сопоставлены с локальными Q и, где сделан фактический вывод, с
официальной документацией. Из [Go Blog](https://go.dev/blog/),
[Spring Blog](https://spring.io/blog/), [Python.org](https://www.python.org/downloads/)
и других первичных источников проверена **выборка**, а не весь полугодовой
поток. Дата в таблице — дата первоначальной публикации по архиву или дата
релиза; «last updated» старой статьи само по себе не доказывает новизну.
Даты и версии перепроверять перед написанием.

Новая статья — повод сопоставить **тему и будущий вопрос** с каталогом, а не
команда скопировать статью в шпаргалку. Таблица ниже — **исходный
исследовательский срез до авторинга**; текущие статусы перечислены выше.
Здесь `ПРОВЕРЕНО` означало, что
локализован конкретный конфликт с текстом Q и официальным источником;
`КАНДИДАТ` — что найдена тема без достаточной проверки пересечений и ответов.
Точечно прочитаны только ответы, нужные для подтверждения конфликта; остальные
9 818 ответов содержательно не вычитывались.

| Приоритет / статус | Публикация | Куда смотреть | Решение для плана |
|---|---|---|---|
| P0 / **ПРОВЕРЕНО** | 19.08 [Go 1.27](https://go.dev/blog/go1.27), 26.08 [Generic Methods](https://go.dev/blog/generic-methods) | `programming-languages/go/go-generics-interview.md` Q10/Q18–Q20 | `DEEPEN`/исправить Q10/Q18–Q19: в тексте категорически запрещены type-параметры методов, а Go 1.27 их поддерживает. Q20 проверить отдельно из-за расширенного вывода типов. Указать границу версий и проверить связанные примеры и MCQ при отдельном поручении на контент. |
| P0 / **ПРОВЕРЕНО** | 07.04 [MFA в Spring Security 7](https://www.baeldung.com/spring-security-7-mfa); [официальный reference](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/mfa.html) | `frameworks/spring/spring-security-interview.md` Q30 | `DEEPEN`/исправить утверждение «встроенного 2FA из коробки нет»: с версии 7 есть поддержка требования нескольких факторов через `FactorGrantedAuthority` и `@EnableMultiFactorAuthentication`. Объяснить границы встроенного механизма и отдельной реализации TOTP/собственного flow; сверить совместимость с версией файла и будущий MCQ. |
| P0 / **ПРОВЕРЕНО** | 22.08 [SSRF и `InetAddressFilter` в Spring Boot](https://www.baeldung.com/spring-boot-http-client-ssrf-mitigation-inetaddressfilter); [Spring Boot API](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/http/client/InetAddressFilter.html), [OWASP](https://cheatsheetseries.owasp.org/cheatsheets/Server_Side_Request_Forgery_Prevention_Cheat_Sheet.html) | `security/owasp-top10-interview.md` Q34; `security/application-security-interview.md` Q13; соседний `frameworks/spring/spring-rest-client-interview.md` Q1 | `DEEPEN`/исправить оба примера: IP проверяется при отдельном DNS-запросе, а HTTP-клиент затем получает исходный URL и может резолвить его повторно; пример не доказывает защиту от DNS rebinding. Для Spring Boot 4.1+ проверить применение `InetAddressFilter` в конфигурации клиента, ограничения redirect и egress; для старых версий описать эквивалентную проверку на этапе фактического соединения. Не создавать новую SSRF-тему. |
| P2 / `ADD_Q`, написано · О | 19.08 [Go 1.27](https://go.dev/blog/go1.27); [release notes](https://go.dev/doc/go1.27#jsonv2), [json/v2 docs](https://pkg.go.dev/encoding/json/v2), [method sets](https://go.dev/ref/spec#Method_sets) | `programming-languages/go/go-stdlib-interview.md` Q7–Q10 описывали v1, но не выбор API и изменение JSON-контракта при миграции; Q10 ошибочно утверждал, что value receiver не входит в method set указателя. | Добавлен Q31: `json/v2`/`jsontext`, строгие defaults, совместимый v1 и проверка payload/golden JSON. Q10 исправлен, MCQ Q10 B0 и Q31 B0/B1 синхронизированы. Structure PASS, parity всего старого файла FAIL; новые блоки без жёстких локальных сигналов, blind review ожидается. Go 1.27 локально не установлен, пример сверялся по документации. Инвентарь +1 Q. |
| P2 / `DEEPEN`, выполнено · О | 02.09 [Goroutine Leak Profiles](https://go.dev/blog/goroutine-leak-profiles); [Go 1.27 release notes](https://go.dev/doc/go1.27#goroutine-leak-profile), [runtime/pprof](https://pkg.go.dev/runtime/pprof) | `programming-languages/go/go-concurrency-interview.md` Q32 уже покрывал причину/профилактику утечек; `go-memory-gc-interview.md` Q15 объясняет HTTP-профили, но ни один не отличал новый профиль от полного списка goroutines. | Q32 углублён: on-demand `goroutineleak`, достижимость при GC, путь `/debug/pprof/goroutineleak`, пропуски для глобально достижимых примитивов и I/O. MCQ Q32 B0 исправлен, B1 добавлен; structure PASS, parity всего старого файла FAIL, локальные блоки без жёстких сигналов; blind review ожидается. Go 1.27 локально не установлен. |
| P2 / `NEW_TOPIC`, выполнено · О | 15.09 [JDK 27 release notes](https://www.oracle.com/java/technologies/javase/27all-relnotes.html), [JDK 26 release notes](https://www.oracle.com/java/technologies/javase/26all-relnotes.html); 29.09 [обзор Baeldung](https://www.baeldung.com/java-27-new-features) | `programming-languages/java/java-22-25-interview.md` Q8/Q14/Q22 уже описывал Java 25, но не переход 26/27 | Создан `java-26-27-interview.md` Q1–Q4: новые default JVM, preview, JFR, TLS. Авторская точечная проверка `О`; 4/4 MCQ structure/pre-check PASS, blind review ожидается. |
| P2 / `ADD_Q`, выполнено · О | 10.09 [HTTP/3 в Java HttpClient](https://www.baeldung.com/java-httpclient-http-3-quic); [JDK 26 HttpClient API](https://docs.oracle.com/en/java/javase/26/docs/api/java.net.http/java/net/http/HttpClient.html) | `programming-languages/java/java-io-nio-interview.md` Q40 описывал только базовый API 11+ | Добавлен Q41: выбор HTTP/3, discovery/fallback и ограничения. Q40 уточнён. MCQ Q40/Q41 синхронизированы; structure PASS, старый parity backlog и blind review открыты. |
| P2 / `ADD_Q`, выполнено · О | 10.09 [Jackson 3](https://www.baeldung.com/java-jackson-3-updates); [официальный migration guide](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md), [Boot 4 JSON](https://docs.spring.io/spring-boot/reference/features/json.html) | `programming-languages/java/java-jackson-interview.md` Q28/Q30 не отделяли Boot 3/Jackson 2 от Boot 4/Jackson 3; Q24 неверно объяснял массив дат | Добавлен Q32, исправлены Q24/Q28 и MCQ. Structure PASS, старый parity backlog и blind review открыты; полный аудит `П` не проводился. |
| P2 / `DEEPEN`, выполнено | 22.04 [Spring Data AOT Repositories](https://www.baeldung.com/spring-data-aot-repositories); [Spring Data JPA AOT 4.1.1](https://docs.spring.io/spring-data/jpa/reference/jpa/aot.html) | `frameworks/spring/spring-data-jpa-interview.md` Q4–Q6; общий AOT в `spring-boot-3-migration-interview.md` Q8 | Q4 углублён: генерация подходящих query methods при сборке, ограничения `ScrollPosition`/dynamic projections, отсутствие обращения к БД, фиксация диалекта; базовый CRUD остаётся. `О` по Q4–Q6, Q4 MCQ B0/B1 обновлён; structure PASS, parity **всего старого файла FAIL**, blind review ожидается. |
| P2 / `ADD_Q`, написано · О | 25.07 [Application Startup Tracking](https://www.baeldung.com/spring-application-startup-tracking); [Spring Boot reference 4.1.1](https://docs.spring.io/spring-boot/reference/features/spring-application.html), [startup API](https://docs.spring.io/spring-boot/api/rest/actuator/startup.html) | `frameworks/spring/spring-boot-actuator-interview.md` Q3 только перечислял endpoint; Q38 оказался про heapdump/threaddump, Q42 — про Kubernetes startup probe. `spring-framework-interview.md` Q6/Q10 описывают жизненный цикл без измерения его шагов. | Добавлен Actuator Q44 и MCQ B0/B1: Buffering vs FlightRecorder, GET snapshot/POST drain, экспозиция и ограниченная ёмкость. Structure/parity PASS; blind review ожидается. Инвентарь обновлён: +1 Q. |
| P2 / `NEW_TOPIC`, выполнено · О | 22.07 [MySQL: Incorrect string value](https://www.baeldung.com/java-mysql-sqlexception-incorrect-string-value), 28.05 [Communications link failure](https://www.baeldung.com/jdbc-connector-j-mysql-communications-link-failure); [MySQL charset](https://dev.mysql.com/doc/refman/8.4/en/charset-connection.html), [Connector/J troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html) | `databases/mysql-interview.md` Q5/Q6 уже созданы; рядом `databases/jdbc-connection-pool-interview.md` Q6 и `sql-interview.md` Q45. | Q5 разделяет charset соединения и столбца, collation и `utf8mb4`; Q6 — таймауты/сеть/пул после простоя. Парные MCQ существуют, структура/parity PASS по срезу плана; независимая приёмка ожидается. Повторное создание тем не требуется. |
| P2 / `DEEPEN`, выполнено · О | 23.06 [Spring AI 2.0: structured output](https://spring.io/blog/2026/06/23/spring-ai-self-correcting-structured-output/), 25.09 [Baeldung](https://www.baeldung.com/spring-ai-structured-output); [Spring AI 2.0.1 reference](https://docs.spring.io/spring-ai/reference/api/structured-output.html) | `frameworks/spring/spring-ai-interview.md` Q3: `.entity()` ошибочно описывался как строгое JSON-поведение, пример конкатенировал код. | Q3 углублён: best effort, provider-native constraint, schema validation/retry и цена вызовов, предел `.stream()`, риск prompt injection. MCQ B0 обновлён, B1 добавлен; structure/parity PASS, blind review ожидается. |
| P2 / `DEEPEN`, выполнено · О | 15.06 [Spring AI 2.0: tool calling](https://spring.io/blog/2026/06/15/spring-ai-composable-tool-calling/); [Spring AI 2.0.1 reference](https://docs.spring.io/spring-ai/reference/api/tools.html) | `frameworks/spring/spring-ai-interview.md` Q7/Q15: пример создавал Spring-бин через `new` без зависимости, текст советовал старый `.toolNames(...)`, агент использовал необъявленное поле. | Q7/Q15 исправлены: `ToolCallingAdvisor`/`ToolCallingManager`, явный `ToolCallback` для Function, внедрение бинов, memory order и контроль side effects. MCQ Q7 B0/B1 и Q15 B0 синхронизированы; structure/parity PASS, blind review ожидается. |
| P3 / `DEEPEN`, выполнено · О | 27.06 [Bean Background Initialization](https://www.baeldung.com/spring-bean-background-init); [Spring Framework 6.2 reference](https://docs.spring.io/spring-framework/reference/6.2/core/beans/java/composing-configuration-classes.html) и [Bean API](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/context/annotation/Bean.html) | `frameworks/spring/spring-framework-interview.md` Q10 о жизненном цикле; Q38 относится к `@Async`, это другой механизм. | Q10 углублён: `@Bean(bootstrap=BACKGROUND)` с 6.2 требует `bootstrapExecutor`, non-lazy завершится до конца `refresh()`, ранняя инъекция может блокировать старт. 07.10 дополнительно исправлены схема и B0 A: аргументы конструктора разрешаются при создании, поля/сеттеры — после; [Spring DI](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html). JSON Q10 B0/B1 синхронизирован; 41 Q/42 блока, structure/parity PASS. Полный pre-check теперь hard=0 (23 предупреждения), Q10 без hard-сигналов; авторская точечная проверка выполнена, blind review ожидается. |
| P2 / `DEEPEN`, выполнено · О | [Spring Framework overview](https://spring.io/projects/spring-framework/), [Spring projects](https://spring.io/projects/), [Spring DI](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html), [Boot testing](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html) | `frameworks/spring/spring-framework-interview.md` Q1 ошибочно называл Boot/Security/Data модулями Framework; Q2 относил к Framework интеграции отдельных проектов и представлял `@SpringBootTest` как тест части контекста. Q9 MCQ имел старый сигнал формы. | Q1 таблица/схема разделяют модули Framework и проекты экосистемы; Q2 уточняет границы интеграций и тестовых API. JSON Q1/Q2/Q9 синхронизирован, варианты Q1/Q9 отредактированы; 41 Q/42 блока, structure/parity PASS, pre-check hard=0, 23 предупреждения. Точечная авторская проверка выполнена, полный содержательный обзор и blind review ожидаются. |
| P3 / `DEEPEN`, выполнено | 27.06 [Kafka CommitFailedException](https://www.baeldung.com/java-kafka-commitfailedexception); [KafkaConsumer 4.3.1](https://kafka.apache.org/43/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html) | `messaging/kafka-interview.md` Q15/Q16/Q45 | Q16 дополнен: `CommitFailedException` после потери партиции нельзя слепо повторять; auto-commit может дать at-least-once при полной обработке batch до следующего `poll()`. `О` по Q15/Q16/Q45; Q16/MCQ B1 написаны, structure/parity PASS, blind review ожидается. |
| P3 / `DEEPEN`, выполнено · О | 21.06 [Spring Boot 4.1 и MongoDB JobRepository](https://spring.io/blog/2026/06/21/spring-boot-41-and-spring-batch/); [Batch 5.0 final](https://docs.spring.io/spring-batch/docs/5.0.1/reference/html/whatsnew.html), [Batch 5.2](https://docs.spring.io/spring-batch/reference/5.2/whatsnew.html), [Batch 6](https://docs.spring.io/spring-batch/reference/whatsnew.html), [Boot 3.5](https://docs.spring.io/spring-boot/3.5/how-to/batch.html), [Boot 4.1](https://docs.spring.io/spring-boot/reference/io/spring-batch.html), [partitioning](https://docs.spring.io/spring-batch/reference/scalability.html), [test API](https://docs.spring.io/spring-batch/reference/api/org/springframework/batch/test/JobOperatorTestUtils.html) | `frameworks/spring/spring-batch-interview.md` Q6/Q28/Q38: Q6 описывал только JDBC, вступление называло 5.2.x актуальной версией, Q28/Q38 смешивали роли Batch и Boot. Смежные Q39/Q42 были неточны по параллелизму и границе тестового API. | Q6 уточнён: MongoDB и resourceless появились в 5.2, в 6 изменился default ручной конфигурации. Q28 различает JDBC-таблицы и MongoDB-коллекции. Q38 переписан по **финальному** Batch 5.0: удаление map-based repository и прекращение публикации bean менеджера транзакций; ошибочное утверждение из предварительной документации о замене `DataSourceTransactionManager` удалено. Q39 различает `gridSize` и фактический параллелизм; Q42 отмечает `JobOperatorTestUtils` в Batch 6. Production JSON Q2/Q6 B0/B1/Q26/Q27/Q28/Q38/Q39/Q42 синхронизирован: 43 Q/44 блока, structure/parity PASS, полный pre-check hard=0 (17 предупреждений вне этой группы). Точечная авторская проверка A/B/C/D проведена, полный содержательный обзор файла и независимая blind-приёмка ожидаются. |
| P3 / КАНДИДАТ | 02.10 [Python 3.15.0rc3](https://www.python.org/downloads/release/python-3150rc3/), [черновик What's New](https://docs.python.org/3.15/whatsnew/3.15.html) | `programming-languages/python/python-interview.md` Q1–Q9 уже написаны | В существующей Python-теме рассмотреть **один версионный блок**, если изменения полезны backend-разработчику. На 07.10 официальный [3.15.0rc3](https://www.python.org/downloads/release/python-3150rc3/) всё ещё указывает финальный релиз на 09.10.2026. Перед написанием снова проверить GA и окончательную документацию. |
| P3 / КАНДИДАТ, **БЕТА** | 24.09 [PostgreSQL 19 beta 4](https://www.postgresql.org/about/news/postgresql-19-beta-4-released-3386/), [черновик release notes](https://www.postgresql.org/docs/19/release-19.html) | `databases/postgresql-interview.md` Q32/Q44–Q46 | После GA проверить `DEEPEN` Q32 о встроенном `REPACK` против `VACUUM FULL`/`pg_repack` и возможный Q о `WAIT` для read-your-writes на standby. На 07.10 официальный [архив релизов](https://www.postgresql.org/docs/release/) всё ещё показывает Beta 4; черновик release notes не имеет даты GA. До релиза не описывать предложения как гарантированную функциональность PostgreSQL 19. |

Проверенные пересечения и отклонения из того же архива (основание — наличие
целевого Q/соседней темы, а не приёмка всего ответа):

| Исход | Публикация | Где уже есть тема / почему не добавлять |
|---|---|---|
| `COVERED` по заголовку | 30.07 [CTE Support in Hibernate](https://www.baeldung.com/hibernate-common-table-expressions) | `databases/hibernate-jpql-criteria-interview.md` Q16 прямо включает CTE в HQL 6; нового Q по одному заголовку статьи не требуется. |
| `COVERED` по заголовку | 11.05 [Key Derivation Function API in Java 25](https://www.baeldung.com/java-25-key-derivation-function-api) | `programming-languages/java/java-22-25-interview.md` Q18 посвящён JEP 510. |
| `COVERED` по заголовку | 11.05 [Synchronize Virtual Thread Without Pinning](https://www.baeldung.com/java-synchronize-virtual-thread-no-pinning) | `programming-languages/java/java-22-25-interview.md` Q9 посвящён JEP 491. |
| `OUT_OF_SCOPE` для нового файла | 22.08 [DB-Scheduler](https://www.baeldung.com/java-db-scheduler) | `frameworks/spring/spring-scheduling-interview.md` Q11–Q13 уже обсуждают кластерное планирование, ShedLock и Quartz; отдельная шпаргалка об одной библиотеке без подтверждённого спроса не нужна. |
| `OUT_OF_SCOPE` для очереди | Еженедельные дайджесты и индексы серий в [архиве](https://www.baeldung.com/full_archive) | Это навигационные записи; конкретный backend-вопрос извлекать из первичной статьи, а не из номера дайджеста. |

Исходные черновики заголовков из исследовательского среза. Часть уже
реализована, как указано в таблице «Выполнено»:

- Security: «Что встроено в MFA Spring Security 7 и что приложение должно
  предоставить для второго фактора?»; «Почему отдельная проверка DNS перед
  исходящим HTTP-запросом не гарантирует защиту от SSRF?» Это темы правки
  **существующих** Q30 и Q34/Q13, а не новые файлы.
- Go: «Что изменилось для generic methods в Go 1.27 и какие ограничения
  остались?»; «Когда выбрать `encoding/json/v2`, а когда оставить
  `encoding/json`?»; «Как находить утечки goroutine через профиль
  `goroutineleak` и что он может пропустить?»
- Java/JDK: «Какие изменения Java 26–27 важны для backend-миграции и какие из
  них ещё preview?»; «Как `HttpClient` включает HTTP/3 и когда переходит на
  HTTP/2 или HTTP/1.1?»; «Что проверить при миграции приложения с Jackson 2
  на Jackson 3?»
- Spring: «Как обеспечить валидный структурированный ответ Spring AI и что
  происходит при повторе?»; «Кто выполняет цикл вызова инструментов в Spring
  AI 2.0?»; «Когда `JobRepository` Spring Batch может жить в MongoDB и какие
  требования это накладывает?»; «Что делает AOT-генерация репозиториев Spring
  Data JPA и какие запросы она не сможет обработать на этапе сборки?»;
  «Как использовать `ApplicationStartup` и `/actuator/startup` для диагностики
  медленного запуска?»
- MySQL/JDBC: «Почему Connector/J теряет соединение после простоя и как
  отличить timeout сервера от устаревшего соединения в пуле?»; «Почему MySQL
  выдаёт `Incorrect string value` и как соотносятся кодировки клиента, столбца
  и таблицы?»
- PostgreSQL: «Когда встроенный `REPACK` удобнее `VACUUM FULL` и расширения
  `pg_repack`?»; «Как `WAIT` помогает read-your-writes на standby?» — только
  после проверки финального PostgreSQL 19.
- Python: «Какие изменения стабильной версии Python 3.15 значимы для
  backend-приложения?» — только после проверки финального релиза.

Следующий обзор должен углубить **выборку официальных** блогов и релизов
Java/JDK, Kafka, MySQL и других релевантных проектов. Архивные заголовки
Baeldung за апрель—сентябрь уже пройдены; возвращаться к ним нужно для
конкретного открытого кандидата. После 05.10 проверять новые публикации и
двигать шестимесячное окно. Для каждой
отобранной статьи фиксировать дату первичной публикации, URL, конкретный
backend-сценарий, близкие `файл#Q`, исход из шести решений и основание
включения/отклонения. Более старые, но недавно обновлённые статьи учитывать
отдельно. Не считать просмотр заголовков полным аудитом содержания полугодия.

## Исходная проверка небольшими пакетами — 2026-10-05

Этот раздел сохраняет состояние **до авторинга**; слова «отсутствует» и
«будущий» ниже описывают тот исходный срез. Актуальные файлы и MCQ — в
таблице «Выполнено». Уровень `З`: сверены файлы и заголовки Q, но ответы не приняты; `О`:
точечно прочитан названный ответ; `П`: все ответы файла содержательно
проверены. В этом проходе уровня `П` **нет**. Статус `ПРОВЕРЕНО`
относится лишь к конкретному пробелу. Датированные новости и справочники
разделены: актуальная документация подтверждает факт, но сама не считается
новой публикацией полугодия.

### Пакет 1: приоритетные правки

| Приоритет / исход / уровень | Ближайший файл и Q; пробел | Источник и следующий шаг |
|---|---|---|
| P0 · `DEEPEN` · ПРОВЕРЕНО · О | `programming-languages/go/go-generics-interview.md` Q10/Q18/Q19 прямо запрещают type-параметры метода; Q20 утверждает, что вывод типов идёт только из аргументов. | [Go 1.27, 19.08](https://go.dev/blog/go1.27), [release notes](https://go.dev/doc/go1.27), [Generic Methods, 26.08](https://go.dev/blog/generic-methods). Разделить правила до/после Go 1.27; методы интерфейсов по-прежнему не могут объявлять type-параметры, generic-метод не реализует метод интерфейса. Отдельно проверить расширение inference в Q20 и парный JSON Q10/Q18–Q20 при будущем авторинге. |
| P0 · `DEEPEN` · ПРОВЕРЕНО · О | `frameworks/spring/spring-security-interview.md` Q30 говорит, что встроенного 2FA нет, и предлагает только собственный provider/TOTP flow; Q44 про WebAuthn — другая задача. | [Spring Security 7 reference](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/mfa.html): `FactorGrantedAuthority`, `@EnableMultiFactorAuthentication`, настройка механизмов факторов. Уточнить, что встроено для password+OTT и что приложение предоставляет само для TOTP/JWT; потом проверить JSON Q30. [Официальная статья 21.10.2025](https://spring.io/blog/2025/10/21/multi-factor-authentication-in-spring-security-7/) вне окна полугодия. |
| P0 · `DEEPEN` · ПРОВЕРЕНО · О | `security/owasp-top10-interview.md` Q34 делает DNS lookup, затем `executeRequest(uri)`; `security/application-security-interview.md` Q13 проверяет IP, затем отдаёт исходный URL `RestTemplate`. Проверенный адрес не связан с фактическим соединением; сосед `frameworks/spring/spring-rest-client-interview.md` Q1. | [OWASP SSRF guide](https://cheatsheetseries.owasp.org/cheatsheets/Server_Side_Request_Forgery_Prevention_Cheat_Sheet.html), [Spring Boot 4.1, 10.06](https://spring.io/blog/2026/06/10/spring-boot-4/), [настройка `InetAddressFilter`](https://docs.spring.io/spring-boot/reference/io/rest-client.html). При будущей правке показать проверку адреса в клиенте при подключении, redirects и egress; развести Boot 4.1+ и прежние версии. Проверить JSON Q34/Q13. |

### Пакет 2: Python и Go

В `programming-languages/` нет Python interview-файла. По Go просмотрены
заголовки **182 Q** в шести файлах: язык, generics, stdlib, concurrency,
memory/GC и testing. Седьмой файл о модулях не меняет вывод по `database/sql`.

| Приоритет / исход / уровень | Ближайший файл и Q; пробел | Источник и следующий шаг |
|---|---|---|
| P1 · `NEW_TOPIC` · ПРОВЕРЕНО · З | `programming-languages/python/python-interview.md` отсутствует; Python в `testing/property-based-testing-interview.md` Q9 и `testing/mutation-testing-interview.md` Q13 — лишь инструментальные примеры. Нет маршрута по объектам/именам, мутабельности, функциям, исключениям и data model. | [Python language reference](https://docs.python.org/3/reference/). Будущие Q: объекты/имена → функции/области видимости → итераторы/генераторы → исключения/контекстные менеджеры → классы/data model. Перед написанием развести границу с typing/concurrency. |
| P1 · `NEW_TOPIC` · ПРОВЕРЕНО · З | `programming-languages/python/python-concurrency-interview.md` отсутствует; `programming-languages/go/go-concurrency-interview.md` Q23–Q25 — про Go `context`, не Python. Нет Python-вопросов про event loop, `TaskGroup`/отмену, потоки/процессы и GIL. | [Python `asyncio` tasks](https://docs.python.org/3/library/asyncio-task.html), [free-threaded CPython](https://docs.python.org/3/howto/free-threading-python.html). Будущие Q: structured concurrency/отмена → I/O или CPU → обычная и free-threaded сборки. Не утверждать, что GIL снят во всех сборках. |
| P1 · `DEEPEN` · ПРОВЕРЕНО · О | Новый `go-database-interview.md` отклонён: `programming-languages/go/go-stdlib-interview.md` Q22–Q25 уже покрывают `sql.DB`, пул, prepared statements и транзакции. Пробелы точечные: `QueryContext`/отмена, `Rows.Err`, `DB.Stats()` при ожидании пула. | [Go database guide](https://go.dev/doc/database/), [cancel operations](https://go.dev/doc/database/cancel-operations), [manage connections](https://go.dev/doc/database/manage-connections). Углубить Q22–Q23 и сверить Q25; новый файл только при нескольких новых неповторяющихся сценариях. |
| P2 · `NEW_TOPIC` × 2 · выполнено · О | `programming-languages/python/python-typing-interview.md` Q1–Q4 и `programming-languages/python/python-packaging-interview.md` Q1–Q4: соседние Python core Q1/Q9, testing Q1–Q7 и FastAPI не покрывали контракт типов и поставку. | [Python typing](https://docs.python.org/3/library/typing.html), [Packaging: `pyproject.toml`](https://packaging.python.org/en/latest/specifications/pyproject-toml/), [package formats](https://packaging.python.org/en/latest/discussions/package-formats/). Оба файла и парные MCQ написаны; structure/pre-check PASS, независимая содержательная приёмка MCQ ожидается. |
| P2 · `NEW_TOPIC` · ПРОВЕРЕНО · З | `programming-languages/python/python-testing-interview.md` отсутствует; `testing/property-based-testing-interview.md` Q9 охватывает только Hypothesis, а `cheatsheets/testing/unit-testing/pytest/` вне interview. Нет Python-маршрута по fixtures, параметризации, mock и async-тестам. | [pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html), [Python unittest](https://docs.python.org/3/library/unittest.html). Сначала определить Q без повторения общих принципов тестирования. |
| P2 · `NEW_TOPIC` · ПРОВЕРЕНО · З | `frameworks/python/fastapi-interview.md` отсутствует; [Python frameworks overview](cheatsheets/frameworks/python-frameworks/python-frameworks-overview.md) вне interview, а `frameworks/spring/spring-rest-client-interview.md` Q1 даёт лишь HTTP-смежность. Нет FastAPI-вопросов о dependency lifecycle, валидации и lifespan. | [FastAPI dependencies](https://fastapi.tiangolo.com/tutorial/dependencies/), [lifespan](https://fastapi.tiangolo.com/advanced/events/). Будущие Q: ASGI/`async def` → dependencies/yield → модели/ошибки → lifespan/тесты; не копировать Python concurrency. |

### Пакет 3: базы данных и другие серверные темы

| Приоритет / исход / уровень | Ближайший файл и Q; пробел | Источник и следующий шаг |
|---|---|---|
| P2 · `NEW_TOPIC` · ПРОВЕРЕНО · З | `databases/mysql-interview.md` отсутствует; `database-transactions-interview.md` Q9 — об изоляции, `database-replication-interview.md` Q15/Q18 — о репликации, `sql-interview.md` Q16/Q45 — общий SQL/JDBC. Нет MySQL-маршрута по InnoDB, плану/индексу, charset/collation и сбоям соединения. | [MySQL InnoDB](https://dev.mysql.com/doc/refman/8.4/en/innodb-introduction.html), [EXPLAIN](https://dev.mysql.com/doc/refman/8.4/en/using-explain.html), [charset соединения](https://dev.mysql.com/doc/refman/8.4/en/charset-connection.html), [Connector/J troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html); [Baeldung 22.07](https://www.baeldung.com/java-mysql-sqlexception-incorrect-string-value) дал пример ошибки. Будущие Q: InnoDB/изоляция → индекс/план → `utf8mb4` → отличить сервер/сеть/пул при `Communications link failure`. |
| P2 · `NEW_TOPIC` · ПРОВЕРЕНО · З | `databases/jdbc-connection-pool-interview.md` отсутствует; `sql-interview.md` Q45 сравнивает JDBC/JPA, `database-transactions-interview.md` Q33–Q34 — связь пула и транзакции, `spring-data-jdbc-interview.md` Q1/Q16 — уровень выше JDBC. Нет цельного resource lifecycle и диагностики пула; [обычный JDBC-справочник](cheatsheets/languages/java/java-jdbc.md) вне interview. | [JDBC `DataSource`](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/javax/sql/DataSource.html), [HikariCP settings](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby), [MySQL troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html). Будущие Q: `DataSource`/connection → autocommit/transaction → statement/result set/streaming → размер пула, `maxLifetime`, `keepaliveTime`, stale connection. |
| P2 · `ADD_Q` · ПРОВЕРЕНО · З | `frameworks/spring/spring-data-jdbc-interview.md` Q13–Q16 описывают репозитории, запросы и транзакции; `databases/sql-interview.md` Q16 — SQL upsert. Нет вопроса о `JdbcAggregateTemplate.upsert` и отличии от `save`. | [Spring Data 2026.0 GA, 09.06](https://spring.io/blog/2026/06/09/spring-data-2026-0-0-generally-available/), [reference 4.1](https://docs.spring.io/spring-data/relational/reference/jdbc/entity-persistence.html). Возможный заголовок: «Когда `JdbcAggregateTemplate.upsert` заменяет `save` и какие у него ограничения?»; затронуть single-statement, обязательный ID, диалекты и границу aggregate. Сначала точечно прочитать ответы Q13–Q16 и SQL Q16. |
| P3 · `DEEPEN` · НАПИСАНО · О | `messaging/rabbitmq-interview.md` Q19/Q24/Q25/Q30: прежний список DLX пропускал delivery-limit, сравнение quorum/classic устарело; отдельный Q не нужен. | [RabbitMQ quorum docs 4.3](https://www.rabbitmq.com/docs/quorum-queues), [DLX](https://www.rabbitmq.com/docs/dlx). Исправлены Q19/Q24/Q25/Q30 и MCQ B1 для Q19/Q30: default 20 с 4.0, `nack` не увеличивает delivery-count с 4.3, strict priority доступен с 4.3, причины DLX и `x-death`. Structure/parity PASS, независимая приёмка ожидается. |
| P3 · `DEEPEN` · НАПИСАНО · О | `messaging/kafka-interview.md` Q15/Q16/Q45: rebalance и commit были, но без `CommitFailedException`; Q16 ошибочно считал auto-commit безусловным at-most-once. | [KafkaConsumer API 4.3.1](https://kafka.apache.org/43/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html), [CommitFailedException](https://kafka.apache.org/43/javadoc/org/apache/kafka/clients/consumer/CommitFailedException.html). Исправлены Q16 и MCQ B0, добавлен B1; structure/parity PASS, blind review ожидается. |

**Отрицательные решения:** новый Go HTTP-файл отклонён по `go-stdlib-interview.md`
Q1–Q5 и `go-testing-interview.md` Q14–Q15; отдельный profiling-файл — по
`go-memory-gc-interview.md` Q15–Q16 и `go-concurrency-interview.md` Q32.
Django/Flask вне текущего приоритета до подтверждения профиля вакансий;
FastAPI выбран как один Python backend-фреймворк. Rust, JavaScript, Node.js,
TypeScript и фронтенд исключены по явному запросу пользователя.
P3 · `OUT_OF_SCOPE` · З: [Go Blog, 02.10, `simd/archsimd`](https://go.dev/blog/archsimd)
проверен относительно `go-memory-gc-interview.md` Q26 (оптимизация) и
`go-stdlib-interview.md` Q30 (новый stdlib API). Экспериментальный API под
`GOEXPERIMENT=simd` и аппаратно специфичные intrinsics не дают типового
Middle/Senior backend-вопроса; отдельную шпаргалку сейчас не предлагать.

**Источник полугодового среза 05.04—05.10:** повторно открыт [архив Baeldung](https://www.baeldung.com/full_archive)
(после предыдущего снимка за октябрь виден только дайджест 02.10), а также
ленты [Go Blog](https://go.dev/blog/), [Python Insider](https://blog.python.org/),
[Spring Blog](https://spring.io/blog/), [PostgreSQL news](https://www.postgresql.org/about/newsarchive/pgsql/)
и [Kafka releases](https://kafka.apache.org/blog/releases/). Точечно проверены
публикации Go 19.08/26.08, 02.09 и 02.10, Python beta 23.06 и RC3 02.10, Spring
Data 09.06 и Boot 10.06, PostgreSQL beta 4 24.09. Это выборка официальных
источников, **не** полный обзор всех постов. [Python 3.15 RC3](https://www.python.org/downloads/release/python-3150rc3/)
на 05.10 ещё не GA (финал запланирован на 09.10), [PostgreSQL 19 beta 4](https://www.postgresql.org/about/news/postgresql-19-beta-4-released-3386/)
тоже не GA; их пункты в старой таблице остаются гипотезами до релиза.

## Как считать пробелом внутри уже существующей темы

Сначала искать отсутствие **целого файла-темы** по каталогу выше. Для уже
существующей темы сверять **все заголовки Q** и соседние файлы. Текст ответа
открывать лишь когда заголовок не позволяет понять, раскрыт ли аспект. Записывать
один из исходов:

1. `COVERED` — вопрос уже отвечает на задачу; указать `файл#Q`.
2. `DEEPEN` — вопрос есть, но ему недостаёт механизма, примера, ограничения или
   практического выбора; улучшить существующий ответ.
3. `PROMOTE` — понятие упомянуто внутри ответа, но заслуживает самостоятельного
   проверяемого вопроса; сначала проверить, не достаточно ли подзаголовка.
4. `ADD_Q` — отдельный вопрос даёт новую компетенцию и не дублирует соседний.
5. `NEW_TOPIC` — самостоятельная тема с несколькими связанными вопросами;
   новый файл оправдан только после проверки соседних шпаргалок.
6. `OUT_OF_SCOPE` — редкая или несоответствующая профилю Middle/Senior backend тема;
   зафиксировать причину, чтобы не возвращаться к ней циклически.

Вопросы отбирать по пользе на интервью Middle/Senior backend с учётом
конкретного языка: механизм, применение, сбои, ограничения, стоимость и выбор
между подходами. Количество Q не
является целью. Версионные утверждения снабжать конкретной версией и датой проверки.

## Новый источник для System Design: `system-design-notes`

Добавлено **05.10.2026** по [репозиторию `liquidslr/system-design-notes`](https://github.com/liquidslr/system-design-notes).
Это конспект 28 глав по книгам *System Design Interview* (Vol. 1–2), который
сам автор помечает как незавершённый. Просмотрены оглавление репозитория и
заголовки 14 глав, выбранных для проверки пробелов; сопоставлены названия
наших прежних 22 файлов `system-design/` и близкие Q. Статус остальных строк
ниже — **`З`**, кроме точечно прочитанного `system-design-interview.md` Q10;
первый кандидат проверен и написан отдельно. Это не проверка
правильности всех ответов и не готовое задание на авторинг каждого файла.
Использовать конспект для постановки задач, а факты сверять по первичным
источникам. Текст и иллюстрации чужого конспекта не переносить в шпаргалки.

**Уже покрыто отдельным файлом или маршрутом Q:** главы 1–6 (масштабирование,
оценки, ход интервью, rate limiter, consistent hashing, KV-store), 8–9
(URL shortener, crawler), 11–15 (лента, чат, autocomplete, YouTube,
Dropbox/Google Drive), 18 (карты) и 26 (платёжная система). Примеры точных
пересечений: `system-design-interview.md` Q1–Q6/Q31, Q15;
`design-key-value-store-interview.md` Q5–Q8;
`design-payment-system-interview.md` Q1–Q30. Новые файлы с теми же названиями
не нужны только ради переноса структуры источника.

| Приоритет / статус | Глава источника и решение | Существующее покрытие и граница будущей шпаргалки | Черновик проверяемых вопросов |
|---|---|---|---|
| P2 · `NEW_TOPIC`, выполнено · О | 7. Distributed Unique ID Generator → `system-design/design-unique-id-generator-interview.md` Q1–Q4 | `system-design-interview.md` Q7, `design-url-shortener-interview.md` Q19 и SQL Q13 не раскрывали выбор генератора и сбои вне URL-кода. Q19 точечно исправлен. | Область уникальности и порядок; sequence/ticket/ranges; clock rollback/worker collision; UUIDv4/v7/Snowflake. Новая пара structure/pre-check PASS, старый URL Shortener full pre-check FAIL, blind review нет. |
| P2 · `NEW_TOPIC`, выполнено · О | 10. Notification System → `system-design/design-notification-system-interview.md` Q1–Q4 | `system-design-interview.md` Q10 уже даёт архитектуру каналов, preferences, очередей и retry; новый файл раскрывает четыре независимых эксплуатационных сценария. | Принятие vs доставка; предел идемпотентности внешнего вызова; повторная проверка правил/TTL; классификация ошибок. FCM/APNs/SES/AWS сверены, 4 MCQ structure/pre-check PASS, blind review ожидается. |
| P2 · `NEW_TOPIC` отклонён, `DEEPEN`/`ADD_Q` выполнены · О | 19. Distributed Message Queue → RabbitMQ Q40–Q42, Kafka Q42, сравнение брокеров Q15, event-driven Q3 | Kafka Q6–Q24 и RabbitMQ Q18–Q30 уже покрывают проектные механизмы; отдельная шпаргалка стала бы повтором. Обнаружен реальный пробел: сравнения отрицали RabbitMQ Streams и replay. | По официальным RabbitMQ 4.3/Kafka 4.3 источникам исправлены queue vs log, offset/retention и super stream. 4 JSON структуры PASS, локальные Q без hard-сигналов; full pre-check старых файлов FAIL, blind review ожидается. |
| P2 · `NEW_TOPIC`, выполнено · О | 22. Hotel Reservation System → `system-design/design-reservation-system-interview.md` Q1–Q4 | `design-ecommerce-delivery-interview.md` Q9 покрывает резерв товара с TTL, `design-payment-system-interview.md` Q4/Q7/Q17 — деньги, `database-transactions-interview.md` Q14 — блокировки. Не было интервала ночей и назначения одной комнаты. | Q1: остаток по ночам и непрерывное назначение; Q2: атомарный резерв всего интервала; Q3: гонка TTL/оплаты; Q4: координация с PSP и отмена. PostgreSQL 18 и Stripe сверены, MCQ structure/parity/pre-check PASS, blind review ожидается. |
| P2 · `NEW_TOPIC`, выполнено · О | 24. S3-like Object Storage → `system-design/design-object-storage-interview.md` Q1–Q4 | `design-dropbox-interview.md` Q5–Q7/Q18 уже покрывает CAS, metadata/blob и Magic Pocket; `data-lake-lakehouse-interview.md` Q5 — сравнение с HDFS; `cloud/aws-interview.md` Q9/Q12 — обзор S3. Не было сквозного контракта объектного API и multipart. | Q1: PUT/GET/LIST, условная перезапись и версии; Q2: публикация metadata после bytes; Q3: multipart, checksum и abort; Q4: домены отказа и repair. AWS S3 и Ceph Squid сверены, MCQ structure/parity/pre-check PASS, blind review ожидается. |
| P2 · `NEW_TOPIC`, выполнено · О | 25. Real-time Gaming Leaderboard → `system-design/design-leaderboard-interview.md` Q1–Q5 | `databases/redis-interview.md` Q2/Q11 покрывает Sorted Set, `stream-processing-interview.md` Q5–Q10 — опоздавшие события; не было продукта рейтинга с правилами мест и сезонов. | Q1: top-N/rank и источник истины; Q2: равные очки; Q3: повторы и поздние матчи; Q4: сезоны; Q5: шардирование и глобальный rank. Redis/Flink сверены, MCQ structure/parity/pre-check PASS, blind review ожидается. |
| P3 · `NEW_TOPIC`, выполнено · О | 20. Metrics Monitoring and Alerting System → `system-design/design-metrics-platform-interview.md` Q1–Q4 | `system-design-interview.md` Q23 и `monitoring/prometheus-grafana-interview.md` Q3/Q16/Q19–Q24/Q35–Q37 покрывают инструменты, cardinality и alerting одного стека; отсутствовала общая платформа для нескольких tenant. | Q1: доверенный tenant ID и лимиты ingest; Q2: raw/long-term/downsampling; Q3: изоляция тяжёлых запросов; Q4: правила и отказ ruler. Prometheus/Mimir/Thanos сверены, MCQ structure/parity/pre-check PASS, blind review ожидается. |
| P3 · `NEW_TOPIC`, выполнено · О | 21. Ad Click Event Aggregation → `system-design/design-event-aggregation-interview.md` Q1–Q4 | `data-engineering/stream-processing-interview.md` Q5–Q10/Q14–Q16 покрывает механику окон и повторов; `design-url-shortener-interview.md` Q14 — счётчик переходов; `message-brokers-comparison-interview.md` Q25 — выбор брокера. Рекламная идентичность, атрибуция и расчёт не раскрыты. | Q1: event ID и повторная доставка против недействительного трафика; Q2: provisional окна и коррекции; Q3: поздняя конверсия/окно атрибуции; Q4: settled начисления и сверка. Сверены [Flink 2.0](https://nightlies.apache.org/flink/flink-docs-release-2.0/docs/learn-flink/streaming_analytics/), [Google Ads API](https://developers.google.com/google-ads/api/docs/conversions/legacy_oci_guide) и [invalid traffic](https://support.google.com/google-ads/answer/11182074?hl=en); MCQ structure/parity/pre-check PASS, авторская точечная проверка выполнена, независимый blind review ожидается. |
| P3 · `NEW_TOPIC`, выполнено · О | 23. Distributed Email Service → `system-design/design-email-service-interview.md` Q1–Q4 | `system-design-interview.md` Q10 уже задаёт общую очередь email; `design-notification-system-interview.md` Q1–Q4 покрывает статусы, дубли и общую классификацию ошибок. Не было почтовых квот, suppression, доменной аутентификации и репутации нескольких отправителей. | Q1: квоты и приоритеты; Q2: bounce/complaint и suppression; Q3: SPF/DKIM/DMARC и доставляемость; Q4: tenant/IP-репутация. Сверены [SES quotas](https://docs.aws.amazon.com/ses/latest/dg/manage-sending-quotas.html), [suppression](https://docs.aws.amazon.com/ses/latest/dg/sending-email-suppression-list.html), [tenants](https://docs.aws.amazon.com/ses/latest/dg/tenants.html) и [Gmail guidelines](https://support.google.com/mail/answer/81126?hl=en); MCQ structure/parity/pre-check PASS, авторская точечная проверка выполнена, независимый blind review ожидается. |
| P3 · `NEW_TOPIC`, выполнено · О | 27. Digital Wallet → `system-design/design-digital-wallet-interview.md` Q1–Q4 | `design-payment-system-interview.md` Q5/Q8/Q22, `database-transactions-interview.md` Q10/Q14 и `cqrs-event-sourcing-interview.md` Q24/Q30–Q31 сверены точечно. Они не объясняли внутренний перевод в одной валюте как отдельный контракт. | Q1: атомарный перевод; Q2: повтор после потерянного ответа; Q3: pending и доступный баланс; Q4: исправление и сверка. Сверены [TigerBeetle Transfer](https://docs.tigerbeetle.com/reference/transfer/), [reliable submission](https://docs.tigerbeetle.com/coding/reliable-transaction-submission/), [two-phase transfers](https://docs.tigerbeetle.com/coding/two-phase-transfers/), [correcting transfers](https://docs.tigerbeetle.com/coding/recipes/correcting-transfers/) и [PostgreSQL 18](https://www.postgresql.org/docs/18/transaction-iso.html). JSON structure/parity/pre-check PASS; авторская проверка Q1–Q4 выполнена, независимый blind review ожидается. Коммит `3a300d20`. |
| P3 · КАНДИДАТ `NEW_TOPIC` | 28. Stock Exchange → `system-design/design-exchange-interview.md` | Финансовый домен уже есть в платёжной системе, но order book и matching engine — отдельная задача. Подтвердить интерес к узкой предметной области до написания. | Как упорядочивать команды и обеспечивать детерминированное сопоставление заявок? Как разделить matching, рыночные данные и отчётность? |
| P3 · `DEEPEN`, выполнено · О | 16–17. Proximity Service / Nearby Friends | `design-uber-interview.md` Q6/Q7/Q12–Q14 уже покрывает поток координат и геоиндекс, `design-google-maps-interview.md` Q27 — совместный доступ, Q19 — POI. Два новых файла повторили бы эти ответы. | Uber Q6/Q7 исправлены: PostgreSQL/PostGIS умеет индексированный `ST_DWithin`, выбор Redis/H3 зависит от нагрузки; удалено категоричное «SQL не годится». Maps Q27 углублён: `GEOSEARCH` даёт кандидатов, право дружбы/согласие/отзыв проверяются на сервере для каждого чтения, большой граф проверяется по кандидатам, `COUNT ANY` до авторизации не равен лимиту видимых друзей. Сверены [PostGIS ST_DWithin](https://postgis.net/docs/ST_DWithin.html), [Redis GEOSEARCH](https://redis.io/docs/latest/commands/geosearch/), [OWASP Authorization](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html). JSON Q6/Q7/Q27 синхронизированы; обе пары structure/parity/pre-check PASS. Авторская точечная проверка выполнена, blind review ожидается. |

Перед созданием каждого `NEW_TOPIC` прочитать полный ближайший файл,
проверить заголовки Q соседних разделов, выбрать первичные источники и
зафиксировать независимый маршрут нескольких вопросов. Если после проверки
остаётся один полезный аспект, заменить новый файл на `DEEPEN`/`ADD_Q`.
При написании синхронизировать production MCQ и навигацию по правилам этого
плана; `З` здесь не означает готовности фактов или MCQ к приёмке.

## Вторая очередь: вопросы внутри существующих тем

Это очередь возможных вопросов, а не задание немедленно их добавлять. К ним
вернуться после приоритетных отсутствующих файлов. Перед будущей правкой читать
полный целевой файл и сверять вопрос с официальной документацией. `P2` в этой
таблице — подтверждённая по заголовкам граница JDBC, `P3` — высокая польза
во второй очереди, `P4` — полезно после более глубокой проверки.

| Приоритет | Статус | Целевой файл и имеющееся покрытие | Проверить и сформулировать | Возможный исход |
|---|---|---|---|---|
| P3 | `ADD_Q` + исправление выполнены · О | `programming-languages/java/java-completable-future-interview.md` Q11/Q14, рядом Q13 об ограничениях | Q11: ошибочный Java 8 `applyToEither` не срабатывал на исключительном таймере; заменён отдельным result-future и проверен компиляцией/запуском под `--release 8`. Q14: `cancel(true)` у `CompletableFuture` не прерывает исходную задачу, для неё нужен отдельный handle/клиентская отмена. [Java 25 CompletableFuture](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CompletableFuture.html), [Future](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/Future.html), [Java 8 API](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html). | Q11 JSON исправлен, Q14 добавлен; структура/parity PASS, полный pre-check старого файла FAIL на прежних Q2/Q3/Q4/Q6/Q8/Q10; новые Q11/Q14 без hard-сигналов, blind review ожидается. |
| P3 | `ADD_Q` выполнено · О | `databases/database-transactions-interview.md` Q43; Q6–Q12 уже объясняли изоляцию/MVCC, `postgresql-interview.md` упоминал `SQLSTATE 40001` без полного сценария | Write skew на двух дежурных и повтор всей транзакции с повторной проверкой инварианта по [PostgreSQL 18 Transaction Isolation](https://www.postgresql.org/docs/18/transaction-iso.html) и [Serialization Failure Handling](https://www.postgresql.org/docs/18/mvcc-serialization-failure-handling.html). | Markdown/JSON синхронизированы; structure/parity PASS, полный pre-check файла FAIL из-за прежних Q15/Q22/Q23, новый Q43 без hard-сигналов. Blind review Q43 ожидается. |
| P3 | `ADD_Q` + исправление выполнены · О | `devops/kubernetes-interview.md` Q39/Q46; рядом Q14 о rollout и Q42 о завершении Pod | Q39 неверно называл PDB гарантией минимального числа доступных Pod; в MCQ rolling update ошибочно отнесён к его защите. Q46 показывает `drain` через Eviction API, ожидание бюджета и границы PDB по [Kubernetes Disruptions](https://kubernetes.io/docs/concepts/workloads/pods/disruptions/) и [Safely Drain a Node](https://kubernetes.io/docs/tasks/administer-cluster/safely-drain-node/). | Теория и JSON Q39/Q46 синхронизированы, structure PASS; полный parity/pre-check старого файла FAIL на многочисленных прежних блоках, freshness sidecar для всего файла TODO. Новые Q требуют независимого blind review. |
| P3 | `ADD_Q` + исправление выполнены · О | `devops/kubernetes-interview.md` Q22/Q47; Q22 об Ingress только кратко упоминал Gateway API | Q22 неверно описывал Gateway API как встроенные core-ресурсы с обязательной TCP/UDP-поддержкой. Q47 объясняет выбор API, роли `GatewayClass → Gateway → HTTPRoute`, CRD/контроллер и проверку conformance/поддержки функций по [Kubernetes Gateway API](https://kubernetes.io/docs/concepts/services-networking/gateway/) и [Implementer's Guide](https://gateway-api.sigs.k8s.io/guides/implementers-guide/). | Теория/JSON Q22/Q47 синхронизированы; structure PASS, локально без hard-сигналов, полный parity/pre-check старого файла FAIL. Blind review Q22/Q47 ожидается. |
| P3 | `ADD_Q` + уточнение выполнены · О | `frameworks/spring/spring-rest-client-interview.md` Q14; рядом Q5/Q12 и `resilience4j-interview.md` Q9/Q14 | Q12 объяснял только фазовые таймауты. Q14 добавляет единый бюджет на ожидание пула, соединение, ответ и повторы; ограничение retry по семантике операции; метрики и trace context через внедрённый Boot builder. Q12 уточнён по наблюдаемости. Сверены [Apache HttpClient 5.6 RequestConfig](https://hc.apache.org/httpcomponents-client-5.6.x/current/httpclient5/apidocs/org/apache/hc/client5/http/config/RequestConfig.Builder.html), [Spring Boot Metrics](https://docs.spring.io/spring-boot/reference/actuator/metrics.html), [Tracing](https://docs.spring.io/spring-boot/reference/actuator/tracing.html), [Resilience4j Retry](https://resilience4j.readme.io/docs/retry), [RFC 9110](https://www.rfc-editor.org/rfc/rfc9110.html#section-9.2.2). | Теория/JSON Q12/Q14 синхронизированы; структура/parity PASS, полный pre-check после второго пакета 07.10 PASS (hard=0); Q14 без hard-сигналов. Авторская точечная проверка выполнена, blind review ожидается. |
| P3 | `ADD_Q` + исправления выполнены · О | `security/authentication-authorization-patterns-interview.md` Q24/Q46; сосед `frameworks/spring/spring-security-interview.md` Q44 | Q24 смешивал отсутствие пароля со стойкостью к фишингу; Q44 ошибочно утверждал, что ключ никогда не покидает устройство, и не называл обязательный модуль. Q46 добавляет регистрацию/вход, challenge, origin/RP ID, подпись и восстановление. Сверены [W3C WebAuthn Level 3](https://www.w3.org/TR/webauthn-3/), [FIDO Alliance Recovery](https://fidoalliance.org/white-paper-displace-password-otp-authentication-with-passkeys/), [Spring Security Passkeys](https://docs.spring.io/spring-security/reference/servlet/authentication/passkeys.html), [CVE-2026-47841](https://spring.io/security/cve-2026-47841/). | Q24/Q46 и Spring Q44 JSON синхронизированы; структура/parity обоих PASS. Полный pre-check общей темы PASS, Spring Security FAIL из-за прежнего Q30 drift и старых блоков, Q44 без hard-сигнала. Авторская точечная проверка выполнена, blind review ожидается; freshness общей темы сброшен в TODO. |
| P2 | `NEW_TOPIC` выполнено · О | `databases/jdbc-connection-pool-interview.md` Q1–Q6; рядом `sql-interview.md` Q45 и `database-transactions-interview.md` Q33–Q34 | **JDBC и пулы**: `DataSource`, `PreparedStatement`, `ResultSet`, auto-commit, batch/streaming, жизненный цикл и диагностика. Граница с MySQL и Spring Data JDBC записана в пакете 05.10. | Шпаргалка и парный JSON уже созданы; 6 Q, структура/parity PASS, независимая MCQ-приёмка ожидается. |
| P4 | `DEEPEN` выполнено · О | `monitoring/metrics-tracing-interview.md` Q40 уже содержит Baggage; `opentelemetry-interview.md` Q14 объясняет только trace context. Указанный прежде Q37 — tail sampling, сосед был записан ошибочно. | Q40 пример читал `Context.current()` после записи в отдельный контекст без `Scope` и передавал `userId` в заголовке. Исправлены `makeCurrent()`, несекретный пример, граница доверия, отсутствие встроенной целостности/автоматических span attributes и лимиты [W3C Baggage](https://www.w3.org/TR/baggage/#limits); сверены [OpenTelemetry Baggage](https://opentelemetry.io/docs/concepts/signals/baggage/) и [Java API](https://opentelemetry.io/docs/languages/java/api/). | JSON Q40 edge cases синхронизирован, структура PASS. Полный parity/pre-check старого файла FAIL (в том числе прежний скелет Q40); авторская точечная проверка Q40 выполнена, blind review и полная свежесть файла ожидаются. Отдельный Q не нужен. |
| P4 | `DEEPEN` выполнено · О | `frameworks/spring/spring-testing-interview.md` Q9/Q10 названы `@MockBean`/`@SpyBean` и уже отмечали замену в Boot 3.4/4 | Уточнены `@MockitoBean` `REPLACE_OR_CREATE`/`enforceOverride`, qualifier и кеш, отсутствие AOP-прокси mock; `@MockitoSpyBean` `WRAP`, необходимость существующего бина и безопасный `doReturn`/`doThrow`. Исправлено описание `@InjectMocks` и обозначены legacy-примеры. Сверены [Spring Framework bean overrides](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-mockitobean.html), [Boot 3.4 API](https://docs.spring.io/spring-boot/3.4/api/java/org/springframework/boot/test/mock/mockito/package-summary.html), [Mockito InjectMocks](https://javadoc.io/static/org.mockito/mockito-core/4.5.0/org/mockito/InjectMocks.html). | JSON Q9/Q10 синхронизирован; структура/parity PASS, полный pre-check старого файла FAIL из-за прежнего Q16 и других предупреждений. Нового Q не требуется; авторская точечная проверка выполнена, blind review ожидается. |
| P4 | ВЫПОЛНЕНО · О | `messaging/rabbitmq-interview.md`, Q19/Q24/Q25/Q30 | Пробел закрыт в существующих Q: DLX/TTL retry, delivery limit и 4.3 `nack` разграничены. | `DEEPEN`; отдельный Q не нужен, MCQ ждут независимой приёмки |
| P4 | КАНДИДАТ | `ai-ml/ai-application-architecture-interview.md` и `llm-integration-patterns-interview.md` покрывают rate limit/retry | Есть ли отдельный вопрос о **планировании ёмкости** LLM-сервиса: очередь, concurrency cap, лимиты токенов и деградация при перегрузке? Не дублировать Q по стоимости и rate limit. | `ADD_Q` только если новый операционный сценарий подтверждён |

### Очистка перед расширением

- Сравнить три пары точных дублей из `inventory.tsv`. Разные по смыслу ответы
  можно оставить, уточнив заголовки; при совпадении — решить судьбу стабильных
  `Q<N>` и входящих ссылок отдельно. Простое удаление или перенумерация запрещены.
- Проверить заголовки, которые формулируют **один и тот же вопрос разными словами**
  внутри файла и соседних тем (например, Java Collections Q20/Q46, Java Modules
  Q14–Q16/Q31–Q33). Это кандидаты, а не автоматически доказанные дубли.
- Для замечаний о читаемости (например, необычная русская лексика в Terraform)
  заводить отдельную редакторскую работу; не маскировать её добавлением Q.

## Учёт решений и порядок проверки каталога тем

`inventory.tsv` — снимок **всех** файлов, а не таблица содержательной приёмки.
Для каждой группы сравнить наличие самостоятельных тем, затем заголовки Q внутри
существующих файлов. На новый файл записывать границу темы, соседние файлы,
примерный набор вопросов, первичный источник и причину отдельного файла.
Для пробела внутри файла сохранять `файл#Q` и исход из шести вариантов выше.
Для каждой строки очереди будущий исполнитель отмечает отдельно:

- `КАНДИДАТ` — предложено по карте тем, без содержательной проверки;
- `ПРОВЕРЕНО` — сверены соседние файлы и релевантные заголовки Q, записано решение;
- `ОТКЛОНЕНО` — пробел уже закрыт или не соответствует профилю, указано где/почему;
- `ГОТОВО К НАПИСАНИЮ` — есть граница, список будущих Q, источники и отсутствие
  подтверждённого дубля;
- `НАПИСАНО` — Markdown создан, но качество ответа и MCQ ещё проверяются отдельно.

Ни число файлов в инвентаре, ни зелёный структурный валидатор не означают
`ПРОВЕРЕНО` или `НАПИСАНО`. Решение и доказательство оставлять рядом с кандидатом
в этом плане, а не в памяти чата. Указатель на следующую группу обновлять ниже.
Удобно обрабатывать темы небольшими группами:

1. **Бэкенд-языки и их экосистемы:** Python → Go → Kotlin/Scala/Java. Сначала
   проверить отсутствие файла, затем Q.
2. **Бэкенд и данные:** SQL/другие СУБД → ORM → фреймворки → API → messaging.
3. **Эксплуатация:** DevOps/cloud/CI/CD → мониторинг/логирование → performance.
4. **Архитектура и практика:** security → distributed systems → testing →
   code quality/design patterns → system design → AI/ML → behavioral/leadership.
   Пропускать фронтенд и явно исключённые языки; остальные категории оценивать
   по их пользе для бэкенд-собеседований.

После каждой группы обновлять этот план: написанные Q и MCQ, подтверждённые
пробелы, отклонённые гипотезы и точное следующее место. Для новых ответов
проверять факты по официальным источникам; уровень независимой приёмки MCQ
фиксировать отдельно от структуры и детерминированных проверок.

## Источники и границы текущей проверки

- Репозиторий уже ссылается на Baeldung в **196** Markdown-файлах, включая
  [Java interview questions](https://www.baeldung.com/java-interview-questions),
  [Spring interview questions](https://www.baeldung.com/spring-interview-questions),
  [CompletableFuture](https://www.baeldung.com/java-completablefuture) и статьи
  по SQL/Kubernetes. Подборку Java-вопросов Baeldung удалось открыть при
  дополнительной проверке; это источник идей, а не исчерпывающий каталог языков.
  [Переданный чат «Улучшение MCQ»](https://chatgpt.com/s/cx_6abe50333f00819191e0e08d7c49cddc)
  прочитан в браузере; его переносимые принципы учёта изложены выше.
- Для фактов использовать первичные источники из соответствующих файлов:
  [Java API](https://docs.oracle.com/en/java/docs/api/),
  [Spring](https://docs.spring.io/spring-framework/reference/),
  [PostgreSQL](https://www.postgresql.org/docs/),
  [Kubernetes](https://kubernetes.io/docs/),
  [RabbitMQ](https://www.rabbitmq.com/docs),
  [OpenTelemetry](https://opentelemetry.io/docs/) и спецификации OAuth/WebAuthn.
  Baeldung использовать для обнаружения вариантов вопросов и примеров, а
  версионные утверждения сверять с первоисточником.
- Внешнее исследование конкретной темы завершать **до написания новых фактических
  ответов**. Выбор новых файлов основан на локальном каталоге и приведённых
  первичных источниках; не заявлять проверку статей, которые не были открыты.

## Следующий пакет для другой LLM

**Checkpoint 05.10.2026:** P0 исправлен, включая версионные ответы MCP
Q6/Q8/Q13/Q14/Q28–Q30; P1 Python core/concurrency написан,
Go `database/sql` углублён в stdlib Q22–Q25. Из P2 написаны Python testing,
FastAPI, MySQL и JDBC/пулы. Следующая небольшая группа: RabbitMQ
Q19/Q24/Q25/Q30, Kafka Q16, Hibernate JPQL/Criteria Q6 и Spring Data JPA
Q4 углублены после
точечной проверки соседних Q и официальных документов; отдельные файлы и
новые номера Q для этих четырёх тем не понадобились. После них добавлен
Actuator Q44 о диагностике старта, затем Go stdlib Q31 о JSON v2 и
Go concurrency Q32 о профиле `goroutineleak`.
Состав: 325 файлов, 9 865 Q; инвентарь обновлён на два Q, новых файлов нет.
Все новые/изменённые MCQ ещё ждут независимой содержательной приёмки.
Для MCP структурная проверка PASS, parity всего старого файла FAIL; до
исправления остальных блоков нельзя отмечать MCQ темы как принятый.
Структурные и детерминированные проверки не заменяют её. RabbitMQ: JSON
41 Q / 43 блока, structure/parity PASS. Kafka: 50 Q / 51 блок,
structure/parity PASS. Hibernate: 16 Q / 17 блоков, structure PASS, parity
**FAIL из-за старых блоков всего файла**; добавленный Q6 B1 точечно проверен
по теории и официальному Javadoc, но независимого blind review нет.
Spring Data JPA: 43 Q / 44 блока, structure PASS, parity **FAIL из-за
старого файла**; оба блока Q4 выровнены локально по содержанию и форме,
но независимой приёмки тоже нет.
Actuator: 44 Q / 45 блоков, structure/parity PASS; новый Q44 прошёл
авторскую точечную проверку по Spring Boot 4.1.1, blind review ожидается.
Полный MCQ pre-check остаётся FAIL на прежних Q15/Q18/Q40 (surface skeleton),
у нового Q44 этих сигналов нет.
Spring AI: Q3/Q7/Q15 точечно проверены по reference 2.0.1; 18 Q / 21 блок,
structure/parity PASS. Изменённые MCQ прочитаны автором, независимой
содержательной приёмки нет. Полный MCQ pre-check остаётся FAIL на прежних
Q1/Q6/Q8–Q10/Q14/Q16/Q17 (surface skeleton), локальные Q3/Q7/Q15 без
жёстких сигналов. Остальные Q файла не получили П.
Go stdlib: Q7–Q10 и Q31 точечно проверены по Go 1.27 release notes и
package docs; 31 Q / 32 блока, структура PASS, parity всего старого файла
FAIL (24/32 correct длиннее всех wrong; 25 блоков вне коридора длины).
Q10 и Q31 B0/B1 без жёстких локальных сигналов после правок; полного П и
blind review нет. Go 1.27 локально не установлен, код Q31 не выполнялся.
Go concurrency: Q32 и соседний Q33 точечно проверены по Go blog/release notes;
35 Q / 36 блоков, structure PASS, parity всего старого файла FAIL (34/36
correct самые длинные). Новый Q32 B1 и обновлённый B0 не имеют жёстких
локальных сигналов, blind review нет. Go 1.27 локально не установлен.
Полного аудита ответов существующих файлов не было. Параллельно появились
посторонние незавершённые правки `agentic-patterns` и новые MCQ run-файлы;
их не смешивать с этой группой и не переписывать их audit-state. Пользователь
поручил тематические коммиты, push не запрошен.

Контрольная точка Git: `26a86918` (новые и обновлённые шпаргалки),
`492eeeb8` (процесс MCQ), `1d925af5` (Spring Data JDBC Q17),
`32b72efd` (ранее существовавшие правки четырёх теоретических пар),
`e817fd82` (evidence и синхронизация MCQ), `24efdeb7` (RabbitMQ),
`c27a450d` (Kafka), `4d649610` (Hibernate `@Find`), `16bad0d0`
(Spring Data JPA AOT), `b938557c` (Actuator), `58aca34b` (Spring AI),
`6d69f1b3` (Go JSON v2), `e5618bb9` (Go goroutineleak).
После первых пяти рабочее дерево было чистым;
позднее возникли параллельные правки `agentic-patterns`. Push не выполнялся.
Предыдущая сводка `sync` до этих четырёх тем: 324 пары / 9 824 блока;
четыре темы добавили пять MCQ-блоков без новых Q, повторный полный `sync`
во время параллельных MCQ runs не выполнялся.
3 файла / 59 блоков с действующей полной приёмкой; `agentic-patterns` Q12
остаётся открытым finding, остальные новые MCQ из этого плана ждут blind review.

**Checkpoint 07.10.2026:** Digital Wallet Q1–Q4 добавлен отдельной темой и
закоммичен (`3a300d20`); граница с платежной системой, транзакциями и
event sourcing сверена по ответам. В существующую тему транзакций добавлен
Q43 о write skew и полном повторе после `40001` (коммит `1a892642`).
Java CompletableFuture Q11 исправлен, Q14 об отмене добавлен; парные MCQ и инвентарь
синхронизированы (коммит `3321e788`). Kubernetes Q39 исправлен, Q46 о
PDB/`drain` добавлен (коммит `c45d2be4`); Q22 уточнён по Gateway API,
Q47 о выборе и ролях добавлен (коммит `d95a6029`).
Spring Rest Client Q12 уточнён по наблюдаемости, Q14 добавлен для
общего бюджета исходящего вызова, повторов и Boot-инструментации.
Инвентарь теперь показывает 9 923 Q. Структура/parity JSON PASS;
полный pre-check всего файла PASS (hard=0) после двух пакетов 07.10;
новый Q14 без hard-сигналов. Независимая приёмка Q4/Q6/Q7/Q11/Q12/Q13/Q14 ожидается.
WebAuthn Q46 добавлен в общую тему; Q24 и Spring Security Q44 уточнены,
включая модуль, synced passkeys и границы CVE-2026-47841. JSON двух тем
синхронизированы и структурно проверены; parity PASS. Полный pre-check
Spring Security по-прежнему FAIL на прежних Q (включая Q30 drift);
независимая MCQ-приёмка Q24/Q46/Q44 ожидается. Полная проверка обоих
старых файлов не проводилась.
Metrics & Tracing Q40 углублён по Baggage: исправлен неактивный контекст,
убран `userId` из примера, добавлены границы размера и доверия. Парный JSON
Q40 обновлён; структура PASS, старые parity/pre-check всего файла FAIL.
Отдельного Q не потребовалось, полная приёмка не проводилась.
Spring Testing Q9/Q10 углублены для миграции на `@MockitoBean`/
`@MockitoSpyBean`, парный JSON обновлён. Структура/parity PASS,
полный pre-check старого файла FAIL на прежнем Q16; новое содержимое
авторски проверено, независимая приёмка ожидается.
Proximity Service и Nearby Friends проверены по ответам, а не только
заголовкам: `design-uber-interview.md` Q6/Q7 исправлены по PostGIS и
Redis, `design-google-maps-interview.md` Q27 углублён по правам доступа
и геокандидатам. Два новых файла не нужны. Обе пары JSON имеют 30 Q,
structure/parity/pre-check PASS; независимая приёмка Q6/Q7/Q27 ожидается.
Spring Framework Q10 углублён по фоновому созданию бинов с 6.2,
добавлен MCQ B1. 07.10 схема и B0 A исправлены по конструкторной DI.
Q1/Q2 исправлены по границе модулей Framework и проектов Boot/Security/Data,
Q9 MCQ выровнен по форме. Структура/parity PASS (41 Q/42 блока),
полный pre-check hard=0 с 23 предупреждениями.
Независимая приёмка Q1/Q2/Q9/Q10 B0/B1 ожидается. Инвентарь не менялся: состав
файлов и число Q остались 337/9 923.
Spring Batch Q6/Q28/Q38 повторно сверены с финальной документацией
Batch 5.0, 5.2, 6 и Boot 3.5/4.1. Исправлены граница появления
MongoDB/resourceless (5.2), смешение Boot/Batch и Q38 о менеджере
транзакций. В отдельном пакете Q39 уточнён по `gridSize`, Q42 — по
тестовому API Batch 6; старые MCQ Q2/Q26/Q27/Q39/Q42 отредактированы.
JSON содержит 43 Q/44 блока: structure/parity PASS, полный pre-check
hard=0, 17 предупреждений. Авторская точечная проверка выполнена,
независимая blind-приёмка и полный обзор старого файла ожидаются.
Для этих пакетов проведена авторская точечная проверка
по официальной документации, **полная содержательная проверка старых
файлов не проводилась**. Автоматические структура/parity Q43 PASS,
общий pre-check транзакционного JSON PASS после отдельной правки
Q15/Q22/Q23; у CompletableFuture он тоже PASS после правки Q2/Q3/Q4/Q6/Q8/Q10.
Полный parity/pre-check Kubernetes тоже FAIL на прежних блоках; его
fact-freshness sidecar остаётся TODO. Это не оценка новых Q43/Q14/Q46/Q47.
Независимая содержательная
приёмка новых MCQ ещё не проведена. Посторонние AI/ML и audit-файлы
в рабочем дереве оставлены за параллельной работой.

**Контрольная точка 07.10.2026 — Database Transactions Q15/Q22/Q23 · О.**
По [Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2),
[`OptimisticLockException`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/optimisticlockexception),
[Spring Retry 2.x](https://docs.spring.io/spring-retry/docs/current/apidocs/spring.retry/org/springframework/retry/annotation/Retryable.html),
[Spring rollback rules](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html)
и [`@Transactional`](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)
исправлены момент проверки версии и повтор всей операции в новой транзакции,
приоритет rollback-правил и общий режим Spring Framework 6.2+,
видимость методов class-based proxy с версии 6.0 и граница AspectJ.
Парные Q15/Q22/Q23 B0 переписаны с разбором A/B/C/D и sections;
structure/parity/pre-check всего JSON: PASS (43 Q/43 блока, hard=0,
два предупреждения). Это авторская точечная проверка, не полная проверка
старого файла и не независимый blind review. MCQ Q15/Q22/Q23 B0 и Q43 B0
ждут независимой приёмки. Состав шпаргалок и число Q не менялись,
`inventory.tsv` не обновлялся.

**Контрольная точка 07.10.2026 — Java CompletableFuture Q2/Q3/Q4/Q6/Q8/Q10 · О.**
По [Java SE 25 `CompletableFuture`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CompletableFuture.html),
[`CompletionStage`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CompletionStage.html)
и [`ForkJoinPool`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ForkJoinPool.html)
исправлены негарантированный поток для не-Async этапа, настраиваемый размер
общего пула, роль `thenCombine` без обещания параллельности, цепочки после
`thenAccept`/`thenRun`, момент завершения `allOf` при ошибке,
тип `exceptionally` и граница таймаута `join`/`get`. MCQ Q2 B0,
Q3 B0/B1, Q4 B0, Q6 B0, Q8 B1, Q10 B0 синхронизированы,
включая прежде истинный дистрактор Q6 C. Структура/parity/pre-check
всего JSON PASS (14 Q/16 блоков, hard=0, три предупреждения).
Авторская проверка A/B/C/D и sections выполнена для этих блоков;
полный обзор прежних Q и независимый blind review остаются открытыми.
Q8 B0 связан с изменённой теорией и также требует независимой проверки.
Состав файлов и число Q не менялись, инвентарь прежний.

**Контрольная точка 07.10.2026 — Spring Rest Client Q4/Q11 · О.**
По [Spring Framework 6.2 REST Clients](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html),
[`ConvertibleClientHttpResponse`](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/web/client/RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse.html)
и [Spring Boot 4 HTTP Service groups](https://docs.spring.io/spring-boot/4.0/reference/io/rest-client.html)
уточнены `retrieve()/exchange()`, тип ответа в лямбде `exchange()`,
`bodyTo()` и `Optional.ofNullable()`, а также граница между синхронной
сигнатурой интерфейса и реактивным `Mono/Flux`. Убрано ложное обобщение,
что в Boot нельзя задать base URL через свойства: для групп Boot 4.x есть
`spring.http.serviceclient.<group-name>.base-url`, а ручная сборка
использует builder клиента. MCQ Q4/Q11 B0 синхронизированы, A/B/C/D и
sections авторски вычитаны. Structure/parity JSON PASS (14 Q/14 блоков),
полный pre-check этой промежуточной версии FAIL: Q6/Q7/Q12/Q13, hard=4.
Независимая приёмка Q4/Q11 и Q12/Q14 ожидается; полный обзор старого
файла не выполнен. В fact-freshness sidecar добавлены точечные источники;
предыдущая дата общей проверки 02.09 сохранена, новый полный проход не заявлен.

**Контрольная точка 07.10.2026 — Spring Rest Client Q6/Q7/Q12/Q13 · О.**
По [Spring Framework 6.2 REST Clients](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html),
[синхронному WebClient](https://docs.spring.io/spring-framework/reference/6.2/web/webflux-webclient/client-synchronous.html),
[SimpleClientHttpRequestFactory](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/http/client/SimpleClientHttpRequestFactory.html)
и [Apache HttpClient 5.6](https://hc.apache.org/httpcomponents-client-5.6.x/connection-management.html)
уточнён Q7: модель исходящего вызова важнее названия серверного стека,
а `.block()` занимает вызывающий поток. Q6/Q12/Q13 точечно сверены:
`ParameterizedTypeReference`, таймауты на реализации request factory,
перенос конфигурации через `RestClient.create(RestTemplate)`.
MCQ Q6/Q7/Q12/Q13 B0 переработаны с проверкой A/B/C/D и sections;
структура/parity/pre-check всего JSON PASS (14 Q/14 блоков, hard=0,
четыре предупреждения). Это авторская точечная проверка, не полный обзор
старых Q и не независимая приёмка. Sidecar дополнен проверенными источниками,
дата прежнего полного прохода 02.09 сохранена. Состав шпаргалок и число Q
не менялись; инвентарь прежний.

**Контрольная точка 07.10.2026 — Spring Data JPA Q3 B0 и Q4 B1 · О.**
По [Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2),
[Hibernate HQL 7.0](https://docs.hibernate.org/orm/7.0/querylanguage/html_single/)
и [Spring Data JPA AOT 4.1.1](https://docs.spring.io/spring-data/jpa/reference/jpa/aot.html)
исправлен Q3: CTE и оконные функции отсутствуют в стандартном JPQL, но
доступны в Hibernate HQL как расширения с ограниченной переносимостью.
У Q4 уточнён стандартный in-memory путь сбора метаданных и возможность
включить `EntityManagerFactory`. Парные MCQ Q3 B0 и Q4 B1 сверены по
ответу, всем A/B/C/D и sections; версия/источники записаны в sidecar.
Структура JSON PASS (43 Q/44 блока), локальный pre-check обоих блоков
PASS без предупреждений; во всём старом файле остаётся 35 hard-сигналов,
parity всего файла FAIL. Это авторская точечная проверка, независимая
приёмка и полный содержательный обзор остаются открытыми. Состав и число
Q не менялись, инвентарь прежний.

**Контрольная точка 07.10.2026 — Spring Data JPA Q5/Q6/Q7 B0 · О.**
По [Spring Data JPA query methods](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html),
[return types](https://docs.spring.io/spring-data/jpa/reference/repositories/query-return-types-reference.html)
и [транзакциям](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html)
уточнены derived methods: `deleteBy` выбирает сущности и удаляет их
по одной с callbacks, а возвращённый `Stream<T>` надо читать и закрывать
в транзакции вызывающего сервиса. Убраны фиктивные JPQL-эквиваленты
`LIMIT`/bulk `DELETE`. Q6/Q7 теперь согласованы с Q3: Hibernate HQL
поддерживает CTE/оконные функции сверх стандарта JPQL; нативный SQL
можно задать через `@NativeQuery` или `@Query(nativeQuery=true)`,
а `countQuery` не обязателен для каждого простого `Page`.
Парные MCQ Q5/Q6/Q7 B0 переписаны и точечно проверены по A/B/C/D и
sections; структура PASS (43 Q/44 блока), локальный pre-check PASS
без жёстких сигналов, весь старый JSON остаётся с 32 hard-сигналами
и общим parity FAIL. Независимая приёмка и полный обзор остальных Q
открыты; инвентарь без изменений.

**Контрольная точка 07.10.2026 — Spring Data JPA Q8 B0 · О.**
По [Spring Data JPA modifying queries](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
и [transactionality](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html)
уточнены область `@Modifying`, транзакция для объявленных query methods,
порядок `flushAutomatically`/`clearAutomatically` и риск потери
несохранённых изменений при очистке контекста. В Q7 устранено оставшееся
абсолютное требование `countQuery` для всякой нативной пагинации.
MCQ Q8 B0 синхронизирован; A/B/C/D и sections авторски проверены.
Структура PASS (43 Q/44 блока), локальный pre-check Q8 PASS без
предупреждений; в старом файле остаётся 31 hard-сигнал и общий parity
FAIL. Независимая приёмка и обзор других Q ожидаются; инвентарь прежний.

**Контрольная точка 07.10.2026 — Spring Data JPA Q9 B0 · О.**
По [Spring Data JPA templated queries](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
и [repository interfaces](https://docs.spring.io/spring-data/jpa/reference/repositories/definition.html)
исправлены границы SpEL: `#{#entityName}` даёт JPA-имя из `@Entity(name)`
или класса, а выражения с аргументами вычисляются при выполнении запроса.
Обобщённый пример получил `@NoRepositoryBean` и общий mapped superclass
с полем `active`. MCQ Q9 B0 синхронизирован; A/B/C/D и sections точечно
проверены. Структура PASS (43 Q/44 блока), локальный pre-check Q9 PASS;
во всём старом JSON всё ещё 31 hard-сигнал и parity FAIL. Независимая
приёмка и полный обзор темы открыты, инвентарь прежний.

**Контрольная точка 07.10.2026 — Spring Data JPA Q10 B0 · О.**
По [Spring Framework 6.2 `@Transactional`](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/declarative/annotations.html),
[propagation](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/declarative/tx-propagation.html)
и [AOP proxying](https://docs.spring.io/spring-framework/reference/6.2/core/aop/proxying.html)
исправлены proxy-mode и self-invocation: внутренний вызов не применяет
настройки вызываемого метода, но не отменяет уже открытую внешнюю
транзакцию. `REQUIRED` начинает либо присоединяет её; diagram больше
не обещает отдельный физический `BEGIN`/`COMMIT` на каждом вызове.
Уточнены rollback для `RuntimeException`/`Error`, checked-исключения,
глобальный `ALL_EXCEPTIONS` с 6.2 и видимость методов; пример с checked
`PaymentException` приведён к компилируемой сигнатуре. MCQ Q10 B0
синхронизирован, A/B/C/D и sections авторски проверены. Структура PASS
(43 Q/44 блока), локальный pre-check Q10 PASS без предупреждений;
во всём старом JSON остаётся 30 hard-сигналов и parity FAIL.
Независимая приёмка и полный обзор других Q остаются открыты.

**Контрольная точка 07.10.2026 — Spring Data JPA Q11 B0 · О.**
По [Spring propagation](https://docs.spring.io/spring-framework/reference/6.2/data-access/transaction/declarative/tx-propagation.html)
и [`JpaTransactionManager` 6.2.19](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/orm/jpa/JpaTransactionManager.html)
разграничены одна физическая транзакция `REQUIRED`, независимая
`REQUIRES_NEW` и JDBC savepoint у `NESTED`. Для JPA `EntityManager`
savepoint не означает откат его состояния. Отмечена граница версий:
`nestedTransactionAllowed` в 6.2.19 по умолчанию `true`, а в 7.0 —
`false`. `UnexpectedRollbackException` привязан к rollback-only после
выхода исключения через внутренний прокси, а не к любому пойманному
исключению. Пример независимого аудита теперь фиксирует попытку заказа,
а не утверждает успешный коммит. MCQ Q11 B0 синхронизирован; A/B/C/D
и sections авторски проверены. Структура PASS (43 Q/44 блока),
локальный pre-check Q11 PASS без предупреждений; в старом файле
остаётся 29 hard-сигналов и общий parity FAIL. Независимая приёмка
и полный обзор иных Q открыты, инвентарь прежний.

**Контрольная точка 07.10.2026 — Spring Data JPA Q12–Q13 B0 · О.**
Q12 сверён по [Spring Data JPA Locking](https://docs.spring.io/spring-data/jpa/reference/jpa/locking.html)
и [Jakarta Persistence 3.2 §3.5](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#locking-and-concurrency):
`@Lock` задаёт режим запроса, `PESSIMISTIC_READ` может усиливаться до
`WRITE`, а конкретный SQL не является переносимым контрактом. Пример
оптимистического обновления больше не вызывает метод с
`PESSIMISTIC_WRITE`; проверка `@Version` может произойти при
`flush`/`commit`. Q13 сверён по [Spring Data JPA Projections](https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html):
оптимизация закрытой проекции относится к top-level полям, вложенный join
может материализоваться целиком, DTO нужен конструктор, а открытая SpEL
проекция не получает такую же оптимизацию. Парные MCQ Q12–Q13 B0,
включая все A/B/C/D и sections, авторски сверены; структура PASS
(43 Q/44 блока), локальный pre-check обоих блоков PASS без warnings.
Полный pre-check прежнего файла всё ещё FAIL: 27 hard-сигналов,
32 блока с warnings; полный parity FAIL. Независимая blind-приёмка и
полная содержательная проверка других Q открыты; инвентарь прежний.

**Контрольная точка 07.10.2026 — Spring Data JPA Q14 B0 · О.**
По [Spring Data JPA Projections](https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#dynamic-projections)
подтверждено, что `Class<T>` выбирает интерфейс, DTO или сущность при
каждом вызове; при совпадении с возвращаемым `T` этот аргумент не доступен
JPQL/SpEL. Уточнён пример: сервис получил объявленную зависимость
репозитория. MCQ Q14 B0, A/B/C/D и sections авторски сверены;
структура 43 Q/44 блока PASS, локальный pre-check Q14 PASS без warnings.
В целом старый JSON остаётся FAIL: 26 hard-сигналов, 31 блок с warnings,
parity FAIL. Независимая приёмка Q14 и полный обзор иных Q открыты.

**Контрольная точка 07.10.2026 — Spring Data JPA Q15–Q16 B0 · О.**
По [Spring Data JPA Paging](https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html),
[`PageRequest`](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/domain/PageRequest.html)
и [`PageableExecutionUtils`](https://docs.spring.io/spring-data/commons/reference/api/java/org/springframework/data/support/PageableExecutionUtils.html)
уточнены zero-based страницы, возможный count для `Page`, size+1 для
`Slice`, отсутствие метаданных у `List`, стабильный tie-breaker и
смещение offset при конкурирующих изменениях. Пример Q15 больше не
выдаёт `JOIN FETCH` коллекции с `Page` за безопасную оптимизацию:
[Hibernate 7.4 release notes](https://docs.hibernate.org/orm/7.4/whats-new/)
разграничивают прежнюю пагинацию в памяти и новый SQL-путь при поддержке
СУБД. Прямой MVC `Pageable` привязан к Spring Data web support, а для
публичного JSON отмечен стабильный DTO/`PagedModel` вместо прямого
`PageImpl`. Q15/Q16 MCQ B0 и все A/B/C/D с sections авторски сверены;
структура 43 Q/44 блока PASS, локальный pre-check обоих блоков PASS
без warnings. В целом старый JSON всё ещё FAIL: 25 hard-сигналов,
29 блоков с warnings, parity FAIL. Независимая приёмка и полный
содержательный обзор остальных Q открыты; инвентарь прежний.

**Контрольная точка 07.10.2026 — Spring Data JPA Q17 B0 · О.**
По [Spring Data JPA Specifications](https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html)
и [API 4.1.1](https://docs.spring.io/spring-data/jpa/reference/api/java/org/springframework/data/jpa/domain/Specification.html)
проверены Criteria-предикаты, композиция и `JpaSpecificationExecutor`.
`Specification.where(nonNullSpec)` в 4.1.1 **существует**;
`where(null)` с 4.0 не принимается, для пустой базы есть
`unrestricted()`. Пример начат напрямую с `isActive()`, diagram
исправлена; пользовательский `LIKE` экранирует `%`/`_` средствами
[`EscapeCharacter`](https://docs.spring.io/spring-data/jpa/reference/api/java/org/springframework/data/jpa/repository/query/EscapeCharacter.html)
и `CriteriaBuilder.like(..., escapeChar)`. MCQ Q17 B0, A/B/C/D и
sections авторски сверены; структура 43 Q/44 блока PASS,
локальный pre-check Q17 PASS без warnings. В старом файле остаются
24 hard-сигнала, 28 блоков с warnings, общий parity FAIL;
независимая приёмка открыта.

**Контрольная точка 07.10.2026 — Spring Data JPA Q18/Q19/Q39 B0 · О.**
Q18 и повторяющий его Q39 разделены: Q18 описывает включение
`@EnableJpaAuditing`, подключение `AuditingEntityListener`,
даты без `AuditorAware`, автора с `AuditorAware<T>` и значения
`modifyOnCreate`/`setDates` по умолчанию. Для Spring Security пример
проверяет анонимный `Authentication` через `AuthenticationTrustResolver`,
а не только `isAuthenticated()`. Q39 теперь посвящён фоновому автору,
JPQL bulk update/delete, обходу listener и автоматической проверки
`@Version`, а также пределу истории в полях строки. Проверены
[Spring Data JPA Auditing 4.1.1](https://docs.spring.io/spring-data/jpa/reference/auditing.html),
[`@EnableJpaAuditing` API](https://docs.spring.io/spring-data/jpa/reference/api/java/org/springframework/data/jpa/repository/config/EnableJpaAuditing.html),
[Spring Security 7.1.1 trust resolver](https://docs.spring.io/spring-security/reference/api/java/org/springframework/security/authentication/AuthenticationTrustResolver.html)
и [Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2).
Q19 по [Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2)
и [Spring Data JPA Persisting Entities 4.1.1](https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html)
исправляет `merge(detached)`: управляемым становится возвращённый
экземпляр, который может уже быть в контексте. Новый объект с назначенным
ID ещё не managed; обязательный `SELECT` перед каждым `merge()` удалён.
Парные MCQ Q18/Q19/Q39 B0 и fact-freshness sidecar синхронизированы.
Авторски просмотрены A/B/C/D и sections. Структура 43 Q/44 блока PASS,
локальный pre-check всех трёх блоков без hard/warnings; весь прежний файл
имеет 22 hard-сигнала и 26 блоков с warnings, общий parity FAIL.
Независимой blind-приёмки этих блоков и полного содержательного аудита
файла не было; инвентарь не менялся.

**Контрольная точка 07.10.2026 — Spring Data JPA Q20 B0 · О.**
По [Jakarta Persistence 3.2, callbacks](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2)
уточнены `@PrePersist`/`@PreRemove` при вызове операции,
`@PostPersist`/`@PostRemove` после SQL без гарантии commit,
`@PostLoad` после загрузки/refresh и недоступность `IDENTITY` ID
в `@PrePersist`. Категоричный запрет на `EntityManager` заменён точной
рекомендацией спецификации для переносимого кода. Пример listener
больше не публикует внешнее событие до commit. Для действий после
транзакции разграничены [`@TransactionalEventListener`](https://docs.spring.io/spring-framework/reference/data-access/transaction/event.html)
и [журнал публикаций Spring Modulith](https://docs.spring.io/spring-modulith/reference/events.html).
MCQ Q20 B0 и sidecar обновлены, A/B/C/D с sections авторски сверены;
локальный pre-check без hard/warnings, structure 43 Q/44 блока PASS.
У прежнего файла остаются 21 hard-сигнал и 25 блоков с warnings,
общий parity FAIL; независимой blind-приёмки нет.

**Контрольная точка 07.10.2026 — Spring Data JPA Q21 B0 + Hibernate Q8 B0 · О.**
По [Jakarta Persistence 3.2 `FlushModeType`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/flushmodetype)
и [Hibernate ORM 7.4 Flushing/Batching](https://docs.hibernate.org/orm/7.4/userguide/html_single/)
исправлено ложное «`AUTO` flush перед каждым SELECT»: JPA гарантирует
видимость влияющих изменений, Hibernate проверяет пересечение JPQL/HQL
с ожидающими операциями. `COMMIT` не гарантирует видимость изменений
промежуточному запросу и может flush'ить раньше commit; `MANUAL` —
режим Hibernate, не JPA. Batch-пример Q21 больше не меняет режим без
промежуточных запросов; исправлен первый flush после 50-й записи,
описаны `flush()`/`clear()` отдельно от JDBC batching и `IDENTITY`.
Соседний Hibernate Q8 и его MCQ синхронизированы; дополнительно
разделены обычное diff-based dirty checking и in-line tracking
при bytecode enhancement. Новая версия Q8
требует повторной независимой приёмки, freshness-status переведён в
`TODO`. Авторски сверены A/B/C/D и sections обоих блоков; локальный
pre-check без hard/warnings. Структура MCQ двух файлов PASS; у старого
Spring Data JPA ещё 20 hard-сигналов/24 блока с warnings, у Hibernate
18/29; parity Spring Data JPA FAIL, Hibernate PASS. Полной
содержательной проверки файлов не было.

**Контрольная точка 07.10.2026 — Hibernate Q9 B0 · О.**
При сопоставлении Spring Data JPA Q19 найден устаревший ответ Hibernate
Q9: он называл `Session.save/update` действующими методами и обещал
всегда «новую копию» после `merge`. По [Hibernate 7.0 Migration Guide](https://docs.hibernate.org/orm/7.0/migration-guide/)
все формы `Session.save/update` удалены с 7.0, в
[6.6 API](https://docs.hibernate.org/orm/6.6/javadocs/org/hibernate/Session.html)
они deprecated; `save` возвращал `Object`. `merge(detached)` возвращает
managed-экземпляр, который может уже быть в контексте; исходный объект
остаётся detached. Q9 и MCQ B0 исправлены, A/B/C/D и sections авторски
сверены, локальный pre-check без hard/warnings. Hibernate full parity PASS,
но прежний файл остаётся с 18 hard-сигналами и 28 блоками с warnings,
независимой содержательной приёмки новой Q9 нет.

**Контрольная точка 07.10.2026 — Hibernate Q10 B0 · О.**
По [Hibernate 7.0 Migration Guide](https://docs.hibernate.org/orm/7.0/migration-guide/),
[Hibernate 7.4 `Session` API](https://docs.hibernate.org/orm/7.4/javadocs/org/hibernate/Session.html)
и [Jakarta Persistence 3.2 `EntityManager` API](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/entitymanager)
исправлено: `Session.load` удалён, `Session.get` deprecated;
актуальные методы — `find` и `getReference`. `find` может вернуть
managed объект без SELECT и даёт `null` при отсутствии; Hibernate
`getReference` не читает БД при вызове, но ссылка не доказывает
существование строки и не обязана быть новым proxy. JPA допускает
`EntityNotFoundException` как при вызове, так и при доступе к состоянию.
MCQ Q10 B0/sections и freshness-sidecar синхронизированы;
локальный pre-check без hard/warnings, структура PASS, полный parity
Hibernate PASS. Старый файл по-прежнему имеет 18 hard-сигналов и
28 блоков с warnings, независимой содержательной приёмки Q10 нет.

**Контрольная точка 07.10.2026 — Spring Data JPA Q22 B0 + Hibernate Q19 B0 · О.**
После просмотра заголовков (`З`) точечно сопоставлены оба ответа и
четыре варианта/sections каждого MCQ (`О`); полного аудита файлов (`П`)
не было. По [Jakarta Persistence 3.2 `FetchType`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/fetchtype),
[Hibernate ORM 7.4 fetching](https://docs.hibernate.org/orm/7.4/userguide/html_single/),
[Hibernate `LazyInitializationException`](https://docs.hibernate.org/orm/7.4/javadocs/org/hibernate/LazyInitializationException.html)
и [Spring Boot OSIV](https://docs.spring.io/spring-boot/reference/data/sql.html)
исправлены ложные гарантии `EAGER = JOIN` и `LAZY = отдельный SELECT`;
`EAGER` — требование ранней загрузки, `LAZY` — подсказка, to-one по
умолчанию EAGER, to-many LAZY. Hibernate JPQL без fetch join может
загрузить EAGER связь вторичными SELECT и дать N+1. Spring Boot web
по умолчанию включает OSIV, поэтому окончание сервисной транзакции
не обязательно закрывает `EntityManager`; исключение связано с доступом
к незагруженной связи после закрытия `Session`. Парные MCQ и
fact-freshness sidecar синхронизированы. Structure 93 Q/94 блока PASS,
оба Markdown без inline MCQ; локальный pre-check Q22/Q19 без hard/warnings.
Полный parity Hibernate PASS после выравнивания Q19, Spring Data JPA FAIL
на прежних блоках (25 нарушений длины, 8 повторяющихся 10-грамм и другие
сигналы); полный skel старых файлов: JPA 19 hard/23 блока с warnings,
Hibernate 18/28. Это не независимая содержательная приёмка: Q22/Q19
ожидают blind review.

**Контрольная точка 07.10.2026 — Spring Data JPA Q23/Q24 B0 + Hibernate Q20/Q21/Q23 B0 · О.**
По [Jakarta Persistence 3.2, графы](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2),
[Spring Data JPA 4.1.1, query methods](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html),
[Hibernate ORM 7.4, fetching](https://docs.hibernate.org/orm/7.4/userguide/html_single/)
и [`MultipleBagFetchException`](https://docs.hibernate.org/orm/7.4/javadocs/org/hibernate/loader/MultipleBagFetchException.html)
исправлены: N+1 бывает и при LAZY-обходе, и при EAGER to-one в JPQL/HQL;
EAGER не гарантирует JOIN, `EntityGraph` не обещает LEFT JOIN/один SQL;
`FETCH` для остальных атрибутов — LAZY-подсказка, `LOAD` сохраняет
маппинг, провайдер может загрузить больше. Для параллельных fetch joins
коллекций отмечены декартово произведение и запрет нескольких bag.
Пояснено, что Hibernate 6+ удаляет дубли корней из результата fetch join
в памяти: `DISTINCT` и смена `List` на `Set` не нужны ради этой цели и
не уменьшают SQL-результат. Исправлены примеры `LEFT JOIN FETCH`,
`GROUP BY` по идентификатору и условность числа запросов при batch/subselect.
После сверки заголовков (`З`) прочитаны конкретные ответы и все A/B/C/D
с sections (`О`); полного аудита файлов (`П`) и независимой blind-приёмки
нет. Structure 93 Q/94 блока PASS, оба Markdown без inline MCQ; все пять
изменённых блоков локально без hard/warnings. Полный parity Hibernate
PASS, Spring Data JPA FAIL на прежних блоках; полный skel остаётся
Hibernate 16 hard/26 блоков с warnings, JPA 17/21. Sidecar обновлены,
MCQ ожидают независимой приёмки.

**Контрольная точка 07.10.2026 — Spring Data JPA Q25 B0 + Hibernate Q15/Q16 B0 · О.**
По [Jakarta Persistence 3.2 `CascadeType`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/cascadetype),
[orphan removal и ограничению `cascade=REMOVE`](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2),
[Spring Data JPA `save`](https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html)
и [Hibernate ORM 7.4 `@ManyToMany`](https://docs.hibernate.org/orm/7.4/userguide/html_single/)
разделены `PERSIST`/`MERGE`/dirty checking; `ALL` перечисляет пять
операций. `orphanRemoval` для приватно принадлежащих детей срабатывает
при flush, не применяется к new/detached/removed сиротам и каскадирует
удаление при удалении родителя. `REMOVE` на `@ManyToMany` непереносим,
распространяется на целевые сущности при удалении владельца, тогда как
изъятие элемента меняет join-таблицу. В Hibernate Q15 пример
Department–Employee больше не удаляет сотрудника при переводе, а Q16
не требует `Set` вместо `List` без учёта доменной семантики и SQL.
Парные MCQ/sections и freshness-sidecar обновлены. После просмотра
заголовков (`З`) выполнена точечная сверка ответов и A/B/C/D (`О`), но
полного аудита (`П`) и независимой blind-приёмки нет. Structure 93 Q/94
блока PASS; изменённые Q25/Q15/Q16 без локальных hard/warnings; полный
parity Hibernate PASS, Spring Data JPA FAIL из-за прежних блоков;
полный skel Hibernate 16 hard/25 блоков с warnings, JPA 16/20.

**Контрольная точка 07.10.2026 — Spring Data JPA Q26 B0 · О.**
Сверено соседство с Hibernate Q18 (`З` и точечное чтение: основа
`@Embeddable` уже покрыта, новый файл не нужен). По
[Jakarta Persistence 3.2 `@Embeddable`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/embeddable),
[`@AttributeOverride`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/attributeoverride)
и [релизу 3.2](https://jakarta.ee/specifications/persistence/3.2/)
добавлена граница: Java record с Java 16, стандартный record-embeddable
с Jakarta Persistence 3.2; no-arg конструктор record не нужен, обычному
embeddable-классу нужен. `@Entity` не может быть record. Уточнены
identity и хранение в таблицах владельца, `@ElementCollection` отдельно,
`@AttributeOverrides` переопределяет SQL-колонки, а не Java-компоненты.
MCQ Q26 B0/sections и sidecar синхронизированы; A/B/C/D авторски
сверены (`О`), полной проверки файла (`П`) и независимой blind-приёмки
нет. Structure JPA 43 Q/44 блока PASS, Markdown без inline MCQ;
Q26 локально без hard/warnings. Полный skel старого JPA файла
15 hard/19 блоков с warnings, parity FAIL из-за прежних блоков.

**Контрольная точка 07.10.2026 — Spring Data JPA Q27 B0 + Hibernate Q14 B0 · О.**
После просмотра заголовков (`З`) и соседних ответов по
[Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2.html),
[Hibernate ORM 7.4](https://docs.hibernate.org/orm/7.4/userguide/html_single/) и
[`@CheckConstraint`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/checkconstraint)
уточнены обязательные `SINGLE_TABLE`/`JOINED` и необязательная
`TABLE_PER_CLASS`; JOIN при материализации подтипа и полиморфном чтении,
а не при каждом запросе; UNION или отдельные запросы для полиморфного
`TABLE_PER_CLASS` по JPA и пример UNION ALL в Hibernate. Поле подтипа в
`SINGLE_TABLE` допускает NULL для других строк, но условный CHECK может
требовать значение для нужного дискриминатора. Категоричные оценки
скорости сняты. Парные MCQ A/B/C/D и sections, freshness-sidecar
синхронизированы и авторски проверены (`О`). Structure 93 Q/94 блока
PASS, изменённые блоки без hard/warnings, Hibernate full parity PASS;
JPA full parity FAIL на старых блоках (24/44 с несоразмерной длиной,
другие прежние сигналы). Полной содержательной проверки файлов (`П`)
и независимой blind-приёмки нет. Коммит `5a480396b`.

**Контрольная точка 07.10.2026 — Spring Data JPA Q28 B0 + Hibernate Q36 B0 · О.**
Сопоставлены заголовки и ответы (`З` → `О`) с JPA Q21 и Hibernate Q36.
По [Hibernate ORM 7.4, batching](https://docs.hibernate.org/orm/7.4/userguide/html_single/#batch),
[BatchSettings](https://docs.hibernate.org/orm/7.4/javadocs/org/hibernate/cfg/BatchSettings.html),
[Spring Data JPA, transactions](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html)
и [`SimpleJpaRepository.saveAll`](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/support/SimpleJpaRepository.html)
исправлено смешение JDBC batching и размера persistence context:
положительный `batch_size` включает пакеты, а периодические
`flush()`/`clear()` ограничивают память; `flush()` не делает commit.
`saveAll` не гарантирует JDBC batch, `IDENTITY` отключает именно insert
batching, `SEQUENCE` может выдать ключ до INSERT. `order_inserts` и
`order_updates` необязательны и требуют замера. В обоих примерах
устранён ошибочный первый flush на первой записи/51-й записи; убрано
неподтверждённое обещание одного round-trip ровно на 50 INSERT и
категоричный `OutOfMemoryError`. Парные MCQ A/B/C/D/sections и
freshness-sidecar синхронизированы. Structure 93 Q/94 блока PASS,
изменённые блоки без hard/warnings, Hibernate full parity PASS; JPA
full parity FAIL по прежним блокам (22/44 с несоразмерной длиной и
другие сигналы). Полной проверки файлов (`П`) и независимой blind-
приёмки нет. Коммит `851363aa4`.

**Контрольная точка 07.10.2026 — Spring Data JPA Q29 B0 + Hibernate Q24/Q25/Q27 B0 · О.**
Просмотр заголовков (`З`) и точечное чтение Q24–Q27 показали пересечение:
отдельная тема L2 не нужна, но в JPA Q29 L1 ошибочно ограничивался
транзакцией, а JCache provider был задан не под `hibernate`.
По [Hibernate ORM 7.4, caching/JCache](https://docs.hibernate.org/orm/7.4/userguide/html_single/)
уточнены жизнь L1 до `clear()`/закрытия EntityManager, отсутствие L1 у
`StatelessSession`, L2 на фабрике, default `use_second_level_cache=true`
при настроенном `RegionFactory`, selective `@Cacheable`, роль Hibernate
`@Cache`, отдельный кэш ID коллекции и границы `READ_WRITE`/
`TRANSACTIONAL`. Убрано обещание полного XA/кластерного кэша от одного
enum и категоричное «L2 всегда требует явного флага».
Теория, четыре парных MCQ A/B/C/D/sections и freshness-sidecar
синхронизированы; авторская точечная проверка выполнена (`О`).
Structure 93 Q/94 блока PASS, локальные Q29/Q24/Q25/Q27 без
hard/warnings, Hibernate full parity PASS; JPA full parity FAIL из-за
прежних блоков (21/44 с несоразмерной длиной и другие сигналы).
Полной проверки файлов (`П`) и независимой blind-приёмки нет. Коммит
`adba6f9eb`.

**Контрольная точка 07.10.2026 — Spring Data JPA Q30 B0, soft delete · О.**
Просмотр заголовков (`З`) показал, что отдельная тема не нужна: ближайший
существующий вопрос — JPA Q30; Q8/Q33/Q39 покрывают смежные `@Modifying`,
derived delete и bulk/auditing. Точечная проверка ответа (`О`) по
[Hibernate ORM 7.4, soft delete](https://docs.hibernate.org/orm/7.4/userguide/html_single/#soft-delete),
[`@SoftDelete` Javadoc](https://docs.hibernate.org/orm/7.4/javadocs/org/hibernate/annotations/SoftDelete.html),
[`@SQLRestriction` Javadoc](https://docs.hibernate.org/orm/7.4/javadocs/org/hibernate/annotations/SQLRestriction.html),
[Spring Data JPA, transactions](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html),
[modifying queries](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
и [PostgreSQL, partial indexes](https://www.postgresql.org/docs/current/indexes-partial.html)
подтвердила: `@SQLRestriction` с 6.3 задаёт статическое ограничение, но
не превращает `remove()` в `UPDATE`; `@SoftDelete` с 6.4 управляет
удалением и обычной загрузкой, `TIMESTAMP` проверена для 7.4. Native
SQL требует явного предиката; объявленный `@Modifying @Query` требует
транзакционной границы, bulk UPDATE может оставить устаревшие сущности
в контексте. Исправлены теория, четыре MCQ-варианта и sections, а
также freshness-sidecar; отдельный файл и новый Q не создавались.
Structure 43 Q/44 блока PASS, локальный Q30 без hard/warnings.
Полный JPA parity по-прежнему FAIL из-за старых блоков, полной проверки
файла (`П`) и независимой blind-приёмки Q30 нет. Коммит `24942dd8e`.
Гипотеза о поведении
произвольных bulk DELETE при soft-delete-маппинге оставлена за рамками
без отдельной проверки конкретной версии/запроса.

**Контрольная точка 08.10.2026 — Spring Data JPA Q31 B0, Hibernate Filters · О.**
Просмотр заголовков (`З`) показал, что фильтры уже имеют JPA Q31;
в Hibernate-шпаргалке отдельного Q о них нет, а multi-tenancy в соседних
темах не раскрывает API фильтра. По [Hibernate ORM 7.4, `@Filter`](https://docs.hibernate.org/orm/7.4/userguide/html_single/#pc-filter),
[`@FilterDef` Javadoc](https://docs.hibernate.org/orm/7.4/javadocs/org/hibernate/annotations/FilterDef.html)
и [PostgreSQL RLS](https://www.postgresql.org/docs/current/ddl-rowsecurity.html)
точечно исправлены (`О`) ложное «ко всем запросам», прямые загрузки по
ID без `applyToLoadByKey`, автоматическое включение, AOP-пример без
явной границы транзакции и обещание изоляции арендаторов одним фильтром.
Пример использует конкретный `Session` внутри транзакционного сервиса,
параметр и `finally`; MCQ B0 и freshness-sidecar синхронизированы.
Structure 43 Q/44 блока PASS, локальный Q31 без hard/warnings;
полный JPA parity FAIL из-за прежних блоков. Полной проверки файла (`П`)
и независимой blind-приёмки Q31 нет. Коммит `73c177d6d`.
Возможное поведение уже загруженных
объектов и ленивых связей названо ограничением, но отдельный runtime-тест
не выполнен.

**Контрольная точка 08.10.2026 — Spring Data JPA Q32 B0, repository fragments · О.**
Просмотр заголовков (`З`) нашёл ближайший JPA Q32 и пересечения с Q4
(создание реализации) и Q17 (`Specification`); отдельный файл не нужен.
Точечная сверка (`О`) с [Spring Data JPA 4.1.1, Custom Repository Implementations](https://docs.spring.io/spring-data/jpa/reference/repositories/custom-implementations.html)
подтвердила фрагментную композицию, `<ИнтерфейсФрагмента>Impl` как
конвенцию autodetection, возможность ручного bean wiring и регистрации
внешнего фрагмента через `META-INF/spring.factories`. Историческое
`<ОсновнойРепозиторий>Impl` поддерживается, но документация считает
single-implementation naming устаревшим. Исправлены теория и пример
на полях существующего `User`, MCQ B0 и freshness-sidecar. Structure
43 Q/44 блока PASS, локальный Q32 без hard/warnings; полный JPA parity
FAIL по прежним блокам. Полного аудита файла (`П`) и независимой blind-
приёмки Q32 нет. Коммит `3875da44e`.

**Контрольная точка 08.10.2026 — Spring Data JPA Q33 B0, derived delete · О.**
Просмотр заголовков (`З`) показал пересечение с JPA Q8/Q30/Q39/Q42,
но Q33 уже отвечает за `deleteBy`; новый Q не нужен. По
[Spring Data JPA 4.1.1, Derived Delete Queries](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html),
[transactionality](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html)
и [Hibernate ORM 7.4, bulk DML/soft delete](https://docs.hibernate.org/orm/7.4/userguide/html_single/)
точечно проверены (`О`) выборка сущностей перед поштучным удалением,
callback'и и каскады, явная транзакция для объявленного метода и
затраты памяти. Убраны неточные «N+1 на удалении» и обещание ровно
одного SQL для bulk JPQL: один JPQL-вызов может породить несколько SQL.
Теория, MCQ B0 и freshness-sidecar синхронизированы; структура 43 Q/
44 блока PASS, локальный Q33 без hard/warnings, полный JPA parity FAIL
из-за прежних блоков. Полного аудита файла (`П`) и независимой blind-
приёмки Q33 нет. Коммит `02337debc`.
Взаимодействие конкретного bulk DELETE с `@SoftDelete`
остаётся гипотезой до runtime-проверки версии и запроса.

**Точная точка авторского продолжения:** Spring Data JPA Q34 B0,
`@DataJpaTest`. Сравнить заголовки Spring Testing и Testcontainers,
затем точечно проверить Q34/MCQ по Spring Boot 4.1/3.5 и Testcontainers:
состав test slice, транзакцию/rollback, замещение DataSource,
`@ServiceConnection` и Flyway. Исправить только подтверждённое,
не объявляя полный файл проверенным.
Независимую MCQ-приёмку начинать с Batch Q6 B0/B1, Q28 B0, Q38 B0
по пункту ниже; авторская самопроверка её не заменяет.

1. **Точная точка независимой приёмки:** содержательная MCQ-приёмка
   последней группы, начиная с `frameworks/spring/spring-batch-interview.md`
   Q6 B0/B1, Q28 B0, Q38 B0 и соответствующего production JSON. Проверить
   правильность каждого A/B/C/D по теории и официальным Batch 5.0/5.2/6,
   Boot 3.5/4.1 источникам, затем отдельно SFPL/ANSC и русский текст через
   фазы A/B workflow; авторские structure/parity/pre-check PASS не считать
   независимым `FINAL`. После этого пройти независимую приёмку Uber
   Q6/Q7, Maps Q27 и Spring Framework Q1/Q2/Q9/Q10 B0/B1. Их авторская
   точечная проверка проведена; Q10 B0 и граница проектов исправлены.
   Старые hard-сигналы Q2/Q26/Q27/Q39/Q42 Batch уже устранены, их независимая содержательная проверка также ожидается.
   Python 3.15 и PostgreSQL 19 проверять только после GA; Stock Exchange остаётся
   кандидатом до подтверждения профильной ценности.
2. Ad Click Event Aggregation и Distributed Email Service Q1–Q4 написаны; их MCQ вместе с
   Metrics Platform Q1–Q4, Leaderboard Q1–Q5, Object Storage Q1–Q4, Reservation System Q1–Q4,
   Notification System Q1–Q4, Digital Wallet Q1–Q4, Database Transactions Q43,
   Java CompletableFuture Q2 B0/Q3 B0/B1/Q4 B0/Q6 B0/Q8 B0/B1/Q10 B0/Q11/Q14,
   Kubernetes Q22/Q39/Q46/Q47,
   Spring Rest Client Q4/Q6/Q7/Q11/Q12/Q13/Q14, WebAuthn Q24/Q46, Spring Security Q44,
   Metrics & Tracing Q40, Spring Testing Q9/Q10, Uber Q6/Q7,
   Google Maps Q27, Spring Framework Q1/Q2/Q9/Q10 B0/B1,
   Spring Batch Q6 B0/B1/Q28/Q38, Spring Data JPA Q3 B0/Q4 B1/Q5–Q29 B0/Q39 B0,
   Hibernate Q8–Q10 B0/Q14–Q16 B0/Q19–Q21 B0/Q23–Q25/Q27/Q36 B0,
   Database Transactions Q15/Q22/Q23 B0
   и RabbitMQ Streams Q40–Q42 ждут
   независимой MCQ-приёмки.
   Java 26–27, HTTP/3, Jackson 3 и Python typing/packaging уже написаны.
   Остальные P2/P3 без решения остаются кандидатами. Статусы Python 3.15 и
   PostgreSQL 19 перепроверять после финальных релизов.
3. Отдельной задачей пройти независимую blind-приёмку новых MCQ и старый
   parity/pre-check backlog Hibernate, Spring Data JPA (после Q3/Q4,
   Q5–Q29 и Q39 ещё 12 hard-сигналов; Hibernate после Q8–Q10/Q14–Q16/Q19–Q21/Q23–Q25/Q27/Q36 — 15),
   Java IO/NIO, Jackson,
   URL Shortener, RabbitMQ, Kafka, сравнения брокеров и event-driven;
   положительный валидатор не означает `FINAL`.
4. Продолжать выборку официальных публикаций за скользящие шесть месяцев;
   не выдавать выборку за полный охват. Для каждого решения фиксировать
   источник, ближайший файл/Q, пробел, приоритет и уровень `З`/`О`/`П`.
   Обновлять план и парный MCQ после каждой небольшой группы; `FINAL` MCQ
   только после независимого evidence по [правилам качества](docs/mcq-quality/workflow.md).
5. По новому запросу пользователя продолжать
   [блок `system-design-notes`](#новый-источник-для-system-design-system-design-notes):
   генератор ID и уведомления написаны, распределённая очередь закрыта
   углублением соседей; остальные строки пока
   сопоставлены по структуре и Q (`З`). Проверять соседние ответы и отсекать
   дубли перед новым файлом; при авторинге добавлять парный MCQ.

[Самодостаточный промпт продолжения](PROMPT_PLAN_CHEATSHEETS.md) описывает
порядок работы без истории чата.
