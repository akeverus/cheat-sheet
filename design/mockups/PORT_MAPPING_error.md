# PORT MAPPING — error (D_PORT_MAPPING, R0.5)

> Маппинг разметки макета `design/mockups/error.html` (язык **Instrument**) на прод-шаблон
> `modules/quiz-app/src/main/resources/templates/error.html` и контракт
> `.design/editorial-redesign/SERVER_CONTRACT.md`. **Правок прод НЕ вносит** — план для Фазы E.

## Контракт поверхности (нельзя ломать)

- **Роут:** страница отдаётся Spring-обработчиком ошибок (`BasicErrorController`/`ErrorController`),
  НЕ обычным контроллером с моделью. Model-attrs приходят из атрибутов ошибки:
  **`status`** (Integer, может быть null), **`error`** (reason), **`message`**, **`path`**,
  **`timestamp`**. `summary`/`current` и т.п. здесь НЕТ.
- **Определение заголовка/тела — СЕРВЕРНОЕ** через `th:with code=${status!=null?status:500}` +
  тернарник по кодам 404/403/401/400/≥500. Никакого JS. **Порт обязан сохранить серверный
  тернарник** (макетный JS `STATES`/`?code=` — это скаффолд ревью, НЕ портируется).
- **head/header фрагменты:** `head(... includeChart=false, includeMermaid=false)`,
  `header(activePage='focus', showPrimaryNav=true, statsPage=false)`.
- `body.error-page` (print/скоуп-CSS завязан). `.skip-link → #main-content`.

## Таблица маппинга (mockup-элемент → прод-хук → действие/риск)

| Mockup (Instrument) | Прод-хук (сохранить) | Действие / риск |
|---|---|---|
| `body` | `body.error-page` | Сохранить класс. |
| `.skip-link → #main-content` | идентично | Сохранить дословно (текст «Перейти к сообщению об ошибке»). |
| `.mock-bar` (switcher 400/401/403/404/500) + `?code=` | — | **НЕ портировать** — mockup-скаффолд. В проде код приходит в `status`, ветвление серверное. |
| `.site-head` inline | `~{fragments/header}` | Заменить на прод-фрагмент header. |
| `.theme-toggle` (цикл auto/light/dark) | — | Скаффолд ревью. Тема персистится глобально (head pre-paint). НЕ портировать. |
| `main#main-content.error…[tabindex=-1]` | `main#main-content.ed-page[tabindex=-1]` | Сохранить `id`+`tabindex=-1`; `.ed-page` базовый каркас + классы Instrument. |
| `.error-grid` (main + `.error-rail`) | прод: одноколоночная `section.error-card` | **Новый layout (Фаза E, визуальный язык)** для мандата полной ширины. `section` несёт `aria-labelledby="error-heading"` — **сохранить связку с h2**. Рельс — статические навигационные ссылки, model-данные НЕ нужны. **Риск дубля:** рельс не должен повторять `.error-actions` — развести роли (actions = первичные пути, rail = вторичная «карта»/подсказки) или отказаться от рельса, если дублирует. |
| `.error-eyebrow` | `p.error-eyebrow[aria-hidden]` «Ошибка» | Сохранить, декоративный. |
| `.error-code` (крупный serif, декор) | `div.error-code[aria-hidden] th:text=${status}?:'×'` | Значение из `status`, фолбэк `×` для не-числовых. `aria-hidden` (реальный заголовок — `.error-title`). Сохранить. |
| `.error-title` | `h2#error-heading.error-title` `th:with`+тернарник | **КРИТ:** сохранить `id=error-heading` (на него `aria-labelledby`) и **серверный тернарник** title по коду. Макетный JS-словарь заголовков выкинуть. |
| `.error-body` | `p.error-body` `th:with`+тернарник | Тексты причин по коду — **серверные** (404/403/401/400/≥500). Порт переносит прод-копию дословно (она уже человекочитаемая, RU). Макетный JS-body выкинуть. |
| `.error-details` | `details.error-details th:if=(message непустой) or (path!=null)` | Сохранить `<details><summary>` + `dl.error-meta` с условными `dt/dd` для `path`/`status`/`error`/`message`/`timestamp`. `code` для path/message. Все `th:if` сохранить — dev-мета показывается только когда есть. |
| `.error-actions` | `nav.error-actions[aria-label="Что дальше"]` | Сохранить `nav`+`aria-label`. 3 ссылки: `/`(На главную, `.next-btn`), `/stats`(Аналитика), `/settings`(Настроить сессию) — обе `.secondary-btn`. Классы Instrument (`.btn-primary/.btn-ghost`) — визуальный слой поверх прод `.btn.next-btn/.secondary-btn`. |

## Риски/решения для Фазы E (сводка)

1. **Серверное ветвление по коду** — весь title/body/error-code driven из `status`; порт НЕ
   вводит JS-ветвление, сохраняет `th:with`+тернарники. Макетные `STATES`/`?code=`/switcher —
   только для ревью, в прод не идут.
2. **`.error-rail` vs `.error-actions`** — не плодить два одинаковых набора «куда пойти».
   Решение при порте: либо развести (actions=первичный CTA, rail=вторичные подсказки/ссылки
   на разделы), либо не портировать рельс и добиться полной ширины иначе. Полноширинность
   макета верифицирована структурно (R0.2), но рельс не обязателен для неё.
3. **`aria-labelledby="error-heading"`** на `section` + `id` на `h2` — сохранить связку.
4. **Нет `app.js`-зависимостей** — error-страница статична; никакого PE-скрипта не требуется
   (в отличие от session-summary с copy/print). Проще всего из независимых поверхностей.
5. **SSR `data-design="editorial"`** не менять (Instrument opt-in, решение пользователя).

## Готовность к порту

- `error.html` прод **чист** (не в параллельном WIP) → порт можно ставить в очередь Фазы E
  отдельным тиком по §5 (explicit pathspec). Из двух независимых поверхностей error —
  **простейшая** (нет таблиц с ARIA-ролями, нет PE-инъекции, нет summary-модели).
- Обе независимые поверхности (session-summary R0.4, error R0.5) теперь **MAPPED** —
  маппинг-фаза для доступного фронта замкнута. Дальнейший прогресс упирается в:
  (а) вход в Фазу E (порт) — как только берём отдельный тик на правку чистого прод-файла;
  (б) разблокировку 5 BLOCKED-поверхностей (ждём коммита параллельной сессии).
