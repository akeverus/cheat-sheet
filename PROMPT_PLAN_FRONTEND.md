/loop 10m

# FINALIZATION OVERRIDE

Ты работаешь не в бесконечном redesign/maintenance-loop, а в конечном FINALIZATION ROUND.
Цель — довести проект до FINALIZED и остановить loop.

Не спрашивай пользователя, какое направление выбрать. Следующая задача всегда выбирается
из FRONTEND_STATE.json по P0→P1→P2→P3, dependency impact и core-flow priority.

Один тик = один связный work package, который включает подтверждение проблемы,
implementation, targeted tests, live QA, evidence и atomic commit. Можно менять frontend,
целевые backend/API/DB contracts и tests, если это необходимо для UX-flow, изменение
минимально, безопасно, мигрируемо и покрыто тестами. seed/mcq content не менять без
отдельной задачи content pipeline.

Не проводи audit-only тики подряд. Если найден исправимый дефект — исправь его в том же
work package. Не ставь VERIFIED/FINALIZED до deep-pass и устранения всех blocking/user-visible
дефектов.

Решения по обратимым UI/UX вопросам принимай самостоятельно на основе PRODUCT.md,
DESIGN.md, accessibility и минимального риска. Пользователя спрашивай только при реальной
необратимости, потере данных, внешних credentials или изменении бизнес-модели. Blocked-задача
не останавливает цикл: зафиксируй blocker и возьми следующую независимую.

После feature-freeze не расширяй scope. Новые идеи записывай в FUTURE (FRONTEND_BACKLOG.md) и не держи
ими текущий loop открытым.

Когда очередь задач пуста, выполни один final gate: clean build, full available tests,
production-like smoke, core UX flows, accessibility, performance, themes/designs,
responsive, console/network, dead-code/dirty-files. При полном pass:

1. поставь FINALIZED (FRONTEND_STATE.json.roundStatus);
2. создай FINAL_FRONTEND_REPORT.md;
3. сделай итоговый atomic commit;
4. останови текущий loop (CronDelete 22b94f95);
5. не переходи в maintenance и не начинай новый review без новой команды пользователя.

---

# ИСТОЧНИКИ ПРАВДЫ (читать эти МАЛЕНЬКИЕ файлы каждый тик, НЕ giant PLAN)

```text
FRONTEND_STATE.json      # единственный статус + очередь задач + nextTaskId + gates
FRONTEND_BACKLOG.md      # раскрытие задач (P0→P3), MUST/SHOULD/FUTURE
FRONTEND_DECISIONS.md    # зафиксированные решения (+ NEEDS-USER блокеры)
FRONTEND_EVIDENCE.md     # ground-truth inventory, live-QA, замеры
PLAN_FRONTEND.md         # ТОЛЬКО правила/фазы/release-gates (§21/§7 заморожены → FRONTEND_HISTORY.md)
FINAL_UI_UX_REVIEW_AND_AUTONOMOUS_PLAN.md  # авторитетный источник round-scope (Разделы 4/6/7/9)
```

Порядок тика: восстановить статус из FRONTEND_STATE.json → взять nextTaskId (или пересчитать
по §7.2 selection algo) → work package → обновить FRONTEND_STATE.json (status/gates/nextTaskId) +
evidence → atomic commit (explicit pathspec, trailer). Крон один: 22b94f95 (10m); ScheduleWakeup
не звать (фикс-крон), дубль-крон не создавать.

Разделы ниже (0..) — LEGACY-справка режима порта; действуют лишь там, где НЕ противоречат
FINALIZATION OVERRIDE и источникам правды выше.

---

# 0. Обязательное восстановление состояния

В начале каждого тика прочитай полностью:

```text
~/.claude/skills/frontend-design/SKILL.md
~/.claude/skills/frontend-design/references/iterative-refactor-protocol.md
PLAN_FRONTEND.md
design/mockups/DESIGN.md
design/mockups/PRODUCT.md
design/mockups/PROGRESS.md
docs/ui-ux-improvement-log.md
```

