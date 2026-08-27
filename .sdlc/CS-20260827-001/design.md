# CS-20260827-001: Phase 0 разведка фронтенда (framework, routing, компоненты)

## Контекст

Задача — research: единственный артефакт `.sdlc/CS-20260827-001/report.md`,
DoD-гейт `test -s .sdlc/CS-20260827-001/report.md`. Код не меняем.

Источник требования — миссия `cheat-sheet-product-rebuild.md:138-145`,
§5 PHASE 0 — REPOSITORY RECONNAISSANCE, блок FRONTEND. Шестнадцать рубрик:
framework; routing; components; pages; layouts; state; hooks; API layer; forms;
validation; styling; design tokens; assets; fonts; icons; animations; tests.
Смысл фазы — карта AS-IS перед редизайном, а не приговор («что чинить» — это
§7–§17 миссии и уже сделанный аудит, см. ниже).

**Что во фронтенде есть на HEAD `26cd3c50` (2026-08-27), измерено по репо:**

Стек — server-rendered Spring MVC + Thymeleaf, никакого SPA-фреймворка,
бандлера и npm-зависимостей в рантайме нет.
- Страницы-шаблоны: 10 файлов, `modules/quiz-app/src/main/resources/templates/*.html`
  (`focus-training.html` 1168 строк — самый крупный; `stats.html`, `settings.html`,
  `session-summary.html`, `topics.html`, `bookmarks.html`, `result.html`,
  `login.html`, `error.html`, `train.html`); суммарно 5632 строки.
- Фрагменты (переиспользуемые «компоненты»): 16 файлов в `templates/fragments/`
  (`head.html`, `shell-head.html`, `sidebar.html`, `mobile-nav.html`, `icons.html`,
  `session-builder.html`, `progression.html`, `today-widget.html`,
  `state-illustrations.html`, `post-answer-controls.html`, `training-actions.html`,
  `recoverable-banners.html`, `notice.html`, `result-zone-head.html`,
  `scripts.html`, `mermaid-init.html`).
- Роутинг — аннотации контроллеров, единый источник:
  `feature/interview/controller/InterviewMvcController.java` (страницы + form-POST:
  `/` :45, `/training` :82, `/review` :94, `/settings` :123, `/train` :188,
  `/start` :215, `/study-confirm` :223, `/flashcard-reveal` :234,
  `/flashcard-grade` :239, `/finish` :248, `/skip` :253, `/pause` :262,
  `/resume` :276, `/pause/discard` :281, `/session-summary` :286, `/answer` :291,
  `/settings/reset-options` :335, `/stats` :342, `/topics` :375, `/bookmarks` :383,
  `/bookmarks/note` :394, `/question/note` :411, `/bookmarks/remove` :420);
  `InterviewApiController.java` (JSON-слой: `/api/answer` :67, `/api/confidence` :79,
  `/api/streak` :87, `/api/stats` :96, `/api/topic-stats` :108, `/api/next` :116,
  `/api/favorite` :140, `/api/bookmark` :147, `/api/bookmark/note` :154,
  `/api/bookmark/remove` :161, `/api/report` :171);
  `feature/auth/controller/LoginController.java:35` (`/login`);
  `feature/export/controller/ExportController.java:32,46` (`/export`);
  `config/web/CssBundleController.java:34` (`/css/bundle/{page}.{hash}.css`).
  Первый уровень навигации — enum `NavSection` (бин `@nav`), его читают
  `fragments/sidebar.html` и `fragments/mobile-nav.html`.
- JS — 42 ES-модуля, 5512 строк, единая точка входа
  `templates/fragments/scripts.html:5` → `static/js/app-entry.js` (`type="module"`,
  CSP-nonce). Раскладка по доменам: `js/core/` (`api-client.js`, `csrf.js`, `dom.js`,
  `lifecycle.js`, `constants.js`), `js/ui/`, `js/quiz/`, `js/content/`, `js/stats/`,
  `js/settings/`, `js/summary/`. Клиентское состояние — localStorage-ключи
  `theme`, `density`, `fontScale`, `motion`, `pixelIntensity`, `readingWidth`,
  `screenPreset`, admin-token, ключ вкладки настроек; серверное — сессия
  интервью в БД. Реактивного стора/хуков нет — «hooks» рубрики миссии в этом
  стеке отображаются на pre-paint инлайн-скрипты `head.html` и lifecycle-хуки
  `js/core/lifecycle.js`.
