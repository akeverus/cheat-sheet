# swiss

> Per-design TOKEN SPEC for `[data-design="swiss"]`. International Typographic Style: rigid grid, dramatic scale contrast, ALL-CAPS tracked sublabels, horizontal RULES as structure, FLAT (no shadows, no gradients), B/W + one signal red. Drives the `[data-design="swiss"]` + `[data-design="swiss"][data-theme="dark"]` token blocks in `tokens.css` per `docs/design/DESIGN.md` §2.

---

## 1. Character

Pure structure, zero atmosphere: black ink on white paper, hairline RULES doing the work that shadows do elsewhere, and a single signal red reserved for the one thing that matters on each screen. Hierarchy comes from dramatic scale contrast (huge announce next to tiny tracked ALL-CAPS sublabels) and from the grid itself, never from elevation or color variety. **The one bold move:** every card and surface is *flat with a single square-cornered hairline rule* — `--radius-*` is `0px` across the board and `--elevation-card` is `none` (not even a ring); separation is achieved by RULES and whitespace alone. The interface looks printed, not rendered.

---

## 2. Fonts

```
--font-family-display: "Geist", "Geist Sans", -apple-system, system-ui, "Segoe UI", Helvetica, Arial, sans-serif;
--font-family-body:    "Geist", "Geist Sans", -apple-system, system-ui, "Segoe UI", Helvetica, Arial, sans-serif;
--font-family-mono:    "Geist Mono", ui-monospace, "SF Mono", "JetBrains Mono", Menlo, Consolas, monospace;
```

Display and body share Geist (one family, two roles separated by weight + scale + tracking — true to the International Typographic single-grotesque tradition). Helvetica/Arial sit early in the fallback so the grotesque character survives if Geist is unloaded.

---

## 3. Color — LIGHT

| Token | Value | Notes |
|---|---|---|
| `--color-bg-primary` | `#FFFFFF` | paper |
| `--color-bg-secondary` | `#FFFFFF` | cards = same paper; separated by RULES, not tint |
| `--color-bg-tertiary` | `#F4F4F4` | inputs/well/code-bg — faint grey, the only surface tint |
| `--color-bg-inverse` | `#0A0A0A` | inverted block / footer band |
| `--color-surface-overlay` | `rgba(10,10,10,0.45)` | modal/dropdown scrim |
| `--color-text-primary` | `#0A0A0A` | near-black ink |
| `--color-text-secondary` | `#525252` | muted body / captions |
| `--color-text-tertiary` | `#737373` | placeholder/disabled |
| `--color-text-inverse` | `#FFFFFF` | text on inverse / on accent fill |
| `--color-link` | `#0A0A0A` | ink + underline (NOT red — see accent discipline) |
| `--color-border-primary` | `#0A0A0A` | structural RULES are full-strength black |
| `--color-border-secondary` | `#D4D4D4` | secondary dividers / input edges |
| `--color-border-focus` | `#E5231B` | signal red focus |
| `--color-accent` | `#E5231B` | signal red — CTA fill / active / progress |
| `--color-accent-hover` | `#C81D15` | darken ~8% |
| `--color-accent-active` | `#A8160F` | darken ~16% |
| `--color-accent-contrast` | `#FFFFFF` | text on red fill |
| `--color-accent-secondary` | `#0A0A0A` | categorical 2nd tone = black (charts/tags); NOT a 2nd brand color |
| `--color-status-success` | `#1B7A3D` | desaturated, prints-flat |
| `--color-status-warning` | `#B45309` | amber, AA on white |
| `--color-status-error` | `#E5231B` | reuses the signal red (state, not decoration) |
| `--color-status-info` | `#0A0A0A` | info = ink, not a 5th hue |
| `--color-status-success-bg` | `#EAF4EE` | optional tint |
| `--color-status-warning-bg` | `#FBF1E5` | optional tint |
| `--color-status-error-bg` | `#FCEAE9` | optional tint |
| `--color-status-info-bg` | `#F4F4F4` | optional tint |

