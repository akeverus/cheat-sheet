# FRONTEND_EVIDENCE — доказательная база FINALIZATION ROUND

> Куда складывать факты: ground-truth inventory, live-QA (chrome-devtools), Lighthouse, скриншоты, замеры.
> Задачи ссылаются сюда вместо повторного чтения кода. Append-only по мере накопления.

---

## EV-INV-001 — Ground-truth inventory (2026-07-15)

**Источник:** workflow `wpi2y5071` (7 параллельных срезов, 0 ошибок, 593k subagent-токенов).
**Журнал:** `.claude/projects/.../subagents/workflows/wf_c29cc00b-120/journal.jsonl` (7 result-строк).

### Маршруты (21 эндпоинт, 3 контроллера)
- **InterviewMvcController** (@Controller, 13): GET `/` `/training` `/review` `/settings` `/stats`; GET `/session-summary`; POST `/start` `/study-confirm` `/flashcard-reveal` `/flashcard-grade` `/finish` `/answer` `/settings/reset-options`.
- **InterviewApiController** (@RestController, 7 `/api/*`): POST `/api/answer` `/api/confidence` `/api/favorite`; GET `/api/streak` `/api/stats` `/api/topic-stats` `/api/next`.
- **ExportController**: GET `/export` (admin-token).
- View-имена через `MvcNavigationService`. Ошибки — `GlobalExceptionHandler` (@ControllerAdvice), нет отдельного /error-контроллера; error.html обслуживает и его, и дефолт Spring Boot.
- **Отсутствуют:** `/pause` `/resume` `/api/report` (grep→0). Actuator: `/actuator/health`(+liveness/readiness)`/info`. Swagger в dev, off в prod.

### Шаблоны/фрагменты
- 6 top-level: focus-training · result · settings · stats · session-summary · error. **0 orphan.**
- 12 фрагментов в 10 файлах: head, header, icons(sprite), stats-grid(+content), inline-alert, mermaid-init, post-answer-controls, result-zone-head, today-widget(hero+chip), training-actions. Все имеют потребителя.
- `result.html` = живой no-JS fallback для POST /answer (не dead). Reuse только через `th:replace`.

### JS/CSS-ассеты + версионирование (PROC-16)
- 4 файла статики (без билд-шага): `app.js` (1972 строки), `stats.js` (410), `base.css` (3895), `tokens.css` (466).
- Версии: `app.js v=65` (**НЕ single-sourced** — focus-training:229, result:142, settings:291 → латентный drift-риск), `base.css v=96` (single, head.html:310), `tokens.css v=64` (single, head.html:309), `stats.js v=15` (single, stats.html:340).
- `app.js` не на /stats и /session-summary. `stats.js` держит СВОИ копии `hydrateProgressBarsFromData` + `initKeyboardHelp` (риск расхождения).
- CDN (версия в URL): highlight.js@11.9.0, chart.js@4.5.0, mermaid@11.12.0.

### Дизайн-варианты (PROC-06 → DEC-002)
- **canonicalCount = 1** (instrument). `DESIGNS=['instrument']`; tokens.css токен-блоки только instrument; SSR-дефолт instrument.
- **CMP-1 (blocking):** settings.html seg-control `data-design-pref` перечисляет **11** (коммент 'Дизайн (11 кнопок)', L169-183); `setDesign()` коерсит не-instrument в instrument (head.html:98) → 10 мёртвых кнопок.
- Dead в base.css: SIGNATURE-блоки editorial(~L3438)/swiss(~L3535)/linear(~L3519)/stripe(L846)/claude(L3500). head.html THEME_COLORS = 11 ключей (10 недостижимы).

### Wide-layout (PROC-07 → DEC-003) — 4-стороннее расхождение
- CSS: центрированные узкие колонки (page 1280 / focus 960 / margin auto), нет media ≥1440. `--measure:72ch`.
- mockups DESIGN.md: full-width + правый рельс `.focus-aside`≥1080px, 66ch.
- root /DESIGN.md: `--max-width-*:none`, `--measure:none`.
- project memory: deliberate full-width prose (259ch).

### Backend-флоу (для MUST-фич)
- **Сессия только в HTTP-session** (`HttpSessionStateService`, ключ `interviewSession`); `InterviewSession` Serializable с `answerHistory: List<AnswerRecord(questionId, correct, topic)>`. TRAINING сессию не создаёт. При expiry/рестарте активная сессия теряется (в БД не персистится).
- **answerFlow:** POST /answer (@Valid SubmitAnswerRequest, optionId @Positive **обязателен**) → InterviewService.submitAnswer @Transactional → ReviewService.applyAnswer (SM-2 + user_topic_stats) → applySessionProgress (registerAnswer: index++, correct/wrong, AnswerRecord). `registerAnswer` **НЕ сверяет** questionId с текущим index → двойной POST считается дважды.
- **SM-2** (`SpacedRepetitionService`): correct→grade5, wrong→2; confidence зажимает correct в [3,5]. Исходы бинарные: CORRECT/WRONG(+RESET). **UNKNOWN нет.** Clock инъектируем (→ облегчает QA-01).
- **Миграции:** Flyway V1..**V15**, PostgreSQL only (`:5432/interview`, тесты Testcontainers 16-alpine). Нет таблицы issue/report.
- **SessionSummary** (`SessionSummaryService`, quiz-domain): логика в Java (не в шаблоне ✓); есть score/mistakes/weak/topicResults; нет unknownCount/dueCount/typed-nextActions. Одноразовый (clearLastSessionSummary). Смежное для due/next: `QuestionStatsRepository.countDue/findReviewForecast/findWeakestTopic`, daily_activity(V10), DailyStreakService.

### base.css:754 (CLEAN-01B)
- `.question-code-details > pre { margin-top: --space-3 }` — **мёртв**: app.js оборачивает `<pre>` в `.code-copy-wrap` → direct-child match разорван. Отступ не применяется (подтверждено inventory + прошлой live-probe gap=0). Фикс: перецелить на `.question-code-details > .code-copy-wrap` / `pre.question-code`.

---

## EV-ENV — окружение

- bootRun **жив на :8080** (2026-07-15, curl→200) → `./gradlew test/build` = wedge. postgres `:5432`. Live-правка static: `cp source → build/resources/main` (без рестарта). Chromium-only browser-QA; истинный <485px — device-emulation.

---

## Live-QA / Lighthouse / скриншоты

### EV-QA-001 — terminal-state CTA + код-блок (2026-07-15, R0.167, chrome-devtools :8080, 375px)

**UX-01 (иерархия CTA empty/done) — VERIFIED-CLEAN, обе темы.** Empty-state форсирован `/?topic=zzz-nonexistent`. Ветка settingsPrimary: primary «Открыть настройки» computed `bg oklch(0.47 0.115 205)` (light) / `oklab(0.47…)` teal (dark), `bgImage:none`, текст accent-on — доминирует; secondary «Обновить тренировку» `bg rgba(0,0,0,0)` + нейтральный бордер (outline). Markup вычисляет ровно один `.next-btn` primary по состоянию (finished→«итоги» / settingsPrimary→«настройки» / else→«обновить»); primary совпадает с текстом-инструкцией. Дефект «secondary сильнее» не воспроизводится.

**UX-03 (градиент/блик на primary CTA) — VERIFIED-CLEAN.** Все primary CTA — сплошная `var(--color-accent-primary)`; live `bgImage:none` обе темы; в base.css 0 `linear/radial-gradient`, 0 `::before`-блика, 0 shine-shadow на кнопках.

