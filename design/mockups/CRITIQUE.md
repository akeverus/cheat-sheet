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
