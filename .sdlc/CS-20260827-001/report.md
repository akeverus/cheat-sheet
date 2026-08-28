# Phase 0 — Frontend Reconnaissance (CS-20260827-001)

**SHA HEAD:** `77884ea5c57f845d31ed35ff840d700cf3569e54`
**Дата:** 2026-08-28
**Ветка:** `sdlc/CS-20260827-001`
**Метод:** статическое чтение репозитория (`git log`, `git diff --stat`, `find`, `wc`,
`grep`) на зафиксированном SHA. Приложение не поднималось, БД не читалась,
graphify-граф не перестраивался (граф в этом репо — AST-only по `modules/` и не
покрывает Thymeleaf/CSS/JS; см. `CLAUDE.md` этого проекта, §6).

---

## 0. Сводка одним экраном

| Рубрика | Факт | Главный якорь |
|---|---|---|
| 1. Framework | Server-rendered Spring MVC + Thymeleaf, без SPA/бандлера/npm-рантайма | `modules/quiz-app/src/main/resources/templates/fragments/scripts.html:5` |
| 2. Routing | 23 маршрута страниц/форм + 11 `/api/*` + `/login` + `/export` + `/css/bundle/*` = 37 точек входа | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java:45-426` |
| 3. Pages | 10 шаблонов, 4218 строк, 9 CSS page-гейтов (train.html делит гейт с settings) | `modules/quiz-app/src/main/resources/templates/*.html` |
| 4. Layouts/shell | shell-head + sidebar (рельс) + mobile-nav (нижняя полоса), единый источник разделов — enum `NavSection` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/NavSection.java:42-48` |
| 5. Components | 16 переиспользуемых фрагментов, включения 1–10 файлов каждый | `modules/quiz-app/src/main/resources/templates/fragments/*.html` |
| 6. State | Серверное — сессия интервью в БД; клиентское — 3 pre-paint localStorage-ключа + 3 legacy + 4 post-paint/константных localStorage-ключа + 2 sessionStorage-ключа | `modules/quiz-app/src/main/resources/templates/fragments/head.html:210-268`, `modules/quiz-app/src/main/resources/static/js/core/constants.js:21` |
| 7. «Hooks» | Pre-paint инлайн-скрипт в `<head>` (тема/пресет/motion, без FOUC; font-scale — post-paint) + `modules/quiz-app/src/main/resources/static/js/core/lifecycle.js` init/destroy-реестр | `modules/quiz-app/src/main/resources/templates/fragments/head.html:242-268`, `modules/quiz-app/src/main/resources/static/js/core/lifecycle.js:1-42` |
| 8. API layer | `modules/quiz-app/src/main/resources/static/js/core/api-client.js` — единый `apiFetch`: CSRF, 30s timeout, ProblemDetail-парсинг | `modules/quiz-app/src/main/resources/static/js/core/api-client.js:64-113` |
| 9. Forms | Thymeleaf-формы, CSRF из cookie `XSRF-TOKEN`, no-JS фоллбэк на `/answer` | `modules/quiz-app/src/main/resources/static/js/core/csrf.js:1-7` |
| 10. Validation | Jakarta Bean Validation на DTO (`@Positive`/`@Min`/`@Max`) + клиентские form-guards | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/api/dto/request/interview/SubmitAnswerRequest.java:27-73` |
| 11. Styling | 44 CSS-файла, 12069 строк, `@layer`-каскад из 7 слоёв, порядок — `css-bundle-manifest.txt` (161 строка, 43 записи) | `css-bundle-manifest.txt` |
| 12. Design tokens | `tokens.css`, 376 строк, тёмная тема — дефолт (`color-scheme: dark`), light — оверрайд атрибутом | `modules/quiz-app/src/main/resources/static/css/tokens.css:1-9` |
| 13. Assets | 20 woff2, 25 SVG-символов, 10 растровых PNG-стикеров (design.md ошибочно считал img/ без PNG), 3 vendor-библиотеки | `modules/quiz-app/src/main/resources/static/img/stickers/*.png` |
| 14. Fonts/icons | Literata (текст) + Inter (интерфейс), локально; один пиксельный SVG-спрайт (Lucide изгнан контрактным тестом) | `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/IconLanguageContractTest.java:20-30` |
| 15. Animations | 3 CSS-файла motion/FX, `pixel-motion-parity.css` гасит под `prefers-reduced-motion`/`data-motion=off` | `modules/quiz-app/src/main/resources/static/css/pixel-motion-parity.css` |
| 16. Tests | 74 Java-файла в `ui/` (73 контракта + 1 хелпер), 2 Playwright-спеки, 28 голден-PNG (chromium+webkit) | `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/*ContractTest.java` |
| 17. Дельта vs аудит 2026-08-22 | 105 коммитов, 100 файлов `modules/quiz-app/src/main/resources/**` с даты создания аудита | `git diff --stat 3ba1845c..HEAD` |
| 18. Белые пятна | 6 пунктов, все требуют живого рендера/DOM | см. §18 |

---

## 1. Framework и рендер-модель

Стек — **Spring MVC + Thymeleaf**, server-side rendering. SPA-фреймворка,
JS-бандлера (webpack/vite/esbuild) и npm-зависимостей в рантайме нет — весь JS
отдаётся статикой Spring Boot из `static/js/`.

- Единая точка входа фронтенд-скриптов — один `<script type="module">` с
  CSP-nonce: `modules/quiz-app/src/main/resources/templates/fragments/scripts.html:5` →
  `<script type="module" th:src="@{/js/app-entry.js}" th:attr="nonce=${cspNonce}">`.
  `app-entry.js` (156 строк) статически импортирует ядро (lifecycle, dom,
  highlight, table-scroll, streak, favorites, bookmarks, form-guards, pending,
  keyboard-help, code-toolbar, cycle-focus, modals, shell) и динамически
  (`import()`) — модули настроек и экспорта, чтобы не тащить их бандлы на
  страницы, где они не нужны (`modules/quiz-app/src/main/resources/static/js/app-entry.js:29-40`).
- CSP: nonce генерируется на сервер-стороне и пробрасывается в `<script>`
  через `th:attr="nonce=${cspNonce}"` — инлайн-скрипты (pre-paint в `head.html`)
  и внешний модуль используют один и тот же nonce. Гейт —
  `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/DiagramCspContractTest.java`.
- Pre-paint инлайн-скрипты в `modules/quiz-app/src/main/resources/templates/fragments/head.html` (333 строки) применяют тему,
  screen-preset (density/readingWidth/pixel-intensity) и motion
  **до первого кадра** — иначе смена этих атрибутов после отрисовки дала бы
  видимую перекладку (`modules/quiz-app/src/main/resources/templates/fragments/head.html:197-203`).
  Font-scale в этот набор НЕ входит: он применяется post-paint модулем
  `modules/quiz-app/src/main/resources/static/js/settings/personalization-bootstrap.js`, чей заголовочный
  javadoc (`:1-4`) прямо называет себя «Non-FOUC-critical personalization:
  applied when module loads (after first paint) … Здесь остался размер
  шрифта» — не двигает геометрию блоков, поэтому не обязан быть в шапке.
- Модуль `feature/interview/` организован как vertical slice:
  `controller/` → `usecase/` (page/flow MVC-сервисы) → `dto/`/`mapper/` —
  контроллер не содержит бизнес-логики, только маршрутизацию и биндинг DTO
  (`modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java:1-428`).

## 2. Routing

Пять контроллеров, единый источник маршрутов. Таблица покрывает все
`@GetMapping`/`@PostMapping` из перечисленных в AC контроллеров.

### `InterviewMvcController.java` (23 маршрута, страницы + form-POST)

| URL | Метод | Контроллер:строка | View / редирект | Назначение |
|---|---|---|---|---|
| `/` | GET | `InterviewMvcController.java:45` | `focus-training` (`MvcNavigationService.focusView()` :46) | Канонический вход — тренировка без сессии (TRAINING) |
| `/training` | GET | `:82` | `redirect:/` (whitelist query-параметров) | Легаси-адрес, перенос на канонический `/` |
| `/review` | GET | `:94` | `focus-training` (тот же шаблон, `onlyWrong` форсится) | Разбор ошибок — режим внутри тренировки, не отдельный раздел |
| `/settings` | GET | `:123` | `settings` либо `redirect:/train` (если есть builder-параметры) | Настройки; deep-link'и конструктора уводятся на `/train` |
| `/train` | GET | `:188` | `train` (`MvcNavigationService.trainView()` :61) | Конструктор сессии «Собрать сессию» (UXD-001) |
| `/start` | POST | `:215` | `redirect:/` | Старт сессии (`StartSessionRequest`, `@Valid`) |
| `/study-confirm` | POST | `:223` | — | Подтверждение входа в STUDY-цикл |
| `/flashcard-reveal` | POST | `:234` | — | Раскрытие эталона во флешкарт-режиме (без persist черновика) |
| `/flashcard-grade` | POST | `:239` | — | Оценка флешкарты (`questionId`, `grade` 0..5) |
| `/finish` | POST | `:248` | `redirect:/session-summary` | Завершение сессии |
| `/skip` | POST | `:253` | — | Пропуск вопроса |
| `/pause` | POST | `:262` | — | Пауза с сохранением `BrowseTarget` |
| `/resume` | POST | `:276` | — | Возврат к приостановленной сессии |
| `/pause/discard` | POST | `:281` | — | Отбросить приостановленную сессию |
| `/session-summary` | GET | `:286` | `session-summary` (`MvcNavigationService.sessionSummaryView()` :96) | Итоги сессии |
| `/answer` | POST | `:291` | `result` (no-JS фоллбэк) либо JSON через `/api/answer` | Отправка ответа (`SubmitAnswerRequest`, `@Valid`) |
| `/settings/reset-options` | POST | `:335` | `redirect:/settings` | Очистка банка вариантов (admin-gated) |
| `/stats` | GET | `:342` | `stats` (`MvcNavigationService.statsView()` :75) | Аналитика/поиск по банку |
| `/topics` | GET | `:375` | `topics` (`MvcNavigationService.topicsView()` :82) | Каталог тем банка |
| `/bookmarks` | GET | `:383` | `bookmarks` (`MvcNavigationService.bookmarksView()` :89) | Закладки |
| `/bookmarks/note` | POST | `:394` | `redirect:/bookmarks#q-{id}` | Заметка к закладке |
| `/question/note` | POST | `:411` | `redirect:/` (тот же вопрос) | Заметка к вопросу с экрана тренировки |
| `/bookmarks/remove` | POST | `:420` | `redirect:/bookmarks` | Удаление закладки |

View-имена разрешаются через единственный источник —
`modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/MvcNavigationService.java:45-96` (`focusView()`,
`settingsView()`, `trainView()`, `resultView()`, `statsView()`, `topicsView()`,
`bookmarksView()`, `sessionSummaryView()` — каждый возвращает литеральную
строку имени шаблона).

### `InterviewApiController.java` (11 маршрутов, JSON-слой)

| URL | Метод | Строка | Ответ (DTO) |
|---|---|---|---|
| `/api/answer` | POST | `:67` | `ResponseEntity<AnswerResponse>` |
| `/api/confidence` | POST | `:79` | `ResponseEntity<ConfidenceResponse>` |
| `/api/streak` | GET | `:87` | `ResponseEntity<StreakResponse>` |
| `/api/stats` | GET | `:96` | `ResponseEntity<InterviewStatsResponse>` |
| `/api/topic-stats` | GET | `:108` | `ResponseEntity<List<TopicStatsResponse>>` |
| `/api/next` | GET | `:116` | `ResponseEntity<NextQuestionResponse>` |
| `/api/favorite` | POST | `:140` | `ResponseEntity<FavoriteResponse>` |
| `/api/bookmark` | POST | `:147` | `ResponseEntity<BookmarkResponse>` |
| `/api/bookmark/note` | POST | `:154` | `ResponseEntity<BookmarkResponse>` |
| `/api/bookmark/remove` | POST | `:161` | `ResponseEntity<BookmarkResponse>` |
| `/api/report` | POST | `:171` | `ResponseEntity<ReportIssueResponse>` |

Все 11 вызываются с фронта — полное совпадение с `fetch`-адресами в
`static/js/` (см. §8).

### Прочие контроллеры

| URL | Метод | Контроллер:строка | Условие/гейт |
|---|---|---|---|
| `/login` | GET | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/auth/controller/LoginController.java:35` | Только `app.security.mode=multi` (`@ConditionalOnProperty` :19) |
| `/export` | GET | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/export/controller/ExportController.java:46` | `produces=json,csv`; admin-token/`ROLE_ADMIN` через `SensitiveEndpointAccessService`, доп. rate-limit |
| `/css/bundle/{page}.{hash}.css` | GET | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/CssBundleController.java:34` | Content-hash в имени → `Cache-Control: max-age=365d, immutable` |

## 3. Pages

10 Thymeleaf-шаблонов, суммарно **4218 строк** (`find … -maxdepth 1 -name
"*.html" | xargs wc -l`).

| Шаблон | Строк | Контроллер-владелец | CSS page-гейт |
|---|---|---|---|
| `focus-training.html` | 1168 | `InterviewMvcController` (`/`, `/review`) | `focus` |
| `stats.html` | 1266 | `InterviewMvcController` (`/stats`) | `stats` |
| `session-summary.html` | 365 | `InterviewMvcController` (`/session-summary`) | `summary` |
| `bookmarks.html` | 334 | `InterviewMvcController` (`/bookmarks`) | `bookmarks` |
| `topics.html` | 294 | `InterviewMvcController` (`/topics`) | `topics` |
| `result.html` | 270 | `InterviewMvcController` (`/answer`, no-JS) | `result` |
| `settings.html` | 249 | `InterviewMvcController` (`/settings`) | `settings` |
| `login.html` | 113 | `LoginController` (`/login`) | `login` |
| `error.html` | 113 | Spring Boot error-view fallback | `error` |
| `train.html` | 46 | `InterviewMvcController` (`/train`) | `settings` (переиспользован, `train.html:14`) |

`train.html` — единственный шаблон без собственного CSS page-гейта: он
намеренно переиспользует `settings`, потому что конструктор сессии визуально
идентичен вкладке настроек (комментарий в `train.html:10`). Отсюда 10
шаблонов при 9 значениях `page` в `css-bundle-manifest.txt`.

## 4. Layouts и shell

Первый уровень навигации живёт в ОДНОМ месте — enum
`modules/quiz-app/src/main/java/com/cheatsheet/quiz/config/web/NavSection.java:42-48` (5 значений: `FOCUS`, `TOPICS`,
`ANALYTICS`, `BOOKMARKS`, `SETTINGS`), который читают оба навигационных
фрагмента:

- `modules/quiz-app/src/main/resources/templates/fragments/sidebar.html` (78 строк) — боковой рельс, десктоп.
- `modules/quiz-app/src/main/resources/templates/fragments/mobile-nav.html` (23 строки) — нижняя полоса, мобильный.

До появления enum разделы были продублированы вручную в обоих фрагментах и
расходились (задокументированный, а не гипотетический дефект — javadoc
`NavSection.java:8-24`: рельс имел 6 пунктов, полоса — 5, «Темы» была
недостижима с телефона). Дополнительно enum устранил вторую находку —
несовпадение иконок одного и того же раздела между рельсом и полосой
(`NavSection.java:17-18`).

Раздел «Ошибки» убран из первого уровня осознанно: `/review` и `/` рендерят
один и тот же шаблон, разница — принудительный `onlyWrong=true`, который
человек мог включить и чипом в конструкторе сессии; включение экрана-режима в
навигацию наравне с разделами путало модель (`NavSection.java:29-35`).

`modules/quiz-app/src/main/resources/templates/fragments/shell-head.html` (89 строк) — общая верхняя обвязка/топбар,
включена в 9 из 10 шаблонов (все, кроме `error.html`).

Пресеты экрана (`phone`/`laptop`/`wide`) управляют плотностью раскладки
до первого кадра — см. §6 и §7.

## 5. Components (фрагменты)

16 фрагментов в `templates/fragments/`, 1414 строк суммарно. Частота
включения (`grep -rl "fragments/<name> ::"`, без учёта самого файла-владельца):

| Фрагмент | Строк | Включений | Что инкапсулирует |
|---|---|---|---|
| `head.html` | 333 | 10 | `<head>`: мета, CSS-бандл, pre-paint скрипты темы/пресета/шрифта/motion |
| `session-builder.html` | 335 | 1 | Форма конструктора сессии (используется в `train.html`) |
| `icons.html` | 117 | 10 | Пиксельный SVG-спрайт (`<symbol>`) |
| `state-illustrations.html` | 117 | 1 | Пиксель-арт иллюстрации пустых/ошибочных состояний |
| `progression.html` | 108 | 2 | Виджет ступени гильдии / уровня (итоги, аналитика) |
| `shell-head.html` | 89 | 9 | Топбар приложения |
| `sidebar.html` | 78 | 9 | Боковой навигационный рельс (десктоп) |
| `today-widget.html` | 77 | 1 | Виджет «сегодня» на главной |
| `recoverable-banners.html` | 35 | 1 | Баннеры восстановимых состояний (напр. resume-сессия) |
| `training-actions.html` | 30 | 1 | Кнопки действий на экране тренировки |
| `mobile-nav.html` | 23 | 9 | Нижняя навигационная полоса (мобильный) |
| `post-answer-controls.html` | 21 | 1 | Контролы после ответа на вопрос |
| `notice.html` | 25 | 3 | Единая система уведомлений (`.notice` + тоны) |
| `result-zone-head.html` | 13 | 2 | Заголовок зоны разбора ответа |
| `mermaid-init.html` | 6 | 1 | Инициализация mermaid.js |
| `scripts.html` | 7 | 10 | Точка подключения `app-entry.js` |

## 6. State

**Серверное** — активная сессия интервью хранится в БД (PostgreSQL,
`quiz-persistence`), привязана к `HttpSession` (передаётся во все
POST/GET-обработчики `InterviewMvcController` как параметр `HttpSession
session`). Реактивного клиентского стора (Redux/Zustand-аналог) нет.

**Клиентское.** Первый прогон `grep -rhoE "localStorage\.(getItem|setItem|removeItem)\(['\"][A-Za-z0-9_.-]+['\"]"`
по `static/js/`+`templates/` ловит только строковые литералы и пропускает
ключи, заданные константами (`ADMIN_TOKEN_KEY`) или шаблонной конкатенацией
(`` `tabPanel:${storageKey}` ``). Ниже — таблица, дополненная ручным резолвом
таких констант/шаблонов; она же учтена в обновлённой команде Приложения A.

*localStorage — pre-paint (до первого кадра, влияет на раскладку/тему):*

| Ключ | Пишет | Читает | Статус |
|---|---|---|---|
| `theme` | `modules/quiz-app/src/main/resources/templates/fragments/head.html` (pre-paint) | `modules/quiz-app/src/main/resources/templates/fragments/head.html` | активен |
| `motion` | `modules/quiz-app/src/main/resources/templates/fragments/head.html` | `modules/quiz-app/src/main/resources/templates/fragments/head.html` | активен |
| `screenPreset` | `modules/quiz-app/src/main/resources/templates/fragments/head.html:256` (`setScreenPreset`) | `modules/quiz-app/src/main/resources/templates/fragments/head.html:237` | активен, значения `phone`\|`laptop`\|`wide` |
| `density` | — (только `removeItem`) | — (только `getItem` в миграции) | **legacy**, авто-мигрируется |
| `readingWidth` | — (только `removeItem`) | — (только `getItem` в миграции) | **legacy**, авто-мигрируется |
| `pixelIntensity` | — (только `removeItem`) | — (только `getItem` в миграции) | **legacy**, авто-мигрируется |

Миграция трёх legacy-ключей в один `screenPreset` — `modules/quiz-app/src/main/resources/templates/fragments/head.html:210-234`
(`migrateLegacyPreset()`): при первом визите после обновления читает старые
ключи, вычисляет ближайший пресет (`compact`→`phone`, `wide`-ширина→`wide`,
иначе по `pixelIntensity`), пишет `screenPreset`, удаляет три старых ключа.
Одноразовое событие `screenpresetchange` (`modules/quiz-app/src/main/resources/templates/fragments/head.html:260`) уведомляет
модули настроек о смене без перезагрузки.

*localStorage — post-paint / константные (не влияют на первый кадр):*

| Ключ | Пишет/читает | Назначение |
|---|---|---|
| `fontScale` | `modules/quiz-app/src/main/resources/static/js/settings/personalization-bootstrap.js:29,47-48` | размер шрифта, применяется после загрузки модуля (см. §1, §7) |
| `cheatsheet-admin-token` (константа `ADMIN_TOKEN_KEY`, `modules/quiz-app/src/main/resources/static/js/core/constants.js:21`) | `modules/quiz-app/src/main/resources/static/js/ui/export.js:10-16` (`readStoredToken`/`storeToken`/`clearStoredToken`) | admin-токен для `/export` |
| `tabPanel:<область>` (шаблон-ключ, семейство) | `modules/quiz-app/src/main/resources/static/js/ui/tablist.js:37,57,92` (пишет/читает активную панель) + pre-paint-скан `modules/quiz-app/src/main/resources/templates/fragments/head.html:88-99` (собирает `style`-правило по всем найденным `tabPanel:*`, чтобы восстановить открытую вкладку до подключения `tablist.js`) | какая вкладка настроек была открыта последней, per-область |
| `quiz.session.count` | `modules/quiz-app/src/main/resources/static/js/settings/session-form.js:234-245` | последнее выбранное количество вопросов в конструкторе сессии |

*sessionStorage (отдельный от localStorage слой):*

| Ключ | Пишет/читает | Назначение |
|---|---|---|
| `recall-typed-answer` (константа `RECALL_ECHO_KEY`, `modules/quiz-app/src/main/resources/static/js/quiz/flashcard.js:7`) | пишет/читает/удаляет `flashcard.js:39,51,78` | эхо введённого ответа для сравнения с эталоном во флешкарт-режиме |
| `cycleFocusMain` (константа `FLAG`, `modules/quiz-app/src/main/resources/static/js/ui/cycle-focus.js:18`) | пишет `:36`, читает/удаляет `:44-45` | одноразовый флаг «после MPA-перехода посадить фокус на main» (A11Y-11) |

## 7. «Hooks» и жизненный цикл

React-хуков нет — стек не React. Рубрика миссии отображается на два механизма:

1. **Pre-paint инлайн-скрипт** в `<head>` (`modules/quiz-app/src/main/resources/templates/fragments/head.html:170-268`) —
   синхронно, до первого кадра, читает `localStorage` и расставляет
   `data-theme`/`data-density`/`data-reading-width`/`data-pixel`/`data-motion`
   атрибуты на `<html>`. Сделано намеренно: применение после отрисовки дало бы
   видимую перекладку (комментарий `head.html:197-199`).
2. **`modules/quiz-app/src/main/resources/static/js/core/lifecycle.js`** (42 строки) — плоский init/destroy-реестр
   ES-модулей: `registerLifecycle(name, {init, destroy})`, `runInit()` (вызов
   всех `init` на `DOMContentLoaded`, с try/catch на каждый — падение одного
   модуля не роняет остальные), `runDestroy()` (для bfcache/`pageshow`).
   `app-entry.js` — единственный вызывающий `runInit()`.

## 8. API layer

`modules/quiz-app/src/main/resources/static/js/core/api-client.js` (155 строк) — единая обёртка `apiFetch(url, options)`:

- CSRF: для мутирующих методов (`POST/PUT/PATCH/DELETE`, `api-client.js:4`)
  читает токен через `modules/quiz-app/src/main/resources/static/js/core/csrf.js` (`XSRF-TOKEN` cookie,
  `CookieCsrfTokenRepository`) и ставит заголовок `X-XSRF-TOKEN`; при
  отсутствии токена бросает `ApiError('CSRF-токен не найден…', {status:403,
  type:'CSRF_MISSING'})` до сетевого запроса (`api-client.js:78-84`).
- Таймаут: `DEFAULT_TIMEOUT_MS = 30_000` (`api-client.js:3`), реализован через
  `AbortController` + `setTimeout`; при истечении бросает
  `ApiError('Превышено время ожидания ответа сервера', {status:408})`
  (`api-client.js:107-111`), отличая это от внешней отмены (`externalSignal`).
- Ошибки: `normalizeProblemDetail()` (`api-client.js:41-61`) парсит RFC7807
  ProblemDetail-тело (`title`/`detail`/`type`/`instance`/`correlationId`/
  `fieldErrors`) в единый класс `ApiError`.
- Идемпотентность: заголовок `Idempotency-Key` сознательно не выставляется —
  комментарий `api-client.js:87-89` фиксирует, что ключ идемпотентности
  `/api/answer` передаётся полем тела `clientAttemptId`, а не HTTP-заголовком.

Полный список вызываемых эндпоинтов (`grep -rhoE "/api/[a-z/-]+"` по
`static/js/`) — все 11 маршрутов `InterviewApiController` (см. §2), без
расхождений.

## 9. Forms

Формы — обычный Thymeleaf `<form th:action="...">`, найдены в 10 файлах
(`grep -rl "th:action"`): `topics.html`, `stats.html`, `login.html`,
`bookmarks.html`, `result.html`, `settings.html`, `focus-training.html` и три
фрагмента (`shell-head.html`, `session-builder.html`, `sidebar.html`).

CSRF-токен как скрытое поле (`_csrf`) явно выведен в 6 из них:
`shell-head.html`, `sidebar.html`, `login.html`, `bookmarks.html`,
`settings.html`, `focus-training.html`.

**No-JS фоллбэк** — `result.html` (270 строк): `/answer` при обычной
form-отправке (без `fetch`) рендерит эту страницу целиком на сервере
(`MvcNavigationService.resultView()` → `"result"`); прогрессивное улучшение
переключает на JSON через `/api/answer`, если JS доступен.

`modules/quiz-app/src/main/resources/static/js/ui/form-guards.js` (75 строк) — два клиентских guard'а:
`initDangerousFormGuard()` (модальное подтверждение для форм с
`[data-confirm]`, использует `confirmModal()`) и `initSubmitOnceGuard()`
(защита от двойной отправки, интегрирована с `modules/quiz-app/src/main/resources/static/js/ui/pending.js`).

## 10. Validation

**Серверная** — Jakarta Bean Validation на DTO, `@Valid` в контроллере
(`InterviewMvcController.java:217,293`):
- `StartSessionRequest.java:29,44` — `@Positive` (count), `@Min(0)/@Max(600)`.
- `SubmitAnswerRequest.java:27,30,40,65-66,73` — `@Positive` (questionId,
  optionId, revisionId), `@Min(0)/@Max(5)` (confidence-подобное поле),
  `@Min(0)/@Max(QuizConstants.RESPONSE_TIME_MS_MAX)` (время ответа).
- Точечные `@RequestParam`-валидаторы прямо в контроллере: `@Positive long
  questionId` на `/skip`, `/bookmarks/note`, `/question/note`,
  `/bookmarks/remove`; `@Min(0) @Max(5) int grade` на `/flashcard-grade`.

**Клиентская** — не отдельный валидационный слой, а те же `form-guards.js`
(подтверждение опасных действий, защита от двойной отправки) плюс HTML5
required/pattern-атрибуты в формах настроек (`modules/quiz-app/src/main/resources/static/js/settings/session-form.js`
синхронизирует состояние формы, не подменяет серверную валидацию).

## 11. Styling

44 CSS-файла, **12069 строк** (`find … -name "*.css" | xargs wc -l`). Каскад
объявлен одной строкой в `tokens.css:4`:

```
@layer tokens, reset, shell, components, pages, utilities, pixel;
```

Единственный источник порядка и постраничных гейтов —
`css-bundle-manifest.txt` (161 строка, 43 непустые некомментарные записи —
остальное комментарии-обоснования порядка; читают его
`config/web/CssBundleService` и `ui/TemplateFragmentContractTest`). Формат
строки: `<гейт> <путь>`, где гейт
— `*` (все страницы) либо список значений `page` через запятую.

Файл размечен развёрнутыми комментариями-обоснованиями порядка (не голый
список): например, `components-pending.css` обязана идти сразу после
`components-controls.css` из-за равной специфичности селекторов
(`(0,3,1)` против самого себя) — переставить строку значит показать занятой
кнопке вид «сломана» вместо «идёт работа» (манифест, блок
`components-pending`).

Постраничные файлы (`pages-*.css`) на 100% заскоуплены под `.{page}-page` —
инвариант проверяет `scripts/css-leak-audit.py` (упомянут в манифесте у
`components-answer.css` и `components-filter-bar.css` как источник гарантии
`leak=0`).

Слой `pixel` (9 файлов: `pixel-fx`, `pixel-brand`, `pixel-buttons`,
`pixel-options`, `pixel-nav-badges`, `pixel-cards`, `pixel-result-banner`,
`pixel-animations`, `pixel-motion-parity`) идёт последним — только декор,
ничего структурного (`position`/`display`/`grid`), иначе перебил бы `shell`.

## 12. Design tokens

`modules/quiz-app/src/main/resources/static/css/tokens.css`, **376 строк**. Заголовок ссылается на внешний журнал
решений: `docs/css-decisions/tokens.md#tokens-e01` (`tokens.css:1-2`) — каждый
токен в файле снабжён комментарием с ссылкой на конкретный пункт этого
журнала (`tokens-e02` и далее).

- `color-scheme: dark` — тёмная тема дефолтная (`tokens.css:11`), light
  включается атрибутом `[data-theme="light"]` (реализовано на `tokens.css:267`);
  переключение — та же pre-paint логика `head.html`, что и у screen-preset.
- Группы токенов в `:root`, видимые в первых 40 строках: фон (`--color-bg-*`,
  5 переменных + `--color-bg-canvas-2` для градиента оболочки), текст
  (`--color-text-*`, 5 переменных), границы (`--color-border-*`, 3 переменные,
  включая отдельный контраст-таргетированный тон поля ввода —
  комментарий `tokens.css:33-40` про WCAG 1.4.11 3:1).
- Полный файл (`grep -c "^\s*--"`) содержит **193** объявления
  токенов/комментариев-заголовков секций — палитра явно многогранна (цвет,
  типографика, spacing, radii, motion, layering, судя по структуре
  `@layer tokens { :root { … } }`).
- Дублирование палитры вне токенов не проверялось статически (см. §18,
  требует grep по hex-литералам в 44 CSS-файлах — заявлено, но не выполнено,
  чтобы не раздувать отчёт числом ложных срабатываний без ручной проверки).

## 13. Assets

| Каталог | Содержимое | Якорь |
|---|---|---|
| `static/fonts/` | 20 файлов `.woff2` (Literata + Inter варианты) | `ls static/fonts/ \| wc -l` = 20 |
| `modules/quiz-app/src/main/resources/static/icons/pixel-icons.svg` | 27 строк, 25 `<symbol>` | `grep -c "<symbol>"` = 25 |
| `static/img/stickers/` | **10 растровых PNG** (code, coffee, book, cloud-upload, question, terminal, database, target, rocket, network) | `find static/img/stickers -name "*.png"` |
| `modules/quiz-app/src/main/resources/static/img/pixel/THIRD_PARTY_ASSETS.md` | Только документация лицензий — растровых PNG в этом подкаталоге нет | `find static/img/pixel` |
| `modules/quiz-app/src/main/resources/static/vendor/chart/chart.umd.min.js` | Chart.js, локальная поставка | — |
| `modules/quiz-app/src/main/resources/static/vendor/hljs/highlight.min.js` | highlight.js, локальная поставка | — |
| `modules/quiz-app/src/main/resources/static/vendor/mermaid/mermaid.min.js` | mermaid.js, локальная поставка | — |

**Уточнение к design.md**: формулировка «`static/img/pixel/` — только
`THIRD_PARTY_ASSETS.md`, растровых PNG нет» верна лишь для подкаталога
`img/pixel/`. Соседний `img/stickers/` содержит 10 PNG — растровые ассеты в
проекте есть, просто не в `pixel/`. Это уточнение факта, не критика дизайна.

Гейт целостности — `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/StaticAssetInventoryTest.java`: всё в `static/img`
обязано быть либо реально загружаемым (CSS/JS-ссылка), либо явно
задокументированным как не-ассет. Мотив в javadoc теста (`:24-33`) —
исторический прецедент двух мёртвых иконочных систем (12 КБ мёртвого спрайта
+ 6 одиночных SVG), обнаруженных только этим замером, не рантаймом.

## 14. Fonts и icons

- **Шрифты**: Literata (для чтения — прозаические блоки, markdown) + Inter
  (интерфейс — UI-текст, кнопки, лейблы). Поставляются локально (20 woff2 в
  `static/fonts/`), подключение — вероятно `modules/quiz-app/src/main/resources/static/css/fonts-local.css`
  (первая строка `css-bundle-manifest.txt`: `* /css/fonts-local.css`).
- **Иконки**: один пиксельный SVG-спрайт `modules/quiz-app/src/main/resources/static/icons/pixel-icons.svg`
  (25 символов), подключается фрагментом `modules/quiz-app/src/main/resources/templates/fragments/icons.html` (117 строк,
  включён во все 10 шаблонов). Lucide (line-art, viewBox 24, stroke-width 2)
  изгнан контрактным тестом `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/IconLanguageContractTest.java` — javadoc
  теста (`:20-30`) документирует исторический дефект «три словаря иконок
  одновременно» (Lucide-спрайт + `pixel-sprite.html` + вложенные одиночные
  SVG), устранённый до единого языка в iter-112.

## 15. Animations и motion

Три специализированных CSS-файла в слое `pixel`:

- `pixel-fx.css` — визуальные эффекты/декор.
- `pixel-animations.css` — сами анимации.
- `pixel-motion-parity.css` — гашение под `prefers-reduced-motion` и
  `data-motion="off"`; манифест явно требует его **последним** среди
  `pixel-*`-файлов — «иначе гасить будет нечего» (`css-bundle-manifest.txt`,
  комментарий у `pixel-motion-parity.css`).
- `prefers-reduced-motion` встречается в **7** файлах (`grep -rl` по
  `static/css`), не только в `pixel-motion-parity.css` — то есть отдельные
  компоненты дублируют собственные reduced-motion правила рядом со своей
  анимацией, а не полагаются целиком на общий гасящий файл.
- Клиентский тумблер — `localStorage['motion']`, читается/пишется в
  `modules/quiz-app/src/main/resources/templates/fragments/head.html` (см. §6), применяется атрибутом `data-motion` на
  `<html>` синхронно pre-paint.

## 16. Tests фронта

**Java UI-контракты** — `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/`,
**74 файла** (73 тестовых класса + 1 вспомогательный `CssRules.java`, не
тестовый класс сам по себе). Группировка по предмету (по имени файла):

- **Composition/layout** (11): `FocusWideCompositionContractTest`,
  `SummaryWideCompositionContractTest`, `RecallCompositionContractTest`,
  `UltrawideWorkspaceContractTest`, `PageWidthScaleContractTest`,
  `CatalogColumnsContractTest`, `BuilderRailContractTest`,
  `ModeWorkspaceContractTest`, `ErrorPageCompositionContractTest`,
  `FocusBandMeasureContractTest`/`ResultBandMeasureContractTest`.
- **Навигация/reach** (7): `NavigationSourceContractTest`,
  `SessionBuilderLaunchReachContractTest`, `FocusPrimaryActionReachContractTest`,
  `AnswerPageReachContractTest`, `SummaryRoutingContractTest`,
  `HotkeyRegionContractTest`, `KeyboardE2eContractTest`.
- **Компоненты-системы** (12): `TabComponentContractTest`/`TabPanelContractTest`,
  `FormControlComponentContractTest`, `DataTableComponentContractTest`,
  `ButtonRankComponentContractTest`, `EmptyStateSystemContractTest`,
  `PendingStateSystemContractTest`, `ModalTitleContractTest`,
  `DifficultyBadgeContractTest`, `RankEmblemContractTest`/`RankLadderContractTest`,
  `ProgressionContractTest`.
- **Иконки/палитра/pixel-art** (8): `IconLanguageContractTest`,
  `PixelCssPaletteTest`, `PrintPaletteContractTest`, `ReviewReasonPaletteTest`,
  `SectionStickerPaletteTest`, `StateIllustrationContractTest`,
  `SurfaceLanguageContractTest`, `StaticAssetInventoryTest`.
- **Шаблоны/фрагменты (структурный CSS)** (6): `TemplateFragmentContractTest`,
  `TemplateExpressionContractTest`, `PageHeadUnificationContractTest`,
  `DomIdUniquenessContractTest`, `HeadingScaleContractTest`,
  `PageTitleContractTest`.
- **Экран-специфика** (стats/summary/bookmarks/topics/settings, 29 файлов):
  `StatsSummaryContractTest`, `TopicBriefContractTest`, `TopicMapContractTest`,
  `BookmarksFilterContractTest`, `BookmarkTouchTargetContractTest`,
  `QuestionNoteContractTest`, `SessionBuilderStepsContractTest`,
  `SessionHistoryContractTest`, `SessionLogContractTest`,
  `SummarySealContractTest`, `SummaryTopContractTest`,
  `DailyGoalCopyContractTest`, `AchievementContractTest`,
  `AnsweredOrderContractTest`, `ReviewEmptyStateContractTest`,
  `MaterialCapContractTest`, `EnumLabelSourceContractTest`,
  `ReportCategorySourceContractTest`, `StudyReadingContractTest`,
  `RevealFocusContractTest`, `FocusHandoffContractTest`,
  `CssProseBudgetContractTest`, `DiagramCspContractTest`,
  `DiagramLegibilityContractTest`, `MobileContextContractTest`,
  `ScreenPresetContractTest`, `ShellChromeContractTest`, `PrintContractTest`,
  `VisualBaselineContractTest`.

**Playwright e2e** — `e2e/` (`playwright.config.js`, `testDir: '.'`,
`baseURL` из `E2E_BASE_URL` или `http://localhost:8080`):
- `keyboard-quiz.spec.js` (TEST-006) — полный цикл только с клавиатуры, оба
  движка. Документирует платформенную разницу: WebKit требует `Alt+Tab` вместо
  `Tab` для обхода интерактивных элементов (`keyboard-quiz.spec.js:6-13`).
- `visual-matrix.spec.js` (TEST-005) — визуальная матрица theme × pixel ×
  motion. Голдены — `e2e/visual-baselines/{chromium,webkit}/`, **28 PNG** (14
  на движок), `VISUAL_GOLDEN=1` включает сравнение со снапшотами.
- Два проекта: `chromium` (`Desktop Chrome`) и `webkit` (`Desktop Safari`) —
  раздельные baseline-директории объясняются разными шрифтовыми метриками
  движков (`playwright.config.js:21-23`).

**Чего не покрыто** (по инвентарю, без проверки живым запуском): Firefox не
входит ни в один project (`devices['Desktop Chrome']`/`['Desktop Safari']` —
оба десктопные, `playwright.config.js:21-23`); e2e-спек всего 2 (клавиатура +
визуальная матрица) — нет отдельного e2e для форм-фоллбэка (`result.html` без
JS) и для API-слоя (`/api/*`) как HTTP-контракта вне Java-интеграционных
тестов. Мобильные вьюпорты сами по себе покрыты: `visual-matrix.spec.js:79-83`
объявляет `VIEWPORTS` 375/768/1280 и применяет их через `setViewportSize`
(`:154-156`), голдены существуют (`home-dark-med-vp-375.png`, `vp-768`,
`vp-1280` в обеих baseline-директориях, входят в посчитанные 28 PNG) — но
только для одного экрана (`/` home) и только скриншотами. Настоящий пробел —
отсутствие мобильных вьюпортов для `settings`/`stats`/`train` и отсутствие
интеракционных (не только визуальных) e2e-проверок на узких ширинах.

## 17. Дельта против аудита 2026-08-22

Базовый документ: `docs/frontend-refoundation-audit.md` (511 строк), commit
создания `3ba1845cdd47e64d46e69c00cecb78abcc51b741`
(2026-08-22T18:41:01+03:00, `git log --diff-filter=A -- docs/frontend-refoundation-audit.md`).

- Коммитов между базовым SHA и HEAD: **105**
  (`git log --oneline 3ba1845c..77884ea5 | wc -l`).
- Файлов изменено во фронтенд-поверхности за это время: **100**
  (`git diff --stat 3ba1845c..HEAD -- modules/quiz-app/src/main/resources`),
  +39957/−42053 строк.
- design.md этой задачи (написан 2026-08-27) оценивал дельту в 127 коммитов
  от даты аудита (2026-08-22) без привязки к конкретному SHA; при пересчёте
  на SHA создания файла (а не календарную дату) число иное — **105** —
  потому что часть из «127» коммитов design.md, вероятно, считала от полуночи
  22-го, а не от 18:41, когда аудит появился в дереве. Оба числа — не
  противоречие, а разная точка отсчёта; для протокола фиксирую точную (SHA)
  версию.
- Инвентарный факт из аудита, разошедшийся с HEAD: аудит (§1) говорит про
  «банк 9818 вопросов» и «42 optionless-вопроса» — это **живые данные БД на
  момент аудита** (аудит явно помечен «работали по живому DOM… read-only SQL
  к живой БД»), не факт исходников; Phase 0 статическим чтением такие числа
  принципиально не проверяет (см. §18). Расхождение не подтверждено и не
  опровергнуто этим отчётом.
- Структурные факты аудита (§2 «Current visual language» и далее),
  затрагивающие `NavSection`, `screenPreset`-миграцию и CSS-манифест, при
  чтении HEAD **подтверждаются** тем же кодом, который цитирует аудит (файлы
  и номера строк, использованные в §4/§6/§11 этого отчёта, существуют на
  HEAD) — за 105 коммитов дельты архитектурные решения, описанные в аудите,
  не были откачены.

## 18. Белые пятна разведки

Статическое чтение принципиально не отвечает на следующее — нужен живой
рендер (см. Follow-ups в design.md):

1. **Фактическое визуальное состояние обеих тем на реальных вьюпортах**
   (1280/1440/1920/2560, light+dark) — этот отчёт перечисляет CSS-файлы и
   токены, но не видел ни одного отрендеренного пикселя. Проверка: bootRun на
   `SERVER_PORT=8091` + Playwright screenshot-прогон.
2. **Живое состояние БД** (число вопросов, optionless-вопросов, реальное
   распределение MCQ) — аудит 2026-08-22 брал эти числа из живой БД, Phase 0
   их не пересчитывал. Проверка: `docker compose up -d postgres` +
   `./gradlew bootRun` + прямой SQL/`/api/stats`.
3. **Реальный порядок каскада в браузере** — `css-bundle-manifest.txt`
   декларирует порядок и объясняет его комментариями, но ни один тест этого
   отчёта не проверял итоговый вычисленный стиль в DOM (только структурное
   наличие правил). Проверка: DevTools computed-styles на спорных селекторах,
   упомянутых в манифесте (`components-pending`, `components-filter-bar`).
4. **Дублирование палитры вне tokens.css** (§12, явно не выполнено) — нужен
   grep по hex-литералам в оставшихся 43 CSS-файлах с ручной проверкой
   каждого совпадения (автоматический grep даст много ложных срабатываний на
   валидные не-палитровые значения). Проверка: `grep -rhoE "#[0-9a-fA-F]{3,8}"
   static/css/*.css | sort | uniq -c`, затем ручная классификация.
5. **Пометка §11 «слой pixel ничего структурного не меняет»** — заявлено
   манифестом, но не проверено вычислением computed `position`/`display` в
   реальном DOM. Проверка: тот же живой Playwright-прогон, что и пункт 1.
6. **Actual runtime-поведение `/review` как режима, а не раздела** — код и
   javadoc `NavSection.java` описывают дизайн-намерение, но не проверяют
   статическим чтением, что подсветка активного раздела и счётчик ошибок в
   рельсе прогресса действительно работают на живой сессии. Проверка: живой
   клик по пути TRAINING → накопить ошибку → открыть рельс прогресса.

## Приложение A. Команды воспроизведения

```bash
# SHA / ветка / дата
git rev-parse HEAD
git branch --show-current

# Шаблоны и фрагменты
find modules/quiz-app/src/main/resources/templates -maxdepth 1 -name "*.html" | sort
wc -l modules/quiz-app/src/main/resources/templates/*.html | sort -n
ls modules/quiz-app/src/main/resources/templates/fragments/
wc -l modules/quiz-app/src/main/resources/templates/fragments/*.html | sort -n

# Роутинг
grep -n "@GetMapping\|@PostMapping" \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java
grep -n "@GetMapping\|@PostMapping" \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewApiController.java
grep -n 'return "' \
  modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/MvcNavigationService.java

# JS / CSS инвентарь
find modules/quiz-app/src/main/resources/static/js -name "*.js" | wc -l
find modules/quiz-app/src/main/resources/static/js -name "*.js" | xargs wc -l | tail -1
find modules/quiz-app/src/main/resources/static/css -name "*.css" | wc -l
find modules/quiz-app/src/main/resources/static/css -name "*.css" | xargs wc -l | tail -1
cat modules/quiz-app/src/main/resources/css-bundle-manifest.txt

# localStorage/sessionStorage-ключи: строковые литералы
grep -rhoE "(local|session)Storage\.(getItem|setItem|removeItem)\(['\"][A-Za-z0-9_.-]+['\"]" \
  modules/quiz-app/src/main/resources/static/js modules/quiz-app/src/main/resources/templates | sort -u

# localStorage/sessionStorage-ключи: обращения через константу/шаблонную строку
# (литеральный grep выше их не ловит — далее резолвить руками, см. §6)
grep -rnE "(local|session)Storage\.(getItem|setItem|removeItem)\(\s*[A-Za-z_][A-Za-z0-9_]*\s*[,)]|(local|session)Storage\.(getItem|setItem|removeItem)\(\`" \
  modules/quiz-app/src/main/resources/static/js modules/quiz-app/src/main/resources/templates

# API-эндпоинты, вызываемые с фронта
grep -rn "'/api\|\"/api\|/api/" modules/quiz-app/src/main/resources/static/js \
  | grep -oE "/api/[a-z/-]+" | sort -u

# Ассеты
ls modules/quiz-app/src/main/resources/static/fonts/ | wc -l
grep -c "<symbol" modules/quiz-app/src/main/resources/static/icons/pixel-icons.svg
find modules/quiz-app/src/main/resources/static/img -name "*.png"
find modules/quiz-app/src/main/resources/static/vendor -maxdepth 2

# Тесты
find modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui -name "*.java" | wc -l
find e2e -name "*.png" | wc -l
ls e2e/*.spec.js

# Дельта против аудита
git log --diff-filter=A --format="%H %ad" --date=iso-strict \
  -- docs/frontend-refoundation-audit.md
git log --oneline 3ba1845cdd47e64d46e69c00cecb78abcc51b741..HEAD | wc -l
git diff --stat 3ba1845cdd47e64d46e69c00cecb78abcc51b741..HEAD \
  -- modules/quiz-app/src/main/resources | tail -1

# Резолв всех path-якорей отчёта (path:line → path). Фильтр на "/" отсекает
# короткие контекстные упоминания файлов (напр. "head.html" после того, как
# полный путь уже назван абзацем выше) — якорем считается только то, что
# само по себе является путём.
grep -oE '`[a-zA-Z0-9_./-]+\.(java|html|css|js|txt|md|svg):?[0-9,-]*`' .sdlc/CS-20260827-001/report.md \
  | tr -d '`' | sed -E 's/:[0-9,-]+$//' | sort -u | grep '/' \
  | while read -r p; do test -e "$p" || echo "MISSING: $p"; done

# Дельта коммита задачи ограничена .sdlc/
git status --porcelain -uall
```
