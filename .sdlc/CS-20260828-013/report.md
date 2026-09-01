# Phase 0 — Documentation Reconnaissance (CS-20260828-013)

**SHA HEAD:** `bf1ff278`
**Дата:** 2026-09-01
**Ветка:** `sdlc/CS-20260828-013`
**Метод:** статическое чтение репозитория (`find`, `wc`, `grep`, `git log`) на
зафиксированном SHA. Ничего не запускалось: ни `./gradlew`, ни Docker, ни
живое приложение — снятие `/v3/api-docs` вживую нарушило бы статический метод
разведки и ограничение дельты (`.sdlc/CS-20260828-013/**`), вынесено в
follow-up. Конфигурация и метрики как таковые уже разобраны предшественником
(`.sdlc/CS-20260828-004/report.md` §3–§6) — здесь не дублируются, только угол
«что о них написано и совпадает ли с кодом».

## 0. Сводка одним экраном

| № | Рубрика | Факт | Главный якорь |
|---|---|---|---|
| 1 | Инвентарь | 17 корневых `.md`, 851 файл в `docs/` (100 `.md`/745 `.json`/5 `.tsv`/1 `.csv`) в 17 подкаталогах | `docs/css-decisions/` (36 `.md`, крупнейший) |
| 2 | README | 840 строк, 53 реальных заголовка (1×h1+24×h2+28×h3); список REST-эндпоинтов и «Структура проекта» неполны | `README.md:359-392` |
| 3 | Архитектура | 4 md-документа (193–1234 строк) + `CLAUDE.md` + 12 `package-info.java`; один `@see` ссылается на несуществующий класс | `docs/backend-architecture.md:1-541` |
| 4 | Продукт | `PRODUCT.md` (80, 2026-06-08) устарел рядом с `MASTER_PLAN_COMPLETE_PRODUCT_REDESIGN.md` (3301, 2026-08-04) и живым `docs/frontend-redesign-state.md` (9821, 2026-08-28) | `docs/frontend-redesign-state.md:1` |
| 5 | Домен | 1165 `.md` в `cheatsheets/` (321 в `interview/`), 318 JSON-сидеров `seed/mcq/**`, 3 доменных доки (2097 строк) | `docs/mcq-quality-standard.md:1` |
| 6 | API | springdoc 2.6.0 подключён, `OpenApiConfig` задаёт только title/version/description/contact/license, аннотаций `@Operation|@ApiResponse|@Tag|@Schema` в main — 0; в `prod` спека выключена | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/OpenApiConfig.java:34-51` |
| 7 | Свежесть | Диапазон правок 2026-04-10 (`WIKI.md`, корень) …2026-08-28 (`docs/*`); корневые `.md` сами по себе уже — 2026-04-10…2026-08-11 | Приложение A |
| 8 | Дельта doc↔код | 9/9 расхождений подтверждены командами: 2 битые ссылки на удалённый код, 1 неисполнимая инструкция (`graphify-out/`), 2 неполных перечня маршрутов, 1 вводящее в заблуждение «полный контракт», 1 чужеродный файл, 1 отсутствующий индекс | §8 ниже |
| 9 | Связность | 12 локальных relative-ссылок в 7 «системных» доках, битых 0; 14 из 16 проверенных корневых документов — орфаны (нет входящих ссылок) | Приложение A |
| 10 | Белые пятна | 3: живая OpenAPI-спека, содержимое 81 файла во вложенных `docs/**`, актуальность 100 `.md` `docs/` относительно кода | §10 ниже |

---

## 1. Инвентарь документации

**Корень репозитория — 17 `.md`** (`find . -maxdepth 1 -name '*.md' | wc -l`):

| Файл | Строк |
|---|---|
| `CLAUDE.md` | 181 |
| `DESIGN.md` | 319 |
| `FINAL_UI_UX_REVIEW_AND_AUTONOMOUS_PLAN.md` | 1405 |
| `FRONTEND_BACKLOG.md` | 92 |
| `FRONTEND_DECISIONS.md` | 148 |
| `FRONTEND_EVIDENCE.md` | 880 |
| `FRONTEND_HISTORY.md` | 3265 |
| `INTERVIEW-MASTER-PLAN.md` | 247 |
| `MASTER_PLAN_COMPLETE_PRODUCT_REDESIGN.md` | 3301 |
| `PLAN_FRONTEND.md` | 120 |
| `PLAN_INTERVIEW.md` | 376 |
| `PRODUCT.md` | 80 |
| `PROMPT_PLAN_FRONTEND.md` | 910 |
| `PROMPT_PLAN_INTERVIEW.md` | 1800 |
| `README.md` | 840 |
| `WIKI.md` | 850 |
| `roadmap-checklist.md` | 1416 |

**`docs/` — 851 файл** (`find docs -type f | wc -l`): 100 `.md`, 745 `.json`
(в основном `docs/mcq-quality/reviews` и архивы), 5 `.tsv`, 1 `.csv`.

19 `.md` верхнего уровня `docs/`:

| Файл | Строк |
|---|---|
| `docs/METRICS_GLOSSARY.md` | 120 |
| `docs/UI_UX_GRAPHICS_BEST_PRACTICES.md` | 1405 |
| `docs/accessibility.md` | 193 |
| `docs/backend-architecture.md` | 541 |
| `docs/design-system.md` | 1234 |
| `docs/frontend-architecture.md` | 737 |
| `docs/frontend-redesign-loop-brief.md` | 2124 |
| `docs/frontend-redesign-state.md` | 9821 |
| `docs/frontend-refoundation-audit.md` | 511 |
| `docs/frontend-refoundation-changelog.md` | 596 |
| `docs/golden-examples.md` | 1110 |
| `docs/mcq-llm-batch-automation.md` | 137 |
| `docs/mcq-quality-improvement-plan.md` | 326 |
| `docs/mcq-quality-standard.md` | 740 |
| `docs/mcq-refactoring-state.md` | 406 |
| `docs/mcq-structure-parity-plan.md` | 331 |
| `docs/product-redesign-state.md` | 484 |
| `docs/quality-report.md` | 808 |
| `docs/visual-assets.md` | 372 |

17 подкаталогов `docs/**` (рекурсивно, `find docs -mindepth 1 -type d`),
число `.md` — только собственные файлы каталога (без вложенных):

| Каталог | `.md` |
|---|---|
| `docs/css-decisions` | 36 |
| `docs/design` | 2 |
| `docs/design/systems` | 10 |
| `docs/learning-plan` | 1 |
| `docs/learning-plan/diagnostic` | 5 |
| `docs/mcq-quality` | 6 |
| `docs/mcq-quality/archive` | 0 |
| `docs/mcq-quality/archive/round-8` | 4 |
| `docs/mcq-quality/archive/round-8/fact-freshness` | 0 |
| `docs/mcq-quality/archive/round-8/reviews` | 1 |
| `docs/mcq-quality/fact-freshness` | 0 |
| `docs/mcq-quality/reviews` | 1 |
| `docs/superpowers` | 0 |
| `docs/superpowers/plans` | 3 |
| `docs/superpowers/specs` | 8 |
| `docs/ui-critique` | 3 |
| `docs/ux-decisions` | 1 |

Сумма подкаталогов (81) + верхний уровень `docs/` (19) = 100, сходится с
общим счётчиком `.md` выше.

Отдельные README вне `docs/`: `prompts/README.md` (24 строки, роль папки
`prompts/` — «внешний контур для ChatGPT/LLM в ручном режиме»),
`cheatsheets/CHEATSHEETS_ARCHITECTURE_AND_RULES.md`.

## 2. README

`README.md` — 840 строк, 53 реальных заголовка (1 `#` + 24 `##` + 28 `###`;
наивный подсчёт `grep -cE '^#'` даёт 91 из-за строк вида `# 1. Собрать fat-JAR`
внутри bash-блоков — это комментарии кода, не markdown-заголовки).

Покрывает: TL;DR, 5 способов запуска (Gradle/JAR/Docker/Compose/PostgreSQL без
Docker), источник MCQ-вариантов, переменные окружения, профили Spring, доступ
к приложению, «как пользоваться» (8 подпунктов), health/наблюдаемость,
управление данными, сборку/тесты, структуру проекта, troubleshooting
(10 сценариев, `README.md:703-795`), безопасность, лицензию.

**Список REST-эндпоинтов неполон** (`README.md:359-392`, секция «Основные
REST-эндпоинты»): перечисляет 10 MVC POST-маршрутов (6 буллетов) и
12 REST-маршрутов (8 буллетов), но не упоминает ни одного `GET`-маршрута,
отдающего HTML напрямую (`/`, `/training`,
`/review`, `/settings`, `/train`, `/stats`, `/topics`, `/bookmarks`,
`/session-summary`, `/login`), ни `POST /bookmarks/note`,
`POST /question/note`, `POST /bookmarks/remove`, `POST /pause/discard`, ни
CSS-бандл-маршрут. Разбор полного списка — §6, §8.

**«Структура проекта» неполна** (`README.md:664-686`): в дереве каталогов нет
`e2e/`, `prompts/`, `design/`, `.design/` — все четыре существуют в
репозитории верхним уровнем (`find . -maxdepth 1 -mindepth 1 -type d`).

Раздел «Дополнительная документация» (`README.md:796-807`) — единственный
индекс: 9 ссылок на `CLAUDE.md` + 7 файлов `docs/*.md` + `DESIGN.md`, из
100 `.md` внутри `docs/` — ссылка есть только на 7.

## 3. Архитектурная документация

- `docs/backend-architecture.md` — 541 строка, 11 заголовков `## `
  (`## 1`…`## 9`, плюс `## 3a`/`## 3b`), правка 2026-08-06: модули, пакеты
  `quiz-app`, планирование повторений (SM-2/FSRS), обработка ошибок,
  безопасность, источник вопросов, HTTP-поверхность
  (`docs/backend-architecture.md:499-511`), идемпотентность, тесты.
- `docs/frontend-architecture.md` — 737 строк, правка 2026-08-12.
- `docs/design-system.md` — 1234 строки, правка 2026-08-12: токены,
  `@layer`-каскад, компоненты.
- `docs/accessibility.md` — 193 строки, правка 2026-08-12.
- `CLAUDE.md` — 181 строка, правка 2026-08-11: build/run, правила слоёв,
  ключевая конфигурация, раздел про `graphify-out/` (`CLAUDE.md:102-166`).
- 12 файлов `package-info.java` (`find modules -name package-info.java`):

| Модуль | Файл | Пакет |
|---|---|---|
| `quiz-app` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/controller/package-info.java` | `com.cheatsheet.quiz.api.controller` |
| `quiz-app` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/dto/package-info.java` | `com.cheatsheet.quiz.api.dto` |
| `quiz-app` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/package-info.java` | `com.cheatsheet.quiz.api` |
| `quiz-app` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/common/util/package-info.java` | `com.cheatsheet.quiz.common.util` |
| `quiz-app` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/package-info.java` | `com.cheatsheet.quiz.config` |
| `quiz-app` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/package-info.java` | `com.cheatsheet.quiz` |
| `quiz-app` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/cache/package-info.java` | `com.cheatsheet.quiz.service.cache` |
| `quiz-app` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/package-info.java` | `com.cheatsheet.quiz.service.imports` |
| `quiz-app` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/package-info.java` | `com.cheatsheet.quiz.service` |
| `quiz-domain` | `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/exception/package-info.java` | `com.cheatsheet.quiz.domain.exception` |
| `quiz-domain` | `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/package-info.java` | `com.cheatsheet.quiz.domain` |
| `quiz-persistence` | `modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/package-info.java` | `com.cheatsheet.quiz.persistence` |

Один из двенадцати (`api/controller/package-info.java:12`) ссылается через
`@see` на класс `com.cheatsheet.quiz.feature.admin.controller.AdminController`,
которого в репозитории нет — разбор в §8.

Конфигурация и метрики — уже разобраны `.sdlc/CS-20260828-004/report.md`
§3–§6, здесь не дублируются.

## 4. Продуктовая документация

Слоями по свежести и авторитету:

- **Устаревший якорь.** `PRODUCT.md` (80 строк, правка 2026-06-08) — самый
  старый продуктовый документ в наборе; не обновлялся с момента появления
  master-плана редизайна.
- **Историческая дизайн-дока.** `DESIGN.md` (319, 2026-06-25) — ссылается на
  `docs/design/DESIGN.md` и `docs/design/systems/` (`DESIGN.md:5-6`), оба
  существуют.
- **Master-план.** `MASTER_PLAN_COMPLETE_PRODUCT_REDESIGN.md` (3301 строка,
  2026-08-04) — крупнейший корневой документ.
- **Живой state-файл.** `docs/frontend-redesign-state.md` (9821 строка,
  2026-08-28 — правился в день, когда писался design.md этой задачи) —
  крупнейший `.md` во всём репозитории.
- Сопутствующие: `docs/frontend-redesign-loop-brief.md` (2124),
  `docs/product-redesign-state.md` (484), `docs/ui-critique/` (3 `.md`),
  `docs/ux-decisions/` (1 `.md`), четыре корневых `FRONTEND_*.md`
  (`_BACKLOG`/`_DECISIONS`/`_EVIDENCE`/`_HISTORY`, все правлены 2026-07-15/16;
  `FRONTEND_STATE.json` рядом — не `.md`), `PLAN_FRONTEND.md`,
  `PROMPT_PLAN_FRONTEND.md`.

Три поколения продуктовой документации (PRODUCT.md → MASTER_PLAN → живой
frontend-redesign-state.md) сосуществуют без взаимных пометок «устарело» —
детали в §7 (свежесть) и §10 (белые пятна).

## 5. Доменная документация

- `cheatsheets/**` — 1165 `.md` (`find cheatsheets -name '*.md' | wc -l`), из
  них `cheatsheets/interview/` — 321.
- `seed/mcq/**/*.json` — 318 сидеров
  (`find modules/quiz-app/src/main/resources/seed/mcq -name '*.json' | wc -l`).
- Три доменных документа общим объёмом 2097 строк: `docs/mcq-quality-standard.md`
  (740), `docs/golden-examples.md` (1110), `INTERVIEW-MASTER-PLAN.md` (247).
- `docs/mcq-quality/` — 6 собственных `.md` + `ROUND-10-LINKAGE-FIRST.md`,
  `surface-parity-rules.md`, `golden-linkage-examples.md`, архив `round-8/`
  (4 `.md`).
- `prompts/` — `README.md` (24 строки, роль папки), `PROMPTS_RULES.md`,
  `index.yaml` (72 строки), 8 шаблонов в `prompts/markdown/` и `prompts/text/`
  (по 4: `combine`/`long`/`screen`/`short`, `.md` и `.txt`-варианты).

`prompts/README.md:7` документирует `prompts/` как «внешний контур для
ChatGPT/LLM в ручном режиме», а строка 9 указывает на
`modules/quiz-app/.../service/ai/AiPrompts.java` как «runtime-промпты самого
приложения» — файла нет, разбор в §8.

## 6. API-документация

springdoc-openapi-starter-webmvc-ui зафиксирован версией 2.6.0
(`gradle/libs.versions.toml:10`, подключение —
`modules/quiz-app/build.gradle.kts:62`). `OpenApiConfig`
(`modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/OpenApiConfig.java`,
51 строка) конфигурирует ровно один `@Bean OpenAPI customOpenAPI()` (:34-51),
задающий только `servers`/`title`/`version`/`description`/`contact`/`license`
— ни одного `@Operation`, `@ApiResponse`, `@Tag` или `@Schema` в вызовах нет.
Поиск по всему `main`-дереву подтверждает нуль: `@Operation|@ApiResponse|
@Tag|@Schema` не встречается ни в одном файле `modules/**/src/main`.

В `prod`-профиле спека и UI явно выключены:
`modules/quiz-app/src/main/resources/application-prod.yml:42-46`
(`springdoc.swagger-ui.enabled: false`, `springdoc.api-docs.enabled: false`,
с комментарием «чтобы не светить API-карту наружу»).

**Полная HTTP-поверхность — 37 handler-методов в 5 контроллерах** (main-дерево,
`@Get|Post|Put|Delete|PatchMapping`):

| Контроллер | Методов |
|---|---|
| `feature/interview/controller/InterviewMvcController.java` | 23 |
| `feature/interview/controller/InterviewApiController.java` | 11 |
| `config/web/CssBundleController.java` | 1 |
| `feature/auth/controller/LoginController.java` | 1 |
| `feature/export/controller/ExportController.java` | 1 |

(Дизайн этой задачи фиксировал 21 метод в `InterviewMvcController` — занижение:
маршруты `/pause/discard` (REF-016, `b56fc164`) и `/question/note` (REF-024,
`87f3433f`) уже входили в HEAD `af3f2db6`, на котором писался design.md; файл
не менялся ни на одном коммите этой ветки
(`git diff af3f2db6 -- .../InterviewMvcController.java` — пусто). Далее в
отчёте — актуальные числа.)

Маршруты `InterviewMvcController`, отсутствующие в README §«Основные
REST-эндпоинты» (список шире — раздел вообще не описывает MVC GET-страницы):
`GET /`, `/training`, `/review`, `/settings`, `/train`, `/stats`, `/topics`,
`/bookmarks`, `/session-summary`; `POST /bookmarks/note`, `/question/note`,
`/bookmarks/remove`, `/pause/discard` — 13 маршрутов.

Маршруты, отсутствующие в `docs/backend-architecture.md:499-511` §7 (список
короче: MVC GET-страницы там перечислены, `POST /bookmarks/*` покрывает
`/bookmarks/note` и `/bookmarks/remove` как маска) — только: `GET /train`,
`GET /topics`, `POST /question/note`, `POST /pause/discard` и CSS-бандл
(`CssBundleController.java:34`, шаблон `{page}.{hash}.css`, не описан ни в
README, ни в `docs/backend-architecture.md`) — 5 маршрутов.

`GET /login` (`LoginController.java:35`) описан в
`docs/backend-architecture.md:499-511`, но не в README. `/error` — не
controller-маршрут: разрешён в `SecurityConfig.java:105` через `permitAll()`,
обрабатывается дефолтным Spring Boot error-handler'ом.

README прямым текстом утверждает «Полный контракт — Swagger UI»
(`README.md:381`) — при нуле описательных аннотаций сгенерированная спека
содержит только сигнатуры (пути, HTTP-методы, имена параметров из кода),
без описаний операций, кодов ответов и примеров; «полнота» контракта — это
полнота списка маршрутов, а не описания.

## 7. Свежесть и владение

Дата — последний коммит, затронувший файл (`git log -1 --format=%ad
--date=short -- <file>`); строк — на HEAD `bf1ff278`.

| Файл | Строк | Дата |
|---|---|---|
| `CLAUDE.md` | 181 | 2026-08-11 |
| `DESIGN.md` | 319 | 2026-06-25 |
| `FINAL_UI_UX_REVIEW_AND_AUTONOMOUS_PLAN.md` | 1405 | 2026-07-15 |
| `FRONTEND_BACKLOG.md` | 92 | 2026-07-16 |
| `FRONTEND_DECISIONS.md` | 148 | 2026-07-16 |
| `FRONTEND_EVIDENCE.md` | 880 | 2026-07-16 |
| `FRONTEND_HISTORY.md` | 3265 | 2026-07-15 |
| `INTERVIEW-MASTER-PLAN.md` | 247 | 2026-06-17 |
| `MASTER_PLAN_COMPLETE_PRODUCT_REDESIGN.md` | 3301 | 2026-08-04 |
| `PLAN_FRONTEND.md` | 120 | 2026-07-15 |
| `PLAN_INTERVIEW.md` | 376 | 2026-07-18 |
| `PRODUCT.md` | 80 | 2026-06-08 |
| `PROMPT_PLAN_FRONTEND.md` | 910 | 2026-07-15 |
| `PROMPT_PLAN_INTERVIEW.md` | 1800 | 2026-07-18 |
| `README.md` | 840 | 2026-08-11 |
| `WIKI.md` | 850 | 2026-04-10 |
| `roadmap-checklist.md` | 1416 | 2026-06-30 |
| `docs/METRICS_GLOSSARY.md` | 120 | 2026-08-06 |
| `docs/UI_UX_GRAPHICS_BEST_PRACTICES.md` | 1405 | 2026-08-04 |
| `docs/accessibility.md` | 193 | 2026-08-12 |
| `docs/backend-architecture.md` | 541 | 2026-08-06 |
| `docs/design-system.md` | 1234 | 2026-08-12 |
| `docs/frontend-architecture.md` | 737 | 2026-08-12 |
| `docs/frontend-redesign-loop-brief.md` | 2124 | 2026-08-28 |
| `docs/frontend-redesign-state.md` | 9821 | 2026-08-28 |
| `docs/frontend-refoundation-audit.md` | 511 | 2026-08-24 |
| `docs/frontend-refoundation-changelog.md` | 596 | 2026-08-25 |
| `docs/golden-examples.md` | 1110 | 2026-07-11 |
| `docs/mcq-llm-batch-automation.md` | 137 | 2026-05-21 |
| `docs/mcq-quality-improvement-plan.md` | 326 | 2026-05-09 |
| `docs/mcq-quality-standard.md` | 740 | 2026-08-11 |
| `docs/mcq-refactoring-state.md` | 406 | 2026-08-11 |
| `docs/mcq-structure-parity-plan.md` | 331 | 2026-07-11 |
| `docs/product-redesign-state.md` | 484 | 2026-08-11 |
| `docs/quality-report.md` | 808 | 2026-08-11 |
| `docs/visual-assets.md` | 372 | 2026-08-21 |

Наблюдения: самый старый документ — `WIKI.md` (2026-04-10, чужеродный
контент, см. §8); самый старый продуктовый — `PRODUCT.md` (2026-06-08);
активнее всего правится `docs/frontend-redesign-state.md` (2026-08-28, тот же
день, что и HEAD этой задачи на момент проектирования) — прямое следствие
параллельного loop'а фронтенд-рефаундации (Хартия §4). Корневые `FRONTEND_*`
(2026-07-15/16) и оба `PROMPT_PLAN_*`/`PLAN_*` (2026-07-15/18) — заморожены
одним днём каждая пара, признак единой сессии написания без последующей
правки. `docs/` в среднем свежее корня: 15 из 19 файлов `docs/*.md` правлены
в августе против 3 из 17 корневых.

## 8. Дельта «документ vs код»

Каждая строка — тройкой: цитата документа с якорем → факт кода с якорем →
команда-доказательство (все — в Приложении A).

1. **`prompts/README.md:9`** — «`modules/quiz-app/.../service/ai/
   AiPrompts.java` — runtime-промпты самого приложения» → файла нет
   (`find modules -name 'AiPrompts*'` — пусто) → прямо противоречит
   `CLAUDE.md:58-63` («AI-код вырезан целиком»). AI-контур удалён, ссылка на
   него в `prompts/README.md` — не обновлена.
2. **`api/controller/package-info.java:12`** — `@see com.cheatsheet.quiz.
   feature.admin.controller.AdminController` → класса нет
   (`find modules -name 'AdminController*'` — пусто); в
   `feature/admin/` есть только `dto/` и `service/` (`AdminOperationResult`,
   `AdminMaintenanceService`), контроллера как класса не существует.
3. **`CLAUDE.md:102-166`** («В проекте есть граф знаний `graphify-out/`...
   ОБЯЗАТЕЛЬНО достроить») → каталога `graphify-out/` в репозитории нет
   (`ls graphify-out 2>&1` → `No such file or directory`), в `.gitignore` он
   не упомянут (`grep -c graphify .gitignore` → 0) — инструкция неисполнима
   на чистом клоне без предварительного шага, которого сама инструкция не
   описывает.
4. **`README.md:664-686`** («Структура проекта») → 4 каталога верхнего
   уровня отсутствуют в дереве: `e2e/`, `prompts/`, `design/`, `.design/` —
   все четыре существуют (`find . -maxdepth 1 -mindepth 1 -type d`).
5. **`README.md:359-392`** («Основные REST-эндпоинты») → раздел вообще не
   описывает MVC GET-страницы, список не содержит 13 маршрутов
   (`GET /`, `/training`, `/review`, `/settings`, `/train`, `/stats`,
   `/topics`, `/bookmarks`, `/session-summary`; `POST /bookmarks/note`,
   `/question/note`, `/bookmarks/remove`, `/pause/discard`), ни CSS-бандл —
   полный разбор в §6.
6. **`docs/backend-architecture.md:499-511`** (§7 «HTTP-поверхность») →
   список короче: MVC GET-страницы там перечислены, `POST /bookmarks/*`
   покрывает `/bookmarks/note` и `/bookmarks/remove` как маска. Не хватает
   только `GET /train`, `GET /topics`, `POST /question/note`,
   `POST /pause/discard` и CSS-бандл — разбор в §6.
7. **`README.md:381`** — «Полный контракт — Swagger UI» → при нуле
   springdoc-аннотаций (§6) сгенерированная спека не содержит описаний
   операций/кодов ответов/примеров — полнота ограничена списком путей.
8. **`WIKI.md`** (850 строк, правка 2026-04-10) → не документация этого
   проекта: `WIKI.md:1-15` — шаблон «LLM Wiki Schema» для Obsidian
   («Based on Andrej Karpathy's LLM Wiki pattern»); ссылок на него нет ни в
   `README.md`, ни в `CLAUDE.md`, ни в `docs/*.md`
   (`grep -rl 'WIKI\.md' README.md CLAUDE.md docs/*.md` — пусто).
9. **Отсутствие индекса.** `docs/README.md` не существует
   (`test -e docs/README.md` → код возврата 1) при 100 `.md` внутри `docs/`
   — единственная точка входа в документацию `docs/` — раздел «Дополнительная
   документация» в `README.md:796-807`, покрывающий 7 из 100 файлов.

Расхождения #4–#6 — не про удалённый/выдуманный код (как #1–#2), а про
неполноту перечня в актуальном документе; #7–#9 — про структурные пробелы
(вводящая в заблуждение формулировка, чужеродный файл, нет индекса).
Негативные утверждения (`CLAUDE.md:58` про удалённый `AIQuestionService` —
корректный текст о том, чего нет, не битая ссылка) в перечень не попадают
согласно правилу 3 design.md.

## 9. Связность и навигация

Локальных относительных markdown-ссылок (`](путь)`, не `#якорь`, не `http`) в
`README.md`, `CLAUDE.md`, `PRODUCT.md`, `DESIGN.md` и трёх архитектурных
доках (`docs/backend-architecture.md`, `docs/frontend-architecture.md`,
`docs/design-system.md`) — **12**, битых — **0** (скрипт-проверка в
Приложении A: все 12 целей существуют на HEAD, включая `docs/design/
DESIGN.md` и `docs/design/systems/`, на которые ссылается `DESIGN.md:5-6`).
`CLAUDE.md`, `PRODUCT.md` и три архитектурных документа таких ссылок не
содержат вовсе — все 12 находятся в `README.md` (10, включая дубль
`docs/backend-architecture.md`) и `DESIGN.md` (2).

**Орфаны.** Из 16 проверенных корневых документов (15 `.md` без `README.md`/
`CLAUDE.md` + `FRONTEND_STATE.json`) на входящие упоминания из `README.md`/
`CLAUDE.md`/`docs/*.md` — входящие есть только у `DESIGN.md` (2 упоминания) и
`MASTER_PLAN_COMPLETE_PRODUCT_REDESIGN.md` (1). Остальные **14 — орфаны**:

`FINAL_UI_UX_REVIEW_AND_AUTONOMOUS_PLAN.md`, `FRONTEND_BACKLOG.md`,
`FRONTEND_DECISIONS.md`, `FRONTEND_EVIDENCE.md`, `FRONTEND_HISTORY.md`,
`FRONTEND_STATE.json`, `INTERVIEW-MASTER-PLAN.md`, `PLAN_FRONTEND.md`,
`PLAN_INTERVIEW.md`, `PRODUCT.md`, `PROMPT_PLAN_FRONTEND.md`,
`PROMPT_PLAN_INTERVIEW.md`, `WIKI.md`, `roadmap-checklist.md`.

Документация связана слабо, но не сломанно: существующие ссылки не битые,
но 87.5% корневых non-README-документов не достижимы ни из одной другой
markdown-точки входа — найти их можно только листингом каталога.

## 10. Белые пятна

1. **Живая OpenAPI-спека.** Статикой видна конфигурация (§6: 0 аннотаций,
   spec выключена в prod), но не фактический JSON `/v3/api-docs` в
   default-профиле — сколько путей реально попадает в спеку и как выглядят
   auto-generated имена операций/схем без аннотаций. Закрывается: поднять
   приложение на `SERVER_PORT=8091` (не 8080), снять `curl
   localhost:8091/v3/api-docs`, сверить число путей с 37 handler-методами
   §6 — follow-up этой задачи.
2. **Содержимое 81 файла во вложенных `docs/**`.** §1 даёт агрегаты
   (17 подкаталогов, число `.md` в каждом), но не читает содержимое —
   неизвестно, сколько из них актуальны против дублируют/противоречат друг
   другу (особенно `docs/mcq-quality/archive/round-8/` — уже архив по имени,
   и `docs/css-decisions/` — 36 файлов, крупнейший подкаталог, ни разу не
   упомянутый ни в README, ни в CLAUDE.md). Закрывается: точечное чтение
   `docs/css-decisions/*.md` и `docs/mcq-quality/*.md` на предмет актуальности
   против текущего `docs/design-system.md`.
3. **Актуальность 100 `.md` `docs/` относительно кода.** §8 проверяет 9
   конкретных расхождений (список design.md), но не прогоняет автоматическую
   сверку «упомянутый в docs/ путь/класс существует» по всем 100 файлам —
   только по выборке, вручную профильтрованной от JDK-типов/SQL-ключевых
   слов/тестовых классов (риск design.md, «Ложные битые ссылки»). Закрывается:
   написать `scripts/`-валидатор, который извлекает токены `path/Class.java`
   из `docs/**/*.md` и проверяет их существованием файла — сузит фильтр до
   документного контекста, а не разовой ручной проверки.

## Приложение A. Команды воспроизведения

```bash
# Шапка
git rev-parse --short HEAD && git branch --show-current
git status --porcelain -uall   # ожидается: пусто (assert-main-tree-touched)

