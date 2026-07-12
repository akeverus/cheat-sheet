# PIXEL_QA_MATRIX — матрица приёмки frontend (ROUND RESET 2026-07-11)

> Источник инвентаря — `screen-registry.json`. Этот файл трекает **QA-ячейки** каждой
> поверхности по обязательным осям (PROMPT_PLAN_FRONTEND.md §9 mockup-gates, §13 parity).
> **RESET:** ни одна ячейка НЕ наследует старый PASS. Легенда: `TODO` не проверено ·
> `RECHECK` было PASS в прошлом раунде, требует переподтверждения · `PASS` подтверждено
> в этом раунде (screenshot + метрики) · `FAIL` дефект · `BLOCKED` заблокировано (dirty-дерево).

## Обязательные QA-оси (на каждую поверхность/state)

1. **detector** — `impeccable detect` exit 0 (макет).
2. **AA** — OKLCH→sRGB контраст ≥4.5, **обе темы**.
3. **full-width** — нет боковых пустот на 375/768/1280/1440/1728/1920/2560 (DPR2), обе темы; chrome span=100vw, gutter симметричен.
4. **a11y** — клавиатура/focus-visible/reduced-motion/aria-каркас (§8).
5. **states** — все fixtures (normal/min/max/long/code/empty/error) детерминированно.
6. **parity** — production == mockup по DOM/computed-метрикам (screenshot + bounds/gutters/typo/spacing).
7. **contract** — id/классы/data-*/формы/CSRF/model-attrs/JS-хуки не сломаны; ArchUnit/тесты зелёные.
8. **no-js/print** — прогрессивная деградация и печать.

## Матрица (surface × ось) — статус текущего раунда

| Surface | detector | AA | full-width | a11y | states | parity | contract | no-js/print | Фаза |
|---|---|---|---|---|---|---|---|---|---|
| shell (chrome) | PASS | PASS✧ | PASS* | PASS✧ | PASS | TODO▲ | TODO | RECHECK | D done; E BLOCKED (baseline R0.35+R0.39) |
| focus-training | PASS | PASS‡ | PASS* | PASS | PASS(core)† | PASS(active, R0.49) | PENDING-TEST¤ | PASS(static R0.32) | **E DONE шаг 1 (C1-i применён, v=75)**; flashcard/empty/result-инъекции — parity-фаза |
| result | PASS | PASS‡ | PASS* | PASS | PASS(core)§ | APPLIED▽(R0.50) | PENDING-TEST¤ | RECHECK | **E DONE шаг 1 (C3-i применён, v=76)**; визуальный паритет (нужен ответ) — parity-фаза |
| session-summary | PASS | PASS | PASS* | PASS | PASS | APPLIED▽(R0.47) | PENDING-TEST¤ | TODO | **E DONE шаг 1 (C6-i применён, v=74)**; визуальный паритет + шаг 2 (headline/grid) — parity-фаза |
| settings | PASS | PASS | PASS* | PASS✦ | PASS | TODO▲ | TODO | PASS(static R0.32) | D done; E BLOCKED (baseline R0.36) |
| stats | PASS | PASS♦ | PASS* | PASS | PASS(core)◊ | PASS(R0.51) | PENDING-TEST¤ | PASS(static R0.32) | **E DONE шаг 1 (C5-i применён, v=77)**; цвета графиков stats.js — Фаза G |
| error | PASS | PASS | PASS* | PASS | PASS | **PASS(R0.46)** | PENDING-TEST¤ | PASS(static R0.32) | **E DONE (C7-i применён, v=73)** |

`¤` contract PENDING-TEST: вставка — чистый append (пиннутые тестом строки не
тронуты), но прогон TemplateFragmentContractTest отложен — живой bootRun
параллельной сессии (§4: gradle при живом devtools-bootRun = wedge); прогнать
при первой возможности. `▲` parity TODO, но **численный ДО-портовый baseline СНЯТ** (живой инстанс, @1280 +
375 для shell/focus, 4 состояния editorial/instrument × light/dark, settle 400ms) —
координаты в PARITY_QA_CHECKLIST §2; после порта паритет доказывается диффом чисел,
editorial = контроль бит-в-бит. `▽` baseline невозможен без сессии (§4 запрещает
отвечать на живом квизе) — снимается ПЕРВЫМ шагом parity-фазы.

## Дименсиональные оси (кросс-поверхностные) — статус

