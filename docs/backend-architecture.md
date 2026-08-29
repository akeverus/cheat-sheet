# Архитектура бэкенда

## 0. Как читать этот документ

Якоря `path:line` проверены на `3efc7883456de898464f65cb829ba20c9e0e7f9a`; перепроверка —
`python3 scripts/check-arch-anchors.py`. Каждый следующий коммит в `modules/` может
сдвинуть номера строк внутри файла — валидатор ловит вышедший за пределы файла якорь,
но не сдвиг внутри того же файла; это осознанный размен на простоту.

Пути записаны сокращённо, префикс перед двоеточием разворачивается по таблице:

| Префикс | Разворачивается в |
|---|---|
| `app:` | `modules/quiz-app/src/main/java/com/cheatsheet/quiz/` |
| `dom:` | `modules/quiz-domain/src/main/java/com/cheatsheet/quiz/domain/` |
| `per:` | `modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/` |
| `mig:` | `modules/quiz-persistence/src/main/resources/db/migration/` |
| `res:` | `modules/quiz-app/src/main/resources/` |
| `tst:` | `modules/quiz-app/src/test/java/com/cheatsheet/quiz/` |

Пример: якорь `app:feature/interview/service/review/ReviewService.java:32` разворачивается
в путь `modules/quiz-app/src/main/java/com/cheatsheet/quiz/feature/interview/service/review/ReviewService.java`, строка 32.

Числа в документе бывают двух категорий. **Состояние на HEAD** (53 файла в
`quiz-domain`, `V1…V29`, 9818 вопросов) — обязано сходиться с командой из соседнего
кода прямо сейчас; такое число без якоря или без сопровождающей команды — дефект
документа. **Исторические замеры** (1231 вопрос без диаграммы до бэкфилла, 79
дублей код/схема, `↻1292 переразобрано`) — факты о произошедшем однажды событии,
их не пересчитывают заново; такие числа помечены «на момент правки» и не участвуют
в проверке актуальности.

## 1. Модули

Три Gradle-модуля с односторонними зависимостями:

```
quiz-domain  ←  quiz-persistence  ←  quiz-app
```

- **`quiz-domain`** — 53 файла (51 без `package-info`), чистая модель без Spring и без
  инфраструктуры. Ключевые типы: `Question` (`dom:Question.java:31`),
  `InterviewQuestion`, `InterviewSession`, `InterviewFilter`, `ReviewState`
  (`dom:ReviewState.java:23`), `FsrsState` (`dom:FsrsState.java:24`), `Attempt`
  (`dom:Attempt.java:27`), `Bookmark` (`dom:Bookmark.java:18`), `TopicStats`
  (`dom:TopicStats.java:25`), перечисления (`InterviewMode`, `Difficulty`,
  `QuestionType`, `FlashcardGrade`, `ReviewReason`, …). Почти всё — record'ы. Здесь же
  `RussianPlural` — склонение числительных для UI-копирайта. С прошлой ревизии
  добавились три семейства: прогрессия (`Achievement` `dom:Achievement.java:24`,
  `GuildRank` `dom:GuildRank.java:27`, `Progression` `dom:Progression.java:26`,
  `UserExperience` `dom:UserExperience.java:18`, `StreakInfo`
  `dom:StreakInfo.java:10`, `DailyProgress` `dom:DailyProgress.java:10`), сессия
  (`SessionRecord` `dom:SessionRecord.java:27`, `PausedSessionInfo`
  `dom:PausedSessionInfo.java:19`, `StudyPhase` `dom:StudyPhase.java:11`,
  `FlashcardPhase` `dom:FlashcardPhase.java:12`), экспорт и связи
  (`ProgressExportRow` `dom:ProgressExportRow.java:19`, `RelatedQuestion`
  `dom:RelatedQuestion.java:15`, `QuestionIssueStatus` `dom:QuestionIssueStatus.java:7`).
- **`quiz-persistence`** — 16 файлов `*Repository*` (15 JDBC-реализаций + интерфейс
  `FullTextSearchRepository` `per:FullTextSearchRepository.java:12`) из 21 всего +
  Flyway (`db/migration/`, `V1…V29`). PostgreSQL only; H2/встроенных БД нет.
  Полнотекстовый поиск — нативный PostgreSQL FTS (`PostgresFullTextSearchRepository`
  `per:PostgresFullTextSearchRepository.java:23`, `FtsQueryUtils`
  `per:FtsQueryUtils.java:12`).
- **`quiz-app`** — Spring Boot: контроллеры, сервисы, Thymeleaf, конфигурация.

Границы **проверяются тестом**, а не соглашением:
`LayeredArchitectureTest` (`tst:architecture/LayeredArchitectureTest.java:18`, ArchUnit)
запрещает `domain → api/service/persistence/config`, `service → controller/security`,
`persistence → api`.

## 2. Пакеты `quiz-app`

Вертикальные срезы (`feature/`) вместо слоёв по типу — контроллер, DTO, маппер и
сервисы фичи лежат рядом:

```
feature/interview/   controller · dto · mapper · service · usecase   (ядро продукта)
feature/export/      выгрузка прогресса
feature/admin/       административные операции
feature/question/    engine/ — рендер и метаданные вопроса
api/                 dto · mapper · security — общая обвязка REST
common/              constants · exception · util · web
config/              app · web
service/             cross-cutting: cache · diagram · imports · strategy · event
infrastructure/      bootstrap · health · render · search
```

