# JUnit — проверка и синхронизация теории

Дата: 2026-11-05. `interview-writer / SEMANTIC_UPDATE`. Прочитаны все 15 ответов, существовавший diff и все 60 MCQ-вариантов с sections. Исходная теория другого автора проверена с открытыми ключами; после правок исполнитель является автором исправлений. Независимая приёмка исправлений, слепые Phase A/B и FINAL **NOT_RUN**. В рамках этого пакета JSON и общий индекс не изменялись.

Входной Markdown SHA `e13634f9e61a07dcb5cc0715601d5e096c548fccec6720d99e8e813e97ac8167`. Прочитанный seed SHA `b42ac5086995dc915a4f7afeb52de2d116cc354682bac918a0cfd37d45c00266`; это уже другая редакция, чем seed в [старом авторском отчёте MCQ](../mcq-quality/llm-first/reviews/testing/junit-interview.md). Его LLM_READY не переносится на новую пару. Первоначальные находки сохранены в `/tmp/junit-theory-sync-20261105/original-findings.json` до основных правок; отдельное уточнение BOM записано при финализации.

После подготовки изучен [новый peer review MCQ](../mcq-quality/llm-first/reviews/testing/junit-peer-review-2026-11-05.md), привязанный к тому же seed SHA. Все восемь замечаний владельцу теории (Q3/Q4/Q8/Q10/Q11/Q12/Q13/Q14) сопоставлены с исправленными ответами. Дополнительно в этом пакете проверены границы default MOCK, кэша и TimingExtension; это не независимая приёмка нового Markdown.

## Изменения

| Q | Исправленный смысл |
|---|---|
| 1 | Явный `${junit.version}` требует свойства проекта; при управлении через BOM явную version обычно убирают. |
| 3 | ValueSource даёт один аргумент источника, включая Class; TestInfo может поступать отдельно. EnumSource без фильтров и с фильтрами имеют разный охват; MethodSource допускает вычисление данных и несколько форм результата. |
| 4 | Исправлен комментарий к assertAll. Обычные ошибки агрегируются, unrecoverable error прерывает выполнение; guard и зависимые проверки могут находиться в одной лямбде. |
| 8 | MethodSource тоже может вычислять runtime-данные; TestFactory создаёт узлы с телами. Описаны DynamicContainer, формы результата и отсутствие BeforeEach/AfterEach для каждого динамического ребёнка. |
| 9 | Пример TimingExtension компилируется без необъявленного logger; namespace отделён от lifecycle Store. Замер окружает тестовый метод и исключает пользовательские BeforeEach/AfterEach. |
| 10 | Собственные conditions шире свойств среды; штатный skip отделён от ошибки вычисления condition. |
| 11 | Преимущество диагностики сравнено с конкретным assertTrue, а не всеми assertions JUnit. BigDecimal isEqualByComparingTo отделён от isEqualTo с учётом scale. |
| 12 | Файл properties — один из способов настройки. ResourceLock действует для согласованных ключей Jupiter, READ допускает читателей, READ_WRITE исключителен; SAME_THREAD не даёт глобальной блокировки. Parallelism отделён от max pool size. |
| 13 | SpringBootTest по умолчанию не запускает HTTP-сервер. Срез допускает явный импорт нужного сервиса; ContextConfiguration не объявлен автоматическим Boot-срезом. Уточнены замена БД и границы кэша контекста. |
| 14 | Default lifecycle настраивается; новый instance не сбрасывает static/shared/external state. Дорогой ресурс можно подготовить статическим BeforeAll без PER_CLASS. |

Включены и рассмотрены прежние изменения Q1/Q7/Q9/Q12/Q13/Q15, frontmatter и якорей. Для прежнего Q7 подтверждён конкретный конструктор DataAccessResourceFailureException вместо абстрактного класса. Доменные примеры с многоточиями остаются иллюстративными фрагментами. Заголовки Q1–Q15, IDs, frontmatter, TOC и See also относительно входа сохранены.

## Первичные источники

