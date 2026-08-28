# CS-20260828-002: Phase 0 разведка инфраструктуры и тестирования

## Контекст

Задача — research: единственный артефакт `.sdlc/CS-20260828-002/report.md`,
DoD-гейт `test -s .sdlc/CS-20260828-002/report.md`, профиль `trivial`.
Код не меняем — дельта коммита обязана остаться внутри `.sdlc/CS-20260828-002/`.

Источник требования — миссия `cheat-sheet-product-rebuild.md`, §5 PHASE 0 —
REPOSITORY RECONNAISSANCE, блоки INFRASTRUCTURE и TESTING:

```
INFRASTRUCTURE: build; deployment; environment; configuration; logging;
monitoring.
TESTING: unit; integration; E2E; visual; accessibility; API.
```

Двенадцать рубрик. Смысл фазы — карта AS-IS перед перестройкой продукта, а не
приговор «что чинить». Задачи-предшественники: CS-20260827-001 (FRONTEND,
16 рубрик) и CS-20260827-002 (BACKEND, 14 рубрик) — их `report.md` лежат рядом
в `.sdlc/` и задают формат: сводная таблица «рубрика → факт → якорь», затем
секция на рубрику, в конце дельта против docs и белые пятна. Эта задача
закрывает третий блок §5; из пяти блоков останется DOCUMENTATION.

**Что измерено по репо на HEAD `b1818f96` (2026-08-28), из worktree:**

INFRASTRUCTURE:

- **Build.** Gradle Kotlin DSL, 3 модуля (`settings.gradle.kts:13`:
  `:quiz-domain`, `:quiz-persistence`, `:quiz-app` с `projectDir` в `modules/`).
  Корневой `build.gradle.kts` — только `base` + общий `subprojects`-блок
  (Java 17 source/target, UTF-8) + 4 alias-таска (`compileJava`, `bootRun`,
  `bootJar`, `javadoc`) для совместимости со старыми командами.
  `modules/quiz-app/build.gradle.kts` (6.6K) — плагины `java`, `jacoco`,
  spring-boot и dependency-management через version-catalog alias'ы
  (`libs.plugins.*`), т.е. в репо есть `gradle/libs.versions.toml`.
  Есть `springBoot { buildInfo { ... } }` → `META-INF/build-info.properties`
  для `/actuator/info`. `tasks.withType<Test>` форсит
  `spring.profiles.active=test`. Jacoco: `jacocoTestReport` (xml+html) и
  `jacocoTestCoverageVerification` с двумя правилами — BUNDLE
  LINE ≥ 0.85 / BRANCH ≥ 0.70 и PACKAGE LINE ≥ 0.55 для
  `api.*`/`service.*`/`feature.*` (исключая `*.dto`, `*.dto.*`);
  `tasks.check` зависит от verification. `processResources` подставляет
  `@projectVersion@` в `application.yml`. В комментариях зафиксирован замер
  2026-08-04: LINE 0.915 (3633/3970), BRANCH 0.764 (1366/1788).
  Заметный факт для дельты: в зависимостях всё ещё объявлены
  `spring-ai-bom`/`spring-ai-openai` и `spring-boot-starter-webflux` —
  бэкенд-отчёт §15 уже зафиксировал, что они не используются.
- **Deployment.** `Dockerfile` (4.3K): 2 стадии — `gradle:8.9-jdk17 AS builder`
  → `eclipse-temurin:17-jre`; OCI-`LABEL` (`org.opencontainers.image.*`,
  подставляются из ARG `VERSION`/`BUILD_DATE`/`VCS_REF`), непривилегированный
  `USER app`, `EXPOSE 8080`, `HEALTHCHECK --interval=30s --timeout=5s
  --start-period=40s --retries=5`, `ENTRYPOINT java -jar app.jar`.
  `docker-compose.yml` (5.7K): 2 сервиса — `postgres` (строка 5) и
  `interview-prep` (строка 31) + volume `postgres-data`. Registry-push нет
  нигде: CI собирает образ `push: false`.
- **Environment.** `.env.example` (4.1K) — 13 переменных: `APP_ADMIN_TOKEN`,
  `APP_CORS_ALLOWED_ORIGINS`, `SENSITIVE_RATE_LIMIT_PER_MINUTE`, `APP_PORT`,
  `APP_VERSION`, `POSTGRES_USER/PASSWORD/PORT`, `INTERVIEW_RESET_ON_STARTUP`,
  `DB_POOL_MAX_SIZE/MIN_IDLE`, `APP_ENV`, `SERVER_FORWARD_HEADERS`.
