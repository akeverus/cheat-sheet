# Продолжение авторинга interview-шпаргалок

Этот промпт самодостаточен для следующего этапа в локальном репозитории
/Users/sergeyvoronin/IdeaProjects/cheat-sheet. История чата служит контекстом
решений, но актуальные факты проверяй по файлам. Пользователь разрешил
изменять и создавать шпаргалки и парные production MCQ JSON. Не останавливайся
на обновлении плана. Последнее поручение: после завершения работы закоммитить
все изменения репозитория отдельными тематическими коммитами, включая ранее
существовавшие, предварительно разобрав их состав и проверки. Push не запрошен.

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

## Текущий проверенный срез на 05.10.2026

В каталоге 336 тематических Markdown-файлов и 9 913 заголовков Q в 25
разделах; 335 файлов имеют парный production MCQ JSON. Семнадцать новых тем
в текущем этапе: Python core, Python concurrency, Python testing, FastAPI,
MySQL, JDBC/пулы, Java 26–27, Python typing, Python packaging и
System Design Unique ID Generator, Notification System, Hotel Reservation System,
Object Storage, Gaming Leaderboard, Metrics Platform, Ad Click Event Aggregation
и Distributed Email Service.
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

Начни с P3-кандидата Digital Wallet из раздела `system-design-notes` в плане.
`design-payment-system-interview.md` Q5/Q8/Q22 уже прочитаны точечно; сравни
транзакции БД и Q по ledger/event sourcing. По документации TigerBeetle
и PostgreSQL, указанной в плане, проверь несколько самостоятельных
сценариев перевода между кошельками и реши `NEW_TOPIC` против `DEEPEN`.
Ad Click Event Aggregation и Distributed Email Service Q1–Q4 написаны,
их MCQ требуют независимой приёмки.
RabbitMQ/Kafka/Hibernate, новые Java и Python Q требуют независимого blind
review MCQ; старый parity/pre-check backlog Hibernate, Spring Data JPA,
Java IO/NIO, Jackson, URL Shortener, RabbitMQ, Kafka, сравнение брокеров и
event-driven открыт. Ad Click Event Aggregation и Distributed Email Service Q1–Q4, Metrics Platform Q1–Q4, Leaderboard Q1–Q5, Object Storage Q1–Q4,
Reservation System Q1–Q4, Notification System Q1–Q4, RabbitMQ Streams Q40–Q42
и Spring Data JDBC Q17 тоже ждут
содержательной приёмки.

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
