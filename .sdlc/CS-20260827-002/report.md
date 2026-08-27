# Phase 0 — Backend Reconnaissance (CS-20260827-002)

**SHA HEAD:** `16f75d51d6b5e8a20140cad986de6c2c26d53281`
**Дата:** 2026-08-28
**Ветка:** `sdlc/CS-20260827-002`
**Метод:** статическое чтение репозитория (`git log/ls-files`, `find|wc`, `grep` по аннотациям, чтение SQL-миграций и ключевых Java-файлов). Приложение и Gradle-сборка **не запускались** — ограничение задачи требует, чтобы дельта коммита осталась внутри `.sdlc/CS-20260827-002/`.

---

## 0. Сводка одним экраном

| Рубрика | Факт | Якорь |
|---|---|---|
| Модули | 3 Gradle-модуля, домен→персистентность→app | `modules/quiz-domain`, `modules/quiz-persistence`, `modules/quiz-app` |
| Controllers | 5 (`@Controller`/`@RestController`), 37 HTTP-маппингов суммарно | §2 |
| Services | 46 `@Service` | `grep -rl "@Service" modules --include="*.java" \| grep -v /test/ \| wc -l` → 46 |
| Repositories | 15 `@Repository`, все на `JdbcTemplate`, JPA/Hibernate нет | §4 |
| DTO / мапперы | 27 DTO, 9 ручных мапперов, MapStruct не используется | §5 |
| Domain model | 53 файла (record/enum + 2 исключения), почти всё immutable | §6 |
| Validation | Jakarta Bean Validation на DTO и `AppProperties`, 1 кастомный валидатор | §7 |
| Error handling | 14 `@ExceptionHandler` в одном `GlobalExceptionHandler`, RFC 7807 | §8 |
| Authentication | `app.security.mode` `local`\|`multi`, OAuth2 опционален, 2 startup-guard'а | §9 |
| Authorization | `SecurityConfig` + `SensitiveEndpointAccessService` + `OwnerContextFilter` | §10 |
| Persistence | 14 таблиц, 29 миграций, только PostgreSQL | §11–§12 |
| Caching | Caffeine, только варианты ответов, ключ = `questionId` | §13 |
| Transactions | 48 `@Transactional` в 16 файлах, граница размазана сервис+репозиторий | §14 |
| External integrations | Только PostgreSQL + опциональный OAuth2 IdP; `spring-ai-*`/`webflux` объявлены, но не используются | §15 |
| Тесты | 233 test-файла, Testcontainers PostgreSQL, ArchUnit 3 правила | §17 |
| Дельта с docs | 50 коммитов в `modules/` с 2026-08-06, документ протух частично | §18 |

---

## 1. Модули и границы

Три Gradle-модуля с однонаправленной зависимостью `quiz-domain ← quiz-persistence ← quiz-app` (`settings.gradle.kts`, `modules/*/build.gradle.kts`).

| Модуль | main *.java | test *.java |
|---|---|---|
| `quiz-domain` | 53 | 21 |
| `quiz-persistence` | 21 | 20 |
| `quiz-app` | 171 | 192 |
| **Итого** | **245** | **233** |

Команда: `find modules/<m>/src/{main,test} -name "*.java" \| wc -l`. Суммарно 23 015 строк main-кода: `find modules -path "*/src/main/*" -name "*.java" -exec cat {} + \| wc -l`.

Границы модулей проверяются не соглашением, а тестом — `LayeredArchitectureTest` (`modules/quiz-app/src/test/java/com/cheatsheet/quiz/architecture/LayeredArchitectureTest.java:20-44`, ArchUnit, `@AnalyzeClasses(packages = "com.cheatsheet.quiz")`), три правила:

1. `domainIsIndependent` (:21-30) — `..domain..` не зависит от `..api..`/`..service..`/ `..persistence..`/`..config..`.
2. `serviceDoesNotDependOnApi` (:33-37) — `..service..` не зависит от `..api.controller..`/`..api.security..`/`..api.exception..` (зависимость от `api.dto` разрешена явно, комментарий в коде).
3. `persistenceDoesNotDependOnApi` (:40-44) — `..persistence..` не зависит от `..api..`.

Пакетная раскладка `quiz-app` — вертикальные срезы (`feature/`), а не слои по типу: `feature/{interview,export,admin,auth}/`, `service/` (кросс-cutting: `cache`, `diagram`, `imports`, `strategy`, `event`, `admin`), `api/` (dto/mapper/security — общая REST-обвязка), `common/`, `config/{app,web}`, `infrastructure/{bootstrap,health,render,search}`.

---

## 2. Controllers

Ровно 5 классов с `@Controller`/`@RestController` на классе (проверено `grep -E "^\s*@(Rest)?Controller(\(|$)"`, отсекает ложные срабатывания вроде `@ControllerAdvice`, которые содержат подстроку `Controller`):

| Класс | Файл:строка | Тип | Base path |
|---|---|---|---|
| `InterviewMvcController` | `modules/quiz-app/.../feature/interview/controller/InterviewMvcController.java:35` | `@Controller` (implicit) | `/` |
| `InterviewApiController` | `modules/quiz-app/.../feature/interview/controller/InterviewApiController.java:56` | `@RestController` | `/api` |
| `ExportController` | `modules/quiz-app/.../feature/export/controller/ExportController.java:32,35` | `@RestController` | `/export` |
| `LoginController` | `modules/quiz-app/.../feature/auth/controller/LoginController.java:19` | `@Controller` | — |
| `CssBundleController` | `modules/quiz-app/.../config/web/CssBundleController.java:24` | `@Controller` (вне `feature/`) | — |

### 2.1 InterviewMvcController — 23 маппинга (428 строк, HTML: Thymeleaf view либо `redirect:`)

| HTTP | URL | :строка | HTTP | URL | :строка |
|---|---|---|---|---|---|
| GET | `/` | :45 | POST | `/pause/discard` | :281 |
| GET | `/training` (легаси-редирект) | :82 | GET | `/session-summary` | :286 |
| GET | `/review` | :94 | POST | `/answer` | :291 |
| GET | `/settings` | :123 | POST | `/settings/reset-options` | :335 |
| GET | `/train` | :188 | GET | `/stats` | :342 |
| POST | `/start` | :215 | GET | `/topics` | :375 |
| POST | `/study-confirm` | :223 | GET | `/bookmarks` | :383 |
| POST | `/flashcard-reveal` | :234 | POST | `/bookmarks/note` | :394 |
| POST | `/flashcard-grade` | :239 | POST | `/question/note` | :411 |
| POST | `/finish` | :248 | POST | `/bookmarks/remove` | :420 |
| POST | `/skip` | :253 | | | |
| POST | `/pause` | :262 | | | |
| POST | `/resume` | :276 | | | |

