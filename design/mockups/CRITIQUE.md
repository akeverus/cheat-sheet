# CRITIQUE — адверсарный разбор макетов «Instrument» (Фаза 1)

> Свежий критический взгляд на 6 готовых макетов ПЕРЕД переносом во фронт (Фаза 2).
> Правим ТОЛЬКО макеты в `design/mockups/`. Каждая правка — с причиной (см. iterate-протокол §10).
> Статусы: TODO / IN_PROGRESS / DONE / RECHECK / REJECTED.

## Метод

Линзы: DESIGN.md (absolute-bans, стратегия цвета/типографики, мандат ширины) ·
Taste-дайлы (VARIANCE 6 / MOTION 4 / DENSITY 4) · impeccable detect · icon-system
(монохромный Lucide, НЕ эмодзи) · консистентность компонентов через все 6 экранов ·
a11y/клавиатура/reduced-motion. Слоп ищем не «на глаз красиво», а по нарушению
установленного языка и по кросс-экранной рассинхронизации.

## Бэклог (worst-first)

| id | приоритет | проблема | причина/линза | файлы | статус |
|---|---|---|---|---|---|
| C1 | P2 | `🔥` эмодзи в стрике | icon-system: язык монохромный Lucide, эмодзи выбивается; единственный сырой эмодзи в наборе | `shell.html` | **DONE** |
| C2 | P2 | Единственная шапка с `backdrop-filter: blur` + `color-mix`-полупрозрачностью | DESIGN.md банит дефолтный glassmorphism; плюс расходится с 5 сплошными шапками | `shell.html` | **DONE** |
| C3 | P2 | Sticky-шапка непоследовательна: `shell` sticky, остальные 5 — статичные | Хром обязан быть ОДНИМ компонентом через все экраны; sticky-нав = хорошее UX, применить единообразно (sticky + solid, без блюра) | все 6 | **DONE** |
| C4 | P3 | kbd-help модалка живёт только в `shell.html` | По контракту (§8) это глобальный оверлей экрана вопроса; при Фазе 2 привязать к `focus-question` | `shell.html`→`focus-question.html` | **DONE** |
| C5 | P4 | Входной ритм: kicker + headline на всех экранах | Оценено: ритм САМ ПО СЕБЕ ок (системный, не монотонный, VARIANCE-адекватно). Реальный слоп — 2 кикера-эхо (`stats`/`settings` = lowercase-копия H1) против DESIGN.md «кикер = функциональный тег». Сделаны функциональными | `stats.html`, `settings.html` | **DONE** |
| C6 | P4 | Подвал только на `shell` (домашний), у 5 задача-экранов его нет | **РЕШЕНО (осознанно):** подвал — только на hub-экране (home). Задача-экраны (вопрос/разбор/итоги/настройки/аналитика) намеренно БЕЗ подвала — фокус, ethos «инструмент не шумит»; кросс-нав уже в шапке на каждом экране, так что навигация не теряется. Не «чинить» | — | **РЕШЕНО** |
| C7 | P1 | skip-link `signal-on` на `--signal` = AA 4.40 < 4.5 (light) на 5 из 6 | Тот же слоп-класс, что и раньше в btn-primary (общий компонент), но в focus-only skip-link — пропустили; skip-link = видимый по фокусу текст (WCAG 2.4.1), обязан держать AA | 5 экранов (кроме shell) | **DONE** |
| C8 | — | focus-ring/reduced-motion/AA-каркас | Верифицированы по PROGRESS it.10–21; при каждой правке — re-check, не регресс | все | RECHECK |
| C10 | P4 | Type-scale: `.state-title` (empty/done, font-hero fs-xl) держит `--track-tight`, а 3 других serif-page-title того же размера (`settings/stats/summary`) — `--track-hero` | Кросс-экранная консистентность типографики: одна роль (serif page-title @fs-xl) = одна трактовка tracking. Сам scale КОГЕРЕНТЕН (2 tier: content-hero clamp vs page-title fixed) — дрейф только в одном letter-spacing | `focus-question.html` | **DONE** |
| C9 | P2 | Кнопочный компонент дрейфует на `shell`: 3 расхождения с остальными 5 экранами | Кросс-экранная консистентность: одна кнопка = один компонент. (1) `.btn` padding `s-5` vs `s-6` у 4 других; (2) `.btn-primary` без `box-shadow: shadow-sm` (у всех 4 — есть → плоский CTA на home); (3) `.btn-quiet` = байт-идентичен `.btn-ghost` у focus/result/summary, но result/summary держат `.btn-quiet` для ДРУГОЙ безрамочной третичной → коллизия имени (одно имя → два вида) | `shell.html` | **DONE** |
| C11 | — | `opt-mark` (галочка верного варианта): `success-on` на заливке `success` = AA 4.28 < 4.5 (light) | **РЕШЕНО (compliant):** галочка — **графический объект** (WCAG 1.4.11, порог 3:1), `aria-hidden`, избыточна с зелёной обводкой `.opt--correct` + тегом «верный ответ» + тинтом бейджа. 4.28 ≥ 3:1 → проходит. Единственная пара ниже строгого 4.5-бара (сестра `error-on/error` = 4.95). Общий токен `--success` НЕ риплю в recheck-тик (задел на 8 макетов). Если понадобится симметрия ≥4.5 — точечно затемнить `--success` fill в отдельном тике | `focus-question.html` (tokens) | **РЕШЕНО** |
| C12 | P3 | `focus-question` покрывает 4 состояния (active/result/empty/done), но registry-список focus-training шире | Reference-экран задал ЯЗЫК на 4 ядровых состояниях. **R0.12: добавлено состояние `flashcard` (изучение/study-LEARN)** — reveal→grade: вопрос → «Показать ответ» (disclosure, Пробел/click, aria-expanded/controls, фокус на ответ, aria-live) → ответ-проза (карточка 1px border, БЕЗ side-tab) + самооценка 1–4 (Снова/Трудно/Хорошо/Легко, role=group, крайние оценки семантический край на hover). Закрывает flashcard-reveal/grade(1-4) + study-LEARN. **R0.13: `alert` (inline-alert fragment)** — 3 варианта error/info/warn (`role=alert`/`role=status`, иконка + заголовок семантический-ink + текст --ink + dismiss `aria-label`, closable JS); закрывает inline-alert/error. **R0.14: `loading` (скелет)** — скелет повторяет раскладку вопроса (рейка/тема/2 строки вопроса/4 опции с badge+строками), блоки декоративны `aria-hidden`, регион `role=status`+`aria-live`+`aria-busy` + visually-hidden «Загрузка вопроса…», пульс off под reduced-motion; закрывает loading-placeholder. Остаются: generationUnavailable, no-js-fallback, diagram/mermaid (diagram отложен — Mermaid self-authorship запрещён §4). Порт focus-training BLOCKED параллельным WIP | `focus-question.html` | IN_PROGRESS |
| C13 | P3 | `result` покрывает correct/incorrect/analysis/favorite, но registry-список result шире | **R0.15: добавлен confidence-виджет** — самооценка после вердикта (вердикт→уверенность→кнопка, memory `project_post_answer_js`): 3 уровня 🎲 Угадал / 🤔 Не уверен / 💪 Уверен, `role=radiogroup` + roving tabindex + стрелки/Home/End (паттерн seg-control), эмодзи `aria-hidden` + текст-лейбл. Прод НАМЕРЕННО держит 🎲🤔💪 (memory `project_icon_system`) → при порте точка решения эмодзи-vs-монохром (отмечено в CSS). **R0.17: regenerate = МЁРТВЫЙ прод-UI** — кнопка гейтится `th:if="${aiEnabled}"`, а `aiEnabled` захардкожен `false` (MvcModelAttributeMapper:152, InterviewPageMvcService:166; AI вырезан 2026-07-07) → в проде никогда не рендерится; макет ПРАВИЛЬНО его не содержит. no-js-fallback (живой `/answer`) = parity-фазный контракт-концерн, не отдельный визуал макета. При ПОРТЕ: провести `aria-live` на вердикт (§8 `#result-feedback`); НЕ портировать btn-regenerate/regen-badge. Для Фазы G: dead-code кандидат (btn-regenerate + regen-badge + app.js regenerateQuestion + API.REGENERATE) | `result.html` | **DONE** |
| C14 | P3 | `settings` покрывает 3 вкладки/export/7 осей/filters/session/streak, но не reset-options-confirm | Danger-кнопка «Сбросить банк вариантов» есть, шага ПОДТВЕРЖДЕНИЯ (необратимое действие) в макете нет. **ФИКС (R0.10):** добавлена confirm-модалка `role=alertdialog` (aria-labelledby/describedby, focus-trap, Esc, возврат фокуса, дефолт-фокус на «Отмена» = безопасно, scrim-click close); кнопка Danger получила `aria-haspopup=dialog`. Устанавливает паттерн подтверждения деструктива для порта (`POST /settings/reset-options`) | `settings.html` | **DONE** |
| C15 | P2 | `stats`: амбер-заливки данных (`bar-due`, `bar-acc-mid`, `cp-due`, `fc-fill.is-today`) на треке `surface-2` = **2.06:1 < 3:1** (light) | **Реальный дефект** (не «граф. объект compliant», как C11): столбцы данных ДОЛЖНЫ отличаться от трека ≥3:1 (WCAG 1.4.11) — иначе амбер-бары «к повтору»/mid-точность не видны в светлой теме. Яркий `--spark` (L 0.720) слишком светлый на светлом треке. **ФИКС:** заливки данных → `--spark-ink` (L 0.500) = 5.02 light / 8.72 dark ≥3:1; легенда `.sw-due` синхронизирована. Правка ЛОКАЛЬНА в `stats.html` (`--spark` — декор-акцент, не тронут). learned↔due различаются ПО ТОНУ (teal↔амбер, CVD-safe blue-yellow ось) + числовой лейбл | `stats.html` | **DONE** |
| C16 | P3 | `stats` покрывает overview/charts+fallback/sortable-table/forecast/gaps, но registry-список шире | **R0.16: добавлен поиск по темам** — панель в секции «Детализация» (в проде GET /stats?q= + хоткей «/»): `role=search` форма + visually-hidden label + иконка + kbd-подсказка («/» фокус · Esc сброс); live-фильтр строк таблицы; счётчик «N из M тем» `role=status aria-live`; empty-строка «Ничего не нашлось» + скрытие таблицы. «/» не срабатывает из текстовых полей; Esc в инпуте сбрасывает (`stopPropagation` — не конфликтует с модалкой). empty/cold-start НЕ делаем — в проде осознанный вид (memory `project_design_elevation_round1`). Порт stats BLOCKED параллельным WIP | `stats.html` | **DONE** |
| C18 | P1 | Reflow 320px (WCAG 1.4.10): 2 дефекта, найденные ЖИВЫМ браузер-аудитом всех 7 макетов × все mock-состояния | (1) `.explain code` — неразрывный токен `@Transactional(REQUIRES_NEW)` (286px) распирал документ на 1px в `focus-question` «Разбор» и `result` (оба варианта) → `overflow-wrap: break-word`; (2) `settings` «Оформление» — `.set-grid` `minmax(20rem,1fr)` форсит колонку 320px → 35px overflow всех 7 карт → `minmax(min(20rem,100%),1fr)`. Верификация: 320 чисто по всем 7 макетам × состояния (вкл. flashcard-revealed, reset-confirm, mobile-menu, kbd-help, search-empty); 640 (zoom200@1280) чисто; detector exit 0 ×3. Скроллируемые исключения легитимны (pre.code-body, tablist overflow-x:auto) | `focus-question.html`, `result.html`, `settings.html` | **DONE** |
| C17 | P2 | `shell`: заливка стрика `.streak-fill` `--spark` на треке `surface-2` = **2.06:1 < 3:1** (light) | **Реальный дефект того же класса, что C15** (не «граф. объект compliant» как C11): полоса стрика показывает ДОЛЮ прогресса (`width:72%`) → графобъект (WCAG 1.4.11, 3:1), а яркий `--spark` (L 0.720) на светлом треке `surface-2` не виден в light. **ФИКС:** `.streak-fill` → `--spark-ink` (L 0.500) = 5.02 light / 8.72 dark ≥3:1. Локально в `shell.html` (`--spark` декор-акцент не тронут; `.streak-flame`/`.streak-count` уже `--spark-ink`). Найден при замыкании 7-поверхностного recheck (R0.11) | `shell.html` | **DONE** |

## Журнал критики

### cr.153 — WCAG 1.4.12 Text Spacing (verified-clean)

**Вопрос:** при пользовательском переопределении интервалов (line-height ≥1.5, para-spacing ≥2em, letter-spacing ≥0.12em, word-spacing ≥0.16em) — без потери контента/функций (без обрезки видимого текста, без перекрытия).

**Метод:** инъекция стандартного 1.4.12 override-CSS (`* {line-height:1.5; letter-spacing:0.12em; word-spacing:0.16em} p,li,h*{margin-bottom:2em}`) с `!important` + детекция clipping (overflow:hidden/clip контейнеры со scrollH>clientH, содержащие текст) + проверка конкретных fixed-height компонентов на overflow бокса + page-level overflow. После замера override снимается.

**Результат — чисто на /, /stats, /settings (`pageHorizontalOverflow: 0`):**
- Fixed-height компоненты вмещают увеличенные интервалы, `overflowsBox: false`: sticky-th (43px), stats-btn (44px), table-cell (53px), chart-fallback, settings-tab (51px), seg-btn (42px), font-value (24px), .btn (42px).
- Видимый текст нигде не обрезан и не перекрыт.

**Ложные срабатывания (exempt — намеренные visually-hidden SR-only, НЕ видимый контент):**
- /stats: `caption.visually-hidden` (clientH:1) — SR-only имя таблицы (clip-паттерн).
- /settings: `h2.settings-panel-title` (clientH:1) — под `.js-tabs`/`[data-js]` (base.css 1358/1373) visually-hidden (`width/height:1px; clip:rect(0 0 0 0)`); визуальное имя панели даёт вкладка, AT-имя — этот h2. Без JS (fallback) заголовок видимый (1353) и не клипается. AT читает полный текст независимо от 1px-clip → вне scope 1.4.12.

**Вывод — чисто.** Правок кода нет.

**Огранич.:** /result, /session-summary POST-gated — при text-spacing override не замерены этим проходом.


### cr.152 — WCAG 1.4.10 Reflow @320px (verified-clean)

**Вопрос:** при ширине 320px CSS (эквивалент 400% зума) контент без потери информации/функций и БЕЗ горизонтального скролла — кроме исключений (дата-таблицы, код, карты).

**Метод:** эмуляция вьюпорта 320px + замер `documentElement.scrollWidth − clientWidth` (page-level overflow) и поэлементный поиск «вылезающих» (right > viewport), исключая легитимные scroll-контейнеры (table-wrap/pre/overflow-x-auto).

**Результат — чисто на всех достижимых страницах/состояниях (`pageHorizontalOverflow: 0`, offenders: []):**
- **/** — overflow 0, скролла нет.
- **/stats** — overflow 0. Дата-таблица на мобиле НЕ горизонтально-скроллит, а переходит в **стек-раскладку** (`td[data-label]` → display:grid, псевдо-лейбл из R1.49) → рефлоу без 2D-скролла даже для таблицы (`.topic-table-wrap` overflowX=visible на 320px).
- **/settings** дефолтная вкладка (Сессия) — overflow 0.
- **/settings** вкладка «Оформление» (активирована client-side) — 7 seg-контролов, включая **design с 11 опциями**: `flex-wrap: wrap` → опции переносятся на несколько рядов (width 257 < vw 305, scrollWidth == clientWidth, worstOffender []). Ни один seg-контрол не вылезает и не скроллит свой бокс.

**Вывод — чисто.** Все компоненты рефлоуятся корректными техниками (таблица→стек, seg-контролы→flex-wrap); горизонтального скролла нет нигде. Правок кода нет.

**Огранич.:** /result и /session-summary POST-gated — на 320px не замерены (прежние аудиты overflow чисты на 375/768/1280, project_summary_page_test_flow); при 320px не подтверждены эмпирически этим проходом.


### cr.151 — WCAG 3.2.2 On Input (verified-clean)

**Вопрос:** изменение значения контрола не должно автоматически вызывать смену контекста (навигация/новое окно/перемещение фокуса/контент, меняющий смысл), если пользователь не предупреждён заранее.

**Метод:** инвентарь всех `change`-хендлеров (шаблоны+JS) + разбор, что каждый делает.

**Разбор change-хендлеров:**
- **Выбор варианта (options change, app.js 1155)** — ЕДИНСТВЕННЫЙ кандидат на смену контекста: при `instantMode` change radio → `form.submit()` → навигация /answer. НО `instantMode` **вечно false**: контролы режимов (instant/hard/review/adaptive/timer) удалены из шаблонов (`#instant-mode-toggle` нет ни в одном), ключ prefs бампнут v2→v3 (v3 никто не пишет), `onLearningPrefChange` недостижим (app.js:5–11 документирует «ловушку round-01 B7»). Ветка авто-сабмита — **мёртвый код, не исполняется**. Реальный сабмит только явной кнопкой.
- **mode-select (app.js 253/258)** — показ/скрытие поля count внутри формы; без навигации, без перемещения фокуса → не «смена контекста» (WCAG: контекст = окно/фокус/viewport/смысл-меняющий контент, не показ поля).
- **toggleTopic (shuffle/ordered, 212/213)** — enable/disable селекта тем; within-form.
- **seg-контролы тема/дизайн/раскладка/ширина/плотность/движение** — презентационная смена (флип `data-*` на `<html>` без перезагрузки, app.js:596); тема/оформление НЕ являются «контекстом» по WCAG; + смена анонсируется aria-live (`#personalization-status`).
- **count change (280)** — только persist значения.

**Итог — чисто.** Ни один change-хендлер не вызывает смену контекста; единственная теоретическая (instant-auto-submit) — недостижимый задокументированный мёртвый код. Правок кода нет.

**Наблюдение (не a11y-дефект):** мёртвая ветка instant-submit — код-гигиена, задокументирована как осознанный defensive-guard (app.js 5–11); удаление вне scope a11y-прохода, не трогаю.


### cr.150 — WCAG 3.3.1 Error Identification / 3.3.3 Error Suggestion (verified-clean)

**Вопрос:** когда ввод автоматически проверяется и ошибка обнаружена — она должна быть идентифицирована текстом (3.3.1, A) и предложено исправление, если известно (3.3.3, AA).

**Инвентарь форм с возможной ошибкой:**
- Единственное поле с ограничениями — `count` (кол-во вопросов сессии): `<input type=number min=1 max=200>` (без `required`, без кастом-валидации; JS-валидации в проекте НЕТ вообще — checkValidity/setCustomValidity/aria-invalid = 0).
- Фильтр-селекты (group/topic/ordered/mode), поиск (`type=search`), чекбоксы — невалидных состояний не имеют (все опции валидны, free-text без constraints).

**Путь обработки — нативная HTML5 Constraint Validation** (форма без `novalidate` → активна). Эмпирическая проверка через Constraint Validation API (read-only, без submit/мутаций, значение сброшено):
- `500` → `rangeOverflow`, message «Значение должно быть меньше или равно 200.» → идентификация ошибки + граница-подсказка.
- `0` → `rangeUnderflow`, «Значение должно быть больше или равно 1.».
- `15.5` → `stepMismatch`, «Введите допустимое значение. Ближайшие допустимые значения: 15 и 16.» → предлагает ближайшие валидные (образцовый 3.3.3).
- `20` → valid, message пусто.

**Вывод — чисто.** Сообщения локализованы (русский, совпадает с UI), идентифицируют ошибку (3.3.1) и предлагают исправление — границу диапазона или ближайшие валидные значения (3.3.3). Браузер блокирует submit и озвучивает сообщение через AT (нативная техника, принята WCAG). min/max также экспонируются AT как valuemin/valuemax до ошибки. Правок кода нет.


### cr.149 — WCAG 1.3.1 Info & Relationships: формы, группы, дата-таблица (verified-clean)

**Вопрос:** программные связи (метки контролов, групповые метки, header-ассоциации таблиц) должны быть определяемы кодом, не только визуально.

**Метод:** живой DOM-аудит на 3 достижимых страницах — каждый `input/select/textarea` на наличие программной метки (`label[for]`/wrap-label/`aria-label`/`aria-labelledby`); каждый `[role=radiogroup|group]`/`fieldset` на групповую метку; дата-таблица на `th[scope]`/caption/aria-sort.

**Результат — чисто везде, образцово:**
- **/settings**: 9 form-контролов — ВСЕ с меткой (`controlsUnlabelled:[]`), via native wrap-`<label>`; 7 групп (6 radiogroup + font-stepper group) — ВСЕ с групповой меткой via `aria-labelledby`.
- **/stats**: 5 контролов — все с меткой; **дата-таблица образцовая**: `<caption>`«Статистика по темам» (= имя таблицы), **325 th ВСЕ со `scope`** (значения col+row → у каждой из 319 строк первая ячейка = `th scope=row`), 5 сортируемых колонок ВСЕ с `aria-sort`.
- **/**: radiogroup `#interview-options` role=radiogroup + `aria-label`«Варианты ответа» + `aria-describedby`«options-flow-hint»; 4 радио одной name-группы «optionId», ВСЕ с меткой (wrap-label/aria-label).

**Не-проверено (POST-gated):** /result опции используют `role=list`/`role=listitem` (display-only результат — семантически корректно, подтверждено в шаблоне ранее cr-историей). Header/nav-семантика уже верифицирована (R1.41).

**Итог:** ни одного контрола/группы без программной метки; таблица — с caption+scope+aria-sort. Правок кода нет.


### cr.148 — WCAG 2.5.8 Target Size (Minimum, AA/2.2) (verified-clean)

**Вопрос:** интерактивные цели ≥24×24 CSS px, ЛИБО подпадают под исключение (spacing: 24px-круги по центрам не пересекаются ⇔ center-to-center ≥24px; inline: ссылка в потоке текста; essential; UA-контролируемая native-форма).

**Метод:** живой замер всех интерактивных целей (`button/a/input/select/[role=button|tab|radio|checkbox]/summary/[tabindex]`) на 3 достижимых страницах + для <24px проверка spacing-исключения (min center-to-center ко всем соседям) и inline-исключения. /result icon-кнопки — через CSS (POST-gated).

**Результат — чисто на всех страницах, `violations: []`:**
- **/settings** (23 цели): 4 «маленьких» = нативные чекбоксы/радио 18×18 → spacing pass (center-dist 52px) + UA-исключение.
- **/stats** (37): 14 «маленьких» = чекбоксы 18×18 (center-dist 91/137) + topic-ссылки высотой 17px (inline-in-table + center-dist 56–61px). Все exempt.
- **/** (13): 4 «маленьких» = визуально-скрытые радио-инпуты 1×1, но РЕАЛЬНАЯ цель — оборачивающий `<label>` 960×126/156px (огромный); MCQ-опции 960×126/156.
- **/result** (CSS): `.btn-favorite`/`.btn-regenerate` — `min-width/height: 44px` (2427) → ≥24 (даже ≥44/2.5.5). regen-badge — неинтерактивный span.

**Вывод:** ни один author-стилизованный интерактив (button/seg-btn/tab/nav-link/toggle/close/next) не оказался <24px — большинство ≥44px. Все «маленькие» элементы — либо нативные form-инпуты с большой ассоциированной label + spacing-исключение, либо inline-ссылки с inline+spacing исключениями. Правок кода нет.


### cr.147 — WCAG 4.1.3 Status Messages (verified-clean)

**Вопрос:** статус-сообщения, не получающие фокус (результаты AJAX-операций, ошибки, изменения контекста), должны быть программно определяемы через `role=status/alert/log` или `aria-live`, чтобы AT анонсировал их без перемещения фокуса.

**Метод:** grep всех live-регионов (шаблоны+JS) + сверка каждого JS-статус-writer'а с целевым регионом + живой DOM-скан /stats и /settings (регионы существуют, атрибуты корректны, нет статус-контейнеров без live-региона).

**Покрытие (все сценарии имеют live-регион):**
- **Export** (JSON/CSV) → `#export-status` role=status polite ✓ (live-скан подтвердил).
- **Copy code** успех/ошибка → copyStatus role=status polite (app.js 1966) ✓.
- **Table sort** /stats → `#table-sort-status` aria-live polite atomic (stats.js 270) ✓.
- **Settings saved** (7 персонализация-контролов) → `#personalization-status` role=status polite atomic; `announceSaved` (app.js 548, wireSegControl) ✓.
- **Font scale** → `#font-scale-value` role=status polite; announceFontScale ✓.
- **AJAX-ошибка** → `#interview-alert` (inline-alert role=alert assertive), app.js 83–90 ДИНАМИЧЕСКИ переключает role=status/polite ↔ role=alert/assertive по severity ✓ (изощрённо-корректно).
- **Answer result feedback** → `#result-feedback` role=status polite atomic ✓; confidence/extra-analysis → aria-live polite (app.js 1532, post-answer 22) ✓.
- **Chart fallback** (no-JS/no-data) → role=status polite (stats 154/165) ✓.
- **Filter mode hint** (смена select) → `#filter-mode-hint` role=status polite ✓.

**Не-gap'ы:**
- Формы /stats (фильтр+поиск) — `method=get action=/stats` → полный reload; результаты на перезагруженной странице (навигация, НЕ status-message) → вне scope 4.1.3.
- Streak-виджет (`/api/streak`) намеренно БЕЗ aria-live (документировано C29 — иначе счётчик озвучивался бы постоянно) → осознанное решение, не пробел.

**Живая верификация:** /settings — 4 региона (filter-mode-hint/font-scale-value/personalization-status atomic/export-status), все `isLive:true`, `statusLikeWithoutLiveRegion:[]`. /stats — chart-fallbacks + table-sort-status, обе формы = GET-reload. Правок кода нет.

