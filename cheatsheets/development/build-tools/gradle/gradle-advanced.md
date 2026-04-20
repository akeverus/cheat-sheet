---
title: "Gradle Advanced"
description: "Gradle - это мощная система автоматизации сборки с открытым исходным кодом, которая использует Groovy или Kotlin DSL для описания build скриптов. Этот документ охватывает продвинутые концепции, паттерны и best practices для enterprise-grade проектов на Gradle."
tags:
  - development
  - build-tools
  - gradle-advanced
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Gradle Advanced

**Gradle** — это мощная система автоматизации сборки с открытым исходным кодом, которая использует **Groovy** или **Kotlin DSL** для описания **build** скриптов. Этот документ охватывает продвинутые концепции, паттерны и **best practices** для **enterprise-grade** проектов на **Gradle**.

## Полезные ссылки
- [Gradle Documentation](https://docs.gradle.org/current/userguide/userguide.html)
- [Gradle Plugin Portal](https://plugins.gradle.org/)
- [Gradle GitHub](https://github.com/gradle/gradle)
- [Gradle Build Cache](https://docs.gradle.org/current/userguide/build_cache.html)
- [Gradle Enterprise](https://gradle.com/enterprise/)
- [Version Catalogs](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog)


### См. также
- [[maven|Maven (основы)]]
## Содержание

- [Продвинутая конфигурация проекта](#продвинутая-конфигурация-проекта)
  - [Много-модульные проекты](#много-модульные-проекты)
    - [Настройка композитной сборки](#настройка-композитной-сборки)
    - [Version Catalogs](#version-catalogs)
    - [Gradle.properties для больших проектов](#gradleproperties-для-больших-проектов)
  - [Продвинутые плагины](#продвинутые-плагины)
    - [Кастомный плагин](#кастомный-плагин)
    - [Convention Plugin](#convention-plugin)
- [Продвинутые задачи и автоматизация](#продвинутые-задачи-и-автоматизация)
  - [Кастомные задачи](#кастомные-задачи)
  - [Build lifecycle hooks](#build-lifecycle-hooks)
- [Управление зависимостями](#управление-зависимостями)
  - [Продвинутые конфигурации зависимостей](#продвинутые-конфигурации-зависимостей)
  - [Dependency locking](#dependency-locking)
- [Тестирование и качество кода](#тестирование-и-качество-кода)
  - [Продвинутые тестовые конфигурации](#продвинутые-тестовые-конфигурации)
  - [Mutation testing](#mutation-testing)
- [Публикация и доставка](#публикация-и-доставка)
  - [Multi-repository publishing](#multi-repository-publishing)
  - [Docker integration](#docker-integration)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Build cache и incremental builds](#build-cache-и-incremental-builds)
  - [Memory и performance tuning](#memory-и-performance-tuning)
- [CI/CD интеграция](#cicd-интеграция)
  - [Jenkins pipeline](#jenkins-pipeline)
  - [GitHub Actions](#github-actions)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [Performance monitoring](#performance-monitoring)
- [Лучшие практики](#лучшие-практики)
  - [Enterprise project structure](#enterprise-project-structure)
  - [Quality gates](#quality-gates)
  - [Release management](#release-management)
- [См. также](#см-также)

## Продвинутая конфигурация проекта

### Много-модульные проекты

#### Настройка композитной сборки
Ниже — настройка многомодульного проекта и композитной сборки (Kotlin `DSL`, `settings.gradle`.kts).
```kotlin
// settings.gradle.kts
rootProject.name = "enterprise-project"

// Включение подпроектов
include("core")
include("web")
include("api")
include("infrastructure")
include("testing")

// Композитная сборка для связанных проектов
includeBuild("../shared-libraries")
includeBuild("../infrastructure-tools")

// Конфигурация plugin management
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/milestone") }
        mavenLocal()
    }

    plugins {
        id("org.springframework.boot") version "3.1.0"
        id("io.spring.dependency-management") version "1.1.0"
        id("org.jetbrains.kotlin.jvm") version "1.8.20"
        id("org.jetbrains.kotlin.plugin.spring") version "1.8.20"
        id("com.github.johnrengelman.shadow") version "8.1.1"
        id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
        id("com.diffplug.spotless") version "6.18.0"
    }
}

// Конфигурация dependency resolution
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        mavenLocal()
    }

    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}
```

#### Version Catalogs
```toml
# gradle/libs.versions.toml
[versions]
kotlin = "1.8.20"
springBoot = "3.1.0"
junit = "5.9.2"
mockito = "5.1.1"
testcontainers = "1.18.3"

[libraries]
# Spring Boot
spring-boot-starter-web = { module = "org.springframework.boot:spring-boot-starter-web", version.ref = "springBoot" }
spring-boot-starter-data-jpa = { module = "org.springframework.boot:spring-boot-starter-data-jpa", version.ref = "springBoot" }
spring-boot-starter-security = { module = "org.springframework.boot:spring-boot-starter-security", version.ref = "springBoot" }
spring-boot-configuration-processor = { module = "org.springframework.boot:spring-boot-configuration-processor", version.ref = "springBoot" }

# Kotlin
kotlin-stdlib-jdk8 = { module = "org.jetbrains.kotlin:kotlin-stdlib-jdk8", version.ref = "kotlin" }
kotlin-reflect = { module = "org.jetbrains.kotlin:kotlin-reflect", version.ref = "kotlin" }

# Testing
junit-jupiter = { module = "org.junit.jupiter:junit-jupiter", version.ref = "junit" }
mockito-core = { module = "org.mockito:mockito-core", version.ref = "mockito" }
testcontainers-junit-jupiter = { module = "org.testcontainers:junit-jupiter", version.ref = "testcontainers" }

[bundles]
spring-boot = ["spring-boot-starter-web", "spring-boot-starter-data-jpa", "spring-boot-starter-security"]
kotlin = ["kotlin-stdlib-jdk8", "kotlin-reflect"]
testing = ["junit-jupiter", "mockito-core", "testcontainers-junit-jupiter"]

[plugins]
spring-boot = { id = "org.springframework.boot", version.ref = "springBoot" }
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
```

#### Gradle.properties для больших проектов
```properties
# gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
org.gradle.daemon=true
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g -Dfile.encoding=UTF-8

# Build cache
org.gradle.buildcache.local.enabled=true
org.gradle.buildcache.remote.enabled=true
org.gradle.buildcache.remote.url=https://gradle-cache.example.com/cache/

# Version management
version=1.0.0-SNAPSHOT
group=com.example.enterprise

# Repository credentials
artifactory_username=user
artifactory_password=password
nexus_username=user
nexus_password=password

# Custom properties
buildProfile=development
enableIntegrationTests=true
enableCodeCoverage=true
enableSonarQube=true
```

### Продвинутые плагины

#### Кастомный плагин
```kotlin
// buildSrc/src/main/kotlin/com/example/EnterprisePlugin.kt
package com.example

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.repositories

class EnterprisePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            // Применение стандартных плагинов
            apply(plugin = "java")
            apply(plugin = "maven-publish")

            // Настройка Java
            configure<JavaPluginExtension> {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            // Настройка тестирования
            tasks.named<Test>("test") {
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                }
                finalizedBy("jacocoTestReport")
            }

            // Настройка репозиториев
            repositories {
                mavenCentral()
                maven {
                    name = "Artifactory"
                    url = uri("https://artifactory.example.com/libs-release")
                    credentials {
                        username = findProperty("artifactory_username") as? String
                        password = findProperty("artifactory_password") as? String
                    }
                }
            }

            // Настройка публикации
            configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("maven") {
                        from(components["java"])
                        groupId = project.group.toString()
                        artifactId = project.name
                        version = project.version.toString()
                    }
                }
            }
        }
    }
}
```

#### Convention Plugin
```kotlin
// buildSrc/src/main/kotlin/com/example/JavaConventionPlugin.kt
package com.example

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

class JavaConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            apply(plugin = "java")

            // Настройка Java compilation
            tasks.withType<JavaCompile>().configureEach {
                options.compilerArgs.addAll(listOf(
                    "-Xlint:all",
                    "-Xlint:-processing",
                    "-parameters",
                    "-Werror"
                ))
                options.encoding = "UTF-8"
            }

            // Настройка source sets
            configure<JavaPluginExtension> {
                sourceSets {
                    create("integrationTest") {
                        compileClasspath += sourceSets.main.get().output
                        runtimeClasspath += sourceSets.main.get().output
                    }
                }
            }

            // Настройка manifest
            tasks.named<Jar>("jar") {
                manifest {
                    attributes(
                        "Implementation-Title" to project.name,
                        "Implementation-Version" to project.version,
                        "Implementation-Vendor" to "Example Corp",
                        "Main-Class" to findProperty("mainClass") ?: "com.example.Application"
                    )
                }
            }
        }
    }
}
```

## Продвинутые задачи и автоматизация

### Кастомные задачи
```kotlin
// build.gradle.kts
import org.gradle.api.tasks.*

abstract class GenerateVersionFileTask : DefaultTask() {
    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val buildNumber: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val content = """
            package com.example;
            public class Version {
                public static final String VERSION = "${version.get()}";
                public static final String BUILD_NUMBER = "${buildNumber.get()}";
                public static final String BUILD_TIME = "${java.time.Instant.now()}";
            }
        """.trimIndent()

        outputFile.get().asFile.writeText(content)
    }
}

abstract class DockerBuildTask : Exec() {
    @get:Input
    abstract val imageName: Property<String>

    @get:Input
    abstract val dockerfile: RegularFileProperty

    @get:InputDirectory
    abstract val contextDir: DirectoryProperty

    init {
        executable = "docker"
        args("build", "-f", dockerfile.get().asFile.absolutePath,
             "-t", imageName.get(), contextDir.get().asFile.absolutePath)
    }
}

abstract class CodeQualityTask : DefaultTask() {
    @TaskAction
    fun check() {
        val violations = project.fileTree("src/main/java").matching {
            include("/*.java")
            exclude("/generated/")
        }.files.flatMap { file ->
            // Custom code quality checks
            val lines = file.readLines()
            val issues = mutableListOf<String>()

            lines.forEachIndexed { index, line ->
                if (line.contains("System.out.println")) {
                    issues.add("${file.name}:${index + 1}: Avoid System.out.println")
                }
                if (line.length > 120) {
                    issues.add("${file.name}:${index + 1}: Line too long (${line.length} chars)")
                }
            }

            issues
        }

        if (violations.isNotEmpty()) {
            violations.forEach { logger.error(it) }
            throw GradleException("Code quality check failed")
        }
    }
}

// Регистрация задач
tasks.register<GenerateVersionFileTask>("generateVersionFile") {
    version.set(project.version.toString())
    buildNumber.set(System.getenv("BUILD_NUMBER") ?: "local")
    outputFile.set(project.layout.buildDirectory.file("generated/sources/version/com/example/Version.java"))
}

tasks.register<DockerBuildTask>("buildDocker") {
    imageName.set("${project.name}:${project.version}")
    dockerfile.set(project.layout.projectDirectory.file("Dockerfile"))
    contextDir.set(project.layout.projectDirectory.dir("."))
    dependsOn("build")
}

tasks.register<CodeQualityTask>("codeQuality")
tasks.named("check").configure { dependsOn("codeQuality") }
```

### Build lifecycle hooks
```kotlin
// build.gradle.kts
import org.gradle.api.tasks.*

// Lifecycle hooks
gradle.taskGraph.whenReady { taskGraph ->
    if (taskGraph.hasTask(":publish")) {
        logger.lifecycle("🚀 Publishing artifacts...")
    }

    if (taskGraph.hasTask(":test")) {
        logger.lifecycle("🧪 Running tests...")
    }
}

// Build finished listener
gradle.buildFinished { result ->
    when (result.failure) {
        null -> logger.lifecycle("✅ Build successful!")
        else -> logger.error("❌ Build failed: ${result.failure?.message}")
    }
}

// Project evaluation listener
gradle.afterProject { project ->
    project.logger.info("📦 Configuring project: ${project.name}")

    // Custom configuration based on project type
    if (project.plugins.hasPlugin("java")) {
        project.logger.info("  📚 Java project detected")
    }

    if (project.plugins.hasPlugin("org.springframework.boot.gradle.plugin")) {
        project.logger.info("  🌱 Spring Boot project detected")
    }
}

// Custom build phases
tasks.register("prepare") {
    group = "build"
    description = "Prepare build environment"
    doLast {
        logger.lifecycle("🔧 Preparing build environment...")
        // Custom preparation logic
    }
}

tasks.register("verify") {
    group = "verification"
    description = "Run all verification tasks"
    dependsOn("check", "integrationTest", "codeQuality")
}

tasks.register("package") {
    group = "build"
    description = "Package application"
    dependsOn("bootJar", "buildDocker")
}

tasks.named("assemble").configure {
    dependsOn("prepare")
}

tasks.named("build").configure {
    dependsOn("verify")
}
```

## Управление зависимостями

### Продвинутые конфигурации зависимостей
```kotlin
// build.gradle.kts
dependencies {
    // Platform BOM для управления версиями
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.1.0"))
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.20"))

    // Стандартные зависимости
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit-vintage-engine")  // Исключаем старый JUnit
    }

    // Custom configuration
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Native dependencies (для GraalVM)
    implementation("org.springframework.experimental:graphql-support:1.0.0-RC3")

    // Optional dependencies
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Version conflict resolution
    implementation("com.google.guava:guava:31.1-jre") {
        because("Version 32+ requires Java 11+")
    }
}

// Resolution strategy
configurations.all {
    resolutionStrategy {
        // Force versions
        force("com.fasterxml.jackson.core:jackson-databind:2.15.0")

        // Fail on version conflict
        failOnVersionConflict()

        // Custom version selection
        eachDependency {
            when {
                requested.group == "com.fasterxml.jackson.core" -> {
                    useVersion("2.15.0")
                }
                requested.name.startsWith("spring-") -> {
                    useVersion("6.0.0")
                }
            }
        }

        // Component selection rules
        componentSelection {
            all { selection ->
                if (selection.candidate.version.contains("alpha") ||
                    selection.candidate.version.contains("beta")) {
                    selection.reject("Alpha/Beta versions not allowed in production")
                }
            }
        }
    }
}
```

### Dependency locking
```bash
# Создание lock файла
./gradlew dependencies --write-locks

# Проверка на изменения зависимостей
./gradlew dependencies --update-locks

# Lock файл для production builds
dependencyLocking {
    lockAllConfigurations()
}
```

## Тестирование и качество кода

### Продвинутые тестовые конфигурации
```kotlin
// build.gradle.kts
plugins {
    jacoco
    id("org.sonarqube") version "4.2.1.3168"
    id("com.diffplug.spotless") version "6.18.0"
}

// Test suites
testing {
    suites {
        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("org.testcontainers:junit-jupiter")
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(tasks.test)
                        group = "verification"
                        testLogging {
                            events("passed", "skipped", "failed")
                        }
                    }
                }
            }
        }

        val performanceTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("com.github.tomakehurst:wiremock-jre8:2.35.0")
            }
        }
    }
}

// JaCoCo coverage
jacoco {
    toolVersion = "0.8.8"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test, tasks.integrationTest)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("/config/")
                exclude("/entity/")
            }
        })
    )
}

// SonarQube
sonarqube {
    properties {
        property("sonar.projectKey", "enterprise-project")
        property("sonar.projectName", "Enterprise Project")
        property("sonar.coverage.jacoco.xmlReportPaths", jacocoTestReport.reports.xml.outputLocation)
        property("sonar.exclusions", "/generated/,/*.config.*,/entity/")
    }
}

// Spotless code formatting
spotless {
    java {
        googleJavaFormat()
        licenseHeaderFile(rootProject.file("gradle/spotless.license"))
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlin {
        ktlint("0.48.2").userData(mapOf("indent_size" to "4"))
        licenseHeaderFile(rootProject.file("gradle/spotless.license"), "(package |import |@file)")
        trimTrailingWhitespace()
        endWithNewline()
    }
}
```

### Mutation testing
```kotlin
// build.gradle.kts
plugins {
    id("pl.allegro.tech.build.axion-release") version "1.15.3"
    id("info.solidsoft.pitest") version "1.9.11"
}

// Pitest mutation testing
configure<PitestPluginExtension> {
    junit5PluginVersion.set("1.1.0")
    pitest {
        targetClasses.set(listOf("com.example.*"))
        excludedClasses.set(listOf("com.example.config.*", "com.example.entity.*"))
        threads.set(4)
        outputFormats.set(listOf("XML", "HTML"))
        timestampedReports.set(false)
        mutationThreshold.set(80)
        coverageThreshold.set(80)
        avoidCallsTo.set(listOf("java.util.logging", "org.slf4j"))
        excludedMethods.set(listOf("equals", "hashCode", "toString"))
    }
}
```

## Публикация и доставка

### Multi-repository publishing
```kotlin
// build.gradle.kts
plugins {
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.shadowJar.get()) {
                classifier = "shadow"
            }

            pom {
                name.set("Enterprise Library")
                description.set("Core enterprise functionality")
                url.set("https://github.com/example/enterprise-lib")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("team")
                        name.set("Enterprise Team")
                        email.set("team@example.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/example/enterprise-lib.git")
                    developerConnection.set("scm:git:ssh://github.com/example/enterprise-lib.git")
                    url.set("https://github.com/example/enterprise-lib")
                }
            }
        }
    }

    repositories {
        maven {
            name = "artifactory"
            url = uri("https://artifactory.example.com/libs-release-local")
            credentials {
                username = findProperty("artifactory_username") as String?
                password = findProperty("artifactory_password") as String?
            }
        }

        maven {
            name = "sonatype"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) {
                "https://oss.sonatype.org/content/repositories/snapshots/"
            } else {
                "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            })
            credentials {
                username = findProperty("sonatype_username") as String?
                password = findProperty("sonatype_password") as String?
            }
        }
    }
}

// Signing
signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}

// Release management
scmVersion {
    versionCreator("version-with-branch")
    tag {
        prefix.set("v")
        versionSeparator.set("")
    }
}
```

### Docker integration
```kotlin
// build.gradle.kts
plugins {
    id("com.bmuschko.docker-java-application") version "9.4.0"
    id("com.bmuschko.docker-spring-boot-application") version "9.4.0"
}

docker {
    javaApplication {
        baseImage.set("eclipse-temurin:17-jre-alpine")
        maintainer.set("DevOps Team <devops@example.com>")
        ports.set(listOf(8080, 8443))
        images.set(setOf("${project.name}:${project.version}", "${project.name}:latest"))

        jvmArgs.set(listOf(
            "-XX:+UseContainerSupport",
            "-XX:MaxRAMPercentage=75.0",
            "-Djava.security.egd=file:/dev/./urandom"
        ))
    }

    springBootApplication {
        baseImage.set("eclipse-temurin:17-jre-alpine")
        maintainer.set("DevOps Team <devops@example.com>")
        ports.set(listOf(8080))
        images.set(setOf("${project.name}:${project.version}"))

        jvmArgs.set(listOf(
            "-XX:+UseContainerSupport",
            "-XX:MaxRAMPercentage=75.0",
            "-Dspring.profiles.active=production"
        ))
    }

    registryCredentials {
        username.set(findProperty("docker_username") as String?)
        password.set(findProperty("docker_password") as String?)
        url.set("https://index.docker.io/v1/")
    }
}

tasks.named("dockerPush") {
    dependsOn("build")
}
```

## Производительность и оптимизация

### Build cache и incremental builds
```kotlin
// build.gradle.kts
// Build cache configuration
buildCache {
    local {
        isEnabled = true
        directory = file("${gradle.gradleUserHomeDir}/caches/build-cache")
        removeUnusedEntriesAfterDays = 30
    }

    remote<HttpBuildCache> {
        url = uri("https://gradle-cache.example.com/cache/")
        isEnabled = findProperty("gradle.cache.remote.enabled")?.toString()?.toBoolean() ?: false
        isPush = findProperty("gradle.cache.remote.push")?.toString()?.toBoolean() ?: false

        credentials {
            username = findProperty("gradle.cache.username") as String?
            password = findProperty("gradle.cache.password") as String?
        }
    }
}

// Incremental compilation
tasks.withType<JavaCompile> {
    options.incremental = true
}

tasks.withType<KotlinCompile> {
    incremental = true
}

// Parallel execution
gradle.startParameter.isParallelProjectExecutionEnabled = true

// Configuration avoidance
tasks.register("customTask") {
    notCompatibleWithConfigurationCache("Uses task execution avoidance API")
}
```

### Memory и performance tuning
```properties
# gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
org.gradle.daemon=true

# Memory settings
org.gradle.jvmargs=-Xmx8g -XX:MaxMetaspaceSize=2g -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8

# Performance settings
org.gradle.workers.max=8
org.gradle.vfs.watch=true
org.gradle.vfs.verbose=true

# Build cache
org.gradle.buildcache.local.enabled=true
org.gradle.buildcache.remote.enabled=true
org.gradle.buildcache.remote.url=https://gradle-cache.example.com/cache/

# Version management
version=1.0.0-SNAPSHOT
group=com.example.enterprise

# Custom properties
buildProfile=production
enableIntegrationTests=true
enableCodeCoverage=true
enableSonarQube=true
```

## CI/CD интеграция

### Jenkins pipeline
```groovy
// Jenkinsfile
pipeline {
    agent {
        docker {
            image 'gradle:8.1.0-jdk17'
            args '-v gradle-cache:/home/gradle/.gradle'
        }
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Cache') {
            steps {
                sh 'gradle buildCache --enable'
            }
        }

        stage('Build') {
            steps {
                sh 'gradle build --parallel --build-cache --no-daemon'
            }
        }

        stage('Test') {
            steps {
                sh 'gradle test integrationTest --parallel --continue'
            }
            post {
                always {
                    junit '/build/test-results//*.xml'
                    jacoco execPattern: '/build/jacoco/*.exec'
                }
            }
        }

        stage('Code Quality') {
            steps {
                sh 'gradle spotlessCheck sonarqube --no-daemon'
            }
        }

        stage('Security Scan') {
            steps {
                sh 'gradle dependencyCheck --no-daemon'
            }
        }

        stage('Package') {
            steps {
                sh 'gradle bootJar --no-daemon'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'gradle dockerBuild --no-daemon'
            }
        }

        stage('Publish') {
            when {
                anyOf {
                    branch 'main'
                    tag pattern: "v\\d+.*", comparator: "REGEX"
                }
            }
            steps {
                sh 'gradle publish --no-daemon'
            }
        }
    }

    post {
        always {
            sh 'gradle clean --no-daemon || true'
            archiveArtifacts artifacts: 'build/libs/*.jar', allowEmptyArchive: true
            publishHTML target: [
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'build/reports/tests',
                reportFiles: 'index.html',
                reportName: 'Test Report'
            ]
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
            sh 'gradle buildInfo --no-daemon || true'
        }
    }
}
```

### GitHub Actions
```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('/*.gradle*', '/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Build with Gradle
      run: ./gradlew build --parallel --build-cache

    - name: Run tests
      run: ./gradlew test integrationTest --parallel --continue

    - name: Code quality checks
      run: ./gradlew spotlessCheck sonarqube

    - name: Security scan
      run: ./gradlew dependencyCheck

    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: '/build/test-results//*.xml'

    - name: Upload coverage reports
      uses: codecov/codecov-action@v3
      if: success()
      with:
        file: '/build/reports/jacoco//*.xml'

    - name: Build Docker image
      if: github.ref == 'refs/heads/main'
      run: |
        ./gradlew dockerBuild
        docker tag myapp:latest myapp:${{ github.sha }}
        docker push myapp:${{ github.sha }}
```

## Решение проблем

### Распространенные проблемы
```bash
# Очистка кэша
gradle clean
gradle cleanBuildCache
rm -rf ~/.gradle/caches

# Debug build
gradle build --info --stacktrace

# Debug dependency resolution
gradle dependencies --configuration runtimeClasspath
gradle dependencyInsight --dependency spring-boot-starter-web --configuration compileClasspath

# Debug task execution
gradle build --debug
gradle tasks --all

# Memory issues
gradle build --no-daemon --max-workers 2

# Network issues
gradle build --offline
gradle build --refresh-dependencies

# Configuration cache issues
gradle build --no-configuration-cache
gradle cleanConfigurationCache
```

### Performance monitoring
```kotlin
// build.gradle.kts
plugins {
    id("com.gradle.build-scan") version "3.13.4"
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    publishOnFailure()
    uploadInBackground = false

    tag(if (System.getenv("CI") != null) "CI" else "LOCAL")
    tag(project.name)

    value("Branch", System.getenv("GITHUB_REF") ?: "unknown")
    value("Commit", System.getenv("GITHUB_SHA") ?: "unknown")

    link("Source", "https://github.com/example/project")
    link("CI", System.getenv("GITHUB_SERVER_URL") + "/" + System.getenv("GITHUB_REPOSITORY") + "/actions/runs/" + System.getenv("GITHUB_RUN_ID"))
}

// Custom performance monitoring
gradle.taskGraph.whenReady { taskGraph ->
    val startTime = System.currentTimeMillis()

    gradle.buildFinished { result ->
        val duration = System.currentTimeMillis() - startTime
        logger.lifecycle("Build completed in ${duration}ms")

        if (duration > 300000) { // 5 minutes
            logger.warn("⚠️  Build took longer than 5 minutes")
        }

        // Log performance metrics
        val taskCount = taskGraph.allTasks.size
        val executedTasks = taskGraph.allTasks.count { it.state.executed }

        logger.lifecycle("Tasks: $executedTasks/$taskCount executed")
    }
}
```

## Лучшие практики

### Enterprise project structure
```text
enterprise-project/
├── gradle/
│   ├── wrapper/
│   ├── libs.versions.toml
│   └── spotless.license
├── buildSrc/
│   ├── src/main/kotlin/
│   │   └── com/example/
│   │       ├── EnterprisePlugin.kt
│   │       ├── JavaConventionPlugin.kt
│   │       └── KotlinConventionPlugin.kt
│   └── build.gradle.kts
├── core/
│   ├── src/main/java/
│   ├── src/test/java/
│   └── build.gradle.kts
├── web/
│   ├── src/main/kotlin/
│   ├── src/test/kotlin/
│   └── build.gradle.kts
├── api/
│   ├── src/main/java/
│   └── build.gradle.kts
├── infrastructure/
│   ├── src/main/kotlin/
│   └── build.gradle.kts
├── testing/
│   ├── src/main/java/
│   └── build.gradle.kts
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── nginx.conf
├── docs/
├── scripts/
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

### Quality gates
```kotlin
// build.gradle.kts
plugins {
    jacoco
    id("org.sonarsource.scanner.gradle") version "4.2.0.3129"
}

// Quality gates
tasks.register("qualityGate") {
    group = "verification"
    description = "Run all quality checks"

    dependsOn("test", "integrationTest", "jacocoTestReport", "spotlessCheck", "sonarqube")

    doLast {
        // Check test coverage
        val coverageFile = file("${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
        if (coverageFile.exists()) {
            // Parse coverage and fail if below threshold
            val coverage = parseCoverage(coverageFile)
            if (coverage < 80.0) {
                throw GradleException("Code coverage is below 80%: ${coverage}%")
            }
        }

        // Check for security vulnerabilities
        val dependencyCheckReport = file("${buildDir}/reports/dependency-check-report.html")
        if (dependencyCheckReport.exists()) {
            // Parse report and fail on critical vulnerabilities
            val criticalVulns = parseVulnerabilities(dependencyCheckReport)
            if (criticalVulns > 0) {
                throw GradleException("Found $criticalVulns critical security vulnerabilities")
            }
        }
    }
}

tasks.named("build").configure {
    dependsOn("qualityGate")
}
```

### Release management
```kotlin
// build.gradle.kts
plugins {
    id("net.researchgate.release") version "3.0.2"
}

release {
    git {
        requireBranch = "main"
        pushToRemote = "origin"
    }

    preTagCommitMessage = "[Gradle Release Plugin] - pre tag commit: "
    tagCommitMessage = "[Gradle Release Plugin] - creating tag: "
    newVersionCommitMessage = "[Gradle Release Plugin] - new version commit: "

    versionPropertyFile = "gradle.properties"
    versionProperties = ["version"]
}

tasks.named("release") {
    dependsOn("build", "test", "publish")
}

afterReleaseBuild.dependsOn("publish")
```
## См. также
- [[maven-advanced|Maven Advanced]] — альтернативная система сборки
- [[spring-boot|Spring Boot]] — **Java** фреймворк
- [[kotlin-basics|Kotlin]] — язык программирования
- [[docker-basics|Docker]] — контейнеризация
