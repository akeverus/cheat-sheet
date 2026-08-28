# CS-20260828-003 — Живое наблюдение: закрытие белых пятен §18 фронт-разведки

Follow-up к CS-20260827-001. Отчёт `.sdlc/CS-20260827-001/report.md:560-591`
перечислил 6 вопросов, на которые статическое чтение кода принципиально не
отвечает. Этот документ закрывает все шесть живым наблюдением: поднятым
стендом, реальным браузером (Playwright + CDP) и прямыми запросами к БД.
Каждый раздел ниже — один вопрос, с числами, снимками и командой
воспроизведения.

## 1. Метод и окружение

- **SHA:** `09b3e1f00dab5e8a3e936b4ef8e3a11fc202b9bd` (совпадает с HEAD ветки
  `sdlc/CS-20260828-003` на момент всех измерений; сверен после каждого блока —
  расхождений не было, перепрогонов не потребовалось).
- **Порт приложения:** `8091` (изолированно от пользовательского bootRun на
  `8080`, который не трогался).
- **Профиль Spring:** `qa` (`modules/quiz-app/src/main/resources/application-qa.yml:1-19`)
  — фиксированный `Clock` (`2026-07-15T12:00:00Z`), прогрев выключен,
  детерминированные фикстуры `due=2/learned=1` от `QaFixtureRunner`.
- **БД:** изолированная `interview_recon` в уже поднятом контейнере
  `quiz-postgres` (создана `CREATE DATABASE interview_recon OWNER interview`,
  удалена по завершении — раздел 12 Acceptance Criteria и данные
  `.sdlc/CS-20260828-003/data/user-db-guard.txt`). Datasource прокинут переменной
  `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/interview_recon`.
- **Старт:** `SERVER_PORT=8091 SPRING_PROFILES_ACTIVE=qa SPRING_DATASOURCE_URL=... ./gradlew :quiz-app:bootRun --no-daemon`,
  лог — `.sdlc/CS-20260828-003/data/boot.log.txt` (213 КБ; закоммичен под
  `.txt`-именем, т.к. `*.log` игнорируется `.gitignore:14`), старт подтверждён строкой
  `.sdlc/CS-20260828-003/data/boot.log.txt:51`: `Tomcat started on port 8091 (http) with context path '/'`
  в `00:09:04 MSK 2026-08-29` (`21:09:04 UTC 2026-08-28`).
- **Браузер:** Chromium `149.0.7827.55` через `Playwright` `^1.52.0`
  (`e2e/package.json:14`, резолвнутая версия из общего `node_modules` — `1.61.1`),
  взят из общего кэша `~/Library/Caches/ms-playwright`.
  Способ драйвера: **основной**, без фолбэков — симлинк
  `e2e/node_modules → ~/IdeaProjects/cheat-sheet/e2e/node_modules` (путь
  gitignored, `e2e/.gitignore:1`, удалён по завершении разведки) плюс
  `NODE_PATH` при запуске `node`, чтобы резолвер модулей из каталога
  `.sdlc/CS-20260828-003/recon/` находил пакеты из `e2e/node_modules`.
  Живой каскад CSS снимался через **Chrome DevTools Protocol**
  (`CSS.getMatchedStylesForNode`) внутри Playwright-контекста — основной способ
  из design.md сработал, фолбэк на текстовый разбор ленты не понадобился (сам
  разбор ленты использован отдельно для точных якорей слоя `pixel`, см.
  раздел 4 и 6).
- **Инструмент разведки:** 7 скриптов в `.sdlc/CS-20260828-003/recon/`
  (`shots.js`, `space.js`, `db.sh`, `cascade.js`, `pixel-layer.js`, `hex.sh`,
  `review-mode.js`) — коммитятся как доказательство, в продуктовый набор
  `e2e/*.spec.js` не добавлялись.
- **Чужое состояние:** `.sdlc/CS-20260828-003/data/user-db-guard.txt` — `review_state` в
  пользовательской БД `interview` до и после прогона: `265` и `265`
  (совпадают). Recon-БД `interview_recon` удалена
  (`DROP DATABASE interview_recon`, подтверждено `SELECT count(*) FROM
  pg_database WHERE datname='interview_recon'` = `0`).

## 2. WS-1. Визуальное состояние обеих тем на реальных вьюпортах (§18.1) и мёртвое пространство (§17)

**Вопрос:** отчёт CS-20260827-001 не видел ни одного отрендеренного пикселя
(`.sdlc/CS-20260827-001/report.md:565-568`).

