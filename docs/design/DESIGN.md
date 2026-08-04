# DESIGN.md — quiz-app (cheat-sheet interview trainer)

> **Status:** актуальная спека UI (Instrument + terminal handoff-3, 2026-07).
> Язык интерфейса — русский; имена файлов/токенов/классов — английские.

---

## 0. Что мы строим

Тренажёр для подготовки к собеседованиям: reading-heavy Q&A (markdown), код
(highlight.js), диаграммы (mermaid), флешкарты (SM-2), сессии/прогресс/стрик,
статистика (chart.js). Аудитория — разработчики. **Главная задача —
безупречные читаемость и UX.**

**Единственный скин:** `html[data-design="instrument"]` (SSR, без переключателя).
`data-theme` ∈ {light, dark} — ортогональная ось (localStorage `theme`, дефолт dark).

**Шрифты runtime** (Google Fonts в `head.html`, токены `:root`):
- **Inter** — display + body (`--font-family-display`, `--font-family-body`)
- **JetBrains Mono** — код, эйбрау, mono-лейблы (`--font-family-mono`)

Pixel-слой **не** подменяет body/display шрифты — только иконки, спрайты, FX.

### Пиксельный хром (arcade overlay)

Поверх Instrument-скина живёт декоративный слой `pixel-fx` (`static/css|js/pixel-fx.*`,
`static/img/pixel/*`):

- **Текст и шрифты** — только design-system (не pixel-typeface).
- **Пиксель** — functional SVG sprite (`fragments/pixel-sprite.html`, `currentColor`) + PNG-спрайты/FX.
- **Интенсивность** — `html[data-pixel="low|med|high"]` (localStorage `pixelIntensity`,
  дефолт `med`). CRT/scanlines/crumbs/trail — только на `high`.
- **Motion** — уважает `prefers-reduced-motion` и `data-motion=off`.
- Переключатель — Настройки → Персонализация; API `window.__pixelIntensity` /
  `window.__pixelFx`.

#### Intensity matrix

| FX / слой | low | med | high |
|-----------|-----|-----|------|
| Functional icons (SVG `<use>`, semantic `color`) | ✓ | ✓ | ✓ |
| Chunky кнопки / MCQ chrome | ✓ | ✓ | ✓ |
| Burst, float text, screen flash | — | ✓ (RU) | ✓ (+ EN arcade) |
| Exam lives HUD (backend wrong count) | — | ✓ EXAM | ✓ EXAM |
| Fake combo/XP HUD | — | — | ✓ |
| Ambient crumbs, floaters, CRT | — | — | ✓ |
| Pointer trail | — | — | ✓ + motion |
| Stage intro / portal wipe / coin pickup | — | ✓ | ✓ |
| Infinite filter/bg loops | — | — | ✓ |

#### Asset inventory (PNG under `static/img/pixel/`)

| Disposition | Assets |
|-------------|--------|
| **KEEP** | crumbs, dots, confetti, boom, coin*, gem, star, mascot, hero, UI chrome (tab/radio/checkbox/btn), xp-bar, corners, spark, skull, potion, trophy, heart-life, hp, portal frames |
| **MERGE** | portal / coin-spin / heart / fire → `*-sheet.png` + CSS `steps()` |
| **HIGH_ONLY** | pad, power, sword, shield, lightning, cloud, flag, cherry, key |
| **REMOVE** | nav-home/stats/settings PNG (→ SVG sprite) |

Canonical bitmap scale: `--px-s/m/l/xl` = 8/16/24/32; только целые кратные + `image-rendering: pixelated`.

### Принцип CSS (Wave C — cascade layers)

```
@layer tokens, reset, shell, components, pages, utilities, pixel;
```

| Слой | Файлы | Назначение |
|------|-------|------------|
| tokens | `tokens.css` | `:root` + light/dark; **Inter/JetBrains** в `--font-family-*` |
| reset | `reset.css`, `reset-layout.css` | reset, типографика body, a11y, motion, `.ed-page` |
| shell | `shell.css` | AppShell: sidebar, topbar, mobile-nav |
| components | `components-*.css` | masthead, widgets, signatures |
| pages | `pages-*.css` | focus, settings, stats, summary, result, error, layout |
| utilities | `utilities-*.css` | `.hidden`, UX-review overrides, print |
| pixel | `pixel-fx.css` | декор/FX; **не** layout skip-link / MCQ-grid / form flow |

Структурный CSS читает **только** `var(--token)`. Хардкод цвета/шрифта в pages/components запрещён.

---

