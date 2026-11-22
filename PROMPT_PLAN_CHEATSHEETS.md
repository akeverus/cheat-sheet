# Продолжение авторинга interview-шпаргалок

Этот промпт самодостаточен для следующего этапа в локальном репозитории
/Users/sergeyvoronin/IdeaProjects/cheat-sheet. История чата служит контекстом
решений, но актуальные факты проверяй по файлам. Пользователь разрешил
изменять и создавать шпаргалки и парные production MCQ JSON. Не останавливайся
на обновлении плана. Последнее поручение: после завершения работы закоммитить
все завершённые изменения репозитория отдельными тематическими коммитами,
предварительно разобрав их состав и проверки. Не включай в эти коммиты
параллельные незавершённые AI/ML и MCQ-audit файлы другого исполнителя.
Push не запрошен.

## Что прочитать перед началом

1. PLAN_CHEATSHEETS.md, docs/interview-coverage/inventory.tsv,
   PLAN_INTERVIEW.md, правила репозитория и применимые AGENTS.md/RTK.md.
2. .claude/skills/interview-writer/SKILL.md и его рекомендации для авторинга.
3. .codex/skills/mcq-quality-fixer/SKILL.md,
   docs/mcq-quality-standard.md и docs/mcq-quality/workflow.md для MCQ.
4. Указанные в плане ближайшие файлы и Q, включая тексты ответов, прежде
   чем решать о новом файле или изменении существующего.

При недоступности локальной папки попроси пользователя подключить её или
приложить план, инвентарь, правила и нужные шпаргалки.

## Текущий проверенный срез на 07.10.2026

В каталоге 337 тематических Markdown-файлов и 9 923 заголовка Q в 25
разделах; 336 файлов имеют парный production MCQ JSON. Восемнадцать новых тем
в текущем этапе: Python core, Python concurrency, Python testing, FastAPI,
MySQL, JDBC/пулы, Java 26–27, Python typing, Python packaging и
System Design Unique ID Generator, Notification System, Hotel Reservation System,
Object Storage, Gaming Leaderboard, Metrics Platform, Ad Click Event Aggregation
и Distributed Email Service, Digital Wallet.
Навигация и инвентарь обновлены. Go database/sql углублён в
go-stdlib-interview.md Q22–Q25: отдельный Go DB файл отвергнут из-за
пересечения. Исправлены Go generics Q10/Q15/Q18–Q20, Spring Security Q30,
OWASP SSRF Q34, Application Security Q13. Сверяй этот срез с текущим деревом
перед правкой: параллельно в репозитории есть другие изменения.

Уровни проверки в плане:
- З: каталог, оглавление и заголовки Q.
- О: точечно прочитан и проверен ответ по первичному источнику.
- П: все ответы существующего файла прошли содержательную проверку.

Ни один старый файл в этом этапе не получил П. Дополнительно исправлены
`ai-ml/mcp-interview.md` Q6/Q8/Q13/Q14/Q28–Q30 и парные MCQ по ревизии
MCP 2026-07-28; структура JSON PASS, parity старого файла FAIL, независимая
приёмка не выполнена. Spring Data JDBC Q17 и JSON добавлены по reference
4.1.1: назначенный ID, поддержка диалекта, без optimistic locking.
Spring AI Q14 дополнен prompt caching и MCQ B1:
структура/parity PASS, blind review ожидается. Новые Q авторски сверены с
официальной документацией. Их MCQ имеют структурную и, кроме старых блоков
двух Go-файлов, детерминированную parity-проверку. Ни один MCQ этого этапа
не получил независимый blind review или статус FINAL. Не объявляй пакет
готовым по одному валидатору. Не меняй чужие MCQ audit-state/evidence без
отдельной необходимости и их процесса.

Следующая группа уже выполнена без новых файлов и номеров Q: RabbitMQ
Q19/Q24/Q25/Q30 (delivery-limit, DLX, TTL, версия 4.3), Kafka Q16
(`CommitFailedException` и условная гарантия auto-commit), Hibernate
JPQL/Criteria Q6 (`@Find`, Hibernate Processor), Spring Data JPA Q4
(AOT repositories). Соседние заголовки и
ответы Q проверены точечно (`О`), весь файл (`П`) не проверялся. У RabbitMQ
41 Q / 43 MCQ-блока, у Kafka 50 Q / 51 блок; structure/parity PASS.
У Hibernate 16 Q / 17 блоков, structure PASS, parity всего старого файла
FAIL из-за прежних вариантов; новый Q6 B1 не имеет независимой приёмки.
У Spring Data JPA 43 Q / 44 блока, structure PASS, parity всего старого файла
FAIL; два блока Q4 локально проверены, blind review ожидается. Источники и
причины решений зафиксированы в PLAN_CHEATSHEETS.md. Инвентарь
обновлён после добавления Actuator Q44: отдельный вопрос о
`ApplicationStartup` и `/actuator/startup` с двумя MCQ-блоками,
structure/parity PASS, blind review ожидается. Spring AI Q3/Q7/Q15
точечно сверены с reference 2.0.1 (`О`): исправлены structured output,
tool calling и примеры бинов; MCQ Q3/Q7 получили B1, Q15 B0 обновлён.
18 Q / 21 блок, structure/parity PASS, независимая приёмка ожидается.
Другие Q файла не проходили полной содержательной проверки (`П`).
Полный MCQ pre-check обоих файлов остаётся FAIL на прежних блоках:
Actuator Q15/Q18/Q40 и Spring AI Q1/Q6/Q8–Q10/Q14/Q16/Q17;
локально изменённые Q44 и Q3/Q7/Q15 не дают жёстких сигналов.
После этого в Go stdlib добавлен Q31 о `encoding/json/v2` и `jsontext`;
Q10 исправлен по Go spec: метод с value receiver входит в method set
указателя, но изменение его полей затрагивает копию. MCQ Q10 B0 и Q31 B0/B1
синхронизированы. Go stdlib: 31 Q / 32 блока, structure PASS, parity всего
старого файла FAIL; независимая приёмка и локальный запуск Go 1.27 отсутствуют.
Инвентарь обновлён ещё на один Q. Остальные Q файла не получили `П`.
Go concurrency Q32 углублён по Go 1.27 `goroutineleak` и соседнему
`go-memory-gc` Q15: диагностика on-demand, пределы обнаружения, MCQ B0/B1.
35 Q / 36 блоков, structure PASS, parity всего старого файла FAIL;
локальные Q32 B0/B1 без жёстких сигналов, blind review не было.
Go 1.27 в локальном окружении отсутствует.
В рабочем дереве параллельно появились чужие изменения `agentic-patterns`
и MCQ runs; сохрани их отдельно.

Группа Java/JDK выполнена тематическими коммитами `99970c22`, `95c046ca`,
`02c4493d`: новый `java-26-27-interview.md` Q1–Q4; `java-io-nio-interview.md`
Q41 о HTTP/3; `java-jackson-interview.md` Q32 о Jackson 3 и исправления Q24/Q28.
Новые/затронутые MCQ синхронизированы. Java 26–27: структура и pre-check PASS.
Java IO/NIO и Jackson: структура PASS, parity всего старого файла FAIL;
локально изменённые Q проверены автором, blind review нет. На машине JDK 22,
поэтому примеры 26/27 не запускались. Источники и точные версии — в плане.

Группа Python typing/packaging выполнена коммитом `12411235`: две новые пары
по четыре Q и MCQ. Сверены с Python typing spec, PyPA packaging specs и pip
Repeatable Installs. Обе пары прошли структуру и полный pre-check; код Python
исполнен на 3.14.4, TOML разобран `tomllib`. Независимой blind-приёмки MCQ
нет. Ни один старый Python-файл не проходил полного аудита `П`.

Первый кандидат `system-design-notes` выполнен коммитом `97c1a764`:
`design-unique-id-generator-interview.md` Q1–Q4 и парный MCQ. Отдельный
маршрут оправдан после сравнения с `system-design-interview.md` Q7,
`design-url-shortener-interview.md` Q19 и SQL Q13: область уникальности,
sequence/ticket/ranges, сбои Snowflake и выбор UUIDv4/v7. Официальные и
первичные источники — RFC 9562, PostgreSQL 18, Flickr и Twitter/X Engineering.
Соседний URL Shortener Q19 исправлен: математический предел одного worker
не является throughput системы, ID не строго упорядочены между узлами;
MCQ Q19 синхронизирован. Новая пара structure/pre-check PASS. Старый URL
Shortener structure/parity PASS, но полный pre-check FAIL на старых блоках;
Q19 локально без жёсткого сигнала, blind review нет. Полного аудита `П`
старого файла не было. Параллельные AI/ML и MCQ-run правки не трогать.

Следующий P2-кандидат выполнен: `design-notification-system-interview.md`
Q1–Q4 и парный MCQ. После сравнения с `system-design-interview.md` Q10
и брокерным Q24 выделены новые сценарии: стадии доставки, неопределённый
ответ провайдера и дубли, повторная проверка preferences/TTL, постоянные и
временные ошибки. Факты сверены с FCM, APNs, SES и AWS outbox на 05.10.2026.
Новая пара structure/parity/pre-check PASS, независимого blind review нет;
старый Q10 проверен точечно, полного аудита файла не было.

P2-кандидат Distributed Message Queue не стал новым файлом: Kafka Q6–Q24,
RabbitMQ Q18–Q30 и system-design Q21 уже покрывают основные механизмы.
Вместо дублирования исправлены RabbitMQ Q40/Q41, Kafka Q42, сравнение
брокеров Q15, event-driven Q3 и добавлен RabbitMQ Q42 о Streams; MCQ
синхронизированы. RabbitMQ 4.3 Streams — сохраняемый лог с replay по offset,
в отличие от classic/quorum queue после ack. Источники — официальные
RabbitMQ Streams/compare Kafka и Kafka topic configs. Структура всех четырёх
JSON PASS; parity RabbitMQ/Kafka/сравнения PASS, event-driven FAIL на старых
блоках. Полный pre-check каждого старого файла FAIL, изменённые Q локально
без hard-сигналов, blind review нет. `CHECKED` sidecar сравнения брокеров
сброшен в `TODO` после Q15. Файлов столько же, вопросов на один больше.

## Точная точка продолжения

Начни с **независимой содержательной MCQ-приёмки** последней группы:
`frameworks/spring/spring-batch-interview.md` Q6 B0/B1, Q28 B0,
Q38 B0 и парный production JSON. Сверь каждый A/B/C/D с теорией и
официальными Batch 5.0/5.2/6 и Boot 3.5/4.1 docs, затем отдельно
проверь SFPL/ANSC и русский текст по фазам A/B `docs/mcq-quality/workflow.md`.
Авторские structure/parity/pre-check PASS не заменяют blind review.
После Batch провести независимую приёмку Uber Q6/Q7, Google Maps Q27
и Spring Framework Q10 B0/B1: авторская точечная проверка уже проведена,
Q10 B0 исправлен по конструкторной DI. Старые hard-сигналы Spring
Batch Q2/Q26/Q27/Q39/Q42 уже
исправлены отдельным пакетом; они также ждут независимой проверки.
Python 3.15 GA подтверждён 09.10.2026, Core Q3/Q5/Q10–Q12, Typing Q4,
Concurrency Q9 и Packaging Q5 написаны; следующий авторский кандидат —
Python-доступ к БД.
PostgreSQL 19 пока RC, перепроверять после GA. Stock Exchange остаётся
кандидатом до подтверждения ценности.
MongoDB `JobRepository` уже разобран в Batch Q6/Q28/Q38 без нового Q:
различены resourceless/JDBC/MongoDB, транзакции metadata и данные шага.
JSON Q2/Q6 B0/B1/Q26/Q27/Q28/Q38/Q39/Q42 синхронизирован;
43 Q/44 блока, structure/parity PASS, полный pre-check hard=0 с 17
предупреждениями. Q6/Q38 повторно сверены с финальными Batch 5.0/5.2/6
источниками; Q39 различает число разделов и фактический параллелизм,
Q42 отмечает новый тестовый API Batch 6. Это авторская точечная проверка,
полный содержательный обзор файла и blind review ожидаются.
Proximity Service/Nearby Friends уже разобраны без новых файлов:
`design-uber-interview.md` Q6/Q7 исправлены по PostGIS и Redis,
`design-google-maps-interview.md` Q27 углублён по GEOSEARCH, проверке
прав доступа при каждом чтении и отзыву. Официальные PostGIS, Redis и
OWASP сверены; пары JSON Q6/Q7/Q27 синхронизированы, structure/parity/
pre-check PASS, независимый blind review ожидается. Spring Framework
Q10 углублён по `@Bean(bootstrap=BACKGROUND)` с 6.2; 07.10 схема и MCQ
B0 A уточнены по конструкторной DI, B1 добавлен. Q1/Q2 разграничивают
модули Framework и проекты Boot/Security/Data, Q9 MCQ выровнен по форме.
Structure/parity PASS, полный pre-check hard=0 с 23 предупреждениями;
blind review Q1/Q2/Q9/Q10 B0/B1 ожидается.
Spring Testing Q9/Q10 уже углублены по `@MockitoBean`/`@MockitoSpyBean`,
парный JSON обновлён; структура/parity PASS, полный pre-check старого
файла FAIL на прежнем Q16, blind review ожидается. Отдельный Q не нужен.
Metrics & Tracing Q40 уже углублён по Baggage, исправлен пример с
неактивным Context и передачей `userId`. Парный JSON Q40 изменён,
структура PASS; общий parity/pre-check старого файла FAIL, независимая
приёмка ожидается. Отдельный Q не потребовался. Прежняя ссылка плана
на Q37 была ошибочной: Baggage находится в Q40, Q37 — tail sampling.
WebAuthn Q46 уже добавлен в общую тему; Q24 и Spring Security Q44
исправлены по W3C/FIDO/Spring Security, включая различие synced и
device-bound passkeys, отдельный модуль и CVE-2026-47841.
Парные JSON синхронизированы, структура/parity PASS. Полный pre-check
общей темы PASS, Spring Security FAIL на прежних блоках; изменённый Q44
без hard-сигнала. Независимая MCQ-приёмка ожидается, freshness общей
темы сброшен в TODO.
Spring Rest Client Q12 уточнён, Q14 об общем бюджете исходящего HTTP-вызова,
повторах и наблюдаемости добавлен. Структура/parity JSON PASS, полный
pre-check после двух пакетов 07.10 тоже PASS (14 Q/14 блоков, hard=0,
четыре предупреждения); новый Q14 без hard-сигналов. Q4 уточнён по
`ConvertibleClientHttpResponse`: `bodyTo()` доступен в лямбде `exchange()`,
пустое тело требует `Optional.ofNullable()`. Q11 разграничивает смену
адаптера и реактивной сигнатуры, учитывает HTTP Service groups Boot 4.x
с `spring.http.serviceclient.<group-name>.base-url`. Парный JSON Q4/Q11
синхронизирован. Q7 уточнён по неблокирующей композиции и `.block()`;
Q6/Q12/Q13 точечно сверены по Spring Framework 6.2 и Apache HttpClient 5.6,
их варианты и sections выровнены. Авторская точечная проверка A/B/C/D
выполнена для этой группы; независимая MCQ-приёмка ожидается.
Fact-freshness sidecar дополнен источниками, прежняя дата общего прохода
02.09 сохранена; новый полный обзор старых Q не проводился.
Digital Wallet Q1–Q4 уже создан и закоммичен (`3a300d20`), его MCQ
structure/parity/pre-check PASS, независимый blind review ожидается.
Database Transactions Q43 о write skew добавлен в существующую тему:
structure/parity PASS; новый Q43 без hard-сигналов. 07.10 Q15/Q22/Q23
исправлены по официальным Jakarta Persistence 3.2 и Spring Framework
6.0/6.2: момент проверки версии, новая транзакция для retry,
rollback-правила, видимость методов и AspectJ. Парный JSON синхронизирован;
полный structure/parity/pre-check PASS (43 Q/43 блока, hard=0), но это
авторская точечная проверка. Независимая приёмка Q15/Q22/Q23/Q43 ожидается.
Java CompletableFuture Q11 исправлен (Java 8 timeout), Q14 об отмене
добавлен. 07.10 Q2/Q3/Q4/Q6/Q8/Q10 исправлены по Java SE 25
`CompletableFuture`, `CompletionStage` и `ForkJoinPool`: поток исполнения,
`thenCombine`, `thenAccept`/`thenRun`, `allOf` при ошибке, возвращаемый тип
`exceptionally`, `join`/`get`. Q6 C раньше был истинным дистрактором.
Парный JSON синхронизирован, структура/parity/pre-check всего файла PASS
(14 Q/16 блоков, hard=0); авторская проверка A/B/C/D и sections сделана
по изменённым блокам. Полная и независимая приёмка ожидается, включая Q8 B0,
который связан с изменённой теорией.
Kubernetes Q39 о PDB исправлен, Q46 о `drain` добавлен. Структура PASS,
полный parity/pre-check старого файла FAIL на прежних блоках; sidecar
fact-freshness TODO. Q39/Q46 ждут независимой приёмки.
Kubernetes Q22 о Gateway API уточнён, Q47 о ролях добавлен. Структура
PASS, локально без hard-сигналов, общий parity/pre-check старого файла
по-прежнему FAIL; Q22/Q47 ждут независимой приёмки.
Ad Click Event Aggregation и Distributed Email Service Q1–Q4 написаны,
их MCQ требуют независимой приёмки.
RabbitMQ/Kafka/Hibernate, новые Java и Python Q требуют независимого blind
review MCQ; старый parity/pre-check backlog Hibernate, Spring Data JPA,
Java IO/NIO, Jackson, URL Shortener, RabbitMQ, Kafka, сравнение брокеров и
event-driven открыт. Ad Click Event Aggregation и Distributed Email Service Q1–Q4, Metrics Platform Q1–Q4, Leaderboard Q1–Q5, Object Storage Q1–Q4,
Reservation System Q1–Q4, Notification System Q1–Q4, Digital Wallet Q1–Q4,
Database Transactions Q43, Java CompletableFuture Q11/Q14,
Database Transactions Q15/Q22/Q23, Java CompletableFuture Q2/Q3/Q4/Q6/Q8/Q10,
Spring Rest Client Q4/Q6/Q7/Q11/Q12/Q13,
Spring Data JPA Q3 B0/Q4 B1/Q5–Q21 B0/Q39 B0, Hibernate Q8–Q10 B0,
Kubernetes Q22/Q39/Q46/Q47, RabbitMQ Streams Q40–Q42
и Spring Data JDBC Q17 тоже ждут
содержательной приёмки.

