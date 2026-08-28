# Phase 0 — Infrastructure & Testing Reconnaissance (CS-20260828-002)

**SHA HEAD:** `dff68c5503a71e39bc38b5eb103d5e1db9113474` (`dff68c55`)
**Дата:** 2026-08-28
**Ветка:** `sdlc/CS-20260828-002`
**Метод:** статическое чтение репозитория (`git rev-parse`, `find | wc`, `grep` по
конфигам/аннотациям/workflow'ам, чтение `Dockerfile`, `docker-compose.yml`,
`application*.yml`, `playwright.config.js`, `.github/workflows/*.yml`,
`.pre-commit-config.yaml`). Приложение и Gradle-сборка **не запускались** —
задача требует, чтобы дельта коммита осталась внутри `.sdlc/CS-20260828-002/`,
а `build/`-каталоги от `./gradlew check`/Playwright загрязнили бы worktree.
Третий блок Phase 0 §5 миссии `cheat-sheet-product-rebuild.md` (после FRONTEND
CS-20260827-001 и BACKEND CS-20260827-002); закрывает рубрики INFRASTRUCTURE
и TESTING.

---

## 0. Сводка одним экраном

| Рубрика | Факт | Якорь |
| --- | --- | --- |
| Build | 3 Gradle-модуля (Kotlin DSL), Java 17, jacoco-гейт LINE≥0.85/BRANCH≥0.70 | §1 |
| Deployment | 2-стадийный `Dockerfile`, `docker-compose.yml` (postgres+app), push в registry нет | §2 |
| Environment | `.env.example` — 13 активных переменных + 7 закомментированных knob'ов | §3 |
| Configuration | 5 `application*.yml` (base/prod/qa/test/oauth-example), 236 строк в базовом | §4 |
| Logging | Нет logback/log4j-конфига и ключа `logging:`; MDC-correlation есть, в лог не идёт | §5 |
| Monitoring | actuator+micrometer-prometheus, health-группы liveness/readiness+db | §6 |
| unit | 235 test-файлов (21/20/194 по модулям), 1298 `@Test` | §7 |
| integration | 21 `@SpringBootTest`, 8 Testcontainers, 18 упоминаний `AbstractPostgresRepositoryTest` | §8 |
| E2E | 6 Playwright-спеков в `e2e/`, ни один не триггерится в `ci.yml` (paths-фильтр) | §9 |
| visual | 14 голденов×2 движка в `e2e/visual-baselines/`, автоматизирован только chromium | §10 |
| accessibility | Нет axe/pa11y; a11y живёт в `docs/accessibility.md` + 76 `ui/*ContractTest` | §11 |
| API | 15 файлов на `MockMvc`, `@WebMvcTest` — 0 (все API-тесты через полный контекст) | §12 |

---

## 1. Build

Сборка — Gradle 8.9 (Kotlin DSL), три модуля с однонаправленной зависимостью
`quiz-domain ← quiz-persistence ← quiz-app`:

```bash
$ grep -c 'project(":' settings.gradle.kts
3
```

`settings.gradle.kts:13-15` объявляет `projectDir` в `modules/<name>`.
Корневой `build.gradle.kts` (46 строк) содержит только плагин `base`, общий
`subprojects`-блок (Java 17 source/target, UTF-8) и 4 alias-таска
(`compileJava`, `bootRun`, `bootJar`, `javadoc`) — совместимость со старыми
командами вида `./gradlew bootRun` из корня без указания `:quiz-app:`:

```bash
$ grep -n 'VERSION_17' build.gradle.kts
22:        sourceCompatibility = JavaVersion.VERSION_17
23:        targetCompatibility = JavaVersion.VERSION_17
```

`modules/quiz-app/build.gradle.kts` (176 строк) — плагины `java`, `jacoco`,
Spring Boot и `io.spring.dependency-management`; зависимости объявлены через
version-catalog alias'ы `libs.*` (`gradle/libs.versions.toml`). Есть
`springBoot { buildInfo { ... } }`, который кладёт `META-INF/build-info.properties`
для `/actuator/info` (§6). `tasks.withType<Test>` форсит
`spring.profiles.active=test` для всех тестовых JVM.

Jacoco-верификация — два блока правил в `jacocoTestCoverageVerification`:

```bash
$ grep -n 'toBigDecimal' modules/quiz-app/build.gradle.kts
123:                minimum = "0.85".toBigDecimal()
128:                minimum = "0.70".toBigDecimal()
153:                minimum = "0.55".toBigDecimal()
```

BUNDLE-правило — LINE ≥ 0.85, BRANCH ≥ 0.70 на весь модуль; PACKAGE-правило —
LINE ≥ 0.55 для пакетов `api.*`/`service.*`/`feature.*` (исключая `*.dto` и
`*.dto.*`). `tasks.check` зависит от `jacocoTestCoverageVerification`, т.е.
`./gradlew check` без явного покрытия не зеленеет. В комментарии
(`modules/quiz-app/build.gradle.kts:117`) зафиксирован снапшот замера
2026-08-04: `LINE 0.915 (3633/3970), BRANCH 0.764 (1366/1788)` — это
исторический комментарий, не гарантия текущего состояния (§15).
`processResources` подставляет `@projectVersion@` в `application.yml`.