- **Configuration.** `modules/quiz-app/src/main/resources/`:
  `application.yml` (секции `server`/`spring`/`app`/`management` на строках
  1/36/93/187), `application-prod.yml`, `application-qa.yml`; тестовый
  `src/test/resources/application-test.yml`. Отдельного `postgres`-профиля
  нет — Postgres в base. prod: `thymeleaf.cache=true`,
  `hikari.leak-detection-threshold=0`, content-hash версионирование статики
  (`spring.web.resources.chain.strategy.content`) с `max-age`
  `${STATIC_RESOURCES_CACHE_SECONDS:31536000}`, springdoc выключен,
  `app.admin-token: ${APP_ADMIN_TOKEN}` без дефолта (обязателен).
- **Logging.** Отдельной конфигурации логирования в репо **нет**:
  `find modules -name 'logback*' -o -name 'log4j*'` → пусто, `grep -n logging`
  по всем четырём `application*.yml` → 0 совпадений. То есть уровни, формат
  и appenders — дефолты Spring Boot; структурированного JSON-логирования и
  correlation-id нет (проверить в отчёте на уровне кода: `@Slf4j`/`Logger`,
  фильтры, MDC).
- **Monitoring.** `spring-boot-starter-actuator` + runtime
  `micrometer-registry-prometheus`. `management.endpoints.web.exposure.include:
  health,info,metrics,prometheus`; health-пробы liveness/readiness включены,
  `show-details: never`, кэш health 5s (`${HEALTH_CACHE_SECONDS:5}`);
  группы: `liveness=livenessState`, `readiness=readinessState,db` —
  `seedCoverage` намеренно вне readiness (есть кастомный
  `SeedCoverageHealthIndicator`); `management.metrics.tags:
  application=interview-prep, env=${APP_ENV:dev}`. Внешнего стека (Prometheus,
  Grafana-дашборд) в самом репо надо проверить отдельно —
  комментарий в build-файле ссылается на «Grafana dashboard in release
  package».

TESTING:

- **Объём.** 235 test-файлов: `quiz-domain` 21, `quiz-persistence` 20,
  `quiz-app` 194; 1298 `@Test`-аннотаций суммарно.
- **Раскладка `quiz-app/src/test/java/com/cheatsheet/quiz/`:** `api/`,
  `architecture/`, `bootstrap/`, `common/`, `config/`, `domain/`, `feature/`,
  `infrastructure/`, `persistence/`, `service/`, `ui/` + 6 тестов и 3
  хелпера (`TestQuestionBuilder`, `TestInterviewPath`, `TestResources`) в корне
  пакета.
- **Integration.** 21 файл с `@SpringBootTest`, 8 с
  `Testcontainers`/`@Container`, 18 наследников
  `AbstractPostgresRepositoryTest`. `@WebMvcTest` — **0** (слайсовых тестов
  контроллеров нет, всё через полный контекст).
- **API.** 15 файлов используют `MockMvc`.
- **UI-контракты.** `quiz-app/src/test/java/com/cheatsheet/quiz/ui/` — **76**
  тест-классов вида `*ContractTest` (статические утверждения по шаблонам/DOM/
  CSS: `DomIdUniquenessContractTest`, `FocusHandoffContractTest`,
  `HotkeyRegionContractTest`, `VisualBaselineContractTest`, …). Это ключевая
  особенность: часть a11y/visual-покрытия реализована как JVM-тесты по
  исходникам шаблонов, а не браузерными прогонами.
- **E2E.** `e2e/` — Playwright, 6 спеков: `visual-matrix.spec.js` (11.6K),
  `keyboard-quiz.spec.js` (12.2K), `console-clean.spec.js`, `reflow.spec.js`,
  `font-network.spec.js`, `chart-lazy.spec.js`. `playwright.config.js`:
  2 проекта (chromium + webkit, оба гоняют визуальную матрицу),
  `fullyParallel:false`, `workers:1`, `retries: CI?1:0`,
  `toHaveScreenshot.maxDiffPixelRatio 0.03`, `animations:'disabled'`,
  `snapshotPathTemplate` разводит голдены по `{projectName}`, `webServer`
  поднимается только при `E2E_START_SERVER=1`. Обёртки —
  `scripts/run-visual-e2e.sh`, `scripts/run-keyboard-e2e.sh`.
- **Visual.** `e2e/visual-baselines/{chromium,webkit}/` — по 14 PNG-голденов
  (home/settings/stats/train × темы × вьюпорты 375/768/1280 × плотность).