### 2.2 InterviewApiController — 11 маппингов (179 строк, JSON)

| HTTP | URL | :строка | HTTP | URL | :строка |
|---|---|---|---|---|---|
| POST | `/api/answer` | :67 | GET | `/api/next` | :116 |
| POST | `/api/confidence` | :79 | POST | `/api/favorite` | :140 |
| GET | `/api/streak` | :87 | POST | `/api/bookmark` | :147 |
| GET | `/api/stats` | :96 | POST | `/api/bookmark/note` | :154 |
| GET | `/api/topic-stats` | :108 | POST | `/api/bookmark/remove` | :161 |
| | | | POST | `/api/report` | :171 |

Вход — DTO из `api/dto/request/interview/**` (`@Valid`), выход — DTO из `feature/interview/dto/response/**`, сериализация Jackson.

### 2.3–2.5 Остальные три контроллера (по 1 маппингу)

| Контроллер | Маппинг | Файл:строка |
|---|---|---|
| `ExportController` (65 стр.) | `GET /export` (content negotiation по `produces`: JSON/CSV) | `:32` (`@RequestMapping`), `:46` |
| `LoginController` (60 стр.) | `GET /login` — форма для `app.security.mode=multi`, не активна в `local` | `:35` |
| `CssBundleController` (43 стр., живёт в `config/web`, не `feature/`) | `GET {page}.{hash}.css` — склеенный CSS-бандл с content-hash в пути (кэшбастинг) | `:34` |

**Итого 37 HTTP-маппингов на 5 контроллеров** (23 + 11 + 1 + 1 + 1).

---

## 3. Services

46 `@Service`-классов (`grep -rl "@Service" modules --include="*.java" \| grep -v /test/ \| wc -l`). Раскладка по каталогам `feature/interview/`:

| Пакет | Роль | Пример:строка |
|---|---|---|
| `usecase/` | тонкие оркестраторы под конкретный HTTP-вызов | `AnswerApiService`, `NextQuestionApiService`, `BookmarkApiService`, `ConfidenceApiService`, `FavoriteApiService`, `IssueReportApiService` |
| `usecase/mvc/` | то же для MVC-страниц | `InterviewPageMvcService`, `InterviewFlowMvcService` |
| `usecase/stats/` | сборка статистики для API/MVC | — |
| `service/core/` | центральная механика | `InterviewService` (`modules/quiz-app/.../service/core/InterviewService.java`, 483 стр.), `OptionLookupService`, `TrainingSessionService` |
| `service/review/` | интервальное повторение | `ReviewService`, `SpacedRepetitionService`, `FsrsService`, `AttemptRecorder`, `ReviewReasonService` |
| `service/flow/` | жизненный цикл сессии | `SessionFlowService`, `PauseService`, `ReviewModeService`, `SessionSummaryService`, `SessionHistoryService` |
| `service/page/` | сборка состояния экрана | `StatsPageService` (742 стр. — крупнейший main-файл проекта), `FocusTrainingPageService` (418 стр.), `TopicsPageService`, `BookmarksPageService`, `AnswerPageService` |
| `service/progress/` | прогресс и метки | `ExperienceService`, `DailyStreakService`, `BookmarkService`, `FavoriteService` |
| `service/topic/` | каталог тем | `TopicCatalogService` (617 стр.), `AdaptiveDifficultyService` |
| `service/insight/`, `service/report/` | аналитика, жалобы на вопросы | — |
| `service/command/` | команда отправки ответа | `SubmitAnswerCommand` |

Кросс-срезы вне `feature/`:

| Пакет | Класс |
|---|---|
| `service/imports/` | `QuestionImportService` (314 стр.) — импорт из markdown |
| `service/cache/` | `OptionCache`, `CaffeineOptionCache` (§13) |
| `service/diagram/` | нормализация Mermaid-диаграмм |
| `service/event/` | `AnswerEvent` |
| `service/strategy/` | `QuestionSelectionStrategy` (интерфейс), `DefaultSelectionStrategy`, `ShuffleSelectionStrategy`, `WeakTopicsSelectionStrategy` |
| `service/admin/` | `SeniorRulePriorityOverrideStore` |
| `infrastructure/render/` | `MarkdownRenderService` (468 стр.) |
| `infrastructure/search/` | `SearchService` |

**Вход/выход слоя:** контроллер вызывает `usecase/*ApiService` или `usecase/mvc/*PageMvcService` (тонкий уровень, без бизнес-логики) → те вызывают `service/*` (доменная механика, транзакционные границы, §14) → те вызывают репозитории (`quiz-persistence`, §4). DTO маппинг происходит на границе usecase↔controller через `api/mapper/**` (§5); внутри `service/*` циркулирует domain-модель (§6), не DTO.

---

## 4. Repositories

15 `@Repository`, все в `quiz-persistence`, все на `JdbcTemplate`/`NamedParameterJdbcTemplate` (`grep -l "JdbcTemplate" modules/quiz-persistence/src/main/java/**/*Repository.java` → 15 из 15; JPA/Hibernate в проекте нет — ни одной зависимости `spring-boot-starter-data-jpa` в `modules/*/build.gradle.kts`).

| Репозиторий | Строк | Таблица(ы) |
|---|---|---|
| `QuestionRepository` | 674 | `questions` |
| `QuestionStatsRepository` | 663 | агрегаты по `questions`/`review_state`/`attempt` |
| `AttemptRepository` | 313 | `attempt` |
| `AnswerOptionRepository` | — | `answer_options` |
| `ReviewStateRepository` | — | `review_state` |
| `BookmarkRepository` | — | `bookmark` |
| `HintRepository` | — | `question_hints` |
| `DailyActivityRepository` | — | `daily_activity` |
| `UserExperienceRepository` | — | `user_experience` |
| `UserTopicStatsRepository` | — | `user_topic_stats` |
| `SessionHistoryRepository` | — | `session_history` |
| `PausedSessionRepository` | — | `paused_session` |
| `QuestionIssueRepository` | — | `question_issue` |
| `QuestionRevisionRepository` | — | `question_revision` |
| `PostgresFullTextSearchRepository` | — | `questions.search_vector` (реализация интерфейса `FullTextSearchRepository`) |