Заметный мёртвый груз в зависимостях: `spring-ai-bom`/`spring-ai-openai`
(`modules/quiz-app/build.gradle.kts:43-44`, версии в
`gradle/libs.versions.toml:4,18,47`) и `spring-boot-starter-webflux`
(`modules/quiz-app/build.gradle.kts:14`) объявлены, но не используются:
`grep -rl "org.springframework.ai" modules --include='*.java'` — пусто;
`grep -rl "WebClient" modules --include='*.java'` даёт единственное
совпадение — Javadoc-комментарий в `config/package-info.java`, не код. Это
уже зафиксировано детально в `.sdlc/CS-20260827-002/report.md:418-451` (§15
бэкенд-отчёта) — здесь только инфраструктурный срез: наличие этих
зависимостей раздувает Docker-слой `RUN gradle :quiz-app:dependencies` (§2) и
итоговый jar без функциональной пользы.

## 2. Deployment

`Dockerfile` (68 строк) — двухстадийная сборка:

1. `gradle:8.9-jdk17 AS builder` — сначала копируются только
   build-скрипты + `gradle/` (кэш зависимостей: `RUN gradle
   :quiz-app:dependencies --no-daemon -q || true`), затем `modules/` и
   `bootJar` (`Dockerfile:10-19`). Порядок специально разводит инвалидацию
   Docker-слоёв: правка исходников не трогает слой зависимостей.
2. `eclipse-temurin:17-jre` — финальный образ: OCI `LABEL`
   (`org.opencontainers.image.title/version/created/revision/...`,
   `Dockerfile:31-38`) с подстановкой из `ARG VERSION/BUILD_DATE/VCS_REF`;
   непривилегированный `USER app` с явным UID/GID 10001
   (`Dockerfile:46`, K8s-совместимость `runAsUser`); `EXPOSE 8080`;
   `HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=5
   CMD curl -fsS http://localhost:8080/actuator/health/readiness`
   (`Dockerfile:66-67`); JVM cgroup-tuning через `JAVA_TOOL_OPTIONS`
   (`-XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError
   -XX:+HeapDumpOnOutOfMemoryError`, `Dockerfile:64`).

`docker-compose.yml` (111 строк) — 2 сервиса и volume:

- `postgres` (строка 5) — `postgres:16-alpine`, `no-new-privileges:true`,
  healthcheck `pg_isready` (5s/5s/5 retries), собственный
  `logging: driver: json-file, options: {max-size: 10m, max-file: "3"}`
  (`docker-compose.yml:26-29`) — единственное место в репо, где вообще
  сконфигурирован лог-драйвер (docker-уровень, не приложение — см. §5).
- `interview-prep` (строка 31) — билд из `Dockerfile`, пробрасывает build-args
  `VERSION/BUILD_DATE/VCS_REF`, `tmpfs: /tmp:size=128m,mode=1777`,
  `.env` прокидывается внутрь контейнера (не только для `${...}`-интерполяции
  compose).
- `volumes: postgres-data` (строка 109) — единственный volume, персистентность
  БД между рестартами.

Registry-push нигде не настроен: `docker-build` job в `ci.yml` собирает образ
только со smoke-целью (`push: false`, `.github/workflows/ci.yml:100`) и
только на `push` в `master` (`if: github.event_name == 'push' &&
github.ref == 'refs/heads/master'`, `ci.yml:81`) — на PR job вообще не
запускается. Внешнего деплой-механизма (Helm/K8s-манифесты, ArgoCD-конфиг,
Terraform) в репозитории нет — `find . -iname '*.yaml' -path '*k8s*' -o
-iname '*helm*'` пуст (не встречено при обходе `.github/`, корня, `docs/`).

## 3. Environment

`.env.example` (82 строки) — 13 активных (не закомментированных) переменных:

```bash
$ grep -c '^[A-Z_]*=' .env.example
13
```

Список: `APP_ADMIN_TOKEN`, `APP_CORS_ALLOWED_ORIGINS`,
`SENSITIVE_RATE_LIMIT_PER_MINUTE`, `APP_PORT`, `APP_VERSION`,
`POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_PORT`,
`INTERVIEW_RESET_ON_STARTUP`, `DB_POOL_MAX_SIZE`, `DB_POOL_MIN_IDLE`,
`APP_ENV`, `SERVER_FORWARD_HEADERS`.

