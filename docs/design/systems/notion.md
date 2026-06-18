# notion

Per-design TOKEN SPEC for `[data-design="notion"]`. Warm-minimalist skin for the switchable redesign. Concrete values for every §2 contract token, LIGHT + DARK, plus §2.4 character decisions. Directly convertible to a `[data-design="notion"]` / `[data-design="notion"][data-theme="dark"]` CSS block.

---

## 1. Character

Тёплый минимализм «качественной бумаги»: тёплая серошкала с жёлто-коричневым подтоном, near-black текст вместо чистого чёрного, рамки-шёпот в 1px и тени, которые *чувствуются, а не видны*. Это «холст, который уходит с дороги» — максимум воздуха, минимум хрома, единственный насыщенный цвет — notion blue.

**Смелый ход (отличительные 20%):** карточки стоят на **плоском hairline-ring (`box-shadow: 0 0 0 1px`)**, а НЕ на drop-тени и НЕ на border — поверхности «впечатаны» в страницу, не парят над ней. Тени приберегаются исключительно для всплывающих слоёв (поповеры/модалки), где они работают как многослойный (4 слоя, opacity ≤0.04) ambient-occlusion. Радиусы малы и трезвы (кнопки/инпуты 4px), badges — полные pill. Эффект — спокойный, тактильный, документ-как-продукт.

---

## 2. Fonts

| Роль | Стек |
|------|------|
| display | `"Inter", "Inter var", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif` |
| body | `"Inter", "Inter var", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif` |
| mono | `"JetBrains Mono", ui-monospace, "SF Mono", SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", monospace` |

Inter и для display, и для body (бренд NotionInter = модифицированный Inter; берём стоковый Inter). OpenType `cv11`/`ss01` опциональны на крупных заголовках, но не обязательны для токен-блока.

---

## 3. Color — LIGHT

| Токен | Значение | Заметка |
|-------|----------|---------|
| `--color-bg-primary` | `#FFFFFF` | чистый белый холст |
| `--color-bg-secondary` | `#FFFFFF` | карточки — белые; разделяются ring'ом, не заливкой |
| `--color-bg-tertiary` | `#F6F5F4` | warm white — инпуты, well, code-bg, секц-чередование |
| `--color-bg-inverse` | `#31302E` | warm dark блок |
| `--color-surface-overlay` | `rgba(15,14,13,0.32)` | тёплая подложка модалки |
| `--color-text-primary` | `rgba(0,0,0,0.95)` | сигнатурный near-black |
| `--color-text-secondary` | `#615D59` | warm gray 500 |
| `--color-text-tertiary` | `#85807A` | плейсхолдер/disabled (затемнён с `#a39e98` ради AA-large — см. ниже) |
| `--color-text-inverse` | `#F6F5F4` | текст на inverse/на accent-fill |
| `--color-link` | `#0075DE` | notion blue (underline-on-hover) |
| `--color-border-primary` | `rgba(0,0,0,0.10)` | whisper border |
| `--color-border-secondary` | `rgba(0,0,0,0.06)` | слабее, для row-делителей |
| `--color-border-focus` | `#097FE8` | focus blue |
| `--color-accent` | `#0075DE` | единственный акцент |
| `--color-accent-hover` | `#0068C6` | чуть темнее (hover-fill CTA) |
| `--color-accent-active` | `#005BAB` | active blue (pressed) |
| `--color-accent-contrast` | `#FFFFFF` | текст поверх accent-fill |
| `--color-accent-secondary` | `#2A9D99` | teal — категориальный 2-й тон для тэгов/графиков (НЕ 2-й бренд-акцент) |
| `--color-status-success` | `#1AAE39` | notion green |
| `--color-status-success-bg` | `#EAF7EC` | |
| `--color-status-warning` | `#B8540B` | затемнён с `#dd5b00` ради AA на тексте |
| `--color-status-warning-bg` | `#FBF0E7` | |
| `--color-status-error` | `#DC2626` | |
| `--color-status-error-bg` | `#FCECEC` | |
| `--color-status-info` | `#097FE8` | badge blue text |
| `--color-status-info-bg` | `#F2F9FF` | badge blue bg (pill) |