**Метод:** `.sdlc/CS-20260828-003/recon/shots.js` прогнал матрицу `{home, train, stats, topics} ×
{dark, light} × {1280×800, 1440×900, 1680×1050, 1920×1080, 2560×1440}` = 40
снимков, плюс 2 Retina (`dpr=2`, 1440×900) для `home`/`stats`, плюс Tier B
(`bookmarks, settings, review, summary` × обе темы, 1440×900). Тема ставится
через `localStorage.theme` (как в `e2e/visual-matrix.spec.js:92-102`),
`screenPreset` НЕ выставляется — наблюдается поведение по умолчанию. Home
пинится тем же URL, что и в визуальной матрице
(`e2e/visual-matrix.spec.js:33-36`, вопрос id 2060), волатильные блоки
скрыты тем же приёмом (`VOLATILE_PRESENCE`, `e2e/visual-matrix.spec.js:56-58`).

**Результат:** 48 обязательных снимков сняты, все непустые
(`find .sdlc/CS-20260828-003/shots -size 0` → 0 файлов). Индекс —
`data/shots-index.json`. `unreachable: []` — ни один комбо не вернул ошибку.

**Находка внутри этого блока (см. также LIVE-01):** на ВСЕХ 40 комбинациях
`data-pixel` фактического узла `<html>` равен `"med"` — включая 2560×1440.
Причина — не баг измерения: `screenPreset` по умолчанию равен `laptop`
(`modules/quiz-app/src/main/resources/templates/fragments/head.html:235-241`), и связь пресета с пикселем —
`modules/quiz-app/src/main/resources/templates/fragments/head.html:204-209`
(`laptop → pixel: "med"`, `wide → pixel: "high"`) — **не зависит от реальной
ширины окна**, только от явного выбора пользователя в `/settings`. То есть
на 2560px без ручного действия пользователь получает ту же плотность
пиксель-арта, что и на 1280px.

**Tier B:** `bookmarks`, `settings`, `review` — по 2 снимка каждый (обе темы),
все получены. `summary` — недостижим напрямую: `GET /session-summary` без
завершённой сессии отвечает `200`, но фактический URL после навигации —
`http://localhost:8091/` (редирект на `/`, экран одноразов и требует
`POST /finish` с `_csrf`, как и предупреждал design.md). Обе строки
зафиксированы: `НЕДОСТИЖИМО: summary/dark — HTTP 200, редирект на
http://localhost:8091/` и `НЕДОСТИЖИМО: summary/light — HTTP 200, редирект на
http://localhost:8091/` (`data/shots-index.json`, ключ `missing`).

**§17 (мёртвое пространство), `.sdlc/CS-20260828-003/recon/space.js`:** для Tier A на 1920×1080 и
2560×1440, обе темы — `data/space-metrics.json` (16 замеров). Числа (доля
ширины `#main-content` от вьюпорта, `mainWidthRatio`):

| Экран | 1920×1080 | 2560×1440 | Крупнейший жёлоб на 2560 |
|---|---|---|---|
| home | 0.871 | 0.863 | 287px слева от `#main-content` |
| train | 0.871 | **0.700** | **495px** слева от `#main-content` |
| stats | 0.871 | **0.700** | **495px** слева от `#main-content` |
| topics | 0.871 | **0.700** | **495px** слева от `#main-content` |

На 2560px три экрана из четырёх (`train`, `stats`, `topics`) используют
только 70% ширины вьюпорта — `home` устроен иначе и держит 86.3%. Это
числовой ответ на «беспощадный аудит пустого пространства» (см. LIVE-02).

**Вердикт: ПОДТВЕРЖДЕНО** — обе темы на всех 5 вьюпортах увидены живьём;
белое пятно закрыто числами и 50 снимками (индекс — раздел 11).

## 3. WS-2. Живое состояние БД (§18.2)

**Вопрос:** число вопросов, optionless-вопросов, распределение MCQ
(`.sdlc/CS-20260827-001/report.md:569-572`).

**Метод:** `recon/db.sh` — 6 запросов к `interview_recon` (после
`StartupRunner.importAll()` на HEAD) плюс 3 read-only сверочных запроса к
пользовательской `interview`. Полный текст запросов и сырой вывод —
`.sdlc/CS-20260828-003/data/db-counts.txt`. Дополнительно снят `GET /api/stats` →
`data/api-stats.json`.

| Метрика | recon-БД (`interview_recon`) | пользовательская БД (`interview`) | аудит 2026-08-22 |
|---|---|---|---|
| Всего вопросов | **9818** | **9818** | 9818 (`docs/frontend-refoundation-audit.md:19`) |
| Optionless (без единого варианта) | **42** | **42** | 42 (`docs/frontend-refoundation-audit.md:24`) |
| Распределение по числу вариантов | `0→42, 4→9774, 5+→2` | `0→42, 4→9774, 5+→2` | — |
| Число тем | 319 | — | — |
| Число категорий | 25 | — | — |
| Позиция correct (display_order 0..3) | `2514 / 2491 / 2421 / 2352` | — | — |

