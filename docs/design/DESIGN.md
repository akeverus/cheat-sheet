# DESIGN.md — quiz-app (cheat-sheet interview trainer)

> **Status:** master spec for the radical switchable-design redesign (директива 2026-06-18).
> Drives all UI work per `/frontend-design` + `/design-tokens` («DESIGN.md is mandatory»).
> Per-design specs: [`systems/`](systems/). Craft rules vendored from
> [`open-design`](https://github.com/nexu-io/open-design) (`craft/`) + refero.design.
> Язык интерфейса — русский; имена файлов/токенов/классов — английские.

---

## 0. Что мы строим

Тренажёр для подготовки к собеседованиям: reading-heavy Q&A (markdown), код
(highlight.js), диаграммы (mermaid), флешкарты (SM-2), сессии/прогресс/стрик,
статистика (chart.js). Аудитория — разработчики. **Главная задача —
безупречные читаемость и UX**, с возможностью на лету **переключать дизайн**.

### Принцип переключаемых дизайнов

```
<html data-design="anthropic" data-theme="dark">
        └─ выбор «скина» (палитра+шрифты+характер)   └─ ортогональная тема
```

- `data-design` ∈ роестр (§5). `data-theme` ∈ {light, dark}. **Ортогональны**:
  любой дизайн × любая тема = валидно.
- **Структурный CSS design-agnostic** и читает ТОЛЬКО токены (§2). Хардкод
  цвета/шрифта/радиуса/тени в структуре запрещён.
- **Каждый дизайн = полный набор токенов** в `[data-design="X"]` (+ тёмные
  оверрайды в `[data-design="X"][data-theme="dark"]`). Добавить дизайн = один
  блок токенов + один файл `systems/<name>.md` + запись в `head.html`
  (`DESIGNS[]`, `THEME_COLORS`, при нужде шрифт). Структуру/шаблоны не трогаем.
- Переключение — без перезагрузки: `window.__design.set()` флипает атрибут
  (см. `fragments/head.html`). FOUC-safe: атрибут ставится инлайн до 1-го кадра.
- Дизайны **различаются не только цветом**, но и характером: радиусы, стиль
  рамок, высота над поверхностью (тень/ring/flat), плотность, шрифтовые пары,
  ритм. Для этого токен-контракт включает component-level токены (§2.4).

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
  tokens.css     :root контракт-дефолты + [data-design=X] блоки + dark-оверрайды (ВСЕ дизайны)
  base.css       design-agnostic структура: reset, layout, компоненты, утилиты, a11y, motion
                 (= переписанный editorial.css; сохранить контракт editorial-теста ИЛИ обновить тест)
static/js/
  app.js         поведение (перепривязать селекторы к новым шаблонам, поведение сохранить)
  stats.js       графики (redraw на data-theme/data-design)
templates/       переписать с нуля (см. §4); fragments/head.html — загрузчик+switcher
docs/design/
  DESIGN.md      этот файл
  systems/*.md   per-design спеки
```

Загрузка в `head.html`: preload+link `tokens.css` → `base.css` (порядок важен:
токены до структуры). Версионирование `?v=N` бампать при каждой правке.

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
- Структурные CSS-ассерты (`html[data-design] .*-page`, утилиты) — обновить под
  base.css (имя/селекторы) синхронно.

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

## 5. Роестр дизайнов (расширяемый; источники — open-design)

| id | характер | accent | display / body / mono | bg light / dark | open-design источник |
|----|----------|--------|----------------------|-----------------|----------------------|
| **editorial** (default) | тёплая бумага, серифный display, интеллектуально-спокойно (Anthropic-якорь) | terracotta `#C96442`/`#D97757` | Lora · Inter · JetBrains Mono | `#FAF9F5` / `#262624` | claude, warm-editorial |
| **linear** | dark-native, инженерная точность, ахроматика | indigo `#5E6AD2`/`#7170FF` | Inter(510) · Inter · Berkeley/Geist Mono | `#FFFFFF` / `#08090A` | linear-app |
| **swiss** | интернац. типографика, сетка, B/W + 1 красный, flat, rules | red `#E5231B` | Geist · Geist · Geist Mono | `#FFFFFF` / `#0A0A0A` | swiss philosophy / shadcn |
| **notion** | тёплый минимализм, near-black, whisper-рамки, soft | blue `#0075DE` | Inter · Inter · JetBrains Mono | `#FFFFFF` / `#191919` | notion |
| **mintlify** | docs-as-product, воздух, крупные радиусы, mono-CAPS лейблы | green `#18E299`/`#0FA76E` | Inter · Inter · Geist Mono | `#FFFFFF` / `#0D0D0D` | mintlify |
| **broadsheet** | газетный editorial, Source Serif, фолио-детали, сдержанно | ink + 1 тон | Source Serif 4 · Inter · JetBrains Mono | `#FEFFFC` / `#15151D` | theverge, wired, publication |

**Решение по id (минимум churn):** дефолтный warm-paper дизайн оставляем под id
`editorial` (он = `:root` в tokens.css; уже зашит в head SSR, base.css signature,
`data-design` шаблонов, NAMES-map тоггла, seg-control). НЕ переименовываем в
`anthropic`. Итоговый роестр id: `editorial` (default), `swiss`, `linear`,
`notion`, `mintlify`, `broadsheet`. `swiss`/`linear`/`broadsheet` переоснащаются
полноценными токен-наборами (вместо тонких рекрасок-оверлеев); `notion`/`mintlify`
— новые. Спека warm-paper лежит как `systems/editorial.md` (Anthropic/claude-якорь).

---

## 6. Порядок исполнения (эпик #59–#67)

1. **#59** этот DESIGN.md + токен-контракт ✔(в работе)
2. **#60** per-design спеки `systems/*.md` (workflow fan-out из open-design)
3. **#61** `tokens.css` (контракт + блоки всех дизайнов, light+dark, контраст-гейты)
4. **#62** `base.css` — design-agnostic структура (rewrite editorial.css)
5. **#63** rewrite шаблонов с нуля (fragments + страницы), контракты §4 целы
6. **#64** switcher UI (settings + шапка) + head.html роестр/шрифты/THEME_COLORS
7. **#65** выровнять app.js/stats.js под новую разметку
8. **#66** обновить `TemplateFragmentContractTest`
9. **#67** live-верификация Playwright × 7 вьюпортов × 2 темы × дизайны + `/design-review` + `/emil-design-eng` полировка

Каждый шаг — отдельные коммиты (русские сообщения, Co-Authored-By). Не запускать
gradle при живом devtools-bootRun. JSON-вопросы не трогать.
