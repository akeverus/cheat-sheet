# Mockito Q39 — выбор matcher и captor

Дата: 2026-11-05. Узкий `interview-writer / SEMANTIC_UPDATE` после коммита `da0c8e618151d7b228150792a6f91e2f09ec46ef`: исправлен только Q39. Суммарно автором исправлены **16 из 45** ответов; другие 29 не считаются исправленными этим набором. Авторская сверка с открытым ключом не является independent acceptance, blind Phase A/B или FINAL. JSON и индекс не редактировались.

Вход: теория SHA `40741bdab8242305aa3c5faf1c4414c1c8b330ee074ea55a00d2dc3de853a374`, MCQ SHA **`9a730182df2886458740bac88738e4eef2f187a4afe49e8bbaf78ac755d99a09`**. Прочитаны полный ответ и все четыре варианта Q39 с sections; ключ B и актуальные пояснения согласуются с исправлением. Итоговая теория SHA **`e14c8040742cb80e6597a79e0b5b2259282b472ef6db585e60f56a878eb1c5bb`**.

Убраны утверждения, что captor технически не работает в stubbing, что несколько matcher-вызовов требуют ручного счётчика и что имя API гарантирует подробный diff. Разделены условие сопоставления и последующие assertions, getValue/getAllValues, рекомендация verification и техническая возможность stubbing. Добавлено ограничение захвата ссылок: изменяемый аргумент не превращается в глубокий снимок.

Источники **Mockito 5.11.0**: [ArgumentCaptor](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/ArgumentCaptor.java), [ArgumentMatcher](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/ArgumentMatcher.java). В JavaDoc captor рекомендуется для verification по читаемости и локализации дефекта. Техническая возможность stubbing утверждается в пределах исполненной версии 5.11.0, а не как рекомендация обходить стиль API во всех версиях.

**5 tests PASS / 0 failed**, javac и JVM exit 0: два теста полного Java-блока Q39 скопированы без изменений; три support probes подтверждают captor в stubbing, `argThat` с times(2) и сохранение ссылки на изменяемый объект. Код сначала исполнен из candidate.md, затем этот же файл побайтно применён к репозиторию с проверкой входного SHA. Старые probes отдельно сохранены; они не выдаются за исполнение прежнего ошибочного примера.

Конфигурация: JDK **21.0.10**, кешированные Mockito/core и Mockito Jupiter **5.11.0**, Jupiter **5.10.3**, Platform **1.10.3**, Byte Buddy/agent **1.14.18**, Objenesis **3.3**. Agent передан только на старте дочерней JVM. Зависимости не загружались, настройки приложений не менялись. Full project/Gradle/CI, Spring, Kotlin, другие версии, varargs matrix и concurrency не проверялись.

Schema **PASS 45/45**, semantic **NOT_RUN**. Whole-topic linkage **FAIL: 10 errors / 21 warnings / 26 manual review**, exit 1. Q39 — **0 errors / 0 warnings**. После усиления Q39 появился `NEIGHBOR-LEAK Q17→Q39` (own 0.367, neighbor 0.5); Q17 в этот пакет не входит и требует отдельной содержательной проверки. Общий FAIL сохраняется и не объявляется приёмкой. Правила и пороги не менялись, терминология ради overlap не добавлялась.

Структурная проверка: изменён только Q39, остальные **44** ответа побайтно сохранены. Все 45 заголовков, ID, metadata, TOC и See also сохранены. Frozen-validation содержит точные Markdown/MCQ и неизменённый linkage-скрипт с прежними относительными путями. Артефакты `/tmp/mockito-theory-captor-20261105/`: before/candidate/seed-before/seed-postcheck, q39-body, code-extraction, structural-check, runtime/execution с SHA JAR, schema/linkage/diagnostic-summary. Исходники probes и runner сохранены рядом с execution.

Тема целиком остаётся **NEEDS_REPAIR**, FINAL **NOT_RUN**. Пакет подготовлен без staging после освобождения индекса; для очередного коммита нужна координационная передача индекса от MCQ.
