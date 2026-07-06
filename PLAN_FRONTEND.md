# PLAN_FRONTEND.md — план работ по frontend / UI / UX (по каждому файлу и пункту)

Зафиксированный план бесконечного итеративного цикла улучшения интерфейса
quiz-app (тренажёр подготовки к собеседованию). Работаем как senior frontend +
product UI/UX + accessibility reviewer. **Хирург, а не экскаватор:** маленькие
безопасные порции, каждая с причиной, без хаотичного редизайна и слома
бизнес-логики.

Центр документа — **§5: МАСТЕР-ТАБЛИЦА** (строки = frontend-файлы, столбцы = 27
измерений UI/UX/a11y; каждая ячейка = состояние файла по этому измерению —
✅ готово / 🌱 править / 🚫 осознанно / ⛔ отложено / 🔒 recompile / — н/п). Полная
расшифровка каждого пункта — **§6: per-file/per-point бэклог**. §7 — тематический
cross-cutting индекс (тот же бэклог под другим углом). Журнал сделанного — `docs/ui-ux-improvement-log.md`.
Память между тиками — `project_audit_backlog_2026-06` (memory).

> **⚠️ СТАТУС ПРОЦЕССА (с 2026-07-01/02): пользователь ведёт активный ручной frontend-pass.**
> Пользователь сам делает кодовые правки полным тулчейном (gradle + Playwright + live QA):
> его р39 = answer-fairness (adaptive single-column / neutral selected / Escape-clear, CSS v→65, JS v→53),
> р40 = analytics next-actions CTA (stats.js v→12). Он ведёт **собственный** бэклог
> `docs/ui-ux-backlog.md` (ID: AF/AN/SE/AX) и **сам редактирует** `docs/ui-ux-improvement-log.md`
> (перенумеровал мой закоммиченный р39 → р41). **Под активной незакоммиченной правкой:** base.css,
> app.js, stats.js, head.html, result.html, settings.html, focus-training.html, stats.html + **сам лог + сам бэклог**.
> **Правило коллизий (ужесточено р40):** пока это дерево не закоммичено — НЕ писать ни в код, ни в
> `docs/ui-ux-improvement-log.md`, ни в `docs/ui-ux-backlog.md` (всё это территория пользователя;
> `git add` свернёт его хунки, `-p` недоступен). Безопасны только **этот файл** (PLAN, committed-clean)
> и **память** (вне репо). Мои находки, пересекающиеся с его AF/AN/SE/AX — проверять по on-disk, НЕ считать
> закрытыми на слово (**корр. р97: BAS-21 НЕ в его base.css — `base.css:2764` до сих пор tertiary; запись р40
> ошибочна**). Возврат к реальным правкам — когда его дерево осядет (git status чист).

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
Берём сначала **Impact=H, Risk=L/M, Confidence=H**. Колонка **↔** — ссылка на
тематический ID из §7 (answer-fairness AF, тренировка TR, типографика TY, layout LO,
аналитика AN, настройки SE, цвета CO, компоненты CM, a11y A11Y, responsive RE,
perf PE, контент CN, иконки IC).

---

## 1. Стек и жёсткие ограничения

- **Стек:** Spring Boot + **Thymeleaf** + **vanilla CSS/JS**. НЕ npm/React →
  `npm lint/typecheck/test/build`, Storybook, Cypress, Playwright-CI = **N/A**.
  Проверки = Gradle-static (когда уместно) + живой `chrome-devtools` (CSSOM/DOM).
- **CSS:** `tokens.css` (токены 10 дизайнов) + `base.css` (структура).
  `editorial.css` — мёртвый код. Бамп `?v=N`: при правке CSS — обе ссылки в
  `fragments/head.html`; при правке JS — `app.js ?v=N` в `result.html` /
  `settings.html` / `focus-training.html`, `stats.js` — в `stats.html`. Чистая
  правка текста шаблона бампа НЕ требует. **Версии (on-disk, вкл. in-flight пользователя, свер. 2026-07-06 / HEAD bcde7ade): app.js = v=54 (settings/result/focus-training), stats.js = v=12; CSS — коммит-база v=61 (р37), у пользователя in-flight v=65 (р39). Точные committed-номера сверить, когда дерево пользователя осядет.**
- **Дизайн-система:** 10 переключаемых дизайнов (editorial=дефолт, linear, swiss,
  notion, mintlify, broadsheet, superhuman, stripe, claude, theverge) × 2 темы.
  Персонализация — `data-*` на `<html>`; **дефолт = `data-design="editorial"`
  (явный атрибут, не отсутствие — ставит inline-скрипт в head; правила
  `html[data-design] …` гейтятся на нём).** В скрин/verify-рутине ставить
  `setAttribute('data-design','editorial')`, НЕ `removeAttribute`.
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

## 2. Инвентарь frontend-файлов (что где живёт)

Путь-база: `modules/quiz-app/src/main/resources/`. Размеры — на момент фиксации.

### Шаблоны страниц (`templates/`)

| Файл | Стр. | Экран / роль | URL |
|------|-----:|--------------|-----|
| `focus-training.html` | 191 | Главная + Фокус (вопрос/флешкарта/MCQ/empty) | `/`, `/training` |
| `result.html` | 151 | No-JS фоллбэк результата ответа (живой!) | POST `/answer` |
| `session-summary.html` | 171 | Итоги сессии (одноразовая) | `/session-summary` |
| `settings.html` | 279 | Настройки — вкладки Сессия/Оформление/Данные | `/settings` |
| `stats.html` | 247 | Аналитика — таблица тем + графики + поиск | `/stats` |
| `error.html` | 58 | Страница ошибки 4xx/5xx | любой битый путь |

### Фрагменты (`templates/fragments/`)

| Файл | Стр. | Роль |
|------|-----:|------|
| `head.html` | 355 | `<head>`: CSS/JS-линки + версии, inline-скрипты персонализации (theme/design/layout/font/measure/motion/density), FOUC-guard, font-loading flip |
| `header.html` | 212 | Editorial masthead: nav, тогглы (layout/design/theme), back-to-top, noscript, sprite-include |
| `icons.html` | 51 | SVG-спрайт Lucide (20 символов `#i-…`) |
| `inline-alert.html` | 12 | Параметризуемый `role=alert` `aria-live=assertive` |
| `mermaid-init.html` | 55 | Инициализация mermaid (antiscript guard) — **внешний owner, не трогать** |
| `post-answer-controls.html` | 25 | inline-alert + result-feedback + триггер «доп. анализ» + sink |
| `result-zone-head.html` | 13 | zone-chip + zone-hint (заголовок зоны результата) |
| `stats-grid.html` | 32 | Сетка 6 метрик + accuracy-bar (sidebar/result) |
| `today-widget.html` | 61 | `today-hero` (settings) + `today-chip` (focus): стрик + «N из M к повтору» |
| `training-actions.html` | 24 | Таймер + submit + next + keyboard-hint |

### Стили (`static/css/`)

| Файл | Стр. | Роль |
|------|-----:|------|
| `tokens.css` | 1405 | Токены 10 дизайнов × 2 темы (цвет/spacing/типографика/радиусы/error-wash) |
| `base.css` | 3217 | Вся структура: layout, компоненты, состояния, WIDE LAYOUT, print, forced-colors, per-design SIGNATURES |
| `editorial.css` | 2656 | **МЁРТВЫЙ КОД** — не подключён ни одним `<link>` |
| `broadsheet.css` | 230 | Доп. слой дизайна broadsheet |
| `linear.css` | 191 | Доп. слой дизайна linear |
| `swiss.css` | 184 | Доп. слой дизайна swiss |

### Скрипты (`static/js/`)

| Файл | Стр. | Роль |
|------|-----:|------|
| `app.js` | 2239 | Answer-flow, пост-ответный анализ-sink, стрик-бар, sync тогглов, favorite, regenerate, `icon()`, seg-controls, вкладки настроек |
| `stats.js` | 348 | Chart.js-конфиги графиков аналитики |

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

## 4. Карта экранов и их состояний (держать в голове)

| # | Экран | Шаблон | Состояния |
|---|-------|--------|-----------|
| 1 | Главная / вопрос | `focus-training.html` (`body.focus-page`) | flashcard, MCQ-выбор, выбран-до-проверки, проверен-верно/неверно, разбор; no-JS фоллбэк `result.html` |
| 2 | Фокус-тренировка | `focus-training.html` | те же + empty (5 взаимоисключающих веток) |
| 3 | Настройки | `settings.html` | вкладки Сессия/Оформление/Данные; danger-zone |
| 4 | Аналитика | `stats.html` + `stats.js` | таблица тем, графики, поиск/фильтры, empty (нет данных/нет совпадений) |
| 5 | Ошибка | `error.html` | 404/403/401/400/5xx + dev-disclosure |
| — | Итоги сессии | `session-summary.html` | одноразовая; только через EXAM/MARATHON/STUDY |

---

## 5. МАСТЕР-ТАБЛИЦА: файлы × 27 измерений (что править / что готово)

Одна строка = один frontend-файл. Один столбец = одно измерение UI/UX/a11y.
Ячейка отвечает на вопрос «в каком состоянии этот файл по этому аспекту».
Детальная расшифровка каждого 🌱/🚫/⛔ — в **§6** (по ID в колонке «Главный открытый пункт»).

**Легенда ячеек:** ✅ соответствует / сделано · 🌱 открытый пункт (нужна правка, +ID) ·
🚫 осознанное решение (не трогать) · ⛔ отложено (ждёт решения пользователя) ·
🔒 требует Java/recompile (вне фронта) · 🔁 постоянный контроль · **—** не применимо.

