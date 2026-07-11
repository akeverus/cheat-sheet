# CRITIQUE — адверсарный разбор макетов «Instrument» (Фаза 1)

> Свежий критический взгляд на 6 готовых макетов ПЕРЕД переносом во фронт (Фаза 2).
> Правим ТОЛЬКО макеты в `design/mockups/`. Каждая правка — с причиной (см. iterate-протокол §10).
> Статусы: TODO / IN_PROGRESS / DONE / RECHECK / REJECTED.

## Метод

Линзы: DESIGN.md (absolute-bans, стратегия цвета/типографики, мандат ширины) ·
Taste-дайлы (VARIANCE 6 / MOTION 4 / DENSITY 4) · impeccable detect · icon-system
(монохромный Lucide, НЕ эмодзи) · консистентность компонентов через все 6 экранов ·
a11y/клавиатура/reduced-motion. Слоп ищем не «на глаз красиво», а по нарушению
установленного языка и по кросс-экранной рассинхронизации.

## Бэклог (worst-first)

| id | приоритет | проблема | причина/линза | файлы | статус |
|---|---|---|---|---|---|
| C1 | P2 | `🔥` эмодзи в стрике | icon-system: язык монохромный Lucide, эмодзи выбивается; единственный сырой эмодзи в наборе | `shell.html` | **DONE** |
| C2 | P2 | Единственная шапка с `backdrop-filter: blur` + `color-mix`-полупрозрачностью | DESIGN.md банит дефолтный glassmorphism; плюс расходится с 5 сплошными шапками | `shell.html` | **DONE** |
| C3 | P2 | Sticky-шапка непоследовательна: `shell` sticky, остальные 5 — статичные | Хром обязан быть ОДНИМ компонентом через все экраны; sticky-нав = хорошее UX, применить единообразно (sticky + solid, без блюра) | все 6 | **DONE** |
| C4 | P3 | kbd-help модалка живёт только в `shell.html` | По контракту (§8) это глобальный оверлей экрана вопроса; при Фазе 2 привязать к `focus-question` | `shell.html`→`focus-question.html` | **DONE** |
| C5 | P4 | Входной ритм: kicker + headline на всех экранах | Оценено: ритм САМ ПО СЕБЕ ок (системный, не монотонный, VARIANCE-адекватно). Реальный слоп — 2 кикера-эхо (`stats`/`settings` = lowercase-копия H1) против DESIGN.md «кикер = функциональный тег». Сделаны функциональными | `stats.html`, `settings.html` | **DONE** |
| C6 | P4 | Подвал только на `shell` (домашний), у 5 задача-экранов его нет | **РЕШЕНО (осознанно):** подвал — только на hub-экране (home). Задача-экраны (вопрос/разбор/итоги/настройки/аналитика) намеренно БЕЗ подвала — фокус, ethos «инструмент не шумит»; кросс-нав уже в шапке на каждом экране, так что навигация не теряется. Не «чинить» | — | **РЕШЕНО** |
| C7 | P1 | skip-link `signal-on` на `--signal` = AA 4.40 < 4.5 (light) на 5 из 6 | Тот же слоп-класс, что и раньше в btn-primary (общий компонент), но в focus-only skip-link — пропустили; skip-link = видимый по фокусу текст (WCAG 2.4.1), обязан держать AA | 5 экранов (кроме shell) | **DONE** |
| C8 | — | focus-ring/reduced-motion/AA-каркас | Верифицированы по PROGRESS it.10–21; при каждой правке — re-check, не регресс | все | RECHECK |
| C10 | P4 | Type-scale: `.state-title` (empty/done, font-hero fs-xl) держит `--track-tight`, а 3 других serif-page-title того же размера (`settings/stats/summary`) — `--track-hero` | Кросс-экранная консистентность типографики: одна роль (serif page-title @fs-xl) = одна трактовка tracking. Сам scale КОГЕРЕНТЕН (2 tier: content-hero clamp vs page-title fixed) — дрейф только в одном letter-spacing | `focus-question.html` | **DONE** |
| C9 | P2 | Кнопочный компонент дрейфует на `shell`: 3 расхождения с остальными 5 экранами | Кросс-экранная консистентность: одна кнопка = один компонент. (1) `.btn` padding `s-5` vs `s-6` у 4 других; (2) `.btn-primary` без `box-shadow: shadow-sm` (у всех 4 — есть → плоский CTA на home); (3) `.btn-quiet` = байт-идентичен `.btn-ghost` у focus/result/summary, но result/summary держат `.btn-quiet` для ДРУГОЙ безрамочной третичной → коллизия имени (одно имя → два вида) | `shell.html` | **DONE** |
| C11 | — | `opt-mark` (галочка верного варианта): `success-on` на заливке `success` = AA 4.28 < 4.5 (light) | **РЕШЕНО (compliant):** галочка — **графический объект** (WCAG 1.4.11, порог 3:1), `aria-hidden`, избыточна с зелёной обводкой `.opt--correct` + тегом «верный ответ» + тинтом бейджа. 4.28 ≥ 3:1 → проходит. Единственная пара ниже строгого 4.5-бара (сестра `error-on/error` = 4.95). Общий токен `--success` НЕ риплю в recheck-тик (задел на 8 макетов). Если понадобится симметрия ≥4.5 — точечно затемнить `--success` fill в отдельном тике | `focus-question.html` (tokens) | **РЕШЕНО** |
| C12 | P3 | `focus-question` покрывает 4 состояния (active/result/empty/done), но registry-список focus-training шире | Reference-экран задал ЯЗЫК на 4 ядровых состояниях; недостающие: flashcard-reveal/grade(1-4), study-LEARN(study-confirm), generationUnavailable, loading-placeholder, inline-alert/error, no-js-fallback, diagram/mermaid. Coverage-gap — доработать состояния reference-экрана (порт focus-training всё равно BLOCKED параллельным WIP) | `focus-question.html` | TODO |
| C13 | P3 | `result` покрывает correct/incorrect/analysis/favorite, но registry-список result шире | Недостающие состояния: confidence-виджет (🎲🤔💪, оставлен намеренно в проде), regenerate, no-js-fallback (живой `/answer`). Coverage-gap reference-разбора (порт result BLOCKED параллельным WIP). Отдельно — при ПОРТЕ провести `aria-live` на вердикт (§8 `#result-feedback`): в макете вердикт статичный `<p>`, live-region проводится в прод, не дефект макета | `result.html` | TODO |
| C14 | P3 | `settings` покрывает 3 вкладки/export/7 осей/filters/session/streak, но не reset-options-confirm | Danger-кнопка «Сбросить банк вариантов» есть, шага ПОДТВЕРЖДЕНИЯ (необратимое действие) в макете нет. Coverage-gap: показать/застабить confirm-модалку сброса. Порт settings BLOCKED параллельным WIP | `settings.html` | TODO |
| C15 | P2 | `stats`: амбер-заливки данных (`bar-due`, `bar-acc-mid`, `cp-due`, `fc-fill.is-today`) на треке `surface-2` = **2.06:1 < 3:1** (light) | **Реальный дефект** (не «граф. объект compliant», как C11): столбцы данных ДОЛЖНЫ отличаться от трека ≥3:1 (WCAG 1.4.11) — иначе амбер-бары «к повтору»/mid-точность не видны в светлой теме. Яркий `--spark` (L 0.720) слишком светлый на светлом треке. **ФИКС:** заливки данных → `--spark-ink` (L 0.500) = 5.02 light / 8.72 dark ≥3:1; легенда `.sw-due` синхронизирована. Правка ЛОКАЛЬНА в `stats.html` (`--spark` — декор-акцент, не тронут). learned↔due различаются ПО ТОНУ (teal↔амбер, CVD-safe blue-yellow ось) + числовой лейбл | `stats.html` | **DONE** |
| C16 | P3 | `stats` покрывает overview/charts+fallback/sortable-table/forecast/gaps, но registry-список шире | Недостающие состояния: empty/cold-start (в проде — осознанный вид, memory `project_design_elevation_round1`), search (поиск по темам). Coverage-gap. Порт stats BLOCKED параллельным WIP | `stats.html` | TODO |

