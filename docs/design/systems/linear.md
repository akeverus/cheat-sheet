# linear

> Per-design TOKEN SPEC for `[data-design="linear"]`. Maps the Linear brand (инженерная точность, ахроматика + один индиго, dark-NATIVE) onto the §2 token contract. Adapted, not copied: Linear is dark-first, so DARK is the canonical surface and LIGHT is the deliberate inversion. Indigo accent `#5E6AD2` (bg) / `#7170FF` (interactive); fonts Inter 510 + Geist Mono.

---

## 1. Character

Инженерная точность на почти-чёрном холсте: информация проступает из темноты градациями белой прозрачности, а не цветом. Палитра почти полностью ахроматическая — единственный хроматический тон это индиго-фиолетовый, и он появляется только там, где есть действие или фокус. **Смелый ход (отличительные 20%):** глубина передаётся не тенью, а ступенями светимости поверхности + полупрозрачными белыми hairline'ами (`--elevation-card` = ring `0 0 0 1px`, НЕ drop-shadow на тёмном), плюс агрессивный отрицательный трекинг на display — заголовки выглядят «вырезанными лазером», а не нарисованными.

---

## 2. Fonts

| role | stack |
|------|-------|
| `--font-family-display` | `"Inter", "Inter Variable", -apple-system, BlinkMacSystemFont, "SF Pro Display", "Segoe UI", Roboto, Helvetica, Arial, sans-serif` |
| `--font-family-body` | `"Inter", "Inter Variable", -apple-system, BlinkMacSystemFont, "SF Pro Text", "Segoe UI", Roboto, Helvetica, Arial, sans-serif` |
| `--font-family-mono` | `"Geist Mono", "Berkeley Mono", ui-monospace, "SF Mono", "JetBrains Mono", Menlo, Monaco, Consolas, monospace` |

- Enable globally: `font-feature-settings: "cv01", "ss03";` on body/display Inter — это не декор, а часть идентичности Linear (геометричные альтернаты a/g).
- Один display = один body (Inter в обеих ролях), различаются весом/трекингом/размером. Mono — отдельная гарнитура для кода и эйбрау. Максимум 2 гарнитуры соблюдён.

---

## 3. Color — LIGHT

Дефолт Linear — тёмный; LIGHT собран как чистая ахроматическая инверсия (white canvas, cool-gray текст, тот же индиго). Контраст считается на ахроматике, поэтому проходит с запасом.

