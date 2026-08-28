# CS-20260828-010: Phase 0 разведка инфраструктуры и тестирования

## Контекст

Задача-артефакт: единственный результат — `.sdlc/CS-20260828-010/report.md`,
DoD-гейт `test -s .sdlc/CS-20260828-010/report.md`. Код не меняем.

Источник требования — миссия `cheat-sheet-product-rebuild.md`, §5 PHASE 0 —
REPOSITORY RECONNAISSANCE, строки 151–154. Дословно две группы рубрик:

- **INFRASTRUCTURE** (6): build; deployment; environment; configuration;
  logging; monitoring.
- **TESTING** (6): unit; integration; E2E; visual; accessibility; API.

Итого 12 рубрик — ровно то, что перечислено в заголовке задачи.

Предшественники в той же серии, формат ратифицирован ревью и воспроизводится
здесь: `.sdlc/CS-20260827-001/report.md` (фронтенд, 661 стр.) и
`.sdlc/CS-20260828-001/report.md` (бэкенд, 700 стр.). Правила формата: факт +
якорь `path:line`, инвентарь вместо критики, каждое число — с командой
пересчёта в приложении, привязка к SHA HEAD.

**Замер на HEAD `8e26bf3a` (2026-08-28, ветка `sdlc/CS-20260828-010`).**
Все числа ниже сняты командами; они же уходят в Приложение A отчёта.

**BUILD.** Gradle 8.9 (`gradle/wrapper/gradle-wrapper.properties:3`), Kotlin
DSL, version catalog `gradle/libs.versions.toml` (3.6K). Корневой
`build.gradle.kts` (43 строки) — `base` + `allprojects` repos (mavenCentral +
`repo.spring.io/milestone`) + `subprojects { java, Java 17, UTF-8 }` + 4
alias-задачи (`compileJava`, `bootRun`, `bootJar`, `javadoc`) для совместимости
со старыми командами. `settings.gradle.kts` включает 3 модуля с явным
`projectDir = modules/*`.
- `quiz-app/build.gradle.kts` — `spring.boot` + `spring.dependency.management`
  + `jacoco`; ~25 зависимостей (web, webflux, thymeleaf, jdbc, validation,
  actuator, security, oauth2-client, flyway, postgresql, jackson-jsr310,
  json-schema-validator, flexmark, jsoup, spring-ai-openai, caffeine, guava,
  commons-lang3, lombok, devtools, springdoc); `runtimeOnly
  micrometer-registry-prometheus` — единственная зависимость с версией из
  BOM, а не из каталога.
- Тест-таск форсит `spring.profiles.active=test` системным свойством
  (`modules/quiz-app/build.gradle.kts`, блок `tasks.withType<Test>`), поэтому
  Testcontainers включается даже без `@ActiveProfiles`.
- `springBoot { buildInfo { … } }` → `META-INF/build-info.properties` для
  `/actuator/info`. `processResources` подставляет `@projectVersion@` в
  `application.yml`.
- **Jacoco объявлен только в `quiz-app`.** Гейты: BUNDLE LINE ≥ 0.85 и BRANCH
  ≥ 0.70; PACKAGE LINE ≥ 0.55 для `api.*`/`service.*`/`feature.*` минус
  `*.dto*`. `tasks.check` зависит от `jacocoTestCoverageVerification`. В
  `quiz-domain` и `quiz-persistence` плагина jacoco нет — их покрытие не
  измеряется и не гейтится вовсе.

**CI.** `.github/workflows/` — 3 файла: `ci.yml` (110 стр., job `build-test`:
JDK 17 Temurin, `gradle/actions/setup-gradle@v4`, wrapper-validation,
`./gradlew build -x test`, затем `./gradlew check`; job `docker-build` —
smoke-сборка образа без push, только на push в master), `visual-goldens.yml`
(116 стр., `workflow_dispatch`, postgres-service, bootRun в фоне, Playwright
`visual-matrix.spec.js`, артефакт базлайнов), `cheatsheet-lint.yml` (35 стр.,
python-линт шпаргалок). `ci.yml` триггерится только по `modules/**`,
`gradle/**`, `*.gradle.kts`, `gradlew*`, самому `ci.yml` — правки
`cheatsheets/**`, `e2e/**`, `scripts/**`, `docker-compose.yml`, `Dockerfile`
CI-прогон тестов НЕ запускают.

