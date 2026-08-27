# CS-20260827-002: Phase 0 разведка бэкенда (контроллеры, сервисы, персистентность)

## Контекст

Задача — research: единственный артефакт `.sdlc/CS-20260827-002/report.md`,
DoD-гейт `test -s .sdlc/CS-20260827-002/report.md`. Код не меняем.

Источник требования — миссия `cheat-sheet-product-rebuild.md`, §5 PHASE 0 —
REPOSITORY RECONNAISSANCE, блок BACKEND. Четырнадцать рубрик: controllers;
services; repositories; DTO; domain model; validation; error handling;
authentication; authorization; persistence; migrations; caching; transactions;
external integrations. Смысл фазы — карта AS-IS перед перестройкой продукта, а
не приговор («что чинить» — §7–§17 миссии). Задача-близнец по фронтенду —
CS-20260827-001 (`report.md` по 16 рубрикам FRONTEND), её формат берём за образец.

**Что в бэкенде есть на HEAD `f78dd6ca` (2026-08-27), измерено по репо:**

Стек — Spring Boot (Java, Lombok) + Spring MVC + Thymeleaf, JDBC без ORM,
PostgreSQL + Flyway, Spring Security, Caffeine. Три Gradle-модуля:
`quiz-domain` (53 main / 21 test java-файлов), `quiz-persistence` (21 / 20),
`quiz-app` (171 / 192). Всего 245 main + 233 test java-файлов, 23 015 строк main.

- **Контроллеры — 5** (`@Controller`/`@RestController`, main):
  `feature/interview/controller/InterviewMvcController.java` (428 строк, MVC-страницы
  и form-POST), `feature/interview/controller/InterviewApiController.java` (JSON),
  `feature/export/controller/ExportController.java`,
  `feature/auth/controller/LoginController.java`,
  `config/web/CssBundleController.java` (отдаёт склеенный CSS-бандл — контроллер
  живёт вне `feature/`, в `config/web`).
- **Сервисы — 46 `@Service`.** Раскладка `quiz-app`: `feature/interview/**` —
  75 файлов, внутри разделение `usecase/` (тонкие API/MVC-сценарии:
  `AnswerApiService`, `NextQuestionApiService`, `BookmarkApiService`,
  `ConfidenceApiService`, `FavoriteApiService`, `IssueReportApiService`,
  `usecase/mvc/InterviewPageMvcService`, `usecase/mvc/InterviewFlowMvcService`,
  `usecase/stats/*`) и `service/` по доменным группам: `core/` (`InterviewService`
  483 стр., `TrainingSessionService`, `OptionLookupService`), `review/`
  (`ReviewService`, `SpacedRepetitionService`, `FsrsService`, `AttemptRecorder`,
  `ReviewReasonService`), `flow/` (`SessionFlowService`, `PauseService`,
  `ReviewModeService`, `SessionSummaryService`, `SessionHistoryService`),
  `page/` (`StatsPageService` 742 стр. — крупнейший main-файл,
  `FocusTrainingPageService` 418, `TopicsPageService`, `BookmarksPageService`,
  `AnswerPageService`), `progress/` (`ExperienceService`, `DailyStreakService`,
  `BookmarkService`, `FavoriteService`), `topic/` (`TopicCatalogService` 617,
  `AdaptiveDifficultyService`), `insight/`, `report/`, `command/SubmitAnswerCommand`.
  Кросс-срезы вне `feature/`: `service/imports/QuestionImportService` (314),
  `service/cache/`, `service/diagram/`, `service/event/AnswerEvent`,
  `service/strategy/` (`DefaultSelectionStrategy`, `ShuffleSelectionStrategy`,
  `WeakTopicsSelectionStrategy`, интерфейс `QuestionSelectionStrategy`),
  `service/admin/SeniorRulePriorityOverrideStore`,
  `infrastructure/render/MarkdownRenderService` (468),
  `infrastructure/search/SearchService`.
- **Репозитории — 15 `@Repository`**, все в `quiz-persistence`, все на
  `JdbcTemplate`/`NamedParameterJdbcTemplate` (15 из 15 файлов упоминают
  JdbcTemplate; JPA/Hibernate в проекте нет): `QuestionRepository` (674 стр.),
  `QuestionStatsRepository` (663), `AttemptRepository` (313),
  `AnswerOptionRepository`, `ReviewStateRepository`, `BookmarkRepository`,
  `HintRepository`, `DailyActivityRepository`, `UserExperienceRepository`,
  `UserTopicStatsRepository`, `SessionHistoryRepository`, `PausedSessionRepository`,
  `QuestionIssueRepository`, `QuestionRevisionRepository`,
  `PostgresFullTextSearchRepository`.
