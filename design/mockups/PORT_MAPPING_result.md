# PORT MAPPING — result (D_PORT_MAPPING, R0.23)

> Маппинг макета `design/mockups/result.html` (язык **Instrument**) на прод-шаблон
> `modules/quiz-app/src/main/resources/templates/result.html`. Цель — сделать порт
> (Фаза E) известной величиной. **Правок прод НЕ вносит.**
>
> ⚠️ **Каверза сверки:** прод-шаблон в незакоммиченном WIP параллельной сессии —
> маппинг снят с рабочего дерева 2026-07-12 (в нём уже видны пост-AI-removal правки:
> удалена кнопка доп.анализа, появился фрагмент `inline-alert`). Пересверить перед портом.

## Контракт поверхности (нельзя ломать)

- **Роут:** `POST /answer` → view `result` — **ЖИВОЙ no-JS фоллбэк** (при включённом JS
  разбор рисует app.js прямо на focus-странице; сюда попадают no-JS пользователи и
  прямые POST). Model: `result{question{id,topic,important,regenCount,questionType,
  codeSnippet}, options[{id,optionText,explanation,correct}], correct, selected,
  correctAnswer}`, `answerHtml`, `reviewState` (SM-2), `stats`, `interviewSession`,
  `filter`, `weakTopics`, `relatedQuestions`, `aiEnabled` (зашит false).
- **Фрагменты:** `head(includeMermaid=true)`, `header(activePage='focus')`,
  `stats-grid`, `result-zone-head(id='result-zone-head-main', hidden=false)`,
  `inline-alert(id='interview-alert', hidden=true)` — **уже существует в проде**
  (344B, `role=alert aria-live=assertive`, наполняет app.js).
- **app.js v56:** favorite-toggle (`#btn-favorite` aria-pressed), подсветка кода;
  сам шаблон обязан работать целиком без JS.
- **ВАЖНО (память project_ui_verified_clean):** result.html — не мёртвый шаблон;
  no-JS деградация — контракт-фича.

## Таблица маппинга (mockup → прод-хук → действие/риск)

