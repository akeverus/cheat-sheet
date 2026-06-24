# Design

> [!IMPORTANT]
> **Этот файл устарел в части архитектуры и реестра. Актуальный master-спек —
> [`docs/design/DESIGN.md`](docs/design/DESIGN.md)** (+ per-design спеки в
> [`docs/design/systems/`](docs/design/systems/)). Что изменилось с момента
> написания этого файла:
> - **Архитектура CSS:** уже НЕ `editorial.css` + тонкие оверлеи. Теперь два слоя —
>   `static/css/tokens.css` (все токены: `:root`=editorial-default + блоки
>   `html[data-design="X"]` light/dark) и `static/css/base.css` (design-agnostic
>   структура, потребляет `var(--token)`). Версии в `head.html` — `?v=10`.
> - **Реестр — 10 дизайнов** (не 4): `editorial` (default), `linear`, `swiss`,
>   `notion`, `mintlify`, `broadsheet`, `superhuman`, `stripe`, `claude`, `theverge`.
> - Гейт контраста — `scripts/design-token-audit.py` (WCAG по реальному каскаду).
>
> Ниже — исторический спек (refero-якоря и крафт-правила всё ещё полезны).

> Визуальная система quiz-app. Это **мульти-дизайн система с переключателем**
> (`window.__design`): общая структура + дизайн «Editorial» (default) в
> `editorial.css`, плюс дизайн-оверлеи `swiss.css`, `linear.css`, `broadsheet.css`.
> Активный дизайн — атрибут `data-design` на `<html>`; тема — `data-theme` (dark по
> умолчанию). Этот файл — спецификация; правки стилей идут в CSS, сюда — фиксация
> решений. Формат — Google Stitch DESIGN.md.
>
> **Refero-якоря (каждый дизайн = 100%-реплика конкретного стиля
> styles.refero.design; spec-точные и derived-значения помечены в CSS):**
> - **Editorial** = «Anthropic» —
>   <https://styles.refero.design/style/d469cba4-c448-4a43-a033-883f8bfcdc42>
>   (ivory `#faf9f5`, чернила `#141413`, Clay `#d97757`, zero box-shadows,
>   flat-кнопки 0px, толстое подчёркивание ссылок). Тёмная тема — derived
>   (из claude.ai `#262624`), у маркетинг-спеки её нет.
> - **Swiss** = «Ui (shadcn/ui)» —
>   <https://styles.refero.design/style/0fd67ec5-7e9c-4ca9-b368-5d9c7388477a>
>   (ахроматика, Geist, Graphite `#0a0a0a` CTA, hairline `#e5e5e5`, радиусы
>   4/10/14, тени запрещены — только oklab-ring). Тёмная тема — derived
>   (light-only спека); статусы — функциональное исключение из ахроматики.
> - **Linear** = «Linear» —
>   <https://styles.refero.design/style/90ce5883-bb24-4466-93f7-801cd617b0d1>
>   (Onyx `#08090a`, Acid Lime `#e4f222` + текст `#030404`, Indigo `#5e6ad2`,
>   радиусы 2/6/12). «No light mode exists» — светлая тема derived
>   (Indigo-акцент). Slate/Indigo подняты до AA в мелком тексте.
> - **Broadsheet** = «General Intelligence Company» —
>   <https://styles.refero.design/style/34baa524-5d5b-4165-bbab-d01f05e6d6b9>
>   (Cream `#fefffc`, serif-заголовки Source Serif 4, Hudson Blue `#0081c0`
>   только в ссылках, Obsidian-кнопки `#1f1f29`, sage-хайрлайны `#dee2de`,
>   pill-навигация 50px, frosted-тени). Тёмная тема — derived (из Graphite
>   Night `#282834`).

## Мульти-дизайн система (переключатель)

**Что это.** Пользователь выбирает один из дизайнов двумя путями: (1) иконка-тоггл
в шапке (близнец тоггла темы, `#design-toggle`, иконка `i-shapes`) — клик циклит
Editorial→Swiss→Linear→Broadsheet; (2) seg-control в Настройки → Персонализация →
«Дизайн». Обе точки синхронятся событием `designchange` (никогда не расходятся).
Программно — `window.__design.set('editorial'|'swiss'|'linear'|'broadsheet')`.
Переключение мгновенное — флип
`data-design` на `<html>`, без перезагрузки. Выбор хранится в `localStorage('design')`;
применяется инлайн в `head.html` ДО первого кадра (как тема) — без вспышки чужого
дизайна. SSR-дефолт `data-design="editorial"` → no-JS получает legacy-дизайн.
Графики `/stats` пере-рисовываются на смену дизайна (MutationObserver на
`data-design`/`data-theme`), читая токены палитры из живого CSS.