**CLEAN-01B (мёртвый base.css:754) — FIXED + verified.** Live-структура: `.question-code-details` → `.code-copy-wrap` → `<pre>` (app.js оборачивает); `directChildPre:false` → старый `> pre` не матчил → `gap summary→pre = 0`. Фикс: селектор расширен на `> pre, > .code-copy-wrap`; `?v` base.css 96→97; после reload(ignoreCache) `gap = 12px` (=--space-3), console чист.

*Тема переключается штатным `window.__theme.set()` (persisted-pref перебивает `emulate colorScheme`, нужен либо set, либо reload). Виджет-минимум для theme-QA: emulate dark + reload ИЛИ __theme.set('dark').*

### EV-QA-002 — UX-02 terminal-state ↔ активный shell (2026-07-15, R0.168, chrome-devtools :8080, 1280×900)

**UX-02 (terminal-состояния читаются как продолжение flow, не отдельный мини-сайт) — VERIFIED-CLEAN.** Empty-state форсирован `/?topic=zzz-nonexistent-topic-xyz` (GET, сессия не создаётся, без записей). Live-инспекция:
- **Тот же shell:** `shellPresent:true`, `navPresent:true`, nav = «Фокус Аналитика Настройки», активная вкладка `aria-current` = «Фокус» (teal-underline) — идентичный masthead+nav тренировочного flow, не изолированная страница.
- **Full-width, не плавающий мини-сайт:** карта `.empty.empty-card-inner` `max-width:none`, ширина 1164px (край-в-край панель), `margin-top:64px`.
- **Панель = border, не elevated card (Instrument-этос):** `box-shadow:none`; `border:1px solid`; `radius:16px`; `bg oklch(0.958 0.004 262)` (bg-secondary) чуть отличается от pageBg `oklch(0.985 0.003 262)` — subtle-демаркация региона без «приподнятости». Согласуется с мандатом Instrument «full-border ряды, не карточки-с-тенью».
- **Интегрированная типографика + CTA:** заголовок «Сейчас нет вопросов» (Newsreader serif), один primary `.next-btn` «Открыть настройки» (см. UX-01). 
- **Вывод:** terminal-состояние рендерится ВНУТРИ активного shell, full-width, border-based — дефект «читается как отдельный мини-сайт» не воспроизводится (был против мокапа/старого дизайна). Instrument-порт уже унифицировал terminal-состояния с shell.

### EV-QA-003 — Инвентарь пост-ответной раскрытия + no-JS hint (2026-07-15, R0.169, статический разбор result.html/focus-training.html/app.js)

**UX-13 (прогрессивный разбор) — ядро decision-gated (DEC-006), no-JS-consistency часть FIXED.**

Текущая раскрытие пост-ответа (по коду, не выдумано):
- **JS-путь:** `renderFeedbackHtml` (app.js:1420) кладёт в `#result-feedback` вердикт (`<strong>Верно/Неверно</strong>`) **И полный `answerHtml` сразу вместе** (aria-live=polite status). `applyOptionStyles` (app.js:1365) навешивает correct/wrong/other + status-label + per-option `.option-explanation`. Раскрытие-`<details>` per-option добавляет `optionExplanationSummaryText` (app.js:1222, summary «Почему выбранный ответ неверен»/«Пояснение к варианту»). Похожие вопросы — за кнопкой `#extra-analysis-toggle` → `appendAnalysisBlock` в `#extra-analysis-content` (app.js:1537). Уверенность (только correct) — `attachConfidenceButtons` под фидбэком.
- **no-JS (result.html):** вердикт (стр. 52), варианты+пояснения (58–73, inline), «Пояснение» `answerHtml` (75–78, inline), SM-2 в `<details>` (80–90), похожие вопросы server-rendered inline (123–138). Клик-раскрытий нет.

**Дефект (FIXED):** result.html zone-head hint (стр. 95) обещал «доп. анализ — **по кнопке**» — но AI-разбор и его кнопка/контейнер удалены (R0.126, стр. 105–108); в no-JS доп-кнопок нет вовсе. Hint вводил no-JS-пользователя в заблуждение. → переписан на «Полный ответ, пояснения и похожие вопросы — сразу на странице» (честно описывает no-JS-модель). `?v`-бамп не нужен (строка-параметр th:replace, SSR). Live-verify no-JS result **честно заблокирован**: путь достижим только POST /answer, который пишет SM-2 (запрет destructive live submit) → положился на статическую корректность (изменение = строковый литерал в существующем вызове фрагмента, без структурных/логических правок).

**Ядро UX-13 (краткое почему → полный под клик) — BLOCKED (DEC-006):** любой вариант спорен — инверсия (полный `answerHtml` под клик) противоречит deliberate «полный ответ доступен сразу»; настоящий отдельный tier требует backend-поля summary (needsGradle). → собранный вопрос.

---

## Performance / delivery

### EV-PERF-001 — prune Google Fonts (2026-07-15, R0.168, chrome-devtools :8080)

**PERF-02 (font-prune, часть a) — инкремент DONE + verified.** Из Google Fonts `<link>` + `<noscript>` в `head.html` удалены **Geist, Geist Mono, Source Serif 4** — грузились впустую (0 ссылок: ни `--font-family-*` в tokens.css, ни fallback-стеки, ни class-хуки; Source Serif 4 — тяжёлый variable-шрифт с ital+opsz осями). Осталось 5 семейств: Inter, JetBrains Mono, Lora, Newsreader, Space Grotesk.
- **?v-бамп НЕ нужен** — правка URL в шаблоне = text-only template change (head.html рендерится SSR каждый запрос; сам URL — cache-key Google Fonts). Подтверждено: `tokens.css?v=64`/`base.css?v=97` без изменений в Network.
- **Live-Network (reload ignoreCache):** fonts-CSS запрос содержит ровно 5 семейств (0 Geist/Geist Mono/Source Serif). Реально скачанные woff2 — только Newsreader + Space Grotesk + JetBrains Mono ×2 (используемые); Inter/Lora не качаются (перебиты instrument-токенами).
- **`document.fonts` = [Inter, JetBrains Mono, Lora, Newsreader, Space Grotesk]** — 3 удалённых семейства отсутствуют в @font-face полностью.
- **0 регресса:** computed hero=`Newsreader…`, mono=`JetBrains Mono…` (как до prune); console чист (0 error/warn).
- **Тема-инвариантно:** tokens.css не переопределяет `--font-family-*` в `:root[data-theme=dark]` (только цвета) → font-delivery одинаков в обеих темах, light-проверки достаточно.

**Остаток PERF-02 (не закрыт):**
- **(a2) полный prune до 3** (убрать Inter/Lora) требует переписать `:root` font-stacks tokens.css (81–82) — сейчас это inert editorial-фолбэк, перебиваемый instrument-блоком, но достижимый на non-instrument-пути. Трогает deliberate «editorial-базу :root» → пара к **FE-CMP-1** (editorial-remnant cleanup).
- **(b) prod-like budget-замер** явно требует НЕ devtools-bootRun среды (unminified/devtools-overhead) → эффективно **BLK-BOOTRUN-adjacent** (нужна prod-профиль сборка).

### EV-PERF-002 — font-prune (a2): до 3 семейств (2026-07-15, R0.172, chrome-devtools :8080)

