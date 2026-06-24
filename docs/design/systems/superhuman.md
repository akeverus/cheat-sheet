# superhuman

## 1. Character

Premium tool, dialed quiet: a warm-neutral paper canvas (white → cream → parchment) under near-black charcoal ink, so the surface feels like fine stationery rather than a generic app shell. The accent is **amethyst/lavender** — a deep amethyst `#714CB6` carries links, focus, and accent-as-text, while a soft high-luminance lavender `#CBB7FB` fills the single primary CTA with dark ink-on-label. **The one bold move: the signature dark theme is not charcoal but a deep "mysteria" indigo-black `#1B1938`** — a striking purple-tinted canvas that brands the dark mode the way the lavender brands the light. Restraint is the whole point: soft surfaces, ring-first elevation, the amethyst used **≤2 times per screen** (primary CTA + focus / active state). Type carries weight with a slightly-heavier-than-normal UI pairing (460/540) and a tight `0.98` display line-height that packs headings into dense, confident blocks.

## 2. Fonts

```
--font-family-display: 'Inter', system-ui, -apple-system, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
--font-family-body:    'Inter', system-ui, -apple-system, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
--font-family-mono:    'JetBrains Mono', 'Geist Mono', ui-monospace, 'SF Mono', Menlo, monospace;
```