## Журнал критики

- **cr.11 (ROUND-RESET, R0.9)** — **B_MOCKUP_RECHECK экрана `stats` → найден и ПОФИКШЕН
  реальный дефект контраста столбцов (C15).** detector exit 0. **AA обе темы:** все
  ТЕКСТОВЫЕ пары ≥4.5 (acc-тона на surface/paper 5.5–5.95; ov/action signal-ink 6.4–6.5;
  gap-chip 5.41; chart-лейблы 7.18/14.4). **Графобъекты (столбцы, 3:1):** нашёл провал —
  амбер `--spark` на треке `surface-2` = **2.06:1** (`bar-due`, `bar-acc-mid`, `cp-due`,
  `fc-fill.is-today`): данные-бары почти не видны в светлой теме. **ФИКС (локально в
  stats.html):** заливки данных → `--spark-ink` (5.02 light / 8.72 dark), легенда `.sw-due`
  синхронизирована; яркий `--spark` (декор) не тронут. learned↔due — по тону (teal↔амбер,
  CVD-safe) + числовой лейбл, не по яркости (inherent: любой амбер, читаемый на светлом
  треке, близок к teal по L). После фикса: signal/surface-2 3.65, spark-ink/surface-2 5.02,
  success 3.55, error 4.16 — все столбцы ≥3:1. **a11y-каркас образцовый:** SVG-графики
  `role=img`+aria-label (+«данные в таблице ниже»)+fallback `role=status aria-live`; таблица
  `caption`+`th scope=col/row`+sortable `aria-sort`+клавиши Enter/Space+статус-анонс;
  скролл-регион `role=region tabindex=0`; паритет §5 (stats.js хуки)/§6. **states:** покрыты
  overview/charts+fallback/sortable-table/forecast/gaps; НЕ покрыты empty/cold-start (осознан
  в проде) + search → **C16**. **Веха:** recheck всех 7 поверхностей замкнут. Дальше — Фаза E
  по разблокировке `base.css`, либо доработка coverage-gaps (C12/C13/C14/C16).
