# RES-15 — list-семантика блока «Похожие вопросы» (prep → apply)

**Источник:** static a11y-workflow 2026-07-13 (R0.101), WCAG 1.3.1 Info & Relationships, единственный CONFIRMED (адверсари-верифицирован), severity **P4 minor**.

**Суть:** набор из N ссылок «Похожие вопросы для закрепления» рендерится как визуальный список (стопка одинаковых `.related-question-item`), но без программной list-семантики (нет `ul/li`, нет `role=list/listitem`). Скринридер не объявит «список из N», не даст навигацию по элементам. Отступление от собственного стандарта файла: соседний блок вариантов (`.options`, result.html:63) намеренно `role="list"` + `role="listitem"`.

**Скоуп — ДВА места (одинаковая разметка h3 + N `<a>`-сиблингов):**
1. `result.html:132-138` — SSR-рендер (страница /answer, no-JS фоллбэк).
2. `app.js:1624-1648` `renderRelatedQuestions` — динамический рендер на focus-странице после ответа.

## Анализ layout-риска — НУЛЕВОЙ (проверено по base.css 2562-2588)

- `.related-question-item` = `display: block` (стопка нормальным потоком).
- Родитель `.related-questions` / `#result-related-questions` — **без** flex/grid (только `border-top` + `padding-top` + `margin-top`).
- Обёртка `<div role="list">` = дефолтный блок 100%-ширины без своих margin/padding → элементы-блоки внутри текут **идентично**. Визуально 0 изменений.
- Wide-layout (`.ed-page > #result-related-questions` sticky-рейка, `:has(> #result-related-questions)`, `overflow-y:auto`, `max-height`) таргетит **секцию по id** — обёртка внутри её не затрагивает. h3 (`.related-questions-title`) остаётся первым потомком секции (label через `aria-labelledby`), вне списка — корректно (list содержит только listitem).
- **CSS-правок НЕ требуется** (обёртка стилей не несёт) → **v-бамп НЕ нужен для CSS**. app.js меняется → v-бамп app.js (**v=62→63**; факт. свер. R0.114, все 3 шаблона на v=62) + live-sync + это APP-7 контракт.

## Точные ханки

### 1) result.html (было 132-138)
```html
<section th:if="${relatedQuestions != null and !relatedQuestions.isEmpty()}" id="result-related-questions" class="related-questions related-questions-block" aria-labelledby="related-questions-heading">
    <h3 id="related-questions-heading" class="related-questions-title"><svg class="ed-icon ed-icon-lead" aria-hidden="true"><use href="#i-link"></use></svg>Похожие вопросы для закрепления:</h3>
    <div class="related-questions-list" role="list">
        <a th:each="rq : ${relatedQuestions}" class="related-question-item" role="listitem"
           th:href="@{/(topic=${rq.topic()},group=${filter?.group},ordered=${filter == null or filter.ordered != false},important=${filter?.importantOnly},onlyWrong=${filter?.onlyWrong},shuffle=${filter?.shuffle},weakTopics=${weakTopics})}"
           th:utext="${@markdownRenderService.toInlineHtml(rq.questionText())}">
        </a>
    </div>
</section>
```
(добавлены: обёртка `<div class="related-questions-list" role="list">` вокруг цикла + `role="listitem"` на `<a>`.)

### 2) app.js renderRelatedQuestions (было 1635-1646)
```js
    let relatedHtml = '<h3 class="related-questions-title">' + icon('link', 'ed-icon-lead') + 'Похожие вопросы для закрепления:</h3>';
    relatedHtml += '<div class="related-questions-list" role="list">';
    data.relatedQuestions.forEach(rq => {
      relatedHtml += '<a class="related-question-item" role="listitem" href="/?topic=' + encodeURIComponent(rq.topic)
        + '&group=' + encodeURIComponent(currentGroup)
        + '&ordered=' + encodeURIComponent(currentOrdered)
        + (currentImportant ? '&important=true' : '')
        + (currentOnlyWrong ? '&onlyWrong=true' : '')
        + (currentShuffle ? '&shuffle=true' : '')
        + (currentWeakTopics ? '&weakTopics=true' : '')
        + '">' + escapeHtml(rq.text) + '</a>';
    });
    relatedHtml += '</div>';
    relatedDiv.innerHTML = relatedHtml;
```
+ v-бамп app.js в 3 шаблонах (result:142/settings:291/focus-training:222) **v=62→63** (свер. R0.114) + live-sync build/resources.

## План применения (окно, когда app поднят)
1. Collision-guard: `git status` result.html / app.js / шаблоны с `?v=` чисты.
2. Применить оба ханка; бампнуть app.js **v=62→63** (3 шаблона — сверить фактическую версию перед бампом, менялась 56→62 без апдейта плана); `cp` source→build/resources/main.
3. `node --check app.js`.
4. QA (**session-gated** — result рендерится только на POST /answer): в EXAM/window-сессии ответить на вопрос → проверить в DOM: секция → h3 (вне списка) + `div[role=list]` → N × `a[role=listitem]`; a11y-снапшот объявляет «список, N элементов»; визуально стопка идентична (0 сдвига); обе темы. Focus-динамика: тот же чек после ответа.
5. Контракт-тест (если окно без bootRun): app.js-версии в шаблонах согласованы.
6. Леджеры: §10 RES-15 → DONE; §9 result.html/app.js A11y-ячейки; CRITIQUE.

**QA-гейт:** result.html и focus-динамика недостижимы read-only (нужен POST /answer = мутация) → RES-15 apply+QA идёт вместе с session-gated parity (EXAM-решение юзера), одним окном.
