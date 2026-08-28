# CS-20260828-004: Phase 0 разведка инфраструктуры и тестирования

## Контекст

Задача-артефакт: единственный результат — `.sdlc/CS-20260828-004/report.md`,
DoD-гейт `test -s .sdlc/CS-20260828-004/report.md`. Код не меняем.

Источник требования — миссия `cheat-sheet-product-rebuild.md`, §5 PHASE 0 —
REPOSITORY RECONNAISSANCE, строки 151–154:
- `INFRASTRUCTURE: build; deployment; environment; configuration; logging;
  monitoring.` — 6 рубрик;
- `TESTING: unit; integration; E2E; visual; accessibility; API.` — 6 рубрик.

Итого 12 рубрик. Заголовок задачи обрезан на середине, но перечень в
`origin_note` совпадает с миссией дословно — скоуп однозначен.

Серия Phase 0: `.sdlc/CS-20260827-001/report.md` (фронтенд, 661 стр.) и
`.sdlc/CS-20260828-001/report.md` (бэкенд, 700 стр.) уже ратифицированы ревью.
Их формат воспроизводится: факт + якорь `path:line`, инвентарь вместо критики,
каждое число с командой пересчёта, привязка к SHA.

**Что измерено по репо на HEAD `af764992` (2026-08-28):**

INFRASTRUCTURE.
- **Build.** Gradle wrapper 8.9 (`gradle/wrapper/gradle-wrapper.properties:3`),
  Java 17 через `subprojects{}` корневого `build.gradle.kts:20-26`, version
  catalog `gradle/libs.versions.toml` (59 строк; spring-boot 3.3.2, spring-ai
  1.0.0, archunit 1.3.0, jacoco 0.8.12). `gradle.properties` отсутствует.
  Три модуля (`settings.gradle.kts:12`): `quiz-domain`/`quiz-persistence` —
  `java-library` без Spring Boot-плагина, `quiz-app` — boot + jacoco
  (`modules/quiz-app/build.gradle.kts:1-6`). Корень регистрирует
  прокси-задачи `compileJava/bootRun/bootJar/javadoc` (`build.gradle.kts:31-46`).
  `processResources` подставляет `@projectVersion@` в `application.yml`
  (`modules/quiz-app/build.gradle.kts:163-169`), `springBoot.buildInfo`
  генерирует `META-INF/build-info.properties` для `/actuator/info` (там же
  :85-93). Jacoco-гейты только в `quiz-app`: BUNDLE LINE ≥ 0.85 / BRANCH ≥ 0.70,
  PACKAGE LINE ≥ 0.55 для `api.*|service.*|feature.*` минус `*.dto*`
  (:118-155); `tasks.check` зависит от verification (:159-161).
- **Deployment.** `Dockerfile` (68 строк): двухстадийная сборка
  `gradle:8.9-jdk17` → `eclipse-temurin:17-jre`, кэш-слой зависимостей (:10-15),
  UID/GID 10001 (:46), OCI-labels из ARG `VERSION/BUILD_DATE/VCS_REF` (:31-38),
  `JAVA_TOOL_OPTIONS` с `MaxRAMPercentage=75` (:64), `HEALTHCHECK` на
  `/actuator/health/readiness` (:66-67), `SPRING_PROFILES_ACTIVE=prod` (:56).
  `docker-compose.yml` (111 строк): `postgres:16-alpine` + `interview-prep`,
  `env_file: .env required:false` (:64-66), `depends_on: service_healthy`
  (:85-87), `tmpfs /tmp` (:49-50), `no-new-privileges` (:44-45), json-file
  ротация логов 10m×5 (:100-104), bind-mount `./cheatsheets` (:106-107).
  CI: 3 workflow — `ci.yml` (110 стр., job `build-test` + `docker-build`
  **без push**, :100), `visual-goldens.yml` (116 стр., только
  `workflow_dispatch`), `cheatsheet-lint.yml` (35 стр.). Registry-push,
  release-workflow, dependabot, k8s-манифесты в репо отсутствуют
  (`find .github -type f` → ровно 3 файла).
- **Environment.** Профили: default, `prod` (56 стр.), `qa` (19 стр.), `test`
  (14 стр., только в `src/test/resources`). В yml-файлах 31 уникальный
  `${ENV_VAR}`; `.env.example` документирует 13 → расхождение 18 переменных.
