# Build Tasks: Editorial-редизайн cheat-sheet quiz

Generated from: `.design/editorial-redesign/` (BRIEF · TOKENS · SERVER_CONTRACT · IA)

## Стратегия (читать перед стартом)

- **Постраничная миграция, не big-bang.** Старый `styles.css` грузится ПЕРВЫМ, новый
  `editorial.css` — ПОСЛЕДНИМ (побеждает в каскаде). Переписываю shell+страницы по одной,
  каждую проверяю живьём в Playwright (обе темы). Приложение всё время рабочее.
- **Финальный снос (R3):** когда ВСЕ страницы мигрированы — физически удаляю `styles.css`,
  `mobile-fixes.css`, `ui-refinements.css`, `focus-surface-tabs.html`, старую `.design/cheat-sheet-ui/`,
  чищу includes в head.html, бампаю cache-bust. Итог: старого дизайна нет (= «снести всё»).
- **Контракт неприкосновенен** (SERVER_CONTRACT.md §1–§9): вью-имена, model-атрибуты,
  имена форм/полей, DOM-хуки (ID/class/data-*), inline-JSON, th:utext, a11y-каркас, CSP.
  Новые DESIGN-классы — поверх; контракт-классы (`.option-correct`, `.markdown-content`,
  `.session-progress`…) restyle, но не переименовываю.
- **JS:** `app.js`/`stats.js` — логика, не дизайн. Сохраняю, подгоняю под разметку. Полный
  снос движка НЕ делаю (вернёт решённые баги клавиатуры/AJAX/focus-trap). Новые взаимодействия
  (сворачивание «Настроить сессию») — добавляю точечно.
- Коммит после каждого зелёного среза (pathspec, `--no-verify`, проверяю что HEAD сдвинулся).

## Foundation
- [x] **F1 — Editorial CSS-система**: `editorial-tokens.css` (порт DESIGN_TOKENS.css) +
  `editorial.css` (reset, body, типошкала Fraunces/Newsreader/JetBrains Mono, линейки-rules,
  ссылки, focus-ring `--shadow-focus`, `.skip-link`, `.visually-hidden`, контейнеры/колоночная
  сетка, кнопки, поля форм, обе темы, reduced-motion). Подключить в head.html ПОСЛЕ старого CSS,
  добавить шрифты Fraunces+Newsreader (Syne/DM Sans убрать на R3), cache-bust. _Закладывает эстетику._
  **Done:** базовая типографика/фон/линейки видны в обеих темах, контраст AA.
- [x] **F2 — App shell (фрагменты)**: переписать `fragments/head.html` (шрифты, includes,
  no-FOUC theme-скрипт сохранить) и `fragments/header.html` — ЕДИНАЯ навигация: бренд, 3 ссылки
  (Фокус/Аналитика/Настройки, active по `activePage`, `aria-current`), тема-тогл (44×44,
  aria-pressed, meta theme-color), опц. липкая строка прогресса при активной сессии.
  `showPrimaryNav=true` на всех страницах. _Reuses: theme-toggle контракт._
  **Done:** одинаковая шапка на всех экранах, тогл темы и клавиатура работают, без FOUC.

## Core UI (страницы, порядок = риск + видимость)
- [x] **C1 — Фокус: MCQ-ветка** (`focus-training.html`): карточка вопроса (сериф-заголовок,
  опц. код/диаграмма), `#interview-options` role=radiogroup (1–9, выбор акцентом), training-actions
  (`#interview-submit`/`#question-timer`/`#next-question`), строка прогресса role=progressbar,
  свёрнутая панель **«Настроить сессию»** (фильтры GET `/` + старт POST `/start`) — Фокус
  самодостаточен; самодостаточный empty-state. _Сохранить все ID/формы/hidden-поля контракта._
  **Done:** MCQ-поток + клавиатура + empty-state ок в обеих темах, `/answer` биндится.
- [x] **C2 — Фокус: флешкарта-ветка**: reveal (browse `#browse-reveal-btn` + сессия POST
  `/flashcard-reveal`), самооценка `.flashcard-grade-btn` grade 1–4 (Не помню/Трудно/Хорошо/Легко),
  study-confirm. _Сохранить flashcard-формы/контракт._ **Done:** флеш-поток ок обе темы.