- **Accessibility.** `axe`/`pa11y` в `e2e/package.json` **нет** (единственная
  devDependency — `@playwright/test`); a11y живёт в `docs/accessibility.md`
  (19.2K) и в `ui/*ContractTest` — это надо в отчёте назвать прямо.
- **CI.** `.github/workflows/`: `ci.yml` (110 строк; job `build-test`:
  JDK 17 Temurin, wrapper-validation, `./gradlew build -x test`, затем
  `./gradlew check` = тесты + jacoco + ArchUnit; job `docker-build` —
  только на push в master, smoke-сборка образа без push, gha-кэш);
  `visual-goldens.yml` (116 строк; `workflow_dispatch`, postgres 16-alpine как
  service, bootRun в фоне, визуальная матрица только на chromium);
  `cheatsheet-lint.yml` (35 строк; python-линт `cheatsheets/`).
  Заметный разрыв: Playwright-спеки **не** гоняются в `ci.yml` — ни на PR,
  ни на push.
- **Локальные гейты.** `.pre-commit-config.yaml` — 5 хуков: `cheatsheet-lint`,
  `verify-mcq-json`, `verify-md-no-mcq`, `css-leak-audit`,
  `design-token-audit`. Файл защищён от автономных правок хартией §6.

**Документация для дельты:** отдельного `docs/infrastructure.md`/
`docs/testing.md` нет. Смежные документы: `README.md` (45.8K, содержит
инструкции запуска/деплоя), `CLAUDE.md` (секции Build & Run, Testing
Conventions, Key Configuration), `docs/accessibility.md`,
`docs/backend-architecture.md` (48.5K), `docs/frontend-architecture.md`.

**Ограничение среды.** Порт 8080 может держать живой bootRun пользователя.
Прогон `./gradlew check` и Playwright не требуется для DoD и создаёт риск
конфликта с параллельными loop'ами — разведка статическая; всё, что не удалось
установить чтением, идёт в «белые пятна», а не в догадку.

## Решение

Написать `.sdlc/CS-20260828-002/report.md` — статическую карту AS-IS по
12 рубрикам §5 (INFRASTRUCTURE + TESTING) в формате отчётов CS-20260827-001/002.

Метод: только чтение репозитория (`git log/ls-files`, `find|wc`, `grep` по
конфигам и аннотациям, чтение build-файлов, workflow'ов, Dockerfile,
compose, `application*.yml`, `playwright.config.js`). Сборка и приложение
**не запускаются** — дельта коммита обязана остаться внутри `.sdlc/`, а
`build/`-каталоги в worktree загрязнили бы porcelain.

Структура `report.md` — фиксированный набор из 16 заголовков уровня `##`
(ровно эти строки, в этом порядке):

```
## 0. Сводка одним экраном
## 1. Build
## 2. Deployment
## 3. Environment
## 4. Configuration
## 5. Logging
## 6. Monitoring
## 7. Тесты: unit
## 8. Тесты: integration
## 9. Тесты: E2E
## 10. Тесты: visual
## 11. Тесты: accessibility
## 12. Тесты: API
## 13. CI/CD и локальные гейты
## 14. Дельта против документации
## 15. Белые пятна разведки
```

Шапка до `## 0` — SHA HEAD, дата, ветка, строка «Метод».

`## 0` — таблица `| Рубрика | Факт | Якорь |` с ровно 12 строками рубрик §5
(build, deployment, environment, configuration, logging, monitoring, unit,
integration, E2E, visual, accessibility, API); якорь — либо `§N`, либо
`path:line`, либо воспроизводящая команда.

Каждая рубричная секция (§1–§12) обязана содержать: (а) перечисление
артефактов с якорями `path` или `path:line`; (б) числа с командой-производителем
там, где число заявлено; (в) явную строку «чего нет» (отсутствующие
механизмы называем прямо: нет logback-конфига, нет `@WebMvcTest`, нет axe,
нет push в registry) — отсутствие в разведке такой же факт, как присутствие.

§13 сводит три workflow'а и pre-commit в одну таблицу «гейт → триггер → что
проверяет → где живёт» и фиксирует, какие проверки НЕ автоматизированы в CI.

§14 — расхождения между тем, что описано в `README.md`/`CLAUDE.md`/
`docs/accessibility.md`, и измеренным состоянием; каждое расхождение — с
якорями на обе стороны.

§15 — что осталось непроверенным и почему (в первую очередь всё, что требует
запуска: реальное покрытие jacoco на HEAD, зелёность `check`, прохождение
Playwright, фактическая доступность `/actuator/prometheus`).

