# claude

Token spec for `html[data-design="claude"]`. Brand anchor: Anthropic's warm, literary, human voice — but a *distinct* take from the `editorial` default. Where editorial sits on ivory and paints terracotta on everything, `claude` drops onto a deeper **parchment** body, sets its display in a **serif** (Lora), and breaks the all-terracotta monotony with a **blue keyboard-focus ring**. Same family, different posture: editorial whispers ink-and-clay; claude reads like a printed book page with a single cool accent of attention.

## 1. Character

A warm-parchment canvas (`#F5F4ED`) — one full step deeper than editorial's ivory — under medium-weight serif headings (Lora) and a quiet sans body (Inter). Every screen reads like a well-set book page laid on a desk: depth comes almost entirely from **ring hairlines** (`0 0 0 1px` warm-gray) and a faint surface-tone shift, never from drop shadows, so cards, MCQ rows and code blocks all sit at one physical altitude. Coral/terracotta marks the single primary action and carries near-black ink *on* its fill. **The character knob that separates claude from editorial: the serif-display + blue-focus pairing.** The focus ring is deliberately *not* terracotta — it is a darkened editorial-friendly **blue** (`#2476C4` light / `#5AA9F0` dark), a cool counterpoint that keeps "where is my keyboard?" legible against a warm page and prevents the all-one-hue terracotta wash that editorial leans into. Two designs, two readings: editorial = ivory + clay-everything; claude = parchment + serif voice + cool-blue attention.

## 2. Fonts

| Role | Stack |
|------|-------|
| display | `'Lora', Georgia, 'Times New Roman', serif` |
| body | `'Inter', system-ui, -apple-system, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif` |
| mono | `'JetBrains Mono', ui-monospace, 'SF Mono', Menlo, monospace` |

Lora is the variable serif display used for all H1–H3 and big numbers (streak, "N из M"), set single-weight at 500 — the literary serif voice. Inter carries body, UI labels, nav and MCQ option text. JetBrains Mono is strictly for code (highlight.js) and inline `code`. Never sans on display; never mono on prose. The serif display is the same letterform commitment editorial makes — but here it is paired with the cool focus ring, which is what makes the screen read as a different system rather than a re-skin.

## 3. Color — LIGHT

| Token | Value | Role |
|-------|-------|------|
| `--color-bg-primary` | `#F5F4ED` | page canvas (parchment — deeper than editorial ivory) |
| `--color-bg-secondary` | `#FAF9F5` | cards / panels (ivory, one step lighter than the body) |
| `--color-bg-tertiary` | `#FFFFFF` | nested surfaces: inputs, code-bg, wells (true white well) |
| `--color-bg-inverse` | `#141413` | inverted block (warm near-black on light) |
| `--color-surface-overlay` | `rgba(20, 20, 19, 0.45)` | modal/dropdown scrim |
| `--color-text-primary` | `#141413` | primary text (warm near-black ink) |
| `--color-text-secondary` | `#5E5D59` | secondary body / muted |
| `--color-text-tertiary` | `#76756E` | placeholder / metadata / disabled-label |
| `--color-text-inverse` | `#FAF9F5` | text on inverse block + on accent fill |
| `--color-text-link` | `#3D3D3A` | links (warm near-ink, not terracotta — quiet on parchment) |
| `--color-border-primary` | `#E8E6DC` | default hairline (ring elevation) |
| `--color-border-secondary` | `#F0EEE6` | weaker divider |
| `--color-border-focus` | `#2476C4` | **focus-ring color — BLUE, the differentiator** |
| `--color-accent-primary` | `#D97757` | coral/terracotta fill (primary CTA, progress, active) |
| `--color-accent-strong` | `#C16040` | darkened coral for accent-as-text (large only) |
| `--color-accent-primary-hover` | `#C96442` | CTA hover |
| `--color-accent-primary-active` | `#A84E30` | CTA pressed |
| `--color-accent-on` | `#141413` | near-black ink on coral fill |
| `--color-accent-secondary` | `#788C5D` | olive — categorical 2nd tone (tags/charts ONLY) |
| `--color-accent-wash` | `#F0E7DD` | coral-tint wash (pre-answer selection) |
| `--color-status-success` | `#2E6B45` | |
| `--color-status-success-on` | `#FFFFFF` | text on success fill |
| `--color-status-success-wash` | `#E5EEE3` | success tint |
| `--color-status-warning` | `#7A5400` | |
| `--color-status-error` | `#B53333` | warm crimson (not generic red) |
| `--color-status-error-on` | `#FFFFFF` | text on error fill |
| `--color-status-error-wash` | `#F4E0DA` | error tint |
| `--color-status-info` | `#2476C4` | info = same blue as focus (cool accent unified) |

