package com.cheatsheet.quiz.service.ai.profile;

import lombok.Builder;
import java.util.List;

public final class SeniorInterviewRuleCatalog {
    @Builder(toBuilder = true)
    public record RuleDefinition(String key, List<String> scopeTokens, List<String> questionTokens, String trapHint, int priority) {}

    private static final List<String> JAVA_SCOPE = List.of("java", "jvm", "programming-languages/java");
    private static final List<String> SPRING_SCOPE = List.of("spring", "spring-boot", "spring-framework");
    private static final List<String> SQL_SCOPE = List.of("sql", "postgres", "mysql", "database", "db");
    private static final List<String> ARCH_SCOPE = List.of("architecture", "system-design", "microservice", "distributed");

    private static final List<RuleDefinition> RULES = List.of(
            rule("equals-vs-hashcode", JAVA_SCOPE, List.of("equals", "hashcode"),
                    "Ложные варианты: корректный equals без согласованного hashCode, mutable поля в hashCode, сравнение по ссылке."),
            rule("hashmap-vs-concurrenthashmap", JAVA_SCOPE, List.of("hashmap", "concurrenthashmap"),
                    "Ложные варианты: global lock на всю map, null-key parity, fail-fast vs weakly-consistent итерация."),
            rule("arraylist-vs-linkedlist", JAVA_SCOPE, List.of("arraylist", "linkedlist"),
                    "Ложные варианты: O(1) вставка в середину без поиска узла, кэш-локальность и стоимость random access."),
            rule("optional", JAVA_SCOPE, List.of("optional"),
                    "Ложные варианты: Optional в полях/JPA, Optional.of(null), попытка заменить им обработку ошибок."),
            rule("string-pool", JAVA_SCOPE, List.of("string", "pool"),
                    "Ложные варианты: '==' всегда корректно для String, intern без memory tradeoff, compile-time и runtime конкатенация."),
            rule("gc-collectors", JAVA_SCOPE, List.of("gc", "collector"),
                    "Ложные варианты: путать throughput и latency, игнорировать pause-time цели, миф о 'полном отсутствии stop-the-world'."),
            rule("jmm-volatile", JAVA_SCOPE, List.of("volatile"),
                    "Ложные варианты: volatile как замена атомарности, отсутствие happens-before, reorder visibility misconceptions."),
            rule("synchronized-vs-lock", JAVA_SCOPE, List.of("synchronized", "lock"),
                    "Ложные варианты: fairness by default, lock reentrancy misconceptions, lock-free claims для synchronized."),
            rule("threadpool", JAVA_SCOPE, List.of("threadpool", "executor"),
                    "Ложные варианты: unbounded queue без backpressure, fixed pool для blocking I/O, shutdownNow semantics myths."),
            rule("completablefuture", JAVA_SCOPE, List.of("completablefuture"),
                    "Ложные варианты: thenApply/thenCompose путаница, блокировка join на request thread, исключения теряются."),
            rule("stream-parallel", JAVA_SCOPE, List.of("parallel", "stream"),
                    "Ложные варианты: parallelStream быстрее всегда, общий ForkJoinPool без contention, side effects safety."),
            rule("java-memory-leak", JAVA_SCOPE, List.of("memory", "leak"),
                    "Ложные варианты: leak only in native code, forgotten listeners/caches weak refs, classloader leaks."),
            rule("spring-transactional", SPRING_SCOPE, List.of("transactional"),
                    "Ложные варианты: self-invocation активирует прокси, checked exception rollback по умолчанию, readOnly запрещает write на уровне JDBC.",
                    3),
            rule("spring-proxy", SPRING_SCOPE, List.of("proxy", "aop"),
                    "Ложные варианты: private/final методы перехватываются, JDK vs CGLIB различия игнорируются."),
            rule("spring-bean-scope", SPRING_SCOPE, List.of("bean", "scope"),
                    "Ложные варианты: prototype авто-destroy lifecycle, singleton state в web-контексте без race risks."),
            rule("spring-autoconfiguration", SPRING_SCOPE, List.of("autoconfiguration", "spring"),
                    "Ложные варианты: @EnableAutoConfiguration грузит всё подряд, порядок condition-ов не важен."),
            rule("spring-security-filter-chain", SPRING_SCOPE, List.of("security", "filter"),
                    "Ложные варианты: порядок фильтров не влияет, csrf всегда можно отключать для session apps."),
            rule("hibernate-n-plus-one", SPRING_SCOPE, List.of("n+1", "hibernate"),
                    "Ложные варианты: eager решает N+1 всегда, join fetch без дубликатов/пагинации рисков."),
            rule("jpa-entity-lifecycle", SPRING_SCOPE, List.of("entity", "lifecycle"),
                    "Ложные варианты: detached updates auto-merge, flush == commit, persistence context myths."),
            rule("optimistic-vs-pessimistic-lock", SPRING_SCOPE, List.of("optimistic", "pessimistic"),
                    "Ложные варианты: optimistic всегда быстрее, lock escalation myths, retry strategy absent."),
            rule("sql-join-vs-exists", SQL_SCOPE, List.of("join", "exists"),
                    "Ложные варианты: JOIN и EXISTS всегда эквивалентны по плану, дубликаты из JOIN игнорируются."),
            rule("sql-null-semantics", SQL_SCOPE, List.of("null", "sql"),
                    "Ложные варианты: NULL = NULL true, NOT IN with NULL behaves like NOT EXISTS.",
                    2),
            rule("sql-index-composite", SQL_SCOPE, List.of("index", "composite"),
                    "Ложные варианты: порядок колонок в составном индексе не важен, covering index myths."),
            rule("sql-transaction-isolation", SQL_SCOPE, List.of("isolation", "transaction"),
                    "Ложные варианты: repeatable read исключает все аномалии, snapshot == serializable."),
            rule("sql-deadlock", SQL_SCOPE, List.of("deadlock"),
                    "Ложные варианты: deadlock only under high load, retry not needed, lock ordering ignored."),
            rule("sql-explain-plan", SQL_SCOPE, List.of("explain", "plan"),
                    "Ложные варианты: estimated rows всегда точны, seq scan всегда плохо."),
            rule("redis-cache-aside", ARCH_SCOPE, List.of("cache", "redis"),
                    "Ложные варианты: cache-aside без stampede защиты, TTL одинаковый для всех ключей."),
            rule("rest-idempotency", ARCH_SCOPE, List.of("idempot", "rest"),
                    "Ложные варианты: POST idempotent by default, PUT partial update semantics confusion."),
            rule("microservice-saga", ARCH_SCOPE, List.of("saga"),
                    "Ложные варианты: distributed transaction == 2PC everywhere, compensation can be skipped."),
            rule("kafka-exactly-once", ARCH_SCOPE, List.of("kafka", "exactly"),
                    "Ложные варианты: exactly-once without idempotent producer/transactions, consumer retries without dedup.",
                    2)
    );

    private SeniorInterviewRuleCatalog() {
    }

    public static List<RuleDefinition> rules() {
        return RULES;
    }

    private static RuleDefinition rule(String key, List<String> scopeTokens, List<String> questionTokens, String trapHint) {
        return new RuleDefinition(key, scopeTokens, questionTokens, trapHint, 0);
    }

    private static RuleDefinition rule(String key, List<String> scopeTokens, List<String> questionTokens, String trapHint, int priority) {
        return new RuleDefinition(key, scopeTokens, questionTokens, trapHint, priority);
    }
}