Отвергнутые альтернативы:

- Прогнать `./gradlew check` и Playwright ради живых чисел покрытия — отвергнуто:
  загрязняет worktree `build/`, конфликтует с параллельными loop'ами, не нужно
  для DoD; неизмеренное честнее в §15.
- Объединить INFRASTRUCTURE и TESTING в 6 укрупнённых секций — отвергнуто:
  рубрики §5 миссии должны отображаться 1:1, иначе неочевидно, что блок закрыт.
- Добавить в отчёт раздел «что чинить» с приоритетами — отвергнуто: Phase 0 —
  карта, приоритизация живёт в §7–§17 миссии и в follow-ups.

## Acceptance Criteria

1. Файл `.sdlc/CS-20260828-002/report.md` существует и непуст —
   `test -s .sdlc/CS-20260828-002/report.md`.
2. Объём — не менее 300 строк: `wc -l < .sdlc/CS-20260828-002/report.md` ≥ 300.
3. Присутствуют ровно 16 заголовков `## `, дословно совпадающих со списком из
   «Решения» и в том же порядке:
   `grep -c '^## ' .sdlc/CS-20260828-002/report.md` → 16, и
   `grep '^## ' .sdlc/CS-20260828-002/report.md` даёт список из «Решения»
   строка в строку.
4. Все 12 рубрик §5 покрыты именной секцией: для каждого из
   `Build|Deployment|Environment|Configuration|Logging|Monitoring|unit|
   integration|E2E|visual|accessibility|API` есть свой `## `-заголовок (следует
   из п.3) и под ним ≥ 8 строк текста
   (`awk` по диапазонам между `## ` — ни одна секция §1–§12 не короче 8 строк).
5. Шапка содержит фактический SHA HEAD ветки: строка отчёта включает вывод
   `git rev-parse HEAD` (полный или ≥8 первых символов) —
   `grep -c "$(git rev-parse --short=8 HEAD)" report.md` ≥ 1.
6. Сводная таблица `## 0` содержит ровно 12 строк-рубрик (не считая заголовка
   и разделителя): подсчёт строк, начинающихся с `| ` внутри диапазона
   `## 0` … `## 1`, минус 2 → 12.
7. **Числовые утверждения подтверждаются командой.** Каждое из перечисленных
   ниже чисел присутствует в отчёте и совпадает с выводом своей команды
   (проверка ревьюером — прогон команды и сравнение):
   - 3 Gradle-модуля — `grep -c 'project(":' settings.gradle.kts`;
   - Java 17 — `grep -c 'VERSION_17' build.gradle.kts` ≥ 1;
   - пороги jacoco 0.85 / 0.70 / 0.55 — `grep -n 'toBigDecimal'
     modules/quiz-app/build.gradle.kts`;
   - 235 test-файлов и разбивка 21 / 20 / 194 —
     `find modules/<m>/src/test -name '*.java' | wc -l` по трём модулям;
   - 1298 `@Test` — `grep -rho '@Test' modules --include='*.java' | wc -l`;
   - 21 `@SpringBootTest`, 0 `@WebMvcTest`, 8 Testcontainers, 18 наследников
     `AbstractPostgresRepositoryTest`, 15 `MockMvc` —
     `grep -rl '<маркер>' modules --include='*.java' | wc -l`;
   - 76 `ui/*ContractTest` —
     `ls modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/*.java | wc -l`;
   - 6 Playwright-спеков — `ls e2e/*.spec.js | wc -l`;
   - 14 голденов на движок × 2 движка —
     `ls e2e/visual-baselines/chromium | wc -l` и `.../webkit`;
   - 13 переменных `.env.example` — `grep -c '^[A-Z_]*=' .env.example`;
   - 5 pre-commit хуков — `grep -c 'id:' .pre-commit-config.yaml`;
   - 3 workflow-файла и их длины —
     `wc -l .github/workflows/*.yml`.
   Числа, которых нет в этом списке, допускаются, но каждое обязано нести рядом
   свою команду-производитель в тексте отчёта.
8. **Утверждения об отсутствии подтверждаются командой.** Каждое из
   перечисленных «нет X» присутствует в отчёте вместе с командой, дающей
   пустой вывод / ноль: нет logback/log4j-конфига
   (`find modules -name 'logback*' -o -name 'log4j*'`); нет ключа `logging:`
   ни в одном `application*.yml` (`grep -rn '^logging:' modules/*/src/*/resources`);
   нет `@WebMvcTest`; нет axe/pa11y (`grep -n 'axe\|pa11y' e2e/package.json`);
   нет push образа в registry (`grep -n 'push:' .github/workflows/ci.yml` →
   только `push: false`); нет отдельного `postgres`-профиля
   (`ls modules/quiz-app/src/main/resources/application-*.yml`).