Разделение `service/` и `usecase/` внутри `feature/interview` содержательное:
`service/` — доменная механика (планировщик, сессии, страницы), `usecase/` — тонкие
оркестраторы под конкретный HTTP-вызов (`AnswerApiService`
`app:feature/interview/usecase/AnswerApiService.java:31`, `NextQuestionApiService`
`app:feature/interview/usecase/NextQuestionApiService.java:23`, `FavoriteApiService`
`app:feature/interview/usecase/FavoriteApiService.java:17`, `BookmarkApiService`
`app:feature/interview/usecase/BookmarkApiService.java:25`, `ConfidenceApiService`
`app:feature/interview/usecase/ConfidenceApiService.java:17`, `IssueReportApiService`
`app:feature/interview/usecase/IssueReportApiService.java:19`). Контроллер не
содержит логики — он вызывает use-case.

Ключевые сервисы `feature/interview/service/`:

| Пакет | Класс | Ответственность |
|---|---|---|
| `core` | `InterviewService` (`app:feature/interview/service/core/InterviewService.java:54`) | Центральный оркестратор выдачи вопроса и записи ответа |
| `core` | `OptionLookupService` (`app:feature/interview/service/core/OptionLookupService.java:27`) | Варианты MCQ из seed-JSON |
| `flow` | `SessionFlowService` (`app:feature/interview/service/flow/SessionFlowService.java:29`), `PauseService` (`app:feature/interview/service/flow/PauseService.java:37`), `SessionSummaryService` (`app:feature/interview/service/flow/SessionSummaryService.java:36`) | Жизненный цикл сессии |
| `review` | `ReviewService` (`app:feature/interview/service/review/ReviewService.java:32`), `SpacedRepetitionService` (`app:feature/interview/service/review/SpacedRepetitionService.java:36`), `FsrsService` (`app:feature/interview/service/review/FsrsService.java:30`), `AttemptRecorder` (`app:feature/interview/service/review/AttemptRecorder.java:28`) | Интервальное повторение |
| `page` | `FocusTrainingPageService` (`app:feature/interview/service/page/FocusTrainingPageService.java:49`), `StatsPageService` (`app:feature/interview/service/page/StatsPageService.java:47`), `AnswerPageService` (`app:feature/interview/service/page/AnswerPageService.java:32`), `BookmarksPageService` (`app:feature/interview/service/page/BookmarksPageService.java:34`) | Сборка состояния экрана |
| `progress` | `ExperienceService` (`app:feature/interview/service/progress/ExperienceService.java:34`), `DailyStreakService` (`app:feature/interview/service/progress/DailyStreakService.java:25`), `BookmarkService` (`app:feature/interview/service/progress/BookmarkService.java:26`), `FavoriteService` (`app:feature/interview/service/progress/FavoriteService.java:23`) | Прогресс и метки |
| `topic` | `TopicCatalogService` (`app:feature/interview/service/topic/TopicCatalogService.java:35`) | Каталог и гуманизация имён тем |
| `copy` | `AnswerCopy` (`app:feature/interview/service/copy/AnswerCopy.java:19`) | Текст разбора: вердикт, срок повторения, буквы вариантов |

`AnswerCopy` — единственный автор формулировок разбора. Разбор рисуют два рендерера
(SSR `result.html` и `quiz/interview.js`), и пока каждый собирал текст сам, у одного
события было две редакции. Теперь MVC-модель получает `verdictText` / `nextReviewText`
через `MvcModelAttributeMapper` (`app:api/mapper/view/MvcModelAttributeMapper.java:29`),
а `AnswerResponse` (`app:feature/interview/dto/response/answer/AnswerResponse.java:22`)
несёт те же поля в JSON — клиенты только печатают. Строки — единственное, что здесь
считается: буквы A/B/C выводятся из позиции варианта в списке, срок — из `intervalDays`
обновлённого `ReviewState`.

**Куда ведёт «следующий шаг» — свойство домена, а не шаблона.** `SessionSummaryService`
собирает короткий план из `SessionSummary.NextAction`, и назначение каждого пункта несёт
сам `NextActionType` (`dom:NextActionType.java:16`; вложенный `Destination`:
`TRAINING` / `MISTAKES` / `TOPIC`). Раньше ссылку выбирал шаблон, сравнивая имя
константы со строкой, — из-за чего вопрос «сколько на экране *разных* назначений»
нельзя было задать ни в сервисе, ни в тесте: `REVIEW_DUE` и `KEEP_GOING` вели в одно
место и показывались как два выбора. Теперь сервис подавляет `KEEP_GOING`, когда долг
повторений уже дал `REVIEW_DUE`, а шаблон только печатает. Держит
`SummaryRoutingContractTest` (`tst:ui/SummaryRoutingContractTest.java:43`).

Точность и длительность сводки тоже считает домен, а не представление: знаменатель
точности — число **отвеченных** вопросов (не запланированных), а `getAccuracyRounded()` /
`getCompactDuration()` дают формы, гарантированно влезающие в кольцо и KPI-тайл
(см. `design-system.md` §5, «бюджет содержимого»).

## 3. Планирование повторений

Два алгоритма, переключаются `app.scheduling.algorithm` (`sm2` | `fsrs`, env
`SCHEDULING_ALGORITHM`). По умолчанию — **SM-2**.

FSRS-4.5 не «второй режим», а миграционный путь: при первом ответе на вопрос с
накопленной SM-2-историей `ReviewService.applyWithFsrs` сидирует память из неё
(`interval → stability`, `easeFactor → difficulty`) и дальше пишет только FSRS-колонки.
SM-2-колонки при этом не затираются, поэтому откат флага не теряет прогресс. Обратное
решение (жёсткая миграция колонок) сделало бы переключатель односторонним.

## 3a. Отсутствие прогресса — состояние, а не ошибка

Банк вопросов и прогресс по ним — две независимые сущности. Строка в `review_state`
означает «на этот вопрос отвечали», её отсутствие — «вопрос новый». Никакой другой
смысл на неё не навешивается.

Правила, которые это удерживают:

| Правило | Где | Что ломалось без него |
|---|---|---|
| Выборка вопросов идёт **LEFT JOIN** к `review_state` | `InterviewFilterSql.appendReviewStateJoin` | INNER JOIN делал строку прогресса условием видимости: потеря таблицы прятала весь банк без единой ошибки |
| Скоуп владельца — в `ON`, не в `WHERE` | там же | `WHERE rs.owner_key = ?` отбрасывает NULL и молча возвращает INNER JOIN |
| «Готов к показу» — один текст на все запросы | `InterviewFilterSql.READY_FOR_REVIEW` | счётчик на главной и набор сессии считали разное |
| Сортировки оборачивают `rs.*` в `COALESCE` | `QuestionRepository.appendReviewPriorityOrder`, `appendSessionPickOrder` | `NULL` уезжает в конец `ORDER BY` и в ветку `ELSE` у `CASE` — новые вопросы становились недостижимы |
| Запись прогресса — **upsert** | `ReviewStateRepository.update`, `updateFsrs` | строку заводил показ вопроса, поэтому «посмотрел» и «ответил» были неразличимы; а после того как показ писать перестал, голый `UPDATE` стал терять первый ответ — ноль затронутых строк, без исключения |
| Статистика считает от `questions` | `QuestionStatsRepository` | сводка показывала размер прогресса вместо размера банка («3 из 6 к повтору» на 9818 вопросах), а каталог тем на `/stats` пустел до «нет данных» |

Показ вопроса в БД не пишет ничего. Импорт тоже не заводит строку прогресса — её
создаёт первый ответ, и только он.

Эти два правила — «показ не пишет» и «запись через upsert» — держатся **только вместе**.
Порознь любое из них ломает противоположный конец: без upsert'а первый ответ уходит в
ноль строк, без «показ не пишет» просмотренный вопрос числится просроченным повторением.
Строки, записанные старым поведением, снимает миграция `V28` — у них нулевые счётчики,
пустая FSRS-память и `last_result = 'NEW'`, то есть информации в них нет, а на счётчик
`overdue` (он без `COALESCE`, см. ниже) они влияли.

Именно поэтому `updateFsrs` не перечисляет колонки SM-2: при вставке их проставляет
схема, при конфликте они остаются нетронутыми. Планировщик, который переключили, не
должен стирать память планировщика, который работал до него.

Исключение, где INNER JOIN осмыслен: метрики, которые по определению существуют
только при наличии ответов — точность темы, слабые темы, разбор ошибок, прогноз
расписания, экспорт прогресса. Там `overdue` тоже намеренно без `COALESCE`:
просрочить можно только назначенный срок, а у нового вопроса срока не было.

### 3b. Очередь разделена на повторения и новое

`READY_FOR_REVIEW` даёт **одно** число — «что можно показать сейчас», и в него по
построению входят вопросы без расписания. Как счётчик выборки это верно, как подпись
в интерфейсе — нет: на пустом прогрессе весь банк попадает в очередь, и «9818 к
повтору» обещает повторить то, чего пользователь не видел.

Поэтому рядом с `due` считается `fresh` (`rs.question_id IS NULL`) — тем же запросом,
той же выборкой:

| Слой | Что несёт |
|---|---|
| SQL | `due` (готово к показу) и `fresh` (из них без расписания) — соседние агрегаты одного `SELECT` |
| `LearningMetrics` | `dueNow`, `dueFresh`, производное `dueRepeats()` |
| `InterviewStats` | `due`, `fresh`, производное `dueRepeats()` |
| `InterviewStatsResponse` / `static/js/quiz/stats.js` | оба слагаемых; разность считается на клиенте, чтобы части не разъехались во времени |
| Шаблоны | «к повтору» — только `dueRepeats()`, «новых» / «ещё не открывали» — только `fresh` |

Само `due` не тронуто: подбор вопросов сессии опирается на него, и сузить его до
повторений значило бы сделать новые вопросы недостижимыми.

**`cardsSeen` не заменяет `dueFresh`.** Первое — факт журнала попыток
(`AttemptRecorder.record()` идёт отдельной транзакцией и умеет выйти рано по
idempotency-ключу), второе — факт расписания. Разделять очередь можно только
счётчиком из той же таблицы, что и сама очередь, иначе разность уходит в минус.

### 3c. «Слабая тема» — одно определение на подсказку и на тренировку

Карточка «Тренировать слабые темы» на `/stats` и режим `weakTopics` — это обещание и
его исполнение. Обещание («слабее всего — Транзакции БД, 31%») даёт ссылку с
`topic=`, а `WeakTopicsSelectionStrategy`, получив чужую тему, молча уходит в
стратегию по умолчанию, чтобы не подсовывать вопрос из непрошеной темы. Значит,
разойдясь в вердикте, эти двое дают экран, который обещал одно, а тренирует другое,
— и никакой ошибки при этом не происходит.

Поэтому определение ровно одно: `QuestionStatsRepository.findWeakestTopic(threshold)`.
Оно возвращает тему **вместе с точностью** — иначе процент для подписи пришлось бы
считать повторно, второй формулой. Порог общий, `QuizConstants.WEAK_TOPIC_THRESHOLD`.

Тай-брейк детерминированный: точность → больше попыток → имя темы. Без него СУБД
вправе вернуть любую из равных строк, и подпись карточки менялась бы от перезагрузки
к перезагрузке, а ссылка вела бы то в одну тему, то в другую.

Счётчик на карточке — `countWeakTopics(threshold)`, число тем ниже порога. Соседние
карточки показывают объём работы (долг по повторениям, ошибки в истории), и общее
число тем в аналитике рядом с ними читалось бы как такой же backlog, которым оно не
является.