Помимо активных, файл документирует 7 **закомментированных** (опциональных,
с дефолтом в `application.yml`) knob'ов, которые `grep -c '^[A-Z_]*='` не
считает, потому что они начинаются с `#`: `JAVA_TOOL_OPTIONS`,
`MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE`,
`MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS`, `DB_POOL_LEAK_DETECTION_MS`,
`STATIC_RESOURCES_CACHE_SECONDS`, `HEALTH_CACHE_SECONDS`,
`SHUTDOWN_TIMEOUT`. Их присутствие в файле (пусть и закомментированное) —
единственная точка документации этих env-knob'ов в репозитории: код читает
их напрямую через `${VAR:default}` в `application*.yml` (§4); отдельного
markdown-документа про переменные окружения в каталоге `docs/` нет (§15).

Переменных без значения по умолчанию (обязательных к заполнению) — одна:
`APP_ADMIN_TOKEN` (пустая в `.env.example`, `app.admin-token:
${APP_ADMIN_TOKEN}` без дефолта в `application-prod.yml:50`, приложение не
стартует в `prod` без неё).

## 4. Configuration

Пять файлов `application*.yml` на HEAD (черновик design.md этой задачи
предполагал 4 — `examples/application-oauth.yml` в нём не был учтён; это
пример-сниппет вне `src/`, ниже он выделен отдельно):

```bash
$ find modules -name 'application*.yml'
modules/quiz-app/examples/application-oauth.yml
modules/quiz-app/src/test/resources/application-test.yml
modules/quiz-app/src/main/resources/application-qa.yml
modules/quiz-app/src/main/resources/application-prod.yml
modules/quiz-app/src/main/resources/application.yml
```

- **`application.yml`** (236 строк, база/дефолт) — четыре секции верхнего
  уровня: `server:` (строка 1), `spring:` (36), `app:` (93), `management:`
  (187). Postgres — датастор по умолчанию, отдельного `postgres`-профиля нет
  (нечего активировать сверх базы). Полная опись `app.*`-свойств — в
  `.sdlc/CS-20260827-002/report.md` §16, здесь фиксируется только сам факт
  структуры и её файловый layout.
- **`application-prod.yml`** (56 строк) — `spring.thymeleaf.cache: true`;
  `hikari.leak-detection-threshold: 0` (в базовом профиле — 30s, в проде
  отключено — комментарий объясняет: ложные stack trace на длинных
  batch/import); content-hash версионирование статики
  (`spring.web.resources.chain.strategy.content.enabled: true`,
  `cache-control.max-age: ${STATIC_RESOURCES_CACHE_SECONDS:31536000}s`);
  `springdoc.swagger-ui.enabled: false` + `api-docs.enabled: false`;
  `app.admin-token: ${APP_ADMIN_TOKEN}` без дефолта — обязателен (§3);
  `management.metrics.tags.env: ${APP_ENV:prod}`.
- **`application-qa.yml`** (19 строк) — профиль, которого не было в
  черновике design.md: детерминированное окружение для воспроизводимого
  browser-QA (визуальная фаза, ссылка на `finalization-раунд` в
  комментарии). Фиксирует `Clock` через `app.qa.fixed-instant:
  "2026-07-15T12:00:00Z"` и детерминированные фикстуры due/learned
  (`due-fixtures: 2`, `learned-fixtures: 1`), отключает прогрев
  (`app.preload.startupPreload/fullWarmup: false`) ради быстрого старта.
  Активируется явно: `SPRING_PROFILES_ACTIVE=qa`.
- **`src/test/resources/application-test.yml`** — подхватывается тестовым
  профилем (`jdbc:tc:postgresql:16-alpine:///` из Testcontainers JDBC URL,
  форсится `tasks.withType<Test>` в build-файле, §1).
- **`examples/application-oauth.yml`** — не production-конфиг, а
  примерный сниппет для включения `app.security.mode=multi` +
  OAuth2 IdP (лежит вне `src/`, не подхватывается Spring автоматически;
  описан в README как копипаст-шаблон).

## 5. Logging

Отдельной инфраструктуры логирования (logback/log4j2-конфиг) в репозитории
**нет**:

```bash
$ find modules -name 'logback*' -o -name 'log4j*'
(пусто)
$ grep -rn '^logging:' modules/*/src/*/resources
(пусто)
```

Оба грепа дают пустой вывод — уровни, формат вывода и appenders целиком на
дефолтах Spring Boot (`CONSOLE_LOG_PATTERN` из `spring-boot-starter-logging`,
Logback под капотом, но без переопределений). Прямых пользователей
`org.slf4j.Logger`/`@Slf4j` в main-исходниках всех трёх модулей — 36 файлов
(`grep -rl 'org.slf4j.Logger\|LoggerFactory.getLogger\|@Slf4j' modules/*/src/main
--include='*.java' | wc -l`).

