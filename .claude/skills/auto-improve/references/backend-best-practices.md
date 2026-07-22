# Backend best-practices (справочник для auto-improve)

Источник: параллельный ресёрч 7 направлений (Spring Boot 3 / Java 21 / тесты / security / observability / persistence-Flyway / дисциплина рефакторинга) с проверкой против фактического состояния quiz-app. Использовать в Фазе 1 (Анализ) как источник детерминированных grep-сигналов и в Фазе 2 (Приоритизация) как worst-first лестницу.

> Каждый пункт: **Проверить** — команда/grep-сигнал, дающий находку; **Улучшить** — конкретная правка под стек. Брать по одному measurable-смеллу за цикл.

---

## Backend best-practices чеклист

Каждый пункт: **Проверить** (детерминированный сигнал для loop) → **Улучшить** (правка под стек SB 3.3.2 / Java 21 / JdbcTemplate / PostgreSQL 16 / Flyway / Testcontainers / ArchUnit). Стек: seed-first (AI вырезан), multi-user с owner-scoping, Thymeleaf MVC + JSON REST.

### Spring (Boot 3 / Framework 6)

- **Транзакции на границе use-case, не на репозиториях.**
  Проверить: `@Transactional` на методах `modules/quiz-persistence/**Repository.java` (сейчас ~14 мест: `QuestionRepository.insert/update/updateImportant/updateCodeSnippet`, `ReviewStateRepository` и др.).
  Улучшить: снять аннотацию с одиночных write-стейтментов (они атомарны сами по себе), поднять на публичные методы сервисов/usecase, оркестрирующих >1 репо-вызова (`AttemptRecorder`, `ReviewService`, `SubmitAnswerCommand`, `InterviewService`); чисто читающие методы — `@Transactional(readOnly=true)`. Закрепить ArchUnit-правилом «классы в `..persistence..` не аннотированы `@Transactional`».

- **`@ConfigurationProperties` — immutable record + constructor binding.**
  Проверить: `AppProperties` и вложенные `Interview/Cache/Search/Security/Import/Scheduling` — mutable `@Component` с Lombok `@Getter/@Setter`.
  Улучшить: переписать в record'ы (или final-поля + один конструктор), убрать `@Component/@Getter/@Setter`, оставить `@ConfigurationProperties(prefix="app")` + `@Validated` + jakarta-констрейнты, дефолты через `@DefaultValue`, вложенные — `@NestedConfigurationProperty`; на `@SpringBootApplication` добавить `@ConfigurationPropertiesScan`. Loop-сигнал: config-класс с `@Component` или setter'ами.

- **Ошибки API через `ResponseEntityExceptionHandler` + `ProblemDetail`, а не ручной перехват framework-исключений.**
  Проверить: `GlobalExceptionHandler` вручную ловит `MethodArgumentNotValidException/BindException/MethodArgumentTypeMismatchException`; поле `type` у ProblemDetail = `about:blank`; флаг `spring.mvc.problemdetails.enabled` в yml отсутствует.
  Улучшить: включить `spring.mvc.problemdetails.enabled: true`; разнести один advice на `@RestControllerAdvice extends ResponseEntityExceptionHandler` для `/api` + `/export` (переопределять только доменные `QuestionNotFound/OptionNotFound/RevisionMismatch/DataIntegrity`, валидацию отдать базовому классу — он покрывает и `HttpMessageNotReadable/HttpRequestMethodNotSupported/HandlerMethodValidationException`) и отдельный MVC-advice под `error.html`/redirect; в `apiErrorResponse()` проставлять `problem.setType(URI.create("https://quiz.local/errors/"+type))` из `ApiErrorTypes`, сохранив `correlationId`.

- **Валидация на границе: `@Valid` на `@RequestBody`, `@Validated` на контроллере для `@RequestParam/@PathVariable`.**
  Проверить: `handleConstraintViolationException` — мёртвый код, если контроллеры не помечены `@Validated`; request-DTO (`ReportIssueRequest/StartSessionRequest/SubmitAnswerRequest/SubmitAttemptRequest`) без jakarta-констрейнтов, проверки размазаны по сервисам.
  Улучшить: перенести инварианты в аннотации DTO (`@NotBlank/@Size/@Min/@Max/@Pattern`); каждый `@RequestBody` — `@Valid`; контроллеры с параметрами-констрейнтами — `@Validated`. ArchUnit/grep: контроллер с параметром-констрейнтом обязан иметь `@Validated`; request-DTO имеет ≥1 констрейнт.