**DEPLOYMENT.** `Dockerfile` (68 стр.): multi-stage `gradle:8.9-jdk17` →
`eclipse-temurin:17-jre`, отдельный слой зависимостей, UID/GID 10001, OCI-labels
из ARG (`VERSION`, `BUILD_DATE`, `VCS_REF`), `ENV SPRING_PROFILES_ACTIVE=prod`,
`JAVA_TOOL_OPTIONS` с `MaxRAMPercentage=75/G1GC/ExitOnOutOfMemoryError`,
`HEALTHCHECK` на `/actuator/health/readiness`, копирование `cheatsheets/` в
образ. `docker-compose.yml` (111 стр.): сервисы `postgres` (16-alpine,
healthcheck `pg_isready`, volume `postgres-data`) и `interview-prep`
(`env_file: .env required:false`, `tmpfs /tmp`, `no-new-privileges`,
`depends_on: service_healthy`, json-file логи с ротацией, bind-mount
`./cheatsheets`). Четыре документированных способа запуска —
`README.md:110/151/185/215`. Kubernetes-манифестов, Helm, Terraform в репо нет.

**ENVIRONMENT.** `.env.example` — 82 строки, 20 переменных, из них 13
раскомментированы. Профили Spring: базовый `application.yml` (236 стр.),
`application-prod.yml` (56), `application-qa.yml` (19),
`src/test/resources/application-test.yml` (15) и НЕ-classpath пример
`modules/quiz-app/examples/application-oauth.yml` (42). Порт `8080`
(`SERVER_PORT`), БД PostgreSQL 5432, тесты — Testcontainers
`jdbc:tc:postgresql:16-alpine:///interview`.

**CONFIGURATION.** `application.yml` содержит `server.*` (graceful shutdown,
cookie-only session, forward-headers, gzip), `spring.datasource` + Hikari
(pool, leak-detection, `reWriteBatchedInserts`), `spring.flyway`,
`spring.thymeleaf.cache`, `spring.web.resources.cache`, ~25 ключей `app.*` и
блок `management.*`. Наблюдение, требующее фиксации: `app.preload.*` и
`app.qa.*` объявлены ТОЛЬКО в `application-qa.yml:8-19` и в базовом yaml
отсутствуют.

**LOGGING.** Ни `logback.xml`/`logback-spring.xml`, ни ключа `logging.` ни в
одном yml — используется дефолт Spring Boot. В main-коде 32 файла с `@Slf4j`
и 4 с `LoggerFactory.getLogger`. Корреляция — `common/web/CorrelationIdFilter.java`
(единственное место с MDC). Ротация — только на уровне docker-драйвера
(`docker-compose.yml`, `max-size 10m`).

**MONITORING.** `management.endpoints.web.exposure.include: health,info,metrics,
prometheus`; `show-details: never`; кэш health 5s; группы `liveness=livenessState`
и `readiness=readinessState,db`. Кастомный индикатор один —
`infrastructure/health/SeedCoverageHealthIndicator.java` (в агрегатном health,
намеренно вне readiness). Micrometer используется в 4 main-файлах
(`config/runtime/CacheConfig.java`,
`feature/interview/controller/InterviewSessionSupport.java`,
`feature/interview/service/command/SubmitAnswerCommand.java`,
`service/imports/McqJsonLoader.java`). Общие теги `application=interview-prep`,
`env=${APP_ENV}`. Комментарий в `modules/quiz-app/build.gradle.kts` обещает
«Grafana dashboard in release package» — файла дашборда в репо нет.

**TESTING (счётчики).** 236 тестовых java-файлов, 1354 метода
(`@Test` + `@ParameterizedTest`):
- `quiz-domain` — 21 файл, 118 методов (94 + 24);
- `quiz-persistence` — 20 файлов, 120 методов, база — Testcontainers;
- `quiz-app` — 195 файлов, 1116 методов. Разбивка по пакетам:
  `ui/` 77 файлов/422, `service/` 47/259, `api/` 20/170, `feature/` 12/69,
  `infrastructure/` 5/66, `config/` 12/52, `common/` 7/46, `bootstrap/` 2/7,
  `persistence/` 2/3, `domain/` 1/1, `architecture/` 1/0 (ArchUnit-правила
  как поля, не `@Test`), плюс 9 файлов в корне пакета.