Владелец `owner_key` (owner-scoping, §10) физически сидит в трёх таблицах/репозиториях: `review_state` (`ReviewStateRepository`, миграция `V24__owner_key_scoping.sql`), `user_experience`/`daily_activity` (`UserExperienceRepository`/`DailyActivityRepository`, `V25__owner_key_xp_activity.sql`), `question_issue` (`QuestionIssueRepository`, `V26__owner_key_question_issue.sql`). Ключевой паттерн запросов — **LEFT JOIN к `review_state` с owner-условием в `ON`, не в `WHERE`** (задокументирован в `docs/backend-architecture.md:104-109`, актуален на HEAD — таблица не переименована, проверено `grep -n "review_state" modules/quiz-persistence/.../QuestionRepository.java` возвращает непустой результат).

RowMapper'ы объявлены инлайн внутри репозиториев как `RowMapper<T>` лямбды/анонимные классы (стиль ручного JDBC, не отдельные классы) — паттерн одинаков во всех 15 файлах.

---

## 5. DTO и мапперы

27 DTO-файлов (`find modules/quiz-app/src/main -path "*/dto/*" -name "*.java" \| grep -v package-info \| wc -l`), по направлениям:

| Направление | Каталог | Файлы |
|---|---|---|
| Request | `api/dto/request/interview/` | `BookmarkNoteRequest`, `QuestionIdRequest`, `ReportIssueRequest`, `StartSessionRequest`, `SubmitAnswerRequest` |
| Response (answer) | `feature/interview/dto/response/answer/` | `AnswerResponse`, `OptionExplanationDto`, `RelatedQuestionDto`, `SessionInfoDto` |
| Response (progress) | `feature/interview/dto/response/progress/` | `BookmarkResponse`, `ConfidenceResponse`, `FavoriteResponse`, `InterviewStatsResponse`, `ReportIssueResponse`, `StreakResponse`, `TopicStatsResponse` |
| Response (session) | `feature/interview/dto/response/session/` | `QuestionResponseDTO`, `ReviewDTO`, `TrainingSessionDTO` |
| Response (top-level) | `feature/interview/dto/response/` | `NextQuestionOptionDto`, `NextQuestionResponse` |
| Response (admin) | `feature/admin/dto/response/` | `AdminOperationResult` |
| Import seed | `service/imports/dto/` | `McqSeed`, `McqSeedBlock`, `McqSeedOption`, `McqSeedQuestion` |

Мапперы — 9 файлов, все ручные (MapStruct не подключён — нет зависимости `org.mapstruct` в `libs.versions.toml`):

| Маппер | Роль |
|---|---|
| `api/mapper/request/ApiRequestMapper` | JSON-request → domain |
| `api/mapper/request/MvcRequestMapper` | form-POST → domain |
| `api/mapper/request/MvcAnswerRequestMapper` | form-POST ответа → domain |
| `api/mapper/response/InterviewDtoMapper` | domain → JSON-response |
| `api/mapper/response/StatsApiMapper` | domain-статистика → JSON |
| `api/mapper/view/MvcModelAttributeMapper` | domain → Thymeleaf `Model` (480 стр. — самый большой маппер) |
| `api/mapper/view/SessionLog` | вспомогательный логгер MVC-сессии |
| `feature/interview/mapper/ContentBlockMapper` | контент-блоки вопроса → DTO/view (решает `ASCII_DIAGRAM` vs `CODE`, см. `docs/backend-architecture.md` §6d) |
| `feature/interview/mapper/NextQuestionResponseMapper` | domain вопрос → `NextQuestionResponse` |

Правило конверсии: domain-модель (`quiz-domain`) циркулирует внутри `service/*`; на границе с транспортом (`api/`, `feature/*/controller`) она конвертируется мапперами в DTO (JSON) либо в Model-атрибуты (Thymeleaf). Обратного пути (DTO → domain, кроме request-мапперов) в проекте нет.

---

## 6. Domain model

53 файла в `quiz-domain/src/main` (`find modules/quiz-domain/src/main -name "*.java" \| wc -l`), без Spring и инфраструктурных зависимостей (гарантируется `domainIsIndependent`, §1). Почти всё — Java record либо enum.

Ключевые агрегаты и значения: `InterviewQuestion`, `InterviewSession`, `Question`, `QuestionOption`, `AnswerOption`, `Attempt`, `AttemptOutcome`, `ReviewState`, `ReviewResult`, `ReviewReason`, `ReviewDefaults`, `FsrsState`, `FsrsRating`, `FlashcardPhase`, `FlashcardGrade`, `StudyPhase`, `InterviewMode`, `InterviewFilter`, `AnswerDisplayMode`, `AnswerResult`, `Difficulty`/`QuestionDifficulty`, `QuestionType`, `ContentBlock`/`BlockType`, `Hint`, `Bookmark`, `TopicMap`, `TopicStats`, `InterviewStats`, `LearningMetrics`, `StreakInfo`, `DailyProgress`, `Progression`, `GuildRank`, `Achievement`, `UserExperience`, `SessionRecord`, `SessionSummary`, `PausedSessionInfo`, `NextActionType`, `OptionSource`, `QuestionIssue`/`QuestionIssueCategory`/`QuestionIssueStatus`, `QuestionRevision`, `RelatedQuestion`, `ProgressExportRow`, `RussianPlural` (утилита склонения числительных).

Доменные исключения — ровно два, в `quiz-domain/src/main/.../domain/exception/`: `QuestionNotFoundException`, `OptionNotFoundException`. Оба перехватываются `GlobalExceptionHandler` (§8, строки :42, :51).

Кто владеет инвариантами: логика решений («что показать», «что считается слабой темой», «какая причина повторения») сознательно вынесена из record'ов в domain-типы с поведением (`InterviewMode.isUnlimited()`, `BlockType.rendersAsMermaid()`, `ReviewReason` как результат чистой функции `ReviewReasonService.classify` в `service/review/`) — сами record'ы остаются пассивными носителями данных.

---

## 7. Validation

Bean Validation (Jakarta) на двух уровнях:

1. **DTO** (`api/dto/request/interview/*`) — стандартные аннотации на полях, срабатывают через `@Valid` на параметрах контроллера, ошибки ловит `GlobalExceptionHandler.handleBindingValidationException` (§8, :60).
2. **Конфигурация** — `AppProperties` (`modules/quiz-app/.../config/app/AppProperties.java`, `@ConfigurationProperties(prefix = "app")`, класс помечен для валидации на старте), вложенные группы валидируются `@Valid @NotNull` (:49-69): `Interview` (:102, `@Min`/`@Max` на `learned-repetitions`, `exam-penalty-questions`, `default-exam-count` ≤500, `max-session-count` ≤200), `Search` (:133), `Import` (:153), `Scheduling` (:167, `@Pattern(regexp = "sm2|fsrs")`), `Cache` (:184, `option-max-size`/`ttl-hours` ≥1), `Security` (:198, `@Pattern(regexp = "local|multi")`), `MultiUser` (:228), `Oauth` (:241), `Account` (:269), `Cors` (:281, `@Min(0) @Max(86_400)`).

Кастомный аннотационный валидатор — один: `api/validation/ValidIssueCategory`, проверяет, что присланная категория жалобы входит в `QuestionIssueCategory.values()` (перечень собирается из enum, не выписан прозой — см. `docs/backend-architecture.md` §3b, «Снисходительный разбор»).

Ручные (не аннотационные) проверки в сервисах — вне скоупа этой рубрики без дополнительного якоря на каждую; факт зафиксирован, детальный список — предмет более глубокого аудита (§19).

---

## 8. Error handling

Единая точка — `GlobalExceptionHandler` (`modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/exception/GlobalExceptionHandler.java`, 332 строки, `@ControllerAdvice`), 14 `@ExceptionHandler` (`grep -c "@ExceptionHandler" GlobalExceptionHandler.java` → 14):

| # | Исключение | Строка | Статус | Ветка |
|---|---|---|---|---|
| 1 | `QuestionNotFoundException` | :42 | 404 (API) / redirect `/` (MVC) | `ApiErrorTypes.QUESTION_NOT_FOUND` |
| 2 | `OptionNotFoundException` | :51 | 404 / redirect `/` | `ApiErrorTypes.OPTION_NOT_FOUND` |
| 3 | `MethodArgumentNotValidException`, `BindException` | :60 | 400 (API) / redirect | `VALIDATION_ERROR`, список полей |
| 4 | `ConstraintViolationException` | :82 | 400 / redirect | `VALIDATION_ERROR` |
| 5 | `MethodArgumentTypeMismatchException` | :90 | 400 / redirect | `VALIDATION_ERROR` |
| 6 | `RevisionMismatchException` | :96 | 409 / redirect | `REVISION_MISMATCH` |
| 7 | `IllegalStateException` | :113 | 409 / redirect | `QUESTION_STATE_INVALID`, фиксированный текст (не `ex.getMessage()`) |
| 8 | `UnsupportedExportFormatException` | :123 | 400 (через `badRequest`) | `UNSUPPORTED_FORMAT` |
| 9 | `ExportSerializationException` | :128 | 500 | `EXPORT_SERIALIZATION_ERROR`, `log.error` |
| 10 | `RateLimitExceededException` | :139 | 429 + заголовок `Retry-After` | `RATE_LIMIT_EXCEEDED` |
| 11 | `DataIntegrityViolationException` | :151 | 409 / redirect | `CONFLICT` |
| 12 | `NoResourceFoundException` | :165 | 404 (или 204 для `/favicon.ico`) | `RESOURCE_NOT_FOUND` / HTML-вьюха |
| 13 | `ResponseStatusException` | :193 | статус исключения (403→`FORBIDDEN`, иначе `REQUEST_REJECTED`) | HTML-вьюха или ProblemDetail |
| 14 | `Exception` (catch-all) | :209 | 500 | `INTERNAL_ERROR`, `log.error` со стектрейсом |

**HTML- и JSON-ветки:** развилка — `isApiRequest` (:260) = `RequestFormat.isMachineFacingPath` (путь `/api/**`, `/export/**`) ИЛИ `RequestFormat.prefersJson` (клиент просит JSON и не готов принять HTML). JSON-ответ — RFC 7807 `ProblemDetail` (`apiErrorResponse`, :278), `Content-Type: application/problem+json`, поля `errorCode` (из `ApiErrorTypes`), `correlationId` (из `CorrelationIdFilter`, :296-304), опционально `errors`. HTML-ответ — либо `redirect:/` (`redirectToHome`, :312), либо вьюха `error` (`errorView`, :235-248) с атрибутами `status`/`error`/`path`/`errorTime`/`correlationId`.

`ErrorViewAttributes` — отдельный класс, наполняет ту же модель для error-dispatch, перехваченного контейнером (403 от security-фильтров, которые срабатывают до MVC — `@ControllerAdvice` их не видит, см. §10), чтобы `error.html` выглядела одинаково независимо от того, кто поймал сбой.

`RateLimitExceededException` — дополнительный класс исключения вне `common/exception/` (в пакете доступа/rate-limit), несёт `retryAfterSeconds()`.

---

## 9. Authentication

Режимы задаёт `app.security.mode` (`AppProperties.Security`, :202, `@Pattern(regexp = "local|multi")`):

- **`local`** (default) — без аутентификации, анонимный пользователь маппится на `owner_key=local` (комментарий в `SecurityConfig.java:109`).
- **`multi`** — включает OAuth2-цепочку (`OAuth2LoginUserServiceConfig`) и/или `MultiUserSecurityConfig` (in-memory HTTP Basic/form-аккаунты, скелет, `MultiUserSecurityConfig.java:19` явно называет себя «skeleton multi-user identity»).

Форма логина — `LoginController` (`GET /login`, §2.4), активна только при `multi` (`app.security.oauth.enabled=true` требует `app.security.mode=multi` — `OAuthSecurityStartupGuard.java:38`).

Два startup-guard'а валят приложение при несогласованной конфигурации (fail-fast вместо полуоткрытого старта):

- `SecurityModeStartupGuard` (`SecurityModeStartupGuard.java:19-53`) — запрещает `local` под профилем `prod` без явного допуска (:44) и запрещает `multi` под `prod` с дефолтными кредами `trainer/trainer` (:53).
- `OAuthSecurityStartupGuard` — требует `mode=multi`, если включён OAuth (:38).

`OwnerContextFilter` (`config/web/OwnerContextFilter.java`, 36 строк, `OncePerRequestFilter`) кладёт ключ владельца в контекст запроса независимо от режима — в `local` это константа `"local"`, в `multi` — из аутентифицированного пользователя.

---

## 10. Authorization

`SecurityConfig` (`config/web/SecurityConfig.java`, 315 строк, `@ConditionalOnWebApplication(type = SERVLET)`, :56):