- **Мигрировать JdbcTemplate → JdbcClient инкрементально.**
  Проверить: 37 использований `JdbcTemplate`, 0 `JdbcClient` (автоконфигурируется в Boot 3.2+ при наличии `NamedParameterJdbcTemplate`).
  Улучшить: начать с самых опасных позиционных INSERT/UPDATE (`QuestionRepository.insert/update` — 17 колонок) → `.sql(...).param("slug", ...).update(keyHolder)`; RowMapper'ы переиспользуются как есть. Метрика прогресса: убыль grep-счётчика `JdbcTemplate` против роста `JdbcClient`. **Осторожно:** это не меняет транзакционную модель — не смешивать с транзакционным рефактором в одной итерации.

### Java 21 идиомы и чистый код

- **Java 21 toolchain + virtual threads** (объединяет два дименшена).
  Проверить: `build.gradle.kts` фиксирует `VERSION_17`, хотя рантайм-цель — 21; `spring.threads.virtual.enabled` в yml не выставлен. Стек синхронно-блокирующий (JdbcTemplate + Thymeleaf/Tomcat) — идеальный профиль.
  Улучшить: поднять toolchain до `JavaLanguageVersion.of(21)` в корневом `build.gradle.kts` (единая точка); `spring.threads.virtual.enabled: true`. **Аудит pinning:** `synchronized` держать на 0 (заменять на `ReentrantLock` — 1 уже есть, верный паттерн); `ThreadLocal` для request-scoped данных → `ScopedValue`; при scheduled-задачах ставить keep-alive; задать явный `hikari.maximum-pool-size` (пул станет главным лимитом). Нагрузочный прогон с `-Djdk.tracePinnedThreads=short`.

- **`orElseGet` вместо `orElse` для вычисляемых дефолтов.**
  Проверить: `.orElse(` — 33 вхождения, `.orElseGet(` — 1. Дефекты: `ReviewService.java:~59/83/103/156/193` (`.orElse(ReviewDefaults.initialState(...clock.instant()...))` исполняется на КАЖДЫЙ вызов), `InterviewService.java:384`.
  Улучшить: если аргумент `orElse` — вызов метода/`new`/обращение к `clock` → `.orElseGet(() -> ...)`. Оставлять `orElse` только для готовых констант (`.orElse(false)`, `.orElse("")`).

- **Защитное копирование коллекций в compact-конструкторе record.**
  Проверить: record'ы домена с полями `List/Map/Set`, валидирующие только `Objects.requireNonNull` без `List.copyOf` (`InterviewQuestion.options`, `AnswerResult.options`, `InterviewFilter`, `ContentBlock`).
  Улучшить: добавить `options = List.copyOf(options);` в compact-конструктор (заодно неизменяемость наружу и NPE на null-элементах — желаемо).

- **Sealed-иерархия record'ов вместо enum-тег + пачка nullable-полей.**
  Проверить: `sealed` в проекте 0 вхождений; `ContentBlock` = `BlockType` + `body/language/caption/accessibilityDescription`, где часть полей null в зависимости от типа.
  Улучшить: `sealed interface ContentBlock permits MarkdownBlock, CodeBlock, MermaidBlock, ImageBlock` + record'ы только со своими полями → exhaustive switch в маппере/рендере без `default`/instanceof-цепочек.

- **Pattern matching for switch / instanceof-pattern.**
  Проверить: 17 `instanceof`, 7 `switch`. `if (x instanceof Foo) { Foo f=(Foo)x; }`; instanceof-каскады; statement-switch по enum с `break`; `QuestionType.fromString` — ручная if-else из 6 сравнений (в отличие от `InterviewMode.fromString` через `valueOf`).
  Улучшить: `instanceof Foo f`; каскады → arrow-switch без `default` (принудительная exhaustiveness при добавлении константы в `NextActionType/BlockType/InterviewMode`); привести `QuestionType.fromString` к идиоме `InterviewMode`.

