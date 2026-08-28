# Phase 0 — Infrastructure & Testing Reconnaissance (CS-20260828-004)

**SHA HEAD:** `b173d316d318f914602e2b00dee351a37a461759`
**Дата:** 2026-08-28
**Ветка:** `sdlc/CS-20260828-004`
**Метод:** статическое чтение репозитория (`find`, `wc`, `grep`, `sed`, `cat -n`)
на зафиксированном SHA. Ничего не запускалось: ни `./gradlew`, ни Docker, ни
Playwright, ни graphify-rebuild — порт 8080 может держать живой bootRun
пользователя, а любой прогон сборки пишет в `build/`/`node_modules/`, что
запрещено ограничением дельты этой задачи. Карта URL и разбор `app.*`-свойств
по потребителям уже сняты предшественниками (`.sdlc/CS-20260827-001/report.md`
§2, `.sdlc/CS-20260828-001/report.md` §16) — здесь не дублируются; конфигурация
разбирается как окружение (источник значения → профиль → env-override), а не
«кто читает».

## 0. Сводка одним экраном

| № | Рубрика | Факт | Главный якорь |
|---|---|---|---|
| 1 | Build | Gradle 8.9, Java 17, 3 модуля (`java-library`×2 + Spring Boot×1), version catalog на 59 строк, jacoco-гейты только в `quiz-app` | `settings.gradle.kts:11` |
| 2 | Deployment | Двухстадийный `Dockerfile` (68 строк) + `docker-compose.yml` (2 сервиса, 111 строк) + 3 GitHub Actions workflow, push в registry нет | `Dockerfile:2` |
| 3 | Environment | 4 профиля (default/prod/qa/test), 31 уникальная `${ENV_VAR}` в yml против 13 активных в `.env.example` | `modules/quiz-app/src/main/resources/application.yml:2` |
| 4 | Configuration | `application.yml` 236 строк по блокам server/spring/app/management + prod/qa/test-оверлеи; мёртвый ключ `app.preload.*` | `modules/quiz-app/src/main/resources/application.yml:93` |
| 5 | Logging | Нет `logback-spring.xml`/`log4j2.xml` — дефолт Spring Boot; 36 файлов с логгером (32 `@Slf4j` + 4 `LoggerFactory`), 39 warn/16 info/10 debug/4 error/0 trace | `modules/quiz-app/src/main/resources/application-prod.yml:11` |
| 6 | Monitoring | Actuator `health,info,metrics,prometheus`, 9 своих Micrometer-метрик в 3 классах, 1 `HealthIndicator` | `modules/quiz-app/src/main/resources/application.yml:192` |
| 7 | Карта тестов | 236 тест-файлов / 1296 `@Test` в 3 модулях; профиль `test` форсится системным свойством | `modules/quiz-app/build.gradle.kts:80` |
| 8 | Unit | Mockito в 43 файлах `quiz-app`, чистый JUnit+AssertJ в `quiz-domain`, 14+13 параметризованных, 1 `@Nested` | `modules/quiz-domain/build.gradle.kts:10-12` |
| 9 | Integration | 21 `@SpringBootTest` + 17 persistence-тестов на `AbstractPostgresRepositoryTest`; срезов `@WebMvcTest`/`@JdbcTest` нет вовсе | `modules/quiz-persistence/src/test/java/com/cheatsheet/quiz/persistence/AbstractPostgresRepositoryTest.java:1` |
| 10 | API | `MockMvc` в 14 файлах, `jsonPath` в 6; OpenAPI отключён в prod, контракта на сам документ нет | `modules/quiz-app/src/main/resources/application-prod.yml:42-45` |
| 11 | E2E | Playwright 1.52, 6 спеков (599 строк), 2 браузерных проекта; НЕ запускается ни в одном автоматическом workflow | `e2e/playwright.config.js:33-37` |
| 12 | Visual | Матрица 11 сценариев + 3 доп. вьюпорта, 28 голденов (14×2 движка), порог `maxDiffPixelRatio: 0.03`, пин `questionId=2060` | `e2e/visual-matrix.spec.js:62-77` |
| 13 | Accessibility | Ни axe-core, ни pa11y, ни lighthouse-a11y в репо нет; косвенное покрытие разметочными контрактами + контраст-гейт | `.pre-commit-config.yaml:43-49` |
| 14 | Гейты вне тестов | 5 pre-commit хуков, ArchUnit (`LayeredArchitectureTest`, 3 правила), 38 файлов-валидаторов (`.py`+`.sh`) в `scripts/`, из них к CI подключён 1 | `.pre-commit-config.yaml:1-49` |

---

## 1. Build

Gradle wrapper 8.9 (`gradle/wrapper/gradle-wrapper.properties:3`), собирается
через `distributionUrl=...gradle-8.9-bin.zip`. Java 17 задаётся корневым
`build.gradle.kts` через `subprojects{}`: `sourceCompatibility`/
`targetCompatibility = JavaVersion.VERSION_17` (`build.gradle.kts:21-24`),
`options.encoding = "UTF-8"` для всех `JavaCompile` (:26-28). `gradle.properties`
в репозитории отсутствует (`ls gradle.properties` → нет файла).

Version catalog `gradle/libs.versions.toml` — 59 строк, 13 закреплённых версий
(`[versions]`, :1-14), 36 модулей-библиотек (`[libraries]`, :16-55), 2 плагина
(`[plugins]`, :57-59): `spring-boot=3.3.2`, `spring-ai=1.0.0`,
`archunit=1.3.0`, `jacoco=0.8.12`, `springdoc=2.6.0`.

Три Gradle-модуля (`settings.gradle.kts:11`, `project(...).projectDir`
:13-15):
- **`quiz-domain`** — `java-library` (`modules/quiz-domain/build.gradle.kts:2`),
  без Spring Boot-плагина; тестовые зависимости — чистый JUnit 5 + AssertJ
  (:10-12).
- **`quiz-persistence`** — тоже `java-library` (`modules/quiz-persistence/
  build.gradle.kts:2`), плюс Flyway/JDBC/Testcontainers для интеграционных
  тестов (:16-19, 24-31).