- Типы: `@SpringBootTest` — 21 файл, `@AutoConfigureMockMvc` — 12,
  `MockitoExtension` — 43, `@WebMvcTest` — 0, ArchUnit — 2 файла
  (`architecture/LayeredArchitectureTest.java`, `common/util/TopicUtilsTest.java`).
- Слой `ui/` — 77 «контрактных» тестов над Thymeleaf-шаблонами/CSS
  (`ShellChromeContractTest`, `HeadingScaleContractTest`,
  `VisualBaselineContractTest`, `PrintContractTest`, …); 101 файл в
  `quiz-app/src/test` содержит `aria-`/`role=`/`contrast`/`tabindex` —
  это фактический суррогат a11y-проверок, при этом ни axe-core, ни pa11y,
  ни отдельного a11y-спека в репо нет.
- E2E — `e2e/`: 6 spec-файлов (`visual-matrix`, `keyboard-quiz`,
  `console-clean`, `chart-lazy`, `font-network`, `reflow`),
  `playwright.config.js` с двумя проектами (chromium, webkit),
  `snapshotPathTemplate` → `e2e/visual-baselines/{projectName}/`, 28 файлов
  базлайнов, `webServer` только при `E2E_START_SERVER=1`. Playwright не
  подключён ни к `./gradlew check`, ни к `ci.yml`.
- Текстовые «визуальные» базлайны бэкенда: 5 файлов
  `modules/quiz-app/src/test/resources/visual-baseline/*.txt`.

**Гейты вне Gradle.** `.pre-commit-config.yaml` — 5 хуков (cheatsheet-lint,
verify-mcq-json, verify-md-no-mcq, css-leak-audit, design-token-audit).
В `scripts/` — 41 файл + каталоги `tests/`, `readability/`; из них
`healthcheck.sh` (smoke живого приложения), `run-visual-e2e.sh`,
`run-keyboard-e2e.sh`, `lint-scripts.sh`, `verify-mcq-json.sh`.

## Решение

Написать `.sdlc/CS-20260828-010/report.md` — статический инвентарь
инфраструктуры и тестирования на HEAD, покрывающий все 12 рубрик §5, по
структуре предшественников серии:

```
# Phase 0 — Infrastructure & Testing Reconnaissance (CS-20260828-010)
Шапка: SHA HEAD, дата, ветка, метод (статическое чтение; приложение
       не поднималось, gradle/playwright не запускались)
## 0. Сводка одним экраном   ← таблица: рубрика → 1 строка факта → главный якорь
## 1. Build                  ← Gradle 8.9, 3 модуля, version catalog, toolchain,
                                зависимости quiz-app, тест-таск, buildInfo,
                                processResources; jacoco-гейты и их асимметрия
                                (только quiz-app)
## 2. CI                     ← 3 workflow: триггеры, шаги, что гейтится, что нет;
                                path-фильтры ci.yml и их следствия
## 3. Deployment             ← Dockerfile (стадии, USER, labels, JAVA_TOOL_OPTIONS,
                                HEALTHCHECK), compose (2 сервиса, healthcheck,
                                volumes, env_file), 4 способа запуска из README,
                                отсутствие k8s/helm/tf
## 4. Environment            ← .env.example (20 переменных: имя, дефолт, кто читает),
                                матрица профилей default/prod/qa/test/oauth-пример,
                                порты, источники БД
## 5. Configuration          ← полный реестр app.* (свойство → дефолт → файл:line →
                                класс-потребитель), server.*/spring.*/management.*;
                                расхождения (app.preload/app.qa только в qa-профиле)
## 6. Logging                ← отсутствие logback/logging.*, @Slf4j (32) +
                                LoggerFactory (4), CorrelationIdFilter/MDC,
                                ротация на уровне docker-драйвера
## 7. Monitoring             ← actuator exposure, health-группы и probes,
                                SeedCoverageHealthIndicator, micrometer (4 места),
                                теги; дашборда в репо нет
## 8. Карта тестов           ← 236 файлов / 1354 метода, разбивка по модулям и
                                пакетам, раннеры, профиль test, Testcontainers
## 9. Unit                   ← MockitoExtension (43), domain/persistence-юниты,
                                ArchUnit как исполняемая граница слоёв
## 10. Integration           ← @SpringBootTest (21), AbstractPostgresRepositoryTest,
                                Flyway в тестах, bootstrap/startup-guard-тесты
## 11. API                   ← MockMvc (12), PublicEndpointsSmokeTest,
                                контрактные тесты сидеров, springdoc/OpenAPI
## 12. E2E                   ← 6 spec, 2 браузерных проекта, конфиг, скрипты
                                запуска, CI-подключение (и его отсутствие)
## 13. Visual                ← visual-matrix (матрица theme×pixel×motion),
                                28 базлайнов chromium/webkit, 5 txt-базлайнов
                                бэкенда, VisualBaselineContractTest
## 14. Accessibility         ← что реально проверяется (ui-контракты по aria/role/
                                контрасту/фокусу — перечислить конкретные тесты),
                                чего нет (axe/pa11y/Lighthouse в CI)
## 15. Гейты вне Gradle      ← pre-commit (5 хуков), scripts/ (41 файл),
                                healthcheck.sh, verify-*.sh — кто что ловит
## 16. Белые пятна разведки  ← что статикой не видно, с планом закрытия
## Приложение A. Команды воспроизведения ← все счётчики одной копипастой
```

