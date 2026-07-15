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
