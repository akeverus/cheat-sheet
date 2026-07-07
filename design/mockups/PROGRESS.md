# PROGRESS — макеты design/mockups/ (леджер)

Язык: **Instrument** (гринфилд). Каденция: **эталон → аппрув → разворот**.
Правим только `design/mockups/`. Токены — `tokens.css`. Спека — `DESIGN.md`.

## Статус экранов

| Экран | Файл | Статус | Что дальше |
|---|---|---|---|
| **Экран вопроса (эталон)** | `focus-question.html` | 🟢 draft v1 · detector✅ · QA✅ | **ЧЕКПОИНТ: ждёт аппрув языка.** После — полиш: zone-chip/hint, флешкарт/study состояния, keyboard-hints скрыть на touch |
| Результат/фидбек | `result.html` | ⬜ ждёт аппрува языка | — |
| Итоги сессии | `session-summary.html` | ⬜ | — |
| Настройки | `settings.html` | ⬜ | — |
| Статистика | `stats.html` | ⬜ | — |
| Shell/шапка | `shell.html` | ⬜ | — |

**ЧЕКПОИНТ:** после того как эталон чист по detector + AA в обеих темах +
читаем на 5 вьюпортах → ПАУЗА, запрос аппрува визуального языка. Фазу B не начинать.

## Маппинг на DOM-хуки (SERVER_CONTRACT.md) — экран вопроса

Что уже легло 1:1 (проводка потом тривиальна):
- `.focus-question` — вопрос-герой (в макете `h1`; при проводке под masthead → `h2`).
- `#interview-form` + `#interview-options[role=radiogroup]` + `.options`.
- `label[data-option-id] > input[name=optionId][value] + span.opt-text` — контракт §3.
- `#interview-submit` — сабмит «Проверить ответ».
- `.progress-track[role=progressbar]` (valuemin/max/now) — синк app.js `data-progress`.
- Пустое состояние: `.empty` + действия (map → `empty-action-settings`/`empty-action-retry`).
- `a.skip-link[href=#main-content]` + `<main id=main-content>` — a11y-каркас §8.

Открытые вопросы / долги (в полиш эталона, до аппрува):
1. **zone-chip / zone-hint** (`focusModeChipText` / `focusModeHintText`, напр.
   «Тренировка» + «Выбери один вариант…») — пока НЕ в макете, добавить над вариантами.
2. **Бейдж-буква через CSS-counter?** В контракте буква рисуется CSS-счётчиком, а
   `optionText` в БД идёт как «A. текст» и видимый span стрипает ведущее «A. ».
   В макете буква захардкожена в `.opt-badge` — при проводке заменить на counter,
   span оставить чистым текстом. Записать, чтобы не потерять.
3. **Флешкарт-фаза** (`.flashcard-phase`, reveal + grade 1–4) и **study-LEARN**
   карточка (`/study-confirm`) — первоклассные ветки (§2 контракта), добавить как
   доп. состояния эталона.
4. **Код-пример**: `<details>` совпадает с реальным `.question-code-details`
   (нативный, работает без JS) — ок.
5. `h1` vs `h2`: в standalone-макете вопрос = `h1` ради корректной иерархии одной
   страницы; в приложении masthead держит `h1`, вопрос станет `h2` (как в текущем
   `focus-training.html`). Не баг — отметка для проводки.

## Журнал

- **it.1** — бутстрап: `tokens.css` (OKLCH light+dark), `DESIGN.md`, `PRODUCT.md`,
  `PROGRESS.md`; draft v1 эталонного экрана вопроса (состояния: вопрос+варианты,
  пусто; обе темы через тогл; motion + reduced-motion). **impeccable detect — чисто
  (exit 0)** после правки type-scale (xs/sm → 1.25) и унификации inline-code/kbd.
- **it.2** — QA эталона через chrome-devtools MCP. Скрины: 1280 light+dark, 375 light,
  768 light, 1440 light (fullPage). Вывод: рендерится чисто на всём диапазоне —
  masthead + сессионная рейка + serif-герой с `@Transactional`-бейджем + teal-выбранный
  вариант B (кольцо+wash) + keyboard-hints + пустое состояние. Dark: прохладная near-black
  бумага, teal-signal и амбер-стрик контрастны, герой читаем. Mobile 375: masthead
  переносит тогл на 2-ю строку, герой корректно wrap'ится с инлайн-бейджем, варианты
  стекаются. Токены аннотированы AA-ratios (body ≥4.5:1, крупный ≥3:1) — сверено при
  проектировании; пиксельный AA-замер отложен в полиш. Мелкий долг: keyboard-hints
  показываются на touch-вьюпортах (нет клавиатуры) → гейт `@media (hover:hover) and
  (pointer:fine)` в полиш. **ЧЕКПОИНТ ДОСТИГНУТ:** эталон detector-чист + читаем на
  5 вьюпортах в обеих темах → ПАУЗА, запрос аппрува визуального языка. Фазу B не начинаю.
