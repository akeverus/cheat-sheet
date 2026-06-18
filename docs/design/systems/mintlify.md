# mintlify

## 1. Character

Docs-as-product made tactile: a luminous white canvas where separation comes from 5%-opacity hairlines and whitespace, never from gray panels or heavy shadows — the page reads like beautifully-set documentation, calm and engineered. Type does the work (Inter with tight negative display tracking, Geist Mono ALL-CAPS for code labels), and mint-green appears only twice per screen so it always means "interactive." **The one bold move: the full-pill (9999px) radius on every button, input, and chip combined with near-flat, ring-only elevation** — components are identified at a glance by their rounded edges floating on a borderless paper surface, not by drop-shadows.

## 2. Fonts

```
--font-family-display: "Inter", "Inter Fallback", system-ui, -apple-system, "Segoe UI", Helvetica, Arial, sans-serif;
--font-family-body:    "Inter", "Inter Fallback", system-ui, -apple-system, "Segoe UI", Helvetica, Arial, sans-serif;
--font-family-mono:    "Geist Mono", "Geist Mono Fallback", ui-monospace, SFMono-Regular, "SF Mono", Menlo, Monaco, Consolas, monospace;
```

Single Inter family for display+body (docs voice = one human typeface, strict boundary). Geist Mono is reserved exclusively for code blocks, inline `code`, kbd, and uppercase technical labels — never mixed into prose.

## 3. Color — LIGHT

| token | value | role |
|---|---|---|
| `--color-bg-primary` | `#FFFFFF` | page background (pure white canvas) |
| `--color-bg-secondary` | `#FFFFFF` | cards/panels (separated by hairline, not fill) |
| `--color-bg-tertiary` | `#FAFAFA` | inputs / wells / code-bg (Gray 50 tint) |
| `--color-bg-inverse` | `#0D0D0D` | inverted block (near-black) |
| `--color-surface-overlay` | `rgba(13, 13, 13, 0.32)` | modal/dropdown scrim |
| `--color-text-primary` | `#0D0D0D` | headings, body (near-black, micro-soft) |
| `--color-text-secondary` | `#4A4A4A` | descriptions, body copy |
| `--color-text-tertiary` | `#6B6B6B` | placeholder / muted labels |
| `--color-text-inverse` | `#FFFFFF` | text on inverse block / on dark CTA |
| `--color-link` | `#0FA76E` | links (deep green — accent at readable contrast) |
| `--color-border-primary` | `rgba(0, 0, 0, 0.08)` | default hairline (interactive/card edge) |
| `--color-border-secondary` | `rgba(0, 0, 0, 0.05)` | signature 5% section/divider hairline |
| `--color-border-focus` | `#18E299` | focus ring color (brand mint) |
| `--color-accent` | `#18E299` | the single accent — progress/active/brand pill |
| `--color-accent-hover` | `#0FA76E` | Brand Green Deep (documented hover) |
| `--color-accent-active` | `#0B8659` | deeper still (pressed) |
| `--color-accent-contrast` | `#0D0D0D` | text/icon on mint fill (mint is high-luminance) |
| `--color-accent-secondary` | `#3772CF` | categorical 2nd tone (tags/charts only, not 2nd brand) |
| `--color-status-success` | `#0F9A60` | success (darkened from accent for AA text use) |
| `--color-status-success-bg` | `#D4FAE8` | success tint |
| `--color-status-warning` | `#B5740C` | warning (Warm Amber, darkened for AA) |
| `--color-status-warning-bg` | `#FBF1DC` | warning tint |
| `--color-status-error` | `#C0392B` | error (Error Red, darkened for AA) |
| `--color-status-error-bg` | `#FBE4E1` | error tint |
| `--color-status-info` | `#2F66BD` | info (Soft Blue, darkened for AA) |
| `--color-status-info-bg` | `#E4EDFB` | info tint |