**PERF-02 font-prune ЗАВЕРШЁН (a+a2).** Из Google Fonts URL (link+noscript) убраны **Inter, Lora** → осталось ровно **3**: JetBrains Mono, Newsreader, Space Grotesk (= acceptance «prune до Newsreader/Space Grotesk/JetBrains»).
- **Подход уточнён vs прогноз R0.168:** переписывать `:root` font-stacks НЕ пришлось. Instrument-токен `--font-family-body` (Space Grotesk) перебивает `:root`-фолбэк (Inter) на ВСЕХ элементах (root `data-design="instrument"`) → Inter/Lora не рендерятся ни на одном пути. Достаточно снять из URL. `:root` ещё словесно ссылается на Lora/Inter (inert, теперь не загружается → system-ui/Georgia; недостижим) — финальная чистка отнесена к FE-CMP-1 (:root editorial-база).
- **Критичная safety-проверка (live):** `getComputedStyle(body).fontFamily = "Space Grotesk"…`, **`bodyUsesInter:false`**; hero=Newsreader, option=Space Grotesk, nav=JetBrains Mono — все рендерятся инструментными; 0 missing-glyph.
- **Network (reload ignoreCache):** fonts-CSS = 3 семейства; woff2 качаются только используемые (JetBrains Mono ×2 / Newsreader / Space Grotesk). `document.fonts` = ровно [JetBrains Mono, Newsreader, Space Grotesk] — Inter/Lora отсутствуют.
- **0 регресса, console чист. `?v`-бамп не нужен** (URL в SSR-шаблоне — text-only). Тема-инвариантно.

**Остаток PERF-02 (единственный):** (b) prod-like budget-замер — требует не-devtools среды (BLK-BOOTRUN-adjacent). Font-prune часть закрыта полностью.

*(append далее — вьюпорты 375/768/1280/1440/1728/1920/2560, обе темы; Lighthouse prod-like)*

---

## Accessibility (A11Y-01)

### EV-A11Y-001 — консолидированный SR/keyboard проход core-flow (2026-07-15, R0.170, chrome-devtools :8080, 1280)

**Единый проход по 3 core-страницам (focus → settings → stats), light-тема; a11y-структура тема-инвариантна, dark-контраст покрыт статически `design-token-audit.py` (OKLCH AA CLEAN, memory).**

**Lighthouse (focus, desktop, navigation):** Accessibility **100**, Best Practices **100**, Agentic **100**. 2 фейла — оба SEO (`is-crawlable` noindex — намеренно для внутреннего auth-tool; `meta-description` — SEO-нюанс) → вне scope.

**Focus/training (`/?topic=…`):**
- Landmarks: `header` · `nav "Основная навигация"` · `main` · `aside "Сессия"` ✅.
- Heading-order: H1 «Подготовка к собеседованию» → H2 (вопрос) → H3 «Горячие клавиши» — без скипов ✅.
- Radiogroup: `aria-label="Варианты ответа"`, `aria-describedby=options-flow-hint` (резолвится ✅), 4 опции, per-option `aria-label` «Вариант N: …» ✅.
- Live-region: `interview-alert` assertive (ошибки), `result-feedback`/`extra-analysis-content`/`code-copy-status` polite ✅.
- Skip-link «Перейти к вопросу» → `#main-content` ✅.
- **ДЕФЕКТ НАЙДЕН+ИСПРАВЛЕН (A11Y-defect, R0.170):** кнопка `#design-toggle` («Сменить дизайн (сейчас: Instrument)») была **фокусируема и озвучена как интерактивный переключатель, но мертва** — при единственном дизайне (DEC-002) клик коерсит instrument→instrument (no-op). Мёртвый контрол в tab-order/a11y-дереве на КАЖДОЙ странице (в шапке). Фикс: inline-скрипт header.html не раскрывает кнопку при `window.__design.list.length <= 1` (early-return, `class="hidden"`=display:none остаётся → вне a11y-дерева). Live-verify после reload: `design-toggle inTree:false/hidden:true`, `designInFocusOrder:false`; theme-toggle+layout-toggle остались `inTree:true` (живы); console чист. `?v`-бамп не нужен (inline-скрипт в SSR-шаблоне). Полное удаление узла+скрипта — FE-CMP-1.

**Settings (`/settings`):** landmarks (header/nav/main) ✅; heading-order H1→H2→H3 без скипов ✅; **ARIA-tabs** `role=tablist aria-label="Разделы настроек"`, 3 таба (Сессия selected / Оформление / Данные), все `aria-controls` резолвятся ✅; **0 unlabeled inputs** (все контролы с label/aria) ✅; shell `#design-toggle` скрыт (offsetParent=null) ✅. **Residual (FE-CMP-1, gradle-gated):** design-ось `data-design-pref` в панели «Оформление» ещё присутствует как мёртвая surface — вне scope A11Y-01 (это FE-CMP-1); кросс-ссылка проставлена.

**Stats (`/stats`):** landmarks ✅; heading-order H1 «Аналитика» → 7×H2 → H3 «Горячие клавиши» ✅; **canvas-графики образцово доступны** — оба `role=img` + описательный `aria-label` («Гистограмма … Полные данные — в таблице ниже») ✅; live-region фолбэки `topicProgressChartFallback`/`topicAccuracyChartFallback` (polite) + `table-sort-status` (polite) ✅; shell `#design-toggle` скрыт ✅.

**Вывод A11Y-01:** core-flow a11y — clean (автоматизируемое 100 + ручной проход landmarks/headings/ARIA/live-regions/forms/charts/keyboard). Один cross-page defect (мёртвый design-toggle) устранён. Единственный residual — settings design-ось, отнесён к FE-CMP-1.

---

## Flow / recovery (FLOW-04)

### EV-FLOW-004 — клиентские recoverable-состояния offline/ошибка сабмита (2026-07-15, R0.171, chrome-devtools :8080, network-emulation Offline)

**FLOW-04 клиентский слайс (offline/error submit recovery) — VERIFIED-CLEAN.** Статический разбор `handleSubmit` (app.js:1292–1361): две error-ветки — `!response.ok` (доменная ошибка сервера) и `catch` (сеть/offline) — обе делают recovery: `answered=false`, кнопка re-enabled (оригинальный текст восстановлен, `aria-busy` снят), `startQuestionTimer()`, actionable-алерт, возврат фокуса на кнопку `if activeElement===body` (WCAG 2.4.3).

Live-QA (emulate Offline → выбрать вариант → submit; POST не доходит до сервера → SM-2 НЕ пишется, SRS-safe):
- Кнопка: `disabled:false`, текст «Проверяю…»→«Проверить ответ» (восстановлен), `aria-busy:null` — **не тупик, повтор доступен** ✅.
- Алерт: виден, **`role="alert"`** (assertive live-region → SR озвучивает немедленно): «Сервер недоступен. Проверь соединение и повтори отправку.» — actionable ✅.
- `resultShown:false` (ложный результат не отрисован), радио активно, фокус не осиротел (на INPUT, не body) ✅.
- Console: ровно 2 ожидаемых offline-артефакта (`ERR_INTERNET_DISCONNECTED` браузерный + намеренный `console.error 'AJAX answer failed: Failed to fetch'` из catch) — не баги ✅.
- После восстановления сети + reload — чистое состояние ✅.

**Остаток FLOW-04 (BLOCKED, BLK-BOOTRUN):** серверная часть — детект истёкшей HTTP-сессии + автосейв/персистентность для resume (сейчас expired-session → доменная ошибка → тот же recoverable-алерт «вопрос устарел, обнови» — не тупик, но без dedicated resume-UX). Это backend (needsGradle, Flyway V16+ пересекается с FLOW-01 pause/resume).

