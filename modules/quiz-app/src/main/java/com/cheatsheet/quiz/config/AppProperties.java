package com.cheatsheet.quiz.config;

import com.cheatsheet.quiz.domain.AiProvider;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Корневой класс конфигурации приложения тестирования перед интервью.
 *
 * <p>Все параметры считываются из {@code application.yml} по префиксу {@code app.*}.
 * Поддерживается валидация через {@link Validated}.</p>
 *
 * <p>Пример конфигурации:</p>
 * <pre>
 * app:
 *   interview-path: cheatsheets/interview
 *   ai-provider: openai
 *   interview:
 *     options-count: 4
 *     learned-repetitions: 3
 *     exam-penalty-questions: 5
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "app")
@Validated
@Getter
@Setter
public class AppProperties {

    /** Путь к директории с markdown-файлами вопросов (относительно рабочей директории). */
    @NotBlank(message = "app.interviewPath должен быть задан")
    private String interviewPath;

    /** Путь к YAML-каталогу групп и порядка тем (classpath). */
    private String topicGroupsConfig = "topic-groups.yaml";

    /** Активный AI-провайдер (primary) для генерации вариантов ответов. */
    @NotNull
    private AiProvider aiProvider = AiProvider.OPENAI;

    /** Настройки тестирования (количество вариантов, пороги и т.д.). */
    @Valid @NotNull
    private Interview interview = new Interview();

    /** Настройки AI-провайдера: общие параметры запросов. */
    @Valid @NotNull
    private Ai ai = new Ai();

    /** Конфигурация DeepSeek API. */
    @Valid @NotNull
    private DeepSeek deepseek = new DeepSeek();

    /** Конфигурация OpenAI API. */
    @Valid @NotNull
    private OpenAi openai = new OpenAi();

    /** Настройки фонового предзагрузчика вариантов ответов. */
    @Valid @NotNull
    private Preload preload = new Preload();

    /** Настройки SQLite. */
    @Valid @NotNull
    private Sqlite sqlite = new Sqlite();

    /** Настройки in-memory кэша вариантов ответов. */
    @Valid @NotNull
    private Cache cache = new Cache();

    /** Настройки полнотекстового поиска. */
    @Valid @NotNull
    private Search search = new Search();

    /** Настройки импорта вопросов из markdown. */
    @Valid @NotNull
    private Import importSettings = new Import();

    /** Настройки автоматических итераций улучшения качества AI-вариантов. */
    @Valid @NotNull
    private QualityIterations qualityIterations = new QualityIterations();

    /** Директория данных (SQLite и др.), относительно рабочей директории. */
    private String dataDir = "data";

    /** Токен для защиты чувствительных endpoint (`/api/admin/*`, `/api/regenerate`, `/export`). */
    private String adminToken = "";

    /** Лимит вызовов `/api/regenerate` на клиента в минуту. */
    @Min(value = 1, message = "app.regenerate-rate-limit-per-minute должен быть не меньше 1")
    @Max(value = 300, message = "app.regenerate-rate-limit-per-minute должен быть не больше 300")
    private int regenerateRateLimitPerMinute = 20;

    /**
     * Доверять ли заголовку X-Forwarded-For для rate-limit ключа.
     * По умолчанию false — безопасный режим, использующий только remoteAddr.
     */
    private boolean trustForwardedForHeader = false;

    // ========== Вложенные классы конфигурации ==========

    /**
     * Параметры тестирования, управляющие логикой тренировок и экзаменов.
     *
     * <p>Настраиваются через {@code app.interview.*}.</p>
     */
    @Getter @Setter
    public static class Interview {
        /** Количество вариантов ответа на каждый вопрос. */
        @Min(value = 2, message = "app.interview.options-count должен быть не меньше 2")
        @Max(value = 10, message = "app.interview.options-count должен быть не больше 10")
        private int optionsCount = 4;

