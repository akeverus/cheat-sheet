# Window-tick манифест — единое окно app-up (R0.118, консолидация R0.112–117)

> **Назначение:** когда postgres+app подняты и есть окно без живого devtools-bootRun
> (gradle разрешён), исполнить ВСЮ gated-работу как один упорядоченный чеклист — не
> переоткрывая план. Все части предварительно сверены turnkey (R0.114/115/116). Этот
> файл — оркестратор; детальные ханки в под-планах (ссылки ниже). RU, explicit pathspec, без push.

## Почему одним окном (координация)

Три cleanup'а + один semantic-fix трогают **пересекающиеся файлы** — бампать версии и
гонять контракт-тесты нужно СОВМЕСТНО, иначе двойной бамп / рассинхрон:

| Файл | regenerate-removal | STA-20 (stats-колонка) | AIR-6 (рудимент) | RES-15 (list-семантика) | SET-21 (dead .app-layout) | Итоговый v-бамп |
|---|---|---|---|---|---|---|
| `app.js` | удаление regenerate | — | — | wrapper в `renderRelatedQuestions` | — | **v=62→63 ОДИН раз** (3 шаблона) |
| `base.css` | .result-page .btn-regenerate/question-side | строка-селектор 1892 | — | — (обёртка стилей не несёт) | правила 3200-3206 + коммент 3197-3199 | **v=92→93 ОДИН раз** (head.html:311) |
| `result.html` | btn 48-50 + regen-badge 51-53 + question-side 121 | — | — | related-блок 132-138 | — (версии в script-тегах) |
| `stats.html` | — | колонка «Сброшено» 224-227 (+ th-заголовок) | — | — | — |
| `focus-training.html` | — | — | флаг generationUnavailable + guards | — | (только app.js-версия :222) |
| `FocusTrainingPageService` + `FocusPageState` + `MvcModelAttributeMapper` | — | — | убрать проброс generationUnavailable | — | — (контракт-тесты!) |
| `TopicStatsResponse` + `StatsApiMapper` | — | опц. поле regenSum | — | — | — |

**Критично:** app.js правится ДВУМЯ единицами (regenerate-removal + RES-15) → применить
обе, потом **один** бамп v=62→63. Аналогично base.css (regenerate-removal + STA-20) → **один** v=92→93.

## Порядок исполнения

### 0. Pre-flight
1. `git status --short` — collision-guard: `result.html` / `app.js` / `base.css` / `stats.html` /
   `focus-training.html` / затрагиваемые `.java` чисты (не в чужом WIP). Если dirty — стоп, ждать.
2. app отвечает на :8080; для gradle-прогона — devtools-bootRun остановлен (wedge-риск §4).
3. Свериться с ФАКТИЧЕСКИМИ версиями перед бампом (дрейфуют): `grep 'app.js(v=' templates/*`,
   `grep 'base.css(v=' fragments/head.html`. Ожидаемо app.js=62, base.css=92 (на R0.115).

### 1. Применить статические ханки (порядок безразличен, всё до бампа)
- **regenerate-removal** — по `regenerate-removal-plan.md` (result.html + app.js ~898-965/~977-981 +
  base.css 6 мест, актуализ. R0.115). Бэкенд `/api/regenerate` НЕ трогать (§4).
- **STA-20** — удалить колонку «Сброшено»: `stats.html:224-227` td + соответствующий `<th>` в thead;
  base.css: убрать строку-селектор 1892 (НЕ блок — общий с `.maturity-badge`); опц. поле
  `TopicStatsResponse.regenSum` + `StatsApiMapper:59`. **Перед удалением сверить share/print-контракт**
  (SUM-7-класс, кол-во `<td>` на строку — share-JS может считать ячейки).
- **AIR-6 рудимент** — убрать `generationUnavailable` из `FocusTrainingPageService:54/94/147` +
  `FocusPageState` + `MvcModelAttributeMapper:47`; в `focus-training.html:190-195` снять всегда-истинные
  `!generationUnavailable and` из 4 guard'ов (ветки остаются гейтнуты reviewMode/сессией).
- **RES-15** — по `res15-related-list-semantics-plan.md` (result.html:132-138 обёртка `<div role=list>` +
  `role=listitem`; app.js `renderRelatedQuestions` тот же wrapper). Layout-риск NULL (base.css:2562-2588).
- **SET-21** — удалить мёртвые правила `.settings-page .app-layout:has(...)` (base.css:3200-3206) +
  комментарий 3197-3199. 0 render-эффекта (правила не матчатся — `.app-layout` только под `.stats-page`,
  settings — вкладки). Сверить перед удалением: `grep app-layout templates static/js` = ровно 1 (stats.html:8).
  Едет на том же base.css v=92→93 бампе + контракт-тест.

