---
title: "Maven"
description: "Точка входа в раздел Apache Maven: POM, жизненный цикл, многомодульность, плагины."
tags:
  - meta
  - index
  - build-tools
  - maven
type: "index"
updated: "2026-04-20"
---
# Maven

**Apache Maven** — система управления проектами и сборки JVM-приложений, построенная на декларативном `pom.xml` и convention over configuration. Maven задал индустриальный стандарт: `src/main/java`, Maven Central, координаты `groupId:artifactId:version`. Сильная сторона — предсказуемость и повсеместность инструментов; слабая — меньшая гибкость и отсутствие нативного build-кэша тасков.

Для кого: инженеры, поддерживающие существующие Maven-проекты, готовящие миграцию на Gradle или желающие понять, как работает Maven-экосистема (BOM, reactor, профили, enforcer). Раздел даёт и введение, и enterprise-практики.

## Полезные ссылки

### Основные документы
- [[maven]] — основы: структура, lifecycle, команды, wrapper
- [[maven-advanced]] — многомодульность, плагины, профили, dependency locking, mutation testing, CI/CD

### Соседние разделы
- [[README|Родительский раздел: Build Tools]]
- [[README|Gradle]] — основной конкурент
- [Spring Boot](../../../frameworks/java-frameworks/spring/) — `spring-boot-maven-plugin`
- [[README|CI/CD]]
- [[README|Testing]]

### Внешние ресурсы
- [Maven Documentation](https://maven.apache.org/guides/)
- [Maven Central Search](https://central.sonatype.com/)
- [Maven Plugins](https://maven.apache.org/plugins/)
- [Maven Wrapper](https://maven.apache.org/wrapper/)
- [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/html/)

## Содержание

- [Maven vs Gradle: краткое сравнение](#maven-vs-gradle-краткое-сравнение)
- [Карта тем](#карта-тем)
- [Типичные проблемы Maven и как их лечить](#типичные-проблемы-maven-и-как-их-лечить)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Maven vs Gradle: краткое сравнение

| Критерий | Maven | Gradle |
|----------|-------|--------|
| Язык | XML (`pom.xml`), декларативно | Groovy / Kotlin DSL |
| Предсказуемость | Высокая | Ниже (код в билде) |
| Производительность | Последовательная, без кэша тасков | Incremental + build/config cache |
| Управление версиями | `<dependencyManagement>`, BOM | `libs.versions.toml` |
| Многомодульность | `<modules>` reactor | `settings.gradle`, composite builds |
| Кривая обучения | Ниже | Выше |
| Экосистема плагинов | Огромная, зрелая | Большая, современнее |
| Когда выбрать | Legacy, простые сборки, команды без DSL-опыта | Новый проект, монорепо, Android, нужна скорость |

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Установка, wrapper, структура | [[maven#установка]] |
| POM и координаты | [[maven#базовая-структура-проекта]] |
| Lifecycle, фазы, goals | [[maven#жизненный-цикл-и-фазы]] |
| Многомодульные проекты и reactor | [[maven-advanced#много-модульные-проекты]] |
| Профили (`-P`) и `settings.xml` | [[maven-advanced#профили-и-конфигурация]] |
| Dependency locking, BOM | [[maven-advanced#dependency-locking]] |
| Кастомные плагины | [[maven-advanced#кастомные-плагины]] |
| Mutation testing, quality | [[maven-advanced#mutation-testing]] |

## Типичные проблемы Maven и как их лечить

- **Медленная сборка.** Использовать `-T 1C` (parallel), inclemental modules `-pl`, mvnd (Maven Daemon). Для кэша — Gradle Enterprise Develocity Maven plugin.
- **Конфликты версий (dependency hell).** `mvn dependency:tree` `<dependencyManagement>` с BOM enforcer-rules (`dependencyConvergence`).
- **Непрозрачные плагины.** `mvn help:effective-pom`, `mvn help:effective-settings`.
- **Дрифт версий.** `versions-maven-plugin` + `<dependencyManagement>` + lock-файлы (Maven Dependency Lock).
- **Отсутствие reproducible builds.** Указать `maven.build.timestamp`, зафиксировать версии плагинов.

## Маршруты чтения

- **Первый проект (1 день):** `maven.md` собрать Spring Boot через `mvn spring-boot:run` `mvn package`.
- **Enterprise-монорепо:** `maven-advanced.md` — reactor + BOM + профили + enforcer.
- **Миграция на Gradle:** снять список плагинов Maven найти аналоги постепенный переход (сначала один модуль).

## Куда идти дальше

- Gradle как альтернатива и замена — [[README]]
- CI/CD и артефакт-репозитории (Nexus, Artifactory) — [[README]]
- Контейнеризация Java-приложений — [[README]]
- Тестирование — [[README]]
