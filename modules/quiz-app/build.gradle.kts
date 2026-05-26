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

    // --- PostgreSQL — единственная поддерживаемая БД ---
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
    // Все тесты грузят application-test.yml → Testcontainers Postgres
    // (jdbc:tc:postgresql:16-alpine), даже если в тест-классе нет @ActiveProfiles("test").
    systemProperty("spring.profiles.active", "test")
}

// META-INF/build-info.properties — отдаётся через /actuator/info.
// Полезно операторам видеть, какая версия и когда собрана крутится в продакшене.
springBoot {
    buildInfo {
        properties {
            additional.set(mapOf(
                "name" to project.name,
            ))
        }
    }
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
    onlyIf {
        val testTask = tasks.test.get()
        testTask.state.executed && !testTask.state.skipped
    }
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

// Раньше тут жил task `seedOptions`, дёргавший scripts/seed-options.py для
// офлайн-сидинга `answer_options` через Claude SDK напрямую в SQLite. После
// миграции на PostgreSQL и переход к JSON-сидерам (modules/quiz-app/src/main/
// resources/seed/mcq/) сам скрипт превращён в deprecation-stub. Workflow
// генерации опций теперь через skill mcq-quality-fixer + правка JSON-сидеров;
// загрузка в Postgres — на старте через McqJsonLoader.
