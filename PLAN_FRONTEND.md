# PLAN_FRONTEND.md — план работ по frontend / UI / UX

Зафиксированный план бесконечного итеративного цикла улучшения интерфейса
quiz-app (тренажёр подготовки к собеседованию). Работаем как senior frontend +
product UI/UX + accessibility reviewer. **Хирург, а не экскаватор:** маленькие
безопасные порции, каждая с причиной, без хаотичного редизайна и слома
бизнес-логики.

Центр документа — **большая детальная таблица бэклога** (§4). Журнал сделанного —
`docs/ui-ux-improvement-log.md`. Память между тиками —
`project_audit_backlog_2026-06` (memory).

**Легенда статусов:**

| Статус | Значение |
|--------|----------|
| ✅ DONE | Сделано и верифицировано (ссылка на раунд/коммит) |
| 🔁 ONGOING | Постоянная проверка по кругу (не закрывается) |
| ⛔ DEFERRED | Ждёт решения пользователя — НЕ трогать без него |
| 🔒 GATED | Требует правки Java MAIN → recompile-wedge живого bootRun; отдельный цикл без live |
| 🚫 WONTFIX | Осознанное решение пользователя — НЕ менять |
| 🌱 BACKLOG | Открыто, безопасно брать worst-first |
| 🏛 CONTENT | Контент-сторона (seed/mcq), владеет внешний auto-improve — фронту не трогать |

**Легенда оценок:** Impact / Risk / Effort / Confidence = H(igh) / M(edium) / L(ow).
Берём сначала **Impact=H, Risk=L/M, Confidence=H**.

---

## 1. Стек и жёсткие ограничения

- **Стек:** Spring Boot + **Thymeleaf** + **vanilla CSS/JS**. НЕ npm/React →
  `npm lint/typecheck/test/build`, Storybook, Cypress, Playwright-CI = **N/A**.
  Проверки = Gradle-static (когда уместно) + живой `chrome-devtools` (CSSOM/DOM).
- **CSS:** `tokens.css` (токены 10 дизайнов) + `base.css` (структура).
  `editorial.css` — мёртвый код. Бамп `?v=N`: при правке CSS — обе ссылки в
  `fragments/head.html`; при правке JS — `app.js ?v=N` в `result.html` /
  `settings.html` / `focus-training.html`, `stats.js` — в `stats.html`. Чистая
  правка текста шаблона бампа НЕ требует (сервер рендерит свежим).
- **Дизайн-система:** 10 переключаемых дизайнов (editorial=дефолт, linear, swiss,
  notion, mintlify, broadsheet, superhuman, stripe, claude, theverge) × 2 темы.
  Персонализация — `data-*` на `<html>`; дефолт = отсутствие атрибута.
- **Живой прогон:** правки `static/`/`templates/` → `cp` в `build/resources/main/…`
  (LiveReload). Не запускать gradle build при живом bootRun (wedge). Postgres
  `quiz-postgres` на хост-порту **5433**.

### Запрещено
Менять бизнес-логику / API ради фронта · трогать MCQ JSON-сидеры `seed/mcq/**`
(внешний auto-improve) · mermaid · новые зависимости · контролы в минималистичный
focus-режим · деструктив в живом app (ответ/старт/финиш сессии = SRS-загрязнение,
проверять статически) · выключать Happ VPN · push (юзер пушит сам) · `git add -A`
(только свои pathspec).

---

## 2. Карта экранов

| # | Экран | URL | Шаблон | Состояния держать в голове |
|---|-------|-----|--------|----------------------------|
| 1 | Главная / вопрос | `/` | `focus-training.html` (`body.focus-page`) | flashcard, MCQ-выбор, выбран-до-проверки, проверен-верно, проверен-неверно, разбор; no-JS фоллбэк `result.html` |
| 2 | Фокус-тренировка | `/training?topic=…` | `focus-training.html` | те же + empty (нет вопросов по фильтру) |
| 3 | Настройки | `/settings` | `settings.html` | вкладки Сессия/Оформление/Данные; danger-zone (сброс банка) |
| 4 | Аналитика | `/stats` | `stats.html` + `stats.js` (Chart.js) | таблица тем, графики, поиск/фильтры, empty (нет данных/нет совпадений) |
| 5 | Ошибка 404 | несуществующий путь | `error.html` | — |
| — | Итоги сессии | `/session-summary` | — | одноразовая; только через EXAM/MARATHON/STUDY |