        /** Порог повторений, после которого вопрос считается «выученным». */
        @Min(value = 1, message = "app.interview.learned-repetitions должен быть не меньше 1")
        private int learnedRepetitions = 3;

        /** Количество дополнительных (штрафных) вопросов при ошибке в режиме экзамена. */
        @Min(value = 0, message = "app.interview.exam-penalty-questions не может быть отрицательным")
        private int examPenaltyQuestions = 5;

        /** Максимальная длина варианта ответа (символов). */
        @Min(50) @Max(2000)
        private int maxOptionLength = 600;

        /** Число дополнительных формулировок вопроса из одного источника (0 = отключено). */
        @Min(0) @Max(5)
        private int expandPerSource = 0;

        /** Максимальное количество вопросов в сессии (защита от DoS). */
        @Min(1) @Max(500)
        private int maxSessionCount = 200;

        /** Удалять ли все варианты ответов при старте приложения (для перегенерации через AI).
         *  По умолчанию {@code false} — безопасно для production. Включайте только для dev-среды. */
        private boolean resetOnStartup = false;

        /** Дефолтное число вопросов в экзамене. */
        @Min(1) @Max(200)
        private int defaultExamCount = 20;

        /** Дефолтное число вопросов в марафоне. */
        @Min(1) @Max(200)
        private int defaultMarathonCount = 50;

        /** Минимальная позиция точки для обрезки по границе предложения (символов). */
        @Min(1)
        private int minSentenceDotPosition = 20;

        /** Максимальная длина explanation для quality-check (символов). */
        @Min(20) @Max(500)
        private int optionExplanationMaxLength = 160;

        /** Порог Jaccard-схожести для дедупликации вариантов. */
        @Min(0) @Max(1)
        private double optionDedupSimilarityThreshold = 0.88;

        /** Максимум повторов генерации при критических quality-ошибках. */
        @Min(0) @Max(10)
        private int optionQualityRetryAttempts = 3;

        /** Минимально допустимое число уникальных вариантов после дедупликации. */
        @Min(2) @Max(10)
        private int optionMinAcceptedCount = 3;

        /** Базовая задержка (мс) между quality-retry попытками. */
        @Min(0) @Max(10_000)
        private long optionQualityRetryBackoffMs = 300;

        /** Верхняя граница времени (мс) на quality-retry цепочку одного вопроса. */
        @Min(200) @Max(60_000)
        private long optionQualityRetryMaxElapsedMs = 8_000;

        /** Маркеры, которые запрещены в тексте опций как "утечка вердикта". */
        private List<String> optionVerdictLeakageMarkers = List.of(
                "неверно:",
                "верно:",
                "ключевое отличие",
                "на самом деле",
                "incorrect:",
                "correct:"
        );

        /** Маркеры intent-профиля для вопросов-сравнений (RU/EN). */
        private List<String> optionComparisonIntentMarkers = List.of(
                "сравн", "разниц", "отлич", "эффективност", "сложност",
                "compare", "difference", "trade-off", "complexity"
        );

        /** Маркеры intent-профиля для вопросов о результате выполнения кода (RU/EN). */
        private List<String> optionCodeResultIntentMarkers = List.of(
                "что выведет", "что вернет", "результат выполнения", "какое значение", "исключени",
                "what prints", "what returns", "output", "exception", "runtime result"
        );

        /** Маркеры intent-профиля для вопросов о механизме работы (RU/EN). */
        private List<String> optionMechanismIntentMarkers = List.of(
                "как ", "каким образом", "какой механизм", "каким способом",
                "how ", "mechanism", "works", "flow"
        );

        /** Маркеры intent-профиля для вопросов-определений (RU/EN). */
        private List<String> optionDefinitionIntentMarkers = List.of(
                "что такое", "что означает", "что называется",
                "what is", "definition", "means", "called"
        );

        /** Переопределение приоритетов Senior-правил по ключу (key -> priority). */
        private Map<String, Integer> seniorRulePriorityOverrides = new ConcurrentHashMap<>();
    }

