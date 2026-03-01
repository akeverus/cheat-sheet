package com.cheatsheet.quiz.service.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OptionQualityValidatorTest {

    @Mock
    OptionDeduplicator deduplicator;

    OptionQualityValidator validator;

    @BeforeEach
    void setUp() {
        lenient().when(deduplicator.isDuplicate(any(), any())).thenReturn(false);
        AppProperties properties = new AppProperties();
        validator = new OptionQualityValidator(deduplicator, properties);
    }

    @Test
    void flagsExplanationLikeOptionTextWithListStyle() {
        GeneratedOptions options = new GeneratedOptions(
                "Используется Elasticsearch Query DSL в JSON-формате.",
                "Проверяется через выполнение match-запроса в Dev Tools.",
                List.of(
                        "Elasticsearch предоставляет несколько преимуществ: 1. Масштабируемость. 2. Скорость. 3. Полнотекстовый поиск.",
                        "Используется SQL-синтаксис с оператором SELECT ... WHERE.",
                        "Используется Lucene Query Parser как основной формат API."
                ),
                List.of(
                        "Это перечисление преимуществ, а не ответ на вопрос о языке запросов.",
                        "Проверяется по документации API _search.",
                        "Проверяется по endpoint _search с JSON body."
                )
        );

        List<String> issues = validator.validateQuality(options);

        assertThat(issues).anyMatch(msg -> msg.contains("слишком длинные или лекционные"));
    }

    @Test
    void flagsBoilerplatePrefixAcrossManyOptions() {
        GeneratedOptions options = new GeneratedOptions(
                "Elasticsearch использует Query DSL в JSON.",
                "Это проверяется структурой body в _search запросе.",
                List.of(
                        "Elasticsearch предоставляет несколько преимуществ, включая шардирование и репликацию.",
                        "Elasticsearch предоставляет несколько преимуществ, включая распределенное хранение данных.",
                        "Elasticsearch предоставляет несколько преимуществ, включая быстрые агрегации.",
                        "Elasticsearch предоставляет несколько преимуществ, включая полнотекстовый индекс.",
                        "Elasticsearch предоставляет несколько преимуществ, включая масштабирование кластера."
                ),
                List.of(
                        "Это не отвечает на вопрос про язык запросов.",
                        "Это описание свойств, а не формата запроса.",
                        "Это перечисление возможностей, а не механизм запроса.",
                        "Это функциональные плюсы, а не синтаксис API.",
                        "Это характеристики системы, а не ответ про DSL."
                )
        );

        List<String> issues = validator.validateQuality(options);

        assertThat(issues).anyMatch(msg -> msg.contains("шаблонный префикс"));
    }

    @Test
    void flagsWrongOptionsThatMissQuestionCoreEntities() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают по временной и пространственной сложности в best/worst/average case.",
                "Это показывает поведение алгоритма на разных входах.",
                List.of(
                        "Команда проекта работает по Scrum с недельными митингами.",
                        "Хороший код должен быть читаемым и лаконичным.",
                        "CI/CD ускоряет доставку изменений."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithQuestionContext(
                options,
                "Как сравнить эффективность двух алгоритмов?"
        );

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.QUESTION_CORE_MISMATCH
                        && i.severity() == OptionQualityValidator.Severity.WARNING);
    }

    @Test
    void doesNotFlagSingleWeakDistractorForShortQuestionCore() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают сложность по времени и памяти.",
                "x",
                List.of(
                        "Сравнивают алгоритмы O(n) и O(log n) на одинаковых входных данных.",
                        "Пишут документацию по процессу разработки.",
                        "Смотрят алгоритмы в worst-case и average-case."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithQuestionContext(
                options,
                "Как сравнить алгоритмы?"
        );

        assertThat(report.issues())
                .noneMatch(i -> i.code() == OptionQualityValidator.IssueCode.QUESTION_CORE_MISMATCH);
    }

    @Test
    void keepsShortTechnicalTokensInQuestionCoreMatching() {
        GeneratedOptions options = new GeneratedOptions(
                "JVM и GC влияют на паузы и пропускную способность.",
                "x",
                List.of(
                        "JVM и GC влияют на latency и throughput по-разному.",
                        "JVM уменьшает паузы GC при правильной настройке поколений.",
                        "Команда использует Scrum и ретроспективы каждую неделю."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithQuestionContext(
                options,
                "Как JVM GC влияет на производительность?"
        );

        assertThat(report.issues())
                .noneMatch(i -> i.code() == OptionQualityValidator.IssueCode.QUESTION_CORE_MISMATCH);
    }

    @Test
    void doesNotFlagSingleWeakDistractorForTechnicalSpringQuestion() {
        GeneratedOptions options = new GeneratedOptions(
                "Границы @Transactional важны для согласованности данных.",
                "x",
                List.of(
                        "Spring @Transactional задает границу коммита/rollback.",
                        "Пишут документацию по релизному процессу команды.",
                        "Spring прокси управляет транзакцией на уровне вызова метода."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithQuestionContext(
                options,
                "Как работает транзакция в Spring?"
        );

        assertThat(report.issues())
                .noneMatch(i -> i.code() == OptionQualityValidator.IssueCode.QUESTION_CORE_MISMATCH);
    }

    @Test
    void flagsCorrectOptionThatDoesNotMatchExpectedAnswer() {
        GeneratedOptions options = new GeneratedOptions(
                "Это про горизонтальное масштабирование брокера сообщений.",
                "x",
                List.of(
                        "Используют synchronized для критических секций.",
                        "Используют volatile для видимости.",
                        "Используют AtomicInteger для lock-free инкремента."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithContext(
                options,
                "Как синхронизировать доступ к общему счетчику в многопоточном коде?",
                "Используют synchronized/Lock или атомики в зависимости от сценария."
        );

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.CORRECT_ANSWER_MISMATCH
                        && i.severity() == OptionQualityValidator.Severity.WARNING);
    }

    @Test
    void treatsLectureStyleOptionTextAsCriticalIssue() {
        GeneratedOptions options = new GeneratedOptions(
                "Обычно выбирают структуру данных по операции доступа и ограничениям памяти.",
                "x",
                List.of(
                        "Стек полезен для LIFO. В реальных системах это помогает при откате и парсинге. " +
                                "Дополнительно учитывают нагрузку и характер данных.",
                        "Очередь полезна для FIFO и задач планирования.",
                        "Хеш-таблица нужна для быстрого поиска по ключу."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.LECTURE_STYLE_OPTION
                        && i.severity() == OptionQualityValidator.Severity.CRITICAL);
        assertThat(report.hasCritical()).isTrue();
    }

    @Test
    void flagsOptionsThatDoNotMatchComparisonQuestionFormat() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают алгоритмы по времени и памяти в одинаковых условиях.",
                "x",
                List.of(
                        "Используют кэширование для ускорения чтения.",
                        "Настраивают индексы в поисковом движке.",
                        "Оптимизируют SQL-запросы на уровне БД."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithQuestionContext(
                options,
                "Как сравнить эффективность двух алгоритмов?"
        );

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.ANSWER_TYPE_MISMATCH);
        assertThat(validator.hasCriticalIssues(report)).isTrue();
    }

    @Test
    void treatsSingleComparisonFormatMissAsWarning() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают по времени и памяти на одном наборе входных данных.",
                "x",
                List.of(
                        "Сравнивают сложность по времени и памяти в худшем случае.",
                        "Сравнивают throughput и latency на одинаковых данных.",
                        "Команда пишет документацию перед релизом."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithQuestionContext(
                options,
                "Как сравнить эффективность двух алгоритмов?"
        );

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.ANSWER_TYPE_MISMATCH
                        && i.severity() == OptionQualityValidator.Severity.WARNING);
    }

    @Test
    void treatsSingleDefinitionFormatMissAsWarning() {
        GeneratedOptions options = new GeneratedOptions(
                "Это неизменяемый объект, который нельзя модифицировать после создания.",
                "x",
                List.of(
                        "Это структура данных с фиксированным состоянием.",
                        "Это объект, который создают один раз и дальше только читают его состояние.",
                        "Проверяют производительность на нагрузочном стенде."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithQuestionContext(
                options,
                "Что такое immutable объект?"
        );

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.ANSWER_TYPE_MISMATCH
                        && i.severity() == OptionQualityValidator.Severity.WARNING);
    }

    @Test
    void keepsCodeResultIntentStrictAsCritical() {
        GeneratedOptions options = new GeneratedOptions(
                "Код выведет true.",
                "x",
                List.of(
                        "Вариант про архитектурные trade-off без результата выполнения.",
                        "Другой вариант про паттерны проектирования в сервисе.",
                        "Третий вариант про процесс ревью и деплоя."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithQuestionContext(
                options,
                "Что выведет этот код?"
        );

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.ANSWER_TYPE_MISMATCH
                        && i.severity() == OptionQualityValidator.Severity.CRITICAL);
    }

    @Test
    void flagsAmbiguousCorrectAnswerWithoutConcreteSignals() {
        GeneratedOptions options = new GeneratedOptions(
                "Обычно используют подход в зависимости от ситуации в проекте.",
                "x",
                List.of(
                        "Используют synchronized для критических секций.",
                        "Используют lock-free атомики для счётчиков.",
                        "Используют очереди для асинхронной обработки задач."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.AMBIGUOUS_CORRECT_ANSWER
                        && i.severity() == OptionQualityValidator.Severity.WARNING);
    }

    @Test
    void doesNotFlagOptionLengthByItself() {
        GeneratedOptions options = new GeneratedOptions(
                "Это механизм работы cache-aside с invalidate после записи.",
                "x",
                List.of(
                        "Это вариант с подробной, но цельной формулировкой, где есть конкретные критерии поведения системы в edge case при cache miss и повторном чтении без лишнего словесного мусора.",
                        "Это альтернативный вариант с корректной предметной терминологией и проверяемыми ограничениями по консистентности данных после обновления записи.",
                        "Это правдоподобная, но неверная трактовка с сохранением технического контекста и одной завершенной мыслью."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues())
                .noneMatch(i -> i.message().contains("слишком длинные или лекционные")
                        && i.code() != OptionQualityValidator.IssueCode.LECTURE_STYLE_OPTION);
    }

    @Test
    void flagsWrongOptionTooCloseToCorrect() {
        GeneratedOptions options = new GeneratedOptions(
                "HashMap не гарантирует порядок итерации элементов.",
                "x",
                List.of(
                        "HashMap не гарантирует порядок обхода элементов в map.",
                        "HashMap всегда хранит элементы в порядке вставки.",
                        "HashMap запрещает null ключи и null значения."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.WRONG_TOO_CLOSE_TO_CORRECT
                        && i.severity() == OptionQualityValidator.Severity.WARNING);
    }

    @Test
    void doesNotFlagCorrectAlignmentWhenShortExpectedHasOverlap() {
        GeneratedOptions options = new GeneratedOptions(
                "GC уменьшает задержки при корректной настройке JVM.",
                "x",
                List.of(
                        "Выбирают иной алгоритм балансировки нагрузки.",
                        "Используют более короткий TTL в кэше.",
                        "Отключают логирование для ускорения dev-сборки."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithContext(
                options,
                "Что важно в JVM GC?",
                "JVM GC latency"
        );

        assertThat(report.issues())
                .noneMatch(i -> i.code() == OptionQualityValidator.IssueCode.CORRECT_ANSWER_MISMATCH);
    }

    @Test
    void flagsVaguePlaceholderOptionsAsCritical() {
        GeneratedOptions options = new GeneratedOptions(
                "Кэш обновляется при истечении TTL и возвращает актуальные данные.",
                "x",
                List.of(
                        "Используют альтернативный подход с другими ограничениями.",
                        "Применяют другой механизм для обработки данных.",
                        "Выбирают иной вариант в зависимости от ситуации."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues()).anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.VAGUE_OPTION_TEXT);
        assertThat(report.hasCritical()).isFalse();
    }

    @Test
    void flagsTruncatedOptionsAsCritical() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают по времени и памяти на одном наборе входных данных.",
                "x",
                List.of(
                        "Сравнивают по асимптотике и латентности в худшем случае...",
                        "Сравнивают только по средней сложности без учета памяти...",
                        "Сравнивают по числу аллокаций и времени на горячем пути..."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues()).anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.TRUNCATED_OPTION_TEXT);
        assertThat(report.hasCritical()).isTrue();
        assertThat(validator.hasHardBlockIssues(report)).isTrue();
    }

    @Test
    void treatsAnswerTypeMismatchAsRetryableNotHardBlock() {
        OptionQualityValidator.ValidationReport report = new OptionQualityValidator.ValidationReport(List.of(
                new OptionQualityValidator.ValidationIssue(
                        OptionQualityValidator.IssueCode.ANSWER_TYPE_MISMATCH,
                        OptionQualityValidator.Severity.CRITICAL,
                        "тип ответа не совпадает"
                )
        ));
        assertThat(validator.hasHardBlockIssues(report)).isFalse();
    }

    @Test
    void flagsComparisonOptionsWithoutExplicitCriteria() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают подходы и выбирают более подходящий для задачи.",
                "x",
                List.of(
                        "Один вариант удобнее в поддержке команды.",
                        "Другой вариант проще читать в большом проекте.",
                        "Третий вариант чаще выбирают на интервью."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReportWithQuestionContext(
                options,
                "Как сравнить эффективность двух алгоритмов?"
        );

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.MISSING_COMPARISON_CRITERIA);
        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.MISSING_COMPARISON_CRITERIA
                        && i.severity() == OptionQualityValidator.Severity.WARNING);
    }

    @Test
    void allowsConceptualContextWhenOptionHasConcreteFact() {
        GeneratedOptions options = new GeneratedOptions(
                "Кэш хранит данные до TTL и обновляет их после истечения.",
                "x",
                List.of(
                        "Используют другой механизм: TTL 60 сек и обновление по таймеру.",
                        "Используют альтернативный подход: cache-aside с invalidate после записи.",
                        "Выбирают иной вариант: read-through c fallback при cache miss."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues())
                .noneMatch(i -> i.code() == OptionQualityValidator.IssueCode.VAGUE_OPTION_TEXT);
    }

    @Test
    void flagsPathLikeFragmentsAsCritical() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают по времени и памяти на одинаковых входных данных.",
                "x",
                List.of(
                        "Сравнивают algorithms/algorithms-interview только по времени без памяти.",
                        "Берут src/main/java как эталон производительности.",
                        "Сравнивают по worst-case и average-case на одном наборе."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.PATH_LIKE_OPTION_TEXT);
        assertThat(report.hasCritical()).isFalse();
        assertThat(validator.hasHardBlockIssues(report)).isTrue();
    }

    @Test
    void doesNotTreatDomainRatiosAsPathLikeFragments() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают алгоритмы по критерию ценность/вес и по затратам памяти.",
                "x",
                List.of(
                        "Оценивают отношение ценность/вес только на среднем случае без проверки худшего.",
                        "Сравнивают по памяти и времени на одинаковых входных данных.",
                        "Учитывают worst-case и число аллокаций при росте n."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues())
                .noneMatch(i -> i.code() == OptionQualityValidator.IssueCode.PATH_LIKE_OPTION_TEXT);
    }

    @Test
    void flagsClicheFallbackPatternsAsCritical() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают по времени и памяти на одинаковых входных данных.",
                "x",
                List.of(
                        "Описывают алгоритм как общую идею без ключевого свойства из вопроса.",
                        "Дают смежное определение алгоритма, но подменяют исходный термин другим понятием.",
                        "Подменяют смысл алгоритма близким термином с иными ограничениями применения."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.CLICHE_FALLBACK_OPTION);
        assertThat(report.hasCritical()).isTrue();
        assertThat(validator.hasHardBlockIssues(report)).isTrue();
    }

    @Test
    void flagsInterviewMetaAdviceInOptionsAsCritical() {
        GeneratedOptions options = new GeneratedOptions(
                "Сравнивают алгоритмы по времени и памяти на одинаковых входных данных.",
                "x",
                List.of(
                        "Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом.",
                        "На интервью ожидают, что вы назовёте критерии выбора и метрики.",
                        "Сравнивают по worst-case и average-case."
                ),
                List.of("x", "y", "z")
        );

        OptionQualityValidator.ValidationReport report = validator.validateQualityReport(options);

        assertThat(report.issues())
                .anyMatch(i -> i.code() == OptionQualityValidator.IssueCode.INTERVIEW_META_ADVICE_TEXT
                        && i.severity() == OptionQualityValidator.Severity.CRITICAL);
    }
}
