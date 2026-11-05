# JUnit 5 — ревью исходной редакции и исправления MCQ

Дата: 2026-11-05. Codex, контекст координатора MCQ. Прочитаны Q1–Q15, все 60 вариантов с sections и соответствующая теория другого автора. Режим — **distinct-author independent open-key review** исходной редакции. Строгие слепые Phase A/B, workflow record и FINAL **NOT_RUN**. После коррекции исполнитель является её автором и не принимает новую версию независимо.

Исходный JSON `modules/quiz-app/src/main/resources/seed/mcq/testing/junit-interview.json`: SHA `82366ca65bba4f64e43ef92e2731d4661f33ba65a5076cfea672f0f97cea6b78`. Теория `cheatsheets/interview/testing/junit-interview.md`: SHA `e13634f9e61a07dcb5cc0715601d5e096c548fccec6720d99e8e813e97ac8167`; этой задачей не редактировалась.

Вердикт исходной версии **NEEDS_REPAIR**. Исправленный JSON SHA `b42ac5086995dc915a4f7afeb52de2d116cc354682bac918a0cfd37d45c00266`: изменены Q3/Q4/Q8/Q10/Q11/Q12/Q13/Q14. Q1/Q2/Q5/Q6/Q7/Q9/Q15 сохранены относительно авторского снимка. Все номера, block_idx, question_text, labels, order и correct сохранены. Исправленная пара **NEEDS_THEORY_SYNC**, новая independent acceptance **NOT_RUN**. Прежний авторский LLM_READY привязан к исходному SHA.

## Подтверждённые находки

