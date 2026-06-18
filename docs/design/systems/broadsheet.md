# broadsheet

> Per-design TOKEN SPEC for `[data-design="broadsheet"]`. Газетный editorial: serif display, folio/dateline details, multi-column rhythm, thin rules as dividers, ink-led restraint with a single masthead-red tone. Light + dark. Directly convertible to a `[data-design="broadsheet"]` CSS token block.

## 1. Character

A printed broadsheet wired to a wall socket: warm near-white newsprint stock, ink-black serif headlines, hairline rules doing the structural work that shadows would do elsewhere, and mono ALL-CAPS kickers/datelines marking every block like pica labels. Restraint is the rule — one inked masthead-red (`#A21B24`) is the only color outside grayscale, used as section signal, link, and progress ink, never as a fill wash. **The one bold move:** elevation is *flat-by-religion* — cards are pure surface separated by a 1px ink hairline plus a left "column rule" on the masthead row only; the distinctive 20% is a **folio/dateline strip** (mono caps, hairline-underscored) sitting above every question, turning each card into a clipping from a paper.

## 2. Fonts

```
--font-family-display: "Source Serif 4", "Source Serif Pro", Georgia, "Times New Roman", "Noto Serif", serif;
--font-family-body:    "Inter", "Inter var", -apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", Arial, sans-serif;
--font-family-mono:    "JetBrains Mono", "SFMono-Regular", "Cascadia Code", "Fira Code", ui-monospace, Menlo, Consolas, monospace;
```

- Display = Source Serif 4 for ALL headings/Q-titles/numerals (folio "01" style). Body = Inter for prose, decks, UI, MCQ option text. Mono = JetBrains Mono for kickers, datelines, timestamps, tags, code, button labels — **always UPPERCASE when used as a label**.
- Max two families per cluster: serif headline never sets UI; Inter never sets a headline.

## 3. Color — LIGHT

| Token | Value |
|---|---|
| `--color-bg-primary` | `#FEFFFC` (newsprint warm-white) |
| `--color-bg-secondary` | `#F6F5F0` (clipping / card stock) |
| `--color-bg-tertiary` | `#EDEBE3` (inset well / input / code-bg) |
| `--color-bg-inverse` | `#16161A` (ink ribbon block) |
| `--color-surface-overlay` | `rgba(22,22,26,0.42)` (modal scrim) |
| `--color-text-primary` | `#16161A` — **17.98:1** on bg-primary ✓ |
| `--color-text-secondary` | `#55555E` — **7.35:1** ✓ |
| `--color-text-tertiary` | `#70707A` — **4.88:1** ✓ (clears body min) |
| `--color-text-inverse` | `#FEFFFC` (on inverse / on accent fill) |
| `--color-link` | `#A21B24` — **7.70:1** ✓ (masthead-red, underline always present) |
| `--color-border-primary` | `#16161A` (1px ink hairline — printerly column rule) |
| `--color-border-secondary` | `rgba(22,22,26,0.16)` (quiet `<hr>`-grade rule) |
| `--color-border-focus` | `#A21B24` |
| `--color-accent` | `#A21B24` (inked masthead red) |
| `--color-accent-hover` | `#8A171F` — **9.41:1** ✓ |
| `--color-accent-active` | `#74121A` — 11.35:1 ✓ |
| `--color-accent-contrast` | `#FEFFFC` — **7.70:1** on accent fill ✓ |
| `--color-accent-secondary` | `#2E4A6B` (editorial slate — charts/tags only, NOT a 2nd brand accent) — 9.07:1 ✓ |
| `--color-status-success` | `#1B7A3D` (5.37:1 ✓) |
| `--color-status-warning` | `#9A6700` (4.85:1 ✓) |
| `--color-status-error` | `#B0151F` (7.04:1 ✓) |
| `--color-status-info` | `#1F5FA8` (6.42:1 ✓) |
| `--color-status-success-bg` | `#E9F2EB` |
| `--color-status-warning-bg` | `#F6EFDF` |
| `--color-status-error-bg` | `#F7E9EA` |
| `--color-status-info-bg` | `#E8EFF7` |

**Accent discipline (≤2 per screen):** (1) the current section dateline/kicker + active-tab underscore, (2) one element of {primary CTA fill · correct-answer mark · progress-bar ink}. Links use the accent underline but count as ambient editorial linking, not a "spot" — keep total *visible blocks/fills* of `--accent` ≤2.

## 4. Color — DARK overrides

Only tokens that change. Backgrounds not pure black, foreground not pure white; hairlines become white-rgba.