Нюанс, которого не было в черновике design.md: correlation-id **есть** на
уровне кода, но не долетает до текста лог-строки. `CorrelationIdFilter`
(`modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/web/CorrelationIdFilter.java`)
кладёт `X-Correlation-Id` (входящий, провалидированный allowlist'ом
`[A-Za-z0-9._-]{1,128}`, либо сгенерированный `UUID`) в `MDC` (ключ
`correlationId`), в атрибут запроса и в заголовок ответа; значение читает
`GlobalExceptionHandler` (`common/exception/GlobalExceptionHandler.java:242-300`)
и кладёт в `ProblemDetail`/модель ошибки. Но поскольку `logging.pattern.*`
нигде не переопределён (проверка выше), дефолтный `CONSOLE_LOG_PATTERN`
Spring Boot **не содержит** `%X{correlationId}` — MDC-значение доступно
программно (`MDC.get("correlationId")`), но не появляется в самих строках
консольного лога без ручной настройки `logging.pattern.console`. Это прямо
расходится с формулировкой в `docs/backend-architecture.md` — детали в §14.

Структурированного JSON-логирования (logstash-encoder и т.п.) нет —
`grep -rn 'logstash\|net.logstash' modules/quiz-app/build.gradle.kts` пуст.

## 6. Monitoring

`spring-boot-starter-actuator` + `runtimeOnly("io.micrometer:micrometer-registry-prometheus")`
(`modules/quiz-app/build.gradle.kts:18-20`). Экспозиция эндпоинтов —
`management.endpoints.web.exposure.include: health,info,metrics,prometheus`
(`application.yml:190`, комментарий рядом поясняет разделение
metrics/prometheus).

Health-конфигурация (`application.yml:194-222`):

- `probes.enabled: true` — включает `/actuator/health/liveness` и
  `/actuator/health/readiness`;
- `show-details: never`, `show-components: never` — по умолчанию наружу не
  течёт детализация (переключается через `MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS`, §3);
- `cache.time-to-live: ${HEALTH_CACHE_SECONDS:5}s` — агрегированный health
  кэшируется 5s, комментарий объясняет мотив: K8s liveness/readiness ~10s,
  docker-compose healthcheck 10s → примерно каждый второй scrape отдаётся из
  памяти без удара по БД и по SQL-запросу `seedCoverage`;
- `group.liveness.include: livenessState` (минимален: жив, если JVM не
  зависла) и `group.readiness.include: readinessState,db` — БД входит в
  readiness, а кастомный `SeedCoverageHealthIndicator`
  (`modules/quiz-app/src/main/java/com/cheatsheet/quiz/infrastructure/health/SeedCoverageHealthIndicator.java`)
  сознательно **не** входит: неполнота MCQ-сидеров — контентный вопрос, не
  вопрос готовности принимать трафик (extensive rationale в комментарии
  `application.yml:210-221`).

`management.metrics.tags: {application: interview-prep, env: ${APP_ENV:dev}}`
(`application.yml:224-232`) — единый тег на все Micrometer-метрики, чтобы
Prometheus-скрейп нескольких сервисов не давал коллизию по имени метрики.
`build.gradle.kts` подключает `springBoot { buildInfo {} }` → `/actuator/info`
отдаёт версию/время сборки (`management.info.build.enabled: true`,
`env.enabled/git.enabled: false`).

Комментарий в build-файле ссылается на «Grafana dashboard in release
package» (`modules/quiz-app/build.gradle.kts:19`) — сам дашборд в репозитории
не найден (`find . -iname '*dashboard*.json'` пусто), верифицировать
существование и актуальность можно только вне этого чтения (§15). Внешний
стек сбора метрик (сам Prometheus-сервер, Grafana-инстанс) в репо не
конфигурируется — это заведомо operator-side инфраструктура.

## 7. Тесты: unit

Суммарный объём тестового кода:

```bash
$ find modules/quiz-domain/src/test -name '*.java' | wc -l
21
$ find modules/quiz-persistence/src/test -name '*.java' | wc -l
20
$ find modules/quiz-app/src/test -name '*.java' | wc -l
194
$ grep -rho '@Test' modules --include='*.java' | wc -l
1298
```

Итого 235 test-файлов, 1298 `@Test`-аннотаций. `quiz-domain` — чистые
unit-тесты доменной модели (без Spring-контекста, модуль без Spring-зависимостей
вообще, §1 в бэкенд-отчёте). `quiz-persistence` — репозитории на
`AbstractPostgresRepositoryTest` (§8). `quiz-app` — 194 файла разложены по
11 пакетам и 9 файлам в корне пакета:

```bash
$ ls modules/quiz-app/src/test/java/com/cheatsheet/quiz/
api/ architecture/ bootstrap/ common/ config/ domain/ feature/
infrastructure/ persistence/ service/ ui/
ExportControllerErrorTest.java  ExportIntegrationTest.java
InterviewSessionTest.java  OptionCacheIntegrationTest.java
OptionCacheTest.java  SpacedRepetitionServiceTest.java
TestInterviewPath.java  TestQuestionBuilder.java  TestResources.java
```

