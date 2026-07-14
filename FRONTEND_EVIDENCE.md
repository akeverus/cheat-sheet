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

*(append далее — вьюпорты 375/768/1280/1440/1728/1920/2560, обе темы)*