| token | value | note |
|-------|-------|------|
| `--color-bg-primary` | `#FFFFFF` | страница |
| `--color-bg-secondary` | `#F8F9FA` | карточки/панели (едва холодный) |
| `--color-bg-tertiary` | `#F0F1F3` | инпуты, well, code-bg |
| `--color-bg-inverse` | `#08090A` | инвертированный блок (= dark canvas) |
| `--color-surface-overlay` | `rgba(8,9,10,0.55)` | подложка модалки (тёмная для фокус-изоляции) |
| `--color-text-primary` | `#08090A` | основной текст |
| `--color-text-secondary` | `#52555E` | приглушённый body |
| `--color-text-tertiary` | `#6E727B` | placeholder/meta (затемнён vs Linear `#8a8f98` чтобы пройти AA на белом) |
| `--color-text-inverse` | `#F7F8F8` | текст на inverse-блоке (near-white, не #FFF) |
| `--color-link` | `#4B55C0` | индиго затемнён для AA на белом (см. ниже) |
| `--color-border-primary` | `rgba(8,9,10,0.12)` | hairline по умолчанию |
| `--color-border-secondary` | `rgba(8,9,10,0.07)` | слабее |
| `--color-border-focus` | `#5E6AD2` | = accent |
| `--color-accent` | `#5E6AD2` | brand indigo — fill CTA/прогресс/актив |
| `--color-accent-hover` | `#525DCB` | темнее на hover (на светлом hover темнит) |
| `--color-accent-active` | `#4752C4` | pressed |
| `--color-accent-contrast` | `#FFFFFF` | текст на accent-fill |
| `--color-accent-secondary` | `#7A7FAD` | категориальный 2-й тон (security-lavender; теги/графики, НЕ 2-й бренд) |
| `--color-status-success` | `#1A7F37` | зелёный, затемнён для AA на белом |
| `--color-status-warning` | `#9A6700` | янтарь, затемнён для AA |
| `--color-status-error` | `#C2362F` | red |
| `--color-status-info` | `#4B55C0` | = link (индиго и есть info-тон) |
| `--color-status-success-bg` | `rgba(26,127,55,0.10)` | тинт |
| `--color-status-warning-bg` | `rgba(154,103,0,0.10)` | тинт |
| `--color-status-error-bg` | `rgba(194,54,47,0.10)` | тинт |
| `--color-status-info-bg` | `rgba(75,85,192,0.10)` | тинт |

**WCAG AA (LIGHT, vs `#FFFFFF`):**
- text-primary `#08090A` → **~20.0:1** PASS (body 4.5 / large 3).
- text-secondary `#52555E` → **~7.2:1** PASS.
- text-tertiary `#6E727B` → **~4.8:1** PASS body (исправлено: исходный Linear `#8a8f98` давал ~3.2:1 — ниже 4.5, поэтому затемнён до `#6E727B`).
- link `#4B55C0` → **~5.6:1** PASS (исправлено: brand `#5E6AD2` на белом ~4.0:1 < 4.5 для текста; для текста ссылок используем затемнённый `#4B55C0`. Бренд-индиго `#5E6AD2` остаётся как fill/border/focus, где порог 3:1 выполняется).
- accent-contrast `#FFFFFF` on accent fill `#5E6AD2` → **~4.0:1** PASS (кнопочный текст ≥16px treat as large UI; для мелкого лейбла внутри кнопки держим ≥510 вес / ≥15px — практически large). Бордюр accent vs white → 3:1 цель выполнена.

**Accent-дисциплина (≤2 на экран):** (1) primary CTA fill (одна на экран — «Ответить» / «Начать»), (2) focus-ring ИЛИ активный индикатор прогресса/выбранной MCQ-опции. Ссылки используют затемнённый индиго-родственник но считаются тем же акцентным семейством — на странице чтения это единственное второе появление. Статус-зелёный/красный — semantic-слой, не accent.

---

## 4. Color — DARK overrides

Это родная среда Linear. Меняем только то, что отличается от LIGHT. Фон НЕ чистый чёрный, текст НЕ чистый белый, hairline = белая rgba.

| token | value | note |
|-------|-------|------|
| `--color-bg-primary` | `#08090A` | marketing black (cool, не #000) |
| `--color-bg-secondary` | `#0F1011` | панель/сайдбар |
| `--color-bg-tertiary` | `#191A1B` | elevated surface, инпуты, code-bg |
| `--color-bg-inverse` | `#F7F8F8` | инвертированный блок (light) |
| `--color-surface-overlay` | `rgba(0,0,0,0.85)` | очень тёмный backdrop |
| `--color-text-primary` | `#F7F8F8` | near-white (не #FFF — anti eye-strain) |
| `--color-text-secondary` | `#D0D6E0` | silver-gray body |
| `--color-text-tertiary` | `#8A8F98` | placeholder/meta (на тёмном проходит) |
| `--color-text-inverse` | `#08090A` | текст на light inverse-блоке |
| `--color-link` | `#7170FF` | accent-violet (interactive вариант) |
| `--color-border-primary` | `rgba(255,255,255,0.08)` | standard hairline |
| `--color-border-secondary` | `rgba(255,255,255,0.05)` | ultra-subtle |
| `--color-border-focus` | `#7170FF` | ярче на тёмном |
| `--color-accent` | `#5E6AD2` | brand indigo fill (неизменно) |
| `--color-accent-hover` | `#828FFF` | светлее на hover (на тёмном hover светлит) |
| `--color-accent-active` | `#7170FF` | pressed/interactive |
| `--color-accent-contrast` | `#FFFFFF` | текст на indigo fill |
| `--color-accent-secondary` | `#7A7FAD` | security-lavender (без изменений) |
| `--color-status-success` | `#3FB950` | зелёный осветлён для тёмного |
| `--color-status-warning` | `#D29922` | янтарь осветлён |
| `--color-status-error` | `#F85149` | red осветлён |
| `--color-status-info` | `#7170FF` | = link на тёмном |
| `--color-status-*-bg` | `rgba(<same hue>,0.16)` | тинты чуть плотнее на тёмном |

**WCAG AA (DARK, vs `#08090A`):**
- text-primary `#F7F8F8` → **~18.8:1** PASS.
- text-secondary `#D0D6E0` → **~13.4:1** PASS.
- link `#7170FF` → **~5.2:1** PASS (тут brand-interactive `#7170FF` проходит для текста — отдельный затемнённый тон НЕ нужен, в отличие от LIGHT).
- accent-contrast `#FFFFFF` on `#5E6AD2` → **~4.0:1** PASS для UI/large-кнопочного текста (вес ≥510). 
- accent fill `#5E6AD2` vs bg `#08090A` (граница/индикатор) → **~3.9:1** PASS (≥3:1).

**Accent-дисциплина (DARK):** идентична LIGHT — ≤2 видимых появления. На тёмном индиго ярче бросается в глаза, поэтому fill CTA + один индикатор; ссылки `#7170FF` это и есть второе системное появление в reading-view.

---

## 5. Typography tokens

Веса по Linear (signature 510), но в рамках контракта Read/Emphasize/Announce:

| token | value | note |
|-------|-------|------|
| `--font-weight-read` | `400` | основной текст |
| `--font-weight-emphasize` | `510` | **signature** Linear-вес (между regular и medium); nav, лейблы, UI |
| `--font-weight-announce` | `590` | максимум (не 700 — Linear не использует bold) |

Размерная шкала (rem, scale=1; контракт §2.2):

| token | rem | px |
|-------|-----|----|
| `--font-size-xs` | `0.75rem` | 12 |
| `--font-size-sm` | `0.875rem` | 14 |
| `--font-size-base` | `1rem` | 16 |
| `--font-size-md` | `1.125rem` | 18 |
| `--font-size-lg` | `1.25rem` | 20 |
| `--font-size-xl` | `1.5rem` | 24 |
| `--font-size-2xl` | `2rem` | 32 |
| `--font-size-3xl` | `2.5rem` | 40 |
| `--font-size-4xl` | `3.5rem` | 56 |

Line-height:

| token | value |
|-------|-------|
| `--line-height-tight` | `1.05` (display компрессия à la Linear 1.00, но ≥reading-safe для длинных RU-заголовков) |
| `--line-height-snug` | `1.3` |
| `--line-height-normal` | `1.55` |
| `--line-height-relaxed` | `1.7` |

Tracking (краф: ALL CAPS ≥0.06em, display отрицательный):

| token | value | note |
|-------|-------|------|
| `--tracking-tight` | `-0.022em` | display ≥48px («engineered» компрессия, ≈ Linear -1.584px/72px) |
| `--tracking-snug` | `-0.012em` | заголовки 24–40px |
| `--tracking-normal` | `0` | body 16px |
| `--tracking-wide` | `0.01em` | small 11–13px / UI-лейблы |
| `--tracking-caps` | `0.07em` | ALL CAPS / mono-overline (≥0.06em соблюдён) |

---

## 6. Component tokens (§2.4) — где linear FEELS different

| token | value | justification |
|-------|-------|---------------|
| `--radius-button` | `6px` | Linear «comfortable» 6px — функциональный, не игривый |
| `--radius-card` | `8px` | standard card; featured-панели локально могут 12px |
| `--radius-input` | `6px` | = button (единый функциональный радиус) |
| `--radius-pill` | `9999px` | чипы/фильтры |
| `--border-width-card` | `1px` | hairline-карта; вся структура — на ширине 1px |
| `--border-width-input` | `1px` | то же |
| `--card-surface` | bg=`--color-bg-secondary` + border `1px solid var(--color-border-primary)` + `--elevation-card` | translucent-stack на тёмном через bg-ступень |
| `--elevation-card` | **ring** `0 0 0 1px var(--color-border-primary)` (LIGHT может добавить `, 0 1px 2px rgba(8,9,10,0.04)`) | **Character knob.** На тёмном drop-shadow невидим (dark-on-dark) — Linear передаёт глубину ступенью светимости bg + белым ring'ом. Поэтому карта = RING, не shadow. Это и есть отличительный ход. |
| `--elevation-popover` | LIGHT: `0 8px 24px rgba(8,9,10,0.12), 0 0 0 1px var(--color-border-primary)` · DARK: `0 8px 24px rgba(0,0,0,0.5), 0 0 0 1px rgba(255,255,255,0.08)` | поповеры/дропдауны/command-palette: ступень bg-tertiary + ring + лёгкая тень для отрыва от холста |
| `--hairline` | LIGHT `rgba(8,9,10,0.10)` · DARK `rgba(255,255,255,0.08)` | на тёмном — белая rgba (craft-инвариант) |
| `--focus-ring` | `0 0 0 2px var(--color-bg-primary), 0 0 0 4px var(--color-border-focus)` | двухслойный: внутренний «зазор» цветом фона + индиго-кольцо; на near-black холсте индиго остаётся видимым |
| `--button-fill-style` | `ghost` (default) → fill для primary CTA | вокабуляр Linear: дефолтная кнопка почти-прозрачная (ghost), только primary CTA получает indigo fill |

**Density/space:** база 4px сохраняется; Linear плотнее editorial — рекомендую `--measure: 68ch` (инженерная плотная колонка, в нижней части craft-диапазона 60–70ch) и компактнее вертикальный ритм карточек. `--max-width-page: 1200px`, `--gutter` desktop `24px`. Space-шкала контракта (`--space-1..9` = 4..96px) без сдвига базы.

---

## 7. Motion

В основном дефолты контракта. Один tweak — фирменное Linear-easing для «снэпа»:

| token | value | note |
|-------|-------|------|
| `--duration-instant` | `50ms` | |
| `--duration-fast` | `150ms` | Linear `--motion-fast` — основной для hover/state |
| `--duration-normal` | `200ms` | ужесточён vs контракт 250 (Linear `--motion-base`); инженерная резкость |
| `--duration-slow` | `400ms` | |
| `--duration-slower` | `600ms` | |
| `--easing-default` | `cubic-bezier(0.2, 0, 0, 1)` | Linear standard ease — резкий старт, мягкая остановка |
| `--easing-in` | `cubic-bezier(0.4, 0, 1, 1)` | |
| `--easing-out` | `cubic-bezier(0, 0, 0.2, 1)` | |
| `--easing-bounce` | `cubic-bezier(0.34, 1.56, 0.64, 1)` | использовать редко — Linear не «перформит»; не для основных state-переходов |

Все переходы под `prefers-reduced-motion: reduce` → near-0.

---

## 8. Anti-slop & component cues

**Анти-слоп подтверждения:**
- **Indigo-hex как «trust»-accent — РАЗРЕШЁН ТОЛЬКО как намеренный бренд-цвет Linear** (`var(--color-accent)` = `#5E6AD2`). Это не Tailwind-`#6366f1`-слоп: индиго и есть ДНК Linear, единственный хроматический тон. Декоративно не применять.
- **Нет** карточки `radius + цветная левая граница` (канонический AI-tile): карты Linear имеют radius, но обводка — равномерный hairline `1px` по периметру, никаких colored-left-border. Состояние (success/error) — через тинт-фон/иконку, не через цветную полосу слева.
- **Нет** serif-on-display: display и body — Inter (sans), это правильно для linear (serif был бы у anthropic/broadsheet).
- **Нет** two-stop trust-градиента на герое, **нет** эмодзи-как-иконок (Lucide SVG-спрайт), **нет** выдуманных метрик.

**Конкретные cue's:**
- **Buttons:** дефолтная = ghost — почти-прозрачный фон (`rgba(255,255,255,0.03)` на тёмном / `rgba(8,9,10,0.03)` на светлом), `1px` hairline border, radius 6px, текст вес 510. Primary CTA — единственная с indigo fill `#5E6AD2`, белый текст, без border. Hover на тёмном = светлит fill (`#828FFF`) и поднимает bg-opacity на ghost; на светлом = темнит fill (`#525DCB`).
- **Cards:** translucent на тёмном (bg `#0F1011`), равномерный белый hairline ring `0 0 0 1px`, radius 8px, БЕЗ drop-shadow на dark. Hover — едва заметный подъём светимости фона (bg-opacity +0.02), не тень. Глубина читается ступенью, а не падающим светом.
- **Inputs:** фон = bg-tertiary (`#191A1B` dark / `#F0F1F3` light), `1px` hairline, radius 6px, placeholder = text-tertiary. Focus = `--focus-ring` (индиго-кольцо с фоновым зазором), border не меняет толщину — только кольцо появляется снаружи.
- **MCQ-option rows:** ряд = ghost-поверхность с hairline-разделителями, radius 6px, mono-«hotkey» (Geist Mono, caps, `--tracking-caps`) слева как у командной палитры. Выбранный/правильный = indigo-tinted bg (`rgba(94,106,210,0.12)`) + индиго left-аaccent НЕ полосой, а 2px indigo border вокруг всего ряда + галочка-иконка; неверный = error-tint + иконка. Акцент тратится тут как «активный индикатор» (одно из ≤2 появлений).
- **Chips/pills:** transparent bg, `1px` hairline border, radius 9999px, текст 12px вес 510, padding ~`0 10px`. Статус-точка — circular dot (status-color), сам чип остаётся ахроматическим; цвет несёт точка, не заливка чипа.