`architecture/` — ArchUnit-правила (`LayeredArchitectureTest`, упомянут в
CLAUDE.md); `ui/` — 76 контрактных классов (§11); `bootstrap/` — тесты
стартового импорта (`StartupRunner`, TRUNCATE/переимпорт, см.
`app.interview-reset-on-startup`).

## 8. Тесты: integration

```bash
$ grep -rl '@SpringBootTest' modules --include='*.java' | wc -l
21
$ grep -rl 'Testcontainers\|@Container' modules --include='*.java' | wc -l
8
$ grep -rl 'AbstractPostgresRepositoryTest' modules --include='*.java' | wc -l
18
```

21 класс поднимает полный `@SpringBootTest`-контекст; 8 файлов используют
Testcontainers-аннотации напрямую (`@Testcontainers`/`@Container`) — помимо
них, интеграционные тесты `quiz-persistence` наследуют общий
`AbstractPostgresRepositoryTest` (`modules/quiz-persistence/src/test/java/com/
cheatsheet/quiz/persistence/AbstractPostgresRepositoryTest.java`) — один
контейнер PostgreSQL 16-alpine на JVM, `TRUNCATE` между тестами вместо
пересоздания контейнера (дешевле по времени). 18 совпадений включают сам
класс-базу — 17 фактических наследников (`QuestionRepositoryTest`,
`PausedSessionRepositoryTest`, `BookmarkRepositoryTest`,
`QuestionStatsRepositoryTest`, `UserExperienceRepositoryTest`,
`QuestionRevisionRepositoryTest`, `OwnerScopedQuestionIssueTest`,
`QuestionIssueRepositoryTest`, `DailyActivityRepositoryTest`,
`AttemptRepositoryTest`, `OwnerScopedXpActivityTest`, `HintRepositoryTest`,
`UserTopicStatsRepositoryTest`, `ReviewStateRepositoryTest`,
`OwnerScopedProgressTest`, `PostgresFullTextSearchRepositoryTest`,
`AnswerOptionRepositoryTest`).

`quiz-app`-тесты подхватывают тот же Testcontainers-движок через
`jdbc:tc:postgresql:16-alpine:///` в `application-test.yml` (§4), не через
явные `@Container`-поля — отсюда разница между 8 (явная аннотация) и 21
(`@SpringBootTest`, часть которых тоже бьёт по реальной БД неявно).

**Чего нет.** `@WebMvcTest` — 0 совпадений (`grep -rl '@WebMvcTest' modules
--include='*.java' | wc -l` → 0): слайсовых MVC-тестов контроллеров в
репозитории не заведено, все HTTP-тесты идут через полный
`@SpringBootTest`-контекст (медленнее, но исключает рассинхрон
security/interceptor-цепочки со слайсом).

## 9. Тесты: E2E

`e2e/` — Playwright, 6 спеков:

```bash
$ ls e2e/*.spec.js
e2e/chart-lazy.spec.js
e2e/console-clean.spec.js
e2e/font-network.spec.js
e2e/keyboard-quiz.spec.js
e2e/reflow.spec.js
e2e/visual-matrix.spec.js
```

`playwright.config.js` (49 строк): `testDir: '.'`, `timeout: 60_000`,
`expect.timeout: 15_000`; 2 проекта — `chromium` и `webkit`
(`devices['Desktop Chrome']`/`devices['Desktop Safari']`), оба гоняют полный
набор спеков, включая визуальную матрицу; `fullyParallel: false`,
`workers: 1`, `retries: process.env.CI ? 1 : 0` — намеренно последовательный
прогон (комментарий в конфиге: конфликт shared state приложения между
параллельными воркерами); `snapshotPathTemplate:
'{testDir}/visual-baselines/{projectName}/{arg}{ext}'` разводит голдены по
движку (§10); `webServer` условен — поднимается через `../gradlew
:quiz-app:bootRun -q` только при `E2E_START_SERVER=1`, иначе спеки ожидают
уже запущенное приложение на `E2E_BASE_URL` (дефолт `http://localhost:8080`).

Обёртки для локального запуска — `scripts/run-visual-e2e.sh`,
`scripts/run-keyboard-e2e.sh` (оба файла присутствуют,
`ls scripts/run-*-e2e.sh`).

**Критичный факт.** `.github/workflows/ci.yml` триггерится по `paths:`
(`ci.yml:6-12,14-20`): `modules/**`, `gradle/**`, `*.gradle.kts`, `gradlew`,
`gradlew.bat`, `.github/workflows/ci.yml`. Каталога `e2e/**` в этом списке
**нет** — значит правка любого из 6 спеков или самого
`playwright.config.js` не триггерит `ci.yml` вообще, а `ci.yml` в принципе не
содержит шага `npx playwright test`. Единственный workflow, который вообще
запускает Playwright — `visual-goldens.yml`, и только один спек из шести
(`visual-matrix.spec.js`) и только на `chromium` (§10, §13). Остальные пять
спеков (`chart-lazy`, `console-clean`, `font-network`, `keyboard-quiz`,
`reflow`) не выполняются автоматически **нигде**.

