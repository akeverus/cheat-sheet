# METRICS_GLOSSARY — единые формулы метрик

Единый источник истины для всех чисел, показываемых пользователю (экран вопроса, разбор, итоги
сессии, аналитика). Закрывает UI-006 (`docs/backlog.csv`): одна метрика — одна формула на всех
экранах. Реализация формул централизована в
`LearningMetricsService.compute()`
(`modules/quiz-app/.../feature/interview/usecase/stats/LearningMetricsService.java`) и доменной записи
`LearningMetrics` (`modules/quiz-domain/.../domain/LearningMetrics.java`). Любой новый экран берёт
значения оттуда, а не пересчитывает своим SQL.

## Базовые сущности

| Термин | Определение |
|---|---|
| **Карточка (card)** | Вопрос банка (`questions`). Идентичность — `question_id`. |
| **Ревизия (revision)** | Версия контента карточки (`question_revision`). Попытка ссылается на конкретную ревизию. |
| **Попытка (attempt)** | Один ответ пользователя (`attempt`): `CORRECT` / `WRONG` / `UNKNOWN` / `SKIP`. Хранит `response_time_ms`, `memory_grade`, `idempotency_key`. |
| **Ошибка (mistake)** | Попытка со статусом `WRONG` или `UNKNOWN` (не `SKIP`). |
| **Освоение (mastery)** | Карточка освоена, когда `review_state.repetitions ≥ app.interview.learnedRepetitions`. |
| **Повторение (review)** | Не-первая попытка по карточке (используется в retention). |
| **Сессия (session)** | Прохождение в режиме `TRAINING/EXAM/MARATHON/STUDY/FLASHCARD`. |

## Правило «нет данных»

Любая доля с пустым знаменателем возвращает `LearningMetrics.NO_DATA` (= `-1.0`). UI обязан
показывать **«—»**, а не «0%». Хелперы: `hasFirstAttemptAccuracy()`, `hasOverallAccuracy()`,
`hasRetention7d()`, `hasRetention30d()`, `hasAverageResponse()`.

## Формулы (0..100 %, знаменатель у каждой свой)

| Метрика | Числитель | Знаменатель | Примечание |
|---|---|---|---|
| **firstAttemptAccuracy** — точность первой попытки | верные первые попытки | все первые попытки | честная память; главный показатель. |
| **overallAccuracy** — общая точность | `attemptsCorrect` | `attemptsCorrect + attemptsIncorrect` (non-SKIP) | все попытки, кроме пропусков. |
| **retention7d / retention30d** — удержание | верные повторения в окне | все повторения (не-первые) в окне 7 / 30 дней | доля успешных повторов. |

## Счётчики (без знаменателя)

| Метрика | Смысл |
|---|---|
| `cardsTotal` | всего карточек в банке. |
| `cardsSeen` | карточек с ≥1 попыткой. |
| `cardsMastered` | карточек с `repetitions ≥ порога`. |
| `attemptsTotal` / `attemptsCorrect` / `attemptsIncorrect` | суммы попыток (incorrect = WRONG+UNKNOWN). |
| `dueNow` | карточек готовы к повторению сейчас (`next_review_at ≤ now`). |
| `overdue` | из них просрочены ≥ суток. |
| `averageResponseMs` | среднее время ответа по попыткам с непустым `response_time_ms`. |

## Серия и рекорд (streak)

| Метрика | Источник | Формула |
|---|---|---|
| **Серия (streak)** | `DailyStreakService` / `daily_activity` | число подряд идущих дней с активностью, оканчивающихся сегодня/вчера. Реальные данные. |
| **Рекорд (best streak)** | `user_experience.best_streak` (миграция V19) | максимум `streak` за всю историю; обновляется при каждом росте серии. |

## Опыт (XP) — детерминированный вывод из реальных попыток

XP не выдумывается: это переименованная агрегация реальных попыток, начисляется в
`ExperienceService.award(attempt)` при записи попытки. Хранится в `user_experience` (V19).

| Величина | Формула |
|---|---|
| **XP за попытку** | `CORRECT` → `BASE_XP × difficultyWeight`; `WRONG/UNKNOWN` → `PARTIAL_XP` (награда за попытку); `SKIP` → `0`. |
| **difficultyWeight** | `EASY = 1.0`, `MEDIUM = 1.5`, `HARD = 2.0` (из `Question.difficulty`). |
| **BASE_XP / PARTIAL_XP** | `BASE_XP = 10`, `PARTIAL_XP = 2` (константы `ExperienceService`). |
| **Всего XP** | сумма XP всех попыток пользователя (`user_experience.xp`). |
| **Уровень (level)** | `level = floor( sqrt(totalXp / 100) ) + 1` — сублинейный рост; порог уровня `L` = `100 × (L−1)²` XP. |
| **XP за сессию** | сумма XP попыток внутри сессии — показывается в итогах и `+N XP` в баннере разбора. |

Значения `BASE_XP`, `PARTIAL_XP`, весов и формулы уровня зафиксированы здесь и в константах
`ExperienceService`; менять — только синхронно в обоих местах.

## Сложность (для доната аналитики)

`Question.difficulty` — статический enum `EASY / MEDIUM / HARD` (задаётся при импорте, дефолт
`MEDIUM`). Донат «Сложность вопросов» строит распределение банка по этому полю
(`GROUP BY difficulty`). Не путать с производной `QuestionDifficulty` (`NEW/EASY/MEDIUM/HARD` из
статистики точности) — она для адаптивного подбора, не для доната.

## Следующий интервал повторения

После оценки памяти пользователю показывается `nextReviewAt` / `intervalDays` из `ReviewState`
(SM-2: `SpacedRepetitionService`; FSRS: `FsrsService`). Отдаётся в ответе сабмита через `ReviewDTO`.
Закрывает UI-013 (memory grade виден и влияет на расписание).

## Content-block типы (фиксация, Этап 0)

Типизированные блоки контента вопроса (`ContentBlock` + `BlockType`): `MARKDOWN`, `CODE`, `MERMAID`,
`IMAGE`, `CALLOUT`, `TABLE`. Тип приходит с backend (`data-block-type`), фронт не угадывает его по
содержимому. Реально присутствуют в данных: `MARKDOWN` (`answerMarkdown`), `CODE` (`codeSnippet`+lang),
`MERMAID` (`diagramMermaid`). `IMAGE/CALLOUT/TABLE` — модель готова, рендерятся при появлении данных.