**Архитектура CSS (DRY, без дублирования структуры).**
- `editorial.css` = ОБЩАЯ БАЗА: вся структура (layout, spacing, responsive, a11y,
  reduced-motion, ВСЕ компоненты) под нейтральным скоупом `html[data-design]` —
  работает при любом значении. Плюс дефолтные токены (`:root`/`[data-theme]`) =
  идентичность «Editorial». Цвет/шрифт/радиус/тень в компонентах идут только через
  токены, поэтому смена токенов перекрашивает всё.
- Каждый альтернативный дизайн = тонкий оверлей (`swiss.css`, `linear.css`,
  `broadsheet.css`): свои identity-токены под `html[data-design="<name>"]`
  (специфичность 0,1,1 перебивает `:root` 0,1,0) + точечные signature-оверрайды.
  Структуру НЕ дублируют (~200–250 строк против 2300+ у базы).
- Грузятся в порядке: `editorial.css` (база) → оверлеи. Активен только тот, чей
  `data-design` выбран; остальные инертны (не матчатся).
- **Добавить дизайн** = новый оверлей-файл + регистрация в `head.html` (`DESIGNS`,
  `THEME_COLORS`, `<link>`) + кнопка в seg-control на `/settings`. Шапочный тоггл
  подхватит его автоматически (циклит по `window.__design.list`). Editorial и базу
  трогать не нужно.

**Инвариант.** База структурно нейтральна (скоуп `html[data-design]` действует при
любом значении атрибута; обе формы = (0,1,1)). Каждый дизайн обязан проходить
контраст-инварианты в ОБЕИХ темах. **Refero-инвариант:** каждый дизайн на 100%
следует своему якорю; отступления допустимы только ради WCAG AA (помечаются
`[derived]` в CSS с расчётом контраста) и для тем, которых у спеки нет.

Ниже «Overall Vibe … Anti-slop» описывают дизайн **«Editorial»** (база/дефолт,
якорь Anthropic); спеки «Swiss», «Linear», «Broadsheet» — в конце файла.

## Overall Vibe

**Anthropic ivory — исследовательская публикация.**

Сцена: разработчик читает хорошо свёрстанный research-пост на тёплом
ivory-пергаменте; терракота Clay «held in reserve» — единственный хроматический
голос, рамки — hairline, теней нет вовсе, ссылки подчёркнуты толсто и этим всё
сказано. Светлая тема — носитель идентичности (спека: ivory `#faf9f5`, «never
pure white»); тёмная — derived из claude.ai (`#262624`), тот же характер ночью.

Не журнал-лендинг и не SaaS-дашборд: это **инструмент для чтения и думания**.

## Color

Значения в HEX (как в editorial.css). Стратегия — **restrained**: ivory-нейтрали +
один акцент (Clay). Цвет не несёт иерархию — её несёт типографика; цвет несёт
*статус* (verdict) и *акцент* (один голос). Spec-точные значения — `[spec]`,
производные — `[derived]` (см. комменты в editorial.css).

### Light theme (основная идентичность, [spec])

| Роль | HEX | Назначение |
|---|---|---|
| bg-primary | `#FAF9F5` | Ivory Light — пергамент страницы [spec] |
| bg-secondary | `#F0EEE6` | Ivory Medium — release-карточки [spec] |
| bg-tertiary | `#E3DACC` | Oat — код-подложки, треки [spec] |
| text-primary | `#141413` | Slate Dark — чернила [spec], ~16:1 |
| text-secondary | `#5E5D59` | Slate Light [spec], 6.2:1 |
| text-tertiary | `#686760` | Cloud Dark ↓AA [derived], 4.9:1 на карточке |
| text-link | `#141413` | чернила; декор — толстое Clay-подчёркивание |
| accent-primary | `#D97757` | Clay [spec] — CTA-заливки, прогресс, актив |
| accent-on | `#141413` | чернила на терракоте (5.9:1; белый = 2.97 — не AA) |
| border-primary | `#D1CFC5` | Cloud Light hairline [spec] |
| status success `#2E6B45` · error `#B3261E` · warning `#7A5400` · info `#2C5E8A` | | вне спеки [derived], AA на ivory |

### Dark theme (derived — claude.ai палитра)