07.10 Spring Data JPA Q3 B0/Q4 B1/Q5–Q17 B0
исправлены по Jakarta Persistence 3.2, Hibernate HQL 7.0,
Spring Data JPA 4.1.1 и Spring Framework 6.2:
стандарт JPQL отделён
от расширений HQL, описан in-memory путь AOT и опциональное применение
EntityManagerFactory. Derived `deleteBy` отделён от bulk DELETE,
`Stream<T>` требует транзакции и закрытия; для нативного `Page` не всегда
нужен явный `countQuery`. Для `@Modifying` уточнены транзакция,
bulk-семантика и `flushAutomatically`/`clearAutomatically`.
Для Q9 исправлены `#{#entityName}`, `@NoRepositoryBean` и оценка
выражений с аргументами при выполнении запроса.
Для Q10 разграничены self-invocation и существующая внешняя транзакция,
`REQUIRED`, rollback-правила и видимость методов для разных прокси.
Q11 уточняет независимость `REQUIRES_NEW`, JDBC-only границу savepoint
у `NESTED`, версии `JpaTransactionManager` 6.2.19/7.0 и условия
`UnexpectedRollbackException`.
Q12 по Jakarta Persistence 3.2 разграничивает немедленный
`PESSIMISTIC_READ/WRITE`, `@Version`/`OPTIMISTIC`, переносимую гарантию и
SQL конкретной БД; прежний «оптимистический» пример с
`PESSIMISTIC_WRITE` исправлен. Q13 по Spring Data JPA 4.1.1 уточняет
top-level оптимизацию закрытых проекций, полную материализацию вложенного
join, конструктор DTO и стоимость открытой SpEL-проекции.
Q14 разграничивает выбор результата аргументом `Class<T>` на каждом
вызове и использование класса как bind-параметра запроса; пример сервиса
получил объявленную зависимость репозитория.
Q15/Q16 разграничивают zero-based `Pageable`, возможный count у `Page`,
size+1 у `Slice` и отсутствие метаданных у `List`; в Q15 убран опасный
пример `Page` с collection `JOIN FETCH` без версии. Hibernate 7.4
умеет ограничивать такой запрос в БД при поддержке диалекта; ранние
версии применяли лимит в памяти.
Q17 уточняет `Specification`/Criteria и фильтры: в Spring Data JPA
4.1.1 `where(nonNullSpec)` существует, `where(null)` не принимается с 4.0;
пример начат с базового `isActive()`, а `%`/`_` в пользовательском
`LIKE` экранируются через `EscapeCharacter`.
Парные MCQ и fact-freshness sidecar обновлены.
Structure PASS (43 Q/44 блока), локальный pre-check Q3 B0/Q4 B1/Q5–Q21 B0/Q39 B0 PASS;
полный parity FAIL, в старом JSON остаётся 20 hard-сигналов pre-check
и 24 блока с предупреждениями.
Независимой приёмки и полного содержательного обзора темы не было.

07.10 Spring Data JPA Q18/Q19/Q39 B0: Q18 и Q39 разделены на настройку
JPA auditing и его пределы. Даты не требуют `AuditorAware`, поля автора
требуют; `modifyOnCreate=true`, `setDates=true` по умолчанию. Пример
Spring Security исключает анонимный `Authentication` через
`AuthenticationTrustResolver`. Q39 уточняет фоновую идентичность,
отсутствие callbacks и автоматической проверки `@Version` при JPQL bulk
update. Q19 исправляет ложные выводы об ID, `merge(detached)` и
обязательном `SELECT`. Факты сверены по официальным Spring Data JPA
4.1.1, Spring Security 7.1.1 и Jakarta Persistence 3.2; парные JSON и
fact-freshness sidecar обновлены. У трёх блоков нет локальных hard/warnings,
но независимой blind-приёмки нет.

07.10 Spring Data JPA Q20 B0: по Jakarta Persistence 3.2 исправлены
время callbacks, недоступность `IDENTITY` ID в `@PrePersist` и
категоричный «запрет» `EntityManager`: спецификация не рекомендует
такие вызовы для переносимого кода. `@PostPersist` не подтверждает
commit. Пример listener больше не посылает внешнее событие до commit;
`@TransactionalEventListener` и надёжный журнал публикаций разведены
по Spring Framework и Spring Modulith. MCQ и sidecar синхронизированы,
Q20 локально без hard/warnings; независимой blind-приёмки нет.

07.10 Spring Data JPA Q21 B0 и Hibernate Q8 B0: по Jakarta Persistence
3.2 и Hibernate ORM 7.4 исправлено «AUTO flush перед каждым SELECT».
JPA требует видимости влияющих изменений, Hibernate проверяет
пересечение JPQL/HQL и ожидающих операций; `COMMIT` не гарантирует
видимость до commit и может flush'ить раньше, `MANUAL` не JPA.
Batch-пример Q21 не меняет режим без промежуточных запросов;
`flush`/`clear` отделены от JDBC batching и `IDENTITY`. Парные MCQ
и freshness-sidecars синхронизированы; Hibernate Q8 также различает
diff-based snapshot и bytecode-enhanced in-line tracking. Q21/Q8
локально без hard/warnings.
В старом Hibernate JSON после Q8 остаются 18 hard и 29 блоков с warnings;
его прежний freshness-status `AUDITED` снят после изменения Q8.
Независимой приёмки новых редакций нет; parity Spring Data JPA FAIL,
Hibernate PASS.

07.10 Hibernate Q9 B0: обнаружено пересечение со Spring Data JPA Q19.
По Hibernate 6.6 Session API и 7.0 Migration Guide исправлено:
`Session.save/update` deprecated с 6.0 и удалены в 7.0;
`save` исторически возвращал `Object`, а `merge(detached)` может
вернуть уже существующий managed-экземпляр. MCQ и sidecar
синхронизированы, Q9 локально без hard/warnings. У старого Hibernate
JSON 18 hard/28 предупреждений; независимой приёмки Q9 нет.

07.10 Hibernate Q10 B0: по Hibernate 7.0 Migration Guide, 7.4 Session
API и Jakarta Persistence 3.2 исправлено `Session.load` (удалён),
`Session.get` (deprecated) и ложная обязательность SELECT для `find`.
`getReference` в Hibernate не читает БД при вызове и может вернуть
уже managed объект; JPA допускает `EntityNotFoundException` сразу или
при доступе к состоянию. Ссылка не подтверждает наличие строки.
Парный MCQ/sections и sidecar обновлены, Q10 локально без hard/warnings;
независимой приёмки нет. Старый Hibernate JSON остаётся с
18 hard/28 предупреждений, parity PASS.

07.10 Spring Data JPA Q22 B0 и Hibernate Q19 B0: по Jakarta Persistence
3.2, Hibernate ORM 7.4 и Spring Boot SQL docs исправлены ложные
`EAGER = JOIN`, `LAZY = отдельный SELECT` и неизбежность
LazyInitializationException после сервисной транзакции. `EAGER` —
требование, `LAZY` — hint; to-one по умолчанию EAGER, to-many LAZY;
Hibernate JPQL может дать вторичные SELECT и N+1 даже для EAGER.
Парные MCQ/sections и freshness-sidecars синхронизированы. Обе пары
structure PASS, Q22/Q19 локально без hard/warnings; Hibernate full
parity PASS, Spring Data JPA full parity FAIL из-за прежних блоков;
независимой blind-приёмки нет. Полный аудит файлов не проводился.

07.10 Spring Data JPA Q23/Q24 B0 и Hibernate Q20/Q21/Q23 B0: по
Jakarta Persistence 3.2, Spring Data JPA 4.1.1 и Hibernate ORM 7.4
исправлены N+1 при EAGER to-one, ложная гарантия JOIN от EAGER/EntityGraph,
различие FETCH/LOAD и ограничения нескольких коллекционных fetch joins.
Hibernate 6+ удаляет дубли корневых сущностей после fetch join в памяти;
старый совет DISTINCT/Set только ради удаления этих дублей убран.
Теория, пять блоков MCQ и оба freshness-sidecar синхронизированы.
Structure PASS, все изменённые блоки локально без hard/warnings;
Hibernate full parity PASS, Spring Data JPA full parity FAIL из-за прежних
блоков. Независимой blind-приёмки и полного аудита файлов нет.

07.10 Spring Data JPA Q25 B0 и Hibernate Q15/Q16 B0: по Jakarta
Persistence 3.2, Spring Data JPA `save` и Hibernate ORM 7.4 исправлены
состав CascadeType.ALL, границы orphanRemoval, каскад REMOVE на ManyToMany
и различие удаления элемента связи от удаления целевой сущности.
Department–Employee больше не использует orphanRemoval для переводимого
сотрудника; рекомендация «Set всегда лучше List» заменена проверкой
доменной семантики и SQL. Теория, три MCQ B0/sections и sidecar
синхронизированы. Structure PASS, локальные блоки без hard/warnings,
Hibernate full parity PASS, Spring Data JPA full parity FAIL на прежних
блоках; независимой blind-приёмки и полного аудита нет.

07.10 Spring Data JPA Q26 B0: сопоставлен Hibernate Q18, отдельный
файл не нужен. По Jakarta Persistence 3.2 `@Embeddable`,
`@AttributeOverride` и release notes уточнены: Java record доступен с
Java 16, стандартный record-embeddable — с JPA 3.2; record не требует
no-arg конструктора, обычный embeddable-класс требует. Разделены
одиночный Embedded, ElementCollection, Java-компоненты и SQL-колонки.
Теория, MCQ Q26 B0/sections и sidecar синхронизированы; structure PASS,
Q26 локально без hard/warnings, JPA full parity по прежним блокам FAIL;
независимой blind-приёмки и полного аудита нет.

07.10 Spring Data JPA Q27 B0 и Hibernate Q14 B0: по Jakarta Persistence
3.2/Hibernate 7.4 и `@CheckConstraint` разграничены обязательные
SINGLE_TABLE/JOINED и необязательная TABLE_PER_CLASS, условный JOIN,
UNION/отдельные запросы, nullable-поля подтипов и условный CHECK.
Категоричные заявления о скорости сняты. Теория, парные MCQ и
freshness-sidecar синхронизированы. Structure PASS, локальные блоки
без hard/warnings, Hibernate full parity PASS, Spring Data JPA full
parity FAIL из-за старых блоков. Полного аудита и blind-приёмки нет.
Коммит `5a480396b`.

07.10 Spring Data JPA Q28 B0 и Hibernate Q36 B0: по Hibernate 7.4
Batching/BatchSettings и Spring Data JPA Transactions/`saveAll`
разделены положительный `batch_size` для JDBC batching и периодические
`flush()`/`clear()` для размера persistence context; flush не commit,
`saveAll` не гарантирует пакеты, IDENTITY мешает insert batching, а
SEQUENCE может выдать ID заранее. Исправлены настройки, обе ошибки
счётчика в примерах, необязательные order-флаги, категоричные обещания
скорости и OOM. Теория, парные MCQ и sidecar синхронизированы.
Structure PASS, локальные блоки без hard/warnings, Hibernate full
parity PASS, Spring Data JPA full parity FAIL из-за старых блоков.
Полного аудита и blind-приёмки нет. Коммит `851363aa4`.

07.10 Spring Data JPA Q29 B0 и Hibernate Q24/Q25/Q27 B0: по Hibernate
7.4 уточнены жизнь L1 до clear/закрытия EntityManager, L2 на фабрике,
условный default `use_second_level_cache`, правильное вложение
`hibernate.javax.cache.provider`, selective `@Cacheable`, роль `@Cache`,
кэш ID коллекции, `READ_WRITE` и JTA-зависимый `TRANSACTIONAL`.
Теория, четыре MCQ и sidecar синхронизированы. Structure PASS,
локальные блоки без hard/warnings, Hibernate full parity PASS, JPA
full parity FAIL из-за старых блоков. Полного аудита и blind-приёмки
нет. Коммит `adba6f9eb`.

07.10 Spring Data JPA Q30 B0 о soft delete: по Hibernate ORM 7.4,
`@SoftDelete`/`@SQLRestriction` Javadoc, Spring Data JPA transactions/
modifying queries и PostgreSQL partial indexes исправлены смешение
read-filter с операцией удаления, версия `@SQLRestriction` (6.3),
пропущенный встроенный `@SoftDelete` (с 6.4), пример времени удаления
для 7.4, границы native SQL, bulk UPDATE и транзакции. Теория, MCQ и
freshness-sidecar синхронизированы. Structure PASS (43 Q/44 блока),
локальный Q30 без hard/warnings; полный JPA parity FAIL на прежних
блоках. Полного содержательного аудита файла и независимого blind
review Q30 нет. Коммит `24942dd8e`.

08.10 Spring Data JPA Q31 B0 о Hibernate Filters: по Hibernate ORM 7.4
`@Filter`/`@FilterDef` и PostgreSQL RLS исправлены область действия
фильтра, прямая загрузка по ID (`applyToLoadByKey`), включение в
`Session`, граница безопасности арендаторов и пример с явной
транзакцией. Теория, MCQ и freshness-sidecar синхронизированы.
Structure PASS (43 Q/44 блока), локальный Q31 без hard/warnings;
полный JPA parity FAIL на прежних блоках. Полного содержательного
аудита файла и независимого blind review Q31 нет. Коммит `73c177d6d`.

08.10 Spring Data JPA Q32 B0: по reference 4.1.1 уточнены repository
fragments, конвенция `<ИнтерфейсФрагмента>Impl`, устаревшее имя по
основному репозиторию, ручной bean wiring и `spring.factories` для
внешнего фрагмента. Пример согласован с полями `User` из Q1 и
ограничен пояснением, что простому фильтру хватает `Specification`.
Теория, MCQ и freshness-sidecar синхронизированы; structure PASS,
локальный Q32 без hard/warnings, полный JPA parity FAIL из-за старых
блоков. Полного аудита и независимой blind-приёмки Q32 нет.
Коммит `3875da44e`.

08.10 Spring Data JPA Q33 B0: по Spring Data JPA 4.1.1 и Hibernate 7.4
уточнены SELECT→поштучное удаление, callbacks, транзакция объявленного
метода, память и различие между одним JPQL-вызовом и числом SQL.
Теория, MCQ и freshness-sidecar синхронизированы; structure PASS,
локальный Q33 без hard/warnings, полный JPA parity FAIL на прежних
блоках. Полного аудита и независимой blind-приёмки Q33 нет.
Bulk DELETE с `@SoftDelete` требует отдельной runtime-проверки.
Коммит `02337debc`.

08.10 Spring Data JPA Q34 B0, Integration Testing Q4/Q36 B0 и
Testcontainers Q5 B0: по Spring Boot 3.5.16/4.1.1 и Testcontainers
исправлено устаревшее правило об обязательном `replace = NONE` при
распознанном Testcontainers `DataSource`. Теория, четыре MCQ и sidecar
синхронизированы; структура 123 Q/124 блока PASS, локальные блоки без
hard/warnings, полный parity трёх файлов FAIL на старых блоках. Полной
проверки и независимой blind-приёмки нет. Коммит `50f305e46`.