---

## Cleanup / полная замена (FE-CMP-1)

### EV-CMP-001 — удаление мёртвого мульти-дизайн-движка (2026-07-15, R0.173)

**FE-CMP-1 — DONE.** Собранный вопрос отвечен пользователем (DEC-003=B, DEC-006=A, DEC-007 BLK-BOOTRUN снят «Отключил его»). Первый P0-тик разблокированной gradle-волны «полной замены»: Instrument остаётся ЕДИНСТВЕННЫМ дизайном (DEC-002), но остатки мульти-дизайн-инфраструктуры вырезаны.

**Удалено (по файлам):**
- **settings.html** — ось «Дизайн» (`#design-pref-control`, 11 кнопок `data-design-pref` editorial…instrument): вводящий в заблуждение UX, где 10 значений коерсились обратно в instrument.
- **fragments/header.html** — `#design-toggle` (кнопка `ed-design-toggle` + `<use href="#i-shapes">`) + весь его inline-скрипт (циклер `window.__design.list`). Остались theme + layout тогглы.
- **fragments/icons.html** — orphan-символ `<symbol id="i-shapes">` (единственная ссылка была в удалённом toggle → иначе красный `iconSpriteHasNoOrphanSymbols`).
- **fragments/head.html** — `DESIGNS`, `readDesign`, `setDesign`, `window.__design`; `THEME_COLORS` 11 дизайнов → 1 (`{light,dark}` instrument); `metaColor` фолбэк editorial→instrument.
- **static/js/app.js** — `wireSegControl('design-pref-control', …, window.__design.*)` (обвязка удалённого seg-control).
- **static/css/base.css** — SIGNATURES-блоки `html[data-design="editorial|linear|swiss|stripe|claude"]` + `#design-pref-control` CSS. **Визуально-инертно:** блоки скоуплены под `data-design="X"`, а SSR всегда рендерит `data-design="instrument"` → они не матчились ни на одном пути. Instrument-SIGNATURES + нейтральный `html[data-design]` слой сохранены.
- **static/css/tokens.css** — `:root` шрифты `'Lora'`/`'Inter'` → `'Newsreader'`/`'Space Grotesk'` (закрывает хвост PERF-02(a2): недостижимый editorial-фолбэк; `:root` теперь консистентен загружаемым 3 семействам).

**Cache-busting (закрывает CACHE-1):** `?v` бампнуты — tokens.css 64→65, base.css 97→98, app.js 65→66 (во ВСЕХ 3 шаблонах focus/result/settings). Устраняет дрейф «served ≠ cached» после cleanup-1 (tokens.css 1537→467 строк без бампа).

**Тесты / гейты:**
- `TemplateFragmentContractTest` — метод `tokensCssDefinesDefaultDesignAndSwitchableRoster` (ассертил роестр linear/swiss/notion/mintlify/broadsheet — **был красный** после cleanup-1, не мог прогнаться при живом bootRun) переписан → `tokensCssDefinesSingleInstrumentDesign`: ассертит `:root` + instrument light/dark + `doesNotContain` 10 удалённых дизайнов (guard против реинтродукции). **`./gradlew :quiz-app:test --tests TemplateFragmentContractTest` → BUILD SUCCESSFUL**, все 20 методов зелёные (orphan-icon без i-shapes, versioned-assets app.js=v66, head `setAttribute('data-design')` сохранён).
- `scripts/design-token-audit.py` → **VERDICT: CLEAN** (Designs=['instrument'], 46 пар, hard-fails=0, completeness=0, large-warn=0).
- Orphan-grep: 0 функциональных ссылок на `i-shapes`/`design-pref-control`/`window.__design`/`designchange` (только объясняющие комментарии).

**Прогон безопасен:** bootRun остановлен пользователем (:8080 curl→000) → `./gradlew test` не wedge. `processResources` пересинхронил `build/resources/main`.

**Deferred:** live-QA рендера `/settings` (вкладка «Оформление» без design-строки) + header (без design-toggle) — консолидированно в конце gradle-волны, когда bootRun вернётся (держим off на всю волну, иначе следующий gradle-тик wedge).

---

## Flow (backend-верифицируемые слайсы, bootRun-OFF волна)

### EV-FLOW-002 — серверная идемпотентность сабмита ответа (2026-07-15, R0.174, `:quiz-app:test` Testcontainers PG 16-alpine)

**FLOW-02 — DONE.** Второй тик разблокированной волны; чисто бэкенд, без визуальных изменений (live-QA неприменима). Защита от двойного/устаревшего POST ответа реализована на СЕРВЕРЕ в едином choke-point.

**Проблема:** до сих пор защита от двойного сабмита была только клиентской (`aria-disabled` + submit-once в app.js). Прямой повторный POST (двойной клик мимо клиентского guard, refresh-resubmit страницы `/answer`, network-retry) писал SM-2 дважды и двигал `index`/счёт дважды. `registerAnswer` слепо делает `index++` без сверки questionId.

**Решение (единый choke-point):** оба контроллера (MVC `InterviewFlowMvcService` + API `AnswerApiService`) сходятся в `InterviewSessionSupport.processAnswer`. Добавлен `isStaleDuplicate`-guard ПЕРЕД `submitAnswer`:
- **stale-условие:** `interviewSession != null && !isFinished() && currentQuestionId() != submission.questionId()` — активная сессия уже ушла ВПЕРЁД от отправленного вопроса.
- при stale → read-only повтор через новый `InterviewService.evaluateAnswer(questionId, optionId)` (+ `ReviewService.currentState` — отдаёт УЖЕ сохранённое состояние повторений, не применяя SM-2 заново). Возвращается тот же вердикт/варианты в идентичном `AnswerContext` → downstream рендер (MVC-шаблон / API-JSON) не меняется. **БЕЗ** второй записи SM-2, **БЕЗ** `registerAnswer`/index++/счёта, **БЕЗ** `AnswerEvent`.
- сверка идёт с ТЕКУЩИМ вопросом (`currentQuestionId()`), а НЕ «был ли когда-либо отвечен» → легитимный повтор того же questionId в EXAM (penalty-requeue возвращает вопрос в конец) НЕ блокируется.
- `TRAINING` (session=null) и finished-сессия → прежний путь `submitAnswer` (finished-контракт `processAnswerDoesNotMutateOrPersistWhenSessionAlreadyFinished` сохранён: submitAnswer вызывается, applySessionProgress раньше выходил по isFinished).

**Новые read-only методы (без побочных эффектов):** `ReviewService.currentState(questionId)` (`@Transactional(readOnly=true)`, findByQuestionId ∥ initialState), `InterviewService.evaluateAnswer(questionId, optionId)` (`@Transactional(readOnly=true)`, тот же `resolveQuestionAndOptions` + currentState, DisplayMode FULL).

**Тест-нюанс (задокументирован):** MockitoExtension отдаёт `0L` (а НЕ `null`) для незастабанного метода с типом-обёрткой `Long currentQuestionId()` (`Primitives.isPrimitiveOrWrapper` → defaultValue). Поэтому в существующих EXAM/STUDY-тестах `0L != submitted` ложно триггерил guard → добавлен стаб `currentQuestionId()=submitted` (репрезентует легитимный не-stale ответ: в момент сабмита текущий вопрос == отправленному). В проде метод возвращает реальный ID/null — расхождение чисто тестовое.

