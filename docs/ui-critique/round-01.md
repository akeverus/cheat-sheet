# Критика UI/UX и вёрстки — раунд 1 (2026-06-11)

Роль: экзаменатор. Задача — завалить. Метод — только проверяемые факты: каждое замечание
имеет файл, строки и/или живой замер (Playwright `evaluate`, WCAG-расчёт по формуле
относительной яркости). Статический разбор выполнен тремя независимыми критиками
(CSS / шаблоны / JS), **каждое** замечание прошло adversarial-верификацию вторым агентом
(опровергнутые — выброшены). Live-замеры: editorial, light+dark, 1728×1080.

**Вердикт: НЕ СДАЛ.** WCAG AA — заявленный жёсткий инвариант проекта — нарушен в обеих
темах. Дефолтный режим обучения лжёт пользователю кнопкой «Начать сессию». В app.js
обнаружен пласт мёртвого функционала, который при этом *активно вредит* (ловушка
localStorage). Ниже — 67 замечаний: 3 блокера, 7 major, 39 minor, 18 nit.

Что **проверено и чисто** (чтобы не пугаться зря): дубликатов id нет, битых
aria-ссылок нет, безымянных интерактивов нет, tabindex>0 нет, горизонтального
оверфлоу нет ни на одной странице, консоль чистая, фокус после ответа корректно
уходит на вердикт, чекбоксы настроек обёрнуты в label 44px.

---

## A. Блокеры — нарушения WCAG AA (live-замер, обе величины — сплошные цвета)

### A1. Активная вкладка навигации не читается: 2.96:1 при норме 4.5:1
- **Где:** `editorial.css:606` — `.ed-nav-link.is-active { color: var(--color-accent-primary); }`;
  токен `--color-accent-primary: #D97757` (`editorial.css:57`).
- **Замер:** rgb(217,119,87) на rgb(250,249,245), **12px** → **2.96:1**, норма 4.5:1. Light-тема,
  воспроизведено на всех 5 страницах (/, /settings, /stats, /session-summary, пост-ответ).
- **Почему завал:** это текущая позиция пользователя в навигации — самый
  семантически нагруженный текст шапки, и он самый нечитаемый. Clay задуман
  как display-акцент крупных форм, а применён к 12px тексту.
- **Фикс:** активной вкладке — цвет текста `--color-text-primary`, Clay оставить
  только в `border-bottom` (маркер и так есть на той же строке 606). Identity
  сохраняется, контраст становится 16:1.

### A2. Hero-цифра «к повтору» на /settings: 2.96:1 при норме 3:1 для крупного текста
- **Где:** `editorial.css:2241-2245` — `.today-hero-due { color: var(--color-accent-primary); }`, 64px.
- **Замер:** та же пара #D97757/#FAF9F5 → **2.96:1**, норма для large text 3.0:1. Недобор 0.04 —
  но норма есть норма: это главная цифра экрана настроек.
- **Фикс:** ввести `--color-accent-strong: #C96442` (замер: **3.70:1** на ivory) для
  light-темы, в dark оставить #D97757 (там 4.8:1 — проходит, см. комментарий `editorial.css:205`).

### A3. Dark-тема: «плохая точность» в таблице /stats — 4.41:1 при норме 4.5:1
- **Где:** `editorial.css:1430` — `.accuracy-low { color: var(--color-status-error); }`;
  dark-токен `#E5736B` (`editorial.css:216,254`).
- **Замер:** rgb(229,115,107) на строке таблицы rgb(48,48,46), 14px semibold → **4.41:1**.
- **Почему завал:** значение «40,0%» — именно тот сигнал, ради которого колонка
  существует (слабая тема, иди учи) — читается хуже остальных.
- **Фикс:** поднять dark-токен `--color-status-error` до `#EC8580` (замер: **5.18:1**
  на строке, 5.94:1 на основном фоне). Light-токен не трогать.

---

## B. Major — функциональные и системные дефекты

### B1. Кнопка «Начать сессию» в дефолтном режиме не начинает сессию
- **Где:** `settings.html` — секция «Сессия» с полем «Кол-во» и кнопкой «Начать сессию»;
  `SessionFlowService.java:38-40` — `if (selected == InterviewMode.TRAINING) return new StartFlowResult(true, null);`.
- **Live-факт:** выставил count=1 (и count=3), режим «Тренировка» (дефолт), нажал
  «Начать сессию» → редирект на /, `interviewSession == null`, прогресс-строка пустая,
  `aria-valuemax="0"`. Поле count молча проигнорировано, никакой сессии нет.
- **Почему завал:** UI обещает контракт («сессия», «кол-во вопросов»), сервер для
  дефолтного режима его сознательно не выполняет — и ни UI, ни редирект об этом
  не сообщают. Пользователь, выставивший 10 вопросов, получает бесконечную ленту
  без прогресса и без итогов.
- **Фикс (минимальный честный):** при mode=TRAINING прятать поле «Кол-во» и
  переименовывать CTA в «Начать тренировку» (th:условие по селекту + 5 строк JS на change);
  либо заводить настоящую TRAINING-сессию с count.

### B2. Экзамен молча подбрасывает штрафные вопросы: «1/1» превращается в «2/6»
- **Где:** `application.yml:89` — `exam-penalty-questions: 5`; UI-уведомления нет нигде.
- **Live-факт:** EXAM, count=1 → прогресс «1/1». Ответил неверно → «Следующий вопрос» →
  прогресс **«2/6»**. Сессия выросла на 5 вопросов без единого слова в интерфейсе.
- **Почему завал:** механика правильная (наказание ошибок), но контракт сессии
  меняется за спиной пользователя. Доверие к прогресс-бару обнулено: он только что
  показывал 1/1.
- **Фикс:** в `showResult` (app.js) при неверном ответе в EXAM показывать в вердикте
  строку «+5 штрафных вопросов за ошибку» (данные о росте total уже приходят в
  `data.session` — app.js:830-835).

### B3. Завершить сессию с фокус-страницы невозможно
- **Где:** кнопка «Завершить…» есть только в `settings.html:105-106` и в no-JS
  фоллбэке `result.html:18-19`. На фокус-странице (`focus-training.html`) — нигде.
- **Live-факт:** находясь в EXAM-сессии 2/6, единственный UI-путь к итогам —
  уйти в Настройки и найти там «Завершить текущую сессию».
- **Почему завал:** страница, на которой пользователь проводит 100% сессии,
  не имеет выхода из сессии. IA-провал: действие живёт не там, где нужно.
- **Фикс:** в сессионную рейку (`focus-session-rail`) добавить форму POST /finish
  с secondary-кнопкой «Завершить сессию» при `interviewSession != null`.


