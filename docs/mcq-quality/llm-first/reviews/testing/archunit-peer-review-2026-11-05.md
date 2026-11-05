# ArchUnit: независимое ревью исходной редакции и исправления MCQ

Дата: 2026-11-05. Исполнитель: Codex, контекст координатора MCQ; исходную редакцию подготовил другой исполнитель, указанный в `testing/archunit-interview.md`. Прочитаны Q1–Q15, все 60 вариантов, все sections, вводная часть и соответствующая теория. Это **independent open-key review**, не слепые Phase A/B. После изменения JSON текущий исполнитель является автором исправлений и не принимает собственную новую версию независимо.

Исходная пара:

- JSON `modules/quiz-app/src/main/resources/seed/mcq/testing/archunit-interview.json`, SHA `96a9d803b6b5e6c900c53dddd19d0b1e8d0697543798da80af992d45de4e970b`.
- Теория `cheatsheets/interview/testing/archunit-interview.md`, SHA `aa29a8487335d3647ceaac2740f2aebe160374d3e63e6fd9637a105b5d2de3b9`.
- Вердикт по исходной редакции: **NEEDS_REPAIR**, несмотря на прежний авторский LLM_READY и зелёную структурную проверку. Неизменяемый исходный разбор и baseline сохранены в task-2: `archunit-peer-original-findings.json`, `archunit-peer-baseline-seed.json`, `archunit-peer-baseline-theory.md`.

Исправленный JSON: SHA `7de93c0bf864780caa0859896835e6fa1bd44ca500b2f026a74d6c9f1e4d9375`. Изменены только Q1/Q2/Q4/Q5/Q7/Q9/Q12/Q13/Q14; Q3/Q6/Q8/Q10/Q11/Q15 сохранены побайтово. Ключи, номера, block_idx, question_text, order и labels сохранены. Markdown не редактировался. Статус исправленной пары — **NEEDS_THEORY_SYNC**, новая независимая приёмка **NOT_RUN**, FINAL **NOT_RUN**.

## Подтверждённые находки

