# План-патч: удаление мёртвого regenerate-UI + .question-side (R0.63, ready-to-apply)

> Статус: **DRAFT — ждёт окна без bootRun** (правка шаблона/JS/CSS + прогон тестов
> одним тиком; сейчас gradle запрещён — wedge при живом devtools-bootRun §4).
> Подготовлен read-only, чтобы window-тик был чистым применением.

## Почему мёртвое (evidence, снято 2026-07-12)

- AI вырезан целиком 2026-07-07 (b4ee4ffc+e323aac4); `aiEnabled` = false навсегда.
- `btn-regenerate` — `th:if="${aiEnabled}"` (result.html:48-50) → никогда не рендерится.
- `.question-side` — `th:if="${aiEnabled and … CODE …}"` (result.html:121) → никогда.
- `regen-badge` — `th:if="${regenCount() > 0}"`; в БД `max(regen_count)=0, count(>0)=0`
  (read-only SELECT после wipe 2026-07-08) → тоже мёртв, УДАЛЯТЬ вместе с кнопкой.
- Тесты НЕ пиннят: `grep regenerate|question-side` по TemplateFragmentContractTest,
  VisualBaselineContractTest, InterviewMvcControllerTest — 0 совпадений.
- `obtainAdminToken()` ОСТАЁТСЯ — им живёт экспорт (settings).

## Ханки удаления

### result.html
1. Строки ~48-52: `<button class="btn-regenerate" …>` + `<span class="regen-badge" …>`
   (комментарий над ними про AI-перегенерацию — тоже).
2. Строки ~121-123: весь `<div class="question-side" th:if="${aiEnabled …}">…</div>`.
3. Строка ~30: комментарий «Грид двухколонки включает CSS через :has(.question-side)» —
   переписать/удалить вместе с CSS-хунком 2292.

### app.js (v-бамп app.js в head.html при применении!)
1. `API.REGENERATE: '/api/regenerate',` (строка ~18).
2. Вся `regenerateQuestion()` (~884-950, JSDoc включительно).
3. Ветка делегата `const reg = e.target.closest('.btn-regenerate') … }` (~968-973)
   + слово «and regenerate» в комментарии ~958. Ветку favorite НЕ трогать.

### base.css (v-бамп base.css)
1. ~1047: `html[data-design] .result-page .btn-regenerate:active,` — строка в
   групповом селекторе (аккуратно: удалить строку, не сломать группу).
2. ~2292-2300: блок `:has(.question-side)` грид + `.question-side` flex + комментарий.
3. ~2398-2419: `.btn-regenerate` базовый + `:hover` + `.regenerating` busy + комментарии.
4. ~2689-2692: `@media` override `:has(.question-side) { 1fr }` + комментарий.
5. ~3256-3258: `@media` правило related-рейка × `.question-side` + комментарий.
6. ~3493: `.btn-regenerate` в групповом print/touch-селекторе (проверить группу).
7. `.regen-badge` правила — grep перед применением (`grep -n regen-badge base.css`).

### Бэкенд НЕ трогать (§4)
`/api/regenerate` endpoint остаётся (мёртвый код бэкенда — не наша фаза).

## Верификация window-тика (по порядку)

1. `git status --short` — collision guard.
2. Применить ханки → бампнуть `?v=` app.js и base.css в head.html.
3. `grep -rn 'regenerate\|question-side\|regen-badge' modules/quiz-app/src/main/resources/{templates,static}` → 0 (кроме бэкенда).
4. `./gradlew :quiz-app:test --tests "*TemplateFragmentContractTest" --tests "*InterviewMvcControllerTest"` — контракт + рендер result-вью (заодно закроет 7 PENDING-TEST¤ матрицы).
5. Если bootRun поднят заново — smoke GET / + console clean (app.js без синтакс-ошибок).
6. Коммит explicit pathspec: result.html, app.js, base.css, head.html + леджеры
   (матрица result-строка, CRITIQUE, PLAN, этот файл — удалить как применённый).
