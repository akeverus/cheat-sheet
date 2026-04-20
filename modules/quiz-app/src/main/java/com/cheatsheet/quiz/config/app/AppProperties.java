package com.cheatsheet.quiz.config.app;

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

    /** Настройки тестирования (пороги, лимиты, defaults). */
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

    /** Security-параметры приложения (CORS/CSRF policy). */
    @Valid @NotNull
    private Security security = new Security();

    /** Настройки импорта вопросов из markdown. */
    @Valid @NotNull
    private Import importSettings = new Import();

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

    /**
     * @return true, если хотя бы у одного из AI-провайдеров задан API-ключ
     */
    public boolean isAiEnabled() {
        OpenAi openai = getOpenai();
        DeepSeek deepseek = getDeepseek();
        boolean openaiSet = openai != null && openai.getApiKey() != null && !openai.getApiKey().isBlank();
        boolean deepseekSet = deepseek != null && deepseek.getApiKey() != null && !deepseek.getApiKey().isBlank();
        return openaiSet || deepseekSet;
    }

    // ========== Вложенные классы конфигурации ==========

    /**
     * Параметры тестирования, управляющие логикой тренировок и экзаменов.
     *
     * <p>Настраиваются через {@code app.interview.*}.</p>
     */
    @Getter @Setter
    public static class Interview {
        /** Порог повторений, после которого вопрос считается «выученным». */
        @Min(value = 1, message = "app.interview.learned-repetitions должен быть не меньше 1")
        private int learnedRepetitions = 3;

        /** Количество дополнительных (штрафных) вопросов при ошибке в режиме экзамена. */
        @Min(value = 0, message = "app.interview.exam-penalty-questions не может быть отрицательным")
        private int examPenaltyQuestions = 5;

        /** Число дополнительных формулировок вопроса из одного источника (0 = отключено). */
        @Min(0) @Max(5)
        private int expandPerSource = 0;

        /** Максимальное количество вопросов в сессии (защита от DoS). */
        @Min(1) @Max(500)
        private int maxSessionCount = 200;

        /** Дефолтное число вопросов в экзамене. */
        @Min(1) @Max(200)
        private int defaultExamCount = 20;

        /** Дефолтное число вопросов в марафоне. */
        @Min(1) @Max(200)
        private int defaultMarathonCount = 50;

        /** Переопределение приоритетов Senior-правил по ключу (key -> priority). */
        private Map<String, Integer> seniorRulePriorityOverrides = new ConcurrentHashMap<>();

        /** Значение служебных метаданных для AI-сгенерированных вопросов. */
        @NotBlank
        private String questionGeneratedMetadataValue = "generated";
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

        /** Максимальная длина ввода в AI (символов). */
        @Min(1000)
        private int maxInputLength = 10_000;

        /** Длина превью вопроса в логах (символов). */
        @Min(10)
        private int questionPreviewLength = 60;

        /** Длина превью ответа при передаче в AI (символов). */
        @Min(100)
        private int answerPreviewLength = 500;

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
         * Выполнять ли стартовую предзагрузку первой пачки вопросов в StartupRunner.
         * Если false, вопросы загружаются лениво только по пользовательским действиям.
         */
        private boolean startupPreload = true;

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

    /**
     * Настройки security-политик приложения.
     */
    @Getter @Setter
    public static class Security {
        @Valid @NotNull
        private Cors cors = new Cors();
    }

    /**
     * Настройки CORS для web/api endpoint.
     */
    @Getter @Setter
    public static class Cors {
        /** Разрешённые origin для CORS. */
        private List<String> allowedOrigins = List.of("http://localhost:8080", "http://127.0.0.1:8080");

        /** Разрешённые HTTP-методы. */
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

        /** Разрешённые заголовки запроса. */
        private List<String> allowedHeaders = List.of("Content-Type", "X-Admin-Token", "X-CSRF-TOKEN");

        /** Разрешённые заголовки ответа. */
        private List<String> exposedHeaders = List.of("Retry-After");

        /** Разрешать ли credentials в CORS. */
        private boolean allowCredentials = true;

        /** Время кэширования preflight (секунды). */
        @Min(0) @Max(86_400)
        private long maxAgeSeconds = 3_600;
    }
}