- **`quiz-app`** — единственный модуль с `org.springframework.boot` +
  `io.spring.dependency-management` + `jacoco` (`modules/quiz-app/
  build.gradle.kts:1-6`).

Корень регистрирует прокси-задачи для обратной совместимости команд:
`compileJava`/`bootRun`/`bootJar`/`javadoc`, каждая `dependsOn(":quiz-app:...")`
(`build.gradle.kts:31-46`). `tasks.processResources` подставляет
`@projectVersion@` в `application.yml` через `ReplaceTokens`
(`modules/quiz-app/build.gradle.kts:163-169`). `springBoot.buildInfo`
генерирует `META-INF/build-info.properties` для `/actuator/info`, с
дополнительным полем `name` (:85-93).

**Jacoco-гейты — только `quiz-app`.** `quiz-domain/build.gradle.kts` и
`quiz-persistence/build.gradle.kts` не подключают плагин `jacoco` вовсе (оба
файла — только `java-library` + `dependencies`, см. полный листинг выше);
`modules/quiz-app/build.gradle.kts:3` — единственное место, где `jacoco`
объявлен в `plugins{}`. Правила верификации: `BUNDLE` LINE ≥ 0.85 / BRANCH ≥
0.70 (:118-130), `PACKAGE` LINE ≥ 0.55 для `api.*|service.*|feature.*` минус
`*.dto*` (:139-155). `tasks.check` зависит от `jacocoTestCoverageVerification`
(:159-161) — значит `quiz-domain`/`quiz-persistence` в `./gradlew check`
компилируются и тестируются, но покрытие для них никогда не проверяется
гейтом.

Инвентарная деталь: `quiz-app/build.gradle.kts:43-44` объявляет
`platform(libs.spring.ai.bom)` + `libs.spring.ai.openai`, но
`grep -rl "org.springframework.ai" modules/quiz-app/src/main --include='*.java'`
даёт 0 файлов — зависимость собирается, но ни один класс её не импортирует.

## 2. Deployment

**`Dockerfile`** (68 строк) — двухстадийная сборка:
- Стадия 1 (`gradle:8.9-jdk17`, :2-19): сначала копируются только
  build-скрипты и `gradle/` (:10-14), затем `gradle :quiz-app:dependencies`
  для прогрева кэша слоя зависимостей (:15), и только потом `COPY modules
  modules` + `bootJar` (:17-19) — правка исходников не инвалидирует
  dependency-слой.
- Стадия 2 (`eclipse-temurin:17-jre`, :22-68): непривилегированный
  пользователь UID/GID 10001 (:46), OCI-labels из ARG `VERSION`/`BUILD_DATE`/
  `VCS_REF` (:31-38, `image.licenses="MIT"` при отсутствии файла `LICENSE` в
  репозитории — `ls LICENSE*` не находит ничего), `JAVA_TOOL_OPTIONS` с
  `MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError` (:64),
  `SPRING_PROFILES_ACTIVE=prod` (:56), `HEALTHCHECK` на
  `/actuator/health/readiness` (:66-67).

**`docker-compose.yml`** (111 строк) — 2 сервиса:
- `postgres` (:5-29) — `postgres:16-alpine`, `no-new-privileges` (:9-10),
  healthcheck `pg_isready` (:19-24), json-file ротация 10m×3 (:25-29).
- `interview-prep` (:31-107) — билдится из локального `Dockerfile` (:32-40),
  `env_file: .env required: false` (:64-66, свежий клон без `.env` стартует и
  падает явной ошибкой про `APP_ADMIN_TOKEN`, а не тихо игнорирует конфиг),
  `depends_on: postgres condition: service_healthy` (:85-87), `tmpfs /tmp`
  128m (:49-50), json-file ротация 10m×5 (:100-104), bind-mount
  `./cheatsheets:/app/cheatsheets` для двусторонней синхронизации избранного
  (:106-107).

**CI: ровно 3 workflow** (`find .github -type f` → 3 файла, других файлов в
`.github/` нет):
- `.github/workflows/ci.yml` (110 строк) — 2 job'а: `build-test`
  (:28-73: `./gradlew build -x test` затем `./gradlew check` — тесты + jacoco
  + ArchUnit одним гейтом) и `docker-build` (:75-110, `needs: build-test`,
  только на `push` в `master`, :81, `push: false` — smoke-сборка образа без
  публикации, :100). Триггеры — `push`/`pull_request` с `paths` на
  `modules/**`, `gradle/**`, `*.gradle.kts`, `gradlew*` (:3-20).
- `.github/workflows/visual-goldens.yml` (116 строк) — 1 job `visual`
  (:20-116), триггер только `workflow_dispatch` с input'ом
  `update_snapshots` (:7-13) — поднимает postgres как GH Actions `services`
  (:25-38), стартует `bootRun` в фоне и ждёт готовности (:65-85), гоняет
  `visual-matrix.spec.js` в Chromium (:87-98).
- `.github/workflows/cheatsheet-lint.yml` (35 строк) — 1 job `lint` (:14-35),
  триггер `push`/`pull_request` на `paths: cheatsheets/**` +
  `scripts/cheatsheet-lint.py` (:3-11), запускает
  `python3 scripts/cheatsheet-lint.py cheatsheets` (:27).

Чего в CD нет: push в registry, отдельного release-workflow, dependabot,
k8s-манифестов — в репозитории таких файлов не найдено (`find . -iname
"*dependabot*"` и `find . -iname "*.yaml" -path "*k8s*"` пусты).

## 3. Environment

4 профиля Spring: default (`application.yml`, 236 строк, без суффикса),
`prod` (`application-prod.yml`, 56 строк), `qa` (`application-qa.yml`,
19 строк), `test` (`application-test.yml`, 15 строк, только в
`src/test/resources`, не попадает в jar).

- **default** — datasource на `localhost:5432`, `thymeleaf.cache=false`,
  `STATIC_RESOURCES_CACHE_SECONDS` дефолт 0 (revalidate на каждый запрос,
  `application.yml:87`), Hikari `leak-detection-threshold=30000`
  (`application.yml:57`).