**WCAG AA проверка (на `--color-bg-primary` #FFFFFF):**
- text-primary `rgba(0,0,0,0.95)` ≈ `#0D0D0D` → **~19.6:1** — AAA. PASS.
- text-secondary `#615D59` → **~5.6:1** — PASS (body ≥4.5:1).
- link `#0075DE` → **~4.6:1** — PASS как body-текст ссылки (граница; для ≥18px запас больше). OK.
- accent-contrast `#FFFFFF` на accent-fill `#0075DE` → **~4.6:1** — PASS для жирной/крупной кнопочной надписи (≥600/≥16px = large по WCAG). OK.
- *Fix применён:* text-tertiary `#a39e98` давал на белом ~2.6:1 (fail даже для large) → затемнил до `#85807A` (**~3.1:1**, AA-large/placeholder min). status-warning `#dd5b00` (~3.5:1) → `#B8540B` (**~4.6:1**) для текста статуса.

**Дисциплина акцента (≤2 на экран):** (1) одна primary-CTA с blue-fill *или* активный сегмент/прогресс; (2) ссылки + focus-ring (один акцентный тон, считается как одно использование). Всё остальное — нейтрали и `info`-pill (info = тот же синий тинт, но как поверхность badge, не как акцент-fill).

---

## 4. Color — DARK overrides

Только изменяемые токены. Фон — `#191919` (warm near-black, НЕ чистый чёрный); foreground — warm off-white (НЕ чистый белый). На тёмном hairlines — белая rgba.

| Токен | Значение | Заметка |
|-------|----------|---------|
| `--color-bg-primary` | `#191919` | notion dark canvas |
| `--color-bg-secondary` | `#202020` | карточка чуть светлее фона (ring всё ещё несёт край) |
| `--color-bg-tertiary` | `#262625` | инпут/well/code-bg, warm |
| `--color-bg-inverse` | `#F6F5F4` | |
| `--color-surface-overlay` | `rgba(0,0,0,0.55)` | |
| `--color-text-primary` | `rgba(255,255,255,0.92)` | warm off-white, не #FFF |
| `--color-text-secondary` | `#9B958E` | warm gray, осветлён под тёмный фон |
| `--color-text-tertiary` | `#6E6862` | placeholder/disabled |
| `--color-text-inverse` | `#1F1E1D` | текст на inverse-блоке |
| `--color-link` | `#62AEF0` | link light blue (читаемее на тёмном) |
| `--color-border-primary` | `rgba(255,255,255,0.10)` | белый whisper |
| `--color-border-secondary` | `rgba(255,255,255,0.06)` | |
| `--color-border-focus` | `#62AEF0` | |
| `--color-accent` | `#2B8FEE` | осветлённый notion blue для контраста на тёмном |
| `--color-accent-hover` | `#3D9BF2` | |
| `--color-accent-active` | `#1F7BD6` | |
| `--color-accent-contrast` | `#0C0C0C` | тёмный текст на светлом accent-fill |
| `--color-accent-secondary` | `#3DBFBA` | teal, осветлён |
| `--color-status-success` | `#46C45F` | |
| `--color-status-success-bg` | `rgba(70,196,95,0.14)` | |
| `--color-status-warning` | `#E08B3D` | |
| `--color-status-warning-bg` | `rgba(224,139,61,0.14)` | |
| `--color-status-error` | `#F26D6D` | |
| `--color-status-error-bg` | `rgba(242,109,109,0.14)` | |
| `--color-status-info` | `#62AEF0` | |
| `--color-status-info-bg` | `rgba(98,174,240,0.14)` | |

**Re-check AA (на `#191919`):**
- text-primary `rgba(255,255,255,0.92)` → **~14.5:1** — PASS.
- text-secondary `#9B958E` → **~5.6:1** — PASS.
- link `#62AEF0` → **~6.9:1** — PASS.
- accent-contrast `#0C0C0C` на accent-fill `#2B8FEE` → **~5.9:1** — PASS.

---

## 5. Typography tokens

```
--font-family-display : см. §2 (Inter)
--font-family-body    : см. §2 (Inter)
--font-family-mono    : см. §2 (JetBrains Mono)
```

Размерная шкала (rem @16px base, контрактные слоты):

| Токен | rem | px |
|-------|-----|----|
| `--font-size-xs` | 0.75 | 12 |
| `--font-size-sm` | 0.875 | 14 |
| `--font-size-base` | 1 | 16 |
| `--font-size-md` | 1.125 | 18 |
| `--font-size-lg` | 1.25 | 20 |
| `--font-size-xl` | 1.5 | 24 |
| `--font-size-2xl` | 2 | 32 |
| `--font-size-3xl` | 2.5 | 40 |
| `--font-size-4xl` | 3.5 | 56 |

Веса (Notion использует более широкий диапазон; display тяготеет к 700, но в рамках контракта 3 веса):

```
--font-weight-read     : 400
--font-weight-emphasize: 530   /* UI/навигация/эмфаза — между notion 500 и 600 */
--font-weight-announce : 680   /* заголовки/display — приближение notion 700 */
```

Line-height:
```
--line-height-tight  : 1.05   /* display ≥40px — notion-компрессия (1.00–1.04) */
--line-height-snug   : 1.27   /* card-title/sub-heading */
--line-height-normal : 1.55   /* проза (notion body 1.50, чуть воздуха для кириллицы) */
--line-height-relaxed: 1.7
```

Tracking (краф-инварианты: ALL CAPS ≥0.06em, display отрицательный):
```
--tracking-tight  : -0.025em  /* display ≥48px (notion -2.125px/64px ≈ -0.033em; берём -0.025 как безопасный для кириллицы) */
--tracking-snug   : -0.012em  /* заголовки ≥32px */
--tracking-normal : 0         /* body */
--tracking-wide   : 0.01em    /* small/caption 12–14px (notion badge +0.125px/12px ≈ +0.01em) */
--tracking-caps   : 0.07em    /* ALL CAPS лейблы */
```

---

## 6. Component tokens (§2.4) — где notion ОЩУЩАЕТСЯ иначе

```
--radius-button : 4px      /* notion micro-radius — кнопки трезвы, почти прямоугольны */
--radius-card   : 12px     /* comfortable card */
--radius-input  : 4px      /* совпадает с кнопкой */
--radius-pill   : 9999px   /* badges/chips — полный pill (сигнатура) */

--border-width-card  : 0      /* НЕ border — край несёт ring (см. elevation) */
--border-width-input : 1px

--card-surface   : bg-secondary + elevation-card (ring), без отдельного border
--elevation-card : 0 0 0 1px var(--color-border-primary)   /* RING — не тень, не border */
--elevation-popover :
    rgba(0,0,0,0.04)  0px 4px 18px,
    rgba(0,0,0,0.027) 0px 2px 7.85px,
    rgba(0,0,0,0.02)  0px 0.8px 2.93px,
    rgba(0,0,0,0.01)  0px 0.175px 1.04px,
    0 0 0 1px var(--color-border-primary)   /* 4-слойная notion-тень + ring, ТОЛЬКО для overlay */

--hairline   : var(--color-border-primary)   /* light rgba(0,0,0,.10) · dark rgba(255,255,255,.10) */
--focus-ring : 0 0 0 3px rgba(9,127,232,0.30)   /* dark: 0 0 0 3px rgba(98,174,240,0.35) */
--button-fill-style : fill   /* primary = blue solid-fill; secondary = ghost на rgba(0,0,0,.05) */
```

**Justify elevation-card = RING (`0 0 0 1px`):** это и есть смелый ход дизайна. Notion DESIGN.md описывает «Whisper (Level 1)» как стандарт для карточек, а drop-тени называет «felt rather than seen» и применяет к контенту, но в нашем контракте мы выбираем дисциплину: карточки = плоский hairline-ring (нулевой blur → ничего не «парит»), тени резервируем для всплывающих слоёв (`--elevation-popover`). Ring вместо border даёт идеально ровный 1px-край без сдвига box-model и одинаково читается в light/dark (меняется только цвет hairline). `--border-width-card: 0`, потому что край рисует ring, а не border — иначе был бы двойной контур.

**Density-shift:** базовая плотность не меняется (контракт `--space-*` structural), но компонентные паддинги notion компактны: кнопка `var(--space-2) var(--space-4)` (8/16), pill `var(--space-1) var(--space-2)` (4/8), инпут ~6px вертикали. Секционный ритм щедрый (desktop 80px) — оставляем как есть.

---

## 7. Motion

Notion держит дефолты контракта (спокойный, не «перформит»). Один твик — стандартный easing ближе к notion-кривой:

```
--duration-instant : 50ms
--duration-fast    : 150ms
--duration-normal  : 200ms   /* notion --motion-base = 200, чуть быстрее контрактных 250 */
--duration-slow    : 400ms
--duration-slower  : 600ms

--easing-default : cubic-bezier(0.2, 0, 0, 1)   /* notion ease-standard — мягкий out */
--easing-in      : cubic-bezier(0.4, 0, 1, 1)
--easing-out     : cubic-bezier(0, 0, 0.2, 1)
--easing-bounce  : cubic-bezier(0.34, 1.56, 0.64, 1)
```

Кнопки: hover — мгновенный сдвиг фона (150ms); active — лёгкий `scale(0.98)` (notion использует 0.9, но это агрессивно для UI-тренажёра — смягчаем). Всё под `prefers-reduced-motion: reduce` → near-0.

---

## 8. Anti-slop & component cues

**Анти-слоп подтверждения:**
- Indigo HEX: **нет.** Единственный насыщенный цвет — notion blue `#0075DE`, и это намеренный бренд-accent через `var(--color-accent)`. `--color-accent-secondary` = teal `#2A9D99`, не indigo.
- Карточка-с-радиусом-+-цветной-левой-границей: **нет.** Карточки = radius 12px + нейтральный hairline-**ring** (равномерный, не левый). Цветные акцент-полосы слева не используются — статус сигналим через `*-bg` тинт + иконку, не через colored-left-border на скруглённой плитке.
- Serif-на-display: неприменимо — notion целиком sans (Inter и для display, и для body), serif здесь не задан.
- Эмодзи-как-иконки: нет — Lucide SVG-спрайт (намеренные 💡/🎲🤔💪 уверенности — единственное исключение проекта).
- Two-stop trust-градиент на герое: нет — фоны плоские (белый / warm-white чередование).

**Конкретные cues:**
1. **Кнопки** — primary: blue solid-fill `#0075DE`, белый текст 530–600 веса, radius **4px** (почти прямоугольная, не «таблетка»), паддинг 8/16, hover темнеет к `#0068C6`, active `scale(0.98)`. Secondary: ghost на `rgba(0,0,0,0.05)`, near-black текст, тот же 4px радиус. Focus → 3px синее кольцо (`--focus-ring`).
2. **Карточки** — белый фон (light) / `#202020` (dark), radius 12px, **никакого border и никакой drop-тени** — только `box-shadow: 0 0 0 1px` hairline-ring. Заголовок Inter ~22px/680, тело 16px/400 в `--color-text-secondary` (#615D59). Поверхность «впечатана», не парит.
3. **Инпуты** — warm-white fill `#F6F5F4` (light) / `#262625` (dark), 1px border `#dddddd`-эквивалент через `--color-border-primary`, radius 4px, плейсхолдер warm-gray `#85807A`, focus → синее кольцо. Компактная вертикаль (~6px).
4. **MCQ-option rows** — белая строка на hairline-ring (как мини-карточка), radius 4px, между опциями — `--color-border-secondary` тонкий делитель; hover = warm-white подсветка `#F6F5F4`; выбранный/правильный — `--color-status-success-bg` тинт + success-иконка (не colored-left-border); неправильный — `--color-status-error-bg` тинт. Текст рендерится inline-markdown (`code`/**bold**).
5. **Chips / badges** — полный pill (9999px), info-badge: bg `#F2F9FF` + text `#097FE8` (notion badge), 12px/600, tracking `+0.01em`, паддинг 4/8. Категориальные chips используют нейтральный warm-white fill или teal `--color-accent-secondary`, никогда не синий-fill (синий зарезервирован под единственный accent на экран).