| Роль | HEX | Назначение |
|---|---|---|
| bg-primary | `#262624` | тёплый графит claude.ai |
| bg-secondary | `#30302E` / bg-tertiary `#3A3937` | панели / подложки |
| text-primary | `#F0EEE6` | тёплый off-white |
| text-secondary | `#C5C1B4` (8.4:1) / tertiary `#A29F94` (5.5:1) | вторичный / мета |
| accent-primary | `#D97757` | Clay [spec] — 4.8:1 на `#262624` (AA и как текст) |
| border-primary | `#3E3C36` | hairline |
| status-success `#6FB585` · error `#E5736B` · warning `#D9A441` · info `#6FA8D6` | | AA на графите |

**Контраст-инварианты:** body ≥4.5:1, крупный/bold ≥3:1 в обеих темах — во всех
четырёх дизайнах. Самоцветная ловушка: hljs код-блок всегда тёмный (`#282c34`) в
обеих темах — инлайн-`code` обязан скоупиться `:not(pre) > code`, иначе перебивает
базовый цвет hljs.

> Ivory `#faf9f5` формально попадает в «cream/sand band» impeccable-бана, но здесь
> это **мандат якоря**: спека Anthropic прямо требует `#faf9f5` («never pure
> white»). 100%-соответствие refero-якорю — явное требование пользователя,
> оно сильнее эвристики.

## Typography

Три семейства (потолок impeccable = 3) — fallback-цепочки самой Anthropic-спеки:

- **Display** — `Lora` (Anthropic Serif → Lora [spec-fallback]): h1–h3, hero-вопрос.
- **Body** — `Inter` (Anthropic Sans → Inter [spec-fallback]): UI-хром и проза.
- **Mono** — `JetBrains Mono` (Anthropic Mono → JetBrains Mono [spec-fallback]):
  эйбрау-метки (FOCUS, Q14/50), код, бейджи, числа.

Шкала (rem, ratio ≈1.25+, не плоская):
`xs .75 · sm .875 · base 1 · md 1.125 · lg 1.375 · xl 1.75 · 2xl 2.25 · 3xl 3 · 4xl 4`.
Веса: 400 / 500 / 600 / 700. Line-height: tight 1.15 · normal 1.5 · relaxed 1.7.
Tracking: tight `-0.02em` (дисплей), wide `0.12em` (капс-метки).

Правила: мера строки 60–68ch (`--measure`); hero — `clamp(1.75rem, …, 3xl)`,
потолок ≤4rem; `text-wrap: balance` на заголовках; капс — только короткие метки
(≤4 слов), не body. drop-cap `3.2em` для разбора.

## Spacing & Layout

8px-база: `--space-1..12` = .25 / .5 / .75 / 1 / 1.5 / 2 / 3 / 4 / 6 / 8 / 12 / 16 rem.
**Все** padding/margin/gap идут через токены (raw-значений нет — инвариант).

**Full-width режим (по запросу пользователя).** Контентные колонки тянутся на
всю ширину вьюпорта: токены ширины `--max-width-{page,focus,reading,result,
settings,data}` = `none`, а проза не ограничена мерой строки (`--measure: none`).
Жёлоб задаёт горизонтальный padding `.ed-page`/masthead (`--space-5`), поэтому
контент выровнен с шапкой и не липнет к краю стекла. Прежние «семантические»
значения колонок (focus 760 / reading 820 / result 880 / settings 860 / data
1180 / page 1280 px) и читаемая мера (68ch) сохранены в комментариях editorial.css
для быстрого отката к колоночному режиму.

**Единственное исключение из full-width — колонка MCQ-опций** (`#interview-options`,
`.result-page .options`): `--max-width-options: 60rem` (960px), left-aligned (левый
край флешит с заголовком/гуттером). Причина: у коротких вариантов при ширине экрана
справа зиял пустой хвост. Разбор-проза (`.answer`) остаётся full-width — кэп только
на самой колонке вариантов. Error-страница тоже центрирована (status-сообщение).

На широких вьюпортах (1920/2560) контент растягивается на всю ширину; строки
длинной прозы становятся длиннее меры (осознанный выбор пользователя ради
максимального использования экрана).
Радиусы [spec]: sm 0 (flat) · md 8 (release cards) · lg 16 (panels) · full 999.
Кнопки flat 0px сигнатурой; primary CTA — асимметричный `0 0 8px 8px` («Try
Claude»). Тени: **нет** («zero box-shadows throughout» [spec]) — elevation несут
hairline-рамки; focus-ring остаётся (a11y).

## Components / Patterns

- **Hairline-чип** (`.zone-chip`, `.topic-badge`): mono xs, uppercase, wide
  tracking, прозрачный фон, рамка `--rule-weight`, radius-full, padding `.25em .75em`.
  Различаются только семантическим цветом (accent vs secondary). Это базовый
  «голос меток» — новые метки наследуют этот паттерн.
