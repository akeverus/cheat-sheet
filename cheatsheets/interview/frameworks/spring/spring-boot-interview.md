---
title: "Вопросы на собеседовании: Spring Boot"
description: "Краткие ответы по Spring Boot: стартеры, автоконфигурация, профили, Actuator, развёртывание."
tags: ["interview", "frameworks", "spring-boot-interview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вопросы на собеседовании: `Spring Boot`

Краткие ответы по `Spring Boot`: стартеры, автоконфигурация, профили, `Actuator`, развёртывание.

Дата последнего обновления: 2026-02-04

Краткое введение: `Spring Boot` — стандарт для быстрого запуска `Java`-приложений. Вопросы по стартерам, автоконфигурации, профилям и развёртыванию часто задают на собеседованиях на позиции `Java / Senior Developer`.

## Полезные ссылки

### Официальная документация

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Boot Guides](https://spring.io/guides)

### См. также

- [Spring Framework Interview](spring-framework-interview.md) — вопросы по Spring Framework
- [Spring Cloud Interview](spring-cloud-interview.md) — вопросы по Spring Cloud
- [Spring Boot](../../../frameworks/java-frameworks/spring/spring-boot.md) — руководство по Spring Boot

## Содержание

- [Полезные ссылки](#полезные-ссылки)

**Основы Spring Boot**
- [Q1. (!) Что такое Spring Boot?](#q1-важно-что-такое-spring-boot)
- [Q2. (!) В чем разница между Spring и Spring Boot?](#q2-важно-в-чем-разница-между-spring-и-spring-boot)
- [Q3. Как настроить Spring Boot с помощью Maven?](#q3-как-настроить-spring-boot-с-помощью-maven)
- [Q4. Что такое Spring Initializr?](#q4-что-такое-spring-initializr)

**Starters и автоконфигурация**
- [Q5. (!) Что такое Starters?](#q5-важно-что-такое-starters)
- [Q6. (!) Какие наиболее популярные Starters?](#q6-важно-какие-наиболее-популярные-starters)
- [Q7. (!) Как создать свой Starter в Spring Boot?](#q7-важно-как-создать-свой-starter-в-spring-boot)
- [Q8. Как отключить конкретную автоматическую конфигурацию?](#q8-как-отключить-конкретную-автоматическую-конфигурацию)
- [Q9. (!) Как зарегистрировать пользовательскую автоконфигурацию?](#q9-важно-как-зарегистрировать-пользовательскую-автоконфигурацию)

**Развёртывание и DevTools**
- [Q10. Как развернуть Spring Boot в виде файлов JAR и WAR?](#q10-как-развернуть-spring-boot-в-виде-файлов-jar-и-war)
- [Q11. (!) Как использовать Spring Boot как приложение командной строки?](#q11-важно-как-использовать-spring-boot-как-приложение-командной-строки)
- [Q12. (!) Что такое Loose Coupling (Расслабленное связывание)?](#q12-важно-что-такое-loose-coupling-расслабленное-связывание)
- [Q13. (!) Что такое Spring Boot DevTools?](#q13-важно-что-такое-spring-boot-devtools)
- [Q14. (!) Что такое Spring Boot Actuator?](#q14-важно-что-такое-spring-boot-actuator)

**Конфигурация и профили**
- [Q15. Какие основные аннотации предлагает Spring Boot?](#q15-какие-основные-аннотации-предлагает-spring-boot)
- [Q16. Как изменить порт по умолчанию в Spring Boot?](#q16-как-изменить-порт-по-умолчанию-в-spring-boot)
- [Q17. Какие встроенные серверы поддерживает Spring Boot?](#q17-какие-встроенные-серверы-поддерживает-spring-boot)
- [Q18. Что такое application.properties и application.yml?](#q18-что-такое-applicationproperties-и-applicationyml)
- [Q19. Как использовать профили (profiles) в Spring Boot?](#q19-как-использовать-профили-profiles-в-spring-boot)

**Actuator, мониторинг и Docker**
- [Q20. Что такое Spring Boot Actuator endpoints?](#q20-что-такое-spring-boot-actuator-endpoints)
- [Q21. Как настроить логирование в Spring Boot?](#q21-как-настроить-логирование-в-spring-boot)
- [Q22. Как упаковать Spring Boot в Docker образ?](#q22-как-упаковать-spring-boot-в-docker-образ)
- [Q23. Что такое внешняя конфигурация (Externalized Configuration)?](#q23-что-такое-внешняя-конфигурация-externalized-configuration)
- [Q24. Как настроить health check для Kubernetes?](#q24-как-настроить-health-check-для-kubernetes)

**Продвинутые темы**
- [Q25. Что такое Spring Boot auto-configuration и как она работает?](#q25-что-такое-spring-boot-auto-configuration-и-как-она-работает)
- [Q26. Как отключить автоконфигурацию для тестов?](#q26-как-отключить-автоконфигурацию-для-тестов)
- [Q27. Как использовать @ConfigurationProperties?](#q27-как-использовать-configurationproperties)
- [Q28. Что такое Spring Boot executable JAR и как он устроен?](#q28-что-такое-spring-boot-executable-jar-и-как-он-устроен)
- [Q29. Как настроить мониторинг и метрики (Micrometer)?](#q29-как-настроить-мониторинг-и-метрики-micrometer)
- [Q30. Как мигрировать с Spring на Spring Boot?](#q30-как-мигрировать-с-spring-на-spring-boot)

## Q1. (!) Что такое `Spring Boot`?

Фреймворк для быстрого создания самостоятельных приложений на `Spring`: встроенный сервер (`Tomcat` / `Jetty` / `Undertow`), автоконфигурация по зависимостям, стартеры (starter-*), минимум конфигурации. Цель — сократить настройку и упростить развёртывание. Точка входа — класс с `@SpringBootApplication` и `SpringApplication.run()`. Даёт умные настройки по умолчанию, встроенные серверы, автоконфигурацию по класспасу, удобное тестирование и `Actuator` для мониторинга.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q2. (!) В чем разница между `Spring` и `Spring Boot`?

`Spring` — ядро (`DI`, `AOP`, `MVC` и т.д.) с явной конфигурацией (`XML` или `Java`-конфиг). `Spring Boot` — надстройка: автоконфигурация по зависимостям и конвенциям, встроенный сервер (`Tomcat` / `Jetty` / `Undertow`), стартеры для типовых сценариев, минимум ручной настройки. В `Spring` нужно самому поднимать сервер и собирать зависимости; в `Spring Boot` достаточно добавить starter и запустить приложение. `Spring Boot` не заменяет `Spring`, а упрощает его использование.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q3. Как настроить `Spring Boot` с помощью `Maven`?

Мы можем включить `Spring Boot` в проект `Maven`, как и любую другую библиотеку. Однако лучший способ — наследоваться от проекта `spring-boot-starter-parent` и объявить зависимости от `Spring Boot` starters. Это позволяет нашему проекту повторно использовать настройки `Spring Boot` по умолчанию.

Наследовать проект `spring-boot-starter-parent` очень просто — нам нужно только указать родительский элемент в `pom.xml`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0.RELEASE</version>
</parent>
```

Использование начального родительского проекта удобно, но не всегда возможно. Например, если наша компания требует, чтобы все проекты наследуются от стандартного `POM`, мы все равно можем извлечь выгоду из управления зависимостями `Spring Boot` с помощью пользовательского parent.

## Q4. Что такое `Spring Initializr`?

`Spring Initializr` — это удобный способ создать проект `Spring Boot`.

Мы можем перейти на сайт `Spring Initializr`, выбрать инструмент управления зависимостями (`Maven` или `Gradle`), язык (`Java`, `Kotlin` или `Groovy`), схему упаковки (Jar или War), версию и зависимости и загрузить проект.

Это создает для нас каркас проекта и экономит время на настройку, поэтому мы можем сосредоточиться на добавлении бизнес-логики.

Даже когда мы используем мастер создания нового проекта нашей `IDE` (например, `STS` или `Eclipse` с плагином `STS`) для создания проекта `Spring Boot`, под капотом он использует `Spring Initializr`.

## Q5. (!) Что такое `Starters`?

**Starters** — готовые наборы зависимостей под типовые задачи (веб, `JPA`, `Security`, тесты и т.д.). Один артефакт (например, `spring-boot-starter-web`) подтягивает совместимые библиотеки и часто включает автоконфигурацию. Не нужно вручную подбирать версии. Пример: `implementation 'org.springframework.boot:spring-boot-starter-data-jpa'` — подтягивает `JPA`, `Hibernate` и настройки по умолчанию.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q6. (!) Какие наиболее популярные `Starters`?

Наиболее распространённые: `spring-boot-starter-web` (`Spring MVC`, встроенный `Tomcat`), `spring-boot-starter-data-jpa` (`JPA` и БД), `spring-boot-starter-security` (аутентификация и авторизация), `spring-boot-starter-test` (JUnit, `MockMvc` и др.), `spring-boot-starter-data-rest` (`REST`-репозитории), `spring-boot-starter-thymeleaf` (шаблоны `Thymeleaf`). Есть также `starter-jdbc`, `starter-actuator`, `starter-mail`, `starter-data-elasticsearch` и др.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q7. (!) Как создать свой `Starter` в `Spring Boot`?

Отдельный `Maven / Gradle`-модуль с нужными зависимостями и классом `@Configuration` (автоконфигурация). Регистрация в `META-INF`: ключ `org.springframework.boot.autoconfigure.EnableAutoConfiguration`, значение — полное имя класса автоконфигурации. Собирают `JAR` и подключают как зависимость в другие проекты; при наличии starter на класспате бины создаются по условиям (`@ConditionalOnClass` и т.д.).

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q8. Как отключить конкретную автоматическую конфигурацию?

Если мы хотим отключить определённую автоконфигурацию, мы можем указать это с помощью атрибута exclude аннотации `@EnableAutoConfiguration`.

Например, этот фрагмент кода нейтрализует `DataSourceAutoConfiguration`:

```java
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class MyConfiguration {}
```

Если бы мы включили автоматическую настройку с помощью аннотации `@SpringBootApplication`, которая имеет `@EnableAutoConfiguration` в качестве мета-аннотации, мы могли бы отключить автоматическую настройку с помощью атрибута с тем же именем:

```java
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MyConfiguration {}
```

Мы также можем отключить автоматическую настройку с помощью свойства среды `spring.autoconfigure.exclude`. Этот параметр в файле `application.properties` делает то же самое, что и раньше:

```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

## Q9. (!) Как зарегистрировать пользовательскую автоконфигурацию?

Чтобы зарегистрировать класс автоконфигурации, его полное имя должно быть указано в ключе `EnableAutoConfiguration` в файле `META-INF/spring.factories`:

`org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.baeldung.autoconfigure.CustomAutoConfiguration`

При сборке проекта с помощью `Maven` каталог `META-INF` оказывается в нужном месте на этапе пакета.

## Q10. Как развернуть `Spring Boot` в виде файлов `JAR` и `WAR`?

Традиционно мы упаковываем веб-приложение в виде файла `WAR`, а затем развертываем его на внешнем сервере. Это позволяет нам размещать несколько приложений на одном сервере. Когда ЦП и памяти не хватало, это был отличный способ сэкономить ресурсы.

Но все изменилось. Компьютерное оборудование сейчас довольно дешевое, и внимание было обращено на конфигурацию сервера. Небольшая ошибка в настройке сервера при развертывании может привести к катастрофическим последствиям. `Spring` решает эту проблему, предоставляя подключаемый модуль, а именно `spring-boot-maven-plugin`, для упаковки веб-приложения в виде исполняемого файла `JAR`.

Чтобы включить этот плагин, просто добавьте элемент плагина в `pom.xml`:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

С этим плагином мы получим толстый `JAR`-файл после выполнения этапа пакета. Этот `JAR` содержит все необходимые зависимости, включая встроенный сервер. Таким образом, нам больше не нужно беспокоиться о настройке внешнего сервера.

Затем мы можем запустить приложение так же, как обычный исполняемый файл `JAR`.

Обратите внимание, что элемент упаковки в файле `pom.xml` должен иметь значение jar для создания файла `JAR`:

```xml
<packaging>jar</packaging>
```

Если мы не включим этот элемент, по умолчанию он также будет jar.

Чтобы создать файл `WAR`, мы меняем элемент упаковки на war:

```xml
<packaging>war</packaging>
```

и оставьте зависимость контейнера от упакованного файла:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

После выполнения фазы пакета `Maven` у нас будет развертываемый файл `WAR`.

## Q11. (!) Как использовать `Spring Boot` как приложение командной строки?

Как и любая другая программа `Java`, приложение командной строки `Spring Boot` должно иметь метод main.

Этот метод служит точкой входа, которая вызывает метод `SpringApplication.run()` для начальной загрузки приложения:

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class);
    }
}
```

Затем класс `SpringApplication` запускает `Container Spring` и автоматически настраивает bean-компоненты.

Обратите внимание, что мы должны передать класс конфигурации в метод run(), чтобы он работал в качестве основного источника конфигурации. По соглашению этот аргумент является самим входным классом.

После вызова метода run мы можем выполнять другие операторы, как в обычной программе.

## Q12. (!) Что такое `Loose Coupling` (Расслабленное связывание)?

`Loose Coupling` (Расслабленное связывание) в `Spring Boot` означает, что приложение может быть гибко настроено и конфигурировано без прямой зависимости от конкретной реализации или компонента.

В контексте нейминга свойств среды (environment property naming) в `Spring Boot`, речь идет о правильном и согласованном именовании свойств, которые используются для настройки приложения в различных окружениях (например, разработка, тестирование, продакшн).

В `Spring Boot`, свойства среды могут быть определены и управляться через различные источники конфигурации, такие как файлы `application.properties` (или `application.yml`), переменные среды, системные свойства, а также специальные файлы, такие как `bootstrap.properties` (или `bootstrap.yml`).

Важно соблюдать следующие рекомендации при именовании свойств среды:

1. Для свойств, которые относятся к определенному модулю или компоненту, рекомендуется начинать имя свойства с префикса, чтобы указать на принадлежность к определенной области. Например, `myapp.database.url` для `URL` базы данных или `myapp.email.host` для хоста электронной почты.
2. Принято использовать точку или дефис для разделения слов в именах свойств, чтобы сделать их более читабельными. Например, `myapp.database.url` или `myapp.email.username`.
3. Свойства среды должны быть записаны строчными буквами.
4. Для свойств, которые экспортируются как переменные среды, рекомендуется использовать большие буквы и подчеркивания вместо дефисов или точек. Например, MYAPP_DATABASE_URL или MYAPP_EMAIL_USERNAME.

Соблюдение этих рекомендаций поможет создать четкую и согласованную структуру именования свойств среды, что облегчит управление настройками и конфигурацией приложения в разных окружениях.

## Q13. (!) Что такое `Spring Boot DevTools`?

`Spring Boot DevTools` — это модуль в `Spring Boot`, который предоставляет набор инструментов для разработки приложений. Он облегчает процесс разработки, ускоряет время повторной сборки и автоматически перезапускает приложение при обнаружении изменений.

Некоторые из основных функций и возможностей, предоставляемых `Spring Boot DevTools`, включают:

1. Автоматическая перезагрузка: `DevTools` отслеживает изменения в исходных файлах и ресурсах и автоматически перезапускает приложение. Это позволяет быстро видеть изменения в коде без необходимости ручной перезагрузки сервера.
2. Горячая перезагрузка: Для некоторых изменений, таких как изменение шаблонов `HTML` или статических ресурсов, `DevTools` предоставляет возможность горячей перезагрузки. Это означает, что вместо полной перезагрузки приложения только измененные ресурсы будут обновлены.
3. Глобальные настройки: `DevTools` позволяет настраивать поведение перезагрузки и горячей перезагрузки через файлы `application.properties` или `application.yml`. Можно включить или отключить перезагрузку, настроить, какие файлы будут отслеживаться, и многое другое.
4. Отключение определенных `DevTools`: В некоторых сценариях разработки может потребоваться отключить `DevTools`. Это можно сделать, добавив `spring.`devtools.restart.enabled`=false` в файл `application.properties` или `application.yml`.

`Spring Boot DevTools` является очень полезным модулем для увеличения производительности и эффективности разработки приложений на основе `Spring Boot`. Он упрощает процесс разработки, позволяя быстро видеть изменения и автоматически перезапускать приложение.

## Q14. (!) Что такое `Spring Boot Actuator`?

`Spring Boot Actuator` — это модуль в `Spring Boot`, предоставляющий различные функции мониторинга и управления производительностью приложений. Он позволяет получить информацию о работе приложения в режиме реального времени, а также выполнять операции управления, такие как перезагрузка приложения или сбор информации о потоках выполнения.

Функции и возможности, предоставляемые `Spring Boot Actuator`, включают:

1. **/actuator/health** — проверка состояния приложения и отображение статуса его здоровья.
2. **/actuator/info** — отображение информации о версии и метаданных приложения.
3. **/actuator/metrics** — отображение метрик производительности приложения, таких как количество `HTTP`-запросов, использование памяти и другие пользовательские метрики.
4. **/actuator/env** — отображение информации о переменных среды, например, настройках конфигурации.
5. **/actuator/loggers** — управление уровнями журналирования и настройкой логирования во время выполнения.
6. **/actuator/shutdown** — отключение приложения, чтобы оно могло быть легко остановлено или перезапущено.
7. **/actuator/httptrace** — отображение трассировки запросов `HTTP`, отслеживание времени выполнения и составление отчетов о работе `MVC` контроллеров.

`Spring Boot Actuator` также позволяет расширять и настраивать функции мониторинга и управления путем добавления собственных метрик, конечных точек и интерфейсов управления.

В целом, `Spring Boot Actuator` обеспечивает удобный способ мониторить и управлять приложениями, что помогает быстро обнаруживать и устранять проблемы производительности и повышать эффективность работы приложений на основе `Spring Boot`.

## Q15. Какие основные аннотации предлагает `Spring Boot`?

Основные аннотации, которые предлагает `Spring Boot`, находятся в пакете `org.springframework.boot.autoconfigure` и его подпакетах. Вот пара основных:

1. `@EnableAutoConfiguration` — заставить `Spring Boot` искать bean-компоненты автоконфигурации в своем пути к классам и автоматически применять их.
2. `@SpringBootApplication` — для обозначения основного класса загрузочного приложения. Эта аннотация объединяет аннотации `@Configuration`, `@EnableAutoConfiguration` и `@ComponentScan` с их атрибутами по умолчанию.

## Q16. Как изменить порт по умолчанию в `Spring Boot`?

Мы можем изменить порт по умолчанию сервера, встроенного в `Spring Boot`, одним из следующих способов:

1. Использование файла свойств. Мы можем определить это в файле `application.properties` (или `application.yml`), используя свойство `server.port`.
2. Программно в нашем основном классе `@SpringBootApplication` мы можем установить `server.port` для экземпляра `SpringApplication`.
3. Использование командной строки. При запуске приложения в виде файла jar мы можем установить `server.port` в качестве аргумента команды java:

`java -jar -Dserver.port=8081 myspringproject.jar`

## Q17. Какие встроенные серверы поддерживает `Spring Boot`?

**Краткий ответ:** `Spring Boot` поддерживает три встроенных сервера: **Tomcat** (по умолчанию в `starter-web`), **Jetty**, **Undertow**. Смена сервера — исключить `Tomcat` из `starter-web` и добавить `spring-boot-starter-jetty` или `spring-boot-starter-undertow`. `Undertow` часто выбирают для меньшего потребления памяти.

`Spring Boot` поддерживает три встроенных сервера приложений:

1. **Tomcat** (по умолчанию) — `spring-boot-starter-web` подтягивает `Tomcat`; настройка порта и контекста через `server.port`, `server.servlet.context-path`.
2. **Jetty** — исключить `Tomcat` и добавить `spring-boot-starter-jetty`; подходит для легковесных и реактивных сценариев.
3. **Undertow** — исключить `Tomcat` и добавить `spring-boot-starter-undertow`; низкое потребление памяти, хорошая производительность.

Выбор сервера влияет на метрики и настройки (пулы потоков, размер буферов). Для смены на `Jetty / Undertow` в `pom.xml` исключают `spring-boot-starter-tomcat` из `spring-boot-starter-web` и подключают соответствующий starter.

### Конфигурация встроенного сервера

```java
@Configuration
public class ServerConfiguration {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.setPort(8080);
        factory.setContextPath("/api");
        return factory;
    }
}
```

## Q18. Что такое `application.properties` и `application.yml`?

**application.properties** и **application.yml** — файлы конфигурации `Spring Boot` по умолчанию в `src/main/resources`. Свойства подставляются в **@Value**, **@ConfigurationProperties** и встроенные компоненты. Порядок загрузки: встроенные default → application.* → профиль (`application-dev.yml`) → переменные окружения (высший приоритет).

Иерархия в yml: `server: port: 8080` вместо `server.port=8080`. Списки: `spring.profiles.include: [a,b]` или многострочный yml. Переменные окружения имеют приоритет над файлом — в контейнерах часто задают DATABASE_URL, SPRING_PROFILES_ACTIVE через env. Специфичные для профиля файлы: `application-prod.yml` загружается при `spring.profiles.active=prod`.

**Пример (`application.yml`):**
```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:postgresql://localhost/app
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
```
Внешний конфиг: файл `application.properties`/`application.yml` рядом с jar или переменные окружения переопределяют значения из jar.

## Q19. Как использовать профили (profiles) в `Spring Boot`?

**Профили** позволяют включать разную конфигурацию по окружению. **`@Profile`("dev")** на конфигурации или `Bean` — активен только при профиле dev. Активация: `spring.profiles.active=dev` в конфиге, `--spring.profiles.active=prod` в командной строке, переменная **SPRING_PROFILES_ACTIVE**. Файлы `application-dev.yml` загружаются при active=dev. Группы: `spring.profiles.group.prod=db,live`. Для тестов: **`@ActiveProfiles`("test")**.

**Пример:** `@Configuration @Profile("dev")` — `Bean` загружается только при dev. В `application-dev.yml` задают `logging.level.root: DEBUG`, локальный URL БД. Запуск с профилем: `java -jar app.jar --spring.profiles.active=prod` или `SPRING_PROFILES_ACTIVE=prod`. Группа: `spring.profiles.group.prod=db,live` — при active=prod подтягиваются и db, и live.

## Q20. Что такое `Spring Boot Actuator` endpoints?

**Actuator** — эндпоинты для мониторинга и управления приложением: **/actuator/health** (состояние, readiness/liveness), **/actuator/info**, **/actuator/metrics**, **/actuator/env** и др. Включение: зависимость `spring-boot-starter-actuator`; **management.endpoints.web.exposure.include**=health,info (в prod не открывать env, shutdown без защиты).

Кастомные индикаторы — класс, реализующий **HealthIndicator**, метод `health()` возвращает `Health.up()` или `Health.down()`. Регистрация как `Bean` — индикатор автоматически включается в /actuator/health.

**Настройка (`application.yml`):** `management.endpoints.web.exposure.include: health,info,metrics`. Кастомный индикатор: класс, реализующий `HealthIndicator`, метод `health()` возвращает `Health.up()` или `Health.down().withDetail(...)`. Регистрация как `Bean` — индикатор автоматически включается в `/actuator/health`. В prod не открывать env и shutdown без защиты.

## Q21. Как настроить логирование в `Spring Boot`?

По умолчанию **Logback**; конфигурация через **application.yml** (`logging.level.root`, `logging.`level.com.example`=DEBUG`) или **logback-spring.xml**. Вывод в файл: `logging.file.name` или `logging.file.path`. В контейнерах логи в stdout; уровень по профилю (dev — `DEBUG`, prod — `INFO`).

Замена на `Log4j2`: исключить `spring-boot-starter-logging`, добавить `spring-boot-starter-log4j2`. В prod уровень root — `INFO`, пакет приложения — `WARN` или `INFO`; в dev — `DEBUG` для своего пакета.

**Практика (`application.yml`):**
```yaml
logging:
  level:
    root: INFO
    com.example: DEBUG
    org.springframework.web: INFO
  file:
    name: logs/app.log
  pattern:
    console: "%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n"
```
В prod уровень root — `INFO`, пакет приложения — `WARN` или `INFO`; в dev — `DEBUG` для своего пакета. В `Kubernetes` логи в stdout, сбор — `DaemonSet` (`Fluentd`, `Filebeat`) или sidecar.

## Q22. Как упаковать `Spring Boot` в `Docker` образ?

**Multi-stage сборка:** этап 1 — Maven или Gradle собирает приложение (например, `app.jar`); этап 2 — образ только с JRE и jar. Оптимизация: `spring-boot-maven-plugin` с **layers** — в образ копировать слои для кэширования зависимостей. Не запускать от root; создать непривилегированного пользователя (`RUN adduser -D appuser`, `USER appuser`).

**Пример `Dockerfile` (`multi-stage`):**
```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY mvnw ./
COPY .mvn ./
COPY pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN adduser -D appuser
COPY --from=build /app/target/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
```
Сборка с layers: в `pom.xml spring-`boot-maven`-plugin` с `layers`; при сборке создаётся `layers.idx` и каталоги слоёв; в `Docker` копируем слои — кэш слоёв переиспользуется при изменении только кода приложения.

## Q23. Что такое внешняя конфигурация (`Externalized Configuration`)?

**Externalized Configuration** — задание свойств вне кода: **application.properties** вне jar, **переменные окружения**, системные свойства, **config/application.properties** рядом с jar. Порядок приоритета (от высшего): командная строка → переменные окружения → config/* → classpath. Имена свойств из env: SPRING_DATASOURCE_URL (точка → подчёркивание, верхний регистр).

Секреты — не в репозитории; использовать переменные окружения или внешнее хранилище (`Vault`, облако). В `Kubernetes` — `ConfigMap` для несекретных настроек, `Secrets` для паролей.

**Практика:** в `Kubernetes` — `ConfigMap` для несекретных настроек, `Secrets` для паролей и ключей; монтирование в под как файлы или переменные окружения. В `application.yml` задать значения по умолчанию; переопределение через `SPRING_DATASOURCE_URL`, `SPRING_PROFILES_ACTIVE` и т.д. Для секретов — не писать в Git; при локальном запуске — `application-local.yml` в `.gitignore` или переменные окружения.

## Q24. Как настроить health check для `Kubernetes`?

В **Deployment** задают **livenessProbe** и **readinessProbe** на **/actuator/health/liveness** и **/actuator/health/readiness** (`Spring Boot` 2.3+). В `application.yml`: `management.endpoint.health.probes.enabled=true`. **Readiness** — приложение готово принимать трафик (под исключается из `Service` при провале); **liveness** — приложение живо (`Kubernetes` перезапускает под при провале).

Настроить **initialDelaySeconds** (ждать перед первой проверкой), **periodSeconds** (интервал). Пример: `livenessProbe`/`readinessProbe` в манифесте с `initialDelaySeconds: 30`, `periodSeconds: 10`.

```yaml
# deployment fragment
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 15
```
Кастомные индикаторы: реализовать `HealthIndicator`, зарегистрировать `Bean / Readiness` может быть `DOWN` до подключения к БД; после успешного старта — `UP`, под получает трафик.

## Q25. Что такое `Spring Boot auto-configuration` и как она работает?

**Auto-configuration** — условная регистрация бинов на основе classpath и **@Conditional** (**ConditionalOnClass**, **ConditionalOnProperty**, **ConditionalOnMissingBean**). **@EnableAutoConfiguration** (включено через `@SpringBootApplication`) сканирует `META-INF/spring.factories` (или `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` в новых версиях) и условно регистрирует `Bean`. При наличии в classpath зависимостей (например, `DataSource`) подставляется конфигурация по умолчанию.

Отключение: **`@SpringBootApplication`(exclude = {DataSourceAutoConfiguration.class})** или **spring.autoconfigure.exclude** в properties. Для тестов часто отключают автоконфигурацию БД или безопасности.

## Q26. Как отключить автоконфигурацию для тестов?

**@SpringBootTest** загружает полный контекст. Отключить конкретную автоконфигурацию: **`@SpringBootTest`(`excludeAutoConfiguration` = DataSourceAutoConfiguration.class)** или **`@EnableAutoConfiguration`(exclude = {...})** в тестовом конфиге. **@AutoConfigureTestDatabase** — замена БД на `H2`. **@MockBean** — подмена бинов.

Для срезов: **@WebMvcTest** (только `MVC`), **@DataJpaTest** (только `JPA`, встроенная БД) — загружается только нужный слой, остальная автоконфигурация не поднимается.

## Q27. Как использовать `@ConfigurationProperties`?

Класс с полями и геттерами/сеттерами, аннотированный **`@ConfigurationProperties`(prefix = "app")**, биндится к префиксу **app.*** в `application.yml`; в конфиге — **`@EnableConfigurationProperties`(MyProps.class)** или **@ConfigurationPropertiesScan**. Валидация: **@Validated** и **@NotNull**, **@Size** на полях.

Типобезопасный доступ к вложенным свойствам; **spring-boot-configuration-processor** генерирует метаданные для подсказок в `IDE`. Пример: `app.server.port`, `app.feature.enabled` в yml → поля `server.port`, `feature.enabled` в классе с prefix = "app".

## Q28. Что такое `Spring Boot` executable `JAR` и как он устроен?

**Executable JAR** (fat `JAR`) — один jar со всеми зависимостями в **BOOT-INF**; **Spring Boot** (**JarLauncher**) читает **BOOT-INF/Main-Class** из манифеста.

Вложенные jar распаковываются при старте во временный каталог. **spring-boot-maven-plugin** (repackage) создаёт такой jar. С **layers** образ разбит на слои (dependencies, `spring-boot-loader`, `snapshot-dependencies`, application) для кэширования в `Docker`.

## Q29. Как настроить мониторинг и метрики (`Micrometer`)?

**Micrometer** — абстракция метрик; **spring-`boot-starter / Prometheus`, `Datadog` через зависимость **micrometer-registry-prometheus** и т.д. Свойства **management.`metrics.export`.*** для настройки реестра. Кастомные метрики: **MeterRegistry.counter()**, **timer()**, **gauge()**. Метки (tags) для разбиения по эндпоинтам, кодам ответа; кардинальность тегов ограничивать.

Зависимость `micrometer-registry-prometheus` + `management.endpoints.web.exposure.include=prometheus`. JVM-метрики (память, потоки, `GC`) регистрируются автоматически. Кастом: `@Autowired MeterRegistry registry; registry.counter("orders.created", "status", "new").increment();` Метки ограничивать по кардинальности (не `user_id` в тег при миллионах пользователей).

## Q30. Как мигрировать с `Spring` на `Spring Boot`?

Добавить **spring-boot-starter-parent** (или `BOM`), заменить конфигурацию XML на `application.yml`/`application.properties`, подключать `spring-boot-starter-*` вместо ручных зависимостей. Постепенно включать автоконфигурацию и удалять дублирующие `Bean`. Встроенный сервер (`Tomcat` / `Jetty` / `Undertow`) вместо внешнего.

Миграция по модулям; тесты и регрессии на каждом шаге. Документация `Spring Boot` по миграции. Типичные шаги: `BOM` → замена зависимостей → перенос конфига в `application.yml` → отключение лишней автоконфигурации при конфликтах.