    /**
     * Общие параметры AI-запросов, одинаковые для всех провайдеров.
     *
     * <p>Настраиваются через {@code app.ai.*}.</p>
     */
    @Getter @Setter
    public static class Ai {
        /** Таймаут HTTP-запроса к AI-провайдеру (секунды). */
        @Min(value = 5, message = "app.ai.timeout-seconds не менее 5")
        private int timeoutSeconds = 30;

        /** Максимальное количество повторных попыток при ошибке 429 (rate limit). */
        @Min(value = 0)
        private int maxRetries = 3;

        /** Минимальная задержка перед повторной попыткой (секунды, экспоненциальный backoff). */
        @Min(value = 1)
        private int retryBackoffSeconds = 2;

        /** Максимальная длина ввода в AI (символов). */
        @Min(1000)
        private int maxInputLength = 10_000;

        /** Длина превью вопроса в логах (символов). */
        @Min(10)
        private int questionPreviewLength = 60;

        /** Длина превью ответа при передаче в AI (символов). */
        @Min(100)
        private int answerPreviewLength = 500;

        /** Длина превью правильного ответа в логах (символов). */
        @Min(10)
        private int correctPreviewLength = 80;

    }

    /**
     * Конфигурация DeepSeek API.
     *
     * <p>Настраивается через {@code app.deepseek.*}.</p>
     */
    @Getter @Setter
    public static class DeepSeek {
        /** Базовый URL без пути (например, {@code https://api.deepseek.com}). */
        private String baseUrl = "https://api.deepseek.com";

        /** Путь к эндпоинту чата. */
        private String endpoint = "/v1/chat/completions";

        /** Название модели. */
        private String model = "deepseek-chat";

        /** API-ключ (должен передаваться через переменную окружения). */
        private String apiKey;

        /** Температура генерации (0.0 — детерминированно, 1.0 — творчески). */
        private double temperature = 0.7;
    }

    /**
     * Конфигурация OpenAI API (ChatGPT).
     *
     * <p>Настраивается через {@code app.openai.*}.</p>
     */
    @Getter @Setter
    public static class OpenAi {
        /** Базовый URL (для ChatGPT: {@code https://api.openai.com}). */
        private String baseUrl = "https://api.openai.com";

        /** Путь к эндпоинту. */
        private String endpoint = "/v1/chat/completions";

        /** Модель (например, {@code gpt-4.1-mini}, {@code gpt-4o}). */
        private String model = "gpt-4.1-mini";

        /** API-ключ. */
        private String apiKey;

        /** Температура генерации. */
        private double temperature = 0.7;
    }

    /**
     * Настройки фонового предзагрузчика вариантов ответов.
     *
     * <p>Предзагрузчик заранее генерирует варианты для следующих вопросов,
     * чтобы ускорить отображение сессии тестирования.</p>
     *
     * <p>Настраивается через {@code app.preload.*}.</p>
     */
    @Getter @Setter
    public static class Preload {
        /** Количество вопросов, загружаемых за одну итерацию. */
        @Min(1) private int batchSize = 5;

        /** Минимальное количество потоков в пуле предзагрузчика. */
        @Min(1) private int corePoolSize = 2;

        /** Максимальное количество потоков в пуле предзагрузчика. */
        @Min(1) private int maxPoolSize = 4;

        /** Размер очереди задач предзагрузчика. */
        @Min(1) private int queueCapacity = 100;

        /** Пауза между запросами к AI при предзагрузке (мс). */
        @Min(100) private long sleepMs = 1000;

        /** Максимальный размер одной очереди предзагрузки. */
        @Min(1) private int maxQueueSize = 50;

        /** Максимальное количество одновременных очередей (по комбинациям фильтров). */
        @Min(1) private int maxQueues = 20;

        /** Генерировать AI-варианты для ВСЕХ вопросов при старте (фоновый процесс). */
        private boolean fullWarmup = true;