- **DTO — 27 файлов** в `api/dto/**` (`request/interview/`),
  `feature/interview/dto/response/{answer,progress,session}/`,
  `feature/admin/dto/response/`, `service/imports/dto/`. Мапперы — 9 файлов:
  `api/mapper/request/{ApiRequestMapper,MvcRequestMapper,MvcAnswerRequestMapper}`,
  `api/mapper/response/{InterviewDtoMapper,StatsApiMapper}`,
  `api/mapper/view/{MvcModelAttributeMapper (480 стр.),SessionLog}`,
  `feature/interview/mapper/{ContentBlockMapper,NextQuestionResponseMapper}`.
  MapStruct не используется — мапперы ручные.
- **Домен — 53 файла** в `quiz-domain` (record/enum/POJO без Spring):
  `InterviewQuestion`, `InterviewSession`, `Question`, `QuestionOption`,
  `AnswerOption`, `Attempt`, `AttemptOutcome`, `ReviewState`, `ReviewResult`,
  `ReviewReason`, `ReviewDefaults`, `FsrsState`, `FsrsRating`, `FlashcardPhase`,
  `FlashcardGrade`, `StudyPhase`, `InterviewMode`, `InterviewFilter`,
  `AnswerDisplayMode`, `AnswerResult`, `Difficulty`, `QuestionDifficulty`,
  `QuestionType`, `ContentBlock`, `BlockType`, `Hint`, `Bookmark`, `TopicMap`,
  `TopicStats`, `InterviewStats`, `LearningMetrics`, `StreakInfo`, `DailyProgress`,
  `Progression`, `GuildRank`, `Achievement`, `UserExperience`, `SessionRecord`,
  `SessionSummary`, `PausedSessionInfo`, `NextActionType`, `OptionSource`,
  `QuestionIssue*`, `QuestionRevision`, `RelatedQuestion`, `ProgressExportRow`,
  `RussianPlural`; `domain/exception/` — только `QuestionNotFoundException`,
  `OptionNotFoundException`.
- **Валидация** — Jakarta Bean Validation на DTO и на
  `config/app/AppProperties` (`@Validated`, `@NotBlank/@Min/@Max/@Pattern`,
  вложенные `@Valid` группы Interview/Cache/Search/Security); один кастомный
  аннотационный валидатор `api/validation/ValidIssueCategory`.
- **Обработка ошибок** — `common/exception/GlobalExceptionHandler` (332 стр.,
  14 `@ExceptionHandler`: доменные NotFound, `MethodArgumentNotValid`/`Bind`,
  `ConstraintViolation`, `MethodArgumentTypeMismatch`, `RevisionMismatch`,
  `IllegalState`, экспортные, `RateLimitExceeded`, `DataIntegrityViolation`,
  `NoResourceFound`, `ResponseStatus`, catch-all `Exception`), плюс
  `ErrorViewAttributes` (HTML-ветка) и `RateLimitExceededException`.
- **Аутентификация/авторизация** — `config/web/SecurityConfig` (315 стр.,
  `@ConditionalOnWebApplication(SERVLET)`, permitAll на статику/`/login`/
  `/actuator/health`/swagger, закрытые `/export` и `/settings/reset-options`),
  `MultiUserSecurityConfig`, `OAuth2LoginUserServiceConfig`,
  `OAuthSecurityStartupGuard`, `SecurityModeStartupGuard`, `OwnerContextFilter`
  (`OncePerRequestFilter`, owner-scoping), `api/security/` —
  `CurrentUserAccessor`, `OrgIdpGroupAuthoritiesMapper`, `RequestRateLimiter`,
  `SensitiveEndpointAccessService`, `SensitiveEndpointAccessDeniedHandler`,
  `SensitiveEndpointAuthenticationEntryPoint`. Режимы — `app.security.mode`
  `local`|`multi`.
- **Персистентность и миграции** — 29 Flyway-скриптов `V1..V29`
  (`quiz-persistence/src/main/resources/db/migration/`), 14 таблиц:
  `questions`, `answer_options`, `attempt`, `attempt_idempotency_claim`,
  `review_state`, `bookmark`, `daily_activity`, `paused_session`,
  `question_hints`, `question_issue`, `question_revision`, `session_history`,
  `user_experience`, `user_topic_stats`. Заметная линия миграций — owner-scoping
  (V24–V26) и удаления (V27 `drop_regen_count`, V28 `drop_view_only_review_state`).
