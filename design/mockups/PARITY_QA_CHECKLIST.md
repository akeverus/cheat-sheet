# PARITY QA CHECKLIST — Фаза F (production parity, подготовлен R0.28)

> Готовый прогон для Фазы F: после применения каждого порта (Фаза E) выполняется
> соответствующий блок отсюда. Цель — доказуемый паритет прод ↔ макет (screenshot +
> DOM/computed-метрики), без повторного изобретения процедуры на каждом тике.

## 0. Предусловия запуска (грабли уже собраны)

1. **Postgres:** `docker compose up -d postgres`. Порт сверить фактически: последняя
   фиксация — **5432** (память 2026-07-08; старая заметка про 5433 устарела, но при
   отказе коннекта проверить оба). `bootRun` может требовать явный
   `SPRING_DATASOURCE_URL`.
2. **Запуск:** `./gradlew bootRun` (без AI-ключей: сидер-режим, `aiEnabled=false`).
   **НЕ запускать** параллельную gradle-сборку/тесты при живом devtools-bootRun (wedge).
3. **Live-правки статики при bootRun:** `cp` source → `build/resources/main/...`
   (css/js/шаблоны), иначе правки не видны без рестарта.
4. **Кэш-бастинг:** после правки tokens.css/base.css бампить **ОБА** `?v=N` в
   head.html (4 строки). После app.js — его `?v=`.
5. **Chrome MCP:** вьюпорт 320 — только через `emulate` (`320x900x2`), окно ниже
   ~485px не ужимается; `evaluate_script` умирает на навигации — ходить через
   `navigate_page`. Скрины fullscreen, не 1280×720.