- **Configuration.** `application.yml` — 236 строк: `server` (graceful
  shutdown, cookie-only session, forward-headers, gzip), `spring` (datasource
  + Hikari `reWriteBatchedInserts`, flyway, thymeleaf, static cache), `app.*`
  (~25 свойств), `management` (см. Monitoring). `application-qa.yml:9-11`
  задаёт `app.preload.startupPreload/fullWarmup`, для которых в main-коде нет
  биндинга (`grep -rn "startupPreload\|fullWarmup" modules/quiz-app/src/main/java`
  → ноль) — мёртвая настройка.
- **Logging.** Ни `logback-spring.xml`, ни `log4j2.xml` в репо нет (`find` даёт
  только шпаргалки `cheatsheets/monitoring/logging/*.md`) — конфигурация
  дефолтная Spring Boot, и в yml-файлах нет ни одного ключа `logging.*`.
  В main-коде 37 файлов с логгером: 32 через `@Slf4j`, 4 через
  `LoggerFactory.getLogger`. Вызовы: `warn` 39, `info` 16, `debug` 10,
  `error` 4, `trace` 0. Инфраструктурные сигналы — Hikari
  `leak-detection-threshold` 30s в dev и 0 в prod (`application-prod.yml:11`).
- **Monitoring.** Actuator exposure `health,info,metrics,prometheus`
  (`application.yml:192`), probes liveness/readiness с явными группами
  (:218-222), health-кэш 5s (:205-206), `show-details: never`.
  `micrometer-registry-prometheus` — `runtimeOnly`
  (`modules/quiz-app/build.gradle.kts:20`). Свои метрики — 9 имён в 3 классах:
  `attempt.submit.duration|success|duplicate`,
  `attempt.idempotency.claim.conflict`, `attempt.revision.mismatch`,
  `attempt.journal.write.failure`, `mcq.seed.options.inserted|questions.skipped|
  topic.requests`. Один `HealthIndicator` — `SeedCoverageHealthIndicator`.
  Общие теги `application=interview-prep`, `env=${APP_ENV:dev}` (:233-236).
  Комментарий `build.gradle.kts:19` обещает «Grafana dashboard in release
  package» — дашборда в репо нет (найденные `*grafana*.json` — MCQ-сидеры и
  их копии в `docs/mcq-quality/`).

TESTING.
- Всего 236 тест-файлов и 1300 `@Test` в трёх модулях: `quiz-domain` 21/94,
  `quiz-persistence` 20/120, `quiz-app` 195/1086 (из них `ui/` — 77 файлов и
  421 `@Test`).
- **Unit.** Ядро — `@ExtendWith(MockitoExtension` в 43 файлах `quiz-app`,
  чистый JUnit+AssertJ в `quiz-domain` (build-файл модуля не тянет Spring).
  `@ParameterizedTest` в 14 файлах, `@Nested` — в 1.
- **Integration.** `@SpringBootTest` в 21 файле `quiz-app`;
  `quiz-persistence` — 20 файлов на `AbstractPostgresRepositoryTest`
  (Testcontainers PG 16-alpine). Профиль `test` форсится системным свойством
  для всех тестов (`modules/quiz-app/build.gradle.kts:76-81`), URL
  `jdbc:tc:postgresql:16-alpine:///interview`. `@WebMvcTest`/`@DataJdbcTest`/
  `@JdbcTest` не используются вовсе (0 файлов) — срез всегда полный контекст.
- **API.** `MockMvc` в 14 файлах, `jsonPath` — в 6; `@AutoConfigureMockMvc` в 12.
  OpenAPI-описание — `config/web/OpenApiConfig.java`, springdoc отключён в prod
  (`application-prod.yml:42-46`). Контрактных тестов на сам OpenAPI-документ нет.
- **E2E.** `e2e/` — Playwright 1.52, 6 спеков, 599 строк: `keyboard-quiz` (231),
  `visual-matrix` (206), `reflow` (50), `console-clean` (38), `chart-lazy` (37),
  `font-network` (37). Конфиг: 2 проекта (chromium + webkit), `workers: 1`,
  `fullyParallel: false`, baseURL `http://localhost:8080`, опциональный
  `webServer` по `E2E_START_SERVER=1` (`e2e/playwright.config.js:39-47`).
  В `ci.yml` E2E не запускается ни разу — единственный workflow с браузером
  ручной (`visual-goldens.yml:8`).