Если существуют:

```text
CLAUDE.md
AGENTS.md
README.md
.design/editorial-redesign/SERVER_CONTRACT.md
project_audit_backlog_2026-06.md
design/mockups/screen-registry.json
design/mockups/PIXEL_QA_MATRIX.md
```

Не полагайся на память между тиками.

`PLAN_FRONTEND.md` нового раунда является главным ledger.
Все старые `DONE/verified-clean` считаются историей и должны быть перепроверены.

---

# 1. Режимы и фазы

Определи текущую фазу выбранной поверхности:

```text
A DISCOVER
B MOCKUP_RECHECK
C MOCKUP_APPROVED
D PORT_MAPPING
E PORT_TO_PRODUCTION
F PRODUCTION_PARITY
G FULL_REFACTOR
H REGRESSION_QA
I VERIFIED
J MAINTENANCE
```

Нельзя перепрыгивать:

```text
MOCKUP_APPROVED → PORT
```

без полного mapping.

Нельзя ставить `VERIFIED` сразу после port: обязателен отдельный refactor и regression.

---

# 2. Первый тик нового раунда

Если новый reset-план ещё не применён:

1. Замени текущий рабочий план на обновлённый `PLAN_FRONTEND_RESET_UPDATED.md`.
2. Сохрани Git-историю и source.
3. Не удаляй mockups.
4. Аннулируй старые acceptance statuses.
5. Сохрани решения пользователя.
6. Создай/обнови:
   - `design/mockups/screen-registry.json`;
   - `design/mockups/PIXEL_QA_MATRIX.md`.
7. Проведи только exhaustive inventory.
8. Не делай массовый visual/production refactor.
9. Создай отдельный reset commit.

Reset завершён, когда:

```text
Old PASS carried forward = 0
All surfaces = TODO/RECHECK
All production files = TODO/RECHECK
```

---

# 3. Полный scope поверхностей

Найди и покрой:

- каждый route;
- каждый top-level template;
- каждый page-state;
- каждый modal/dialog;
- каждый drawer/sheet;
- каждый menu/dropdown/popover;
- каждый tooltip с важной информацией;
- каждый toast/alert/status;
- каждый confirmation;
- каждый loading/empty/error/success/partial/offline/permission state;
- каждый validation state;
- каждый mobile-only surface;
- каждый no-JS fallback;
- каждый print state;
- каждый component state;
- default/light/dark;
- все 10 design variants;
- все обязательные viewports;
- DPR 1/2;
- zoom 100/200;
- keyboard/focus/reduced-motion.

Inventory строится из:

- controllers/routes;
- templates/fragments;
- links/forms;
- JS selectors/triggers;
- browser crawl;
- current mockups;
- product docs;
- server contract.

Новый найденный state немедленно добавляется в registry и plan.

---

# 4. Границы

## Разрешено после mockup approval и mapping

```text
design/mockups/**
modules/quiz-app/src/main/resources/templates/**
modules/quiz-app/src/main/resources/static/css/**
modules/quiz-app/src/main/resources/static/js/**
PLAN_FRONTEND.md
docs/ui-ux-improvement-log.md
```

## Запрещено

- backend/business logic ради UI;
- API/DB без отдельного решения;
- `seed/mcq/**`;
- новые dependencies/package manager;
- destructive live quiz operations;
- Mermaid;
- push;
- `git add -A`;
- reset чужих правок;
- коммит runtime-copy вместо source;
- тяжёлый build при live bootRun.

---

# 5. Осознанные решения

Не переоткрывать без нового evidence:

- neutral selected option до проверки;
- semantic colors после проверки;
- adaptive 1/2-column answer layout;
- soft inline code;
- minimal focus-mode;
- 48px focus question;
- summary rowheader/share contract;
- intentional emoji;
- external Mermaid owner;
- deferred favorite/analytics CTA/self-host decisions;
- MCQ content fairness принадлежит внешнему pipeline.

