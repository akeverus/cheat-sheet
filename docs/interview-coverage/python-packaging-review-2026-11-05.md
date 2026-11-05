# Python packaging: проверка существующего пакета — 05.11.2026

Независимые Phase A/B завершены: **5/5 блоков имеют текущий FINAL PASS**
по всем десяти проверкам workflow. Это адресная приёмка Python packaging;
состояние остальных тем и общий checkpoint не изменялись. Staging/commit
не выполнялись: индекс передан параллельной задаче MCQ.

## Проверенные изменения

Проверены существующие Markdown Q1–Q5, все 20 вариантов и их sections:
pyproject, wheel/sdist, runtime/extras/groups, повторяемые установки и startup
hooks Python 3.15. Два TOML-примера разобраны tomllib, frontmatter, заголовки,
относительные ссылки и число вопросов сверены.

В Q4 A исправлен counterfactual, ранее противоречивший ошибочной модели.
Независимое ревью r2 обнаружило реальный SFPL-дефект: correct C был единственным
полным и существенно более длинным рецептом. Finding `Q4-B0-SFPL-01` сохранён
как OPEN в истории r2. В r3 A/B/D переработаны в полные сопоставимые стратегии:
A оставляет транзитивные зависимости незакреплёнными, B ошибочно полагается
на build backend при разрешении диапазонов, D принимает фиксацию/hashes
за переносимость wheelhouse. Объяснения синхронизированы с этими моделями.
Ключи B/D/A/C/C и порядок вариантов сохранены; Markdown автором этого
дополнительного прохода не переписывался.

Свежий независимый контекст r3 выбрал C до раскрытия. Затем он проверил все
варианты, sections и первичные источники и явно разобрал свои предварительные
наблюдения о форме. Finding закрыт как RESOLVED с прежним id/check/blocks.
Четыре неизменённых блока продолжают использовать evidence r2 по своим хешам.

## Evidence

- [r2: независимый отчёт](../mcq-quality/runs/20261105-python-packaging-existing-author-r2/disclosure/independent-review.md).
- [r2: импортированный evidence, четыре PASS и один FAIL](../mcq-quality/reviews/checks/20261105-python-packaging-existing-author-r2.json).
- [r3: независимый отчёт Q4](../mcq-quality/runs/20261105-python-packaging-q4-repair-r3/disclosure/review.md).
- [r3: импортированный evidence, finding RESOLVED](../mcq-quality/reviews/checks/20261105-python-packaging-q4-repair-r3.json).

Оба run содержат неизменяемые Phase A/receipts, snapshot, раскрытие и итоговый
evidence. Автор `author-root-20261105` отличается от независимых reviewers;
оба использовали явно выбранную дешёвую модель `gpt-6-luna` в свежих контекстах.
Первоначальный r1 до слепого ревью был заменён после counterfactual-правки;
его два неиспользованных файла сохранены вне репозитория в
`/tmp/python-packaging-review-20261105/superseded-run-r1`.

## Проверки и пределы

- verify-mcq-json: PASS, 5 Q / 5 блоков. Его строка `semantic NOT_RUN` описывает
  только возможности этого скрипта; независимая семантика записана отдельно.
- Точный mcq-skel-gate: **FAIL Q4 semicolon_presence A/B=1, C/D=0**;
  warnings Q2/Q3. Это распределение 2:2, correct C делит форму с wrong D.
  Независимый [SKEL addendum](../mcq-quality/runs/20261105-python-packaging-q4-repair-r3/disclosure/skel-addendum.md)
  подтверждает SFPL PASS и закрытый finding; автоматический FAIL сохранён.
  Дополнительный structure-scorer: максимальный tell 0.67. Anti-tell: 0;
  linkage: 0 errors/warnings.
- [gates.sh, полный вывод](../mcq-quality/runs/20261105-python-packaging-q4-repair-r3/disclosure/author-gates.txt):
  exit 1; structure-report отмечает Q2/Q3/Q4, verbatim — 0. Независимая
  проверка вариантов не подменена этими статистическими отчётами.
- Parity: **FAIL только DUPLICATE_NGRAMS = 4**, все совпадения в Q3. Независимый
  рецензент r2 прочитал A–D и принял общую часть сравнения extras/groups:
  неправильность B находится в dependencies, D — в публикации groups.
  Порог не менялся, регулярный FAIL сохранён и не выдан за автоматический PASS.
- Python 3.15 локально не исполнялся; `.pth`/`.start` сверены по официальной
  документации. Никакие пакеты не устанавливались и сборки не запускались.
- Поддержка pylock.toml не заявляется для любого installer/version.

Первичные источники: [PyPA pyproject](https://packaging.python.org/en/latest/specifications/pyproject-toml/),
[Package formats](https://packaging.python.org/en/latest/discussions/package-formats/),
[Dependency groups](https://packaging.python.org/en/latest/specifications/dependency-groups/),
[pip repeatable installs](https://pip.pypa.io/en/stable/topics/repeatable-installs/),
[Hash checking](https://pip.pypa.io/en/stable/topics/secure-installs/#hash-checking-mode),
[pylock.toml](https://packaging.python.org/en/latest/specifications/pylock-toml/),
[Python 3.15 site](https://docs.python.org/3.15/library/site.html).

## Согласованный коммит

Исходный seed r2 и прежний отчёт MCQ сохранены параллельной задачей в
`70b82f23f`; **Markdown ещё изменён относительно HEAD**. Production-diff seed
теперь ограничен Q4. После нового разделения владения этот агент больше не
редактирует JSON вопросов/вариантов: правка и её evidence передаются владельцу
MCQ. Теория Q1–Q5 проверена и готова к адресному коммиту. Новый пакет содержит
Markdown, текущий diff Q4, sidecar, этот отчёт, оба run и оба evidence. Точный список и SHA-256 — в подготовленном manifest
`/tmp/prepared-theory-groups-20261105.json`; временные файлы в commit не входят.

Дополнительно нужны ровно два уже существовавших изменения в общих реестрах:
count 4→5 для этой темы в inventory.tsv и TOC.md. Полные реестры не проверялись
и целиком в пакет не входят. Изолированный patch без изменения индекса:
`/tmp/python-packaging-review-20261105/registry-only.patch`.
Перед staging координатору нужно сверить hunks с новым HEAD.
После уточнения владения Python packaging JSON не редактировался; точный
seed hash для принятой редакции записан в r3, последующие правки MCQ потребуют
проверить status на STALE.

| Реестр | Проверенный прежний HEAD | Рабочее дерево |
|---|---|---|
| inventory.tsv, Python packaging | 4 Q / 4 блока | 5 Q / 5 блоков |
| TOC.md, Python packaging | 4 вопроса | 5 вопросов |
