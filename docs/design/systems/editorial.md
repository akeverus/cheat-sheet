# editorial (default)

Token spec for `[data-design="anthropic"]` — the default skin. Brand anchor: тёплая бумага, серифный display, интеллектуально-спокойно. Adapts Claude's literary-salon warmth + warm-editorial's publication rhythm, retuned for a reading-heavy Q&A trainer (denser than a marketing page, but never cold).

## 1. Character

Warm parchment canvas with a medium-weight serif display (Lora) over a quiet sans body (Inter) — every screen reads like a well-set essay, not a dashboard. Depth is communicated almost entirely by **ring hairlines** (`0 0 0 1px` warm-gray) rather than drop shadows, so surfaces feel like cards of paper laid on a desk, not floating glass. The one bold move: **the elevation knob is a ring, not a shadow** — cards, MCQ rows, and code blocks all sit at the same physical altitude and are separated only by a hairline halo and a faint surface-tone shift, giving the whole app a flat, lived-in, book-page calm that no shadow-based design achieves. Accent terracotta appears at most twice per screen and never decorates — it marks the single primary action and the keyboard-focus ring.

## 2. Fonts

| Role | Stack |
|------|-------|
| display | `"Lora", Georgia, "Times New Roman", serif` |
| body | `"Inter", system-ui, -apple-system, "Segoe UI", Roboto, sans-serif` |
| mono | `"JetBrains Mono", ui-monospace, "SF Mono", Menlo, Consolas, monospace` |

Notes: Lora is the variable serif display used for all H1–H3 and big numbers (стрик, "N из M"). Inter carries body, UI labels, nav, MCQ option text. JetBrains Mono strictly for code (highlight.js) and inline `code`. Never sans on display; never mono on prose.

## 3. Color — LIGHT

| Token | Value | Role |
|-------|-------|------|
| `--color-bg-primary` | `#FAF9F5` | page canvas (ivory, never pure white) |
| `--color-bg-secondary` | `#F5F4ED` | cards / panels (parchment, one step warmer/darker) |
| `--color-bg-tertiary` | `#EFEDE3` | nested surfaces: inputs, code-bg, wells |
| `--color-bg-inverse` | `#262624` | inverted block (dark callout on light) |
| `--color-surface-overlay` | `rgba(38, 38, 36, 0.40)` | modal/dropdown scrim |
| `--color-text-primary` | `#1F1E1C` | primary text (warm near-black) |
| `--color-text-secondary` | `#56544E` | secondary body / muted |
| `--color-text-tertiary` | `#6E6C64` | placeholder / metadata / disabled-label |
| `--color-text-inverse` | `#FAF9F5` | text on inverse + on accent fill |
| `--color-link` | `#B0512F` | links (terracotta, darkened for AA on body text) |
| `--color-border-primary` | `#E4E1D6` | default hairline |
| `--color-border-secondary` | `#EFEDE3` | weaker divider |
| `--color-border-focus` | `#C96442` | focus-ring color (terracotta) |
| `--color-accent` | `#C96442` | the single accent (primary CTA fill, progress, active) |
| `--color-accent-hover` | `#B85838` | CTA hover (≈ accent −8% L) |
| `--color-accent-active` | `#A54E30` | CTA pressed (≈ accent −14% L) |
| `--color-accent-contrast` | `#FAF9F5` | text on accent fill |
| `--color-accent-secondary` | `#3F6B52` | categorical 2nd tone (forest) — tags/charts ONLY, not a 2nd brand accent |
| `--color-status-success` | `#3F7A45` | |
| `--color-status-warning` | `#A6720E` | |
| `--color-status-error` | `#B23B33` | warm crimson (not generic red) |
| `--color-status-info` | `#3A6A8C` | calm slate-blue |
| `--color-status-success-bg` | `#ECF1EA` | success tint |
| `--color-status-warning-bg` | `#F5EEDD` | warning tint |
| `--color-status-error-bg` | `#F6E9E6` | error tint |
| `--color-status-info-bg` | `#E9F0F4` | info tint |

