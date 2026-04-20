---
title: "Gradle"
description: "Точка входа в раздел Gradle: базовые понятия и продвинутые паттерны для JVM-проектов."
tags:
  - meta
  - index
  - build-tools
  - gradle
type: "index"
updated: "2026-04-17"
---
# Gradle

**Gradle** — система автоматизации сборки, использующая Groovy или Kotlin DSL для описания сборочных скриптов. Сильные стороны: инкрементальная сборка, build cache, configuration cache, многомодульность, version catalogs, отличная поддержка Kotlin и Android. В JVM-мире Gradle постепенно вытесняет Maven как дефолт для новых проектов.

Для кого: инженеры, которые стартуют новый проект на Gradle или ведут корпоративный многомодульный build с требованиями к производительности и управляемости зависимостей. Раздел покрывает и вход в язык, и продвинутые темы (version catalogs, composite builds, convention plugins).

## Полезные ссылки

### Основные документы
- [[gradle]] — основы: структура проекта, команды, wrapper
- [[gradle-advanced]] — многомодульность, version catalogs, build cache, convention plugins, CI/CD

### Соседние разделы
- [[README|Родительский раздел: Build Tools]]
- [[README|Maven]] — основной конкурент
- [Spring Boot](../../../frameworks/java-frameworks/spring/) — `spring-boot-gradle-plugin`
- [[README|CI/CD]]
- [[README|Testing]]
- [[README|Docker]] — `jib`, `bootBuildImage`

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
| Установка, wrapper, структура | [[gradle#установка]] |
| build.gradle.kts / плагины / задачи | [[gradle]] |
| Version catalogs (`libs.versions.toml`) | [[gradle-advanced#version-catalogs]] |
| Многомодульные проекты и composite builds | [[gradle-advanced#много-модульные-проекты]] |
| Build cache и configuration cache | [[gradle-advanced#build-cache]] |
| Convention plugins, `buildSrc` | [[gradle-advanced]] |
| Spring Boot, Kotlin, Testing плагины | [[gradle-advanced#spring-boot]] |

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

- **Первый проект на Gradle (1 день):** `gradle.md` → собрать простой Spring Boot проект → понять wrapper и основные задачи.
- **Переход с Maven:** `gradle-advanced.md` секция version catalogs → дерево зависимостей → плагины замены `maven-*`.
- **Оптимизация большого монорепо:** `gradle-advanced.md` — build cache + composite builds + convention plugins.

## Куда идти дальше

- Maven как альтернатива — [[README]]
- Контейнеризация Java-приложений — [[README]]
- CI/CD пайплайны — [[README]]
- Тестирование — [[README]]
