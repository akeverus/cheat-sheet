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

    // --- SQLite ---
    implementation(libs.sqlite.jdbc)

    // --- Jackson (поддержка Java Time) ---
    implementation(libs.jackson.datatype.jsr310)

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