**Ключи 27 столбцов:** HIER=иерархия/layout · SPACE=отступы (шкала 4/8/12/16/24/32/48) ·
TYPO=типографика/шкала · RESP=адаптив/overflow/mobile · COLOR=семантич. цвет + не-только-цветом ·
CONTR=контраст WCAG AA · BTN=кнопки/состояния · FORM=формы (label/валидация/required) ·
TABLE=таблицы (scope/caption/headers) · ICON=иконки-система · EMPTY=empty-state · LOAD=loading-state ·
ERR=error-state · SEM=семантич. HTML/роли · FOCUS=focus-visible · KBD=клавиатура · SRLBL=SR-лейблы ·
LIVE=aria-live/анонсы · TOUCH=тач-таргеты ≥44px · MOTION=reduced-motion · HEADO=иерархия h1–h3 ·
COPY=микрокопирайт · FAIR=answer-option fairness · PE=progressive-enhancement (без JS) ·
DUP=переиспользование/нет дублей/dead-code · VER=версионный контракт `?v=N` · PRINT=`@media print`.

_Свер. 2026-07-06 (HEAD после pedago-интерливов). On-disk версии: `tokens.css` v=61, `base.css` v=69
(пользователь бампнул дальше в in-flight pass), `app.js` v=54, `stats.js` v=12._

| Файл | HIER | SPACE | TYPO | RESP | COLOR | CONTR | BTN | FORM | TABLE | ICON | EMPTY | LOAD | ERR | SEM | FOCUS | KBD | SRLBL | LIVE | TOUCH | MOTION | HEADO | COPY | FAIR | PE | DUP | VER | PRINT | Главный открытый пункт |
|------|------|-------|------|------|-------|-------|-----|------|-------|------|-------|------|-----|-----|-------|-----|-------|------|-------|--------|-------|------|------|----|-----|-----|-------|------------------------|
| **▸ СТРАНИЦЫ** | | | | | | | | | | | | | | | | | | | | | | | | | | | | |
| `focus-training.html` | ✅ | ✅ | 🚫 | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | FT-1..18 закрыты; 48px h2 + neutral-selected = 🚫 |
| `result.html` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | Чисто; RES-14 снят (false-positive: код всегда в `.answer:82`, `.question-side`=избыт.пин AI-режима), RES-8 контраст AA все 20; RES-3 favorite=⛔ |
| `settings.html` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | 🌱 | ✅ | ✅ | 🌱 | ✅ | — | ✅ | ✅ | ✅ | — | 🌱SET-14 блок экспорта без h3; 🌱SET-7 персонализация не анонсируется SR |
| `stats.html` | ✅ | ✅ | ✅ | ✅ | 🌱 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | 🌱 | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | — | 🌱STA-18 forecast-count SR-дубль; 🌱STA-5 серии графиков не только цветом |
| `session-summary.html` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | Чисто; dual-path nav = 🚫SUM-8 |
| `error.html` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | — | — | — | — | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | Чисто; 403 авто-retry = ⛔ERR-3 |
| **▸ ФРАГМЕНТЫ** | | | | | | | | | | | | | | | | | | | | | | | | | | | | |
| `fragments/head.html` | — | — | ✅ | ✅ | ✅ | — | — | — | — | — | — | ✅ | ✅ | ✅ | — | — | — | — | — | ✅ | — | ✅ | — | ✅ | ✅ | ✅ | — | Чисто; CDN-guards, 6 осей персонализации до 1-го кадра |
| `fragments/header.html` | 🌱 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | — | ✅ | — | — | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | — | — | 🌱HDR-1 компактный header во время сессии |
| `fragments/icons.html` | — | — | — | — | ✅ | ✅ | — | — | — | 🌱 | — | — | — | ✅ | — | — | ✅ | — | — | — | — | — | — | ✅ | ✅ | — | — | 🌱ICO-3 нет warning-иконки для warn/error (low) |
| `fragments/mermaid-init.html` | — | — | — | ✅ | 🚫 | 🚫 | — | — | — | — | — | ✅ | ✅ | — | — | — | 🚫 | — | — | — | — | — | — | ✅ | ✅ | — | — | MER-1 🚫 внешний owner; сам код чист (antiscript-guard) |
| `fragments/inline-alert.html` | ✅ | — | — | ✅ | ✅ | — | — | — | — | — | — | — | ✅ | ✅ | — | — | — | ✅ | — | — | — | — | — | — | ✅ | — | — | Чисто; role=alert+assertive (IAL-1) |
| `fragments/post-answer-controls.html` | ✅ | — | — | ✅ | ✅ | — | ✅ | — | — | — | — | — | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | — | ✅ | — | ✅ | ✅ | — | — | Чисто; feedback polite + sink под кнопкой (PAC-1..2) |
| `fragments/result-zone-head.html` | ✅ | — | — | ✅ | ✅ | — | — | — | — | — | — | — | — | ✅ | — | — | — | — | — | — | — | ✅ | — | ✅ | ✅ | — | — | Чисто; zone-chip+hint (RZH-1) |
| `fragments/stats-grid.html` | ✅ | — | — | ✅ | ✅ | — | — | — | — | — | 🚫 | ✅ | — | ✅ | — | — | ✅ | — | — | — | — | ✅ | — | ✅ | ✅ | — | — | Чисто; accuracy-bar aria-hidden (SGR-3); cold-start = 🚫 |
| `fragments/today-widget.html` | ✅ | — | — | ✅ | ✅ | — | ✅ | — | — | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | 🚫 | ✅ | — | ✅ | ✅ | — | ✅ | ✅ | — | — | Чисто; «N из M к повтору» (TDW-1); streak без aria-live = 🚫 |
| `fragments/training-actions.html` | ✅ | — | — | ✅ | ✅ | 🌱 | ✅ | ✅ | — | ✅ | — | — | — | ✅ | ✅ | ✅ | ✅ | 🚫 | ✅ | — | — | ✅ | — | ✅ | ✅ | — | — | 🌱TAC-3 keyboard-hint контраст (low); timer/hint без aria-live = 🚫 |
| **▸ СТИЛИ** | | | | | | | | | | | | | | | | | | | | | | | | | | | | |
| `css/tokens.css` | — | ✅ | ✅ | — | 🚫 | ✅ | — | — | — | — | — | — | — | — | ✅ | — | — | — | — | ✅ | — | — | — | — | ✅ | ✅ | — | TOK-1 AA-гейт CLEAN, TOK-5 OS-prefs; TOK-2 accent≈semantic hue = 🚫 identity, mitig. not-by-color-alone |
| `css/base.css` | 🌱 | ✅ | ✅ | ✅ | ✅ | 🌱 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | 🌱BAS-15 tabs↔seg (HIER), 🌱BAS-21 today-hero-hint tertiary (4 AA-fail, min 2.96); BAS-17 helper-контраст = ✅ verified-clean; BAS-18 measure = 🚫 |
| `css/editorial.css` (мёртв) | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 🌱 | — | — | 🌱EDC-1 не подключён, 2656 строк-дубль tokens+base + stale (до AF-5/tabs); verify-then-remove |
| `css/{linear,swiss}.css` (мёртвы) | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 🌱 | — | — | 🌱DSG-1 не подключены, сигнатуры уже в base.css (linear=2/swiss=6) → чистые дубли, удалить |
| `css/broadsheet.css` (не дубль!) | 🌱 | — | — | — | — | — | 🌱 | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 🌱 | — | — | 🌱DSG-2 CONFIRMED: 13 структ.правил (nav/btn/link) НЕ в base.css (0 vs linear 2/swiss 6) → broadsheet теряет структ.акценты, токены живут |
| **▸ СКРИПТЫ** | | | | | | | | | | | | | | | | | | | | | | | | | | | | |
| `js/app.js` | ✅ | — | — | ✅ | ✅ | — | ✅ | ✅ | 🌱 | ✅ | ✅ | 🌱 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | 🌱 | ✅ | — | 🌱APP-8 regenerate без aria-busy; 🌱APP-9 comparison-table th без scope/caption; ⛔APP-5 favorite; v=54 |
| `js/stats.js` | — | — | 🌱 | ✅ | ✅ | ✅ | ✅ | — | ✅ | — | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | — | 🌱STJ-1 tick-подписи графиков мелкие (low); kbd/progress-dup c app.js = 🚫 standalone-by-design; v=12 |

**Как читать сводку.** Открытые 🌱-пункты по файлам: `settings.html`(SET-14/SET-7),
`stats.html`(STA-18/STA-5), `header.html`(HDR-1), `icons.html`(ICO-3), `training-actions.html`(TAC-3),
`base.css`(BAS-15/BAS-21), `editorial.css`+`linear/swiss.css`(EDC-1/DSG-1 — мёртвые дубли),
`broadsheet.css`(DSG-2 — не дубль!), `app.js`(APP-8/APP-9), `stats.js`(STJ-1).
⛔: `error.html`(ERR-3 403-retry), `app.js`(APP-5 favorite).
**Не-blocked, можно брать сразу:** EDC-1 + DSG-1 (удалить мёртвые `editorial/linear/swiss.css` — вне in-flight
дерева, но удаление = решение). DSG-2 требует правки `base.css` (blocked) + дизайн-решения. Остальные 🌱 —
в blocked-файлах под активным pass пользователя → ждут чистого `git status`. Расшифровка каждого ID — §6 ниже.

**Из аудита 2026-07-06/07:** DSG-2 — **CONFIRMED статически** (2026-07-07): `broadsheet.css` держит 13 структурных
rule-блоков (nav/btn/link, Hudson-бордюры), НЕ портированных в `base.css` (0 правил); broadsheet — единственный
из 4 дизайнов с dedicated `.css`, чью структуру не мигрировали (linear/swiss/editorial — мигрированы). Токены
broadsheet живут через `tokens.css`, теряются лишь структурные акценты (severity M→L). APP-9 — JS-построенная
comparison-table без `th[scope]`/`caption`. Оба в §6.

