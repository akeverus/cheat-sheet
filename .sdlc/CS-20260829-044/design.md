# CS-20260829-044: Forensic-аудит продукта перед редизайном

## Контекст

Миссия `missions/cheat-sheet-visual-redesign.md` §4 требует ПЕРЕД правкой
production-кода собрать внутреннюю модель фронтенда по восьми рубрикам: ЧТО ЕСТЬ,
ЧТО ХОРОШО, ЧТО ПЛОХО, ЧТО СЛУЧАЙНО, ЧТО ФУНДАМЕНТАЛЬНО, ЧТО УСТАРЕЛО, ЧТО
ПЕРЕУСЛОЖНЕНО, ЧЕГО НЕ ХВАТАЕТ (§4:233–240). §5 перечисляет 18 экранов и
запрещает вывод «если Home красивый — остальные тоже». §6 перечисляет 8 режимов и
запрещает считать их одной композицией. Задача — документ; production-код не
трогать (`origin_note`).

### Инвентарь фронтенда (замерено в этом worktree, ветка `sdlc/CS-20260829-044`)

| Область | Кол-во | Где |
|---|---|---|
| Шаблоны-страницы | 10 | `modules/quiz-app/src/main/resources/templates/*.html` |
| Фрагменты | 16 | `templates/fragments/*.html` |
| CSS-файлы | 35 (10 755 строк) | `static/css/*.css` |
| JS-модули | 43 (5 756 строк) | `static/js/**/*.js` |
| UI-контракт-тесты | 74 | `modules/quiz-app/src/test/**/*ContractTest.java` |
| Playwright-спеки | 7 | `e2e/*.spec.js` |
| Визуальные голдены | 28 = 14×2 движка | `e2e/visual-baselines/{chromium,webkit}` |
| Досье CSS-решений | 36 | `docs/css-decisions/*.md` |

Крупнейшие CSS: `pages-focus.css` 2205, `shell.css` 1084, `pages-stats.css` 1062,
`pages-settings.css` 733, `pages-summary.css` 472.

### Как устроена подача стилей

`css-bundle-manifest.txt` (`modules/quiz-app/src/main/resources/`) — единственный
источник правды порядка каскада; его читают `CssBundleService` (склеивает
`/css/bundle/{page}.{hash}.css`) и `TemplateFragmentContractTest`. Шапка отдаёт
один `<link>` (`templates/fragments/head.html:333`). Гейты строк: `*` либо список
значений `page` из восьми: `focus, result, settings, stats, bookmarks, summary,
error, login` (манифест, строки 12–13). Порядок слоёв объявлен первой строкой
`tokens.css`: `tokens, reset, shell, components, pages, utilities`; многие файлы
объявляют больше одного слоя, поэтому строки манифеста непереставляемы.

Оси персонализации ставятся до первого кадра инлайн-скриптом
`fragments/head.html`: `data-theme` (:119), `data-motion` (:153–154),
`data-density` (:246–247), `data-reading-width` / `screenPreset` (:260). Пресеты
экрана: `phone → density=compact`, `laptop → без атрибутов`, `wide →
reading-width=wide` (`e2e/visual-matrix.spec.js:85–89`).

### Карта 18 экранов миссии → маршрут → шаблон

`InterviewMvcController` = `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/controller/InterviewMvcController.java`.

| № | Экран §5 | Маршрут | Шаблон / `page` |
|---|---|---|---|
| 1 | Home | `GET /` (:45) | `focus-training.html`, `page='focus'` |
| 2 | Training | `GET /training` (:82) → 302 на `/` | тот же (легаси-алиас) |
| 3 | Topics | `GET /topics` (:375) | `topics.html`, `page='topics'` |
| 4 | Analytics | `GET /stats` (:342) | `stats.html` |
| 5 | Bookmarks | `GET /bookmarks` (:383) | `bookmarks.html` |
| 6 | Settings | `GET /settings` (:123) | `settings.html` |
| 7 | Session Summary | `GET /session-summary` (:286) | `session-summary.html` |
| 8 | Login | `GET /login` (`LoginController.java:35`) | `login.html` |
| 9 | Error | Spring error-mapping | `error.html` |
| 10 | Result | `POST /answer` (:291) | `result.html` |
| 11 | Focus | тот же `/` | `focus-training.html` (2205 строк CSS) |
| 12–18 | Typed Recall, Interview, Code, Study, Flashcard, Exam, Marathon | `POST /start` (:215) + `GET /` | `focus-training.html` в разных состояниях |

