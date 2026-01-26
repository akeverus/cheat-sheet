# Вопросы на собеседовании: Spring Boot

**Комплексное руководство по вопросам собеседования на тему Spring Boot для Senior Java Developer. Включает детальные объяснения концепций, практические примеры на Java + Spring, best practices и troubleshooting.**

**Дата последнего обновления:** 2026-01-25


## Полезные ссылки

### Официальная документация
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Boot Guides](https://spring.io/guides)

### См. также
- `../spring-framework-interview.md` - Вопросы по Spring Framework
- `../spring-cloud-interview.md` - Вопросы по Spring Cloud
- `../../frameworks/java-frameworks/spring/spring-boot.md` - Детальное руководство по Spring Boot

## Содержание

- [Q1. (ВАЖНО) - Что такое Spring Boot?](#q1-важно---что-такое-spring-boot)
- [Q2. (ВАЖНО) - В чем разница между Spring и Spring Boot?](#q2-важно---в-чем-разница-между-spring-и-spring-boot)
- [Q3. Как настроить Spring Boot с помощью Maven?](#q3-как-настроить-spring-boot-с-помощью-maven)
- [Q4. Что такое Spring Initializr?](#q4-что-такое-spring-initializr)
- [Q5. (ВАЖНО) - Что такое Starters?](#q5-важно---что-такое-starters)
- [Q6. (ВАЖНО) - Какие наиболее популярные Starters?](#q6-важно---какие-наиболее-популярные-starters)
- [Q7. (ВАЖНО) - Как создать свой Starter в Spring Boot?](#q7-важно---как-создать-свой-starter-в-spring-boot)
- [Q8. Как отключить конкретную автоматическую конфигурацию?](#q8-как-отключить-конкретную-автоматическую-конфигурацию)
- [Q9. (ВАЖНО) - Как зарегистрировать пользовательскую автоконфигурацию?](#q9-важно---как-зарегистрировать-пользовательскую-автоконфигурацию)
- [Q10. Как развернуть Spring Boot в виде файлов JAR и WAR?](#q10-как-развернуть-spring-boot-в-виде-файлов-jar-и-war)
- [Q11. (ВАЖНО) - Как использовать Spring Boot как приложение командной строки?](#q11-важно---как-использовать-spring-boot-как-приложение-командной-строки)
- [Q12. (ВАЖНО) - Что такое Loose Coupling (Расслабленное связывание)?](#q12-важно---что-такое-loose-coupling-расслабленное-связывание)
- [Q13. (ВАЖНО) - Что такое Spring Boot DevTools?](#q13-важно---что-такое-spring-boot-devtools)
- [Q14. (ВАЖНО) - Что такое Spring Boot Actuator?](#q14-важно---что-такое-spring-boot-actuator)
- [Q15. Какие основные аннотации предлагает Spring Boot?](#q15-какие-основные-аннотации-предлагает-spring-boot)
- [Q16. Как изменить порт по умолчанию в Spring Boot?](#q16-как-изменить-порт-по-умолчанию-в-spring-boot)
- [Q17. Какие встроенные серверы поддерживает Spring Boot?](#q17-какие-встроенные-серверы-поддерживает-spring-boot)

## Q1. (ВАЖНО) - Что такое Spring Boot?

### Введение

**Spring Boot** — это фреймворк, разработанный для упрощения и ускорения процесса создания самостоятельных, готовых к использованию приложений на базе Spring. Он предоставляет ряд удобных функций и автоматических настроек, чтобы сократить конфигурацию и упростить развертывание приложений.

### Детальное объяснение

Основные особенности **Spring Boot** включают:

1. Упрощенная конфигурация: Spring Boot предоставляет умные настройки по умолчанию для широкого спектра конфигурационных параметров, что позволяет разработчикам сосредоточиться на разработке функциональности вместо траты времени на настройку приложений.

2. Встраиваемые серверы приложений: Spring Boot интегрирует встраиваемые серверы приложений, такие как Tomcat, Jetty или Undertow, что упрощает процесс развертывания приложений без необходимости внешнего сервера.

3. Автоматическая конфигурация: Spring Boot использует принцип автоматической конфигурации, который позволяет приложению "разумно" определить зависимости и настройки, основываясь на используемых компонентах и библиотеках.

### Практический пример

```java
/**
 * Простое Spring Boot приложение
 * Минимальная конфигурация благодаря автоконфигурации
 */
@SpringBootApplication // Комбинирует @Configuration, @EnableAutoConfiguration, @ComponentScan
public class Application {
 
 public static void main(String[] args) {
 // Запуск Spring Boot приложения
 // SpringApplication автоматически настраивает встроенный Tomcat сервер
 SpringApplication.run(Application.class, args);
 }
}

/**
 * REST контроллер в Spring Boot
 * Автоматически регистрируется благодаря @RestController
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
 
 private final UserService userService;
 
 // Dependency Injection через конструктор
 public UserController(UserService userService) {
 this.userService = userService;
 }
 
 /**
 * GET endpoint - автоматически сериализует ответ в JSON
 */
 @GetMapping("/{id}")
 public ResponseEntity<User> getUser(@PathVariable Long id) {
 return userService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
 }
 
 /**
 * POST endpoint - автоматически десериализует JSON в объект
 */
 @PostMapping
 public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
 User user = userService.createUser(request);
 return ResponseEntity.status(HttpStatus.CREATED).body(user);
 }
}
```

Основные особенности **Spring Boot** включают:

1. **Упрощенная конфигурация**: **Spring Boot** предоставляет умные настройки по умолчанию для широкого спектра конфигурационных параметров, что позволяет разработчикам сосредоточиться на разработке функциональности вместо траты времени на настройку приложений.
2. **Встраиваемые серверы приложений**: **Spring Boot** интегрирует встраиваемые серверы приложений, такие как **Tomcat**, **Jetty** или **Undertow**, что упрощает процесс развертывания приложений без необходимости внешнего сервера.
3. **Автоматическая конфигурация**: **Spring Boot** использует принцип автоматической конфигурации, который позволяет приложению "разумно" определить зависимости и настройки, основываясь на используемых компонентах и библиотеках.
4. **Управление зависимостями**: **Spring Boot** упрощает управление зависимостями, позволяя использовать **Maven** или **Gradle** для автоматического разрешения и загрузки всех необходимых библиотек и компонентов.
5. **Удобное тестирование**: **Spring Boot** предоставляет удобные инструменты и аннотации для тестирования приложений, что упрощает создание модульных, интеграционных и функциональных тестов.
6. **Мониторинг и управление**: **Spring Boot** предоставляет актуаторы и инструменты управления и мониторинга приложений, такие как **Actuator**, который позволяет легко мониторить состояние приложения, собирать метрики и выявлять проблемы в производственной среде.
7. **Поддержка внешних систем**: **Spring Boot** предоставляет интеграцию с большим количеством внешних систем и сервисов, таких как базы данных, мессенджеры, кэш и другие, что упрощает интеграцию с существующими компонентами.

В целом, **Spring Boot** помогает разработчикам создавать приложения на базе **Spring** быстро, эффективно и с минимальными усилиями по конфигурации и настройке.



## Q2. (ВАЖНО) - В чем разница между Spring и Spring Boot?

**Spring** и **Spring Boot** — это две связанные технологии, но существуют некоторые ключевые различия:

1. **Конфигурация**: В **Spring** требуется явная конфигурация, где разработчику нужно определить бины, настройки, компоненты и многое другое, используя файлы **XML** (или аннотации). В то время как **Spring Boot** предоставляет автоматическую конфигурацию, которая настроит большую часть приложения автоматически, основываясь на конвенциях и наличии класса запуска.
2. **Зависимости**: В **Spring** требуется явно определить зависимости и управлять ими с помощью инструментов сборки, например, **Maven** или **Gradle**. В **Spring Boot** управление зависимостями происходит автоматически с использованием встроенных инструментов управления зависимостями, что упрощает разработку и развертывание приложений.
3. **Встраиваемые серверы**: **Spring** не имеет встроенного сервера приложений, поэтому требуется запускать приложение на стороннем сервере, таком как **Tomcat** или **Jetty**. В отличие от этого, **Spring Boot** включает встроенные серверы приложений (например, Tomcat, Jetty или Undertow), что позволяет разработчику запускать приложение без настройки отдельного сервера.
4. **Управление настройками**: **Spring** требует явной настройки всех конфигурационных параметров, включая имена бинов, пути к файлам и другие настройки. **Spring Boot** предлагает соглашения по конфигурированию, где основные настройки уже предопределены, и разработчику не обязательно указывать все настройки явно.
5. **Упрощение разработки**: **Spring Boot** стремится упростить процесс разработки, предоставляя умные настройки по умолчанию и автоматическую конфигурацию, что позволяет разработчикам сосредоточиться на бизнес-логике приложения вместо настройки инфраструктуры.

В целом, **Spring Boot** стремится предоставить удобные инструменты и соглашения по конфигурированию, чтобы упростить и ускорить создание приложений на базе **Spring**, в то время как **Spring** предоставляет расширяемый фреймворк для разработки приложений на **Java** со всеми его возможностями и гибкостью.



## Q3. Как настроить Spring Boot с помощью Maven?


Мы можем включить **Spring Boot** в проект **Maven**, как и любую другую библиотеку. Однако лучший способ — наследоваться от проекта **spring-boot-starter-parent** и объявить зависимости от **Spring Boot starters**. Это позволяет нашему проекту повторно использовать настройки **Spring Boot** по умолчанию.

Наследовать проект **spring-boot-starter-parent** очень просто — нам нужно только указать родительский элемент в **pom.xml**:

&lt;parent&gt;

&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;

&lt;artifactId&gt;spring-boot-starter-parent&lt;/artifactId&gt;

&lt;version&gt;2.4.0. RELEASE&lt;/version&gt;

&lt;/parent&gt;

Использование начального родительского проекта удобно, но не всегда возможно. Например, если наша компания требует, чтобы все проекты наследуются от стандартного **POM**, мы все равно можем извлечь выгоду из управления зависимостями **Spring Boot** с помощью пользовательского **parent**.



## Q4. Что такое Spring Initializr?

**Spring Initializr** — это удобный способ создать проект **Spring Boot**.

Мы можем перейти на сайт **Spring Initializr**, выбрать инструмент управления зависимостями (Maven или Gradle), язык (Java, Kotlin или Groovy), схему упаковки (Jar или War), версию и зависимости и загрузить проект.

Это создает для нас каркас проекта и экономит время на настройку, поэтому мы можем сосредоточиться на добавлении бизнес-логики.

Даже когда мы используем мастер создания нового проекта нашей **IDE** (например, STS или Eclipse с плагином STS) для создания проекта **Spring Boot**, под капотом он использует **Spring Initializr**.



## Q5. (ВАЖНО) - Что такое Starters?

**Starters** в **Spring Boot** представляют собой удобный способ управления зависимостями в проекте. **Starters** — это предопределенные наборы зависимостей, которые объединяются для поддержки конкретной функциональности или технологии в вашем приложении.

Когда вы создаете проект **Spring Boot**, вы можете выбрать один или несколько **Starters**, которые добавят в ваш проект все необходимые зависимости. **Starters** включают в себя не только библиотеки, но и плагины, конфигурации, а также предварительные настройки, чтобы упростить весь процесс.

Каждый **Starter** в **Spring Boot** имеет свое назначение, исходя из различных технологий, фреймворков или бизнес-задач, которые они поддерживают. Например, есть **Starters** для работы с базами данных, веб-разработки, авторизацией и многого другого.

Преимущество использования **Starters** заключается в том, что вам не нужно заботиться о том, какие зависимости включать или как их настроить. **Spring Boot** автоматически добавляет все необходимое и предоставляет удобные средства для настройки и использования этих библиотек.

Ниже приведен пример использования **Starter** для работы с базами данных **H2** и **JDBC:

dependencies {

// Добавляем Starter для работы с базами данных H2 и JDBC

implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

implementation 'org.springframework.boot:spring-boot-starter-data-rest'

implementation 'com.h2database:h2'

implementation 'org.apache.tomcat:tomcat-jdbc'

}

В этом примере мы добавляем **Starters** для работы с базами данных, **JPA** и **RESTful**-сервисами. **Spring Boot** автоматически подключит все необходимые зависимости для поддержки этих функций в нашем проекте.

Использование **Starters** в **Spring Boot** позволяет вам быстро и легко подключать нужные зависимости и сосредоточиться на разработке приложения, не тратя время на настройку и управление зависимостями.



## Q6. (ВАЖНО) - Какие наиболее популярные Starters?


В **Spring Boot** предоставляется множество **Starters** для различных технологий и фреймворков. Некоторые из наиболее распространенных **Starters** включают:

1. **spring-boot-starter-web**: Starter для создания веб-приложений с использованием **Spring MVC**.
2. **spring-boot-starter-data-jpa**: Starter для работы с **API Java Persistence (JPA)** и базами данных, поддерживаемыми **JPA**.
3. **spring-boot-starter-security**: Starter для добавления аутентификации и авторизации в приложение с использованием **Spring Security**.
4. **spring-boot-starter-test**: Starter для написания автоматических тестов в **Spring Boot** с использованием фреймворка **JUnit** и других инструментов.
5. **spring-boot-starter-data-rest**: Starter для создания **RESTful**-сервисов на основе **Spring Data REST**.
6. **spring-boot-starter-thymeleaf**: Starter для работы с шаблонами **Thymeleaf**, добавляющий поддержку серверного **HTML**-рендеринга.
7. **spring-boot-starter-jdbc**: Starter для работы с **JDBC (Java Database Connectivity)** и базами данных.
8. **spring-boot-starter-actuator**: Starter для добавления функциональности мониторинга и управления производительностью в ваше приложение.
9. **spring-boot-starter-mail**: Starter для отправки электронных писем с использованием **JavaMail API**.
10. **spring-boot-starter-data-elasticsearch**: Starter для работы с **Elasticsearch** и интеграции с **Spring Data**.

Это только некоторые из множества **Starters**, доступных в **Spring Boot**. Каждый **Starter** предоставляет набор зависимостей и настроек, которые упрощают подключение соответствующей технологии или фреймворка в вашем проекте. Вы также можете создавать собственные **Starters** для интеграции с другими самостоятельными модулями или библиотеками.



## Q7. (ВАЖНО) - Как создать свой Starter в Spring Boot?


Создание собственного **Starter** в **Spring Boot** включает несколько шагов:

1. Создайте новый проект Maven или Gradle. Убедитесь, что вы используете **Spring Boot** как родительский проект в файле `pom.xml` или `build.gradle`.
2. Определите зависимости, которые вы хотите включить в свой Starter. Обычно это набор библиотек, конфигураций и плагинов, связанных с определенной функциональностью или технологией. Убедитесь, что указываете наиболее подходящую версию каждой зависимости.
3. Создайте файл AutoConfiguration для настройки автоматической конфигурации вашего Starter. Этот файл должен быть помечен аннотацией **@Configuration** и содержать методы для настройки, инициализации и настройки зависимостей вашего **Starter**.
4. Зарегистрируйте вашу AutoConfiguration в файле `resources/META-INF/spring.factories`. Укажите полное квалифицированное имя класса вашей **AutoConfiguration** в качестве значения ключа `org.springframework.boot.autoconfigure.EnableAutoConfiguration`.
5. (Опционально) Создайте файл `src/main/resources/META-INF/spring.handlers`, если вы хотите поддерживать свой собственный **XML**-схему. Если ваш **Starter** не требует использования **XML**-схемы, этот шаг можно пропустить.
6. (Опционально) Создайте файл `src/main/resources/META-INF/spring.tooling`, если вы хотите предоставить поддержку в инструментарии **Spring Tool Suite**. Если ваш **Starter** не требует поддержки для инструментария, этот шаг можно пропустить.
7. Соберите ваш проект в JAR-файл, используя команду `mvn package` или `gradle build`. При этом будет сгенерирован **Starter JAR** со всеми зависимостями и настройками.
8. Подключите ваш **Starter JAR** в другие проекты **Spring Boot**, добавив его в зависимости в файле `pom.xml` или `build.gradle`.

Теперь ваш собственный **Starter** готов к использованию. При подключении **Starter JAR** в другом проекте **Spring Boot**, все зависимости и настройки, определенные в вашей **AutoConfiguration**, будут автоматически применяться и интегрироваться в проект.

Важно отметить, что создание собственного **Starter** в **Spring Boot** требует хорошего понимания фреймворка **Spring** и умения правильно настроить зависимости и реализацию. Документация **Spring Boot** содержит подробные примеры и руководства для создания собственных **Starter**.



## Q8. Как отключить конкретную автоматическую конфигурацию?


Если мы хотим отключить определенную автоконфигурацию, мы можем указать это с помощью атрибута **exclude** аннотации **@EnableAutoConfiguration.

Например, этот фрагмент кода нейтрализует **DataSourceAutoConfiguration:

@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)

public class MyConfiguration{}

Если бы мы включили автоматическую настройку с помощью аннотации `@SpringBootApplication`, которая имеет `@EnableAutoConfiguration` в качестве мета-аннотации, мы могли бы отключить автоматическую настройку с помощью атрибута с тем же именем:

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)

public class MyConfiguration{}

Мы также можем отключить автоматическую настройку с помощью свойства среды **spring.autoconfigure.exclude**. Этот параметр в файле **Application.properties** делает то же самое, что и раньше:

spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration



## Q9. (ВАЖНО) - Как зарегистрировать пользовательскую автоконфигурацию?


Чтобы зарегистрировать класс автоконфигурации, его полное имя должно быть указано в ключе **EnableAutoConfiguration** в файле **META-INF/spring.factories**:

org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.baeldung.autoconfigure.CustomAutoConfiguration

Если мы собираем проект с помощью **Maven**, этот файл должен быть помещен в каталог **resources/META-INF**, который окажется в указанном месте на этапе пакета.



## Q10. Как развернуть Spring Boot в виде файлов JAR и WAR?


Традиционно мы упаковываем веб-приложение в виде файла **WAR**, а затем развертываем его на внешнем сервере. Это позволяет нам размещать несколько приложений на одном сервере. Когда ЦП и памяти не хватало, это был отличный способ сэкономить ресурсы.

Но все изменилось. Компьютерное оборудование сейчас довольно дешевое, и внимание было обращено на конфигурацию сервера. Небольшая ошибка в настройке сервера при развертывании может привести к катастрофическим последствиям. **Spring** решает эту проблему, предоставляя подключаемый модуль, а именно `spring-boot-maven-plugin`, для упаковки веб-приложения в виде исполняемого файла **JAR**.

Чтобы включить этот плагин, просто добавьте элемент плагина в **pom.xml**:

&lt;plugin&gt;

&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;

&lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;

&lt;/plugin&gt;

С этим плагином мы получим толстый **JAR-**файл после выполнения этапа пакета. Этот **JAR** содержит все необходимые зависимости, включая встроенный сервер. Таким образом, нам больше не нужно беспокоиться о настройке внешнего сервера.

Затем мы можем запустить приложение так же, как обычный исполняемый файл **JAR**.

Обратите внимание, что элемент упаковки в файле **pom.xml** должен иметь значение **jar** для создания файла **JAR**:

&lt;packaging&gt;jar&lt;/packaging&gt;

Если мы не включим этот элемент, по умолчанию он также будет **jar**.

Чтобы создать файл **WAR**, мы меняем элемент упаковки на **war**:

&lt;packaging&gt;war&lt;/packaging&gt;

и оставьте зависимость контейнера от упакованного файла:

&lt;dependency&gt;

&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;

&lt;artifactId&gt;spring-boot-starter-tomcat&lt;/artifactId&gt;

&lt;scope&gt;provided&lt;/scope&gt;

&lt;/dependency&gt;

После выполнения фазы пакета **Maven** у нас будет развертываемый файл **WAR**.



## Q11. (ВАЖНО) - Как использовать Spring Boot как приложение командной строки?


Как и любая другая программа **Java**, приложение командной строки **Spring Boot** должно иметь метод **main**.

Этот метод служит точкой входа, которая вызывает метод **SpringApplication.run()** для начальной загрузки приложения:

@SpringBootApplication

public class MyApplication{

public static void main(String\[\] args) {

SpringApplication.run(MyApplication.class);

}

}

Затем класс **SpringApplication** запускает **Container Spring** и автоматически настраивает **bean-**компоненты.

Обратите внимание, что мы должны передать класс конфигурации в метод **run()**, чтобы он работал в качестве основного источника конфигурации. По соглашению этот аргумент является самим входным классом.

После вызова метода **run** мы можем выполнять другие операторы, как в обычной программе.



## Q12. (ВАЖНО) - Что такое Loose Coupling (Расслабленное связывание)?

**Loose Coupling (Расслабленное связывание)** в **Spring Boot** означает, что приложение может быть гибко настроено и конфигурировано без прямой зависимости от конкретной реализации или компонента.

В контексте нейминга свойств среды (environment property naming) в **Spring Boot**, речь идет о правильном и согласованном именовании свойств, которые используются для настройки приложения в различных окружениях (например, разработка, тестирование, продакшн).

В **Spring Boot**, свойства среды могут быть определены и управляться через различные источники конфигурации, такие как файлы `application.properties` (или `application.yml`), переменные среды, системные свойства, а также специальные файлы, такие как `bootstrap.properties` (или `bootstrap.yml`).

Важно соблюдать следующие рекомендации при именовании свойств среды:

1. Для свойств, которые относятся к определенному модулю или компоненту, рекомендуется начинать имя свойства с префикса, чтобы указать на принадлежность к определенной области. Например, **myapp.database.url** для **URL** базы данных или **myapp.email.host** для хоста электронной почты.
2. Принято использовать точку или дефис для разделения слов в именах свойств, чтобы сделать их более читабельными. Например, **myapp.database.url** или **myapp.email.username**.
3. Свойства среды должны быть записаны строчными буквами.
4. Для свойств, которые экспортируются как переменные среды, рекомендуется использовать большие буквы и подчеркивания вместо дефисов или точек. Например, **MYAPP_DATABASE_URL** или **MYAPP_EMAIL_USERNAME**.

Соблюдение этих рекомендаций поможет создать четкую и согласованную структуру именования свойств среды, что облегчит управление настройками и конфигурацией приложения в разных окружениях.



## Q13. (ВАЖНО) - Что такое Spring Boot DevTools?

**Spring Boot DevTools** — это модуль в **Spring Boot**, который предоставляет набор инструментов для разработки приложений. Он облегчает процесс разработки, ускоряет время повторной сборки и автоматически перезапускает приложение при обнаружении изменений.

Некоторые из основных функций и возможностей, предоставляемых **Spring Boot DevTools**, включают:

1. **Автоматическая перезагрузка**: **DevTools** отслеживает изменения в исходных файлах и ресурсах и автоматически перезапускает приложение. Это позволяет быстро видеть изменения в коде без необходимости ручной перезагрузки сервера.
2. **Горячая перезагрузка**: Для некоторых изменений, таких как изменение шаблонов **HTML** или статических ресурсов, **DevTools** предоставляет возможность горячей перезагрузки. Это означает, что вместо полной перезагрузки приложения только измененные ресурсы будут обновлены.
3. **Глобальные настройки**: **DevTools** позволяет настраивать поведение перезагрузки и горячей перезагрузки через файлы `application.properties` или `application.yml`. Можно включить или отключить перезагрузку, настроить, какие файлы будут отслеживаться, и многое другое.
4. **Отключение определенных DevTools**: В некоторых сценариях разработки может потребоваться отключить **DevTools**. Это можно сделать, добавив `spring.devtools.restart.enabled=false` в файл `application.properties` или `application.yml`.

**Spring Boot DevTools** является очень полезным модулем для увеличения производительности и эффективности разработки приложений на основе **Spring Boot**. Он упрощает процесс разработки, позволяя быстро видеть изменения и автоматически перезапускать приложение.



## Q14. (ВАЖНО) - Что такое Spring Boot Actuator?

**Spring Boot Actuator** — это модуль в **Spring Boot**, предоставляющий различные функции мониторинга и управления производительностью приложений. Он позволяет получить информацию о работе приложения в режиме реального времени, а также выполнять операции управления, такие как перезагрузка приложения или сбор информации о потоках выполнения.

Функции и возможности, предоставляемые **Spring Boot Actuator**, включают:

1. `/health`: Проверка состояния приложения и отображение статуса его здоровья.
2. `/info`: Отображение информации о версии и метаданных приложения.
3. `/metrics`: Отображение метрик производительности приложения, таких как количество **HTTP**-запросов, использование памяти и другие пользовательские метрики.
4. `/env`: Отображение информации о переменных среды, например, настройках конфигурации.
5. `/loggers`: Управление уровнями журналирования и настройкой логирования во время выполнения.
6. `/shutdown`: Отключение приложения, чтобы оно могло быть легко остановлено или перезапущено.
7. `/trace`: Отображение трассировки запросов **HTTP**, отслеживание времени выполнения и составление отчетов о работе **MVC** контроллеров.

**Spring Boot Actuator** также позволяет расширять и настраивать функции мониторинга и управления путем добавления собственных метрик, конечных точек и интерфейсов управления.

В целом, **Spring Boot Actuator** обеспечивает удобный способ мониторить и управлять приложениями, что помогает быстро обнаруживать и устранять проблемы производительности и повышать эффективность работы приложений на основе **Spring Boot**.



## Q15. Какие основные аннотации предлагает Spring Boot?


Основные аннотации, которые предлагает **Spring Boot**, находятся в его **org.springframework.boot.autoconfigure** и его подпакетах. Вот пара основных:

1. **@EnableAutoConfiguration** — заставить **Spring Boot** искать bean-компоненты автоконфигурации в своем пути к классам и автоматически применять их.
2. **@SpringBootApplication** — для обозначения основного класса загрузочного приложения. Эта аннотация объединяет аннотации **@Configuration**, **@EnableAutoConfiguration** и **@ComponentScan** с их атрибутами по умолчанию.



## Q16. Как изменить порт по умолчанию в Spring Boot?


Мы можем изменить порт по умолчанию сервера, встроенного в **Spring Boot**, одним из следующих способов:

1. Использование файла свойств. Мы можем определить это в файле **Application.properties** (или **Application.yml**), используя свойство **server.port**.
2. Программно в нашем основном классе **@SpringBootApplication** мы можем установить **server.port** для экземпляра **SpringApplication**.
3. Использование командной строки. При запуске приложения в виде файла **jar** мы можем установить **server.port** в качестве аргумента команды **java**:

java -jar -Dserver.port=8081 myspringproject.jar



## Q17. Какие встроенные серверы поддерживает Spring Boot?

Spring Boot поддерживает три встроенных сервера приложений:

1. Tomcat** (по умолчанию)
2. Jetty**
3. Undertow**

### Конфигурация встроенного сервера

```java
/**
 * Настройка встроенного сервера в Spring Boot
 */
@Configuration
public class ServerConfiguration {
 
 /**
 * Настройка Tomcat сервера
 */
 @Bean
 public TomcatServletWebServerFactory servletContainer() {
 TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
 factory.setPort(8080);
 factory.setContextPath("/api");
 return factory;
 }
}
```

## Best Practices для Spring Boot

### 1. Структура проекта

```
src/
├── main/
│ ├── java/
│ │ └── com/example/
│ │ ├── Application.java
│ │ ├── config/
│ │ ├── controller/
│ │ ├── service/
│ │ ├── repository/
│ │ └── model/
│ └── resources/
│ ├── application.yml
│ └── application-dev.yml
└── test/
```

### 2. Конфигурация через application.yml

```yaml
# application.yml
spring:
 datasource:
 url: jdbc:postgresql://localhost:5432/mydb
 username: user
 password: password
 jpa:
 hibernate:
 ddl-auto: validate
 show-sql: true

server:
 port: 8080
 servlet:
 context-path: /api
```

## Troubleshooting Spring Boot

### Типичные проблемы

1. Ошибки автоконфигурации** — проверка зависимостей в pom.xml
2. Проблемы с портом** — проверка настройки server.port
3. Ошибки загрузки контекста** — проверка компонентов и их сканирования

---

*Обновлено: 2026-01-25*


На сегодняшний день **Spring MVC** поддерживает **Tomcat**, **Jetty** и **Undertow**. **Tomcat** — это сервер приложений по умолчанию, поддерживаемый вебстартером **Spring Boot**. **Spring WebFlux** поддерживает **Reactor Netty**, **Tomcat**, **Jetty** и **Undertow** с **Reactor Netty** по умолчанию.

В **Spring MVC**, чтобы изменить значение по умолчанию, скажем, на **Jetty**, нам нужно исключить **Tomcat** и включить **Jetty** в зависимости:

&lt;dependency&gt;

&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;

&lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;

&lt;exclusions&gt;

&lt;exclusion&gt;

&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;

&lt;artifactId&gt;spring-boot-starter-tomcat&lt;/artifactId&gt;

&lt;/exclusion&gt;

&lt;/exclusions&gt;

&lt;/dependency&gt;

&lt;dependency&gt;

&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;

&lt;artifactId&gt;spring-boot-starter-jetty&lt;/artifactId&gt;

&lt;/dependency&gt;

Точно так же, чтобы изменить значение по умолчанию в **WebFlux** на **Undertow**, нам нужно исключить **Reactor Netty** и включить **Undertow** в зависимости.

Сравнение встроенных контейнеров сервлетов в **Spring Boot** содержит более подробную информацию о различных встроенных серверах, которые мы можем использовать с **Spring MVC**.

## Практические примеры на Java + Spring

### Пример 1: Создание Spring Boot приложения с автоконфигурацией

```java
/**
 * Основной класс Spring Boot приложения
 * @SpringBootApplication включает @Configuration, @EnableAutoConfiguration, @ComponentScan
 */
@SpringBootApplication
public class Application {
 
 public static void main(String[] args) {
 // Запуск Spring Boot приложения
 // Автоматически настраивает встроенный Tomcat сервер
 SpringApplication.run(Application.class, args);
 }
}

/**
 * REST контроллер
 * Автоматически регистрируется благодаря @RestController
 */
@RestController
@RequestMapping("/api")
public class UserController {
 
 private final UserService userService;
 
 public UserController(UserService userService) {
 this.userService = userService;
 }
 
 @GetMapping("/users/{id}")
 public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
 return userService.findById(id).map(user -> ResponseEntity.ok(toDTO(user))).orElse(ResponseEntity.notFound().build());
 }
}
```

### Пример 2: Использование Spring Boot Starters

```java
/**
 * Конфигурация зависимостей через Starters
 */
// В pom.xml или build.gradle:

// spring-boot-starter-web - для веб-приложений
// Включает: Spring MVC, Tomcat, Jackson, Validation

// spring-boot-starter-data-jpa - для работы с БД
// Включает: Hibernate, Spring Data JPA, HikariCP

// spring-boot-starter-security - для безопасности
// Включает: Spring Security

/**
 * Использование автоматически настроенных компонентов
 */
@Service
public class UserService {
 
 // Автоматически настроенный DataSource
 @Autowired
 private DataSource dataSource;
 
 // Автоматически настроенный EntityManager
 @PersistenceContext
 private EntityManager entityManager;
 
 // Автоматически настроенный ObjectMapper (Jackson)
 @Autowired
 private ObjectMapper objectMapper;
}
```

### Пример 3: Кастомная автоконфигурация

```java
/**
 * Создание кастомной автоконфигурации
 */
@Configuration
@ConditionalOnClass(UserService.class)
@EnableConfigurationProperties(UserProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class UserAutoConfiguration {
 
 @Bean
 @ConditionalOnMissingBean
 public UserService userService(UserProperties properties) {
 return new UserService(properties);
 }
}

/**
 * Свойства для автоконфигурации
 */
@ConfigurationProperties(prefix = "app.user")
public class UserProperties {
 private String defaultRole = "USER";
 private int maxRetries = 3;
 
 // Геттеры и сеттеры
}

/**
 * Регистрация автоконфигурации в META-INF/spring.factories
 */
// org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
// com.example.config.UserAutoConfiguration
```

### Пример 4: Использование Spring Boot Actuator

```java
/**
 * Конфигурация Actuator endpoints
 */
@Configuration
public class ActuatorConfig {
 
 /**
 * Настройка доступных endpoints
 */
 @Bean
 public EndpointFilter endpointFilter() {
 return new EndpointFilter() {
 @Override
 public boolean match(EndpointInfo endpointInfo) {
 // Разрешить только определенные endpoints
 return endpointInfo.getId().equals("health") || 
 endpointInfo.getId().equals("info");
 }
 };
 }
}

/**
 * Кастомный health indicator
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
 
 @Autowired
 private DataSource dataSource;
 
 @Override
 public Health health() {
 try (Connection connection = dataSource.getConnection()) {
 if (connection.isValid(1)) {
 return Health.up().withDetail("database", "Available").build();
 }
 } catch (SQLException e) {
 return Health.down().withDetail("database", "Unavailable").withException(e).build();
 }
 return Health.down().build();
 }
}
```

### Пример 5: Использование Spring Boot DevTools

```java
/**
 * DevTools автоматически перезагружает приложение при изменении классов
 * Требует зависимости spring-boot-devtools
 */
@SpringBootApplication
public class Application {
 public static void main(String[] args) {
 SpringApplication.run(Application.class, args);
 }
}

/**
 * DevTools также предоставляет:
 * - LiveReload для автоматической перезагрузки браузера
 * - Упрощенную конфигурацию для разработки
 * - Отключение шаблонизаторов кэша
 */
```

## Best Practices для Spring Boot

### 1. Использование @SpringBootApplication

**✅ Правильно:
```java
// Использование @SpringBootApplication для упрощения конфигурации
@SpringBootApplication
public class Application {
 public static void main(String[] args) {
 SpringApplication.run(Application.class, args);
 }
}
```

**❌ Неправильно:
```java
// Избыточная конфигурация
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.example") // ❌ Избыточно, если использовать @SpringBootApplication
public class Application {
 //...
}
```

### 2. Использование application.properties/yml

**✅ Правильно:
```java
// Конфигурация через application.properties
// server.port=8080
// spring.datasource.url=jdbc:postgresql://localhost/mydb

@Value("${app.max-retries:3}")
private int maxRetries;
```

**❌ Неправильно:
```java
// Хардкод конфигурации в коде
private int maxRetries = 3; // ❌ Должно быть в application.properties
```

### 3. Использование Profiles

**✅ Правильно:
```java
// Разделение конфигурации по профилям
@Configuration
@Profile("production")
public class ProductionConfig {
 // Конфигурация для production
}

@Configuration
@Profile("development")
public class DevelopmentConfig {
 // Конфигурация для development
}
```

## Troubleshooting Spring Boot

### Проблема 1: Приложение не запускается

**Симптомы:
- Ошибки при запуске
- Отсутствие бинов

**Решение:
```java
// Проверка компонентного сканирования
@SpringBootApplication(scanBasePackages = "com.example") // ✅ Указать правильный пакет
public class Application {
 public static void main(String[] args) {
 SpringApplication.run(Application.class, args);
 }
}
```

### Проблема 2: Проблемы с автоконфигурацией

**Симптомы:
- Автоконфигурация не применяется
- Конфликты конфигураций

**Решение:
```java
// Отключение конкретной автоконфигурации
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {
 //...
}

// Или в application.properties
// spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

### Проблема 3: Проблемы с портом

**Симптомы:
- Порт уже занят
- Неправильный порт

**Решение:
```java
// Настройка порта в application.properties
// server.port=8081

// Или программно
@SpringBootApplication
public class Application {
 public static void main(String[] args) {
 SpringApplication app = new SpringApplication(Application.class);
 app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
 app.run(args);
 }
}
```

### Проблема 4: Проблемы с зависимостями

**Симптомы:
- ClassNotFoundException
- NoClassDefFoundError
- Конфликты версий

**Решение:
```java
// Проверка зависимостей через Maven
// mvn dependency:tree

// Исключение конфликтующих зависимостей
<dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-web</artifactId>
 <exclusions>
 <exclusion>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-tomcat</artifactId>
 </exclusion>
 </exclusions>
</dependency>
```

### Проблема 5: Проблемы с профилями

**Симптомы:
- Неправильная конфигурация для окружения
- Отсутствие свойств для профиля

**Решение:
```java
// Использование профилей
@SpringBootApplication
public class Application {
 public static void main(String[] args) {
 SpringApplication app = new SpringApplication(Application.class);
 app.setAdditionalProfiles("production");
 app.run(args);
 }
}

// Или через переменные окружения
// SPRING_PROFILES_ACTIVE=production
```

## Заключение

Spring Boot значительно упрощает разработку Spring приложений, предоставляя автоконфигурацию, встроенные серверы и множество удобных функций. Понимание Spring Boot критически важно для Senior Java Developer.Ключевые моменты для запоминания:

1.@SpringBootApplication** - комбинация @Configuration, @EnableAutoConfiguration, @ComponentScan
2. Starters** - предварительно настроенные зависимости
3. Автоконфигурация** - автоматическая настройка компонентов
4. Actuator** - мониторинг и управление приложением
5. Profiles** - разделение конфигурации по окружениям

**Рекомендации для собеседования:

- Уметь объяснить разницу между Spring и Spring Boot
- Знать основные Starters и их назначение
- Понимать, как работает автоконфигурация
- Уметь создавать кастомные Starters
- Знать, как использовать Actuator для мониторинга

### Дополнительные примеры: Использование Spring Boot Test

```java
/**
 * Интеграционное тестирование Spring Boot приложения
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
 
 @Autowired
 private MockMvc mockMvc;
 
 @Test
 void testCreateUser() throws Exception {
 mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"John\",\"email\":\"john@example.com\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("John"));
 }
}
```

### Дополнительные примеры: Использование Spring Boot Admin

```java
/**
 * Конфигурация Spring Boot Admin Server
 */
@SpringBootApplication
@EnableAdminServer
public class AdminServerApplication {
 public static void main(String[] args) {
 SpringApplication.run(AdminServerApplication.class, args);
 }
}

/**
 * Конфигурация клиента Spring Boot Admin
 */
@Configuration
public class AdminClientConfig {
 
 @Bean
 public AdminServerProperties adminServerProperties() {
 AdminServerProperties properties = new AdminServerProperties();
 properties.setUrl("http://localhost:8080");
 return properties;
 }
}
```

### Дополнительные примеры: Использование Spring Boot CLI

```java
/**
 * Spring Boot CLI позволяет быстро создавать приложения
 * Пример команды: spring init --dependencies=web,data-jpa myapp
 */
@RestController
public class QuickStartController {
 
 @GetMapping("/")
 public String hello() {
 return "Hello from Spring Boot CLI!";
 }
}
```

---

**Последнее обновление: 2026-01-25
