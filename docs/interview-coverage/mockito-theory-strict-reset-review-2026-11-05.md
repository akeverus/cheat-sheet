# Mockito — reset, strictness, lenient и последовательные Answers

Дата: 2026-11-05. `interview-writer / SEMANTIC_UPDATE`, четыре ответа **Q24/Q28/Q29/Q30**. Продолжение [проверки первых девяти ответов](mockito-theory-lifecycle-static-review-2026-11-05.md): суммарно исправлены **13 из 45**. Авторская сверка с открытыми ключами не является blind Phase A/B, independent acceptance или FINAL темы. Остальные 32 ответа этим пакетом не исправлялись.

Вход: HEAD `88b392616b0a2e3720b13a286c5f00900cfd353a`, Markdown SHA `6555b853197654d6700c6caaf74423b2bafa7f5569d383b7028e5e481ccf51f9`, seed SHA `a46a887f5fd1db34adef5546a50de23269146fbfb4d3c234be094cf8c5ee53e4`. Прочитаны четыре ответа и все 16 соответствующих MCQ-вариантов с sections. После параллельных исправлений владельца MCQ повторно прочитаны все 16 вариантов снимка seed SHA **`226229acf2538d894a1d22f990b739b89ebaee1cdba84e12d2a5cd66471bdb6c`**. В выбранной группе изменились Q24/Q28/Q30; Q29 побайтно прежний. JSON и индекс не редактировались. Итоговая теория этой группы: SHA **`16f6c26ff5603fd2f8bb98d6f2508c0f1d8ebd84912a47bdbf05d9857b6c8be0`**.

## Исправления и первоисточники

| Q | Проверенное изменение |
|---|---|
| 24 | Reset удаляет stubs и историю, сохраняя объект и creation settings. Не запускает конструктор и не восстанавливает реальные поля spy. ClearInvocations сохраняет stubs. Убрана общая рекомендация ручного reset для любого MockBean: Boot 3.3.2 имеет default AFTER. |
| 28 | Strict stubbing выполняется во время тестового прогона. Вместо несуществующего StubbingArgumentMismatchException указан PotentialStubbingProblem. Разделены LENIENT/WARN/STRICT_STUBS, default MockitoExtension 5.11.0 и программный mock(), strictness и default Answer. Показана автоматическая verification использованного strict stub. |
| 29 | Per-stub lenient исключает выбранный stub из unused и mismatch проверок, сохраняя verify и валидацию типов/API. Описан приоритет более локальных настроек над session default. Полный пример имеет ветки с использованием и без использования общего стаба. |
| 30 | После исчерпания предыдущих Answers повторно применяется последний обработчик: константа повторяется, динамический Answer вычисляет новый результат, throwing Answer снова бросает. Отдельные when не образуют очередь; число вызовов проверяется отдельно. |

