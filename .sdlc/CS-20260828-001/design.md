# CS-20260828-001: Phase 0 разведка бэкенда

## Контекст

Задача-артефакт: единственный результат — `.sdlc/CS-20260828-001/report.md`,
DoD-гейт `test -s .sdlc/CS-20260828-001/report.md`. Код не меняем.

Источник требования — миссия `cheat-sheet-product-rebuild.md`, §5 PHASE 0 —
REPOSITORY RECONNAISSANCE, блок BACKEND. Дословный перечень рубрик (14, а не 11
как в укороченном заголовке задачи): controllers; services; repositories; DTO;
domain model; validation; error handling; **authentication; authorization**;
persistence; migrations; caching; transactions; **external integrations**.
Три выделенные рубрики в заголовке задачи потерялись при обрезке строки — они
внутри той же строки миссии, поэтому входят в скоуп.

Прямой предшественник — `.sdlc/CS-20260827-001/report.md` (661 строка,
Phase 0 фронтенда, коммит `ea458932`). Его формат ратифицирован ревью и
воспроизводится здесь: факт + якорь `path:line`, инвентарь вместо критики,
каждое число с командой пересчёта, привязка к SHA.

**Что в бэкенде есть на HEAD `ea458932` (2026-08-28), измерено по репо:**

Три Gradle-модуля (`settings.gradle.kts`), Spring Boot MVC + Spring JDBC,
PostgreSQL — единственная БД, Flyway-миграции.
- `quiz-domain` — 53 java-файла, 2934 строки main; ~49 доменных типов
  (`InterviewSession.java` 13.7K и `SessionSummary.java` 14.9K — самые крупные)
  + пакет `domain/exception/`. Ни Spring, ни JDBC внутри.
- `quiz-persistence` — 21 java-файл, 3103 строки main; 16 классов/интерфейсов
  `*Repository*` (`QuestionRepository`, `AttemptRepository`, `ReviewStateRepository`,
  `SessionHistoryRepository`, `UserExperienceRepository`, `BookmarkRepository`,
  `AnswerOptionRepository`, `HintRepository`, `DailyActivityRepository`,
  `UserTopicStatsRepository`, `QuestionStatsRepository`, `QuestionIssueRepository`,
  `QuestionRevisionRepository`, `PausedSessionRepository`,
  `FullTextSearchRepository` + `PostgresFullTextSearchRepository`), Flyway-скрипты
  `src/main/resources/db/migration/V1..V29` (29 файлов; последние —
  `V27__drop_regen_count.sql`, `V28__drop_view_only_review_state.sql`,
  `V29__session_history.sql`).
- `quiz-app` — 171 java-файл, 17040 строк main. Пакетная раскладка:
  `feature/` (interview, export, admin, auth) с вертикальными срезами
  controller/dto/mapper/service/usecase; `service/` (admin, cache, diagram, event,
  imports, strategy); `api/` (controller, dto, mapper, security, validation,
  constants); `common/` (exception, constants, util, web); `config/`
  (app, runtime, web); `infrastructure/` (bootstrap, health, render, search).
- Контроллеров ровно 5: `feature/interview/controller/InterviewMvcController.java`,
  `feature/interview/controller/InterviewApiController.java`,
  `feature/auth/controller/LoginController.java`,
  `feature/export/controller/ExportController.java`,
  `config/web/CssBundleController.java`. Полная карта маршрутов уже снята
  фронт-разведкой (`.sdlc/CS-20260827-001/report.md` §2) — здесь она НЕ
  дублируется, здесь разбирается серверная сторона: сигнатуры, DTO, делегирование.
- `*Service.java` — 49 файлов в `quiz-app`; ядро разложено по подпакетам
  `feature/interview/service/{core,command,flow,facade,page,progress,review,
  report,insight,topic,copy}`.
- Ошибки: `common/exception/GlobalExceptionHandler.java` (17.4K — единая точка),
  `common/exception/ErrorViewAttributes.java`,
  `common/exception/RateLimitExceededException.java`,
  `feature/export/exception/{ExportSerializationException,
  UnsupportedExportFormatException}.java`.
