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
> **р99 (2026-07-07): read-only аудит фронт-осколков AI-removal (мой backend-рефактор `b4ee4ffc`+`e323aac4` удалил AI-endpoints) → новый кластер §6.E (AIR-1..5). Ключевое: «Показать доп. анализ» теперь падает на КАЖДЫЙ клик (AIR-1) — верифицировано по on-disk WIP. Фикс blocked (app.js+шаблоны под pass) + нужно решение A/B/C. Закоммичен только PLAN.**
> **р100 (2026-07-07): полный route-integrity аудит → §6.F (RIA-1..3). forward-направление CLEAN (никаких битых вызовов сверх 5 AI-endpoints); reverse-орфан `POST /study-confirm` без вызывающего (RIA-2, ⛔ live-verify+decision, out-of-frontend-scope). Всё ещё blocked на коде — закоммичен только PLAN.**
> **р101 (2026-07-07): empty-state аудит (loop §4) + чистые шаблоны. `error.html`/`header.html` — образцовы (skip-link, aria-current, PE-тогглы, back-to-top respects data-motion), правок нет. focus empty-state (FT-16): 4 живые ветки образцовы; найдена 5-я МЁРТВАЯ AI-ветка (`generationUnavailable` хардкод false) → AIR-6. Blocked на коде — закоммичен только PLAN.**

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
  правка текста шаблона бампа НЕ требует. **Версии (on-disk, вкл. in-flight пользователя, свер. 2026-07-07 р100): app.js = v=54 (settings/result/focus-training), stats.js = v=12; CSS on-disk `head.html:298-299` = `tokens.css v=61` / `base.css v=69` (пользователь двигал base дальше — было v=65 на р39). Точные committed-номера сверить, когда дерево пользователя осядет.**
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
| `session-summary.html` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | Чисто; dual-path nav = deliberate (SUM-3); score-card h2 добавлен р104 (SUM-9) |
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
| `fragments/training-actions.html` | ✅ | — | — | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | — | — | — | ✅ | ✅ | ✅ | ✅ | 🚫 | ✅ | — | — | ✅ | — | ✅ | ✅ | — | — | Чисто; TAC-3 keyboard-hint контраст ✅ verified р107 (min 5.93:1); timer/hint без aria-live = 🚫 |
| **▸ СТИЛИ** | | | | | | | | | | | | | | | | | | | | | | | | | | | | |
| `css/tokens.css` | — | ✅ | ✅ | — | 🚫 | ✅ | — | — | — | — | — | — | — | — | ✅ | — | — | — | — | ✅ | — | — | — | — | ✅ | ✅ | — | TOK-1 AA-гейт CLEAN, TOK-5 OS-prefs; TOK-2 accent≈semantic hue = 🚫 identity, mitig. not-by-color-alone |
| `css/base.css` | 🌱 | ✅ | ✅ | ✅ | ✅ | 🌱 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | 🌱BAS-15 tabs↔seg (HIER), 🌱BAS-21 today-hero-hint tertiary (4 AA-fail, min 2.96); BAS-17 helper-контраст = ✅ verified-clean; BAS-18 measure = 🚫 |
| `css/editorial.css` (мёртв) | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 🌱 | — | — | 🌱EDC-1 subset-proof ✅ р109: pre-split монолит, 9 uniques = box-sizing-дубли + обсолет pre-tabs-разметка → чистый историч-дубль, SAFE-TO-DELETE (ждёт ГО юзера, НЕ stranded как DSG-1) |
| `css/{linear,swiss}.css` (не подключены) | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 🌱 | — | — | 🌱DSG-1 ❌КОРР.р99: НЕ чистые дубли — linear держит `a`/`a:hover`, swiss `.ed-masthead-kicker`/`.ed-eyebrow` НЕ в base.css (частичный порт, как broadsheet) → port-or-abandon decision, НЕ удалять как junk |
| `css/broadsheet.css` (не дубль!) | 🌱 | — | — | — | — | — | 🌱 | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 🌱 | — | — | 🌱DSG-2 CONFIRMED: 13 структ.правил (nav/btn/link) НЕ в base.css (0 vs linear 2/swiss 6) → broadsheet теряет структ.акценты, токены живут |
| **▸ СКРИПТЫ** | | | | | | | | | | | | | | | | | | | | | | | | | | | | |
| `js/app.js` | ✅ | — | — | ✅ | ✅ | — | ✅ | ✅ | 🌱 | ✅ | ✅ | 🌱 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | 🌱 | ✅ | — | 🌱APP-8 regenerate без aria-busy; 🌱APP-9 comparison-table th без scope/caption; ⛔APP-5 favorite; v=54 |
| `js/stats.js` | — | — | 🌱 | ✅ | ✅ | ✅ | ✅ | — | ✅ | — | ✅ | — | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | — | ✅ | ✅ | ✅ | — | 🌱STJ-1 tick-подписи графиков мелкие (low); kbd/progress-dup c app.js = 🚫 standalone-by-design; v=12 |

