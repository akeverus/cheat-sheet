# Trie и Union-Find — редакция MCQ

- Дата: 2026-11-04; модель: GPT-6.
- Статус: `LLM_READY`. Это авторская редакция без независимого ревью и без `FINAL PASS`.
- Охват: Q1–Q28, 28 блоков B0, варианты A–D и поля `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/algorithms/data-structures/tries-interview.json`; SHA-256 `5a62a59c109e67b56bb24b1823f0a02d0149fd899486daf90915ddb02d42c3bc`.
- Парный Markdown: `cheatsheets/interview/algorithms/data-structures/tries-interview.md`; SHA-256 `9ed80ec7c3c675cb3f2edc581eb99f0711bbccea6c034fd757d6becefe9c25d8`.

## Что исправлено

Прочитаны все 28 блоков. Q3 теперь явно ограничен алфавитом `a–z`, который соответствует массиву из 26 детей. Q4 учитывает стоимость перечисления результатов и память на узлы. Q5 отделяет точный поиск от префиксных операций и связывает лексикографический порядок с порядком обхода детей. В Q9 уточнён смысл failure link.

В Q11, Q20, Q22 и Q23 видимый вопрос фиксирует конкретный способ решения, иначе другой вариант описывал рабочий алгоритм. Q14 ограничен неотрицательными числами, Q21 — простым графом, Q27 — бинарной формой Patricia trie. Q28 требует актуального Bloom filter; объяснение называет условия отсутствия false negatives. Из теории убраны гарантии, которые не распространяются на другие реализации Patricia и Bloom filter.

В Markdown обновлены пролог, оглавление и явные якоря Q1–Q28; редакционные метки `(!)` удалены.

## Проверка и открытые вопросы

Первичные источники: [Aho–Corasick (1975)](https://par.cse.nsysu.edu.tw/resource/paper/2022/220913/Efficient%20String%20Matching%20an%20Aid%20to%20Bibliographic%20Search.pdf) о поиске множества образцов, [Morrison (1968)](https://www.oneofus.la/have-emacs-will-hack/files/PatriciaTrie-JACM1968.pdf) о бинарной форме PATRICIA и [Bloom (1970)](https://www.cs.princeton.edu/courses/archive/spr05/cos598E/bib/p422-bloom.pdf) о вероятностной проверке членства.

Самопроверка: 28 вопросов JSON совпадают с заголовками Markdown, в каждом блоке один верный вариант, поля `sections` заполнены. `git diff --check` для пары файлов прошёл.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/algorithms/data-structures/tries-interview.json` → `PASS: files=1 questions=28 blocks=28; semantic review NOT_RUN`.

Независимое содержательное ревью и слепой пользовательский проход по этим хешам остаются открытыми.
