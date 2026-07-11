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
| focus-training | RECHECK | RECHECK | RECHECK | RECHECK | RECHECK | TODO | TODO | TODO | B_MOCKUP_RECHECK |
| result | RECHECK | RECHECK | RECHECK | RECHECK | RECHECK | TODO | TODO | RECHECK | B_MOCKUP_RECHECK |
| session-summary | RECHECK | RECHECK | RECHECK | RECHECK | RECHECK | TODO | TODO | TODO | B_MOCKUP_RECHECK |
| settings | RECHECK | RECHECK | RECHECK | RECHECK | RECHECK | TODO | TODO | TODO | B_MOCKUP_RECHECK |
| stats | RECHECK | RECHECK | RECHECK | RECHECK | RECHECK | TODO | TODO | TODO | B_MOCKUP_RECHECK |
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

`*` full-width для error верифицирован **структурно** (edge-to-edge `.wrap` + `.error-grid`
main+рельс, брейк 1080px = проверенный паттерн других экранов), не живым screenshot
(capture в этой сессии флапал) — переподтвердить на bootRun при parity-фазе.

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
