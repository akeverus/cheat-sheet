# ArchUnit — повторная сверка исправленной пары

Дата: 2026-11-05. Режим: авторская проверка исправленного MCQ после синхронизации теории другим исполнителем; ключи видны. JSON `testing/archunit-interview.json` остаётся SHA `7de93c0bf864780caa0859896835e6fa1bd44ca500b2f026a74d6c9f1e4d9375`. Теория `cheatsheets/interview/testing/archunit-interview.md` подготовлена с SHA `00e30365ffe189f2e5a739748d7f03bb59b2739ab0f1683a0469dbc988559360`; она ещё не входит в собственный коммит этой задачи.

Полностью перечитаны изменённые Q4/Q5/Q7/Q12/Q13/Q14 и все связанные варианты/sections; неизменённые девять Q ранее прочитаны в полном peer pass. Крупные расхождения предыдущего отчёта **семантически устранены**:

- Q4 различает начальный DependencySettings и ArchRule после considering*.
- Q5 проверяет полноту ролей/adapters, optional layers и направление связей с классами вне модели; MCQ не обещает проверки необъявленного persistence adapter по одному имени.
- Q7 допускает regex вместо orShould; неверный D теперь проверяет конкретный EndingWith(Service), который не принимает Impl.
- Q12 согласует per-class/shared caches, locations + ImportOption types и повторное выполнение правил.
- Q13 разделяет Platform, Jupiter и ArchUnit engine, а обязательный CI gate отделён от наличия теста.
- Q14 разделяет создание индекса, обновление store и refreeze; готовый baseline не восстанавливается из текущего bytecode.

Дополнительная собственная **runtime-проверка PASS** на cached ArchUnit **1.3.0** и JDK21: согласованная первая инициализация принимает старый долг, последующий запуск использует сохранённый baseline при запрещённом создании нового индекса, исправление удаляет долг, возвращение того же нарушения вызывает AssertionError. Каждый запуск fixture использует новый отдельный каталог в task-2; БД/сервис и проектные тесты не затронуты. Артефакт `archunit-corrected-ratchet-check.json` содержит команды, SHA source/JAR и результат. Это конкретная проверка ratchet, не полный JUnit engine/Gradle suite benchmark.

Дополнительный отчёт другого исполнителя `docs/interview-coverage/archunit-theory-sync-review-2026-11-05.md` прочитан: в нём есть свои реальные fixtures для outside→domain, native engine и режимов store. Его результаты не выдаются за наше собственное исполнение и не превращают проверку с ключом в blind acceptance.

Источники уже сверены с тегом **v1.3.0**: [Architectures](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/Architectures.java), [ClassesShould](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/lang/syntax/elements/ClassesShould.java), [ClassCache](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit-junit/src/main/java/com/tngtech/archunit/junit/internal/ClassCache.java), [FreezingArchRule](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/freeze/FreezingArchRule.java), [file store](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/freeze/TextFileBasedViolationStore.java).

Небольшая RU-правка владельцу теории, Q12: `Пользовательская ImportOption должен` → `Пользовательская ImportOption должна`. Содержательный блокер снят, но актуальный Markdown нужно сохранить/закоммитить его владельцу; этот отчёт не захватывает его файлы.

Структуру и linkage после новой теории следует фиксировать по текущим хешам отдельно. Старые результаты 0/15 anti-tell и word-parity FAIL 1.45 относятся к тому же неизменённому JSON; старый linkage NOVEL-TERM ImportOption больше нельзя оценивать по старому снимку Markdown. Полная независимая приёмка исправленной версии, строгие Phase A/B и FINAL остаются **NOT_RUN**; прежний canonical LLM_READY относится к исходным хешам и сам по себе текущую пару не подтверждает. Старые evidence не переписывались.

Фактически выполненный повторный проход по новой теории: schema **PASS15/15**, anti-tell **0/15**, linkage **errors=0 / warnings=0**, SKEL **hard=0 / warn=7**. Word-parity остаётся **FAIL 1.45 Q12**, style guessability **0.30**; остальные parity gates проходят. Логи `archunit-post-theory-sync-checks.json` и check-0..6. Это не независимая приёмка после собственного исправления JSON.
