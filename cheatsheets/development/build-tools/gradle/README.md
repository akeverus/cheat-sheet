---
title: "Gradle"
description: "Точка входа в раздел Gradle: базовые понятия и продвинутые паттерны для JVM-проектов."
tags:
  - meta
  - index
  - build-tools
  - gradle
type: "index"
aliases:
  - "Gradle"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Gradle

**Gradle** — система автоматизации сборки, использующая Groovy или Kotlin DSL для описания сборочных скриптов. Сильные стороны: инкрементальная сборка, build cache, configuration cache, многомодульность, version catalogs, отличная поддержка Kotlin и Android. В JVM-мире Gradle постепенно вытесняет Maven как дефолт для новых проектов.

Для кого: инженеры, которые стартуют новый проект на Gradle или ведут корпоративный многомодульный build с требованиями к производительности и управляемости зависимостей. Раздел покрывает и вход в язык, и продвинутые темы (version catalogs, composite builds, convention plugins).

## Полезные ссылки

### Основные документы
- [gradle](gradle.md) — основы: структура проекта, команды, wrapper
- [gradle-advanced](gradle-advanced.md) — многомодульность, version catalogs, build cache, convention plugins, CI/CD

### Соседние разделы
- [Родительский раздел: Build Tools](../../../basics/README.md)
- [Maven](../../../basics/README.md) — основной конкурент
- [Spring Boot](../../../frameworks/java-frameworks/spring/) — `spring-boot-gradle-plugin`
- [CI/CD](../../../basics/README.md)
- [Testing](../../../basics/README.md)
- [Docker](../../../basics/README.md) — `jib`, `bootBuildImage`

### Внешние ресурсы
- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html)
- [Gradle Plugin Portal](https://plugins.gradle.org/)
- [Gradle Build Cache Guide](https://docs.gradle.org/current/userguide/build_cache.html)
- [Version Catalogs](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog)
- [Gradle Enterprise](https://gradle.com/enterprise/)

## Содержание

- [Gradle vs Maven: краткое сравнение](#gradle-vs-maven-краткое-сравнение)
- [Карта тем](#карта-тем)
- [Чек-лист production-grade Gradle-проекта](#чек-лист-production-grade-gradle-проекта)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Gradle vs Maven: краткое сравнение

| Критерий | Gradle | Maven |
|----------|--------|-------|
| Язык | Groovy / Kotlin DSL (императив + декларатив) | XML (декларативно) |
| Производительность | Инкрементальная, build cache, config cache | Последовательная, без нативного кэша тасков |
| Читаемость | Ниже (DSL даёт гибкость) | Выше (POM строг и предсказуем) |
| Версии зависимостей | `libs.versions.toml` (version catalogs) | `<dependencyManagement>`, BOM |
| Многомодульность | `settings.gradle(.kts)`, composite builds | `<modules>` reactor |
| Android | Нативная поддержка | Ограниченная |
| Кривая обучения | Круче | Более пологая |
| Когда выбрать | Новый проект, Android, многомодульный монорепо, требуется скорость | Legacy, простой pipeline, CI-скрипты завязаны на mvn |

Правило большого пальца: новый JVM-проект в 2026 — **Gradle**. Если команда уже живёт на Maven, миграция ради миграции не нужна — только при конкретной боли (медленный build, слабое управление версиями, сложные customizations).

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Установка, wrapper, структура | [gradle](gradle.md#установка) |
| build.gradle.kts / плагины / задачи | [gradle](gradle.md) |
| Version catalogs (`libs.versions.toml`) | [gradle-advanced](gradle-advanced.md#version-catalogs) |
| Многомодульные проекты и composite builds | [gradle-advanced](gradle-advanced.md#много-модульные-проекты) |
| Build cache и configuration cache | [gradle-advanced](gradle-advanced.md#build-cache) |
| Convention plugins, `buildSrc` | [gradle-advanced](gradle-advanced.md) |
| Spring Boot, Kotlin, Testing плагины | [gradle-advanced](gradle-advanced.md#spring-boot) |

## Чек-лист production-grade Gradle-проекта

- [ ] Gradle Wrapper зафиксирован (`./gradlew --version`)
- [ ] Kotlin DSL (`.kts`) если нет legacy-причин
- [ ] Version catalog в `gradle/libs.versions.toml`
- [ ] Build cache включён (`org.gradle.caching=true`)
- [ ] Configuration cache включён (`org.gradle.configuration-cache=true`)
- [ ] Parallel execution (`org.gradle.parallel=true`)
- [ ] Convention plugins в `buildSrc` для общих настроек
- [ ] Dependency updates и security scan в CI (Dependabot, OWASP)
- [ ] Тесты разделены на unit/integration (`test`, `integrationTest`)

## Маршруты чтения

- **Первый проект на Gradle (1 день):** `gradle.md` собрать простой Spring Boot проект понять wrapper и основные задачи.
- **Переход с Maven:** `gradle-advanced.md` секция version catalogs дерево зависимостей плагины замены `maven-*`.
- **Оптимизация большого монорепо:** `gradle-advanced.md` — build cache + composite builds + convention plugins.

## Куда идти дальше

- Maven как альтернатива — [README](../../../basics/README.md)
- Контейнеризация Java-приложений — [README](../../../basics/README.md)
- CI/CD пайплайны — [README](../../../basics/README.md)
- Тестирование — [README](../../../basics/README.md)
