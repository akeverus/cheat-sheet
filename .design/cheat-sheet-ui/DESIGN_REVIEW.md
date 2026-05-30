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