Single Inter family for display+body (Superhuman's Super Sans VF substitutes cleanly to Inter, already variable-loaded). JetBrains Mono is reserved exclusively for code blocks, inline `code`, and kbd — never mixed into prose. The character lives in weight, not in a second face.

## 3. Color — LIGHT

| token | value | role |
|---|---|---|
| `--color-bg-primary` | `#FFFFFF` | page background (pure white paper) |
| `--color-bg-secondary` | `#F4F1EB` | warm cream surface (cards/panels) |
| `--color-bg-tertiary` | `#E9E5DD` | parchment — inputs / button wells |
| `--color-bg-inverse` | `#1B1938` | inverted block (mysteria indigo) |
| `--color-surface-overlay` | `rgba(27, 25, 56, 0.45)` | modal/dropdown scrim (indigo-tinted) |
| `--color-text-primary` | `#292827` | charcoal ink (headings, body) |
| `--color-text-secondary` | `#5C5A54` | descriptions, body copy |
| `--color-text-tertiary` | `#6E6B64` | placeholder / muted labels |
| `--color-text-inverse` | `#FFFFFF` | text on inverse block |
| `--color-text-link` | `#714CB6` | links (deep amethyst, accent-as-text) |
| `--color-border-primary` | `#DCD7D3` | parchment border (interactive/card edge) |
| `--color-border-secondary` | `#E9E5DD` | softer section/divider rule |
| `--color-border-focus` | `#714CB6` | focus ring color (amethyst) |
| `--color-accent-primary` | `#CBB7FB` | the single accent fill — lavender glow (primary CTA) |
| `--color-accent-strong` | `#714CB6` | amethyst as TEXT (links/active/accent text) |
| `--color-accent-primary-hover` | `#BBA3F2` | lavender hover |
| `--color-accent-primary-active` | `#5B3D94` | pressed (deep amethyst) |
| `--color-accent-on` | `#292827` | ink on lavender fill (lavender is high-luminance) |
| `--color-accent-secondary` | `#8B79C4` | muted lavender — tags only, not 2nd brand |
| `--color-accent-wash` | `rgba(203, 183, 251, 0.22)` | lavender tint (selected/active fill) |
| `--color-status-success` | `#1E7A4D` | success (darkened for AA text) |
| `--color-status-success-on` | `#FFFFFF` | text on success fill |
| `--color-status-success-wash` | `#E4F0EA` | success tint |
| `--color-status-warning` | `#8A5A00` | warning (deep amber, AA) |
| `--color-status-error` | `#B23636` | error (darkened for AA) |
| `--color-status-error-on` | `#FFFFFF` | text on error fill |
| `--color-status-error-wash` | `#F4E3E3` | error tint |
| `--color-status-info` | `#714CB6` | info (reuses amethyst) |

**WCAG AA verification (on `--color-bg-primary` #FFFFFF unless noted):**
- text-primary `#292827` on `#FFFFFF` → **14.7:1** ✓ (needs 4.5)
- text-secondary `#5C5A54` on `#FFFFFF` → **6.9:1** ✓
- text-tertiary `#6E6B64` on `#FFFFFF` → **5.3:1** ✓ (placeholder still clears body 4.5)
- link / accent-strong `#714CB6` on `#FFFFFF` → **6.2:1** ✓ — passes body 4.5:1 as plain text, so the amethyst link needs no underline crutch (still underlined for affordance, but color alone is AA-safe)
- accent-on `#292827` on accent fill `#CBB7FB` → **8.2:1** ✓ (white on `#CBB7FB` = 1.8:1, would fail — dark ink-on-label is correct because lavender is high-luminance)
- focus ring `#714CB6` on `#FFFFFF` → **6.2:1** ✓ (WCAG 1.4.11 non-text needs 3:1)

**Accent discipline (≤2 per screen):** the lavender/amethyst appears in exactly two slots — (1) the active/primary interactive signal (primary CTA fill `#CBB7FB`, selected MCQ-option ring + `accent-wash` tint) and (2) the focus ring `#714CB6`. Links use `--color-text-link #714CB6` which reads as ink-amethyst at AA contrast, so it carries the "interactive" meaning without consuming a third bright-fill slot.

## 4. Color — DARK overrides

Only changed tokens (the mysteria canvas):

| token | value | note |
|---|---|---|
| `--color-bg-primary` | `#1B1938` | Mysteria Purple (signature indigo-black, not charcoal) |
| `--color-bg-secondary` | `#232046` | card sits slightly above page |
| `--color-bg-tertiary` | `#2C2954` | input/well, one step lighter |
| `--color-bg-inverse` | `#FFFFFF` | inverted = white paper |
| `--color-surface-overlay` | `rgba(0, 0, 0, 0.6)` | darker scrim |
| `--color-text-primary` | `#F4F2FF` | near-white, faint violet cast |
| `--color-text-secondary` | `#C9C4E6` | muted lavender-grey |
| `--color-text-tertiary` | `#9F9AC4` | placeholder |
| `--color-text-inverse` | `#1B1938` | text on white inverse |
| `--color-text-link` | `#CBB7FB` | lavender reads bright on indigo |
| `--color-border-primary` | `rgba(255, 255, 255, 0.16)` | white hairline (interactive) |
| `--color-border-secondary` | `rgba(255, 255, 255, 0.09)` | white 9% section rule |
| `--color-border-focus` | `#CBB7FB` | lavender focus ring |
| `--color-accent-primary` | `#CBB7FB` | lavender glow (unchanged fill) |
| `--color-accent-strong` | `#CBB7FB` | lavender passes as text on dark |
| `--color-accent-primary-hover` | `#DAC9FF` | brighter lavender hover |
| `--color-accent-primary-active` | `#B7A0F0` | pressed |
| `--color-accent-on` | `#1B1938` | indigo ink on lavender fill |
| `--color-accent-secondary` | `#A99BD8` | muted lavender tag |
| `--color-accent-wash` | `rgba(203, 183, 251, 0.18)` | translucent tint |
| `--color-status-success` | `#5FC78C` | brighter green |
| `--color-status-success-on` | `#10241A` | dark text on green fill |
| `--color-status-success-wash` | `#1E2C2A` | tint |
| `--color-status-warning` | `#E0B257` | brighter amber |
| `--color-status-error` | `#F08A84` | brighter red |
| `--color-status-error-on` | `#2A1414` | dark text on red fill |
| `--color-status-error-wash` | `#33222E` | tint |
| `--color-status-info` | `#CBB7FB` | reuses lavender |

**Dark accent re-check (on `--color-bg-primary` #1B1938):** text-primary `#F4F2FF` → **15.3:1** ✓; text-secondary `#C9C4E6` → **10.1:1** ✓; text-tertiary `#9F9AC4` → **6.4:1** ✓; link / accent-strong `#CBB7FB` → **9.4:1** ✓ (clears body 4.5 easily); accent-on `#1B1938` on lavender fill `#CBB7FB` → **9.4:1** ✓; focus ring `#CBB7FB` on `#1B1938` → **9.4:1** ✓ (1.4.11 needs 3:1). Borders use white-rgba hairlines per craft rule (never black on the indigo canvas).

## 5. Typography tokens

```
--font-weight-normal:   400   /* reading / passive text */
--font-weight-medium:   460   /* SIGNATURE workhorse UI weight — between regular and medium */
--font-weight-semibold: 540   /* SIGNATURE display weight */
--font-weight-bold:     600   /* heaviest in the system — no 700 */
```

The 460/540 pairing is the identity: UI text sits at 460 (slightly heavier than a normal 400 to feel "tooled"), and display/headings at 540, with 600 as the ceiling. No 700 bold.

Tracking:
```
--letter-spacing-tight: -0.03em   /* display compression — headings pull tight */
--letter-spacing-wide:   0.08em   /* ALL-CAPS labels / chips */
```

Line-height:
```
--line-height-tight:   0.98   /* SIGNATURE: dense "power-block" headings */
--line-height-normal:  1.5    /* body */
--line-height-relaxed: 1.7    /* long-form prose */
```

The `0.98` display line-height is the structural signature — headings stack into tight, confident blocks rather than airy ones, reinforcing the "premium tool" density.

## 6. Component tokens

```
--border-radius-sm:   8px;     /* chips, small controls */
--border-radius-md:  12px;     /* buttons, inputs */
--border-radius-lg:  16px;     /* cards / panels */
--border-radius-full: 999px;   /* pills, avatars */
--rule-weight:        1px;
--rule-weight-strong: 2px;     /* selected / active rings */

--shadow-sm: none;                                                        /* flat by default */
--shadow-md: 0 0 0 1px var(--color-border-primary);                       /* RING, not drop-shadow */
--shadow-lg: 0 0 0 1px var(--color-border-primary), 0 8px 30px rgba(27, 25, 56, 0.10);   /* ring + soft indigo lift */
--shadow-focus: 0 0 0 3px rgba(113, 76, 182, 0.35);                       /* amethyst focus glow */
```

**Medium radii (8/12/16), not pills.** Unlike a full-pill system, Superhuman uses soft-but-rectangular radii: 8 for chips, 12 for buttons/inputs, 16 for cards. The `999px` full pill is reserved for true pill shapes (status chips, avatars), not the primary controls.

**`--shadow-md` is a 1px RING, not a drop-shadow — justified:** the premium-restraint character means separation comes from a hairline border + the warm surface step (white → cream → parchment), not from heavy elevation. Cards float on a ring; only `--shadow-lg` (popover/dropdown) layers a soft, indigo-tinted `0 8px 30px rgba(27,25,56,0.10)` lift, and even that keeps the warm-cool tint of the canvas rather than a neutral grey shadow.

**`--shadow-focus`** is a 3px `rgba(113,76,182,0.35)` amethyst glow — the keyboard-focus signal, one of the two allowed accent uses. In dark mode it becomes `rgba(203,183,251,0.40)` so the lavender ring reads on the indigo canvas. The dark `--shadow-lg` deepens to `0 10px 34px rgba(0,0,0,0.55)` for legibility against the dark surface.

## 7. Motion

Superhuman's voice lives in surface warmth, weight, and the amethyst's scarcity — **not** in animation. Keep all structural motion defaults from the base system: short, calm transitions on hover/focus/border, no bounce (bounce contradicts the premium-tool composure). All transitions collapse to near-0 under `prefers-reduced-motion`. The one design intent: focus and active-state changes should feel instantaneous and crisp, matching the keyboard-driven, "fast and quiet" ethos of the source product — favor the fast tier for ring/border transitions and avoid any decorative loops.

## 8. Anti-slop & component cues

**Anti-slop confirmations:**
- No Tailwind-indigo hex — the accent is the brand amethyst `#714CB6` / lavender `#CBB7FB`, not `#6366f1`; the dark canvas `#1B1938` is a deliberate mysteria indigo, not a recolored Tailwind slate.
- No generic two-stop "trust" gradient — depth is the warm paper step (white → `#F4F1EB` → `#E9E5DD`) plus a hairline ring, never a purple→blue card wash.
- No emoji-as-icons — Lucide SVG sprite only.
- No serif-on-display violation — Inter is intentionally the display face; there is no serif spec to break.
- **No rounded-card-with-colored-left-border** — cards are warm-cream surface + hairline ring with NO colored left rail. Category/status is signaled by a small chip or icon inside the card, never an amethyst left border.

**Component cues:**
- **Buttons:** 12px radius. Primary = solid lavender `#CBB7FB` fill, charcoal `#292827` label (`accent-on`), Inter 460 — the single bright accent fill per screen; hover → `#BBA3F2`, active → `#5B3D94`. Secondary = parchment `#E9E5DD` well, `#292827` label, 1px `#DCD7D3` border.
- **Cards:** warm-cream `#F4F1EB` surface, 16px radius, 1px `#DCD7D3` ring (`--shadow-md`), generous padding, no fill tint beyond the warm step; hover keeps the ring (no lift). Featured panels may layer `--shadow-lg` for a soft indigo-tinted float.
- **Inputs:** 12px radius, parchment `#E9E5DD` fill, 1px `#DCD7D3` border, placeholder `#6E6B64`; focus → amethyst border `#714CB6` + `--shadow-focus` 3px glow. On-blur validation; error border = `--color-status-error #B23636` with role=alert message.
- **MCQ-option rows:** 12px-radius rows on the cream surface, 1px hairline, resting = hairline only; hover = border darkens to amethyst-adjacent; **selected = 2px amethyst `#714CB6` ring + `--color-accent-wash` lavender tint** (one of the two allowed accent uses); correct/incorrect after submit use `--color-status-success`/`-error` ring + wash, never the lavender accent (keeps the amethyst meaning "interactive," not "graded").
- **Chips:** 8px or full-pill, ALL-CAPS with `--letter-spacing-wide` 0.08em; default = parchment fill + hairline + `#6E6B64` text; brand chip = `--color-accent-wash` lavender fill + `#714CB6` amethyst text (the documented accent badge).
