# FRONTEND_BACKLOG — открытые задачи FINALIZATION ROUND

> Машиночитаемый статус и очередь — в `FRONTEND_STATE.json` (единственный источник).
> Правила/фазы/гейты — в `PLAN_FRONTEND.md`. Решения — в `FRONTEND_DECISIONS.md`.
> Evidence — в `FRONTEND_EVIDENCE.md`. История — в `FRONTEND_HISTORY.md`.
> Этот файл — читаемое раскрытие задач для человека; **при расхождении с JSON правит JSON.**

Приоритеты: **P0** блокируют финал · **P1** MUST-фичи core-flow · **P2** UX/cleanup/perf/a11y · **P3** мелочи.
Обновлено: **2026-07-16 (R0.198)** — сверено со STATE-json (было устаревшее bootstrap-состояние 2026-07-15).

---

## Активный блокер

**BLK-BOOTRUN** — живой devtools-bootRun на `:8080` (app-JVM PID 64174; gradle project-lock держит PID 40113). `./gradlew test/build/check` при нём = wedge. Блокирует всё с `needsGradle=true`. Разрешение: пользователь останавливает bootRun на gradle-волну. Пока жив — доступны только bootRun-safe задачи (docs + read-only live-QA).

**Статус bootRun-safe пула: ИСЧЕРПАН (R0.197).** Все чистые CSS/template-срезы и read-only верификации сделаны. Дальнейший прогресс к FINALIZED требует остановки bootRun.

---

## Что осталось до FINALIZED (ВСЁ требует bootRun-stopped тик)

| ID | Задача | Статус | Тип работы |
|---|---|---|---|
| **UX-13** | Прогрессивный разбор (минимум/глубже) — код закоммичен R0.184, нужна перекомпиляция backend `AnswerHtmlSplitter` + faithful live-QA | OPEN | recompile + verify |
| **FLOW-01** | Pause/resume (b: resume-баннер + `pausedInfo` model-attribute) | IN_PROGRESS | Java + шаблон |
| **FLOW-04** | Recoverable expired/offline/error UI-баннеры | OPEN | Java + шаблон |
| **BP-STATS-1** | CSP `connect-src` для sourcemap chart.js → Lighthouse BP=100 + consoleNetworkClean на /stats (двойной gate одним фиксом) | BLOCKED | 1 строка `SecurityConfig.java` |
| **PERF-02** | Prod-like budget-замер (prod-профиль-билд + 4G/CPU-throttling); font-prune уже DONE | OPEN | prod build + Lighthouse perf |
| — | **sync app.js** v=68→70 в build (несёт UX-13+report JS; сейчас не синхронизирован из-за entanglement) | — | после recompile backend |
| — | **deferred gradle-тесты**: FLOW-01a/03a/SUMMARY-a/REPORT-a + `TemplateFragmentContractTest` + REVIEW-03/FB-1 регрессия | — | `./gradlew :quiz-app:test` |
| — | **финальный consolidated gate** (Раздел 9): cleanBuild/fullTests/prodSmoke/coreUxFlows + re-run 4 провизорных gate против recompiled app → roundStatus FINALIZED → CronDelete | — | финальная волна |

**Провизорно закрытые gate** (зелёные на текущем live-build, ждут re-run против перекомпилированного/prod app): `responsive` (R0.192/193, 320–2560), `accessibility` a11y=100 (R0.194 Lighthouse + R0.197 ручной keyboard/SR), `consoleNetworkClean` (R0.195, весь долг = BP-STATS-1), `performanceProdLike` структура (R0.196, LCP 265/283 CLS 0.00).

---

## P0 — процесс/дизайн-фундамент — ✅ ВСЕ DONE

| ID | Задача | Статус |
|---|---|---|
| PROC-01 | Loop конечный + FINALIZED | ✅ DONE |
| PROC-02 | Единая каденция + work-package model | ✅ DONE |
| PROC-11 | Разделить giant PLAN → state/backlog/decisions/history | ✅ DONE |
| PROC-12 | Единственный machine-readable state | ✅ DONE |
| PROC-07 | Wide-layout модель B (full-width + правый рельс) | ✅ DONE (R0.183) |
| FE-CMP-1 | Завершить 'полную замену' (удалить мёртвую ось дизайна) | ✅ DONE (R0.173) |

---

## P1 — MUST-фичи (core training flow)

| ID | Задача | Статус |
|---|---|---|
| FLOW-02 | Idempotent submit + анти-двойной-POST | ✅ DONE |
| FLOW-03 | 'Не знаю' outcome (UNKNOWN/SKIPPED) | ✅ DONE |
| FLOW-SUMMARY | Actionable summary (unknown/due/typed-next) | ✅ DONE |
| FLOW-REPORT | Question issue reporting | ✅ DONE |
| PERF-01 | Cache-busting: single-source app.js | ✅ DONE |
| QA-01 | Детерминированный QA profile/fixtures | ✅ DONE |
| **FLOW-01** | Pause/resume сессии | ◐ IN_PROGRESS (b-half: resume-баннер) |
| **FLOW-04** | Recoverable expired/offline/error | ⛔ OPEN (Java) |

---

## P2 — UX / cleanup / perf / a11y

| ID | Задача | Статус |
|---|---|---|
| UX-01 | Иерархия CTA на empty/done | ✅ CLOSED (Instrument-порт) |
| UX-02 | Унификация terminal ↔ shell | ✅ CLOSED (Instrument-порт) |
| UX-03 | Убрать градиент/блик с primary CTA | ✅ CLOSED (Instrument-порт) |
| UX-05 | Русская плюрализация серии (backend) | ✅ DONE |
| UX-15 | Adaptive next action (итог) | ✅ CLOSED (поглощён FLOW-SUMMARY) |
| UX-16 | Analytics next-action блок | ✅ CLOSED (Instrument-порт) |
| REVIEW-03 | Mobile sticky submit-панель | ✅ DONE (R0.189) |
| FB-1 | Inline invalid-визуал формы | ✅ DONE (R0.190) |
| CLEAN-01B | Мёртвый селектор base.css (код-блок отступ) | ✅ DONE (исправлен: `> pre \| .code-copy-wrap`) |
| A11Y-01 | Консолидированный SR/keyboard проход | ✅ DONE (R0.170, ре-verify R0.197) |
| **UX-13** | Прогрессивный разбор (минимум/глубже) | ⛔ OPEN (код закоммичен R0.184, нужна перекомпиляция) |
| **PERF-02** | Prod-like perf budgets | ◐ OPEN (font-prune done; budget-замер нужен prod-like) |
| **BP-STATS-1** | Lighthouse BP=92 /stats: CSP sourcemap chart.js | ⛔ BLOCKED (Java) |

---

## SHOULD (из §4 FINAL-плана — если без архитектурного переворота)

Причина показа вопроса (UX-08) · autosave status (UX-09) · группировка ошибок по misconception (UX-14) · persistence фильтров аналитики (UX-17) · текстовые эквиваленты графиков (UX-18, частично есть showChartFallback) · упрощённые presets настроек (UX-19).

## FUTURE (НЕ держат loop открытым — записать, не реализовывать)

offline/PWA · social/share expansion · новый content pipeline · adaptive ML · **новые дизайн-варианты** · геймификация сверх серии.
