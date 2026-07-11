# PORT MAPPING — session-summary (D_PORT_MAPPING, R0.4)

> Маппинг разметки макета `design/mockups/session-summary.html` (язык **Instrument**)
> на прод-шаблон `modules/quiz-app/src/main/resources/templates/session-summary.html`
> и контракт `.design/editorial-redesign/SERVER_CONTRACT.md`. Цель — сделать поздний
> порт (Фаза E) **известной величиной**: что сохранить дословно, что переименовать,
> где риск сломать контракт/a11y. **Правок прод НЕ вносит** — это план.

## Контракт поверхности (нельзя ломать)

- **Роут:** `GET /session-summary` → view `session-summary`; model-attr **`summary`**
  (если `summary` нет в сессии → `redirect:/`). `POST /finish` → `redirect:/session-summary`.
  Ссылка «Повторить ошибки» ведёт на `@{/review}`.
- **Модель `summary`:** `mode`, `accuracy`(int), `accuracyFormatted`, `totalQuestions`,
  `correctCount`, `wrongCount`, `formattedDuration`, `topicResults[]{topic(),total(),
  correct(),wrong(),accuracy()}`, `mistakes[]{topic(),questionText()}`, `recommendations[]`(String).
- **Бины-хелперы:** `@modeUtils.displayName`, `@topicUtils.displayName`,
  `@markdownRenderService.toInlineHtml`, `#strings`, `#numbers`.
- **head/header фрагменты:** `head(... includeChart=false, includeMermaid=false)`,
  `header(activePage='focus', showPrimaryNav=true, statsPage=false)`. **app.js на этой
  странице НЕ грузится** — весь интерактив (copy/print) самодостаточным inline-скриптом.

## Таблица маппинга (mockup-элемент → прод-хук → действие при порте)

