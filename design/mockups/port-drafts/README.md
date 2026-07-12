# port-drafts — черновики Фазы E (НЕ подключены к проду)

Статус: **DRAFT**. Файлы здесь никем не загружаются (ни head.html, ни макетами) —
это ready-to-paste заготовки для Фазы E, подготовленные пока целевые прод-файлы
заблокированы параллельным WIP (§6 collision guard PROMPT_PLAN_FRONTEND.md).

| Черновик | Целевой файл | Куда вставлять | Блокер |
|---|---|---|---|
| `error-instrument-base.css` | `modules/quiz-app/src/main/resources/static/css/base.css` | СРАЗУ ПОСЛЕ общего блока §C7 (`html[data-design] .error-page ...`, ~строка 2060) | base.css dirty (чужой WIP) |
| `session-summary-instrument-base.css` | тот же base.css | СРАЗУ ПОСЛЕ общего блока §C6 (`html[data-design] .summary-page ...`, ~строка 1972) | base.css dirty (чужой WIP) |

## error-instrument-base.css — решения порта (по PORT_MAPPING_error.md)

1. **Шаблон error.html НЕ меняется вообще.** Риск №2 контракта (`.error-rail` vs
   `.error-actions`) решён отказом от рельса: все 3 ссылки рельса дословно дублируют
   `.error-actions` (главная/аналитика/настройки), а полноширинность от рельса не
   зависит (контракт, §38 п.2). Уникален был только `rail-hint` — его совет про
   устаревший токен уже есть в серверном 403-body. Итог: порт = чистый append в
   base.css, ноль правок Thymeleaf → минимальная поверхность конфликта.
2. **Специфичность:** `html[data-design="instrument"] .error-page .X` равна общему
   `html[data-design] .error-page .X` (атрибут с значением и без — одинаковый вес),
   поэтому блок обязан стоять **после** общего §C7 — выигрывает по порядку.
3. **Серверные тернарники** title/body/code не трогаются (черновик — только CSS).
   Макетные `STATES`/`?code=`/mock-bar в прод не идут.
4. **AA:** призрачный номер статуса — декоративная типографика (`aria-hidden`,
   реальный заголовок — `h2.error-title`), контраст-требования не применяются
   (как и в общем §C7 с `text-tertiary`). Все текстовые роли — на AA-токенах
   instrument (`text-secondary` 7.2:1, `text-tertiary` 5.4:1 light).
5. **Токен-маппинг** (словарь макета → прод):
   `--paper`→`--color-bg-primary` · `--surface`→`--color-bg-tertiary` ·
   `--ink/-2/-3`→`--color-text-primary/secondary/tertiary` ·
   `--line`→`--color-border-primary` · `--line-strong` (ghost-номер)→`--color-border-primary`+комментарий ·
   `--signal-strong/-on/-ink`→`--color-accent-primary/-on/-strong` ·
   `--font-hero/ui/mono`→`--font-family-display/body/mono`.

## session-summary-instrument-base.css — решения порта (по PORT_MAPPING_session-summary.md)

1. **Шаг 1 (этот черновик) = CSS-only**, DOM прод-шаблона не меняется: рестайл
   существующих хуков (`.card`→bg-tertiary/line, mono-цифры `.stat-value`,
   тихие body-заголовки секций, lowercase `.summary-mode-line`, hover ошибок
   paper+accent). Тон-классы точности не трогаем — instrument переопределяет
   статус-токены сам.
2. **Шаг 2 (отдельный тик Фазы E, правки session-summary.html)** — отложенные
   единицы, требующие шаблона: вердикт-headline (`summary-headline` + серверный
   `th:classappend`, п.32 маппинга), двухколоночный грид main+aside со sticky
   (п. mockup `.summary-grid` ≥1080px), перенос `.summary-actions` в aside.
   При шаге 2 ОБЯЗАТЕЛЬНО: сохранить a11y-роли таблицы (`role=table/rowgroup/…`,
   риск №1), PE-инъекцию `.summary-tools` (риск №3), плоские recommendations
   (риск №2, backend запрещён §4).
3. **Print НЕ портируется:** прод base.css уже несёт общий `@media print`
   (~строка 3208) с полным покрытием summary (скрытие actions/tools, ч/б,
   break-inside) — print-блок макета был mockup-QA (R0.19), дубль не нужен.

## Пересверка черновиков против WIP working-tree (R0.29, 2026-07-12)

Дрейф якорей проверен по диффу чужого WIP в base.css: ханки затрагивают только
зоны masthead-title (468/474), stats topic-table-wrap (1791), today-hero (2772),
summary-tools-status (3236) и НОВЫЕ правила `#interview-options label.option-wrong:has(...)`
(3592, зона будущего focus-порта — каверза R0.22 подтверждена). **Секции §C6
(1855) и §C7 (1975) НЕ тронуты, все хуки-якоря обоих черновиков на месте
(C7-i: 13 совпадений; C6-i: все 8 селекторов ≥1). Черновики валидны без правок.**
Точки вставки в working-tree: C6-i — после ~1972 (@media-хвост §C6), C7-i — после
~2070 (@media-хвост §C7, за `.error-actions .btn { width: 100% }`).

## Процедура применения (когда base.css чист)

1. `git status --short` — убедиться, что base.css больше не dirty.
2. Вставить содержимое черновика (без заголовка-шапки файла) после общего §C7.
3. Бамп `?v=N` для base.css в `fragments/head.html` (голова тоже должна быть чистой).
4. QA parity: bootRun + chrome-devtools, `?code=`-ветвления серверные — дергать
   реальные 404/403, обе темы, 375/1280/2560 + 320 reflow.
5. Удалить применённый черновик из port-drafts/ тем же коммитом.