- **Кэш** — `service/cache/OptionCache` + `CaffeineOptionCache`, параметры
  `app.cache.option-max-size` / `ttl-hours`. Spring Cache-абстракция
  (`@Cacheable`) не используется.
- **Транзакции** — 48 `@Transactional` в 16 файлах; разложены и по сервисам
  (`ReviewService` 5, `BookmarkService` 5, `InterviewService` 5, `AttemptRecorder` 4),
  и по репозиториям (`QuestionRepository` 7, `HintRepository` 3,
  `AnswerOptionRepository` 3, `ReviewStateRepository` 3) — граница транзакции
  размазана по двум слоям, это факт для инвентаря.
- **Внешние интеграции** — де-факто только PostgreSQL и опциональный OAuth2-IdP
  (`spring-boot-starter-oauth2-client`, включается `app.security.oauth.enabled`).
  Сетевых клиентов в коде нет. При этом `modules/quiz-app/build.gradle.kts`
  тянет `spring-ai-bom` + `spring-ai-openai` и `spring-boot-starter-webflux`,
  а `grep -rl "org.springframework.ai" modules --include=*.java` пуст и WebClient
  в main-коде не встречается — мёртвые зависимости (CLAUDE.md утверждает, что
  AI вырезан целиком; в build-файле остаток).
- **Стартовые/служебные механизмы** — `infrastructure/bootstrap/StartupRunner`
  (импорт вопросов, `app.interview-reset-on-startup`), `QaFixtureRunner`,
  `infrastructure/health/{SeedCoverageHealthIndicator,SeedInfoContributor}`
  (actuator), `config/app/{AppProperties,InfrastructureConfig,QaConfig}`.
- **Тесты бэкенда** — 233 test-файла, структура зеркалит main
  (`api/**`, `feature/**`, `service/**`, `persistence/**`, `domain`,
  `architecture/LayeredArchitectureTest` — ArchUnit); Testcontainers
  PostgreSQL 16-alpine, профиль `test` форсится в `build.gradle.kts`.

**Что уже задокументировано и чего дублировать нельзя.**
`docs/backend-architecture.md` (541 строка, последний коммит `4556a627`,
2026-08-06) описывает модули, пакеты, планирование повторений, обработку ошибок,
безопасность, данные, HTTP-поверхность, идемпотентность, тесты. С тех пор в
`modules/` — 50 коммитов, то есть инвентарная часть частично протухла. Отчёт
обязан фиксировать факты по HEAD и явно назвать расхождения, а не пересказывать.

**Ограничения.** Дельта задачи допустима только внутри `.sdlc/CS-20260827-002/`
(раунд 1 запаркован по `assert-main-tree-touched`). Порт 8080 — живой bootRun
пользователя. Gradle/graphify/playwright запускать нельзя: они пишут в рабочее
дерево (`build/`, `graphify-out/`) и выведут дельту за разрешённый каталог.
Репозиторий параллельно правят другие агенты — отчёт привязывается к SHA.

## Решение

Пишем `.sdlc/CS-20260827-002/report.md` — **инвентарную карту бэкенда по 14
рубрикам §5 BACKEND**, полученную статическим чтением репозитория на
зафиксированном SHA.

Правила содержания (обязательны для каждой рубрики):
1. **Факт + якорь.** Каждое утверждение сопровождается `path:line` либо
   воспроизводимой командой подсчёта. Утверждение без якоря не пишем.
2. **Инвентарь, а не критика.** Оценки и приоритеты — вне скоупа; допустима
   нейтральная пометка «источник правды / дубль / мёртвый код», подтверждённая
   ссылкой (пример: неиспользуемые `spring-ai-*`/`webflux`).
3. **Числа проверяемы.** Каждый счётчик приводится с командой, которой получен.
4. **Привязка ко времени.** В шапке — SHA HEAD, дата, ветка; в конце — секция
   дельты против `docs/backend-architecture.md` (база `4556a627`, 2026-08-06).

Структура `report.md` (жёсткая, порядок сохранять):

