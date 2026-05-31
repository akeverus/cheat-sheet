# Design Review: Cheat-Sheet Quiz App UI

Reviewed against: (no formal brief — reviewed against design best practices + observed design system in `styles.css`)
Philosophy (inferred): **Dark "developer console"** — deep navy canvas, indigo/periwinkle accent (`#7c8ff5`), Syne display + DM Sans body + JetBrains Mono, generous radii, soft elevation.
Date: 2026-05-30
Reviewer: design-review skill (Playwright live capture)

## Screenshots Captured

| Screenshot | Breakpoint | Description |
| --- | --- | --- |
| `screenshots/review-focus-question-desktop-1280.png` | Desktop 1280×800 | Focus-training question + 4 MCQ options |
| `screenshots/review-focus-question-tablet-768.png` | Tablet 768×1024 | Same, tablet |
| `screenshots/review-focus-question-mobile-375.png` | Mobile 375×812 | Same, mobile single-column |
| `screenshots/review-settings-desktop-1280.png` | Desktop 1280×800 | Settings/session config |
| `screenshots/review-settings-tablet-768.png` | Tablet 768×1024 | Settings, tablet |
| `screenshots/review-settings-mobile-375.png` | Mobile 375×812 | Settings, mobile |
| `screenshots/review-stats-desktop-1280.png` | Desktop 1280×800 | Stats / per-topic table |
| `screenshots/review-stats-tablet-768.png` | Tablet 768×1024 | Stats, tablet |
| `screenshots/review-stats-mobile-375.png` | Mobile 375×812 | Stats, mobile |

> All screenshots are in `.design/cheat-sheet-ui/screenshots/` (gitignored — bulky PNGs).

## Summary

The **focus-training page is the strong core** — clean, scannable, well-balanced at every breakpoint, with a distinctive dark aesthetic and good MCQ option hierarchy. Mobile layouts across all pages are genuinely good (single-column stacking, ≥44px touch targets, no overflow). The **weakest surface is the Settings page on desktop**: the "Управление данными" card overlaps the filter form, and the whole page is crammed into a ~230px left rail leaving ~75% of the viewport empty — the single most jarring "перекос". Recent hardening (line-length cap, reduced-motion, table scroll, `/stats` heading hierarchy) already closed the main a11y/responsive gaps.

## Must Fix

1. **Settings desktop: overlapping cards + broken column balance.** See `screenshots/review-settings-desktop-1280.png`. The "Управление данными" (data-management) card renders on top of / overlapping the Filters form, and all controls are pinned to a narrow left sidebar while the main content area is empty dark space. On mobile (`review-settings-mobile-375.png`) the same content stacks correctly (data-management at the bottom), so this is a desktop sidebar-layout defect. _Fix: give Settings a real two-column (or centered single-column) desktop layout; data-management as its own full-width section below the form, never overlapping._

## Should Fix

2. **Settings desktop wastes horizontal space.** Even without the overlap, the sidebar-only layout leaves the right ~75% empty. _Fix: center the settings column (max-width ~720–820px) like the focus card, or use a balanced 2-col grid (filters | session+progress)._
3. **Header title wraps awkwardly.** "Подготовка к собеседованию" breaks to two lines inside the banner on desktop. _Fix: `text-wrap: balance` on the banner title, or a slightly smaller clamp at mid widths._
4. **Surface tabs (Фокус/Аналитика/Настройки) wrap to two rows** below ~360px. Acceptable but slightly untidy. _Fix: allow horizontal scroll or tighten padding at the smallest widths._

## Could Improve

5. **Stats per-topic table is a very long single list** (`review-stats-desktop-1280.png` is extremely tall). _Suggestion: group/collapse by category, or surface the worst-N topics first with a "show all" expander._
6. **Empty progress bar in the no-session state** reads as "broken/0%" rather than "no session yet". _Suggestion: an explicit empty-state label or hide the bar until a session starts._
7. **Option letter badges (A/B/C/D)** are subtle; on a long option they can be missed. _Suggestion: marginally stronger badge contrast or a fixed badge column._

## What Works Well