---

## 3. Методология приоритизации

4 оси: **Impact / Risk / Effort / Confidence** (H/M/L). Берём high-impact +
low/medium-risk + high-confidence. Каждое изменение обязано иметь причину:
читаемость / сценарий / консистентность / a11y / responsive / меньше дублирования /
ближе к дизайн-системе / понятнее состояние / меньше когнитивной нагрузки.

Порядок приоритетов: (1) UX-баги → (2) a11y → (3) responsive/overflow →
(4) loading/error/empty → (5) формы/валидация → (6) консистентность
кнопок/полей/typography → (7) дубли компонентов → (8) декомпозиция → (9) визуал →
(10) микрокопирайтинг.

---

## 4. БЭКЛОГ (большая детальная таблица)

### 4.1 Честность вариантов ответа (answer-option fairness) — продуктовое ядро

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| AF-1 | Правильный вариант угадывается по длине/наполненности | Это контент-перекос; UI обязан не усиливать. Уровень контента — `seed/mcq` | H | — | — | H | 🏛 CONTENT (auto-improve: pedago single-delta паритет) |
| AF-2 | Selected до проверки мог читаться как correct | Нейтральный selected (ring/border, без green/red), семантика только после «Проверить» | H | L | L | H | ✅ DONE (нейтральный ring-state; verify по кругу) |
| AF-3 | Цвет correct/wrong до проверки | Семантический цвет показывать ТОЛЬКО после submit | H | L | L | H | ✅ DONE 🔁 ONGOING |
| AF-4 | Длинный вариант визуально доминирует в 2-col grid | Adaptive layout: single-column когда max/min длины вариантов > порога; grid только для близких по длине | M | M | M | M | 🌱 BACKLOG (editorial сейчас 2×2 ring-grid; adaptive не внедрён) |
| AF-5 | Inline-code в варианте = визуальная подсказка correct | Мягкая, одинаковая для всех подсветка `code`; не ярче окружения | M | L | L | M | 🌱 BACKLOG (проверить контраст code-фона в вариантах) |
| AF-6 | Correct после неверного выбора не выделен отдельно | Явный «правильный был: …» блок, не сливать с разбором | M | L | L | M | 🌱 BACKLOG (verify текущего после-проверочного рендера) |
| AF-7 | Невыбранные wrong-варианты «кричат» после проверки | Приглушить unselected wrong, акцент на selected-wrong + correct | M | L | L | M | 🌱 BACKLOG |
| AF-8 | Маркеры-иконки внутри вариантов до проверки | НЕ добавлять иконки в options до проверки (риск подсказки) | — | — | — | H | 🚫 WONTFIX (правило, а не задача) |

### 4.2 Экран тренировки / «Фокус»

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| TR-1 | Header конкурирует с вопросом | Компактный sticky header во время тренировки | M | M | M | M | 🌱 BACKLOG (masthead уже PE-скрыт без JS; проверить высоту) |
| TR-2 | Empty-state «нет вопросов» был противоречив | Нейтральный заголовок «Сейчас нет вопросов» + recovery-ссылки | M | L | L | H | ✅ DONE (раунд 15, `3bb19db0`) |
| TR-3 | Hotkeys-подсказка мелкая/слабая | Усилить контраст/иерархию keyboard-hint | L | L | L | M | 🌱 BACKLOG |
| TR-4 | «Проверить ответ» далеко от выбора при длинных вариантах | Sticky/видимый CTA если контент длинный | M | M | M | M | 🌱 BACKLOG |
| TR-5 | Нет явной подсказки «что будет после проверки» | Микрокопия «Сначала итог, затем разбор» (уже есть строка) | L | L | L | M | ✅ DONE (строка под кнопкой) |
| TR-6 | Прогресс «1 день / 9887 к повтору» пугающе-непонятен | Человекочитаемый учебный статус; пояснить «к повтору» | M | M | M | M | 🌱 BACKLOG (требует аккуратной микрокопии, не Java) |
| TR-7 | keyboard-help-оверлей вешался на всех страницах | Гейт по `.focus-question` | M | L | L | H | ✅ DONE (`0a5a5cc6`) |
| TR-8 | `.focus-question` 48px «съедает» область | Намеренный размер | — | — | — | H | 🚫 WONTFIX (решение юзера) |
| TR-9 | Post-answer разбор тяжело сканируется | Verdict → уверенность → кнопка → sink доп.анализа (структурировано) | M | M | M | M | ✅ DONE (post-answer JS, см. память) 🔁 ONGOING |

