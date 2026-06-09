# Design

> Визуальная система quiz-app. Теперь это **мульти-дизайн система с переключателем**
> (`window.__design`): общая структура + дизайн «Editorial» (default/legacy) в
> `editorial.css`, плюс альтернативные дизайн-оверлеи `swiss.css`, `linear.css`.
> Активный дизайн — атрибут `data-design` на `<html>`; тема — `data-theme` (dark по
> умолчанию). Этот файл — спецификация; правки стилей идут в CSS, сюда — фиксация
> решений. Формат — Google Stitch DESIGN.md.
>
> Refero-якоря дизайнов (styles.refero.design): Editorial = книжный letterpress,
> Swiss = «Ui (shadcn) — Brutalist Swiss grid in graphite», Linear = «Linear —
> Midnight command deck with acid-lime accents».

## Мульти-дизайн система (переключатель)

**Что это.** Пользователь выбирает один из дизайнов двумя путями: (1) иконка-тоггл
в шапке (близнец тоггла темы, `#design-toggle`, иконка `i-shapes`) — клик циклит
Editorial→Swiss→Linear; (2) seg-control в Настройки → Персонализация → «Дизайн».
Обе точки синхронятся событием `designchange` (никогда не расходятся). Программно —
`window.__design.set('editorial'|'swiss'|'linear')`. Переключение мгновенное — флип
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
- Каждый альтернативный дизайн = тонкий оверлей (`swiss.css`, `linear.css`): свои
  identity-токены под `html[data-design="<name>"]` (специфичность 0,1,1 перебивает
  `:root` 0,1,0) + точечные signature-оверрайды. Структуру НЕ дублируют (~200–250
  строк против 2300 у базы).
- Грузятся в порядке: `editorial.css` (база) → оверлеи. Активен только тот, чей
  `data-design` выбран; остальные инертны (не матчатся).
- **Добавить дизайн** = новый оверлей-файл + регистрация в `head.html` (`DESIGNS`,
  `THEME_COLORS`, `<link>`) + кнопка в seg-control на `/settings`. Шапочный тоггл
  подхватит его автоматически (циклит по `window.__design.list`). Editorial и базу
  трогать не нужно.

**Инвариант.** Editorial остаётся точь-в-точь как был (рефактор скоупа
`html[data-design="editorial"]`→`html[data-design]` специфичностно-нейтрален: обе
формы = (0,1,1), а атрибут всегда присутствует). Каждый дизайн обязан проходить
контраст-инварианты в ОБЕИХ темах.

Ниже «Overall Vibe … Anti-slop» описывают дизайн **«Editorial»** (база/дефолт);
спеки «Swiss» и «Linear» — в конце файла.

## Overall Vibe

**Night Study — letterpress on warm ink.**

Сцена: разработчик готовится к собеседованию поздним вечером за деревянным столом
под тёплой лампой; перед ним — хорошо набранная печатная книга вопросов. Отсюда
всё: почти-чёрный тёплый фон (тёмная «чернильная» бумага), терракотовый акцент как
оттиск штампа/свет лампы, высококонтрастный serif (Fraunces) для заголовков,
читаемый book-serif (Newsreader) для разбора, моноширинные капс-метки как строки
кода на полях. Светлая тема — та же книга при дневном свете на кремовой бумаге.

Не журнал-лендинг и не SaaS-дашборд: это **инструмент для чтения и думания**.
Тёмная тема — носитель идентичности; светлая — равноправный дневной режим.

## Color

OKLCH-мышление, значения в HEX (как в editorial.css). Стратегия — **restrained**:
тинтованные нейтрали + один акцент (терракота). Цвет не несёт иерархию — её несёт
типографика; цвет несёт *статус* (verdict, success/error) и *акцент* (один голос).

### Dark theme (по умолчанию — основная идентичность)

| Роль | HEX | Назначение |
|---|---|---|
| bg-primary | `#16130D` | фон страницы — тёплый near-black «чернила» |
| bg-secondary | `#1F1B13` | приподнятые поверхности (sidebar, чипы-блоки) |
| bg-tertiary | `#272117` | инлайн-код, треки прогресса, hover-подложки |
| text-primary | `#ECE4D4` | основной текст (тёплый off-white) |
| text-secondary | `#A99E89` | вторичный текст, подписи |
| text-tertiary | `#7C7263` | третичный (мета, hint) |
| accent-primary | `#E0895C` | терракота — ссылки, акцент, активные состояния |
| accent-on | `#1A1208` | текст на акцентной заливке |
| border-primary | `#352E22` | hairline-линейки, рамки |
| status-success | `#6FB585` | «Верно» |
| status-error | `#E5736B` | «Неверно» |
| status-warning | `#D9A441` / status-info `#6FA8D6` | предупреждения / инфо |

### Light theme (дневная бумага — вторичная)

| Роль | HEX | Назначение |
|---|---|---|
| bg-primary | `#F7F3EA` | кремовая бумага |
| bg-secondary | `#FCF9F2` / bg-tertiary `#EFE8D9` | поверхности / подложки |
| text-primary | `#211C15` | чернильный charcoal |
| text-secondary | `#5A5145` / tertiary `#8A8073` | вторичный / третичный |
| accent-primary | `#A8431F` | терракота (темнее для контраста на бумаге) |
| border-primary | `#DDD5C4` | hairline |
| status-success `#2E6B45` · error `#B3261E` · warning `#7A5400` · info `#2C5E8A` | | статусы (затемнены под AA на бумаге) |