1. **Q7 D, второй защитимый ответ.** Исходное «для UserService и UserServiceImpl хватает простого should, orShould не нужна» допускает один regex через haveNameMatching. Реальный ArchUnit **1.3.0** принимает оба класса правилом `classes().should().haveNameMatching("peer\\.naming\\.UserService(Impl)?")`. Новый D утверждает конкретное ошибочное свойство EndingWith("Service"), которое отвергает Impl. В sections признана альтернатива одним regex. [ClassesShould 1.3.0](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/lang/syntax/elements/ClassesShould.java).
2. **Q4 A/B sections, неверный тип фабрики.** layeredArchitecture() в **1.3.0** возвращает DependencySettings. После consideringAllDependencies() получается LayeredArchitecture, реализующий ArchRule. Это подтверждено исходником и проверкой реальных объектов из cached jar. Неверный A всё ещё неверен: начальный объект не является предикатом для that(). [Architectures 1.3.0](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/Architectures.java).
3. **Q5 A.example, неполная модель.** Исходный пример без domainServices завершается empty domain service. После разрешения optional layers он не обнаруживает REST→необъявленный persistence adapter. Пример с обоими объявленными adapters обнаруживает связь. JSON-пример дополнен ролями, обязательными слоями и ограничением scope; onion не приравнивается ко всем вариантам clean/hexagonal. [Architectures 1.3.0](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/Architectures.java).
4. **Q12, неполный ключ кэша.** Общий ClassCache **1.3.0** учитывает locations **и типы ImportOption**; отдельно есть cachedByTest. При одинаковом пустом наборе locations ключи с DoNotIncludeTests и DoNotIncludeJars не равны; одинаковые filters дают равные ключи. Это исполнено на cached engine jar. Правила не кэшируются этим механизмом как готовые результаты. JSON исправлен; добавлено требование стабильного поведения custom ImportOption. [ClassCache 1.3.0](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit-junit/src/main/java/com/tngtech/archunit/junit/internal/ClassCache.java), [ImportOption contract](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/core/importer/ImportOption.java).
5. **Q13 D, слишком сильное требование буквального вызова.** JUnit Platform можно выбрать через JVM test suite или convention plugin; один обязательный текст useJUnitPlatform() в пользовательском build-файле не является универсальным требованием. Новый D ошибочно приписывает автоматическое исполнение @ArchTest-полей Jupiter engine вместо отдельного ArchUnit engine. Source Gradle **8.9.0** подтверждает настройку framework из toolchain; отдельный запуск Gradle suite не выполнялся. [DefaultJvmTestSuite 8.9.0](https://github.com/gradle/gradle/blob/v8.9.0/platforms/jvm/plugins-jvm-test-suite/src/main/java/org/gradle/api/plugins/jvm/internal/DefaultJvmTestSuite.java), [официальное описание test suites](https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html).
6. **Q14, пропущена инициализация store.** Default file store **1.3.0** имеет allowStoreCreation=false. На чистом локальном пути первый запуск действительно выдаёт StoreInitializationFailedException. Явно разрешённая подготовка baseline проходит; затем тот же baseline работает при запрещённом создании нового store. Исправлены все A–D и sections: исторический baseline не восстанавливается из текущего bytecode; ошибки store не объявляются новыми архитектурными нарушениями. [TextFileBasedViolationStore 1.3.0](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/freeze/TextFileBasedViolationStore.java), [FreezingArchRule 1.3.0](https://github.com/TNG/ArchUnit/blob/v1.3.0/archunit/src/main/java/com/tngtech/archunit/library/freeze/FreezingArchRule.java).

Дополнительно Q1 B отделяет AssertionError/провал теста от настройки merge gate; Q2 A.example не обещает распознавание production-роли по одному фильтру каталогов; Q9 B уточняет последствие записи satisfied для нарушающего объекта. Это не изменения ключей.

## Охват чтения A/B/C/D

| Q | Сравнение вариантов и результат |
|---|---|
| 1 | A внешний инструмент, B только warning, C bytecode+правила, D исходники. Ключ C соответствует материалу; гарантия merge в sections исправлена. |
| 2 | A bytecode+Location filter; B source; C автоматическое исключение JAR; D запрет custom option. Ключ A сохранён; ограничение фильтра уточнено. |
| 3 | A DSL якобы проверяет сразу; B инвертирует noClasses; C меняет that/should; D различает роли. Новых находок не заявлено. |
| 4 | A ошибочный predicate/тип; B объявления слоёв; C переворачивает направление; D требует ручной набор правил. Исправлены факты sections, ключ B сохранён. |
| 5 | A внутрь домена; B наружу; C whereLayer вместо ролей; D связь adapters. Исправлены scope и исполнимый пример A. |
| 6 | A naming вместо grouping; B slice как класс; C capture group; D grouping как naming. Материал поддерживает C; полный cycle stress test не выполнялся. |
| 7 | A неполная двусторонняя проверка; B regex; C пакетный синтаксис в regex; D раньше допускал корректный regex. Второй ответ устранён конкретным suffix assertion. |
| 8 | A требует injection; B разрешает generic exceptions; C игнорирует границу проекта; D готовые и проектные правила. Проверены GeneralCodingRules 1.3.0; политики проекта не объявлены универсальными. |
| 9 | A ArchCondition.check; B satisfied как violation; C запрет custom predicate; D запрет composition. Уточнено B-контрфактическое объяснение. |
| 10 | A ручной check; B только методы; C AnalyzeClasses+engine; D импорт на каждое поле. Ключ C поддерживается API; отдельный запуск engine для этого примера NOT_RUN. |
| 11 | A/C фильтр по имени; B по Location; D production-only default. Источник ImportOption подтверждает B; A/C близки по заблуждению, это не независимый ANSC PASS. |
| 12 | A межклассовый PER_CLASS; B только имя класса; C теперь cache всех результатов; D locations+filters. Исправлены source-факты и область кэша. |
| 13 | A обычная тест-фаза; B отдельная инфраструктура; C because невидим; D теперь путает engines. Необоснованное требование буквального Gradle вызова устранено. |
| 14 | A сохранение исправленного долга; B восстановление истории из bytecode; C готовый baseline; D отказ при разрешённой инициализации. Все четыре прочитаны после изменения. |
| 15 | A/D приписывают jMolecules текущему проекту; B различает роли и интеграцию; C запрещает api.dto. Три фактических правила и зависимости quiz-app сверены; source jMolecules просмотрен, его runtime NOT_RUN. |

## Форма, полнота и язык

SFPL: сравнивались одинаковость предмета, уровень деталей, API, код, отрицания и условия у всех четырёх вариантов. Новые Q7/Q13/Q14 сужают конкретное заблуждение, а не вводят условие только после ключа. Независимый SFPL PASS для исправленной версии не присваивается.

ANSC: исходные Q7/Q13 допускали защиту wrong без скрытого контекста. Они не принимаются по одному совпадению длины; исправленные формулировки проверены автором правок, но требуют нового отдельного reviewer. A/C Q11 проверяют близкую ошибку — это открытое замечание к разнообразию distractors, без заявления о втором ключе.

RU: прочитаны видимые заголовки, теория, варианты и sections; новые уточнения различают источник, условие, механизм и границы. Привычные API сохранены. Открытые фактические противоречия в Markdown не замаскированы редакторским RU PASS.

## Проверки и передача теории

Локальные harness: JDK **21**, cached ArchUnit **1.3.0** и SLF4J API; main/project tests не менялись. PASS: Q7 regex/suffix, Q4 реальные типы, Q5 неполная/полная модель, Q14 default/контролируемая инициализация; отдельный reflection harness подтвердил Q12 key equality. SLF4J работал без provider с NOP logger. Это изолированные fixtures, не приёмка реального приложения. Ничего не установлено, Docker/сервис/БД не затронуты.

Артефакты task-2: `archunit-peer-runtime-checks.json`, `archunit-peer-cache-key-check.json`, `archunit-peer-runtime/`, `archunit-peer-original-findings.json`, `archunit-peer-prepared.json`, `archunit-peer-verified-checks.json`. У первых двух исходные harness-ошибки (неверное имя regex API и выбор synthetic constructor) исправлены в оболочках; итоговый положительный вывод основан на показанных исполненных API.

**Владельцу теории:** Q4 назвать DependencySettings и этап considering*; Q5 пояснить непустые роли/объявленные adapters; Q7 ограничить требование orShould выбранным suffix-условием, допуская regex; Q12 включить ImportOption types и разделить per-class/shared caches; Q13 требовать Platform+ArchUnit engine, а не буквальную строку во всех конфигурациях Gradle; Q14 добавить allowStoreCreation=false и контролируемую подготовку/доставку baseline. До синхронизации этих Q пара не LLM_READY. Старый авторский отчёт относится к другому SHA и не подтверждает исправленный JSON.

Старые evidence и производные статусы не переписывались. Полный строгий workflow record, независимая новая Phase A/B, performance benchmark, Gradle suite runtime и jMolecules runtime — **NOT_RUN**. Исправленные автором блоки не получают independent PASS от этого же автора.

Итоговые pre-checks исправленного JSON: schema **PASS 15/15**, anti-tell **0/15**, SKEL **hard=0 / warn=7**. Parity **FAIL** только word ratio **1.45, Q12**; correct-longest 1/15, correct/wrong average 1.008, style guessability 0.30. Linkage **1 error / 1 warning**: ImportOption в Q12 отсутствует в текущей теории (реальная необходимая синхронизация); ArchUnitTestEngine в Q13 относится к уточняемому различию engines. Сигналы не скрывались и пороги не менялись. Символьное отношение Q12 около 1.12; поле API не дополнялось padding.

При коммите включается и уже существовавшая редакция JSON другого автора, потому что весь её скоуп прочитан: по сравнению с HEAD затронуты 14 вопросов, а собственные исправления относительно снимка автора — 9. Исходный авторский отчёт сохраняется как свидетельство версии с прежними хешами; он не подтверждает текущий JSON. Теория остаётся за её владельцем, текущий рабочий снимок требуется для этой пары до его отдельного коммита/синхронизации.