**Verify (bootRun OFF → gradle безопасен, Docker UP):**
- Новый `processAnswerReplaysWithoutSideEffectsOnStaleDuplicate`: stale-дубль → `evaluateAnswer` вызван, `submitAnswer`/`registerAnswer`/`addExamPenaltyQuestions`/`setInterviewSession` — `never()`, результат = replay ✅.
- `InterviewSessionSupportTest` + `ReviewServiceTest` + `InterviewServiceTest` зелёные; затем **полный `./gradlew :quiz-app:test` → BUILD SUCCESSFUL** (choke-point общий MVC+API → прогнал весь модуль на регрессии, 0 упавших).

**Residual (вне scope, честно):**
- Полный **PRG** (Post-Redirect-Get) — belt-and-suspenders поверх идемпотентности; текущий MVC рендерит результат напрямую (живой no-JS фолбэк `/answer`), редирект затронул бы этот контракт + требует live-QA → отложено, substantive harm (двойной SM-2/index) уже устранён guard'ом.
- Строгая **concurrent-thread** гонка на ОДНОЙ HTTP-сессии (TOCTOU между guard-чтением и registerAnswer) — реальные вектора двойного сабмита последовательны (одна вкладка сериализует свои запросы; session-cookie per-tab), `registerAnswer`/`currentQuestionId` уже `synchronized` → вне scope.

### EV-FLOW-001A — pause/resume backend-ядро (2026-07-15, R0.175, `:quiz-persistence:test` + `:quiz-app:test` Testcontainers PG 16-alpine)

**FLOW-01a — DONE** (FLOW-01 разбит: 01a backend-ядро — этот тик; 01b UI-баннер/кнопка — визуальная фаза). Третий бэкенд-слайс bootRun-OFF волны. Приостановка сессии тренировки с персистентностью в БД (переживает перезапуск приложения, в отличие от in-memory HTTP-сессии).

**Формат персистентности (решение):** single-user приложение (нет user-колонок в daily_activity/user_topic_stats) → `paused_session` = singleton-строка `id=1` (CHECK). `session_blob BYTEA` = Java-сериализованный `InterviewSession` — ТОТ ЖЕ механизм, которым объект уже персистится в HTTP-сессию (Serializable, serialVersionUID=1) → **доменная модель не тронута** (наименьший риск vs reconstruct-from-columns, который потребовал бы rehydration-конструктор + JSON questionIds/answerHistory). Денормализованные колонки mode/topic/total/answered/paused_at — для баннера «продолжить?» без десериализации блоба.

**Слои:**
- **V16__paused_session.sql** — таблица singleton-blob + метаданные.
- **quiz-domain** `PausedSessionInfo(mode, topic, total, answered, pausedAt)` — метаданные для UI (без блоба).
- **quiz-persistence** `PausedSessionRepository` (JdbcTemplate; `save` UPDATE-first-then-INSERT — как DailyActivity, т.к. ON CONFLICT ломается в Testcontainers PG16; `findBlob`/`findInfo`/`exists`/`clear`). + `paused_session` добавлена в TRUNCATE `AbstractPostgresRepositoryTest` (иначе течь между тестами).
- **quiz-app** `PauseService` (service.flow; inject Clock как ReviewService): `pause` (serialize→save; игнорит null/finished — паузить нечего), `resume` (findBlob→deserialize→**clear**, одноразовое потребление; битый/несовместимый блоб → deserialize вернёт null, блоб всё равно discard), `pausedInfo`, `discard`. Сериализация guarded (ObjectOutputStream/ObjectInputStream, catch → warn+null).
- **endpoints** `POST /pause` (persist активную не-finished + clearHttpSession, redirect focus) + `POST /resume` (restore в HttpSession + redirect focus) в `InterviewMvcController` → `InterviewFlowMvcService` (+ поле PauseService; прямой тест-ctor обновлён).

**Verify (bootRun OFF → gradle безопасен, Docker UP):**
- `PausedSessionRepositoryTest` (Testcontainers): empty-когда-нет-паузы; round-trip blob byte-точный; findInfo метаданные; null-topic; **singleton-overwrite** (вторая save заменяет — ровно 1 строка, COUNT=1); clear ✅.
- `PauseServiceTest` (Mockito, fixed Clock): pause сериализует + save(blob, EXAM, "java", total=3, answered=1, NOW); **round-trip** — перехваченный из pause блоб скормлен в resume → восстановленная сессия равна по mode/topic/total/index/correct/answerHistory + `clear()` вызван; corrupt-блоб → empty + `clear()`; guard null/finished → save `never()`; pausedInfo/discard делегируют ✅.
- 4 теста pause/resume в `InterviewFlowMvcServiceTest`: pause активной → pauseService.pause + clearHttpSession + focusRedirect; pause без сессии → no-op; resume present → setInterviewSession; resume empty → HttpSession не тронут ✅.
- **Полный `:quiz-persistence:test` + `:quiz-app:test` → BUILD SUCCESSFUL** (общий MVC-сервис + изменённый TRUNCATE → прогнал оба модуля; ArchUnit LayeredArchitectureTest зелёный — PauseService в service-слое не зависит от контроллеров).

**Deferred → 01b (визуальная фаза, live-QA):** UI-баннер «есть незавершённая сессия — продолжить?» на `/` (читает PauseService.pausedInfo через model-attr) + кнопка «Пауза» рядом с «Завершить» в focus-training.html (form POST /pause с _csrf) + resume-триггер. Endpoints уже протестированы — 01b лишь тонкая обвязка + live browser QA.

**Residual (задокументировано):** pause перезаписывает singleton; resume потребляет; finish/start НЕ авто-дискардят паузу (лингер приемлем для single-user, UX уточнить в 01b). Blob-персистентность зависит от serialVersionUID — при эволюции InterviewSession старый блоб не десериализуется, но guarded (discard, не падение); для короткоживущей паузы локального инструмента приемлемо.

---

### EV-FLOW-003A — UNKNOWN («не знаю») backend-ядро (2026-07-15, R0.176, `:quiz-domain:test` + `:quiz-app:test`)

**FLOW-03a — DONE** (FLOW-03 разбит: 03a backend-исход UNKNOWN — этот тик; 03b UI-кнопка + summary-рендер — визуальная фаза). Четвёртый бэкенд-слайс bootRun-OFF волны, чистая доменная/сервисная логика (никаких миграций/шаблонов) → полностью Testcontainers/Mockito-верифицируемо без браузера.

**Педагогическая модель:** «не знаю» — провал припоминания *сильнее* неверного выбора (полный blackout vs угадал-и-промахнулся). SM-2 грейд **0** (WRONG=2) → штраф ease больше; лапс (repetitions=0, interval=1) как у wrong, но `last_result=UNKNOWN` разведён для сессии/аналитики. На уровне карточки засчитывается `wrongCount++` (мастерство темы падает), но на уровне сессии — отдельный счётчик, не смешивается с wrong.