Recon-БД совпадает с пользовательской ПОЛНОСТЬЮ (9818/42 и распределение
0/4/5+ идентичны) — импорт из репозитория на HEAD детерминирован и не зависит
от истории конкретной БД. Оба совпадают с числами аудита
2026-08-22 (`docs/frontend-refoundation-audit.md:19,24`) день в день без
расхождений — банк с тех пор не менялся по количеству вопросов/optionless.
Распределение позиции правильного варианта (2514/2491/2421/2352 из ~9776
MCQ-вопросов) почти равномерное — 25.7% / 25.5% / 24.8% / 24.1%, разброс
1.6 п.п., без явного перекоса в конкретную позицию (соответствует принципу
визуальной нейтральности MCQ, миссия §8-12).

`GET /api/stats` (`data/api-stats.json`): `{"total":9818,"due":9817,"fresh":9815,"learned":1,"correct":3,"wrong":2}`.
Счётчики `learned=1`, `wrong=2` — это ровно фикстуры `QaFixtureRunner`
(`due-fixtures: 2, learned-fixtures: 1`,
`application-qa.yml`), а не живой прогресс — отчёт называет это явно, чтобы
не выдать фикстуры за реальную историю ответов.

**Вердикт: ПОДТВЕРЖДЕНО** — числа сняты живьём, совпадают между собой и с
опорным аудитом; расхождений не обнаружено.

## 4. WS-3. Реальный порядок каскада в браузере (§18.3)

**Вопрос:** манифест декларирует порядок, но вычисленный стиль в DOM не
проверялся; спорные места — `components-pending`, `components-filter-bar`
(`.sdlc/CS-20260827-001/report.md:573-577`).

**Метод:** `.sdlc/CS-20260828-003/recon/cascade.js`, четыре проверки через CDP
(`CSS.getMatchedStylesForNode`) и текст живой ленты:

1. **Порядок `@layer` в отданной ленте.** Лента `focus` (`/`) отдаёт
   `@layer tokens, reset, shell, components, pages, utilities, pixel;` —
   байт-в-байт совпадает с объявлением `tokens.css:4`. `data/cascade-layers.json`.
2. **Гейт `.filter-bar`.** Наивная проверка `includes('.filter-bar')` дала
   ложное срабатывание на `focus`: `print.css` (гейт `*`) в `@media print`
   гасит `.filter-bar` наравне с `.resume-banner`/`.report-zone` — это не
   `components-filter-bar.css`. Точная проверка по базовому правилу
   `html[data-design] .filter-bar {` (`components-filter-bar.css:8`)
   подтвердила заявление манифеста буквально: **отсутствует** на `focus`,
   **присутствует** на `stats` (`data/cascade-layers.json`,
   `css-bundle-manifest.txt:94`).
3. **Спор `components-pending` vs `components-controls`.** Пробный узел —
   реальный `<button class="btn" data-pending="action" aria-disabled="true">`,
   подключённый к живому документу (не `#interview-submit`: у него
   собственный ID-скоупленный оверрайд `components-controls.css:157-158`,
   который старше по специфичности и замаскировал бы спор). Победитель по
   `cursor` и `opacity` — правило
   `[data-pending="action"][aria-disabled="true"], [data-pending="action"]`
   (`components-pending.css:9-10`, `cursor: progress`, `opacity: 1`), а НЕ
   `.btn[aria-disabled="true"]` (`components-controls.css:210-218`,
   `opacity: 0.55`, `cursor: not-allowed`) — при равной специфичности (0,3,1)
   выигрывает components-pending.css, потому что стоит позже в манифесте
   (`css-bundle-manifest.txt:39` после `:25`). Обоснование манифеста
   (`components-pending.css:7-8`) подтверждено вычислением, а не чтением.
   `data/cascade-pending.json`.
4. **Пара равной специфичности, где `pixel` бьёт `components`.** `.card` на
   `/stats`: `components-card.css:5` задаёт `background: var(--color-bg-secondary)`
   (шорт-хенд, неявно `background-image: none`), `pixel-cards.css:7-15` тем же
   селектором `html[data-design] .card` задаёт
   `background-image: var(--px-grain-surface)`. Оба правила совпадают по
   селектору → равная специфичность → решает порядок слоёв, объявленный
   `@layer tokens, reset, shell, components, pages, utilities, pixel;`
   (`tokens.css:4`): `components` идёт раньше `pixel`, поэтому побеждает
   `pixel` независимо от физического порядка файлов в ленте
   (`components-card.css` — `css-bundle-manifest.txt:43`, `pixel-cards.css` —
   `css-bundle-manifest.txt:149`). Живой узел подтвердил: победитель
   `background-image` — правило `pixel-cards.css`, значение
   `var(--px-grain-surface)`.
   `data/cascade-pixel-vs-components.json`.