- **Text blocks для SQL.**
  Проверить: ~145 конкатенаций `" +` в `modules/quiz-persistence`, text block применён в 1 из 19 репозиториев; риск потери пробела на стыке строк (`...FROM`+`questions...`).
  Улучшить: многострочные SQL-константы → `"""..."""` (`QuestionRepository`, `ReviewStateRepository`, `AttemptRepository`, `UserTopicStatsRepository`), это заодно чинит класс багов склейки.

- **Защитить принятые идиомы от регресса.**
  Проверить (регрессия): новый `Collectors.toList()/toSet()` (сейчас 0, `.toList()` — 18); новый `return null` (14 в quiz-app); `Optional` в полях/параметрах; `isPresent()+get()`.
  Улучшить: `Collectors.toList()` → `.toList()`; «может не найтись» → `Optional`, не null; логику по enum-константе класть методом в enum, а не во внешний switch. Не разворачивать стримы там, где цикл читаемее.

### Тесты (JUnit 5, Testcontainers, JaCoCo, ArchUnit)

- **Единый abstract base + singleton-контейнер с `@ServiceConnection`** (объединяет 3 дименшена).
  Проверить: 18 `@SpringBootTest`, 0 `@WebMvcTest`; quiz-app берёт БД из `jdbc:tc:...` через `@DynamicPropertySource` (дубль в ~10 тестах), persistence руками зовёт `Flyway.configure().migrate()` в `AbstractPostgresRepositoryTest`.
  Улучшить: `abstract AbstractIntegrationTest` с `static final PostgreSQLContainer<>("postgres:16-alpine")` (singleton-`static{start()}`, переносимо на CI в отличие от `withReuse`) + `@Bean @ServiceConnection`; миграции подхватит автоконфиг (тестирует реальную Flyway-цепочку); один общий набор `@MockitoBean` для переиспользования контекста. Метрика: убыль уникальных сигнатур `@SpringBootTest/@DynamicPropertySource` (каждая новая = минус к hit-rate кэша); лог `org.springframework.test.context.cache=DEBUG`. Reuse-контур для скорости: `?TC_REUSABLE=true` + `testcontainers.reuse.enable=true` локально, узкий срез `--tests "...ИзменённыйClassTest"` на каждом шаге, полный `check` — финальный гейт.

- **Контроллеры — срез `@WebMvcTest`, репозитории — `@JdbcTest`.**
  Проверить: контроллер-тест помечен `@SpringBootTest @AutoConfigureMockMvc`, но реально использует только MockMvc + моки (`InterviewControllerApiTest`).
  Улучшить: `@WebMvcTest(InterviewApiController.class)` + `@MockitoBean` коллабораторов + `spring-security-test` (`csrf()` уже есть) + мок `SensitiveEndpointAccessService`. Repository-тесты — `@JdbcTest @AutoConfigureTestDatabase(replace=NONE) @ServiceConnection` (rollback-изоляция вместо ручного TRUNCATE-списка, который надо синхронизировать с V*.sql).

- **Не мокать: БД, доменные value-object/DTO/record.**
  Проверить: `@Mock`-поля (161 в suite), тип которых — java record / `..domain..` / `..api.dto..`.
  Улучшить: заменить реальными экземплярами (`TestQuestionBuilder`, не `mock(Question.class)`); SQL-репозитории гонять на реальном Postgres; моки — только для сервис-границ (портов между слоями).

- **Усилить JaCoCo-гейт (ratchet вверх, BRANCH, исключения).**
  Проверить: `minimum = 0.20` LINE только на 3 пакета, без BRANCH, без исключений DTO/config/Lombok.
  Улучшить: вторая rule `counter="BRANCH"` на `service.*` (`AdaptiveDifficultyService/FsrsService`/стратегии); `lombok.addLombokGeneratedAnnotation = true` в lombok.config (JaCoCo пропустит `@Generated`); исключить records/config; порог поднимать ratchet-ом (floor = факт − epsilon, монотонно вверх, никогда вниз) чтением jacoco XML.