**Слои (только JSON… нет — только Java, миграций нет):**
- **quiz-domain** `ReviewResult.UNKNOWN` — новый enum-констант между WRONG и RESET; `fromString` допустимые-значения обновлены. `SpacedRepetitionService.applyUnknown(ReviewState)` — `GRADE_UNKNOWN=0`, лапс + ease += computeEaseDelta(0) с полом MIN_EASE_FACTOR, `nextReviewAt` через Clock, wrongCount++. `InterviewSession` — аддитивный `int unknown`-счётчик + `getUnknown()` + `registerUnknown(topic)` (unknown++, index++; **НЕ пишет в answerHistory** — форма `AnswerRecord(questionId,correct,topic)` неизменна → serialization-safe для paused_session-блоба FLOW-01a, и unknown-вопросы не всплывают в разбор ошибок как ложные mistakes). `SessionSummary` — поле/геттер/builder `unknownCount` + валидация ≥0.
- **quiz-app** `ReviewService.applyUnknown(Question)` (@Transactional: findOrInitial → applyUnknown → update + userTopicStats.recordAnswer(topic,**false**)). `InterviewService.submitUnknown(questionId)→Question` (resolve или QuestionNotFoundException → reviewService.applyUnknown → publish `AnswerEvent` как незнание: correct=false, selected/correct-тексты пусты, answerMarkdown/topic из вопроса; guarded warn). `InterviewSessionSupport.processSkip(questionId, session)` — идемпотентный choke-point: null/finished/`currentQuestionId≠questionId` → **no-op** (без побочных эффектов, как FLOW-02 stale-guard); иначе submitUnknown + registerUnknown; EXAM добавляет штрафные (незнание = wrong для прогрессии, нельзя «проскипать» штраф); STUDY → LEARN-фаза; persist. `InterviewFlowMvcService.skip` → focusRedirect. `SessionSummaryService.buildSummary` — total += unknown, `.unknownCount(session.getUnknown())` → accuracy = correct/total естественно учитывает unknown как не-correct.
- **endpoint** `POST /skip` (`@RequestParam @Positive long questionId`) в `InterviewMvcController`. Выбран **отдельный** endpoint вместо sentinel-optionId в `/answer` → `/answer` сохраняет `@Positive`-контракт optionId, разметка чище.

**Verify (bootRun OFF, Docker UP → gradle безопасен):**
- `SpacedRepetitionServiceTest`: applyUnknown лапсит+метит UNKNOWN (wrongCount++, correctCount сохранён); ease-штраф **строго больше** чем applyAnswer(false); ease не проваливается ниже пола 1.3 ✅.
- `ReviewServiceTest`: applyUnknown update + `recordAnswer(topic,false)` ✅.
- `InterviewServiceTest`: submitUnknown happy (returns question, applyUnknown, publishEvent) + not-found → QuestionNotFoundException, applyUnknown `never()` ✅.
- `InterviewSessionTest` (domain): registerUnknown → unknown=1, correct/wrong=0, index=1, `answerHistory` пуст ✅.
- `InterviewSessionSupportTest`: processSkip EXAM → submitUnknown+registerUnknown+addExamPenalty+persist; STUDY → switchToLearnPhase, penalty `never()`; stale (currentQuestionId≠questionId) → всё `never()`; finished → всё `never()` ✅.
- `SessionSummaryServiceTest`: 1 correct + 1 unknown → correct=1/wrong=0/unknown=1/total=2/accuracy=50.0/mistakes пуст ✅.
- `InterviewFlowMvcServiceTest`: skip делегирует processSkip + focusRedirect ✅.
- **Полный `:quiz-domain:test` + `:quiz-app:test` → BUILD SUCCESSFUL** (ArchUnit зелёный).

**NOT в 03a (осознанно):** пер-топик атрибуция unknowns (unknown не в answerHistory → нет ветки в collectTopicResults; headline-счётчик достаточен для 03a); клиентская обвязка. **Deferred → 03b (визуальная фаза):** кнопка «Не знаю» в focus-training.html (form POST /skip questionId+_csrf) + рендер unknownCount в session-summary.html + live-QA.

---

### EV-FLOW-SUMMARY-A — actionable summary backend (dueCount + typed nextActions) (2026-07-15, R0.177, `:quiz-domain:test` + `:quiz-app:test`)

**FLOW-SUMMARY-a — DONE** (FLOW-SUMMARY разбит: SUMMARY-a backend-поля — этот тик; SUMMARY-b UI-рендер — визуальная фаза). Пятый бэкенд-слайс bootRun-OFF волны. Итоги сессии получают машиночитаемый «короткий план» вместо только свободных строк.

**Ключевое наблюдение:** `MvcModelAttributeMapper.applySessionSummary` кладёт **весь** объект `summary` в модель (`model.addAttribute("summary", summary)`) → любые новые поля `SessionSummary` доступны шаблону автоматически. Значит бэкенд-слайс = поля + сервисная логика + тесты; сам рендер (кнопки/headline) = SUMMARY-b (визуальная фаза). Mapper не тронут.

**Слои:**
- **quiz-domain** `NextActionType` (enum: REVIEW_MISTAKES / PRACTICE_WEAK_TOPIC / REVIEW_DUE / KEEP_GOING). `SessionSummary`: новые поля `int dueCount` + `List<NextAction> nextActions`; nested `NextAction(NextActionType type, String label, String target, int count)` (Serializable, requireNonNull type/label); геттеры + builder (`dueCount`, `addNextAction`) + валидация `dueCount ≥ 0`.
- **quiz-app** `SessionSummaryService` (+ inject `QuestionStatsRepository` + `Clock`): `countDue(session)` = `QuestionStatsRepository.countDue(topic/important/onlyWrong-скоуп сессии, now)` с guard (`RuntimeException` → warn + 0, итоги не должны падать из-за счётчика). `addNextActions` строит типизированный план в приоритетном порядке: есть ошибки → REVIEW_MISTAKES(count=mistakes); каждая слабая тема (accuracy < WEAK_TOPIC_THRESHOLD & total>0) → PRACTICE_WEAK_TOPIC(target=topic, count=wrong); dueCount>0 → REVIEW_DUE(count=dueCount); если нет ошибок + anyAnswered + allGood → KEEP_GOING. Строки-`recommendations` **сохранены** (текущий шаблон читает их) — `nextActions` идут параллельно из тех же источников; `shortTopic`-хелпер вынесен и переиспользован (дедуп с рекомендациями).

**Verify (bootRun OFF, Docker UP):**
- `SessionSummaryServiceTest` (ручная сборка сервиса с fixed Clock + mock repos вместо @InjectMocks): mixed → dueCount=7 из countDue + nextActions `[REVIEW_MISTAKES(count=1), PRACTICE_WEAK_TOPIC(target=spring), REVIEW_DUE(count=7)]` в точном порядке; all-correct → `[KEEP_GOING]` + dueCount=0 (countDue не застабан→0); countDue бросает RuntimeException → dueCount=0, без REVIEW_DUE (guard); существующие тесты (unknownCount, mistakes, empty-session vacuous-truth guard) зелёные без изменений.
- **Полный `:quiz-domain:test` + `:quiz-app:test` → BUILD SUCCESSFUL** (Spring-контекст-тесты ок: Clock + QuestionStatsRepository — существующие бины InfrastructureConfig; ArchUnit зелёный).

**NOT в SUMMARY-a (осознанно):** двойная поддержка recommendations(строки)+nextActions(типы) — намеренно, т.к. шаблонный своп на nextActions = SUMMARY-b; drift-риск снят единым источником (mistakes/topicResults). dueCount по topic-скоупу сессии (для group-сессий topic=null → глобальный due в important/onlyWrong-скоупе — приемлемо). **Deferred → SUMMARY-b (визуальная фаза):** рендер nextActions как actionable-кнопок (ссылки на /training?onlyWrong / ?topic=X / ?mode=review) + headline «{accuracy}% · {N} требуют разбора» + live-QA.

---