**Вердикт: ПОДТВЕРЖДЕНО** — реальный каскад в браузере совпадает с
декларацией манифеста по всем 4 проверкам, включая оба спорных места,
названных в CS-20260827-001.

## 5. WS-4. Дублирование палитры вне `tokens.css` (§18.4)

**Вопрос:** §12 заявлено, но не выполнено — не проверено
(`.sdlc/CS-20260827-001/report.md:578-582`).

**Метод:** `recon/hex.sh` — инвентарь ВСЕХ цветовых литералов (`hex`,
`rgb()/rgba()`, `hsl()/hsla()`) по всем **44** файлам
`static/css/*.css` (43 в манифесте + `base.css` вне манифеста), с
классификацией каждого уникального литерала. Полная таблица —
`data/hex-inventory.tsv` (116 строк). Проверка полноты: множество hex-литералов
из `grep -rhoE "#[0-9a-fA-F]{3,8}\b" .../css/*.css | tr 'A-F' 'a-f' | sort -u`
(97 значений) целиком содержится в первой колонке TSV (`comm -23` → пусто).

Агрегат по классам:

| Класс | Кол-во | Значение |
|---|---|---|
| **S** | 74 | значение живёт только внутри `tokens.css` — источник в порядке |
| **C** | 15 | встречается только внутри `/* ... */` комментариев (grep-артефакт) |
| **P** | 15 | палитровый цвет БЕЗ центрального токена |
| **T** | 6 | точный дубль значения токена, захардкожен ВНЕ `tokens.css` |
| **N** | 6 | не-палитровое употребление (градиент/тень/маска/data-uri) |

**Класс T — 6 литералов, поимённо** (значение · файлы · раз):

- `#ffffff` — `print.css`, `tokens.css` (4 вхождения)
- `#8fbf6a` — `pixel-fx.css`, `tokens.css` (3)
- `#e08a41` — `pixel-fx.css`, `tokens.css` (3)
- `#e8705c` — `pixel-fx.css`, `tokens.css` (3)
- `#1a1305` — `pixel-fx.css`, `tokens.css` (2)
- `#d0a33c` — `pixel-fx.css`, `tokens.css` (2)

**Класс P — 15 литералов, поимённо:**

- `#141413` (7×, `print.css`), `#fff` (3×, `print.css`), `#999488` (2×,
  `print.css`), `#f0f0f0` (2×, `components-prose.css`, `print.css`),
  `#444440`, `#66625a`, `#f5f5f5` (по 1×, все `print.css`) — **7 из 15**
  палитровых литералов без токена живут в `print.css`: печатная версия по
  конструкции не наследует тему экрана (`@media print`), поэтому хардкоды тут
  отчасти оправданы дизайном, но остаются вне охвата любого аудитора.
- `#7a6e57`, `#8a7455`, `#12100b`, `#6b5218` — не хардкоды в свойствах, а
  **локальные ad-hoc токены** `--px-edge-ink`/`--px-brass-deep`/
  `--px-edge-surface-ink` (`pixel-fx.css:30,38,53,66`), объявленные ВНУТРИ
  `pixel-fx.css`, а не в `tokens.css` — вторая, теневая система токенов вне
  единого источника (комментарии рядом фиксируют контраст `3.16/3.74/3.43` —
  осознанный выбор, но не в `tokens.css`).
- `#b3261e` — **не хардкод значения токена, а fallback** в
  `var(--color-status-error, #B3261E)` (`components-controls.css:359-360`,
  `pages-stats.css:468`). Практически недостижим: `--color-status-error`
  всегда определён в `tokens.css:65` (dark, `#e8705c`) и `:313` (light,
  `#a5342a`) — ни то, ни другое НЕ совпадает с fallback-значением `#B3261E`.
  Мёртвый код с рассинхронизированным значением — минорная находка (LIVE-05).
- `rgba(0, 0, 0, 0.95)` (`reset-layout.css`), `rgba(0,0,0,.3)`
  (`pixel-options.css`), `rgba(0,0,0,.35)` (`pixel-fx.css`) — оттенки чёрного
  с альфой вне токенов (не подпадают под классификатор N, потому что не incl.
  слова gradient/shadow/mask в той же строке, хотя визуально это затемнения).

**Дыра в существующем аудиторе — подтверждена.** `scripts/design-token-audit.py:206-213`
проверяет хардкод-hex действительно только в `BASE` (`base.css`, 829 Б),
гейт pre-commit `.pre-commit-config.yaml:43-48` называется «хардкоды в
base.css» честно, но покрытие остальных **43** файлов (~712 КБ CSS-корпуса)
равно нулю — 6 реальных дублей токена (T) и 15 палитровых литералов без
токена (P) в этих файлах не проверяет никто.