08.10 Testcontainers Q2/Q6 B0: по релизу 2.0 и официальным страницам
модулей 2.0.5 обновлены координаты зависимостей, пакеты классов,
таблица БД и требования к Docker API-совместимому рантайму. Теория,
MCQ и sidecar синхронизированы; structure 40 Q/40 блоков PASS,
локальные Q2/Q6 без hard/warnings, полный parity FAIL на старых блоках.
Полной проверки и независимой blind-приёмки нет. Коммит `a2f01ccc2`.

08.10 Testcontainers Q28 B0: по модулю Elasticsearch 2.0.5 и Elastic
обновлены координата, пакет класса, URI через `getHttpHostAddress()`,
готовый `single-node` и условия security/refresh. Теория, MCQ и sidecar
синхронизированы; structure 40 Q/40 блоков PASS, локальный Q28 без
hard/warnings, полный parity FAIL на старых блоках. Полной проверки,
runtime-прогона и blind-приёмки нет. Коммит `81270a2f6`.

08.10 Testcontainers Kafka Q9/Q15/Q16/Q17/Q32: по официальному модулю
Kafka 2.0.5, Spring TestContext и JUnit 5 Extension исправлены класс
для Confluent-образа, `@DynamicPropertySource` и жизненный цикл
унаследованного `static @Container`. Теория, MCQ Q9/Q15/Q17/Q32 B0
и sidecar синхронизированы; Q16 изменён только в теории. Structure
40 Q/40 блоков PASS, локальные четыре B0 без hard/warnings, полный
skel/parity FAIL на старых блоках. Полной проверки, запуска контейнера
и независимой blind-приёмки нет. Коммит `8b0d71b4e`.

08.10 Testcontainers MongoDB Q11 B0: по модулю/коду 2.0.5 исправлены
координата, пакет класса, явный `withReplicaSet()` и смысл
`getReplicaSetUrl()`. Теория, MCQ и sidecar синхронизированы;
structure 40 Q/40 блоков PASS, Q11 без локальных hard/warnings,
полный skel/parity FAIL на старых блоках. Полной проверки,
runtime-прогона и независимой blind-приёмки нет. Коммит `7a3aa1b19`.

08.10 Spring Data JPA Q35 B0: по Spring Data JPA 4.1.1/Hibernate 7.4
исправлены пример проекции, неосуществлённый bulk-update и границы
рекомендаций по fetch, batching, readOnly, OSIV и L2. Теория, MCQ
и sidecar синхронизированы; structure 43 Q/44 блока PASS, Q35 без
локальных hard/warnings, полный skel/parity FAIL на старых блоках.
Полной проверки, компиляции примера и независимой blind-приёмки нет.
Коммит `52aed25b9`.

08.10 Spring Data JPA Q36 B0: по официальным query methods и
keyword reference исправлена подпись к `deleteBy`: выборка и
пообъектное удаление с callbacks, а не один bulk DELETE. Теория,
MCQ и sidecar синхронизированы; structure 43 Q/44 блока PASS,
Q36 без локальных hard/warnings, полный parity FAIL на старых блоках.
Полной проверки, runtime-прогона и независимой blind-приёмки нет.
Коммит `46510e35f`.

02.11 Spring Data JPA Q37 B0: по официальным Specifications 4.1.2,
EscapeCharacter API и Jakarta Persistence 3.2 исправлены фильтр
по связанной `Category.name` и экранирование `%`/`_` в собственном
Criteria-запросе. Теория, MCQ и freshness-sidecar синхронизированы;
structure 43 Q/44 блока PASS, Q37 без локальных hard/warnings,
полный parity файла FAIL на старых блоках. Полной проверки,
компиляции примера и независимой blind-приёмки нет. Коммита нет.

02.11 Spring Data JPA Q38 B0: по официальным Projections 4.1.2
исправлено противоречие между `@Value`/SpEL с `target` и обещанием
узкого SELECT. Пример использует закрытую проекцию с `default`-методом;
уточнены DTO, динамический выбор результата и вложенные JOIN.
Сопоставлены Q13/Q14/Q35; теория, MCQ и sidecar синхронизированы.
Structure 43 Q/44 блока PASS, локальный Q38 без hard/warnings,
полный parity файла FAIL на старых блоках. Полной проверки,
runtime/SQL-прогона и независимой blind-приёмки нет. Коммита нет.

02.11 Spring Data JPA Q40 B0: сопоставлены Q34/Q40 и Flyway Q10.
По Spring Boot 3.5/4.1.2, Flyway baselineOnMigrate и Boot Replace
уточнены порядок миграций до JPA, явный `ddl-auto: validate` при
дефолте `none`, выключенный по умолчанию baseline и `NON_TEST` в
`@DataJpaTest`. Теория, MCQ и sidecar синхронизированы. Structure
43 Q/44 блока PASS, локальный Q40 без hard/warnings, полный parity
файла FAIL на других блоках. Полной проверки, runtime-прогона и
независимой blind-приёмки нет. Коммита нет.

02.11 Spring Data JPA Q41 B0: сопоставлены Q15/Q24/Q35/Q41.
По Spring Data JPA 4.1.2, Jakarta Persistence 3.2 и Hibernate 7.4
исправлены безусловные обещания `LEFT JOIN`, отдельных SELECT для
нескольких bags и правильной пагинации через граф с `countQuery`.
Теория, MCQ и sidecar синхронизированы. Structure 43 Q/44 блока PASS,
локальный Q41 без hard/warnings, полный parity файла FAIL на других
блоках. Полной проверки, runtime/SQL-прогона и независимой
blind-приёмки нет. Коммита нет.

02.11 Spring Data JPA Q42 B0: сопоставлены Q8/Q33/Q42. По Spring
Data JPA 4.1.2, Jakarta Persistence 3.2 и Hibernate HQL 7.4
уточнены объявленный `@Query` против derived delete, транзакция,
JPQL `UPDATE`/`DELETE` против нативного/HQL `INSERT`, flush/clear и
состояние контекста. Исправлен пример нативной записи аудита.
Теория, MCQ и sidecar синхронизированы. Structure 43 Q/44 блока PASS,
локальный Q42 без hard/warnings, полный parity файла FAIL на других
блоках. Полной проверки, runtime/SQL-прогона и независимой
blind-приёмки нет. Коммита нет.

02.11 Spring Data JPA Q43 B0: сопоставлены Q15/Q16/Q43. По Spring
Data JPA 4.1.2 и PostgreSQL transaction isolation убраны обещания
замены `Pageable` и постоянной скорости, уточнены состав ключа,
ненулевые поля, проекция, индекс и конкурентные изменения.
Теория, MCQ и sidecar синхронизированы. Structure 43 Q/44 блока,
локальный Q43 без hard/warnings, полный skel/parity файла FAIL на
других блоках. Полной проверки, runtime/SQL-прогона и независимой
blind-приёмки нет. Инвентарь не менялся. Коммита нет.

02.11 Spring Batch Q6 B0/B1: в теории уточнено, что персистентный
`JobRepository` не гарантирует восстановления позиции reader/writer
без сохранённого `ExecutionContext` через `ItemStream`; исправлены
B0 A и объяснения B0, B1 точечно проверен. Structure, Markdown,
parity, skel и JSON sidecar PASS. Общий gates FAIL из-за прежнего
Q15 и других сигналов; Q6 B1 `NOVEL-TERM-WRONG` разобран вручную.
Полной `П` и независимой приёмки нет. Blind-input Q6 B0/B1 готов:
`docs/mcq-quality/runs/20261102-spring-batch-q6-b0-b1-r1/`.
Фазы A/B и record не выполнялись. Коммита нет.

02.11 Spring Batch Q28 B0: сопоставлены Q6/Q28/Q38. По Boot 3.5/4.1
исправлено ложное обещание автозапуска всех `Job`: запускается один;
при нескольких задают `spring.batch.job.name`. Разделены отключение
автозапуска и инфраструктуры. Теория, MCQ и sidecar синхронизированы;
structure, Markdown, parity, skel PASS. Общий gates FAIL на старом Q15
и других сигналах. Формальный Q28 C «частей=1» проверен вручную:
две смысловые части присутствуют. Полной `П` и независимой фазы A/B
нет. Старый blind-input Q6 B0/B1 устарел: manifest не совпадает с
хешами теории и JSON после Q28; для рецензирования его не использовать.
Коммита нет.

02.11 Spring Batch Q38 B0: сопоставлены Q6/Q28/Q38. По Batch
5.0/5.2/6 уточнено различие менеджеров транзакций репозитория и
шага и риск повторной обработки, когда данные и метаданные пишутся
в разные БД. Теория, MCQ и sidecar синхронизированы; structure,
Markdown, parity, skel PASS. Общий gates FAIL из-за прежнего Q15
и других сигналов, по Q38 локальных hard/warnings нет. Полной `П`
и независимой приёмки нет. Новый blind-input Q6 B0/B1, Q28 B0,
Q38 B0: `docs/mcq-quality/runs/20261102-spring-batch-q6-q28-q38-r1/`;
manifest совпадает с текущими хешами. Старый пакет Q6 устарел.
Коммита нет.

02.11 P4-пакет ёмкости LLM-сервиса написан: Architecture Q33 и MCQ B0,
TOC, freshness-sidecar, инвентарь 33 Q/33 блока. Сверены официальные
OpenAI/Claude rate limits, vLLM metrics и Kubernetes HPA. JSON и Markdown
валидны, локальный skel Q33 чист; общие parity/skel файла FAIL из-за
других Q. Авторская точечная проверка A/B/C/D выполнена, полной `П`
и независимой MCQ-приёмки нет. Коммита нет.

02.11 `DEEPEN` LLM Integration Patterns Q14 B0 выполнен на уровне `О`:
исправлен подсчёт только выходных токенов, добавлены применимые лимиты
OpenAI/Claude, оценка до вызова, сверка с usage и общая квота инстансов.
Теория, production MCQ и freshness-sidecar синхронизированы; structure,
Markdown и локальный skel Q14 PASS, общие parity/skel файла FAIL из-за
других Q. A/B/C/D и sections авторски прочитаны; полной `П` и
независимой приёмки нет. Инвентарь не менялся, коммита нет.

02.11 Python 3.15 обработан одним вопросом Python Core Q10 о смене
кодировки текстового I/O по умолчанию. Сверены финальные What's New,
PEP 686 и `io`; Q8/соседняя concurrency-тема не дублируют сценарий.
Теория, TOC, production MCQ Q10 B0, freshness-sidecar и инвентарь
(10 Q/10 блоков) синхронизированы. Structure/Markdown/parity PASS,
локальный skel Q10 без hard/warn; общий gates FAIL на старых Q1–Q9.
A/B/C/D и sections прочитаны автором, полной `П` и независимой
MCQ-приёмки нет. Коммита нет.

02.11 Python Typing Q4 дополнен `closed=True` и `extra_items=T` по
Python 3.15, PEP 728 и официальной документации `TypedDict`.
Production MCQ Q4 B1 добавлен, B0 сохранён; sidecar обновлён.
Structure/Markdown/parity/gates PASS для 4 Q/5 блоков. A/B/C/D и
sections авторски прочитаны, полная `П` и независимая MCQ-приёмка
не проведены. Инвентарь не менялся, коммита нет.

02.11 Python Core Q11 про explicit lazy import написан по финальным
What's New 3.15 и PEP 810. Core Q6/Q7/Q10 и Packaging Q1/Q4 сопоставлены
по заголовкам и точечно по ответам: сценарий старта сервиса и первого
запроса там отсутствовал. TOC, production MCQ Q11 B0, sidecar и инвентарь
синхронизированы: 11 Q/11 блоков. Structure/Markdown/parity PASS,
локальный skel Q11 без hard/warn; общий gates FAIL на старых Q1–Q9.
A/B/C/D и sections авторски прочитаны, полной `П` и независимой MCQ-приёмки
нет. Python 3.15 локально не установлен; пример сверен с документацией.
Коммита нет.

02.11 Python Core Q5 углублён по Python 3.15 `sentinel()`, PEP 661 и
What's New. С Core Q5 и Typing Q1/Q4 сравнили заголовки и соседние
ответы: недоставало различия пропуска и явного `None` в примере.
Заголовок/TOC и production MCQ Q5 B0 синхронизированы, B1 добавлен;
sidecar и инвентарь отражают 11 Q/12 блоков. Structure/Markdown/parity
PASS; локальный skel B1 без hard/warn, общий skel FAIL на старых блоках,
включая Q5 B0. A/B/C/D и sections B1 авторски прочитаны, полной `П`
и независимой MCQ-приёмки нет. Python 3.15 локально не установлен;
пример сверен с документацией. Коммита нет.

02.11 Python Core Q3 углублён по Python 3.15 `frozendict`, PEP 814 и
документации built-in types. Q3/Q4/Q5/Q9 сопоставлены по заголовкам и
точечно по ответам: практического выбора immutable mapping не было.
Заголовок/TOC и production MCQ Q3 B0 синхронизированы, B1 добавлен;
sidecar и инвентарь отражают 11 Q/13 блоков. Structure/Markdown/parity
PASS; локальный skel Q3 B1 без hard/warn, общий gates FAIL на старых
блоках (`errors=1`, `warnings=9`, `manual_review=7`, skel `hard=8`).
A/B/C/D и sections B1 авторски прочитаны, полной `П` и независимой
MCQ-приёмки нет. Python 3.15 локально не установлен; пример сверен
по документации. Коммита нет.

02.11 Python Core Q12 добавлен по официальным Python 3.15
`profiling.sampling`, `profiling.tracing` и What's New. Общий profiling
Q1/Q2/Q14/Q25/Q27 и Python Core/Concurrency сопоставлены по заголовкам
и точечно по ответам: сценарий attach к Python worker отсутствовал.
TOC/See also, production MCQ Q12 B0, sidecar и инвентарь синхронизированы:
12 Q/14 блоков. Structure/Markdown/parity PASS, локальный skel Q12
без hard/warn; общий gates FAIL на прежних блоках (`errors=1`,
`warnings=9`, `manual_review=7`, skel `hard=8`). A/B/C/D и sections
авторски прочитаны, полной `П` и независимой MCQ-приёмки нет.
Python 3.15 локально не установлен; команда сверена по документации.
Коммита нет.

Python Concurrency Q9 углублён по free-threading guide и PEP 803:
проверка сборки, runtime GIL после импортов, wheel `abi3t`/`abi3.abi3t`.
Production MCQ Q9 B1, sidecar и инвентарь синхронизированы: 9 Q/10 блоков.
Structure/Markdown/parity PASS, локальный skel Q9 B1 без hard, с двумя
предупреждениями формы; общий gates FAIL на прежних блоках (`skel hard=9`).
Авторская проверка A/B/C/D и sections проведена, полной `П` и независимой
MCQ-приёмки нет. CPython 3.15 локально не запускался. Коммита нет.

