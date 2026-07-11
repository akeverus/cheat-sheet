# PORT MAPPING — focus-training (D_PORT_MAPPING, R0.22)

> Маппинг макета `design/mockups/focus-question.html` (язык **Instrument**, 7 состояний)
> на прод-шаблон `modules/quiz-app/src/main/resources/templates/focus-training.html`.
> Цель — сделать порт (Фаза E) известной величиной. **Правок прод НЕ вносит.**
>
> ⚠️ **Каверза сверки:** прод-шаблон сейчас в незакоммиченном WIP параллельной сессии —
> маппинг снят с рабочего дерева на 2026-07-12. Перед портом ОБЯЗАТЕЛЬНО пересверить
> хуки по актуальному состоянию файла.

## Контракт поверхности (нельзя ломать)

- **Роут:** `GET /` → view `focus-training`. Model-attrs: `current` (вопрос+options),
  `interviewSession` (index/total/finished/mode), `reviewMode`, `flashcardMode`,
  `studyLearnPhase`, `flashcardRevealed`, `studyAnswerHtml`, `focusModeChipText/HintText`,
  `progressPercent`, `stats` (today-chip), `filter` (topic/importantOnly/onlyWrong/shuffle/
  ordered), `selectedGroup`, `weakTopics`, `focusEmptyRetryHref/Text`,
  `generationUnavailable` (зашит false после AI-removal).
