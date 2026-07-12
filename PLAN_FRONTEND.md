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