# §1 — инвентарь
find . -maxdepth 1 -name "*.md" -type f | wc -l                       # 17
find . -maxdepth 1 -name "*.md" -type f | sort | while read f; do echo "$(wc -l < "$f") $f"; done
find docs -type f | wc -l                                             # 851
find docs -name "*.md" -type f | wc -l                                # 100
find docs -name "*.json" -type f | wc -l                              # 745
find docs -name "*.tsv" -type f | wc -l                                # 5
find docs -name "*.csv" -type f | wc -l                                # 1
find docs -maxdepth 1 -name "*.md" -type f | wc -l                    # 19
find docs -mindepth 1 -type d | wc -l                                 # 17
find docs -mindepth 1 -type d | sort | while read d; do
  echo "$(find "$d" -maxdepth 1 -name '*.md' | wc -l) $d"
done

# §2 — README
wc -l README.md                                                       # 840
grep -cE "^#[^#]" README.md   # 39, но большинство — bash-комментарии в блоках кода
grep -nE "^#[^#]" README.md | grep -vE ":#" | head            # ручная фильтрация code-fence строк
grep -cE "^##[^#]" README.md                                          # 24
grep -cE "^###[^#]" README.md                                         # 28
sed -n '664,686p' README.md                                           # структура проекта
find . -maxdepth 1 -mindepth 1 -type d -not -path "./.git" -not -path "./build" | sort
sed -n '796,807p' README.md                                           # индекс документации
sed -n '359,392p' README.md                                           # 6 буллетов MVC POST (10 маршрутов), 8 буллетов REST (12 маршрутов)
awk '/^## Troubleshooting/{f=1} /^## Дополнительная документация/{f=0} f' README.md | grep -c '^### '  # 10