- **POST-каналы:** `/answer` (форма #interview-form, 8 hidden-inputs фильтра),
  `/finish` (rail + empty), `/flashcard-reveal`, `/flashcard-grade` (grade 1–4),
  `/study-confirm`.
- **app.js v56 — самая JS-тяжёлая страница:** клавиатура радиогруппы (1–9 выбор,
  ↑/↓ навигация, Esc снять, Enter отправка), AJAX-флоу ответа (вердикт → уверенность →
  кнопка доп.разбора → sink `#extra-analysis-content` ПОД кнопкой), подсветка кода,
  browse-details toggle-подпись. `includeMermaid=true` (диаграммы в ответах).
- **Фрагменты:** `head`, `header(activePage='focus')`, `today-widget :: today-chip`,
  `training-actions` (submit + kbd-hint), `result-zone-head` (`#answer-flow-hint`,
  hidden=true), `post-answer-controls` («Похожие вопросы»).
- **Контракт-тест:** `TemplateFragmentContractTest` фиксирует th:href/th:text
  empty-actions (retry/settings) и структуру фрагментов.

## Таблица маппинга (mockup → прод-хук → действие/риск)

| Mockup (Instrument) | Прод-хук (сохранить) | Действие / риск |
|---|---|---|
| `body` | `body.focus-page` | Сохранить. |
| `.mock-bar` (7 состояний) | — | **НЕ портировать** — состояния в проде серверные (`th:if` current/flashcardMode/studyLearnPhase/…) + AJAX app.js. |
| `.site-head` inline | `~{fragments/header}` | Заменить фрагментом (хром общий). |
| `.focus-grid` (aside-рельс слева + main) | `.focus-topbar` (горизонтальный бар) + контент | **КРИТ-КОНФЛИКТ с решением пользователя:** вертикальная правая рейка уже была и «выглядела по-любительски — прямая претензия пользователя» (коммент в шаблоне, round-01) → заменена topbar'ом ≥1200px. Порт **НЕ возвращает боковой рельс**: instrument-голос кладётся на существующий topbar (mono-цифры прогресса, ring-чипы). Грид-раскладку макета НЕ портировать. |
| `.rail-count` «Вопрос 7 из 20» | `.focus-progress-line .session-progress` | Сохранить серверные значения; instrument = mono/tabular-nums. |
| `.progress-track/[role=progressbar]+.progress-fill` | `.session-progress-track[role=progressbar aria-valuemin/max/now]+.session-progress-fill[data-progress]` | Сохранить ARIA-атрибуты и `data-progress` (fill ширину ставит CSS/JS по нему, НЕ inline-style как в макете). |
| `.streak-chip` | today-chip фрагмент (`stats`) | Сохранить фрагмент; restyle токенами. |
| `.rail-finish` | `form POST /finish .rail-finish > .rail-finish-btn` | Сохранить форму (серверный финиш), не button-заглушку. |
| `.aside-hints` (карта хоткеев) | training-actions kbd-hint + глобальная `?`-справка | **НЕ портировать** — дублирует kbd-hint и keyboard-help модалку (юзер-гейт). |
| `.focus-topic` («spring·транзакции») | `p.focus-question-topic` (серверный, capitalize/replace) | Сохранить; instrument = lowercase mono + `.dot` разделитель допустим CSS-ом (`text-transform: lowercase`). |
| `h1.focus-question` | `h2.focus-question` `th:utext` inline-md | **Уровень заголовка НЕ менять** (h2 под h1-скрытым каркасом страницы); стиль — display serif clamp. |
| `details.question-code-details > summary.code-summary + pre.code-body` | `details.question-code-details > summary.btn.question-code-summary + pre.question-code>code` | Хуки совпадают почти дословно — сохранить прод-имена; restyle. Макетный `open` по умолчанию НЕ переносить (прод закрыт до клика — осознанно). |
| `label.opt > .opt-badge + .opt-text + .opt-mark` | `label[data-option-id][aria-label] > input[radio]+span` | **КРИТ:** прод рисует букву CSS-счётчиком `label::before` (letter в optionText срезается только в видимом span, aria-label с буквой). Порт = CSS-only: стилизовать `::before` как ring-бейдж макета; `.opt-mark`-галочку добавляет app.js-флоу — сверить классы before/after AJAX. НЕ добавлять span-бейджи в DOM. |
| `#interview-form onsubmit=false` | `form#interview-form POST /answer` + 8 hidden | Сохранить форму целиком (id, hidden-набор, required radio). |
| `.options#interview-options[role=radiogroup aria-label aria-describedby=options-flow-hint]` | идентично | Сохранить дословно (клавиатура app.js завязана). |
| `.submit-row > #interview-submit` | training-actions фрагмент (`submitId='interview-submit'`) | Сохранить фрагмент+id; restyle кнопки токенами. |
| result-состояние (verdict/`opt--correct`/`opt--incorrect`/`.explain`) | AJAX-инъекция app.js (verdict → sink `#extra-analysis-content`) + result-zone-head `#answer-flow-hint` | **КРИТ:** в проде разбор на этой странице рисует app.js; порт стилизует классы, которые app.js реально ставит (сверить в app.js: verdict/correct/incorrect-классы опций) — НЕ статическую разметку макета. Порядок вердикт→уверенность→кнопка→sink сохранён (memory project_post_answer_js). |
| flashcard: `#flash-reveal[aria-expanded aria-controls]` + `.flash-answer[aria-live]` + `.grade-row` 1–4 | 3 серверных ветки: browse `details#browse-details`; сессия `POST /flashcard-reveal`; revealed → `.flashcard-grade-buttons[role=group aria-labelledby]` + 4×`POST /flashcard-grade` `.flashcard-grade-btn.grade-1..4` | Сохранить все 3 ветки и POST-формы (SM-2 серверный!). Макетный JS-reveal НЕ портировать. Стили: `.grade-row` 2→4 кол. ≥560px, hover-акценты again=error/easy=success → на `.grade-1`/`.grade-4`. `.flash-prompt`/dot-маркер → `.flashcard-badge` restyle. |
| `.inline-alert--error/--info/--warn` (`role=alert/status`, `.ia-dismiss`) | **нет прод-хука** | Новый компонент. Кандидаты в проде: flash-сообщения об ошибке сохранения (app.js AJAX-fail путь) + `.flashcard-badge` (≈ia--info). Вводить на Фазе E только если есть реальный продьюсер сообщений; иначе — Фаза G вместе с app.js-обвязкой. НЕ плодить мёртвый UI. |
| loading-скелет (`role=status aria-busy`) | **нет прод-хука** (SSR-страница) | **N/A для MVC**: страница приходит отрендеренной. Возможное применение — AJAX-переход «Проверить ответ» (app.js) — отложить до Фазы G, отметить как кандидата. |
| empty («Нет вопросов») | `section.empty.empty-card-inner` (4 ветки th:if + 3 CTA c classappend-логикой primary) | Сохранить ветвление и **контракт-тест empty-actions** (th:href/th:text фиксированы). Restyle токенами. |
| done («Завершено») | ветка `finished` того же empty (флаг + CTA «Посмотреть итоги» POST /finish) | Прод покрывает done внутри empty — отдельного состояния НЕ создавать. |
| `<html data-design>` | прод: SSR `data-design="editorial"` | Не менять (instrument opt-in, решение пользователя). |

## Риски/решения для Фазы E (сводка)

1. **Не возвращать боковой рельс** — topbar-раскладка это зафиксированное решение
   пользователя (round-01); instrument-голос кладётся на неё, а не наоборот.
2. **app.js-инъекции — источник истины для result-состояния**: перед портом снять
   фактические классы, которые app.js ставит опциям/вердикту, и стилизовать ИХ.
3. **Буква варианта = CSS-счётчик**, DOM-бейджи макета не переносить (иначе дубль
   буквы и рассинхрон с aria-label).
4. **SM-2 флоу серверный** (reveal/grade POST) — никакого макетного JS-reveal.
5. **inline-alert/skeleton** — без продьюсера не вводить (мёртвый UI); кандидаты Фазы G.
6. **Порт затрагивает focus-training.html + base.css + возможно app.js** — все три
   в параллельном WIP → порт стартует ТОЛЬКО после полной разблокировки тройки.
7. **Контракт-тест** TemplateFragmentContractTest прогнать после любой правки шаблона.

## Готовность к порту

- **BLOCKED**: focus-training.html, base.css, app.js — все dirty (чужой WIP).
- Порядок внутри Фазы E: error → session-summary (черновики готовы) → focus-training
  (этот маппинг) → result → stats → settings → shell/head.