Вне списка §5, но существуют и обязаны попасть в аудит: `GET /train` (:188 →
`train.html`, конструктор сессии) и `GET /review` (:94, разбор ошибок).

### 8 режимов

`InterviewMode` (`modules/quiz-domain/.../domain/InterviewMode.java`): `TRAINING`,
`EXAM`, `MARATHON`, `STUDY`, `FLASHCARD`, `TYPED_RECALL`, `INTERVIEW`,
`CODE_CHALLENGE`. Предикаты уже кодируют различие рабочих мест: `isRecallMode()`,
`usesTypedInput()`, `usesCodeEditor()`, `usesSpokenRehearsal()`, `isUnlimited()`.
Композицию режимов стережёт `ModeWorkspaceContractTest`,
`RecallCompositionContractTest`.

### Свежая смена направления (главный факт контекста)

- `b56bf4c6` (2026-08-29 19:45) `design(folio): новое направление — «графит и
  чернила» вместо гильдейского pixel-art`;
- `265f3630` — состояния и полировка после сноса pixel-слоя;
- `82aa5406` — новые голдены FOLIO, 14×2, матрица theme×preset×motion.

Следствия, проверенные здесь: файлов `static/css/pixel-*.css` больше нет; ось
«пиксельность» снята из визуальной матрицы (`e2e/visual-matrix.spec.js:12–14`);
при этом в `docs/css-decisions/` лежат 8 досье `pixel-*.md` (animations, brand,
buttons, cards, fx, motion-parity, nav-badges, options), описывающих снесённый
слой. Это готовый кандидат в рубрику «ЧТО УСТАРЕЛО».

### Предшествующая разведка (не дублировать — надстроить)

`.sdlc/CS-20260828-003/` — задача «Закрыть белые пятна §18 фронт-разведки живым
наблюдением»: `report.md` (41 КБ), `shots/` (50 PNG), `data/` (`space-metrics.json`,
`pixel-layer-scan.json`, `cascade-*.json`, `hex-inventory.tsv`), `recon/`
(`shots.js`, `space.js`, `cascade.js`, `hex.sh`, `db.sh`). Последний коммит той
задачи — `798c9a66` в 01:56 того же дня, то есть на ~18 часов РАНЬШЕ FOLIO-сброса:
все её снимки и весь pixel-анализ описывают уже несуществующий дизайн. Скрипты
переиспользуемы (`recon/shots.js:1–5` — запуск `E2E_BASE_URL=http://localhost:8091
node ...` из каталога `e2e`, чтобы резолвился playwright), покрытие — 8 экранов
(`home, train, stats, topics, bookmarks, settings, review, summary`), ни одного
режима, ни `login`, ни `error`, ни `result`.

### Покрытие голденами (замер)

`e2e/visual-baselines/chromium` — 14 снимков ровно четырёх экранов: `home` (7),
`train` (2), `settings` (2), `stats` (3). Остальные 14 экранов из §5 и все 8
режимов пикселями не застрахованы.

### Ограничения

- Кап задачи — 40 файлов (хартия §5).
- Порт 8080 может держать bootRun пользователя; поднимать только `SERVER_PORT=8091`
  (хартия §6). Нужен `docker compose up -d postgres`.
- `e2e/node_modules` отсутствует — перед прогоном нужен `npm ci` в `e2e/`
  (`e2e/scripts/run-visual-goldens.sh:32–35` делает это сам).
- Профиль задачи `trivial`, DoD-гейт единственный: `test -s
  .sdlc/CS-20260829-044/report.md`.

## Решение

