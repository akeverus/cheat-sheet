# Mockito Q3/Q5/Q9/Q10 — первая группа исправлений теории

Дата: 2026-11-05. Режим `interview-writer / SEMANTIC_UPDATE`. Узкий авторский пакет: прочитаны четыре ответа и все 16 соответствующих MCQ-вариантов с sections, сохранены первоначальные находки до правок. Основание — [полный аудит владельца MCQ](../mcq-quality/llm-first/reviews/testing/mockito-peer-review-2026-11-05.md). Это **4 из 45 вопросов**, не whole-topic acceptance. Остальные 41 ответ сохранены побайтно; их ошибки и замечания аудита остаются открыты.

Входной Markdown SHA `2592267455f276cab6ab5c1d1ecf498da90fa7cb7d255e7266a7a3bdfd169c09`, seed SHA `8e062d5930ad653bdc8bfb454f5b3ae41c061272ea555e35b77bda3ee06f0d04`. Ранее существовавшего diff теории Mockito не было. Заголовки всех 45 Q, содержание и See also сохранены; во frontmatter изменена только дата updated. JSON и индекс этим пакетом не менялись.

## Подтверждённые исправления

- **Q3:** короткое `mock(Type.class)` требует доступности имени метода. Это та же фабрика, что `Mockito.mock(Type.class)`, а не третий механизм без импорта. Отдельный type-inferred overload `Mockito.mock()` существует с 4.10.0; тип UserRepository задаётся присваиванием, не `var`. Стандартные результаты зависят от return type, а не сводятся к null для всех ссылок.
- **Q5:** выбирается самый большой конструктор, не самый большой полностью удовлетворимый. Отсутствующий ссылочный аргумент может стать null; после успешного конструктора нет обязательного дополнительного setter/field injection. Исключение конструктора не игнорируется. InjectMocks не обещает внедрение всех моков или построение произвольного графа.
- **Q9:** обычный default Answer отделён от STRICT_STUBS и PotentialStubbingProblem. Уточнены equals/matchers, эвристика source file и различие постоянного последнего thenReturn с динамическим Answer.
- **Q10:** doReturn(value) не подходит для void: выражение компилируется, но Mockito отклоняет настройку. Это отличается от ошибки компиляции when(voidCall()). Показан воспроизводимый spy над пустым списком; уточнено вычисление аргумента до when и влияние текущего Answer.

Исследована версия **Mockito 5.11.0**, а не неопределённая последняя: [Mockito.java](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/Mockito.java), [InjectMocks](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/InjectMocks.java), [DefaultStubbingLookupListener](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/junit/DefaultStubbingLookupListener.java), [Strictness](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/quality/Strictness.java), [Returns.validateFor](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/answers/Returns.java), [ReturnsEmptyValues](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/stubbing/defaultanswers/ReturnsEmptyValues.java).

## Исполненные проверки

Кешированные Mockito **5.11.0**, Jupiter **5.10.3**, Platform **1.10.3**, Byte Buddy/agent **1.14.18**, Objenesis **3.3**, JDK **21.0.10**. Итог: **10 JUnit tests PASS**, отдельно **две ожидаемые ошибки javac**. Проверены три фабричные формы и вывод Object для var, необходимость обработки Mock-аннотации, largest-constructor/null/отсутствие fallback setter, InjectMocksException с исходной ошибкой конструктора, plain default и строгий mismatch из другого source file, lenient без отмены verify, точный spy-фрагмент Q10, runtime-отказ doReturn для void и повтор последнего thenReturn.

Из Q3 без изменения скопированы три фабричных объявления, из Q10 — весь Java-блок; harness добавляет импорты, интерфейс и тестовую обёртку. Остальные fixtures — отдельные API probes, а не полная компиляция доменных примеров Q5/Q9. Byte Buddy agent указан только при старте дочерней JVM. Глобальные настройки Mac и приложения не менялись; установки, скачивания и Docker отсутствовали.

Первый extraction-check ошибочно захватил также аннотированное поле Q3 и остановился до javac; выбор ограничен первым code fence, Markdown не менялся. Первый исполняемый набор дал **9 passed / 1 failed**: harness ожидал WantedButNotInvoked, но фактический вызов того же метода с другим аргументом дал ArgumentsAreDifferent. Уточнена проверка diagnostic в harness; теория не менялась. Оба результата сохранены (`runtime/execution-first.json` и `runtime/execution.json`); первый прогон не считается успешным.

Schema **PASS 45 Q / 45 B**, semantic `NOT_RUN`. Whole-topic linkage **FAIL: 10 errors / 36 warnings / 33 manual review**, exit 1. В выбранных четырёх вопросах **0 errors / 3 warnings**: Q3 STEM-MISMATCH только по обратным кавычкам, Q3 MockClass/new в неверной альтернативе и Q10 C low coverage 0.182. Авторская сверка: фабрика/обработанная аннотация опровергают обязательное ручное наследование; механизм spy и отказ void опровергают полную взаимозаменяемость. Пороги, заголовки и формулировки ради сканера не менялись. Структура и `git diff --check` изменённых строк — PASS. Пять прежних строк с trailing spaces в неизменённом Q44 оставлены без правки; финальный helper сначала ошибочно проверял отсутствие whitespace во всём файле, затем был ограничен diff.

## Открытая синхронизация MCQ

На указанном seed SHA **Q3 C, Q5 B/C, Q9 A/B и Q10 B/C/D с sections остаются ошибочными или противоречат исправленной теории**. JSON оставлен владельцу MCQ; пакет теории нельзя считать синхронизированной учебной парой. После изменения JSON нужны сверка точных per-Q SHA и новые диагностики. Остальной приоритет аудита: Q23/Q25/Q26/Q28/Q30/Q34/Q41/Q42/Q44/Q45 и связанные ответы. Тема целиком **NEEDS_REPAIR**; свежие blind Phase A/B, независимая приёмка исправлений и FINAL **NOT_RUN**.

Артефакты `/tmp/mockito-theory-sync-20261105/`: before/head-theory/seed-before, first-four-original-findings, web-sources, runtime с кодом/результатами и SHA JAR, static-checks, schema/linkage logs. Веб-выдержки не являются полными byte snapshots исходников. Точные финальные SHA передаются манифестом. Full project/Gradle/CI, Spring/Kotlin, MockitoExtension lifecycle и остальные 41 вопрос не исполнялись в этом пакете.

## Последующий проход

Этот отчёт фиксирует первую группу на указанном входном SHA. Утверждение о 41 неизменённом ответе относится к её завершению. В [следующем проходе](mockito-theory-lifecycle-static-review-2026-11-05.md) добавлены Q23/Q25/Q26/Q41/Q42 и уточнена типизация Q10; совокупный охват — 9 ответов, 36 остальных сохранены. Владелец MCQ переписал seed: прежние открытые формулировки следует оценивать по новому SHA и конкретным замечаниям следующего отчёта. Исходный отчёт и Markdown первой группы сохранены снимками в `/tmp/mockito-theory-lifecycle-static-20261105/`; старый манифест не переписан.