| Путь | Правило | Строка |
|---|---|---|
| `/export`, `/settings/reset-options` | требуют `app.admin-token` (см. ниже) | :99 |
| `/css/**`, `/js/**`, `/vendor/**`, `/fonts/**`, `/images/**`, `/img/**`, `/webjars/**`, `/favicon.*` (GET) | `permitAll` | :102-104 |
| `/error`, `/actuator/health`, `/actuator/health/**` | `permitAll` | :105 |
| `/login`, `/logout`, `/oauth2/**`, `/login/oauth2/**` | `permitAll` (только релевантно при `multi`) | :107-109 |
| остальное при `multi` | `authenticated()` | :111 |
| `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**` (GET) | `permitAll` | :113-114 |
| остальное при `local` | `permitAll` | :116 |

Чувствительные эндпоинты (`/export`, `/settings/reset-options`, админ-операции) закрыты `SensitiveEndpointAccessService` (`api/security/SensitiveEndpointAccessService.java`) — проверяет заголовок `X-Admin-Token` против `app.admin-token`; пустой токен в конфиге ⇒ эндпоинты закрыты полностью (см. CLAUDE.md проекта). Токен **не** принимается из query string (см. `docs/backend-architecture.md:332-336` — CWE-598, актуально: `grep -n "getQueryString\|X-Admin-Token" SensitiveEndpointAccessService.java` подтверждает проверку заголовка). Отказ отдают собственные `SensitiveEndpointAccessDeniedHandler` / `SensitiveEndpointAuthenticationEntryPoint` (единый `application/problem+json` через `writeProblem`), а не редирект на форму логина — эти фильтры срабатывают **до** MVC, поэтому `GlobalExceptionHandler` их не покрывает.

`RequestRateLimiter` (`api/security/RequestRateLimiter.java`) — ограничивает частоту обращений к дорогим эндпоинтам, порог — `app.sensitive-rate-limit-per-minute` (`AppProperties.java:84-85`, default 20, диапазон 1–300).

`OwnerContextFilter` (§9) — фактический механизм авторизации по данным: owner-scoping в `review_state`, `user_experience`, `daily_activity`, `question_issue`, `session_history` (миграции `V24`–`V26`, `V29`, §11). Предыдущий security-ревью (см. память проекта `project_security_review_owner_scoping`) отметил, что часть таблиц (в частности флаг `is_important`) не имеет `owner_key` — латентно при `mode=local`, актуально при `multi`; это НЕ переоценивается здесь (вне скоупа инвентаря), но фиксируется как факт для §19.

`CspNonceFilter`/`CspNonceAdvice` — CSP с nonce на запрос, без `unsafe-inline`.

---

## 11. Persistence

PostgreSQL — единственная поддерживаемая СУБД (`docs/backend-architecture.md:18`, подтверждено: в `modules/quiz-persistence/build.gradle.kts` нет H2/иных драйверов, `runtimeOnly(libs.postgresql)` — единственный JDBC-драйвер в `quiz-app/build.gradle.kts:30`).

14 таблиц (`grep -n "CREATE TABLE" modules/quiz-persistence/src/main/resources/db/migration/*.sql`):

| Таблица | Создана | Ключевые колонки/индексы |
|---|---|---|
| `questions` | `V1__init.sql:1` | PK `id`; `search_vector tsvector` (`V2`); `question_type`, `code_snippet` (`V4`/`V8`); `difficulty`, `short_explanation`, `detailed_explanation` (`V14`); индексы `idx_questions_topic`, `idx_questions_important`, `idx_questions_file_path` (`V7`) |
| `answer_options` | `V1__init.sql:12` | FK → `questions(id)` ON DELETE CASCADE; `mcq_block_idx` (`V15`); уникальный индекс «один correct на блок» (`V13`→`V15`) |
| `review_state` | `V1__init.sql:23` | FK → `questions(id)` ON DELETE CASCADE; PK составной `(owner_key, question_id)` с `V24__owner_key_scoping.sql` (был просто `question_id`) |
| `question_hints` | `V5__question_hints.sql:1` | FK → `questions(id)` ON DELETE CASCADE; `idx_hints_question` |
| `daily_activity` | `V10__daily_activity.sql:2` | owner-scoped с `V25` |
| `user_topic_stats` | `V14__question_v2_and_user_topic_stats.sql:7` | — |
| `paused_session` | `V16__paused_session.sql:11` | — |
| `question_issue` | `V17__question_issue.sql:9` | FK → `questions(id)` ON DELETE CASCADE; owner-scoped с `V26`; индексы `idx_question_issue_question`, `idx_question_issue_status`, `idx_question_issue_owner_question`, `idx_question_issue_owner_status` |
| `question_revision` | `V18__attempts_revisions_fsrs.sql:16` | `idx_question_revision_question` |
| `attempt` | `V18__attempts_revisions_fsrs.sql:36` | FK → `questions`, `question_revision` (SET NULL), `answer_options` (SET NULL); `opened_content_block_ids` (`V21`) |
| `user_experience` | `V19__experience.sql:13` | owner-scoped с `V25`, singleton-constraint снят |
| `bookmark` | `V20__bookmarks.sql:9` | FK → `questions(id)` ON DELETE CASCADE; `idx_bookmark_created_at` |
| `attempt_idempotency_claim` | `V22__attempt_idempotency_claim.sql:3` | — |
| `session_history` | `V29__session_history.sql:14` | `idx_session_history_owner_finished` |

FK-констрейнты (9 штук) все `ON DELETE CASCADE` к `questions`, кроме `attempt → question_revision`/`attempt → answer_options`, которые `ON DELETE SET NULL` (мягкая ссылка, чтобы удаление ревизии/варианта не роняло историю попыток).

Маппинг на домен — вручную в RowMapper внутри каждого `*Repository` (§4), не через JPA/аннотации на entity.

Полнотекстовый поиск — нативный PostgreSQL FTS: `search_vector tsvector` (`V2`) + триггер `questions_search_vector_trigger`, читает `PostgresFullTextSearchRepository`.

Идемпотентность `attempt`: уникальный индекс `attempt_idempotency_claim` (`V22`) не даёт двойной записи попытки при ретрае; `uq_attempt_first_per_question` (`V23`) гарантирует единственность первой попытки на вопрос (от неё считается статистика освоенности).

---

## 12. Migrations

29 файлов `V1..V29` (`ls modules/quiz-persistence/src/main/resources/db/migration \| wc -l` → 29; нумерация нелинейна лексикографически — `V10` идёт перед `V2` в `ls`, но версии Flyway применяются численно):

