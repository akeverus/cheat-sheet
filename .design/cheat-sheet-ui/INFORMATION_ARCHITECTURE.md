# Information Architecture: Cheat-Sheet Interview-Quiz

> Phase 3 of the design-flow cycle. Builds on `DESIGN_BRIEF.md`.
> Structure only — no visual styling here. Confirms page map, navigation,
> per-page layout intent, and where the theme toggle lives.

## Site Map

```
/                 Focus (training surface)   — question · MCQ · flashcard · result-zone
/start  (POST)    → starts session → redirects to /
/stats            Analytics                  — summary grid + per-topic table + charts
/settings         Settings                   — filters · session config · progress · data-mgmt
/start (POST from /settings) → session
/finish (POST)    → ends session
/export?format=…  JSON / CSV download (no page)
/session-summary  End-of-session recap
/error            Error fallback
```

Server-rendered (Thymeleaf). Each route returns a full HTML document sharing
`fragments/head`, `fragments/header`, and page-specific fragments. No SPA routing.

## Two Navigation Systems (current redundancy — resolve)

There are **two** parallel nav mechanisms today:

1. **`fragments/header.html` → `<nav aria-label="Основная навигация">`** — primary
   nav: Фокус / Аналитика / Настройки (+ export links on stats). Used on
   settings & stats (`showPrimaryNav=true`).
2. **`fragments/focus-surface-tabs.html` → `.surface-tabs`** — Фокус / Аналитика /
   Настройки as "surface tabs". Used on the Focus page.

**Decision:** keep both *roles* but unify their *model and styling* — one nav
component, two render contexts. Same three destinations, same active-state logic,
same wrap behavior (no 2-row break; horizontal scroll/condense on narrow). The
header `<nav>` and `.surface-tabs` should look like the same control family, not
two different widgets. The theme toggle is added once, in the header (see below),
not duplicated into surface-tabs.

## Global Chrome (every page)

```
┌─ header ──────────────────────────────────────────────────────┐
│  h1 page title            [ nav: Фокус Аналитика Настройки ]   │
│  (progress bar if active)                      [ ☀/☾ theme ]   │
└───────────────────────────────────────────────────────────────┘
  main  (centered content column, max-width ≈ 720–820px)
  ...
```

- **`<h1>` page title** — left. `text-wrap: balance` so "Подготовка к
  собеседованию" / "Настройки сессии" never breaks awkwardly.
- **Primary nav** — three equal destinations; active = `aria-current="page"`.
- **Theme toggle** — far right of header, single `<button aria-pressed>` with an
  accessible name ("Переключить тему"); icon reflects state (☀ in dark → switch to
  light, ☾ in light → switch to dark). One instance per page, in global chrome.
- **Progress bar** — only when a session is active; in the no-session state it must
  read as an explicit empty-state, not "0% / broken".

## Per-Page Layout Intent

### Focus (`/`) — the strong core, preserve

Single centered column. Vertical flow:
`surface-tabs → question card (or flashcard) → MCQ options → CTA → post-answer
result-zone`. One task on screen at a time. **Do not restructure** — only
re-tokenize and verify in light theme. A/B/C/D option badges get marginally
stronger anchoring (brief: Could-Improve #7).

### Settings (`/settings`) — REBUILD LAYOUT (the must-fix)

Current `settings.html` wraps everything in `.app-layout` (a sidebar+main grid
meant for Focus), so the control card collapses into a ~230px rail and the
data-management card floats in the wide "main" slot → overlap + 75% empty.

**New structure — Settings owns its own layout, not `.app-layout`:**

```
main (centered, max-width ≈ 820px)
├─ Section A — "Конфигурация сессии"  (the control card, full column width)
│    ├─ Фильтры        (group / topic / toggles / order → Применить)
│    ├─ Сессия         (mode / count → Начать сессию; Завершить if active)
│    └─ Прогресс       (streak + stats-grid; empty-state aware)
└─ Section B — "Управление данными"   (full-width band BELOW Section A, never overlapping)
     └─ Сбросить банк вариантов (danger action, confirm)
```

- Desktop ≥1024: optionally a balanced 2-column grid **inside Section A**
  (Фильтры | Сессия+Прогресс) — equal columns, gutters, no narrow rail. If 2-col
  adds complexity, a single centered column is acceptable and still fixes the bug.
- Tablet/mobile: single column, Фильтры → Сессия → Прогресс → Управление данными
  (this is already how mobile stacks correctly — desktop must match that order).
- The "Свернуть панель" collapse toggle was a sidebar affordance; on a centered
  settings page it is no longer needed — drop or repurpose (out of critical path).

### Analytics (`/stats`) — tame the long table

`stats-grid` (summary cards) + per-topic table + charts. The per-topic table is
one infinite list (brief: Could-Improve #5).

**New structure:**
```
main (centered, wider max-width OK for tabular data, ≈ 960px)
├─ Summary grid           (KPI cards)
├─ Charts                 (if chart.js present)
└─ Per-topic performance
     ├─ Worst-N first (default visible: weakest ~8 topics)
     └─ "Показать все" expander → full table (scrolls within its own box)
```

Heading order already fixed (h1→h2). Keep.

### Result-zone / Session-summary / Error

- **Result-zone** (post-answer, inside Focus): correct/incorrect verdict + per-
  option explanation. Re-tokenize verdict colors for both themes.
- **Session-summary**: recap card, centered column, same system.
- **Error**: minimal centered card, same chrome, links back to Focus.

## Theme Toggle — Behavior & Persistence (structural)

1. **Default**: no stored preference → follow `prefers-color-scheme` (dark is the
   brand default if the OS has no preference).
2. **Override**: user clicks toggle → set `data-theme="light|dark"` on `<html>`,
   persist to `localStorage`.
3. **No flash**: a tiny inline script in `<head>` (before CSS paints) reads
   localStorage/`matchMedia` and sets `data-theme` on `<html>` synchronously.
4. **Sync**: update `<meta name="theme-color">` and keep `color-scheme` consistent
   with the active theme.
5. **Reduced motion**: theme switch is instantaneous for users who request it.

## Reading Order & Landmarks (a11y)

- `header` (banner) → `nav` (primary) → `main` (`#main-content`, skip-link target)
  → page content. One `<h1>` per page (the title). Sections under it are `<h2>`,
  subsections `<h3>` — no rank skips.
- Skip-link first focusable element, jumps to `#main-content`.
- Theme toggle reachable in tab order from the header.

## Out of Scope (IA)

- No new routes/pages. No change to POST endpoints or their params.
- No change to MCQ/seed data flow. Navigation destinations stay the same three.