Версия **Mockito 5.11.0**: [MockUtil.resetMock](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/util/MockUtil.java), [Strictness](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/quality/Strictness.java), [StrictnessSelector](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/StrictnessSelector.java), [DefaultLenientStubber](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/DefaultLenientStubber.java), [StubbedInvocationMatcher](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/StubbedInvocationMatcher.java). Default расширения и source-file heuristic дополнительно разобраны в предыдущих отчётах по [MockitoExtension](https://github.com/mockito/mockito/blob/v5.11.0/subprojects/junit-jupiter/src/main/java/org/mockito/junit/jupiter/MockitoExtension.java) и [DefaultStubbingLookupListener](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/junit/DefaultStubbingLookupListener.java). Для Spring использован [MockBean Boot 3.3.2](https://github.com/spring-projects/spring-boot/blob/v3.3.2/spring-boot-project/spring-boot-test/src/main/java/org/springframework/boot/test/mock/mockito/MockBean.java); default AFTER также подтверждён javap по кешированному spring-boot-test 3.3.2. Это проверка метаданных аннотации, не исполнение Spring context/listener.

## Исполнение

JDK **21.0.10**, кешированные Mockito/core и Mockito Jupiter **5.11.0**, Jupiter **5.10.3**, Platform **1.10.3**, Byte Buddy/agent **1.14.18**, Objenesis **3.3**. Итог: **16 tests PASS / 0 failed**. Четыре полных Java-блока Markdown скопированы и исполнены без изменений: **6 тестов** (1 + 1 + 2 + 2). Отдельные **10 probes** подтвердили:

- reset сохраняет идентичность, имя и custom default Answer, но удаляет stub/history; reset spy не восстанавливает изменённое поле и не повторяет конструктор;
- unused strict stub даёт ожидаемый UnnecessaryStubbingException на finishMocking; WARN сообщает Unused через Mockito logger без такого отказа;
- per-stub lenient не ослабляет соседний обычный unused stub; mismatch strict stub из отдельного production source file даёт ожидаемый PotentialStubbingProblem;
- lenient mismatch возвращает default, но явный verify неприсутствующего вызова продолжает падать; несовместимый doReturn по-прежнему даёт WrongTypeOfReturnValue;
- последний throwing Answer повторяет исключение; отдельный повторный stubbing перекрывает предыдущий, а не образует очередь.

Первый запуск остановился на компиляции вспомогательной fixture: `assertDoesNotThrow(session::finishMocking)` неоднозначен для перегруженных Executable/ThrowingSupplier. Во вспомогательном коде ссылка заменена явной void-лямбдой; четыре примера Markdown не менялись. Первый протокол сохранён как `runtime/execution-first.json` и не считается успешным прогоном. Повторный javac и JVM завершились с кодом 0, все 16 тестов прошли.

Agent передан только при старте отдельной дочерней JVM. Установки и загрузки зависимостей не выполнялись; параметры пользовательских приложений не менялись. Full project/Gradle/CI, Spring integration, другие версии Mockito, Kotlin, производительность и порядок вызовов при реальной конкуренции не проверялись.

## Согласование и диагностика

На прочитанном снимке 226229ac ключи Q24=A, Q28=A, Q29=C, Q30=B и объяснения согласуются с теорией. Прежняя неверная контрфактическая фраза Q30 A о невозможности компиляции varargs уже заменена владельцем MCQ примером двух отдельных when. Сохранять её как текущий дефект оснований нет. Это содержательная авторская сверка выбранных четырёх вопросов, а не приёмка всех 45.

Schema **PASS: 45 questions / 45 blocks**, semantic gate **NOT_RUN**. Whole-topic linkage **FAIL: 8 errors / 21 warnings / 25 manual review**, exit 1. В выбранных четырёх — **1 error / 0 warnings**: `NEIGHBOR-LEAK q29b0`, Q29 own 0.324 против Q28 0.471. После расширения Q28 общий словарь strictness сильнее пересекается с вариантами Q29. Ручная сверка всех четырёх вариантов показывает, что Q29 непосредственно объясняет per-stub scope, unused/mismatch, сохранение verify/API validation и отличие session default; это не требует ответа соседнего вопроса. Сигнал сохранён, пороги и скрипт не менялись, текст для lexical overlap не добавлялся. Диагностика не объявлена зелёной; новая независимая приёмка темы не присвоена.

При параллельной записи MCQ проверка закреплена на точном снимке seed и Markdown в `/tmp/mockito-theory-strict-reset-20261105/frozen-validation/`. Скрипт linkage скопирован туда побайтно (SHA `e43fffaf40c28ec26ad3522b2047a299c1fe70bcf9ddba3cf2ff126aca7cd5cf`), стандартные относительные пути сохранены. Исходные JSON и скрипт не перезаписывались.

Структурная проверка: изменены только Q24/Q28/Q29/Q30; другие **41** секции, включая ранее проверенные девять, побайтно сохранены относительно входа группы. Все 45 заголовков, их ID, TOC, metadata и See also сохранены. `git diff --check` для изменённых строк — PASS. Артефакты `/tmp/mockito-theory-strict-reset-20261105/`: before/seed-before/seed-postcheck, original-findings, web-sources-1/2, cached-boot-api и javap, structural-check, schema/linkage/diagnostic-summary, run-checks и runtime/execution с SHA точных Java-фрагментов и кешированных JAR. Веб-выдержки не являются полными byte snapshots.

Тема целиком остаётся **NEEDS_REPAIR**, FINAL **NOT_RUN**. Пакет подготовлен без staging; индекс находится у MCQ.