**Огранич. эмпирики:** post-answer регионы (#result-feedback/confidence/extra-analysis) рендерятся после ответа (POST /answer запрещён границами) — подтверждены статически по фрагментам (атрибуты в разметке) + JS-attr'ы.


### cr.146 — WCAG 2.5.3 Label in Name: аудит + ФИКС декоративного «×» close-кнопки

**Вопрос:** у контролов с ВИДИМЫМ текстом доступное имя (aria-label/labelledby) обязано СОДЕРЖАТЬ этот видимый текст (voice-control). Icon-only (без видимого текста) — вне scope.

**Метод:** живой замер accessible-name по всем `button/a/[role=button|tab|radio]` на /settings + /stats (+ структурный разбор /). Для каждого: visibleText (textContent минус `aria-hidden`-поддеревья) vs accName; флаг если accName не содержит visibleText.

**Результат — почти чисто, 1 находка:**
- /settings (4): `A−`/`A+` (font-step) → «A−/A+: …размер шрифта» ✓; `Экспорт JSON`/`Экспорт CSV` → «Экспорт … — прогресс и статистика» ✓.
- /stats: `Применить фильтры` → «Применить фильтры статистики» ✓; `Искать` → «Искать по вопросам» ✓.
- (структурно) flashcard-grade `Не помню/Трудно/Хорошо/Легко` → «Оценка N: …» ✓; nav-ссылки без aria-label (имя=текст) ✓; icon-only favorite/regenerate/toggles — вне scope ✓.
- **Находка:** close-кнопка модалки клавиатурной справки имела видимый глиф `×` (U+00D7) НЕ помеченный декоративным + `aria-label="Закрыть"`. Строго 2.5.3 символьный глиф-иконка не считается текстовой надписью (voice-user скажет «Закрыть»), формально не провал — НО расходится с конвенцией проекта (все SVG-иконки несут `aria-hidden="true"`), тянет ложный флаг в скане и риск двойного озвучивания «times»+«Закрыть» в части AT.

**Фикс:** `×` обёрнут в `<span aria-hidden="true">×</span>` в обоих генераторах модалки — `app.js:1878` (все страницы с app.js) и `stats.js:340` (/stats). Теперь close-кнопка icon-only (visibleText=""), вне scope 2.5.3, консистентна с прочими иконками.

**Верификация:** `node --check` app.js+stats.js OK; live /stats после reload: `.kbd-help-close` → `spanHidden:true`, `visibleText:""`, `aria-label:"Закрыть"`, весь скан `violations=[]`. app.js-правка идентична stats.js (структурно + node). Версии app.js v=61→62 (×3 шаблона), stats.js v=14→15. gradle пропущен (JS+version-bump при живом bootRun — wedge-риск; применимые гейты node --check + live-скан пройдены).


### cr.145 — WCAG 2.4.3 Focus Order: CSS-reflow vs фокус-порядок (verified-clean)

**Вопрос:** визуальное переупорядочивание через grid (`grid-template-areas`/`grid-column`/`grid-row`/`display:contents`) не должно ломать смысл и операбельность tab-последовательности (фокус идёт по DOM, а не по визуалу).

**Инвентарь reflow-мест (grep + разбор):**
- **header** (base.css 588–627): `display:contents` на `.ed-masthead-actions` поднимает nav+toggles в грид `.ed-masthead-inner`; области `"brand toggles" / "nav nav"` при ≤1239px. DOM/tab = brand→nav→toggles. Замер на 900px (tabindex в шапке = [] → tab==DOM): brand(top12) → nav ×3(top101) → toggles ×3(top12). Фокус идёт вниз (brand→nav) затем вверх (nav→toggles) — расхождение на группу «toggles».
- **result** (3142–3147): interview-card col1 → related-questions col2 (правая рейка). DOM card→related = визуал слева→справа → совпадает.
- **summary** (3224–3230): summary-table col1 → summary-mistakes col2 → recommendations 1/-1. DOM-порядок = порядок чтения.
- **stats** (3176–3181): topic-table col1 (span2) → coverage-gaps col2 → совпадает.
- **focus-split** (3287–3337, opt-in `[data-layout=split]`): левая колонка meta(row2)→question(row3)→code(row4), форма — правый пейн (row1/-1). DOM meta→question→code→form = чтение «вопрос слева → ответы справа». Явные grid-row монотонны с DOM. Совпадает.

**Вердикт по header — НЕ провал 2.4.3.** Критерий требует сохранения смысла+операбельности, НЕ попиксельного совпадения (C27 «DOM=visual» — sufficient-техника, не требование; F44 про tabindex здесь неприменим — переупорядочивания через tabindex нет). Операбельность: все 7 контролов достижимы, ловушек нет. Смысл: brand→primary-nav→display-toggles — связная группировка, ВНУТРИ каждой группы порядок совпадает с визуалом. Расходится лишь групповой уровень (toggles визуально в ряду1, но tab-ятся после nav из ряда2).

**Ключевой аргумент против правки:** нет DOM-порядка, совпадающего с визуалом на ОБОИХ брейкпоинтах. При ≥1240px (один ряд brand·nav·toggles) текущий DOM совпадает ТОЧНО; перестановка под ≤1239px (brand→toggles→nav) сломала бы десктопное совпадение. Текущий порядок оптимален для десктопа и семантически связен на планшете/мобиле — лучший компромисс. Правок кода нет.


### cr.144 — WCAG 1.4.13 Content on Hover or Focus (verified-clean)

**Вопрос:** любой контент, показываемый по hover/focus (tooltip/popover/reveal), обязан быть dismissable + hoverable + persistent (нативные UA-`title` из критерия ИСКЛЮЧЕНЫ).

**Инвентарь триггерного контента (grep по CSS/JS/шаблонам):**
- **Кастомных тултипов НЕТ:** `role=tooltip` — 0, `data-tooltip` — 0, `content: attr()`-псевдо-тултипов — 0, JS-`mouseenter/mouseover/focusin`-хендлеров — 0.
- **Нативные `title=`** на `<button>`/`<div>`/`<th>` (favorite/regenerate/regen-badge/th-подсказки) — UA-рендер, **явно exempt** из 1.4.13.
- **`aria-describedby`** (settings seg-controls, focus options) → указывают на **всегда-видимые** инлайн-подсказки `*-hint`, не на hover/focus-триггерный попап → вне scope.
- **`content: attr(data-label)`** (base.css 3760) → стек-лейбл адаптивной topic-table (мобильный вид), всегда видим в стеке → не тултип.
- **`thead:focus-within`** (3709) → раскрывает sortable-заголовок по клавиатурному фокусу (skip-link-паттерн), persistent пока фокус внутри → ок.

**Единственный hover/focus-reveal — `.code-copy-btn`** (2833): `opacity:0` → `1` по `.code-copy-wrap:hover`/`:focus-within` (десктоп), всегда видима на touch (`@media hover:none`).
- **Hoverable ✓ (структурно гарантировано):** кнопка — *потомок* триггера `.code-copy-wrap`; наведение с обёртки на кнопку сохраняет `:hover` обёртки → не исчезает.
- **Persistent ✓:** видима пока hover/focus-within на обёртке; таймера-автоскрытия нет.
- **Dismissable:** кнопка — action-аффорданс в паддинг-гаттере pre (`top/right: space-2` внутри `padding: space-4`), не информационный оверлей; теоретически может накрыть правый край длинной 1-й строки кода на hover — это универсально-принятый reveal-on-hover copy-паттерн (GitHub/MDN), где операционные условия (hoverable+persistent) выполнены.

**Итог — чисто.** Кастомных tooltip/popover нет; нативные `title` exempt; `aria-describedby`-подсказки всегда видимы; единственный reveal (copy-btn) выполняет hoverable+persistent структурно. Правок кода нет.

**Огранич. эмпирики:** живой код-блок достижим только на `/result` через мутирующий POST `/answer` (запрещён проектными границами) — на дом-странице `pre`=0; вердикт по copy-btn опирается на структурный CSS-анализ (для hoverable/persistent он definitive).


### cr.143 — R1.48 ФИКС: alpha focus-ring → solid double-ring на 5 non-default дизайнах (WCAG 1.4.11) [follow-up R1.43]

**Метод:** canvas-sRGB контраст (надёжен и для oklch) по всем 10 остаточным alpha-`--shadow-focus` из R1.43-follow-up. Для каждого дизайн×тема — два числа: solid `outline` (`--color-border-focus` vs bg, покрывает большинство контролов через глобальный `:focus-visible`) и alpha box-shadow ring (композит над фоном, покрывает box-shadow-only контролы: `.settings-tab`/`.seg-btn`/inputs/selects).

**Замер (подтверждено):**
- `outline vs bg` — ВСЕ 10 проходят ≥3:1: notion 4.03/7.39 · superhuman 6.16/9.42 · stripe 6.19/5.71 · claude 4.27/7.35 · instrument 4.32/7.78.
- `alphaRing vs bg` — ВСЕ 10 ПРОВАЛИВАЮТ: 1.48 / 1.99 / 1.70 / 2.61 / 1.78 / 2.24 / 1.58 / 2.18 / 1.73 / 2.50 (диапазон 1.48–2.61, требование ≥3:1). Тот же класс дефекта, что editorial имел до R1.43 (~1.4:1).

**Фикс:** 10 alpha-колец (`0 0 0 3px rgba/oklch(accent, .30–.45)`) → solid double-ring `0 0 0 2px var(--color-bg-primary), 0 0 0 4px var(--color-border-focus)` (regex по `0 0 0 3px`, ровно 10 совпадений; solid-паттерн уже был у editorial/linear/swiss/mintlify/broadsheet/theverge). Внешнее 4px-кольцо = `--color-border-focus` → его контраст к фону = тот самый outline-ряд 4.03–9.42 ≥3:1. `--color-border-focus` не трогал.

**Верификация:** design-token-audit VERDICT CLEAN (hard-fails=0). Live (обе темы, /settings): `--shadow-focus` на `.settings-tab` резолвится в `0 0 0 2px <bg>, 0 0 0 4px <border-focus>` для notion/superhuman/stripe/claude ×light/dark — напр. notion/light `#097FE8`, stripe/dark `#8B93FF`, claude/light `#2476C4`. Все box-shadow-only контролы теперь ≥3:1 вместо 1.48–2.61. Теперь все 23 `--shadow-focus` в файле — solid double-ring (0 alpha-колец). head.html tokens.css v=63→64. gradle пропущен (CSS-only токены при живом bootRun — wedge-риск; применимый гейт = design-token-audit).


### cr.142 — WCAG 2.4.11 Focus Not Obscured (Minimum, AA): sticky-элементы не перекрывают фокус (verified-clean)

**Вопрос:** может ли какой-либо клавиатурно-сфокусированный контрол оказаться ПОЛНОСТЬЮ скрыт за author-created sticky/fixed-элементом (критерий 2.4.11, «entirely hidden» = провал на AA).

**Инвентарь sticky/fixed по приложению:**
- `thead th { position: sticky; top: 0; height: 45px }` в таблице тем на `/stats` — единственный sticky-**top** оверлей-кандидат.
- Sticky-**сайдбары** (base.css 3149/3301: result-related-questions, focus-question split, `top: space-5`) — боковые колонки, не верхние оверлеи.
- `site-header` — **осознанно НЕ sticky** (base.css:497, коммент «Sticky НЕ делаем: FNO-риск»).
- Fixed-position оверлеев нет.

**Ключевой структурный факт (замер в живом браузере):** sticky-контейнер thead — не документ, а `div.topic-table-wrap`. У обёртки `overflow-x: auto`, из-за чего `overflow-y` вычисляется в `auto` (CSS: visible+non-visible → non-visible становится auto). Но фикс-высоты у обёртки нет → `scrollHeight === clientHeight` (18005 === 18005) → **обёртка не скроллит вертикально** → `top:0` у thead вертикально инертен. Эмпирика подтверждает: при прокрутке страницы `th.getBoundingClientRect().top` едет линейно со скроллом (1581 → −2439 → −5690), т.е. **thead никогда не приклеивается к верху вьюпорта** — уезжает вместе с контентом как обычный поток. Он физически не может стать viewport-fixed оверлеем поверх сфокусированных ссылок tbody.

**Вывод по остальным:** sticky-сайдбары — это боковые колонки, они НЕ накрывают другие фокусируемые элементы (сами являются персистентным контентом). Non-sticky site-header верхнего оверлея не создаёт.

**Итог — чисто.** Ни один контрол при получении клавиатурного фокуса не скрывается целиком за author-content. `scroll-padding-top`/`scroll-margin-top` не требуются (нет активного sticky-оверлея, от которого нужно защищать scrollIntoView). Правок кода нет.


### cr.141 — WCAG 2.4.7 Focus Visible: каждый интерактив имеет индикатор (+ R1.43 регресс-чек)

**Метод:** статический разбор всех `outline:none` в `base.css` + live-обход реальным Tab по `/settings` (8 сэмплов, dark-тема).

**CSS-анализ (8 `outline:none` без комментов):**
- 233 `:focus:not(:focus-visible)` и 821 `label:focus-within` — гасят только МЫШИНЫЙ фокус; клавиатурный ринг сохранён (229 глобальный outline; 822 `label:has(input:focus-visible)` → 2px outline).
- 267 `#main-content:focus-visible` — контейнер-skip-target (tabindex=-1, не интерактив) → ринг на нём не нужен.
- 1338 `.settings-tab`, 1429/1682 (input/select), 3000 `.seg-btn` — все `outline:none; box-shadow: var(--shadow-focus)` (+ у input/select ещё border-color) → замена есть.

**Live (реальный Tab, 8 подряд, все `hasVisibleIndicator:true`):** skip-link, masthead-brand, nav «Аналитика», nav «Настройки», ed-theme-toggle, theme-toggle, next-btn CTA — все **outline 2px solid**; settings-tab «Сессия» — `outline:none` + **boxShadow `rgb(38,38,36) 2px, rgb(217,119,87) …`** = solid double-ring (R1.43 в рендере, непрозрачный Clay-ринг, не 40%-альфа).

**Правок нет** — каждый клавиатурно-фокусируемый интерактивный контрол показывает видимый индикатор; box-shadow-путь после R1.43 рендерит ≥3:1 double-ring (регресс-чек пройден).

### cr.140 — R1.43+R1.44 LIVE-VERIFIED на поднятом инстансе (app.js v=61, tokens.css v=63)

Поднял стек (postgres + bootRun) и проверил обе правки в живом браузере, обе темы.

**R1.44 (шорткаты) — PASS:**
- autofocus: при загрузке `/` `document.activeElement` = 1-я опция (`activeIsFirstOption:true`, `focusInForm:true`), MCQ-режим (4 опции).
- scope: фокус в форме + `3` → выбрана опция idx 2 ✓; затем фокус на header-ссылке (`nowFocusInForm:false`) + `1` → опция ОСТАЛАСЬ idx 2 (не сменилась) ✓ — исключение 2.1.4c работает.

**R1.43 (focus-ring) — PASS обе темы:**
- light: `--shadow-focus` = `0 0 0 2px #FAF9F5, 0 0 0 4px #C6613F` → Ember ринг vs ivory = **3.85:1**.
- dark: `0 0 0 2px #262624, 0 0 0 4px #D97757` → Clay ринг vs #262624 = **4.8:1**.
- solid double-ring применён (не alpha), оба ≥3:1 (было ~1.4:1).

Оба фикса закрыты полностью (код + токен-аудит + gradle-тесты + live). Тестовый браузер сброшен reload'ом.

### cr.139 — R1.44 ФИКС: character-key shortcuts scope + autofocus (WCAG 2.1.4)

**Реализация решения пользователя по R1.37 (вариант C).** `app.js`:
1. **focus-scope `1–9`** (keydown ~1215): ветка выбора варианта теперь гейтится `if (form.contains(document.activeElement))` — печатный шорткат активен ТОЛЬКО когда фокус внутри формы вопроса (исключение 2.1.4c «active only on focus»). Вне формы (шапка/ссылка) цифра не перехватывается.
2. **autofocus 1-й опции** (после keydown-хендлера ~1234): `if (optionInputs.length && !answered) optionInputs[0].focus({preventScroll:true})` — сажает фокус в форму при загрузке, чтобы `1–9` работали сразу. `preventScroll` — не прыгаем мимо текста вопроса; программный focus() не триггерит `:focus-visible` → визуального кольца на загрузке нет.

`?` (справка) оставлен глобальным осознанно: benign+reversible help-toggle, guard на text-inputs, scope сломал бы discoverability.

**Верификация:** `node --check` OK; `InterviewMvcControllerTest` + `TemplateFragmentContractTest` = GREEN. Live-проверка клавиатуры — при поднятом инстансе.

### cr.138 — R1.43 ФИКС: editorial focus-ring → solid double-ring (WCAG 1.4.11/2.4.11)

**Реализация решения пользователя по R1.40.** `tokens.css`: три `--shadow-focus` editorial (light 196, dark 263, dark-mirror 300) переведены с alpha-ring `0 0 0 3px rgba(accent, .40/.45)` на **solid double-ring** `0 0 0 2px var(--color-bg-primary), 0 0 0 4px var(--color-border-focus)` — тот же паттерн, что у linear и ~8 дизайнов.

**`--color-border-focus` НЕ трогал:** Ember #C6613F vs ivory #FAF9F5 = **3.85:1**, dark Clay #D97757 vs #262624 = **4.8:1** — оба уже ≥3:1 (цифра 2.69 из cr.134 = Clay-заливка кнопки, не focus-цвет).

**Верификация:** `design-token-audit.py` = **CLEAN**. box-shadow-only контролы (`.settings-tab`/`.seg-btn`) теперь ≥3:1 вместо ~1.4:1. Non-default alpha-ринг дизайны — follow-up.

### cr.137 — WCAG 2.3.3 prefers-reduced-motion: CSS + JS-анимации (three-state)

**Метод:** аудит покрытия motion-kill по CSS (`base.css` 285-304) и JS (`app.js`, `stats.js`).

**Результат — verified-clean, исчерпывающе.** Три-состоянная ось «Движение» (`data-motion` на `<html>`) реализована консистентно во всех слоях:
- **CSS:** `@media (prefers-reduced-motion: reduce) html[data-design]:not([data-motion=on]) *,::before,::after` гасит `animation-duration/iteration/transition-duration/scroll-behavior` (`!important`, универсальный селектор); плюс всегда-активный `[data-motion=off]` kill-блок вне media. auto→OS-pref, on→форс движения (`:not([data-motion=on])`), off→форс покоя.
- **JS smooth-scroll** (`app.js` 104-108): `dm=off→reduced`, `on→motion`, иначе `matchMedia(prefers-reduced-motion)` → `scrollIntoView(behavior: reduced?auto:smooth)`. Та же three-state модель.
- **Chart.js canvas** (`stats.js` 97-101, 163): `motionAllowed()` = та же модель; `var anim = motionAllowed()` → `options.animation = anim` (false при reduced → анимация off). Комментарий явно отмечает: canvas-анимация вне досягаемости CSS, гейтим вручную.

**Правок нет** — reduced-motion покрывает и CSS-переходы/анимации, и обе JS-анимации (скролл, графики); in-app тумблер и OS-pref обрабатываются единой three-state логикой во всех трёх местах.

### cr.136 — WCAG 1.3.1 / 2.4.6: иерархия заголовков + landmark-структура

**Метод:** fetch+DOMParser по `/`, `/stats`, `/settings`; извлечён outline `h1-h6` (уровень/текст/скрытость), поиск пропусков уровней, пустых и **фейковых** `[role=heading]`; инвентарь landmarks (main/header/nav/aside/search/region) + счётчики.

**Результат — verified-clean.**
- **Заголовки:** ровно **1 h1** на страницу (`Подготовка к собеседованию` / `Аналитика` / `Настройки сессии`); **пропусков уровней нет** (в т.ч. корректная h2→h3 вложенность на `/settings`: Сессия→{Фильтры,Режим,Прогресс}, Данные→{Экспорт,Опасная зона}); пустых заголовков 0; все описательные; **фейковых `role=heading` 0** — вся иерархия на реальных `<h*>`.
- **Landmarks:** на каждой странице ровно **1 `<main>` + 1 `<header>`** (нет дублей-путаницы); `<nav aria-label="Основная навигация">`; `/` — `<aside aria-label="Сессия">` (комплементарная панель); `/stats` — `<form role="search" aria-label="Поиск по вопросам">` (R1.26) + `<div role="region" aria-label="Детализация по темам">` (таблица); `/settings` — минимальный чистый набор.

**Правок нет** — структура документа семантически корректна: одиночный h1, непрерывные уровни, реальные заголовки, единичные main/banner, помеченные nav/search/region/complementary.

### cr.135 — WCAG 1.4.11/2.4.11 focus-ring: полная энумерация по 11 дизайнам (R1.39 завершён, decision-gated)

**Продолжение cr.134.** Прочитаны CSS-правила фокуса (`base.css` 229/326/345/1337) + все токены `--shadow-focus`/`--color-border-focus` (`tokens.css`). Картина полная.

**Два паттерна focus-ринга в системе:**
- **Solid double-ring (сильный):** `0 0 0 2px var(--color-bg-primary), 0 0 0 4px var(--color-border-focus)` — непрозрачный ринг фокус-цветом; у linear и ~8 дизайнов (`tokens.css` 385/505/551/743/784/856/908/1326…).
- **Альфа-ринг (слабее):** `0 0 0 3px rgba(accent, 0.30–0.45)` — у **editorial (дефолт)** (196 light / 263+300 dark) и ряда других (623/664/981/1022/1095/1136/1210/1251).

**Editorial дефолт — маргинально-слабый фокус в нормальном режиме:**
- Глобальный `:focus-visible` outline = `--color-border-focus` #C6613F (light) / #D97757 (dark). #C6613F vs тело страницы ≈ **2.69:1** (маргинал; ~3.5–3.85 только на карточке с иным bg).
- Box-shadow-only контролы (`.settings-tab` L1337, `.seg-btn`) гасят outline и берут `--shadow-focus` = rgba(198,97,63,0.40) → эффективный контраст ≈ **1.4:1** (замер реальным Tab, cr.134).

**Уже сделано автором (не дефект):** `@media (forced-colors: active)` (L345-363) возвращает системный `Highlight` outline для box-shadow-only контролов + кодирует seg-active/checked-MCQ системной парой; `prefers-contrast: more` → outline 3px. HC/forced-colors юзеры покрыты. Пробел — **нормальный режим дефолтного editorial**.

**Вердикт:** реальный, но узкий a11y-gap (focus-индикатор дефолтного дизайна <3:1 в нормальном режиме) — в отличие от resting-границ/accent-fill (осознанный editorial-язык, cr.134) фокус-контраст это a11y, не эстетика; авторские аннотации (`--color-border-focus` 4.2–7:1 у ряда дизайнов) подтверждают целевые ≥3:1.

**Ремедиация — decision-gated:** перевести editorial (и прочие альфа-ринг дизайны) `--shadow-focus` на solid double-ring паттерн, уже применённый linear/др. в том же файле, и/или затемнить editorial `--color-border-focus` до ≥3:1 vs тело страницы. Это правка дизайн-токенов → гейт `scripts/design-token-audit.py` + AA-перепроверка обеих тем + прогон тестов; **затрагивает editorial-идентичность → решение пользователя.** Правок не вносил.

### cr.134 — WCAG 1.4.11 Non-text Contrast: замер границ/состояний/focus-ring (частичный, actionable lead)

**Метод:** canvas-нормализация цветов (oklch→sRGB) + расчёт контраста для границ и заливок UI-компонентов на `/settings`, тема light. Focus-индикаторы мерены **реальным Tab** (не `.focus()` — тот не триггерит `:focus-visible`).

**Замеры (light):**
- text input: border rgb(209,207,197) vs page = **1.48**, fill vs page = 1.10 → граница едва различима.
- secondary button: border vs page = **1.48** (заливки нет).
- primary button: accent-fill rgb(217,119,87) vs page = **2.69**.
- seg active (`.seg-btn.is-active`): состояние = заливка accent rgb(217,119,87) (~2.69 vs окружение) + смена цвета текста + `aria-checked=true`.
- focus (реальный Tab): `.settings-panel` (tabpanel) — 2px solid outline, **3.85** (проходит). `.settings-tab` — глобальный outline перекрыт в `none`, focus-ring = `box-shadow rgba(198,97,63,0.4) 0 0 0 3px`; accent@40%α над cream ≈ (223,181,163), контраст ≈ **1.42** (не проходит).

**Классификация (важно — не всё это дефект):**
- **Осознанный дизайн, НЕ трогать:** hairline-границы инпутов/кнопок (1.48) и accent-заливка (2.69) — часть editorial-языка и брендового акцента (токены, решения пользователя). Границу несёт также сильный keyboard-focus outline; resting-subtlety намеренна. 1.4.11 для этих — спорно, но это дизайн-токен-территория, в одностороннем порядке не меняю.
- **Actionable lead (реальный кандидат в дефект):** focus-индикаторы **неоднородны** — часть контролов (панель) используют сильный глобальный outline (3.85, проходит), а `.settings-tab` заменяет его слабым 40%-альфа accent-рингом (~1.42, НЕ проходит). Это внутренняя несогласованность фокуса, а не брендовое решение → чинибельно (усилить ring до ≥3:1 либо не гасить глобальный outline).

**Правок нет** — нужна полная энумерация focus-индикаторов по всем типам контролов (input/select/seg-btn/кнопки/ссылки/чекбоксы) в **обеих темах**, затем адресный фикс слабых рингов (focus-токены → test-verify, сейчас заблокировано живым bootRun). Продолжение — след. тик.

### cr.133 — WCAG 1.4.10 Reflow: 320 CSS px без 2D-скролла

**Метод:** эмуляция viewport 320×800 (CDP `Emulation.setDeviceMetricsOverride` через `emulate`, т.к. `resize_page` заблокирован fullscreen-окном) на `/`, `/stats`, `/settings`. Скан `documentElement.scrollWidth − clientWidth` + перечисление элементов, чей правый край выходит за viewport и которые НЕ лежат в `overflow-x:auto/scroll` контейнере (легитимный 2D-контент исключается).

**Результат — verified-clean.** На всех трёх страницах `docOverflow = 0`, offenders = 0:
- `/`: горизонтального скролла нет, ничего не выламывается.
- `/stats`: таблица тем внутри `.card.overflow-x-auto`; при 320px колонки схлопнуты (`is-collapsed`), `scrollW = clientW = 255` — таблица вписывается, документ не скроллится по горизонтали. Широкая таблица = легитимное 2D-исключение 1.4.10 в любом случае.
- `/settings`: сегмент-контролы `flex-wrap:wrap` → переносятся на несколько рядов, а не переполняют ряд; offenders 0.

**Правок нет** — на 320px (≈400% зум от 1280) контент реально reflow-ится в одну колонку без горизонтального скролла; единственный 2D-контент (таблица) корректно обёрнут в скролл-контейнер и/или схлопывается.

### cr.132 — WCAG 2.1.4 Character Key Shortcuts: глобальные `1–9`/`?` без off/remap (OPEN, decision-gated)

**Находка (Level A, подтверждена замером).** На экране вопроса два печатных одиночных символьных шортката привязаны к `document` (глобальные), а не к фокусу компонента, и не имеют механизма отключения/переназначения:
- `1`–`9` (`app.js` L1215) — выбор варианта MCQ;
- `?` (`app.js` L1917) — показать/скрыть справку.

**Замер:** фокус переведён на header-ссылку (`<a>`, вне группы опций) → нажатие `2` выделило вариант №2 (`globalShortcutFired:true`). При загрузке вопроса фокус на `document.body` (autofocus нет) — то есть `1–9` работают сразу, это осознанная power-user фича немедленного клавиатурного ответа.

**Что уже сделано верно:** guard на `INPUT/TEXTAREA/SELECT` (не radio) — ввод «?» и цифр в поля поиска/счётчика не перехватывается (L1176, L1915); модалка справки гасит шорткаты страницы (L1174). Но 2.1.4 требует именно off / remap / «active only on focus» — ни одно не выполнено, т.к. шорткаты активны при фокусе на любом нетекстовом элементе.

**Ремедиация — tradeoff, нужно решение пользователя:**
- **A. Тумблер «горячие клавиши вкл/выкл»** — строго по 2.1.4 (mechanism to turn off), сохраняет немедленный ввод. Но это **новая ось персонализации** → территория отложенных пользователем решений (§5), в одностороннем порядке не добавляю.
- **B. Привязка к фокусу (исключение 2.1.4c)** — гасить `1–9` пока фокус вне `<form>`. Чисто клиентское, но **регрессирует UX**: фокус стартует на `body`, юзеру придётся сперва Tab-нуть в опции.
- **C. Autofocus первой опции при загрузке + привязка к фокусу** — делает B почти прозрачным (фокус уже в форме) и удовлетворяет 2.1.4c; минус — перенос фокуса при загрузке (умеренный a11y-нюанс, но фокус на первом контроле основной задачи обычно приемлем).

Рекомендация: **C** как самый спец-корректный с минимальной UX-потерей; при нежелании autofocus — **A** (но это разблокировка отложенной оси). Правок кода не вносил — сердцевина answer-flow, ремедиация decision-gated и требует прогонки MVC/JS-тестов (сейчас заблокировано живым bootRun).

### cr.131 — WCAG 2.5.8 Target Size (Minimum, AA): замер всех интерактивных целей

**Метод:** живой замер `getBoundingClientRect` по всем интерактивным элементам (`a[href]`, `button`, `input`, `select`, `[role=button/tab/radio/checkbox/switch]`, `summary`, `[tabindex≥0]`) на `/`, `/stats`, `/settings` (1280). Флаг — цель <24×24 CSS px, затем разбор исключений 2.5.8 (inline / label-wrap / UA-control / spacing).

**Результат — verified-clean.** Все суб-24 узлы разобраны и легитимны:
- **`/stats`** (14 флагов): 12 — inline-ссылки тем (высота 17px = line-height ячейки, ширина 62–176px) → **inline-исключение** (размер ограничен line-height нетаргетного текста). 2 — чекбоксы `important`/`onlyWrong` (визуал 18×18), но оба **обёрнуты в `<label>` высотой 44px** → эффективная хит-зона 125×44 / 81×44 ≥ 24.
- **`/`** (4 флага): sr-only radio-инпуты селектора режима (1×1, `absolute`-clip), кликается стилизованный `<label>`-карточка высотой 126/156px → цель ≥24.
- **`/settings`** (4 флага): чекбоксы/инпуты в `<label>` высотой 44px → хит-зона ≥24.

Иконочных кнопок (тема, «?», копи, наверх) и сегмент-контролов/степперов/табов <24×24 **нет** — все ≥ порога.

**Правок нет** — ни одна реальная интерактивная цель не меньше 24×24; малые визуальные глифы всегда обёрнуты в крупную кликабельную область или подпадают под inline-исключение.

### cr.130 — WCAG 1.4.12 Text Spacing: инжекция override, проверка на потерю контента

**Метод:** инжектирован стандартный 1.4.12-override (`line-height:1.5`, `letter-spacing:0.12em`, `word-spacing:0.16em`, `margin-bottom:2em` на блоках) на `/`, `/stats`, `/settings` (1280). Сканирование горизонтального оверфлоу документа + элементов с `overflow:hidden/clip`, у которых `scrollHeight/Width` превышает клиентский → признак обрезанного текста.

**Результат — verified-clean.**
- `/`: горизонтального оверфлоу нет, обрезанных элементов 0.
- `/stats`: 0 реальных; единственный флаг — `caption.visually-hidden` (штатная SR-техника 1px `clip`, не видимый контент). Широкая таблица тем лежит в `.topic-table-wrap` (`overflow-x:auto`) → под увеличенным трекингом расширяется в **скролл**, а не клиппинг (потери контента нет — соответствует 1.4.12). `letter-spacing` таблицы под override = 1.68px (=0.12em при 14px) → трекинг реально применяется без обрезки.
- `/settings`: 0 реальных; единственный флаг — `h2.settings-panel-title` (подтверждено: `position:absolute; 1px×1px; clip:rect(0,0,0,0)` — visually-hidden заголовок таб-панели для SR). Сегмент-контролы/кнопки: 0 обрезанных подписей.

**Правок нет** — жёсткие высоты нигде не режут текст; все контейнеры либо auto-высотные, либо скроллятся. Ложные срабатывания — только легитимные visually-hidden узлы.

### cr.129 — WCAG 4.1.3 Status Messages: комплексный аудит aria-live/role=status

**Область:** все динамические регионы-оповещения на `/`, `/stats`, `/settings` (SSR-инвентарь + живая проверка динамики).

**Инвентарь регионов (роль / aria-live / atomic / фокус / текст-на-старте):**
- `/`: `.inline-alert.ed-noscript` (role=alert, статичный no-JS фоллбэк); `.inline-alert.hidden` (role=alert, assertive, пусто — сток ошибок); `.result-feedback.hidden` (role=status, polite, atomic, пусто — вердикт ответа); безымянный polite-div (сток объявлений, пусто).
- `/stats`: no-script alert; 2× `.chart-fallback.hidden` (role=status, polite — «График временно недоступен…»); `.visually-hidden` (polite, atomic — регион объявления сортировки из cr.124).
- `/settings`: no-script alert; `.control-inline-hint` (role=status, polite — живой хинт порядка); `.font-scale-value` (role=status, polite — «100%»); `.control-inline-hint.hidden` (role=status, atomic, пусто); `.export-status` (role=status, polite, скрыт CSS-классом, пусто).

**Вердикт — verified-clean.** Конфигурация ровно по 4.1.3:
- срочность разделена корректно: `role=alert`/assertive только у ошибок и no-JS фоллбэка, у всех несрочных обновлений — `role=status`/polite (не перебивают ридер);
- `aria-atomic=true` стоит там, где регион перезаписывается целиком (вердикт, сортировка, хинты) — ридер читает всё сообщение, а не дельту;
- ни один регион не фокусируемый — статус-сообщения не крадут фокус (проверено);
- скрытые регионы пусты на старте → нет ложного объявления при загрузке; заполненные (`.control-inline-hint`, `.font-scale-value`) статичны на старте и объявляются лишь при изменении.

**Живая верификация динамики:** клик `A+` (шаг шрифта) → `.font-scale-value` 100%→110%, `root.style.fontSize`=110%, регион role=status/polite → ридер озвучит «110%». Сортировочный регион подтверждён ранее (cr.124). Дефолты восстановлены (reset + очистка localStorage).

**Правок нет** — инфраструктура status-сообщений полная и настроена верно.

### cr.128 — WCAG 1.3.1 Info & Relationships: таблицы + списки — ЧИСТО (R1.33)

Новая размерность: программная определяемость структурных связей (caption, scope,
term-value, декоративная графика).

- **Таблица тем — образцово**: `<caption>` «Статистика по темам»; все 6 колоночных
  th со `scope="col"`; ВСЕ 319 строк начинаются с `<th scope="row">` (имя темы —
  row header). Полная 2D-навигация для скринридера. Эталон разметки.
- **Stat-карточки доступны по порядку чтения**: `.stat-item` = `.stat-label` («Всего») +
  `.stat-value` («9818»); SR читает «Всего 9818» — связь метка-значение передаётся
  DOM-порядком и близостью, информация не теряется. Плюс каждая несёт title-расшифровку.
- **Декоративный accuracy-bar** — `aria-hidden="true"` (дублирует текстовую «Точность N%»,
  заливка визуально смещена) — корректно убран из дерева доступности (документировано
  в самом шаблоне).
- **Псевдо-заголовки не найдены**: «Фильтры»/«Поиск» — настоящие `<h2>`; лейблы карточек —
  `<span>` (метки, а не заголовки секций) — корректно.

**Отложенный кандидат-улучшение (P4, низкий impact)**: stat-карточки можно поднять до
`<dl>/<dt>/<dd>` для явной term-definition-семантики. НЕ применяю сейчас: (1) информация
и так не теряется (порядок чтения), т.е. это не барьер; (2) фрагмент stats-grid общий
(сайдбар + summary), (3) dd несёт UA-margin — риск визуальной регрессии, (4) TemplateFragment
ContractTest заблокирован живым :8080 — структурную правку шаблона нельзя верифицировать.
Кандидат для window-tick-сессии, когда тесты прогонятся. Кодовых правок нет; cr.128.

### cr.127 — WCAG 2.5.3 Label in Name — ЧИСТО (R1.32)

Новая размерность: видимый текст контрола должен входить в accessible name (голосовой
ввод «нажми <видимый текст>» иначе не находит контрол).

- **MCQ-опции ✓**: aria-label = «Вариант N: <буква>. » + полный видимый текст опции →
  видимая метка ЦЕЛИКОМ содержится в accessible name (проверено точным вхождением на
  4 живых опциях, prefix «вариант 1: a. » … «вариант 4: d. »). Голосовая команда по
  видимому тексту сработает; префикс лишь добавляет контекст для SR.
- **Контролы /stats, /settings ✓**: кнопки с aria-label («Применить фильтры
  статистики» ⊃ видимое «Применить фильтры»; «Искать по вопросам» и т.д.) — видимый
  текст всегда подстрока имени.
- **Осознанный не-дефект**: кнопка закрытия модалки — видимый глиф «×», aria-label
  «Закрыть». «×» — символьный глиф, а не текстовая метка; 2.5.3 распространяется на
  видимые ТЕКСТОВЫЕ метки, символы-иконки исключены (канонический паттерн close-button).

**Tooling-урок №8**: сравнение containment по SSR-DOMParser даёт ложные срабатывания
на длинных строках с инлайн-глифами (стрелки →) — пробелы вокруг них в aria и в
textContent расходятся. Сравнивать на ЖИВОМ DOM с идентичным collapse пробелов
(\\s+→' '), не на fetched-разметке.

Вывод: все текстово-подписанные контролы проходят 2.5.3; фиксов не требуется.

### cr.126 — WCAG 3.2.3/3.2.4 Consistent Navigation & Identification — ЧИСТО (R1.31)

Новая размерность: одинаковость общего хрома между /, /stats, /settings.

- **3.2.3 Consistent Navigation ✓**: три nav-ссылки (Фокус / Аналитика / Настройки) —
  идентичный текст, href и ПОРЯДОК на всех страницах; `aria-current="page"` корректно
  маркирует текущую (page-specific, как и должно). Навигация в одном относительном
  порядке — требование выполнено.
- **3.2.4 Consistent Identification ✓**: три тумблера шапки (раскладка / дизайн / тема)
  — одинаковый порядок и базовые aria-label на всех страницах (динамический суффикс
  «(сейчас: X)» отражает состояние, не идентификацию — легитимно варьируется);
  back-to-top — идентичный aria-label везде.
- **Осознанный не-дефект — skip-link**: текст различается по странице («Перейти к
  вопросу» / «к аналитике» / «к настройкам») при одном href `#main-content`. Это НЕ
  нарушение 3.2.4: цель прыжка семантически РАЗНАЯ на каждой странице (вопрос ≠
  аналитика ≠ настройки), и точное именование места назначения информативнее генерик-
  «Перейти к содержимому». 3.2.4 таргетит запутывающую непоследовательность, а не
  полезную специфичность — как и page-title/aria-current, skip-текст правомерно
  контекстен.

Вывод: общий хром консистентен там, где это требуется, и осознанно контекстен там,
где специфичность полезнее. Фиксов не требуется.

### cr.125 — WCAG 3.1.1/3.1.2 Language: lang=ru + иноязычные фрагменты — ЧИСТО (R1.30)

Новая размерность: язык страницы и разметка иноязычных вставок (скринридер без lang
читает англ. термины русской фонетикой).

- **3.1.1 (Level A)**: `<html lang="ru">` на /, /stats, /settings — выполнено. dir не
  задан (для ЛТР-русского и не нужен), xml:lang не требуется в HTML5.
- **3.1.2 (Level AA) — чисто по исключению**: англоязычный контент на доступных
  страницах ограничен именами тем-ссылок («Agentic patterns», «Clickhouse», «Linkerd»),
  UI-техтерминами и код-фрагментами. Все три категории — явные исключения 3.1.2:
  «proper names, technical terms, words of indeterminate language». Полноценных
  англоязычных ПРЕДЛОЖЕНИЙ в русской прозе на этих страницах нет → разметка lang="en"
  не требуется.
- **Код**: на /,/stats,/settings 0 блоков `code/pre/kbd`; на страницах вопроса/разбора
  (session-gated) код появляется, но код — «words of indeterminate language», тоже
  вне требования 3.1.2. Forward-нота: при session-gated проходе перепроверить, нет ли
  цельных англ. предложений в разборах (эталон-ответы), но техтермины/код — exempt.

Вывод: язык страницы объявлен, иноязычные вставки попадают под исключения; фиксов
не требуется.

### cr.124 — WCAG 2.4.4 Link Purpose + безопасность ссылок — ЧИСТО (R1.29)

Новая размерность: цель ссылки из текста (2.4.4), target=_blank-гигиена и мёртвые href
на 4 страницах (347 ссылок суммарно, из них 327 на /stats).

- **target=_blank**: НИ ОДНОЙ ссылки с _blank во всём приложении → нет ни утечки
  window.opener (rel=noopener не требуется), ни необъявленного открытия нового окна.
  Вся навигация — в том же контексте.
- **Мёртвые/placeholder href** (`#`, пустой, `javascript:`): 0.
- **Пустые accessible names**: 0 (подтверждает R1.08 на ссылочном срезе).
- **Дубли текста → разные адреса**: 0 (каждый видимый текст ссылки уникально
  адресует).
- **Неоднозначный текст**: 0 реальных. Сканер дал 3 ложных срабатывания —
  «Linked lists», «Clickhouse», «Linkerd»: имена тем, чьи префиксы («Link»/«Click»)
  совпали с regex неоднозначности. Текст этих ссылок как раз описателен (имя темы).

**Методическая заметка**: regex-детектор неоднозначных ссылок ловит англоязычные
техтермины как префиксы (Link*, Click*) — фильтровать по точному равенству/границам
слова, а не startsWith, либо игнорировать известные topic-slug'и.

Вывод: цели ссылок ясны из текста, target-гигиена и href-целостность идеальны.
Фиксов не требуется.

### cr.123 — WCAG 1.4.1 Use of Color: индикаторы не полагаются на цвет — ЧИСТО (R1.28)

Новая размерность: передаётся ли где-либо информация ТОЛЬКО цветом (без текста/формы/иконки).
Верификация структурная (template-логика + CSS), т.к. значения сейчас data-gated («—» после
wipe) — но пары «цвет+не-цвет» видны в разметке.

- **MCQ-опции**: выбор маркируется буквенным бейджем (counter upper-latin A/B/C/D) + рамка +
  заливка — не цветом. ✓ (контраст этих не-цветовых индикаторов — R1.07/R1.17/R1.23).
- **Точность /stats** (accuracy-cell): цвет high/mid/low, НО текст ячейки — само число
  «85.0%»; low вдобавок font-weight: semibold (не-цветовой дубль). Цвет вторичен. ✓
- **Зрелость** (maturity-badge): цвет + border-color по уровню, НО текст — числовой скор
  «12.0» и рамка-бейдж; форма отличает от простого текста. ✓
- **Прогресс**: стек-бар learned(зелёный)/due(акцент), НО первичная метрика — текст
  «0/30» в той же ячейке; счётчик due — в title. Существенная информация не теряется. ✓
- **Графики**: два цвета сегментов, но role=img + aria-label + таблица = альтернатива
  (R1.09); сейчас скрыты.

Вывод: ни один индикатор не полагается на цвет в одиночку; каждый цветовой сигнал
сопровождается числом, бейджем, буквой или весом шрифта. Фиксов не требуется.
Живая перепроверка с реальными значениями — data-gated (вместе с instrument-палитрой).

### cr.122 — Нулевые результаты и семантика фильтров /stats — чисто + 1 открытый вопрос (R1.27)

Новая размерность: пустые выдачи и фактическая семантика фильтр-панели (всё GET-безопасно).

- **Zero-state поиска образцовый**: «По запросу «щзщзщз…» ничего не найдено. Измени
  запрос или сбрось поиск и фильтры.» — цитата запроса + руководство + ссылка сброса
  на чистый /stats; ввод сохраняет запрос (контекст не теряется). Live-регион не нужен —
  это полная SSR-навигация.
- **Семантика фильтров разгадана**: «Применить фильтры» скоупит stat-карточки
  (topic=algorithms → «Всего 16» вместо 9818) и НЕ фильтрует таблицу тем — таблица
  намеренно всегда полный обзор (у неё свои sort/expander). Осмысленный дизайн,
  не дефект; моя первая гипотеза «фильтр без эффекта» была неверна.
- **ОТКРЫТЫЙ ВОПРОС (продуктовый, бэкенд — вне мандата)**: выдача поиска при активном
  topic-фильтре содержит вопросы других тем (q=сложность + topic=algorithms → в топе
  Saga из database-transactions). Либо поиск задуман глобальным (тогда hidden-проброс
  фильтров в search-форме — только для сохранения UI-состояния, что консистентно),
  либо scope потерян. Требует решения пользователя; UI-подпись «поиск по всем
  вопросам» — кандидат-фикс ПОСЛЕ подтверждения семантики.
- **Data-gated**: These onlyWrong/important при нуле ответов неотличимы от «нет данных» —
  проверить с появлением данных.

### cr.121 — Поиск/фильтры /stats: search-landmark добавлен + регрессия R1.12 (R1.26)

Новая размерность: семантика поисково-фильтровой пары /stats.

- **Найден и закрыт пробел**: форма поиска не была search-landmark (ни role="search",
  ни <search>) — SR-пользователи не могли прыгнуть к ней landmark-навигацией, а формы
  были безымянны (безымянная form — не landmark). Фикс в stats.html: форме поиска —
  role="search" + aria-label="Поиск по вопросам", форме фильтров —
  aria-label="Фильтры статистики" (становится именованным form-landmark).
  Верифицировано живьём после cp-синка: search-landmark ×1, обе формы в инвентаре
  именованных.
- **Остальное чисто**: обе формы method=get action=/stats (идемпотентны, безопасны для
  повторов/back), все контролы с label (R1.08 подтверждён), скрытые input проносят
  контекст фильтров между формами (поиск не сбрасывает фильтры — продуманная связка).
- **Регрессия R1.12**: print-правило .stats-filter-card держится на served v=91.

Контракт-тестов прибавилось на прогон: +1 правка шаблона (stats.html) в очередь
window-tick.

### cr.120 — Disclosure «показать ещё» таблицы /stats — ЧИСТО (R1.25)

Новая размерность: жизненный цикл expander-кнопки свёрнутой таблицы.

- **Семантика**: button с aria-expanded (false→true→false при toggle) + aria-controls →
  topic-table-wrap (резолвится). Канонический APG disclosure.
- **Текст кнопки информативен и меняется**: «Показать все темы (307 ниже)» ↔
  «Свернуть таблицу» — до раскрытия пользователь знает объём скрытого.
- **Поведение**: 12 видимых строк → 319 → 12; tr[hidden] честно снимаются/возвращаются.
- **Фокус**: остаётся на кнопке при обоих переходах (не теряется в body) — свернуть/
  развернуть можно многократно с клавиатуры без потери позиции.
- Live-объявления раскрытия нет — и не требуется: aria-expanded на кнопке в фокусе
  озвучивается скринридером сам; отдельный live-регион был бы дублированием.

Напоминание связки: в print tr[hidden] разворачиваются CSS-ом независимо от кнопки
(R0.x print-блок), т.е. печать не зависит от состояния disclosure. Фиксов не требуется.

### cr.119 — Семантика сортировки таблицы /stats (aria-sort) — ЧИСТО (R1.24)

Новая размерность: полный жизненный цикл сортируемой таблицы — состояние, клавиатура,
объявления.

- **Паттерн**: 5 сортируемых th («Тема», «Всего», «Точность», «Сброшено», «Зрелость») —
  сами фокусируемы (tabindex=0) с aria-sort; «Прогресс» честно несортируемый (без
  tabindex/aria-sort). Строчные заголовки — th.topic-name (327 th всего: 6 колонок +
  319 строк — корректная разметка row headers).
- **Цикл состояний**: none → ascending → descending, строки реально пересортировываются
  (первая колонка 30/28/32 → 13/13/14 → 56/55/53). **Эксклюзивность**: активация другой
  колонки сбрасывает предыдущую в none — ровно один активный aria-sort.
- **Клавиатура**: и Enter, и Space активируют сортировку на фокусируемом th.
- **Объявление для SR**: live-регион получает «Таблица отсортирована: Точность,
  по убыванию.» — локализованное объявление смены порядка (4.1.3 закрыт для сортировки).

Нота: ARIA APG рекомендует button внутри th, но фокусируемый th с aria-sort + полная
клавиатурная активация + live-объявление — эквивалентная по доступности реализация;
не переделывать. Состояние сброшено перезагрузкой (клиентская сортировка не персистится).

### cr.118 — 1.4.11 Non-text Contrast: linear + theverge, обе темы — ЧИСТО (R1.23)

Продолжение R1.07/R1.17: linear — последний из трёх дизайнов со структурными SIGNATURES
в base.css, theverge — представитель token-only четвёрки с самой контрастной палитрой.

| Комбинация | бейдж vs карточка | текст бейджа | checked-рамка vs фон | дельта состояний | фокус-кольца |
|---|---|---|---|---|---|
| linear dark | 16.38 | 18.73 | 13.64 | 8.39 | 5.18 |
| linear light | 17.63 | 19.93 | 7.45 | 5.7 | 4.7 |
| theverge dark | 13.01 | 18.58 | 8.57 | 4.93 | 7.2 |
| theverge light | 15.3 | 18.58 | 7.11 | 4.9 | 4.55 |

Все индикаторы состояния и фокус-кольца ≥3:1, минимум 4.55.

**Уточнение урока №6**: у linear transition длиннее — 450ms не хватило (первый замер
дал ложные ≈1.0), 800ms достаточно. Диагностический дифф checked-vs-соседний работает
как детектор «transition не устаканился»: sanity-гейт settled = badgeVsCard > 1.5
добавлен в скрипт замера.

Покрытие 1.4.11 теперь: **5/11 дизайнов × 2 темы** — все 3 структурных (editorial,
linear, swiss) + оба края палитр (claude, theverge). Оставшиеся 6 — token-only
вариации между уже замеренными краями; эскалация при изменении токенов.
localStorage возвращён к дефолтам.

### cr.117 — Уважение браузерного размера шрифта (rem-цепочка) — ЧИСТО (R1.21)

Новая размерность: масштабируется ли типографика от пользовательской настройки
размера шрифта браузера (chrome://settings/fonts, 16→24px), а не только от zoom.

- **Токены целиком rem-based**: --font-size-xs…4xl = 0.75rem…4rem, px-размеров шрифта
  в токенах нет.
- **Ось fontScale реализована канонически**: head.html:244 ставит
  root.style.fontSize = N% — процент ОТ браузерного дефолта (настройка пользователя
  уважается и перемножается с осью), а при fontScale=1 инлайн снимается полностью —
  чистый браузерный дефолт.
- **Живой тест** (root 16→24px, эмуляция настройки): контентный текст — ×1.5 ровно
  (полное следование), h1 шапки — ×1.194: осознанная fluid-типографика
  clamp(1.55rem, 1.1rem + 2.2vw, 3rem) — rem-компоненты в полу и в preferred
  гарантируют рост при любом дефолте (не замораживается), vw-часть даёт
  суб-пропорциональность; при полностраничном zoom (основной путь 1.4.4) vw
  масштабируется визуально вместе со всем. Обоснованный не-дефект.

Вывод: rem-цепочка чистая, пользовательская настройка шрифта работает на всём
контенте; фиксов не требуется.

### cr.116 — Перф-бюджет /stats (319-строчная таблица) — ЧИСТО (R1.20)

Новая размерность: перф-baseline самой тяжёлой страницы. Lighthouse дал NO_FCP —
вкладка автоматизации в фоне не пейнтит (**расширение tooling-урока №5**: Lighthouse
через MCP работает только при окне Chrome на переднем плане; в фоне — только
paint-независимые метрики Performance API).

Замер Navigation Timing + Resource Timing на живом /stats (desktop 1280):

- **TTFB 98ms** — сервер рендерит 319-строчную таблицу Thymeleaf быстрее 100ms.
- **HTML 41KB** по сети (gzip), **DOMContentLoaded 174ms, load 177ms** — статика
  из кэша (7 запросов, 0KB повторного трафика при max-age=86400 из R1.14).
- **Long tasks: 0** — никакого JS-джанка на инициализации (сортировка/фильтры
  подключаются лениво-легко).
- **DOM 5368 узлов** — выше lighthouse-эвристики «1500», но обоснованно: 319 строк ×
  ~15 узлов; свёрнутые tr[hidden] (display:none) не участвуют в лейауте, рендер-куст
  фактически ~12 видимых строк. Не дефект.

Вывод: перф-бюджет здоров, фиксов не требуется. Полный Lighthouse-прогон (FCP/LCP/CLS)
— при живой сессии с Chrome на переднем плане (там же перф-скор), отмечено в плане.

### cr.115 — Viewport-единицы (dvh) и fixed-геометрия на мобильных — ЧИСТО (R1.19)

Новая размерность: iOS/мобильные ловушки вьюпорта — legacy 100vh (переполнение под
URL-баром Safari), safe-area у fixed-элементов, геометрия FAB.

- **Все 5 viewport-height употреблений в CSS — уже 100dvh** (body min-height, focus-зона,
  max-height обеих модалок с overflow-y auto + overscroll-behavior: contain): legacy
  100vh нет вообще; dvh — baseline с 2022, отдельный fallback в 2026 не нужен.
- **Оверлеи** (prompt-overlay, kbd-help-overlay) — position: fixed; inset: 0 — без
  vh-зависимости, модалки скроллируемы внутри (max-height: calc(100dvh − отступ)).
- **safe-area/env() не требуется**: meta viewport без viewport-fit=cover → браузер сам
  не кладёт контент под чёлку/индикатор, env(safe-area-inset-*) актуален только при
  cover-режиме.
- **back-to-top на 360px живьём**: 44×44 (тач-таргет), отступы 24px от краёв, полностью
  во вьюпорте, под ним нет интерактивных элементов (только MAIN tabindex=-1).
  Появление направленное (scroll-up) — перекрытие контента эфемерно.

Вывод: мобильная fixed-геометрия и вьюпорт-единицы образцовые; фиксов не требуется.

### cr.114 — ARIA-грамматика: роли, контексты, обязательные свойства — ЧИСТО (R1.18)

Новая размерность: валидность ARIA-словаря на 4 страницах (SSR) + живое состояние
seg-controls.

- **Инвентарь ролей**: / — alert×2, radiogroup, status; /stats — alert, img×2 (канвасы),
  status×2, region; /settings — tablist, tab×3, tabpanel×3, radiogroup×6, radio×24,
  status×4, group, alert; 404 — alert. Неизвестных/опечатанных ролей: 0.
- **Контекстные требования**: все tab внутри tablist с aria-selected и aria-controls;
  tabpanel поименованы; radio — все с aria-checked и внутри radiogroup; radiogroup/
  group/region поименованы (через aria-labelledby — потому aria-label пуст, имя
  резолвится); listitem/row вне контейнеров: 0; aria-hidden на фокусируемых: 0;
  progressbar без value: 0.
- **Живое состояние seg-controls** (SSR отдаёт все radio aria-checked=false — состояние
  знает только клиент из localStorage): после init во всех 6 видимых radiogroup на
  всех вкладках ровно по одному aria-checked=true. Инвариант single-selection
  выполняется.

Вывод: ARIA-словарь грамматически корректен, фиксов не требуется. (alertdialog
promptModal — JS-территория, покрыт R0.83.)

### cr.113 — 1.4.11 Non-text Contrast: полярные палитры swiss + claude, обе темы — ЧИСТО (R1.17)

Углубление размерности R1.07 (там — только editorial): те же замеры на двух полярных
палитрах, по протоколу R0.58 (верификация data-design/data-theme после каждого reload)
и с уроком №6 (замер checked-состояния после 450ms, не rAF).

| Комбинация | бейдж vs карточка | текст бейджа | checked-рамка vs фон | фокус-кольца |
|---|---|---|---|---|
| swiss dark | 15.31 | 16.91 | 7.85 | 5.78 |
| swiss light | 18.0 | 19.8 | 7.81 | 4.58 |
| claude dark | 10.57 | 14.73 | 8.29 | 7.35 |
| claude light | 18.43 | 16.72 | 5.98 | 4.27 |

Все индикаторы состояния и фокус-кольца ≥3:1 с запасом (мин. 4.27). Обоснованная нота:
дельта checked/unchecked-рамки в swiss light = 2.53 — supplementary-сигнал, основной
индикатор состояния (бейдж-заливка 18:1) и checked-рамка vs фон (7.81) проходят с
многократным запасом; 1.4.11 требует контраст индикатора к смежным цветам, не дельту
двух состояний между собой.

Покрытие 1.4.11 теперь: editorial + swiss + claude × 2 темы (3 из 11 дизайнов —
дефолт + оба полярных). Остальные 8 — token-only вариации тех же структурных правил;
эскалация только при жалобе/изменении токенов. localStorage возвращён к дефолтам
(design+theme сняты, страница перегружена на editorial/auto).

### cr.112 — Якорная навигация, sticky-перекрытие, back-to-top фокус — ЧИСТО (R1.16)

Новая размерность: перекрытие целей якорей/фокуса липким хромом (2.4.1/2.4.11-подобное)
и фокус-менеджмент кнопки «наверх».

- **Липкого page-хрома нет**: masthead на /, /stats, /settings — position: relative
  (не sticky/fixed) → цель skip-link и якоря физически не могут прятаться под шапкой;
  scroll-margin-top не требуется. Skip-link → #main-content (tabindex="-1"), фокус
  садится на MAIN, цель у верхней кромки.
- **Sticky-заголовки таблицы /stats — вертикально инертны**: th имеют position: sticky;
  top: 0, но живут внутри overflow-x-обёртки → липнут только в её скролл-контексте,
  а при прокрутке СТРАНИЦЫ уезжают вместе с таблицей (проверено: thBottom=2 при
  скролле). Сфокусированные строки никогда не оказываются под заголовком. Свёрнутые
  tr[hidden] — display: none → их ссылки вне tab-порядка (не фокус-ловушки).
- **Back-to-top**: появление направленное (только при скролле вверх — осознанный
  паттерн из R0-раундов); после активации скролл на 0 и **фокус переносится на MAIN**
  (не теряется в body), кнопка прячется. Образцовый фокус-менеджмент.

Методическая заметка: links[50] дал height=0 — зонды по таблице брать только из
видимых строк (getClientRects), свёрнутые tr[hidden] дают мусорную геометрию.

Вывод: фиксов не требуется.

### cr.111 — Эргономика текстового ввода + допроверка input#count — ЧИСТО (R1.15)

Новая размерность: мобильная эргономика свободных полей ввода (type / inputmode /
enterkeyhint / autocomplete / autocapitalize / spellcheck) по SSR всех страниц.

- **Свободных полей во всём проекте два.** Поиск на /stats — образцовый: type="search" +
  enterkeyhint="search" + autocomplete/autocapitalize=off + spellcheck=false (мобильная
  клавиатура с кнопкой «Найти», без автокоррекции по русским термам). Счётчик вопросов
  #count на /settings — type="number" (цифровая клавиатура), min=1 max=200, label
  «Вопросов:».
- **Коррекция R1.11**: утверждение «number-инпутов в проекте ноль» было неверным —
  visible-only фильтр пропустил #count, скрытый при дефолтном режиме ТРЕНИРОВКА
  (initSessionModeForm прячет счётчик). cr.107 аннотирован. Урок методики: для
  инвентаризации контролов сканировать SSR-DOM (DOMParser), живой visible-фильтр —
  только для проверки видимых состояний.
- **Допроверка валидации #count живьём** (STUDY-режим открывает поле): 999 → «Значение
  должно быть меньше или равно 200», 0/-5 → «…больше или равно 1», 42 → валидно.
  Смена режима подставляет дефолт из data-default-count (STUDY→50), CTA переименовывается
  («Начать тренировку» ↔ «Начать сессию»). Всё восстановлено (TRAINING/20, поле скрыто).

Вывод: обе точки свободного ввода настроены правильно; вердикт R1.11 по 3.3 подтверждён
теперь на полной поверхности. Фиксов не требуется.

### cr.110 — Мета-гигиена документа + bfcache/кэш-политика — ЧИСТО (R1.14)

Новая размерность: заголовки страниц (2.4.2), мета-каркас, таймерные приёмы (2.2.x),
bfcache-killers и HTTP-кэш статики.

- **Титулы** уникальны и по паттерну «Раздел — Подготовка к собеседованию»; 404 честно
  озаглавлен «Ошибка — …». 2.4.2 чисто.
- **Viewport** `width=device-width, initial-scale=1.0` — без user-scalable=no /
  maximum-scale: пинч-зум не заблокирован (поддержка 1.4.4/1.4.10).
- **theme-color адаптивный**: #262624 в тёмной (равен фону страницы), inline-скрипт head
  обновляет мету при переключении темы — мобильный браузер-хром следует теме.
- **favicon/charset есть; meta refresh нет** (2.2.1/2.2.4 — автообновлений/редиректов
  по таймеру нет нигде).
- **bfcache-killers отсутствуют**: ни одного beforeunload/unload/onunload во всём JS.
  HTML отдаётся с Cache-Control: no-store — осознанная политика для динамичного
  сессионного состояния (back не должен показывать устаревший вопрос), к тому же
  X-Content-Type-Options: nosniff на месте.
- **Статика**: max-age=86400 + must-revalidate + Last-Modified при версионированных URL
  (v=91) — корректно; `immutable` было бы чуть лучше, но это бэкенд-конфиг (вне скоупа,
  наблюдение без правки).
- **meta description отсутствует** — обоснованный не-дефект: внутренний тренажёр, SEO
  не является целью.

Вывод: мета-каркас и кэш-политика здоровы; фиксов не требуется.

### cr.109 — Целостность ID и ARIA-ссылок: 4 страницы + JS-состояния — ЧИСТО (R1.13)

Новая размерность: дубликаты id (ломают label[for], якоря и все aria-ссылки молча) и
висячие ссылочные атрибуты — aria-labelledby / describedby / controls / activedescendant /
details / errormessage, label[for], td[headers] — с резолвом каждого id-токена.

- **SSR-скан** (fetch + DOMParser): / (37 id), /settings (78 id — все панели и контролы
  вкладок), /stats (38 id live), 404 (26 id) — 0 дублей, 0 висячих ссылок, 0 label-сирот.
- **Живые JS-состояния /settings**: после init js-tabs, на каждой из трёх вкладок —
  0 дублей, 0 висячих (aria-controls вкладок корректно резолвятся в панели).
- Оговорка: kbd-help-оверлей на синтетический Shift+? не открылся (гейт/особенность
  синтетических событий) — его внутренняя ARIA-целостность покрыта модальными аудитами
  R0.76/R0.83 и здесь не перепроверялась.

Вывод: ссылочный ARIA-граф целостен во всех проверенных состояниях; фиксов не требуется.

### cr.108 — Печать /stats и /settings: аудит + фикс фильтр-карточки (R1.12)

Расширение print-размерности на оставшиеся страницы (R0.90 закрывал focus-страницу).
Метод: полная инвентаризация @media print (17 правил) + сверка живого DOM — какой
интерактивный хром НЕ попадает под print-скрытие (el.matches по цепочке предков).

- **/stats — найден и закрыт 1 дефект**: карточка фильтров/поиска (`.stats-filter-card`:
  2 селекта, чекбоксы «важные»/«только ошибочные», кнопки «Применить»/«Искать», поле
  поиска) печаталась как пустые рамки без функции. Добавлен селектор в print hide-list
  (base.css, рядом с chart-card), v=91. Верифицировано CSSOM на served v=91:
  print-видимого хрома на /stats теперь 0, таблица и stat-карточки печатаются
  (tr[hidden] по-прежнему разворачиваются).
- **/settings — осознанно без правок**: печать отдаёт tablist + активную панель.
  Tablist оставлен намеренно — заголовок панели под js-tabs sr-only-клипнут, и именно
  tablist даёт печатному листу контекст «какая секция открыта». Прятать tablist без
  расклипа заголовка = лист без контекста; расклип + скрытие — избыточная инженерия
  для экзотического сценария печати настроек.

Остальной print-каркас подтверждён здоровым: ч/б-нормализация, break-inside по строкам,
скрытие чарт-карточек (битмап тёмной темы) и оверлеев.

### cr.107 — Предсказуемость форм + constraint-валидация (3.2.2/3.3) — ЧИСТО (R1.11)

Новая размерность: смена контекста на input (3.2.2), нативная constraint-валидация и
сообщения об ошибках (3.3.1/3.3.3) на / и /settings (все вкладки).

- **3.2.2 On Input**: в app.js/stats.js ровно два submit-вызова — оба явные пользовательские
  действия (finishForm.submit() по клику «завершить» с CSRF-переносом; form.requestSubmit()
  после модалки «Подтвердить»). Ни одного submit на change/input; селекты и seg-controls
  не вызывают навигацию. Чисто.
- **novalidate отсутствует** во всех шаблонах — нативная валидация активна.
- **[СКОРРЕКТИРОВАНО в cr.111 — visible-only фильтр пропустил input#count (скрыт при дефолтном TRAINING); number-инпут существует, min=1 max=200, валидация работает]** **Поверхность ошибок минимальна by design**: на /settings свободного ввода нет вообще
  (seg-controls, селекты, чекбоксы — невалидное состояние сконструировать нельзя,
  error-prevention через конструирование); number-инпутов в проекте ноль.
- **Единственный constraint** — required-radio в MCQ-форме (/answer): без выбора
  form.checkValidity()=false, нативное локализованное сообщение «Выберите один из
  вариантов.», submit-кнопка нативная (валидационный путь браузера не обойдён),
  все radio с label. Пустой сабмит невозможен.

Вывод: фиксов не требуется. Проверено без единого POST (checkValidity submit-free,
значения восстановлены, settingsTab снят).

### cr.106 — Устойчивость к недоступному localStorage (private/lockdown) — ЧИСТО (R1.10)

Новая размерность: поведение при SecurityError на любом обращении к storage (приватный
режим Safari/Firefox, enterprise-политики, заблокированные куки).

**Статический аудит** всех точек касания (grep по static/js + templates): head.html
pre-paint IIFE — все 7 осей читаются в try/catch с фоллбэками (editorial/auto/flow/
auto/standard/spacious/1) и все записи+CustomEvent-диспатчи обёрнуты; app.js — счётчик
сессии (275/283), админ-токен (335–337), settingsTab (660, 679–682), learningPrefs
(1047–1068) — всё в try/catch с дефолтами. settings.html — только комментарий.
Незащищённых обращений: **0**.

**Живая симуляция** на /settings: Object.defineProperty подменяет localStorage на
кидающий SecurityError, затем UI-прогон — переключение вкладок (aria-selected
обновляется), design-переключатель (editorial→linear→editorial: data-design применяется
живо), тумблер темы (dark→light→dark: применяется на сессию). **Uncaught-ошибок: 0**;
деградация ровно желаемая — всё работает в рамках вкладки, персист молча пропускается.

**Tooling-урок №7:** в шапке ТРИ кнопки делят класс ed-theme-toggle (раскладка, дизайн,
тема) — селектор `.ed-theme-toggle` цепляет тумблер раскладки первым в DOM; целиться
по `.theme-toggle` (уникален) или aria-label. Первый прогон кликал не ту кнопку.

Реальный localStorage не тронут (записи под lockdown кидались), страница перезагружена.

### cr.105 — WCAG 1.4.13 hover/focus-контент + 1.1.1 canvas-альтернативы — ЧИСТО (R1.09)

Новая размерность: авторский hover-контент, нативные title-тултипы и текстовые
альтернативы Chart.js-канвасов на /stats (заодно скан CSSOM всех :hover-правил).

- **Авторский hover-reveal ровно один**: кнопка копирования кода (opacity 0→1). Полный
  селектор: `.code-copy-wrap:hover .code-copy-btn, .code-copy-wrap:focus-within .code-copy-btn`
  — клавиатурный путь есть в том же правиле, 1.4.13/2.1.1 выполнены. Никаких авторских
  тултипов/поповеров по hover в проекте нет.
- **1608 title-атрибутов** (5 на каждую из 319 строк таблицы топиков + stat-карточки +
  тумблеры + th). Нативные title-тултипы рисует user agent → из 1.4.13 они исключены
  (Understanding: SC покрывает только авторский контент). Проверена доступная избыточность:
  ссылка темы — видимый текст + title-слуг (бонус); прогресс-бары — данные видимы в ячейке
  «Прогресс» (0/30), title лишь аннотирует; stat-карточки — видимая метка + число, title
  расшифровывает; th «Точность» — подписан текстом, title advisory. Title-ONLY имён нет
  (подтверждено в R1.08). Best-practice нота: расшифровка «—» в ячейках доступна только
  по hover — незначительно (тире = универсальный «нет данных», th объясняет метрику).
- **Chart.js canvas 1.1.1**: оба канваса (topicProgressChart, topicAccuracyChart) —
  role="img" + информативный русский aria-label с отсылкой «Полные данные — в таблице ниже»;
  таблица (319 строк) — полная текстовая альтернатива. Канвасы сейчас скрыты (нет данных
  после wipe 2026-07-08) — data-gated состояние, не дефект.

Вывод: фиксов не требуется; hover-гигиена и canvas-альтернативы образцовые.

### cr.104 — WCAG 4.1.2 Name/Role/Value: accessible names на 4 страницах — ЧИСТО (R1.08)

Новая размерность: скан всех интерактивных элементов (a[href], button, input, select,
textarea, role=button/tab/link, [tabindex]) на четыре класса дефектов: пустое accessible
name, имя только из title-атрибута (не читается с тача/клавиатуры), имя только из
placeholder, фокусируемые элементы внутри aria-hidden="true", положительный tabindex.
Каскад имени: aria-label → aria-labelledby (с резолвом id и проверкой на пустоту) →
label[for] → текст → svg title → img alt.

- **/** — 18 элементов, 0 проблем.
- **/stats** — 347 элементов (таблица топиков с сортировкой и ссылками), 0 проблем.
- **/settings** — 58 элементов, все 3 вкладки (Сессия/Оформление/Данные), 0 проблем.
- **404 (error.html)** — 11 элементов, 0 пустых имён.

Итого 434 интерактивных элемента — ни одного безымянного, ни одного title/placeholder-only,
ни одного фокусируемого под aria-hidden, ни одного положительного tabindex. Иконочные
кнопки (наверх, копирование кода, ?-справка) все с aria-label — система иконок Lucide
из fragments/icons.html последовательно подписана. localStorage возвращён к дефолтам.

### cr.103 — WCAG 1.4.11 Non-text Contrast: editorial, обе темы — ЧИСТО (R1.07)

Новая размерность: контраст ≥3:1 для нетекстовых индикаторов (границы компонентов,
индикаторы состояния, фокус-кольца) на живой `/` (editorial), canvas-нормализация oklch.

**Dark:** checked-бейдж vs карточка 9.93; текст бейджа vs бейдж 13.05; checked-рамка vs фон
8.42 / vs карточки 6.41; дельта checked/unchecked рамки 6.12; фокус-кольца radio 4.24, btn 4.86.
**Light** (localStorage theme=light, `data-theme` верифицирован по протоколу R0.58):
checked-бейдж 13.31; текст бейджа 17.5; checked-рамка 6.26; дельта 4.22; фокус-кольца 3.85/3.85.
Все замеры ≥3:1.

**Обоснованный не-дефект:** рамка НЕвыбранной MCQ-карточки и primary-кнопки ≈1.37 vs фон —
ниже 3:1, но по Understanding 1.4.11 контур hit-области не обязан контрастировать, когда
компонент опознаётся другими средствами (текст опции, литера-бейдж A/B/C/D, текст кнопки);
требование распространяется на индикаторы СОСТОЯНИЯ — они все ≥4.22.

**Tooling-урок №6:** первый замер checked-состояния дал ложные дельты ≈1.0 — у карточки
transition на цветах, и через 2 rAF после click переход только начался. Замерять состояние
после таймаута ≥ transition-duration (400ms) или transitionend, НЕ после rAF. Диагностика —
дифф computed-стилей checked-элемента против соседнего unchecked (9 реальных дельт).

localStorage возвращён к дефолту (theme снят, страница перезагружена в auto).

### cr.102 — Структура документа: заголовки + landmarks на 4 страницах — ЧИСТО (R1.06)

Новая размерность: иерархия заголовков (WCAG 1.3.1/2.4.6) и landmark-структура через
DOMParser-скан server-side HTML (fetch без JS-мутаций — проверяется именно SSR-каркас).

- **/, /stats, /settings** — ровно один h1 на страницу («Подготовка к собеседованию» /
  «Аналитика» / «Настройки сессии»), пропусков уровней h1→h6 нет, banner и main уникальны,
  nav один с aria-label «Основная навигация». Единственный элемент вне landmarks —
  `a.skip-link` первым child body: канонический WAI-паттерн, не дефект.
- **contentinfo: 0 на всех страницах** — следствие отложенного решения §5 (футера нет);
  не переоткрываю.
- **/focus-training → 404** — не дефект: шаблон focus-training.html это view главной `/`
  (MvcNavigationService.focusView), отдельного роута нет и не должно быть.
- **404-страница (error.html)** — структура валидна: один h1, banner/main, два nav с
  различимыми aria-label («Основная навигация» и «Что дальше» с 3 ссылками) — WCAG 2.4.1
  ARIA-требование «повторяющиеся landmark-роли различать имена» выполнено.

Вывод: SSR-каркас всех доступных GET-страниц структурно чист; фиксов не требуется.

### cr.101 — Регрессион-рескан фикс-сетов R0.90/R0.93/R0.94 — все три держатся (R1.05)

Свежая проверка трёх последних кодовых фикс-сетов на served base.css v=90 (CSSOM по selectorText —
урок №1, не через r.cssRules):

- **R0.90 print-гигиена focus-страницы** — все 11 селекторов hide-списка присутствуют в
  `@media print` (topbar, action-footer, keyboard-hint, flashcard-бейдж/форма/грейды/саммари,
  question-code-summary, confidence-кнопки, extra-analysis-toggle, empty-actions). 11/11.
- **R0.93 forced-colors** — все 5 правил в `@media (forced-colors: active)`: checked-опция
  (рамка + ::before бейдж Highlight/HighlightText), confidence-btn.selected, прогресс
  (track CanvasText border + fill Highlight). 5/5.
- **R0.94 thead:focus-within** — функциональный тест на /stats @375px: thead стекированной
  таблицы в покое 1×1px clip rect(0,0,0,0) → фокус в сортируемую кнопку th → 134×246px,
  clip:auto, кнопка видима во вьюпорте → blur → клип восстановлен. Полный цикл работает.

Дрейфа нет; v-пин head.html отдаёт 90. Вьюпорт эмуляции возвращён на 1280.

### cr.100 — WCAG 1.4.12 Text Spacing: live-прогон / + /stats + /settings — ЧИСТО (R1.04)

Новая размерность аудита: стандартный оверрайд 1.4.12 (line-height 1.5, letter-spacing 0.12em,
word-spacing 0.16em, margin-bottom абзацев 2em) инъекцией `<style>` в живые страницы, скан всех
текстонесущих элементов на clip-y (overflow hidden + scrollHeight > clientHeight), clip-x без
text-overflow: ellipsis, viewport-переполнение вне overflow-x:auto скроллеров и hscroll документа.

- **/** — hscroll false, 0 проблем.
- **/stats** — 2 флага, оба `caption.visually-hidden` таблицы топиков: sr-only-клип (1px-паттерн),
  контент невидим по определению → не дефект 1.4.12.
- **/settings** (все 3 вкладки: Сессия / Оформление / Данные) — по 2 флага на вкладку, все на
  `h2.settings-panel-title`: под js-tabs заголовок панели визуально скрыт тем же sr-only-клипом
  (base.css:1358, задокументированное решение — дублирует активную вкладку) → не дефект.

Вывод: реальных обрезок текста при спейсинг-оверрайде нет ни на одной из трёх страниц; вся
типографика и контейнеры выдерживают 1.4.12 без фиксов. sr-only-элементы — единственный класс
срабатываний сканера, добавлен в исключения методики (фильтр по классу visually-hidden/sr-only).
localStorage тест-браузера возвращён к дефолтам (settingsTab снят).

- **cr.99 (ROUND-RESET, R1.03) — Сетевой аудит статики (3 страницы, холодная загрузка ignoreCache): VERIFIED-CLEAN (0 правок).** Новая размерность — целостность ресурсной цепочки. **(1) Статусы**: 47 запросов на /, /stats, /settings — ни одного не-200 (нет битых ссылок на шрифты/иконки/скрипты; /api/streak 200 для today-чипа). **(2) CDN-гигиена**: все три внешние зависимости exact-pin + SRI + crossorigin (mermaid 11.12.0, chart.umd 4.5.0, highlight 11.9.0) — соответствует комментарию-политике в head.html:312. **(3) Условная загрузка работает правильно**: mermaid — только на / (серверный гейт ${includeMermaid}, head.html:344, с graceful-fallback dropAllDiagrams при блокировке — offline/CSP/ad-blocker не роняют страницу); chart.js + stats.js — только на /stats; app.js — на /+/settings, НЕ на /stats. Ни одного лишнего бандла ни на одной странице. **(4) Кэш-бастинг**: v-параметры собственной статики консистентны (tokens v62 / base v90 / app v60 / stats v14) — все совпадают с пинами в шаблонах. **Наблюдение (НЕ дефект, не трогаю)**: fonts css2-запрос грузит все 8 семейств сразу (~11 woff2 на холодную загрузку) независимо от активного дизайна — осознанная плата за мгновенный дизайн-свитч без FOUT; display=swap стоит. Оптимизация (сплит по data-design) потребовала бы пер-дизайн препаять запросы — риск/выгода не в пользу правки в поддерживающем режиме. Правок нет. Контракт-тест PENDING. Дальше: window-тик при свободном :8080.

- **cr.98 (ROUND-RESET, R1.02) — Регрессия-ре-скан зоны R0.78 (whitelist-валидация 8 осей localStorage): БЕЗ ДРЕЙФА 8/8 (0 правок).** Мусор во все 8 ключей одним заходом (XSS-пейлоад в design, «neon» в theme, path-traversal-строка в layout, «MAXIMUM» в motion, «yes» в readingWidth, «9001» в density, 1e10 в fontScale, «javascript:...» в settingsTab) → reload → все оси в штатных дефолтах: design=editorial, theme=auto→dark (система), layout=flow (явный фоллбэк readLayout:125 — единственная ось с дефолт-атрибутом, так по проекту), motion/readingWidth/density=атрибут отсутствует, fontScale=clamp до 1.4 (140%, root 22.4px), вкладка=Сессия; DOM-инъекции нет. Pre-paint-валидация пережила 3 инкремента base.css (v87→90) и все правки head.html раунда (5 касаний — как раз того файла, где живут whitelists!) без регрессий — это был главный мотив выбора зоны. localStorage возвращён (8/8 ключей удалены). Правок нет. Контракт-тест PENDING. Регрессионная фаза: R0.76 ✓ (R1.00), R0.83 ✓ (R1.01), R0.78 ✓ (тут); остаток — R0.94 thead-фокус (свежий), R0.90 print (свежий), R0.93 forced-colors (статичен). Дальше: window-тик при свободном :8080, иначе завершающие ре-сканы свежих зон.

- **cr.97 (ROUND-RESET, R1.01) — Регрессия-ре-скан зоны R0.83 (модалки promptModal): БЕЗ ДРЕЙФА 3/3, но с детективом — урок тулинга №5 (rAF-троттлинг фоновой вкладки).** Итоговый чистый прогон confirm-модалки «Сбросить банк вариантов» (×3, три разных пути закрытия — Отмена-клик, Esc с фокусного элемента, клик по фону): count=1, role=alertdialog, inert на 4 сиблингах, фокус внутри за 1 кадр, закрытие/inert-очистка/возврат фокуса — всё зелёное во всех циклах. Машинерия ЖИВА, R0.83-выводы подтверждены. Детектив: промежуточные пробы дали каскад ЛОЖНЫХ «провалов» (стопка из 3 оверлеев, «незакрывающийся Esc», «несадящийся фокус») — все три оказались артефактами моих же проб: (a) Esc, dispatched на document, не доходит до хэндлера, висящего на overlay — реальный keypress рождается на сфокусированном элементе ВНУТРИ диалога и всплывает через него (клавиатурный аналог урока R0.83 про клики); (b) **rAF-троттлинг**: у фоновой/невидимой вкладки автоматизации requestAnimationFrame может не тикать, а setTimeout тикает → «подожди 120мс и проверь фокус» видит фокус до rAF-колбэка; замеры rAF-зависимого поведения выравнивать по await raf(), не по wall-clock (урок №5); (c) stale-селектор: querySelector('.prompt-overlay') в стопке берёт СТАРЕЙШИЙ — брать последний. Стопка воспроизводима только программным .click() сквозь inert (реальный пользователь не может — подтверждение R0.83). Правок нет. Страница оставлена чистой (0 оверлеев, 0 inert). Контракт-тест PENDING. Следующий ре-скан: R0.94 (thead:focus-within) или R0.78 (whitelists).

- **cr.96 (ROUND-RESET, R1.00) — Регрессия-ре-скан зоны R0.76 (хоткеи «?»/«/» с radio/checkbox): БЕЗ ДРЕЙФА 6/6 (0 правок).** Первый тик регрессионной фазы (по сводке R0.99: read-only размерности исчерпаны, гейты закрыты — ре-сканим ранее фиксированные зоны против дрейфа). Зона выбрана как самая ранняя JS-правка раунда, пережившая с тех пор больше всего чужих касаний (app.js v58→60: удаление apiPost, комментарий-фиксы; base.css v87→90). Живой прогон: **/** — «?» с фокуса на MCQ-radio открывает справку ✓, Esc закрывает ✓, гард текстового поля глушит «?» ✓ (временный input, удалён); **/stats** — «?» с чекбокса фильтра открывает ✓, Esc ✓, «/» с чекбокса прыгает в .search-input ✓. Ни одного клика по submit/POST — только клиентские хоткеи и фокус. Итог: R0.76-фикс (исключение radio/checkbox из input-гарда в обоих хэндлерах) работает в обоих бандлах (app.js + stats.js), рефакторинги раунда его не задели. Правок нет. Контракт-тест PENDING (гейт живого bootRun). Следующий ре-скан-кандидат: зона R0.83 (модалки/inert/Esc-дренаж) или R0.94 (thead:focus-within на 375px после v90).

- **cr.95 (ROUND-RESET, R0.98) — Язык страницы/частей (3.1.1, 3.1.2) + console-чистота: VERIFIED-CLEAN (0 правок).** **(1) Язык**: все 6 шаблонов несут <html lang="ru"> (3.1.1 pass). По 3.1.2 (language of parts, AA): англоязычные фрагменты в контенте — имена тем («Agentic patterns», «Kotlin»), код-сниппеты, inline-термины (`Kafka`, `JVM`) — попадают под явные изъятия критерия («technical terms», проприетарные имена); проза русифицирована (Lane 5/6 корпуса). Размечать их span lang="en" НЕ нужно и вредно: скринридер прыгал бы между голосами на каждом термине внутри русского предложения — осознанно не делаем, зафиксировано. **(2) Console-суип** (обе части после полной перезагрузки): / — 0 сообщений; /settings — 0; /stats — только известный CSP-шум sourcemap chart.umd.min.js.map (connect-src 'self'; классифицирован ранее, решение о self-host Chart.js — за пользователем, §5 не переоткрываю). 0 JS-ошибок, 0 warning'ов на всех трёх страницах. Правок нет. Попутно в git status появился чужой WIP (seed/mcq kotlin-interview.json — параллельная MCQ-сессия) — не тронут, мои коммиты explicit-pathspec. Контракт-тест PENDING. Размерности углубления близки к исчерпанию по read-only поверхностям — следующий тик: window-тик (приоритет №1, накоплено 5 касаний head.html + 3 правки base.css) либо ре-скан сводного статуса плана §7/§28.

- **cr.94 (ROUND-RESET, R0.97) — ::selection-контраст (22 комбо) + консистентность навигации (3.2.3): VERIFIED-CLEAN (0 правок) + третий урок тулинга (oklch).** **(1) ::selection** (base.css:223 — accent-wash фон + text-primary): in-browser прогон всех 11 дизайнов × 2 темы переключением data-атрибутов (без перезагрузок; localStorage не трогался, атрибуты возвращены). Первый замер дал «провал instrument 1.32–1.36» — оказался ЛОЖНЫМ: instrument единственный задан в oklch(), а наивный regex-парсер считал компоненты oklch как RGB-каналы. Перезамер с канвас-нормализацией (fillStyle+getImageData — конвертирует любой CSS-цвет в честный sRGB): instrument light 14.71, dark 11.79. Итог: 22/22 комбо ≥10:1 — выделение текста читаемо во всех палитрах. **Урок тулинга №3** (к nested-CSS cr.91 и getClientRects cr.92): computed-цвета из oklch-токенов приходят строкой oklch(...) — числовой парсинг цветов ТОЛЬКО через canvas. **(2) Консистентность навигации (3.2.3)**: DOM-сравнение шапки трёх страниц через fetch+DOMParser — nav-порядок (Фокус|Аналитика|Настройки), порядок тогглов (раскладка|дизайн|тема), landmark-структура (header:1 nav:1 main:1) идентичны; skip-link контекстный per-page (хорошая практика, не нарушение — 3.2.3 про относительный порядок, он сохранён); титулы уникальны (2.4.2 попутно). Правок нет. Контракт-тест PENDING. Дальше: window-тик / lang-of-parts.

- **cr.93 (ROUND-RESET, R0.96) — Эргономика полей форм на мобиле: VERIFIED-CLEAN (0 правок).** Живой аудит всех полей ввода на 375×812 по четырём осям: (1) **iOS-zoom**: computed font-size каждого видимого поля ≥16px — /settings 4 селекта (group/topic/ordered/mode), /stats 2 селекта + поиск, MCQ-лейблы 16px — ни одно поле не спровоцирует авто-зум Safari; (2) **лейблинг**: 100% полей имеют label/aria-label (placeholder «Поиск по вопросам…» — дополнение, не замена лейбла; антипаттерн placeholder-as-label отсутствует); (3) **семантика ввода**: поиск — честный type=search + enterkeyhint="search" (мобильная клавиатура покажет кнопку «Найти»); number/text-полей нет вовсе — вся конфигурация на селектах/радио/чекбоксах, значит inputmode-дыр нет по построению; (4) **радио MCQ**: 4/4 в label с текстом + aria-label, 0 сирот. Autocomplete-атрибуты не нужны: ни одно поле не собирает персональные данные (WCAG 1.3.5 N/A). Правок нет. Вьюпорт возвращён 1280. Контракт-тест PENDING (5 касаний head.html). Дальше: window-тик при свободном :8080; из размерностей — lang-of-parts, ::selection-контраст, консистентность навигации (3.2.3).

- **cr.92 (ROUND-RESET, R0.95) — Focus-order аудит (2.4.3): VERIFIED-CLEAN на /, /stats, /settings (0 правок).** Живой проход: positive tabindex — 0 на всех страницах (порядок = DOM, никто не перекраивает); skip-link — первый tabbable везде («Перейти к вопросу/аналитике»); монотонность tab-последовательности по вертикали проверена детектором «прыжков назад >60px». Все сработки детектора разобраны и оказались НЕ дефектами: (1) /stats «Применить фильтры»→«Поиск» и /settings «Применить фильтры»→session-form — двухколоночные раскладки в одной карточке (фильтры 121–601, сессия 665–1145, один top 487): обход «закончи левую колонку → правая» и есть правильный логический порядок; (2) «Наверх» рано в DOM (header.html appendChild срабатывает при парсинге шапки, кнопка ложится между header и main) — но в скрытом состоянии visibility:hidden → НЕ фокусируема (проверено btn.focus(): не садится), а в видимом состоянии позиция «после шапки» для utility-кнопки смысла не ломает — осознанно оставлено, зафиксировано; (3) фантомные «контролы других вкладок» /settings — артефакт моего фильтра. Скрытые панели действительно выключены из tab-порядка (24/41/17 tabbable по вкладкам против 53 сырых). Два урока тулинга в копилку (к nested-CSS уроку cr.91): computed display потомка НЕ отражает display:none предка — фильтровать по getClientRects().length; у visibility:hidden клиентские rect'ы ЕСТЬ — tabbability проверять фокусом или computed visibility. Правок нет. Контракт-тест PENDING. Дальше: window-тик (5 касаний head.html накопилось) или новая размерность.

- **cr.91 (ROUND-RESET, R0.94) — Target-size аудит (WCAG 2.5.8) на 375px + найден и исправлен невидимый клавиатурный фокус в стек-таблице /stats (base.css v=90).** Живой замер всех интерактивных элементов (a/button/input/select/summary/tab/radio-label) на трёх страницах, вьюпорт 375×812. **/: 0 под-24px**; 6 элементов 40px высотой (nav-ссылки + тогглы шапки) — выше AA-минимума 24px, spacing-исключение с запасом; проектная планка 44px не выдержана только шапкой (осознанный компромисс плотности, не трогал). **/settings: 0 под-24px по всем трём вкладкам** (44px-минимумы вкладок/сег-кнопок работают). **/stats: сырой скан дал ложные «провалы» th 22px/1px** — разбор показал, что ≤760px таблица тем становится стек-карточками, а thead скрыт sr-only-клипом (:3689) — th не тап-цели вовсе. Но у этого нашёлся РЕАЛЬНЫЙ побочный дефект: сортируемые th (tabindex=0) остаются в tab-порядке внутри клипнутого thead → 5 фокус-стопов без видимого кольца на узких экранах (2.4.7 Focus Visible). Фикс — паттерн skip-link: thead:focus-within разклипывается (заголовки стопкой, текущий с кольцом), при уходе фокуса скрывается обратно; сортировка и live-анонс работали и раньше. Проверено живьём: clip 1px → 246px на фокусе → 1px на blur, фокус садится. Топик-ссылки 17px в карточках — pass через spacing-исключение (единственная цель в карточке, соседи далеко). Попутный урок тулинга: CSSStyleRule.cssRules в nested-CSS браузерах существует (пустой) — сканер правил через `if (r.cssRules) recurse` молча пропускает ВСЕ плоские правила; матчить по selectorText. Вьюпорт возвращён 1280. v-бамп 89→90, live-sync. Контракт-тест PENDING.

- **cr.90 (ROUND-RESET, R0.93) — Forced-colors аудит: 3 реальных дефекта НАЙДЕНЫ и ИСПРАВЛЕНЫ (base.css v=89).** Углубление существующего блока @media (forced-colors: active) (:345 — покрывал только box-shadow-фокус и seg-btn.is-active). Систематический проход по категориям HC-уязвимостей (state-only-background, hidden-native-control, borderless-bar): **(1) КРИТИЧНО — выбранный MCQ-вариант неотличим**: нативный radio спрятан sr-only-паттерном (:784), состояние checked кодируется только цветом (border/wash/бейдж accent, :787-795) → в HC все рамки одинаково CanvasText, фоны Canvas — пользователь не видит, что выбрал; это ДО-ответный основной интеракшн. Фикс: бейдж-буква и рамка выбранного — системной парой Highlight/HighlightText (паттерн seg-btn). **(2) confidence-btn.selected** — та же болезнь (border+wash, :2737) → Highlight-пара. **(3) Прогресс-бар сессии исчезает целиком**: track и fill — чистые background без рамок (:660, :668) → оба Canvas; фикс: track border CanvasText, fill Highlight (текст «N/M» рядом был и остаётся — но видимый индикатор терять незачем). НЕ-дефекты (проверено, не трогал): вкладки /settings — underline-состояние переживает HC (transparent сохраняет альфу, accent→CanvasText остаётся видим); option-correct/wrong — есть текстовые статус-лейблы (не color-only); option-dimmed — opacity в HC работает; zone-chip/бейджи — читаемый CanvasText. Верификация: braces 0, CSSOM v=89 — все 5 новых правил (вкл. :has()) распарсены; полная HC-эмуляция MCP-тулингом недоступна (emulate только colorScheme) — уровень верификации зафиксирован. v-бамп 88→89, live-sync. Контракт-тест PENDING (+head.html). Дальше: window-тик, либо новая размерность.

- **cr.89 (ROUND-RESET, R0.92) — Покрытие prefers-reduced-motion / оси «Движение»: VERIFIED-CLEAN (0 правок).** Статический аудит всех источников движения против двухпутевого kill-механизма (media-блок base.css:285 для auto + always-on блок :296 для data-motion=off; :not([data-motion="on"]) даёт форс-оверрайд). Проверено и закрыто: (1) CSS — единственный @keyframes (verdictReveal) и все transitions гасятся wildcard-declaration'ами duration 0.001ms/iteration 1; html scroll-behavior:smooth (:57) перебивается в обоих путях; animation-delay/transition-delay/fill-mode в кодовой базе ОТСУТСТВУЮТ → escape через delayed-fill невозможен. (2) JS вне досягаемости CSS — все три сайта реализуют один и тот же 3-way протокол (off→reduced, on→motion, auto→matchMedia): inline-alert scrollIntoView (app.js:104-108), back-to-top (header.html:181-186, читается на каждый клик — смена оси в /settings подхватывается без перезагрузки), Chart.js canvas-анимация (stats.js motionAllowed:97-102). (3) Element.animate/SMIL/View Transitions — не используются; rAF-сайты (фокус модалки, copyStatus) — тайминг, не движение. Консистентность: три JS-копии протокола идентичны по семантике (кандидат на общий хелпер — но они в разных бандлах: app.js не подключён на /stats, header-скрипт инлайновый → дублирование осознанное, DRY тут дороже). Правок нет. Live-проверка emulate media Reduce — тулингом MCP недоступна (только colorScheme), остаётся tooling-gated как и было. Контракт-тест PENDING. Кандидаты дальше: forced-colors статический аудит, window-тик.

- **cr.88 (ROUND-RESET, R0.91) — Aria-live-протокол динамических инъекций app.js/stats.js: VERIFIED-CLEAN (0 правок).** Статический аудит всех точек мутации DOM (60+ сайтов innerHTML/textContent/appendChild в app.js, 7 в stats.js + inline-экспандер stats.html) на предмет «информация меняется — скринридер не узнаёт». Инфонесущие точки ВСЕ обеспечены: вердикт #result-feedback (role=status + polite + atomic; note «+N» аппендится ВНУТРЬ региона → re-announce целиком), inline-alert (status/polite ↔ alert/assertive по severity), confidence-группа (polite, вставка после ухода фокуса — обоснование в комменте L1511), copy-status, export-status, personalization-status («Сохранено…» + atomic), filter-mode-hint (status/polite — L197/205 пишут в него), font-scale-value (сам status/polite + дублирующий анонс через personalization-status), chart-fallback ×2, table-sort-status (visually-hidden polite atomic — анонс сортировки), таймер aria-hidden (осознанно: не спамить отсчётом), today-чип без live (фоновое обновление — шум). Транзиентные лейблы кнопок («Проверяю…», MODE_CTA) — смена лейбла сфокусированного/соседнего элемента, стандартная практика, не дефект. Модалки — фокус-менеджмент (подтверждено R0.83), экспандер — aria-expanded/controls. Паттерн-гигиена проекта: показ live-регионов через .hidden-класс с записью текста ДО показа (комментарий у setExportStatus) — консистентен во всех точках. Правок нет. Контракт-тест PENDING. Кандидаты дальше: статический аудит @media prefers-reduced-motion покрытия (какие анимации не гасятся), forced-colors, либо window-тик (regenerate-removal + контракт-тесты) когда :8080 освободится.

- **cr.87 (ROUND-RESET, R0.90) — Print-аудит: у страницы вопроса не было ни одного print-правила — интерактивный хром шёл на бумагу. ИСПРАВЛЕНО (base.css v=88).** Новая размерность: @media print (строка 3328) оказался основательным для result/stats/summary (тёмная тема → белый лист, код-блоки, разрывы страниц, разворот скрытых строк таблицы), но фокус-страница — главный кандидат на «распечатать вопрос для офлайн-разбора» — не упоминалась вовсе. На печать шли: .focus-topbar (прогресс сессии, today-чип, форма «Завершить сессию»), .action-footer (submit+таймер), .keyboard-hint («1–9 — выбор…»), reveal/grade-формы флешкарты, кнопка-summary «Пример кода», .confidence-buttons и «Похожие вопросы» (пост-ответные инъекции app.js), CTA пустого состояния. Добавлен focus-блок в существующий hide-лист (11 селекторов с обоснованием в комментарии): вопрос, радио-варианты и вердикт #result-feedback остаются; открытый <details> печатает код и без кнопки; закрытая флешкарта печатает только вопрос (семантика reveal сохранена). Верификация: brace-balance 0, live CSSOM — @media print распарсен, все новые селекторы присутствуют, элементы на странице матчатся (print-эмуляция в MCP недоступна — верификация уровнем CSSOM+селектор-матч). v-бамп base.css 87→88 (head.html), live-sync в bootRun. Контракт-тест PENDING (head.html снова тронут — тем важнее window-тик). Кандидат дальше: aria-live-протокол инъекций app.js (статический аудит).

- **cr.86 (ROUND-RESET, R0.89) — Стресс-комбо reflow 320px + fontScale 1.4: VERIFIED-CLEAN (0 правок). Размерность reflow ЗАКРЫТА.** Худший законный кейс осей персонализации поверх WCAG 1.4.10: вьюпорт 320×900 + максимальный шаг шрифт-степпера (clamp-потолок 1.4 → root 22.4px, применение подтверждено computed font-size). /, /stats, /settings (все 3 вкладки клиентским переключением) — hasHScroll=false, 0 элементов за вьюпортом вне легитимных overflow-x:auto. Т.е. связка «reading-width/сеточные max-width + wrap-стратегии» держит одновременно минимальную ширину И максимальный масштаб текста — самый частый провал 1.4.4×1.4.10 (текст растёт → фикс-ширины выталкивают контейнер) здесь отсутствует. Размерность reflow закрыта: 320px дефолт (cr.85) + 320px×1.4 (тут); 375/768/1280 были чисты ранее (R0.55-серия). localStorage возвращён (fontScale null), эмуляция вьюпорта — 1280×900. Правок кода нет. Контракт-тест PENDING. Кандидаты дальше: print-стили (@media print — есть ли вообще, «Печать» из Bundle 3), aria-live-протокол динамических инъекций app.js (вердикт/extra-analysis sink — статический аудит без POST).

- **cr.85 (ROUND-RESET, R0.88) — Reflow 320px ЧИСТО + КОРРЕКЦИЯ методологии cr.83/cr.84: те 10 прогонов шли на editorial (неверные ключи localStorage); все 10 перегнаны с верификацией атрибута — 100/100, выводы восстановлены.** Две части. **(1) Новая размерность WCAG 1.4.10 (reflow при 320 CSS px ≈ zoom 400%):** / (MCQ), /stats, /settings (все 3 вкладки клиентским переключением) на эмуляции 320×900 — hasHScroll=false, 0 элементов за вьюпортом вне легитимных overflow-x:auto скроллеров. Дефектов нет. **(2) Дефект методологии, найденный по пути:** при поиске ключа fontScale обнаружено, что pre-paint читает `design`/`theme` БЕЗ префикса (head.html:45,67), а в R0.86/R0.87 я ставил `quiz-design`/`quiz-theme` — whitelist молча падал на editorial+auto, т.е. те 10 аудитов мерили editorial, а не заявленные broadsheet/stripe/claude/swiss. Урок = усиление протокола R0.58: **после reload ОБЯЗАТЕЛЬНО верифицировать `document.documentElement.getAttribute('data-design')`/`data-theme`**, а не только localStorage (whitelist-фоллбэк бесшумный — то самое свойство, что хвалили в R0.78, маскирует опечатку ключа). Все 10 прогонов перегнаны с верными ключами и live-проверкой атрибута (swiss+dark подтверждён на html): /settings swiss/claude × dark/light 4/4 — a11y 100, BP 100; / broadsheet/stripe/claude × dark/light 6/6 — a11y 100, BP 100 (broadsheet+light: CLS 0.99 — метрический блип, не a11y). Итог: выводы cr.83/cr.84 теперь подтверждены честно, размерность live-a11y (11/11 дизайнов + /settings на полюсах) закрыта на реальных данных. R0.85 и ранее не затронуты (там ключи были верные — restore-логи показывали design/theme). localStorage возвращён (design/theme null). Правок кода нет. Контракт-тест PENDING. Кандидаты дальше: стресс 320px+fontScale 1.4, print-стили, aria-live-протокол инъекций app.js.

- **[СКОРРЕКТИРОВАНО в cr.85 — прогоны шли на editorial из-за неверных ключей localStorage; перегнано, вывод в итоге подтверждён]** **cr.84 (ROUND-RESET, R0.87) — Spot-check /settings (swiss vs claude, обе темы): VERIFIED-CLEAN 4/4 (0 правок). Размерность live-a11y ЗАКРЫТА ПОЛНОСТЬЮ.** Завершение размерности: после 11/11 дизайнов на / (MCQ-страница) — контрольный прогон самой плотной интерактивной страницы /settings (ARIA-вкладки с roving tabindex, 7 сег-контролов wireSegControl, шрифт-степпер с aria-disabled) на двух самых непохожих палитрах: swiss (жёсткий ч/б контраст-язык) и claude (тёплые слоистые нейтрали). 4/4 прогона: **a11y 100, BP 100, color-contrast PASS** (50 passed-аудитов — больше, чем на / из-за плотности разметки; провалы — только SEO-шум). Вывод размерности: весь дизайн-свитч (11 палитр × 2 темы) и вся интерактивная ARIA-механика /settings держат живой axe без единого исключения; статический AA-гейт и контракт-паттерны (вкладки/radiogroup/степпер) подтверждены на обоих полюсах палитр. localStorage возвращён (design/theme null, проверено). Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков. Следующая размерность — по §28: углубление аудита (кандидаты: reflow/zoom 400%, print-стили, aria-live протокол динамических инъекций app.js).

- **[СКОРРЕКТИРОВАНО в cr.85 — прогоны шли на editorial из-за неверных ключей localStorage; перегнано, вывод в итоге подтверждён]** **cr.83 (ROUND-RESET, R0.86) — Live-a11y серия 3/3 (broadsheet+stripe+claude, обе темы): VERIFIED-CLEAN 6/6 (0 правок). Размерность ЗАКРЫТА: 11/11 дизайнов живьём.** Финал серии той же механикой (localStorage → pre-paint → LH navigation desktop на / MCQ). broadsheet, stripe, claude × dark+light — все шесть прогонов: **a11y 100, BP 100, color-contrast PASS** (провалы — только SEO-шум). Итого размерность live-a11y по дизайнам закрыта полностью: editorial, instrument (R0.58–61), notion, superhuman, theverge (R0.84), linear, swiss, mintlify (R0.85), broadsheet, stripe, claude (тут) — 11/11, обе темы, ни одного контраст-провала; статический AA-гейт design-token-audit.py подтверждён живым axe на всех палитрах. Остаток размерности: spot-check /settings (страница с самой плотной интерактивной разметкой — вкладки/сег-контролы/степпер) на паре самых непохожих палитр (swiss vs claude) — следующий тик. localStorage возвращён (design/theme null, проверено). Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.

- **cr.82 (ROUND-RESET, R0.85) — Live-a11y серия 2/3 (linear+swiss+mintlify, обе темы): VERIFIED-CLEAN 6/6 (0 правок).** Продолжение размерности R0.84 той же механикой (localStorage → pre-paint → LH navigation desktop на / MCQ). linear, swiss, mintlify × dark+light — все шесть прогонов: **a11y 100, BP 100, color-contrast PASS** (провалы — только SEO-шум). Примечательно: swiss с его агрессивным контраст-языком (чёрное/белое, 650-веса) и linear с тонкими серыми — оба держат axe в обеих темах; статический токен-аудит на этих палитрах не врал. Счёт по размерности: 8 из 11 дизайнов подтверждены живьём (editorial, instrument — серии R0.58–61; notion, superhuman, theverge — R0.84; linear, swiss, mintlify — тут). Остаток: broadsheet+stripe+claude (серия 3/3) → затем spot-check /settings на паре самых непохожих палитр. localStorage возвращён (design/theme null). Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.81 (ROUND-RESET, R0.84) — Live-a11y непроверенных дизайнов, серия 1/3 (notion+superhuman+theverge, обе темы): VERIFIED-CLEAN 6/6 (0 правок).** Пробел гейта: живой axe (Lighthouse) гонялся ТОЛЬКО на editorial (фоново во всех прогонах) и instrument (серия R0.58–R0.61) — остальные 9 дизайнов держались на статическом design-token-audit.py (CLEAN), который не видит ловушек rgba/градиентов (project_ui_verified_clean). Открыта серийная размерность: LH a11y по непроверенным дизайнам. **Серия 1:** notion (волна notion/mintlify), superhuman и theverge (token-only волна 2026-06-23) × / (MCQ, крупнейшая поверхность) × dark+light. **Результат 6/6: a11y 100, BP 100, color-contrast PASS** (единственные провалы — вечный SEO-шум meta-description/noindex, не гейт). Механика прогона: localStorage design/theme → pre-paint head.html применяет ДО первого кадра → LH меряет настоящий дизайн (проверенный паттерн R0.58). Значение: token-only дизайны второй волны впервые подтверждены живым инструментом — токен-подход (полные наборы light+dark на дизайн) держит AA не только статически. localStorage возвращён к дефолтам (design/theme = null). Остаток серии: linear+swiss+mintlify (серия 2), broadsheet+stripe+claude (серия 3) — следующие тики; потом spot-check /settings худших. Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.80 (ROUND-RESET, R0.83) — Целостность DOM-ссылок + модальная гигиена повторных циклов: VERIFIED-CLEAN, ложная «утечка» разоблачена как артефакт пробы (0 правок).** Новая размерность: ссылочная целостность DOM. **Served-HTML (6 страниц: /, /settings, /stats, flashcard, empty /review, 404):** дубли ID — 0; битые aria-labelledby/aria-describedby/aria-controls — 0; label[for] в никуда — 0. **Живой DOM после JS-циклов:** prompt-модалка экспорта ×2 (открыл/Esc) и kbd-help ×2 — ноль оставленных оверлеев, ноль застрявших inert, ID уникальны. **Каверза-урок (главное содержание тика):** первый прогон confirm-модалки ×2 «нашёл» дубли prompt-modal-title/message + 5 inert-сирот — но это артефакт МОЕЙ пробы: программный .click() ДОСТАВЛЯЕТ событие сквозь inert (inert блокирует пользовательский ввод, не dispatchEvent) → скрипт открыл ВТОРУЮ модалку поверх первой, чего реальный пользователь сделать не может (фон: pointer-events заблокированы inert-ом, клавиатура: focus-trap не выпускает Tab из модалки). Плюс мой селектор «Отмены» был кривой (.prompt-modal-overlay — а класс на самом деле .prompt-overlay) — «Отмена» ни разу не нажималась. Разбор подтвердил и НЕОЖИДАННУЮ устойчивость: Esc на верхнем оверлее разбирает даже нештатный стек из двух модалок ПО ОДНОЙ за нажатие, без осиротевших inert (модалка-2 снимает свой inert честно, включая с модалки-1). Чистый повторный цикл: 1 оверлей → настоящая «Отмена» → 0/0/0, URL цел (сабмита не было). Методика в копилку: (1) тесты модалок обязаны кликать ТОЛЬКО достижимые пользователю элементы — программный клик сквозь inert создаёт состояния, невозможные в реальности; (2) селекторы динамических узлов сверять с кодом (grep класса), не угадывать. Правок кода нет. Консоль пуста, settingsTab снят. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.79 (ROUND-RESET, R0.82) — Файловый инвентарь static/+templates + tokens.css-скан + мёртвые JS-функции: поверхность мёртвого кода ЗАМКНУТА (app.js v=60, −1 функция).** Три под-скана закрывают начатую в R0.79 линию гигиены по всем осям. **(1) Файловый инвентарь** (урок R0.81 «файлы ≠ селекторы»): static/ после чисток = 5 файлов (base/tokens/favicon/app/stats — все referenced: favicon ×3 в head, JS/CSS пиннуты), templates/ = 16 (5 корневых — все в контроллерах: focus-training/result/session-summary/settings/stats; error.html — конвенция Spring; 10 фрагментов — все th:replace-ятся: head/header ×6, stats-grid ×3, inline-alert/result-zone-head/today-widget ×2, icons/mermaid-init/post-answer-controls/training-actions ×1). Сирот НЕТ. **(2) tokens.css-скан** (не был покрыт: R0.79 сканировал только base.css): 9 классов — все живые, 0 ID. CSS-поверхность закрыта целиком: base классы+ID, tokens, файлы. **(3) Мёртвые JS-функции** (наивный def/use-скан 87 определений app.js + 16 stats.js): единственный труп — apiPost (708–714), осиротел когда POST-флоу ушли в apiFetch напрямую; apiGet/apiFetch/parseApiError живые. Удалён (7 строк), node --check OK, app.js v=60 (3 шаблона), live-синк; smoke: вкладки/js-tabs живы, консоль пуста, settingsTab снят. regenerateQuestion НЕ тронут — он в regenerate-removal-plan (window-тик, там же удаление вместе с result.html-ханками). Итог линии гигиены R0.79–R0.82: −173 строки CSS + 4 файла (~201KB) + toggle-result + apiPost; все три размерности (селекторы/файлы/функции) просканированы и чисты. Контракт-тест PENDING (bootRun жив; в шаблонах только v-бампы). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.78 (ROUND-RESET, R0.81) — ID-скан (зона роста R0.79) + сироты-CSS: 4 легаси-файла (~201KB) удалены, мёртвый #extra-analysis-toggle-result вырезан (base.css v=87).** ID-скан 21 селектора base.css × корпус: 18 живых, 3 кандидата → 2 регэксп-артефакта префиксов из комментариев (interview-options-, left-sidebar-) и 1 РЕАЛЬНЫЙ мертвец: **#extra-analysis-toggle-result** — кнопки нет ни в одном шаблоне (no-JS-вариант «доп. анализа» на result.html удалён с AI-вырезкой), а CSS держал её в 3 зонах (:active-группа, :disabled-группа, width:auto-override) + 2 комментария врали про живость. Вырезано хирургически (селекторные строки из групп, живой #extra-analysis-toggle не задет — проверено живьём: margin-top 24px на месте). **Главная находка тика — СЛУЧАЙНАЯ, через грепы ID-скана: 4 файла-сироты в static/css/** — editorial.css (169KB!), linear.css, swiss.css, broadsheet.css: 0 ссылок в templates/js/java/config (единственное упоминание — stale-комментарий в application.yml, файл вне §4 — не тронут, зафиксирован тут). Это довспышечное поколение пер-дизайн файлов ДО switchable-редизайна (память: editorial.css РАЗБИТ на tokens+base) — никогда не линкуются, но грузили jar и ЗАСОРЯЛИ maintenance-грепы (в R0.80-грепах editorial.css-строки маскировались под живые). Удалены git rm + вычищены из build/resources; live-контроль: fetch('/css/editorial.css') → 404, base.css v=87 отдаётся, страницы целы, скролл в норме (единственная консоль-«ошибка» = сам probe-fetch). Урок методики: класс-скан R0.79 сирот не видел, потому что корпус-диффу подлежали только СЕЛЕКТОРЫ base.css — сироты нашлись только когда греп пошёл по каталогу; инвентарь файлов = отдельная размерность от инвентаря селекторов. Контракт-тест PENDING (bootRun жив; в шаблонах только v-бамп head). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.77 (ROUND-RESET, R0.80) — Мёртвый CSS ПРИМЕНЁН по плану R0.79 (base.css v=86, app.js v=59): −173 строки, обе зоны вырезаны, живое не задето.** Протокол применения выполнен полностью. (1) Live-проверка ПЕРЕД удалением: fetch+DOMParser served-HTML трёх страниц (/, /settings, /stats) — 0 узлов по всем 13 селекторам обеих зон. (2) **Зона A (2579–2701, 123 строки):** AI-разбор (takeaway/code-trace/trace-step/wrong-feedback/comparison) удалён одним диапазоном; нижняя граница выверена чтением — живой @media(600px) с .result-actions сохранён впритык. (3) **Зона B:** медиа-композиция #left-sidebar-* (3183–3218, 36 строк, включая комментарий «асимметричная панель») + базовые .control-section/-tips/-title + #left-sidebar-filters (1386–1399, 14 строк); соседние живые правила целы (.control-inline-hint, .filters-fields, stats-грид). (4) **Попутные stale-комментарии актуализированы:** base.css ~1190 (перечисление отступов без takeaway/trace), комментарий у .filters-fields (обоснование ссылалось на мёртвый span-layout), app.js:1600 («сегодня это только похожие вопросы») и app.js:1476 (markdown-content «общие стили прозы», не «как у takeaway»); ОСТАВЛЕНЫ исторические комментарии-надгробия (base.css 3328, app.js 1699 — документируют факт удаления). (5) .sr-only-алиас ОСТАВЛЕН осознанно (3 строки, страховка для будущей разметки — решение из плана). (6) Grep-чистота: по всем 18+4 именам остались только надгробия. (7) node --check OK; v-бампы base 85→86 (head.html), app 58→59 (3 шаблона); live-синк. (8) Скрин-контроль: /settings dark+light (hero/вкладки/Сессия целы, hint стилизован, scrollW 1265==vw-запас) и / (MCQ-карты целы); консоль ПУСТА; тема возвращена в auto. Файл dead-css-inventory.md удалён как применённый. Итог: base.css 4038→3865 строк. Зона роста остаётся: полный ID-скан. Контракт-тест PENDING (bootRun жив; в шаблонах только v-бампы). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.76 (ROUND-RESET, R0.79) — Инвентаризация мёртвого CSS (паттерн R0.63: read-only prep → чистое применение следующим тиком): 2 мёртвые зоны найдены, план готов.** Новая размерность: гигиена самого CSS. Метод: детерминированный скан 368 класс-селекторов base.css × корпус (templates+fragments+js), контроль по Java/Kotlin/prompts, затем ручной разбор 34 сырых кандидатов. **Зона A (~2585–2701, ≈110 строк): AI-разбор** — takeaway-*/code-trace-*/trace-step-*/wrong-feedback-*/comparison-* (18 классов). Продюсеры вырезаны с AI 2026-07-07 (InterviewControllerApiTest:34 прямо: «/api/wrong-feedback удалены»); пост-ответный флоу инжектит ТОЛЬКО .related-questions (живые стили отдельно). Попутно два СТАЛЕ-комментария врут про живость: base.css ~2650 («классы есть в app.js» — нет) и app.js:1600 («takeaway / трейс кода / похожие» — остались только похожие). **Зона B: старый /settings до вкладок** — .control-section/-tips/.control-title + всё семейство #left-sidebar-* (0 в шаблонах/JS, 10 вхождений в CSS, включая grid-зону @media ~3315-3335; каркас до 0eb762a5, вкладки живут на .settings-panel). ID-селекторы сканом не покрывались — left-sidebar найден точечно, полный ID-скан = зона роста. **Ложные кандидаты разоблачены:** .ed-theme-toggle — живое правило 44×44 (артефакт чтения через границы sed-диапазонов); ed-btn/chip/field/stack, ed-export, settings-tab-panel — комментарии, документирующие ПРЕЖНИЕ удаления; springframework/setRelay/ai- — регэксп-шум из кода-примеров в комментариях. .sr-only — микро-дубль visually-hidden (3 вхождения, 0 использований) — решение при применении. Артефакт: port-drafts/dead-css-inventory.md с протоколом применения (live-проверка querySelector → хирургическое удаление → v-бампы → grep-чистота → скрин-контроль). Правок кода нет (prep-тик). Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.75 (ROUND-RESET, R0.78) — Входная устойчивость (мусор в localStorage + GET-параметрах, XSS-echo): VERIFIED-CLEAN, 0 правок.** Новая размерность: не «как выглядит», а «что переживёт». **localStorage-мусор (все 8 ключей разом):** design=`<img src=x onerror=alert(1)>`, theme=hacker, layout=diagonal, motion=hyper, readingWidth=xxl, density=nano, fontScale=1e10, settingsTab=tab-evil → перезагрузка: КАЖДАЯ ось живьём подтвердила белый список из head.html — design→editorial (пейлоад НЕ попал в атрибут), theme→auto (resolved dark), layout→flow, motion/readingWidth/density→атрибут отсутствует (дефолт), settingsTab→атрибут не ставится (tab-session активна), fontScale 1e10→isFinite→кламп 1.4 (140%, страница юзабельна — осознанно кламп, не сброс). Консоль ПУСТА даже с мусором. Нюанс (не дефект): read-path санитизирует, но НЕ перезаписывает storage — мусорный ключ лежит до первого set(); вредных эффектов нет. **GET-параметры:** (1) /stats?q=`<script>alert(1)</script>` — пейлоад литеральным ТЕКСТОМ и в input (th:value эскейпит), и в echo «По запросу … ничего не найдено», 0 внедрённых тегов, alert не сработал; (2) important=junk → graceful-редирект на / (Boolean-биндинг падает БЕЗ 500/Whitelabel/stacktrace; поведение бэкенда — вне §4, зафиксировано как приемлемое); (3) topic=../../etc/passwd на /stats — игнор (select пуст, значение НЕ в опциях, таблица жива); (4) /review и / с мусорным topic → штатный empty-state «Сейчас нет вопросов» с CTA восстановления (настройки/обновить), эскейп цел (`<b>` не внедрился). **Каверза проб:** матч «exception» на /stats — НЕ утечка, а слаг темы java-exceptions-interview в JSON data-island графиков; проверять контекст матча до классификации. Итог: фронтовая поверхность устойчива к мусорному вводу по обоим каналам; утечек (Whitelabel/stacktrace/SQLException) нигде нет. Гигиена: все ключи сняты, браузер на чистом /. Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.74 (ROUND-RESET, R0.77) — Клавиатурная операбельность /settings (вкладки/seg-контролы/степпер): VERIFIED-CLEAN, размерность замкнута на всех read-only поверхностях (0 правок).** Завершение размерности R0.76 (там / и /stats + фикс «?»-гарда): /settings — самая клавиатурно-насыщенная страница (ARIA-вкладки + 6 seg-radiogroup + степпер). Всё реальными нажатиями. **Вкладки (полный ARIA tabs-паттерн живьём):** ArrowRight session→appearance, End →data, Home →session, ArrowLeft с первой ОБОРАЧИВАЕТ на последнюю; каждый шаг синхронно: aria-selected, roving tabindex (0/-1), panel.is-active, фокус следует, localStorage settingsTab пишется. **Seg-контрол (ось «ширина чтения», паттерн wireSegControl един для 6 осей):** ArrowRight с активной «Стандартная» — ОДНО нажатие двигает всю цепочку: фокус+aria-checked+roving на «Широкая», html data-reading-width=wide, localStorage=wide, live-region «Сохранено на этом устройстве: Ширина чтения — Широкая.», --measure 72ch→100ch применился; ArrowLeft назад — ось САМА вычищает дефолт (attr и ключ сняты, не «standard»-мусор в ls). Selection-follows-focus — осознанный вариант ARIA radiogroup (значения применяются мгновенно и обратимо — не деструктив). **Степпер шрифта:** Space на font-increase = нативная активация кнопки type=button → 110% (inline root font-size + live-region); reset → 100%, inline снят, ключ null, кнопка сама переводит себя в aria-disabled=true (механика краёв доказана живьём; aria-disabled вместо native disabled — фокус не сбрасывается, комментарий кода честен). **Дефектов нет.** Гигиена: все 8 ключей localStorage проверены = null. Размерность «клавиатурная операбельность» закрыта: / + /stats (R0.76) + /settings (тут); session-gated поверхности (result/summary) — parity-фаза. Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.73 (ROUND-RESET, R0.76) — Клавиатурная операбельность живьём: Tab-порядок и шорткаты VERIFIED, дефект «„?" мёртв с radio/checkbox» ПОЧИНЕН (app.js v=58, stats.js v=14).** Новая размерность: не «виден ли фокус» (R0.68-69), а «работает ли всё с клавиатуры». **Tab-порядок / (MCQ):** 10 реальных Tab через focusin-лог — skip-link ПЕРВЫМ («Перейти к вопросу») → бренд → nav (Фокус/Аналитика/Настройки) → 3 тоггла → radio-группа; disabled-submit корректно пропускается; после radio фокус уходит в браузер-хром (radio-группа = последний tabstop до выбора) — ловушек нет. **Шорткаты MCQ:** «2» выбирает опцию 2 + разблокирует submit; ArrowDown циклит; Escape снимает выбор + re-disable submit + фокус НЕ теряется (остаётся на radio); «9» вне диапазона 4 опций — no-op без ошибок. Enter НЕ нажимался (submit = деструктив §4). **ДЕФЕКТ (обе копии паттерна):** гард хоткеев справки глушил `['INPUT','TEXTAREA','SELECT']` целиком → «?» был мёртв с фокуса на radio (app.js:1918) — а это ОСНОВНАЯ клавиатурная позиция страницы вопроса; на /stats (stats.js:385) с фокуса на чекбоксе фильтра были мертвы и «?», и «/»-прыжок в поиск. При этом MCQ-хэндлер (1183) radio давно исключает — рассинхрон гардов одного назначения. Фикс: исключение radio/checkbox (текст не принимают — глушить нечего) в обоих файлах, текстовые поля остаются заглушенными. **Re-QA живьём:** «?» с radio открывает справку, фокус в модалке, «3» под оверлеем НЕ выбирает опцию (гард 1181 цел), Esc закрывает + фокус возвращается на radio; /stats: «?» с чекбокса открывает, «/» с чекбокса прыгает в поиск, «?» ВНУТРИ текстового поиска остаётся литеральным вводом (searchValue=«?», справка закрыта) — гард текстовых полей не задет. Консоль: только известный CSP-шум sourcemap. Гигиена: поиск очищен, blur. Бампы: app.js v=58 (result/settings/focus-training), stats.js v=14 (stats.html), live-синк. Контракт-тест PENDING (bootRun жив; в шаблонах только v-бампы). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.72 (ROUND-RESET, R0.75) — Функциональное QA интерактива /stats (сортировка/экспандер/GET-формы): VERIFIED-CLEAN, ложная тревога «поиск не сбрасывается» разоблачена (0 правок).** Размерность, которой ещё не было: не вид, а ПОВЕДЕНИЕ интерактива /stats — всё client-side или GET, деструктива нет. **Ложная тревога первого прохода:** проба показала «поиск „kafka" → 12 строк, очистка → всё ещё 12» — выглядело как сломанный сброс фильтра. Разбор: клиентского поиска по таблице тем НЕ СУЩЕСТВУЕТ ВООБЩЕ (`#search-input` в DOM нет; единственный поиск — GET-форма `.search-input` name=q «Поиск по вопросам»), а «12 видимых» = дефолт свёрнутого экспандера (data-collapse-rows limit=12, `row.hidden`). Совпадение чисел (12 «результатов» == 12 строк лимита) породило ложный вывод — дефекта нет. **Сортировка:** aria-sort none→ascending→descending по клику th, live-region `#table-sort-status` объявляет («Таблица отсортирована: Тема, по убыванию»), порядок реально меняется; **в свёрнутом состоянии MutationObserver честно ре-синкает**: после сортировки видимых ровно 12 УЖЕ НОВОГО порядка (первая строка «Всего» 19→13 asc→56 desc; rows[12] и rows[318] снова hidden). Каверза проб: имя темы = `th[scope=row]` (a11y-плюс), поэтому `querySelector('td')` строки отдаёт колонку «Всего» — числа в пробах не баг. **Экспандер:** тоггл 12↔319 в обе стороны, textContent («Показать все темы (307 ниже)»/«Свернуть таблицу») и aria-expanded синхронны. **GET-поиск round-trip:** /stats?q=kafka → значение удержано в поле, секция «Результаты поиска» рендерится (20 элементов); результат «Transaction Log (WAL)» по запросу kafka = честный full-text (grep: 4 упоминания Kafka в database-transactions-interview.md — сравнение WAL с Kafka-логом). **GET-фильтры round-trip:** important/onlyWrong удержаны checked, q сохранён; две GET-формы (Фильтры и Поиск) взаимно протаскивают чужие параметры через hidden-поля — состояние не теряется при отправке любой из них. Браузер возвращён на чистый /stats (сорт/экспандер — клиентские, сбрасываются загрузкой; localStorage не трогался). Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.71 (ROUND-RESET, R0.74) — Ассет-целостность + консоль-чистота после серии v-бампов: VERIFIED-CLEAN (0 правок).** После правок R0.64–R0.68 версии разошлись по трём файлам (base 82→85, app 56→57, head-пины) — проверено, что живые страницы согласованы и ничего не 404-ит. Холодные загрузки 4 различных шаблонов: **/** (focus-training) — tokens v=62 + base v=85 + app v=57, все 200, консоль ПУСТА; шрифтовые woff2 (Inter/JetBrains/Lora/Newsreader/SpaceGrotesk) 200; mermaid+highlight CDN 200 (includeMermaid на вопросных страницах — осознанно, внешний владелец диаграмм). **/settings** — тот же набор минус mermaid, все 200, консоль пуста. **/stats** — stats.js v=13 + chart.umd 200; консоль содержит ТОЛЬКО известный CSP-шум sourcemap chart.js.map (connect-src 'self'; классифицирован в cr.56/cr.58, упирается в отложенное self-host решение юзера) — новых ошибок нет. **error 404** — ассеты все 200; единственная консоль-«ошибка» = сам документ-запрос со статусом 404 (это страница ошибки — легитимно); app.js на error-странице не грузится (минимальный шаблон — осознанно). Итог: ни одного битого ассета, ни одной stale-версии, ни одной новой JS-ошибки; крест-навигация браузера в дефолтном состоянии (editorial, все ключи сняты в R0.73). Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.70 (ROUND-RESET, R0.73) — Стресс-комбо всех 7 осей: VERIFIED-CLEAN, интеракции осей не ломают композицию (0 правок).** Оси были проверены поодиночке (E-серия, R0.71, R0.72) — но баги живут на пересечениях. Худший случай разом: instrument + dark + **split** + **compact** + **wide** + **fontScale 1.4 (максимум)**. **/ @1280:** все 6 атрибутов применились из pre-paint, scrollW==vw (0 overflow), split-геометрия ЖИВА под увеличенным шрифтом и сжатыми отступами (вопрос и опции бок-о-бок, прямоугольники НЕ перекрываются — проверено пересечением bounding-rect), root 22.4px. **/settings @1280:** все 3 вкладки без overflow; вкладка-кнопка выросла до 137×63 (≥44px touch с запасом — рост шрифта не сломал таргеты). **@375 (mobile+touch):** /settings (Сессия+Оформление) и / — scrollW==375 везде; **split корректно деградирует в столбец** (медиа-запрос перебивает ось — правильный порядок каскада), MCQ-опция 516px высотой (длинные тексты × 1.4 — высокая, но не сломанная). Итог: ортогональность осей держится не только по построению (токен-переопределения не пересекаются), но и живьём в worst-case комбинации на двух форм-факторах. Все 8 ключей localStorage сняты пост-фактум. Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков. Незакрываемое текущим тулингом (задокументировано ранее): @media prefers-reduced-motion / prefers-contrast / forced-colors — нет эмуляции в этом MCP.
- **cr.69 (ROUND-RESET, R0.72) — Оставшиеся оси персонализации live-QA (раскладка/шрифт/ширина/плотность): VERIFIED-CLEAN с двумя картографическими находками (0 правок).** Завершение осевого QA (дизайн/тема — серия E, движение — R0.71). **Раскладка (layout):** flow↔split доказан геометрией на / — split разносит вопрос (x=57, w=320) и опции (x=441, w=768) в два пейна, flow ставит в столбец (оба x=153, w=960); pre-paint атрибут и `window.__layout` живы. **Находка 1:** правил `[data-layout="grid"]` в base.css НЕТ ВООБЩЕ — grid = «атрибут есть, оверрайдов нет» = базовая композиция (это соответствует докстроке head.html «grid — прежняя композиция, сохранена»); а flow-оверрайды на browse-странице не задевают замеренные блоки — они про focus-topbar/session-rail, которых без сессии нет → неотличимость flow==grid на browse ЗАКОННА, полная дифференциация проверяема только в session-флоу (parity-фаза). **Ширина чтения:** wide → --measure 72ch→100ch, живой абзац #browse-answer 818→910px при контейнере 960 (мера растёт внутри max-width-content — правильная ортогональность). **Шрифт:** __fontScale.set(1.2) → inline 120%, root 19.2px; сброс к 1 снимает и inline, и ключ. **Находка 2:** осей СЕМЬ, не шесть (память проекта считала 6): в tokens.css живёт data-density (spacious/compact, --space-* шкала) с полным pre-paint контуром в head.html — проверена: compact → --space-6 2rem→1.5rem, attr/localStorage-гигиена чистая. Все атрибуты/ключи возвращены к дефолтам (проверено пост-фактум: null везде). Seg-контролы осей на /settings разделяют wiring-паттерн, живьём доказанный на оси движения (R0.71) + API-путь каждой оси доказан здесь. Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.68 (ROUND-RESET, R0.71) — Ось движения (data-motion) live-QA: VERIFIED-CLEAN во всех трёх состояниях (0 правок).** Шестая ось персонализации ни разу не проверялась живьём в этом окне. Архитектура (head pre-paint + base.css 278-304): auto = атрибута нет, работает @media prefers-reduced-motion с гейтом `:not([data-motion="on"])`; off = принудительное гашение (`* !important`: animation/transition 0.001ms, scroll-behavior auto); on = принудительно оставить движение даже при OS-reduce. **Живой QA на /settings:** off → attr=off, transition тоггла/вкладки **1e-06s**, scroll-behavior root **auto**; on → 0.15s + smooth; auto → атрибут снят, нормальные длительности в обычной среде. `window.__motion` API жив; **seg-контрол оси на вкладке «Оформление» связан**: клик «выкл» → attr+localStorage+aria-pressed одновременно, возврат «авто» чист (attr и ключ сняты). Модалки/фейды покрываются тем же `*`-правилом (комментарий 2428 честен). **Ограничение инструмента задокументировано:** путь @media (OS-настройка «уменьшить движение») не эмулируется этим MCP (нет prefers-reduced-motion в emulate) — корректность ветки подтверждена конструктивно: тело блока идентично off-блоку, гейт `:not([data-motion="on"])` соответствует спецификации оси; при появлении эмуляции — прогнать живьём (auto+OS-reduce → гашение; on+OS-reduce → движение остаётся). localStorage возвращён (motion/settingsTab сняты). Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.67 (ROUND-RESET, R0.70) — Reflow-свип 320px (WCAG 1.4.10): VERIFIED-CLEAN на всех read-only поверхностях (0 правок).** Пробел гейта: overflow-свипы ходили от 375px (project_summary_page_test_flow, cr.53), а критерий reflow стандарта — **320 CSS px**. Прогнано на 320×800 (dpr2, mobile+touch): **editorial** (дефолт) — / (MCQ), /settings (все 3 вкладки поочерёдно), /stats (базовое состояние + раскрытая полная таблица тем через экспандер), flashcard, empty /review, error 404 — везде scrollWidth == 320, из виновников только CODE в flashcard, лежащий в собственном overflow-x:auto контейнере (правильный паттерн 1.4.10 — двумерный контент исключение); **instrument** спот-чек — /settings и / (MCQ) чисты (320/320, 0 виновников). Методика: bounding-rect всех элементов против clientWidth + классификация «в скролл-контейнере или течёт наружу». Итог: reflow-гейт закрыт на уровне стандарта, а не только 375; браузер и localStorage возвращены к дефолтам. Правок кода нет. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.66 (ROUND-RESET, R0.69) — Focus-visible свип / (MCQ) + /stats + flashcard: VERIFIED-CLEAN, размерность закрыта на всех read-only поверхностях (0 правок).** Продолжение R0.68 той же методикой (реальный Tab → клавиатурная модальность → script-focus + settle 350ms, instrument). **/ (MCQ):** радио-опция — ринг на label (механика :has(input:focus-visible) работает живьём), submit «Ответить» в исходном состоянии disabled (не фокусируем ЗАКОННО — enable по выбору опции); после клиентского выбора радио (без POST) — глобальный ринг 2px; back-to-top — ринг. **/stats:** search-input и фильтр-select — shadow-focus 3px teal (0.72/0.45); apply/экспандер/ссылки таблицы — глобальный ринг; sortable th — свой inset-ринг (offset −2px, правильный для ячейки заголовка). **flashcard:** reveal-кнопка и summary «Пример кода» — ринг; code-copy-btn изначально «не фокусируем» — внутри СВЁРНУТОГО details (контент closed-details не рендерится; contradiction computed display flex ↔ фокус не берётся = content-visibility-квирк Chrome, задокументирован); после открытия details — фокусируем с рингом = ЗАКОННО. **error 404** покрыт архитектурно: там только ссылки/кнопка, ни одного правила outline:none на них нет → глобальный ринг (проверка по карте R0.68, живьём не гонял). Итог: 0 новых дефектов; фикс R0.68 остаётся единственным по этой размерности. localStorage возвращён. Контракт-тест PENDING (bootRun жив). Гейты прежние: window-тик / EXAM / данные для графиков.
- **cr.65 (ROUND-RESET, R0.68) — Focus-visible аудит /settings: невидимый клавиатурный фокус tabpanel ПОЧИНЕН (v=85); остальное покрытие VERIFIED-CLEAN.** Новая размерность аудита (WCAG 2.4.7): архитектура фокуса = глобальный ринг `:focus-visible` (229) + глобальная мышиная тишина `:focus:not(:focus-visible)` (233) → дефекты могут жить только там, где локальный `outline:none` глушит ринг без замены. Статический проход по всем 10 сайтам outline:none: 6 с заменой (settings-tab/seg-btn — shadow-focus + High-Contrast-восстановление; MCQ-label — :has(input:focus-visible)-ринг; select/inputs — border+shadow), 1 осознанный (#main-content — приёмник skip-link, tabindex=-1, в таб-порядок не попадает). **Дефект:** `.settings-panel:focus {outline:none}` (из tabs-редизайна 0eb762a5) — панель с tabindex=0 СТОИТ в таб-порядке (ARIA-паттерн), правило специфичнее глобального ринга (0,3,1)>(0,2,1) → Tab из tablist приземлялся на панель НЕВИДИМО. Правило к тому же избыточно для мыши (233 покрывает). Фикс: правило удалено с комментарием — клавиатурный ринг вернулся через глобальный, мышь тихая. QA живьём (реальный Tab, не script-focus): с активной вкладки Tab → panel-session, `:focus-visible` true, ринг 2px solid border-focus 0.72 teal (instrument). **Методика-урок:** (1) script-focus сразу после реального Tab наследует клавиатурную модальность — :focus-visible матчится честно; (2) transition box-shadow: снимок сразу после focus() даёт СТАРТОВОЕ значение (прозрачный ноль) — мерить после settle ~350ms, иначе ложный «нет ринга» (select дал ложный минус в первом проходе); (3) элементы в скрытых панелях/PE-блоках «не фокусируемы» законно (numberInput в .hidden — не дефект). Полный спот-чек instrument по видимым: skip-link/nav/тогглы/tabs/seg-btn/select/checkbox/export/danger/btn — все с индикатором. Дизайн-агностик (html[data-design]) — фикс всем 11 дизайнам. Контракт-тест PENDING (bootRun жив).
- **cr.64 (ROUND-RESET, R0.67) — Confirm-модалка в instrument (обе темы) + focus-trap: VERIFIED-CLEAN, пробел cr.63 закрыт (0 правок).** cr.63 QA-л alertdialog только в editorial light — а тайтл модалки пересекается с INSTRUMENT-SIGNATURE R0.57 (`.prompt-modal-title` → medium), и danger-язык у instrument свой. Проверено живьём в instrument: **dark** — тайтл Newsreader **500** (SIGNATURE достаёт динамически создаваемую модалку — правило работает по классу, не по месту в DOM), сообщение text-secondary 0.78, карта bg 0.19/border 0.36, danger-OK = ghost с error-цветом 0.72 (тот же язык, что у danger-кнопки страницы — модалка внутри body.settings-page, скоуп `.settings-page .danger-btn` достаёт), оверлей ink-wash 0.55; **light** — тайтл 500, сообщение 0.43, OK error 0.5 на карте 0.985 (контраст с запасом), cancel-рамка border-primary 0.876; скриншот визуально чист (структура тайтл→абзац→Отмена|Подтвердить, ничего не течёт). **Focus-trap проверен в обе стороны**: Tab с последнего (OK) оборачивает на первый (Отмена), Shift+Tab с первого — на последний (cr.63 проверял только фокус-дефолт и Esc, сам trap — нет). Esc закрыл, localStorage возвращён к дефолтам. Правок кода нет. Контракт-тест PENDING (bootRun жив). Остаток гейтов без изменений: window-тик / EXAM / данные для графиков.
- **cr.63 (ROUND-RESET, R0.66) — alertdialog сброса ПРИМЕНЁН (app.js v=57, base.css v=84): последний нативный window.confirm заменён внутренней модалкой.** Последний не-гейтнутый кандидат Фазы G. Было: `initDangerousFormGuard` держал деструктивный сброс банка вариантов на `window.confirm` — нестилизуемый «localhost:8080 says…», чужеродный рядом с уже существующей внутри-приложенческой prompt-модалкой (obtainAdminToken) и без alertdialog-семантики. Стало: (1) **promptModal получил mode:'confirm'** — та же машинерия (inert-фон, focus-trap, Esc, возврат фокуса, textContent-защита от инъекции), но role=**alertdialog** + aria-describedby на `<p class="prompt-modal-message">` (вместо label+input), кнопка OK = «Подтвердить» с `.danger-btn`, **фокус по умолчанию на «Отмена»** (безопасный дефолт деструктива), resolve(true|false); обычный prompt-режим не тронут. (2) `confirmModal()` — тонкая обёртка; передаётся параметром в top-level `initDangerousFormGuard` (он вне IIFE, promptModal замкнута внутри). (3) Гард: preventDefault → модалка → при «Подтвердить» `requestSubmit()` с флагом `data-confirmed` (повторный вход отсекается; остальные submit-гарды продолжают работать). (4) settings.html: кнопке добавлен `data-confirm-title="Сбросить банк вариантов?"`. **QA живьём** (клики без подтверждения — деструктив не запускался): alertdialog/describedby/тайтл/сообщение/danger-OK — всё на месте; фокус на «Отмена»; фон inert; Esc и «Отмена» закрывают БЕЗ отправки формы (url остался /settings), inert снят, фокус вернулся на кнопку (первый прогон «focusReturned=false» — артефакт программного btn.click() без focus, перепроверено с focus()). Регрессия prompt-режима: экспорт JSON → role=dialog, password-input с фокусом, okText «Продолжить», без danger — бит-в-бит прежнее поведение. PE: без JS форма отправляется без подтверждения — как и раньше (confirm тоже жил только в JS). node --check OK. Бампы: app.js v=57 во всех 3 шаблонах (result/settings/focus), base.css v=84. Контракт-тест PENDING (bootRun жив); пинов на reset-форму в тестах нет (grep 0).
- **cr.62 (ROUND-RESET, R0.65) — CLS-свип всех read-only страниц под троттлингом: VERIFIED-CLEAN, размерность закрыта.** Методология R0.64 (Slow 4G + CPU 4x, холодный кэш, PerformanceObserver layout-shift с источниками) прогнана по всем достижимым поверхностям — заодно контроль, что глобальный `data-js` (он теперь на `<html>` каждой страницы) ничего не сдвинул вне /settings. Результаты: **/** (focus, MCQ) desktop 0.0012 (единственный источник — LABEL, шрифтовый микросдвиг опции; на порядок ниже порога 0.1) и mobile 375 — 0; **/stats** — 0 (PE-блоки на пустой базе уже в потоке, раскрытий со сдвигом нет); **flashcard** (`/?topic=preparation/interview-preparation`) — 0; **empty** (`/review?topic=…`) — 0; **error 404** — 0; **/settings mobile 375 — 0** (baseline cr.56 был 0.35 — фикс R0.64 закрыл и худший mobile-случай, не только desktop 0.285→0.0004). Итог: сдвиги первого кадра отсутствуют на всех read-only поверхностях в обоих форм-факторах; CLS-строка бэклога Фазы G закрыта полностью. Session-gated поверхности (result/summary) — в parity-фазу. Правок кода нет; эмуляция сброшена. Контракт-тест PENDING (bootRun жив) — обязателен в window-тик (head.html тронут в R0.64).
- **cr.61 (ROUND-RESET, R0.64) — CLS /settings ПОЧИНЕН (v=83): pre-paint data-js + data-settings-tab, замер 0.285→0.0004 под троттлингом.** Кандидат Фазы G «резерв места под PE-блоки» взят с замером, как и предписано cr.55. **Диагностика:** на локалке CLS ≈ 0.0004 даже с холодным кэшем (app.js успевает до первого кадра) — сдвиг живёт только под троттлингом (Slow 4G + CPU 4x, условия LH): PerformanceObserver показал ЕДИНСТВЕННЫЙ крупный сдвиг 0.2844 от `#panel-appearance` — первый кадр рисует стопку 3 панелей, потом initSettingsTabs схлопывает её в вкладки И восстанавливает сохранённую вкладку из localStorage (в тест-профиле была appearance — воспроизвёлся худший случай: двойной источник сдвига). **Фикс — первый кадр = пост-JS состояние:** (1) pre-paint скрипт head.html ставит на `<html>` `data-js` (метка «JS работает») и `data-settings-tab` (валидированная сохранённая вкладка) — гарантированно ДО первого кадра, как data-design/theme; (2) base.css: 4 правила `[data-js]` в зоне вкладок — tablist `display:flex !important` (единственный способ перебить утилиту `.hidden`, она сама !important; специфичность (0,4,1)>(0,2,1)), панели display:none, panel-title sr-only, показ ОДНОЙ панели по ID из data-settings-tab (дефолт — panel-session). ID-правила гейтованы `:not(.js-tabs)` — иначе после клика по вкладке ID-правило (1,x,x) навсегда пришпилило бы сохранённую панель; после DOMContentLoaded панелями правит штатная is-active-механика. **QA:** троттлированный re-run: CLS 0.285→0.0004 (остаток — шрифтовый микросдвиг seg-btn); pre-DCL снимок (readystatechange=interactive): js-tabs ещё НЕТ на body, но tablist уже flex и видна ровно одна панель = сохранённая; интерактив цел (клики Данные/Сессия переключают, aria-selected/localStorage пишутся); instrument-контроль чист (tablist flex, hero bg-tertiary 0.262 dark); localStorage возвращён к дефолтам. **PE-контракт не тронут:** без JS атрибутов нет → стопка. Осознанный трейдофф задокументирован: если inline-скрипт отработал, а app.js упал — юзер видит одну панель + инертный tablist (классическая цена .js-флага; вероятность ничтожна). Правки: head.html (pre-paint + v=83), base.css. Контракт-тест PENDING (bootRun жив) — head.html тронут, прогнать в window-тик вместе с regenerate-планом.
- **cr.60 (ROUND-RESET, R0.63) — План-патч удаления мёртвого regenerate-UI подготовлен (паттерн R0.28: read-only prep, чтобы window-тик был чистым применением).** Отложенная с cr.54 единица «regenerate-UI/.question-side» переведена из «ждёт окна» в «ready-to-apply»: написан `port-drafts/regenerate-removal-plan.md` с точными ханками по трём файлам. Скоуп собран read-only: **result.html** — btn-regenerate `th:if="${aiEnabled}"` (48-51) + span regen-badge + div.question-side (~121) + комментарий про :has-грид; **app.js** — API.REGENERATE (18), regenerateQuestion() (~884-950), ветка делегата (968-973; favorite-ветка и obtainAdminToken ОСТАЮТСЯ — экспорт живёт на них); **base.css** — 6 зон (1047 / 2292-2300 / 2398-2419 / 2689-2692 / 3256-3258 / 3493) + grep regen-badge перед применением. Новое evidence этого тика: read-only SELECT к quiz-postgres — `max(regen_count)=0, count(regen_count>0)=0` → regen-badge мёртв ВМЕСТЕ с кнопкой (не только через aiEnabled), удалять всё разом. Подтверждено отсутствие тест-пинов: 0 совпадений regenerate/question-side в TemplateFragmentContractTest, VisualBaselineContractTest, InterviewMvcControllerTest. Бэкенд /api/regenerate НЕ трогаем (§4). Window-тик сведён к механике: применить ханки → бамп v app.js+base.css → grep-чистота → таргетный gradle (контракт + MVC — заодно закроет 7 PENDING-TEST¤ ячеек матрицы) → smoke. Правок кода нет (bootRun жив — d4e2aefc-сессия активна). Контракт-тест PENDING.
- **cr.59 (ROUND-RESET, R0.62) — Режим поддержки §28: sentinel-проверка целостности + shell no-js RECHECK закрыт.** (1) **Sentinel:** 46 instrument-правил на месте во всех 7 зонах (shell 618+ / focus 839+ / stats 1933+ / summary 2076+ / error 2236+ / result / settings / SIGNATURES); source == build == served БИТ-В-БИТ (d523fa45, полный diff). Каверза повторилась: `curl | md5` в пайпе дал ЛОЖНЫЙ md5 (a96dd67d) — rtk-хук искажает пайплайны (тот же урок, что с git diff R0.28) → интегрити мерить только через файл + полный diff, не через пайп. (2) **Shell no-js RECHECK → PASS:** статик-аудит served-HTML: noscript-алерт `.ed-noscript` role=alert с честной копией (что сломается, что останется); ВСЕ 3 тоггла (layout/design/theme) рендерятся с классом hidden — PE-контракт «без JS нет мёртвых контролов» жив; nav — 3 обычные ссылки (работают без JS); sticky в разметке 0; back-to-top — безвредный анкор. Плюс проверены noscript-фоллбэки шрифтов и highlight.js (обнаружены попутно — грамотный паттерн). Матрица: последняя RECHECK-ячейка закрыта. Правок кода нет. Контракт-тест PENDING (bootRun жив; параллельная сессия активна — d4e2aefc pedago).
- **cr.58 (ROUND-RESET, R0.61) — Lighthouse ТЁМНОЙ темы instrument (/, /settings, /stats): VERIFIED-CLEAN — контраст-гейт закрыт для обеих тем.** Пробел гейта: все LH-прогоны R0.58/R0.59 шли в light (дефолт), axe-контраст тёмной instrument-палитры живьём не проверялся (статический токен-аудит есть, но ловушки rgba/градиентов он не видит — project_ui_verified_clean). Методика: localStorage design=instrument + theme=dark → pre-paint init применяет ДО первой отрисовки → LH navigation-аудит меряет настоящий dark. Результат: **color-contrast=1 и a11y 100 на всех трёх страницах**; наборы провалов БИТ-В-БИТ совпадают со светлыми прогонами (SEO-шум ×3; CLS 0.5 на /settings = PE-кандидат Фазы G; console/inspector на /stats = CSP-шум sourcemap ← deferred self-host). Ни одного dark-специфичного дефекта. localStorage возвращён к дефолтам (design/theme сняты — браузер юзера не оставлен в instrument-dark). Итог LH-серии R0.58–R0.61: instrument держит a11y 100 / BP 100 (кроме классифицированного CSP-шума) на desktop+mobile × light+dark — гейт «AA обе темы» закрыт живым инструментом, не только статикой. Правок кода нет. Контракт-тест PENDING (bootRun жив).
- **cr.57 (ROUND-RESET, R0.60) — Греп-аудит антипаттерна «статичный aria-label + живой/видимый текст»: VERIFIED-CLEAN, пара R0.58/R0.59 была полным множеством.** Пройдены ВСЕ шаблоны + app.js/stats.js. Проверено и чисто (имя содержит видимый текст, регистронезависимо): grade-кнопки флешкарт («Оценка 1: Не помню» ⊇ «Не помню»), confidence-кнопки app.js («Уровень уверенности: угадал» ⊇ «Угадал»), фильтры/поиск stats («Применить фильтры статистики» ⊇ «Применить фильтры»), экспорт settings («Экспорт JSON — …» ⊇ «Экспорт JSON»), MCQ-опции focus/result (th:aria-label строится ИЗ текста опции). Правило неприменимо (icon-only или landmark): favorite/regenerate, тогглы шапки (JS-swap label, внутри только svg), kbd-help-close («×» — символы axe игнорирует), nav/section/aside-лендмарки, forecast-bar role=img. Пограничный случай, признан НЕ дефектом: code-copy-btn во flash-состоянии 2с показывает «Скопировано»/«Ошибка» при имени «Копировать код» — транзиентная смена, результат озвучивается отдельным live-регионом (announceCopy), churn не оправдан. Итог: экземпляров антипаттерна больше нет; степпер (R0.58) и экспандер (R0.59) были единственными. Правок кода нет. Контракт-тест PENDING (bootRun жив).
- **cr.56 (ROUND-RESET, R0.59) — Lighthouse mobile-прогон instrument (/, /settings, /stats): второй label-in-name пойман и починен; CSP-шум классифицирован.** Mobile-серия завершает LH-гейт R0.58: **/** — a11y/BP/agentic 100, target-size PASS (touch-таргеты держат и mobile-профиль). **/settings** — a11y/BP 100, фикс степпера R0.58 держится на mobile; CLS-кандидату добавлен mobile-замер 0.35 (desktop был 0.246 — PE-раскрытие на узком экране сдвигает больше). **/stats** — a11y 100, но второй экземпляр того же класса дефекта: `#topic-table-expander` носил СТАТИЧНЫЙ aria-label «Показать или свернуть таблицу тем», а живой textContent кнопки — «Показать все темы (307 ниже)»/«Свернуть таблицу» → имя перекрывало видимый текст (WCAG 2.5.3). Фикс лучше степперного: aria-label ПОЛНОСТЬЮ снят — имя теперь равно живому тексту автоматически и всегда точно (state и так в aria-expanded/aria-controls); контракт-тест кнопку не пиннит. Re-audit: mismatch ушёл (51 passed). BP 92 на /stats — НЕ дефект наших правок: DevTools тянет sourcemap chart.umd.min.js.map с cdn.jsdelivr.net, connect-src 'self' его режет → console-error+inspector-issue; лечится только self-host Chart.js — это ОТЛОЖЕННОЕ решение юзера (§5 промпта), не переоткрываю, только фиксирую связь «BP 92 ← deferred self-host». Паттерн-урок: статичный aria-label на кнопке с живым textContent — антипаттерн; грепнуть проект на другие такие места — кандидат следующего тика. Контракт-тест PENDING (bootRun жив).
- **cr.55 (ROUND-RESET, R0.58) — Lighthouse-гейт живого instrument: a11y/BP 100 на / и /settings; починен label-in-name степпера (WCAG 2.5.3).** Токен-аудит AA был статическим — прогнан live-Lighthouse на instrument (localStorage design=instrument → pre-paint init применяет SSR-safe). **/** (focus): a11y 100 / BP 100 / agentic 100 — единственные провалы SEO-шум (meta-description, noindex), не дизайн и не гейт. **/settings**: a11y-категория 100, но axe-аудит `label-content-name-mismatch` вскрыл реальный дефект (не instrument-специфичный, всех дизайнов): кнопки шрифт-степпера «A−»/«A+» имели aria-label «Уменьшить/Увеличить размер шрифта» БЕЗ видимого текста в имени → голосовое управление («нажми A минус») не находит кнопку. Фикс: aria-label «A−: уменьшить размер шрифта» / «A+: увеличить…» — видимый текст в начале имени, описание сохранено; ID и app.js-хуки не тронуты, контракт-тест aria-label не пиннит. Re-audit: label-in-name ушёл (48 passed). Остаток /settings: CLS 0.246 (score 0.5) — сдвиги от PE-раскрытия (стопка→вкладки js-tabs, personalization-card, export-блок); это ОСОЗНАННАЯ PE-архитектура → ад-хок не чиню, кандидат Фазы G «резерв места под PE-блоки» с замером. SEO-шум одинаков на обеих страницах (личный тренажёр, не индексируется — вероятно осознанно; решение юзеру, если вообще нужно). Контракт-тест PENDING (bootRun жив).
- **cr.54 (ROUND-RESET, R0.57) — kbd-help/prompt-модалки: тайтлы в medium (v=82); два кандидата тика отложены с обоснованием.** Отложены: (1) instrument-палитра графиков stats.js — графики НЕ рендерятся на свежевымытой базе (canvas hidden, chart-fallback виден) → изменение палитры нечем проверить визуально; ждёт данных (юзер начнёт отвечать) или session-решения; (2) удаление мёртвого regenerate-UI/.question-side — result.html недостижим read-only → Thymeleaf-ошибку рендера нечем поймать до контракт-теста; единица ждёт окна без bootRun (правка+тест одним тиком). Выполнено: QA kbd-help модалки в instrument (открытие по `?` — client-side, безопасно): оверлей ink-wash 0.42, карточка paper/line/r16/shadow — всё несут токены; kbd-чипы голым mono — НЕ дефект, это общий тихий язык приложения (keyboard-hint — такая же голая mono-строка; chip-декора нет нигде); единственная дельта — h3-тайтл semibold: правило `.kbd-help-modal h3` (0,2,2) бьёт INSTRUMENT-SIGNATURE (0,1,2). Фикс в SIGNATURES-секции: kbd-help h3 + prompt-modal-title (та же картина, (0,2,1)) → medium, равная специфичность + позже по файлу. QA: instrument 500, editorial-контроль 600 (его отгруженный вид, не трогаем); Esc-закрытие отработало. Ложный кандидат задокументирован: borderTopColor у kbd репортит currentColor при border:none — «рамка 0.245» была артефактом чтения computed, не стилем. Контракт-тест PENDING (bootRun жив).
- **cr.53 (ROUND-RESET, R0.56) — Мультивьюпорт-свип instrument-портов: VERIFIED-CLEAN, ноль правок.** Порты серии E QA-лись только @1280 — прогнан свип 375/2560 (обе темы, instrument) по всем read-only поверхностям: **/settings** (375: 0 overflow; 2560: hero 1024px центрирован по max-width-settings, bg-tertiary 0.998/0.262 держится); **/stats** (375+2560: 0 overflow, stat-value mono и thead tertiary несутся на всех ширинах); **focus/flashcard** (375: 0 overflow, reveal-чип 327×53 ≥ 44px touch; единственный «широкий» элемент CODE лежит в pre.question-code overflow-x:auto — правильный паттерн скролла в собственном контейнере, НЕ дефект; 2560: колонка 960px центрирована); **error 404** (2560: ghost-код 136px = верх clamp, цвет border-primary в обеих темах; 375: 77px, 0 overflow). Методика: bounding-rect сверх clientWidth + проверка overflow-контейнера у виновника; settle 400ms после каждого флипа. Итог: instrument-порты reflow-чисты на краях диапазона юзера (375…2560) — full-width/parity ячейки матрицы подтверждены и вне 1280. Правок нет → v=81 не бампался, контракт-тест по-прежнему PENDING (bootRun жив). Остаток: session-gated parity (EXAM-решение юзера), контракт-тест при окне без bootRun.
- **cr.52 (ROUND-RESET, R0.55) — Flashcard+empty ветки взяты READ-ONLY + INSTRUMENT-SIGNATURE h1..h6 medium (v=81).** Разведка сняла гейт «нужна сессия» с двух веток: (1) **flashcard** — единственная тема без seed-JSON = `preparation/interview-preparation` (42 вопроса без опций, подтверждено read-only SELECT-ом к quiz-postgres) → `GET /?topic=preparation/interview-preparation` даёт flashcardMode без всякой сессии. QA живьём: вопрос Newsreader 500, тема lowercase/tertiary, reveal-кнопка = отдельный teal r10-чип (editorial-контроль: приклеенный 0 0 8 8 — его язык); reveal вне формы (чистый клиентский toggle, кликать безопасно), после reveal — ответ + «Пример кода» + скрытые «Похожие вопросы», grade-кнопок в browse-режиме НЕТ (SM2-грейды живут в due-flow — остаются session-gated). (2) **empty** — `GET /review?topic=…` на теме без ошибок отдаёт «Сейчас нет вопросов»: кнопки корректны token-only (secondary ghost r10 line-рамка, CTA teal 0.47/0.8). (3) Empty-QA вскрыл дефект голоса: h2 вне явных C*-i-правил (empty-state, markdown-ответы) падал в глобальный semibold 600, потому что у instrument НЕ БЫЛО своей SIGNATURE-секции (у editorial есть h1..h6 medium — потому его заголовки 500). Системный фикс: секция INSTRUMENT — SIGNATURES (h1..h6 medium) после SWISS-сигнатур, зеркало editorial-паттерна. QA: instrument h2 600→500, editorial 500 (свой), swiss 650 (не задет). Цифровые НЕ-заголовки (.stat-value semibold) намеренно не тронуты. Контракт-тест PENDING (bootRun жив). Остаток session-gated: summary/result parity, grade-кнопки due-flow, .session-progress mono — ждут EXAM-решения юзера.
- **cr.51 (ROUND-RESET, R0.54) — NAMES закрыт без кода + checked-fix C1-i (v=80): каверза R0.22 сработала ровно там, где её ждали.** (1) НАХОДКА п.36 маппинга shell устарела: параллельный WIP уже внёс `instrument: 'Instrument'` в NAMES и фолбэк order() дизайн-тоггла — живьём aria «Сменить дизайн (сейчас: Instrument)» / title «Instrument → Editorial». Закрыто документацией. (2) Отложенный с R0.49 live-QA checked-состояния опций вскрыл РЕАЛЬНЫЙ дефект порта: секция «UX REVIEW PASS 2026-07-01» (низ файла, зона ~3592 — та самая каверза R0.22 из пересверки R0.29!) переопределяет checked-язык для ВСЕХ дизайнов (нейтральная рамка text-secondary + raised bg-tertiary + inset-ring border-secondary + ink-чип буквы) с равной специфичностью ПОЗЖЕ C1-i → checked-teal C1-i был мёртв с рождения (R0.49 проверял только hover). Фикс по паттерну swiss (у него уже был пост-оверрайд в той же секции): instrument-checked перенесён ЗА generic-правило — механика прода сохранена (raised bg + double-ring + ink-чип), кольцо+рамка → accent-strong. Live-QA: teal 0.435 light / 0.76 dark, bg tertiary, editorial-контроль нетронут (нейтральный язык). Мёртвое правило из зоны C1-i удалено с комментарием-указателем. Урок в копилку: «равная специфичность → позже wins» надо проверять не только на месте вставки, но и ГРЕПОМ ПО ВСЕМУ ФАЙЛУ на тот же селектор (9 вхождений checked!). Контракт-тест PENDING (bootRun жив). Дальше: контракт-тест при окне без bootRun; parity-остаток session-gated (EXAM малым N — решение юзера) или flashcard/empty ветки read-only, если достижимы.
- **cr.50 (ROUND-RESET, R0.53) — Порт shell C2-i ПРИМЕНЁН (v=79): СЕДЬМОЙ, ПОСЛЕДНИЙ порт — ФАЗА E (шаг 1 CSS) ЗАКРЫТА 7/7.** Блок = 3 правила перед секцией FOCUS: тайтл шапки medium, nav-link lowercase-шёпот, ring-hover тогглов accent-strong без заливки (один класс .ed-theme-toggle кроет все 3 тоггла). Живой QA @1280 4 состояния: nav «фокус/аналитика/настройки» lowercase живьём (a11y-снапшот), hover тоггла подтверждён РЕАЛЬНЫМ наведением (border 0.435 teal, bg transparent), sticky отсутствует (position relative везде — контракт цел), kicker/underline/progress-fill teal несут токены. **Расследование «аномалии» weight:** editorial-контроль показал 500 вместо ожидаемых 600 — не дефект и не утечка: editorial-SIGNATURE `html[data-design="editorial"] h1..h6 {medium}` уже давал editorial 500, а instrument до порта брал `html[data-design] h1 {bold}` (0,1,2 по порядку) = 700 → дельта C2-i ровно та, что предсказал baseline R0.35 (700→500). Каверза инструментария: в новом Chrome у CSSStyleRule есть .cssRules (CSS nesting) → сканер правил уходил в ветку-контейнер и возвращал пусто; чинить проверкой selectorText ПЕРЕД cssRules (вписано сюда как урок). Геометрия не тронута: mono-глифы lowercase=uppercase по ширине (nav без сдвигов), титул 500 уже давнего editorial — мобильные числа R0.39 валидны без перемера. НЕ портированы по решениям R0.26: sticky (контракт-бан), drawer (3 ссылки не требуют бургера), футер (продуктовый вопрос Фазы G), ?-кнопка (гейт юзера). Контракт-тест PENDING (bootRun жив). **Дальше: NAMES-микрофикс instrument в header.html (п.36 маппинга, файл чист) → прогон контракт-теста при первом окне без bootRun → Фаза F parity.**
- **cr.49 (ROUND-RESET, R0.52) — Порт settings C4-i ПРИМЕНЁН (v=78): шестой порт, паритет проверен ЖИВЬЁМ.** Baseline R0.36 не соврал: токены уже несли шрифтовой слой (Space Grotesk), радиусы (r6/r10) и seg-инверсию — дельты голоса instrument уложились в 4 правила: hero-карта на bg-tertiary+border-primary (линия карт C6-i/C7-i, «приподнятая карта светлее фона»), due-цифра mono/semibold вместо Lora/bold (приборная, цвет teal несут токены сами: 0.435 light / 0.76 dark), launcher-subtitle lowercase+tertiary (шёпот C1-i), panel-title medium (виден только no-JS). Live-QA @1280 4 состояния settle 400ms: все дельты = прогноз, editorial-контроль бит-в-бит с R0.36 (Lora 700 clay, uppercase secondary, hero bg-secondary 240,238,230). Геометрия не тронута → 320-перемер не нужен. Скоуп `.settings-page` — утечек некуда (хуки существуют только на /settings). НЕ instrument-scoped и потому отложено: карточная сетка осей персонализации + switch-вид тогглов (маппинг «шаг 1» называл их рестайлом, но это структурный вид для ВСЕХ дизайнов → отдельное решение, шаг 2/Фаза G); вертикальный tablist-рельс = шаг 2 (шаблон+app.js). Вставка перед секцией ПЕРСОНАЛИЗАЦИЯ (после всех перебиваемых правил: 1311/1327/2959/2993), head v77→78, live-синк. Контракт-тест PENDING (bootRun жив). Остался ПОСЛЕДНИЙ порт Фазы E: shell (масthead-title 700→500? — сверить с макетом; sticky ЗАПРЕЩЁН контрактом).
- **cr.48 (ROUND-RESET, R0.51) — Порт stats C5-i ПРИМЕНЁН (v=77): пятый порт, паритет проверен ЖИВЬЁМ.** Stats — первая портированная поверхность, достижимая read-only → полный live-QA сразу: instrument stat-value стал mono/tabular (приборные плитки, линия C6-i/C3-i), thead таблицы тем — tertiary (sticky и bg-secondary целы), stat-card не тронут (paper r10 = baseline R0.37); editorial-контроль бит-в-бит (Lora/600, th rgb(94,93,89)); reflow чист. Блок минимален (2 правила!) — baseline R0.37 честно показал, что token-only уже нёс 90% языка stats. Цвета графиков Chart.js живут в stats.js config — осознанно НЕ трогаем (кандидат Фазы G, допустимый рассинхрон зафиксирован в PARITY_QA_CHECKLIST). Вставка перед §C6, head v76→77, live-синк. Контракт-тест PENDING (bootRun жив). Остались порты: settings → shell.
- **cr.47 (ROUND-RESET, R0.50) — Порт result C3-i ПРИМЕНЁН (v=76): четвёртый порт.** Пересверка хуков §C3 по свежему дереву показала: result-зона уже глубоко в языке (mono-бейджи/капсулы, session-bar mono, sm2 как dev-панель) → блок минимален (5 правил): topic-badge lowercase+tertiary (голос C1-i), вердикт medium (цвета несут status-правила 0,4,1 — их не трогаем), stat-value mono/tabular (линия плиток C6-i), sm2-details на tertiary+line (линия error-details C7-i), sm2-grid border-top primary. Вставка перед секцией «Утилиты» (после confidence-зоны), head v75→76, live-синк. QA в пределах §4: served v=76, 5 правил распарсены, утечки нет — /stats .stat-value остался serif/600 (baseline R0.37), скоуп .result-page держит. Визуальный паритет (вердикт/опции role=list/пер-опционные объяснения/related-рейка ≥1200 — нужен POST /answer) — parity-фаза. НЕ портированы намеренно: confidence-виджет уже прод-реализован (эмодзи-решение юзера project_icon_system — не трогаем), btn-regenerate/question-side мертвы (aiEnabled=false, кандидаты Фазы G). Контракт-тест PENDING (bootRun жив). Остались порты: stats → settings → shell.
- **cr.46 (ROUND-RESET, R0.49) — Порт focus C1-i ПРИМЕНЁН (v=75): третий порт, крупнейшая поверхность, шаг 1.** Вставка после stripe-блока опций (равная специфичность — выигрыш по порядку над generic-правилами 632–789), head v74→75, live-синк. QA живого инстанса: дельта = прогноз R0.48 — topic lowercase+tertiary (oklch 0.5), вопрос Newsreader 500, опция-карты paper (0.985, была secondary 0.958), буква-бейдж tertiary; **ring-hover подтверждён РЕАЛЬНЫМ наведением** (hover tool → border oklch(0.435 0.105 205) accent-strong, фон остался paper — «щелчок прибора», не заливка); editorial-контроль = baseline R0.38 бит-в-бит (uppercase/600/240,238,230/r8). Каверзы: (1) радио-инпут визуально скрыт (clip) → hover tool целить в текст варианта, не в radio; (2) `.focus-progress-line .session-progress` на текущем состоянии не рендерится (нет активной сессии с счётчиком) — mono-правило распарсено, живая проверка на parity; (3) геометрию не трогали → 320-перемер не нужен (R0.39 остаётся валиден). Проверка выбранного состояния (checked accent-strong) не делалась — клик по радио выбирает вариант БЕЗ мутации (ответ = отдельный POST), но осторожность §4: отложено на parity вместе с flashcard/empty/result-инъекциями. Контракт-тест PENDING (bootRun жив). Следующий порт: result (маппинг R0.23).
- **cr.45 (ROUND-RESET, R0.48) — Подготовка порта focus: re-verify хуков по свежему дереву + черновик C1-i (7 правил).** Пересверка сняла 3 пункта маппинга без кода: (1) пост-ответные классы app.js (option-correct/wrong/dimmed, app.js:1435-1441) стилизованы design-agnostic статус-токенами → instrument наследует; (2) grade-1/grade-4 hover-акценты УЖЕ design-agnostic (base.css:1060-1061) — п.53 маппинга закрыт прод-кодом; (3) submit-«гибрид» cr.40 — НЕ дефект: 0 0 8 8 = editorial-решение, instrument-токены уже дают отдельный r10-чип + margin-top space-6 → композиция макета выполняется без правок. Черновик C1-i минимален: lowercase mono-тема (tertiary), medium-вопрос, mono/tabular прогресс, paper-карты опций, ring-hover accent-strong без заливки (линия C6-i «щелчок прибора»), checked accent-strong, буква-::before tertiary. DOM/app.js не трогаются; буква остаётся CSS-счётчиком. Контракт-тест всё ещё PENDING (bootRun жив). Применение C1-i + QA по baseline R0.38 — следующий тик.
- **cr.44 (ROUND-RESET, R0.47) — Порт session-summary C6-i ПРИМЕНЁН (v=74): оба ready-to-paste черновика в проде.** Вставка после §C6 (перед §C7, специфичность-по-порядку), head v73→74, live-синк. QA в пределах §4 (сессию не создаём): (1) served v=74; (2) все 9 правил C6-i распарсены (плюс 8 C7-i на месте); (3) **утечки нет** — /stats, где тоже есть `.stat-item`, отдаёт числа baseline R0.37 бит-в-бит (stat-value остался serif/600, НЕ стал mono → скоуп `.summary-page` работает). Визуальный паритет summary (карты tertiary/mono-цифры/тихие заголовки/hover ошибок) — первым шагом parity-фазы вместе с ▽-baseline (EXAM-сессия малым N → POST /finish). Шаг 2 (вердикт-headline + main+aside грид, правки шаблона) — отдельный тик по README/PORT_MAPPING п.32/39. Контракт-тест по-прежнему PENDING (bootRun жив). Каталог port-drafts пуст от черновиков — дальше журнал решений.
- **cr.43 (ROUND-RESET, R0.46) — ФАЗА E ОТКРЫТА: первый порт ПРИМЕНЁН (error C7-i).** Параллельная сессия закоммитила весь WIP → дерево чисто (блокер снят после 25 тиков). Выполнено по PARITY_QA_CHECKLIST §3: (1) тройная проверка чистоты; (2) C7-i вставлен в base.css сразу после §C7 (перед C3), специфичность-по-порядку соблюдена; (3) head.html base v72→73; (4) live-синк cp в build/resources; (5) QA на живом инстансе: **дельта = прогноз R0.34 бит-в-бит** — instrument card flex-start/left, eyebrow lowercase+text-tertiary, ghost-код 136px/500/lh129.2/border-primary (@1280), title 500/48px/20ch(573.9px), actions flex-start; dark-инверсия корректна (0.71/0.36); **editorial-контроль = baseline R0.34 бит-в-бит** (center/uppercase/64px/700/rgb-числа те же); 320-reflow чист (clamp 70.4px, overflow нет); (6) черновик удалён, README-таблица обновлена. Оговорки: .error-details на 404 не рендерится (как и в baseline — хук проверить на 403/500 при случае); контракт-тест PENDING (живой bootRun, §4 wedge) — вставка чистый append, пиннутые строки не тронуты. Следующий порт: session-summary C6-i (после §C6 ~1972).
- **cr.42 (ROUND-RESET, R0.40)** — **PIXEL_QA_MATRIX сведена с baseline-серией — предподготовка Фазы E/F формально закрыта.** Матрица: parity-ячейки аннотированы `▲` (численный ДО-портовый baseline снят: shell R0.35+375, focus R0.38+375, settings R0.36, stats R0.37, error R0.34) и `▽` (result/summary — baseline требует сессии, §4; снимается первым шагом parity). no-js статик-ячейки → PASS(R0.32) для 4 GET-поверхностей. Фазы столбца обновлены: B_MOCKUP_RECHECK → «D done; E BLOCKED/READY». В блокер-секцию вписан итог 13 тиков предподготовки (R0.28–R0.40) и вывод: read-only повестка ИСЧЕРПАНА, дальше разблокировка (→ error C7-i) или maintenance-режим — сторожевые проверки якорей при изменении WIP-диффа (протокол §28 iterate: не «улучшать нечего», а режим поддержки). Блокер base.css — 20-й тик.
- **cr.41 (ROUND-RESET, R0.39)** — **Mobile-375 baseline focus+shell: обе новые WIP-фичи (HDR-1 compact-clamp + :has(progress)-уплотнение) подтверждены живьём.** Masthead-title 18.75px (=5vw в клампе 1.15–1.35rem) при вопросе 27px → ratio 1.44 (было бы ~1.05 без фикса — ровно то, что чинил HDR-1). Уплотнение шапки при активной сессии живо (inner p8×16, row-gap 4). Нав одной строкой, тогглы 136×40 в верхнем ряду, submit 327×48 (touch ≥44), reflow чист 375/375. Layout БИТ-ИДЕНТИЧЕН между editorial и instrument (структурные правила дизайн-независимы) → мобильная вёрстка при порте не трогается вообще, инструмент-порт обязан сохранить эти числа (контракт-тест их пиннит). Всё вписано в PARITY_QA_CHECKLIST §2/shell. Блокер base.css — 19-й тик.
- **cr.40 (ROUND-RESET, R0.38)** — **Численный baseline focus (GET / @1280, active-MCQ, 4 состояния) — снята ПОСЛЕДНЯЯ поверхность, доступная read-only.** Вопрос/опции/submit/progress → PARITY_QA_CHECKLIST §2/focus. Ключевая находка для порта focus: editorial submit-кнопка «приклеена» к низу карты (radius `0 0 8px 8px`), а instrument token-only даёт полный r10 → сейчас в instrument живёт композиционный гибрид (скруглённая кнопка, визуально всё ещё прижатая к карте) — при порте решить композицию по макету (submit отделён) и не оставлять полускруглый гибрид. Опция-карты/шрифты/прогресс уже в языке (r10, Newsreader/Space Grotesk, teal). Не сматчились: topic-badge/session-counter/details-подсказка (не рендерятся на этом состоянии или иная разметка). Reflow чист. Все read-only baseline сняты: error/shell/settings/stats/focus; summary/result — только с сессией (§4). Блокер base.css — 18-й тик.
- **cr.39 (ROUND-RESET, R0.37)** — **Численный baseline stats (GET /stats @1280, 4 состояния) — серия read-only baseline-ов ЗАМКНУТА (error R0.34, shell R0.35, settings R0.36, stats R0.37).** Снято: stat-карты/values/labels, section-title, таблица (th/td), фильтр-инпуты, fallback, expander → PARITY_QA_CHECKLIST §2/stats. Живьём подтверждён новый collapse: 319 строк / 307 tr[hidden] (WIP-механизм работает; R0.32-вывод про развёрнутый served HTML не противоречит — это JS после загрузки). Instrument token-only уже несёт язык и тут (Newsreader/ls 0.24/r10-карты/r6-инпуты). Каверза: expander r0 в ОБОИХ дизайнах — осознанный B5 «плоский радиус», при порте не «чинить». Несматчившиеся селекторы (topic-link/filters-form/forecast-*) — брать фактические классы из served DOM на parity. Reflow чист. Baseline session-summary/result остаются невозможными без сессии (§4) — снимутся в parity-фазе первыми. Блокер base.css — 17-й тик.
- **cr.38 (ROUND-RESET, R0.36)** — **Численный baseline settings (GET /settings @1280, 4 состояния, вкладка «Оформление» открыта, settle 400ms).** Снято: tablist/актив-вкладка/card/seg-controls (актив+idle)/label/hint/danger/secondary — вписано в PARITY_QA_CHECKLIST §2/settings. Наблюдения: (1) editorial UI-шрифт = Inter, instrument = Space Grotesk УЖЕ применяется token-only (шрифтовой слой порта settings фактически готов); (2) радиусы дифференцируются токенами (editorial seg/btn r0, instrument r6/r10) — «форма» инструмента тоже уже живёт; (3) активная seg-кнопка editorial = тёмный текст на терракоте в ОБЕИХ темах (осознанный editorial-паттерн, контроль бит-в-бит), instrument = светлый-на-teal light / тёмный-на-светлом-teal dark — корректная инверсия; (4) селекторы `.switch input`/`.set-card` не сматчились — фактическую разметку switch-тогглов уточнить в served DOM перед parity-замером. Reflow чист 1265/1265. Порт settings шаг 1 после этого baseline — преимущественно тихий рестайл поверх уже работающих токенов. Блокер base.css — 16-й тик.
- **cr.37 (ROUND-RESET, R0.35)** — **Численный baseline shell-хрома (GET / @1280, 4 состояния: editorial/instrument × light/dark) + методологическая каверза transition-lag.** Первый замер дал ложную аномалию: navLink.color и toggle.border идентичны во всех 4 состояниях (заморожены на light-значениях) — причина НЕ дефект прода, а `transition: color .15s` на `.ed-nav-link`/тогглах: синхронный флип data-theme + мгновенный getComputedStyle читает ДО-переходное значение. Перемер с settle 400ms — все 4 состояния корректны (dark nav `rgb(240,238,230)` editorial / `oklch(0.94 0.008 258)` instrument). Правило вписано в PARITY_QA_CHECKLIST §2/shell: после флипа темы/дизайна ждать ≥400ms до чтения computed-цветов. Baseline вписан туда же (masthead/kicker/title/nav/active/тогглы, все 4 состояния); кандидат дельты порта: instrument title weight 700→500 (сверить с макетом shell). Sticky отсутствует и в живом DOM (`position: relative` везде) — согласуется с контракт-запретом cr.36. Блокер base.css — 15-й тик.
- **cr.36 (ROUND-RESET, R0.34)** — **Sticky-вопрос shell ЗАКРЫТ + численный baseline error ДО порта.** (1) Запрет sticky-шапки перенесён из cr.35 в канонический PORT_MAPPING_shell.md (строка маппинга .site-head + риск №6→РЕШЕНО, +риск №7 про :has(progress)-уплотнение и compact-clamp HDR-1 — instrument-порт их не перебивает). Макетное решение C3 остаётся макетным. (2) Живой численный baseline error-страницы @1280 (404, обе темы editorial + instrument token-only light) снят и вписан в PARITY_QA_CHECKLIST §2/error вместе с ожидаемой дельтой C7-i — после порта паритет доказывается диффом чисел, editorial обязан остаться бит-в-бит. Каверза: рестарт chrome-devtools наследует последний emulate (375 mobile) — перед desktop-замером явно ставить 1280x900x2. localStorage возвращён в instrument+light (состояние, оставленное R0.30). Блокер base.css — 14-й тик.
- **cr.35 (ROUND-RESET, R0.33)** — **Дифф-ревизия ВСЕХ маппингов Фазы D против разросшегося WIP** (теперь накрывает app.js/stats.js/4 шаблона/head.html/контракт-тест; base.css dirty 13-й тик). Итог: черновики C6-i/C7-i валидны (якоря §C6=1857/§C7-тейл=2070 не тронуты, все хук-селекторы на месте); маппинги focus/result валидны бит-в-бит (в шаблонах только бамп v56). Дрейф: (1) **stats collapse сменил механизм** — `tr[hidden]`+MutationObserver вместо max-height+fade → parity-QA проверяет скрытие строк/Tab-порядок/print-разворот; (2) **settings +#personalization-status** (live-region, announceSaved) и новая сигнатура wireSegControl(+label); (3) **shell: sticky-шапка ЗАПРЕЩЕНА прод-контрактом** — новый TemplateFragmentContractTest ассертит doesNotContain sticky (FNO-риск) → макетное решение C3 (sticky+solid) в прод НЕ портируется; (4) контракт-тест пиннит ТОЧНЫЕ строки base.css → после любой вставки Фазы E обязателен прогон теста; (5) focus HDR-1 правила (compact-clamp тайтла + :has(progress)-уплотнение) — instrument-порт не должен их перебивать. Всё записано в port-drafts/README.md §R0.33.
- **cr.34 (ROUND-RESET, R0.32)** — **No-JS контракт-аудит серверного HTML (curl GET-смоук, живой инстанс) — ВСЕ PE-инварианты зелёные.** 4 страницы сняты curl'ом в scratchpad (focus/settings/stats/error). (1) **PE-hidden уходит с сервера**: все 3 тоггла шапки (`ed-layout-toggle hidden`, `ed-design-toggle hidden`, `theme-toggle … hidden`), settings-tablist, personalization-card, data-export-block, streak-bar — все с `hidden` → без JS ни одного мёртвого контрола. (2) **Живое без JS на месте**: focus `#interview-form`+noscript-алерт; settings filters-form/session-form/danger-btn (по 1); stats таблица РАЗВЁРНУТА (0 `tr[hidden]`, table-expander hidden — collapse чисто клиентский), chart-fallback в DOM; error статичен (actions=1). Каверза грепа: порядок атрибутов в HTML ≠ порядок в шаблоне — грепать классы по-отдельности, не паттерном «class="X Y"». Оговорка: снято с WIP-инстанса (живой bootRun параллельной сессии, java :8080) — при parity после портов пересъёмка не нужна, инварианты структурные. PIXEL_QA_MATRIX no-js: TODO→PASS(static); интерактивный no-JS смоук (сабмиты форм, result-фоллбэк) — на parity-фазе (мутации базы, §4). Предподготовка Фазы E/F этим ИСЧЕРПАНА: baseline (cr.31–33) + no-js смоук сняты, все 7 маппингов и 2 черновика готовы — дальше только разблокировка base.css.
- **cr.33 (ROUND-RESET, R0.31)** — **Живой baseline stats (instrument token-only) —
  зелёный; ложная тревога по графикам разобрана.** base.css dirty (11-й тик) → продолжение
  baseline Фазы F на живом инстансе. Сначала показалось «графики мертвы» (fallback
  виден, Chart.instances пуст) — глубокий зонд показал: это **легитимный cold-start
  фолбэк** stats.js («Пока нет активных тем. Начни отвечать…»): после wipe базы
  2026-07-08 нет отвеченных вопросов; Chart.js 4.5.0 загружен, `#topic-stats-data`
  парсится (массив 319 тем), canvas скрыт осознанно. Отличать два фолбэка: cold-start
  (норма) vs «График временно недоступен» (СТОП-сигнал) — в PARITY_QA_CHECKLIST добавлена
  каверза + требование ответить на 3–5 вопросов перед замером цветов графиков (иначе
  parity цветов мерить нечего). Остальной baseline stats: reflow чист 1280/375
  (sw==cw), **stacked-card режим таблицы живой на 375** (td display:grid, data-label),
  collapse работает (319 строк → is-collapsed), next-actions с is-recommended кольцом,
  фильтры/поиск рендерятся, cold-copy «Слабая тема появится после первых ответов» уже
  человечная. forecast/gaps секции скрыты (нет данных — th:if, корректно). Скрин 1280
  light цельный. Итог: stats в instrument token-only работоспособна; порт C-блока —
  чистое улучшение.
- **cr.32 (ROUND-RESET, R0.30)** — **ЖИВОЙ baseline instrument (token-only) на прод-инстансе —
  ЗЕЛЁНЫЙ.** base.css dirty (10-й тик) → порты стоят; вместо простоя снят живой baseline
  Фазы F: обнаружено, что :8080 уже слушает чужой java-инстанс (вероятно devtools-bootRun
  параллельной сессии; **урок: живость bootRun проверять `lsof -i :8080`, не ps** — R0.27
  тест прогонялся при живом инстансе, повезло без wedge). Аудит read-only через chrome MCP
  (localStorage design=instrument): **(1)** токены применяются (body bg = oklch instrument
  paper), **(2)** все 3 шрифта реально загружены (document.fonts.check: Newsreader /
  Space Grotesk / JetBrains Mono = true), **(3)** reflow чист на 1280/375/320 (sw==cw,
  0 wide-элементов), **(4)** обе темы рендерятся цельно (скрины focus 1280 light+dark:
  Newsreader-вопрос, ring-бейджи A–D, teal-акценты), **(5)** /settings: вкладки живы,
  seg-btn Instrument aria-checked=true, **NAMES-фикс R0.27 подтверждён живьём**
  (title «Дизайн: Instrument → Editorial»), **(6)** error 404 в instrument уже пристойна
  на общей центрированной структуре (C7-i даст левую композицию макета). Итог: instrument
  как token-only дизайн УЖЕ работоспособен и чист — черновики C6-i/C7-i являются
  улучшением, не починкой; parity-риск портов низкий. Каверза: живой инстанс может
  отдавать WIP-статику параллельной сессии — метрики помечены как «снято с WIP-инстанса».
  Пользовательский localStorage: theme возвращён в light (был light), design оставлен
  instrument (прежнее значение не зафиксировано — переключается в /settings одним кликом).
- **cr.31 (ROUND-RESET, R0.29)** — **Пересверка черновиков против дрейфа WIP: валидны.**
  base.css dirty (9-й тик подряд) → порты стоят; по плану R0.28 выполнена read-only
  сверка обоих черновиков с диффом чужого WIP в base.css. Результат: ханки WIP лежат
  в зонах masthead-title/topic-table-wrap/today-hero/summary-tools-status и НЕ трогают
  секции §C6 (1855) и §C7 (1975); все якоря черновиков на месте (C7-i 13 совпадений,
  C6-i 8/8 селекторов). Точки вставки уточнены по working-tree (~1972 / ~2070) и
  записаны в port-drafts/README. **Бонус-находка:** WIP добавляет
  `#interview-options label.option-wrong:has(input:checked)` (+12 строк, ханк 3592) —
  параллельная сессия работает в зоне будущего focus-порта; каверза R0.22
  («пересверить хуки focus по актуальному дереву») подтверждена делом — при порте
  focus сверка app.js-классов ОБЯЗАТЕЛЬНА. Конвейер полностью готов, блокер один:
  base.css.
- **cr.30 (ROUND-RESET, R0.28)** — **Подготовка Фазы F: PARITY_QA_CHECKLIST.md.**
  base.css dirty → порты стоят; независимые прод-правки исчерпаны (R0.27) → собран
  исполняемый чеклист parity-QA, чтобы Фаза F не изобреталась на каждом тике:
  (0) предусловия запуска со всеми собранными граблями (postgres 5432-факт vs
  устаревшая 5433-заметка, SPRING_DATASOURCE_URL, wedge-запрет сборки при bootRun,
  cp-синк статики, бамп ОБОИХ v=, emulate 320, instrument = localStorage/клиентски);
  (1) матрица прогона: instrument + editorial-контроль × 2 темы × 7 вьюпортов DPR2 +
  320 reflow + 640, метрики evaluate_script (reflow/шрифты/токены/фокус-кольца/AA
  спот-чек aa_shell); (2) пер-поверхностные сценарии ДОЕЗДА (как добраться до
  каждого состояния: /finish с _csrf для summary, отключение JS для result-фоллбэка,
  FLASHCARD-сессия для grade-веток) + точки паритета и регресс-чеки из маппингов
  (включая «известные»: мёртвые aiEnabled-гейты не чинить в parity, цвета Chart.js
  ещё editorial — known рассинхрон шага 1); (3) сжатый порядок применения при
  разблокировке (черновики → очередь маппингов, editorial-контроль после каждого
  порта — instrument скоупнут, editorial обязан быть бит-в-бит); (4) выходные
  артефакты прогона (matrix/скрины/журнал). Все 4 фазовых артефакта конвейера
  готовы: маппинги 7/7, черновики 2, чеклист F — ожидание разблокировки.
- **cr.29 (ROUND-RESET, R0.27)** — **Фаза E, ПЕРВАЯ прод-правка раунда: микро-фикс NAMES
  (instrument) в header.html.** base.css всё ещё dirty (порт CSS блокирован), но
  header.html чист → применена независимая правка из находки cr.28: `instrument:
  'Instrument'` добавлен в NAMES-словарь дизайн-тоггла и в фолбэк-список order()
  (реальный источник __design.list уже содержал instrument — фолбэк догнал). Теперь
  aria-label/title тоггла показывают «Instrument», а не сырой id. Заодно комментарий
  «реестр из 10» → «из 11» (+опечатка «роестр»). **QA:** живого bootRun нет (проверено
  ps) → TemplateFragmentContractTest прогнан — зелёный (exit 0). Прод-WIP параллельной
  сессии не тронут (правка только header.html, explicit pathspec). Схема §6 соблюдена:
  правка не касается ни одного dirty-файла.
- **cr.28 (ROUND-RESET, R0.26)** — **D-маппинг shell/хром — ФАЗА D ИСЧЕРПАНА (7/7).**
  base.css/app.js/head.html dirty (header.html/icons/today-widget ЧИСТЫ); снят финальный
  маппинг: `PORT_MAPPING_shell.md`. **Крит-решения:** (1) мобильный drawer макета НЕ
  портировать — прод-нав из 3 всегда видимых ссылок проще и работает без JS; (2) футер:
  в проде НЕТ ни одного `<footer>` — добавление на все страницы = продуктовое решение,
  открытый вопрос пользователю (Фаза G), в порт не идёт; (3) ?-кнопка справки в шапку —
  шаг 2 (kbd-help модалка УЖЕ живёт в app.js, открытие по `?` — гейт-паттерн юзера);
  (4) прод-паттерн тоггла темы (быстрый флип, «Авто» на /settings) сохранён — осознанное
  разделение; (5) порт shell ≈ чистый base.css-рестайл, DOM header.html не трогается;
  4 inline-скрипта (theme/design/layout-синк + back-to-top с data-motion гейтом) —
  контракт. **Две находки:** (а) шрифты instrument (Newsreader/Space Grotesk) УЖЕ
  подключены в head.html — шрифтовой риск порта снят; (б) **NAMES дизайн-тоггла в
  header.html не знает instrument** → aria/title показывают сырой id; 2-строчный
  микро-фикс НЕ блокирован (header.html чист!) — единственная доступная прод-правка,
  кандидат ближайшего тика Фазы E. **Итог фазы D:** 7/7 замаплено, 2 черновика
  ready-to-paste; дальше только Фаза E по разблокировке.
- **cr.27 (ROUND-RESET, R0.25)** — **D-маппинг settings (последняя страничная поверхность).**
  settings.html/base.css/app.js dirty → порт BLOCKED; снят маппинг:
  `PORT_MAPPING_settings.md`. **Главная находка — структуры уже изоморфны:** прод прошёл
  вкладочный редизайн (0eb762a5, ARIA-вкладки + PE) до макета, поэтому settings — самый
  дешёвый из «блокированных» портов: **шаг 1 = чистый CSS-рестайл прод-DOM** (карточная
  сетка 7 осей `.personalization-row`→set-card вид через auto-fit minmax(min(20rem,100%),1fr);
  switch-вид тогглов на существующих checkbox БЕЗ смены порядка узлов; вкладки; danger-zone
  уже концептуально совпадает). **Шаг 2 (отдельное решение):** вертикальный tablist-рельс
  макета = шаблон (aria-orientation) + app.js (стрелки ↑/↓ по WAI-APG) + иконки; panel-lead
  копия. **Alertdialog сброса (макет R0.10) НЕ портируется в шаг 1** — прод-`data-confirm`
  (нативный confirm) доступен и прост; кастомный диалог = Фаза G low-prio. Не задеть:
  контракт-тест ID форм/вкладок, `data-default-count` (SET-15), TRAINING-скрытие счётчика,
  PE-паттерны (tablist/personalization/export скрыты без JS, экспорт = fetch с
  X-Admin-Token). Registry: settings → MAPPED. **Замаплено 6/7 — остался только
  shell/head (хром-фрагменты header/head/icons).**
- **cr.26 (ROUND-RESET, R0.24)** — **D-маппинг stats (JS-тяжёлая: Chart.js + сортировка +
  PE-скрипты).** stats.html/stats.js/base.css dirty → порт BLOCKED; снят маппинг:
  `PORT_MAPPING_stats.md`. **Крит-решения:** (1) статические SVG-графики макета — это
  QA-заглушки, в прод НЕ идут: графики рисует Chart.js из `topicStatsJson` (блок
  `[(...)]` unescaped — не трогать, иначе тихо мёртвые графики); (2) цвета/шрифты
  графиков живут в stats.js-конфиге, не в CSS → шаг 1 порта графики не перекрашивает
  (временный рассинхрон допущен и фиксируется в parity-QA); (3) сорт-стрелку рисовать
  CSS `[aria-sort]::after` — DOM-span макета не переносить (aria-sort ставит stats.js,
  анонс через #table-sort-status, паттерн C28); (4) прод-фичи БЕЗ макетного аналога
  сохранить целиком: collapse 12 строк (MutationObserver, PE-развёрнута без JS),
  Intl-гуманизация дат прогноза, stacked-card мобайл-режим таблицы (data-label, C30),
  формы фильтров+серверного поиска с перекрёстными hidden (макет их пропустил осознанно);
  (5) live-фильтр ТЕМ из макета (R0.16) ≠ серверный поиск ВОПРОСОВ — не путать фичи;
  фильтр тем = кандидат Фазы G (stats.js, с синком к collapse); (6) одноколонка
  сохраняется в шаге 1; вынос прогноза/пробелов в правую рейку = шаг 2 по отдельному
  решению; (7) фрагмент stats-grid общий с result — рестайл проверять на обеих.
  Registry: stats → MAPPED. Замаплено 5/7.
- **cr.25 (ROUND-RESET, R0.23)** — **D-маппинг result (самый тонкий контракт: живой no-JS
  фоллбэк).** Тройка base.css/result.html/app.js dirty → порт BLOCKED; снят маппинг:
  `PORT_MAPPING_result.md`. **Крит-решения:** (1) страница обязана работать целиком без
  JS — confidence-виджет макета (🎲🤔💪) сюда НЕ портируется (нет POST-канала на no-JS
  пути, новый endpoint запрещён §4; уверенность живёт в AJAX-флоу focus — там и стили);
  (2) `v-sub` вердикта («B → A», «+1 к стрику») не портируется — серверных данных нет;
  (3) прод-опции БОГАЧЕ макета (role=list из div-listitem, пер-опционные
  `.option-explanation`, статус-лейблы, aria-префиксы) — порт идёт ОТ прод-структуры,
  макет даёт только тон; буква опции на result в тексте (не CSS-счётчик, в отличие от
  focus!); (4) `#result-related-questions` — прямой потомок `.ed-page` (`:has`-грид
  правой рейки), в card не заворачивать; (5) takeaway/trace макета — AI-вырезано,
  не портировать. **Две находки:** (а) прод-фрагмент `inline-alert` ПОЯВИЛСЯ
  (`#interview-alert` role=alert, наполняет app.js) — R0.22-заметка «нет продьюсера»
  устарела для error-тона (dismiss всё ещё без продьюсера); (б) **НОВЫЙ дефект-кандидат:
  `.question-side` (код вопроса) гейтится `aiEnabled=false` → код на no-JS result никогда
  не рендерится**, хотя codeSnippet есть и focus его показывает — отображение кода не
  должно зависеть от AI-флага; НЕ чинить молча (шаблонная логика), пункт Фазы G/решение
  пользователя, записан рядом с btn-regenerate (R0.17). Registry: result → MAPPED.
  Замаплено 4/7 поверхностей.
- **cr.24 (ROUND-RESET, R0.22)** — **D-маппинг focus-training (самая сложная поверхность).**
  base.css/focus-training.html/app.js dirty → порт BLOCKED; независимых черновиков больше
  нет → по плану R0.21 снят полный маппинг ядровой поверхности:
  `PORT_MAPPING_focus-training.md` (макет 7 состояний ↔ прод-шаблон + app.js v56 + 5
  фрагментов + контракт-тест). **Ключевые крит-решения:** (1) боковой рельс макета НЕ
  портируется — горизонтальный topbar это зафиксированное решение пользователя round-01
  («вертикальная рейка выглядела по-любительски»), instrument-голос кладётся на topbar;
  (2) буква варианта в проде — CSS-счётчик `label::before` с осознанным срезанием "X. "
  только в видимом span → DOM-бейджи макета не переносить (дубль буквы + рассинхрон
  aria-label); (3) result-состояние на этой странице рисует app.js (AJAX) → стилизовать
  фактические app.js-классы, не статику макета; (4) SM-2 reveal/grade строго серверные
  POST (3 ветки: browse-details / /flashcard-reveal / grade 1–4); (5) inline-alert и
  loading-скелет не имеют прод-продьюсера → НЕ вводить мёртвый UI, кандидаты Фазы G
  (AJAX-fail и submit-переход app.js); (6) empty покрывает и done (finished-ветка),
  отдельного состояния не создавать. Каверза: маппинг снят с working-tree WIP-версии
  шаблона — пересверить хуки перед портом. Registry: focus-training → MAPPED.
- **cr.23 (ROUND-RESET, R0.21)** — **Фаза E prep №2: черновик порта session-summary
  (шаг 1, CSS-only) → `port-drafts/`.** base.css всё ещё dirty → порт BLOCKED, подготовлен
  второй ready-to-paste блок **C6-i** (`session-summary-instrument-base.css`, ставить ПОСЛЕ
  общего §C6): instrument-рестайл СУЩЕСТВУЮЩИХ прод-хуков без правок DOM — `.card` на
  приподнятой bg-tertiary/line (кольца вместо утопленных фонов), mono-цифры плиток
  (`tabular-nums`, «приборная панель»), тихие body-семиболд заголовки секций вместо
  display-xl, lowercase mono-шёпот `.summary-mode-line` (text-tertiary 5.4:1), hover
  ошибок paper+accent-strong. Тон-классы точности не трогаются — instrument-токены сами
  переопределяют статусы. **Скоуп-решения:** (а) двухколоночный грид main+aside и
  вердикт-headline требуют правок шаблона → отложены на шаг 2 Фазы E (в README перечислены
  с рисками №1 a11y-роли таблицы / №3 PE-инъекция tools / №2 плоские recommendations);
  (б) **print НЕ портируется** — прод base.css уже несёт общий `@media print` (~3208) с
  полным summary-покрытием, print-блок макета был mockup-QA (R0.19), дубль не нужен.
  QA черновика: токен-ревью (text-tertiary/primary пары на tertiary/paper ≥5.4:1 обе темы
  по f2.2-верифицированным токенам); detector неприменим (не HTML). Оба независимых
  черновика готовы: error (cr.22) + session-summary шаг 1 — Фаза E стартует мгновенно
  по разблокировке base.css.
- **cr.22 (ROUND-RESET, R0.20)** — **Фаза E, read-only подготовка: черновик порта error →
  `port-drafts/`.** Collision guard: base.css по-прежнему dirty (чужой WIP) → сам порт BLOCKED,
  подготовлен ready-to-paste черновик. Созданы `port-drafts/README.md` (процедура применения,
  токен-маппинг словарей, правило специфичности: блок `="instrument"` равен по весу общему
  `html[data-design]` → обязан стоять ПОСЛЕ §C7) и `port-drafts/error-instrument-base.css`
  (блок C7-i: левосторонняя instrument-композиция поверх общей центрированной — lowercase
  mono-eyebrow text-tertiary, призрачный clamp-номер 4rem→8.5rem в тоне границы (декор,
  aria-hidden), title 3xl balance 20ch, детали на bg-tertiary/line, actions flex-start).
  **Ключевое решение (риск №2 PORT_MAPPING_error.md):** `.error-rail` НЕ портируется — все
  3 ссылки рельса дословно дублируют `.error-actions`, уникальный `rail-hint` уже покрыт
  серверным 403-body → порт error = чистый append в base.css, **ноль правок Thymeleaf**
  (минимальная поверхность конфликта, DOM-контракт §2/§8 не тронут). Черновик никем не
  загружается (не подключён ни к макетам, ни к проду). QA черновика: только токен-ревью
  (все роли на AA-токенах instrument; detector неприменим — не HTML-страница). Применение
  (по разблокировке): вставка после §C7 + бамп `?v=` в head.html + live parity QA + удаление
  черновика тем же коммитом.
- **cr.21 (ROUND-RESET, R0.19)** — **print-дименсия: print-CSS для `session-summary`.**
  Единственная страница с реальным печатным сценарием (в проде кнопка «Печать» в
  `.summary-tools`; в макете — в `.share-row`). Принцип: бумага получает ДОКУМЕНТ, не
  приложение — скрыты mock-bar/шапка/skip-link/кнопки действий/стрелки `.m-go`/интерактивная
  подсказка `.mistakes-hint`; одна колонка (grid→block, sticky→static); чёрным по белому 11pt;
  таблица тем с рамками 1px #000; `break-inside: avoid` на плитках/секциях/строках/ошибках;
  анимации off. **Верификация (без print-preview в MCP):** CSSMediaRule распарсен (16 правил);
  автопроверка «мёртвых» селекторов print-блока против DOM = 0 (нет опечаток); экранный рендер
  не тронут (overflow 0); detector exit 0. Живой print-preview — при parity-фазе (Playwright/
  печать из браузера). Остальные 6 макетов print не получают намеренно: печатных сценариев нет,
  прод-print — концерн base.css (BLOCKED). Дальше — Фаза E по разблокировке; матрица дименсий
  почти закрыта (no-js/parity — фазные).
- **cr.20 (ROUND-RESET, R0.18)** — **закрыта дименсия zoom200/reflow: живой браузер-аудит
  320px+640px всех 7 макетов через chrome-devtools MCP (эмуляция вьюпорта; окно Chrome не
  жмётся <485px — resize_page недостаточен).** Методика: `document.documentElement.scrollWidth
  > clientWidth` по КАЖДОМУ mock-состоянию (focus 7 состояний + flashcard-revealed; result 2
  варианта + details-open; settings 3 вкладки + reset-confirm; shell + mobile-menu + kbd-help;
  stats + search-empty; summary 2 варианта; error). Найдено 2 реальных дефекта (C18): explain-код
  токен 286px (1px overflow ×2 файла) и set-grid minmax 320px (35px overflow, все 7 карт
  «Оформления»). Пофикшены минимально (`overflow-wrap: break-word`; `min(20rem,100%)`).
  Ре-верификация: 320 и 640 чисто по всем состояниям; detector exit 0 ×3. Легитимные
  скролл-контейнеры (pre.code-body, tablist) не считаются overflow-дефектами (1.4.10 exception).
  Дименсия «zoom 200% / DPR1» в матрице: TODO → PASS(320/640 live). Дальше — print-дименсия
  либо Фаза E по разблокировке.
- **cr.19 (ROUND-RESET, R0.17)** — **закрыт C13: read-only аудит показал, что regenerate —
  мёртвый прод-UI.** Кнопка `btn-regenerate` в прод `result.html` гейтится `th:if="${aiEnabled}"`,
  а `aiEnabled` захардкожен `false` в ОБОИХ местах, где кладётся в модель
  (`MvcModelAttributeMapper.java:152`, `InterviewPageMvcService.java:166`) — следствие полного
  вырезания AI 2026-07-07 (memory `project_ai_removal`). Значит: (1) кнопка в проде никогда не
  рендерится → макет ПРАВИЛЬНО её не содержит, «недостающее состояние» было фантомом
  registry-списка; (2) при порте btn-regenerate/regen-badge НЕ портировать; (3) для Фазы G
  зафиксирован dead-code кандидат: `btn-regenerate` + `regen-badge` в шаблоне, `regenerateQuestion`
  + `API.REGENERATE` в app.js (правка прод-файлов сейчас BLOCKED — только заметка). Остаток C13
  no-js-fallback = живой `/answer` (RECHECK в дименсиях, parity-фазный контракт-концерн, не
  визуальное состояние макета) → C13 **DONE**. Изменений в макетах нет — тик чисто аудиторский,
  QA-прогоны не требуются. Бэклог макетов пуст (C1–C17 все DONE/РЕШЕНО); дальше — Фаза E по
  разблокировке либо независимые дименсии (zoom200/DPR1/print методология).
- **cr.18 (ROUND-RESET, R0.16)** — **закрыт C16: поиск по темам в `stats`.** Проверил прод
  (read-only): поиск реально существует (`stats.html` search-pane, `stats.js` хоткей «/» +
  guard модалки) — фича заметная, важнее мелкой admin-gated кнопки regenerate (C13-остаток).
  Добавил в секцию «Детализация по темам»: `role=search` форма (visually-hidden label,
  иконка-лупа, `type=search`, placeholder с примерами) + kbd-подсказка «/» фокус · Esc сброс
  (`aria-hidden` — дубль хоткеев, SR получает label формы). Live-фильтр по имени темы:
  скрытие строк, счётчик «N из M тем» (`role=status aria-live=polite`), при нуле — скрытие
  таблицы + строка «Ничего не нашлось. Попробуй короче: „aop", „транз"» (конструктивный
  empty, не тупик). Хоткей «/» глобальный, но не из текстовых полей; Esc в инпуте сбрасывает
  запрос со `stopPropagation` (не закрывает будущие оверлеи). **QA:** detector exit 0; AA обе
  темы ≥4.5 (input 15.55/15.49, placeholder/hint ink3 5.75/7.18, счётчик 7.77/9.23,
  kbd 6.56/7.72). C16 **DONE** (empty/cold-start намеренно не делаем — прод-решение). Дальше —
  C13-остаток (regenerate) / порт по разблокировке.
- **cr.17 (ROUND-RESET, R0.15)** — **пивот на C13: добавлен confidence-виджет в `result`.**
  C12 остаточные состояния обесценились (generationUnavailable ≈ покрыт flashcard+info-alert
  R0.12/R0.13; no-js — parity-фазный контракт-концерн, не отдельный визуал; diagram — §4-бан
  Mermaid), поэтому worst-first-по-ЦЕННОСТИ переключился на C13 confidence — реальный компонент,
  намеренно живущий в проде. Виджет: «Насколько был уверен в ответе?» + 3 уровня (🎲 Угадал /
  🤔 Не уверен / 💪 Уверен) после вердикта (порядок вердикт→уверенность→кнопка). `role=radiogroup`
  (`aria-labelledby`), каждая кнопка `role=radio`+`aria-checked`, roving tabindex + стрелки/Home/
  End (паттерн settings seg-control = консистентность), эмодзи `aria-hidden` + текст-лейбл (на
  <460px только глиф). Прод намеренно держит эмодзи 🎲🤔💪 (memory `project_icon_system`) —
  честно перенёс их сюда, при порте это точка решения (эмодзи vs монохром Lucide), отмечено в
  CSS-комменте. **Слоп-ловушка (детектор):** `1.15em` глиф добавил 18.4px-шаг рядом с 16px =
  [flat-type-hierarchy] → убрал кастомный размер глифа (эмодзи наследует fs-sm). **QA:** detector
  exit 0 (после фикса); AA обе темы ≥4.5 (conf-q ink2/surface 7.18/8.57; текст ink/paper 15.55;
  checked signal-ink/signal-wash 6.54/6.87). Остаются 2 (regenerate/no-js) → C13 IN_PROGRESS.
- **cr.16 (ROUND-RESET, R0.14)** — **C12 дальше: добавлено состояние `loading` (скелет
  загрузки) в reference-экран.** Worst-first после inline-alert (R0.13) — следующий недостающий
  кусок C12, P1 loading-state. Скелет **повторяет раскладку вопроса** (не спиннер): рейка
  (узкая строка + трек прогресса), тема, 2 строки вопроса (полная + короткая), 4 опции-скелета
  с квадратным badge + 1–2 строками. Блоки `.skeleton` (surface-2, `sk-pulse` 1.4s) декоративны
  `aria-hidden`; регион `.focus-main` = `role=status`+`aria-live=polite`+`aria-busy=true` с
  visually-hidden «Загрузка вопроса…» (SR слышит статус, не скелет). Пульс отключается под
  `prefers-reduced-motion`. **QA:** detector exit 0; AA не применяется (скелет — чистая
  декорация, WCAG 1.4.11 исключает); блоки видимы как светло-серые на paper/surface, JS не нужен
  (статичный скелет). Остаются 3 (generationUnavailable/no-js/diagram; diagram отложен по §4) →
  C12 IN_PROGRESS. Дальше — generationUnavailable/no-js либо C13/C16.
- **cr.15 (ROUND-RESET, R0.13)** — **C12 дальше: добавлено состояние `alert` (inline-alert
  fragment) в reference-экран.** Worst-first после flashcard (R0.12) — следующий по ценности
  недостающий кусок C12 = переиспользуемый inline-alert (прод-фрагмент `inline-alert.html`,
  P1 error/notice-сообщения, полностью отсутствовал). Демо-состояние показывает 3 варианта в
  колонке main: **error** (`role=alert`, «Не удалось сохранить ответ»), **info** (`role=status`,
  «Режим флешкарты» — заодно документирует messaging generationUnavailable), **warn**
  (`role=status`, «Скоро повтор»). Каждое: иконка (alert-triangle / info-circle) + заголовок
  (семантический ink) + текст (`--ink` для читаемости) + dismiss-кнопка (`aria-label`, закрытие
  делегированным click-JS). Цвета: `--error-ink`/`--signal-ink`/`--spark-ink` на соответствующих
  `*-wash`. **QA:** detector exit 0; AA обе темы все пары ≥4.5 (семантический ink на wash:
  error 5.52/5.24, signal 6.54/6.87, spark 5.41/7.76; текст ink на wash 11–14); прозе без
  em-dash (двоеточия/точки — урок cr.14). Остаются 4 состояния (generationUnavailable/loading/
  no-js/diagram) → C12 IN_PROGRESS. Дальше — следующее состояние C12 либо C13/C16.
- **cr.14 (ROUND-RESET, R0.12)** — **закрыт coverage-gap C12 (частично): добавлено состояние
  `flashcard` (изучение / study-LEARN) в reference-экран.** После замыкания recheck (R0.11)
  worst-first — самый крупный coverage-gap (focus-question, 7 недостающих состояний). Добавлен
  целый режим взаимодействия, полностью отсутствовавший: **reveal→grade**. Под-состояние 1 —
  ответ скрыт, кнопка «Показать ответ» (disclosure: `aria-expanded`/`aria-controls`, Пробел или
  click, скоуп-гард «только пока флешкарта видна и ответ скрыт»). Под-состояние 2 — ответ-проза
  (карточка `1px` border + mono-заголовок «Ответ» signal-ink с dot-маркером; фокус переходит на
  ответ, `aria-live=polite`) + самооценка 1–4 (Снова/Трудно/Хорошо/Легко, `role=group`, key-хинт
  + имя, крайние оценки получают семантический край error/success на hover в дополнение к тексту).
  Рейка изучения (Карточка N из M) + aside-хинты (Пробел/1–4/→). **Слоп-ловушки, пойманные
  детектором и исправленные:** (1) первый заход дал `border-left: 3px solid` = [side-tab] (главный
  AI-tell) → полный `1px` border + dot-акцент; (2) [em-dash-overuse] в прозе ответа → тире→
  двоеточия/точки. **QA:** detector exit 0 (после фиксов); AA обе темы все пары ≥4.5 (flash-h2
  signal-ink/surface 6.41/8.32; flash-body ink/surface 14.4; g-key/h2 ink3/surface 5.31; grade
  uniform). Остаются 5 состояний (generationUnavailable/loading/inline-alert/no-js/diagram) → C12
  IN_PROGRESS. Дальше — следующее состояние C12 либо C13/C16.
- **cr.13 (ROUND-RESET, R0.11)** — **замкнут полный 7-поверхностный B_MOCKUP_RECHECK: `shell`
  (глобальный хром) перепроверен + фикс C17.** Последняя поверхность на `RECHECK` (все 6
  страничных пройдены R0.3–R0.10). detector exit 0. AA обе темы — 12 текст-пар хрома
  (шапка/нав/меню/подвал/kbd-help) все ≥4.5 (worst 5.31 ink3/surface; skip/btn 6.01; nav-current
  signal-ink/signal-wash 6.54; kbd 13.1). Графобъект: `.streak-fill` (доля прогресса стрика,
  `width:72%`) `--spark`/`surface-2` = **2.06 light < 3:1 → ПОФИКШЕН** на `--spark-ink` (5.02/
  8.72) — тот же класс, что C15 в stats (декор `--spark` не тронут; flame/count уже spark-ink).
  a11y образцовый (skip→main, main tabindex=-1, nav aria-label, nav-menu-btn aria-expanded/
  controls, theme-toggle aria-label синх JS, kbd-help role=dialog+aria-modal+focus-trap+Esc+
  возврат фокуса, streak role=group, декор-svg aria-hidden). States полны (6): header-nav/
  mobile-drawer/theme-toggle(авто→светлая→тёмная)/kbd-help-modal/skip-link/footer. Порт хрома
  BLOCKED (head.html/header в параллельном WIP); no-js хрома (тема/меню/справка на JS) — проверить
  на bootRun при parity-фазе. **Итог: все 7 поверхностей recheck-PASS; следующий worst-first —
  coverage-gaps C12(focus)/C13(result)/C16(stats) либо parity-фаза, когда base.css разблокируется.**
- **cr.12 (ROUND-RESET, R0.10)** — **закрыт coverage-gap C14: confirm-модалка необратимого
  сброса в `settings`.** worst-first после замыкания recheck — самый ценный gap (data-safety:
  до правки деструктив-кнопка «Сбросить банк вариантов» срабатывала без подтверждения).
  **Добавлено:** `role=alertdialog` overlay (`aria-modal`, `aria-labelledby=reset-confirm-title`,
  `aria-describedby=reset-confirm-desc`), кнопки «Отмена»/«Сбросить банк», JS с focus-trap +
  Esc + возврат фокуса на триггер + scrim-click close; **дефолт-фокус на «Отмена»** (безопасный
  для деструктива); danger-кнопка → `aria-haspopup=dialog`. Overlay-язык = Instrument
  (scrim `color-mix(ink 42%)`, `ov-pop` только в no-preference), как kbd-help в focus-question.
  **QA:** detector exit 0; AA обе темы (title/btn-secondary 15.55, desc 7.77/9.23, btn-danger
  4.95/6.13 — все ≥4.5). Паттерн подтверждения деструктива установлен для порта
  (`POST /settings/reset-options`). Дальше worst-first — C16 (stats states) / C12 (focus states) /
  C13 (result states) ЛИБО Фаза E по разблокировке `base.css`.
- **cr.11 (ROUND-RESET, R0.9)** — **B_MOCKUP_RECHECK экрана `stats` → найден и ПОФИКШЕН
  реальный дефект контраста столбцов (C15).** detector exit 0. **AA обе темы:** все
  ТЕКСТОВЫЕ пары ≥4.5 (acc-тона на surface/paper 5.5–5.95; ov/action signal-ink 6.4–6.5;
  gap-chip 5.41; chart-лейблы 7.18/14.4). **Графобъекты (столбцы, 3:1):** нашёл провал —
  амбер `--spark` на треке `surface-2` = **2.06:1** (`bar-due`, `bar-acc-mid`, `cp-due`,
  `fc-fill.is-today`): данные-бары почти не видны в светлой теме. **ФИКС (локально в
  stats.html):** заливки данных → `--spark-ink` (5.02 light / 8.72 dark), легенда `.sw-due`
  синхронизирована; яркий `--spark` (декор) не тронут. learned↔due — по тону (teal↔амбер,
  CVD-safe) + числовой лейбл, не по яркости (inherent: любой амбер, читаемый на светлом
  треке, близок к teal по L). После фикса: signal/surface-2 3.65, spark-ink/surface-2 5.02,
  success 3.55, error 4.16 — все столбцы ≥3:1. **a11y-каркас образцовый:** SVG-графики
  `role=img`+aria-label (+«данные в таблице ниже»)+fallback `role=status aria-live`; таблица
  `caption`+`th scope=col/row`+sortable `aria-sort`+клавиши Enter/Space+статус-анонс;
  скролл-регион `role=region tabindex=0`; паритет §5 (stats.js хуки)/§6. **states:** покрыты
  overview/charts+fallback/sortable-table/forecast/gaps; НЕ покрыты empty/cold-start (осознан
  в проде) + search → **C16**. **Веха:** recheck всех 7 поверхностей замкнут. Дальше — Фаза E
  по разблокировке `base.css`, либо доработка coverage-gaps (C12/C13/C14/C16).
- **cr.10 (ROUND-RESET, R0.8)** — **B_MOCKUP_RECHECK экрана `settings` (вкладки Сессия/
  Оформление/Данные).** detector exit 0. **AA обе темы:** все пары ≥4.5 (light worst 4.95
  btn-danger `error-on/error`, dark 5.24); danger-карта `ink-2/error-wash` 6.71, `error-ink/
  error-wash` 5.52; seg-btn 6.56–6.93; вкладка выбранная `signal-ink/signal-wash` 6.54; поля
  `ink/paper` 15.55. **a11y ОБРАЗЦОВЫЙ WAI-ARIA:** `role=tablist/tab/tabpanel` + roving
  tabindex + стрелки ↑↓←→/Home/End; seg-control `role=radiogroup`+`aria-checked`+стрелки;
  font-stepper `role=group` + `step-value role=status aria-live=polite`; toggle — скрытый
  checkbox + `.switch aria-hidden` + focus-ring на `input:focus-visible+.switch`; `label
  for/id` на всех select/input. **Паритет §3** (#filters-form/#session-form)/**§8**.
  **full-width** структурно (settings-grid rail+panels, брейк 1080px; set-grid auto-fit
  наполняет ширину; вкладки → гориз-скролл на узких). **states:** покрыты 3 вкладки + export
  json/csv + 7 осей персонализации + filters + session + streak; НЕ покрыт reset-options-
  confirm → **C14** (шаг подтверждения danger-сброса). Дальше worst-first — recheck `stats`
  (последний чистый макет) ЛИБО Фаза E по разблокировке `base.css`.
- **cr.9 (ROUND-RESET, R0.7)** — **B_MOCKUP_RECHECK экрана `result` (разбор ответа).**
  detector exit 0. **AA обе темы:** все текст-пары ≥4.5 (light worst 5.52, dark 5.24);
  ключевое — проза на семантических wash-заливках держит с запасом (ink на signal/success/
  error-wash = 13–14 light, 11–12 dark); verdict `success-ink/success-wash` 5.58, `error-ink/
  error-wash` 5.52; chip-hover/takeaway-icon `signal-ink/signal-wash` 6.54. `opt-mark` галочка
  верного = 4.28 = граф. объект (C11, compliant). **a11y-каркас полный:** progressbar
  aria-valuenow, `role=group` разбора вариантов, `.analysis` aria-label «Разбор глубже»,
  focus-visible ring, reduced-motion fail-safe, иконки aria-hidden, mock-switch aria-pressed.
  **full-width** структурно (result-grid main+aside, брейк 1080px; рельс наполняет правое
  поле — «разбор глубже» takeaway/trace/related). **states:** покрыты correct/incorrect
  (mock-switch) + post-answer-analysis + favorite; НЕ покрыты confidence/regenerate/no-js
  → **C13** (coverage-gap). **Наблюдение на порт:** вердикт — статичный `<p>`, не live-region;
  `aria-live` (§8 `#result-feedback`) проводится при порте, НЕ дефект макета. Дальше worst-first
  — recheck `settings`/`stats` ЛИБО Фаза E по разблокировке `base.css`.
- **cr.8 (ROUND-RESET, R0.6)** — **B_MOCKUP_RECHECK reference-экрана `focus-question`.**
  detector exit 0. **AA обе темы:** все ТЕКСТОВЫЕ пары ≥4.5 (light worst 4.95 badge
  error-on/error, dark 5.24); единственная пара ниже 4.5 — `opt-mark` галочка верного
  `success-on/success` = **4.28 light** → но это **графический объект** (порог 3:1),
  `aria-hidden`, избыточна с обводкой/тегом/бейджем → compliant (C11, РЕШЕНО, без риппла
  общего токена). **a11y-каркас образцовый, паритет §8:** `role=radiogroup`+aria-label,
  `progressbar` aria-valuemin/max/now, kbd-help `role=dialog aria-modal aria-labelledby/
  describedby` + focus-trap + `?`-toggle + Esc + возврат фокуса + `aria-haspopup=dialog`,
  focus-visible ring на `:has(input:focus-visible)`, reduced-motion fail-safe, kbd-hint
  гейт `hover:hover+pointer:fine+≥641` (нет touch-мёртвых подсказок), моб. sticky submit-bar
  c `env(safe-area-inset-bottom)`. **full-width** структурно (`.wrap width:100%` + focus-grid
  main+aside, брейк 1080px). **states:** покрыты 4 ядровых (active/result/empty/done); НЕ
  покрыты 7 из registry-списка focus-training (flashcard/study-LEARN/generationUnavailable/
  loading/inline-alert/no-js/diagram) → **C12** (coverage-gap, TODO; порт всё равно BLOCKED).
  **Вывод:** reference-язык verified-clean на ядровых состояниях. Дальше worst-first —
  recheck остальных чистых макетов (result/settings/stats) ЛИБО вход в Фазу E по разблокировке.
- **cr.7 (Фаза 1, тик 7)** — **кросс-экранный аудит type-scale → система когерентна,
  1 микро-дрейф закрыт (C10).** Разобрал page-title/hero-трактовку всех 6 макетов.
  **Вывод: шкала НЕ дрейфует — это осознанный 2-tier дизайн:** (a) content-hero
  (`shell.home-headline` clamp 2.1→3.15, `focus.focus-question` fs-hero clamp 1.75→3.05) —
  responsive, крупный, потому что ЭТО фокус экрана; (b) page-title (`settings/stats-title`,
  `summary-headline`, `state-title`) — фикс `--fs-xl` 2rem, спокойный. Различия внутри
  tier-а оправданы/задокументированы (floor focus-hero занижен it.3 под перенос вопроса).
  **Реальный дефект — только 1:** среди serif-page-title @fs-xl три (`settings/stats/summary`)
  держат `letter-spacing: --track-hero`, а `.state-title` (empty/done, тот же serif@2rem) —
  `--track-tight` → рассинхрон. Одна роль = один tracking. **Фикс:** state-title
  `--track-tight`→`--track-hero` (1 файл). **QA:** detector exit 0; все 4 serif-page-title
  теперь `--track-hero`; правка letter-spacing → AA/лейаут не затронуты (0.005em при 32px,
  консистентность важнее заметности). **Type-scale помечен verified-clean** (2-tier
  осознан) → не переаудировать. Следующий тик Фазы 1 — иная дименсия (focus-ring/spacing-
  ритм консистентность) или обратно в Фазу 2 (по готовности дерева/bootRun).
- **cr.6 (Фаза 1, тик 6)** — **углублённый кросс-экранный аудит компонентов → нашёл и закрыл C9 (дрейф кнопок на `shell`).**
  Сравнил определения `.btn*` через все 6 макетов. Три расхождения, все на `shell`:
  (1) базовый `.btn` padding `var(--s-3) var(--s-5)` против `s-6` у остальных 4 →
  привёл к `s-6`; (2) `.btn-primary` без `box-shadow: var(--shadow-sm)`, тогда как
  focus/result/summary/settings — все с ним (главный CTA выглядел плоским на home) →
  добавил `shadow-sm`; (3) `.btn-quiet` на shell был байт-идентичен общему `.btn-ghost`
  (transparent/ink/line-strong), но result/summary используют `.btn-quiet` для ДРУГОЙ,
  безрамочной третичной кнопки → одно имя = два вида (коллизия словаря). shell'овский
  `.btn-quiet` — **мёртвый CSS** (в разметке не используется, только `.btn-primary`),
  поэтому переименовал `.btn-quiet`→`.btn-ghost` (+ hover): коллизия убрана, канонич.
  вторичная кнопка теперь задокументирована и в home-эталоне; 0 изменений разметки →
  0 визуального регресса. **QA:** detector shell exit 0; кросс-скрин — все 5 `.btn-primary`
  теперь с `shadow-sm`, padding `s-5` = 0 вхождений (все `s-3 s-6`), `btn-quiet` в shell
  отсутствует. Правка чисто токен/словарь — цвета не тронуты (signal-strong/signal-on уже
  AA-verified, box-shadow текст-контраст не меняет, padding — тоже). **Кросс-экранная
  консистентность кнопок закрыта** → следующий тик: либо ещё один срез (type-шкалы hero/
  H1 через экраны, token-дрейф), либо старт Фазы 2 (перенос во фронт).
- **cr.5 (Фаза 1, тик 5)** — **C6 решён + углублённый аудит нашёл и закрыл C7.**
  **C6 (подвал):** решение — подвал только на hub-экране `shell` (home); 5 задача-экранов
  намеренно без подвала (фокус + ethos «инструмент не шумит»; кросс-нав уже в шапке каждого
  экрана → навигация не теряется). Это осознанное решение, не дефект; код не трогал.
  **C7 (углублённый скан AA-каркаса):** нашёл, что 5 из 6 skip-link'ов держат
  `background: var(--signal); color: var(--signal-on)` — тот же провальный combo (AA 4.40 < 4.5
  в light), что раньше чинили в `btn-primary`, но в focus-only skip-link пропустили. skip-link
  показывается по Tab (WCAG 2.4.1) → обязан держать AA. **Фикс:** 5 файлов `--signal` →
  `--signal-strong` (как shell/btn-primary). **QA:** detector 5/5 clean; замер skip-link AA
  (stats) — light **6.33** / dark **10.66** (было 4.40 light) ✓; правка позиционно-цветовая,
  остальной каркас без изменений. **Бэклог C1–C7 исчерпан** → Фаза 1 (первичная критика)
  в основном закрыта; следующий тик — либо ещё глубже (компонентная консистентность btn/
  hero-шкал/токенов через экраны), либо старт Фазы 2 (перенос во фронт).
- **cr.4 (Фаза 1, тик 4)** — **C5: входной ритм оценён; исправлены 2 кикера-эхо.**
  Разбор входа всех 6: `focus-question` («spring·транзакции»), `session-summary`
  («итоги сессии·spring·режим»), `shell` («тренажёр собеседований·java») — кикеры
  ФУНКЦИОНАЛЬНЫ (контекст/мета, отличаются от заголовка). А `stats` («аналитика» = H1
  «Аналитика») и `settings` («настройки» = H1 «Настройки») — lowercase-ЭХО заголовка,
  декоративный эйбрау, ровно то, что DESIGN.md запрещает («mono-кикер = функциональный
  тег, не эйбрау»). **Вывод: сам ритм kicker+headline — НЕ дефект** (системный, VARIANCE 6
  ок), дефект — только 2 эхо. Фикс (текст-онли, в том же `·`-идиоме): stats → «прогресс ·
  точность · повторы» (что трекает дашборд), settings → «сессия · оформление · данные»
  (три вкладки = скоуп). Теперь отличаются от H1 и несут навигацию. **QA:** detector оба
  clean; AA без изменений (тот же `--ink-3`/фон); 375 — overflow 0, кикер в пределах gutter
  (right 355<375). Дальше — C6 (подвал: решение).
- **cr.3 (Фаза 1, тик 3)** — **C4: kbd-help модалка перенесена в `focus-question.html`.**
  По контракту §8 глобальный оверлей горячих клавиш принадлежит экрану вопроса (там
  работают хоткеи 1-9), а жил только в `shell`. Добавил в эталон: `.icon-btn`-триггер в
  шапке (`aria-haspopup=dialog`), модалку `role=dialog aria-modal` (focus-trap, `?` toggle,
  Esc close, возврат фокуса на триггер, 9 строк вкл. навигацию ↑↓ по вариантам),
  scrim `color-mix(ink 42%)`, `kh-pop` только в no-preference. Статичную карту `.aside-hints`
  (3 клавиши, десктоп-рельс) ОСТАВИЛ намеренно: glanceable-подсказка на десктопе vs полный
  список по `?` (в т.ч. на мобиле) — осознанное слоение, не дубль. **QA:** detector exit 0;
  AA модалки+иконки обе темы (light min 7.77 / dark 9.23); функционал — `?` открыл/закрыл
  (toggle), фокус в окне, Esc + возврат фокуса, role=dialog/aria-modal/labelled, 9 строк;
  375 (тайтовый мобайл: brand+kbd+☰+тема) — overflow 0 при открытой и закрытой модалке,
  шапка edge-to-edge, оверлей кроет вьюпорт, модалка в границах. **Хук §8:** `.kbd-help-overlay/
  -modal/-close` role=dialog + `?`/Esc + focus-trap; при проводке 1-9 гасить пока оверлей
  открыт (отмечено в JS-комменте). Дальше — C5 (ритм) / C6 (подвал).
- **cr.2 (Фаза 1, тик 2)** — **C3: унифицирована sticky-шапка на всех 6 экранах.**
  5 статичных `.site-head` привёл к канону `shell` (`position: sticky; top: 0; z-index: 40`
  на сплошном `--paper`, без блюра) — теперь хром = один компонент, нав всегда доступна.
  Побочный эффект sticky-шапки: залипающие рельсы (`@media ≥1080`, `top: var(--s-5)` = 24px)
  тукались бы ПОД 73px-шапку на длинном скролле → поправил offset всех 5 рельсов
  (`.focus/result/summary-aside`, `.settings-rail`, `.stats-aside`) на
  `top: calc(var(--s-8) + var(--s-5))` = 80px (≈7px зазор под шапкой). Табличный
  sticky-thead (`session-summary` `.topic-table thead`) — внутри `overflow`-контейнера,
  container-relative, шапка не мешает, не трогал. **QA:** detector — все 6 clean; AA/ширина
  без изменений (правка чисто позиционная); проверка скроллом (stats, reload): шапка
  pinned top:0, рельс залипает на 80px, clears header ✓. Дальше — C4/C5/C6.
- **cr.1 (Фаза 1, тик 1)** — первичный разбор всех 6 макетов. Нашёл 2 явных слоп-дефекта
  и применил: **C1** `🔥`→Lucide flame SVG (`.streak-flame` теперь `currentColor` +
  `--spark-ink`, монохромно как весь набор); **C2** шапка `shell` — убрал
  `backdrop-filter: blur` и `color-mix`-полупрозрачность, оставил `position: sticky`
  (полезное UX) на сплошном `--paper`. Остальные находки (C3–C6) — в бэклоге worst-first
  для следующих тиков; C3 (унификация sticky-шапки на всех 6) — крупная согласованная
  правка, отдельный тик. QA после правок: detector + AA re-check `shell`.