| Ось | Статус | Заметка |
|---|---|---|
| 11 дизайн-вариантов | RECHECK | instrument добавлен (f2.1, AA-verified f2.2); остальные 10 — RECHECK. |
| light/dark | RECHECK | обе темы проверялись в прошлом раунде — переподтвердить на живом рендере. |
| viewports 375–2560 DPR2 | RECHECK | покрыты метриками прошлого раунда; screenshot был сломан (capture timeout). |
| DPR1 / zoom 200% | PASS (макеты, R0.18) | живой аудит chrome-devtools: эмуляция 320px (WCAG 1.4.10 reflow) + 640px (zoom200@1280), все 7 макетов × все mock-состояния; 2 дефекта найдены и пофикшены (C18: explain-code токен, set-grid minmax); прод — на parity-фазе. |
| no-js | PASS (static, R0.32) / интерактив — parity | GET-смоук серверного HTML 4 страниц (curl, живой инстанс): все PE-блоки уходят с `hidden` (settings-tablist, personalization-card, data-export-block, streak-bar, все 3 тоггла шапки) → без JS мёртвых контролов нет; живое без JS на месте (filters-form/session-form/danger-btn, noscript-алерт, stats-таблица РАЗВЁРНУТА — 0 tr[hidden], expander hidden, chart-fallback в DOM). Интерактивный no-JS смоук (сабмиты форм, result.html фоллбэк) — на parity-фазе (мутации базы). |
| print | PASS (summary) / TODO (остальные) | R0.19: print-CSS добавлен в session-summary (единственная страница с кнопкой «Печать» в проде): без хрома/кнопок/стрелок, 1 колонка, чёрным по белому, таблица с рамками, break-inside: avoid. Верификация: CSSMediaRule распарсен (16 правил), 0 мёртвых селекторов, экран не тронут; живой print-preview — на parity-фазе. Остальные 6 макетов — низкий приоритет (нет печатных сценариев). |

`†` focus-training: покрыты 4 ядровых (active/result/empty/done) **+ flashcard** (изучение/
study-LEARN: reveal→grade, R0.12) **+ alert** (inline-alert error/info/warn, R0.13) **+ loading**
(скелет, R0.14); НЕ покрыты generationUnavailable, no-js, diagram (CRITIQUE C12 **IN_PROGRESS**;
diagram отложен §4; порт focus-training BLOCKED параллельным WIP). flashcard QA: detector exit 0
(после фиксов side-tab→1px + em-dash→двоеточия); AA ≥4.5 (flash-h2 6.41/8.32). alert QA: detector
exit 0; AA семантический-ink на wash ≥4.5 (error 5.52/5.24, signal 6.54/6.87, spark 5.41/7.76);
role=alert/status. loading QA: detector exit 0; скелет декоративен (aria-hidden), регион
role=status+aria-live+aria-busy+visually-hidden текст, пульс off под reduced-motion (AA н/п —
чистая декорация).

`‡` focus-training AA: все ТЕКСТОВЫЕ пары ≥4.5 обе темы (light worst 4.95 badge, dark 5.24);
единственная пара ниже 4.5 — `opt-mark` галочка верного `success-on/success` = 4.28 light,
но это **графический объект** (WCAG 1.4.11, порог 3:1), `aria-hidden`, избыточна → compliant
(CRITIQUE C11, РЕШЕНО).

`§` result: покрыты correct/incorrect (mock-switch) + post-answer-analysis (takeaway/trace/
related) + favorite **+ confidence** (R0.15: 🎲/🤔/💪 radiogroup + roving tabindex + стрелки/
Home/End, эмодзи aria-hidden + текст; прод намеренно держит эмодзи → порт-решение). regenerate =
**мёртвый прод-UI** (R0.17: `aiEnabled` захардкожен false → кнопка не рендерится; НЕ портировать;
dead-code кандидат Фазы G); no-js = живой `/answer`, parity-концерн → CRITIQUE C13 **DONE**.
confidence QA: detector exit 0
(после фикса flat-type-hierarchy — убран 1.15em глиф), AA ≥4.5 (conf-q 7.18/8.57, checked
6.54/6.87). AA все текст-пары ≥4.5 обе темы (worst 5.52/5.24); opt-mark галочка = граф.
объект (C11). Наблюдение на порт: verdict `<p>` не live-region — `aria-live` (§8
`#result-feedback`) проводится при порте, не дефект макета. Порт result BLOCKED (WIP).