| Mockup (Instrument) | Прод-хук (сохранить) | Действие / риск |
|---|---|---|
| `body` | `body.result-page` | Сохранить. |
| `.rail` (прогресс/стрик в main-колонке) | `.top-row`: stats-grid фрагмент + `.session-bar` (mode/прогресс/✓/✗/`POST /finish`) | Сохранить top-row состав; instrument = mono-цифры, ring-чипы. Прогресс тут **getIndex() БЕЗ +1** (уже инкрементирован) — комментарий в шаблоне не терять. |
| `h1.result-question` | `h2` в `.question-header` (`th:utext` inline-md) | Уровень НЕ менять. Рядом `.question-meta`: `#btn-favorite` (aria-pressed, app.js) + `.topic-badge` + `regen-badge` — сохранить. `#btn-regenerate` гейтится `aiEnabled=false` → мёртв (кандидат Фазы G, R0.17). |
| `p.verdict.is-ok/.is-err` + `.v-sub` | `p.status.status-correct/.status-wrong` + иконки спрайта | Restyle прод-классов. `v-sub` («B → A», «+1 к стрику») — **НЕ портировать**: серверных данных для «B → A» нет (буквы в optionText), стрик-механика в другом месте. |
| `.confidence` (🎲🤔💪 radiogroup, roving tabindex) | **нет хука на no-JS странице** (уверенность живёт в AJAX-флоу focus: вердикт→уверенность→кнопка→sink, memory project_post_answer_js) | **НЕ портировать сюда**: на no-JS пути нет POST-канала для уверенности; добавление = новый endpoint (запрещено §4). Прод-эмодзи 🎲🤔💪 в AJAX-флоу оставлены намеренно (memory project_icon_system). Зона порта — стили `.confidence`-классов app.js в base.css (Фаза E focus-порта). |
| `.options` (readonly `.opt--correct/incorrect` + один `.explain`) | `.options[role=list]` из **div[role=listitem]** (НЕ label: display-only без input) с `option-correct/option-wrong/option-other` + `option-status-label` («Правильный ответ»/«Твой выбор») + **пер-опционные** `.option-explanation.markdown-content` | Прод богаче макета: объяснение у КАЖДОЙ опции + статус-лейблы + aria-label с префиксами. Сохранить структуру прода целиком, restyle токенами. **Буква здесь в тексте опции** (optionText «A. …» НЕ срезается на result — нет CSS-счётчика) — бейджи макета не вводить. |
| `.explain` (единый разбор) | `h3 «Пояснение» + .answer.markdown-content` (`answerHtml`) | Сохранить; restyle. |
| — (в макете нет) | `details.sm2-details > dl.sm2-grid` (6 позиций SM-2) | Прод-фича без макетного аналога — сохранить как есть, привести токены (моно-dd). |
| `details.question-code-details` | `.question-side > pre.question-code` th:if **`aiEnabled and CODE`** | **НАХОДКА (дефект-кандидат):** `aiEnabled` зашит false → код вопроса на no-JS result НИКОГДА не рендерится, хотя codeSnippet у вопроса есть и на focus-странице код показывается. Отображение кода не должно зависеть от AI-флага. НЕ чинить молча в порте — отдельный пункт Фазы G/решение (правка th:if = шаблонная логика). |
| aside `.result-actions` (следующий/похожие/завершить) | `.result-actions > a.btn.next-btn` «Следующий вопрос →» (querystring 7 параметров фильтра!) + finish в session-bar | Сохранить ссылку с полным набором параметров (потеря = сброс фильтра сессии). «Похожие» — не кнопка, а серверная секция ниже. |
| `.analysis` (takeaway / trace / related-chips) | takeaway/trace — **АI-вырезано** (нет данных, кнопка удалена в WIP); related = `section#result-related-questions` (прямой потомок `.ed-page`! WIDE LAYOUT `:has` уводит в правую sticky-рейку ≥1200px) | takeaway/trace НЕ портировать (мёртвые без AI). Related: сохранить **прямое потомство** `.ed-page` и `aria-labelledby=related-questions-heading` — обёртка в .card сломает грид (коммент C31). Chips-стиль макета → на `a.related-question-item`. |
| `.inline-alert--error/--info/--warn` + `.ia-dismiss` (макет focus) | фрагмент `inline-alert` (`#interview-alert`, `role=alert aria-live=assertive`, hidden) — наполняет app.js | **ОБНОВЛЕНИЕ R0.22:** прод-хук ПОЯВИЛСЯ (был «без продьюсера»). Порт: instrument-стили `.inline-alert` семейства в base.css; тональные модификаторы — только если app.js их ставит (сверить при порте). Dismiss-кнопки в проде нет — не добавлять без продьюсера. |
| `<html data-design>` | SSR `data-design="editorial"` | Не менять (instrument opt-in). |

## Риски/решения для Фазы E (сводка)

1. **no-JS цел, JS не обязателен** — никакой интерактив макета (confidence, JS-verdict
   свитчи) на эту страницу не переносится; всё серверное.
2. **Прод-структура опций богаче макета** (пер-опционные объяснения, статус-лейблы,
   role=list) — порт идёт ОТ прода, макет даёт только визуальный тон.
3. **Два мёртвых aiEnabled-гейта:** `#btn-regenerate` (известно, R0.17) и **`.question-side`
   с кодом вопроса (НОВАЯ находка R0.23)** — код на result скрыт навсегда; решение
   (убрать гейт или блок) — Фаза G / пользователь.
4. **`#result-related-questions` — прямой потомок `.ed-page`** (грид `:has`), в card не
   заворачивать.
5. **inline-alert прод-хук существует** — R0.22-заметка про «нет продьюсера» устарела
   для error-тона; dismiss по-прежнему без продьюсера.
6. **Порт требует result.html + base.css (+ app.js для confidence-стилей focus-флоу)** —
   всё в чужом WIP; пересверка хуков обязательна.

## Готовность к порту

- **BLOCKED**: result.html, base.css, app.js dirty.
- Очередь Фазы E: error → session-summary (черновики) → focus-training → **result**
  (этот маппинг) → stats → settings → shell/head.