**Числитель и знаменатель — из одной выборки.** Экран тренировки несёт две статистики:
`SurfaceState.stats` (глобальные `LearningMetrics` для KPI сайдбара, единые знаменатели
на всех страницах) и `SurfaceState.filteredStats` (та же структура в границах текущего
фильтра, для виджетов «Сегодня»). Пока во второй роли ездило одно число, подпись
читалась «4 из 9818» — числитель по фильтру, знаменатель по всей базе.

### 3d. Причина повторения: классификатор отвечает «какой повод», страница — «показывать ли»

`ReviewReasonService.classify` — чистая функция без БД: получает состояние SM-2,
FSRS-память, точность темы и признак избранного, возвращает `ReviewReason`
(`RECENT_LAPSE` → `OVERDUE` → `BOOKMARK` → `WEAK_SKILL` → `NEW`, в этом приоритете).
Данные ей передаёт вызывающий, `FocusTrainingPageService`.

Раньше в неё был вкручен флаг `examMode`, который первой же строкой возвращал причину
`EXAM` («Часть экзаменационной сессии»). Это ломало границу дважды. Во-первых, факт был
не про вопрос, а про сессию — одинаковый на всех её вопросах, то есть классификатор
возвращал не то, что классифицирует. Во-вторых, короткое замыкание глушило настоящие
поводы: на экзамене ни просрочка, ни лапс не вычислялись.

Теперь режим решает страница: при `InterviewMode.EXAM` `FocusTrainingPageService` не
зовёт классификатор вовсе. Это не только чистота слоёв — на экзамене причину всё равно
нельзя показывать (продуктовое обоснование в `product-redesign-state.md` §4), а вместе
с вызовом уходят три запроса на каждый вопрос: `findByQuestionId`, `findFsrsState`,
`getTopicAccuracy`. Контракт закреплён `FocusTrainingPageServiceTest`
`skipsReviewReasonEntirelyInExamMode` — проверяет и пустую причину, и `never()` на всех
трёх репозиториях: без второй половины тест прошёл бы и на варианте, который считает
и выбрасывает.

Тип `ReviewReason.Type.EXAM` удалён вместе с флагом, перегрузки `classify` схлопнуты
в одну (две из трёх существовали только ради тестов).

## 3b. Русская подпись перечисления: свойство константы или копирайт экрана

Интерфейс полностью русский, а перечисления — английские, и вопрос «где живёт подпись»
в проекте решён по-разному в разных местах. Разница не историческая, у неё есть критерий:

- **Свойство самой константы → `quiz-domain`.** `Difficulty.label()`,
  `ReviewReason.message`, `ReviewResult.label()`, `QuestionIssueCategory.label()`.
  «Неверно» — это и есть `WRONG`, «Опечатка» — это и есть `TYPO`, на любом экране;
  отдельного мнения у экрана тут быть не может.
- **Копирайт экрана → слой приложения (`common/util/ModeUtils`).** У режима подпись
  зависит от места: `STUDY` в конструкторе сессии — «Изучение», а на рабочем экране в
  бейдже фазы — «Проверка». Затащив её в домен, мы бы зафиксировали одну из двух.

Оба варианта одинаково уязвимы к одной ошибке — **второй копии**. Копию не видит ни
компилятор, ни ревью: разметка сравнивает `X.name() == 'ЛИТЕРАЛ'`, JS держит объект с
ключами по именам констант, шаблон пишет подпись своими словами. Переименование
константы такую ветку не ломает, а **гасит**: из списка пропадает режим, у карточки не
остаётся ни одной подписи, схема остаётся на экране, но перестаёт быть схемой. Один
такой разъезд уже доезжал до пользователя — «Интенсив» показывался как `MARATHON`
(`docs/ui-critique/round-01.md`, C22).

Отсюда два правила, и оба закреплены `EnumLabelSourceContractTest`:

1. **Ветвление — предикатом, а не именем.** `InterviewMode.isUnlimited()`,
   `usesTypedInput()`, `BlockType.rendersAsMermaid()`. Предикат переименование ловит
   компилятором; литерал — нет. Тест запрещает `.name() == '…'` во всех шаблонах.
2. **Список режимов собирает сервер.** `ModeUtils.options(selected, examCount,
   marathonCount)` отдаёт `ModeOption(name, label, startAction, defaultCount, unlimited,
   selected)` — и `<option>`, и подпись кнопки старта, и признак «без счётчика» едут из
   одного места (см. `docs/frontend-architecture.md`, «Дубли между JS и сервером»).
   `defaultCount` при этом **выводится** из `AppProperties` по тому же правилу, что и
   `SessionFlowService.resolveSessionTotal` (`EXAM || usesTypedInput()` → `examCount`,
   иначе `marathonCount`), а не проставляется в разметке руками: рукописные значения
   этому правилу уже противоречили.

Гейт на «подписи только в `ModeUtils`» намеренно **пороговый** (≥4 режима из 8 в одном
файле), а не «ни одного вхождения». Подписи — обычные русские слова: «Тренировка» —
пункт навигации, «Интенсив» — часть «Интенсивности arcade-эффектов», «Изучение» — фаза
обучения. Ловится не слово, а **таблица**: прозе не нужны четыре режима сразу.

### Снисходительный разбор прячет копию вместо того, чтобы её ронять

`QuestionIssueCategory.fromString` устроен намеренно мягко: `null`, пустая строка и
неизвестное имя дают `OTHER`, чтобы жалоба пользователя не потерялась из-за расхождения
фронта и бэкенда. Полезное свойство и **худший из возможных фонов для второй копии**:
переименование константы не уронит запрос и не оставит следа в логе — пользователь
выберет «Опечатка», а на проверку уедет «Другое». Обычная копия отказывает громко, эта —
**переклассифицирует молча**, и заметить это можно только по статистике жалоб.

