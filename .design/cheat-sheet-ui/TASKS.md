# Build Tasks: Cheat-Sheet UI Redesign

> Phase 5 of the design-flow cycle. Ordered, commit-sized steps.
> Strategy: re-token first with **zero intended visual change to dark** (de-risk),
> then layer light + toggle, then fix layouts, then polish. Verify each step live
> in Playwright across the full viewport matrix (375/768/1280/1440/1728/1920/2560),
> both themes once light exists. Commit after each green step.

## Verification matrix (every visual step)

- Viewports: 375 · 768 · 1280 · 1440 · 1728 · 1920 · 2560
- Themes: dark (baseline) · light (from B3 onward)
- Pages: Focus (`/`) · Settings (`/settings`) · Stats (`/stats`)
- Live-sync edits to `build/resources/main` (devtools won't rebuild static).

---

## B1 — Token foundation (dark stays identical) ✅ commit

- Replace `styles.css` `:root` semantic palette (lines ~32–92) with the new DARK
  semantic token set from `DESIGN_TOKENS.css` (adds `--surface-2`, `--well-bg`,
  `--header-bg`, `--glass-bg`, `--accent-bg-2`, `--accent-grad-end`,
  `--border-strong`, `--text-on-accent`, `--amber`, `--focus-ring`,
  `--content-max`, `--content-max-wide`, `--measure`, `--space-xl`).
- Keep primitives (lines ~7–30) untouched.
- **Acceptance:** dark renders visually identical to baseline (spot-check Focus).

## B2 — Tokenize the rgba leaks (dark still identical) ✅ commit

- Replace baked literals in `styles.css` with tokens:
  - `rgba(10,19,38,*)` → `var(--header-bg)`; `rgba(12,21,42,*)` → `var(--glass-bg)`
  - `rgba(7,16,31,*)` / `rgba(10,19,40,*)` → `var(--well-bg)`
  - `rgba(99,85,240,*)` → `var(--accent-grad-end)`
  - `rgba(124,143,245,.08/.10/.12/.16)` → `var(--accent-bg)` / `var(--accent-bg-2)`
  - `rgba(110,140,220,*)` borders → `var(--border*)`
  - body radials → `var(--ambient-1/2/3)`
- **Acceptance:** dark still pixel-identical; grep shows leak count dropped sharply.

## B3 — Light theme + no-flash + toggle ✅ commit

- Append light overrides (`:root[data-theme=light]` + `@media prefers-color-scheme`)
  to `styles.css`.
- `head.html`: inline no-flash script (read localStorage/`matchMedia` → set
  `data-theme` on `<html>` before paint); bump CSS `?v`.
- `header.html`: theme-toggle `<button aria-pressed>` (accessible name, ☀/☾ icon).
- `app.js`: toggle handler — flip `data-theme`, persist to localStorage, sync
  `<meta theme-color>` + `aria-pressed`; bump JS `?v` in all consumers.
- **Acceptance:** toggle flips instantly, persists across reloads, no white flash;
  light theme renders correctly; respects OS preference when unset.

## B4 — Settings layout rebuild (MUST FIX) ✅ commit

- Stop reusing `.app-layout` on `settings.html`; give Settings its own centered
  layout (`max-width: var(--content-max)`).
- Section A "Конфигурация сессии" full-width (Фильтры → Сессия → Прогресс);
  optional balanced 2-col inside A on ≥1024.
- Section B "Управление данными" full-width band **below** A — never overlaps.
- Drop/repurpose the sidebar collapse toggle (not needed on a centered page).
- **Acceptance:** no overlap; no narrow rail; no empty 75% at 1440/1728/1920/2560;
  mobile order preserved; both themes.

## B5 — Edge alignment fixes ✅ commit

- Header `<h1>` `text-wrap: balance`; toggle fits without pushing title to 2 lines.
- Surface-tabs / primary nav: no 2-row wrap on narrow — horizontal scroll/condense.
- `<meta theme-color>` correct per theme (done in B3; verify).
- **Acceptance:** header one line at all widths; tabs never double-row.

## B6 — Stats: tame the long table ✅ commit

- Per-topic: show weakest ~8 by default + "Показать все" expander; full table
  scrolls within its own box; wider `--content-max-wide` column.
- **Acceptance:** stats page not an infinite simple-list; expander works; both themes.

## B7 — Polish pass ✅ commit

- A/B/C/D option badges: marginally stronger anchoring.
- Empty progress bar → explicit empty-state (no "broken 0%").
- Unify `:focus-visible` ring via `--focus-ring` across controls.
- Fold `ui-refinements.css` `--measure`/line-length into the system; reconcile
  with new tokens (keep the file, but point it at tokens).
- **Acceptance:** consistent states; clean empty states; focus ring uniform.

---

## Done = Phase 7

Re-run `/design-review` against the rebuilt UI in BOTH themes across the full
viewport matrix; update `DESIGN_REVIEW.md`; address any must-fix.