- **prod** (`application-prod.yml`) — переопределяет `thymeleaf.cache=true`
  (:6), content-hash версионирование статики через
  `spring.web.resources.chain.strategy.content.enabled=true` (:16-19, с
  годовым `max-age` :30-31), `leak-detection-threshold=0` (:11), обязательный
  без дефолта `APP_ADMIN_TOKEN` (:50), выключает `springdoc.swagger-ui`/
  `api-docs` (:42-46).
- **qa** (`application-qa.yml`) — детерминированное окружение для
  визуального QA: комментарий (:1-7) описывает фиксированный `Clock`
  (`QaConfig`) и фикстуры due/learned/new (`QaFixtureRunner`); задаёт
  `app.qa.fixed-instant`, `due-fixtures: 2`, `learned-fixtures: 1` (:12-19) —
  активируется явно `SPRING_PROFILES_ACTIVE=qa`.
- **test** (`application-test.yml`, только `src/test/resources`) — Testcontainers
  JDBC URL `jdbc:tc:postgresql:16-alpine:///interview` (:6), фиксированный
  `app.admin-token: test-secret-token` (:15).

**Полная таблица `${ENV_VAR}` → дефолт → профиль.** Команда (Приложение A)
даёт ровно 31 уникальное имя по всем `application*.yml` (main) +
`application-test.yml`:

| Переменная | Дефолт (профиль) |
|---|---|
| `SERVER_PORT` | `8080` (default) |
| `SERVER_FORWARD_HEADERS` | `framework` (default) |
| `SHUTDOWN_TIMEOUT` | `30s` (default) |
| `SPRING_DATASOURCE_URL` | сборный из `POSTGRES_HOST/PORT/DB` (default) |
| `POSTGRES_HOST` | `localhost` (default, вложен в URL) |
| `POSTGRES_PORT` | `5432` (default, вложен) |
| `POSTGRES_DB` | `interview` (default, вложен) |
| `SPRING_DATASOURCE_USERNAME` | `${POSTGRES_USER:interview}` (default) |
| `SPRING_DATASOURCE_PASSWORD` | `${POSTGRES_PASSWORD:interview}` (default) |
| `POSTGRES_USER` | `interview` (default, вложен) |
| `POSTGRES_PASSWORD` | `interview` (default, вложен) |
| `DB_POOL_MAX_SIZE` | `10` (default) |
| `DB_POOL_MIN_IDLE` | `2` (default) |
| `DB_POOL_LEAK_DETECTION_MS` | `30000` (default) |
| `FLYWAY_CLEAN_DISABLED` | `true` (default) |
| `STATIC_RESOURCES_CACHE_SECONDS` | `0` (default) / `31536000` (prod) |
| `APP_ADMIN_TOKEN` | пусто (default) / без дефолта, обязателен (prod) |
| `SENSITIVE_RATE_LIMIT_PER_MINUTE` | `20` (default) |
| `SCHEDULING_ALGORITHM` | `sm2` (default) |
| `INTERVIEW_RESET_ON_STARTUP` | `false` (default) |
| `APP_SECURITY_MODE` | `local` (default + prod) |
| `APP_SECURITY_ALLOW_INSECURE_PUBLIC` | `false` (default + prod) |
| `APP_SECURITY_MULTI_USERNAME` | `trainer` (default) |
| `APP_SECURITY_MULTI_PASSWORD` | `trainer` (default) |
| `APP_SECURITY_OAUTH_ENABLED` | `false` (default) |
| `APP_SECURITY_OAUTH_REGISTRATION_ID` | `oidc` (default) |
| `APP_SECURITY_OAUTH_DISPLAY_NAME` | `OIDC` (default) |
| `APP_SECURITY_OAUTH_GROUPS_CLAIM` | `groups` (default) |
| `HEALTH_CACHE_SECONDS` | `5` (default) |
| `APP_ENV` | `dev` (default) / `prod` (prod) |
| `APP_CORS_ALLOWED_ORIGINS` | без дефолта / `https://localhost` (prod) |