**WCAG AA verification (against the surface each sits on):**
- `text-primary #0A0A0A` on `#FFFFFF` → **20.0:1** ✓ (body ≤16px needs 4.5:1)
- `text-secondary #525252` on `#FFFFFF` → **7.4:1** ✓
- `link #0A0A0A` on `#FFFFFF` → **20.0:1** ✓ (underline carries the affordance, not hue)
- `accent-contrast #FFFFFF` on `--color-accent #E5231B` fill → **4.55:1** ✓ (≥4.5:1 for button labels; red was tuned exactly here — a lighter red would fail, so `#E5231B` is the floor)
- `status-warning #B45309` on `#FFFFFF` → **4.9:1** ✓ (the documented amber `#D97706` is only 3.4:1 and would fail body text — corrected)
- `text-tertiary #737373` on `#FFFFFF` → **4.6:1** ✓ (placeholder passes body AA, exceeds large-text minimum)

**Accent discipline (≤2 visible uses of red per screen):** (1) the single primary CTA fill (e.g. "Ответить", "Начать сессию", "Далее"); (2) the focus-ring on the currently-focused control. Progress bars, the streak, selected-MCQ state, and the active-nav marker use the *active* moment they already own — but the rule is: **at most the CTA + the focus-ring show red at one time.** Links are ink-underlined, headings are black, dividers are black — red never becomes chrome.

---

## 4. Color — DARK overrides

Only tokens that change. Backgrounds are near-black (not pure), foreground near-white (not pure), hairlines become white-rgba.

