# PORT MAPPING — shell / хром (D_PORT_MAPPING, R0.26 — ФИНАЛЬНЫЙ маппинг фазы D)

> Маппинг макета `design/mockups/shell.html` (язык **Instrument**: шапка, нав, тогглы,
> kbd-help, футер) на прод-хром: `fragments/header.html` (чист) + `fragments/head.html`
> (**dirty WIP**) + `fragments/icons.html` + `fragments/today-widget.html` + app.js
> (kbd-help, **dirty**). **Правок прод НЕ вносит.**

## Контракт поверхности (нельзя ломать)

- **Сигнатура фрагмента:** `header(pageTitle, statsPage, activePage, showPrimaryNav)` —
  вызовы во всех 6+ шаблонах.
- **Состав header.html:** SVG-спрайт (один на страницу), noscript-алерт, брендинг
  (`.ed-masthead-brand`: kicker + h1 pageTitle), `.ed-nav` (3 ссылки, `aria-current=page`),
  `.ed-masthead-toggles` — три PE-тоггла (`#layout-toggle`/`#design-toggle`/`#theme-toggle`,
  рендерятся hidden, раскрывает inline-скрипт → без JS мёртвых контролов нет),
  progressbar-полоса (`th:if progressPercent`), 4 inline-скрипта (theme/design/layout
  синк по событиям `themechange/designchange/layoutchange` + back-to-top с гейтом
  «Движение» data-motion).
- **head.html (dirty WIP):** pre-paint init `window.__theme/__design/__layout/…` из
  localStorage (анти-FOUC), список дизайнов (instrument УЖЕ в реестре), загрузка
  Google Fonts — **Newsreader + Space Grotesk уже подключены** (коммент строки 269:
  «Newsreader — serif-display "Instrument"») → шрифтовой риск порта ЗАКРЫТ.
- **kbd-help:** прод-модалку инжектит app.js (`.kbd-help-overlay/-modal`,
  aria-labelledby=kbd-help-title, гейт юзера 0a5a5cc6) — хук существует.
- **today-widget:** фрагменты today-chip/today-hero (due серверный, стрик app.js).

## Таблица маппинга (mockup → прод-хук → действие/риск)