Поэтому список категорий не размножается ни в одну сторону:

- в тексте ошибки `ValidIssueCategory` перечень **собирается из `values()`**, а не
  выписан прозой: рукописный список — та же копия, просто в человеческом виде, и после
  добавления категории сообщение начинает врать клиенту API;
- подпись живёт в `label()` — по критерию выше это свойство константы, а не копирайт
  экрана;
- клиенту список приходит **разметкой** (`reportCategories` → инертный
  `<template id="report-category-options">`), а не таблицей в JS, и правило
  «неизвестное/пустое → `OTHER`» остаётся только на сервере — при невыбранной категории
  клиент шлёт `null`, а не свою догадку. Приземлено (`7530fcb8` + `ca63ef4b`); границу
  держит `ReportCategorySourceContractTest`.

Соседний `ReviewResult.fromString` ведёт себя противоположно — на мусоре бросает.
Разница осознанная и закреплена тестами (`QuestionIssueCategoryTest` против
`ReviewResultTest`): расписание повторов на неверном результате портит данные, жалоба
на неизвестной категории — нет, а вот потеря жалобы стоит дороже.

## 4. Обработка ошибок

`GlobalExceptionHandler` — единая точка. Ключевые решения:

**Развилка API vs MVC.** `isApiRequest` возвращает true для `/api/**` и `/export/**`,
а для остальных путей — только если клиент просит JSON **и не готов принять HTML**.
Проверка «есть ли `application/json` в Accept» была бы неверной: браузерная навигация
шлёт `text/html,…,*/*`, и любой прокси, дописывающий JSON, показал бы пользователю
голый JSON вместо страницы ошибки.

Сама развилка живёт в `common/web/RequestFormat` — четыре статических предиката на двух
ортогональных осях: **путь** (`isMachineFacingPath` / `isAdminSensitivePath`) и
**заголовок `Accept`** (`acceptsHtml` / `prefersHtml` / `prefersJson`). До этого одна и
та же логика существовала в четырёх копиях — в хендлере, в `AccessDeniedHandler`, в
`AuthenticationEntryPoint` и в сервисе доступа, — и копии разошлись: один и тот же
запрос мог получить HTML от одного места и JSON от другого.

**Формат ошибки — RFC 7807 `ProblemDetail`** с `Content-Type: application/problem+json`
и тремя расширениями: `errorCode` (машиночитаемый, из `ApiErrorTypes`), `correlationId`
(проставляется `CorrelationIdFilter`, связывает ответ с логом) и `errors` (детали
валидации).

**Явные маппинги вместо generic-500.** Отдельно обрабатываются
`NoResourceFoundException` → 404 (без него опечатка в URL давала 500 и ложную тревогу
в мониторинге) и `ResponseStatusException` → его собственный статус (без него штатный
403 от гейта админ-эндпоинтов превращался в 500 с `log.error`). `/favicon.ico` гасится
в 204, чтобы не шуметь в логах.

**Наружу не утекает внутренний текст.** `IllegalStateException` отдаёт фиксированную
формулировку — её сообщение содержит имена бинов, состояние сессии, фрагменты SQL.
Диагностика остаётся в логе, связь — через `correlationId`. `safeMessage` схлопывает
переводы строк (защита от log injection) и режет до 300 символов.

`RateLimitExceededException` дополнительно отдаёт `Retry-After` — без него клиент не
знает, когда осмысленно повторить.

## 5. Безопасность

Два режима, `app.security.mode` (`app:config/app/AppProperties.java:203`, дефолт
`"local"`):

- **`local`** (по умолчанию) — однопользовательский, без аутентификации.
- **`multi`** — учётки в памяти (HTTP Basic / форма) плюс опциональный OAuth2/OIDC,
  собирает `MultiUserSecurityConfig` (`app:config/web/MultiUserSecurityConfig.java:25`),
  сам называющий себя в javadoc «skeleton multi-user identity» — режим не рассчитан на
  реальную многопользовательскую эксплуатацию без доработки. Owner-скоупинг общий для
  обоих способов входа: `OwnerContextFilter` кладёт ключ владельца в `OwnerContext`
  (`per:OwnerContext.java:9`), репозитории фильтруют по нему. Режим выключен и живой
  проверкой не покрыт.

`SecurityModeStartupGuard` и `OAuthSecurityStartupGuard` валят приложение на старте
при несогласованной конфигурации — молчаливый старт в полуоткрытом состоянии опаснее
отказа.

Чувствительные эндпоинты (админ, экспорт) закрыты `SensitiveEndpointAccessService`
с собственными `AccessDeniedHandler` / `AuthenticationEntryPoint` — чтобы отказ был
ProblemDetail'ом, а не редиректом на форму логина в API-контексте. Фильтры отрабатывают
**до** MVC, поэтому `@ControllerAdvice` их не покрывает: контракт ответа воспроизводится
ровно один раз, в `SensitiveEndpointAccessService.writeProblem` (тот же
`application/problem+json`, тот же `errorCode`, тот же `correlationId`).

Токен читается из заголовка `X-Admin-Token`. Из **query string** он не принимается
вовсе: `HttpServletRequest.getParameter` не различает строку запроса и тело формы, то
есть любая защита «по HTTP-методу» вокруг него — иллюзия, а секрет в URL оседает в
access-логах, `Referer` и истории браузера (CWE-598). Проверяется разбором самой
`getQueryString()`, а не методом запроса; из тела формы токен по-прежнему принимается.

CSP с nonce: `CspNonceFilter` генерирует nonce на запрос, `CspNonceAdvice` кладёт его
в модель. `unsafe-inline` не используется ни для скриптов, ни для стилей.

