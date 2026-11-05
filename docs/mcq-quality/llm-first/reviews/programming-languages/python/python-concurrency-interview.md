# Python Concurrency — авторская редакция MCQ

- Дата: 2026-11-05; исполнитель: Codex (точный model-id не экспортирован).
- Статус: `LLM_READY`. Авторский проход с доступным ключом; независимая приёмка и FINAL PASS не заявлены.
- Охват: все Q1–Q9, 10 блоков, включая Q9:B1; прочитаны видимые вопросы, вся теория, все A/B/C/D и sections.
- JSON: `modules/quiz-app/src/main/resources/seed/mcq/programming-languages/python/python-concurrency-interview.json`; SHA-256 `52053c7b4f46e0078116449dfdf0a152b198b0854170d38b7f0b5313a31e0e21`.
- Markdown: `cheatsheets/interview/programming-languages/python/python-concurrency-interview.md`; SHA-256 `7d3f58ac939b8b14feee1092a0ce2a59cff83265bdf24e154ac5ae7b085db02c`. Теория не редактировалась.

## Содержательные изменения

Q1/Q2: вместо одного полного ответа и трёх частных отрицаний варианты сопоставляют выбор модели и границы GIL. Инвариант можно защищать lock либо владением состоянием; GIL сам не объединяет несколько действий.

Q3–Q6: объяснения различают планирование и исполнение coroutine, готовый Future без обязательного yield, eager Task с 3.14, обычную ошибку TaskGroup, gather по умолчанию и поздний cancel. В Q5 все варианты охватывают отмену и timeout; преобразуется только собственная отмена timeout, внешняя отмена сохраняется. Вынос I/O в поток не обещает CPU-параллелизма или принудительной остановки функции.

Q7/Q8: backlog отделён от числа активных операций, join — от остановки workers, task_done — от бизнес-успеха. Все альтернативы ProcessPoolExecutor относятся к передаче callable/данных, импорту и ожиданию результатов. Указаны цена обмена, смена start method с 3.14 и предупреждение об Executor/Future из worker.

Q9: сборка, runtime GIL, ABI и потокобезопасность рассматриваются отдельно. Correct B1 больше не перечисляет только Stable ABI: version-specific cp315-cp315t — допустимый пример; abi3t/dual ABI разобраны в объяснении вместе с границами совместимости.

## Сравнение A/B/C/D

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q1:B0 | loop принят за многоядерное исполнение | новый поток принят за освобождение от GIL | обмен процессами объявлен несущественным | три профиля работы и стоимость процессов |
| Q2:B0 | GIL сериализует ожидания I/O | GIL защищает составной dict-инвариант | байткод, I/O и отдельная защита инварианта | контракт перенесён на все Python runtime |
| Q3:B0 | создание, await и отдельная Task | тело исполняется при создании | Task принята за поток ОС | create_task принят за чтение готового результата |
| Q4:B0 | gather приписана отмена соседей | TaskGroup ждёт отмену, gather передаёт ошибку | TaskGroup возвращает ошибки как значения | TaskGroup выходит без ожидания детей |
| Q5:B0 | принудительное завершение без cleanup | CancelledError принят за Exception | timeout останавливает внешний поток | кооперативная отмена и собственный timeout |
| Q6:B0 | sync sleep сам становится async | to_thread автоматически обходит GIL | async API/поток и клиентский timeout | отмена await завершает функцию потока |
| Q7:B0 | буфер, фиксированные workers и producer | semaphore ограничивает созданные Task | обычный put сам отбрасывает элементы | join сам завершает workers |
| Q8:B0 | объекты передаются по общим ссылкам | pickle, импорт модуля и издержки | код lambda из REPL передаётся сам | worker может ждать Future того же пула |
| Q9:B0 | контейнерный lock защищает весь инвариант | импорт не может включить GIL | однопоточное ускорение гарантировано | особая сборка, runtime и синхронизация |
| Q9:B1 | обычный abi3 подходит 3.15t | build-флаг заменяет runtime-проверку | совместимый wheel и GIL после импортов | выключенный GIL доказывает безопасность |

Это авторское сравнение полноты, смысла, формы и русского текста. Ключи не переставлялись, вопросы/номера/порядок сохранены; неверные варианты опровергаются своими текущими объяснениями. Общие условия не служат маскировкой пустого ответа: в каждом wrong названа конкретная ошибочная модель.

## Источники и проверка

- [Python 3.15 asyncio tasks, cancellation, TaskGroup, gather, timeout, to_thread](https://docs.python.org/3.15/library/asyncio-task.html)
- [Python 3.15 queues](https://docs.python.org/3.15/library/asyncio-queue.html)
- [Python 3.15 concurrent.futures](https://docs.python.org/3.15/library/concurrent.futures.html)
- [Free-threading guide](https://docs.python.org/3.15/howto/free-threading-python.html)
- [PEP 803 compatibility](https://peps.python.org/pep-0803/#compatibility-overview)

`verify-mcq-json`: PASS, 9 Q / 10 блоков; semantic review NOT_RUN. Anti-tell: 0/10. SKEL pre-check: hard_fails=0; warnings Q6 и Q9:B1 относятся к скобкам вызова API. Linkage: errors=0, warning STEM-MISMATCH Q9:B1. Видимый Q9 шире скрытого B1, но версия 3.15t и проверка бинарных зависимостей явно присутствуют во всех вариантах и теле Q9; условие не спрятано только в question_text.

Answer-parity: exit 1, DUPLICATE_NGRAMS=11; остальные проверки проходят. Повторы находятся в общих условиях сравнений Q1/Q2 и проверках зависимостей: модель ошибки меняется по существу. Пороги не менялись, FAIL не объявлен автоматическим PASS. Struct-scan отмечает clauses/commas:max=0.4 на десяти блоках; отсутствие статистической утечки не доказано и независимое сравнение формы остаётся открытым.

В этом проходе Python 3.15 не устанавливался, зависимости не менялись и примеры повторно не запускались. Исполнение примеров обычной Python 3.14 другой задачей описано в [отчёте теории](../../../../../interview-coverage/python-theory-packages-2026-11-05.md); это не собственная runtime-проверка автора MCQ и не независимая MCQ-приёмка.