- Стили — 44 CSS-файла, 12069 строк, каскад через `@layer`
  (tokens, reset, shell, components, pages, utilities, pixel). Порядок и гейты
  страниц — единственный источник `modules/quiz-app/src/main/resources/css-bundle-manifest.txt`,
  который читают `config/web/CssBundleService.java` (склейка одного бандла на
  страницу + content-hash + `immutable`) и `ui/TemplateFragmentContractTest`.
  Токены — `static/css/tokens.css` (376 строк, двухтемная палитра
  «мастерская гильдии»).
- Ассеты: `static/fonts/` — 20 локальных woff2 (Literata + Inter),
  `static/icons/pixel-icons.svg` — монохромный пиксельный спрайт,
  `static/img/pixel/` (только `THIRD_PARTY_ASSETS.md` — растровых PNG нет),
  `static/img/stickers/`, `static/vendor/` — три вендорные библиотеки
  (`chart`, `hljs`, `mermaid`), поставляются локально.
- Тесты фронта: 74 контрактных теста в
  `modules/quiz-app/src/test/java/com/cheatsheet/quiz/ui/` (шаблоны, фрагменты,
  иконки, ассеты, композиции экранов) + Playwright `e2e/` (2 спеки:
  `visual-matrix.spec.js`, `keyboard-quiz.spec.js`; 28 golden-PNG в
  `e2e/visual-baselines/<project>/`; проекты chromium+webkit,
  `e2e/playwright.config.js`).

**Что уже сделано и чего дублировать нельзя.** `docs/frontend-refoundation-audit.md`
(130 КБ, 2026-08-22) — Phase A аудит: 144 сырых находки, бэклог 108 строк,
оценочные суждения по 8 направлениям. Он отвечает на вопрос «что плохо»,
Phase 0 отвечает на «что есть». С даты аудита в ветке **127 коммитов**
(`git log --since=2026-08-22 --oneline | wc -l` = 127), то есть его инвентарная
часть частично протухла — отчёт обязан фиксировать факты по HEAD, а не
пересказывать аудит.

**Ограничения.** Дельта задачи допустима только внутри `.sdlc/CS-20260827-001/`
(предыдущий раунд был запаркован по `assert-main-tree-touched`). Порт 8080 —
живой bootRun пользователя, занимать нельзя. Репозиторий параллельно правят
другие агенты: отчёт обязан быть привязан к конкретному SHA.

## Решение

Пишем `.sdlc/CS-20260827-001/report.md` — **инвентарную карту фронтенда по 16
рубрикам §5**, полученную статическим чтением репозитория на зафиксированном SHA.

Правила содержания (обязательны для каждой рубрики):
1. **Факт + якорь.** Каждое утверждение сопровождается `path:line` либо
   воспроизводимой командой подсчёта. Утверждение без якоря не пишем.
2. **Инвентарь, а не критика.** Оценки, приоритеты, «надо переделать» — вне
   скоупа; максимум — нейтральная пометка «источник правды / дубль / мёртвый
   код», подтверждённая ссылкой.
3. **Числа проверяемы.** Все счётчики (файлы, строки, маршруты, тесты)
   приводятся вместе с командой, которой получены, чтобы следующий агент
   пересчитал за секунду.
4. **Привязка ко времени.** В шапке — SHA HEAD, дата, ветка; в конце — секция
   дельты против аудита 2026-08-22.

Структура `report.md` (жёсткая, порядок сохранять):