Объём 350–700 строк, русский язык, таблицы и короткие абзацы.

Отвергнутые альтернативы (по одной строке):
- Запускать `./gradlew check` / Playwright для замера реального покрытия и
  времени прогона — создаёт `build/`, `e2e/node_modules`, `test-results/` в
  дереве и ломает дельту задачи; замер — предмет §16 и follow-up.
- Дублировать бэкенд-отчёт §17 «Тесты бэкенда» — вместо этого ссылка и
  углубление: там был счёт по модулям, здесь — типология и покрытие рубрик.
- Собирать граф `graphify` — пишет `graphify-out/` в дерево.
- Класть отчёт в `docs/` — вне разрешённой дельты и вне DoD-гейта.
- Смешать infrastructure и testing в один общий раздел — теряется
  прослеживаемость к 12 рубрикам §5.

## Acceptance Criteria

Каждый пункт — с проверкой, выполняемой из корня worktree.

1. `.sdlc/CS-20260828-010/report.md` существует и непуст.
   Проверка: `test -s .sdlc/CS-20260828-010/report.md` (DoD-гейт).
2. Присутствуют секции `## 0`…`## 16` и `## Приложение A`, ровно в этом
   порядке. Проверка: `grep -n '^## ' report.md` даёт 18 заголовков, номера
   строго возрастают от 0 до 16, последний — `## Приложение A`.
3. Покрыты все 12 рубрик §5 (build, deployment, environment, configuration,
   logging, monitoring; unit, integration, E2E, visual, accessibility, API) —
   каждая имеет свою секцию: §1→build, §3→deployment, §4→environment,
   §5→configuration, §6→logging, §7→monitoring, §9→unit, §10→integration,
   §11→API, §12→E2E, §13→visual, §14→accessibility. Проверка: 12 grep'ов по
   именам секций.
4. Шапка содержит SHA HEAD (`8e26bf3a` или актуальнее), дату и ветку.
   Проверка: `head -12 report.md`; SHA совпадает с `git rev-parse --short HEAD`
   на момент коммита.
5. Каждая секция §1–§15 содержит минимум один якорь `path:line` или команду
   подсчёта. Проверка: разбить файл по `## ` и убедиться, что в каждом блоке
   §1–§15 есть совпадение с
   `[A-Za-z0-9_./-]\+\.\(java\|kts\|yml\|yaml\|js\|sh\|py\|json\|xml\|properties\)`.
6. Полные списки, обязательные к перечислению (неполнота = провал приёмки):
   - §2 — все 3 workflow-файла `.github/workflows/*.yml`;
   - §4 — все 20 переменных `.env.example` и все 5 файлов `application*.yml`
     (включая `examples/application-oauth.yml`);
   - §7 — все 4 main-файла, использующих Micrometer;
   - §12 — все 6 spec-файлов `e2e/*.spec.js`;
   - §15 — все 5 хуков `.pre-commit-config.yaml`.
   Проверка: `grep -c` по каждому списку в отчёте против команд Приложения A
   (`ls … | wc -l`, `grep -rl … | wc -l`), числа совпадают.
7. Числа в отчёте подтверждены: для каждого счётчика (тестовые файлы и методы
   по модулям и пакетам, число workflow, переменных, миграций, базлайнов,
   скриптов, файлов с `@Slf4j`) в Приложении A есть команда, и её вывод на
   HEAD равен числу в тексте. Расхождение хотя бы в одном — провал.