---

## 6. PER-FILE / PER-POINT БЭКЛОГ (расшифровка мастер-таблицы)

> Колонки: **#** · **Пункт** (точка интерфейса в файле) · **Состояние / проблема** ·
> **Направление правки** · Imp · Risk · Eff · Conf · **Статус** · **↔** (тематич. ID).

### 6.A.1 — `templates/focus-training.html` (главный экран)

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| FT-1 | `.focus-topbar` (eyebrow/hint ↔ session-rail) | Полноширинный бар вместо вырожденной правой рейки; ряд только ≥1200px | — | M | M | H | ✅ DONE (редизайн) | TR-1 |
| FT-2 | `.question-zone-head` chip+hint (`focusModeChipText/HintText`) | Серверная микрокопия режима | Уточнять per-mode формулировки точечно | L | L | L | M | 🌱 BACKLOG | TR-3 |
| FT-3 | `.focus-progress-line` / `.session-progress-track` | Гейт `th:if` — вне сессии нет пустого `progressbar 0/0` | — | M | L | L | H | ✅ DONE (C-live-1) | A11Y |
| FT-4 | `today-chip` include | Стрик + «N из M к повтору» | см. TDW | — | — | — | — | ✅ DONE | TR-6 |
| FT-5 | `.rail-finish` «Завершить сессию» | Выход к итогам с самой тренировки (POST /finish) | — | M | L | L | H | ✅ DONE (B3) | — |
| FT-6 | `.focus-question-topic` eyebrow-kicker | Тема вопроса подписана (ориентир в смешанной выборке) | — | M | L | L | H | ✅ DONE | TY-1 |
| FT-7 | `.focus-question` h2 48px | Намеренный размер «окна вопроса» | НЕ трогать | — | — | — | H | 🚫 WONTFIX | TR-8 |
| FT-8 | `.question-code-details` (Пример кода) | Нативный `<details>` — работает без JS | — | M | L | L | H | ✅ DONE | — |
| FT-9 | `.flashcard-grade-buttons` (Не помню…Легко) | grade-1..4 цветовая шкала; проверить контраст и порядок цвет↔смысл | Verify AA + что цвет дублируется текстом (он есть) | M | L | L | M | 🌱 BACKLOG | CO-4 |
| FT-10 | `.options[role=radiogroup]` selected-до-проверки | Нейтральный ring/border (без green/red) до submit | — | H | L | L | H | ✅ DONE 🔁 | AF-2/3 |
| FT-11 | option label: CSS-бейдж `counter(upper-latin)` + срез «X. » | Буква рисуется CSS, видимый span чистится; aria-label с буквой | — | M | L | L | H | ✅ DONE | A11Y-1 |
| FT-12 | option fairness: длина/наполненность выдаёт correct | Контент-перекос; UI обязан не усиливать | seed/mcq pedago-loop | H | — | — | H | 🏛 CONTENT | AF-1 |
| FT-13 | option layout: adaptive single/2-col по длине (fairness) | ✅ Реализовано пользователем (р39): app.js `updateAnswerLayoutMode` (1157-1179) меряет длину видимого текста вариантов, `lengthRatio=max/min`; `forceSingleColumn = ratio > 1.4 (ANSWER_LAYOUT_SKEW_THRESHOLD) OR inline-code сконцентрирован в одной карточке` → `data-answer-layout=single|balanced`. base.css:2913-2936: дефолт 1 колонка (честно при skew), `[data-answer-layout=balanced]`→2×2 только для сопоставимых, `:has(.option-explanation)`→снова 1 колонка после ответа. Ровно бриф #3-5 (single default, 2-col лишь при близких длинах, авто-порог) | H | L | L | H | ✅ DONE-BY-USER (р39) | AF-4 |
| FT-14 | inline-`code` в варианте = визуальная подсказка | Гасим заливку/паддинг чипа в тексте выбора (моно остаётся, color inherit); explanation/проза не тронуты | M | L | L | M | ✅ DONE (р17 v=54) | AF-5 |
| FT-15 | `training-actions` include (Проверить ответ) | CTA далеко при длинных вариантах | sticky/видимый submit | M | M | M | M | 🌱 BACKLOG | TR-4 |
| FT-16 | empty-state «Сейчас нет вопросов» (5 веток) | Нейтральный заголовок + recovery (finished→«Посмотреть итоги») | — | M | L | L | H | ✅ DONE (р15 `3bb19db0`) | TR-2 |
| FT-18 | empty-state «фильтры пусты»: primary CTA = «Обновить тренировку» (no-op для filter-mismatch, уводит по кругу), а копия просит «Открой настройки» | Флип primary→«Открыть настройки» ТОЛЬКО в filters-ветке (`settingsPrimary` = `!genUnavail and !review and session==null`); прочие ветки (AI/сессия/review) байт-идентичны. Шаблон-текст, без бампа. Контракт-тесты сверены (filters-expr:94, empty-action-*, next-btn=/result-only) | M | L | L | H | ✅ DONE (р31) | TR-2 |
| FT-17 | `app.js(v=51)` в конце body | Версионный контракт JS | бампать при правке app.js | — | — | — | H | 🔁 ONGOING | PE |

### 6.A.2 — `templates/result.html` (живой no-JS фоллбэк POST /answer)

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| RES-1 | `.top-row` stats-grid + session-bar | Компактная верхняя строка, иконки верно/неверно + текст | — | M | L | L | H | ✅ DONE | — |
| RES-2 | `.question-layout :has(.question-side)` | CSS-грид без класса-флага (убрана мёртвая ссылка на diagram) | — | M | L | L | H | ✅ DONE (C24) | — |
| RES-3 | `.btn-favorite` (звезда, aria-pressed) | Избранное — флоу под вопросом юзера | НЕ трогать без решения | — | — | — | — | ⛔ DEFERRED | — |
| RES-4 | `.btn-regenerate` (только `aiEnabled`) | Перегенерация вариантов через AI | — | — | — | — | H | ✅ DONE (гейт aiEnabled) | — |
| RES-5 | `.status` (Верно!/Неверно) | Иконка + текст + цвет (не только цвет) | — | H | L | L | H | ✅ DONE 🔁 | CO-4 |
| RES-6 | options после проверки: `option-correct/wrong/other` + status-label | Правильный помечен «Правильный ответ», выбор — «Твой выбор» | — | M | L | L | H | ✅ DONE | AF-6 |
| RES-7 | невыбранные wrong-варианты после проверки «кричат» | `.option-other` opacity un-scoped из `.result-page` → focus-page тоже 0.85 (был баг: пояснение возвращало 1.0) | M | L | L | M | ✅ DONE (р18 v=55) | AF-7 |
| RES-8 | `.explanation-wrong` контраст текста (base.css:816-819: bg=`--color-bg-tertiary`, color=`--color-text-secondary`, нейтральный — НЕ red, по answer-fairness) | **✅ VERIFIED-CLEAN статически (2026-07-07):** посчитал WCAG для пары text-secondary/bg-tertiary во **всех 20 дизайн×тема** — минимум editorial-light **4.76:1**, notion-dark **5.11:1**, остальные 5.5–11.9 → AA-normal (≥4.5) проходит везде. `.explanation-wrong strong`=text-primary (ещё выше). Приглушённый нейтральный вид = осознанная fairness, НЕ баг контраста | — | — | — | — | H | ✅ DONE (verified-clean, static) | AF/A11Y |
| RES-9 | «Пояснение» h3 (book-open) + markdown | Полный разбор | — | — | — | — | H | ✅ DONE | — |
| RES-10 | `.sm2-details` (SM-2 состояние) | Раскрываемые SRS-данные | — | — | — | — | H | ✅ DONE | — |
| RES-11 | `#extra-analysis-toggle-result` hidden→JS reveal | Без JS не показывается (нет мёртвого контрола) | — | M | L | L | H | ✅ DONE | TR-9 |
| RES-12 | `#result-related-questions` sticky-рейка ≥1200px | Прямой потомок `.ed-page` (id-якорь), не в `.card` | — | M | L | L | H | ✅ DONE (C31) | RE-5 |
| RES-13 | `app.js(v=51)` в конце body | Перенесён из середины main | — | — | — | — | H | ✅ DONE (C31) | — |
| RES-14 | `.question-side` (пин кода, result.html:128) гейтится `aiEnabled and questionType==CODE and codeSnippet!=null` | **❌ FALSE-POSITIVE, СНЯТ (verify-resolved 2026-07-07, р98).** Ранняя гипотеза р90 «под seed-first разбор теряет код-контекст» — НЕВЕРНА. Трассировка данных: (1) `codeSnippet` = «первый блок кода **из ответа**» (`MarkdownQuestionParser`); (2) `QuestionImportService:229` пишет `codeSnippet` ТОЛЬКО при `questionType==CODE` → в БД `codeSnippet!=null ⟺ CODE` (гейт `questionType==CODE` избыточен, не вреден); (3) **`result.html:82 .answer` рендерит ПОЛНЫЙ `answerHtml` (`AnswerPageService:59` = `renderMarkdown(answerMarkdown)`, код НЕ вырезается) ВСЕГДА, без гейта** → блок кода уже внутри разбора. → **На /answer код НЕ теряется** (он в `.answer`); `.question-side` = избыточная ПИН-копия того же кода в правой рейке, включаемая только в two-column AI-режиме (`:has(.question-side)`). Единственный эффект `aiEnabled=true` = код показан ДВАЖДЫ (в `.answer` + пин) — мягкая избыточность, defensible как deliberate (пин кода рядом со скроллящимся разбором+анализом). Асимметрия с focus-training оправдана: там ответ ещё скрыт → `<details>Пример кода` = ЕДИНСТВЕННОЕ место кода (нужно для ответа). **Никакой правки: не баг.** | — | — | — | — | H | ✅ verified-NOT-a-bug (код всегда в `.answer:82`) | — |