```
# Phase 0 — Frontend Reconnaissance (CS-20260827-001)
Шапка: SHA HEAD, дата, ветка, метод (статическое чтение репо, приложение не поднималось)
## 0. Сводка одним экраном      ← таблица: рубрика → 1 строка факта → главный якорь
## 1. Framework и рендер-модель  ← Spring MVC + Thymeleaf, отсутствие SPA/бандлера/npm-рантайма,
                                    CSP-nonce, pre-paint инлайн-скрипты
## 2. Routing                    ← полная таблица маршрутов: URL | метод | контроллер:line |
                                    view/DTO | назначение; отдельно редиректы и /api
## 3. Pages                      ← 10 шаблонов: путь, строк, контроллер-владелец, CSS-гейт page=
## 4. Layouts и shell            ← shell-head/sidebar/mobile-nav, NavSection, сетки, брейкпоинты
## 5. Components (фрагменты)     ← 16 фрагментов: что инкапсулирует, кто включает (th:replace)
## 6. State                      ← серверное (сессия/БД) vs клиентское (localStorage-ключи),
                                    кто пишет и кто читает каждый ключ
## 7. «Hooks» и жизненный цикл   ← js/core/lifecycle.js, pre-paint, делегирование событий;
                                    явно: React-хуков нет, это отображение рубрики на стек
## 8. API layer                  ← js/core/api-client.js + csrf.js, список вызываемых эндпоинтов,
                                    обработка ошибок/таймаутов
## 9. Forms                      ← формы Thymeleaf, CSRF, no-JS фоллбэки (result.html), form-guards
## 10. Validation                ← клиентская (js) vs серверная (@Valid/DTO/api/validation)
## 11. Styling                   ← @layer-каскад, css-bundle-manifest.txt, CssBundleService,
                                    content-hash, постраничные гейты; карта 44 файлов по слоям
## 12. Design tokens             ← tokens.css: группы токенов, две темы, что темизируется,
                                    где палитра дублируется вне токенов
## 13. Assets                    ← fonts/ icons/ img/ vendor/, размеры, THIRD_PARTY_ASSETS.md,
                                    StaticAssetInventoryTest как гейт
## 14. Fonts и icons             ← Literata/Inter (роли, 20 woff2, fonts-local.css),
                                    pixel-icons.svg спрайт + IconLanguageContractTest
## 15. Animations и motion       ← pixel-animations/pixel-motion-parity/pixel-fx, motion-токены,
                                    prefers-reduced-motion, localStorage motion
## 16. Tests фронта              ← 74 ui-контракта (сгруппировать по предмету),
                                    e2e (2 спеки, 28 голденов, chromium+webkit), чего НЕ покрыто
## 17. Дельта против аудита 2026-08-22 ← что из инвентарных утверждений аудита разошлось с HEAD
                                    (127 коммитов); только факты расхождения, без переоценки находок
## 18. Белые пятна разведки      ← что осталось неизвестным статическим методом и требует
                                    живого рендера (перечислить конкретно, с гипотезой проверки)
## Приложение A. Команды воспроизведения ← все счётчики одной копипастой
```

Метод сбора: `git log/ls-files`, `find|wc`, `grep` по аннотациям и `th:fragment`/
`th:replace`, чтение `css-bundle-manifest.txt`, `tokens.css`, `app-entry.js`,
`playwright.config.js`, каталога `ui/`-тестов. Приложение не поднимается.

Объём: 350–700 строк. Русский язык, короткие абзацы и таблицы (правила репо).

Отвергнутые альтернативы:
- Поднимать приложение на 8091 и снимать DOM/скриншоты — Phase 0 инвентаризует
  исходники, живые наблюдения дают §7 миссии; загрузка среды не окупается, а
  риск занять ресурсы и породить артефакты вне `.sdlc/` реален (YAGNI).
- Строить graphify-граф под отчёт — пишет `graphify-out/` в рабочее дерево,
  то есть нарушает дельта-ограничение задачи.
- Копировать инвентарь из `docs/frontend-refoundation-audit.md` — устарел на
  127 коммитов и смешивает факты с оценками.
