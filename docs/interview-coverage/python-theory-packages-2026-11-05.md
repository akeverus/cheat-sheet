# Python: проверенные пакеты теории — 05.11.2026

Проверены 26 теоретических вопросов, связанные 31 MCQ-блок использованы
только для сверки. Это AUTHOR_VERIFIED и независимое peer review теории;
формальная MCQ FINAL-приёмка не заявляется. JSON, глобальные планы,
checkpoint и audit-state этим проходом не редактировались.

| Группа | Охват | Результат |
|---|---|---|
| Основы Python | Q1–Q12 | Сохранены и проверены прежние изменения, в Q12 добавлено обязательное совпадение minor-версии и типа сборки профайлера/target; для prerelease — точной версии. Finding закрыт повторным review. |
| Конкурентность | Q1–Q9 | Уточнены готовый Future и await, своя/внешняя отмена timeout, bounded workers с cleanup, ProcessPoolExecutor 3.14 и version-specific FT ABI. Новые примеры проверены. |
| Типизация | Q1–Q5 | Прежние изменения про 3.14 annotations и 3.15 TypedDict/TypeForm проверены, дополнительных правок теории не потребовалось. |

## Независимые отчёты

- [Основы Python](python-core-theory-review-2026-11-05.md).
- [Конкурентность](python-concurrency-theory-review-2026-11-05.md).
- [Типизация](python-typing-theory-review-2026-11-05.md).

Reviewers использовали явно выбранную дешёвую модель gpt-6-luna. Они прочитали
все вопросы своих тем и точные первичные источники, отдельно указали ограничения
и хеши снимков. Это peer review теории с открытой связанной MCQ-поверхностью,
не слепая Phase A/B и не импорт formal acceptance.

## Практическая проверка

Python 3.14.4, обычная сборка, Py_GIL_DISABLED=0 и sys._is_gil_enabled()=True.

- Дословный run_jobs из Q7: 100 работ, не более четырёх активных обработчиков,
  очередь не больше 16; успех, ошибка worker и внешняя отмена; после выхода
  не остаётся дочерних задач. В нормальном случае все task_done сбалансированы.
- Подтверждены готовый Future без переключения, eager_start=True, продолжение
  соседней задачи после ошибки gather и неэффективность позднего gather.cancel,
  TaskGroup cleanup, timeout conversion и сохранение внешнего CancelledError.
- Дословный spawn-пример Q8 запущен отдельным импортируемым .py: [5, 25].
- Из основ исполнены примеры Q1/Q2/Q4/Q5/Q6/Q7/Q8/Q9: копии, defaults,
  замыкания, исчерпание генератора, закрытие файла, default_factory и явно
  переданный общий список. Проверены примеры типизации Q1–Q3 и базовый TypedDict.
- API и синтаксис Python 3.15 не исполнялись: frozendict, sentinel, lazy import,
  TypeForm, новые параметры TypedDict, sampling profiler и abi3t сверены
  по документации/PEP. Поддержка конкретным type checker не тестировалась.

Скрипты проверки находятся вне репозитория:
`/tmp/python-theory-review-20261105/check_examples.py`,
`check_core_and_cancel.py`, `process_pool_example.py`.
Их выводы действительно получены, а не выведены из чтения исходного кода.

verify-mcq-json: PASS для трёх пар, 12/15 + 9/10 + 5/6 Q/блоков.
Frontmatter, последовательные номера Q, отсутствие inline MCQ, относительные
ссылки и адресный diff --check проверены. У Core Q3 общий заголовок шире B0:
B0 покрывает list/tuple/set/dict, а B1 — расширенный вопрос с frozendict.
Это намеренное разделение блоков; заголовок JSON автоматически не менялся.

## Точные оставшиеся вопросы MCQ

- Concurrency Q9:B1, C.text: перечислены abi3t/abi3.abi3t, но общий вопрос
  о бинарных зависимостях допускает и version-specific cp315-cp315t. Нужно
  убрать впечатление, будто Stable ABI — единственный допустимый вариант.
  Текст и ключ этим проходом не редактировались.
- Concurrency Q3:B0, A.edge_cases: можно дополнить отсутствием обязательного
  yield у await готового Future; это дополнительная граница, не смена ключа.
- Concurrency Q5:B0, D.example/edge_cases: рекомендуются явные условия
  преобразования только собственной отмены timeout.
- Concurrency Q7:B0, A.example/edge_cases: при синхронизации можно сослаться
  на fixed workers и учёт попытки task_done, не принимая его за бизнес-успех.
- Concurrency Q8:B0, B.edge_cases: можно добавить Future и смену default
  start method с 3.14; пример в теории использует явный spawn.
- Core Q12:B0, B.edge_cases: добавить совместимость версии/сборки attach;
  новая оговорка не меняет правильный выбор sampling profiler.
- Typing Q1:B0, C.edge_cases: различие eager/stringized/deferred annotations
  и форматов annotationlib — необязательное расширение; противоречия нет.

Точные поля сверены с текущим seed и перечислены как очередь владельцу MCQ.
Ни один из этих пунктов не закрывает независимую MCQ-приёмку всей темы.

## Снимки

- `python-interview`: MD `76c4767753adb78e5419b7ec65f18781d83833736a8ffca168f19e07509a5775`; seed-reference `5491249650c63aa9ab36897919309aba2b075e0c9573ce4a6f89a45c376fc09c`.
- `python-concurrency-interview`: MD `7d3f58ac939b8b14feee1092a0ce2a59cff83265bdf24e154ac5ae7b085db02c`; seed-reference `da41c2fd527fe92ade759ed1bd25ef0f662310b815f0588ad86924497e5161dc`.
- `python-typing-interview`: MD `565b9470427c422e97e49aefa233d63700830dab3183a945686e30dd030e3dc0`; seed-reference `1759a98865c9a650c92bfa4ebd2157522849fc1cf965b87da01ff07300b4bf04`.