### 6.A.3 — `templates/session-summary.html` (одноразовые итоги)

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| SUM-1 | `.summary-score` (score-value цвет по accuracy) | Точность + Всего/Верно/Ошибки + длительность | — | — | — | — | H | ✅ DONE | — |
| SUM-2 | `.summary-tools` (Скопировать/Печать) инжект | PE: без JS кнопок нет; текст из DOM | — | M | L | L | H | ✅ DONE (sharing-bundle) | — |
| SUM-3 | `.summary-actions` nav | НЕ дубль (р38-проверка): при ошибках primary=«Повторить ошибки»(/review) + secondary=«Продолжить тренировку»(/) — РАЗНЫЕ пути (ревью ошибок vs продолжить с новыми). Комментарий шаблона (стр.34-35) подтверждает намеренный dual-path IA. «Новая сессия»(/settings)/«Аналитика»(/stats) тоже distinct | Не трогать — намеренный выбор | L | L | L | H | ✅ verified-deliberate (р38) | — |
| SUM-4 | `.summary-table` role=table tabindex=0 + ARIA | Восстановление семантики при `display:block` скролле | — | M | L | L | H | ✅ DONE (`8d1a52ab`) | A11Y-5 |
| SUM-5 | `role=rowheader` на `<td>` темы | Намеренно: share-JS читает `querySelectorAll('td')` | НЕ менять структуру td | — | — | — | H | 🚫 WONTFIX | — |
| SUM-6 | `.summary-mistakes` / `.summary-recommendations` | Кликабельные ошибки → тренировка по теме | — | — | — | — | H | ✅ DONE | — |
| SUM-7 | share-script `buildShareText` | Контракт: ≥5 `<td>` на строку, recs из `li` | НЕ ломать селекторы | — | — | — | H | 🔁 ONGOING (контракт) | — |
| SUM-8 | `@media print` | Чистый лист, break-inside avoid | — | M | L | L | H | ✅ DONE (р14) | PE |

### 6.A.4 — `templates/settings.html` (вкладки)

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| SET-1 | ARIA-вкладки Сессия/Оформление/Данные | Заменили 4-карточную стопку (3.6→1.2 экрана) | — | H | M | M | H | ✅ DONE (`0eb762a5`) | SE-1 |
| SET-2 | Кнопка фильтров «Применить фильтры» | Паритет с /stats, самоописательность | — | L | L | L | H | ✅ DONE (р15) | SE-2 |
| SET-3 | Design seg-control без preview | Краткое описание/preview пресета при выборе | M | M | M | M | 🌱 BACKLOG | SE-3 |
| SET-4 | Danger-zone сброса банка | «⚑ Опасная зона» error-wash + полная рамка + «Действие необратимо.» | — | H | L | M | H | ✅ DONE (р16 `90d3d245`) | SE-4 |
| SET-5 | seg-controls похожи на tabs | Развести визуально tabs vs seg-control | M | M | M | M | 🌱 BACKLOG | SE-5 |
| SET-6 | font-stepper | Показывать %/reset явно | M | L | M | M | 🌱 BACKLOG | SE-6 |
| SET-7 | нет feedback «сохранено» | Тост/строка «Применено» после смены настройки | M | L | L | M | 🌱 BACKLOG | SE-7 |
| SET-8 | `aria-describedby` на seg/шафл/порядок/stepper | Подсказки связаны со скринридером | — | M | L | L | H | ✅ DONE (р14 `6dc4c3cd`) | SE-8 |
| SET-9 | a11y-оси разбросаны | Сгруппировать движение/контраст в раздел Accessibility | M | M | M | L | 🌱 BACKLOG | SE-9 |
| SET-10 | admin reset (был `window.prompt`) | Доступная модалка | — | M | L | M | H | ✅ DONE (`653c706e`) | SE-10 |
| SET-11 | `.focus-question` min-height | Одинаковые «окна вопросов» | — | M | L | L | M | ✅ DONE (`0eb762a5`) | SE-11 |
| SET-12 | CTA запуска был бинарным («Начать тренировку»/дженерик «Начать сессию») | 4 из 5 режимов давали родовое «Начать сессию» (не говорит, что запускается). Карта `MODE_CTA` в `initSessionModeForm` (app.js): каждый режим → свой винительный лейбл («Начать экзамен/изучение/флешкарты/интенсив»), `\|\| 'Начать сессию'` defensive-фоллбэк. app.js v=51→52 | M | L | L | H | ✅ DONE (р26) | CN |
| SET-12 | `app.js(v=51)` | Версионный контракт | — | — | — | — | H | 🔁 ONGOING | — |
| SET-13 | 10-опционный дизайн-seg-control переносится в 3 ряда → `border-right`-разделители «повисают» на торце рядов (Notion/Superhuman): `:last-child` гасит только глоб.-последнюю кнопку | Скоуп `#design-pref-control`: контейнер без рамки/bg + gap, каждый сегмент = чип с собственной рамкой + `--border-radius-sm` (design-adaptive). Орфанов нет; прочие 5 контролов (2–3 опции) не тронуты. CSS v=58→59 | M | L | L | H | ✅ DONE (р32) | SE-3/SE-5 |
| SET-14 | Вкладка «Данные»: подраздел экспорта (settings.html:252-259) без заголовка, а danger-zone ниже несёт h3 «Опасная зона» (265) → heading-nav несимметрична: SR прыжком по заголовкам слышит h2 «Данные» → сразу h3 «Опасная зона», экспорт-блок не якорится как секция | +h3 «Экспорт данных» над `.data-export-block` — паритет с danger-zone h3, обе подсекции навигируемы по заголовкам; визуально дублирует существующий `<p>`-хинт, но даёт SR-структуру. Чистый шаблон-текст, без бампа. **⚠️ БЛОКЕР:** settings.html под активным in-flight пользователя → `git add` свернёт чужие хунки (`-p` недоступен); отложить до коммита пользователя | L | L | L | H | 🌱 BACKLOG (blocked-file) | A11Y |