**WCAG AA verification (on `--color-bg-primary` `#F5F4ED` unless noted):**
- text-primary `#141413` on `#F5F4ED` → **16.7:1** ✓ (body 4.5:1).
- text-secondary `#5E5D59` on `#F5F4ED` → **6.0:1** ✓ (body).
- text-tertiary `#76756E` on `#F5F4ED` → **4.2:1** ✓ as large/metadata (AA-large 3:1); placeholder/secondary-meta sit at ≥14px-bold or non-essential, so 3:1 governs.
- link `#3D3D3A` on `#F5F4ED` → **9.9:1** ✓ (body). Links stay warm near-ink, distinguished by underline; they deliberately do **not** consume an accent slot.
- accent-on `#141413` on accent fill `#D97757` → **5.9:1** ✓ (clears body 4.5:1, so even small ink-on labels pass; confirms `#141413` on `#D97757` ≥ 5.4:1 target).
- accent-strong `#C16040` as text on `#F5F4ED` → **3.8:1** — FAILS body 4.5:1, passes large 3:1. So `accent-strong` is for ≥18.66px / ≥14px-bold accent text only; coral is never small body text.
- border-focus `#2476C4` on `#F5F4ED` → **4.3:1** ✓ — clears the 1.4.11 non-text 3:1 gate against the parchment, so the blue ring is unmistakable.
- status-error `#B53333` on `#F5F4ED` → **5.5:1** ✓ (body).

**Accent discipline (≤2 per screen):** (1) the single primary CTA / progress fill in coral; (2) the keyboard focus ring in **blue**. Because the two accent moments are *different hues* (warm fill + cool ring), claude never reads as monochrome the way an all-terracotta screen can — and the olive `--color-accent-secondary` is categorical only (tag text / charts), never a CTA fill.

## 4. Color — DARK overrides

Only tokens that change (warm near-black base `#141413`, never pure black; foreground warm off-white, never pure white; hairlines become warm dark-gray; the blue focus ring brightens to `#5AA9F0`).

| Token | Value |
|-------|-------|
| `--color-bg-primary` | `#141413` |
| `--color-bg-secondary` | `#1F1E1C` |
| `--color-bg-tertiary` | `#30302E` |
| `--color-bg-inverse` | `#FAF9F5` |
| `--color-surface-overlay` | `rgba(0, 0, 0, 0.6)` |
| `--color-text-primary` | `#E8E6DC` |
| `--color-text-secondary` | `#B0AEA5` |
| `--color-text-tertiary` | `#908E85` |
| `--color-text-inverse` | `#141413` |
| `--color-text-link` | `#E8E6DC` |
| `--color-border-primary` | `#34332F` |
| `--color-border-secondary` | `#262523` |
| `--color-border-focus` | `#5AA9F0` |
| `--color-accent-primary` | `#D97757` |
| `--color-accent-strong` | `#E08B6D` |
| `--color-accent-primary-hover` | `#E08B6D` |
| `--color-accent-primary-active` | `#C96442` |
| `--color-accent-on` | `#141413` |
| `--color-accent-secondary` | `#9DB180` |
| `--color-accent-wash` | `#2E2018` |
| `--color-status-success` | `#6FB585` |
| `--color-status-success-on` | `#11241A` |
| `--color-status-success-wash` | `#1C2A20` |
| `--color-status-warning` | `#D9A441` |
| `--color-status-error` | `#EC8580` |
| `--color-status-error-on` | `#2A1414` |
| `--color-status-error-wash` | `#38211F` |
| `--color-status-info` | `#5AA9F0` |

