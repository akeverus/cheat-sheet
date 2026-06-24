# theverge

## 1. Character

Tech-media at full volume: a confident, "hazard-tape" publication system where the page shouts in **neon jelly-mint** (`#3CFFD0`) and structure is drawn with flat 1px rules, never shadows — the print-poster feel of a bold editorial front page rendered as UI. Inter does the heavy lifting at **weight 800** for display, compressed to a hulking `0.95` line-height so headlines read as solid blocks of ink. Mint is so high-luminance that it is *only ever a fill*, always carrying near-black `#131313` ink-on; white on mint would collapse to 1.3:1, so dark text is mandatory. **The three bold moves: (1) pill geometry — 20px cards / 24px featured radii so buttons, chips, and cards read as pills; (2) heavy 800 display weight; (3) zero drop-shadows — every elevation is a flat 1px border.** These are structural signatures, not a recolor. Note: The Verge's public identity is dark-canvas-first, so the LIGHT palette here is partly *derived* — the mint, ultraviolet, and pill DNA are authentic, but the white canvas and dark-mint text token are engineered to keep the loud system AA-legible on white.

## 2. Fonts

```
--font-family-display: 'Inter', 'Helvetica Neue', Impact, Helvetica, Arial, sans-serif;
--font-family-body:    'Inter', 'Helvetica Neue', Helvetica, Arial, sans-serif;
--font-family-mono:    'Geist Mono', 'JetBrains Mono', ui-monospace, 'Courier New', monospace;
```

