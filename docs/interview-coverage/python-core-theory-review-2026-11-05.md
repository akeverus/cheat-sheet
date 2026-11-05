# Python core theory review (Q1–Q12)

Дата проверки: 2026-11-05. Scope: только теория cheatsheets/interview/programming-languages/python/python-interview.md и read-only paired JSON modules/quiz-app/src/main/resources/seed/mcq/programming-languages/python/python-interview.json, Q1–Q12. Проверен git diff HEAD по этим двум файлам. Исходники, MCQ, индекс, планы и HEAD не изменялись. Это ограниченный review теории и синхронизации, не MCQ FINAL.

Локальный интерпретатор — Python 3.14.4. Он не использовался для проверки поведения новых функций Python 3.15; версионные утверждения сверены с опубликованной документацией Python 3.15, PEP и официальными HOWTO.

## Итог

Статус после повторной проверки: finding PYCORE-THEORY-Q12-ATTACH-VERSION закрыт.

В первоначальном проходе в Q12 была найдена существенная пропущенная эксплуатационная граница для profiling.sampling attach: профайлер и целевой процесс должны использовать совместимые версии Python. В последующей правке автор добавил условия, подтверждённые первичной документацией; повторная проверка закрыла finding.

Пример команды python3.15 -m profiling.sampling attach ... 12345 корректен при условии, что PID принадлежит Python 3.15 того же вида сборки (standard/free-threaded совместимость тоже должна совпадать). Вставить короткую оговорку рядом с требованиями доступа: «Профайлер и целевой процесс должны использовать одну minor-версию Python; free-threaded и standard build несовместимы для attach». Это сохраняет смысл существующего объяснения.

## Проверка по вопросам

| Q | Результат теории | Проверка парного MCQ |
|---|---|---|
| Q1 | PASS. Верно различены имя-ссылка, identity, ==, изменяемость и вложенный mutable-объект в tuple; оговорка про interning корректна. | Синхронно: correct D проверяет alias/identity/value; wrong A–C отражают конкретные перепутывания. |
| Q2 | PASS. Корректно различены присваивание, shallow/deep copy; отмечены циклы/shared references и особые объекты, для которых глубокое копирование может быть бессмысленным. | Синхронно: correct B. Дистрактор D чрезмерно обещает для внешних ресурсов/классов; теория прямо оговаривает это. |
| Q3 | PASS, версионная проверка выполнена. PEP 814: frozendict immutable, не dict-subclass, реализует Mapping, сохраняет insertion order; hash зависит от hashability ключей и значений; construction from dict — shallow copy. Текст и пример соответствуют. | Синхронно: correct A в B1 согласован с shallow copy и хешируемостью; wrong B–D адресуют hash, вложенную копию и subclass. B0 предшествующий общий выбор коллекции не конфликтует. |
| Q4 | PASS. Hash contract и последствия изменения полей hash/eq после вставки изложены верно; объяснение поведения класса с __eq__ без __hash__ корректно. | Синхронно: correct A и wrong B–D соответствуют теории. |
| Q5 | PASS, версионная проверка выполнена. Defaults вычисляются при исполнении def; sentinel уникален и truthy, для отсутствующего аргумента сравнивается через is. Python 3.15 docs явно разрешают использовать sentinel-переменную в type expressions; запись str \| None \| MISSING валидна для этого режима. | Синхронно: correct D в B0 и correct B в B1. Теория объясняет каждую модель wrong. |
| Q6 | PASS. Области имён и late binding closure изложены правильно; default argument в lambda фиксирует текущее значение. | Синхронно: correct C; distractors противоречат примеру и правилам scope. |
| Q7 | PASS. Iterable/iterator/generator определения верны, включая legacy sequence protocol и ленивое выполнение generator body. Ресурсная оговорка уместна. | Синхронно: correct A; B–D проверяют eager execution, неверный iterable protocol и restart после StopIteration. |
| Q8 | PASS. finally/with, suppression через truthy __exit__, exception chaining и риск маскировки исходной ошибки изложены верно. | Синхронно: correct B и wrong A/C/D соответствуют теории. |
| Q9 | PASS. default_factory, frozen=True, slots=True и отсутствие runtime-валидации аннотаций изложены корректно; caveat про явно переданный общий список точен. | Синхронно: correct D; варианты A–C соответствуют отмеченным границам. |
| Q10 | PASS, версионная проверка выполнена. PEP 686 устанавливает UTF-8 mode по умолчанию в 3.15; явная encoding остаётся рекомендуемым контрактом, encoding="locale" нужен для locale semantics. Приведены EncodingWarning и временный opt-out для диагностики. | Синхронно: correct C; wrong A/B/D проверяют прежнее поведение, locale opt-out и миф об автоопределении кодировки. |
| Q11 | PASS, версионная проверка выполнена. PEP 810: явный lazy import откладывает исполнение модуля, ошибки и side effects до первого доступа; синтаксис модульного уровня и оговорка о global mode/filter отражены. | Синхронно: correct A; wrong B–D опровергаются body/теорией. |
| Q12 | PASS после повторной проверки — первоначальное finding закрыто; Остальная проверенная семантика run/attach/dump, wall/CPU, native frames, perf map и frame pointers согласуется с Python 3.15 docs / PEP 831. Команда attach имеет допустимый синтаксис. | Синхронно по проверенным тезисам: B0 correct B, B1 correct C. MCQ проверяет выбор профайлера и роль frame pointers; новая операционная оговорка теории не меняет эти ключевые ответы. Seed не менялся; формальная MCQ-приёмка не выполнялась. |