- **Расширить ArchUnit за пределы слоёв + `freeze()` для легаси** (объединяет 2 дименшена).
  Проверить: `LayeredArchitectureTest` покрывает только dependency-границы.
  Улучшить: добавлять по одному правилу за итерацию — `controller`-классы кончаются на `Controller` и аннотированы `@Controller/@RestController`; `noFields().should().beAnnotatedWith(Autowired.class)` (конструкторная инъекция); `slices()...beFreeOfCycles()`; DTO из `..api.dto..` не в `..persistence..`; controllers не вызывают `Jdbc*Repository` напрямую; domain без Spring-аннотаций. Шумные на легаси правила оборачивать `FreezingArchRule.freeze(...)` (baseline, падают только новые нарушения). Прогон `--tests "*LayeredArchitectureTest"`.

- **Дисциплина JUnit 5 + контракт ошибок.**
  Проверить: 0 `@DisplayName`, 0 `@Nested`; из 21 `@ParameterizedTest` только 1 `@MethodSource`; для каждого `@ExceptionHandler` в `GlobalExceptionHandler` есть ≥1 тест-кейс.
  Улучшить: конвенция `@DisplayName` «<состояние> → <ожидание>»; ≥3 почти-одинаковых `@Test` → `@ParameterizedTest @MethodSource`; `@Nested` для happy/edge/error; `GlobalExceptionHandlerTest` держать на `standaloneSetup` (эталон, не `@SpringBootTest`), добавить кейс на каждый маппинг с проверкой статуса И типа из `ApiErrorTypes` через jsonPath.

### Security (Spring Security 6 + OWASP, multi-tenant)

- **PostgreSQL RLS как backstop под ручной `owner_key = ?`.**
  Проверить: изоляция держится на 49 ручных `WHERE owner_key = ?` поверх ThreadLocal `OwnerContext`; забытая WHERE = BOLA (OWASP API1:2023).
  Улучшить: миграция V27 — `ENABLE ROW LEVEL SECURITY` + `CREATE POLICY owner_isolation USING (owner_key = current_setting('app.current_owner', true))` на owner-scoped таблицах (`attempt/review_state/bookmark/user_topic_stats/daily_activity/user_experience/paused_session/question_issue`); `SET LOCAL app.current_owner = ?` из `OwnerContext.current()` (через `TransactionSynchronization`/ConnectionPreparer). Тест: убрать WHERE в одном методе → cross-owner SELECT всё равно 0 строк. Loop-сигнал: SQL по таблице из allowlist без `owner_key`.

- **Method-level authz как второй слой над URL-matcher.**
  Проверить: авторизация однослойная (request-matchers + ручной SQL); GET по чужому `{id}` внутри разрешённого эндпоинта.
  Улучшить: `@EnableMethodSecurity`; на эндпоинтах «объект по id» — `@PostAuthorize("returnObject.ownerKey == @currentUserAccessor.ownershipKey()")`; admin-действия — `@PreAuthorize("hasRole('ADMIN')")` (X-Admin-Token отдельным `AuthorizationManager`). Тест: пользователь A не читает объект B (403/404).

- **Client-IP только из доверенного прокси.**
  Проверить: `RequestRateLimiter/SensitiveEndpointAccessService` при `trustForwardedForHeader=true` берут `X-Forwarded-For.split(',')[0]` (подконтрольно клиенту → обход rate-limit на `/regenerate`).
  Улучшить: `server.forward-headers-strategy: native` + `server.tomcat.remoteip.trusted-proxies: <CIDR reverse-proxy>`; ключ = нормализованный `getRemoteAddr()`; удалить самодельный парсинг XFF. Тест: подделанный XFF от недоверенного источника не меняет rate-limit-ключ.

- **Admin-token: только заголовок, без query-param fallback.**
  Проверить: `SecurityConfig.hasSensitiveAccess()` читает `getParameter("adminToken")` (секрет в query утекает в access-логи/history/Referer). Constant-time `MessageDigest.isEqual` — уже хорошо.
  Улучшить: убрать ветку `getParameter`, оставить `X-Admin-Token`; сохранить `@PostConstruct`-guard на `APP_ADMIN_TOKEN` в prod. Grep: `getParameter("adminToken")` / `adminToken=` в шаблонах/JS. Тест: `?adminToken=` → 403, заголовок → 200.