### 6.A.5 — `templates/stats.html` + `static/js/stats.js`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| STA-1 | Метрики технические, не actionable | ✅ Реализовано пользователем: секция «Что делать дальше» (stats.html:17-49) — 3 CTA-карточки (Повторить сегодня / Разобрать ошибки / Тренировать слабые темы) + `is-recommended` по состоянию банка (due/wrong/weak); stats.js `initNextActions` (44-68) называет реально слабейшую тему «Слабее всего: X · N%» + пишет aria-label карточки. На существующих маршрутах/данных, без новых API | H | M | M | H | ✅ DONE-BY-USER (р40) | AN-1 |
| STA-2 | «К повтору 9886» пугает | today-hero получил «N из M вопросов к повтору» (паттерн+гард from today-chip line 57) → знаменатель = честная пропорция, не интимидирующая стена. Stats-tile «К повтору» в сетке оставлен (SRS-термин, dashboard-контекст; hero/chip несут пояснение) | — | M | L | L | H | ✅ DONE (р21) | AN-2 |
| STA-3 | Поиск отделён от фильтров линией | ~~Объединить поиск+фильтры~~ — НЕ дефект: вертикальный hairline + верт.центрирование поиска = осознанное решение с rationale `base.css:1476-1480` (одно поле vs высокий фильтр → пустота под полем как намеренный воздух). Форм-мердж рискован (2 cross-wired формы). Переоткрытие = churn | — | M | M | M | 🚫 WONTFIX (deliberate) | AN-3 |
| STA-4 | Таблица тем — горизонтальный scroll | overflow-x:auto + tabindex/role/aria-label скролл-региона | — | M | L | L | H | ✅ DONE (р11) | AN-4 |
| STA-5 | `stats.js` графики мелкие/слабые labels | Меньше decorative grid, крупнее labels, tooltips (Chart.js config). **Ясность/честность-ось закрыта R30 (STA-7); остаётся только label-size/grid — низкий приоритет (JS + бамп stats.js, MxM)** | M | M | M | M | 🌱 BACKLOG | AN-5 |
| STA-6 | Сортировка не видна в покое | Glyph ⇅ на `sortable[aria-sort=none]` | — | M | L | L | H | ✅ DONE (р6) | AN-6 |
| STA-7 | Графики рисуют top-25, но заголовок «…по темам» подразумевает ВСЕ; actionable-порядок (точность — слабые впереди) невидим | Подзаголовок `.chart-subtitle` под каждым h2: «Самые активные темы» / «Сначала самые слабые»; aria «те же»→«полные данные». Muted-sm (переиспользован токен `.chart-fallback`, light 6.3:1 / dark 7.34:1) | M | L | L | H | ✅ DONE (р30) | AN-5 |
| STA-7 | Empty-state поиска обещал сброс, контрола не было | Ссылка «сбрось поиск и фильтры» → `/stats` | — | M | L | L | H | ✅ DONE (р15 `3bb19db0`) | AN-7 |
| STA-8 | Empty-state нулевых данных по теме | `.stats-empty` секция после stats-grid: «Пока нет данных» + CTA «Начать тренировку» + reset-фильтров (гейт = инверсия section-gates) | M | L | L | H | ✅ DONE (р19, template-only) | AN-8 |
| STA-9 | Print печатал 12 из 319 тем | Разворот `.is-collapsed` в `@media print` | — | M | L | L | H | ✅ DONE (`06985234`) | AN-9 |
| STA-10 | CTA из аналитики (тренировать слабые/ошибки) | Кнопки-переходы — может требовать роутов/параметров | M | M | M | L | ⛔ DEFERRED (проверить контракт) | AN-10 |
| STA-11 | accuracy «58/122/32.2%» необъяснима | (а) `title` на `<th>Точность` (паритет с Сброшено/Зрелость): «Доля верных среди отвеченных (не из всех)»; (б) `th:title` ячейки раскрывает дробь «Верных: N · Отвечено: M» (числа после `:` → grammar-safe), «—»→«Пока нет ответов». Чистый Thymeleaf, без бампа | M | L | L | H | ✅ DONE (р25, template-only) | AN-11 |
| STA-12 | `stats.js(v)` в stats.html | Версионный контракт stats.js | — | — | — | — | H | 🔁 ONGOING | — |
| STA-13 | «Прогноз повторений» показывал сырой ISO `2026-07-01` | `record ForecastDay(String day)` → дата уже String из SQL (не temporal); починка в источнике = Java/SQL (gated). Фикс на клиенте: `<span>`→`<time th:datetime>` (семантика + ISO машинно) + inline PE-скрипт `Intl ru-RU` → `Сегодня`/`Завтра`/`6 июля` + `title` с днём недели. Локаль-независимо, без recompile, PE-фоллбэк = ISO | M | L | L | H | ✅ DONE (р24, template+inline-script, без бампа) | AN-13 |
| STA-15 | thead `<th>` таблиц (topic-table 6 + data-table 2) без `scope` | `scope="col"` на все 8 заголовков колонок (WCAG 1.3.1/H63 — явная ассоциация ячейка↔заголовок, надёжнее браузерной эвристики у sortable-таблицы). Инертный атрибут (0 CSS/JS/визуала), без бампа. Верифиц. curl'ом под app-деградацией | M | L | L | H | ✅ DONE (р35, template-only) | A11Y |
| STA-16 | `td.topic-name` — не row-header | `td.topic-name → <th scope="row">` (319 строк): SR при навигации по data-ячейке озвучит тему строки. Компенсация UA `th{bold}`: `base.css .topic-table th.topic-name{font-weight:var(--font-weight-normal)}` (text-align:left уже на th+td). **Sort цел:** stats.js читает `children[col]` (th-agnostic), не `querySelectorAll('td')` — проверено ПЕРЕД. Рендер пиксель-идентичен (компенсация) → семантическая дельта, скрины не нужны. Live-verify: 319 th[scope=row], fw 400 обе темы, sort asc/desc ОК | M | L | M | H | ✅ DONE (р37, CSS v=60→61) | A11Y |
| STA-17 | coverage-gaps `data-table` без `<caption>` | topic-table несёт h2+region+`<caption>`, data-table — только h2+scope (R35). `<caption>` = accessible name таблицы в table-nav SR (≠ h2 в heading-nav; topic-table:142 задаёт прецедент при своём h2). Фикс: +`<caption class="visually-hidden">Темы с неполным банком вопросов</caption>` (noun-phrase, не дублирует h2). Region-обёртка не нужна (2 колонки, нет overflow). Инертный visually-hidden (0 layout), без бампа. Верифиц. curl by-construction (секция за `th:if coverageGaps`, паттерн-сиблинг topic-table caption рендерится живьём) | M | L | L | H | ✅ DONE (р36, template-only) | A11Y |
| STA-18 | Прогноз повторений: `.forecast-count` (голое число, stats.html:278) дублирует уже озвученный `.forecast-bar[role=img]` aria-label «N вопросов» (274) → SR на строке слышит число дважды (у bar — с единицей, у count — без). Визуальному пользователю нужны оба (bar = длина, count = точное число), но для AT count избыточен | `aria-hidden="true"` на `.forecast-count` — декоративный визуальный дубль, полную формулировку с единицей несёт bar; паттерн как у SGR-3 (accuracy-bar aria-hidden при дубле текстом). Чистый шаблон-атрибут, без бампа. **⚠️ БЛОКЕР:** stats.html под in-flight пользователя → отложить до коммита | L | L | L | H | 🌱 BACKLOG (blocked-file) | A11Y |

### 6.A.6 — `templates/error.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| ERR-1 | `.error-code` крупный номер (декор) | aria-hidden, реальный заголовок — `error-title` | — | — | — | — | H | ✅ DONE | — |
| ERR-2 | `.error-title` per-code | 404/403/401/400/5xx персонализированы | — | M | L | L | H | ✅ DONE | — |
| ERR-3 | `.error-body` per-code | 403 = понятная CSRF-копия (устаревший токен); body-гранулярность приведена к заголовочной — +400-ветка «проверь данные» (р23), 400 больше не получает серверную «логи приложения» рамку | — | M | L | L | H | ✅ DONE (`7149f7ec`, р23) | — |
| ERR-4 | `.error-details` dev-disclosure | Путь/статус/причина/сообщение/время | — | — | — | — | H | ✅ DONE | — |
| ERR-5 | `.error-actions` (На главную/Аналитика/Настроить) | Recovery-пути из тупика | — | M | L | L | H | ✅ DONE | CM-10 |

### 6.B.1 — `fragments/head.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| HEAD-1 | 2 CSS-линка `?v=N` (=53) | Контракт кэш-инвалидации при правке CSS | бампать ОБЕ строки | — | — | — | H | 🔁 ONGOING | — |
| HEAD-2 | inline-скрипты `__theme/__design/__layout` + оси font/measure/motion/density | Персонализация data-* на `<html>` | паттерн добавления = 4 касания | — | — | — | H | ✅ DONE | — |
| HEAD-3 | FOUC-guard: `data-design=editorial` ставится до рендера | Дефолт-дизайн — явный атрибут | НЕ переводить на «отсутствие атрибута» | — | — | — | H | ✅ DONE | CO-6 |
| HEAD-4 | font-loading `media=print`/onload + noscript | Намеренный нерендер-блокирующий flip | НЕ «чинить» | — | — | — | H | 🚫 WONTFIX | PE-3 |
| HEAD-5 | удалены no-op `<link rel=preload as=style>` | Render-blocking same-origin CSS | — | L | L | L | H | ✅ DONE (р14) | PE-4 |
| HEAD-6 | Chart.js `defer` + порядок stats.js | Не блокировать рендер | — | M | L | L | H | ✅ DONE (р14) | PE-2 |
| HEAD-7 | self-host chart.js vs CDN (CSP sourcemap) | Архитектурный + download-gated выбор | — | — | — | — | ⛔ DEFERRED | PE-5 |
| HEAD-8 | `<title>` через `${title}` (head-fragment param, per-template литерал) | settings/summary выбивались из паттерна «<Имя> — Подготовка к собеседованию» (stats/result/error следуют). Приведены к паттерну (р22); home=голое имя приложения (конвенция). Title — template-литерал, фиксится без Java | — | M | L | L | H | ✅ DONE (р22) | — |

### 6.B.2 — `fragments/header.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| HDR-1 | masthead конкурирует с вопросом на тренировке | Компактный sticky header во время сессии (masthead уже PE-скрыт без JS; проверить высоту) | M | M | M | M | 🌱 BACKLOG | TR-1 |
| HDR-2 | Единая nav Фокус/Аналитика/Настройки + `aria-current` | Конец дубля с surface-tabs | — | M | L | L | H | ✅ DONE | — |
| HDR-3 | Группа тогглов (layout/design/theme) PE hidden→reveal | Без JS — нет мёртвых контролов; пара не разъезжается при wrap | — | M | L | L | H | ✅ DONE | — |
| HDR-4 | back-to-top инжект, уважает `data-motion` | Появляется после ~1.5 экрана, rAF-throttle, focus-return | — | M | L | L | H | ✅ DONE (`af765fd0`) | CM-11 |
| HDR-5 | noscript-alert | Честно предупреждает о JS-зависимых фичах | — | — | — | — | H | ✅ DONE | — |
| HDR-6 | sprite-include один раз на страницу | header есть везде | — | — | — | — | H | ✅ DONE | IC-1 |

### 6.B.3 — `fragments/icons.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| ICO-1 | 20 монохромных Lucide-символов через `currentColor` | Единый icon-layer, обе темы | — | M | L | M | H | ✅ DONE | IC-1 |
| ICO-2 | `.icon-sprite` (absolute, 0×0) hide | Канон-hide SVG-спрайта (не display:none — рвёт `<use>` в Safari); убрал 159px мёртвого отступа | — | M | L | L | H | ✅ DONE | — |
| ICO-3 | нет dedicated warning-иконки (danger исп. `#i-flag`) | Намеренный reuse flag для danger-zone | при IC-4 — взвесить отдельный warning-символ | L | L | L | M | 🌱 BACKLOG | IC-4 |
| ICO-4 | кнопки экспорта несли сырой юникод «↓» (не icon-система, рендер OS-зависим, стоял ПОСЛЕ текста) | Добавлен `#i-download` (Lucide, 21-й символ), swap «текст ↓»→«`<svg.ed-icon>` текст» (SVG + icon-lead как сиблинги). Спрайт+шаблон, без бампа. Контракт-тесты (Экспорт JSON/CSV текст + data-export-format) целы | L | L | L | H | ✅ DONE (р34) | IC-4 |
| IC-5 | CTA-кнопки несут сырой юникод «→» (SR-шум + вне icon-системы). **Точный скоуп (р38-аудит):** 7 `btn next-btn` — `today-widget.html:46,47` («Начать повторение →»/«Учить новое →»), `result.html:107` («Следующий вопрос →»), `session-summary.html:37,38` («Повторить ошибки →»/«Продолжить →»), `stats.html:99`, `error.html:52` («На главную →»). Остальные 40 «→» в grep = комменты/JS/проза (НЕ трогать) | **⚠️ БЛОКЕР — convention-решение (р38):** не механический swap. Все icon-кнопки ВЕДУТ иконкой (icon-lead, R34-конвенция); directional «next→» семантически ТРЕЙЛИТ → iconify создаёт mixed lead/trail. 3 варианта, каждый taste-call: (a) trail arrow-icon (ломает lead-конвенцию); (b) lead «→ Следующий» (семантически странно); (c) убрать «→» совсем (минимализм, но снимает намеренную консистентную аффордансу). «→» консистентен на ВСЕХ CTA → вероятно ОСОЗНАННЫЙ паттерн. Нужен выделенный design-decision раунд в СПОКОЙНОЙ среде (visible-delta → screenshot regen settings/stats/error + by-construction result/summary), НЕ casual sweep под нагрузкой | M | M | M | L | 🌱 BACKLOG (design-decision) | IC-4 |

