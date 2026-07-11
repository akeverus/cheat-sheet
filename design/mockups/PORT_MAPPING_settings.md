# PORT MAPPING — settings (D_PORT_MAPPING, R0.25)

> Маппинг макета `design/mockups/settings.html` (язык **Instrument**) на прод-шаблон
> `modules/quiz-app/src/main/resources/templates/settings.html`. **Правок прод НЕ вносит.**
>
> ⚠️ **Каверза сверки:** settings.html + base.css + app.js в чужом WIP — маппинг снят
> с рабочего дерева 2026-07-12, пересверить перед портом.
>
> **Хорошая новость:** прод УЖЕ прошёл вкладочный редизайн (0eb762a5: ARIA-вкладки
> Сессия/Оформление/Данные, PE, все form-ID сохранены) — структуры макета и прода
> близки как нигде; порт по большей части = токен-рестайл.

## Контракт поверхности (нельзя ломать)

- **Роут:** `GET /settings`. Model: `stats`, `resetDeleted` (toast), `groups`, `topics`,
  `filter`, `selectedGroup`, `weakTopics`, `mode`, `interviewSession`.
- **Формы (ID зафиксированы TemplateFragmentContractTest!):** `#filters-form`
  (GET /settings) и `#session-form` (POST /start) с hidden-зеркалами фильтров;
  `#session-mode-select` с **`data-default-count`** per-mode (SET-15: EXAM/TRAINING=20,
  остальные=50, источник application.yml); `#session-count-field` (TRAINING скрывает
  счётчик — initSessionModeForm); POST /finish (при живой сессии);
  POST /settings/reset-options c `data-confirm`-гардом.
- **PE-паттерны app.js:** tablist скрыт без JS (`initSettingsTabs` добавляет `.js-tabs`;
  без JS — все панели стопкой); `#personalization-card` скрыт без JS
  (`initPersonalization`); экспорт скрыт без JS (`initExportButtons` — fetch с
  заголовком `X-Admin-Token`, потому кнопки, не ссылки); streak-bar наполняет app.js.
- **7 осей персонализации:** seg-controls `role=radiogroup` с data-хуками
  `data-design-pref` (11 значений, instrument уже в списке) / `data-theme-pref` /
  `data-layout-pref` / `data-reading-width-pref` / `data-density-pref` /
  `data-motion-pref` + font-stepper (`#font-decrease/-increase/-reset`,
  `#font-scale-value` role=status). Паттерн добавления оси = 4 касания (память
  project_personalization_settings) — не ломать.
- **Фрагменты:** today-hero, stats-grid-content.

## Таблица маппинга (mockup → прод-хук → действие/риск)

| Mockup (Instrument) | Прод-хук (сохранить) | Действие / риск |
|---|---|---|
| `.settings-head` (kicker+h1) | header-фрагмент (`pageTitle`) + today-hero виджет | Kicker не добавлять; hero-виджет сохранить над вкладками (гланс-герой, решение прода). |
| `.settings-grid` + `aside.settings-rail` (ВЕРТИКАЛЬНЫЙ tablist, `aria-orientation=vertical`, иконки) | `.settings-shell` + ГОРИЗОНТАЛЬНЫЙ `#settings-tablist` над панелями | **Шаг 2 (не шаг 1):** вертикализация = шаблон (aria-orientation) + app.js (стрелки ↑/↓ вместо ←/→ по WAI-APG) + иконки спрайта. Шаг 1 — рестайл горизонтальных вкладок токенами. |
| `.settings-panel-lead` (лид-абзац панели) | нет (только h2) | Шаг 2, микро-добавка шаблона (копию согласовать). |
| `.launcher` 2 колонки | `.session-launcher` + `#launcher-filters/#launcher-session` | Совпадает — рестайл. Все name/hidden/labels сохранить дословно. |
| `label.toggle` (switch-вид, текст→input) | `label.toggle-item` (checkbox→текст) | Switch-вид достижим CSS-ом на прод-DOM (`input:checked + span`, фокус-кольцо `input:focus-visible`); порядок узлов НЕ менять. |
| `.set-grid > .set-card` (head: label+hint / control) | `.personalization-grid > .personalization-row` (label / seg-control / hint) | Структуры изоморфны → карточный вид = CSS-only (grid auto-fit `minmax(min(20rem,100%),1fr)` как в макете R0.18; ряд→карта). ID/data-хуки всех 7 осей сохранить. |
| `.step-btn/.step-value/.step-reset` | `#font-decrease/#font-scale-value/#font-increase/#font-reset` + классы btn | Сохранить ID; restyle. |
| `.streak` статичный (inline width 50%) | `#streak-bar` hidden + data-streak-* (наполняет app.js) | Сохранить прод-механизм (данные живые); restyle трека/заливки. `--spark-ink` для заливки (класс C15/C17!). |
| Экспорт json/csv | `#data-export-block` hidden + `data-export-format` кнопки + `#export-status` | Сохранить PE+fetch-паттерн (admin-токен в заголовке); restyle. |
| `reset-options-confirm` (R0.10: `role=alertdialog`, focus-trap, Esc, дефолт-фокус «Отмена») | `data-confirm` → нативный `confirm()` (app.js) | **Решение:** нативный confirm доступен и прост — шаг 1 сохраняет его. Кастомный alertdialog = app.js-обвязка → кандидат Фазы G (низкий приоритет: UX-выигрыш мал, риск focus-trap регрессий). |
| `.danger-zone` (error-wash, полная рамка) | `.danger-zone` (уже error-wash + полная рамка, НЕ side-stripe) | Совпадает концептуально — только токены. |
| toast `resetDeleted` | `p.toast.toast-success` над вкладками | Сохранить позицию (виден с любой вкладки); restyle. |
| `<html data-design>` | SSR `data-design="editorial"` | Не менять (instrument opt-in). |

## Риски/решения для Фазы E (сводка)

1. **Контракт-тест держит ID форм и вкладок** — прогонять после любой правки шаблона.
2. **Шаг 1 = чистый CSS-рестайл** (карточная сетка осей, switch-тогглы, вкладки,
   danger-zone) — прод-DOM уже совпадает с макетом по структуре.
3. **Шаг 2 (по отдельному решению):** вертикальный tablist-рельс (шаблон+app.js
   стрелки+иконки), panel-lead копия.
4. **Alertdialog-подтверждение сброса** — НЕ шаг 1; кандидат Фазы G с низким
   приоритетом (нативный confirm доступен).
5. **`data-default-count`/TRAINING-скрытие счётчика** — поведенческий контракт SET-15,
   не задеть селекторами.
6. **Порт требует settings.html + base.css (+ app.js для шага 2)** — всё dirty.

## Готовность к порту

- **BLOCKED**: settings.html, base.css, app.js dirty.
- Очередь Фазы E: error → session-summary (черновики) → focus-training → result →
  stats → **settings** (этот маппинг) → shell/head (последний D-маппинг).
