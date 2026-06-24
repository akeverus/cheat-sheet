# stripe

## 1. Character

Engineered clarity, the way a payments developer platform earns trust: a clean white canvas where cards are lifted off the page by a faint **blue-tinted float**, never by a gray box. Cool, blue-leaning neutrals do the structural work — pure-white page, a very-light blue-tinted card (`#F6F9FC`), and a pale-blue well (`#EBF1F7`) — while the ink is the famous Stripe **deep navy** (`#0A2540`), which reads precise and confident rather than black-harsh. The accent is Stripe's indigo-violet (`#533AFD`), used as a solid primary fill with a crisp white label, and a darker `#4434D4` for accent-as-text so links and inline accents clear body contrast. **The one signature move: every elevation is a two-layer navy-tinted shadow (`rgba(50,50,93,*)` over `rgba(0,0,0,*)`) — the recognizable "Stripe card float."** Where mintlify is deliberately shadow-less and ring-only, stripe is shadow-*first*: depth is the brand. Tight radii (4/8/12px) keep every corner looking machined and exact.

## 2. Fonts

```
--font-family-display: 'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
--font-family-body:    'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
--font-family-mono:    'JetBrains Mono', 'Geist Mono', ui-monospace, 'SF Mono', Menlo, monospace;
```

Inter (with the system stack behind it — `SF Pro Display`/`SF Pro Text` map to Stripe's house Söhne) carries both display and body: one crisp geometric grotesque, no serif. Mono is JetBrains Mono (Stripe's SourceCodePro analogue), reserved for code blocks, inline `code`, and technical labels — never mixed into prose.

## 3. Color — LIGHT

| token | value | role |
|---|---|---|
| `--color-bg-primary` | `#FFFFFF` | page background (pure white canvas) |
| `--color-bg-secondary` | `#F6F9FC` | cards/panels (very light blue-tinted) |
| `--color-bg-tertiary` | `#EBF1F7` | inputs / wells / code-bg (pale-blue) |
| `--color-bg-inverse` | `#0A2540` | inverted block (deep navy) |
| `--color-surface-overlay` | `rgba(10, 37, 64, 0.5)` | modal/dropdown scrim (navy-tinted) |
| `--color-text-primary` | `#0A2540` | headings, body (Stripe deep navy ink) |
| `--color-text-secondary` | `#475569` | descriptions, body copy (cool slate) |
| `--color-text-tertiary` | `#5E708A` | placeholder / muted labels |
| `--color-text-inverse` | `#FFFFFF` | text on inverse navy block |
| `--color-text-link` | `#4434D4` | links (darkened indigo — AA as text) |
| `--color-border-primary` | `#E5EDF5` | default hairline (card/interactive edge) |
| `--color-border-secondary` | `#EDF2F7` | softer divider hairline |
| `--color-border-focus` | `#533AFD` | focus ring color (brand indigo) |
| `--color-accent-primary` | `#533AFD` | the single accent — primary fill / progress / active |
| `--color-accent-strong` | `#4434D4` | accent-as-text (darker, AA-safe) |
| `--color-accent-primary-hover` | `#4434D4` | hover fill |
| `--color-accent-primary-active` | `#2E2B8C` | pressed fill (deeper) |
| `--color-accent-on` | `#FFFFFF` | white label on indigo fill |
| `--color-accent-secondary` | `#7A7FAD` | categorical 2nd tone (tags/charts only) |
| `--color-accent-wash` | `rgba(83, 58, 253, 0.08)` | selected/active tint fill |
| `--color-status-success` | `#0F7A38` | success (darkened green for AA text) |
| `--color-status-success-on` | `#FFFFFF` | text on success fill |
| `--color-status-success-wash` | `#E3F4E9` | success tint |
| `--color-status-warning` | `#9B6829` | warning (lemon, darkened for AA) |
| `--color-status-error` | `#C8123C` | error (Stripe ruby, darkened for AA) |
| `--color-status-error-on` | `#FFFFFF` | text on error fill |
| `--color-status-error-wash` | `#FBE3E9` | error tint |
| `--color-status-info` | `#4434D4` | info (shares the indigo accent-as-text) |