## 10. Тесты: visual

```bash
$ ls e2e/visual-baselines/chromium | wc -l
14
$ ls e2e/visual-baselines/webkit | wc -l
14
```

По 14 PNG-голденов на движок — матрица `{home, settings, stats, train}` ×
`{light, dark}` × вьюпорты `{375, 768, 1280}` (комбинаторно даёт больше 14,
т.е. не полный декартов перебор — конкретную развёртку задаёт сам
`visual-matrix.spec.js`, здесь фиксируется только счёт файлов на диске).
Сравнение — `toHaveScreenshot({ maxDiffPixelRatio: 0.03, animations:
'disabled' })` (`playwright.config.js:15-18`).

Автоматизация — `.github/workflows/visual-goldens.yml` (116 строк):
`workflow_dispatch` с булевым input `update_snapshots` (**не** запускается
ни на push, ни на pull_request — только вручную из UI Actions); поднимает
`postgres:16-alpine` как GitHub Actions `services:`; ставит Node 20 +
`npx playwright install --with-deps chromium` (только Chromium, WebKit в
этом workflow не устанавливается вообще); фоном стартует
`./gradlew :quiz-app:bootRun -q`, поллит `/actuator/health` до 90×2s;
запускает `visual-matrix.spec.js` (с `--update-snapshots`, если
`update_snapshots=true`); заливает `e2e/visual-baselines/`,
`visual-results/`, `test-results/` артефактом на 14 дней. Из этого следует:
WebKit-голдены (14 файлов) лежат в репо, но никогда не перепроверяются и не
перегенерируются автоматизацией — только вручную с локальной машины
разработчика (комментарий в файле: «Darwin baselines in repo may differ
slightly — use --update on this runner»).

## 11. Тесты: accessibility

Инструментальной a11y-автоматизации (axe-core, pa11y) в `e2e/` нет:

```bash
$ grep -n 'axe\|pa11y' e2e/package.json
(пусто)
```

Единственная `devDependency` — `@playwright/test`. Accessibility-покрытие
реализовано двумя другими путями:

1. **`docs/accessibility.md`** (193 строки, 8 разделов уровня `##`:
   Структура документа, Клавиатура, Динамический контент и живые области,
   Цвет и контраст, Движение, Графика, Чем это удерживается, Известные
   пробелы) — описательный документ, сам честно признаёт границы
   (`docs/accessibility.md:187-189`: «Ручная проверка живым скринридером
   (VoiceOver / NVDA) не проводилась»).
2. **`ui/*ContractTest`** (76 файлов, §7) — JVM-тесты, статически
   проверяющие шаблоны/DOM/CSS без браузера: примеры из
   `docs/accessibility.md:171-184` — `TemplateFragmentContractTest`
   (резолвятся ли `aria-labelledby`/`aria-controls` на существующий `id`),
   `…osResolvedAxesSubscribeToTheMediaQueryTheyRead` (подписка на смену
   `prefers-*`-осей через `matchMedia`), `PixelSpriteContractTest`
   (цвет спрайта строго из палитры `tokens.css`), `SummaryRoutingContractTest`
   (дублирующиеся CTA/ссылки на экране).

То есть a11y-«тестирование» в проекте — это компиляция статических
инвариантов шаблонов через JUnit, а не runtime-аудит браузерной страницы.
Отсутствие axe/pa11y — не пробел по недосмотру, а архитектурное решение,
явно повторяемое подходом «весь фронтенд проверяется contract-тестами по
исходникам шаблонов» (paralleли с §10: визуальные диффы — тоже
Playwright-screenshot, не отдельный a11y-движок).

## 12. Тесты: API

```bash
$ grep -rl 'MockMvc' modules --include='*.java' | wc -l
15
```

15 файлов используют `MockMvc` для HTTP-уровня. Поскольку `@WebMvcTest` не
применяется нигде (§8), все эти 15 файлов конструируют `MockMvc` поверх
полного `@SpringBootTest`-контекста (`MockMvcBuilders.webAppContextSetup`
или `@AutoConfigureMockMvc` внутри `@SpringBootTest`) — не изолированный
слайс контроллера. Специализированного REST-контрактного инструмента
(REST Assured, Pact) в зависимостях нет:

```bash
$ grep -n 'rest-assured\|pact\|RestAssured' modules/quiz-app/build.gradle.kts gradle/libs.versions.toml
(пусто)
```

Контроллеров в проекте 5 (`@Controller`/`@RestController`, без
`@ControllerAdvice`):

```bash
$ grep -rl '@Controller\b\|@RestController' modules/quiz-app/src/main --include='*.java'
config/web/CssBundleController.java
feature/auth/controller/LoginController.java
feature/interview/controller/InterviewApiController.java
feature/interview/controller/InterviewMvcController.java
feature/export/controller/ExportController.java
```