| Mockup (Instrument) | Прод-хук (сохранить) | Действие / риск |
|---|---|---|
| `.site-head` sticky, `.brand` + point-mark | `.ed-masthead` + `.ed-masthead-brand` (kicker «Cheat·Sheet» + h1) | Restyle токенами. **Sticky НЕ портировать — РЕШЕНО R0.34:** прод-контракт-тест (WIP TemplateFragmentContractTest) ассертит `doesNotContain(".ed-masthead { position: sticky")`, обоснование FNO-риск; макетное решение C3 (sticky+solid) остаётся макетным. h1-в-шапке сохранить (заголовок страницы = бренд-блок, контракт вызовов). |
| `.site-nav` (3 ссылки, `aria-current`) | `.ed-nav` (те же 3, `is-active` + aria-current) | Совпадает — только токены. |
| `.nav-menu-btn` + мобильный drawer (`data-open`) | **нет** — прод-нав всегда видима (3 ссылки переносятся) | **НЕ портировать drawer**: 3 пункта не требуют бургера; прод-паттерн проще и без JS. Мобильную вёрстку нав-строки проверить на parity (375px). |
| `.icon-btn#kbd-help-open` (?-справка в шапке) | app.js-модалка `.kbd-help-overlay` (хоткей `?`), кнопки в шапке нет | Кнопку-триггер в шапку НЕ добавлять в шаг 1 (открытие по `?` — существующий гейт-паттерн юзера); видимый триггер = кандидат шага 2 (правка header.html + app.js). |
| `.theme-toggle` (цикл авто/светлая/тёмная, 3 иконки) | `#theme-toggle` — флип light↔dark, «Авто» только на /settings | Прод-паттерн сохранить (осознанное разделение: быстрый флип в шапке, полный выбор в настройках); restyle иконки/кольца. |
| — (в макете нет) | `#design-toggle` + `#layout-toggle` (циклы по __design.list/__layout.list) | Прод-фичи без макетного аналога — сохранить. **НАХОДКА:** в NAMES дизайн-тоггла НЕТ `instrument` → aria/title покажут сырой id «instrument» (фолбэк label(id)); фолбэк-список order() тоже без instrument (реальный источник — __design.list из head.html, там есть). Микро-фикс: +1 запись NAMES (+фолбэк-список) в header.html — независим от dirty-файлов, кандидат ближайшего свободного тика Фазы E. |
| `.streak-chip` (стрик в шапке) | today-chip/today-hero фрагменты на страницах (не в шапке) | Позицию НЕ менять (шапка минимальна — решение прода); restyle чипа в рамках порта страниц. |
| kbd-help модалка макета (focus-trap, Esc, 9 строк) | app.js `.kbd-help-overlay/-modal` | Уже эквивалент в проде — при порте только токен-рестайл классов модалки в base.css. |
| `<footer>` (макет) | **футера в проде НЕТ вообще** (ни одного `<footer>`) | **НЕ портировать без решения пользователя**: добавление футера на все страницы = продуктовое решение (лишний хром на «фокусной» тренировке спорен). Записать как открытый вопрос Фазы G. |
| noscript-алерт | `.ed-noscript` inline-alert в header | Сохранить; instrument-тон через существующие `.inline-alert`-токены. |
| progressbar шапки | `.ed-masthead-progress` (`th:if progressPercent`, data-progress) | Сохранить механизм; restyle цвета трека/заливки. |
| pre-paint тема/дизайн | head.html `window.__*` init | Не трогать логику; порт shell = только CSS (+бамп v= обеих css в head.html при применении). |

## Риски/решения для Фазы E (сводка)

1. **Порт shell = почти чистый base.css-рестайл** — DOM header.html менять не требуется
   (drawer/футер/?-кнопка осознанно не идут в шаг 1).
2. **Микро-фикс NAMES+order() (instrument)** в header.html — независимая 2-строчная
   правка, header.html ЧИСТ → можно сделать отдельным тиком Фазы E не дожидаясь
   base.css (единственная разблокированная прод-правка сейчас).
3. **Шрифты instrument уже в head.html** (Newsreader/Space Grotesk) — риск снят.
4. **4 inline-скрипта header.html** (theme/design/layout/back-to-top) — контракт
   событий `*change` и PE-паттерн hidden→reveal сохранить дословно.
5. **Футер** — открытый вопрос пользователю (Фаза G), в порт не входит.
6. **Sticky-шапка — ЗАПРЕЩЕНА, вопрос закрыт (R0.34):** контракт-тест прода
   явно банит `position: sticky` у `.ed-masthead` (FNO-риск) и пиннит точные
   строки base.css → инструмент-порт shell не меняет позиционирование шапки;
   после вставки CSS — обязательный прогон TemplateFragmentContractTest
   (НЕ при живом bootRun — wedge).
7. **Уплотнение chrome при активной сессии** (WIP: `.focus-page
   .ed-masthead:has(.ed-masthead-progress)` + compact-clamp тайтла ≤600, HDR-1) —
   instrument-порт shell/focus НЕ перебивает эти правила (не задавать font-size
   тайтла шапки на focus-page и не трогать padding-block masthead-inner).

## Готовность к порту

- shell CSS — BLOCKED (base.css dirty); микро-фикс NAMES — **НЕ блокирован** (header.html чист).
- **Фаза D ИСЧЕРПАНА: замаплены 7/7 поверхностей** (error R0.5, session-summary R0.4,
  focus-training R0.22, result R0.23, stats R0.24, settings R0.25, shell R0.26).
  Два черновика ready-to-paste (error C7-i, summary C6-i). Дальше — только Фаза E
  по разблокировке + микро-фикс NAMES.