`RequestRateLimiter` ограничивает частоту обращений к дорогим эндпоинтам.

## 6. Данные и источник вопросов

- Вопросы импортируются из markdown (`service/imports/`) в PostgreSQL.
- Варианты MCQ — **только** из seed-JSON `resources/seed/mcq/<category>/<topic>.json`,
  загружаются на старте. Вопрос без сидера автоматически вырождается во флешкарту.
- AI-пути **нет вовсе** — это свойство кода, а не конфигурации. Ни `app.ai.*`, ни
  `AI_FALLBACK_ENABLED` в проекте не существует; включить генерацию флагом нельзя.
  `OptionLookupService` ходит только в БД/кэш: нет сидера — пустой список, и MVC
  форсит `flashcardMode`.
- `SeedCoverageHealthIndicator` выводит покрытие сидерами в `/actuator/health`,
  `SeedInfoContributor` — в `/actuator/info`.
- Кэш вариантов — Caffeine (`service/cache/`).

Вместе с AI ушла и его телеметрия. Колонка `questions.regen_count` считала перезагрузки
вариантов генератором; после вырезания AI её единственный писатель исчез, метод
`QuestionRepository.incrementRegenCount` остался без вызывающих, и колонка навсегда
застыла на нуле. Этот ноль не был безобидным: `findTopicStats` агрегировал его
`SUM(q.regen_count)`, домен нёс его полем `TopicStats.regenSum`, а публичный
`GET /api/topic-stats` печатал `"regenSum": 0` в каждом из 320 объектов ответа (по
числу тем — см. §6a) — контракт обещал метрику, которой не существует. Вертикаль
удалена целиком: поле `Question.regenCount`, оба метода репозитория, поле домена, поле
DTO `TopicStatsResponse` (`app:feature/interview/dto/response/progress/TopicStatsResponse.java:17`),
столбец в SQL и сама колонка (`V27__drop_regen_count`,
`mig:V27__drop_regen_count.sql:1`).

Рендер markdown — `MarkdownRenderService` (flexmark) + `AnswerHtmlSplitter`, который
режет ответ на блоки для пошагового раскрытия.

После flexmark и jsoup-safelist фрагмент проходит один разбор в DOM (`postProcess`), и
в нём — три прохода подряд: санитайзинг ссылок/SVG/`<input>`, нормализация лестницы
заголовков к `h2` и простановка `aria-label` на GFM-чекбоксы (оба последних — WCAG
1.3.1 / 4.1.2, подробности в `docs/accessibility.md` §1). Проходов три, а `Jsoup.parse`
один: разбор HTML — самая дорогая часть рендера, и делать его по разу на правило значит
платить трижды за одно и то же дерево.

### 6a. Название темы приходит из файла темы, а не из slug-а

В БД тема хранится как путь (`databases/cockroachdb-interview`), и UI когда-то
восстанавливал из него человеческое имя: резал суффиксы, менял дефисы на пробелы,
поднимал первую букву, а поверх держал два хардкод-словаря исключений
(`DISPLAY_NAMES` на 24 записи и списки токенов «пиши заглавными» / «пиши
смешанным регистром»). Реконструкция из slug-а невозможна в принципе: slug —
результат необратимого упрощения, из `cockroachdb` не выводится `CockroachDB`
никакими правилами. Словари покрывали 5 тем из 320 — и то мимо: половина их
ключей (`acid`, `collections`, `gc`, `streams`) вообще не соответствовала ни
одной существующей теме. Остальные 315 показывались как «Cockroachdb»,
«Archunit», «Clickhouse», «Argocd», «Apache airflow», «AWS lambda».

Правильное имя уже есть — в самом файле темы, в его frontmatter
(`title: "<раздел>: <название>"`). Импорт теперь его и читает:

- `TopicTitleReader` (`app:service/imports/TopicTitleReader.java:26`) берёт `title` из
  ведущего frontmatter-блока (чтение обрывается на закрывающем `---`, файлы весят
  десятки килобайт) и отбрасывает префикс раздела до первого двоеточия — раздел в UI
  задан навигацией.
- `QuestionImportService.importFile` на каждый файл кладёт пару «slug → название» в
  `TopicDisplayNames` (`app:common/util/TopicDisplayNames.java:23`).
- `TopicUtils.displayName` (тот самый `@topicUtils` из шаблонов) читает реестр. Тема не
  найдена — значит файл удалён, а вопросы ещё в БД: показываем slug с заглавной буквы.
  Догадки о «настоящем» имени не строим.

Реестр — in-memory (`ConcurrentHashMap`), без таблицы и миграции: банк вопросов
и так переимпортируется из тех же файлов на каждом старте (`StartupRunner`
`app:infrastructure/bootstrap/StartupRunner.java:32` → `importAll`), поэтому таблица
была бы вторым источником правды, который умеет
расходиться с первым. Новая тема получает корректное имя без единой правки Java.

### 6b. Хеш сторожит текст, но не парсер, который его разбирает

`upsertQuestion` сравнивает SHA-256 от «текст вопроса + сырой ответ» и на совпадении
раньше просто выходил. Логика верная ровно наполовину: часть колонок — не контент, а
его *прочтение*. `question_type`, `code_snippet` и `diagram_mermaid` целиком выводятся
из того же markdown кодом `MarkdownQuestionParser`. Меняется парсер — меняется и
правильное значение колонки, а хеш при этом тот же, потому что файл никто не трогал.