## Findings

### PYCORE-THEORY-Q12-ATTACH-VERSION — Q12, условие применения команды

Severity: medium
Status history: открыт в первом проходе; закрыт после точечной правки и повторной проверки (см. addendum ниже).

Q12 рекомендует запускать attach-профайлер python3.15 к PID работающего сервиса и объясняет права доступа к памяти target. Однако Python 3.15 profiling.sampling docs требуют совпадения Python minor version у профайлера и target; attach из 3.14 к 3.15 не поддерживается. Для free-threaded target отдельно требуется совпадающий тип сборки. Без этой границы команда может быть скопирована для неподходящего Python-процесса и завершиться неудачно.

Evidence: Python 3.15 profiling.sampling, раздел Version compatibility: https://docs.python.org/3.15/library/profiling.sampling.html#version-compatibility. Раздел также требует совместимости standard/free-threaded builds. CLI пример attach и duration/flamegraph подтверждён тем же руководством: https://docs.python.org/3.15/library/profiling.sampling.html#the-attach-command.

### Другое обнаруженное

Других подтверждённых фактических дефектов или командных ошибок в Q1–Q12 не обнаружено. Q12 finding не означает, что всю тему или MCQ следует переоткрыть: затронуты теория Q12 и синхронные explanation/edge-case поля Q12 attach-опций, если автор добавит это ограничение.

## Официальные источники и проверенные утверждения

- PEP 814, frozendict, Python 3.15: https://peps.python.org/pep-0814/ — hashability, shallow construction, insertion order, Mapping, non-subclass of dict.
- Python 3.15 built-in types: https://docs.python.org/3.15/builtins/stdtypes.html#mapping-types-dict-frozendict — версия официальной справки и mapping API.
- PEP 661 и sentinel built-in: https://peps.python.org/pep-0661/ ; https://docs.python.org/3.15/builtins/functions.html#sentinel — unique/truthy sentinel и type expression int | MISSING.
- PEP 686 и io docs: https://peps.python.org/pep-0686/ ; https://docs.python.org/3.15/library/io.html#text-encoding — UTF-8 mode defaults и explicit encoding guidance.
- PEP 810 и Python 3.15 What's New: https://peps.python.org/pep-0810/ ; https://docs.python.org/3.15/whatsnew/3.15.html#pep-810-explicit-lazy-imports — lazy import timing, syntax restrictions, side effects/errors.
- profiling.sampling docs: https://docs.python.org/3.15/library/profiling.sampling.html — attach/run/dump, command syntax, sampling caveats, permissions and version compatibility.
- PEP 831 and perf HOWTO: https://peps.python.org/pep-0831/ ; https://docs.python.org/3.15/howto/perf_profiling.html — frame-pointer support and separate perf-map requirement.

## Ограничения проверки

- Не запускал Python 3.15: выводы по новой функциональности основаны на официальных опубликованных спецификациях/документации, а не на runtime.
- Q1–Q12 и указанный paired JSON просмотрены read-only; проверка здесь не является полной приёмкой MCQ.
- Проверен diff HEAD только этих двух целевых файлов; никакие изменения не вносились.


## Повторная проверка и закрытие finding

Перечитал актуальный абзац Q12. Он теперь явно требует, чтобы профайлер и target использовали одну minor-версию Python и один тип сборки (обычная либо free-threaded); для alpha/beta/RC требует точного совпадения версии; отдельно указывает, что команда выше предназначена для совместимого Python 3.15 PID, а не произвольного процесса. Это соответствует официальному разделу Version compatibility: https://docs.python.org/3.15/library/profiling.sampling.html#version-compatibility. В документации указаны те же три условия: совпадение minor version, точная версия для prerelease и несовместимость standard/free-threaded пар. Команда attach также соответствует документированному CLI.

Следовательно, PYCORE-THEORY-Q12-ATTACH-VERSION закрыт; требование теперь явно стоит рядом с командой и требованием прав доступа. Проверка была чтением и сверкой с источником; runtime на Python 3.15 не запускался. Seed не изменён.

Актуальные SHA-256 после правки:

- Теория Markdown: `76c4767753adb78e5419b7ec65f18781d83833736a8ffca168f19e07509a5775`
- Paired MCQ seed: `5491249650c63aa9ab36897919309aba2b075e0c9573ce4a6f89a45c376fc09c`
