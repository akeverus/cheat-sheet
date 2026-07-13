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