### 2. Единые v-бампы
- `app.js`: **v=62→63** в `result.html` / `settings.html` / `focus-training.html` (3 script-тега).
- `base.css`: **v=92→93** в `fragments/head.html:311`.
- `cp` source→`build/resources/main` (live-sync, если bootRun будет перезапущен).

### 3. Верификация (один прогон)
1. `grep -rn 'regenerate\|question-side\|regen-badge\|generationUnavailable' modules/quiz-app/src/main/resources/{templates,static}` → 0 (кроме комментариев/бэкенда).
2. `node --check .../app.js`.
3. `./gradlew :quiz-app:test --tests "*TemplateFragmentContractTest" --tests "*InterviewMvcControllerTest"`
   → зелёно. **Закрывает 7 PENDING-TEST (⏸) §9** (Contract/Tests-ячейки).
4. Если bootRun поднят — smoke GET / + console clean.

### 4. Parity-QA (session-gated — отдельная под-фаза, но то же окно)
- RES-15 + result-parity: EXAM/сессия → POST /answer (мутация — с ведома юзера) → в DOM:
  `#result-related-questions` → h3 (вне списка) + `div[role=list]` → N × `a[role=listitem]`;
  a11y-снапшот «список, N элементов»; визуально стопка идентична (0 сдвига); обе темы.
  Focus-динамика: тот же чек после ответа на focus-странице.
- STA-20: /stats → таблица без колонки «Сброшено»; share/print не сломаны; 375/768/1280.
- Port-parity прочих поверхностей (§7 Parity ◐→): скрин+DOM/computed прод vs mockup.

### 5. Леджеры (тем же/следующим коммитом)
- §10: RES-15→DONE, STA-20→DONE, AIR-6→DONE(plumbing), APP-4/AIR-4→DONE.
- §9: result.html/app.js/stats.html/base.css/focus-training.html — Contract/Tests ⏸→✅ (7 шт).
- §7: соответствующие Parity ◐→ (по факту QA).
- Удалить применённые под-планы (`regenerate-removal-plan.md`, `res15-…-plan.md`) как исполненные.
- §21: R0.NN итог окна.

## Ссылки на детальные под-планы
- `regenerate-removal-plan.md` (R0.63, актуализ. R0.115) — ханки regenerate.
- `res15-related-list-semantics-plan.md` (R0.102, актуализ. R0.114) — ханки list-семантики.
- §10 `STA-20` (R0.116) — dead stats-колонка.
- §10 `AIR-6` (R0.112) — рудимент generationUnavailable.
- §21 `R0.117` — сертификация полноты AI-fallout (весь dead-AI-surface = эти 3 cleanup'а, без хвостов).

## 🔓 РАЗБЛОКИРОВКА (R0.125, 2026-07-14) — гейт статических единиц app-НЕЗАВИСИМ

**Открытие:** `TemplateFragmentContractTest` — чистый JUnit (`ClassPathResource`+string-ассерты,
БЕЗ `@SpringBootTest`/Testcontainers/БД). Прогон = BUILD SUCCESSFUL за 2s без Docker/postgres.
→ **Пред-условие «postgres+app» было неверным для статических хунков.** Реальный гейт cleanup'ов
(base.css/шаблоны) = только gradle с мёртвым bootRun (сейчас выполнено — app down). Значит единицы
исполнимы **инкрементально по одному тику**, не дожидаясь подъёма app:
- **SET-21 — ✅ ИСПОЛНЕНО R0.125** (base.css v=92→93, 2 правила+коммент удалены, контракт 10/10).
- **regenerate-removal** — исполнимо (0 render-эффекта: элементы не рендерятся при aiEnabled=false; гейт = контракт-тест + `node --check`).
- **RES-15** — исполнимо (семантика списка; контракт-тест + node).
- **STA-20** — почти исполнимо (визуальная правка таблицы, но td/share-контракт проверяем статически; DTO-поле опц.).
- **AIR-6** — отдельно: `.java` plumbing → нужен render-тест; сперва проверить, нужна ли ему БД.

«Единое окно» больше НЕ требуется для статики — v-бампы делаются по мере применения (несколько
бампов безвредны, это лишь кэш-инвалидация). Parity-QA (визуальные скрины) по-прежнему нужен app —
но чисто-статические 0-render единицы (SET-21, regenerate-removal) его не требуют.

**Остаточное пред-условие (только для parity-QA и AIR-6-render):** postgres+app. Статические
cleanup'ы — исполнять инкрементально сейчас.