`✦` settings a11y образцовый WAI-ARIA: tablist (`role=tablist/tab/tabpanel`, roving tabindex,
стрелки+Home/End), seg-control `role=radiogroup`+`aria-checked`+стрелки, font-stepper
`role=status`+`aria-live=polite`, toggle focus-ring на `input:focus-visible+.switch`, label
for/id на select/input. Паритет §3 (#filters-form/#session-form)/§8. AA все пары ≥4.5 обе
темы (worst 4.95 btn-danger / 5.24 dark).

`◊` stats states: покрыты overview/charts+fallback/sortable-table/forecast/gaps **+ search**
(R0.16: `role=search` форма + live-фильтр + счётчик `role=status` + empty-строка + хоткей «/»
с guard текст-полей + Esc-сброс; detector exit 0; AA ≥4.5, worst placeholder ink3 5.75/7.18) —
CRITIQUE C16 **DONE**. empty/cold-start намеренно не делаем (прод-решение). Порт stats BLOCKED (WIP).

_settings states — теперь ПОЛНЫ (R0.10):_ 3 вкладки + export json/csv + 7 осей + filters +
session + streak + **reset-options-confirm** (role=alertdialog, focus-trap, Esc, дефолт-фокус
на «Отмена», detector exit 0, AA обе темы worst 4.95) → CRITIQUE C14 **DONE**.

`♦` stats: все ТЕКСТ-пары ≥4.5 обе темы. Графобъекты-столбцы (3:1): найден провал амбер
`--spark`/`surface-2` = 2.06 light → **ПОФИКШЕН** (заливки данных → `--spark-ink`, 5.02
light / 8.72 dark; CRITIQUE C15). После фикса все столбцы ≥3:1. states: overview/charts+
fallback/sortable-table/forecast/gaps покрыты; НЕ покрыты empty/cold-start (осознан) + search
(C16). Порт stats BLOCKED (WIP).

`✧` shell (chrome) RECHECK ПРОЙДЕН + ФИКС (R0.11): detector exit 0. AA обе темы — все 12
текст-пар хрома ≥4.5 (worst 5.02 spark-ink/surface light, 5.31 ink3/surface; skip-link/btn
signal-on/signal-strong 6.01; nav-current signal-ink/signal-wash 6.54; kbd 13.1). Графобъект:
`.streak-fill` (доля прогресса стрика) `--spark`/`surface-2` = **2.06 light < 3:1 → ПОФИКШЕН**
на `--spark-ink` (5.02 light / 8.72 dark; C17 — тот же класс, что C15 в stats). a11y ОБРАЗЦОВЫЙ:
skip-link→#main-content, `main tabindex=-1`, header nav `aria-label`, `nav-menu-btn`
`aria-expanded`+`aria-controls=mobile-menu`, theme-toggle `aria-label` синх в JS (авто/светлая/
тёмная), kbd-help `role=dialog`+`aria-modal`+`aria-labelledby`/`aria-describedby`+focus-trap+Esc+
возврат фокуса, streak `role=group`+`aria-label`, все декор-svg `aria-hidden`. States ПОЛНЫ (6):
header-nav / mobile-drawer(data-open+JS) / theme-toggle(3 режима) / kbd-help-modal / skip-link /
footer. parity/contract TODO (порт хрома BLOCKED — head.html в параллельном WIP); no-js RECHECK
(тема/меню/справка на JS — деградацию проверить на bootRun при parity-фазе).

`*` full-width для error и session-summary верифицирован **структурно** (edge-to-edge
`.wrap width:100%` + грид main+рельс, брейк 1080px = проверенный паттерн других экранов),
не живым screenshot (capture в этой сессии флапал) — переподтвердить на bootRun при
parity-фазе. **session-summary (R0.3):** detector exit 0; AA обе темы 18 пар worst 4.95
(light badge error-on/error) / 5.24 (dark) — все ≥4.5; a11y-каркас полный (skip→main,
main tabindex=-1, aria-labelledby секции, table caption+th scope, mock-switch aria-pressed,
reduced-motion `.anim{opacity:1}`). Наблюдение (не блокер, на parity-фазу): `thead th`
sticky top:0 в `overflow-x`-only обёртке на 4-строчной таблице фактически не активируется
и может уходить под sticky-шапку — решить при порте, где прод-таблица иначе устроена.

## Пробелы покрытия (из registry.gaps_found)

- ~~error.html — нет макета~~ **ЗАКРЫТ R0.2** (макет с 5 состояниями создан).
- print-CSS не проверен ни на одной странице.
- zoom 200% / DPR1 не покрыты.
- toast/success-подтверждения не инвентаризованы отдельно от inline-alert.

## Блокеры (§6 collision guard)

Параллельная сессия держит незакоммиченный WIP в: `base.css`, `app.js`, `stats.js`,
`focus-training.html`, `head.html`, `result.html`, `settings.html`, `stats.html`,
`TemplateFragmentContractTest.java`. Пока эти файлы dirty — порт/refactor затрагивающих
их поверхностей **BLOCKED**; берём независимые задачи (session-summary, error-макет,
mockup-recheck) или read-only аудит.

**Итог предподготовки (R0.28–R0.40, блокер жив 20 тиков):** runbook Фазы F
(PARITY_QA_CHECKLIST) + live-baseline instrument token-only (R0.30–31) + no-JS
static-смоук (R0.32) + дифф-ревизия маппингов против WIP (R0.33, sticky-запрет) +
численные ДО-портовые baseline всех 5 без-сессионных поверхностей (R0.34–R0.39,
@1280 + mobile-375). **Read-only повестка ИСЧЕРПАНА** — дальше либо разблокировка
base.css (→ Фаза E: error C7-i первым), либо maintenance-режим (сторожевые
проверки якорей черновиков при изменении WIP-диффа).
