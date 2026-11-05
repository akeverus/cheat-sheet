# Python Core — авторская редакция MCQ

Дата: 2026-11-05. Статус: `LLM_READY`, авторский проход с доступным ключом. Охват: все Q1–Q12, 15 блоков / 60 вариантов; прочитаны видимые вопросы, теория, A/B/C/D и все sections. Независимая приёмка и FINAL PASS не заявлены.

JSON рабочего дерева: `modules/quiz-app/src/main/resources/seed/mcq/programming-languages/python/python-interview.json`, SHA-256 `3972c01118f6d93212dee2b423770098bb9967801b486a88fd7ccb084aa743e6`.

Теория: `cheatsheets/interview/programming-languages/python/python-interview.md`, SHA-256 `76c4767753adb78e5419b7ec65f18781d83833736a8ffca168f19e07509a5775`. Теория не редактировалась.

## Содержание и сравнение альтернатив

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q1:B0 | is/== переставлены | присваивание принято за копию | tuple глубоко замораживает | идентичность, равенство и alias |
| Q2:B0 | shallow/deep переставлены | внешний контейнер и рекурсивная копия | присваивание копирует | deepcopy клонирует файлы/сокеты |
| Q3:B0 | set хранит повторы | изменяемость list/tuple переставлена | роли пяти контейнеров | dict ограничен строковыми ключами |
| Q3:B1 | внешняя неизменность и условный hash | hash при вложенном list | глубокая копия вложений | frozendict принят за dict subclass |
| Q4:B0 | равенство/hash и стабильность ключа | равным нужен разный hash | list подходит при обещании не менять | eq автоматически сохраняет identity hash |
| Q5:B0 | default при каждом вызове | default при первом вызове | аннотация различает пропуск/None | def-time и отдельный sentinel |
| Q5:B1 | None различает пропуск | общий sentinel и is | имя задаёт идентичность нового sentinel | перепутана truthiness None/MISSING |
| Q6:B0 | замыкание копирует значение | global/nonlocal переставлены | поиск имён и текущая переменная | присваивание модулю без global |
| Q7:B0 | iter/next и ленивый generator | тело generator исполнено при создании | iterable обязан иметь next | завершённый generator возобновляется |
| Q8:B0 | GC гарантирует закрытие на выходе | with/finally и передача исключения | except исполнится при любом выходе | with обязательно подавляет ошибку |
| Q9:B0 | frozen замораживает вложения | factory делит один list | dataclass проверяет типы | методы, factory и границы frozen |
| Q10:B0 | locale остаётся default | encoding=locale отключён | UTF-8 default и явное encoding | автоматическое преобразование файла |
| Q11:B0 | отложенный импорт при обращении | отложен только вызов функции | импорт запускается в фоне | регистрация до выполнения модуля |
| Q12:B0 | tracing attach точно считает вызовы | sampling attach и выборочные стеки | run принят за attach | dump принят за профиль интервала |
| Q12:B1 | frame pointers сами дают Python names | -X perf меняет флаги сборки | нативный стек и отдельный perf map | sampling attach требует perf map |

Q1/Q2/Q3/Q5/Q6/Q9 теперь сопоставляют одинаковый набор свойств, сохраняя в wrong конкретную ошибку. Исправлены повторяющиеся what/how и практические рекомендации. Q3:B0 question_text согласован с видимым заголовком о пяти контейнерах: frozendict (3.15+) присутствует во всех четырёх вариантах. Его внешняя неизменность не выдана за deep freeze. Это единственное изменение question_text относительно начала данного авторского прохода; ключи, block_idx, номера, порядок и остальные вопросы сохранены.

Q4 больше не называет всякий tuple хешируемым: пример использует хешируемые элементы. Q5:B1 wrong D уточнён, чтобы bool-проверка не оказалась защищаемым способом различить только две величины; явно неверно указана truthiness пропуска и None. Q7 различает создание generator и исполнение тела. Q8 не скрывает возможность подавления исходного исключения cleanup-кодом. Q10 сохраняет encoding=locale и возможность отключить UTF-8 mode. Во всех Q11 вариантах оговорена активная отложенная политика: eager override не делает wrong B правильным. Q12:B0/B1 повторно сверены с контекстом и оставлены без правки относительно предшествующего scoped report; ограничения совместимости версии/сборки, perf map и native stack не удалены.

## Проверки и открытые ограничения

verify-mcq-json: PASS, 12 Q / 15 блоков; semantic review NOT_RUN. Anti-tell: 0/15. SKEL: hard_fails=0, warnings=2 (Q6 wrong B dash, Q11 wrong D colon). Linkage errors=0, один STEM-MISMATCH Q12:B1: видимый Q12 шире частного вопроса о frame pointers; контекст frame pointers/perf явно есть в теле теории и всех четырёх вариантах, не спрятан только в question_text.

Answer-parity: exit 1, OPTION_LENGTH_RATIO=1.36 в Q5 (порог 1.35) и DUPLICATE_NGRAMS=4 (общие сравнения пяти контейнеров). Остальные проверки проходят: correct-longest 2/15, correct/wrong average=1.018, style-guessability=0.289. Пороги не менялись и диагностический FAIL не переименован в PASS. Struct-scan contrast:max=0.489 и negation:min=0.328 оставлены видимыми; отсутствие статистической утечки не доказано. Повторы/длину автор счёл содержательно оправданными, но независимый reviewer должен проверить это решение.

Логи и точный проверенный SHA сохранены в рабочем пространстве: python-core-final-reviewed-checks.json и check-0..6.log. Это не immutable независимые evidence. Phase A/B, независимые SFPL/ANSC/RU и итоговая MCQ-приёмка NOT_RUN. Python 3.15 не устанавливался, новые возможности не исполнялись. Исполнение обычных примеров другой задачей отражено в [отчёте теории](../../../../../interview-coverage/python-theory-packages-2026-11-05.md), не считается собственной runtime-проверкой MCQ. Работающий сервис не менялся.

Источники: [PEP 814 frozendict](https://peps.python.org/pep-0814/), [PEP 661 sentinel](https://peps.python.org/pep-0661/), [PEP 686 UTF-8 default](https://peps.python.org/pep-0686/), [PEP 810 lazy imports](https://peps.python.org/pep-0810/), [Python 3.15 sampling profiler](https://docs.python.org/3.15/library/profiling.sampling.html), [PEP 831 frame pointers](https://peps.python.org/pep-0831/). Версионные положения проверены по официальным источникам 2026-11-05; наличие документации не является доказательством запуска новой версии.