### EV-FLOW-REPORT-A — «Сообщить о проблеме» backend (2026-07-15, R0.178, `:quiz-domain` + `:quiz-persistence` + `:quiz-app` test)

**FLOW-REPORT-a — DONE** (FLOW-REPORT разбит: REPORT-a backend — этот тик; REPORT-b UI-триггер/модалка — визуальная фаза). Шестой бэкенд-слайс bootRun-OFF волны. Полный вертикальный срез приёма жалоб на вопросы, чистый Testcontainers/Mockito (UI-триггер = REPORT-b).

**Слои:**
- **V17__question_issue.sql** — таблица жалоб: `id BIGSERIAL PK`, `question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE` (репорт бессмыслен без вопроса), `category`/`status TEXT`, `comment` nullable, `created_at TIMESTAMP`; индексы `idx_question_issue_question` (репорты вопроса) + `idx_question_issue_status` (сколько OPEN на разбор).
- **quiz-domain** `QuestionIssue` record (id/questionId/category/comment/status/createdAt); `QuestionIssueCategory` enum (INCORRECT_ANSWER/TYPO/UNCLEAR/OUTDATED/OTHER, `fromString` ленивый: null/пусто/неизвестное → OTHER, чтобы репорт не терялся при рассинхроне фронт/бэкенд); `QuestionIssueStatus` enum (OPEN/RESOLVED/DISMISSED, при создании всегда OPEN — смена вручную, single-user).
- **quiz-persistence** `QuestionIssueRepository` (JdbcTemplate): `save` через `INSERT … RETURNING id` (проверено — работает в Testcontainers PG16, в отличие от ON CONFLICT); `findByQuestionId` newest-first; `countByStatus`. + `question_issue` добавлена в TRUNCATE `AbstractPostgresRepositoryTest`.
- **quiz-app** `QuestionIssueService` (feature/interview/service/report; inject repo + QuestionRepository + Clock): валидирует существование вопроса (иначе `QuestionNotFoundException`), нормализует blank-comment → null, `created_at` через Clock, статус OPEN. `IssueReportApiService` (usecase): парсит category через `fromString`, оборачивает `ReportIssueResponse`. `POST /api/report` (`@Valid @ModelAttribute ReportIssueRequest`: `@Positive questionId` + category-строка + `@Size(max=2000) comment`) в `InterviewApiController` — тот же security-паттерн, что у существующих POST /api/* (favorite/confidence), доп. конфиг не нужен.
- **DTO** `ReportIssueRequest` (класс с валидацией, category как строка — парсится лениво на бэкенде) + `ReportIssueResponse` (record: success/issueId/questionId/category/status).

**Verify (bootRun OFF, Docker UP):**
- `QuestionIssueRepositoryTest` (Testcontainers, FK на seeded question id=1): save возвращает положительный id + round-trip (category/comment/status=OPEN/createdAt точно); null-comment; **newest-first** порядок findByQuestionId; countByStatus считает только совпадающий статус (OPEN=2, RESOLVED=0); empty для неизвестного вопроса ✅.
- `QuestionIssueServiceTest` (Mockito, fixed Clock): report сохраняет + возвращает OPEN-запись с trimmed-comment + `save(…, clock.instant())`; blank-comment → null; вопрос отсутствует → `QuestionNotFoundException`, `save` never() ✅.
- `IssueReportApiServiceTest` (Mockito): "outdated" → OUTDATED + 200/success/поля; "gibberish" → OTHER (ленивый fromString) ✅.
- `InterviewApiControllerUnitTest` ctor обновлён (+IssueReportApiService). **Полный `:quiz-domain:test` + `:quiz-persistence:test` + `:quiz-app:test` → BUILD SUCCESSFUL** (Spring-контекст ок: новые бины QuestionIssueRepository/Service + usecase; V17 применяется Flyway в интеграционных тестах; ArchUnit зелёный — service/usecase не зависят от контроллеров).

**NOT в REPORT-a (осознанно):** админ-просмотр/смена статуса жалоб (countByStatus/findByQuestionId уже есть как фундамент; UI-разбор вне scope тренажёра); rate-limiting репортов (single-user). **Deferred → REPORT-b (визуальная фаза):** кнопка «Сообщить о проблеме» на focus-training.html + result.html → модалка выбора категории + опц. комментарий → `fetch POST /api/report` с `_csrf`; toast-подтверждение; live-QA.

---

### EV-PERF-003 — app.js single-sourced (cache-busting без дрейфа) (2026-07-15, R0.179, `:quiz-app:test`)

**PERF-01 — DONE.** Седьмой бэкенд/статик-слайс bootRun-OFF волны. Cache-busting-версия `app.js` сведена в единственную точку правки — устранён латентный drift-риск (v=N вручную синхронизировался в 3 шаблонах).

**Проблема:** `<script th:src="@{/js/app.js(v=66)}">` дублировался инлайном в `focus-training.html`, `result.html`, `settings.html`. При правке app.js версию нужно было бампать в 3 местах; забыть одно → часть страниц отдаёт устаревший кэшированный JS (тот же класс дефекта, что ловит `versionedAssetsAreConsistentAcrossTemplates`, но там ассерт срабатывал бы уже ПОСЛЕ рассинхрона). CSS уже был single-sourced в `head.html`; app.js — нет.

**Решение:** создан фрагмент `fragments/scripts.html` с `<script th:fragment="app-script" th:src="@{/js/app.js(v=66)}">` — единственное место, где живёт версия. Три инлайн-тега заменены на `<script th:replace="~{fragments/scripts :: app-script}">` (та же конвенция, что у `fragments/mermaid-init :: mermaid-init`). Дрейф версии теперь **структурно невозможен** — второй точки правки нет.

**Verify (bootRun OFF, Docker UP → gradle безопасен):**
- Новый ассерт `TemplateFragmentContractTest.appJsIsSingleSourcedInFragment`: тег `/js/app.js(v=N)` встречается в дереве шаблонов ровно в 1 файле, и это `fragments/scripts.html`. Инлайн-возврат тега в любую страницу → ассерт красный (ловит регресс).
- `versionedAssetsAreConsistentAcrossTemplates` по-прежнему зелёный (app.js в 1 файле = 1 версия; санити `containsKey("app.js")` держится — фрагмент под `templates/`).
- **Рендер end-to-end:** `PublicEndpointsSmokeTest` рендерит `/` (focus-training) и `/settings` → 200 text/html → `th:replace` резолвится в реальном Thymeleaf-контексте (contract-test читает сырой HTML и битую ссылку не поймал бы — smoke закрывает этот зазор; `result.html` транзитивно покрыт идентичной ссылкой).
- Оба класса → **BUILD SUCCESSFUL**.

**Residual (осознанно):** истинный content-fingerprint (авто-хэш вместо ручного v=N) — вне scope PERF-01; ручной v=N в одном месте закрывает drift-риск при минимальном изменении. Отдельные v=N у `tokens.css`/`base.css` в `head.html` остаются независимыми (разные файлы, равенство не требуется). REPORT-b/остальные UI-слайсы, которым понадобится бамп app.js, правят его теперь в `fragments/scripts.html`.

---

### EV-QA-004 — детерминированный профиль `qa` (фикс. Clock + фикстуры due/learned/new) (2026-07-15, R0.180, `:quiz-app:test`)

**QA-01 — DONE.** Восьмой бэкенд-слайс bootRun-OFF волны и **разблокировщик визуальной фазы**: даёт воспроизводимое окружение, чтобы live-QA (6 брейкпойнтов × 2 темы) сверялась с одинаковым состоянием от прогона к прогону. Полностью gradle-верифицируемо (профиль поднимается в `@SpringBootTest`), нулевой blind-visual риск.

**Проблема:** browser-QA визуальной фазы бессмысленно сверять пиксели/счётчики, если «сегодня», набор «к повторению» и статистика плавают между запусками. До этого профилей было только `prod`/`test`; Clock = `systemUTC()` (недетерминирован).

**Решение (3 файла + 1 строка):**
- **`QaConfig`** (`@Profile("qa")`): бин `qaClock` `@Primary` = `Clock.fixed(app.qa.fixed-instant, UTC)` (дефолт `2026-07-15T12:00:00Z`). Перекрывает `InfrastructureConfig.clock()` по типу через `@Primary` (имя бина иное — `qaClock` vs `clock` — чтобы не поймать `BeanDefinitionOverrideException`: overriding отключён). В prod/dev не грузится.
- **`QaFixtureRunner`** (`@Profile("qa")` `@Order(LOWEST_PRECEDENCE)`): `ApplicationRunner`, идёт ПОСЛЕ `StartupRunner` (которому добавлен `@Order(HIGHEST_PRECEDENCE)` — единственный раннер до сих пор, порядок сохранён). Владеет состоянием прогресса: `TRUNCATE review_state, daily_activity, user_topic_stats RESTART IDENTITY`, затем сеет первые `app.qa.due-fixtures` (=2) вопросов по id как **due** (`next_review_at = now-1d`, `last_result=WRONG`, rep=1) + следующие `app.qa.learned-fixtures` (=1) как **learned** (`now+10d`, `CORRECT`, rep=3), остальные остаются **new** (без строки). Все метки — из фиксированного Clock → набор идентичен между прогонами.
- **`application-qa.yml`**: `app.qa.{fixed-instant,due-fixtures,learned-fixtures}` + `preload.startupPreload/fullWarmup=false` (быстрый предсказуемый старт). Datasource наследуется из базового `application.yml` (PostgreSQL :5432).
- Активация: `SPRING_PROFILES_ACTIVE=qa ./gradlew bootRun`.

**Verify (bootRun OFF, Docker UP → gradle безопасен):**
- `QaProfileFixtureIntegrationTest` (`@ActiveProfiles({"test","qa"})` — TC datasource + qa Clock/фикстуры): `clock.instant()` == `2026-07-15T12:00Z` (@Primary qaClock реально перекрыл systemUTC); `COUNT(review_state WHERE next_review_at<=now)` == 2, `>now` == 1, new = total-3; `daily_activity`/`user_topic_stats` пусты ✅.
- Регрессии: `NoAiModeStartupIntegrationTest` (StartupRunner `@Order` не сломал старт), `PublicEndpointsSmokeTest` (non-qa — дефолтный Clock без ambiguity), `LayeredArchitectureTest` (QaConfig в config-слое, QaFixtureRunner в infrastructure) — все зелёные.

**Residual (осознанно):** «известная сессия/юзер» из acceptance — приложение single-user без user-колонок и без серверного стора сессий (HTTP-session in-memory), поэтому «юзер» уже единичен; детерминизм сессии обеспечивается фиксированным Clock + чистыми прогресс-таблицами (сессия стартует с предсказуемого нуля). Реальный корпус вопросов под `qa` = полный импорт (первые N по id детерминированы; при желании абсолютных id 1..N — включить `app.interview-reset-on-startup=true`, но это тяжёлый реимпорт каждого старта, вне scope). Фикстуры — минимальный репрезентативный набор; счётчики настраиваются через `app.qa.*` без изменения кода.

---

### EV-UX-005 — серверная плюрализация серии (единый util, устранён баг + дубль) (2026-07-15, R0.181, `:quiz-domain:test` + `:quiz-app:test`)

**UX-05 — DONE.** Девятый и **последний** бэкенд-слайс bootRun-OFF волны. Русское склонение «день/дня/дней» вынесено на сервер в одну каноничную реализацию; исправлен реальный баг клиентской плюрализации.

**Проблема:** в `app.js` было ДВА расходящихся склонятеля — наивный `daysText` (`n===1?день:(n>=2&&n<=4?дня:дней)`) для стрика и корректный `pluralRu` (mod10/mod100) для штрафных вопросов. Наивный **врал** на числах 11–14 и оканчивающихся на 1/2–4 в этом диапазоне сотен: streak=21 → «21 дней» (надо «день»), 22 → «22 дней» (надо «дня»). Дубль логики + баг.

**Решение (server-side, как требует заголовок задачи):**
- **`RussianPlural`** (новый, `quiz-domain`, чистый util как `ReviewDefaults`): `pluralize(long n, one, few, many)` — каноничное правило (11–14 по mod100 → many; oканчивается на 1 → one; 2–4 → few; иначе many; знак игнорируется) + `days(long n)` → «N день/дня/дней». Reusable для любых числительных.
- **`StreakResponse.streakLabel`** (новое поле record): просклонённый лейбл, считает `DailyStreakService` через `RussianPlural.days(streak)` в обеих ветках (`today==null` → «0 дней»).
- **`app.js`**: `daysText(d)` теперь возвращает `d.streakLabel` (defensive-фолбэк на корректный `pluralRu`, если поле отсутствует — старый кэш ответа); наивная клиентская плюрализация удалена. `app.js` v=66→67 (bump в ЕДИНОЙ точке `fragments/scripts.html` — плод PERF-01).

**Verify (bootRun OFF, Docker UP):**
- `RussianPluralTest` (24 параметризованных кейса): one (1/21/31/101), few (2–4/22–24/102), many (0/5/10/20/25/100), спец-диапазон 11–14/111–114 → many несмотря на mod10; `pluralize` на произвольных словах; отрицательные → по модулю ✅.
- `DailyStreakServiceTest`: `streakLabel` == «0 дней» (нет активности) и «7 дней» (streak=7) — серверное вычисление end-to-end ✅.
- `StreakApiServiceTest` + `InterviewApiControllerUnitTest` — конструкторы `StreakResponse` обновлены (6-й арг) ✅.
- `TemplateFragmentContractTest` (app.js v=67 всё ещё в 1 файле = single-sourced) + `PublicEndpointsSmokeTest` (стрик-бар разметка не тронута, `/` + `/settings` → 200) зелёные.

**Residual (осознанно):** `stats.js` собственной плюрализации дней не имел (греп 0) — «2 JS» из формулировки на деле = два склонятеля ВНУТРИ `app.js`; оба класса теперь сходятся (стрик → сервер, штрафные вопросы → `pluralRu`, который можно позже тоже вынести на сервер при желании — вне scope UX-05). Рендер серверной строки в DOM — тривиальный (`textContent = d.streakLabel`), визуально идентичен для частых значений и КОРРЕКТНЕЕ на краях; консолидированная live-QA визуальной фазы подтвердит текст на экране.

**⚑ Бэкенд-фаза (bootRun-OFF волна) ЗАКРЫТА** — все 9 чистых gradle-верифицируемых слайсов сделаны (FLOW-02/01a/03a/SUMMARY-a/REPORT-a, PERF-01, QA-01, UX-05). Следующий этап — визуальная фаза (bootRun ON + профиль qa): PROC-07 → UX-13 → UI-слайсы FLOW-* → PERF-02 → консолидированная live-QA → final gate → FINALIZED.