**WCAG AA verification (on `--color-bg-primary` #FFFFFF unless noted):**
- text-primary navy `#0A2540` on `#FFFFFF` → **15.5:1** ✓ (needs 4.5; very high contrast — navy ink reads ink-deep, never gray)
- text-primary `#0A2540` on card `#F6F9FC` → **14.7:1** ✓ (the blue tint barely costs contrast)
- text-secondary `#475569` on `#FFFFFF` → **7.6:1** ✓ (cool slate, comfortable in dense Q&A prose)
- text-tertiary `#5E708A` on `#FFFFFF` → **5.1:1** ✓ (placeholder still clears body 4.5)
- link `#4434D4` on `#FFFFFF` → **7.8:1** ✓ (the darker indigo clears 4.5:1 as plain inline text — links need no underline crutch for contrast, though convention keeps it)
- accent-on white `#FFFFFF` on accent fill `#533AFD` → **6.2:1** ✓ (clears body 4.5; white label on the brand indigo is correct — dark text on `#533AFD` would be harder to read)
- focus-ring `#533AFD` on `#FFFFFF` → **6.2:1** ✓ (clears WCAG 1.4.11 ≥3:1 for non-text UI)
- error `#C8123C` on `#FFFFFF` → **5.8:1** ✓ (Stripe crimson darkened for AA text)
- success `#0F7A38` → **5.4:1** ✓ · warning `#9B6829` → **4.8:1** ✓ (all status colors pass body 4.5)

**Accent discipline:** the indigo accent appears as the single interactive signal — primary CTA fill (`#533AFD`, white label), selected MCQ ring/wash, progress fill, and the focus ring. Accent-as-text always uses the darker `#4434D4` (`--color-accent-strong`/`--color-text-link`) so it reads as deep ink-indigo at AA, not as the bright fill — links and info copy don't consume the "fill" accent slot. `--color-accent-secondary #7A7FAD` is a muted categorical tone (tags/charts), never a second brand color.

## 4. Color — DARK overrides

Only changed tokens (deep-navy canvas — the page *becomes* the brand navy):

| token | value | note |
|---|---|---|
| `--color-bg-primary` | `#0A2540` | deep navy canvas (Stripe ink → page) |
| `--color-bg-secondary` | `#0E2D4D` | card sits slightly above page |
| `--color-bg-tertiary` | `#143656` | input/code well |
| `--color-bg-inverse` | `#FFFFFF` | inverted = white |
| `--color-surface-overlay` | `rgba(0, 0, 0, 0.6)` | darker scrim |
| `--color-text-primary` | `#F6F9FC` | near-white (the light card tint, reused as ink) |
| `--color-text-secondary` | `#B6C2D2` | muted cool slate |
| `--color-text-tertiary` | `#8B9BB0` | placeholder |
| `--color-text-inverse` | `#0A2540` | navy text on white inverse |
| `--color-text-link` | `#A5B4FC` | light-indigo (bright accent-as-text on dark) |
| `--color-border-primary` | `rgba(255, 255, 255, 0.14)` | white hairline (interactive) |
| `--color-border-secondary` | `rgba(255, 255, 255, 0.08)` | softer white hairline |
| `--color-border-focus` | `#8B93FF` | brighter indigo ring |
| `--color-accent-primary` | `#635BFF` | brand brightened for the dark fill |
| `--color-accent-strong` | `#A5B4FC` | light-indigo accent-as-text |
| `--color-accent-primary-hover` | `#8B93FF` | hover fill |
| `--color-accent-primary-active` | `#635BFF` | pressed fill |
| `--color-accent-on` | `#FFFFFF` | white label on indigo fill |
| `--color-accent-secondary` | `#9CA3D8` | categorical 2nd tone |
| `--color-accent-wash` | `rgba(99, 91, 255, 0.20)` | translucent selected tint |
| `--color-status-success` | `#4ADE80` | brighter green for dark legibility |
| `--color-status-success-on` | `#06281A` | dark text on bright green fill |
| `--color-status-success-wash` | `#16301F` | success tint |
| `--color-status-warning` | `#E0B257` | brighter amber |
| `--color-status-error` | `#FB7185` | brighter rose-crimson |
| `--color-status-error-on` | `#2A1014` | dark text on error fill |
| `--color-status-error-wash` | `#3A1F25` | error tint |
| `--color-status-info` | `#A5B4FC` | shares light-indigo |

**Dark accent re-check:** text-primary `#F6F9FC` on `#0A2540` → **14.7:1** ✓; text-secondary `#B6C2D2` → **8.6:1** ✓; text-tertiary `#8B9BB0` → **5.5:1** ✓; link `#A5B4FC` → **7.8:1** ✓; accent-on white on `#635BFF` fill → **4.7:1** ✓ (clears body 4.5 — the brand is brightened from `#533AFD` so white stays readable on the dark canvas); focus-ring `#8B93FF` on `#0A2540` → **5.7:1** ✓ (clears WCAG 1.4.11 ≥3:1); error `#FB7185` → **5.8:1** ✓; success `#4ADE80` → **8.9:1** ✓. Borders are white-rgba hairlines per dark-craft rule (not black).

## 5. Typography tokens

```
--font-weight-normal:   400   /* body / reading */
--font-weight-medium:   500   /* UI, nav, emphasis */
--font-weight-semibold: 600   /* headings / titles */
--font-weight-bold:     700   /* heavy display / numerics */
```

Four-step weight ladder (Stripe leans on 500/600 for its precise UI voice; 700 is held for heavy display numbers).

Letter-spacing:
```
--letter-spacing-tight: -0.025em   /* «engineered», dense display blocks */
--letter-spacing-wide:   0.06em    /* UI labels, mono-CAPS */
```
Tight `-0.025em` is the engineered-display knob — it pulls headline glyphs together for the compact, machined feel; `0.06em` opens up uppercase labels.

Line-height: `--line-height-tight` 1.05 (display) · `-normal` 1.5 (body) · `-relaxed` 1.6 (long prose).

## 6. Component tokens

```
--border-radius-sm:   4px;     /* Stripe standard — tight, machined */
--border-radius-md:   8px;
--border-radius-lg:  12px;     /* card radius */
--border-radius-full: 999px;   /* pills / avatars only */
--rule-weight:        1px;
--rule-weight-strong: 2px;

/* SIGNATURE: blue-tinted, two-layer elevation shadows */
--shadow-sm: 0 1px 2px rgba(50, 50, 93, 0.08);
--shadow-md: 0 3px 6px rgba(50, 50, 93, 0.10), 0 1px 3px rgba(0, 0, 0, 0.06);
--shadow-lg: 0 15px 35px rgba(50, 50, 93, 0.15), 0 5px 15px rgba(0, 0, 0, 0.07);
--shadow-focus: 0 0 0 3px rgba(83, 58, 253, 0.35);
```

**The blue-tinted shadow is stripe's signature character knob.** Elevation is built from a navy-tinted layer (`rgba(50,50,93,*)`) softly floating the card, plus a second neutral `rgba(0,0,0,*)` layer underneath for a crisp contact edge — this is the recognizable "Stripe card float." It is the deliberate opposite of mintlify's ring-only, shadow-less paper: where mintlify separates cards with a 5% hairline and *no* drop-shadow, stripe separates them with a colored shadow and *no* ring. `--shadow-md` is the resting card; `--shadow-lg` is for popovers/modals. In dark mode the navy tint would vanish against the navy canvas, so the shadows switch to neutral black floats with a thin white inset hairline (`--shadow-md: 0 0 0 1px rgba(255,255,255,0.10)`) to re-establish the card edge against the dark page.

**Radii** stay tight (4/8/12px) for the engineered, precise feel — `999px` is reserved for pills and avatars, not general cards. Cards use `--border-radius-lg` 12px.

**Focus** is a 3px indigo glow (`--shadow-focus`, `rgba(83,58,253,0.35)` light / `rgba(139,147,255,0.45)` dark) — a soft brand-indigo halo rather than a hard ring, matching Stripe's input-focus signature.

## 7. Motion

Stripe's voice is crispness and precision, not playful bounce — motion stays brisk and structural. Use the system defaults (`--duration-fast` 150ms for hover/border, `--duration-normal` 250ms for surface transitions) and the standard `cubic-bezier(.4,0,.2,1)` ease; avoid the `-bounce` curve, which contradicts the engineered, trustworthy register. Hover lifts a card by deepening `--shadow-md` → `--shadow-lg` (a literal "float up"), the clearest expression of the shadow-first character. All transitions collapse to near-0 under `prefers-reduced-motion`.

## 8. Anti-slop & component cues

**Anti-slop confirmations:**
- No Tailwind-indigo default — the accent is Stripe's documented `#533AFD` / `#635BFF`, not `#6366f1`; `--color-accent-secondary` is a muted `#7A7FAD`, a categorical tone, not a second brand.
- No generic gray box for cards — separation is the navy-tinted *shadow*, the brand's signature, plus the faint blue card tint (`#F6F9FC`), never a flat gray panel.
- No emoji-as-icons — Lucide SVG sprite only.
- No serif-on-display — Inter is intentionally the display face; there is no serif spec to break.
- **No rounded-card-with-colored-left-border** — cards are blue-tinted surface + shadow with NO colored left rail. Category/status is signaled by a chip or icon inside the card, never a colored left border.

**Component cues:**
- **Buttons:** tight radius (8px). Primary = solid indigo `#533AFD` fill, white label (6.2:1), Inter 500, resting `--shadow-sm`; hover → `#4434D4` + `--shadow-md`; active → `#2E2B8C`. Secondary = white surface, `#0A2540` label, 1px `#E5EDF5` border. Focus adds the 3px indigo glow.
- **Cards:** blue-tinted `#F6F9FC` surface, 12px radius, `--shadow-md` resting (the navy-tinted float), no ring; hover lifts to `--shadow-lg`. The shadow *is* the boundary — this is the character that most distinguishes stripe from shadow-less designs.
- **Inputs:** 8px radius, `#EBF1F7` (or white) fill, 1px `#E5EDF5` border, placeholder `#5E708A`; focus → 3px indigo glow + `#533AFD` border. On-blur validation; error border = `#C8123C` with `role=alert` message.
- **MCQ-option rows:** 8px-radius rows, 1px `#E5EDF5` hairline, touch-safe height; resting = hairline + `--shadow-sm`; hover = `--shadow-md`; **selected = `#533AFD` border + `--color-accent-wash` `rgba(83,58,253,0.08)` tint**; correct/incorrect after submit use `--color-status-success` / `-error` border + wash, never the indigo accent (keeps accent meaning "interactive," not "graded").
- **Chips:** pill (999px) or 8px, JetBrains Mono uppercase with `--letter-spacing-wide` 0.06em; default = `#EBF1F7` fill + `#E5EDF5` hairline + `#475569` text; accent chip = `--color-accent-wash` fill + `#4434D4` text (the indigo badge at AA).