### 4.3 Типографика

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| TY-1 | Русские UPPERCASE labels с большим letter-spacing плохо читаются | Сократить tracking на рус. uppercase | M | L | L | M | 🌱 BACKLOG (проверить eyebrow-kicker/labels) |
| TY-2 | Inline-code слишком заметен в прозе | Менее контрастный, но читаемый фон code | L | L | L | M | 🌱 BACKLOG |
| TY-3 | Ширина строки длинных объяснений | Cap `--measure` для разбора | M | M | L | M | 🚫 WONTFIX для prose full-width (решение юзера); ✅ для measure в разборе |
| TY-4 | body 18px/1.7 | Намеренно | — | — | — | H | 🚫 WONTFIX |
| TY-5 | Мелкий/слабый helper-text | Контраст helper ≥ WCAG | M | L | L | M | 🌱 BACKLOG (точечно, не глобально) |

### 4.4 Layout и сетка

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| LO-1 | Контейнированные сцены вместо full-bleed | Стабильный page-container с max-width | M | M | M | H | ✅ DONE (редизайн-волна) |
| LO-2 | Consistent spacing scale 4/8/12/16/24/32/48 | Токены spacing | M | L | M | H | ✅ DONE (space-* токены) |
| LO-3 | Layout modes (Поток/Два пейна/Сетка) | `data-layout` + объяснение в настройках | M | M | M | M | ✅ DONE (переключаемые раскладки) |
| LO-4 | today-hero рендерился голым на /settings | page-scoped `.card` surface | M | L | L | H | ✅ DONE (раунд 6, `f2a285a6`) |
| LO-5 | Много воздуха при узкой рабочей зоне (wide) | prose full-width | — | — | — | H | 🚫 WONTFIX (решение юзера) |
| LO-6 | «акцент-линейка» border-left 3px | A/B impeccable-ban vs editorial-идентичность | — | — | — | — | ⛔ DEFERRED (решение юзера; impeccable банит side-stripe) |

### 4.5 Аналитика (/stats)

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| AN-1 | Метрики технические, не actionable | Top-insights «что делать дальше» (слабая тема/повтор ошибок) | H | M | M | M | 🌱 BACKLOG (бэкенд даёт данные? проверить model — без новых API) |
| AN-2 | «К повтору 9887 из 9888» пугает | Пояснить семантику «к повтору» микрокопией | M | L | L | M | 🌱 BACKLOG |
| AN-3 | Поиск отделён от фильтров странной линией | Объединить поиск+фильтры в одну панель | M | M | M | M | 🌱 BACKLOG |
| AN-4 | Таблица тем требует горизонтальный scroll | overflow-x:auto + клавиатурный скролл-регион | M | L | L | H | ✅ DONE (раунд 11 tabindex/role/aria-label; mobile «ПР…» = by-design) |
| AN-5 | Графики мелкие, decorative grid, слабые labels | Меньше сетки, крупнее labels, tooltips | M | M | M | M | 🌱 BACKLOG (Chart.js config, не Java) |
| AN-6 | Сортировка не видна в покое | Glyph ⇅ на sortable[aria-sort=none] | M | L | L | H | ✅ DONE (раунд 6) |
| AN-7 | Empty-state поиска обещал сброс, контрола не было | Ссылка «сбрось поиск и фильтры» → `/stats` | M | L | L | H | ✅ DONE (раунд 15, `3bb19db0`) |
| AN-8 | Empty-state нулевых данных по теме | «По теме ещё нет ответов — начни тренировку» | M | L | L | M | 🌱 BACKLOG |
| AN-9 | Print /stats печатал 12 из 319 тем | Разворот `.is-collapsed` в @media print | M | L | L | H | ✅ DONE (`06985234`) |
| AN-10 | CTA из аналитики (тренировать слабые/повторить ошибки) | Кнопки-переходы | M | M | M | L | ⛔ DEFERRED (может требовать роутов/параметров — проверить контракт) |
| AN-11 | accuracy 58/122/32.2% объяснимость | Tooltip/пояснение расчёта | L | L | L | M | 🌱 BACKLOG |