| Mockup (Instrument) | Прод-хук (сохранить) | Действие / риск |
|---|---|---|
| `body` (без класса) | `body.summary-page` | **Сохранить** класс `summary-page` (print-CSS завязан). |
| `.skip-link → #main-content` | идентично | Сохранить дословно. |
| `.mock-bar` тон-свитчер | — (нет в проде) | **НЕ портировать** — это mockup-скаффолд. Тон (ok/warn/err) в проде даёт `th:classappend` по порогам 80/50. |
| `.site-head` inline | `~{fragments/header}` | Заменить на прод-фрагмент header (хром общий, не дублировать). |
| `main#main-content.summary[tabindex=-1]` | `main#main-content.ed-page[tabindex=-1]` | Сохранить `id`+`tabindex=-1`; класс `.summary`→оставить `.ed-page` (базовый page-каркас) + доп. классы Instrument. |
| `.summary-headline` (вердикт-фраза, `data-variant`) | **нет прод-аналога** (у прода крупный `.summary-score-value`) | **Новый элемент (Фаза E, визуальный язык)**: населять из `summary` — `th:text` фразы + `accuracyFormatted`. Тон-акцент `.accent-ok/.accent-err` ← тот же `th:classappend`. `data-variant`/JS-свитч из макета **выкинуть** — вариант определяет сервер. **Сохранить** `h2.summary-section-title.visually-hidden` «Результат сессии» для SR-heading-навигации. |
| `.stat-strip > .stat-tile` ×5 (точность/верно/ошибки/время/вопросов) | `.summary-score-value`+`.stats-grid>.stat-item`(×3)+`.summary-duration` | Маппинг значений: accuracy-tile ← `accuracyFormatted+'%'` + `th:classappend tone-ok/warn/err`; верно ← `correctCount`; ошибки ← `wrongCount`; время ← `formattedDuration`; вопросов ← `totalQuestions`. `data-when` свитч убрать. |
| `.summary-section > .topic-table` | `section.card > table.summary-table` | **КРИТИЧНО (a11y §8):** прод-таблица несёт `role=table tabindex=0` + `thead[role=rowgroup]` + `th[role=columnheader]` + `tr[role=row]` + `td[role=cell]` + первую ячейку `td[role=rowheader]` (CSS `display:block` для гориз-скролла сбрасывает нативную семантику в Safari/VO). **Порт ОБЯЗАН перенести все эти role+tabindex**, которых в макете НЕТ. `caption.visually-hidden` сохранить. |
| topic-row ячейка «Тема» | `td[role=rowheader] > a.summary-topic-link` `@{/(topic=...)}` | Ссылка на тренировку по теме; текст через `#strings.capitalize/replace` + `@topicUtils.displayName`, `th:title=topic`. Сохранить. |
| `.acc.tone-ok/warn/err` | `td.summary-td-accuracy` `th:classappend` text-success/warning/danger | Тон-класс = server-driven, не статический. |
| `.mistakes > a.mistake` (m-badge/m-text/m-topic/m-go) | `ul.summary-mistakes > li > a.summary-mistake-item` | Вопрос ← `.summary-mistake-question` **`th:utext=@markdownRenderService.toInlineHtml(m.questionText())`** (inline-markdown!) — НЕ `th:text`. Мета ← `.summary-mistake-meta` displayName. `href=@{/(topic=...)}`. `m-badge`/`m-go` — декоративные `aria-hidden` добавки, ок. `th:each m : summary.mistakes`, секция `th:if=!mistakes.isEmpty()`. |
| `.recs > ul > li` (иконка+`<b>`+`.rec-link`) | `ul.summary-recommendations-list > li.summary-recommendation-item` `th:text=rec` | **Ограничение:** прод-`recommendations[]` = плоские **строки** (`th:text`), без структуры bold/link. Bold/ссылку из строки НЕ собрать без backend-изменения (**ЗАПРЕЩЕНО §4**). Порт: `li ← th:each rec`, иконка декоративная, текст плоский. Секция `th:if=!recommendations.isEmpty()`. |
| `.summary-actions` (aside, статичные `.btn`) | `nav.summary-actions[aria-label="Что дальше"]` | Сохранить `nav`+`aria-label`. Ветвление: `.next-btn` review vs continue по `th:if=!mistakes.isEmpty()`; `.secondary-btn` → `/settings`(Новая сессия), `/stats`(Аналитика). Классы Instrument (`.btn-primary/.btn-ghost`) — визуальный слой поверх прод-классов `.btn.next-btn/.secondary-btn`. |
| `.share-row` (copy/print, статичные) | инжектируемый `.summary-tools` + `.summary-tools-status[role=status][aria-live=polite]` | **Сохранить PE-паттерн**: кнопки инжектит inline-скрипт ПОСЛЕ score-карточки (без JS — нет мёртвых кнопок). Иконки `#i-copy`/`#i-printer` из спрайта. Разместить по проду (`insertAdjacentElement('afterend')`), НЕ статикой в aside. Restyle под Instrument допустим; логику `buildShareText`/`window.print` сохранить. |
| `.theme-toggle` (макетный цикл auto/light/dark) | — | В проде тема/дизайн персистятся глобально (head.html pre-paint из localStorage). Макетный toggle — скаффолд, **НЕ портировать** как отдельную кнопку на странице. |
| `<html data-design>` | прод: `data-design="editorial"` хардкод | **Решение пользователя (pending):** Instrument = opt-in 11-й дизайн (localStorage), SSR-дефолт НЕ менять в этом порте. Порт кладёт структурные классы в `base.css` scoped-блок, а токены — в существующий `html[data-design="instrument"]`. |

## Риски/решения для Фазы E (сводка)

1. **A11y-роли таблицы** — макет их не содержит; порт обязан их вернуть (иначе регресс §8).
2. **Recommendations плоские** — визуальный «bold+link» макета недостижим без backend → порт оставляет плоский текст (иконка декор). Зафиксировано как ограничение, не дефект.
3. **PE-инъекция tools** — не превращать в статичные кнопки; сохранить no-JS-деградацию.
4. **Score → verdict-headline** — новый визуальный элемент; данные только из `summary`, никаких новых model-attr. `visually-hidden h2` сохранить для SR.
5. **data-design SSR-дефолт** — не трогать до явного решения пользователя (opt-in остаётся).
6. **Наблюдение R0.3** (sticky thead в overflow-x-обёртке) — в проде таблица `display:block`
   с гориз-скроллом; sticky-thead решать на паритет-фазе, в макет не тащить.

## Готовность к порту

- Порт session-summary **разблокируется** только когда прод-дерево чисто ИЛИ этот файл
  не в параллельном WIP (сейчас `session-summary.html` чист → порт можно ставить в очередь,
  но правку прод делать отдельным тиком в Фазе E по §5, explicit pathspec).
- Следующий логичный тик: аналогичный `D_PORT_MAPPING` для **error** (прод чист) —
  замкнуть mapping обеих независимых поверхностей до входа в Фазу E.