**WCAG AA re-check (on `--color-bg-primary` `#141413`):**
- text-primary `#E8E6DC` on `#141413` → **14.7:1** ✓ (body).
- text-secondary `#B0AEA5` on `#141413` → **8.3:1** ✓ (body).
- text-tertiary `#908E85` on `#141413` → **5.6:1** ✓ (body — comfortably clears even at small size).
- link `#E8E6DC` on `#141413` → **14.7:1** ✓ (links read at primary-ink strength on dark, underline-distinguished).
- accent-strong `#E08B6D` as text on `#141413` → **7.1:1** ✓ — unlike light, coral *may* serve as body-size accent text on dark.
- accent-on `#141413` on accent fill `#D97757` → **5.9:1** ✓ (dark ink on coral reads strongly on both themes).
- border-focus `#5AA9F0` on `#141413` → **7.4:1** ✓ — the blue ring is even more emphatic against near-black (1.4.11 3:1 gate cleared with margin).
- status-error `#EC8580` on `#141413` → **7.2:1** ✓.

## 5. Typography tokens

Four Inter weights are declared (**normal 400 · medium 500 · semibold 600 · bold 700**); Lora display is set single-weight at **500** — the literary serif voice, no bold serif headings. Big stat numbers use Lora 500.

```
--font-weight-normal   400
--font-weight-medium   500   /* serif 500 «single-weight»; also UI emphasis */
--font-weight-semibold 600
--font-weight-bold     700

--letter-spacing-tight -0.018em   /* display / large headings */
--letter-spacing-wide   0.10em    /* ALL-CAPS overlines/labels (≥0.06em gate) */

--line-height-tight    1.10   /* display / H1–H2 */
--line-height-normal   1.5    /* body */
--line-height-relaxed  1.7    /* long Q&A prose blocks */
```

Per craft: serif display gets the negative tracking `-0.018em`; ALL-CAPS labels go to `0.10em` (well past the 0.06em floor). Body line-height holds at 1.5 with long prose blocks relaxing to 1.7 — the book-page cadence, never a 1.3 dashboard. Structural rem size scale (12/14/16/18/20/24/32/40/56) is inherited from the shared base layer.

## 6. Component tokens

```
--border-radius-sm   6px
--border-radius-md   8px      /* buttons, inputs */
--border-radius-lg  16px      /* cards */
--border-radius-full 999px    /* pills/chips */
--rule-weight        1px
--rule-weight-strong 2px

/* THE elevation knob: ring + warm whisper, not a drop shadow */
--shadow-sm    none
--shadow-md    0 0 0 1px var(--color-border-primary)                          /* ring halo — default card */
--shadow-lg    0 0 0 1px var(--color-border-primary), 0 4px 24px rgba(0,0,0,0.05)   /* ring + whisper, popovers only */

/* THE differentiator: blue focus ring (light), brightened (dark) */
--shadow-focus 0 0 0 3px rgba(36, 118, 196, 0.35)
               /* dark: 0 0 0 3px rgba(90, 169, 240, 0.40) */
```

**Elevation decision — RING, justified.** The signature is the `0 0 0 1px` warm halo ("a shadow pretending to be a border"). Like editorial, claude adopts the ring as the *default* card altitude so the whole reading surface stays flat — critical for a reading-heavy app where stacked shadows would fake hierarchy. Drop shadow is allowed ONLY on true overlays via `--shadow-lg`, and even there it's a 0.05-opacity / 24px whisper layered over the ring. Cards, MCQ rows, code blocks, stats panels: ring only.

**Focus decision — BLUE, the character knob.** `--shadow-focus` is a 3px **blue** halo, not terracotta. This is the single most legible difference from editorial: keyboard focus is a cool ring on a warm page, so the accent-of-attention is visually orthogonal to the accent-of-action (coral). It reads clearly on parchment (`#2476C4`, 4.3:1) and even more so on near-black (`#5AA9F0`, 7.4:1), and prevents the all-one-hue terracotta feel.

