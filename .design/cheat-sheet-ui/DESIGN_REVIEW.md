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

### ⏳ Open — deferred to user decision (disputed or net-new design)
- **Wide-viewport ≥1600px density** (focus, stats-dark, settings). **Disputed between reviewers**: stats-light *praised* the centered even-gutter column as satisfying the brief's "ровность" at exactly the widths stats-dark flagged as "75% empty". A centered column with symmetric gutters *is* even (the user's literal directive: "ровный … для всех положений окна"). Restructuring all pages to a ≥1600px multi-column/side-rail layout is a significant design decision for the user's call — and risks conflict with their styles.css WIP. **Confirm intent before overhauling.**
- **Focus flashcard empty-state void** (~700px gap when seed MCQ is empty). Real, but the fix is **net-new design** (a compact flashcard/empty layout). Should be designed deliberately, not autopiloted.

### Coverage gaps for a future pass (from the critic)
Interaction states (post-answer "разбор по шагам", accordion/table toggles, hover/focus-visible) and **measured** a11y (keyboard order, ARIA) were not in this static pass. The post-answer state was separately verified to render in light (`screenshots/rev-answer-light-1440.png`).