**Контраст-инварианты (проверено):** body ≥4.5:1, крупный/bold ≥3:1 в обеих
темах. Светлая тема прошла инструментальный аудит (0 фейлов). Самоцветная ловушка:
hljs код-блок всегда тёмный (`#282c34`) в обеих темах — инлайн-`code` обязан
скоупиться `:not(pre) > code`, иначе перебивает базовый цвет hljs.

> ⚠️ Известный долг (audit-first, не трогать без live-проверки): light bg `#F7F3EA`
> попадает в «cream/sand AI-default» band. Смягчено тем, что **dark — основная
> тема**, а кремовый — осознанный «дневная бумага». Пересмотр только с живым
> рендером и согласием — это часть идентичности, которой пользователь дорожит.

## Typography

Три семейства (потолок impeccable = 3), контраст по оси serif↔mono:

- **Display** — `Fraunces` (high-contrast modern serif): h1–h3, hero-вопрос.
- **Body/Reading** — `Newsreader` (book serif): разбор, длинная проза.
- **Mono** — `JetBrains Mono`: эйбрау-метки (FOCUS, Q14/50), код, бейджи, числа.

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
Радиусы: sm 3 · md 6 · lg 10 · full 999. Тени мягкие, тёплые (`rgba(33,28,21,…)`
в light; `rgba(0,0,0,…)` в dark).

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
- Cream light-bg — осознанный «дневная бумага», не дефолтный AI-тёплый-почти-белый;
  идентичность держит dark-тема + serif + терракота, а не светлый фон.
- favicon, theme-color, иконки — в фирменной терракоте, без старой индиго-палитры.

## Дизайн «Swiss» (оверлей `swiss.css`)

**Refero-якорь:** «Ui (shadcn) — Brutalist Swiss grid in graphite».
**Vibe:** International Typographic Style × shadcn. Стерильный графит, острые углы,
тонкая хайрлайн-сетка, плоско. Полная противоположность тёплому книжному Editorial.

- **Цвет — МОНОХРОМ.** Акцент = чернила (ink), а не цвет: чёрная primary-кнопка
  (light) / белая (dark) — shadcn-приём. Хроматику несут ТОЛЬКО статусы (verdict
  success/error/warning/info). Поверхности — нейтральный zinc без тёплого тинта.
  - Light: bg `#FFFFFF`/`#FAFAFA`/`#F4F4F5`; ink `#18181B`/`#52525B`/`#71717A`;
    border `#E4E4E7`; accent `#18181B` (on `#FFFFFF`).
  - Dark: bg `#09090B`/`#18181B`/`#27272A`; ink `#FAFAFA`/`#A1A1AA`/`#71717A`;
    border `#27272A`; accent `#FAFAFA` (on `#18181B`).
- **Типографика:** `Inter` (display+body, sans везде — никакого serif),
  `JetBrains Mono` (код/эйбрау/числа). Tracking display чуть плотнее (-0.022em).
- **Форма:** острые углы (radius 0/2/4px), плоско (тени минимальны, иерархия —
  хайрлайн-рамки + space). Фокус — графитовое кольцо.
- **Signatures:** ссылки — чёткое подчёркивание (offset .18em); эйбрау-метки
  графитовые (часть сетки, не «голос акцента»).

## Дизайн «Linear» (оверлей `linear.css`)

**Refero-якорь:** «Linear — Midnight command deck with acid-lime accents».
**Vibe:** современный продуктовый «командный пульт». Холодный near-black midnight +
один яркий кислотный лайм. **Тёмная тема — герой** (Linear dark-native); светлая —
чистый дневной вариант.

- **Цвет:**
  - Dark (герой): bg `#0B0C0E`/`#141518`/`#1C1E22`; text `#F7F8F8`/`#9CA0A8`/
    `#62666D`; accent **acid-lime `#BCF03D`** (on `#0B1402`); border `#23262B`.
    Фокус — лаймовое свечение `rgba(188,240,61,.35)`.
  - Light (дневной): bg `#FFFFFF`/`#F7F8F9`/`#ECEEF1`; text `#0D0E10`/`#4A4E57`/
    `#8A8F98`; accent — глубокий лайм `#4D7C0F` (AA-safe для текста; кислотный лайм
    в light не проходит контраст, потому затемнён). border `#E3E6EA`.
- **Типографика:** `Inter`, плотный трекинг; `JetBrains Mono` — код/эйбрау.
- **Форма:** мягкие современные радиусы (4/8/12px — круглее Editorial), тонкая
  elevation. Эйбрау/актив/прогресс — лаймовые.
- **Signatures:** ссылки цветные без подчёркивания (подчёркивание на hover).

## Cache discipline

editorial.css → бамп `v=N` в `head.html` (2 строки: preload+stylesheet). Оверлеи
`swiss.css`/`linear.css` — там же (`v=N`), бампать при правке оверлея.
app.js → `v=N` в result/settings/focus-training (3). stats.js → stats.html (1).
Текущее: editorial.css **v66**, swiss.css **v2**, linear.css **v2**, app.js **v29**,
stats.js **v7**.