Детальная разбивка по HTTP-маппингам на класс — в
`.sdlc/CS-20260827-002/report.md` §2 (тот отчёт зафиксировал 37 маппингов
суммарно на замере от 2026-08-28 на HEAD `16f75d51`); здесь важен только
факт способа тестирования — MockMvc поверх полного контекста, без выделенных
слайсов и без внешнего контрактного тулинга.

## 13. CI/CD и локальные гейты

| Гейт | Триггер | Что проверяет | Где живёт |
|---|---|---|---|
| `build-test` (job в `ci.yml`) | `push`/`pull_request` с `paths` на `modules/**`, `gradle/**`, `*.gradle.kts`, `gradlew*`, сам `ci.yml` | `./gradlew build -x test`, затем `./gradlew check` (тесты + jacocoTestCoverageVerification + ArchUnit) | `.github/workflows/ci.yml:28-73` |
| `docker-build` (job в `ci.yml`) | только `push` в `master`, `needs: build-test` | smoke-сборка Docker-образа (`push: false`, только валидирует, что `Dockerfile` собирается) | `.github/workflows/ci.yml:75-110` |
| `visual` (job в `visual-goldens.yml`) | только `workflow_dispatch` вручную | `visual-matrix.spec.js` на chromium против живого `bootRun` + postgres-service | `.github/workflows/visual-goldens.yml` |
| `lint` (job в `cheatsheet-lint.yml`) | `push`/`pull_request` с `paths` на `cheatsheets/**`, `scripts/cheatsheet-lint.py` | `python3 scripts/cheatsheet-lint.py cheatsheets` | `.github/workflows/cheatsheet-lint.yml` (35 строк) |
| `cheatsheet-lint` (pre-commit) | локальный `pre-commit`, файлы `cheatsheets/*.md` | 13 правил + TOC-якоря | `.pre-commit-config.yaml:4-10` |
| `verify-mcq-json` (pre-commit) | локальный, `seed/mcq/**.json` | схема MCQ-сидеров | `.pre-commit-config.yaml:12-18` |
| `verify-md-no-mcq` (pre-commit) | локальный, `cheatsheets/interview/**.md` | отсутствие legacy `> [!mcq]`-блоков в .md | `.pre-commit-config.yaml:20-26` |
| `css-leak-audit` (pre-commit) | локальный, CSS/шаблоны/манифест ленты | синтаксис CSS + утечки page-scope стилей | `.pre-commit-config.yaml:35-41` |
| `design-token-audit` (pre-commit) | локальный, `static/css/*.css` | контраст токенов, полнота light/dark, хардкоды | `.pre-commit-config.yaml:43-49` |

Длины трёх workflow-файлов:

```bash
$ wc -l .github/workflows/*.yml
35 .github/workflows/cheatsheet-lint.yml
110 .github/workflows/ci.yml
116 .github/workflows/visual-goldens.yml
261 total
```

**Не автоматизировано в CI ни при каких условиях (push/PR):** все 6
Playwright-спеков (§9), 5 из них — и в ручном `visual-goldens.yml` тоже (там
только `visual-matrix.spec.js`); WebKit-прогон визуальной матрицы (§10);
любая форма a11y-сканирования (axe/pa11y и так нет, §11); реальный прогон
`docker run` контейнера (только `docker build`, без smoke-теста поднятия);
проверка `.pre-commit-config.yaml`-хуков в CI отдельным job'ом — они
полагаются на локальный `pre-commit install` у разработчика, в `ci.yml` нет
шага `pre-commit run --all-files`.

## 14. Дельта против документации

1. **`docs/backend-architecture.md:295,306`** утверждает, что `correlationId`
   «связывает ответ с логом» / «связь — через `correlationId`». Фактически
   (§5) ни в одном `application*.yml`, ни в отдельном logback-конфиге нет
   переопределения `logging.pattern.*`, а дефолтный `CONSOLE_LOG_PATTERN`
   Spring Boot не содержит `%X{correlationId}` — значение осядет в MDC и в
   HTTP-ответе, но не в тексте лог-строки без дополнительной настройки.
   Документ описывает целевое поведение, код обеспечивает его лишь частично.
2. **`CLAUDE.md:91`** ссылается на класс `PostgresIntegrationTest` как на
   пример интеграционного теста на Testcontainers. Такого класса в
   репозитории нет:
   ```bash
   $ find modules -iname 'PostgresIntegrationTest.java'
   (пусто)
   $ grep -rn 'PostgresIntegrationTest' modules --include='*.java'
   (пусто)
   ```
   Фактический базовый класс — `AbstractPostgresRepositoryTest`
   (`modules/quiz-persistence/src/test/java/com/cheatsheet/quiz/persistence/AbstractPostgresRepositoryTest.java`,
   18 упоминаний, §8) — по всей видимости, класс переименовали, а
   `CLAUDE.md` не обновили.
