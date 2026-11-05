# Cucumber / BDD — синхронизация теории

Дата: 2026-11-05. SEMANTIC_UPDATE: переданные **Q2/Q3/Q5/Q7/Q8/Q10/Q11/Q12/Q13/Q14/Q15**, дополнительно **Q1** — примеры BDD не объявляются автоматическими тестами без автоматизации. Прочитаны все 15 разделов теории, прежний Markdown diff и все 48 вариантов/sections этих 12 Q; ключи были видны. Это авторская source-проверка и ограниченный Java runtime, не independent blind, Phase A/B или FINAL.

Вход: [MCQ peer report](../mcq-quality/llm-first/reviews/testing/cucumber-peer-review-2026-11-05.md). JSON только чтение: `modules/quiz-app/src/main/resources/seed/mcq/testing/cucumber-bdd-interview.json`, SHA-256 `8c14073758da2f530ba2d568bdec67594464f2bd7f3053fe5e012907a2db57f9`. Исходная незакоммиченная теория имела SHA-256 `14eef76415e2583c1081e1b5e3bb8f734d64413af8647ee7b1341e66e5d4ced1`; подготовленный [Markdown](../../cheatsheets/interview/testing/cucumber-bdd-interview.md) — `2c181bd497a281f38ba769d9df54bf08bf379b978c9ee2da45183c7f42e683f9`. Изменены ровно 12 Q; Q4/Q6/Q9, все текущие IDs/заголовки/anchors, header/frontmatter/TOC/See also сохранены побайтово. JSON, планы, общие реестры и индекс не менялись.

## Исправления и источники

| Q | Содержательная синхронизация |
|---|---|
| 1/2/15 | Разделены совместное уточнение поведения, возможная автоматизация и выбор Cucumber. Нет обещания нулевой пользы для технической/малой команды или автоматизатора готовой системы; нет гарантии ROI. Three Amigos — перспективы, не ровно три человека и одна встреча. |
| 3 | Feature-текст связан с glue/проверками, а не сам исполняет действие. Красный статус требует проверить также данные, проверку и среду; отчёт не доказывает полноту требований. |
| 5 | Feature/Rule scope, порядок после Before и повторение для применимых сценариев/Examples. Нет автоматического reset внешних данных; длинный setup не даёт безусловной читаемости. |
| 7 | Definition — обработчик, обычно Java-метод; класс может содержать несколько. Разделены identical pattern registration и несколько совпадений конкретного шага, а также reuse кода и mutable state. |
| 8 | Locale.ROOT в enum converter; оба вида expressions поддерживаются, Java regex явно выбирают, тип capture group не объявляется универсально int. |
| 10 | Формы DataTable не ограничены тремя. Entry/row/cell/table transformers различаются по сигнатурам; asMaps использует заголовки колонок. Доменные фрагменты не выдаются за самостоятельный класс. |
| 11 | Before/BeforeStep ascending и After/AfterStep descending; обработка ошибки и пропуск последующих step hooks. Пример cleanup проверяет, был ли ресурс создан; успешная очистка не гарантируется одним вызовом After. |
| 12 | Полный набор поддерживаемых уровней tags, inheritance и отдельный filter expression. В примере smoke OR payment выбирает оба сценария; метка сама не сужает отбор. |
| 13 | Native engine отделён от JUnit4/Vintage; свойства не заменяют discovery/selectors. Maven dependencies названы фрагментом с cucumber.version7.18.1 и prerequisite JUnit BOM5.10.3. |
| 14 | Один конфигурационный glue-класс; запрет Component на step glue; отдельный зарегистрированный scenario bean. Import регистрирует носитель явно; scope не копирует static и не сбрасывает БД/cookies/singletons. |

