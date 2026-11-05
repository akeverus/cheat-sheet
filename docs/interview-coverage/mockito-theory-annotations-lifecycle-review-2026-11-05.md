# Mockito Q7/Q8/Q43/Q45 — annotations и lifecycle

Дата: 2026-11-05. `interview-writer / SEMANTIC_UPDATE`, четыре полных ответа и **16 вариантов MCQ с sections** прочитаны. Исправлены Q7/Q8/Q43/Q45; суммарно автором исправлены **26 из 45** ответов, другие 19 этим набором не исправлялись. Это авторская сверка с открытыми ключами, не independent acceptance, blind Phase A/B или FINAL. JSON и индекс не редактировались.

Входная теория SHA `01484c268dcbb842415d8c895b03ee406138cc15962bd619e46b00ffffa136ec`, выходная SHA **`c3e307a42442cf73263bf8bd0d2ad56c36fd59fe3043801065e714923f28a511`**. Seed SHA **`2cc16d5379c06898bc20e70d96fd289465ea6bb46591dd9e1e68a751b173c6ef`**; выбранные четыре per-Q не менялись между начальным и итоговым снимком. Ключи D/C/A/A и основные механизмы согласуются с теорией.

## Что подтверждено и исправлено

| Q | Результат |
|---|---|
| 7 | Полный пример с обработкой Captor, Act/assert и generic List; аннотация не объявлена обходом стирания/глубокой runtime-проверкой элементов. Type-inferred captor() показан отдельно от полевой декларации. |
| 8 | Полный ручной lifecycle openMocks допустим в Jupiter. AutoCloseable закрывается явно, но это не MockitoSession strictness и не reset всех сохранённых объектов. Расширения не запрещены одним наличием чужого базового класса. |
| 43 | Поддержка **и Mock, и Captor** в 5.11.0 сохранена и подтверждена. Вместо неявных OrderRequest/Service fixtures дан полный исполняемый пример с конструктором, BeforeEach, параметрами метода и явным SUT wiring. Параметры не объявлены гарантией полной изоляции. |
| 45 | MockitoSettings сама регистрирует MockitoExtension; проверены class default, method override и локальная mock strictness при lookup. LENIENT не выключает verify; выдуманный platform parameter не стал API настройки. |