Аудит выполняется в два прохода и издаётся двумя файлами.

**Проход 1 — статическая форензика.** Сплошной обход инвентаря: 35 CSS, 43 JS, 26
шаблонов, `css-bundle-manifest.txt`, `tokens.css`, 74 контракт-теста, 7 e2e-спеков,
36 досье решений, ключевые `docs/*.md` фронтенда. Каждое утверждение аудита
сопровождается якорем `path:line` от корня репозитория, именем теста или именем
PNG. Утверждения без якоря запрещены.

**Проход 2 — живое наблюдение.** Приложение поднимается из worktree на
`SERVER_PORT=8091` (`docker compose up -d postgres` + `./gradlew bootRun
--no-daemon`), затем скрипт `.sdlc/CS-20260829-044/recon/shots.js` (адаптация
одноимённого скрипта CS-20260828-003) снимает матрицу и складывает числа мёртвого
пространства в `.sdlc/CS-20260829-044/data/space-metrics.json`. Матрица снимков:

- 18 обязательных кадров, тёмная тема, 1440×900: 10 не-workspace экранов
  (`train`, `topics`, `stats`, `bookmarks`, `settings`, `summary`, `login`,
  `error`, `result`, `review`) + 8 состояний workspace по одному на каждое
  значение `InterviewMode`;
- 4 кадра ultrawide 2560×1440 (`workspace-training`, `stats`, `summary`, `topics`)
  — доказательная база §17 о пустом пространстве;
- 4 кадра светлой темы 1440×900 (`workspace-training`, `stats`, `settings`,
  `summary`).

Итого 26 PNG в `.sdlc/CS-20260829-044/shots/` + машинный индекс `shots-index.json`.
Детерминизм home-кадра — тем же пином `questionId`, что в
`e2e/visual-matrix.spec.js:31–36`.

**Издание.** Основной документ — `docs/redesign/01-forensic-audit.md` (новый
каталог `docs/redesign/`), восемь разделов ровно по рубрикам §4 плюс служебные
«Метод и ограничения», «Инвентарь», «Карта экранов», «Карта режимов», «Тестовое
покрытие и его дыры», «Мёртвое пространство (ultrawide)». Гейт-файл
`.sdlc/CS-20260829-044/report.md` — короткая выжимка (≤120 строк): 10–15 главных
находок с якорями и ссылка на основной документ; он не дублирует его целиком,
чтобы два файла не разъехались.

**Рубрики наполняются с разной оптикой, а не одним списком:** «случайно» — это
решения без записанной причины (нет строки в `docs/css-decisions/`, нет
контракт-теста, нет комментария), «фундаментально» — то, на чём держится каскад и
что редизайн обязан сохранить или сознательно снести, «устарело» — то, что
описывает несуществующий код (кандидаты уже найдены: 8 `pixel-*.md`, весь
`.sdlc/CS-20260828-003/report.md`), «переусложнено» — стоимость поддержки выше
пользы, с числом строк, «не хватает» — сверка 18 экранов × 8 режимов против
покрытия голденами и против композиционных требований §6.

Отвергнутые альтернативы:
- Только статический аудит без живого рендера — не даёт §17-замера пустоты и
  честной картины режимов; миссия §41 требует доказательства наблюдением.
- Скриншоты всех 18 экранов в обеих темах и пяти вьюпортах (180 PNG) — упирается в
  кап 40 файлов; выбрана минимальная матрица, закрывающая §5, §6 и §17.
- Положить полный аудит в `report.md`, а в `docs/` — ссылку: `origin_note` прямо
  называет `docs/redesign/01-forensic-audit.md` местом результата.
- Переиспользовать снимки CS-20260828-003 — они сделаны до FOLIO-сброса и
  показывают снесённый pixel-слой.

## Acceptance Criteria

Каждый пункт — с командой приёмки; команды выполняются из корня worktree.

1. Созданы оба файла: `test -s docs/redesign/01-forensic-audit.md && test -s
   .sdlc/CS-20260829-044/report.md`.