9. **Все якоря резолвятся.** Каждый упомянутый в отчёте путь существует, а для
   ссылок вида `path:line` — файл имеет не меньше `line` строк. Проверка: извлечь
   из отчёта токены `(modules|e2e|scripts|docs|\.github)/[^\s`,)]+` и для
   каждого `test -e`; для `path:line` дополнительно
   `[ "$(wc -l < path)" -ge line ]`. Ноль несуществующих.
10. Полные списки заявлены полными и таковы: перечень 6 Playwright-спеков
    совпадает с `ls e2e/*.spec.js`; перечень 5 pre-commit хуков — с
    `grep 'id:' .pre-commit-config.yaml`; перечень 3 workflow-файлов — с
    `ls .github/workflows/`; перечень 4 `application*.yml` — с `find modules
    -name 'application*.yml'`; перечень 11 тест-подпакетов `quiz-app` — с
    `ls modules/quiz-app/src/test/java/com/cheatsheet/quiz/`.
11. §14 содержит не менее 3 пунктов расхождений, каждый с якорем на документ
    (`README.md`/`CLAUDE.md`/`docs/*.md` + строка или заголовок) и якорем на
    код/конфиг.
12. §15 содержит не менее 5 белых пятен, каждое — с причиной, почему не
    проверено, и с указанием, какая команда/запуск это закрыл бы.
13. Отчёт на русском языке; блоки команд оформлены как fenced code.
14. Дельта коммита ограничена `.sdlc/CS-20260828-002/` —
    `git status --porcelain -uall` показывает изменения только под этим путём.

## Тест-контракт

не применимо (артефакт-документ)

## Риски

- **Дрейф чисел.** Параллельные loop'ы пользователя коммитят в `modules/` и
  `e2e/` во время работы. Числа отчёта верны на конкретный SHA — поэтому SHA
  обязателен в шапке (AC-5), а ревью сверяет команды на том же SHA. При
  расхождении из-за чужого коммита — пересчитать и обновить, а не спорить.
- **Соблазн запустить сборку.** `./gradlew check`/Playwright дали бы живые
  числа покрытия, но создадут `build/`-каталоги и сломают AC-14 (чистота
  дельты). Мера: разведка статическая, всё исполняемое — в §15.
- **Раздувание.** У бэкенд-отчёта 611 строк; здесь рубрик меньше (12 против
  14), но `ui/`-контракты и CI-детали тянут объём. Мера: секции §1–§12 —
  факты с якорями, без пересказа кода; всё «что чинить» — вне отчёта.
- **Пересечение с бэкенд-отчётом.** Actuator/health-индикаторы и конфигурация
  частично описаны в CS-20260827-002 §16. Мера: в §4/§6 давать ссылку на
  соседний отчёт и добавлять только инфраструктурный срез, не дублируя.
- **Ложное «нет».** Утверждение об отсутствии (логирования, axe, `@WebMvcTest`)
  легко ошибочно: механизм может жить под другим именем (например,
  структурированный лог через `spring.output.ansi`/`ConsoleAppender` в коде
  или a11y-проверки внутри `ui/*ContractTest`). Мера: AC-8 требует
  команду-подтверждение под каждое «нет», а не утверждение на глаз.
- **`.pre-commit-config.yaml` защищён** хартией §6 — только читаем, любые
  предложения по нему идут в follow-up, не в правку.

## Follow-ups

- Провести Phase 0 разведку документации (§5 DOCUMENTATION: README,
  architecture, product, domain, API docs) — отчёт `.sdlc/<ID>/report.md`
  по 5 рубрикам, с картой актуальности каждого документа против HEAD.
- Свести три отчёта Phase 0 (frontend CS-20260827-001, backend
  CS-20260827-002, infra+testing CS-20260828-002) в единый
  `docs/phase0-reconnaissance.md` с общей сводной таблицей и списком
  подтверждённых пробелов — вход для §6–§17 миссии.
- Добавить Playwright-прогон (`console-clean`, `reflow`, `keyboard-quiz`) в
  `.github/workflows/ci.yml` как отдельный job на PR: сейчас 6 e2e-спеков не
  выполняются ни в одном автоматическом гейте (проверка — job появился и
  зелёный на PR).