- Безопасность: `config/web/SecurityConfig.java`,
  `config/web/MultiUserSecurityConfig.java`, `config/web/OwnerContextFilter.java`,
  `config/web/OAuth2LoginUserServiceConfig.java`,
  `config/web/OAuthSecurityStartupGuard.java`,
  `config/web/SecurityModeStartupGuard.java`, плюс `api/security/` (6 классов:
  `CurrentUserAccessor`, `OrgIdpGroupAuthoritiesMapper`, `RequestRateLimiter`,
  `SensitiveEndpointAccessService`, `SensitiveEndpointAccessDeniedHandler`,
  `SensitiveEndpointAuthenticationEntryPoint`). Режим `app.security.mode`:
  `local` | `multi`.
- Кэш: `service/cache/OptionCache.java` + `CaffeineOptionCache.java`,
  `config/runtime/CacheConfig.java`, размеры из `config/app/AppProperties.java`
  (`app.cache.option-max-size` 500, `ttl-hours` 1). Spring-аннотации
  `@Cacheable`/`@EnableCaching` в main-коде отсутствуют — кэш ручной.
- Транзакции: `@Transactional` встречается в 16 main-файлах — 5 из них
  репозитории `quiz-persistence` (`QuestionRepository`, `ReviewStateRepository`,
  `AnswerOptionRepository`, `HintRepository`, `UserTopicStatsRepository`),
  11 — сервисы `quiz-app` (`InterviewService`, `SubmitAnswerCommand`,
  `ReviewService`, `AttemptRecorder`, `ExperienceService`, `DailyStreakService`,
  `BookmarkService`, `FavoriteService`, `BookmarkApiService`,
  `QuestionIssueService`, `AdminMaintenanceService`). Границу транзакции держат
  оба слоя одновременно — это факт для §13, не оценка.
- Внешние интеграции: HTTP-клиентов нет — `grep` по main-java на
  `org.springframework.ai|WebClient|RestTemplate|RestClient|reactor.core` даёт
  один хит, и тот в комментарии `config/package-info.java`. При этом
  `modules/quiz-app/build.gradle.kts` объявляет `spring-boot-starter-webflux` и
  `spring-ai-openai` (+ BOM). Единственные «внешние» источники данных —
  файловые: `cheatsheets/interview/**/*.md` (`service/imports/`) и
  `resources/seed/mcq/**/*.json` (`McqJsonLoader`), плюс Postgres.
- Тесты: `quiz-app` — 194 java-файла в `src/test`, 189 из них `*Test.java`;
  76 лежат в `ui/` (это фронт-контракты, уже учтённые предшественником) →
  бэкенд-тестов порядка 113. `quiz-persistence` — 20 тест-файлов на
  Testcontainers (`AbstractPostgresRepositoryTest`), `quiz-domain` — 21.
  Гейты покрытия — в `modules/quiz-app/build.gradle.kts`: BUNDLE LINE ≥ 0.85 /
  BRANCH ≥ 0.70, PACKAGE LINE ≥ 0.55 (замер 2026-08-04: 0.915 / 0.764).
  Архитектурные правила — `architecture/LayeredArchitectureTest`.

**Ограничения.** Дельта коммита допустима только внутри
`.sdlc/CS-20260828-001/` (прецедент парковки по `assert-main-tree-touched`).
Любой запуск gradle/graphify пишет в дерево — запрещён. Порт 8080 занят живым
bootRun пользователя. Репозиторий параллельно правят другие агенты — отчёт
обязан быть привязан к SHA.

## Решение

Пишем `.sdlc/CS-20260828-001/report.md` — **инвентарную карту бэкенда по 14
рубрикам §5 BACKEND**, снятую статическим чтением репозитория на
зафиксированном SHA. Метод и правила — те же, что у ратифицированного
фронт-отчёта (единый формат серии Phase 0):

1. **Факт + якорь.** Каждое утверждение — с `path:line` либо командой подсчёта.
2. **Инвентарь, а не критика.** Никаких «надо переделать»; допустима только
   нейтральная пометка «источник правды / дубль / объявлено, но не используется»
   со ссылкой, доказывающей её.
3. **Числа проверяемы.** Счётчик приводится вместе с командой из Приложения A.
4. **Привязка ко времени.** Шапка: SHA HEAD, дата, ветка, метод.
5. **Без дублирования предшественника.** Карта URL уже есть в
   `.sdlc/CS-20260827-001/report.md` §2; здесь по каждому маршруту — только
   серверная сторона (аргументы, DTO, кому делегирует, транзакционность).

Структура `report.md` (жёсткая, порядок сохранять):