Расхождение с `.env.example`: **31 переменная в yml против 13 активных
(раскомментированных) строк в `.env.example`** (`grep -cE
'^[A-Z_][A-Z0-9_]*=' .env.example` → 13: `APP_ADMIN_TOKEN`,
`APP_CORS_ALLOWED_ORIGINS`, `SENSITIVE_RATE_LIMIT_PER_MINUTE`, `APP_PORT`,
`APP_VERSION`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_PORT`,
`INTERVIEW_RESET_ON_STARTUP`, `DB_POOL_MAX_SIZE`, `DB_POOL_MIN_IDLE`,
`APP_ENV`, `SERVER_FORWARD_HEADERS`; из них `APP_PORT`/`APP_VERSION` —
compose-only, не читаются приложением). Ещё 7 переменных упомянуты
закомментированными строками-подсказками (`.env.example:28,52-53,69,74,78,82`:
`JAVA_TOOL_OPTIONS`, `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE`,
`MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS`, `DB_POOL_LEAK_DETECTION_MS`,
`STATIC_RESOURCES_CACHE_SECONDS`, `HEALTH_CACHE_SECONDS`,
`SHUTDOWN_TIMEOUT`). **16 переменных из 31 не упомянуты в `.env.example`
вообще — ни активной строкой, ни комментарием** (команда в Приложении A):
`APP_SECURITY_ALLOW_INSECURE_PUBLIC`, `APP_SECURITY_MODE`,
`APP_SECURITY_MULTI_PASSWORD`, `APP_SECURITY_MULTI_USERNAME`,
`APP_SECURITY_OAUTH_DISPLAY_NAME`, `APP_SECURITY_OAUTH_ENABLED`,
`APP_SECURITY_OAUTH_GROUPS_CLAIM`, `APP_SECURITY_OAUTH_REGISTRATION_ID`,
`FLYWAY_CLEAN_DISABLED`, `POSTGRES_DB`, `POSTGRES_HOST`,
`SCHEDULING_ALGORITHM`, `SERVER_PORT`, `SPRING_DATASOURCE_PASSWORD`,
`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`.

## 4. Configuration

`modules/quiz-app/src/main/resources/application.yml` — 236 строк, 4 блока:

- **`server`** (:1-34) — `shutdown: graceful` (:6), `servlet.session.
  tracking-modes: cookie` (:13, комментарий :9-12 объясняет: URL rewriting
  ломает статику без cookie на первой странице), `forward-headers-strategy`
  (:19), gzip-компрессия 6 mime-типов включая `text/javascript` первым
  (комментарий :24-30 — RFC 9239, иначе не сжимался JS-бандл вопросов) (:31-34).
- **`spring`** (:36-88) — `datasource` с Hikari (`reWriteBatchedInserts:
  true` для батч-импорта сидеров, :63-64), `flyway` (`locations:
  classpath:db/migration`, :65-72), `thymeleaf.cache: false` (:74),
  `web.resources.cache.cachecontrol.max-age: 0s` + `must-revalidate: true`
  (:83-88).
- **`app`** (:93-184) — `interview-path`/`topic-groups-config` (:95-97),
  `admin-token`/`sensitive-rate-limit-per-minute` (:100-101),
  `scheduling.algorithm` (:104-107), `interview.*` (6 числовых параметров +
  пустая map-настройка `senior-rule-priority-overrides`, :110-117),
  `cache.*` (:120-122), `search.*` (:125-128),
  `import-settings.min-code-block-length` (:131-132),
  `interview-reset-on-startup` (:138), `security.*` (mode/allow-insecure-
  public/multi/oauth/cors, :141-184).
- **`management`** (:186-236) — см. §6.

**Prod-оверлей** (`application-prod.yml`, 56 строк) переопределяет 5 узлов:
`thymeleaf.cache`, Hikari `leak-detection-threshold`, `web.resources.cache`
(content-hash версия, :12-31), `management.metrics.tags.env`, `springdoc.*`
(:42-46), `app.admin-token`/`security.*` (:48-56).

**QA-оверлей** (`application-qa.yml`, 19 строк) добавляет 2 новых узла:
`app.preload.startupPreload`/`fullWarmup` (:9-11) и `app.qa.*`
(fixed-instant/due-fixtures/learned-fixtures, :12-19).

**Test-оверлей** (`application-test.yml`, 15 строк, `src/test/resources`)
переопределяет только `datasource`/`flyway` под Testcontainers и
`app.interviewPath`/`app.admin-token` (см. §3).

`@projectVersion@` — единственный ANT-токен в конфигурации, подставляется
`processResources` (`modules/quiz-app/build.gradle.kts:163-169`) в
`spring.application.version` (`application.yml:39`).

**Мёртвая настройка.** `application-qa.yml:9-11` задаёт
`app.preload.startupPreload: false` и `app.preload.fullWarmup: false`, но
`grep -rn "startupPreload\|fullWarmup" modules/quiz-app/src/main/java` даёт 0
совпадений — ни один класс не биндит `app.preload.*` (нет
`@ConfigurationProperties(prefix = "app.preload")` и нет прямого чтения
через `Environment`). Ключ синтаксически валиден (YAML не упадёт), но
семантически ни на что не влияет.

## 5. Logging

Ни `logback-spring.xml`, ни `log4j2.xml` в репозитории нет (`find . -iname
"logback*.xml" -o -iname "log4j2*.xml"` вне `build/`/`node_modules/` — пусто);
в `application*.yml` нет ни одного ключа `logging.*` (`grep -rln "^logging:"
modules/quiz-app/src/main/resources/*.yml modules/quiz-app/src/test/
resources/*.yml` — пусто). Логирование работает целиком на дефолтах Spring
Boot (Logback + консольный паттерн из `spring-boot-starter-logging`).

**36 файлов с логгером** во всех трёх модулях main-кода: 32 через
`@Slf4j` (Lombok) + 4 через `LoggerFactory.getLogger` явно, без пересечений
(`comm -12` двух списков — пусто). Разбивка по модулям: `quiz-app` 29+3,
`quiz-domain` 1+1, `quiz-persistence` 2+0 (команды — Приложение A).

Распределение вызовов по уровням (по всем трём модулям, main): `warn` 39,
`info` 16, `debug` 10, `error` 4, `trace` 0 — уровень `trace` в коде не
используется вовсе.

Ротация логов задана только на уровне Docker: `docker-compose.yml` —
json-file `max-size: 10m, max-file: 3` для postgres (:25-29) и `10m, 5` для
приложения (:100-104); на уровне самого приложения (logback) ротации нет,
т.к. нет конфиг-файла logback.

**Hikari leak-detection dev vs prod.** `application.yml:57` —
`leak-detection-threshold: ${DB_POOL_LEAK_DETECTION_MS:30000}` (30s, ловит
незакрытые connection); `application-prod.yml:11` — жёстко `0` (отключено),
с комментарием (:9-10) «иначе ложные stack trace на длинных batch/import» —
единственный инфраструктурный сигнал, который в проде осознанно выключен.

## 6. Monitoring

Actuator: `management.endpoints.web.exposure.include: health,info,metrics,
prometheus` (`application.yml:192`). Probes: `management.endpoint.health.
probes.enabled: true` (:199), раздельные группы `liveness` (`livenessState`,
:219-220) и `readiness` (`readinessState,db`, :221-222) — liveness намеренно
не включает `db`/`seedCoverage` (комментарий :207-217: деградация БД не
должна триггерить рестарт пода). `show-details: never`, `show-components:
never` (:200-201). Health-агрегат кэшируется `${HEALTH_CACHE_SECONDS:5}s`
(:206) — под типовые 10s-пробы K8s/compose каждый второй scrape отдаётся из
памяти без удара по БД.

`micrometer-registry-prometheus` — `runtimeOnly`
(`modules/quiz-app/build.gradle.kts:20`), с комментарием на той же строке
«Grafana dashboard in release package» (:19).

**Все 9 своих метрик Micrometer, класс-владелец и строка:**

| Метрика | Класс:строка | Тип |
|---|---|---|
| `attempt.submit.duration` | `SubmitAnswerCommand.java:37` | Timer |
| `attempt.idempotency.claim.conflict` | `SubmitAnswerCommand.java:40` | Counter |
| `attempt.revision.mismatch` | `SubmitAnswerCommand.java:43` | Counter |
| `attempt.submit.success` | `SubmitAnswerCommand.java:46` | Counter |
| `attempt.journal.write.failure` | `InterviewSessionSupport.java:51` | Counter |
| `attempt.submit.duplicate` | `InterviewSessionSupport.java:54` | Counter |
| `mcq.seed.topic.requests` | `McqJsonLoader.java:90,94` | Counter (2 инстанса, found/not-found) |
| `mcq.seed.questions.skipped` | `McqJsonLoader.java:101` | Counter |
| `mcq.seed.options.inserted` | `McqJsonLoader.java:98` | Counter |

Все три класса — под `modules/quiz-app/src/main/java/com/cheatsheet/quiz/`
(`feature/interview/service/command/`, `feature/interview/controller/`,
`service/imports/` соответственно).

**Один `HealthIndicator`** — `SeedCoverageHealthIndicator`
(`modules/quiz-app/src/main/java/com/cheatsheet/quiz/infrastructure/health/
SeedCoverageHealthIndicator.java`, 89 строк, `@Component("seedCoverage")`,
:38). Отражает долю вопросов с загруженными MCQ-вариантами; порог
`MIN_COVERAGE_RATIO = 0.05` (:41) намеренно низкий — проект мигрирует на
JSON-сидеры постепенно, DOWN означает «импорт сломан», а не «контент не
дозалит» (javadoc :9-18). Не входит в readiness-группу (не гейтит трафик,
:20-23).

Общие теги на все метрики: `application=interview-prep`, `env=${APP_ENV:dev}`
(default) / `env=${APP_ENV:prod}` (`application-prod.yml:38`)
(`application.yml:233-236`).

**Заявлено, но отсутствует.** Комментарий `modules/quiz-app/build.gradle.kts:19`
обещает «Grafana dashboard in release package». `find . -iname "*grafana*"`
(вне `build/`/`node_modules/`/`.git/`) находит только шпаргалки
(`cheatsheets/interview/monitoring/*grafana*.md`) и их MCQ-сидеры/аудит-копии
(`modules/quiz-app/src/main/resources/seed/mcq/monitoring/*grafana*.json`,
`docs/mcq-quality/**/*grafana*.json`) — ни одного файла дашборда в
репозитории нет.

## 7. Карта тестов

Всего **236 тест-файлов и 1296 `@Test`** в трёх модулях (команды —
Приложение A):

| Модуль | Файлов | `@Test` |
|---|---|---|
| `quiz-domain` | 21 | 94 |
| `quiz-persistence` | 20 | 119 |
| `quiz-app` | 195 | 1083 |
| — из них `quiz-app/.../ui/` | 77 | 421 |

Соглашение именования — суффикс `*Test.java` (контрактные UI-тесты — суффикс
`*ContractTest.java` внутри пакета `ui/`, например
`modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/
FocusHandoffContractTest.java`).

Профиль `test` форсится для ВСЕХ тестов `quiz-app` системным свойством,
независимо от `@ActiveProfiles`:

```kotlin
tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}
```

(`modules/quiz-app/build.gradle.kts:76-81`). URL —
`jdbc:tc:postgresql:16-alpine:///interview` (`application-test.yml:6`) —
Testcontainers поднимает контейнер автоматически, без ручного docker-compose.