- **Focus-training page** — exemplary: clear question hierarchy, evenly spaced options, disabled-until-selected primary button, visible keyboard hints. Holds up perfectly 375 → 2560.
- **Mobile-first quality** — every page stacks cleanly to a single column with comfortable touch targets and zero horizontal overflow (verified 320–2560).
- **Aesthetic cohesion** — consistent accent, radii, elevation; the Syne/DM Sans/JetBrains Mono trio gives a recognizable, non-generic identity.
- **Recently hardened** — comfortable line-length (≈56ch), `prefers-reduced-motion`, scrollable wide tables, long-token wrapping, correct `/stats` heading order, double-submit protection.

---

## Resolution log — dual-theme review pass (2026-05-31)

A second review pass ran a **7-agent parallel visual review over 42 screenshots** (3 pages × 2 themes × 7 widths: 375/768/1280/1440/1728/1920/2560) plus a completeness critic. Every claim below was then checked by **in-browser measurement** (computed geometry / WCAG ratios), not by eye — and the numbers overturned three reviewer findings.

### ✅ Fixed — `2d2ebb10` (`fix(ui): три перекоса по итогам design-review`)
1. **Stats 375 horizontal overflow.** Measured cause: global `<header>` (`flex; nowrap`) — h1 (231px) + `.header-actions` (368px) couldn't fit 329px and didn't wrap → page `scrollWidth` 380. (An earlier `.data-table` guess was wrong; harmless.) Fix: `header { flex-wrap:wrap }` + full-width `.header-actions` at ≤640px. **overflow +29 → −15.**
2. **Settings 375 right-clip** (cross-validated both themes). Cause: `.filters-fields/toggles` kept `max-width:460px` inside a 299px form; the single-column reset targeted `.sidebar`, but settings uses `.compact-sidebar`/`#left-sidebar-*`. Fix: width reset + toggles → 1 column at ≤640px. **"Слабые темы" no longer clipped.**
3. **"Свернуть панель" full-width pill** (906px@1920, ~2480px@2560). Cause: `.compact-sidebar` is a flex-column → button stretched by `align-self:stretch` despite `width:auto`. Fix: `align-self:flex-start` + chevron ▾ (rotates on `.is-collapsed`). **906px → 155px at every width; toggle round-trip + aria-expanded verified.**

### ✅ Fixed — `d188e241` (`fix(a11y): определён --text-muted`)
- **Undefined `--text-muted` token → sub-AA hints on light.** `.review-forecast-hint` / `.coverage-gaps-hint` / `.sm2-grid dt` / `.kbd-help-*` referenced `var(--text-muted, #6b7280)`, but `--text-muted` was **never defined** → all fell back to hardcoded gray-500 `#6b7280` = **4.24:1** on light. Fix: `:root { --text-muted: var(--text-3) }` (one alias, both themes, in ui-refinements.css). **hints 4.24 → 5.40:1; full light contrast-sweep of all 3 pages = 0 informational-text fails.**

### ✅ Resolved by measurement — no code change warranted
- **"Focus options entirely absent" (must-fix, focus-dark).** The two bundles were shot on **different questions**: dark hit a **flashcard-mode** question (empty seed MCQ → `flashcardMode=true`, the *intended* default per project config, AI fallback off), light hit an MCQ question. Live check: MCQ renders `optionCount=4`. **Not a bug.**
- **"Settings card stuck in left corner on wide screens" (must-fix, settings-dark).** Measured at 1920: `.app-layout` = `l=480 r=1440 w=960` — **correctly centered**, NOT corner-stuck. The dark reviewer misread; the real defect was the stretched toggle (fixed above).
- **"Light-theme palette below AA."** Tokens measure AA-compliant: `--text-3 #5e6a87` = **4.74:1** on page bg, **5.40:1** on cards. The only real failure was the undefined-token fallback (fixed). Darkening AA-passing tokens would deviate from the user's deliberate design → **declined**.