        /**
         * Лимит вопросов для полного прогрева при старте.
         * <ul>
         *   <li>0 — без лимита (прогрев всех вопросов без опций);</li>
         *   <li>>0 — прогрев только первых N вопросов (тестовый режим экономии токенов).</li>
         * </ul>
         */
        @Min(0)
        private int warmupLimit = 0;

        /**
         * Упрощенный тестовый режим прогрева.
         * Если включен и {@code warmupLimit == 0}, автоматически применяется лимит 10 вопросов.
         */
        private boolean testMode = false;

        /**
         * Seed для рандомного поднабора warmup (только при {@code warmupLimit > 0}).
         * <ul>
         *   <li>-1 — недетерминированный random на каждый запуск;</li>
         *   <li>>=0 — фиксированный seed для воспроизводимости.</li>
         * </ul>
         */
        @Min(-1)
        private long warmupRandomSeed = -1;

        /** Логировать прогресс предзагрузки каждые N вопросов. */
        @Min(1)
        private int logProgressEvery = 50;
    }

    /**
     * Настройки автоматического цикла оценки и улучшения качества вопросов/вариантов.
     *
     * <p>Используются раннером, который выполняет reset -> generation -> scoring -> prompt tuning.</p>
     */
    @Getter @Setter
    public static class QualityIterations {
        /** Включить автозапуск 10-итерационного цикла при старте приложения. */
        private boolean enabled = false;

        /** Количество итераций в одном прогоне цикла качества. */
        @Min(1) @Max(100)
        private int iterations = 10;

        /** Размер сэмпла вопросов на итерацию (для генерации и оценки). */
        @Min(1) @Max(500)
        private int sampleSize = 10;

        /** Целевой средний score (0..100), используется как ориентир для тюнинга промпта. */
        @Min(0) @Max(100)
        private int targetScore = 90;

        /** Пауза между итерациями в миллисекундах. */
        @Min(0) @Max(60_000)
        private long sleepBetweenIterationsMs = 0;

        /** Ограничение на число одновременно активных auto-rules в prompt context. */
        @Min(1) @Max(20)
        private int maxPromptRules = 6;
    }

    /**
     * Настройки полнотекстового поиска.
     *
     * <p>Настраивается через {@code app.search.*}.</p>
     */
    @Getter @Setter
    public static class Search {
        /** Максимальное количество результатов поиска (защита от тяжёлых запросов). */
        @Min(1) @Max(500)
        private int maxLimit = 100;

        /** Максимальная длина превью ответа в поиске (символов). */
        @Min(50)
        private int previewMaxLength = 300;

        /** Лимит результатов поиска на странице статистики. */
        @Min(1) @Max(100)
        private int statsResultsLimit = 20;
    }

    /**
     * Настройки импорта вопросов из markdown.
     *
     * <p>Настраивается через {@code app.import.*}.</p>
     */
    @Getter @Setter
    public static class Import {
        /** Минимальная длина code block для определения типа вопроса CODE (символов). */
        @Min(10)
        private int minCodeBlockLength = 50;
    }

    /**
     * Настройки SQLite.
     *
     * <p>Настраивается через {@code app.sqlite.*}.</p>
     */
    @Getter @Setter
    public static class Sqlite {
        /** Включить WAL-режим для лучшей конкурентной производительности. */
        private boolean enableWal = true;
    }

    /**
     * Настройки Caffeine-кэша для вариантов ответов.
     *
     * <p>Настраивается через {@code app.cache.*}.</p>
     */
    @Getter @Setter
    public static class Cache {
        /** Максимальное количество записей в кэше. */
        @Min(value = 1, message = "app.cache.option-max-size должен быть не меньше 1")
        private int optionMaxSize = 500;

        /** Время жизни записей в кэше (часы). */
        @Min(value = 1, message = "app.cache.ttl-hours должен быть не меньше 1")
        private int ttlHours = 1;
    }
}