2. Основной документ содержателен, выжимка коротка: `[ $(wc -l <
   docs/redesign/01-forensic-audit.md) -ge 600 ]` и `[ $(wc -l <
   .sdlc/CS-20260829-044/report.md) -le 120 ]`; выжимка ссылается на основной
   документ: `grep -c 'docs/redesign/01-forensic-audit.md'
   .sdlc/CS-20260829-044/report.md` ≥ 1.
3. Восемь рубрик §4 присутствуют заголовками второго уровня:
   `grep -cE '^## (ЧТО ЕСТЬ|ЧТО ХОРОШО|ЧТО ПЛОХО|ЧТО СЛУЧАЙНО|ЧТО ФУНДАМЕНТАЛЬНО|ЧТО УСТАРЕЛО|ЧТО ПЕРЕУСЛОЖНЕНО|ЧЕГО НЕ ХВАТАЕТ)'
   docs/redesign/01-forensic-audit.md` == 8.
4. Все 18 экранов §5 разобраны: в документе есть таблица «Карта экранов», и для
   каждого из 18 имён (Home, Training, Topics, Analytics, Bookmarks, Settings,
   Session Summary, Login, Error, Result, Focus, Typed Recall, Interview, Code
   Challenge, Study, Flashcard, Exam, Marathon) `grep -c` ≥ 1. Дополнительно
   упомянуты `/train` и `/review`.
5. Все 8 режимов разобраны отдельными подразделами с указанием композиции: для
   каждого значения `InterviewMode` из
   `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/InterviewMode.java`
   выполняется `grep -c '<VALUE>' docs/redesign/01-forensic-audit.md` ≥ 1, и
   `grep -cE '^### (TRAINING|EXAM|MARATHON|STUDY|FLASHCARD|TYPED_RECALL|INTERVIEW|CODE_CHALLENGE)\b'`
   == 8.
6. Полный инвентарь без пропусков — цикл по живому списку файлов не находит
   неупомянутых:
   `for f in $(ls modules/quiz-app/src/main/resources/static/css/*.css | xargs -n1 basename); do grep -q "$f" docs/redesign/01-forensic-audit.md || echo "MISS $f"; done` — пусто.
   То же для `static/js` (43 файла, сравнение по пути относительно `static/js/`) и
   для 26 шаблонов (`templates/*.html` + `templates/fragments/*.html`).
7. Якоря резолвятся: все токены вида `<путь>:<строка>` в документе указывают на
   существующий файл, в котором не меньше указанного числа строк. Проверка —
   скрипт `.sdlc/CS-20260829-044/recon/check-anchors.sh`, печатающий `0 broken`
   и завершающийся кодом 0; сам скрипт коммитится вместе с аудитом.
8. Каждая находка в рубриках ПЛОХО / СЛУЧАЙНО / УСТАРЕЛО / ПЕРЕУСЛОЖНЕНО / НЕ
   ХВАТАЕТ доказана: каждый буллет верхнего уровня внутри этих пяти разделов
   содержит хотя бы один якорь — `path:line`, имя `*ContractTest`, имя `*.spec.js`
   или имя PNG из `shots/`. Проверка — тот же `check-anchors.sh`, режим
   `--bullets`, печатает `0 unproven`.
9. Живая матрица снята: `[ $(ls .sdlc/CS-20260829-044/shots/*.png | wc -l) -ge 26 ]`,
   присутствуют 8 кадров с именами `workspace-<mode>-dark-1440x900.png` по числу
   значений `InterviewMode`, 10 кадров не-workspace экранов, 4 кадра `*-2560x1440.png`
   и 4 кадра `*-light-1440x900.png`; существует `shots/shots-index.json`, и
   каждое имя PNG встречается в основном документе.
10. Раздел «Тестовое покрытие и его дыры» перечисляет группы 74 контракт-тестов и
    явным списком называет экраны и режимы, НЕ покрытые голденами
    (`e2e/visual-baselines/chromium` = 14 снимков четырёх экранов: home, train,
    settings, stats). Проверка: `grep -c 'visual-baselines' ` ≥ 1 и наличие
    подраздела `### Не покрыто голденами` со списком ≥ 14 позиций.