**Как читать сводку.** Открытые 🌱-пункты по файлам: `settings.html`(SET-14/SET-7),
`stats.html`(STA-18/STA-5), `header.html`(HDR-1), `icons.html`(ICO-3),
`base.css`(BAS-15/BAS-21), `linear/swiss/broadsheet.css`(DSG-1/DSG-2 — overlay'и со stranded-правилами), `editorial.css`(EDC-1 — subset-proof ✅ р109: чистый историч-дубль, SAFE-TO-DELETE ждёт ГО),
`app.js`(APP-8/APP-9), `stats.js`(STJ-1).
⛔: `error.html`(ERR-3 403-retry), `app.js`(APP-5 favorite).
**⚠️ КОРР. р99 — overlay'и НЕ «можно удалить как junk»:** прежняя рекомендация «EDC-1+DSG-1 удалить мёртвые
`editorial/linear/swiss.css`» ОТОЗВАНА. Селектор-diff (р99) показал: linear/swiss держат element-правила
(`a`/`a:hover`, `.ed-masthead-kicker`/`.ed-eyebrow`) НЕ в `base.css` — частичный порт, как broadsheet (DSG-2).
Все 4 overlay'я не подключены (design-switch = `data-design`-атрибут, ноль динам. CSS-загрузки), но держат
непортированные design-акценты → удаление = 0 рендер-дельты, но теряет source восстановления fidelity.
**Не механический cleanup: DSG-1=DSG-2 = один класс port-or-abandon (дизайн-решение юзера).** Остальные 🌱 —
в blocked-файлах под активным pass пользователя → ждут чистого `git status`. Расшифровка каждого ID — §6 ниже.

**Из аудита 2026-07-06/07/07-р99:** DSG-1+DSG-2 — **ОДИН класс (CONFIRMED селектор-diff'ом):** все non-editorial
overlay'и (`linear`/`swiss`/`broadsheet.css`) держат design-specific element-правила, лишь ЧАСТИЧНО портированные
в `base.css`, и `base.css` имеет НОВЫЕ правила (`label:has(checked)`, `.option-*`), которых у overlay нет → overlay
stale+partial. Дизайны рендерятся БЕЗ overlay-акцентов (linear без link-цвета, swiss без kicker/eyebrow, broadsheet
без 13 nav/btn-правил). Токены живут через `tokens.css`. Решение: port-to-base (вернуть fidelity) ИЛИ accept
token-only → затем удалить. **EDC-1 (editorial.css) — subset-proof ✅ ВЫПОЛНЕН (р109): пре-split монолит (621 sel), лишь 9 editorial-only vs base+tokens (817 sel) = box-sizing-дубли + обсолет pre-tabs-разметка (.control-card/.settings-content/.app-layout) → ноль live-stranded, чистый историч-дубль, SAFE-TO-DELETE (в отличие от DSG-1) — ждёт лишь ГО юзера.** APP-9 — JS-построенная comparison-table без `th[scope]`/`caption`. Всё в §6.

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
| FT-9 | `.flashcard-grade-buttons` (Не помню…Легко) | **Резолвлено статически (р107):** «цветовая шкала» МИНИМАЛЬНА — resting-состояние ВСЕХ 4 кнопок нейтрально (`bg-secondary`+`text-primary`, base.css:1046), семантический wash ТОЛЬКО на hover и ТОЛЬКО grade-1/grade-4 (grade-2/3 нейтральны всегда). (1) **1.4.1 color-not-alone:** смысл в ТЕКСТЕ — «Не помню/Трудно/Хорошо/Легко» + `aria-label="Оценка N: …"`, `role=group`+`aria-labelledby`; цвет не единственный канал. (2) **Порядок цвет↔смысл:** grade-1(худшее)→error-wash, grade-4(лучшее)→success-wash = интуитивно (red=трудно, green=легко). (3) **1.4.3 контраст:** text-primary/bg-secondary MIN 11.38:1; hover text-primary/error-wash 11.87:1, /success-wash 11.69:1 (по всем токен-блокам) — все ≫ AA 4.5 (≈AAA). Не баг | ✅ verified-clean, правки нет | M | L | L | H | ✅ DONE (р107, static) | CO-4 |
| FT-10 | `.options[role=radiogroup]` selected-до-проверки | Нейтральный ring/border (без green/red) до submit | — | H | L | L | H | ✅ DONE 🔁 | AF-2/3 |
| FT-11 | option label: CSS-бейдж `counter(upper-latin)` + срез «X. » | Буква рисуется CSS, видимый span чистится; aria-label с буквой | — | M | L | L | H | ✅ DONE | A11Y-1 |
| FT-12 | option fairness: длина/наполненность выдаёт correct | Контент-перекос; UI обязан не усиливать | seed/mcq pedago-loop | H | — | — | H | 🏛 CONTENT | AF-1 |
| FT-13 | option layout: adaptive single/2-col по длине (fairness) | ✅ Реализовано пользователем (р39): app.js `updateAnswerLayoutMode` (1157-1179) меряет длину видимого текста вариантов, `lengthRatio=max/min`; `forceSingleColumn = ratio > 1.4 (ANSWER_LAYOUT_SKEW_THRESHOLD) OR inline-code сконцентрирован в одной карточке` → `data-answer-layout=single|balanced`. base.css:2913-2936: дефолт 1 колонка (честно при skew), `[data-answer-layout=balanced]`→2×2 только для сопоставимых, `:has(.option-explanation)`→снова 1 колонка после ответа. Ровно бриф #3-5 (single default, 2-col лишь при близких длинах, авто-порог) | H | L | L | H | ✅ DONE-BY-USER (р39) | AF-4 |
| FT-14 | inline-`code` в варианте = визуальная подсказка | Гасим заливку/паддинг чипа в тексте выбора (моно остаётся, color inherit); explanation/проза не тронуты | M | L | L | M | ✅ DONE (р17 v=54) | AF-5 |
| FT-15 | `training-actions` include (Проверить ответ) | CTA далеко при длинных вариантах | sticky/видимый submit | M | M | M | M | 🌱 BACKLOG | TR-4 |
| FT-16 | empty-state «Сейчас нет вопросов» (было 5 веток → **4 живые + 1 мёртвая AI**) | Нейтральный заголовок + recovery (finished→«Посмотреть итоги»). **Empty-state аудит р101: 4 живые ветки (review/filter/session-run/session-fin) — взаимоисключающие, контекстные, с recovery-действиями (weighted primary/secondary) → образцово. ⚠ 5-я ветка (`generationUnavailable`, стр.166 «AI временно недоступен») теперь МЁРТВАЯ post-AI-removal → см. AIR-6** | — | M | L | L | H | ✅ DONE (р15 `3bb19db0`) · ⚠ AIR-6 | TR-2 |
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
| RES-11 | `#extra-analysis-toggle-result` hidden→JS reveal | Без JS не показывается (нет мёртвого контрола). **⚠ при JS тоггл (стр.113, НЕ гейтится `aiEnabled`) зовёт битый флоу → всегда ошибка, см. AIR-1/AIR-2** | — | M | L | L | H | ✅ DONE (no-JS-состояние) · ⚠ AIR-1 | TR-9 |
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
| SUM-9 | `.summary-score` без заголовка (HEADO nav-gap) | 3 контент-секции («Результаты по темам»/«Ошибки»/«Рекомендации») несут `<h2 class=summary-section-title>`, но карточка счёта (главный результат: mode+accuracy+counts+duration) — НЕТ → SR-навигация по заголовкам (H) перепрыгивала счёт. Порядок h1→h2… был монотонным (не баг order), но самое важное не имело якоря | Добавлен `<h2 class="summary-section-title visually-hidden">Результат сессии</h2>` в начало `.summary-score`. Паритет с сёстрами (тот же класс), БЕЗ `aria-label`/`aria-labelledby` на `<section>` (не плодим region-landmark на 1 карточке из 4). Чистый HTML → без version-bump; visually-hidden → 0 визуал-дельты, без screenshot regen. `buildShareText` не читает h2 → JS не задет | M | L | L | H | ✅ DONE (р104, `session-summary.html`) | HEADO |

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
| HDR-7 | stale-комментарий design-toggle: «циклит Editorial→Swiss→Linear→Broadsheet» (4 дизайна, неверный порядок) при фактическом JS-цикле из 10 (`window.__design.list`: editorial→linear→swiss→notion→mintlify→broadsheet→superhuman→stripe→claude→theverge) — рудимент 4-дизайн-эпохи, вводит в заблуждение мейнтейнера | Переписан на drift-proof формулировку: «циклит по ПОЛНОМУ списку `window.__design.list`, сейчас 10 (…)» — описывает МЕХАНИЗМ, не замороженный порядок (не сгниёт при 11-м дизайне). Чистый комментарий (0 CSS/JS/поведения, без бампа), non-blocked header.html | L | L | L | H | ✅ DONE (р102, comment-only, `commit ниже`) | — |

### 6.B.3 — `fragments/icons.html`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| ICO-1 | 20 монохромных Lucide-символов через `currentColor` | Единый icon-layer, обе темы | — | M | L | M | H | ✅ DONE | IC-1 |
| ICO-2 | `.icon-sprite` (absolute, 0×0) hide | Канон-hide SVG-спрайта (не display:none — рвёт `<use>` в Safari); убрал 159px мёртвого отступа | — | M | L | L | H | ✅ DONE | — |
| ICO-3 | нет dedicated warning-иконки (danger исп. `#i-flag`). **Уточнено р110:** `#i-flag` = double-duty на 3 сайтах — `settings.html:266` «Опасная зона» (danger) + `result.html:124` и `focus-training.html:170` «Сессия завершена» (finish-line). ВСЕ три `aria-hidden="true"` (декоративные) → смысл несёт ТЕКСТ, иконка = визуальный акцент. Один символ для «финиш» (позитив) и «опасность» (негатив) — мягкая семантич-полисемия, но **НЕ a11y-баг** (decorative) | Намеренный reuse flag для danger-zone; distinct warning-символ = опц. визуал-полиш, только с IC-4 + требует settings.html (blocked). Не срочно | L | L | L | M | 🌱 BACKLOG (decorative, opt-polish с IC-4) | IC-4 |
| ICO-4 | кнопки экспорта несли сырой юникод «↓» (не icon-система, рендер OS-зависим, стоял ПОСЛЕ текста) | Добавлен `#i-download` (Lucide, 21-й символ), swap «текст ↓»→«`<svg.ed-icon>` текст» (SVG + icon-lead как сиблинги). Спрайт+шаблон, без бампа. Контракт-тесты (Экспорт JSON/CSV текст + data-export-format) целы | L | L | L | H | ✅ DONE (р34) | IC-4 |
| IC-5 | CTA-кнопки несут сырой юникод «→» (SR-шум + вне icon-системы). **Точный скоуп (р38-аудит):** 7 `btn next-btn` — `today-widget.html:46,47` («Начать повторение →»/«Учить новое →»), `result.html:107` («Следующий вопрос →»), `session-summary.html:37,38` («Повторить ошибки →»/«Продолжить →»), `stats.html:99`, `error.html:52` («На главную →»). Остальные 40 «→» в grep = комменты/JS/проза (НЕ трогать) | **⚠️ БЛОКЕР — convention-решение (р38):** не механический swap. Все icon-кнопки ВЕДУТ иконкой (icon-lead, R34-конвенция); directional «next→» семантически ТРЕЙЛИТ → iconify создаёт mixed lead/trail. 3 варианта, каждый taste-call: (a) trail arrow-icon (ломает lead-конвенцию); (b) lead «→ Следующий» (семантически странно); (c) убрать «→» совсем (минимализм, но снимает намеренную консистентную аффордансу). «→» консистентен на ВСЕХ CTA → вероятно ОСОЗНАННЫЙ паттерн. Нужен выделенный design-decision раунд в СПОКОЙНОЙ среде (visible-delta → screenshot regen settings/stats/error + by-construction result/summary), НЕ casual sweep под нагрузкой. **2.4.4-вердикт (р117):** Link Purpose (In Context) = CLEAN — цель ссылки ЯСНА из текста ПЕРЕД «→» на всех CTA («Перейти к теме», «Начать тренировку», «На главную»…); стрелка = сырой текст-глиф (не CSS `::after`, не aria-hidden, не VAGUE-«здесь»), НЕ носитель смысла → 2.4.4 НЕ нарушен. «→» = чистая directional-аффорданса + минимальный SR-шум, а НЕ a11y-баг → подтверждает «вероятно осознанный»; iconify/удаление остаётся taste-decision, не WCAG-необходимость. Консистентный-или-никак: partial iconify только незаблокированных файлов (session-summary/error/today-widget) фрагментирует консистентность (blocked: result/stats/focus) → делать РАЗОМ или не делать | M | M | M | L | 🌱 BACKLOG (design-decision; 2.4.4 CLEAN р117) | IC-4 |

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
| PAC-2 | `#extra-analysis-toggle` + `#extra-analysis-content` sink ПОД кнопкой | Раскрытый контент идёт под триггером (не сиротит кнопку); aria-controls/expanded. **⚠ тоггл НЕ гейтится `aiEnabled` (стр.9) + флоу битый → см. AIR-1/AIR-2** | — | M | M | M | H | ✅ DONE (структура) 🔁 · ⚠ AIR-1 | TR-9 |

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
| TAC-3 | `.keyboard-hint` (символ ⌨ убран) | Текст самодостаточен, без цветного эмодзи вне icon-системы. **Контраст резолвлен статически (р107):** hint = `color:var(--color-text-secondary)` (base.css:992, единственная декларация; блок 3662 = только `display:none` на узком брейкпоинте) на `--color-bg-primary` (body), font-size-xs = small-text → порог AA-normal 4.5:1. Посчитал text-secondary↔bg-primary по всем 21 токен-блокам (10 дизайнов×2 темы+editorial-варианты): **MIN 5.93:1 (notion-dark), ВСЕ PASS** — margin к 4.5 комфортный. Контраст НЕ баг | ✅ verified-clean, правки нет | L | L | L | H | ✅ DONE (р107, static contrast) | TR-3 |

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
| EDC-1 | не подключён ни одним `<link>` (свер. 2026-07-07: link/import/test/JS/build-ссылок нет; `TemplateFragmentContractTest` читает только `tokens.css`+`base.css`, editorial.css не пиннит) | editorial.css = **пре-split монолит** (173K, 578 блоков: `:root`-токены + `html[data-design]`-база + `.ed-*`-компоненты в одном файле) — предок нынешних `tokens.css`+`base.css`. Путает чтение/поиск | **✅ SUBSET-PROOF ВЫПОЛНЕН (р109):** селектор-set-diff editorial.css(621 sel) vs `base.css`+`tokens.css`(817 sel) → **всего 9 editorial-only селекторов**, и ВСЕ 9 = либо (a) функц-дубли box-sizing (`html[data-design] *`/`::before`/`::after` — base.css:76 держит `box-sizing:border-box`), либо (b) **ОБСОЛЕТ pre-redesign-разметка** (`.settings-page .control-card`/`.settings-content`/`.app-layout`/`.main-content`/`.card`, `.result-page .option-other`) — заменена tabs-редизайном `0eb762a5` (`.settings-shell`/`.settings-tab` в base.css); текущие шаблоны эти классы НЕ эмитят. → **ноль live-релевантных stranded-правил.** Категорически ОТЛИЧАЕТСЯ от linear/swiss (DSG-1: держат УНИКАЛЬНЫЕ current design-signature вроде link-цвета/kicker, которых в base НЕТ). editorial = **чистый pre-split историч-дубль, безопасен к удалению** (0 рендер-дельты + 0 потери fidelity: «unique» биты целят в несуществующую разметку). Осталось лишь ГО юзера (r99-политика: overlay-удаление = решение юзера; 173K = консеквентно) | L | L | L | H | 🌱 SAFE-TO-DELETE (subset-proof ✅ р109; ждёт ГО юзера, НЕ stranded как DSG-1) | — |

### 6.C.4 — `static/css/{broadsheet,linear,swiss}.css`

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| DSG-1 | `linear.css` + `swiss.css` НЕ подключены ни одним `<link>` (свер. 2026-07-06/07: `head.html` грузит только `tokens.css` v=61 + `base.css` v=69; ноль per-design `<link>`; design-switch = чистый `data-design`-атрибут, app.js:713/stats.js, ноль динамической загрузки CSS) | **❌ КОРРЕКЦИЯ 2026-07-07 (р99): НЕ «чистые дубли».** Прежняя запись «сигнатуры УЖЕ портированы, удалить» — НЕВЕРНА. Селектор-уровневый diff overlay↔base.css: **`linear.css` держит `html[data-design=linear] a` + `a:hover` (link-treatment), которых в `base.css` НЕТ** (там только `body`{font-features} + `#interview-options label:has(checked)`); **`swiss.css` держит `.ed-masthead-kicker`/`.ed-eyebrow` (типографика kicker/eyebrow), которых в `base.css` НЕТ** (там `a:hover`/`.zone-chip`/`label:has`/`.option-correct,wrong`). Порт был **ЧАСТИЧНЫМ**, и в `base.css` есть НОВЫЕ правила (`label:has(checked)`, `.option-*`), которых у overlay НЕТ → overlay **stale + частично-портирован**, ровно как `broadsheet.css` (DSG-2). → **DSG-1 и DSG-2 — ОДИН класс:** все 3 non-editorial overlay'я держат design-specific element-правила, лишь частично мигрированные в `base.css`; т.к. overlay'и не грузятся, эти дизайны СЕЙЧАС рендерятся БЕЗ своих element-акцентов (linear без кастомного link-цвета, swiss без kicker/eyebrow-типографики). | **НЕ механический cleanup.** Удаление меняет 0 рендера (overlay не подключён), но теряет source восстановления fidelity. Решение port-or-abandon (как DSG-2): либо портировать stranded element-правила в `base.css` (вернуть fidelity), либо осознанно принять token-only-дизайны → ТОГДА удалить overlay. Требует дизайн-решения юзера, НЕ «удалить как junk» | L | L | M | H | 🌱 BACKLOG (port-or-abandon decision, как DSG-2) | — |
| DSG-2 | **CONFIRMED статически (2026-07-07, app не нужен):** `broadsheet` теряет структурную identity. `base.css` = **0** правил `[data-design="broadsheet"]` (editorial=30, swiss=6, linear=2, а также stripe=3/claude=3 портированы), но `broadsheet.css` (не подключён) содержит **13 структурных rule-блоков** на `.ed-nav`/`.ed-nav-link`/`.btn`/`.secondary-btn`/`.next-btn`/`.danger-btn`/`.flashcard-*-btn`/`#extra-analysis-toggle`/`a` (nav-treatment, `border-radius:4px`, Hudson-blue `#41A1CF` бордюры). Broadsheet — ЕДИНСТВЕННЫЙ из 4 дизайнов с dedicated `.css` (editorial/linear/swiss/broadsheet), чью структуру НЕ мигрировали в `base.css` (нет `superhuman/theverge/notion/mintlify.css` — те born-token-only, для них 0 = норма) | При выборе `broadsheet` **токены рендерятся** (cream bg / obsidian buttons / serif — из `tokens.css` `[data-design=broadsheet]`×2), но 13 структурных акцентов ОТСУТСТВУЮТ → broadsheet = «свои токены на editorial-структуре», без Hudson-бордюров/nav-treatment. Severity M→L (identity частично живёт через токены; теряются тонкие акценты) | Портировать 13 rule-блоков из `broadsheet.css` в `base.css` (token-safe, ровно как сделали для linear/swiss) → затем `broadsheet.css` становится дублем → удалить с DSG-1. **Blocked: `base.css` под in-flight pass; + дизайн-решение (broadsheet как signature-дизайн vs демоут в token-only)** | M | L | M | H | 🌱 BACKLOG (blocked-file + decision) | — |

### 6.D.1 — `static/js/app.js` (v=54)

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| APP-1 | answer-flow (submit→verdict→sink) | aria-live politeness по kind, focus-return | — | M | L | L | H | ✅ DONE (р7/р11) | A11Y-2/4 |
| APP-2 | пост-ответный `appendAnalysisBlock` в sink | takeaway/трейс/related под кнопкой. **⚠ 2026-07-07: сам flow БИТ AI-removal — все 4 fetch'а зовут удалённые endpoints → см. AIR-1. DONE относится к a11y-механике sink (aria-live/focus), НЕ к работоспособности флоу** | — | M | M | M | H | ✅ DONE (механика) 🔁 · ⚠ AIR-1 | TR-9 |
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

### 6.E — AI-REMOVAL FALLOUT (фронт-осколки удаления AI-бэкенда)

> **Контекст.** Задача пользователя «убрать OpenAI/DeepSeek, ответы только из seed» выполнена в коммитах `b4ee4ffc` + `e323aac4`: `InterviewApiController` **удалил** endpoints `POST /api/wrong-feedback`, `GET /api/takeaway`, `GET /api/comparison`, `GET /api/code-trace`, `POST /api/regenerate` (+ hint). Фронт всё ещё их зовёт. **Верифицировано по on-disk (в т.ч. in-flight WIP пользователя) 2026-07-07, р99.** Правка ВСЕХ пунктов ниже требует `app.js` + шаблонов → **всё под активным pass пользователя (blocked-file)**; плюс AIR-1 несёт продуктовое решение (A/B/C). Пока — только фиксируем; фикс, когда дерево осядет.

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| AIR-1 | «Показать доп. анализ» → гарантированный dead-end на КАЖДЫЙ клик | `app.js` flow (340-460): `TAKEAWAY` (395) и `CODE_TRACE` (410) пушатся **безусловно** → `requests.length ≥ 1` даже на верном ответе (на неверном +`WRONG_FEEDBACK` 358 +`COMPARISON` 376). Все 4 endpoint'а **удалены** → каждый `apiFetch` reject → `failedCount === requests.length` (435) → всегда ветка полного провала: `setInlineAlert('Не удалось загрузить доп. анализ. Попробуй ещё раз.')`, кнопка сбрасывается в retryable, повтор = та же ошибка. **Прод-регрессия: заметный post-answer CTA всегда падает.** Решение (нужен выбор пользователя): **(A)** гейтить оба тоггла на `aiEnabled` → в seed-first (`aiEnabled=false`) кнопка не рендерится (просто, но теряет related-reveal, см. AIR-3); **(B, реком.)** выкинуть 4 мёртвых `apiFetch` из `app.js`, оставить только reveal похожих вопросов, переименовать кнопку в «Показать похожие вопросы»; **(C)** удалить тоггл+флоу целиком. | Реком. B (сохраняет единственную выжившую ценность — related) | H | M | M | H | 🌱 BACKLOG (blocked-file + decision A/B/C) | TR-9 |
| AIR-2 | оба тоггла доп.анализа НЕ гейтятся `aiEnabled` | `#extra-analysis-toggle` (`post-answer-controls.html:9`, focus-флоу) и `#extra-analysis-toggle-result` (`result.html:113`, /answer) рендерятся **безусловно** (в отличие от `.btn-regenerate` result.html:49 и `.question-side` :128, которые под `th:if=${aiEnabled}` → уже скрыты). → битая кнопка (AIR-1) видна в ОБОИХ флоу. Часть решения AIR-1 (вариант A правит именно это) | Вместе с AIR-1 | H | L | L | H | 🌱 BACKLOG (blocked-file) | AIR-1 |
| AIR-3 | выжившие «Похожие вопросы» подавлены битым флоу | reveal `related.classList.remove('hidden')` (`app.js:447`) — в ветке **НЕ-полного-провала**; т.к. все fetch'и падают (AIR-1), ветка недостижима → единственная выжившая доп.аналитика (related-вопросы, backend `RelatedQuestionsService` ЖИВ) никогда не показывается через эту кнопку. Вариант B (AIR-1) это чинит | Вместе с AIR-1 (реком. B) | M | L | L | H | 🌱 BACKLOG (blocked-file) | AIR-1 |
| AIR-4 | `regenerateQuestion` (app.js:1047-1115) зовёт удалённый `POST /api/regenerate` | Хендлер + делегирование от `.btn-regenerate` (1126) живы в JS, но сама кнопка под `th:if=${aiEnabled}` (result.html:49) → в seed-first **не рендерится** → путь **недостижим** (не user-facing). Мёртвый JS к будущей чистке (не срочно). Отдельно APP-8 (async-state a11y) — теперь moot, пока кнопка скрыта | Удалить при чистке app.js (низкий приоритет) | L | L | L | H | 🌱 BACKLOG (blocked-file, dead-not-reachable) | APP-8 |
| AIR-5 | резолв ранее-подозреваемых пунктов | `RES-4` (regenerate-btn) и `RES-14` (question-side CODE-пин) — оба `th:if=${aiEnabled}` → навсегда скрыты в seed-first, контент НЕ теряется (`RES-14`: код всегда в `.answer:82`). **Не баги, действий нет** — фиксируется как закрытая ветка fallout | — | — | — | — | H | ✅ verified-not-a-bug | RES-4/RES-14 |
| AIR-6 | мёртвая AI-ветка в empty-state focus-training | `focus-training.html:166` — `<p th:if=${generationUnavailable}>«AI временно недоступен. Обнови страницу через несколько секунд.»`. **`generationUnavailable` хардкод `false`** (`FocusTrainingPageService:54`, без переприсваивания до передачи на :94 — бывший AI-fail путь удалён) → ветка **НИКОГДА не рендерится**. Побочно: guard'ы `!generationUnavailable` на стр.167-170/172 всегда-истинны → вестигиальны (безвредны). 4 живые ветки empty-state (review/filter/session-run/session-fin) целы и корректны (см. FT-16/FT-18). **Вред: (а) AI-упоминание в seed-first приложении, (б) мёртвый код/сложность.** НЕ user-facing (не показывается). Направление: удалить стр.166 + распутать `!generationUnavailable` guard'ы (упростить до безусловных) — часть B-cleanup AI-хвостов. **focus-training.html под in-flight pass (blocked)** | Удалить при AI-cleanup | L | L | L | H | 🌱 BACKLOG (blocked-file, dead-not-rendered) | TR-2 |

### 6.F — ROUTE / ACTION INTEGRITY (frontend→backend, полный аудит р100, 2026-07-07)

> **Метод (read-only, static).** Сопоставил ВСЕ frontend-точки вызова (app.js/stats.js `apiFetch`/`fetch`/inline + шаблонные `th:action`/`th:href="@{...}"`) с ЖИВЫМ набором маршрутов (`@*Mapping` в 4 контроллерах: `InterviewMvcController`, `InterviewApiController`, `ExportController`, `GlobalExceptionHandler`). Цель — вылов осиротевших вызовов ПОМИМО AI-кластера (§6.E).
>
> **Живой набор маршрутов (11 GET + 10 POST):** GET `/ · /training · /review · /settings · /stats · /session-summary · /export · /api/streak · /api/stats · /api/topic-stats · /api/next`; POST `/start · /study-confirm · /flashcard-reveal · /flashcard-grade · /finish · /answer · /settings/reset-options · /api/favorite · /api/answer · /api/confidence`.

| # | Пункт | Состояние / проблема | Направление | Imp | Risk | Eff | Conf | Статус | ↔ |
|---|-------|----------------------|-------------|-----|------|-----|------|--------|---|
| RIA-1 | forward-аудит: битые вызовы фронта | **CLEAN-BILL.** Каждая frontend-точка вызова бьётся в живой маршрут, КРОМЕ ровно 5 AI-удалённых (`/api/regenerate·wrong-feedback·takeaway·comparison·code-trace` = §6.E). Проверены: `/api/answer·next·stats·topic-stats·confidence·favorite·streak` (7 живых API), `/answer·/finish·/review·/export` (inline app.js), шаблонные `/·/stats·/settings·/start·/flashcard-reveal·/flashcard-grade·/settings/reset-options` — все ✅. → **никаких НОВЫХ сирот сверх AIR**; класс «битая кнопка» закрыт | — | — | — | — | H | ✅ verified-clean (кроме AIR) | AIR-1 |
| RIA-2 | reverse-орфан: `POST /study-confirm` без вызывающего | Живой маршрут `studyConfirm` (`InterviewMvcController:157` → `InterviewFlowMvcService:49` → `applyStudyConfirm` → focus-redirect), но **НИ один шаблон/JS не постит на него** (grep `study-confirm` по static+templates = 0). В STUDY-режиме focus-training рендерит ветку `!sessionFlashcard` (browse `<details>`, стр.82-89) — reveal БЕЗ POST и БЕЗ advance-контрола; `/answer`-форма скрыта (`th:if=${!flashcardMode}`). → либо `/study-confirm` **вестигиальный** (STUDY продвигается иначе — вероятно app.js `/api/next`), либо в STUDY нет кнопки «изучил → дальше». **НЕ фронт-фикс:** это backend-маршрут/бизнес-флоу (правка API запрещена) + требует live-проверки STUDY-режима (app сейчас down; старт сессии = SRS-загрязнение). Действие: при живом app пройти STUDY-режим, решить с пользователем — удалить вестигиальный маршрут ИЛИ добавить advance-контрол | Live-verify STUDY + решение пользователя | M | L | M | M | ⛔ DEFERRED (needs live+decision, out-of-frontend-scope) | TR |
| RIA-3 | reverse: `/training` GET, `/session-summary` GET не залинкованы | Не баг: `/session-summary` — redirect-таргет POST `/finish` (рендерится там, не линкуется — by design); `/training` — bookmarkable/redirect-вход focus-страницы (`MvcNavigationService:32` → view `focus-training`), достижим прямой навигацией. Обе достижимы не через `<a>` | Действий нет | — | — | — | — | H | ✅ verified-not-a-bug | — |

---

## 7. Тематический cross-cutting индекс (тот же бэклог по областям)

Свод ID по темам — быстрый «worst-first» вход. Подробности и статусы — в §5
(колонка ↔ ведёт обратно).

| Область | Открытые (🌱/⛔) | Закрытые (✅) | Не трогать (🚫) | Внешние (🏛) |
|---------|------------------|---------------|-----------------|--------------|
| **AF** answer-fairness | — (ядро закрыто) | AF-2/3 (нейтр. selected), AF-4 (single-column уже, р19), AF-5 (мягкий code, р17), AF-6 (correct помечен), AF-7 (muted wrong, р18) | AF-8 (иконки до проверки) | AF-1 (паритет длины) |
| **TR** тренировка | TR-1/HDR-1 (sticky header), TR-3 (hint), TR-4 (sticky CTA), TR-6 (микрокопия прогресса) | TR-2 (empty), TR-5 (zone-hint), TR-7 (kbd-гейт), TR-9 (post-answer sink-механика; сам flow БИТ→AIR-1) | TR-8 (48px) | — |
| **AIR** AI-removal fallout | AIR-1 (доп.анализ dead-end, реком.B), AIR-2 (тогглы без aiEnabled-гейта), AIR-3 (related подавлены), AIR-4 (мёртвый regenerate JS), AIR-6 (мёртвая AI-ветка empty-state) | AIR-5 (RES-4/RES-14 — not-a-bug) | — | — |
| **RIA** route-integrity | RIA-2 (`/study-confirm` reverse-орфан ⛔ live+decision) | RIA-1 (forward clean-bill, кроме AIR), RIA-3 (`/training`·`/session-summary` — not-a-bug) | — | — |
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
| **IC** иконки | IC-5 (сырой «→» на CTA), IC-4-part (empty/insights) | IC-1/2, ICO-4 (export «↓»→`#i-download` SVG, р34); ICON sprite = 0 orphan (р110) | IC-3 (намеренные эмодзи) | — |
| **NTC** non-text contrast (1.4.11) | NTC-2 (resting control-border sub-3:1 в 8/10 дизайнов — decision-gated, нужен новый `--color-border-input`, НЕ бампать общий border-primary; р111) | NTC-1 (focus-ring ≥3:1 все 21 блока, min 3.49; р111) | — | — |
| **FORM** label↔контрол (1.3.1/3.3.2/4.1.2) | — | FORM-1 (все видимые контролы settings/stats/focus имеют программное имя: wrapping-label+текст ИЛИ aria-label; р112) | — | — |
| **LIN** label-in-name (2.5.3) | — | LIN-1 (10 элементов: 8 visible⊆aria-label + 2 font-stepper A−/A+ icon-glyph exempt; р113) | — | — |
| **MOTION** reduced-motion (2.3.3) | — | MOTION-1 (CSS universal-kill 2 пути + JS scroll три-state off>on>system; 1 keyframes + 1 scroll покрыты; р114) | — | — |
| **STATUS** status-messages (4.1.3) | — | STATUS-1 (9 live-регионов wired + politeness верна + 3 opt-out документированы; `.hidden`-класс не `hidden`-атрибут; р115) | — | — |
| **IMG** non-text content (1.1.1) | — | IMG-1 (все иконки декоративны+`aria-hidden` — static-шаблоны И JS `icon()`/`svgIcon()` хардкодят его; ноль `<img>`; icon-only контролы несут `aria-label`-АТРИБУТ, переживающий `innerHTML=icon()`-своп состояния; canvas-графики+`.forecast-bar[role=img]` → descriptive `aria-label`+«данные в таблице ниже»; р116) | — | — |
| **LINK** link purpose in context (2.4.4) | — | LINK-1 (все 35 `<a>` в 6 стр несут purpose-clear текст; ноль VAGUE-«здесь»/arrow-only/empty-nolabel; trailing «→» всегда за самодостаточным текстом = не носитель смысла; topic-ссылки в таблицах = имя темы + `title`-слаг; р117) | IC-5 (raw «→» = design-consistency/SR-шум, НЕ 2.4.4-баг — design-decision, консистентно-или-никак) | — |
| **FOCUS** focus visible (2.4.7) | — | FOCUS-1 (глобальный `:focus-visible`=2px outline; КАЖДЫЙ `outline:none` = mouse-only-подавление [keyboard-ринг цел через `:focus-visible`/`:has(input:focus-visible)`] ИЛИ container-focus-таргет [`#main-content`/`.settings-panel` skip/tab-цели] ИЛИ замена на `box-shadow:shadow-focus` [реальный ринг]; forced-colors восстанавливает `outline:Highlight` для всех box-shadow-only; ноль `box-shadow:none`-на-фокусе = ноль истинных подавлений; р118) | — | — |
| **KBD** keyboard operable (2.1.1/2.1.2) | — | KBD-1 (кастомные ARIA-виджеты полны по WAI-ARIA: `role=tablist` ←→↑↓/Home/End+roving-tabindex+activate-panel; seg-`role=radiogroup` ←→↑↓+roving+select; sortable-`th` Enter/Space+`aria-keyshortcuts`+focusable; нативные radio/button/link/select — клавиатура из коробки; модалки Escape+escapable focus-trap [inert-фон восстанавливается] = no-trap 2.1.2; бонус-шорткаты цифры/«?»/«/»; р119) | — | — |
| **REFLOW** reflow / h-overflow (1.4.10 + 1.4.4) | (RE-1/AF-4 mobile-1-col = layout-выбор, отдельно) | REFLOW-1 (viewport разрешает zoom [нет `user-scalable=no`/`max-scale`]; ноль фикс-`width`≥320 контейнеров [fluid `max-width:100%/560px/68ch`]; `overflow-wrap:break-word/anywhere` повсеместно на прозе/лейблах/заголовках + flex `min-width:0`; 2D-контент [таблицы/код/диаграммы] в явных `overflow-x:auto` боксах = скролл внутри, не рвёт страницу; нет `body overflow-x:hidden`=защита у источника; corrob. live 375/768/1280 clean; р120) | — | — |
| **TEXTSPACE** text spacing (1.4.12) | — | TEXTSPACE-1 (`line-height` ВЕЗДЕ unitless [ноль px] → масштабируется под user-override 1.5×; ноль `-webkit-line-clamp`; фикс-`height` ТОЛЬКО на не-тексте [checkbox 18/chart-wrapper 320/bars/icon-toggle 40]; текст-контейнеры на min-height/auto; `overflow:hidden` лишь radius-clip прогресс-треков+`.seg-control`, off-screen `.visually-hidden`, 1 ellipsis-бейдж [`max-width:100%`, инфо дублируется в ссылке] — ни один не клипает растущий текст вертикально; р121) | — | — |
| **CONSIST** consistent nav+id (3.2.3/3.2.4) | — | CONSIST-1 (nav + глобальные тогглы в ЕДИНОМ `~{fragments/header}` на всех 6 стр → консистентность гарантирована АРХИТЕКТУРНО [нет per-page копии для дрейфа]; порядок Фокус→Аналитика→Настройки идентичен; `activePage` лишь `is-active`+`aria-current=page` [не реордер/не добавляет]; toggle-компоненты layout/design/theme = один источник → идентичные `aria-label`/`title`/иконка везде; export дедуп-нут в /settings [убран битый header-дубль]; р122) | — | — |
| **FNO** focus not obscured (2.4.11 AA) | 🌱FNO-1 (sticky stats-`thead` `top:0` НАД focusable row-links [`th[scope=row].topic-name>a`] + НИГДЕ нет `scroll-margin/padding-top` → Tab-фокус row-link может уйти под липкую шапку [класс. 2.4.11]; + split-layout ≥1200px sticky `.focus-question`/related-rail — нужна live-Tab; фикс `scroll-margin-top≈thead-height` = CSS→blocked; р123) | FNO-clean (ГЛАВНЫЙ риск чист: site-header/nav НЕ sticky; модалки `fixed inset:0` focus-trapped = сам фокус-контекст, не obscure; related-rail в отд. grid-колонке 2 — не оверлапит col-1) | — | — |

**DUP / duplicate-id (свод-аудит 2026-07-07, р100):** ✅ **CLEAN** — первый явный проход на риск «дубль id от мульти-включения фрагмента» (ломает `aria`/label-ассоциации, невалидный HTML). Результат: ноль same-page дублей id. Паттерн корректен: (1) фрагменты с **хардкод-id** включаются **≤1×/страницу** — `header` ровно 1× на каждой из 6 страниц (id `layout/design/theme-toggle` уникальны per-page), `stats-grid`/`today-widget`/`icons`/`post-answer-controls`/`training-actions` — единожды (`stats-grid` wrapper вкладывает `stats-grid-content` внутри себя, НЕ второй инстанс); (2) фрагменты, потенциально включаемые **несколько раз** (`inline-alert`, `result-zone-head`), **ПАРАМЕТРИЗУЮТ** id — caller передаёт уникальный (`interview-alert`, `result-zone-head-main`). Инвариант для будущих правок: **hardcode-id ⟹ single-include; multi-include ⟹ param-id**. Матрица DUP-колонка (✅ у фрагментов) подтверждена этим evidence.

**SEM interactive-семантика + HEADO heading-иерархия (свод-аудит 2026-07-07, р101):** ✅ **CLEAN** во всех шаблонах. (A) **Интерактивные элементы:** ноль `onclick`/`role="button"` на не-`<button>`; ноль позитивного `tabindex` (>0); ВСЕ `<a>` имеют `th:href`/`href` (ноль JS-only мёртвых ссылок — `focus-training`/`result`/`stats` анкеры многострочные, href на след.строке подтверждён); ноль `<img>` (весь icon-layer = inline-SVG `<use>` с `aria-hidden`); ноль clickable `<div>`/`<span>` с data-handler'ами → каждое действие = настоящий `<button>` или `<a href>`. (B) **Heading-иерархия:** `header`-фрагмент эмитит ЕДИНСТВЕННЫЙ `<h1>`/страницу (`ed-masthead-title` = `pageTitle`), секции = `<h2>` (error 1×h2, session-summary 4×h2 — р104 добавил visually-hidden h2 «Результат сессии» на карточку счёта, SUM-9) → ровно один h1/страницу, ноль пропущенных уровней, консистентно на всех 6 страницах. Инвариант: **h1 = masthead `pageTitle` (в `header`-фрагменте), контент страницы стартует с h2.** `error.html` перечитан on-disk — polished (skip-link→`#main-content`, `main[tabindex=-1]`, декор eyebrow/error-code `aria-hidden`, per-status микрокопия, dev-`<details>`), правок нет; ERR-3 (403-retry) остаётся final-defer.

**DOC-A11Y: `lang` (WCAG 3.1.1) + skip-link (WCAG 2.4.1) — свод-аудит 2026-07-07, р105:** ✅ **CLEAN** на всех 6 страницах. (A) **Language of Page (3.1.1):** каждый `<html>`-корень (error/session-summary/focus-training/result/settings/stats) несёт `lang="ru"` (заодно `data-design="editorial"` как SSR-дефолт, потом JS-override по persisted-предпочтению) — ноль страниц без `lang`, ноль mixed-lang деклараций. Фрагменты `<html>` не эмитят (у них `<html xmlns:th>` без `lang` — корректно, они `th:fragment`, не документы). (B) **Bypass Blocks (2.4.1):** каждая из 6 страниц имеет `<a class="skip-link" href="#main-content">` первым фокусируемым элементом body, и на КАЖДОЙ ровно один элемент `id="main-content"` (`<main id="main-content" tabindex="-1">`) → skip-link всегда резолвится в валидный таргет, ноль битых якорей. Инвариант: **каждая страница = `<html lang="ru">` + skip-link→`#main-content` (единственный `main[tabindex=-1]`).** Метод: grep `<html`-тега + сверка `id="main-content"`-счётчика per-page (evidence, не память). Заполняет DOC-level a11y-покрытие рядом с DUP (р100) и SEM/HEADO (р101).

**DOC-A11Y продолжение: `<title>` (WCAG 2.4.2 Page Titled) — свод-аудит 2026-07-07, р106:** ✅ **CLEAN + deliberate convention.** Все 6 страниц имеют непустой описательный `<title>` (рендер `head.html:257` `<title th:text=${title}>`, аргумент из `head(title=…)` в каждой странице). Паттерн **«‹Раздел› — Подготовка к собеседованию»** у 5 подстраниц: `Ошибка —` / `Итоги сессии —` / `Результат —` / `Настройки сессии —` / `Аналитика —`. **Исключение — `focus-training.html` = голый бренд «Подготовка к собеседованию» без префикса, и это КОРРЕКТНО:** focus-training рендерится на `/` (подтверждено `MvcNavigationService.focusView()="focus-training"` + `focusRedirect()="redirect:/"`, nav `@{/}` activePage=focus) = **домашняя страница** → канон «home=бренд, подстраницы=‹Раздел› — бренд» (стандартная tab-title-конвенция). WCAG 2.4.2 PASS, ноль пустых/дублирующих титулов. Опциональный enhancement (более описательный/динамический титул домашней — напр. «Тренировка —» или тема вопроса) — **blocked** (`focus-training.html` в M-списке юзера) + **convention-decision** (менять home=бренд-канон = вкусовое решение юзера) → НЕ автономно. Инвариант: **home `/` = бренд-титул; каждая подстраница = «‹Раздел› — Подготовка к собеседованию».**

**ICON sprite-utilization — dead-symbol аудит (свод-аудит 2026-07-07, р110):** ✅ **CLEAN, ноль orphan-символов.** Спрайт `fragments/icons.html` = **21 `<symbol>`** (i-star, i-refresh, i-book-open, i-link, i-flame, i-flag, i-check, i-x, i-circle-check, i-circle-x, i-lightbulb, i-target, i-search, i-bot, i-chart, i-arrow-up, i-download, i-copy, i-printer, i-shapes, i-layout). Проверка «есть ли символ, который никто не рендерит» (dead-code / вес спрайта). **ЛОВУШКА р99 (наивный usage-grep даёт ложные orphan'ы):** литеральный grep `#i-<name>` по шаблонам пометил 6 символов (i-check, i-x, i-lightbulb, i-search, i-bot, i-chart) как «неиспользуемые» — но `app.js` строит href **динамически**: `icon(name)` (`app.js:49`, `'#i-'+name`) и `svgIcon(name)` (`app.js:2227`). Сверка **call-sites** (не литералов) подтвердила использование всех шести: `svgIcon('check')` (2275), `svgIcon('x')` (2279), `icon('lightbulb')` (369/1767), `icon('search')` (417/1850), `icon('bot')` (1762), `icon('chart')` (383/1818). Итог: **все 21 символа используются** — 15 через литеральный `<use href="#i-…">` в Thymeleaf, 6 через JS-хелперы `icon()`/`svgIcon()`. Ноль мёртвых символов → удалять нечего; icons.html verified-clean. Инвариант (переиспользовать при любом sprite-audit): **usage-скан ОБЯЗАН учитывать динамическую сборку `'#i-'+name` в `icon()`/`svgIcon()`, а не только литеральный `#i-NAME`-grep** — иначе ложно-положительные orphan'ы (та же ошибка, что р99 с overlay-CSS).

**NTC — Non-text Contrast (WCAG 1.4.11): фокус-ринг + границы контролов — свод-аудит 2026-07-07, р111.** Первый проход по non-text-контрасту (раньше делал только text 1.4.3: RES-8/TAC-3/FT-9). Две части:

- **(A) NTC-1 фокус-индикатор = ✅ CLEAN, все 21 блока ≥3:1.** Фокус = `base.css:230` `outline: 2px solid var(--color-border-focus)` (+ offset 2px; HC-режим 3px; для settings/stats-контролов системный `Highlight`). Посчитал `--color-border-focus` vs `--color-bg-primary` (страница) И `--color-bg-secondary` (карточка) по всем 10 дизайнам×2 темы (Python brace-split tokens.css). **МИНИМУМ — editorial-light 3.49:1 (vs карточка) / 3.85:1 (vs страница); ВСЕ 21 PASS** (порог 3:1). Фокус виден для клавиатурных юзеров во всех комбинациях. Часть токенов уже несли ручные аннотации 1.4.11 (superhuman `amethyst 6.2:1`, stripe `brand 6.1:1`/`5.6:1`) — подтверждено расчётом + распространено на ВСЕ блоки. Инвариант: **`--color-border-focus` ≥3:1 к обоим bg-primary/bg-secondary во всех 21 блоках — держать при добавлении дизайна.**

- **(B) NTC-2 resting-граница контролов = 🌱 CONSIDERED (decision-gated, НЕ hard-bug).** Настройки/stats select+`input[type=number]` (`base.css:1311-1318`): `background: var(--color-bg-secondary)` + `border: var(--rule-weight) solid var(--color-border-primary)`. Проверил, даёт ли границу **fill-разница** фона: `--color-bg-primary` vs `--color-bg-secondary` = **1.0–1.35:1 везде** (near-white/near-black пары) → fill НЕ несёт границу. Значит resting-boundary держится на `--color-border-primary`: **1.1–1.5:1 у 8 дизайнов** (editorial 1.48, stripe 1.18, claude 1.13, superhuman 1.43, theverge 1.45, notion/mintlify/linear rgba-хайрлайны ~1.1) — **sub-3:1**; **swiss 19.8 / broadsheet 18.0** — clean (strong-border дизайны). Строго 1.4.11 resting-граница input в 8/10 дизайнах < 3:1. **НО НЕ объявляю багом** — митигации: (1) фокус-состояние надёжно (NTC-1 ≥3:1 + `shadow-focus`), (2) кастомная **select-стрелка** (граф.аффорданса, `base.css` C6-рецепт), (3) видимый `<label>` + 44px min-target. Хайрлайн-границы = **осознанный визуальный язык** этих систем (Notion/Stripe/Claude/Linear в реале ~1.1:1). **Правильный фикс ≠ бампать общий `--color-border-primary`** (blast-radius: карточки/дивайдеры/таблицы → убивает хайрлайн-эстетику) — а **новый токен `--color-border-input` ≥3:1 ТОЛЬКО на интерактивные контролы**, 4-касания (tokens.css ×21 блок + base.css select/number/text). Blocked (tokens.css+base.css в M-цепочке) + version-bump + затрагивает design-identity → **decision-round юзера, НЕ автономно.** Записано как considered-backlog, чтобы будущий тик не сделал наивный «просто подними border-primary» (сломает эстетику) и не пере-аудитил. См. §7-индекс: строка **NTC**.

**FORM-A11Y — ассоциация label↔контрол (WCAG 1.3.1 / 3.3.2 / 4.1.2) — свод-аудит 2026-07-07, р112.** ✅ **CLEAN на всех 3 form-страницах** (settings/stats/focus-training — единственные с видимыми контролами; grep `<select>`/`input[type≠hidden]`/`<textarea>` по всем шаблонам). Полная инвентаризация видимых контролов и их программного имени:

- **`settings.html` (форма фильтров + форма сессии):** 5 `<select>` (`group`/`topic`/`ordered`/`mode`/`count-number`) + `input[type=number]` — КАЖДЫЙ **потомок** своего `<label class="field">` с видимым `<span class="field-label">` («Группа:»/«Тема:»/«Порядок:»/«Режим:»/«Вопросов:») → неявная (wrapping) ассоциация валидна; 4 чекбокса в `<label class="toggle-item">` + `<span>`-текст; `aria-describedby="filter-mode-hint"` на shuffle/ordered → `<p id=filter-mode-hint role=status aria-live=polite>` (доп.инструкция, не имя).
- **`stats.html` (фильтр + поиск):** 2 `<select>` (`group`/`topic`) в `<label>` с инлайн-текстом «Группа:»/«Тема:»; 2 чекбокса в `<label>` с текстом; `input[type=search]` = `aria-label="Поиск по вопросам"` (placeholder «Поиск по вопросам…» = **декоративный**, реальное имя в aria-label — корректно, placeholder исчезает при вводе); submit-кнопки с `aria-label`.
- **`focus-training.html`:** answer-`<div role=radiogroup aria-label>` + каждый `<input type=radio>` в `<label>` c `th:aria-label="Вариант N: …"`; flashcard-grade = `role=group`+`aria-labelledby` (FT-9, р108). Все `input[type=hidden]` (questionId/topic/…) корректно без label (скрытые).

Итог: **каждый видимый контрол несёт программное имя** — через (1) **wrapping-`<label>` с видимым текстом** (селекты/number/чекбоксы/radio) ИЛИ (2) **`aria-label`** на standalone-контроле без смежного видимого label (stats-search). Ноль немаркированных, ноль `placeholder`-as-label, ноль осиротевших `for`. Инвариант: **новый видимый контрол ОБЯЗАН быть потомком `<label>` с текстом ЛИБО нести `aria-label`; `placeholder` = только доп.подсказка, не имя.** См. §7-индекс: строка **FORM**.

**LIN — Label in Name (WCAG 2.5.3): видимый текст ⊆ accessible name — свод-аудит 2026-07-07, р113.** ✅ **CLEAN.** Естественная пара к FORM (р112): для speech-input юзеров («click ‹видимая метка›») видимый текст кнопки/ссылки должен содержаться в её accessible name. Python-скан всех `<button>`/`<a>` с СТАТИЧЕСКИМ `aria-label` + видимым текстом (strip inner-tags, нормализация «→»/«↓»): **10 элементов, 8 прямых PASS + 2 icon-glyph PASS**.

- **8 текстовых PASS (visible ⊆ aria-label):** stats submit «Применить фильтры» ⊆ «Применить фильтры статистики», «Искать» ⊆ «Искать по вопросам»; settings export «Экспорт JSON/CSV» ⊆ «Экспорт JSON/CSV — прогресс и статистика» (visible **в начале** — идеал); focus flashcard-grade «Не помню/Трудно/Хорошо/Легко» ⊆ «Оценка N: ‹слово›» (visible содержится, «Оценка N:» = ordinal-префикс для SR; containment 2.5.3 удовлетворён — start-позиция это AAA-рекомендация, не AA-требование).
- **2 icon-glyph PASS (settings font-stepper, `base.css`/settings.html:194,196):** `<button aria-label="Уменьшить/Увеличить размер шрифта">A−</button>`/`A+`. Видимое «A−»/«A+» = **символьные глифы** (универсальная конвенция размера шрифта, как `×`-close/`+`-add), НЕ читаемая текст-метка → 2.5.3 icon-glyph интерпретация: aria-label = имя, глиф exempt (тот же класс, что `×`-close с `aria-label=Close`, который axe/Deque НЕ флагают). Обёрнуты в `<div role=group aria-labelledby=font-scale-label>` («Размер шрифта») — групп-контекст. **Осознанно НЕ префиксовать глиф в aria-label** (`"A−, уменьшить…"` заставит SR читать бессмысленное «A минус» — хуже для доминирующего AT ради пренебрежимого speech-input-выигрыша).

Ноль настоящих 2.5.3-нарушений. Инвариант: **интерактивный элемент с `aria-label` + видимым ТЕКСТОМ → видимый текст ⊆ aria-label (лучше — в начале); символьно-глифовый видимый контент (A−/×/+/стрелки) exempt — aria-label несёт имя, глиф в name НЕ дублировать.** См. §7-индекс: строка **LIN**.

**MOTION — prefers-reduced-motion / вестибулярная безопасность (WCAG 2.3.3) — свод-аудит 2026-07-07, р114.** ✅ **CLEAN, ноль пробелов.** Полный аудит декларативного (CSS) И императивного (JS) движения на уважение ОС-настройки + оси персонализации `data-motion`. Архитектура — один набор гасящих деклараций, три-state иерархия `off > on > system`, зеркалящаяся в CSS и JS:

- **(A) CSS (`base.css:285-304`) = универсальный kill.** Селектор `html[data-design]:not([data-motion="on"]) *`/`::before`/`::after` внутри `@media (prefers-reduced-motion: reduce)` + always-on двойник `html[data-design][data-motion="off"] *` гасят `animation-duration:0.001ms !important` + `animation-iteration-count:1` + `transition-duration:0.001ms` + `scroll-behavior:auto`. Два пути активации: (1) системный `prefers-reduced-motion` (для auto/нет-атрибута), (2) явный `data-motion=off` (даже когда система движение разрешает); forced-on (`data-motion=on`) обходит media-kill через `:not([data-motion=on])`. **Универсальный `*` → покрывает ВСЁ**: единственный `@keyframes verdictReveal` (1144, `animation: verdictReveal … both` на 1154) + все CSS-transitions. `!important` на duration бьёт любую авторскую `animation`-декларацию без !important. Инвентарь CSS-анимаций: ровно 1 keyframes + 1 animation — обе под kill'ом.
- **(B) JS (`app.js:114-118`) = единственный императивный скролл зеркалит гейт.** `alertEl.scrollIntoView({behavior: reduced ? 'auto' : 'smooth'})` — где `reduced` = точная три-state иерархия: `data-motion==='off'`→true, `==='on'`→false, иначе `matchMedia('(prefers-reduced-motion: reduce)').matches`. Комментарий: «Скролл уважает ось Движение». Прочие JS-`requestAnimationFrame` (527 defer-focus, 2247 defer-aria-live-текст) = отложенный вызов, НЕ анимация-цикл; ноль `.animate()`, ноль setInterval-хореографии, ноль JS scroll-smooth без гейта.

Итог: декларативное движение покрыто глобально (CSS universal-kill), единственное императивное (JS scroll) реплицирует тот же `off>on>system` приоритет. Инвариант: **новое CSS-движение автоматически под глобальным kill'ом (не нужен пер-элемент reduced-motion); новое JS-движение (scroll/animate) ОБЯЗАНО реплицировать три-state `data-motion off>on>system` перед выбором smooth/анимации — см. эталон app.js:114-118.** См. §7-индекс: строка **MOTION**.

**STATUS — Status Messages / aria-live (WCAG 4.1.3) — свод-аудит 2026-07-07, р115.** ✅ **CLEAN.** Полный аудит: каждое динамическое изменение контента либо озвучивается через корректный live-region, либо — осознанный документированный opt-out. Проверил (а) каждый live-region РЕАЛЬНО обновляется JS (не осиротел), (б) politeness корректна, (в) opt-out'ы обоснованы.

- **9 live-регионов, ВСЕ wired к JS-обновлению:** (1) `inline-alert` — app.js:96-102 динамически ставит `role=status`/`aria-live=polite` ИЛИ `role=alert`/`aria-live=assertive` + textContent (assertive для submit-без-ответа = валидация, юзер обязан заметить — верная эскалация); (2) `result-feedback` (role=status polite aria-atomic) + (3) `extra-analysis-content`/`result-extra-analysis` (polite) — пост-ответный разбор; (4) `filter-mode-hint` (role=status polite) — app.js:207/215 modeHint; (5) `font-scale-value` (role=status polite) — app.js:724; (6) `export-status` (role=status polite) — app.js:660 `setExportStatus`; (7) `table-sort-status` (polite aria-atomic) — stats.js:288-290 `«Таблица отсортирована: ‹колонка›, по возрастанию/убыванию»` на каждый sort (+aria-sort states, click&keydown); (8) `chart-fallback ×2` (role=status polite) — stats.js:110 fallback-текст; (9) `summary-tools-status` (polite, JS-инжект) — копи-статус итога.
- **3 осознанных opt-out'а (документированы в шаблонах):** `training-actions` счётчик (без aria-live — иначе SR читал бы каждую секунду), `training-actions` hint (статичен, сервер рендерит 1×), `today-widget` стрик ×2 (C29 — та же причина).
- **Тонкость реализации (`app.js:583-585` коммент):** видимость live-регионов переключается **классом `.hidden`, НЕ атрибутом `hidden`** — единый паттерн проекта: `hidden`-атрибут выдёргивает регион из a11y-дерева и может подавить анонс в части AT; CSS-`.hidden` держит регион в дереве, текст пишется ДО показа.

Ноль неозвученных значимых изменений, ноль осиротевших регионов, politeness верна (assertive только у validation-alert). Инвариант: **новое динамическое изменение → либо в подходящий live-region (`polite` для статуса, `assertive` только для срочной валидации; `aria-atomic` если нужен полный ре-чит), либо явный документированный opt-out (частый/шумный апдейт); показ регионов — через класс `.hidden`, НЕ атрибут `hidden`; текст писать ДО снятия `.hidden`.** См. §7-индекс: строка **STATUS**.

**IMG — Non-text Content / текст-альтернативы (WCAG 1.1.1) — свод-аудит 2026-07-07, р116.** ✅ **CLEAN.** Отдельная от ICON (р110 = dead-symbol утилизация) и NTC (р111 = контраст) размерность: у каждого нетекстового элемента либо `aria-hidden` (декор), либо программное имя (значимая графика/icon-only контрол). Проверил три класса нетекстового контента:

- **(A) Декоративные иконки — 100% `aria-hidden="true"`, ноль `<img>`.** Census всех `<svg>` в 6 страницах + фрагментах: каждый `<svg class="ed-icon">` несёт `aria-hidden="true"` (statup/result/settings/session-summary/focus-training/today-widget/header — 25 инстансов). ДВА `<svg>` в `icons.html:12,16` = внутри HTML-комментария (примеры), не реальные элементы. Спрайт `icons.html:26` = `aria-hidden="true" focusable="false"` (определения, не контент). Ноль `<img>` во всём `templates/` — весь icon-layer = inline-SVG `<use href="#i-…">`. **JS-инъекция тоже хардкодит `aria-hidden`:** `icon(name)` (app.js:51) и `svgIcon(name)` (app.js:2227) оба эмитят `'<svg class="ed-icon…" aria-hidden="true">'` → любая рантайм-иконка декоративна. Каждый call-site (`icon('lightbulb')+'Почему это неверно:'`, `icon('circle-check')+'Верно'`, `icon('bot')+'Анализирую ошибку…'` и т.д.) парит иконку с ВИДИМЫМ текстом → смысл в тексте, иконка усиливает.
- **(B) Icon-only интерактивные контролы — стабильный `aria-label`-АТРИБУТ.** Header layout/design/theme-тогглы (`header.html:49/59/63` + динамический ре-лейбл `«…(сейчас: X)»`), back-to-top (`Наверх`), `.btn-regenerate` (`result.html:50` `aria-label="Перегенерировать варианты ответа"`). **Ключевая тонкость 1.1.1:** `.btn-regenerate` в JS меняет содержимое на icon-only через `button.innerHTML = icon('circle-check'/'circle-x')` (app.js:1070/1098/1110) — но `innerHTML` заменяет только ДЕТЕЙ, не атрибуты → `aria-label` (атрибут кнопки) переживает своп success/error-глифа, кнопка НИКОГДА не безымянна. Тот же инвариант держит header-тогглы (глиф внутри, имя на кнопке).
- **(C) Значимая графика — descriptive `aria-label` + текст-альтернатива.** 2 Chart.js `<canvas>` (`stats.html:151/162`): `aria-label="Гистограмма прогресса/точности… Полные данные — в таблице ниже."` — не только описывают, но и явно указывают на таблицу как ПОЛНУЮ текст-альтернативу. `.forecast-bar[role="img"]` (`stats.html:274`): `aria-label` = count + плюрал («N вопросов»). Verdict-иконки (`result.html:19/58` success/error) aria-hidden, смысл в тексте «Верно!»/«Неверно» (RES-5).

Ноль нетекстовых элементов без корректной обработки. Инвариант: **декоративная иконка ⟹ `aria-hidden="true"` (в шаблоне И в JS `icon()`/`svgIcon()` — оба хардкодят; никогда `<img>`); icon-only контрол ⟹ `aria-label`-АТРИБУТ на `<button>` (переживает `innerHTML=icon()`-своп — success/error глиф не делает кнопку безымянной); значимая графика (canvas/`role=img`) ⟹ descriptive `aria-label` + указание на таблицу-альтернативу.** Метод: census `<svg>`/`<img>`/`<use>` в шаблонах + чтение JS icon-хелперов (оба эмитят aria-hidden) + проверка что `aria-label` icon-only контролов — АТРИБУТ (не текст-нода). См. §7-индекс: строка **IMG**.

**LINK — Link Purpose in Context / текст ссылок (WCAG 2.4.4) — свод-аудит 2026-07-07, р117.** ✅ **CLEAN.** Скринридеры умеют зачитывать список всех ссылок ВНЕ контекста (rotor/elements-list), поэтому расплывчатый текст («здесь», «сюда», «подробнее», голый «→») = реальный дефект. Python-census всех `<a>` в `templates/` (visible-текст = strip inner-tags; учёт `th:text`/`th:utext`/`aria-label`/`title`): **35 якорей, ноль VAGUE / ноль arrow-only / ноль empty-no-label.**

- **Навигация/CTA — все purpose-clear:** skip-links («Перейти к вопросу/результату/итогам/настройкам/аналитике»), header-nav («Фокус»/«Аналитика»/«Настройки» + masthead-бренд→home), CTA («На главную»/«Начать повторение»/«Учить новое»/«Следующий вопрос»/«Повторить ошибки»/«Продолжить тренировку»/«Перейти к теме»/«Начать тренировку»/«Открыть настройки»/«Обновить тренировку»/«Сбросить фильтры»/«сбрось поиск и фильтры»). Каждый текст самодостаточен без всякого контекста строки.
- **Динамические ссылки — текст несёт суть:** topic-ссылки в таблицах (`session-summary.html:73` `.summary-topic-link`, `stats.html:196`) = отображаемое ИМЯ ТЕМЫ (`th:text` displayName) + `title`=raw-слаг (supplementary); mistake-ссылки (`session-summary.html:93` «Вопрос ‹тема›»); related-questions (`result.html:141` `th:utext`= САМ текст вопроса) — все различимы в списке-вне-контекста.
- **Trailing «→» — НЕ 2.4.4-нарушение (характеризует открытый IC-5):** «→» = СЫРОЙ текст-глиф (подтверждено grep: не CSS `::after`, не aria-hidden, не SVG), но всегда стоит ПОСЛЕ самодостаточного текста («Перейти к теме →») → цель ссылки ясна из текста, стрелка = directional-аффорданса + минимальный SR-шум, а не носитель смысла. → 2.4.4 PASS. IC-5 (iconify/удаление «→») остаётся taste/design-decision (консистентно-или-никак: 4 из ~6 CTA-файлов blocked → partial-swap фрагментирует консистентность), НЕ a11y-необходимость.

Ноль ссылок с неясной вне-контекста целью. Инвариант: **каждая `<a>` = самодостаточный purpose-clear текст (никаких «здесь/сюда/подробнее»/только-стрелка/пустой-без-aria); динамическая ссылка → видимый текст = осмысленное имя (тема/вопрос), `title`-слаг только supplementary; directional-глиф в тексте ссылки допустим ТОЛЬКО как хвост за самодостаточным текстом (не единственный контент).** Метод: Python-census `<a>` с strip-tags visible-текста + флаги vague/arrow-only/empty. См. §7-индекс: строка **LINK**.

**FOCUS — Focus Visible / индикатор фокуса не подавлён (WCAG 2.4.7) — свод-аудит 2026-07-07, р118.** ✅ **CLEAN, образцовая обработка.** Отдельная от NTC (р111 = КОНТРАСТ ринга ≥3:1): здесь — не подавляется ли фокус ВООБЩЕ (`outline:none` без замены = самый частый keyboard-блокер). Прошёл КАЖДЫЙ `outline:none`/`outline:0` в base.css — все 9 legitimate:

- **(A) Глобальный ринг (base.css:229-230):** `html[data-design] :focus-visible { outline: 2px solid var(--color-border-focus); }` (+offset 2px) — базовый keyboard-индикатор для всего (контраст ≥3:1 подтверждён NTC р111).
- **(B) Mouse-only подавление (keyboard-ринг ЦЕЛ):** `:focus:not(:focus-visible){outline:none}` (233 — стандартный совр. паттерн: клавиатура через `:focus-visible` держит ринг, мышь нет); `#interview-options label:focus-within{outline:none}` (771) — комментарий 769-770: мышиный `:focus-within` глушится, но keyboard-ринг идёт через `label:has(input:focus-visible)` (772-773 = `outline:2px solid border-focus`). Оба — суппресс ТОЛЬКО мыши.
- **(C) Container-focus-таргеты (не интерактивные контролы):** `#main-content:focus/:focus-visible{outline:none}` (266-267) — `<main tabindex=-1>` цель skip-link'а; `.settings-panel:focus{outline:none}` (1249) — `role=tabpanel tabindex=0` цель при выборе вкладки. Оба получают программный фокус как скролл/чтение-цели, ринг на них намеренно отсутствует (широко принято) — комментарий 265 группирует их.
- **(D) Замена на box-shadow-ринг:** `.settings-tab:focus-visible` (1243), `select/input[number]:focus` settings (1325), `select/input:focus` stats (1578), `.seg-btn:focus-visible` (2815) — все `outline:none` НО с `box-shadow: var(--shadow-focus)` (+ border-color сдвиг у инпутов). `--shadow-focus` = РЕАЛЬНЫЙ видимый ринг во всех дизайнах (`0 0 0 3px rgba(accent,0.40-0.45)` ИЛИ double-ring `0 0 0 2px bg-primary, 0 0 0 4px border-focus` — с зазором, ещё виднее). Не subtle-тень.
- **(E) Forced-colors backstop (base.css:344-352):** box-shadow стрипается в Windows High Contrast → отдельный `@media (forced-colors: active)` восстанавливает `outline: 2px solid Highlight !important` для ВСЕХ box-shadow-only (.settings-tab, settings select+input[number], stats select+input, .seg-btn — полное покрытие набора D) + перекодирует активный `.seg-btn.is-active` парой `Highlight/HighlightText` (accent-bg-only иначе перетирается). Проверено: ноль правил с `box-shadow:none`-на-фокусе → ни одного элемента с ОБОИМИ убранными индикаторами.

Ноль истинных подавлений фокуса. Инвариант: **новый интерактивный элемент наследует глобальный `:focus-visible` outline автоматически; `outline:none` допустим ТОЛЬКО с (1) сохранением keyboard-ринга через `:focus-visible`/`:has(:focus-visible)` [мышь-суппресс], ИЛИ (2) замена `box-shadow:var(--shadow-focus)` + ОБЯЗАТЕЛЬНО добавить элемент в forced-colors-блок (346-352), иначе невидим в High Contrast, ИЛИ (3) container-focus-таргет (skip/tab-цель, не контрол). Никогда `outline:none` без одного из трёх.** Метод: grep всех `outline:none`/`0` + классификация каждого + проверка `--shadow-focus`=реальный ринг + покрытие forced-colors-набора + отсутствие `box-shadow:none`-на-фокусе. См. §7-индекс: строка **FOCUS**.

**KBD — Keyboard Operable / кастомные виджеты (WCAG 2.1.1 + 2.1.2 no-trap) — свод-аудит 2026-07-07, р119.** ✅ **CLEAN.** SEM (р101) проверял «ноль onclick на div, все действия = `<button>`/`<a>`», но НЕ клавиатурные ПАТТЕРНЫ кастомных ARIA-виджетов (где keyboard-support чаще всего ломается — role задан, а стрелки не реализованы). Проверил каждый интерактивный виджет:

- **Нативные контролы = клавиатура из коробки:** `<button>` (font-stepper, flashcard-grade, submit, регенерация, back-to-top), `<a href>` (nav, CTA, skip-link), `<select>`/`<input>` (settings/stats фильтры), **нативные `<input type=radio>`** (варианты ответа в `role=radiogroup` — браузер даёт arrow-навигацию + roving автоматически). Ничего кастомного = ничего чинить.
- **`role=tablist` (settings, 3 вкладки; app.js:747-791) = полный WAI-ARIA tabs:** roving tabindex (`t.tabIndex = on ? 0 : -1`, 763), `aria-selected`, keydown (772-781) `ArrowRight/Down`→next, `ArrowLeft/Up`→prev, `Home`→0, `End`→last, `preventDefault`+`activate(idx,true)` (перемещает фокус И активирует панель = automatic-activation паттерн). PE: без JS tablist скрыт (`.hidden`), нет мёртвого виджета. Восстановление вкладки из localStorage.
- **seg-`role=radiogroup` (theme/design/layout/font/width/motion; app.js:679-707 `wireSegControl`) = полный radiogroup-паттерн:** кастомные `<button role=radio tabindex=-1 aria-checked>` (нативных arrow нет → реализовано вручную). `sync()` = roving tabindex (`b.tabIndex = on ? 0 : -1`, 690) + aria-checked; keydown (695-703) `←→↑↓`→`preventDefault`+`setValue` (выбор) + `btns[idx].focus()` (перемещение). Единый хелпер для 6 контролов = ноль рассинхрона.
- **sortable-`th` (stats.js:294-295):** focusable (tabindex) + keydown `Enter`/`Space`→sort + `aria-keyshortcuts="Enter Space"` (263, анонсирует) + `aria-sort`-состояния (STATUS р115). Комментарий 260: заголовки «обещают Enter/Space» и держат обещание.
- **Модалки (2.1.2 no-trap):** overlay keydown `Escape`→close (app.js:542-543/2209, stats.js:355), focus-trap = `inert`+`aria-hidden` на фоне (516-533/2163-2182), ВОССТАНАВЛИВАЕТСЯ при закрытии → trap escapable (Escape всегда выходит) = **не keyboard-trap**. Бонус-шорткаты: цифры выбирают вариант (1318-1336), «?» — help-overlay (2206), «/» — фокус поиска (stats).

Ноль неоперабельных клавиатурой виджетов, ноль keyboard-trap. Инвариант: **нативный контрол (`button`/`a`/`input`/`select`) = клавиатура бесплатно, предпочитать его; кастомный `role=tab*/radio*` ОБЯЗАН реализовать roving-tabindex (активный=0, прочие=−1) + arrow-навигацию (`preventDefault`+move-focus+activate/select) — эталоны `initSettingsTabs` (tablist +Home/End) и `wireSegControl` (radiogroup); focusable-не-контрол (sortable-th) = Enter/Space + `aria-keyshortcuts`; модалка = Escape-выход + восстанавливаемый inert-фон (escapable trap, не keyboard-trap).** Метод: grep `role=(tablist|tab|radiogroup|radio|…)` в шаблонах → для каждого найти keydown-обработчик в JS + проверить roving-tabindex + arrow/activate; модалки — Escape+inert-restore. См. §7-индекс: строка **KBD**.

**REFLOW — Reflow / горизонтальный overflow (WCAG 1.4.10 + 1.4.4 Resize) — свод-аудит 2026-07-07, р120.** ✅ **CLEAN, образцовые основания.** ПЕРВЫЙ систематический responsive-аудит (все прежние — a11y-семантика): при 320px CSS-ширине / 400% zoom контент обязан переноситься без горизонтального скролла страницы (исключение — 2D-контент: таблицы/код/картинки, им можно свой скролл). Статический аудит CSS-оснований, включающих/ломающих reflow:

- **(1) Viewport разрешает масштабирование (1.4.4):** `head.html:5` `content="width=device-width, initial-scale=1.0"` — ноль `user-scalable=no`, ноль `maximum-scale` → zoom/pinch НЕ заблокирован (частый анти-паттерн, крадущий resize у слабовидящих). ✓
- **(2) Fluid-раскладка, ноль фикс-ширин:** grep `width/min-width:≥300px` на элементах = ТОЛЬКО media-брейкпоинты (768/761/1200 — не размеры элементов). Контейнеры на `max-width` (fluid cap, сжимается ниже вьюпорта): `max-width:100%` (svg/img/inputs 1313), `560px` (empty-card 1187), `68ch` (option-текст 740, читаемая мера), `220px` (field-inline 1306). `svg{max-width:100%;height:auto}` (207) — картинки не переполняют. Ноль контейнеров, форсящих горизонт на 320.
- **(3) Защита от длинных неразрывных токенов (вторая половина reflow):** `overflow-wrap:break-word`/`anywhere` + `word-break:break-word` ПОВСЕМЕСТНО на каждом текст-элементе — option-лейблы (1083), проза (447), заголовки (1257/1297), summary-mistakes (1954), похожие вопросы (2329), stat-титулы (2131 +`min-width:0` для flex-сжатия), meta (2233). Комментарий 1074-1078 явно документирует: длинный URL при `overflow-wrap:normal` расталкивает вёрстку на 375px → защищён `anywhere`; inline-code защищён, блочный `<pre>` скроллится. `flex min-width:0` (2131/2233) = разрешает flex-детям сжиматься ниже контента (частый reflow-баг «flex-item не сжимается» устранён).
- **(4) 2D-контент в явных `overflow-x:auto` боксах (корректное исключение 1.4.10):** широкие таблицы (`.markdown-content table` 1084 `display:block;overflow-x:auto`, `.summary-table` 1924, `.topic-table-wrap` 1790/1794), код/диаграммы (1096/1099) — каждый скроллится ВНУТРИ своего бокса, а не рвёт ширину страницы. Именно так 1.4.10 разрешает 2D-данные.
- **(5) Нет `body/html { overflow-x:hidden }`:** намеренно — это НЕ пробел, а правильный подход: глобальный `overflow-x:hidden` МАСКИРУЕТ реальные overflow-баги и ломает `position:sticky`; здесь overflow предотвращён У ИСТОЧНИКА (fluid-ширины + overflow-wrap + scroll-боксы для 2D), а не заклеен. Corroborates ранее live-verified «mobile-overflow чист на 375/768/1280» (project_summary_page_test_flow) — теперь с CSS-фундамент-слоем.

Ноль элементов, форсящих горизонтальный скролл страницы. Инвариант: **контейнер = `max-width` (fluid), НЕ фикс `width:NNNpx`; каждый текст-элемент = `overflow-wrap:break-word` (длинные токены/URL/код-идентификаторы не рвут layout); flex-контейнер с текстом = `min-width:0`; широкий 2D-контент (таблица/код/диаграмма) = обёртка `overflow-x:auto` (скролл внутри бокса); НЕ глушить `body overflow-x:hidden` (предотвращать у источника, не clip); viewport НЕ блокировать (`user-scalable=no`/`maximum-scale` запрещены).** Метод: grep viewport-meta + фикс-`width≥320` + `white-space:nowrap` (только off-screen/th/ellipsis допустимы) + `overflow-x:auto` (2D-боксы) + `overflow-wrap` покрытие. См. §7-индекс: строка **REFLOW**.

**TEXTSPACE — Text Spacing / пользовательский оверрайд интервалов (WCAG 1.4.12) — свод-аудит 2026-07-07, р121.** ✅ **CLEAN.** Пара к REFLOW (обе — robustness под user-адаптацию): когда юзер применяет text-spacing-букмарклет (`line-height ≥1.5×font`, `letter-spacing ≥0.12em`, `word-spacing ≥0.16em`, `paragraph ≥2×font`), контент не должен обрезаться/накладываться. Главный риск — фикс-высота текст-контейнера + `overflow:hidden` (клип при росте line-height) или px-`line-height` (не масштабируется). Статический аудит:

- **(ЯДРО) `line-height` ВЕЗДЕ unitless (ноль px):** grep `line-height:\d+px` = ПУСТО → все line-height безразмерны (body 1.7 и т.п.) → при user-override 1.5× строки раздвигаются пропорционально шрифту, ноль клипа. Это точно то, чего требует 1.4.12. px-`line-height` (не масштабируется под font-size) отсутствует.
- **Ноль `-webkit-line-clamp` / `box-orient`:** нет многострочной truncation, которая прятала бы текст при расширении интервалов.
- **Фикс-`height:NNpx` ТОЛЬКО на не-текстовых элементах:** checkbox 18px (1341, form-control), `.chart-wrapper` 320px (1720, canvas-график), `.forecast-bar` 10px + progress-треки (графика-бары), `.ed-theme-toggle` 40px (3600, icon-кнопка). Текст-несущие контейнеры используют `min-height`/auto (напр. `.focus-question` min-height SET-11, `.btn`/inputs `min-height:44px` BAS-14 — растут, не клипают).
- **`overflow:hidden` (13 шт) — ни один не клипает растущий текст вертикально:** прогресс-треки (633 + streak/accuracy/topic — radius-clip заливки), `.seg-control` (2799 — radius-clip сегментов, при этом `flex-wrap:wrap`+`max-width:100%`, БЕЗ фикс-height → растёт с текстом), `.visually-hidden`/`.sr-only` (3498 — off-screen 1px, не видимый контент), и ОДИН ellipsis-бейдж topic-пилюли (2177 — намеренный single-line `nowrap`+`text-overflow:ellipsis` чтобы `radius-full`-торцы не деформировались на 320px; `max-width:100%` → не переполняет; полное имя темы дублируется в связанной ссылке/заголовке → truncation не теряет контент). Ни одного «фикс-height текст-бокс + overflow:hidden».

Ноль клипа/наложения при 1.4.12-оверрайде. Инвариант: **`line-height` держать БЕЗРАЗМЕРНЫМ (never px) — тогда масштабируется под user-spacing; текст-контейнер = `min-height`/auto, НЕ фикс `height:NNpx` (фикс-height допустим только на графике/иконках/form-controls без текста); `overflow:hidden` на текст-боксе только если он БЕЗ фикс-height (radius-clip ок) — фикс-height+overflow:hidden на тексте = клип при росте интервалов; ellipsis-truncation допустима лишь на redundant-лейбле (полная инфо доступна рядом) + `max-width:100%`.** Метод: grep px-`line-height` (должно быть 0) + `line-clamp` (0) + фикс-`height:NNpx` (классифицировать текст/не-текст) + `overflow:hidden` (проверить отсутствие парного фикс-height на текст-элементе). См. §7-индекс: строка **TEXTSPACE**.

**CONSIST — Consistent Navigation + Identification (WCAG 3.2.3 + 3.2.4) — свод-аудит 2026-07-07, р122.** ✅ **CLEAN, консистентность гарантирована АРХИТЕКТУРНО.** Прямо ложится на брифовые «inconsistent buttons/навигация/consistency». Проверил, что навигация повторяется в одном относительном порядке на всех страницах (3.2.3) и что повторяющиеся функциональные компоненты идентифицируются одинаково (3.2.4):

- **(3.2.3) Consistent Navigation:** ВСЕ 6 страниц включают ОДИН shared-фрагмент `<header th:replace="~{fragments/header :: header(...)}">` (error/focus-training/result/session-summary/settings/stats). Nav-ссылки в ФИКСИРОВАННОМ порядке на каждой: **Фокус (`@{/}`) → Аналитика (`@{/stats}`) → Настройки (`@{/settings}`)** (header.html:25-33). Единственная per-page вариация — параметр `activePage`, который лишь добавляет `is-active`-класс + `aria-current="page"` на текущую ссылку (корректная индикация местоположения), НЕ реордерит и не добавляет/убирает ссылки. Бренд-ссылка masthead→home консистентна. → относительный порядок nav идентичен везде.
- **(3.2.4) Consistent Identification:** повторяющиеся компоненты определены ЕДИНОЖДЫ во фрагменте → идентичные имя+иконка на КАЖДОЙ странице: layout-toggle (`aria-label="Сменить раскладку"`, `title="Раскладка страниц"`, `#i-layout`), design-toggle (`Сменить дизайн`, `#i-shapes`), theme-toggle (`Переключить тему`, `Светлая / тёмная тема`). nav `aria-label="Основная навигация"` консистентен. **Дедуп как усиление consistency:** комментарий header.html:35-39 — экспорт РАНЬШЕ дублировался в шапке битыми `<a href=/export>` (всегда 403, требовали app.js, не подключённого на /stats); убран → экспорт ЕДИНАЯ рабочая точка в /settings «Управление данными» (устранён «та же функция — разная сломанная аффорданса»).
- **Архитектурная суть:** т.к. nav + глобальные тогглы живут в ОДНОМ Thymeleaf-фрагменте, включаемом идентично везде, консистентность 3.2.3/3.2.4 СТРУКТУРНО ГАРАНТИРОВАНА — нет per-page копии nav, которая могла бы разойтись (самый сильный вид гарантии, сильнее ручной сверки).

Ноль расхождений nav/идентификации между страницами. Инвариант: **общая навигация + глобальные повторяющиеся контролы ОБЯЗАНЫ жить в едином shared-фрагменте (`fragments/header`), включаемом на все страницы — тогда порядок/имена/иконки консистентны by-construction; per-page различие допустимо ТОЛЬКО в indication текущей позиции (`is-active`/`aria-current`) и `pageTitle`-h1, НЕ в порядке/наборе/лейблах ссылок; одинаковая функция (экспорт/тогглы) = одна точка/один лейбл, не дублировать per-page (дубли дрейфуют и ломаются).** Метод: grep `fragments/header :: header` во всех страницах (счётчик = число страниц) + чтение nav-порядка во фрагменте + сверка что per-page варьируется лишь `activePage`/`pageTitle`. См. §7-индекс: строка **CONSIST**.

**FNO — Focus Not Obscured (WCAG 2.4.11, 2.2 AA) — свод-аудит 2026-07-07, р123.** Первый проход по этому 2.2-критерию: когда фокус ПЕРЕМЕЩАЕТСЯ (Tab/якорь/программно) на элемент, который затем скрыт author-created sticky/fixed-контентом — **отличается от FOCUS (р118, вид focus-ring): там ринг видимости, здесь физическое накрытие фокуса оверлапом.** Статический аудит всех `position:sticky/fixed` (base.css) + сверка, есть ли под каждым липким focusable-контент в том же скролл-контексте:
- **ГЛАВНЫЙ риск ЧИСТ:** site-header/nav **НЕ** sticky/fixed (grep `position:(sticky|fixed)` на header/nav = 0) → самый частый 2.4.11-провал (липкая верхняя навигация накрывает фокус) здесь **не применим**.
- **Модалки** (`.kbd-help-overlay`/`.kbd-help-modal` + `.export-*` — `position:fixed; inset:0; z-index:var(--z-modal)`) = focus-trapped оверлеи, они САМИ фокус-контекст → не obscure.
- **back-to-top** (`fixed` bottom-right 44px) — теоретически накрыл бы фокус в правом-нижнем углу, но малый corner-виджет, видим лишь после ~1.5-экрана скролла → низкий риск.
- **related-rail** (`#result-related-questions`, sticky top:space-5, ≥1200px) — в отдельной grid-колонке 2, не оверлапит col-1 → не obscure главную колонку.
- **⛔ ОТКРЫТО FNO-1:** `.stats-page .topic-table thead th` = `position:sticky; top:0; z-index:1` (base.css:1744) стоит НАД focusable row-links (`<th scope="row" class="topic-name"><a th:href>` — по ссылке-в-строке на каждую тему, stats.html:195-196), и НИГДЕ в base.css нет `scroll-margin-top`/`scroll-padding-top` (grep=0). При Tab-навигации браузер скроллит фокус-ссылку к верх-краю скролл-контейнера — под липкую `thead` → строка-ссылка может быть частично/полностью накрыта липкой шапкой. **Классический 2.4.11-паттерн.** Плюс split-layout ≥1200px: sticky `.focus-question` (top:space-5, base.css:3152) над скроллящимися `#interview-options` — фокус-опция у верха скролла может уйти под липкий вопрос.
- **Статус: OPEN-candidate, НЕ verified-clean:** реальное накрытие зависит от фактических высот + браузерного scroll-into-view → требует LIVE keyboard-verify (Tab по stats-таблице до ухода строки под thead; ≥1200px split — Tab по опциям), которую сейчас нельзя (app down). Фикс (если подтвердится) = `scroll-margin-top: ≈высота-thead` на focusable-таргетах ПОД липкими (или `scroll-padding-top` на скролл-контейнере) — CSS-правка → **blocked** (base.css в M-списке юзера + требует version-bump `?v=N` + live-verify).

Инвариант: **любой `position:sticky|fixed` с `top:0` (или малым top) НАД зоной с focusable-контентом ⟹ таргеты под ним ОБЯЗАНЫ иметь `scroll-margin-top ≥ высота липкого` (или скролл-контейнер — `scroll-padding-top`), иначе Tab-фокус прячется под липкий элемент (2.4.11 fail).** Метод: grep `position:(sticky|fixed)` → для каждого с малым `top` найти focusable-контент под ним в том же скролл-контексте → проверить наличие `scroll-margin/padding-top`; отдельно сверить, НЕ липкий ли site-header/nav (главный чек). См. §7-индекс: строка **FNO**.

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