```
# Phase 0 — Backend Reconnaissance (CS-20260827-002)
Шапка: SHA HEAD, дата, ветка, метод (статическое чтение репо; приложение и сборка не запускались)
## 0. Сводка одним экраном      ← таблица: рубрика → 1 строка факта → главный якорь
## 1. Модули и границы          ← quiz-domain/persistence/app, зависимости, ArchUnit-правила
## 2. Controllers               ← 5 контроллеров: класс:line, базовый путь, полный список
                                   @GetMapping/@PostMapping (URL | метод | сигнатура | вход | выход)
## 3. Services                  ← карта 46 @Service по группам (usecase/ vs service/**),
                                   для каждой группы: зона ответственности + якорь; вход/выход слоя
## 4. Repositories              ← 15 репозиториев: таблицы, ключевые запросы, JdbcTemplate-стиль,
                                   RowMapper'ы, где сидит owner_key
## 5. DTO и мапперы             ← 27 DTO по направлениям (request/response/view), 9 мапперов,
                                   правило конверсии domain↔DTO, где мапперы обходятся
## 6. Domain model              ← 53 типа: агрегаты/значения/enum, кто владеет инвариантами,
                                   доменные исключения
## 7. Validation                ← Bean Validation на DTO, @Validated AppProperties,
                                   ValidIssueCategory, ручные проверки в сервисах
## 8. Error handling            ← таблица 14 @ExceptionHandler: исключение → статус → тело/вьюха;
                                   HTML- и JSON-ветки, ErrorViewAttributes, error.html
## 9. Authentication            ← режимы app.security.mode local|multi, OAuth2-цепочка,
                                   startup-guard'ы, форма логина
## 10. Authorization            ← правила SecurityConfig (кто что может), sensitive-endpoints,
                                   admin-token, rate limiting, OwnerContextFilter и owner_key
## 11. Persistence              ← схема: 14 таблиц (колонки-ключи, индексы, FK), маппинг на
                                   домен, полнотекстовый поиск, идемпотентность attempt
## 12. Migrations               ← V1..V29: что каждая делает (одной строкой), линии эволюции,
                                   деструктивные шаги, схема-гварды
## 13. Caching                  ← OptionCache/CaffeineOptionCache, ключи, TTL, инвалидация,
                                   что НЕ кэшируется
## 14. Transactions             ← 48 @Transactional: где границы, readOnly, двухслойность
                                   (сервис + репозиторий), места без транзакции
## 15. External integrations    ← фактические (PostgreSQL, опциональный IdP) и заявленные
                                   в build.gradle.kts, но не используемые (spring-ai, webflux)
## 16. Конфигурация и запуск    ← AppProperties-дерево app.*, профили, StartupRunner/QaFixtureRunner,
                                   actuator-индикаторы (это «контекст» рубрик, не отдельная §5-рубрика)
## 17. Тесты бэкенда            ← 233 файла по слоям, Testcontainers, ArchUnit; чего НЕ покрыто
## 18. Дельта против docs/backend-architecture.md (4556a627, 2026-08-06)
                                ← факты расхождения после 50 коммитов, без переоценки
## 19. Белые пятна разведки     ← что не видно статическим методом (реальные планы запросов,
                                   поведение под нагрузкой, фактические статусы ответов),
                                   и каким наблюдением закрыть
## Приложение A. Команды воспроизведения ← все счётчики одной копипастой
```

Метод сбора: `git log/ls-files`, `find|wc`, `grep` по аннотациям
(`@Controller`, `@Service`, `@Repository`, `@*Mapping`, `@Transactional`,
`@ExceptionHandler`), чтение SQL-миграций, `AppProperties`, `SecurityConfig`,
`GlobalExceptionHandler`, репозиториев и каталога тестов. Приложение и Gradle
не запускаются.

Объём: 350–700 строк. Русский язык, короткие абзацы и таблицы (правила репо).

Отвергнутые альтернативы:
- Поднимать приложение на 8091 и снимать реальные ответы/статусы — Phase 0
  инвентаризует исходники; запуск создаёт `build/` в дереве и ломает дельту (YAGNI).
- Строить graphify-граф под отчёт — пишет `graphify-out/` в рабочее дерево.
- Обновить `docs/backend-architecture.md` вместо отчёта — вне разрешённой дельты
  и вне DoD-гейта; расхождения фиксируем в §18 отчёта.
- Смешать инвентарь с планом рефакторинга — это §7–§17 миссии, отдельные задачи.

## Acceptance Criteria

1. Файл `.sdlc/CS-20260827-002/report.md` существует и непуст (DoD-гейт
   `test -s` зелёный).
2. В отчёте присутствуют все секции `## 0`…`## 19` и `## Приложение A` в
   указанном порядке; ни одна из 14 рубрик §5 BACKEND не пропущена.
3. Шапка содержит SHA HEAD, дату и ветку; в §18 явно назван базовый коммит
   (`4556a627`, 2026-08-06) и число коммитов дельты в `modules/`.