### 6.B.4 — `fragments/inline-alert.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| IAL-1 | `role=alert` `aria-live=assertive`, параметр hidden | Переиспользуемый алерт-примитив | — | — | — | — | H | ✅ DONE | A11Y-2 |

### 6.B.5 — `fragments/mermaid-init.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| MER-1 | antiscript guard + drop при отсутствии mermaid | XSS-поверхность 'loose' закрыта; offline degrade | mermaid — внешний owner | — | — | — | H | 🚫 WONTFIX (не трогать mermaid) | — |

### 6.B.6 — `fragments/post-answer-controls.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| PAC-1 | inline-alert + `#result-feedback` (role=status polite) | Фидбэк проверки озвучивается politely | — | M | L | L | H | ✅ DONE | A11Y-2 |
| PAC-2 | `#extra-analysis-toggle` + `#extra-analysis-content` sink ПОД кнопкой | Раскрытый контент идёт под триггером (не сиротит кнопку); aria-controls/expanded | — | M | M | M | H | ✅ DONE 🔁 | TR-9 |

### 6.B.7 — `fragments/result-zone-head.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| RZH-1 | zone-chip + zone-hint (параметризуемые) | «Сначала итог, затем разбор» — предсказуемость флоу | — | L | L | L | M | ✅ DONE | TR-5 |

### 6.B.8 — `fragments/stats-grid.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| SGR-1 | 6 метрик (Всего/К повтору/Выучено/Верно/Ошибки/Точность) + accuracy-bar | Общая сетка, переиспользуется sidebar/result | — | — | — | — | H | ✅ DONE | — |
| SGR-2 | accuracy-bar-fill цвет/пороги | Цвет НЕ единственный носитель: значение дублируется текстовой плиткой «Точность N%» сразу выше (CSSOM-verify R20) | verify AA + дубль текстом — выполнено | L | L | L | M | ✅ DONE | CO-4, SGR-3 |
| SGR-3 | accuracy-bar без accessible-семантики | Осмысленная data-viz (ширина=точность), но без role/aria/лейбла; заливка стоит слева (под «Всего»), не под лейблом «Точность» → безымянная сбивающая графика для AT | `aria-hidden="true"` на `.accuracy-bar` (декоративный дубль текста; не `role=progressbar` — дабл-озвучка). 1 правка фрагмента = 3 экрана | M | L | L | H | ✅ DONE R20 | AN-12 |
| SGR-4 | 6 stat-карточек без пояснений SRS-жаргона (К повтору/Выучено) — несогласовано с колонками таблицы (у тех title R24/R25) | `title` на каждый `.stat-item`, формулировки сверены с SQL (due=next_review<=now, learned=repetitions>=threshold, correct/wrong=SUM, accuracy=Верно/(Верно+Ошибки)). Плейн-статик, 1 фрагмент = 3 включения. Чистый шаблон → без бампа | M | L | L | H | ✅ DONE (р29) | AN-14 |

### 6.B.9 — `fragments/today-widget.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| TDW-1 | `today-chip` «N из M к повтору» | Знаменатель даёт честную пропорцию вместо стены цифр; total==due → нейтральное «N к повтору» | — | M | L | L | M | ✅ DONE | TR-6/AN-2 |
| TDW-2 | `today-hero` CTA (Начать повторение/Учить новое) + hint | Лэндинг сессии на /settings | — | M | L | L | H | ✅ DONE | — |
| TDW-3 | streak-inline (🔥) без aria-live | Намеренно: наполняется при каждой загрузке, не state-change | НЕ добавлять live-region | — | — | — | H | 🚫 WONTFIX | — |
| TDW-4 | `today-hero` figure-label показывал голое «N вопросов к повтору» | Hero — самая заметная поверхность, но имел МЕНЕЕ продуманный паттерн, чем chip (TDW-1); голое 4-5-значное число = интимидирующая стена | Тот же «N из M вопросов к повтору» + гард `total>due` (мирроринг chip line 57); существительное сохранено (есть место). Big number/`data-stat-field`/live не тронуты | M | L | L | H | ✅ DONE (р21) | AN-2 |

### 6.B.10 — `fragments/training-actions.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| TAC-1 | `#question-timer` aria-hidden | Без live-region (иначе озвучка каждую секунду) | — | — | — | — | H | ✅ DONE | A11Y |
| TAC-2 | submit (`aria-keyshortcuts=Enter`) + next-btn | Контракт submitId/Text/Disabled/Title | — | — | — | — | H | ✅ DONE | — |
| TAC-3 | `.keyboard-hint` (символ ⌨ убран) | Текст самодостаточен, без цветного эмодзи вне icon-системы | Усилить контраст/иерархию hint | L | L | L | M | 🌱 BACKLOG | TR-3 |