| Token | Value |
|---|---|
| `--color-bg-primary` | `#15151D` (ink-blue newsprint negative) |
| `--color-bg-secondary` | `#1D1D27` (raised clipping) |
| `--color-bg-tertiary` | `#24242F` (inset well / input / code-bg) |
| `--color-bg-inverse` | `#ECEBE6` (paper ribbon on dark) |
| `--color-surface-overlay` | `rgba(0,0,0,0.58)` |
| `--color-text-primary` | `#ECEBE6` — **15.21:1** ✓ |
| `--color-text-secondary` | `#A4A4AD` — **7.34:1** ✓ |
| `--color-text-tertiary` | `#86868F` — **5.03:1** ✓ |
| `--color-text-inverse` | `#15151D` (on paper ribbon / on accent fill) |
| `--color-link` | `#E8736C` — **6.14:1** ✓ (lightened red — pure `#A21B24` fails on dark) |
| `--color-border-primary` | `rgba(255,255,255,0.16)` (white hairline) |
| `--color-border-secondary` | `rgba(255,255,255,0.08)` |
| `--color-border-focus` | `#E8736C` |
| `--color-accent` | `#E8736C` (paper-side masthead red) |
| `--color-accent-hover` | `#EE847D` |
| `--color-accent-active` | `#D9605A` |
| `--color-accent-contrast` | `#15151D` — **6.14:1** on accent fill ✓ |
| `--color-accent-secondary` | `#7FA8D6` (slate, 7.33:1 ✓) |
| `--color-status-success` | `#5FD089` (9.40:1) |
| `--color-status-warning` | `#E0B24A` (9.19:1) |
| `--color-status-error` | `#F2746C` (6.47:1) |
| `--color-status-info` | `#6FA8E8` (7.30:1) |
| `--color-status-*-bg` | success `rgba(95,208,137,0.12)` · warning `rgba(224,178,74,0.12)` · error `rgba(242,116,108,0.12)` · info `rgba(111,168,232,0.12)` |

## 5. Typography tokens

```
/* families — see §2 */
--font-size-xs:   0.75rem;   /* 12 */
--font-size-sm:   0.875rem;  /* 14 */
--font-size-base: 1rem;      /* 16 body (Inter) */
--font-size-md:   1.125rem;  /* 18 deck/lead */
--font-size-lg:   1.25rem;   /* 20 */
--font-size-xl:   1.5rem;    /* 24 sub-head (serif) */
--font-size-2xl:  2rem;      /* 32 section head (serif) */
--font-size-3xl:  2.5rem;    /* 40 Q-title / feature (serif) */
--font-size-4xl:  3.5rem;    /* 56 masthead display (serif) */

/* weights — serif display carries weight; Inter UI stays light-ish, broadsheet is ink-not-bold */
--font-weight-read:      400;
--font-weight-emphasize: 500;
--font-weight-announce:  600;   /* serif headlines + ribbon labels; 700 reserved for masthead only */

--line-height-tight:   1.12;   /* serif display ≥40px */
--line-height-snug:    1.28;   /* headings 24–32px */
--line-height-normal:  1.55;   /* UI / decks */
--line-height-relaxed: 1.68;   /* long-form serif-adjacent body, generous newsprint leading */

--tracking-tight:  -0.022em;   /* display ≥48px */
--tracking-snug:   -0.012em;   /* headings 24–40px */
--tracking-normal:  0;         /* body */
--tracking-wide:    0.02em;    /* UI labels */
--tracking-caps:    0.09em;    /* mono ALL-CAPS kickers/datelines/tags — well above 0.06em floor */
```

- Two registers only: positive (`+0.09em`) for ALL-CAPS mono labels, negative (`−0.012…−0.022em`) for large serif display. Plain `0` on body Inter.
- Headlines lean on *size + serif weight 600*, not heavy bolding — the газетный voice is ink-contrast, not weight-contrast.

## 6. Component tokens (§2.4) — where broadsheet must FEEL different

```
--radius-button: 0px;          /* square — printerly, not webby */
--radius-card:   0px;          /* clippings have square corners */
--radius-input:  0px;
--radius-pill:   9999px;       /* reserved ONLY for inline status spans / chips that must read as pills */

--border-width-card:  1px;     /* hairline ink rule, not a heavy frame */
--border-width-input: 1.5px;   /* slightly heavier so the field reads as a "form box" on paper */

/* THE character knob — flat by religion: a 1px ring hairline, never a drop shadow */
--elevation-card:    0 0 0 1px var(--color-border-primary);     /* ring-as-rule, NO shadow */
--elevation-popover: 0 0 0 1px var(--color-border-primary), 0 8px 28px rgba(22,22,26,0.16);
   /* dark: 0 0 0 1px rgba(255,255,255,0.14), 0 10px 32px rgba(0,0,0,0.5) */

--hairline: var(--color-border-secondary);   /* the <hr>/divider tint; white-rgba on dark (see §4) */

--focus-ring: 0 0 0 2px var(--color-bg-primary), 0 0 0 4px var(--color-border-focus);
   /* double-ring: paper gap then ink-red ring — visible on any surface */

--button-fill-style: outline;  /* default CTA is 1.5px ink box that inverts on hover (WIRED rule), NOT a soft fill */
```

