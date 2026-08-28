# CS-20260828-006: Актуализировать `docs/backend-architecture.md` по дельте §18 Phase 0

## Контекст

**Что за документ.** `docs/backend-architecture.md` (541 строка) — прозаическое описание
бэкенда: модули, планировщик повторов, инварианты выборки, обработка ошибок, безопасность,
импорт, HTTP-поверхность, тесты. Ценность — не перечни, а зафиксированные *решения и их
цена* (почему LEFT JOIN, почему upsert, почему `single`-квери-токен запрещён). Это надо
сохранить: документ не сводится к таблице фактов.

**Почему протух.** Базовый коммит документа — `4556a627` (2026-08-06,
`git log -1 --format='%H %ad' --date=short -- docs/backend-architecture.md`). На HEAD
(`8e26bf3a`) в `modules/` с этой даты **421** коммит (`git log --oneline 4556a627..HEAD --
modules/ | wc -l`); без сид-каталога MCQ — **136**; только по `*.java` — **116**.

> Коррекция входа: отчёт Phase 0 (`.sdlc/CS-20260827-002/report.md:497` и Приложение A)
> называет **50** коммитов. Число не воспроизводится ни на его HEAD (`16f75d51`: 399 / 125),
> ни на текущем. Практическое следствие: **список §18 из 5 пунктов нельзя считать полным** —
> он получен точечной перепроверкой, а дрейф вчетверо больше. Дизайн требует сплошного
> прохода, а не применения пяти патчей.

**Ни одного якоря `path:line` в документе сейчас нет** — утверждения опираются на имена
классов в бэктиках. Проверка показала, что 103 из 146 «классоподобных» токенов документа
резолвятся в существующие `*.java`, остальные 43 — ключевые слова SQL, типы JDK/Spring и
константы перечислений. То есть база фактов в целом жива; отсутствует *механизм*, которым
это можно перепроверить после следующих 400 коммитов.

**Расхождения, подтверждённые на HEAD лично (командой, а не по отчёту):**

| # | Строка документа | Утверждение | Факт на HEAD | Команда |
|---|---|---|---|---|
| 1 | `:11` | `quiz-domain` — «46 файлов (не считая `package-info`)» | 53 файла, 51 без `package-info` | `find modules/quiz-domain/src/main -name '*.java' \| wc -l` |
| 2 | `:16` | «15 файлов `*Repository*` (14 JDBC + интерфейс) из 19 всего» | 16 файлов `*Repository*` (15 JDBC + интерфейс `FullTextSearchRepository`) из 21 | `ls modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/*Repository*.java \| wc -l` → 16 |
| 3 | `:17` | Flyway «V1…V26» | V1…V29 (29 файлов) | `ls modules/quiz-persistence/src/main/resources/db/migration \| wc -l` → 29 |
| 4 | `:314-319` | режимы `single` / `multi` | `local` / `multi`, дефолт `local` | `AppProperties.java:202-203`: `@Pattern(regexp = "local\|multi")`, `mode = "local"` |
| 5 | — | `session_history` / `SessionHistoryRepository` / `SessionHistoryService` не упомянуты нигде | есть на HEAD (V29) | `grep -rl SessionHistory modules --include='*.java' \| grep -v /test/` → 4 файла |
| 6 | `:501-509` | HTTP-поверхность | нет `/topics`, `/train`, `/pause/discard`, `/question/note`, `/bookmarks/note`; `/api/bookmark*` — глоб вместо трёх реальных путей | `grep -rhoE '@(Get\|Post)Mapping\("[^"]*"\)' modules/quiz-app/src/main/java` |
| 7 | `:384` | «5 тем из 319» | тем 320 | `find cheatsheets/interview -name '*.md' ! -name README.md \| wc -l` → 320 |
| 8 | `:361` | «~320 объектов ответа» | 320 | там же |

