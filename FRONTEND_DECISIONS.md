# FRONTEND_DECISIONS — зафиксированные решения FINALIZATION ROUND

> Одно решение = один блок. Обратимые UI-решения принимаются автономно (см. FINALIZATION OVERRIDE §7.3).
> Решения, конфликтующие с осознанным выбором пользователя или меняющие бизнес-семантику, помечаются `NEEDS-USER` и не реализуются молча.
> Статус машиночитаемо связан через `decision` в задачах `FRONTEND_STATE.json`.

---

## DEC-001 — Header: статичный, nav всегда видима, компактный на мобильном

**Статус:** ADOPTED (2026-07-15) · обратимо, следует DESIGN.md.

- Masthead **статичный** (не sticky) — sticky-masthead забанен ассертом `TemplateFragmentContractTest` (сохраняем).
- Основная навигация (Фокус/Аналитика/Настройки) **видна всегда** — без burger, без гейта `showPrimaryNav` (конец дубля с surface-tabs).
- Группа тогглов: остаются **theme-toggle** и **layout-toggle**; **design-toggle удаляется** (см. FE-CMP-1 — он no-op после 'полной замены').
- Мобильный header должен сообщать текущий контекст даже при компактной подаче (UX-24); целевой compact — не раздутый (прежний ориентир ~56–64px против 144px). Реализация — отдельная UI-задача при live-QA <485px (device-emulation).

**Почему:** header-фрагмент прочитан (fragments/header.html); sticky-бан и always-visible nav — уже действующие контракты.

---

## DEC-002 — Единственный дизайн: Instrument (design variant count = 1)

**Статус:** ADOPTED (2026-07-15) · runtime-derived, необратимо в рамках round (feature-freeze запрещает новые дизайн-варианты).

- Канонический и **единственный** дизайн — `instrument`. `designVariantCount = 1` (runtime-факт из inventory: `head.html DESIGNS=['instrument']`, `tokens.css` токен-блоки только instrument light+dark, SSR-дефолт instrument).
- Ось персонализации «дизайн» **упраздняется** (задача FE-CMP-1): 10 не-instrument значений мертвы (коерсятся в instrument), 11 кнопок в /settings — вводящий в заблуждение UX.
- Новые дизайн-варианты — в FUTURE, не реализуются.

**Почему:** PROC-06 verdict — ровно один рабочий дизайн; движок 'полной замены' уже промоутнут, остались хвосты (settings-ось, header-toggle, dead SIGNATURE base.css, THEME_COLORS).

---

## DEC-003 — Wide-layout модель — **NEEDS-USER (BLOCKED)**

**Статус:** OPEN · заблокировано: конфликтует с осознанным выбором пользователя И источники расходятся 4 способами. НЕ реализуется молча (правило «не переопределять deliberate design decisions»).

Inventory (wide-layout срез) обнаружил **4-стороннее расхождение**:

1. **Текущий CSS (реальность):** центрированные узкие колонки с симметричными пустыми полями — `.ed-page` max-width 1280px, `.focus-training` 960px, `margin:0 auto`; нет ни одной media-query ≥1440. Это ровно тот анти-паттерн, который `frontend-design §12.2` и mockups DESIGN.md **запрещают**.
2. **`design/mockups/DESIGN.md`:** мандат **full-width edge-to-edge** + **содержательный правый рельс** `.focus-aside` ≥1080px (сессия/хоткеи/мета; на result — «разбор глубже»); проза 66ch ВНУТРИ зоны.
3. **Корневой `/DESIGN.md`:** ещё третья модель — `--max-width-*: none` и `--measure: none` (чистый full-width без рельса).
4. **Осознанный выбор пользователя (project memory):** full-width prose, `--measure` во всю ширину («259ch на 2560 — deliberate, НЕ трогать»).

**Почему NEEDS-USER:** выбор между «full-width prose без рельса» (память пользователя / root DESIGN.md) и «full-width + содержательный правый рельс» (mockups DESIGN.md) меняет визуальную идентичность и прямо противоречит зафиксированному «не трогать». Автономно решаю только бесспорную часть: **текущие центрированные-узкие-колонки-с-пустотами — это дрейф/баг** (противоречит ВСЕМ трём канонам сразу). Направление устранения дрейфа — за пользователем.

**Собранный вопрос (задать вместе с BLK-BOOTRUN):** какую wide-модель канонизировать —
(A) full-width prose без правого рельса (реаффирм прежнего выбора), либо
(B) full-width + содержательный правый рельс из mockups (заполнить пространство мета-контентом)?
До ответа PROC-07 остаётся BLOCKED; беру независимые задачи.

---

## DEC-004 — Backend-правки разрешены в этом round

**Статус:** ADOPTED (2026-07-15) · из FINAL-плана §PROC-09 + OVERRIDE.

Можно менять frontend, целевые backend/API/DB contracts и tests, если это нужно для UX-flow, изменение минимально, безопасно, **мигрируемо** (Flyway V16+, PostgreSQL) и покрыто тестами. `seed/mcq/**` — НЕ трогать без отдельной content-pipeline задачи (домен параллельной MCQ-сессии).

---

## DEC-005 — Источник статуса и коммит-дисциплина

**Статус:** ADOPTED (2026-07-15).

- Единственный статус — `FRONTEND_STATE.json`. `PLAN_FRONTEND.md` §21/§7 **заморожены** (историчны, → `FRONTEND_HISTORY.md`).
- Коммиты — explicit pathspec, без `git add -A`, без push; trailer `Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>`.
- НЕ трогать MCQ-WIP: `seed/mcq/**`, `.claude/settings.local.json`, `.cursor/hooks/state/*`, `PLAN_INTERVIEW.md`, `docs/mcq-quality/**`.
- Крон один: `22b94f95` (10m). ScheduleWakeup не звать (фикс-крон).