Процесс/ролевая модель: [Cucumber myths](https://cucumber.io/docs/bdd/myths/), [Who does what](https://cucumber.io/docs/bdd/who-does-what/). Синтаксис и параметры: [Gherkin](https://cucumber.io/docs/gherkin/reference/), [expressions](https://cucumber.io/docs/cucumber/cucumber-expressions/), [tags inheritance](https://cucumber.io/docs/cucumber/api/?lang=java#tag-inheritance). Online-страницы прочитаны 2026-11-05; они отделены от pinned JVM source.

Механизмы сверены с **Cucumber JVM v7.18.1**: [CachingGlue](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-core/src/main/java/io/cucumber/core/runner/CachingGlue.java), [PickleStepTestStep](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-core/src/main/java/io/cucumber/core/runner/PickleStepTestStep.java), [TestCase](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-core/src/main/java/io/cucumber/core/runner/TestCase.java), [Before](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-java/src/main/java/io/cucumber/java/Before.java), [After](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-java/src/main/java/io/cucumber/java/After.java), [DataTable](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/datatable/src/main/java/io/cucumber/datatable/DataTable.java), [DataTableType](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-java/src/main/java/io/cucumber/java/DataTableType.java), [native engine README](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-junit-platform-engine/README.md), [SpringFactory](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-spring/src/main/java/io/cucumber/spring/SpringFactory.java), [ScenarioScope](https://github.com/cucumber/cucumber-jvm/blob/v7.18.1/cucumber-spring/src/main/java/io/cucumber/spring/ScenarioScope.java).

Для Platform/Vintage/BOM использован [JUnit 5.10.3 guide](https://docs.junit.org/5.10.3/user-guide/); для явного Import обычного component-класса — [Spring 6.1.11 Import](https://github.com/spring-projects/spring-framework/blob/v6.1.11/spring-context/src/main/java/org/springframework/context/annotation/Import.java). Java expression heuristics дополнительно прочитаны в [ExpressionFactory main](https://github.com/cucumber/cucumber-expressions/blob/main/java/src/main/java/io/cucumber/cucumberexpressions/ExpressionFactory.java); подвижный main не объявляется локально исполненной версией Cucumber.

## Согласование и проверки

Ключи **Q1 D / Q2 C / Q3 A / Q5 D / Q7 C / Q8 C / Q10 C / Q11 A / Q12 B / Q13 A / Q14 D / Q15 B** совместимы с подготовленной теорией по авторской проверке всех A/B/C/D и sections. Три остальных MCQ этим пакетом не принимаются повторно. Общие статусы LLM_READY/FINAL не менялись.

- PASS: точный scope 12 Q, три остальных раздела и структура/anchors/ссылки/fences/whitespace.
- PASS verify-mcq-json: 15 Q / 15 B; semantic review NOT_RUN. SHA и 12 per-Q хешей стабильны.
- XML dependency fragment разобран ElementTree; prerequisite BOM/свойство записаны явно. Maven resolution/runner NOT_RUN.
- Булева проверка двух явных наборов tags подтверждает приведённые результаты OR и AND/NOT. Это не Gherkin parser или Cucumber tag-expression runtime.
- Linkage: **exit0/errors0/warnings1**, LOW-COVERAGE Q5 A/C, min0.182. A «однажды на файл» опровергается повторением применимых шагов; C «длинный Background только улучшает» — оговоркой о затруднении чтения. Автор не выявил отсутствующего факта, но предупреждение остаётся OPEN для независимого review. Пороги/алгоритм/allowlist не менялись, padding не добавлялся.

В MCQ peer report на том же JSON SHA сохраняется **parity FAIL Q13 word ratio1.44**. Собственный новый parity/Phase A/B не выполнялся; структурный PASS и author source-sync не являются полной приёмкой пары.

## Runtime и границы

**PASS только извлечённого Java-метода status(String):** Microsoft JDK21.0.10, отдельные дочерние JVM. Исходный метод проходит при en-US; при tr-TR active/inactive дают ожидаемый IllegalArgumentException, deleted проходит. Метод с Locale.ROOT обрабатывает все три значения при tr-TR. Тело метода извлечено без изменения; Cucumber annotation не компилировалась, enum с тремя значениями добавлен согласно примеру. Locale задавалась только параметрами дочерней JVM; системная locale Mac не менялась.

В проверенных Gradle/Maven io.cucumber cache готовых jars нет. **NOT_RUN:** полный Cucumber engine, annotations/binding, Spring bridge/scenario lifecycle, parser, DataTable/parameter-type converters, duplicate/ambiguity и hooks runtime, Maven/Gradle/Vintage/Suite launch, реальные сервисы/браузер/БД, Mermaid render и независимые SFPL/ANSC/RU/Phase A/B/FINAL. Не устанавливались зависимости, Docker не вызывался, прежний отказ не обходился.

Прежние незакоммиченные изменения тоже сохранены и прочитаны: смысловые Q1/Q2/Q3/Q4/Q6/Q9/Q13/Q15, metadata/date и явные anchors/TOC. Убраны прежние абсолютные обещания свежести документации, смешение keyword lookup и гарантия устойчивости glue при изменении UI; новый пакет их не откатывает. Парсерное поведение Outline без строк проверено по смыслу Gherkin reference, не запуском parser.

Артефакты `/tmp/cucumber-theory-sync-20261105/`: before/head/seed snapshots, preexisting-changes.json, static-checks.json, XML и Boolean evidence, cache availability, extracted-status-methods.json, web-sources-1..5.txt, linkage/verify logs; runtime Java sources/classes и execution.json. После locale runtime менялись только пробелы в строках plugin-настроек Q3/Q13; SHA status-метода повторно совпал. JSON и индекс не менялись; адресный пакет ожидает следующей координации индекса.
