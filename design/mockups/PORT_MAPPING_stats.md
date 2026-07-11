# PORT MAPPING — stats (D_PORT_MAPPING, R0.24)

> Маппинг макета `design/mockups/stats.html` (язык **Instrument**) на прод-шаблон
> `modules/quiz-app/src/main/resources/templates/stats.html`. Цель — сделать порт
> (Фаза E) известной величиной. **Правок прод НЕ вносит.**
>
> ⚠️ **Каверза сверки:** stats.html + stats.js + base.css в незакоммиченном WIP
> параллельной сессии — маппинг снят с рабочего дерева 2026-07-12, пересверить перед портом.

## Контракт поверхности (нельзя ломать)

- **Роут:** `GET /stats`. Model: `stats`, `topicStats`, **`topicStatsJson`** (сырой JSON
  в `#topic-stats-data` через `[(...)]` — unescaped, иначе `&quot;` ломает JSON.parse),
  `groups`, `topics`, `filter`, `selectedGroup`, `searchQuery`, `searchResults`,
  `reviewForecast`, `reviewForecastMax`, `coverageGaps`.
- **JS:** `includeChart=true` (Chart.js), `stats.js v13` (два canvas-графика из
  topicStatsJson, сортировка таблицы: `aria-sort` на th + live-анонс `#table-sort-status`,
  `data-weak-topic-summary`), **два inline-скрипта в шаблоне**: collapse таблицы
  (12 строк, `MutationObserver` на пересортировку, PE: без JS таблица РАЗВЁРНУТА) и
  гуманизация дат прогноза (`Intl` ru-RU: «Сегодня»/«Завтра»/«6 июля», ISO остаётся
  в `<time datetime>`).
- **Порядок секций (сам по себе решение):** stats-grid → «Что делать дальше» →
  фильтры+поиск → empty-поиска → результаты поиска → stats-empty → графики →
  таблица тем → прогноз 7 дней → пробелы банка.
- **Mobile-режим таблицы:** stacked-card через `data-label` на td (C30) — макет этого
  не имеет, терять нельзя.

## Таблица маппинга (mockup → прод-хук → действие/риск)

