# PLAN_FRONTEND.md — правила, фазы и release-gates FINALIZATION ROUND

> **Конечный finalization-round (с 2026-07-15).** Цель — довести frontend до `FINALIZED` и
> остановить loop. Governing-директива — `PROMPT_PLAN_FRONTEND.md` §`# FINALIZATION OVERRIDE`.
> **Статус живёт ТОЛЬКО в `FRONTEND_STATE.json`** (этот файл статус не хранит).
> Полная история порт-раунда (§7 матрица, §9 аудит, §21 лог R0.x) — в `FRONTEND_HISTORY.md`.

---

## 1. Продукт и режим

- **Продукт:** quiz-app — тренажёр подготовки к собеседованию.
- **Стек:** Spring Boot + Thymeleaf + vanilla CSS/JS. PostgreSQL (Flyway, `:5432`). Chromium-only browser-QA (chrome-devtools).
- **Дизайн:** единственный — `instrument` (DEC-002). Новые дизайн-варианты запрещены (feature-freeze).
- **Режим:** конечный round. Один тик = один связный work package (issue → impl → targeted tests → live-QA → evidence → atomic commit), НЕ микроправка и не audit-only-подряд.

## 2. Источники правды (читать маленькие файлы каждый тик)

| Файл | Роль |
|---|---|
| `FRONTEND_STATE.json` | ЕДИНСТВЕННЫЙ статус: очередь P0→P3, `nextTaskId`, `gates`, `activeBlockers`, `roundStatus` |
| `FRONTEND_BACKLOG.md` | раскрытие задач + MUST/SHOULD/FUTURE |
| `FRONTEND_DECISIONS.md` | зафиксированные решения (DEC-001..), NEEDS-USER блокеры |
| `FRONTEND_EVIDENCE.md` | ground-truth inventory, live-QA, замеры |
| `FINAL_UI_UX_REVIEW_AND_AUTONOMOUS_PLAN.md` | авторитетный round-scope (Разделы 4/6/7/9) |
| `FRONTEND_HISTORY.md` | архив (порт-раунд R0.x, снимок старого PLAN) |

**Приоритет при конфликте:** явное требование пользователя → бизнес-контракты/сценарии → production-код → DECISIONS/DESIGN → accessibility → дизайн-система → этот файл → внешние референсы.

## 3. Жёсткие ограничения

**Разрешено** (DEC-004): менять `templates/**`, `static/css/**`, `static/js/**`, `design/mockups/**`, и — целево — backend/API/DB/tests, если это нужно для UX-flow, минимально, безопасно, **мигрируемо** (Flyway V16+) и покрыто тестами. Удаление доказанно-мёртвого frontend-кода, унификация, cache-busting, targeted tests.

**Запрещено:**
- трогать `seed/mcq/**`, `.claude/settings.local.json`, `.cursor/hooks/state/*`, `PLAN_INTERVIEW.md`, `docs/mcq-quality/**` (домен параллельной MCQ-сессии);
- новый content pipeline; новые зависимости / смена package manager;
- destructive live submit/start/finish, загрязняющий SRS-данные;
- выключать VPN; `git add -A`; `git reset --hard`; откатывать чужие изменения; push;
- коммитить runtime-копии вместо source;
- запускать тяжёлый gradle build/test при живом `bootRun` (**wedge**, BLK-BOOTRUN);
- новые дизайн-варианты; расширять scope после feature-freeze (новые идеи → FUTURE).

**Cache-busting:** CSS change → согласованный bump `tokens.css`+`base.css` в `head.html`; `app.js` change → bump во ВСЕХ templates-consumers (focus-training/result/settings — не single-sourced, см. PERF-01); `stats.js` → bump в `stats.html`; text-only template change → bump не нужен; после bump проверить Network и фактически загруженную версию.

## 4. Сохранённые продуктовые решения (durable — не дефекты без нового evidence)

- вариант ответа до проверки нейтрален; correct/wrong semantic color — только после проверки;
- options layout адаптивен (1–2 колонки по длине/code-density); inline `code` в вариантах мягкий;
- focus-mode минималистичен; `.focus-question` 48px — осознанное решение;
- `role=rowheader` на summary `<td>` сохраняется (share-script contract);
- намеренные emoji в подсказке/уверенности сохраняются;
- Mermaid имеет внешнего owner;
- border-left accent-линейка, favorite-flow, CTA из аналитики, self-host Chart.js — не менять без отдельного решения;
- content fairness относится к MCQ pipeline; frontend не усиливает подсказки;
- sticky-masthead ЗАБАНЕН (ассерт `TemplateFragmentContractTest`); nav всегда видима, без burger (DEC-001);
- `result.html` — живой no-JS fallback POST /answer (не dead); table-a11y роли в session-summary обязательны.