Пункт §18.4 отчёта («число репозиториев совпадает») — **ошибка отчёта**: он сравнил
`@Repository`-аннотации (15, действительно совпадает) с утверждением документа про *файлы*
`*Repository*` (16 на HEAD против 15 в тексте). Расхождение есть, дизайн его чинит.

**Проверенные и подтверждённые (менять нельзя, «исправление» будет регрессом):**
14 `@ExceptionHandler` в `GlobalExceptionHandler` (`grep -c` → 14); объём банка **9818**
вопросов — совпадает с числом заголовков `## Q<N>` в источнике импорта
(`grep -rhcE '^## Q[0-9]+' cheatsheets/interview --include='*.md' | paste -sd+ - | bc` → 9818);
«43 файла» про `diagram_mermaid` — ровно 43 в `modules/` без `graphify-out/`
(`git grep -l 'diagram_mermaid\|diagramMermaid' -- modules ':(exclude)modules/graphify-out'`);
`QuizConstants.WEAK_TOPIC_THRESHOLD` (`QuizConstants.java:12`); ArchUnit-правила; RFC 7807.

**Новая фича, которой нет в документе (V29).** `session_history` хранит **итоги** сессии,
а не ответы (`mig:V29__session_history.sql:1-13` — обоснование прямо в миграции: попытки
уже лежат в `attempt`/`review_state`, копировать их значило бы завести второй источник
правды). Колонки: `owner_key` (дефолт `'local'`, как V24), `finished_at` (epoch-сек),
`mode`, `total_questions`, `correct_count`, `wrong_count`, `unknown_count`,
`duration_seconds`, `session_xp`; индекс `(owner_key, finished_at DESC)`. Пишет
`InterviewFlowMvcService`, читает `StatsPageService`, посредник —
`SessionHistoryService` (`app:feature/interview/service/flow/`).

**Ограничения.** Живой запуск и Gradle не нужны и не используются: всё проверяемо
статически. `.pre-commit-config.yaml` и `.claude/**` — не трогать (хартия §6).

## Решение

Не «пять патчей по §18», а **сплошная актуализация с проверяемым аппаратом якорей**.
Три части, все в двух файлах.

### 1. Аппарат якорей (новое в документе)

В начало документа — раздел **«0. Как читать этот документ»**:

- **SHA-пин**: «Якоря `path:line` проверены на `<HEAD-SHA>`; перепроверка —
  `python3 scripts/check-arch-anchors.py`».