**Вердикт: ПОДТВЕРЖДЕНО** — дублирование существует (6 T + часть из 15 P),
покрытие аудитора нулевое вне `base.css`; оба факта проверены вычислением по
всему корпусу, а не по цитате §12.

## 6. WS-5. «Слой pixel ничего структурного не меняет» (§18.5)

**Вопрос:** заявлено манифестом, не проверено вычислением
`position`/`display` в DOM (`.sdlc/CS-20260827-001/report.md:583-585`).

**Метод:** `.sdlc/CS-20260828-003/recon/pixel-layer.js`. На `/` (пин id 2060) и `/stats`, тёмная
тема, 1440×900, `screenPreset=wide` (худший случай `data-pixel=high`,
подтверждено живьём: оба прогона дали `data-pixel: "high"`) — обход до 400
узлов внутри `#main-content` (`.sdlc/CS-20260828-003/recon/pixel-layer.js:110`, на `/stats` это упор
в кап `slice(0, 400)`: узлов-кандидатов там ≥400, выборка усечена, на `/` —
191 узел без усечения). Из узлов-кандидатов после дедупликации селектора
реально опрошено через CDP `DOM.querySelector`/`CSS.getMatchedStylesForNode` —
95 и 78 узлов соответственно (`nodesScanned`). Слой `pixel` определён НЕ эвристикой по имени файла, а
разбором самой отданной ленты: все `@layer pixel { ... }` блоки (по одному на
каждый из 9 файлов `pixel-*.css`, скобки сбалансированы) дают пары
`(селектор, свойство) → значение`, которые точно принадлежат слою pixel в
ЖИВОЙ ленте текущего запроса (149→135 таких пар после исключения дублей
текста селектора; первая версия матчинга только по тексту селектора дала
ложные срабатывания — пойманы и исправлены на этом же прогоне, см. ниже).

**Найдены НАСТОЯЩИЕ структурные объявления в слое `pixel`** (проверка на
`position/display/float/grid*/flex*/inset/top/right/bottom/left`):

| Селектор | Свойство | Значение | Источник |
|---|---|---|---|
| `.card, .panel, .explanation-panel, .question-code-wrap, .resume-banner, .error-card, .summary-hero, .builder-card, .metric` | `position` | `relative` | `pixel-cards.css:7-18` |
| `.brand .avatar` | `display` | `grid` | `pixel-brand.css:9-16` |

Расширенная проверка (`width/height/min-*/max-*/margin*`, не опровергает
буквальное заявление, но идёт в находки): `.brand .avatar { width: 40px;
height: 40px; }` (`pixel-brand.css:10-11`) — размер знака бренда целиком
задаётся в слое `pixel`, а не в `components`/`shell`; выключи слой `pixel` —
и `.brand .avatar` схлопнется в блок нулевого размера (см. LIVE-03).

`data/pixel-layer-scan.json` — home: 6 структурных нарушений (после
дедупликации по правилу — 2 уникальных), 4 расширенных; stats: 22 (2
уникальных после дедупликации), 4 расширенных (числа выше — до дедупликации,
т.к. один и тот же `.card` встречается многократно как разные узлы DOM).

**Методологическая находка этого блока.** Первая версия сопоставления
«селектор встречается внутри `@layer pixel` где-то в ленте» ловила ложные
срабатывания: `.app { display: grid; grid-template-columns: ... }`
(`shell.css`) отмечался как «pixel», потому что ТОТ ЖЕ текст селектора
`html[data-design] .app` отдельно встречается в
`pixel-motion-parity.css:7` (гасит `animation`/`background-image` под
`@media (prefers-reduced-motion: reduce)`) — те же 4 слова селектора,
разные свойства, разные слои. Исправлено сопоставлением по паре
(селектор, свойство) со значением, а не по одному тексту селектора.

**Вердикт: ОПРОВЕРГНУТО** — буквальное заявление манифеста
(`css-bundle-manifest.txt:142-143`, «Ничего структурного
(position/display/grid)») не выдерживает проверки: `position: relative` на
9-селекторной группе карточек и `display: grid` на знаке бренда — оба живут
в слое `pixel`, оба подтверждены вычислением на живом DOM, а не найдены в
исходниках. Ни один из них не привёл к наблюдаемой поломке вёрстки на
снятых 50 снимках, но заявление как формулировка — ложно.

## 7. WS-6. `/review` как режим, а не раздел (§18.6)

**Вопрос:** подсветка активного раздела и счётчик ошибок в рельсе прогресса
не проверены живьём (`.sdlc/CS-20260827-001/report.md:586-590`).

**Метод:** `.sdlc/CS-20260828-003/recon/review-mode.js`, реальный путь через форму (не `curl`,
чтобы session/CSRF отрабатывали штатно):