Это уже случилось. Когда парсер научился отличать ` ```mermaid ` от обычного блока
кода, диаграммы получил **1 вопрос из 1231**, у которых mermaid лежит прямо в ответе, —
единственный, чей текст кто-то потом отредактировал. Остальные 1230 отдавали вопрос
без схемы, хотя схема была в файле; вкладки «Пример кода / Схема» на фокусе не
открылись ни разу за всё время существования фичи. Отказ бесшумный: ошибки нет, есть
корректно отрендеренный вопрос, в котором просто нет блока.

Теперь ветка «текст не изменился» сверяет производные поля с тем, что парсер выдаёт
сейчас, и при расхождении переписывает **только их** — `QuestionRepository.updateDerivedContent`,
исход `REDERIVED` в счётчиках импорта. Отдельный метод вместо `update(Question)`
принципиален: `update` означает «текст изменился» и тянет сброс SM-2, удаление
вариантов и новую ревизию. Здесь пользователь ничего не терял, поэтому прогресс,
варианты и история не трогаются — это и закреплено тестом (`updateDerivedContent`
вызван, `update` / `reset` / `deleteByQuestionId` / `append` — нет).

Схема БД не меняется, миграции нет: банк переимпортируется на каждом старте, поэтому
починка сама доезжает до всех вопросов. Первый запуск после правки: `↻1292
переразобрано`, диаграмм 1 → 1231, вопросов с обоими материалами 0 → 604.

Тот же механизм с тех пор отработал ещё раз, уже без единой правки в нём самом:
`DiagramPaletteNormalizer` (§6c) поменял только выдачу парсера, а бэкфилл поехал сам —
`↻177 переразобрано` на ближайшем старте.

### 6c. Цвет диаграммы принадлежит теме, а не автору диаграммы

Диаграммы писались вручную в разное время и красились «на глаз»: 177 диаграмм, 510
операторов `style`, больше тридцати разных hex-значений, почти все — светлые пастельные
заливки в расчёте на дефолтную светлую тему Mermaid. Приложение рисует своей темой,
собранной из design-токенов и переключающейся вместе со светлой/тёмной, но
`style A fill:#e8f5e9` этой темы не знает — инлайн-стиль на узле бьёт тему. Итог: часть
корпуса выглядела радужной и не по палитре, а в тёмной теме светлая заливка оставалась
светлой под подписью, чей цвет пришёл из токенов.

Убрать цвет совсем было нельзя: красный/зелёный/жёлтый несут смысл («ошибка», «успех»,
«предупреждение»), обесцвечивание потеряло бы информацию. Поэтому
`DiagramPaletteNormalizer` переводит оттенок в один из шести семантических классов
(`dgmDanger`/`dgmWarning`/`dgmSuccess`/`dgmInfo`/`dgmAccent`/`dgmNeutral`) — hex → HSL,
насыщенность ниже 0.15 значит «серый», остальное разбирается по полосам оттенка.
Конкретные цвета подставляет фронт из живых токенов (`frontend-architecture.md`, §3).

Три решения, которые стоит знать при правках:

- трогаются только `style` и `classDef`. `#` в корпусе встречается и в подписи ребра
  (`-->|"#access_token"|`), и её замена сломала бы смысл;
- удаляя `classDef`, нормализатор запоминает его имя и перенацеливает ссылки
  `class A,B cycle` на семантический класс — иначе они указывали бы в пустоту;
- границы полос подобраны по фактическому корпусу, а не по круглым числам: `#e1f5fe`
  (hue ≈ 199) должен попасть в info, а бирюзовый `#4ecdc4` (hue ≈ 176) — в accent,
  поэтому граница стоит на 195°. Красно-оранжевая — на 15°, потому что коралловый `#f96`
  даёт ровно 20°, и на «круглой» границе его судьбу решала бы ошибка округления
  (что тест и поймал).

### 6d. «Пример кода» обязан быть кодом: схему от листинга отличает бэкенд