| Версия | Что делает |
|---|---|
| V1 | Создаёт `questions`, `answer_options`, `review_state` — базовая схема |
| V2 | Полнотекстовый поиск: `search_vector` + триггер обновления |
| V3 | `questions.source_slug` |
| V4 | `questions.question_type`, `code_snippet` |
| V5 | Создаёт `question_hints` + индекс |
| V6 | `questions.diagram_mermaid`, `regen_count` (позже удалена V27) |
| V7 | Индексы на `questions` (topic/important/file_path) |
| V8 | Schema-guard: повторно добавляет `source_slug`/`question_type`/`code_snippet` идемпотентно (`IF NOT EXISTS`) |
| V9 | `answer_options.explanation` |
| V10 | Создаёт `daily_activity` |
| V11 | `questions.takeaway` |
| V12 | Версионирование `answer_options` (изменение колонок) |
| V13 | Уникальный индекс «один correct на вопрос» |
| V14 | `questions.difficulty`/`short_explanation`/`detailed_explanation`; создаёт `user_topic_stats` |
| V15 | `answer_options.mcq_block_idx`; уникальный индекс переезжает на «один correct на блок» |
| V16 | Создаёт `paused_session` |
| V17 | Создаёт `question_issue` + индексы |
| V18 | Создаёт `question_revision`, `attempt` (FSRS + ревизии) |
| V19 | Создаёт `user_experience` |
| V20 | Создаёт `bookmark` + индекс |
| V21 | `attempt.opened_content_block_ids` |
| V22 | Создаёт `attempt_idempotency_claim` — защита от двойной записи при ретрае |
| V23 | Уникальный индекс «первая попытка на вопрос» |
| V24 | **Owner-scoping**: `review_state.owner_key`, составной PK `(owner_key, question_id)` |
| V25 | **Owner-scoping**: `user_experience.owner_key` (снимает singleton-constraint), `daily_activity` |
| V26 | **Owner-scoping**: `question_issue.owner_key` + индексы |
| V27 | **Деструктивная**: `DROP COLUMN questions.regen_count` (мёртвая метрика после удаления AI-генерации, см. `docs/backend-architecture.md:356-364`) |
| V28 | **Деструктивная**: чистит строки `review_state`, записанные старым (до-upsert) поведением показа вопроса |
| V29 | Создаёт `session_history` + индекс |

Линии эволюции: **owner-scoping** (V24–V26, три миграции подряд разносят `owner_key` по таблицам прогресса) и **удаления** (V27 мёртвая колонка, V28 мусорные строки) — обе линии совпадают с направлением, названным в design.md задачи.

Схема-гварды: V8 и паттерн `ADD COLUMN IF NOT EXISTS` почти во всех ALTER — миграции написаны идемпотентно (безопасный повторный прогон), а не строго append-only.

---

## 13. Caching

Единственный кэш в проекте — `service/cache/OptionCache` (интерфейс) + `CaffeineOptionCache` (реализация, `service/cache/CaffeineOptionCache.java`, 43 строки). Spring Cache-абстракция (`@Cacheable`/`@CacheEvict`) **не используется** — прямой вызов Caffeine `Cache<Long, List<AnswerOption>>` API (`get`/`put`/`invalidate`/`invalidateAll`).

Параметры — `app.cache.option-max-size` (default 500) / `app.cache.ttl-hours` (default 1), `AppProperties.Cache` (:184-190), backend собирается в `config/app/InfrastructureConfig`.

Ключ — `questionId` (`long`), значение — список `AnswerOption` для этого вопроса. Инвалидация — точечная (`invalidate(questionId)`) при изменении вариантов конкретного вопроса, либо полная (`invalidateAll()`).

**Что НЕ кэшируется:** сами вопросы (`Question`/`InterviewQuestion`), статистика, сессии, прогресс — все читаются из БД на каждый запрос без кэш-слоя. Только MCQ-варианты (наиболее частый read из-за `OptionLookupService`, вызываемого на каждый показ вопроса) получили кэш.

---

## 14. Transactions

48 `@Transactional` в 16 файлах (`grep -ro "@Transactional" modules --include="*.java" \| grep -v /test/ \| wc -l` → 48; `grep -rl` на тот же паттерн → 16 файлов):

| Файл | Кол-во |
|---|---|
| `QuestionRepository` (persistence) | 7 |
| `ReviewService` (service) | 5 |
| `BookmarkService` (service) | 5 |
| `InterviewService` (service) | 5 |
| `AttemptRecorder` (service) | 4 |
| `ReviewStateRepository` (persistence) | 3 |
| `HintRepository` (persistence) | 3 |
| `AnswerOptionRepository` (persistence) | 3 |
| `BookmarkApiService` (usecase) | 3 |
| `ExperienceService` (service) | 2 |
| `DailyStreakService` (service) | 2 |
| `AdminMaintenanceService` (service) | 2 |
| `UserTopicStatsRepository` (persistence) | 1 |
| `QuestionIssueService` (service) | 1 |
| `FavoriteService` (service) | 1 |
| `SubmitAnswerCommand` (service) | 1 |

Граница транзакции **размазана по двум слоям**: и сервисы (`feature/interview/service/*`, `feature/interview/usecase/BookmarkApiService`, `feature/admin/service/*`), и репозитории (`quiz-persistence`) объявляют собственные `@Transactional`. Это структурный факт, не оценка — design.md прямо просит зафиксировать это как наблюдение для инвентаря. `AttemptRecorder.record()` — отдельная транзакция, которая, по документации, «умеет выйти рано по idempotency-ключу» (`docs/backend-architecture.md:151-154`).

Места без явной транзакции: чтения (`get*`/`find*` в репозиториях) в основном не аннотированы — транзакция применяется точечно на операциях записи/составных последовательностях чтения+записи.

---

## 15. External integrations

Фактических сетевых интеграций — де-факто ноль до одной опциональной:

- **PostgreSQL** — единственная реальная внешняя система (JDBC, §11).
- **OAuth2 IdP** — опционален, включается `app.security.oauth.enabled=true` (требует `app.security.mode=multi`, §9); используется зависимость `spring-boot-starter-oauth2-client` (`modules/quiz-app/build.gradle.kts:23`).

Сетевых клиентов (`WebClient`/`RestClient`/`RestTemplate` с реальным вызовом) в main-коде нет: `grep -rl "WebClient" modules/quiz-app/src/main --include="*.java"` возвращает только `config/package-info.java` (упоминание в Javadoc пакета, не использование).

**Объявленные, но не используемые зависимости** (`modules/quiz-app/build.gradle.kts`):