# §3 — архитектура
grep -cE '^## ' docs/backend-architecture.md                          # 11 (## 1..## 9 плюс ## 3a/## 3b)
wc -l docs/backend-architecture.md docs/frontend-architecture.md docs/design-system.md docs/accessibility.md  # 541/737/1234/193
git log -1 --format=%ad --date=short -- docs/backend-architecture.md  # 2026-08-06
git log -1 --format=%ad --date=short -- docs/frontend-architecture.md # 2026-08-12
git log -1 --format=%ad --date=short -- docs/design-system.md         # 2026-08-12
git log -1 --format=%ad --date=short -- docs/accessibility.md         # 2026-08-12
git log -1 --format=%ad --date=short -- CLAUDE.md                     # 2026-08-11
find modules -name "package-info.java" | wc -l                        # 12
find modules -name "package-info.java" | sort | while read f; do
  echo "$f :: $(grep -m1 '^package' "$f")"
done
grep -n "AdminController" modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/controller/package-info.java

# §4 — продукт
wc -l PRODUCT.md docs/frontend-redesign-state.md MASTER_PLAN_COMPLETE_PRODUCT_REDESIGN.md
sed -n '5,6p' DESIGN.md
ls FRONTEND_*.md | wc -l                                              # 4 (FRONTEND_STATE.json не .md)