### 4.6 Настройки (/settings)

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| SE-1 | 4-карточная стопка тяжёлая | ARIA-вкладки Сессия/Оформление/Данные | H | M | M | H | ✅ DONE (`0eb762a5`) |
| SE-2 | Кнопка фильтров «Применить» неинформативна | «Применить фильтры» (паритет /stats) | L | L | L | H | ✅ DONE (раунд 15, `3bb19db0`) |
| SE-3 | Design presets непонятны, нет preview | Краткое описание/preview пресета при выборе | M | M | M | M | 🌱 BACKLOG (segmented-control есть; добавить hint-описание) |
| SE-4 | «Сбросить банк вариантов» слабо отделён | Danger-zone + confirm modal + объяснение последствий | H | M | M | M | 🌱 BACKLOG (confirm уже есть; вынести в Danger-zone визуально) |
| SE-5 | Segmented-controls похожи на tabs | Развести визуально tabs vs seg-control | M | M | M | M | 🌱 BACKLOG |
| SE-6 | Размер шрифта: показывать %/reset | font-stepper с % и reset | M | L | M | M | 🌱 BACKLOG (stepper есть; проверить % и reset) |
| SE-7 | Не очевидно, сохраняются ли настройки | Feedback «Сохранено/Применено» | M | L | L | M | 🌱 BACKLOG |
| SE-8 | aria-describedby на контролы настроек | Связать подсказки со скрин-ридером | M | L | L | H | ✅ DONE (раунд 14, `6dc4c3cd`) |
| SE-9 | Раздел Accessibility в настройках | Сгруппировать a11y-оси (движение/контраст) | M | M | M | L | 🌱 BACKLOG |
| SE-10 | admin reset через `window.prompt` | Доступная модалка вместо prompt | M | L | M | H | ✅ DONE (`653c706e`) |
| SE-11 | focus-question «окна вопросов» разной высоты | min-height для одинаковых окон | M | L | L | M | ✅ DONE (`0eb762a5`) |

### 4.7 Цвета / темы / токены

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| CO-1 | accent конфликтует с success, red с error | Развести semantic-токены (accent/selected/success/error/...) | H | M | M | M | 🌱 BACKLOG (частично через нейтральный selected AF-2/3) |
| CO-2 | Слабые границы/текст в dark | Усилить border-primary в dark | M | L | L | H | ✅ DONE (раунд 4 linear-dark .12; раунд 9 dark-elevation clean) |
| CO-3 | WCAG-контраст по всем темам | AA-гейт `scripts/design-token-audit.py` | H | M | M | H | ✅ DONE (CLEAN) 🔁 ONGOING |
| CO-4 | Состояние не только цветом | Иконка/текст дублируют цвет verdict | H | L | L | H | ✅ DONE (verdict текст+цвет) 🔁 ONGOING |
| CO-5 | prefers-contrast / forced-colors / reduced-transparency | OS-prefs media-блоки | M | L | M | H | ✅ DONE (`77882943`, раунд 12 `b21acbea`) |
| CO-6 | light-тема отдельно (не только dark) | Проверять обе темы в каждом раунде | M | L | L | H | 🔁 ONGOING (скрин-набор обе темы) |

### 4.8 Компоненты (audit состояний)

| ID | Компонент | Что проверять (default/hover/focus-visible/active/selected/disabled/loading/error) | Статус |
|----|-----------|-----------------------------------------------------------------------------------|--------|
| CM-1 | Button / IconButton | press `:active` feedback, focus-ring | ✅ DONE (раунд 1/4 :active добавлен) |
| CM-2 | Tabs vs SegmentedControl | развести визуально (см. SE-5) | 🌱 BACKLOG |
| CM-3 | Select / Checkbox | кастомная тематизация, autofill | ✅ DONE (раунд 13 autofill) |
| CM-4 | AnswerOption | см. §4.1 (нейтральный/correct/wrong/long/code/parity) | 🔁 ONGOING |
| CM-5 | ExplanationPanel | verdict/correct/wrong-collapsible/related/CTA | 🌱 BACKLOG (структура есть, улучшать) |
| CM-6 | StatsCard / ProgressBar | actionable (см. AN-1) | 🌱 BACKLOG |
| CM-7 | FilterPanel | объединить с поиском (AN-3) | 🌱 BACKLOG |
| CM-8 | ChartCard | labels/tooltips/empty (AN-5) | 🌱 BACKLOG |
| CM-9 | DangerZone | вынести reset (SE-4) | 🌱 BACKLOG |
| CM-10 | Empty/Loading/Error states | человечные тексты + recovery | 🌱 BACKLOG (раунд 15 закрыл 2 empty) |
| CM-11 | back-to-top | уважает data-motion | ✅ DONE (`af765fd0`) |
| CM-12 | Модалки (kbd-help/prompt) | focus-trap, inert-фон, overscroll-contain | ✅ DONE (раунд 5/11) |