Переоткрытие решения — только записью: `evidence → impact → alternatives → migration risk → user decision`.

## 5. Фазы round (детально — §5 FINAL-плана)

| Фаза | Суть |
|---|---|
| 0 | Остановить дрейф, feature-freeze, split-docs, prompt-override — **DONE (R0.165)** |
| 1 | Ground-truth inventory + reconciliation — **DONE (EV-INV-001)** |
| 2 | Финализация дизайн-системы (завершить 'полную замену': FE-CMP-1, wide-layout PROC-07) |
| 3 | Core training flow (pause/resume, idempotent submit, «Не знаю») |
| 4 | Result и explanation flow (прогрессивный разбор) |
| 5 | Session summary и adaptive next action |
| 6 | Analytics (next-action блок) |
| 7 | Settings и data management (упрощение осей после снятия дизайн-оси) |
| 8 | Global resilience и error states |
| 9 | Accessibility consolidation (единый SR/keyboard проход) |
| 10 | Performance и delivery (prod-like budgets, prune fonts) |
| 11 | Финальная QA-матрица |
| 12 | Cleanup и release candidate |
| 13 | Final gate и остановка |

Выбор задачи — не по фазам линейно, а по `FRONTEND_STATE.json` (P0→P1→P2→P3 → dependency impact → core-flow-before-polish).

## 6. QA-гейты работы с production

**До изменения:** target-файлы clean (collision guard §8); app/runtime доступен либо есть безопасная статическая стратегия; rollback определён; бизнес-контракт понятен.

**После изменения:** source (не runtime-копия) правится; cache-busting обновлён + Network грузит новую версию; проверены — целевой экран, shared consumers, light/dark, responsive-матрица (375/768/1280/1440/1728/1920/2560 + reflow 320/640), keyboard/focus, accessibility, no-JS/PE, console/network, targeted tests; bounded diff; atomic commit. Live-верификация без gradle при живом bootRun (`cp source → build/resources/main`).

## 7. Full-refactor критерии (сверх визуального переноса)

Проверять: лишние wrappers / DOM duplication; повторяющиеся CSS-правила и specificity wars; stale selectors; dead overlays; fragment/component reuse; JS listeners и races; async busy/error states; table semantics; forms; loading/empty/error; print; forced-colors; reduced-motion; performance; progressive enhancement; content-independent layout resilience. Нельзя считать задачу закрытой при только визуальном переносе.

## 8. Collision guard (перед каждым тиком)

```bash
git status --short
```

Dirty обязательного файла блокирует всю связанную цепочку:

```text
tokens.css ↔ base.css ↔ head.html
app.js ↔ result/settings/focus templates
stats.js ↔ stats.html
shared fragment ↔ все consumers
```

При blocker: не захватывать чужие hunks; выбрать независимую задачу либо read-only аудит; поставить задаче `BLOCKED` + записать конкретный blocker; не подделывать прогресс. MCQ-WIP-файлы в dirty — норма (параллельная сессия), их не трогаем.

## 9. Definition of Done задачи

Задача `DONE`, только если: user-эффект реализован; acceptance выполнены; бизнес-поведение сохранено; states предусмотрены (loading/empty/error/recovery); responsive + light/dark + relevant a11y проверены; browser-QA выполнен либо честно заблокирован; targeted tests/checks зелёные; console/network чисты; diff bounded; docs/evidence обновлены; atomic commit записан; остаточный риск отсутствует или явно принят. `FINALIZED` не ставить при accepted-but-unfixed дефекте (PROC-18).

## 10. Release-gates и остановка

`FINALIZED` (final gate, §9 FINAL-плана) при полном pass `FRONTEND_STATE.json.gates`: clean build · full available tests · production-like smoke · core UX flows · accessibility · performance (prod-like) · themes/designs · responsive · console/network clean · dead-code/dirty-files clean · clean-checkout reproducible · design-docs match production. Плюс: unresolved P0/P1 = 0; orphan surfaces = 0; horizontal-overflow = 0; unexplained pixel-diff = 0.

При полном pass: `roundStatus=FINALIZED` → создать `FINAL_FRONTEND_REPORT.md` → итоговый atomic commit → `CronDelete 22b94f95` → остановить loop (не переходить в maintenance без новой команды пользователя).

---

_История и подробный progress-лог — `FRONTEND_HISTORY.md`. Крон один: `22b94f95` (10m), ScheduleWakeup не звать._