- **CSP: добавить `object-src/base-uri/frame-ancestors/form-action`.**
  Проверить: `CspNonceFilter.buildPolicy()` выставляет script/style/img/font/connect, но пропускает `object-src 'none'`, `base-uri`, `frame-ancestors` (только устаревший X-Frame-Options), `form-action`.
  Улучшить: добавить `object-src 'none'; base-uri 'none'; frame-ancestors 'self'; form-action 'self'`; сперва `Content-Security-Policy-Report-Only` (не сломать Mermaid/hljs). Тест: CSP содержит директивы и НИКОГДА `unsafe-inline/unsafe-eval` в script-src; каждый inline `<script>/<style>` несёт nonce; nonce = 16 байт base64url, разный на запрос.

- **CSRF-cookie для SPA: Secure + SameSite=Lax, проверяемый ignore-list.**
  Проверить: `CookieCsrfTokenRepository.withHttpOnlyFalse()` без Secure/SameSite; CSRF отключён для `/export` и `/actuator/**`.
  Улучшить: `setCookieCustomizer(c -> c.secure(true).sameSite("Lax"))` (secure только в prod); зафиксировать тестом, что `/export` без admin-token = 403 (иначе CSRF-ignore = дыра); POST без `X-XSRF-TOKEN` → 403.

- **Fail-closed маппинг IdP-групп в authorities.**
  Проверить: `OrgIdpGroupAuthoritiesMapper` — пустой `admin-groups`, claim другого типа, отсутствие claim.
  Улучшить: параметризованные тесты трёх форм claim (String/String[]/Collection) + кейсы null-claim → только ROLE_USER, пустой `admin-groups` → startup-guard кидает при multi+oauth; роли пересчитываются на каждый логин (не кэшируются); ROLE_USER выдаётся всегда.

- **Actuator hardening: allowlist, env/heapdump закрыты, метрики не публичны** (объединяет Security + Observability).
  Проверить: в режиме `local` (дефолт даже для single-user prod) `anyRequest().permitAll()` открывает `/actuator/metrics` и `/actuator/prometheus` анонимно.
  Улучшить: явный `management.endpoints.web.exposure.include: health,info,prometheus` + `exclude: env,beans,configprops,heapdump,threaddump,loggers,mappings`; `health.show-details: when-authorized`; предпочтительно вынести на отдельный `management.server.port: 9090` (не публикуется наружу, Grafana Детмир скрейпит внутренний порт) ИЛИ `EndpointRequest.toAnyEndpoint().excluding(HealthEndpoint.class)` под auth. Тест под prod: `/actuator/env` и `/heapdump` → 404/401, `/health` → 200. Loop-сигнал: exposure содержит `*` или опасные эндпоинты при `permitAll()` без exclude.

### Observability и production-readiness

- **Correlation ID в MDC не печатается — починить паттерн.**
  Проверить: `CorrelationIdFilter` кладёт `MDC.put(correlationId)`, но в application*.yml нет `logging.pattern.correlation` и `%X{correlationId}` — id есть в заголовке ответа и ProblemDetail, но в логах отсутствует.
  Улучшить: `logging.pattern.correlation: "[%X{correlationId:-}] "`. Тест: WebMvc-запрос + CapturingAppender → строка лога содержит id. Loop-сигнал: класс с `MDC.put(X)`, но ни один паттерн/аппендер не ссылается на ключ X.

- **Structured JSON logging (ECS) в prod.**
  Проверить: plain-text, нет `logstash-logback-encoder`/`ecs-logging-java` в libs.versions.toml, нет `logback-spring.xml`.
  Улучшить: бампнуть SB 3.3.2 → 3.4.x/3.5.x и включить `logging.structured.format.console: ecs` в prod (JSON из коробки, MDC → top-level поля, service.name/version/environment); текст в dev-профиле. Совместимо с Kibana/ES-proxy Детмир.