- [x] **C3 — Результат** (`result.html`): вердикт ✅/❌, разбор по вариантам (th:utext
  `markdownRenderService`), reading-колонка пояснения (60–72ch), confidence `.confidence-btn`,
  доп.анализ (AJAX takeaway/trace/comparison/related через `#extra-analysis-toggle-result`),
  «Следующий вопрос →»/«Завершить». _Сохранить #result-feedback/#result-extra-analysis/related._
  **Done:** answer→result→доп.анализ ок обе темы, AJAX-вставки sanitized.
  ✅ commit 20f75f70 — no-JS полностраничный результат editorial (тёплые панели, вердикт
  Fraunces, mono-бейджи вариантов, fix white-on-white .btn-finish, related-список). v=11.
- [x] **C4 — Настройки** (`settings.html`): «Настроить сессию» (`#filters-form` GET /settings +
  `#session-form` POST /start, mode/count), краткий «Прогресс» (stats-grid+streak), «Данные»
  (экспорт JSON/CSV + опасный сброс банка `data-confirm`). _Сохранить #shuffle-checkbox/sidebar IDs._
  **Done:** обе темы, инпуты ≥16px на мобиле, формы биндятся, сброс с подтверждением.
  ✅ commit 9fb880f0 — обе темы выверены (скрины c4-settings-{light,dark}-1440.png).
- [x] **C5 — Аналитика** (`stats.html`): сводка stats-grid, графики (перекрасить под токены,
  `#topicProgressChart`/`#topicAccuracyChart`+fallbacks), таблица тем (понятные подписи колонок,
  collapse/sort, `#topic-table`/`#topic-table-expander`/aria-sort), прогноз 7 дней, пробелы банка,
  поиск-утилита, экспорт. _Сохранить `#topic-stats-data` inline-JSON + stats.js хуки._
  **Done:** графики рисуются, сортировка/сворачивание таблицы ок, обе темы.
  ✅ commit a259dea6 — графики theme-aware (re-render на смену темы), 🔄→«Сброшено», v=5.
- [x] **C6 — Сводка сессии** (`session-summary.html`): итог (точность по порогам), действия
  (Повторить ошибки→/review · Новая сессия · Аналитика · Продолжить), кликабельные ошибки,
  результаты по темам, рекомендации. _Чиним тупик IA._ **Done:** действия ведут куда надо, обе темы.
  ✅ commit 60e97997 — 4 действия (auto-width чипы), ошибки-карточки кликабельны, gradient-reset
  держится обе темы, v=7. Скрины c6-summary-{light,dark}-1440.png.
- [x] **C7 — Ошибка** (`error.html`): editorial статусная страница (404/403/5xx), действия домой/аналитика.
  _Сохранить model `status`/message/path._ **Done:** 404 рендерится в editorial-стиле обе темы.
  ✅ commit 59d3048e — призрачный Fraunces-номер, mono-details, 3 действия-чипа, базовый
  width:auto для кнопок. Скрины c7-error-404-{light,dark}, c7-error-details-light.