6. **Instrument включается клиентски:** localStorage `design=instrument` (или через
   /settings → Оформление → Instrument; или циклом #design-toggle). SSR-дефолт
   editorial НЕ трогаем.

## 1. Матрица прогона (на КАЖДЫЙ применённый порт)

Оси: **дизайн** instrument (+ editorial как контроль не-регрессии) × **тема**
light/dark × **вьюпорты** 375 / 768 / 1280 / 1440 / 1728 / 1920 / 2560 (DPR2) +
**320 reflow** (emulate) + **640** (zoom200-эквивалент @1280).

Метрики (evaluate_script, фиксировать в отчёт тика):
- `document.documentElement.scrollWidth <= clientWidth` (reflow, кроме легитимных
  скролл-контейнеров: pre, .topic-table-wrap, tablist);
- computed: font-family заголовка/тела (Newsreader/Space Grotesk пришли?), цвет
  фона body, границы карт (--color-border-primary применился?);
- gutters симметричны, chrome span 100vw;
- фокус-кольца видимы (`:focus-visible`) на 3 контролах страницы;
- AA спот-чек: злейшие пары страницы из маппинга (aa_shell.py, обе темы).

## 2. Пер-поверхностные сценарии доезда и точки паритета

### error (черновик C7-i)
- Доезд: прямой GET несуществующего пути (404); 403 — протухший _csrf POST.
- Паритет: левая композиция (instrument) vs центр (editorial-контроль); ghost-номер
  clamp; details открыт/закрыт; actions flex-start; 320 без переполнения clamp-номера.
- **Численный baseline ДО порта (R0.34, 404 @1280, живой инстанс):**
  - *editorial light (контроль — обязан остаться бит-в-бит):* card center/center,
    eyebrow uppercase `rgb(201,100,66)` 12px JetBrains Mono, code 64px/700/lh64
    `rgb(104,103,96)` Lora, title 36px/600/max-width none Lora `rgb(20,20,19)`,
    actions center gap 12, masthead relative `rgb(250,249,245)` border
    `rgb(209,207,197)`, bodyBg `rgb(250,249,245)`, reflow 1280/1280.
  - *editorial dark:* bodyBg `rgb(38,38,36)`, code `rgb(162,159,148)`, title
    `rgb(240,238,230)`, eyebrow `rgb(217,119,87)`, masthead border `rgb(62,60,54)`.
  - *instrument token-only ДО порта (light):* bodyBg `oklch(0.985 0.003 262)`,
    eyebrow uppercase teal `oklch(0.435 0.105 205)`, code 64px/700 Newsreader
    `oklch(0.5 0.012 264)`, title 36px/600/none, card center/center, actions center.
  - *Ожидаемая дельта ПОСЛЕ C7-i (только instrument):* card → flex-start/left;
    eyebrow → lowercase + text-tertiary; code → weight 500, size clamp(4rem,
    2rem+12vw, 8.5rem) = 136px @1280, lh 0.95, цвет --color-border-primary;
    title → weight 500, max-width 20ch; actions → flex-start. Editorial-числа
    выше — НЕ меняются ни на бит.

### session-summary (черновик C6-i)
- Доезд: настроить сессию EXAM (малое N) → отвечать → **POST /finish с _csrf в теле**
  → /session-summary. Summary одноразовая — НЕ reload-ить (redirect:/). TRAINING
  сессии не создаёт!
- Паритет: карты bg-tertiary/line; mono-цифры плиток (tabular-nums); тихие заголовки
  секций; lowercase mode-line; hover ошибок paper+accent; @media print (browser
  print-preview — MCP не эмулирует): хром/кнопки скрыты, 1 колонка, рамки таблицы.
- Регресс-чек: PE-инъекция .summary-tools (copy/print работают), a11y-роли таблицы
  на месте (role=table/rowgroup/columnheader/row/cell/rowheader).

### focus-training (порт после разблокировки, маппинг R0.22)
- Состояния: active (варианты+клавиатура 1-9/↑↓/Esc/Enter), AJAX-разбор (вердикт→
  уверенность→кнопка→sink #extra-analysis-content), flashcard browse (details) и
  сессионная (FLASHCARD: reveal POST → grades 1-4), study-learn (STUDY→LEARN),
  empty 4 ветки (+done finished).
- Паритет: topbar (НЕ рельс!), CSS-счётчик буквы опции (нет дубля букв), прогресс
  data-progress, mermaid в ответах жив.
- Контракт: TemplateFragmentContractTest + ручной no-JS смоук (формы работают).
- **Численный baseline focus ДО порта (R0.38, GET / @1280, active-MCQ 4 опции,
  settle 400ms, reflow чист 1265/1265):**
  - *Общее:* вопрос 48px/600 serif lh 55.2; опция-карта p16 18px display:grid;
    submit 16px p12×32; progress-полоса h2 accent.
  - *editorial (контроль):* вопрос Lora `rgb(20,20,19)`; опция bg
    `rgb(240,238,230)` r8 border `rgb(209,207,197)` (dark `rgb(48,48,46)`/
    `rgb(62,60,54)`); submit (disabled-старт) bg `rgb(227,218,204)` text
    `rgb(94,93,89)`, radius `0 0 8px 8px` — ПРИКЛЕЕН к низу карты; UI Inter;
    progress `rgb(217,119,87)`.
  - *instrument token-only:* вопрос Newsreader; опция bg `oklch(0.958 0.004 262)`
    r10 (dark `oklch(0.224 0.014 264)`); submit bg `oklch(0.998 0.002 262)` r10
    (dark `oklch(0.262 0.016 264)`) — ВНИМАНИЕ: полный r10 вместо editorial
    «приклеенного» `0 0 8 8` — при порте focus решить композицию кнопки
    (в макете submit отделён от карты) и НЕ оставлять полускруглый гибрид;
    UI Space Grotesk; progress teal `oklch(0.47 0.115 205)` / dark
    `oklch(0.8 0.115 205)`.
  - *Не сматчились на активном состоянии:* `.topic-badge`, `.session-counter`,
    `details`-подсказка (вероятно рендерятся не во всех состояниях/иная
    разметка) — фактические классы из served DOM на parity; буквы опций
    (CSS-counter `::before`) мерить через getComputedStyle(el,'::before').

### result (no-JS фоллбэк, маппинг R0.23)
- Доезд: отключить JS (chrome MCP: page settings / CDP Emulation.setScriptExecutionDisabled)
  → ответить на вопрос формой → полная страница /answer.
- Паритет: status verdict, role=list опции с пер-опционными объяснениями, SM-2
  details, related-questions в правой рейке ≥1200 (:has-грид цел!), сессия-бар.
- Регресс-чек находок: btn-regenerate НЕ рендерится (ожидаемо, aiEnabled=false);
  .question-side тоже НЕ рендерится — ИЗВЕСТНЫЙ дефект-кандидат, в parity не чинить.

### stats (маппинг R0.24)
- **Каверза cold-start (замечено R0.31):** после wipe базы графики в легитимном
  empty-фолбэке («Пока нет активных тем…» — НЕ дефект, canvas скрыт осознанно;
  Chart.js 4.5.0 загружен, JSON парсится). Для parity-замера ЦВЕТОВ графиков
  сначала ответить на 3–5 вопросов по 2+ темам, иначе мерить нечего.
- Паритет: Chart.js графики живы (topicStatsJson распарсился — при мёртвых графиках
  виден фолбэк-текст «График временно недоступен» = СТОП, откат; НЕ путать с
  cold-start фолбэком выше); допустимый рассинхрон: цвета графиков ещё
  editorial (stats.js шаг 1 не трогает) — зафиксировать скрином как known;
  сортировка (aria-sort + анонс), collapse >12 строк, stacked-card на 375,
  формы фильтров/поиска, forecast «Сегодня/Завтра», gaps.
- Cold-start чек: /stats с пустой фильтрацией → stats-empty карта.
- **Численный baseline stats ДО порта (R0.37, GET /stats @1280, settle 400ms,
  reflow чист 1265/1265; collapse tr[hidden] живьём: 319 строк / 307 hidden):**
  - *Общее (4 состояния):* stat-card p12×16; stat-value 28px/600 serif
    tabular-nums; stat-label 12px uppercase; section-title 28px/600 serif;
    таблица 14px, th 12px uppercase mono border-bottom 2px, td p12 border 1px;
    фильтр-инпут 16px; fallback 14px.
  - *editorial (контроль):* карты `rgb(250,249,245)` r8 (dark `rgb(38,38,36)`);
    serif Lora; ls 1.08; th bg `rgb(240,238,230)` / dark `rgb(48,48,46)`;
    td-границы `rgb(224,221,211)` / dark `rgb(52,50,45)`; инпуты r0; expander r0.
  - *instrument token-only:* карты `oklch(0.985 0.003 262)` r10 (dark
    `oklch(0.19 0.012 264)`); serif Newsreader; ls 0.24; th bg
    `oklch(0.958 0.004 262)` / dark `oklch(0.224 0.014 264)`; td-границы
    `oklch(0.912 0.006 262)` / dark `oklch(0.31 0.014 264)`; инпуты r6;
    expander r0 — ОСОЗНАННО (WIP-коммент B5 «плоский радиус во всех дизайнах»),
    при порте НЕ «чинить».
  - *Селекторы, не сматчившиеся в живом DOM (уточнить перед parity):*
    `.topic-link`, `#filters-form`, `.forecast-list li`, `.forecast-count`
    (разметка отличается от ожиданий маппинга — брать фактические классы
    из served DOM).

### settings (маппинг R0.25)
- Паритет: вкладки (JS: одна панель; no-JS: стопка всех трёх), set-card сетка осей
  320→2560 (auto-fit), switch-тогглы (фокус-кольцо на input:focus-visible),
  font-stepper, экспорт (кнопки живы, X-Admin-Token fetch), danger-zone,
  data-default-count при смене режима (EXAM→20, MARATHON→50), TRAINING скрывает
  счётчик.
- Контракт-тест обязателен после любой правки шаблона.
- **Численный baseline settings ДО порта (R0.36, GET /settings @1280, вкладка
  «Оформление» открыта кликом, settle 400ms, reflow чист 1265/1265):**
  - *Общее (4 состояния):* tablist flex gap4 border-bottom 1px; активная вкладка
    18px, подчёрк 2px accent; card r16 p32; seg-control inline-flex gap8;
    seg-кнопка 16px; label 18px; hint 14px.
  - *editorial (контроль бит-в-бит):* UI-шрифт **Inter**; card
    `rgb(240,238,230)` / dark `rgb(48,48,46)`; активная seg-кнопка bg
    `rgb(217,119,87)` + text `rgb(20,20,19)` (ОБЕ темы), radius 0; secondary-btn
    r0; danger `rgb(179,38,30)` / dark `rgb(236,133,128)`; tab-подчёрк
    `rgb(217,119,87)`.
  - *instrument token-only:* UI-шрифт **Space Grotesk** (уже применяется); card
    `oklch(0.958 0.004 262)` / dark `oklch(0.224 0.014 264)`; активная seg-кнопка
    bg teal `oklch(0.47 0.115 205)` + text `oklch(0.99 0.01 205)` r6 (dark: bg
    `oklch(0.8 0.115 205)` + text `oklch(0.17 0.02 258)`); secondary-btn r10;
    danger `oklch(0.5 0.2 27)` / dark `oklch(0.72 0.175 27)`; tab-подчёрк teal.
  - *Уточнить при parity:* селекторы `.switch input` и `.set-card` на живой
    странице НЕ сматчились (switch-тогглы/сет-карты размечены иначе) — перед
    замером switch-фокус-колец найти фактические классы в served DOM.

### shell (маппинг R0.26)
- Паритет на ЛЮБОЙ странице: masthead, нав aria-current, три PE-тоггла (навести
  на design-toggle: title называет «Instrument» — фикс R0.27), back-to-top после
  1.5 экрана (уважает data-motion), kbd-help по `?`, noscript-алерт (при выкл. JS).
- 375: нав-строка без переполнения (drawer нет — переносы).
- **Mobile-375 baseline (R0.39, GET / active-MCQ с progress-полосой, оба дизайна
  light; layout БИТ-ИДЕНТИЧЕН между дизайнами, reflow чист 375/375):**
  - HDR-1 compact-clamp ЖИВ: masthead-title **18.75px** (=5vw@375, в клампе
    1.15–1.35rem) при вопросе **27px** → ratio 1.44 ≥ 1.25;
  - :has(.ed-masthead-progress)-уплотнение ЖИВО: inner padding `8px 16px`,
    row-gap 4px; kicker 10.88px;
  - нав одной строкой (3 ссылки, y=93), тогглы в верхнем ряду (136×40@223,8);
  - опция p12 16px; submit full-width 327×48 (≥44px touch);
  - instrument-порт focus/shell обязан СОХРАНИТЬ эти мобильные числа
    (кламп/уплотнение — прод-контракт, тест пиннит строки).
- **МЕТОДОЛОГИЯ (каверза R0.35):** у `.ed-nav-link`/тогглов `transition: color .15s` —
  после флипа data-theme/data-design ждать ≥400ms до чтения computed-цветов,
  иначе ложные диффы (значения замораживаются на прежней теме). Стабильные
  свойства (bg/border masthead, kicker, title) читаются сразу.
- **Численный baseline shell ДО порта (R0.35, GET / @1280, живой инстанс):**
  - *Общее для всех 4 состояний:* masthead `position: relative` (sticky ЗАПРЕЩЁН),
    inner padding `12px 50.6px` max-width 1280 gap 16; kicker 12px uppercase mono;
    title 36px lh 41.4; nav-link 12px uppercase mono, hit-area 44px; тогглы 44×44
    r999; progress-полоса h2.
  - *editorial light:* bg/masthead `rgb(250,249,245)`, border `rgb(209,207,197)`,
    kicker `rgb(94,93,89)` ls 1.08, title Lora 500 `rgb(20,20,19)`, nav
    `rgb(20,20,19)`, active border-bottom `2px rgb(217,119,87)`.
  - *editorial dark:* masthead `rgb(38,38,36)`, border `rgb(62,60,54)`, kicker
    `rgb(197,193,180)`, title/nav `rgb(240,238,230)`, active `rgb(217,119,87)`.
  - *instrument light (token-only):* masthead `oklch(0.985 0.003 262)`, border
    `oklch(0.876 0.007 262)`, kicker teal `oklch(0.435 0.105 205)` ls 0.24, title
    Newsreader **700** `oklch(0.245 0.015 264)`, active `oklch(0.47 0.115 205)`.
  - *instrument dark:* masthead `oklch(0.19 0.012 264)`, border
    `oklch(0.36 0.015 264)`, kicker `oklch(0.76 0.11 205)`, title/nav
    `oklch(0.94 0.008 258)`, active `oklch(0.8 0.115 205)`.
  - *Кандидат дельты порта shell:* title weight 700→500 (в макете shell брендинг
    medium — сверить с mockup при порте); остальное instrument-состояние уже
    близко к языку. Editorial-числа — контроль бит-в-бит.

## 3. Порядок применения при разблокировке (сжатый план Фазы E)

1. `git status --short` — тройная проверка целевых файлов.
2. error C7-i → вставка после §C7 + бамп v= → блок QA «error» отсюда → коммит →
   удалить черновик.
3. summary C6-i → после §C6 + бамп → блок QA «session-summary» → коммит → удалить черновик.
4. Дальше по очереди маппингов: focus → result → stats → settings → shell
   (каждый порт = отдельный тик: правка + свой QA-блок + коммит explicit pathspec).
5. Editorial-контроль после каждого порта: instrument-блоки скоупнуты
   `html[data-design="instrument"]` → editorial обязан быть бит-в-бит прежним
   (спот-скрин 1280 light достаточно).

## 4. Выходные артефакты каждого QA-прогона

- Строка в PIXEL_QA_MATRIX (parity: TODO→PASS/FAIL с датой тика);
- скрины в scratchpad (не коммитить), метрики в отчёт тика;
- журнал CRITIQUE cr.N (что мерил, что нашёл, что отложено);
- при FAIL — фикс в том же тике если ≤малой правки CSS, иначе отдельная запись.