### 6.C.1 — `static/css/tokens.css`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| TOK-1 | WCAG-контраст всех 10×2 | AA-гейт `scripts/design-token-audit.py` CLEAN | гонять при правке токенов | — | H | M | M | H | ✅ DONE 🔁 | CO-3 |
| TOK-2 | accent-hue vs success/error hue **измерено статически (2026-07-07)**: посчитал hue-дистанцию accent↔success/error по всем 10×2. 4 дизайна near-identical (swiss accent #e5231b = error red Δ0°; broadsheet #a21b24 = error Δ0°; mintlify #18e299 ≈ success green Δ4°; theverge #3cffd0 ≈ success Δ9°), 2 adjacent-но-различимы (editorial/claude coral #d97757 Δ12-15° от error, но S=63 vs насыщенный red), 4 cleanly separated (linear/stripe/superhuman/notion) | **Это НЕ баг-в-токенах, а identity-tension: accent = бренд-хью дизайна (Swiss red, mintlify/theverge mint, Anthropic coral) — менять hue = ломать identity.** Реальный сейфгард = **not-by-color-alone (WCAG 1.4.1) УЖЕ есть:** семантический фидбэк всегда несёт иконку+текст (app.js:1639 `circle-check`+«Верно» / `circle-x`+«Неверно»; option-status-label 1608; wash-фоны ≠ accent-wash) → смысл не зависит от hue. + fairness: семантика только после «Проверить», до — нейтраль | **Держать (mostly 🚫 deliberate + ✅ mitigated).** Residual: primary-CTA в swiss/broadsheet (red) / mintlify/theverge (green) делят hue с error/success — но контекст (кнопка vs status-label+wash) + иконки различают. Если юзер захочет доп.разделения — рычаг = sat/lightness семантических washes, НЕ accent-hue | L | M | M | H | ✅ DONE (characterized, safeguarded) | CO-1 |
| TOK-3 | `--color-status-error-wash` на все дизайны×темы | Питает danger-zone, тематизируется автоматически | — | — | — | — | H | ✅ DONE | SE-4 |
| TOK-4 | dark border/text слабые | Усилены border-primary в dark | — | M | L | L | H | ✅ DONE | CO-2 |
| TOK-5 | OS-prefs (prefers-contrast/forced-colors/reduced-transparency) | media-блоки токенов | — | M | L | M | H | ✅ DONE (`77882943`) | CO-5 |

### 6.C.2 — `static/css/base.css`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| BAS-1 | spacing scale (space-* 4/8/12/16/24/32/48) | Единый ритм | — | M | L | M | H | ✅ DONE | LO-2 |
| BAS-2 | контейнированные сцены + max-width | Стабильный page-container вместо full-bleed | — | M | M | M | H | ✅ DONE | LO-1 |
| BAS-3 | `data-layout` (Поток/Два пейна/Сетка) | Переключаемые раскладки | — | M | M | M | M | ✅ DONE | LO-3 |
| BAS-4 | WIDE LAYOUT грид (≥1200px) + sticky related-rail | `:has()`-driven, без класс-флагов | — | M | M | M | H | ✅ DONE | RES-12 |
| BAS-5 | `.danger-zone` (error-wash + полная рамка) | НЕ side-stripe (impeccable-ban); собственный divider формы погашен | — | H | L | L | H | ✅ DONE (р16) | SE-4 |
| BAS-6 | MCQ states (selected neutral / correct / wrong / other) | Семантика цвета только после проверки | — | H | L | L | H | ✅ DONE 🔁 | AF-2/3 |
| BAS-7 | MCQ adaptive single/2-col по длине | ✅ Реализовано (р39): `#interview-options` = `display:grid` 1fr (base.css:2917), `[data-answer-layout=balanced]`→`repeat(2,1fr)` (2925-2928), после ответа `:has(.option-explanation)`→1 колонка (2933). Порог/решение — app.js:1153-1178 (ratio>1.4 ИЛИ inline-code-skew→single). `data-answer-skew`/`data-answer-layout-reason` на DOM = инспектируемый parity-сигнал (частично закрывает content-parity dev-tool из брифа) | M | L | L | H | ✅ DONE-BY-USER (р39) | AF-4 |
| BAS-8 | inline-`code` фон в вариантах | `.options label code` + result text-span code → transparent/0/inherit (chip-pop убран, overflow-wrap цел) | M | L | L | M | ✅ DONE (р17) | AF-5 |
| BAS-9 | focus-visible ring везде + forced-colors fallback | Видимый ring | — | M | L | L | H | ✅ DONE (р12) | A11Y-3 |
| BAS-10 | `:active` press-feedback кнопок | Тактильный отклик | — | M | L | L | H | ✅ DONE | CM-1 |
| BAS-11 | reduced-motion + `data-motion` | `@media` + ось движения | — | M | L | L | H | ✅ DONE | A11Y-7 |
| BAS-12 | `@media print` (break-inside avoid, разворот collapsed) | Печать результата/таблиц/итогов | — | M | L | L | H | ✅ DONE (р14) | PE/AN-9 |
| BAS-13 | per-design SIGNATURES (editorial/linear/swiss) | Хвост base.css | новые 4 дизайна — token-only | — | — | — | H | ✅ DONE | — |
| BAS-14 | touch target ≥44px мелких контролов | Скан @375 (р29): checkbox 18px/radio 1×1 обёрнуты в `<label>` 44px-высоты (реальная цель — лейбл); seg-btn/step 44/48px; icon-тогглы ≥44. Провалов нет | M | L | L | H | ✅ DONE (verified-clean р29) | A11Y-8 |
| BAS-15 | tabs vs seg-control визуально похожи | Развести | M | M | M | M | 🌱 BACKLOG | SE-5/CM-2 |
| BAS-16 | UPPERCASE рус. labels + большой tracking: editorial-дефолт держал `--letter-spacing-wide: 0.12em` (самое широкое в системе, без комментария; 9 сиблингов оттюнены до 0.07–0.09em с Cyrillic-safety-коммами). На 12px mono кириллица разваливалась на буквы («Ф И Л Ь Т Р Ы») | `:root` `--letter-spacing-wide: 0.12em → 0.09em` (= Swiss-сигнатура, Cyrillic-safe верх полосы). Editorial-only; прочие 9 не тронуты. Замер live 1.44px→1.08px, скрины 14. CSS v=59→60 | M | L | L | H | ✅ DONE (р33) | TY-1 |
| BAS-17 | helper-text контраст всех `*-hint`/meta | **✅ VERIFIED-CLEAN статически (2026-07-07):** проверил все helper-классы base.css (`.zone-hint`:663, `.keyboard-hint`:992, `.coverage-gaps-hint`:1806, `.summary-mistakes-hint`:1945, `.summary-mistake-meta`:1955) — все на `--color-text-secondary`, посчитал contrast во всех 10×2 → минимум 5.50, AA-normal проходит везде. **ЕДИНСТВЕННОЕ исключение = `.today-hero-hint` на `text-tertiary` → см. BAS-21 (REOPEN)** | — | — | — | H | ✅ DONE (verified-clean, кроме BAS-21) | TY-5 |
| BAS-18 | prose `--measure` full-width | Намеренный выбор юзера (259ch на 2560) | НЕ трогать | — | — | — | H | 🚫 WONTFIX | LO-5/TY-3 |
| BAS-19 | `.zone-chip` красился `accent-strong` (#C96442, «AA large» 3.7:1) при 12px → провал AA-small в editorial-light | Добавлен в editorial-override (base.css:3187) к братьям `.ed-masthead-kicker`/`.flashcard-badge` → `text-secondary`; light 3.70→**6.26:1**, dark 4.8→**8.42:1**; остальные 8 дизайнов не тронуты (accent-чип сохранён). CSS v=55→56 | H | L | L | H | ✅ DONE (р27) | A11Y-14 |
| BAS-20 | дизайн `claude` (token-only): `.ed-masthead-kicker`+`.zone-chip`+`.flashcard-badge` = **3.8:1** в LIGHT (все три наследуют base `accent-strong` #C16040, «AA-large» токен на 12px caps); kicker глобальный | claude-**light**-scoped override (`:not([data-theme="dark"])`) три метки → `text-secondary` (5.98:1). Dark НЕ трогаем: там accent-strong #E08B6D=7.1:1 проходит + намеренный бренд-coral (token-only наследование). Развилка «нейтраль vs затемнить токен» решена в пользу нейтрали (зеркалит editorial spec «pure typographic label»). CSS v=56→57 | M | L | L | H | ✅ DONE (р28) | A11Y-15 |
| BAS-21 | `.today-hero-hint` (подсказка под CTA today-hero /settings, `font-size-sm`) — единственный `*-hint`, красящийся `--color-text-tertiary` (все прочие: zone-hint/keyboard-hint/coverage-gaps-hint/summary-mistakes-hint = `text-secondary`). tertiary калиброван «AA-large» (3:1), а hint = мелкий текст (порог 4.5:1). **Полный замер статически по tokens.css (р97, все 10×2, tertiary/bg-secondary):** 4 ПРОВАЛА AA-small — `notion-dark 2.96`, `notion-light 3.91`, `claude-light 4.39`, `broadsheet-light 4.49`; остальные пограничные (editorial-light 4.89, editorial-dark 4.99, claude-dark 5.07). | `.today-hero-hint color: tertiary → secondary` (1 property, паритет со всеми *-hint; **secondary AA-safe во всех 10×2, min 5.50**). CSS-бамп. **⚠️ БЛОКЕР: base.css + head.html + app.js + 5 шаблонов под АКТИВНЫМ незакоммиченным UX REVIEW PASS пользователя.** `git add base.css` свернёт чужую работу, `-p` недоступен → правку ОТЛОЖИТЬ до коммита пользователя (не затирать in-flight). **❌ КОРРЕКЦИЯ р97:** запись р40 «✅ DONE-BY-USER (фикс уже в uncommitted base.css)» ОШИБОЧНА — on-disk `base.css:2764` до сих пор `color: var(--color-text-tertiary)` (фикс не приземлился / откачен). Находка РЕАЛЬНА и хуже, чем документировалось (notion-dark 2.96). Применить `tertiary→secondary` когда base.css освободится. | M | L | L | H | 🌱 BACKLOG (blocked-file, REOPEN р97) | A11Y |

### 6.C.3 — `static/css/editorial.css` (МЁРТВЫЙ КОД)

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| EDC-1 | 2656 строк не подключены ни одним `<link>` | Путает чтение/поиск, дублирует токены | Перед удалением — `grep` по всем шаблонам/JS, что нет ссылки; затем убрать. Удаление — мягко-деструктивно, спросить/подтвердить | L | L | L | M | 🌱 BACKLOG (verify-then-remove) | — |

### 6.C.4 — `static/css/{broadsheet,linear,swiss}.css`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| DSG-1 | `linear.css` + `swiss.css` НЕ подключены ни одним `<link>` (свер. 2026-07-06: `head.html` грузит только `tokens.css` v=61 + `base.css` v=69; ноль per-design `<link>`) | Их структурные сигнатуры УЖЕ портированы в `base.css` (`[data-design="linear"]`=2 правила, `swiss`=6) → эти два файла — **чистые мёртвые дубли**, как `editorial.css` (EDC-1): токены дублируют `tokens.css`, структура дублирует `base.css`. Активный механизм дизайна = `tokens.css` + `base.css`, не эти файлы. **⚠️ `broadsheet.css` — НЕ дубль, см. DSG-2 (единственный источник его структуры)** | Как EDC-1: удалить `linear.css`+`swiss.css` (мягко-деструктивно, спросить). `broadsheet.css` НЕ удалять до DSG-2 | L | L | L | H | 🌱 BACKLOG (verify-then-remove, non-blocked) | — |
| DSG-2 | **CONFIRMED статически (2026-07-07, app не нужен):** `broadsheet` теряет структурную identity. `base.css` = **0** правил `[data-design="broadsheet"]` (editorial=30, swiss=6, linear=2, а также stripe=3/claude=3 портированы), но `broadsheet.css` (не подключён) содержит **13 структурных rule-блоков** на `.ed-nav`/`.ed-nav-link`/`.btn`/`.secondary-btn`/`.next-btn`/`.danger-btn`/`.flashcard-*-btn`/`#extra-analysis-toggle`/`a` (nav-treatment, `border-radius:4px`, Hudson-blue `#41A1CF` бордюры). Broadsheet — ЕДИНСТВЕННЫЙ из 4 дизайнов с dedicated `.css` (editorial/linear/swiss/broadsheet), чью структуру НЕ мигрировали в `base.css` (нет `superhuman/theverge/notion/mintlify.css` — те born-token-only, для них 0 = норма) | При выборе `broadsheet` **токены рендерятся** (cream bg / obsidian buttons / serif — из `tokens.css` `[data-design=broadsheet]`×2), но 13 структурных акцентов ОТСУТСТВУЮТ → broadsheet = «свои токены на editorial-структуре», без Hudson-бордюров/nav-treatment. Severity M→L (identity частично живёт через токены; теряются тонкие акценты) | Портировать 13 rule-блоков из `broadsheet.css` в `base.css` (token-safe, ровно как сделали для linear/swiss) → затем `broadsheet.css` становится дублем → удалить с DSG-1. **Blocked: `base.css` под in-flight pass; + дизайн-решение (broadsheet как signature-дизайн vs демоут в token-only)** | M | L | M | H | 🌱 BACKLOG (blocked-file + decision) | — |

### 6.D.1 — `static/js/app.js` (v=54)

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| APP-1 | answer-flow (submit→verdict→sink) | aria-live politeness по kind, focus-return | — | M | L | L | H | ✅ DONE (р7/р11) | A11Y-2/4 |
| APP-2 | пост-ответный `appendAnalysisBlock` в sink | takeaway/трейс/related под кнопкой | — | M | M | M | H | ✅ DONE 🔁 | TR-9 |
| APP-3 | sync тогглов (theme/design/layout) | Шапка ↔ seg-control на /settings | — | M | L | L | H | ✅ DONE | — |
| APP-4 | `icon()` инжект SVG (не sanitizeHtml) | Иконки в динамике | — | — | — | — | H | ✅ DONE | IC-1 |
| APP-5 | favorite toggle (`btn-favorite`) | Флоу под решением юзера | НЕ трогать без решения | — | — | — | — | ⛔ DEFERRED | — |
| APP-6 | seg-controls + вкладки настроек (`initSettingsTabs`) | ARIA-вкладки, восстановление localStorage в try/catch | — | M | L | L | H | ✅ DONE | SE-1 |
| APP-7 | `?v=` контракт в 3 шаблонах | Бампать при правке app.js | — | — | — | — | H | 🔁 ONGOING | — |
| APP-8 | `regenerateQuestion` (app.js:1047-1115) async-state НЕ консистентен с братьями-хендлерами | Extra-analysis (343/348: `aria-busy=true` + re-entrancy guard) и export (592-601: `aria-disabled`+`aria-busy`) выставляют busy-семантику и гард повторного клика; regenerate же only `.regenerating`-класс + `title='Удаляю варианты…'` (tooltip → SR ненадёжно) — **нет `aria-busy`** (SR не слышит «занято») и **нет re-entrancy-гарда/disable** (двойной клик → дубль `POST /api/regenerate`, т.к. `obtainAdminToken` кэширует токен). Направление: при старте `button.setAttribute('aria-busy','true')` + ранний гард `if(button.classList.contains('regenerating'))return`, снимать в финале. Admin-only путь (`th:if=aiEnabled` скрывает в seed-first) → низкий impact. **app.js под in-flight pass (blocked)** | L | L | L | H | 🌱 BACKLOG (blocked-file) | A11Y |
| APP-9 | JS-построенная comparison-table (app.js ~стр.384/1819, extra-analysis разбор) — `<th>` без `scope`, `<table>` без `<caption>` | Аудит 2026-07-06: динамически собираемая таблица сравнения в доп.анализе рендерит `th` без `scope="col"/"row"` и без `caption` → SR не связывает заголовки со строками, теряется навигация по таблице (в отличие от SSR summary-table, где ARIA под `display:block` уже проставлена). Направление: добавить `th.scope` + `<caption class="sr-only">` в билдере таблицы. Проверить точные строки при разблокировке (app.js большой; блок extra-analysis). **app.js под in-flight pass (blocked)** | L | L | L | M | 🌱 BACKLOG (blocked-file, verify) | A11Y |

### 6.D.2 — `static/js/stats.js`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| STJ-1 | Chart.js конфиги: мелкие, decorative grid, слабые labels | Меньше сетки, крупнее labels, tooltips, empty-state графика. **Empty-state уже есть (`.chart-fallback`); ясность top-25-среза закрыта R30 (STA-7 — подзаголовки). Остаётся label-size/grid-density — низкий приоритет** | M | M | M | M | 🌱 BACKLOG | AN-5 |
| STJ-2 | insights-данные для STA-1 | ✅ Реализовано: stats.js `initNextActions` (44-68) сортирует topicStats по accuracy, берёт слабейшую (attempts>0), пишет в `[data-weak-topic-summary]` + aria-label карточки. Питает STA-1 «Что делать дальше», источник — модельный topicStatsJson (без новых API) | H | M | M | H | ✅ DONE-BY-USER (р40) | AN-1 |
| STJ-3 | `defer` + порядок загрузки | Chart.js не блокирует | — | — | — | — | H | ✅ DONE (р14) | PE-2 |

---

## 7. Тематический cross-cutting индекс (тот же бэклог по областям)

Свод ID по темам — быстрый «worst-first» вход. Подробности и статусы — в §5
(колонка ↔ ведёт обратно).

| Область | Открытые (🌱/⛔) | Закрытые (✅) | Не трогать (🚫) | Внешние (🏛) |
|---------|------------------|---------------|-----------------|--------------|
| **AF** answer-fairness | — (ядро закрыто) | AF-2/3 (нейтр. selected), AF-4 (single-column уже, р19), AF-5 (мягкий code, р17), AF-6 (correct помечен), AF-7 (muted wrong, р18) | AF-8 (иконки до проверки) | AF-1 (паритет длины) |
| **TR** тренировка | TR-1/HDR-1 (sticky header), TR-3 (hint), TR-4 (sticky CTA), TR-6 (микрокопия прогресса) | TR-2 (empty), TR-5 (zone-hint), TR-7 (kbd-гейт), TR-9 (post-answer) | TR-8 (48px) | — |
| **TY** типографика | TY-2/AF-5 (code), TY-5 (helper) | TY-1 (editorial-дефолт tracking 0.12→0.09em Cyrillic-safe, р33) | TY-3 (prose measure), TY-4 (18px/1.7) | — |
| **LO** layout | LO-6 (border-left 3px ⛔) | LO-1/2/3/4 | LO-5 (prose full-width) | — |
| **AN** аналитика | AN-1 (insights), AN-5 (графики), AN-10 (CTA ⛔) | AN-4/6/7/9, AN-8 (data-empty, р19), AN-12 (accuracy-bar aria-hidden, р20), AN-2 (микрокопия «N из M» в today-hero, р21), AN-13 (гуманизация ISO-дат прогноза `<time>`+Intl, р24), AN-11 (объяснение «Точность» title заголовка+дробь ячейки, р25); AN-3 (поиск+фильтр) = 🚫 WONTFIX deliberate | — | — |
| **SE** настройки | SE-3 (preview), SE-5 (seg vs tabs), SE-6 (% reset), SE-7 (feedback), SE-9 (a11y-раздел) | SE-1/2/4/8/10/11 | — | — |
| **CO** цвета | CO-1/TOK-2 (semantic split) | CO-2/3/4/5/6 | — | — |
| **CM** компоненты | CM-2/5/6/7/8/9(part)/10(part) | CM-1/3/4/11/12, CM-9 (danger-zone done) | — | — |
| **A11Y** | A11Y-1(verify), A11Y-8 (touch), A11Y-10 (confirm усилить) | A11Y-2/3/4/5/6/7/9 | — | — |
| **RE** responsive | RE-1/AF-4 (mobile 1-col) | RE-2/3/4/5/6 | — | — |
| **PE** perf | PE-5 (self-host chart ⛔) | PE-1/2/4 | PE-3 (font-flip) | — |
| **CN** контент-тулы | CN-2 (⛔) | — | — | CN-1 (паритет — pedago) |
| **IC** иконки | IC-5 (сырой «→» на CTA), IC-4-part (empty/insights) | IC-1/2, ICO-4 (export «↓»→`#i-download` SVG, р34) | IC-3 (намеренные эмодзи) | — |

---

## 8. Не трогать — осознанные решения пользователя (🚫 / ⛔)

- prose `--measure` full-width (259ch на 2560) · `.focus-question` 48px · `body`
  18px/1.7 · stats cold-start вид.
- `role=rowheader` на summary `<td>` (share-text JS читает `querySelectorAll('td')`).
- 💡-подсказка и 🎲🤔💪-уверенность (намеренные эмодзи); остальной UI — SVG-спрайт.
- font-loading media-flip (намеренно, откомментировано).
- mermaid (`mermaid-init.html`) — внешний owner, фронт не трогает.
- «акцент-линейка» `border-left 3px` (LO-6) — ⛔ DEFERRED (A/B решение юзера;
  impeccable банит side-stripe).
- favorite-флоу (RES-3/APP-5), CTA-из-аналитики (AN-10/STA-10), self-host chart.js
  (PE-5/HEAD-7) — ⛔ DEFERRED (decision/download/recompile-gated).

---

## 9. Цикл одной итерации

1. Выбрать одну строку §5 (или §6) worst-first (Impact↑/Risk↓/Conf↑).
2. Понять UX и код (graphify-first для навигации).
3. Сформулировать правку + оценка по 4 осям.
4. Минимальная source-правка.
5. Live-sync: `cp` source → `build/resources/main/…`.
6. Live-verify chrome-devtools (CSSOM/DOM), оба видимых состояния.
7. При видимой дельте — пересобрать полный скрин-набор (§9), старый удалить,
   **каждый скрин верифицировать содержимым** (race-bug, см. §9).
8. Запись в `docs/ui-ux-improvement-log.md` + статус строки в §5 → ✅.
9. Commit source-only пер-pathspec (без push).
10. Обновить память `project_audit_backlog_2026-06`. Следующая строка.

---

## 10. Скриншот-рутина (после каждого цикла с видимой дельтой)

`docs/ui-critique/screenshots/` (gitignored). **Старый набор удаляется
(`rm -f *.png`), новый перезаписывается.** Набор: 5 страниц × 2 темы desktop
(1440×900, fullPage) + 4 ключевых mobile (375×812×2, light) = **14 файлов**:
`1-home-{light,dark}`, `2-training-{light,dark}`, `3-settings-{light,dark}`,
`4-stats-{light,dark}`, `5-error-{light,dark}`, `6-home-mobile-light`,
`7-settings-mobile-light`, `8-stats-mobile-light`, `9-training-mobile-light`.

**Ловушки:**
- Dev-браузер копит персонализацию в localStorage (ключ `design` уводит дизайн с
  дефолта) — перед каждым скрином чистить ключи + форсить
  `setAttribute('data-design','editorial')` (НЕ `removeAttribute` — тот гасит
  правила `html[data-design] …` в момент чтения computed-style).
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
