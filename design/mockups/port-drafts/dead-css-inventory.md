# Инвентаризация мёртвого CSS в base.css (R0.79, 2026-07-12)

Детерминированный скан: 368 уникальных класс-селекторов base.css × корпус
(все templates/*.html + fragments/*.html + static/js/*.js), затем контроль по
Java/Kotlin/prompts. 34 сырых кандидата → после разбора 2 реальные мёртвые
зоны + 1 микро-дубль + комментарии-артефакты. ID-селекторы сканом не
покрывались — семейство left-sidebar найдено точечно, полный ID-скан = зона
роста следующей итерации.

## Группа A — зона AI-разбора (~base.css 2585–2701, ≈110 строк) — УДАЛИТЬ

Классы: `takeaway-block/-title/-text`, `code-trace-block/-title/-steps/-step`,
`trace-step-num/-line/-state/-explanation`, `wrong-feedback/-title/-text`,
`comparison-block/-title/-table`.

Доказательство смерти: продюсеры — AI-разбор эндпоинты — вырезаны при
удалении AI 2026-07-07 (см. комментарий
`InterviewControllerApiTest.java:34`: «/api/wrong-feedback удалены — эти
AI-разбор эндпоинты вырезаны»); 0 вхождений классов в templates/js/Java.
Пост-ответный флоу app.js сегодня инжектит ТОЛЬКО `.related-questions`
(appendAnalysisBlock, app.js:1637) — его стили отдельные и живые.

Попутно при удалении:
- CSS-комментарий ~2648–2653 («классы есть в app.js», «AI-gated условия») —
  устарел, врёт: классов в app.js больше нет. Удалить вместе с зоной.
- app.js:1600 комментарий «(takeaway / трейс кода / похожие вопросы)» —
  оставить только «похожие вопросы» (двухстрочная правка комментария).
- Точная нижняя граница зоны: последнее правило `.comparison-table th`
  (~2695–2700), ПЕРЕД живой `@media (max-width: 600px)` с
  `.result-actions` — при применении перепроверить sed-ом.

## Группа B — старый layout /settings до вкладок — УДАЛИТЬ

1. `.control-section` (~1386–1389), `.control-section-tips` (~1391),
   `.control-title` (~1392–1396+): 0 вхождений в templates/js.
2. Всё семейство `#left-sidebar-*` (`-content`, `-filters`, `-session`,
   `-progress`): **0 в шаблонах и JS, 10 вхождений в base.css**, включая
   grid-зону в `@media` ~3315–3335. Это каркас /settings ДО редизайна во
   вкладки (0eb762a5); вкладки живут на `.settings-panel`/`.settings-tablist`.
   Внутри тех же media-блоков есть ЖИВЫЕ правила — вырезать хирургически
   по-селекторно, не блоками.

## Группа C — микро-дубль sr-only — РЕШИТЬ ПРИ ПРИМЕНЕНИИ

`.sr-only` — 3 вхождения в base.css (standalone ~270 + алиас в общей утилите
с `.visually-hidden` ~2580), 0 использований в templates/js (везде
`visually-hidden`). Либо снять алиас (минус 3 строки), либо оставить как
страховку для будущей разметки — решение при применении, вреда нет.

## Группа D — НЕ дефекты (артефакты скана, не трогать)

- `ed-btn/ed-chip/ed-field/ed-stack` (398), `ed-export/ed-nav-export` (507),
  `settings-tab-panel` (265) — комментарии, ДОКУМЕНТИРУЮЩИЕ прежние удаления.
- `springframework/autoconfigure/setRelay/ai-/result-page-/success-` —
  фрагменты кода-примеров и wildcard-упоминаний в комментариях, регэксп-шум.
- `.ed-theme-toggle` (~509) — живое правило шапки (44×44), ложный кандидат
  первого прохода.

## Протокол применения (следующий тик)

1. Collision guard; перед каждой зоной — live-проверка
   `document.querySelector` на отсутствие узлов по всем страницам
   (/,/stats,/settings,flashcard,error).
2. Удалить зону A + группу B (по-селекторно в media), комментарий-фикс
   app.js:1600.
3. Бамп base.css v=85→86 (+app.js v=58→59 если трогаем комментарий) в
   head.html и шаблонах; live-sync cp; grep-чистота по удалённым именам.
4. Скрин-контроль /settings + result-недоступен (зона A целит result-page —
   контроль через focus-page пост-ответных стилей не нужен: узлов нет).
5. Леджеры, explicit-pathspec коммит, удалить этот файл как применённый.
