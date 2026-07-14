# FRONTEND_BACKLOG — открытые задачи FINALIZATION ROUND

> Машиночитаемый статус и очередь — в `FRONTEND_STATE.json` (единственный источник).
> Правила/фазы/гейты — в `PLAN_FRONTEND.md`. Решения — в `FRONTEND_DECISIONS.md`.
> Evidence — в `FRONTEND_EVIDENCE.md`. История — в `FRONTEND_HISTORY.md`.
> Этот файл — читаемое раскрытие задач для человека; при расхождении с JSON правит JSON.

Приоритеты: **P0** блокируют финал · **P1** MUST-фичи core-flow · **P2** UX/cleanup/perf/a11y · **P3** мелочи.
Обновлено: **2026-07-15** (bootstrap R0.165). Источник backlog — Раздел 6 FINAL-плана + ground-truth inventory (workflow `wpi2y5071`, 7 срезов).

---

## Активный блокер

**BLK-BOOTRUN** — живой devtools-bootRun на `:8080`. `./gradlew test/build/check` при нём = wedge. Блокирует всё с `needsGradle=true`. Разрешение: пользователь останавливает bootRun на gradle-волну (один собранный вопрос по контракту §7.3). Пока жив — берём bootRun-safe задачи (docs + live-QA read-only).

---

## P0 — процесс/дизайн-фундамент

| ID | Задача | Статус | needsGradle |
|---|---|---|---|
| PROC-01 | Loop конечный + FINALIZED | ✅ DONE | нет |
| PROC-02 | Единая каденция + work-package model | ✅ DONE | нет |
| PROC-12 | Единственный machine-readable state | ✅ DONE | нет |
| PROC-11 | Разделить giant PLAN (→ ужать PLAN + HISTORY) | ◐ IN_PROGRESS | нет |
| PROC-07 | Сверить wide-layout (контракт vs реальность) | ⛔ BLOCKED (DEC-003) | да |
| FE-CMP-1 | Завершить 'полную замену' (удалить мёртвую ось дизайна) | ⛔ BLOCKED (bootRun) | да |

**PROC-11 (next):** перенести из `PLAN_FRONTEND.md` §7 (матрица) / §9 / §21 (лог R0.x) в `FRONTEND_HISTORY.md`, ужать PLAN до ≤300 строк (философия · фазы · release-gates · pointer'ы). bootRun-safe.

**FE-CMP-1:** inventory-факт — `/settings` рендерит **11** кнопок дизайна (`editorial…instrument`, коммент 'Дизайн (11 кнопок)'), но `head.html DESIGNS=['instrument']` → `setDesign()` коерсит любой не-instrument обратно в instrument. Пользователь видит 11 выборов, получает 1. Убрать: ось `data-design-pref` из settings.html; `#design-toggle`+его inline-скрипт (108-130) из header.html; dead SIGNATURE-блоки base.css (editorial ~L3438, swiss ~L3535, linear ~L3519, stripe L846, claude L3500); THEME_COLORS 11→1 в head.html; бампнуть `?v` tokens+base (закрывает старый CACHE-1). Обновить `TemplateFragmentContractTest` (снять design-axis ассерты). → `./gradlew :quiz-app:test` при остановленном bootRun.

**PROC-07:** см. DEC-003 — заблокировано конфликтом с осознанным выбором пользователя.

---

## P1 — MUST-фичи (core training flow), все `needsGradle=true` → ждут BLK-BOOTRUN

| ID | Задача | Backend-state (inventory) |
|---|---|---|
| FLOW-01 | Pause/resume сессии | PARTIAL — неявное resume из HttpSession; нет endpoint/флага/персистентности |
| FLOW-02 | Idempotent submit + анти-двойной-POST | PARTIAL — только клиентский aria-disabled; серверной идемпотентности нет |
| FLOW-03 | 'Не знаю' outcome (UNKNOWN) | ABSENT — нет enum/endpoint/DTO; /answer требует optionId |
| FLOW-04 | Recoverable expired/offline/error | PARTIAL — есть GlobalExceptionHandler; нет expired-UX/autosave-подтверждения |
| FLOW-SUMMARY | Actionable summary (unknown/due/typed-next) | PARTIAL — логика в Java ✓; нет unknown/due/typed-next |
| FLOW-REPORT | Question issue reporting | ABSENT — нет таблицы/endpoint/UI |
| QA-01 | Детерминированный QA profile/fixtures | ABSENT — только prod/test; Clock уже инъектируем |
| PERF-01 | Cache-busting: single-source app.js | app.js v=65 в 3 шаблонах (drift-риск), css уже single-sourced |

Детали backend-флоу (sessionModel/answerFlow/SM-2/миграции) — в `FRONTEND_EVIDENCE.md`. Последняя миграция **V15**; PostgreSQL only; новые фичи → V16+.

---

## P2 — UX / cleanup / perf / a11y

| ID | Задача | needsGradle | Заметка |
|---|---|---|---|
| UX-01 | Иерархия CTA на empty/done | нет | bootRun-safe |
| UX-02 | Унификация terminal ↔ shell | нет | bootRun-safe |
| UX-03 | Убрать градиент/блик с primary CTA | нет | bootRun-safe |
| UX-05 | Русская плюрализация серии backend-side | да | дедуп 2 JS |
| UX-13 | Прогрессивный разбор (минимум/глубже) | нет | согласовать с result.html |
| UX-15 | Adaptive next action (итог) | да | пересечение с FLOW-SUMMARY |
| UX-16 | Analytics next-action блок | да | частично есть |
| CLEAN-01B | Мёртвый base.css:754 (код-блок отступ) | нет | подтверждён inventory |
| A11Y-01 | Консолидированный SR/keyboard проход | нет | evidence-driven |
| PERF-02 | Prod-like perf budgets + prune fonts | нет | не на devtools-bootRun |

---

## SHOULD (из §4 FINAL-плана — если без архитектурного переворота)

Причина показа вопроса (UX-08) · autosave status (UX-09) · группировка ошибок по misconception (UX-14) · persistence фильтров аналитики (UX-17) · текстовые эквиваленты графиков (UX-18, частично есть showChartFallback) · упрощённые presets настроек (UX-19).

## FUTURE (НЕ держат loop открытым — записать, не реализовывать)

offline/PWA · social/share expansion · новый content pipeline · adaptive ML · **новые дизайн-варианты** · геймификация сверх серии.