- **Линейки вместо карточек.** Иерархия — hairline-`border`/`border-bottom`, а не
  тяжёлые тени-карточки. Карточки — только где это реально лучший аффорданс;
  вложенных карточек нет.
- **Кнопки**: verb+object лейблы. primary (акцентная заливка), `secondary-btn`
  (рамка), `danger-btn`. Touch-target ≥44px на `(hover:none)`.
- **Варианты MCQ**: `<label>` с CSS-counter бейджем (A/B/C/D), radio визуально
  скрыт. Результат — `option-correct` / `option-wrong` / `option-other`.
- **Иконки**: монохромный Lucide SVG-спрайт (`#i-*`) + `.ed-icon`, не эмодзи
  (исключения намеренны: 💡-подсказка, 🎲🤔💪-уверенность).
- **Masthead**: `.ed-masthead` с h1=pageTitle (per-page), эйбрау-kicker
  «Cheat · Sheet», тонкая полоса прогресса.

## Motion

Durations: instant 50 · fast 150 · normal 250 · slow 400 · slower 600 (ms).
Easings: default/`in`/`out` (cubic-bezier), `bounce` — крайне редко.
Правила impeccable: ease-out, без bounce/elastic в обычных переходах; не
анимировать layout-свойства; reveal только поверх уже-видимого контента.

**Reduced-motion (обязательно, есть):** два блока — токены `--duration-*`=0 +
универсальный `*` nuke (`scroll-behavior:auto`, `transition/animation 0.001ms
!important`). Полное покрытие.

## Anti-slop guardrails (этот проект)

- Нет gradient-text, нет side-stripe бордеров как акцента, нет декоративного
  glassmorphism, нет hero-метрики-шаблона, нет эйбрау над *каждой* секцией.
- Один акцентный цвет. Цвет = статус/акцент, иерархия = типографика.
- Ivory light-bg `#faf9f5` — мандат Anthropic-якоря ([spec] «never pure white»),
  не дефолтный AI-тёплый-почти-белый; идентичность держат serif-display + Clay +
  отсутствие теней + flat-кнопки.
- favicon, theme-color, иконки — в фирменной терракоте, без старой индиго-палитры.

## Дизайн «Swiss» (оверлей `swiss.css`)

**Refero-якорь (100%):** «Ui (shadcn/ui)» —
<https://styles.refero.design/style/0fd67ec5-7e9c-4ca9-b368-5d9c7388477a>.
**Vibe:** строго ахроматическая shadcn-система: Chalk-канва, Graphite-чернила,
hairline `#e5e5e5` — «the load-bearing wall».

- **Цвет — АХРОМАТИКА [spec].**
  - Light (герой): bg `#FFFFFF`(канва и карточки)/`#F2F2F2`(Mist); ink `#0A0A0A`
    (Graphite)/`#525252`[derived]/`#737373`(Concrete); border `#E5E5E5`(Hairline);
    accent `#0A0A0A` CTA, hover `#171717`(Carbon), active `#000000` (on `#FFFFFF`).
  - Dark [derived — спека light-only]: bg `#0A0A0A`/`#171717`(Carbon)/`#262626`;
    ink `#FAFAFA`/`#A1A1A1`(Ash)/`#8A8A8A`; border `#262626`; accent `#FAFAFA`
    (on `#0A0A0A`).
  - Статусы — функциональное исключение из «no chromatic» (вердикты квиза).
- **Типографика [spec]:** `Geist` (mandatory; fallback Inter — по спеке),
  `Geist Mono` (→ JetBrains Mono). Tracking только отрицательный (-0.025em).
- **Форма [spec]:** радиусы 4 (micro) / 10 (кнопки-инпуты) / 14 (карточки) /
  9999 (пилюли). Тени запрещены — только 1px ring `oklab(0.145 0 0 / 0.1)`.
- **Signatures:** ссылки — чернила + подчёркивание; эйбрау — Concrete.

## Дизайн «Linear» (оверлей `linear.css`)

**Refero-якорь (100%):** «Linear» —
<https://styles.refero.design/style/90ce5883-bb24-4466-93f7-801cd617b0d1>.
**Vibe:** midnight command deck. **Тёмная тема — герой** (спека: «No light mode
exists for this system»).