- **Percentile-гистограммы для `http.server.requests` и доменных таймеров.**
  Проверить: метрики публикуются как count/sum/max без гистограммы → `histogram_quantile()` не считает p95/p99.
  Улучшить: `management.metrics.distribution.percentiles-histogram.http.server.requests: true` + `slo: 50ms,100ms,200ms,500ms,1s`; для `attempt.submit.duration` (`SubmitAnswerCommand`) — `.publishPercentileHistogram()` с ограниченными min/max. Loop-сигнал: `Timer.builder` без `publishPercentileHistogram`.

- **MeterFilter против cardinality + консолидация счётчиков по тегу `outcome`.**
  Проверить: тег `uri` взрывается от 404/ботов; `attempt.submit.success/duplicate/duration` — три метра для одного события.
  Улучшить: `management.metrics.web.server.max-uri-tags` + `@Bean MeterFilter`; свести парные счётчики к одному `attempt.submit` с `.tag("outcome", success|duplicate|failure)`. Никаких `sessionId/questionId/userId` в тегах. Loop-сигнал: `Counter.builder` с общим префиксом и разными суффиксами-исходами; динамические значения в `.tag(...)`.

- **git-commit metadata в `/actuator/info`.**
  Проверить: `build.enabled=true`, но `git.enabled=false` — по `/info` нельзя сопоставить инстанс с коммитом.
  Улучшить: плагин `com.gorylenko.gradle-git-properties` в `modules/quiz-app/build.gradle.kts` (+ `springBoot { buildInfo() }`); секция git появится рядом с существующим `SeedInfoContributor`. Micrometer `@Observed/@Timed` на горячих сервисах (`SubmitAnswerCommand`, `AttemptRecorder`, `PostgresFullTextSearchRepository`); проверить видимость `hikari.*`/`jdbc.connections.*` в `/prometheus`.

- **Таймауты JDBC/Hikari как graceful degradation.**
  Проверить: `JdbcTemplate` в `SeedCoverageHealthIndicator` без `queryTimeout`; нет `hikari.connection-timeout` → медленная БД вешает readiness вместо fail-fast.
  Улучшить: `hikari.connection-timeout: 3s`, `validation-timeout`, `max-lifetime`; `queryTimeout` на health/seed-запросах или `statement_timeout`/`socketTimeout` в data-source-properties.

- **Startup fail-fast guards на инварианты схемы.**
  Проверить: есть `@Validated AppProperties`, `SecurityModeStartupGuard`, prod требует `APP_ADMIN_TOKEN`; не хватает проверки схемы/сидеров.
  Улучшить: `ApplicationRunner` после Flyway проверяет ключевые таблицы, доступность БД, загрузку сидеров (hard-fail в prod, warning в dev); `spring.flyway.validate-on-migrate=true` (дефолт — оставить). Loop-сигнал: обязательная prod-env-переменная без стартовой проверки.

### Persistence / Flyway (PostgreSQL 16)

- **Применённые миграции неизменяемы — только новый Vxx** (объединяет 2 дименшена).
  Проверить: `git diff --name-only` по `db/migration/` пересекается с уже существующими V1..V26 (не новыми).
  Улучшить: V1..V26 заморожены (checksum), любое изменение схемы = forward-only `V27__*.sql`; рассинхрон чинить `flyway repair`, не редактированием файла. `V8__schema_guard` — эталон дозаливки без ломки checksum V3/V4/V6. Loop-гейт: правка старого V-файла → ошибка ревью.

- **Индексы на непустых таблицах — `CREATE INDEX CONCURRENTLY` в отдельном файле.**
  Проверить: во всех миграциях `CREATE INDEX IF NOT EXISTS` (блокирующий SHARE-lock) в новых V-файлах.
  Улучшить: новый индекс на существующей таблице → `CREATE INDEX CONCURRENTLY IF NOT EXISTS` в ОТДЕЛЬНОМ файле (нельзя смешивать с другими statements) + `spring.flyway.postgresql.transactional.lock: false`; модуль `flyway-database-postgresql` уже на classpath распознаёт CONCURRENTLY. Loop-гейт: `CREATE INDEX` без `CONCURRENTLY` в новом V-файле.