---

# 6. Collision guard

Перед выбором и перед commit:

```bash
git status --short
git status --porcelain -- <candidate paths>
```

Dirty обязательного файла блокирует всю цепочку.

Связанные цепочки:

```text
tokens.css ↔ base.css ↔ head.html
app.js ↔ result.html ↔ settings.html ↔ focus-training.html
stats.js ↔ stats.html
shared fragment ↔ every consumer
```

Если blocked:

- не трогать;
- не захватывать чужие hunks;
- взять независимую задачу;
- либо провести read-only audit;
- поставить `BLOCKED`.

---

# 7. Выбор единицы одного тика

Один тик = одна единица:

- inventory одного surface-family;
- mockup одного surface/state-family;
- approval одного mockup;
- mapping одной surface;
- port одной surface-family;
- parity fix одной surface;
- post-port refactor одной области;
- regression slice;
- resolution одного manual-review.

Не начинать второй независимый экран.

Worst-first:

1. broken primary flow;
2. missing surface/mockup;
3. business-contract mismatch;
4. critical a11y;
5. unusable mobile/overflow;
6. missing state/recovery;
7. mockup-production mismatch;
8. design-system drift;
9. duplication/architecture;
10. performance;
11. polish/microcopy.

---

# 8. Task contract

Перед изменением запиши:

```text
Task ID:
Pipeline phase:
Surface/state:
Problem:
Evidence:
User impact:
Mockup source:
Production source:
Business/server contract:
Dependencies:
Files:
Out of scope:
Risk:
Acceptance criteria:
QA matrix:
Rollback:
```

Нельзя использовать критерий «сделать красивее».

---

# 9. Фаза mockup

Mockup должен:

- покрывать всё окно и все states;
- быть deterministic;
- открываться без backend;
- использовать реалистичный русский content;
- иметь normal/min/max/long/code/empty/error fixtures;
- поддерживать URL state switches;
- иметь light/dark/design variants;
- проходить responsive;
- keyboard/focus/a11y;
- reduced motion;
- edge-to-edge wide layout;
- screenshot + DOM metrics;
- design-review;
- `impeccable detect`, если установлен.

Mockup `APPROVED` только после всех обязательных gates.

---

# 10. Mapping перед переносом

Для surface зафиксируй:

```text
mockup URL/state
production template
server th:if/model conditions
DOM hooks
JS consumers
form/API/CSRF contract
progressive enhancement
tokens/components
cache-busting
tests
rollback
risk
```

Прочитай production template/fragment/CSS/JS полностью.

Graphify-first для Java/Kotlin contract.
Для templates/CSS/JS допустимы `rg`, DOM и browser tools.

`PORT_READY` запрещён при неполном mapping.

---

# 11. Перенос mockup в production

Переноси не HTML-снимок, а систему:

- semantic structure;
- tokens;
- components;
- layout;
- states;
- interaction contract;
- responsive behavior;
- a11y;
- theme/design behavior.

Сохраняй:

- Thymeleaf conditions;
- form actions;
- CSRF;
- model attributes;
- JS selectors;
- no-JS fallback;
- print;
- DOM hooks;
- product decisions.

Не создавай второй параллельный frontend внутри production.

Старый CSS/DOM удалять только после доказанной parity и проверки consumers.

---

# 12. Runtime и cache-busting

После source change:

1. Обнови обязательные `?v=N`.
2. Скопируй source в `build/resources/main` только для live verification, если это требуется.
3. Не коммить runtime-copy.
4. Hard reload.
5. Проверь Network и реальную asset version.
6. Не запускай Gradle build при live `bootRun`, если это wedge.
7. Postgres host port = 5433.

---

# 13. Production parity

Для одного и того же surface/state/theme/variant/viewport сравни:

- mockup;
- production.

Обязательны:

- screenshot;
- DOM/computed metrics;
- shell bounds;
- gutters;
- main/rail;
- typography;
- spacing;
- controls;
- borders/radii;
- focus;
- overflow;
- states;
- interaction;
- content order.