## 1. Принятые крафт-правила (open-design `craft/` + refero)

Применяются ПОВЕРХ любого дизайна — универсальны, не зависят от бренда.

**Типографика** (`craft/typography.md`)
- Мультипликативная шкала 1.2–1.25, ≤6–8 размеров на артефакт.
- Три веса: Read 400/450 · Emphasize 510/550 · Announce 590/600. 700+ почти не нужен.
- Letter-spacing (нерушимо): body 0; small 11–13px +0.01–0.02em; UI-лейблы 0.02em;
  **ALL CAPS ≥0.06em**; заголовки ≥32px −0.01…−0.02em; display ≥48px −0.02…−0.03em.
- Мера строки 60–75ch (editorial 60–70ch); line-height: display 1.0–1.2, body 1.5–1.7.
- Никогда `text-align: justify`. Максимум 2 гарнитуры.

**Цвет** (`craft/color.md`)
- 4 слоя: нейтрали 70–90% · ОДИН accent 5–10% · semantic 0–5% · effect <1%.
- **≤2 видимых использования `--accent` на экран** (ссылки/фокус-ринги считаются).
- Контраст-гейты: body ≤16px → 4.5:1; крупный >18px → 3:1; UI-компоненты → 3:1.
- Тёмная тема: не чистый чёрный/белый (bg ~#0f0f0f, fg ~#f0f0f0); на тёмном —
  полупрозрачные белые рамки `rgba(255,255,255,0.06–0.08)`.
- Семантические имена токенов, не по hue. **Anti-default: запрет Tailwind-indigo
  как захардкоженного hex** (`#6366f1` и т.п.); через `var(--accent)` — ок, если
  это намеренный бренд-цвет дизайна (напр. Linear).

**Anti-AI-slop** (`craft/anti-ai-slop.md`) — 7 смертных грехов:
indigo-hex-accent; two-stop «trust»-градиент на герое; эмодзи-как-иконки в
h*/button/li/[class*=icon]; sans на display когда задан serif; **карточка с
радиусом + цветной левой границей** (канонический AI-tile — убрать радиус ИЛИ
левую границу); выдуманные метрики; filler-copy. Душа = ~80% проверенных
паттернов + 20% отличительного (один смелый ход, голос микрокопи, одна
запоминающаяся микроинтеракция).

**Состояния** (`craft/state-coverage.md`) — 5 обязательных на каждой
data-поверхности: Loading / Empty / Error / Populated / Edge. Формы +Untouched/
Dirty/Submitted-pending. Валидация on-blur. Ошибка отвечает: что/почему/что
делать; ввод не теряется. **Live-region должен существовать в DOM ДО вставки
контента** (иначе не озвучится — прямо чинит C12/C18). role=alert+focus для
inline-ошибок; role=status (polite) для тостов без переноса фокуса.

**Laws of UX** (`craft/laws-of-ux.md`) — для обучающего флоу честно:
- Goal-Gradient: прогресс «N из M» реальный, не выдуманный.
- Peak-End: празднующий финал сессии (`/session-summary`) — пик в КОНЦЕ.
- Zeigarnik: видимый прогресс ок; **стрик-нагон как dark-pattern — нет**.
- Hick/Choice-Overload: ≤5 групп настроек; рекомендуемый выбор выделен.
- Miller/чанкинг: настройки сгруппированы заголовками.
- Jakob: переиспользуем конвенции (gear=настройки, привычные позиции nav).

**A11y-floor** (`craft/accessibility-baseline.md`): тач-таргет ≥24×24 CSS-px
(моб. крупнее), видимый focus-ring только для клавиатуры, не только цветом
сигналим состояние, body ≥16px на моб. (anti iOS-zoom), prefers-reduced-motion.

---

## 2. Токен-контракт (семантический, light+dark)

Структурный CSS читает ТОЛЬКО эти токены. Имена — расширение существующего
вокабуляра editorial.css (миграция бесшовная). **Design-divergent** токены
каждый дизайн переопределяет; **structural** — общие константы (можно оставить
в `:root`, дизайн редко трогает).

### 2.1 Color (design-divergent)
```
--color-bg-primary      фон страницы
--color-bg-secondary    карточки/панели
--color-bg-tertiary     вложенные поверхности (инпуты, well, code-bg)
--color-bg-inverse      инвертированный блок
--color-surface-overlay подложка модалки/дропдауна (rgba)

--color-text-primary    основной текст (AA на bg-primary)
--color-text-secondary  приглушённый (AA)
--color-text-tertiary   плейсхолдер/disabled (AA-large min)
--color-text-inverse    текст на inverse/на accent-fill
--color-link            ссылки (= accent или ink-underline по дизайну)

--color-border-primary  рамки по умолчанию (hairline)
--color-border-secondary тоньше/слабее
--color-border-focus    цвет focus-ring (= accent обычно)

--color-accent          ЕДИНСТВЕННЫЙ акцент (fill CTA/прогресс/актив)
--color-accent-hover
--color-accent-active
--color-accent-contrast текст поверх accent-fill (AA)
--color-accent-secondary категориальный 2-й тон (НЕ 2-й бренд-акцент; для тэгов/графиков)

--color-status-success / -warning / -error / -info  (+ опц. -*-bg тинты)
```

### 2.2 Typography (design-divergent families, structural scale)
```
--font-family-display   заголовки/display
--font-family-body      основной текст
--font-family-mono      код/эйбрау

--font-size-xs 12 · -sm 14 · -base 16 · -md 18 · -lg 20 · -xl 24
  · -2xl 32 · -3xl 40 · -4xl 56  (px-эквивалент при scale=1; в rem)
--font-weight-read 400 · -emphasize 510 · -announce 600   (дизайн может сдвинуть)
--line-height-tight 1.15 · -snug 1.3 · -normal 1.55 · -relaxed 1.7
--tracking-tight −0.02em · -snug −0.01em · -normal 0 · -wide 0.02em · -caps 0.07em
```

### 2.3 Space / Layout (structural; дизайн может менять базу плотности)
```
--space-1 4 · -2 8 · -3 12 · -4 16 · -5 24 · -6 32 · -7 48 · -8 64 · -9 96  (px→rem)
--measure        ширина чтения прозы (60–70ch; full-width-режим переопр. локально)
--max-width-page контейнер страницы
--gutter         горизонтальный жёлоб страницы (единый: .ed-page и шапка)
--breakpoints (в CSS как media): sm 375 · md 768 · lg 1024 · xl 1280 · 2xl 1536
```

### 2.4 Component-level (design-divergent — ЭТО даёт разный «характер»)
```
--radius-button · --radius-card · --radius-input · --radius-pill(=9999)
--border-width-card · --border-width-input
--card-surface          стиль карточки: задаётся комбинацией bg+border+elevation
--elevation-card        тень ИЛИ ring (0 0 0 1px) ИЛИ none — по дизайну
--elevation-popover     для дропдаунов/тултипов
--hairline              цвет/прозрачность тонкой линии (на тёмном — белая rgba)
--focus-ring            полный shadow focus-кольца (цвет=border-focus)
--button-fill-style     намёк fill|outline|ghost (CSS-ветки по дизайну, опц.)
```

### 2.5 Motion (structural; дизайн может ужесточить/смягчить)
```
--duration-instant 50 · -fast 150 · -normal 250 · -slow 400 · -slower 600 (ms)
--easing-default cubic-bezier(.4,0,.2,1) · -in · -out · -bounce(.34,1.56,.64,1)
```
Все переходы под `@media (prefers-reduced-motion: reduce)` → near-0. Мотион
подтверждает смену состояния, не «перформит» (Emil/animation-discipline).

### 2.6 Z-index (structural)
```
--z-base 0 · -dropdown 100 · -sticky 200 · -modal 1000 · -toast 1100
```

---

## 3. Архитектура файлов

```
static/css/
  tokens.css                  @layer tokens — палитра handoff-3, шрифты Inter/Mono
  reset.css                   @layer reset
  reset-layout.css            @layer reset — a11y, skip-link, motion, .ed-page
  utilities-core.css          @layer utilities — .hidden, layout utils
  components-masthead.css       @layer components — legacy ed-masthead (header fragment)
  components-widgets.css        @layer components — back-to-top, today-hero, personalization
  components-signatures.css     @layer components — instrument h1..h6 voice
  pages-focus.css               @layer pages — тренировка, MCQ, flashcard
  pages-settings.css            @layer pages
  pages-stats.css               @layer pages
  pages-summary.css             @layer pages
  pages-error.css               @layer pages
  pages-result.css              @layer pages
  pages-layout.css              @layer pages — wide layout, data-layout, @media print
  utilities-overrides.css       @layer utilities — UX-review pass
  base.css                      @import-агрегатор (dev); prod — цепочка в head.html
  shell.css                     @layer shell — AppShell редизайна
  pixel-fx.css                  @layer pixel — декор (не body-font, не core layout)
static/js/
  app.js, stats.js, settings/*
templates/
  fragments/head.html         загрузчик CSS + theme/pixel bootstrap
docs/design/
  DESIGN.md                     этот файл
```

**Загрузка** (`fragments/head.html`): tokens → reset/utilities/components/pages
(14 файлов, `?v=123`) → shell → pixel-fx. Google Fonts: Inter + JetBrains Mono
(non-blocking `media=print` + onload). Версии `?v=N` бампать при правке файла.

---

## 4. Контракты-инварианты (НЕ ломать при rewrite)

**Backend model-attrs** (биндинги шаблонов; контроллеры их кладут):
`current, interviewSession, filter, groups, selectedGroup, topics, stats, mode,
diagram, aiEnabled, weakTopics, topicStats, topicStatsJson, summary,
studyLearnPhase, studyAnswerHtml, searchResults, searchQuery, reviewState,
reviewMode, reviewForecast, reviewForecastMax, result, relatedQuestions,
progressPercent, generationUnavailable, focusModeChipText, focusModeHintText,
focusEmptyRetryHref, focusEmptyRetryText, flashcardRevealed, flashcardMode,
difficulty, coverageGaps, answerHtml, answerDisplayMode`.
Контроллеры: `InterviewMvcController` (страницы), `InterviewApiController` (AJAX
`/answer` и т.д.), `AdminController`, `ExportController`, `GlobalExceptionHandler`
(error). Формы: POST `/answer` (questionId, optionId, topic, group, _csrf…),
`/finish`, `/flashcard-reveal`, `/flashcard-grade` (grade 1–4).

**Fragment-contract a11y** (`TemplateFragmentContractTest`) — сохранить смысл,
обновить структурные ассерты в lockstep:
- `data-ui-fragment="..."` на извлечённых фрагментах.
- training-actions: `#action-footer`, `#question-timer`, `#next-question`,
  `.keyboard-hint`, `aria-keyshortcuts="Enter"`, **без** `aria-live="`.
- post-answer-controls: `#result-feedback` `aria-atomic="true"`, кнопка
  `aria-controls="extra-analysis-content"`, sink `#extra-analysis-content` ПОСЛЕ кнопки.
- inline-alert: `role="alert"` `aria-live="assertive"`.
- stats: `#table-sort-status` `aria-live="polite"` `aria-atomic="true"`,
  `role="columnheader"`, chart-fallback’ы, `role="status"`.
- body-классы: `focus-page`/`stats-page`/`settings-page`/`result-page`/summary/error.
- settings: каркас `app-layout` + `#left-sidebar-card`/`#left-sidebar-content`/`#main-content`
  (можно эволюционировать — синхронно с тестом).
- Структурные CSS-ассерты — `readStructuralCss()` в `TemplateFragmentContractTest`
  (split CSS, Wave C).

**Mermaid pathway** (см. [[feedback_diagrams_non_mermaid_ok]]): `includeMermaid`
в head → `mermaid@11.12.0` + `fragments/mermaid-init.html` (securityLevel
`'antiscript'`); `MarkdownRenderService.preprocessMermaid` оборачивает ```mermaid
в `<div class="mermaid">`; app.js рендерит на AJAX-контенте. `.markdown-content
pre code` — селектор highlight.js.

**Иконки** ([[project_icon_system]]): монохромный Lucide SVG-спрайт
(`fragments/icons.html`) + `.ed-icon` + JS `icon()`. НЕ эмодзи (кроме намеренных
💡/🎲🤔💪 уверенности — пересмотреть в редизайне на SVG).

**JSON-вопросы НЕ трогать.** MCQ только в seed/mcq/**.json.

---

## 5. Дизайн Instrument (единственный runtime)

| id | характер | accent | display / body / mono | bg dark / light |
|----|----------|--------|----------------------|-------------------|
| **instrument** | terminal handoff-3, teal-signal, dark-default | `#39d0c7` | **Inter · Inter · JetBrains Mono** | `#071116` / `#f5f8f8` |

SSR всегда `data-design="instrument"`. Исторический роестр switchable-дизайнов
(editorial, linear, swiss, …) удалён из tokens/CSS (FE-CMP-1); `systems/*.md` —
архив для референса.

---

## 6. История эпика (#59–#67)

1. **#59** DESIGN.md + токен-контракт ✔
2. **#61** `tokens.css` — single-source `:root` Instrument ✔
3. **#62** structural CSS — split + `@layer` (Wave C) ✔
4. **#63–#67** шаблоны, app.js, тесты, live-verify — по backlog

---
