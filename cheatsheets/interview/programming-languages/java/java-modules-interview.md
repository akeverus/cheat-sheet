---
title: "Вопросы на собеседовании: Java Modules (JPMS)"
description: "Полное руководство по вопросам собеседования на тему модульной системы Java (JPMS, Java 9+): module-info.java, exports/requires/opens/uses/provides, automatic и unnamed модули, split packages, multi-release JAR, jlink, ServiceLoader, рефлексия, module layers, стратегии миграции."
tags:
  - interview
  - programming-languages
  - java-modules-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Modules"
  - "JPMS"
  - "Java Modules interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Java Modules` (JPMS)

Полное руководство по вопросам собеседования на тему модульной системы `Java` (`JPMS`, `Java 9+`): `module-info.java`, директивы `exports`/`requires`/`opens`/`uses`/`provides`, `automatic` и `unnamed` модули, `split packages`, `multi-release JAR`, `jlink`, `ServiceLoader`, рефлексия в модульном мире, `module layers`, стратегии миграции.

## Полезные ссылки

### Официальная документация

- [The State of the Module System (JSR 376)](https://openjdk.org/jeps/261) — JEP 261, описание модульной системы
- [Java Module System (Oracle JLS)](https://docs.oracle.com/javase/specs/jls/se17/html/jls-7.html#jls-7.7) — спецификация языка
- [JEP 238: Multi-Release JAR Files](https://openjdk.org/jeps/238) — спецификация multi-release JAR
- [JEP 282: jlink](https://openjdk.org/jeps/282) — создание custom runtime images
- [Java Platform Module System (Baeldung)](https://www.baeldung.com/java-9-modularity) — обзор JPMS на Baeldung
- [A Guide to Java 9 Modularity (Baeldung)](https://www.baeldung.com/java-modularity) — практическое руководство
- [Multi-Module Maven Application with Java Modules — Baeldung](https://www.baeldung.com/maven-multi-module-project-java-jpms) — Maven + JPMS на практике
- [Design Strategies for Decoupling Java Modules — Baeldung](https://www.baeldung.com/java-modules-decoupling-design-strategies) — паттерны декаплинга модулей

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы JPMS**
- [Q1. (!) Что такое `JPMS` и какие проблемы он решает?](#q1--что-такое-jpms-и-какие-проблемы-он-решает)
- [Q2. (!) Что такое `module-info.java` и какие директивы в нём доступны?](#q2--что-такое-module-infojava-и-какие-директивы-в-нём-доступны)
- [Q3. Как устроена модульная структура самого `JDK`?](#q3-как-устроена-модульная-структура-самого-jdk)
- [Q4. Чем отличается `module path` от `classpath`?](#q4-чем-отличается-module-path-от-classpath)

**Директивы: `exports`, `requires`, `opens`**
- [Q5. (!) В чём разница между `exports` и `opens`?](#q5--в-чём-разница-между-exports-и-opens)
- [Q6. Что такое `qualified exports` и `qualified opens`?](#q6-что-такое-qualified-exports-и-qualified-opens)
- [Q7. (!) Что такое `requires` и `requires transitive`?](#q7--что-такое-requires-и-requires-transitive)
- [Q8. Что такое `requires static` и когда это используется?](#q8-что-такое-requires-static-и-когда-это-используется)
- [Q9. Что такое `open module`?](#q9-что-такое-open-module)

**Сервисы и `ServiceLoader`**
- [Q10. (!) Что такое `provides` и `uses` в контексте `ServiceLoader`?](#q10--что-такое-provides-и-uses-в-контексте-serviceloader)
- [Q11. Как `ServiceLoader` работает в модульном мире по сравнению с `classpath`?](#q11-как-serviceloader-работает-в-модульном-мире-по-сравнению-с-classpath)

**Рефлексия и модули**
- [Q12. (!) Как модули влияют на рефлексию и доступ к внутренним `API`?](#q12--как-модули-влияют-на-рефлексию-и-доступ-к-внутренним-api)
- [Q13. Какие флаги JVM используются для обхода модульных ограничений?](#q13-какие-флаги-jvm-используются-для-обхода-модульных-ограничений)

**`Unnamed module` и `automatic module`**
- [Q14. (!) Что такое `unnamed module`?](#q14--что-такое-unnamed-module)
- [Q15. (!) Что такое `automatic module` и как определяется его имя?](#q15--что-такое-automatic-module-и-как-определяется-его-имя)
- [Q16. В чём ключевые отличия между `unnamed module`, `automatic module` и именованным модулем?](#q16-в-чём-ключевые-отличия-между-unnamed-module-automatic-module-и-именованным-модулем)

**`Split packages`**
- [Q17. (!) Что такое `split package` и почему это проблема в `JPMS`?](#q17--что-такое-split-package-и-почему-это-проблема-в-jpms)

**`Multi-release JAR`**
- [Q18. Что такое `multi-release JAR` и как он связан с модулями?](#q18-что-такое-multi-release-jar-и-как-он-связан-с-модулями)

**`jlink` и custom runtime**
- [Q19. (!) Что такое `jlink` и зачем собирать custom runtime image?](#q19--что-такое-jlink-и-зачем-собирать-custom-runtime-image)
- [Q20. Какие плагины и опции поддерживает `jlink`?](#q20-какие-плагины-и-опции-поддерживает-jlink)

**Стратегии миграции**
- [Q21. (!) Какие существуют стратегии миграции на модули?](#q21--какие-существуют-стратегии-миграции-на-модули)
- [Q22. Как мигрировать проект с `Maven`/`Gradle` на модули?](#q22-как-мигрировать-проект-с-mavengradle-на-модули)

**`Module layers`**
- [Q23. Что такое `ModuleLayer` и зачем он нужен?](#q23-что-такое-modulelayer-и-зачем-он-нужен)

**Циклические зависимости**
- [Q24. Как в `JPMS` обрабатываются циклические зависимости между модулями?](#q24-как-в-jpms-обрабатываются-циклические-зависимости-между-модулями)

**Тестирование**
- [Q25. Как тестировать модульное приложение?](#q25-как-тестировать-модульное-приложение)

**`JPMS` и фреймворки**
- [Q26. (!) Как `Spring`/`Spring Boot` работает с модульной системой?](#q26--как-springspring-boot-работает-с-модульной-системой)
- [Q27. Как `Hibernate`/`JPA` взаимодействует с модулями?](#q27-как-hibernatejpa-взаимодействует-с-модулями)

**Лучшие практики**
- [Q28. Какие практики проектирования модулей считаются хорошими?](#q28-какие-практики-проектирования-модулей-считаются-хорошими)
- [Q29. Какие типичные ошибки допускают при работе с модулями?](#q29-какие-типичные-ошибки-допускают-при-работе-с-модулями)
- [Q30. Каковы перспективы развития модульной системы `Java`?](#q30-каковы-перспективы-развития-модульной-системы-java)

**Типы модулей и совместимость**
- [Q31. Чем отличаются `unnamed module`, `automatic module` и `named module` на практике?](#q31-чем-отличаются-unnamed-module-automatic-module-и-named-module-на-практике)
- [Q32. `Split packages`: почему запрещены в `JPMS` и как их устранить?](#q32-split-packages-почему-запрещены-в-jpms-и-как-их-устранить)
- [Q33. `--add-opens` и `--add-exports`: когда и как использовать для рефлексии с `JPMS`?](#q33---add-opens-и---add-exports-когда-и-как-использовать-для-рефлексии-с-jpms)
- [Q34. `jlink`: создание custom minimal JRE — практическое руководство](#q34-jlink-создание-custom-minimal-jre--практическое-руководство)
- [Q35. `jdeps`: анализ зависимостей модулей перед миграцией](#q35-jdeps-анализ-зависимостей-модулей-перед-миграцией)
- [Q36. `ServiceLoader` с `JPMS`: директивы `uses` и `provides...with` в деталях](#q36-serviceloader-с-jpms-директивы-uses-и-provideswith-в-деталях)
- [Q37. Совместимость `Spring`, `Hibernate` и `Jackson` с `JPMS`: типичные проблемы](#q37-совместимость-spring-hibernate-и-jackson-с-jpms-типичные-проблемы)
- [Q38. Стратегии миграции legacy-кода: `Bottom-Up` vs `Top-Down` в деталях](#q38-стратегии-миграции-legacy-кода-bottom-up-vs-top-down-в-деталях)

---

## Q1. (!) Что такое `JPMS` и какие проблемы он решает?

**`JPMS`** (`Java Platform Module System`), введённый в `Java 9` (проект `Jigsaw`), — это модульная система платформы. Модуль — именованная совокупность пакетов и ресурсов с явно объявленными зависимостями и экспортом.

**Проблемы, которые решает `JPMS`:**

| Проблема | Как решает `JPMS` |
|----------|-------------------|
| **JAR Hell** — конфликты версий, дублирование классов на `classpath` | Каждый пакет принадлежит ровно одному модулю, `split packages` запрещены |
| **Отсутствие инкапсуляции** — все `public` классы видны всем | `exports` контролирует, какие пакеты доступны снаружи |
| **Неявные зависимости** — класс может случайно использовать чужой JAR | `requires` делает зависимости явными и проверяемыми |
| **Монолитный JDK** — полный runtime даже для микросервиса | `jlink` создаёт компактный runtime только с нужными модулями |

```mermaid
graph TB
    subgraph "До JPMS (classpath)"
        J1[JAR A] --- J2[JAR B]
        J2 --- J3[JAR C]
        J1 --- J3
        style J1 fill:#ffcccc
        style J2 fill:#ffcccc
        style J3 fill:#ffcccc
    end

    subgraph "После JPMS (module path)"
        M1[Module A<br/>exports api] -->|requires| M2[Module B<br/>exports spi]
        M1 -->|requires| M3[Module C<br/>exports util]
        style M1 fill:#ccffcc
        style M2 fill:#ccffcc
        style M3 fill:#ccffcc
    end
```

Сама платформа `JDK` разбита на модули: `java.base` (неявно подключается всегда), `java.sql`, `java.xml`, `java.logging` и др. Приложение может зависеть только от того, что реально использует.


> [!mcq]
> - [ ] JPMS запрещает использование classpath — все JAR должны быть переведены в модули | ❌ ПОСЛЕДСТВИЕ: unnamed module и automatic module позволяют legacy JAR работать на classpath/module path без module-info.java
> - [x] JPMS решает JAR Hell (split packages запрещены), добавляет инкапсуляцию через exports, явные зависимости через requires, компактный runtime через jlink | ✓ ПРИМЕНЯТЬ: микросервисы на Java 9+ с jlink; библиотеки с чёткими API границами 📋 ПРАВИЛО: JPMS = явные границы модулей → нет JAR Hell 🔗 См. Q4
> - [ ] JPMS заменяет Maven/Gradle — больше не нужны системы сборки | ❌ ПОСЛЕДСТВИЕ: JPMS и системы сборки решают разные задачи; Maven/Gradle управляют зависимостями при сборке, JPMS — модульностью в runtime
> - [ ] JPMS добавляет вертикальное масштабирование JVM — больше heap на один модуль | ❌ ПОСЛЕДСТВИЕ: JPMS про модульность кода и runtime image, не про распределение памяти JVM

## Q2. (!) Что такое `module-info.java` и какие директивы в нём доступны?

`module-info.java` — специальный файл-описание модуля, расположенный в корне исходного кода модуля (рядом с корневым пакетом). Компилируется в `module-info.class`. Содержит имя модуля и набор директив.

**Полный набор директив:**

| Директива | Назначение |
|-----------|------------|
| `requires` | Объявляет зависимость от другого модуля |
| `requires transitive` | Транзитивная зависимость — видна клиентам |
| `requires static` | Зависимость только на этапе компиляции (optional) |
| `exports` | Делает пакет доступным для компиляции и вызова |
| `exports ... to` | Квалифицированный экспорт — только указанным модулям |
| `opens` | Открывает пакет для рефлексии |
| `opens ... to` | Квалифицированное открытие — только указанным модулям |
| `uses` | Объявляет потребление сервиса через `ServiceLoader` |
| `provides ... with` | Объявляет реализацию сервиса |

```java
module com.example.app {
    // Зависимости
    requires java.sql;
    requires transitive com.example.api;
    requires static lombok;                    // compile-only

    // Экспорт пакетов
    exports com.example.app.api;
    exports com.example.app.spi to com.example.plugin;  // qualified

    // Открытие для рефлексии (Spring, Hibernate)
    opens com.example.app.entities to org.hibernate.orm;
    opens com.example.app.config to spring.core;

    // Сервисы
    uses com.example.spi.PaymentProvider;
    provides com.example.spi.PaymentProvider
        with com.example.app.StripeProvider,
             com.example.app.PayPalProvider;
}
```

Имя модуля должно быть уникальным; по соглашению используют обратное доменное имя (как для пакетов). На один модуль — ровно один `module-info.java`.


> [!mcq]
> - [ ] module-info.java должен находиться в папке src/main/resources | ❌ ПОСЛЕДСТВИЕ: module-info.java обязан быть в корне source root (src/main/java); в resources компилятор его не обнаружит
> - [ ] Достаточно написать только `requires` — `exports` добавляется автоматически для public классов | ❌ ПОСЛЕДСТВИЕ: без явного `exports` даже public API недоступен другим модулям; отсутствие `exports` = closed module by default
> - [ ] `opens` и `exports` — синонимы; оба делают пакет доступным | ❌ ПОСЛЕДСТВИЕ: `exports` — доступ для компиляции/runtime вызовов; `opens` — только рефлексия (setAccessible); Spring требует `opens`, не `exports`
> - [x] module-info.java в корне source root содержит: `requires` (зависимости), `exports` (публичный API), `opens` (рефлексия), `uses`/`provides` (ServiceLoader) | ✓ ПРИМЕНЯТЬ: любой именованный модуль Java 9+ 📋 ПРАВИЛО: module-info.java = контракт модуля: что берёт и что отдаёт 🔗 См. Q5

## Q3. Как устроена модульная структура самого `JDK`?

Начиная с `Java 9`, `JDK` разбит на ~70 модулей (в зависимости от дистрибутива). Ключевые модули:

| Модуль | Содержимое |
|--------|------------|
| `java.base` | Фундамент: `java.lang`, `java.util`, `java.io`, `java.nio`, `java.net`, `java.time` и др. Подключается неявно — писать `requires java.base` не нужно |
| `java.sql` | JDBC API: `java.sql`, `javax.sql` |
| `java.xml` | XML-парсинг: DOM, SAX, StAX, XSLT |
| `java.logging` | `java.util.logging` |
| `java.desktop` | AWT, Swing, JavaFX-мост |
| `java.net.http` | HTTP Client API (с Java 11) |
| `jdk.httpserver` | Встроенный HTTP-сервер |
| `jdk.jlink` | Утилита `jlink` |

```mermaid
graph BT
    A[java.base] --> B[java.sql]
    A --> C[java.xml]
    A --> D[java.logging]
    A --> E[java.net.http]
    B --> F[java.naming]
    A --> G[java.desktop]
```

Модули с префиксом `java.*` — стандартная спецификация (SE), `jdk.*` — специфичны для конкретной реализации JDK. Это важно при миграции между дистрибутивами.


> [!mcq]
> - [ ] java.base нужно явно указывать в `requires java.base` | ❌ ПОСЛЕДСТВИЕ: java.base подключается неявно ко всем модулям; явный `requires java.base` — лишний код, но не ошибка
> - [x] java.base содержит java.lang/util/io/nio и подключается неявно; jdk.* модули специфичны для JDK (не SE-стандарт) | ✓ ПРИМЕНЯТЬ: при создании custom jlink image — только нужные java.* и jdk.* модули 📋 ПРАВИЛО: java.* = SE-стандарт; jdk.* = JDK-specific; java.base = неявный фундамент 🔗 См. Q19
> - [ ] Все JDK модули начинаются с jdk.* | ❌ ПОСЛЕДСТВИЕ: стандартные модули начинаются с java.* (java.sql, java.xml, java.net.http); jdk.* — внутренние инструменты (jdk.jlink, jdk.jdeps)
> - [ ] JDK разбит на 10 модулей | ❌ ПОСЛЕДСТВИЕ: JDK содержит ~70 модулей в зависимости от дистрибутива; это обеспечивает jlink гранулярность при создании custom runtime

## Q4. Чем отличается `module path` от `classpath`?

| Характеристика | `classpath` | `module path` |
|----------------|-------------|---------------|
| **Инкапсуляция** | Нет — все `public` классы видны всем | Есть — видны только `exports`-пакеты |
| **Зависимости** | Неявные, проверяются в runtime | Явные, проверяются при запуске |
| **Split packages** | Допускаются (первый найденный побеждает) | Запрещены — ошибка при запуске |
| **Рефлексия** | `setAccessible(true)` работает всегда | Требует `opens` или `--add-opens` |
| **Ошибки** | `ClassNotFoundException` в runtime | `ResolutionException` при старте приложения |

При миграции часто используют гибрид: часть артефактов на `module path`, часть — на `classpath` (как `unnamed module`). Для предсказуемого поведения рекомендуется поэтапно переводить проект на чистый `module path`.

```bash
# Classpath (старый стиль)
java -cp lib/a.jar:lib/b.jar com.example.Main

# Module path (модульный стиль)
java --module-path lib -m com.example.app/com.example.Main

# Гибрид
java --module-path mods -cp lib/legacy.jar -m com.example.app
```


> [!mcq]
> - [ ] На classpath split packages приводят к ошибке компиляции | ❌ ПОСЛЕДСТВИЕ: на classpath split packages допускаются (первый найденный JAR побеждает); только JPMS запрещает их с ResolutionException
> - [ ] На module path рефлексия (setAccessible) работает без ограничений | ❌ ПОСЛЕДСТВИЕ: на module path нужен `opens` или `--add-opens`; без него InaccessibleObjectException при setAccessible
> - [x] classpath: нет инкапсуляции, split packages допускаются, ошибки в runtime; module path: exports-инкапсуляция, split packages запрещены, ошибки при старте | ✓ ПРИМЕНЯТЬ: переход на module path даёт fail-fast диагностику зависимостей 📋 ПРАВИЛО: classpath = cowboy mode; module path = explicit contracts 🔗 См. Q1
> - [ ] module path несовместим с Maven — нужен специальный build tool | ❌ ПОСЛЕДСТВИЕ: Maven поддерживает module path через maven-compiler-plugin 3.6+ и maven-jar-plugin; стандартная конфигурация

## Q5. (!) В чём разница между `exports` и `opens`?

| Аспект | `exports` | `opens` |
|--------|-----------|---------|
| **Доступ** | Компиляция + runtime вызовы | Рефлексия (`setAccessible`) |
| **Видимость** | Только `public` типы и члены | Все типы и члены (включая `private`) |
| **Проверка** | Compile-time + runtime | Только runtime |
| **Типичное использование** | Публичный API модуля | Сущности для Hibernate, бины для Spring |

```java
module com.example.service {
    // Публичный API — можно вызывать из других модулей
    exports com.example.service.api;

    // Рефлексия для Spring DI и Hibernate маппинга
    opens com.example.service.model to org.hibernate.orm, spring.core;
}
```

Без `opens` вызов `field.setAccessible(true)` из другого модуля приводит к `InaccessibleObjectException`. Для фреймворков, активно использующих рефлексию (`Spring`, `Hibernate`, `Jackson`), `opens` — обязательное условие работы.

**Ключевое отличие**: `exports` — «видно снаружи для вызова», `opens` — «разрешена глубокая рефлексия».


> [!mcq]
> - [ ] `exports` даёт доступ только для вызовов; рефлексия всегда запрещена даже для экспортированных пакетов | ❌ ПОСЛЕДСТВИЕ: экспортированные пакеты доступны для вызовов, но рефлексия (setAccessible) всё равно требует `opens`; exports и opens ортогональны
> - [ ] Spring DI работает без `opens` — он использует только `exports` | ❌ ПОСЛЕДСТВИЕ: Spring использует рефлексию для инъекции зависимостей; без `opens com.example.service to spring.core` — InaccessibleObjectException
> - [x] `exports` = публичный API для компиляции/runtime; `opens` = рефлексия (setAccessible) для Spring/Hibernate | ✓ ПРИМЕНЯТЬ: `exports` для API; `opens ... to spring.core, org.hibernate.orm` для DI-фреймворков 📋 ПРАВИЛО: exports = вызывай меня; opens = смотри внутрь меня 🔗 См. Q12
> - [ ] `open module` = то же что `exports *`; всё доступно для компиляции | ❌ ПОСЛЕДСТВИЕ: `open module` = opens всех пакетов для рефлексии; это НЕ exports; компиляция пакетов всё равно требует явного `exports`

## Q6. Что такое `qualified exports` и `qualified opens`?

`Qualified` (квалифицированные) директивы ограничивают доступ не для всех модулей, а только для перечисленных:

```java
module com.example.core {
    // Доступ к internal API только для модуля тестирования
    exports com.example.core.internal to com.example.tests;

    // Рефлексия только для Hibernate
    opens com.example.core.entity to org.hibernate.orm;
}
```

Это позволяет реализовать паттерн «друзья» (friend access): модуль предоставляет расширенный доступ к внутренним пакетам только доверенным модулям, не раскрывая их всему миру. Полезно для:
- Тестовых модулей, которым нужен доступ к internal API
- Модулей одного проекта, которые тесно связаны
- Фреймворков, которым нужна рефлексия к конкретным пакетам


> [!mcq]
> - [ ] qualified exports открывает пакет для рефлексии только из указанных модулей | ❌ ПОСЛЕДСТВИЕ: `exports X to M` — это доступ для компиляции/вызовов; для рефлексии нужен `opens X to M`
> - [x] `exports pkg to M1, M2` ограничивает доступ к internal API только доверенным модулям; `opens pkg to hibernate` — рефлексия только для hibernate | ✓ ПРИМЕНЯТЬ: test-модули нуждаются в internal API; фреймворки нуждаются в рефлексии 📋 ПРАВИЛО: qualified = friend-access pattern; меньше is more 🔗 См. Q5
> - [ ] qualified exports нельзя использовать с Maven — только с jlink | ❌ ПОСЛЕДСТВИЕ: qualified exports — стандартная директива module-info.java; работает с любым build tool
> - [ ] `exports X to M` транзитивен — клиенты M тоже видят X | ❌ ПОСЛЕДСТВИЕ: qualified exports не транзитивен; только модуль M может использовать X, его клиенты — нет

## Q7. (!) Что такое `requires` и `requires transitive`?

**`requires`** объявляет прямую зависимость модуля. Без неё нельзя использовать типы из целевого модуля — компилятор и runtime это проверяют. Зависимость **не транзитивна**.

**`requires transitive`** делает зависимость транзитивной: модули-клиенты автоматически получают доступ к транзитивному модулю.

```mermaid
graph LR
    A[Module App] -->|requires| B[Module API]
    B -->|requires transitive| C[Module Model]
    A -.->|автоматически читает| C
    
    A -->|requires| D[Module Impl]
    D -->|requires| E[Module Utils]
    A -.-x|НЕ видит| E
```

```java
// module-info.java модуля API
module com.example.api {
    requires transitive com.example.model; // клиенты API видят model
    exports com.example.api;
}

// module-info.java модуля приложения
module com.example.app {
    requires com.example.api;   // автоматически видит com.example.model
}
```

**Правило**: если тип из зависимого модуля появляется в публичном API (параметры методов, возвращаемые типы, наследование), нужен `requires transitive`. Иначе клиенты не смогут скомпилировать код.


> [!mcq]
> - [ ] `requires transitive` автоматически делает все пакеты видимыми — `exports` не нужен | ❌ ПОСЛЕДСТВИЕ: `requires transitive` управляет читаемостью, но не видимостью; пакеты модуля C всё равно нужно явно `exports` в module-info.java модуля C
> - [x] `requires M` — прямая зависимость (не транзитивная); `requires transitive M` — клиенты тоже видят M; нужен когда типы из M в публичном API | ✓ ПРИМЕНЯТЬ: `requires transitive model` в api-модуле когда DTO из model в сигнатурах методов API 📋 ПРАВИЛО: тип в публичном API = requires transitive 🔗 См. Q8
> - [ ] `requires transitive` нужен всегда — без него ничего не скомпилируется | ❌ ПОСЛЕДСТВИЕ: `requires transitive` только когда типы зависимости в публичном API; избыточный transitive = нежелательная связность между модулями
> - [ ] `requires` достаточно для транзитивного доступа клиентов | ❌ ПОСЛЕДСТВИЕ: без `transitive` клиент получит compile error при использовании типов из транзитивной зависимости; нужно явно указывать `requires transitive`

## Q8. Что такое `requires static` и когда это используется?

`requires static` объявляет **optional** зависимость: модуль необходим при компиляции, но не обязателен при запуске.

```java
module com.example.lib {
    requires static lombok;           // аннотации нужны при компиляции
    requires static org.slf4j;        // опциональное логирование
}
```

Типичные сценарии:
- **Compile-time аннотации**: `Lombok`, `JetBrains Annotations`, `SpotBugs Annotations`
- **Опциональные интеграции**: библиотека проверяет наличие зависимости через `try-catch` и включает функциональность, если модуль доступен
- **Annotation processors**: не нужны в runtime

При отсутствии модуля в runtime обращение к его классам вызовет `NoClassDefFoundError`, поэтому код должен корректно обрабатывать отсутствие:

```java
public class OptionalFeature {
    public static boolean isAvailable() {
        try {
            Class.forName("org.slf4j.Logger");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
```


> [!mcq]
> - [ ] `requires static M` — M загружается при старте если присутствует на classpath | ❌ ПОСЛЕДСТВИЕ: `requires static` — compile-only; если M отсутствует в runtime — модуль просто не загружается; это optional dependency, не eager loading
> - [ ] `requires static` используется для тестовых зависимостей вместо test scope | ❌ ПОСЛЕДСТВИЕ: Lombok при compile-time обработке — типичный кейс; но test зависимости лучше управлять через scope в Maven/Gradle, не `requires static`
> - [x] `requires static M` — M нужен при компиляции, но не при runtime; подходит для Lombok, annotation processors, compile-time-only frameworks | ✓ ПРИМЕНЯТЬ: `requires static lombok` — аннотации Lombok не нужны в runtime 📋 ПРАВИЛО: requires static = compile-time-only optional dependency 🔗 См. Q7
> - [ ] `requires static` транзитивен по умолчанию | ❌ ПОСЛЕДСТВИЕ: `requires static` не транзитивен; клиенты не получают автоматически доступ к optional зависимости

## Q9. Что такое `open module`?

`open module` — модуль, все пакеты которого открыты для рефлексии (как если бы каждый пакет имел `opens`). При этом `exports` по-прежнему нужно указывать явно для compile-time доступа.

```java
// Все пакеты открыты для рефлексии, но только api экспортирован
open module com.example.webapp {
    requires spring.core;
    requires spring.web;
    exports com.example.webapp.api;
}
```

**Когда использовать**: для приложений, активно использующих фреймворки с рефлексией (`Spring Boot`, `Hibernate`), где прописывать `opens` для каждого пакета слишком утомительно. Это компромисс: вы теряете часть инкапсуляции ради удобства.

**Ограничение**: внутри `open module` нельзя использовать директивы `opens` — они конфликтуют с `open` на уровне модуля.


> [!mcq]
> - [ ] `open module` автоматически добавляет `exports *` — все пакеты доступны для компиляции | ❌ ПОСЛЕДСТВИЕ: `open module` открывает рефлексию, но НЕ добавляет exports; для compile-time доступа всё равно нужен явный `exports`
> - [x] `open module M` открывает все пакеты для рефлексии (как opens для каждого); используется для Spring Boot apps где много DI-рефлексии | ✓ ПРИМЕНЯТЬ: Spring Boot приложения на Java 9+; вместо перечисления `opens` для каждого пакета 📋 ПРАВИЛО: open module = удобство рефлексии ценой потери инкапсуляции 🔗 См. Q5
> - [ ] Внутри `open module` можно и нужно указывать `opens` для дополнительного контроля | ❌ ПОСЛЕДСТВИЕ: `opens` внутри `open module` запрещён — compile error; `open module` уже covers all packages
> - [ ] `open module` нельзя комбинировать с `exports` | ❌ ПОСЛЕДСТВИЕ: `exports` и `open module` совместимы; `open module` только про рефлексию; `exports` контролирует compile-time видимость как обычно

## Q10. (!) Что такое `provides` и `uses` в контексте `ServiceLoader`?

**`uses`** объявляет, что модуль потребляет сервис — будет искать реализации через `ServiceLoader`. **`provides ... with`** объявляет реализации сервиса.

```java
// SPI-модуль: определяет контракт
module com.example.spi {
    exports com.example.spi;
}

// Модуль-провайдер: реализует контракт
module com.example.stripe {
    requires com.example.spi;
    provides com.example.spi.PaymentProvider
        with com.example.stripe.StripePaymentProvider;
}

// Модуль-потребитель: ищет реализации
module com.example.app {
    requires com.example.spi;
    uses com.example.spi.PaymentProvider;
}
```

```java
// Код потребителя
ServiceLoader<PaymentProvider> loader =
    ServiceLoader.load(PaymentProvider.class);

for (PaymentProvider provider : loader) {
    System.out.println("Found: " + provider.getName());
    provider.processPayment(amount);
}

// Или с Optional (Java 9+)
PaymentProvider provider = ServiceLoader
    .load(PaymentProvider.class)
    .findFirst()
    .orElseThrow(() -> new RuntimeException("No payment provider found"));
```

```mermaid
graph LR
    SPI[com.example.spi<br/>PaymentProvider interface] 
    STRIPE[com.example.stripe<br/>provides PaymentProvider<br/>with StripeProvider]
    PAYPAL[com.example.paypal<br/>provides PaymentProvider<br/>with PayPalProvider]
    APP[com.example.app<br/>uses PaymentProvider]
    
    APP -->|ServiceLoader.load| SPI
    STRIPE -->|provides...with| SPI
    PAYPAL -->|provides...with| SPI
```

Классический пример из `JDK`: `JDBC`-драйверы регистрируются через `provides java.sql.Driver with ...`.


> [!mcq]
> - [ ] `uses` в consumer-модуле необязателен — ServiceLoader.load() найдёт все implementations на classpath | ❌ ПОСЛЕДСТВИЕ: без `uses` в module-info.java модуль не может использовать ServiceLoader для этого интерфейса; JPMS проверяет uses при старте
> - [ ] `provides X with Y` требует что Y должен экспортироваться через `exports` | ❌ ПОСЛЕДСТВИЕ: provider-класс Y не требует `exports`; ServiceLoader получает доступ через provides-декларацию, минуя обычный exports
> - [x] Consumer: `uses PaymentProvider`; Provider: `provides PaymentProvider with StripeProvider`; ServiceLoader.load() находит все providers через JPMS | ✓ ПРИМЕНЯТЬ: plugin-архитектура; DI-контейнеры; расширяемые системы без жёстких зависимостей 📋 ПРАВИЛО: uses = хочу найти; provides = я реализую 🔗 См. Q11
> - [ ] ServiceLoader в JPMS работает только если consumer и provider в одном JAR | ❌ ПОСЛЕДСТВИЕ: ServiceLoader специально для разных модулей; provider-модуль и consumer-модуль независимы, связываются только через provides/uses

## Q11. Как `ServiceLoader` работает в модульном мире по сравнению с `classpath`?

| Аспект | `classpath` (до Java 9) | `module path` (JPMS) |
|--------|-------------------------|----------------------|
| **Регистрация** | Файл `META-INF/services/<interface>` | Директива `provides ... with` в `module-info.java` |
| **Обнаружение** | Сканирование всех JAR | Только модули с `provides` |
| **Валидация** | В runtime | При разрешении модульного графа (старт приложения) |
| **Безопасность** | Можно подменить файл в JAR | Декларация в `module-info` — часть модуля |

В модульном мире `ServiceLoader` стал предсказуемее: реализации видны только если модуль объявил `provides`, а потребитель — `uses`. Файл `META-INF/services` по-прежнему поддерживается для обратной совместимости (в `automatic` и `unnamed` модулях).


> [!mcq]
> - [ ] В JPMS META-INF/services файлы больше не работают | ❌ ПОСЛЕДСТВИЕ: META-INF/services поддерживается для обратной совместимости в automatic и unnamed модулях; именованные модули должны использовать provides директиву
> - [x] classpath: META-INF/services (сканирование JAR, ошибки в runtime); module path: `provides ... with` (декларативно, валидация при старте) | ✓ ПРИМЕНЯТЬ: именованные модули → provides/uses; legacy classpath → META-INF/services 📋 ПРАВИЛО: JPMS ServiceLoader = validates at startup, not runtime 🔗 См. Q10
> - [ ] JPMS ServiceLoader работает медленнее classpath из-за модульной проверки | ❌ ПОСЛЕДСТВИЕ: JPMS ServiceLoader быстрее на старте т.к. нет сканирования всех JAR; только модули с `provides` проверяются
> - [ ] `provides X with Y` требует явного `exports Y` в module-info | ❌ ПОСЛЕДСТВИЕ: provider-класс не должен быть экспортирован; ServiceLoader получает доступ внутренне; provides — специальный доступ вне exports

## Q12. (!) Как модули влияют на рефлексию и доступ к внутренним `API`?

Модули жёстко ограничивают рефлексивный доступ. Пакет должен быть либо `exports`, либо `opens` для модуля, выполняющего рефлексию.

**Уровни доступа:**

```mermaid
graph TD
    A[Пакет не экспортирован] -->|"Доступ запрещён"| X[InaccessibleObjectException]
    B[exports pkg] -->|"public типы/члены"| Y[Компиляция + Runtime ОК]
    B -->|"setAccessible на private"| X
    C[opens pkg] -->|"Любой доступ через рефлексию"| Z[Полный рефлексивный доступ]
```

Обращение к внутренним пакетам `JDK` (`sun.*`, `jdk.internal.*`) без специальных флагов приводит к ошибке:

```
java.lang.reflect.InaccessibleObjectException:
  Unable to make field private final byte[] java.lang.String.value
  accessible: module java.base does not "opens java.lang" to unnamed module
```

В модульном приложении вместо глобального `--add-opens` нужно точечно указывать `opens` в `module-info.java`, снижая поверхность атаки. Подробнее об ограничениях рефлексии см. [вопросы по аннотациям Java](java-annotations-interview.md).


> [!mcq]
> - [ ] В JPMS setAccessible(true) полностью запрещён для всех полей | ❌ ПОСЛЕДСТВИЕ: setAccessible работает если пакет открыт через `opens`; запрещён только если пакет не экспортирован и не открыт
> - [ ] `exports` пакета достаточно для setAccessible(true) на private поля | ❌ ПОСЛЕДСТВИЕ: exports даёт доступ к public API; для setAccessible на private нужен `opens`; иначе InaccessibleObjectException
> - [x] Без `opens` → InaccessibleObjectException при setAccessible; нужен `opens com.example.entities to spring.core, org.hibernate.orm` | ✓ ПРИМЕНЯТЬ: Spring DI и Hibernate ORM требуют opens для entity пакетов 📋 ПРАВИЛО: exports = вызовы; opens = рефлексия на все члены включая private 🔗 См. Q5
> - [ ] --add-opens работает только для модулей в самом JDK, не для пользовательских | ❌ ПОСЛЕДСТВИЕ: --add-opens работает для любых модулей: `--add-opens com.example.app/com.example.internal=ALL-UNNAMED`

## Q13. Какие флаги JVM используются для обхода модульных ограничений?

| Флаг | Назначение | Пример |
|------|------------|--------|
| `--add-opens` | Открывает пакет для рефлексии | `--add-opens java.base/java.lang=ALL-UNNAMED` |
| `--add-exports` | Экспортирует пакет для compile/runtime доступа | `--add-exports java.base/sun.nio.ch=ALL-UNNAMED` |
| `--add-reads` | Добавляет зависимость чтения между модулями | `--add-reads com.example.app=ALL-UNNAMED` |
| `--add-modules` | Добавляет модули в граф | `--add-modules java.xml.bind` |
| `--patch-module` | Добавляет классы/ресурсы в существующий модуль | `--patch-module java.base=patch.jar` |
| `--illegal-access` | Глобальная политика доступа (убран в Java 17) | `--illegal-access=permit` (Java 9-16) |

```bash
# Типичный запуск Spring Boot с модульными ограничениями
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     -jar my-app.jar
```

**Важно**: `ALL-UNNAMED` — специальная цель, означающая «все классы из classpath (unnamed module)». В модульном приложении лучше указывать конкретные модули вместо `ALL-UNNAMED`.

**Эволюция `--illegal-access`:**
- `Java 9-15`: по умолчанию `permit` (с предупреждениями)
- `Java 16`: по умолчанию `deny`
- `Java 17+`: флаг удалён, строгая инкапсуляция


> [!mcq]
> - [ ] `--add-opens` в Java 17+ удалён вместе с `--illegal-access` | ❌ ПОСЛЕДСТВИЕ: `--add-opens` и `--add-exports` живут в Java 17+; удалён только `--illegal-access`; корректные JVM флаги работают для всех версий
> - [ ] `--illegal-access=permit` включает pre-JPMS поведение в Java 17+ | ❌ ПОСЛЕДСТВИЕ: `--illegal-access` удалён в Java 17; попытка использовать его в Java 17 → JVM warning; нужно переходить на `--add-opens`
> - [x] `--add-opens M/pkg=ALL-UNNAMED` открывает для рефлексии; `--add-exports M/pkg=ALL-UNNAMED` для compile/runtime; `--illegal-access` удалён в Java 17 | ✓ ПРИМЕНЯТЬ: при запуске Spring Boot на Java 17+ без opens в module-info 📋 ПРАВИЛО: --add-opens = runtime workaround; правильное решение = opens в module-info.java 🔗 См. Q12
> - [ ] `--add-modules java.base` нужен явно для каждого запуска | ❌ ПОСЛЕДСТВИЕ: java.base добавляется автоматически; `--add-modules` нужен только для модулей не в default module graph (например java.xml.bind)

## Q14. (!) Что такое `unnamed module`?

**`Unnamed module`** — совокупность всех классов, загруженных с `classpath`. У него нет имени и `module-info.class`.

**Свойства:**
- Неявно **читает** все именованные модули
- **Экспортирует и открывает** все свои пакеты (полный доступ и рефлексия)
- Один `unnamed module` на один `ClassLoader`
- Разные JAR на `classpath` объединяются в один `unnamed module`
- Именованные модули **не могут** объявить `requires` на `unnamed module`

```mermaid
graph TB
    subgraph "Module Path"
        M1[com.example.api<br/>именованный модуль]
        M2[com.example.service<br/>именованный модуль]
    end
    
    subgraph "Classpath"
        U[Unnamed Module<br/>legacy-lib.jar + old-util.jar<br/>всё видно, всё открыто]
    end
    
    U -->|"читает"| M1
    U -->|"читает"| M2
    M1 -.-x|"НЕ может requires"| U
    M2 -->|"через automatic module"| U
```

Это обеспечивает **обратную совместимость**: старые приложения без `module-info.java` продолжают работать без изменений.


> [!mcq]
> - [ ] Именованный модуль может использовать `requires unnamed` для доступа к classpath | ❌ ПОСЛЕДСТВИЕ: `requires unnamed module` синтаксически невозможен; именованный модуль не может зависеть от unnamed module; нужен automatic module как мост
> - [ ] Unnamed module не видит именованные модули из module path | ❌ ПОСЛЕДСТВИЕ: unnamed module неявно читает все именованные модули на module path; именованные не могут читать unnamed
> - [x] Unnamed module = всё на classpath; exports/opens все пакеты; читает все именованные; именованные НЕ могут `requires` на него | ✓ ПРИМЕНЯТЬ: legacy code на classpath работает без изменений рядом с JPMS модулями 📋 ПРАВИЛО: unnamed = обратная совместимость; нет имени = нет requires 🔗 См. Q15
> - [ ] Разные JAR на classpath создают разные unnamed modules | ❌ ПОСЛЕДСТВИЕ: все JAR на classpath объединяются в ОДИН unnamed module на один ClassLoader; это проблема при split packages

## Q15. (!) Что такое `automatic module` и как определяется его имя?

**`Automatic module`** — обычный JAR (без `module-info.class`), помещённый на `module path`. JVM автоматически превращает его в модуль.

**Определение имени (в порядке приоритета):**

1. **Атрибут `Automatic-Module-Name`** в `MANIFEST.MF` — рекомендуемый способ:
   ```
   Automatic-Module-Name: com.google.gson
   ```
2. **Из имени файла JAR** — если атрибут отсутствует:
   - `gson-2.10.1.jar` → `gson`
   - `commons-lang3-3.12.0.jar` → `commons.lang3`
   - Версия и расширение отбрасываются, дефисы заменяются на точки

**Свойства automatic module:**
- `exports` все свои пакеты
- `opens` все свои пакеты для рефлексии
- `requires transitive` все другие модули
- Может **читать** `unnamed module` (в отличие от именованных)
- Поддерживает `META-INF/services` для `ServiceLoader`

Это делает `automatic modules` ключевым мостом при миграции: именованные модули могут объявлять `requires` на `automatic module`, а тот может читать код из `classpath`.


> [!mcq]
> - [ ] Имя automatic module берётся из package declaration в коде | ❌ ПОСЛЕДСТВИЕ: имя берётся из MANIFEST.MF атрибута Automatic-Module-Name или из имени JAR-файла; из кода не читается
> - [x] Automatic module = JAR на module path без module-info; имя из Automatic-Module-Name в MANIFEST.MF или из имени JAR файла; exports/opens всё | ✓ ПРИМЕНЯТЬ: переходный этап миграции; именованный модуль может `requires` automatic module 📋 ПРАВИЛО: automatic = мост между named и unnamed; выставляй Automatic-Module-Name в библиотеках 🔗 См. Q16
> - [ ] Automatic module не поддерживает META-INF/services ServiceLoader | ❌ ПОСЛЕДСТВИЕ: automatic module поддерживает META-INF/services как legacy classpath; это одно из преимуществ при миграции
> - [ ] Automatic module нельзя использовать в production — только для тестов | ❌ ПОСЛЕДСТВИЕ: automatic modules активно используются в production при поэтапной миграции; многие библиотеки (Guava, Jackson) выставляют Automatic-Module-Name

## Q16. В чём ключевые отличия между `unnamed module`, `automatic module` и именованным модулем?

| Характеристика | Unnamed | Automatic | Named |
|----------------|---------|-----------|-------|
| **Расположение** | `classpath` | `module path` (без `module-info`) | `module path` (с `module-info`) |
| **Имя** | Нет | Из `MANIFEST.MF` или имени JAR | Из `module-info.java` |
| **`exports`** | Все пакеты | Все пакеты | Только явно указанные |
| **`opens`** | Все пакеты | Все пакеты | Только явно указанные |
| **`requires`** | Все модули (неявно) | Все модули (неявно + transitive) | Только явно указанные |
| **Читает unnamed** | Да | Да | Нет |
| **Можно `requires`** | Нет | Да | Да |
| **`ServiceLoader`** | `META-INF/services` | `META-INF/services` | `provides...with` |

**Ключевая разница**: `automatic module` можно использовать из именованного модуля через `requires`, а `unnamed module` — нельзя. Это делает `automatic modules` критичными для поэтапной миграции.


> [!mcq]
> - [ ] Unnamed module может зависеть от automatic module через `requires` | ❌ ПОСЛЕДСТВИЕ: unnamed module не имеет module-info.java поэтому не может объявлять `requires`; читает все модули неявно
> - [ ] Именованный модуль автоматически видит unnamed module через transitive | ❌ ПОСЛЕДСТВИЕ: именованный модуль не может `requires unnamed module`; для доступа к classpath нужен automatic module как мост
> - [x] Unnamed: classpath, нет имени, нельзя requires; Automatic: module path без module-info, есть имя, можно requires; Named: module path + module-info, явные exports/requires | ✓ ПРИМЕНЯТЬ: понять взаимодействие при гибридной миграции 📋 ПРАВИЛО: unnamed ← automatic ← named; только в этом направлении через automatic 🔗 См. Q14
> - [ ] Все три типа имеют одинаковую инкапсуляцию — разница только в месте на диске | ❌ ПОСЛЕДСТВИЕ: Named имеет строгую инкапсуляцию (только exports); Unnamed и Automatic — всё открыто; разница фундаментальная

## Q17. (!) Что такое `split package` и почему это проблема в `JPMS`?

**`Split package`** — ситуация, когда один и тот же пакет (`com.example.util`) находится в нескольких модулях одновременно.

В `JPMS` это **запрещено**: модульная система должна однозначно определять, из какого модуля загружать типы пакета. При обнаружении split package приложение не запустится:

```
java.lang.module.ResolutionException:
  Modules moduleA and moduleB export package com.example.util to module moduleC
```

**Типичные случаи:**
- Две библиотеки содержат одинаковый пакет (например, `javax.annotation` в `java.xml.ws.annotation` и `javax.annotation-api.jar`)
- При миграции монолита на модули код одного пакета разнесён по артефактам
- Библиотеки, которые «дополняют» пакеты JDK

**Решения:**
1. **Переразбить пакеты** — каждый пакет в одном модуле
2. **Выделить общий пакет** в отдельный модуль-библиотеку
3. **`--patch-module`** — добавить классы одного JAR в другой модуль (временный workaround)
4. **Исключить дублирующую зависимость** в сборке (`Maven exclusions`)


> [!mcq]
> - [ ] Split packages на classpath запрещены так же как на module path | ❌ ПОСЛЕДСТВИЕ: на classpath split packages допускаются (первый JAR в порядке classpath побеждает); только JPMS запрещает их с ResolutionException
> - [x] Split package = один пакет в нескольких модулях → ResolutionException при старте; решается переразбивкой пакетов или Maven exclusions | ✓ ПРИМЕНЯТЬ: диагностика через jdeps --check при миграции на JPMS 📋 ПРАВИЛО: один пакет = один модуль; нарушение = fail-fast при старте 🔗 См. Q1
> - [ ] Split packages в тестах (test scope) тоже запрещены JPMS | ❌ ПОСЛЕДСТВИЕ: тестовый код может иметь split packages если тесты на classpath (unnamed module); JPMS строго только для named модулей
> - [ ] --patch-module решает split packages навсегда — можно не трогать код | ❌ ПОСЛЕДСТВИЕ: --patch-module — временный workaround; в production нужно устранять split packages рефакторингом

## Q18. Что такое `multi-release JAR` и как он связан с модулями?

**`Multi-release JAR`** (`MRJAR`, `JEP 238`) — JAR-файл, содержащий разные версии классов для разных версий Java. Структура:

```
my-lib.jar
├── META-INF/
│   ├── MANIFEST.MF         # содержит "Multi-Release: true"
│   └── versions/
│       ├── 9/
│       │   ├── module-info.class
│       │   └── com/example/Util.class    # версия для Java 9+
│       └── 17/
│           └── com/example/Util.class    # версия для Java 17+
├── com/
│   └── example/
│       └── Util.class                    # базовая версия (Java 8)
```

**Связь с модулями**: `MRJAR` позволяет добавить `module-info.class` в `META-INF/versions/9/`, сохраняя совместимость с Java 8 в базовом варианте. Это ключевой механизм для библиотек, которые хотят поддерживать и `classpath` (Java 8), и `module path` (Java 9+).

**Важно для собеседования**: `MRJAR` — не замена модулям, а инструмент совместимости. JVM выбирает наиболее подходящую версию класса автоматически.


> [!mcq]
> - [ ] Multi-release JAR создаёт отдельные исполняемые файлы для каждой версии Java | ❌ ПОСЛЕДСТВИЕ: MRJAR — один JAR файл; JVM сама выбирает правильную версию класса из META-INF/versions/<version>/ в зависимости от runtime
> - [ ] module-info.class в MRJAR должен быть в корне, не в META-INF/versions | ❌ ПОСЛЕДСТВИЕ: module-info.class для модульного MRJAR должен быть в META-INF/versions/9/ (или выше); базовая версия для Java 8 совместимости — в корне
> - [x] MRJAR (`Multi-Release: true` в MANIFEST.MF) содержит разные классы для разных Java версий в META-INF/versions/<N>/; module-info.class там же для Java 9+ | ✓ ПРИМЕНЯТЬ: библиотеки поддерживающие Java 8 и 9+ одновременно 📋 ПРАВИЛО: MRJAR = один JAR, несколько Java-версий 🔗 См. Q19
> - [ ] MRJAR не поддерживается Maven — нужен Gradle | ❌ ПОСЛЕДСТВИЕ: maven-compiler-plugin поддерживает MRJAR через multiRelease конфигурацию; это стандартный инструментарий

## Q19. (!) Что такое `jlink` и зачем собирать custom runtime image?

`jlink` — утилита из `JDK`, создающая **custom runtime image**: минимальный образ среды выполнения только с нужными модулями.

```bash
# Создание компактного runtime
jlink --add-modules java.base,java.sql,com.example.app \
      --module-path $JAVA_HOME/jmods:mods \
      --output runtime-image \
      --strip-debug \
      --no-header-files \
      --no-man-pages \
      --compress zip-9

# Запуск приложения из собранного образа
./runtime-image/bin/java -m com.example.app/com.example.Main
```

**Преимущества:**

| Аспект | Полный JDK | `jlink` image |
|--------|-----------|---------------|
| **Размер** | ~300+ MB | 30-50 MB (зависит от модулей) |
| **Безопасность** | Все модули JDK | Только используемые — меньше поверхность атаки |
| **Деплой** | Требует установленный JDK | Самодостаточный образ |
| **Docker** | Тяжёлый базовый образ | Компактный контейнер |

**Ограничение**: `jlink` работает только с именованными модулями. Если приложение использует `automatic modules` или `unnamed module`, `jlink` не сможет их включить.

**Для Docker/Kubernetes**: `jlink` image + `FROM scratch` или Alpine даёт контейнеры размером 40-60 MB вместо 200+ MB с полным JDK.


> [!mcq]
> - [ ] jlink работает с любыми JAR включая unnamed module | ❌ ПОСЛЕДСТВИЕ: jlink требует только именованные модули с module-info.java; automatic и unnamed modules не поддерживаются — это ограничение jlink
> - [x] jlink создаёт custom runtime только с нужными модулями; Docker image 40-60MB vs 200+MB с полным JDK; требует только named modules | ✓ ПРИМЕНЯТЬ: контейнеризованные Java микросервисы на JPMS 📋 ПРАВИЛО: jlink = custom JRE = меньше размер + меньше attack surface 🔗 См. Q3
> - [ ] jlink нельзя использовать в Docker — только на bare metal | ❌ ПОСЛЕДСТВИЕ: jlink + Docker = классическая комбинация; jlink образ кладут в FROM scratch или alpine контейнер
> - [ ] jlink требует покупки коммерческой лицензии Oracle JDK | ❌ ПОСЛЕДСТВИЕ: jlink входит в OpenJDK бесплатно; все популярные дистрибутивы (Temurin, Corretto, Liberica) включают jlink

## Q20. Какие плагины и опции поддерживает `jlink`?

`jlink` имеет систему плагинов для оптимизации собранного образа:

| Плагин / Опция | Назначение |
|----------------|------------|
| `--strip-debug` | Удаляет отладочную информацию из классов |
| `--compress zip-9` | Сжимает ресурсы (Java 21+: `zip-0`..`zip-9`) |
| `--no-header-files` | Исключает C-header файлы |
| `--no-man-pages` | Исключает man-страницы |
| `--add-modules` | Указывает модули для включения |
| `--bind-services` | Автоматически включает все `provides` для `uses` |
| `--launcher name=module/main` | Создаёт launcher-скрипт |
| `--endian` | Порядок байтов (`little` / `big`) |

```bash
# Полный пример с launcher и сжатием
jlink --module-path mods:$JAVA_HOME/jmods \
      --add-modules com.example.app \
      --launcher myapp=com.example.app/com.example.Main \
      --strip-debug \
      --compress zip-6 \
      --no-header-files \
      --no-man-pages \
      --output dist/myapp

# Запуск через launcher
./dist/myapp/bin/myapp
```

Для анализа зависимостей перед `jlink` полезна утилита `jdeps`:

```bash
# Анализ зависимостей JAR
jdeps --module-path mods -s my-app.jar

# Генерация module-info.java
jdeps --generate-module-info out my-lib.jar
```


> [!mcq]
> - [ ] jlink --bind-services включает только явно перечисленные provides | ❌ ПОСЛЕДСТВИЕ: `--bind-services` автоматически включает ВСЕ провайдеры для объявленных `uses`; без него ServiceLoader не найдёт реализации
> - [ ] --compress работает только для Java 17+ | ❌ ПОСЛЕДСТВИЕ: --compress доступен в jlink с Java 9; синтаксис изменился в Java 21 (zip-N вместо числовых уровней)
> - [x] jlink плагины: --strip-debug, --compress zip-6, --no-header-files, --no-man-pages, --launcher name=module/main | ✓ ПРИМЕНЯТЬ: оптимизация Docker образов; --launcher создаёт удобный скрипт запуска 📋 ПРАВИЛО: jlink compress + strip-debug + no-headers = максимально компактный runtime 🔗 См. Q19
> - [ ] jlink --add-modules нельзя комбинировать с --bind-services | ❌ ПОСЛЕДСТВИЕ: оба флага комбинируются; --add-modules указывает начальные модули, --bind-services автоматически расширяет граф через ServiceLoader

## Q21. (!) Какие существуют стратегии миграции на модули?

Существуют две основные стратегии:

### Bottom-Up (снизу вверх) — рекомендуемая

Начинаем с библиотек без зависимостей (листья графа зависимостей) и двигаемся к приложению:

```mermaid
graph BT
    A[utils.jar<br/>1. Добавить module-info] --> B[service.jar<br/>2. Добавить module-info]
    B --> C[app.jar<br/>3. Добавить module-info]
    D[external-lib.jar<br/>automatic module] --> B
```

**Плюсы**: каждый шаг компилируется и тестируется, зависимости уже модульные.
**Минусы**: сторонние библиотеки могут блокировать процесс.

### Top-Down (сверху вниз)

Начинаем с приложения верхнего уровня, зависимости временно остаются как `automatic modules`:

```mermaid
graph TB
    A[app.jar<br/>1. Добавить module-info<br/>requires automatic modules] --> B[service.jar<br/>automatic module]
    A --> C[utils.jar<br/>automatic module]
    B --> D[external-lib.jar<br/>automatic module]
```

**Плюсы**: быстро видно модульные границы приложения.
**Минусы**: зависимость от `automatic modules`, нет гарантий стабильности имён.

### Практический чеклист миграции

1. Запустить `jdeps` для анализа зависимостей
2. Убрать использование внутренних API JDK (`sun.*`, `com.sun.*`)
3. Решить проблемы `split packages`
4. Добавить `Automatic-Module-Name` в `MANIFEST.MF` сторонних JAR (или дождаться обновления)
5. Создать `module-info.java` с `requires`, `exports`, `opens`
6. Добавить `--add-opens` для фреймворков, использующих рефлексию
7. Протестировать в модульном режиме


> [!mcq]
> - [ ] Bottom-Up миграция: начинаем с приложения верхнего уровня и спускаемся к библиотекам | ❌ ПОСЛЕДСТВИЕ: Bottom-Up = снизу вверх = начинаем с leaf-библиотек; то что описано — Top-Down стратегия
> - [ ] Top-Down миграция невозможна пока все зависимости не переведены в named modules | ❌ ПОСЛЕДСТВИЕ: Top-Down использует automatic modules как мост; позволяет мигрировать до того как все зависимости получат module-info
> - [x] Bottom-Up (рекомендован): leaf-библиотеки первыми → надёжно; Top-Down: app первым, зависимости как automatic → быстро видно границы | ✓ ПРИМЕНЯТЬ: Bottom-Up для библиотек с зависимостями только от JDK; Top-Down для быстрого прототипа 📋 ПРАВИЛО: Bottom-Up = надёжно медленно; Top-Down = быстро рискованно 🔗 См. Q15
> - [ ] После миграции нельзя сохранить обратную совместимость с Java 8 | ❌ ПОСЛЕДСТВИЕ: Multi-Release JAR (MRJAR) позволяет иметь module-info.java для Java 9+ и Java 8 совместимый код в базовом JAR

## Q22. Как мигрировать проект с `Maven`/`Gradle` на модули?

### Maven

```xml
<!-- Каждый Maven-модуль = один JPMS-модуль -->
<!-- module-info.java в src/main/java/ -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <release>17</release>
    </configuration>
</plugin>

<!-- Для тестов: открытие пакетов -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>
            --add-opens com.example.app/com.example.internal=ALL-UNNAMED
        </argLine>
    </configuration>
</plugin>
```

### Gradle

```groovy
// Для Java 9+ модулей
plugins {
    id 'java-library'
}

java {
    modularity.inferModulePath = true  // Gradle автоматически определяет module path
}

// module-info.java в src/main/java/
// Gradle 7+ автоматически размещает зависимости на module path,
// если в проекте есть module-info.java

tasks.withType(Test).configureEach {
    jvmArgs += ['--add-opens', 'com.example.app/com.example.internal=ALL-UNNAMED']
}
```

**Ключевой момент**: `Maven`/`Gradle` корректно обрабатывают `module-info.java`, если он находится в `src/main/java/`. Обе системы сборки поддерживают гибрид `classpath` + `module path`.


> [!mcq]
> - [ ] Maven не поддерживает module-info.java — нужен специальный плагин moditect | ❌ ПОСЛЕДСТВИЕ: maven-compiler-plugin 3.6+ нативно поддерживает module-info.java; moditect нужен только для legacy кода или специфических конфигураций
> - [ ] Gradle modularity.inferModulePath = true нужно прописывать в каждом подпроекте вручную | ❌ ПОСЛЕДСТВИЕ: настройку можно сделать в корневом build.gradle через allprojects {}; Gradle 7+ часто автоматически определяет module path по наличию module-info.java
> - [x] Maven: maven-compiler-plugin 3.6+ автоматически; maven-surefire с --add-opens для тестов; Gradle: modularity.inferModulePath = true | ✓ ПРИМЕНЯТЬ: module-info.java в src/main/java; обе системы поддерживают гибрид classpath + module path 📋 ПРАВИЛО: Maven/Gradle поддерживают JPMS без доп. плагинов начиная с 2017+ версий 🔗 См. Q21
> - [ ] Module path не работает с fat JAR (Spring Boot jar) | ❌ ПОСЛЕДСТВИЕ: Spring Boot fat JAR и JPMS имеют ограниченную совместимость, но Spring Boot 3.x улучшил поддержку; для jlink нужна нестандартная конфигурация

## Q23. Что такое `ModuleLayer` и зачем он нужен?

`ModuleLayer` — механизм для создания дополнительных слоёв модулей поверх boot layer. Позволяет динамически загружать модули в runtime.

```java
// Создание кастомного ModuleLayer
ModuleFinder finder = ModuleFinder.of(Path.of("plugins/"));
ModuleLayer parent = ModuleLayer.boot();

Configuration cfg = parent.configuration().resolve(
    finder,
    ModuleFinder.of(),           // fallback finder
    Set.of("com.example.plugin") // корневые модули
);

ModuleLayer layer = parent.defineModulesWithOneLoader(
    cfg, ClassLoader.getSystemClassLoader()
);

// Загрузка класса из нового слоя
Class<?> pluginClass = layer.findLoader("com.example.plugin")
    .loadClass("com.example.plugin.MyPlugin");
```

```mermaid
graph TB
    BL[Boot Layer<br/>java.base, java.sql, ...<br/>com.example.app]
    PL1[Plugin Layer 1<br/>com.example.plugin.a]
    PL2[Plugin Layer 2<br/>com.example.plugin.b]
    
    BL --> PL1
    BL --> PL2
```

**Сценарии использования:**
- **Плагинные системы** — загрузка/выгрузка плагинов без перезапуска
- **Мультиверсионность** — разные версии одного модуля в разных слоях
- **Изоляция** — каждый слой имеет свой `ClassLoader`, предотвращая конфликты
- **Серверы приложений** — `Jakarta EE` серверы могут изолировать деплойменты

`ModuleLayer` — продвинутая тема, но важная для понимания полной архитектуры `JPMS`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. Как в `JPMS` обрабатываются циклические зависимости между модулями? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`JPMS` **запрещает** циклические зависимости: `module A requires B` и `module B requires A` одновременно невозможны. При попытке сборки или запуска — ошибка:

```
java.lang.module.ResolutionException:
  Cycle detected: com.example.a requires com.example.b requires com.example.a
```

**Решения:**

```mermaid
graph LR
    subgraph "Проблема: цикл"
        A1[Module A] -->|requires| B1[Module B]
        B1 -->|requires| A1
    end

    subgraph "Решение: общий API"
        API[Module API<br/>общие интерфейсы]
        A2[Module A] -->|requires| API
        B2[Module B] -->|requires| API
    end
```

1. **Выделить общий контракт** в третий модуль (`api`)
2. **Инвертировать зависимость** через интерфейсы (DIP)
3. **`ServiceLoader`** (`uses`/`provides`) для слабой связности
4. **Объединить модули**, если они слишком тесно связаны

Запрет циклов в `JPMS` заставляет проектировать чистую архитектуру с однонаправленными зависимостями — аналогично принципам в [паттернах проектирования](../../design-patterns/design-patterns-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. Как тестировать модульное приложение? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Тестирование модульного приложения имеет специфику:

**Unit-тесты**: обычно тесты находятся в том же модуле (или в тестовом source set). Компилятор и runtime автоматически «патчат» тестовый код в модуль:

```bash
# Maven Surefire автоматически добавляет --patch-module и --add-reads
# для тестового кода
```

**Доступ к неэкспортируемым пакетам**:

```java
// module-info.java основного модуля
module com.example.service {
    exports com.example.service.api;
    // internal пакет НЕ экспортирован
}
```

```groovy
// build.gradle — открытие для тестов
tasks.withType(Test).configureEach {
    jvmArgs += [
        '--add-opens', 'com.example.service/com.example.service.internal=ALL-UNNAMED',
        '--add-reads', 'com.example.service=ALL-UNNAMED'
    ]
}
```

**Интеграционные тесты**: полезно запускать приложение в модульном режиме, чтобы раньше поймать проблемы, которые на `classpath` не проявляются (`split packages`, отсутствие `opens` для фреймворков).

**Whitebox vs Blackbox тестирование:**
- **Blackbox** — тесты используют только экспортированный API модуля
- **Whitebox** — тесты через `--add-opens` / `--add-exports` получают доступ к internal пакетам


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. (!) Как `Spring`/`Spring Boot` работает с модульной системой? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

На практике большинство `Spring Boot` приложений до сих пор работают на `classpath` без `module-info.java`. Причины:

1. **`Spring` активно использует рефлексию**: создание бинов, инъекция зависимостей, AOP-прокси
2. **Classpath scanning**: `@ComponentScan` сканирует пакеты, что требует доступа ко всем классам
3. **Dynamic proxies**: CGLIB-прокси создают подклассы, что требует `opens`

**Если вы модуляризуете `Spring Boot` приложение:**

```java
open module com.example.webapp {
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.web;
    requires spring.data.jpa;
    requires jakarta.persistence;

    // open module открывает всё для рефлексии
    exports com.example.webapp.api;
}
```

Или точечно без `open module`:

```java
module com.example.webapp {
    requires spring.boot;
    requires spring.context;
    requires spring.web;

    exports com.example.webapp.api;
    opens com.example.webapp to spring.core, spring.beans, spring.context;
    opens com.example.webapp.config to spring.core, spring.context;
    opens com.example.webapp.model to org.hibernate.orm, com.fasterxml.jackson.databind;
}
```

**На собеседовании**: важно понимать, что `Spring` полноценно работает с JPMS, но требует значительного количества `opens` директив. Подробнее о Spring — в вопросах по [Java Core](java-core-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. Как `Hibernate`/`JPA` взаимодействует с модулями? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`Hibernate` — один из наиболее «рефлексивных» фреймворков. Для работы с модулями необходимо:

```java
module com.example.persistence {
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;

    // Hibernate нужен рефлексивный доступ к entity-классам
    opens com.example.persistence.entity to org.hibernate.orm.core;

    exports com.example.persistence.repository;
}
```

**Что `Hibernate` делает через рефлексию:**
- Создаёт экземпляры entity-классов (нужен default конструктор)
- Читает/пишет приватные поля (lazy loading, dirty checking)
- Создаёт прокси для lazy-загрузки коллекций
- Обрабатывает аннотации `@Entity`, `@Column`, `@OneToMany`

Без `opens` для entity-пакетов `Hibernate` выбросит:

```
org.hibernate.MappingException: Could not get constructor for
  org.hibernate.persister.entity.SingleTableEntityPersister
Caused by: InaccessibleObjectException
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q28. Какие практики проектирования модулей считаются хорошими? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Принципы модульного дизайна:**

1. **Минимальный экспорт** — экспортировать только API-пакеты, всё остальное скрывать
2. **Модуль = осмысленная единица** — по домену или функциональности, не по техническим слоям
3. **`opens` точечно** — только для конкретных фреймворков, не `open module` без необходимости
4. **Стабильные API-модули** — отделять API от реализации (`com.example.api` vs `com.example.impl`)
5. **Avoid split packages** — каждый пакет в одном модуле
6. **`requires transitive`** для публичных зависимостей API

**Пример хорошей структуры:**

```
com.example.api          // контракты (exports)
com.example.impl         // реализации (не экспортируется)
com.example.spi          // точки расширения (exports)
com.example.internal     // внутренние утилиты (не экспортируется)
```

```java
module com.example.service {
    requires transitive com.example.api;    // API видно клиентам
    requires com.example.impl;              // impl скрыт от клиентов

    exports com.example.service.api;
    provides com.example.spi.Processor
        with com.example.service.DefaultProcessor;
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q29. Какие типичные ошибки допускают при работе с модулями? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Ошибка | Последствие | Решение |
|--------|-------------|---------|
| Забыли `requires` | `java.lang.module.FindException` | Добавить зависимость |
| Не указали `opens` для фреймворка | `InaccessibleObjectException` | Добавить `opens ... to` |
| Split package между модулями | `ResolutionException` | Переразбить пакеты |
| `requires` на `unnamed module` | Ошибка компиляции | Использовать `automatic module` |
| `open module` везде | Потеря инкапсуляции | Использовать точечные `opens` |
| Имя automatic module из файла JAR | Нестабильное имя при обновлении | Попросить автора добавить `Automatic-Module-Name` |
| Циклическая зависимость | `ResolutionException` | Выделить общий API-модуль |
| Экспорт implementation-пакетов | Хрупкий API | Экспортировать только контракты |


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q30. Каковы перспективы развития модульной системы `Java`? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Модульная система `Java` продолжает развиваться:

- **Строгая инкапсуляция** (Java 16-17): `--illegal-access` удалён, доступ к internal API закрыт по умолчанию
- **`jlink` оптимизации**: улучшение сжатия, поддержка CDS (Class Data Sharing) в образах
- **Project Leyden** (в разработке): статическая компиляция и оптимизация на основе модульной информации, AOT-компиляция
- **Project Panama**: модули для FFI (Foreign Function Interface)
- **Adoption**: библиотеки постепенно добавляют `Automatic-Module-Name` и `module-info.java`

**Реальность**: несмотря на то, что `JPMS` существует с 2017 года (`Java 9`), массовое adoption идёт медленно. Большинство `Spring Boot` приложений работают на `classpath`. Однако понимание `JPMS` важно для:
- Работы с библиотеками и `JDK` internals
- Понимания ошибок `InaccessibleObjectException`
- Оптимизации Docker-образов через `jlink`
- Собеседований по глубоким знаниям [Java Core](java-core-interview.md) и [JVM](../../jvm/jvm-interview.md)


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q31. Чем отличаются `unnamed module`, `automatic module` и `named module` на практике? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Три типа модулей — центральное понятие JPMS. Разница между ними определяет, как артефакты взаимодействуют при миграции и в смешанных проектах.

**Unnamed module** — всё, что попало на `classpath`. Это не настоящий модуль: у него нет имени, нет `module-info.java`. Он существует ради обратной совместимости.

**Automatic module** — JAR без `module-info.class`, помещённый на `module path`. JVM автоматически присваивает ему имя.

**Named module** — JAR с `module-info.class`. Полноценный JPMS-модуль с явными `exports` и `requires`.

**Практическая разница:**

| | Unnamed | Automatic | Named |
|--|---------|-----------|-------|
| Может `requires`-ить именованный | да (неявно) | да | да |
| Может `requires`-ить `unnamed` | да (неявно) | да | **нет** |
| Именованный может `requires`-ить | **нет** | да | да |
| Экспортирует всё | да | да | только через `exports` |
| Открыт для рефлексии | да | да | только через `opens` |

**Ключевое для миграции:** именованный модуль **не может** объявить `requires` на `unnamed module`. Это «стена» между модульным миром и legacy classpath. Именно поэтому `automatic module` — необходимый мост: именованный модуль может `requires`-ить `automatic`, а тот уже читает `unnamed`.

```
Именованный → requires → Automatic → читает → Unnamed
(module-info)              (без module-info,   (classpath)
                            на module path)
```

Диагностика типа артефакта:

```bash
# Проверить, является ли JAR модулем и каким
jar --describe-module --file=mylib.jar
# Для named module: выведет module-info
# Для automatic module: "No module descriptor found, not a modular JAR"
# Unnamed: вообще не на module path
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q32. `Split packages`: почему запрещены в `JPMS` и как их устранить? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Split package** — ситуация, когда классы одного пакета (`com.example.util`) находятся в разных модулях одновременно. В `JPMS` это жёстко запрещено.

**Почему запрещено:** модульная система должна однозначно определять, из какого модуля загружать классы пакета. При split package это невозможно — возникает неопределённость, которая в classpath-мире решалась «первый найденный побеждает», но давала непредсказуемые результаты.

**Классический пример:**

```
javax.annotation-api-1.3.2.jar  → содержит javax.annotation.*
java.xml.ws.annotation (JDK)    → также содержит javax.annotation.*

# При запуске на module path:
java.lang.module.ResolutionException: Modules javax.annotation.api and
  java.xml.ws.annotation export package javax.annotation to module myapp
```

**Диагностика через `jdeps`:**

```bash
# Найти split packages в наборе JAR
jdeps --multi-release 17 --module-path mods --check com.example.app
```

**Решения:**

| Стратегия | Применение |
|-----------|------------|
| **Исключить дублирующую зависимость** | Maven `<exclusion>`, Gradle `exclude` — самое простое |
| **`--patch-module`** | Слить два JAR в один модуль: `--patch-module java.annotation=javax.annotation-api.jar` |
| **Переименовать пакеты** | `relocate` в Maven Shade/Gradle Shadow Plugin |
| **Перейти на Jakarta EE** | Многие `javax.*` → `jakarta.*` в Jakarta EE 9+ |
| **Разбить JAR** | Если собственный код — вынести общий пакет в отдельный модуль |

```bash
# Быстрый workaround через --patch-module
java --module-path mods \
     --patch-module java.xml.ws.annotation=javax.annotation-api.jar \
     -m com.example.app/com.example.Main
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q33. `--add-opens` и `--add-exports`: когда и как использовать для рефлексии с `JPMS`? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Флаги `--add-opens` и `--add-exports` — механизм для обхода модульных ограничений без изменения `module-info.java`. Необходимы при использовании фреймворков с рефлексией или внутренних JDK API.

**`--add-exports`**: открывает пакет для compile-time и runtime доступа:

```bash
# Синтаксис: --add-exports <module>/<package>=<target-module>
# ALL-UNNAMED — все классы из classpath

java --add-exports java.base/sun.nio.ch=ALL-UNNAMED -jar my-app.jar

# При компиляции:
javac --add-exports java.base/sun.nio.ch=com.example.app -d out src/...
```

**`--add-opens`**: открывает пакет для рефлексии (`setAccessible(true)`):

```bash
# Типичный набор для Spring Boot:
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     --add-opens java.base/java.util.concurrent=ALL-UNNAMED \
     -jar spring-boot-app.jar
```

**Разница между флагами:**

| Флаг | Доступ | Аналог в module-info |
|------|--------|---------------------|
| `--add-exports` | Public типы и члены | `exports pkg to M` |
| `--add-opens` | Все типы и члены через рефлексию | `opens pkg to M` |

**Добавление в `build.gradle.kts` для тестов:**

```kotlin
tasks.withType<Test> {
    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED"
    )
}
```

**Важно:** `--add-opens` и `--add-exports` — временные меры. Долгосрочное решение — добавить `opens`/`exports` в `module-info.java` или обновить зависимость с поддержкой JPMS.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q34. `jlink`: создание custom minimal JRE — практическое руководство ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`jlink` — утилита для создания **custom runtime image**: самодостаточного дистрибутива JVM только с необходимыми модулями. Требование: все модули должны быть **именованными**.

**Полный рабочий пример:**

```bash
# 1. Анализируем зависимости
jdeps --print-module-deps \
      --ignore-missing-deps \
      --multi-release 17 \
      --module-path mods \
      myapp.jar
# Вывод: java.base,java.sql,java.logging

# 2. Создаём custom runtime
jlink \
  --module-path $JAVA_HOME/jmods:mods \
  --add-modules com.example.app,java.base,java.sql,java.logging \
  --output dist/myapp-runtime \
  --strip-debug \
  --no-header-files \
  --no-man-pages \
  --compress zip-6 \
  --launcher myapp=com.example.app/com.example.Main

# 3. Запуск
./dist/myapp-runtime/bin/myapp
```

**Docker (многоэтапная сборка):**

```dockerfile
FROM eclipse-temurin:17-jdk AS builder
COPY mods/ /app/mods/
RUN $JAVA_HOME/bin/jlink \
    --module-path $JAVA_HOME/jmods:/app/mods \
    --add-modules com.example.app \
    --output /app/runtime \
    --strip-debug --compress zip-6 \
    --no-header-files --no-man-pages

FROM debian:bullseye-slim
COPY --from=builder /app/runtime /opt/myapp
ENTRYPOINT ["/opt/myapp/bin/myapp"]
# Итоговый размер: ~50 MB вместо 300+ MB
```

**Типичные ошибки:**
- `Error: automatic module found in jlink` — один из модулей является automatic. Решение: заменить на именованный или собрать fat JAR.
- `Module not found` — модуль не на module path. Проверить `--module-path`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q35. `jdeps`: анализ зависимостей модулей перед миграцией ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`jdeps` — инструмент статического анализа зависимостей JAR-файлов. Незаменим при подготовке к миграции на JPMS.

**Основные режимы работы:**

```bash
# Краткий анализ зависимостей (по модулям JDK)
jdeps -s myapp.jar
# Вывод:
# myapp.jar -> java.base
# myapp.jar -> java.sql
# myapp.jar -> not found

# Анализ всех JAR в директории
jdeps --module-path lib -s --multi-release 17 lib/*.jar

# Генерация module-info.java
jdeps --generate-module-info out/ mylib.jar
# Создаёт: out/mylib/module-info.java

# Поиск использования внутренних JDK API (sun.*, jdk.internal.*)
jdeps --jdk-internals myapp.jar
```

**Пример вывода `--jdk-internals`:**

```
myapp.jar -> JDK internal API: sun.misc.Unsafe
myapp.jar -> JDK internal API: sun.security.util.KeyUtil

JDK internal API                Suggested Replacement
sun.misc.Unsafe                 See http://openjdk.java.net/jeps/260
sun.security.util.KeyUtil       java.security.KeyFactory
```

**Генерированный `module-info.java`:**

```bash
jdeps --generate-module-info generated/ --module-path lib lib/mylib.jar
# Результат: generated/mylib/module-info.java
# module mylib {
#     requires java.base;
#     requires java.sql;
#     exports com.example.mylib;
# }
```

Этот файл — отправная точка: нужно доработать (добавить `opens` для рефлексии, проверить экспорт).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q36. `ServiceLoader` с `JPMS`: директивы `uses` и `provides...with` в деталях ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`ServiceLoader` в модульном мире работает принципиально иначе: декларирование сервисов переносится из `META-INF/services/` в `module-info.java`.

**Полный пример SPI-архитектуры:**

```java
// Модуль SPI (контракт)
module com.example.spi {
    exports com.example.spi; // интерфейс PaymentProvider
}

// Модуль реализации
module com.example.stripe {
    requires com.example.spi;
    // НЕ нужно exports для реализации!
    provides com.example.spi.PaymentProvider
        with com.example.stripe.StripeProvider,
             com.example.stripe.StripeTestProvider; // можно несколько
}

// Модуль приложения (потребитель)
module com.example.app {
    requires com.example.spi;
    uses com.example.spi.PaymentProvider; // объявляет потребление
}
```

```java
// Java 9+: стримовое API
ServiceLoader<PaymentProvider> loader = ServiceLoader.load(PaymentProvider.class);

Optional<PaymentProvider> stripe = loader.stream()
    .filter(p -> p.type().getSimpleName().contains("Stripe"))
    .map(ServiceLoader.Provider::get)
    .findFirst();
```

**Ключевое отличие от classpath:**

| Аспект | `classpath` | `module path` (JPMS) |
|--------|-------------|----------------------|
| Регистрация | `META-INF/services/<interface>` | `provides ... with` в `module-info.java` |
| Обнаружение | Сканирование всех JAR | Только модули с `provides` |
| Валидация | В runtime | При разрешении модульного графа |

**Важно:** модуль-провайдер должен быть на `module path`, иначе `provides` не работает. `META-INF/services` продолжает работать для `automatic` и `unnamed` модулей.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q37. Совместимость `Spring`, `Hibernate` и `Jackson` с `JPMS`: типичные проблемы ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

На практике большинство Spring Boot приложений работают на `classpath` без `module-info.java`. Если добавить `module-info.java`, возникают характерные ошибки.

**Spring Framework:**

```
InaccessibleObjectException: Unable to make field private final ...
  accessible: module com.example.app does not "opens com.example.config"
  to module spring.core
```

Решение:

```java
module com.example.app {
    requires spring.core;
    requires spring.context;
    requires spring.web;
    requires spring.boot;
    requires spring.boot.autoconfigure;

    // Spring нужна рефлексия для бинов, AOP-прокси, @Value
    opens com.example.app to spring.core, spring.beans, spring.context;
    opens com.example.app.config to spring.core, spring.context;
    opens com.example.app.service to spring.core, spring.beans;

    exports com.example.app.api;
}
```

**Hibernate:**

```
InaccessibleObjectException: Unable to make field private String name
  accessible: module com.example.app does not "opens com.example.entity"
  to module org.hibernate.orm.core
```

Решение:

```java
module com.example.app {
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    // Hibernate нужен доступ к полям entity (lazy loading, dirty checking)
    opens com.example.app.entity to org.hibernate.orm.core;
}
```

**Jackson:**

```
InvalidDefinitionException: No serializer found for class com.example.dto.UserDto
```

Решение:

```java
module com.example.app {
    requires com.fasterxml.jackson.databind;

    // Jackson нужна рефлексия для сериализации/десериализации
    opens com.example.app.dto to com.fasterxml.jackson.databind;
}
```

**Практический совет:** при первой попытке модуляризации Spring Boot приложения используйте `open module` — откроет всё для рефлексии. Затем постепенно заменяйте на точечные `opens`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q38. Стратегии миграции legacy-кода: `Bottom-Up` vs `Top-Down` в деталях ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Миграция на JPMS — итеративный процесс. Выбор стратегии зависит от структуры проекта.

**Bottom-Up (снизу вверх) — рекомендуемая:**

Начинаем с листьев графа зависимостей — модулей без зависимостей на другой свой код.

```mermaid
graph BT
    A["utils.jar → Named module<br/>(шаг 1)"] --> B["service.jar → Named module<br/>(шаг 2)"]
    B --> C["app.jar → Named module<br/>(шаг 3)"]
    D["external-lib.jar<br/>Automatic module"] --> B
```

```java
// Шаг 1: utils — нет зависимостей на свой код
module com.example.utils {
    exports com.example.utils;
}

// Шаг 2: service
module com.example.service {
    requires com.example.utils;   // named module
    requires external.lib;        // automatic module (временно)
    exports com.example.service.api;
}

// Шаг 3: app
module com.example.app {
    requires com.example.service;
    requires com.example.utils;
    exports com.example.app;
}
```

**Top-Down (сверху вниз) — для быстрой видимости границ:**

Начинаем с приложения верхнего уровня, зависимости временно как `automatic modules`.

```java
module com.example.app {
    requires com.example.service; // пока automatic module
    requires com.example.utils;   // пока automatic module
    exports com.example.app;
}
// Затем постепенно добавляем module-info в service, utils
```

**Практический чеклист:**

```bash
# 1. Анализировать зависимости
jdeps --module-path lib --multi-release 17 -s app.jar

# 2. Найти использование internal JDK API
jdeps --jdk-internals --multi-release 17 app.jar

# 3. Найти split packages
jdeps --module-path lib --check com.example.app

# 4. Проверить наличие Automatic-Module-Name в сторонних JAR
jar --describe-module --file=lib/external.jar

# 5. Добавить module-info.java (начать с листьев)
# 6. Добавить --add-opens для фреймворков (временно)
# 7. Запустить тесты в модульном режиме
# 8. Постепенно убирать --add-opens, добавляя opens в module-info
```

**Когда остановиться на classpath:** если проект использует много legacy-библиотек без `Automatic-Module-Name` или активно использует `sun.*` API, полная модуляризация может не стоить затрат. Компромисс: использовать `jlink` с `--add-modules` для оптимизации Docker-образов без полной модуляризации кода приложения.

---

## See also

- [Java Core](java-core-interview.md) — базовые концепции языка, `ClassLoader`, рефлексия и инкапсуляция
- [Java 17-21](java-17-21-interview.md) — `sealed classes`, `records`, `virtual threads` — фичи, тесно связанные с модульностью
- [Java Annotations](java-annotations-interview.md) — аннотации и Annotation Processing в модульном контексте
- [Java IO / NIO](java-io-nio-interview.md) — `ServiceLoader` для плагинов ввода-вывода, модульные провайдеры
- [JVM](../../jvm/jvm-interview.md) — загрузка классов, `ClassLoader` иерархия, `InaccessibleObjectException` при рефлексии
- [Паттерны проектирования](../../design-patterns/design-patterns-interview.md) — `Service Locator`, `Plugin Pattern` через `ServiceLoader`
- [Spring Framework](../../frameworks/spring/spring-framework-interview.md) — Spring и `JPMS`: совместимость, `opens` для рефлексии Spring


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Java 17-21](java-17-21-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Java 8](java-8-interview.md)
- [Java Annotations](java-annotations-interview.md)
- [Java Collections](java-collections-interview.md)
- [Java Concurrency](java-concurrency-interview.md)
- [Java Conditional Statements](java-conditional-statements-interview.md)
