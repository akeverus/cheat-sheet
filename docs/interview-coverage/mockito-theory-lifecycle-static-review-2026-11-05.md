# Mockito — lifecycle, static scopes и реальные методы

Дата: 2026-11-05. Узкий `interview-writer / SEMANTIC_UPDATE`: новые ответы Q23/Q25/Q26/Q41/Q42 и уточнение типизации Q10. Это продолжение [первой группы Q3/Q5/Q9/Q10](mockito-theory-q3-q5-q9-q10-review-2026-11-05.md). Суммарно исправлены **9 из 45** ответов, остальные 36 сохранены побайтно относительно исходного Markdown. Это авторская проверка с открытыми ключами, не independent acceptance исправлений, не blind Phase A/B и не FINAL.

Вход этой группы: Markdown SHA `4639c1e30b1edb0f4bb697addb3ea75f4eae5cdd9c974371bc1ee1b241e5bb88`, seed SHA `8e062d5930ad653bdc8bfb454f5b3ae41c061272ea555e35b77bda3ee06f0d04`. Сначала прочитаны все 20 вариантов пяти новых вопросов с sections. Во время работы владелец MCQ изменил все 45 вопросов: новый прочитанный seed SHA **`2d58c1ed06f33648b89b508e0368f203117a97b085adc0d46f8b7be7668917b2`**. Затем перечитаны все **36 вариантов девяти затронутых вопросов** с sections. При следующем изменении seed повторно сверены изменённые поля семи затронутых вопросов; итоговый прочитанный снимок — SHA **`f4e36481f3ffcc631396845cd95fd8d12fb8922ddb1ae56f98c13aba98b69c18`**. Остальные новые MCQ этим проходом не приняты. JSON и индекс не редактировались.

## Изменения и источники

| Q | Что исправлено |
|---|---|
| 23 | Завершение MockitoSession отделено от reset всех сохранённых объектов. Описано удержание старой final constructor dependency при PER_CLASS и способ пересоздать SUT с текущим mock. Пример действительно использует stub. |
| 25 | UUID.fromString больше не вызывается внутри активного mockStatic UUID для подготовки значения. Основной пример использует свой IdSource, проверяет вызов и закрытие. Close обязателен, lexical try не единственный lifecycle. |
| 26 | Вместо недопустимого mock(String.class) показан свой final-тип. Enum отделён от String/Class/wrappers, maker и настройки — от номера артефакта. Убрана неизмеренная универсальная оценка скорости. |
| 41 | Независимые scopes одного класса могут сосуществовать в разных потоках; повторная активная регистрация конфликтует в одном потоке. Показаны worker до/во время/после собственного scope и восстановление main scope. |
| 42 | Mock признан существующим объектом. Вызов реального метода отделён от создания и выполнения конструктора; показан useConstructor. Для spy уточнены копирование состояния, общие ссылки и возможность null. |
| 10 | По новому MCQ уточнено отличие типизированного thenReturn от runtime-проверки doReturn(Object); Java-фрагмент ранней группы не менялся. |