- **Visual.** Матрица theme × pixel × motion: 11 сценариев + отдельные
  вьюпорты; голдены — 14 PNG на движок (`e2e/visual-baselines/{chromium,webkit}`),
  порог `maxDiffPixelRatio: 0.03`. Пин детерминизма — `questionId=2060`
  (`e2e/visual-matrix.spec.js`), маски `DYNAMIC_CONTENT` и скрытие
  `VOLATILE_PRESENCE`. Java-двойник — `ui/VisualBaselineContractTest`.
- **Accessibility.** Ни axe-core, ни pa11y, ни lighthouse-a11y в репо нет
  (`grep -rl "axe\|pa11y" e2e/ scripts/` → 0). A11y покрыта косвенно:
  разметочные контракты в `ui/` (`aria-` в 17 файлах, `role=` в 7, `tabindex`
  в 7, `focus` в 41, `prefers-reduced-motion` в 2) плюс контраст-гейт
  `scripts/design-token-audit.py` в pre-commit.
- **Гейты вне тестов.** `.pre-commit-config.yaml` — 5 локальных хуков
  (cheatsheet-lint, verify-mcq-json, verify-md-no-mcq, css-leak-audit,
  design-token-audit). ArchUnit — `architecture/LayeredArchitectureTest`.

**Ограничения.** Дельта коммита допустима только внутри
`.sdlc/CS-20260828-004/`. Любой запуск gradle/playwright/graphify пишет в дерево
(build/, node_modules/, test-results/) — запрещён: метод строго статический.
Порт 8080 держит bootRun пользователя. Репозиторий параллельно правят другие
агенты — отчёт обязан быть привязан к SHA.

## Решение

Пишем `.sdlc/CS-20260828-004/report.md` — **инвентарную карту инфраструктуры и
тестирования по 12 рубрикам §5**, снятую статическим чтением репозитория на
зафиксированном SHA. Правила те же, что у двух ратифицированных предшественников:

1. **Факт + якорь.** Каждое утверждение — с `path:line` либо командой подсчёта.
2. **Инвентарь, а не критика.** Никаких «надо переделать»; допустима нейтральная
   пометка «объявлено, но не используется / документировано, но отсутствует /
   гейтом не покрыто» — только со ссылкой, доказывающей её.
3. **Числа проверяемы.** Счётчик приводится вместе с командой из Приложения A.
4. **Привязка ко времени.** Шапка: SHA HEAD, дата, ветка, метод.
5. **Без дублирования предшественников.** Карта URL — в §2 фронт-отчёта, разбор
   `app.*`-свойств по потребителям — в §16 бэкенд-отчёта; здесь конфигурация
   рассматривается как окружение (источник значения, профиль, env-override), а
   не как «кто читает».

Структура `report.md` (жёсткая, порядок сохранять):