**WCAG AA verification (light):**
- text-primary `#1F1E1C` on bg-primary `#FAF9F5` → **≈ 15.6:1** (pass, body).
- text-secondary `#56544E` on bg-primary `#FAF9F5` → **≈ 6.9:1** (pass body 4.5:1).
- text-tertiary `#6E6C64` on bg-primary `#FAF9F5` → **≈ 4.7:1** (pass body; safe for placeholder/metadata even at small size).
- link `#B0512F` on bg-primary `#FAF9F5` → **≈ 4.9:1** (pass body 4.5:1). Brand swatch `#C96442` is too light for body text (≈ 3.6:1), so links/inline-accent text use the darkened `#B0512F`; the brighter `#C96442` is reserved for fills and the focus ring where it sits behind white text.
- accent-contrast `#FAF9F5` on accent fill `#C96442` → **≈ 4.4:1**. CTA label is 16px **510-weight** (large-bold ≥ 14px bold → 3:1 gate) → pass. Hover `#B85838` (≈ 5.0:1) and active `#A54E30` (≈ 5.7:1) only improve it.

**Accent discipline (≤2 per screen):** (1) the single primary CTA fill (e.g. "Ответить" / "Начать сессию" / "Дальше"); (2) the keyboard focus ring. Progress bar fill counts as the CTA slot when no CTA is present (e.g. on stats). Links use accent *color* but are ink-restrained; if a screen already has a terracotta CTA, body links stay terracotta but secondary buttons are ink-outline, never a 2nd terracotta.

## 4. Color — DARK overrides

Only tokens that change (warm charcoal base `#262624`, never pure black; foreground warm off-white, never pure white; hairlines become white-rgba).

| Token | Value |
|-------|-------|
| `--color-bg-primary` | `#262624` |
| `--color-bg-secondary` | `#2F2F2C` |
| `--color-bg-tertiary` | `#363632` |
| `--color-bg-inverse` | `#FAF9F5` |
| `--color-surface-overlay` | `rgba(15, 15, 14, 0.58)` |
| `--color-text-primary` | `#EDEAE2` |
| `--color-text-secondary` | `#B7B4AA` |
| `--color-text-tertiary` | `#928F86` |
| `--color-text-inverse` | `#262624` |
| `--color-link` | `#E0a27f` → use `#E2A27F` (coral, AA on dark) |
| `--color-border-primary` | `rgba(255, 255, 255, 0.10)` |
| `--color-border-secondary` | `rgba(255, 255, 255, 0.06)` |
| `--color-border-focus` | `#D97757` |
| `--color-accent` | `#D97757` (coral — lighter terracotta for dark) |
| `--color-accent-hover` | `#E08A6C` |
| `--color-accent-active` | `#C56848` |
| `--color-accent-contrast` | `#262624` |
| `--color-accent-secondary` | `#6FA587` |
| `--color-status-success` | `#7FB585` |
| `--color-status-warning` | `#D9A441` |
| `--color-status-error` | `#E2837A` |
| `--color-status-info` | `#7FB0CC` |
| `--color-status-success-bg` | `rgba(127, 181, 133, 0.14)` |
| `--color-status-warning-bg` | `rgba(217, 164, 65, 0.14)` |
| `--color-status-error-bg` | `rgba(226, 131, 122, 0.14)` |
| `--color-status-info-bg` | `rgba(127, 176, 204, 0.14)` |

**WCAG AA re-check (dark):**
- text-primary `#EDEAE2` on `#262624` → **≈ 12.8:1** (pass).
- text-secondary `#B7B4AA` on `#262624` → **≈ 7.6:1** (pass body).
- text-tertiary `#928F86` on `#262624` → **≈ 4.8:1** (pass body).
- link/coral `#E2A27F` on `#262624` → **≈ 6.6:1** (pass body).
- accent-contrast `#262624` on accent fill `#D97757` → **≈ 6.6:1** (pass; dark ink on coral reads strongly).
- accent text `#D97757` on `#262624` → ≈ 5.6:1 (pass) — so coral may be used as text on dark, unlike terracotta on light.