### 4.9 Accessibility

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| A11Y-1 | radio-group семантика вариантов | корректные роли/aria-checked | H | M | M | M | 🌱 BACKLOG (verify текущей разметки options) |
| A11Y-2 | aria-live для результата проверки | politeness по kind (success=polite/error=assertive) | M | L | L | H | ✅ DONE (раунд 7) |
| A11Y-3 | focus-visible везде | видимый ring, forced-colors fallback | M | L | L | H | ✅ DONE (раунд 12 forced-colors) |
| A11Y-4 | focus-return после async submit | вернуть фокус если ушёл на body | M | L | L | H | ✅ DONE (раунд 11) |
| A11Y-5 | table headers / summary-table семантика | ARIA-роли + scope | M | L | L | H | ✅ DONE (`8d1a52ab`) |
| A11Y-6 | screen-reader labels для icon-buttons | aria-label на смысловые иконки, aria-hidden на декор | M | L | L | H | ✅ DONE (icon-система раунд 9) |
| A11Y-7 | reduced-motion | `@media` + data-motion | M | L | L | H | ✅ DONE |
| A11Y-8 | touch target ≥44px | проверить мелкие контролы | M | L | L | M | 🌱 BACKLOG (раунд 11 проверял; точечно) |
| A11Y-9 | читаемость при 125/150% font-scale | проверить отсутствие обрезки | M | L | L | M | ✅ DONE (раунд 11 text-zoom-200 clean) |
| A11Y-10 | confirm modal для destructive | подтверждение сброса | M | L | M | M | 🌱 BACKLOG (confirm есть; усилить — SE-4) |

### 4.10 Responsive (320/375/768/1024/1440/1920)

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| RE-1 | mobile: 2-col answer grid ломает чтение | single-column на mobile | M | M | M | M | 🌱 BACKLOG (связано с AF-4) |
| RE-2 | таблицы mobile-friendly | overflow + keyboard-scroll | M | L | L | H | ✅ DONE (раунд 11) |
| RE-3 | settings одной колонкой на mobile | проверить вкладки на 375 | M | L | L | M | ✅ DONE (скрин 7-settings-mobile) 🔁 ONGOING |
| RE-4 | tablet midwidth split collapse | collapse 600→859px | M | L | L | H | ✅ DONE (раунд 4) |
| RE-5 | overflow длинных токенов в related-rail | overflow-wrap break-word | M | L | L | H | ✅ DONE (раунд 10) |
| RE-6 | landscape mobile | проверка | L | L | L | M | ✅ DONE (раунд 14 clean) |

### 4.11 Performance / ощущение скорости

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| PE-1 | layout shift при выборе ответа | избегать reflow (MCQ box-shadow в transition) | M | L | L | H | ✅ DONE (раунд 1 transition fix) |
| PE-2 | Chart.js render-blocking | `defer` | M | L | L | H | ✅ DONE (раунд 14) |
| PE-3 | font loading | media=print/onload + noscript | — | — | — | H | 🚫 WONTFIX (намеренно, откомментировано) |
| PE-4 | render-blocking preload no-op | удалить лишние `<link rel=preload>` | L | L | L | H | ✅ DONE (раунд 14) |
| PE-5 | CSP sourcemap chart.js | self-host vs CDN | — | — | — | — | ⛔ DEFERRED (download-gated + архитектурный выбор) |

### 4.12 Content/UI quality tools (dev/audit)

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| CN-1 | Option-parity warning (correct/avg-wrong > 1.3; max/min > 1.5) | dev-only индикатор подозрительных MCQ | M | M | M | L | 🏛 CONTENT (внешний pedago-loop уже чинит паритет; UI-warning — отдельный вопрос юзеру) |
| CN-2 | Раздел качества контента в analytics | «подозрительные вопросы» | L | M | M | L | ⛔ DEFERRED (пересечение с auto-improve) |

### 4.13 Иконки / emoji / визуальное обогащение