| Mockup (Instrument) | Прод-хук (сохранить) | Действие / риск |
|---|---|---|
| `.stats-head` (kicker+h1) | заголовок в header-фрагменте (`pageTitle='Аналитика'`) | Kicker-строку НЕ добавлять без шаблонного решения; instrument-тон заголовка — из хрома. |
| `.stats-grid` (main + aside c форкастом/пробелами) | `.app-layout > .main-content` — ОДНА колонка, forecast/gaps — секции в потоке | **Решение:** шаг 1 порта = CSS-only рестайл текущей одноколонки; вынос прогноза/пробелов в правую рейку = перестройка DOM → шаг 2 (отдельный тик, если вообще нужен — на 375–1280 рейка всё равно складывается в поток). |
| `.action-card` ×3 (`.is-recommended`) | `.stats-next-action` ×3, `is-recommended` — **серверный каскад** (due>0 → wrong>0 → topics) + `data-weak-topic-summary` (наполняет stats.js) | Сохранить каскад и data-хук; restyle: ring-карты, mono-цифры `.action-num`≈`strong`. |
| `.ov-tile` обзор | фрагмент `stats-grid(stats)` | Restyle фрагмента (общий для result!) — трогает и result-страницу, проверить обе. |
| Статические SVG-графики + `.chart-legend` | `canvas#topicProgressChart/#topicAccuracyChart` + видимые фолбэки `#...Fallback` (`role=status`) | **SVG НЕ портировать** — это QA-заглушки макета; прод-график рисует Chart.js из topicStatsJson. Цвета/шрифты графиков задаются в stats.js-конфиге (не CSS!) → instrument-палитру графиков делать через CSS-переменные, читаемые в stats.js (`getComputedStyle`) ЛИБО отложить (stats.js dirty). Легенду макета сравнить с Chart.js legend — не дублировать. Фолбэк прода видимый (лучше макетного visually-hidden) — сохранить прод. |
| `.topic-search` (live-фильтр строк, `/` хоткей, счётчик, empty) | **нет прод-хука** (прод-поиск — серверный ПО ВОПРОСАМ, другая фича) | **НЕ путать фичи.** Клиентский фильтр ТЕМ поверх таблицы (∼318 строк + collapse 12) — ценный кандидат, но требует stats.js → **Фаза G кандидат** (аддитивный PE-скрипт по образцу R0.16: guard текст-полей, Esc-сброс, синк с collapse!). В шаг 1 порта НЕ входит. |
| `th.sortable` + `.arw`-стрелка в DOM | `th.sortable[data-col][data-sort-label]`, `aria-sort` ставит stats.js, анонс — `#table-sort-status` (C28) | Сохранить прод-паттерн; стрелку рисовать CSS `[aria-sort]::after`, DOM-span макета НЕ переносить. |
| `.cell-progress` (inline width) | `.topic-progress-bar` + `.topic-progress-learned/-due` c **`data-progress`-атрибутами** | Сохранить data-progress механизм (без inline-style). Двухслойный трек learned+due уже есть — только токены. |
| `.acc.lo/mid/hi` | `td.accuracy-cell` + `accuracy-high/-mid/-low` (серверные пороги 80/50) + title-подсказки | Сохранить серверные классы; instrument = mono, право-выравнивание допустимо CSS-ом. `—` для неотвеченных тем сохранить. |
| — (в макете нет) | collapse-механизм: `#topic-table-expander` + `data-collapse-rows=12` + MutationObserver-скрипт | Прод-фича без аналога в макете — сохранить целиком (PE!). |
| — (в макете нет) | фильтры (group/topic/important/onlyWrong) + серверный поиск по вопросам (2 GET-формы с ПЕРЕКРЁСТНЫМИ hidden-инпутами) + search-results/empty | Макет это пропустил осознанно. Сохранить обе формы дословно (hidden-синхронизация фильтров между формами — контракт), restyle контролов токенами. |
| `.fc-day` прогноз (сегодня/завтра/дни) | `ul.forecast-list > li.forecast-row`: `<time.forecast-day datetime=ISO>` + `.forecast-bar[role=img]` + `data-progress` fill + Intl-гуманизация | Сохранить `<time>`-семантику, role=img с aria-label склонений, data-progress. Restyle в fc-стиль макета (mono-подписи, тонкие треки). `is-today`-акцент допустим: первый li при `diff==0` — но это JS-правка → отложить, если stats.js/шаблон-скрипт dirty. |
| `.gap-row` + `.gap-chip` («4 из 12») | `table.data-table` (Тема/Вопросов) + caption | Таблица→чипы = смена DOM. Шаг 1: restyle таблицы токенами. Чип-вид «N из 5» — шаг 2 при желании (данные есть: total; порог 5 в hint). |
| aside `.rail-card-kicker` lowercase | `h2` секций | Сохранить h2-уровни (SR-навигация); lowercase-kicker — только как доп. стиль, не замена заголовка. |
| `<html data-design>` | SSR `data-design="editorial"` | Не менять (instrument opt-in). |

## Риски/решения для Фазы E (сводка)

1. **topicStatsJson `[(...)]` unescaped** — при любой правке шаблона не трогать этот блок
   (регресс = тихо мёртвые графики; фолбэк, правда, покажется).
2. **Цвета графиков живут в stats.js-конфиге Chart.js** — instrument-палитра графиков
   требует stats.js (dirty) → шаг 1 порта графики НЕ перекрашивает (допустимый
   временный рассинхрон, зафиксировать в parity-QA).
3. **Два inline-скрипта шаблона** (collapse + Intl-даты) — PE-паттерны, сохранить
   дословно; collapse-синк с будущим topic-фильтром (Фаза G) — отдельная задача.
4. **stats-grid фрагмент общий с result** — рестайл проверять на обеих страницах.
5. **Живой фильтр тем** (макет R0.16) — Фаза G кандидат (stats.js), НЕ шаг 1.
6. **Одноколонка сохраняется в шаге 1**; вынос прогноза/пробелов в рейку — шаг 2
   по отдельному решению.
7. **Порт требует stats.html + base.css (+ stats.js для графиков/фильтра)** — всё dirty;
   пересверка хуков перед портом обязательна.

## Готовность к порту

- **BLOCKED**: stats.html, stats.js, base.css dirty.
- Очередь Фазы E: error → session-summary (черновики) → focus-training → result →
  **stats** (этот маппинг) → settings → shell/head.