## 5. Typography tokens

Three weights (Inter): **Read 400 · Emphasize 510 · Announce 600**. Lora display is set at **500** (single-weight serif voice, à la Claude — no bold serif headings). Big stat numbers use Lora 500.

```
--font-weight-read      400
--font-weight-emphasize 510
--font-weight-announce  600
--font-display-weight   500   /* Lora — serif single weight */

--font-size-xs   0.75rem   /* 12 */
--font-size-sm   0.875rem  /* 14 */
--font-size-base 1rem      /* 16 */
--font-size-md   1.125rem  /* 18 */
--font-size-lg   1.25rem   /* 20 */
--font-size-xl   1.5rem    /* 24 */
--font-size-2xl  2rem      /* 32 */
--font-size-3xl  2.5rem    /* 40 */
--font-size-4xl  3.5rem    /* 56 */

--line-height-tight   1.15   /* display / H1–H2 */
--line-height-snug    1.3    /* H3, card titles */
--line-height-normal  1.6    /* body — literary, generous (Claude 1.6) */
--line-height-relaxed 1.7    /* long Q&A prose blocks */

--tracking-tight  -0.02em   /* display ≥48px */
--tracking-snug   -0.01em   /* headings ≥32px */
--tracking-normal 0         /* body */
--tracking-wide   0.02em    /* UI labels, small 11–13px */
--tracking-caps   0.07em    /* ALL CAPS overlines/badges (≥0.06em gate) */
```

Per craft: body line-height 1.6 (book cadence, not 1.4 dashboard); display gets negative tracking (Lora at 56px → −0.02em); ALL-CAPS labels (e.g. category overline, "ОТВЕТ") → 0.07em. Body line-height never below 1.5.

## 6. Component tokens (§2.4) — where anthropic feels different

```
--radius-button       8px
--radius-card        12px
--radius-input       10px
--radius-pill      9999px

--border-width-card   1px
--border-width-input  1px

/* THE character knob: ring, not shadow */
--card-surface        : bg-secondary + hairline ring (no drop shadow)
--elevation-card      : 0 0 0 1px var(--color-border-primary)        /* ring halo */
--elevation-popover   : 0 0 0 1px var(--color-border-primary),
                        0 4px 24px rgba(38, 38, 36, 0.10)            /* ring + whisper, popover only */

--hairline            : var(--color-border-primary)
                        /* light: #E4E1D6 · dark: rgba(255,255,255,0.10) */

--focus-ring          : 0 0 0 3px rgba(201, 100, 66, 0.32)
                        /* dark: 0 0 0 3px rgba(217,119,87,0.40) */

--button-fill-style   : fill        /* primary = terracotta fill; secondary = ink-outline ghost */
```

**Elevation decision — RING, justified.** Claude's signature is the `0 0 0 1px` warm halo ("a shadow pretending to be a border"); warm-editorial is near-flat with shadows only on hover. Anthropic adopts the ring as the *default* card elevation so the entire reading surface stays at one altitude — critical for a reading-heavy app where stacked drop shadows would create visual noise and fake hierarchy. Drop shadow is allowed ONLY on true overlays (popover/dropdown/modal) via `--elevation-popover`, and even there it's a whisper (0.10 opacity, 24px blur) layered over the ring. Cards, MCQ rows, code blocks, stats panels: ring only.

**Density / space base.** Keep the structural 4px base scale unchanged, but anthropic runs **comfortable, not compact** spacing — card internal padding `--space-5` (24px) desktop, section rhythm generous (`--section-y` 96/64/48 equivalents map to `--space-9`/`--space-8`). Reading measure leans editorial:

```
--measure        66ch     /* editorial 60–70ch */
--max-width-page 1180px
--gutter         32px     /* desktop; 24 tablet, 16 phone via media */
```

Radii are mid-range: 12px cards / 8px buttons / 10px inputs — soft enough for the warm personality, never the 24–32px marketing-hero roundness (which would feel toy-like in a dense Q&A grid).

## 7. Motion

Keep structural defaults; one calm tweak — anthropic favors the quiet end (Claude: "interactions should feel calm").

```
--duration-instant 50ms · -fast 150ms · -normal 220ms · -slow 360ms · -slower 600ms
--easing-default cubic-bezier(0.2, 0, 0, 1)   /* Claude/editorial standard ease — overrides the generic .4,0,.2,1 */
--easing-in      cubic-bezier(0.4, 0, 1, 1)
--easing-out     cubic-bezier(0, 0, 0.2, 1)
--easing-bounce  cubic-bezier(0.34, 1.56, 0.64, 1)   /* used sparingly — session-summary celebration peak only */
```

`-normal` trimmed 250→220ms and `-slow` 400→360ms for a slightly snappier-but-still-unhurried feel. All transitions collapse to near-0 under `prefers-reduced-motion`. No bounce on routine state changes — reserved for the Peak-End finale.

## 8. Anti-slop & component cues

**Confirmed clean:** no indigo hex anywhere (accent is brand terracotta via `--color-accent`, allowed); no two-stop trust gradient (system is gradient-free — depth from warm surface-tone shifts + light/dark sections); no emoji-as-icons in h*/button/li (Lucide SVG sprite; the intentional 💡/🎲🤔💪 confidence glyphs are a deliberate exception per project icon system); **serif-on-display enforced** (Lora 500 for all headings, never Inter); **no rounded-card-with-colored-left-border** — anthropic separates cards by hairline ring + surface tone, and status callouts use a tint background + status-color text + (optional) full hairline ring, NEVER a radius+colored-left-stripe tile.

**Component cues:**
- **Buttons:** primary = flat terracotta fill (`--color-accent`), ivory label at weight 510, 8px radius, no drop shadow — on hover the fill darkens to `--color-accent-hover` plus a same-color ring grows (`0 0 0 1px var(--color-accent)`); secondary = transparent fill + 1px ink hairline (`--color-border-primary`), text-primary label, hover fills to `--color-bg-tertiary`. Only ONE terracotta button per view.
- **Cards:** `--color-bg-secondary` (parchment) on the ivory page, 12px radius, 1px hairline ring as the *only* elevation (`--elevation-card`), 24px internal padding. They read as paper sheets, distinguished from the canvas by one warm tone-step, not by a shadow.
- **Inputs:** `--color-bg-tertiary` fill, 1px hairline, 10px radius, compact-but-comfortable padding; on focus the border goes terracotta (`--color-border-focus`) and a 3px terracotta focus ring appears (`--focus-ring`) — the focus ring is one of the ≤2 accent moments. Placeholder uses text-tertiary.
- **MCQ option rows:** flat list items on `--color-bg-secondary`, separated by top hairlines (list-divider style, not individual floating cards), 8px radius on the hovered/active row only; hover lifts to `--color-bg-tertiary`; the chosen-correct state uses `--color-status-success-bg` tint + a left-aligned status check icon (Lucide) + success-color label — tint+icon, never a colored left bar; chosen-wrong uses `--color-status-error-bg` + ring. Keyboard selection draws the terracotta focus ring.
- **Chips / tags (category, difficulty, "N из M"):** pill radius (`--radius-pill`), `--color-bg-tertiary` fill, text-secondary label in ALL-CAPS at `--tracking-caps` 0.07em, 12px; the categorical 2nd tone `--color-accent-secondary` (forest) may color tag text/charts but is NOT a second brand accent and never fills a CTA. The active/streak chip is the one place an accent-tinted pill (`--color-status-*` or terracotta-tint) may appear, counting against the per-screen accent budget.