`MarkdownQuestionParser` кладёт в `code_snippet` любой не-mermaid блок ` ``` ` длиннее
порога. Авторы шпаргалок рисуют схемы прямо в таком блоке, поэтому поле хранит две
разные сущности. Интерфейс об этом не знал и подписывал всё подряд «Пример кода»: на
**79** вопросах этот «код» дословно повторял соседнюю mermaid-схему (две вкладки, одна
и та же топология), ещё на **151** был единственной схемой вопроса, спрятанной под
чужим ярлыком.

Разделяет `AsciiDiagramDetector`, результат назначает `ContentBlockMapper` — теперь
единственное место, где вообще решается тип и заголовок вкладки. Схема БД не меняется:
отдельная колонка `diagram_ascii` создала бы второе поле-диаграмму рядом с
`diagram_mermaid` (43 файла) и второй способ отвечать на один вопрос. Классификация —
свойство прочтения, а не хранения, и живёт в сборке представления.

Критерий калиброван по корпусу, а не придуман:

- признаком считается только псевдографика U+2500…U+25FF (рамки, блоки, фигуры). Таких
  блоков 185, и **все** до одного — рисунки;
- одиночная стрелка признаком **не** является. Это выглядит логично и неверно: в русских
  комментариях стрелка — обычная пунктуация («0 → NULL», «данные: канал → буфер»), и на
  корпусе такой критерий уводил в «Схему» 383 блока настоящего кода. Первая версия
  детектора этим и болела;
- зато строка, состоящая из стрелки целиком (`"    ↓"`), встречается только в
  нарисованной цепочке — 45 блоков, все схемы;
- обратный случай (код с нарисованной рамкой в комментарии) отсекает доля
  «синтаксических» строк ≥ 0.3: точка с запятой, скобки, XML-тег, ключевое слово. На
  185 подтверждённых рисунках этот guard не срабатывает ни разу.

`BlockType.ASCII_DIAGRAM` отделён от `MERMAID` не ради полноты перечисления: у него
другой рендер (моноширинный `<pre>` без подсветки — highlight.js раскрасил бы слова
внутри рисунка) и другой набор возможных действий на фронте.

## 7. HTTP-поверхность

MVC (возвращают HTML): `GET /`, `/review`, `/stats`, `/settings`, `/bookmarks`,
`/session-summary`, `/login`, `/topics`, `/train`, `/error`; `POST /answer`, `/start`,
`/finish`, `/pause`, `/pause/discard`, `/resume`, `/skip`, `/flashcard-reveal`,
`/flashcard-grade`, `/study-confirm`, `/bookmarks/note`, `/bookmarks/remove`,
`/question/note`, `/settings/reset-options`. `GET /training` — легаси-редирект на `/`
с переносом whitelisted query-параметров. Из более новых страниц: `/topics` —
каталог тем как самостоятельный адрес первого уровня (а не якорь внутри `/stats`),
`/train` — конструктор «Собрать сессию», вынесенный из вкладок `/settings`
(UXD-001), принимает те же параметры префилла, что раньше принимал `/settings`.

REST: `GET /api/next`, `/api/stats`, `/api/streak`, `/api/topic-stats`; `POST
/api/answer`, `/api/favorite`, `/api/confidence`, `/api/report`, а также три
конкретных пути закладок — `/api/bookmark`, `/api/bookmark/note`,
`/api/bookmark/remove` (в коде это три отдельных `@PostMapping`, не общий глоб).
Экспорт — `ExportController` держит класс-уровневый `@RequestMapping("/export")`; сама
выгрузка (`GET`, без литерального пути — `produces` перечисляет форматы) документируется
здесь прозой, а не проверяется валидатором как литерал.

Спецификация — springdoc, Swagger UI на `/swagger-ui.html` (отключён в профиле `prod`).
CSS-бандл (`/css/bundle/{page}.{hash}.css`) собирается из `CssBundleService.URL_PREFIX`
конкатенацией с именем файла — путь нелитеральный, валидатор C его не проверяет.

## 8. Идемпотентность и конкурентность

- Ключ идемпотентности `POST /api/answer` — поле тела `clientAttemptId`
  (`SubmitAnswerRequest` `app:api/dto/request/interview/SubmitAnswerRequest.java:25` →
  `ApiRequestMapper` `app:api/mapper/request/ApiRequestMapper.java:13` →
  `AttemptRecorder.normalizeKey`). Заголовка `Idempotency-Key` сервер не читает.
  Уникальный индекс `V22__attempt_idempotency_claim` не даёт двойной записи попытки
  при ретрае.
- `V23__attempt_first_attempt_unique` гарантирует единственность первой попытки —
  от неё считается статистика освоенности.
- Контент вопроса версионируется (`QuestionRevisionRepository`
  `per:QuestionRevisionRepository.java:26`); ответ на устаревшую ревизию отвергается
  `RevisionMismatchException` `app:feature/interview/service/review/RevisionMismatchException.java:7`
  → 409 с внятной формулировкой, а не молча засчитывается по новому тексту.

## 8a. История сессий

`session_history` (`V29__session_history.sql`,
`mig:V29__session_history.sql:12`) хранит **итоги** завершённых сессий, а не сами
ответы. До этой таблицы сводка жила только в `HttpSession`: экран `/session-summary`
читал её ровно один раз и терял при следующем старте, перезагрузке страницы или
закрытии вкладки — вопрос «сколько я занимался и как шёл» не имел ответа нигде, кроме
момента сразу после финиша.

Копировать подробности каждой попытки сюда — значило бы завести второй источник
правды рядом с `attempt` и `review_state`, которые их уже несут. Поэтому в таблице
только то, что нельзя восстановить из них: границы самой сессии — `owner_key`
(дефолт `'local'`, тот же приём, что и в `V24`), `finished_at` (epoch-секунды), `mode`,
счётчики `total_questions` / `correct_count` / `wrong_count` / `unknown_count`,
`duration_seconds` и `session_xp`. Индекс `(owner_key, finished_at DESC)` — под
единственный реальный запрос: «последние сессии владельца, свежие сверху».

Пишет `SessionHistoryService.record`
(`app:feature/interview/service/flow/SessionHistoryService.java:51`), вызывает его
`InterviewFlowMvcService`
(`app:feature/interview/usecase/mvc/InterviewFlowMvcService.java:276`) на завершении
сессии. Пустая сессия (ни одного ответа) не пишется — строка «0 из 0» в истории
выглядела бы занятием, которым не была. Ошибка записи не роняет завершение сессии:
человек только что закончил работу, и потеря строки в истории — меньшая беда, чем
500 вместо итогов. Читает `StatsPageService`
(`app:feature/interview/service/page/StatsPageService.java:174`) тем же
`SessionHistoryService.recent()`. `SessionHistoryService` — единственный посредник
между `quiz-app` и `SessionHistoryRepository`
(`per:SessionHistoryRepository.java:24`); домен новых типов под это не завёл —
записи истории читаются и пишутся как `SessionRecord`, уже входящий в семейство
«сессия» из §1.

## 9. Тесты

- Unit-тесты рядом с классами.
- Persistence — Testcontainers PostgreSQL 16-alpine, один контейнер на JVM, TRUNCATE
  между тестами (`AbstractPostgresRepositoryTest`).
- `quiz-app` — профиль `test`, `jdbc:tc:postgresql:16-alpine:///`.
- Архитектурные правила — `LayeredArchitectureTest`.
- Контракты шаблонов — `TemplateFragmentContractTest` (проверяет разметку фрагментов:
  landmarks, `aria-current`, единственность тоггла темы).

Запуск требует Docker. На OrbStack нужен обход рассинхронизации версий API:

```bash
JAVA_TOOL_OPTIONS=-Dapi.version=1.41 \
DOCKER_HOST=unix://$HOME/.orbstack/run/docker.sock \
./gradlew test --no-daemon
```