### ✅ Fixed — `4e9d2a1c` (`fix(ui): флешкарта заполняет вьюпорт`)
- **Focus flashcard empty-state void.** Reproduced on a **real FLASHCARD session** (POST `/start` mode=FLASHCARD — *not* `/flashcard-mode-all`, which 404s; an earlier note wrongly used that URL and measured the 404 page). Measured @1440×900: flashcard card was only **258px** vs MCQ's 629px → **391px void** below it, reveal button cramped at top. Fix (ui-refinements.css): `.focus-training:has(.flashcard-phase) { flex-grow:1 }` so the card fills `main`'s free height → **void 391→2px**, plus `margin-block:auto` on `.flashcard-phase` → reveal block **centered** when collapsed (129.5/129.5 symmetric) and **top-aligned & readable** when revealed (answer+grades). No new scroll (720/900 verified). `:has(.flashcard-phase)` leaves MCQ untouched; browsers without `:has()` degrade gracefully.

### ✅ Resolved as deliberate design — no overhaul
- **Wide-viewport ≥1600px density.** Measured: the content card is **symmetrically centered** at every width — gutters 80/80 @1440, 320/320 @1920, 640/640 @2560 — which *is* even ("ровный … для всех положений окна"), and the 1280px cap is a sound readability ceiling. The "75% empty" framing was overstated (real gutter is 33–50%, symmetric). Restructuring to a multi-column/side-rail layout would fight the user's deliberate centered-column language (and risk their styles.css WIP). **Kept centered by decision, not deferral.**

### Coverage gaps for a future pass (from the critic)
Interaction states (post-answer "разбор по шагам", accordion/table toggles, hover/focus-visible) and **measured** a11y (keyboard order, ARIA) were not in this static pass. The post-answer state was separately verified to render in light (`screenshots/rev-answer-light-1440.png`).

---

## Resolution log — interaction-state + measured-a11y pass (2026-05-31)

Closed the "future pass" gaps above by **exercising** the focus-training interaction flow live (Playwright, measured geometry/ARIA, real FLASHCARD + seeded-MCQ sessions), not by eye.

### ✅ Fixed — `a979ff03` (flashcard fix actually served)
- The committed flex-grow flashcard fix wasn't being served: CSS content had changed (clamp→flex-grow) under the same `?v=10`, so the running app/build served the stale clamp version. Bumped `ui-refinements.css` v10→v11. **Re-measured on a live FLASHCARD session @1440×900: `flex-grow:1` applied, void 391→17px, reveal-block margins 45.7/45.7 symmetric, no scroll.**

### ✅ Fixed — `6cb8a3f` (`fix(a11y): aria-controls → #details`)
- The "Показать доп. анализ" disclosure button (`#extra-analysis-toggle`) had `aria-controls="result-feedback"`, but `toggleDetails` (app.js) lazy-loads `/details` and shows/hides `#details` — a different region. Per the WAI-ARIA disclosure pattern `aria-controls` must reference the controlled element. Fixed to `aria-controls="details"`. **Live check: attribute now `details`, target exists, `aria-expanded` toggles.** (The two sibling disclosures — `toggle-code-btn`→`#codeBlock`, `browse-reveal-btn`→`#browse-answer` — were already correct.)

### ✅ Fixed — `6e2a3c1` (`fix(ux): dead «доп. анализ» button`)
- `renderResult` revealed the "доп. анализ" button after **every** answer ("показываем всегда"), but in seed-mode (AI off, the default) there is no explanation: `/answer.explanation` and `/details` both call the *same* `getExplanationText(questionId)` and return `""`. Result: a **dead button** that opened an empty block and flipped its label to "Скрыть". This violated the documented `AnswerResponse` contract ("explanation может быть пустым … фронт это учитывает и не показывает доп. анализ"). Now the button is gated on `data.explanation` (empty → hidden, present → shown). Bumped `app.js` v7→v8. **Live seed-session check (postgresql q-1, `/details`=200 `""`): button hidden, feedback "Неверно…" still shown, no regression.**

### ✅ Verified clean — keyboard focus-visible + tab order (no change needed)
- Tabbed through the focus-training page (16 stops) and measured each `document.activeElement`'s focus indicator: **every** interactive element (skip-link, theme-toggle, surface-tabs, MCQ radio + label, submit) shows a visible `solid 2px #7c8ff5` outline (some + box-shadow). Tab order is logical. `missingRing: []`.

### ✅ Verified — Docker/cloud build
- The repo's existing multi-stage `Dockerfile` + `docker-compose.yml` (`interview-prep` service) build successfully: `docker build` → image 610MB, exit 0. Seed-first prod profile boots without an AI key. No change needed.
