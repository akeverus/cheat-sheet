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
| shell (chrome) | RECHECK | RECHECK | RECHECK | RECHECK | RECHECK | TODO | RECHECK | TODO | B_MOCKUP_RECHECK |
| focus-training | PASS | PASS‡ | PASS* | PASS | PASS(core)† | TODO | TODO | TODO | B_MOCKUP_RECHECK (recheck R0.6) |
| result | PASS | PASS‡ | PASS* | PASS | PASS(core)§ | TODO | TODO | RECHECK | B_MOCKUP_RECHECK (recheck R0.7) |
| session-summary | PASS | PASS | PASS* | PASS | PASS | TODO | TODO | TODO | B_MOCKUP_RECHECK (recheck R0.3) |
| settings | PASS | PASS | PASS* | PASS✦ | PASS | TODO | TODO | TODO | B_MOCKUP_RECHECK (recheck R0.8, +confirm R0.10) |
| stats | PASS | PASS♦ | PASS* | PASS | PASS(core)◊ | TODO | TODO | TODO | B_MOCKUP_RECHECK (recheck+fix R0.9) |
| error | PASS | PASS | PASS* | PASS | PASS | TODO | TODO | TODO | B_MOCKUP_RECHECK (макет R0.2) |

## Дименсиональные оси (кросс-поверхностные) — статус

| Ось | Статус | Заметка |
|---|---|---|
| 11 дизайн-вариантов | RECHECK | instrument добавлен (f2.1, AA-verified f2.2); остальные 10 — RECHECK. |
| light/dark | RECHECK | обе темы проверялись в прошлом раунде — переподтвердить на живом рендере. |
| viewports 375–2560 DPR2 | RECHECK | покрыты метриками прошлого раунда; screenshot был сломан (capture timeout). |
| DPR1 / zoom 200% | TODO | не покрыто ни разу. |
| no-js | TODO | result.html живой fallback (RECHECK); прочие — TODO. |
| print | TODO | не инвентаризован. |

`†` focus-training: покрыты 4 ядровых состояния (active/result/empty/done); НЕ покрыты
flashcard-reveal/grade, study-LEARN, generationUnavailable, loading, inline-alert/error,
no-js, diagram (CRITIQUE C12, coverage-gap; порт focus-training BLOCKED параллельным WIP).

`‡` focus-training AA: все ТЕКСТОВЫЕ пары ≥4.5 обе темы (light worst 4.95 badge, dark 5.24);
единственная пара ниже 4.5 — `opt-mark` галочка верного `success-on/success` = 4.28 light,
но это **графический объект** (WCAG 1.4.11, порог 3:1), `aria-hidden`, избыточна → compliant
(CRITIQUE C11, РЕШЕНО).

`§` result: покрыты correct/incorrect (mock-switch) + post-answer-analysis (takeaway/trace/
related) + favorite; НЕ покрыты confidence-виджет, regenerate, no-js-fallback (CRITIQUE C13,
coverage-gap). AA все текст-пары ≥4.5 обе темы (worst 5.52/5.24); opt-mark галочка = граф.
объект (C11). Наблюдение на порт: verdict `<p>` не live-region — `aria-live` (§8
`#result-feedback`) проводится при порте, не дефект макета. Порт result BLOCKED (WIP).

`✦` settings a11y образцовый WAI-ARIA: tablist (`role=tablist/tab/tabpanel`, roving tabindex,
стрелки+Home/End), seg-control `role=radiogroup`+`aria-checked`+стрелки, font-stepper
`role=status`+`aria-live=polite`, toggle focus-ring на `input:focus-visible+.switch`, label
for/id на select/input. Паритет §3 (#filters-form/#session-form)/§8. AA все пары ≥4.5 обе
темы (worst 4.95 btn-danger / 5.24 dark).

`◊` stats states: покрыты overview/charts+fallback/sortable-table/forecast/gaps; НЕ покрыты
empty/cold-start (осознан в проде) + search (CRITIQUE C16, coverage-gap). Порт stats BLOCKED (WIP).

_settings states — теперь ПОЛНЫ (R0.10):_ 3 вкладки + export json/csv + 7 осей + filters +
session + streak + **reset-options-confirm** (role=alertdialog, focus-trap, Esc, дефолт-фокус
на «Отмена», detector exit 0, AA обе темы worst 4.95) → CRITIQUE C14 **DONE**.

`♦` stats: все ТЕКСТ-пары ≥4.5 обе темы. Графобъекты-столбцы (3:1): найден провал амбер
`--spark`/`surface-2` = 2.06 light → **ПОФИКШЕН** (заливки данных → `--spark-ink`, 5.02
light / 8.72 dark; CRITIQUE C15). После фикса все столбцы ≥3:1. states: overview/charts+
fallback/sortable-table/forecast/gaps покрыты; НЕ покрыты empty/cold-start (осознан) + search
(C16). Порт stats BLOCKED (WIP).

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