### B4. Чип статуса варианта стилизован ТОЛЬКО под 
- **Где:** `modules/quiz-app/src/main/resources/static/css/editorial.css:1850-1857, 813-814`
- **Цитата:** `html[data-design] .result-page .option-status-label { display: inline-block; ... font-family: var(--font-family-mono); ... }`
- **Факт:** Чип статуса варианта стилизован ТОЛЬКО под .result-page (L1850-1857: mono, uppercase, рамка), но app.js (static/js/app.js:1482-1484) инжектит .option-status-label/.option-status-correct/.option-status-selected/.option-status-muted и на JS-флоу focus-страницы, где для него есть лишь layout-правило grid-column (L813-814). Класс .option-status-muted не имеет НИ ОДНОГО правила ни в одном css-файле (grep по static/css/*.css — 0 совпадений). Это ровно та болезнь, которую файл сам описал и чинил для .related-question-item (комментарий L1901-1906: «Раньше правила были только под .result-page…»).
- **Почему завал:** Один и тот же компонент выглядит по-разному на /answer (бордерный mono-чип) и на живой focus-странице (голый текст без чипа) — рассинхрон визуального языка между no-JS и JS путями одного флоу.
- **Фикс:** Продублировать селекторы .option-status-label/-correct/-selected под .focus-page (как сделано для .related-question-item на L1907-1929) и добавить правило для .option-status-muted (например, color: var(--color-text-tertiary)).
- *Верификатор:* Подтверждено полностью. app.js:1481-1487 инжектит span .option-status-label с .option-status-correct/-selected/-muted в пост-ответном JS-флоу focus-страницы; в editorial.css «option-status» встречается 4 раза — все стили чипа только под .result-page (L1850-1857), на focus-page лишь layout-правило grid-column (L813-814). .option-status-muted не имеет ни одного правила ни в одном css-файле (только app.js:1484). Прецедент-аналогия с .related-question-item (комментарий L1901-1906) тоже точна.

### B5. Editorial-сигнатура радиусов (L2574-2585) покрывает кнопки выборочно
- **Где:** `modules/quiz-app/src/main/resources/static/css/editorial.css:2574-2585, 1170-1179, 1350-1352, 2128-2135, 1777-1783, 1158-1167`
- **Цитата:** `html[data-design="editorial"] .next-btn, ... .focus-page #interview-submit, ... .flashcard-reveal-btn { border-radius: 0 0 8px 8px; }`
- **Факт:** Editorial-сигнатура радиусов (L2574-2585) покрывает кнопки выборочно. Accent-залитые primary-CTA получают ТРИ разных радиуса: .next-btn/#interview-submit/.flashcard-reveal-btn — асимметричный 0 0 8px 8px (L2583-2585); #session-form button[type=submit] («Начать сессию», accent-fill L1174) — radius-md 8px со всех углов (L1175), т.к. у кнопки нет класса (settings.html:103 — <button type="submit"> без class); .stats-search-action («Искать», accent-fill L1350-1352) — плоский 0 через .stats-action-btn в flat-списке (L2578). Secondary-кнопки тоже расходятся: .btn-finish (result.html:19, без .btn), .btn-finish-small (settings.html:106), #filters-form submit (settings.html:74) и .confidence-btn (app.js:1541, без .btn) сохраняют radius-md 8px, тогда как их близнецы .secondary-btn/.flashcard-grade-btn/.table-expander сплющены в 0 (L2574-2580). При этом комментарий L2118 заявляет «Стилизуем единообразно с .flashcard-grade-*» для confidence-кнопок — в editorial у них радиусы 8px против 0.
- **Почему завал:** Спека дизайна (L2573: «no uniform rounded buttons (0px is deliberate)») нарушается внутри самого дефолтного дизайна: однотипные кнопки на соседних страницах имеют 0 / 8 / 0-0-8-8 — система радиусов разваливается там, где она и есть «сигнатура».
- **Фикс:** Дополнить flat-список (L2574-2580): .confidence-btn, .btn-finish, .btn-finish-small, .settings-page #filters-form button[type=submit]; асимметричный CTA-список (L2583-2585): .settings-page #session-form button[type=submit], .stats-search-action (и убрать его из эффекта flat-правила).
- *Верификатор:* Ядро подтверждено, но две детали неверны из-за специфичности: .stats-search-action НЕ плоская 0 — page-правило html[data-design] .stats-page .stats-action-btn (L1339-1343, специфичность 0,3,1) с border-radius-md ПОБЕЖДАЕТ editorial-сигнатуру (0,2,1, L2578); то же с .table-expander (L1457 бьёт L2579) и .danger-btn (L1207 бьёт L2576) — записи flat-списка частично мертвы. Итого accent-CTA имеют ДВА радиуса (0 0 8px 8px у submit/reveal vs 8px у «Начать сессию»/«Искать»), не три. Остальное верно: settings.html:103 submit без класса (8px, L1175), #filters-form submit без класса (8px, L1164), .confidence-btn 8px (L2132) против .flashcard-grade-btn 0 при комментарии L2118 «единообразно», .btn-finish/.btn-finish-small 8px. Несогласованность сигнатуры реальна и даже глубже заявленной.

### B6. Document-level keydown в initFlashcardShortcuts перехватывает Enter/Space с ЛЮБОГО фокусир
- **Где:** `modules/quiz-app/src/main/resources/static/js/app.js:2078-2090`
- **Цитата:** `if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName)) { return; } ... if (revealBtn && (event.key === ' ' || event.key === 'Enter')) { event.preventDefault(); revealBtn.click();`
- **Факт:** Document-level keydown в initFlashcardShortcuts перехватывает Enter/Space с ЛЮБОГО фокусируемого элемента: исключены только INPUT/TEXTAREA/SELECT, в отличие от MCQ-обработчика того же файла (строки 1244-1247), который явно исключает SUMMARY/BUTTON/A. На флешкард-странице есть нативный <summary class="btn ... question-code-summary">Пример кода</summary> (focus-training.html:45) и nav-ссылки из header-фрагмента (focus-training.html:6): Enter/Space на них вызывает preventDefault() и revealBtn.click() вместо нативного действия.
- **Почему завал:** Клавиатурный пользователь на флешкарте (pre-reveal и browse-режим) НЕ может открыть «Пример кода» или перейти по ссылке шапки через Enter — вместо этого раскрывается ответ флешкарты (или отправляется POST /flashcard-reveal, двигающий SM-2 фазу). Прямое противоречие с собственным комментарием в MCQ-ветке: «Элементы со своей семантикой Enter/Space … не перехватываем».
- **Фикс:** Добавить в начало обработчика тот же гард, что в MCQ-ветке (1244-1247): if ((event.key === 'Enter' || event.key === ' ') && ['SUMMARY','BUTTON','A'].includes(event.target.tagName)) return; — либо срабатывать только когда event.target === document.body.
- *Верификатор:* Факт подтверждён: app.js:2082-2088 исключает только INPUT/TEXTAREA/SELECT, в отличие от MCQ-обработчика (1244-1247, исключает SUMMARY/BUTTON/A); на флешкард-странице есть <summary>Пример кода</summary> (focus-training.html:45) и nav-ссылки header (fragments/header.html:25-31). В browse-режиме revealBtn — это summary #browse-reveal-btn (всегда в DOM), до reveal в сессии — submit-кнопка: Enter/Space на ссылках/кнопках/summary действительно перехватываются. Но после reveal в сессии revealBtn=null и ссылки работают; core-флоу (reveal Space/Enter, оценка 1-4, Tab+Enter на grade-кнопках) не сломан, мышь/тач не затронуты — это серьёзный a11y-дефект (WCAG 2.1.1), но не blocker.

### B7. Все пять контролов режимов обучения не существуют в шаблонах
- **Где:** `modules/quiz-app/src/main/resources/static/js/app.js:1057-1061, 1202-1204, 1789-1796`
- **Цитата:** `const instantModeToggle = document.getElementById('instant-mode-toggle'); ... [instantModeToggle, hardModeToggle, reviewModeToggle, adaptiveModeToggle, timerSelect].filter(Boolean).forEach((el) => el.addEventListener('ch`
- **Факт:** Все пять контролов режимов обучения не существуют в шаблонах: id «instant-mode-toggle», «hard-mode-toggle», «review-mode-toggle», «adaptive-mode-toggle», «timer-select» — 0 вхождений в templates/*.html (проверено grep). Все пять const = null, навешивание change-слушателей (1202-1204) отфильтровывается через .filter(Boolean) в пустой массив — onLearningPrefChange недостижим. При этом loadLearningPrefs() (1198) продолжает читать localStorage «quiz.learning.prefs.v2» и ПРИМЕНЯТЬ его: hardMode=true из старого стораджа прячет confidence-кнопки (1789: «if (!learningPrefs.hardMode)») и кнопку доп. анализа (1796), timerSeconds>0 запускает таймер — а UI для отключения не существует.
- **Почему завал:** Пользователь со старым значением hardMode/timerSeconds в localStorage навсегда теряет confidence-кнопки, доп. анализ и получает неотключаемый таймер на каждый вопрос — ловушка без выхода из UI. Плюс сотни строк недостижимых веток (instantMode-сабмит 1219-1221, startQuestionTimer 1159-1184, hardMode-ветки showResult 1844-1861).
- **Фикс:** Либо вернуть контролы режимов в шаблон, либо удалить применение prefs: перестать читать quiz.learning.prefs.v2 (или мигрировать ключ на v3 с дефолтами), выпилить hardMode/instantMode/timer-ветки и таймер.
- *Верификатор:* Полностью подтверждено: все 5 id отсутствуют в templates (grep по modules = 0), const = null, onLearningPrefChange недостижим (1202-1204 filter(Boolean) → []). При этом loadLearningPrefs (1198) применяет localStorage quiz.learning.prefs.v2: hardMode прячет confidence (1789) и доп.анализ (1796/1859), а #question-timer СУЩЕСТВУЕТ в training-actions.html:9 → таймер реально запустится. Toggles раньше жили в удалённом index.html (коммит 272998ca «удалить мёртвый шаблон»), так что stale-сторадж у старых пользователей реалистичен. Major корректен.

---

## C. Minor — шероховатости, портящие систему (подтверждены верификатором)

Мои live-добавки к списку критиков:

- **C-live-1.** Пустой прогрессбар рендерится вне сессии: `focus-training.html:21-27` —
  `.session-progress-track` не гейтится `th:if`, при `interviewSession == null` в DOM
  висит видимая полоска 254×2px с `aria-valuemax="0"` (live-замер). Скринридер получает
  progressbar «0 из 0». Фикс: `th:if="${interviewSession != null}"` на трек и строку.
- **C-live-2.** Кнопка «Следующий вопрос» врёт на последнем вопросе сессии:
  `app.js:1778` ставит текст безусловно, хотя `data.session.index/total` уже в руках
  (app.js:830-835). На 1/1 после ответа кнопка должна говорить «Итоги сессии».
  Live-факт: EXAM 1/1 → после ответа «Следующий вопрос».
- **C-live-3.** Чип режима на фокус-странице говорит «Focus mode» (англицизм в
  русском UI) и не меняется по реальному режиму: в EXAM-сессии чип тот же
  (live-замер; `focusModeChipText`).

Список критиков:


**C1. `modules/quiz-app/src/main/resources/static/css/editorial.css:2201-2209, 2184`** — В @media (hover: none) кнопке копирования кода подняли только min-height до 44px, min-width нет. На экранах <=600px текст-лейбл скрыт (L2207-2209: .code-copy-label { display: none; }), остаётся иконка 0.875rem (14px, L2199) + горизонтальный padding var(--space-2) (8px×2, L2184) + рамка = ширина ~32px. На телефонах (touch + узкий экран — основной кейс пересечения этих двух media) тап-цель 32×44.
  - Почему: Комментарий в коде сам ссылается на WCAG 2.5.5 (44×44), но выполняет требование только по одной оси — внутреннее противоречие; промахнуться пальцем по 32px-цели над скроллируемым кодом легко.
  - Фикс: В блок @media (hover: none) добавить min-width: 44px; justify-content: center (иконка отцентруется в расширенной цели).

**C2. `modules/quiz-app/src/main/resources/static/css/editorial.css:2389-2397, 2334-2337`** — Принцип WIDE LAYOUT задекларирован в шапке секции (L2335-2337): «Условные секции гейтятся :has()… браузер без :has() остаётся на полноширинном стеке (progressive enhancement)». Но грид result-страницы включён БЕЗ гейта (L2389), карточка прибита к колонке 1 (L2396), а возврат на полную ширину при отсутствии related завязан на :not(:has(...)) (L2397). #result-related-questions рендерится условно (result.html:134, th:if="${relatedQuestions != null and !relatedQuestions.isEmpty()}"). В браузере без :has() правило L2397 отбрасывается → на странице без related карточка зажата в 1-ю колонку, справа пустой жёлоб 16-22rem + column-gap.
  - Почему: Код противоречит собственному задекларированному инварианту PE: вместо «полноширинного стека» no-:has-браузер получает сломанную двухколонку с мёртвой зоной справа.
  - Фикс: Гейтить весь грид: html[data-design] .result-page .ed-page:has(> #result-related-questions) { display: grid; … } — тогда L2397 не нужен вовсе, а без :has()-поддержки страница остаётся стеком, как обещает комментарий.

**C3. `modules/quiz-app/src/main/resources/static/css/editorial.css:1452-1459, 1185-1196, 1561-1579, 870-888`** — Page-скоупные рецепты кнопок дословно дублируют глобальный .btn (L870-888) для элементов, которые УЖЕ носят классы btn/secondary-btn: .table-expander (stats.html:159 — class="btn secondary-btn table-expander hidden"; правило L1452-1458 повторяет 12 деклараций глобального .btn, новое в нём только margin-top), .data-export-actions .btn (settings.html — class="btn secondary-btn"; L1185-1193 повторяет 14 деклараций), .summary-actions .btn (session-summary.html:37-39 — class="btn next-btn"; L1561-1569). Ховеры тоже скопированы: L1459 и L1194 == L886-888 (background: var(--color-bg-tertiary); border-color: var(--color-text-secondary)). Эти дубли писались как оборона от styles.css, которого больше нет (в static/css/ только editorial+3 оверлея).
  - Почему: ~40 дублирующих деклараций: любое изменение базовой кнопки (например, min-height) придётся повторять в 3-5 местах, иначе кнопки разъедутся — что уже и произошло с радиусами (см. отдельный finding).
  - Фикс: Сократить page-правила до дельт: .table-expander { margin-top: var(--space-4); }, .data-export-actions .btn { flex: 0 0 auto; }, .summary-actions .btn { min-height: 48px; padding: var(--space-3) var(--space-6); } — остальное даёт глобальный .btn.

**C4. `modules/quiz-app/src/main/resources/static/css/editorial.css:188-227, 229-264`** — Полный блок тёмных токенов (~30 деклараций: цвета, статусы, overlay, shadow-focus) продублирован дословно дважды: явный [data-theme=dark] (L188-227) и системный prefers-color-scheme (L229-264). Никакого механизма синхронизации, второй блок даже не имеет комментария-ссылки на первый.
  - Почему: Классическая ловушка поддержки: правка цвета в одном блоке и забытый второй → «авто»-тема и явная тёмная тема тихо разъезжаются.
  - Фикс: Минимум — комментарий-страж «ЗЕРКАЛО L188-227, менять синхронно» у второго блока; правильно — перейти на light-dark() в одном :root (color-scheme уже управляется) либо генерировать оба блока из одного источника.

**C5. `modules/quiz-app/src/main/resources/static/css/editorial.css:109-112, 125-127, 167-181, 2078`** — 13 токенов не используются ни в одном из 4 css-файлов, ни в JS/шаблонах (grep — 0 совпадений): --measure-narrow (L110, сам помечен «не используется»), --drop-cap-size (111), --column-gap (112), --space-10/11/12 (125-127), --duration-instant (167), --duration-slower (171), --easing-in (173), --easing-bounce (175), --z-base (177), --z-sticky (178), --z-modal (181). При этом единственная модалка приложения (.kbd-help-overlay, role=dialog) сидит на --z-overlay: 40 (L2078), а семантически предназначенный ей --z-modal: 100 мёртв.
  - Почему: Мёртвые токены раздувают «систему» и врут о её охвате; модалка на overlay-слое — готовая мина: любой будущий элемент на --z-overlay/-z-modal встанет в непредсказуемом порядке относительно диалога.
  - Фикс: Удалить неиспользуемые токены (или пометить reserved с причиной); .kbd-help-overlay перевести на var(--z-modal).

**C6. `modules/quiz-app/src/main/resources/static/css/editorial.css:1131-1140, 1326-1335`** — Рецепт кастомной select-стрелки (appearance:none + два linear-gradient + позиции calc(100% - 18px)/calc(100% - 12px) + size 6px 6px + padding-right) и focus-правило (outline:none; border-color focus; box-shadow shadow-focus) продублированы дословно дважды: .settings-page select (L1131-1140) и .stats-page select (L1326-1335).
  - Почему: Поправка геометрии стрелки или focus-стиля в одном месте незаметно разведёт идентичные контролы двух страниц.
  - Фикс: Вынести в общий селектор: html[data-design] .settings-page select, html[data-design] .stats-page select { … } одним блоком (или общий класс .ed-select).

**C7. `modules/quiz-app/src/main/resources/static/css/editorial.css:1232, 1356, 1374-1379, 1361-1366`** — Один и тот же компонент .stats-grid имеет три разных рецепта треков: settings — с страховкой min(120px,100%) (L1232, комментарий L1228-1231 объясняет её необходимостью при A+ масштабе), stats — без страховки minmax(120px,1fr) (L1356), планшетные полосы — голый repeat(3, 1fr) (L1375, 1378). При этом соседний комментарий в этом же блоке (L1362-1364) сам предупреждает: «1fr-трек не ужимался ниже min-content плиток и распирал сетку».
  - Почему: Защита от переполнения при крупном шрифте применена к одной странице и пропущена на другой и в планшетном диапазоне — код игнорирует собственное задокументированное правило.
  - Фикс: Унифицировать: minmax(min(120px, 100%), 1fr) для обоих auto-fit, repeat(3, minmax(0, 1fr)) для планшетных полос.

**C8. `modules/quiz-app/src/main/resources/static/css/editorial.css:851-853, 1170-1172, 1561-1564, 978-979`** — Главная кнопка приложения «Ответить» (#interview-submit) — min-height 44px, padding space-2 (L853), тогда как однотипные primary-CTA крупнее: #session-form submit — 48px, padding space-3 (L1172), .summary-actions .btn — 48px, padding space-3 (L1564), и даже вторичные кнопки самооценки .flashcard-grade-btn — 48px (L979) и .confidence-btn — 48px (L2129).
  - Почему: Самое частое действие пользователя визуально мельче, чем второстепенные кнопки, стоящие на той же странице сразу под ним — перевёрнутая иерархия размеров.
  - Фикс: Поднять #interview-submit/.flashcard-reveal-btn до min-height: 48px; padding: var(--space-3) var(--space-6) — в один ряд с остальными primary.

**C9. `modules/quiz-app/src/main/resources/static/css/editorial.css:1591, 1446-1450, 2024-2028`** — Два противоположных паттерна горизонтального скролла однотипных data-таблиц: .topic-table обёрнута в div.topic-table-wrap с overflow-x:auto, сама остаётся display:table (L1446-1450); .summary-table (L1591) и .comparison-table (L2026) получают display:block прямо на <table>. display:block на элементе table убирает имплицитную таблично-сеточную семантику из accessibility-дерева (Chrome/Safari), а role="table" в разметке нет (session-summary.html:47 — <table class="summary-table"> без role).
  - Почему: Скринридер теряет навигацию по ячейкам/заголовкам в 5-колоночной таблице итогов; плюс две разные техники для одной задачи в одном файле сбивают конвенцию.
  - Фикс: Либо обернуть summary/comparison-таблицы в скролл-обёртку по образцу .topic-table-wrap, либо добавить role="table"/"row"/"cell"-атрибуты в разметку при сохранении display:block.

**C10. `modules/quiz-app/src/main/resources/static/css/editorial.css:1095, 2441-2446`** — Война !important против !important: L1095 ставит display: block !important, L2442 в @media (min-width:1200px) перебивает display: grid !important. Селекторы идентичны по специфичности — победитель определяется только порядком в исходнике, что прямо признаёт комментарий L2439-2440 («display:grid c !important перебивает … каскад»). Любая перестановка секций файла или вынос WIDE LAYOUT в отдельный файл молча сломает трёхколоночную панель настроек.
  - Почему: Каскад, держащийся на взаимном расположении двух !important-правил в 1350 строках друг от друга — самый хрупкий вид зависимости; ошибка проявится только на >=1200px и только на /settings.
  - Фикс: Убрать !important из обоих: базовое правило L1095 нужно лишь чтобы перебить инлайновое/JS-скрытие — если это hidden-атрибут или класс, заменить на селектор с достаточной специфичностью без !important, тогда и wide-правилу важность не нужна.

**C11. `modules/quiz-app/src/main/resources/static/js/app.js:185-270, 993-995`** — initHintButton полностью мёртв: id «btn-hint» и «hint-container» не существуют ни в одном файле templates/ (grep по *.html = 0 вхождений), guard «if (!btn || !container) return;» срабатывает всегда. Следствие: window.__resetHintState никогда не определяется, и его вызов в regenerateQuestion (993-995: «if (typeof window.__resetHintState === 'function')») тоже мёртв. Это ~90 строк нерабочего кода, включая POST /api/hint.
  - Почему: Мёртвая фича вводит в заблуждение при поддержке (выглядит как рабочий функционал с уровнями подсказок и retry-логикой), раздувает файл и тянет за собой эмодзи-иконки и эндпоинт. Любая правка этого кода — потраченное впустую время.
  - Фикс: Либо удалить initHintButton, вызов из DOMContentLoaded (строка 733) и ветку __resetHintState в regenerateQuestion, либо вернуть кнопку #btn-hint и контейнер #hint-container в шаблон focus-training.

**C12. `modules/quiz-app/src/main/resources/static/js/app.js:1525-1545`** — Live-region вставляется в DOM УЖЕ с контентом: confidenceDiv создаётся, получает aria-live=polite (1538), наполняется через innerHTML (1539-1544) и только потом вставляется feedbackDiv.after(confidenceDiv) (1545). Скринридеры озвучивают ИЗМЕНЕНИЯ внутри существующего live-региона; вставка самого региона вместе с готовым контентом в NVDA/JAWS/VoiceOver стабильно не озвучивается. Комментарий в коде (1534-1537) утверждает обратное: «сама себя озвучивает при появлении».
  - Почему: Заявленная цель — чтобы SR-пользователь узнал о появлении вопроса «Насколько ты уверен?» — не достигается: регион и контент появляются атомарно, объявления не будет. Незрячий пользователь не узнает о confidence-кнопках без ручного таб-обхода — ровно то, от чего комментарий якобы защищает.
  - Фикс: Держать пустой контейнер с aria-live=polite в шаблоне post-answer-controls (как сделано с #extra-analysis-content) и наполнять его после ответа; либо вставить пустой confidenceDiv в DOM и заполнить innerHTML на следующий кадр (requestAnimationFrame/setTimeout 0).

**C13. `modules/quiz-app/src/main/resources/static/js/app.js:1957-2027`** — initKeyboardHelp продублирован почти дословно в stats.js:243-306 (~70 строк: тот же оверлей, focus-trap, open/close, '?'/Escape). Поведение уже разъехалось: в app.js:2018 ранний выход для полей ввода стоит ДО обработки Escape («if (event.target && ['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName)) return;»), а в stats.js:289-293 Escape обрабатывается ПЕРВЫМ, до tag-check («if (event.key === 'Escape' && !overlay.classList.contains('hidden')) { … close(); return; } if (tag === 'INPUT' …) return;»).
  - Почему: Копипаста уровня целого компонента уже дала поведенческий дрейф (порядок Escape/tag-check); следующий фикс в одном файле гарантированно не попадёт во второй. Это классический источник «починили на /stats, сломано на тренировке».
  - Фикс: Вынести модал справки в общий kbd-help.js (параметр — массив shortcuts), подключить на обеих страницах; порядок взять из stats.js (Escape до tag-check).

**C14. `modules/quiz-app/src/main/resources/static/js/app.js:1049-1061, 1906-1911`** — Мёртвые привязки, подтверждённые grep-ом по templates/ и static/: (1) window.submitAnswer (1906-1911) не вызывается нигде — ни одного вхождения «submitAnswer» в шаблонах/JS вне определения; (2) фолбэк «|| document.getElementById('submitBtn')» (1051) — id «submitBtn» отсутствует во всех шаблонах (реальный id всегда interview-submit, передаётся в training-actions); (3) const answerFlowSteps (1056) — id «answer-flow-steps» отсутствует в templates/ → setAnswerFlowStep (1074-1090) и все 5+ его вызовов (1388, 1397, 1781, 1805, 1862) — гарантированные no-op.
  - Почему: Каждый из трёх артефактов выглядит как рабочий механизм (глобальный API, легаси-фолбэк, степпер прогресса ответа) и заставляет читателя искать несуществующих потребителей. setAnswerFlowStep — целый визуальный фичефлоу, который молча никогда не рендерится.
  - Фикс: Удалить window.submitAnswer, фолбэк '|| getElementById("submitBtn")' и setAnswerFlowStep со всеми вызовами; либо добавить разметку #answer-flow-steps в result-zone-head, если степпер ещё нужен.

**C15. `modules/quiz-app/src/main/resources/static/js/app.js:131-149, 737-744`** — trackUxMetric — write-only телеметрия: пишет в sessionStorage (METRICS_STORAGE_KEY='quiz.ux.metrics.v2') и диспатчит CustomEvent «quiz:ux-metric», но во всём проекте (static/ + templates/) нет ни одного читателя ключа и ни одного addEventListener на это событие (grep = 0). Вдобавок селектор document.querySelector('.question-support') (737) не находит ничего — класса «question-support» нет ни в одном шаблоне, метрика support_opened мёртвая дважды.
  - Почему: 9 точек вызова trackUxMetric создают видимость работающей аналитики, а данные никто никогда не читает — мёртвый вес в горячем пути ответа (JSON.parse/stringify sessionStorage на каждый сабмит).
  - Фикс: Удалить trackUxMetric и все вызовы (вместе с блоком .question-support), либо добавить реального потребителя (отправку метрик на бэкенд/чтение на /stats).

**C16. `modules/quiz-app/src/main/resources/static/js/app.js:433-441, 467-479`** — Рендер comparison-таблицы и code-trace продублирован внутри app.js дословно: result-страница строит таблицу в initResultPageExtraAnalysis (436-440: «table += '<table class="comparison-table"><tr><th>Критерий</th>…») и трейс (469-479), а index-путь строит те же строки второй раз в fetchComparison (1693-1697) и fetchCodeTrace (1724-1734) — вплоть до одинаковых литералов th/классов.
  - Почему: Две копии одного HTML-генератора неизбежно разъедутся при первой же правке вёрстки таблицы сравнения или шагов трейса (как уже произошло с initKeyboardHelp).
  - Фикс: Выделить чистые функции buildComparisonHtml(criteria) и buildTraceHtml(steps), вызывать их из обоих путей.

**C17. `modules/quiz-app/src/main/resources/static/js/stats.js:11-21, 48-50`** — При битом JSON в #topic-stats-data getTopicStats молча возвращает [] (catch → return []), initCharts делает ранний return («if (topicStats.length === 0) return;») — но канвасы уже отрендерены сервером (stats.html:79 рисует .charts-row при непустом topicStats), а фолбэки #topicProgressChartFallback/#topicAccuracyChartFallback остаются с классом hidden (stats.html:86, 96). Пользователь видит две пустые карточки без какого-либо сообщения.
  - Почему: Сценарий «сервер отдал темы, но JSON не распарсился» (экранирование, обрезка) даёт пустые блоки графиков, хотя в шаблоне специально лежит фолбэк-текст «График временно недоступен» именно для этого случая.
  - Фикс: В ветке отказа (parse-fail или пустой результат при наличии канвасов) вызывать showChartFallback для обоих графиков вместо молчаливого return.

**C18. `modules/quiz-app/src/main/resources/static/js/app.js:1631-1653, 1699`** — Шаблон даёт #result-feedback aria-live=polite c aria-atomic="true" (post-answer-controls.html:7), а app.js после ответа выполняет несколько асинхронных вставок ВНУТРЬ этого региона: fetchWrongFeedback вставляет placeholder в .result-wrong (дочерний элемент feedbackDiv) и затем заменяет его контентом, fetchComparison вставляет cmpDiv через wrongBlock.after(cmpDiv) (1699) — тоже внутрь feedbackDiv. При aria-atomic=true каждая из 2-4 мутаций заставляет скринридер перечитывать ВЕСЬ вердикт с полным текстом ответа заново.
  - Почему: SR-пользователь после неверного ответа слышит полный текст разбора 3-4 раза подряд (placeholder появился, placeholder заменён, comparison вставлен) — шумовой а11y-антипаттерн, при том что для доп. контента уже есть отдельный live-контейнер #extra-analysis-content.
  - Фикс: Вставлять wrong-feedback/comparison в #extra-analysis-content (как takeaway/trace через appendAnalysisBlock), либо снять aria-atomic=true с #result-feedback.

**C19. `modules/quiz-app/src/main/resources/static/js/app.js:2056-2068`** — Фидбэк кнопки копирования кода чисто визуальный: flash() меняет класс и innerHTML кнопки («Скопировано»/«Ошибка»), live-region или role=status отсутствуют; aria-label кнопки остаётся «Копировать код» в обоих исходах.
  - Почему: Озвучивание смены содержимого фокусированной кнопки скринридерами нестабильно (NVDA/VoiceOver ведут себя по-разному); незрячий пользователь не получает подтверждения, скопировался код или произошла ошибка.
  - Фикс: Добавить рядом visually-hidden span с role=status (aria-live=polite) и писать туда «Код скопирован»/«Не удалось скопировать», либо обновлять aria-label кнопки синхронно с innerHTML.

**C20. `modules/quiz-app/src/main/resources/templates/settings.html:13-19`** — Кнопка «Свернуть панель» — мёртвый контрол: editorial.css:1094 безусловно прячет её во ВСЕХ дизайнах («html[data-design] .settings-page .sidebar-toggle-btn { display: none; }», правило вне @media, комментарий рядом: «В одной колонке свернуть-панель не нужна»), а строкой ниже контент форсируется видимым («#left-sidebar-content { display: block !important; }»). При этом app.js:103-104 продолжает её находить и вешать обработчики («const toggle = document.getElementById('sidebar-collapse-toggle');»).
  - Почему: Явное противоречие внутри кода: шаблон и JS поддерживают сворачивание, которое CSS навсегда отключил. Мёртвая разметка с aria-expanded/aria-controls обещает поведение, которого нет, и вводит в заблуждение при поддержке.
  - Фикс: Удалить кнопку из settings.html и её wiring из app.js (initSidebarToggle), либо — если сворачивание нужно на каких-то брейкпоинтах — убрать безусловный display:none и показать кнопку только там, где она работает.

**C21. `modules/quiz-app/src/main/resources/templates/fragments/mermaid-init.html:18`** — securityLevel: 'loose' разрешает HTML в label-ах и click-коллбэки mermaid, при том что источник диаграмм — вывод LLM: Javadoc MermaidSanitizer.java прямо говорит «Очистка и исправление типичных ошибок в Mermaid-коде, сгенерированном AI», а сам санитайзер чинит только синтаксис (fences, точки с запятой, кавычки в label) и не вырезает HTML/скрипты.
  - Почему: AI-вывод — недоверенные данные; loose-режим превращает их в XSS-поверхность (инъекция HTML/обработчиков через диаграмму прямо в DOM result-страницы).
  - Фикс: Поставить securityLevel: 'strict' (дефолт mermaid — экранирует теги и отключает click) или минимум 'antiscript'; проверить, что существующие seed-диаграммы рендерятся.

**C22. `modules/quiz-app/src/main/resources/templates/session-summary.html:9`** — В UI выводится сырое имя enum-константы: SessionSummary.mode имеет тип InterviewMode (проверено: «InterviewMode mode;» в SessionSummary.java:27), у enum нет русских имён и toString не переопределён — пользователь видит «TRAINING»/«FLASHCARD»/«MARATHON». То же в result.html:14: «Сессия: <strong th:text="${interviewSession.mode}">MODE</strong>». При этом settings.html:92-96 те же режимы уже переводит: «Тренировка», «Изучение», «Флешкарты», «Экзамен», «Интенсив».
  - Почему: Английские системные идентификаторы в полностью русском интерфейсе; пользователь, выбравший «Интенсив», на итогах видит «MARATHON» — связь не очевидна.
  - Фикс: Единый словарь отображаемых имён режимов (бин-утилита по типу @topicUtils.displayName) и использовать его в session-summary.html:9, result.html:14 и settings.html — один источник правды для лейблов.

**C23. `modules/quiz-app/src/main/resources/templates/error.html:27-30`** — Несогласованность ты/вы между страницами одного приложения. На «вы»: error.html:27-30 (цитаты слева) и result.html:64,67 («Ваш ответ: », «Ваш выбор»). На «ты»: focus-training.html:79 «Насколько уверенно ты ответил?», :145 «Обнови страницу через несколько секунд.», :148 «Попробуй обновить тренировку.», settings.html:124 «переходи во вкладку „Фокус“ и отвечай», :154 «если сменил AI-модель или хочешь перегенерировать всё», session-summary.html:78 «Нажми на вопрос», stats.html:60 «Измени запрос или сбрось фильтры», :209 «сгенерируй ещё».
  - Почему: Тон скачет между страницами одного флоу (вопрос на «ты» → результат на «вы» → ошибка на «вы») — выглядит как тексты разных авторов, разрушает целостность голоса продукта.
  - Фикс: Привести к «ты» (преобладает в кодовой базе): переписать копи error.html:27-30 и заменить «Ваш ответ»/«Ваш выбор» в result.html:64,67 на «Твой ответ»/«Твой выбор».

**C24. `modules/quiz-app/src/main/resources/templates/result.html:27`** — Класс has-side — мёртвый: ни один селектор CSS/JS его не использует (grep по всем css/js — единственное упоминание это комментарий editorial.css:1733 «Грид включаем через :has(.question-side) — а НЕ .has-side»). Вдобавок условие класса разошлось с условием рендера колонки: line 122 требует «aiEnabled and (diagram != null or …)», line 27 — без aiEnabled.
  - Почему: Мёртвый код с дублирующимся (и уже дрейфанувшим) условием — ловушка для следующего редактора: правка одного условия без второго даст рассинхрон.
  - Фикс: Удалить th:classappend с has-side целиком — CSS уже принял решение через :has(.question-side).

**C25. `modules/quiz-app/src/main/resources/templates/stats.html:199-200`** — Инлайн-ширина через th:style, хотя на этой же странице все остальные прогресс-полосы идут через th:attr="data-progress=…" (stats.html:130, 133; stats-grid.html:24; focus-training.html:26) и гидрируются общей функцией stats.js:2-9 hydrateProgressBarsFromData (el.style.width = safe.toFixed(1) + '%').
  - Почему: Два механизма для одной сущности (прогресс-полоса) в одном файле; inline-style ломает единообразие и будущий переход на строгую CSP (style-src без unsafe-inline).
  - Фикс: Заменить th:style на th:attr="data-progress=${…}" — существующий stats.js подхватит без единой строчки нового кода.

**C26. `modules/quiz-app/src/main/resources/templates/fragments/today-widget.html:27`** — Плюрализация по абсолютному значению, а не по mod 10/mod 100: при due=21 выводится «21 вопросов к повтору» (правильно — «вопрос»), при due=22…24 — «вопросов» (правильно — «вопроса»). Сломано для всех чисел ≥21, кроме оканчивающихся на 5–20.
  - Почему: Due легко превышает 20 при банке в сотни вопросов — грамматическая ошибка на самом видном числе виджета «Сегодня».
  - Фикс: Правило русской плюрализации: n%100 in 11..14 → «вопросов»; n%10==1 → «вопрос»; n%10 in 2..4 → «вопроса»; иначе «вопросов». Вынести в утилитный бин и вызвать из шаблона.

**C27. `modules/quiz-app/src/main/resources/templates/settings.html:27-47`** — Селекты «Группа» и «Тема» скопированы между settings.html:27-47 и stats.html:18-33 — включая дословно идентичное выражение построения option (цитата встречается в обоих файлах: settings.html:31 и stats.html:22) и одинаковые th:selected-условия.
  - Почему: Копипаста двух нетривиальных блоков: правка формата лейбла группы или условия selected в одном файле молча разъедется со вторым.
  - Фикс: Вынести во fragment (например fragments/filter-selects :: group-topic(groups, topics, selectedGroup, filter)) и подключить th:replace в обоих шаблонах.

**C28. `modules/quiz-app/src/main/resources/templates/stats.html:111-116`** — aria-label на <th> подменяет accessible name заголовка колонки: при навигации по ячейкам скринридер объявляет контекст колонки как «Сортировать по колонке: Тема» вместо «Тема» (для всех 5 сортируемых колонок, lines 111-116). role="columnheader" на нативном th в thead избыточен. Состояние сортировки и так передаёт aria-sort, который ставит stats.js:196.
  - Почему: Каждая ячейка таблицы озвучивается с многословным «Сортировать по колонке: …» — шум при чтении таблицы; ARIA-паттерн sortable table (W3C APG) держит имя заголовка чистым, состояние — в aria-sort.
  - Фикс: Убрать aria-label и role="columnheader" из шаблона; оставить текст th как имя, aria-sort из stats.js как состояние (data-sort-label уже хранит название для status-анонса).

**C29. `modules/quiz-app/src/main/resources/templates/fragments/today-widget.html:19-23, 41-44`** — aria-live="polite" на стрик-чипе, который app.js наполняет при КАЖДОЙ загрузке страницы: initStreakBar (app.js:158-171) делает fetch /api/streak и пишет textContent в [data-streak-days] внутри этого live-региона. На focus-странице чип входит в каждый экран вопроса.
  - Почему: Скринридер объявляет «N дней» после каждого перехода к следующему вопросу — это не изменение состояния, а обычная загрузка контента; классическое злоупотребление aria-live.
  - Фикс: Убрать aria-live с обоих фрагментов (today-hero и today-chip); если хочется анонсировать рост стрика — объявлять только при изменении значения относительно сохранённого.

**C30. `modules/quiz-app/src/main/resources/templates/stats.html:14, 42`** — Подпись «Фильтры» (и «Поиск» на line 42) свёрстана div-ом, тогда как та же сущность на settings.html:22 — заголовок: «<h2 class="control-title">Фильтры</h2>». Секция фильтров/поиска на /stats выпадает из outline документа (первый h2 на странице — «Результаты поиска» или «Прогресс по темам»).
  - Почему: Одинаковые блоки управления на двух страницах имеют разную семантику; SR-пользователь не может прыгнуть к фильтрам по заголовкам на /stats, хотя на /settings может.
  - Фикс: Заменить .stats-pane-label div-ы на h2 (визуальный стиль перенести на класс), выровняв с control-title-паттерном settings.

**C31. `modules/quiz-app/src/main/resources/templates/result.html:131-140`** — Подключение app.js стоит ВНУТРИ main между секцией результата и блоком «Похожие вопросы», тогда как на остальных страницах скрипт перед </body> (focus-training.html:168, settings.html:201). Сам блок похожих вопросов — div с h3 (line 135) вне какой-либо section, в отличие от всех соседних блоков страницы, обёрнутых в <section class="card">.
  - Почему: Контент после script-тега в середине main — структурная аномалия, осложняющая поддержку; div с заголовком вместо section ломает единый ландмарк/карточный паттерн страницы.
  - Фикс: Перенести <script> в конец body; обернуть похожие вопросы в <section class="card" aria-labelledby=…> как остальные блоки.

**C32. `modules/quiz-app/src/main/resources/templates/settings.html:147-148`** — Accessible name «Экспорт прогресса в JSON» не содержит видимый текст «Экспорт JSON» как непрерывную строку (между словами вклинивается «прогресса в»). То же у CSV-кнопки (line 148).
  - Почему: Нарушение WCAG 2.5.3 Label in Name: пользователь голосового управления, произнося видимую надпись «Экспорт JSON», не активирует кнопку.
  - Фикс: Убрать aria-label вовсе (видимого текста достаточно) или сделать имя с видимым префиксом: «Экспорт JSON — прогресс и статистика».

**C33. `modules/quiz-app/src/main/resources/templates/stats.html:3, 6`** — Один экран называется двумя словами: <title> — «Статистика», h1 в masthead и пункт навигации (header.html:30) — «Аналитика». Плюс result.html:6 передаёт в видимый h1 строку-тайтл целиком («pageTitle='Результат — Подготовка к собеседованию'»), тогда как остальные страницы передают короткое имя (error.html:6 — «Подготовка к собеседованию», session-summary.html:6 — «Итоги сессии»).
  - Почему: Расхождение имени страницы в табе браузера, заголовке и навигации дезориентирует; «Результат — Подготовка к собеседованию» как h1 — слипшийся документ-тайтл вместо названия экрана.
  - Фикс: stats: title='Аналитика — Подготовка к собеседованию'; result: pageTitle='Результат' (или «Разбор ответа»), суффикс оставить только в <title>.

**C34. `modules/quiz-app/src/main/resources/templates/focus-training.html:125`** — Символ ⌨ (U+2328) в подсказке (и в дефолте фрагмента training-actions.html:17) — эмодзи-подобный знак вне принятой системы иконок: проект заменил эмодзи на монохромный Lucide-спрайт, исключения зафиксированы только для 💡 и 🎲🤔💪.
  - Почему: ⌨ рендерится по-разному на разных ОС (где-то цветным эмодзи) — именно та проблема, ради которой эмодзи выпиливали; нарушает собственное правило системы иконок.
  - Фикс: Убрать символ (текст самодостаточен) или добавить #i-keyboard в спрайт icons.html и выводить <svg class="ed-icon">.

**C35. `modules/quiz-app/src/main/resources/templates/stats.html:229`** — Неэкранированная вставка в script-контекст: StatsPageService.java:52 сериализует обычным objectMapper.writeValueAsString(...), а Jackson по умолчанию не экранирует «<» и «/» — строка «</script>» внутри названия темы разорвёт script-элемент (breakout в HTML-контекст). Сейчас темы — доверенные пути файлов, поэтому не blocker.
  - Почему: Хрупкий инвариант «в данных никогда не будет </script>» нигде не проверяется; любое будущее пользовательское поле в topicStats превратит это в XSS.
  - Фикс: При сериализации экранировать «<» как < (Jackson CharacterEscapes / JsonWriteFeature.ESCAPE_FORWARD_SLASHES для «</») — JSON.parse в stats.js это не ломает.

**C36. `modules/quiz-app/src/main/resources/templates/focus-training.html:46`** — Одна сущность — пример кода вопроса — свёрстана по-разному: на focus голый <pre> без <code>, на result.html:126 — «<pre … class="question-code"><code th:text=…>». Инициализатор подсветки head.html:174 покрывает только «.markdown-content pre code», а hljs.highlightElement в app.js работает по «pre code» — голый <pre> на focus в принципе не может быть подсвечен.
  - Почему: Один и тот же сниппет выглядит по-разному на соседних экранах одного флоу (с подсветкой/стилями question-code и без); расхождение разметки блокирует единый стайлинг и подсветку.
  - Фикс: Унифицировать на <pre><code> в обоих шаблонах (можно с классом языка из метаданных вопроса), чтобы существующие селекторы hljs подхватывали оба места.

---

## D. Nit — вкусовщина с обоснованием


**D1. `modules/quiz-app/src/main/resources/static/css/editorial.css:674, 1067, 1279, 1443`** — Четыре правила — пустышки или no-op: L674 — пустой рулсет без деклараций; L1067 @media (max-width:600px) .flashcard-grade-buttons { grid-template-columns: 1fr 1fr; } — идентично базовому repeat(2, 1fr) на L970 (вычисляемое значение одно и то же); L1279 .stats-page .card h2.mt-0 { margin-top: 0; } — margin-top уже 0 из шортхенда margin: 0 0 var(--space-4) того же селектора (L1276), плюс есть глобальная утилита .mt-0 (L391); L1443 .maturity-low { color: var(--color-text-secondary); } — базовый .maturity-badge уже даёт ровно color: var(--color-text-secondary) (L1438).
  - Фикс: Удалить все четыре; если .maturity-low оставлен как страж от смены базового цвета — заменить на комментарий у L1438.

**D2. `modules/quiz-app/src/main/resources/static/css/editorial.css:968, 1010, 1012, 1058, 1220, 1460, 386`** — Шесть точечных правил повторяют скрытие через .hidden (L968, 1010, 1012, 1058, 1220, 1460), хотя глобальная утилита L386 — html[data-design] .hidden { display: none !important; } — с !important перебивает любой display независимо от специфичности. Все шесть — гарантированные no-op.
  - Фикс: Удалить шесть точечных правил, оставить только глобальный L386 (файл сам это знает: комментарий L2075-2076 у kbd-help прямо пишет «отдельное правило на .hidden не нужно»).

**D3. `modules/quiz-app/src/main/resources/static/css/editorial.css:989-990, 2440, 2336, 789, 875-877`** — Комментарии-навигаторы по файлу врут: L990 отсылает к «L896», реальное правило #interview-submit:disabled — L864-868; L2440 отсылает к «L1090», реальный display:block !important — L1095; L2336 отсылает к «(L1300, L1732)», реальные :has-прецеденты — L1305 и L1737. Плюс 17 комментариев ссылаются на styles.css («гасим старое индиго-свечение из styles.css» L789, «протёкший .secondary-btn{width:100%} из styles.css» L875-877 и т.д.), которого в static/css/ больше не существует (только editorial + 3 оверлея).
  - Фикс: Заменить номера строк якорями-названиями секций (grep-устойчиво), ссылки на styles.css пометить «(история, файл удалён)» или вычистить вместе с уже ненужными защитными декларациями.

**D4. `modules/quiz-app/src/main/resources/static/css/editorial.css:746, 955, 1687-1690, 1877-1878`** — Один и тот же disclosure-маркер <details> реализован четырьмя слегка разными способами: литерал без пробела + flex-gap (L746), escape-нотация \25B8 + gap (L955), литерал с хвостовым пробелом вместо gap (L1687-1690 error-details и L1877-1878 sm2-details).
  - Фикс: Единый рецепт: одна нотация (литерал «▸»), отступ всегда через gap у summary; в идеале — общий класс .ed-disclosure-summary.

**D5. `modules/quiz-app/src/main/resources/static/css/editorial.css:2352, 2391, 1396, 1422, 1466, 1491, 1061, 2084`** — Две однотипные sticky-рейки WIDE LAYOUT сайзятся разными моделями: focus-рейка — жёсткие 19rem (L2352), related-рейка — эластичный minmax(16rem, 22rem) (L2391). Вокруг — россыпь размерных констант вне токен-шкал: height: 320px (L1396), width: 120px (L1422), колонки 100px/44px и 84px/36px (L1466, L1491), max-width: 560px (L1061) и 420px (L2084).
  - Фикс: Выбрать одну модель для реек (minmax — лучше деградирует на 1200px) и завести 2-3 layout-токена (--rail-width, --panel-max-width) для повторяющихся констант.

**D6. `modules/quiz-app/src/main/resources/static/css/editorial.css:266-274, 487-497`** — Reduced-motion реализован двумя независимыми механизмами: обнуление duration-токенов в :root (L266-274) и универсальный селектор с !important, глушащий transition/animation у всех элементов (L487-497). Второй покрывает всё, что покрывает первый (все transition в файле живут на элементах внутри html[data-design]), — токенный блок ничего не добавляет.
  - Фикс: Оставить один механизм — универсальный !important-блок (он же страхует инлайн-стили); токенный блок удалить или оставить только если оверлеи начнут использовать токены вне скоупа html[data-design].

**D7. `modules/quiz-app/src/main/resources/static/js/stats.js:2-9`** — hydrateProgressBarsFromData продублирована из app.js (строки 50-63): обе итерируют [data-progress] и пишут el.style.width, но stats-версия не нормализует атрибут обратно (app.js: «element.setAttribute('data-progress', safe.toFixed(1))», stats.js — только width).
  - Фикс: Вынести setProgressValue/hydrateProgressBarsFromData в общий progress.js, подключаемый на обеих страницах.

**D8. `modules/quiz-app/src/main/resources/static/js/app.js:534-541`** — obtainAdminToken использует блокирующий window.prompt для ввода admin-токена; вызывается из runExport (570) и regenerateQuestion (965).
  - Фикс: Заменить на инлайн-поле ввода в data-export-block (input type=password + кнопка), токен валидировать тем же запросом; prompt оставить разве что как фолбэк.

**D9. `modules/quiz-app/src/main/resources/static/js/app.js:26-32`** — Семь констант — бессмысленные локальные алиасы полей замороженного UI_CONSTANTS, объявленного пятью строками выше: «const EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT = UI_CONSTANTS.EXTRA_ANALYSIS_BUTTON_INITIAL_TEXT;» и т.д.
  - Фикс: Использовать UI_CONSTANTS.X напрямую (или наоборот — оставить только плоские константы и удалить объект).

**D10. `modules/quiz-app/src/main/resources/static/js/app.js:1957-1966`** — initKeyboardHelp вызывается безусловно на каждой странице с app.js (DOMContentLoaded, строка 751), включая /settings и /result, и всегда показывает один и тот же список: «1…9 — выбрать вариант», «Space — раскрыть флешкарту», «1-4 — оценить флешкарту» — хотя на /settings ни один из этих шорткатов не работает (нет ни interview-form, ни .flashcard-phase).
  - Фикс: Формировать список по контексту: блок MCQ-шорткатов добавлять только при наличии #interview-form, флешкарт-блок — при .flashcard-phase; общие ('?', Esc) — всегда.

**D11. `modules/quiz-app/src/main/resources/templates/fragments/header.html:19`** — Inline-стиль на бренд-ссылке шапки — единственный недокументированный style= во всех шаблонах (style="display:none" на спрайте icons.html:17 явно обоснован комментарием).
  - Фикс: Перенести в editorial.css: html[data-design] .ed-masthead-brand { text-decoration: none; } и убрать атрибут style.

**D12. `modules/quiz-app/src/main/resources/templates/fragments/training-actions.html:15`** — Единственная ссылка во всех шаблонах с захардкоженным href="/" вместо th:href="@{/}" (все остальные ссылки используют @{…}-синтаксис, ср. focus-training.html:159 th:href="@{/settings}").
  - Фикс: Заменить на th:href="@{/}" (JS-перезапись href в app.js это не затрагивает).

**D13. `modules/quiz-app/src/main/resources/templates/fragments/training-actions.html:17`** — aria-live="polite" на статичном тексте: подсказка рендерится сервером и никогда не меняется — в app.js нет ни одного обращения к keyboard-hint (grep по app.js — 0 совпадений).
  - Фикс: Убрать aria-live с keyboard-hint.

**D14. `modules/quiz-app/src/main/resources/templates/error.html:39-42`** — В одном dl смешаны русские и английские термины: «Путь», «Сообщение», «Время» — но «HTTP status» и «Reason» без перевода.
  - Фикс: «HTTP status» → «HTTP-статус», «Reason» → «Причина».

**D15. `modules/quiz-app/src/main/resources/templates/settings.html:100-101`** — Канцелярская аббревиатура «Кол-во:» среди полных подписей той же формы: «Режим:» (line 90), «Порядок:» (line 68), «Группа:», «Тема:».
  - Фикс: Заменить на «Вопросов:» (или «Количество:»).

**D16. `modules/quiz-app/src/main/resources/templates/settings.html:175`** — Опечатка в названии библиотеки: «shadcn/Ui» вместо общепринятого «shadcn/ui».
  - Фикс: Заменить на «shadcn/ui».

**D17. `modules/quiz-app/src/main/resources/templates/result.html:12-17`** — Мёртвые else-ветки: секция уже отрендерится только при interviewSession != null (line 12), но внутри трижды повторяется проверка «interviewSession != null ? … : 0» (lines 15, 16, 17) — ветка «: 0» недостижима.
  - Фикс: Упростить до ${interviewSession.getIndex() + 1}, ${interviewSession.getTotal()}, ${interviewSession.getCorrect()}, ${interviewSession.getWrong()}.

**D18. `modules/quiz-app/src/main/resources/templates/session-summary.html:38, 41`** — Одно действие (переход на /) в двух взаимоисключающих ветках названо по-разному: «Продолжить тренировку →» и «Продолжить». Аналогично разъехались aria-label однотипных навигаций: session-summary.html:36 «Что дальше» vs error.html:50 «Куда дальше».
  - Фикс: Унифицировать: оба лейбла «Продолжить тренировку», оба aria-label навигаций — «Что дальше».

---

## Порция 1 к немедленному исправлению (этот раунд)

| # | Замечание | Объём |
|---|-----------|-------|
| A1 | Активная вкладка: ink-текст + Clay-подчёркивание | CSS, 1 правило |
| A2 | `--color-accent-strong: #C96442` для hero-due (light) | CSS, токен + 1 правило |
| A3 | Dark `--color-status-error` → `#EC8580` | CSS, 2 строки токенов |
| B1 | TRAINING: прятать «Кол-во», CTA «Начать тренировку» | шаблон + JS |
| B2 | Вердикт: «+N штрафных вопросов за ошибку» | JS, ~10 строк |
| B3 | «Завершить сессию» в фокус-рейке | шаблон, ~5 строк |
| C-live-1 | th:if на пустой прогрессбар | шаблон, 2 атрибута |
| C-live-2 | «Итоги сессии» на последнем вопросе | JS, ~5 строк |

Остальное — раунды 2+. После фиксов порции — повторная критика с нуля (по инструкции).

## Порция 2 — копи/семантика/i18n/a11y шаблонов (2026-06-15)

Батч низкорискового слоя (шаблоны + 3 утилитных бина), верифицирован live
(bootRun :8080, editorial dark): /, /settings, /stats — 200 без ошибок;
плюрализация (`due=9615 → «вопросов»`), h2-лейблы /stats (mono-капс сохранён),
отсутствие ⌨ и опечатки shadcn — подтверждены в отрендеренном HTML; пост-ответный
flow на /focus отрисован чисто.

| # | Замечание | Как закрыто |
|---|-----------|-------------|
| C22 | Сырой enum режима в UI | Бин `@modeUtils.displayName` (session-summary, result) — единый словарь, как `@topicUtils` |
| C23 | ты/вы рассинхрон | error.html + result.html переписаны на «ты» |
| C24 | Мёртвый класс `has-side` | Удалён th:classappend (CSS решает через `:has(.question-side)`) |
| C25 | Inline `th:style` прогресс-бара | → `th:attr=data-progress` (гидрирует stats.js) |
| C26 | Плюрализация по абс. значению | Бин `@plural.pick` (n%100/n%10 правила) |
| C30 | «Фильтры»/«Поиск» как div | → `<h2 class="stats-pane-label">` + CSS-нейтрализация h2 (специфичность 0,4,2 бьёт `.card h2`) |
| C31 | script в середине main; related как div | script → конец body; related `<div>`→`<section aria-labelledby>` (остался прямым потомком .ed-page для WIDE LAYOUT, без .card) |
| C32 | Label-in-Name на экспорт-кнопках | aria-label начинается с видимого текста |
| C33 | «Статистика» vs «Аналитика»; длинный h1 result | title→«Аналитика…»; result pageTitle→«Результат» |
| C34 | Символ ⌨ (U+2328) | Убран из training-actions + focus-training |
| D11 | Inline style в шапке | → `.ed-masthead-brand { text-decoration:none }` |
| D12 | Захардкоженный href="/" | → `th:href="@{/}"` |
| D13 | aria-live на статичной подсказке | Убран |
| D14 | EN-термины в error dl | «HTTP status»→«HTTP-статус», «Reason»→«Причина» |
| D15 | «Кол-во:» | Уже «Вопросов:» (закрыто ранее) |
| D16 | Опечатка «shadcn/Ui» | → «shadcn/ui» |
| D17 | Мёртвые else-ветки result | Тернары упрощены (внутри `th:if=interviewSession!=null`) |
| D18 | Рассинхрон лейблов/aria summary | «Продолжить»→«Продолжить тренировку»; aria «Куда дальше»→«Что дальше» |

Отложено (требует правок JS, отдельный батч): C28 (stats.js читает/пишет aria-label
сортируемых th — снять можно только вместе с JS), B4–B7, прочие C/D с JS/CSS-дедупом.

## Порция 3 — B4 чип статуса + ты/вы в app.js (2026-06-16)

- **B4** Чип статуса варианта (`.option-status-*`) был стилизован только под
  `.result-page`; app.js инжектит те же классы на JS-флоу focus-страницы, где чип
  оставался голым текстом, а `.option-status-muted` не имел НИ ОДНОГО правила.
  Снят `.result-page`-скоуп → визуальные правила (mono/рамка/капс) общие для обоих
  путей; добавлено правило `.option-status-muted` (нейтральная рамка/текст).
  Верифицировано live: неверный ответ на /focus → «ПРАВИЛЬНЫЙ ОТВЕТ» (зелёная рамка),
  «ТВОЙ ВЫБОР (ОШИБКА)» (красная), «НЕ ВЫБРАН» (нейтральная) — все бордерные mono-чипы.
- **C23-доп.** Тот же ты/вы рассинхрон в app.js (критика покрыла только шаблоны):
  все пользовательские строки переведены на «ты» — «Попробуй ещё раз», «Проверь
  сеть/соединение», «Введи admin-токен», и чип «Твой выбор (ошибка)».

editorial.css v71→v72, app.js v31→v32 (result/settings/focus-training).

## Порция 4 — B6 клавиатурный гард + B7 ловушка localStorage (2026-06-16)

- **B6** WCAG 2.1.1 (Keyboard): `initFlashcardShortcuts` вешал глобальный
  `keydown`-листенер, который ловил Enter/Space ВЕЗДЕ. На флешкард-странице это
  означало: Enter/Space на nav-ссылке шапки, на `<summary>` «Пример кода» или на
  любой `<button>` не выполнял нативное действие, а слал `revealBtn.click()` (POST
  /flashcard-reveal). Добавлен тот же гард, что уже стоял в MCQ-обработчике
  (app.js:1294): если `event.target` — `SUMMARY`/`BUTTON`/`A`, выходим до
  перехвата. Теперь нативная семантика этих элементов сохраняется.
- **B7** Ловушка localStorage. Контролы режимов обучения (instant/hard/review/
  adaptive/timer) были удалены вместе со старым `index.html` — UI, чтобы их
  изменить, больше нет. Но `loadLearningPrefs` продолжал ПРИМЕНЯТЬ значения,
  записанные старой версией под ключом `v2`: у кого в localStorage висел
  `hardMode=true`, тот НАВСЕГДА терял confidence-кнопки (app.js:1825) и доп.анализ
  (app.js:1832); `timerSeconds>0` запускал неотключаемый таймер. Бамп ключа
  `quiz.learning.prefs.v2`→`v3` осиротляет stale-данные (их больше никто не
  читает), а `v3` никто не пишет (`onLearningPrefChange` недостижим) → prefs всегда
  = безопасные defaults. Верифицировано live: верный ответ на /focus → confidence-
  блок «Насколько ты уверен…» и кнопка «Показать доп. анализ» появляются (раньше
  при stale-hardMode были скрыты); консоль чистая.

app.js v32→v33 (result/settings/focus-training).

## Порция 5 — B5 сигнатура радиусов кнопок (2026-06-16)

Editorial-сигнатура (editorial.css L2612): утилитарные/secondary кнопки — плоские
`0`, accent-залитые primary-CTA — асимметричный `0 0 8px 8px` («Try Claude»). На
деле одинаковые кнопки имели `0` / `8px` / `0 0 8px 8px` вразнобой. Корень (как
показал верификатор): flat-список сигнатуры (0,2,1) проигрывал по специфичности
page-правилам (0,3,1 … 1,3,2), которые заново объявляли `border-radius-md`.

Фикс — правил радиус в **самих побеждающих page-правилах**, а не во flat-списке:
- → плоские `0`: `#filters-form submit` («Применить»), `.btn-finish-small`,
  `.data-export-actions .btn` (Экспорт JSON/CSV), `.danger-btn` («Сбросить банк»),
  `.stats-action-btn` (база), `.table-expander`, `.result-page .btn-finish`,
  `.confidence-btn` (обещание комментария «единообразно с .flashcard-grade-btn»).
- → асимметричные `0 0 8px 8px`: `#session-form submit` («Начать сессию/
  тренировку»), `.stats-search-action` («Искать») — оба accent-fill primary.

Верифицировано live (computed `border-radius`): settings — Применить/Экспорт/
Сбросить = `0px`, «Начать тренировку» = `0px 0px 8px 8px`; stats — stats-action-btn/
table-expander = `0px`, stats-search-action = `0px 0px 8px 8px`. Today-hero CTA
(«Начать повторение») = `.btn.next-btn` → уже асимметричный через сигнатуру, новой
рассинхронизации нет.

editorial.css v72→v73.

## Порция 6 — C-блок: видимая иерархия + a11y (2026-06-16)

- **C8** Иерархия размеров кнопок. Главное действие флоу (`#interview-submit`
  «Проверить ответ», `.flashcard-reveal-btn` «Показать ответ») было `min-height:44px`,
  тогда как все прочие primary-CTA (`#session-form submit`, `.summary-actions .btn`)
  и даже вторичные кнопки самооценки (`.flashcard-grade-btn`, `.confidence-btn`) —
  48px. Самая частая кнопка была мельче второстепенных. → 48px / padding space-3 6.
  Верифицировано live: computed `min-height: 48px`.
- **C1** Тап-цель кнопки копирования кода. В `@media (hover: none)` поднимали только
  `min-height:44px`; на `<=600px` лейбл скрыт, остаётся иконка 14px + padding ≈ 32px
  по горизонтали → цель 32×44 (нарушение WCAG 2.5.5, на которое сам комментарий и
  ссылался). → добавлены `min-width:44px; justify-content:center`.
- **C28** Многословный `aria-label` на сортируемых `<th>`. Скринридер объявлял
  «Сортировать по колонке: Тема» вместо «Тема» в КАЖДОЙ ячейке; на сортировке
  aria-label снова перетирался («Сортировка: …»). Приведено к W3C APG sortable-table:
  имя = чистый текст `<th>`, состояние = `aria-sort` (ставит stats.js), транзиентный
  анонс = live-region `#table-sort-status`. Убраны `role="columnheader"` (избыточен
  у th в thead) и `aria-label` из шаблона + вся манипуляция aria-label в stats.js.
  Верифицировано live: имена чистые, после клика «Точность» → `aria-sort=ascending`,
  aria-label остаётся null, статус «Таблица отсортирована: Точность, по возрастанию».
- **C29** `aria-live="polite"` на стрик-чипе (today-hero + today-chip). `initStreakBar`
  наполняет чип при КАЖДОЙ загрузке (на /focus — каждый вопрос) → SR озвучивал
  «N дней» после каждого перехода. Это загрузка контента, не смена состояния. Убран.
  Верифицировано live: `aria-live` снят.
- **C21** (mermaid `securityLevel: 'loose'`) — **снято**: `fragments/mermaid-init.html`
  удалён вместе с фронтенд-mermaid (задача #48), XSS-поверхности больше нет.

editorial.css v73→v74, stats.js v7→v8.