- **cr.10 (ROUND-RESET, R0.8)** — **B_MOCKUP_RECHECK экрана `settings` (вкладки Сессия/
  Оформление/Данные).** detector exit 0. **AA обе темы:** все пары ≥4.5 (light worst 4.95
  btn-danger `error-on/error`, dark 5.24); danger-карта `ink-2/error-wash` 6.71, `error-ink/
  error-wash` 5.52; seg-btn 6.56–6.93; вкладка выбранная `signal-ink/signal-wash` 6.54; поля
  `ink/paper` 15.55. **a11y ОБРАЗЦОВЫЙ WAI-ARIA:** `role=tablist/tab/tabpanel` + roving
  tabindex + стрелки ↑↓←→/Home/End; seg-control `role=radiogroup`+`aria-checked`+стрелки;
  font-stepper `role=group` + `step-value role=status aria-live=polite`; toggle — скрытый
  checkbox + `.switch aria-hidden` + focus-ring на `input:focus-visible+.switch`; `label
  for/id` на всех select/input. **Паритет §3** (#filters-form/#session-form)/**§8**.
  **full-width** структурно (settings-grid rail+panels, брейк 1080px; set-grid auto-fit
  наполняет ширину; вкладки → гориз-скролл на узких). **states:** покрыты 3 вкладки + export
  json/csv + 7 осей персонализации + filters + session + streak; НЕ покрыт reset-options-
  confirm → **C14** (шаг подтверждения danger-сброса). Дальше worst-first — recheck `stats`
  (последний чистый макет) ЛИБО Фаза E по разблокировке `base.css`.
- **cr.9 (ROUND-RESET, R0.7)** — **B_MOCKUP_RECHECK экрана `result` (разбор ответа).**
  detector exit 0. **AA обе темы:** все текст-пары ≥4.5 (light worst 5.52, dark 5.24);
  ключевое — проза на семантических wash-заливках держит с запасом (ink на signal/success/
  error-wash = 13–14 light, 11–12 dark); verdict `success-ink/success-wash` 5.58, `error-ink/
  error-wash` 5.52; chip-hover/takeaway-icon `signal-ink/signal-wash` 6.54. `opt-mark` галочка
  верного = 4.28 = граф. объект (C11, compliant). **a11y-каркас полный:** progressbar
  aria-valuenow, `role=group` разбора вариантов, `.analysis` aria-label «Разбор глубже»,
  focus-visible ring, reduced-motion fail-safe, иконки aria-hidden, mock-switch aria-pressed.
  **full-width** структурно (result-grid main+aside, брейк 1080px; рельс наполняет правое
  поле — «разбор глубже» takeaway/trace/related). **states:** покрыты correct/incorrect
  (mock-switch) + post-answer-analysis + favorite; НЕ покрыты confidence/regenerate/no-js
  → **C13** (coverage-gap). **Наблюдение на порт:** вердикт — статичный `<p>`, не live-region;
  `aria-live` (§8 `#result-feedback`) проводится при порте, НЕ дефект макета. Дальше worst-first
  — recheck `settings`/`stats` ЛИБО Фаза E по разблокировке `base.css`.
- **cr.8 (ROUND-RESET, R0.6)** — **B_MOCKUP_RECHECK reference-экрана `focus-question`.**
  detector exit 0. **AA обе темы:** все ТЕКСТОВЫЕ пары ≥4.5 (light worst 4.95 badge
  error-on/error, dark 5.24); единственная пара ниже 4.5 — `opt-mark` галочка верного
  `success-on/success` = **4.28 light** → но это **графический объект** (порог 3:1),
  `aria-hidden`, избыточна с обводкой/тегом/бейджем → compliant (C11, РЕШЕНО, без риппла
  общего токена). **a11y-каркас образцовый, паритет §8:** `role=radiogroup`+aria-label,
  `progressbar` aria-valuemin/max/now, kbd-help `role=dialog aria-modal aria-labelledby/
  describedby` + focus-trap + `?`-toggle + Esc + возврат фокуса + `aria-haspopup=dialog`,
  focus-visible ring на `:has(input:focus-visible)`, reduced-motion fail-safe, kbd-hint
  гейт `hover:hover+pointer:fine+≥641` (нет touch-мёртвых подсказок), моб. sticky submit-bar
  c `env(safe-area-inset-bottom)`. **full-width** структурно (`.wrap width:100%` + focus-grid
  main+aside, брейк 1080px). **states:** покрыты 4 ядровых (active/result/empty/done); НЕ
  покрыты 7 из registry-списка focus-training (flashcard/study-LEARN/generationUnavailable/
  loading/inline-alert/no-js/diagram) → **C12** (coverage-gap, TODO; порт всё равно BLOCKED).
  **Вывод:** reference-язык verified-clean на ядровых состояниях. Дальше worst-first —
  recheck остальных чистых макетов (result/settings/stats) ЛИБО вход в Фазу E по разблокировке.
- **cr.7 (Фаза 1, тик 7)** — **кросс-экранный аудит type-scale → система когерентна,
  1 микро-дрейф закрыт (C10).** Разобрал page-title/hero-трактовку всех 6 макетов.
  **Вывод: шкала НЕ дрейфует — это осознанный 2-tier дизайн:** (a) content-hero
  (`shell.home-headline` clamp 2.1→3.15, `focus.focus-question` fs-hero clamp 1.75→3.05) —
  responsive, крупный, потому что ЭТО фокус экрана; (b) page-title (`settings/stats-title`,
  `summary-headline`, `state-title`) — фикс `--fs-xl` 2rem, спокойный. Различия внутри
  tier-а оправданы/задокументированы (floor focus-hero занижен it.3 под перенос вопроса).
  **Реальный дефект — только 1:** среди serif-page-title @fs-xl три (`settings/stats/summary`)
  держат `letter-spacing: --track-hero`, а `.state-title` (empty/done, тот же serif@2rem) —
  `--track-tight` → рассинхрон. Одна роль = один tracking. **Фикс:** state-title
  `--track-tight`→`--track-hero` (1 файл). **QA:** detector exit 0; все 4 serif-page-title
  теперь `--track-hero`; правка letter-spacing → AA/лейаут не затронуты (0.005em при 32px,
  консистентность важнее заметности). **Type-scale помечен verified-clean** (2-tier
  осознан) → не переаудировать. Следующий тик Фазы 1 — иная дименсия (focus-ring/spacing-
  ритм консистентность) или обратно в Фазу 2 (по готовности дерева/bootRun).
- **cr.6 (Фаза 1, тик 6)** — **углублённый кросс-экранный аудит компонентов → нашёл и закрыл C9 (дрейф кнопок на `shell`).**
  Сравнил определения `.btn*` через все 6 макетов. Три расхождения, все на `shell`:
  (1) базовый `.btn` padding `var(--s-3) var(--s-5)` против `s-6` у остальных 4 →
  привёл к `s-6`; (2) `.btn-primary` без `box-shadow: var(--shadow-sm)`, тогда как
  focus/result/summary/settings — все с ним (главный CTA выглядел плоским на home) →
  добавил `shadow-sm`; (3) `.btn-quiet` на shell был байт-идентичен общему `.btn-ghost`
  (transparent/ink/line-strong), но result/summary используют `.btn-quiet` для ДРУГОЙ,
  безрамочной третичной кнопки → одно имя = два вида (коллизия словаря). shell'овский
  `.btn-quiet` — **мёртвый CSS** (в разметке не используется, только `.btn-primary`),
  поэтому переименовал `.btn-quiet`→`.btn-ghost` (+ hover): коллизия убрана, канонич.
  вторичная кнопка теперь задокументирована и в home-эталоне; 0 изменений разметки →
  0 визуального регресса. **QA:** detector shell exit 0; кросс-скрин — все 5 `.btn-primary`
  теперь с `shadow-sm`, padding `s-5` = 0 вхождений (все `s-3 s-6`), `btn-quiet` в shell
  отсутствует. Правка чисто токен/словарь — цвета не тронуты (signal-strong/signal-on уже
  AA-verified, box-shadow текст-контраст не меняет, padding — тоже). **Кросс-экранная
  консистентность кнопок закрыта** → следующий тик: либо ещё один срез (type-шкалы hero/
  H1 через экраны, token-дрейф), либо старт Фазы 2 (перенос во фронт).
- **cr.5 (Фаза 1, тик 5)** — **C6 решён + углублённый аудит нашёл и закрыл C7.**
  **C6 (подвал):** решение — подвал только на hub-экране `shell` (home); 5 задача-экранов
  намеренно без подвала (фокус + ethos «инструмент не шумит»; кросс-нав уже в шапке каждого
  экрана → навигация не теряется). Это осознанное решение, не дефект; код не трогал.
  **C7 (углублённый скан AA-каркаса):** нашёл, что 5 из 6 skip-link'ов держат
  `background: var(--signal); color: var(--signal-on)` — тот же провальный combo (AA 4.40 < 4.5
  в light), что раньше чинили в `btn-primary`, но в focus-only skip-link пропустили. skip-link
  показывается по Tab (WCAG 2.4.1) → обязан держать AA. **Фикс:** 5 файлов `--signal` →
  `--signal-strong` (как shell/btn-primary). **QA:** detector 5/5 clean; замер skip-link AA
  (stats) — light **6.33** / dark **10.66** (было 4.40 light) ✓; правка позиционно-цветовая,
  остальной каркас без изменений. **Бэклог C1–C7 исчерпан** → Фаза 1 (первичная критика)
  в основном закрыта; следующий тик — либо ещё глубже (компонентная консистентность btn/
  hero-шкал/токенов через экраны), либо старт Фазы 2 (перенос во фронт).
- **cr.4 (Фаза 1, тик 4)** — **C5: входной ритм оценён; исправлены 2 кикера-эхо.**
  Разбор входа всех 6: `focus-question` («spring·транзакции»), `session-summary`
  («итоги сессии·spring·режим»), `shell` («тренажёр собеседований·java») — кикеры
  ФУНКЦИОНАЛЬНЫ (контекст/мета, отличаются от заголовка). А `stats` («аналитика» = H1
  «Аналитика») и `settings` («настройки» = H1 «Настройки») — lowercase-ЭХО заголовка,
  декоративный эйбрау, ровно то, что DESIGN.md запрещает («mono-кикер = функциональный
  тег, не эйбрау»). **Вывод: сам ритм kicker+headline — НЕ дефект** (системный, VARIANCE 6
  ок), дефект — только 2 эхо. Фикс (текст-онли, в том же `·`-идиоме): stats → «прогресс ·
  точность · повторы» (что трекает дашборд), settings → «сессия · оформление · данные»
  (три вкладки = скоуп). Теперь отличаются от H1 и несут навигацию. **QA:** detector оба
  clean; AA без изменений (тот же `--ink-3`/фон); 375 — overflow 0, кикер в пределах gutter
  (right 355<375). Дальше — C6 (подвал: решение).
- **cr.3 (Фаза 1, тик 3)** — **C4: kbd-help модалка перенесена в `focus-question.html`.**
  По контракту §8 глобальный оверлей горячих клавиш принадлежит экрану вопроса (там
  работают хоткеи 1-9), а жил только в `shell`. Добавил в эталон: `.icon-btn`-триггер в
  шапке (`aria-haspopup=dialog`), модалку `role=dialog aria-modal` (focus-trap, `?` toggle,
  Esc close, возврат фокуса на триггер, 9 строк вкл. навигацию ↑↓ по вариантам),
  scrim `color-mix(ink 42%)`, `kh-pop` только в no-preference. Статичную карту `.aside-hints`
  (3 клавиши, десктоп-рельс) ОСТАВИЛ намеренно: glanceable-подсказка на десктопе vs полный
  список по `?` (в т.ч. на мобиле) — осознанное слоение, не дубль. **QA:** detector exit 0;
  AA модалки+иконки обе темы (light min 7.77 / dark 9.23); функционал — `?` открыл/закрыл
  (toggle), фокус в окне, Esc + возврат фокуса, role=dialog/aria-modal/labelled, 9 строк;
  375 (тайтовый мобайл: brand+kbd+☰+тема) — overflow 0 при открытой и закрытой модалке,
  шапка edge-to-edge, оверлей кроет вьюпорт, модалка в границах. **Хук §8:** `.kbd-help-overlay/
  -modal/-close` role=dialog + `?`/Esc + focus-trap; при проводке 1-9 гасить пока оверлей
  открыт (отмечено в JS-комменте). Дальше — C5 (ритм) / C6 (подвал).
- **cr.2 (Фаза 1, тик 2)** — **C3: унифицирована sticky-шапка на всех 6 экранах.**
  5 статичных `.site-head` привёл к канону `shell` (`position: sticky; top: 0; z-index: 40`
  на сплошном `--paper`, без блюра) — теперь хром = один компонент, нав всегда доступна.
  Побочный эффект sticky-шапки: залипающие рельсы (`@media ≥1080`, `top: var(--s-5)` = 24px)
  тукались бы ПОД 73px-шапку на длинном скролле → поправил offset всех 5 рельсов
  (`.focus/result/summary-aside`, `.settings-rail`, `.stats-aside`) на
  `top: calc(var(--s-8) + var(--s-5))` = 80px (≈7px зазор под шапкой). Табличный
  sticky-thead (`session-summary` `.topic-table thead`) — внутри `overflow`-контейнера,
  container-relative, шапка не мешает, не трогал. **QA:** detector — все 6 clean; AA/ширина
  без изменений (правка чисто позиционная); проверка скроллом (stats, reload): шапка
  pinned top:0, рельс залипает на 80px, clears header ✓. Дальше — C4/C5/C6.
- **cr.1 (Фаза 1, тик 1)** — первичный разбор всех 6 макетов. Нашёл 2 явных слоп-дефекта
  и применил: **C1** `🔥`→Lucide flame SVG (`.streak-flame` теперь `currentColor` +
  `--spark-ink`, монохромно как весь набор); **C2** шапка `shell` — убрал
  `backdrop-filter: blur` и `color-mix`-полупрозрачность, оставил `position: sticky`
  (полезное UX) на сплошном `--paper`. Остальные находки (C3–C6) — в бэклоге worst-first
  для следующих тиков; C3 (унификация sticky-шапки на всех 6) — крупная согласованная
  правка, отдельный тик. QA после правок: detector + AA re-check `shell`.