```
# Phase 0 — Backend Reconnaissance (CS-20260828-001)
Шапка: SHA HEAD, дата, ветка, метод (статическое чтение; приложение не поднималось)
## 0. Сводка одним экраном     ← таблица: рубрика → 1 строка факта → главный якорь
## 1. Модули и границы         ← 3 модуля, LOC/файлы, направление зависимостей,
                                  правила ArchUnit как исполняемая граница
## 2. Controllers              ← 5 контроллеров: класс, число хендлеров, вход
                                  (DTO/@ModelAttribute/@PathVariable), выход
                                  (view/DTO), кому делегирует; ссылка на §2 фронт-отчёта
## 3. Services                 ← 49 сервисов, сгруппированные по подпакетам;
                                  для core/command/flow/facade — назначение и вызывающий
## 4. Repositories             ← 16 репозиториев: таблица(ы), тип доступа (JdbcTemplate/
                                  NamedParameterJdbcTemplate), интерфейс vs реализация
## 5. DTO и мапперы            ← карта пакетов dto/ и mapper/ (request/response/view),
                                  чем DTO отличается от доменного типа
## 6. Domain model             ← ~49 типов quiz-domain по кластерам (вопрос/сессия/
                                  повторение/прогресс), инварианты, domain/exception
## 7. Validation               ← jakarta-аннотации в DTO, кастомный @ValidIssueCategory,
                                  доменные проверки в конструкторах/фабриках, где @Valid
## 8. Error handling           ← GlobalExceptionHandler: перечень @ExceptionHandler,
                                  маппинг исключение → статус → view/JSON; ErrorViewAttributes
## 9. Authentication           ← app.security.mode local|multi, SecurityConfig vs
                                  MultiUserSecurityConfig, OAuth2-ветка, startup-guards,
                                  LoginController/LoginPageService
## 10. Authorization           ← owner_key-скоупинг (OwnerContextFilter, CurrentUserAccessor),
                                  admin-token, sensitive endpoints, RequestRateLimiter
## 11. Persistence             ← схема БД: таблицы из миграций → владеющий репозиторий,
                                  ключи/уникальные индексы, FTS (PostgresFullTextSearchRepository)
## 12. Migrations              ← 29 файлов V1..V29: номер, что делает, есть ли drop/backfill;
                                  где живут, кто применяет, поведение в тестах
## 13. Transactions            ← 16 мест @Transactional (репозитории vs сервисы),
                                  readOnly/propagation, что вне транзакции
## 14. Caching                 ← OptionCache/CaffeineOptionCache, CacheConfig, ключи,
                                  инвалидация, конфиг-параметры; отсутствие @Cacheable
## 15. External integrations   ← фактическое отсутствие HTTP-интеграций; файловые источники
                                  (cheatsheets md, seed/mcq json); объявленные-без-потребителя
                                  зависимости (spring-ai-openai, webflux) с якорем в build.gradle.kts
## 16. Конфигурация бэкенда    ← app.* из AppProperties + application.yml: свойство,
                                  дефолт, кто читает (класс:line)
## 17. Тесты бэкенда           ← 113 бэкенд-тестов quiz-app + 20 persistence + 21 domain,
                                  Testcontainers, ArchUnit, jacoco-гейты; чего НЕ покрыто
## 18. Белые пятна разведки    ← что статикой не видно (рантайм-поведение), с гипотезой проверки
## Приложение A. Команды воспроизведения ← все счётчики одной копипастой
```

Объём 350–700 строк, русский язык, таблицы и короткие абзацы.

Отвергнутые альтернативы:
- Поднимать приложение на 8091 и снимать реальные планы запросов/эндпоинты —
  Phase 0 инвентаризует исходники; рантайм — предмет §18 и следующей задачи.
- Строить graphify-граф — пишет `graphify-out/` в дерево, ломает дельту.
- Дублировать таблицу URL из фронт-отчёта — 100+ строк дубля без нового знания.
- Класть отчёт в `docs/` — вне разрешённой дельты и вне DoD-гейта.
- Считать «11 рубрик» из заголовка задачи — потеря трёх рубрик миссии.

## Acceptance Criteria

Каждый пункт — с проверкой, выполняемой из корня worktree.

1. `.sdlc/CS-20260828-001/report.md` существует и непуст.
   Проверка: `test -s .sdlc/CS-20260828-001/report.md` (DoD-гейт).
2. Присутствуют все секции `## 0`…`## 18` и `## Приложение A`, ровно в этом
   порядке. Проверка: `grep -n '^## ' .sdlc/CS-20260828-001/report.md` — 20
   заголовков, номера строго возрастают от 0 до 18.