8. Все пути, упомянутые в отчёте, существуют на HEAD. Проверка (команда
   включается в Приложение A): извлечь токены `modules/…`, `e2e/…`,
   `scripts/…`, `docs/…`, `.github/…`, `.sdlc/…`, прогнать `test -e` — ноль
   ненайденных.
9. Отчёт явно фиксирует минимум четыре расхождения «объявлено/обещано vs
   фактически есть», каждое с якорем:
   (а) jacoco-гейт покрывает только `quiz-app`, `quiz-domain`/`quiz-persistence`
   не измеряются; (б) path-фильтры `ci.yml` исключают `e2e/**`, `scripts/**`,
   `Dockerfile`, `docker-compose.yml` из гейта; (в) Playwright-набор не
   подключён ни к `check`, ни к `ci.yml` (только `workflow_dispatch`);
   (г) комментарий про «Grafana dashboard in release package» при отсутствии
   файла дашборда в репо. Для каждого — команда-доказательство.
10. §14 честно разделяет «что проверяется» и «чего нет»: перечислены
    конкретные ui-контрактные тесты, реально смотрящие на aria/role/фокус/
    контраст (не менее 5 имён файлов с якорями), и зафиксировано отсутствие
    axe-core/pa11y/Lighthouse-гейта командой-доказательством
    (`grep -ri 'axe-core\|pa11y\|lighthouse' e2e package*.json .github` → пусто).
11. §16 перечисляет не менее четырёх конкретных белых пятен, у каждого —
    способ закрытия (какой запуск/замер и что он покажет).
12. Отчёт на русском; объём 350–700 строк. Проверка:
    `wc -l .sdlc/CS-20260828-010/report.md`.
13. Дельта задачи ограничена `.sdlc/CS-20260828-010/**`. Проверка:
    `git status --porcelain -uall` не показывает путей вне этого каталога.

## Тест-контракт

не применимо (артефакт-документ)

## Риски

- **Дрейф репо.** Параллельные loop'ы пользователя коммитят в `modules/` и
  `e2e/`; числа устареют. Митигация — SHA в шапке, все счётчики снимаются
  одним проходом непосредственно перед записью отчёта.
- **Соблазн запустить сборку.** «Проверить покрытие честно» = `./gradlew check`
  → `build/`, `.gradle/` в дереве, дельта вне `.sdlc/` и парковка задачи.
  Митигация — метод строго статический, замеры уходят в §16 и follow-up.
- **Сползание в аудит.** «CI не гоняет e2e — это плохо» — оценка. Митигация:
  формулировать как факт + якорь + следствие («правка `e2e/**` не запускает
  ни один workflow»), без предписаний.
- **Дубль бэкенд-отчёта.** §17 предшественника уже считал тесты по модулям.
  Митигация — ссылка вместо копии, здесь типология и покрытие 6 TESTING-рубрик.
- **Неполные списки.** 20 env-переменных и 6 spec-файлов легко «сократить для
  краткости» — ломает AC 6. Митигация — списки генерируются командой.
- **Объём.** 12 рубрик × детализация дают >1000 строк. Митигация — потолок
  700 строк, §0 «сводка одним экраном», полные реестры в таблицах.
- **Ложная точность в §14.** Контрактные ui-тесты — не a11y-аудит; выдать их
  за покрытие доступности = дезинформировать программу. Митигация — AC 10
  требует явного разделения «есть/нет».

## Follow-ups

- Закрыть «белые пятна» §16 infra/testing-разведки живым замером (`./gradlew check` и Playwright-прогон в изолированном worktree): фактическое покрытие jacoco по пакетам, время прогона, список падающих/flaky тестов — отчёт `.sdlc/<ID>/report.md`.
- Провести Phase 0 разведку документации (§5 DOCUMENTATION: README, архитектура, продуктовая и доменная документация, API-документация) и зафиксировать расхождения документов с кодом на HEAD — отчёт `.sdlc/<ID>/report.md`.
- Подключить e2e/визуальный набор к автоматическому гейту (расширить path-фильтры `ci.yml` на `e2e/**`, `Dockerfile`, `docker-compose.yml` и добавить job запуска Playwright) — код + зелёный прогон workflow.