Inter for display+body (Manuka — The Verge's heavyweight display face — is unavailable, so the heaviest Inter weight stands in; Impact sits in the display fallback chain to preserve the shout if Inter fails to load). Geist Mono substitutes for PolySans Mono and is reserved for code, kbd, and UPPERCASE "telegraph ticker" labels — never mixed into prose.

## 3. Color — LIGHT

| token | value | role |
|---|---|---|
| `--color-bg-primary` | `#FFFFFF` | page background (white canvas) |
| `--color-bg-secondary` | `#F4F4F4` | cards/panels (light gray tint) |
| `--color-bg-tertiary` | `#E9E9E9` | inputs / wells / code-bg |
| `--color-bg-inverse` | `#131313` | inverted block (canvas black) |
| `--color-surface-overlay` | `rgba(19, 19, 19, 0.5)` | modal/dropdown scrim |
| `--color-text-primary` | `#131313` | headings, body (canvas-black ink) |
| `--color-text-secondary` | `#585858` | descriptions, body copy |
| `--color-text-tertiary` | `#6E6E6E` | placeholder / muted labels |
| `--color-text-inverse` | `#FFFFFF` | text on inverse block |
| `--color-text-link` | `#3860BE` | links (deep blue) |
| `--color-border-primary` | `#D6D6D6` | default 1px rule (card/interactive edge) |
| `--color-border-secondary` | `#E4E4E4` | lighter section/divider rule |
| `--color-border-focus` | `#0E7FA8` | focus ring (cyan, darkened for white) |
| `--color-accent-primary` | `#3CFFD0` | jelly-mint — the signature fill |
| `--color-accent-strong` | `#157A52` | dark-mint AS TEXT (accent at readable contrast) |
| `--color-accent-primary-hover` | `#2EE6B9` | mint fill hover |
| `--color-accent-primary-active` | `#20C9A0` | mint fill pressed |
| `--color-accent-on` | `#131313` | text/icon on mint fill (mandatory dark ink) |
| `--color-accent-secondary` | `#5200FF` | ultraviolet — categorical 2nd tone (tags/charts) |
| `--color-accent-wash` | `rgba(60, 255, 208, 0.18)` | mint tint (selected-option fill) |
| `--color-status-success` | `#157A52` | success (dark-mint for AA text use) |
| `--color-status-success-on` | `#FFFFFF` | text on success fill |
| `--color-status-success-wash` | `#E2F7F0` | success tint |
| `--color-status-warning` | `#8A6D00` | warning (dark amber, AA) |
| `--color-status-error` | `#5200FF` | error — ultraviolet repurposed (deliberate Verge move) |
| `--color-status-error-on` | `#FFFFFF` | text on error fill |
| `--color-status-error-wash` | `#ECE5FF` | error tint |
| `--color-status-info` | `#3860BE` | info (matches link blue) |

**WCAG AA verification (on `--color-bg-primary` #FFFFFF unless noted):**
- text-primary `#131313` on `#FFFFFF` → **18.6:1** ✓ (needs 4.5)
- text-secondary `#585858` on `#FFFFFF` → **7.1:1** ✓
- text-tertiary `#6E6E6E` on `#FFFFFF` → **5.1:1** ✓ (placeholder still clears body 4.5)
- link `#3860BE` on `#FFFFFF` → **5.9:1** ✓ (deep blue keeps mint reserved for fills)
- accent-on `#131313` on mint fill `#3CFFD0` → **14.5:1** ✓ — **white on `#3CFFD0` = 1.3:1, FAILS, so dark ink-on is the only correct choice**
- accent-strong (dark-mint) `#157A52` as text on `#FFFFFF` → **5.3:1** ✓ (this is how the accent reads as *text* — pure mint never carries copy)
- focus-ring `#0E7FA8` on `#FFFFFF` → **4.6:1** ✓ (clears 1.4.11 non-text 3:1 with margin; cyan darkened so it survives the white canvas)
- error/ultraviolet `#5200FF` on `#FFFFFF` → **7.5:1** ✓ (UV-as-error is legible; the repurpose is safe)
- warning `#8A6D00` on `#FFFFFF` → **4.9:1** ✓

**Accent discipline:** the pure mint `#3CFFD0` is a **fill only** — progress, selected-option ring/wash, the promo "hazard" pill — always with `#131313` ink-on. Anything that must read *as text* (links, statuses, inline accent) uses the dark-mint `--color-accent-strong #157A52` or the blue link, never the neon. Ultraviolet `#5200FF` is both the categorical 2nd tone and the error color — a deliberate single-hue Verge gesture, not an accident.

## 4. Color — DARK overrides

This is the **native** Verge palette (dark-canvas-first); LIGHT is the derived counterpart. Only changed tokens:

| token | value | note |
|---|---|---|
| `--color-bg-primary` | `#131313` | Canvas Black (not pure #000) |
| `--color-bg-secondary` | `#2D2D2D` | Surface Slate (card lifts off page) |
| `--color-bg-tertiary` | `#313131` | input/code well |
| `--color-bg-inverse` | `#FFFFFF` | inverted = white |
| `--color-surface-overlay` | `rgba(0, 0, 0, 0.7)` | darker scrim |
| `--color-text-primary` | `#FFFFFF` | Hazard White |
| `--color-text-secondary` | `#B0B0B0` | muted |
| `--color-text-tertiary` | `#949494` | placeholder |
| `--color-text-inverse` | `#131313` | text on white inverse |
| `--color-text-link` | `#3CFFD0` | mint becomes the link (reads fine on black) |
| `--color-border-primary` | `rgba(255, 255, 255, 0.18)` | white 18% rule (interactive) |
| `--color-border-secondary` | `rgba(255, 255, 255, 0.10)` | white 10% divider rule |
| `--color-border-focus` | `#1EAEDB` | Focus Cyan (brighter for black) |
| `--color-accent-strong` | `#3CFFD0` | on black, the neon itself is legible as accent text |
| `--color-accent-primary-hover` | `#5EFFD9` | brighter mint hover |
| `--color-accent-secondary` | `#8C6BFF` | ultraviolet lightened for dark |
| `--color-accent-wash` | `rgba(60, 255, 208, 0.16)` | translucent mint tint |
| `--color-status-success` | `#3CFFD0` | mint = positive |
| `--color-status-success-on` | `#06281F` | deep-mint ink on success fill |
| `--color-status-success-wash` | `#103029` | success tint |
| `--color-status-warning` | `#FFD23C` | yellow tile |
| `--color-status-error` | `#8C6BFF` | ultraviolet = alert (repurpose held) |
| `--color-status-error-on` | `#14091F` | near-black-violet ink on error fill |
| `--color-status-error-wash` | `#241738` | error tint |
| `--color-status-info` | `#1EAEDB` | cyan info |

**Dark re-check:** text-primary `#FFFFFF` on `#131313` → **18.6:1** ✓; text-secondary `#B0B0B0` → **8.6:1** ✓ (and **6.4:1** on card `#2D2D2D` ✓); text-tertiary `#949494` → **6.1:1** ✓; link mint `#3CFFD0` on `#131313` → **14.5:1** ✓; accent-strong mint-as-text → **14.5:1** ✓; accent-on `#131313` on mint fill → **14.5:1** ✓; focus-ring `#1EAEDB` → **7.2:1** ✓ (clears 1.4.11 3:1); error UV `#8C6BFF` on `#131313` → **5.0:1** ✓, with error-on `#14091F` on the UV fill → **5.2:1** ✓; success-on `#06281F` on mint fill → **12.3:1** ✓; warning `#FFD23C` → **12.9:1** ✓. Borders are white-rgba rules, never black.

## 5. Typography tokens

```
--font-weight-normal:   400   /* body / reading */
--font-weight-medium:   500   /* UI, nav, emphasis */
--font-weight-semibold: 700   /* sub-heads */
--font-weight-bold:     800   /* SIGNATURE heavy display (Manuka-shout → heaviest Inter) */
```

Four weights. The **800** is the character knob — display headings are the heaviest Inter, not 700, so titles read as solid hazard blocks.

Tracking & line-height (per the loud-display voice):
```
--letter-spacing-tight: -0.02em   /* heavy display compression */
--letter-spacing-wide:   0.14em   /* mono-UPPERCASE «telegraph ticker» labels */
--line-height-tight:   0.95       /* hulking display — lines nearly touch */
--line-height-normal:  1.45       /* body */
--line-height-relaxed: 1.6        /* dense prose */
```

Display headings use weight 800 + `--letter-spacing-tight -0.02em` + `--line-height-tight 0.95`; UPPERCASE Geist Mono labels use the wide `0.14em` ticker tracking (well clear of the 0.06em floor).

## 6. Component tokens

```
--border-radius-sm:   4px;     /* small chips / inline */
--border-radius-md:  20px;     /* SIGNATURE PILL cards/buttons */
--border-radius-lg:  24px;     /* featured panels */
--border-radius-full: 999px;   /* full-pill chips */

--rule-weight:        1px;
--rule-weight-strong: 2px;

/* Zero drop-shadow — depth is 1px borders / saturated color only (signature flatness). */
--shadow-sm: none;
--shadow-md: 0 0 0 1px var(--color-border-primary);   /* RING, not shadow */
--shadow-lg: 0 0 0 1px var(--color-border-primary);
--shadow-focus: 0 0 0 2px var(--color-bg-primary), 0 0 0 4px var(--color-border-focus);
```

**Three structural character knobs (the anti-recolor signature):**
1. **Pill radii** — `--border-radius-md 20px` / `--border-radius-lg 24px`. Buttons, chips, and cards all read as pills; this large-radius geometry, not just the mint, is what makes the system unmistakable in grayscale.
2. **Heavy 800 display weight** — `--font-weight-bold 800`. Headlines are solid ink blocks (see §5).
3. **Zero-shadow flat 1px-border elevation** — `--shadow-sm: none`; `--shadow-md`/`-lg` are `0 0 0 1px` rings. The print-poster feel comes from saturated color and hairline rules, never a soft drop-shadow. Strip any one of these three and it becomes a recolor.

**focus-ring** is a 2px bg-color gap + 2px cyan ring (double box-shadow) so the focus floats clear of the pill edge without a diffuse halo — keyboard focus stays unmistakable on both canvases.

## 7. Motion

```
--easing-default: cubic-bezier(0.4, 0, 0.2, 1);
--duration-normal: 160ms;   /* snappy «console-meets-club» */
```

Motion is **snappy**, not soft: `--duration-normal 160ms` is faster than the conventional 250ms — interactions feel like a console toggle, matching the loud, confident voice. The default ease stays the standard `cubic-bezier(.4,0,.2,1)`; no bounce (it would soften the print-poster edge). All transitions collapse to near-0 under `prefers-reduced-motion`.

## 8. Anti-slop & component cues

**Anti-slop confirmations:**
- **No drop-shadow elevation** — `--shadow-sm: none`; cards/popovers separate by a flat `0 0 0 1px` ring on `--color-border-primary`. The flatness is deliberate, not unfinished.
- **No mint-as-text** — pure `#3CFFD0` is a fill only (always `#131313` ink-on); text-accent is the dark-mint `#157A52` (light) / the neon itself on black (dark). White on mint is banned (1.3:1).
- **No Tailwind-indigo** — the categorical/error tone is the brand's `--color-accent-secondary #5200FF` ultraviolet, not `#6366f1`.
- **No emoji-as-icons** — monochrome Lucide SVG sprite only.
- **No soft "trust" gradient** — separation is saturated flat color + 1px rules; any background flourish is a single brand-tinted wash, not a purple→blue card gradient.

**Component cues:**
- **Buttons:** pill (`--border-radius-md 20px`, or full-pill for chips). Primary = solid mint `#3CFFD0` fill, `#131313` ink-on label, Inter 800; hover → `#2EE6B9`, active → `#20C9A0`. Secondary = transparent pill, `#131313` label, 1px `#D6D6D6` rule. No shadow on either.
- **Cards:** `#F4F4F4` surface (light) / `#2D2D2D` (dark), 20px radius (24px featured), 1px ring rule, NO drop-shadow. Heading inside uses weight 800.
- **Inputs:** pill to match buttons, `#E9E9E9` well (light), 1px `#D6D6D6` rule, placeholder `#6E6E6E`; focus → 2px cyan ring via `--shadow-focus`. Error rule = `--color-status-error` (ultraviolet) with role=alert message.
- **MCQ-option rows:** pill/20px rows, 1px rule, resting = rule only; hover = stronger rule; **selected = mint `--color-accent-wash` fill + 2px mint edge**; correct/incorrect after submit use `--color-status-success` (dark-mint / mint) and `--color-status-error` (ultraviolet) rings + washes, keeping the pure neon for "active," not "graded."
- **Chips:** full-pill (`999px`), Geist Mono UPPERCASE, `--letter-spacing-wide 0.14em` ticker tracking; default = tertiary fill + 1px rule + `#6E6E6E` text; brand chip = mint wash + dark-mint `#157A52` text. The UPPERCASE mono chip is the system's "telegraph ticker" voice.