## 8. Unit

**Mockito-слой `quiz-app`** — `@ExtendWith(MockitoExtension...` в 43 файлах
(`grep -rl "@ExtendWith(MockitoExtension" modules/quiz-app/src/test`).

**Чистый JUnit `quiz-domain`** — build-файл модуля не тянет Spring вовсе:
только `org.junit.jupiter:junit-jupiter` + `org.assertj:assertj-core`
(`modules/quiz-domain/build.gradle.kts:10-12`), тесты работают на голых
доменных объектах без DI-контейнера.

**Параметризация:** `@ParameterizedTest` — 14 файлов `quiz-app`, 13 файлов
`quiz-domain`, 0 файлов `quiz-persistence` (persistence-тесты — по одному
сценарию на репозиторный метод, без табличных кейсов).

**`@Nested`** — 1 файл во всём репозитории (`grep -rl "@Nested" modules
--include='*.java' | grep '/src/test/'`), т.е. группировка тестов вложенными
классами практически не используется — плоские flat-классы являются нормой.

## 9. Integration

**`quiz-app`** — `@SpringBootTest` в 21 файле (`grep -rl "@SpringBootTest"
modules/quiz-app/src/test --include='*.java'`), полный контекст Spring на
каждый такой тест (срезов `@WebMvcTest`/`@DataJdbcTest`/`@JdbcTest` в
репозитории нет — 0 файлов при поиске по всем трём модулям).

**`quiz-persistence`** — 20 тест-файлов: 1 — сама
`AbstractPostgresRepositoryTest.java` (базовый класс, не тест); 17 —
repository-тесты, наследующие его (Testcontainers PostgreSQL 16-alpine, один
контейнер на JVM, изоляция через TRUNCATE между тестами); 2 —
`InterviewFilterSqlTest.java` и `OwnerContextTest.java` — чистые unit без
`@Testcontainers` (проверяют построение SQL и owner-контекст без реальной БД).

**API-срез MockMvc** (детали — §10) фактически и есть основной способ
интеграционного тестирования HTTP-слоя `quiz-app`: контроллер + Spring MVC +
сервисы полным стеком через `@SpringBootTest` + `@AutoConfigureMockMvc`, без
изолированных web-срезов.

## 10. API

