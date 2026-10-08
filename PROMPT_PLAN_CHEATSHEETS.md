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
Python 3.15 и PostgreSQL 19 перепроверять после GA; Stock Exchange остаётся кандидатом до подтверждения ценности.
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

Следующий авторский пакет: Spring Data JPA Q36 B0 и парный JSON.
В примере `deleteByDeletedAtIsNotNull()` ошибочно подписан как
прямой SQL DELETE; Q33 уже объясняет derived delete через выборку
и пообъектное удаление. Сверить Q36/Q33 с официальными query methods,
исправить подтверждённое, затем пройти Q37.
Независимую MCQ-приёмку
вести отдельно: ближайший
пункт для другого контекста — Spring Batch Q6 B0/B1, Q28 B0, Q38 B0,
затем Uber Q6/Q7, Maps Q27, Spring Framework Q1/Q2/Q9/Q10 B0/B1 и
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
- Каждый добавленный или изменённый Q синхронизируй с парным production JSON
  по номеру. Проверь правильный ответ, правдоподобие и различимость
  дистракторов, пояснения к каждому варианту, соответствие Markdown.
  Выполни структурный валидатор и parity-гейт, но не выдавай их за независимую
  содержательную приёмку. Если gate всего старого файла падает из-за ранее
  существующих блоков, зафиксируй это отдельно.
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
