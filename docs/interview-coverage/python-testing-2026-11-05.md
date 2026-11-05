# Python testing: авторская проверка и подготовка коммита — 05.11.2026

Режим: `SEMANTIC_UPDATE`. Пакет ограничен Python testing; активный авторинг
MySQL Q2/Q3 и общий MCQ-audit не затронуты. Номера и заголовки Q1–Q7,
состав шпаргалок и инвентарь сохранены.

## Результат

В `python-testing-interview.md` Q6 добавлены два самостоятельных исполняемых
примера из стандартной библиотеки: различие вызова/ожидания `AsyncMock` и
проверка очистки после отмены задачи с синхронизацией через `Event`.
Проверка `assert_called_once_with` не обнаруживает забытый `await`;
`assert_awaited_once_with` проверяет ожидание, но не успешный результат
и не реальную работу внешнего сервиса. Тест отмены дожидается входа
в `try/finally`, затем завершения задачи и проверяет эффект очистки.

В production JSON добавлен Q6 B1 с четырьмя вариантами и объяснениями.
Семь существующих B0 сохранены без изменений этого запуска. Прежние
незакоммиченные уточнения Q1–Q7 в Markdown/JSON перечитаны и сохранены;
их основные факты сверены с указанными ниже первоисточниками.
Существовавший untracked fact-freshness sidecar дополнен записью Q6,
его статус остаётся `TODO`.

## Источники и границы

- Q1: [pytest и unittest.TestCase](https://docs.pytest.org/en/stable/how-to/unittest.html):
  ограничения параметризации и аргументов fixtures, `autouse`/`usefixtures`.
- Q2: [pytest fixture finalization](https://docs.pytest.org/en/stable/how-to/fixtures.html#teardown-cleanup-aka-fixture-finalization):
  конец scope, ошибка до `yield`, очистка уже подготовленных fixtures и finalizers.
- Q3: [pytest parametrization](https://docs.pytest.org/en/stable/how-to/parametrize.html):
  отдельные случаи, `id` и marks.
- Q4/Q5: [Python 3.15 mock](https://docs.python.org/3.15/library/unittest.mock.html#where-to-patch),
  [autospeccing](https://docs.python.org/3.15/library/unittest.mock.html#autospeccing)
  и [monkeypatch](https://docs.pytest.org/en/stable/how-to/monkeypatch.html):
  место поиска имени, сигнатура, динамические атрибуты и срок подмены.
- Q6: [Python 3.15 AsyncMock](https://docs.python.org/3.15/library/unittest.mock.html#unittest.mock.AsyncMock),
  [отмена asyncio-задач](https://docs.python.org/3.15/library/asyncio-task.html#task-cancellation),
  [IsolatedAsyncioTestCase](https://docs.python.org/3.15/library/unittest.html#unittest.IsolatedAsyncioTestCase)
  и [pytest-asyncio 1.4.1 modes](https://pytest-asyncio.readthedocs.io/en/v1.4.1/concepts.html).
- Q7: [SQLAlchemy 2.0: внешняя тестовая транзакция](https://docs.sqlalchemy.org/en/20/orm/session_transaction.html#joining-a-session-into-an-external-transaction-such-as-for-test-suites):
  ORM `commit()` внутри неё не обязательно фиксирует внешнюю транзакцию.

Просмотрен [pytest 9.2.0 changelog от 27.10.2026](https://docs.pytest.org/en/stable/changelog.html#pytest-9-2-0-2026-10-27):
нового относящегося к выбранному пробелу поведения не перенесено в теорию.
Точечный поиск свежих публикаций Baeldung не дал применимого материала
о Python AsyncMock. System Design Notes к этому API-сценарию не относится.
Примеры исполнены на локальном Python 3.14.4; документация сверена по 3.15,
сам Python 3.15 в этом запуске не исполнялся.

## Выполненные проверки

- `bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/programming-languages/python/python-testing-interview.json`
  — PASS: 7 Q / 8 блоков, semantic review `NOT_RUN`.
- `python3 scripts/mcq-answer-parity-gate.py modules/quiz-app/src/main/resources/seed/mcq/programming-languages/python/python-testing-interview.json`
  — PASS. Первоначальные сигналы длины и распределения ключей нового B1 исправлены.
- Оба Python-блока Q6 извлечены из Markdown и запущены через unittest
  с `-Werror`: 2 теста PASS. Контрольные изменения обнаружены:
  пропущенный `await` и удалённая очистка дали ожидаемые assertion failures.
- Frontmatter разобран Ruby Psych; стабильные Q1–Q7, оглавление и anchors,
  относительные ссылки, конечный `See also`, отсутствие inline MCQ — PASS.
- Сравнение с копией до правки: Q1–Q5/Q7 Markdown и все семь прежних MCQ B0
  сохранены, изменения JSON других вопросов отсутствуют.
- Адресный `git diff --check` — PASS; JSON sidecar разобран.

Автор перечитал Q6, все A/B/C/D и sections нового B1 с попыткой защитить
каждый неверный вариант. Независимые semantic/SFPL/ANSC/RU/blind-приёмка
и whole-file freshness не выполнялись; пакет не `FINAL` и не `AUDITED`.

## Передача для коммита

Предлагаемая отдельная группа — ровно четыре файла:

1. `cheatsheets/interview/programming-languages/python/python-testing-interview.md`
2. `modules/quiz-app/src/main/resources/seed/mcq/programming-languages/python/python-testing-interview.json`
3. `docs/mcq-quality/fact-freshness/python-testing-interview.json`
4. `docs/interview-coverage/python-testing-2026-11-05.md`

Эта группа включает прежние незакоммиченные изменения Python testing.
Предлагаемое сообщение: `Clarify Python testing isolation and async cleanup`.
Первоначально staging был отложен для координации с MCQ. После разрешения
координатора проверены HEAD `740d896b3`, пустой индекс и совпадение всех
четырёх файлов с сохранёнными хешами проверенного пакета. Группа разрешена
к адресному коммиту; остальные изменения в него не включаются.
Общие `PLAN_CHEATSHEETS.md` и `authoring-checkpoint.md` сейчас меняет другой
исполнитель; этот отдельный checkpoint оставлен для предотвращения коллизий.

Следующий независимый пакет: приёмка Python testing Q6 B1 по MCQ workflow.
Следующий авторский кандидат, если свободен: Q2 с минимальным исполняемым
примером finalizer при ошибке до `yield`, только после сравнения с текущим
ответом и соседними материалами о lifecycle тестовых ресурсов.