1. `GET /review` → `200`, тот же шаблон, что `/`: в разметке присутствует
   `<input type="hidden" name="onlyWrong" value="true">` (форсированный
   фильтр `ReviewModeService.apply` — `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/service/flow/ReviewModeService.java:21-33`).
2. Активный пункт навигации — **ровно один на каждую поверхность**
   (`a.active[aria-current="page"]`): найдено 2 узла (рельс `sidebar.html` +
   `mobile-nav.html`), оба с текстом `"Тренировка"` — совпадает с
   `NavSection.java:44` (`FOCUS`) и обоснованием javadoc
   (`NavSection.java:37-40`): `/review` подсвечивает раздел «Тренировка», а
   не отдельный пункт «Ошибки».
3. Счётчик «Ошибок к разбору» в `.focus-bank-progress` (рельс прогресса) —
   снят ДО: **2**. Определён показанный вопрос (`questionId=1`, из скрытого
   поля формы заметки), запрошен ЗАВЕДОМО НЕВЕРНЫЙ вариант через прямой
   `SELECT id FROM answer_options WHERE question_id=1 AND is_correct=0 ORDER
   BY display_order LIMIT 1` (получен `id=1`) — вариант выбран и форма
   отправлена как в браузере (`label[data-option-id="1"] input`→`check()`,
   клик `#interview-submit`). Повторный `GET /review` — счётчик ПОСЛЕ: **2**
   (дельта `0`). Наблюдение честное: вопрос УЖЕ был помечен как ошибочный
   (review-режим и так показывает только их), повторный неверный ответ на
   уже-ошибочный вопрос не меняет счётчик — это ожидаемо, не дефект.
   Два снимка — `shots/review-mode-before.png`, `shots/review-mode-after.png`.

`data/review-mode.json` — полный сырой результат прогона.

**Вердикт: ПОДТВЕРЖДЕНО** — `/review` живьём ведёт себя как заявлено:
режим внутри «Тренировки», не отдельный раздел; фильтр `onlyWrong`
форсирован; счётчик ошибок читается из живого DOM, поведение при повторном
неверном ответе на уже-ошибочный вопрос задокументировано числами и
снимками, а не предположением.

## 8. Итог по белым пятнам

| # | Белое пятно (CS-20260827-001) | Вердикт | Ключевое число/факт |
|---|---|---|---|
| WS-1 | Визуальное состояние обеих тем (§18.1) + мёртвое пространство (§17) | ПОДТВЕРЖДЕНО | 50 снимков; `data-pixel` везде `med` по умолчанию; на 2560px train/stats/topics используют 70% ширины |
| WS-2 | Живое состояние БД (§18.2) | ПОДТВЕРЖДЕНО | 9818 вопросов / 42 optionless — recon-БД = пользовательская БД = аудит 2026-08-22 |
| WS-3 | Реальный порядок каскада (§18.3) | ПОДТВЕРЖДЕНО | `@layer` порядок, гейт `.filter-bar`, спор pending/controls и pixel/components — все 4 подтверждены CDP |
| WS-4 | Дублирование палитры вне tokens.css (§18.4) | ПОДТВЕРЖДЕНО | 6 T + 15 P литералов из 116; аудитор покрывает только `base.css` (829 Б из ~712 КБ) |
| WS-5 | «pixel ничего структурного не меняет» (§18.5) | ОПРОВЕРГНУТО | `position:relative` на `.card`, `display:grid` на `.brand .avatar` — оба в слое pixel |
| WS-6 | `/review` как режим, а не раздел (§18.6) | ПОДТВЕРЖДЕНО | 1 активный пункт «Тренировка» на обеих поверхностях, `onlyWrong` форсирован, счётчик читается живьём |

## 9. Находки

