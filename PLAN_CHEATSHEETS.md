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
| P1 · новый файл | `python-concurrency-interview.md` Q1–Q9; рядом Python core | asyncio/threads/processes, TaskGroup/отмена, backpressure, free-threaded CPython; [asyncio](https://docs.python.org/3/library/asyncio-task.html), [free-threading](https://docs.python.org/3/howto/free-threading-python.html). | Исходный пакет 9/9 JSON; теперь 9 Q/10 блоков после Q9 B1. Структура/parity PASS; независимая приёмка ожидается. |
| P1 · углублено · Q22–Q25 П | `go-stdlib-interview.md` Q22–Q25; рядом Q1–Q5 HTTP | `database/sql` уже имел маршрут; добавлены context, `Rows.Err`, `DB.Stats()`, границы `LastInsertId` и транзакций по [Go database docs](https://go.dev/doc/database/). Новый файл отклонён. | JSON Q22–Q25, структура PASS; parity всего файла блокируют старые Q. Независимая приёмка ожидается. |
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
| P0 · `DEEPEN`, выполнено | [Baeldung: MCP Logging in Spring AI](https://www.baeldung.com/spring-ai-mcp-logging), обновлено 14.10; [MCP 2026-07-28](https://blog.modelcontextprotocol.io/posts/2026-07-28/) | `ai-ml/mcp-interview.md` Q6/Q8/Q13/Q14/Q28–Q30: версия протокола и deprecated-механизмы не были разграничены. Новая Logging-тема пока не нужна: Q уже охватывают протокол, а Spring AI logging требует отдельного сравнения с `spring-ai-interview.md` Q16. | `О` по указанным Q и changelog; JSON обновлён, структура PASS, файл целиком не принят. |
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
| P3 / `ADD_Q` выполнено · О | 09.10 [Python 3.15.0](https://www.python.org/downloads/release/python-3150/), [What's New](https://docs.python.org/3.15/whatsnew/3.15.html), [PEP 686](https://peps.python.org/pep-0686/) | `programming-languages/python/python-interview.md` Q8 показывал явный `encoding` при закрытии файла, но не объяснял смену значения по умолчанию; Python concurrency Q1–Q9 посвящены другому механизму | Q10 объясняет UTF-8 Mode по умолчанию в 3.15, границу 3.11–3.14, `encoding="locale"` и проверку старых файлов при обновлении сервиса. Теория, TOC, JSON B0, freshness-sidecar и инвентарь 10 Q/10 блоков синхронизированы. Structure/Markdown/parity PASS; локальный skel Q10 без hard/warn, общий pre-check FAIL на прежних Q1–Q9. Авторская проверка A/B/C/D проведена; полная `П` и независимая MCQ-приёмка ожидаются. |
| P3 / `DEEPEN` выполнено · О | [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html), [Python `TypedDict`](https://docs.python.org/3.15/library/typing.html#typing.TypedDict), [PEP 728](https://peps.python.org/pep-0728/) | `programming-languages/python/python-typing-interview.md` Q4 уже объяснял `NotRequired` и отсутствие runtime-валидации; Q1 — общую границу аннотаций, Python Core Q10 — кодировку | Q4 дополнен `closed=True` и `extra_items=T` в стандартном Python 3.15: статические контракты лишних ключей, взаимоисключение параметров, тип известных полей и отдельная проверка JSON. Production MCQ Q4 B1 добавлен, B0 сохранён; sidecar обновлён. Structure/Markdown/parity/gates PASS, 4 Q/5 блоков; авторская точечная проверка проведена, полная `П` и независимая MCQ-приёмка ожидаются. Инвентарь не менялся. |
| P3 / `ADD_Q`, написано · О | [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-810-explicit-lazy-imports), [PEP 810](https://peps.python.org/pep-0810/) | `programming-languages/python/python-interview.md` Q6/Q7/Q10 и `python-packaging-interview.md` Q1/Q4 не объясняли отложенную загрузку модуля, перенос ошибки и побочных эффектов к первому запросу | Добавлен Core Q11 о `lazy import` Python 3.15, выборе обычного импорта для регистрации и границе синтаксиса. Production MCQ Q11 B0, TOC, sidecar и инвентарь синхронизированы: 11 Q/11 блоков. Structure/Markdown/parity PASS, локальный skel Q11 без hard/warn; общий gates FAIL на прежних Q1–Q9. A/B/C/D и sections точечно прочитаны автором; полная `П` и независимая MCQ-приёмка ожидаются. |
| P3 / `DEEPEN`, выполнено · О | [Python 3.15 `sentinel()`](https://docs.python.org/3.15/builtins/functions.html#sentinel), [PEP 661](https://peps.python.org/pep-0661/) | `programming-languages/python/python-interview.md` Q5 упоминал отдельный sentinel, но не показывал различие пропуска и явного `None`; `python-typing-interview.md` Q1 лишь отличал optional/default от `T | None` | Q5 углублён примером `sentinel()` в Python 3.15, сравнением через `is` и вариантом для 3.11–3.14. Заголовок/TOC и production MCQ Q5 B0 синхронизированы, B1 добавлен; sidecar и инвентарь: 11 Q/12 блоков. Structure/Markdown/parity PASS; локальный skel B1 без hard/warn, общий skel FAIL на старых блоках, включая Q5 B0. Авторская точечная проверка A/B/C/D проведена, полная `П` и независимая MCQ-приёмка ожидаются. |
| P3 / `DEEPEN`, выполнено · О | [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-814-add-frozendict-built-in-type), [PEP 814](https://peps.python.org/pep-0814/), [built-in types](https://docs.python.org/3.15/builtins/stdtypes.html#mapping-types-dict-frozendict) | `programming-languages/python/python-interview.md` Q3 выбирал `dict`, Q4 разбирал hash ключа, Q5 — default, Q9 — `frozen=True`; ни один ответ не объяснял `frozendict` как отдельный тип в Python 3.15 | Q3 углублён: неизменяемый внешний mapping, shallow copy, условная хешируемость и контракт `Mapping` вместо проверки `dict`. Заголовок/TOC и MCQ Q3 B0 синхронизированы, B1 добавлен; sidecar и инвентарь: 11 Q/13 блоков. Structure/Markdown/parity PASS, локальный skel B1 без hard/warn; общий gates FAIL на старых блоках. A/B/C/D и sections точечно прочитаны автором; полная `П` и независимая MCQ-приёмка ожидаются. |
| P3 / `ADD_Q`, написано · О | [Python 3.15 `profiling.sampling`](https://docs.python.org/3.15/library/profiling.sampling.html), [`profiling.tracing`](https://docs.python.org/3.15/library/profiling.tracing.html), [What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-799-a-dedicated-profiling-package) | `performance/application-profiling-interview.md` Q1/Q2/Q14/Q25/Q27 даёт общий и преимущественно JVM-контекст; Python Concurrency Q1/Q9 и Core Q11 не описывают подключение к работающему Python-процессу | Добавлен Core Q12: `attach`/`run`/`dump`, wall/CPU, sampling против tracing, короткие процессы и права attach. TOC/See also, MCQ B0, sidecar и инвентарь синхронизированы: 12 Q/14 блоков. Structure/Markdown/parity PASS, локальный skel Q12 без hard/warn; общий gates FAIL на прежних блоках. A/B/C/D и sections точечно прочитаны автором; полная `П` и независимая MCQ-приёмка ожидаются. |
| P3 / `DEEPEN`, выполнено · О | [Python free-threading guide 3.15](https://docs.python.org/3.15/howto/free-threading-python.html), [PEP 803](https://peps.python.org/pep-0803/) | `programming-languages/python/python-concurrency-interview.md` Q9 объяснял параллелизм и GIL, но не проверку wheel; `python-packaging-interview.md` Q4 обсуждает повторяемые зависимости без `abi3t`. | Q9 дополнен проверкой сборки и runtime GIL после импортов, совместимостью `abi3t`/`abi3.abi3t` в CPython 3.15 и границей ABI против потокобезопасности. MCQ Q9 B1 добавлен, 9 Q/10 блоков; structure/Markdown/parity PASS, локальный skel B1 без hard, с предупреждениями. Полная `П` и независимая MCQ-приёмка ожидаются. |
| P3 / `ADD_Q`, написано · О | [Python 3.15 `site`](https://docs.python.org/3.15/library/site.html), [What's New: PEP 829](https://docs.python.org/3.15/whatsnew/3.15.html#pep-829-package-startup-configuration-files), [PEP 829](https://peps.python.org/pep-0829/) | `programming-languages/python/python-packaging-interview.md` Q1–Q4 разобраны точечно: Q4 о повторяемой установке, но ни один Q не объяснял код при запуске интерпретатора; Python Core Q11 касается lazy-импорта. | Добавлен Packaging Q5: пути из `.pth`, явные entry points `.start` в Python 3.15, правило парных файлов и граница `-S`. Production MCQ Q5 B0, sidecar, TOC и инвентарь синхронизированы: 5 Q/5 блоков. Structure/Markdown/parity PASS, локальный skel Q5 без hard/warn. Авторская точечная проверка A/B/C/D выполнена; полная `П` и независимая MCQ-приёмка ожидаются. |
| P3 / `DEEPEN`, выполнено · О | [Python 3.15 What's New: PEP 831](https://docs.python.org/3.15/whatsnew/3.15.html#pep-831-frame-pointers-enabled-by-default), [perf profiling HOWTO](https://docs.python.org/3/howto/perf_profiling.html), [PEP 831](https://peps.python.org/pep-0831/) | `programming-languages/python/python-interview.md` Q12 объяснял встроенный `profiling.sampling attach`; `performance/application-profiling-interview.md` Q1/Q2/Q35 давал общий/JVM-контекст, но не сборку CPython для системного профайлера. | Core Q12 дополнен: frame pointers в поддерживаемых сборках CPython 3.15, отдельный perf map для имён Python-функций, цепочка нативных расширений и границы Windows x64/custom build. MCQ Q12 B1, sidecar и инвентарь синхронизированы: 12 Q/15 блоков. Structure/Markdown/parity PASS, локальный skel B1 без hard/warn; общий gates FAIL на прежних блоках. Авторская точечная проверка выполнена, полная `П` и независимая MCQ-приёмка ожидаются. |
| P2 / `NEW_TOPIC` + `ADD_Q`, написано · П | [PEP 249 DB-API](https://peps.python.org/pep-0249/), [Python `sqlite3`](https://docs.python.org/3.15/library/sqlite3.html), [SQLAlchemy Session Basics](https://docs.sqlalchemy.org/en/20/orm/session_basics.html), [async implicit I/O](https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html#preventing-implicit-io-when-using-asyncsession), [relationship loading](https://docs.sqlalchemy.org/en/20/orm/queryguide/relationships.html) | Python Database Q6/Q7 показывали загрузку конкретной связи, Hibernate Q20/Q21 объясняли N+1 в другой ORM; выбора `selectinload`/`joinedload` и требования `Result.unique()` для коллекции не было. | Python Database Q1–Q8 прошли авторскую полную `П`, MCQ 13 блоков. Q1 `sql.Identifier`, Q2 восстановление Session, Q3 offload sync DB-операции, Q4 ручной SQL BEGIN, Q5 возврат scalar IDs; Q6/Q7 сверены без смысловой правки. Q8 уточнён: joinedload при LIMIT/OFFSET сохраняет набор родителей через подзапрос, но требует unique() для коллекции; MCQ B1 добавлен. TOC, sidecar и инвентарь — 8 Q/13 блоков. Structure/Markdown/parity/style PASS, skel pre-check без hard, с предупреждениями; все MCQ авторски сверены. Локально проверен Q4 на Python 3.14.4; интеграционные сценарии DB и независимая MCQ-приёмка ожидаются. |
| P3 / КАНДИДАТ, **RC** | [PostgreSQL 19 RC 1](https://www.postgresql.org/about/news/postgresql-19-rc-1-released-3409/), [черновик release notes](https://www.postgresql.org/docs/19/release-19.html) | `databases/postgresql-interview.md` Q32/Q44–Q46 | После GA проверить `DEEPEN` Q32 о встроенном `REPACK` против `VACUUM FULL`/`pg_repack` и возможный Q о `WAIT` для read-your-writes на standby. На 02.11 официальный анонс RC 1 планирует RC 2; до GA не описывать предложения как уже выпущенные функции PostgreSQL 19. |

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
| P1 · `DEEPEN` · Q22–Q25 П | Новый `go-database-interview.md` отклонён: `programming-languages/go/go-stdlib-interview.md` Q22–Q25 уже покрывают `sql.DB`, пул, prepared statements и транзакции. Пробелы точечные: `QueryContext`/отмена, `Rows.Err`, `DB.Stats()` при ожидании пула. | [Go database guide](https://go.dev/doc/database/), [cancel operations](https://go.dev/doc/database/cancel-operations), [manage connections](https://go.dev/doc/database/manage-connections). Q22–Q25 дополнены и прошли авторскую полную проверку; новый файл только при нескольких новых неповторяющихся сценариях. |
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
| P2 · `NEW_TOPIC`, выполнено · П | 7. Distributed Unique ID Generator → `system-design/design-unique-id-generator-interview.md` Q1–Q4 | `system-design-interview.md` Q7, `design-url-shortener-interview.md` Q19 и SQL Q13 не раскрывали выбор генератора и сбои вне URL-кода. Q19 точечно исправлен. | Q1–Q4 прошли авторскую `П`: Q2 уточняет commit `nextval` до внешней публикации ID и аварийный сброс `UNLOGGED` sequence; Q4 — встроенную `uuidv7()` в PostgreSQL 18. Добавлен Q2 B1, Q2/Q4 B0 синхронизированы. Адресная структура PASS, независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`; старый URL Shortener full pre-check остаётся отдельным риском. |
| P2 · `NEW_TOPIC`, выполнено · П | 10. Notification System → `system-design/design-notification-system-interview.md` Q1–Q4 | `system-design-interview.md` Q10 уже даёт архитектуру каналов, preferences, очередей и retry; новый файл раскрывает четыре независимых эксплуатационных сценария. | Q1 различает агрегаты FCM и индивидуальные сигналы, Q2 добавляет SES message tags для корреляции, Q3 проверен без правки, Q4 различает `INVALID_ARGUMENT` и недействительный токен. Авторская полная `П` по официальным FCM/APNs/SES источникам; production MCQ 4 Q/6 блоков, структура PASS, независимая содержательная/blind-приёмка `NOT_RUN`. AndroidConfigV2 на 04.11.2026 — preview. |
| P2 · `NEW_TOPIC` отклонён, `DEEPEN`/`ADD_Q` выполнены · П | 19. Distributed Message Queue → RabbitMQ Q40–Q42, Kafka Q42, сравнение брокеров Q15, event-driven Q3 | Kafka Q6–Q24 и RabbitMQ Q18–Q30 уже покрывают проектные механизмы; отдельная шпаргалка стала бы повтором. Обнаружен реальный пробел: сравнения отрицали RabbitMQ Streams и replay. | Авторская `П` перечисленных Q выполнена 05.11.2026: RabbitMQ Q42 добавляет старт consumer без offset, ограничение retention и проверку первой доступной позиции; в MCQ добавлен Q42 B1. Сверены официальные RabbitMQ 4.4, Kafka 4.3/4.4 и Java Stream Client 1.12. Адресная структура RabbitMQ PASS; full pre-check старых файлов остаётся FAIL, независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`. |
| P2 · `NEW_TOPIC`, выполнено · П | 22. Hotel Reservation System → `system-design/design-reservation-system-interview.md` Q1–Q5 | `design-ecommerce-delivery-interview.md` Q9 покрывает резерв товара с TTL, `design-payment-system-interview.md` Q4/Q7/Q17 — деньги, `database-transactions-interview.md` Q14 — блокировки. Не было интервала ночей, назначения одной комнаты и согласования остатков с OTA/PMS. | Q1–Q4 проверены; Q4 уточняет область Stripe API v1. Добавлен Q5: идемпотентный импорт внешних броней, подтверждение после записи, синхронизация доступности и разбор конфликта. Сверены PostgreSQL 18, Stripe и Booking.com Connectivity; авторская `П` Q1–Q5 и пяти MCQ-блоков, независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`. |
| P2 · `NEW_TOPIC`, выполнено · П | 24. S3-like Object Storage → `system-design/design-object-storage-interview.md` Q1–Q4 | `design-dropbox-interview.md` Q5–Q7/Q18 уже покрывает CAS, metadata/blob и Magic Pocket; `data-lake-lakehouse-interview.md` Q5 — сравнение с HDFS; `cloud/aws-interview.md` Q9/Q12 — обзор S3. Не было сквозного контракта объектного API и multipart. | Q1: PUT/GET/LIST, условная перезапись и версии; Q2: публикация metadata после bytes; Q3: multipart, checksum и abort; Q4: домены отказа и repair. AWS S3 и Ceph Squid сверены; авторская полная `П` Q1–Q4 и пяти MCQ-блоков выполнена 04.11.2026. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`. |
| P2 · `NEW_TOPIC`, выполнено · П | 25. Real-time Gaming Leaderboard → `system-design/design-leaderboard-interview.md` Q1–Q5 | `databases/redis-interview.md` Q2/Q11 покрывает Sorted Set, `stream-processing-interview.md` Q5–Q10 — опоздавшие события; не было продукта рейтинга с правилами мест и сезонов. | Q1–Q5 прошли авторскую `П`: Q1 уточняет точность score Redis, Q3 — надёжную проекцию из журнала и защиту версии; Q2/Q4/Q5 без правки. Production MCQ 5 Q/7 блоков проверены автором; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`. |
| P3 · `NEW_TOPIC`, выполнено · П | 20. Metrics Monitoring and Alerting System → `system-design/design-metrics-platform-interview.md` Q1–Q4 | `system-design-interview.md` Q23 и `monitoring/prometheus-grafana-interview.md` Q3/Q16/Q19–Q24/Q35–Q37 покрывают инструменты, cardinality и alerting одного стека; отсутствовала общая платформа для нескольких tenant. | Q1: Mimir tenant header не является аутентификацией; доверенный proxy проверяет отправителя и устанавливает ID на записи и чтении. Q2: один активный Thanos Compactor на поток блоков, `thanos_compact_halted` и задержка retention. Q3–Q4 сверены без правки. Авторская `П` Q1–Q4 и пяти MCQ-блоков выполнена 05.11.2026; адресная структура PASS, независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`. |
| P3 · `NEW_TOPIC`, выполнено · О | 21. Ad Click Event Aggregation → `system-design/design-event-aggregation-interview.md` Q1–Q4 | `data-engineering/stream-processing-interview.md` Q5–Q10/Q14–Q16 покрывает механику окон и повторов; `design-url-shortener-interview.md` Q14 — счётчик переходов; `message-brokers-comparison-interview.md` Q25 — выбор брокера. Рекламная идентичность, атрибуция и расчёт не раскрыты. | Q1: event ID и повторная доставка против недействительного трафика; Q2: provisional окна и коррекции; Q3: поздняя конверсия/окно атрибуции; Q4: settled начисления и сверка. Сверены [Flink 2.0](https://nightlies.apache.org/flink/flink-docs-release-2.0/docs/learn-flink/streaming_analytics/), [Google Ads API](https://developers.google.com/google-ads/api/docs/conversions/legacy_oci_guide) и [invalid traffic](https://support.google.com/google-ads/answer/11182074?hl=en); MCQ structure/parity/pre-check PASS; авторская полная `П` Q1–Q4 и 7 MCQ-блоков выполнена 04.11.2026, независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`. |
| P3 · `NEW_TOPIC`, выполнено · П | 23. Distributed Email Service → `system-design/design-email-service-interview.md` Q1–Q4 | `system-design-interview.md` Q10 уже задаёт общую очередь email; `design-notification-system-interview.md` Q1–Q4 покрывает статусы, дубли и общую классификацию ошибок. Не было почтовых квот, suppression, доменной аутентификации и репутации нескольких отправителей. | Q1: квоты и приоритеты; Q2: bounce/complaint и suppression; Q3: SPF/DKIM/DMARC и доставляемость; Q4: tenant/IP-репутация. Сверены [SES quotas](https://docs.aws.amazon.com/ses/latest/dg/manage-sending-quotas.html), [suppression](https://docs.aws.amazon.com/ses/latest/dg/sending-email-suppression-list.html), [tenants](https://docs.aws.amazon.com/ses/latest/dg/tenants.html) и [Gmail guidelines](https://support.google.com/mail/answer/81126?hl=en); MCQ structure/parity/pre-check PASS; авторская полная `П` Q1–Q4 и 7 MCQ-блоков выполнена 04.11.2026, независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`. |
| P3 · `NEW_TOPIC`, выполнено · П | 27. Digital Wallet → `system-design/design-digital-wallet-interview.md` Q1–Q4 | Payment System Q5/Q8/Q22, Database Transactions Q10/Q14 и CQRS/Event Sourcing Q24/Q30–Q31 сверены по заголовкам и точечным ответам; отдельный контракт внутреннего перевода остаётся оправдан. | Авторская полная `П` Q1–Q4 выполнена 05.11.2026: Q2 разделяет потерянный ответ и подтверждённый `exceeds_credits`, описывает `id_already_failed` для TigerBeetle клиента 0.16.4+ и поведение старых клиентов. Q1/Q3/Q4 проверены без смысловой правки. Production MCQ Q2 B0 синхронизирован, B1 добавлен; всего 4 Q/5 блоков, автором проверены A/B/C/D и sections. Адресная структура PASS; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`. |
| P3 · `NEW_TOPIC`, авторская П | 28. Stock Exchange → `system-design/design-exchange-interview.md` Q1–Q4 | Payment System Q5/Q17 и Digital Wallet Q1–Q4 покрывают ledger и повтор денежной операции, но не цену/время в стакане, гонку исполнения с отменой и восстановление рыночного фида. Профиль расширен на system design; отдельная тема имеет четыре неповторяющихся сценария. | Q1 — FIFO при заданном правиле рынка; Q2 — порядок команд, отмена и восстановление; Q3 — Binance snapshot/`U..u` и разрыв; Q4 — matching, рыночные проекции и FIX ExecutionReport. Источники: [CME](https://www.cmegroup.com/education/articles-and-reports/overview-what-makes-ags-markets-work), [FIX](https://www.fixtrading.org/wp-content/uploads/download-manager-files/FIX-Latest-Specification-Trade.pdf), [Binance Spot](https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md) и [изменение 06.05.2026](https://github.com/binance/binance-spot-api-docs/blob/master/CHANGELOG.md#2026-05-06). Авторская полная проверка Q1–Q4 выполнена; Q2 углублён устойчивой связью ID с результатом и новым MCQ B1, Q4 исправлен по цепочке `ExecRefID`/`ExecID`. Структура 4 Q/5 блоков PASS; независимая содержательная приёмка ожидается. |
| P3 · `DEEPEN`, Uber Q1–Q30 · П; Maps Q27 · О | 16–17. Proximity Service / Nearby Friends | `design-uber-interview.md` Q6/Q7/Q12–Q14 уже покрывает поток координат и геоиндекс, `design-google-maps-interview.md` Q27 — совместный доступ, Q19 — POI. Два новых файла повторили бы эти ответы. | Uber Q6/Q7 исправлены: PostgreSQL/PostGIS умеет индексированный `ST_DWithin`, выбор Redis/H3 зависит от нагрузки; удалено категоричное «SQL не годится». Maps Q27 углублён: `GEOSEARCH` даёт кандидатов, право дружбы/согласие/отзыв проверяются на сервере для каждого чтения, большой граф проверяется по кандидатам, `COUNT ANY` до авторизации не равен лимиту видимых друзей. Сверены [PostGIS ST_DWithin](https://postgis.net/docs/ST_DWithin.html), [Redis GEOSEARCH](https://redis.io/docs/latest/commands/geosearch/), [OWASP Authorization](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html). JSON Q6/Q7/Q27 синхронизированы; обе пары structure/parity/pre-check PASS. Авторская точечная проверка выполнена, blind review ожидается; Uber Q1–Q30 прошли авторскую `П` 05.11.2026: уточнены геоиндексы, поиск/назначение, цена, outbox поездки, ETA, платёжный поток, региональное владение и ключи разделения; удалены неподтверждённые продуктовые числа и обязательный стек Uber. MCQ Q1–Q30 синхронизированы, независимая приёмка `NOT_RUN`. |

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
| P4 | `ADD_Q` выполнено · О | `ai-ml/ai-application-architecture-interview.md` Q8/Q11/Q19/Q21/Q26 и `llm-integration-patterns-interview.md` Q12/Q14–Q16/Q23–Q25/Q28 покрывают части сценария | В Q33 собраны оценка по запросам и токенам, предел одновременных вызовов, конечная очередь с дедлайном, явный отказ и метрики перегрузки. Сверены [OpenAI rate limits](https://developers.openai.com/api/docs/guides/rate-limits), [Claude rate limits](https://platform.claude.com/docs/en/api/rate-limits), [vLLM metrics](https://docs.vllm.ai/en/latest/usage/metrics/) и [Kubernetes HPA](https://kubernetes.io/docs/concepts/workloads/autoscaling/horizontal-pod-autoscale/). | Теория, TOC, production MCQ Q33 B0, freshness-sidecar и инвентарь синхронизированы. Авторская точечная проверка A/B/C/D выполнена; полная `П` и независимая MCQ-приёмка ожидаются. Общие parity/skel старого файла FAIL; Q33 skel без замечаний. |
| P4 | `DEEPEN` выполнено · О | `ai-ml/llm-integration-patterns-interview.md` Q14 рядом с Q15/Q16 и Architecture Q33 | Q14 неверно предлагал считать только выходные токены и показывал лишь локальное ведро по вызовам. Уточнены совместный допуск по RPM и применимым токенным бюджетам, оценка до вызова и сверка с `usage`, общая атомарно обновляемая квота. Сверены [OpenAI rate limits](https://developers.openai.com/api/docs/guides/rate-limits) и [Claude rate limits](https://platform.claude.com/docs/en/api/rate-limits). | Q14 B0 и freshness-sidecar синхронизированы; структура и Markdown PASS, локальный skel без hard/warn. Общие parity/skel файла FAIL из-за других Q; A/B/C/D и sections точечно прочитаны автором. Полная `П` и независимая приёмка ожидаются. |

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
Go stdlib: Q1–Q14 и Q22–Q31 прошли авторскую `П` по документации Go,
release notes и официальному блогу Go; 31 Q / 34 блока, структура PASS, parity всего файла
FAIL (9/34 correct длиннее всех wrong; 10 блоков вне коридора длины).
Q1–Q14 B0, Q4/Q14/Q31 B1 без жёстких локальных сигналов после правок;
независимого blind review нет. Go 1.27 локально не установлен, код Q31 не выполнялся.
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

**Контрольная точка 08.10.2026 — Spring Data JPA Q34 B0,
Integration Testing Q4/Q36 B0, Testcontainers Q5 B0 · О.**
Просмотр заголовков (`З`) показал, что `@DataJpaTest` и контейнерная БД уже
описаны в этих трёх файлах; отдельная тема не нужна. Точечная проверка (`О`)
по [Spring Boot 3.5.16, `AutoConfigureTestDatabase`](https://docs.spring.io/spring-boot/3.5/api/java/org/springframework/boot/test/autoconfigure/jdbc/AutoConfigureTestDatabase.html),
[Spring Boot 4.1.1, `Replace.NON_TEST`](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/jdbc/test/autoconfigure/AutoConfigureTestDatabase.Replace.html),
[`@DataJpaTest`](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/data/jpa/test/autoconfigure/DataJpaTest.html),
[test slices](https://docs.spring.io/spring-boot/appendix/test-auto-configuration/slices.html)
и [Testcontainers в Spring Boot](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html)
подтвердила устаревшее требование всегда ставить `replace = NONE`.
Начиная с Boot 3.4, default `NON_TEST` сохраняет автонастроенное
тестовое подключение через `@DynamicPropertySource`, `@ServiceConnection`
или `jdbc:tc:`. Указаны границы: обычный/собственный `DataSource`, версия
Boot, наличие `spring-boot-testcontainers` для `@ServiceConnection`.
Уточнены rollback, условность миграционной автоконфигурации и отсутствие
обоснования для обещанного времени запуска slice-теста. Теория, четыре MCQ B0
и три freshness-sidecar синхронизированы. Structure PASS: 123 Q/124 блока
по трём JSON; `verify-md-no-mcq` PASS; локально четыре блока без hard/warnings.
Полный parity всех трёх файлов FAIL на прежних блоках. Полной проверки (`П`),
runtime-прогона примеров и независимой blind-приёмки MCQ нет. Коммит
`50f305e46`. Инвентарь не менялся: состав файлов прежний.

**Контрольная точка 08.10.2026 — Testcontainers Q2/Q6 B0,
зависимости и контейнерный рантайм · О.** Просмотр заголовков (`З`)
показал пересечение Q2 с требованиями среды и Q6 с каталогом модулей;
отдельный вопрос не нужен. Точечно (`О`) сверены
[Testcontainers 2.0 release](https://github.com/testcontainers/testcontainers-java/releases/tag/2.0.0),
[JUnit 5 quickstart](https://java.testcontainers.org/quickstart/junit_5_quickstart/),
[runtime requirements](https://java.testcontainers.org/supported_docker_environment/),
[сборка 2.0.5 с `--release 8`](https://raw.githubusercontent.com/testcontainers/testcontainers-java/2.0.5/build.gradle)
и [страницы модулей БД](https://java.testcontainers.org/modules/databases/).
В 2.x координаты получили префикс `testcontainers-`, а классы переехали
в пакеты модулей. Q2 теперь показывает согласованные зависимости 2.0.5
и Docker API-совместимые варианты рантайма; Q6 приводит координаты
10 модулей 2.0.5 и отмечает границу с 1.x и отдельный JDBC-драйвер.
Теория, Q2/Q6 MCQ B0 и freshness-sidecar синхронизированы. Structure
40 Q/40 блоков PASS, Markdown gate PASS, локальные Q2/Q6 без
hard/warnings; полный parity файла FAIL на прежних блоках. Полной
проверки (`П`), runtime-прогона альтернативных сред и независимой
blind-приёмки MCQ нет. Коммит `a2f01ccc2`; инвентарь не менялся.

**Контрольная точка 08.10.2026 — Testcontainers Q28 B0,
Elasticsearch · О.** Заголовки Q9/Q11/Q28 просмотрены (`З`): новый
вопрос не нужен. По [модулю Elasticsearch 2.0.5](https://java.testcontainers.org/modules/elasticsearch/),
[его исходному коду](https://raw.githubusercontent.com/testcontainers/testcontainers-java/2.0.5/modules/elasticsearch/src/main/java/org/testcontainers/elasticsearch/ElasticsearchContainer.java),
[Spring Boot Testcontainers](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html)
и [Elastic near real-time search](https://www.elastic.co/docs/manage-data/data-store/near-real-time-search)
точечно (`О`) исправлены координата `testcontainers-elasticsearch`,
пакет класса и пример URI через `getHttpHostAddress()`. Исходный код
подтвердил, что `discovery.type=single-node` уже задаёт контейнер;
для Elasticsearch 8+ уточнены security/HTTPS и refresh перед
немедленной проверкой поиска. Теория, Q28 MCQ B0 и freshness-sidecar
синхронизированы; structure 40 Q/40 блоков PASS, Markdown gate PASS,
Q28 без локальных hard/warnings. Полный parity файла FAIL на прежних
блоках; полного аудита (`П`), запуска ES-контейнера и независимой
blind-приёмки MCQ нет. Коммит `81270a2f6`.

**Контрольная точка 08.10.2026 — Testcontainers Kafka Q9/Q15/Q16/Q17/Q32
B0 · О.** Заголовки смежных Q просмотрены (`З`), затем ответы и MCQ
проверены точечно (`О`). По [модулю Kafka 2.0.5](https://java.testcontainers.org/modules/kafka/)
и [исходному коду ConfluentKafkaContainer](https://raw.githubusercontent.com/testcontainers/testcontainers-java/2.0.5/modules/kafka/src/main/java/org/testcontainers/kafka/ConfluentKafkaContainer.java)
исправлена пара `confluentinc/cp-kafka` → `ConfluentKafkaContainer`,
отделён новый `KafkaContainer` для образов Apache и deprecated класс
старого пакета. По [Spring TestContext](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/dynamic-property-sources.html)
уточнены владелец `@DynamicPropertySource`, ленивое чтение `Supplier`
и риск кеширования контекста между подклассами; по [JUnit 5 Extension](https://java.testcontainers.org/test_framework_integration/junit_5/)
исправлено ложное обещание singleton для унаследованного `static @Container`.
Теория, MCQ Q9/Q15/Q17/Q32 B0 и freshness-sidecar синхронизированы;
Q16 изменён только в теории. Structure 40 Q/40 блоков PASS, Markdown
gate PASS, локальные четыре B0 без hard/warnings. Полный skel/parity
файла FAIL на прежних блоках; полного аудита (`П`), запуска Kafka
контейнера и независимой blind-приёмки MCQ нет. Коммит `8b0d71b4e`.

**Контрольная точка 08.10.2026 — Testcontainers MongoDB Q11 B0 · О.**
По [модулю MongoDB 2.0.5](https://java.testcontainers.org/modules/databases/mongodb/),
[исходному коду контейнера](https://raw.githubusercontent.com/testcontainers/testcontainers-java/2.0.5/modules/mongodb/src/main/java/org/testcontainers/mongodb/MongoDBContainer.java)
и [документации транзакций MongoDB](https://www.mongodb.com/docs/v8.0/data-modeling/enforce-consistency/transactions/)
исправлены координата/пакет и ошибочное обещание replica set по
умолчанию: нужен явный `withReplicaSet()`. Уточнено, что
`getReplicaSetUrl()` возвращает адрес с именем БД, но не включает режим
и не добавляет параметр `replicaSet`. Теория, MCQ Q11 B0 и freshness-
sidecar синхронизированы; Q6/Q15/Q16 просмотрены на пересечение.
Structure 40 Q/40 блоков PASS, Markdown gate PASS, Q11 без локальных
hard/warnings. Полный skel/parity файла FAIL на старых блоках; полного
аудита (`П`), запуска MongoDB-контейнера и независимой blind-приёмки
MCQ нет. Коммит `7a3aa1b19`; инвентарь не менялся.

**Контрольная точка 08.10.2026 — Spring Data JPA Q35 B0 · О.**
По [Spring Data JPA 4.1.1 Specifications](https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html),
[projections](https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html),
[transactions](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html)
и [Hibernate ORM 7.4](https://docs.hibernate.org/orm/7.4/userguide/html_single/)
заменён ложный пример «проекции» после `findAll(...).map(...)` на
проекцию через fluent `findBy(..., q -> q.as(...).page(...))`.
Неподтверждённый `bulkUpdatePrices(Map)` как «один SQL» удалён;
показан реализуемый метод `@Query` с планом загрузки для одной
сущности. Уточнены границы LAZY/EAGER, JDBC batching, readOnly,
OSIV, L2 и измерения SQL. Теория, Q35 MCQ B0 и freshness-sidecar
синхронизированы. Structure 43 Q/44 блока PASS, Markdown gate PASS,
Q35 без локальных hard/warnings. Полный skel/parity файла FAIL на
старых блоках; полного аудита (`П`), компиляции иллюстративного
примера и независимой blind-приёмки MCQ нет. Коммит `52aed25b9`.

**Контрольная точка 08.10.2026 — Spring Data JPA Q36 B0 · О.**
По [query methods](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
и [keyword reference](https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html)
исправлен комментарий к `deleteByDeletedAtIsNotNull()`: derived
delete выбирает сущности и удаляет их по одной с callbacks, а не
выполняет один bulk `DELETE`. Уточнены `Top`/`First`, `OrderBy` и
граница с Q33. Теория, Q36 MCQ B0 и freshness-sidecar синхронизированы.
Structure 43 Q/44 блока PASS, Markdown gate PASS, Q36 без локальных
hard/warnings; полный parity файла FAIL на старых блоках. Полной
проверки (`П`), runtime/compile-прогона примеров и независимой
blind-приёмки MCQ нет. Коммит `46510e35f`.

**Контрольная точка 02.11.2026 — Spring Data JPA Q37 B0 · P2 ·
`DEEPEN` · О.** Сопоставлены Q17/Q35 и ответ Q37 (`З`, затем `О`).
В примере `Product.category` — связь с `Category`, поэтому фильтр по
имени переведён со сравнения связи со строкой на `root.join("category")`
и её поле `name`. Для собственного `CriteriaBuilder.like` добавлены
явное экранирование `%`/`_` через `EscapeCharacter` и оговорка о
границах этого метода. Уточнено, что строковые имена полей Criteria
API не проверяются компилятором без статической метамодели.
Источники: [Spring Data JPA Specifications 4.1.2](https://docs.spring.io/spring-data/jpa/reference/jpa/specifications.html),
[EscapeCharacter API 4.1.1](https://docs.spring.io/spring-data/jpa/reference/api/java/org/springframework/data/jpa/repository/query/EscapeCharacter.html),
[Jakarta Persistence 3.2 LIKE](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2.html).
Теория, MCQ Q37 B0 и freshness-sidecar синхронизированы. Structure
43 Q/44 блока PASS, Markdown gate PASS, локальный Q37 без
hard/warnings; полный parity файла FAIL на прежних блоках. Нет
полного содержательного аудита (`П`), компиляции примера и
независимой blind-приёмки MCQ. Инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Spring Data JPA Q38 B0 · P2 ·
`DEEPEN` · О.** Заголовки Q13/Q14/Q35 и ответ Q38 сопоставлены (`З`,
затем `О`). Пример `UserSummary` содержал `@Value`/SpEL с `target`,
но обещал узкий `SELECT`; открытая проекция этого не гарантирует.
По [Spring Data JPA 4.1.2 Projections](https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html)
пример заменён закрытой проекцией с `default`-методом. Разделены
закрытая/открытая проекции, DTO и динамический выбор результата;
убраны безусловные заявления о точном SQL и превосходстве DTO по
скорости. Добавлена граница вложенных свойств через `JOIN` и ссылки на
Q13/Q14. Теория, MCQ Q38 B0 и freshness-sidecar синхронизированы.
Structure 43 Q/44 блока PASS, Markdown gate PASS, локальный Q38 без
hard/warnings, JSON sidecar и diff-check PASS. Полный parity файла
FAIL на старых блоках. Полного содержательного аудита (`П`),
runtime/SQL-проверки и независимой blind-приёмки MCQ нет. Инвентарь
не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Spring Data JPA Q40 B0 · P2 ·
`DEEPEN` · О.** Заголовки и ответы Spring Data JPA Q34/Q40 и Flyway Q10
сопоставлены (`З`, затем `О`). По [Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/how-to/data-initialization.html),
[Spring Boot 4.1.2](https://docs.spring.io/spring-boot/how-to/data-initialization.html),
[Boot Data Access](https://docs.spring.io/spring-boot/how-to/data-access.html),
[Flyway baselineOnMigrate](https://documentation.red-gate.com/flyway/reference/configuration/flyway-namespace/flyway-baseline-on-migrate-setting)
и [Boot Replace](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/jdbc/test/autoconfigure/AutoConfigureTestDatabase.Replace.html)
убраны универсальные советы включать `baseline-on-migrate: true` и
ставить `Replace.NONE` в каждом `@DataJpaTest`. Уточнено, что
автоконфигурация запускает Flyway до JPA, `ddl-auto` с Flyway по
умолчанию `none`, а `validate` выбирают явно. Для тестовой БД описан
`NON_TEST` (Boot 3.4+) и отдельная тестовая миграция; границы
нескольких `DataSource` оставлены явными. Теория, MCQ Q40 B0 и
freshness-sidecar синхронизированы. Structure 43 Q/44 блока PASS,
Markdown gate PASS, локальный Q40 без hard/warnings, JSON sidecar и
diff-check PASS. Полный parity файла FAIL на других блоках. Полной
содержательной проверки (`П`), runtime-прогона и независимой
blind-приёмки MCQ нет. Инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Spring Data JPA Q41 B0 · P2 ·
`DEEPEN` · О.** Сопоставлены Q15/Q24/Q35/Q41 (`З`, затем `О`).
Q24 уже корректно отделял граф загрузки от SQL, а Q41 и MCQ ошибочно
обещали только `LEFT JOIN`, отдельные SELECT для нескольких коллекций
и правильную пагинацию через `@EntityGraph` с `countQuery`.
По [Spring Data JPA 4.1.2](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.entity-graph),
[Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2.html),
[Hibernate 7.4 User Guide](https://docs.hibernate.org/orm/7.4/userguide/html_single/),
[MultipleBagFetchException API](https://docs.hibernate.org/orm/7.4/javadocs/org/hibernate/loader/MultipleBagFetchException.html)
и [Hibernate 7.4 What's New](https://docs.hibernate.org/orm/7.4/whats-new/)
разделены план графа, явный `JOIN FETCH`, фактический SQL и count.
Учтено изменение пагинации collection fetch join в Hibernate 7.4
на поддерживаемых БД; граф сам по себе не гарантирует этот SQL.
Теория, MCQ Q41 B0 и freshness-sidecar синхронизированы. Structure
43 Q/44 блока PASS, Markdown gate PASS, локальный Q41 без
hard/warnings, JSON sidecar и diff-check PASS. Полный parity файла
FAIL на других блоках. Полной содержательной проверки (`П`),
runtime/SQL-прогона и независимой blind-приёмки MCQ нет. Инвентарь
не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Spring Data JPA Q42 B0 · P2 ·
`DEEPEN` · О.** Сопоставлены Q8/Q33/Q42 (`З`, затем `О`).
По [Spring Data JPA 4.1.2 `@Modifying`](https://docs.spring.io/spring-data/jpa/reference/api/java/org/springframework/data/jpa/repository/Modifying.html),
[query methods](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.modifying-queries),
[transactions](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html),
[Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2.html)
и [Hibernate HQL 7.4](https://docs.hibernate.org/orm/7.4/querylanguage/html_single/)
уточнены область `@Modifying` для объявленного `@Query`, отдельная
граница транзакции, JPQL `UPDATE`/`DELETE` против нативного/HQL
`INSERT`, flush до и clear всего контекста после bulk-запроса.
Убрано обещание одного физического SQL; исправлен нативный пример,
который только вставлял аудит, хотя метод назывался обновлением цены.
Теория, MCQ Q42 B0 и freshness-sidecar синхронизированы. Structure
43 Q/44 блока PASS, Markdown gate PASS, локальный Q42 без
hard/warnings, JSON sidecar и diff-check PASS. Полный parity файла
FAIL на других блоках. Полной содержательной проверки (`П`),
runtime/SQL-прогона и независимой blind-приёмки MCQ нет. Инвентарь
не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Spring Data JPA Q43 B0 · P2 ·
`DEEPEN` · О.** Сопоставлены заголовки и ответы Q15/Q16/Q43 (`З`, затем
точечная проверка `О`). По [Spring Data JPA 4.1.2, Scrolling](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#scrolling),
[Query Methods Details](https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html)
и [PostgreSQL, Transaction Isolation](https://www.postgresql.org/docs/current/transaction-iso.html)
исправлены утверждения о замене `Pageable` и гарантированно стабильной
скорости. Уточнены исключительная позиция, все ключи сортировки и PK,
ненулевые значения, доступность ключей в проекции, роль индекса и
влияние конкурентных изменений. Пример не обращается к последнему
элементу пустого окна. Теория, MCQ Q43 B0 и freshness-sidecar
синхронизированы. Structure 43 Q/44 блока, Markdown gate, локальный
Q43 без hard/warnings, sidecar JSON и diff-check PASS. Полный skel и
parity файла FAIL на других блоках. Полной содержательной проверки
(`П`), runtime/SQL-прогона и независимой blind-приёмки MCQ нет.
Инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Spring Batch Q6 B0/B1 · P2 ·
`DEEPEN` · О.** Сопоставлены Q4/Q5/Q6/Q8 и MCQ B0/B1.
По [Spring Batch 6, JobRepository](https://docs.spring.io/spring-batch/reference/job/configuring-repository.html),
[Spring Batch 5.2, новые реализации](https://docs.spring.io/spring-batch/reference/5.2/whatsnew.html),
[ItemStream](https://docs.spring.io/spring-batch/reference/readers-and-writers/item-stream.html),
[FlatFileItemReaderBuilder 6.0](https://docs.spring.io/spring-batch/reference/api/org/springframework/batch/infrastructure/item/file/builder/FlatFileItemReaderBuilder.html),
[Spring Boot 4.1](https://docs.spring.io/spring-boot/reference/io/spring-batch.html)
уточнено: долговечный `JobRepository` нужен для перезапуска, но не
гарантирует восстановление позиции каждого reader/writer. Для этого
нужно сохраняемое состояние `ItemStream`; `saveState(false)` у
`FlatFileItemReader` отключает его. Исправлен B0 A и объяснения B0;
B1 проверен без изменения. Теория, JSON и freshness-sidecar
синхронизированы. Structure 43 Q/44 блока, Markdown, parity, skel,
JSON sidecar и diff-check PASS. Общий `gates.sh` FAIL: есть старый
`NEIGHBOR-LEAK` Q15 и другие сигналы; Q6 B1 `NOVEL-TERM-WRONG`
по `ItemReader` проверен по смыслу — термин не является подсказкой к
ключу, а опровергается примером Q6 с CSV и MongoDB.
SFPL/ANSC/RU по Q6 B0/B1 просмотрены автором, независимых фаз A/B и
`П` нет. Подготовлен [blind-input](docs/mcq-quality/runs/20261102-spring-batch-q6-b0-b1-r1/blind-input.jsonl)
с manifest для нового независимого контекста; `phase-a`, `reveal` и
`record` не выполнялись. Инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Spring Batch Q28 B0 · P2 ·
`DEEPEN` · О.** Сопоставлены Q6/Q28/Q38 (`З`, затем `О`). По
[Spring Boot 3.5 Batch Applications](https://docs.spring.io/spring-boot/3.5/how-to/batch.html),
[Boot 3.5 BatchAutoConfiguration](https://docs.spring.io/spring-boot/3.5/api/java/org/springframework/boot/autoconfigure/batch/BatchAutoConfiguration.html)
и [Spring Boot 4.1 Spring Batch](https://docs.spring.io/spring-boot/reference/io/spring-batch.html)
исправлено утверждение, что Boot запускает **все** `Job` при старте:
автозапуск действует для единственного `Job`, при нескольких нужно
указать `spring.batch.job.name`. Уточнено, что
`spring.batch.job.enabled=false` отключает запуск, а не инфраструктуру,
и что `@EnableBatchProcessing` выключает Boot-автоконфигурацию.
Теория, MCQ Q28 B0 и freshness-sidecar синхронизированы. Structure
43 Q/44 блока, Markdown, parity, skel, JSON sidecar и diff-check PASS.
Общий `gates.sh` FAIL из-за старого Q15 `NEIGHBOR-LEAK` и других
сигналов; формальный Q28 C «частей=1» проверен по тексту: вариант
содержит обе части (создание инфраструктуры и запуск Job), а неверность
в обязательности `@EnableBatchProcessing`. SFPL/ANSC/RU просмотрены
автором, независимых фаз A/B и полной проверки `П` нет.
Предыдущий blind-input Q6 B0/B1 от 02.11 теперь **устарел**: хеши
теории и JSON в его manifest не совпадают с рабочими файлами после
Q28. Не использовать его для phase-a/reveal; пересобрать пакет после
следующей правки Q38. Инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Spring Batch Q38 B0 · P2 ·
`DEEPEN` · О.** Сопоставлены Q6/Q28/Q38 (`З`, затем `О`). По
[Spring Batch 5.0](https://docs.spring.io/spring-batch/docs/5.0.1/reference/html/whatsnew.html),
[Spring Batch 5.2, Configuring a Step](https://docs.spring.io/spring-batch/reference/5.2/step/chunk-oriented-processing/configuring.html),
[Spring Batch 6](https://docs.spring.io/spring-batch/reference/whatsnew.html)
и [Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/how-to/batch.html)
уточнена граница между менеджером транзакций `JobRepository` и
менеджером `Step`. Если метаданные и результаты пишутся в разные БД,
разные менеджеры не образуют одну транзакцию: после сбоя возможна
повторная обработка; требуются идемпотентность либо отдельная общая
транзакционная координация. Теория, MCQ Q38 B0 и freshness-sidecar
синхронизированы. Structure 43 Q/44 блока, Markdown, parity, skel,
JSON sidecar и diff-check PASS; по Q38 локальных hard/warnings нет.
Общий `gates.sh` FAIL из-за прежнего `NEIGHBOR-LEAK` Q15 и других
сигналов. A/B/C/D и sections Q38 точечно прочитаны; SFPL/ANSC/RU
просмотрены автором. Независимых фаз A/B и полной проверки `П` нет.
Инвентарь не менялся. Коммита нет.

Заново подготовлены [blind-input](docs/mcq-quality/runs/20261102-spring-batch-q6-q28-q38-r1/blind-input.jsonl)
и manifest для Q6 B0/B1, Q28 B0, Q38 B0; оба хеша совпадают с текущими
теорией и JSON. Прежний пакет `20261102-spring-batch-q6-b0-b1-r1`
остаётся устаревшим, его не использовать. Независимый контекст должен
прочитать только новый blind-input, затем выполнить phase-a, reveal,
проверку CONTENT/FACTS/LINKAGE/SECTIONS/SFPL/ANSC/RU и record. Пока
этих evidence нет, блоки не `FINAL`; последующие правки Batch снова
потребуют пересборки пакета.

02.11 P4-кандидат ёмкости LLM-сервиса закрыт новым
`ai-ml/ai-application-architecture-interview.md` Q33. Существующие
Q8/Q11/Q19/Q21/Q26 и `llm-integration-patterns-interview.md`
Q12/Q14–Q16/Q23–Q25/Q28 просмотрены по заголовкам и точечно по ответам;
полная проверка этих Q не проводилась. Официальные источники: OpenAI
rate limits, Claude rate limits, vLLM Production Metrics, Kubernetes HPA.
Теория, MCQ B0, TOC, freshness-sidecar и инвентарь (33 Q/33 блока)
синхронизированы. JSON и Markdown валидны, у Q33 skel нет hard/warn;
общие parity и skel старого файла FAIL из-за других Q. Варианты
A/B/C/D и их пояснения точечно прочитаны автором, полная `П` и
независимая MCQ-приёмка не проводились. Коммита нет.

02.11 `DEEPEN` LLM Integration Patterns Q14 B0 завершён на уровне `О`.
Q14 сопоставлен с Q15/Q16 и Architecture Q33. По OpenAI/Claude исправлено
утверждение о подсчёте только выходных токенов: теперь описаны применимые
RPM/TPM либо RPM/ITPM/OTPM, предварительная оценка, сверка с `usage`
и общая атомарная квота нескольких инстансов. Старый учебный вызов
`bucket.consume(1)` убран как неполный. Теория, JSON Q14 B0 и
freshness-sidecar синхронизированы. Structure/Markdown PASS, Q14 skel
без hard/warn; общие parity/skel старого файла FAIL. A/B/C/D и sections
прочитаны автором, полная `П` и независимая MCQ-приёмка не проводились.
Инвентарь не менялся: 28 Q/28 блоков. Коммита нет.

02.11 P3 Python 3.15 закрыт одним версионным вопросом Q10 в
`programming-languages/python/python-interview.md`. В Q8 уже был явный
`encoding`, но изменение значения по умолчанию и миграция файлов не
объяснялись. Python concurrency Q1–Q9 покрывает другие механизмы.
По финальным Python 3.15 What's New, PEP 686 и `io` добавлены теория,
TOC, MCQ Q10 B0, freshness-sidecar; инвентарь теперь 10 Q/10 блоков.
Structure, Markdown и parity PASS. Локальный skel Q10 без hard/warn;
общий `gates.sh` FAIL на прежних Q1–Q9 (`NOVEL-TERM` Q7 и форма других
блоков). A/B/C/D и sections точечно проверены автором, полная `П` и
независимая MCQ-приёмка не проводились. Коммита нет.

02.11 Python Typing Q4 углублён на уровне `О` по Python 3.15
`TypedDict(closed=True)` и `extra_items=T`. Q1/Q4 и Python Core Q10
сопоставлены по заголовкам и точечно по ответам; полной `П` темы нет.
Сверены Python 3.15 `typing`, What's New, спецификация TypedDict и PEP 728.
MCQ Q4 B1 добавлен, B0 сохранён и перечитан; freshness-sidecar обновлён.
Structure, Markdown, parity и общий `gates.sh` PASS для 4 Q/5 блоков,
но это предварительные проверки. A/B/C/D и sections B1 прочитаны
автором, независимой MCQ-приёмки нет. Инвентарь не менялся: 4 Q.
Коммита нет.

02.11 P3 Python Core Q11 про explicit lazy import написан на уровне `О`.
Сопоставлены Core Q6/Q7/Q10 и Packaging Q1/Q4 (`З`, затем `О`): сценарий
отложенных import side effects и цены первого запроса там отсутствовал.
По финальным [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-810-explicit-lazy-imports)
и [PEP 810](https://peps.python.org/pep-0810/) добавлены теория, TOC и
production MCQ Q11 B0; freshness-sidecar и инвентарь теперь отражают
11 Q/11 блоков. Structure/Markdown/parity PASS, локальный skel Q11
без hard/warn; общий gates FAIL из-за прежних Q1–Q9. Варианты A/B/C/D
и sections точечно прочитаны автором. Полная `П` и независимая MCQ-приёмка
не проводились. Python 3.15 локально не установлен, пример проверен
по официальной документации. Коммита нет.

02.11 P3 Python Core Q5 углублён на уровне `О`. Сопоставлены Core Q5
и Typing Q1/Q4 (`З`, затем `О`): существующая фраза о sentinel не давала
примера для «аргумент не передан» против явного `None`. По
[Python 3.15 `sentinel()`](https://docs.python.org/3.15/builtins/functions.html#sentinel),
[PEP 661](https://peps.python.org/pep-0661/) и What's New добавлены
пример, граница Python 3.11–3.14 и проверка через `is`. Заголовок Q5,
TOC и JSON Q5 B0 синхронизированы; B1 добавлен. Freshness-sidecar
обновлён, инвентарь: 11 Q/12 блоков. Structure/Markdown/parity PASS,
локальный skel B1 без hard/warn; общий skel FAIL на прежних блоках,
включая Q5 B0. A/B/C/D и sections B1 точечно прочитаны автором.
Полная `П` и независимая MCQ-приёмка не проводились. Python 3.15
локально не установлен; пример сверен с официальной документацией.
Коммита нет.

02.11 P3 Python Core Q3 углублён на уровне `О`. Core Q3/Q4/Q5/Q9
сопоставлены по заголовкам и точечно по ответам: выбора `frozendict`
в них не было. По [Python 3.15 What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-814-add-frozendict-built-in-type),
[PEP 814](https://peps.python.org/pep-0814/) и документации built-in types
добавлены внешний immutable mapping, shallow copy, условная
хешируемость и граница `Mapping`/`dict`. Заголовок Q3, TOC и JSON
Q3 B0 синхронизированы; B1 добавлен. Freshness-sidecar и инвентарь
обновлены: 11 Q/13 блоков. Structure/Markdown/parity PASS; локальный
skel Q3 B1 без hard/warn, общий gates FAIL на прежних блоках
(`errors=1`, `warnings=9`, `manual_review=7`, skel `hard=8`).
A/B/C/D и sections B1 точечно прочитаны автором, полная `П` и
независимая MCQ-приёмка не проводились. Python 3.15 локально не
установлен; пример сверен по документации. Коммита нет.

02.11 P3 Python Core Q12 добавлен на уровне `О`. Общий profiling
Q1/Q2/Q14/Q25/Q27 и Python Core/Concurrency сопоставлены по заголовкам
и точечно по ответам: сценария attach к Python worker не было.
По [Python `profiling.sampling`](https://docs.python.org/3.15/library/profiling.sampling.html),
[`profiling.tracing`](https://docs.python.org/3.15/library/profiling.tracing.html)
и What's New добавлены `run`/`attach`/`dump`, wall/CPU, статистическая
граница, короткие процессы и права доступа. TOC/See also, JSON Q12 B0,
freshness-sidecar и инвентарь синхронизированы: 12 Q/14 блоков.
Structure/Markdown/parity PASS, локальный skel Q12 без hard/warn;
общий gates FAIL на прежних блоках (`errors=1`, `warnings=9`,
`manual_review=7`, skel `hard=8`). A/B/C/D и sections точечно
прочитаны автором, полная `П` и независимая MCQ-приёмка не проводились.
Python 3.15 локально не установлен; команда сверена по документации.
Коммита нет.

02.11 P3 Python Concurrency Q9 углублён на уровне `О`: по [Python 3.15
free-threading guide](https://docs.python.org/3.15/howto/free-threading-python.html)
и [PEP 803](https://peps.python.org/pep-0803/) добавлены проверка сборки,
runtime GIL после импортов, совместимость wheel и граница `abi3t`.
`python-packaging-interview.md` Q4 точечно сопоставлен; новой темы нет.
Production MCQ Q9 B1, freshness-sidecar и инвентарь синхронизированы:
9 Q/10 блоков. Structure/Markdown/parity PASS; локальный skel Q9 B1
без hard, с двумя предупреждениями формы. Общий gates FAIL на прежних
блоках (`skel hard=9`; длина вариантов PASS); B1 A/B/C/D и sections
авторски прочитаны, полной `П` и независимой MCQ-приёмки нет.
CPython 3.15 локально не запускался. Коммита нет.

02.11 P3 Python Packaging Q5 добавлен на уровне `О`. Q1–Q4 и Python Core
Q11 сопоставлены по заголовкам и ответам: установка и lazy-импорт были,
исполнения кода пакета до старта приложения не было.
По [Python 3.15 `site`](https://docs.python.org/3.15/library/site.html),
[What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-829-package-startup-configuration-files)
и [PEP 829](https://peps.python.org/pep-0829/) добавлены `.pth`, `.start`,
правило одноимённых файлов, порядок обработки и граница `-S`.
Production MCQ Q5 B0, freshness-sidecar, локальное оглавление, общий TOC
и инвентарь обновлены: 5 Q/5 блоков. Structure/Markdown/parity PASS,
локальный skel Q5 без hard/warn; A/B/C/D и sections проверены автором.
Полная `П` и независимая MCQ-приёмка не проводились. Локально есть
Python 3.14, поведение 3.15 сверено по документации. Коммита нет.

02.11 P3 Python Core Q12 углублён на уровне `О`. Q12 и общий
`performance/application-profiling-interview.md` Q1/Q2/Q35 сопоставлены:
встроенный sampling и JVM-примеры не объясняли native stack CPython.
По [What's New: PEP 831](https://docs.python.org/3.15/whatsnew/3.15.html#pep-831-frame-pointers-enabled-by-default),
[perf profiling HOWTO](https://docs.python.org/3/howto/perf_profiling.html),
[configure](https://docs.python.org/3.15/using/configure.html) и
[PEP 831](https://peps.python.org/pep-0831/) добавлены frame pointers,
отдельный perf map для Python-имён, границы custom build и расширений.
Production MCQ Q12 B1, sidecar и инвентарь синхронизированы:
12 Q/15 блоков. Structure/Markdown/parity PASS; локальный skel B1
без hard/warn, общий gates FAIL на прежних блоках. A/B/C/D и sections
проверены автором; полной `П` и независимой MCQ-приёмки нет.
Локально Python 3.14, Linux `perf` не запускался. Коммита нет.

02.11 P2 Python Database создан на уровне `О` после проверки FastAPI
Q2/Q3, Database Transactions Q33/Q34 и Python Packaging Q3. Три
самостоятельных сценария обосновали новый файл: параметризация DB-API,
транзакция SQLAlchemy Session и sync/async драйвер. Сверены [PEP 249](https://peps.python.org/pep-0249/),
[Python `sqlite3`](https://docs.python.org/3/library/sqlite3.html),
[SQLAlchemy Session Basics](https://docs.sqlalchemy.org/en/20/orm/session_basics.html),
[SQLAlchemy asyncio](https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html)
и [FastAPI async](https://fastapi.tiangolo.com/async/).
Написаны Q1–Q3, четыре MCQ-блока, sidecar; TOC и инвентарь — 3 Q/4 блока.
Структура, Markdown, parity и style-gate прошли. MCQ pre-check skel
не дал hard, но оставил предупреждения о форме вариантов; автор
точечно проверил правильные ответы и дистракторы. Полная `П`,
интеграционный тест с БД и независимая MCQ-приёмка не проводились.
Коммита нет.

02.11 P2 Python Database Q4 добавлен на уровне `О`: сравнение
Python Database Q2 и Database Transactions Q33/Q34 показало
отдельную границу `sqlite3.Connection`. Сверены
[Python 3.15 `sqlite3`](https://docs.python.org/3.15/library/sqlite3.html#how-to-use-the-connection-context-manager)
и [transaction control](https://docs.python.org/3.15/library/sqlite3.html#transaction-control).
Написаны Q4, MCQ B0 и свежесть фактов; TOC и инвентарь — 4 Q/5 блоков.
На Python 3.14.4 локально проверены успешный выход, исключение,
`autocommit=False/True` и то, что `with con` не закрывает соединение.
Structure/Markdown/parity/style PASS; skel pre-check без hard, с
предупреждениями на старых и новом блоках. Авторская точечная
проверка Q4 A/B/C/D и sections проведена. Полная `П` и независимая
MCQ-приёмка ожидаются. Коммита нет.

02.11 P2 Python Database Q5 добавлен на уровне `О`. Python Database
Q2/Q3, Database Transactions Q33/Q34 и FastAPI Q3 сверены по
заголовкам и ответам: в них не было границы долгоживущего SQLAlchemy
`Engine` и краткоживущей `Session`, момента получения соединения
и диагностики пула. Сверены [SQLAlchemy Engine usage](https://docs.sqlalchemy.org/en/20/core/connections.html#basic-usage),
[Session lifecycle](https://docs.sqlalchemy.org/en/20/orm/session_basics.html#when-do-i-construct-a-session-when-do-i-commit-it-and-when-do-i-close-it)
и [pool timeout](https://docs.sqlalchemy.org/en/20/errors.html#queuepool-limit-of-size-x-overflow-y-reached-connection-timed-out-timeout-z).
Написаны Q5 и MCQ B0; TOC, sidecar и инвентарь — 5 Q/6 блоков.
Structure/Markdown/parity/style PASS, MCQ skel pre-check без hard,
с предупреждениями по существующим вариантам. Авторская точечная
проверка Q5 A/B/C/D и sections проведена. Локальный SQLAlchemy
не установлен, пример с БД не запускался. Полная `П` и независимая
MCQ-приёмка ожидаются. Коммита нет.

02.11 P2 Python Database Q6 добавлен на уровне `О`. Сверены
Python Database Q2/Q5, FastAPI Q4 и Hibernate Q22 по заголовкам и
ответам. Сценарий SQLAlchemy `expire_on_commit` и отсоединения объекта
при `Session.close()` не был покрыт; Java-аналог не заменяет
Python-вопрос. По [SQLAlchemy Session Basics](https://docs.sqlalchemy.org/en/20/orm/session_basics.html),
[DetachedInstanceError](https://docs.sqlalchemy.org/en/20/errors.html#parent-instance-x-is-not-bound-to-a-session-lazy-load-deferred-load-refresh-etc-operation-cannot-proceed)
и [relationship loading](https://docs.sqlalchemy.org/en/20/orm/queryguide/relationships.html)
написаны Q6 и MCQ B0; TOC, sidecar и инвентарь — 6 Q/7 блоков.
Structure/Markdown/parity/style PASS, MCQ skel pre-check без hard,
с предупреждениями по существующим блокам. Авторская точечная
проверка Q6 A/B/C/D и sections проведена. SQLAlchemy локально не
установлен, пример с ORM не запускался. Полная `П` и независимая
MCQ-приёмка ожидаются. Коммита нет.

02.11 P2 Python Database Q7 добавлен на уровне `О`. Python Database
Q3/Q6, FastAPI Q1/Q4 и Python Concurrency Q1 сверены по заголовкам
и ответам. В них не было сценария неявного SQL I/O при чтении
ORM-атрибута через открытую `AsyncSession`. По
[SQLAlchemy asyncio](https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html#preventing-implicit-io-when-using-asyncsession)
и [MissingGreenlet](https://docs.sqlalchemy.org/en/20/errors.html#missinggreenlet)
написаны Q7 и MCQ B0: `selectinload` выполняет I/O в ожидаемом
запросе; `AsyncAttrs.awaitable_attrs` доступен с 2.0.13 для явного
ожидания lazy-атрибута. TOC, sidecar и инвентарь — 7 Q/8 блоков.
Structure/Markdown/parity/style PASS, skel pre-check без hard,
с предупреждениями. Авторская точечная проверка Q7 A/B/C/D и sections
проведена. SQLAlchemy локально не установлен, async-пример с БД
не запускался. Полная `П` и независимая MCQ-приёмка ожидаются.
Коммита нет.

02.11 P2 Python Database Q8 добавлен на уровне `О`. По заголовкам (`З`)
и ответам (`О`) Python Database Q6/Q7 и Hibernate Q20/Q21 сверены:
прежние ответы не сравнивали SQLAlchemy loader strategies для списка.
[SQLAlchemy relationship loading](https://docs.sqlalchemy.org/en/20/orm/queryguide/relationships.html)
подтверждает отдельный SELECT по ключам для `selectinload`, JOIN для
`joinedload` и обязательный `Result.unique()` при JOIN коллекции.
MCQ Q8 B0, sidecar, TOC и инвентарь синхронизированы: 8 Q/9 блоков.
Structure/Markdown/parity/style PASS, skel pre-check без hard; авторская проверка A/B/C/D и sections проведена. Полный `П`, запуск
ORM-примера и независимая MCQ-приёмка остаются открыты. Коммита нет.

02.11 Python Database Q1–Q2 прошли авторскую полную `П` по
[PEP 249](https://peps.python.org/pep-0249/),
[Python sqlite3](https://docs.python.org/3.15/library/sqlite3.html#how-to-use-placeholders-to-bind-values-in-sql-queries),
[Psycopg 3 parameters](https://www.psycopg.org/psycopg3/docs/basic/params.html)
и [SQLAlchemy Session Basics](https://docs.sqlalchemy.org/en/20/orm/session_basics.html).
Q1 дополнен проверяемым примером `sql.Identifier` после allowlist;
MCQ B1 example синхронизирован. Q2 уточняет driver-level autocommit,
безусловный flush при commit даже с `autoflush=False` и неактивное
состояние Session после сбоя flush. MCQ Q2 B0 edge case обновлён,
B1 добавлен про `rollback()` перед повторной работой. Инвентарь —
8 Q/10 блоков. Structure/Markdown/parity/style PASS, skel pre-check
без hard; Q1 B0/B1 и Q2 B0/B1 авторски сверены по A/B/C/D и
sections. Psycopg/SQLAlchemy с БД локально не запускались; независимой
MCQ-приёмки нет. Коммита нет.

02.11 Python Database Q3–Q4 прошли авторскую полную `П` после
сверки FastAPI Q1 и Python Concurrency Q1 по заголовкам и ответам.
[FastAPI async](https://fastapi.tiangolo.com/async/),
[SQLAlchemy asyncio](https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html),
[Starlette thread pool](https://www.starlette.io/threadpool/) и
[AnyIO worker threads](https://anyio.readthedocs.io/en/stable/threads.html)
подтверждают явный offload sync DB-операции из `async def` без
совместного использования Session; Q3 дополнен этой границей.
[Python sqlite3](https://docs.python.org/3.15/library/sqlite3.html)
и [SQLite transactions](https://www.sqlite.org/lang_transaction.html)
подтверждают, что ручной SQL `BEGIN` при `autocommit=True` не
завершается через `with con` или `con.rollback()`; Q4 уточнён и
проверен локально на Python 3.14.4 (`in_transaction` остаётся True до
SQL `ROLLBACK`). MCQ Q3 B1 и Q4 B1 добавлены, B0 sections обновлены.
Инвентарь — 8 Q/12 блоков. Structure/Markdown/parity/style PASS,
skel pre-check без hard; Q3/Q4 B0/B1 авторски сверены по A/B/C/D и
sections. Интеграционный запуск FastAPI/SQLAlchemy и независимая
MCQ-приёмка остаются открыты. Коммита нет.

02.11 Python Database Q5–Q6 прошли авторскую полную `П`.
Сопоставлены Q2/Q3/Q7 и FastAPI Q2 по заголовкам и ответам.
[SQLAlchemy Session Basics](https://docs.sqlalchemy.org/en/20/orm/session_basics.html),
[Connection Pooling](https://docs.sqlalchemy.org/en/20/core/pooling.html)
и [DetachedInstanceError](https://docs.sqlalchemy.org/en/20/errors.html#parent-instance-x-is-not-bound-to-a-session-lazy-load-deferred-load-refresh-etc-operation-cannot-proceed)
подтверждают жизненный цикл Session, возврат соединения и риск
дозагрузки после close. Пример Q5 возвращал ORM-объекты из закрытой
сессии и противоречил совету Q6; теперь возвращает уже полученные
scalar IDs. MCQ Q5 B0 вариант B (без обещания reuse при любом пуле), example/edge cases и freshness синхронизированы.
Q6 проверен без смысловой правки; MCQ Q6 B0 A/B/C/D и sections
авторски сверены. Структура/Markdown/parity/style PASS, skel pre-check
без hard. Счётчики остались 8 Q/12 блоков, инвентарь не менялся.
SQLAlchemy с БД локально не запускался, независимой MCQ-приёмки нет.
Коммита нет.

02.11 Python Database Q7–Q8 прошли авторскую полную `П` после
сверки Q3/Q6 и Hibernate Q20/Q21 по заголовкам и ответам.
[SQLAlchemy asyncio](https://docs.sqlalchemy.org/en/20/orm/extensions/asyncio.html)
и [Errors: MissingGreenlet](https://docs.sqlalchemy.org/en/20/errors.html#missinggreenlet)
подтвердили Q7, включая `AsyncAttrs` с 2.0.13; смысловой правки нет.
[SQLAlchemy relationship loading](https://docs.sqlalchemy.org/en/20/orm/queryguide/relationships.html)
подтвердила Q8 и границу `joinedload` с LIMIT/OFFSET: подзапрос
сохраняет родителей страницы, JOIN размножает строки коллекции,
`unique()` остаётся обязательным. Q8 дополнен, MCQ B1 и freshness
синхронизированы. TOC и инвентарь — 8 Q/13 блоков. Structure/
Markdown/parity/style PASS, skel pre-check без hard; Q7/Q8 B0/B1
авторски сверены по A/B/C/D и sections. Вся Python Database Q1–Q8
прошла авторскую `П`; SQLAlchemy с БД локально не запускался,
независимой MCQ-приёмки нет. Коммита нет.

02.11 Go `database/sql` Q22–Q23 прошли авторскую полную `П` после
сверки Q24–Q25, Go HTTP Q1–Q5 и общих DB/pool вопросов по заголовкам
и точечно по ответам. [Go querying](https://go.dev/doc/database/querying)
подтвердил, что полный проход `Rows.Next()` закрывает `Rows`
автоматически, а `defer Rows.Close()` нужен для раннего выхода;
`Rows.Err()` проверяет ошибку итерации. [Go manage connections](https://go.dev/doc/database/manage-connections)
и [database/sql API](https://pkg.go.dev/database/sql) подтвердили
различие лимита открытых и простаивающих соединений: `SetMaxIdleConns`
не создаёт резерв сверх `SetMaxOpenConns`. Q22/Q23 и их production MCQ
уточнены, freshness обновлён. MCQ structure/parity PASS; локальный
pre-check Q22/Q23 без hard/warnings, общий pre-check старого файла
FAIL: 25 hard в остальных блоках. Варианты A/B/C/D и sections Q22/Q23
авторски проверены; независимой blind-приёмки и запуска с реальной БД
нет. Состав не менялся: 31 Q/32 блока, инвентарь не обновлялся.
Коммита нет.

02.11 Go `database/sql` Q24–Q25 прошли авторскую полную `П` после
сверки Q22–Q23, `databases/database-transactions-interview.md`
по заголовкам и точечно по ответам. [Go prepared statements](https://go.dev/doc/database/prepared-statements)
и [database/sql API](https://pkg.go.dev/database/sql) подтвердили
разницу между контекстом подготовки и выполнения, привязку
`tx.StmtContext` к транзакции и закрытие statement. [Go transactions](https://go.dev/doc/database/execute-transactions)
подтвердили работу `defer Rollback`, срок действия контекста `BeginTx`
и необходимость отбросить результаты при ошибке `Commit`. Теория,
production MCQ Q24/Q25 B0 и freshness синхронизированы. Структура
31 Q/32 блока PASS; локальный pre-check Q24/Q25 без hard/warnings.
Общие parity и pre-check старого файла FAIL: 23 hard в других блоках,
correct часто выделяется длиной. A/B/C/D и sections Q24/Q25 сверены
автором; независимой blind-приёмки и запуска с БД нет. Инвентарь не
менялся, коммита нет. Вся Go `database/sql` группа Q22–Q25 прошла
авторскую `П`.

02.11 Go stdlib Q26–Q27 прошли авторскую полную `П` после
сверки Q28, Go stdlib Q12 и смежных Go-файлов по заголовкам и
точечно по ответам. [Пакет `io`](https://pkg.go.dev/io) подтвердил
неполное чтение без ошибки, данные вместе с `EOF`, обязанность
`Writer.Write` возвращать ошибку при короткой записи, а также
потоковую работу `io.Copy` и чтение всего потока через `io.ReadAll`.
[Go 1.16 release notes](https://go.dev/doc/go1.16#ioutil) и
[`io/ioutil`](https://pkg.go.dev/io/ioutil) подтвердили сохранение
старого пакета, отличие `os.ReadDir` (`[]fs.DirEntry` вместо
`[]fs.FileInfo`) и границу Go 1.17 для deprecation `TempFile`/`TempDir`.
Убраны неподтверждённые обещания о предупреждениях `go vet`,
автозамене и одинаковой скорости старых и новых вызовов. Теория,
production MCQ Q26/Q27 B0 и freshness синхронизированы. Структура
31 Q/32 блока PASS; локальный pre-check Q26/Q27 без hard/warnings.
Общие parity и pre-check старого файла FAIL: 21 hard в других блоках,
correct часто выделяется длиной. A/B/C/D и sections Q26/Q27
авторски сверены; независимой blind-приёмки и запуска всех примеров
нет. Инвентарь не менялся, коммита нет.

02.11 Go stdlib Q28 прошёл авторскую полную `П` после сверки Q27,
Go HTTP и общих файловых вопросов по заголовкам и точечно по ответам.
[Пакет `embed`](https://pkg.go.dev/embed) и [Go 1.16 release notes](https://go.dev/doc/go1.16#embed)
подтвердили типы переменных, ограничения шаблонов, правило о пустых
строках/`//` комментариях между директивой и объявлением, а также
неизменность встроенных данных после сборки. [`fs.Sub`](https://pkg.go.dev/io/fs#Sub)
и [`http.FS`](https://pkg.go.dev/net/http#FS) подтвердили пример
публикации только поддерева статики. Старый пример использовал
необъявленную `staticFS`; его заменили согласованным фрагментом.
Убраны неподтверждённые заявления о времени запуска из MCQ.
Теория, production MCQ Q28 B0 и freshness синхронизированы.
Структура 31 Q/32 блока PASS; локальный pre-check Q28 без
hard/warnings. Общие parity и pre-check старого файла FAIL: 21 hard
в других блоках. A/B/C/D и sections Q28 авторски сверены;
независимой blind-приёмки нет. Локальный Go toolchain отсутствует,
поэтому пример не компилировался и HTTP не запускался. Инвентарь не
менялся, коммита нет.

02.11 Go stdlib Q29–Q30 прошли авторскую полную `П` после
сверки соседнего Q31 и `go-generics-interview.md` Q25 по заголовкам
и точечно по ответам. [Пакет `sort`](https://pkg.go.dev/sort)
подтвердил сигнатуру `sort.Slice`, контракт `sort.Interface` и
отсутствие гарантии стабильности у `Slice`/`Sort`.
[Пакет `slices`](https://pkg.go.dev/slices) подтвердил компаратор
`SortFunc` по значениям, `SortStableFunc`, поверхностную копию
`Clone` и границы `Concat` (Go 1.22), `Collect` (Go 1.23).
[Go 1.21 release notes](https://go.dev/doc/go1.21#slices) подтвердили
появление пакета. Убраны неподтверждённые универсальные оценки
скорости; вместо незавершённых Go-фрагментов даны согласованные
примеры. Из Q30/MCQ убран повтор вопроса о `maps.Keys`, уже
разобранного в generics Q25. Теория, production MCQ Q29/Q30 B0 и
freshness синхронизированы. Структура 31 Q/32 блока PASS; локальный
pre-check Q29/Q30 без hard/warnings. Общие parity и pre-check старого
файла FAIL: 19 hard в других блоках. A/B/C/D и sections Q29/Q30
авторски сверены; независимой blind-приёмки нет. Локального Go
нет, примеры не компилировались. Инвентарь не менялся, коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q31, JSON v2 · П.** Заголовки
Q7–Q10/Q31 и соседних Go/API-тем сверены: отдельного файла для миграции
JSON не нужно. По [Go 1.27 release notes](https://go.dev/doc/go1.27#jsonv2),
[encoding/json/v2](https://pkg.go.dev/encoding/json/v2),
[jsontext](https://pkg.go.dev/encoding/json/jsontext) и
[encoding/json](https://pkg.go.dev/encoding/json) проверены версия,
совместимость v1, строгие defaults, опции и граница потокового API.
В Q31 уточнено: `UnmarshalRead` потребляет весь Reader до EOF и одно
значение; поток нескольких значений требует `jsontext.Decoder` с
`UnmarshalDecode`. Уточнены `FormatNil*AsNull` и отличие `[]byte`.
MCQ Q31 B0/B1 и freshness синхронизированы; A/B/C/D и sections
сверены автором с Q31. Структура 31 Q/32 блока PASS, локальный
pre-check Q31 B0/B1 без hard/warnings. Общие parity и pre-check
FAIL на старых блоках (parity: 21/32 correct самые длинные; 21 блок
вне коридора длины; pre-check: 19 hard). Независимой blind-приёмки
нет. Локального Go нет, пример не компилировался. Инвентарь не менялся,
коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q7–Q10, JSON v1 · П.**
По [encoding/json](https://pkg.go.dev/encoding/json) и
[Go spec: method sets](https://go.dev/ref/spec#Method_sets) сверены
теги, `RawMessage`, `Decoder`/`Encoder` и собственные методы JSON.
Q7: `omitempty` ограничен пустыми значениями v1, а `json:"-"`
исключает чтение и запись. Q8: `RawMessage` откладывает отображение
в Go-тип, но внешний `Unmarshal` проверяет JSON-синтаксис; убран
несуществующий «base64-тег», пример обрабатывает ошибки и неизвестный
тип. Q9: `Decode(&slice)` не даёт поэлементный поток большого массива;
разделены последовательность значений и обход массива через
`Token`/`More`. Q10: JSON-строка в `UnmarshalJSON` разбирается через
`json.Unmarshal`, что учитывает escapes и неверный тип. MCQ Q7–Q10
B0 и freshness синхронизированы; A/B/C/D и sections сверены автором.
Структура 31 Q/32 блока PASS; локальный pre-check Q7–Q10 без
hard/warnings. Общие parity/pre-check FAIL на старых блоках:
18/32 correct самые длинные, 19 блоков вне коридора длины, 16 hard
pre-check. Независимого blind review нет; локального Go нет, примеры
не компилировались. Инвентарь не менялся, коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q6, Marshal/Unmarshal · П.**
По [официальному encoding/json API](https://pkg.go.dev/encoding/json)
проверены стандартное отображение экспортируемых полей, требования
`Unmarshal` к указателю, совпадение имён и игнорирование неизвестных
ключей. Пример Q6 переписан как функция: убрано повторное объявление
`u`, ошибки обоих вызовов проверяются. Уточнено, что собственные методы
сериализации меняют стандартное отображение. MCQ Q6 B0 и freshness
синхронизированы; A/B/C/D и sections сверены автором. Структура
31 Q/32 блока PASS; локальный pre-check Q6 без hard/warnings.
Общие parity/pre-check FAIL на других блоках: 17/32 correct самые
длинные, 18 блоков вне коридора длины, 15 hard pre-check.
Независимого blind review нет. Локального Go нет, пример не
компилировался. Инвентарь не менялся, коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q1–Q2, HTTP-сервер и ServeMux · П.**
Соседние Q3–Q5, Go Testing Q14–Q15 и HTTP & REST сверены по заголовкам
и необходимым ответам; отдельный файл не нужен. По
[net/http](https://pkg.go.dev/net/http),
[Go 1.22 release notes](https://go.dev/doc/go1.22#enhanced_routing_patterns)
и [блогу команды Go](https://go.dev/blog/routing-enhancements)
исправлены Q1–Q2. Q1: пример использует собственный mux, `http.Server`,
таймауты и проверяет ошибку `ListenAndServe`; отделены базовый сервер
и настройки для рабочего сервиса. Q2: правило «самый длинный паттерн»
заменено на специфичность по множеству запросов; уточнены GET/HEAD,
один сегмент `{id}`, перенаправление при завершающем `/`, конфликт
паттернов и совместимость `httpmuxgo121=1`. Production MCQ Q1–Q2 B0
и freshness синхронизированы; A/B/C/D и sections сверены автором.
Структура 31 Q/32 блока PASS, локальный pre-check Q1–Q2 без
hard/warnings. Общие parity/pre-check FAIL на других блоках: 16/32
correct самые длинные, 17 блоков вне коридора длины, 13 hard
pre-check. Независимого blind review нет; локального Go нет,
примеры не компилировались. Инвентарь не менялся, коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q3, HTTP middleware · П.**
Q1–Q2/Q5 и Go Testing Q14 сверены как соседние ответы. По
[net/http Handler](https://pkg.go.dev/net/http#Handler) и
[HandlerFunc](https://pkg.go.dev/net/http#HandlerFunc) проверены
обёртка, порядок и граница работы с `ResponseWriter`. Вместо
неопределённых `validToken`/`myHandler` пример показывает проверку
`X-Request-ID` и функцию композиции. Уточнено: отказ внутреннего
middleware останавливает конечный handler, но внешний `Logging`
продолжает работу после возврата из `next`; выполнение `next` в
отдельной горутине с ранним возвратом недопустимо. Production MCQ
Q3 B0 и freshness синхронизированы, A/B/C/D и sections авторски
сверены с теорией. Структура 31 Q/32 блока PASS, локальный
pre-check Q3 без hard/warnings. Общие parity/pre-check FAIL на
других блоках: 15/32 correct самые длинные, 16 блоков вне
коридора длины, 12 hard pre-check. Независимого blind review нет;
локального Go нет, пример не компилировался. Инвентарь не менялся,
коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q4, HTTP client · П.**
Q1–Q3/Q5 и Go Testing Q15 сверены как соседние ответы. По
[net/http Client](https://pkg.go.dev/net/http#Client),
[Transport](https://pkg.go.dev/net/http#Transport),
[NewRequestWithContext](https://pkg.go.dev/net/http#NewRequestWithContext)
и [блогу Go о HTTP/1](https://go.dev/blog/http-connection-reuse)
исправлены пример и границы ответа: `Client.Timeout` действует до
чтения body включительно, `Transport:nil` использует общий
`DefaultTransport`, пул принадлежит транспорту, а закрытие
непрочитанного HTTP/1 body с Go 1.27 может запустить ограниченное
асинхронное дочитывание. Пример проверяет создание запроса, статус
и ошибку чтения; для недоверенного body отмечен отдельный лимит.
Production MCQ Q4 B0 переписан и B1 добавлен для пула; все A/B/C/D
и sections авторски сверены с Q4 и источниками. Freshness обновлён.
Структура 31 Q/33 блока PASS, локальный pre-check Q4 B0/B1 без
hard/warnings. Общие parity/pre-check FAIL из-за других блоков:
14/33 correct самые длинные, 15 блоков вне коридора длины,
11 hard pre-check. Независимого blind review нет; локального Go
нет, пример не компилировался. Инвентарь не менялся: состав Q и
файлов прежний; коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q5, graceful shutdown · П.**
Q1/Q4 и Go Testing Q14–Q15 сверены как соседние ответы. По
[Server.Shutdown](https://pkg.go.dev/net/http#Server.Shutdown),
[Server.Close](https://pkg.go.dev/net/http#Server.Close),
[NotifyContext](https://pkg.go.dev/os/signal#NotifyContext) и
[Kubernetes Pod lifecycle](https://kubernetes.io/docs/concepts/workloads/pods/pod-lifecycle/#termination-of-pods)
исправлен пример: ошибка запуска обрабатывается отдельно от сигнала,
для ожидания `Shutdown` создаётся новый контекст, а вызывающий код
ждёт возврата `Shutdown`, не только `ErrServerClosed` из
`ListenAndServe`. Уточнено, что истечение контекста возвращает
ошибку, но само по себе не обрывает активные соединения;
захваченные соединения требуют отдельной остановки. Production MCQ
Q5 B0 и freshness синхронизированы, A/B/C/D и sections авторски
сверены с Q5 и источниками. Заголовок и TOC Q4 уточнены под
его уже существующий B1 о `Transport`; STEM-DRIFT Q4 B1 исчез,
остался старый сигнал Q7 B0. Структура 31 Q/33 блока PASS,
локальный pre-check Q5 без hard/warnings. Общие parity/pre-check
FAIL на других блоках: 13/33 correct самые длинные, 14 блоков
вне коридора длины, 10 hard pre-check. Независимого blind review
нет; локального Go нет, код не компилировался и сигналы не
тестировались. Инвентарь не менялся, коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q11–Q12, строки и буфер · П.**
Go core Q11–Q12 и stdlib Q26 сверены как соседние темы. По
[strings.Builder](https://pkg.go.dev/strings#Builder),
[bytes.Buffer](https://pkg.go.dev/bytes#Buffer),
[исходному коду Builder](https://go.dev/src/strings/builder.go) и
[блогу Go о `go fix`](https://go.dev/blog/gofix) проверены ответы.
Q11: пример оформлен двумя самодостаточными функциями; описаны
квадратичный объём копирования при повторной конкатенации,
необязательный `Grow`, запрет копирования ненулевого Builder и
граница гарантии `String()`; сравнительная скорость без замера
не обещается. Q12: заголовок и TOC уточнены; пример теперь
проверяет ошибки `Encoder`/`Decoder`, а ответ различает непрочитанные
данные, alias от `Bytes()` и сохранение ёмкости после `Reset()`.
Production MCQ Q11–Q12 B0 и freshness синхронизированы; A/B/C/D и
sections авторски сверены с вопросами и источниками. Структура
31 Q/33 блока PASS, локальный skel Q11–Q12 без hard/warnings.
Общие parity/pre-check FAIL на других блоках: 11/33 correct самые
длинные, 12 блоков вне коридора длины, 9 hard skel; linkage
сохраняет старый STEM-DRIFT Q7 B0. Независимого blind review нет;
локального Go нет, примеры не компилировались и бенчмарк не
проводился. Инвентарь не менялся, коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q13–Q14, время и layout · П.**
Q15–Q16 сверены как соседние ответы; отдельного файла не нужно.
По [time](https://pkg.go.dev/time), [Duration](https://pkg.go.dev/time#Duration),
[Parse](https://pkg.go.dev/time#Parse) и
[ParseInLocation](https://pkg.go.dev/time#ParseInLocation)
исправлены Q13–Q14. Q13: удалён некомпилируемый фрагмент констант,
пример теперь детерминированно показывает `Time.Add`, `Sub` и вывод
`5s 5`; уточнены наносекунды и монотонная часть `time.Now`.
Q14: пример проверяет ошибку разбора, объясняет опорный layout,
UTC при `Parse` без зоны, отличие `ParseInLocation` и RFC3339.
Production MCQ Q13 B0 и Q14 B0/B1 синхронизированы с теорией;
все A/B/C/D и sections авторски сверены. Freshness обновлён.
Структура 31 Q/34 блока PASS, локальный skel Q13/Q14 без
hard/warnings. Общие parity/pre-check FAIL на других блоках:
9/34 correct самые длинные, 10 блоков вне коридора длины,
7 hard skel; linkage сохраняет старый STEM-DRIFT Q7 B0.
Q14 B0/B1 получают лишь предупреждение о недословном заголовке:
оба сценария прямо разобраны в ответе Q14.
Независимого blind review нет; локального Go нет, код не
компилировался. Инвентарь не менялся, коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q15–Q16 и Go Concurrency Q14, зоны и таймеры · П.**
По [time.Time](https://pkg.go.dev/time#Time),
[LoadLocation](https://pkg.go.dev/time#LoadLocation),
[Timer.Stop/Reset](https://pkg.go.dev/time#Timer.Stop),
[time.After](https://pkg.go.dev/time#After) и
[Go 1.23 release notes](https://go.dev/doc/go1.23#timer)
исправлены три связанных ответа. Q15 больше не содержит повторного
`t :=` и двух независимых `time.Now()`; пример проверяет один момент
в UTC и Нью-Йорке, `Equal`/`==` и ошибку `LoadLocation`. Уточнены
источники данных IANA, неоднозначность местного времени при DST и
случай повторяющегося расписания. Q16 различает `After`/`NewTimer`
и `Tick`/`NewTicker`; исключена безусловная «утечка» от `After` в
Go 1.23+ и опасный drain после `Stop`. Указаны строка `go` главного
модуля и `GODEBUG=asynctimerchan=1`. Go Concurrency Q14 приведён к
тем же правилам. Production MCQ Q15 B0, Q16 B0 и Concurrency Q14 B0
переписаны; все A/B/C/D и sections авторски сверены с теорией.
Freshness обновлён в обеих темах. Число Q/блоков не изменилось,
инвентарь не трогали.
Структура двух MCQ PASS: 66 Q/70 блоков; локальный skel для трёх
изменённых блоков без hard/warnings. Общие parity/pre-check обеих
тем FAIL из-за старых блоков (stdlib: 8 блоков вне коридора длины,
5 hard skel; concurrency: прежний крупный backlog, 26 hard skel).
Linkage сохраняет старые ошибки stdlib Q7 B0 и concurrency Q21 B0;
Q15/Q16 и concurrency Q14
получают лишь предупреждения о недословных заголовках. Независимой
blind-приёмки нет; Go toolchain отсутствует, примеры не компилировались
и версии таймеров не запускались. Коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q17 и Go Concurrency Q23–Q25, context · П.**
По [context](https://pkg.go.dev/context),
[CancelFunc](https://pkg.go.dev/context#CancelFunc),
[WithDeadline](https://pkg.go.dev/context#WithDeadline) и
[go vet](https://pkg.go.dev/cmd/vet) переписан Q17: пример больше
не переобъявляет `ctx`, различает `Done()`/`Err()` и показывает
владение `CancelFunc`. Уточнено, что `cancel()` отсоединяет потомка
и останавливает таймер, но не ждёт завершения горутин; без него
потомок может удерживаться до отмены родителя, что само по себе
не доказывает утечку горутины. В Concurrency Q23 убран `log.Fatal`,
из-за которого `defer cancel()` не выполнялся; Q24 теперь различает
ручную, относительную и абсолютную отмену без повторных `ctx :=`;
Q25 показывает явную передачу отмены и использование обоих cancel.
Production MCQ stdlib Q17 B0 и Concurrency Q23–Q25 B0 синхронизированы;
четыре набора A/B/C/D и sections авторски сверены. Freshness обеих
тем обновлён. Состав Q/блоков прежний, инвентарь не менялся.
Структура двух MCQ PASS: 66 Q/70 блоков; локальный skel четырёх
блоков без hard/warnings, локальные сигналы формы устранены.
Общие gates FAIL: у stdlib 7 блоков вне коридора длины и 4 hard
skel; у concurrency 32 блока и 25 hard skel. Linkage сохраняет
старые ошибки stdlib Q7 B0 и concurrency Q21 B0; новые формулировки
получают лишь предупреждения о недословных заголовках. Независимой
blind-приёмки нет; Go toolchain отсутствует, примеры и `go vet`
не запускались. Коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q18–Q19 и Go Core Q28, ошибки · П.**
По [errors](https://pkg.go.dev/errors), [fmt.Errorf](https://pkg.go.dev/fmt#Errorf),
[Go 1.13 errors](https://go.dev/blog/go1.13-errors),
[Go 1.20](https://go.dev/doc/go1.20) и [os.File](https://pkg.go.dev/os#File.Close)
переписаны Q18–Q19. Q18 теперь показывает связный пример `Is`/`As`,
отличает `%w` от `%v`, отдельные значения `errors.New` и
типизированную `AsType` с Go 1.26. Q19 объясняет ветви `Join`,
пропуск `nil`, `errors.Unwrap(joined) == nil` и разницу с несколькими
`%w`. Пересечение с Go Core Q25 подтверждено, новый файл не нужен.
В Go Core Q28 исправлены неверная версия `Join` (1.20 вместо 1.21),
двойной `Close` и пропущенная ошибка `os.Create`; пример теперь
сохраняет ошибки записи и закрытия через `errors.Join`. Парный
production MCQ Q28 B0 переписан вместе с stdlib Q18–Q19 B0; все
A/B/C/D и sections авторски проверены. Freshness обеих тем обновлён.
Состав Q/блоков прежний, инвентарь не менялся.
Структура двух MCQ PASS: 67 Q/70 блоков; локальный skel трёх
изменённых блоков без hard/warnings. Общие gates FAIL: stdlib —
6 блоков вне коридора длины, 2 hard skel; Go Core — 35 блоков и
29 hard skel. Linkage сохраняет старые ошибки stdlib Q7 B0 и Go
Core Q8/Q25/Q31 B0; у изменённых блоков лишь предупреждения о
недословных заголовках, их сценарии есть в теории. Независимой
blind-приёмки нет; Go toolchain отсутствует, примеры не компилировались
и ошибку закрытия не воспроизводили. Коммита нет.

**Контрольная точка 02.11.2026 — Go stdlib Q20–Q21, логирование · П.**
Go Core Q32 и заголовки общего logging-файла сверены: отдельной
Go-темы не требуется. По официальным [log/slog](https://pkg.go.dev/log/slog),
[блогу Go](https://go.dev/blog/slog), [log](https://pkg.go.dev/log)
и документации [zap](https://github.com/uber-go/zap),
[zerolog](https://github.com/rs/zerolog),
[logrus](https://github.com/sirupsen/logrus) исправлены Q20–Q21.
Q20 получил связный пример и границу `InfoContext`: стандартный
Handler не извлекает trace ID из контекста. Q21 больше не обещает
универсальный рейтинг скорости и лёгкую миграцию через собственный
интерфейс. Production MCQ Q20–Q21 B0 переписаны; A/B/C/D и sections
авторски проверены, freshness обновлён. Структура JSON PASS
(31 Q/34 блока), локальный skel Q20–Q21 без hard/warnings.
Общие gates остаются FAIL из-за накопленных сигналов других блоков;
независимой blind-приёмки нет. Go toolchain отсутствует, пример Q20
не компилировался; бенчмарки не проводились. Состав Q/блоков прежний,
инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q1–Q3, язык и выбор стека · П.**
Заголовки Go stdlib и concurrency сверены; новые вопросы и отдельный
файл не нужны. По [Go FAQ](https://go.dev/doc/faq),
[Go at Google](https://go.dev/talks/2012/splash.article),
[Effective Go](https://go.dev/doc/effective_go#concurrency),
[Go wiki о mutex/channel](https://go.dev/wiki/MutexOrChannel),
[Java HotSpot](https://docs.oracle.com/en/java/javase/25/vm/java-virtual-machine-technology-overview.html)
и [Java virtual threads](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)
проведена авторская полная `П` Q1–Q3. Удалены вымышленные фиксированные
RAM/стартовые показатели, безусловные обещания автономного бинарника
и требование всегда заменять mutex каналом. Q2 сравнивает обычные
сборки Go и Java на JVM с учётом Java virtual threads; Q3 сохраняет
композицию, errors as values и единый стиль `gofmt` без догмы
«один алгоритм на задачу». Production MCQ Q1–Q3 B0 переписаны;
A/B/C/D и sections авторски проверены, freshness обновлён.
Structure PASS (36 Q/36 блоков); локальные skel, parity, anti-tell
и linkage Q1–Q3 без сигналов. Общие gates FAIL: в других блоках
остаются старые проблемы формы и ссылок. Независимой blind-приёмки
нет; локального Go toolchain нет, измерения ресурсов не проводились.
Состав Q/блоков прежний, инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q4–Q7, типы и zero values · П.**
Заголовки Go stdlib и concurrency сверены; отдельной темы не нужно.
По [Go specification](https://go.dev/ref/spec),
[Go strings](https://go.dev/blog/strings),
[GC guide](https://go.dev/doc/gc-guide),
[Go 1.18](https://go.dev/doc/go1.18) и
[sync.Mutex](https://pkg.go.dev/sync#Mutex) проведена авторская полная
`П` Q4–Q7. Q4 объясняет границы GC, `unsafe` и `cgo`; Q5 получил
связный пример `var`/`const`/`:=` и условие повторного объявления;
Q6 отличает произвольные байты `string` от UTF-8 и предупреждает об
усечении при сужающей конвертации; Q7 различает `nil`-slice, map и
channel. Production MCQ Q4–Q7 B0 переписаны вместе с A/B/C/D и
sections; freshness обновлён. Structure PASS (36 Q/36 блоков),
локальные skel/parity/anti-tell/linkage Q4–Q7 без сигналов.
Общие gates остаются FAIL из-за старого backlog других блоков.
Независимой blind-приёмки нет; локальный Go toolchain отсутствует,
примеры не компилировались. Состав Q/блоков прежний, инвентарь не
менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q8–Q9, конверсии и указатели · П.**
Q6/Q7/Q16 и заголовки соседних Go-файлов сверены; новой темы не
требуется. По [Go specification](https://go.dev/ref/spec#Conversions),
[Go FAQ](https://go.dev/doc/faq#stack_or_heap),
[strconv](https://pkg.go.dev/strconv) и
[unsafe](https://pkg.go.dev/unsafe) проведена авторская полная `П`.
Q8 отличает явную конверсию переменной от присваивания представимой
нетипизированной константы, `string(65)` от десятичного текста и
предупреждает об усечении без ошибки. Q9 объясняет `&`/`*`, nil-panic,
границу обычных указателей и `unsafe`; возвращённый адрес локальной
переменной больше не обещает обязательное выделение в heap. Примеры
Q8–Q9 переписаны в связный код. Production MCQ Q8–Q9 B0 и freshness
синхронизированы; A/B/C/D и sections авторски проверены. Structure
PASS (36 Q/36 блоков), локальные skel/parity/anti-tell/linkage Q8–Q9
без сигналов. Общие gates FAIL из-за старого backlog других блоков;
независимой blind-приёмки нет. Go toolchain отсутствует, примеры не
компилировались, escape analysis не запускался. Состав Q/блоков
прежний, инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q10–Q12, массивы, срезы и `append` · П.**
Заголовки соседних Go-файлов, в том числе `go-memory-gc-interview.md`
Q18/Q25, сверены; пересечение не требует нового файла. По
[Go specification](https://go.dev/ref/spec#Appending_and_copying_slices),
[Go Slices](https://go.dev/blog/slices-intro) и
[slices.Clone](https://pkg.go.dev/slices#Clone) проведена авторская
полная `П`. Q10 различает копирование элементов массива и значения
среза. Q11 объясняет `len`/`cap` как модель без обещания конкретного
layout и коэффициента роста. Q12 исправляет некомпилируемый пример
голого `append(s, x)`, показывает перезапись общего свободного слота
и необходимость использовать результат. Production MCQ Q10–Q12 B0
и freshness синхронизированы; все A/B/C/D и sections авторски
сверены с теорией. Structure PASS (36 Q/36 блоков), локальные
skel/parity/anti-tell/linkage Q10–Q12 без сигналов. Общие gates FAIL
на других блоках; независимой blind-приёмки нет. Go toolchain
отсутствует, примеры не компилировались. Состав Q/блоков прежний,
инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q13–Q14, map и embedding · П.**
Заголовки соседних Go-шпаргалок и Q7/Q16/Q19/Q32 сверены; отдельный
файл не нужен. По [Go specification: map](https://go.dev/ref/spec#Map_types),
[range](https://go.dev/ref/spec#For_statements),
[struct/selectors](https://go.dev/ref/spec#Struct_types),
[Go 1.24 release notes](https://go.dev/doc/go1.24),
[Go maps in action](https://go.dev/blog/maps) и
[sync.Map](https://pkg.go.dev/sync#Map) проведена авторская полная `П`.
Q13 убирает прежнее описание бакетов по 8 элементов: с Go 1.24
стандартный runtime использует реализацию на основе Swiss Tables.
Порядок `range` не гарантирован; ключ, вставленный при обходе той же
горутиной, может быть выдан или пропущен. Конкурентный доступ с записью
требует синхронизации. Q14 различает продвижение методов и наследование,
описывает конфликт селекторов и набор методов для встроенных `T`/`*T`.
Примеры заменены связным кодом. Production MCQ Q13–Q14 B0 и freshness
синхронизированы; A/B/C/D и sections авторски проверены. Structure
PASS (36 Q/36 блоков), локальные skel/parity/anti-tell/linkage без
сигналов. Общие gates FAIL на других блоках; независимой blind-приёмки
нет. Go toolchain отсутствует, примеры не компилировались. Состав
Q/блоков прежний; инвентарь в этом пакете не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q15–Q16, результаты функций и получатели методов · П.**
Заголовки соседних Go-файлов, Q14/Q19/Q24/Q28 и граница с generics
сверены; отдельный файл не нужен. По
[Go specification: return](https://go.dev/ref/spec#Return_statements),
[method sets](https://go.dev/ref/spec#Method_sets),
[Go FAQ](https://go.dev/doc/faq#methods_on_values_or_pointers) и
[Effective Go](https://go.dev/doc/effective_go#methods) проведена
авторская полная `П`. Q15 теперь показывает связный пример двух
результатов, `_`, прямого возврата вызова и именованных результатов;
`error` в конце отмечен как идиома, а не синтаксическое требование.
Q16 разделяет поверхностное копирование value receiver, мутацию через
`*T`, автоматический адрес при прямом вызове и набор методов для
интерфейса. Смешивание получателей больше не объявляется запретом;
пример `Counter` приведён к согласованному выбору. Production MCQ
Q15–Q16 B0 и freshness синхронизированы; A/B/C/D и sections авторски
сверены с теорией. Structure PASS (36 Q/36 блоков), локальные
skel/parity/anti-tell/linkage Q15–Q16 без сигналов. Общие gates FAIL
на других блоках; независимой blind-приёмки нет. Go toolchain
отсутствует, примеры не компилировались. Состав Q/блоков прежний;
инвентарь в этом пакете не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q17–Q18, variadic-функции и замыкания · П.**
Соседние заголовки Go Concurrency, Q10/Q12 и Q26–Q28 сверены; новая
тема не нужна. По [Go specification: variadic](https://go.dev/ref/spec#Passing_arguments_to_..._parameters),
[function literals](https://go.dev/ref/spec#Function_literals),
[for statements](https://go.dev/ref/spec#For_statements),
[Go 1.22 release notes](https://go.dev/doc/go1.22) и
[Go blog: loop variables](https://go.dev/blog/loopvar-preview) проведена
авторская полная `П`. Q17 уточняет `nil` при нуле аргументов, новый
срез для отдельных аргументов и общий базовый массив при `f(s...)`;
отдельно разобрана несовместимость `[]int` с `...any` в `fmt.Println`.
Q18 отличает захват общей переменной от снимка значения и ограничивает
семантику Go 1.22 версией языка модуля и объявлением переменной через
`:=`; переменная, объявленная до цикла, остаётся общей. Примеры
переписаны в связный код. Production MCQ Q17–Q18 B0 и freshness
синхронизированы; A/B/C/D и sections авторски проверены. Structure
PASS (36 Q/36 блоков), локальные skel/parity/anti-tell/linkage без
сигналов. Общие gates FAIL на других блоках; независимой blind-приёмки
нет. Go toolchain отсутствует, примеры не компилировались. Состав
Q/блоков прежний; инвентарь в этом пакете не менялся. Коммита нет.

Go Core Q19–Q20 в `programming-languages/go/go-interview.md` прошли
авторскую `П`: Q19 различает обычный интерфейс и ограничение с типовыми
элементами Go 1.18+, описывает динамические тип и значение без обещания
конкретного layout; Q20 показывает проверку `io.Reader` и границу
наборов методов `T`/`*T`. Примеры записаны как цельные программы.
Production MCQ Q19–Q20 B0 и freshness синхронизированы, варианты и
sections авторски сверены с теорией и Go spec/FAQ/io. Локальная
структурная проверка PASS (36 Q/36 блоков), MD без MCQ. Общие gates
содержат старые сигналы других Q; независимой blind-приёмки нет.
Go toolchain отсутствует, примеры не компилировались. Состав Q/блоков
прежний, инвентарь в этом пакете не менялся. Коммита нет.

Go Core Q21–Q22 в `programming-languages/go/go-interview.md` прошли
авторскую `П`. Q21 уточняет алиас `any` с Go 1.18, допустимые типы при
`encoding/json.Unmarshal` и выбор между динамическими и известными
данными. Q22 различает assertion к конкретному типу и интерфейсу,
формы с `ok` и без него, `case nil` и тип переменной в type switch.
Неисполнимый пример Q22 заменён цельной программой. Production MCQ
Q21–Q22 B0 и freshness синхронизированы. Варианты A/B/C/D и их
sections авторски проверены по Go spec, fmt и encoding/json. Structure
PASS (36 Q/36 блоков), локальные struct/skel/linkage без hard-сигналов;
общий parity/gates FAIL на других блоках. Независимой blind-приёмки
нет. Go toolchain отсутствует, примеры не компилировались. Состав
Q/блоков прежний; инвентарь в этом пакете не менялся. Коммита нет.

Go Core Q23–Q24 в `programming-languages/go/go-interview.md` прошли
авторскую `П`. Q23 отделён от Q20: проверены точные имена/сигнатуры
методов, неэкспортируемые имена разных пакетов и контроль присваиванием.
Q24 больше не утверждает, что компилятор не даёт забыть ошибку;
`defer file.Close()` обозначен как потеря возвращённого `error` без
дополнительного кода. Пример заменён на цельную программу с
`os.ReadFile` (Go 1.16+) и `fmt.Errorf` с `%w`. Production MCQ Q23–Q24
B0 и freshness синхронизированы; A/B/C/D и sections авторски сверены
с теорией. Источники: Go spec (interfaces, method sets, identifier
uniqueness, errors), Go FAQ о nil error, Go blog «Errors are values»,
официальные API os и fmt. Structure PASS (36 Q/36 блоков), локальные
struct/skel/linkage без сигналов на Q23–Q24; общие gates/parity FAIL
на других блоках. Независимой blind-приёмки нет. Go toolchain
отсутствует, примеры не компилировались. Состав Q/блоков прежний;
инвентарь в этом пакете не менялся. Коммита нет.

Go Core Q25–Q26 в `programming-languages/go/go-interview.md` прошли
авторскую `П`. Q25 теперь различает `errors.Is` и `errors.As` в дереве
причин, объясняет контракт API при `%w` и границы Go 1.13/1.20/1.26.
Q26 уточняет прямой `recover` в `defer` той же goroutine, поведение
после перехвата и изменение `panic(nil)` в Go 1.21. Оба примера
переписаны в цельные программы. Production MCQ Q25–Q26 B0 и freshness
синхронизированы; A/B/C/D и sections авторски сверены с теорией.
Источники: официальные errors/fmt/os API, Go spec, Go 1.13 errors blog,
Go 1.21 release notes. Structure PASS (36 Q/36 блоков), локальные
struct/skel/linkage без сигналов на Q25–Q26; общие gates/parity FAIL
на других блоках. Независимой blind-приёмки нет. Go toolchain
отсутствует, примеры не компилировались. Состав Q/блоков прежний;
инвентарь в этом пакете не менялся. Коммита нет.

Go Core Q27 в `programming-languages/go/go-interview.md` прошёл
авторскую `П`. Цельный пример показывает LIFO, вычисление аргументов
при регистрации и изменение именованного результата после `return`.
Убрана слишком сильная гарантия «освобождение нельзя забыть»;
уточнены накопление `defer` в цикле, потеря ошибки `Close` и отсутствие
отложенных вызовов при `os.Exit`. Production MCQ Q27 B0 и freshness
синхронизированы; A/B/C/D и sections авторски сверены с теорией.
Источники: Go spec Defer statements, официальный блог Defer, Panic,
and Recover, os.Exit и os.File.Close API. Structure PASS (36 Q/36
блоков), локальные struct/skel/linkage без сигналов на Q27; общие
gates/parity FAIL на других блоках. Независимой blind-приёмки нет.
Go toolchain отсутствует, пример не компилировался. Состав Q/блоков
прежний; инвентарь в этом пакете не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q29–Q30 · авторская `П`.**
Проверены ответы, соседние Q1–Q4/Q31, Go Concurrency и Go Memory & GC.
В Q29 исправлены модель G/M/P и смысл `GOMAXPROCS`, убраны обещания
«меньше потоков, чем goroutines», неблокирующего любого I/O, диапазона
размера бинарника и малых пауз GC. В Q30 разделены чистая кросс-сборка
и cgo с целевым C-компилятором; пример ограничен проектом без
обязательного cgo. Production MCQ Q29/Q30 B0 и freshness обновлены.
Источники: [Go runtime HACKING](https://go.dev/src/runtime/HACKING),
[GC guide](https://go.dev/doc/gc-guide),
[cgo](https://pkg.go.dev/cmd/cgo),
[GOOS/GOARCH](https://go.dev/doc/install/source#environment).
Structure PASS (36 Q/36 блоков), локальные struct/skel/linkage Q29/Q30
без сигналов, Markdown gate PASS. Общие parity/skel/linkage остаются
FAIL на других блоках, включая Q31 STEM-DRIFT; независимой blind-приёмки
Q29/Q30 нет. Go toolchain отсутствует, кросс-сборка не запускалась.
Состав Q/блоков прежний; инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q31 · авторская `П`.**
Сверены соседние Q1/Q29/Q30 и официальные Go linker/cgo/net/os/user,
Docker scratch. Исправлены ложные гарантии «Go всегда статичен»,
«cgo всегда динамичен», «net по умолчанию требует cgo» и «CGO_ENABLED=0
работает где угодно». Добавлены проверка ELF через `readelf` и граница
между библиотеками и файлами среды, включая сертификаты CA.
Production MCQ Q31 B0 и freshness синхронизированы. Structure PASS
(36 Q/36 блоков), локальные struct/skel/linkage Q31 без сигналов,
Markdown gate PASS; прежний Q31 STEM-DRIFT устранён. Общий parity
остаётся FAIL на других блоках; skel также FAIL на старых блоках.
Независимой blind-приёмки Q31 нет. Go toolchain отсутствует, ELF и
образ не собирались. Состав Q/блоков прежний; инвентарь не менялся.
Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q32–Q33 · авторская `П`.**
Сопоставлены Go Stdlib Q1/Q17/Q22 и Go Concurrency Q23–Q25.
В Q32 убран неподтверждённый процент покрытия задач; названы границы
`database/sql` без драйверов, синтаксиса `regexp` и версии `log/slog`.
В Q33 заменён пример с необъявленным каналом на `QueryRowContext`;
разграничены сигнал отмены, завершение работы и освобождение ресурсов
дочернего контекста. Источники: [Go std](https://pkg.go.dev/std),
[`database/sql`](https://pkg.go.dev/database/sql),
[`regexp`](https://pkg.go.dev/regexp),
[`context`](https://pkg.go.dev/context),
[отмена запросов БД](https://go.dev/doc/database/cancel-operations),
[Go 1.21](https://go.dev/doc/go1.21#log_slog).
Production MCQ Q32/Q33 B0 и freshness синхронизированы. Structure
PASS (36 Q/36 блоков), локальные struct/skel/linkage Q32/Q33 без
сигналов, Markdown gate PASS. Общий parity FAIL на других блоках;
общий skel остаётся FAIL (2 hard в других Q), linkage — 0 errors,
7 warnings вне Q32/Q33. Независимой blind-приёмки нет. Go toolchain
отсутствует, пример с драйвером БД не компилировался. Состав Q/блоков
прежний; инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Core Q34–Q36 · авторская `П`.**
Сопоставлены Q1–Q4, Q29–Q32 и соседние Go-файлы. Q34 оставляет
проверяемые production-примеры Docker/Kubernetes, Prometheus и Google,
а кейс PayPal привязывает к 2020 году без вывода о нынешней доле Go.
Q35 уточняет Go 1.27: методы конкретных типов получили собственные
type-параметры, методы интерфейсов — нет; type-set unions не являются
tagged sum value. Убраны универсальные обещания о паузах GC и размере
бинарника. Q36 сравнивает Go и Java/Kotlin с учётом virtual threads
Java 21, интеграций, нагрузки и JDK 25 вместо безусловного вывода о
памяти или запуске. Источники: [Go FAQ](https://go.dev/doc/faq#usage),
[Prometheus FAQ](https://prometheus.io/docs/introduction/faq/),
[PayPal 2020](https://go.dev/solutions/paypal),
[Go 1.27](https://go.dev/doc/go1.27#language),
[Go spec](https://go.dev/ref/spec), [Go GC guide](https://go.dev/doc/gc-guide),
[Java 21 virtual threads](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html),
[JDK 25 changes](https://docs.oracle.com/en/java/javase/25/migrate/significant-changes-jdk-25.html).
Production MCQ Q34–Q36 B0 и freshness синхронизированы: 36 Q/36 блоков,
JSON/Markdown, локальные struct/skel и linkage Q34–Q36 PASS без сигналов.
Общий parity файла FAIL на прежних Q23 (длина варианта) и повторяющихся
10-граммах; linkage даёт одно прежнее предупреждение Q28. Независимой
blind-приёмки Q34–Q36 нет. Go 1.27 локально не запускался. Состав
Q/блоков прежний, инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Memory & GC, вводный абзац,
Q6/Q9–Q10/Q22–Q23 · авторская `О`.** Заголовки и ответы этих Q
сопоставлены между собой и с Go Core Q29/Q35/Q36 (`З` → `О`). Убраны
безусловные «STW под 1 мс», фиксированные диапазоны пауз и памяти,
универсальный вывод о меньшем RSS Go. Q23 переименован в вопрос о
сравнении памяти; номер и парный блок сохранены, локальное оглавление
обновлено, внешних ссылок на старый anchor не найдено. Уточнены
Green Tea по умолчанию с Go 1.26, две STW-фазы обычного цикла,
поколенческий ZGC в JDK 24+, цель `MaxGCPauseMillis=200` в G1 JDK 25
и границы `-Xmx`/`GOMEMLIMIT`. Источники: [Go GC guide](https://go.dev/doc/gc-guide),
[Go 1.26](https://go.dev/doc/go1.26#runtime),
[runtime/metrics](https://pkg.go.dev/runtime/metrics),
[G1 JDK 25](https://docs.oracle.com/en/java/javase/25/gctuning/garbage-first-g1-garbage-collector1.html),
[коллекторы JDK 25](https://docs.oracle.com/en/java/javase/25/gctuning/available-collectors.html),
[изменение ZGC](https://docs.oracle.com/en/java/javase/25/migrate/significant-changes-jdk-24.html),
[память HotSpot](https://docs.oracle.com/en/java/javase/25/gctuning/other-considerations.html).
Production MCQ Q6/Q9–Q10/Q22–Q23 B0 и freshness синхронизированы.
JSON/Markdown PASS, 27 Q/27 блоков; struct 0 отклонений, parity всего
файла PASS. Linkage: 0 errors, предупреждение Q10 B проверено автором
по тексту Q10 (ложная линейная зависимость STW от heap); прежние
предупреждения Q12/Q27 остаются. Общий skel FAIL на прежних Q11–Q13,
изменённые блоки без hard/warn. Независимой blind-приёмки MCQ нет;
остальные ответы файла не получили `П`. Go 1.26+ локально не запускался.
Состав Q/файлов прежний, инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Memory & GC Q5/Q8/Q11–Q13 ·
авторская `О`.** Q5 убирает гарантию стартовых 2 KiB и вывод о миллионе
goroutine; рост стека, его перенос и `SetMaxStack` разведены с
фактическим RSS. Q8 уточняет гибридный write barrier: старый указатель
затеняется при перезаписи, новый — при сером стеке; снята
неподтверждённая стоимость «2–5 нс». Q11 исправляет формулу Go 1.18+:
`heap target = live + (live + roots) × GOGC/100` и отличает цель от
точного момента запуска. Q12–Q13 уточняют мягкий лимит
`Sys - HeapReleased`, исходное `math.MaxInt64`, исключения для cgo/mmap,
совместное действие с `GOGC=off` и отсутствие гарантии от OOM контейнера.
Источники: [Go runtime HACKING](https://go.dev/src/runtime/HACKING),
[stack.go](https://go.dev/src/runtime/stack.go),
[mbarrier.go](https://go.dev/src/runtime/mbarrier.go),
[Go 1.8](https://go.dev/doc/go1.8#garbage_collector),
[Go GC guide](https://go.dev/doc/gc-guide),
[`runtime/debug`](https://pkg.go.dev/runtime/debug).
Production MCQ Q5/Q8/Q11–Q13 B0 и freshness синхронизированы. Общие
JSON/Markdown, struct (0 отклонений), skel (0 hard/warn) и parity всего
файла PASS; прежние skel hard Q11–Q13 устранены. Linkage: 0 errors,
прежние warnings Q10/Q27 остаются; изменённые Q без сигналов. Авторская
проверка правильности и дистракторов проведена, независимой blind-
приёмки нет. Остальные ответы файла не получили полную `П`; Go toolchain
в PATH отсутствует, примеры не запускались. Состав Q/блоков прежний,
инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Memory & GC Q1–Q4 · авторская `О`.**
Q1 исправляет утверждение, что GC «не трогает» stack: стеки служат
корнями при сканировании, а размещение значения не делает общий объект
потокобезопасным. Q2–Q3 убирают безусловные правила «`&x`/интерфейс/
closure/goroutine → heap» и единый порог 64 KB: результат зависит от
контекста, inlining и версии компилятора. Q4 различает диагностику
`-gcflags=-m=2` и измерение частоты аллокаций; `all=` включает
зависимости. Источники: [Go FAQ](https://go.dev/doc/faq#stack_or_heap),
[Go GC guide](https://go.dev/doc/gc-guide),
[Go compiler README](https://go.dev/src/cmd/compile/README),
[Go 1.27 size-specialized allocations, 16.09.2026](https://go.dev/blog/size-specialized-allocations).
Production MCQ Q1–Q4 B0 и freshness синхронизированы. Общие
JSON/Markdown, struct (0 отклонений), skel (0 hard/warn) и parity
всего файла PASS. Linkage: 0 errors, прежние warnings Q10/Q27
остаются; изменённые Q без сигналов. Авторская проверка правильного
варианта и дистракторов проведена, независимой blind-приёмки нет.
Примеры компилятора не запускались: Go toolchain в PATH отсутствует.
Остальные ответы файла не получили полную `П`. Состав Q/блоков
прежний, инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Memory & GC Q16–Q20 · авторская `О`.**
Q16 различает устойчивый рост живой кучи и разницу выборочных профилей:
`-diff_base` помогает локализовать удержание, но прирост без сопоставимой
нагрузки и GC не доказывает утечку. Q17 исправляет значение `allocs/op`,
убирает скомпилируемый в константу пример с выдуманным результатом и
ложный вывод «0 allocs/op ⇒ нет GC и максимальная скорость». Q18
поясняет, почему ограничение capacity не отделяет под-slice от большого
массива; для строк указан `strings.Clone`. Q19 убирает универсальные
«bucket'ы», фиксированные мегабайты и обещание немедленного снижения
RSS; учитывает Swiss Tables по умолчанию с Go 1.24. Q20 убирает
фиксированные 2 KiB на goroutine и различает живые ссылки и все когда-либо
захваченные объекты. Источники: [runtime/pprof](https://pkg.go.dev/runtime/pprof),
[pprof comparing profiles](https://github.com/google/pprof/blob/main/doc/README.md#comparing-profiles),
[testing](https://pkg.go.dev/testing#B.ReportAllocs),
[SliceTricks](https://go.dev/wiki/SliceTricks),
[strings.Clone](https://pkg.go.dev/strings#Clone),
[Go 1.24](https://go.dev/doc/go1.24#runtime),
[runtime maps](https://go.dev/src/internal/runtime/maps/map.go),
[runtime HACKING](https://go.dev/src/runtime/HACKING).
Production MCQ Q16–Q20 B0 и freshness синхронизированы. Авторская
проверка A/B/C/D, sections и русского текста проведена, независимой
blind-приёмки нет. JSON/Markdown 27 Q/27 блоков PASS, parity PASS,
skel 0 hard и linkage 0 errors; изменённые Q16–Q20 без сигналов.
Общий gate exit 1 из-за прежнего verbatim-сигнала Q22; linkage
warnings Q10/Q27 и anti-tell-сигналы Q3/Q5/Q6/Q23 остаются на разбор.
Локальный Go toolchain отсутствует, примеры не исполнялись. Состав
Q/блоков прежний, инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Memory & GC Q7/Q14–Q15/Q21 · авторская `О`.**
Q7 отделяет трёхцветную модель от гарантии FIFO и буквального сильного
инварианта чёрный→белый в гибридном барьере Go. Q14 уточняет: `runtime.GC()`
блокирует вызывающую goroutine до конца цикла, но не гарантирует
снижения RSS и завершения асинхронных очисток. Q15 описывает выборочный
heap-профиль после последнего GC, `DefaultServeMux`, отдельный сервер,
`?gc=1` и GET с Go 1.22. Q21 учитывает `runtime.AddCleanup` с Go 1.24,
различает панику при `arg == ptr` и косвенное удержание `ptr`, а также
границу `Cleanup.Stop()`/`KeepAlive`. Источники:
[Go GC guide](https://go.dev/doc/gc-guide),
[runtime/mbarrier.go](https://go.dev/src/runtime/mbarrier.go),
[`runtime.GC`](https://pkg.go.dev/runtime#GC),
[`net/http/pprof`](https://pkg.go.dev/net/http/pprof),
[`runtime/pprof`](https://pkg.go.dev/runtime/pprof),
[`runtime.AddCleanup`](https://pkg.go.dev/runtime#AddCleanup),
[`runtime.SetFinalizer`](https://pkg.go.dev/runtime#SetFinalizer),
[Go 1.24](https://go.dev/doc/go1.24#runtime).
Production MCQ Q7/Q14–Q15/Q21 B0 и freshness синхронизированы;
авторски проверены A/B/C/D, sections и русский текст. Независимой blind-
приёмки нет. JSON/Markdown 27 Q/27 блоков PASS, parity PASS, skel 0 hard,
linkage 0 errors; изменённые Q без сигналов. Общий gate exit 1 оставляет
прежний verbatim-сигнал Q22; linkage warnings Q10/Q27 и anti-tell
Q3/Q5/Q6/Q23 требуют отдельного разбора. Go toolchain в PATH нет,
примеры не запускались. Состав Q/блоков прежний, инвентарь не менялся.
Коммита нет.

**Контрольная точка 02.11.2026 — Go Memory & GC Q24–Q27 · авторская `О`.**
Q24 уточняет `sync.Pool`: `Get` может проигнорировать содержимое, элементы
не удерживаются гарантированно, выгода по аллокациям и паузам требует
замера; пример показывает владение буфером до `Put`. Q25 исправляет
сигнатуры `make`: у slice третий аргумент — capacity, у map второй —
приблизительный size hint; универсальные «bucket'ы» удалены. Q26
убирает противоречие `//go:noinline` в примере и неподтверждённое
«с Go 1.20 все функции встраиваются активнее», объясняет решения
компилятора и PGO. Q27 удаляет фиксированные 25% CPU, вывод из одного
`allocs/op` и советы применять `unsafe`/арены без профиля; предлагает
проверять CPU, паузы и SLO вместе. Источники: [`sync.Pool`](https://pkg.go.dev/sync#Pool),
[Go spec `make`](https://go.dev/ref/spec#Making_slices_maps_and_channels),
[Go slices](https://go.dev/blog/slices),
[Go compiler README](https://go.dev/src/cmd/compile/README),
[Go PGO guide](https://go.dev/doc/pgo),
[Go GC guide](https://go.dev/doc/gc-guide),
[`runtime/metrics`](https://pkg.go.dev/runtime/metrics),
[`runtime` gctrace](https://pkg.go.dev/runtime#hdr-Environment_Variables).
Production MCQ Q24–Q27 B0 и freshness синхронизированы. Авторски
проверены ключи, A/B/C/D, sections, SFPL/ANSC и русский текст.
JSON/Markdown 27 Q/27 блоков PASS, parity PASS, skel 0 hard,
linkage 0 errors; прежний Q27 linkage warning исчез. Общий gate exit 1
сохраняет прежний verbatim-сигнал Q22; Q10 linkage warning и anti-tell
Q3/Q5/Q6/Q23 требуют отдельного разбора. Независимой blind-приёмки нет;
Go toolchain в PATH отсутствует, пример не запускался. Состав Q/блоков
прежний, инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Memory & GC Q10/Q22/Q27 · авторская повторная `О`.**
Q10 уточнён: большой heap не задаёт секундную STW, так как полный
обход графа не помещается в одну глобальную остановку; неверный вариант
B и его sections приведены к этому объяснению. Q22 сверён с
[Go 1.26](https://go.dev/doc/go1.26#runtime),
[Go GC guide](https://go.dev/doc/gc-guide),
[JDK 25 G1](https://docs.oracle.com/en/java/javase/25/gctuning/garbage-first-g1-garbage-collector1.html)
и [удалением непоколенческого ZGC в JDK 24](https://docs.oracle.com/en/java/javase/25/migrate/significant-changes-jdk-24.html).
Ответ точнее описывает Green Tea; правильный MCQ A перефразирован,
сняв verbatim-сигнал. Q27 сопоставлен с Q10/Q17/Q24–Q26 и
[`runtime/metrics`](https://pkg.go.dev/runtime/metrics): отдельной
правки не потребовалось. Production MCQ Q10/Q22 B0 и freshness
синхронизированы; авторски проверены все A/B/C/D и sections.
JSON/Markdown 27 Q/27 блоков PASS, parity PASS, skel 0 hard,
linkage 0 errors/warnings и verbatim 0. Общий gate exit 1 остаётся
из-за старых anti-tell Q3/Q5/Q6/Q23; изменённые блоки без этих
сигналов. Независимой blind-приёмки и полной авторской `П` файла
пока нет. Состав Q/блоков прежний, инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Go Memory & GC Q3/Q5/Q6/Q23 и полная авторская `П` Q1–Q27.**
Q3 уточнён: размер — ограничение размещения на стеке, а не оценка
«выгодности». В production MCQ Q3 A, Q5 D, Q6 A и Q23 B изменены
без смены ключей. Убраны формальные подсказки «только wrong с
категоричностью», «смягчение только у correct» и «числа только у
correct»; содержание ошибочных моделей и их sections сверены с
ответами. Последовательно прочитаны все Q1–Q27, включая прежние
Q10/Q22/Q27. Версионные утверждения сверены с
[Go GC guide](https://go.dev/doc/gc-guide),
[Go FAQ](https://go.dev/doc/faq#stack_or_heap),
[runtime/stack.go](https://go.dev/src/runtime/stack.go),
[Go 1.26](https://go.dev/doc/go1.26#runtime),
[Go 1.24](https://go.dev/doc/go1.24#runtime),
[`runtime/debug`](https://pkg.go.dev/runtime/debug#SetMemoryLimit),
[`runtime.AddCleanup`](https://pkg.go.dev/runtime#AddCleanup),
[`runtime/metrics`](https://pkg.go.dev/runtime/metrics),
[`sync.Pool`](https://pkg.go.dev/sync#Pool),
[Go 1.27 allocation blog](https://go.dev/blog/size-specialized-allocations)
и [JDK 25 memory areas](https://docs.oracle.com/en/java/javase/25/gctuning/other-considerations.html).
Это авторская содержательная проверка теории `П`, не независимая
MCQ-приёмка. JSON/Markdown 27 Q/27 блоков PASS, parity PASS, anti-tell
0, linkage 0 errors/warnings, skel 0 hard/2 warn, verbatim 0; общий
pre-check exit 0. Frontmatter delimiters, Q/TOC, See also, JSON Q
parity и отсутствие inline MCQ проверены; YAML parser в окружении
отсутствует. Go toolchain в PATH нет, примеры не исполнялись. Freshness
синхронизирован, инвентарь не менялся, независимый blind-review остаётся
в очереди. Коммита нет.

**Контрольная точка 02.11.2026 — Python Core Q1–Q5 · авторская `О`.**
В Q4 уточнён контракт `__eq__`/`__hash__`: равным ключам нужен
одинаковый хеш, а переопределение `__eq__` без `__hash__` делает
экземпляр нехешируемым. В production MCQ Q2 B0 обнаружен и исправлен
второй правильный ответ: прежний wrong A верно описывал shallow/deep
copy. Обновлены тексты Q1 B0, Q2 B0, Q4 B0, Q5 B0, содержательные
sections правильных вариантов и ссылки на соседние Q. Q3 B0 получил
узкий видимый stem про `list`/`tuple`/`set`/`dict`, поскольку свойства
`frozendict` отдельно проверяет Q3 B1; linkage оставляет предупреждение
о несовпадении с более широким заголовком Q3, это осознанная граница
двух блоков. Все A/B/C/D и sections Q1–Q5/B0–B1 прочитаны автором.
Проверены [Python 3.15 data model](https://docs.python.org/3.15/reference/datamodel.html),
[`copy`](https://docs.python.org/3.15/library/copy.html),
[коллекции и `frozendict`](https://docs.python.org/3.15/builtins/stdtypes.html),
[hashable](https://docs.python.org/3.15/glossary.html#term-hashable),
[defaults](https://docs.python.org/3.15/tutorial/controlflow.html#default-argument-values)
и [`sentinel()`](https://docs.python.org/3.15/builtins/functions.html#sentinel).
Локальный Python подтвердил alias/copy/deepcopy, `__eq__`/`__hash__`
и переиспользование default; Python 3.15 в PATH нет, его новые API
локально не исполнялись. JSON/Markdown 12 Q/15 блоков PASS, parity
PASS; у изменённой группы anti-tell 0, skel 0 hard, verbatim 0.
Общий gate exit 1 из-за старых Q6/Q8 anti-tell, Q6–Q9 skel и
Q7/Q12 linkage errors. Независимой blind-приёмки MCQ и полной `П`
файла нет. Freshness обновлён, состав Q/блоков прежний, инвентарь
не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Python Core Q6–Q9 · авторская `О`.**
В Q6 добавлен пример late binding рядом с фиксацией через default.
Q7 явно объясняет `iter()` и `next()`, что устранило linkage error
правильного MCQ. Q8 уточняет пределы `except`/`finally`/`__exit__`:
`return` в `finally` может скрыть исходную ошибку. Q9 уточняет, что
`field(default_factory=list)` вызывается при пропуске поля; явно
переданный список остаётся общим. Production MCQ Q6–Q9 B0
синхронизированы: все A/B/C/D и sections прочитаны автором, уточнены
дистракторы, применение и ссылки; в Q7 неверные варианты охватывают
тот же путь `iter()`/`next()`/`yield` без второго правильного ответа. Проверены
[Python 3.15 execution model](https://docs.python.org/3.15/reference/executionmodel.html#resolution-of-names),
[iterators/generators](https://docs.python.org/3.15/builtins/stdtypes.html#iterator-types),
[`try`/`finally`/`with`](https://docs.python.org/3.15/reference/compound_stmts.html#the-try-statement),
[context managers](https://docs.python.org/3.15/reference/datamodel.html#context-managers)
и [`dataclasses`](https://docs.python.org/3.15/library/dataclasses.html).
Локальный Python выполнил примеры closures, ленивого generator,
неподавленного исключения в context manager и поведения фабрики.
JSON/Markdown 12 Q/15 блоков PASS, parity PASS, anti-tell 0,
skel 0 hard/1 warn, verbatim 0; для Q6–Q9 linkage errors/warnings 0.
Общий gate exit 1 оставляет прежний Q12 B1 STEM-DRIFT; Q3 B0
STEM-MISMATCH отражает отдельный узкий блок о четырёх контейнерах.
Независимой blind-приёмки MCQ и полной авторской `П` файла нет.
Freshness обновлён; состав Q/блоков прежний, инвентарь не менялся.
Коммита нет.

**Контрольная точка 02.11.2026 — Python Core Q10–Q12 и файл целиком · авторская `П`.**
Q10 сверён с [Python 3.15 `io`](https://docs.python.org/3.15/library/io.html#text-encoding)
и [PEP 686](https://peps.python.org/pep-0686/): UTF-8 по умолчанию,
явный `encoding="locale"`, отключение UTF-8 Mode и предупреждение о
неявной кодировке описаны с границей версий. Q11 сверён с
[PEP 810](https://peps.python.org/pep-0810/): отложенная загрузка
может стать немедленной при глобальном режиме или фильтре импортов.
Q12 сверён с [документацией sampling](https://docs.python.org/3.15/library/profiling.sampling.html),
[tracing](https://docs.python.org/3.15/library/profiling.tracing.html),
[PEP 831](https://peps.python.org/pep-0831/) и
[perf HOWTO](https://docs.python.org/3/howto/perf_profiling.html):
добавлены основной поток/`--all-threads`, граница синтетического
`--native` и полного нативного стека. Production MCQ Q11 B0 и Q12 B0
синхронизированы. У Q12 B1 устранён STEM-DRIFT при сохранении узкого
вопроса о frame pointers. Весь Markdown Q1–Q12 прочитан последовательно;
для всех 15 MCQ-блоков ранее и сейчас точечно проверены A/B/C/D и
sections. Structure 12 Q/15 блоков, parity, anti-tell, skel pre-check,
verbatim и Markdown PASS; общий gates exit 0, linkage errors 0,
остались два осознанных предупреждения STEM-MISMATCH для узких Q3 B0
и Q12 B1. Это авторская `П`, не независимая MCQ-приёмка и не `FINAL`.
Python 3.15 локально недоступен для запуска новых примеров; выигрыш
старта и накладные расходы профилирования не измерялись. Freshness
обновлён; состав Q/файлов прежний, инвентарь не менялся. Коммита нет.

**Контрольная точка 02.11.2026 — Python Typing Q1–Q4 · авторская `П`.**
Весь `programming-languages/python/python-typing-interview.md` прочитан
последовательно. Q1 уточнён по [Python `typing`](https://docs.python.org/3.15/library/typing.html)
и [`annotationlib`](https://docs.python.org/3.15/library/annotationlib.html#annotation-semantics):
в 3.11–3.13 обычные аннотации вычисляются сразу, с `future` хранятся
строками, а с 3.14 по умолчанию вычисляются отложенно; получение их
значений может выполнить код. Q2 дополнен эквивалентом `TypeVar` для
3.11 по [спецификации generics](https://typing.python.org/en/latest/spec/generics.html).
Q3 сверён со [спецификацией Protocol](https://typing.python.org/en/latest/spec/protocol.html).
Q4 уточнён по [спецификации TypedDict](https://typing.python.org/en/latest/spec/typeddict.html)
и [PEP 728](https://peps.python.org/pep-0728/): открытость допускает
структурно совместимое значение с лишними ключами, но не произвольный
лишний ключ в собственном конструкторе или литерале при статической
проверке. Production MCQ Q1/Q2/Q4 B0 и Q4 B1 синхронизированы;
все A/B/C/D и sections пяти блоков автором проверены. JSON/Markdown
4 Q/5 блоков, parity, anti-tell, linkage, skel pre-check, verbatim и
Markdown PASS; общий gates exit 0. Legacy `TypeVar` пример исполнен
локально на Python 3.14.4; Python 3.11 и 3.15 локально не запускались.
Freshness обновлён, состав вопросов/файлов прежний, инвентарь не
менялся. Независимой blind-приёмки MCQ нет, `FINAL` не ставить.

**Контрольная точка 02.11.2026 — Python Concurrency Q1–Q4 · авторская `О`.**
Q1–Q2 и все их MCQ сверены с [asyncio](https://docs.python.org/3.15/library/asyncio.html),
[threading/GIL](https://docs.python.org/3.15/library/threading.html#gil-and-performance-considerations)
и [concurrent.futures](https://docs.python.org/3.15/library/concurrent.futures.html).
Q3 уточнён по [документации создания Task](https://docs.python.org/3.15/library/asyncio-task.html#creating-tasks):
с Python 3.14 `eager_start=True` или фабрика могут начать Task сразу,
но простой вызов `async def` не исполняет тело. Q4 уточнён по
[TaskGroup](https://docs.python.org/3.15/library/asyncio-task.html#task-groups)
и [`gather`](https://docs.python.org/3.15/library/asyncio-task.html#asyncio.gather):
группа отменяет только ещё работающих соседей, а `gather` после
передачи исключения не отменяет их даже при последующем `cancel()`.
Исправлен пример Q4: `return` теперь находится внутри функции.
Production MCQ Q1–Q4 B0 переписаны для сравнимой формы без жёстких
skel-сигналов в этой группе; варианты A/B/C/D и sections автором
проверены, Q3/Q4 sections синхронизированы. Локальный CPython 3.14.4
подтвердил поведение coroutine, eager Task, TaskGroup и gather.
Structure 9 Q/10 блоков, parity PASS, linkage errors 0, Markdown PASS;
для Q1–Q4 anti-tell 0 и skel hard 0. Общий gates exit 1 относится к
старым anti-tell/skel сигналам Q5–Q9; они остаются следующим пакетом.
Freshness обновлён. Авторская полная `П` всего файла и независимая
MCQ-приёмка не проведены; состав Q/файлов прежний, инвентарь не менялся.

**Контрольная точка 02.11.2026 — Python Concurrency Q5–Q9 и весь файл · авторская `П`.**
Q5/Q6 сверены с [asyncio tasks](https://docs.python.org/3.15/library/asyncio-task.html):
`CancelledError` — `BaseException`, `asyncio.timeout()` преобразует
отмену в `TimeoutError` за пределами блока, `to_thread` не прерывает
работающий поток. Пример Q5 помещён внутрь coroutine. Q7 уточнён по
[asyncio Queue](https://docs.python.org/3.15/library/asyncio-queue.html)
и [Semaphore](https://docs.python.org/3.15/library/asyncio-sync.html#semaphore):
backpressure требует `maxsize > 0` и ожидания `put` producer-ом до
создания новой работы. Q8 проверен по
[ProcessPoolExecutor](https://docs.python.org/3.15/library/concurrent.futures.html#processpoolexecutor):
picklable данные/функция, импортируемый `__main__`, deadlock при
вызове методов пула из worker-а. Q9 проверен по
[free-threading guide](https://docs.python.org/3.15/howto/free-threading-python.html)
и [PEP 803](https://peps.python.org/pep-0803/): различены сборка и
runtime GIL, `abi3t` и `abi3.abi3t`, добавлены конкретные теги
`cp315-abi3`/`cp315-abi3.abi3t` и граница защиты общего состояния.
Production MCQ Q5–Q9 B0 и Q9 B1 автором проверены по A/B/C/D и
sections; дистракторы Q5–Q9 переписаны для сравнимой формы и
самодостаточной связи с соответствующим ответом. Весь Markdown Q1–Q9
прочитан последовательно. Локальный CPython 3.14.4 подтвердил
`timeout`, ограниченную очередь и то, что отмена ожидания `to_thread`
не останавливает выполняющийся поток. JSON/Markdown 9 Q/10 блоков,
parity, anti-tell 0, skel 0 hard, Markdown и общий gates exit 0.
Linkage errors 0; один осознанный STEM-MISMATCH остаётся у узкого Q9 B1.
Python 3.15 и free-threaded сборка локально не исполнялись. Freshness
обновлён, инвентарь не менялся. Независимой blind-приёмки MCQ нет;
авторскую `П` не считать `FINAL` для MCQ.

**Контрольная точка 02.11.2026 — Python Packaging Q1–Q5 · авторская `П`.**
Весь Markdown и пять MCQ-блоков Q1–Q5 B0 последовательно прочитаны;
для каждого A/B/C/D и sections проверены правильность, различимость
и связь с ответом. Q1–Q3 сверены с [PyPA `pyproject.toml`](https://packaging.python.org/en/latest/specifications/pyproject-toml/),
[package formats](https://packaging.python.org/en/latest/discussions/package-formats/)
и [dependency groups](https://packaging.python.org/en/latest/specifications/dependency-groups/).
В Q4 по [pip Secure Installs](https://pip.pypa.io/en/stable/topics/secure-installs/#hash-checking-mode)
добавлено требование `--require-hashes`: явно закрепить и захешировать
каждую прямую и транзитивную зависимость; также сверены
[repeatable installs](https://pip.pypa.io/en/stable/topics/repeatable-installs/)
и [спецификация `pylock.toml`](https://packaging.python.org/en/latest/specifications/pylock-toml/).
В Q5 по [Python 3.15 `site`](https://docs.python.org/3.15/library/site.html)
и [PEP 829](https://peps.python.org/pep-0829/) уточнены повторное
исполнение одинаковых `.start`-записей и продолжение после ошибки
hook. Парный MCQ синхронизирован; у Q5 исправлено совпадение
формулировки вопроса с Markdown. Общие gates: exit 0, structure 5 Q/5
блоков, parity PASS, anti-tell 0, skel hard 0, linkage errors 0,
verbatim 0; Markdown PASS. Freshness обновлён. Python 3.15 локально
не исполнялся, независимой blind-приёмки MCQ нет, `FINAL` не ставить.
Состав Q и файлов прежний, инвентарь не менялся.

**Контрольная точка 02.11.2026 — Python Testing Q1–Q4 · авторская `О`.**
Пересечения с Python Core, Unit Testing Q24 и Integration Testing Q32
проверены: общий смысл fixtures там есть, но ограничения и API pytest,
`unittest` и `patch` раскрываются в текущем файле. Q1 уточнён по
[pytest/unittest.TestCase](https://docs.pytest.org/en/stable/how-to/unittest.html):
методы `TestCase` не получают fixtures напрямую и не параметризуются
обычным способом; `autouse`/`usefixtures` доступны. Q2 по
[pytest fixtures](https://docs.pytest.org/en/stable/how-to/fixtures.html)
различает конец `function` и `session` scope, очистку ранее успешных
fixtures при ошибке до `yield` и своевременный `request.addfinalizer`.
Q3 по [pytest parametrization](https://docs.pytest.org/en/stable/how-to/parametrize.html)
уточняет роль `id` и marks отдельного случая. Q4 по
[Python `unittest.mock`](https://docs.python.org/3/library/unittest.mock.html#where-to-patch)
различает `from gateway import send` и `import gateway`, оговаривает
границу `autospec`. MCQ Q1–Q4 B0 сверены по всем A/B/C/D/sections,
исправлены Q2 scope/teardown и Q2/Q3 stem; Q2–Q4 options избавлены
от выявленных формальных подсказок. Создан freshness-sidecar. Markdown
PASS; JSON structure 7 Q/7 блоков, parity PASS, linkage errors 0.
Общий gates exit 1: в Q1–Q4 нет anti-tell/skel hard; оставшиеся
anti-tell Q6, skel hard Q5–Q7 и linkage warnings Q5/Q6 относятся к
следующему пакету. Внешнее выполнение pytest-примеров не проводилось;
независимой blind-приёмки нет. Состав Q/файлов прежний, инвентарь не
менялся.

**Контрольная точка 02.11.2026 — Python Testing Q5–Q7 и весь файл · авторская `П`.**
Q5 по [pytest monkeypatch](https://docs.pytest.org/en/stable/how-to/monkeypatch.html)
и [Python autospeccing](https://docs.python.org/3/library/unittest.mock.html#autospeccing)
уточнён: срок подмены, `monkeypatch.context()` и динамические атрибуты,
не видимые `autospec`. Q6 по [Python `IsolatedAsyncioTestCase`](https://docs.python.org/3.15/library/unittest.html#unittest.IsolatedAsyncioTestCase),
[pytest-asyncio 1.4.1](https://pytest-asyncio.readthedocs.io/en/v1.4.1/concepts.html)
и [asyncio cancellation](https://docs.python.org/3.15/library/asyncio-task.html#task-cancellation)
различает strict/auto marker, scope loop и проверку cleanup после
`Task.cancel()`. Q7 по [SQLAlchemy 2.0 external transaction](https://docs.sqlalchemy.org/en/20/orm/session_transaction.html#joining-a-session-into-an-external-transaction-such-as-for-test-suites)
и [PostgreSQL `COMMIT`](https://www.postgresql.org/docs/current/sql-commit.html)
уточняет, почему откат тестовой внешней транзакции не доказывает
настоящую фиксацию. MCQ Q5–Q7 B0 переписаны и автором сверены по
каждому A/B/C/D/sections; Q6 stem совпадает с Markdown. Весь Markdown
Q1–Q7 перечитан последовательно. Общие gates exit 0: 7 Q/7 блоков,
parity PASS, anti-tell 0, skel hard 0, linkage errors/warnings 0;
Markdown PASS. Freshness обновлён, состав Q/файлов прежний, инвентарь
не менялся. Примеры не запускались в Python/pytest окружении; MCQ
ждут независимой blind-приёмки, авторскую `П` не считать `FINAL`.

**Контрольная точка 03.11.2026 — FastAPI Q1–Q4 · авторская `О`.**
Соседние Python Concurrency Q1–Q4, Database Q3/Q7 и Testing Q2/Q7
сверены по заголовкам и ответам: они объясняют asyncio, БД и pytest,
а FastAPI Q1–Q4 — правила самого фреймворка. Q1 сверён с
[FastAPI async](https://fastapi.tiangolo.com/async/), Q2 — с
[sub-dependencies](https://fastapi.tiangolo.com/tutorial/dependencies/sub-dependencies/):
thread pool применяется к вызываемым FastAPI sync handlers/dependencies,
кеш `Depends` ограничен запросом. Q3 по [dependencies with yield](https://fastapi.tiangolo.com/tutorial/dependencies/dependencies-with-yield/)
и [истории изменений](https://fastapi.tiangolo.com/advanced/advanced-dependencies/#dependencies-with-yield-and-scope)
уточнён: с 0.121.0 `scope="function"` закрывает ресурс до ответа,
`scope="request"` — после; в 0.106.0–0.117.x обычный cleanup был до
ответа. Для streaming нужен подходящий срок ресурса. Q4 по
[response model](https://fastapi.tiangolo.com/tutorial/response-model/)
и [direct Response](https://fastapi.tiangolo.com/advanced/response-directly/)
уточняет серверную ошибку при неверном выходе и обход Pydantic-фильтра
при прямом `Response`. MCQ Q1–Q4 B0 и все A/B/C/D/sections автором
сверены, варианты переписаны, Q2–Q4 stem синхронизированы. Markdown
PASS; structure 7 Q/7 блоков, parity PASS, у Q1–Q4 anti-tell 0,
skel hard 0, linkage errors/warnings 0. Общие gates exit 1 только
из-за Q5–Q7: anti-tell Q5/Q6, skel hard Q5–Q7, stem warnings Q5–Q7.
Freshness обновлён. FastAPI-примеры локально не исполнялись;
независимой MCQ-приёмки нет. Состав Q/файлов прежний, инвентарь не
менялся.

**Контрольная точка 03.11.2026 — FastAPI Q5–Q7 · авторская `П` всей темы.**
Q5 по [handling errors](https://fastapi.tiangolo.com/tutorial/handling-errors/)
различает `RequestValidationError` со стандартным 422, заменяемый
exception handler, прикладной `HTTPException` и безопасную обработку
внутреннего сбоя. Q6 по [lifespan](https://fastapi.tiangolo.com/advanced/events/)
разделяет ресурс приложения и ресурс запроса; исправлено противоречие
MCQ Q6 C между вариантом и его разбором. Q7 по
[dependency overrides](https://fastapi.tiangolo.com/advanced/testing-dependencies/),
[testing events](https://fastapi.tiangolo.com/advanced/testing-events/) и
[async tests](https://fastapi.tiangolo.com/advanced/async-tests/)
восстанавливает прежний mapping overrides после теста, запускает
lifespan через `with TestClient` и различает синхронный и async-тест.
Соседние Python Concurrency Q1–Q4, Database Q3/Q7 и Testing Q2/Q7 не
заменяют правила FastAPI. Весь Markdown Q1–Q7 перечитан: авторская `П`.
MCQ Q5–Q7 B0 и A/B/C/D/sections синхронизированы с теорией; структура
7 Q/7 блоков, parity PASS, anti-tell 0, linkage 0 errors/warnings,
skel 0 hard/warn, общий pre-check exit 0. Это авторская проверка, а не
независимая MCQ-приёмка или blind `FINAL`. Примеры локально не
исполнялись. Состав Q/файлов прежний, инвентарь не менялся.

**Контрольная точка 03.11.2026 — MySQL Q1–Q3 · авторская `О`.**
Пересечения сверены по заголовкам и ответам: `sql-interview.md`
Q16 описывает UPSERT, Q45 — JDBC/JPA, `database-transactions-interview.md`
Q9 — уровень изоляции по умолчанию. Они не объясняют выбор InnoDB,
кластерный индекс и MySQL `EXPLAIN`. В MySQL Q1 по
[InnoDB introduction](https://dev.mysql.com/doc/refman/8.4/en/innodb-introduction.html),
[redo](https://dev.mysql.com/doc/refman/8.4/en/innodb-redo-log.html) и
[undo](https://dev.mysql.com/doc/refman/8.4/en/innodb-undo-logs.html)
уточнена зависимость default engine от конфигурации и граница SQL-транзакции.
В Q2 по [index types](https://dev.mysql.com/doc/refman/8.4/en/innodb-index-types.html)
уточнён кластерный ключ при отсутствии PK и исключение covering index.
В Q3 по [EXPLAIN](https://dev.mysql.com/doc/refman/8.4/en/explain.html)
разделены оценки плана и исполнение `EXPLAIN ANALYZE`; в MySQL 8.4
последний поддерживает `SELECT`, `TABLE` и многотабличные `UPDATE`/`DELETE`.
MCQ Q1–Q3 B0 переписаны и все A/B/C/D/sections автором сверены с
теорией. Структура всего файла 6 Q/6 блоков и parity PASS; у Q1–Q3
anti-tell 0, skel hard 0, linkage errors/warnings 0. Общий pre-check
exit 1 относится к старым Q4–Q6: skel hard Q4–Q6, anti-tell Q4,
linkage warning Q4. Независимая MCQ-приёмка Q1–Q3 не проводилась.
Состав Q/файлов прежний, инвентарь не менялся.

**Контрольная точка 03.11.2026 — MySQL Q4–Q6 · авторская `П` всей темы.**
В Q4 по [consistent reads](https://dev.mysql.com/doc/refman/8.4/en/innodb-consistent-read.html),
[locking reads](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking-reads.html)
и [isolation levels](https://dev.mysql.com/doc/refman/8.4/en/innodb-transaction-isolation-levels.html)
разделены снимок `REPEATABLE READ`/`READ COMMITTED`, видимость своих
изменений и locking read в явной транзакции. В Q5 по
[connection character sets](https://dev.mysql.com/doc/refman/8.4/en/charset-connection.html)
и [utf8mb4](https://dev.mysql.com/doc/refman/8.4/en/charset-unicode-utf8mb4.html)
исправлена прежняя смешанная цепочка: `character_set_results` относится
к ответам, а при записи проверяют вход, соединение и столбец. В Q6 по
[Connector/J troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html),
[wait_timeout](https://dev.mysql.com/doc/refman/8.4/en/server-system-variables.html#sysvar_wait_timeout)
и [HikariCP](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
уточнены проверка простаивавшего соединения, граница настроек пула и
неопределённый исход записи при потере ответа. Соседний
`jdbc-connection-pool-interview.md` Q6 покрывает механизм пула, а MySQL
Q6 — конкретную диагностику Connector/J. Весь Markdown MySQL Q1–Q6
перечитан: авторская `П`. MCQ Q4–Q6 B0 и все A/B/C/D/sections
синхронизированы; ранее найденные сигналы Q4–Q6 устранены. Общий
pre-check exit 0: структура 6 Q/6 блоков, parity PASS, anti-tell 0,
skel hard/warn 0, linkage errors/warnings 0. Независимая содержательная
и blind-приёмка MCQ ещё не проводилась. Новых Q и файлов нет,
инвентарь не менялся.

**Контрольная точка 03.11.2026 — JDBC/пулы Q1–Q3 · авторская `О`.**
Соседние SQL Q45 (JDBC/JPA), Database Transactions Q33–Q34
(занятость пула и Spring propagation), Spring Data JDBC Q1/Q16
(репозитории и транзакции) сверены по заголовкам и ответам: они не
заменяют контракт `DataSource`, ресурсный цикл и собственную JDBC-
транзакцию. Q1 по [Java SE 25 DataSource API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/javax/sql/DataSource.html)
различает фабрику и наличие пула. Q2 по [Statement](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html),
[ResultSet](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/ResultSet.html)
и [Spring DataSourceUtils](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/datasource/DataSourceUtils.html)
исправлен: закрытие Statement автоматически закрывает его ResultSet;
`DataSourceUtils.getConnection/releaseConnection` учитывает Spring-
транзакцию. Q3 по [Connection API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Connection.html)
уточняет default auto-commit, возможную настройку пула,
implementation-defined исход `close()` при активной транзакции и
неопределённый исход сбоя во время commit. MCQ Q1–Q3 B0 и все
A/B/C/D/sections синхронизированы. Структура 6 Q/6 блоков и parity
PASS; в изменённой группе anti-tell 0, skel hard 0, linkage
errors/warnings 0. Общий pre-check exit 1 из-за старых Q4–Q6:
linkage error Q5, warning Q4, skel hard Q4–Q6. Независимой
содержательной и blind-приёмки MCQ нет. Состав Q/файлов прежний,
инвентарь не менялся.

**Контрольная точка 03.11.2026 — JDBC/пулы Q4–Q6 · авторская `П` всей темы.**
Q4 по [Java SE 25 BatchUpdateException](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/BatchUpdateException.html) и [Statement](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html) различает остановку и продолжение batch после ошибки, `SUCCESS_NO_INFO`/`EXECUTE_FAILED`, `executeLargeBatch` и границы отката. Q5 по [JDBC `setFetchSize`](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Statement.html#setFetchSize(int)), [pgJDBC](https://jdbc.postgresql.org/documentation/query/) и [Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-performance-extensions.html) различает итерацию `ResultSet` и фактическую буферизацию драйвера. Q6 по [HikariCP configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby) и [JMX](https://github.com/brettwooldridge/HikariCP/wiki/MBean-%28JMX%29-Monitoring-and-Management) больше не приравнивает любой `getConnection()` timeout к заполненному пулу: сопоставляет active/total/pending и сбои создания соединений; `leakDetectionThreshold` не закрывает соединение и не доказывает утечку. Markdown Q1–Q6 перечитан полностью. MCQ Q4–Q6 B0 и A/B/C/D/sections синхронизированы. Общий pre-check exit 0: структура 6 Q/6 блоков, parity PASS, anti-tell 0, skel hard/warn 0, linkage errors/warnings 0. Независимая содержательная и blind-приёмка Q1–Q6 ожидается. Состав Q/файлов не менялся, инвентарь прежний.

**Контрольная точка 03.11.2026 — Spring Batch Q6 B0/B1, Q28 B0, Q38 B0 · повторная авторская `О`.**
Q6 по [Batch 6 JobRepository](https://docs.spring.io/spring-batch/reference/job/configuring-repository.html), [схеме метаданных](https://docs.spring.io/spring-batch/reference/schema-appendix.html) и [ItemStream](https://docs.spring.io/spring-batch/reference/readers-and-writers/item-stream.html) больше не называет JobRepository полным аудитом: сохранённые метаданные включают статус, счётчики и сообщения о завершении, а срок хранения задаёт приложение. Resourceless не подходит для перезапуска, конкурентного исполнения и обмена состоянием через ExecutionContext. По [Boot 3.5](https://docs.spring.io/spring-boot/3.5/how-to/batch.html), [Boot 4.1](https://docs.spring.io/spring-boot/reference/io/spring-batch.html) и [Batch 5.0](https://docs.spring.io/spring-batch/docs/5.0.1/reference/html/whatsnew.html) перечитаны Q28/Q38 и четыре варианта каждого блока. В Q6 B0/B1 и Q28 B0 улучшен каркас дистракторов без изменения ключа; Q38 B0 оставлен по фактам. Markdown, JSON и freshness-sidecar синхронизированы. Структура 43 Q/44 блока, parity и Markdown PASS; общий `gates.sh` FAIL на прежнем Q15 `NEIGHBOR-LEAK` и предупреждениях Q15/Q16/Q23/Q43. У выбранных Q нет linkage error; skel pre-check не является содержательной приёмкой. Подготовлен свежий [blind-input](docs/mcq-quality/runs/20261103-spring-batch-q6-q28-q38-r1/blind-input.jsonl) с manifest. Фазы A/B и record не выполнялись, независимый `FINAL` не заявлен. Состав Q/файлов не изменился, инвентарь прежний.

**Контрольная точка 03.11.2026 — Spring Batch Q14–Q16 · авторская `О`.**
При сверке соседних вопросов найдено больше, чем сигнал Q15: Q14 ошибочно обещал только `LIMIT/OFFSET`, новое соединение на страницу, безусловную потокобезопасность и более высокую скорость курсора. По [JdbcPagingItemReader API](https://docs.spring.io/spring-batch/reference/api/org/springframework/batch/infrastructure/item/database/JdbcPagingItemReader.html) и [Database readers](https://docs.spring.io/spring-batch/reference/readers-and-writers/database.html) уточнены `PagingQueryProvider`, уникальный sort key, `saveState(false)` при общем многопоточном использовании, отдельный запрос на страницу и зависимость соединения от транзакции. Q15 по [JpaPagingItemReader API](https://docs.spring.io/spring-batch/reference/api/org/springframework/batch/infrastructure/item/database/JpaPagingItemReader.html) исправлен: reader сам открывает транзакцию чтения, делает `flush/clear` после страницы и отдаёт detached сущности; внешняя `readOnly`-аннотация не управляет его транзакцией. [JpaCursorItemReader API](https://docs.spring.io/spring-batch/reference/api/org/springframework/batch/infrastructure/item/database/JpaCursorItemReader.html) подтверждает появление в Batch 4.3, а не 5.0; фактическое потоковое чтение зависит от провайдера/драйвера. Q16 по [FlatFileItemReader](https://docs.spring.io/spring-batch/reference/readers-and-writers/flat-files/file-item-reader.html), [FlatFileItemWriter](https://docs.spring.io/spring-batch/reference/readers-and-writers/flat-files/file-item-writer.html) и [DelimitedBuilder](https://docs.spring.io/spring-batch/reference/api/org/springframework/batch/infrastructure/item/file/builder/FlatFileItemWriterBuilder.DelimitedBuilder.html) уточняет пропуск заголовка, TSV/fixed-width и необходимость проверять CSV-значения с разделителем, кавычкой и переводом строки; `quoteCharacter` доступен с 5.1. Markdown, production MCQ Q14–Q16 B0 и freshness-sidecar синхронизированы. Сигналы `NEIGHBOR-LEAK` Q15 и `LOW-COVERAGE` Q15/Q16 устранены. Структура 43 Q/44 блока, parity, Markdown и общий pre-check exit 0; linkage errors 0, warnings 2 на старых Q23/Q43, skel hard 0. Автором прочитаны все A/B/C/D и sections Q14–Q16; независимых фаз A/B и `FINAL` нет. Состав Q/файлов не менялся, инвентарь прежний. После изменения общего файла старый blind-input Q6/Q28/Q38 r1 устарел: подготовлен [r2](docs/mcq-quality/runs/20261103-spring-batch-q6-q28-q38-r2/blind-input.jsonl). Для Q14–Q16 подготовлен отдельный [blind-input](docs/mcq-quality/runs/20261103-spring-batch-q14-q16-r1/blind-input.jsonl).

**Контрольная точка 03.11.2026 — Spring Batch Q22–Q25 · авторская `О`.**
При проверке Q23/Q24 найден версионный разрыв. По [Batch 5.2 Scaling](https://docs.spring.io/spring-batch/reference/5.2/scalability.html) TaskExecutor выполняет целые чанки в worker потоках; по [Batch 6 Scaling](https://docs.spring.io/spring-batch/reference/scalability.html) он параллелит `ItemProcessor`, а чтение и запись остаются в основном потоке. В Q24 обновлены ответ, пример на `chunk(int).transactionManager(...)` и MCQ; `throttleLimit()` больше не рекомендован как настройка нового примера. Q23 исправляет ложное требование брокера для partitioning: разделы могут работать локально либо удалённо. Добавлены local chunking и remote step Batch 6, различены chunk/partition/целый Step. Сигнал `LOW-COVERAGE` Q23 устранён содержательной синхронизацией теории и вариантов. В соседнем Q22 по [Retry Logic](https://docs.spring.io/spring-batch/reference/step/chunk-oriented-processing/retry-logic.html) исправлено отрицание повторов `ItemReader`: следующий `read()` возможен, но тот же элемент не гарантирован, если reader уже сдвинул позицию. В Q25 по [Partitioning](https://docs.spring.io/spring-batch/reference/scalability.html) устранено обещание безусловно корректного restart: нужны устойчивые границы разделов, состояние reader, персистентный репозиторий и безопасный повтор записи. Пример также обрабатывает пустой источник и неположительный `gridSize`. Теория, production MCQ Q22–Q25 B0 и freshness-sidecar синхронизированы; Q22–Q25 A/B/C/D и sections перечитаны автором. Структура 43 Q/44 блока, parity, Markdown и общий pre-check exit 0; linkage errors 0, один warning на прежнем Q43, skel hard 0. Это не независимая MCQ-приёмка и не `FINAL`; новые Q/файлы не появлялись, инвентарь прежний. Ранее подготовленные blind-input Q6/Q28/Q38 r2 и Q14–Q16 r1 устарели по полным хешам после этой правки общего файла; новые пакеты готовить после завершения оставшихся правок Batch.

**Контрольная точка 03.11.2026 — Spring Batch Q39–Q40 · авторская `О`.** Q39 по [Spring Batch Scalability](https://docs.spring.io/spring-batch/reference/scalability.html) больше не выводит диапазоны ID из `COUNT(*)`, не обещает, что `gridSize` равен числу одновременно работающих worker-ов, и отсылает к Q25 за реализацией Partitioner/reader. Локальный `TaskExecutorPartitionHandler` использует настраиваемый ограниченный executor; remote partitioning остаётся отдельным вариантом. Q40 заменяет ошибочный пример `MessageChannelPartitionHandler` (remote partitioning) на `RemoteChunkingManagerStepBuilder`/`RemoteChunkingWorkerBuilder` по [Spring Batch Integration](https://docs.spring.io/spring-batch/reference/spring-batch-integration/externalizing-execution.html) и их [manager](https://docs.spring.io/spring-batch/reference/api/org/springframework/batch/integration/chunk/RemoteChunkingManagerStepBuilder.html)/[worker](https://docs.spring.io/spring-batch/reference/api/org/springframework/batch/integration/chunk/RemoteChunkingWorkerBuilder.html) API. Каналы показаны как граница транспорта; [Kafka-адаптеры](https://docs.spring.io/spring-integration/reference/kafka.html), доставка, сериализация и идемпотентность требуют отдельной конфигурации и интеграционного теста. Q39–Q40 B0 production MCQ и freshness-sidecar синхронизированы; варианты и sections перечитаны автором. Markdown PASS, структура 43 Q/44 блока, parity PASS, общий pre-check exit 0, linkage errors 0, прежний warning Q43 один, skel hard 0. У Q39/Q40 остаются диагностические skel-сигналы формы, а независимая содержательная и blind-приёмка не выполнялась. Инвентарь не менялся.

**Контрольная точка 03.11.2026 — Spring Batch Q31/Q43 · авторская `О`.** По [Batch 5.2](https://docs.spring.io/spring-batch/reference/5.2/monitoring-and-metrics.html) и [Batch 6](https://docs.spring.io/spring-batch/reference/spring-batch-observability/micrometer.html) разделены режимы наблюдаемости: в 5.2 встроенные метрики регистрируются автоматически, в 6 нужен `ObservationRegistry` с meter-handler и при ручной конфигурации без `@EnableBatchProcessing`/`DefaultBatchConfiguration` — `BatchObservabilityBeanPostProcessor`. Q31/Q43 больше не называют таймеры чтения/обработки/записи счётчиками элементов; `spring.batch.item.write` удалён как неподтверждённый. По [Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html) уточнены зависимость `micrometer-registry-prometheus`, HTTP-экспозиция и отсутствие списка Job в `/actuator/health` по умолчанию. По [Micrometer Gauge](https://docs.micrometer.io/micrometer/reference/concepts/gauges.html) ложный пример `gauge` с неизменяемым числом заменён на `Counter` в зарегистрированном listener. Q31/Q43 B0 production MCQ и freshness-sidecar синхронизированы, все A/B/C/D и sections прочитаны автором. Markdown PASS, структура 43 Q/44 блока, parity и общий pre-check exit 0; linkage errors/warnings 0, skel hard 0, 15 диагностических warn-блоков по всему файлу. Независимая содержательная и blind-приёмка не проводилась, `FINAL` нет. Инвентарь не менялся.

После стабилизации Batch созданы свежие blind-input и manifest: [Q6/Q28/Q38 r3](docs/mcq-quality/runs/20261103-spring-batch-q6-q28-q38-r3/blind-input.jsonl), [Q14–Q16 r2](docs/mcq-quality/runs/20261103-spring-batch-q14-q16-r2/blind-input.jsonl), [Q22–Q25 r1](docs/mcq-quality/runs/20261103-spring-batch-q22-q25-r1/blind-input.jsonl), [Q39–Q40 r1](docs/mcq-quality/runs/20261103-spring-batch-q39-q40-r1/blind-input.jsonl), [Q31/Q43 r1](docs/mcq-quality/runs/20261103-spring-batch-q31-q43-r1/blind-input.jsonl). Старые Batch blind-input не передавать на review. `phase-a`, `reveal`, фаза B и `record` ещё не выполнялись; нужен действительно отдельный контекст ревьюера, которому виден только blind-input. Ограничение этого запуска — запрет дополнительных агентов.

**Контрольная точка 03.11.2026 — PostgreSQL replication Q44–Q46 · авторская `О`.** [Официальный анонс](https://www.postgresql.org/about/news/postgresql-19-rc-1-released-3409/) подтверждает, что PostgreSQL 19 пока RC1, RC2 запланирован на 12.11, GA — на 19.11; `REPACK`/`WAIT` оставлены кандидатами без объявления выпущенными. В PostgreSQL 18 по [WAL configuration](https://www.postgresql.org/docs/18/runtime-config-wal.html), [standby operation](https://www.postgresql.org/docs/18/warm-standby.html) и [recovery config](https://www.postgresql.org/docs/18/recovery-config.html) Q44 исправляет ошибочный `recovery.conf` для 12+, зависимость remote-режимов от `synchronous_standby_names`, границу видимости `remote_apply` и lag async standby. Q45 по [Logical Replication](https://www.postgresql.org/docs/18/logical-replication.html), [Subscription](https://www.postgresql.org/docs/18/logical-replication-subscription.html) и [Restrictions](https://www.postgresql.org/docs/18/logical-replication-restrictions.html) уточняет совместимость версий, необходимость заранее создать таблицы, отсутствие репликации DDL/sequence и автоматическое создание слота обычной подпиской. Q46 по [Replication configuration](https://www.postgresql.org/docs/18/runtime-config-replication.html), [pg_replication_slots](https://www.postgresql.org/docs/18/view-pg-replication-slots.html) и [functions](https://www.postgresql.org/docs/18/functions-admin.html) показывает компромисс `max_slot_wal_keep_size`, `wal_status`, `safe_wal_size`, немедленное резервирование и `primary_slot_name`; LSN-расстояние не называется задержкой в секундах. Markdown, production MCQ Q44–Q46 B0 и freshness-sidecar синхронизированы; все варианты и sections точечно прочитаны автором. Markdown и structure/parity PASS, у Q44–Q46 linkage и skel hard/warn 0. Общий pre-check exit 1 из-за прежних `LOW-COVERAGE` Q6/Q24/Q26/Q34; общий skel hard 0, warn-блоков 2. Независимой содержательной и blind-приёмки нет; 55 Q/55 блоков, состав вопросов/файлов не менялся, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL cost/VACUUM/bloat Q24/Q31/Q32/Q34 · авторская `О`.** Q24 по [EXPLAIN](https://www.postgresql.org/docs/18/using-explain.html) и [planner cost constants](https://www.postgresql.org/docs/18/runtime-config-query.html) различает startup/total cost и actual time, ограничивает сравнение одним запросом и убирает рецепт `random_page_cost=1.1` для любого SSD. Q31/Q32 по [Routine Vacuuming](https://www.postgresql.org/docs/18/routine-vacuuming.html) и [VACUUM](https://www.postgresql.org/docs/18/sql-vacuum.html) больше не утверждают, что обычный VACUUM никогда не возвращает место ОС и никогда не берёт исключительный lock: свободный хвост может быть обрезан с кратким `ACCESS EXCLUSIVE`; `VACUUM FULL` требует длительной блокировки и дополнительного диска. Внешний [pg_repack](https://github.com/reorg/pg_repack/blob/master/doc/pg_repack.rst) также требует место и краткие блокировки. Q34 по [pgstattuple](https://www.postgresql.org/docs/18/pgstattuple.html) отделяет размер файла от измерения bloat, показывает точный полный скан и приближённую оценку живых строк/свободного места через `pgstattuple_approx`; `fillfactor` не объявлен мусором. Markdown и production MCQ Q24/Q31/Q32/Q34 B0, freshness-sidecar синхронизированы; варианты и sections точечно перечитаны автором. Markdown/structure/parity PASS; у этой группы linkage warning 0 и skel hard 0. Общий pre-check exit 1 лишь из-за прежних `LOW-COVERAGE` Q6/Q26, общий skel hard 0 и 3 диагностических warn-блока. Независимая содержательная и blind-приёмка не выполнена; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q26–Q28 · авторская `О`.** По [Transaction Isolation](https://www.postgresql.org/docs/18/transaction-iso.html), [MVCC](https://www.postgresql.org/docs/18/mvcc-intro.html) и [System Columns](https://www.postgresql.org/docs/18/ddl-system-columns.html) исправлены три соседних ответа. Q26 различает четыре названия и три режима, показывает возможную аномалию сериализации на `REPEATABLE READ`, снимок на команду в `READ COMMITTED` и SSI с неблокирующими предикатными блокировками; `40001` требует повторить всю транзакцию и может возникнуть до `COMMIT`. Q27 ограничивает тезис о неблокирующем чтении обычным `SELECT`, различает время снимка по уровням и убирает ложное физическое «рядом» для новой версии. Q28 объясняет ненулевой `xmax` у видимой версии, нестабильность `ctid` и цикличность XID. Production MCQ Q26–Q28 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown и structure/parity PASS, linkage Q26–Q28 без warnings, skel hard 0. Общий pre-check exit 1 только из-за старого `LOW-COVERAGE` Q6; есть диагностические skel warn-блоки и текстовая форма Q26, не заменяющие независимое ревью. Независимая содержательная и blind-приёмка не выполнена; 55 Q/55 блоков, инвентарь не менялся.

**Контрольная точка 03.11.2026 — PostgreSQL Q5–Q7 · авторская `О`.** По [pg_hba.conf](https://www.postgresql.org/docs/18/auth-pg-hba-conf.html) Q5 уточняет отсутствие перехода к следующему правилу после ошибки аутентификации, reload на работающем Unix-подобном сервере и `pg_hba_file_rules`. По [Data Types](https://www.postgresql.org/docs/18/datatype.html), [hstore](https://www.postgresql.org/docs/18/hstore.html), [Network Address Types](https://www.postgresql.org/docs/18/datatype-net-types.html) и [Monetary Types](https://www.postgresql.org/docs/18/datatype-money.html) Q6 различает встроенные типы и подключаемый `hstore`, `inet`/`cidr`, интервалы и зависимость `money` от `lc_monetary`. По [JSON Types](https://www.postgresql.org/docs/18/datatype-json.html) Q7 убирает ложное «`json` не индексируется» и необоснованную долю 99%; GIN по документу `jsonb` отделён от индекса по выражению для извлечённого значения `json`. B0 production MCQ Q5–Q7 и freshness-sidecar синхронизированы; варианты A/B/C/D и sections проверены автором по этим источникам. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, для Q5–Q7 нет локального сигнала формы. Общий pre-check exit 1 из-за прежнего сигнала дословного совпадения Q32, не из-за Q5–Q7; есть диагностические warn-блоки в других Q. Независимая содержательная/blind-приёмка не выполнена; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q8–Q10 · авторская `О`.** По [Arrays](https://www.postgresql.org/docs/18/arrays.html), [Array Functions and Operators](https://www.postgresql.org/docs/18/functions-array.html) и [GIN Indexes](https://www.postgresql.org/docs/18/gin.html) Q8 больше не обещает GIN для `= ANY(tags)` или обязательный `Seq Scan` без индекса: стандартный `array_ops` покрывает `@>`, `<@`, `&&`, равенство массивов; план выбирает оптимизатор. Уточнены семантика `@>` при дублях и граница массива против отдельной таблицы. По [hstore](https://www.postgresql.org/docs/18/hstore.html) Q9 и соседняя строка Q6 допускают SQL `NULL` как значение, объясняют `exist`/`defined`, дубли ключей и убирают неподтверждённое «почти вытеснен». По [Enumerated Types](https://www.postgresql.org/docs/18/datatype-enum.html), [Composite Types](https://www.postgresql.org/docs/18/rowtypes.html) и [CREATE DOMAIN](https://www.postgresql.org/docs/18/sql-createdomain.html) Q10 отличает ограничение меток `enum`, структуру `composite` без собственных `CHECK` и проверку `domain`; `CHECK (VALUE > 0)` не запрещает `NULL`, поэтому в примере колонке задан `NOT NULL`. B0 production MCQ Q8–Q10 и зависимый section Q6, freshness-sidecar синхронизированы; A/B/C/D и sections точечно проверены автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0; у Q8–Q10 нет локальных tell/скелетных сигналов. Общий pre-check exit 1 лишь из-за прежнего сигнала дословного совпадения Q32; есть диагностические warn-блоки в других Q. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q11–Q14 · авторская `О`.** По [Index Types](https://www.postgresql.org/docs/18/indexes-types.html) Q11 связывает метод с оператором и классом операторов, не обещает индексный план и заменяет произвольные примеры `SP-GiST` на подтверждённые. По [Multicolumn Indexes](https://www.postgresql.org/docs/18/indexes-multicolumn.html), [Indexes and ORDER BY](https://www.postgresql.org/docs/18/indexes-ordering.html) и [Operator Classes](https://www.postgresql.org/docs/18/indexes-opclass.html) Q12 исправляет абсолютное «левый префикс или индекс бесполезен»: в PostgreSQL 18 возможен cost-based skip scan по последующему столбцу при небольшом числе значений ведущего; уточнены сортировка и префиксный LIKE вне локали C. По [GIN Indexes](https://www.postgresql.org/docs/18/gin.html) Q13 различает `jsonb_ops`/`jsonb_path_ops`, перепроверку и задержки очистки pending list. По [GiST Indexes](https://www.postgresql.org/docs/18/gist.html) и [Text Search Indexes](https://www.postgresql.org/docs/18/textsearch-indexes.html) Q14 убирает безусловные сравнения размера/скорости и ложное «GIN не перепроверяет, GiST всегда перепроверяет»: оба зависят от класса и запроса. B0 production MCQ Q11–Q14 и freshness-sidecar синхронизированы; A/B/C/D и sections точечно сверены автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных tell Q11–Q14 нет. Общий pre-check exit 1 только из-за прежнего MCQ-сигнала дословного совпадения Q32; есть диагностические warn-блоки вне группы. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q15–Q19 · авторская `О`.** По [BRIN Indexes](https://www.postgresql.org/docs/18/brin.html) Q15 различает классы сводок, lossy-перепроверку, `pages_per_range` и задержанную суммаризацию новых диапазонов. По [Partial Indexes](https://www.postgresql.org/docs/18/indexes-partial.html) Q16 уточняет доказательство предиката при планировании, границу параметризованного условия и условную уникальность. По [Indexes on Expressions](https://www.postgresql.org/docs/18/indexes-expressional.html) и [CREATE INDEX](https://www.postgresql.org/docs/18/sql-createindex.html) Q17 убирает обещание обязательного индексного плана и непроверенный пример с годом от `created_at`, добавляет требование `IMMUTABLE` и цену обновлений. По [Index-Only Scans](https://www.postgresql.org/docs/18/indexes-index-only-scans.html) Q18 исправляет ложное «покрытие исключает heap fetch»: нужен бит all-visible; `INCLUDE` не участвует в поиске, сортировке или уникальности, ограничения по размеру и методам уточнены. Q19 по [CREATE INDEX](https://www.postgresql.org/docs/18/sql-createindex.html) уточняет ожидание транзакций, два скана, `INVALID` индекс после сбоя (игнорируется для чтения, но может нагружать запись и проверять уникальность), запрет транзакционного блока и прямого concurrent-построения на партиционированной таблице. B0 production MCQ Q15–Q19 и freshness-sidecar синхронизированы; A/B/C/D и sections точечно проверены автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0; локальных tell/скелетных сигналов Q15–Q19 нет. Общий pre-check exit 1 из-за прежнего дословного совпадения Q32; другие диагностические warn-блоки вне группы. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q20–Q23 · авторская `О`.** По [Using EXPLAIN](https://www.postgresql.org/docs/18/using-explain.html) и [EXPLAIN](https://www.postgresql.org/docs/18/sql-explain.html) Q20 различает дерево узлов, свойства `Index Cond`/`Filter`, оценочные `cost`/`rows` и средние `actual rows` за `loops`; расхождение не сводится автоматически к устаревшей статистике. Q21 уточняет выполнение `ANALYZE`, DML/ROLLBACK и включённые по умолчанию `BUFFERS` в PostgreSQL 18. Q22 убирает гарантии о `Index Only Scan`, фиксированном пороге выбора `Seq Scan` и абсолютном запрете индекса через `enable_indexscan=off`. Q23 по [Combining Multiple Indexes](https://www.postgresql.org/docs/18/indexes-bitmap-scans.html) и [Index-Only Scans](https://www.postgresql.org/docs/18/indexes-index-only-scans.html) уточняет физический порядок bitmap-чтения, потерю сортировки по индексу, lossy recheck и `Heap Fetches`. Production MCQ Q20–Q23 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown, structure/parity PASS; linkage errors/warnings 0; skel hard 0, локальных TELL Q20–Q23 нет. Общий pre-check exit 1 из-за старого дословного совпадения Q32; другие диагностические warn-блоки вне пакета. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q29–Q30 · авторская `О`.** По [Transaction Isolation](https://www.postgresql.org/docs/18/transaction-iso.html) Q29 различает dirty read от повторных чтений и описывает phantom как изменение набора строк по условию, не только появление строк после `INSERT`; уточнена связь с `READ UNCOMMITTED`/`READ COMMITTED`/`REPEATABLE READ`/`SERIALIZABLE` без смешения аномалии сериализации с фантомами. По [SAVEPOINT](https://www.postgresql.org/docs/18/sql-savepoint.html), [ROLLBACK TO SAVEPOINT](https://www.postgresql.org/docs/18/sql-rollback-to.html) и [RELEASE SAVEPOINT](https://www.postgresql.org/docs/18/sql-release-savepoint.html) Q30 объясняет, что частичный откат сохраняет саму точку и открытую транзакцию, удаляет более поздние точки, а `RELEASE` не делает `COMMIT` и недоступен в состоянии ошибки до `ROLLBACK TO`. Production MCQ Q29–Q30 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q29–Q30 нет. Общий pre-check exit 1 из-за прежнего дословного совпадения Q32; диагностические warn-блоки вне пакета. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q25 и Q33 · авторская `О`.** По [Statistics Used by the Planner](https://www.postgresql.org/docs/18/planner-stats.html), [pg_statistic](https://www.postgresql.org/docs/18/catalog-pg-statistic.html), [pg_stats](https://www.postgresql.org/docs/18/view-pg-stats.html) и [ANALYZE](https://www.postgresql.org/docs/18/sql-analyze.html) Q25 исправляет несуществующее `pg_statistics` в заголовке/TOC/MCQ stem, различает каталог и представление, приблизительность выборочной статистики, `pg_class.reltuples`/`relpages`, знак `n_distinct` и необходимость расширенной статистики при корреляции столбцов. По [Routine Vacuuming](https://www.postgresql.org/docs/18/routine-vacuuming.html) и [Vacuuming Configuration](https://www.postgresql.org/docs/18/runtime-config-vacuum.html) Q33 разделяет пороги `VACUUM`/`ANALYZE`, учитывает верхний предел 100 млн для обычного VACUUM в PostgreSQL 18, запуск по вставкам и против XID wraparound, ручной `ANALYZE` родительской партиционированной и временной таблицы. B0 production MCQ Q25/Q33 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q25/Q33 нет. Общий pre-check exit 1 только из-за прежнего дословного совпадения Q32; есть диагностические warn-блоки вне пакета. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q35–Q37 · авторская `О`.** По [Table Partitioning](https://www.postgresql.org/docs/18/ddl-partitioning.html) и [CREATE TABLE](https://www.postgresql.org/docs/18/sql-createtable.html) Q35 уточняет `RANGE`/`LIST`/`HASH`, пустого родителя, маршрутизацию `INSERT`, включённую нижнюю/исключённую верхнюю границу `RANGE`, отсутствие гарантии ровной нагрузки у `HASH` и блокировки при `DROP`/`DETACH`. Q36 убирает обещание исключительно планового pruning и правило «нет партиции в EXPLAIN — отсечена»: различены планирование, инициализация (`Subplans Removed`, возможна блокировка) и выполнение (`loops`, `never executed`); отсечение идёт по границам, а не индексу. Q37 снимает универсальные тезисы об обязательном проигрыше при тысячах партиций и отсутствии фильтра-константы, уточняет накладные расходы, виртуальные индексы родителя и требование включать весь ключ партиционирования в `UNIQUE`/`PRIMARY KEY`. Production MCQ Q35–Q37 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q35–Q37 нет. Общий pre-check exit 1 только из-за прежнего дословного совпадения Q32; диагностические warn-блоки вне пакета. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q38–Q41 · авторская `О`.** По [Window Functions](https://www.postgresql.org/docs/18/tutorial-window.html), [функциям окна](https://www.postgresql.org/docs/18/functions-window.html), [Window Function Calls](https://www.postgresql.org/docs/18/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS) и [Window Function Processing](https://www.postgresql.org/docs/18/queries-table-expressions.html) Q38 различает партицию, рамку и порядок результата, а также вычисление после `WHERE`/группировки. Q39 добавляет устойчивый tie-breaker для `ROW_NUMBER` и поясняет peers/пропуски `RANK`. Q40 уточняет смещение по партиции, `default` только при отсутствии соседней строки, сохранение реального `NULL` и отсутствие `IGNORE NULLS` в PostgreSQL 18. Q41 исправляет рамку по умолчанию `RANGE UNBOUNDED PRECEDING` до последнего peer, делает накопительный `SUM` по явной `ROWS` и уникальному порядку, а «7 дней» исправляет на семь строк. Production MCQ Q38–Q41 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q38–Q41 нет. Общий pre-check exit 1 только из-за прежнего дословного совпадения Q32; диагностические warn-блоки вне пакета. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q42–Q43 · авторская `О`.** По [WITH Queries](https://www.postgresql.org/docs/18/queries-with.html) Q42 исправляет ложное общее правило встраивания CTE с PostgreSQL 12: пригодный нерекурсивный `SELECT` с одной ссылкой по умолчанию встраивается, с несколькими — вычисляется отдельно; `MATERIALIZED`/`NOT MATERIALIZED` меняют выбор с разными издержками. Q43 описывает итеративную рабочую таблицу, различие `UNION ALL`/`UNION`, случай цикла при меняющемся `depth`, `CYCLE`, явный `ORDER BY` по `SEARCH` и предел применимости внешнего `LIMIT`. Production MCQ Q42–Q43 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q42–Q43 нет. Общий pre-check exit 1 из-за прежнего дословного совпадения Q32; диагностические warn-блоки вне пакета и не являются приёмкой. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q47–Q48 · авторская `О`.** По [Full Text Search](https://www.postgresql.org/docs/18/textsearch-intro.html), [Tables and Indexes](https://www.postgresql.org/docs/18/textsearch-tables.html), [Controlling Text Search](https://www.postgresql.org/docs/18/textsearch-controls.html), [JSON Functions and Operators](https://www.postgresql.org/docs/18/functions-json.html) и [JSONB Indexing](https://www.postgresql.org/docs/18/datatype-json.html#JSON-INDEXING) Q47 уточняет роли `tsvector`/`tsquery`, одинаковую конфигурацию, разницу `to_tsquery` и `websearch_to_tsquery`, `coalesce` для nullable полей, возможности GIN и отдельное ранжирование. Q48 исправляет тип результата `->`/`#>` для `jsonb`, верхний уровень для `?`, поддержку операторов классами `jsonb_ops`/`jsonb_path_ops` и необходимость индекса по выражению для извлечённого вложенного объекта. Ближайшие Q17/Q18/Q13 проверены: новое отдельное Q не нужно. Production MCQ Q47–Q48 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q47–Q48 нет. Общий pre-check exit 1: отчёт о дословных совпадениях перечисляет прежние Q21/Q23/Q30/Q32/Q38; Q32 требует первого ручного разбора. Диагностические предупреждения не являются независимой приёмкой. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q49–Q50 · авторская `О`.** По [PgBouncer Usage](https://www.pgbouncer.org/usage.html), [Features](https://www.pgbouncer.org/features.html), [Configuration](https://www.pgbouncer.org/config.html), [FAQ](https://www.pgbouncer.org/faq.html) и [1.26 changelog](https://www.pgbouncer.org/changelog.html) Q49 убирает неподтверждённые универсальные цифры памяти и времени, различает клиентский `max_client_conn` и серверный `default_pool_size` на пару пользователь/база, объясняет ожидание свободного соединения и совместную настройку с пулом приложения. Q50 уточняет три момента возврата сервера, режим `session` по умолчанию, ограничения session state, различие SQL `PREPARE` и протокольных prepared statements (поддержка с PgBouncer 1.21 при ненулевом `max_prepared_statements`), а также отслеживание `search_path` в PgBouncer 1.26.1 с PostgreSQL 18. Production MCQ Q49–Q50 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q49–Q50 нет. Общий pre-check exit 1 на прежних literal-overlap сигналах, в том числе Q32; диагностические предупреждения вне пакета не равны приёмке. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q51–Q52 · авторская `О`.** По [Explicit Locking](https://www.postgresql.org/docs/18/explicit-locking.html), [Lock Management](https://www.postgresql.org/docs/18/runtime-config-locks.html), [pg_locks](https://www.postgresql.org/docs/18/view-pg-locks.html), [pg_blocking_pids](https://www.postgresql.org/docs/18/functions-info.html), [SELECT](https://www.postgresql.org/docs/18/sql-select.html) и [SQLSTATE](https://www.postgresql.org/docs/18/errcodes-appendix.html) Q51 исправляет матрицу конфликтов, различает табличные `ROW SHARE`/`ROW EXCLUSIVE` и блокировки строк, добавляет page/advisory/predicate locks, ограничения `pg_locks` и диагностику через `pg_blocking_pids()`. Q52 уточняет `deadlock_timeout`, непредсказуемую жертву, `40P01` и повтор всей транзакции; `NOWAIT`/`SKIP LOCKED` ограничены строковым захватом и не гарантируют отсутствие ожидания табличной блокировки. Соседний Q30 о `SAVEPOINT` сверён для семантики снятия блокировок. Production MCQ Q51–Q52 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q51–Q52 нет. Общий pre-check exit 1 на прежних literal-overlap сигналах, включая Q32; диагностические предупреждения вне пакета не равны приёмке. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q53–Q54 · авторская `О`.** По [Resource Consumption](https://www.postgresql.org/docs/18/runtime-config-resource.html), [Query Planning](https://www.postgresql.org/docs/18/runtime-config-query.html), [Connections](https://www.postgresql.org/docs/18/runtime-config-connection.html), [WAL](https://www.postgresql.org/docs/18/runtime-config-wal.html), [Logging](https://www.postgresql.org/docs/18/runtime-config-logging.html), [pg_stat_statements](https://www.postgresql.org/docs/18/pgstatstatements.html) и [Using EXPLAIN](https://www.postgresql.org/docs/18/using-explain.html) Q53 заменяет универсальные числа на связь параметра с симптомом и измерением, уточняет `effective_cache_size` как оценку без выделения памяти, пределы `work_mem`/`hash_mem_multiplier`, нагрузку автоочистки и версионные значения PostgreSQL 18. Q54 требует предварительной загрузки `pg_stat_statements` и query ID, разделяет завершённые запросы в логе, агрегаты и текущие ожидания, убирает сброс статистики из диагностического сценария и напоминает, что `EXPLAIN ANALYZE` выполняет DML. Соседние Q20–Q25/Q33 сверены: нового файла или Q не требуется. Production MCQ Q53–Q54 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q53–Q54 нет. Общий pre-check exit 1 на прежних literal-overlap сигналах, включая Q32; диагностические предупреждения не равны приёмке. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q55 · авторская `О`.** По [CREATE EXTENSION](https://www.postgresql.org/docs/18/sql-createextension.html), [pg_available_extensions](https://www.postgresql.org/docs/18/view-pg-available-extensions.html), [pg_extension](https://www.postgresql.org/docs/18/catalog-pg-extension.html), [UUID functions](https://www.postgresql.org/docs/18/functions-uuid.html), [uuid-ossp](https://www.postgresql.org/docs/18/uuid-ossp.html), [pgcrypto](https://www.postgresql.org/docs/18/pgcrypto.html) и [pg_trgm](https://www.postgresql.org/docs/18/pgtrgm.html) Q55 разделяет расширения, встроенные `gen_random_uuid()`/`uuidv7()` и внешний PgBouncer. Уточнены установка файлов на сервере, `CREATE EXTENSION` в текущей базе, каталог установленных и представление доступных расширений; примеры сторонних проектов оставлены с прямыми ссылками на их источники. Соседние Q13/Q47/Q49/Q54 сверены. Production MCQ Q55 B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q55 нет. Общий pre-check exit 1 на прежних literal-overlap сигналах, включая Q32; диагностические предупреждения не равны приёмке. Независимой содержательной/blind-приёмки нет; 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q31–Q34 · авторская `О`.** По [Routine Vacuuming](https://www.postgresql.org/docs/18/routine-vacuuming.html), [VACUUM](https://www.postgresql.org/docs/18/sql-vacuum.html) и [pgstattuple](https://www.postgresql.org/docs/18/pgstattuple.html) Q31 дополнен картой видимости, `Index Only Scan`, заморозкой XID и отличием `ANALYZE`. Q32–Q34 точечно сверены без правки теории. Production MCQ Q31 B0 переформулирован после сигнала TELL; Q32 B0 переведён на выбор операции после массового удаления вместо дословного пересказа таблицы. A/B/C/D и sections Q31–Q34 перечитаны автором; freshness-sidecar обновлён. Схема/структура/parity PASS, linkage errors/warnings 0, skel hard 0; локальных TELL Q31–Q34 и literal-overlap Q32 нет. Общий pre-check остаётся exit 1 из-за прежних literal-overlap Q21/Q23/Q30/Q38; отдельно остаётся TELL Q44. Независимой содержательной/blind-приёмки нет, 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q20–Q24 · авторская `О`.** По [EXPLAIN](https://www.postgresql.org/docs/18/sql-explain.html), [Using EXPLAIN](https://www.postgresql.org/docs/18/using-explain.html), [Combining Multiple Indexes](https://www.postgresql.org/docs/18/indexes-bitmap-scans.html) и [Index-Only Scans](https://www.postgresql.org/docs/18/indexes-index-only-scans.html) Q21 уточняет отличие опции `EXPLAIN (ANALYZE)` от самостоятельной команды `ANALYZE`; Q23 различает построение bitmap по индексу и чтение строк таблицы. Q20/Q22/Q24 точечно сверены без правки теории. Production MCQ Q21/Q23 B0 переформулированы после сигнала дословного пересечения; в Q23 C исправлено противоречивое контрфактическое объяснение. A/B/C/D и sections Q20–Q24 перечитаны автором; freshness-sidecar обновлён. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0; локальных TELL и literal-overlap Q20–Q24 нет. Общий pre-check exit 1 из-за прежних literal-overlap Q30/Q38; отдельно TELL Q44. Независимой содержательной/blind-приёмки нет, 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q29–Q30 · авторская `О`.** По [Transaction Isolation](https://www.postgresql.org/docs/18/transaction-iso.html), [SAVEPOINT](https://www.postgresql.org/docs/18/sql-savepoint.html), [ROLLBACK TO SAVEPOINT](https://www.postgresql.org/docs/18/sql-rollback-to.html) и [RELEASE SAVEPOINT](https://www.postgresql.org/docs/18/sql-release-savepoint.html) Q29 и его B0 проверены без правок: определения аномалий и особенности PostgreSQL согласованы с документацией. В Q30 прежний широкий заголовок позволял защитить дистрактор про `RELEASE`; заголовок и B0 question_text уточнены до эффекта `ROLLBACK TO SAVEPOINT`, номер Q30 сохранён, TOC обновлён. Теория дополнена удалением более поздних активных точек при `RELEASE`. MCQ Q30 B0 переформулирован после literal-overlap и перечитан целиком с sections; freshness-sidecar обновлён. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL и literal-overlap Q29–Q30 нет. Общий pre-check exit 1 из-за прежнего literal-overlap Q38; отдельно TELL Q44. Независимой содержательной/blind-приёмки нет, 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q38–Q41 · авторская `О`.** По [Window Functions](https://www.postgresql.org/docs/18/tutorial-window.html), [функциям окна](https://www.postgresql.org/docs/18/functions-window.html), [Window Function Calls](https://www.postgresql.org/docs/18/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS) и [Window Function Processing](https://www.postgresql.org/docs/18/queries-table-expressions.html) Q38 уточняет пример `running_total`: при одинаковой дате рамка по умолчанию может дать один итог peers; для расчёта по строкам нужны явная `ROWS` и устойчивый порядок. Q39–Q41 точечно сверены без правки теории. Production MCQ Q38 B0 переформулирован после literal-overlap, Q40 B0 исправляет контрфактическое объяснение дистрактора; A/B/C/D и sections Q38–Q41 перечитаны автором, freshness-sidecar обновлён. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL и literal-overlap Q38–Q41 нет. Дословных correct во всём файле теперь 0; общий pre-check exit 1 из-за прежнего TELL Q44. Независимой содержательной/blind-приёмки нет, 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q44–Q46 · авторская `О`.** По [Log-Shipping Standby Servers](https://www.postgresql.org/docs/18/warm-standby.html), [Replication Configuration](https://www.postgresql.org/docs/18/runtime-config-replication.html), [Publication](https://www.postgresql.org/docs/18/logical-replication-publication.html), [Restrictions](https://www.postgresql.org/docs/18/logical-replication-restrictions.html) и [pg_replication_slots](https://www.postgresql.org/docs/18/view-pg-replication-slots.html) Q44 прямо уточняет асинхронный режим по умолчанию. Q45 уточняет физическую копию всего кластера и возможность передавать `TRUNCATE` в логической публикации; Q46 точечно проверен без правки теории. Production MCQ Q44 B0 переформулирован после TELL, Q45 B0 согласован с теорией, Q46 B0 выровнен по форме; A/B/C/D и sections Q44–Q46 перечитаны автором, freshness-sidecar обновлён и исправлена неверная помета версии PgBouncer. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL и literal-overlap нет; общий pre-check exit 0. Независимой содержательной/blind-приёмки нет, 55 Q/55 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — PostgreSQL Q1–Q4 · авторская `О`.** По [How Connections Are Established](https://www.postgresql.org/docs/18/connect-estab.html), [PostgreSQL 15 Release Notes](https://www.postgresql.org/docs/15/release-15.html), [Resource Consumption](https://www.postgresql.org/docs/18/runtime-config-resource.html), [Cumulative Statistics](https://www.postgresql.org/docs/18/monitoring-stats.html), [WAL](https://www.postgresql.org/docs/18/wal-intro.html), [WAL Configuration](https://www.postgresql.org/docs/18/wal-configuration.html) и [WAL Settings](https://www.postgresql.org/docs/18/runtime-config-wal.html) исправлены: в Q1 удалён `stats collector` (нет с PostgreSQL 15), общий «кэш планов» и универсальная цена `fork()`; в Q2 сняты жёсткий потолок 8–16 GB и порог 99%, различены промах `shared_buffers` и физическое чтение, запрос доли попаданий приведён к `numeric` и защищён от нулевого знаменателя; в Q3 правило WAL уточнено как порядок устойчивой записи, исправлены `wal_compression`, PITR и граница `synchronous_commit=off`; в Q4 уточнены redo-позиция, мягкий `max_wal_size` и распределение I/O. Production MCQ Q1–Q4 B0 и sections синхронизированы; A/B/C/D перечитаны автором. Freshness-sidecar обновлён. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, TELL и literal-overlap 0, общий pre-check exit 0; 55 Q/55 блоков, инвентарь прежний. Это точечная авторская проверка `О` всего диапазона Q1–Q55 по пакетам, не полная содержательная `П` и не независимая/blind-приёмка MCQ.

**Контрольная точка 03.11.2026 — Metrics & Tracing Q9/Q28/Q37 · авторская `О`.** Из трёх пар точных дублей заголовков `inventory.tsv` разобрана пара Q9/Q28. Q9 оставлен вводным вопросом, Q28 уточнён до выбора head/tail для production; номера сохранены, оглавление и парный MCQ синхронизированы. Q37 уточняет работу Collector: `decision_wait` не гарантирует полного трейса, все span'ы одного trace должны попасть к одному sampler, выборка зависит от памяти и доставки. Универсальные 1–10%, обязательные 100% в dev и гарантия сохранения всех ошибок удалены из этих трёх ответов и MCQ. Точечно сверено по [OpenTelemetry Sampling](https://opentelemetry.io/docs/concepts/sampling/) и [Collector Tail Sampling Processor](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/processor/tailsamplingprocessor/README.md); версия processor зависит от установленного Collector, пример не является универсальной настройкой. Инвентарь обновлён только в поле точных дублей: 41 Q осталось. Markdown и структура/parity MCQ PASS. Общий MCQ pre-check FAIL из-за прежних проблем всего файла, включая `NEIGHBOR-LEAK` Q17, 21 hard и диагностические warn-блоки; у Q9/Q28/Q37 локального hard/linkage-сигнала нет. В других ответах того же файла, в частности Q24/Q27, ещё есть широкие обещания о tail sampling — полная `П` не проводилась. Независимая содержательная/blind-приёмка MCQ не выполнена, статус `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Collections Q19/Q43 · авторская `О`.** Вторая пара точных дублей `inventory.tsv` действительно повторяла один рецепт LRU-кэша. Q19 оставлен реализацией на `LinkedHashMap`: `initialCapacity` отделена от лимита, `maxSize` проверяется, `removeEldestEntry()` описан как хук после добавления новой записи. Q43 без смены номера стал вопросом об ограничениях при конкурентном доступе: `get()` в access-order может изменить порядок, `synchronizedMap` защищает отдельные вызовы, а обход представления требует удерживать монитор обёртки на протяжении всего цикла; fail-fast не заменяет синхронизацию. Ответы и MCQ Q19/Q43 B0 сверены по [LinkedHashMap Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/LinkedHashMap.html), [Collections.synchronizedMap](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Collections.html#synchronizedMap(java.util.Map)) и [Caffeine Eviction](https://github.com/ben-manes/caffeine/wiki/Eviction). Заголовок, TOC, production JSON, freshness-sidecar и поле дубликата инвентаря синхронизированы; 46 Q/46 блоков сохранены. Markdown и structure/parity PASS, tell/linkage errors и warnings 0. Общий MCQ pre-check FAIL из-за прежних skel hard Q37/Q40; Q19/Q43 локальных сигналов не имеют. Все A/B/C/D и sections Q19/Q43 перечитаны автором; полная `П` файла и независимая MCQ-приёмка `NOT_RUN`, `FINAL` не заявлен.

**Контрольная точка 03.11.2026 — Kotlin Q5/Q30 · авторская `О`.** Третья пара из `inventory.tsv` оказалась ложным дублем: нормализация удалила символы `?.`/`!!` и `==`/`===`, оставив одинаковые слова заголовков. Q5 о null safety, Q30 о равенстве; оба номера и заголовка сохранены. В Q5 по [Null safety](https://kotlinlang.org/docs/null-safety.html) уточнены безопасный вызов, обоснованное применение `!!` и по [Kotlin 1.4](https://kotlinlang.org/docs/whatsnew14.html) историческая граница `NullPointerException` на JVM. В Q30 по [Equality](https://kotlinlang.org/docs/equality.html) исправлено чрезмерное обещание сравнения содержимого любого класса и удалён строковый пример, опиравшийся на интернирование; добавлены обычный класс без собственного `equals`, массивы и примитивные представления. Production MCQ Q5/Q30 B0 и sections синхронизированы, варианты A/B/C/D перечитаны автором. Freshness-sidecar и поле дубликата инвентаря обновлены; 45 Q/45 блоков сохранены. Markdown, структура и parity PASS. Общий MCQ pre-check FAIL из-за старых сигналов в файле; локальных hard/linkage-сигналов Q5/Q30 нет. Полная `П` и независимая MCQ-приёмка не выполнены, статус `NOT_RUN`.

**Контрольная точка 03.11.2026 — Metrics & Tracing Q24/Q27 · авторская `О`.** Q24 отделён от Q27: первый вопрос теперь о сокращении потока до хранилища, второй — о retention и span metrics. По [OpenTelemetry Sampling](https://opentelemetry.io/docs/concepts/sampling/), [Java SDK configuration](https://opentelemetry.io/docs/languages/java/configuration/), [Attributes Processor](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/processor/attributesprocessor/README.md) и [Tail Sampling Processor](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/processor/tailsamplingprocessor/README.md) Q24 убирает недействительную настройку `span.limits` в Collector, относит лимиты к SDK, отделяет экономию трафика приложения от удаления атрибутов в Collector и не обещает сохранить все ошибки. Q27 по [Collector architecture](https://opentelemetry.io/docs/collector/architecture/) и [Span Metrics Connector](https://github.com/open-telemetry/opentelemetry-collector-contrib/blob/main/connector/spanmetricsconnector/README.md) строит метрики из поступивших span'ов до tail sampling, чтобы выборка ошибок/задержек не смещала их; ранний head sampling всё ещё может сделать поток неполным. Убраны универсальные 7–14 дней и представление, что старые трейсы задним числом превращают в метрики. Заголовок/TOC Q24, production MCQ Q24/Q27 B0, freshness-sidecar синхронизированы; 41 Q/41 блок сохранены, инвентарь не менялся. Markdown и структура/parity PASS; у Q24/Q27 локальных hard/linkage-сигналов нет, у Q27 остаются диагностические skel-подсказки формы. Общий MCQ pre-check FAIL: 19 прежних hard по другим блокам; это авторская `О`, независимая приёмка `NOT_RUN`, полная `П` не проводилась. Фрагменты Collector не проверены на конкретной версии бинарника.

**Контрольная точка 03.11.2026 — Java Collections Q20/Q46 · авторская `О`.** Подтверждён смысловой дубль: оба вопроса определяли `WeakHashMap` и повторяли пример с `System.gc()`. Q20 оставлен механизмом слабых ключей; по [Java SE 25 WeakHashMap API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/WeakHashMap.html) уточнены сильное удержание значений, особый ключ `null` и отсутствие гарантированного момента очистки. Q46 без смены номера стал вопросом о ловушках: значение может сильно ссылаться на собственный ключ и удерживать запись; вновь созданный равный ключ не гарантирует попадания после сборки прежнего; GC не задаёт TTL или предел размера. Удалены вводящие в заблуждение примеры с ключом `String` из пула и обещанием, что weak-key реестр слушателей сам решает срок подписки. По [Caffeine Eviction](https://github.com/ben-manes/caffeine/wiki/Eviction#reference-based) отмечено, что `weakKeys()` сравнивает по идентичности, поэтому не заменяет `WeakHashMap` без проверки семантики ключей. Заголовок/TOC Q46, production MCQ Q20/Q46 B0 и freshness-sidecar синхронизированы; 46 Q/46 блоков, инвентарь без изменения. Markdown, структура/parity и общий tell/linkage PASS; общий MCQ pre-check FAIL из-за прежних skel hard Q37/Q40. У Q20 осталась диагностическая skel-подсказка формы, у Q46 локальных сигналов нет. A/B/C/D и sections перечитаны автором; полная `П` и независимая MCQ-приёмка `NOT_RUN`, `FINAL` не заявлен.

**Контрольная точка 03.11.2026 — Java Modules Q14–Q16/Q31 · авторская `О`.** Сверка Q14–Q16 с Q31–Q33 выявила реальный повтор сравнительной таблицы Q16/Q31 и ошибочную схему «любой automatic module служит мостом к произвольному classpath JAR». Q14 теперь различает запрет `requires` на безымянный модуль и возможность runtime-чтения через `--add-reads`. Q15 убирает вымышленный `requires transitive` на все модули: automatic module читает граф по особому правилу, а его дескриптор не содержит таких директив. Q16 оставлен таблицей свойств, включая случай `open module`; Q31 стал практическим сценарием переноса **нужного** JAR на `module path` и проверки его automatic-имени. Номера сохранены. Теория, TOC, production MCQ Q14/Q15/Q16/Q31 B0 и freshness-sidecar синхронизированы. Проверены [JLS §7.7.5](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.5), [ModuleDescriptor Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/ModuleDescriptor.html), [JAR specification](https://docs.oracle.com/en/java/javase/25/docs/specs/jar/jar.html), [`java --add-reads`](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html) и [`jar --describe-module`](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jar.html). Markdown, JSON structure, parity и linkage PASS: 38 Q/38 блоков, linkage errors/warnings 0. Общий MCQ pre-check FAIL из-за прежних skel hard Q5/Q7/Q18/Q19/Q26/Q27/Q29/Q34; у изменённых Q hard нет. Q14–Q16 имеют диагностические сигналы формы, независимая SFPL/ANSC/RU и blind-приёмка `NOT_RUN`; полная `П` файла не проводилась. Инвентарь не менялся: состав и число Q прежние. Q32/Q33 пока только просмотрены по заголовкам, без полной проверки ответа.

**Контрольная точка 03.11.2026 — Java Modules Q13/Q17/Q32/Q33 · авторская `О`.** Q17 и Q32 раньше дважды определяли split package и без условия утверждали, что любой дубль всегда ломает запуск. По [Configuration.resolve Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) Q17 теперь объясняет проверяемое условие: модуль читает два модуля, экспортирующих ему один пакет. Q32 стал диагностикой и исправлением: [`java --validate-modules`/`--dry-run`](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html), проверка состава JAR, условное исключение лишней зависимости, ограничение `--patch-module`. Удалены неверное назначение [`jdeps --check`](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jdeps.html) и команда с `java.xml.ws.annotation`, [удалённым из JDK 11](https://docs.oracle.com/en/java/javase/22/migrate/removed-tools-and-components.html). Q13 по [Oracle JDK Migration Guide](https://docs.oracle.com/en/java/javase/25/migrate/migrating-jdk-8-later-jdk-releases.html) уточняет, что `--illegal-access` с Java 17 игнорируется с предупреждением, и убирает `java.xml.bind` из актуального примера. Q33 отделён от Q5: показывает, что прямой доступ к закрытому публичному API требует `--add-exports` при компиляции и запуске, а глубокая рефлексия — `--add-opens` при запуске. Номера Q сохранены; теория, TOC, production MCQ B0 и freshness-sidecar синхронизированы. Автор проверил A/B/C/D и sections. Markdown, JSON structure, parity, tell и linkage PASS; 38 Q/38 блоков, linkage errors/warnings 0. Общий MCQ pre-check FAIL из-за прежних skel hard Q5/Q7/Q18/Q19/Q26/Q27/Q29/Q34; у изменённых Q hard нет, есть диагностические сигналы длины/формы. Независимая SFPL/ANSC/RU и blind-приёмка `NOT_RUN`; полная `П` файла не проводилась. Инвентарь не менялся: число Q и состав файлов прежние.

**Контрольная точка 03.11.2026 — Java Modules Q4/Q5/Q12 · авторская `О`.** По [JLS §7.7.2](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.2), [§7.7.5](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.5), [AccessibleObject Java 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/reflect/AccessibleObject.html), [Configuration.resolve](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) и [Oracle JDK Migration Guide](https://docs.oracle.com/en/java/javase/25/migrate/migrating-jdk-8-later-jdk-releases.html) устранены широкие обещания Q4: `classpath` не открывает закрытые пакеты именованных модулей, `module path` не гарантирует отсутствие поздних ошибок доступа/загрузки, split package зависит от читающего графа, а смешанный запуск возможен без автоматической зависимости named→unnamed. Q5 уточняет, что `opens` даёт runtime-доступ к публичным типам, но не прямой compile-time доступ; пример больше не выдаёт непроверенный Hibernate module name за универсальный. Q12 различает переходные режимы Java 9–16, default `deny` в Java 16 и сильную инкапсуляцию с Java 17; `sun.misc`/`sun.reflect` названы исключением, правка своего дескриптора не открывает пакет чужого модуля. Production MCQ Q4/Q5/Q12 B0, объяснения и freshness-sidecar синхронизированы; автор перечитал A/B/C/D и sections. Номера и TOC сохранены, 38 Q/38 блоков, инвентарь прежний. Markdown, JSON structure, parity, tell и linkage PASS. Общий MCQ pre-check FAIL на прежних skel hard Q7/Q18/Q19/Q26/Q27/Q29/Q34; у изменённых Q hard нет, остались диагностические сигналы формы Q5/Q12. Независимая SFPL/ANSC/RU и blind-приёмка `NOT_RUN`; полная `П` файла не проводилась.

**Контрольная точка 03.11.2026 — Java Modules Q18/Q19/Q34 · авторская `О`.** Q18 уточнён по [JAR File Specification Java 25](https://docs.oracle.com/en/java/javase/25/docs/specs/jar/jar.html): дескриптор MRJAR допустим как в корне, так и в версионном каталоге; пример Java 8-совместимой базовой части использует последний вариант и сохраняет публичный API. Q19 оставлен объяснением `jlink`, Q34 — практической командой по [jlink Java 25](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jlink.html); убраны неподтверждённые размеры, обязательный `jdeps`, `--ignore-missing-deps` и предположение, что classpath-JAR входит в образ. [jdeps Java 25](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jdeps.html) использован для границы анализа. Production MCQ B0 и freshness-sidecar синхронизированы, автор перечитал варианты и sections. Markdown/структура/parity/tell/linkage PASS; общий MCQ pre-check FAIL из-за прежних skel hard Q7/Q26/Q27/Q29, локальных hard Q18/Q19/Q34 нет. 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` файла не проводилась. Q20 остаётся кандидатом: проверить по версии `--compress` и формулировку `--bind-services`.

**Контрольная точка 03.11.2026 — Java Modules Q20/Q26/Q27 · авторская `О`.** Q20 по [jlink Java 25](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jlink.html) разделяет корневые и транзитивные модули, объясняет `--bind-services` и предлагает выбирать сжатие по `--list-plugins` используемого JDK: локальный Corretto 22.0.2 и Oracle Java 25 man page дают разные формы значений. Q26 по [Spring Framework Overview 7.0](https://docs.spring.io/spring-framework/reference/overview.html), [Classpath Scanning](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html) и [Boot Executable Jar](https://docs.spring.io/spring-boot/specification/executable-jar/index.html) убирает неподтверждённое «почти все приложения» и обязательное открытие всех пакетов; объясняет `Automatic-Module-Name`, `exports` для публичных компонентов и `opens` для конкретной глубокой рефлексии. Q27 по [Hibernate ORM 7.1 User Guide](https://docs.hibernate.org/orm/7.1/userguide/html_single/) и [JLS §7.7.2](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.2) различает field/property access и убирает гарантированное падение при старте. Production MCQ Q20/Q26/Q27 B0 и форма Q18 B0 синхронизированы, все варианты и sections перечитаны автором. Markdown, JSON structure, parity, tell/linkage для изменённого пакета PASS; общий MCQ pre-check FAIL на прежних skel hard Q7/Q29, Q33 остаётся tell-сигналом. 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` не проводилась.

**Контрольная точка 03.11.2026 — Java Modules Q7/Q8/Q29 · авторская `О`.** По [JLS §7.7.1 Java SE 25](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.1) Q7 теперь отделяет читаемость от `exports` и ограничивает транзитивность публичным API; Q8 по [ModuleDescriptor.Requires.Modifier](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/ModuleDescriptor.Requires.Modifier.html) уточняет статическую и динамическую фазы, убирает пример `Class.forName` как доказательство работоспособности интеграции и по [javac](https://docs.oracle.com/en/java/javase/25/docs/specs/man/javac.html) отделяет настройку annotation processor. Q29 по [FindException API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/FindException.html) и [Configuration.resolve](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) заменяет неверную пару «забытый requires → FindException» маршрутом диагностики без повтора Q17/Q32/Q33. Production MCQ Q7/Q8/Q29 B0 синхронизированы, автор перечитал A/B/C/D и sections. Markdown, JSON structure/parity, локальные tell/linkage PASS; structural subcheck PASS (hard 0); общий MCQ pre-check exit=1 из-за прежнего TELL Q33 и иных диагностических сигналов. 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` не проводилась.

**Контрольная точка 03.11.2026 — Java Modules Q9/Q30/Q33 · авторская `О`.** Q9 по [JLS §7.7 Java SE 25](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7) уточняет runtime-доступ к публичным типам, глубокую рефлексию и ошибку `opens` внутри `open module`; пример больше не выдаёт конфигурацию Spring Boot за универсальную. Q30 переименован без смены номера в вопрос о связи JPMS с возможностями Java: по [Java 25 launcher](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html), [Migration Guide](https://docs.oracle.com/en/java/javase/25/migrate/migrating-jdk-8-later-jdk-releases.html), [Inside Java о JDK 25](https://inside.java/2025/10/20/jdk-25-performance-improvements/) и [FFM API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/package-summary.html) убраны ложное «`--illegal-access` удалён», обязательная зависимость Leyden от модульного графа и «отдельные модули Panama». Q33 MCQ B0 выровнен по фазам без подсказки формы; теория сохранена. TOC, production MCQ Q9/Q30/Q33 B0, `question_text` Q30 и freshness-sidecar синхронизированы; автор перечитал варианты и sections. Markdown, JSON structure/parity, tell/linkage pre-check PASS, общий MCQ pre-check exit=0. Это не независимая приёмка: SFPL/ANSC/RU/blind `NOT_RUN`, полная `П` файла не проводилась. 38 Q/38 блоков; инвентарь прежний.

**Контрольная точка 03.11.2026 — Java Modules Q21/Q22/Q23 · авторская `О`.** Q21 по [JLS §7.7.1](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.1), [Gradle Java Library Plugin](https://docs.gradle.org/current/userguide/java_library_plugin.html) и [jdeps](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jdeps.html) перестал объявлять Bottom-Up универсально рекомендуемым и полностью модульным при стороннем automatic JAR; два порядка работы описаны как практический выбор, это вывод, а не правило спецификации. Q22 по [Gradle](https://docs.gradle.org/current/userguide/java_library_plugin.html), [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/examples/module-info.html) и [Surefire JPMS](https://maven.apache.org/surefire/maven-surefire-plugin/examples/jpms.html) исправляет безусловное обещание переноса всех зависимостей на module path и смешение build/JPMS-модулей. Q23 по [ModuleLayer Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/ModuleLayer.html) и [Configuration](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) уточняет новый загрузчик для one-loader-слоя и убирает обещание автоматической выгрузки плагина. Production MCQ Q21/Q22/Q23 B0 и freshness-sidecar синхронизированы; автор перечитал варианты и sections. Markdown, JSON structure/parity, tell/linkage pre-check PASS, общий MCQ pre-check exit=0. 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` не проводилась.

**Контрольная точка 03.11.2026 — Java Modules Q24/Q25/Q35 · авторская `О`.** Q24 по [Configuration Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) и [JLS §7.7.1](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.1) убирает вымышленный текст ошибки и неподтверждённое объяснение порядка разрешения; уточняет граф `requires` и контракт сервиса. Q25 по [Gradle Testing Java Modules](https://docs.gradle.org/current/userguide/java_testing.html#sec:java_testing_java_modules) и [Maven Surefire JPMS](https://maven.apache.org/surefire/maven-surefire-plugin/examples/jpms.html) разделяет classpath unit, blackbox и whitebox patching без утверждения об автоматических флагах. Q35 по [jdeps Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jdeps.html) заменяет вымышленные выводы командами и границами статического анализа; остаётся дополнением к Q17/Q21/Q22. Production MCQ B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown, JSON structure/parity, tell/linkage и общий pre-check PASS. 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` не проводилась.

**Контрольная точка 03.11.2026 — Java Modules Q36/Q37/Q38 · авторская `О`.** Q36 по [ServiceLoader Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/ServiceLoader.html), [JLS §7.7.4](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.4) и [JAR specification](https://docs.oracle.com/en/java/javase/25/docs/specs/jar/jar.html) разделяет регистрацию на `module path` и `classpath`, уточняет создание провайдера и включение модуля в конфигурацию. Q37 по [Spring Classpath Scanning](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html), [AccessibleObject Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/reflect/AccessibleObject.html) и [Jackson 3 module-info](https://github.com/FasterXML/jackson-databind/blob/3.x/src/main/java/module-info.java) заменяет универсальное `opens` диагностикой конкретного доступа и отмечает смену JPMS-имени Jackson 3. Q38 по [JLS](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.1), [jdeps](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jdeps.html) и [jlink](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jlink.html) устранён повтор Q21: остался выбор промежуточного результата, убраны обязательный Bottom-Up и ложная проверка split packages через `jdeps --check`. Production MCQ B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown, JSON structure/parity, tell/linkage и общий pre-check PASS. 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` не проводилась.

**Контрольная точка 03.11.2026 — Java Modules Q1/Q2/Q3 · авторская `О`.** По [JLS §7.7 Java SE 25](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7), [Configuration](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) и [jlink](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jlink.html) Q1 больше не обещает автоматического выбора версии JAR и безусловного запрета всех split packages; различает явный модуль и classpath-JAR, связывает граф с проверяемой конфигурацией. Q2 по [JLS](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.1) и [ModuleDescriptor](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/ModuleDescriptor.html) отделяет явный named module от automatic module и уточняет `requires static`; ошибочные реальные имена модулей убраны из учебного примера. Q3 по [java.sql API](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/module-summary.html) и [jdk.jlink API](https://docs.oracle.com/en/java/javase/25/docs/api/jdk.jlink/module-summary.html) убирает приблизительное число модулей и перевёрнутую схему зависимостей, показывает реальные `requires transitive` и способ проверки установленного JDK. Production MCQ B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown, JSON structure/parity, tell/linkage и общий pre-check PASS. 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` не проводилась.

**Контрольная точка 03.11.2026 — Java Modules Q6/Q10/Q11 · авторская `О`.** Q6 по [JLS §7.7.2](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.2) и [ModuleDescriptor.Exports/Opens](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/ModuleDescriptor.Exports.html) уточняет адресные права, требование чтения, отличие named-получателя от unnamed-тестов и убирает универсальное имя Hibernate. Q10 по [ServiceLoader Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/ServiceLoader.html) и [DriverManager](https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/DriverManager.html) отделяет роли `uses`/`provides` от доступности и ленивого создания провайдера; убирает обещание обнаружения любого JDBC JAR на пути. Q11 по [ServiceLoader](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/ServiceLoader.html) и [JAR specification](https://docs.oracle.com/en/java/javase/25/docs/specs/jar/jar.html) сравнивает named/unnamed/automatic и два API поиска без ложной гарантии полной проверки на старте. С Q5/Q36 пересечения сняты ссылками и отдельными сценариями. Production MCQ B0 и freshness-sidecar синхронизированы; A/B/C/D и sections перечитаны автором. Markdown, JSON structure/parity, tell/linkage и общий pre-check PASS. 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` не проводилась.

**Контрольная точка 03.11.2026 — Java Modules Q28 · авторская `О` и адресная согласованность.** По [JLS §7.7](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7), [Configuration.resolve](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) и [ServiceLoader](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/ServiceLoader.html) Q28 больше не объявляет доменное деление единственно допустимым и не приписывает одному правилу split package все случаи; `requires transitive` теперь привязан к публичному API. Пример с неописанным `com.example.impl` и отсутствующим `requires` на сервисный контракт заменён двумя согласованными дескрипторами. Их компиляция с минимальными типами через локальный `javac` прошла. Production MCQ Q28 B0 и freshness-sidecar синхронизированы, A/B/C/D и sections перечитаны автором. Проверены наличие 38 заголовков и всех Q-ссылок, а также конкретные прежние категоричные формулы; это **адресная согласованность**, не полная `П` ответов Q1–Q38. Markdown, JSON structure/parity, tell/linkage и общий pre-check PASS; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`.

**Контрольная точка 03.11.2026 — Java Modules Q1–Q5 · авторская полная `П`.** Каждый ответ и пример Q1–Q5 проверен по [JLS §7.7](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html), [Configuration](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html), [ModuleDescriptor](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/ModuleDescriptor.html), страницам модулей JDK и [изменениям JDK 11](https://docs.oracle.com/en/java/javase/24/migrate/significant-changes-jdk-11.html). В Q1 сужено описание видимости типов на classpath до обычных правил загрузчика; в Q2 уточнены `requires transitive`, `exports`, `opens`, регистрация провайдера и допустимость `requires static transitive`; в Q3 исправлен выпуск отделения JavaFX от JDK — начиная с JDK 11; в Q5 разделены общий `opens` и адресный `opens ... to`. Q4 проверен без содержательной правки. Q1–Q5 B0: правильность A/B/C/D, дистракторы и все sections сверены с теорией, изменённые пояснения синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, поэтому MCQ не `FINAL`; 38 Q/38 блоков, инвентарь прежний.

**Контрольная точка 03.11.2026 — Java Modules Q6–Q9 · авторская полная `П`.** Каждый ответ и пример проверен по [JLS §7.7](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html), [правилам разрешения модулей](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/package-summary.html) и [java launcher](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html). Q6 уточняет, что адресат `to` может не наблюдаться при сборке владельца; Q7 исправляет диаграмму обычного `requires`; Q8 объясняет, почему `requires static` сам не ищет optional module при runtime-разрешении, и показывает `--show-module-resolution`. Q9 проверен без содержательной правки. Дескрипторы Q6/Q7/Q9 с минимальными типами скомпилированы `javac`; отдельный пример Q8 показал отсутствие optional module в графе с одним `module path` и его появление после `--add-modules`. Production MCQ Q6–Q9 B0: A/B/C/D, дистракторы и все sections сверены с теорией, пояснения Q6/Q8 синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Modules Q10–Q13 · авторская полная `П`.** Каждый ответ и пример сверен с [ServiceLoader](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/ServiceLoader.html), [AccessibleObject](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/reflect/AccessibleObject.html), [java launcher](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html), [руководством миграции JDK](https://docs.oracle.com/en/java/javase/25/migrate/migrating-jdk-8-later-jdk-releases.html) и JLS Java SE 25. Q10 теперь точно указывает `ServiceConfigurationError` при отсутствии `uses` у вызывающего явного модуля; Q11 уточняет context class loader и порядок поиска named/unnamed providers; Q12 ограничивает пример ошибки фрагментом реального сообщения и отмечает предупреждение для методов `sun.misc.Unsafe` начиная с JDK 24; Q13 добавляет `--show-module-resolution` для проверки графа. Пересечения Q10/Q11 с Q36 и Q12/Q13 с Q5/Q9/Q33 сверены. На локальном JDK 22 пример Q10 нашёл провайдера с `uses` и получил `ServiceConfigurationError` без него; Q12 воспроизвёл `InaccessibleObjectException`. Production MCQ Q10–Q13 B0: A/B/C/D, дистракторы и все sections сверены с теорией, пояснения синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Modules Q14–Q17 · авторская полная `П`.** По [JLS §7.7.5](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.5), [ModuleDescriptor](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/ModuleDescriptor.html), [ModuleFinder](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/ModuleFinder.html), [JAR specification](https://docs.oracle.com/en/java/javase/25/docs/specs/jar/jar.html) и [Configuration](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) проверены все ответы Q14–Q17. Q14 подтверждён без правки теории. Q15 больше не обещает, что любой JAR без дескриптора станет automatic module: недопустимое имя или содержимое может вызвать `FindException`; уточнён алгоритм имени и предел открытых пакетов. Q16 отделяет явный именованный модуль от automatic, включая модули runtime-образа; исправлены заголовок, оглавление и таблица регистрации провайдеров. Q17 учитывает конфликт, когда читающий модуль сам содержит пакет, экспортируемый ему другой зависимостью. На локальном JDK 22 `jar --describe-module` показал имена `gson` и `commons.lang3`, а для `42.jar` сообщил о недопустимом имени. Production MCQ Q14–Q17 B0: A/B/C/D, дистракторы и все sections сверены с теорией; формулировки и пояснения синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS после исправления OPTION_LENGTH_RATIO Q16; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Modules Q18–Q20 · авторская полная `П`.** По [JAR specification](https://docs.oracle.com/en/java/javase/25/docs/specs/jar/jar.html), [jlink](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jlink.html), [jdeps](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jdeps.html) и [Configuration](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) проверены все ответы и примеры. Q18 теперь указывает точное требование одинакового экспортируемого API между версиями MRJAR, отсутствие обязательной проверки средой выполнения и невозможность версионировать `META-INF/services`. Q19 отмечает, что automatic module нельзя включить в связываемый `jlink` граф как обычный модуль; отдельный classpath-JAR по-прежнему нужно поставлять рядом. Q20 фиксирует значения `--compress=0/1/2` именно для документации JDK 25 и ограничивает `--bind-services` провайдерами используемых сервисов. Пересечения с Q3/Q34/Q36 сверены. На локальном JDK 22 `jlink` отверг automatic module, а явный `com.example.app` был скомпилирован, связан в образ и запущен через launcher. Production MCQ Q18–Q20 B0: A/B/C/D, дистракторы и все sections сверены с теорией, пояснения синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS после исправления OPTION_LENGTH_RATIO Q20; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Modules Q21–Q23 · авторская полная `П`.** По [JLS §7.7.1](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.1), [Gradle Java Library Plugin](https://docs.gradle.org/current/userguide/java_library_plugin.html), [Maven Compiler](https://maven.apache.org/plugins/maven-compiler-plugin/examples/module-info.html), [Maven Surefire](https://maven.apache.org/surefire/maven-surefire-plugin/examples/jpms.html), [ModuleLayer](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/ModuleLayer.html), [Configuration](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html) и [ServiceLoader](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/ServiceLoader.html) проверены ответы и примеры. Q21 уточняет ограниченность статического `jdeps` и связь с Q38. Q22 исправляет условие вывода Gradle на `module path`: нужны `module-info.class` или `Automatic-Module-Name`, а не только имя, которое JVM могла бы вывести из JAR; Maven пример пояснён как часть настройки, а не полная миграция. Q23 уточняет разрешение графа и различие прямого доступа к экспортируемому типу и регистрации закрытого пакета провайдера через `ServiceLoader`. Соседние Q15/Q25/Q38 сверены; новые Q не требуются. Production MCQ Q21–Q23 B0: A/B/C/D, дистракторы и sections перечитаны автором, пояснения синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Modules Q24–Q27 · авторская полная `П`.** По [Configuration Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html), [Gradle Java Testing](https://docs.gradle.org/current/userguide/java_testing.html), [Maven Surefire](https://maven.apache.org/surefire/maven-surefire-plugin/examples/jpms.html), [Spring Framework 7](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html), [Spring Boot nested JARs](https://docs.spring.io/spring-boot/specification/executable-jar/nested-jars.html), [Hibernate ORM 7.1](https://docs.hibernate.org/orm/7.1/userguide/html_single/) и [Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2.html) проверены ответы и примеры. Q24 уточняет, что `ServiceLoader` разрывает цикл только без обратного `requires` от провайдера; Q25 различает запуски Gradle и IDE; Q26 объясняет упаковку `BOOT-INF` и loader обычного Boot JAR, который не заменяет модульный прогон; Q27 связывает размещение `@Id` с field/property access и фиксирует требование спецификации к конструктору сущности. Сверены соседние Q10/Q12/Q23/Q28. Прежняя точка продолжения ошибочно называла Q27 вопросом о `ServiceLoader`; фактический Q27 — Hibernate/JPA. Production MCQ Q24–Q27 B0: A/B/C/D, дистракторы и sections перечитаны автором, пояснения синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Modules Q28–Q30 · авторская полная `П`.** По [JLS §7.7.1](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.1), [ServiceLoader](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/ServiceLoader.html), [FindException](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/FindException.html), [Configuration](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html), [Java 25 launcher](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html), [java.base](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/module-summary.html) и [официальному обзору JDK 25](https://inside.java/2025/10/20/jdk-25-performance-improvements/) проверены ответы и примеры. Q28 уточняет `public` провайдер и конструктор либо `provider()` при закрытом пакете. Q29 расширяет причины `FindException`: отсутствие модуля, ошибки чтения дескриптора и две версии в каталоге. Q30 исправляет границу `--illegal-access`: deprecated в JDK 16, obsolete/ignored с JDK 17; в JDK 25 нативный доступ без разрешения по умолчанию предупреждает, `deny` включают явно. Связь Leyden AOT cache с classpath и module path подтверждена; Q17/Q19/Q24/Q32/Q33/Q34 сверены. Production MCQ Q28–Q30 B0: A/B/C/D, дистракторы и sections перечитаны автором, пояснения синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Modules Q31–Q33 · авторская полная `П`.** По [ModuleFinder](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/ModuleFinder.html), [JLS §7.7.5](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.5), [Configuration](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/module/Configuration.html), [java](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html) и [javac](https://docs.oracle.com/en/java/javase/25/docs/specs/man/javac.html) Java SE 25 проверены ответы и примеры. Q31 уточняет, что не каждый JAR на `module path` образует допустимый automatic module: недопустимое имя или содержимое приводит к `FindException`. Q32 различает проверку всех модулей через `--validate-modules` и графа конкретного запуска через `--dry-run`. Q33 фиксирует, что `javac --release` для выпуска с JPMS запрещает расширять `--add-exports` экспорт системных модулей JDK. Сверены соседние Q5/Q15/Q17/Q29/Q32. Production MCQ Q31–Q33 B0: A/B/C/D, дистракторы и sections перечитаны автором, пояснения синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Modules Q34–Q36 · авторская полная `П`.** По [jlink](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jlink.html), [jdeps](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jdeps.html), [ServiceLoader](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/ServiceLoader.html) и [JLS §7.7.4](https://docs.oracle.com/javase/specs/jls/se25/html/jls-7.html#jls-7.7.4) Java SE 25 проверены ответы и примеры. Q34 уточняет платформенный разделитель `module path` и то, что `--ignore-signing-information` удаляет сведения о подписи из образа. Q35 добавляет анализ старых зависимостей через `--class-path` и ограничение `--generate-module-info`, которое с ним несовместимо. Q36 отделяет явный модуль, где допустим `static provider()`, от automatic module, которому нужен конструктор провайдера. Сверены Q10/Q11/Q19/Q20/Q23/Q31. Production MCQ Q34–Q36 B0: A/B/C/D, дистракторы и sections перечитаны автором, пояснения синхронизированы. Markdown, JSON structure/parity и общий MCQ pre-check PASS; 38 Q/38 блоков, инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

**Контрольная точка 03.11.2026 — Java Modules Q37–Q38 · авторская полная `П`.** По [Spring Framework](https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html), [Hibernate ORM 7.1](https://docs.hibernate.org/orm/7.1/userguide/html_single/), [Jackson 3 migration guide](https://github.com/FasterXML/jackson/blob/main/jackson3/MIGRATING_TO_JACKSON_3.md) и [Java launcher SE 25](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html) проверены ответы о работе на module path и миграции legacy-кода. Q37 уточняет случай Jackson 2 → 3: старые databind-аннотации могут компилироваться из оставшейся зависимости 2.x, но игнорироваться Jackson 3; `opens` этого не исправит. Q38 добавляет `--show-module-resolution` и `--dry-run` для проверки разрешённого графа и отдельно оставляет runtime-проверку reflection, services и динамической загрузки. Сверены Q21/Q26/Q27/Q31/Q33/Q34. Production MCQ Q37–Q38 B0: A/B/C/D, дистракторы и sections перечитаны автором; пояснения синхронизированы с Markdown. Markdown, JSON structure/parity и общий MCQ pre-check PASS; 38 Q/38 блоков, инвентарь прежний. Авторская полная `П` Java Modules Q1–Q38 завершена; независимая SFPL/ANSC/RU/blind-приёмка MCQ `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 03.11.2026 — PostgreSQL Q1–Q4 · авторская полная `П`.** По [архитектуре соединений](https://www.postgresql.org/docs/18/connect-estab.html), [настройкам памяти](https://www.postgresql.org/docs/18/runtime-config-resource.html), [статистике PostgreSQL 18](https://www.postgresql.org/docs/18/monitoring-stats.html), [архивированию WAL и PITR](https://www.postgresql.org/docs/18/continuous-archiving.html), [checkpoint](https://www.postgresql.org/docs/18/wal-configuration.html) и [release notes PostgreSQL 17/18](https://www.postgresql.org/docs/18/release-18.html) проверены ответы и границы версий. Q1 отличает клиентские backend от фоновых процессов в `pg_stat_activity`. Q2 фиксирует перезапуск после изменения `shared_buffers` и интервальную оценку накопительных счётчиков. Q3 перечисляет необходимые условия PITR, включая непрерывный архив WAL. Q4 добавляет `pg_stat_checkpointer` и различия `num_requested`/`num_done` между PostgreSQL 17 и 18. Production MCQ Q1–Q4 B0 синхронизированы; A/B/C/D, дистракторы и sections перечитаны автором. `verify-md-no-mcq` PASS, `verify-mcq-json` PASS (55 Q/55 блоков; semantic `NOT_RUN`), общий MCQ pre-check PASS; его замечания к 11 блокам файла не затрагивают Q1–Q4. Состав файла и инвентарь прежние. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 03.11.2026 — PostgreSQL Q5 и Q49–Q50 · авторская полная `П`.** По [`pg_hba.conf`](https://www.postgresql.org/docs/18/auth-pg-hba-conf.html), [`pg_hba_file_rules`](https://www.postgresql.org/docs/18/view-pg-hba-file-rules.html), [PgBouncer Configuration](https://www.pgbouncer.org/config.html), [Features](https://www.pgbouncer.org/features.html), [Usage](https://www.pgbouncer.org/usage.html) и [changelog 1.26.1](https://www.pgbouncer.org/changelog.html) проверены ответы и версии. Q5 отличает текущий файл правил от загруженной конфигурации, учитывает include и устаревание MD5-хешей. Q49 отделяет клиентскую аутентификацию PgBouncer от `pg_hba.conf` PostgreSQL и уточняет метрики ожидания. Q50 добавляет оговорку о нерешённых ошибках отслеживания `search_path` в PgBouncer 1.26.1. Production MCQ Q5/Q49/Q50 B0 синхронизированы; A/B/C/D, дистракторы и sections перечитаны автором. `verify-md-no-mcq` PASS, `verify-mcq-json` PASS (55 Q/55 блоков; semantic `NOT_RUN`), общий MCQ pre-check PASS. После улучшения дистракторов текущий пакет вне коридора не отмечен; замечания остались к 9 другим блокам файла. Инвентарь прежний. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 03.11.2026 — PostgreSQL Q6–Q10 · авторская полная `П`.** По [range/multirange](https://www.postgresql.org/docs/18/rangetypes.html), [JSON](https://www.postgresql.org/docs/18/datatype-json.html), [arrays](https://www.postgresql.org/docs/18/arrays.html), [ANY/SOME](https://www.postgresql.org/docs/18/functions-comparisons.html), [hstore](https://www.postgresql.org/docs/18/hstore.html), [ALTER TYPE](https://www.postgresql.org/docs/18/sql-altertype.html) и [composite types](https://www.postgresql.org/docs/18/rowtypes.html) PostgreSQL 18 проверены ответы и границы возможностей. Q6 добавляет поиск и запрет пересечений диапазонов; Q7 — более строгий ввод `jsonb`; Q8 — нестрогие объявленные размеры массива и `NULL`-семантику `ANY`; Q9 — поддерживаемые GIN/GiST-операторы `hstore`; Q10 — использование новой `ENUM`-метки после `COMMIT` и `DOMAIN` поверх composite. Сверен Q48 о JSONB-операторах; дублирования нет. Production MCQ Q6–Q10 B0 синхронизированы, A/B/C/D, дистракторы и sections перечитаны автором. `verify-md-no-mcq` PASS, `verify-mcq-json` PASS (55 Q/55 блоков; semantic `NOT_RUN`), общий MCQ pre-check PASS. Q6–Q10 без замечаний pre-check; 9 других блоков файла вне коридора. Инвентарь прежний; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 03.11.2026 — PostgreSQL Q11–Q15 · авторская полная `П`.** По [типам индексов](https://www.postgresql.org/docs/18/indexes-types.html), [сортировке B-tree](https://www.postgresql.org/docs/18/indexes-ordering.html), [составным индексам](https://www.postgresql.org/docs/18/indexes-multicolumn.html), [GIN](https://www.postgresql.org/docs/18/gin.html), [полнотекстовым индексам](https://www.postgresql.org/docs/18/textsearch-indexes.html) и [BRIN](https://www.postgresql.org/docs/18/brin.html) PostgreSQL 18 проверены ответы и условия применения. Q11 отделяет `bloom`-расширение от встроенных методов. Q12 показывает обратный проход B-tree и необходимость смешанного порядка ключей для соответствующего `ORDER BY`. Q13 добавляет очистку GIN pending list и границу порядка столбцов. Q14 уточняет поиск ближайших точек и перепроверку полнотекстовых кандидатов. Q15 добавляет явную сводку новых BRIN-диапазонов. Сверены Q16–Q19 и Q20–Q24; дублирования нет. Production MCQ Q11–Q15 B0 синхронизированы, A/B/C/D, дистракторы и sections перечитаны автором. `verify-md-no-mcq` PASS, `verify-mcq-json` PASS (55 Q/55 блоков; semantic `NOT_RUN`), общий MCQ pre-check PASS. Q11–Q15 без замечаний pre-check; 9 других блоков файла вне коридора. Инвентарь прежний; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 03.11.2026 — PostgreSQL Q16–Q19 · авторская полная `П`.** По [partial indexes](https://www.postgresql.org/docs/18/indexes-partial.html), [expression indexes](https://www.postgresql.org/docs/18/indexes-expressional.html), [index-only scans](https://www.postgresql.org/docs/18/indexes-index-only-scans.html), [CREATE INDEX](https://www.postgresql.org/docs/18/sql-createindex.html), [progress reporting](https://www.postgresql.org/docs/18/progress-reporting.html), [DROP INDEX](https://www.postgresql.org/docs/18/sql-dropindex.html) и [release notes PostgreSQL 15](https://www.postgresql.org/docs/15/release-15.html) проверены ответы и версии. Q16 добавляет условную уникальность при soft delete и оговорку о `NULLS NOT DISTINCT` с версии 15. Q17 уточняет `IMMUTABLE` для всего определения индекса. Q18 отделяет `INCLUDE` от поддержки `Index Only Scan` конкретным классом GiST/SP-GiST и показывает проверку `Heap Fetches`. Q19 добавляет мониторинг фаз concurrent build и ограничения `DROP INDEX CONCURRENTLY`. Сверены Q11–Q15/Q20–Q24. Production MCQ Q16–Q19 B0 синхронизированы, A/B/C/D, дистракторы и sections перечитаны автором. `verify-md-no-mcq` PASS, `verify-mcq-json` PASS (55 Q/55 блоков; semantic `NOT_RUN`), общий MCQ pre-check PASS; Q16–Q19 без замечаний, 9 других блоков вне коридора. Инвентарь прежний; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 03.11.2026 — PostgreSQL Q20–Q24 · авторская полная `П`.** По [Using EXPLAIN](https://www.postgresql.org/docs/18/using-explain.html), [EXPLAIN](https://www.postgresql.org/docs/18/sql-explain.html), [Combining Multiple Indexes](https://www.postgresql.org/docs/18/indexes-bitmap-scans.html), [Index-Only Scans](https://www.postgresql.org/docs/18/indexes-index-only-scans.html) и [planner cost constants](https://www.postgresql.org/docs/18/runtime-config-query.html) PostgreSQL 18 проверены все пять ответов. Q20 уточняет сравнение `rows`/`actual rows` за запуск, накопительные `Buffers` и раннюю остановку `Seq Scan` при `LIMIT`; Q21 — отличие измеренного времени от клиентской задержки, опции `TIMING OFF` и `SERIALIZE`; Q23 — `Heap Blocks: exact/lossy`, `Rows Removed by Index Recheck` и предел вывода из `Recheck Cond`. Q22/Q24 сверены без новой правки; соседние Q11–Q19 и Q25 не дублируются. Production MCQ Q20/Q21/Q23 B0 синхронизированы; правильность, дистракторы, A/B/C/D и sections Q20–Q24 перечитаны автором. `verify-md-no-mcq` PASS, `verify-mcq-json` PASS (55 Q/55 блоков; semantic `NOT_RUN`), общий MCQ pre-check PASS. В пакете нет hard-сигналов; Q20 B0 имеет лишь предупреждение о числе частей одного варианта, во всём файле 10 блоков вне мягкого коридора. Инвентарь прежний; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 03.11.2026 — PostgreSQL Q25 и Q53–Q54 · авторская полная `П`.** По [planner statistics](https://www.postgresql.org/docs/18/planner-stats.html), [ANALYZE](https://www.postgresql.org/docs/18/sql-analyze.html), [pg_stats](https://www.postgresql.org/docs/18/view-pg-stats.html), [resource consumption](https://www.postgresql.org/docs/18/runtime-config-resource.html), [WAL settings](https://www.postgresql.org/docs/18/runtime-config-wal.html) и [pg_stat_statements](https://www.postgresql.org/docs/18/pgstatstatements.html) PostgreSQL 18 проверены ответы и версии. Q25 добавляет пример `CREATE STATISTICS (mcv)`, отдельный `ANALYZE` и границу применения `dependencies`. Q53 включает `max_wal_size` как мягкий предел после изменения `shared_buffers`. Q54 отделяет агрегированную нормализованную запись `query` от параметров конкретного вызова и плана; добавляет `stats_reset`/`dealloc` и оговорку о `track_planning`. Сверены Q20–Q24 и тематические пересечения по заголовкам Q33/Q49–Q51/Q55; новый файл не нужен. Production MCQ Q25/Q53/Q54 B0 синхронизированы; A/B/C/D, правильные ответы, дистракторы и sections проверены автором. `verify-md-no-mcq` PASS, `verify-mcq-json` PASS (55 Q/55 блоков; semantic `NOT_RUN`), общий MCQ pre-check PASS; у Q25/Q53/Q54 нет hard-сигналов и мягких предупреждений, девять иных блоков файла вне коридора. Инвентарь прежний; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 03.11.2026 — PostgreSQL Q26–Q30 · авторская полная `П`.** По [Transaction Isolation](https://www.postgresql.org/docs/18/transaction-iso.html), [SET TRANSACTION](https://www.postgresql.org/docs/18/sql-set-transaction.html), [HOT](https://www.postgresql.org/docs/18/storage-hot.html), [System Columns](https://www.postgresql.org/docs/18/ddl-system-columns.html), [Routine Vacuuming](https://www.postgresql.org/docs/18/routine-vacuuming.html), [Explicit Locking](https://www.postgresql.org/docs/18/explicit-locking.html) и [ROLLBACK TO SAVEPOINT](https://www.postgresql.org/docs/18/sql-rollback-to.html) PostgreSQL 18 проверены все пять ответов. Q26 исправляет `SET TRANSACTION` вне транзакции и объясняет конфликт конкурирующих `UPDATE` при `READ COMMITTED` и `REPEATABLE READ`. Q27 учитывает `HOT` и очистку промежуточных версий без `VACUUM`. Q28 уточняет, что `xmax` может относиться к блокировке и содержать `MultiXact ID`. Q29 полностью сверен без правки. Q30 добавляет освобождение блокировок после savepoint и исключение для позиции курсора. Production MCQ Q26–Q28/Q30 B0 синхронизированы; правильность, дистракторы, A/B/C/D и sections Q26–Q30 проверены автором. `verify-md-no-mcq` PASS, `verify-mcq-json` PASS (55 Q/55 блоков; semantic `NOT_RUN`), общий MCQ pre-check PASS; замечаний к пакету нет, восемь других блоков вне мягкого коридора. Инвентарь прежний; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 04.11.2026 — Distributed Email Service Q1–Q4 · авторская полная `П`.** По официальным SES notification contents, configuration overrides, API tracking overrides, tenants, tenant suppression и Gmail sender guidelines проверены ответы и границы функций. Q1 проверен без правки; Q2 добавляет неопределённость `complainedRecipients` у multi-recipient письма; Q3 разделяет подписку и open/click tracking, указывает запросный scope переопределения в `SendBulkEmail`; Q4 требует явных `TenantName`/`X-SES-TENANT` и `SuppressionScope=TENANT`. Сентябрьские публикации AWS послужили сигналом; код с boolean tracking override из статьи не перенесён, поскольку официальный API требует `ENABLED`/`DISABLED`. Production MCQ Q2/Q3/Q4 B1 добавлены, Q2/Q4 B0 уточнены, Q1/Q3 B0 проверены; все A/B/C/D и sections семи блоков перечитаны автором. `verify-md-no-mcq` OK, `verify-mcq-json` PASS (4 Q/7 блоков; semantic `NOT_RUN`). Инвентарь прежний; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 05.11.2026 — Google Maps Q22–Q24 · авторская полная `П`.** Сверены соседние Q1–Q3/Q19–Q21. В Q22 пример собственной архитектуры отделён от внутренних систем Google и убраны фиксированные CDN/граф/ML-оценки. Q23 выбирает хранилища по запросам и правам на данные; убрана обязательность Bigtable/Spanner и некорректное обещание локальности соседних `(zoom,x,y)`. Q24 различает категории данных, правила трансграничной передачи и готовность резервного региона; устранены ложный абсолютный запрет GDPR и обещание бесшовного DNS failover. Проверены CloudFront, Bigtable, PostGIS, Google Cloud DR, EDPB и AWS Architecture Blog 13.08.2026. Production MCQ Q22–Q24 и freshness-sidecar синхронизированы; варианты A/B/C/D и sections перечитаны автором. Структурная проверка пакета PASS: Markdown, MCQ JSON (30 Q/30 блоков) и `git diff --check`; semantic review валидатора `NOT_RUN`, независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`. Инвентарь прежний.

**Контрольная точка 05.11.2026 — MCP Q1–Q4 · авторская полная `П`.** Сверены заголовки Q1–Q30 и пересечения вводных Q1–Q4 с Q5–Q8/Q13–Q14/Q28–Q30. Редакция MCP 2026-07-28 уже выпущена: Q2–Q3 больше не описывают обязательный `initialize` и stateful-сессию; Q1 оставляет N+M оценкой повторного использования, Q4 отделяет протокол от function calling, LangChain tools и OpenAPI. Парные production MCQ Q1–Q4 B0 переписаны, A/B/C/D и sections перечитаны автором; `verify-md-no-mcq` PASS, `verify-mcq-json` PASS (30 Q/30 блоков, semantic `NOT_RUN`), `git diff --check` PASS. Официальные источники: спецификация и архитектура MCP 2026-07-28, блог релиза 28.07.2026, документация OpenAI MCP, LangChain tools и OpenAPI 3.1.1. Инвентарь прежний; whole-file freshness `TODO`, независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, файл не `FINAL`.

**Контрольная точка 05.11.2026 — MCP Q27–Q30 · авторская `П`.** Сверены соседние Q5–Q15/Q20–Q26. В Q27 исправлены коды: для `2026-07-28` MCP задаёт `-32020`/`-32021`/`-32022`, отсутствие ресурса — `-32602`, старый `-32002` принимается только ради совместимости; отмена не является ошибкой `-32001`. Q28 уточняет подтверждение фильтра `subscriptions/listen`, повторную подписку и перечитывание списка после обрыва. Q29 оставлен stateless с явным handle; Q30 уточняет обрыв SSE и миграцию. Production MCQ Q27–Q30 и частичная freshness-запись синхронизированы; авторская вычитка и структурная проверка записаны в [authoring-checkpoint.md](docs/interview-coverage/authoring-checkpoint.md). Источники: официальные MCP Overview, Resources, Subscriptions, Streamable HTTP и changelog редакции `2026-07-28`; блог релиза 28.07.2026. Новых шпаргалок нет, инвентарь прежний. Независимая содержательная MCQ-приёмка и whole-file freshness `NOT_RUN`/`TODO`; файл не `FINAL`.

**Решение 05.11.2026 — TLS-доверие, `DEEPEN` Q31/Q43/Q44.** В `security/tls-ssl-interview.md` найден не только пробел диагностики: Q31 ошибочно загружал системный `cacerts` через `KeyStore.load(null,null)`, Q43 ограничивал сверку ключа RSA `modulus`, Q44 предлагал импортировать непроверенный leaf из проблемного соединения в общий `cacerts`. Исправлены ответы и парные production MCQ по официальным JSSE, `keytool`, OpenSSL `s_client`/`verify`; частичная запись freshness ограничена этими Q. Внешним сигналом послужила публикация [Go Security 26.10.2026](https://go.dev/blog/emudhra-incident) о компрометации intermediate, но факты Java/TLS сверены отдельно. Состав шпаргалок не изменился; независимая MCQ-приёмка `NOT_RUN`.

**Решение 05.11.2026 — TLS Q12/Q15, `DEEPEN` завершён.** Q12 теперь различает путь, заранее настроенный trust anchor, проверку имени и отдельную политику отзыва; отзыв intermediate не назван мгновенной защитой всех клиентов. Q15 разделяет self-issued (`Issuer == Subject`) и self-signed (подпись проверяется собственным публичным ключом), а также владение ключом и доверие к личности. Заголовок/TOC Q12 и production MCQ Q12/Q15 синхронизированы; частичный freshness-sidecar ограничен проверенными Q. Источники: RFC 5280, Oracle JDK 25 PKI Guide, CA/Browser Forum TLS Baseline Requirements, Go Security 26.10.2026. Структурная проверка и авторская вычитка — в [authoring-checkpoint.md](docs/interview-coverage/authoring-checkpoint.md); независимая MCQ-приёмка `NOT_RUN`, инвентарь прежний.

**Решение 05.11.2026 — TLS Q13/Q14, `DEEPEN` завершён.** Q13 больше не приписывает всем браузерам и Java 9+ одинаковое поведение: RFC 9525 запрещает `CN` как идентификатор сервиса, а CA/Browser Forum требует `SAN` для публичного серверного сертификата; работа `HostnameVerifier` зависит от клиента и настройки. Q14 исправляет ложное противопоставление: wildcard — значение `dNSName` в `SAN`, покрывающее ровно один левый DNS-label; выбор учитывает все имена и общий ключ сертификата. Заголовки/TOC и production MCQ Q13/Q14 синхронизированы, частичная freshness-запись ограничена ими. Источники: [RFC 9525](https://www.rfc-editor.org/rfc/rfc9525.html), [CA/Browser Forum TLS BR](https://cabforum.org/working-groups/server/baseline-requirements/requirements/), [Oracle JSSE Guide](https://docs.oracle.com/en/java/javase/25/security/java-secure-socket-extension-jsse-reference-guide.html). Авторская точечная `О`; полная `П`, whole-file freshness и независимая MCQ-приёмка `NOT_RUN`. Инвентарь прежний.

**Решение 05.11.2026 — TLS Q11, `DEEPEN` завершён.** В ответе и парном production MCQ исправлено смешение `AIA` (`caIssuers`/OCSP) с отдельным `CRL Distribution Points`; подпись сертификата больше не названа достаточным основанием доверия без проверки пути к якорю клиента. Сверены Q12/Q13/Q15/Q18/Q43/Q44, первоисточник — [RFC 5280, §4.2.2.1](https://www.rfc-editor.org/rfc/rfc5280.html#section-4.2.2.1) и [§4.2.1.13](https://www.rfc-editor.org/rfc/rfc5280.html#section-4.2.1.13). Авторская точечная `О`; независимая MCQ-приёмка и whole-file freshness `NOT_RUN`/`TODO`. Инвентарь прежний.

**Решение 05.11.2026 — TLS Q18, `DEEPEN` завершён.** Q18 и парный production MCQ теперь различают подписанный CRL, адресный OCSP-ответ и его доставку через stapling; `OCSP good` не выдан за полную проверку сертификата, а Must-Staple — за универсальный отказ каждого клиента. Исправлена хронология Let's Encrypt: 90 дней по умолчанию и опциональные 160 часов, доступные в 2026 году; короткий срок не объявлен заменой реакции на утечку ключа. Частичная freshness-запись ограничена Q18. Источники: [RFC 6960](https://www.rfc-editor.org/rfc/rfc6960.html), [RFC 6066](https://www.rfc-editor.org/rfc/rfc6066.html), [RFC 7633](https://www.rfc-editor.org/rfc/rfc7633.html), [RFC 9325](https://www.rfc-editor.org/rfc/rfc9325.html), [CA/Browser Forum TLS BR 2.3.1](https://cabforum.org/working-groups/server/baseline-requirements/requirements/), [Let's Encrypt, обновление 22.07.2026](https://letsencrypt.org/docs/cert-lifetimes/) и [Mozilla, 20.10.2026](https://blog.mozilla.org/security/2026/10/20/protecting-firefox-users-following-the-compromise-of-emudhra-intermediate-certificates/). Авторская точечная `О`; независимая MCQ-приёмка и whole-file freshness `NOT_RUN`/`TODO`, инвентарь прежний.

**Решение 05.11.2026 — TLS Q19, `DEEPEN` завершён.** CT описан как наблюдаемость публичной выдачи: SCT обещает включение в лог в пределах MMD, но не доказывает немедленную публикацию, доверие или отзыв. Убрано универсальное «ровно два SCT»: Chrome различает срок, операторов и состояние логов; Chrome 148 больше не засчитывает SCT из stapled OCSP для CT, не меняя роль OCSP при проверке отзыва. Парный production MCQ Q19 и частичная freshness-запись синхронизированы. Источники: [RFC 9162](https://www.rfc-editor.org/rfc/rfc9162.html), [Chrome CT Policy](https://googlechrome.github.io/CertificateTransparency/ct_policy.html), [Mozilla CT Policy](https://wiki.mozilla.org/SecurityEngineering/Certificate_Transparency), [Chrome 148 от 05.05.2026](https://developer.chrome.com/release-notes/148). Авторская точечная `О`; независимая MCQ-приёмка и whole-file freshness `NOT_RUN`/`TODO`, инвентарь прежний.

**Контрольная точка 05.11.2026 — TLS Q20/Q21 · авторская точечная `О`.** Q20 разделяет `(EC)DHE` для согласования секрета, сертификат и подпись для аутентификации, `AEAD` для записей; удалены неподтверждённые показатели скорости и оговорены старый RSA key exchange в TLS 1.2 и PSK в TLS 1.3. Q21 различает состав cipher suite по версиям: в TLS 1.2 `SHA256` у примера AES-GCM относится к PRF, в TLS 1.3 suite задаёт AEAD и хеш HKDF. Исправлен обратный порядок CBC: при вынужденной совместимости нужен encrypt-then-MAC, а современный выбор — AEAD. Сверены RFC 5246/5289/7366/9325, актуальный [RFC 9846](https://datatracker.ietf.org/doc/html/rfc9846) (заменил RFC 8446) и TLSRef Server-Side TLS. Парные production MCQ Q20/Q21 B0 и частичная freshness-запись синхронизированы. Заголовки и состав файлов прежние; инвентарь не менялся. Автор перечитал Q20/Q21 и все A/B/C/D с sections; адресная структурная проверка PASS (Markdown, JSON 45 Q/45 блоков, diff). Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, whole-file freshness `TODO`, файл не `FINAL`.

**Решение 05.11.2026 — TLS Q3/Q7/Q22–Q24.** [RFC 9846, июль 2026](https://datatracker.ietf.org/doc/html/rfc9846) заменил RFC 8446. Из ответов удалены абсолютные утверждения, что TLS 1.3 и любая его cipher suite всегда дают Forward Secrecy: полный handshake с `(EC)DHE`, `psk_dhe_ke`, `psk_ke` и `0-RTT` имеют разные свойства. Q3 также исправляет неверное название `CIA` для конфиденциальности, целостности и аутентификации; [NIST](https://csrc.nist.gov/glossary/term/CIA) относит `A` к доступности. Q23 отделяет утечку RSA-ключа от будущей квантовой угрозы классическому `(EC)DHE`. Q24 называет пять suites RFC 9846, из них три — в профиле TLSRef Modern; убраны неподтверждённые общие сравнения скорости. Сверена официальная публикация [Cloudflare от 08.09.2026](https://blog.cloudflare.com/automatic-key-exchange-for-origins/) о постквантовом обмене; адресный поиск Baeldung не дал дополнительного подтверждённого сценария. Парные MCQ Q3/Q7/Q22–Q24 и частичная freshness-запись синхронизированы; структурная проверка пары PASS (45 Q/45 блоков, semantic `NOT_RUN`), `git diff --check` PASS. Независимая MCQ-приёмка `NOT_RUN`, whole-file freshness `TODO`, инвентарь прежний.

**Решение 05.11.2026 — TLS Q8/Q9 · авторская точечная `О`.** Q8 отделяет ранние данные от завершённого handshake: у 0-RTT нет протокольных гарантий от replay и Forward Secrecy; ни `GET`, ни одна идемпотентность не дают автоматической безопасности. Для HTTP добавлены выбор безопасных операций, `425 Too Early` и защита от повторов между узлами. Q9 разделяет TLS 1.2 Session ID, защищённый ticket и PSK-возобновление TLS 1.3: ticket может быть stateful или stateless, PSK не пересылается в `NewSessionTicket`, новый handshake выводит новые ключи; срок билета TLS 1.3 не более 7 дней. Открыты [RFC 9846](https://datatracker.ietf.org/doc/html/rfc9846), [RFC 8470](https://www.rfc-editor.org/rfc/rfc8470.html), [RFC 5077](https://www.rfc-editor.org/rfc/rfc5077.html), [RFC 9325](https://www.rfc-editor.org/rfc/rfc9325.html) и [OpenSSL](https://docs.openssl.org/master/man3/SSL_CTX_set_options/). Парные production MCQ Q8/Q9 и частичная freshness-запись синхронизированы. Структурная проверка пары PASS (45 Q/45 блоков, semantic `NOT_RUN`), `git diff --check` PASS; независимая MCQ-приёмка `NOT_RUN`, инвентарь прежний.

**Решение 05.11.2026 — TLS Q39/Q45 · авторская точечная `О`.** Q39 отделяет аутентификацию сервера и целостность handshake от Forward Secrecy: FS ограничивает последствия поздней утечки ключа, но не блокирует активный MITM при ошибочном доверии. Q45 перестроен вокруг фактического TLS-терминатора и клиентов: Modern TLSRef — TLS 1.3, Intermediate добавляет TLS 1.2; режим PSK проверяется отдельно от suite, HSTS и OCSP stapling применяются по сценарию. Предел срока публичных сертификатов по CA/Browser Forum BR 2.3.1 для выдачи с 15.03.2026 до 15.03.2027 — **200 дней**, а не прежнее «до года». Источники: [RFC 9846](https://datatracker.ietf.org/doc/html/rfc9846), [RFC 9325](https://www.rfc-editor.org/rfc/rfc9325.html), [TLSRef](https://docs.tlsref.org/server-side-tls.html), [CA/B BR](https://cabforum.org/working-groups/server/baseline-requirements/requirements/) и [RFC 6797](https://www.rfc-editor.org/rfc/rfc6797.html). [Baeldung, обновление 30.09.2026](https://www.baeldung.com/spring-tls-setup) просмотрен: настройка one-way/mTLS уже покрыта Q35/Q36, отдельного Q не требуется. Production MCQ Q39/Q45 и частичная freshness-запись синхронизированы; структурная проверка пары PASS (45 Q/45 блоков, semantic `NOT_RUN`), `git diff --check` PASS. Независимая MCQ-приёмка `NOT_RUN`, whole-file freshness `TODO`, инвентарь прежний.

**Точная точка следующего авторинга:** `security/tls-ssl-interview.md` Q25/Q26 о Let's Encrypt/ACME: проверить срок 90 дней и короткоживущий профиль 160 часов, дату доступности и challenge-ограничения по официальным Let's Encrypt и RFC 8555; сверить Q18/Q45 и парные MCQ. Затем при необходимости проверить Q40 HSTS против обновлённого чеклиста. Spring Batch Q6/Q28/Q38 остаётся отдельной очередью независимой MCQ-приёмки. MCP Q16 с TypeScript вне очереди расширения.


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
   Python Core Q3/Q5/Q10–Q12, Typing Q4, Concurrency Q9 и Packaging Q5
   по 3.15 написаны; Python Core Q1–Q12, Python Typing Q1–Q4, Python Concurrency Q1–Q9, Python Database Q1–Q8, Python Packaging Q1–Q5 и Python Testing Q1–Q7 прошли авторскую `П`. FastAPI Q1–Q7 и MySQL Q1–Q6 прошли авторскую `П`; независимая MCQ-приёмка ожидается. В Core MCQ Q2 B0 исправлен второй правильный ответ. Go stdlib Q1–Q31, Go Concurrency Q23–Q25, Go Core Q1–Q36 и Go Memory & GC Q1–Q27 прошли авторскую `П`. Для Python Core, Typing, Concurrency, Packaging, Testing и Go Memory & GC общий pre-check exit 0, независимая MCQ-приёмка остаётся. JDBC/пулы Q1–Q6 прошли авторскую `П`; PostgreSQL Q1–Q55 прошли авторскую полную `П` по пакетам, без независимой MCQ-приёмки; PostgreSQL 19 после GA, Batch blind-приёмка ожидается.
   PostgreSQL 19 проверять после GA. Stock Exchange уже написан как
   `system-design/design-exchange-interview.md` Q1–Q4; второй файл не создавать.
2. Ad Click Event Aggregation и Distributed Email Service Q1–Q4 написаны; их MCQ вместе с
   Metrics Platform Q1–Q4, Leaderboard Q1–Q5, Object Storage Q1–Q4, Reservation System Q1–Q4,
   Notification System Q1–Q4, Digital Wallet Q1–Q4, Database Transactions Q43,
   Java CompletableFuture Q2 B0/Q3 B0/B1/Q4 B0/Q6 B0/Q8 B0/B1/Q10 B0/Q11/Q14,
   Kubernetes Q22/Q39/Q46/Q47,
   Spring Rest Client Q4/Q6/Q7/Q11/Q12/Q13/Q14, WebAuthn Q24/Q46, Spring Security Q44,
   Metrics & Tracing Q40, Spring Testing Q9/Q10, Uber Q6/Q7,
   Google Maps Q27, Spring Framework Q1/Q2/Q9/Q10 B0/B1,
   Spring Batch Q6 B0/B1/Q28/Q38, Spring Data JPA Q3 B0/Q4 B1/Q5–Q43 B0,
   Integration Testing Q4/Q36 B0, Testcontainers Q2/Q5/Q6/Q28 B0,
   Hibernate Q8–Q10 B0/Q14–Q16 B0/Q19–Q21 B0/Q23–Q25/Q27/Q36 B0,
   Database Transactions Q15/Q22/Q23 B0, Python Core Q3/Q5 B1/Q10–Q12 B0 и Q12 B1,
   Python Typing Q4 B1,
   AI Application Architecture Q33 B0, LLM Integration Patterns Q14 B0
   и RabbitMQ Streams Q40–Q42 ждут
   независимой MCQ-приёмки.
   Java 26–27, HTTP/3, Jackson 3 и Python typing/packaging уже написаны.
   Остальные P2/P3 без решения остаются кандидатами. Python 3.15 Core
   Q3/Q5/Q10–Q12, Typing Q4, Concurrency Q9 и Packaging Q5 написаны;
   Python Core Q1–Q12, Python Typing Q1–Q4, Python Concurrency Q1–Q9, Python Database Q1–Q8, Python Packaging Q1–Q5, Python Testing Q1–Q7, Go stdlib Q1–Q31, Go Core Q1–Q36 и Go Memory & GC Q1–Q27 прошли авторскую полную `П`; FastAPI Q1–Q7 и MySQL Q1–Q6 — авторскую `П`, JDBC/пулы Q1–Q6 — авторская `П`; PostgreSQL Q1–Q55 прошли авторскую полную `П` по пакетам, без независимой MCQ-приёмки; PostgreSQL 19 после GA, Batch blind-приёмка ожидается. Статус релиза PostgreSQL 19 перепроверить после GA 19.11.2026.
3. Отдельной задачей пройти независимую blind-приёмку новых MCQ и старый
   parity/pre-check backlog Hibernate, Spring Data JPA (после Q37
   ещё 4 hard-сигнала skel, 02.11.2026; Hibernate после Q8–Q10/Q14–Q16/Q19–Q21/Q23–Q25/Q27/Q36 — 15),
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

**Контрольная точка 05.11.2026 — TLS Q25/Q26 · авторская точечная `О`.** По [RFC 8555](https://www.rfc-editor.org/rfc/rfc8555.html), [RFC 8737](https://www.rfc-editor.org/rfc/rfc8737.html) и официальным [срокам](https://letsencrypt.org/docs/cert-lifetimes/), [профилям](https://letsencrypt.org/docs/profiles/) и [типам проверок Let's Encrypt](https://letsencrypt.org/docs/challenge-types/) исправлены ответы и парные production MCQ. `classic` на 05.11.2026 действует 90 дней по умолчанию; опциональный `shortlived` — 160 часов при поддержке выбора профиля клиентом и наличии профиля в `directory`. Удалены неподтверждённые утверждения о датах и встроенном ACME конкретных версий Nginx/Apache. Для Let's Encrypt wildcard требует DNS-01, HTTP-01 начинается на порту 80, TLS-ALPN-01 использует 443; DNS-01 требует видимой CA TXT-записи и аккуратной автоматизации. Инвентарь прежний. Структурные проверки пары PASS (45 Q/45 блоков, semantic `NOT_RUN`); независимая MCQ-приёмка и whole-file freshness остаются `NOT_RUN`/`TODO`.

**Точная точка следующего авторинга:** `security/tls-ssl-interview.md` Q40 HSTS — проверить первый визит, preload и `includeSubDomains` по RFC 6797 и актуальным документам браузеров; сверить Q39/Q45 и парный MCQ Q40. Затем перейти к следующему кандидату Markdown-плана. Spring Batch Q6/Q28/Q38 — отдельная очередь независимой MCQ-приёмки.

**Решение 05.11.2026 — HSTS, авторская точечная `О`.** TLS Q40 и REST API Q24 с парными MCQ исправлены по [RFC 6797](https://www.rfc-editor.org/rfc/rfc6797.html), [Chromium](https://www.chromium.org/hsts/), [hstspreload.org](https://hstspreload.org/) и [Spring Security 6.5](https://docs.spring.io/spring-security/reference/6.5/servlet/exploits/headers.html). `preload` в заголовке сам по себе не включает домен в браузерный список; HSTS из HTTP-ответа браузер игнорирует, а `includeSubDomains` и длительный `max-age` требуют проверки всех затронутых имён. Единый набор security-заголовков не обязателен для каждого REST API-ответа. Новых Q/файлов и изменений инвентаря нет. Адресная структура двух пар PASS (88 Q/88 блоков, semantic `NOT_RUN`); независимая содержательная MCQ-приёмка и whole-file freshness `NOT_RUN`/`TODO`.

**Точная точка следующего авторинга:** TLS Q41 о certificate pinning/HPKP: проверить выводы о Chrome, Certificate Transparency и OkHttp по первоисточникам, сверить Q19/Q40 и парный MCQ Q41. Spring Batch Q6/Q28/Q38 остаётся отдельной очередью независимой MCQ-приёмки.

**Решение 05.11.2026 — TLS Q41 · авторская точечная `О`.** По [Chrome 67](https://developer.chrome.com/blog/chrome-67-deps-rems/), [Chrome 72](https://developer.chrome.com/blog/chrome-72-deps-rems/), [Firefox 72](https://developer.mozilla.org/en-US/docs/Mozilla/Firefox/Releases/72), [Android TLS](https://developer.android.com/privacy-and-security/security-ssl), [OkHttp CertificatePinner](https://raw.githubusercontent.com/square/okhttp/master/okhttp/src/commonJvmAndroid/kotlin/okhttp3/CertificatePinner.kt) и [Chrome CT](https://googlechrome.github.io/CertificateTransparency/) исправлены Q41 и парный MCQ. HPKP не представляется действующей браузерной защитой, CT не приравнивается к pinning, а pinning в Android не рекомендуется по умолчанию из-за риска потери связи при ротации. Частичная freshness-запись ограничена Q41; инвентарь прежний. Структура пары PASS (45 Q/45 блоков, semantic `NOT_RUN`); независимая MCQ-приёмка и whole-file freshness `NOT_RUN`/`TODO`.

**Точная точка следующего авторинга:** TLS Q42 — проверить команды `openssl s_client`, `testssl.sh`, `keytool`, различие наблюдения и верификации, SNI и TLS 1.3 `-ciphersuites`; сверить Q43/Q44 и production MCQ Q42. Spring Batch Q6/Q28/Q38 остаётся отдельной очередью независимой MCQ-приёмки.

**Решение 05.11.2026 — TLS Q42 · авторская точечная `О`.** По [OpenSSL 3.5](https://docs.openssl.org/3.5/man1/openssl-s_client/), [Oracle JDK 25](https://docs.oracle.com/en/java/javase/25/docs/specs/man/keytool.html) и [testssl.sh 3.2.4](https://raw.githubusercontent.com/testssl/testssl.sh/v3.2.4/doc/testssl.1.md) исправлены ответ и production MCQ. `s_client -showcerts` не гарантирует доверие, а строгая проверка требует CA, имени и `-verify_return_error`; `-cipher` и `-ciphersuites` относятся к разным версиям TLS. Старый дистрактор ошибочно отрицал сетевую команду `keytool -printcert -sslserver`; теперь `keytool` различает локальное хранилище и просмотр сертификата live-сервера. `testssl.sh --vulnerable` не называется полным аудитом. Инвентарь прежний; структура пары PASS (45 Q/45 блоков, semantic `NOT_RUN`), независимая MCQ-приёмка и whole-file freshness `NOT_RUN`/`TODO`.

**Точная точка следующего авторинга:** TLS Q27/Q28 о KeyStore/TrustStore, JKS/PKCS12 — сверить пароль и роль truststore, дефолт Java 9+, команды `keytool` по Oracle JDK 25 и JEP 229; проверить Q31/Q42/Q44 и парные MCQ Q27/Q28. Spring Batch Q6/Q28/Q38 — отдельная очередь независимой MCQ-приёмки.

**Решение 05.11.2026 — TLS Q27/Q28 · авторская точечная `О`.** По [Oracle JDK 25 KeyStore API](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/KeyStore.html), [JSSE](https://docs.oracle.com/en/java/javase/25/security/java-secure-socket-extension-jsse-reference-guide.html), [keytool](https://docs.oracle.com/en/java/javase/25/docs/specs/man/keytool.html) и [RFC 7292](https://www.rfc-editor.org/rfc/rfc7292.html) исправлены ответы и парные production MCQ. Роль truststore не означает иной файловый формат или отсутствие требований к целостности; `trustStorePassword` в SunJSSE при задании проверяет целостность. PKCS12 — дефолт JDK 9+, но `keystore.type`/`-storetype` его меняют, а совместимость конкретного файла и безопасность зависят от содержимого и защиты. JEP 229 напрямую не открылся; вывод о JDK 9 проверен по `keytool`. Сверены Q31/Q42/Q44; новые Q и файлы не нужны, инвентарь прежний. Автор перечитал A/B/C/D и sections. Структура пары PASS (45 Q/45 блоков, semantic `NOT_RUN`), независимая MCQ-приёмка и whole-file freshness `NOT_RUN`/`TODO`.

**Точная точка следующего авторинга:** TLS Q29/Q30/Q32 — проверить генерацию сертификата и CSR, `SSLContext.init` и HTTPS-настройку Spring Boot по официальным Oracle JDK 25 и Spring Boot; сначала сверить Q27/Q28/Q31/Q33/Q34, затем исправить только подтверждённые пробелы и парные MCQ. Spring Batch Q6/Q28/Q38 — отдельная очередь независимой MCQ-приёмки.

**Решение 05.11.2026 — TLS Q29/Q30/Q32/Q33 · авторская точечная `О`.** По [Oracle JDK 25](https://docs.oracle.com/en/java/javase/25/security/java-secure-socket-extension-jsse-reference-guide.html), [keytool](https://docs.oracle.com/en/java/javase/25/docs/specs/man/keytool.html), [OpenSSL 3.5](https://docs.openssl.org/3.5/man1/openssl-req/) и [Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/reference/features/ssl.html) исправлены ответы и парные MCQ. Главные пробелы: CSR не равен выпуску CA, сырой `SSLSocket` требует включения проверки имени, `server.ssl` не создаёт HTTP-редирект, а reload SSL bundle зависит от opt-in и потребителя. Свежая [статья Baeldung от 30.09.2026](https://www.baeldung.com/spring-tls-setup) не дала отдельного нового вопроса. Новых Q/файлов нет, инвентарь прежний; независимая MCQ-приёмка и whole-file freshness остаются `NOT_RUN`/`TODO`.

**Точная точка следующего авторинга:** TLS Q34/Q35/Q36 — проверить API HTTP-клиентов Spring Boot 3.5, mTLS в TLS 1.2/1.3 и серверную/клиентскую проверку сертификатов; сверить Q30/Q31/Q33/Q37, затем синхронизировать парные MCQ. Spring Batch Q6/Q28/Q38 — отдельная очередь независимой MCQ-приёмки.

**Решение 05.11.2026 — TLS Q34–Q36 · авторская точечная `О`.** По [Spring Boot 3.5 REST clients](https://docs.spring.io/spring-boot/3.5/reference/io/rest-client.html), [Spring Security X.509](https://docs.spring.io/spring-security/reference/6.5/servlet/authentication/x509.html), [RFC 9846](https://datatracker.ietf.org/doc/html/rfc9846) и [Jakarta Servlet 6.1](https://jakarta.ee/specifications/servlet/6.1/) обновлены ответы и парные MCQ. Для клиентов показан штатный SSL bundle; схема mTLS ограничена TLS 1.3, а клиентская аутентификация отделена от прав приложения. `client-auth: need` отклоняет соединение на TLS-handshake, а не отвечает HTTP 401. Свежий [релиз Netty от 02.11.2026](https://netty.io/news/2026/11/02/4-2-20-Final.html) подтвердил уместность проверки привязки аутентификации при resumption. Заголовки и инвентарь прежние, независимая MCQ-приёмка и whole-file freshness `NOT_RUN`/`TODO`.

**Точная точка следующего авторинга:** TLS Q37/Q38 — сверить атаки, downgrade и `TLS_FALLBACK_SCSV` с Q21/Q24/Q35/Q45 и парными MCQ по RFC и первичным advisories. Spring Batch Q6/Q28/Q38 — отдельная очередь независимой MCQ-приёмки.

### 2026-11-05 — TLS Q37/Q38: выполненная авторская правка

Точечно исправлены классификация BEAST/POODLE/Heartbleed/CRIME/BREACH и условия `TLS_FALLBACK_SCSV`/downgrade sentinel в TLS Q37/Q38; оба production MCQ синхронизированы. Источники и границы проверки записаны в `docs/interview-coverage/authoring-checkpoint.md`. Структурные проверки прошли, независимая содержательная MCQ-приёмка и whole-file freshness остаются `NOT_RUN`/`TODO`. Следующий авторский пакет — TLS Q6/Q10 по точному пункту продолжения в checkpoint.

**Решение 05.11.2026 — TLS Q6/Q10 · точечная `О`.** Уточнён полный TLS 1.2 ECDHE-handshake и отделены resumption/RSA key exchange; ECH в Q10 переведён из статуса проекта в опубликованный RFC 9849. Парные MCQ синхронизированы. Подробности источников и проверок — в `docs/interview-coverage/authoring-checkpoint.md`. Следующий пакет — TLS Q4/Q5; независимая MCQ-приёмка Q6/Q10 и whole-file freshness остаются открытыми.

**Решение 05.11.2026 — TLS Q4/Q5 · точечная `О`.** Исправлены объяснение расположения TLS и HTTPS с учётом QUIC/HTTP/3, видимых метаданных и границы TLS-терминации; парные MCQ синхронизированы. Статья Baeldung о Java 26 HTTP/3 уже покрыта Java IO/NIO Q. Следующий авторский пакет — TLS Q1/Q2 по checkpoint; независимая MCQ-приёмка и whole-file freshness остаются открытыми.

**Решение 05.11.2026 — TLS Q1/Q2 · точечная `О`.** Исправлены условность аутентификации сервера, пределы защиты TLS и статус версий: RFC 9846 не запрещает TLS 1.2, а OpenSSL 3.0 EOL не является изменением версии протокола. Парные MCQ синхронизированы. Следующий авторский пакет — TLS Q16/Q17 по checkpoint; независимая MCQ-приёмка и whole-file freshness остаются открытыми.

**Решение 05.11.2026 — TLS Q16/Q17 · точечная `О`.** Исправлены категории DV/OV/EV и роли PKI по CA/B Forum, Mozilla и RFC 3647/5280; парные production MCQ синхронизированы, авторский перечитанный набор A/B/C/D прошёл адресную структурную проверку. Следующий пакет — Q45 и сопоставление OpenSSL HollowByte с эксплуатационным чеклистом. Независимая содержательная MCQ-приёмка и whole-file freshness остаются открытыми; подробный статус — в `docs/interview-coverage/authoring-checkpoint.md`.

**Решение 05.11.2026 — OpenSSL HollowByte · TLS Q45 `DEEPEN`, выполнено · О.** [Первичный анализ OpenSSL](https://openssl-library.org/post/2026-07-21-hollowbyte/) подтвердил отсутствующий эксплуатационный сценарий: обновление библиотеки уменьшает выделение памяти на незавершённый handshake, но не заменяет таймауты и лимиты соединений. Q45 и production MCQ B1 дополнены; отдельный файл или новый Q не нужны. Адресная структура PASS (45 Q/46 блоков), независимая содержательная MCQ-приёмка и whole-file freshness остаются открытыми. Следующий авторский пакет — Java Modules Q14–Q16/Q31–Q33 по точному пункту в `docs/interview-coverage/authoring-checkpoint.md`.

**Уточнение 05.11.2026 — биржа Q3 · точечная `О`.** В [журнале Binance от 09.06.2026](https://github.com/binance/binance-spot-api-docs/blob/master/CHANGELOG.md#2026-06-09) отменено прежнее обещание `serverShutdown` за 10 минут до отключения. Q3 и пояснение production MCQ теперь требуют быстро открыть новое соединение и проверить непрерывность фида без фиксированного запаса времени. Структура пары PASS; эвристический parity-gate сообщает прежние сигналы по длине и распределению ключей, независимая содержательная приёмка `NOT_RUN`. Точный следующий пакет указан в checkpoint.

**Решение 05.11.2026 — Payment System Q23 · авторская полная `П`.** Подписки, dunning и MIT/CIT исправлены по Stripe Billing, Adyen, EBA и статье 14 Регламента 2018/389: удалены фиксированные повторы, выдуманный процент оттока и общее освобождение MIT после первого 3DS. Production MCQ Q23 B0 переписан, B1 добавлен; структура PASS (30 Q/48 блоков), независимая содержательная приёмка `NOT_RUN`. Подробные источники и ограничения проверки — в `docs/interview-coverage/authoring-checkpoint.md`. Следующий пакет — Payment System Q22 о FX; Q21, whole-file freshness и независимая MCQ-приёмка остаются открытыми.

**Решение 05.11.2026 — Payment System Q22 · авторская полная `П`.** Исправлены валютные счета, FX-котировка и проводки, удалены неподтверждённые универсальные ставки, сроки и правило half-even. Production MCQ Q22 B0 переписан, B1/B2 добавлены; структура PASS (30 Q/50 блоков), независимая содержательная приёмка `NOT_RUN`. Следующий пакет — Payment System Q21 AML/KYC; подробная точка продолжения и источники — в `docs/interview-coverage/authoring-checkpoint.md`.

**Решение 05.11.2026 — Payment System Q21 · авторская полная `П`.** KYC/CDD, AML и санкционный screening разделены по роли, юрисдикции и решению; парные production MCQ синхронизированы (30 Q/52 блока, структура PASS). Убраны универсальные требования к каждому платежу и SAR, неподтверждённая сумма штрафа. Следующий пакет — Payment System whole-file freshness с Q1–Q10; конкретные источники и проверка — в `docs/interview-coverage/authoring-checkpoint.md`. Независимая MCQ-приёмка остаётся открытой.

**Решение 05.11.2026 — Payment System Q10 · адресная `О`.** Добавлен сценарий перестановки событий двумя polling-воркерами: Kafka key сам не исправляет порядок, нарушенный до отправки. Парный production MCQ Q10 B1 добавлен; структура PASS (30 Q/53 блока), semantic `NOT_RUN`. Q1–Q9 просмотрены без полной содержательной приёмки. Следующий пакет — открыть и проверить оставшиеся живые утверждения Q1–Q9 по первоисточникам, затем Q11–Q20 и Q21–Q30; точная граница в `docs/interview-coverage/authoring-checkpoint.md`.

**Решение 05.11.2026 — MCP Q11 · `DEEPEN`, выполнено · О.** Пункт продолжения про MCP Q9–Q12 оказался уже выполненным по актуальным Markdown, JSON и freshness-записи. В Q11 оставался узкий сценарный пробел: `resource_link` из tool result может отсутствовать в `resources/list`, а встроенный `resource` уже содержит данные. По [спецификации MCP 2026-07-28](https://modelcontextprotocol.io/specification/2026-07-28/server/tools#tool-result) ответ и production MCQ Q11 B1 дополнены. Отдельный Q и файл не нужны; независимая MCQ-приёмка и whole-file freshness остаются открытыми. Следующий кандидат — Spring AI Q16 о MCP-интеграции после сверки с официальной документацией Spring AI и MCP.

**Решение 05.11.2026 — Spring AI Q16 · `DEEPEN`, выполнено · О.** Исправлено смешение `@Tool` с `@McpTool`, клиентского `ToolCallbackProvider` с серверной публикацией и транспорта `STATELESS` с поддержкой редакции MCP `2026-07-28`. Q16 и production MCQ B0/B1 сверены с [Spring AI 2.0.2 reference](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-overview.html), [анонсом 2.0 GA](https://spring.io/blog/2026/06/12/spring-ai-2-0-0-GA-available-now/), [анонсом 2.1.0-M1](https://spring.io/blog/2026/09/25/spring-ai-2-1-0-M1-available-now/) и [Java SDK 2.2.0-M1](https://github.com/modelcontextprotocol/java-sdk/releases/tag/v2.2.0-M1). Нового Q/файла нет; структура пары PASS (18 Q/22 блока), независимая MCQ-приёмка и whole-file freshness открыты. Следующий пакет — Spring AI Q2/Q5 о 2.x starters, конфигурации и PgVectorStore после сверки с Q1/Q6.

**Решение 05.11.2026 — Spring AI Q1/Q2/Q5 · `DEEPEN`, выполнено · О.** Обещание переноса между провайдерами без проверки убрано; Q2 переведён на `spring-ai-starter-model-openai` и свойства 2.0.2 без `.options`; Q5 заменяет устаревший конструктор PGvector на starter или builder, фиксирует opt-in для создания схемы и отделяет `add()` от `upsert()` с готовым вектором в предварительной 2.1. По [OpenAI Chat](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html), [PGvector](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html), [анонсу 2.1.0-M1](https://spring.io/blog/2026/09/25/spring-ai-2-1-0-M1-available-now/) и [публикации о `upsert`](https://spring.io/blog/2026/10/28/spring-ai-upsert-own-embeddings/) обновлены production MCQ Q1/Q2 B0 и Q5 B0/B1. Структура PASS (18 Q/23 блока), независимая MCQ-приёмка и whole-file freshness остаются открытыми. Следующий пакет — Q4/Q6 о EmbeddingModel и RAG с проверкой Q5/Q9.

**Решение 05.11.2026 — Python typing Q5 · `ADD_Q`, выполнено · О.** Продолжение по Q4 подтвердило, что `TypedDict` / PEP 728 уже раскрыты и отдельная правка повторила бы ответ. По [PEP 747](https://peps.python.org/pep-0747/), [Python 3.15 typing](https://docs.python.org/3.15/library/typing.html#typing.TypeForm) и [официальному анонсу 3.15](https://blog.python.org/2026/10/python-3150-final-is-here/) выявлен отдельный пробел: API парсера принимает составные выражения типа, которые `type[T]` не описывает. В `python-typing-interview.md` добавлен Q5 о `TypeForm[T]` и границе runtime-валидации; production MCQ Q5 B0 синхронизирован. Структура PASS (5 Q/6 блоков), независимая содержательная MCQ-приёмка `NOT_RUN`; инвентарь файлов не меняется. Следующий авторский пакет — Spring AI Q18 о паттернах агентов после сверки с Q7/Q15; точное задание — в checkpoint.

**Решение 05.11.2026 — Spring AI Q18 `COVERED`, Q7 `DEEPEN` · О.** Пять workflow-паттернов и граница примеров/core API уже раскрыты в Q18 и MCQ B0/B1. По [Spring AI 2.0.2 Tool Search Tool](https://docs.spring.io/spring-ai/reference/api/tools/tool-search-tool.html) и [анонсу Spring AI 2.0 GA](https://spring.io/blog/2026/06/12/spring-ai-2-0-0-GA-available-now/) в Q7 найден прикладной пробел: при большом каталоге `ToolSearchToolCallingAdvisor` раскрывает инструменты по поиску, но не заменяет авторизацию. Ответ и production MCQ Q7 B3 дополнены; структура PASS (18 Q/39 блоков), независимая содержательная приёмка `NOT_RUN`. Следующий пакет — Spring AI Q12 после сопоставления Q5/Q6; подробности в checkpoint.

**Решение 05.11.2026 — Java Modules Q20, `DEEPEN` выполнено · О.** Проверка по [jlink JDK 25](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jlink.html) подтвердила прежние `--compress=0/1/2` и `--bind-services`; открытым оставался выбор одного нужного провайдера без автоматического связывания остальных. Q20 и production MCQ B1 теперь различают `--suggest-providers` и явный `--add-modules`. Состав файлов и инвентарь прежние; независимая MCQ-приёмка `NOT_RUN`. Точный следующий пакет записан в checkpoint.

**Решение 05.11.2026 — MySQL Q4, `DEEPEN` выполнено · О.** Q2/Q3 уже объясняют кластерный индекс и чтение плана; в Q4 не хватало влияния индексного диапазона на вставки при `SELECT ... FOR UPDATE`. По [MySQL 8.4 InnoDB Locking](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking.html), [Locks Set](https://dev.mysql.com/doc/refman/8.4/en/innodb-locks-set.html) и [Isolation Levels](https://dev.mysql.com/doc/refman/8.4/en/innodb-transaction-isolation-levels.html) добавлены next-key/gap locks, случай полного уникального ключа и граница `READ COMMITTED`; production MCQ Q4 B1 синхронизирован. Нового Q/файла нет, инвентарь прежний; независимая MCQ-приёмка `NOT_RUN`. Следующий пакет — в checkpoint.

**Решение 05.11.2026 — MySQL Q2/Q3 и SQL Q35/Q36, `DEEPEN` выполнено · О.** В MySQL Q2/Q3 добавлены пример покрывающего/непокрывающего вторичного индекса, различие `Using index` и `Using index condition`, а также MVCC-исключение из «только индекс». Соседние SQL Q35/Q36 исправлены: вторичный индекс InnoDB хранит кластерный ключ, PostgreSQL `CLUSTER` не поддерживает порядок после вставок, покрытие лишь допускает index-only план и может сопровождаться `Heap Fetches`. Production MCQ MySQL Q2/Q3 B1 добавлены, SQL Q35/Q36 B0 синхронизированы. Источники — [MySQL 8.4 indexes](https://dev.mysql.com/doc/refman/8.4/en/innodb-index-types.html), [EXPLAIN](https://dev.mysql.com/doc/refman/8.4/en/explain-output.html), [MVCC](https://dev.mysql.com/doc/refman/8.4/en/innodb-multi-versioning.html), [PostgreSQL 18 index-only scans](https://www.postgresql.org/docs/18/indexes-index-only-scans.html) и [CLUSTER](https://www.postgresql.org/docs/18/sql-cluster.html). Структура обеих пар PASS; независимая MCQ-приёмка `NOT_RUN`. Новых Q/файлов нет, инвентарь прежний; следующий пакет — в checkpoint.

**Решение 05.11.2026 — SQL Q37/Q42/Q49, `DEEPEN` выполнено · О.** Из Q37 убраны безусловный вывод из низкой кардинальности и порог 10–15%; ведущий `%` ограничен диапазонным поиском по обычному B-tree, пример индекса по выражению разделён для PostgreSQL 18 и MySQL 8.4. Соседние Q42/Q49 уточняют `Seq Scan`, `Index Only Scan` с `Heap Fetches` и расхождение оценок с фактом. Три production MCQ B0 синхронизированы; новых Q/файлов нет, инвентарь прежний. Источники и фактические проверки — в `docs/interview-coverage/authoring-checkpoint.md`; независимая MCQ-приёмка `NOT_RUN`. Следующий авторский пакет — SQL Q51 по checkpoint.

**Решение 05.11.2026 — SQL Q51, `DEEPEN` выполнено · О.** Ответ теперь различает методы PostgreSQL 18 B-tree/Hash/GIN/GiST/SP-GiST/BRIN и конструкции partial/expression/covering. Убраны гарантии «Hash быстрее B-tree», «prefix LIKE всегда использует B-tree», «Index Only Scan не читает heap», неподтверждённый размер BRIN и неверный пример радиусного поиска. Production MCQ Q51 B0 имел три защитимых ответа; после правки оставлен один верный вариант и три предметных дистрактора. Новых Q/файлов нет, инвентарь прежний; структура пары PASS (53 Q/53 блока), независимая содержательная MCQ-приёмка `NOT_RUN`. Следующий пакет — SQL Q52 по checkpoint.

**Решение 05.11.2026 — SQL Q52, `DEEPEN` выполнено · О.** В ответе разведены `VACUUM`, `ANALYZE`, обычный autovacuum и `VACUUM FULL`; уточнены верхний предел порога autovacuum, автоанализ, возможная обрезка хвоста файла с краткой блокировкой и ранняя защита от XID wraparound. Убраны универсальные «1,5 млрд — опасно» и «корреляция ±1 гарантирует BRIN». Production MCQ Q52 B0 синхронизирован без новых Q/файлов; инвентарь прежний. Структура пары PASS (53 Q/53 блока), независимая содержательная MCQ-приёмка `NOT_RUN`; следующий пакет — SQL Q53 по checkpoint.


**Решение 09.11.2026 — Hibernate Q33, `DEEPEN` выполнено · П.** Исправлены момент проверки `@Version`, пример повтора после `OptimisticLockException`, переносимые типы версии и оговорка о массовом JPQL `UPDATE`. Production MCQ Q33 B0 синхронизирован; структурная проверка PASS (50 Q/50 блоков), независимая содержательная MCQ-приёмка `NOT_RUN`. Новых Q/файлов нет, инвентарь прежний. Источник и точка следующего авторинга — `docs/interview-coverage/authoring-checkpoint.md`.


**Решение 09.11.2026 — Hibernate Q35, `DEEPEN` выполнено · П.** Разведены локальные JDBC-транзакции и JTA, дополнены правила Spring `@Transactional` и уточнён `flush` перед коммитом. Production MCQ Q35 B0 синхронизирован; структура PASS (50 Q/50 блоков), независимая содержательная MCQ-приёмка `NOT_RUN`. Новых Q/файлов нет, инвентарь прежний. Точная следующая точка — Hibernate Q37 по `docs/interview-coverage/authoring-checkpoint.md`.


**Решение 09.11.2026 — Hibernate Q37, `DEEPEN` выполнено · П.** Исправлен пример `ScrollableResults.get()`, уточнены условия курсора pgJDBC и граница влияния `setFetchSize` на память. Production MCQ Q37 B0 синхронизирован; структура PASS (50 Q/50 блоков), независимая содержательная MCQ-приёмка `NOT_RUN`. Новых Q/файлов нет, инвентарь прежний. Источники и точная следующая точка — `docs/interview-coverage/authoring-checkpoint.md`.


**Решение 09.11.2026 — Hibernate Q38, `DEEPEN` выполнено · П.** Разведены неизменяемость управляемой сущности, контракт `merge`, удалённый `Session.update()` и отдельное включение READ_ONLY L2 cache. Production MCQ Q38 B0 синхронизирован; структура PASS (50 Q/50 блоков), независимая содержательная MCQ-приёмка `NOT_RUN`. Новых Q/файлов нет, инвентарь прежний. Точная следующая точка — Hibernate Q39 по `docs/interview-coverage/authoring-checkpoint.md`.


**Решение 09.11.2026 — Hibernate Q39, `DEEPEN` выполнено · П.** Разделены счётчики запросов, подготовленных statement и L2 cache; исправлена формула hit ratio и условия экспорта в Spring Boot. Production MCQ Q39 B0 синхронизирован; структура PASS (50 Q/50 блоков), независимая содержательная MCQ-приёмка `NOT_RUN`. Новых Q/файлов нет, инвентарь прежний. Точная следующая точка — Hibernate Q40 по `docs/interview-coverage/authoring-checkpoint.md`.


**Решение 09.11.2026 — Hibernate Q40, `DEEPEN` выполнено · П.** Уточнены варианты `ddl-auto`, default Spring Boot, граница Open EntityManager in View и различие batch fetching с JDBC batching. Production MCQ Q40 B0 синхронизирован; структура PASS (50 Q/50 блоков), независимая содержательная MCQ-приёмка `NOT_RUN`. Новых Q/файлов нет, инвентарь прежний. Точная следующая точка — Hibernate Q41 по `docs/interview-coverage/authoring-checkpoint.md`.