`MockMvc` используется в 14 файлах, `jsonPath` — в 6 из них,
`@AutoConfigureMockMvc` — в 12 (числа не совпадают ровно, т.к. часть тестов
собирает `MockMvc` вручную через `MockMvcBuilders` без аннотации). OpenAPI-
описание собирается в `modules/quiz-app/src/main/java/com/cheatsheet/quiz/
config/web/OpenApiConfig.java`; springdoc UI и `/v3/api-docs` включены по
умолчанию (default/qa-профили), но выключены в prod:

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
```

(`application-prod.yml:42-46`). `SwaggerProdDisabledTest.java:59` обращается
к `/v3/api-docs`, но проверяет только его выключенность в prod-профиле
(тело ответа больше не содержит OpenAPI JSON); контрактного теста, который
сверял бы итоговый документ со снапшотом или JSON-схемой в default/qa-
профилях, в репозитории нет.

## 11. E2E

`e2e/` — Playwright `^1.52` (см. `e2e/package.json`), **6 спеков, 599 строк
суммарно**:

| Спек | Строк | Назначение |
|---|---|---|
| `keyboard-quiz.spec.js` | 231 | TEST-006 — основной цикл прохождения квиза только с клавиатуры (без мыши), в обоих движках |
| `visual-matrix.spec.js` | 206 | TEST-005 — визуальная матрица theme × pixel × motion (детали §12) |
| `reflow.spec.js` | 50 | WCAG 1.4.10 Reflow: контент перекладывается без горизонтального скролла на 320 CSS px |
| `console-clean.spec.js` | 38 | Консоль и сеть чисты на каждой поверхности (ловит JS-ошибки, которые не ловит ни клавиатурный, ни визуальный спек) |
| `chart-lazy.spec.js` | 37 | Chart.js грузится лениво — оплачивается просмотром `/stats`, а не существованием графиков |
| `font-network.spec.js` | 37 | Каждый woff2-шрифт качается ровно один раз, в обоих движках |

Конфиг `e2e/playwright.config.js` (48 строк): 2 проекта — `chromium` и
`webkit` (`use: {...devices['Desktop Chrome'/'Desktop Safari']}`, :33-37),
`workers: 1`, `fullyParallel: false` (:24,26 — детерминизм важнее скорости),
`baseURL` по умолчанию `http://localhost:8080` (:4), опциональный
`webServer`, который поднимается только при `E2E_START_SERVER=1`
(`command: '../gradlew :quiz-app:bootRun -q'`, :39-47) — по умолчанию тесты
ожидают уже поднятое приложение.

Локальный запуск — 2 shell-скрипта: `scripts/run-keyboard-e2e.sh` и
`scripts/run-visual-e2e.sh` — оба поднимают `docker compose up -d postgres`,
устанавливают `e2e`-зависимости, при необходимости стартуют `bootRun` в фоне
и дожидаются готовности перед `npx playwright test`.

**E2E не запускается ни в одном автоматическом workflow.** `ci.yml` (§2) не
содержит ни одного шага с `playwright`/`npx`/`e2e` (`grep -n "playwright\|e2e"
.github/workflows/ci.yml` — пусто). Единственное место, где спеки реально
гоняются в CI-окружении, — `visual-goldens.yml`, и его триггер строго
`workflow_dispatch` (`.github/workflows/visual-goldens.yml:7-8`) — вручную по
кнопке, не на push/PR.

## 12. Visual

Матрица `MATRIX` в `visual-matrix.spec.js:62-77` — **11 сценариев**
(theme × pixel-density × motion, разные страницы): 5 на `/` (pinned
`questionId=2060`, dark/light × low/med/high × on/off/auto), 2 на `/train`,
2 на `/settings`, 2 на `/stats`. Отдельно — `VIEWPORTS` (:80-84), 3
дополнительных вьюпорта (375/768/1280) для «Wave 8 responsive smoke
(subset)» — снимаются только для home.

Голдены — **28 PNG: 14 на движок** (`find e2e/visual-baselines/chromium
-name '*.png'` → 14, то же для `webkit`), путь `e2e/visual-baselines/
{chromium,webkit}/<id>.png`. Раздельные каталоги на движок — комментарий в
конфиге объясняет: у Chromium и WebKit разные шрифтовые метрики, общий путь
означал бы перезапись голденов одного движка другим (`playwright.config.js`
:20-22). Порог: `maxDiffPixelRatio: 0.03`, `animations: 'disabled'`
(`playwright.config.js:16-17`).

**Техника детерминизма:**
- Пин по явному `questionId=2060` + `ordered=true&shuffle=false`
  (`visual-matrix.spec.js:33-36`) — история эволюции задокументирована прямо
  в коде тремя комментариями (:15-32): сначала маскировали контент, затем
  пиновали темой (ломалось SRS-очередью), в итоге закрепили конкретный
  `questionId`.
- `DYNAMIC_CONTENT` (:38-44) — 15 CSS-селекторов, маскируемых в
  `toHaveScreenshot({ mask: [...] })` (:148, :174) — значения скрыты, но
  геометрия остаётся под охраной пикселей.
- `VOLATILE_PRESENCE` (:56) — 4 селектора (`.focus-question-reason`,
  `.resume-banner`, `.notice`, `.today-chip`), которые то есть, то нет —
  скрываются целиком через `display: none !important` (:57-58), т.к. маска
  не лечит присутствие/отсутствие блока, только его содержимое.

Java-двойник — `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/
VisualBaselineContractTest.java` (214 строк) — держит контракты разметки,
которые обслуживают визуальную матрицу (например, наличие масштабируемых
классов), со стороны Java без запуска браузера.

**Workflow обновления голденов** — `visual-goldens.yml` (§2, §11): вручную
через `workflow_dispatch` с `input.update_snapshots=true` →
`npx playwright test visual-matrix.spec.js --update-snapshots` (:94-98),
результат заливается артефактом `visual-goldens-${{ github.run_id }}`
(:100-109) — т.е. голдены переписываются НЕ автоматическим коммитом, а
руками после скачивания артефакта.

## 13. Accessibility

