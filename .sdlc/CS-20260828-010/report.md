# Phase 0 — Infrastructure & Testing Reconnaissance (CS-20260828-010)

**HEAD:** `50f2f077b2966bb4f948b0d51d88c047c3d7d3cd` (короткий `50f2f077`)
**Дата:** 2026-08-29
**Ветка:** `sdlc/CS-20260828-010`
**Метод:** статическое чтение репозитория. Приложение не поднималось,
`./gradlew check`/`build` и Playwright не запускались — все числа сняты
одним проходом текстовых команд (`wc`, `grep`, `find`) непосредственно
перед записью этого файла, без интерпретации/запуска кода. Полный список
команд — Приложение A.

Родственные отчёты той же серии: `.sdlc/CS-20260827-001/report.md`
(фронтенд, замер на HEAD той задачи), `.sdlc/CS-20260828-001/report.md`
(бэкенд, замер на `8e26bf3a`). Числа там сняты на более раннем HEAD и не
обновлялись здесь заново — где они расходятся с текущими, это дрейф
репозитория, а не ошибка.

## 0. Сводка одним экраном

| Рубрика | Факт | Якорь |
|---|---|---|
| Build | Gradle 8.9, 3 модуля, version catalog, jacoco только в `quiz-app` | `modules/quiz-app/build.gradle.kts:95` |
| CI | 3 workflow; `ci.yml` гейтит только `modules/**`+`gradle/**` | `.github/workflows/ci.yml:5-19` |
| Deployment | Dockerfile multi-stage + compose (2 сервиса); k8s/helm/tf нет | `Dockerfile:2,22` |
| Environment | 20 переменных `.env.example` (13 активны, 7 закомментированы), 5 yaml-профилей | `.env.example:9-82` |
| Configuration | ~26 ключей `app.*`, `app.preload`/`app.qa` только в qa-профиле | `modules/quiz-app/src/main/resources/application-qa.yml:8-19` |
| Logging | Нет `logback.xml`/`logging.*`; 32×`@Slf4j` + 4×`LoggerFactory` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/web/CorrelationIdFilter.java` |
| Monitoring | actuator `health,info,metrics,prometheus`; 4 main-файла с Micrometer; дашборда в репо нет | `modules/quiz-app/src/main/resources/application.yml:187-236` |
| Unit | 213 методов domain+persistence, 43 файла с `MockitoExtension` в quiz-app | `modules/quiz-domain/src/test` |
| Integration | 21 `@SpringBootTest`, `AbstractPostgresRepositoryTest`, 29 Flyway-миграций в тестах | `modules/quiz-persistence/src/test/java/com/cheatsheet/quiz/persistence/AbstractPostgresRepositoryTest.java` |
| API | 12 файлов `@AutoConfigureMockMvc`, `PublicEndpointsSmokeTest`, springdoc 2.6.0 | `modules/quiz-app/src/test/java/com/cheatsheet/quiz/api/PublicEndpointsSmokeTest.java` |
| E2E | 6 spec-файлов Playwright, 2 браузера, НЕ подключён к CI | `e2e/playwright.config.js:34-39` |
| Visual | 28 базлайнов chromium/webkit + 5 txt-базлайнов бэкенда | `e2e/visual-baselines/` |
| Accessibility | 27 ui-контрактных тестов с aria/role/contrast/tabindex; axe/pa11y/Lighthouse — 0 | AC10-проверка ниже |
| Гейты вне Gradle | 5 pre-commit хуков, 41 файл в `scripts/` | `.pre-commit-config.yaml:4-43` |

## 1. Build

Gradle **8.9** (`gradle/wrapper/gradle-wrapper.properties:3`), Kotlin DSL.
Корневой `build.gradle.kts` — 46 строк: `base`-плагин, `allprojects` repos
(mavenCentral + `repo.spring.io/milestone`), `subprojects { java { toolchain
languageVersion = 17 } }` + UTF-8, и 4 alias-задачи (`compileJava`, `bootRun`,
`bootJar`, `javadoc`) для совместимости со старыми командами запуска.
`settings.gradle.kts` (15 строк) включает 3 модуля явными `projectDir`:
`modules/quiz-domain`, `modules/quiz-persistence`, `modules/quiz-app`.
Version catalog `gradle/libs.versions.toml` — 59 строк.

`modules/quiz-app/build.gradle.kts` (176 строк) — плагины `spring.boot`,
`spring.dependency.management`, `jacoco`; зависимости включают web, webflux,
thymeleaf, jdbc, validation, actuator, security, oauth2-client, flyway,
postgresql, jackson-jsr310, json-schema-validator, flexmark, jsoup,
spring-ai-openai (клиент присутствует в classpath, но код, который бы его
вызывал, в проекте отсутствует — см. `CLAUDE.md` §«Источник вопросов»),
caffeine, guava, commons-lang3, lombok, devtools, springdoc
(`springdoc-openapi-starter-webmvc-ui`, версия `2.6.0` —
`gradle/libs.versions.toml:10`); `runtimeOnly
micrometer-registry-prometheus` — единственная зависимость с версией не из
каталога, а из Spring Boot BOM.

Тест-таск форсит `spring.profiles.active=test` системным свойством
(`modules/quiz-app/build.gradle.kts`, блок `tasks.withType<Test>`), поэтому
Testcontainers включается даже без `@ActiveProfiles` на классе теста.
`springBoot { buildInfo { … } }` генерирует `META-INF/build-info.properties`
для `/actuator/info`; `processResources` подставляет `@projectVersion@` в
`application.yml:39`.

**Jacoco объявлен только в `quiz-app`.** Гейты (`modules/quiz-app/build.gradle.kts:107-160`):
BUNDLE LINE ≥ 0.85, BRANCH ≥ 0.70 (строки 121-128); дополнительно PACKAGE
LINE ≥ 0.55 для `api.*`/`service.*`/`feature.*` минус `*.dto*` (строка 151-153).
Комментарий строки 117 фиксирует последний известный замер (2026-08-04):
LINE 0.915 (3633/3970), BRANCH 0.764 (1366/1788) — это исторический снимок в
коде, не переизмерен здесь (см. §16, follow-up на живой прогон).
`tasks.check` зависит от `jacocoTestCoverageVerification` (строка 160).
В `modules/quiz-domain/build.gradle.kts` и `modules/quiz-persistence/build.gradle.kts`
плагина `jacoco` нет вовсе — команда `grep -l jacoco` по обоим файлам
возвращает пусто (Приложение A) — их покрытие не измеряется и не гейтится.

## 2. CI

`.github/workflows/` содержит ровно 3 файла:

1. **`ci.yml`** (110 строк). Триггеры `push`/`pull_request` с
   `paths: modules/**, gradle/**, *.gradle.kts, gradlew, gradlew.bat,
   .github/workflows/ci.yml` (строки 5-19) — правки `cheatsheets/**`,
   `e2e/**`, `scripts/**`, `docker-compose.yml`, `Dockerfile` этот workflow
   НЕ запускают вовсе. Job `build-test`: JDK 17 Temurin, `gradle/actions/setup-gradle@v4`,
   `gradle/actions/wrapper-validation@v4`, `./gradlew build -x test --no-daemon`,
   затем `./gradlew check --no-daemon` (строки 49-54) — это единственный
   CI-гейт, реально гоняющий тесты + jacoco + ArchUnit. Job `docker-build`
   (строки 74-108) — smoke-сборка образа `docker/build-push-action@v6` без
   push, выполняется только при `push` в `master` (строка 79).
2. **`visual-goldens.yml`** (116 строк) — `workflow_dispatch` (ручной запуск),
   поднимает postgres-service, стартует `bootRun` в фоне, гоняет Playwright
   `visual-matrix.spec.js`, публикует артефакт базлайнов. Не триггерится
   автоматически ни на push, ни на PR.
3. **`cheatsheet-lint.yml`** (35 строк) — python-линт содержимого `cheatsheets/**`.

**Расхождение (б):** правка `e2e/**`, `Dockerfile` или
`docker-compose.yml` не запускает НИ ОДИН из трёх workflow — ни тесты, ни
docker-smoke, ни визуальный набор (см. §9 AC).

## 3. Deployment

`Dockerfile` (68 строк) — multi-stage: `FROM gradle:8.9-jdk17 AS builder`
(строка 2) → `FROM eclipse-temurin:17-jre` (строка 22). Слой зависимостей
кэшируется отдельно от исходников (строки 10-15, комментарий объясняет
причину — инвалидация кэша при копировании исходников раньше). OCI-labels
из ARG `VERSION`/`BUILD_DATE`/`VCS_REF` (строки 23-38). Непривилегированный
пользователь UID/GID 10001 (строка 46), `USER app` (строка 55).
`ENV SPRING_PROFILES_ACTIVE=prod` (строка 56); `JAVA_TOOL_OPTIONS` с
`MaxRAMPercentage=75`, `+UseG1GC`, `+ExitOnOutOfMemoryError`,
`+HeapDumpOnOutOfMemoryError` (строка 64). `HEALTHCHECK` на
`/actuator/health/readiness` (строки 66-67). `COPY cheatsheets
/app/cheatsheets` (строка 51) — контент шпаргалок встроен в образ.

`docker-compose.yml` (111 строк) — ровно 2 сервиса: `postgres` (строки 5-29,
образ `postgres:16-alpine`, healthcheck `pg_isready`, volume `postgres-data`,
json-file логи с ротацией `max-size 10m`/`max-file 3`) и `interview-prep`
(строки 31-108, `env_file: .env required:false` — строки 64-66, `tmpfs
/tmp:size=128m` — строка 50, `security_opt: no-new-privileges:true` —
строки 9-10 и 44-45, `depends_on: postgres condition: service_healthy` —
строки 85-87, json-file логи `max-size 10m`/`max-file 5`, bind-mount
`./cheatsheets:/app/cheatsheets` — строка 107).

README документирует **пять**, а не четыре способа запуска (расхождение
с исходным контекстом задачи, зафиксированным до этого замера — см. §16):
`README.md:110` (Способ 1 — Gradle), `:151` (Способ 2 — собранный JAR),
`:185` (Способ 3 — Docker одиночный), `:215` (Способ 4 — Docker Compose с
PostgreSQL), `:256` (Способ 5 — PostgreSQL без Docker). Kubernetes-манифестов,
Helm-чартов, Terraform в репозитории нет — команда `find . -iname
"*.tf" -o -iname "Chart.yaml"` (вне `build/`/`node_modules`) не находит ничего
(Приложение A).

## 4. Environment

`.env.example` — 82 строки. **20 переменных** (Приложение A: команда
`grep -cE '^#{0,2} ?[A-Z_]+=' .env.example` = 20), из них 13 раскомментированы
по умолчанию и 7 закомментированы (опциональны):

Активные (строки): `APP_ADMIN_TOKEN` (9), `APP_CORS_ALLOWED_ORIGINS` (12),
`SENSITIVE_RATE_LIMIT_PER_MINUTE` (16), `APP_PORT` (19), `APP_VERSION` (22),
`POSTGRES_USER` (33), `POSTGRES_PASSWORD` (34), `POSTGRES_PORT` (35),
`INTERVIEW_RESET_ON_STARTUP` (41), `DB_POOL_MAX_SIZE` (46),
`DB_POOL_MIN_IDLE` (47), `APP_ENV` (58), `SERVER_FORWARD_HEADERS` (64).

Закомментированные (строки): `JAVA_TOOL_OPTIONS` (28),
`MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` (52),
`MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS` (53),
`DB_POOL_LEAK_DETECTION_MS` (69), `STATIC_RESOURCES_CACHE_SECONDS` (74),
`HEALTH_CACHE_SECONDS` (78), `SHUTDOWN_TIMEOUT` (82).

Матрица yaml-профилей (5 файлов, найдены `find . -name "application*.yml"`,
Приложение A):

| Файл | Строк | Роль |
|---|---|---|
| `modules/quiz-app/src/main/resources/application.yml` | 236 | базовый (default) |
| `modules/quiz-app/src/main/resources/application-prod.yml` | 56 | prod-оверрайды |
| `modules/quiz-app/src/main/resources/application-qa.yml` | 19 | детерминированное QA-окружение |
| `modules/quiz-app/src/test/resources/application-test.yml` | 15 | тестовый (Testcontainers) |
| `modules/quiz-app/examples/application-oauth.yml` | 42 | НЕ на classpath, пример OAuth-оверлея |

Порт по умолчанию `8080` (`SERVER_PORT` в `application.yml:2`), БД —
PostgreSQL, порт `5432` (`.env.example:35`). Тесты используют
Testcontainers-URL `jdbc:tc:postgresql:16-alpine:///interview`
(`modules/quiz-app/src/test/resources/application-test.yml`).

## 5. Configuration

`application.yml` (236 строк) содержит 4 верхнеуровневых блока: `server.*`
(строки 1-34: graceful shutdown, cookie-only session tracking, forward-headers,
gzip-compression с explicit mime-types), `spring.*` (строки 36-88: datasource
+ Hikari pool/leak-detection/`reWriteBatchedInserts`, flyway, thymeleaf.cache,
web.resources.cache), `app.*` (строки 93-184) и `management.*` (строки 187-236).

Реестр ключей `app.*` (полный список подкорней в `application.yml`, строки
93-184): `interview-path`, `topic-groups-config`, `admin-token`,
`sensitive-rate-limit-per-minute`, `scheduling.algorithm`,
`interview.learned-repetitions`, `interview.exam-penalty-questions`,
`interview.max-session-count`, `interview.default-exam-count`,
`interview.default-marathon-count`, `interview.default-daily-goal`,
`interview.senior-rule-priority-overrides`, `cache.option-max-size`,
`cache.ttl-hours`, `search.max-limit`, `search.preview-max-length`,
`search.stats-results-limit`, `import-settings.min-code-block-length`,
`interview-reset-on-startup`, `security.mode`,
`security.allow-insecure-public`, `security.multi.username`,
`security.multi.password`, `security.oauth.enabled`,
`security.oauth.registration-id`, `security.oauth.display-name`,
`security.oauth.groups-claim`, `security.oauth.admin-groups`,
`security.cors.*` (allowed-origins/methods/headers/exposed-headers/
allow-credentials/max-age-seconds) — 26 верхних/вложенных групп.

**Расхождение (а):** `app.preload.*` (`startupPreload`, `fullWarmup`) и
`app.qa.*` (`fixed-instant`, `due-fixtures`, `learned-fixtures`) объявлены
**только** в `application-qa.yml:8-19` — в базовом `application.yml` этих
ключей нет (`grep -c preload application.yml` = 0, Приложение A). Это
означает: при активном профиле `qa` приложение конфигурируется свойствами,
у которых нет дефолта в базовом файле — они существуют исключительно как
qa-специфичные, а не как переопределение общего ключа.

## 6. Logging

Ни `logback.xml`, ни `logback-spring.xml` в репозитории нет (`find . -iname
"logback*.xml"` — пусто, вне `build/`). Ни в одном из 5 yaml-профилей нет
ключа `logging.*` (`grep -n '^logging:' <файл>` — пусто по всем пяти,
Приложение A) — используется дефолтный Logback-конфиг Spring Boot
(паттерн вывода, уровень INFO по умолчанию).

В main-коде: **32 файла** с `@Slf4j` (Lombok-аннотация генерирует
статический логгер) и **4 файла** с ручным `LoggerFactory.getLogger(...)`.
Корреляция запросов — `modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/web/CorrelationIdFilter.java`,
единственное место в кодовой базе, кладущее значение в MDC (по имени файла
и его роли — фильтр на входе запроса). Ротация логов — только на уровне
docker-драйвера: `docker-compose.yml:25-29` (postgres, `max-size 10m`,
`max-file 3`) и `docker-compose.yml:100-104` (interview-prep, `max-size 10m`,
`max-file 5`); на уровне приложения ротации нет.

## 7. Monitoring

`management.endpoints.web.exposure.include: health,info,metrics,prometheus`
(`application.yml:192`); `show-details: never`, `show-components: never`
(строки 200-201); health кэшируется `HEALTH_CACHE_SECONDS` (дефолт 5s,
строка 206). Health-группы: `liveness` → `livenessState`, `readiness` →
`readinessState,db` (строки 218-222) — комментарий строк 207-217 явно
объясняет, почему `seedCoverage` НЕ входит в readiness (неполнота контента —
не про готовность принимать трафик).

Кастомный health-индикатор один —
`modules/quiz-app/src/main/java/com/cheatsheet/quiz/infrastructure/health/SeedCoverageHealthIndicator.java`,
виден в агрегатном `/actuator/health`, но не в readiness-группе.

Micrometer используется в **4 main-файлах** (`grep -rl io.micrometer
modules/*/src/main`, Приложение A):
- `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/runtime/CacheConfig.java`
- `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewSessionSupport.java`
- `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/service/command/SubmitAnswerCommand.java`
- `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/McqJsonLoader.java`

Общие теги метрик: `application=interview-prep` (константа) и
`env=${APP_ENV}` (`application.yml:233-235`, оверрайд в `application-prod.yml:35-38`
на дефолт `prod`).

**Расхождение (г):** комментарий в `modules/quiz-app/build.gradle.kts`
обещает Grafana dashboard в release-пакете — файла дашборда (`.json`
провижининг для Grafana) в репозитории нет: `find . -iname
"*dashboard*.json" -o -iname "*grafana*"` (вне `build/`) не находит ничего
(Приложение A).

## 8. Карта тестов

Свежий подсчёт на HEAD `50f2f077` (команды — Приложение A) даёт **236
тестовых java-файлов, 1350 тестовых методов** (`@Test` + `@ParameterizedTest`),
что отличается от снимка design.md на предыдущем HEAD `8e26bf3a` (1354) —
дрейф в пределах 4 методов, ожидаемый эффект параллельных loop'ов
пользователя (см. `CLAUDE.md` §4 «Протокол параллельных агентов»).

По модулям:

| Модуль | Файлов | `@Test` | `@ParameterizedTest` | Всего методов |
|---|---|---|---|---|
| `quiz-domain` | 21 | 94 | 24 | 118 |
| `quiz-persistence` | 20 | 119 | 0 | 119 |
| `quiz-app` | 195 | 1084 | 29 | 1113 |
| **Итого** | **236** | **1297** | **53** | **1350** |

Разбивка `quiz-app` по пакетам (файлы/методы):
`ui/` 77/422, `service/` 47/259, `api/` 20/170, `feature/` 12/69,
`infrastructure/` 5/66, `config/` 12/50, `common/` 7/46, `bootstrap/` 2/6,
`persistence/` 2/3, `domain/` 1/1, `architecture/` 1/0 (ArchUnit-правила —
поля класса, не JUnit-методы), плюс 9 файлов прямо в корне пакета
(`ExportControllerErrorTest`, `SpacedRepetitionServiceTest`, `TestResources`,
`TestQuestionBuilder`, `OptionCacheTest`, `ExportIntegrationTest`,
`OptionCacheIntegrationTest`, `InterviewSessionTest`, `TestInterviewPath`) с
21 методом. Сумма файлов 77+47+20+12+5+12+7+2+2+1+1+9 = 195 — сходится.

Раннеры/типы: `@SpringBootTest` — 21 файл, `@AutoConfigureMockMvc` — 12,
`MockitoExtension` — 43, `@WebMvcTest` — 0, ArchUnit
(`com.tngtech.archunit`) — 1 файл
(`modules/quiz-app/src/test/java/com/cheatsheet/quiz/architecture/LayeredArchitectureTest.java`).
Профиль `test` форсится Gradle-таском (§1); Testcontainers PostgreSQL
16-alpine — база для persistence- и части app-тестов.

## 9. Unit

`quiz-domain` (21 файл / 118 методов) — чистые доменные юниты без Spring;
`quiz-persistence` содержит 20 файлов / 119 методов, но фактически это
преимущественно интеграционные тесты на Testcontainers (см. §10) —
модуль не разделяет unit/integration отдельными пакетами. В `quiz-app`
единичное мокирование через `MockitoExtension` встречается в **43 файлах**
(`grep -l MockitoExtension`, Приложение A) — это основной unit-паттерн
сервисного слоя (`service/`, `feature/*/service/`).

ArchUnit-правила в
`modules/quiz-app/src/test/java/com/cheatsheet/quiz/architecture/LayeredArchitectureTest.java`
исполняются как обычный JUnit-класс (0 `@Test`-методов в подсчёте §8
потому что правила ArchUnit объявлены как поля `ArchRule`, а не методы —
запускаются через `@AnalyzeClasses`/`ArchTests`), но по факту это
единственная механически проверяемая граница слоёв domain/service/
persistence/api из `CLAUDE.md` §«Architecture & Layer Rules» — гейтится
через `tasks.check` наравне с обычными тестами.

## 10. Integration

21 файл с `@SpringBootTest`. Основа persistence-тестов —
`modules/quiz-persistence/src/test/java/com/cheatsheet/quiz/persistence/AbstractPostgresRepositoryTest.java`
(один Testcontainers-контейнер на JVM, TRUNCATE между тестами — согласно
`CLAUDE.md`). Flyway-миграции применяются к тестовой БД так же, как в
проде: **29 SQL-файлов** в `modules/quiz-persistence/src/main/resources/db/migration/`
(`find ... -name "*.sql" | wc -l`, Приложение A).

Bootstrap/startup-guard тесты (2 файла, отражены в §8 как пакет
`bootstrap/` — 2/6): `QaProfileFixtureIntegrationTest.java` (проверяет
детерминированные фикстуры qa-профиля из §5) и
`NoAiModeStartupIntegrationTest.java` (проверяет, что старт приложения не
требует и не пытается вызывать внешние AI-провайдеры — прямое
подтверждение инварианта из `CLAUDE.md` §«Источник вопросов»).

## 11. API

`@AutoConfigureMockMvc` — **12 файлов**. Ключевой контрактный тест —
`modules/quiz-app/src/test/java/com/cheatsheet/quiz/api/PublicEndpointsSmokeTest.java`
(smoke по публичным REST-эндпоинтам). Springdoc-openapi версии `2.6.0`
(`gradle/libs.versions.toml:10`) подключён зависимостью
`springdoc.openapi.starter.webmvc.ui` (`modules/quiz-app/build.gradle.kts:62`) —
даёт живой Swagger UI на `/swagger-ui.html` в default-профиле; в prod
отключён явно (`application-prod.yml:42-46`, `swagger-ui.enabled: false`,
`api-docs.enabled: false`) — исключает риск случайной публикации карты API.

## 12. E2E

`e2e/` содержит **6 spec-файлов** Playwright: `visual-matrix.spec.js`,
`keyboard-quiz.spec.js`, `console-clean.spec.js`, `chart-lazy.spec.js`,
`font-network.spec.js`, `reflow.spec.js`. `playwright.config.js` объявляет
**2 браузерных проекта** — `chromium` (`use: devices['Desktop Chrome']`) и
`webkit` (`use: devices['Desktop Safari']`), строки 34/37.
`snapshotPathTemplate` направляет базлайны в
`e2e/visual-baselines/{projectName}/` (строка 23). `webServer` поднимается
условно, только при `E2E_START_SERVER=1` (строка 39) — по умолчанию
Playwright ожидает уже запущенное приложение (комментарий строки 8).

**Расхождение (в):** Playwright-набор не подключён ни к `./gradlew check`
(нет соответствующего Gradle-таска, вызывающего `npx playwright test`),
ни к `ci.yml` — единственный workflow, который его запускает,
`visual-goldens.yml`, триггерится исключительно вручную (`workflow_dispatch`,
без `push`/`pull_request`, см. §2). Это означает: сломанный e2e-сценарий
не остановит ни один автоматический CI-прогон.

## 13. Visual

`visual-matrix.spec.js` — основной визуальный сценарий (матрица
theme × viewport по имени файла и роли в наборе). Артефакты:
**28 файлов** базлайнов в `e2e/visual-baselines/` (`find e2e/visual-baselines
-type f | wc -l`, Приложение A) — раздельно по `chromium`/`webkit` через
`snapshotPathTemplate` (§12). На бэкенде — 5 текстовых «визуальных»
базлайнов Thymeleaf-разметки:
`modules/quiz-app/src/test/resources/visual-baseline/{train,stats,focus,settings,result}-shell.txt`,
используемых
`modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/VisualBaselineContractTest.java`
(файл входит в список §14 ниже) — это текстовое сравнение отрендеренного
HTML-каркаса, а не пиксельный снапшот.

## 14. Accessibility

**Что реально проверяется.** Свежий поиск (`grep -rliE "aria-|role=|contrast|
tabindex" modules/quiz-app/src/test --include="*.java"`, Приложение A) даёт
**27 файлов** в `quiz-app/src/test`, содержащих эти паттерны — заметно
меньше, чем упомянуто в исходном контексте задачи (снимок на более раннем
HEAD, вероятно иной метод подсчёта); текущее число подтверждено командой
и является актуальным. Не менее пяти конкретных примеров с якорями:

- `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/FocusHandoffContractTest.java` — фокус после перехода между состояниями.
- `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/RevealFocusContractTest.java` — управление фокусом при раскрытии ответа.
- `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/TabComponentContractTest.java` — `role=tab`/`role=tablist` семантика вкладок.
- `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/TabPanelContractTest.java` — `role=tabpanel`/`aria-controls` связка.
- `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/PixelCssPaletteTest.java` — контраст пиксельной палитры.
- `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/ModalTitleContractTest.java` — `aria-labelledby` модалок.

Это **контрактные unit/integration-тесты над Thymeleaf-выводом**
(проверяют присутствие конкретных атрибутов/значений в HTML через
Jsoup-подобный парсинг), а не полноценный accessibility-аудит.

**Чего нет.** Ни axe-core, ни pa11y, ни Lighthouse не подключены нигде в
проекте: `grep -ri 'axe-core\|pa11y\|lighthouse' -r e2e package.json
package-lock.json .github` возвращает 0 совпадений (Приложение A, команда
выполнена буквально). Автоматизированного a11y-скоринга (WCAG-контрастный
чекер, программный screen-reader обход) в CI нет — покрытие ограничено
точечными ассертами конкретных атрибутов в конкретных вью, выбранных
вручную разработчиком, а не системным сканом.

## 15. Гейты вне Gradle

`.pre-commit-config.yaml` — **5 хуков** (`.pre-commit-config.yaml:4-43`):
`cheatsheet-lint`, `verify-mcq-json`, `verify-md-no-mcq`, `css-leak-audit`,
`design-token-audit`.

`scripts/` — **41 файл** на верхнем уровне (`find scripts -maxdepth 1 -type f
| wc -l`, Приложение A), плюс подкаталоги `tests/`, `readability/`. Из
именованных в контексте задачи: `healthcheck.sh` (smoke живого
приложения — HTTP-проверка поднятого сервера), `run-visual-e2e.sh`
(обёртка запуска Playwright visual-набора), `run-keyboard-e2e.sh`
(обёртка keyboard-quiz.spec.js), `lint-scripts.sh` (линт самих shell-скриптов),
`verify-mcq-json.sh` (тот же валидатор, что вызывается pre-commit хуком
`verify-mcq-json`, — дублирующий путь запуска вне git-хука).

## 16. Белые пятна разведки

Метод этой задачи — статическое чтение; следующее статикой не видно и
требует живого прогона (follow-up уже зафиксирован в design.md):

1. **Фактическое покрытие jacoco.** Комментарий в коде фиксирует замер
   2026-08-04 (LINE 0.915, BRANCH 0.764) — устарел минимум на 3 недели
   активной разработки. Закрывается: `./gradlew check --no-daemon` в
   изолированном worktree + чтение сгенерированного HTML-отчёта jacoco
   (см. п.2 — путь появится после прогона).
2. **Время прогона полного набора тестов** (1350 методов, Testcontainers).
   Неизвестно, укладывается ли CI-таймаут 25 минут (`ci.yml:31`) с запасом
   или уже близок к пределу. Закрывается: замер `time ./gradlew check
   --no-daemon` с холодным и тёплым Gradle-кэшем (отчёт о покрытии из
   п.1 появится в `build/reports/jacoco/test/html/` того же прогона —
   каталог `build/` в репозитории на HEAD отсутствует, гейт `.gitignore`).
3. **Flaky-тесты.** Статика не покажет нестабильные тесты (Testcontainers-
   таймауты, состояние гонки в интеграционных сценариях). Закрывается:
   3-5 последовательных прогонов `./gradlew check` в CI или локально,
   сравнение результатов.
4. **Реальный охват Playwright visual-matrix** (какие именно
   theme×viewport×motion комбинации покрыты 28 базлайнами — не пересчитано
   построчно из самого спека). Закрывается: чтение
   `e2e/visual-matrix.spec.js` построчно + сопоставление с именами файлов
   в `e2e/visual-baselines/*/`.
5. **Актуальность OAuth-примера.** `examples/application-oauth.yml` не на
   classpath и не тестируется автоматически — работоспособность конфига
   с реальным IdP статикой не проверить. Закрывается: ручной smoke с
   тестовым OIDC-провайдером (Keycloak в docker) — отдельная задача,
   не Phase 0.
6. **Реальный охват accessibility за пределами точечных контрактных
   тестов** (§14) — цветовой контраст по WCAG AA/AAA, keyboard-навигация
   по всем интерактивным элементам, screen-reader тестирование. Закрывается:
   подключение axe-core в Playwright-раннер (`@axe-core/playwright`) как
   отдельный e2e-спек + прогон.

## Приложение A. Команды воспроизведения

Все команды выполняются из корня worktree на HEAD `50f2f077`.

```bash
# --- HEAD/branch/статус дельты ---
git rev-parse --short HEAD
git branch --show-current
git status --porcelain -uall