```
# Phase 0 — Infrastructure & Testing Reconnaissance (CS-20260828-004)
Шапка: SHA HEAD, дата, ветка, метод (статическое чтение; ничего не запускалось)
## 0. Сводка одним экраном   ← таблица: 12 рубрик → 1 строка факта → главный якорь
## 1. Build                  ← wrapper, JDK, version catalog, 3 модуля и их плагины,
                                задачи (bootJar/buildInfo/processResources), jacoco-гейты
## 2. Deployment             ← Dockerfile по стадиям, docker-compose (2 сервиса),
                                3 workflow с триггерами и шагами, чего в CD нет
## 3. Environment            ← профили default/prod/qa/test: назначение, что меняют;
                                полная таблица 31 ${ENV} → дефолт → где читается;
                                расхождение с .env.example (13 задокументировано)
## 4. Configuration          ← application.yml по блокам server/spring/app/management,
                                overlay prod и qa, test-overlay; @projectVersion@;
                                мёртвые ключи (app.preload.*)
## 5. Logging                ← отсутствие logback/log4j конфигурации и logging.* в yml,
                                дефолт Spring Boot, 37 логгеров (32 @Slf4j / 4 factory),
                                распределение уровней, ротация в docker json-file,
                                Hikari leak-detection dev vs prod
## 6. Monitoring             ← actuator exposure и probes, группы health, кэш,
                                SeedCoverageHealthIndicator, все 9 своих метрик
                                (имя → класс:line → что считает), теги, prometheus registry
## 7. Карта тестов           ← 236 файлов / 1300 @Test по модулям и пакетам, соглашения
                                именования, форсированный профиль test, Testcontainers
## 8. Unit                   ← Mockito-слой quiz-app (43), чистый JUnit quiz-domain (21),
                                параметризация, что покрыто/не покрыто
## 9. Integration            ← 21 @SpringBootTest + 20 persistence-тестов на
                                AbstractPostgresRepositoryTest; отсутствие срезов
                                @WebMvcTest/@JdbcTest; фикстуры и изоляция (TRUNCATE)
## 10. API                   ← MockMvc/jsonPath-тесты, OpenApiConfig, springdoc в профилях,
                                отсутствие контракта на сам OpenAPI-документ
## 11. E2E                   ← 6 спеков (назначение каждого), playwright.config,
                                2 браузерных проекта, как запускается локально
                                (scripts/run-*.sh) и что E2E не в ci.yml
## 12. Visual                ← матрица 11 сценариев + вьюпорты, 14 голденов на движок,
                                порог диффа, техника детерминизма (пин/маски/скрытие),
                                workflow обновления голденов
## 13. Accessibility         ← отсутствие axe/pa11y/lighthouse; косвенное покрытие
                                контрактами ui/ (aria/role/tabindex/focus/motion),
                                контраст-гейт design-token-audit.py
## 14. Гейты качества вне тестов ← 5 pre-commit хуков, ArchUnit, python-валидаторы
                                scripts/*, что запускается в CI, а что только локально
## 15. Белые пятна разведки  ← что статикой не видно, с гипотезой проверки
## Приложение A. Команды воспроизведения ← все счётчики одной копипастой
```

Объём 350–700 строк, русский язык, таблицы и короткие абзацы.

Отвергнутые альтернативы:
- Запускать `./gradlew check` и приводить фактическое покрытие — пишет в дерево,
  ломает дельту; актуальные числа покрытия — предмет follow-up.
- Гонять Playwright и мерить визуальные диффы — то же плюс занятый порт.
- Свести INFRASTRUCTURE и TESTING в два больших раздела вместо 12 рубрик —
  теряется соответствие миссии и проверяемость приёмки.
- Дублировать таблицу `app.*` из §16 бэкенд-отчёта — дубль без нового знания.
- Класть отчёт в `docs/` — вне разрешённой дельты и вне DoD-гейта.

## Acceptance Criteria

Каждый пункт — с проверкой, выполняемой из корня worktree.

1. `.sdlc/CS-20260828-004/report.md` существует и непуст.
   Проверка: `test -s .sdlc/CS-20260828-004/report.md` (DoD-гейт).
2. Присутствуют секции `## 0`…`## 15` и `## Приложение A` ровно в этом порядке.
   Проверка: `grep -n '^## ' report.md` — 17 заголовков, номера строго
   возрастают от 0 до 15, последний — Приложение A.
3. Покрыты все 12 рубрик §5 (build, deployment, environment, configuration,
   logging, monitoring, unit, integration, E2E, visual, accessibility, API) —
   каждая ровно в своей секции §1–§6 и §8–§13.
   Проверка: имя секции совпадает с рубрикой, отсутствие любой = провал.
4. Шапка содержит SHA HEAD (`af764992` или актуальнее), дату и ветку.
   Проверка: `head -12 report.md`; SHA совпадает с `git rev-parse --short HEAD`
   на момент коммита.
5. Каждая секция §1–§14 содержит минимум один якорь вида `path:line` или
   команду подсчёта. Проверка: разбить по `## ` и убедиться в совпадении с
   `[A-Za-z0-9_./-]\+\.\(java\|kts\|yml\|yaml\|js\|sh\|py\|toml\|txt\)\(:[0-9]\+\)\?`
   в каждом блоке §1–§14.
6. Полные списки, обязательные к перечислению (неполнота = провал приёмки):
   - §2 — все 3 файла `.github/workflows/*.yml` и все их job'ы;
   - §3 — все 31 уникальная `${ENV_VAR}` из `application*.yml` (main + test);
   - §6 — все 9 имён метрик Micrometer с классом-владельцем;
   - §11 — все 6 файлов `e2e/*.spec.js`;
   - §14 — все 5 хуков `.pre-commit-config.yaml`.
   Проверка: `grep -c` по каждому списку в отчёте против команд Приложения A,
   числа совпадают.