Первичные источники **5.11.0**: [MockitoSettings](https://github.com/mockito/mockito/blob/v5.11.0/subprojects/junit-jupiter/src/main/java/org/mockito/junit/jupiter/MockitoSettings.java), [MockitoExtension](https://github.com/mockito/mockito/blob/v5.11.0/subprojects/junit-jupiter/src/main/java/org/mockito/junit/jupiter/MockitoExtension.java), [CaptorAnnotationProcessor](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/configuration/CaptorAnnotationProcessor.java), [GenericMaster](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/internal/util/reflection/GenericMaster.java), [ArgumentCaptor](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/ArgumentCaptor.java), [MockitoAnnotations](https://github.com/mockito/mockito/blob/v5.11.0/src/main/java/org/mockito/MockitoAnnotations.java). Поддержку параметров нельзя выводить из памяти о другом выпуске: source содержит CompositeParameterResolver с MockParameterResolver и CaptorParameterResolver. Наличие обоих дополнительно подтверждено zip inventory и javap **реального кешированного Mockito Jupiter 5.11.0 JAR**, SHA `a30ea4fe0484e54f64cdc15269a6c6ff1bd89bc26a0d41e4c27cb91cb78dc548`.

## Исполненная проверка

Итог **14 outer tests PASS / 0 failed**, javac/JVM exit 0. Четыре полных Java-блока Markdown скопированы **без изменений** и дали **8** успешных тестов (2 + 1 + 1 + 4). Ещё **6** outer probes проверили:

- polluted List<Integer> захватывается полем Captor<List<String>> как подходящий raw List; чтение элемента как String даёт ожидаемый ClassCastException;
- close ресурса openMocks сохраняет обычный mock и его stub в поле; закрытие не является универсальным reset;
- default MockitoExtension отвергает unused stub, в то время как ручной openMocks/close сам по себе не включает такой же строгий session lifecycle;
- методовая STRICT_STUBS переопределяет LENIENT класса и отвергает unused stub при завершении сессии;
- передача mockito.strictness=LENIENT через Launcher не меняет default расширения;
- Captor и Mock поддерживаются также в параметрах конструктора теста в этой конфигурации.

Четыре outer probes запускают **пять вложенных fixtures**: две успешно проходят, три дают ожидаемый UnnecessaryStubbingException, проверенный по типу. Эти три внутренние ошибки — контрпримеры, не успешность исходного unused-stub кода. Они не прибавляются к числу 14 как дополнительные PASS. Первоначальный зелёный запуск 13 outer tests сохранён как execution-first13.json; после добавления проверки close-state финальные 14 включают те же 13, а не суммируются с ними.

Конфигурация: JDK **21.0.10**, кешированные Mockito/core и Mockito Jupiter **5.11.0**, Jupiter **5.10.3**, Platform **1.10.3**, Byte Buddy/agent **1.14.18**, Objenesis **3.3**. Agent передан только при старте дочерней JVM. Зависимости не загружались; настройки приложений и глобальные provider-файлы не менялись. Full project/Gradle/CI, Spring, Kotlin/Android, другие версии, custom resolvers/annotation engines, полная generic/lifecycle/maker matrix, performance/concurrency — **NOT_RUN**. Закрытие обычного mock не обобщается на все ScopedMock/custom resource случаи.

## Согласование и диагностика

Текущий MCQ Q43 правильно утверждает поддержку Captor-параметров в 5.11.0; это не дефект и не повод удалить её из теории. Владельцу JSON остаётся узкое уточнение **Q8 B.if_it_were_true**: возвращаемый тип назван Closeable вместо AutoCloseable, а факт возврата closeable-объекта сам по себе не доказывает отсутствие автоматического teardown. Лучше сослаться на отсутствие автоматического lifecycle в openMocks и необходимость связать закрытие с ним, сохранив правильный ключ C.

Schema **PASS 45/45**, semantic **NOT_RUN**. Whole-topic linkage **FAIL: 7 errors / 20 warnings / 24 manual-review blocks**, exit 1. Выбранные четыре: **0 errors / 1 warning**, Q43 novel-term openMocks в неверной альтернативе. Полный Q43-пример реально исполняется под MockitoExtension без openMocks, прямо опровергая требование дополнительной ручной инициализации; механизм ручного API отдельно объяснён Q8. Термин ради overlap в Q43 не вставлялся. Правила/пороги не менялись, независимая приёмка не присвоена.

Структурный guard: только Q7/Q8/Q43/Q45 изменены, остальные **41** секции побайтно сохранены, включая прежние исправления и неизменённые trailing spaces Q44. Все 45 заголовков, ID, metadata, TOC и See also сохранены. SHA четырёх Java-блоков и теории совпадают с успешным execution, `git diff --check` — PASS.

Артефакты `/tmp/mockito-theory-annotations-lifecycle-20261105/`: before/seed-before/seed-postcheck, original-findings до правок, четыре тела, cached-resolver-check и javap, source log, structural-check, runtime с точными классами/runner/вложенными fixtures и execution с SHA JAR/блоков, schema/linkage/diagnostic-summary, frozen-validation с точной парой и неизменённым linkage-скриптом. Веб-выдержки не являются полными byte snapshots. Точный пакет двух файлов перечисляет `/tmp/prepared-mockito-theory-annotations-lifecycle-20261105.json`.

Следующие приоритеты теории: Q6/Q31 (spy identity, создание и ограничения упрощённого «настоящего объекта»). Тема целиком **NEEDS_REPAIR**, FINAL **NOT_RUN**. Staging и коммиты не выполнялись; индекс находится у MCQ.

## Сверка при передаче индекса

После MCQ-коммита HEAD **`ba4f7c1001ceda4a30c637cd150c1e97b2b1805c`** индекс передан теории. Пустой индекс, SHA обоих подготовленных файлов, полный seed 2cc16d53 и все четыре per-Q SHA проверены. Код четырёх примеров совпадает с успешным execution 14 outer tests; повтор неизменённого набора не требовался. Три ожидаемых внутренних отказа остаются отдельными контрпримерами, а не PASS исходного unused-stub кода.

Коммитный пакет ограничен Mockito Markdown и этим отчётом, без JSON или других файлов. Точное состояние сохранено в `/tmp/mockito-annotations-transfer-20261105/`, финальные файлы/SHA — в `/tmp/prepared-mockito-theory-annotations-lifecycle-20261105-v2.json`. Ограничения независимой приёмки и runtime выше сохраняются.
