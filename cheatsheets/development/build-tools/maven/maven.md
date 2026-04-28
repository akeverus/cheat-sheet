---
title: "Maven: основы"
description: "Структура pom.xml, координаты артефактов, scopes, lifecycle и фазы, плагины, профили, multi-module, troubleshooting."
tags:
  - development
  - build-tools
  - maven
type: "reference"
difficulty: "intermediate"
aliases:
  - "Maven"
  - "основы"
  - "Maven: основы"
prerequisites: []
related:
  - "[[gradle]]"
  - "[[java-basics]]"
next:
  - "[[maven-advanced]]"
updated: "2026-04-26"
---

# Maven: основы

Apache Maven — система управления проектами и сборки для JVM. Описание проекта
декларативное, в XML-файле `pom.xml` (Project Object Model). Maven сам определяет
порядок шагов сборки через стандартный жизненный цикл и берёт зависимости из
центрального репозитория Maven Central.

Документ покрывает базовое: координаты артефактов, scopes зависимостей, фазы
жизненного цикла, плагины, профили, многомодульная сборка, типовые проблемы.
Продвинутое — в [Maven Advanced](maven-advanced.md).

## Полезные ссылки

### Официальная документация

- [Maven Getting Started Guide](https://maven.apache.org/guides/getting-started/) — стартовый гайд
- [Introduction to the POM](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) — структура pom.xml
- [Build Lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html) — фазы и goals
- [Available Plugins](https://maven.apache.org/plugins/) — каталог плагинов

### Обучающие материалы

- [Maven Central Search](https://central.sonatype.com/) — поиск артефактов
- [Maven by Example](https://books.sonatype.com/mvnex-book/reference/index.html) — книга от Sonatype

### См. также

- [Maven Advanced](maven-advanced.md) — multi-module реактор, релизы, кастомные плагины
- [Gradle: основы](../gradle/gradle.md) — альтернативная система сборки
- [Java: основы](../../../languages/java/java-basics.md) — целевой язык
- [Spring Boot](../../../frameworks/java-frameworks/spring/spring-boot.md) — типовое использование Maven
- [GitHub Actions](../../../platform/ci-cd/github-actions.md) — Maven в CI

## Содержание

- [Maven vs Gradle: краткое сравнение](#maven-vs-gradle-краткое-сравнение)
- [Установка и Maven Wrapper](#установка-и-maven-wrapper)
- [Структура проекта](#структура-проекта)
- [POM: основные элементы](#pom-основные-элементы)
  - [Координаты артефакта](#координаты-артефакта)
  - [Свойства](#свойства)
  - [Зависимости](#зависимости)
  - [Плагины](#плагины)
- [Scopes зависимостей](#scopes-зависимостей)
- [Жизненный цикл и фазы](#жизненный-цикл-и-фазы)
- [Goals и плагины](#goals-и-плагины)
- [Тестирование](#тестирование)
- [Профили](#профили)
- [Multi-module через parent POM](#multi-module-через-parent-pom)
- [BOM и dependencyManagement](#bom-и-dependencymanagement)
- [Часто используемые команды](#часто-используемые-команды)
- [settings.xml](#settingsxml)
- [Решение проблем](#решение-проблем)
- [Лучшие практики](#лучшие-практики)

## Maven vs Gradle: краткое сравнение

| Критерий | Maven | Gradle |
|----------|-------|--------|
| DSL | XML | Kotlin/Groovy |
| Подход | Декларативный, convention over configuration | Программируемый |
| Скорость | Полная сборка каждый раз | Инкрементальная, build cache |
| Кривая обучения | Ниже | Выше |
| Зрелость экосистемы | Стабильная и предсказуемая | Активно развивается |
| Где применяется | Корпоративные кодовые базы, унаследованные проекты | Android, новые JVM-сервисы |

**Когда выбрать Maven:** существующая команда уже работает с XML-POM, нужна
максимальная предсказуемость, нет требований к скорости инкрементальной сборки.

Подробнее про различия — в [Gradle: основы](../gradle/gradle.md).

## Установка и Maven Wrapper

Maven Wrapper (`mvnw`) — аналог Gradle Wrapper: версия Maven фиксируется в проекте
и скачивается автоматически. Не требует глобально установленного Maven.

```bash
mvn wrapper:wrapper -Dmaven=3.9.9   # инициализация в существующем проекте
./mvnw clean install                # запуск через wrapper
```

В репозиторий коммитятся `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`.
Версия Maven указана в `.mvn/wrapper/maven-wrapper.properties`.

Глобальная установка нужна только для самого первого `wrapper:wrapper`
или если работаешь с проектами без wrapper.

## Структура проекта

Maven навязывает стандартную структуру каталогов (Standard Directory Layout):

```text
my-app/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/
│   ├── maven-wrapper.jar
│   └── maven-wrapper.properties
└── src/
    ├── main/
    │   ├── java/             # исходники
    │   ├── resources/        # ресурсы (application.yml и т. п.)
    │   └── webapp/           # для WAR (опционально)
    └── test/
        ├── java/             # тесты
        └── resources/
```

Каталог `target/` создаётся при сборке: компилированные классы, отчёты тестов,
финальный jar/war. Игнорируется в Git.

## POM: основные элементы

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.11.0</junit.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.14.0</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### Координаты артефакта

| Поле | Назначение | Пример |
|------|-----------|--------|
| `groupId` | Идентификатор организации (обратная нотация домена) | `com.example` |
| `artifactId` | Имя артефакта в группе | `my-app` |
| `version` | Версия | `1.0.0`, `1.0.0-SNAPSHOT` |
| `packaging` | Тип артефакта | `jar`, `war`, `pom`, `ear` |

`SNAPSHOT` означает версию в разработке: Maven каждый раз проверяет в репозитории,
не появилась ли новая. Релизные версии (`1.0.0`) считаются неизменяемыми.

### Свойства

```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
    <spring.version>6.1.14</spring.version>
</properties>
```

Использование: `${spring.version}`. Свойства централизуют версии и опции компилятора.

### Зависимости

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>6.1.14</version>
    </dependency>
</dependencies>
```

Транзитивные зависимости подтягиваются автоматически. Версии транзитивных
зависимостей разрешаются по правилу «ближайшая к корню в дереве выигрывает»
(nearest-wins).

### Плагины

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <release>21</release>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Scopes зависимостей

Scope определяет, в каких classpath видна зависимость и попадёт ли она в итоговый артефакт.

| Scope | Compile | Test | Runtime | В итоговом jar | Транзитивен | Когда использовать |
|-------|---------|------|---------|----------------|-------------|--------------------|
| `compile` (по умолчанию) | да | да | да | да | да | Обычная зависимость кода |
| `provided` | да | да | нет | нет | нет | Servlet API в WAR — даёт контейнер |
| `runtime` | нет | да | да | да | да | JDBC-драйверы, реализация SLF4J |
| `test` | нет | да | нет | нет | нет | JUnit, Mockito |
| `system` | да | да | да | нет | нет | Локальный jar — устаревший подход |
| `import` | — | — | — | — | — | Только в `<dependencyManagement>` для BOM |

**Когда использовать `provided`:** API, который предоставит среда выполнения —
servlet-контейнер, application server, runtime платформы.

**Когда использовать `runtime`:** реализации, нужные при запуске, но не при компиляции
(`logback-classic`, JDBC-драйверы).

## Жизненный цикл и фазы

В Maven три встроенных жизненных цикла: `default` (сборка), `clean`, `site`.
Каждый состоит из фаз. Фазы выполняются по порядку — вызов фазы запускает её
и все предыдущие в этом цикле.

Основные фазы цикла `default`:

| Фаза | Что делает |
|------|-----------|
| `validate` | Проверяет POM и конфигурацию |
| `compile` | Компилирует основной код |
| `test-compile` | Компилирует тесты |
| `test` | Запускает unit-тесты (Surefire) |
| `package` | Упаковывает в jar/war |
| `verify` | Запускает integration-тесты (Failsafe), проверки |
| `install` | Кладёт артефакт в локальный `~/.m2/repository` |
| `deploy` | Публикует в удалённый репозиторий (Nexus, Artifactory) |

```bash
mvn package    # validate -> compile -> test-compile -> test -> package
mvn install    # всё, что выше, плюс install
```

Цикл `clean`:

```bash
mvn clean             # удаляет target/
mvn clean install     # стандартная команда: чистая сборка с install
```

## Goals и плагины

Goal — конкретное действие плагина. Плагин привязан к фазе через POM, либо
вызывается напрямую: `mvn plugin:goal`.

```bash
mvn dependency:tree                 # дерево зависимостей
mvn help:effective-pom              # реальный POM с учётом наследования
mvn versions:display-dependency-updates    # доступные обновления
mvn spring-boot:run                 # запуск Spring Boot
```

Привязка goal к фазе:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>flatten-maven-plugin</artifactId>
    <version>1.6.0</version>
    <executions>
        <execution>
            <id>flatten</id>
            <phase>process-resources</phase>
            <goals>
                <goal>flatten</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Тестирование

Unit-тесты выполняет `maven-surefire-plugin` в фазе `test`. Integration-тесты —
`maven-failsafe-plugin` в фазе `verify`. Surefire по умолчанию ищет классы
`*Test.java`, `Test*.java`, `*Tests.java`. Failsafe — `*IT.java`, `IT*.java`.

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.11.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.5.0</version>
        </plugin>
    </plugins>
</build>
```

Запуск:

```bash
mvn test                                          # все unit-тесты
mvn test -Dtest=UserServiceTest                   # один класс
mvn test -Dtest=UserServiceTest#shouldCreateUser  # один метод
mvn -pl module-name test                          # тесты в одном модуле
mvn verify                                        # включая integration-тесты
```

Отчёты: `target/surefire-reports/`, `target/failsafe-reports/`.

## Профили

Профили — это переопределение конфигурации для среды или условий.

```xml
<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <db.url>jdbc:postgresql://localhost:5432/dev</db.url>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <db.url>jdbc:postgresql://prod-host:5432/app</db.url>
        </properties>
    </profile>
</profiles>
```

Активация:

```bash
mvn package -Pprod                  # явно
mvn package -P!dev                  # выключить dev
```

Активация по условию: `<activation>` поддерживает `os`, `jdk`, `property`, `file`.

> Не злоупотребляй профилями для логики сборки. Если профилей больше 3–4 —
> структура POM выходит из-под контроля. Часто лучше — переменные окружения
> или Spring Boot профили.

## Multi-module через parent POM

Корневой `pom.xml` (parent):

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>my-service-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>domain</module>
        <module>persistence</module>
        <module>app</module>
    </modules>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-lang3</artifactId>
                <version>3.14.0</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

POM подмодуля:

```xml
<project>
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example</groupId>
        <artifactId>my-service-parent</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>persistence</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <!-- версия из dependencyManagement родителя -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
    </dependencies>
</project>
```

Команды для multi-module:

```bash
mvn -pl app -am package    # собрать app и зависимые модули
mvn -pl app install        # только app
mvn -T 1C package          # параллельная сборка, 1 поток на CPU
```

## BOM и dependencyManagement

`<dependencyManagement>` фиксирует версии без подключения зависимостей.
Дочерние POM подключают артефакт без `<version>`.

BOM (Bill of Materials) — это POM с `packaging=pom`, импортируемый через
`scope=import`. Так Spring Boot, Quarkus, Jackson дают согласованные версии
всех своих модулей одной строкой.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.4.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- версия берётся из BOM -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

## Часто используемые команды

| Команда | Что делает |
|---------|-----------|
| `mvn clean install` | Чистая сборка с публикацией в локальный репозиторий |
| `mvn package` | Сборка артефакта без install |
| `mvn verify` | Включая integration-тесты |
| `mvn test` | Только unit-тесты |
| `mvn -DskipTests package` | Сборка без выполнения тестов (классы тестов компилируются) |
| `mvn -Dmaven.test.skip=true package` | Полный пропуск тестов (даже компиляции) |
| `mvn dependency:tree` | Дерево зависимостей |
| `mvn dependency:tree -Dincludes=org.slf4j` | Только подграф нужной библиотеки |
| `mvn help:effective-pom` | Эффективный POM с учётом наследования |
| `mvn help:effective-settings` | Эффективные `settings.xml` |
| `mvn versions:display-dependency-updates` | Какие зависимости можно обновить |
| `mvn -o package` | Offline-режим |
| `mvn -U package` | Принудительно проверить SNAPSHOT-обновления |
| `mvn -X package` | Debug-вывод (полный traceback) |
| `mvn -pl module -am install` | Собрать модуль и его зависимости |

## settings.xml

Глобальная конфигурация Maven для пользователя — `~/.m2/settings.xml`. Там хранятся:

- Корпоративные репозитории (зеркала Maven Central через Nexus/Artifactory).
- Учётные данные для деплоя.
- Активные профили и proxy.

```xml
<settings>
    <mirrors>
        <mirror>
            <id>company-nexus</id>
            <url>https://nexus.example.com/repository/maven-public/</url>
            <mirrorOf>*</mirrorOf>
        </mirror>
    </mirrors>
    <servers>
        <server>
            <id>company-nexus</id>
            <username>${env.NEXUS_USER}</username>
            <password>${env.NEXUS_PASSWORD}</password>
        </server>
    </servers>
</settings>
```

## Решение проблем

| Симптом | Причина | Решение |
|---------|---------|---------|
| `Could not find artifact ...` | Артефакт не в репозитории / нет доступа | Проверь `mirrors` в `settings.xml`, доступ к Nexus, опечатки в координатах |
| Старая версия зависимости тянется | Конфликт транзитивных зависимостей (nearest-wins) | `mvn dependency:tree -Dincludes=...`, добавь явную зависимость в `dependencies` |
| Тест проходит локально, валится в CI | Различие в JDK, локали, очерёдности тестов | `<release>` в compiler plugin, `-Duser.timezone=UTC`, `-Dtest=` для воспроизведения |
| `OutOfMemoryError` при сборке | Маленький heap | `MAVEN_OPTS=-Xmx2g` или `.mvn/jvm.config` |
| `Plugin not found` | Не подключён нужный репозиторий плагинов | Добавь `<pluginRepositories>` или mirror в `settings.xml` |
| Maven игнорирует изменения в SNAPSHOT | Закеширована метадата | `mvn -U install` для принудительного обновления |
| Изменения в parent POM не применяются | Старая версия parent в локальном кеше | `mvn install` parent перед сборкой children |
| Surefire не находит тесты | Имена не подходят под паттерн | Назови класс `*Test` или явно укажи `<includes>` в Surefire |

Откуда тянется зависимость:

```bash
mvn dependency:tree -Dincludes=com.fasterxml.jackson.core:jackson-databind
```

Полное дерево с дублирующими/опущенными версиями:

```bash
mvn dependency:tree -Dverbose=true
```

## Лучшие практики

- Используй Maven Wrapper (`mvnw`). Локальный Maven нужен только для `wrapper:wrapper`.
- Версии библиотек выноси в `<properties>` или BOM, не дублируй в каждом POM.
- Не злоупотребляй профилями для логики — лучше переменные окружения и runtime-профили.
- В parent POM держи общий `<dependencyManagement>` и `<pluginManagement>`,
  а в children — только `<dependencies>` без версий.
- Тесты: unit — Surefire (`*Test`), integration — Failsafe (`*IT`).
  Не смешивай в одном плагине.
- В CI — параллельная сборка `-T 1C`, отключение интерактива `-B`,
  `--fail-fast` или `--fail-at-end` по политике команды.
- Закрывай `SNAPSHOT` в релизах: финальная версия должна тянуть только релизы,
  иначе сборка невоспроизводима.
- Для аудита уязвимостей — `org.owasp:dependency-check-maven`.

**Итог:** Maven предсказуем и многословен. Большая часть работы — `pom.xml`,
команды `clean install`/`package`/`test`, `dependency:tree` для разбора конфликтов.
Сложные кейсы (релизы, multi-module реактор, кастомные плагины) — в
[Maven Advanced](maven-advanced.md).
