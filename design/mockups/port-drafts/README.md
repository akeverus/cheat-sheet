# port-drafts — черновики Фазы E (НЕ подключены к проду)

Статус: **DRAFT**. Файлы здесь никем не загружаются (ни head.html, ни макетами) —
это ready-to-paste заготовки для Фазы E, подготовленные пока целевые прод-файлы
заблокированы параллельным WIP (§6 collision guard PROMPT_PLAN_FRONTEND.md).

| Черновик | Целевой файл | Куда вставлять | Блокер |
|---|---|---|---|
| ~~`error-instrument-base.css`~~ | base.css | **ПРИМЕНЁН R0.46** (после §C7, v=73; QA: дельта = прогноз бит-в-бит, editorial-контроль = baseline R0.34, 320 clamp 70.4px чисто) | — |
| ~~`session-summary-instrument-base.css`~~ | base.css | **ПРИМЕНЁН R0.47** (после §C6, v=74; QA: 9 правил распарсены, утечки на /stats нет — stat-item там = baseline R0.37; ВИЗУАЛЬНЫЙ паритет summary = parity-фаза, нужна сессия) | — |

**Оба черновика применены — каталог port-drafts дальше живёт как журнал решений
порта (таблица выше + секции решений + пересверки R0.29/R0.33).**

| `focus-instrument-base.css` | base.css | после блока опций §C1 (за `.option-dimmed`/пост-ответной зоной, ~строка 800) | ГОТОВ К ПРИМЕНЕНИЮ (R0.49) |

## focus-instrument-base.css — решения порта (R0.48, по PORT_MAPPING_focus-training.md)

1. **Пересверка хуков по СВЕЖЕМУ дереву (WIP закоммичен):** app.js ставит опциям
   `option-correct` / `option-wrong` / `option-dimmed` (app.js:1435-1441) — стилизованы
   design-agnostic через статус-токены (base.css:785-789), instrument их наследует
   БЕЗ правок. Радиогруппа/счётчик-буква/checked-механика — base.css:722-783.
2. **Грейды флешкарт уже покрыты:** `.grade-1`/`.grade-4` hover-акценты error/success
   существуют design-agnostic (base.css:1060-1061) — п.53 маппинга закрыт прод-кодом,
   в черновик НЕ входит.
3. **Submit-«гибрид» из cr.40 НЕ дефект:** приклеенный radius `0 0 8 8` — editorial-
   решение; у instrument токены дают отдельный r10-чип + margin-top (training-actions
   @768: space-6). Композиция макета (submit отделён) выполняется токенами — правок нет.
4. **Черновик минимален (7 правил):** lowercase-тема (tertiary), medium-вопрос,
   mono/tabular прогресс, paper-карты опций, ring-hover accent-strong без заливки,
   checked border accent-strong, ::before-буква tertiary. Всё поверх существующих
   хуков; DOM/app.js не тронуты; буква остаётся CSS-счётчиком (риск №3).
5. **AA:** text-tertiary instrument = 5.4:1 light — проходит для темы/буквы;
   hover/checked границы — графические объекты ≥3:1 (accent-strong 205-teal).
6. **QA при применении:** активное состояние живьём (GET /) по baseline R0.38 —
   ожидаемая дельта: topic lowercase/tertiary, question weight 600→500, опция bg
   `oklch(0.985...)` (paper) вместо `0.958` (secondary), hover border teal-strong
   без заливки; editorial-контроль = R0.38 бит-в-бит; 320-reflow. Flashcard/empty
   ветки — parity-фаза (§4).

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

## Пересверка ВСЕХ маппингов Фазы D против WIP working-tree (R0.33, 2026-07-12)

WIP разросся на всю зону порта (app.js/stats.js/4 шаблона/head.html/контракт-тест).
Дифф-ревизия по поверхностям:

- **Черновики C6-i/C7-i — ПО-ПРЕЖНЕМУ ВАЛИДНЫ.** Ханки base.css: masthead-title
  (468→474+), stats collapse (1796), today-hero-hint (2769), summary-tools print
  (3233), focus mobile-clamp (3592+). §C6 (1857) и §C7 (тейл-якорь
  `.error-actions .btn { width:100% }` = строка 2070) не тронуты; все хук-селекторы
  обоих черновиков на месте (проверено grep-ом).
- **stats: механизм collapse СМЕНИЛСЯ** — было max-height 540px + fade `::after`,
  стало `tr[hidden]` (`display:none !important`) + MutationObserver-ресинк после
  сортировки + print `display: table-row !important`. PARITY-QA stats: проверять
  скрытие СТРОК (Tab не попадает в скрытые topic-link), не клип высоты; фейда
  больше нет. R0.32 no-JS вывод не устарел (сервер отдаёт развёрнутой).
- **settings: новый живой регион `#personalization-status`** (role=status,
  aria-live=polite, PE hidden; app.js `announceSaved` объявляет «Сохранено на этом
  устройстве…»). `wireSegControl` сменил сигнатуру (+`label` 3-м аргументом) —
  паттерн «4 касания» новой оси теперь включает label. Экспорт-блок получил
  `<h3>Экспорт данных</h3>`; копия danger-zone переписана (без AI-формулировок,
  «восстановятся из JSON-сидеров»). Структура вкладок/панелей/осей не тронута —
  маппинг R0.25 валиден.
- **focus/result шаблоны:** только бамп app.js v55→v56 — маппинги R0.22/R0.23
  валидны бит-в-бит.
- **base.css focus-зона:** новые правила иерархии заголовка (HDR-1): compact-clamp
  ≤600 + уплотнение chrome при активной сессии
  `.ed-masthead:has(.ed-masthead-progress)`. Instrument-порт focus/shell обязан
  НЕ перебивать эти правила (не задавать font-size тайтла шапки на focus-page).
- **КРИТИЧНО для порта shell: sticky-шапка ЗАПРЕЩЕНА прод-контрактом.**
  Новый TemplateFragmentContractTest ассертит
  `doesNotContain(".ed-masthead { position: sticky")` (обоснование WIP: FNO-риск).
  В макетах sticky+solid была частью языка (CRITIQUE C3) — при порте shell
  sticky НЕ портировать; языковое решение C3 остаётся макетным. Тест также
  пиннит ТОЧНЫЕ строки base.css (включая `\n`-форматирование clamp-правила) —
  после любой вставки в base.css обязателен прогон контракт-теста.
- **stats.js v13:** рестайл commonOpts графиков (tick/legend 12px, x-grid off,
  y drawTicks off) — инструмент-палитра графиков (позже) ребейзится на это.
- **head.html:** tokens v62 / base v72 в WIP — бамп Фазы E делать от АКТУАЛЬНЫХ
  значений на момент разблокировки, не от кэшированных в заметках.

## Процедура применения (когда base.css чист)

1. `git status --short` — убедиться, что base.css больше не dirty.
2. Вставить содержимое черновика (без заголовка-шапки файла) после общего §C7.
3. Бамп `?v=N` для base.css в `fragments/head.html` (голова тоже должна быть чистой).
4. QA parity: bootRun + chrome-devtools, `?code=`-ветвления серверные — дергать
   реальные 404/403, обе темы, 375/1280/2560 + 320 reflow.
5. Удалить применённый черновик из port-drafts/ тем же коммитом.