```
14:    implementation(libs.spring.boot.starter.webflux)
...
42:    // --- Spring AI (кастомная модель, OpenAI-совместимый API) ---
43:    implementation(platform(libs.spring.ai.bom))
44:    implementation(libs.spring.ai.openai)
```

(`gradle/libs.versions.toml:4,18,47` — версии `spring-ai = "1.0.0"`.)

Доказательство отсутствия использования:

```bash
$ grep -rl "org.springframework.ai" modules --include="*.java"
# пусто, exit code 1
$ grep -rl "WebClient" modules/quiz-app/src/main --include="*.java"
modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/package-info.java   # только Javadoc-упоминание, не код
```

Это согласуется с фактом проекта «AI полностью вырезан» (CLAUDE.md: «нет пакета `llm/`, нет `service/ai/`… отсутствие генерации — свойство кода, а не настройки»): зависимости в `build.gradle.kts` — остаток, который никто не удалил при вырезании AI-кода. Это дубль/мёртвый груз, а не источник правды (правило 2 design.md).

---

## 16. Конфигурация и запуск

`AppProperties` (`config/app/AppProperties.java`, `@ConfigurationProperties(prefix = "app")`) — дерево `app.*`, валидируемое Bean Validation (§7): `interviewPath`, `admin-token`, `sensitive-rate-limit-per-minute`, вложенные группы `Interview`, `Search`, `Import`, `Scheduling`, `Cache`, `Security` (+ `MultiUser`, `Oauth`, `Account`), `Cors`.

Профили: `default` (dev, Swagger включён), `test` (форсится в `build.gradle.kts` для тестового `sourceSet`, Testcontainers), `prod` (отключает Swagger UI, см. CLAUDE.md проекта).

Стартовые механизмы:

- `infrastructure/bootstrap/StartupRunner` — импорт вопросов из markdown при старте; `app.interview-reset-on-startup=true` форсит TRUNCATE + переимпорт.
- `QaFixtureRunner` — служебные фикстуры для QA-окружения.
- `SecurityModeStartupGuard`, `OAuthSecurityStartupGuard` — fail-fast guard'ы (§9).
- `infrastructure/health/SeedCoverageHealthIndicator` — покрытие MCQ-сидерами в `/actuator/health`.
- `infrastructure/health/SeedInfoContributor` — то же в `/actuator/info`.
- `config/app/InfrastructureConfig` — сборка Caffeine-кэша (§13) и прочих бинов.
- `config/app/QaConfig` — конфигурация QA-профиля.

---

## 17. Тесты бэкенда

233 test-файла (`find modules -path "*/src/test/*" -name "*.java" \| wc -l`), структура зеркалит main:

| Модуль/пакет | Файлов |
|---|---|
| `quiz-app` / `ui` | 74 |
| `quiz-app` / `service` | 47 |
| `quiz-domain` / `domain` | 21 |
| `quiz-persistence` / `persistence` | 20 |
| `quiz-app` / `api` | 20 |
| `quiz-app` / `feature` | 12 |
| `quiz-app` / `config` | 12 |
| `quiz-app` / `common` | 7 |
| `quiz-app` / `infrastructure` | 5 |
| `quiz-app` / прочее (persistence-дубли пакета, bootstrap, domain, architecture) + корневые классы (`TestQuestionBuilder`, `TestInterviewPath`, `SpacedRepetitionServiceTest`, …) | остаток до 233 |

Testcontainers: `AbstractPostgresRepositoryTest` (`modules/quiz-persistence/src/test/java/.../AbstractPostgresRepositoryTest.java`) — один контейнер на JVM, TRUNCATE между тестами; `quiz-app` тесты подхватывают `jdbc:tc:postgresql:16-alpine:///` из `application-test.yml` (:1-13). 8 файлов упоминают `Testcontainers`/`PostgreSQLContainer` напрямую (`grep -rl "Testcontainers\| PostgreSQLContainer" modules --include="*.java" \| wc -l`).

ArchUnit — `LayeredArchitectureTest`, 3 правила (§1).

Чего не покрыто статически видимым способом: нагрузочное поведение, реальные HTTP-статусы под конкурентным трафиком, поведение `multi`-режима (design.md фиксирует: «Режим выключен и живой проверкой не покрыт» — `docs/backend-architecture.md:319`, актуальность не переподтверждена на HEAD, см. §19).

---

## 18. Дельта против docs/backend-architecture.md (4556a627, 2026-08-06)

Базовый коммит документа: `4556a627122c05ab67c02a986966f550537117d4`, 2026-08-06 (`git log -1 --format='%H %ad' --date=short -- docs/backend-architecture.md`). Коммитов в `modules/` с этой даты по HEAD: **50** (`git log --oneline 4556a627..HEAD -- modules/ \| wc -l`).

Расхождения, найденные при перепроверке фактов документа на HEAD:

1. **Число файлов `quiz-domain`.** Документ говорит «46 файлов» (`:11`); на HEAD — 53 (`find modules/quiz-domain/src/main -name "*.java" \| wc -l`). Домен вырос на 7 файлов за 50 коммитов (появились `QuestionIssue*`, `ProgressExportRow`, `RelatedQuestion` и др., которых нет в перечислении документа).
2. **Число миграций.** Документ говорит «V1…V26» (`:17`); на HEAD — V1..V29 (§12). Три новые миграции (V27 удаление `regen_count`, V28 чистка `review_state`, V29 `session_history`) появились после базового коммита.
3. **Режимы безопасности переименованы.** Документ называет режимы `single`/`multi` (`:314-319`); на HEAD валидатор (`AppProperties.java:202`) и весь код используют `local`/`multi` (`grep -rn "app.security.mode" modules` — везде `local`, ни одного `single` в актуальном коде). Терминология документа устарела.
4. **Число репозиториев.** Документ говорит «15 файлов `*Repository*` (14 JDBC-реализаций + интерфейс)» (`:16`) — совпадает с HEAD (§4), расхождения нет.
5. **`session_history`/`SessionHistoryRepository` отсутствуют в документе** — фича добавлена в V29, которая новее базового коммита; §2/§3 документа не упоминают `SessionHistoryService`/`SessionHistoryRepository`, хотя они есть на HEAD (design.md задачи их перечисляет).

Совпадения (перепроверены и подтверждены актуальными): структура модулей и ArchUnit-правила (§1), развилка ошибок API/MVC и RFC 7807 (§8, документ:276-310 совпадает буквально с кодом на HEAD), owner-scoping паттерн LEFT JOIN (§4, документ:100-109), отсутствие AI-путей (§15, документ:348-351).