# §5 — домен
find cheatsheets -name "*.md" | wc -l                                 # 1165
find cheatsheets/interview -name "*.md" | wc -l                       # 321
find modules/quiz-app/src/main/resources/seed/mcq -name "*.json" | wc -l  # 318
wc -l docs/mcq-quality-standard.md docs/golden-examples.md INTERVIEW-MASTER-PLAN.md
find docs/mcq-quality -maxdepth 1 -name "*.md" | wc -l                # 6
find prompts/markdown prompts/text -type f | wc -l                    # 8
sed -n '8,9p' prompts/README.md

# §6 — API
grep -n "springdoc = " gradle/libs.versions.toml                      # 2.6.0
sed -n '34,51p' modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/OpenApiConfig.java
grep -rlE '@(Operation|ApiResponse|Tag|Schema)\b' modules --include="*.java" | grep '/src/main/' | wc -l  # 0
sed -n '42,46p' modules/quiz-app/src/main/resources/application-prod.yml
for f in modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java \
         modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewApiController.java \
         modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/CssBundleController.java \
         modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/auth/controller/LoginController.java \
         modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/export/controller/ExportController.java; do
  echo "$(grep -cE '@(Get|Post|Put|Delete|Patch)Mapping' "$f") $f"
done
grep -rE '@(Get|Post|Put|Delete|Patch)Mapping' modules --include="*Controller.java" | grep '/src/main/' | wc -l  # 37
grep -n '"/error"' modules/quiz-app/src/main --include="*.java" -r
git diff af3f2db6 -- modules/quiz-app/.../InterviewMvcController.java     # пусто — файл не менялся на этой ветке
git merge-base --is-ancestor b56fc164 af3f2db6 && echo REF-016-in-af3f2db6
git merge-base --is-ancestor 87f3433f af3f2db6 && echo REF-024-in-af3f2db6