1. **Q4 — зависимые проверки.** Запрет любых зависимых шагов в assertAll чрезмерен: guard и зависимое действие можно разместить в одном Executable, а независимые группы — рядом. Исходный wrong C допускал такое прочтение. Теперь C ложно обещает автоматический пропуск соседней лямбды после упавшего assertNotNull. A и sections ограничены обычными failures; unrecoverable errors прерывают исполнение. Локально проверены агрегирование двух failures и guard без NPE. [Assertions 5.10.3](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html).
2. **Q3/Q8 — источники и runtime.** ParameterizedTest не требует заранее фиксированных данных: MethodSource вычисляет аргументы при запуске и поддерживает не только Stream<Arguments>. ValueSource предоставляет также Class literals, один аргумент источника не исключает отдельный TestInfo через resolver. EnumSource без фильтра охватывает все константы; при names/mode гарантия зависит от отбора. [MethodSource](https://docs.junit.org/5.10.3/api/org.junit.jupiter.params/org/junit/jupiter/params/provider/MethodSource.html), [ValueSource](https://docs.junit.org/5.10.3/api/org.junit.jupiter.params/org/junit/jupiter/params/provider/ValueSource.html).
3. **Q8 — lifecycle динамических тестов.** Отличие объясняется фабрикой DynamicNode/Executable и lifecycle, а не возможностью runtime-данных. BeforeEach/AfterEach выполняются для TestFactory, без автоматического сброса полей между её детьми. Исполняемый пример подтвердил один setup и три ребёнка с общим instance state. [Dynamic Test Lifecycle 5.10.3](https://docs.junit.org/5.10.3/user-guide/#writing-tests-dynamic-tests), [TestFactory](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/TestFactory.html).
4. **Q11 — AssertJ/BigDecimal.** isEqualTo по умолчанию различает scale, для числового равенства нужен isEqualByComparingTo. Проверено на 1.0/1.00 с AssertJ 3.25.3. JUnit assertEquals тоже сообщает expected/actual: сравнение hasSize с assertTrue не характеризует все встроенные assertions. [AbstractBigDecimalAssert 3.25.3](https://github.com/assertj/assertj/blob/assertj-build-3.25.3/assertj-core/src/main/java/org/assertj/core/api/AbstractBigDecimalAssert.java).
5. **Q12 — конфигурация и блокировки.** Отдельный properties file не обязателен: параметры можно передать через Launcher или поддерживаемую конфигурацию среды запуска. SAME_THREAD не является глобальной блокировкой. ResourceLock координирует объявленные ключи: READ совместим с READ, READ_WRITE исключителен; внешние процессы этим не блокируются. Вариант D сокращён без изменения смысла после диагностики длины. [Parallel Execution](https://docs.junit.org/5.10.3/user-guide/#writing-tests-parallel-execution), [ResourceLock API](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/parallel/ResourceLock.html).
6. **Q14 — предел изоляции.** Новый test instance не сбрасывает static, разделяемые ссылки или внешние ресурсы и не гарантирует независимость порядка. Default PER_METHOD может переопределяться конфигурацией. Исполняемый пример показал свежий instance counter и сохранённую static-коллекцию. [TestInstance 5.10.3](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/TestInstance.html).

Также уточнены Q10 (собственные conditions не ограничены ОС/JDK; ошибка condition не равна штатному skipped) и Q13 (WebMvcTest исключает обычные сервисы из default scanning, но допускает осознанный Import вместо мока). [EnabledIf](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/condition/EnabledIf.html), [WebMvcTest Boot 3.3.2](https://github.com/spring-projects/spring-boot/blob/v3.3.2/spring-boot-project/spring-boot-test-autoconfigure/src/main/java/org/springframework/boot/test/autoconfigure/web/servlet/WebMvcTest.java). Версия Boot не обновлялась.

## Полный охват

| Q | Ключ и проверка альтернатив |
|---|---|
| 1 | C: Platform/Jupiter/Vintage; не поменянные роли, не один артефакт, не Jupiter Test API в Platform. |
| 2 | B: class-level BeforeAll при default lifecycle; не BeforeEach, не повтор класса, причина Disabled доступна. |
| 3 | A: параметризация и источник; EnumSource не требует ручного перечисления, ValueSource даёт один source argument, invocation видны отдельно. |
| 4 | A: агрегация обычных failures; не fail-fast группы, не авто-guard соседней лямбды, не сбор всех ошибок обычными assertions. |
| 5 | C: assertThrows принимает подклассы и возвращает исключение; assertThrowsExactly строго проверяет тип. |
| 6 | B: non-static Nested и внешний BeforeEach; свой setup допустим, иерархия сохраняется в дереве тестов. |
| 7 | B: MockitoExtension, Mock/InjectMocks; повторный openMocks не нужен, strict stubs проверяются по умолчанию. |
| 8 | A: фабрика создаёт runtime-узлы; не список одних аргументов, не compile-time cases, не только Stream. |
| 9 | A: несколько совместимых extensions; ParameterResolver участвует, один класс может реализовать несколько callbacks. |
| 10 | D: conditional execution и skipped; не assertions предметной логики, не Disabled по переменной, не failed из-за штатного отключения. |
| 11 | C: assertion library рядом с engine; готовые сообщения и специальные типы есть, запуск engine не заменяется. |
| 12 | D: opt-in конфигурация; Execution не включает глобальный механизм, default последовательный, ресурсные блокировки поддерживаются. |
| 13 | C: SpringExtension и slices; контекст кэшируется, сервисы не добавляются автоматически по зависимости контроллера. |
| 14 | D: количество test instances; PER_CLASS допускает non-static BeforeAll, стандартный default PER_METHOD. |
| 15 | B: публичное поведение устойчивее приватных деталей; число моков — сигнал для оценки, verify уместен для наблюдаемого взаимодействия. |

SFPL/ANSC/RU: просмотрены все альтернативы и локальные объяснения. В Q4 неверная модель auto-guard заменяет спорный общий запрет зависимых групп. В Q12 убрана обязательность файла без единственной уточняющей оговорки только у ключа. Question_text не добавляет скрытых условий; ключи и порядок не переставлялись ради формальных метрик. Самостоятельные corrections не получают независимый SFPL/ANSC/RU PASS.

## Проверки и ограничения

**15 executed tests, 15 passed, 0 failed** в отдельной JVM, JDK **21.0.10**, JUnit **5.10.3**, Platform **1.10.3**, AssertJ **3.25.3**, только кешированные JAR. Проверены assertAll, Class+TestInfo, три MethodSource-аргумента из параметра дочерней JVM, lifecycle трёх DynamicTest, два PER_METHOD instance и static state, BigDecimal equality, сообщения assertEquals. Ещё два теста исполнили тело TimingExtension, извлечённое из теории: добавлен лишь inert logging sink вместо необъявленного log. remove(START_TIME, long.class) в этой версии работает. Прогон последовательный; thread-safety extension в параллельном режиме не заявляется.

Это изолированные semantic probes, а не полный проект. Spring context/MockBean/JPA, Mockito strict-stubbing lifecycle, реальные ресурсные гонки, timeout interruption и внешний CI **NOT_RUN**. Код и существующие тесты проекта не менялись; ничего не устанавливалось и не скачивалось.

Schema **PASS 15 Q / 15 B**; parity **PASS**, max ratio **1.33** (Q3), correct-longest **2/15**, style guessability **0.278**. Anti-tell **0/15**; linkage **0 errors / 0 warnings**; SKEL **0 hard / 0 warnings**. Все семь pre-checks exit 0. Эти результаты не являются independent FINAL.

Артефакты task-2: `junit-peer-baseline-*`, `junit-peer-original-findings.json`, `junit-peer-candidate.json`, patch/prepared manifest, `junit-peer-runtime-checks.json`, `junit-peer-runtime/*.java`, `junit-peer-final-checks.json` и логи 0..6. Исходные findings сохранены до правок. Авторский отчёт сохраняется как исторический snapshot.

**Владельцу теории:** синхронизировать Q3 (формы источников, resolver и фильтры enum), Q4 (guard в одной лямбде), Q8 (runtime-данные и per-factory lifecycle), Q10 (границы conditions), Q11 (BigDecimal методы и сравнение сообщений), Q12 (способы конфигурации, SAME_THREAD, cooperative READ/WRITE), Q13 (default scanning и Import), Q14 (instance против общего state и переопределяемый default). До этого пара NEEDS_THEORY_SYNC. Работающий сервис не менялся.