Приёмка:

```text
unexplained geometry delta ≤ 1 CSS px
horizontal overflow = 0
clipping/overlap = 0
unexpected changed regions = 0
```

Raster antialiasing допускает малый tolerance.
Нельзя повышать threshold, чтобы скрыть дефект.

Production constraint deviation документируется отдельно.

---

# 14. Viewports и environments

Минимум:

```text
320×800
375×812
390×844
430×932
768×1024
1024×768
1280×800
1440×900
1512×982
1728×1117
1920×1080
2560×1440
```

Также:

```text
DPR 1
DPR 2
zoom 100%
zoom 200%
default theme
light
dark
all relevant design variants
prefers-reduced-motion
keyboard-only
```

Не обязательно делать screenshot всех комбинаций каждый тик.
Перед `VERIFIED` evidence обязано покрывать полную применимую матрицу.

---

# 15. Accessibility

Цель WCAG 2.2 AA.

Проверить:

- semantic HTML;
- landmarks/headings;
- native controls;
- labels;
- keyboard;
- tab order;
- focus-visible;
- focus-not-obscured;
- dialog trap/return;
- contrast;
- target size;
- no color-only state;
- live regions;
- reduced motion;
- 200% reflow;
- accessible names;
- no redundant ARIA.

Нельзя ставить PASS только по scanner.

---

# 16. UI states

Проверяй применимые:

```text
initial
loading
refreshing
success
empty
partial
recoverable error
fatal error
offline
permission denied
disabled
pending
mutation success
mutation failure
```

Каждый state должен объяснять:

- что происходит;
- сохранились ли данные;
- что пошло не так;
- можно ли повторить;
- что делать дальше.

---

# 17. Quiz-specific

До проверки:

- selection neutral;
- no green/red hint;
- correct не палится формой/позицией;
- keyboard/focus;
- adaptive options;
- accessible submit.

После проверки:

- user choice;
- correct;
- result;
- correct explanation;
- wrong explanations;
- next action;
- no overload.

Не трогай MCQ data.

---

# 18. Полный post-port refactor

После parity проведи отдельный аудит и исправления:

## UX
- IA;
- hierarchy;
- primary action;
- recovery;
- cognitive load;
- microcopy.

## CSS
- token usage;
- duplication;
- specificity;
- stale selectors;
- dead overlays;
- theme divergence;
- magic values.

## Templates
- semantic structure;
- fragments;
- duplication;
- state conditions;
- no-JS;
- print.

## JavaScript
- listeners;
- races;
- busy/error states;
- selector contracts;
- progressive enhancement;
- duplication;
- dead AI remnants;
- generated table semantics.

## Performance
- blocking assets;
- font loading;
- layout shift;
- network;
- DOM cost;
- chart/render cost.

Parity не означает refactor complete.

---

# 19. Browser QA

Для изменённой surface:

1. дождаться expected DOM;
2. проверить URL/title/heading;
3. console;
4. network;
5. loaded assets;
6. themes/designs;
7. viewports;
8. keyboard/focus;
9. states;
10. reduced motion;
11. overflow;
12. screenshot;
13. DOM/computed metrics;
14. fix;
15. repeat.

Stale screenshot:

- переснять в fresh page;
- проверить selector/text;
- одинаковые кадры разных страниц = red flag.

---

# 20. Screenshots и pixel diff

Mockup shots:

```text
design/mockups/.shots/
```

Production shots:

```text
docs/ui-critique/screenshots/
```

Формат имени:

```text
<surface>__<state>__<theme>__<design>__<viewport>__dpr<dpr>.webp
```

Перезаписывать старые.
Не плодить PNG.

Pixel diff:

- одинаковая среда;
- одинаковые dimensions;
- no broad ignore masks;
- dynamic mask только с доказательством;
- changed region должна быть объяснена.

---

# 21. Automated checks

Запускай только существующие и пропорциональные change:

- template/static contract tests;
- targeted Gradle tests;
- ArchUnit, если применим;
- browser a11y;
- visual regression;
- selector/DOM contract tests.

Не выдумывай npm.
Не отключай failing test.

Full available suite — на milestone/final regression, не после каждого text fix.

---

# 22. Обновление плана

После каждого тика обнови:

- surface pipeline row;
- production file row;
- historical candidate row, если проверена;
- `design/mockups/screen-registry.json`;
- `design/mockups/PIXEL_QA_MATRIX.md`;
- `design/mockups/PROGRESS.md`;
- `docs/ui-ux-improvement-log.md`.

Нельзя массово поставить `✅`.

Каждая колонка закрывается собственным evidence.

---

# 23. Commit

Один тик — один atomic commit.

Разрешённый pathspec:

- target mockup;
- target production source;
- required shared source;
- asset-version consumers;
- registry/matrix/progress/plan;
- targeted tests.

Не включать unrelated files.
Не push.

Message по фазе:

```text
design(mockup): <surface> — complete <state-family>
design(port): <surface> — move approved mockup to production
refactor(frontend): <surface> — resolve <defect>
test(frontend): <surface> — lock production parity
```

После commit проверить hash/message, а не только `git log -1`.

---

# 24. Definition of Done surface

`VERIFIED` только если:

- inventory complete;
- mockup approved;
- mapping complete;
- port complete;
- production parity proven;
- post-port refactor complete;
- full regression complete;
- responsive/themes/designs complete;
- keyboard/a11y complete;
- states complete;
- no-JS complete;
- console/network clean;
- tests pass;
- screenshots current;
- pixel diff explained;
- cleanup complete;
- docs updated;
- atomic commit exists;
- no unresolved high-risk issue.

---

# 25. Stop condition полного раунда

Переход в maintenance только когда:

```text
All production routes discovered
All windows/overlays/states covered
All mockups approved
All mappings complete
All production ports complete
All parity checks pass
All post-port refactors complete
All P0/P1 resolved
A11y blockers = 0
Overflow defects = 0
Unexplained pixel diffs = 0
Orphan mockups = 0
Orphan production surfaces = 0
Loop-created dirty files = 0
```

После этого:

- не начинать новый random redesign;
- ловить regressions;
- проверять новые product changes;
- редкие viewports;
- zoom 200%;
- keyboard edge cases;
- performance;
- design drift.

---

# 26. Отчёт тика

```markdown
## Frontend end-to-end tick

### State
- Phase:
- Surface/state:
- Previous status:
- Priority:
- Evidence:
- Acceptance criteria:

### Mockup
- Files:
- States:
- Themes/designs:
- Viewports:
- Approval gates:

### Mapping
- Production source:
- Server/DOM contract:
- JS/forms:
- Cache-busting:
- Tests:

### Production
- Files changed:
- User-visible effect:
- Parity:
- Refactor:
- Cleanup:

### QA
- Browser:
- DOM metrics:
- Screenshots:
- Pixel diff:
- Keyboard/focus:
- Accessibility:
- Console/network:
- Automated checks:

### Safety
- Collision:
- Business/API:
- Destructive actions:
- Dirty files:

### Commit
- Hash:
- Message:
- Pathspec:

### Plan
- Rows/columns updated:
- Remaining states:
- Next worst-first candidate:
```

Не заявлять проверку, которая не запускалась.

---

# 27. Итоговая инструкция

Начни с reset/inventory, если новый план ещё не применён.

Затем каждый тик:

```text
read skills and state
→ collision guard
→ choose one worst-first pipeline unit
→ read mockup and production source
→ define measurable acceptance
→ implement current phase only
→ verify browser + DOM + screenshots
→ run targeted checks
→ update registry/matrix/plan/progress
→ atomic commit
```

Макеты являются первым этапом, а не конечным артефактом.
После их утверждения обязательно выполнить mapping, перенос в production, parity,
полный refactor и regression.