- **`lock_timeout`/`statement_timeout` на миграционном соединении.**
  Проверить: во flyway-блоке yml нет ни того, ни другого.
  Улучшить: `spring.flyway.init-sqls: ["SET lock_timeout = '3s'", "SET statement_timeout = '30s'"]` — `ALTER TABLE` при конкуренции упадёт быстро, а не завесит квиз. Loop-сигнал: новый `ALTER TABLE` без защиты таймаутом.

- **Backward-compatible изменения (expand–contract) — критично из-за hard-coded SQL.**
  Проверить: репозитории хардкодят списки колонок в SQL-строках (`ReviewStateRepository` и др.); `DROP/RENAME/ALTER TYPE` мгновенно ломает работающий инстанс.
  Улучшить: `ADD COLUMN` (nullable) → деплой кода, пишущего в старую+новую → поздней миграцией `DROP` старой; смена типа = новая колонка + backfill. Loop-гейт: `DROP/RENAME/ALTER COLUMN ... TYPE` по колонке, встречающейся в SQL-строках `modules/quiz-persistence/**/*.java`.

- **NOT NULL на существующей колонке — через CHECK NOT VALID → VALIDATE, не прямым SET NOT NULL.**
  Проверить: прямой `SET NOT NULL` сканирует таблицу под ACCESS EXCLUSIVE. `ADD COLUMN ... NOT NULL DEFAULT` (V8: `question_type/regen_count`) на PG16 мгновенный — эталон.
  Улучшить: для nullable-колонки: `ADD CONSTRAINT CHECK (col IS NOT NULL) NOT VALID` → отдельно `VALIDATE CONSTRAINT` → `SET NOT NULL` (PG12+ пропустит full scan). Loop-гейт: `SET NOT NULL` на ранее добавленной nullable-колонке.

- **DDL и объёмный DML раздельно; backfill батчами.**
  Проверить: `UPDATE ... WHERE ... IS NULL` рядом с `ALTER TABLE` в одном файле (V8); `UPDATE/DELETE` без WHERE.
  Улучшить: schema-change — одна миграция, наполнение — отдельная, батчами (`WHERE id BETWEEN` / чанки ≤100k). `reWriteBatchedInserts=true` (уже включён) — про INSERT сидеров, не про backfill.

- **Repeatable-миграции (`R__`) для вью/функций.**
  Проверить: `CREATE OR REPLACE VIEW/FUNCTION` внутри версионного V-файла.
  Улучшить: вью/функции для статистики/поиска (при появлении рядом с `PostgresFullTextSearchRepository`/`V2__fulltext`) → `R__stats_view.sql` (переприменяется при смене checksum, применяется после всех V).

- **Смоук-тест «V1 → head» на чистом контейнере + `validate()`.**
  Проверить: наличие теста, гоняющего `Flyway.migrate()` с нуля.
  Улучшить: интеграционный тест на СВЕЖЕМ `postgresql:16-alpine` (не переиспользуя TRUNCATE-состояние `StartupRunner`): migrate без ошибок, `validate()` зелёный, повторный migrate = no-op. Гейт формата имени `^V\d+__[a-z0-9_]+\.sql$`, нет дублей номеров версий. Тег контейнера синхронен с мажором прода (PG16).

### Дисциплина автономного рефакторинга

- **Один цикл = одно behavior-preserving микро-изменение, зелёное между шагами.**
  Каждая итерация меняет РОВНО одну вещь (extract method / переименование / схлопывание дубля), НЕ смешивая переименование + смену логики; оставляет `./gradlew :quiz-app:test` зелёным перед следующим шагом. Цель diff <200 строк, потолок ~400.

- **Характеризующий (golden-master) тест перед правкой непокрытого кода.**
  Покрытие 20% — «behavior-preserving» нечем доказать. Кандидаты: рендер markdown (flexmark+jsoup → зафиксировать HTML), `MvcAnswerRequestMapper`/`ApiRequestMapper` (вход→выход), `AdaptiveDifficultyService`/`SpacedRepetitionService` (детерминированные расчёты). Схема: реальные входы → approved-фикстура → рефактор → тест зелёный. Только после этого трогать класс.

- **Изоляция коммитов + detect-and-rollback.**
  Работать в ветке от master; один focused-коммит на итерацию (`feat(...)`/`pedago(mcq)...`). Гейт итерации: `./gradlew check` (test + jacoco + ArchUnit); при не-нуле немедленный `git revert` последнего коммита, а не «дочинить поверх». Держать bisect-скрипт с минимальным репро-тестом (exit 0/1) для отлова просочившегося регресса.