**WCAG AA verification (on `--color-bg-primary` #FFFFFF unless noted):**
- text-primary `#0D0D0D` on `#FFFFFF` → **19.9:1** ✓ (needs 4.5)
- text-secondary `#4A4A4A` on `#FFFFFF` → **8.9:1** ✓ (raised from DESIGN.md's `#666` 5.7:1 for safety margin in dense Q&A prose)
- text-tertiary `#6B6B6B` on `#FFFFFF` → **5.1:1** ✓ (placeholder still passes body 4.5; DESIGN.md `#888` failed at 3.5:1 — fixed)
- link `#0FA76E` on `#FFFFFF` → **3.0:1** — FAILS body 4.5:1 as plain text, so links are **always underlined + use `#0FA76E`**; for body-size inline links bump to **`#0B8659`** → **4.6:1** ✓. (Use `--color-link: #0B8659` if links must be color-only; underlined links may keep `#0FA76E`.) Set `--color-link: #0B8659`.
- accent-contrast `#0D0D0D` on accent fill `#18E299` → **12.3:1** ✓ (white on `#18E299` = 1.6:1, would fail — dark text is correct)

Correction applied: `--color-link: #0B8659` (AA-safe inline). Underline always present per Mintlify (links rely on underline/context).

**Accent discipline (≤2 per screen):** mint `--color-accent` appears in exactly two slots per screen — (1) the active/primary interactive signal (progress bar fill, selected MCQ-option ring, the brand "promotional" pill) and (2) the focus ring. Primary CTA is near-black `#0D0D0D`, NOT mint. Links use deep-green `#0B8659` which reads as ink-green, not as the bright accent fill, so it doesn't consume an accent slot.

## 4. Color — DARK overrides

Only changed tokens:

| token | value | note |
|---|---|---|
| `--color-bg-primary` | `#0D0D0D` | near-black (not pure #000) |
| `--color-bg-secondary` | `#141414` | card sits slightly above page |
| `--color-bg-tertiary` | `#1B1B1B` | input/code well |
| `--color-bg-inverse` | `#FAFAFA` | inverted = near-white |
| `--color-surface-overlay` | `rgba(0, 0, 0, 0.6)` | darker scrim |
| `--color-text-primary` | `#EDEDED` | near-white (not pure #fff) |
| `--color-text-secondary` | `#B0B0B0` | muted |
| `--color-text-tertiary` | `#8A8A8A` | placeholder |
| `--color-text-inverse` | `#0D0D0D` | text on near-white inverse |
| `--color-link` | `#18E299` | bright mint reads fine on dark |
| `--color-border-primary` | `rgba(255, 255, 255, 0.12)` | white hairline (interactive) |
| `--color-border-secondary` | `rgba(255, 255, 255, 0.07)` | white 7% signature hairline |
| `--color-accent-active` | `#0FA76E` | pressed = deep green on dark |
| `--color-accent-contrast` | `#0D0D0D` | dark text still correct on mint fill |
| `--color-status-success` | `#3DD68C` | brighter for dark legibility |
| `--color-status-success-bg` | `rgba(24, 226, 153, 0.14)` | translucent tint |
| `--color-status-warning` | `#E0A93B` | brighter amber |
| `--color-status-warning-bg` | `rgba(195, 125, 13, 0.18)` | |
| `--color-status-error` | `#F0746A` | brighter red |
| `--color-status-error-bg` | `rgba(212, 86, 86, 0.18)` | |
| `--color-status-info` | `#6FA0E8` | brighter blue |
| `--color-status-info-bg` | `rgba(55, 114, 207, 0.18)` | |

**Dark accent re-check:** accent `#18E299` unchanged (works on both per brand). text-primary `#EDEDED` on `#0D0D0D` → **17.0:1** ✓; text-secondary `#B0B0B0` on `#0D0D0D` → **9.4:1** ✓; link `#18E299` on `#0D0D0D` → **12.5:1** ✓; accent-contrast `#0D0D0D` on `#18E299` → 12.3:1 ✓. Borders use white-rgba hairlines per craft rule (not black).

## 5. Typography tokens

```
--font-weight-read:      400   /* body / reading */
--font-weight-emphasize: 500   /* UI, nav, emphasis — Mintlify's middle weight */
--font-weight-announce:  600   /* headings / titles — no 700 in the system */
```

Three weights only (Mintlify forbids bold 700). `emphasize` stays at 500 (not 510) to match the brand's documented UI weight exactly.

Size scale (structural, rem @ 16px root): `--font-size-xs` 0.75rem(12) · `-sm` 0.875rem(14) · `-base` 1rem(16) · `-md` 1.125rem(18) · `-lg` 1.25rem(20) · `-xl` 1.5rem(24) · `-2xl` 2rem(32) · `-3xl` 2.5rem(40) · `-4xl` 3.5rem(56).

Line-height: `--line-height-tight` 1.15 · `-snug` 1.3 · `-normal` 1.55 · `-relaxed` 1.7.

Tracking (per craft + Mintlify scaling):
```
--tracking-tight:  -0.02em   /* display ≥48px (≈ -1.12px @56) — Mintlify compressed hero */
--tracking-snug:   -0.01em   /* headings 32–40px */
--tracking-normal:  0        /* body */
--tracking-wide:    0.02em   /* UI labels, links */
--tracking-caps:    0.07em   /* ALL CAPS Geist Mono code labels — ≥0.06em floor satisfied */
```

Display headings (≥32px) use weight 600 + negative tracking; ALL-CAPS mono labels use `--tracking-caps` 0.07em (Mintlify's documented 0.6–0.65px at 12px ≈ 0.05–0.054em, bumped to 0.07em to clear the craft 0.06em floor).

## 6. Component tokens (§2.4)

```
--radius-button: 9999px;     /* SIGNATURE full pill */
--radius-card:   16px;        /* standard card; featured panels may use 24px locally */
--radius-input:  9999px;      /* pill input — matches buttons (Mintlify signature) */
--radius-pill:   9999px;

--border-width-card:  1px;
--border-width-input: 1px;

--card-surface: var(--color-bg-secondary);   /* white fill, separated by ring below */
--elevation-card:    0 0 0 1px var(--color-border-secondary);   /* RING, not shadow */
--elevation-popover: 0 0 0 1px var(--color-border-primary), 0 8px 24px rgba(0,0,0,0.08);

--hairline: var(--color-border-secondary);   /* 5% black light / 7% white dark */
--focus-ring: 0 0 0 2px var(--color-bg-primary), 0 0 0 4px var(--color-border-focus);
--button-fill-style: fill;   /* primary = near-black solid fill */
```

**elevation-card = RING (`0 0 0 1px`), not shadow — justified:** DESIGN.md §6 is explicit that Mintlify's depth is *border-driven, not shadow-driven* ("Mintlify barely uses shadows… paper-like"). A ring at the 5% hairline reproduces the white-on-white card separation exactly; a drop-shadow would betray the engineered, flat-paper feel. This is the character knob that most distinguishes mintlify from shadow-using designs. An optional whisper `0 2px 4px rgba(0,0,0,0.03)` may be layered on hover only.

**focus-ring** is a 2px offset gap + 2px mint ring (double box-shadow: first ring paints bg-color gap so the mint floats clear of pill edges on the white canvas, keeping keyboard focus unmistakable without a diffuse halo).

**Density / space base:** keep structural 8px base; section rhythm is generous (documentation-grade): `--space-9` 96px for desktop section breaks, gutters 24→32px. No density compression — Mintlify sells reading comfort, so spacing stays roomy (cards 24px padding, body measure ~68ch).

## 7. Motion

Mintlify's voice lives in type, color restraint, and whitespace — **not** animation. Keep all structural defaults:
```
--duration-instant 50ms · -fast 150ms · -normal 250ms · -slow 400ms · -slower 600ms
--easing-default cubic-bezier(.4,0,.2,1) · -in · -out · -bounce(.34,1.56,.64,1)
```
One design-specific nudge: prefer `--duration-fast` (150ms) for hover/border transitions (Mintlify's documented `--motion-fast`), and avoid `-bounce` entirely — bounce contradicts the calm, engineered feel. All transitions collapse to near-0 under `prefers-reduced-motion`.

## 8. Anti-slop & component cues

**Anti-slop confirmations:**
- No Tailwind-indigo hex — `--color-accent-secondary #3772CF` is the brand's documented Soft Blue (categorical only), not `#6366f1`; mint accent is the intentional brand color via `var(--accent)`.
- No two-stop hero "trust" gradient as a generic device — the atmospheric green-white wash, if used, is a single brand-tinted background flourish, not a purple→blue card gradient.
- No emoji-as-icons — Lucide SVG sprite only.
- No serif-on-display violation — Inter is intentionally the display face (docs voice), no serif spec to break.
- **No rounded-card-with-colored-left-border** — cards are pure white + hairline ring with NO colored left border. The canonical AI-tile is explicitly avoided; category/status is signaled by a small mono-CAPS chip or icon inside the card, never a colored left rail.

**Component cues:**
- **Buttons:** full-pill (9999px). Primary = solid near-black `#0D0D0D` fill, white label, Inter 15px/500, micro-shadow `0 1px 2px rgba(0,0,0,0.06)`, hover → opacity 0.9. Secondary = white pill, `#0D0D0D` label, 1px `rgba(0,0,0,0.08)` border. The mint "brand-accent" pill (dark text on `#18E299`) is rare — promo CTA only.
- **Cards:** white surface, 16px radius (24px featured), 1px 5%-hairline RING, no fill tint, generous 24px padding; hover darkens border to 8% (no lift). Inner content blocks may nest their own 16px containers.
- **Inputs:** pill (9999px) to match buttons, white/`#FAFAFA` fill, 1px `rgba(0,0,0,0.08)` border, placeholder `#6B6B6B`; focus → 2px mint ring + mint border. On-blur validation; error border = `--color-status-error` with role=alert message.
- **MCQ-option rows:** pill or 16px-radius rows on white, 1px hairline, 24px touch-safe height; resting = hairline only; hover = border→8%; **selected = 2px mint ring + `#D4FAE8` tint fill** (one of the two allowed accent uses); correct/incorrect after submit use `--color-status-success`/`-error` ring + tint, never the mint accent (keeps accent meaning "interactive," not "graded").
- **Chips:** full-pill, Geist Mono 12px ALL-CAPS, `--tracking-caps` 0.07em; default = `#FAFAFA` fill + 5% hairline + `#6B6B6B` text; brand chip = `#D4FAE8` fill + `#0FA76E` text (the documented mint badge). The mono-CAPS chip is Mintlify's signature "terminal voice" label.
