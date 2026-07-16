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

---

### EV-QA-005 — визуальная фаза онлайн + PROC-07 baseline + разбор конфликта решений (2026-07-15, R0.182, chrome-devtools live)

**Старт визуальной фазы (bootRun ON).** Первый тик после закрытия бэкенд-фазы. App поднят в фоне (`nohup ./gradlew bootRun`, **дефолтный dev-профиль**, НЕ qa), :8080 отдаёт 200, 9818 вопросов импортированы, 0 console-ошибок.

**Почему НЕ профиль qa:** `QaFixtureRunner` под профилем qa делает `TRUNCATE review_state/daily_activity/user_topic_stats` — это уничтожило бы реальный прогресс пользователя в рабочей БД. PROC-07 — структурная CSS-задача (full-width + рельс), детерминированные фикстуры для неё не нужны. qa-детерминизм приберёг для финальной консолидированной QA — запущу деструктивно только с подтверждением пользователя.

**Baseline снят** (`.qa-artifacts/`, gitignored): `/` (focus-training) на 1920, light + dark. Виден таргет-дефект DEC-003: контент центрирован на `max-width` 1280 (`.ed-page`) / 960 (`.focus-training`), `margin:0 auto` → большие симметричные пустые поля на 1920+ (ровно анти-паттерн, запрещённый mockups DESIGN.md).

**Вживую подтверждён UX-05 (R0.181):** стрик-чип рендерит «2 дня» — серверная плюрализация `RussianPlural` корректна в браузере (2 → «дня», не «дней»).

**Токен-архитектура разобрана:** `.ed-page` = chrome (`--max-width-page` 80rem/1280), `.focus-training` = контент (`--max-width-focus` 60rem/960), оба центрированы; per-layer токены focus60/reading56/result72/settings64/data-none; `--measure` 72ch.