# §7 — свежесть (пример на одном файле, остальные — та же команда)
git log -1 --format=%ad --date=short -- docs/frontend-redesign-state.md   # 2026-08-28
wc -l docs/frontend-redesign-state.md                                     # 9821
for f in docs/*.md; do git log -1 --format=%ad --date=short -- "$f"; done | grep -c '^2026-08'  # 15 из 19
for f in $(find . -maxdepth 1 -name "*.md"); do git log -1 --format=%ad --date=short -- "$f"; done | grep -c '^2026-08'  # 3 из 17

# §8 — дельта doc↔код
find modules -name "AiPrompts*"                                       # пусто
find modules -name "AdminController*"                                 # пусто
ls graphify-out 2>&1                                                  # No such file or directory
grep -c graphify .gitignore                                           # 0
grep -rl "WIKI\.md" README.md CLAUDE.md docs/*.md                     # пусто
test -e docs/README.md; echo $?                                       # 1 (нет)

# §9 — связность и орфаны
for f in README.md CLAUDE.md PRODUCT.md DESIGN.md docs/backend-architecture.md \
         docs/frontend-architecture.md docs/design-system.md; do
  grep -noE '\]\([^)]*\)' "$f" | grep -v '](http' | grep -vE '\]\(#'
done | wc -l                                                          # 12
# существование целей (без якорей #...):
for p in docs/backend-architecture.md CLAUDE.md docs/product-redesign-state.md \
         docs/frontend-architecture.md docs/design-system.md docs/visual-assets.md \
         docs/accessibility.md docs/quality-report.md DESIGN.md \
         docs/design/DESIGN.md docs/design/systems; do
  test -e "$p" || echo "MISSING: $p"
done                                                                   # пусто — 0 битых
for f in DESIGN.md FINAL_UI_UX_REVIEW_AND_AUTONOMOUS_PLAN.md FRONTEND_BACKLOG.md \
         FRONTEND_DECISIONS.md FRONTEND_EVIDENCE.md FRONTEND_HISTORY.md \
         INTERVIEW-MASTER-PLAN.md MASTER_PLAN_COMPLETE_PRODUCT_REDESIGN.md \
         PLAN_FRONTEND.md PLAN_INTERVIEW.md PRODUCT.md PROMPT_PLAN_FRONTEND.md \
         PROMPT_PLAN_INTERVIEW.md WIKI.md roadmap-checklist.md FRONTEND_STATE.json; do
  echo "$f refs=$(grep -rl "$f" README.md CLAUDE.md docs/*.md 2>/dev/null | wc -l)"
done                                                                   # 14 строк с refs=0

# AC13 — существование ВСЕХ упомянутых в отчёте путей
grep -oE '(modules|cheatsheets|e2e|docs|scripts|gradle|prompts|\.github|\.sdlc)/[A-Za-z0-9_./-]+' \
  .sdlc/CS-20260828-013/report.md \
  | sed -E 's/[):,.]+$//; s/:[0-9]+(-[0-9]+)?$//' | sort -u | while read -r p; do test -e "$p" || echo "MISSING: $p"; done
# ожидаемый вывод: только заведомо отсутствующие пути, помеченные в тексте как таковые
```
