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

### settings (маппинг R0.25)
- Паритет: вкладки (JS: одна панель; no-JS: стопка всех трёх), set-card сетка осей
  320→2560 (auto-fit), switch-тогглы (фокус-кольцо на input:focus-visible),
  font-stepper, экспорт (кнопки живы, X-Admin-Token fetch), danger-zone,
  data-default-count при смене режима (EXAM→20, MARATHON→50), TRAINING скрывает
  счётчик.
- Контракт-тест обязателен после любой правки шаблона.

### shell (маппинг R0.26)
- Паритет на ЛЮБОЙ странице: masthead, нав aria-current, три PE-тоггла (навести
  на design-toggle: title называет «Instrument» — фикс R0.27), back-to-top после
  1.5 экрана (уважает data-motion), kbd-help по `?`, noscript-алерт (при выкл. JS).
- 375: нав-строка без переполнения (drawer нет — переносы).

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