**Radii** are mid-range: 16px cards / 8px buttons & inputs / 6px small — soft enough for the warm personality, never the toy-like 24–32px marketing roundness in a dense Q&A grid. Spacing/measure are inherited from the shared base (comfortable, not compact: ~24px card padding, generous section rhythm, editorial reading measure).

## 7. Motion

Motion is inherited from the shared base; claude favors the quiet end (Anthropic: "interactions should feel calm"). Standard ease `cubic-bezier(0.2, 0, 0, 1)`, durations roughly `fast 150ms · normal 220ms · slow 360ms`. Bounce is reserved for the session-summary celebration peak only — never on routine state changes. All transitions collapse to near-0 under `prefers-reduced-motion`. The voice lives in the serif type, the warm surface tones and the cool focus ring, not in animation.

## 8. Anti-slop & component cues

**How claude differs from editorial (so they don't read as duplicates):** both are Anthropic-derived (coral `#D97757`, warm ink `#141413`, ring elevation), but claude (1) sits on a deeper **parchment** body `#F5F4ED` rather than editorial's ivory `#FAF9F5`; (2) commits the display to **Lora serif** as a defining surface, not just an option; and (3) — the load-bearing distinction — uses a **blue focus ring** (`#2476C4` / `#5AA9F0`) instead of editorial's terracotta-everything. The serif-display + blue-focus combination is the character signature; remove either and it collapses back toward editorial.

**Confirmed clean:** no indigo/Tailwind hex anywhere (warm accent is brand coral via `--color-accent-primary`; the blue is a darkened focus utility at AA-non-text contrast, the cool counterpoint, not a generic `#6366f1`); no two-stop trust gradient (system is gradient-free — depth from warm surface-tone shifts); no emoji-as-icons in h*/button/li (Lucide SVG sprite; the deliberate 💡/🎲🤔💪 glyphs are the project's intentional exception); **serif-on-display enforced** (Lora 500 for all headings, never Inter); **no rounded-card-with-colored-left-border** — cards are separated by hairline ring + surface tone, and status callouts use a tint background + status-color text + (optional) hairline ring, never a radius+colored-left-stripe tile.

**Component cues:**
- **Buttons:** primary = flat coral fill (`--color-accent-primary`), near-black ink-on label (`--color-accent-on`) at medium weight, 8px radius, no drop shadow — on hover the fill darkens to `--color-accent-primary-hover`; pressed → `--color-accent-primary-active`. Secondary = transparent fill + 1px ink hairline, text-primary label, hover fills to `--color-bg-tertiary`. Only ONE coral button per view. Keyboard focus draws the **blue** `--shadow-focus`, not a coral ring.
- **Cards:** `--color-bg-secondary` (ivory) sheets on the parchment page, 16px radius, 1px hairline ring as the only elevation (`--shadow-md`), ~24px internal padding — paper laid on a desk, one warm tone-step above the canvas, no shadow.
- **Inputs:** `--color-bg-tertiary` (true white) fill, 1px hairline, 8px radius; on focus the border goes blue (`--color-border-focus`) and the 3px blue `--shadow-focus` appears. Placeholder uses text-tertiary. Error border = `--color-status-error` with a role=alert message.
- **MCQ option rows:** flat ring-hairline cards on `--color-bg-secondary` (1px `--color-border-primary` ring, 8px radius, no shadow) — the same one-altitude ring elevation as §6, never a shadow-floating tile and never a colored-left-stripe slop tile. On the wide focus board they lay out as a 2×2 exam-form grid, collapsing to one column on narrow widths and after answering. Pre-answer mouse-selection gets a faint `--color-accent-wash` coral tint + an accent letter marker; chosen-correct uses `--color-status-success-wash` + success marker + success label; chosen-wrong uses `--color-status-error-wash` + error marker — tint + marker, never a colored left bar. Keyboard selection draws the **blue** focus ring.
- **Chips / tags (category, difficulty, "N из M"):** full-pill radius, `--color-bg-tertiary` fill, text-secondary label in ALL-CAPS at `--letter-spacing-wide` 0.10em, 12px. The olive `--color-accent-secondary` may color tag text / charts but is NOT a second brand accent and never fills a CTA. The active/streak chip is the one place an accent-tinted pill may appear, counting against the per-screen accent budget.