3. Покрыты все 14 рубрик §5 BACKEND (controllers, services, repositories, DTO,
   domain model, validation, error handling, authentication, authorization,
   persistence, migrations, caching, transactions, external integrations) —
   каждая ровно в своей секции §2–§15 (соответствие имени секции рубрике).
4. Шапка содержит SHA HEAD (`ea458932` или актуальнее), дату и ветку.
   Проверка: `head -12 report.md` содержит 40-символьный или укороченный SHA,
   совпадающий с `git rev-parse HEAD` на момент коммита.
5. Каждая секция §1–§17 содержит минимум один якорь вида `path:line` или
   команду подсчёта. Проверка: разбить файл по `## ` и убедиться, что в каждом
   блоке §1–§17 есть совпадение с `[A-Za-z0-9_./-]\+\.\(java\|sql\|kts\|yml\)\(:[0-9]\+\)\?`.
6. Полные списки, обязательные к перечислению (неполнота = провал приёмки):
   - §2 — все 5 классов-контроллеров;
   - §4 — все 16 `*Repository*` из `quiz-persistence`;
   - §12 — все 29 файлов `db/migration/V*.sql`;
   - §13 — все 16 main-файлов с `@Transactional`.
   Проверка: `grep -c` по каждому списку в отчёте против команд Приложения A
   (`find ... | wc -l`), числа совпадают.
7. Числа в отчёте подтверждены: для каждого счётчика (файлы модулей, LOC,
   сервисы, тесты, миграции) в Приложении A есть команда, и её вывод на HEAD
   равен числу в тексте. Расхождение хотя бы в одном — провал.
8. Все пути, упомянутые в отчёте, существуют на HEAD. Проверка (команда
   включается в Приложение A): извлечь токены `modules/…`, `cheatsheets/…`,
   `e2e/…`, `docs/…`, `.sdlc/…`, прогнать `test -e` — ноль ненайденных.
9. §15 явно фиксирует расхождение «объявлено в build.gradle.kts vs
   используется в main-коде» для `spring-ai-openai` и `webflux`, с якорем на
   строки `modules/quiz-app/build.gradle.kts` и командой-доказательством
   отсутствия потребителей.
10. §18 перечисляет не менее трёх конкретных белых пятен, у каждого — способ
    закрытия (какое наблюдение/замер).
11. Отчёт на русском; объём 350–700 строк. Проверка:
    `wc -l .sdlc/CS-20260828-001/report.md`.
12. Дельта задачи ограничена `.sdlc/CS-20260828-001/**`. Проверка:
    `git status --porcelain -uall` не показывает путей вне этого каталога.

## Тест-контракт

не применимо (артефакт-документ)

## Риски

- **Дрейф репо.** Параллельные агенты коммитят; отчёт без SHA станет ложью.
  Митигация — SHA в шапке, все числа снимаются одним проходом.
- **Дубль фронт-отчёта.** Соблазн переписать таблицу маршрутов. Митигация —
  правило 5 и явная ссылка вместо копии.
- **Сползание в аудит.** «Транзакция в двух слоях — плохо» — оценка, не факт.
  Митигация — правило 2; спорные места формулировать как наблюдение с якорем.
- **Неполные списки.** 29 миграций и 16 репозиториев легко «сократить для
  краткости» — это ломает AC 6. Митигация — генерировать списки командой,
  а не по памяти.
- **Объём.** 14 рубрик × детализация дают 1500 строк. Митигация — потолок
  700 строк и §0 «сводка одним экраном».
- **Выход дельты за `.sdlc/`.** Запуск gradle/graphify/playwright создаёт файлы
  и паркует задачу. Митигация — метод строго статический.

## Follow-ups

- Провести Phase 0 разведку инфраструктуры и тестирования (§5 INFRASTRUCTURE + TESTING: сборка, деплой, окружение, конфигурация, логирование, мониторинг; unit/integration/E2E/visual/a11y/API-покрытие) — отчёт `.sdlc/<ID>/report.md`, DoD custom-гейт.
- Провести Phase 0 разведку документации (§5 DOCUMENTATION: README, архитектура, продуктовая и доменная документация, API-документация) и зафиксировать расхождения документов с кодом на HEAD — отчёт `.sdlc/<ID>/report.md`.
- Закрыть «белые пятна» §18 бэкенд-разведки замером на живом приложении (bootRun на 8091, Testcontainers-профиль): фактические SQL-запросы горячих путей, поведение кэша опций, границы транзакций — отчёт `.sdlc/<ID>/report.md`.