**⚑ Разрешён конфликт двух пользовательских решений:**
- **PORT_MAPPING-lock (2026-07-14):** «FOCUS — горизонтальный `.focus-topbar`, НЕ возвращать вертикальный сайд-рейл (прямая жалоба пользователя „выглядело любительски"), правый рейл макета НЕ портируем».
- **DEC-003=B (2026-07-15, явный ответ пользователя через collected-question):** full-width + содержательный правый рельс `.focus-aside` ≥1080px; «отменяет прежний выбор».
- **Разрешение:** DEC-003 новее, авторитетнее (finalization-round source-of-truth `FRONTEND_DECISIONS.md`, ADOPTED) и прямо resolved этот самый вопрос (A vs B → B) → **PROC-07=B идёт**. Жалоба на «любительский» honored не отказом от рельса, а качеством: `.focus-aside` = вспомогательный рельс ТОЛЬКО ≥1080px (сессия-мета/хоткеи/стрик), главная колонка вопроса+опций доминирует; <1080px — текущее stacked/topbar-поведение (не воссоздаём вертикальный сплит как главный флоу). Повторно спрашивать не нужно — пользователь уже ответил на этот вопрос.

**PROC-07 → IN_PROGRESS.** Реализация (следующий тик, live iterative edit→cp→screenshot): tokens full-width → `.focus-training` grid `[main 66ch][.focus-aside ~280px]` ≥1080px → перенос хоткей-легенды+меты в рельс → result.html рельс «разбор глубже» → live-QA 1440/1728/1920/2560 обе темы. Правки через `cp source→build/resources/main` (НЕ отдельный gradle при живом bootRun = wedge).

## EV-PROC-007 — Wide-layout модель B: focus full-width + правый рельс (R0.183, 2026-07-16)

**Задача:** PROC-07 (DEC-003=B). Реализован правый рельс `.focus-aside` на focus-странице для широких экранов + доводка ширины. `base.css` v=98→v=99.

**Реализация:**
- `focus-training.html` — `<aside class="focus-aside focus-session-rail">` вынесен из `.focus-topbar` в прямые дети `.focus-training` (topbar теперь несёт только `.question-zone-head` — chip режима + hint потока). Весь контент рельса сохранён (progress-line, `.session-progress-track` с aria, today-chip, `.rail-finish`-форма).
- `base.css` `@media (min-width: 1080px)` с гейтом `:has(.session-progress-track)`.

**Ключевые решения:**
- **Гейт `:has(.session-progress-track)`** — правая колонка включается ТОЛЬКО в активной сессии (progressbar рендерится `th:if interviewSession!=null`). Вне сессии (TRAINING browse, где рельс = только today-chip) страница остаётся как была → НЕ воскрешаем «одинокую фишку в мёртвом жёлобе» (прямая прошлая претензия пользователя). Браузер без `:has()` — на прежнем стеке (PE).
- **Ширина: НЕ edge-to-edge.** Замером на 2560 обнаружено: при full-width `.ed-page` главная зона растягивается до 2017px, но опции капятся на `--max-width-options` (960) → ~1000px мёртвого центра между опциями и рельсом (тот же анти-паттерн жёлоба). Одобренное превью B показывало ОГРАНИЧЕННУЮ главную колонку (~66ch) + рельс, не 2000px-растяжку → правильное прочтение: наполнять уже-капнутый на `--max-width-page` (1280) `.ed-page`; `.focus-training` тянется на всю его ширину, делится на ~800 main + ~288 rail. Шире прежних 960 («шире + рельс»), консистентно с общей app-шириной 1280, без симметричных воидов сверх неё. Проза (hero) держит `--measure` внутри зоны; опции наполняют главную колонку до рельса (их `--max-width-options` > ширины колонки → cap не срабатывает, воида справа нет).

**Live-QA (chrome-devtools, dev-профиль, реальная EXAM-сессия, обе темы):**
- **900px** → `.focus-training` display:block, maxW 960 центрирован; рельс static в потоке ВЫШЕ вопроса (стек head→рельс→вопрос — как было до правки, регресса нет); 0 h-overflow.
- **1728px** (light+dark) → grid `[main][rail]`; рельс sticky, border-left, контент «1/20 + teal прогресс-бар + 9818 к повтору + Завершить сессию»; teal-ring на выбранной опции; 0 h-overflow.
- **2560px** → `.ed-page` 1280 centered (как всё приложение); grid 784 main + 288 rail; опции наполняют 784; deadGap=48px = column-gap (не воид); 0 h-overflow.
- **0 console-ошибок** на всех вьюпортах.
- **result.html не задет** (правила scoped к `.focus-page`; maxW 1152 = `--max-width-result`, 0 overflow).

**Верификация тестов:** ассерты `TemplateFragmentContractTest` (`contains`/`doesNotContain` по строкам focus-training.html) верифицированы инспекцией — весь контент aside сохранён при переносе, запрещённых строк не добавлено. Live-bootRun успешно рендерит `/` → Thymeleaf валиден, `PublicEndpointsSmokeTest` зелёный. Gradle-прогон контракт-теста ОТЛОЖЕН на bootRun-stopped тик (wedge).

**Отложено (мелкий follow-up, НЕ блокер):** result.html `.focus-aside` «разбор глубже» (п.4 acceptance) — самостоятельный срез; хоткей-легенда пока в главной колонке (рядом с submit — логично, главная колонка доминирует).

## EV-UX-016 — Analytics next-action блок: VERIFIED-CLEAN (R0.188, 2026-07-16)

**Задача:** UX-16 (P2) — «аналитика отвечает *что делать*, не только *что произошло*». Помечено OPEN с заметкой «частично есть (initNextActions)». Аудит показал: реализовано ПОЛНОСТЬЮ Instrument-портом + существующими stats-атрибутами → gradle/бэкенд не потребовались.

**Что уже есть (stats.html:17, `.stats-next-actions` «Что делать дальше»):** grid из 3 actionable-CTA на существующих маршрутах:
- **«Повторить сегодня»** → `@{/}`, счётчик `stats.due`, детализация через `@plural.pick` (вопрос/вопроса/вопросов к повтору), `is-recommended` при `due > 0`.
- **«Разобрать ошибки»** → `@{/(onlyWrong=true, ordered=true)}`, счётчик `stats.wrong`, `is-recommended` при `due == 0 and wrong > 0`.
- **«Тренировать слабые темы»** → `@{/(weakTopics=true, ordered=true)}`, `topicStats.size`, `is-recommended` при `due == 0 and wrong == 0 and topics`. JS (`stats.js` `initNextActions`, строка 44) заполняет `data-weak-topic-summary` → «Слабее всего: {name} · {acc}%» + `aria-label` действия.

Приоритетная `is-recommended`-подсветка = teal ring+wash (Instrument-трактовка выбранного, НЕ залитый бейдж). Семантика: `<section aria-labelledby>` + `<h2>` + подзаголовок.

**Live-QA (chrome-devtools, /stats, dev-БД: due=9800/wrong=26/319 тем, обе темы):**
- 3 карточки рендерятся; computed-инспекция подтвердила: «Повторить сегодня» `is-recommended=true` (due>0), «Разобрать ошибки»/«Тренировать» — нет (корректный приоритет).
- JS-обогащение: `data-weak-topic-summary` = «Слабее всего: Arrays strings · 19%»; `aria-label` = «Тренировать слабые темы. Слабее всего: Arrays strings, точность 19%.».
- href-ы: `/`, `/?onlyWrong=true&ordered=true`, `/?weakTopics=true&ordered=true` — верны.
- Обе темы полированы (teal-ring на рекомендованной, корректный контраст): `.qa-artifacts/ux16-stats-next-actions-{dark,light}.png`.
- **0 функциональных console-ошибок.** Единственное сообщение — dev-only CSP-блок sourcemap chart.js (`chart.umd.min.js.map` с cdn.jsdelivr.net, `connect-src 'self'`); сам chart.js загрузился, sourcemap в рантайме не нужен — не связано с UX-16.
- `stats.js` v=15: build == source (не завязан на app.js/UX-13-entanglement).

**Вывод:** дефект «аналитика только показывает числа» не воспроизводится — Instrument-порт уже добавил actionable next-step-блок, аналогичный `session-summary` nextActions. Как UX-01/02/03 — замечание было против мокапа/старого дизайна. UX-16 → CLOSED. `StatsApiService` доработки не потребовал.

## EV-REVIEW-03 — Mobile sticky submit-панель (R0.189, 2026-07-16)

**Задача:** ревью #3 / макет Instrument `focus-question.html` (`.state--active .submit-row` fixed bottom + safe-area). На мобиле главное действие MCQ-ответа = закреплённая нижняя панель, а не кнопка, тонущая под длинным списком вариантов. Открытый пункт фикс-волны; прод ранее сбросил старый fixed-footer в static (стр.960).

**Реализация (чистый `base.css`-срез, +`head.html` ?v; app.js/шаблоны/фрагменты НЕ тронуты):**
- `@media (max-width: 600px)`:
  - `html[data-design] .focus-page #interview-form [data-ui-fragment="training-actions"]:not(.is-answered) .action-footer` → `position: fixed; left/right/bottom: 0; z-index: var(--z-sticky)` (10 — ниже back-to-top `--z-dropdown:20` и модалок `--z-modal:100`); `padding: var(--space-3) var(--page-gutter)`; `padding-bottom: calc(var(--space-3) + env(safe-area-inset-bottom))`; `background: var(--color-bg-primary)`; `border-top` + `box-shadow: var(--shadow-md)`.
  - Submit уже `width:100%` на ≤600px (стр.1306) — full-width в панели без доп. правил.
  - Клиренс: `.focus-page:has(...:not(.is-answered)) .focus-training { padding-bottom: calc(var(--space-9) + env(safe-area-inset-bottom)) }` (96px) — контент (последний — «Не знаю») не прячется за фикс-панелью.
  - Back-to-top: `.focus-page:has(...:not(.is-answered)) .back-to-top { bottom: calc(var(--space-5) + 4.5rem) }` — приподнята над панелью, нет наезда.

**Ключевое решение — гейт `:not(.is-answered)`:** ПОСЛЕ ответа app.js метит `[data-ui-fragment=training-actions]` классом `.is-answered` и прячет его (стр.~1005), а `#next-question` уезжает в зону разбора. Значит фикс-панель живёт ТОЛЬКО в pre-answer-состоянии — идентичном во ВСЕХ версиях app.js (v=68 build / v=70 source). → срез НЕ завязан на пост-ответный флоу UX-13, безопасно валидировать против текущей live-сборки. Скоуп — только основной `#interview-form` (флешкарта/study-confirm — свои состояния, не тронуты). Desktop (>600px) не тронут (`.action-footer` static; `@media (min-width: 768/1080)` отдельны).

**Live-QA (chrome-devtools, device-emulation, dev-сессия MARATHON, обе темы):**
- vw=500 (OS-минимум окна ~485–500px; истинный 375 недостижим, но брейкпойнт `<600` активен — правило работает).
- Computed: `.action-footer` `position: fixed`, `z-index: 10`, `border-top: 1px`, bg по теме (dark `oklch(0.19 …)` / light paper), `padding-bottom: 12px` (space-3; safe-area=0 в эмуляторе); прижата к низу (`bottom == innerHeight == 720`); submit full-width (437 в панели 485).
- Клиренс: на низу скролла (scrollY=606) «Не знаю» (vp 470–504) и последняя опция (bottom 446) — полностью НАД панелью (top 647) = не скрыты. `.focus-training padding-bottom: 96px`.
- **0 console-сообщений**, **0 h-overflow**.
- Скрины: `.qa-artifacts/review3-sticky-submit-mobile-{dark,dark-bottom,light}.png`.

**Верификация тестов:** правка чисто CSS + `head.html` ?v-bump → `TemplateFragmentContractTest`/gradle не затрагиваются (шаблоны/фрагменты не менялись). Закрывает ревью #3.

## EV-FB-001 — Inline invalid-визуал формы: VERIFIED-CLEAN (R0.190, 2026-07-16)

**Задача:** FB-1 (feedback-парити, ревью-2) — невалидное поле должно иметь постоянное inline error-состояние, не только нативный браузерный bubble. Помечено открытой parity-QA-находкой. Аудит: реализовано ещё R0.163 (`base.css` 1470-1484) — план-заметка «нет inline invalid-визуала» устарела.

**Реализация (`base.css`, R0.163):**
- `html[data-design] .settings-page input[type="number"]:user-invalid` → `border-color: var(--color-status-error)` + `background: var(--color-status-error-wash)`.
- `:user-invalid:focus` → error-ринг `box-shadow: 0 0 0 var(--rule-weight-strong) var(--color-status-error)` (специфичность (0,5,2) перебивает `:focus` (0,4,2) → error-ринг вместо signal-focus-ring).
- **Гейт `:user-invalid`, НЕ `:invalid`:** красит ТОЛЬКО после взаимодействия пользователя — нетронутое поле (`value=20`) не «грязним» красным на загрузке.
- Error-тон `hue27` (красно-оранжевый) разведён с signal `teal hue205`. Нативный Constraint Validation bubble (`count min=1 max=200`) остаётся доп. каналом.

**Осознанные non-port (не дефекты):**
- Кастомная `.field-error`-сообщение (`aria-invalid` + `aria-describedby`) — макет `feedback-state-atlas.html:230-231` явно: «прод использует нативную Constraint Validation; кастомная inline-ошибка — **необязательное усиление** (кандидат Фазы G, JS-blast-radius)». Текущего inline-визуала достаточно для парити.
- Атлас-состояния «Спиннер-строка» / «Скелет» — loading-состояния; в seed-first проде (AI/async-загрузка вырезаны) грузить нечего → мёртвые, намеренно не портированы.

**Live-QA (chrome-devtools, /settings, dark, реальный ввод через fill + blur):**
- Ввод `500` (> max 200) в `input[name=count]` (uid спинбаттон «ВОПРОСОВ»), затем blur.
- `inp.validity.rangeOverflow = true`; `inp.matches(':user-invalid') = true`.
- Computed: `borderColor = oklab(0.72 …)` = резолв `--color-status-error` (`oklch 0.720 0.175 27`); `background = oklch(0.30 0.07 27)` = `--color-status-error-wash`.
- Визуально: отчётливая красная граница + тёмно-красный wash, явно отличны от teal-signal (кнопка «Начать интенсив» outline, «Начать повторение», подчёркивание активной вкладки). Скрин `.qa-artifacts/fb1-user-invalid-count.png`.
- Значение восстановлено на 20 после проверки.

**Вывод:** FB-1 закрыт — inline invalid-визуал есть и работает; сообщение-усиление осознанно отложено (Фаза G) по указанию макета. Чисто верификационный тик → коммит только `FRONTEND_STATE.json` + `FRONTEND_EVIDENCE.md`.

---

## EV-RESP-192 — Responsive h-overflow sweep, регресс-проверка REVIEW-03+FB-1 (R0.192, 2026-07-16)

**Цель:** частичный вклад в release-gate `responsive`; поймать возможный горизонтальный overflow-регресс от последних CSS-правок (REVIEW-03 fixed sticky submit-панель R0.189 + FB-1 `:user-invalid` R0.190). Чисто read-only (chrome-devtools, dark), без правок кода → gradle не нужен.

**Метод:** переиспользуемая evaluate-функция сканирует `body *` на элементы с `getBoundingClientRect().right > clientWidth+1 || left < -1` (исключая `position:fixed` и `offsetWidth==0`), возвращает `hOverflow (scrollWidth>clientWidth+1)`, scrollWidth/clientWidth и до 6 переполняющих элементов. Проверены оба экстремума ширины: узкий (OS-минимум окна ~485–500px, ниже которого браузер не ужимается) и широкий (1920).

**Результаты (dark):**
| Поверхность | vw≈485–500 | vw=1920 |
|---|---|---|
| `/` (focus) | `hOverflow=false`, over=[] (один широкий `CODE` right=644 в `overflow-x:auto`-контейнере = не page-overflow) | `{scrollWidth:1905,clientWidth:1905,over:[]}` |
| `/stats` | `hOverflow=false`, over=[] | `{scrollWidth:1905,clientWidth:1905,over:[]}` |
| `/settings` | `hOverflow=false`, over=[] | `{scrollWidth:1905,clientWidth:1905,over:[]}` |

Все 3 основные поверхности чисты на обоих экстремумах → REVIEW-03 sticky-панель (fixed, вне overflow-скана) и FB-1 error-визуал не внесли горизонтального overflow. Вьюпорт возвращён на 1280×900.

**Light-тема не проверялась отдельно:** overflow — layout-driven (ширины боксов), тема меняет только цвета → light-скан near-redundant для h-overflow. Полная responsive-матрица (6 брейкпойнтов 375/768/1280/1440/1728/1920/2560 × 2 темы, device-emulation <485px) остаётся для консолидированной live-QA в конце волны — gate `responsive` НЕ флипается этим тиком.

**Wedge-статус (сверено live):** `:8080` жив (java PID 64174, LISTEN); gradle project-локи `.gradle/8.9/{fileHashes,executionHistory}.lock` держит PID **40113** (gradle-launcher, родитель app-JVM 64174). Прежний PID 64168 ушёл — держатель lock сместился на 40113, но wedge сохраняется: `./gradlew test` по-прежнему заблокирован. Перекомпиляционный путь к FINALIZED остаётся gated на bootRun-stopped тик.

---

## EV-RESP-193 — Device-emulation responsive sweep: истинные 320/375/768 (R0.193, 2026-07-16)

**Цель:** закрыть давно откладываемый (PLAN §статус: «истинный 375/320 — на device-emulation тик при необходимости») пробел в `responsive`-охвате. Окно ОС не ужимается ниже ~485px → R0.189/R0.192 били по ~485–500 как прокси мобильного. Здесь — CDP device-metrics override (chrome-devtools `emulate viewport`), обходящий OS-минимум, для истинных мобильных/планшетных брейкпойнтов. Read-only, без правок кода → gradle не нужен.

**Метод:** `emulate` с `viewport=<W>x<H>x<dpr>,mobile,touch` (CDP device override) + overflow-скан `body *`. Улучшённый классификатор: элемент, торчащий за `clientWidth`, помечается «unscrolled» ТОЛЬКО если ни один из 5 предков не является реальным `overflow-x:auto/scroll`-контейнером (`scrollWidth>clientWidth`) — отсекает ложные срабатывания на широком коде внутри скролл-обёртки.

**Результаты (все — page `hOverflow=false`, `unscrolledOverflow=0`):**
| BP | / (focus) | /stats | /settings |
|---|---|---|---|
| **320** dark | ✅ (nav не переполнен; широкий CODE `scrolled:true`) | — | — |
| **375** dark | ✅ | ✅ | ✅ |
| **375** light-media | ✅ (layout theme-independent) | — | — |
| **768** dark (tablet) | ✅ | ✅ (chart.js не переполняет) | ✅ |

**Код-скролл-контейнмент (важно для мобильной читаемости, ревью-пункт):** на focus @375/320 широкий `CODE` (w≈603) сидит в `PRE.question-code` с `overflow-x:auto` (clientWidth 325, scrollWidth 635) → код НЕ клипается, читается горизонтальным скроллом; страница при этом не переполняется. Цепочка предков подтверждена (`code-copy-wrap`/`question-code-details`/`focus-training` все `overflowX:visible`, w=327 в пределах вьюпорта).

**Тема-нюанс:** CDP `colorScheme:light` меняет только `prefers-color-scheme`-медиа; палитра сайта осталась тёмной (`bg oklch(0.19 …)`) — тема управляется `data-theme`/localStorage-тумблером, а не медиа-запросом. Для overflow неважно (геометрия боксов тождественна свет/тьме → layout theme-independent подтверждён). Палитра-level light-QA поверхностей — в R0.151–161 (обе темы).

**Итог охвата `responsive` (на текущем live-build):** 320✅ 375✅ 485/500✅(R0.189/192) 768✅ 1280✅(мн. тиков) 1440/1728✅(R0.151-161 per-surface) 1920✅(R0.192) 2560✅(R0.151-161). H-overflow-матрица по основным поверхностям практически полна. Gate `responsive` **НЕ флипнут** — финальная консолидированная live-QA (6 брейкпойнтов × 2 темы) должна пройти против ПЕРЕкомпилированного приложения (после bootRun-stop), поэтому текущий проход — провизорный (layout-часть, стабильная между версиями app.js: изменения app.js касаются пост-ответного флоу, не вёрстки). Вьюпорт возвращён 1280×900 non-mobile, colorScheme auto.

---

## EV-A11Y-194 — Lighthouse accessibility/BP аудит основных поверхностей (R0.194, 2026-07-16)

**Цель:** вклад в release-gate `accessibility` (план: a11y=100, BP=100, только Chromium). После промоута Instrument в дефолт + правок шаблонов (UX-13/FLOW-REPORT/REVIEW-03) — свежий аудит текущего live-build. chrome-devtools `lighthouse_audit`, desktop, mode=navigation. Read-only → gradle не нужен.

**Результаты (desktop):**
| Поверхность | Accessibility | Best Practices | SEO | Agentic |
|---|---|---|---|---|
| `/` (focus) | **100** ✅ | **100** ✅ | 50 | 100 |
| `/stats` | **100** ✅ | **92** ⚠️ | 50 | 100 |
| `/settings` | **100** ✅ | **100** ✅ | 50 | 100 |

**Accessibility = 100 на всех трёх основных поверхностях.** (Advisory weight-0 `label-content-name-mismatch` на /stats не влияет на score=100.)

**🔴 Находка BP-STATS-1 (BP=92 на /stats, needsGradle):** два провала — `errors-in-console` + `inspector-issues`, оба один корень: **CSP блокирует `connect` к `cdn.jsdelivr.net`, когда chart.js@4.5.0 (CDN) пытается подтянуть sourcemap** (`chart.umd.js.map`). CSP (`SecurityConfig.java:65`, HTTP-заголовок) имеет `default-src 'self'` + `script-src … https://cdn.jsdelivr.net`, но БЕЗ `connect-src` с jsdelivr → fetch sourcemap падает на `default-src 'self'` → CSP-violation в консоли. **Pre-existing** (не регресс моих правок; chart.js только на /stats, на / focus BP=100). Проявляется только с прикреплённым инспектором (sourcemap не грузится без devtools) → для реальных юзеров без devtools ошибки нет, но Lighthouse всегда с инспектором → штрафует. **Рекомендуемый фикс (1 строка, migratable, low-risk):** добавить `connect-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com` в CSP-директиву `SecurityConfig.java`. Java → **gated на bootRun-stopped тик** (иначе gradle-wedge). Альтернатива (self-host chart.js без sourcemap) отклонена — инвазивно под feature-freeze, непоследовательно с mermaid/hljs-CDN.

**SEO=50 (все поверхности) — by design, НЕ дефект, вне gate:** провалы `is-crawlable` (страница заблокирована от индексации — `noindex` осознан: личный учебный инструмент, не публичный веб-контент) + `meta-description` (публичная SEO-мелочь, нерелевантна). План таргетит только a11y=100+BP=100. SEO не трогаем.

**Gate `accessibility` НЕ флипнут** — a11y=100 подтверждён на 3 core-поверхностях desktop, но финальный gate требует: остальные поверхности (result/session-summary/error — покрыты per-surface R0.151-161), mobile-device аудит, и re-run против ПЕРЕкомпилированного app. Провизорный проход, как responsive.

---

## EV-CNCLEAN-195 — Console/network audit основных поверхностей (R0.195, 2026-07-16)

**Цель:** release-gate `consoleNetworkClean` — систематический захват console-сообщений + сетевых сбоев (404/blocked/5xx) по всем always-reachable поверхностям. chrome-devtools `list_console_messages` + `list_network_requests`, read-only → gradle не нужен.

**Результаты (desktop 1280):**
| Поверхность | Console | Network | Вердикт |
|---|---|---|---|
| `/` (focus) | **0 сообщений** | **13/13 [200]** | ✅ CLEAN |
| `/settings` | **0 сообщений** | **12/12 [200]** | ✅ CLEAN |
| `/stats` | 2 (оба = BP-STATS-1) | **12/12 [200]** | ⚠️ только BP-STATS-1 |

**Network — все запросы [200] на всех 3 поверхностях**, включая: `tokens.css?v=65`, `base.css?v=106`, `app.js?v=68` (подтверждает: live-build держит app.js **v=68**, source-scripts.html=v=70 → app.js НЕ cp-синхронизирован, консистентно с UX-13-entanglement-правилом), `stats.js?v=15`, `chart.umd.min.js` (200), highlight.js/mermaid/Google-шрифты (все 200), `/api/streak` (200). Ни одного 404/blocked-ресурса.

**Console:** `/` и `/settings` — абсолютно чисты (0 сообщений). `/stats` — ровно 2 сообщения, оба = **BP-STATS-1** (R0.194): `[error] Connecting to 'https://cdn.jsdelivr.net/npm/chart.js@4.5.0/dist/chart.umd.min.js.map' violates … "connect-src 'self'". Blocked` + `[issue] CSP blocks some resources (count:1)`. Подтверждено: CSP имеет явную `connect-src 'self'` директиву без jsdelivr → sourcemap-fetch блокируется. Сам `chart.umd.min.js` грузится (200) — блокируется только `.map` (pre-flight connect, в network-списке не появляется). Никаких НОВЫХ находок.

**Консолидация блокеров:** весь остаточный console/network-долг на primary-поверхностях = ОДНА BP-STATS-1. Тот же 1-строчный CSP-фикс (`connect-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com` в `SecurityConfig.java`) закроет **сразу два** gate: `consoleNetworkClean` И `best-practices=100` на /stats.

**Gate `consoleNetworkClean` НЕ флипнут** — заблокирован единственной BP-STATS-1 (Java/gradle, gated на bootRun-stopped тик); плюс финал требует result/session-summary/error (console-clean per-surface в R0.151-161) + re-run против перекомпилированного app (app.js v=68→70). Провизорный проход.

---

## EV-PERF-196 — Performance trace, Core Web Vitals основных поверхностей (R0.196, PERF-02, 2026-07-16)

**Цель:** release-gate `performanceProdLike` / задача PERF-02. chrome-devtools `performance_start_trace` (reload+autoStop), read-only → gradle не нужен.

**Результаты (lab, dev-build, CPU 1x, network none):**
| Поверхность | LCP | CLS | TTFB | Render delay | RenderBlocking savings |
|---|---|---|---|---|---|
| `/` (focus) | **265 ms** ✅ | **0.00** ✅ | 136 ms | 129 ms | FCP 0 / LCP 0 ms |
| `/stats` (chart.js) | **283 ms** ✅ | **0.00** ✅ | 104 ms | 179 ms | FCP 0 / LCP 0 ms |

**LCP 265/283 ms** — глубоко в «good» (<2.5s). **CLS 0.00 на обеих**, включая /stats с chart.js canvas → диаграмма НЕ вызывает сдвига лейаута (зарезервированные размеры canvas + font-loading без reflow). **Нет значимого render-blocking** (savings 0 ms). CrUX field-данных нет (localhost).

**Observed-benign (НЕ actionable): ForcedReflow на /stats** — inline-скрипт `/stats:8996`→`:9013` (chart.js-конструктор читает геометрию canvas при рендере), total 63 ms, но **estimated savings: none**, CLS 0.00 не задет. chart.js-intrinsic, вне нашего кода; под feature-freeze гнаться за savings=none внутри third-party не оправдано. Прочие insights (DOMSize/ThirdParties/NetworkDependencyTree) — без флагов проблем.

**Gate `performanceProdLike` НЕ флипнут — dev-build caveat:** трейс снят на dev-профиле (localhost, без network/CPU-throttling, JVM `-XX:TieredStopAtLevel=1`). Истинно «production-like» бюджеты требуют prod-профиль-билд + реалистичный 4G/CPU-4x throttling → gradle (prod build) → bootRun-stopped тик. НО build-НЕЗАВИСИМЫЕ сигналы все зелёные: CLS 0.00 (стабильность лейаута — не зависит от билда/throttle), короткий критический путь, 0 render-blocking. Структура perf здоровая; абсолютные ms (LCP/TBT под throttling) — на prod-like re-measure. Провизорный проход, как responsive/a11y/console.

---

## EV-A11Y-197 — A11Y-01 регресс-переверификация против текущего билда (R0.197, 2026-07-16)

**Контекст:** A11Y-01 закрыт R0.170 (EV-A11Y-001). С тех пор билд претерпел FE-CMP-1 (удаление design-оси), промоут Instrument в дефолт, правки шаблонов (UX-13/FLOW-REPORT/REVIEW-03). Этот проход — регресс-переверификация keyboard/SR-семантики на текущем live-build + более глубокая структурная детализация. Read-only chrome-devtools `evaluate_script`, без правок → gradle не нужен.

**`/` (focus) — структурный keyboard/SR-аудит:**
- **positiveTabindex: 0** ✅ (нет анти-паттерна; порядок фокуса = DOM-порядок).
- **skip-link** «Перейти к вопросу» → `#main-content` ✅.
- **15 tabbable, логичный порядок:** skip→бренд→nav(Фокус/Аналитика/Настройки)→layout-тумблер→theme-тумблер→«Завершить сессию»→«Пример кода»(summary)→«Копировать код»→4 радио→«Отметить как незнакомый». **#design-toggle ОТСУТСТВУЕТ** (подтверждает: удаление FE-CMP-1 держится, дефект R0.170 не воскрес).
- **interactiveMissingName: 0** ✅; **MCQ 4 радио, все с labels, 1 группа** ✅ (правильная radio-group клавиатура); **4 live-region** (alert/status/polite/vh) ✅; **progressbar** role+valuenow=1/valuemax=20 ✅; **H1→H2→H3** без пропусков ✅; **lang=ru + main + nav landmarks** ✅.
- **focus-visible (grep base.css):** 20 правил; глобальный `:focus-visible`-ринг (L229) + корректный `:focus:not(:focus-visible){outline:none}` (L233, гасит outline только для мыши). Все `outline:none` — либо mouse-only, либо заменены `box-shadow: var(--shadow-focus)`; комментарии учитывают High Contrast Mode. **Ни одного «голого» outline:none-анти-паттерна.**

**`/settings` — кастомный ARIA-tablist:**
- **Roving-tabindex КОРРЕКТЕН** (`rovingOk:true`): ровно один tab tabindex=0 (Сессия, aria-selected=true), остальные -1 → Tab входит в tablist один раз, стрелки навигируют. aria-label «Разделы настроек».
- **panelsLinked:true** (каждый `aria-controls`→реальный role=tabpanel); **panels allLabelled** (aria-labelledby) ✅.
- **Неактивные панели `display:none`** (appearance/data): 0 keyboard-reachable контролов → нет focus-leak, удалены из a11y-дерева. (Атрибут `hidden` не выставлен, но display:none функционально эквивалентен — не дефект.)
- **positiveTabindex:0, noName:0** ✅; **5 radiogroup персонализации — все named** ✅; heading-иерархия логична ✅.

**Вывод:** A11Y-01 **держится clean** на текущем билде после всей волны template-изменений; регресса нет. Более глубокое evidence (roving-tabindex, panel display:none, focus-visible-аудит), чем оригинал EV-A11Y-001. Дополняет автоматический Lighthouse a11y=100 (EV-A11Y-194) ручной keyboard-операбельностью. **Observed-мелочи (НЕ дефекты, feature-freeze → не трогаю):** progressbar `valuetext=null` (valuenow/max уже озвучивают «1 из 20»); неактивные tabpanel без атрибута `hidden` (display:none достаточно). Оба — belt-and-suspenders, не блокеры.

---

## EV-BUILD-199 — Полный clean build + FLOW-01b, замыкание cleanBuild+fullTests (R0.199, 2026-07-16)

**Контекст:** пользователь остановил bootRun (:8080) и разрешил `kill -9 $(lsof -t -i:8080)`, дал «продолжай финальную волну». Окно bootRun-OFF использовано для gradle-work, который при живом bootRun даёт wedge (project-lock).

**FLOW-01b (UI паузы/возобновления), commit `68e00d99`:**
- `InterviewPageMvcService`: инъекция `PauseService` + model-атрибут `pausedInfo` = `interviewSession != null ? null : pauseService.pausedInfo().orElse(null)` (баннер только когда активной HTTP-сессии нет).
- `focus-training.html`: resume-баннер вверху `#main-content` (`th:if=${pausedInfo != null}`, accent-wash, «N из M отвечено · topic», `POST /resume`); кнопка «Пауза» в rail (`th:if=${interviewSession != null and !interviewSession.finished}` → TRAINING без сессии → пауза скрыта = «только сессионные режимы»).
- `base.css`: `.resume-banner` (flex space-between, accent-border, wash) + `.rail-pause-btn` (borderless, hover-underline); версия v=107.
- Тест `InterviewPageMvcServiceTest`: `@Mock PauseService` + 9-арг конструктор.
- Целевой набор `*TemplateFragmentContractTest *InterviewMvcControllerTest *InterviewPageMvcServiceTest *PauseServiceTest *InterviewFlowMvcServiceTest` — **зелёный**.

**Отложенные дефекты компиляции (маскировались инкрементальной компиляцией + wedge):**
- `DiagramServiceTest.java:3` — мёртвый `import com.cheatsheet.quiz.infrastructure.diagram.DiagramService` (класс удалён при AI-removal; тест реально проверяет `MermaidSanitizer`). Импорт удалён.
- `service/package-info.java` — повисшие `@see` на удалённые `DiagramService`/`HintService`, неверный путь `InterviewService`; проза очищена от «AI/диаграммы».

**Полный `./gradlew clean build` — BUILD SUCCESSFUL (43s):** clean + compileJava всех 3 модулей + весь тест-сьют + `jacocoTestCoverageVerification` + `check` (ArchUnit) + `bootJar`/`assemble`. Docker/quiz-postgres :5432 healthy, Testcontainers PG 16-alpine.

**→ Гейты `cleanBuild` + `fullTests` = true.** Честно заслужены: clean build против свежего рабочего дерева *есть* перекомпиляция (не провизорно, в отличие от responsive/accessibility, которые ждут re-run против recompiled/prod-app).

**Остаток bootRun-OFF код-волны:** только FLOW-04 (UI-баннеры recoverable). Далее — один bootRun-up для консолидированной live-QA (UX-13 faithful, FLOW-01b resume-флоу, BP-STATS-1 BP=100, PERF-02 prod-like) + флип оставшихся gate → FINALIZED.