11. Раздел «ЧТО УСТАРЕЛО» выносит явный вердикт по двум найденным кандидатам: по
    восьми `docs/css-decisions/pixel-*.md` (каждое имя файла упомянуто) и по
    `.sdlc/CS-20260828-003/report.md` с указанием причины (сделан до `b56bf4c6`).
12. Числа воспроизводимы: в разделе «Инвентарь» каждая строка с числом несёт
    колонку «команда проверки», и выполнение этих команд даёт ровно те числа,
    что напечатаны. Ревьюер выполняет их построчно; расхождений — 0.
13. Замер §17 приведён числами: раздел «Мёртвое пространство (ultrawide)»
    содержит для каждого из 4 ultrawide-кадров долю занятой ширины/площади,
    источник чисел — `.sdlc/CS-20260829-044/data/space-metrics.json`, файл
    существует и непуст.
14. Production-код не тронут: `git status --porcelain -uall` и `git diff --stat
    HEAD` не содержат путей вне `docs/redesign/` и `.sdlc/CS-20260829-044/`;
    `git diff --stat HEAD -- modules/ e2e/ scripts/` пуст.
15. Документ на русском: `grep -c '[А-Яа-я]' docs/redesign/01-forensic-audit.md`
    ≥ 500; заголовки рубрик — как в п.3.

## Тест-контракт

не применимо (артефакт-документ)

## Риски

- **Приложение не поднимется на 8091** (Docker/Testcontainers, занятый порт,
  gradle-wedge). Митигация: `docker compose up -d postgres` до `bootRun`,
  `--no-daemon`, лог в `.sdlc/CS-20260829-044/data/boot.log.txt` (так делала
  CS-20260828-003). Если после честной попытки приложение не стартует — это
  blocker к человеку, а не тихий пропуск AC 9/13: аудит без живого наблюдения не
  закрывает §5/§6/§17.
- **Кап 40 файлов.** 26 PNG + 2 документа + `shots-index.json` +
  `space-metrics.json` + `boot.log.txt` + 2 скрипта ≈ 33. Запас мал — не добавлять
  вьюпорты и темы сверх матрицы решения.
- **Снимки режимов требуют прохода по флоу** (`POST /start` с `_csrf`,
  `/study-confirm`, `/flashcard-reveal`, `/finish`). Митигация: снимать через
  Playwright-браузер кликами по конструктору `/train`, а не голым HTTP;
  `session-summary` доступен только после `POST /finish` (durable-урок проекта).
- **Соблазн переписать выводы CS-20260828-003.** Её числа устарели по дизайну, но
  метод и скрипты — нет. Переиспользовать `recon/*.js`, выводы — перепроверить
  заново на живом FOLIO.
- **Соблазн начать редизайн.** §4 прямо запрещает («Не начинай сразу писать CSS»);
  гипотеза нового дизайна — предмет следующей задачи, не этой. Любая правка вне
  `docs/redesign/` и `.sdlc/` отвергается харнесом.
- **Раздутый документ вместо честного.** Рубрики «ХОРОШО» и «ФУНДАМЕНТАЛЬНО»
  обязаны быть непустыми: фронтенд зрелый (74 контракт-теста, манифест каскада,
  36 досье решений), и аудит, объявляющий всё плохим, — недостоверен.

## Follow-ups

- Сформировать design hypothesis по §7–§9 (три направления, выбор одного, палитра
  и типографика FOLIO) на основе `docs/redesign/01-forensic-audit.md` — отдельный
  документ `docs/redesign/02-design-hypothesis.md`.
- Расширить визуальную матрицу `e2e/visual-matrix.spec.js` с 4 экранов до всех 18
  экранов и 8 режимов из карты аудита, обновить голдены обоих движков.
- Вычистить устаревшие досье `docs/css-decisions/pixel-*.md` (8 файлов) с
  переносом ещё живых уроков каскада в досье действующих файлов.