7. Числа в отчёте подтверждены: для каждого счётчика (тест-файлы и `@Test` по
   модулям, LOC конфигов и workflow, число голденов, число логгеров и вызовов
   по уровням) в Приложении A есть команда, и её вывод на HEAD равен числу в
   тексте. Расхождение хотя бы в одном — провал.
8. Все пути, упомянутые в отчёте, существуют на HEAD. Проверка (включается в
   Приложение A): извлечь токены `modules/…`, `e2e/…`, `scripts/…`,
   `.github/…`, `docs/…`, `gradle/…`, `.sdlc/…`, прогнать `test -e` — ноль
   ненайденных.
9. Отчёт явно фиксирует, с якорем и командой-доказательством, четыре
   расхождения «объявлено vs фактически»:
   - §3 — 31 `${ENV}` в yml против 13 в `.env.example` (список недостающих);
   - §4 — `app.preload.startupPreload/fullWarmup` в `application-qa.yml:9-11`
     без биндинга в main-коде;
   - §6 — комментарий `modules/quiz-app/build.gradle.kts:19` про Grafana
     dashboard при отсутствии дашборда в репозитории;
   - §11/§14 — E2E-спеки не запускаются ни в одном автоматическом workflow
     (`visual-goldens.yml` — только `workflow_dispatch`).
10. §1 фиксирует, что jacoco-гейты действуют только для `quiz-app`
    (`quiz-domain`/`quiz-persistence` не подключают плагин), с якорями на все
    три `build.gradle.kts`.
11. §15 перечисляет не менее трёх конкретных белых пятен, у каждого — способ
    закрытия (какое наблюдение/замер и чем).
12. Отчёт на русском; объём 350–700 строк. Проверка:
    `wc -l .sdlc/CS-20260828-004/report.md`.
13. Дельта задачи ограничена `.sdlc/CS-20260828-004/**`. Проверка:
    `git status --porcelain -uall` не показывает путей вне этого каталога.

## Тест-контракт

не применимо (артефакт-документ)

## Риски

- **Дрейф репо.** Параллельные агенты коммитят; отчёт без SHA станет ложью.
  Митигация — SHA в шапке, все числа снимаются одним проходом.
- **Соблазн запустить сборку.** «Проверю реальное покрытие» → `build/` в дереве
  → парковка по `assert-main-tree-touched`. Митигация — метод строго
  статический, актуальные числа покрытия вынесены в follow-up.
- **Сползание в аудит.** «Нет a11y-тестов — это плохо» — оценка, не факт.
  Митигация — правило 2: фиксировать наличие/отсутствие с якорем, без вердикта.
- **Неполные списки.** 31 переменная окружения и 9 метрик легко «сократить для
  краткости» — ломает AC 6. Митигация — генерировать списки командой.
- **Пересечение с бэкенд-отчётом.** `app.*`-свойства уже разобраны в §16
  CS-20260828-001. Митигация — правило 5: здесь угол «окружение и профиль»,
  со ссылкой вместо копии.
- **Объём.** 12 рубрик × детализация дают 1500 строк. Митигация — потолок
  700 строк и §0 «сводка одним экраном».

## Follow-ups

- Провести Phase 0 разведку документации (§5 DOCUMENTATION: README, архитектура, продуктовая и доменная документация, API-документация) и зафиксировать расхождения документов с кодом на HEAD — отчёт `.sdlc/<ID>/report.md`, DoD custom-гейт.
- Снять фактические числа качества прогоном сборки (`./gradlew check`, jacoco XML): покрытие по модулям и пакетам, время прогона, список падающих/пропущенных тестов — отчёт `.sdlc/<ID>/report.md` с обновлением замера от 2026-08-04 в `modules/quiz-app/build.gradle.kts`.
- Закрыть расхождения конфигурации, найденные разведкой: дописать в `.env.example` недостающие переменные из `application*.yml`, удалить мёртвый блок `app.preload.*` из `application-qa.yml`, привести комментарий про Grafana dashboard в `modules/quiz-app/build.gradle.kts` в соответствие с содержимым репозитория.
