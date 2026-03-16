---
title: "Maven (основы)"
description: "Apache Maven — система управления проектами и сборки с открытым исходным кодом; использует декларативную модель (POM — Project Object Model) для зависимостей и плагинов. Документ даёт базовые понятия и минимальный старт; продвинутые темы см. в [maven-advanced.md](maven-advanced.m"
tags: ["development", "build-tools", "maven"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Maven (основы)

**Apache Maven** — система управления проектами и сборки с открытым исходным кодом; использует декларативную модель (**POM** — **Project Object Model**) для зависимостей и плагинов. Документ даёт базовые понятия и минимальный старт; продвинутые темы см. в [maven-advanced.md](maven-advanced.md).

**Дата последнего обновления:** 2026-02-11

## Полезные ссылки

- [Официальная документация Maven](https://maven.apache.org/guides/)
- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)
- **См. также:** [maven-advanced.md](maven-advanced.md) — продвинутые концепции, многомодульные проекты, **CI/CD**, **Dependency locking**; [README.md](../) — обзор раздела Build Tools.

## Содержание

- [Введение](#введение)
- [Установка](#установка)
- [Базовая структура проекта](#базовая-структура-проекта)
- [Жизненный цикл и фазы](#жизненный-цикл-и-фазы)
- [Основные команды](#основные-команды)
- [См. также](#см-также)

## Введение

**Maven** стандартизирует структуру каталогов (**convention over configuration**) и управляет зависимостями через центральный репозиторий (**Maven Central**). Ключевые понятия: **POM** (`pom.xml`), **артефакт** (`groupId`, `artifactId`, `version`), **фазы жизненного цикла** (`compile`, `test`, `package`, `install`, `deploy`), **плагины** и **профили**.

## Установка

Скачать бинарный архив с [maven.apache.org/download](https://maven.apache.org/download.cgi), распаковать и добавить `bin` в `PATH`. Либо использовать **Maven Wrapper** (`mvnw`, `mvnw.cmd`) в проекте:

```bash
# Сборка через Maven Wrapper (без установки Maven в систему)
./mvnw clean install
```

## Базовая структура проекта

Минимальный **Java**-проект:

```xml
<!-- Минимальный POM: groupId, artifactId, version и зависимость JUnit для тестов -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

Стандартные каталоги: `src/main/java`, `src/main/resources`, `src/test/java`, `target/` (результат сборки).

## Жизненный цикл и фазы

Основные фазы по порядку: `validate` → `compile` → `test` → `package` → `verify` → `install` → `deploy`. Вызов фазы выполняет её и все предыдущие (например, `mvn package` запускает `compile`, `test`, затем `package`).

## Основные команды

| Команда | Описание |
|--------|----------|
| `mvn clean` | Удаление `target/` |
| `mvn compile` | Компиляция исходников |
| `mvn test` | Запуск тестов |
| `mvn package` | Сборка JAR/WAR (без установки в локальный репозиторий) |
| `mvn install` | Сборка и установка артефакта в локальный репозиторий (`~/.m2/repository`) |
| `mvn dependency:tree` | Дерево зависимостей |

## См. также

- [maven-advanced.md](maven-advanced.md) — многомодульные проекты, **Reactor**, **Dependency locking**, **CI/CD**, **Troubleshooting**.
- [gradle.md](../gradle/gradle.md) — основы **Gradle**.
- [README.md](../) — обзор раздела **Build Tools**.