---

## Приоритеты автоулучшения (worst-first)

Ранжировать находки по **impact × безопасность правки ÷ стоимость**, брать самое дешёвое из верхнего эшелона:

1. **Security / correctness с риском утечки данных** — берётся первым независимо от стоимости: ручной `owner_key` без RLS-backstop, `getParameter("adminToken")`, публичный `/actuator/prometheus`, XFF-spoof обход rate-limit, отсутствие method-level authz на `{id}`. Одна дыра дороже любого рефактора.
2. **Скрытые дефекты в рантайме** — `orElse` с side-effect/`clock` на hot-path, correlation-id, которого нет в логах, отсутствие таймаутов Hikari (залипание проб). Дёшево, поведение-меняюще в правильную сторону, легко тестируется.
3. **Worst-first по метрикам** — брать из `graphify-out/GRAPH_REPORT.md` → секция **God Nodes** (production-классы с макс. fan-in/fan-out) и communities; скрещивать сложность (CC>10 кандидат, >20 обязателен; дубль ×3 = extract) с churn (`git log --format= --name-only | sort | uniq -c`) — где часто меняют И сложно, туда идти. Подключить SpotBugs/PMD как дешёвый метрик-гейт.
4. **Guardrail-долг, устраняемый по одному правилу за итерацию** — новые ArchUnit-правила через `FreezingArchRule.freeze()`, ratchet JaCoCo-порога вверх, миграция `JdbcTemplate → JdbcClient` по одному опасному INSERT, `orElse → orElseGet`, text blocks для SQL. Монотонно улучшают baseline, не блокируя loop требованием «почини всё сразу».
5. **Структурные апгрейды платформы** — Java 21 toolchain + virtual threads, immutable `@ConfigurationProperties`, `@ServiceConnection`-консолидация тестов, structured logging (бамп SB). Крупнее по diff — дробить на серию микрошагов, каждый со своим гейтом.

Правило метрики успеха итерации: **снятый measurable smell** (упавший CC, убранный дубль, закрытый OWASP-вектор, поднятый coverage-floor), а не «красивее по вкусу».

---

## Что НЕ делать

- **Не трогать чистый код (YAGNI + Rule of Three).** Файл, проходящий все гейты, не входящий в god-nodes, с CC в норме и дублями <3 — записать `clean, skipped`, а не выдумывать правку. Не создавать интерфейс/стратегию ради одной реализации (не размножать `strategy/` без 2-3 конкретных use-case). `ModeUtils/TopicUtils/QuizConstants`, стабильный `McqJsonLoader` — зона «работает — не улучшай без метрик-сигнала».
- **Не коммитить без верификации.** Нет `./gradlew check` зелёного перед коммитом — нет коммита. Не смешивать в одном коммите переименование + смену логики (ломает bisect и revert). Не «дочинивать поверх» красного — `git revert`.
- **Не делать крупные небезопасные изменения одним шагом.** Транзакционный рефактор и `JdbcTemplate→JdbcClient` — разные итерации. Смена типа/имени/удаление колонки, которую читают hard-coded SQL-строки, — только expand-contract через несколько миграций и деплоев.
- **Не редактировать применённые миграции.** V1..V26 заморожены; правка ломает checksum-валидацию на всех стендах с историей. Только forward-only `V27+`. Не путать `StartupRunner` TRUNCATE (сброс данных) с пересозданием схемы.
- **Не нарушать ArchUnit/слои.** `service` не дёргает контроллеры/security; `persistence` не тянет api; domain без Spring-аннотаций и без `@Transactional`. Новые нарушения слоёв — падение гейта, а не «временно так».
- **Не выдумывать факты после knowledge-cutoff** при правках теории/MCQ; не аннотировать record'ы `@Component`; не класть секреты в URL/query/теги метрик; не открывать `env/beans/heapdump/threaddump` в actuator.
- **Не оставлять граф устаревшим** к концу сессии после правок исходников (инкрементальный rebuild `graphify-out`).
