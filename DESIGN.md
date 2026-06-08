# Design

> Визуальная система quiz-app (Editorial-редизайн). Единственный источник стилей —
> `modules/quiz-app/src/main/resources/static/css/editorial.css` (все правила под
> `html[data-design="editorial"]`). Темы через `data-theme` (dark — по умолчанию).
> Этот файл — спецификация; правки стилей идут в editorial.css, сюда — фиксация
> решений. Формат — Google Stitch DESIGN.md.

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

Per-surface контентные колонки (семантические, не magic-px), всё `margin: 0 auto`:

| Токен | px | Поверхность |
|---|---|---|
| max-width-focus | 760 | фокус-вопрос, error |
| max-width-reading | 820 | итоги сессии |
| max-width-result | 880 | разбор (вопрос + сайдбар) |
| max-width-settings | 860 | настройки |
| max-width-data | 1180 | аналитика (графики + таблица) |
| max-width-page | 1280 | базовый `.ed-page`, masthead |

На широких вьюпортах (1920/2560) контент не растягивается — кап + центрирование.
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

## Cache discipline

editorial.css → бамп `v=N` в `head.html` (2 строки: preload+stylesheet).
app.js → `v=N` в result/settings/focus-training (3). stats.js → stats.html (1).
Текущее: editorial.css **v61**, app.js **v27**, stats.js **v6**.