- **Цвет:**
  - Dark (герой, [spec]): bg `#08090A`(Onyx)/`#0F1011`(Charcoal)/`#161718`
    (Obsidian); text `#F7F8F8`(Snow)/`#8A8F98`(Fog)/`#7E838C`(Slate ↑AA);
    border `#23252A`(Graphite; Iron `#323334` — medium); accent **Acid Lime
    `#E4F222`** (on `#030404` [spec]); ссылки Indigo `#6E79DC` (`#5e6ad2` ↑AA);
    статусы Emerald `#27A644` / Crimson `#EB5757` / Cyan `#02B8CC` [spec].
  - Light [derived — у спеки нет]: bg `#FFFFFF`/`#F7F8F8`(Snow)/`#ECEDF0`;
    text `#0F1011`(Charcoal)/`#4C5057`/`#6B7079`; accent Indigo `#5E6AD2`
    [spec-цвет, 4.7:1 AA] — лайм на белом нечитаем; border `#E2E4E8`.
- **Типографика [spec]:** `Inter` + feature-сеты `cv01`/`ss03`
  (identity-critical, сигнатурой); mono Berkeley → `JetBrains Mono` (spec-fallback).
- **Форма [spec]:** радиусы 2 (badges) / 6 (кнопки) / 12 (карточки) / 9999.
  Тени: small `0 2px 4px @40%`, large `0 4px 32px rgba(8,9,10,.6)`; inset-ring
  спеки не дублируем — его роль играет border `#23252a` (тот же Graphite).
- **Signatures:** ссылки цветные без подчёркивания (подчёркивание на hover);
  cv01/ss03.

## Дизайн «Broadsheet» (оверлей `broadsheet.css`)

**Refero-якорь (100%):** «General Intelligence Company» —
<https://styles.refero.design/style/34baa524-5d5b-4165-bbab-d01f05e6d6b9>.
**Vibe:** «a publication, not a product catalog» — литературная сдержанность:
cream-канва, серифные заголовки, один яркий синий только в ссылках.

- **Цвет:**
  - Light (герой, [spec]): bg `#FEFFFC`(Cream)/`#FFFFFF`(Paper-карточки)/
    `#F9FAF7`(Linen); text `#171717`(Ink)/`#2C2C2C`(Carbon)/`#646464`(Steel);
    border `#DEE2DE`(Sage hairline); ссылки Hudson Blue (`#0074AD` resting =
    `#0081c0` ↑AA; чистый `#0081C0` — hover/focus); кнопки `#1F1F29`(Obsidian,
    белый текст) — «no filled solid-color buttons on content canvas»;
    outlined-кнопки — рамка Slate Cyan `#41A1CF` [spec].
  - Dark [derived — у спеки нет]: графитовая ночь из `#282834`(Graphite Night):
    bg `#15151D`/`#1D1D27`/`#252531`; text `#F4F5F1`/`#C3C5CE`/`#9094A3`;
    ссылки `#4FB3E2`; CTA — cream-инверсия (`#F4F5F1` on `#1F1F29`).
- **Типографика [spec]:** display `Source Serif 4` (ppmondwest → Source Serif 4 —
  fallback самой спеки); body `Inter` (af → Inter); mono `JetBrains Mono`.
  Display tracking -0.02em.
- **Форма [spec]:** радиусы 4 (кнопки — сигнатурой) / 12 (standard cards) /
  16 (elevated) / nav-pill 50px; hero 24px вне токен-шкалы. Frosted-тени:
  `0 1px 1px @8%` (+`0 4px 5px @8%`), floating-nav `0 2px 6px @15%` +
  гало `0 0 0 5px @4%`.
- **Signatures:** pill-навигация — тёмная Graphite-Night пилюля 50px с
  floating-тенью и белыми ссылками; outlined-кнопки со Slate Cyan-рамкой;
  ссылки с 1px-подчёркиванием.

## Cache discipline

editorial.css → бамп `v=N` в `head.html` (2 строки: preload+stylesheet). Оверлеи
`swiss.css`/`linear.css`/`broadsheet.css` — там же (`v=N`), бампать при правке.
app.js → `v=N` в result/settings/focus-training (3). stats.js → stats.html (1).
Текущее: editorial.css **v70**, swiss.css **v4**, linear.css **v4**,
broadsheet.css **v2**, app.js **v31**,
stats.js **v7**.

> **Mermaid удалён (2026-06-15).** Проект отказался от mermaid-диаграмм: убраны
> CDN-загрузка mermaid.js и фрагмент `mermaid-init.html`, параметр `includeMermaid`
> у `head(...)`, рендер-ветка в `app.js`. `MarkdownRenderService` теперь вырезает
> ```` ```mermaid ```` блоки из вывода (CDN-зависимость и `securityLevel:'loose'`
> исчезли). Диаграммы в контенте — ASCII/текст (см. cheatsheet-writer skill).