**Justification — flat, ring-only:** газетный broadsheets ship zero shadows; depth is rule-weight (1px hairline → 1.5px box → solid ink ribbon). A drop shadow would break the paste-up contract instantly. So `--elevation-card` is a `0 0 0 1px` ring (the hairline column rule), and the only thing allowed a true shadow is the popover/modal (it floats *above* the paper, so a soft 8–10px shadow is legitimate). **Density shift:** broadsheet runs tighter than a SaaS skin — keep `--space-*` defaults but the editorial measure is narrower (60–66ch) and section rhythm uses hairline rules instead of large gaps.

```
--measure: 64ch;            /* editorial column, tighter than 70ch generic */
--max-width-page: 1200px;   /* broadsheet page width */
--gutter: 2rem;             /* 32px; tighten to 1rem on sm */
/* --space-1..9 keep structural defaults (4..96px) */
```

## 7. Motion

Mostly defaults; broadsheet is editorial-restrained — color/border swaps, never bounce.

```
--duration-instant: 50ms;
--duration-fast:    120ms;   /* tightened from 150: hover ink-swaps are near-instant, printerly */
--duration-normal:  200ms;
--duration-slow:    360ms;
--duration-slower:  600ms;
--easing-default: cubic-bezier(0.2, 0, 0, 1);   /* standard, no overshoot */
--easing-in:      cubic-bezier(0.4, 0, 1, 1);
--easing-out:     cubic-bezier(0, 0, 0.2, 1);
--easing-bounce:  cubic-bezier(0.2, 0, 0, 1);   /* bounce neutralized to standard — no springy motion in newsprint */
```

Hover = color/background/border transition only (no lift, no scale). All under `prefers-reduced-motion: reduce` → ≤1ms.

## 8. Anti-slop & component cues

**Confirmed clean:** accent is газетный masthead-red `#A21B24` (a deliberate brand tone), **no Tailwind indigo hex anywhere**. No rounded-card-with-colored-left-border AI-tile (cards are `radius:0` + hairline ring; the only "left rule" is on the masthead row, and it carries no radius). No two-stop hero gradient (zero gradients — color in solid ink only). Serif-on-display honored (Source Serif 4 sets every heading; Inter never sets a headline). No emoji-as-icons (mono caps + Lucide sprite); confidence 🎲🤔💪 kept per project decision.

**Concrete cues:**
- **Buttons** — square (`radius:0`), `1.5px solid var(--color-accent)` outline box, accent-colored mono-or-Inter label in `tracking-wide`; **hover inverts to a solid ink-red fill with `--color-accent-contrast` text** in 120ms. Secondary button = same box in `--color-border-primary` (ink), inverts to ink fill. No shadow ever; focus adds the double-ring `--focus-ring`.
- **Cards (Q-blocks)** — pure `--color-bg-secondary`, square, `--elevation-card` (1px hairline ring), topped by a **folio/dateline strip**: mono `font-size-xs` UPPERCASE `tracking-caps` line (e.g. `RUST · OWNERSHIP · Q07`) in `--color-text-secondary`, underscored by a 1px `--hairline`. Hover = headline link color shifts to `--color-link`; the card never lifts.
- **Inputs** — square, `1.5px solid` ink box on `--color-bg-tertiary`, Inter `font-size-base` (≥16px, anti iOS-zoom), placeholder in `--color-text-tertiary`. Focus: border → `--color-border-focus` + double-ring; error: border → `--color-status-error`, message below in `role=alert`.
- **MCQ option rows** — square hairline-ruled rows (1px `--hairline` between rows, no per-row card), prefixed by a serif folio letter `A`/`B`/`C`/`D` in `--color-text-secondary`; hover tints background to `--color-bg-secondary`. Correct = left edge gets a 3px `--color-status-success` ink bar **with no radius** (rule, not pill) + success-bg tint; wrong-picked = `--color-status-error` bar + error-bg. Selected-pending = `1.5px` ink box.
- **Chips/tags** — these are the *only* pills (`--radius-pill`): mono `font-size-xs` UPPERCASE `tracking-caps`, hairline outline on transparent (category tag) or solid `--color-bg-inverse`/`--color-text-inverse` for a "BREAKING"-style ribbon span; status chips use `--color-status-*` text on the matching `-bg` tint.
