package com.cheatsheet.quiz.config.app;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    /** Настройки тестирования (пороги, лимиты, defaults). */
    @Valid @NotNull
    private Interview interview = new Interview();

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

    /** Выбор алгоритма интервального повторения. */
    @Valid @NotNull
    private Scheduling scheduling = new Scheduling();

    /**
     * Если true — при старте приложения чистит таблицы questions/answer_options
     * (TRUNCATE … RESTART IDENTITY CASCADE) и переимпортирует всё из
     * cheatsheets/interview/**.md + seed/mcq/**.json. AI не вызывается.
     * <p>Привязан к {@code INTERVIEW_RESET_ON_STARTUP} env-var.
     */
    private boolean interviewResetOnStartup = false;

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
        /** Порог повторений, после которого вопрос считается «выученным». */
        @Min(value = 1, message = "app.interview.learned-repetitions должен быть не меньше 1")
        private int learnedRepetitions = 3;

        /** Количество дополнительных (штрафных) вопросов при ошибке в режиме экзамена. */
        @Min(value = 0, message = "app.interview.exam-penalty-questions не может быть отрицательным")
        private int examPenaltyQuestions = 5;

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
     * Выбор планировщика интервального повторения.
     *
     * <p>Настраивается через {@code app.scheduling.algorithm}. По умолчанию
     * {@code sm2} (текущее поведение, fallback). {@code fsrs} включает FSRS-4.5
     * (после валидации на своих данных можно сделать дефолтом).</p>
     */
    @Getter @Setter
    public static class Scheduling {
        /** Алгоритм планировщика: {@code sm2} | {@code fsrs}. */
        @Pattern(regexp = "sm2|fsrs", message = "app.scheduling.algorithm: допустимо sm2 | fsrs")
        private String algorithm = "sm2";

        /** {@code true}, если выбран FSRS-планировщик. */
        public boolean isFsrs() {
            return "fsrs".equalsIgnoreCase(algorithm);
        }
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
