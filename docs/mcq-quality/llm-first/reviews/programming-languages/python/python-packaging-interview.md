# Упаковка Python — авторская редакция MCQ

- Дата: 2026-11-05; исполнитель: Codex (точный model-id не экспортирован).
- Статус авторского прохода: `LLM_READY`. Дополнительная независимая приёмка сохранена другой задачей в b850c017f; свежий workflow status подтверждает `FINAL PASS` для 5/5 блоков по текущим хешам. Это не собственный независимый проход автора этого отчёта.
- Охват: Q1–Q5, 5 блоков B0; прочитаны видимые вопросы, теория, A/B/C/D и все sections.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/programming-languages/python/python-packaging-interview.json`; SHA-256 `484b22c42b0357d7d3227e753f1b51ee0ceb7e5e7cd47c4d4856288c1eb2626f`.
- Парный Markdown: `cheatsheets/interview/programming-languages/python/python-packaging-interview.md`; SHA-256 `98504a41ab86bf8b287d99c34a9d23efd86b1b33c81fde44e2359167cb218bc9`. Теория не изменялась.

## Изменения

Q1: все варианты различают три роли таблиц, включая метаданные и требования сборки. Q2: каждый вариант сравнивает оба формата дистрибутива; ошибочные модели сборки объяснены отдельно. Q3: роли dependencies/extras/groups сопоставлены по публикации метаданных. Q4: сохранено параллельное уточнение всех distractors: частичная фиксация дерева, неверная роль backend и ошибочная переносимость wheelhouse; повторно прочитаны A/B/C/D и объяснения, сохранена независимо принятая редакция без изменения её текста. Q5: каждый вариант охватывает расширение путей, запуск функций и приоритет одноимённых файлов; опровержения относятся к новым формулировкам.

## Сопоставление вариантов

Ключ был доступен. Таблица описывает проверенные модели A/B/C/D; это авторское сравнение содержания, полноты, формы и русского текста, а не независимый blind-проход.

| Блок | A | B | C | D |
|---|---|---|---|---|
| Q1/B0 | поменяны сборка и runtime | верные роли трёх таблиц | поменяны project/tool | поменяны build-system/tool |
| Q2/B0 | поменяны форматы | sdist без backend | wheel требует компиляции при установке | сборка и совместимый wheel |
| Q3/B0 | публикация dependencies/extras, локальные groups | runtime принят за build | extras/groups поменяны местами | groups объявлены публикуемыми |
| Q4/B0 | не закреплены транзитивные версии | диапазон принят за фиксацию | полное дерево, hashes и целевая среда | wheelhouse принят за переносимый на любые ABI/платформы |
| Q5/B0 | старт принят за первый импорт | парный pth целиком отключён | пути, startup-функции, приоритет start | парные import-строки исполняются вместе |

## Первичные источники

- [PyPA pyproject.toml](https://packaging.python.org/en/latest/specifications/pyproject-toml/)
- [PyPA Dependency Groups](https://packaging.python.org/en/latest/specifications/dependency-groups/)
- [pip Hash-checking Mode](https://pip.pypa.io/en/stable/topics/secure-installs/#hash-checking-mode)
- [pip Repeatable Installs](https://pip.pypa.io/en/stable/topics/repeatable-installs/)
- [Python 3.15 site](https://docs.python.org/3.15/library/site.html)

## Проверки и ограничения

`verify-mcq-json`: PASS. Структура, единственность ключа и локальные ссылки проверены; validator пишет semantic review NOT_RUN. Номера Q/B, видимые вопросы и позиции ключей сохранены.

Текущий диагностический набор: `mcq-answer-parity-gate` exit 1, DUPLICATE_NGRAMS=4. Anti-tell и детерминированный linkage без сигналов. SKEL pre-check: hard_fails=1, semicolon_presence в Q4: A/B=1, C/D=0. Автоматический FAIL сохранён. Независимый [SKEL addendum](../../../../runs/20261105-python-packaging-q4-repair-r3/disclosure/skel-addendum.md) оценивает распределение 2:2: correct C и wrong D делят одну форму, поэтому сигнал не выделяет ключ. Пунктуация не изменялась ради сканера, пороги и правила не менялись.

Q4 сравнивает полноту закрепления и проверку пригодности артефактов во всех A/B/C/D. Объяснение D относится к ABI/платформе, а не прежней модели hash прямого пакета. JSON побайтово совпадает с принятым в b850c017f, парный Markdown не менялся. [Отчёт независимой приёмки](../../../../../interview-coverage/python-packaging-review-2026-11-05.md) и [evidence Q4 r3](../../../../reviews/checks/20261105-python-packaging-q4-repair-r3.json) сохранены без изменений. Свежий `mcq-workflow status --topic programming-languages/python/python-packaging-interview.json`: accepted_files=1, accepted_blocks=5, все десять checks PASS.

Поведение startup-файлов Python 3.15 сверено с документацией; на установленном Python 3.14 не исполнялось. Установка чужих пакетов и доступ к сети из примеров не выполнялись.

Независимые SFPL/ANSC/RU, Phase A/B и итоговая приёмка по этим хешам подтверждены существующими evidence r2/r3. Исполнение startup-файлов Python 3.15 локально этим отчётом не подтверждается.
