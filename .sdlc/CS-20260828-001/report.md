# Phase 0 — Backend Reconnaissance (CS-20260828-001)

**SHA HEAD:** `2ee1ead31d7d3cc2ea2d805e8ab46c0c92adf670`
**Дата:** 2026-08-28
**Ветка:** `sdlc/CS-20260828-001`
**Метод:** статическое чтение репозитория (`find`, `wc`, `grep`, `sed`) на
зафиксированном SHA. Приложение не поднималось (порт 8080 занят живым
bootRun пользователя), БД не читалась, graphify-граф не перестраивался
(AST-only по `modules/`, `CLAUDE.md` §6). Карта URL уже снята
предшественником (`.sdlc/CS-20260827-001/report.md` §2) — здесь не
дублируется, разбирается только серверная сторона.

---

## 0. Сводка одним экраном

| Рубрика | Факт | Главный якорь |
|---|---|---|
| 1. Модули | 3 Gradle-модуля: `quiz-domain` (53 файла/2934 LOC), `quiz-persistence` (21/3103), `quiz-app` (171/17040); границы держит ArchUnit | `settings.gradle.kts:9-13` |
| 2. Controllers | Ровно 5 классов, 37 хендлеров суммарно (23+11+1+1+1) | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java:45-420` |
| 3. Services | 49 файлов `*Service.java` + 4 файла в `service/strategy` без суффикса Service, разложены по 11 подпакетам `feature/interview/service/*` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/service/core/InterviewService.java:1` |
| 4. Repositories | 16 классов/интерфейсов `*Repository*`, 15 держат `JdbcTemplate`, 14 таблиц | `modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/QuestionRepository.java:44` |
| 5. DTO/mapper | 27 DTO-файлов (4 пакета), 9 мапперов (2 пакета: `api/mapper`, `feature/interview/mapper`) | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/mapper/response/InterviewDtoMapper.java:1` |
| 6. Domain model | 53 файла (51 тип + 2 `package-info`) в едином пакете `domain` + `domain/exception`, без Spring/JDBC | `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/InterviewSession.java:1` |
| 7. Validation | Jakarta-аннотации на 5 request-DTO + 1 кастомный `@ValidIssueCategory`; доменные инварианты в конструкторах | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/validation/ValidIssueCategory.java:29` |
| 8. Error handling | `GlobalExceptionHandler` — 14 `@ExceptionHandler`, единая точка на 15 типов исключений | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/exception/GlobalExceptionHandler.java:42-209` |
| 9. Authentication | `app.security.mode`=`local`\|`multi`; `SecurityConfig`+`MultiUserSecurityConfig`+`LoginController` (bean только в `multi`) | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/auth/controller/LoginController.java:18` |
| 10. Authorization | `OwnerContextFilter`→`OwnerContext` (ThreadLocal, SAFE-regex), admin-token+`ROLE_ADMIN` на `/export`, `/settings/reset-options` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/OwnerContextFilter.java:29` |
| 11. Persistence | 14 таблиц из миграций, все с `id`/PK; owner_key частично внедрён (5 таблиц из V24-V26) | `modules/quiz-persistence/src/main/resources/db/migration/V24__owner_key_scoping.sql:1` |
| 12. Migrations | 29 файлов `V1`..`V29`, 3 drop/чистки (`V27`,`V28`), 3 owner_key-миграции (`V24`-`V26`) | `modules/quiz-persistence/src/main/resources/db/migration/V27__drop_regen_count.sql:1` |
| 13. Transactions | 16 main-файлов с `@Transactional`: 5 в `quiz-persistence`, 11 в `quiz-app`-сервисах | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/service/core/InterviewService.java:1` |
| 14. Caching | Ручной Caffeine-кэш (`OptionCache`/`CaffeineOptionCache`), `@Cacheable`/`@EnableCaching` в main-коде отсутствуют (0 хитов) | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/runtime/CacheConfig.java:23` |
| 15. External integrations | HTTP-клиентов нет; `webflux`+`spring-ai-openai` объявлены в gradle, потребителей 0; источники — файлы + Postgres | `modules/quiz-app/build.gradle.kts:14,44` |
| 16. Конфигурация | `app.*` — 302-строчный `AppProperties`, 236-строчный `application.yml`, 10+ under-групп | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/app/AppProperties.java:1-302` |
| 17. Тесты | 114 бэкенд-`*Test.java` в `quiz-app` (189 всего минус 75 `ui/`) + 20 persistence + 21 domain; jacoco 0.915/0.764 | `modules/quiz-app/build.gradle.kts:117` |
| 18. Белые пятна | 5 пунктов, все требуют живого прогона | см. §18 |

---

## 1. Модули и границы

Три Gradle-модуля объявлены в `settings.gradle.kts:9-13` и физически лежат в
`modules/quiz-domain`, `modules/quiz-persistence`, `modules/quiz-app`
(`settings.gradle.kts:15-17`).

| Модуль | Java-файлов | Строк main | Технологии |
|---|---|---|---|
| `quiz-domain` | 53 | 2934 | чистая Java, без Spring/JDBC |
| `quiz-persistence` | 21 | 3103 | Spring JDBC (`JdbcTemplate`), Flyway |
| `quiz-app` | 171 | 17040 | Spring Boot MVC, Thymeleaf, Security |

Направление зависимостей закреплено сборкой (`modules/quiz-app/build.gradle.kts:9-10`
— `quiz-app` зависит от `quiz-domain` и `quiz-persistence`; обратной строки
нет ни в одном build-файле) и исполняется ArchUnit-тестом
`modules/quiz-app/src/test/java/com/cheatsheet/quiz/architecture/LayeredArchitectureTest.java`:
`domain` не видит api/service/persistence/config, `service` не видит
контроллеры и security-классы, `persistence` не видит `api` — правило
падает тестом при нарушении, а не соглашение в доке.

`quiz-app` раскладывается на 6 top-level пакетов:

| Пакет | Файлов | Назначение |
|---|---|---|
| `feature/` | 75 (interview 66, export 5, admin 2, auth 2) | вертикальные срезы фич |
| `service/` | 25 | cross-cutting: admin, cache, diagram, event, imports, strategy |
| `api/` | 23 | REST-обвязка: controller/dto/mapper/security/validation/constants |
| `config/` | 22 | app/runtime/web конфигурация |
| `common/` | 17 | исключения, константы, утилиты |
| `infrastructure/` | 7 | bootstrap/health/render/search |
| корень | 2 | `InterviewApplication.java` (main-класс), `package-info.java` |

Сумма 75+25+23+22+17+7+2 = 171, совпадает со счётчиком модуля.

---

## 2. Controllers

Ровно 5 классов с `@RestController`/`@Controller` на верхнем уровне (не
считая `@ControllerAdvice`-классов, см. §8). Проверка:
`grep -lE '^\s*@(RestController|Controller)\b' -r modules/quiz-app/src/main`.

| Класс | Хендлеров | Тип | Вход | Делегирует |
|---|---|---|---|---|
| `InterviewMvcController` | 23 | `@Controller` (view) | `@ModelAttribute`/`@RequestParam`/`@Valid @RequestBody`-подобные form-DTO (`StartSessionRequest`, `SubmitAnswerRequest`) | `InterviewPageMvcService`, `InterviewFlowMvcService`, `AdminMaintenanceService` |
| `InterviewApiController` | 11 | `@RestController` (JSON) | `@RequestBody`/`@RequestParam` DTO (`SubmitAnswerRequest`, `BookmarkNoteRequest`, `QuestionIdRequest`, `ReportIssueRequest`) | 8 usecase-сервисов (`AnswerApiService`, `BookmarkApiService`, `ConfidenceApiService`, `FavoriteApiService`, `IssueReportApiService`, `NextQuestionApiService`, `StatsApiService`, `StreakApiService`) через `ApiRequestMapper` |
| `LoginController` | 1 | `@Controller`, бин только при `app.security.mode=multi` | `@RequestParam` (`error`,`logout`) | `LoginPageService`, `ClientRegistrationRepository` (OAuth2, опционально) |
| `ExportController` | 1 | `@RestController`, `@RequestMapping("/export")` | `@RequestParam format` | `ExportApiService`, `SensitiveEndpointAccessService` |
| `CssBundleController` | 1 | `@RestController` | `@PathVariable page,hash` | `CssBundleService` |

Якоря числа хендлеров:
`InterviewMvcController.java:45-420` (23 `@*Mapping`),
`InterviewApiController.java:67-171` (11 `@*Mapping`),
`LoginController.java:35`, `ExportController.java:46`, `CssBundleController.java:34`.

`InterviewMvcController` — единственный контроллер, отвечающий за оба легаси-
и канонический маршрут (`TRAINING_QUERY_PARAMS`,
`InterviewMvcController.java:36-38`, перенос параметров с `/training` на `/`).
Полная таблица из 37 точек входа (23 страницы + 11 `/api/*` + `/login` +
`/export` + `/css/bundle/*`) — в предшественнике,
`.sdlc/CS-20260827-001/report.md` §2. `LoginController` — единственный
контроллер, поднимаемый условно: `@ConditionalOnProperty(prefix =
"app.security", name = "mode", havingValue = "multi")`
(`LoginController.java:18`); в `local`-режиме бина нет, значит `/login`
резолвится в 404 (или редиректом на `/`, зависит от `SecurityConfig`), а не
рендерит форму.

`InterviewApiController` — единственный REST-контроллер интервью-домена, все
11 хендлеров ответственность делегируют, сам класс без бизнес-логики: тело
метода — вызов usecase + маппинг DTO
(`InterviewApiController.java:67-171`).

---

## 3. Services

49 файлов с суффиксом `*Service.java` в `quiz-app` (проверка:
`find modules/quiz-app/src/main -name '*Service.java' | wc -l`). Плюс 4 файла
в `service/strategy/` без суффикса `Service` (`QuestionSelectionStrategy` +
3 реализации) и по одному файлу в `service/cache`, `service/diagram`,
`service/event`, `service/admin`, `service/imports` без суффикса — они
образуют cross-cutting-каркас, на котором `*Service`-классы держатся.

### 3.1 `feature/interview/service/*` — ядро (11 подпакетов)

| Подпакет | `*Service.java` | Назначение | Пример |
|---|---|---|---|
| `core` | 3 | оркестрация сессии/вопроса | `InterviewService.java`, `OptionLookupService.java`, `TrainingSessionService.java` |
| `command` | 1 | команда с побочными эффектами | `SubmitAnswerCommand.java` (`@Transactional`) |
| `flow` | 5 | шаги теста: пауза/резюме/итог | `PauseService`, `ReviewModeService`, `SessionFlowService`, `SessionHistoryService`, `SessionSummaryService` |
| `facade` | 1 | единая точка входа для usecase-слоя | `InterviewFacade.java` |
| `page` | 5 | сборка данных для конкретной страницы | `AnswerPageService`, `BookmarksPageService`, `FocusTrainingPageService`, `StatsPageService`, `TopicsPageService` |
| `progress` | 4 | закладки/избранное/streak/XP | `BookmarkService`, `DailyStreakService`, `ExperienceService`, `FavoriteService` |
| `review` | 4 (+2 без суффикса: `AttemptRecorder` §13, `RevisionMismatchException` §8) | интервальное повторение | `FsrsService`, `ReviewReasonService`, `ReviewService`, `SpacedRepetitionService` |
| `report` | 1 | жалобы на вопрос | `QuestionIssueService.java` |
| `insight` | 1 | связанные вопросы | `RelatedQuestionsService.java` |
| `topic` | 2 | подбор темы/сложности | `AdaptiveDifficultyService`, `TopicCatalogService` |
| `copy` | 1 | не-`*Service`-хелпер копирайта ответа | `AnswerCopy.java` (без суффикса Service, не входит в счётчик 49) |

`command`/`facade`/`copy` — по одному файлу: `SubmitAnswerCommand` держит
транзакционную границу (§13), `InterviewFacade` — единственная точка входа
usecase-слоя, `AnswerCopy` — генератор текста без side-effects.

### 3.2 `feature/interview/usecase/*` — API/MVC-адаптеры (11 файлов, 2 подпакета)

`AnswerApiService`, `BookmarkApiService`, `ConfidenceApiService`,
`FavoriteApiService`, `IssueReportApiService`, `NextQuestionApiService` —
прямые адаптеры REST-хендлеров к `InterviewFacade`. `usecase/mvc/` — 2 файла
(`InterviewFlowMvcService`, `InterviewPageMvcService`) для
`InterviewMvcController`. `usecase/stats/` — 3 `*Service.java`
(`LearningMetricsService`, `StatsApiService`, `StreakApiService`) + 1 без
суффикса (`MetricDefinitions.java`, 4 файла всего).

### 3.3 Остальные фичи и cross-cutting (11 файлов)

`feature/export/` — `ExportService`, `ExportApiService`. `feature/auth/` —
`LoginPageService`. `feature/admin/` — `AdminMaintenanceService`.
`config/web/` — `CssBundleService`, `JsPreloadService`. `infrastructure/` —
`MarkdownRenderService`, `SearchService`. `service/imports/` —
`HashingService`, `QuestionImportService` (без суффикса — `McqJsonLoader`,
загрузка `seed/mcq/**.json`, не в счётчике 49). `api/security/` —
`SensitiveEndpointAccessService`.

Сумма: 27 (ядро §3.1) + 11 (§3.2) + 11 (§3.3) = 49.

---

## 4. Repositories

16 классов/интерфейсов `*Repository*` в `quiz-persistence`
(`find modules/quiz-persistence/src/main -iname '*Repository*.java' | wc -l`).
15 из них напрямую держат `JdbcTemplate`
(`grep -l JdbcTemplate modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/*.java | wc -l`
→ 15); ни один не использует `NamedParameterJdbcTemplate` (0 хитов) — все
запросы позиционные (`?`).

| Класс | Таблица(ы) | Тип |
|---|---|---|
| `QuestionRepository` | `questions` | класс, `JdbcTemplate` |
| `AttemptRepository` | `attempt` | класс |
| `ReviewStateRepository` | `review_state` | класс, `@Transactional` |
| `SessionHistoryRepository` | `session_history` | класс |
| `UserExperienceRepository` | `user_experience` | класс |
| `BookmarkRepository` | `bookmark` | класс |
| `AnswerOptionRepository` | `answer_options` | класс, `@Transactional` |
| `HintRepository` | `question_hints` | класс, `@Transactional` |
| `DailyActivityRepository` | `daily_activity` | класс |
| `UserTopicStatsRepository` | `user_topic_stats` | класс, `@Transactional` |
| `QuestionStatsRepository` | `questions` (агрегаты, JOIN `review_state`) | класс |
| `QuestionIssueRepository` | `question_issue` | класс |
| `QuestionRevisionRepository` | `question_revision` | класс |
| `PausedSessionRepository` | `paused_session` | класс |
| `FullTextSearchRepository` | — | **интерфейс**, контракт FTS |
| `PostgresFullTextSearchRepository` | `questions` (`ts_vector`) | класс, `implements FullTextSearchRepository` |

Вне списка, но в том же пакете — `OwnerContext.java`, ThreadLocal-держатель
`owner_key` для всех 16 (§10, §11), и `InterviewFilterSql`/`FtsQueryUtils` —
SQL-билдеры без суффикса Repository (пример: `InterviewFilterSql.java:75`,
условие `q.is_important = 1`). `QuestionRepository` — источник правды для
`questions`: CRUD на 17 колонках (`:48`) + легаси `toggleImportant` (`:221`,
см. §18).

---

## 5. DTO и мапперы

27 DTO-файлов в 4 пакетах:

| Пакет | Файлов | Назначение |
|---|---|---|
| `api/dto/request/interview/` | 5 (+`api/dto/package-info.java`) | входные REST/MVC-запросы: `BookmarkNoteRequest`, `QuestionIdRequest`, `ReportIssueRequest`, `StartSessionRequest`, `SubmitAnswerRequest` |
| `feature/admin/dto/response/` | 1 | `AdminOperationResult` |
| `feature/interview/dto/response/**` | 16 | ответы по 4 подпакетам: `answer/` (4), `progress/` (7), `session/` (3), корень (2: `NextQuestionOptionDto`, `NextQuestionResponse`) |
| `service/imports/dto/` | 4 | `McqSeed`, `McqSeedBlock`, `McqSeedOption`, `McqSeedQuestion` — модель JSON-сида MCQ, не REST-DTO |

Response-DTO режут поля под конкретный экран/эндпоинт, не совпадая 1:1 с
доменной моделью — пример:
`feature/interview/dto/response/answer/AnswerResponse.java` ≠ `AnswerResult`,
`SessionInfoDto` ≠ `InterviewSession`.

9 мапперов в 2 пакетах:

| Пакет | Файл | Роль |
|---|---|---|
| `api/mapper/response/` | `StatsApiMapper`, `InterviewDtoMapper` | domain → response DTO |
| `api/mapper/view/` | `MvcModelAttributeMapper`, `SessionLog` | domain → `Model`-атрибуты Thymeleaf (`flashcardMode`, см. §18) |
| `api/mapper/request/` | `ApiRequestMapper`, `MvcRequestMapper`, `MvcAnswerRequestMapper` | request DTO → доменные команды |
| `feature/interview/mapper/` | `NextQuestionResponseMapper`, `ContentBlockMapper` | доменный `ContentBlock`/вопрос → DTO |

---

## 6. Domain model

`quiz-domain` — 53 java-файла (51 тип + 2 `package-info.java`, в пакетах
`domain/` и `domain/exception/`), без единой Spring-аннотации и без импортов
JDBC (ArchUnit это закрепляет, §1). Крупнейшие типы —
`SessionSummary.java` (14983 байт) и `InterviewSession.java` (13727 байт).

По смысловым кластерам (файловая раскладка плоская, кластеризация — по
имени/назначению класса, не по пакету):

| Кластер | Типы |
|---|---|
| Вопрос и контент | `Question`, `InterviewQuestion`, `QuestionOption`, `AnswerOption`, `ContentBlock`, `BlockType`, `Hint`, `QuestionDifficulty`, `Difficulty`, `QuestionType`, `QuestionRevision`, `RelatedQuestion`, `TopicMap` |
| Сессия и режимы | `InterviewSession`, `InterviewMode`, `InterviewFilter`, `SessionRecord`, `SessionSummary`, `StudyPhase`, `FlashcardPhase`, `FlashcardGrade`, `AnswerDisplayMode` |
| Ответы и попытки | `Attempt`, `AttemptOutcome`, `AnswerResult` |
| Повторение (SM-2/FSRS) | `ReviewState`, `ReviewReason`, `ReviewResult`, `ReviewDefaults`, `FsrsState`, `FsrsRating` |
| Прогресс/геймификация | `UserExperience`, `Progression`, `GuildRank`, `Achievement`, `DailyProgress`, `StreakInfo`, `NextActionType` |
| Статистика | `InterviewStats`, `TopicStats`, `LearningMetrics`, `ProgressExportRow` |
| Пользовательские отметки | `Bookmark`, `QuestionIssue`, `QuestionIssueCategory`, `QuestionIssueStatus`, `PausedSessionInfo` |
| Служебные | `OptionSource`, `RussianPlural` |
| Исключения (`domain/exception/`) | `OptionNotFoundException`, `QuestionNotFoundException` |

Конструкторы/фабрики — единственное место, где домен отказывает до
персистентности (доменная валидация отделена от Jakarta-аннотаций на DTO,
см. §7).

---

## 7. Validation

Jakarta Bean Validation задействован на 5 request-DTO:
`ReportIssueRequest`, `BookmarkNoteRequest`, `StartSessionRequest`,
`QuestionIdRequest`, `SubmitAnswerRequest`
(`api/dto/request/interview/*.java`, найдено через `grep -rl
'@NotNull\|@NotBlank\|@Size\|@Positive\|@Min\|@Max\|@Valid'`).
Контроллеры активируют `@Valid`/`@Validated` на уровне класса
(`InterviewMvcController.java:31` — `@Validated`; параметры хендлеров несут
`@Min`/`@Max`/`@Positive`, например `InterviewMvcController.java:188-215`).

Единственный кастомный валидатор — `@ValidIssueCategory`
(`api/validation/ValidIssueCategory.java:29`): composed-аннотация +
`ConstraintValidator`, собирает допустимые значения из
`QuestionIssueCategory.values()`, а не дублирует enum строкой сообщения
вручную (`:22-24`).

Доменные проверки живут отдельно — в конструкторах/фабриках `quiz-domain`
(§6): DTO валидируется на входе HTTP, домен — при построении объекта. Слои
не пересекаются файлово: ни один файл `quiz-domain` не даёт совпадений
`@NotNull|@Valid` при `grep -rl` по модулю.

---

## 8. Error handling

`GlobalExceptionHandler.java` (`common/exception/`, 17.4K) — единая точка,
`@ControllerAdvice`, 14 методов `@ExceptionHandler` на 15 типов исключений
(один хендлер группирует 2 типа):

| Строка | Исключение(я) | Ветвь ответа |
|---|---|---|
| `:42` | `QuestionNotFoundException` | 404 |
| `:51` | `OptionNotFoundException` | 404 |
| `:60` | `MethodArgumentNotValidException`, `BindException` | 400 (Bean Validation) |
| `:82` | `ConstraintViolationException` | 400 |
| `:90` | `MethodArgumentTypeMismatchException` | 400 |
| `:96` | `RevisionMismatchException` | 409 (оптимистическая блокировка ревизии) |
| `:113` | `IllegalStateException` | 409/500 в зависимости от контекста |
| `:123` | `UnsupportedExportFormatException` | 400 (экспорт) |
| `:128` | `ExportSerializationException` | 500 (экспорт) |
| `:139` | `RateLimitExceededException` | 429 + `Retry-After` |
| `:151` | `DataIntegrityViolationException` | 409/400 (нарушение constraint БД) |
| `:165` | `NoResourceFoundException` | 404 (статика) |
| `:193` | `ResponseStatusException` | passthrough статуса |
| `:209` | `Exception` | 500, catch-all |

`ErrorViewAttributes.java` (68 строк, тот же пакет) — вспомогательный класс
для рендера `error.html`, не хендлер сам по себе (без `@ExceptionHandler`).
Доменные исключения (`QuestionNotFoundException`, `OptionNotFoundException`)
живут в `quiz-domain/domain/exception/` — единственное место, где домен
всё-таки «протекает» в HTTP-слой не через DTO, а напрямую через тип
исключения, перехватываемый `GlobalExceptionHandler`.

5 не-доменных исключений: `common/exception/RateLimitExceededException.java`,
`feature/interview/service/review/RevisionMismatchException.java`,
`feature/export/exception/{ExportSerializationException,
UnsupportedExportFormatException}.java`,
`service/imports/InvalidMcqSeedException.java` (последнее — исключение
стартап-загрузки MCQ, не проходит через `GlobalExceptionHandler`, см. §18).

---

## 9. Authentication

Единственный переключатель — `app.security.mode` (`local` | `multi`),
задаётся `${APP_SECURITY_MODE:local}` (`application.yml:143`) и
провалидирован regex `local|multi` (`AppProperties.java:202`).

- **`local`** — единый неаутентифицированный trust boundary
  (`SecurityConfig.java:115`). `SecurityModeStartupGuard`
  (`ApplicationRunner`) фейлит старт под prod-профилем при `mode=local` без
  явного `allow-insecure-public` (`SecurityModeStartupGuard.java:44`).
- **`multi`** — `MultiUserSecurityConfig.java` поднимает in-memory
  HTTP Basic/form-аккаунты («Skeleton multi-user identity»,
  `MultiUserSecurityConfig.java:19`); дефолтные креды `trainer`/`trainer`
  (`application.yml:147-148`) под prod запрещены тем же guard
  (`SecurityModeStartupGuard.java:53`). `LoginController` — бин только в
  этом режиме (`@ConditionalOnProperty`, `LoginController.java:18`).
- **OAuth2/OIDC** — opt-in через `app.security.oauth.enabled`
  (`application.yml:156`); `OAuthSecurityStartupGuard.java` фейлит старт,
  если `oauth.enabled=true` без `mode=multi` (`:38`). Группы IdP матчатся на
  `ROLE_ADMIN` через `OrgIdpGroupAuthoritiesMapper` (`api/security/`).

`LoginPageService` собирает витрину вопросов (`bankOverview`) для
неаутентифицированной `/login` (`LoginController.java:42-43`).

---

## 10. Authorization

Два независимых механизма, не дублирующие друг друга:

**1. Owner-scoping мутируемого прогресса.** `OwnerContextFilter`
(`config/web/OwnerContextFilter.java`) — `OncePerRequestFilter`,
зарегистрирован в `SecurityConfig` *после* `SecurityContextHolderFilter`
(более ранний `@Order` всегда бы биндил `local`, `:17-18`). На каждый запрос
кладёт `OwnerContext.set(ownershipKey())` (`:29`) и чистит в `finally`
(`:33`). `CurrentUserAccessor.ownershipKey()`
(`api/security/CurrentUserAccessor.java:34-51`) резолвит ключ: `local` в
одиночном режиме, `username` при form/basic, `oauth:{registrationId}:
{subject}` при OAuth2, сегменты прогоняются через `safeSegment` (regex
`[^a-zA-Z0-9._@+-]` → `_`, `:53-62`), ключ обрезается до 256 символов.
`OwnerContext.java` (`quiz-persistence`) хранит значение в `ThreadLocal`;
`sanitize()` **бросает** исключение на небезопасный ключ вместо тихого
fallback на `local` — `// Never collapse bad keys to shared "local" (would
mix tenants)` (`OwnerContext.java:44`).

**2. Admin-token / ROLE_ADMIN на чувствительных endpoint.**
`app.admin-token` (`application.yml:100`, дефолт пусто → эндпоинты закрыты)
проверяется `SensitiveEndpointAccessService`
(`api/security/SensitiveEndpointAccessService.java:38`) для `/export` и
`/settings/reset-options`; альтернатива — `ROLE_ADMIN` через
`OrgIdpGroupAuthoritiesMapper`. Лимит частоты —
`app.sensitive-rate-limit-per-minute` (дефолт 20, `application.yml:101`),
окно 60с (`SensitiveEndpointAccessService.java:41-42`), считает
`RequestRateLimiter` (61 строка). Отказ — единый `ProblemDetail` через
`SensitiveEndpointAccessDeniedHandler`/`SensitiveEndpointAuthenticationEntryPoint`.

Owner-scoping покрывает 5 таблиц (`review_state`, `bookmark`,
`paused_session`, `attempt`, `attempt_idempotency_claim` — `V24`) + 2
(`user_experience`/`daily_activity`-семейство — `V25`) + `question_issue`
(`V26`) = все мутируемые таблицы прогресса **кроме** флага `is_important`
на `questions` (см. §18 — глобальный, не per-owner).

---

## 11. Persistence

14 таблиц (`grep -hoE 'CREATE TABLE( IF NOT EXISTS)? [a-z_]+'` по всем
миграциям, уникализировано): `questions`, `answer_options`,
`question_hints`, `daily_activity`, `paused_session`, `question_issue`,
`attempt`, `user_experience`, `bookmark`, `attempt_idempotency_claim`,
`question_revision`, `review_state`, `user_topic_stats`, `session_history`.

Каждая таблица владеется ровно одним репозиторием (см. таблицу §4), кроме
`questions`, которую читают три: `QuestionRepository` (CRUD),
`QuestionStatsRepository` (агрегаты + JOIN `review_state`),
`PostgresFullTextSearchRepository` (полнотекстовый поиск через
`to_tsvector`, колонка добавлена `V2__fulltext.sql`).

Уникальные ограничения/PK — 34 вхождения `UNIQUE`/`PRIMARY KEY` по всем
миграциям (`grep -rn 'UNIQUE\|PRIMARY KEY' db/migration/*.sql | wc -l`).
Показательные: составной `PRIMARY KEY (owner_key, question_id)` на
`review_state` (`V24:6`); частичный `uq_attempt_first_per_owner_question ...
WHERE is_first_attempt = true` (`V24:24-25`) — одна «первая попытка» на пару
owner/question гарантирована constraint'ом БД, а не кодом;
`uq_answer_options_single_correct_per_question` (`V13__answer_option_integrity.sql:3`)
— ровно один correct-вариант на вопрос, тоже на уровне БД.

FTS — `PostgresFullTextSearchRepository implements FullTextSearchRepository`
(`:23`), запрос против `questions` с `ts_vector`/`ts_rank` (`:49`) —
единственная пара интерфейс/класс среди 16 репозиториев.

---

## 12. Migrations

29 файлов `V1__init.sql`..`V29__session_history.sql`
(`ls db/migration/*.sql | wc -l`). Полный список с описанием и объёмом:

| # | Файл | Строк | Что делает |
|---|---|---|---|
| 1 | `V1__init.sql` | 42 | базовая таблица `questions` |
| 2 | `V2__fulltext.sql` | 29 | колонка/индекс полнотекстового поиска |
| 3 | `V3__question_expansion.sql` | 3 | `+source_slug` |
| 4 | `V4__code_questions.sql` | 3 | `+question_type` (тип вопроса CODE/TEXT) |
| 5 | `V5__question_hints.sql` | 9 | таблица `question_hints` |
| 6 | `V6__question_diagram_regen.sql` | 5 | `+diagram_mermaid` (nullable) |
| 7 | `V7__add_indexes.sql` | 19 | индексы под частые запросы |
| 8 | `V8__schema_guard_and_indexes.sql` | 19 | schema-guard, доп. индексы |
| 9 | `V9__answer_option_explanation.sql` | 1 | `+explanation` на `answer_options` |
| 10 | `V10__daily_activity.sql` | 7 | таблица `daily_activity` (streak) |
| 11 | `V11__add_takeaway.sql` | 2 | `+takeaway` на `questions` |
| 12 | `V12__answer_option_versions.sql` | 5 | версионирование вариантов ответа |
| 13 | `V13__answer_option_integrity.sql` | 3 | уникальный «один correct на вопрос» |
| 14 | `V14__question_v2_and_user_topic_stats.sql` | 16 | `+difficulty`, таблица `user_topic_stats` |
| 15 | `V15__answer_option_mcq_block_idx.sql` | 12 | `mcq_block_idx`, переделка unique-constraint |
| 16 | `V16__paused_session.sql` | 19 | таблица `paused_session` (FLOW-01) |
| 17 | `V17__question_issue.sql` | 19 | таблица `question_issue` («Сообщить о проблеме») |
| 18 | `V18__attempts_revisions_fsrs.sql` | 66 | таблицы `attempt`, `question_revision`, поля FSRS |
| 19 | `V19__experience.sql` | 25 | `user_experience` (XP, рекорд серии) |
| 20 | `V20__bookmarks.sql` | 18 | таблица `bookmark` (UI-014) |
| 21 | `V21__attempt_opened_blocks.sql` | 9 | телеметрия открытых блоков контента |
| 22 | `V22__attempt_idempotency_claim.sql` | 6 | `attempt_idempotency_claim` (BE-001) |
| 23 | `V23__attempt_first_attempt_unique.sql` | 5 | одна строка `is_first_attempt=true` на вопрос (BE-006) |
| 24 | `V24__owner_key_scoping.sql` | 38 | `+owner_key` на 5 таблиц прогресса, backfill `'local'` |
| 25 | `V25__owner_key_xp_activity.sql` | 19 | `+owner_key` на XP/streak/topic-mastery |
| 26 | `V26__owner_key_question_issue.sql` | 6 | `+owner_key` на `question_issue` |
| 27 | `V27__drop_regen_count.sql` | 6 | **DROP** `regen_count` (счётчик AI-перегенерации вариантов, больше не нужен) |
| 28 | `V28__drop_view_only_review_state.sql` | 31 | **DROP** строк прогресса, заведённых показом без ответа |
| 29 | `V29__session_history.sql` | 29 | таблица `session_history` |

3 миграции содержат `DROP` (`V27`, `V28`, и `DROP CONSTRAINT`/`DROP INDEX`
внутри `V24` при перестройке ограничений под owner_key). Применяются Flyway
из `quiz-persistence` при старте `quiz-app` (`application.yml:65-72`, секция
`flyway`); в тестах — тот же путь миграций против Testcontainers-Postgres
(`AbstractPostgresRepositoryTest`,
`modules/quiz-persistence/src/test/java/com/cheatsheet/quiz/persistence/AbstractPostgresRepositoryTest.java`),
чистых up-only без rollback-скриптов (Flyway community edition их не
поддерживает).

---

## 13. Transactions

16 main-файлов с `@Transactional`
(`grep -rl '@Transactional' modules/quiz-app/src/main modules/quiz-persistence/src/main | wc -l`):

**`quiz-persistence` (5, все репозитории):**
`AnswerOptionRepository`, `HintRepository`, `QuestionRepository`,
`ReviewStateRepository`, `UserTopicStatsRepository`.

**`quiz-app` (11, все сервисы):**
`feature/admin/service/AdminMaintenanceService.java`,
`feature/interview/service/command/SubmitAnswerCommand`,
`feature/interview/service/core/InterviewService`,
`feature/interview/service/progress/{BookmarkService,DailyStreakService,
ExperienceService,FavoriteService}`,
`feature/interview/service/report/QuestionIssueService`,
`feature/interview/service/review/{AttemptRecorder,ReviewService}`,
`feature/interview/usecase/BookmarkApiService`.

Граница держится одновременно в обоих слоях: репозиторий оборачивает
собственный multi-statement SQL (`ReviewStateRepository` — upsert с
составным PK владельца), сервис поверх оборачивает несколько репозиториев в
одну бизнес-операцию (`SubmitAnswerCommand` — попытка + XP + review_state
одним ответом). `PROPAGATION_REQUIRED` — дефолт, ни разу не переопределён
(`grep -rn 'propagation\s*=' ...` → 0). `readOnly` не используется ни разу
(`grep -rn 'readOnly' ...` → 0) — все транзакции read-write по умолчанию.

---

## 14. Caching

Кэш — ручной, не Spring Cache Abstraction: `@Cacheable`/`@EnableCaching` в
main-коде отсутствуют (`grep -rn '@Cacheable\|@EnableCaching'
modules/quiz-app/src/main` → 0 совпадений).

- `OptionCache` (`service/cache/OptionCache.java`, 44 строки) — интерфейс:
  `get`/`put`/`invalidate`/`invalidate All`, ключ — `long questionId`,
  значение — `List<AnswerOption>`.
- `CaffeineOptionCache` (52 строки) — единственная реализация.
- `CacheConfig` (`config/runtime/CacheConfig.java`) — Caffeine-бин
  `optionCacheBackend`, размер/TTL из `AppProperties.Cache` (`:24-28`),
  `recordStats()` обязателен для метрик (`:26`), публикуются в Micrometer
  под тегом `cache=optionCache` (`:31`). Там же — `Striped<Lock>
  questionLocks()` (128 полос) от гонки при загрузке одного вопроса.
- Параметры: `app.cache.option-max-size=500`, `app.cache.ttl-hours=1`
  (`application.yml:121-122`, `AppProperties.java:186,190`).

Инвалидация — точечная (`invalidate`) при изменении контента вопроса, либо
полная (`invalidateAll`) при перегенерации; call site `invalidate*` не
входили в скоуп статического чтения (поведение времени исполнения, §18).

---

## 15. External integrations

HTTP-клиентов к внешним сервисам **нет**: `grep -rn
'org.springframework.ai|WebClient|RestTemplate|RestClient|reactor.core'
--include='*.java' modules/quiz-app/src/main` даёт единственный хит — javadoc
в `config/package-info.java:7`, ссылающийся на несуществующий раздел
«WebClient» (фактический `InfrastructureConfig.java` объявляет только бин
`Clock`, `WebClient`-бина нет; см. §18).

При этом `modules/quiz-app/build.gradle.kts` объявляет две web-зависимости
без потребителей в main-коде (0 файлов с `WebClient`, 0 с
`org.springframework.ai`, тот же grep): `implementation(libs.spring.boot.
starter.webflux)` (`:14`) и `implementation(libs.spring.ai.openai)` (`:44`,
+ BOM `:43`). Согласуется с `CLAUDE.md` проекта («AI в проекте нет»: код
вырезан целиком) — зависимости объявлены в сборке, но не удалены после
вырезания AI-кода. `InfrastructureConfig.java:12` вдобавок ссылается
javadoc-`{@link AiClientConfig}` на класс, которого в репозитории не
существует (`find modules/quiz-app/src/main -iname 'AiClientConfig*'` →
пусто) — мёртвая ссылка того же происхождения.

Единственные фактические внешние источники данных — файловые и БД:

- `cheatsheets/interview/**/*.md` — читает `QuestionImportService`
  (`service/imports/QuestionImportService.java:35`), путь берётся из
  `app.interview-path` (`application.yml:95`, дефолт
  `cheatsheets/interview`, путь существует на HEAD).
- `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json` —
  читает `McqJsonLoader` (`service/imports/McqJsonLoader.java:123`),
  валидируется JSON Schema `seed/mcq-schema.json`
  (`McqJsonLoader.java:107`).
- PostgreSQL — единственная БД (`modules/quiz-persistence/build.gradle.kts`,
  `runtimeOnly(libs.postgresql)` в `quiz-app/build.gradle.kts:30`).

---

## 16. Конфигурация бэкенда

`AppProperties.java` (302 строки, `@ConfigurationProperties(prefix =
"app")`) — единая точка чтения всех `app.*`. Ключевые группы (свойство →
дефолт → где читается):

| Свойство | Дефолт | Класс:строка |
|---|---|---|
| `app.interview-path` | `cheatsheets/interview` (обязателен, `@NotBlank`) | `AppProperties.java:42-43` |
| `app.topic-groups-config` | `topic-groups.yaml` | `application.yml:97` |
| `app.admin-token` | пусто | `application.yml:100` |
| `app.sensitive-rate-limit-per-minute` | 20 | `application.yml:101` |
| `app.scheduling.algorithm` | `sm2` (альт. `fsrs`) | `application.yml:107` |
| `app.interview.default-exam-count` | 20 | `application.yml:114` |
| `app.interview.max-session-count` | 200 | `application.yml:113` |
| `app.interview.default-marathon-count` | 50 | `application.yml:115` |
| `app.interview.default-daily-goal` | 10 | `application.yml:116` |
| `app.interview.learned-repetitions` | 3 | `application.yml:111` |
| `app.interview.exam-penalty-questions` | 5 | `application.yml:112` |
| `app.cache.option-max-size` / `ttl-hours` | 500 / 1 | `AppProperties.java:186,190` |
| `app.search.max-limit` | 100 | `application.yml:126` |
| `app.import-settings.min-code-block-length` | 50 | `application.yml:132` |
| `app.interview-reset-on-startup` | `false` | `application.yml:138` |
| `app.security.mode` | `local` (`local\|multi`, `@Pattern`) | `AppProperties.java:202` |
| `app.security.allow-insecure-public` | `false` | `application.yml:145` |
| `app.security.multi.username/password` | `trainer`/`trainer` | `application.yml:147-148` |
| `app.security.oauth.enabled` | `false` | `application.yml:156` |
| `app.security.cors.*` | localhost:8080 origins | `application.yml:164-184` |

`application.yml` — 236 строк, также конфигурирует `server.port`
(`${SERVER_PORT:8080}`), `spring.datasource.*`, `spring.flyway.*`,
`spring.thymeleaf.*`, `management.*`. `CLAUDE.md` проекта утверждает, что
prod-профиль отключает Swagger UI, но `application-prod.yml` в ресурсах не
найден (см. §18).

---

## 17. Тесты бэкенда

`quiz-app`: 194 java-файла в `src/test`, 189 — `*Test.java`. Из них 76 лежат
в `ui/` (75 `*Test.java` + 1 хелпер) — фронт-контракты, уже учтённые
предшественником (`.sdlc/CS-20260827-001/report.md` §16). Backend-тестов вне
`ui/`: **114** `*Test.java` (`find modules/quiz-app/src/test -name
'*Test.java' -not -path '*/ui/*' | wc -l`) — точнее оценки design.md
(«порядка 113»: там 76 вычли целиком, не отделив 1 не-`Test`-хелпер).

`quiz-persistence`: 20 тест-файлов на Testcontainers-PostgreSQL,
наследуются от `AbstractPostgresRepositoryTest` (один контейнер на JVM,
`TRUNCATE` между тестами). `quiz-domain`: 21 тест-файл, unit, без Spring-
контекста.

Гейты покрытия — `modules/quiz-app/build.gradle.kts:117-153`: BUNDLE
`LINE ≥ 0.85` / `BRANCH ≥ 0.70` (замер 2026-08-04 в комментарии:
`LINE 0.915`, `BRANCH 0.764`, `:117`), PACKAGE `LINE ≥ 0.55` (`:151-153`).
Замер комментарием — не проверка «здесь и сейчас»: свежий
`jacocoTestCoverageVerification` в скоуп этой артефакт-only задачи не
входит.

Архитектурные правила — `architecture/LayeredArchitectureTest.java` (§1).
Чего не покрыто статическим чтением не видно — требует `jacocoTestReport`
с построчной картой (§18).

---

## 18. Белые пятна разведки

1. **Реальные SQL-планы горячих путей.** 15 репозиториев на позиционном
   `JdbcTemplate` — не видно статикой, есть ли N+1 при сборке
   `NextQuestionResponse` (вопрос+варианты+подсказки+связанные — до 4
   запросов на один HTTP-запрос). Проверка: `EXPLAIN ANALYZE` через bootRun
   на 8091 с `logging.level.org.springframework.jdbc=DEBUG`.
2. **Поведение Caffeine-кэша под нагрузкой.** Не проследены все call site
   `optionCache.invalidate`/`invalidateAll` до вызывающего кода — не
   доказано, что каждая запись варианта ответа реально инвалидирует кэш.
   Проверка: `grep -rn 'optionCache.invalidate' modules/quiz-app/src/main`
   + трассировка каждого места.
3. **`is_important` вне owner-scoping.** Флаг «избранное» на `questions`
   (`V1__init.sql:8`) — глобальный столбец
   (`QuestionRepository.toggleImportant`, `:214-224`), без `owner_key`, в
   отличие от `bookmark` (owner-scoped с `V24`). В `multi`-режиме один
   аккаунт потенциально переключает «избранное», видимое всем (легаси-
   синхронизация с `bookmark`, UI-014, `BookmarkService.java:82`). Проверка:
   наблюдение в `multi` на двух аккаунтах.
4. **`InvalidMcqSeedException` при старте.** Не проходит через
   `GlobalExceptionHandler` (§8) — брошено при загрузке `seed/mcq/**.json`
   на старте. Неясно, падает ли весь контекст Spring или топик молча
   пропускается. Проверка: испортить один `seed/mcq/*.json`, поднять
   bootRun на 8091, прочитать лог/exit code.
5. **`application-prod.yml`.** `CLAUDE.md` утверждает «prod отключает
   Swagger UI», но отдельного YAML профиля `prod` не найдено (`find ...
   -iname 'application-prod*'` → пусто) — вероятно, `@Profile`-аннотация в
   коде. Проверка: `grep -rn '@Profile("prod")' modules/quiz-app/src/main`
   + прогон с `SPRING_PROFILES_ACTIVE=prod`.

---

## Приложение A. Команды воспроизведения

```bash
# §0/§1 — модули и LOC
git rev-parse HEAD && git branch --show-current && cat settings.gradle.kts
for m in quiz-domain quiz-persistence quiz-app; do find modules/$m/src/main -name '*.java' | wc -l; find modules/$m/src/main -name '*.java' -exec cat {} + | wc -l; done
for d in feature service api common config infrastructure; do find modules/quiz-app/src/main/java/com/cheatsheet/quiz/$d -name '*.java' | wc -l; done
find modules/quiz-app/src/main/java/com/cheatsheet/quiz -maxdepth 1 -name '*.java'
# §2 — controllers (5, не считая @ControllerAdvice)
grep -lE '^\s*@(RestController|Controller)\b' -r modules/quiz-app/src/main --include='*.java'
grep -n '@GetMapping\|@PostMapping\|@PutMapping\|@DeleteMapping' modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewApiController.java
# §3 — services (49)
find modules/quiz-app/src/main -name '*Service.java' | wc -l && find modules/quiz-app/src/main -name '*Service.java' | sort
# §4 — repositories (16)
find modules/quiz-persistence/src/main -iname '*Repository*.java' | wc -l
grep -l 'JdbcTemplate' modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/*.java | wc -l
grep -rl 'NamedParameterJdbcTemplate' modules/quiz-persistence/src/main --include='*.java' | wc -l
# §5 — DTO/mapper
find modules/quiz-app/src/main -path '*/dto/*' -name '*.java' | wc -l && find modules/quiz-app/src/main -path '*/mapper/*' -name '*.java' | wc -l
# §6 — domain (53 файла = 51 тип + 2 package-info)
find modules/quiz-domain/src/main -name '*.java' | wc -l && find modules/quiz-domain/src/main -name 'package-info.java' | wc -l
# §7 — validation
grep -rl '@NotNull\|@NotBlank\|@Size\|@Positive\|@Min\|@Max\|@Valid' --include='*.java' modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/dto modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/dto
find modules/quiz-app/src/main -path '*validation*' -name '*.java'
# §8 — error handling (14 хендлеров)
grep -n '@ExceptionHandler' modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/exception/GlobalExceptionHandler.java
# §9/§10 — security
grep -rn 'security.mode' modules/quiz-app/src/main/java --include='*.java'
find modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/security -name '*.java'
# §11/§12 — persistence/migrations (14 таблиц, 29 миграций)
grep -rhoE 'CREATE TABLE( IF NOT EXISTS)? [a-z_]+' modules/quiz-persistence/src/main/resources/db/migration/*.sql | awk '{print $NF}' | sort -u
ls modules/quiz-persistence/src/main/resources/db/migration/*.sql | wc -l
grep -rn 'UNIQUE\|PRIMARY KEY' modules/quiz-persistence/src/main/resources/db/migration/*.sql | wc -l
# §13 — transactions (16 файлов)
grep -rl '@Transactional' --include='*.java' modules/quiz-app/src/main modules/quiz-persistence/src/main | sort
grep -rl '@Transactional' --include='*.java' modules/quiz-app/src/main modules/quiz-persistence/src/main | wc -l
grep -rn 'propagation\s*=\|readOnly' --include='*.java' modules/quiz-app/src/main modules/quiz-persistence/src/main | wc -l
# §14 — caching
grep -rn '@Cacheable\|@EnableCaching' modules/quiz-app/src/main --include='*.java'
# §15 — external integrations
grep -n 'webflux\|spring-ai\|openai' modules/quiz-app/build.gradle.kts
grep -rn 'org.springframework.ai\|WebClient\|RestTemplate\|RestClient\|reactor.core' --include='*.java' modules/quiz-app/src/main
find modules/quiz-app/src/main -iname 'AiClientConfig*'
test -d cheatsheets/interview && test -d modules/quiz-app/src/main/resources/seed/mcq && echo OK
# §17 — tests (114 backend / 76 ui / 20 persistence / 21 domain)
find modules/quiz-app/src/test -name '*.java' | wc -l && find modules/quiz-app/src/test -name '*Test.java' | wc -l
find modules/quiz-app/src/test -path '*/ui/*' -name '*.java' | wc -l && find modules/quiz-app/src/test -name '*Test.java' -not -path '*/ui/*' | wc -l
find modules/quiz-persistence/src/test -name '*.java' | wc -l && find modules/quiz-domain/src/test -name '*.java' | wc -l
grep -n 'LINE\|BRANCH\|minimum' modules/quiz-app/build.gradle.kts
# AC8 — существование ВСЕХ путей, упомянутых в отчёте (авто-извлечение, не ручной список)
grep -oE '(modules|cheatsheets|e2e|docs|\.sdlc)/[A-Za-z0-9_./-]+' .sdlc/CS-20260828-001/report.md \
  | sed -E 's/[):,.]+$//; s/:[0-9]+(-[0-9]+)?$//' | sort -u | while read -r p; do test -e "$p" || echo "MISSING: $p"; done
# ожидаемый вывод: пусто
```
