plugins {
    java
    jacoco
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":quiz-domain"))
    implementation(project(":quiz-persistence"))

    // --- Web & MVC ---
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.security)

    // --- Flyway миграции ---
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)

    // --- SQLite ---
    implementation(libs.sqlite.jdbc)

    // --- PostgreSQL (профиль postgres, Docker) ---
    runtimeOnly(libs.postgresql)

    // --- Jackson (поддержка Java Time) ---
    implementation(libs.jackson.datatype.jsr310)

    // --- JSON Schema Validation ---
    implementation(libs.json.schema.validator)

    // --- Markdown рендеринг ---
    implementation(libs.flexmark.all)
    implementation(libs.jsoup)

    // --- Spring AI (кастомная модель, OpenAI-совместимый API) ---
    implementation(platform(libs.spring.ai.bom))
    implementation(libs.spring.ai.openai)

    // --- Кэш ---
    implementation(libs.caffeine)

    // --- Утилиты ---
    implementation(libs.guava)
    implementation(libs.commons.lang3)

    // --- Lombok ---
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.spring.boot.configuration.processor)

    // --- DevTools ---
    developmentOnly(libs.spring.boot.devtools)

    // --- OpenAPI / Swagger ---
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // --- Тесты ---
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.archunit.junit5)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    violationRules {
        rule {
            element = "PACKAGE"
            includes = listOf(
                "com.cheatsheet.quiz.api.*",
                "com.cheatsheet.quiz.service.*",
                "com.cheatsheet.quiz.persistence.*"
            )
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.20".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.processResources {
    filesMatching("application.yml") {
        filter<org.apache.tools.ant.filters.ReplaceTokens>(
            "tokens" to mapOf("projectVersion" to project.version.toString())
        )
    }
}

/**
 * Оффлайн-сид вариантов ответа через Claude (Anthropic SDK).
 *
 * Запускает scripts/seed-options.py, который проходит по вопросам без
 * answer_options и заполняет их батчами с prompt-caching на системном промпте.
 *
 * Требуется: pip install anthropic, ANTHROPIC_API_KEY в env.
 *
 * Примеры:
 *   ./gradlew :quiz-app:seedOptions                                                  # все pending, batch=10
 *   ./gradlew :quiz-app:seedOptions -PseedArgs="--dry-run --limit 20"                # посчитать
 *   ./gradlew :quiz-app:seedOptions -PseedArgs="--topic databases/% --batch 15"      # только databases
 *   ./gradlew :quiz-app:seedOptions -PseedArgs="--model claude-opus-4-7 --overwrite" # перегенерить всё на opus
 *
 * Прямой запуск без Gradle тоже работает: `python3 scripts/seed-options.py --help`.
 */
tasks.register<Exec>("seedOptions") {
    group = "application"
    description = "Seeds answer_options via Claude (offline batch, idempotent)."
    workingDir = rootProject.projectDir
    executable = "python3"
    args("scripts/seed-options.py")
    // Дополнительные аргументы через -PseedArgs="..."
    val extra: String? = project.findProperty("seedArgs") as String?
    if (!extra.isNullOrBlank()) {
        args(extra.split(" ").filter { it.isNotBlank() })
    }
    // Прокидываем ANTHROPIC_API_KEY из env хоста
    System.getenv("ANTHROPIC_API_KEY")?.let { environment("ANTHROPIC_API_KEY", it) }
}
