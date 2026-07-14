# PLAN_FRONTEND.md — ROUND RESET: mockups → production port → full frontend refactor

## 0. Reset directive

**Дата нового раунда:** 2026-07-11  
**Продукт:** quiz-app, тренажёр подготовки к собеседованию  
**Стек:** Spring Boot + Thymeleaf + vanilla CSS/JS  
**Режим:** бесконечный worst-first loop с атомарными коммитами

Текущий документ полностью заменяет прежнюю приёмочную модель.

### Что сброшено

- Все прежние `DONE`, `verified-clean`, `Golden`, проценты готовности и файловые галочки
  **аннулированы как доказательство текущего качества**.
- Ни один экран, mockup, production-файл, state, тема, дизайн-вариант или viewport не переносит
  старый `PASS` автоматически.
- Старые раунды и коммиты остаются историческим evidence в Git, но не закрывают критерии нового раунда.
- Старые backlog-строки ниже сохранены только как **гипотезы для повторной проверки**.
- Прежние осознанные решения пользователя сохранены и не переоткрываются без нового evidence.

### Что не сброшено

- production-код;
- уже созданные mockups;
- design tokens;
- полезные компоненты;
- server/DOM contracts;
- Git-история;
- подтверждённые продуктовые ограничения;
- защищённые content/backend pipelines.

### Начальные значения нового раунда

```text
Mockup-approved surfaces: 0
Port-ready surfaces: 0
Production-parity surfaces: 0
Fully refactored surfaces: 0
Final verified surfaces: 0
Old PASS carried forward: 0
```

Каждый `✅` в этом документе должен появиться только после новой проверки в текущем раунде.

---

## 1. Цель и полный pipeline

Цель состоит не только в создании макетов.

Каждая пользовательская поверхность должна пройти полный жизненный цикл:

```text
DISCOVER
→ MOCKUP_RECHECK
→ MOCKUP_APPROVED
→ PORT_MAPPING
→ PORT_TO_PRODUCTION
→ PRODUCTION_PARITY
→ FULL_REFACTOR
→ REGRESSION_QA
→ VERIFIED
→ MAINTENANCE
```

Результат:

1. Существующие mockups перепроверены, недостающие созданы.
2. Покрыты все страницы, окна, overlays, feedback surfaces и component states.
3. Mockup-разметка сопоставлена с реальными Thymeleaf/DOM/backend contracts.
4. Утверждённый дизайн перенесён в production frontend.
5. Старый production frontend очищен от конфликтующих и мёртвых слоёв после доказанной миграции.
6. Каждый экран проходит функциональную, визуальную, responsive, theme и accessibility проверку.
7. После переноса выполняется отдельный полный рефакторинг и устранение оставшихся замечаний.
8. Loop переходит в maintenance, а не начинает очередной произвольный редизайн.

---

## 2. Авторитетные инструкции

Каждый тик обязан читать:

```text
~/.claude/skills/frontend-design/SKILL.md
~/.claude/skills/frontend-design/references/iterative-refactor-protocol.md
PLAN_FRONTEND.md
design/mockups/DESIGN.md
design/mockups/PRODUCT.md
design/mockups/PROGRESS.md
docs/ui-ux-improvement-log.md
```

Дополнительно, если существуют:

```text
CLAUDE.md
AGENTS.md
README.md
.design/editorial-redesign/SERVER_CONTRACT.md
project_audit_backlog_2026-06.md
```

Приоритет:

1. бизнес-контракт и реальный пользовательский flow;
2. безопасность данных и accessibility;
3. осознанные решения пользователя;
4. production runtime;
5. утверждённый `DESIGN.md`;
6. текущая дизайн-система;
7. этот план;
8. внешние референсы.

---

## 3. Жёсткие ограничения проекта

### Разрешено

После утверждения mockup конкретной поверхности разрешено менять production frontend:

```text
modules/quiz-app/src/main/resources/templates/**
modules/quiz-app/src/main/resources/static/css/**
modules/quiz-app/src/main/resources/static/js/**
design/mockups/**
PLAN_FRONTEND.md
docs/ui-ux-improvement-log.md
```

Разрешены:

- перенос mockup в production;
- рефакторинг templates/CSS/JS;
- удаление доказанно мёртвого frontend-кода;
- унификация компонентов и токенов;
- исправление UX/a11y/responsive/state defects;
- обновление cache-busting;
- targeted frontend tests.

### Запрещено

- менять бизнес-логику ради дизайна;
- менять API/DB без отдельного решения;
- трогать `seed/mcq/**`;
- создавать новый content pipeline;
- destructive live submit/start/finish, загрязняющий SRS;
- новые зависимости и смена package manager;
- выключать VPN;
- `git add -A`;
- `git reset --hard`;
- откатывать чужие изменения;
- push;
- коммитить runtime-копии вместо source;
- запускать тяжёлый build, конфликтующий с живым `bootRun`.

### Cache-busting

- CSS change → согласованный bump `tokens.css` и `base.css` references в `head.html`;
- `app.js` change → bump во всех templates-consumers;
- `stats.js` change → bump в `stats.html`;
- template text-only change → asset bump не нужен;
- после bump проверить Network и фактически загруженную версию.

---

## 4. Сохранённые продуктовые решения

Эти решения сохраняются после reset и не считаются дефектами без нового evidence:

- вариант ответа до проверки нейтрален;
- correct/wrong semantic color появляется только после проверки;
- options layout адаптивен: одна или две колонки по длине и code-density;
- inline `code` в вариантах визуально мягкий;
- focus-mode остаётся минималистичным;
- `.focus-question` 48px — осознанное решение;
- `role=rowheader` на summary `<td>` сохраняется из-за share-script contract;
- намеренные emoji в подсказке/уверенности сохраняются;
- Mermaid имеет внешнего owner;
- border-left accent-линейка, favorite-flow, CTA из аналитики и self-host Chart.js
  не меняются без отдельного решения;
- content fairness относится к MCQ pipeline; frontend не должен усиливать подсказки.

При появлении нового доказанного дефекта решение может быть переоткрыто только через запись:
`evidence → impact → alternatives → migration risk → user decision`.

---

## 5. Статусы pipeline

| Статус | Значение |
|---|---|
| `⬜ TODO` | не проверено в новом раунде |
| `🔎 DISCOVERED` | поверхность найдена и внесена в inventory |
| `🔄 MOCKUP_RECHECK` | существующий mockup проверяется |
| `🧩 MOCKUP_MISSING` | mockup отсутствует |
| `🎨 MOCKUP_APPROVED` | mockup прошёл все mockup gates |
| `🗺 PORT_READY` | DOM/server mapping полный, перенос безопасен |
| `🚧 PORTING` | перенос в production выполняется |
| `🟦 PROD_PARITY` | production визуально и функционально соответствует mockup |
| `🛠 REFACTOR` | отдельный post-port рефакторинг |
| `🧪 REGRESSION` | полный QA после рефакторинга |
| `✅ VERIFIED` | все обязательные критерии пройдены |
| `◐ PARTIAL` | обработана только часть state-family |
| `🟠 MANUAL_REVIEW` | нужен выбор/неоднозначный контракт |
| `⏸ BLOCKED` | collision, runtime или внешний blocker |
| `🚫 DECISION_LOCK` | осознанно не менять |
| `🏛 EXTERNAL_OWNER` | другой pipeline/owner |

`VERIFIED` нельзя ставить непосредственно после mockup или port. Обязательны отдельные
`PROD_PARITY`, `REFACTOR` и `REGRESSION`.

---

## 6. Фазы

### Фаза A — exhaustive inventory и reset

- обнаружить все production routes/templates/fragments/overlays/states;
- обнаружить все mockups;
- создать/обновить `design/mockups/screen-registry.json`;
- создать/обновить `design/mockups/PIXEL_QA_MATRIX.md`;
- сопоставить production surface ↔ mockup;
- поставить всё в `TODO/RECHECK`;
- не делать массовый visual refactor.

### Фаза B — mockup completeness и approval

Для каждой поверхности:

- перепроверить существующий mockup;
- создать отсутствующие states/windows;
- проверить все темы, варианты, viewports, DPR, zoom, keyboard и a11y;
- сделать deterministic fixtures;
- провести design-review и anti-slop;
- получить `MOCKUP_APPROVED`.

### Фаза C — port mapping

До production-кода:

- определить production source;
- определить server-rendered conditions;
- определить DOM hooks;
- определить JS consumers;
- определить form actions и backend contracts;
- определить progressive-enhancement path;
- определить cache-busting chain;
- определить tests;
- записать mapping;
- получить `PORT_READY`.

### Фаза D — перенос mockup в production

- переносить одну surface-family за тик;
- сохранять бизнес-логику и server conditions;
- использовать реальные tokens/components;
- не вставлять mockup как изолированный второй frontend;
- удалять старый слой только после parity proof;
- обновлять asset versions;
- live-verify;
- получить `PROD_PARITY`.

### Фаза E — полный post-port refactor

После достижения parity отдельно проверить:

- IA и hierarchy;
- cognitive load;
- component duplication;
- CSS cascade/specificity;
- JS architecture;
- state completeness;
- accessibility;
- responsive;
- performance;
- dead code;
- no-JS;
- print;
- all 10 designs × 2 themes;
- исправить замечания;
- получить `REFACTOR`.

### Фаза F — regression и maintenance

- полный browser regression;
- screenshots/pixel diff;
- targeted/full available tests;
- cross-screen shared-component checks;
- final `VERIFIED`;
- затем maintenance по новым изменениям и регрессиям.

---

## 7. Базовый inventory пользовательских поверхностей

Эта таблица является стартовой, а не исчерпывающей. Loop обязан дополнить её static/browser crawl.

