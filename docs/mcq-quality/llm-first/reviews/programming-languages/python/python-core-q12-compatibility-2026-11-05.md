# Python Core Q12 — авторская проверка профилирования

Дата: 2026-11-05. Статус: `AUTHOR_VERIFIED`, Q12:B0 и Q12:B1. Прочитаны видимый вопрос, вся теория Q12, восемь A/B/C/D и sections. Ключи B/C, тексты вариантов, номера и порядок сохранены. Это продолжение прежней адресной правки B0, а не приёмка всей темы.

JSON: `modules/quiz-app/src/main/resources/seed/mcq/programming-languages/python/python-interview.json`, SHA-256 `f7c8c52837aa1150c77d365d8c46f0510d18d9c4b0a3516fc8dff6dd18107c6c`. Markdown: `cheatsheets/interview/programming-languages/python/python-interview.md`, SHA-256 `76c4767753adb78e5419b7ec65f18781d83833736a8ffca168f19e07509a5775`. Теория не редактировалась.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q12:B0 | tracing принят за attach к PID | статистическая выборка attach | run принят за attach без нового процесса | один dump принят за профиль за интервал |
| Q12:B1 | frame pointers заменяют perf map | runtime-опция пересобирает модули | развёртка native и имена Python разделены | встроенный attach требует perf map |

Все варианты отвечают о конкретном механизме профилирования и его результате. Correct не единственный профессионально сформулированный вариант; ложные модели опровергаются текущими sections. В B1 все варианты явно задают область frame pointers/perf/attach; необходимые условия не содержатся только в скрытом question_text. Сравнение формы, полноты и русского текста выполнено автором с ключом, независимые SFPL/ANSC/RU не заявлены.

Сохранена обязательная совместимость attach по minor-версии и типу сборки; для prerelease — по точной версии. B0.example теперь прямо задаёт совместимый target. В B1 уточнены поддерживаемые платформы, compile-time флаги интерпретатора/расширений и Windows x64 unwind metadata. Perf map не приписан встроенному sampling attach. Синтетический native-маркер не объявлен полным стеком C.

Источники: [profiling.sampling](https://docs.python.org/3.15/library/profiling.sampling.html), [version compatibility](https://docs.python.org/3.15/library/profiling.sampling.html#version-compatibility), [PEP 831](https://peps.python.org/pep-0831/). Проверены 2026-11-05. Python 3.15, attach и системные профайлеры не запускались; настройки доступа не менялись.

Адресный verify-mcq-json: PASS, 12 Q / 15 блоков, semantic review NOT_RUN. Остальные 13 блоков темы этим проходом не проверялись; LLM_READY всей темы, независимый или слепой проход и FINAL не заявлены. Полный ранее существовавший diff Core JSON остаётся вне подготовленного к коммиту scope. Staging/commit не выполнялись.