| Token | Value | Notes |
|---|---|---|
| `--color-bg-primary` | `#0A0A0A` | not pure black |
| `--color-bg-secondary` | `#0A0A0A` | cards = same; RULES separate |
| `--color-bg-tertiary` | `#171717` | inputs/well/code-bg |
| `--color-bg-inverse` | `#FAFAFA` | inverted block on dark |
| `--color-surface-overlay` | `rgba(0,0,0,0.65)` | scrim |
| `--color-text-primary` | `#EDEDED` | not pure white |
| `--color-text-secondary` | `#A3A3A3` | muted |
| `--color-text-tertiary` | `#8A8A8A` | placeholder/disabled |
| `--color-text-inverse` | `#0A0A0A` | text on inverse / on accent fill stays light → see contrast |
| `--color-link` | `#EDEDED` | ink-light + underline |
| `--color-border-primary` | `rgba(255,255,255,0.22)` | structural RULES as white hairline |
| `--color-border-secondary` | `rgba(255,255,255,0.10)` | softer divider |
| `--color-border-focus` | `#FF4438` | brightened red for dark-surface focus |
| `--color-accent` | `#FF4438` | brighter signal red so it pops on `#0A0A0A` |
| `--color-accent-hover` | `#FF5C52` | lift toward light on dark (don't press into the bg) |
| `--color-accent-active` | `#E5231B` | the base red reads as "pressed" on dark |
| `--color-accent-contrast` | `#0A0A0A` | dark text on the brighter red — see contrast |
| `--color-accent-secondary` | `#EDEDED` | categorical 2nd tone = light ink |
| `--color-status-success` | `#3FB463` | lightened for dark AA |
| `--color-status-warning` | `#E0A340` | lightened amber |
| `--color-status-error` | `#FF4438` | matches dark accent |
| `--color-status-info` | `#EDEDED` | info = light ink |
| `--hairline` | `rgba(255,255,255,0.12)` | (see §6) |

**Dark accent re-check:**
- `text-primary #EDEDED` on `#0A0A0A` → **16.9:1** ✓
- `text-secondary #A3A3A3` on `#0A0A0A` → **8.1:1** ✓
- `accent-contrast #0A0A0A` on `--color-accent #FF4438` → **5.3:1** ✓ (dark label on bright red wins AA where white-on-bright-red would be ~3.0:1 and fail — that is why dark theme flips `--color-accent-contrast` to ink)
- `status-warning #E0A340` on `#0A0A0A` → **9.3:1** ✓

---

## 5. Typography tokens

```
--font-weight-read:      400
--font-weight-emphasize: 520   /* Geist Medium-ish; the working "emphasize" weight */
--font-weight-announce:  650   /* dramatic top of the scale; below 700 per craft */
```

Sizes (rem at scale=1, 16px root):
```
--font-size-xs:  0.75rem;   /* 12 — ALL-CAPS tracked sublabels */
--font-size-sm:  0.875rem;  /* 14 — UI labels, meta */
--font-size-base:1rem;      /* 16 — body floor (anti iOS-zoom) */
--font-size-md:  1.125rem;  /* 18 — lede */
--font-size-lg:  1.25rem;   /* 20 — H3 */
--font-size-xl:  1.5rem;    /* 24 — H2 */
--font-size-2xl: 2rem;      /* 32 — section title */
--font-size-3xl: 2.5rem;    /* 40 — H1 */
--font-size-4xl: 3.5rem;    /* 56 — display; the dramatic-scale-contrast anchor */
```

Line-height + tracking (Swiss runs tighter display, more aggressive caps than the structural default):
```
--line-height-tight:   1.05;   /* big announce/display — Swiss sets display nearly solid */
--line-height-snug:    1.25;
--line-height-normal:  1.55;
--line-height-relaxed: 1.7;

--tracking-tight:  -0.025em;   /* display ≥48px */
--tracking-snug:   -0.015em;   /* headings ≥32px */
--tracking-normal:  0;         /* body */
--tracking-wide:    0.02em;    /* UI labels */
--tracking-caps:    0.09em;    /* ALL CAPS — well above the 0.06em craft floor; the Swiss signature */
```

ALL-CAPS sublabels (section kickers, "ВОПРОС N ИЗ M", chip text) use `--font-size-xs` + `--tracking-caps` + `--font-weight-emphasize`. Display uses `--font-size-4xl` + `--tracking-tight` + `--line-height-tight`.

---

## 6. Component tokens (§2.4) — where swiss FEELS different

```
--radius-button: 0px;
--radius-card:   0px;
--radius-input:  0px;
--radius-pill:   0px;        /* even "pills" are square — Swiss rejects the capsule */

--border-width-card:  1px;
--border-width-input: 1px;

--card-surface:   var(--color-bg-secondary);   /* same paper; rule + space define the card */
--elevation-card: none;                         /* THE character knob — see justification */
--elevation-popover: 0 0 0 1px var(--color-border-primary);  /* hard 1px edge, no blur — popovers get a crisp rule, never a soft shadow */

--hairline: var(--color-border-secondary);      /* light: #D4D4D4 · dark: rgba(255,255,255,0.12) */

--focus-ring: 0 0 0 2px var(--color-bg-primary), 0 0 0 4px var(--color-border-focus);
              /* offset gap in paper color + 2px hard red ring; square corners inherit from radius:0 */

--button-fill-style: fill;   /* primary = solid red fill; secondary = outline (1px black rule, transparent), no ghost-blur */
```

**`--elevation-card: none` justification.** Swiss is FLAT by mandate (the brand anchor: "no shadows, no gradients"). A shadow contradicts the print metaphor; even a `0 0 0 1px` ring would round nothing but would still read as a "floating chip." Instead, cards are defined by **square 1px RULES + whitespace**. Where a hard edge is needed on all sides we apply `border: var(--border-width-card) solid var(--color-border-primary)`; where the grid alone suffices, a single top RULE plus generous `--space` does the separating. This is the distinctive 20% made structural.

**Density / space base.** Keep the structural 4px scale unchanged for predictable grid math, but Swiss runs a **tighter vertical card padding** and **wider section RULES gaps** — there is no extra space token, the design simply favors `--space-3/4` inside cards and `--space-7/8` between ruled sections, reinforcing the grid.

```
--space-1: 0.25rem;  /* 4 */   --space-2: 0.5rem;   /* 8 */
--space-3: 0.75rem;  /* 12 */  --space-4: 1rem;     /* 16 */
--space-5: 1.5rem;   /* 24 */  --space-6: 2rem;     /* 32 */
--space-7: 3rem;     /* 48 */  --space-8: 4rem;     /* 64 */
--space-9: 6rem;     /* 96 */
--measure: 68ch;
--max-width-page: 1180px;
--gutter: 24px;      /* 16px on sm via media; the grid inset never collapses to 0 */
```

---

## 7. Motion

Swiss keeps the structural defaults — motion confirms state, never performs. One tightening: transitions are **linear and brisk** to match the mechanical/grid feel; no bounce on UI chrome.

```
--duration-instant: 50ms;
--duration-fast:    120ms;   /* tightened from 150 — crisp, not springy */
--duration-normal:  200ms;   /* tightened from 250 */
--duration-slow:    400ms;
--duration-slower:  600ms;

--easing-default: cubic-bezier(0.2, 0, 0, 1);
--easing-in:      cubic-bezier(0.4, 0, 1, 1);
--easing-out:     cubic-bezier(0, 0, 0.2, 1);
--easing-bounce:  cubic-bezier(0.2, 0, 0, 1);   /* bounce DISABLED — Swiss has no spring; aliased to default */
```

All transitions collapse to near-0 under `prefers-reduced-motion: reduce`.

---

## 8. Anti-slop & component cues

**Confirmations:**
- No indigo hex anywhere; the only chromatic value is the brand `#E5231B` / dark `#FF4438`, always via `var(--color-accent)`.
- No rounded-card-with-colored-left-border: radius is `0` everywhere AND left-borders are not used as decoration — the canonical AI tile is impossible by construction (square corners + full RULES, never a colored stripe).
- No sans-on-display violation: display IS the specified grotesque (Geist), which is correct for this brand — no serif is prescribed, so Geist on display is intentional, not a substitution.
- No two-stop hero gradient, no emoji-as-icons (Lucide SVG sprite only), no fake metrics, no filler copy.

**Component cues:**
1. **Buttons** — primary: solid `--color-accent` red fill, `#FFFFFF` label, **square corners**, no shadow, `--font-weight-emphasize` + `--tracking-wide`; label often ALL-CAPS for the CTA. Secondary: transparent fill with a 1px black RULE outline, ink label. Hover = `--color-accent-hover` (no lift, no transform). Focus = the hard red `--focus-ring`.
2. **Cards** — flat white/near-black paper, **square 1px black/white-rgba RULE** as the boundary (or a single top RULE + whitespace inside a grid). No radius, no shadow, no ring-float. Card header carries a tiny ALL-CAPS tracked kicker above a large announce title — the scale-contrast signature.
3. **Inputs** — `--color-bg-tertiary` fill, 1px `--color-border-secondary` bottom-and-side RULE, square corners, label sits above as ALL-CAPS `--font-size-xs` tracked. Focus swaps the border to red and applies the `--focus-ring`; error swaps border to `--color-status-error` with a `role="alert"` message that says what/why/fix.
4. **MCQ-option rows** — full-width stacked rows divided by horizontal RULES (`--hairline`), square, no individual card chrome; the row's leading marker is a square ALL-CAPS letter tile (А/Б/В/Г) in a 1px box. Selected = red left-edge RULE *plus* red marker fill (the active moment, not decoration); correct/incorrect after answer use `--color-status-success`/`-error` on the marker only, never a tinted full-row wash.
5. **Chips / tags** — square (`--radius-pill: 0`), 1px RULE outline, ALL-CAPS `--font-size-xs` + `--tracking-caps`, ink-on-paper; category chips use `--color-accent-secondary` (black/light ink) outline so red stays reserved for the single CTA + focus per screen.