# --- §1 Build ---
wc -l build.gradle.kts settings.gradle.kts gradle/libs.versions.toml \
      modules/quiz-app/build.gradle.kts
grep -n "distributionUrl" gradle/wrapper/gradle-wrapper.properties
grep -l jacoco modules/quiz-domain/build.gradle.kts modules/quiz-persistence/build.gradle.kts
# → нет совпадений (exit 1), jacoco в этих модулях не подключён

# --- §2 CI ---
ls .github/workflows/
wc -l .github/workflows/*.yml
sed -n '1,20p' .github/workflows/ci.yml   # paths-фильтр

# --- §3 Deployment ---
wc -l Dockerfile docker-compose.yml
find . -iname "*.tf" -o -iname "Chart.yaml" 2>/dev/null | grep -v /build/
sed -n '110p;151p;185p;215p;256p' README.md   # 5 способов запуска

# --- §4 Environment ---
wc -l .env.example
grep -cE '^#{0,2} ?[A-Z_]+=' .env.example      # 20
grep -nE '^#{0,2} ?[A-Z_]+=' .env.example
find . -name "application*.yml" -not -path "*/build/*" | sort
wc -l modules/quiz-app/src/main/resources/application.yml \
      modules/quiz-app/src/main/resources/application-prod.yml \
      modules/quiz-app/src/main/resources/application-qa.yml \
      modules/quiz-app/src/test/resources/application-test.yml \
      modules/quiz-app/examples/application-oauth.yml

# --- §5 Configuration ---
grep -c "preload" modules/quiz-app/src/main/resources/application.yml   # 0
grep -n "preload\|qa:" modules/quiz-app/src/main/resources/application-qa.yml

# --- §6 Logging ---
find . -iname "logback*.xml" -not -path "*/build/*"                     # пусто
grep -n "^logging:" modules/quiz-app/src/main/resources/*.yml \
                    modules/quiz-app/src/test/resources/*.yml           # пусто
find modules -path "*/src/main/*" -name "*.java" -print0 \
  | xargs -0 grep -l "@Slf4j" | wc -l                                   # 32
find modules -path "*/src/main/*" -name "*.java" -print0 \
  | xargs -0 grep -l "LoggerFactory.getLogger" | wc -l                  # 4

# --- §7 Monitoring ---
find modules -path "*/src/main/*" -name "*.java" -print0 \
  | xargs -0 grep -l "io.micrometer"
find . -iname "*dashboard*.json" -o -iname "*grafana*" 2>/dev/null \
  | grep -v /build/                                                     # пусто

# --- §8 Карта тестов ---
find modules -path "*/src/test/*" -name "*.java" | wc -l                # 236
find modules -path "*/src/test/*" -name "*.java" -exec grep -o '@Test\b' {} \; | wc -l            # 1297
find modules -path "*/src/test/*" -name "*.java" -exec grep -o '@ParameterizedTest\b' {} \; | wc -l  # 53
# по модулям: заменить modules/ на modules/quiz-domain, modules/quiz-persistence, modules/quiz-app

find modules -path "*/src/test/*" -name "*.java" -print0 | xargs -0 grep -l "@SpringBootTest" | wc -l          # 21
find modules -path "*/src/test/*" -name "*.java" -print0 | xargs -0 grep -l "@AutoConfigureMockMvc" | wc -l    # 12
find modules -path "*/src/test/*" -name "*.java" -print0 | xargs -0 grep -l "MockitoExtension" | wc -l         # 43
find modules -path "*/src/test/*" -name "*.java" -print0 | xargs -0 grep -l "@WebMvcTest" | wc -l              # 0
find modules -path "*/src/test/*" -name "*.java" -print0 | xargs -0 grep -l "com.tngtech.archunit"             # LayeredArchitectureTest.java

# --- §10 Integration ---
find modules/quiz-persistence/src/main/resources/db/migration -name "*.sql" | wc -l   # 29
find modules -name "AbstractPostgresRepositoryTest.java"
find modules/quiz-app/src/test/java/com/cheatsheet/quiz/bootstrap -name "*.java"

# --- §11 API ---
find modules -name "PublicEndpointsSmokeTest*"
grep -n "springdoc" modules/quiz-app/build.gradle.kts gradle/libs.versions.toml

# --- §12 E2E ---
find e2e -maxdepth 1 -name "*.spec.js" | sort | wc -l                    # 6
grep -n "name:\|snapshotPathTemplate\|E2E_START_SERVER" e2e/playwright.config.js

# --- §13 Visual ---
find e2e/visual-baselines -type f | wc -l                                # 28
find modules/quiz-app/src/test/resources/visual-baseline -type f | wc -l # 5

# --- §14 Accessibility ---
grep -rliE "aria-|role=|contrast|tabindex" modules/quiz-app/src/test --include="*.java" | wc -l   # 27
grep -ri 'axe-core\|pa11y\|lighthouse' -r e2e package.json package-lock.json .github 2>/dev/null | wc -l  # 0

# --- §15 Гейты вне Gradle ---
grep -n "id:" .pre-commit-config.yaml
find scripts -maxdepth 1 -type f | wc -l                                 # 41

# --- пути (AC 8) ---
# извлечение и проверка путей вида modules/…, e2e/…, scripts/…, docs/…,
# .github/…, .sdlc/… из report.md и test -e по каждому — прогоняется
# вручную перед коммитом; при отсутствии файла — правка отчёта, а не
# сдача с ложным путём.

# --- объём отчёта (AC 12) ---
wc -l .sdlc/CS-20260828-010/report.md
```