Ни axe-core, ни pa11y, ни lighthouse-a11y в репозитории нет. Прямой поиск
`grep -rl "axe\|pa11y" e2e/ scripts/` формально находит 1 файл
(`scripts/restore_mermaid.py`), но это ложное срабатывание на слово
«pickaxe» в комментарии (:5) — реальных упоминаний инструментов a11y-аудита
(`grep -rlE "axe-core|pa11y|@axe-core" e2e/ scripts/ package.json e2e/
package.json` — 0 файлов) в проекте нет. В `e2e/package.json` нет пакетов
`@axe-core/playwright` или `pa11y` среди зависимостей.

A11y покрыта косвенно — разметочными контрактами в `ui/`
(`modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/*ContractTest.java`):
`aria-` встречается в 17 файлах, `role=` — в 7, `tabindex` — в 7, `focus`
(регистронезависимо) — в 43, `prefers-reduced-motion` — в 2
(`FocusHandoffContractTest.java` и аналогичный контракт мотора анимаций —
команда точного списка в Приложении A). Плюс `reflow.spec.js` (§11) —
WCAG 1.4.10 Reflow на уровне Playwright.

Контраст-гейт — `scripts/design-token-audit.py` (12.6K), подключён как
pre-commit хук `design-token-audit`
(`.pre-commit-config.yaml:43-49`, триггер — любая правка `static/css/*.css`);
проверяет контраст, полноту light/dark-токенов и хардкоды цветов в
`base.css` — но это гейт дизайн-токенов, не автоматизированный аудит
готовой страницы (не эквивалент axe/lighthouse).

## 14. Гейты качества вне тестов

**`.pre-commit-config.yaml`** (49 строк) — ровно 5 локальных хуков
(`repo: local`, все `stages: [pre-commit]`):

1. `cheatsheet-lint` (:4-10) — `python3 scripts/cheatsheet-lint.py
   cheatsheets`, триггер `cheatsheets/*.md`.
2. `verify-mcq-json` (:12-18) — `bash scripts/verify-mcq-json.sh`, триггер
   `modules/quiz-app/src/main/resources/seed/mcq/*.json`.
3. `verify-md-no-mcq` (:20-26) — `bash scripts/verify-md-no-mcq.sh`, триггер
   `cheatsheets/interview/*.md` (банит legacy MCQ-callouts в markdown).
4. `css-leak-audit` (:35-41) — `python3 scripts/css-leak-audit.py`, триггер
   CSS-каталог/манифест/шаблоны, `pass_filenames: false` (смотрит весь
   каталог целиком, не список изменённых файлов).
5. `design-token-audit` (:43-49) — см. §13.

**ArchUnit** — `modules/quiz-app/src/test/java/com/cheatsheet/quiz/
architecture/LayeredArchitectureTest.java` (45 строк, 3 правила через
`@ArchTest`/`ArchRuleDefinition`), запускается как обычный JUnit-тест внутри
`./gradlew check` — гейтуется в `ci.yml` job `build-test`, в отличие от
pre-commit хуков: они локальны, отдельного CI-джоба «прогони pre-commit» нет
(`grep -n "pre-commit" .github/workflows/*.yml` — 0 совпадений).

**Python-валидаторы в `scripts/`** — 38 файлов (`*.py`+`*.sh`, команда в
Приложении A), из которых к автоматическому гейту подключены только 2:
`cheatsheet-lint.py` (pre-commit хук №1 и отдельный CI-job
`cheatsheet-lint.yml`) и опосредованно `design-token-audit.py`/
`css-leak-audit.py` (только pre-commit, без дублирующего CI-job). Остальные
~35 — MCQ-валидаторы (`mcq-answer-parity-gate.py`, `mcq-linkage-scan.py`,
`mcq-anti-tell-scan.py`, `mcq-structure-parity.py`, `mcq-skel-gate.py` и др.),
генераторы (`gen-interview-plan.py`, `gen-section-stickers.py`) и
одноразовые утилиты (`restore_mermaid.py`, `migrate-md-to-json.sh`,
`healthcheck.sh`) — вызываются вручную операторами/skill'ами
(`mcq-quality-fixer` и др.), ни pre-commit, ни CI их не запускает.

## 15. Белые пятна разведки

1. **Фактическое покрытие тестами (jacoco XML/HTML).** Статическим чтением
   не измерить реальный `COVEREDRATIO` — только конфигурацию порогов (§1).
   Закрывается прогоном `./gradlew jacocoTestReport` в изолированной среде
   (follow-up уже заведён в design.md).
2. **Реальный проход E2E/визуальной матрицы на текущем HEAD.** Статикой
   видна конфигурация (§11-§12), но не то, проходят ли сейчас все 6 спеков и
   28 голденов. Закрывается прогоном `scripts/run-visual-e2e.sh` +
   `scripts/run-keyboard-e2e.sh` на выделенном порту (не 8080).
3. **Успешность реальной Docker-сборки образа.** `Dockerfile` инвентаризован
   построчно (§2), но `docker build .` ни разу не запускался. Закрывается
   `docker build --build-arg VERSION=probe .` вне рабочего дерева репозитория
   (например, в `/tmp`-checkout), чтобы не трогать дельту задачи.
4. **Actuator-детали при `show-details=always`.** `.env.example:52-53`
   документирует `MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS`, но по умолчанию
   `show-details: never` (`application.yml:200`) — что именно отдаёт
   `/actuator/health` с этим флагом, не проверено вживую. Закрывается `curl`
   с этим env на поднятом инстансе.

## Приложение A. Команды воспроизведения

