# План покрытия interview-шпаргалок

Состояние репозитория сверено **2026-10-05**. Этот документ ведёт очередь тем и
фактический прогресс авторинга. При написании вопросов синхронизировать
шпаргалки и парные MCQ. [План MCQ](PLAN_INTERVIEW.md) ведёт отдельную независимую
приёмку; автоматический PASS не даёт статус `FINAL`.

## Основание и предел проверки

- Текущий корпус: **329** тематических Markdown-файлов, **9 883** заголовка
  Q в **25** разделах. [Инвентарь](docs/interview-coverage/inventory.tsv)
  обновлён после десяти новых шпаргалок и добавленных Q. **328** файлов имеют парный production
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
| Data engineering | 8 | 236 | Databases | 23 | 736 |
| Design patterns | 1 | 48 | DevOps | 13 | 448 |
| Frameworks | 37 | 885 | JVM | 3 | 71 |
| Leadership | 7 | 187 | Logging | 1 | 42 |
| Messaging | 9 | 237 | Monitoring | 9 | 285 |
| Performance | 8 | 253 | Preparation | 1 | 42 |
| Programming languages | 54 | 1 637 | Reactive | 6 | 206 |
| Security | 10 | 350 | System design | 22 | 644 |
| Testing | 16 | 489 | **Итого** | **325** | **9 865** |

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
| P2 / КАНДИДАТ | 22.07 [MySQL: Incorrect string value](https://www.baeldung.com/java-mysql-sqlexception-incorrect-string-value), 28.05 [Communications link failure](https://www.baeldung.com/jdbc-connector-j-mysql-communications-link-failure); [MySQL charset](https://dev.mysql.com/doc/refman/8.4/en/charset-connection.html), [Connector/J troubleshooting](https://dev.mysql.com/doc/connector-j/en/connector-j-usagenotes-troubleshooting.html) | Будущая MySQL-тема; `databases/sql-interview.md` Q45 и кандидат JDBC/пулы | При решении о `NEW_TOPIC` использовать два самостоятельных диагностических сценария: charset/collation/`utf8mb4` для ошибок записи и разрыв соединения после простоя (таймауты, сеть, устаревшее соединение в пуле). Разнести ответственность MySQL и JDBC, не добавлять дублирующий общий SQL-вопрос. |
| P2 / `DEEPEN`, выполнено · О | 23.06 [Spring AI 2.0: structured output](https://spring.io/blog/2026/06/23/spring-ai-self-correcting-structured-output/), 25.09 [Baeldung](https://www.baeldung.com/spring-ai-structured-output); [Spring AI 2.0.1 reference](https://docs.spring.io/spring-ai/reference/api/structured-output.html) | `frameworks/spring/spring-ai-interview.md` Q3: `.entity()` ошибочно описывался как строгое JSON-поведение, пример конкатенировал код. | Q3 углублён: best effort, provider-native constraint, schema validation/retry и цена вызовов, предел `.stream()`, риск prompt injection. MCQ B0 обновлён, B1 добавлен; structure/parity PASS, blind review ожидается. |
| P2 / `DEEPEN`, выполнено · О | 15.06 [Spring AI 2.0: tool calling](https://spring.io/blog/2026/06/15/spring-ai-composable-tool-calling/); [Spring AI 2.0.1 reference](https://docs.spring.io/spring-ai/reference/api/tools.html) | `frameworks/spring/spring-ai-interview.md` Q7/Q15: пример создавал Spring-бин через `new` без зависимости, текст советовал старый `.toolNames(...)`, агент использовал необъявленное поле. | Q7/Q15 исправлены: `ToolCallingAdvisor`/`ToolCallingManager`, явный `ToolCallback` для Function, внедрение бинов, memory order и контроль side effects. MCQ Q7 B0/B1 и Q15 B0 синхронизированы; structure/parity PASS, blind review ожидается. |
| P3 / КАНДИДАТ | 27.06 [Bean Background Initialization](https://www.baeldung.com/spring-bean-background-init); [Spring Framework reference](https://docs.spring.io/spring-framework/reference/core/beans/java/composing-configuration-classes.html) | `frameworks/spring/spring-framework-interview.md` Q10/Q38 | `DEEPEN` Q10 или `ADD_Q`: когда фоновая инициализация бина сокращает критический путь старта, как задаётся исполнитель и какие зависимости всё равно блокируют startup. Функция появилась в Spring Framework 6.2: новизна статьи не равна новизне API. |
| P3 / `DEEPEN`, выполнено | 27.06 [Kafka CommitFailedException](https://www.baeldung.com/java-kafka-commitfailedexception); [KafkaConsumer 4.3.1](https://kafka.apache.org/43/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html) | `messaging/kafka-interview.md` Q15/Q16/Q45 | Q16 дополнен: `CommitFailedException` после потери партиции нельзя слепо повторять; auto-commit может дать at-least-once при полной обработке batch до следующего `poll()`. `О` по Q15/Q16/Q45; Q16/MCQ B1 написаны, structure/parity PASS, blind review ожидается. |
| P3 / КАНДИДАТ | 21.06 [Spring Boot 4.1 и MongoDB JobRepository](https://spring.io/blog/2026/06/21/spring-boot-41-and-spring-batch/) | `frameworks/spring/spring-batch-interview.md` Q6/Q28/Q38 | `ADD_Q` лишь если подтверждена ценность сценария: где хранить metadata `JobRepository`, ограничения MongoDB и транзакций, отличие хранилища состояния job от источника/назначения данных. Spring Boot 4.1 уже выпущен; проверить версию Spring Batch и ограничения хранилища. |
| P3 / КАНДИДАТ | 02.10 [Python 3.15.0rc3](https://www.python.org/downloads/release/python-3150rc3/), [черновик What's New](https://docs.python.org/3.15/whatsnew/3.15.html) | Будущий `programming-languages/python/python-interview.md` | После базовой Python-темы рассмотреть **один версионный блок**, если изменения полезны backend-разработчику. На 03.10 это RC; финальный релиз перенесён на 09.10.2026. Перед написанием снова проверить GA и окончательную документацию. |
| P3 / КАНДИДАТ, **БЕТА** | 24.09 [PostgreSQL 19 beta 4](https://www.postgresql.org/about/news/postgresql-19-beta-4-released-3386/), [черновик release notes](https://www.postgresql.org/docs/19/release-19.html) | `databases/postgresql-interview.md` Q32/Q44–Q46 | После GA проверить `DEEPEN` Q32 о встроенном `REPACK` против `VACUUM FULL`/`pg_repack` и возможный Q о `WAIT` для read-your-writes на standby. Beta 4 уже отменила несколько функций: до релиза не описывать предложения как гарантированную функциональность PostgreSQL 19. |

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
| P2 · КАНДИДАТ `NEW_TOPIC` или `DEEPEN` | 10. Notification System → `system-design/design-notification-system-interview.md` | `system-design-interview.md` Q10 уже даёт архитектуру каналов, preferences, очередей и retry. Новый файл оправдан, только если отделить несколько новых сценариев доставки; иначе углубить Q10. | Как обеспечить идемпотентную доставку через разные провайдеры? Как хранить предпочтения, тихие часы и статус отправки? Как разделить transactional и массовые уведомления? |
| P2 · КАНДИДАТ `NEW_TOPIC` | 19. Distributed Message Queue → `system-design/design-message-queue-interview.md` | `messaging/kafka-interview.md` Q6–Q24 и `rabbitmq-interview.md` описывают продукты; `system-design-interview.md` Q21 — выбор очереди. Здесь проектирование самой очереди/лога, не пересказ Kafka. | Как распределить partitions и consumers? Где хранить offset/ack и как совместить ordering, replay, retention и отказ брокера? |
| P2 · КАНДИДАТ `NEW_TOPIC` | 22. Hotel Reservation System → `system-design/design-reservation-system-interview.md` | `design-payment-system-interview.md` Q4/Q7/Q17 покрывает платёж, но не конкурентное бронирование ограниченного инвентаря. Не копировать платёжный ledger. | Как избежать overselling при одновременной броне? Чем отличаются hold с TTL, подтверждённая бронь и отмена? Как согласовать резерв с оплатой? |
| P2 · КАНДИДАТ `NEW_TOPIC` | 24. S3-like Object Storage → `system-design/design-object-storage-interview.md` | `design-dropbox-interview.md` Q5/Q18 описывает CAS и синхронизацию файлов; `data-lake-lakehouse-interview.md` Q5 сравнивает object storage с HDFS. Здесь API и внутренняя организация самого хранилища. | Как разделить metadata и bytes? Как обеспечить загрузку больших объектов, восстановление после потери узла, проверку целостности и ожидаемую семантику чтения/перезаписи? |
| P2 · КАНДИДАТ `NEW_TOPIC` | 25. Real-time Gaming Leaderboard → `system-design/design-leaderboard-interview.md` | Есть Redis/кеширование в соседних темах, но нет маршрута по rank/top-N при большом числе обновлений. | Как быстро получать top-N и место конкретного пользователя? Как обрабатывать равные очки, шардирование, сезонный reset и запоздавшие события? |
| P3 · КАНДИДАТ `NEW_TOPIC` или `DEEPEN` | 20. Metrics Monitoring and Alerting System → `system-design/design-metrics-platform-interview.md` | `system-design-interview.md` Q23 и `monitoring/prometheus-grafana-interview.md` Q3/Q19–Q24 покрывают общую observability и Prometheus. Отдельный файл — только для проектирования многопользовательской платформы метрик. | Где агрегировать и хранить высококардинальные ряды? Как масштабировать ingest/query и изолировать правила алертинга разных команд? |
| P3 · КАНДИДАТ `NEW_TOPIC` | 21. Ad Click Event Aggregation → `system-design/design-event-aggregation-interview.md` | `data-engineering/stream-processing-interview.md` Q8 объясняет окна, но нет сквозного кейса по событиям рекламы; общий stream processing оставить соседним. | Как различать event time и processing time, удалять дубли и пересчитывать поздние события? Как сверять оперативные счётчики с итоговой отчётностью? |
| P3 · КАНДИДАТ `NEW_TOPIC` | 23. Distributed Email Service → `system-design/design-email-service-interview.md` | `system-design-interview.md` Q10 рассматривает email как один канал уведомлений; здесь отдельные доставка, bounce и репутация отправителя. | Как построить очередь отправки с повторами и дедупликацией? Что делать с bounce/complaint и как отслеживать доставляемость? |
| P3 · КАНДИДАТ `NEW_TOPIC` | 27. Digital Wallet → `system-design/design-digital-wallet-interview.md` | `design-payment-system-interview.md` Q5/Q8/Q22 покрывает ledger, saga и упоминание кошелька; новый файл должен быть про переводы между балансами и инварианты, не про PSP/карточный платёж. | Как атомарно перевести средства между кошельками при повторах запроса? Как совместить доступный баланс, неизменяемый журнал и сверку? |
| P3 · КАНДИДАТ `NEW_TOPIC` | 28. Stock Exchange → `system-design/design-exchange-interview.md` | Финансовый домен уже есть в платёжной системе, но order book и matching engine — отдельная задача. Подтвердить интерес к узкой предметной области до написания. | Как упорядочивать команды и обеспечивать детерминированное сопоставление заявок? Как разделить matching, рыночные данные и отчётность? |
| P3 · КАНДИДАТ `DEEPEN`/`PROMOTE` | 16–17. Proximity Service / Nearby Friends | `design-uber-interview.md` Q7–Q14 и `design-google-maps-interview.md` Q19/Q27 уже покрывают geo index, POI и sharing location. Сначала проверить глубину соседних ответов; два новых файла сейчас не обоснованы. | Как индексировать обновляющиеся координаты и фильтровать радиус? Как сделать видимость «друзья рядом» с ограничением доступа к местоположению и высокой степенью вершины графа друзей? |

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
| P3 | КАНДИДАТ | `programming-languages/java/java-completable-future-interview.md`, Q11 о timeout и Q13 об ограничениях | Что именно отменяет `cancel(true)` и как останавливать **исходную работу**, когда результат `CompletableFuture` больше не нужен? Таймаут в Q11 лишь завершает future, не прерывая задачу. | `ADD_Q` или `DEEPEN` Q11 |
| P3 | КАНДИДАТ | `databases/database-transactions-interview.md`, Q6–Q12 об изоляции/MVCC; `postgresql-interview.md` объясняет `SQLSTATE 40001` внутри ответа | Показать **write skew** на двух транзакциях и объяснить, когда нужен retry **всей** транзакции при serialization failure. | `ADD_Q`, с перекрёстной ссылкой на PostgreSQL |
| P3 | КАНДИДАТ | `devops/kubernetes-interview.md`, Q14/Q39/Q42 об обновлении, HA и graceful shutdown; PDB упомянут в Q39 | Как `PodDisruptionBudget` и eviction влияют на добровольное вытеснение Pod при `drain`, и чего PDB не гарантирует? | `PROMOTE` или `DEEPEN` Q39 |
| P3 | КАНДИДАТ | `devops/kubernetes-interview.md`, Q22 об Ingress; Gateway API упомянут в ответе | В каких случаях выбрать Gateway API вместо Ingress, как распределены роли `GatewayClass`, `Gateway`, `HTTPRoute`, какие ограничения у контроллера? | `PROMOTE`, если хватает ценности отдельного Q |
| P3 | КАНДИДАТ | `frameworks/spring/spring-rest-client-interview.md`, Q5/Q12 об ошибках и таймаутах; общая устойчивость есть в `resilience4j-interview.md` | Как спроектировать исходящий HTTP-вызов: общий deadline, повтор только безопасной операции, connection pool, метрики и trace context? Указать границы ответственности клиента и Resilience4j, не копировать весь материал. | `ADD_Q` с точными ссылками или `DEEPEN` Q12 |
| P3 | КАНДИДАТ | `security/authentication-authorization-patterns-interview.md`, Q24 о passwordless; `spring-security-interview.md` Q44 о настройке passkeys | Как работают registration/authentication ceremonies WebAuthn, проверка origin/RP ID и восстановление доступа? Spring-вопрос не заменяет протокольный. | `ADD_Q` в общей теме, ссылка на Spring Q44 |
| P2 | ПРОВЕРЕНО · З | `databases/sql-interview.md` Q45 сравнивает JDBC и JPA; `database-transactions-interview.md` Q33–Q34 касаются пула; отдельного interview-файла нет | **JDBC и пулы**: `DataSource`, `PreparedStatement`, `ResultSet`, auto-commit, batch/streaming, жизненный цикл и диагностика. Граница с MySQL и Spring Data JDBC записана в пакете 05.10. | `NEW_TOPIC`: `databases/jdbc-connection-pool-interview.md` |
| P4 | КАНДИДАТ | `monitoring/opentelemetry-interview.md`, Q14 о trace context; `metrics-tracing-interview.md` Q37 содержит Baggage в ответе | Когда Baggage нужен, как ограничивать размер и не передавать чувствительные данные через заголовки? | `DEEPEN` существующий Q или `PROMOTE`; избегать третьего общего объяснения context propagation |
| P4 | КАНДИДАТ | `frameworks/spring/spring-testing-interview.md`, Q9/Q10 названы `@MockBean`/`@SpyBean`; замена `@MockitoBean` уже отмечена в Q9 | Нужен ли отдельный практический вопрос о миграции тестов Boot 3.4/4 и override-бинах, или достаточно актуализировать Q9/Q10 и примеры? | Скорее `DEEPEN`; не добавлять Q только ради нового имени |
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

1. **Точная точка продолжения:** проверить P2-кандидат notification system
   из блока `system-design-notes`: прочитать целиком
   `system-design-interview.md` Q10, заголовки и ответы ближайших
   messaging/security-файлов, затем выбрать первичные источники по
   идемпотентности доставки, предпочтениям и статусу отправки. Создать
   отдельную тему только при нескольких новых независимых сценариях;
   иначе углубить Q10 и синхронизировать его MCQ.
2. Затем проверить P2 distributed message queue,
   reservation, object storage и leaderboard в порядке таблицы ниже.
   Java 26–27, HTTP/3, Jackson 3 и Python typing/packaging уже написаны.
   Остальные P2/P3 без решения остаются кандидатами. Статусы Python 3.15 и
   PostgreSQL 19 перепроверять после финальных релизов.
3. Отдельной задачей пройти независимую blind-приёмку новых MCQ и старый
   parity/pre-check backlog Hibernate, Spring Data JPA, Java IO/NIO, Jackson
   и URL Shortener;
   положительный валидатор не означает `FINAL`.
4. Продолжать выборку официальных публикаций за скользящие шесть месяцев;
   не выдавать выборку за полный охват. Для каждого решения фиксировать
   источник, ближайший файл/Q, пробел, приоритет и уровень `З`/`О`/`П`.
   Обновлять план и парный MCQ после каждой небольшой группы; `FINAL` MCQ
   только после независимого evidence по [правилам качества](docs/mcq-quality/workflow.md).
5. По новому запросу пользователя продолжать
   [блок `system-design-notes`](#новый-источник-для-system-design-system-design-notes):
   первый P2-кандидат (генератор ID) написан; остальные строки пока
   сопоставлены по структуре и Q (`З`). Проверять соседние ответы и отсекать
   дубли перед новым файлом; при авторинге добавлять парный MCQ.

[Самодостаточный промпт продолжения](PROMPT_PLAN_CHEATSHEETS.md) описывает
порядок работы без истории чата.