3. **`CLAUDE.md:86-92`** («Testing Conventions») перечисляет 5 пунктов
   (unit-тесты рядом с классами, `TestQuestionBuilder`, `TestInterviewPath`,
   Testcontainers, ArchUnit) и не упоминает ни 76 `ui/*ContractTest`
   (§11 — значимая часть тестовой базы `quiz-app`), ни `e2e/` Playwright-слой
   (§9-10), ни MockMvc/API-тесты (§12) вообще. Документ, специально
   адресованный AI-ассистентам работы с этим репозиторием, не описывает три
   из шести реально существующих слоёв тестирования.
4. **`README.md:619`** («## Сборка и тесты») документирует только
   Gradle-команды (`build`, `test`, `check`, `jacocoTestReport`, `javadoc`) и
   Testcontainers/OrbStack-грабли. Ни `.github/workflows/` (3 файла, §13),
   ни `e2e/` (6 Playwright-спеков, §9) в этом разделе (и вообще нигде в
   840-строчном README) не упомянуты:
   ```bash
   $ grep -n -i 'github actions\|workflow\|playwright\|e2e' README.md
   (пусто)
   ```
   Разработчик, читающий README целиком, не узнает о существовании CI или
   визуального регрессионного тестирования.

## 15. Белые пятна разведки

1. **Актуальное покрытие jacoco.** Комментарий в `build.gradle.kts:117`
   фиксирует замер от 2026-08-04 (LINE 0.915, BRANCH 0.764) — с тех пор
   в `modules/` были коммиты (см. риск «Дрейф чисел» в design.md).
   Закрывается: `./gradlew jacocoTestReport --no-daemon` на актуальном HEAD
   (не выполнено в этой разведке — загрязнило бы worktree `build/`-каталогами,
   AC-14 требует чистую дельту).
2. **Зелёность `./gradlew check` целиком.** Не запускался: неизвестно,
   проходят ли сейчас все 1298 `@Test` и оба jacoco-правила одновременно.
   Закрывается тем же прогоном, что и п.1, плюс просмотром
   `modules/*/build/test-results/`.
3. **Фактическое прохождение 6 Playwright-спеков.** Статически подтверждена
   только их конфигурация (§9-10), не прогон. Закрывается: `E2E_START_SERVER=1
   npx playwright test` из `e2e/` при поднятом Postgres — вне DoD этой задачи
   (```env
   docker compose up -d postgres
   cd e2e && npx playwright test
   ```), плюс `bash scripts/run-visual-e2e.sh` / `run-keyboard-e2e.sh`.
4. **Реальная доступность `/actuator/prometheus`.** Конфигурация
   (`management.endpoints.web.exposure.include`) включает `prometheus` в
   список (§6), но фактический ответ эндпоинта (формат exposition,
   присутствие ожидаемых метрик) не проверялся — приложение не
   поднималось. Закрывается: `curl -fsS
   http://localhost:8091/actuator/prometheus` на локально поднятом
   инстансе (порт 8091, не 8080 — правило проекта не занимать
   пользовательский bootRun).
5. **Существование внешнего Grafana-дашборда.** Комментарий
   `build.gradle.kts:19` («Grafana dashboard in release package») не
   резолвится ни в один файл этого репозитория (`find . -iname
   '*dashboard*.json'` пуст) — неясно, лежит ли он в отдельном
   release-артефакте вне git, устарел ли комментарий, или дашборд
   существует только в Grafana-инстансе оператора. Закрывается: спросить
   у пользователя/поискать в CI-артефактах релизов, либо в
   `grafana.detmir-infra.ru`, если этот проект туда когда-либо
   выгружался (маловероятно — не detmir-инфраструктура).
6. **Фактический вывод MDC `correlationId` в консоль в рантайме.**
   Статический анализ (§5, §14) показывает отсутствие `logging.pattern`,
   но не проверено эмпирически, что строка в консоли действительно не
   содержит id — теоретически дефолтный паттерн Spring Boot версии,
   зафиксированной в `libs.versions.toml`, мог измениться. Закрывается:
   поднять приложение, сделать запрос с `X-Correlation-Id`, посмотреть
   `docker compose logs -f interview-prep` на предмет наличия значения.
7. **Реальное поведение `docker-build` job при непройденном тесте.**
   `docker-build` зависит от `build-test` (`needs:`), но не проверялось,
   действительно ли GitHub Actions блокирует job при `push: false`-сборке,
   если `Dockerfile` начнёт падать независимо от тестов (например, при
   недоступности базового образа) — закрывается прогоном workflow на
   форке/ветке с намеренно сломанным `Dockerfile`.

---

**Что дальше.** Пятый блок §5 миссии (DOCUMENTATION) остаётся последним
незакрытым Phase 0 блоком — follow-up ниже. Сведение всех трёх Phase 0
отчётов (frontend/backend/infra+testing) в единый документ и добавление
Playwright в `ci.yml` как отдельного PR-гейта — тоже follow-ups, вытекающие
из находок §9 и §13.