| ID | Поверхность | Production | Mockup | Обязательные state-family | Inventory | Mockup | Mockup QA | Mapping | Port | Parity | Refactor | Regression | Final | Notes |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `FOCUS` | Главная / тренировка / вопрос | `focus-training.html` | `focus-question.html` | flashcard; MCQ; selected; correct; wrong; explanation; session; empty branches; long/code-heavy | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | ✅ | ✅ | ◐ | Перепроверить наличие и полноту |
| `RESULT` | Результат ответа / no-JS fallback | `result.html` | `result.html` | correct; wrong; explanations; related; extra controls; loading/error; no-JS | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | ◐ | ◐ | ⏸ | Перепроверить наличие и полноту |
| `SUMMARY` | Итоги сессии | `session-summary.html` | `session-summary.html` | score; mistakes; recommendations; empty; share; print; long table | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | ✅ | ◐ | ⏸ | Перепроверить наличие и полноту |
| `SETTINGS` | Настройки | `settings.html` | `settings.html` | session; appearance; data; validation; saved feedback; reset dialog; danger states | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | Перепроверить наличие и полноту |
| `STATS` | Аналитика | `stats.html` + `stats.js` | `stats.html` | normal; cold start; no match; charts; long table; collapsed/expanded; sort; print | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | Перепроверить наличие и полноту |
| `ERROR` | Ошибки 4xx/5xx | `error.html` | `error.html` | 400; 401; 403; 404; 5xx; developer disclosure; recovery | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | Перепроверить наличие и полноту |
| `SHELL` | Глобальная оболочка / header / navigation | `fragments/header.html` + `head.html` | `shell.html` | desktop; mobile nav; themes; designs; skip link; back-to-top; no-JS | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | Перепроверить наличие и полноту (файл `shell.html`, не `shell-header.html` — испр. R0.103) |
| `OVERLAYS` | Dialogs / popovers / dropdowns / confirmations | templates + `app.js` triggers | `overlay-state-atlas.html` | admin reset; confirmations; menus; popovers; tooltips; any discovered overlay | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | R0.106: атлас СОЗДАН + QA (file:// dark/light/narrow + живой демо scrim/focus-trap/Esc) — dialog/alertdialog/drawer/disclosure/tooltip (зеркалят прод `#kbd-help-overlay`/`#reset-confirm`/`#mobile-menu`) + menu/popover (кандидаты, не в проде). Final ◐ — прод-parity gated |
| `FEEDBACK` | Alerts / toasts / status / validation | fragments + templates + `app.js` | `feedback-state-atlas.html` | assertive; polite; success; warning; error; pending; validation; offline/permission if reachable | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | R0.105: атлас СОЗДАН + QA (file:// dark/light/390) — 6 секций (inline-alert error/success/info/warn, toast, validation норма/ошибка, pending спиннер/скелет, verdict, aria-live легенда), зеркалит `base.css:1275`. Design-gap: нет warning-токена → Фаза G. Final ◐ — прод-parity gated |
| `COMPONENTS` | UI primitives and all states | `base.css` + fragments + JS | `component-state-atlas.html` | default; hover; active; focus; disabled; loading; selected; checked; expanded; dark; long content | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | R0.107: атлас СОЗДАН + QA (file:// dark/light/narrow, 0 overflow) — buttons 4×6 состояний/links/icon/inputs/select/seg-control/switch/native/tabs/MCQ-опция 6 состояний/chips/badges/kbd/surfaces; demo-классы зеркалят `:hover`/`:active`/`:focus`. R0.108: +expanded(disclosure collapsed/expanded)/long-content(перенос option/chip/button) → все 11 state-family §7 покрыты. Final ◐ — прод-parity gated |

**Легенда статуса ячеек (сверка R0.1–R0.99, 2026-07-13; атлас-сверка R0.103):** ✅ закрыто с доказательством · ◐ частично/gated (session-parity или отложенный шаг по решению юзера; для OVERLAYS/FEEDBACK/COMPONENTS — состояния покрыты распределённо, но консолидир. атлас-mockup не создан) · 🧩 mockup-атлас отсутствует на диске (Фаза B backlog) · ⏸ Final ждёт гейта (см. §21 R0.99: window-тик контракт-тестов / решение EXAM / данные графиков) · 🏛 внешний owner · 🚫 файл удалён в R0.81.


---

## 8. Обязательная surface acceptance matrix

Для каждой строки §7 отдельно проверяются:

| Ось | Критерии |
|---|---|
| Inventory | route/trigger/source/parent/state list найдены |
| Mockup completeness | все окна и state-family воспроизводимы |
| Determinism | URL/fixture всегда создаёт одинаковый DOM |
| Visual language | соответствует `DESIGN.md`, без generic AI slop |
| Geometry | gutters/gaps/baselines/control heights/tokens согласованы |
| Responsive | 320/375/390/430/768/1024/1280/1440/1512/1728/1920/2560 |
| Wide layout | chrome edge-to-edge, prose measure внутри зоны, нет пустых полей |
| Themes | default/light/dark |
| Designs | все 10 design variants по применимому coverage |
| DPR/zoom | DPR 1/2, zoom 100/200 |
| Content fixtures | normal/min/max/long/code/empty/error/partial |
| Interaction | hover/active/focus/selected/disabled/loading/expanded |
| Accessibility | semantics/keyboard/focus/contrast/live/target/reflow |
| Motion | reduced-motion, no layout shift |
| Port mapping | server conditions, DOM hooks, JS, forms, tests |
| Production parity | screenshot + DOM/computed metrics |
| Refactor | duplication/cascade/architecture/dead code/perf |
| Regression | shared consumers, opposite theme, adjacent breakpoints |
| Evidence | screenshots, checks, commit, residual risks |

Один красивый screenshot не закрывает поверхность.

---

## 9. Production-файлы: новый файловый аудит

Все прежние файловые галочки сброшены. Историческая заметка сохраняется только как подсказка.

| Production-файл | Тип | Inventory | Mockup map | Contract | Port | UX | Responsive | A11y | Themes | States | Code quality | Tests | Browser | Parity | Cleanup | Final | Историческая заметка |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `focus-training.html` | page | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | ✅ | ✅ | ✅ | ◐ | ✅ | ◐ | FT-1..18 закрыты; 48px h2 + neutral-selected = 🚫; **R0.129: AIR-6 рудимент `generationUnavailable` снят** (4 guard'а + service/mapper, 7 файлов; Contract/Tests ✅ — контракт 10/10 + Mockito render/mapper/service green, app не нужен) |
| `result.html` | page | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | ◐ | ⏸ | ◐ | ◐ | ◐ | ⏸ | Чисто; **R0.126: regenerate-UI + `.question-side` удалены** (app.js/base.css v=94); **R0.127: RES-15 related-list-семантика** (`div role=list` + `role=listitem`, app.js v=64, контракт 10/10); RES-14 снят (код в `.answer`), RES-8 контраст AA все 20; RES-3 favorite=⛔ |
| `settings.html` | page | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ◐ | SET-14/17 р134; SET-7 р136; SET-6/SET-5 verified-clean р137 |
| `stats.html` | page | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ◐ | STA-18 р134; STA-19 р135; STA-5/STJ-1 labels р137; **STA-20 dead-колонка «Сброшено» удалена R0.128** (Зрелость data-col 5→4, base.css v=95, sort цел) |
| `session-summary.html` | page | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | ✅ | ⏸ | ◐ | ◐ | ✅ | ⏸ | Чисто; dual-path nav = deliberate (SUM-3); score-card h2 добавлен р104 (SUM-9) |
| `error.html` | page | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ◐ | Чисто; 403 авто-retry = ⛔ERR-3 |
| `fragments/head.html` | fragment | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ◐ | Чисто; CDN-guards, 6 осей персонализации до 1-го кадра |
| `fragments/header.html` | fragment | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ◐ | HDR-1 закрыт р138 (mobile focus title + session compact; sticky отвергнут) |
| `fragments/icons.html` | fragment | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | 🌱ICO-3 нет warning-иконки для warn/error (low) |
| `fragments/mermaid-init.html` | fragment | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | 🏛 | MER-1 🚫 внешний owner; сам код чист (antiscript-guard) |
| `fragments/inline-alert.html` | fragment | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | Чисто; role=alert+assertive (IAL-1) |
| `fragments/post-answer-controls.html` | fragment | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | ✅ | ⏸ | ◐ | ◐ | ✅ | ⏸ | Чисто; feedback polite + sink под кнопкой (PAC-1..2) |
| `fragments/result-zone-head.html` | fragment | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | Чисто; zone-chip+hint (RZH-1) |
| `fragments/stats-grid.html` | fragment | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | Чисто; accuracy-bar aria-hidden (SGR-3); cold-start = 🚫 |
| `fragments/today-widget.html` | fragment | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | Чисто; «N из M к повтору» (TDW-1); streak без aria-live = 🚫 |
| `fragments/training-actions.html` | fragment | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ◐ | ✅ | ⏸ | ✅ | ◐ | ✅ | ◐ | Чисто; TAC-3 keyboard-hint контраст ✅ verified р107 (min 5.93:1); timer/hint без aria-live = 🚫 |
| `css/tokens.css` | style | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | TOK-1 AA-гейт CLEAN, TOK-5 OS-prefs; TOK-2 accent≈semantic hue = 🚫 identity, mitig. not-by-color-alone |
| `css/base.css` | style | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ◐ | BAS-15 tabs↔seg verified-clean р137; BAS-21 р134; BAS-18 measure = 🚫 |
| `css/editorial.css (мёртв)` | style | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫УДАЛЁН R0.81 (git rm, 0 ссылок) — 🌱EDC-1 subset-proof ✅ р109: pre-split монолит, 9 uniques = box-sizing-дубли + обсолет pre-tabs-разметка → чистый историч-дубль, SAFE-TO-DELETE (ждёт ГО юзера, НЕ stranded как DSG-1) |
| `css/{linear,swiss}.css (не подключены)` | style | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫УДАЛЁН R0.81 (git rm, 0 ссылок) — 🌱DSG-1 ❌КОРР.р99: НЕ чистые дубли — linear держит `a`/`a:hover`, swiss `.ed-masthead-kicker`/`.ed-eyebrow` НЕ в base.css (частичный порт, как broadsheet) → port-or-abandon decision, НЕ удалять как junk |
| `css/broadsheet.css (не дубль!)` | style | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫 | 🚫УДАЛЁН R0.81 (git rm, 0 ссылок) — 🌱DSG-2 CONFIRMED: 13 структ.правил (nav/btn/link) НЕ в base.css (0 vs linear 2/swiss 6) → broadsheet теряет структ.акценты, токены живут |
| `js/app.js` | script | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⏸ | ✅ | ◐ | ✅ | ◐ | ✅APP-8 regenerate удалён R0.126; ✅RES-15 related-list-семантика R0.127 (renderRelatedQuestions обёрнут role=list); 🌱APP-9 comparison-table th без scope/caption; ⛔APP-5 favorite; **v=64** (RES-15 R0.127; regenerate-removal был v=63 R0.126) |
| `js/stats.js` | script | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ⏸ | ✅ | ✅ | ✅ | ◐ | STJ-1 labels/grid закрыт р137; kbd/progress-dup c app.js = 🚫 standalone-by-design; **v=15** (свер. R0.119; заметка «v=13» была устаревшей) |

---

## 10. Исторические backlog-гипотезы после reset

Строки ниже **не являются открытыми дефектами автоматически**. Некоторые могли быть исправлены
поздними коммитами, некоторые являются решениями, некоторые устарели после удаления AI.
Каждая строка получает новый статус:

- `RECHECK` — заново проверить код и runtime;
- `DECISION_LOCK` — сохранить решение, пока нет нового evidence;
- `EXTERNAL_OWNER` — не менять frontend-loop;
- `CONTRACT_MONITOR` — проверять при затрагивающих изменениях.

| ID | Точка | Историческая гипотеза | Старое направление | Прежний статус | Новый статус |
|---|---|---|---|---|---|
| `FT-2` | `.question-zone-head` chip+hint (`focusModeChipText/HintText`) | ~~Серверная микрокопия режима~~ — **ПОДТВЕРЖДЕНО реализованной, per-mode (R0.111, app не нужен):** `MvcModelAttributeMapper.resolveFocusModeChipText` (82-93) = 4 ветки review/flashcard/null/`displayName(mode)`; `resolveFocusModeHintText` (95-106) = 4 ветки review/flashcard/STUDY/default, без пустых строк. EXAM/MARATHON/TRAINING делят default-hint — защитимо (интеракция одного вопроса идентична; режимы различаются длиной/скорингом сессии, не способом ответа). | Опц.: отдельный hint для EXAM/MARATHON — контент-полиш, decision-gated | 🚫 WONTFIX (per-mode готов) / 🌱 opt-content-decision | 🚫 DECISION_LOCK |
| `FT-7` | `.focus-question` h2 48px | Намеренный размер «окна вопроса» | НЕ трогать | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `FT-12` | option fairness: длина/наполненность выдаёт correct | Контент-перекос; UI обязан не усиливать | seed/mcq pedago-loop | 🏛 CONTENT | 🏛 EXTERNAL_OWNER |
| `FT-15` | `training-actions` include (Проверить ответ) | CTA далеко при длинных вариантах | sticky/видимый submit | 🌱 BACKLOG | ⬜ RECHECK |
| `FT-17` | `app.js(v=51)` в конце body | Версионный контракт JS | бампать при правке app.js | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `RES-3` | `.btn-favorite` (звезда, aria-pressed) | Избранное — флоу под вопросом юзера | НЕ трогать без решения | ⛔ DEFERRED | ⬜ RECHECK |
| `RES-15` | `#result-related-questions` > `a.related-question-item` (result.html:132-137) + `app.js renderRelatedQuestions` (1624-1648) | Набор из N ссылок «Похожие вопросы» без list-семантики (нет ul/li, нет role=list/listitem) — отступление от собств. стандарта файла (`.options` role=list на стр.63). Static a11y-workflow 2026-07-13, WCAG 1.3.1, CONFIRMED адверсари-верификатором. Двухчастный: SSR (result.html) + focus-динамика (app.js) — одинаковый дефект | wrapper `<div class="related-questions-list" role="list">` вокруг ссылок + `role="listitem"` на `<a>`; **layout-риск НУЛЕВОЙ** (R0.102, CSS-анализ base.css:2562-2588: `.related-question-item{display:block}`, родитель без flex/grid) | 🆕 WORKFLOW-FINDING (P4 minor) | ✅ DONE R0.127 (wrapper `role=list`/`role=listitem` в result.html+app.js v=64, контракт 10/10; a11y-снапшот «список, N» — app-gated подтверждение на session-окне, 0-render риска нет) |
| `SUM-5` | `role=rowheader` на `<td>` темы | Намеренно: share-JS читает `querySelectorAll('td')` | НЕ менять структуру td | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `SUM-7` | share-script `buildShareText` | Контракт: ≥5 `<td>` на строку, recs из `li` | НЕ ломать селекторы | 🔁 ONGOING (контракт) | 🔁 CONTRACT_MONITOR |
| `SET-3` | Design seg-control без preview | Краткое описание/preview пресета при выборе | M | 🌱 BACKLOG | ⬜ RECHECK |
| `SET-9` | a11y-оси разбросаны | Сгруппировать движение/контраст в раздел Accessibility | M | 🌱 BACKLOG | ⬜ RECHECK |
| `SET-12` | `app.js(v=51)` | Версионный контракт | — | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `SET-15` | `/start` форма: `count` hardcode `value="20"` шэдоуит серверный per-mode дефолт (marathon=50) | **УЖЕ ИСПРАВЛЕНО (свер. R0.113, app не нужен):** `app.js:248-257` (блок с меткой `// SET-15:`) на явную смену режима читает `data-default-count` выбранного `<option>` (settings.html:119-123: EXAM/TRAINING=20, MARATHON/STUDY/FLASHCARD=50) и ставит `countInput.value=def` → FE выровнен к BE per-mode дефолтам (`SessionFlowService.resolveSessionTotal:103-106`). Слушатель намеренно НЕ на первичном `sync()` — чтобы не затереть localStorage-restore (`initSessionFormSync:267-286`); trade-off задокументирован в коде. Остаток: сервер-пред-выбор не-EXAM режима + нет localStorage → стартовое `value="20"` до смены (минор, видно до submit, осознанный выбор в пользу restore). Фикс закоммичен (app.js не в незакоммич.). | ~~Выровнять FE к BE~~ — сделано | ✅ DONE (app.js SET-15-блок) | ✅ DONE |
| `STA-3` | Поиск отделён от фильтров линией | ~~Объединить поиск+фильтры~~ — НЕ дефект: вертикальный hairline + верт.центрирование поиска = осознанное решение с rationale `base.css:1476-1480` (одно поле vs высокий фильтр → пустота под полем как намеренный воздух).… | — | 🚫 WONTFIX (deliberate) | 🚫 DECISION_LOCK |
| `STA-10` | CTA из аналитики (тренировать слабые/ошибки) | ~~Кнопки-переходы — может требовать роутов/параметров~~ — **ОПРОВЕРГНУТО статически (R0.110, app не нужен):** CTA `stats.html:32/40` → GET `/(onlyWrong=true,ordered=true)` и `/(weakTopics=true,ordered=true)` проведены end-to-end: `InterviewMvcController.index` (GET `/`, стр.37-45) биндит `@RequestParam onlyWrong/weakTopics/ordered` → `MvcRequestMapper:116` `weakTopicsPriority` → `FocusTrainingPageService:57` `facade.nextQuestion(filter, weakTopicsPriority)` → `InterviewService:130` Strategy `weakTopicsPriority ? weakTopicsSelectionStrategy : defaultSelectionStrategy`; `onlyWrong` → repo-запросы (`InterviewService:151/155/158`). GET-навигация, без мутации БД. Работает, не dead. | — | 🚫 WONTFIX (functional) | 🚫 DECISION_LOCK |
| `STA-12` | `stats.js(v)` в stats.html | Версионный контракт stats.js | — | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `STA-20` | Мёртвая колонка «Сброшено» (regenSum) в таблице тем stats | **Находка R0.116 (app не нужен):** `stats.html:224-227` `<td data-label="Сброшено">` → `regen-badge` при `regenSum()>0` (мёртво, AI вырезан + wipe БД) иначе `<span class="muted">—</span>` (всегда). Пост-AI колонка **всегда «—»** для всех тем → шум без информации. Источник `TopicStatsResponse.regenSum` (`StatsApiMapper:59`). CSS `.stats-page .regen-badge` (base.css:1892) — общее правило с живым `.maturity-badge`. | Удалить колонку (th+td), опц. поле `regenSum` DTO; при удалении — сверить share/print-контракт stats (кол-во `<td>`, SUM-7-класс) + `data-label`/responsive; base.css: убрать строку-селектор 1892 (не блок). App/visual+contract-gated. | 🆕 FINDING R0.116 (dead AI-остаток, P4 minor) | ✅ DONE R0.128 frontend (th+td удалены, Зрелость data-col 5→4 для sort-выравнивания, base.css .stats-page .regen-badge снят v=95, контракт 10/10; DTO `regenSum` оставлен — API-test-gated, вычисляемое-нерендерящееся безвредно) |
| `SET-21` | Мёртвые CSS-правила `.settings-page .app-layout:has(...)` (base.css:3200-3206) | **Находка R0.120 — workflow static-audit, confirmed адверсари-верификатором + самопроверкой (app не нужен):** два компаунд-правила `html[data-design] .settings-page .app-layout:has(> .personalization-card:not(.hidden))` (grid-блок 3200-3205) и его потомок `> .main-content` (3206) **не могут сматчиться никогда**. `grep app-layout` по templates+static/js = **ровно 1** совпадение (`stats.html:8` под `.stats-page`); `settings.html` (`.settings-page`) НЕ содержит `.app-layout` (0, вкл. th:class/th:classappend; JS не строит динамически). Цепочка предков `.settings-page .app-layout` не существует ни в одном DOM. Остаток редизайна /settings (стопка-карточек+sidebar `.app-layout` → ARIA-вкладки, см. [[project_settings_tabs_redesign]]): старый каркас удалён из settings, а эти 2 правила забыли снять. Комментарий 3197-3199 описывает `:has()`-гейт от несуществующей обёртки. 0 render-эффекта (правила и так не матчатся). | Удалить 2 правила (3200-3206) + комментарий 3197-3199; base.css v-бамп + контракт-тест (README R0.33: любая правка base.css → прогон `TemplateFragmentContractTest`). Едет на общем base.css-бампе window-тика (с regenerate-removal + STA-20). **R0.121: neighborhood swept — ИЗОЛИРОВАННАЯ сирота, не кластер** (все 48 `.settings-page`-хуков проверены; 5 неочевидных живы через фрагменты stats-grid/today-widget + JS-динамику `export-status-error`; чистить ровно эти 2 правила). | 🆕 FINDING R0.120 (dead-CSS, P3, 0 render-риск) | ✅ DONE R0.125 (base.css v=93, оба правила+коммент удалены, контракт-тест 10/10 green) |
| `HEAD-1` | 2 CSS-линка `?v=N` (=53) | Контракт кэш-инвалидации при правке CSS | бампать ОБЕ строки | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `HEAD-4` | font-loading `media=print`/onload + noscript | Намеренный нерендер-блокирующий flip | НЕ «чинить» | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `HEAD-7` | self-host chart.js vs CDN (CSP sourcemap) | Архитектурный + download-gated выбор | — | ⛔ DEFERRED | ⬜ RECHECK |
| `ICO-3` | нет dedicated warning-иконки (danger исп. `#i-flag`). **Уточнено р110:** `#i-flag` = double-duty на… | Намеренный reuse flag для danger-zone; distinct warning-символ = опц. визуал-полиш, только с IC-4 + требует settings.html (blocked). Не срочно | L | 🌱 BACKLOG (decorative, opt-polish с IC-4) | ⬜ RECHECK |
| `IC-5` | CTA-кнопки несут сырой юникод «→» (SR-шум + вне icon-системы). **Точный скоуп (р38-аудит):** 7 `btn… | **⚠️ БЛОКЕР — convention-решение (р38):** не механический swap. Все icon-кнопки ВЕДУТ иконкой (icon-lead, R34-конвенция); directional «next→» семантически ТРЕЙЛИТ → iconify создаёт mixed lead/trail. 3 варианта, каждый t… | M | 🌱 BACKLOG (design-decision; 2.4.4 CLEAN р117) | ⬜ RECHECK |
| `MER-1` | antiscript guard + drop при отсутствии mermaid | XSS-поверхность 'loose' закрыта; offline degrade | mermaid — внешний owner | 🚫 WONTFIX (не трогать mermaid) | 🚫 DECISION_LOCK |
| `TDW-3` | streak-inline (🔥) без aria-live | Намеренно: наполняется при каждой загрузке, не state-change | НЕ добавлять live-region | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `BAS-18` | prose `--measure` full-width | Намеренный выбор юзера (259ch на 2560) | НЕ трогать | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `DSG-1` | `linear.css` + `swiss.css` НЕ подключены ни одним `<link>` (свер. 2026-07-06/07: `head.html` грузит… | **❌ КОРРЕКЦИЯ 2026-07-07 (р99): НЕ «чистые дубли».** Прежняя запись «сигнатуры УЖЕ портированы, удалить» — НЕВЕРНА. Селектор-уровневый diff overlay↔base.css: **`linear.css` держит `html[data-design=linear] a` + `a:hover… | **НЕ механический cleanup.** Удаление меняет 0 рендера (overlay не подключён), но теряет source восстановления fidelity. Решение port-or-abandon (как DSG-2): л… | 🌱 BACKLOG (port-or-abandon decision, как DSG-2) | 🚫 ABANDONED R0.81 (linear/swiss.css git rm 9f0c425c; 0 refs, не рендерились; порт-в-base = новое решение) |
| `DSG-2` | **CONFIRMED статически (2026-07-07, app не нужен):** `broadsheet` теряет структурную identity. `bas… | При выборе `broadsheet` **токены рендерятся** (cream bg / obsidian buttons / serif — из `tokens.css` `[data-design=broadsheet]`×2), но 13 структурных акцентов ОТСУТСТВУЮТ → broadsheet = «свои токены на editorial-структу… | Портировать 13 rule-блоков из `broadsheet.css` в `base.css` (token-safe, ровно как сделали для linear/swiss) → затем `broadsheet.css` становится дублем → удали… | 🌱 BACKLOG (blocked-file + decision) | 🚫 ABANDONED R0.81 (broadsheet.css git rm 9f0c425c; порт-в-base = новое решение) |
| `APP-5` | favorite toggle (`btn-favorite`) | Флоу под решением юзера | НЕ трогать без решения | ⛔ DEFERRED | ⬜ RECHECK |
| `APP-7` | `?v=` контракт в 3 шаблонах | Бампать при правке app.js | — | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `APP-8` | `regenerateQuestion` (app.js:1047-1115) async-state НЕ консистентен с братьями-хендлерами | Extra-analysis (343/348: `aria-busy=true` + re-entrancy guard) и export (592-601: `aria-disabled`+`aria-busy`) выставляют busy-семантику и гард повторного клика; regenerate же only `.regenerating`-класс + `title='Удаляю… | L | 🌱 BACKLOG (blocked-file) | ✅ DONE R0.126 (regenerate удалён целиком — aria-busy inconsistency moot) |
| `APP-9` | JS-построенная comparison-table (app.js ~стр.384/1819, extra-analysis разбор) — `<th>` без `scope`,… | Аудит 2026-07-06: динамически собираемая таблица сравнения в доп.анализе рендерит `th` без `scope="col"/"row"` и без `caption` → SR не связывает заголовки со строками, теряется навигация по таблице (в отличие от SSR sum… | L | 🌱 BACKLOG (blocked-file, verify) | ✅ MOOT R0.130 (код-сверка): JS-построенная comparison-table удалена вместе с AI-разбором (`c0b997ba`, коммент app.js:1630). grep app.js = 0 `createElement('th')`/`buildTable`/comparison-builder → a11y-цель (scope на динамич. `<th>`) больше не существует. SSR summary/stats-таблицы scope-корректны отдельно. |
| `AIR-1` | «Показать доп. анализ» → гарантированный dead-end на КАЖДЫЙ клик | `app.js` flow (340-460): `TAKEAWAY` (395) и `CODE_TRACE` (410) пушатся **безусловно** → `requests.length ≥ 1` даже на верном ответе (на неверном +`WRONG_FEEDBACK` 358 +`COMPARISON` 376). Все 4 endpoint'а **удалены** → к… | Реком. B (сохраняет единственную выжившую ценность — related) | 🌱 BACKLOG (blocked-file + decision A/B/C) | ✅ RESOLVED R0.130 (код-сверка `c0b997ba`/р133 — §10-строка была СТАРОЙ). Факт app.js:1630-1651: AI-разбор (takeaway/trace/feedback/comparison) удалён; кнопка = related-only progressive-disclosure, показывается ТОЛЬКО при `hasRelated` (не-hardMode + `data.relatedQuestions.length>0` из /api/answer), по клику `renderRelatedQuestions` без сети + `aria-expanded` + self-hide. **Dead-end устранён, 0 fetch к удалённым эндпоинтам** (все API-константы → живые контроллеры). Decision A/B/C moot — реализована реком. B. App-gated только визуал раскрытия, не корректность. |
| `AIR-2` | оба тоггла доп.анализа НЕ гейтятся `aiEnabled` | `#extra-analysis-toggle` (`post-answer-controls.html:9`, focus-флоу) и `#extra-analysis-toggle-result` (`result.html:113`, /answer) рендерятся **безусловно** (в отличие от `.btn-regenerate` result.html:49 и `.question-s… | Вместе с AIR-1 | 🌱 BACKLOG (blocked-file) | ✅ RESOLVED R0.130 (код-сверка): result-toggle удалён 9f0c425c; focus-toggle гейтится `hasRelated` (реальные related из /api/answer, app.js:1634-1636), раскрывает контент — не dead-end. Гейт на `aiEnabled` больше не релевантен (AI-разбора нет) — корректный гейт = наличие related-контента. |
| `AIR-3` | выжившие «Похожие вопросы» подавлены битым флоу | reveal `related.classList.remove('hidden')` (`app.js:447`) — в ветке **НЕ-полного-провала**; т.к. все fetch'и падают (AIR-1), ветка недостижима → единственная выжившая доп.аналитика (related-вопросы, backend `RelatedQue… | Вместе с AIR-1 (реком. B) | 🌱 BACKLOG (blocked-file) | ✅ RESOLVED R0.130 (код-сверка `c0b997ba`/р133): related-вопросы теперь — ЕДИНСТВЕННОЕ штатное наполнение кнопки (app.js:1645 `renderRelatedQuestions`, a11y `role=list`/`role=listitem` добавлен R0.127), не подавлены. Прежнее «недостижимая ветка» снято вместе с removal AI-fetch'ей. |
| `AIR-4` | `regenerateQuestion` (app.js:1047-1115) зовёт удалённый `POST /api/regenerate` | Хендлер + делегирование от `.btn-regenerate` (1126) живы в JS, но сама кнопка под `th:if=${aiEnabled}` (result.html:49) → в seed-first **не рендерится** → путь **недостижим** (не user-facing). Мёртвый JS к будущей чистк… | Удалить при чистке app.js (низкий приоритет) | 🌱 BACKLOG (blocked-file, dead-not-reachable) | ✅ DONE R0.126 (regenerateQuestion + делегат + API.REGENERATE удалены; бэкенд /api/regenerate оставлен §4) |
| `AIR-6` | мёртвая AI-ветка в empty-state focus-training | **User-facing часть УЖЕ ЗАКРЫТА (свер. R0.112, app не нужен):** абзац `<p th:if=${generationUnavailable}>«AI временно недоступен…»` **удалён** — на его месте комментарий-аннотация `focus-training.html:185-188` («…никогда не рендерилась и вводила в заблуждение — удалена»); removal закоммичен (файл не в незакоммич. наборе). Остаток — рудиментарная plumbing: `generationUnavailable = false` (`FocusTrainingPageService:54`, не переприсваивается 55-91) протянут через `FocusPageState`→model-attr; 4 empty-ветки (190-193) держат всегда-истинные `!generationUnavailable and …` guards (no-op, безвредны — ветки корректно гейтятся reviewMode/сессией). | Вычистить рудимент. флаг+guards при AI-cleanup (трогает service+model-контракт → нужны contract-тесты) | ◐ user-facing DONE / рудимент | ✅ DONE R0.129 (флаг снят из FocusTrainingPageService:54/94/147 + MvcModelAttributeMapper:47 + 4 guard'а focus-training.html; 7 файлов, render/mapper-тесты на Mockito → app не нужен; 5-ошибочный compile пойман+исправлен; 23 теста green; финальная 5/5 единица window-тика) |
| `RIA-2` | reverse-орфан: `POST /study-confirm` без вызывающего | Живой маршрут `studyConfirm` (`InterviewMvcController:157` → `InterviewFlowMvcService:49` → `applyStudyConfirm` → focus-redirect), но **НИ один шаблон/JS не постит на него** (grep `study-confirm` по static+templates = 0… | Live-verify STUDY + решение пользователя | ⛔ DEFERRED (needs live+decision, out-of-frontend-scope) | ✅ RESOLVED R0.131 (код-сверка `c0b997ba`/р133 — §10-строка СТАРАЯ). Факт focus-training.html:124-132: div `study-learn-phase` (гейт `studyLearnPhase and !flashcardMode`) показывает полный ответ + submit-форму «Проверить себя» POST `@{/study-confirm}` (:129). **/study-confirm БОЛЬШЕ НЕ reverse-orphan** — grep теперь находит форму. CSRF авто-инъектится через `th:action` (консистентно со всеми 5 sibling POST-формами шаблона — явного `_csrf` нет ни у одной). Wiring полон: форма→живой роут (`InterviewMvcController:157`→`applyStudyConfirm`). App-gated только визуал click-through STUDY (backend-поведение вне frontend-scope), не корректность проводки. |

---

## 11. Mockup → production mapping contract

Для каждой surface создать mapping:

| Поле | Что зафиксировать |
|---|---|
| Mockup file/state | точный URL/fixture |
| Production template | source path |
| Server conditions | `th:if`, model attributes, modes |
| DOM hooks | id/class/data/ARIA hooks |
| JS consumers | listeners, selectors, mutation points |
| Form/API contract | action, method, CSRF, parameters |
| Progressive enhancement | поведение без JS |
| Tokens/components | production primitives |
| Cache-busting | все consumers |
| Tests | существующие и новые targeted tests |
| Rollback | как вернуть предыдущий source |
| Risk | business/state/data risk |

`PORT_READY` запрещён при пустом обязательном поле.

---

## 12. Pixel и production parity

Под «pixel-complete» понимается не буквальное сравнение субпиксельного antialiasing разных ОС,
а доказанная геометрическая и визуальная эквивалентность:

- необъяснимое layout-расхождение не больше 1 CSS px;
- unexpected changed regions = 0;
- horizontal overflow = 0;
- clipping/overlap = 0;
- shell/gutter/rail bounds соответствуют mockup;
- typography roles и line-height соответствуют;
- controls, borders, radii и focus offsets соответствуют;
- light/dark/design variants проверены;
- runtime fonts/assets действительно загружены;
- screenshot и DOM/computed metrics согласуются.

При осознанном production-отклонении от mockup нужна запись:
`constraint → approved deviation → user effect → evidence`.

---

## 13. QA-гейты переноса

### До изменения production

- mockup `APPROVED`;
- mapping `PORT_READY`;
- target files clean;
- app/runtime доступен либо есть безопасная статическая стратегия;
- rollback определён;
- business contract понятен.

### После изменения production

- source скопирован в runtime только для live verification;
- cache-busting обновлён;
- Network грузит новую версию;
- target screen проверен;
- shared consumers проверены;
- light/dark;
- relevant designs;
- responsive;
- keyboard/focus;
- accessibility;
- no-JS;
- console/network;
- targeted tests;
- screenshot/pixel diff;
- bounded diff;
- atomic commit.

---

## 14. Full-refactor критерии после parity

Отдельно от переноса проверить:

- ненужные wrappers и DOM duplication;
- повторяющиеся CSS rules;
- specificity wars;
- stale selectors;
- dead overlays;
- fragment/component reuse;
- JS listeners и races;
- async busy/error states;
- table semantics;
- forms;
- loading/empty/error;
- print;
- forced-colors;
- reduced-motion;
- performance;
- progressive enhancement;
- content-independent layout resilience.

Нельзя помечать `VERIFIED`, если был выполнен только визуальный перенос.

---

## 15. Приоритизация

Порядок:

1. broken primary flow;
2. missing surface/mockup;
3. business-contract mismatch;
4. critical a11y;
5. unusable mobile/overflow;
6. missing state/error/recovery;
7. mockup-production mismatch;
8. design-system inconsistency;
9. duplication/architecture;
10. performance;
11. visual polish;
12. microcopy.

Оценка:

```text
Priority =
(Impact × Reach × Confidence × Evidence)
/
(Risk × Effort)
```

P0 severity имеет приоритет над score.

---

## 16. Единица одного тика

Один тик:

- один screen/state-family в mockup;
- или mapping одной surface;
- или port одной surface-family;
- или parity fix;
- или post-port refactor одной области;
- или regression slice.

Не смешивать независимые фазы двух экранов.

---

## 17. Collision guard

Перед каждым тиком:

```bash
git status --short
git status --porcelain -- <candidate paths>
```

Dirty обязательного файла блокирует всю связанную цепочку:

```text
tokens.css ↔ base.css ↔ head.html
app.js ↔ result/settings/focus templates
stats.js ↔ stats.html
shared fragment ↔ все consumers
```

При blocker:

- не захватывать чужие hunks;
- выбрать независимую задачу;
- либо провести read-only аудит;
- поставить `BLOCKED`;
- не подделывать прогресс.

---

## 18. Progress log

Каждый тик добавляет newest-first запись:

```markdown
### YYYY-MM-DD — <Task ID>: <Surface / phase>

- Pipeline stage:
- Previous status:
- Evidence:
- Problem:
- Mockup change:
- Production change:
- User-visible effect:
- Mapping:
- Files:
- Themes/designs:
- Viewports/DPR/zoom:
- Browser QA:
- Accessibility:
- Tests:
- Screenshots/pixel diff:
- Cache-busting:
- Commit:
- Residual risk:
- Next:
- 🔑 Lesson:
```

---

## 19. Definition of Done поверхности

Surface получает `✅ VERIFIED`, только если:

1. inventory полный;
2. mockup существует;
3. все states deterministic;
4. mockup QA пройден;
5. mapping полный;
6. port завершён;
7. production parity доказана;
8. post-port refactor завершён;
9. responsive matrix пройдена;
10. default/light/dark пройдены;
11. relevant designs пройдены;
12. DPR 1/2 и zoom 200% проверены;
13. keyboard/focus пройдены;
14. WCAG blockers = 0;
15. loading/empty/error/recovery проверены;
16. no-JS/PE проверен;
17. console/network без связанных ошибок;
18. targeted tests проходят;
19. screenshots актуальны;
20. pixel diff объяснён;
21. shared consumers проверены;
22. cleanup завершён;
23. docs updated;
24. atomic commit записан;
25. unresolved risk отсутствует либо явно принят.

---

## 20. Stop condition полного раунда

Раунд переходит в maintenance, когда:

```text
Production routes discovered: all
Mockup coverage: 100%
Approved mockups: 100%
Port mappings complete: 100%
Production ports complete: 100%
Production parity: 100%
Post-port refactor: 100%
Regression QA: 100%
Blocking a11y defects: 0
Horizontal overflow defects: 0
Unexplained pixel diffs: 0
Orphan mockups: 0
Orphan production surfaces: 0
Unresolved P0/P1: 0
Dirty changes created by loop: 0
```

После этого loop не запускает новый редизайн. Он переходит в режим:

- regression;
- новые product changes;
- редкие viewports;
- 200% zoom;
- keyboard edge cases;
- performance;
- design-system drift;
- verified-clean evidence.

---

## 21. Новый progress log

Старый progress не переносится как приёмка.

Первая запись нового раунда:

```markdown
### 2026-07-11 — RESET: mockups → production → refactor

- Previous history: preserved in Git.
- Previous acceptance statuses: invalidated.
- Product decisions: preserved.
- Surface inventory: pending re-discovery.
- Mockup status: all RECHECK.
- Production status: all RECHECK.
- Next: exhaustive static/browser inventory.
```

### 2026-07-11 — R0.1: RESET завершён — inventory + registry + QA-matrix

- **Фаза A (exhaustive inventory) выполнена статически** из: контроллеров
  (`@Get/PostMapping` — 6 view-routes + 11 API/POST), `SERVER_CONTRACT.md` (§2 model-attrs,
  §5 DOM-хуки, §8 a11y), списка templates/fragments (6 страниц + 15 фрагментов), макетов
  (`design/mockups/*.html`), state-маркеров в `focus-training.html` (flashcard/study/empty/
  reviewMode/generationUnavailable).
- **Создан `design/mockups/screen-registry.json`** — 7 surfaces (shell, focus-training,
  result, session-summary, settings, stats, error) × states × server-contract × phase,
  все статусы RECHECK/TODO (**Old PASS carried forward = 0** ✓). Дименсии: 11 дизайнов,
  2 темы, 7 вьюпортов, DPR1/2, zoom100/200, a11y-оси.
- **Создан `design/mockups/PIXEL_QA_MATRIX.md`** — surface×8-осей матрица (detector/AA/
  full-width/a11y/states/parity/contract/no-js·print), все ячейки RECHECK/TODO.
- **Пробелы покрытия зафиксированы:** `error.html` БЕЗ макета (worst-first §7.2 missing
  surface); print-CSS не проверен; zoom200/DPR1 не покрыты; toast/success не инвентаризован.
- **Блокеры (§6):** параллельная сессия держит незакоммиченный WIP в base.css/app.js/
  stats.js/head.html/settings.html/result.html/stats.html/focus-training.html/
  TemplateFragmentContractTest.java → порт/refactor этих поверхностей BLOCKED; независимые
  задачи (session-summary, error-макет, mockup-recheck) и read-only аудит — доступны.
- **Reset commit** — отдельный, только PLAN/PROMPT + registry + matrix (explicit pathspec;
  staged skill-dirs и параллельный prod-WIP НЕ захвачены).
- **Reset завершён** (§2 done-условие): registry+matrix существуют, все surfaces/production
  = RECHECK/TODO, 0 carried-forward. **Следующий тик:** worst-first — либо `error.html`-макет
  (missing surface, дерево чисто), либо B_MOCKUP_RECHECK независимой поверхности.

### 2026-07-11 — R0.2: MOCKUP `error.html` создан (missing surface §7.2)

- **Worst-first выбор:** error — единственная поверхность БЕЗ макета (§7.2 missing surface);
  прод `error.html` НЕ в параллельном WIP → задача независима, не заблокирована.
- **Создан `design/mockups/error.html`** по контракту прод-`error.html`: 5 состояний
  (400/401/403/404/500) через switcher + `?code=`-param, детерминированно, без backend.
  Композиция: editorial `error-code` (крупный serif, декор aria-hidden) + `error-title`
  (serif) + `error-body` (проза) + `error-details` (dev-мета: путь/статус/причина/время) +
  `error-actions` (На главную/Аналитика/Настроить). **App-shell main+рельс** «куда пойти»
  (edge-to-edge `.wrap` + `.error-grid`, брейк 1080px) — исполняет мандат полной ширины.
  Канонический хром (skip-link/site-head/mobile-menu/theme-toggle) переиспользован из shell.
- **QA-гейт §9:** detector exit 0; **AA обе темы** — все текст-пары ≥4.5 (title 15.55,
  body 7.77, eyebrow 5.75, btn-primary 6.01, rail-hint ink-3/surface 5.31 light; dark выше
  7.18–10.66); 0 em-dash; reduced-motion fail-safe (`.anim` opacity 1 дефолт); a11y
  (h1#error-heading aria-labelledby, mock-switch aria-pressed, mobile aria-expanded/controls,
  focus-visible ring, skip-link→main). Full-width — **структурно** (тот же паттерн, что и
  верифицированные экраны); живой screenshot отложен (capture флапает) на parity-фазу.
- **registry+matrix обновлены:** error phase A_DISCOVER→B_MOCKUP_RECHECK, gap закрыт.
- **Следующий тик:** worst-first — B_MOCKUP_RECHECK независимой поверхности (session-summary
  — не в WIP) ИЛИ D_PORT_MAPPING error (прод error.html чист → можно готовить перенос).

### 2026-07-11 — R0.3: RECHECK макета `session-summary.html` (независимая поверхность)

- **Worst-first выбор:** collision guard (§6) — параллельная сессия держит dirty
  base.css/app.js/stats.js/head.html/result.html/settings.html/stats.html/focus-training.html/
  тест → 5 поверхностей BLOCKED. Независимы: error (макет уже R0.2) и **session-summary**
  (прод `session-summary.html` НЕ в WIP). Взят session-summary — RECHECK его макета.
- **Проверено (§9 mockup-gates):**
  - **detector** exit 0.
  - **AA обе темы** — 18 пар (семантические тона на wash-заливках + ink-иерархия + кнопки/
    бейджи): light worst 4.95 (badge error-on/error), dark worst 5.24 (acc-tile-err) — все
    ≥4.5. Скрипт `scratchpad/aa_summary.py` (OKLCH→linear-sRGB→WCAG).
  - **a11y-каркас** полный: skip-link→#main-content, `main[tabindex=-1]`, секции
    `aria-labelledby`, таблица `caption`+`th scope=col`, mock-switch `role=group`+`aria-pressed`,
    nav `aria-current`, theme-toggle `aria-label` обновляется в цикле, иконки `aria-hidden`,
    reduced-motion fail-safe (`.anim{opacity:1}`, анимация только в no-preference).
  - **full-width** структурно (`.wrap{width:100%}` edge-to-edge, 1-кол→2-кол на 1080px со
    sticky-рельсом) — живой screenshot отложен на parity-фазу.
  - **states** покрыты: strong/weak (mock-switch), topic-table, mistakes-list, recommendations,
    copy/print. `empty(no-summary)` = redirect контроллера, корректно не mockup-state.
- **Наблюдение (не блокер, на parity/port-фазу):** `thead th{position:sticky;top:0}` внутри
  `overflow-x`-only обёртки на 4-строчной таблице фактически не активируется и может уходить
  под sticky-шапку. Решить при порте (прод-таблица устроена иначе) — не правлю макет в RECHECK.
- **Ledgers:** matrix session-summary detector/AA/full-width*/a11y/states → PASS (parity/
  contract/no-js остаются TODO — нужен bootRun); registry notes = RECHECK ПРОЙДЕН.
- **Следующий тик:** worst-first — `D_PORT_MAPPING` для error ИЛИ session-summary (обе прод-
  поверхности чисты, не в WIP) — подготовить маппинг разметки макета на DOM-хуки контракта.

### 2026-07-11 — R0.4: D_PORT_MAPPING для session-summary (планирование, прод не тронут)

- **Worst-first выбор:** обе независимые поверхности (error, session-summary) на
  B_MOCKUP_RECHECK-PASS; следующая стадия пайплайна — `D_PORT_MAPPING` (нельзя прыгать
  из MOCKUP_APPROVED в PORT без полного маппинга). Взят session-summary (крупнее, богаче
  на контракт). **Read-only планирование — правок прод НЕ вносит** → безопасно под §6.
- **Создан `design/mockups/PORT_MAPPING_session-summary.md`:** таблица «mockup-элемент →
  прод-хук → действие/риск» + сводка контракта (роут `GET /session-summary`, model-attr
  `summary`, бины `@modeUtils`/`@topicUtils`/`@markdownRenderService`, PE-инъекция
  `.summary-tools`, фрагменты head/header, app.js не грузится).
- **Зафиксированы КРИТ-риски порта (Фаза E):**
  1. Таблица тем: вернуть `role=table/rowgroup/columnheader/cell` + `tabindex=0` +
     `td[role=rowheader]` (макет их не содержит; иначе a11y-регресс §8 в Safari/VO).
  2. `recommendations[]` — плоские строки: bold/link из макета недостижим без backend
     (**ЗАПРЕЩЕНО §4**) → порт оставляет плоский текст, иконка декоративная.
  3. `mistakes` вопрос — через `th:utext=toInlineHtml` (inline-markdown), не `th:text`.
  4. score → verdict-headline: новый визуальный элемент из `summary` (без новых model-attr),
     `h2.visually-hidden` «Результат сессии» сохранить для SR-heading-нав.
  5. SSR `data-design="editorial"` не менять (Instrument = opt-in 11-й, решение пользователя).
  6. Наблюдение R0.3 (sticky thead) — решать на паритет-фазе, в макет не тащить.
- **Ledgers:** registry session-summary phase B_MOCKUP_RECHECK→**D_PORT_MAPPING**,
  port_status **MAPPED**, `port_mapping_doc` проставлен.
- **Следующий тик:** `D_PORT_MAPPING` для **error** (замкнуть маппинг обеих независимых
  поверхностей) ЛИБО — когда прод-дерево очистится — вход в Фазу E (порт по §5).

### 2026-07-11 — R0.5: D_PORT_MAPPING для error (планирование, прод не тронут)

- **Worst-first выбор:** замыкаю маппинг-фазу по обеим независимым поверхностям. error —
  простейшая (нет ARIA-таблиц, нет PE-инъекции, нет summary-модели). **Read-only — прод не
  тронут** → безопасно под §6.
- **Создан `design/mockups/PORT_MAPPING_error.md`:** таблица «mockup→прод-хук» + контракт
  (страница от Spring error-handler, model-attrs `status`/`error`/`message`/`path`/`timestamp`;
  title/body/error-code — **серверный `th:with`+тернарник по коду**).
- **Зафиксированы КРИТ-риски порта (Фаза E):**
  1. **Серверное ветвление** title/body/error-code из `status` — сохранить `th:with`+тернарники;
     макетные `STATES`/`?code=`/switcher — только для ревью, в прод НЕ идут.
  2. **`.error-rail` vs `.error-actions`** — не дублировать «куда пойти»; развести роли или
     не портировать рельс (полноширинность от него не зависит).
  3. `aria-labelledby="error-heading"` на `section` + `id` на `h2` — сохранить связку.
  4. Нет `app.js`-зависимостей — статичная страница, PE-скрипт не нужен.
  5. SSR `data-design` не менять.
- **Ledgers:** registry error phase B_MOCKUP_RECHECK→**D_PORT_MAPPING**, port_status **MAPPED**,
  `port_mapping_doc` проставлен.
- **Веха:** обе независимые поверхности (session-summary, error) теперь **MAPPED** — маппинг-фаза
  для доступного фронта замкнута. Дальше: (а) вход в **Фазу E** (порт чистого прод-файла
  отдельным тиком §5); (б) 5 BLOCKED-поверхностей ждут коммита параллельной сессии.
- **Следующий тик:** начать **Фазу E** — порт error (простейшая, прод чист) отдельным
  §5-изолированным тиком, ЛИБО re-audit BLOCKED-поверхностей на разблокировку.

### 2026-07-11 — R0.6: B_MOCKUP_RECHECK reference-экрана `focus-question`

- **Уточнение блокировки:** порт error/session-summary (Фаза E) **тоже BLOCKED** — их
  структурные `.error-*`/`.summary-*` стили живут в `base.css`, который держит параллельный
  WIP. Значит порт ЛЮБОЙ поверхности сейчас упирается в `base.css`. Независимая работа без
  прод — **recheck чистых макетов** (`design/mockups/*.html` не в WIP). Взят reference-экран
  `focus-question` (дизайн-якорь, ещё не rechecked в этом раунде).
- **Проверено (§9):** detector exit 0. **AA обе темы** — все ТЕКСТОВЫЕ пары ≥4.5 (light
  worst 4.95 badge error-on/error, dark 5.24). Единственная пара ниже 4.5 — `opt-mark`
  галочка верного варианта `success-on/success` = **4.28 light**; это **графический объект**
  (WCAG 1.4.11, порог 3:1), `aria-hidden`, избыточна с обводкой `.opt--correct` + тегом
  «верный ответ» + тинтом бейджа → **compliant** (CRITIQUE **C11**, РЕШЕНО; общий токен
  `--success` не риплю в recheck). **a11y-каркас образцовый, паритет §8:** radiogroup,
  progressbar aria-valuenow, kbd-help `role=dialog`+focus-trap+`?`/Esc+возврат фокуса,
  focus-visible ring, reduced-motion fail-safe, kbd-hint гейт (нет touch-мёртвых подсказок),
  моб. sticky submit-bar + safe-area. **full-width** структурно.
- **Coverage-gap (CRITIQUE C12):** макет покрывает 4 ядровых состояния (active/result/empty/
  done); НЕ покрыты 7 из registry-списка focus-training (flashcard-reveal/grade, study-LEARN,
  generationUnavailable, loading, inline-alert/error, no-js, diagram). Задел на доработку
  reference-экрана (порт focus-training всё равно BLOCKED).
- **Ledgers:** matrix focus-training detector/AA‡/full-width*/a11y → PASS, states → PASS(core)†;
  registry port_status → BLOCKED + recheck-заметка; CRITIQUE C11/C12 + журнал cr.8; gaps_found +1.
- **Следующий тик:** worst-first — recheck следующего чистого макета (`result` / `settings` /
  `stats`) ЛИБО вход в Фазу E по разблокировке `base.css`.

### 2026-07-11 — R0.7: B_MOCKUP_RECHECK экрана `result` (разбор ответа)

- **Worst-first:** база всё ещё BLOCKED (base.css в WIP); recheck следующего чистого макета.
  Взят `result` (разбор ответа + пост-ответный анализ).
- **Проверено (§9):** detector exit 0. **AA обе темы** — все текст-пары ≥4.5 (light worst
  5.52, dark 5.24); проза на wash-заливках 11–14; opt-mark галочка 4.28 = граф. объект
  (C11, compliant). **a11y-каркас полный** (progressbar aria-valuenow, options `role=group`,
  analysis aria-label, focus-ring, reduced-motion, aria-hidden иконки). **full-width**
  структурно (result-grid main+aside, брейк 1080px; рельс = «разбор глубже»).
- **Coverage-gap (CRITIQUE C13):** покрыты correct/incorrect/analysis(takeaway+trace+related)/
  favorite; НЕ покрыты confidence-виджет (🎲🤔💪), regenerate, no-js-fallback.
- **Наблюдение на порт:** вердикт — статичный `<p>`, не live-region; `aria-live` (§8
  `#result-feedback`) проводить при порте, НЕ дефект макета.
- **Ledgers:** matrix result detector/AA‡/full-width*/a11y → PASS, states → PASS(core)§;
  registry port_status → BLOCKED + recheck-заметка; CRITIQUE C13 + журнал cr.9; gaps_found +1.
- **Следующий тик:** worst-first — recheck `settings` ЛИБО `stats` (оба чистых макета),
  ЛИБО вход в Фазу E по разблокировке `base.css`.

### 2026-07-11 — R0.8: B_MOCKUP_RECHECK экрана `settings` (вкладки, богатейший a11y)

- **Worst-first:** база всё ещё BLOCKED; recheck следующего чистого макета. Взят `settings`
  (ARIA-вкладки Сессия/Оформление/Данные — богатейшая a11y-поверхность).
- **Проверено (§9):** detector exit 0. **AA обе темы** — все пары ≥4.5 (light worst 4.95
  btn-danger error-on/error, dark 5.24; danger-карта ink-2/error-wash 6.71; seg-btn 6.56–6.93;
  поля ink/paper 15.55). **a11y ОБРАЗЦОВЫЙ WAI-ARIA:** tablist (roving tabindex + стрелки
  ↑↓←→/Home/End), seg-control radiogroup+aria-checked+стрелки, font-stepper role=status+
  aria-live, toggle focus-ring, label for/id — паритет §3 (#filters-form/#session-form)/§8.
  **full-width** структурно (settings-grid rail+panels 1080px; set-grid auto-fit).
- **Coverage-gap (CRITIQUE C14):** покрыты 3 вкладки + export json/csv + 7 осей + filters +
  session + streak; НЕ покрыт reset-options-confirm (danger-кнопка есть, шаг подтверждения нет).
- **Ledgers:** matrix settings detector/AA/full-width*/a11y✦ → PASS, states → PASS(core)◊;
  registry port_status → BLOCKED + recheck-заметка; CRITIQUE C14 + журнал cr.10; gaps_found +1.
- **Следующий тик:** worst-first — recheck `stats` (последний чистый макет, замкнёт recheck
  всех 7 поверхностей), ЛИБО вход в Фазу E по разблокировке `base.css`.

### 2026-07-11 — R0.9: B_MOCKUP_RECHECK `stats` → найден+ПОФИКШЕН дефект контраста (C15)

- **Worst-first:** последний нерасчеканный чистый макет (`stats`) — замыкает recheck всех 7.
- **Проверено (§9):** detector exit 0. **AA обе темы:** все ТЕКСТ-пары ≥4.5.
- **РЕАЛЬНЫЙ ДЕФЕКТ (не «compliant», как C11):** амбер-заливки данных `--spark` на треке
  `surface-2` = **2.06:1 < 3:1** (WCAG 1.4.11, light) — столбцы «к повтору»/mid-точность
  почти не видны в светлой теме. **ФИКС локально в stats.html:** `bar-due`/`bar-acc-mid`/
  `cp-due`/`fc-fill.is-today` → `--spark-ink` (5.02 light / 8.72 dark ≥3:1); легенда `.sw-due`
  синхронизирована; яркий `--spark` (декор) не тронут. learned↔due различаются по тону
  (teal↔амбер, CVD-safe) + числовой лейбл. После фикса все столбцы ≥3:1 (C15 **DONE**).
- **a11y образцовый:** SVG `role=img`+aria-label+«данные в таблице»+fallback `role=status`;
  таблица `caption`+`th scope`+`aria-sort`+клавиши Enter/Space+статус-анонс; скролл-регион
  `role=region tabindex=0` — паритет §5 (stats.js хуки)/§6. **full-width** структурно.
- **Coverage-gap (C16):** покрыты overview/charts+fallback/sortable-table/forecast/gaps;
  НЕ покрыты empty/cold-start (осознан в проде) + search.
- **ВЕХА:** recheck всех 7 поверхностей ЗАМКНУТ (error/session-summary/focus-question/result/
  settings/stats). Найден 1 реальный дефект за проход (C15, пофикшен), остальное — compliant.
- **Ledgers:** matrix stats detector/AA♦/full-width*/a11y → PASS, states → PASS(core)◊;
  registry port_status → BLOCKED + recheck/fix-заметка; CRITIQUE C15(DONE)/C16 + журнал cr.11.
- **Следующий тик:** база всё ещё BLOCKED. Worst-first — доработка coverage-gaps макетов
  (C12 focus-states / C13 result-states / C14 settings-confirm / C16 stats-states) ЛИБО
  вход в Фазу E по разблокировке `base.css`.

### 2026-07-11 — R0.10: закрыт C14 — confirm-модалка необратимого сброса в `settings`

- **Loop → 10m** (cron e2e2b139, `*/10 * * * *`; старый 15m f11d40dd удалён).
- **Worst-first:** после замыкания recheck (R0.9) — самый ценный coverage-gap = **C14**
  (data-safety: деструктив-кнопка «Сбросить банк вариантов» срабатывала БЕЗ подтверждения).
  Параллельные staged skill-dirs съехали (сессия закоммитила); база всё ещё dirty → порт BLOCKED,
  но `settings.html` (макет) чист → правка безопасна.
- **Добавлено в `settings.html`:** confirm-overlay `role=alertdialog` (`aria-modal`,
  `aria-labelledby`/`aria-describedby`), кнопки «Отмена»/«Сбросить банк», JS: open/close +
  focus-trap + Esc + возврат фокуса на триггер + scrim-click close; **дефолт-фокус на «Отмена»**
  (безопасный дефолт деструктива); danger-кнопка → `aria-haspopup=dialog`. Overlay-язык =
  Instrument (как kbd-help в focus-question).
- **QA (§9):** detector exit 0; AA обе темы (title/btn-secondary 15.55, desc 7.77/9.23,
  btn-danger 4.95/6.13 — все ≥4.5).
- **Ledgers:** CRITIQUE C14 **DONE** + журнал cr.12; matrix settings states → PASS (◊-сноска
  переназначена на stats, добавлена settings-states-полны заметка); registry states ПОЛНЫ;
  gaps_found C14 закрыт. Устанавливает паттерн подтверждения деструктива для порта
  (`POST /settings/reset-options`).
- **Следующий тик:** worst-first — C16 (stats states: search) / C12 (focus states) /
  C13 (result states) ЛИБО Фаза E по разблокировке `base.css`.

### 2026-07-12 — R0.11: замкнут полный 7-поверхностный B_MOCKUP_RECHECK — `shell` + фикс C17

- **Worst-first:** `shell` (глобальный хром) — единственная поверхность на `RECHECK` по всей
  строке матрицы (6 страничных пройдены R0.3–R0.10). Collision-guard: `base.css`/прод-шаблоны
  всё ещё dirty (порт BLOCKED), но `shell.html` (макет) чист → правка безопасна.
- **RECHECK-результат:** detector exit 0. AA обе темы — 12 текст-пар хрома (шапка/нав/меню/
  подвал/kbd-help) все ≥4.5 (worst 5.31 ink3/surface light; skip/btn signal-on/signal-strong
  6.01; nav-current signal-ink/signal-wash 6.54; kbd 13.1). a11y образцовый: skip→#main-content,
  `main tabindex=-1`, header nav `aria-label`, `nav-menu-btn` `aria-expanded`+`aria-controls`,
  theme-toggle `aria-label` синх в JS (авто/светлая/тёмная), kbd-help `role=dialog`+`aria-modal`+
  `aria-labelledby`/`describedby`+focus-trap+Esc+возврат фокуса, streak `role=group`, декор-svg
  `aria-hidden`. States полны (6): header-nav/mobile-drawer/theme-toggle(3)/kbd-help-modal/
  skip-link/footer.
- **Найден+пофикшен C17:** графобъект `.streak-fill` (полоса стрика, `width:72%` = доля
  прогресса) `--spark` на треке `surface-2` = **2.06:1 < 3:1** (light) — тот же класс, что C15
  в stats. **ФИКС:** `.streak-fill` → `--spark-ink` = 5.02 light / 8.72 dark ≥3:1 (локально в
  `shell.html`; декор `--spark` не тронут; `.streak-flame`/`.streak-count` уже spark-ink).
- **QA (§9):** detector exit 0 до и после фикса; AA-скрипт (OKLCH→sRGB) обе темы.
- **Ledgers:** matrix shell-строка RECHECK→PASS (+сноска `✧`); registry shell notes + states
  ПОЛНЫ; CRITIQUE C17 **DONE** + журнал cr.13; gaps_found обновлён. **Итог: все 7 поверхностей
  recheck-PASS — полный B_MOCKUP_RECHECK замкнут.**
- **Следующий тик:** worst-first — coverage-gaps C12 (focus states: flashcard/study-LEARN/
  generationUnavailable/loading/inline-alert/no-js/diagram) / C13 (result: confidence/regenerate/
  no-js) / C16 (stats: search) ЛИБО Фаза E (порт) по разблокировке `base.css`.

### 2026-07-12 — R0.12: C12 частично — состояние `flashcard` (изучение/study-LEARN) в reference

- **Worst-first:** после замыкания 7-поверхностного recheck (R0.11) крупнейший coverage-gap —
  `focus-question` (7 недостающих состояний, C12). Collision-guard: mockups+PLAN чисты,
  прод-WIP dirty (порт BLOCKED). Взял самую ценную единицу — целый ОТСУТСТВОВАВШИЙ режим.
- **Добавлено состояние `flashcard`** (mock-switch «Флешкарта (изучение)»): reveal→grade.
  Под-состояние 1 — ответ скрыт, «Показать ответ» (disclosure: `aria-expanded`/`aria-controls`,
  Пробел или click, скоуп-гард). Под-состояние 2 — ответ-проза (карточка `1px` border +
  mono-заголовок «Ответ» signal-ink с dot-маркером; фокус на ответ, `aria-live=polite`) +
  самооценка 1–4 (Снова/Трудно/Хорошо/Легко, `role=group`, key+имя, крайние оценки —
  семантический край error/success на hover в дополнение к тексту). Рейка изучения + aside-хинты.
- **Слоп-ловушки (детектор поймал, исправлено):** (1) `border-left: 3px solid` = [side-tab]
  (главный AI-tell) → полный `1px` border + dot-акцент; (2) [em-dash-overuse] в прозе → тире→
  двоеточия/точки.
- **QA (§9):** detector exit 0 (после фиксов); AA обе темы все пары ≥4.5 (flash-h2 signal-ink/
  surface 6.41/8.32; flash-body ink/surface 14.4; ink3/surface 5.31; grade uniform).
- **Ledgers:** CRITIQUE C12 **IN_PROGRESS** + журнал cr.14; matrix †-сноска (flashcard покрыт);
  registry focus-training notes + states + gaps_found. Остаются 5 состояний C12.
- **Следующий тик:** worst-first — следующее состояние C12 (generationUnavailable / loading /
  inline-alert / no-js / diagram) ЛИБО C13 (result states) / C16 (stats search), ЛИБО Фаза E
  (порт) по разблокировке `base.css`.

### 2026-07-12 — R0.13: C12 дальше — состояние `alert` (inline-alert fragment)

- **Worst-first:** после flashcard (R0.12) следующий по ценности недостающий кусок C12 —
  переиспользуемый inline-alert (прод-фрагмент `inline-alert.html`, P1 error/notice, полностью
  отсутствовал). Collision-guard: mockups+PLAN чисты, прод-WIP dirty (порт BLOCKED).
- **Добавлено состояние `alert`** (mock-switch «Уведомления»): демо 3 вариантов в колонке main —
  **error** (`role=alert`), **info** (`role=status`, «Режим флешкарты» = заодно messaging
  generationUnavailable), **warn** (`role=status`). Каждое: иконка (alert-triangle/info-circle) +
  заголовок (семантический ink) + текст (`--ink`) + dismiss (`aria-label`, закрытие делегированным
  click-JS). Цвета `--error-ink`/`--signal-ink`/`--spark-ink` на `*-wash`.
- **QA (§9):** detector exit 0; AA обе темы все пары ≥4.5 (семантический ink на wash: error
  5.52/5.24, signal 6.54/6.87, spark 5.41/7.76; текст ink на wash 11–14); прозе без em-dash.
- **Ledgers:** CRITIQUE C12 (закрыт inline-alert/error, остаются 4) + журнал cr.15; matrix
  †-сноска; registry notes/gaps_found. C12 остаётся IN_PROGRESS.
- **Следующий тик:** worst-first — следующее состояние C12 (generationUnavailable / loading /
  no-js; diagram отложить — Mermaid self-authorship запрещён §4) ЛИБО C13 (result) / C16 (stats),
  ЛИБО Фаза E (порт) по разблокировке `base.css`.

### 2026-07-12 — R0.14: C12 дальше — состояние `loading` (скелет загрузки)

- **Worst-first:** после inline-alert (R0.13) следующий недостающий кусок C12 — loading-
  placeholder (P1 loading-state). Collision-guard: mockups+PLAN чисты, прод-WIP dirty (порт BLOCKED).
- **Добавлено состояние `loading`** (mock-switch «Загрузка»): скелет **повторяет раскладку
  вопроса** (не спиннер) — рейка (узкая строка + трек), тема, 2 строки вопроса (полная+короткая),
  4 опции-скелета (квадратный badge + 1–2 строки). Блоки `.skeleton` (surface-2, `sk-pulse` 1.4s)
  декоративны `aria-hidden`; регион `.focus-main` = `role=status`+`aria-live=polite`+`aria-busy`
  с visually-hidden «Загрузка вопроса…». Пульс off под `prefers-reduced-motion`. JS не нужен.
- **QA (§9):** detector exit 0; AA не применяется (скелет — чистая декорация, WCAG 1.4.11
  исключает); блоки видимы светло-серыми на paper/surface.
- **Ledgers:** CRITIQUE C12 (закрыт loading-placeholder, остаются 3) + журнал cr.16; matrix
  †-сноска; registry notes/gaps_found. C12 остаётся IN_PROGRESS.
- **Следующий тик:** worst-first — generationUnavailable / no-js (diagram отложен §4) ЛИБО
  C13 (result) / C16 (stats), ЛИБО Фаза E (порт) по разблокировке `base.css`.

### 2026-07-12 — R0.15: пивот на C13 — confidence-виджет в `result`

- **Worst-first по ценности:** остаток C12 обесценился (generationUnavailable ≈ покрыт
  flashcard+info-alert; no-js — parity-фазный контракт-концерн; diagram — §4-бан Mermaid) →
  переключение на C13 confidence (реальный компонент, намеренно живущий в проде).
  Collision-guard: mockups+PLAN чисты, прод-WIP dirty (порт BLOCKED).
- **Добавлен confidence-виджет** после вердикта (порядок вердикт→уверенность→кнопка, memory
  `project_post_answer_js`): «Насколько был уверен в ответе?» + 3 уровня 🎲 Угадал / 🤔 Не
  уверен / 💪 Уверен. `role=radiogroup` (`aria-labelledby`), кнопки `role=radio`+`aria-checked`,
  roving tabindex + стрелки/Home/End (паттерн settings seg-control), эмодзи `aria-hidden` +
  текст-лейбл (<460px только глиф). Прод намеренно держит 🎲🤔💪 (memory `project_icon_system`)
  → при порте точка решения эмодзи-vs-монохром (CSS-коммент).
- **Слоп-ловушка (детектор):** `1.15em` глиф = 18.4px рядом с 16px → [flat-type-hierarchy];
  убран кастомный размер (эмодзи наследует fs-sm), exit 0.
- **QA (§9):** detector exit 0; AA обе темы ≥4.5 (conf-q ink2/surface 7.18/8.57; текст ink/paper
  15.55; checked signal-ink/signal-wash 6.54/6.87).
- **Ledgers:** CRITIQUE C13 IN_PROGRESS + журнал cr.17; matrix §-сноска; registry notes/gaps.
- **Следующий тик:** worst-first — C13 regenerate ЛИБО C16 (stats search), ЛИБО Фаза E (порт)
  по разблокировке `base.css`.

### 2026-07-12 — R0.16: закрыт C16 — поиск по темам в `stats`

- **Worst-first:** из двух остатков (C13 regenerate — мелкая admin-gated иконка, C16 search —
  заметная фича) взял search. Проверил прод read-only: поиск существует (`stats.html`
  search-pane, `stats.js` «/» + guard модалки). Collision-guard: mockups+PLAN чисты.
- **Добавлено в секцию «Детализация по темам»:** `role=search` форма (visually-hidden label,
  иконка-лупа, `type=search`) + kbd-подсказка «/» фокус · Esc сброс; live-фильтр строк по имени
  темы; счётчик «N из M тем» (`role=status aria-live=polite`); при нуле — скрытие таблицы +
  конструктивная empty-строка («Попробуй короче: „aop", „транз"»). «/» не срабатывает из
  текстовых полей; Esc в инпуте сбрасывает со `stopPropagation`.
- **QA (§9):** detector exit 0; AA обе темы ≥4.5 (input 15.55/15.49, placeholder/hint 5.75/7.18,
  счётчик 7.77/9.23, kbd 6.56/7.72).
- **Ledgers:** CRITIQUE C16 **DONE** + журнал cr.18; matrix ◊-сноска; registry notes/gaps.
  empty/cold-start намеренно не делаем (прод-решение, memory project_design_elevation_round1).
- **Следующий тик:** worst-first — C13-остаток (regenerate-кнопка в result) ЛИБО Фаза E (порт)
  по разблокировке `base.css`; после — независимые дименсии (zoom200/DPR1 методология).

### 2026-07-12 — R0.17: закрыт C13 — regenerate оказался мёртвым прод-UI (аудит-тик)

- **Loop → пересоздан** (cron 94ac32f3, `*/10 * * * *`; старый e2e2b139 удалён — сброс 7-дневного
  истечения по повторному /loop).
- **Read-only аудит прод:** `btn-regenerate` гейтится `th:if="${aiEnabled}"`, а `aiEnabled`
  захардкожен `false` в обоих местах модели (`MvcModelAttributeMapper.java:152`,
  `InterviewPageMvcService.java:166`) — следствие полного AI-вырезания 2026-07-07. Кнопка в
  проде НИКОГДА не рендерится → «недостающее состояние regenerate» было фантомом registry-списка;
  макет правильно его не содержит.
- **Решения:** при порте btn-regenerate/regen-badge НЕ портировать; для Фазы G зафиксирован
  dead-code кандидат (btn-regenerate + regen-badge + app.js regenerateQuestion + API.REGENERATE;
  прод сейчас BLOCKED — только заметка). Остаток no-js = живой /answer (parity-концерн) →
  **C13 DONE**. Изменений в макетах нет, QA-прогоны не требуются.
- **Итог бэклога:** C1–C17 ВСЕ закрыты (DONE/РЕШЕНО). Mockup-фаза исчерпана по бэклогу.
- **Следующий тик:** Фаза E (порт error/session-summary) по разблокировке `base.css` ЛИБО
  независимые дименсии-методологии (zoom200/DPR1/print на макетах).

### 2026-07-12 — R0.18: живой reflow-аудит (zoom200/320px) всех 7 макетов — 2 дефекта пофикшены

- **Worst-first:** бэклог C1–C17 закрыт, порт BLOCKED (base.css dirty) → независимая дименсия
  «zoom 200% / DPR1» (TODO, «не покрыто ни разу»). Впервые ЖИВОЙ браузер-аудит через
  chrome-devtools MCP: окно Chrome не жмётся <485px → resize_page недостаточен, использована
  эмуляция вьюпорта (320x900x2 и 640x900x2).
- **Методика:** `scrollWidth > clientWidth` по каждому mock-состоянию: focus 7 состояний +
  flashcard-revealed; result 2 + details-open; settings 3 вкладки + reset-confirm; shell +
  mobile-menu + kbd-help; stats + search-empty; summary 2; error. Скролл-контейнеры
  (pre.code-body, tablist overflow-x:auto) — легитимное исключение 1.4.10.
- **Найдено и пофикшено (C18, P1):** (1) `.explain code` — токен `@Transactional(REQUIRES_NEW)`
  286px распирал документ на 1px (focus «Разбор», result оба варианта) → `overflow-wrap:
  break-word` (2 файла); (2) settings «Оформление» — `.set-grid minmax(20rem,1fr)` форсит 320px
  колонку → 35px overflow всех 7 карт → `minmax(min(20rem,100%),1fr)`.
- **QA (§9):** ре-верификация 320 чисто по всем 7 × состояния; 640 (zoom200@1280) чисто;
  detector exit 0 ×3 правленных файла.
- **Ledgers:** CRITIQUE +C18 DONE + журнал cr.20; matrix дименсия zoom200 TODO→PASS(макеты);
  registry gaps_found. Остаток дименсий: print (TODO), no-js (parity).
- **Следующий тик:** print-дименсия на макетах (emulate print) ЛИБО Фаза E по разблокировке.

### 2026-07-12 — R0.19: print-дименсия — print-CSS для session-summary

- **Worst-first:** порт BLOCKED (base.css dirty); из оставшихся дименсий print был полностью
  непокрыт. Печатный сценарий реален только у session-summary (кнопка «Печать» в проде).
- **Добавлен @media print:** бумага получает документ, не приложение — скрыты
  mock-bar/шапка/skip-link/кнопки/стрелки/интерактивная подсказка; grid→block (1 колонка),
  sticky→static; чёрным по белому 11pt; таблица с рамками 1px; break-inside: avoid; анимации off.
- **QA (§9):** detector exit 0; CSSMediaRule распарсен (16 правил); автопроверка селекторов
  print-блока против DOM — 0 мёртвых (нет опечаток); экранный рендер не тронут (overflow 0).
  Живой print-preview — при parity (MCP не эмулирует media:print).
- **Ledgers:** matrix print TODO→PASS(summary)/TODO(остальные — намеренно, нет сценариев);
  CRITIQUE cr.21; registry gaps_found.
- **Итог дименсий макетов:** detector/AA/states/a11y/reflow320+640/print(там, где сценарий) —
  ЗАКРЫТЫ; no-js и parity — фазные (нужен прод). Mockup-фаза ПОЛНОСТЬЮ исчерпана: дальше
  только Фаза E по разблокировке base.css.
- **Следующий тик:** проверить разблокировку base.css → Фаза E (порт error, самый простой,
  маппинг готов R0.5); если dirty — read-only подготовка порта (черновик CSS-блока error
  в отдельном файле design/mockups/port-drafts/).

### 2026-07-12 — R0.20: Фаза E (read-only prep) — черновик порта error в port-drafts/

- **Collision guard:** base.css по-прежнему dirty (чужой WIP, 15 файлов) → сам порт BLOCKED;
  по плану R0.19 выполнена read-only подготовка.
- **Создано:** `design/mockups/port-drafts/README.md` (процедура применения, токен-маппинг
  словарей макет→прод, правило специфичности: `="instrument"` равен по весу общему
  `html[data-design]` → блок ставить ПОСЛЕ §C7) и `port-drafts/error-instrument-base.css`
  (блок C7-i: левосторонняя instrument-композиция — lowercase mono-eyebrow, призрачный
  clamp-номер 4rem→8.5rem (aria-hidden декор), title 3xl/balance/20ch, details на
  bg-tertiary/line, actions flex-start). Файлы никем не загружаются.
- **Ключевое решение (риск №2 PORT_MAPPING_error.md):** `.error-rail` НЕ портируется —
  дублирует `.error-actions` дословно; `rail-hint` покрыт серверным 403-body →
  порт error = чистый append в base.css, **ноль правок Thymeleaf**.
- **Ledgers:** CRITIQUE cr.22; registry error → port_status DRAFT_READY.
- **Следующий тик:** проверить разблокировку base.css → применить черновик (Фаза E, порт
  error) + live parity QA; если dirty — аналогичный черновик для session-summary
  (маппинг R0.4, второй независимый; сложнее: PE copy/print + ARIA-таблица).

### 2026-07-12 — R0.21: Фаза E prep №2 — черновик порта session-summary (шаг 1, CSS-only)

- **Collision guard:** base.css dirty → порт BLOCKED; подготовлен второй черновик.
- **Создано:** `port-drafts/session-summary-instrument-base.css` (блок C6-i, ПОСЛЕ общего
  §C6): CSS-only рестайл существующих прод-хуков — карты bg-tertiary/line, mono-цифры
  плиток (tabular-nums), тихие body-заголовки секций, lowercase mode-line, hover ошибок
  paper+accent. DOM session-summary.html не меняется. README дополнен: таблица черновиков,
  скоуп шага 2 (headline+грид main+aside — шаблонные правки, риски №1/№2/№3 маппинга).
- **Находка:** print НЕ портируется — прод base.css уже несёт общий @media print (~3208)
  с полным summary-покрытием; print-блок макета был mockup-QA (R0.19).
- **Ledgers:** CRITIQUE cr.23; registry session-summary → DRAFT_READY.
- **Итог prep:** ОБА независимых черновика готовы (error C7-i + summary C6-i) — Фаза E
  стартует мгновенно по разблокировке base.css.
- **Следующий тик:** проверка разблокировки → применение черновиков (порт + parity QA);
  если dirty — черновиков для независимых поверхностей больше нет (остальные 5 заблокированы
  и шаблонами) → read-only аудит следующей очереди (D-маппинг одной из 5 блокированных
  поверхностей: стартовать с focus-training как ядровой).

### 2026-07-12 — R0.22: D-маппинг focus-training (ядровая поверхность)

- **Collision guard:** base.css + focus-training.html + app.js dirty → порт BLOCKED;
  независимые черновики исчерпаны (error, summary) → снят маппинг сложнейшей поверхности.
- **Создано:** `PORT_MAPPING_focus-training.md` — макет focus-question (7 состояний) ↔
  прод (серверные th:if-ветки + AJAX app.js v56 + 5 фрагментов + TemplateFragmentContractTest).
- **Крит-решения:** рельс НЕ портировать (topbar = решение пользователя round-01); буква
  варианта = CSS-счётчик (DOM-бейджи макета не переносить); result-состояние стилизовать
  по фактическим app.js-классам; SM-2 reveal/grade серверные POST; inline-alert/skeleton
  без продьюсера не вводить (кандидаты Фазы G); done = finished-ветка empty.
- **Каверза:** маппинг снят с working-tree (шаблон в чужом WIP) — пересверка перед портом.
- **Ledgers:** CRITIQUE cr.24; registry focus-training → MAPPED.
- **Следующий тик:** проверка разблокировки → Фаза E (черновики error/summary);
  если dirty — D-маппинг следующей блокированной поверхности (result: AJAX+no-JS
  двойной рендер, самый тонкий контракт).

### 2026-07-12 — R0.23: D-маппинг result (живой no-JS фоллбэк)

- **Collision guard:** base.css/result.html/app.js dirty → порт BLOCKED; продолжаю
  D-маппинги блокированных поверхностей.
- **Создано:** `PORT_MAPPING_result.md`. Крит: no-JS цел (confidence/v-sub НЕ портировать);
  прод-опции богаче макета (порт ОТ прода); related-questions прямой потомок .ed-page;
  takeaway/trace AI-вырезаны.
- **Находки:** (а) прод-фрагмент inline-alert появился (#interview-alert, app.js-продьюсер) —
  R0.22-заметка частично устарела; (б) НОВЫЙ дефект-кандидат: .question-side (код вопроса)
  за aiEnabled=false → на no-JS result код никогда не рендерится; Фаза G/решение
  пользователя (рядом с btn-regenerate R0.17).
- **Ledgers:** CRITIQUE cr.25; registry result → MAPPED (4/7 замаплено).
- **Следующий тик:** разблокировка → Фаза E; иначе D-маппинг stats (Chart.js-зоны,
  сортируемая таблица, forecast) или settings (7 осей, ARIA-вкладки).

### 2026-07-12 — R0.24: D-маппинг stats (Chart.js + PE-скрипты)

- **Collision guard:** stats.html/stats.js/base.css dirty → порт BLOCKED.
- **Создано:** `PORT_MAPPING_stats.md`. Крит: SVG-графики макета = заглушки (прод =
  Chart.js из topicStatsJson [(...)] unescaped); цвета графиков в stats.js → шаг 1 не
  красит; сорт-стрелка CSS [aria-sort]::after; сохранить collapse/Intl-даты/stacked-card/
  формы фильтров; live-фильтр тем = Фаза G кандидат (≠ серверный поиск вопросов);
  одноколонка в шаге 1, рейка = шаг 2; stats-grid фрагмент общий с result.
- **Ledgers:** CRITIQUE cr.26; registry stats → MAPPED (5/7).
- **Следующий тик:** разблокировка → Фаза E; иначе D-маппинг settings (последняя
  страничная; ARIA-вкладки, 7 осей персонализации, формы контракта §3) — после неё
  останется только shell/head (хром-фрагменты).

### 2026-07-12 — R0.25: D-маппинг settings (последняя страничная)

- **Collision guard:** settings.html/base.css/app.js dirty → порт BLOCKED.
- **Создано:** `PORT_MAPPING_settings.md`. Находка: прод уже прошёл вкладочный редизайн
  (0eb762a5) → структуры изоморфны макету; шаг 1 = чистый CSS-рестайл (set-card сетка
  осей, switch-тогглы, вкладки); шаг 2 = вертикальный tablist-рельс (+app.js ↑/↓);
  alertdialog сброса = Фаза G low-prio (нативный confirm остаётся). Не задеть:
  контракт-тест, data-default-count SET-15, PE-паттерны.
- **Ledgers:** CRITIQUE cr.27; registry settings → MAPPED (6/7).
- **Следующий тик:** разблокировка → Фаза E; иначе финальный D-маппинг shell/head
  (хром: header.html/head.html/icons.html + today-widget) — после него фаза D
  исчерпана полностью, останется только ждать разблокировку.

### 2026-07-12 — R0.26: D-маппинг shell/хром — ФАЗА D ИСЧЕРПАНА (7/7)

- **Collision guard:** base.css/app.js/head.html dirty; header.html/icons/today-widget ЧИСТЫ.
- **Создано:** `PORT_MAPPING_shell.md` (финал). Крит: drawer НЕ портировать (прод-нав
  проще, без JS); футер — в проде нет вообще, вопрос юзеру (Фаза G); ?-кнопка — шаг 2
  (kbd-help уже в app.js); порт shell ≈ чистый base.css-рестайл; 4 inline-скрипта
  header = контракт.
- **Находки:** (а) шрифты instrument уже в head.html (Newsreader/Space Grotesk) — риск
  снят; (б) NAMES дизайн-тоггла в header.html без instrument (aria/title кажут сырой id) —
  2-строчный микро-фикс НЕ блокирован (header.html чист) = единственная доступная
  прод-правка.
- **Ledgers:** CRITIQUE cr.28; registry shell → MAPPED.
- **Итог фазы D:** 7/7 поверхностей замаплено (error/summary/focus/result/stats/settings/
  shell), 2 черновика ready-to-paste (C7-i, C6-i).
- **Следующий тик:** разблокировка → Фаза E (черновики); если dirty — микро-фикс NAMES
  instrument в header.html (Фаза E, независимая правка чистого файла, explicit pathspec
  + прогон контракт-теста).

### 2026-07-12 — R0.27: Фаза E — первая прод-правка: NAMES instrument (header.html)

- **Collision guard:** base.css dirty (CSS-порт блокирован); header.html ЧИСТ → применён
  микро-фикс из находки R0.26.
- **Правка:** header.html — instrument добавлен в NAMES дизайн-тоггла + фолбэк order();
  aria/title теперь «Instrument», не сырой id. Комментарий 10→11 дизайнов.
- **QA:** живого bootRun нет → TemplateFragmentContractTest прогнан, зелёный.
- **Ledgers:** CRITIQUE cr.29; registry shell (находка → применено).
- **Следующий тик:** проверка разблокировки base.css → применение черновиков C7-i/C6-i
  (порт error + session-summary, бамп v=, live parity QA). Если dirty — независимых
  прод-правок больше нет; read-only опции: пересверка черновиков на свежесть ИЛИ
  подготовка parity-QA чеклиста (страницы×вьюпорты×темы) для быстрого прохода Фазы F.

### 2026-07-12 — R0.28: подготовка Фазы F — PARITY_QA_CHECKLIST.md

- **Collision guard:** base.css dirty; независимые прод-правки исчерпаны (R0.27).
- **Создано:** `design/mockups/PARITY_QA_CHECKLIST.md` — исполняемый прогон Фазы F:
  предусловия запуска (все грабли: postgres-порт, wedge, cp-синк, бамп v=, emulate 320,
  instrument клиентски), матрица instrument+editorial-контроль × темы × вьюпорты,
  пер-поверхностные сценарии доезда (в т.ч. _csrf /finish, no-JS result,
  FLASHCARD-grades) + известные не-дефекты (aiEnabled-гейты, Chart.js цвета шага 1),
  порядок применения черновиков, выходные артефакты.
- **Ledgers:** CRITIQUE cr.30.
- **Состояние конвейера:** ВСЁ подготовлено — маппинги 7/7, черновики C7-i/C6-i,
  NAMES-фикс применён, чеклист F готов. Единственный блокер — base.css (чужой WIP).
- **Следующий тик:** проверка разблокировки → Фаза E п.2 чеклиста (порт error).
  Если dirty — read-only пересверка черновиков против свежего working-tree
  (диффа base.css за неделю) на предмет дрейфа якорей §C6/§C7.

### 2026-07-12 — R0.29: пересверка черновиков против дрейфа WIP — валидны

- **Collision guard:** base.css dirty (9-й тик подряд).
- **Сверка:** дифф чужого WIP не трогает §C6/§C7; все якоря C7-i (13) и C6-i (8/8)
  на месте; точки вставки уточнены (~1972/~2070 WT) → port-drafts/README.
- **Находка:** WIP добавляет правила #interview-options label.option-wrong:has(...) —
  параллельная сессия в зоне focus-порта; пересверка app.js-классов при порте focus
  обязательна (каверза R0.22 подтверждена).
- **Ledgers:** CRITIQUE cr.31.
- **Следующий тик:** проверка разблокировки → порт error (C7-i). Если dirty —
  вариантов подготовки не осталось совсем: короткий тик-проверка + отчёт (или
  углублённый аудит editorial-контроля по чеклисту §1 на живом bootRun без правок,
  если решусь поднимать приложение в фоне).

### 2026-07-12 — R0.30: живой baseline instrument (token-only) — зелёный

- **Collision guard:** base.css dirty (10-й тик). Вместо простоя — живой baseline Фазы F.
- **Находка процесса:** :8080 уже слушал чужой java-инстанс (парал. сессия);
  урок — проверять lsof, не ps. Никаких gradle в этом тике.
- **Аудит (read-only, chrome MCP):** instrument токены применяются; шрифты
  Newsreader/Space Grotesk/JB Mono загружены (fonts.check=true); reflow чист
  1280/375/320; обе темы цельные (скрины focus); /settings вкладки+seg-control ок;
  NAMES-фикс R0.27 подтверждён живьём; error 404 пристойна и до порта.
- **Вывод:** instrument как token-only УЖЕ работоспособен — C6-i/C7-i = улучшение,
  не починка; parity-риск портов низкий. Метрики «снято с WIP-инстанса».
- **Ledgers:** CRITIQUE cr.32.
- **Следующий тик:** проверка разблокировки → порт error (C7-i). Baseline готов —
  сравнение до/после будет предметным.

### 2026-07-12 — R0.31: живой baseline stats — зелёный; каверза cold-start графиков

- **Collision guard:** base.css dirty (11-й тик); baseline Фазы F продолжен (read-only).
- **Разобрана ложная тревога:** «мёртвые графики» = легитимный cold-start фолбэк
  stats.js после wipe базы (Chart.js 4.5.0 жив, JSON 319 тем парсится, canvas скрыт
  осознанно). В PARITY_QA_CHECKLIST внесено различение двух фолбэков + требование
  3–5 ответов перед замером цветов графиков.
- **Baseline stats:** reflow чист 1280/375; stacked-card на 375 живой (data-label);
  collapse 319→12 работает; next-actions/фильтры/cold-copy ок. Скрин 1280 light.
- **Ledgers:** CRITIQUE cr.33; чеклист дополнен.
- **Следующий тик:** проверка разблокировки → порт error (C7-i). Baseline покрыл
  focus/settings/error/stats; остаток (summary/result no-JS) требует сессии/ответов —
  снимется в parity-фазе по чеклисту.

### R0.32 (2026-07-12) — Фаза F prep: no-JS контракт-аудит серверного HTML (все PE-инварианты зелёные)

- **Сделано:** curl GET-смоук 4 страниц живого инстанса (focus/settings/stats/error → scratchpad). Проверены PE-инварианты no-JS: (1) все прогрессив-контролы уходят с сервера СКРЫТЫМИ — 3 тоггла шапки, settings-tablist, personalization-card, data-export-block, streak-bar (`hidden` в served HTML) → без JS мёртвых контролов нет; (2) живое без JS на месте — focus #interview-form + noscript, settings filters/session/danger-формы, stats-таблица развёрнута (0 tr[hidden], collapse чисто клиентский) + chart-fallback, error статичен. PIXEL_QA_MATRIX: no-js TODO→PASS(static). CRITIQUE cr.34.
- **Блокер без изменений (12-й тик):** base.css dirty (чужой WIP). Ответы на вопросы живого квиза запрещены (§4: мутация вымытой базы) → интерактивный no-JS смоук и summary/result-baseline остаются на parity-фазу.
- **Следующий тик:** проверка разблокировки base.css → Фаза E порт error (C7-i по PARITY_QA_CHECKLIST §3). Предподготовка исчерпана ПОЛНОСТЬЮ: 7/7 маппингов, 2 черновика (пересверены R0.29), runbook Фазы F, live-baseline (R0.30–31), no-js смоук (R0.32). Если base.css всё ещё dirty — read-only аудит следующей глубины (напр. computed-парность editorial-контроля или ревизия app.js хуков focus-порта против WIP-диффа).

### R0.33 (2026-07-12) — Дифф-ревизия маппингов Фазы D против разросшегося WIP (read-only)

- **Сделано:** чужой WIP теперь накрывает ВСЮ зону порта (app.js +43, stats.js +21, focus/result/settings/stats.html, head.html, TemplateFragmentContractTest +65). Проведена дифф-ревизия всех артефактов Фазы D: черновики C6-i/C7-i валидны (якоря §C6/§C7 целы), маппинги focus/result валидны бит-в-бит; дрейф зафиксирован в port-drafts/README.md §R0.33: stats-collapse теперь tr[hidden]+MutationObserver (не max-height+fade), settings +#personalization-status live-region + wireSegControl(+label), stats.js v13 рестайл графиков, head.html v62/v72.
- **Критичная находка:** новый контракт-тест ЗАПРЕЩАЕТ sticky-шапку (doesNotContain «.ed-masthead { position: sticky», FNO-риск) и пиннит точные строки base.css → (а) макетное решение C3 (sticky+solid шапка) в прод НЕ портируется; (б) после каждой вставки Фазы E — обязательный прогон TemplateFragmentContractTest (но НЕ при живом bootRun — wedge).
- **Блокер:** base.css dirty 13-й тик. **Следующий тик:** проверка разблокировки → Фаза E error C7-i; если dirty — read-only аудит: computed-парность editorial-контроля ИЛИ ревизия PORT_MAPPING_shell против запрета sticky (перенос решения в маппинг).

### R0.34 (2026-07-12) — Sticky-решение в канон shell-маппинга + численный baseline error (read-only к проду)

- **Сделано:** (1) запрет sticky-шапки (контракт-тест WIP, FNO) перенесён в PORT_MAPPING_shell.md — строка .site-head и риск №6 переведены в РЕШЕНО, добавлен риск №7 (не перебивать :has(progress)-уплотнение и compact-clamp HDR-1). (2) Снят живой ЧИСЛЕННЫЙ baseline error-страницы @1280: editorial light/dark (контроль — бит-в-бит после порта) + instrument token-only light (ДО порта), вписан в PARITY_QA_CHECKLIST §2/error вместе с ожидаемой дельтой C7-i (card flex-start/left, eyebrow lowercase/tertiary, code 500/clamp=136px@1280/lh0.95/border-color, title 500/20ch, actions flex-start). Паритет первого порта теперь доказывается диффом чисел.
- **Блокер:** base.css dirty 14-й тик (WIP-инстанс жив на :8080). **Следующий тик:** проверка разблокировки → Фаза E error C7-i (baseline готов). Если dirty — аналогичный численный baseline session-summary НЕВОЗМОЖЕН без сессии (§4) → альтернатива: численный baseline shell-хрома (masthead/nav/тогглы) на / в обеих темах × 2 дизайна.

### R0.35 (2026-07-12) — Численный baseline shell-хрома (4 состояния) + каверза transition-lag

- **Сделано:** снят численный baseline шапки (GET / @1280, живой инстанс): editorial/instrument × light/dark — masthead/kicker/title/nav/active-подчёрк/тогглы/progress, вписан в PARITY_QA_CHECKLIST §2/shell. Найдена и обезврежена методологическая каверза: `transition: color .15s` на nav-link/тогглах замораживает computed-цвета при мгновенном чтении после флипа data-theme → ложные диффы; правило «settle ≥400ms» вписано в чеклист. Прод чист: dark-цвета корректны, sticky отсутствует во всех состояниях (согласуется с контракт-запретом). Кандидат дельты порта shell: instrument masthead-title weight 700→500 (сверить с макетом).
- **Блокер:** base.css dirty 15-й тик. **Следующий тик:** проверка разблокировки → Фаза E error C7-i. Если dirty — остались read-only цели: численный baseline settings-осей (seg-controls) или stats-хуков (@1280, оба дизайна); интерактив/сессии по-прежнему под запретом §4.

### R0.36 (2026-07-12) — Численный baseline settings (4 состояния)

- **Сделано:** снят computed-baseline /settings @1280 (вкладка «Оформление», settle 400ms, 4 состояния): tablist/вкладки/card/seg-controls/label/hint/danger/secondary → PARITY_QA_CHECKLIST §2/settings. Выводы: шрифтовой слой instrument уже работает token-only (Space Grotesk на UI, Newsreader на display), радиусы дифференцированы токенами (r0 editorial vs r6/r10 instrument) — порт settings шаг 1 будет тихим рестайлом. Уточнение на parity: фактическая разметка switch-тогглов (`.switch input`/`.set-card` не сматчились). Reflow чист.
- **Блокер:** base.css dirty 16-й тик. **Следующий тик:** проверка разблокировки → Фаза E error C7-i. Если dirty — последняя крупная read-only цель: baseline stats-хуков (карты обзора/таблица/фильтры, БЕЗ цветов графиков — они требуют данных).

### R0.37 (2026-07-12) — Численный baseline stats (4 состояния) — baseline-серия замкнута

- **Сделано:** computed-baseline /stats @1280 (4 состояния): stat-карты/таблица/фильтры/fallback/expander → PARITY_QA_CHECKLIST §2/stats. Живьём подтверждён новый tr[hidden]-collapse (319/307). Instrument token-only несёт язык и здесь (r10-карты, r6-инпуты, Newsreader, ls 0.24). Каверза: expander r0 в обоих дизайнах — осознанный B5, не «чинить». Серия read-only baseline-ов ЗАМКНУТА: error/shell/settings/stats; summary/result требуют сессии (§4) — снимутся первыми в parity-фазе.
- **Блокер:** base.css dirty 17-й тик. **Следующий тик:** проверка разблокировки → Фаза E error C7-i. Если dirty — baseline focus-страницы (радиогруппа опций/бейджи/topbar, GET / без ответов — read-only): последняя не снятая поверхность, доступная без сессии.

### R0.38 (2026-07-12) — Численный baseline focus (active-MCQ) — все read-only поверхности сняты

- **Сделано:** computed-baseline GET / @1280 (active-MCQ, 4 опции, 4 состояния): вопрос 48px serif, опция-карты (r8 editorial / r10 instrument), submit, progress → PARITY_QA_CHECKLIST §2/focus. Находка: editorial submit «приклеен» к карте (radius 0 0 8 8), instrument token-only скругляет полностью → композиционный гибрид, при порте focus решить по макету (submit отделён). Не сматчились: topic-badge/session-counter/details (не в этом состоянии). Reflow чист. Снятые baseline: error/shell/settings/stats/focus — ВСЁ доступное без сессии.
- **Блокер:** base.css dirty 18-й тик. **Следующий тик:** проверка разблокировки → Фаза E error C7-i. Если dirty — mobile-380 baseline (focus+shell @375, где живут compact-clamp HDR-1 и переносы нав) ЛИБО чистка PIXEL_QA_MATRIX (сведение baseline-статусов в матрицу).

### R0.39 (2026-07-12) — Mobile-375 baseline: HDR-1 и :has(progress)-уплотнение подтверждены живьём

- **Сделано:** GET / @375 (active-MCQ, оба дизайна light): masthead-title 18.75px vs вопрос 27px (ratio 1.44 — HDR-1 работает), уплотнение шапки при активной сессии (p8×16/row-gap 4), нав одной строкой, submit 327×48, reflow чист. Layout бит-идентичен между дизайнами → мобильная вёрстка при порте не трогается; числа вписаны в PARITY_QA_CHECKLIST §2/shell как обязательные к сохранению (контракт-тест пиннит строки).
- **Блокер:** base.css dirty 19-й тик. **Следующий тик:** проверка разблокировки → Фаза E error C7-i. Если dirty — сведение baseline-статусов в PIXEL_QA_MATRIX (кол-во снятых поверхностей/состояний, что осталось на parity) — завершающая бухгалтерия предподготовки.

### R0.40 (2026-07-12) — PIXEL_QA_MATRIX сведена: предподготовка формально закрыта

- **Сделано:** матрица обновлена — parity-ячейки аннотированы ▲ (baseline снят: error/shell/settings/stats/focus, @1280 + 375) и ▽ (result/summary — нужна сессия, первый шаг parity-фазы); no-js статик → PASS(R0.32); колонка фаз переведена в «D done; E BLOCKED/READY»; в блокер-секции — итог 13 тиков предподготовки. Read-only повестка ИСЧЕРПАНА.
- **Блокер:** base.css dirty 20-й тик. **Следующие тики (maintenance-режим до разблокировки):** (1) сторожевая проверка: git diff чужого WIP изменился? → быстрая пересверка якорей §C6/§C7 и хук-селекторов (grep, 1 мин); (2) при разблокировке — немедленно Фаза E error C7-i по PARITY_QA_CHECKLIST §3. Новых артефактов не плодить — только сторожить валидность готовых.

### R0.41 (2026-07-12) — Maintenance-тик: блокер жив (21), якоря целы

- Сторожевая проверка: base.css dirty (21-й тик), WIP-дифф не изменился (6 ханков, fingerprint 5741ff12); якоря §C6=1857/§C7=2070 и все хук-селекторы черновиков на месте. Действий не требуется. Следующий тик: то же (разблокировка → error C7-i).

### R0.42 (2026-07-12) — Maintenance-тик: блокер жив (22), WIP без изменений

- Fingerprint диффа base.css идентичен R0.41 (5741ff12) → якоря целы без пересверки. Действий нет. Ожидание разблокировки → error C7-i.

### R0.43 (2026-07-12) — Maintenance-тик: блокер жив (23), WIP без изменений

- Fingerprint диффа base.css идентичен (5741ff12). Действий нет. Ожидание разблокировки → error C7-i.

### R0.44 (2026-07-12) — Maintenance-тик: блокер жив (24), WIP без изменений

- Fingerprint диффа base.css идентичен (5741ff12). Действий нет. Ожидание разблокировки → error C7-i.

### R0.45 (2026-07-12) — Maintenance-тик: блокер жив (25), WIP без изменений

- Fingerprint диффа base.css идентичен (5741ff12). Действий нет. Ожидание разблокировки → error C7-i.

### R0.46 (2026-07-12) — ФАЗА E ОТКРЫТА: порт error C7-i ПРИМЕНЁН (v=73)

- **Разблокировка:** параллельная сессия закоммитила WIP (68444a1b+0f66cadf) — дерево чисто после 25 тиков блокера.
- **Сделано (по PARITY_QA_CHECKLIST §3):** C7-i вставлен в base.css после §C7; head.html base v72→73; live-синк в build/resources; QA на живом инстансе: дельта = прогноз R0.34 бит-в-бит (ghost 136px/500/border-primary @1280, card flex-start/left, eyebrow lowercase/tertiary, title 500/20ch, actions flex-start; dark корректен), editorial-контроль = baseline бит-в-бит, 320-reflow чист (clamp 70.4px). Черновик удалён, матрица: error parity PASS, contract PENDING-TEST (живой bootRun — gradle нельзя, §4).
- **Следующий тик:** порт session-summary C6-i (после §C6 ~1972, bump v=74) + тем же тиком прогон TemplateFragmentContractTest, если bootRun остановится.

### R0.47 (2026-07-12) — Порт session-summary C6-i применён (v=74): оба черновика в проде

- **Сделано:** C6-i вставлен после §C6 (перед §C7), head v73→74, live-синк. QA в пределах §4: served v=74; 9 правил C6-i распарсены; утечки нет — /stats с его .stat-item отдаёт baseline R0.37 бит-в-бит (скоуп .summary-page работает). Черновик удалён; port-drafts теперь журнал решений. Матрица: summary parity=APPLIED▽ (визуальный паритет — parity-фаза с EXAM-сессией), contract PENDING-TEST.
- **Следующий тик:** bootRun остановлен? → прогон TemplateFragmentContractTest (закрыть оба PENDING-TEST). Дальше по очереди маппингов: порт focus-training (крупнейший; перед ним re-verify app.js классов против свежего дерева — WIP закоммичен, дифф-ревизия R0.33 могла устареть).

### R0.48 (2026-07-12) — Подготовка порта focus: re-verify + черновик C1-i

- **Сделано:** пересверка хуков focus по свежему дереву (WIP закоммичен): app.js option-классы и grade-акценты уже покрыты design-agnostic кодом; submit-«гибрид» cr.40 снят (editorial-решение, instrument-токены дают отдельный чип). Черновик C1-i (port-drafts/focus-instrument-base.css, 7 правил поверх существующих хуков) + решения в README §R0.48. DOM/app.js не трогаются.
- **Следующий тик:** применить C1-i (после блока опций §C1, bump v=75) + QA live по baseline R0.38 (дельта: topic lowercase/tertiary, вопрос 500, опции paper+ring-hover) + editorial-контроль + 320; контракт-тест при остановленном bootRun.

### R0.49 (2026-07-12) — Порт focus C1-i применён (v=75): третий порт, шаг 1

- **Сделано:** C1-i вставлен после stripe-блока опций (выигрыш по порядку), head v74→75, live-синк. QA: дельта = прогноз (topic lowercase/tertiary, вопрос 500, опции paper, буква tertiary); ring-hover подтверждён реальным hover (teal accent-strong, без заливки); editorial = baseline R0.38 бит-в-бит; геометрия не менялась (320 валиден по R0.39). Черновик удалён. Матрица: focus parity PASS(active). Отложено на parity: checked-состояние, flashcard/empty ветки, result-инъекции app.js, .session-progress.
- **Следующий тик:** порт result (маппинг R0.23) — пересверка хуков result.html/app.js-инъекций по свежему дереву → черновик или прямое применение (v=76); контракт-тест при остановленном bootRun.

### R0.50 (2026-07-12) — Порт result C3-i применён (v=76): четвёртый порт

- **Сделано:** C3-i (5 правил: topic-badge lowercase/tertiary, вердикт medium, stat-value mono/tabular, sm2-details tertiary+line) вставлен перед «Утилитами», head v75→76, live-синк. QA §4: v=76 served, 5 правил распарсены, утечки на /stats нет (stat-value = baseline R0.37). Визуальный паритет — parity-фаза (нужен POST /answer). Не портированы: confidence (прод-решение юзера с эмодзи), мёртвые regenerate/question-side (Фаза G).
- **Следующий тик:** порт stats (маппинг R0.24) — instrument-голос на stat-карты/таблицу/фильтры (v=77); контракт-тест при остановленном bootRun. Затем settings → shell.

### R0.51 (2026-07-12) — Порт stats C5-i применён (v=77): пятый порт, live-паритет

- **Сделано:** C5-i (2 правила: stat-value mono/tabular, thead th tertiary) перед §C6, head v76→77, live-синк. Полный live-QA (stats достижима read-only): дельта применилась, sticky/bg шапки целы, editorial = baseline R0.37 бит-в-бит, reflow чист. Token-only нёс 90% языка stats — блок минимален. Цвета графиков (stats.js) — Фаза G.
- **Следующий тик:** порт settings (маппинг R0.25, шаг 1 pure CSS — по baseline R0.36 token-only тоже почти всё несёт; проверить нужны ли правки вообще) → shell (v=78 при правках); контракт-тест при остановленном bootRun.

### R0.52 (2026-07-12) — Фаза E: порт settings C4-i ПРИМЕНЁН (v=78), шестой порт
- Collision guard: чисто (только .cursor hook-state); bootRun жив (PID 29539) → контракт-тест снова PENDING.
- Пересверка разметки по свежему дереву: панели settings ПЛОСКИЕ (карточная поверхность только у .today-hero, bg-secondary); `.settings-page .card` в base.css нет (комментарий у today-hero это фиксирует). Тихий заголовок колонок = .launcher-subtitle (mono/xs/uppercase/secondary).
- C4-i = 4 правила (вставка перед секцией ПЕРСОНАЛИЗАЦИЯ, после всех перебиваемых правил 1311/1327/2959/2993 — победа по порядку): hero → bg-tertiary+border-primary; .today-hero-due → mono/semibold (цвет teal несут токены); .launcher-subtitle → lowercase+tertiary; .settings-panel-title → medium (no-JS ветка).
- head.html base v77→78 (tokens v62 не тронут); live-синк cp обоих файлов в build/resources/main.
- QA живьём @1280, 4 состояния, settle 400ms: дельты = прогноз бит-в-бит; editorial-контроль = baseline R0.36 (Lora 700 clay / uppercase secondary / hero 240,238,230); instrument dark 0.262/0.36/0.76 корректен. Геометрия не менялась → 320 не перемерялся.
- Отложено ОСОЗНАННО (не instrument-scoped): карточная сетка осей + switch-тогглы = структурный вид всех дизайнов (шаг 2/Фаза G, отдельное решение); вертикальный tablist = шаг 2 (шаблон+app.js).
- Леджеры: PIXEL_QA_MATRIX settings → E DONE шаг 1 / parity PASS(R0.52) / contract PENDING-TEST; CRITIQUE cr.49.
- **Дальше:** ПОСЛЕДНИЙ порт Фазы E — shell (кандидат дельты: masthead-title 700→500, сверить с макетом shell.html; sticky ЗАПРЕЩЁН контракт-тестом; :has(progress)-уплотнение и HDR-1 clamp НЕ перебивать). После shell — прогон TemplateFragmentContractTest при первом окне без bootRun.

### R0.53 (2026-07-12) — Фаза E: порт shell C2-i ПРИМЕНЁН (v=79) — ФАЗА E (шаг 1 CSS) ЗАКРЫТА 7/7
- Collision guard чист; bootRun жив → контракт-тест PENDING (как у всех портов серии).
- C2-i = 3 правила (вставка после shell-секции, перед FOCUS ~610): .ed-masthead-title medium (дельта 700→500 — instrument до порта брал `html[data-design] h1 {bold}`; editorial уже 500 через editorial-SIGNATURE h1..h6 medium — контроль НЕ тронут); .ed-nav-link lowercase (шёпот; mono-глифы равной ширины → геометрия nav неизменна, числа R0.39 валидны); .ed-theme-toggle:hover accent-strong border + transparent bg (ring «щелчок прибора», кроет все 3 тоггла).
- НЕ перебиты (проверено): sticky-бан контракта (position relative), compact-clamp focus-page (0,3,1 задаёт только font-size), :has(progress)-уплотнение, HDR-1 mobile-clamp.
- head.html base v78→79; live-синк; QA @1280 4 состояния settle 400ms + реальный hover тоггла (0.435 teal / bg transparent) + a11y-снапшот (nav lowercase живьём).
- Урок инструментария: CSSStyleRule.cssRules существует в новом Chrome (nesting) → сканер стилей обязан проверять selectorText ПЕРЕД спуском в cssRules (иначе пустой результат).
- Отложено по решениям R0.26: drawer, футер (вопрос юзеру, Фаза G), ?-кнопка в шапке, sticky (закрыт навсегда).
- **Дальше:** (1) NAMES-микрофикс instrument в header.html (п.36 маппинга R0.26 — label дизайн-тоггла показывает сырой id «instrument»; header.html чист, 2 строки); (2) TemplateFragmentContractTest при первом окне без bootRun (закроет все 7 PENDING-TEST¤); (3) Фаза F parity: EXAM-сессия малым N → summary/result визуальный паритет.

### R0.54 (2026-07-12) — NAMES закрыт без кода + checked-fix C1-i (v=80)
- Collision guard чист; bootRun жив → контракт-тест PENDING.
- (1) П.36 маппинга shell (NAMES без instrument) устарел: WIP уже добавил запись и в NAMES, и в фолбэк order(). Живьём: aria «Сменить дизайн (сейчас: Instrument)». Закрыт правкой маппинга, кода не потребовалось.
- (2) Отложенный live-QA checked-состояния (R0.49 → parity) вскрыл дефект: «UX REVIEW PASS 2026-07-01» (низ base.css) переопределяет checked для всех дизайнов равной специфичностью ПОЗЖЕ C1-i → teal-checked был мёртв. Фикс: instrument-checked перенесён за generic (паттерн swiss) — raised bg-tertiary + double-ring + ink-чип сохранены, кольцо/рамка accent-strong; мёртвое правило удалено с указателем. QA: teal 0.435/0.76, editorial-контроль цел. head v79→80.
- Урок: при равной специфичности грепать ВЕСЬ файл на селектор (checked — 9 вхождений), а не только зону вставки.
- **Дальше:** TemplateFragmentContractTest при первом окне без bootRun (7 PENDING-TEST¤); flashcard/empty ветки focus — проверить достижимость read-only; session-gated parity (summary/result) — ждёт решения юзера про EXAM-сессию.

### R0.55 (2026-07-12) — Flashcard+empty ветки read-only + INSTRUMENT-SIGNATURE (v=81)
- Collision guard чист; bootRun жив → контракт-тест PENDING.
- Разведка: единственная тема без seed-JSON = preparation/interview-preparation (42Q без опций; read-only SELECT к quiz-postgres, docker exec). Рецепты read-only QA (вписаны в матрицу): flashcard = GET /?topic=preparation/interview-preparation; empty = GET /review?topic=… («Сейчас нет вопросов»).
- QA flashcard (оба дизайна): вопрос/тема несут C1-i; reveal = отдельный teal r10-чип (токены); reveal вне формы — безопасный клиентский toggle; grade-кнопок в browse-режиме нет (SM2 = due-flow, session-gated).
- QA empty: кнопки token-only корректны. Найден дефект голоса: h2 вне явных правил = semibold 600 (у instrument не было SIGNATURE-секции). Фикс: INSTRUMENT — SIGNATURES (h1..h6 medium) после SWISS-сигнатур; QA 600→500, editorial 500 (свой), swiss 650 цел. head v80→81.
- **Дальше:** контракт-тест при окне без bootRun (7¤); session-gated parity (summary/result/grade-кнопки/.session-progress) — ждёт решения юзера про EXAM-сессию; либо maintenance/полиш по бэклогу Фазы G.

### R0.56 (2026-07-12) — Мультивьюпорт-свип instrument-портов: VERIFIED-CLEAN (0 правок)
- Collision guard чист; bootRun жив → контракт-тест PENDING; session-parity ждёт юзера.
- Свип 375/2560 × light/dark × instrument по read-only поверхностям: /settings, /stats, focus/flashcard (?topic=preparation/interview-preparation), error 404. Все: overflow 0, токены/C*-i-правила несутся на краях диапазона (детали в cr.53).
- Ложный кандидат: CODE шире вьюпорта на flashcard@375 — лежит в pre.question-code overflow-x:auto (паттерн «широкое скроллится в своём контейнере»), не дефект.
- Правок кода нет, v=81 не бампался. По протоколу §28 — verified-clean нота, не «улучшать нечего».
- **Дальше:** контракт-тест при окне без bootRun (7¤); session-gated parity — ждёт EXAM-решения юзера; иначе — следующая maintenance-цель (кандидаты Фазы G: dead-regenerate UI, stats.js instrument-палитра графиков, ?-кнопка, футер-вопрос).

### R0.57 (2026-07-12) — kbd-help/prompt-модалки: тайтлы medium (v=82); 2 кандидата отложены с обоснованием
- Collision guard чист; bootRun жив → контракт-тест PENDING.
- Отложены (не молча): stats.js-палитра графиков (canvas hidden на вымытой базе — QA невозможен); удаление regenerate-UI/.question-side (result.html недостижим read-only — Thymeleaf-рендер нечем проверить; делать одним тиком с контракт-тестом при окне без bootRun).
- QA kbd-help модалки (client-side `?`): токены несут оверлей/карточку целиком; kbd голым mono = общий тихий язык (keyboard-hint такой же) — не дефект. Дельта: тайтлы модалок semibold ((0,2,2)/(0,2,1) бьют сигнатуру (0,1,2)) → фикс в SIGNATURES: kbd-help h3 + prompt-modal-title medium. QA: instrument 500 / editorial 600 цел; Esc работает. head v81→82.
- Урок-артефакт: borderTopColor репортит currentColor при border:none — не принимать за стиль.
- **Дальше:** контракт-тест + regenerate-удаление одним тиком при окне без bootRun; session-gated parity — ждёт юзера; графики — ждут данных.

### R0.58 (2026-07-12) — Lighthouse live-гейт instrument + фикс label-in-name степпера
- Collision guard чист; bootRun жив → контракт-тест PENDING.
- Lighthouse (desktop, navigation) на живом instrument: / — a11y 100/BP 100/agentic 100 (провалы только SEO-шум: meta-description, noindex); /settings — a11y 100/BP 100, но `label-content-name-mismatch`: степпер «A−»/«A+» без видимого текста в accessible name (WCAG 2.5.3, дефект всех дизайнов).
- Фикс: aria-label «A−: уменьшить размер шрифта»/«A+: увеличить…» (settings.html; ID/app.js/контракт не тронуты). Re-audit: label-in-name закрыт (48 passed). Live-синк settings.html.
- Кандидат Фазы G с замером: CLS 0.246 на /settings — PE-раскрытие (стопка→вкладки, personalization, export); осознанная архитектура, чинить только решением «резерв места».
- **Дальше:** контракт-тест + regenerate-удаление при окне без bootRun; session-parity — ждёт юзера; графики — ждут данных; Lighthouse mobile-прогон — опциональный следующий QA-юнит.

### R0.59 (2026-07-12) — Lighthouse mobile (/, /settings, /stats): второй label-in-name починен
- Collision guard чист; bootRun жив → контракт-тест PENDING.
- Mobile-LH: / — a11y/BP/agentic 100 + target-size PASS; /settings — 100/100, фикс R0.58 держится, CLS-кандидат дополнен mobile-замером 0.35; /stats — a11y 100, второй экземпляр label-in-name: статичный aria-label экспандера перекрывал живой textContent → aria-label снят вовсе (имя = живой текст, state в aria-expanded), re-audit чист (51 passed).
- BP 92 на /stats = CSP режет sourcemap Chart.js с jsdelivr (console-error) — лечится только self-host, а это отложенное решение юзера (§5) → зафиксирована связь, не трогаю.
- **Дальше:** греп проекта на паттерн «статичный aria-label + живой textContent» (кандидат тика); контракт-тест + regenerate-удаление при окне без bootRun; session-parity — ждёт юзера.

### R0.60 (2026-07-12) — Греп-аудит label-in-name по всему фронту: VERIFIED-CLEAN (0 правок)
- Collision guard чист; bootRun жив → контракт-тест PENDING.
- Все шаблоны + app.js/stats.js: новых экземпляров антипаттерна нет — пара R0.58 (степпер) / R0.59 (экспандер) была полным множеством. Разбор по категориям (⊇-проверка, icon-only, лендмарки, транзиентный copy-flash) — в cr.57.
- **Дальше:** контракт-тест + regenerate-удаление при окне без bootRun; session-parity — ждёт юзера; графики — ждут данных; из свободных read-only юнитов остаётся мало — при исчерпании перейти в режим поддержки (сторожевые проверки якорей, регресс-скрины) по §28.

### R0.61 (2026-07-12) — Lighthouse dark-theme instrument: VERIFIED-CLEAN (0 правок)
- Collision guard чист; bootRun жив → контракт-тест PENDING.
- Закрыт пробел: axe-контраст тёмной instrument-палитры живьём (localStorage → pre-paint → LH navigation). /, /settings, /stats: color-contrast=1, a11y 100; провалы бит-в-бит = светлым прогонам (SEO-шум, CLS-кандидат, CSP-sourcemap ← deferred self-host). Dark-специфичных дефектов нет.
- localStorage возвращён к дефолтам (браузер юзера не оставлен в instrument-dark).
- Итог LH-серии R0.58–R0.61: a11y 100 / BP 100 на desktop+mobile × light+dark — гейт «AA обе темы» закрыт живым инструментом.
- **Дальше:** свободные read-only юниты исчерпаны почти полностью → режим поддержки §28 (сторожевые проверки якорей при изменениях WIP, регресс-скрины по запросу); контракт-тест + regenerate-удаление при окне без bootRun; session-parity — ждёт юзера; графики — ждут данных.

### R0.62 (2026-07-12) — Режим поддержки §28: sentinel PASS + shell no-js RECHECK закрыт (0 правок)
- Collision guard чист; bootRun жив (параллельная pedago-сессия активна: d4e2aefc).
- Sentinel: 46 instrument-правил на месте; source == build == served бит-в-бит (полный diff). Урок повторно подтверждён: `curl | md5` через rtk-пайп даёт ложный хэш — интегрити только файлом + diff.
- Shell no-js RECHECK → PASS(static R0.62): noscript-алерт role=alert, 3 тоггла hidden (PE), nav plain links, sticky 0. Последняя RECHECK-ячейка матрицы закрыта.
- **Дальше:** режим поддержки — sentinel при следующих тиках только при изменении чужого WIP в зоне порта; разблокировки те же (окно без bootRun / EXAM-решение / данные для графиков).

### R0.63 (2026-07-12) — План-патч удаления мёртвого regenerate-UI готов (read-only prep, паттерн R0.28)
- Collision guard чист (чужой WIP только .cursor state); bootRun жив — правки кода/тесты не запускались.
- Написан `design/mockups/port-drafts/regenerate-removal-plan.md`: точные ханки result.html (btn-regenerate 48-51 + regen-badge + question-side ~121), app.js (API.REGENERATE / regenerateQuestion / ветка делегата; favorite и obtainAdminToken остаются), base.css (6 зон) + процедура верификации window-тика.
- Evidence: read-only SELECT — regen_count max=0, rows>0=0 → regen-badge мёртв вместе с кнопкой. Тест-пинов нет (0 совпадений в 3 контракт/MVC-тестах). Бэкенд /api/regenerate не трогаем (§4).
- **Дальше:** window-тик без bootRun = применить план + таргетный gradle (закроет 7 PENDING-TEST¤); остальные разблокировки прежние (EXAM-решение, данные для графиков).

### R0.64 (2026-07-12) — CLS /settings починен (v=83): pre-paint data-js/data-settings-tab, 0.285→0.0004
- Разблокировки проверены: bootRun жив (window-тик ждёт), данных для графиков нет (0|0) → взят Фаза-G кандидат «резерв места под PE-блоки» с замером.
- Диагностика: сдвиг только под троттлингом (LH-условия); худший случай — restore сохранённой вкладки (#panel-appearance, 0.2844). Локально app.js успевает до первого кадра.
- Фикс: head.html pre-paint ставит data-js + валидированный data-settings-tab на html ДО первого кадра; base.css — 4 правила [data-js] (tablist flex !important против .hidden, панели none, panel-title sr-only, одна панель по ID; ID-правила гейтованы :not(.js-tabs)). Без JS атрибутов нет — PE-стопка не тронута.
- QA: троттлированный CLS 0.285→0.0004; pre-DCL снимок = вкладочное состояние до app.js; интерактив вкладок цел; instrument-контроль чист; localStorage сброшен.
- **Дальше:** window-тик = regenerate-план + контракт-тест (head.html тронут — обязательно); остальные гейты прежние (EXAM, данные для графиков).

### R0.65 (2026-07-12) — CLS-свип read-only страниц под троттлингом: VERIFIED-CLEAN (0 правок)
- Collision guard чист; bootRun жив; данных для графиков нет → взята верификационная единица: методология R0.64 на все достижимые поверхности + контроль глобального data-js.
- Замеры (Slow 4G + CPU 4x, холодный кэш): / desktop 0.0012 (LABEL, шрифт) / mobile 0; /stats 0; flashcard 0; empty 0; error 404 0; /settings mobile 0 (baseline был 0.35 — фикс R0.64 закрыл и mobile).
- CLS-строка Фазы G закрыта полностью; result/summary — parity-фаза. Эмуляция сброшена.
- **Дальше:** window-тик (regenerate-план + контракт-тест — head.html тронут в R0.64); EXAM-решение; данные для графиков.

### R0.66 (2026-07-12) — alertdialog сброса применён (app.js v=57, base.css v=84): window.confirm ушёл
- Последний не-гейтнутый кандидат Фазы G. promptModal получил mode:'confirm' (alertdialog, aria-describedby, сообщение вместо input, danger-OK «Подтвердить», фокус на «Отмена», resolve(true|false)); confirmModal-обёртка передаётся в initDangerousFormGuard (top-level, вне IIFE); гард = preventDefault → модалка → requestSubmit с data-confirmed. settings.html: + data-confirm-title.
- QA живьём без подтверждения: alertdialog/фокус/inert/Esc/Отмена — чисто, форма не отправлялась; регрессия prompt-режима (экспорт JSON) бит-в-бит; node --check OK. PE: без JS отправка без подтверждения, как и раньше.
- Бампы: app.js v=57 (3 шаблона), base.css v=84 (+ .prompt-modal-message). Тест-пинов на reset-форму нет.
- **Дальше:** window-тик (regenerate-план + контракт-тест — head.html/settings.html/app.js тронуты); EXAM; данные для графиков. Не-гейтнутых кандидатов Фазы G больше нет — дальше режим поддержки/углубление аудита.

### R0.67 (2026-07-12) — Confirm-модалка в instrument (обе темы) + focus-trap: VERIFIED-CLEAN (0 правок)
- Пробел cr.63 (QA был только editorial light): instrument dark/light проверены живьём — тайтл 500 (SIGNATURE R0.57 достаёт динамическую модалку), сообщение text-secondary, danger-OK в error-языке страницы (скоуп .settings-page .danger-btn достаёт модалку в body), контрасты с запасом; скриншот чист.
- Focus-trap проверен в обе стороны (Tab/Shift+Tab оборачивают); cr.63 проверял только дефолт-фокус и Esc.
- localStorage сброшен; правок кода нет.
- **Дальше:** гейты прежние (window-тик с контракт-тестом, EXAM, данные для графиков); режим поддержки §28.

### R0.68 (2026-07-12) — Focus-visible аудит /settings: невидимый клавиатурный фокус tabpanel починен (v=85)
- Новая размерность (WCAG 2.4.7). Архитектура: глобальный ринг + глобальная мышиная тишина → аудит = 10 сайтов outline:none. 6 с заменой, 1 осознанный (#main-content), 1 дефект.
- Дефект: .settings-panel:focus{outline:none} (tabs-редизайн 0eb762a5) глушил клавиатурный фокус панели tabindex=0 — Tab из tablist приземлялся невидимо; правило избыточно для мыши. Удалено с комментарием; ринг вернулся через глобальный :focus-visible. QA реальным Tab: ринг 2px border-focus на панели.
- Методика: script-focus после реального Tab наследует клавиатурную модальность; box-shadow с transition мерить после settle ~350ms (ложный минус select); элементы в скрытых панелях легитимно не фокусируемы.
- **Дальше:** гейты прежние (window-тик + контракт-тест, EXAM, данные); режим поддержки.

### R0.69 (2026-07-12) — Focus-visible свип / + /stats + flashcard: VERIFIED-CLEAN (0 правок)
- Методика R0.68 на остальные read-only поверхности (instrument): MCQ-label :has-ринг живьём; submit disabled→enable по выбору радио (клиентски, без POST) → ринг; /stats: search/select shadow-focus, apply/экспандер/ссылки глобальный ринг, sortable th inset −2px; flashcard: reveal/summary ринг, code-copy в свёрнутом details легитимно не фокусируем (content-visibility-квирк задокументирован), после открытия — ринг.
- error 404 покрыт архитектурно (нет outline:none-правил на его элементах). Итог размерности: единственный дефект был R0.68 (tabpanel), всё остальное чисто.
- **Дальше:** гейты прежние (window-тик + контракт-тест, EXAM, данные); режим поддержки.

### R0.70 (2026-07-12) — Reflow-свип 320px (WCAG 1.4.10): VERIFIED-CLEAN (0 правок)
- Пробел: прежние свипы от 375px, стандарт требует 320. Прогнано 320×800 dpr2: editorial — /, /settings (3 вкладки), /stats (+раскрытая таблица), flashcard, empty, 404; instrument — /settings, /. Везде scrollWidth==320; единственный широкий элемент (CODE flashcard) — в собственном overflow-x:auto (законное исключение 1.4.10).
- Браузер/localStorage возвращены к дефолтам. Правок кода нет.
- **Дальше:** гейты прежние (window-тик + контракт-тест, EXAM, данные); режим поддержки.

### R0.71 (2026-07-12) — Ось движения data-motion live-QA: VERIFIED-CLEAN (0 правок)
- Все 3 состояния живьём на /settings: off → attr + transition 1e-06s + scroll auto; on → 0.15s + smooth; auto → атрибута нет. window.__motion жив; seg-контрол «Оформление» связан (attr+localStorage+aria-pressed, возврат чист).
- Ограничение: @media-путь (OS reduce) не эмулируется этим MCP — корректность конструктивно (тело = off-блок, гейт :not([data-motion="on"])); прогнать живьём при появлении эмуляции.
- localStorage возвращён. Правок кода нет.
- **Дальше:** гейты прежние (window-тик + контракт-тест, EXAM, данные); режим поддержки.

### R0.72 (2026-07-12) — Оси персонализации live-QA (раскладка/шрифт/ширина/плотность): VERIFIED-CLEAN (0 правок)
- Раскладка: flow↔split доказан геометрией (два пейна vs столбец); grid = атрибут без CSS-правил (базовая композиция, по докстроке) — flow==grid на browse законна, полная проверка session-gated. Ширина: 72ch→100ch, проза 818→910px. Шрифт: 120% → root 19.2px, сброс чист. Плотность: НАХОДКА — осей 7 (data-density в tokens.css + head.html), compact → --space-6 2→1.5rem.
- Все атрибуты/ключи возвращены (null пост-фактум). Правок кода нет.
- **Дальше:** гейты прежние; осевое QA ЗАВЕРШЕНО (7/7); режим поддержки.

### R0.73 (2026-07-12) — Стресс-комбо 7 осей (instrument+dark+split+compact+wide+font1.4): VERIFIED-CLEAN (0 правок)
- Интеракции осей: / @1280 — split-геометрия жива, прямоугольники не перекрываются, 0 overflow, root 22.4px; /settings @1280 — 3 вкладки чисты, вкладка 137×63 (touch с запасом); @375 — split деградирует в столбец медиа-запросом, 0 overflow везде.
- Ортогональность осей подтверждена живьём в worst-case, не только по построению. localStorage снят (8 ключей).
- Незакрываемое тулингом: @media reduce/contrast/forced-colors — нет эмуляции в MCP (задокументировано).
- **Дальше:** гейты прежние (window-тик + контракт-тест, EXAM, данные); режим поддержки.

### R0.74 (2026-07-12) — Ассет-целостность + консоль после v-бампов: VERIFIED-CLEAN (0 правок)
- Холодные загрузки 4 шаблонов: / и /settings — tokens 62/base 85/app 57, все 200, консоль пуста; /stats — stats.js v=13, только известный CSP-шум sourcemap (deferred self-host); 404 — ассеты 200, «ошибка» = сам документ (легитимно).
- Ни одного битого ассета/stale-версии/новой JS-ошибки. Правок кода нет.
- **Дальше:** гейты прежние (window-тик + контракт-тест, EXAM, данные); режим поддержки.

### R0.75 (2026-07-12) — Функциональное QA интерактива /stats: VERIFIED-CLEAN, ложная тревога поиска разоблачена (0 правок)

- **Единица:** новая размерность §28-аудита — ПОВЕДЕНИЕ интерактива /stats (сортировка, экспандер таблицы тем, GET-формы поиска и фильтров). Всё client-side или GET — деструктива нет.
- **Ложная тревога:** первый проход показал «поиск „kafka" → 12 строк; очистка → всё ещё 12». Разбор: клиентского поиска по таблице тем НЕ существует (единственный поиск — серверная GET-форма `.search-input` name=q), а 12 = limit свёрнутого экспандера (`data-collapse-rows`, `row.hidden`). Совпадение чисел породило ложный вывод — дефекта нет.
- **Проверено живьём:** (1) сортировка th — aria-sort цикл + live-region объявление + реальная смена порядка; (2) ре-синк MutationObserver в свёрнутом виде: после сортировки видимых ровно 12 нового порядка, хвост пере-скрыт; (3) экспандер 12↔319 в обе стороны, textContent/aria-expanded синхронны; (4) GET-поиск round-trip: q удержан, «Результаты поиска» рендерится, WAL-результат по kafka = честный full-text (4 упоминания в исходнике); (5) GET-фильтры round-trip: чекбоксы удержаны, две формы взаимно протаскивают параметры через hidden-поля.
- **Каверза проб (в копилку):** имя темы в таблице = `th[scope=row]` → `querySelector('td')` отдаёт колонку «Всего»; числа вместо тем в пробах — не баг, а признак правильной a11y-разметки.
- **Гигиена:** браузер возвращён на чистый /stats; localStorage не трогался. Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик (regenerate-план + контракт-тест) / EXAM / данные для графиков.

### R0.76 (2026-07-12) — Клавиатурная операбельность: Tab-порядок и шорткаты живьём; дефект «„?" мёртв с radio/checkbox» починен (app.js v=58, stats.js v=14)

- **Единица:** новая размерность §28 — операбельность с клавиатуры (не видимость фокуса, а работоспособность): Tab-порядок, горячие клавиши, ловушки. Enter не нажимался (submit ответа = деструктив).
- **VERIFIED:** Tab-порядок / — skip-link первым → бренд → nav → тогглы → radio-группа; disabled-submit пропускается; ловушек нет. Шорткаты MCQ: «1-9» выбор + разблокировка submit, стрелки циклят, Escape снимает выбор (submit re-disable, фокус не теряется), вне диапазона — no-op.
- **ДЕФЕКТ ПОЧИНЕН:** гард хоткеев справки глушил все INPUT → «?» был мёртв с фокуса на radio (app.js:1918 — основная клавиатурная позиция страницы вопроса), а на /stats с чекбокса фильтра были мертвы «?» и «/» (stats.js:385). MCQ-хэндлер (1183) radio давно исключал — рассинхрон гардов. Фикс: исключение radio/checkbox в обоих файлах; текстовые поля остаются заглушенными («?» в поиске — литеральный ввод, подтверждено живьём).
- **Re-QA:** «?» с radio/чекбокса открывает справку, гард «шорткаты под оверлеем» цел, Esc возвращает фокус, «/» прыгает в поиск. Консоль — только известный CSP-шум. Гигиена: поиск очищен, браузер на чистых страницах.
- **Правки:** app.js (+комментарий-обоснование), stats.js, v-бампы в 4 шаблонах (result/settings/focus-training v=58; stats v=14), live-синк. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик (regenerate-план + контракт-тест) / EXAM / данные для графиков.

### R0.77 (2026-07-12) — Клавиатурная операбельность /settings: VERIFIED-CLEAN, размерность замкнута (0 правок)

- **Единица:** завершение размерности R0.76 на самой клавиатурно-насыщенной странице: ARIA-вкладки, seg-radiogroup осей, степпер шрифта — всё реальными нажатиями.
- **Вкладки:** ArrowRight/End/Home + wrap-around ArrowLeft; каждый шаг синхронно двигает aria-selected, roving tabindex, panel.is-active, фокус и localStorage settingsTab.
- **Seg-контрол (ширина чтения, паттерн един для 6 осей):** одно нажатие стрелки = фокус + aria-checked + html-атрибут + localStorage + live-region + применение --measure 72ch→100ch; возврат стрелкой — ось сама вычищает дефолт (attr и ключ сняты).
- **Степпер:** Space → 110% (inline + live-region); reset → чисто, кнопка сама уходит в aria-disabled=true (фокус не сбрасывается — осознанная замена native disabled).
- **Итог:** дефектов нет; размерность «клавиатурная операбельность» закрыта на всех read-only поверхностях (/ + /stats в R0.76, /settings здесь); result/summary — в parity-фазу. Все 8 ключей localStorage возвращены к null. Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.78 (2026-07-12) — Входная устойчивость: мусор в localStorage и GET-параметрах, XSS-echo — VERIFIED-CLEAN (0 правок)

- **Единица:** новая размерность §28 — устойчивость к мусорному вводу по двум каналам: localStorage (8 ключей персонализации) и GET-параметры (q/important/topic).
- **localStorage:** все оси живьём подтвердили белые списки head.html: XSS-пейлоад в design не попадает в атрибут (→editorial), мусорные значения → дефолты, fontScale=1e10 → кламп 1.4 (страница юзабельна). Консоль пуста. Нюанс (не дефект): read-path не перезаписывает мусорный ключ — лежит до первого set(), вреда нет.
- **GET:** q=`<script>` эскейплен в input и echo (0 внедрённых тегов); important=junk → graceful-редирект на / без 500/stacktrace (бэкенд, вне §4); topic-траверсал на /stats игнорируется; мусорный topic на / и /review → штатный empty-state с CTA. Утечек Whitelabel/stacktrace нет нигде.
- **Каверза проб:** «exception» на /stats = слаг java-exceptions-interview в data-island, не утечка — контекст матча проверять до классификации.
- **Гигиена:** ключи сняты, браузер на чистом /. Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.79 (2026-07-12) — Инвентаризация мёртвого CSS: 2 зоны найдены, план применения готов (prep-тик, 0 правок кода)

- **Единица:** новая размерность — гигиена самого base.css. Детерминированный скан 368 класс-селекторов × корпус (templates+js) + контроль по Java/prompts; 34 сырых кандидата разобраны вручную.
- **Зона A (≈110 строк, ~2585–2701):** стили AI-разбора (takeaway/code-trace/trace-step/wrong-feedback/comparison, 18 классов) — продюсеры вырезаны с AI 2026-07-07; живым остался только .related-questions. Два stale-комментария врут про живость (base.css ~2650, app.js:1600).
- **Зона B:** старый layout /settings до вкладочного редизайна — .control-section/-tips/-title + семейство #left-sidebar-* (0 в шаблонах/JS, 10 вхождений в CSS).
- **Ложные кандидаты разоблачены:** .ed-theme-toggle (живое правило), ed-*-комментарии (документируют прежние удаления), регэксп-шум из кода-примеров. .sr-only — микро-дубль, решение при применении.
- **Артефакт:** design/mockups/port-drafts/dead-css-inventory.md с протоколом применения (паттерн R0.63: prep → чистое применение следующим тиком). ID-скан целиком — зона роста. Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.80 (2026-07-12) — Мёртвый CSS применён по плану R0.79: −173 строки (base.css v=86, app.js v=59)

- **Единица:** применение dead-css-inventory (паттерн prep→apply). Live-проверка served-HTML трёх страниц перед удалением: 0 узлов по 13 селекторам.
- **Вырезано:** зона A — AI-разбор (takeaway/code-trace/trace-step/wrong-feedback/comparison, 123 строки, 2579–2701); зона B — каркас /settings до вкладок (#left-sidebar-* медиа-композиция 36 строк + .control-section/-tips/-title 14 строк). Живые соседи целы (.control-inline-hint, .filters-fields, stats-грид, @media .result-actions).
- **Попутно:** 4 stale-комментария актуализированы (base.css ×2, app.js ×2); надгробия-комментарии оставлены; .sr-only-алиас оставлен осознанно.
- **Верификация:** grep-чистота (только надгробия), node --check OK, скрин-контроль /settings dark+light и / — целы, консоль пуста, scrollW в норме. base.css 4038→3865 строк. Файл-план удалён как применённый.
- **Остаток:** полный ID-скан (классы покрыты, ID — точечно) = зона роста. Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.81 (2026-07-12) — ID-скан + сироты-CSS: 4 легаси-файла (~201KB) удалены, #extra-analysis-toggle-result вырезан (base.css v=87)

- **Единица:** заявленная зона роста R0.79 — полный скан ID-селекторов (21 шт.) + случайно вскрытая размерность «инвентарь файлов».
- **ID-скан:** 1 реальный мертвец — #extra-analysis-toggle-result (no-JS кнопка «доп. анализа» на result.html удалена с AI): вырезан из 3 зон base.css хирургически, живой #extra-analysis-toggle не задет (проверено живьём); 2 кандидата — регэксп-артефакты.
- **Сироты:** editorial.css (169KB!), linear.css, swiss.css, broadsheet.css — 0 ссылок в templates/js/java/config; поколение до switchable-редизайна. git rm + вычистка build/resources; fetch → 404, страницы целы. Stale-упоминание в application.yml зафиксировано (файл вне §4 — не тронут).
- **Урок:** инвентарь ФАЙЛОВ ≠ инвентарь СЕЛЕКТОРОВ — сироты маскировались под живые строки в maintenance-грепах.
- **Верификация:** grep 0 вхождений, v=87 отдаётся, консоль чиста (кроме собственного probe-404). Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.82 (2026-07-12) — Файловый инвентарь + tokens.css + мёртвые JS-функции: линия гигиены замкнута (app.js v=60)

- **Единица:** три под-скана, закрывающие линию R0.79–R0.81 по всем осям мёртвого кода.
- **Файлы:** static/ = 5 файлов, templates/ = 16 — ВСЕ referenced (фрагменты th:replace, корневые в контроллерах, favicon в head). Сирот нет.
- **tokens.css:** 9 классов живые, 0 ID — CSS-поверхность закрыта целиком (base классы R0.79 + ID R0.81 + tokens + файлы).
- **JS:** def/use-скан 103 функций — единственный труп apiPost (осиротел при переходе POST-флоу на apiFetch) удалён; regenerateQuestion не тронут (ждёт window-тика в regenerate-плане). node --check OK, smoke чист.
- **Итог линии гигиены:** −173 строки CSS, −4 файла (~201KB), −toggle-result, −apiPost. Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.83 (2026-07-12) — Целостность DOM-ссылок + модальная гигиена: VERIFIED-CLEAN (0 правок)

- **Единица:** новая размерность — ссылочная целостность DOM: дубли ID, битые aria-labelledby/-describedby/-controls, label[for] в никуда; плюс утечки модалок при повторных циклах.
- **Served-HTML:** 6 страниц — 0 находок по всем четырём классам.
- **Живой DOM:** prompt ×2 и kbd-help ×2 — без оставленных оверлеев/inert/дублей; confirm-цикл чист (1 оверлей → «Отмена» → 0/0/0, без сабмита).
- **Урок методики:** первая «утечка» (2 модалки + 5 inert) — артефакт пробы: программный .click() проходит сквозь inert, чего пользователь не может (pointer заблокирован, focus-trap держит Tab); попутно подтверждена устойчивость — Esc разбирает даже нештатный стек по одной модалке без сирот. Селекторы динамических узлов сверять с кодом (.prompt-overlay, не «угаданный» .prompt-modal-overlay).
- Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.84 (2026-07-12) — Live-a11y непроверенных дизайнов, серия 1/3: notion+superhuman+theverge — 6/6 чисто (0 правок)

- **Единица:** новая серийная размерность — живой axe/Lighthouse по дизайнам, которые проверялись только статическим токен-аудитом (не видит rgba/градиентных ловушек). До сих пор live-LH были только editorial и instrument.
- **Прогнано:** notion, superhuman, theverge × / (MCQ) × dark+light = 6 прогонов: a11y 100, BP 100, color-contrast PASS везде (провалы — только вечный SEO-шум). Token-only дизайны второй волны впервые подтверждены живым инструментом.
- **Механика:** localStorage design/theme → pre-paint до первого кадра → LH меряет настоящий дизайн (паттерн R0.58). Дефолты возвращены.
- **Остаток серии:** linear+swiss+mintlify (2/3), broadsheet+stripe+claude (3/3), затем spot-check /settings. Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.85 (2026-07-12) — Live-a11y серия 2/3: linear+swiss+mintlify — 6/6 чисто (0 правок)

- Та же механика (localStorage → pre-paint → LH на /): linear, swiss, mintlify × dark+light — a11y 100, BP 100, контраст PASS во всех шести прогонах; SEO-шум единственные провалы.
- Счёт размерности: 8/11 дизайнов подтверждены живым axe. Остаток: broadsheet+stripe+claude (серия 3/3), затем spot-check /settings. Дефолты возвращены. Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.86 (2026-07-12) — Live-a11y серия 3/3: broadsheet+stripe+claude — 6/6 чисто; размерность 11/11 ЗАКРЫТА

- Та же механика (localStorage → pre-paint → LH на /): broadsheet, stripe, claude × dark+light — a11y 100, BP 100, контраст PASS во всех шести прогонах; SEO-шум единственные провалы.
- Размерность live-a11y по дизайнам закрыта: все 11 дизайнов подтверждены живым axe в обеих темах, 0 контраст-провалов — статический AA-гейт подтверждён живьём. Остаток: spot-check /settings на swiss vs claude (след. тик). Дефолты возвращены. Контракт-тест PENDING (bootRun жив). Гейты прежние.

### R0.87 (2026-07-12) — Spot-check /settings (swiss vs claude): 4/4 чисто — размерность live-a11y ЗАКРЫТА полностью

- /settings (вкладки + 7 сег-контролов + степпер) × swiss/claude × dark/light — a11y 100, BP 100, контраст PASS во всех четырёх прогонах (50 passed-аудитов; SEO-шум единственные провалы).
- Размерность live-a11y закрыта целиком: 11/11 дизайнов на / + плотнейшая интерактивная страница на полюсных палитрах. Дефолты возвращены. Контракт-тест PENDING. Следующая размерность по §28: reflow/zoom 400% / print / aria-live-протокол.

### R0.88 (2026-07-12) — Reflow 320px чисто + коррекция R0.86/R0.87 (неверные ключи localStorage, все 10 аудитов перегнаны)

- WCAG 1.4.10: /, /stats, /settings (3 вкладки) на 320×900 — 0 горизонтального скролла, 0 элементов вне вьюпорта (вне легитимных overflow-x:auto). Дефектов нет.
- КОРРЕКЦИЯ: в R0.86/R0.87 ключи `quiz-design`/`quiz-theme` вместо `design`/`theme` → whitelist бесшумно падал на editorial; те 10 прогонов мерили editorial. Перегнано с верными ключами + обязательной проверкой data-design/data-theme на html после reload: 10/10 a11y 100 / BP 100 (broadsheet+light CLS 0.99 — блип). Выводы cr.83/cr.84 восстановлены честно; протокол R0.58 усилен (верифицировать атрибут, не localStorage). Дефолты возвращены. Контракт-тест PENDING.

### R0.89 (2026-07-12) — Стресс-комбо reflow 320px + fontScale 1.4: чисто — размерность reflow ЗАКРЫТА

- Вьюпорт 320×900 + шрифт-степпер на потолке (1.4 → root 22.4px, подтверждено computed): /, /stats, /settings (3 вкладки) — 0 горизонтального скролла, 0 элементов вне вьюпорта. Классический провал 1.4.4×1.4.10 (растущий текст выталкивает фикс-ширины) отсутствует.
- Размерность reflow закрыта (320 дефолт + 320×1.4 + ранние 375/768/1280). Дефолты возвращены, эмуляция снята. Контракт-тест PENDING. Дальше: print-стили или aria-live-аудит инъекций app.js.

### R0.90 (2026-07-12) — Print-гигиена фокус-страницы: хром сессии больше не печатается (base.css v=88)

- Аудит @media print: result/stats/summary покрыты, а у страницы вопроса print-правил не было — на бумагу шли топбар сессии, submit-футер, клавиатурная подсказка, формы флешкарты, confidence/«Похожие вопросы», CTA пустого состояния. Добавлен focus-блок в hide-лист (11 селекторов); вопрос/варианты/вердикт остаются.
- Верификация: brace-balance 0, live CSSOM (v=88, селекторы распарсены и матчатся). v-бамп 87→88, live-sync. Контракт-тест PENDING. Дальше: aria-live-аудит инъекций app.js.

### R0.91 (2026-07-12) — Aria-live-аудит динамических инъекций: VERIFIED-CLEAN (0 правок)

- Все 60+ точек мутации DOM в app.js + stats.js/stats.html проверены: инфонесущие обновления идут в live-регионы (result-feedback, inline-alert, confidence, copy/export/personalization/sort-status, chart-fallback, filter-mode-hint, font-scale-value), осознанные исключения (таймер aria-hidden, today-чип) обоснованы, транзиентные лейблы кнопок — норма. Паттерн «.hidden-класс + текст до показа» консистентен.
- Правок нет. Контракт-тест PENDING. Дальше: prefers-reduced-motion покрытие / forced-colors / window-тик.

### R0.92 (2026-07-12) — Аудит покрытия prefers-reduced-motion: VERIFIED-CLEAN (0 правок)

- Все источники движения гасятся двухпутевым kill-механизмом (media + data-motion=off, форс on через :not): единственный @keyframes + transitions — wildcard 0.001ms; scroll-behavior перебит; delay/fill-mode escape отсутствуют; три JS-сайта (inline-alert scroll, back-to-top, Chart.js) — идентичный 3-way протокол; дублирование протокола осознанное (разные бандлы).
- Правок нет. Live-эмуляция Reduce тулингом недоступна (tooling-gated). Дальше: forced-colors / window-тик.

### R0.93 (2026-07-12) — Forced-colors: 3 дефекта исправлены (base.css v=89)

- Windows High Contrast: (1) выбранный MCQ-вариант был неотличим (radio скрыт, state только цветом) — бейдж+рамка теперь Highlight/HighlightText; (2) confidence-btn.selected — так же; (3) прогресс-бар исчезал (background-only) — track очерчен CanvasText, fill Highlight.
- Не-дефекты проверены и не тронуты (вкладки-underline, статус-лейблы вариантов, dimmed-opacity). Верификация: CSSOM v=89, все правила распарсены; HC-эмуляция тулингом недоступна. v-бамп 88→89, live-sync. Контракт-тест PENDING.

### R0.94 (2026-07-12) — Target-size (2.5.8) на 375px: чисто; фикс невидимого фокуса в стек-таблице /stats (v=90)

- Живой замер интерактивных целей на /, /stats, /settings: 0 провалов под 24px (AA); шапка 40px — осознанный компромисс. Ложные «провалы» th оказались sr-only-клипом стек-режима таблицы (≤760px).
- Реальный дефект: сортируемые th (tabindex=0) в клипнутом thead = 5 невидимых фокус-стопов (2.4.7). Фикс: thead:focus-within разклипывается (skip-link паттерн); verified live (1px→246px→1px). v-бамп 89→90, live-sync. Контракт-тест PENDING.

### R0.95 (2026-07-12) — Focus-order аудит (2.4.3): VERIFIED-CLEAN (0 правок)

- /, /stats, /settings: 0 positive tabindex; skip-link первый везде; все «прыжки назад» разобраны — двухколоночные формы (логичный колоночный обход), нефокусируемый скрытый back-to-top, артефакты фильтра (скрытые панели реально вне tab-порядка: 24/41/17 против 53 сырых).
- Уроки тулинга: getClientRects для ancestor-display:none; visibility:hidden ≠ нет rect'ов. Правок нет. Контракт-тест PENDING.

### R0.96 (2026-07-12) — Эргономика полей форм (мобайл): VERIFIED-CLEAN (0 правок)

- 375×812: все поля ≥16px (нет iOS-автозума), 100% с лейблами (placeholder только дополняет), поиск type=search + enterkeyhint, number/text-полей нет по построению (селекты/радио), радио MCQ 4/4 в лейблах. Autocomplete N/A (нет персональных данных).
- Правок нет. Контракт-тест PENDING. Дальше: window-тик / lang-of-parts / ::selection / 3.2.3.

### R0.97 (2026-07-12) — ::selection (22 комбо) + навигация (3.2.3): VERIFIED-CLEAN (0 правок)

- ::selection контраст по всем 11×2 палитрам ≥10:1; «провал instrument» — ложный (oklch-токены нельзя парсить regex'ом, только canvas-нормализация; урок тулинга №3). Навигация/тогглы/landmarks идентичны на трёх страницах, титулы уникальны, skip-link контекстный.
- Правок нет. Контракт-тест PENDING. Дальше: window-тик / lang-of-parts.

### R0.98 (2026-07-12) — Язык (3.1.1/3.1.2) + console-суип: VERIFIED-CLEAN (0 правок)

- lang="ru" на 6/6 шаблонах; англо-фрагменты (темы/код/термины) — изъятия 3.1.2, span lang="en" осознанно не размечаем (прыжки голосов SR хуже). Console: /, /settings — 0 сообщений; /stats — только известный CSP-sourcemap-шум (deferred §5).
- Правок нет. Чужой WIP в seed/mcq не тронут. Контракт-тест PENDING; след. тик — window-тик либо сводный ре-скан плана.

### R0.99 (2026-07-12) — СВОДКА РАУНДА R0.75–R0.98 (консолидация; window-тик всё ещё заблокирован живым bootRun)

**Закрытые размерности раунда (живой QA + статический аудит):**
- /stats функциональный QA (R0.75); клавиатурная операбельность всех read-only поверхностей + фикс «?»/«/» с radio/checkbox (R0.76–77); input-robustness 8 whitelist'ов + XSS-эхо (R0.78).
- Dead-code гигиена целиком: −173 строки CSS, −4 файла (~201KB), −apiPost, 6 stale-комментариев (R0.79–82); DOM-integrity 6 страниц (R0.83).
- Live-a11y ВСЕ 11 дизайнов × 2 темы на / + spot-check /settings на полюсных палитрах (R0.84–87; методология исправлена в R0.88 — верифицировать data-атрибуты, не localStorage).
- Reflow 320px + стресс 320×fontScale 1.4 (R0.88–89); print-гигиена фокус-страницы (R0.90, v=88); aria-live протокол (R0.91); prefers-reduced-motion (R0.92); forced-colors — 3 фикса (R0.93, v=89); target-size 2.5.8 + фикс невидимого фокуса th (R0.94, v=90); focus-order 2.4.3 (R0.95); эргономика полей (R0.96); ::selection 22/22 + навигация 3.2.3 (R0.97); язык 3.1.1/3.1.2 + console (R0.98).

**Код-фиксы раунда:** 82f662f5 (print), debf26ba (forced-colors), a6ba720b (th focus), c4fcaa36 (kbd-help), 50911849/9f0c425c/f81f8221 (dead-code). base.css v=87→90, head.html тронут 5 раз.

**Уроки тулинга (копить, не повторять ошибки):** (1) CSSStyleRule.cssRules существует в nested-CSS — матчить по selectorText; (2) ancestor display:none → фильтровать getClientRects; (3) oklch-цвета парсить только canvas-нормализацией; (4) localStorage-ключи осей БЕЗ префикса + верифицировать data-атрибут после reload.

**Гейты (заблокированное — НЕ трогать до разблокировки):**
| Гейт | Что ждёт |
|------|----------|
| Window-тик (:8080 свободен) | ПРИОРИТЕТ №1: контракт-тесты (5 касаний head.html, base.css v87→90, накоплено с R0.76) + regenerate-removal-plan.md (строки СДВИНУТЫ — re-grep) |
| Решение юзера EXAM | Phase F: summary/result parity, grade-flow, no-JS /answer |
| Решение юзера §5 | self-host Chart.js, favorite, analytics CTA, футер, вертикальный tablist |
| Данные в БД | stats.js instrument-палитра графиков |
| Тулинг | live-эмуляция prefers-reduced-motion / forced-colors (MCP emulate умеет только colorScheme) |

### R0.100 (2026-07-13) — СВЕРКА МАТРИЦ §7/§9 с реальностью раунда + коррекция дрейфа R1.NN

- **Проблема (замечание юзера):** матрицы §7 (поверхности) и §9 (прод-файлы) стояли сплошь ⬜ TODO, хотя раунд R0.1–R0.99 фактически прошёл mockup-recheck → mapping → Фаза E порт 7/7 → Фаза G рефакторинг → регрессию-a11y по ВСЕМ размерностям. Прогресс жил только в логе §21, не в сводных таблицах → «не видно прогресса по таблицам».
- **Причина дрейфа:** нарушен §0 промпта («в начале КАЖДОГО тика читать PLAN_FRONTEND.md ПОЛНОСТЬЮ, не полагаться на память»). Компакт-саммари этой сессии привязал к ДОРЕСЕТ-парадигме WCAG-свипа → 25 блоков R1.73–R1.97 (2026-07-13) дублировали уже закрытое: R1.96 focus-order = R0.95; R1.90/R1.97 язык+headings = R0.98; R1.85 reflow = R0.88-89; R1.83 status-messages = R0.91; R1.87 target-size = R0.94; R1.82 label-in-name = R0.58-60; R1.62/R1.91 focus-visible = R0.68-69. Единственная НОВАЯ прод-правка серии — R1.74-FIX (consistent-identification «Завершить сессию», в git). Всё оторвано от матриц.
- **Сделано ЭТИМ тиком:** §7 (10 поверхностей) + §9 (23 файла) сведены с R0.1–R0.99 — 33 строки статусов проставлены (Inventory→Regression преим. ✅; Parity ◐ для session-gated result/summary; Final ◐/⏸ по гейтам). Удалённые в R0.81 css (editorial/linear/swiss/broadsheet) помечены 🚫. Легенда статусов добавлена под §7. R1.73–R1.97 закрыты SUPERSEDED-баннером (история сохранена — не удаляю, там реальный R1.74-FIX).
- **Состояние конечной цели:** все НЕ-гейтнутые единицы пайплайна закрыты. Осталось строго gated (§21 R0.99): (1) **window-тик** — контракт-тесты (5 касаний head.html, base.css v87→90) + regenerate-removal, ждёт свободного :8080; (2) решение юзера **EXAM** — parity result/summary/grade-flow; (3) решения §5 — self-host Chart.js, favorite, footer, вертикальный tablist; (4) данные БД — палитра графиков stats.js.
- **⚠️ ОПЕР-ИНЦИДЕНТ (2026-07-13):** app отдаёт **HTTP 500 на всех роутах** (/,/stats,/settings), ответ ~30с (таймаут пула Hikari), `pgrep bootRun` пуст → похоже на падение bootRun ИЛИ недоступность quiz-postgres. Если bootRun мёртв — это и есть окно для window-тика (контракт-тесты станут безопасны, wedge-риск снят). Диагностику/перезапуск стека отдаю юзеру — app/БД сам не чиню, gradle при неясном состоянии не гоняю.
- **Дальше (по §0):** следующий тик — читать план ЦЕЛИКОМ → либо window-тик (если :8080 подтверждённо свободен), либо gated-ожидание / углубление §10-бэклога. Нумерация продолжается как R0.101+; ветка R1.NN закрыта.


**Read-only поверхности исчерпаны по глубине текущего инструментария.** Следующие тики до разблокировки гейтов: регрессия-ре-скан ранее фиксированных зон (по одной за тик, против дрейфа), либо новые размерности по мере появления идей/тулинга.

### R1.00 (2026-07-12) — Регрессия-ре-скан R0.76 (хоткеи с radio/checkbox): без дрейфа 6/6

- Живой прогон обеих зон: / («?» с radio, Esc, гард текст-поля) и /stats («?» с чекбокса, Esc, «/» → поиск) — все 6 проверок зелёные; фикс пережил app.js v58→60 и base.css v87→90 без регрессий.
- Правок нет. Контракт-тест PENDING. Следующий ре-скан: R0.83 (модалки) или R0.94 (thead-фокус).

### R1.01 (2026-07-12) — Регрессия-ре-скан R0.83 (модалки): без дрейфа 3/3 + урок тулинга №5

- Чистый прогон confirm-модалки ×3 (Отмена / Esc / фон): alertdialog, inert×4, фокус за 1 кадр, полная очистка и возврат фокуса — всё зелёное. Промежуточные «провалы» — артефакты проб: Esc-на-document не доходит до overlay-хэндлера; rAF-троттлинг фоновой вкладки (замерять через await raf(), не setTimeout); stale-селектор в стопке. Стопка оверлеев достижима только программно сквозь inert.
- Правок нет. Контракт-тест PENDING.

### R1.02 (2026-07-12) — Регрессия-ре-скан R0.78 (whitelists 8 осей): без дрейфа 8/8

- Мусор во все 8 ключей (XSS/traversal/1e10/javascript:) → reload → штатные дефолты по всем осям (layout=flow — явный фоллбэк по проекту), clamp шрифта работает, DOM-инъекции нет. Валидация пережила 5 правок head.html раунда без регрессий. Дефолты возвращены.
- Правок нет. Контракт-тест PENDING. Ре-сканы: R0.76 ✓, R0.83 ✓, R0.78 ✓.

### R1.03 (2026-07-12) — Сетевой аудит статики: VERIFIED-CLEAN (0 правок)

- 47 запросов на 3 страницах (холодная загрузка): 0 не-200; CDN exact-pin+SRI ×3; условная загрузка бандлов правильная (mermaid только /, chart+stats.js только /stats); v-пины консистентны. Наблюдение: все 8 шрифт-семейств грузятся сразу — осознанная плата за мгновенный дизайн-свитч, не трогаю.
- Правок нет. Контракт-тест PENDING.

### R1.04 — WCAG 1.4.12 Text Spacing: live-аудит трёх страниц — verified-clean (2026-07-12)

Инъекция стандартного 1.4.12-оверрайда (lh 1.5 / ls 0.12em / ws 0.16em / p-margin 2em) + скан
clip-y / clip-x / vp-overflow / hscroll на живых `/`, `/stats`, `/settings` (все 3 вкладки).
Реальных обрезок: **0**. Все срабатывания сканера — намеренные sr-only-клипы
(`caption.visually-hidden` в /stats, `h2.settings-panel-title` под js-tabs в /settings,
base.css:1358) — контент невидим, 1.4.12 неприменим. Методика дополнена фильтром
visually-hidden/sr-only. Кодовых правок нет; леджер cr.100. localStorage возвращён к дефолтам.

### R1.05 — Регрессион-рескан R0.90/R0.93/R0.94 — verified-clean (2026-07-12)

CSSOM-скан served base.css v=90 (по selectorText): print-блок 11/11 селекторов,
forced-colors 5/5 правил. Функциональный тест R0.94 на /stats @375px: клип 1×1 →
фокус → 134×246 clip:auto (кнопка видима) → blur → клип восстановлен. Три последних
фикс-сета без дрейфа. Кодовых правок нет; леджер cr.101. Window-tick (контракт-тесты +
regenerate-removal-plan) снова отложен — :8080 жив (200).

### R1.06 — Структура документа (заголовки + landmarks) — verified-clean (2026-07-12)

DOMParser-скан SSR HTML четырёх GET-страниц: один h1 везде, 0 пропусков уровней,
banner/main уникальны, nav с aria-label; skip-link вне landmarks — канон; contentinfo=0 —
отложенное решение §5 (футер). /focus-training 404 ожидаем (шаблон = view главной).
error.html: два nav различимы по aria-label. Кодовых правок нет; леджер cr.102.
Window-tick снова отложен — :8080 жив (200).

### R1.07 — WCAG 1.4.11 Non-text Contrast (editorial, обе темы) — verified-clean (2026-07-12)

Индикаторы состояния checked-опции (бейдж, рамка, дельта checked/unchecked) и фокус-кольца
на живой `/`: dark 4.24–13.05, light 3.85–17.5 — все ≥3:1. Обоснованный не-дефект: рамка
покоя карточки/кнопки 1.37 (контур hit-области при опознании компонента текстом/бейджем —
вне требований 1.4.11). Tooling-урок №6: transition-цвета инвалидируют rAF-замеры после
click — ждать ≥ transition-duration; диагностика диффом против соседнего unchecked-элемента.
Кодовых правок нет; леджер cr.103. Window-tick снова отложен — :8080 жив.

### R1.08 — WCAG 4.1.2 accessible names (4 страницы, 434 элемента) — verified-clean (2026-07-12)

Скан пустых accname / title-only / placeholder-only / focusable-in-aria-hidden /
positive-tabindex: / (18), /stats (347), /settings (58, все 3 вкладки), 404 (11) —
0 проблем везде. Иконочные кнопки последовательно с aria-label. Кодовых правок нет;
леджер cr.104. Window-tick снова отложен — :8080 жив (200).

### R1.09 — WCAG 1.4.13 hover-контент + 1.1.1 canvas (/stats) — verified-clean (2026-07-12)

Единственный авторский hover-reveal (кнопка копирования кода) имеет :focus-within-путь.
1608 нативных title — exempt из 1.4.13, вся информация доступно избыточна (видимый текст /
ячейка «Прогресс»). Канвасы Chart.js: role=img + aria-label → таблица как альтернатива.
Кодовых правок нет; леджер cr.105. Window-tick снова отложен — :8080 жив (200).

### R1.10 — Устойчивость к lockdown localStorage — verified-clean (2026-07-12)

Статический аудит: 0 незащищённых обращений к storage (head.html IIFE — 7 осей с
фоллбэками; app.js — 4 подсистемы в try/catch). Живая симуляция SecurityError на
/settings: вкладки, design-свитч, тумблер темы работают без uncaught-ошибок, персист
молча пропускается. Урок №7: три кнопки шапки делят класс ed-theme-toggle — целиться
по .theme-toggle/aria-label. Кодовых правок нет; cr.106. Window-tick — :8080 жив.

### R1.11 — Формы: 3.2.2 + constraint-валидация — verified-clean (2026-07-12)

Два submit-вызова в JS — оба явные (клик/модалка), 0 на change/input; novalidate нигде;
/settings без свободного ввода (ошибки предотвращены конструированием); required-radio
MCQ-формы гейтит пустой сабмит с нативным русским сообщением. Кодовых правок нет;
cr.107. Window-tick — :8080 жив; чужой WIP (owasp-сидер + docs/mcq-quality) не тронут.

### R1.12 — Печать /stats: скрыта фильтр-карточка (base.css v=91) (2026-07-12)

Инвентаризация @media print (17 правил) + DOM-сверка: на /stats печатались 7 контролов
фильтр/поиск-карточки — добавлен `.stats-filter-card` в print hide-list, v-bump 90→91,
live-sync cp в bootRun, CSSOM-верификация (хром 0, контент печатается). /settings —
осознанно без правок (tablist = печатный контекст при sr-only заголовке панели).
Леджер cr.108. Window-tick — :8080 жив.

### R1.13 — Целостность ID/ARIA-ссылок (4 страницы + вкладки) — verified-clean (2026-07-12)

0 дублей id, 0 висячих aria-labelledby/describedby/controls/activedescendant/details/
errormessage, 0 label[for]-сирот, td[headers] чисты — SSR всех страниц + живые
состояния /settings по вкладкам. kbd-оверлей покрыт R0.76/R0.83. Кодовых правок нет;
cr.109. Window-tick — :8080 жив.

### R1.14 — Мета-гигиена + bfcache/кэш — verified-clean (2026-07-12)

Титулы уникальны (2.4.2), zoom не заблокирован, theme-color адаптивный (JS head),
meta refresh нет, 0 unload-хендлеров; HTML no-store — осознанно (сессионное состояние),
статика max-age=86400+must-revalidate при v-URL. immutable и description — наблюдения
без правок (бэкенд/не-цель). Кодовых правок нет; cr.110. Window-tick — :8080 жив.

### R1.15 — Эргономика ввода + допроверка #count — verified-clean (2026-07-12)

Два свободных поля в проекте: /stats поиск (search+enterkeyhint+off-тройка — образцово),
/settings #count (number, min=1 max=200, живой тест: 999/0/-5 отклоняются с русскими
сообщениями). Коррекция R1.11 «number-инпутов ноль» (visible-фильтр пропустил скрытый
при TRAINING счётчик) — cr.107 аннотирован; инвентаризацию контролов вести по SSR-DOM.
Кодовых правок нет; cr.111. Window-tick — :8080 жив.

### R1.16 — Якоря/sticky-перекрытие/back-to-top — verified-clean (2026-07-12)

Masthead везде relative → перекрытие целей невозможно; skip → #main-content tabindex=-1
работает; sticky th /stats вертикально инертны (overflow-x-контекст); back-to-top
направленный + фокус на MAIN после активации. Кодовых правок нет; cr.112.
Window-tick — :8080 жив.

### R1.17 — 1.4.11 на полярных палитрах swiss/claude — verified-clean (2026-07-12)

4 комбинации (swiss/claude × dark/light): индикаторы checked и фокус-кольца 4.27–19.8,
все ≥3:1. Нота: дельта рамок swiss light 2.53 — supplementary (бейдж 18:1 = основной
индикатор). Покрытие 1.4.11: 3/11 дизайнов (дефолт + полярные), остальные — token-only.
Кодовых правок нет; cr.113. Window-tick — :8080 жив; чужой WIP (test-strategies-сидер)
не тронут.

### R1.18 — ARIA-грамматика (роли/контексты/свойства) — verified-clean (2026-07-12)

0 неизвестных ролей; tab/tabpanel/radio/radiogroup со всеми обязательными свойствами
и контекстами; имена групп через labelledby; живьём во всех 6 radiogroup ровно один
aria-checked=true (SSR честно отдаёт false — состояние клиентское). Кодовых правок
нет; cr.114. Window-tick — :8080 жив; чужой WIP (test-strategies) не тронут.

### R1.19 — dvh + fixed-геометрия mobile — verified-clean (2026-07-12)

Все 5 vh-употреблений уже 100dvh (legacy 100vh нет); оверлеи inset:0 со скроллом внутри;
viewport-fit=cover не используется → env() не нужен; back-to-top 44×44/24px на 360px,
без интерактива под ним. Кодовых правок нет; cr.115. Window-tick — :8080 жив.

### R1.20 — Перф-baseline /stats — verified-clean (2026-07-12)

TTFB 98ms, HTML 41KB gzip, DCL 174ms/load 177ms, 0 long tasks, статика из кэша;
DOM 5368 узлов — обоснован (319 строк, tr[hidden] вне лейаута). Lighthouse NO_FCP
в фоновой вкладке (урок №5 расширен) → paint-метрики (FCP/LCP/CLS) — tooling-gated,
до живой сессии с Chrome на переднем плане. Кодовых правок нет; cr.116.
Window-tick — :8080 жив.

### R1.21 — Браузерный размер шрифта (rem-цепочка) — verified-clean (2026-07-12)

Токены rem-based; fontScale = root font-size в % от браузерного дефолта (перемножение,
при 1 — чистый дефолт); живой тест 16→24px: контент ×1.5, fluid-заголовок шапки ×1.19
(clamp с rem-полом — осознанно, растёт всегда). Кодовых правок нет; cr.117.
Window-tick — :8080 жив.

### R1.22 — ROLLUP окна R1.04–R1.21 (2026-07-12)

**Итог окна: 18 тиков, 17 размерностей закрыто verified-clean, 1 кодовый фикс, 2 коррекции методики.**

| # | Размерность | Вердикт |
|---|---|---|
| R1.04 | WCAG 1.4.12 Text Spacing (3 страницы) | чисто (sr-only-клипы — не дефекты) |
| R1.05 | Регрессии R0.90/R0.93/R0.94 | без дрейфа |
| R1.06 | Структура: заголовки + landmarks | чисто |
| R1.07 | 1.4.11 non-text contrast (editorial ×2 темы) | чисто, 4.24–17.5 |
| R1.08 | 4.1.2 accessible names (434 элемента) | чисто |
| R1.09 | 1.4.13 hover-контент + 1.1.1 canvas | чисто (1 hover-reveal с focus-путём) |
| R1.10 | lockdown localStorage | чисто (0 незащищённых) |
| R1.11 | Формы 3.2.2 + валидация | чисто (+коррекция в R1.15) |
| R1.12 | **Печать /stats — ФИКС** `.stats-filter-card` | **base.css v=91** |
| R1.13 | Целостность ID/ARIA-ссылок | чисто |
| R1.14 | Мета-гигиена + bfcache/кэш | чисто |
| R1.15 | Эргономика ввода + #count | чисто (cr.107 аннотирован) |
| R1.16 | Якоря/sticky/back-to-top | чисто |
| R1.17 | 1.4.11 на swiss+claude ×2 темы | чисто, 4.27–19.8 |
| R1.18 | ARIA-грамматика | чисто |
| R1.19 | dvh + fixed-геометрия mobile | чисто |
| R1.20 | Перф-baseline /stats | чисто (TTFB 98ms, 0 long tasks) |
| R1.21 | rem-цепочка / браузерный шрифт | чисто |

**Tooling-уроки окна** (полный список №1–№5 — в R0.99):
- **№6**: transition на цветах инвалидирует rAF-замеры состояний — ждать ≥ transition-duration
  (400ms+) или transitionend; диагностика — дифф computed против соседнего элемента в другом
  состоянии (R1.07).
- **№7**: три кнопки шапки делят класс ed-theme-toggle — целиться по уникальному .theme-toggle
  или aria-label (R1.10).
- **№5-расширение**: фоновая вкладка не пейнтит → Lighthouse даёт NO_FCP; в фоне — только
  paint-независимые Performance API (R1.20). Инвентаризацию контролов вести по SSR-DOM,
  visible-фильтр только для видимых состояний (R1.15); зонды таблиц — из видимых строк,
  tr[hidden] дают мусорную геометрию (R1.16).

**Открытые гейты (без изменений):**
1. **Window-tick (приоритет №1 при свободном :8080)**: контракт-тесты
   (TemplateFragmentContractTest, InterviewMvcControllerTest) — 6 правок head.html +
   base.css v87→91 не прогонялись; затем regenerate-removal-plan.md (re-grep строк!).
2. **Session-gated (Phase F, ждёт решения по EXAM)**: summary/result parity, grade-flow,
   post-answer JS, related-rail, no-JS /answer.
3. **Data-gated**: instrument-палитра чартов (нужны строки daily_activity).
4. **Tooling-gated**: эмуляция prefers-reduced-motion/contrast/forced-colors (MCP emulate
   только colorScheme); Lighthouse paint-метрики (Chrome на переднем плане).
5. **User-gated (§5/§G — не переоткрывать)**: Chart.js self-host, favorite, analytics CTA,
   футер, вертикальный tablist, card-grid осей, ?-кнопка.

**Состояние: v=91 (tokens v=62), леджер cr.100–cr.117, коммиты 46d4002d…a5c493d0.**
Дальше: регрессион-ресканы окна R1.x по мере старения, углубление 1.4.11/1.4.12 на
редких дизайнах при жалобах, window-tick при первом освобождении порта.

### R1.23 — 1.4.11 на linear + theverge — verified-clean (2026-07-12)

4 комбинации: индикаторы 4.9–19.93, кольца 4.55–7.2, все ≥3:1. Урок №6 уточнён:
linear-transition > 450ms → ждать 800ms + sanity-гейт settled в скрипте. Покрытие
1.4.11: 5/11 дизайнов (все структурные + края палитр). Кодовых правок нет; cr.118.
Window-tick — :8080 жив.

### R1.24 — Сортировка /stats: aria-sort цикл — verified-clean (2026-07-12)

5 сортируемых th (tabindex=0): none→asc→desc с реальной пересортировкой, эксклюзивность
aria-sort, Enter+Space работают, live-объявление «Таблица отсортирована: …» на русском.
Паттерн эквивалентен APG (не переделывать на button). Кодовых правок нет; cr.119.
Window-tick — :8080 жив.

### R1.25 — Disclosure таблицы /stats — verified-clean (2026-07-12)

aria-expanded/controls каноничны, текст кнопки с объёмом скрытого, 12↔319 строк,
фокус на кнопке при обоих переходах; live не нужен (aria-expanded озвучивается).
Кодовых правок нет; cr.120. Window-tick — :8080 жив.

### R1.26 — /stats search-landmark добавлен (stats.html) (2026-07-12)

Форма поиска получила role="search" + aria-label, форма фильтров — aria-label
(именованный form-landmark); верифицировано живьём. GET-идемпотентность и labels
подтверждены; print-регрессия R1.12 чиста (v=91). В window-tick добавилась 1 правка
шаблона. Леджер cr.121.

### R1.27 — Zero-states и семантика фильтров /stats — чисто + открытый вопрос (2026-07-12)

Zero-state поиска образцовый (цитата+сброс+сохранение ввода); фильтры скоупят
stat-карточки, таблица тем намеренно полная. ОТКРЫТЫЙ ВОПРОС пользователю: поиск
игнорирует topic-фильтр (глобальный by design или потерян scope?) — UI-подпись
после подтверждения. onlyWrong/important — data-gated. Кодовых правок нет; cr.122.
Window-tick — :8080 жив.

### R1.28 — WCAG 1.4.1 Use of Color — verified-clean (структурно) (2026-07-12)

Ни один индикатор не полагается на цвет: MCQ — буквенный бейдж; точность — число%
(+bold у low); зрелость — числовой скор+рамка; прогресс — текст «N/M»; графики —
role=img+таблица. Верификация по template+CSS (значения data-gated). Кодовых правок
нет; cr.123. Живая перепроверка с данными — data-gated. Window-tick — :8080 жив.

### R1.29 — Link Purpose 2.4.4 + безопасность ссылок — verified-clean (2026-07-12)

347 ссылок на 4 страницах: 0 target=_blank (noopener не нужен), 0 мёртвых href,
0 пустых имён, 0 дублей текст→разный-адрес, 0 реально неоднозначных (3 ложных —
techterm-префиксы Link/Click в именах тем). Методзаметка: regex неоднозначности
→ границы слова, не startsWith. Кодовых правок нет; cr.124. Window-tick — :8080 жив.

### R1.30 — Language 3.1.1/3.1.2 — verified-clean (2026-07-12)

html lang="ru" на всех страницах (3.1.1 ✓); англ. контент = имена тем + техтермины +
код → исключения 3.1.2, разметка lang="en" не нужна (3.1.2 ✓). Forward-нота: session-
gated разборы перепроверить на цельные англ. предложения (код/термины exempt). Кодовых
правок нет; cr.125. Window-tick — :8080 жив.

### R1.31 — Consistent Navigation/Identification 3.2.3/3.2.4 — verified-clean (2026-07-12)

Nav (3 ссылки) идентичны по тексту/href/порядку + корректный aria-current; тумблеры
и back-to-top одинаковы. Осознанный не-дефект: skip-link текст контекстен по странице
(вопрос/аналитика/настройки) при общем href — специфичность полезнее генерика, 3.2.4
не нарушен. Кодовых правок нет; cr.126. Window-tick — :8080 жив.

### R1.32 — Label in Name 2.5.3 — verified-clean (2026-07-12)

MCQ-опции: aria = «Вариант N: X. »+видимый текст (содержится ✓); кнопки /stats,/settings
— видимый текст подстрока имени ✓; «×»→«Закрыть» — символьный глиф, вне 2.5.3.
Урок №8: containment сравнивать на живом DOM (SSR-DOMParser врёт на инлайн-глифах →).
Кодовых правок нет; cr.127. Window-tick — :8080 жив.

### R1.33 — Info & Relationships 1.3.1 (таблицы/списки) — verified-clean (2026-07-13)

Таблица образцова (caption + scope col×6 + scope row×319); stat-карточки доступны по
порядку чтения (label+value+title); accuracy-bar декоративный aria-hidden; псевдо-
заголовков нет. Отложенный P4-кандидат: stat div→dl (не барьер, общий фрагмент, dd-margin
риск, contract-test заблокирован) — для window-tick. Кодовых правок нет; cr.128.


### R1.34 — WCAG 4.1.3 Status Messages (verified-clean)

Комплексный аудит всех динамических регионов-оповещений (`aria-live` / `role=status` / `role=alert`) на `/`, `/stats`, `/settings`: SSR-инвентарь + живая проверка, что обновления реально проходят через регионы.

**Итог — чисто, правок нет.** Разделение срочности корректно (`alert`/assertive у ошибок и no-JS фоллбэка, `status`/polite у несрочных); `aria-atomic=true` на полностью перезаписываемых регионах (вердикт, сортировка, хинты, размер шрифта); ни один не фокусируемый (не крадёт фокус); скрытые пусты на старте (нет ложных объявлений при загрузке).

**Живая динамика:** `A+` → `.font-scale-value` 100%→110% через role=status/polite (озвучится); сортировка подтверждена в R1.24. Дефолты восстановлены. Детали — cr.129.


### R1.35 — WCAG 1.4.12 Text Spacing (verified-clean)

Инжектирован стандартный text-spacing override (line-height 1.5 / letter-spacing 0.12em / word-spacing 0.16em / para 2em) на `/`, `/stats`, `/settings`; проверена потеря контента через оверфлоу-скан.

**Итог — чисто.** Горизонтального оверфлоу нет ни на одной странице; обрезанных текстовых узлов нет. Широкая таблица тем скроллится (`.topic-table-wrap` overflow-x:auto), а не клиппится. Единственные флаги — легитимные visually-hidden узлы (`caption`, `h2.settings-panel-title` — 1px clip). Сегмент-контролы подписи не режут. Правок кода нет. Детали — cr.130.


### R1.36 — WCAG 2.5.8 Target Size (Minimum, AA) (verified-clean)

Живой замер bounding-box всех интерактивных целей на `/`, `/stats`, `/settings`; флаг <24×24 CSS px с разбором исключений 2.5.8.

**Итог — чисто.** Все суб-24 флаги легитимны: inline-ссылки тем (line-height-исключение), чекбоксы/radio обёрнуты в `<label>` 44px (или sr-only 1×1 + label-карточка 126/156px) → эффективная хит-зона ≥24. Иконочных кнопок и сегмент-контролов <24 нет. Правок кода нет. Детали — cr.131.


### R1.37 — WCAG 2.1.4 Character Key Shortcuts (OPEN — реальная находка, decision-gated)

**Первая реальная находка за серию тиков** (не verified-clean). Глобальные печатные одиночные шорткаты `1–9` (выбор MCQ, `app.js` L1215) и `?` (справка, L1917) привязаны к `document`, активны при фокусе на любом нетекстовом элементе, без механизма off/remap → нарушение 2.1.4 (Level A). Замером подтверждено: фокус на header-ссылке + `2` → выделяется вариант.

Text-input guard и гашение при открытой справке — на месте, но 2.1.4 требует off / remap / focus-scope. При загрузке фокус на `body` → `1–9` работают немедленно (осознанная power-user фича), поэтому фикс имеет UX-tradeoff.

**Ремедиация decision-gated** (см. cr.132): A) тумблер вкл/выкл — строго по спеке, но новая ось персонализации (§5-территория); B) привязка к фокусу формы — регрессирует немедленный ввод; C) autofocus 1-й опции + привязка к фокусу — рекомендуемый компромисс. Правок не вносил (сердцевина answer-flow + нужен прогон MVC/JS-тестов, заблокирован живым bootRun). **Ожидает решения пользователя.**


### R1.38 — WCAG 1.4.10 Reflow (verified-clean)

Эмуляция viewport 320×800 на `/`, `/stats`, `/settings`; скан горизонтального оверфлоу документа + элементов, выламывающихся из viewport вне скролл-контейнеров.

**Итог — чисто.** На всех трёх `docOverflow=0`, offenders=0. Таблица тем в `.card.overflow-x-auto` (схлопывается на 320px, 2D-исключение); сегмент-контролы `flex-wrap:wrap`. Контент reflow-ится в одну колонку без 2D-скролла. Правок кода нет. Детали — cr.133.


### R1.39 — WCAG 1.4.11 Non-text Contrast (частичный — actionable lead по focus-ring)

Canvas-замер контраста границ/заливок/focus-индикаторов на `/settings` (light). Focus мерен реальным Tab.

**Итог смешанный.** Разделил на:
- **Осознанный дизайн (не дефект, не трогать):** hairline-границы инпутов/кнопок (~1.48) и accent-заливка primary/seg-active (~2.69) — editorial-язык + брендовый акцент (дизайн-токены, решения пользователя).
- **Actionable lead:** focus-индикаторы неоднородны — панель использует сильный outline (3.85, проходит), а `.settings-tab` перекрывает его слабым 40%-альфа accent box-shadow-рингом (~1.42, не проходит). Внутренняя несогласованность, чинибельна.

**Правок нет** — нужна полная энумерация focus-рингов по всем типам контролов в обеих темах + адресный фикс (focus-токены, test-verify — заблокировано живым bootRun). Продолжение след. тик. Детали — cr.134.


### R1.40 — WCAG 1.4.11/2.4.11 focus-ring: энумерация завершена (R1.39 закрыт как OPEN decision-gated)

Полная карта focus-индикаторов по 11 дизайнам (CSS-правила + токены). Два паттерна: solid double-ring (сильный, linear +~8 дизайнов) и альфа-ринг rgba(accent,0.30–0.45) (editorial-дефолт +ряд).

**Реальный узкий a11y-gap:** дефолтный editorial в нормальном режиме — глобальный outline #C6613F ≈2.69:1 к телу страницы (маргинал), а box-shadow-only контролы (settings-tab/seg-btn) берут `--shadow-focus` rgba(198,97,63,0.40) ≈1.4:1. HC/forced-colors/prefers-contrast уже покрыты автором (system Highlight). Пробел — нормальный режим дефолта.

**Ремедиация decision-gated** (см. cr.135): editorial `--shadow-focus` → solid double-ring (паттерн уже в файле у linear/др.) и/или затемнить editorial `--color-border-focus` до ≥3:1. Правка дизайн-токенов → гейт design-token-audit + обе темы + тесты; затрагивает editorial-идентичность. **Ожидает решения пользователя** (аналогично R1.37). Правок не вносил.


### R1.41 — WCAG 1.3.1 / 2.4.6 Heading & Landmark structure (verified-clean)

fetch+DOMParser outline `h1-h6` + landmark-инвентарь по `/`, `/stats`, `/settings`.

**Итог — чисто.** Ровно 1 h1 на страницу; пропусков уровней нет (корректная h2→h3 вложенность на /settings); пустых/фейковых `role=heading` нет — всё на реальных `<h*>`. Landmarks: единичные main+header, помеченный nav, `<aside aria-label>` на /, `role=search` + `role=region` на /stats. Правок кода нет. Детали — cr.136.


### R1.42 — WCAG 2.3.3 prefers-reduced-motion (verified-clean)

Аудит motion-kill по CSS + JS. Три-состоянная ось «Движение» (auto/on/off) консистентна во всех слоях: CSS универсальный kill (media + [data-motion=off]); JS smooth-scroll (app.js 104-108) и Chart.js canvas (stats.js motionAllowed→anim) гейтятся той же моделью — обе JS-анимации (вне досягаемости CSS) обработаны вручную. Правок кода нет. Детали — cr.137.


### R1.43 — ФИКС editorial focus-ring → solid double-ring (WCAG 1.4.11/2.4.11) [R1.40 закрыт]

Реализовано решение пользователя. `tokens.css`: editorial `--shadow-focus` (light+dark+mirror) с alpha-ring на solid double-ring (2px bg-gap + 4px --color-border-focus), паттерн linear/др. `--color-border-focus` не менял — Ember 3.85:1 / Clay 4.8:1 уже ≥3:1. design-token-audit CLEAN. box-shadow-only контролы теперь ≥3:1 вместо ~1.4:1. Non-default alpha-дизайны — follow-up. Детали — cr.138.

### R1.44 — ФИКС character-key shortcuts scope + autofocus (WCAG 2.1.4) [R1.37 закрыт]

Реализован вариант C. `app.js`: `1–9` гейтится `form.contains(activeElement)` (2.1.4c active-on-focus); autofocus 1-й опции `{preventScroll}` при загрузке — шорткаты работают сразу. `?` оставлен глобальным (benign help-toggle). node --check OK, MVC+contract тесты GREEN. Live keyboard-проверка — при инстансе. Тест-долг (head/base/stats предыдущих тиков) закрыт этим же прогоном. Детали — cr.139.


### R1.45 — R1.43+R1.44 LIVE-VERIFIED (оба фикса закрыты полностью)

Поднял postgres+bootRun, проверил в живом браузере (обе темы). **R1.44:** autofocus сажает фокус на 1-ю опцию; `3` в форме → опция 3, `1` вне формы → без изменений (2.1.4c). **R1.43:** `--shadow-focus` solid double-ring — Ember 3.85:1 (light) / Clay 4.8:1 (dark), было ~1.4:1. Оба ≥3:1. Полный цикл DONE (код+аудит+тесты+live). Детали — cr.140.


### R1.46 — WCAG 2.4.7 Focus Visible (verified-clean, + R1.43 регресс-чек)

Статический разбор `outline:none` в base.css + live-обход реальным Tab (8 сэмплов). Все `outline:none` — либо мышиные (`:not(:focus-visible)`/`:focus-within`), либо контейнер #main-content, либо с box-shadow-заменой. Live: 8 контролов подряд, все с индикатором — стандартные 2px solid outline, settings-tab рендерит solid double-ring (R1.43 подтверждён: непрозрачный Clay-ринг). Правок кода нет. Детали — cr.141.

### R1.47 — WCAG 2.4.11 Focus Not Obscured (Minimum, AA) (verified-clean)

Аудит sticky/fixed-оверлеев на предмет полного перекрытия клавиатурного фокуса. Единственный sticky-top кандидат — thead таблицы тем на `/stats`. Замер в живом браузере: его sticky-контейнер = `div.topic-table-wrap` (overflow-x:auto → overflow-y вычислен в auto), но без фикс-высоты обёртка не скроллит вертикально (scrollHeight===clientHeight) → `top:0` вертикально инертен → thead уезжает со скроллом (thTop линейно 1581→−5690, никогда не приклеивается к верху вьюпорта) → не может стать оверлеем поверх фокуса. Sticky-сайдбары — боковые колонки, не накрывают фокусируемое. site-header осознанно НЕ sticky (base.css:497). Fixed-оверлеев нет. Итог: ни один контрол при фокусе не скрыт целиком; scroll-padding не нужен. Правок кода нет. Детали — cr.142.

### R1.48 — ФИКС alpha focus-ring → solid double-ring на 5 non-default дизайнах (WCAG 1.4.11) [R1.43 follow-up закрыт]

Закрыт follow-up из R1.43. canvas-sRGB замер 10 остаточных alpha-`--shadow-focus` (notion/superhuman/stripe/claude/instrument ×2 темы): solid outline (`--color-border-focus` vs bg) проходит везде ≥3:1 (4.03–9.42), а alpha box-shadow ring на box-shadow-only контролах (settings-tab/seg-btn/inputs) ПРОВАЛИВАЕТ везде (1.48–2.61:1) — тот же дефект, что editorial до R1.43. Фикс: все 10 alpha-колец → solid double-ring `2px bg + 4px border-focus` (внешнее кольцо = border-focus, уже ≥3:1). Теперь все 23 `--shadow-focus` solid, 0 alpha. design-token-audit CLEAN; live обе темы на /settings (notion/superhuman/stripe/claude) подтвердили резолв в double-ring. head.html tokens.css v=64. gradle пропущен (CSS-only при живом bootRun). Детали — cr.143.

### R1.49 — WCAG 1.4.13 Content on Hover or Focus (verified-clean)

Аудит контента, показываемого по hover/focus. Кастомных тултипов/попоперов НЕТ (role=tooltip/data-tooltip/JS-hover/attr()-псевдо — все 0). Нативные `title=` (favorite/regenerate/th) — UA-рендер, exempt из 1.4.13. `aria-describedby` → всегда-видимые инлайн-подсказки `*-hint`, не триггерные. `content: attr(data-label)` → стек-лейбл адаптивной таблицы. Единственный hover/focus-reveal — `.code-copy-btn`: hoverable (кнопка-потомок триггера `.code-copy-wrap` → hover сохраняется) и persistent (нет автоскрытия) выполнены СТРУКТУРНО; dismissable — action-аффорданс в паддинг-гаттере pre, стандартный reveal-on-hover copy-паттерн. Правок кода нет. Эмпирика по copy-btn ограничена (live код-блок только на /result через запрещённый POST); вердикт на структурном CSS-анализе. Детали — cr.144.

### R1.50 — WCAG 2.4.3 Focus Order при CSS-reflow (verified-clean)

Аудит всех grid-reflow мест на предмет разрыва смысла/операбельности tab-порядка (фокус идёт по DOM, не по визуалу). result/summary/stats/focus-split — DOM-порядок совпадает с порядком чтения (левая колонка → правая), явные grid-row монотонны. Header (`display:contents` + `grid-template-areas "brand toggles"/"nav nav"` при ≤1239px): замер на 900px показал фокус brand→nav(вниз)→toggles(вверх) — групповое расхождение визуал/фокус. Оценено как НЕ провал 2.4.3: операбельность (все достижимо) и смысл (brand→nav→toggles связно, внутри групп порядок совпадает) сохранены; C27 sufficient-не-required, F44 неприменим (нет tabindex). Правка нежелательна: нет DOM-порядка под ОБА брейкпоинта — текущий точно совпадает с десктопом (≥1240px один ряд) и связен на планшете; перестановка сломала бы десктоп. Правок кода нет. Детали — cr.145.

### R1.51 — WCAG 2.5.3 Label in Name: аудит + ФИКС декоративного «×»

Живой замер accessible-name всех контролов с видимым текстом на /settings+/stats (+структурно /). Все проходят «accName содержит visibleText»: font-step A−/A+, Экспорт JSON/CSV, Применить фильтры, Искать, flashcard-grade, nav-ссылки. Единственная находка — close-кнопка kbd-help-модалки: видимый глиф `×` (U+00D7) не помечен декоративным при aria-label="Закрыть" (строго не провал — символьный глиф не текст-надпись, но расходится с конвенцией иконок проекта + ложный флаг + риск двойного озвучивания). ФИКС: `×` → `<span aria-hidden="true">×</span>` в app.js:1878 и stats.js:340. Теперь icon-only, вне scope. node --check OK; live /stats re-scan violations=[], closeBtn spanHidden=true. app.js v=62 (×3), stats.js v=15. gradle пропущен (JS+version-bump при живом bootRun). Детали — cr.146.

### R1.52 — WCAG 4.1.3 Status Messages (verified-clean)

Аудит программной определяемости статус-сообщений (role=status/alert/log|aria-live) + живой DOM-скан /stats и /settings. Все динамические сценарии покрыты live-регионом: export (#export-status), copy-code, table-sort (#table-sort-status atomic), settings-saved (#personalization-status atomic, announceSaved), font-scale (#font-scale-value), AJAX-ошибка (#interview-alert с динамическим status/polite↔alert/assertive по severity), result-feedback/confidence/extra-analysis, chart-fallback, filter-mode-hint. Не-gap'ы: формы /stats (фильтр+поиск) = GET-reload (навигация, вне scope); streak-виджет намеренно без aria-live (C29, антишум). Live-верификация: /settings 4 региона все isLive, statusLikeWithoutLiveRegion=[]. Правок кода нет. Post-answer регионы подтверждены статически (POST запрещён границами). Детали — cr.147.

### R1.53 — WCAG 2.5.8 Target Size (Minimum, AA/2.2) (verified-clean)

Живой замер всех интерактивных целей на /settings (23), /stats (37), / (13) + CSS-проверка /result (POST-gated). violations=[] везде. «Маленькие» (<24px) элементы все под исключениями: нативные чекбоксы/радио 18×18 (spacing center-dist 52–137px + UA-исключение); визуально-скрытые радио-инпуты 1×1 на / с реальной целью — label 960×126/156px; topic-ссылки высотой 17px (inline-in-table + spacing 56–61px). Ни один author-стилизованный контрол (button/seg/tab/nav/toggle/close/next) не <24px — большинство ≥44px; /result favorite/regenerate = min 44×44 (base.css 2427). Правок кода нет. Детали — cr.148.

### R1.54 — WCAG 1.3.1 Info & Relationships: формы/группы/таблица (verified-clean)

Живой DOM-аудит программных связей на /settings, /stats, /. /settings: 9 form-контролов все с меткой (wrap-label), 7 групп все с групповой меткой (aria-labelledby). /stats: 5 контролов с меткой + образцовая дата-таблица (caption «Статистика по темам», 325 th ВСЕ со scope col+row → 319 row-header, 5 сортируемых колонок все с aria-sort). /: radiogroup #interview-options (role+aria-label+aria-describedby), 4 радио одной name-группы, все с меткой. controlsUnlabelled/groupsUnlabelled=[] везде. /result (POST-gated) — опции role=list/listitem (display-only, корректно). Правок кода нет. Детали — cr.149.

### R1.55 — WCAG 3.3.1 Error Identification / 3.3.3 Error Suggestion (verified-clean)

Аудит обработки ошибок форм. Единственное поле с ограничениями — count (type=number min=1 max=200, без required/кастом-валидации; JS-валидации в проекте нет). Путь — нативная HTML5 Constraint Validation (форма без novalidate). Эмпирика через Constraint Validation API (read-only, значение сброшено): 500→«меньше или равно 200», 0→«больше или равно 1», 15.5→«Ближайшие допустимые значения: 15 и 16», 20→valid. Сообщения локализованы, идентифицируют ошибку (3.3.1) + предлагают исправление/границу/ближайшие валидные (3.3.3); браузер блокирует submit и озвучивает через AT. Прочие формы (селекты/поиск/чекбоксы) невалидных состояний не имеют. Правок кода нет. Детали — cr.150.

### R1.56 — WCAG 3.2.2 On Input (verified-clean)

Инвентарь всех change-хендлеров + разбор. Единственный кандидат на смену контекста — авто-сабмит при выборе варианта в instantMode (app.js 1158) — но instantMode вечно false: контролы режимов удалены из шаблонов, onLearningPrefChange недостижим (app.js 5–11, «ловушка round-01 B7») → ветка мёртвая, реальный сабмит только явной кнопкой. Прочие: mode-select → показ/скрытие count (within-form, без фокуса/навигации, не «контекст»); toggleTopic → enable/disable поля; seg-контролы тема/дизайн → презентация (флип data-*, app.js:596, не контекст) + анонс aria-live; count → persist. Ни одной смены контекста на input. Правок кода нет. Мёртвая instant-ветка — код-гигиена (документирована), вне scope. Детали — cr.151.

### R1.57 — WCAG 1.4.10 Reflow @320px (verified-clean)

Эмуляция 320px + замер page-level overflow и вылезающих элементов на /, /stats, /settings (обе вкладки). Везде pageHorizontalOverflow=0, offenders=[]. /stats дата-таблица на мобиле переходит в стек-раскладку (td[data-label] grid), не горизонтальный скролл. /settings вкладка «Оформление»: design-seg-control (11 опций) через flex-wrap переносит опции на ряды, не вылезает (width 257 < vw 305). Все компоненты рефлоуятся корректно (таблица→стек, seg→flex-wrap), горизонтального скролла нет. Правок кода нет. /result+/session-summary POST-gated, при 320px не замерены (прежние аудиты чисты на 375/768/1280). Детали — cr.152.

### R1.58 — WCAG 1.4.12 Text Spacing (verified-clean)

Инъекция стандартного 1.4.12 override (line-height 1.5 / letter 0.12em / word 0.16em / para 2em, !important) на /, /stats, /settings + детекция clipping и проверка fixed-height компонентов. pageHorizontalOverflow=0 везде; все компоненты (sticky-th 43px, кнопки 44px, ячейки 53px, settings-tab 51px, seg-btn 42px, font-value 24px) overflowsBox=false; видимый текст не обрезан/перекрыт. Два «clipped» — намеренные visually-hidden SR-only заголовки (caption /stats + settings-panel-title под js-tabs, base.css 1358/1373), exempt (AT читает полный текст независимо от 1px-clip; без JS panel-title видимый и не клипается). Правок кода нет. /result+/session-summary POST-gated, не замерены. Детали — cr.153.

### R1.59 — WCAG 2.4.4 Link Purpose (In Context): аудит + polish декоративных «→»

Живой аудит a[href] на /, /settings, /stats. 2.4.4 чисто: 0 пустых/неоднозначных/коллизий/дубль-имён (/stats 327 ссылок, 319 topic с уникальными именами). a11y-polish: «→» в 9 CTA-лейблах был литеральным → попадал в доступное имя; обёрнут в <span aria-hidden="true"> →</span> в 6 шаблонах (today-widget/session-summary/stats ×2, error/focus-training/result). Причина: чистые доступные имена + консистентность + меньше AT-шума. Динамические title header (дизайн X→Y) не тронуты. Live: CTA accName="Начать повторение" (стрелка ушла), rawText сохранил «→». Шаблоны без version-bump; gradle пропущен (template-only + live-verified). Детали — cr.154.

### R1.60 — WCAG 1.4.4 Resize Text (AA): текст до 200% без потери контента — verified-clean

Эмпирический text-only resize (удвоение корневого font-size, chrome-devtools) на /, /settings, /stats. Всё текстовое содержимое масштабируется 2.0 (rem-based); h1 на clamp() растёт 1.39× (осознанный потолок, контент не теряется). Нет горизонтального скролла страницы при 200% (scrollW==clientW везде). Клиппинг: только 2 SR-only ложных (visually-hidden + settings-panel-title под js-tabs); ellipsis-обрезки 0; seg-control 0/6 переполнений (переносятся); широкая таблица тем скроллится в overflow-x:auto (санкц. WCAG-паттерн, аудит 2.4.11). Наблюдение (не правил): header icon-тогглы держат иконку ~13.3px при resize (font-size не наследует масштаб) — иконки≠текст, внутри 44px targets; font:inherit сменил бы вид шапки во всех дизайнах при отсутствии провала → спекулятивно. Код не менялся. Детали — cr.155.

### R1.61 — WCAG 3.2.1 On Focus (A): фокус не меняет контекст — verified-clean

Эмпирический перебор всех фокусируемых с .focus() и сравнением сигнатуры до/после (href, диалоги, кража фокуса, переключение tabpanel, DOM-мутация, раскрытие ответа/авто-checked на странице вопроса). Чисто везде: /settings 54 контрола (ARIA-вкладки на ручной активации — фокус не переключает панель), / 13, /stats 345, живая MCQ-страница (GET, БД не мутирует) 4 radio — фокус не раскрывает ответ/не авто-выбирает/не меняет вердикт. 4 ложных FOCUS_STOLEN на <label> объяснены (label не фокусируемы) и перепроверены на самих radio. Код не менялся. Детали — cr.156.

### R1.62 — WCAG 2.4.7 Focus Visible (AA): фикс индикатора фокуса нативных чекбоксов /stats

Эмпирический проход всех фокусируемых (focus({focusVisible:true}) + замер рендер-индикатора). /settings 40/40 чисто; /stats нашёл 2 из 37 без индикатора — нативные фильтр-чекбоксы important/onlyWrong. Первопричина: box-shadow не рендерится на appearance:auto checkbox/radio (у search-input на той же странице кольцо есть, у чекбокса нет), а .stats-page input:focus + :focus:not(:focus-visible) снимают outline; плюс явный box-shadow:none на .stats-page чекбоксах (base.css:1649). Фикс: html[data-design] input[type=checkbox]:focus-visible,input[type=radio]:focus-visible { outline:2px solid var(--color-border-focus); outline-offset:2px } — outline рисуется на нативных контролах надёжно, специфичность (0,3,2) бьёт .stats-page input:focus. base.css v=92. Live: оба чекбокса получили outline #D97757, полный проход /stats noIndicator=0. MCQ-радио не затронуты (visually-hidden, кольцо на label; скриншот — чистое одиночное кольцо, регрессий нет). Детали — cr.157.

### R1.63 — WCAG 2.1.1 Keyboard (A): MCQ-флоу операбелен с клавиатуры — verified-clean

Эмпирический прогон реальными клавишами (CDP press_key) на живой странице вопроса: «1»→опция A (submit включился), ArrowDown→опция B (выбор+фокус синхронно), «3»→опция C, Esc→снятие (checkedCount 0, submit disabled, фокус в форме). Автофокус опции[0] при загрузке (app.js:1240) → шорткаты сразу. «1-9» focus-gated (form.contains, app.js:1221) = соответствие 2.1.4 (R1.44). Enter-сабмит структурно: реальный <button type=submit> в form POST /answer, disabled пока нет выбора — не firing (мутация БД). Ложная тревога первого прогона (мой blur() увёл фокус на BODY, гвард корректно заблокировал цифру) объяснена. Код не менялся. Детали — cr.158.

### R1.64 — WCAG 4.1.2 Name/Role/Value (A): контролы /settings — verified-clean

Обход всех контролов трёх панелей + шапки с вычислением accessible-name/role/value. Чисто: 6 seg-radiogroup (role=radiogroup+имя, role=radio+имя, ровно один aria-checked, roving tabindex checked=0/прочие=-1); степпер шрифта (role=group+имя, кнопки с описательными aria-label, значение в role=status live-регионе, Сбросить aria-disabled при 100%); 9 нативных session-контролов (все именованы через label); 3 header-тоггла (icon-only, но aria-label с текущим значением). Наблюдение (не провал, не правил): theme-toggle несёт aria-pressed на 3-состояночном cycle-контроле (layout/design его опускают) — мелкая нестыковка, значение читается из имени, атрибут связан с app.js-логикой. Код не менялся. Детали — cr.159.

### R1.65 — WCAG 2.1.2 No Keyboard Trap (A): kbd-help оверлей — verified-clean

Эмпирический цикл (CDP press_key) на живой /: «?» с опенера «Фокус» → оверлей role=dialog aria-modal=true, фокус на «×», фон полностью inert+aria-hidden; Tab не утёк в фон (ловушка держит); Esc → закрытие + возврат фокуса на опенер (focusReturnedToOpener:true) + снятие inert. Соответствие 2.1.2: стандартный выход Esc (документирован в справке) + альт. выходы (клик/Enter по ×, повторное ?). Prompt-modal регенерации вне зоны (th:if=aiEnabled, AI выключен → нет в DOM). Roving tabindex вкладок (R1.64) исключает ловушку в tablist. Код не менялся. Детали — cr.160.

### R1.66 — WCAG 2.5.2 Pointer Cancellation (A): активация на up-event — verified-clean

Статический анализ обработчиков + read-only DOM. Ноль down-event активации (mousedown/pointerdown/touchstart/inline on*) во всём static JS и шаблонах; активация только через click-слушатели (15 app.js + 3 stats.js) + нативный submit. Живой DOM: 0 inline down-атрибутов, 0 draggable, все контролы нативные (A/BUTTON). Нативный click = up-event + отменяем уводом указателя = 2.5.2 конструктивно. Бонус 2.5.7 Dragging: нет draggable/pointermove-драга/range → drag-only функционала нет. Код не менялся. Детали — cr.161.

### R1.67 — WCAG 1.4.13 Content on Hover or Focus (AA): code-copy + title — verified-clean

CSS-анализ (появление на hover/focus 100% определяется CSS) + инвентаризация author hover/focus-контента (только code-copy кнопка + skip-link). Code-copy: Hoverable ✓ (кнопка внутри обёртки, :hover держится), Persistent ✓ (без таймера, на touch всегда видна 44×44), Dismissable ✓ по духу (угловой аффорданс в паддинге pre, перекрывает лишь правый край 1-й строки пока видима, восстановимо overflow-x скроллом/mouse-out). skip-link — сам фокусируемый элемент, вне зоны. Native title — UA-controlled, исключение 1.4.13. Ограничение: живой pre-код-блок не достижим через GET (первые ordered-вопросы 3 тем без codeSnippet, markdown-ответ POST-gated) → обскурация посчитана детерминированно из токенов. Код не менялся. Детали — cr.162.

### R1.68 — WCAG 1.4.5 Images of Text (AA): текст не картинкой — verified-clean

Греп шаблонов/CSS + скан живого DOM (/, /settings, /stats). Нет растровых источников: 0 <img> (шаблоны и DOM), 0 url()-фонов (только CSS-градиенты). SVG-иконки — path-only спрайт через <use>, 0 с <text> (декоративны, aria-hidden). Бренд «Cheat · Sheet», заголовки, подписи, кнопки «A−»/«A+» — реальный текст (масштабируется, R1.60). Chart.js canvas (2 шт.) — визуализация данных, оба role=img + описательный aria-label, полные данные реальным текстом в таблице тем (319 строк) → text-эквивалент есть, 1.4.5 удовлетворён. Код не менялся. Детали — cr.163.

### R1.69 — WCAG 3.3.2 Labels or Instructions (A): подписи форм — verified-clean

Обход контролов /settings (обе панели) + /stats с проверкой ВИДИМОЙ подписи. Оформление: 7 групп (6 radiogroup + степпер) все с видимыми подписями + 7 inline-подсказок. Сессия: 8 native-контролов все с wrapping-<label>. Поле count: в TRAINING скрыто (label display:none, вне 3.3.2), при EXAM показывается с видимой подписью «Вопросов:» (замерено переключением mode, восстановлено), min=1/max=200 самоочевиден. /stats: фильтр-чекбоксы с видимыми подписями; search — placeholder + aria-label + кнопка «Искать» (конвенциональный search-паттерн, достаточно). Наблюдение (не правил): search на placeholder, не постоянный <label> — приемлемо для поиска. Код не менялся. Детали — cr.164.

### R1.70 — WCAG 2.4.6 Headings and Labels (AA): описательность — verified-clean

Обход всех h1..h6 на /, /stats, /settings, странице вопроса. Все заголовки непусты и описательны: / (Подготовка→шаблон-вопрос→Горячие клавиши), /stats (Аналитика + 6 h2 разделов), /settings (Настройки сессии + h2 панелей + h3 подсекций), вопрос (site-title h1 + вопрос h2 + группа опций «Варианты ответа»). Везде ровно один h1, 0 пустых, 0 пропусков уровней (бонус 1.3.1 иерархия), 0 дублей; вкладки «Сессия/Оформление/Данные» описательны. Наблюдение (не правил): /stats,/settings h1 page-specific, а home/вопрос — generic site-title с контентом в h2; описателен, не провал, одна template dashboard+вопрос. Код не менялся. Детали — cr.165.

### R1.71 — WCAG 3.3.1 Error Identification (A): ошибки ввода текстом — verified-clean

Constraint Validation API без сабмита на session-form. count (number min=1 max=200, форма без novalidate): over max 500 → «Значение должно быть меньше или равно 200.» (rangeOverflow), under min 0 → «…больше или равно 1.» (rangeUnderflow), empty → valid (required=false, сервер дефолтит), 20 → ok. Нативная валидация блокирует сабмит + фокус/подсветка + текст → 3.3.1 A выполнен (G84/G85). Прочие формы без ошибочного ввода (селекты фикс. опции, search любой текст). Наблюдение (не правил): count без aria-invalid/describedby — нативный UI достаточен для A; кастомный inline-error был бы enhancement (+3.3.3), JS-blast-radius. Код не менялся; count восстановлен, сабмита не было. Детали — cr.166.

### R1.72 — WCAG 2.4.5 Multiple Ways (AA): ≥2 способа найти контент — verified-clean

Инвентаризация wayfinding на / и /stats. Пять независимых путей (требуется ≥2): поиск (/stats input[type=search] «Поиск по вопросам», G161); обзор полного списка тем (319 topic-ссылок = индекс/ToC всех тем, G126/G64); primary-nav (Фокус/Аналитика/Настройки, G125); topic-select (320 опций); контекстные рекомендации (321 ссылка weakTopics/onlyWrong/related/today). Три самодостаточны и независимы → ≥2 с запасом. Страница вопроса = шаг процесса, но темы напрямую локализуемы поиском+списком → исключение не нужно. Код не менялся. Детали — cr.167.

### R0.101 (2026-07-13) — Статический a11y-workflow (6 WCAG-дименсий × адверсари-верификация) + сверка §10 по кодовому evidence

- **Единица:** оркестрованный read-only workflow (8 агентов, 531k ток., 6 дименсий по всем 17 шаблонам/фрагментам; каждый сырой finding — адверсари-опровержение) + фолд-ин в §10. App лежит (docker-postgres не поднят → bootRun голодает на пуле Hikari, HTTP 000) → live-QA/gradle недоступны; выбран не-гейтнутый статический юнит.
- **Результат аудита:** 1.1.1 non-text / 4.1.1 dup-id / 2.4.4 link-purpose / 3.1.1 lang — ЧИСТО (исчерпывающе, включая result/summary — недостижимые в живом рендере). 3.3.2 (count min/max без видимой инструкции) — 1 сырой finding ОПРОВЕРГНУТ верификатором (label+констрейнты достаточны). **Единственный CONFIRMED:** 1.3.1 result.html «Похожие вопросы» = N ссылок без list-семантики (P4 minor) → §10 RES-15.
- **4.1.1 особо ценно:** проверены все 14 th:each — НЕТ литеральных id внутри циклов (рантайм-дублей id нет); фрагменты с параметр-id включаются ≤1 раза с уникальными id. Дубль-id поверхность закрыта статически.
- **Сверка §10 по кодовому evidence (git/grep, app не нужен):** DSG-1/DSG-2 → 🚫 ABANDONED (linear/swiss/broadsheet.css удалены R0.81 / 9f0c425c, 0 refs); AIR-2 → ◐ ЧАСТИЧНО (result-toggle удалён R0.81, focus-toggle перепрофилирован в reveal related); APP-8/AIR-4 → ⏸ WINDOW-ТИК (regenerate-removal-plan.md R0.63 применится в окно).
- **RES-15 НЕ фикшу сейчас:** правка структурная (role=list без wrapper-div — риск layout), app лежит → нельзя browser-QA (§26). Фикс при подъёме стека.
- **Дальше:** гейты те же (§21 R0.99 + R0.100); при подъёме app — RES-15 fix + window-тик (контракт-тесты + regenerate-removal). Нумерация R0.102+.

### R0.102 (2026-07-13) — RES-15 prep: точные ханки + разбор layout-риска + расширение скоупа на app.js (паттерн R0.63 prep→apply)

- **Единица:** подготовка (не применение) единственного CONFIRMED-finding'а из R0.101. App лежит (HTTP 000, docker-postgres не поднят → bootRun голодает), result.html недостижим read-only (рендерится только на POST /answer = мутация) → apply+QA нельзя (§26). Готовлю ready-to-apply артефакт, как с regenerate-removal (R0.63).
- **Открытие — скоуп ДВУХЧАСТНЫЙ:** тот же дефект list-семантики есть не только в SSR result.html:132-138, но и в `app.js renderRelatedQuestions` (1624-1648) — focus-динамика строит `h3 + N <a class="related-question-item">`-сиблингов без role. Паритет обязателен: фикс в обоих местах.
- **Layout-риск снят замером по коду (base.css 2562-2588):** `.related-question-item{display:block}`, родитель `.related-questions`/`#result-related-questions` — БЕЗ flex/grid (только border-top/padding/margin), wide-layout sticky-рейка таргетит секцию по id. Обёртка `<div role="list">` = блок 100%-ширины без своих margin/padding → потомки-блоки текут идентично → **0 визуальных изменений**. Снимает опасение «риск layout» из R0.101 (там рассматривался role=list без wrapper — теперь wrapper-подход подтверждён безопасным). CSS-правок НЕ требуется; v-бамп только app.js (v=60→61, APP-7).
- **Артефакт:** `design/mockups/port-drafts/res15-related-list-semantics-plan.md` — точные ханки обоих мест, план apply (collision-guard → правка → v-бамп → live-sync → node --check → session-gated QA обеих тем → леджеры). §10 RES-15 → 📋 PREP-READY.
- **QA-гейт:** result.html и focus-динамика недостижимы без POST /answer → RES-15 apply+QA пойдёт одним окном с session-gated EXAM-parity (решение юзера).
- **Дальше:** гейты неизменны; следующий не-гейтнутый юнит — если app лежит, углублять статику/леджеры; при подъёме — RES-15 apply + window-тик. Нумерация R0.103+.

### R0.103 (2026-07-13) — Сверка §7 mockup-инвентаря с диском: 3 атлас-mockup'а отсутствуют (ложный ✅ → честный ◐/🧩)

- **Единица:** аудит-сверка §7 «Mockup» ↔ фактические файлы `design/mockups/*.html` (app не нужен — статическая проверка диска). Прямой триггер — курс на «прогресс по таблицам к конечной цели»: таблицы должны быть ПРАВДИВЫ.
- **Находка (worst-first: ложный ✅ хуже честного пробела):** `find design/mockups -maxdepth 1 -name '*.html'` = 8 файлов (error, focus-hero-ab, focus-question, result, session-summary, settings, shell, stats). Три файла, на которые §7 ссылалась в колонке «Mockup», **НЕ существуют**: `overlay-state-atlas.html`, `feedback-state-atlas.html`, `component-state-atlas.html`. При этом матрица держала OVERLAYS/FEEDBACK Mockup=✅ MockupQA=✅ — ложное доказательство.
- **Нюанс (проверено grep по mockups):** состояния этих поверхностей НЕ отсутствуют — они **распределены** по surface-mockups: shell.html держит `mobile-menu`+`kbd-help-overlay`, settings.html — reset-dialog/validation, focus/stats — aria-live/alert. Т.е. дефект не «нет дизайна», а «нет консолидированного атласа + матрица это скрывала».
- **Правка таблицы:** §7 OVERLAYS/FEEDBACK/COMPONENTS → Mockup=◐, MockupQA=◐, колонка-файл = `🧩 нет атласа; states в <surface>-mockups`, Notes = откуда берутся состояния + «консолидир. атлас — Фаза B backlog». Плюс SHELL: имя файла `shell-header.html` → `shell.html` (файл на диске так и называется). Легенда §7 дополнена значком 🧩.
- **Queued Фаза B work (не гейтнуто app):** собрать 3 консолидированных атлас-mockup'а (overlay/feedback/component-state-atlas) как статические демо-листы всех state-family в обеих темах — годится как отдельные атомарные тики Фазы B, пока стек лежит.
- **Дальше:** гейты app/EXAM/window неизменны; не-гейтнутая очередь теперь явная — построить недостающие атласы. Нумерация R0.104+.

### R0.104 (2026-07-13) — Расстыковка `screen-registry.json` (заморожен на R0.27) с фактическим пайплайном (R0.103) — reconciliation-блок

- **Единица:** сверка §2-авторитетного артефакта `design/mockups/screen-registry.json` с текущим состоянием (app не нужен — статическая сверка JSON ↔ §7/§9/§21 + диск). Продолжение нити «правдивость таблиц».
- **Находка:** регистр инвентаризует **7 реальных поверхностей** (shell/focus/result/summary/settings/stats/error), а overlay/feedback/component держит как *состояния* внутри них (shell.`kbd-help-modal`/`mobile-drawer`, settings.`reset-options-confirm`, focus.`inline-alert`) — **точнее**, чем 3 синтетич. атлас-строки §7. НО все per-surface `phase`/`port_status`/`notes` заморожены на ≤R0.27 («D_PORT_MAPPING», «ПОРТ BLOCKED — в параллельном WIP»), хотя порты выполнены R0.46-53 и surfaces прошли parity/refactor/regression R0.54-98. Регистр читается каждый тик (§2) → активно вводил в заблуждение.
- **Подтверждение портов (не из памяти):** все 7 `PORT_MAPPING_*.md` на диске + base.css содержит портированные классы (`ed-page`/`set-card`/`error-heading`, 17 совпадений) → §7/§9 Port=✅ корректны.
- **Правка:** добавлен top-level `reconciliation_2026_07_13` (R0.104): актуальная фаза по каждой из 7 поверхностей (shell/settings/stats/error → H_REGRESSION_QA gated; focus → G/H; result/summary → F_PRODUCTION_PARITY session/EXAM-gated), список открытых гейтов, surface-model note (§7 атлас-строки vs registry state-модель). Per-surface исторические `notes` НЕ тронуты (evidence). JSON провалидирован. Живой трекер = §7/§9 + §21, не замороженные поля.
- **Дальше:** гейты неизменны; инвентарные артефакты (§7, registry) теперь оба правдивы и согласованы. Не-гейтнутая очередь: 3 атлас-mockup'а (Фаза B). Нумерация R0.105+.

### R0.105 (2026-07-13) — Построен `feedback-state-atlas.html` + QA (Фаза B, первый из 3 недостающих атласов; app не нужен)

- **Единица:** Фаза B — построить первый недостающий консолидир. атлас (feedback). **Ключевой анлок:** chrome-devtools MCP рендерит mockups по `file://` без поднятого app (mockups самодостаточны) → весь Фаза-B поток исполним при лежащем стеке.
- **Артефакт:** `design/mockups/feedback-state-atlas.html` — скелет по `error.html` (общий язык Instrument: `tokens.css`, шрифты Newsreader/Space Grotesk/JetBrains Mono, преамбула + хром + тема-тогл). 6 секций: (1) inline-alert error(база)/success/info/warn — зеркалит прод `.inline-alert` `base.css:1275` (+ `.success`); (2) toast; (3) валидация норма/ошибка (нативная Constraint Validation + опц. усиление); (4) pending спиннер/скелет (role=status/aria-busy, reduced-motion-safe); (5) verdict correct/wrong (пост-ответ); (6) легенда aria-live (assertive/polite/busy/native).
- **QA (file://, chrome-devtools):** dark (desktop full), light (desktop full), 390px mobile — все 3 среза чисты: семантические цвета верны обеими темами, 2-кол→1-кол collapse, 0 horizontal overflow, ARIA-метки на месте.
- **Design-gap найден (Фаза G кандидат):** система токенов = 3 семантики (`--signal`/`--success`/`--error`), **нет** warning/caution. Прод `.inline-alert` тоже только error+success. Warn в атласе на амбере `--spark` (акцент графиков, не feedback-семантика). Решение: ввести `--warn/-wash/-ink` ЛИБО осознанно закрепить отсутствие warning.
- **Таблицы:** §7 FEEDBACK Mockup/MockupQA ◐→✅, файл `feedback-state-atlas.html`. Registry surface_model_note обновлён. Final ◐ остаётся (прод-parity gated).
- **Дальше:** очередь Фазы B: `overlay-state-atlas.html` + `component-state-atlas.html` (те же 2 недостающих). Гейты app/EXAM/window неизменны. Нумерация R0.106+.

### R0.106 (2026-07-13) — Построен `overlay-state-atlas.html` + QA (Фаза B, 2-й из 3; включая живой focus-trap demo)

- **Единица:** Фаза B — второй недостающий атлас (оверлеи). Скелет по `error.html` (язык Instrument).
- **Артефакт:** `design/mockups/overlay-state-atlas.html` — 6 секций: (1) модальный dialog — зеркалит прод `#kbd-help-overlay` (`role="dialog"` aria-modal, kbd-список) + **живой демо-оверлей** (реальный fixed scrim `color-mix ink 42%` + focus-trap + Esc + возврат фокуса); (2) alertdialog — зеркалит `#reset-confirm` (`role="alertdialog"`, дефолт-фокус «Отмена», деструктив `--error`); (3) drawer — `#mobile-menu` (disclosure, aria-expanded/controls); (4) disclosure (`<details>`) + tooltip; (5) кандидаты языка menu/popover — **явно помечены «не в проде»** (заготовки на будущее, как warn-gap R0.105); (6) легенда scaffold (scrim/focus-trap/Esc/возврат-фокуса/роли/motion).
- **QA (file://, chrome-devtools):** dark (full), light (full), narrow (0 horizontal overflow, `scrollW==clientW`), **живой демо**: клик открывает — scrim затемняет всю страницу, панель по центру с pop, фокус ушёл на первый focusable (кнопка закрытия, teal-кольцо), Tab-цикл заперт. Оба закрытия (X + клик по scrim) работают.
- **Таблицы:** §7 OVERLAYS Mockup/MockupQA ◐→✅, файл `overlay-state-atlas.html`. Registry note обновлён. Final ◐ (прод-parity gated).
- **Дальше:** остаётся последний атлас Фазы B — `component-state-atlas.html` (primitives × states). Гейты app/EXAM/window неизменны. Нумерация R0.107+.

### R0.107 (2026-07-13) — Построен `component-state-atlas.html` + QA (Фаза B, 3-й из 3 — АТЛАС-ТРИО ЗАМКНУТО)

- **Единица:** Фаза B — последний недостающий атлас (компоненты). Скелет по языку Instrument (`tokens.css`).
- **Артефакт:** `design/mockups/component-state-atlas.html` — 8 секций примитивов × состояния: (1) кнопки 4 варианта × 6 состояний (primary/secondary/ghost/danger; default/hover/active/focus/disabled/loading со спиннером); (2) ссылки + icon-кнопки (default/hover/focus); (3) поля ввода + select (default/focus/invalid/disabled); (4) контролы выбора (seg-control radiogroup, toggle switch off/on/focus, нативные radio/checkbox); (5) вкладки (зеркало `settings-tab`); (6) **MCQ-опция `.opt`** 6 состояний (default/hover/selected/focus/correct/incorrect — статус формой+цветом, зеркалит `focus-question.html`); (7) чипы/бейджи/kbd/теги; (8) уровни поверхностей (paper/surface/surface-2). Псевдо-состояния показаны demo-классами (`.demo-hover/.demo-active/.demo-focus`), зеркалящими реальные `:hover`/`:active`/`:focus-visible` — весь спектр виден статически.
- **QA (file://, chrome-devtools):** dark (full), light (full), narrow 375 (`scrollW==clientW`, 0 horizontal overflow). Все состояния читаемы обеими темами; семантика correct/incorrect зелёный/красный; focus-кольца teal; loading-спиннер; disabled-затемнение.
- **Таблицы:** §7 COMPONENTS Mockup/MockupQA ◐→✅, файл `component-state-atlas.html`. Registry: **Фаза B mockup-coverage OVERLAYS/FEEDBACK/COMPONENTS замкнута** (все 3 атласа созданы+QA: R0.105/106/107).
- **Веха:** §7 матрица — **все 10 поверхностей теперь Mockup+MockupQA=✅** (нет ни одного ◐/🧩 в mockup-колонках). Фаза B (mockup completeness) закрыта в текущем раунде. Остаётся Final ◐/⏸ по всем — гейтнуто прод-parity (app/EXAM/window).
- **Дальше:** не-гейтнутая mockup-очередь исчерпана. Следующие не-гейтнутые юниты: (а) mockup-recheck существующих 7 surface-mockups на новые состояния; (б) при подъёме app — port-parity/RES-15/window-тик. Нумерация R0.108+.

### R0.108 (2026-07-13) — Само-аудит покрытия: закрыты `expanded` + `long content` в component-атласе (честность MockupQA=✅)

- **Единица:** само-аудит §7 COMPONENTS state-family («default; hover; active; focus; disabled; loading; selected; checked; **expanded**; dark; **long content**») против фактического содержимого `component-state-atlas.html` (grep — оба отсутствовали). Чтобы MockupQA=✅ был честным, а не преувеличенным.
- **Правка:** в `component-state-atlas.html` добавлены 2 секции: (1) **Раскрытие** — disclosure collapsed/expanded (caret rotate, `aria-expanded`, работает без JS; зеркалит «Показать код»/error-details); (2) **Длинный контент** — option многострочный (badge top-align, текст переносится 8 строк), группа чипов оборачивается, кнопка с длинным лейблом в 3 строки.
- **QA (file://):** новые секции в light + dark, `overflow=false` — переносы корректны, 0 horizontal scroll, семантика/caret читаемы обеими темами.
- **Итог:** component-атлас теперь покрывает все 11 state-family §7. §7 COMPONENTS note дополнен. (Registry не менялся.)
- **Дальше:** не-гейтнутая mockup-очередь по-прежнему исчерпана (атласы полны). При подъёме app — port-parity/RES-15/window-тик. Нумерация R0.109+.

### R0.109 (2026-07-13) — Аудит консистентности mockup-системы: VERIFIED clean (recheck, goal-шаг 1)

- **Единица:** статический аудит всех 11 mockup'ов (`design/mockups/*.html`) на дрейф после серии свежепостроенных атласов (R0.105–108) — 3 вектора консистентности + token-bypass.
- **Метод:** grep. (1) linkage `href="tokens.css"`; (2) хардкод цветов `#hex|rgb|oklch` в обход токенов (исключая `stroke=`/`fill=` SVG); (3) единообразие пути к tokens.css и загрузки шрифтов.
- **Результат — CLEAN:**
  - 11/11 линкуют `tokens.css`; путь идентичен (относительный `href="tokens.css"`) во всех; загрузка шрифтов идентична (3 ref — preconnect googleapis + gstatic + stylesheet — в каждом).
  - Token-bypass: 0 во всех, **кроме** `session-summary.html` — 10 совпадений. **Адверсариальная проверка:** все 10 внутри `@media print { … }` (стр. 241→258), единицы `pt` (`11pt`, `4pt 6pt`), чистые `#000`/`#fff`. Это **корректная** print-практика (типографские чернила/бумага; OKLCH-токены калиброваны под экран, у печати нет тёмной темы) — **НЕ дрейф, править нельзя** (замена на `var(--ink)` дала бы серый в печати = хуже).
- **Итог:** mockup-слой консистентен, **правок не требуется** (это верный исход аудита). Recheck-ось goal-шага 1 продвинута фактом-верификацией, а не косметикой. Файлы не менялись — только лог.
- **Дальше:** не-гейтнутая mockup-очередь исчерпана И проверена на консистентность. Оставшийся forward-прогресс пайплайна (port-parity/RES-15-apply/window-тик/EXAM-решение) честно app-гейтнут — ждёт подъёма postgres+app пользователем. Нумерация R0.110+.

### R0.110 (2026-07-13) — §10 RECHECK-резолюция: STA-10 CTA аналитики → DECISION_LOCK (контракт проведён, app не нужен)

- **Единица:** довести до вывода одну ⬜ RECHECK-строку §10 чисто-статически (снижает открытый бэклог = forward-прогресс к конечной цели, пока app лежит). Взята `STA-10` — «CTA из аналитики (тренировать слабые/ошибки), может требовать роутов/параметров».
- **Метод:** grep-трассировка контракта CTA→маршрут→выборка (без app).
- **Результат — CTA ПРОВЕДЕНЫ end-to-end, не мёртвые:**
  - `stats.html:32` «Разобрать ошибки» → `@{/(onlyWrong=true, ordered=true)}`; `stats.html:40` «Тренировать слабые темы» → `@{/(weakTopics=true, ordered=true)}` — оба на GET `/` (навигация, без мутации БД).
  - Биндинг: `InterviewMvcController.index` (`@GetMapping("/")`, стр.37) объявляет `@RequestParam onlyWrong`(42)/`weakTopics`(44)/`ordered`(45), прокидывает (57).
  - Маппинг: `MvcRequestMapper:116` `weakTopicsPriority = Boolean.TRUE.equals(weakTopics)` → `SettingsRequestContext`; `onlyWrong`/`ordered` → `resolveFilter` → `InterviewFilter`.
  - Влияние на выборку: `FocusTrainingPageService:57` `facade.nextQuestion(filter, weakTopicsPriority, …)` → `InterviewService:130` **Strategy pattern** `weakTopicsPriority ? weakTopicsSelectionStrategy : defaultSelectionStrategy`; `filter.onlyWrong()` в repo-запросах (`InterviewService:151/155/158`, `TrainingSessionService:52/55/60`).
- **Итог:** гипотеза «может требовать роутов/параметров» **опровергнута** — маршрут и все 3 параметра существуют и честятся. STA-10 `⬜ RECHECK` → `🚫 DECISION_LOCK (functional)`. Правок кода не требуется. §9 STATS не разблокирован (Final по-прежнему parity-gated), но контракт STATS→FOCUS CTA подтверждён (Port-mapping ось).
- **Дальше:** остаются ⬜ RECHECK-строки §10, разрешимые статически (`FT-2` per-mode микрокопия, `AIR-6` dead-ветка, `STA-3`-класс). При подъёме app — port-parity/RES-15/window-тик. Нумерация R0.111+.

### R0.111 (2026-07-13) — §10 RECHECK-резолюция: FT-2 per-mode микрокопия → DECISION_LOCK (реализовано, app не нужен)

- **Единица:** статически разрешить ещё одну ⬜ RECHECK §10 — `FT-2` «`.question-zone-head` chip+hint, серверная микрокопия режима».
- **Метод:** прочитать разметку (`focus-training.html:17-19`) + вычислитель (`MvcModelAttributeMapper`).
- **Результат — микрокопия УЖЕ per-mode, не дженерик:**
  - Chip `resolveFocusModeChipText` (82-93): `reviewMode`→«Повтор ошибок»; `flashcard/FLASHCARD`→«Флешкарты»; `mode==null`→«Тренировка»; иначе `modeUtils.displayName(mode)` (Экзамен/Марафон/…). 4 ветки.
  - Hint `resolveFocusModeHintText` (95-106): `reviewMode`→«Режим review…»; `flashcard/FLASHCARD`→«Флешкарты: вспомни…оцени себя»; `STUDY`→«Изучение: разберись…»; default→«Выбери один вариант. Проверка и разбор идут по шагам.». 4 ветки, ни одна не даёт пустую строку.
  - EXAM/MARATHON/TRAINING → общий default-hint. **Защитимо:** способ ответа на один вопрос у MCQ-режимов идентичен; режимы различаются длиной/скорингом *сессии*, не интеракцией *вопроса* → общий hint корректен.
- **Итог:** гипотеза «серверная микрокопия режима» подтверждена как **уже реализованная и корректная**. Остаток «уточнять per-mode формулировки точечно» = опциональный контент-полиш (distinct EXAM/MARATHON-hint), decision-gated, не frontend-дефект (и граничит с контент-доменом — не переписываю в одностороннем порядке). FT-2 `⬜ RECHECK` → `🚫 DECISION_LOCK`. Правок кода не требуется.
- **Дальше:** ⬜ RECHECK §10, разрешимые статически: `AIR-6` (dead-ветка empty-state), `FT-15` (submit CTA), `SET-3`/`SET-9` (settings-полиш, blocked-file), класс-`STA-3`. При подъёме app — port-parity/RES-15/window-тик. Нумерация R0.112+.

### R0.112 (2026-07-13) — §10 RECHECK-резолюция: AIR-6 user-facing DONE / рудимент → WINDOW-ТИК (app не нужен)

- **Единица:** статически разрешить ⬜ RECHECK §10 `AIR-6` — «мёртвая AI-ветка в empty-state focus-training».
- **Метод:** прочитать empty-state (`focus-training.html:183-215`) + все присвоения `generationUnavailable` (`FocusTrainingPageService`).
- **Находка — user-facing дефект УЖЕ ЗАКРЫТ:**
  - Абзац `<p th:if=${generationUnavailable}>«AI временно недоступен…»` **удалён** прошлым коммитом; на его месте комментарий-аннотация `focus-training.html:185-188` («…никогда не рендерилась и вводила в заблуждение — удалена»). Removal зафиксирован (focus-training.html не в незакоммич. наборе).
  - Текущий empty-state корректен без AI: 4 взаимоисключающие ветки (190-193) review / нет-сессии-фильтры-пусты / сессия-не-готова / сессия-завершена + `.empty-actions` с осмысленным primary-CTA (итоги при finished, настройки при пустых фильтрах — SUM/finish-логика R1.74).
- **Остаток (не user-facing):** рудиментарная plumbing — `generationUnavailable = false` (`FocusTrainingPageService:54`, не переприсваивается 55-91) протянут через `FocusPageState`→model-attr; 4 ветки держат всегда-истинные `!generationUnavailable and …` guards (no-op, безвредны). Вычистка трогает service+model-контракт → **contract-тесты** → window-тик-гейт.
- **Итог:** AIR-6 `⬜ RECHECK` → `⏸ WINDOW-ТИК (dead-plumbing cleanup, contract-gated)`; user-facing = DONE. Ledger приведён к правде (старое «dead-not-rendered / удалить при AI-cleanup» не отражало, что абзац уже удалён). Правок кода не требуется сейчас.
- **Дальше:** оставшиеся ⬜ RECHECK §10 либо app/visual-gated (`FT-15` submit-CTA-раскладка), либо settings-blocked-file (`SET-3`/`SET-9`/`SET-15`), либо decision-gated (`RES-3`/`APP-5` favorite, `IC-5`/`ICO-3` icon). Пул чисто-статических RECHECK почти исчерпан. При подъёме app — port-parity/RES-15/window-тик. Нумерация R0.113+.

### R0.113 (2026-07-13) — §10 RECHECK-резолюция: SET-15 → ✅ DONE (фикс уже в app.js, app не нужен)

- **Единица:** статически разрешить ⬜ RECHECK §10 `SET-15` — «`count` hardcode `value="20"` шэдоуит серверный per-mode дефолт (marathon=50)». Это был подтверждённый forms-UX-рассинхрон (р102), не «дефект ли это» — потому проверял, реализован ли фикс.
- **Метод:** трассировать count-контракт FE↔BE (settings.html + app.js + SessionFlowService), без app.
- **Находка — УЖЕ ИСПРАВЛЕНО:**
  - `app.js:248-257` — блок с явной меткой `// SET-15:`: на `change` режима читает `data-default-count` выбранного `<option>` и `countInput.value = def`. settings.html:119-123 несут `data-default-count` (EXAM/TRAINING=20, MARATHON/STUDY/FLASHCARD=50) — совпадает с BE `SessionFlowService.resolveSessionTotal:103-106` (`EXAM ? defaultExamCount : defaultMarathonCount`).
  - Итог: при выборе MARATHON поле count → 50, не жёсткая 20. FE выровнен к BE per-mode дефолтам.
  - Слушатель намеренно **не** на первичном `sync()` (261-286): иначе затёр бы localStorage-restore пользовательского count; trade-off задокументирован в коде.
- **Остаток (минор, осознанный):** сервер-пред-выбор не-EXAM режима + нет localStorage → стартовое `value="20"` до явной смены. Видно в поле до submit; выбор в пользу restore документирован. Не открытый дефект.
- **Итог:** SET-15 `⬜ RECHECK` → `✅ DONE` (фикс закоммичен, app.js не в незакоммич. наборе). Ledger был устаревшим (не отражал реализацию — как AIR-6 в R0.112). Правок кода не требуется.
- **Дальше:** пул чисто-статических RECHECK §10 фактически исчерпан — оставшиеся app/visual-gated (`FT-15`), settings-blocked-file live-QA (`SET-3`/`SET-9`), decision-gated (favorite/icon). Следующие тики без app: либо re-validate app-gated prep (RES-15-план vs текущий код — turnkey-готовность), либо честно зафиксировать упор в подъём app. Нумерация R0.114+.

### R0.114 (2026-07-13) — Re-validation RES-15-препа vs текущий код: поймана устаревшая версия app.js (app не нужен)

- **Единица:** сверить app-gated prep `port-drafts/res15-related-list-semantics-plan.md` (WCAG 1.3.1, единственный PREP-READY прод-фикс, первый на apply при подъёме app) с ФАКТИЧЕСКИМ кодом — turnkey-готовность (номера строк/версии дрейфуют).
- **Результат сверки:**
  - `result.html:132-138` — ханк плана **точен** (section 132 / h3 133 / `<a th:each class=related-question-item>` 134; `role=listitem`/wrapper ещё нет → фикс не применён). ✅
  - `app.js renderRelatedQuestions:1624-1648` — ханк **точен** (h3 1635, forEach 1636, `<a>` 1637). ✅
  - Layout-риск **= NULL подтверждён** (base.css:2562-2588): `.related-question-item{display:block}` (2581); родитель `#result-related-questions`/`.related-questions` (2562-2568) — только border/margin/padding, **без flex/grid** → `<div role=list>`-обёртка течёт идентично, 0 визуального сдвига. ✅
  - **⚠️ ПОЙМАНА УСТАРЕВШЕСТЬ:** план указывал бамп app.js `v=60→61`, §9-заметка — `v=56`; **фактически все 3 шаблона на `v=62`** (result:142/settings:291/focus-training:222). Слепой apply по «60→61» **понизил бы** версию ниже текущей → сломал бы cache-busting (APP-7/HEAD-1 контракт). Ровно то, что re-validation обязан ловить.
- **Правки (только доки/план, не прод-код):** план-док — бамп исправлен на **v=62→63** (3 места: §layout-риск / §ханк-2 / §apply-шаг-2, + инструкция «сверить факт. версию перед бампом»); §9 app.js — заметка `v=56`→`v=62`.
- **Итог:** RES-15-prep снова **turnkey** (ханки точны, layout-риск NULL держится, версия-бамп актуализирован). Apply остаётся session-gated (result рендерится только на POST /answer → QA вместе с EXAM-parity, одним окном).
- **Дальше:** без app — прочие app-gated preps на turnkey-сверку (regenerate-removal-план R0.63 vs код) либо честная фиксация упора в подъём app. При подъёме — RES-15 apply+QA/port-parity/window-тик. Нумерация R0.115+.

### R0.115 (2026-07-13) — Re-validation regenerate-removal-препа (window-тик) vs код: устранён крупный дрейф (app не нужен)

- **Единица:** сверить второй app-gated prep `port-drafts/regenerate-removal-plan.md` (R0.63 window-тик; §10 APP-4/AIR-4) с фактическим кодом — turnkey-готовность. Как RES-15 в R0.114.
- **Находка — существенный дрейф (план собран 2026-07-12, база активно правится):**
  - result.html: btn-regenerate 48-50 + `regen-badge` (`regenCount()>0`) **51-53** (план: 48-52); question-side 121 ✅; коммент 30 ✅.
  - app.js: `API.REGENERATE` 18 ✅; но `regenerateQuestion` факт. **~898-965** (план ~884-950); делегат `.btn-regenerate` факт. **~977-981** (план ~968-973).
  - base.css — **все 6 ссылок устарели** (дрейф +20…−121 строк): `.btn-regenerate:active` группа **1067** (было 1047); `:has(.question-side)` грид **2329-2337** (2292); `.btn-regenerate` база/hover/regenerating/regen-badge **2435-2457** (2398-2419); `@media 1fr` **2603** (2689); related×question-side **3171-3173** (3256); print/touch группа **3372** (3493).
  - **Новая находка:** `.regen-badge` есть на **stats-page (base.css:1892)**, не только result-page — план не флагал; помечено «проверить stats-разметку перед удалением» (возможно живое использование).
  - **⚠️ Ошибка локации `?v=`:** план велел «бампнуть app.js в head.html», но app.js-версия в 3 шаблонах (v=62, R0.114), а head.html держит только `base.css(v=92)`+`tokens.css(v=64)`. Слепой apply не бампнул бы app.js → кэш не сброшен.
- **Правки (только план-док, не прод-код):** актуализированы все номера строк (result/app.js/base.css); исправлена локация+таргет `?v=` (app.js v=62→63 в 3 шаблонах; base.css v=92→93 в head.html:311); добавлен флаг stats-page regen-badge.
- **Итог:** regenerate-removal-prep снова **turnkey** для window-тика. Apply остаётся gated (нужен gradle-прогон TemplateFragmentContractTest+InterviewMvcControllerTest → окно без живого bootRun).
- **Дальше:** оба крупных app-gated препа (RES-15 R0.114 + regenerate-removal R0.115) сверены turnkey. Без app остаётся: мелкие статические сверки/мониторинг подъёма app. При подъёме postgres+app — window-тик (regenerate-removal, закроет 7 PENDING-TEST §9) + RES-15 apply + port-parity. Нумерация R0.116+.

### R0.116 (2026-07-13) — Разрешён loose-end R0.115 + вскрыт новый dead-остаток: STA-20 (app не нужен)

- **Единица:** довести до вывода флаг из R0.115 — живое ли `.stats-page .regen-badge` (base.css:1892), или мёртвый AI-остаток. Статически.
- **Находка:**
  - `stats.html:224-227` — колонка `<td data-label="Сброшено">`: `<span th:if="${item.regenSum() > 0}" class="regen-badge">` (мёртво — AI вырезан 2026-07-07 + wipe БД → `regenSum()` всегда 0) + `<span th:if="regenSum()==0" class="muted">—</span>` (всегда). → **Колонка всегда рендерит «—»** для всех тем = шум без информации. Источник `TopicStatsResponse.regenSum` (`StatsApiMapper:59`).
  - CSS `base.css:1892` `.stats-page .regen-badge` — в **общем** правиле с живым `.stats-page .maturity-badge` (1893) → блок 1892-1896 нельзя удалять целиком, только строку-селектор 1892.
- **Действия (только доки, не прод-код):**
  - **Новый §10 `STA-20`** — мёртвая колонка «Сброшено»/regenSum (P4 minor); удаление app/contract-gated + сверка share/print-контракта (SUM-7-класс, кол-во `<td>`) → ⏸ WINDOW-ТИК/decision.
  - regenerate-removal-план: base.css item 7 уточнён — этот план трогает ТОЛЬКО `.result-page .regen-badge` (2457); stats-вариант (1892 общее правило + разметка 224-227) вынесен в STA-20, НЕ в result-page removal. Устранена неоднозначность «удалять ли stats-вариант».
- **Итог:** loose-end R0.115 закрыт; граница двух cleanup'ов чёткая; новый dead-остаток задокументирован (не потерян). Правок кода не требуется сейчас.
- **Дальше:** без app — мониторинг подъёма + мелкие статические сверки. При подъёме — window-тик (regenerate-removal + STA-20 stats-колонка + RES-15) одним окном (общий v-бамп/контракт-тесты) + port-parity. Нумерация R0.117+.

### R0.117 (2026-07-13) — Аудит полноты AI-removal fallout: покрытие 100%, 0 недокументированных остатков (app не нужен)

- **Единица:** систематический свип всех AI-остатков в шаблонах/JS (продолжение STA-20) — есть ли ещё осиротевший dead-UI, гейтнутый на всегда-falsy AI-условии. Ограниченное конечное множество токенов.
- **Метод:** grep `aiEnabled`/`regen*`/`generationUnavailable`/`aiProvider`/`aiFallback`/`extra-analysis` по templates+static/js + источники model-attr.
- **Полный инвентарь AI-остатков (все задокументированы):**
  - `aiEnabled` — 2 реф в шаблонах (`result.html:49` btn-regenerate, `:121` question-side) + источник `MvcModelAttributeMapper:152` `addAttribute("aiEnabled", false)` (жёстко false навсегда) → **regenerate-removal-план** + AIR-4.
  - `regen*` — `stats.html:225-226` regenSum-колонка → **STA-20**; `app.js:896/902` JSDoc+коммент + `regenerateQuestion` → **regenerate-removal-план**.
  - `generationUnavailable` — 5 реф (`focus-training.html:185-195`) + источник `MvcModelAttributeMapper:47` → **AIR-6** (user-facing удалён, рудимент-plumbing window-тик).
  - `extra-analysis-toggle` (`post-answer-controls.html:9`) → **AIR-2** (focus-toggle reveal related, не dead-end).
  - Прочих AI-атрибутов (aiProvider/aiFallback/aiMode) в шаблонах/JS **нет**.
- **Итог — покрытие 100%:** каждый dead-AI-UI-элемент учтён тремя cleanup-записями (regenerate-removal + STA-20 + AIR-6) + AIR-1/2/3. **Недокументированных остатков нет.** AI-cleanup-бэклог = **закрытое конечное множество** — при window-тике удаление regenerate + STA-20 + AIR-6-plumbing вычистит весь dead-AI-surface без «хвостов». Сертифицирует goal-шаг 1 (recheck) + 6 (fix all) для AI-removal домена. Правок кода не требуется (верный исход аудита).
- **Дальше:** без app — статический пул сверок фактически исчерпан (RECHECK §10 закрыты R0.110-113; оба препа turnkey R0.114-115; AI-fallout сертифицирован R0.116-117). Остаётся мониторинг подъёма app. При подъёме — window-тик (3 cleanup'а одним окном) + RES-15 + port-parity. Нумерация R0.118+.

### R0.118 (2026-07-13) — Консолидация app-up-работы в единый window-tick манифест (app не нужен)

- **Единица:** app-up-работа разбросана по 4 план-докам + строкам §10/§21 с **координационным риском** (regenerate-removal И RES-15 оба правят app.js → нельзя бампать дважды; regenerate-removal И STA-20 оба правят base.css). Собрать в один исполнительный turnkey-чеклист.
- **Создан** `port-drafts/window-tick-manifest.md` — оркестратор app-up-окна:
  - **Матрица пересечения файлов** (app.js / base.css / result.html / stats.html / focus-training.html / 4 .java) × 4 единицы (regenerate-removal / STA-20 / AIR-6 / RES-15) → видно, что app.js бампается **один раз** v=62→63 (покрывает regenerate+RES-15), base.css **один раз** v=92→93 (regenerate+STA-20).
  - **Порядок:** pre-flight (collision-guard + факт-версии) → применить все статические ханки → единые v-бампы → **один** прогон `TemplateFragmentContractTest`+`InterviewMvcControllerTest` (закрывает 7 PENDING-TEST §9) → session-gated parity-QA (RES-15/STA-20/прочие поверхности) → леджеры + удаление исполненных под-планов.
  - Ссылки на детальные ханки (regenerate-removal-plan R0.115, res15-plan R0.114, §10 STA-20/AIR-6, §21 R0.117-сертификация).
- **Итог:** критический путь к VERIFIED теперь = один чеклист, а не переоткрытие плана из 4 источников; координация v-бампов зафиксирована (устранён риск двойного бампа/рассинхрона). Правок прод-кода не требуется (манифест read-only до подъёма app).
- **Дальше:** без app — весь известный статический/prep-пул закрыт (R0.109-118). Режим: мониторинг подъёма app + точечные микро-сверки при дрейфе. При подъёме postgres+app — исполнить `window-tick-manifest.md` одним окном. Нумерация R0.119+.

### R0.119 (2026-07-13) — Регресс-надзор фронта + подтверждение turnkey-статуса препов (app не нужен)

- **Единица (maintenance §28/§6F):** параллельная MCQ-сессия активно коммитит (HEAD сдвинулся), а turnkey-манифесты R0.114-118 держатся на точных строках/версиях. Проверить, не дрейфнул ли прод-фронт, иначе манифесты молча устареют.
- **Метод:** `git log` по фронт-путям (templates/static) + сверка фактических `?v=` и якорных строк препов.
- **Результат — чисто:**
  - **Фронт не тронут параллельной работой:** последние фронт-коммиты — все из frontend-потока (R0.64…R1.74 a11y/refactor, мои); `pedago(mcq)`-коммиты фронт НЕ трогают (остаются в seed/mcq + docs/mcq-quality). Регрессии от параллельной сессии нет.
  - **Версии совпадают с манифестом:** app.js=62 ✅, base.css=92 ✅, tokens=64 ✅.
  - **Якоря препов стабильны:** RES-15 `th:each rq` = `result.html:134` ✅; regenerate `regenerateQuestion` = `app.js:898` ✅ → turnkey-статус обоих препов **подтверждён неизменным**.
- **Мелкий фикс:** §9 stats.js — заметка `v=13` устарела, факт **v=15** (не влияет на манифест — stats.js там не бампается; правда ledger'а важна, как app.js v=56→62 R0.114). Исправлено.
- **Итог:** препы/манифест по-прежнему turnkey; параллельная сессия не создаёт фронт-регрессий (verified-clean). Правок прод-кода не требуется.
- **Дальше:** без app — периодический регресс-надзор (дёшево, защищает turnkey-препы от тихого дрейфа) + мониторинг подъёма app. При подъёме — `window-tick-manifest.md`. Нумерация R0.120+.

### R0.120 (2026-07-14) — Workflow adversarial static-аудит фронта: 1 новая confirmed находка (SET-21) + верификация полноты каталога (app не нужен)

- **Единица (ultracode workflow):** комплексный adversarial static-аудит текущего продакшн-фронта (templates/CSS/JS) на НОВЫЕ дефекты, которых нет в §10 — исполняет RESET-требование «recheck everything» систематически, а не по одной строке. 3 finder-измерения (dead-CSS / template-semantics / JS-dead+consistency) × adversarial-verify каждой находки (батчи ≤3, concurrency-лимит). 11 агентов, 785K токенов, 0 ошибок.
- **Результат: 8 сырых → 7 адверсариально опровергнуто → 1 confirmed новая.**
  - **Опровергнуто (валидация каталога):** 7 находок оказались уже-известными/deliberate — dead-AI-кластер (btn-regenerate/question-side/regenerateQuestion/API.REGENERATE CSS+JS = AIR-4/APP-8/R0.17/regenerate-removal-план), `.sr-only`-алиас (deliberate R0.79/80), learning-prefs dead-хендлеры (R1.56). Верификаторы независимо переоткрыли ровно те строки §10, что я вёл → **подтверждает точность и полноту каталога AI-fallout (R0.117) и §10**. Make-work отфильтрован адверсариально, не вручную.
  - **Confirmed новая — `SET-21`:** мёртвые CSS-правила `.settings-page .app-layout:has(> .personalization-card:not(.hidden))` (base.css:3200-3205 grid) + потомок `> .main-content` (3206). **Самопроверено:** `grep app-layout` templates+static/js = ровно 1 (`stats.html:8` под `.stats-page`); settings.html (`.settings-page`) не содержит `.app-layout` (0) → цепочка `.settings-page .app-layout` невозможна ни в одном DOM → правила не матчатся никогда. Остаток редизайна /settings (стопка+sidebar `.app-layout` → ARIA-вкладки, [[project_settings_tabs_redesign]]). P3, 0 render-риск. Прежние аудиты пропустили (settings-редизайн снял разметку, но не CSS) — ровно сирота, ради которой sweep и делался.
- **Записано:** §10 `SET-21` (⏸ WINDOW-ТИК, base.css cleanup + контракт-тест); внесён в `window-tick-manifest.md` (едет на общем base.css v=92→93 бампе с regenerate-removal+STA-20, +матрица пересечения). base.css НЕ правил сейчас (контракт-тест пиннит строки base.css → нужен gradle-прогон = window; консистентно R0.118).
- **Итог:** workflow дал реальную новую находку И независимо верифицировал полноту существующего каталога — двойная ценность. goal-шаг 6 (fix all) продвинут: +1 confirmed dead-CSS в очередь window-тика.
- **Дальше:** без app — периодический регресс-надзор + мониторинг подъёма. При подъёме — `window-tick-manifest.md` (теперь 5 единиц: regenerate-removal+STA-20+AIR-6+RES-15+SET-21). Нумерация R0.121+.

### R0.121 (2026-07-14) — SET-21 neighborhood-sweep: изолированная сирота, границы cleanup зафиксированы (app не нужен)

- **Единица (дешёвый inline follow-up к R0.120):** проверить, `SET-21` (`.settings-page .app-layout`) — сирота-одиночка или верхушка кластера мёртвых settings-CSS правил от редизайна /settings (стопка+sidebar → ARIA-вкладки). Без дорогого workflow — grep-sweep.
- **Метод:** извлёк все 48 `.settings-page`-scoped хуков base.css → grep каждого по templates+js на потребителя; для отсутствующих в settings.html — сверка через включаемые фрагменты + JS-динамику.
- **Результат — SET-21 ИЗОЛИРОВАНА:**
  - 0 хуков с нулём потребителей где-либо.
  - 6 хуков отсутствуют в самом settings.html: `.app-layout` (=SET-21, чужой-предок орфан); остальные 5 **живы** через includes/JS: `.stat-item/.stat-label/.stat-value` ← фрагмент `stats-grid.html` (settings.html:152), `.today-hero-due` ← `today-widget.html` (settings.html:10), `.export-status-error` ← JS-динамика (`app.js:462` `classList.toggle`).
  - → Fallout редизайна /settings ограничен ровно **одной** парой правил (3200-3206). Прочая settings-CSS консистентна.
- **Итог:** SET-21 де-рискнута — window-тик-cleanup чистит ровно 2 правила + комментарий, скрытых сиблингов нет. §10 SET-21 аннотирована. Правок кода не требуется. Подтверждает, что settings-редизайн (0eb762a5) был вычищен почти полностью — остался единственный CSS-хвост.
- **Дальше:** без app — регресс-надзор + мониторинг подъёма. Прочие поверхности с крупным недавним редизайном (напр. focus-релейаут) — кандидаты на аналогичный neighborhood-sweep будущими тиками. При подъёме — `window-tick-manifest.md`. Нумерация R0.122+.

### R0.122 (2026-07-14) — Обобщение neighborhood-sweep на ВСЕ page-скоупы: dead-CSS каталог полон, ноль новых сирот (app не нужен)

- **Единица (§16 «один refactor-area» — расширение техники R0.121 с `.settings-page` на все скоупы):** после того как SET-21 нашлась через wrong-ancestor sweep одного скоупа, применить ту же линзу ко ВСЕМ page-скоупам (`.focus-page/.result-page/.summary-page/.stats-page/.error-page`) — либо найдутся ещё скрытые чужой-предок сироты, либо каталог dead-CSS доказанно полон. Дёшево, inline (без workflow).
- **Метод:** (1) zero-consumer sweep — каждый page-scoped хук по templates+js на потребителя; (2) wrong-ancestor — хуки, отсутствующие в «своём» шаблоне, сверить через включаемые фрагменты + JS-динамику (`classList.add/toggle`) + Thymeleaf `th:classappend`.
- **Результат — ЧИСТО, ноль новых сирот:**
  - **0 хуков** с нулём потребителей в любом скоупе.
  - **Все «отсутствующие в своём шаблоне» хуки разрешились как живые:** `.result-page .stat-item/-label/-value` ← фрагмент `stats-grid` (включён в result.html); `.focus-page .action-footer/.keyboard-hint` ← фрагмент `training-actions` (focus-training:163); `.focus-page .is-answered` ← app.js:1704 (прячет опустевший training-actions, base.css:1018); `.option-other` ← result.html:69 (`th:classappend`) + app.js:1494 (focus JS); `.related-question(s)/-item/-title` ← result.html:132-134 (сервер) + app.js:1634-1637 (focus JS-reveal); `.is-disabled/.option-explanation/.option-status-muted` ← app.js JS-динамика.
  - **Единственная встреченная мертвечина = уже известная:** `.result-page .regenerating` (часть regenerate-кластера, regenerate-removal-план) и SET-21 (`.settings-page .app-layout`). Новых нет.
- **Итог:** wrong-ancestor техника, давшая SET-21, при обобщении на все скоупы даёт **пустое множество** → каталог dead-CSS §10 доказанно полон (SET-21 + regenerate-кластер — исчерпывающий список чужой-предок/th:if-мёртвых правил). goal-шаг 6 (fix all) продвинут верификацией полноты, а не новым фиксом. Правок кода не требуется.
- **Дальше:** без app — статический dead-CSS-фронт исчерпан на уровне page-скоупов; режим = регресс-надзор + мониторинг подъёма. При подъёме — `window-tick-manifest.md` (5 единиц). Нумерация R0.123+.

### R0.123 (2026-07-14) — Аудит референс-целостности CSS custom properties: чисто (app не нужен)

- **Единица (§16 «одна верификация» — новая статическая размерность корректности, ранее не sweep-ленная):** dead-CSS по селекторам закрыт (R0.122), но целостность **токен-графа** (`var(--X)` без определения `--X` → тихий фоллбэк на initial/inherited — реальный класс латентных багов) отдельно не проверялась. Детерминированный аудит: собрать все LHS-определения `--name:` + все `var(--name)` референсы по `tokens.css`+`base.css`, сдиффить.
- **Метод:** Python-скан обоих CSS; 99 определённых токенов, 84 референса; классификация referenced-but-undefined (по наличию fallback + рантайм-`setProperty`) и defined-but-unused.
- **Результат — референс-целостность ЧИСТА (0 реальных багов):**
  - **3 referenced-but-undefined — все не-баги:** `--color-accent` (base.css:3538) и `--token` (tokens.css:5 / base.css:35) — внутри **комментариев** (архитектурная проза / generic-плейсхолдер), не декларации → ложные срабатывания regex. `--focus-question-lines` (base.css:749) — `var(--focus-question-lines, 2)` **с фоллбэком**, в JS не задаётся (`setProperty` пусто) → всегда `2`, детерминированный min-height (deliberate «одинаковые окна вопросов» [[project_personalization_settings]]); спящий tuning-хук, 0 render-риска.
  - **18 defined-but-unused — deliberate:** полные шкалы (`--space-0/10/11/12`, `--z-base/-sticky/-overlay`, `--easing-bounce/-in`, `--duration-instant/-slower`, `--max-width-content/-wide`, `--elevation-popover`, `--color-status-info`/`-text-inverse`/`-bg-inverse`/`-accent-primary-active`) = токен-API design-system completeness. Churn против осознанной шкалы запрещён guardrail'ом; 0 render-эффекта.
- **Микро-нит (below-§10, window-gated):** комментарий base.css:3538 пишет `var(--color-accent)` — опечатка, реальный токен `--color-accent-primary`. Правка = коммент → но TemplateFragmentContractTest пиннит строки base.css → сдвиг строк требует gradle-прогона (window). Не стоит окна ради комментария; зафиксировано здесь для правды леджера, поедет попутно если base.css всё равно правится в window-тике.
- **Итог:** токен-граф целостен — ни одного `var()` не резолвится в пустоту (все «undefined» = комментарии/безопасные фоллбэки); неиспользуемые токены — намеренная полнота шкал, не мусор. goal-шаг 1 (recheck) + шаг 6 (fix all — подтверждено «нечего чинить») продвинуты. Правок кода нет.
- **Дальше:** без app — статические размерности (dead-CSS-селекторы R0.122 + токен-референсы R0.123) исчерпаны; режим = регресс-надзор + мониторинг подъёма. При подъёме — `window-tick-manifest.md`. Нумерация R0.124+.

### R0.124 (2026-07-14) — Аудит целостности ID-референсов (ARIA/for/href/form): чисто (app не нужен)

- **Единица (§16 «одна верификация» — 3-я нога статик-интегрити трилогии после dead-CSS-селекторов R0.122 и токен-графа R0.123):** висячая ссылка `aria-labelledby/-controls/-describedby`/`for=`/`href="#…"` на несуществующий ID — реальный user-facing баг (скринридер молча теряет связь метка↔контрол, `<label for>` не фокусирует). Это **integrity-класс**, НЕ переоткрытие закрытой WCAG-ветки R1.NN (не применяю новые критерии — проверяю разрешимость существующих ссылок).
- **Метод:** Python-скан всех `templates/**` + `static/js/*` — собрать ID-референсы (7 ARIA/HTML-атрибутов + `for=` + `href="#X"`) и ID-определения (статические `id=`, `th:id`-префиксы, JS-инъекции `id='…'`/`setAttribute`/конкатенация-wildcard); резолв каждого референса против объединения (учёт кросс-фрагментных ID и JS-построенных). 107 статических ID + 8 JS + 0 th:id.
- **Результат — целостность ЧИСТА (0 висячих ссылок):** все 4 «нерезолвнутых» кандидата = ложные срабатывания regex:
  - `form="filters"`/`form="session"` — regex `\bform=` поймал подстроку внутри `data-sidebar-form="…"` (settings.html:44/102); реальные формы имеют `id="filters-form"`/`id="session-form"` (определены). Custom data-атрибут, не HTML-`form=`-ассоциация.
  - `#i-<name>`/`#i-…` — оба в **комментариях** icons.html (12/24, док-плейсхолдеры). Реальные `<use href="#i-link">`-референсы иконок резолвятся в `<symbol id="i-…">` спрайта (в bad-списке их нет).
- **Итог:** каждая реальная ARIA/label/anchor-ссылка указывает на существующий ID (шаблон/фрагмент/JS) — статическая связность разметки цела. Завершает статик-интегрити трилогию: **dead-CSS-селекторы (R0.122) + токен-граф (R0.123) + ID-референсы (R0.124) — все чисты**. goal-шаг 1 (recheck) + шаг 6 (fix all — нечего чинить). Правок кода нет.
- **Дальше:** без app — статическая связность (CSS-селекторы/токены/ID-графы) исчерпана; режим = регресс-надзор + мониторинг подъёма. При подъёме — `window-tick-manifest.md`. Нумерация R0.125+.

### R0.125 (2026-07-14) — 🔓 РАЗБЛОКИРОВКА window-тика + исполнен SET-21 (первая прод-правка через новооткрытый app-независимый гейт)

- **Открытие (меняет blocker-анализ всего window-тика):** `TemplateFragmentContractTest` — **чистый JUnit** (нет `@SpringBootTest`/Testcontainers/БД): читает шаблоны/CSS через `ClassPathResource` и ассертит строки. Прогон `./gradlew :quiz-app:test --tests "*TemplateFragmentContractTest"` = **BUILD SUCCESSFUL за 2s, 10/10, без Docker/postgres**. → Гейт статических хунков window-тика **не требует postgres+app** (assumption манифеста «app up» был неверен) — нужен только gradle с мёртвым bootRun (wedge-риск отсутствует, app и так down). Проверено: `java 22`, bootRun-процесса нет.
- **Исполнено SET-21** (первая единица window-тика применена, app не понадобился):
  - Удалены base.css:3197-3206 — коммент (3197-3199) + 2 никогда-не-матчащихся правила `.settings-page .app-layout:has(> .personalization-card:not(.hidden))` (grid 3200-3205 + потомок `.main-content` 3206). Bump base.css **v=92→93** (head.html:311).
  - **Verify:** drift-check перед удалением — `.app-layout` ровно 1 (stats.html:8, `.stats-page`), `.settings-page .app-layout` ровно 2 правила (совпало с R0.120); после — `.settings-page .app-layout`=0, stats `.app-layout`=1 (живой не тронут). Контракт-тест на изменённом дереве: **10/10 green** (ассерт `id="personalization-card"` — про settings.html-шаблон, не CSS → не задет). 0 render-эффекта (правила и так не матчились).
- **Записано:** §10 SET-21 ⏸→✅ DONE. §9 base.css v=93.
- **Итог:** первая прод-правка Фазы E проведена — goal-шаг 6 (fix all) реально продвинут (не «нечего чинить», а confirmed cleanup закоммичен). **Важнее:** открытие app-независимости контракт-гейта переводит бо́льшую часть window-тика из «ждём подъёма app» в «исполнимо инкрементально по тику»: regenerate-removal (0 render-эффекта — элементы и так не рендерятся при aiEnabled=false), RES-15 (семантика списка), STA-20 (визуальная, но контракт td/share проверяем статически) — каждая единица = будущий тик через тот же гейт. AIR-6 (.java plumbing) отдельно — нужен render-тест (проверить, нужна ли ему БД).
- **Дальше:** следующий тик — исполнить следующую чисто-статическую единицу window-тика через app-независимый контракт-гейт (кандидат: **regenerate-removal**, 0 render-эффекта, или **RES-15**). Обновить `window-tick-manifest.md` под новую модель «инкрементально, без app». Нумерация R0.126+.

### R0.126 (2026-07-14) — Исполнен regenerate-removal: мёртвый AI-UI вырезан из result.html+app.js+base.css (app не нужен)

- **Единица (window-тик #2 через app-независимый контракт-гейт R0.125):** удалить мёртвый regenerate-функционал — кнопка/бейдж перегенерации + `.question-side` (все под `th:if="${aiEnabled}"`=false с 2026-07-07) + JS-хендлер + CSS. 0 render-эффекта (элементы не рендерятся при aiEnabled=false), поэтому parity-QA не нужна; гейт = контракт-тест (app-независим) + `node --check`.
- **Удалено (по `regenerate-removal-plan.md`, актуализ. R0.115, номера пересверены пост-SET-21):**
  - **result.html:** `<button class="btn-regenerate" th:if=aiEnabled>` + `<span class="regen-badge" th:if=regenCount()>0>` (из `.question-meta`, favorite+topic-badge оставлены); весь `<div class="question-side" th:if=aiEnabled…CODE…>` (код CODE-вопросов рендерится в `.answer`, не в сайде — RES-14); комментарий двухколонки актуализирован под single-column.
  - **app.js:** `API.REGENERATE`, вся `regenerateQuestion()` (JSDoc+тело), делегат-ветка `.btn-regenerate`; delegate-listener оставлен только для favorite. Хелперы (`obtainAdminToken`/`clearStoredToken`/`SERVER_TOKEN_UNSET_MARKER`/`buildNextQuestionHref`) НЕ осиротели — проверено, живут в export-пути. Бамп **v=62→63** (3 шаблона).
  - **base.css (6 хунков):** `.btn-regenerate:active` (групповой), 2-кол грид `:has(.question-side)` + `.question-side` flex, `.btn-regenerate` база/hover/`.regenerating` busy + `.result-page .regen-badge`, @media 859px collapse, @media 1200-1439 related×side override, `.btn-regenerate` в print/touch группе. Комментарии favorite («две»→«она») актуализированы. Бамп **v=93→94**. `.stats-page .regen-badge` (1891, общее с живым `.maturity-badge`) НЕ тронут — это STA-20.
- **Verify:** grep `btn-regenerate|regenerateQuestion|question-side|REGENERATE|.regenerating` по templates+static = **0** (кроме моего пояснит. комментария result.html:31). `node --check app.js` OK. Скобки base.css сбалансированы (784=784). `:has(.question-side)`=0. Контракт-тест **10/10 green**. Бэкенд `/api/regenerate` оставлен (§4, не наша фаза).
- **Записано:** §10 APP-8 ⏸→✅ DONE, AIR-4 ⏸→✅ DONE. §9 result.html/app.js (v=63) обновлены. Под-план `regenerate-removal-plan.md` удалён как исполненный. Манифест — regenerate-removal DONE.
- **Итог:** вторая единица window-тика проведена без подъёма app — крупнейший dead-AI-кластер (кнопка+бейдж+сайд+JS+6 CSS-правил) вырезан, goal-шаг 6 (fix all) существенно продвинут. Остаток window-тика: RES-15 (семантика списка, 0-render), STA-20 (визуальная — таблица stats), AIR-6 (.java plumbing — нужен render-тест). Parity-QA визуалов и EXAM-решение — по-прежнему app-gated.
- **Дальше:** следующий тик — RES-15 (0-render, app-независимый гейт) либо STA-20. Нумерация R0.127+.

### R0.127 (2026-07-14) — Исполнен RES-15: list-семантика «Похожие вопросы» (SSR + focus-динамика) (app не нужен для кода)

- **Единица (window-тик #3 через app-независимый контракт-гейт):** добавить программную list-семантику блоку «Похожие вопросы» — N ссылок рендерились как визуальная стопка без `role=list/listitem` (скринридер не объявлял «список из N»); отступление от собств. стандарта файла (соседний `.options` уже `role=list`, result.html:58). WCAG 1.3.1, единственный CONFIRMED из static a11y-workflow R0.101, P4 minor. Layout-риск NULL (R0.102: обёртка стилей не несёт).
- **Применено (по `res15-related-list-semantics-plan.md`, оба места одинаковой разметки):**
  - **result.html** (SSR /answer + no-JS фоллбэк): `<a th:each>` обёрнут в `<div class="related-questions-list" role="list">` + `role="listitem"` на `<a>`. h3-заголовок остаётся сиблингом списка (label секции через `aria-labelledby`, вне списка — корректно).
  - **app.js `renderRelatedQuestions`** (focus-динамика после ответа): та же обёртка `role=list` + `role=listitem`.
  - Бамп app.js **v=63→64** (3 шаблона) — отдельный от R0.126 (там уже занял v=63; RES-15 — новое содержимое → новый кэш-ключ). base.css НЕ тронут (обёртка без CSS → v-бамп не нужен).
- **Verify:** `role=list`/`role=listitem` присутствуют в обоих местах (result.html:125-126, app.js:1555/1557), консистентно с `.options`. `node --check app.js` OK. Контракт-тест **10/10 green** (версии app.js в 3 шаблонах согласованы = v=64).
- **Записано:** §10 RES-15 📋→✅ DONE. §9 result.html/app.js (v=64). Под-план `res15-related-list-semantics-plan.md` удалён как исполненный. Манифест — RES-15 DONE.
- **Остаток parity-QA (app-gated подтверждение, НЕ гейт корректности):** a11y-снапшот «список, N элементов» + визуальный 0-сдвиг обеих тем — рендерится только на POST /answer, поедет на session-окне с EXAM-parity. Риска нет (0-render, layout-анализ NULL).
- **Итог:** третья единица window-тика проведена без подъёма app — 3/5 закрыты (SET-21 R0.125, regenerate-removal R0.126, RES-15 R0.127). goal-шаг 6 продвинут. Остаток window-тика: STA-20 (визуальная — удаление stats-колонки, контракт td/share проверяем статически), AIR-6 (.java plumbing — нужен render-тест).
- **Дальше:** следующий тик — STA-20 (stats dead-колонка «Сброшено») либо проверить, нужна ли AIR-6-render-тесту БД. Нумерация R0.128+.

### R0.128 (2026-07-14) — Исполнен STA-20: удаление dead-колонки «Сброшено» из stats-таблицы (frontend, app не нужен)

- **Единица (window-тик #4 через app-независимый контракт-гейт):** удалить мёртвую колонку «Сброшено» (`regenSum`) из таблицы тем /stats — пост-AI (вырезан 2026-07-07 + wipe БД) она **всегда «—»** для всех тем → шум без информации. Визуальная правка → критично проверить позиционные зависимости перед удалением.
- **Pre-work (нашёл реальную ловушку):** stats.js-сортировщик использует `a.children[col]` (stats.js:295), где `col` = `th.dataset.col` — **прямой DOM-индекс ячейки**. `data-col`-значения (0,1,[Прогресс без col],3,4,5) = позиции. Удаление колонки «Сброшено» (позиция 4) сдвигает «Зрелость» на позицию 4, но её `data-col="5"` → `children[5]` стал бы undefined → **сортировка Зрелости сломалась бы**. Fix: перенумеровать Зрелость `data-col="5"→"4"`. Прочих позиционных/td-count зависимостей нет (grep stats.js: `children[]` только в sort; share/print stats ячейки не считает — SUM-7 это summary).
- **Применено:**
  - **stats.html:** удалён `<th data-col="4" …Сброшено>` (thead) + `<td data-label="Сброшено">…regenSum…</td>` (tbody); Зрелость `data-col="5"→"4"`.
  - **base.css:** снят селектор-строка `.stats-page .regen-badge,` из общего блока (`.maturity-badge` оставлен — он живой; комментарий «бейджи»→«бейдж зрелости»). Бамп **v=94→95**.
  - **DTO `regenSum` НЕ тронут:** покрыт API-тестами (`StatsApiMapperTest:54`, `InterviewControllerApiTest:157` — `@SpringBootTest`/DB-gated); вычисляемое-но-нерендерящееся поле безвредно. Удаление backend-test-gated → отдельно/опционально.
- **Verify:** `Сброшено`/`regen-badge`/`regenSum` в stats.html = 0; thead data-col = 0,1,3,4; tbody ряд = 5 ячеек (совпадает с 5 заголовками, `children[col]` выровнены); `.stats-page .regen-badge`=0 / `.maturity-badge` жив; скобки base.css 784=784; **контракт-тест 10/10 green**.
- **Записано:** §10 STA-20 ⏸→✅ DONE frontend. §9 stats.html + base.css (v=95). Манифест — STA-20 DONE. (Под-плана-файла нет — STA-20 был §10-строкой.)
- **Остаток parity-QA (app-gated, НЕ гейт корректности):** визуально таблица без колонки, responsive card-view 375/768/1280, share/print — на session-окне. Структурная корректность (cell-count, sort-index) уже верифицирована статически; риск низкий (чистое вычитание колонки).
- **Итог:** четвёртая единица window-тика — 4/5 закрыты (SET-21, regenerate-removal, RES-15, STA-20). Pre-work поймал реальный sort-баг (перенумерация), который наивное удаление внесло бы. Остаток window-тика: **AIR-6** (.java plumbing `generationUnavailable` — единственная оставшаяся; нужен render-тест, проверить требует ли БД).
- **Дальше:** следующий тик — AIR-6: сначала выяснить, требует ли `InterviewMvcControllerTest` (render-гейт для .java) Testcontainers-БД. Если да — AIR-6 app-gated; если нет (как контракт-тест) — исполнить. Нумерация R0.129+.

### R0.129 (2026-07-14) — Исполнен AIR-6: вычистка рудимента `generationUnavailable` (последняя единица window-тика → 5/5, app не нужен)

- **Единица (window-тик #5, финальная):** убрать мёртвую AI-plumbing `generationUnavailable` целиком — user-facing абзац удалён ещё в R0.112, оставался always-`false` флаг, протянутый через `FocusPageState`→model-attr→4 `!generationUnavailable and` guard'а (no-op, ветки уже корректно гейтятся reviewMode/сессией).
- **Пред-условие снято (ключевая находка тика):** проверил render-гейт `.java` — `InterviewMvcControllerTest` и `FocusTrainingPageServiceTest` оба на `@ExtendWith(MockitoExtension.class)` (mock'и repo/facade, **без `@SpringBootTest`/Testcontainers/БД**), как и app-независимый `TemplateFragmentContractTest`. → AIR-6 исполним статически, app **не нужен**. Это закрывает последнее «app-gated?»-сомнение по window-тику.
- **Применено (7 файлов):**
  - **focus-training.html:** снято плечо `!generationUnavailable and ` из 4 empty-state guard'ов (~190-193) + settingsPrimary-выражения; на месте — doc-комментарий (185-186), единственный оставшийся упоминатель флага (намеренно).
  - **FocusTrainingPageService.java:** удалены `boolean generationUnavailable = false;` (было :54), аргумент из `new FocusPageState(...)` (:94) и компонента `boolean generationUnavailable,` из record-определения (:147).
  - **MvcModelAttributeMapper.java:** удалён `model.addAttribute("generationUnavailable", …)` (:47).
  - **3 теста:** TemplateFragmentContractTest (3 pinned-строки без `!generationUnavailable and`), FocusTrainingPageServiceTest (2 ассерта `.generationUnavailable()` сняты), MvcModelAttributeMapperTest (2-й позиционный `false` снят в 3 конструкциях FocusPageState).
- **Ловушка (компилятор поймал, тесты подтвердили правильность фикса):** первый `compileTestJava` дал 5 ошибок `constructor FocusPageState cannot be applied` — в `MvcModelAttributeMapperTest` (42,68,116) и `InterviewPageMvcServiceTest` (74,99) конструкции передавали позиционный `false` **без имени поля**, потому grep по `generationUnavailable` их не нашёл. Fix: убран 2-й позиционный `false` (слот generationUnavailable) в каждой; т.к. флаг всегда `false` и удалённый слот совпал с удалённой компонентой record'а — остальные аргументы остались выровнены. Именно ради ловли этого запуск тестов был правильной верификацией.
- **Verify:** `generationUnavailable` в prod = только doc-комментарий (2 строки), в тестах = 0; **BUILD SUCCESSFUL**, 23 теста зелёные (Contract 10/10 + FocusTrainingPageService 2/2 + InterviewMvcController 3/3 + MvcModelAttributeMapper 4/4 + InterviewPageMvcService 4/4). `?v=`-бамп не нужен (только server-side Thymeleaf-логика + `.java`; кэшируемый ассет не менялся).
- **Записано:** §10 AIR-6 ⏸→✅ DONE. §9 focus-training.html-строка (рудимент снят). Манифест — AIR-6 DONE, **все 5 юнитов window-тика исполнены**.
- **Итог:** window-тик ЗАКРЫТ 5/5 (SET-21, regenerate-removal, RES-15, STA-20, AIR-6) — все исполнены статически без app, благодаря находке R0.125 (контракт-тест DB-free) + этой находке (render/mapper/service-тесты на Mockito). Остаток по этим единицам — только app-gated parity-QA (скрин+DOM-метрики), не гейт корректности.
- **Дальше:** следующий тик — открыть §9/§10 на предмет статически-разрешимых единиц вне window-тика (backlog-гипотезы §10, ⏸/◐-ячейки §9 без app-зависимости) ЛИБО подготовить parity-QA-план для session-окна. Нумерация R0.130+.

### R0.130 (2026-07-14) — Recheck-сверка §10: extra-analysis-семья (AIR-1/AIR-2/AIR-3/APP-9) закрыта по ground-truth кода (app не нужен)

- **Единица (цель-1 «recheck everything done», app down → нет parity-QA):** сверить фактическое состояние extra-analysis JS-флоу против §10-строк. Триггер — **расхождение памяти и §10**: memory говорит «AIR-1/RIA-2 применены р133», а §10 держал `AIR-1`/`AIR-3` = ⬜ RECHECK с «dead-end на каждый клик». §0 «не верь памяти» → установил ground-truth по коду.
- **Ground-truth (app.js прочитан):**
  - **Кнопка `extra-analysis-toggle` = related-only progressive-disclosure** (app.js:1630-1651). AI-разбор (takeaway/trace/feedback/comparison) удалён (коммент :1630). Кнопка показывается ТОЛЬКО при `hasRelated` (`!hardMode && data.relatedQuestions.length>0` из /api/answer); по клику `renderRelatedQuestions(data)` без сети + `aria-expanded=true` + self-hide; иначе `hidden`.
  - **0 fetch к удалённым эндпоинтам:** единственный `fetch(` = generic-хелпер (:59). API-константы (ANSWER/NEXT/STATS/TOPIC_STATS/CONFIDENCE/FAVORITE/STREAK) → **все живые контроллеры** (InterviewApiController :62/:73/:124 confidence/streak/favorite подтверждены). 0 `REGENERATE` (подтверждает R0.126).
  - **comparison-table builder ОТСУТСТВУЕТ:** grep app.js = 0 `createElement('th')`/`buildTable`/comparison → `APP-9` (scope на динамич. `<th>`) целится в несуществующий код.
  - Перепроводчик — коммит `c0b997ba` (2026-07-07, р133); §10 просто не обновляли.
- **Сверено (4 строки §10, no-code-change — код уже корректен):** `AIR-1` ⬜→✅ RESOLVED (dead-end устранён, реализована реком. B); `AIR-3` ⬜→✅ RESOLVED (related — штатное наполнение, не подавлено; a11y `role=list` из R0.127); `AIR-2` ◐→✅ RESOLVED (focus-toggle гейтится наличием контента, не `aiEnabled`); `APP-9` ⬜→✅ MOOT (comparison удалён). Decision A/B/C у AIR-1 — moot (B реализована).
- **Verify:** `node --check app.js` OK; grep-инварианты (0 dead-endpoint, 0 comparison-builder, 0 REGENERATE) подтверждены; live-контроллеры confidence/streak/favorite существуют. App-gated остаётся только визуальное подтверждение раскрытия related (не гейт корректности — поведение детерминировано из кода).
- **Итог:** крупнейший «предполагаемый» оставшийся user-facing дефект (dead-end кнопки) оказался УЖЕ устранён в р133 — леджер был устаревшим, не код. Расхождение memory↔§10 разрешено в пользу memory. Остаток §10 ⬜ RECHECK — преим. decision-gated (favorite APP-5/RES-3, IC-5 convention-3-варианта, RIA-2 live+decision, FT-15 sticky-дизайн) или app-gated.
- **Дальше:** следующий тик — либо `RIA-2` reverse-орфан `/study-confirm` (статическая часть: подтвердить 0 постящих + оформить как decision-ready), либо собрать turnkey parity-QA-план для session-окна (RES-15 a11y-снапшот, STA-20 визуал/share-print, extra-analysis related-раскрытие, §7 port-parity). Нумерация R0.131+.

### R0.131 (2026-07-14) — Recheck-сверка §10: RIA-2 `/study-confirm` RESOLVED по ground-truth (app не нужен); паттерн «р133 не сверён с леджером»

- **Единица (цель-1 recheck, продолжение R0.130):** сверить §10 `RIA-2` (reverse-орфан `POST /study-confirm` «ни один шаблон не постит, grep=0») против кода. Ожидание после R0.130 — возможна та же устарелость (memory: «RIA-2 применён р133»).
- **Ground-truth (focus-training.html прочитан):** §10-строка **СТАРАЯ**. STUDY learn-фаза теперь имеет полный UI:
  - div `study-learn-phase` (`:124`, гейт `studyLearnPhase and !flashcardMode`) → бейдж «Изучение», полный ответ (`studyAnswerHtml`), **submit-форма «Проверить себя» POST `@{/study-confirm}`** (`:129-131`).
  - `/answer`-форма (`:134`) взаимоисключающе гейтится `!studyLearnPhase` → в LEARN нет двойного пути ответа (задокументировано коммент :113-119).
  - **`/study-confirm` больше НЕ reverse-orphan** — grep `study-confirm` по templates теперь = форма :129 (было 0).
  - **CSRF консистентен:** ни одна из 6 POST-форм шаблона (`/finish`×2, `/flashcard-reveal`, `/flashcard-grade`, `/study-confirm`, `/answer`) не несёт явного `_csrf` — все на авто-инъекции `th:action` (Spring Security RequestDataValueProcessor). study-confirm не исключение → 403-риска нет.
  - Коммит-фикс — `c0b997ba` (2026-07-07, р133), кнопка-текст доуточнён `6d7a762c`.
- **Сверено (no-code-change):** §10 `RIA-2` ⬜ RECHECK / ⛔ DEFERRED → ✅ RESOLVED. Wiring полон (форма→живой роут `InterviewMvcController:157`→`applyStudyConfirm`); app-gated только визуал click-through STUDY (backend-поведение вне frontend-scope), не корректность проводки.
- **Паттерн (важно для планирования):** это **третья** стале-строка §10, закрытая ground-truth'ом за два тика (AIR-1/AIR-3 в R0.130 + RIA-2 здесь) — все три исправлены **одним коммитом `c0b997ba`/р133** (AI-fallout), но §10 тогда не сверили. Memory оказалась точнее §10 в каждом случае. → Оставшиеся ⬜ RECHECK §10 могут содержать ещё стале-resolved строки.
- **Дальше:** следующий тик — **систематический recheck остатка ⬜ RECHECK §10 против кода** (FT-15 sticky-CTA, SET-3 seg-preview, SET-9 a11y-оси, HEAD-7 chart.js, APP-5/RES-3 favorite, IC-5): для каждой — «стале-resolved / всё ещё decision-gated / всё ещё app-gated». Это добьёт точность состояния до maintenance-порога (цель 7). Нумерация R0.132+.

>  **⚠️ SUPERSEDED (см. §21 R0.100, 2026-07-13).** Блоки R1.73–R1.97 ниже — дублирующая переработка размерностей, уже закрытых в авторитетном раунде R0.75–R0.98, в устаревшей дорасет-нумерации. Оставлены как история (внутри — реальная прод-правка R1.74-FIX «Завершить сессию», закоммичена). Актуальный трекер конечной цели — матрицы §7/§9 и лог §21 R0.NN. Новых R1.NN не добавлять.

## R1.73 — WCAG 3.2.3 Consistent Navigation (AA) — verified-clean

Проверка: повторяющиеся навигационные механизмы в одинаковом относительном порядке между страницами.

Метод: единственный фрагмент `fragments/header :: header(...)` рендерят все 6 шаблонов; эмпирический DOM-снимок шапки на `/` и `/settings` (chrome-devtools).

Итог — чисто. На обеих страницах идентичны: порядок nav-ссылок (Фокус → Аналитика → Настройки), порядок тогглов (layout → design → theme), brand href `/`, skip-link `#main-content`. Меняется только `aria-current="page"` (переезжает на активную — корректно). Навигация консистентна по построению (единый источник-фрагмент). Правок не требуется. Леджер: cr.168.

## R1.74 — WCAG 3.2.4 Consistent Identification (AA) — FIX

Проверка: контролы с одинаковой функцией обозначены одинаково между страницами.

Находка: три кнопки `POST /finish` (завершить текущую сессию — одна функция) назывались по-разному — result/focus-training «Завершить сессию», settings «Завершить текущую сессию».

Фикс: `settings.html` `.btn-finish-small` → «Завершить сессию» (реконсиляция к мажоритарной метке 2/3). «текущую» избыточно: кнопка гейтится активной сессией и разведена с «Начать» глаголом-антонимом. Только текст; форма/id/action целы, cache-bust не нужен (шаблон).

Смежное verified-clean: skip-link'и намеренно различаются (описывают пункт назначения, дух 2.4.4 — не уравнивать); тогглы/бренд/back-to-top в общем фрагменте → консистентны по построению. Леджер: cr.169.

## R1.75 — WCAG 1.3.5 Identify Input Purpose (AA) — verified-clean по отсутствию

Проверка: поля, собирающие данные о пользователе, несут подходящий autocomplete (список 53 персональных Input Purposes).

Метод: перечислены все input/textarea/select по шаблонам, каждое сверено со списком.

Итог — чисто (нет применимых полей). Видимые поля: count (число вопросов), q (type=search), чекбоксы-фильтры, radio (ответ), select'ы — ни одно не персональные данные. Hidden-поля = состояние, вне scope. Приложение анонимное (нет логина/профиля/адреса/оплаты) → критерий выполнен по отсутствию; добавлять autocomplete нельзя (нет валидного токена). Попутно корректно: type=search, type=number min/max. Правок не требуется. Леджер: cr.170.

## R1.76 — WCAG 2.5.1 Pointer Gestures (A) — verified-clean

Проверка: path-based/multipoint жесты имеют одноточечную альтернативу.

Метод: grep JS на жестовые обработчики + шаблоны/CSS на slider/draggable + Chart.js на pan/zoom-плагин.

Итог — чисто (жестов нет). app.js/stats.js без touch*/pointer*/swipe/pinch/drag/mousedown-move — всё на одиночном клике/тапе. Нет type=range/draggable. Chart.js 4.5.0 без chartjs-plugin-zoom (только hover-тултипы; данные дублируются таблицей по R1.68). touch-action:manipulation лишь снимает double-tap-zoom, не жест. Правок не требуется. Леджер: cr.171.

## R1.77 — WCAG 1.3.4 Orientation (AA) — verified-clean

Проверка: контент не блокирует одну ориентацию (портрет/ландшафт).

Метод: grep CSS на @media(orientation)-ограничения, JS на screen.orientation.lock(), шаблоны на «поверните устройство», viewport-мета.

Итог — чисто (локов нет). Нет orientation-ограничений в CSS, нет lock() в JS, viewport = width=device-width,initial-scale=1.0 (без maximum-scale/user-scalable=no → обе ориентации + zoom). @media(max-width:859px) стекает разбор на портрете = reflow в поддержку, не лок. Правок не требуется. Леджер: cr.172.

## R1.78 — WCAG 2.4.11 Focus Not Obscured (Minimum) (AA, 2.2) — verified-clean

Проверка: фокус не скрыт целиком sticky/fixed-контентом.

Метод: инвентаризация всех position:sticky|fixed + эмпирический замер на /stats (rect фокуса vs sticky-thead, скролл страницы, развёртка таблицы).

Итог — чисто. masthead=relative (не sticky). Sticky thead таблицы инертен: контейнер .topic-table-wrap (overflow-y:auto, max-height:none) не скроллится внутренне ни свёрнутым, ни развёрнутым (319 строк) → страница скроллится, thead уезжает с ней (после scrollTo(0,1200) thead top=−43, к вьюпорту не липнет). Модалки fixed inset:0 = focus-trap (не перекрывают фон). back-to-top 44×44 в углу — full-width целиком не прячет. Sticky-рейки align-self:start в своей колонке. Латентно: если таблицу сделают внутренне-скроллящей — понадобится scroll-padding-top на обёртке (сейчас инертно, не добавляю). Правок не требуется. Леджер: cr.173.

## R1.79 — WCAG 3.3.7 Redundant Entry (A, 2.2) — verified-clean

Проверка: ранее введённые в процессе данные авто-подставляются, не запрашиваются повторно.

Метод: трассировка проброса фильтра/сессии по флоу settings→start→вопрос→answer→result→next + предзаполнение /settings.

Итог — чисто. Фильтр (topic/group/important/onlyWrong/shuffle/weakTopics/ordered) переносится hidden-инпутами settings→start (103-109) и вопрос→answer (focus-training 135-142), и query-параметрами result→next (result.html:106). Форма /settings предзаполнена (th:selected/th:checked/th:value отражают прежний выбор). Повторного ввода нет; персональных полей-дублей нет. Правок не требуется. Леджер: cr.174.

## R1.80 — WCAG 2.5.4 Motion Actuation (A) — verified-clean по отсутствию

Проверка: функции на движении устройства имеют UI-альтернативу и отключаемы.

Метод: grep JS/шаблонов на devicemotion/deviceorientation/DeviceMotionEvent/accelerometer/gyroscope/shake/requestPermission.

Итог — чисто (motion-функций нет). Ноль sensor-обработчиков, нет requestPermission-гейта. Все действия — явные UI-контролы + клавиатура. Критерий выполнен по отсутствию. Правок не требуется. Леджер: cr.175.

## R1.81 — WCAG 1.4.12 Text Spacing (AA) — verified-clean

Проверка: переопределение интервалов (LH 1.5 / para 2em / letter 0.12em / word 0.16em) не теряет контент.

Метод: инъекция тест-CSS 1.4.12 (chrome-devtools) на /, /settings, /stats при 1280 и 375px; замер горизонтального оверфлоу + обрезанных/широких боксов.

Итог — чисто. /,/settings,/stats: оверфлоу 0, широких боксов 0. Единственные «клипы» на /settings — visually-hidden SR-only заголовки (height:1px;overflow:hidden;absolute — паттерн скрытия, не потеря видимого текста, вне scope). Таблица /stats уходит в overflow-x:auto (допустимо). Макет флюидный, переживает override. Правок не требуется. Леджер: cr.176.

## R1.82 — WCAG 2.5.3 Label in Name (AA) — verified-clean

Проверка: доступное имя контрола содержит видимый текст метки.

Метод: grep button/a с aria-label + видимым текстом; эмпирическое вычисление visible vs accName (chrome-devtools), сверка code-points «A−».

Итог — чисто. Во всех случаях видимый текст ⊂ accessible name: stats «Применить фильтры»/«Искать» (подтв.), font-stepper «A−»(U+2212)/«A+» (глиф совпал точь-в-точь), grade «Не помню/…» ⊂ «Оценка N: …», CTA-ссылки (стрелка aria-hidden, R1.59). aside aria-label=Сессия — лендмарк, вне scope. Benign: grade-текст в суффиксе (вхождение выполнено, префикс не обязателен). Правок не требуется. Леджер: cr.177.

## R1.83 — WCAG 4.1.3 Status Messages (AA) — verified-clean

Проверка: статус-сообщения без фокуса программно определяемы (role=status/alert/aria-live).

Метод: инвентаризация aria-live/role=status|alert в шаблонах + трассировка динамических textContent-инъекций app.js к контейнерам.

Итог — чисто. Все динамические статусы в live-регионах: inline-alert (status↔alert по типу), #result-feedback (вердикт+штраф-нота, atomic), #personalization-status, #export-status, #font-scale-value, #filter-mode-hint (role=status), #table-sort-status (visually-hidden aria-live), chart-fallback. Намеренные исключения C29: стрик/счётчик без aria-live (иначе посекундный спам). Политенес по важности (assertive ошибки / polite инфо). Правок не требуется. Леджер: cr.178.

## R1.84 — WCAG 1.4.11 Non-text Contrast (AA) — частично чисто + design-tension (РЕШЕНИЕ ПОЛЬЗОВАТЕЛЯ)

Проверка: границы/индикаторы/фокус-кольца ≥3:1. Метод: эмпирический замер на /settings в обеих темах (chrome-devtools).

Проходит: фокус-кольцо 3.85 (light)/4.86 (dark), primary-кнопка 4.86, select 10.47 (но это UA-дефолт нативного select).

Ниже 3:1, НО осознанный design-choice: текст-инпут бордюр 1.48/1.37, ghost/secondary-кнопки 1.48/1.37 — из-за намеренного hairline `--color-border-primary` (спека «Cloud Light hairline») через все 11 дизайнов.

Вердикт — НЕ правлю (эскалация). Поднятие hairline до 3:1 сломало бы editorial-язык во всех темах (протокол запрещает менять осознанные решения молча). Смягчает: фокус-кольцо ОК, у инпутов label+заливка, 1.4.11 для помеченных полей мягок.

РЕШЕНИЕ ПОЛЬЗОВАТЕЛЯ (A/B): (A) сохранить hairline как есть; (B) поднять контраст границ ТОЛЬКО текст-инпутов/ghost-кнопок до ≥3:1 (отдельный --color-border-control), сохранив hairline для разделителей. Замеры = исходные данные. Кода не менял. Леджер: cr.179.

## R1.85 — WCAG 1.4.10 Reflow (AA) — verified-clean

Проверка: 320 CSS-px (≈400% zoom) → одна колонка без горизонтального скролла.

Метод: CDP-эмуляция 320×640; замер scrollWidth−clientWidth + поиск боксов шире вьюпорта вне допустимых исключений (overflow-x:auto/pre/table).

Итог — чисто. /,/settings,/stats: горизонтальный оверфлоу 0, нарушителей 0. Таблица тем /stats реформатируется в стек (data-label-карточки, overflow-x:visible) — 2D-скролл не понадобился. Ограничение: result/session-summary POST-гейтед (не мерил, наследуют те же base.css-правила). Правок не требуется. Леджер: cr.180.

## R1.86 — WCAG 1.4.13 Content on Hover or Focus (AA) — verified-clean

Проверка: hover/focus-контент dismissable/hoverable/persistent.

Метод: grep кастомных тултипов/поповеров + анализ :hover/:focus-within раскрытий CSS + учёт native title.

Итог — чисто. Кастомных тултипов НЕТ (все aria-describedby → постоянно видимые статичные подсказки, вне scope). Единственный revealed-контент — .code-copy-btn: hoverable ✓ (потомок обёртки), persistent ✓ (без таймаута), dismissable ✓/N-A (в паддинг-жёлобе pre, код не перекрывает), на touch всегда видима. Native title (31) — UA exempt (R1.67). Ограничение: код POST-гейтед, вердикт по dismissable детерминирован из CSS. Правок не требуется. Леджер: cr.181.

## R1.87 — WCAG 2.5.8 Target Size (Minimum) (AA, 2.2) — verified-clean

Проверка: цели ≥24×24px ЛИБО достаточный интервал (24px-круг не пересекает соседей).

Метод: эмпирический замер bbox всех интерактивных контролов на /, /settings (3 вкладки), /stats; для radio/checkbox эффективная цель = label; замер center-to-center интервалов.

Итог — чисто. / — 0 <24px (радио 1×1 → label-опция ≥44px). /settings все вкладки — 0 <24px (seg-control/font-stepper 32px/чекбоксы-label/кнопки ≥24). /stats — 12 ссылок-тем (17px) проходят spacing-исключением: вертикальный интервал 56-57px ≥24, горизонтально единственная цель в строке. Правок не требуется. Леджер: cr.182.

## R1.88 — WCAG 1.3.1 Info and Relationships (A) — verified-clean (таблица /stats)

Проверка: структура/отношения переданы программно (scope/caption/aria-sort).

Метод: разметка + stats.js sort-логика + эмпирика aria-sort в браузере (клик = клиентская DOM-сортировка, без серверной мутации).

Итог — чисто. Таблица тем: caption (visually-hidden), обёртка role=region+aria-label+tabindex, th scope=col/row, сортируемые заголовки tabindex+aria-keyshortcuts+aria-sort (PE: без JS неинтерактивны), click+keydown(Enter/Space). Эмпирика: клик «Всего» → aria-sort=ascending, остальные none, live-регион анонсирует. Несортируемая «Прогресс» без интерактивных атрибутов. Покрывает 4.1.2/2.1.1. Прочие 1.3.1 (заголовки/списки/label) — в R1.69/R1.70. Правок не требуется. Леджер: cr.183.

## R1.89 — WCAG 4.1.2 Name, Role, Value (A) — verified-clean (seg-control /settings)

Проверка: у кастомных виджетов программно определяемы name/role/value + уведомление об изменении.

Метод: эмпирическая инспекция 6 seg-control радиогрупп + тест смены значения (aria-checked/roving-tabindex/применение) в браузере (клиентская pref, не БД).

Итог — чисто. Name: role=radiogroup+aria-labelledby (Дизайн/Тема/Раскладка/Ширина/Плотность/Движение). Role: все опции role=radio. Value: aria-checked, ровно один checked, roving-tabindex=выбранная. Тест «Плотность»: клик Компактно → aria-checked+tabindex переехали, html data-density=compact применился; восстановлено к дефолту. Прочие виджеты (tab/font-stepper/тогглы) — в R1.64/settings-tabs. Правок не требуется. Леджер: cr.184.

## R1.90 — WCAG 3.1.2 Language of Parts (AA) — verified-clean

Проверка: иноязычные фрагменты несут lang, кроме exempt (имена/термины/код).

Метод: html lang + grep англ. UI-фраз + эмпирический скан прозы / на цельные англ. пассажи + баланс кир/лат.

Итог — чисто. html lang=ru на всех 6 шаблонах; англ. UI-предложений нет; проза / : 0 длинных англ. пассажей, кириллица 88% (латиница = изолированные термины/идентификаторы, exempt); контент русифицирован LANE 5/6 (verify_russify.py); английский только в exempt-категориях (термины/имена/код). Вложенной lang-разметки нет и не требуется. Ограничение: пост-ответная проза POST-гейтед (из того же LANE 6-корпуса). Правок не требуется. Леджер: cr.185.

## R1.91 — WCAG 2.4.7 Focus Visible (AA) — verified-clean (сортируемые th /stats)

Проверка: видимый фокус-индикатор у th[tabindex=0].sortable (новый элемент, не в R1.62).

Метод: grep outline-правил + эмпирический фокус в браузере (:focus-visible + computed outline).

Итог — чисто. base.css:1870 th.sortable:focus-visible → outline 2px solid accent, offset −2px (инсет, чтобы не обрезалось ячейкой). Эмпирика: matches(:focus-visible)=true, outline 2px solid rgb(217,119,87) рендерится. Контраст кольца ≥3:1 (R1.84). Бонус ⇅-афорданс через ::after. Прочие фокусируемые — в R1.62. Правок не требуется. Леджер: cr.186.

## R1.92 — WCAG 1.4.1 Use of Color (A) — verified-clean

Проверка: цвет не единственный носитель информации.

Метод: аудит информативных цвето-состояний в шаблонах+CSS на нецветовой дубль.

Итог — чисто. Вердикт: иконка+текст «Верно!»/«Неверно». Опции: текст «Правильный ответ»/«Твой выбор»+aria. Точность/зрелость: цвет+ЧИСЛО+title. Навигация: border-bottom+aria-current. Seg-btn active: фон-заливка (fill, не hue)+aria-checked. MCQ-выбор: рамка+wash+буква-бейдж+aria-checked. Бонус: @media(forced-colors) → Highlight/HighlightText для Windows HC. Правок не требуется. Леджер: cr.187.

## R1.93 — WCAG 2.1.4 Character Key Shortcuts (A) — verified-clean (намерение) + минорная заметка

Проверка: одноклавишные шорткаты отключаемы/переназначаемы/active-on-focus.

Метод: аудит обработчиков + эмпирический тест перехвата (диспатч ?// в поле поиска и вне).

Итог — намерение SC выполнено. 1-9: фокус-гейт form.contains (опция active-on-focus). ?//: guard по [INPUT,TEXTAREA,SELECT] до обработки — эмпирика: в поле не сработали, вне поля ? открыл справку; недеструктивны/обратимы. ArrowUp/Down/Esc — не печатные символы. Минорно (не правлю): ?// глобальны без явного turn-off/remap (строгая буква); вред минимален, паттерн стандартный, тоггл = новая config-ось (user-gated). Кода не менял. Леджер: cr.188.

## R1.94 — WCAG 3.2.2 On Input (A) — verified-clean

Проверка: изменение контрола не вызывает авто-смену контекста без предупреждения.

Метод: аудит change-обработчиков app.js/stats.js + эмпирика (смена mode-select на /settings, замер URL/навигации).

Итог — чисто. toggleTopic (shuffle/ordered change) — только disabled смежных полей + live-подсказка (не контекст). modeSelect change — метка кнопки+дефолт count (эмпирика: TRAINING→STUDY, URL не изменился, метка обновилась). countInput — валидация. Персонализация — презентация (не контекст). Навигация/сабмит только в onclick/onkeydown явной активации. Правок не требуется. Леджер: cr.189.

## R1.95 — WCAG 1.3.2 Meaningful Sequence (A) — verified-clean

Проверка: DOM-порядок = осмысленная последовательность чтения на wide/split-раскладках при линеаризации (SR / CSS-off).

Метод: grep base.css на `order:`/`flex-*-reverse` (0 совпадений) + разбор 4 wide-раскладок + эмпирика на /stats (DOM-порядок детей vs bounding-rects, masthead).

Итог — чисто. Единый механизм: `grid-column`/`grid-row` без CSS `order` и без reverse → DOM-поздний элемент всегда визуально правее/ниже. result: card(col1,DOM-1)→related(col2,DOM-позже). focus split: мета→вопрос→код(col1)→форма-ответы(col2)→вердикт/контролы(full-width row6/7) = вопрос перед ответами. stats: таблица col1 / forecast+gaps col2 (при отсутствии данных грид неактивен, поток одноколоночный top 158→1500). summary: score-areas + таблица(col1)/ошибки(col2)/рекомендации(футер). masthead: brand→actions L→R. Правок не требуется. Леджер: cr.190.

## R1.96 — WCAG 2.4.3 Focus Order (A) — verified-clean

Проверка: фокус приходит в порядке, сохраняющем смысл/операбельность.

Метод: grep всех tabindex (положительных 0) + roving-паттерны + пост-ответный фокус + эмпирика tab-порядка на / (flow).

Итог — чисто. Положительного tabindex нет → tab = DOM-порядок (осмысленность из R1.95). Эмпирика flow: skip-link→бренд→nav(L→R)→тогглы(L→R)→варианты(сверху вниз), монотонно. Roving tabindex (seg×7, tablist, sortable th) — один tab-stop на виджет, корректно. Пост-ответ: фокус вперёд на вердикт (feedbackDiv focus()), CTA «дальше» перенесён ниже разбора. Focus-trap оверлеев корректен. Правок не требуется. Леджер: cr.191.

## R1.97 — WCAG 2.4.6 Headings and Labels (AA) — verified-clean

Проверка: заголовки/подписи описывают тему; иерархия h1–h6 без пропусков.

Метод: эмпирика на focus/settings/stats (все h1–h6 + accessible-name контролов/radiogroup + заголовки колонок).

Итог — чисто. focus: h1→h2(вопрос)→h3(Горячие клавиши). settings: h1 «Настройки сессии»→h2(вкладки)→h3(секции); 6 radiogroup с описательными именами. stats: h1 «Аналитика»→h2(6 секций)→h3; колонки Тема/Всего/Прогресс/Точность/Сброшено/Зрелость. Везде один h1, 0 пропусков уровней, 0 неподписанных контролов. result/summary — по шаблону описательны (кросс-проверка в workflow-аудите). Правок не требуется. Леджер: cr.192.