4. Каждая рубрика §1–§17 содержит минимум один якорь `path:line` или команду
   подсчёта. Таблица §2 покрывает все `@GetMapping/@PostMapping/@RequestMapping`
   из пяти контроллеров; таблица §8 — все 14 `@ExceptionHandler`
   `GlobalExceptionHandler`; §12 — все 29 миграций `V1..V29` построчно.
5. Все пути, упомянутые в отчёте, существуют на HEAD (проверка из Приложения A:
   каждый `path` резолвится `test -e`).
6. Дельта коммита задачи ограничена `.sdlc/CS-20260827-002/**` —
   `git status --porcelain -uall` не показывает изменений вне этого каталога.
7. Отчёт на русском; объём 350–700 строк.
8. §19 перечисляет не менее трёх конкретных белых пятен с указанием, каким
   наблюдением их закрыть.
9. §15 явно фиксирует статус `spring-ai-openai`/`spring-boot-starter-webflux`
   как объявленных в `modules/quiz-app/build.gradle.kts`, но не используемых в
   main-коде, с командой-доказательством.

## Тест-контракт

Автоматизированных тестов задача не добавляет и не меняет: код не трогается,
проверяемость обеспечивает custom-гейт DoD и самопроверка ссылок.

### unit · — (класс под тестом: —)
- [none] новых/переписанных JUnit-тестов нет — research-задача, дельта только в
  `.sdlc/`; добавление теста ради формы нарушило бы ограничение дельты.
  Стадию `test-writer` пропустить.

Проверки вместо тестов (выполняет implementer, вывод вклеивает в Приложение A):
- `test -s .sdlc/CS-20260827-002/report.md` — DoD-гейт (red_proof зафиксирован).
- Резолв якорей: извлечь из отчёта пути вида `modules/...`, `docs/...`, `e2e/...`
  и прогнать `test -e` — ноль ненайденных.
- Полнота §12: число строк-миграций в отчёте = `ls
  modules/quiz-persistence/src/main/resources/db/migration | wc -l` (29).
- Полнота §8: число строк таблицы = `grep -c "@ExceptionHandler"
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/exception/GlobalExceptionHandler.java` (14).
- `git status --porcelain -uall` — пусто вне `.sdlc/CS-20260827-002/`.

## Риски

- **Дрейф репо во время работы.** Другие агенты коммитят параллельно; отчёт без
  SHA станет ложью. Митигация — SHA в шапке, запрет «сглаживать» расхождения.
- **Выход дельты за `.sdlc/`.** Любой `./gradlew`, `/graphify` или Playwright
  создаёт файлы в дереве и паркует задачу (прецедент — раунд 1). Митигация —
  метод строго статический.
- **Объём.** 14 рубрик × 46 сервисов × 29 миграций легко дают 1500 строк
  нечитаемого текста. Митигация — верхняя граница 700 строк, §0 «сводка одним
  экраном», группировка сервисов по каталогам, а не по одному файлу на строку.
- **Сползание в аудит.** Соблазн писать «граница транзакции размазана — плохо»
  вместо «размазана по двум слоям, якоря такие-то». Митигация — правило 2 и AC.
- **Копирование протухшего.** `docs/backend-architecture.md` заманчиво
  переиспользовать целиком; после 50 коммитов часть утверждений неверна.
  Митигация — §18 и требование якорей по HEAD.
- **Неполные счётчики из-за zsh-глоба.** `grep --include=*.java` в этой оболочке
  падает («no matches found») и молча даёт 0. Митигация — в Приложении A
  все шаблоны в кавычках, счётчики перепроверяются двумя способами.

## Follow-ups

- Провести Phase 0 разведку инфраструктуры и тестирования (§5 INFRASTRUCTURE + TESTING: сборка, деплой, окружение, конфигурация, логирование, мониторинг; unit/integration/E2E/visual/a11y/API-покрытие) — отчёт `.sdlc/<ID>/report.md`, DoD custom-гейт.
- Удалить мёртвые зависимости `spring-ai-bom`/`spring-ai-openai` и `spring-boot-starter-webflux` из `modules/quiz-app/build.gradle.kts` (в main-коде нет ни одного импорта) — DoD: `./gradlew build -x test` зелёный, grep по `org.springframework.ai` пуст.
- Актуализировать `docs/backend-architecture.md` по дельте §18 отчёта Phase 0 (50 коммитов в modules/ после 2026-08-06) — DoD: каждое утверждение документа имеет якорь `path:line`, существующий на HEAD.
