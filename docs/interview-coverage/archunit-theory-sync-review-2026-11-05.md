# ArchUnit — синхронизация теории с исправлениями MCQ

Дата: 2026-11-05. SEMANTIC_UPDATE, собственный scope **Q4/Q5/Q7/Q12/Q13/Q14**. Прочитаны эти шесть Q и все 24 варианта/sections с видимыми ключами; остальная теория Q1–Q15 и весь прежний Markdown diff тоже прочитаны. Это авторская source/runtime-проверка теории, не независимая blind-приёмка исправленного JSON, Phase A/B или FINAL.

Вход: [MCQ peer report](../mcq-quality/llm-first/reviews/testing/archunit-peer-review-2026-11-05.md), исправленный JSON SHA-256 `7de93c0bf864780caa0859896835e6fa1bd44ca500b2f026a74d6c9f1e4d9375`; JSON не менялся. Теория до синхронизации SHA-256 `aa29a8487335d3647ceaac2740f2aebe160374d3e63e6fd9637a105b5d2de3b9`, уже содержала незакоммиченные правки. Подготовленный [Markdown](../../cheatsheets/interview/testing/archunit-interview.md) имеет SHA-256 `00e30365ffe189f2e5a739748d7f03bb59b2739ab0f1683a0469dbc988559360`. Собственная правка меняет ровно шесть Q; остальные девять, все текущие заголовки/IDs/anchors, frontmatter/TOC/See also сохранены побайтово.

## Исправления

| Q | Уточнение теории | Проверка |
|---|---|---|
| 4 | Начальный layeredArchitecture возвращает DependencySettings; considering* даёт LayeredArchitecture/ArchRule, а не predicate. Убрана неподтверждённая частотная оценка. | Реальные типы из 1.3.0 и точный Java-блок: допустимая цепочка проходит, обратная связь и обход слоя дают нарушения. |
| 5 | Явные роли/adapters и непустая область импорта; optional layers не восстанавливают пропущенную модель. Нет общего обещания игнорировать все необъявленные пакеты. | Точный onion-блок проходит на согласованных fixtures; объявленная REST→persistence и входящая связь outside→domain обнаружены. Пропущенный adapter не получает автоматически ограничения по имени. |
| 7 | Один EndingWith(Service) отвергает Impl; два суффикса можно выразить orShould либо одним regex. Два входных имени названы явно. | Все три Java-блока извлечены без изменений: обе формы принимают UserService/UserServiceImpl; есть отрицательные проверки имени и обоих направлений имя↔пакет. |
| 12 | Разделены per-test/shared caches; общий ключ — locations + типы ImportOption. Soft references, стабильность custom filter и отдельное применение правил. | Pinned ClassCache/ImportOption source; reflection проверяет равенство реальных внутренних ключей 1.3.0 с одинаковыми locations и разными фильтрами. Это не benchmark/GC-тест. |
| 13 | Нужны Platform и ArchUnit engine; Jupiter не выполняет поля ArchTest. useJUnitPlatform — способ настройки, а не обязательный буквальный текст во всех Gradle-конфигурациях. Убрана автоматическая гарантия merge. | Изолированный Launcher: Jupiter-only нашёл 0 тестов, ArchUnit engine нашёл и успешно выполнил 1 ArchTest. Реальные Gradle config/dependency прочитаны. |
| 14 | Разделены подготовка baseline, запрет создания индекса, запрет обновления и refreeze. Нет обещания безусловно успешного первого запуска. | Точный frozen-блок и properties: default init failure; явная подготовка и известный долг проходят; новый/возвращённый долг падает; запрет записи препятствует удалению исправленного; новая description/refreeze могут принять baseline в существующем индексе. |

Дополнительные факты Q14 проверены исполнением: allowStoreCreation=false не запрещает добавлять baseline нового описания правила в существующий store при разрешённой записи. allowStoreUpdate=false может дать StoreUpdateFailedException при попытке удалить исправленный долг. Текущий bytecode не заменяет доставку исторического baseline.

## Источники и прежние изменения

Основные API и реализация зафиксированы тегом **ArchUnit v1.3.0**, соответствующим `gradle/libs.versions.toml` и cached jars:

- [Architectures](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/Architectures.java), [ClassesShould](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/lang/syntax/elements/ClassesShould.java).
- [ClassCache](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit-junit/src/main/java/com/tngtech/archunit/junit/internal/ClassCache.java), [ImportOption](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/core/importer/ImportOption.java).
- [FreezingArchRule](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/freeze/FreezingArchRule.java), [file store](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/freeze/TextFileBasedViolationStore.java).
- [Gradle 8.9 test suites](https://docs.gradle.org/8.9/userguide/jvm_test_suite_plugin.html) и pinned [DefaultJvmTestSuite](https://github.com/gradle/gradle/blob/v8.9.0/platforms/jvm/plugins-jvm-test-suite/src/main/java/org/gradle/api/plugins/jvm/internal/DefaultJvmTestSuite.java): источник настройки framework, не исполненный suite-тест.

Ранее существовавшие смысловые изменения затрагивали Q2/Q5/Q6/Q8/Q12/Q13/Q15; также уже менялись updated, маркеры заголовков, Q13 title и добавлялись явные q1–q15 anchors. Они сохранены. Q2 importClasspath/filter сверён с [ClassFileImporter 1.3.0](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/core/importer/ClassFileImporter.java), Q6 capture groups — с [PackageMatcher](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/core/domain/PackageMatcher.java), Q8 annotations — с [GeneralCodingRules](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/GeneralCodingRules.java). Q15 сравнен с фактическим `LayeredArchitectureTest.java` и build/version catalog: зависимость service→api.dto не запрещена этими тремя правилами, jMolecules в зависимостях проекта не появился. Неизменённые примеры jMolecules заново не исполнялись.

## Согласование MCQ и пределы

Ключи **Q4 B / Q5 A / Q7 B / Q12 D / Q13 A / Q14 C** совместимы с исправленной теорией. Проверены все A/B/C/D и sections этих шести Q; новая JSON-правка автору теории не потребовалась. Source/runtime-находки передачи устранены, но это не присвоение LLM_READY/FINAL всей паре и не изменение общих реестров.

Schema/Markdown validation **PASS15 Q/15B**, semantic review NOT_RUN. Linkage на окончательном стабильном SHA — **exit0/errors0/warnings0**. После промежуточного переписывания Q7 scanner отметил отсутствие буквального UserService; в пояснении двух разрешённых имён восстановлена явная пара, уже проверенная runtime. Алгоритм, allowlist и пороги не менялись. Зелёный scanner не является независимой содержательной приёмкой.

В переданном MCQ-отчёте остаётся parity Q12 word ratio 1.45 на том же JSON SHA; собственный новый parity/Phase A/B не заявляется. Остальные девять MCQ не получают повторной содержательной приёмки этим пакетом.

## Выполнение

**Runtime PASS:** локальный Microsoft JDK **21.0.10**, cached ArchUnit **1.3.0**, SLF4J API **2.0.13**, Spring annotation classes **6.1.11**; для engine fixture Platform **1.10.3**, Jupiter **5.10.3**. Ничего не установлено. Использованы только изолированные классы/store в `/tmp`; runtime настоящего приложения и его Gradle-задача не запускались. Отсутствие SLF4J provider приводило к NOP logger, не к падению.

Два исходных сбоя обвязки сохранены: сначала она ссылалась на package-private типы исключений напрямую; затем слишком широко ожидала отсутствие любых нарушений в неполной onion-модели. Во втором случае реальная outside→domain связь тоже нарушала правило. Проверка сужена до конкретной пропущенной adapter-связи с сохранением положительного контроля outside→domain. Точные Java-блоки Markdown при исправлении обвязки не менялись; итоговые javac/java завершились 0. Предварительное утверждение в commentary о «вложенных» исключениях было исправлено после javap: они имеют пакетную видимость.

**NOT_RUN:** проектный Gradle/Surefire/CI, реальные merge gates, Gradle test suites/convention plugin runtime, jMolecules runtime, benchmark/GC/concurrency, Mermaid render и независимые SFPL/ANSC/RU/Phase A/B/FINAL. Docker не вызывался; прежний отказ не обходился. Индекс и JSON этого пакета не менялись, коммит ожидает координации после освобождения предыдущего metrics-коммита.

Артефакты `/tmp/archunit-theory-sync-20261105/`: before/head snapshots и seed, static-checks.json, extracted-java-blocks.json, linkage/verify logs, web extracts; runtime/src, classpath.txt, execution.json, execution-attempt1/2.json, store-exceptions-javap.txt и изолированный freeze-store. После успешного runtime менялся только русский текст; точные Java/properties SHA повторно сверены. Источники прочитаны 2026-11-05, версии local runtime отделены от online user guide.
