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
1. Строки **~48-53** (свер. R0.115): `<button class="btn-regenerate" …>` (48-50) +
   `<span class="regen-badge" th:if="${result.question.regenCount() > 0}">` (51-53)
   (комментарий над ними про AI-перегенерацию — тоже).
2. Строки ~121-123: весь `<div class="question-side" th:if="${aiEnabled …}">…</div>`.
3. Строка ~30: комментарий «Грид двухколонки включает CSS через :has(.question-side)» —
   переписать/удалить вместе с CSS-хунком 2292.

### app.js (v-бамп app.js в 3 ШАБЛОНАХ, НЕ в head.html! — свер. R0.115)
> ⚠️ Версия app.js живёт в `result.html:142` / `settings.html:291` / `focus-training.html:222`
> (`@{/js/app.js(v=62)}`), НЕ в head.html. При применении бамп **v=62→63** во всех трёх.
1. `API.REGENERATE: '/api/regenerate',` (строка ~18 — свер. R0.115: факт. 18).
2. Вся `regenerateQuestion()` (**факт. ~898-965**, JSDoc включительно — R0.115; было ~884-950).
3. Ветка делегата `const reg = e.target.closest('.btn-regenerate') … }` (**факт. ~977-981** — R0.115; было ~968-973)
   + слово «and regenerate» в комментарии рядом. Ветку favorite НЕ трогать.

### base.css (v-бамп base.css в head.html:311, факт. v=92→93 — свер. R0.115)
> Все номера актуализированы R0.115 (был дрейф +20…−121 строк). Перед удалением
> всё равно grep — база активно правится параллельно.
1. **~1067**: `html[data-design] .result-page .btn-regenerate:active,` — строка в
   групповом селекторе (аккуратно: удалить строку, не сломать группу). (было ~1047)
2. **~2329-2337**: комментарий (2329-2330) + грид `:has(.question-side)` (2333) +
   `.question-side` flex (2337). (было ~2292-2300)
3. **~2435-2457**: `.btn-regenerate` базовый (2435) + `:hover` (2451) + `.regenerating` busy (2456)
   + `.result-page .regen-badge` (2457) + комментарии. (было ~2398-2419)
4. **~2603**: `@media` override `:has(.question-side) { 1fr }` (2603) + комментарий (2600). (было ~2689-2692)
5. **~3171-3173**: `@media` правило related-рейка × `.question-side` + комментарий. (было ~3256-3258)
6. **~3372**: `.btn-regenerate` в групповом print/touch-селекторе (проверить группу). (было ~3493)
7. `.regen-badge` — **ДВА места:** `.result-page` (2457, выше) И **`.stats-page .regen-badge` (1892)** —
   grep перед применением (`grep -n regen-badge base.css`); решить, удалять ли stats-вариант
   (там regen-badge мог использоваться иначе — проверить stats-разметку).

### Бэкенд НЕ трогать (§4)
`/api/regenerate` endpoint остаётся (мёртвый код бэкенда — не наша фаза).

## Верификация window-тика (по порядку)

1. `git status --short` — collision guard.
2. Применить ханки → бампнуть `?v=`: **app.js v=62→63 в 3 шаблонах** (result:142/settings:291/focus-training:222), **base.css v=92→93 в head.html:311** (свер. R0.115 — app.js-версия НЕ в head.html). Сверить фактические версии перед бампом (дрейфуют).
3. `grep -rn 'regenerate\|question-side\|regen-badge' modules/quiz-app/src/main/resources/{templates,static}` → 0 (кроме бэкенда).
4. `./gradlew :quiz-app:test --tests "*TemplateFragmentContractTest" --tests "*InterviewMvcControllerTest"` — контракт + рендер result-вью (заодно закроет 7 PENDING-TEST¤ матрицы).
5. Если bootRun поднят заново — smoke GET / + console clean (app.js без синтакс-ошибок).
6. Коммит explicit pathspec: result.html, app.js, base.css, head.html + леджеры
   (матрица result-строка, CRITIQUE, PLAN, этот файл — удалить как применённый).