Исследован **Mockito 5.11.0**: [MockitoExtension](https://github.com/mockito/mockito/blob/v5.11.0/subprojects/junit-jupiter/src/main/java/org/mockito/junit/jupiter/MockitoExtension.java), [MockedStatic](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/MockedStatic.java), [InlineDelegateByteBuddyMockMaker](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/creation/bytebuddy/InlineDelegateByteBuddyMockMaker.java), [DefaultMockitoPlugins](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/configuration/plugins/DefaultMockitoPlugins.java), [MockSettings.useConstructor](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/MockSettings.java), [Spy](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/Spy.java), [Mockito](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/Mockito.java), [OngoingStubbing](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/stubbing/OngoingStubbing.java). Общие исторические замечания JavaDoc о невозможности final stubbing не перенесены на default inline: maker-specific исходники и исполненный пример подтверждают поддержку в исследованной конфигурации.

## Исполненная проверка

JDK **21.0.10**, кешированные Mockito/Mockito Jupiter **5.11.0**, Jupiter **5.10.3**, Platform **1.10.3**, Byte Buddy/agent **1.14.18**, Objenesis **3.3**. Основной набор: **13 tests PASS / 0 failed**. Все пять новых полных Java-блоков Markdown скопированы, скомпилированы и исполнены **без изменений**; imports, имена классов и тестовые тела сохранены. Дополнительные fixtures проверили:

- реальный MockitoExtension: два ordered-теста PER_CLASS получают новый Mock в поле, но существующий InjectMocks SUT хранит предыдущую final dependency и её использованный stub;
- два теста с явным пересозданием SUT в BeforeEach используют текущую зависимость без прежнего stub;
- обычный inline enum mock и отказы для String/Integer/Class;
- прежний UUID.fromString внутри scope возвращает null, подготовленное до scope значение работает;
- обычный mock обходит конструктор, а thenCallRealMethod не инициализирует поля и может дать NPE;
- spy имеет отдельное скопированное поле, но общую ссылку на List, и не исправляет null-состояние.

Отдельная проверка Q10: **1 test PASS** для runtime WrongTypeOfReturnValue у doReturn(String) для int-метода; соответствующий thenReturn(String) дал **ожидаемую ошибку javac**. Это отдельный запуск, не часть числа 13. Ранние десять успешных probes и две ожидаемые ошибки компиляции сохранены; SHA их Q3/Q10 Java-фрагментов сверены на новой теории.

Ещё одна отдельная fixture дословно исполнила метод из Q23 C.example нового MCQ, добавив imports, interface и extension registration. Она дала **1 expected FAILED test / 0 passed**, UnnecessaryStubbingException; внешний runner проверил именно этот исход. Этот отрицательный пример не посчитан успешным тестом. Артефакт `mcq-followup/execution.json` содержит seed SHA и исходную строку example.

Agent передан только при старте дочерних JVM. Настройки Mac и запущенных приложений не менялись; установки/скачивания, Docker и обращения к внешним сервисам отсутствовали. UUID mock использован только как локальный regression probe прежнего примера; основной учебный пример работает со своим типом.

## Согласование с прочитанным MCQ и открытые поля

В прочитанном снимке f4e36481 ключи Q3/Q5/Q9/Q10/Q23/Q25/Q26/Q41/Q42 сохраняются и основные механизмы согласуются с обновлённой теорией. Закрытые формулировки старого seed не считаются текущими дефектами. Остались конкретные места владельцу JSON:

1. **Q23 C.sections.example:** `@Test void test(@Mock PaymentGateway gateway) { when(gateway.charge(any())).thenReturn(true); }` не использует stub и падает при завершении STRICT_STUBS. Нужен Act/assertion с этим вызовом либо пример без лишнего stubbing. Теория содержит полностью исполняемый вариант.
2. **Q42 B.sections.source_of_confusion:** заключение «Mockito лишь делегирует вызов … не материализуя сам объект» сохраняет старую ошибку. Mock-объект уже существует; thenCallRealMethod не создаёт его повторно и не запускает конструктор.
3. **Q26 C.sections.source_of_confusion:** обобщение «наследование блокирует только private и final» следует сузить к обсуждаемому ограничению override final, не объявляя исчерпывающий перечень всех правил доступа/наследования.

Первые два пункта подтверждены механизмом/исполнением; третий — редакционная рекомендация сузить широкую формулу, не новая полная проверка JLS. Нужна повторная сверка после финальной записи MCQ. Новая независимая приёмка пары не присвоена. При последней адресной проверке этих трёх полей live seed уже имел SHA `5c74c8ae3e63e44a08d3dd7f099b6c6961e9e18f0ac9b7d4a0595fa2f6302dc7`; все три формулировки оставались прежними. Это проверка трёх полей, не приёмка всех вариантов на новом SHA.

Schema **PASS 45 Q / 45 B**, semantic `NOT_RUN`. Финальная диагностика закреплена на снимке f4e36481: whole-topic linkage **FAIL: 7 errors / 22 warnings / 24 manual review**, exit 1; пять новых вопросов — **0 errors / 1 warning** (Q23 openMocks в неверной альтернативе), совокупные девять — **0 errors / 3 warnings** (также Q3 кавычки и Q9 doNothing). Авторская сверка: механизм extension/BeforeEach опровергает обязательность отдельной ручной инициализации; типизация/void-граница пояснены Q10 и проверены компиляцией. Термины ради scanner overlap не вставлялись. Две финальные проверки live SHA остановились при параллельной записи seed. Поэтому exact Markdown, snapshot JSON и неизменённый скрипт linkage скопированы в `/tmp/mockito-theory-lifecycle-static-20261105/frozen-validation/` с прежними относительными путями. SHA скрипта совпадает с репозиторием, код и пороги не менялись; исходный JSON не перезаписывался. Структура, все 45 заголовков, TOC, See also и metadata относительно входа группы сохранены. `git diff --check` проверяет изменённые строки; прежние trailing spaces неизменённого Q44 оставлены.

Артефакты `/tmp/mockito-theory-lifecycle-static-20261105/`: before/seed-before, original-findings, seed-at-postcheck/seed-at-final-guard, frozen-validation, latest-handoff-field-check, web-sources, runtime с пятью точными файлами и execution.json, mcq-followup, static-checks, schema/linkage logs. Старый пакет и исходные отчёты сохранены снимками; финальные файлы и per-Q SHA перечислены новым совокупным манифестом. Веб-выдержки не являются полными byte snapshots.

Full project/Gradle/CI, Spring context, Kotlin, Android, coverage/agent compatibility matrix, performance, native/private/serialization variants и остальные 36 ответов этим пакетом не исполнялись и не исправлялись. Тема целиком **NEEDS_REPAIR**, FINAL **NOT_RUN**.

## Сверка при передаче индекса

После MCQ-коммита HEAD `3eefb0d23761ed2dd1a17aac9525afd961ae155e` индекс передан владельцу теории. Текущий seed SHA **`a46a887f5fd1db34adef5546a50de23269146fbfb4d3c234be094cf8c5ee53e4`**. По per-Q SHA в выбранных девяти изменились только Q23/Q26/Q42 относительно прочитанного f4e36481. Все изменённые поля перечитаны: неверная Q23 B теперь утверждает повторный вызов конструктора прежнего SUT, C.example использует stub; Q26 C.source_of_confusion сужен; Q42 B.source_of_confusion правильно описывает существующий mock-объект. Три замечания предыдущей секции устранены на этом SHA; они сохранены выше как история проверенного входа.

Новый C.example Q23 скопирован без изменения метода, Gateway/Service взяты из теории: **1 test PASS** на тех же кешированных версиях и с agent только дочерней JVM. Это отдельный проверочный запуск; прежняя ожидаемо падавшая fixture относится к старому примеру и не описывает текущий seed. Код всех пяти примеров теории и двух ранних исполняемых фрагментов сверяется по прежним SHA, повтор всего набора не требовался.

Schema повторно **PASS 45 Q / 45 B**, linkage на текущей паре **7 errors / 22 warnings / 24 manual review**, выбранные девять **0 errors / 3 warnings**. Заголовки, ID, ключи и остальные per-Q SHA выбранной группы проверены. Материалы этой группы согласованы автором по смыслу; общий статус темы NEEDS_REPAIR и ограничения независимой приёмки сохраняются.

Артефакты финальной передачи: `/tmp/mockito-theory-final-transfer-20261105/` — seed, предыдущий манифест и отчёт, current-q23-runtime.json с точным методом, schema/linkage logs и diagnostic-summary. Финальные три файла и SHA фиксируются `/tmp/prepared-mockito-theory-cumulative-20261105-v3.json`. JSON не редактировался; коммит включает только теорию и два её отчёта.