| id | P | Что | Доказательство | Что делать |
|---|---|---|---|---|
| LIVE-01 | P2 | `data-pixel` не зависит от реальной ширины окна — только от `screenPreset` в `localStorage` (по умолчанию `laptop`→`med`). На 2560px без ручного действия пользователя плотность пиксель-арта та же, что на 1280px | `data/shots-index.json` (40/40 записей `dataPixel:"med"`); `modules/quiz-app/src/main/resources/templates/fragments/head.html:204-241` | Продуктовое решение (миссия §41 ultrawide): либо авто-детект пресета по `window.innerWidth` при первом визите, либо явный онбординг в `/settings` на широких экранах |
| LIVE-02 | P1 | На 2560×1440 `/train`, `/stats`, `/topics` используют только 70% ширины вьюпорта (`mainWidthRatio=0.700`), крупнейший жёлоб — 495px слева от `#main-content`; `/` в тех же условиях — 86.3% | `data/space-metrics.json` (4 записи `2560x1440`) | Прямой вход для миссии §17 «беспощадный аудит пустого пространства»: разобраться, почему `home` устроен шире, чем 3 других Tier A экрана, и выровнять либо содержимым, либо мерой |
| LIVE-03 | P2 | Слой `pixel` объявляет структурные свойства вопреки собственному манифесту: `position:relative` на группе `.card`/`.panel`/... (`pixel-cards.css:18`), `display:grid`+`width:40px`+`height:40px` на `.brand .avatar` (`pixel-brand.css:10-16`) — размер знака бренда живёт ЦЕЛИКОМ в pixel-слое | `data/pixel-layer-scan.json`, `data/cascade-pixel-vs-components.json` | Либо перенести `width/height/display` знака бренда в `components-*.css` (структура) и оставить в `pixel-brand.css` только декор, либо переформулировать комментарий `css-bundle-manifest.txt:142-143`, если решение осознанное |
| LIVE-04 | P1 | `scripts/design-token-audit.py:206-213` проверяет хардкод-hex только в `base.css` (829 Б); 43 остальных файла (~712 КБ, 99.9% корпуса) не проверяет никто. Найдено 6 точных дублей значений токенов (T) вне `tokens.css` и 4 ad-hoc локальных токена в `pixel-fx.css`, минующих единый источник | `data/hex-inventory.tsv` (116 строк, классы T/P поимённо в разделе 5) | Follow-up (уже в design.md): расширить аудитор на весь `static/css/*.css`, allow-list — из класса `T`/`P` этого инвентаря |
| LIVE-05 | P3 | `var(--color-status-error, #B3261E)` (`components-controls.css:359-360`, `pages-stats.css:468`) — fallback-значение НЕ совпадает ни с dark (`#e8705c`, `tokens.css:65`), ни с light (`#a5342a`, `tokens.css:313`) токеном; токен всегда определён, поэтому fallback практически не достижим, но несёт рассинхронизированное значение | `data/hex-inventory.tsv` (строка `#b3261e`), `tokens.css:65,313` | Косметика: удалить fallback либо привести его к текущему dark-значению `#e8705c` |

## 10. Приложение A. Команды воспроизведения

```bash
# --- Стенд ---
docker compose up -d postgres
docker exec quiz-postgres psql -U interview -d postgres \
  -c "CREATE DATABASE interview_recon OWNER interview"
SERVER_PORT=8091 SPRING_PROFILES_ACTIVE=qa \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/interview_recon \
  ./gradlew :quiz-app:bootRun --no-daemon > .sdlc/CS-20260828-003/data/boot.log.txt 2>&1 &

# --- Драйвер браузера (из корня worktree) ---
ln -s ~/IdeaProjects/cheat-sheet/e2e/node_modules e2e/node_modules

# --- Блок A: визуальная матрица + мёртвое пространство ---
cd e2e
NODE_PATH="$(pwd)/node_modules" E2E_BASE_URL=http://localhost:8091 \
  node ../.sdlc/CS-20260828-003/recon/shots.js
NODE_PATH="$(pwd)/node_modules" E2E_BASE_URL=http://localhost:8091 \
  node ../.sdlc/CS-20260828-003/recon/space.js
cd ..

# --- Блок B: живая БД ---
bash .sdlc/CS-20260828-003/recon/db.sh
curl -s http://localhost:8091/api/stats > .sdlc/CS-20260828-003/data/api-stats.json

# --- Блок C: каскад и pixel-слой ---
cd e2e
NODE_PATH="$(pwd)/node_modules" E2E_BASE_URL=http://localhost:8091 \
  node ../.sdlc/CS-20260828-003/recon/cascade.js
NODE_PATH="$(pwd)/node_modules" E2E_BASE_URL=http://localhost:8091 \
  node ../.sdlc/CS-20260828-003/recon/pixel-layer.js
cd ..

# --- Блок D: инвентарь палитры (весь корпус static/css/*.css) ---
bash .sdlc/CS-20260828-003/recon/hex.sh

# --- Блок E: /review как режим ---
cd e2e
NODE_PATH="$(pwd)/node_modules" E2E_BASE_URL=http://localhost:8091 \
  node ../.sdlc/CS-20260828-003/recon/review-mode.js
cd ..

# --- Страж пользовательской БД (до/после, оба раза одно и то же число) ---
docker exec quiz-postgres psql -U interview -d interview -tAc \
  "SELECT count(*) FROM review_state"

# --- Демонтаж ---
rm e2e/node_modules
kill <pid Tomcat на 8091>   # см. data/boot.log.txt:51 за PID процесса
docker exec quiz-postgres psql -U interview -d postgres -c \
  "DROP DATABASE interview_recon"
```

## 11. Приложение B. Индекс снимков

Формат: `файл — экран/тема/вьюпорт — data-pixel — наблюдение`. Полные
метаданные (включая `data-theme`, `dpr`) — `data/shots-index.json` и
`data/review-mode.json`.