| ID | Проблема | Улучшение | Imp | Risk | Eff | Conf | Статус |
|----|----------|-----------|-----|------|-----|------|--------|
| IC-1 | Единый icon-layer | Монохромный Lucide SVG-спрайт + `.ed-icon` + JS `icon()` | M | L | M | H | ✅ DONE (icon-система, см. память) |
| IC-2 | aria-hidden на декор, aria-label на смысл | По правилам icon-системы | M | L | L | H | ✅ DONE |
| IC-3 | Emoji дозированно | 💡-подсказка + 🎲🤔💪-уверенность оставлены намеренно | — | — | — | H | 🚫 WONTFIX (намеренные эмодзи; остальной UI — SVG) |
| IC-4 | Иконки в danger-zone/empty/insights | warning-иконка у деструктива; иконки сканирования метрик | L | L | L | M | 🌱 BACKLOG (точечно, без шума, не в options до проверки) |

---

## 5. Не трогать — осознанные решения пользователя (🚫 / ⛔)

- prose `--measure` full-width (259ch на 2560 — осознанный выбор) · `.focus-question`
  48px · `body` 18px/1.7 · stats cold-start вид.
- `role=rowheader` на summary `<td>` (share-text JS читает `querySelectorAll('td')`).
- 💡-подсказка и 🎲🤔💪-уверенность (намеренные эмодзи).
- font-loading media-flip (намеренно, откомментировано).
- «акцент-линейка» `border-left 3px` — ⛔ DEFERRED (A/B решение юзера; impeccable
  банит side-stripe).
- favorite-флоу, CTA-из-аналитики, self-host chart.js — ⛔ DEFERRED (decision/
  download/recompile-gated).

---

## 6. Цикл одной итерации

1. Выбрать одну строку бэклога (§4) worst-first (Impact↑/Risk↓/Conf↑).
2. Понять UX и код (graphify-first для навигации).
3. Сформулировать правку + оценка по 4 осям.
4. Минимальная source-правка.
5. Live-sync: `cp` source → `build/resources/main/…`.
6. Live-verify chrome-devtools (CSSOM/DOM), оба видимых состояния.
7. При видимой дельте — пересобрать полный скрин-набор (§7), старый удалить,
   **каждый скрин верифицировать содержимым** (race-bug, см. §7).
8. Запись в `docs/ui-ux-improvement-log.md` + статус строки в §4 → ✅.
9. Commit source-only пер-pathspec (без push).
10. Обновить память `project_audit_backlog_2026-06`. Следующая строка.

---

## 7. Скриншот-рутина (после каждого цикла с видимой дельтой)

`docs/ui-critique/screenshots/` (gitignored). **Старый набор удаляется
(`rm -f *.png`), новый перезаписывается.** Набор: 5 страниц × 2 темы desktop
(1440×900, fullPage) + 4 ключевых mobile (375×812×2, light) = **14 файлов**:
`1-home-{light,dark}`, `2-training-{light,dark}`, `3-settings-{light,dark}`,
`4-stats-{light,dark}`, `5-error-{light,dark}`, `6-home-mobile-light`,
`7-settings-mobile-light`, `8-stats-mobile-light`, `9-training-mobile-light`.

**Ловушки:**
- Dev-браузер копит персонализацию в localStorage (ключ `design` уводит дизайн с
  дефолта) — перед каждым скрином чистить ключи + форсить `data-design=editorial`.
- **Race/stale-кадр (важно!):** первый `take_screenshot` после `navigate`/`new_page`
  может схватить кадр ПРЕДЫДУЩЕЙ страницы (компоновщик не успел). Признак —
  **байт-идентичные размеры скринов разных страниц** (`md5 | sort | uniq -d`).
  Лечение: перед КАЖДЫМ скрином `evaluate_script` проверяет, что DOM = ожидаемая
  страница (title / отличительный элемент: home → `body.focus-page`+`.focus-question`,
  settings → `[role=tablist]`+`#filters-form`), и только потом снимать; при
  расхождении — `new_page` + повтор.
- Тему ставить через `setAttribute('data-theme',…)` после навигации (reload
  сбрасывает).
- Компоновщик зависает на повторных захватах → свежая вкладка `new_page`.
- Question/result/summary с вариантами не заскринить без SRS-загрязнения —
  training-empty + home-question покрывают активное и пустое состояния.