- Класть отчёт в `docs/` — вне разрешённой дельты и вне DoD-гейта.

## Acceptance Criteria

1. Файл `.sdlc/CS-20260827-001/report.md` существует и непуст (DoD-гейт
   `test -s` зелёный).
2. В отчёте присутствуют все секции `## 0`…`## 18` и `## Приложение A` в
   указанном порядке; ни одна из 16 рубрик §5 FRONTEND не пропущена.
3. Шапка содержит SHA HEAD, дату и ветку; в §17 явно назван базовый SHA/дата
   сравнения (аудит 2026-08-22) и число коммитов дельты.
4. Каждая рубрика §1–§16 содержит минимум один якорь `path:line` или команду
   подсчёта; таблица маршрутов §2 покрывает все `@GetMapping/@PostMapping`
   из `InterviewMvcController`, `InterviewApiController`, `LoginController`,
   `ExportController`, `CssBundleController`.
5. Все пути, упомянутые в отчёте, существуют на HEAD (проверяется командой из
   Приложения A: каждый `path` резолвится `test -e`).
6. Дельта коммита задачи ограничена `.sdlc/CS-20260827-001/**` —
   `git status --porcelain -uall` не показывает изменений вне этого каталога.
7. Отчёт на русском; объём 350–700 строк.
8. §18 перечисляет не менее трёх конкретных белых пятен с указанием, каким
   наблюдением их закрыть.

## Тест-контракт

Автоматизированных тестов задача не добавляет и не меняет: код не трогается,
проверяемость обеспечивает custom-гейт DoD и самопроверка ссылок.

### unit · — (класс под тестом: —)
- [none] новых/переписанных JUnit-тестов нет — research-задача, дельта только в
  `.sdlc/`; добавление теста ради формы нарушило бы ограничение дельты.
  Стадию `test-writer` пропустить.

Проверки вместо тестов (выполняет implementer, вывод вклеивает в Приложение A):
- `test -s .sdlc/CS-20260827-001/report.md` — DoD-гейт (red_proof зафиксирован).
- Резолв якорей: извлечь из отчёта пути вида `modules/...`, `e2e/...`,
  `docs/...` и прогнать `test -e` — ноль ненайденных.
- `git status --porcelain -uall` — пусто вне `.sdlc/CS-20260827-001/`.

## Риски

- **Дрейф репо во время работы.** Другие агенты коммитят параллельно; отчёт без
  SHA станет ложью. Митигация — SHA в шапке и запрет «сглаживать» расхождения.
- **Сползание в аудит.** Соблазн писать «это плохо» вместо «это есть» раздувает
  отчёт и дублирует Phase A. Митигация — правило 2 и приёмка §2 AC.
- **Протухшие числа.** Счётчики без команд невозможно перепроверить. Митигация —
  Приложение A.
- **Выход дельты за `.sdlc/`.** Любой запуск gradle/graphify/playwright создаёт
  файлы в дереве и паркует задачу (прецедент — раунд 1). Митигация — метод
  статический, сборка и рендер запрещены.
- **Объём.** 16 рубрик × подробности легко дают 1500 строк нечитаемого текста.
  Митигация — верхняя граница 700 строк и §0 «сводка одним экраном».

## Follow-ups

- Провести Phase 0 разведку бэкенда (§5 BACKEND: контроллеры, сервисы, репозитории, DTO, домен, валидация, ошибки, персистентность, миграции, кэш, транзакции) — отчёт `.sdlc/<ID>/report.md`, DoD custom-гейт.
- Провести Phase 0 разведку инфраструктуры и тестирования (§5 INFRASTRUCTURE + TESTING: сборка, деплой, конфигурация, логирование, мониторинг; unit/integration/E2E/visual/a11y/API-покрытие) — отчёт `.sdlc/<ID>/report.md`.
- Закрыть «белые пятна» §18 фронт-разведки живым наблюдением (bootRun на 8091 + Playwright, обе темы, вьюпорты 1280/1440/1920/2560) — отчёт со скриншотами в `.sdlc/<ID>/`.