**Tier A — обязательная матрица (40 снимков, `data-pixel="med"` на всех
пяти вьюпортах обеих тем — см. LIVE-01):**

- `shots/home-dark-1280x800.png` — home/dark/1280×800
- `shots/home-dark-1440x900.png` — home/dark/1440×900
- `shots/home-dark-1680x1050.png` — home/dark/1680×1050
- `shots/home-dark-1920x1080.png` — home/dark/1920×1080
- `shots/home-dark-2560x1440.png` — home/dark/2560×1440 — mainWidthRatio 0.863
- `shots/home-light-1280x800.png` — home/light/1280×800
- `shots/home-light-1440x900.png` — home/light/1440×900
- `shots/home-light-1680x1050.png` — home/light/1680×1050
- `shots/home-light-1920x1080.png` — home/light/1920×1080
- `shots/home-light-2560x1440.png` — home/light/2560×1440
- `shots/train-dark-1280x800.png` — train/dark/1280×800
- `shots/train-dark-1440x900.png` — train/dark/1440×900
- `shots/train-dark-1680x1050.png` — train/dark/1680×1050
- `shots/train-dark-1920x1080.png` — train/dark/1920×1080
- `shots/train-dark-2560x1440.png` — train/dark/2560×1440 — mainWidthRatio 0.700, жёлоб 495px (LIVE-02)
- `shots/train-light-1280x800.png` — train/light/1280×800
- `shots/train-light-1440x900.png` — train/light/1440×900
- `shots/train-light-1680x1050.png` — train/light/1680×1050
- `shots/train-light-1920x1080.png` — train/light/1920×1080
- `shots/train-light-2560x1440.png` — train/light/2560×1440
- `shots/stats-dark-1280x800.png` — stats/dark/1280×800
- `shots/stats-dark-1440x900.png` — stats/dark/1440×900
- `shots/stats-dark-1680x1050.png` — stats/dark/1680×1050
- `shots/stats-dark-1920x1080.png` — stats/dark/1920×1080
- `shots/stats-dark-2560x1440.png` — stats/dark/2560×1440 — mainWidthRatio 0.700, жёлоб 495px (LIVE-02)
- `shots/stats-light-1280x800.png` — stats/light/1280×800
- `shots/stats-light-1440x900.png` — stats/light/1440×900
- `shots/stats-light-1680x1050.png` — stats/light/1680×1050
- `shots/stats-light-1920x1080.png` — stats/light/1920×1080
- `shots/stats-light-2560x1440.png` — stats/light/2560×1440
- `shots/topics-dark-1280x800.png` — topics/dark/1280×800
- `shots/topics-dark-1440x900.png` — topics/dark/1440×900
- `shots/topics-dark-1680x1050.png` — topics/dark/1680×1050
- `shots/topics-dark-1920x1080.png` — topics/dark/1920×1080
- `shots/topics-dark-2560x1440.png` — topics/dark/2560×1440 — mainWidthRatio 0.700, жёлоб 495px (LIVE-02)
- `shots/topics-light-1280x800.png` — topics/light/1280×800
- `shots/topics-light-1440x900.png` — topics/light/1440×900
- `shots/topics-light-1680x1050.png` — topics/light/1680×1050
- `shots/topics-light-1920x1080.png` — topics/light/1920×1080
- `shots/topics-light-2560x1440.png` — topics/light/2560×1440

**Retina (2 снимка, `dpr=2`, 1440×900, тёмная тема):**

- `shots/home-dark-1440x900-dpr2.png` — home/dark/1440×900/dpr2
- `shots/stats-dark-1440x900-dpr2.png` — stats/dark/1440×900/dpr2

**Tier B (6 снимков; `summary` недостижим — см. раздел 2):**

- `shots/bookmarks-dark-1440x900.png` — bookmarks/dark/1440×900
- `shots/bookmarks-light-1440x900.png` — bookmarks/light/1440×900
- `shots/settings-dark-1440x900.png` — settings/dark/1440×900
- `shots/settings-light-1440x900.png` — settings/light/1440×900
- `shots/review-dark-1440x900.png` — review/dark/1440×900 — активный раздел «Тренировка» (WS-6)
- `shots/review-light-1440x900.png` — review/light/1440×900

**Блок E — до/после ответа (2 снимка, дополнительные к Tier B):**

- `shots/review-mode-before.png` — review/dark/1440×900 — «Ошибок к разбору: 2», ДО ответа
- `shots/review-mode-after.png` — review/dark/1440×900 — «Ошибок к разбору: 2», ПОСЛЕ заведомо неверного ответа (дельта 0, ожидаемо — вопрос уже был ошибочным)

Итого: 50 файлов (40 + 2 Retina + 6 Tier B + 2 Блок E) — совпадает с
`ls .sdlc/CS-20260828-003/shots/*.png`.