Основная версия исследования и исполнения — **JUnit Jupiter 5.10.3 / Platform 1.10.3**, не текущая неопределённая версия: [MethodSource](https://docs.junit.org/5.10.3/api/org.junit.jupiter.params/org/junit/jupiter/params/provider/MethodSource.html), [ValueSource](https://docs.junit.org/5.10.3/api/org.junit.jupiter.params/org/junit/jupiter/params/provider/ValueSource.html), [Assertions](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html), [TestFactory](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/TestFactory.html), [dynamic lifecycle и parallel configuration](https://docs.junit.org/5.10.3/user-guide/), [ResourceLock](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/parallel/ResourceLock.html), [TestInstance](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/TestInstance.html), [ExtensionContext](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/extension/ExtensionContext.html), [Namespace](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/extension/ExtensionContext.Namespace.html), [EnabledIf](https://docs.junit.org/5.10.3/api/org.junit.jupiter.api/org/junit/jupiter/api/condition/EnabledIf.html).

Версия Boot **3.3.2** подтверждена `gradle/libs.versions.toml`. Проверены закреплённые [SpringBootTest 3.3.2](https://github.com/spring-projects/spring-boot/blob/v3.3.2/spring-boot-project/spring-boot-test/src/main/java/org/springframework/boot/test/context/SpringBootTest.java), [DataJpaTest 3.3.2](https://github.com/spring-projects/spring-boot/blob/v3.3.2/spring-boot-project/spring-boot-test-autoconfigure/src/main/java/org/springframework/boot/test/autoconfigure/orm/jpa/DataJpaTest.java), [ключ конфигурации Spring 6.1.11](https://github.com/spring-projects/spring-framework/blob/v6.1.11/spring-test/src/main/java/org/springframework/test/context/MergedContextConfiguration.java), а также [WebMvcTest API 3.3.13](https://docs.spring.io/spring-boot/3.3/api/java/org/springframework/boot/test/autoconfigure/web/servlet/WebMvcTest.html) и [документация кэша Spring 7.0.10](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html). Более новые страницы не выдаются за версию проекта. Для BigDecimal использованы [AssertJ 3.25.3 API](https://javadoc.io/static/org.assertj/assertj-core/3.25.3/org/assertj/core/api/AbstractBigDecimalAssert.html) и фактический запуск на этой версии.

## Проверки

На JDK **21.0.10**, кешированных Jupiter **5.10.3**, Platform **1.10.3** и AssertJ **3.25.3**: **32 test invocations PASS в 12 изолированных Launcher-запусках**. Проверены:

- точный Collection-factory из Q8 и отдельная fixture с состоянием: два ребёнка, но один BeforeEach/AfterEach, значение состояния 1 → 2;
- вычисляемый List-источник MethodSource, ValueSource(Class) вместе с TestInfo, отдельные lifecycle и экземпляры четырёх параметризованных вызовов;
- агрегация двух ordinary failures, guard внутри зависимой лямбды и NPE в соседней незащищённой; различие двух BigDecimal assertions;
- код Q9 с восемью параллельными repeated invocations: восемь отдельных записей/удалений Store и минимум четыре worker threads; отдельно сам тест из примера;
- исключение одновременного доступа согласованных READ_WRITE, одновременный доступ READ и возможность входа неаннотированного теста;
- два экземпляра при default PER_METHOD, один при конфигурационном PER_CLASS, нестатический BeforeAll при явном PER_CLASS.

Q8 Collection-метод скопирован без изменения. В Q9 импорты подняты в общие импорты harness, TimingTest переименован в ExactTimingTest; TimingExtension и его callbacks не изменены. Остальные fixtures — отдельные проверки механизма, не полная компиляция всех примеров Markdown. После последней правки прозы SHA извлечённого кода сверены повторно. Вредоносная нагрузка на память не создавалась: unrecoverable-error оговорка проверена по API, не через истощение памяти.

`javap` кешированных Boot **3.3.2** подтвердил аннотации, default MOCK и доступность MockBean; Spring **6.1.11** — конструктор конкретного исключения. Это чтение metadata, не запуск Spring.

Schema **PASS 15 Q / 15 B**, semantic `NOT_RUN`. Linkage **errors=0 / warnings=1** — Q10 A, min 0.111. Авторская сверка: текст явно называет встроенные условия ОС/JDK и отделяет их от assertion результата; неверная альтернатива исключает роль JDK. Независимая проверка предупреждения остаётся открыта; пороги и тексты ради scanner overlap не менялись. Структура, ссылки на локальные файлы и whitespace — **PASS**.

Артефакты: `/tmp/junit-theory-sync-20261105/` — входные снимки, preexisting.diff, findings, web-sources, runtime/execution.json с SHA JAR и извлечённого кода, cached-api-checks, static-checks, schema/linkage logs. Веб-артефакты — выдержки инструмента, не полные byte snapshots исходников. Точные финальные SHA передаются манифестом.

Полный проект/Gradle/CI, Spring context/MockMvc/JPA/DB, доменные Mockito-примеры, условные аннотации end-to-end, saturating pool stress, JUnit 5.14.4/6 и все прочие примеры **NOT_RUN**. Установка зависимостей, Docker и изменение MCQ не выполнялись. Пара требует новой независимой приёмки; FINAL не присвоен.