Python Packaging Q5 написан по [документации `site`](https://docs.python.org/3.15/library/site.html)
и [PEP 829](https://peps.python.org/pep-0829/): `.pth`, `.start`, парные
файлы, порядок обработки и `-S`. Production MCQ Q5 B0, sidecar, TOC
и инвентарь синхронизированы: 5 Q/5 блоков. Structure/Markdown/parity
PASS, локальный skel Q5 без hard/warn; A/B/C/D и sections авторски
проверены. Полной `П` и независимой MCQ-приёмки нет. Локально Python
3.14; поведение 3.15 подтверждено документацией. Коммита нет.

Python Core Q12 углублён по [What's New](https://docs.python.org/3.15/whatsnew/3.15.html#pep-831-frame-pointers-enabled-by-default),
[perf profiling HOWTO](https://docs.python.org/3/howto/perf_profiling.html)
и [PEP 831](https://peps.python.org/pep-0831/): frame pointers в
поддерживаемых сборках, отдельный perf map, нативные расширения и
Windows x64. Production MCQ Q12 B1, sidecar и инвентарь синхронизированы:
12 Q/15 блоков. Structure/Markdown/parity PASS, локальный skel B1 без
hard/warn; общий gates FAIL на прежних блоках. Авторская проверка
A/B/C/D и sections проведена, полной `П` и независимой MCQ-приёмки нет.
Python 3.15 и Linux `perf` локально не запускались. Коммита нет.

Python Database Q1–Q3 написаны после точечной проверки FastAPI Q2/Q3,
Database Transactions Q33/Q34 и Python Packaging Q3. Созданы три
вопроса, четыре production MCQ-блока и freshness sidecar; TOC и
инвентарь обновлены. Structure/Markdown/parity/style PASS; MCQ skel
pre-check без hard, с предупреждениями. Авторская точечная проверка
A/B/C/D выполнена; полная `П` и независимая MCQ-приёмка впереди.
Коммита нет.

Python Database Q4 добавлен после сверки Q2, Database Transactions
Q33/Q34 и документации Python 3.15 `sqlite3`. Q4 объясняет, что
`with con` управляет открытой транзакцией, но не закрывает соединение,
и что при `autocommit=True` контекст ничего не фиксирует. Production
MCQ B0, sidecar, TOC и инвентарь синхронизированы: 4 Q/5 блоков.
Локальный Python 3.14.4 подтвердил оба режима, исключение и доступность
соединения после блока. Structure/Markdown/parity/style PASS; skel
pre-check без hard, с предупреждениями. Авторская проверка A/B/C/D
и sections выполнена; полная `П` и независимая MCQ-приёмка открыты.
Коммита нет.

Python Database Q5 добавлен после сверки Python Database Q2/Q3,
Database Transactions Q33/Q34 и FastAPI Q3. Q5 разводит жизненный
цикл SQLAlchemy Engine/пула и Session, описывает возврат соединения
и диагностику ожидания пула. Production MCQ B0, sidecar, TOC и
инвентарь синхронизированы: 5 Q/6 блоков. Structure/Markdown/parity/
style PASS, skel pre-check без hard, с предупреждениями; авторская
проверка A/B/C/D и sections выполнена. SQLAlchemy локально не
установлен; полной `П` и независимой MCQ-приёмки нет. Коммита нет.

Python Database Q6 добавлен после сверки Python Database Q2/Q5,
FastAPI Q4 и Hibernate Q22. Q6 объясняет `expire_on_commit`,
отсоединение ORM-объекта при закрытии Session и подготовку DTO до
выхода из её области. Production MCQ B0, sidecar, TOC и инвентарь
синхронизированы: 6 Q/7 блоков. Structure/Markdown/parity/style
PASS, skel pre-check без hard, с предупреждениями; авторская
проверка A/B/C/D и sections выполнена. SQLAlchemy локально не
установлен; полной `П` и независимой MCQ-приёмки нет. Коммита нет.

Python Database Q7 добавлен после проверки Python Database Q3/Q6,
FastAPI Q1/Q4 и Python Concurrency Q1. Q7 объясняет `MissingGreenlet`
от неявного I/O на lazy-связи при открытой `AsyncSession`,
`selectinload` и версионную границу `AsyncAttrs` 2.0.13.
Production MCQ B0, sidecar, TOC и инвентарь синхронизированы:
7 Q/8 блоков. Structure/Markdown/parity/style PASS, skel pre-check
без hard, с предупреждениями; авторская проверка A/B/C/D и sections
выполнена. SQLAlchemy локально не установлен; полной `П` и
независимой MCQ-приёмки нет. Коммита нет.

Python Database Q8 добавлен после сверки заголовков (`З`) и ответов
(`О`) Python Database Q6/Q7 и Hibernate Q20/Q21. Q8 разбирает N+1,
`selectinload` против `joinedload`, обязательный `Result.unique()`
для JOIN коллекции и границу tuple IN. Production MCQ B0, sidecar,
TOC и инвентарь синхронизированы: 8 Q/9 блоков. Structure/Markdown/parity/style PASS, skel pre-check без hard. Авторская проверка
A/B/C/D и sections проведена. Полная `П`, запуск SQLAlchemy с БД
и независимая MCQ-приёмка остаются открыты. Коммита нет.

Python Database Q1–Q2 прошли авторскую полную `П` по PEP 249,
Python sqlite3, Psycopg 3 parameters и SQLAlchemy Session Basics.
Q1 дополнен примером `sql.Identifier` после allowlist; Q2 — границей
режима autocommit и восстановлением Session после сбоя flush.
Production MCQ Q1 B1 и Q2 B0 синхронизированы, Q2 B1 добавлен;
sidecar и инвентарь обновлены: 8 Q/10 блоков. Structure/Markdown/
parity/style PASS, skel pre-check без hard; A/B/C/D и sections
проверены автором. Локального запуска с БД и независимой MCQ-приёмки
нет. Коммита нет.

Python Database Q3–Q4 прошли авторскую полную `П` по FastAPI,
SQLAlchemy asyncio, Starlette/AnyIO worker threads и Python sqlite3.
Q3 дополнен явным offload всей sync DB-операции с отдельной Session;
Q4 — ручным SQL BEGIN при autocommit=True и SQL COMMIT/ROLLBACK.
MCQ Q3 B1 и Q4 B1 добавлены, B0 sections синхронизированы;
sidecar и инвентарь обновлены: 8 Q/12 блоков. Structure/Markdown/
parity/style PASS, skel pre-check без hard; A/B/C/D и sections
проверены автором. Q4 локально проверен на Python 3.14.4;
интеграционного запуска FastAPI/SQLAlchemy и независимой MCQ-приёмки
нет. Коммита нет.

Python Database Q5–Q6 прошли авторскую полную `П` по SQLAlchemy
Session Basics, Connection Pooling и DetachedInstanceError. Q5 пример
исправлен: возвращает scalar IDs до закрытия Session, что согласовано
с Q6; MCQ Q5 B0 вариант B, example/edge cases и freshness синхронизированы.
Q6 проверен без смысловой правки, MCQ B0 авторски сверён. Счётчики
остались 8 Q/12 блоков; инвентарь не менялся. Structure/Markdown/
parity/style PASS, skel pre-check без hard. SQLAlchemy с БД локально
не запускался; независимой MCQ-приёмки нет. Коммита нет.

Python Database Q7–Q8 прошли авторскую полную `П` по SQLAlchemy
asyncio, Errors и relationship loading. Q7 подтверждён без смысловой
правки. Q8 уточняет LIMIT/OFFSET с joinedload: подзапрос сохраняет
родителей страницы, но unique() для коллекции остаётся обязательным.
MCQ Q8 B1 добавлен, B0 sections и freshness синхронизированы;
инвентарь — 8 Q/13 блоков. Structure/Markdown/parity/style PASS,
skel pre-check без hard; A/B/C/D и sections проверены автором.
Вся Python Database Q1–Q8 прошла авторскую `П`. Интеграционного
запуска SQLAlchemy с БД и независимой MCQ-приёмки нет. Коммита нет.

Go database/sql Q22–Q23 прошли авторскую полную `П` по официальным
Go querying, manage connections и database/sql API. Теория, production
MCQ и freshness уточнены: полный проход Rows.Next закрывает Rows,
Rows.Err проверяет ошибку итерации, SetMaxIdleConns не создаёт резерв
сверх SetMaxOpenConns. Structure/parity PASS; локально Q22/Q23 без
hard/warnings, общий pre-check старого файла FAIL (25 hard вне
целевого пакета). A/B/C/D и sections проверены автором; blind review
и запуска с реальной БД нет. Состав прежний — 31 Q/32 блока;
инвентарь не менялся. Коммита нет.

Go database/sql Q24–Q25 прошли авторскую полную `П` по Go prepared
statements, transactions и database/sql API. Теория, production MCQ
Q24/Q25 B0 и freshness уточнены: контекст подготовки не действует
на последующее выполнение, tx.StmtContext привязывает statement к
транзакции, ошибка Commit не подтверждает результаты. Структура
31 Q/32 блока PASS, локально Q24/Q25 без hard/warnings. Общие parity
и pre-check старого файла FAIL (23 hard вне Q24/Q25); A/B/C/D и sections
проверены автором, независимого blind review и запуска с БД нет.
Инвентарь не менялся, коммита нет. Вся Go database/sql Q22–Q25 прошла
авторскую `П`.

Go stdlib Q26–Q27 прошли авторскую полную `П` по официальным
io, ioutil, os, bufio и Go 1.16 release notes. Теория, production MCQ
Q26/Q27 B0 и freshness уточнены: Reader допускает короткое чтение и
данные вместе с EOF, Writer сообщает ошибку при короткой записи,
io.Copy работает потоково, os.ReadDir возвращает другой тип, а ioutil
остаётся доступным. Структура 31 Q/32 блока PASS; локальный pre-check
Q26/Q27 без hard/warnings. Общие parity и pre-check старого файла FAIL
(21 hard вне Q26/Q27); A/B/C/D и sections проверены автором, blind
review и запуска всех примеров нет. Инвентарь не менялся, коммита нет.

Go stdlib Q28 прошёл авторскую полную `П` по официальным embed,
io/fs, net/http и Go 1.16 release notes. В теории исправлен пример
с необъявленной staticFS; показаны fs.Sub/http.StripPrefix для
публикации только статики и уточнены правила директивы. Production
MCQ Q28 B0 и freshness синхронизированы; неподтверждённые заявления
о времени запуска убраны. Структура 31 Q/32 блока PASS, локальный
pre-check Q28 без hard/warnings. Общие parity и pre-check старого
файла FAIL (21 hard вне Q28); A/B/C/D и sections проверены автором.
Go toolchain отсутствует, пример не компилировался; blind review нет.
Инвентарь не менялся, коммита нет.

Go stdlib Q29–Q30 прошли авторскую полную `П` по официальным
sort, slices и Go 1.21–1.23 release/API docs. Исправлены примеры,
сигнатуры компараторов, стабильность и границы Concat/Collect;
убраны универсальные оценки скорости и дублирование maps.Keys.
Production MCQ Q29/Q30 B0 и freshness синхронизированы. Структура
31 Q/32 блока PASS, локальный pre-check Q29/Q30 без hard/warnings.
Общие parity и pre-check старого файла FAIL (19 hard вне Q29/Q30);
A/B/C/D и sections проверены автором. Go toolchain отсутствует,
примеры не компилировались; blind review нет. Инвентарь не менялся,
коммита нет.

Go stdlib Q31 прошёл авторскую полную `П` по Go 1.27 release notes и
официальным encoding/json, encoding/json/v2, jsontext API. Уточнены
граница `UnmarshalRead` (одно значение, весь Reader до EOF), маршрут
`UnmarshalDecode` для нескольких значений, nil slice/map и `[]byte`.
Production MCQ Q31 B0/B1 и freshness синхронизированы; структура
31 Q/32 блока PASS, локальный pre-check без hard/warnings. Общие
parity и pre-check старого файла FAIL (21 блок вне коридора длины,
19 hard вне Q31). Пример Go 1.27 не компилировался, blind review нет.

Go stdlib Q7–Q10 прошли авторскую `П` по официальному encoding/json API
и Go spec. Исправлены границы `omitempty`/`json:"-"`, синтаксическая
проверка при `RawMessage`, потоковое чтение массива и разбор строки
в `UnmarshalJSON`. Production MCQ Q7–Q10 B0 и freshness
синхронизированы; структура 31 Q/32 блока PASS, локальный pre-check
без hard/warnings. Общие parity/pre-check старого файла FAIL
(18/32 correct самые длинные, 19 блоков вне коридора длины,
16 hard). Go toolchain отсутствует, примеры не компилировались;
blind review нет. Инвентарь не менялся, коммита нет.

Go stdlib Q6 прошёл авторскую `П` по официальному encoding/json API.
Пример переписан без повторного объявления `u` и с проверкой ошибок;
уточнены экспортируемые поля, указатель для Unmarshal и неизвестные
ключи. Production MCQ Q6 B0 и freshness синхронизированы. Структура
31 Q/32 блока PASS, локальный pre-check без hard/warnings. Общие
parity/pre-check старого файла FAIL (17/32 correct самые длинные,
18 блоков вне коридора длины, 15 hard). Go toolchain отсутствует,
пример не компилировался; blind review нет. Инвентарь не менялся,
коммита нет.

Go stdlib Q1–Q2 прошли авторскую `П` по официальным net/http,
Go 1.22 release notes и блогу команды Go. Q1 показывает собственный
mux, `http.Server`, таймауты и проверку ошибки запуска; Q2 объясняет
специфичность паттернов вместо длины строки, GET/HEAD, wildcard,
перенаправление со слэшем и конфликт регистрации. Production MCQ
Q1–Q2 B0 и freshness синхронизированы; структура 31 Q/32 блока PASS,
локальный pre-check без hard/warnings. Общие parity/pre-check старого
файла FAIL (16/32 correct самые длинные, 17 блоков вне коридора длины,
13 hard). Go toolchain отсутствует, примеры не компилировались;
blind review нет. Инвентарь не менялся, коммита нет.

Go stdlib Q3 прошёл авторскую `П` по официальным net/http Handler
и HandlerFunc API. Пример middleware теперь использует определённую
проверку `X-Request-ID`; уточнены порядок внешней и внутренней
обёрток, прерывание цепочки и возврат во внешний `Logging`.
Production MCQ Q3 B0 и freshness синхронизированы. Структура
31 Q/32 блока PASS, локальный pre-check без hard/warnings; общие
parity/pre-check старого файла FAIL (15/32 correct самые длинные,
16 блоков вне коридора длины, 12 hard). Go toolchain отсутствует,
пример не компилировался; blind review нет. Инвентарь не менялся,
коммита нет.

Go stdlib Q4 прошёл авторскую `П` по официальным net/http и блогу Go
о повторном использовании HTTP/1 соединений. Уточнены общий срок
`Client.Timeout`, контекст запроса, пул `DefaultTransport` и граница
Go 1.27 при закрытии непрочитанного body. Пример проверяет ошибки
создания запроса и чтения, а также HTTP-статус. Production MCQ
Q4 B0 переписан, B1 добавлен; freshness синхронизирован.
Структура 31 Q/33 блока PASS, локальный pre-check Q4 B0/B1 без
hard/warnings. Общие parity/pre-check файла FAIL (14/33 correct
самые длинные, 15 блоков вне коридора длины, 11 hard). Go toolchain
отсутствует, пример не компилировался; blind review нет.
Инвентарь не менялся, коммита нет.

Go stdlib Q5 прошёл авторскую `П` по официальным net/http,
os/signal и Kubernetes Pod lifecycle. Пример отдельно обрабатывает
ошибку запуска и сигнал, ждёт возврата `Shutdown` с новым
контекстом; уточнены граница таймаута и захваченные соединения.
Заголовок и TOC соседнего Q4 уточнены под его B1 о `Transport`;
STEM-DRIFT Q4 B1 устранён. Production MCQ Q5 B0 и freshness
синхронизированы. Структура
31 Q/33 блока PASS, локальный pre-check Q5 без hard/warnings.
Общие parity/pre-check файла FAIL (13/33 correct самые длинные,
14 блоков вне коридора длины, 10 hard). Go toolchain отсутствует,
код не компилировался и сигналы не тестировались; blind review нет.
Инвентарь не менялся, коммита нет.

Go stdlib Q11–Q12 прошли авторскую `П` по официальным strings,
bytes, исходному коду Builder и блогу Go о `go fix`. Q11 различает
стоимость конкатенации в цикле и работу растущего буфера без
непроверенного обещания выигрыша в каждом случае. Q12 объясняет
`io.Reader`/`io.Writer`, непрочитанную часть `Bytes()` и то, что
`Reset()` сохраняет память. Примеры и production MCQ Q11–Q12 B0
обновлены, freshness синхронизирован. Структура 31 Q/33 блока
PASS, локальный skel без hard/warnings. Общие parity/pre-check
FAIL (11/33 correct самые длинные, 12 блоков вне коридора длины,
9 hard skel; старый STEM-DRIFT Q7 B0). Go toolchain отсутствует,
примеры не компилировались; blind review нет. Инвентарь не менялся,
коммита нет.

Go stdlib Q13–Q14 прошли авторскую `П` по официальному time API.
Q13 теперь показывает проверяемый результат `Time.Add`/`Sub`,
наносекунды `Duration` и монотонное время. Q14 проверяет ошибку
парсинга и различает UTC без зоны у `Parse` и заданную локацию у
`ParseInLocation`. Production MCQ Q13 B0 и Q14 B0/B1 обновлены,
freshness синхронизирован. Структура 31 Q/34 блока PASS, локальный
skel без hard/warnings. Общие parity/pre-check FAIL (9/34 correct
самые длинные, 10 блоков вне коридора длины, 7 hard skel;
старый STEM-DRIFT Q7 B0; Q14 B0/B1 имеют предупреждение о
недословном заголовке блока, но оба сценария раскрыты в Q14).
Go toolchain отсутствует, код не компилировался; blind review нет.
Инвентарь не менялся, коммита нет.

Go stdlib Q15–Q16 и Go Concurrency Q14 прошли авторскую `П` по
официальным `time` API и Go 1.23 release notes. Исправлены пример
с одним моментом в двух зонах, `Equal`/`==`, поиск IANA-зон и
ошибочные утверждения об утечке `time.After` и дренировании канала
после `Stop`. Указаны условия новой семантики (`go` главного модуля,
`GODEBUG=asynctimerchan=1`). Три production MCQ-блока и два freshness
синхронизированы. Структура двух тем 66 Q/70 блоков PASS, локальный
skel без hard/warnings. Общие parity/pre-check FAIL из-за старого
backlog; linkage сохраняет ошибки stdlib Q7 B0 и concurrency Q21 B0. Недословные
заголовки изменённых MCQ дают предупреждения, сценарии есть в теории.
Go toolchain отсутствует, код и оба режима таймеров не запускались;
независимой blind-приёмки нет. Инвентарь не менялся, коммита нет.

Go stdlib Q17 и Go Concurrency Q23–Q25 прошли авторскую `П` по
официальным `context`, `CancelFunc`, `WithDeadline` и `go vet`.
Исправлены примеры с повторными `ctx :=`, преждевременным `log.Fatal`
и неиспользуемым `child`. Разделены отмена контекста, освобождение
ресурсов и фактическое завершение горутины. Четыре production
MCQ-блока и два freshness синхронизированы. Структура двух тем
66 Q/70 блоков PASS, локальный skel без hard/warnings; общие gates
FAIL из-за старого backlog, linkage сохраняет ошибки stdlib Q7 B0
и concurrency Q21 B0. У новых MCQ остаются предупреждения о
недословных заголовках, но каждый сценарий разобран в своём Q.
Go toolchain отсутствует; код и `go vet` не запускались.
Независимой blind-приёмки нет. Инвентарь не менялся, коммита нет.

Go stdlib Q18–Q19 и связанный Go Core Q28 прошли авторскую `П` по
официальным `errors`, `fmt`, `os` и Go 1.13/1.20 источникам. Q18
различает `Is`/`As`, `%w`/`%v` и `AsType` с Go 1.26; Q19 объясняет
ветви `Join` и границу `errors.Unwrap`. В Q28 исправлены версия
`Join`, двойной `Close` и пропуск ошибки `os.Create`. Три production
MCQ-блока и два freshness обновлены. Структура двух тем 67 Q/70
блоков PASS, локальный skel без hard/warnings. Общие gates FAIL
из-за старого backlog; linkage сохраняет ошибки stdlib Q7 B0 и
Go Core Q8/Q25/Q31 B0. У изменённых блоков предупреждения о
недословных заголовках, сценарии раскрыты в своих Q. Go toolchain
отсутствует, код не компилировался; независимой blind-приёмки нет.
Инвентарь не менялся, коммита нет.

Go stdlib Q20–Q21 прошли авторскую полную `П`: исправлены пример
`slog`, граница передачи контекста и выбор логгера без универсального
рейтинга скорости. Production MCQ Q20–Q21 и freshness обновлены;
structure PASS, локальный skel без замечаний, общие gates FAIL на
старом backlog. Независимой blind-приёмки и компиляции примера нет.

Go Core Q1–Q3 прошли авторскую полную `П`: условия сборки и `cgo`,
сравнение Go с Java на JVM без фиксированных RAM/стартовых чисел,
virtual threads и уместность mutex наряду с channels. Production MCQ
Q1–Q3 и freshness обновлены; structure PASS (36 Q/36 блоков),
локальные skel/parity/anti-tell/linkage без сигналов. Общие gates
FAIL на прежнем backlog других блоков. Независимой blind-приёмки нет;
замеров ресурсов и компиляции Go-примеров не было.

Go Core Q4–Q7 прошли авторскую полную `П`: исправлены условия
повторного объявления через `:=`, граница `string`/UTF-8, усечение
при числовой конверсии и поведение `nil`-map/channel. Примеры Q5–Q7
переписаны, production MCQ Q4–Q7 и freshness синхронизированы.
Structure PASS (36 Q/36 блоков), локальные skel/parity/anti-tell/
linkage без сигналов; общие gates FAIL на старом backlog. Go toolchain
отсутствует, примеры не компилировались; blind-приёмки нет.

Go Core Q8–Q9 прошли авторскую полную `П`: разделены конверсия
переменной и присваивание константы, числовой текст и `string(int)`;
для указателей уточнены nil-panic, `unsafe` и escape analysis без
обязательной heap-аллокации. Примеры, production MCQ Q8–Q9 и freshness
обновлены. Structure PASS (36 Q/36 блоков), локальные skel/parity/
anti-tell/linkage без сигналов; общие gates FAIL на старом backlog.
Go toolchain отсутствует, примеры не компилировались; blind-приёмки нет.

Go Core Q10–Q12 прошли авторскую полную `П`: исправлены семантика
копирования массива и среза, модель `len`/`cap` без обещания layout
и коэффициента роста, некомпилируемый голый `append(s, x)` и пример
перезаписи общего массива. Production MCQ Q10–Q12 B0 и freshness
обновлены. Structure PASS (36 Q/36 блоков), локальные skel/parity/
anti-tell/linkage без сигналов; общие gates FAIL на старом backlog.
Go toolchain отсутствует, примеры не компилировались; независимой
blind-приёмки нет. Инвентарь не менялся.

Go Core Q13–Q14 прошли авторскую полную `П`: убраны устаревшие
фиксированные бакеты карты, уточнены порядок `range`, конкурентный
доступ и адресуемость элемента. Для embedding описаны продвижение
селекторов, конфликты имён, наборы методов и отсутствие подтипизации.
Production MCQ Q13–Q14 B0 и freshness обновлены. Structure PASS
(36 Q/36 блоков), локальные skel/parity/anti-tell/linkage без сигналов;
общие gates FAIL на других блоках. Go toolchain отсутствует, примеры
не компилировались; независимой blind-приёмки нет. Инвентарь в этом
пакете не менялся.

Go Core Q15–Q16 прошли авторскую полную `П`: примеры множественных
результатов переписаны в связную программу, а `error` в конце сигнатуры
отмечен как идиома. Для методов уточнены поверхностная копия при
value receiver, автоматическое взятие адреса и наборы методов `T`/`*T`.
Production MCQ Q15–Q16 B0 и freshness обновлены. Structure PASS
(36 Q/36 блоков), локальные skel/parity/anti-tell/linkage без сигналов;
общие gates FAIL на других блоках. Go toolchain отсутствует, примеры
не компилировались; независимой blind-приёмки нет. Инвентарь в этом
пакете не менялся.

Go Core Q17–Q18 прошли авторскую полную `П`: для variadic-функций
уточнены `nil` без аргументов, общий массив при `f(s...)` и граница
`[]int`/`...any`; для замыканий — версия языка модуля `go 1.22`,
объявление переменной через `:=` и общая переменная до цикла. Примеры,
production MCQ Q17–Q18 B0 и freshness обновлены. Structure PASS
(36 Q/36 блоков), локальные skel/parity/anti-tell/linkage без сигналов;
общие gates FAIL на других блоках. Go toolchain отсутствует, примеры
не компилировались; независимой blind-приёмки нет. Инвентарь в этом
пакете не менялся.

Go Core Q19–Q20 прошли авторскую `П`: обновлены теория интерфейсов,
цельные примеры, production MCQ B0 и freshness. Structure PASS
(36 Q/36 блоков); независимой blind-приёмки нет. Локального Go
toolchain нет, примеры не компилировались; состав Q/блоков прежний.

Go Core Q21–Q22 прошли авторскую `П`: теория `any`, JSON и type
assertion/type switch, цельные примеры, production MCQ B0 и freshness
обновлены. Structure PASS (36 Q/36 блоков), локальные struct/skel/linkage
без hard-сигналов; общие parity/gates FAIL на других блоках.
Независимой blind-приёмки нет. Go toolchain отсутствует, примеры не
компилировались; инвентарь в этом пакете не менялся.

Go Core Q23–Q24 прошли авторскую `П`: структурная реализация
интерфейсов, обработка ошибок, цельные примеры, production MCQ B0 и
freshness обновлены. Structure PASS (36 Q/36 блоков), локальные
struct/skel/linkage без сигналов на целевых блоках; общие gates/parity
FAIL на других блоках. Независимой blind-приёмки нет. Go toolchain
отсутствует, примеры не компилировались; инвентарь не менялся.

Go Core Q25–Q26 прошли авторскую `П`: цепочки причин ошибок,
`errors.Is`/`errors.As`/`errors.AsType`, `%w` и `panic`/`recover`/`defer`,
цельные примеры, production MCQ B0 и freshness обновлены. Structure
PASS (36 Q/36 блоков), локальные struct/skel/linkage без сигналов на
целевых блоках; общие gates/parity FAIL на других блоках. Независимой
blind-приёмки нет. Go toolchain отсутствует, примеры не компилировались;
инвентарь не менялся.

Go Core Q27 прошёл авторскую `П`: `defer`, цельный пример,
production MCQ B0 и freshness обновлены. Structure PASS (36 Q/36
блоков), локальные struct/skel/linkage без сигналов на Q27; общие
gates/parity FAIL на других блоках. Независимой blind-приёмки нет.
Go toolchain отсутствует, пример не компилировался; инвентарь не менялся.

Go Core Q29–Q30 прошли авторскую `П`: исправлены модель G/M/P,
`GOMAXPROCS`, границы GC/netpoll и кросс-сборки с cgo. Production MCQ
B0 и freshness обновлены; structure PASS (36 Q/36 блоков), локальные
struct/skel/linkage без сигналов. Общие gates/parity FAIL на других
блоках. Независимой blind-приёмки нет; Go toolchain отсутствует,
пример кросс-сборки не запускался. Инвентарь не менялся.

Go Core Q31 прошёл авторскую `П`: уточнены линковка, cgo, `net`/`os/user`
и ресурсы `scratch`; прежний STEM-DRIFT исправлен. Production MCQ B0
и freshness синхронизированы. Structure PASS (36 Q/36 блоков),
локальные struct/skel/linkage без сигналов, Markdown gate PASS;
общие parity/skel FAIL на старых блоках. Независимой blind-приёмки
нет; Go toolchain отсутствует, ELF и образ не собирались. Инвентарь
не менялся.

Go Core Q32–Q33 прошли авторскую `П`: убран недоказанный процент
покрытия стандартной библиотекой, уточнены `database/sql`, `regexp`,
`log/slog` и кооперативная отмена через `context.Context`. Production
MCQ B0 и freshness обновлены; structure PASS (36 Q/36 блоков),
локальные struct/skel/linkage без сигналов, Markdown gate PASS.
Общие parity/skel FAIL на старых блоках, независимой blind-приёмки
нет. Go toolchain отсутствует, пример с драйвером не компилировался;
инвентарь не менялся.

Go Core Q34–Q36 прошли авторскую `П`: production-примеры привязаны к
Go FAQ, Prometheus FAQ и кейсу PayPal 2020 года; границы generic-методов
сверены с Go 1.27, а сравнение GC, памяти и запуска с Java/Kotlin —
с Go GC guide и документацией Java 21/JDK 25. Production MCQ B0 и
freshness синхронизированы. 36 Q/36 блоков, JSON/Markdown и локальные
struct/skel/linkage Q34–Q36 PASS; общий parity FAIL на старых Q23 и
повторяющихся вариантах, linkage предупреждает о прежнем Q28.
Независимой blind-приёмки нет, Go 1.27 локально не запускался;
инвентарь не менялся.

Go Memory & GC, вводный абзац и Q6/Q9–Q10/Q22–Q23 прошли авторскую `О`:
убраны фиксированные пределы пауз и памяти, разделены STW и задержки
запроса; Green Tea привязан к Go 1.26, поколенческий ZGC — к JDK 24+,
цель G1 `MaxGCPauseMillis=200` — к JDK 25. Q23 теперь спрашивает, как
сравнивать память; номер, MCQ и локальное оглавление сохранены.
Production MCQ B0 и freshness обновлены: 27 Q/27 блоков, JSON/Markdown,
struct и parity всего файла PASS. Linkage Q10 B — ручная проверка
ложной линейной зависимости STW от heap; прежние warnings Q12/Q27
остаются. Общий skel FAIL на старых Q11–Q13, изменённые блоки без
сигналов. Остальные ответы файла не получили `П`; независимой
blind-приёмки MCQ нет, Go 1.26+ локально не запускался.

Go Memory & GC Q5/Q8/Q11–Q13 прошли авторскую `О`: убраны безусловные
2 KiB и миллион goroutine, фиксированная стоимость write barrier,
исправлена формула `GOGC` с учётом GC roots и пределы мягкого
`GOMEMLIMIT`. Production MCQ B0 и freshness синхронизированы.
Все 27 Q/27 блоков проходят JSON/Markdown, struct, skel и parity;
прежние skel hard Q11–Q13 устранены. Linkage оставляет прежние warnings
Q10/Q27, изменённые Q без сигналов. Независимой MCQ-приёмки и полной
`П` остальных ответов файла нет; Go toolchain в PATH отсутствует.

Go Memory & GC Q1–Q4 прошли авторскую `О`: GC сканирует стеки как
корни, а `&x`, interface, closure и goroutine не означают безусловный
escape; фиксированный порог 64 KB удалён. Диагностика `-gcflags=-m=2`
отделена от измерения `allocs/op`. Учтена официальная публикация о
малых аллокациях Go 1.27 от 16.09.2026. Production MCQ B0 и freshness
обновлены; 27 Q/27 блоков, JSON/Markdown, struct, skel и parity всего
файла PASS. Linkage Q10/Q27 оставляет прежние warnings, изменённые Q
без сигналов. Полная `П` остальных ответов и независимая MCQ-приёмка
ожидаются; Go toolchain в PATH отсутствует.

Go Memory & GC Q16–Q20 прошли авторскую `О`: исправлены выводы из
разницы heap-профилей и `allocs/op`, удержание большого массива через
под-slice, версии реализации map после `delete` и расчёт памяти
зависшей goroutine. Production MCQ B0 и freshness обновлены; независимая
blind-приёмка и полная `П` файла ожидаются. Go toolchain отсутствует,
примеры локально не исполнялись.

Go Memory & GC Q7/Q14–Q15/Q21 прошли авторскую `О`: исправлены модель
tri-color marking, пределы `runtime.GC()`, возраст heap-профиля и отличие
`SetFinalizer` от `AddCleanup` с Go 1.24. Production MCQ B0 и freshness
синхронизированы. Изменённые блоки без сигналов предпроверок;
независимой blind-приёмки и полной `П` файла нет. Общий gate ещё
возвращает прежний verbatim Q22, linkage Q10/Q27 требует разбора.

Go Memory & GC Q24–Q27 прошли авторскую `О`: исправлены границы
`sync.Pool`, два разных аргумента `make` для slice/map, пример inlining
с `//go:noinline` и диагностика GC без фиксированного порога CPU.
Production MCQ B0 и freshness обновлены; Q27 linkage warning исчез.
Независимой blind-приёмки и полной `П` файла нет. Q10/Q22 прошли
повторную авторскую `О`: linkage Q10 устранён, правильный вариант Q22
перефразирован, verbatim 0. Q27 сопоставлен с Q17/Q24–Q26 и
`runtime/metrics`, отдельной правки не потребовал. JSON/Markdown
27 Q/27 блоков PASS, parity PASS, linkage 0 errors/warnings,
skel 0 hard. Общий gate exit 1 остаётся из-за старых anti-tell
Q3/Q5/Q6/Q23.

Go Memory & GC Q3/Q5/Q6/Q23 прошли повторную авторскую проверку:
Q3 уточнён в Markdown, MCQ Q3 A/Q5 D/Q6 A/Q23 B синхронизированы.
После последовательной проверки Q1–Q27 теория получила авторскую
`П`. Общий pre-check exit 0: anti-tell 0, linkage 0 errors/warnings,
skel 0 hard/2 warn, verbatim 0. Независимой MCQ-приёмки ещё нет;
Go-примеры локально не исполнялись без toolchain.

Python Core Q1–Q5 прошли авторскую `О`: в Q4 уточнён контракт hash,
в MCQ Q2 B0 исправлен второй правильный вариант; обновлены тексты
и sections Q1/Q2/Q4/Q5. Q3 B0 теперь явно спрашивает о четырёх
базовых контейнерах, Q3 B1 — о `frozendict`; предупреждение linkage
о расхождении B0 с общим заголовком Q3 осмысленно. Изменённая группа
без anti-tell и hard skel; общий gate exit 1 остаётся из-за Q6–Q9
и Q12. Независимой MCQ-приёмки и полной `П` файла нет.

Python Core Q6–Q9 прошли авторскую `О`: уточнены late binding,
`iter()`/`next()`, пределы `finally`/`__exit__` и момент вызова
`default_factory`; MCQ B0 и freshness синхронизированы. У группы
anti-tell 0, hard skel 0, linkage 0 errors/warnings. Общий gate
exit 1 оставляет Q12 B1 STEM-DRIFT; Q3 B0 — узкий stem с отдельным
B1 про `frozendict`. Независимой MCQ-приёмки и полной `П` файла нет.

Python Core Q10–Q12 проверены по официальной Python 3.15
документации: уточнены глобальный режим и фильтр lazy imports,
основной поток/`--all-threads` sampling и граница `--native`.
Production MCQ Q11 B0/Q12 B0 обновлены; Q12 B1 больше не даёт
STEM-DRIFT. Весь Markdown Q1–Q12 последовательно прочитан, авторская
`П` получена. Gates exit 0 при 12 Q/15 блоках, parity PASS,
anti-tell 0, skel 0 hard/1 warn, verbatim 0 и linkage errors 0;
узкие Q3 B0 и Q12 B1 оставляют два осмысленных STEM-MISMATCH.
Python 3.15 примеры локально не исполнялись; независимая MCQ-приёмка
открыта.

Python Typing Q1–Q4 прошли авторскую `П`: Q1 уточнён для eager,
stringified и deferred аннотаций Python 3.11–3.15; Q2 показывает
эквивалент `TypeVar` для 3.11; Q4 различает открытую структурную
совместимость `TypedDict` и правила его конструктора. Q3 и Q4 B1
повторно сверены с официальной спецификацией. Production MCQ Q1/Q2/
Q4 B0 и Q4 B1 синхронизированы, все пять блоков автором проверены.
Gates exit 0 при 4 Q/5 блоках, Markdown PASS. Python 3.11 и 3.15
локально не запускались; независимая MCQ-приёмка открыта.

Python Concurrency Q1–Q4 прошли авторскую `О` по официальной
документации Python 3.11–3.15. Q3 уточняет eager Task с Python 3.14,
Q4 — условия отмены в TaskGroup и gather; пример Q4 помещён внутрь
функции. MCQ Q1–Q4 B0 переписаны для сравнимой формы, A/B/C/D и
sections проверены. Для этой группы anti-tell 0 и skel hard 0;
общий gates exit 1 остаётся из-за прежних сигналов Q5–Q9. Поведение
coroutine, eager Task, TaskGroup и gather проверено на локальном
CPython 3.14.4. Независимая MCQ-приёмка открыта.

Python Concurrency Q5–Q9 прошли авторскую `О`, весь Markdown Q1–Q9
последовательно перечитан до `П`. В Q5 уточнён `TimeoutError` снаружи
`asyncio.timeout`, Q7 — порядок создания работы при bounded Queue,
Q9 — конкретные wheel-теги Python 3.15 и синхронизация состояния.
MCQ Q5–Q9 B0/Q9 B1 автором проверены и переписаны; общий gates exit 0,
9 Q/10 блоков, parity PASS, anti-tell 0, skel 0 hard, linkage errors 0.
Узкий Q9 B1 оставляет осмысленный STEM-MISMATCH. На локальном
CPython 3.14.4 проверены timeout, Queue и отмена ожидания to_thread;
Python 3.15/free-threaded локально не исполнялись. Независимая
MCQ-приёмка остаётся открытой.

Python Packaging Q1–Q5 прошли авторскую `П`: Markdown и все пять MCQ
B0 прочитаны целиком, A/B/C/D и sections сверены с официальными PyPA,
pip и Python 3.15 источниками. Q4 уточняет требование `--require-hashes`
для каждой прямой и транзитивной зависимости, Q5 — повторные `.start`
entry points и продолжение после ошибки hook. Парный MCQ и freshness
обновлены; gates exit 0, 5 Q/5 блоков, parity PASS, anti-tell 0,
skel hard 0, linkage errors 0, Markdown PASS. Python 3.15 локально не
исполнялся, независимой blind-приёмки MCQ нет.

Python Testing Q1–Q4 прошли авторскую `О`: Q1 уточняет ограничения
`unittest.TestCase` под pytest, Q2 — teardown по scope и cleanup при
ошибке до `yield`, Q3 — роль `pytest.param` id/marks, Q4 — target
`patch` при разных формах импорта. MCQ Q1–Q4 B0 и все A/B/C/D/sections
проверены; Q2/Q3 stem и Q2 scope исправлены. Markdown PASS, структура
7 Q/7 блоков, parity PASS, linkage errors 0. Общий gates exit 1 только
из-за anti-tell Q6, skel hard Q5–Q7 и linkage warnings Q5/Q6;
для Q1–Q4 этих hard-сигналов нет. Freshness добавлен, независимой
blind-приёмки нет.

Python Testing Q5–Q7 и весь файл прошли авторскую `П`. Q5 уточняет
срок подмены `monkeypatch`, `context()` и границу `autospec`; Q6 —
strict/auto режимы pytest-asyncio, область loop и cleanup при отмене;
Q7 — реальный `COMMIT` при внешней тестовой транзакции. MCQ Q5–Q7 B0
переписаны и сверены по A/B/C/D/sections; Q6 stem синхронизирован.
Весь Markdown Q1–Q7 перечитан. Общие gates exit 0, 7 Q/7 блоков,
parity PASS, anti-tell 0, skel hard 0, linkage errors/warnings 0,
Markdown PASS. Freshness обновлён. Примеры не запускались в тестовом
окружении; независимой blind-приёмки MCQ нет.

FastAPI Q1–Q4 прошли авторскую `О`. Q1/Q2 сверены с официальными
разделами async и sub-dependencies; Q3 уточняет `yield` lifecycle:
с 0.121.0 `scope="function"` закрывает до ответа, `request` — после,
в 0.106.0–0.117.x обычный cleanup был до ответа. Q4 различает
проверку `response_model` и прямой `Response` без Pydantic-фильтра.
MCQ Q1–Q4 B0 и A/B/C/D/sections сверены и переписаны; Q2–Q4 stem
совпадают с Markdown. Markdown PASS, структура 7 Q/7 блоков,
parity PASS; Q1–Q4 anti-tell/skel hard/linkage warnings 0. Общие gates
exit 1 только из-за Q5–Q7: anti-tell Q5/Q6, skel hard Q5–Q7 и
stem warnings Q5–Q7. Freshness обновлён, независимой приёмки нет.

FastAPI Q5–Q7 по официальным handling errors, lifespan и testing docs
уточнены: 422 — стандартный, но заменяемый ответ на неверный вход;
прежние dependency overrides в тесте восстанавливаются; `with TestClient`
запускает lifespan. MCQ Q5–Q7 B0 и A/B/C/D/sections синхронизированы,
противоречие Q6 C исправлено. Весь Markdown Q1–Q7 перечитан: авторская
`П`. Общий pre-check exit 0: структура 7 Q/7 блоков, parity PASS,
anti-tell 0, skel hard/warn 0, linkage errors/warnings 0. Примеры не
исполнялись; независимая blind-приёмка MCQ ожидается.

MySQL Q1–Q3 авторски проверены по официальной MySQL 8.4 документации.
Q1 уточняет default engine и назначение redo/undo, Q2 — скрытый
кластерный ключ и покрывающий индекс, Q3 — границу EXPLAIN и
исполняемого EXPLAIN ANALYZE. Соседние SQL Q16/Q45 и Database
Transactions Q9 не дублируют эти вопросы. MCQ Q1–Q3 B0 и все
A/B/C/D/sections синхронизированы; 6 Q/6 блоков, parity PASS,
в изменённой группе anti-tell 0, skel hard 0, linkage errors/warnings 0.
Общий pre-check exit 1 относится к старым Q4–Q6: anti-tell Q4,
skel hard Q4–Q6, linkage warning Q4. Независимая blind-приёмка MCQ
ожидается.

MySQL Q4–Q6 сверены с официальными InnoDB, charset, Connector/J и
HikariCP источниками. Q4 различает snapshot и locking read с границей
явной транзакции; Q5 разделяет кодировку входа и `character_set_results`;
Q6 фиксирует неизвестный исход записи при потере ответа. MCQ Q4–Q6 B0,
A/B/C/D и sections синхронизированы. Весь MySQL Markdown Q1–Q6
перечитан: авторская `П`. Общий pre-check exit 0: 6 Q/6 блоков,
parity PASS, anti-tell 0, skel hard/warn 0, linkage errors/warnings 0.
Независимая содержательная и blind-приёмка MCQ не проводилась.

JDBC/пулы Q1–Q3 проверены по Java SE 25 JDBC API и Spring
DataSourceUtils. Q1 различает DataSource и пул, Q2 исправляет закрытие
ResultSet через Statement и release Spring-managed connection, Q3
уточняет auto-commit, close и неизвестный исход сбоя commit. Соседние
SQL Q45, Database Transactions Q33–Q34 и Spring Data JDBC Q1/Q16
не дублируют эти вопросы. MCQ Q1–Q3 B0 и A/B/C/D/sections
синхронизированы: структура 6 Q/6 блоков и parity PASS, в изменённой
группе anti-tell 0, skel hard 0, linkage errors/warnings 0. Общий
pre-check exit 1 из-за старых Q4–Q6: linkage error Q5, warning Q4,
skel hard Q4–Q6. Независимой MCQ-приёмки нет.

JDBC/пулы Q4–Q6 и парный production JSON обновлены по Java SE 25 JDBC API, pgJDBC, Connector/J и HikariCP. Вся тема Q1–Q6 прошла авторскую `П`; общий pre-check exit 0, структура/parity PASS, anti-tell 0, skel hard/warn 0, linkage errors/warnings 0. Независимая содержательная и blind-приёмка Q1–Q6 ожидается. Инвентарь не менялся.

Spring Batch Q6 B0/B1, Q28 B0 и Q38 B0 повторно сверены с официальными Batch 5.0/6.0 и Boot 3.5/4.1 документами. Q6 больше не обещает полный аудит; у resourceless уточнены ограничения, а у Q6/Q28 улучшена форма MCQ. Markdown, production JSON и freshness-sidecar обновлены. Структура/parity/Markdown PASS; общий pre-check FAIL из-за прежнего Q15 `NEIGHBOR-LEAK` и предупреждений Q15/Q16/Q23/Q43. Для независимого ревью подготовлен `docs/mcq-quality/runs/20261103-spring-batch-q6-q28-q38-r1/blind-input.jsonl`; phase-a/reveal/record не выполнялись. Авторский проход не считать blind или `FINAL`.

Spring Batch Q14–Q16 и MCQ B0 исправлены по официальной документации Batch 4.3/5.1/6.0. Уточнены paging, потокобезопасность и restart JDBC reader, версия JPA cursor, detached сущности после JPA paging, заголовок и специальные значения CSV. `NEIGHBOR-LEAK` Q15 и `LOW-COVERAGE` Q15/Q16 устранены: структура/parity/Markdown PASS, общий pre-check exit 0, linkage errors 0 и warnings 2 на Q23/Q43. Freshness-sidecar обновлён. Старый blind-input Q6/Q28/Q38 r1 устарел из-за новой правки общего файла. Свежие blind-input: `docs/mcq-quality/runs/20261103-spring-batch-q6-q28-q38-r2/blind-input.jsonl` и `docs/mcq-quality/runs/20261103-spring-batch-q14-q16-r1/blind-input.jsonl`. Фазы A/B/record не проводились; `FINAL` нет.

Spring Batch Q22–Q25 и MCQ B0 обновлены по официальным Batch 5.2/6.0 docs. Q22 допускает повтор `read()`, но не обещает тот же элемент. Q23 различает локальное/удалённое partitioning и новые local chunking/remote step. Q24 разделяет старый параллельный чанк и новую параллельную обработку `ItemProcessor`. Q25 ограничивает гарантию restart. Структура/parity/Markdown PASS, общий pre-check exit 0; linkage errors 0 и один warning на Q43. Freshness-sidecar обновлён, независимой приёмки нет. Старые blind-input Batch r2 Q6/Q28/Q38 и r1 Q14–Q16 устарели по полным хешам. Не передавать их ревьюеру; пересобрать после остальных правок файла.

Spring Batch Q39–Q40 и B0 JSON исправлены по официальным Batch 5.2/6.0 и Spring Integration docs: Q39 убрал некорректные диапазоны из COUNT(*) и ложное равенство gridSize числу потоков; Q40 заменил пример remote partitioning на remote chunking builder-ы. Kafka-адаптеры и надёжность доставки отмечены как отдельная интеграционная настройка. Freshness-sidecar обновлён; Markdown PASS, структура/parity PASS, общий pre-check exit 0, linkage errors 0, warning Q43 один, skel hard 0. Это авторская `О`, не независимый `FINAL`; диагностические skel-сигналы Q39/Q40 остаются. Инвентарь не менялся.

Spring Batch Q31/Q43 и B0 JSON исправлены по официальным Batch 5.2/6, Boot Actuator и Micrometer: режимы ObservationRegistry различены по версиям, таймеры не названы счётчиками элементов, ложный gauge и выдуманный health-ответ удалены. Markdown PASS, структура/parity PASS, общий pre-check exit 0; linkage errors/warnings 0, skel hard 0. Это авторская `О`, не независимый `FINAL`. Подготовлены свежие Batch blind-input: `20261103-spring-batch-q6-q28-q38-r3`, `20261103-spring-batch-q14-q16-r2`, `20261103-spring-batch-q22-q25-r1`, `20261103-spring-batch-q39-q40-r1`, `20261103-spring-batch-q31-q43-r1`. Phase-a/reveal/B/record ещё нет. Инвентарь не менялся.

PostgreSQL 19 на 03.11.2026 остаётся RC1: официальный анонс назначил GA на 19.11. Q44–Q46 PostgreSQL 18 и B0 JSON исправлены по официальным WAL, standby, logical replication и slot docs: recovery.conf удалён в 12+, remote_commit требует synchronous standby, DDL/sequence не реплицируются логически, max_slot_wal_keep_size может инвалидировать слот. Freshness-sidecar обновлён. Markdown/structure/parity PASS; у Q44–Q46 linkage/skel сигналов нет. Общий pre-check exit 1 из-за прежних LOW-COVERAGE Q6/Q24/Q26/Q34; skel hard 0. Это авторская `О`, независимого `FINAL` нет. Инвентарь не менялся.

PostgreSQL Q24/Q31/Q32/Q34 и B0 JSON исправлены по официальным EXPLAIN, planner cost, VACUUM и pgstattuple docs. Уточнены startup/total cost, условность random_page_cost, редкая обрезка хвоста VACUUM с ACCESS EXCLUSIVE и граница размеров файлов против bloat. Freshness-sidecar обновлён. Markdown/structure/parity PASS, локальные linkage/skel hard 0; общий pre-check exit 1 только из-за старых LOW-COVERAGE Q6/Q26, skel hard 0. Авторская `О`, независимого `FINAL` нет, инвентарь не менялся.

PostgreSQL Q26–Q28 и B0 JSON исправлены по официальным PostgreSQL 18 Transaction Isolation, MVCC и System Columns: четыре названия/три режима, serialization anomaly в REPEATABLE READ, снимок по уровню, SSI/40001, ненулевой xmax у видимой строки и нестабильный ctid. Freshness-sidecar обновлён; Markdown/structure/parity PASS, у Q26–Q28 linkage warning 0 и skel hard 0. Общий pre-check exit 1 только из-за прежнего LOW-COVERAGE Q6. Авторская `О`, независимого `FINAL` нет; инвентарь не менялся.

PostgreSQL Q5–Q7 и B0 JSON исправлены по официальным PostgreSQL 18 pg_hba.conf, Data Types, JSON Types, hstore, Network Address Types и Monetary Types. Q5 уточняет отсутствие fallback после ошибки аутентификации; Q6 отличает встроенные типы от расширения hstore и исправляет LOW-COVERAGE; Q7 убирает абсолютное утверждение, что json нельзя индексировать. Freshness-sidecar обновлён; Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, Q5–Q7 без локальных сигналов формы. Общий pre-check exit 1 из-за старого дословного совпадения Q32. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q8–Q10 и B0 JSON исправлены по официальным PostgreSQL 18 Arrays, GIN, hstore, Enum, Composite и Domain docs. GIN не обещан для `= ANY(array)`; hstore допускает значение SQL NULL, `exist`/`defined` различают его и отсутствие ключа; composite не наследует ограничения таблицы вне неё, а DOMAIN CHECK пропускает NULL, если не задан NOT NULL. Зависимая строка Q6 и её MCQ section синхронизированы. Freshness-sidecar обновлён. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, у Q8–Q10 локальных tell/скелетных сигналов нет. Общий pre-check exit 1 только из-за старого дословного совпадения Q32; авторская `О`, независимого `FINAL` нет. Инвентарь прежний.

PostgreSQL Q11–Q14 и B0 JSON исправлены по официальным PostgreSQL 18 Index Types, Multicolumn Indexes, ORDER BY, Operator Classes, GIN, GiST и Text Search Indexes: Q12 учитывает cost-based skip scan, Q13 — классы jsonb и pending list, Q14 — условность перепроверки строк у обоих методов. Freshness-sidecar обновлён; Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных tell Q11–Q14 нет. Общий pre-check exit 1 только из-за старого дословного совпадения Q32. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q15–Q19 и B0 JSON исправлены по официальным PostgreSQL 18 BRIN, Partial Indexes, Expression Indexes, Index-Only Scans и CREATE INDEX: lossy BRIN и summarization, planner-time predicate, IMMUTABLE, visibility map/heap fetch, ожидания и INVALID concurrent-индекса. Freshness-sidecar обновлён; Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных tell/скелетных сигналов Q15–Q19 нет. Общий pre-check exit 1 из-за старого дословного совпадения Q32. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q20–Q23 и B0 JSON исправлены по официальным PostgreSQL 18 Using EXPLAIN, EXPLAIN, Combining Multiple Indexes и Index-Only Scans: per-loop actual rows, стоимость, BUFFERS, Seq/Index выбор, bitmap-порядок и visibility map. Freshness-sidecar обновлён; Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q20–Q23 нет. Общий pre-check exit 1 только из-за старого Q32. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q29–Q30 и B0 JSON исправлены по официальным PostgreSQL 18 Transaction Isolation, SAVEPOINT, ROLLBACK TO и RELEASE SAVEPOINT: phantom не ограничен INSERT, READ UNCOMMITTED не даёт dirty read, частичный откат сохраняет точку, RELEASE не равен COMMIT. Freshness-sidecar обновлён; Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q29–Q30 нет. Общий pre-check exit 1 только из-за старого Q32. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q25 и Q33, B0 JSON исправлены по официальным PostgreSQL 18 Statistics Used by the Planner, pg_statistic/pg_stats, ANALYZE, Routine Vacuuming и Vacuuming Configuration: каталог вместо несуществующего pg_statistics, выборочная точность, разные пороги VACUUM/ANALYZE, cap, INSERT, XID и партиционированный parent. TOC и freshness-sidecar обновлены; Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q25/Q33 нет. Общий pre-check exit 1 только из-за старого Q32. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q35–Q37 и B0 JSON исправлены по официальным PostgreSQL 18 Table Partitioning и CREATE TABLE: границы RANGE, маршрутизация, HASH без гарантии нагрузки, три стадии pruning, DDL-блокировки и затраты множества частей. Freshness-sidecar обновлён; Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q35–Q37 нет. Общий pre-check exit 1 только из-за старого Q32. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q38–Q41 и B0 JSON исправлены по официальным PostgreSQL 18 Tutorial/Functions/Calls/Processing: peers и стандартная RANGE-рамка, стабильный tie-breaker, LAG/LEAD default против реального NULL, семь строк вместо семи дней. Freshness-sidecar обновлён; Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q38–Q41 нет. Общий pre-check exit 1 только из-за старого Q32. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q31–Q34 точечно сверены по PostgreSQL 18 Routine Vacuuming, VACUUM и pgstattuple. Q31 дополнен visibility map и защитой от XID wraparound; MCQ Q31/Q32 B0 переформулированы после сигналов TELL/literal-overlap. Схема/структура/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL Q31–Q34 и literal Q32 нет. Общий pre-check exit 1 из-за Q21/Q23/Q30/Q38; TELL Q44 остаётся. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q20–Q24 точечно сверены по PostgreSQL 18 EXPLAIN, Using EXPLAIN, Combining Multiple Indexes и Index-Only Scans. Q21 различает опцию EXPLAIN ANALYZE и команду ANALYZE; Q23 различает bitmap по индексу и последующее чтение heap. MCQ Q21/Q23 B0 переформулированы после literal-overlap, в Q23 C исправлено противоречивое объяснение. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL и literal-overlap Q20–Q24 нет. Общий pre-check exit 1 из-за Q30/Q38; TELL Q44 остаётся. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q29–Q30 сверены по PostgreSQL 18 Transaction Isolation, SAVEPOINT, ROLLBACK TO SAVEPOINT и RELEASE SAVEPOINT. Q29/B0 оставлены без правок. Q30 и B0 question_text уточнены до `ROLLBACK TO SAVEPOINT`, потому что прежний широкий заголовок допускал второй защитимый вариант про RELEASE; TOC обновлён. Теория добавляет удаление последующих активных точек при RELEASE. MCQ Q30 B0 переформулирован после literal-overlap, sections проверены. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL и literal-overlap Q29–Q30 нет. Общий pre-check exit 1 из-за Q38; TELL Q44 остаётся. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q38–Q41 сверены по PostgreSQL 18 Window Functions, Window Function Calls и Window Function Processing. Q38 уточняет пример `running_total` для peers и явной ROWS; Q39–Q41 теория оставлена без правок. MCQ Q38 B0 переформулирован после literal-overlap, в Q40 B0 исправлен контрфактический текст дистрактора. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL и literal-overlap Q38–Q41 нет; дословных correct во всём файле 0. Общий pre-check exit 1 из-за прежнего TELL Q44. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q44–Q46 сверены по PostgreSQL 18 Log-Shipping Standby Servers, Replication Configuration, Publication, Restrictions и pg_replication_slots. Q44 уточняет асинхронный режим по умолчанию; Q45 добавляет TRUNCATE к логической публикации и различает кластер от отдельной базы. Q46 теория оставлена без правок. MCQ Q44 B0 исправлен после TELL, Q45 B0 синхронизирован, Q46 B0 выровнен по форме; sections проверены. Freshness-sidecar исправляет неверную помету PgBouncer. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, локальных TELL и literal-overlap нет, общий pre-check exit 0. Авторская `О`, независимого `FINAL` нет, инвентарь прежний.

PostgreSQL Q1–Q4 точечно сверены по PostgreSQL 18 How Connections Are Established, Resource Consumption, Cumulative Statistics, WAL, WAL Configuration, WAL Settings и PostgreSQL 15 Release Notes. Q1 исправляет устаревший stats collector и общий «кэш планов»; Q2 — несуществующий потолок 8–16 GB, порог 99%, ложное равенство промаха shared_buffers физическому чтению и целочисленный расчёт доли попаданий; Q3 — порядок WAL и data-page flush, wal_compression и PITR; Q4 — redo-позицию, мягкий max_wal_size и распределение I/O. MCQ Q1–Q4 B0 и sections синхронизированы, варианты проверены автором. Markdown/structure/parity PASS, linkage errors/warnings 0, skel hard 0, TELL и literal-overlap 0, общий pre-check exit 0. Авторская `О` по всем Q1–Q55 завершена пакетами, полной `П` и независимого `FINAL` нет, инвентарь прежний.

Metrics & Tracing Q9/Q28/Q37 точечно обновлены по официальным OpenTelemetry Sampling и Collector tail_sampling docs. Q9 остался вводным, Q28 стал отдельным production-выбором, Q37 уточняет неполноту доставки и требования к памяти/маршрутизации. Заголовок Q28, TOC, production MCQ, freshness-sidecar и точный дубликат в inventory.tsv синхронизированы; 41 Q сохранён. Markdown и structure/parity PASS; общий MCQ pre-check FAIL из-за прежних проблем файла (в том числе Q17 NEIGHBOR-LEAK), локальных hard/linkage-сигналов Q9/Q28/Q37 нет. Это авторская `О`, не полная `П` и не независимая MCQ-приёмка. В Q24/Q27 остаются широкие формулировки о tail sampling для будущей проверки.

Java Collections Q19/Q43 разведены по смыслу: Q19 показывает реализацию LRU на LinkedHashMap, Q43 — риски конкурентного доступа и обхода. Сверены официальные Java SE 25 LinkedHashMap и Collections.synchronizedMap, а также Caffeine Eviction. Номера и 46 Q/46 MCQ сохранены; TOC, production JSON, freshness-sidecar и поле дубля в inventory.tsv обновлены. Markdown, structure/parity PASS, tell/linkage 0. Общий MCQ pre-check FAIL на прежних Q37/Q40 skel hard, локальных сигналов Q19/Q43 нет. Авторская точечная `О` выполнена, полная `П` и независимая MCQ-приёмка не выполнены.

Kotlin Q5/Q30 оказались ложным точным дублем из-за удаления операторов при нормализации заголовков: Q5 про `?.`/`!!`, Q30 про `==`/`===`. Теория и B0 MCQ точечно обновлены по официальным Kotlin Null safety, Equality и Kotlin 1.4 docs. Q5 различает ожидаемый null и доказанный ненулевой инвариант; Q30 ограничивает `==` контрактом equals конкретного класса и убирает интернированные строковые литералы как пример ссылочного равенства. Номера, 45 Q/45 MCQ сохранены; freshness-sidecar и поле дубля в inventory.tsv обновлены. Markdown, структура/parity PASS; общий MCQ pre-check FAIL на старых блоках, локальных hard/linkage-сигналов Q5/Q30 нет. Это авторская `О`, не полная `П` и не независимая приёмка.

Metrics & Tracing Q24/Q27 и B0 MCQ точечно исправлены по OpenTelemetry Sampling, Java SDK configuration, Attributes Processor, Collector architecture и Span Metrics Connector. Q24 теперь отвечает за поток до хранилища: лимиты span'ов задаются в SDK, а удаление атрибута в Collector не экономит трафик приложения до него. Q27 отвечает за retention и метрики: span_metrics получает поступившие span'ы до tail_sampling, иначе выборка смещает статистику; ранний head sampling остаётся ограничением. Универсальные 7–14 дней и гарантия сохранения всех ошибок убраны. Заголовок/TOC Q24, JSON и freshness-sidecar синхронизированы, 41 Q/41 MCQ, инвентарь прежний. Markdown/structure/parity PASS, локальных hard/linkage-сигналов Q24/Q27 нет; общий pre-check FAIL на других блоках, Q27 имеет диагностический skel-сигнал. Авторская `О`, не полная `П` и не независимый `FINAL`.

Java Collections Q20/Q46 и B0 MCQ точечно исправлены по Java SE 25 WeakHashMap API и Caffeine Eviction. Q20 объясняет слабые ключи и негарантированный момент очистки, Q46 — сильную ссылку из значения к ключу, ненадёжность GC как TTL и отличие Caffeine weakKeys по сравнению ключей. Удалены повтор кода с System.gc, обобщение про interned String и безусловная очистка listener registry. Заголовок/TOC Q46, production JSON и freshness-sidecar синхронизированы; 46 Q/46 MCQ, инвентарь прежний. Markdown/structure/parity и tell/linkage PASS; общий MCQ pre-check FAIL на старых Q37/Q40, у Q20 диагностический skel-сигнал. Авторская `О`, не полная `П` и не независимая приёмка.

Java Modules Q14–Q16/Q31 и MCQ B0 точечно исправлены по Java SE 25 JLS, ModuleDescriptor, JAR specification и параметрам `java`/`jar`. Q16 оставлен таблицей, Q31 теперь посвящён миграции конкретного classpath JAR; неверные утверждения о `requires transitive` и безусловном запрете runtime-чтения unnamed устранены. Markdown/structure/parity/linkage PASS, общий MCQ pre-check FAIL на прежних Q5/Q7/Q18/Q19/Q26/Q27/Q29/Q34; у изменённых Q skel hard нет. 38 Q/38 MCQ, инвентарь без изменений. Это авторская `О`; независимая SFPL/ANSC/RU и blind-приёмка `NOT_RUN`, полная `П` файла не проводилась.

Java Modules Q13/Q17/Q32/Q33 и MCQ B0 точечно исправлены по официальной Java SE 25 документации. Q17 теперь механизм конфликта двух экспортёров для одного читателя, Q32 — диагностика и устранение; устаревший модуль JDK 10, неверное назначение `jdeps --check` и ошибочный статус `--illegal-access` удалены. Q33 проверяет фазу применения параметров и не повторяет Q5. Markdown/structure/parity/tell/linkage PASS; общий MCQ pre-check FAIL на прежних Q5/Q7/Q18/Q19/Q26/Q27/Q29/Q34, локальных hard нет. 38 Q/38 MCQ, инвентарь прежний. Это авторская `О`; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` файла не проводилась.

Java Modules Q4/Q5/Q12 и MCQ B0 точечно исправлены по JLS Java SE 25, AccessibleObject и Oracle JDK Migration Guide. Q4 уточняет границы classpath/module path и смешанного запуска, Q5 — runtime-доступ при `opens` без compile-time экспорта, Q12 — переходные режимы Java 9–16 и сильную инкапсуляцию с Java 17. Markdown/structure/parity/tell/linkage PASS; общий MCQ pre-check FAIL на прежних Q7/Q18/Q19/Q26/Q27/Q29/Q34, локальных hard нет. 38 Q/38 MCQ, инвентарь прежний. Это авторская `О`; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, полная `П` файла не проводилась.

Java Modules Q18/Q19/Q34 и B0 MCQ точечно исправлены по Java SE 25 JAR File Specification, jlink и jdeps. Q18 допускает корневой или версионный дескриптор MRJAR, Q19 объясняет назначение образа, Q34 даёт практическую сборку модульного приложения. Неверные размеры, обязательный jdeps с --ignore-missing-deps и автоматическое включение classpath-JAR удалены. Markdown/структура/parity/tell/linkage PASS; общий MCQ pre-check FAIL на старых skel hard Q7/Q26/Q27/Q29, локальных hard нет. 38 Q/38 MCQ, инвентарь прежний. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q20/Q26/Q27 и B0 MCQ точечно исправлены по Oracle jlink Java 25, Spring Framework 7.0, Spring Boot executable JAR, Hibernate ORM 7.1 и JLS. Q20 убирает универсальное значение --compress и ошибку о транзитивных модулях без --bind-services; Q26 разделяет exports/opens по действительным операциям Spring; Q27 различает field/property access и не обещает строго стартовую ошибку. Q18 B0 дополнительно выровнен по форме. Markdown/structure/parity/tell/linkage для изменённого пакета PASS; общий pre-check FAIL на старых skel hard Q7/Q29, Q33 имеет tell-сигнал. 38 Q/38 MCQ, инвентарь прежний. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q7/Q8/Q29 и B0 MCQ точечно исправлены по JLS Java SE 25, ModuleDescriptor, javac, FindException и Configuration.resolve. Q7 разделяет читаемость и экспорт; Q8 уточняет requires static без ложной гарантии разрешения модуля и без смешения с annotation processing; Q29 диагностирует поиск, разрешение и рефлексивный доступ по разным симптомам. Markdown/structure/parity и локальные tell/linkage PASS, structural subcheck PASS (hard 0); общий MCQ pre-check exit=1 из-за прежнего TELL Q33 и иных диагностических сигналов. 38 Q/38 MCQ, инвентарь прежний. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q9/Q30/Q33 и B0 MCQ точечно исправлены по JLS Java SE 25, Oracle Java 25 launcher/Migration Guide, Inside Java о Leyden и FFM API. Q9 разделяет runtime-доступ и компиляцию для open module. Q30 с новым заголовком объясняет реальные границы JPMS без прогнозов о Leyden/Panama; `--illegal-access` в Java 25 принимается, но игнорируется с предупреждением. Q33 MCQ лишён прежней подсказки формы. TOC и question_text Q30 синхронизированы; 38 Q/38 MCQ, инвентарь прежний. Markdown/structure/parity/tell/linkage pre-check PASS, общий MCQ pre-check exit=0. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q21/Q22/Q23 и B0 MCQ точечно исправлены по JLS Java SE 25, Gradle Java Library Plugin, Maven Compiler/Surefire и ModuleLayer API. Q21 описывает Bottom-Up/Top-Down как условные порядки, Q22 проверяет фактический module path и непрозрачные classpath-JAR, Q23 объясняет создание загрузчика слоя без обещания автоматической выгрузки. 38 Q/38 MCQ, инвентарь прежний. Markdown/structure/parity/tell/linkage pre-check PASS, общий MCQ pre-check exit=0. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q24/Q25/Q35 и B0 MCQ точечно исправлены по Configuration/JLS Java SE 25, Gradle Testing Java Modules, Maven Surefire JPMS и jdeps. Убраны вымышленные диагностические выводы и универсальные флаги тестирования; описаны classpath/blackbox/whitebox и пределы статического анализа. 38 Q/38 MCQ, инвентарь прежний. Markdown/structure/parity/tell/linkage и общий pre-check PASS. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q36/Q37/Q38 и B0 MCQ точечно исправлены по ServiceLoader/JLS/JAR specification Java SE 25, Spring Framework 7.0, AccessibleObject, Jackson 3 module-info, jdeps и jlink. Q36 разделяет classpath/module path и создание провайдера, Q37 различает причины ошибок доступа, Q38 убирает повтор Q21 и неверный `jdeps --check`. 38 Q/38 MCQ, инвентарь прежний. Markdown/structure/parity/tell/linkage и общий pre-check PASS. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q1/Q2/Q3 и B0 MCQ точечно исправлены по JLS/Configuration/ModuleDescriptor/java.sql/jdk.jlink/jlink Java SE 25. Q1 ограничивает обещания о конфликтах версий, Q2 различает явный и automatic module и уточняет `requires static`, Q3 показывает реальные связи JDK без приблизительного числа модулей. 38 Q/38 MCQ, инвентарь прежний. Markdown/structure/parity/tell/linkage и общий pre-check PASS. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q6/Q10/Q11 и B0 MCQ точечно исправлены по JLS/ModuleDescriptor/ServiceLoader/DriverManager/JAR specification Java SE 25. Q6 уточняет qualified-доступ для named-получателей, Q10 ограничивает обещания поиска и создания провайдеров, Q11 разделяет named/unnamed/automatic и load(service)/load(layer, service). 38 Q/38 MCQ, инвентарь прежний. Markdown/structure/parity/tell/linkage и общий pre-check PASS. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q28 и B0 MCQ исправлены по JLS/Configuration/ServiceLoader Java SE 25: практики привязаны к контракту и конкретному графу, учебные дескрипторы скомпилированы с минимальными типами. Адресно проверены ссылки и известные прежние категоричные формулы по Q1–Q38; это не полная `П`. 38 Q/38 MCQ, инвентарь прежний. Markdown/structure/parity/tell/linkage и общий pre-check PASS. Авторская `О`; независимая приёмка и полная `П` — `NOT_RUN`.

Java Modules Q1–Q5 прошли авторскую полную содержательную `П` по JLS/Configuration/ModuleDescriptor Java SE 25, страницам модулей JDK и изменениям JDK 11. Уточнены classpath-видимость в Q1, директивы и сочетание `requires static transitive` в Q2, дата отделения JavaFX в Q3 и разница `opens`/`opens ... to` в Q5; Q4 проверен без содержательной правки. Парный MCQ Q1–Q5 B0 синхронизирован и автором проверен по вариантам и sections. 38 Q/38 MCQ, инвентарь прежний; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

Java Modules Q6–Q9 прошли авторскую полную содержательную `П` по JLS/правилам разрешения модулей/java launcher Java SE 25. Q6 уточняет ненаблюдаемый при сборке адресат `to`, Q7 исправляет диаграмму, Q8 показывает runtime-поведение `requires static` и проверку графа, Q9 подтверждён без содержательной правки. Дескрипторы Q6/Q7/Q9 скомпилированы локальным `javac`; отдельный пример Q8 показал появление optional module в графе только после `--add-modules`. Парный MCQ Q6–Q9 B0 сверён по вариантам и sections, пояснения Q6/Q8 синхронизированы. 38 Q/38 MCQ, инвентарь прежний; Markdown/structure/parity и общий pre-check PASS; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

Java Modules Q10–Q13 прошли авторскую полную содержательную `П` по ServiceLoader, AccessibleObject, java launcher, руководству миграции JDK и JLS Java SE 25. Q10 уточняет `ServiceConfigurationError` без `uses`, Q11 — context class loader и поиск named/unnamed, Q12 — границы диагностического примера и предупреждение Unsafe с JDK 24, Q13 — `--show-module-resolution`. Пересечения с Q5/Q9/Q33/Q36 сверены. На локальном JDK 22 воспроизведены Q10 с/без `uses` и ошибка Q12. Парный MCQ Q10–Q13 B0 сверён по A/B/C/D, дистракторам и sections, пояснения синхронизированы. 38 Q/38 MCQ, инвентарь прежний; Markdown/structure/parity и общий pre-check PASS; независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

Java Modules Q14–Q17 прошли авторскую полную содержательную `П` по JLS/ModuleDescriptor/ModuleFinder/JAR specification/Configuration Java SE 25. Q14 подтверждён без правки теории. Q15 ограничивает automatic module допустимым именем и содержимым JAR; Q16 отделяет явный именованный модуль от automatic и исправляет заголовок, оглавление, таблицу регистрации провайдеров; Q17 добавляет конфликт собственного пакета читающего модуля. На локальном JDK 22 `jar --describe-module` подтвердил имена `gson` и `commons.lang3`, для `42.jar` сообщил о недопустимом имени. Production MCQ Q14–Q17 B0 сверён по A/B/C/D, дистракторам и sections; формулировки и пояснения синхронизированы. 38 Q/38 MCQ, инвентарь прежний; Markdown/structure/parity и общий pre-check PASS после исправления OPTION_LENGTH_RATIO Q16. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

Java Modules Q18–Q20 прошли авторскую полную содержательную `П` по JAR specification/jlink/jdeps/Configuration Java SE 25. Q18 уточняет одинаковый экспортируемый API MRJAR, отсутствие обязательной runtime-проверки и запрет versioned `META-INF/services`; Q19 — предел связывания automatic modules; Q20 — значения `--compress=0/1/2` для JDK 25 и охват `--bind-services` используемыми сервисами. Соседние Q3/Q34/Q36 сверены. На локальном JDK 22 `jlink` отверг automatic module, а явный учебный модуль успешно связан и запущен. Production MCQ Q18–Q20 B0 сверён по A/B/C/D, дистракторам и sections, пояснения синхронизированы. 38 Q/38 MCQ, инвентарь прежний; Markdown/structure/parity и общий pre-check PASS после исправления OPTION_LENGTH_RATIO Q20. Независимая SFPL/ANSC/RU/blind-приёмка `NOT_RUN`, не `FINAL`.

Java Modules Q21–Q23 прошли авторскую полную `П` по JLS Java SE 25, Gradle, Maven Compiler/Surefire и ModuleLayer/Configuration/ServiceLoader. Исправлены условие `inferModulePath` для Gradle, границы `jdeps` и требования к использованию типов плагина; Markdown и production MCQ синхронизированы, общий pre-check PASS, независимая MCQ-приёмка `NOT_RUN`.

Java Modules Q24–Q27 прошли авторскую полную `П` по Java SE 25, Gradle, Maven Surefire, Spring Framework 7, Spring Boot, Hibernate ORM 7.1 и Jakarta Persistence 3.2. Исправлены границы `ServiceLoader` при цикле, запуск тестов через Gradle/IDE, описание Boot JAR и требования к сущности. Q27 — Hibernate/JPA, а не `ServiceLoader`. Markdown и production MCQ синхронизированы, общий pre-check PASS, независимая MCQ-приёмка `NOT_RUN`.

Java Modules Q28–Q30 прошли авторскую полную `П` по JLS/ServiceLoader/FindException/Configuration и launcher Java SE 25. Уточнены требования к провайдеру, причины FindException, границы --illegal-access и native access. Markdown и production MCQ синхронизированы, общий pre-check PASS, независимая MCQ-приёмка `NOT_RUN`.

Java Modules Q31–Q33 прошли авторскую полную `П` по ModuleFinder/Configuration, JLS 7.7.5 и java/javac Java SE 25. Исправлены границы automatic module, различие --validate-modules и --dry-run, а также ограничение --add-exports при javac --release. Markdown и production MCQ синхронизированы, общий pre-check PASS, независимая MCQ-приёмка `NOT_RUN`.

Java Modules Q34–Q36 прошли авторскую полную `П` по jlink/jdeps, ServiceLoader и JLS Java SE 25. Уточнены разделитель module path, подписанные JAR, ограничение --generate-module-info и создание провайдера в automatic module. Markdown и production MCQ синхронизированы, общий pre-check PASS, независимая MCQ-приёмка `NOT_RUN`.

Java Modules Q37–Q38 прошли авторскую полную `П` по первичным источникам Spring Framework, Hibernate ORM, Jackson 3 и Java SE 25. Сверены соседние Q21/Q26/Q27/Q31/Q33/Q34; Markdown и production MCQ синхронизированы. Обе проверки структуры и общий MCQ pre-check PASS; авторская `П` Q1–Q38 завершена. Независимая MCQ-приёмка `NOT_RUN`, файл не `FINAL`.

PostgreSQL Q1–Q4 прошли авторскую полную `П` по официальной документации PostgreSQL 17/18. Уточнены выборка клиентских backend, изменение `shared_buffers`, условия PITR и диагностика checkpoint с различием счётчиков PostgreSQL 17/18. Markdown и production MCQ синхронизированы; `verify-md-no-mcq`, JSON structure/parity и общий MCQ pre-check PASS. Замечания общего pre-check к 11 другим блокам не затрагивают Q1–Q4. Независимая MCQ-приёмка `NOT_RUN`, файл не `FINAL`.

PostgreSQL Q5 и Q49–Q50 прошли авторскую полную `П` по официальной документации PostgreSQL 18 и PgBouncer 1.26.1. Уточнены содержимое и reload `pg_hba.conf`, отдельная аутентификация PgBouncer и ограничение отслеживания `search_path`. Markdown и production MCQ синхронизированы. `verify-md-no-mcq`, JSON structure/parity и общий MCQ pre-check PASS; к пакету замечаний pre-check нет, к 9 другим блокам файла остались. Независимая MCQ-приёмка `NOT_RUN`, файл не `FINAL`.

PostgreSQL Q6–Q10 прошли авторскую полную `П` по официальной документации PostgreSQL 18. Уточнены диапазоны и `EXCLUDE`, строгий ввод `jsonb`, размеры массива и `ANY` с `NULL`, индексы `hstore`, миграция `ENUM` и ограничения composite. Сверен Q48. Markdown и production MCQ синхронизированы; `verify-md-no-mcq`, JSON structure/parity и общий MCQ pre-check PASS. К Q6–Q10 замечаний pre-check нет, к 9 другим блокам файла остались. Независимая MCQ-приёмка `NOT_RUN`, файл не `FINAL`.

PostgreSQL Q11–Q15 прошли авторскую полную `П` по официальной документации PostgreSQL 18. Уточнены bloom, смешанный ORDER BY, GIN pending list, GiST nearest-neighbor и BRIN summarization. Сверены Q16–Q19/Q20–Q24. Markdown и production MCQ синхронизированы; `verify-md-no-mcq`, JSON structure/parity и общий MCQ pre-check PASS. К Q11–Q15 замечаний pre-check нет, к 9 другим блокам файла остались. Независимая MCQ-приёмка `NOT_RUN`, файл не `FINAL`.

PostgreSQL Q16–Q19 прошли авторскую полную `П` по документации PostgreSQL 18 и границе `NULLS NOT DISTINCT` с PostgreSQL 15. Уточнены условная уникальность, `IMMUTABLE`, `Heap Fetches`, мониторинг и ограничения concurrent-операций. Сверены Q11–Q15/Q20–Q24. Markdown и production MCQ синхронизированы; `verify-md-no-mcq`, JSON structure/parity и общий MCQ pre-check PASS. К Q16–Q19 замечаний pre-check нет, к 9 другим блокам файла остались. Независимая MCQ-приёмка `NOT_RUN`, файл не `FINAL`.

PostgreSQL Q20–Q24 прошли авторскую полную `П` по PostgreSQL 18. Q20 уточняет сравнение `rows`/`actual rows` за запуск и вложенные `Buffers`, Q21 — замер времени и `SERIALIZE`, Q23 — exact/lossy bitmap и повторную проверку. Q22/Q24 содержательно сверены. Production MCQ Q20/Q21/Q23 B0 синхронизированы; все варианты Q20–Q24 автором перечитаны. Markdown, JSON structure/parity и общий MCQ pre-check PASS; Q20 B0 имеет мягкое предупреждение о числе частей, hard-сигналов в пакете нет, в целом файле 10 блоков вне мягкого коридора. Независимая MCQ-приёмка `NOT_RUN`, файл не `FINAL`, инвентарь прежний.

PostgreSQL Q25 и Q53–Q54 прошли авторскую полную `П` по PostgreSQL 18. Q25 уточняет `CREATE STATISTICS`, `ANALYZE` и `dependencies`, Q53 — связь `shared_buffers` с `max_wal_size`, Q54 — нормализованные группы `pg_stat_statements`, `stats_reset`/`dealloc` и `track_planning`. Production MCQ Q25/Q53/Q54 B0 синхронизированы; A/B/C/D и sections проверены автором. Markdown, JSON structure/parity и общий MCQ pre-check PASS; в пакете нет замечаний pre-check, девять иных блоков файла вне мягкого коридора. Независимая MCQ-приёмка `NOT_RUN`, файл не `FINAL`, инвентарь прежний.

PostgreSQL Q26–Q30 прошли авторскую полную `П` по PostgreSQL 18. Q26 исправляет пример `SET TRANSACTION` вне `BEGIN` и разграничивает конкурирующий UPDATE; Q27 учитывает HOT и очистку версий; Q28 — блокировку и MultiXact в `xmax`; Q30 — блокировки и курсоры при savepoint. Q29 содержательно сверен. Production MCQ Q26–Q28/Q30 B0 синхронизированы; все варианты Q26–Q30 автором перечитаны. Markdown, JSON structure/parity и общий MCQ pre-check PASS, замечаний к пакету нет, восемь иных блоков вне мягкого коридора. Независимая MCQ-приёмка `NOT_RUN`, файл не `FINAL`, инвентарь прежний.

Точный следующий пакет: при отдельном проверяющем Spring Batch Q6 B0/B1, Q28 B0, Q38 B0 на независимую MCQ-приёмку. Иначе авторская полная содержательная `П` PostgreSQL Q31–Q34 (`VACUUM`, `VACUUM FULL`, autovacuum, bloat): сверить Q27–Q28 и ближайшие ответы, границы PostgreSQL 18 по официальной документации, синхронизировать Markdown с paired production MCQ и отдельно записать структурные и содержательные проверки. Java Modules Q1–Q38 ждут независимой MCQ-приёмки; PostgreSQL 19 — после GA 19.11.2026.

Независимую приёмку Batch продолжать по новому blind-input: фаза A,
reveal, фаза B, CONTENT/FACTS/LINKAGE/SECTIONS/SFPL/ANSC/RU и record.
После Batch продолжить независимую приёмку Uber Q6/Q7, Maps Q27,
Spring Framework Q1/Q2/Q9/Q10 B0/B1 и
Database Transactions Q15/Q22/Q23/Q43 B0,
Spring Rest Client Q4/Q6/Q7/Q11/Q12/Q13 B0
и Java CompletableFuture
Q2 B0/Q3 B0/B1/Q4 B0/Q6 B0/Q8 B0/B1/Q10 B0. Не записывать авторскую
самопроверку как независимую фазу A/B.

## Как работать с каждой группой

- Сначала сравни идею с каталогом, оглавлениями и заголовками; затем точечно
  прочитай соседние ответы. Разделяй З, О и П. Не считай отсутствие термина
  в заголовке доказательством пробела.
- Для решения укажи ближайший файл и Q, конкретный непокрытый сценарий,
  приоритет, статус, официальный источник и нужную версионную границу.
  Возможные исходы: COVERED, DEEPEN, ADD_Q, NEW_TOPIC, OUT_OF_SCOPE.
- Для авторинга сохраняй стиль, frontmatter, стабильные номера Q, локальное
  оглавление, ссылки и See also. Новая тема требует нескольких отличимых
  backend-вопросов, иначе используй существующий файл.
- Для задачи на разбор кода, запроса, лога или схемы помести входные данные
  в парный Markdown. Тренажёр берёт первый подходящий fenced-блок кода и
  первую Mermaid-схему из тела Q. Используй их для материалов, на которые
  ссылается видимый заголовок. Укажи в заголовке существенные условия, а в
  материалах оставь только вводные, по которым ученик сам выводит ответ.
  Если пример решения может стать первым извлекаемым блоком и раскрыть
  ключ, изложи его в тексте или передай его автору MCQ для разбора после выбора.
- При смысловом изменении Q передай координатору путь к Markdown, парному
  production JSON и суть изменения. Автор MCQ получает всю парную теорию,
  полный исходный JSON и [единый промпт](PROMPT_PLAN_INTERVIEW.md) одной
  задачей. Модель заново генерирует каждый вопрос, все блоки, варианты и
  пояснения и возвращает один полный JSON для прямой замены. Результат
  сохраняется как есть. Порядок описан в
  [workflow](docs/mcq-quality/workflow.md).
- Обнови TOC/README и inventory.tsv при изменении состава файлов.
  PLAN_CHEATSHEETS.md обновляй после каждой группы: написано, проверено,
  только кандидат, MCQ в ожидании приёмки и точный пункт продолжения.
- Факты каждого Q сверяй с официальной документацией. Свежие публикации за
  последние шесть месяцев ищи в Baeldung, официальных блогах и документации
  проектов; сверяй дату первоначальной публикации и статус релиза. Статьи
  служат источником сценария, их текст не копируй. В старом плане есть
  просмотр заголовков архива Baeldung за апрель—сентябрь и выборка
  официальных публикаций; не выдавай это за полный просмотр всех статей.
- Зафиксируй COVERED и OUT_OF_SCOPE, чтобы новая публикация не плодила
  дубли. Область теперь шире backend: AI/ML, тестирование, архитектура,
  безопасность и инфраструктура также входят в поиск. Ранее явные исключения
  Rust, JavaScript, Node.js, TypeScript и фронтенда сохраняй, пока пользователь
  не уточнит их отмену.

В финале перечисли созданные и изменённые файлы, проведённые проверки,
официальные источники, непроверенные гипотезы, ограничения и тематические
коммиты. Работай
последовательными небольшими группами, продолжая следующую, пока хватает
контекста. Если прервёшься, оставь в плане точный файл/Q для продолжения.