```bash
# Шапка
git rev-parse HEAD && git branch --show-current && git status --porcelain -uall

# §1 — build: модули, jacoco-гейты
wc -l gradle/libs.versions.toml
cat settings.gradle.kts
for m in quiz-domain quiz-persistence quiz-app; do
  echo "== $m =="; head -5 modules/$m/build.gradle.kts
done
grep -n "jacoco" modules/quiz-domain/build.gradle.kts modules/quiz-persistence/build.gradle.kts modules/quiz-app/build.gradle.kts
grep -rl "org.springframework.ai" modules/quiz-app/src/main --include='*.java' | wc -l   # 0 — dead dependency

# §2 — deployment
find .github -type f | sort
wc -l Dockerfile docker-compose.yml .github/workflows/*.yml
ls LICENSE* 2>&1                                                                          # нет файла

# §3 — environment: 31 ${ENV_VAR}, .env.example diff
grep -ohE '\$\{[A-Za-z_][A-Za-z0-9_]*' \
  modules/quiz-app/src/main/resources/application.yml \
  modules/quiz-app/src/main/resources/application-prod.yml \
  modules/quiz-app/src/main/resources/application-qa.yml \
  modules/quiz-app/src/test/resources/application-test.yml | sed -E 's/\$\{//' | sort -u | tee /tmp/yml_vars.txt | wc -l
grep -ohE '^[A-Z_][A-Z0-9_]*=' .env.example | sed 's/=$//' | sort -u | tee /tmp/env_active.txt | wc -l   # 13
grep -ohE '^#?[[:space:]]*[A-Z_][A-Z0-9_]*=' .env.example | sed -E 's/^#[[:space:]]*//; s/=$//' | sort -u > /tmp/env_all.txt
comm -23 /tmp/yml_vars.txt /tmp/env_all.txt                                               # 16 нигде не упомянутых

# §4 — configuration: мёртвый app.preload.*
sed -n '1,20p' modules/quiz-app/src/main/resources/application-qa.yml
grep -rn "startupPreload\|fullWarmup" modules/quiz-app/src/main/java          # 0 совпадений

# §5 — logging
find . -iname "logback*.xml" -o -iname "log4j2*.xml" | grep -v -e build/ -e node_modules/
grep -rl "@Slf4j" modules/quiz-app/src/main/java modules/quiz-domain/src/main/java modules/quiz-persistence/src/main/java | wc -l   # 32
grep -rl "LoggerFactory\.getLogger" modules/quiz-app/src/main/java modules/quiz-domain/src/main/java modules/quiz-persistence/src/main/java | wc -l  # 4
for lvl in warn info debug error trace; do
  grep -rohE "log\.$lvl\(|logger\.$lvl\(" modules/quiz-app/src/main/java modules/quiz-domain/src/main/java modules/quiz-persistence/src/main/java | wc -l
done

# §6 — monitoring: 9 метрик
grep -rnE "Counter\.builder|Timer\.builder" modules/quiz-app/src/main/java
find modules -iname "*HealthIndicator*.java" | grep -v test
find . -iname "*grafana*" -not -path "*/node_modules/*" -not -path "*/build/*" -not -path "*/.git/*"

# §7-§9 — карта тестов
for m in quiz-domain quiz-persistence quiz-app; do
  echo "$m: files=$(find modules/$m/src/test -name '*.java' | wc -l) tests=$(grep -rohE '@Test\b' modules/$m/src/test --include='*.java' | wc -l)"
done
find modules/quiz-app/src/test -path "*/ui/*" -name "*.java" | wc -l                      # 77
grep -rohE "@Test\b" $(find modules/quiz-app/src/test -path "*/ui/*" -name "*.java") | wc -l  # 421
grep -rl "@ExtendWith(MockitoExtension" modules/quiz-app/src/test --include="*.java" | wc -l  # 43
grep -rl "@ParameterizedTest" modules/quiz-app/src/test --include="*.java" | wc -l         # 14
grep -rl "@Nested" modules --include="*.java" | grep '/src/test/' | wc -l                  # 1
grep -rl "@SpringBootTest" modules/quiz-app/src/test --include="*.java" | wc -l            # 21
grep -rL "extends AbstractPostgresRepositoryTest" $(find modules/quiz-persistence/src/test -name "*.java")
grep -rl "@WebMvcTest\|@DataJdbcTest\|@JdbcTest" modules --include="*.java" | grep '/src/test/' | wc -l  # 0
sed -n '76,81p' modules/quiz-app/build.gradle.kts

# §10 — API
grep -rl "MockMvc" modules/quiz-app/src/test --include="*.java" | wc -l                    # 14
grep -rl "jsonPath" modules/quiz-app/src/test --include="*.java" | wc -l                   # 6
grep -rl "@AutoConfigureMockMvc" modules/quiz-app/src/test --include="*.java" | wc -l      # 12
find modules -iname "OpenApiConfig.java"

# §11 — E2E
wc -l e2e/*.spec.js
grep -n "playwright\|npx\|e2e" .github/workflows/ci.yml                                    # пусто
cat e2e/playwright.config.js

# §12 — visual
find e2e/visual-baselines -type f -name "*.png" | wc -l                                    # 28
find e2e/visual-baselines/chromium -type f -name "*.png" | wc -l                           # 14
sed -n '62,84p' e2e/visual-matrix.spec.js
wc -l modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/VisualBaselineContractTest.java

# §13 — accessibility
grep -rlE "axe-core|pa11y|@axe-core" e2e/ scripts/ e2e/package.json 2>/dev/null | wc -l     # 0
grep -rl "aria-" modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui | wc -l              # 17
grep -rl "role=" modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui | wc -l              # 7
grep -rl "tabindex" modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui | wc -l           # 7
grep -rli "focus" modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui | wc -l             # 43
grep -rl "prefers-reduced-motion" modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui | wc -l  # 2

# §14 — гейты вне тестов
cat .pre-commit-config.yaml
ls scripts/*.py scripts/*.sh | wc -l
find modules -path "*architecture*" -name "*.java"

# AC8 — существование ВСЕХ путей, упомянутых в отчёте
grep -oE '(modules|cheatsheets|e2e|docs|scripts|gradle|\.github|\.sdlc)/[A-Za-z0-9_./-]+' \
  .sdlc/CS-20260828-004/report.md \
  | sed -E 's/[):,.]+$//; s/:[0-9]+(-[0-9]+)?$//' | sort -u | while read -r p; do test -e "$p" || echo "MISSING: $p"; done
# ожидаемый вывод: пусто
```
