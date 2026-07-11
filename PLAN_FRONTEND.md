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
| `FOCUS` | Главная / тренировка / вопрос | `focus-training.html` | `focus-question.html` | flashcard; MCQ; selected; correct; wrong; explanation; session; empty branches; long/code-heavy | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |
| `RESULT` | Результат ответа / no-JS fallback | `result.html` | `result.html` | correct; wrong; explanations; related; extra controls; loading/error; no-JS | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |
| `SUMMARY` | Итоги сессии | `session-summary.html` | `session-summary.html` | score; mistakes; recommendations; empty; share; print; long table | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |
| `SETTINGS` | Настройки | `settings.html` | `settings.html` | session; appearance; data; validation; saved feedback; reset dialog; danger states | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |
| `STATS` | Аналитика | `stats.html` + `stats.js` | `stats.html` | normal; cold start; no match; charts; long table; collapsed/expanded; sort; print | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |
| `ERROR` | Ошибки 4xx/5xx | `error.html` | `error.html` | 400; 401; 403; 404; 5xx; developer disclosure; recovery | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |
| `SHELL` | Глобальная оболочка / header / navigation | `fragments/header.html` + `head.html` | `shell-header.html` | desktop; mobile nav; themes; designs; skip link; back-to-top; no-JS | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |
| `OVERLAYS` | Dialogs / popovers / dropdowns / confirmations | templates + `app.js` triggers | `overlay-state-atlas.html` | admin reset; confirmations; menus; popovers; tooltips; any discovered overlay | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |
| `FEEDBACK` | Alerts / toasts / status / validation | fragments + templates + `app.js` | `feedback-state-atlas.html` | assertive; polite; success; warning; error; pending; validation; offline/permission if reachable | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |
| `COMPONENTS` | UI primitives and all states | `base.css` + fragments + JS | `component-state-atlas.html` | default; hover; active; focus; disabled; loading; selected; checked; expanded; dark; long content | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Перепроверить наличие и полноту |

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
| `focus-training.html` | page | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | FT-1..18 закрыты; 48px h2 + neutral-selected = 🚫 |
| `result.html` | page | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; RES-14 снят (false-positive: код всегда в `.answer:82`, `.question-side`=избыт.пин AI-режима), RES-8 контраст AA все 20; RES-3 favorite=⛔ |
| `settings.html` | page | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | SET-14/17 р134; SET-7 р136; SET-6/SET-5 verified-clean р137 |
| `stats.html` | page | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | STA-18 р134; STA-19 р135; STA-5/STJ-1 labels р137 |
| `session-summary.html` | page | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; dual-path nav = deliberate (SUM-3); score-card h2 добавлен р104 (SUM-9) |
| `error.html` | page | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; 403 авто-retry = ⛔ERR-3 |
| `fragments/head.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; CDN-guards, 6 осей персонализации до 1-го кадра |
| `fragments/header.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | HDR-1 закрыт р138 (mobile focus title + session compact; sticky отвергнут) |
| `fragments/icons.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | 🌱ICO-3 нет warning-иконки для warn/error (low) |
| `fragments/mermaid-init.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | MER-1 🚫 внешний owner; сам код чист (antiscript-guard) |
| `fragments/inline-alert.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; role=alert+assertive (IAL-1) |
| `fragments/post-answer-controls.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; feedback polite + sink под кнопкой (PAC-1..2) |
| `fragments/result-zone-head.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; zone-chip+hint (RZH-1) |
| `fragments/stats-grid.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; accuracy-bar aria-hidden (SGR-3); cold-start = 🚫 |
| `fragments/today-widget.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; «N из M к повтору» (TDW-1); streak без aria-live = 🚫 |
| `fragments/training-actions.html` | fragment | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | Чисто; TAC-3 keyboard-hint контраст ✅ verified р107 (min 5.93:1); timer/hint без aria-live = 🚫 |
| `css/tokens.css` | style | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | TOK-1 AA-гейт CLEAN, TOK-5 OS-prefs; TOK-2 accent≈semantic hue = 🚫 identity, mitig. not-by-color-alone |
| `css/base.css` | style | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | BAS-15 tabs↔seg verified-clean р137; BAS-21 р134; BAS-18 measure = 🚫 |
| `css/editorial.css (мёртв)` | style | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | 🌱EDC-1 subset-proof ✅ р109: pre-split монолит, 9 uniques = box-sizing-дубли + обсолет pre-tabs-разметка → чистый историч-дубль, SAFE-TO-DELETE (ждёт ГО юзера, НЕ stranded как DSG-1) |
| `css/{linear,swiss}.css (не подключены)` | style | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | 🌱DSG-1 ❌КОРР.р99: НЕ чистые дубли — linear держит `a`/`a:hover`, swiss `.ed-masthead-kicker`/`.ed-eyebrow` НЕ в base.css (частичный порт, как broadsheet) → port-or-abandon decision, НЕ удалять как junk |
| `css/broadsheet.css (не дубль!)` | style | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | 🌱DSG-2 CONFIRMED: 13 структ.правил (nav/btn/link) НЕ в base.css (0 vs linear 2/swiss 6) → broadsheet теряет структ.акценты, токены живут |
| `js/app.js` | script | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | 🌱APP-8 regenerate без aria-busy; 🌱APP-9 comparison-table th без scope/caption; ⛔APP-5 favorite; v=56 |
| `js/stats.js` | script | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | STJ-1 labels/grid закрыт р137; kbd/progress-dup c app.js = 🚫 standalone-by-design; v=13 |

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
| `FT-2` | `.question-zone-head` chip+hint (`focusModeChipText/HintText`) | Серверная микрокопия режима | Уточнять per-mode формулировки точечно | 🌱 BACKLOG | ⬜ RECHECK |
| `FT-7` | `.focus-question` h2 48px | Намеренный размер «окна вопроса» | НЕ трогать | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `FT-12` | option fairness: длина/наполненность выдаёт correct | Контент-перекос; UI обязан не усиливать | seed/mcq pedago-loop | 🏛 CONTENT | 🏛 EXTERNAL_OWNER |
| `FT-15` | `training-actions` include (Проверить ответ) | CTA далеко при длинных вариантах | sticky/видимый submit | 🌱 BACKLOG | ⬜ RECHECK |
| `FT-17` | `app.js(v=51)` в конце body | Версионный контракт JS | бампать при правке app.js | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `RES-3` | `.btn-favorite` (звезда, aria-pressed) | Избранное — флоу под вопросом юзера | НЕ трогать без решения | ⛔ DEFERRED | ⬜ RECHECK |
| `SUM-5` | `role=rowheader` на `<td>` темы | Намеренно: share-JS читает `querySelectorAll('td')` | НЕ менять структуру td | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `SUM-7` | share-script `buildShareText` | Контракт: ≥5 `<td>` на строку, recs из `li` | НЕ ломать селекторы | 🔁 ONGOING (контракт) | 🔁 CONTRACT_MONITOR |
| `SET-3` | Design seg-control без preview | Краткое описание/preview пресета при выборе | M | 🌱 BACKLOG | ⬜ RECHECK |
| `SET-9` | a11y-оси разбросаны | Сгруппировать движение/контраст в раздел Accessibility | M | 🌱 BACKLOG | ⬜ RECHECK |
| `SET-12` | `app.js(v=51)` | Версионный контракт | — | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `SET-15` | `/start` форма: `count` hardcode `value="20"` шэдоуит серверный per-mode дефолт (marathon=50) | **Forms-UX аудит р102 (loop §3).** `settings.html:125` `<input name=count value="20">` — жёсткая 20. Форма ВСЕГДА шлёт count (20 или localStorage-restore), а сервер применяет per-mode дефолт ТОЛЬКО при `requestedCount==… | Выровнять FE к BE per-mode дефолтам | 🌱 BACKLOG (blocked-file) | ⬜ RECHECK |
| `STA-3` | Поиск отделён от фильтров линией | ~~Объединить поиск+фильтры~~ — НЕ дефект: вертикальный hairline + верт.центрирование поиска = осознанное решение с rationale `base.css:1476-1480` (одно поле vs высокий фильтр → пустота под полем как намеренный воздух).… | — | 🚫 WONTFIX (deliberate) | 🚫 DECISION_LOCK |
| `STA-10` | CTA из аналитики (тренировать слабые/ошибки) | Кнопки-переходы — может требовать роутов/параметров | M | ⛔ DEFERRED (проверить контракт) | ⬜ RECHECK |
| `STA-12` | `stats.js(v)` в stats.html | Версионный контракт stats.js | — | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `HEAD-1` | 2 CSS-линка `?v=N` (=53) | Контракт кэш-инвалидации при правке CSS | бампать ОБЕ строки | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `HEAD-4` | font-loading `media=print`/onload + noscript | Намеренный нерендер-блокирующий flip | НЕ «чинить» | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `HEAD-7` | self-host chart.js vs CDN (CSP sourcemap) | Архитектурный + download-gated выбор | — | ⛔ DEFERRED | ⬜ RECHECK |
| `ICO-3` | нет dedicated warning-иконки (danger исп. `#i-flag`). **Уточнено р110:** `#i-flag` = double-duty на… | Намеренный reuse flag для danger-zone; distinct warning-символ = опц. визуал-полиш, только с IC-4 + требует settings.html (blocked). Не срочно | L | 🌱 BACKLOG (decorative, opt-polish с IC-4) | ⬜ RECHECK |
| `IC-5` | CTA-кнопки несут сырой юникод «→» (SR-шум + вне icon-системы). **Точный скоуп (р38-аудит):** 7 `btn… | **⚠️ БЛОКЕР — convention-решение (р38):** не механический swap. Все icon-кнопки ВЕДУТ иконкой (icon-lead, R34-конвенция); directional «next→» семантически ТРЕЙЛИТ → iconify создаёт mixed lead/trail. 3 варианта, каждый t… | M | 🌱 BACKLOG (design-decision; 2.4.4 CLEAN р117) | ⬜ RECHECK |
| `MER-1` | antiscript guard + drop при отсутствии mermaid | XSS-поверхность 'loose' закрыта; offline degrade | mermaid — внешний owner | 🚫 WONTFIX (не трогать mermaid) | 🚫 DECISION_LOCK |
| `TDW-3` | streak-inline (🔥) без aria-live | Намеренно: наполняется при каждой загрузке, не state-change | НЕ добавлять live-region | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `BAS-18` | prose `--measure` full-width | Намеренный выбор юзера (259ch на 2560) | НЕ трогать | 🚫 WONTFIX | 🚫 DECISION_LOCK |
| `DSG-1` | `linear.css` + `swiss.css` НЕ подключены ни одним `<link>` (свер. 2026-07-06/07: `head.html` грузит… | **❌ КОРРЕКЦИЯ 2026-07-07 (р99): НЕ «чистые дубли».** Прежняя запись «сигнатуры УЖЕ портированы, удалить» — НЕВЕРНА. Селектор-уровневый diff overlay↔base.css: **`linear.css` держит `html[data-design=linear] a` + `a:hover… | **НЕ механический cleanup.** Удаление меняет 0 рендера (overlay не подключён), но теряет source восстановления fidelity. Решение port-or-abandon (как DSG-2): л… | 🌱 BACKLOG (port-or-abandon decision, как DSG-2) | ⬜ RECHECK |
| `DSG-2` | **CONFIRMED статически (2026-07-07, app не нужен):** `broadsheet` теряет структурную identity. `bas… | При выборе `broadsheet` **токены рендерятся** (cream bg / obsidian buttons / serif — из `tokens.css` `[data-design=broadsheet]`×2), но 13 структурных акцентов ОТСУТСТВУЮТ → broadsheet = «свои токены на editorial-структу… | Портировать 13 rule-блоков из `broadsheet.css` в `base.css` (token-safe, ровно как сделали для linear/swiss) → затем `broadsheet.css` становится дублем → удали… | 🌱 BACKLOG (blocked-file + decision) | ⬜ RECHECK |
| `APP-5` | favorite toggle (`btn-favorite`) | Флоу под решением юзера | НЕ трогать без решения | ⛔ DEFERRED | ⬜ RECHECK |
| `APP-7` | `?v=` контракт в 3 шаблонах | Бампать при правке app.js | — | 🔁 ONGOING | 🔁 CONTRACT_MONITOR |
| `APP-8` | `regenerateQuestion` (app.js:1047-1115) async-state НЕ консистентен с братьями-хендлерами | Extra-analysis (343/348: `aria-busy=true` + re-entrancy guard) и export (592-601: `aria-disabled`+`aria-busy`) выставляют busy-семантику и гард повторного клика; regenerate же only `.regenerating`-класс + `title='Удаляю… | L | 🌱 BACKLOG (blocked-file) | ⬜ RECHECK |
| `APP-9` | JS-построенная comparison-table (app.js ~стр.384/1819, extra-analysis разбор) — `<th>` без `scope`,… | Аудит 2026-07-06: динамически собираемая таблица сравнения в доп.анализе рендерит `th` без `scope="col"/"row"` и без `caption` → SR не связывает заголовки со строками, теряется навигация по таблице (в отличие от SSR sum… | L | 🌱 BACKLOG (blocked-file, verify) | ⬜ RECHECK |
| `AIR-1` | «Показать доп. анализ» → гарантированный dead-end на КАЖДЫЙ клик | `app.js` flow (340-460): `TAKEAWAY` (395) и `CODE_TRACE` (410) пушатся **безусловно** → `requests.length ≥ 1` даже на верном ответе (на неверном +`WRONG_FEEDBACK` 358 +`COMPARISON` 376). Все 4 endpoint'а **удалены** → к… | Реком. B (сохраняет единственную выжившую ценность — related) | 🌱 BACKLOG (blocked-file + decision A/B/C) | ⬜ RECHECK |
| `AIR-2` | оба тоггла доп.анализа НЕ гейтятся `aiEnabled` | `#extra-analysis-toggle` (`post-answer-controls.html:9`, focus-флоу) и `#extra-analysis-toggle-result` (`result.html:113`, /answer) рендерятся **безусловно** (в отличие от `.btn-regenerate` result.html:49 и `.question-s… | Вместе с AIR-1 | 🌱 BACKLOG (blocked-file) | ⬜ RECHECK |
| `AIR-3` | выжившие «Похожие вопросы» подавлены битым флоу | reveal `related.classList.remove('hidden')` (`app.js:447`) — в ветке **НЕ-полного-провала**; т.к. все fetch'и падают (AIR-1), ветка недостижима → единственная выжившая доп.аналитика (related-вопросы, backend `RelatedQue… | Вместе с AIR-1 (реком. B) | 🌱 BACKLOG (blocked-file) | ⬜ RECHECK |
| `AIR-4` | `regenerateQuestion` (app.js:1047-1115) зовёт удалённый `POST /api/regenerate` | Хендлер + делегирование от `.btn-regenerate` (1126) живы в JS, но сама кнопка под `th:if=${aiEnabled}` (result.html:49) → в seed-first **не рендерится** → путь **недостижим** (не user-facing). Мёртвый JS к будущей чистк… | Удалить при чистке app.js (низкий приоритет) | 🌱 BACKLOG (blocked-file, dead-not-reachable) | ⬜ RECHECK |
| `AIR-6` | мёртвая AI-ветка в empty-state focus-training | `focus-training.html:166` — `<p th:if=${generationUnavailable}>«AI временно недоступен. Обнови страницу через несколько секунд.»`. **`generationUnavailable` хардкод `false`** (`FocusTrainingPageService:54`, без переприс… | Удалить при AI-cleanup | 🌱 BACKLOG (blocked-file, dead-not-rendered) | ⬜ RECHECK |
| `RIA-2` | reverse-орфан: `POST /study-confirm` без вызывающего | Живой маршрут `studyConfirm` (`InterviewMvcController:157` → `InterviewFlowMvcService:49` → `applyStudyConfirm` → focus-redirect), но **НИ один шаблон/JS не постит на него** (grep `study-confirm` по static+templates = 0… | Live-verify STUDY + решение пользователя | ⛔ DEFERRED (needs live+decision, out-of-frontend-scope) | ⬜ RECHECK |

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