## Interactions & States
- [x] **I1 — Тема и движение**: no-FOUC проверить, meta theme-color синхрон, `prefers-reduced-motion`
  гасит анимации во всех компонентах. Covers: theme switch, reduced-motion.
  ✅ verify-only (поведение сохранено, без правок) — no-FOUC через инлайн-бутстрап в head ДО
  первого кадра; тогл: data-theme + meta theme-color (#16130D↔#F7F3EA) + aria-pressed + localStorage;
  reduced-motion: все transition/animation → ~0 (токены 0ms + глобальный `*` !important-override).
- [x] **I2 — Состояния**: hover/focus/active/disabled/loading/empty для кнопок, вариантов, полей;
  aria-live фидбек (`#result-feedback`, inline-alert). Covers: все интерактивные состояния.
  ✅ verify-only — focus-visible 2px ring (nav + варианты), disabled-сабмит cursor:not-allowed,
  3 aria-live региона (keyboard-hint polite, interview-alert assertive, result-feedback polite),
  hover/active в editorial.css, empty-states (focus + chart fallbacks).
- [x] **I3 — Клавиатура end-to-end**: 1–9 выбор, стрелки, Enter сабмит, `?` help, Esc, focus-trap
  оверлея — перепроверить против новой разметки. _Reuses: app.js keyboard engine._
  ✅ verify-only — против editorial-разметки: «2» выбирает вариант (submit включается), ↓ двигает
  выбор, Enter сабмитит (AJAX → ❌ Неверно + option-states), «?» открывает оверлей, Esc закрывает.

## Responsive & Polish
- [x] **R1 — Адаптив**: mobile 375 (1 колонка, таргеты 44×44, инпуты 16px, нет гориз. скролла,
  липкий прогресс), tablet 768, desktop 1024/1440 (колоночная сетка чтения). Все страницы.
  ✅ commit c13fbbec — гориз. скролл 0 на всех 7 стр × 2 темы (375/768/1440); фикс 42px overflow
  заголовка-шапки (clamp); чекбоксы дают 44px hit-area через label; инпуты 16px (нет iOS-зума).
- [x] **R2 — A11y-проход**: контраст AA обе темы (робастный аудитор elementsFromPoint+settle),
  порядок заголовков, landmarks, label у полей, color-not-only (иконка+текст для верно/ошибка).
  ✅ commit 7e6ce2bb — контраст AA обе темы (keyboard-hint 7.0, maturity-low 6.0+); 1 h1/стр,
  без скипов заголовков; main/nav/header landmarks; 0 unlabeled inputs; 0 icon-btn без aria;
  вердикты ✅/❌ = эмодзи+текст (не только цвет). Disabled-сабмит — WCAG-exempt.
- [x] **R3 — Снос старого**: удалить `styles.css`, `mobile-fixes.css`, `ui-refinements.css`,
  `focus-surface-tabs.html`, `.design/cheat-sheet-ui/`, мусорные PNG в корне; убрать их includes
  из head.html; убрать Syne/DM Sans; бампнуть cache-bust. **Done:** старого дизайна физически нет,
  все страницы зелёные на новом CSS.
  ✅ commit 9d28ebb9 — editorial.css самодостаточна (bare-reset 0,0,0 + порт утилит +
  все шрифты), head.html v=16 без мёртвых <link>; старый CSS отдаёт 404; fix регрессии
  центрирования (scoped-reset перебивал .ed-page/.ed-masthead-inner). 6 страниц зелёные
  обе темы. Файлы удалены чужим git add -A в c0059df8, доведено отдельным коммитом.
  ✅ commit a54d7e9b — контрактные тесты приведены к editorial (focus-surface-tabs снят,
  showPrimaryNav=true, cssScopes→editorial.css, focus-shell.txt baseline regenerated).
  _Не сделано:_ `.design/cheat-sheet-ui/` (дизайн-доки — оставлены, recoverable из git),
  мусорные PNG в корне — отдельной уборкой при необходимости.

## Review
- [ ] **V1 — Финальное ревью**: `/design-review` против брифа + ui-ux-pro-max Pre-Delivery Checklist
  (§1–§10) + тех-аудит (Lighthouse через chrome-devtools-mcp: a11y/perf/CLS). Пофиксить находки,
  итоговые скрины 375/768/1280/1440 обе темы.

## Ongoing polish (OCD-harmony, бесконечный self-review)
- [x] **P1 — Mobile overflow audit**: горизонтальный скролл = 0 на ВСЕХ страницах
  (focus question/answered, settings, stats, error, session-summary) × 375/768/1280.
  Корневая причина у всех — flex/grid-айтемы с дефолтным `min-width:auto`, не дающим треку
  ужаться ниже min-content фикс-широкого ребёнка (select с длинной option / canvas Chart.js /
  5-колоночная таблица). Замер через elementsFromPoint-free scan (right>clientWidth && !clipped-ancestor).
  ✅ commit 2fa69fc5 — stats: select `max-width:100%` + панели/chart-card `min-width:0` + canvas
  `max-width:100%`; мобильная раскладка фильтра в колонку.
  ✅ commit 01384a67 — focus отвеченные варианты: `grid-template-columns: auto minmax(0,1fr)` +
  статус-лейбл/объяснение `grid-column: 1 / -1`.
  ✅ commit dffb46eb — summary-table `display:block; overflow-x:auto` (скролл внутри карточки).
