---
title: "Gradle (основы)"
description: "Gradle — система автоматизации сборки с открытым исходным кодом; использует Groovy или Kotlin DSL для описания сборки. Документ даёт базовые понятия и минимальный старт; продвинутые темы см. в [gradle-advanced.md](gradle-advanced.md)."
tags:
  - development
  - build-tools
  - gradle
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Gradle (основы)

**Gradle** — система автоматизации сборки с открытым исходным кодом; использует **Groovy** или **Kotlin DSL** для описания сборки. Документ даёт базовые понятия и минимальный старт; продвинутые темы см. в [gradle-advanced.md](gradle-advanced.md).

## Полезные ссылки

- [Официальная документация Gradle](https://docs.gradle.org/)
- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html)
- **См. также:** [gradle-advanced.md](gradle-advanced.md) — продвинутые концепции, многомодульные проекты, **CI/CD**, **Version Catalogs**, **Docker**; [README.md](../) — обзор раздела Build Tools.

## Содержание

- [Введение](#введение)
- [Установка](#установка)
- [Базовая структура проекта](#базовая-структура-проекта)
- [Основные команды](#основные-команды)
- [См. также](#см-также)

## Введение

**Gradle** заменяет античные **Make** и **Ant** и конкурирует с **Maven**: сборка описывается скриптом (чаще всего `build.gradle.kts` или `build.gradle`). Ключевые понятия: **проект**, **задачи (tasks)**, **плагины**, **конфигурации зависимостей**. Поддержка **incremental build**, **build cache** и **configuration cache** ускоряет повторные сборки.

## Установка

Рекомендуется использовать **Gradle Wrapper** (`gradlew`, `gradlew.bat`), чтобы зафиксировать версию для проекта:

```bash
# Создание wrapper (если проект ещё без него)
gradle wrapper --gradle-version 8.5

# Сборка через wrapper
./gradlew build
```

Локальная установка: скачать дистрибутив с [gradle.org/releases](https://gradle.org/releases/) и добавить `bin` в `PATH`.

## Базовая структура проекта

Минимальный **Java**-проект с **Kotlin DSL**:

```kotlin
// build.gradle.kts
plugins {
    java
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}
```

Файлы: `settings.gradle.kts` (имя проекта и подмодули), `build.gradle.kts` (задачи и зависимости), `gradle/wrapper/` (wrapper).

## Основные команды

| Команда | Описание |
|--------|----------|
| `./gradlew tasks` | Список доступных задач |
| `./gradlew build` | Полная сборка (compile + test + jar) |
| `./gradlew clean` | Удаление `build/` |
| `./gradlew test` | Запуск тестов |
| `./gradlew run` | Запуск приложения (при наличии плагина `application`) |
| `./gradlew dependencies` | Дерево зависимостей |

## См. также

- [[gradle-advanced|gradle-advanced.md]] — многомодульные проекты, **Version Catalogs**, **Spring Boot**, **Kotlin**, **CI/CD**, **Dependency locking**, **Troubleshooting**.
- [[maven|maven.md]] — основы **Maven**.
- [README.md](../) — обзор раздела **Build Tools**.