---

## 19. Белые пятна разведки

Статическое чтение репозитория не может подтвердить следующее — нужно отдельное наблюдение за живой системой:

1. **Реальные планы запросов и производительность.** Индексы перечислены по коду миграций (§11–§12), но фактические `EXPLAIN ANALYZE` на актуальном объёме данных (документ упоминает порядок 9818/319 строк — устарело или нет, неизвестно) не снимались. Закрыть: поднять приложение на `SERVER_PORT=8091` с Testcontainers/реальным Postgres и снять `EXPLAIN ANALYZE` по горячим запросам `QuestionRepository`/ `QuestionStatsRepository`.
2. **Поведение `app.security.mode=multi` под нагрузкой.** И документ (2026-08-06), и код (`MultiUserSecurityConfig` называет себя «skeleton») сходятся, что режим не покрыт живой проверкой; неизвестно, изменилось ли это за 50 коммитов. Закрыть: поднять стенд с `app.security.mode=multi`, пройти OAuth2-цепочку и in-memory basic-auth руками либо интеграционным тестом.
3. **Фактические HTTP-статусы и заголовки под реальными сценариями отказа.** `GlobalExceptionHandler` и `SensitiveEndpointAccessService` описаны по коду (§8, §10), но что именно возвращает сервер на конкретный malformed-запрос (граничные случаи `Accept`-заголовка в `RequestFormat`) не проверялось вызовом. Закрыть: `curl`/HTTP-тесты против поднятого на 8091 приложения с разными `Accept`/`X-Admin-Token`.
4. **Полнота Bean Validation в сервисном слое (§7).** Зафиксирован факт «есть ручные проверки в сервисах», но не перечислен построчно — вне бюджета текущей рубрики. Закрыть: отдельный проход `grep -rn "throw new IllegalArgumentException\| IllegalStateException" modules/quiz-app/src/main` с классификацией по слоям.
5. **Актуальность объёма данных (9818 вопросов / 319 тем), упомянутого в `docs/backend-architecture.md`.** Статически не проверяется — требует живого запроса к БД (`SELECT count(*) FROM questions`) на актуальном импорте `cheatsheets/interview/**`.

---

## Приложение A. Команды воспроизведения

```bash
# SHA / ветка / дата
git rev-parse HEAD
git branch --show-current
git log -1 --format='%H %ad' --date=short 4556a627
git log --oneline 4556a627..HEAD -- modules/ | wc -l   # → 50

# Модули: файлы main/test
find modules/quiz-domain/src/main -name "*.java" | wc -l        # → 53
find modules/quiz-domain/src/test -name "*.java" | wc -l        # → 21
find modules/quiz-persistence/src/main -name "*.java" | wc -l   # → 21
find modules/quiz-persistence/src/test -name "*.java" | wc -l   # → 20
find modules/quiz-app/src/main -name "*.java" | wc -l           # → 171
find modules/quiz-app/src/test -name "*.java" | wc -l           # → 192
find modules -path "*/src/main/*" -name "*.java" -exec cat {} + | wc -l   # → 23015

# Controllers (аннотация на классе, не подстрока)
grep -rlE "^\s*@(Rest)?Controller(\(|$)" modules --include="*.java" | grep -v /test/

# Services / Repositories
grep -rl "@Service" modules --include="*.java" | grep -v /test/ | wc -l       # → 46
grep -rl "@Repository" modules --include="*.java" | grep -v /test/ | wc -l   # → 15
grep -rl "@Repository" modules --include="*.java" | grep -v /test/           # список 15 файлов

# DTO / мапперы
find modules/quiz-app/src/main -path "*/dto/*" -name "*.java" | grep -v package-info | wc -l   # → 27
find modules/quiz-app/src/main -path "*mapper*" -name "*.java"                                  # → 9 файлов

# Domain
find modules/quiz-domain/src/main -name "*.java" | wc -l   # → 53

# Exception handlers (DoD-проверка §8)
grep -c "@ExceptionHandler" \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/exception/GlobalExceptionHandler.java
# → 14

# Миграции (DoD-проверка §12)
ls modules/quiz-persistence/src/main/resources/db/migration | wc -l   # → 29

# Transactional
grep -ro "@Transactional" modules --include="*.java" | grep -v /test/ | wc -l   # → 48
grep -rl "@Transactional" modules --include="*.java" | grep -v /test/ | wc -l   # → 16

# Owner-scoping миграции
grep -l "owner_key" modules/quiz-persistence/src/main/resources/db/migration/*.sql

# Мёртвые зависимости spring-ai/webflux (DoD-проверка §15/AC9)
grep -n "spring-ai\|webflux" modules/quiz-app/build.gradle.kts
grep -rl "org.springframework.ai" modules --include="*.java"; echo "exit=$?"   # → пусто, exit=1
grep -rl "WebClient" modules/quiz-app/src/main --include="*.java"              # только package-info.java Javadoc

# ArchUnit-правила
sed -n '20,44p' \
  modules/quiz-app/src/test/java/com/cheatsheet/quiz/architecture/LayeredArchitectureTest.java

# Тесты
find modules -path "*/src/test/*" -name "*.java" | wc -l   # → 233
grep -rl "Testcontainers\|PostgreSQLContainer" modules --include="*.java" | wc -l   # → 8

# Резолв якорей отчёта (все path: существуют на HEAD)
for p in \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewApiController.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/export/controller/ExportController.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/auth/controller/LoginController.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/CssBundleController.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/exception/GlobalExceptionHandler.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/SecurityConfig.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/app/AppProperties.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/OwnerContextFilter.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/SecurityModeStartupGuard.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/OAuthSecurityStartupGuard.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/MultiUserSecurityConfig.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/cache/OptionCache.java \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/cache/CaffeineOptionCache.java \
  modules/quiz-app/src/test/java/com/cheatsheet/quiz/architecture/LayeredArchitectureTest.java \
  modules/quiz-persistence/src/test/java/com/cheatsheet/quiz/persistence/AbstractPostgresRepositoryTest.java \
  modules/quiz-app/src/test/resources/application-test.yml \
  modules/quiz-app/build.gradle.kts \
  gradle/libs.versions.toml \
  docs/backend-architecture.md \
; do test -e "$p" || echo "MISSING: $p"; done
# (пустой вывод = все якоря резолвятся)

# DoD-гейт задачи
test -s .sdlc/CS-20260827-002/report.md && echo OK
```