- **Таблица префиксов** (иначе 60-символьные пути убивают читаемость прозы):

  | Префикс | Разворачивается в |
  |---|---|
  | `app:` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/` |
  | `dom:` | `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/` |
  | `per:` | `modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/` |
  | `mig:` | `modules/quiz-persistence/src/main/resources/db/migration/` |
  | `res:` | `modules/quiz-app/src/main/resources/` |
  | `tst:` | `modules/quiz-app/src/test/java/com/cheatsheet/quiz/` |

- **Две категории чисел.** Абзац, отделяющий *состояние на HEAD* (53 файла, V29, 9818
  вопросов — обязаны сходиться с командой) от *исторических замеров* (1231 вопрос без
  схемы, 79 дублей, `↻1292 переразобрано` — факты о произошедшем событии, они не
  пересчитываются; такие числа сопровождаются пометкой «на момент правки»).

**Правило расстановки якорей:** якорь ставится при **первом в подразделе** упоминании
кодового артефакта (класс, метод, константа, колонка, миграция, свойство конфигурации).
Форма — `` `app:feature/interview/service/ReviewService.java:88` `` сразу после имени.
Прозу-обоснование («почему так») якорями не засыпать: она наследует якорь артефакта, о
котором говорит. Якорь класса — строка объявления; якорь метода допустим, когда
утверждение именно про метод.

Отвергнуто: якорь-блок в конце каждого подраздела (утверждение и его якорь расходятся
глазами); полные пути без префиксов (проза становится нечитаемой); анкоринг каждого
предложения (шум, и половина предложений — рассуждение, а не факт).

### 2. Валидатор `scripts/check-arch-anchors.py` (новый файл)

Python3, без зависимостей, конвенция репозитория (`scripts/*.py`, ср. `css-leak-audit.py`).
Аргумент — путь документа, дефолт `docs/backend-architecture.md`. Три проверки, exit 1 и
печать всех нарушений при любом провале:

- **A. Якоря.** Каждый `` `<prefix>:<path>:<line>` `` : префикс известен, файл существует,
  `1 ≤ line ≤ wc -l`.
- **B. Символы.** Каждый классоподобный токен в бэктиках (`[A-Z][A-Za-z0-9]{3,}`)
  резолвится в basename существующего `*.java` **или** лежит в явном allowlist внутри
  скрипта (ключевые слова SQL `SELECT`/`WHERE`/`CASE`/`COALESCE`/`ORDER`/`ELSE`/`NULL`/
  `UPDATE`, типы JDK/Spring `ProblemDetail`/`Jsoup`/`ConcurrentHashMap`/
  `HttpServletRequest`/`IllegalStateException`/`NoResourceFoundException`/
  `ResponseStatusException`/`AccessDeniedHandler`/`AuthenticationEntryPoint`, константы
  перечислений `OVERDUE`/`BOOKMARK`/`EXAM`/`MERMAID`/`WRONG`/`TYPO`/`OTHER`/`STUDY`/
  `MARATHON`/`TRAINING`/`MISTAKES`/`TOPIC`/`REDERIVED`, вложенные записи `SurfaceState`/
  `NextAction`, внешние имена `CockroachDB`/`Referer`/`Accept`). Allowlist — в коде,
  не в документе: новый неизвестный токен обязан падать громко.
- **C. HTTP-поверхность.** Каждый **строковый литерал** пути из
  `@GetMapping/@PostMapping/@PutMapping/@DeleteMapping/@RequestMapping` в
  `modules/quiz-app/src/main/java` встречается в документе дословно. Аннотации без
  литерала (`@GetMapping(produces = …)`, конкатенация `CssBundleService.URL_PREFIX + …`)
  пропускаются — они документируются прозой и под проверку не попадают.

Отвергнуто: сверка содержимого строки якоря с ожидаемым токеном (парсинг «какой символ
относится к якорю» хрупок, а выигрыш — только над сдвигом строк внутри того же файла);
обратная проверка «каждый путь из документа есть в коде» (документ законно упоминает
`/error`, `/actuator/health`, `/swagger-ui.html`, которые не `@*Mapping`).

### 3. Сплошная правка текста

Обязательный минимум — 8 расхождений из таблицы контекста плюс:

- **§1** — счётчики домена (53 / 51 без `package-info`) и репозиториев (16 файлов = 15 JDBC
  + интерфейс, из 21 в модуле), Flyway `V1…V29`. Перечисление доменных типов пометить
  «ключевые» и дополнить появившимися семействами: прогрессия (`Achievement`, `GuildRank`,
  `Progression`, `UserExperience`, `StreakInfo`, `DailyProgress`), сессия (`SessionRecord`,
  `PausedSessionInfo`, `StudyPhase`, `FlashcardPhase`), экспорт (`ProgressExportRow`),
  связи (`RelatedQuestion`), жалобы (`QuestionIssueStatus`).
- **§5** — `single` → `local` во всех вхождениях (гейт задачи требует **нуля** подстрок
  `single` во всём файле — проверить и слова вроде «single-user»), дефолт `local`,
  `multi` = in-memory basic/form + опциональный OAuth2, `MultiUserSecurityConfig`
  называет себя skeleton.
- **Новый §8a «История сессий»** — V29 по фактам выше: итоги, а не ответы; почему не
  копируем попытки; owner-скоуп как в V24; индекс под единственный шаблон чтения; кто
  пишет и кто читает. Раздел писать в стиле документа (решение + его цена), а не как
  дамп колонок.
- **§6/§12-факты** — V27/V28/V29 упомянуты все три (V27 и V28 в тексте уже есть).
- **§7** — литеральные пути выписать полностью, глоб `/api/bookmark*` раскрыть в
  `/api/bookmark`, `/api/bookmark/note`, `/api/bookmark/remove`; добавить `/topics`,
  `/train`, `/pause/discard`, `/question/note`, `/bookmarks/note`; `/export` — как
  class-level `@RequestMapping`; про CSS-бандл сказать прозой (путь нелитеральный).
- **Числа объёма** — 319 → 320 тем; «~320 объектов ответа» → 320; 9818 оставить и
  привязать к источнику (`## Q<N>` в `cheatsheets/interview/**`), а не к «базе».
- **Остальной текст** — пройти сплошняком: каждое утверждение либо получает якорь, либо
  переписывается под факт, либо помечается историческим замером.

Отвергнуто: переписывание документа с нуля (прозаические разборы §3a–§6d — самая ценная
его часть и на HEAD подтверждаются); откладывание анкеринга в follow-up (тогда DoD задачи
«якорь, существующий на HEAD» не выполняется вовсе).

## Acceptance Criteria

Все проверки — из корня worktree, каждая формулируется командой.

1. **Гейт задачи зелёный.** `! grep -q '46 файлов' docs/backend-architecture.md && ! grep -qE 'V1[….]+V26' docs/backend-architecture.md && ! grep -q 'single' docs/backend-architecture.md && grep -qi 'session_history' docs/backend-architecture.md && grep -q 'V29' docs/backend-architecture.md` → `exit 0`.
2. **Валидатор существует и проходит.** `python3 scripts/check-arch-anchors.py` → `exit 0`,
   печатает итог вида `anchors=N ok, symbols=M ok, mappings=K ok`, где `N ≥ 60`.
3. **Негативный контроль валидатора (red-proof).** Все три проверки обязаны реально падать:
   - `A`: временно дописать в документ `` `app:no/such/File.java:1` `` → скрипт `exit 1` с этим якорем в выводе;
   - `A`: временно заменить в любом якоре номер строки на `999999` → `exit 1`;
   - `B`: временно дописать `` `NoSuchClassName` `` → `exit 1`;
   - `C`: временно удалить из документа строку с `/api/streak` → `exit 1`.
   После каждого — откат правки, повторный прогон `exit 0`. Результаты (4 команды, их
   вывод и подтверждение отката) приложить в описании коммита или в
   `.sdlc/CS-20260828-006/verify.md`; рабочее дерево после проверки чистое, кроме
   целевых файлов.
4. **Счётчики документа = факту.** Каждая пара сходится:
   - `grep -c '53 файла' docs/backend-architecture.md` ≥ 1 и `find modules/quiz-domain/src/main -name '*.java' | wc -l` → 53;
   - документ говорит `51` без `package-info`, `find modules/quiz-domain/src/main -name '*.java' ! -name 'package-info.java' | wc -l` → 51;
   - документ говорит 16 файлов `*Repository*` / 15 JDBC / 21 в модуле; `ls modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/*Repository*.java | wc -l` → 16, `find modules/quiz-persistence/src/main -name '*.java' | wc -l` → 21;
   - `V1…V29` и `ls modules/quiz-persistence/src/main/resources/db/migration | wc -l` → 29.
5. **Все три поздние миграции названы.** `grep -c 'V27' … ≥ 1`, `grep -c 'V28' … ≥ 1`,
   `grep -c 'V29' … ≥ 1`.
6. **Режимы безопасности.** `grep -c '`local`' docs/backend-architecture.md` ≥ 1;
   `grep -n 'single' docs/backend-architecture.md` → пусто (совпадает с AC-1, включая
   вхождения внутри слов).
7. **Объём банка привязан к источнику.** Документ содержит `9818` и `320`;
   `grep -rhcE '^## Q[0-9]+' cheatsheets/interview --include='*.md' | paste -sd+ - | bc` → 9818,
   `find cheatsheets/interview -name '*.md' ! -name 'README.md' | wc -l` → 320;
   строка `319` в значении «число тем» отсутствует (`grep -n '319' docs/backend-architecture.md`
   — либо пусто, либо только исторический замер с пометкой).
8. **Раздел про историю сессий содержательный.** В документе есть заголовок с
   «История сессий», в нём упомянуты `session_history`, `SessionHistoryService`,
   `SessionHistoryRepository`, `V29`, `owner_key`, `finished_at`, и объём раздела
   ≥ 12 строк (`awk` по границам заголовков / `sed -n '/История сессий/,/^## /p' | wc -l`).
9. **Раздел «0. Как читать» на месте.** Документ содержит таблицу префиксов (все шесть:
   `app:`, `dom:`, `per:`, `mig:`, `res:`, `tst:`), строку с SHA-пином (40-символьный hex,
   равный `git rev-parse HEAD` на момент коммита) и абзац про «исторические замеры»
   (`grep -c 'на момент правки'` ≥ 1).
10. **Прозаическая ценность не утрачена.** `wc -l docs/backend-architecture.md` ≥ 541 —
    документ только дополнялся; удаление разборов §3a–§6d не допускается
    (`grep -c '^### 6[abcd]\.' ` → 4, `grep -c '^### 3[bcd]' ` → 3).
11. **Дельта задачи — ровно два файла.** `git status --porcelain -uall` показывает только
    `docs/backend-architecture.md` и `scripts/check-arch-anchors.py` (плюс, опционально,
    `.sdlc/CS-20260828-006/**`).

## Тест-контракт

не применимо (артефакт-документ)

## Риски

- **Гниение номеров строк.** Любой коммит в `modules/` сдвигает якоря. Смягчение: SHA-пин
  в §0 + валидатор, который ловит вышедший за пределы файла якорь; парный «guard» —
  негативный контроль AC-3, доказывающий, что валидатор действительно падает, а не
  печатает `ok` всегда. Полностью проблема не снимается: сдвиг внутри файла валидатор не
  видит — это осознанный размен на простоту (см. отвергнутые альтернативы).
- **Проверка B слишком строгая.** Незнакомый токен в бэктиках (новое имя JDK-типа) уронит
  валидатор в чужом коммите. Смягчение: allowlist в скрипте правится одной строкой,
  сообщение об ошибке подсказывает это явно. Скрипт **не** добавляется в pre-commit в
  рамках задачи (файл защищён хартией) — падать в чужих коммитах он пока не может.
- **Гейт `! grep -q 'single'` шире, чем кажется.** Подстрока ловится и внутри слов
  («single-user», «single source»). Реализатору проверять гейт целиком, а не только
  строку `:316`.
- **Неполнота сплошного прохода.** 116 java-коммитов с базы — вероятно, расхождений больше
  восьми найденных. AC покрывают найденные механически; остальное ловится глазами при
  проходе. Остаточный риск принимается: следующий такой прогон дешевле за счёт валидатора.
- **Параллельные агенты.** В `modules/` активно коммитят другие loop'ы (хартия §4). Между
  правкой и коммитом счётчики могут сдвинуться. Смягчение: перед коммитом перепрогнать
  AC-2/AC-4/AC-7 и, при расхождении, обновить числа и SHA-пин — не откатывать чужое.

## Follow-ups

- Актуализировать `docs/frontend-architecture.md` тем же анкерным протоколом (§0, префиксы, `scripts/check-arch-anchors.py <файл>`), с отдельным allowlist для JS/CSS-имён.
- Предложить пользователю включить `scripts/check-arch-anchors.py` в `.pre-commit-config.yaml` (файл защищён от автономных правок — нужен явный ответ) и снять якоря с гниения.
- Закрыть белые пятна §19 отчёта Phase 0 живым стендом на `SERVER_PORT=8091`: `EXPLAIN ANALYZE` горячих запросов `QuestionRepository`/`QuestionStatsRepository` и проход `app.security.mode=multi`.
