# ArchUnit — редакция MCQ

- Дата: 2026-11-05; модель: GPT-6.
- Статус: `LLM_READY`. Авторская редакция без независимого содержательного ревью и без `FINAL PASS`.
- Охват: Q1–Q15, 15 блоков B0, все варианты A–D и их `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/testing/archunit-interview.json`; SHA-256 `96a9d803b6b5e6c900c53dddd19d0b1e8d0697543798da80af992d45de4e970b`.
- Парный Markdown: `cheatsheets/interview/testing/archunit-interview.md`; SHA-256 `aa29a8487335d3647ceaac2740f2aebe160374d3e63e6fd9637a105b5d2de3b9`.

## Что исправлено

Прочитаны все 15 видимых вопросов, варианты и объяснения. В Q2 устранён второй защитимый ответ: `importClasspath()` без фильтра включает доступные JAR-локации, а `DO_NOT_INCLUDE_JARS` добавляют явно. Теория и объяснения вариантов теперь говорят об одном поведении.

Q6 точнее связывает группу `(*)` с границей среза, Q8 различает готовые `GeneralCodingRules` и правило проекта для контроллеров, Q10 раскрывает роль обеих аннотаций JUnit, Q12 описывает переиспользование импорта по локациям без неподтверждённой гарантии о внутреннем хранении. Заголовок Q13 ограничен вопросом о запуске в CI, который проверяют его варианты.

Q15 теперь отвечает и про jMolecules, и про текущий `quiz-app`. Три правила `LayeredArchitectureTest` сверены с исходником: для `service` запрещены зависимости от `api.controller`, `api.security` и `api.exception`; `api.dto` разрешён. Описание в Markdown и `sections` исправлено. В Markdown добавлены явные якоря Q1–Q15 и обновлено содержание.

## Проверка и границы результата

Первичные источники: [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html), [ClassFileImporter](https://github.com/TNG/ArchUnit/blob/main/archunit/src/main/java/com/tngtech/archunit/core/importer/ClassFileImporter.java), [ImportOption](https://github.com/TNG/ArchUnit/blob/main/archunit/src/main/java/com/tngtech/archunit/core/importer/ImportOption.java), [интеграция jMolecules с ArchUnit](https://github.com/xmolecules/jmolecules-integrations). Версия в `gradle/libs.versions.toml` для `quiz-app` — ArchUnit 1.3.0; существующий fact-freshness sidecar с 1.4.2 не использован как свидетельство версии проекта.

`verify-mcq-json.sh` → `PASS: files=1 questions=15 blocks=15; semantic review NOT_RUN`. `mcq-skel-gate.py` → hard fails 0, предупреждения 7. `mcq-anti-tell-scan.py` → 0 блоков с формальными подсказками. `mcq-answer-parity-gate.py` → PASS. `git diff --check` для пары файлов прошёл.

Эти проверки подтверждают структуру и ограниченные признаки формы, но не дают независимого содержательного вердикта. Независимое ревью и слепой проход по указанным хешам остаются открытыми.
