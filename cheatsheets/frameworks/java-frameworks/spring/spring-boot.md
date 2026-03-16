---
title: "Spring Boot - Полное руководство"
description: "Комплексное руководство по Spring Boot: авто-конфигурация, стартеры, REST API, безопасность, базы данных, микросервисы, развертывание, мониторинг и best practices"
tags: ["spring-boot", "java", "framework", "microservices", "rest", "jpa", "security", "docker", "kubernetes"]
difficulty: "intermediate"
prerequisites: ["java/java-basics.md", "spring/spring-core.md"]
next: ["spring/spring-data-jpa.md", "spring/spring-security.md"]
updated: "2026-02-06"
---

# Spring Boot

## Полезные ссылки

- [Официальная документация **Spring Boot**](https://spring.io/projects/spring-boot)

## Содержание

- [Создание простого приложения](#создание-простого-приложения)
  - [Добавление безопасности](#добавление-безопасности)
  - [Тестирование](#тестирование)
- [DispatcherServlet и web.xml](#dispatcherservlet-и-webxml)
  - [Настройка DispatcherServlet](#настройка-dispatcherservlet)
  - [Регистрация Filter](#регистрация-filter)
  - [Регистрация Servlet](#регистрация-servlet)
  - [Регистрация Listener](#регистрация-listener)
- [Сравнение Spring и Spring Boot](#сравнение-spring-и-spring-boot)
  - [Основные функции Spring Boot](#основные-функции-spring-boot)
  - [Стартовые зависимости](#стартовые-зависимости)
  - [Конфигурация](#конфигурация)
  - [Точка входа](#точка-входа)
  - [Преимущества Spring Boot в развертывании](#преимущества-spring-boot-в-развертывании)
- [Руководство по Spring Boot Starters](#руководство-по-spring-boot-starters)
  - [Работа с JPA](#работа-с-jpa)
  - [Работа с почтой](#работа-с-почтой)
- [Руководство по Actuator](#руководство-по-actuator)
  - [Настройка Actuator](#настройка-actuator)
  - [Доступные конечные точки](#доступные-конечные-точки)
- [Руководство по @PropertySource](#руководство-по-propertysource)
  - [Использование @PropertySource](#использование-propertysource)
  - [Внедрение свойств](#внедрение-свойств)
  - [Использование в Spring Boot](#использование-в-spring-boot)
  - [Тестовые свойства](#тестовые-свойства)
- [Руководство по @ConfigurationProperties](#руководство-по-configurationproperties)
  - [Вложенные свойства](#вложенные-свойства)
  - [Валидация свойств](#валидация-свойств)
  - [Интеграционное тестирование с @DataJpaTest](#интеграционное-тестирование-с-datajpatest)
  - [Модульное тестирование с @WebMvcTest](#модульное-тестирование-с-webmvctest)
  - [Автоматически настроенные тесты](#автоматически-настроенные-тесты)
- [Интеграционные тесты БД с TestContainers](#интеграционные-тесты-бд-с-testcontainers)
- [Тестирование REST клиентов с @RestClientTest](#тестирование-rest-клиентов-с-restclienttest)
- [Логгирование](#логгирование)
  - [Настройка уровня логирования](#настройка-уровня-логирования)
  - [Использование Lombok для логирования](#использование-lombok-для-логирования)
- [Приложение как Service (Linux)](#приложение-как-service-linux)
- [Spring Boot 3 и Spring Framework 6.0](#spring-boot-3-и-spring-framework-60)
  - [Новые функции Java](#новые-функции-java)
    - [Records](#records)
    - [Текстовые блоки](#текстовые-блоки)
    - [Switch выражения](#switch-выражения)
    - [Pattern Matching](#pattern-matching)
    - [Sealed классы](#sealed-классы)
  - [Миграция на Jakarta EE](#миграция-на-jakarta-ee)
- [Продвинутые возможности Spring Boot](#продвинутые-возможности-spring-boot)
  - [Микросервисы с Spring Boot](#микросервисы-с-spring-boot)
  - [Reactive Spring Boot](#reactive-spring-boot)
  - [Spring Boot с Kotlin](#spring-boot-с-kotlin)
  - [Cloud Native Spring Boot](#cloud-native-spring-boot)
  - [Мониторинг и Observability](#мониторинг-и-observability)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [JVM оптимизации для Spring Boot](#jvm-оптимизации-для-spring-boot)
- [application.properties для production](#applicationproperties-для-production)
- [Развертывание](#развертывание)
  - [Docker с Spring Boot](#docker-с-spring-boot)
  - [Kubernetes развертывание](#kubernetes-развертывание)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Заключение](#заключение)

## Создание простого приложения

**Далее мы настроим простой основной класс для нашего приложения. Точка входа с @**SpringBootApplication**:**

```java
// Точка входа: @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Обратите внимание, как мы используем **@SpringBootApplication** в качестве основного класса конфигурации приложения. За кулисами это эквивалентно **@Configuration**, **@EnableAutoConfiguration** и **@ComponentScan** вместе.

**Наконец, мы определим простой файл **application.properties**, который пока имеет только одно свойство:**

```properties
# Порт встроенного сервера (по умолчанию 8080)
server.port=8081
```

**server.port** изменяет порт сервера с **8080** по умолчанию на **8081**; конечно, доступно гораздо больше свойств **Spring Boot**.

### Добавление безопасности

**Затем давайте добавим безопасность в наше приложение, сначала включив стартер безопасности:**

```xml
<!-- Стартер Spring Security: защита эндпоинтов по умолчанию -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

К настоящему времени мы можем заметить закономерность: большинство библиотек **Spring** легко импортируются в наш проект с использованием простых стартеров **Boot**.

Как только зависимость **spring-`boot-starter`-security** находится в пути к классам приложения, все конечные точки защищены по умолчанию с использованием **httpBasic** или **formLogin** на основе стратегии согласования содержимого **Spring Security**.

**Вот почему, если у нас есть стартер в пути к классам, мы обычно должны определять нашу собственную настраиваемую конфигурацию безопасности, расширяя класс **WebSecurityConfigurerAdapter**:**

```java
// Конфигурация безопасности Spring Security
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Разрешить все запросы и отключить CSRF для примера
        http.authorizeRequests()
            .anyRequest()
            .permitAll()
            .and().csrf().disable();
    }
}
```

В нашем примере мы разрешаем неограниченный доступ ко всем конечным точкам.

### Тестирование

**Мы можем использовать **@SpringBootTest** для загрузки контекста приложения и проверки отсутствия ошибок при запуске приложения:**

```java
// Тест проверки загрузки контекста Spring Boot приложения
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringContextTest {
    @Test
    public void contextLoads() {
        // Контекст загружен успешно, если тест пройден
    }
}
```

Затем давайте добавим тест **JUnit**, который проверяет вызовы написанного нами **API** с использованием **REST `Assured`.**

**Во-первых, мы добавим зависимость:**

```xml
<!-- REST Assured для интеграционных тестов API -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
```

**А теперь можем добавить тест:**

```java
// Интеграционный тест REST API с использованием REST Assured
public class SpringBootBootstrapLiveTest {
    private static final String API_ROOT = "http://localhost:8081/api/books";
    
    // Создание книги со случайными данными для тестов
    private Book createRandomBook() {
        Book book = new Book();
        book.setTitle(randomAlphabetic(10));
        book.setAuthor(randomAlphabetic(15));
        return book;
    }
    
    // Создание книги через API и возврат URI созданного ресурса
    private String createBookAsUri(Book book) {
        Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(book)
            .post(API_ROOT);
        return API_ROOT + "/" + response.jsonPath().get("id");
    }
}
```

**Во-первых, мы можем попробовать найти книги с помощью различных методов:**

```java
// Тест получения всех книг
@Test
public void whenGetAllBooks_thenOK() {
    Response response = RestAssured.get(API_ROOT);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
}

// Тест поиска книг по названию
@Test
public void whenGetBooksByTitle_thenOK() {
    Book book = createRandomBook();
    createBookAsUri(book);
    Response response = RestAssured.get(
        API_ROOT + "/title/" + book.getTitle());
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    assertTrue(response.as(List.class).size() > 0);
}

// Тест получения книги по ID
@Test
public void whenGetCreatedBookById_thenOK() {
    Book book = createRandomBook();
    String location = createBookAsUri(book);
    Response response = RestAssured.get(location);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    assertEquals(book.getTitle(), response.jsonPath().get("title"));
}

// Тест получения несуществующей книги - ожидается 404
@Test
public void whenGetNotExistBookById_thenNotFound() {
    Response response = RestAssured.get(API_ROOT + "/" + randomNumeric(4));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
}
```

**Далее мы протестируем создание новой книги:**

```java
// Тест создания новой книги через POST запрос
@Test
public void whenCreateNewBook_thenCreated() {
    Book book = createRandomBook();
    Response response = RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(book)
        .post(API_ROOT);
    assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
}

// Тест валидации - создание книги с некорректными данными
@Test
public void whenInvalidBook_thenError() {
    Book book = createRandomBook();
    book.setAuthor(null);  // Намеренно устанавливаем null для теста
    Response response = RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(book)
        .post(API_ROOT);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
}
```

**Затем мы обновим существующую книгу:**

```java
// Тест обновления существующей книги через PUT запрос
@Test
public void whenUpdateCreatedBook_thenUpdated() {
    Book book = createRandomBook();
    String location = createBookAsUri(book);
    // Извлечение ID из URI и обновление автора
    book.setId(Long.parseLong(location.split("api/books/")[1]));
    book.setAuthor("newAuthor");
    Response response = RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(book)
        .put(location);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    // Проверка что изменения сохранились
    response = RestAssured.get(location);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    assertEquals("newAuthor", response.jsonPath().get("author"));
}
```

**И мы можем удалить книгу:**

```java
// Тест удаления книги через DELETE запрос
@Test
public void whenDeleteCreatedBook_thenOk() {
    Book book = createRandomBook();
    String location = createBookAsUri(book);
    Response response = RestAssured.delete(location);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    // Проверка что книга действительно удалена
    response = RestAssured.get(location);
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
}
```

## DispatcherServlet и web.xml

**DispatcherServlet** - это контроллер в веб-приложениях **Spring.**

Он используется для создания веб-приложений и служб **REST** в **Spring MVC**. В традиционном веб-приложении **Spring** этот сервлет определяется в файле **web.xml**.

В этом руководстве мы перенесем код из файла **web.xml** в **DispatcherServlet** в приложении **Spring Boot**. Кроме того, мы сопоставим классы **Filter**, **Servlet** и **Listener** из **web.xml** с приложением **Spring Boot**.

### Настройка **DispatcherServlet**

Во-первых, мы должны добавить зависимость **spring-`boot-starter-web` Maven** в наш файл **pom.xml:**

```xml
<!-- Стартер spring-boot-starter-web для веб-приложений -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**DispatcherServlet** получает все **HTTP-**запросы и делегирует их классам контроллера.

До спецификации **Servlet 3.x**, **DispatcherServlet** регистрировался в файле **web.xml** для приложения **Spring MVC**. Начиная со спецификации **Servlet 3.x**, мы можем регистрировать сервлеты программно, используя **ServletContainerInitializer**.

Пример конфигурации **DispatcherServlet** в файле **web.xml:**

```xml
<!-- Регистрация DispatcherServlet и маппинг на URL / -->
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

**Spring Boot** предоставляет библиотеку **spring-`boot-starter`-web** для разработки веб-приложений с использованием **Spring MVC**.

Одной из главных особенностей **Spring Boot** является автоконфигурация. Автоконфигурация **Spring Boot** автоматически регистрирует и настраивает **DispatcherServlet**. Поэтому нам не нужно регистрировать **DispatcherServlet** вручную.

**По умолчанию пускатель **spring-`boot-starter`-web** настраивает **DispatcherServlet** на шаблон **URL** «/». Однако мы можем настроить шаблон **URL** с помощью **server.servlet.** в файле **application.properties**:**

```properties
server.servlet.context-path=/demo
spring.mvc.servlet.path=/baeldung
```

С этими настройками **DispatcherServlet** настроен на обработку шаблона **URL** /**baeldung**, а корневой **contextPath** будет **/demo**. Таким образом, **DispatcherServlet** прослушивает **http://localhost:8080/demo/baeldung/**.

### Регистрация **Filter**

Давайте создадим фильтр, реализовав интерфейс **Filter:**

```java
// Кастомный фильтр для перехвата HTTP-запросов
@Component
public class CustomFilter implements Filter {
    Logger logger = LoggerFactory.getLogger(CustomFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация фильтра
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        // Логирование входящего запроса
        logger.info("CustomFilter is invoked");
        chain.doFilter(request, response);
    }
}
```

Чтобы **Spring Boot** мог распознавать фильтр, нам просто нужно было определить его как **bean**-компонент с аннотацией **@Component**.

### Регистрация **Servlet**

Давайте определим сервлет, расширив класс **HttpServlet:**

```java
// Кастомный сервлет для обработки HTTP-запросов
public class CustomServlet extends HttpServlet {
    Logger logger = LoggerFactory.getLogger(CustomServlet.class);
    
    // Обработка GET-запросов
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        logger.info("CustomServlet doGet() method is invoked");
        super.doGet(req, resp);
    }
    
    // Обработка POST-запросов
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        logger.info("CustomServlet doPost() method is invoked");
        super.doPost(req, resp);
    }
}
```

**В приложении **Spring Boot** сервлет регистрируется либо как **Spring `@Bea`n**:**

```java
// Регистрация кастомного сервлета как Spring Bean
@Bean
public ServletRegistrationBean customServletBean() {
    // Привязка сервлета к URL-паттерну /servlet
    ServletRegistrationBean bean = new ServletRegistrationBean(new CustomServlet(), "/servlet");
    return bean;
}
```

### Регистрация **Listener**

Давайте определим слушателя, расширив класс **ServletContextListener:**

```java
// Слушатель жизненного цикла контекста сервлета
public class CustomListener implements ServletContextListener {
    Logger logger = LoggerFactory.getLogger(CustomListener.class);
    
    // Вызывается при инициализации контекста
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("CustomListener is initialized");
    }
    
    // Вызывается при уничтожении контекста
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("CustomListener is destroyed");
    }
}
```

**Чтобы определить прослушиватель в приложении **Spring Boot**, мы можем использовать класс **ServletListenerRegistrationBean**:**

```java
// Регистрация слушателя контекста как Spring Bean
@Bean
public ServletListenerRegistrationBean<ServletContextListener> customListenerBean() {
    ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean<>();
    bean.setListener(new CustomListener());  // Установка кастомного слушателя
    return bean;
}
```

## Сравнение **Spring** и **Spring Boot**

**Spring Boot** — это, по сути, расширение инфраструктуры **Spring**, которое исключает стандартные конфигурации, необходимые для настройки приложения **Spring.**

Он относится к платформе **Spring**, что открывает путь к более быстрой и эффективной экосистеме разработки.

### Основные функции **Spring Boot**

**Вот лишь некоторые из функций **Spring Boot**:**

1. _Наличие "стартовых" зависимостей для упрощения сборки и настройки приложения._
2. _Встроенный сервер, чтобы избежать сложностей при развертывании приложений_
3. _Метрики, проверка работоспособности и внешняя конфигурация_
4. _Автоматическая конфигурация для функциональности **Spring** - когда это возможно_

### Стартовые зависимости

**Spring Boot** предоставляет ряд стартовых зависимостей для различных модулей **Spring**. Вот некоторые из наиболее часто используемых:**

1. **spring-`boot-starter-data`-jpa**
2. **spring-`boot-starter`-security**
3. **spring-`boot-starter`-test**
4. **spring-`boot-starter`-web**
5. **spring-`boot-starter`-thymeleaf**

### Конфигурация

**Spring** требует определения сервлета диспетчера, сопоставлений и других поддерживающих конфигураций.

**Для сравнения, **Spring Boot** нуждается только в нескольких свойствах, чтобы все работало после добавления веб-стартера:**

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

Вся указанная выше конфигурация **Spring** автоматически включается путем добавления веб-стартера **Boot** через процесс, называемый автоконфигурацией.

### Точка входа

**Точкой входа в приложение **Spring Boot** является класс, помеченный **@SpringBootApplication**:**

```java
// Точка входа Spring Boot приложения
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);  // Запуск приложения
    }
}
```

По умолчанию **Spring Boot** использует встроенный контейнер для запуска приложения. В этом случае **Spring Boot** использует основную точку входа **public static void** для запуска встроенного веб-сервера.

### Преимущества **Spring Boot** в развертывании

_Некоторые из преимуществ **Spring Boot** над **Spring** в контексте развертывания включают:_

1. Обеспечивает поддержку встроенного контейнера
2. Возможность независимого запуска **jar**-файлов с помощью команды **java** -**jar**
3. Возможность исключения зависимостей, чтобы избежать потенциальных конфликтов **jar** при развертывании во внешнем контейнере
4. Возможность указать активные профили при развертывании
5. Генерация случайных портов для интеграционных тестов

## Руководство по **Spring Boot Starters**

Во-первых, давайте посмотрим на разработку службы **REST**; мы можем использовать такие библиотеки, как **Spring MVC**, **Tomcat** и **Jackson** — множество зависимостей для одного приложения.

**Программы запуска **Spring Boot** могут помочь уменьшить количество добавляемых вручную зависимостей, просто добавив одну зависимость. Поэтому вместо того, чтобы вручную указывать зависимости, просто добавьте один стартер, как в следующем примере:**

```xml
<!-- Стартер spring-boot-starter-web: MVC, Tomcat, Jackson -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Для тестирования мы обычно используем следующий набор библиотек: **Spring Test**, **JUnit**, **Hamcrest** и **Mockito**. Мы можем включить все эти библиотеки вручную, но можно использовать стартер **Spring Boot** для автоматического включения этих библиотек следующим образом:

```xml
<!-- Стартер spring-boot-starter-test: JUnit, Mockito, Hamcrest -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Обратите внимание, что вам не нужно указывать номер версии артефакта. **Spring Boot** определит, какую версию использовать — все, что вам нужно указать, это версия артефакта **spring-`boot-starter`-parent**. Если позже вам потребуется обновить библиотеку загрузки и зависимости, просто обновите версию загрузки в одном месте, а все остальное сделает она сама.

### Работа с **JPA**

Большинство веб-приложений обладают некоторой устойчивостью - и это довольно часто **JPA.**

**Вместо того, чтобы определять все связанные зависимости вручную, давайте воспользуемся стартером:**

```xml
<!-- Стартеры JPA и H2 для работы с базой данных -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

Обратите внимание, что из коробки у нас есть автоматическая поддержка как минимум следующих баз данных: **H2**, **Derby** и **Hsqldb**. В нашем примере мы будем использовать **H2**.

### Работа с почтой

**Очень распространённой задачей в корпоративной разработке является отправка электронной почты, и работа напрямую с Java Mail API обычно может быть затруднена.**

**Spring Boot starter** скрывает эту сложность — почтовые зависимости можно указать следующим образом:

```xml
<!-- Стартер spring-boot-starter-mail для отправки почты -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

Теперь мы можем напрямую использовать **JavaMailSender**. Как и любые другие значения по умолчанию в **Boot**, настройки электронной почты для **JavaMailSender** можно настроить в **application.properties:**

```properties
spring.mail.host=localhost
spring.mail.port=25
spring.mail.properties.mail.smtp.auth=false
```

## Руководство по **Actuator**

По сути, **Actuator** привносит в наше приложение готовые к работе функции.

Мониторинг нашего приложения, сбор показателей, понимание трафика или состояния нашей базы данных становится тривиальным делом с этой зависимостью.

Основное преимущество этой библиотеки заключается в том, что мы можем получить инструменты производственного уровня без необходимости самостоятельно реализовывать эти функции.

**Actuator** в основном используется для предоставления оперативной информации о запущенном приложении - о работоспособности, показателях, информации, дампе**, env** и т. Д. Он использует конечные точки **HTTP** или **JMX-**бины, чтобы мы могли взаимодействовать с ним.

### Настройка **Actuator**

Как только эта зависимость находится в пути к классам, несколько конечных точек становятся доступны для нас из коробки. Как и большинство модулей **Spring**, мы можем легко настроить или расширить его разными способами.

В **Actuator** большинство конечных точек отключено.

Таким образом, по умолчанию доступны только два параметра: **/health** и **/info**.

Если мы хотим включить их все, мы могли бы установить **management.`endpoints.`web.exposure`.include` = \**. В качестве альтернативы мы можем перечислить конечные точки, которые должны быть включены.

Теперь **Actuator** разделяет конфигурацию безопасности с обычными правилами безопасности приложений, поэтому модель безопасности значительно упрощается.

```java
// Конфигурация безопасности для Actuator endpoints
@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.authorizeExchange()
        .pathMatchers("/actuator/").permitAll()  // Открытый доступ к actuator
        .anyExchange().authenticated()
        .and().build();
}
```

Кроме того, по умолчанию все конечные точки исполнительных механизмов теперь помещаются в путь **/actuator.**

### Доступные конечные точки

**Кроме того, некоторые конечные точки были добавлены, некоторые удалены, а некоторые реструктурированы:**

1. **/auditevents** перечисляет события, связанные с аудитом безопасности, такие как вход/выход пользователя. Кроме того, мы можем фильтровать по принципалу или типу среди других полей.
2. **/beans** возвращает все доступные **bean**-компоненты в нашей **BeanFactory**. В отличие от /**auditevents**, он не поддерживает фильтрацию.
3. **/conditions**, ранее известный как /**autoconfig**, создаёт отчёт об условиях, связанных с автоконфигурацией.
4. **/configprops** позволяет нам получать все **bean**- компоненты@**ConfigurationProperties**.
5. **/env** возвращает текущие свойства среды. Кроме того, мы можем получить отдельные свойства.
6. **/flyway** предоставляет подробную информацию о миграции нашей базы данных **Flyway**.
7. **/health** показывает состояние здоровья нашего приложения.
8. **/heapdump** создаёт и возвращает дамп кучи из **JVM**, используемой нашим приложением.
9. **/info** возвращает общую информацию. Это могут быть пользовательские данные, информация о сборке или сведения о последней фиксации.
10. **/liquibase** ведет себя так же, как /**flyway**, но для **Liquibase**.
11. **/logfile** возвращает обычные журналы приложений.
12. **/loggers** позволяет нам запрашивать и изменять уровень ведения журнала нашего приложения.
13. **/metrics** подробно описывает показатели нашего приложения. Сюда могут входить как общие, так и настраиваемые метрики.
14. **/prometheus** возвращает метрики, подобные предыдущему, но отформатированные для работы с сервером **Prometheus**.
15. **/scheduletasks** предоставляет подробную информацию о каждой запланированной задаче в нашем приложении.
16. **/sessions** перечисляет **HTTP**-сеансы, учитывая, что мы используем **Spring Session**.
17. **/shutdown** выполняет плавное завершение работы приложения.
18. **/threaddump** выгружает информацию о потоках базовой **JVM**.

## Руководство по @**PropertySource**

В этом руководстве будет показано, как настраивать и использовать свойства в **Spring** с помощью конфигурации **Java** и **`@PropertySource`.**

Мы также увидим, как свойства работают в **Spring Boot**.

**Spring 3.1** также представляет новую аннотацию **@PropertySource** как удобный механизм для добавления источников свойств в среду.

### Использование @**PropertySource**

Мы можем использовать эту аннотацию вместе с аннотацией **`@Configuration`:**

```java
// Загрузка свойств из файла foo.properties в classpath
@Configuration
@PropertySource("classpath:foo.properties")
public class PropertiesWithJavaConfig {
}
```

**Еще один очень полезный способ зарегистрировать новый файл свойств - использовать заполнитель, который позволяет нам динамически выбирать нужный файл во время выполнения:**

```java
// Динамический выбор файла через placeholder (по умолчанию mysql)
@PropertySource({
    "classpath:persistence-${envTarget:mysql}.properties"
})
```

**@PropertySource** аннотация может использоваться в соответствии с **Java 8** конвенциями.

**Следовательно, если мы используем **Java 8** или выше, мы можем использовать эту аннотацию для определения нескольких местоположений свойств:**

```java
// Несколько аннотаций @PropertySource (Java 8+)
@PropertySource("classpath:foo.properties")
@PropertySource("classpath:bar.properties")
public class PropertiesWithJavaConfig {
}
```

**Конечно, мы также можем использовать аннотацию **@PropertySources** и указать массив **@PropertySource**. Это работает в любой поддерживаемой версии **Java**, а не только в **Java 8** или выше:**

```java
// Альтернативный способ через @PropertySources (любая версия Java)
@PropertySources({
    @PropertySource("classpath:foo.properties"),
    @PropertySource("classpath:bar.properties")
})
public class PropertiesWithJavaConfig {
}
```

В любом случае стоит отметить, что в случае конфликта имен свойств приоритет имеет последний прочитанный источник.

### Внедрение свойств

Внедрение свойства с аннотацией **`@Value`:**

```java
// Внедрение значения свойства jdbc.url через @Value
@Value("${jdbc.url}")
private String jdbcUrl;
```

**Мы также можем указать для свойства значение по умолчанию:**

```java
// Значение по умолчанию aDefaultUrl, если свойство не определено
@Value("${jdbc.url:aDefaultUrl}")
private String jdbcUrl;
```

Новый **PropertySourcesPlaceholderConfigurer**, добавленный в **Spring 3.1**, разрешает заполнители **${…}** в значениях свойств определения **bean**-компонентов и аннотациях **@Value**.

Наконец, мы можем получить значение свойства с помощью **Environment `API`:**

```java
// Получение свойств через Environment API
@Autowired
private Environment env;

// Программный доступ к свойствам
dataSource.setUrl(env.getProperty("jdbc.url"));
```

### Использование в **Spring Boot**

**Boot** применяет свое типичное соглашение по подходу к настройке к файлам свойств.

Это означает, что мы можем просто поместить файл **application.properties** в наш каталог **src/main/resources**, и он будет обнаружен автоматически. Затем мы можем ввести из него любые загруженные свойства как обычно.

Таким образом, используя этот файл по умолчанию, нам не нужно явно регистрировать **PropertySource** или даже указывать путь к файлу свойств.

_Мы также можем настроить другой файл во время выполнения, если нам нужно, используя свойство среды:_

```bash
# Указание альтернативного файла конфигурации при запуске JAR
java -jar app.jar --spring.config.location=classpath:/another-location.properties
```

Начиная с **Spring `Boot 2`.3**, мы также можем указывать расположение подстановочных знаков для файлов конфигурации.

Например, мы можем установить для свойства **spring.config.location** значение **config**/\*/:**

```bash
# Подстановочный путь для конфигурации (Spring Boot 2.3+)
java -jar app.jar --spring.config.location=config/*
```

### Тестовые свойства

Если нам нужен более детальный контроль над тестовыми свойствами, мы можем использовать аннотацию **`@TestPropertySource`.**

**Это позволяет нам устанавливать свойства теста для конкретного контекста теста, имея приоритет над источниками свойств по умолчанию:**

```java
// Тест с загрузкой свойств из файла foo.properties
@RunWith(SpringRunner.class)
@TestPropertySource("/foo.properties")
public class FilePropertyInjectionUnitTest {
    @Value("${foo}")
    private String foo;  // Внедрённое значение из файла
    
    @Test
    public void whenFilePropertyProvided_thenProperlyInjected() {
        assertThat(foo).isEqualTo("bar");
    }
}
```

**Если мы не хотим использовать файл, мы можем указать имена и значения напрямую:**

```java
// Тест с указанием свойств напрямую в аннотации
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"foo=bar"})
public class PropertyInjectionUnitTest {
    @Value("${foo}")
    private String foo;  // Значение определено в аннотации
    
    @Test
    public void whenPropertyProvided_thenProperlyInjected() {
        assertThat(foo).isEqualTo("bar");
    }
}
```

**Мы также можем достичь аналогичного эффекта, используя свойства аргумента **@SpringBootTest** аннотации:**

```java
// Интеграционный тест с переопределением свойств через @SpringBootTest
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"foo=bar"}, classes = SpringBootPropertiesTestApplication.class)
public class SpringBootPropertyInjectionIntegrationTest {
    @Value("${foo}")
    private String foo;
    
    @Test
    public void whenSpringBootPropertyProvided_thenProperlyInjected() {
        assertThat(foo).isEqualTo("bar");
    }
}
```

## Руководство по @**ConfigurationProperties**

В официальной документации рекомендуется выделить свойства конфигурации в отдельные объекты **POJO.**

**Итак, начнем с этого:**

```java
// POJO для привязки свойств с префиксом mail.*
@ConfigurationProperties(prefix = "mail")
public class ConfigProperties {
    private String hostName;  // Привязано к mail.hostName
    private int port;         // Привязано к mail.port
    private String from;      // Привязано к mail.from
}
```

Мы используем **@Configuration**, чтобы **Spring** создавал **bean**-компонент в контексте приложения.

**@ConfigurationProperties** лучше всего работает с иерархическими свойствами, имеющими одинаковый префикс; поэтому мы добавляем префикс почты.

Инфраструктура **Spring** использует стандартные сеттеры **Java bean**, поэтому мы должны объявить сеттеры для каждого свойства.

Примечание: если мы не используем **@Configuration** в **POJO**, нам нужно добавить **`@EnableConfigurationProperties`(**ConfigProperties.class**)** в основной класс приложения **Spring**, чтобы привязать свойства к **POJO**:

```java
// Включение @ConfigurationProperties для класса ConfigProperties
@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class EnableConfigurationDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnableConfigurationDemoApplication.class, args);
    }
}
```

**Spring** использует некоторые упрощенные правила для привязки свойств. В результате к свойству **hostName** привязаны следующие варианты:**

- **mail.hostName**
- **mail.hostname**
- **mail.host_name**
- **mail.host-name**
- **mail.HOST_NAME**

**Следовательно, мы можем использовать следующий файл свойств, чтобы установить все поля:**

```properties
#Simple properties
mail.hostname=host@mail.com
mail.port=9000
mail.from=mailer@mail.com
```

### Вложенные свойства

У нас могут быть вложенные свойства в списках, картах и классах.

**Давайте создадим новый класс **Credentials**, который будет использоваться для некоторых вложенных свойств:**

```java
// Вложенный класс для хранения учётных данных
public class Credentials {
    private String authMethod;  // Метод аутентификации
    private String username;
    private String password;
}
```

**Нам также необходимо обновить **ConfigProperties** класс, чтобы использовать список, на карту, и **Credentials** класс:**

```java
// Расширенный класс свойств с коллекциями и вложенными объектами
public class ConfigProperties {
    private String host;
    private int port;
    private String from;
    private List<String> defaultRecipients;  // Список получателей
    private Map<String, String> additionalHeaders;  // Дополнительные заголовки
    private Credentials credentials;  // Вложенный объект
}
```

**Следующий файл свойств установит все поля:**

```properties
#Simple properties
mail.hostname=mailer@mail.com
mail.port=9000
mail.from=mailer@mail.com

#List properties
mail.defaultRecipients[0]=admin@mail.com
mail.defaultRecipients[1]=owner@mail.com

#Map Properties
mail.additionalHeaders.redelivery=true
mail.additionalHeaders.secure=true

#Object properties
mail.credentials.username=john
mail.credentials.password=password
mail.credentials.authMethod=SHA1
```

### Валидация свойств

**@ConfigurationProperties** обеспечивает проверку свойств с использованием формата **JSR-303**.

Это позволяет делать разные изящные вещи.

**Например, сделаем свойство **hostName** обязательным:**

```java
// Поле обязательно для заполнения
@NotBlank
private String hostName;
```

**Затем сделаем свойство **authMethod** длиной от **1** до **4** символов:**

```java
// Ограничение длины строки от 1 до 4 символов
@Length(max = 4, min = 1)
private String authMethod;
```

Затем свойство порта от **1025** до **65536:**

```java
// Порт должен быть в диапазоне 1025-65536
@Min(1025)
@Max(65536)
private int port;
```

**Наконец, свойство **from** должно соответствовать формату адреса электронной почты:**

```java
// Валидация формата email через регулярное выражение
@Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$")
private String from;
```

Если какая-либо из этих проверок завершится неудачно, основное приложение не сможет запуститься с **IllegalStateException.**

## Тестирование

**Spring Boot** предоставляет мощные инструменты для тестирования приложений. В дополнение к базовому тестированию, рассмотренному ранее, существуют специализированные аннотации для различных типов тестов.

### Интеграционное тестирование с @**DataJpaTest**

**Мы собираемся работать с сущностью с именем **Employee,** у которой в качестве свойств есть идентификатор и имя:**

```java
// JPA-сущность Employee для таблицы person
@Entity
@Table(name = "person")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Size(min = 3, max = 20)  // Ограничение длины имени
    private String name;
}
```

**А вот наш репозиторий с использованием Spring Data JPA:**

```java
// Spring Data JPA репозиторий для Employee
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Метод поиска по имени (Spring Data генерирует реализацию)
    public Employee findByName(String name);
}
```

Вот и все, что касается кода уровня сохраняемости. Теперь приступим к написанию нашего тестового класса.

**Сначала создадим скелет нашего тестового класса:**

```java
// Интеграционный тест репозитория с @DataJpaTest
@RunWith(SpringRunner.class)
@DataJpaTest
public class EmployeeRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;  // Для управления тестовыми данными
    
    @Autowired
    private EmployeeRepository employeeRepository;  // Тестируемый репозиторий
}
```

**`@RunWith` (**SpringRunner.class**)** обеспечивает мост между функциями тестирования **Spring Boot** и **JUnit.**

Эта аннотация потребуется всякий раз, когда мы используем какие-либо функции тестирования **Spring Boot** в наших тестах **JUnit.**

**@DataJpaTest** предоставляет некоторую стандартную настройку, необходимую для тестирования уровня сохраняемости:**

- настройка **H2**, базы данных в памяти
- настройка **Hibernate**, **Spring Data** и **DataSource**
- выполнение@**EntityScan**
- включение ведения журнала **SQL**

Для выполнения операций с БД нам нужны записи уже в нашей базе данных. Для настройки этих данных мы можем использовать **TestEntityManager**.

**Spring Boot TestEntityManager** — альтернатива стандартному **JPA EntityManager**, который предоставляет методы, обычно используемые при написании тестов.

**Теперь напишем наш первый тестовый пример:**

```java
// Тест поиска сотрудника по имени
@Test
public void whenFindByName_thenReturnEmployee() {
    // Подготовка тестовых данных
    Employee alex = new Employee("alex");
    entityManager.persist(alex);
    entityManager.flush();
    // Выполнение поиска и проверка результата
    Employee found = employeeRepository.findByName(alex.getName());
    assertThat(found.getName()).isEqualTo(alex.getName());
}
```

В приведенном выше тесте мы используем **TestEntityManager**, чтобы вставить сотрудника в базу данных и прочитать его через **API** поиска по имени.

### Модульное тестирование с @**WebMvcTest**

**Наш Контроллер зависит от уровня сервисов, давайте для простоты включим только один метод:**

```java
// REST-контроллер для работы с сотрудниками
@RestController
@RequestMapping("/api")
public class EmployeeRestController {
    @Autowired
    private EmployeeService employeeService;
    
    // Эндпоинт получения всех сотрудников
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
}
```

**Поскольку мы сосредоточены только на коде контроллера, естественно издеваться над кодом уровня сервиса для наших модульных тестов:**

```java
// Тест контроллера с MockMvc и моками сервисов
@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeRestController.class)
public class EmployeeRestControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;  // Мок HTTP-клиента
    
    @MockBean
    private EmployeeService service;  // Мок сервиса
}
```

Для тестирования контроллеров мы можем использовать **@WebMvcTest**. Он автоматически настроит инфраструктуру **Spring MVC** для наших модульных тестов.

В большинстве случаев **@WebMvcTest** будет ограничиваться загрузкой одного контроллера. Мы также можем использовать его вместе с **@MockBean**, чтобы предоставить фиктивные реализации для любых требуемых зависимостей.

**@WebMvcTest** также автоматически настраивает **MockMvc**, который предлагает мощный способ простого тестирования контроллеров **MVC** без запуска полного **HTTP**-сервера.

```java
// Тест эндпоинта получения сотрудников
@Test
public void givenEmployees_whenGetEmployees_thenReturnJsonArray() throws Exception {
    Employee alex = new Employee("alex");
    List<Employee> allEmployees = Arrays.asList(alex);
    // Настройка мока сервиса
    given(service.getAllEmployees()).willReturn(allEmployees);
    
    // Выполнение HTTP-запроса и проверка ответа
    mvc.perform(get("/api/employees")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is(alex.getName())));
}
```

### Автоматически настроенные тесты

Одна из замечательных особенностей автоматически настраиваемых аннотаций **Spring Boot** заключается в том, что они помогают загружать части всего приложения и тестовые слои кодовой базы.

**В дополнение к вышеупомянутым аннотациям, вот список нескольких широко используемых аннотаций:**

1. **@WebFluxTest**: мы можем использовать аннотацию @**WebFluxTest** для тестирования контроллеров **Spring WebFlux**. Он часто используется вместе с @**MockBean** для предоставления фиктивных реализаций требуемых зависимостей.
2. **@JdbcTest**: мы можем использовать @**JdbcTest** аннотации тесты **JPA** приложений, но для тестов, которые только требуют **DataSource**. Аннотация настраивает встроенную базу данных в памяти и **JdbcTemplate**.
3. **@JooqTest**: для тестирования тестов, связанных с **jOOQ**, мы можем использовать аннотацию @**JooqTest**, которая настраивает **DSLContext**.
4. **@DataMongoTest**: для тестирования приложений **MongoDB** полезной аннотацией является @**DataMongoTest**. По умолчанию он настраивает встроенный в память **MongoDB**, если драйвер доступен через зависимости, настраивает **MongoTemplate**, сканирует классы @**Document** и настраивает репозитории **Spring Data MongoDB**.
5. **@DataRedisTest** упрощает тестирование приложений **Redis**. Он сканирует классы @**RedisHash** и по умолчанию настраивает репозитории **Spring Data Redis**.
6. **@DataLdapTest** настраивает встроенный **LDAP** в памяти (**если доступен**), настраивает **LdapTemplate**, сканирует классы **@Entry** и по умолчанию настраивает репозитории **Spring Data LDAP**.
7. **@RestClientTest**: обычно мы используем аннотацию @**RestClientTest** для тестирования клиентов **REST**. Он автоматически настраивает различные зависимости, такие как поддержка **Jackson**, **GSON** и **Jsonb**; настраивает **RestTemplateBuilder**; и по умолчанию добавляет поддержку **MockRestServiceServer**.
8. **@JsonTest**: инициализирует контекст приложения **Spring** только теми компонентами, которые необходимы для тестирования сериализации **JSON**.

## Интеграционные тесты БД с **TestContainers**

**Spring Data JPA** предоставляет простой способ создавать запросы к базе данных и тестировать их с помощью встроенной базы данных **H2**.

Но в некоторых случаях тестирование на реальной базе данных намного выгоднее, особенно если мы используем запросы, зависящие от провайдера.

В этом руководстве мы покажем, как использовать **Testcontainers** для интеграционного тестирования с **Spring Data JPA** и базой данных **PostgreSQL**.

**Чтобы использовать базу данных **PostgreSQL** в наших тестах, мы должны добавить зависимость **Testcontainers** с областью тестирования:**

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.17.3</version>
    <scope>test</scope>
</dependency>
```

**Давайте также создадим файл **application.properties** в каталоге ресурсов теста, в котором мы проинструктируем **Spring** использовать правильный класс драйвера и создавать схему при каждом запуске теста:**

```properties
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
spring.jpa.hibernate.ddl-auto=create
```

**Чтобы начать использовать экземпляр **PostgreSQL** в одном тестовом классе, мы должны сначала создать определение контейнера, а затем использовать его параметры для установления соединения:**

```java
// Интеграционный тест с реальной PostgreSQL через TestContainers
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmployeeRepositoryTest {
    // Создание контейнера PostgreSQL для тестов
    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");
    
    // Динамическая конфигурация свойств datasource из контейнера
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
}
```

## Тестирование **REST** клиентов с @**RestClientTest**

Обычно мы используем аннотацию **@RestClientTest** для тестирования клиентов **REST**. Он автоматически настраивает различные зависимости, такие как поддержка **Jackson**, **GSON** и **Jsonb**; настраивает **RestTemplateBuilder**; и по умолчанию добавляет поддержку **MockRestServiceServer**.

Это позволяет нам автоматически подключать экземпляр **DetailsServiceClient** внутри нашего теста и оставлять все остальное снаружи, что ускоряет загрузку контекста.

Во-вторых — как **MockRestServiceServer** экземпляр также сконфигурирован для **@RestClientTest**-аннотированного теста (и связанный с **DetailsServiceClient**, например, для нас), мы можем просто внедрить его и использовать.

**Наконец -** поддержка **JSON** для **@RestClientTest** позволяет впрыскивать Джексон **ObjectMapper** экземпляр для подготовки **MockRestServiceServer** в макете значения ответа.

Все, что осталось сделать**, -** это выполнить звонок в нашу службу и проверить результаты.

## Логгирование

**Spring Boot** — очень полезный фреймворк. Это позволяет нам забыть о большинстве параметров конфигурации, многие из которых он самонадеянно настраивает автоматически.

В случае ведения журнала единственной обязательной зависимостью является ведение журнала **Apache Commons**.

Нам нужно импортировать его только при использовании **Spring 4.x** (**Spring `Boot 1`.x**), поскольку он предоставляется модулем **spring-jcl** в **Spring 5** (**Spring `Boot 2`.x**).

Нам не следует вообще беспокоиться об импорте **spring-jcl**, если мы используем **Spring Boot Starter** (что мы почти всегда используем). Это потому, что каждый стартер, как и наш **spring-boot-starter-web**, зависит от **spring-boot-starter-logging**, который уже подключает **spring-jcl** для нас.

При использовании стартеров по умолчанию для ведения журнала используется **Logback.**

**Spring Boot** предварительно настраивает его с помощью шаблонов и цветов **ANSI**, чтобы сделать стандартный вывод более читабельным. Уровень ведения журнала по умолчанию для регистратора предварительно установлен на **INFO**, что означает, что сообщения **TRACE** и **DEBUG** не видны.

### Настройка уровня логирования

**Чтобы активировать их без изменения конфигурации, мы можем передать аргументы **\-debug** или **\-trace** в командной строке:**

```bash
java -jar target/spring-boot-logging-0.0.1-SNAPSHOT.jar -trace
```

**Spring Boot** также дает нам доступ к более тонкой настройке уровня журнала через переменные среды.

Есть несколько способов добиться этого.

**Во-первых, мы можем установить уровень ведения журнала в параметрах виртуальной машины:**

```bash
-Dlogging.level.org.springframework=TRACE
-Dlogging.level.com.baeldung=TRACE
```

**В качестве альтернативы, если мы используем **Maven,** мы можем определить наши настройки журнала через командную строку:**

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE,--logging.level.com.baeldung=TRACE
```

При работе с **Gradle** мы можем передавать настройки журнала через командную строку. Для этого потребуется настроить задачу **bootRun**.

Если мы хотим навсегда изменить **logging,** мы можем сделать это в файле **application.properties:**

```properties
logging.level.root=WARN
logging.level.com.baeldung=TRACE
```

Наконец, мы можем изменить уровень ведения журнала навсегда, используя файл конфигурации нашей платформы ведения журнала.

### Использование **Lombok** для логирования

В примерах, которые мы видели до сих пор, нам нужно было объявить экземпляр регистратора из нашей среды ведения журнала.

Этот шаблонный код может раздражать. Мы можем избежать этого, используя различные аннотации, введенные **Lombok**ом.

**Сначала нам нужно добавить зависимость **Lombok** в наш скрипт сборки, чтобы с ней работать:**

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
    <scope>provided</scope>
</dependency>
```

**SLF4J** и **API** ведения журналов **Apache Commons** позволяют гибко изменять структуру ведения журналов, не влияя на наш код.

И мы можем использовать аннотации **Lombok `@Slf4`j** и **@CommonsLog** для добавления нужный экземпляр регистратора в наш класс: **org.slf4j.Logger** для **SLF4J** и **org.`apache.commons.logging`.Log** для ведения журнала **Apache Commons**.

**Чтобы увидеть эти аннотации в действии, давайте создадим класс, аналогичный **LoggingController**, но без экземпляра регистратора. Мы называем его **LombokLoggingController** и аннотируем его **@Slf4j**:**

```java
// Контроллер с логированием через Lombok @Slf4j
@RestController
@Slf4j
public class LombokLoggingController {
    @RequestMapping("/lombok")
    public String index() {
        // Примеры логирования на разных уровнях
        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");
        return "Howdy! Check out the Logs to see the output...";
    }
}
```

## Приложение как **Service** (**Linux**)

Чтобы превратить приложение **Spring Boot** в службу **Linux**, нам нужно создать скрипт инициализации. В зависимости от дистрибутива **Linux** это может быть **systemd**, **init.d** или **Upstart**.

**Создаем задание **your-app.conf** для запуска нашего приложения **Spring Boot**:**

```bash
# Place in /home/{user}/.config/upstart
description "Some Spring Boot application"
respawn # attempt service restart if stops abruptly
exec java -jar /path/to/your-app.jar
```

Теперь запустите **«start `your-app`»**, и ваша служба запустится.

**Upstart** предлагает множество вариантов конфигурации заданий, большинство из которых вы можете найти здесь.

## Spring Boot 3 и Spring Framework 6.0

До выхода **Spring `Boot` 3** осталось всего **3** месяца. **Spring `Framework 6`.0**, вероятно, появится незадолго до **Spring `Boot` 3**. Так что сейчас самое время проверить, что нового.

Хотя ранее уже была поддержка **Java 17,** эта версия **LTS** теперь получает базовый уровень.

### Новые функции **Java**

При переходе с **LTS** версии **11** разработчики **Java** получают преимущества от новых языковых функций. Поскольку в этой статье **Java** сама по себе не является темой, давайте только назовем самые важные новые функции для разработчиков **Spring Boot**.

#### **Records**

**Record Java** были введены с намерением использовать их в качестве быстрого способа создания классов носителей данных, то есть классов, целью которых является простое хранение данных и перенос их между модулями, также известные как **POJO** и **DTO**.

Мы можем легко создавать неизменяемые **DTO:**

```java
// Java Record - неизменяемый DTO с автоматическими геттерами
public record Person (String name, String address) {}
```

В настоящее время нам нужно быть осторожными при объединении их с проверкой **Bean `Validation`,** потому что ограничения проверки не поддерживаются для аргументов конструктора, например, когда экземпляр создается при десериализации **JSON (**Jackson**)** и помещается в метод контроллера в качестве параметра.

#### Текстовые блоки

**С **JEP 378** теперь можно создавать многострочные текстовые блоки без необходимости конкатенации строк на разрывах строк:**

```java
// Текстовые блоки (Java 15+) для многострочных строк
String textBlock = """
    Hello, this is a
    multi-line
    text block.
    """;
```

#### **Switch** выражения

В **Java 12** появились выражения переключения, которые (**как и все выражения**) оценивают одно значение и могут использоваться в операторах. Вместо объединения вложенных операторов **if-else (?:)** теперь мы можем использовать конструкцию **switch-case**:

```java
// Switch-выражение (Java 14+) возвращает значение
DayOfWeek day = DayOfWeek.FRIDAY;
int numOfLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY -> 7;
    case THURSDAY, SATURDAY -> 8;
    case WEDNESDAY -> 9;
};
```

#### **Pattern Matching**

Сопоставление с образцом было разработано в проекте **Amber** и нашло своё применение в языке **Java**. В случае языка **Java** они могут помочь упростить код для вычислений **instanceof**.

Мы можем использовать их напрямую с **instanceof:**

```java
// Pattern Matching для instanceof (Java 16+)
if (obj instanceof String s) {
    System.out.println(s.toLowerCase());  // Переменная s уже типа String
}
```

**Мы также можем использовать его в операторе **switch-case**:**

```java
// Pattern Matching в switch (Java 21)
static double getDoubleUsingSwitch(Object o) {
    return switch (o) {
        case Integer i -> i.doubleValue();  // Проверка типа и приведение
        case Float f -> f.doubleValue();
        case String s -> Double.parseDouble(s);
        default -> 0d;
    };
}
```

#### **Sealed** классы

**Sealed** классы могут ограничить наследование, указав разрешенные подклассы:**

```java
// Sealed класс (Java 17) - только Dog и Cat могут наследовать
public abstract sealed class Pet permits Dog, Cat {}
```

### Миграция на **Jakarta** `EE`

Наиболее важным критическим изменением может быть переход от **Java EE** к **Jakarta `EE` 9**, где пространство имен пакетов изменилось с **javax.** на **jakarta.**. Поэтому нам нужно настроить весь импорт в нашем коде всякий раз, когда мы используем классы из **Java EE** напрямую.

**Например, когда мы обращаемся к объекту **HttpServletRequest** в вашем контроллере **Spring MVC**, нам нужно заменить:**

```java
// Старый импорт Java EE (до Spring Boot 3)
import javax.servlet.http.HttpServletRequest;
```

**на:**

```java
// Новый импорт Jakarta EE 9 (Spring Boot 3+)
import jakarta.servlet.http.HttpServletRequest;
```

## Продвинутые возможности **Spring Boot**

### Микросервисы с **Spring Boot**

```java
// Микросервис с Eureka, Feign и Hystrix
@SpringBootApplication
@EnableEurekaClient     // Регистрация в Eureka Server
@EnableFeignClients     // Включение Feign клиентов
@EnableHystrix          // Включение Circuit Breaker
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    // RestTemplate с балансировкой нагрузки через Ribbon
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping
    @HystrixCommand(fallbackMethod = "createOrderFallback")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        // Проверка наличия товара через другой микросервис
        ProductDTO product = restTemplate.getForObject(
                "http://product-service/api/products/" + request.getProductId(),
                ProductDTO.class);

        if (product == null) {
            throw new ProductNotFoundException(request.getProductId());
        }

        // Проверка пользователя через другой микросервис
        UserDTO user = restTemplate.getForObject(
                "http://user-service/api/users/" + request.getUserId(),
                UserDTO.class);

        OrderDTO order = orderService.create(request, product, user);
        return ResponseEntity.created(buildLocationUri(order.getId())).body(order);
    }

    public ResponseEntity<OrderDTO> createOrderFallback(CreateOrderRequest request) {
        log.warn("Fallback: Unable to create order for product: {}", request.getProductId());
        throw new ServiceUnavailableException("Order service temporarily unavailable");
    }
}

// Feign клиент для межсервисного взаимодействия
@FeignClient(name = "product-service", fallback = ProductServiceFallback.class)
public interface ProductServiceClient {

    @GetMapping("/api/products/{id}")
    ProductDTO getProduct(@PathVariable("id") Long id);

    @PostMapping("/api/products/{id}/reserve")
    void reserveProduct(@PathVariable("id") Long id, @RequestBody ReservationRequest request);
}

@Component
@Slf4j
public class ProductServiceFallback implements ProductServiceClient {

    @Override
    public ProductDTO getProduct(Long id) {
        log.warn("Product service unavailable, returning default product for id: {}", id);
        return ProductDTO.builder()
                .id(id)
                .name("Unknown Product")
                .price(BigDecimal.ZERO)
                .build();
    }

    @Override
    public void reserveProduct(Long id, ReservationRequest request) {
        log.warn("Product service unavailable, cannot reserve product: {}", id);
        throw new ServiceUnavailableException("Product service unavailable");
    }
}
```

### **Reactive Spring Boot**

```java
// Реактивное приложение Spring Boot с WebFlux
@SpringBootApplication
public class ReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/reactive")
public class ReactiveController {

    @Autowired
    private ReactiveUserService userService;

    @GetMapping("/users")
    public Flux<UserDTO> getAllUsers() {
        return userService.findAll()
                .map(this::convertToDTO);
    }

    @GetMapping("/users/{id}")
    public Mono<ResponseEntity<UserDTO>> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public Mono<ResponseEntity<UserDTO>> createUser(@Valid @RequestBody Mono<UserDTO> userDTOMono) {
        return userDTOMono
                .map(this::convertToEntity)
                .flatMap(userService::save)
                .map(this::convertToDTO)
                .map(savedUser -> ResponseEntity.created(
                        buildLocationUri(savedUser.getId())).body(savedUser));
    }

    @GetMapping(value = "/users/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<UserDTO> streamUsers() {
        return userService.findAll()
                .map(this::convertToDTO)
                .delayElements(Duration.ofSeconds(1));
    }
}

@Service
public class ReactiveUserService {

    @Autowired
    private ReactiveUserRepository userRepository;

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    public Flux<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public Mono<Long> count() {
        return userRepository.count();
    }
}

@Repository
public interface ReactiveUserRepository extends ReactiveCrudRepository<User, Long> {

    @Query("SELECT * FROM users WHERE name = :name")
    Flux<User> findByName(@Param("name") String name);

    @Query("SELECT * FROM users WHERE email LIKE :emailPattern")
    Flux<User> findByEmailPattern(@Param("emailPattern") String emailPattern);
}
```

### **Spring Boot** с **Kotlin**

```kotlin
@SpringBootApplication
@EnableJpaAuditing
class KotlinSpringBootApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringBootApplication>(*args)
}

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val userMapper: UserMapper
) {

    @GetMapping
    fun getAllUsers(
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 20,
        @RequestParam sort: String = "name"
    ): ResponseEntity<Page<UserDTO>> {
        val pageable = PageRequest.of(page, size, Sort.by(sort))
        val users = userService.findAll(pageable)
        val userDTOs = users.map { userMapper.toDTO(it) }
        return ResponseEntity.ok(userDTOs)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<UserDTO> {
        return userService.findById(id)
            ?.let { userMapper.toDTO(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<UserDTO> {
        val user = userMapper.toEntity(request)
        val savedUser = userService.save(user)
        val userDTO = userMapper.toDTO(savedUser)

        return ResponseEntity.created(buildLocationUri(userDTO.id!!)).body(userDTO)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @Valid @RequestBody request: UpdateUserRequest): ResponseEntity<UserDTO> {
        val updatedUser = userService.update(id, request)
        val userDTO = userMapper.toDTO(updatedUser)
        return ResponseEntity.ok(userDTO)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun findAll(pageable: Pageable): Page<User> = userRepository.findAll(pageable)

    fun findById(id: Long): User? = userRepository.findById(id).orElse(null)

    fun save(user: User): User {
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    fun update(id: Long, request: UpdateUserRequest): User {
        val existingUser = userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id) }

        return existingUser.apply {
            name = request.name ?: name
            email = request.email ?: email
            request.password?.let { password = passwordEncoder.encode(it) }
        }.let { userRepository.save(it) }
    }

    fun deleteById(id: Long) {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException(id)
        }
        userRepository.deleteById(id)
    }
}

data class CreateUserRequest(
    @field:NotBlank val name: String,
    @field:Email val email: String,
    @field:NotBlank val password: String
)

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null
)

data class UserDTO(
    val id: Long?,
    val name: String,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

@Component
class UserMapper {
    fun toDTO(user: User): UserDTO = UserDTO(
        id = user.id,
        name = user.name,
        email = user.email,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt
    )

    fun toEntity(request: CreateUserRequest): User = User(
        name = request.name,
        email = request.email,
        password = request.password
    )
}
```

### **Cloud Native Spring Boot**

```java
// Cloud Native приложение: Eureka, Feign, Circuit Breaker, Async
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableAsync
public class CloudNativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudNativeApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

    @Bean
    public AlwaysSampler alwaysSampler() {
        return new AlwaysSampler();
    }
}

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.ofDefaults();
    }

    @Bean
    public BulkheadRegistry bulkheadRegistry() {
        return BulkheadRegistry.ofDefaults();
    }

    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.ofDefaults();
    }
}

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private Tracer tracer;

    @GetMapping("/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    @Bulkhead(name = "productService", fallbackMethod = "getProductBulkheadFallback")
    @TimeLimiter(name = "productService")
    public CompletableFuture<ProductDTO> getProduct(@PathVariable Long id) {
        Span span = tracer.buildSpan("getProduct").start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            span.tag("product.id", id.toString());

            return CompletableFuture.completedFuture(productService.findById(id))
                    .thenApply(this::enrichWithRecommendations)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            span.tag("error", true);
                            span.log(Map.of("error.message", throwable.getMessage()));
                        } else {
                            span.tag("product.found", true);
                        }
                        span.finish();
                    });
        }
    }

    public CompletableFuture<ProductDTO> getProductFallback(Long id, Exception e) {
        log.warn("Circuit breaker activated for product: {}", id, e);
        return CompletableFuture.completedFuture(
            ProductDTO.builder()
                .id(id)
                .name("Product temporarily unavailable")
                .price(BigDecimal.ZERO)
                .build()
        );
    }

    public CompletableFuture<ProductDTO> getProductBulkheadFallback(Long id, BulkheadFullException e) {
        log.warn("Bulkhead full for product: {}", id, e);
        throw new ServiceUnavailableException("Service is under high load");
    }

    @PostMapping
    @Retry(name = "productService")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Span span = tracer.buildSpan("createProduct").start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            span.tag("operation", "create");

            ProductDTO product = productService.create(request);

            span.tag("product.id", product.getId().toString());
            span.finish();

            return ResponseEntity.created(buildLocationUri(product.getId())).body(product);
        }
    }

    @GetMapping("/search")
    @Cacheable(value = "productSearch", key = "#query + '_' + #page + '_' + #size")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Span span = tracer.buildSpan("searchProducts").start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            span.tag("search.query", query);
            span.tag("search.page", String.valueOf(page));

            Pageable pageable = PageRequest.of(page, size);
            Page<ProductDTO> products = productService.search(query, pageable);

            span.tag("search.results", String.valueOf(products.getTotalElements()));
            span.finish();

            return ResponseEntity.ok(products);
        }
    }

    private ProductDTO enrichWithRecommendations(ProductDTO product) {
        // Добавление рекомендаций через другой сервис
        List<ProductDTO> recommendations = getRecommendations(product.getId());
        product.setRecommendations(recommendations);
        return product;
    }

    @Cacheable(value = "recommendations", key = "#productId")
    public List<ProductDTO> getRecommendations(Long productId) {
        // Логика получения рекомендаций
        return Collections.emptyList();
    }
}

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10)))
                .build();
    }

    @Bean
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .weakKeys()
                .recordStats());
        return cacheManager;
    }
}

@Configuration
public class ObservabilityConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCustomizer() {
        return registry -> registry.config()
                .commonTags("application", "product-service")
                .commonTags("version", "1.0.0");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

### Мониторинг и **Observability**

```java
// Конфигурация метрик и таймингов для Micrometer
@Configuration
public class MonitoringConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> customize() {
        return registry -> {
            registry.config()
                    .commonTags("application", "spring-boot-app")
                    .commonTags("version", "${info.app.version:unknown}")
                    .commonTags("environment", "${spring.profiles.active:default}");
        };
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}

@RestController
@RequestMapping("/api/metrics")
public class CustomMetricsController {

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping("/events")
    public ResponseEntity<Void> recordEvent(@RequestBody EventDTO event) {
        // Кастомные метрики
        Counter.builder("events.processed")
                .tag("type", event.getType())
                .tag("source", event.getSource())
                .register(meterRegistry)
                .increment();

        Timer.builder("event.processing.time")
                .tag("type", event.getType())
                .register(meterRegistry)
                .record(() -> processEvent(event));

        Gauge.builder("active.users", this, CustomMetricsController::getActiveUsersCount)
                .register(meterRegistry);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<MetricsDashboard> getDashboard() {
        MetricsDashboard dashboard = MetricsDashboard.builder()
                .totalRequests(getCounterValue("http.server.requests"))
                .activeConnections(getGaugeValue("hikaricp.connections.active"))
                .memoryUsage(getGaugeValue("jvm.memory.used"))
                .uptime(getTimerValue("application.started.time"))
                .build();

        return ResponseEntity.ok(dashboard);
    }

    private double getCounterValue(String name) {
        Counter counter = meterRegistry.find(name).counter();
        return counter != null ? counter.count() : 0.0;
    }

    private double getGaugeValue(String name) {
        Gauge gauge = meterRegistry.find(name).gauge();
        return gauge != null ? gauge.value() : 0.0;
    }

    private double getTimerValue(String name) {
        Timer timer = meterRegistry.find(name).timer();
        return timer != null ? timer.totalTime(TimeUnit.SECONDS) : 0.0;
    }

    private long getActiveUsersCount() {
        // Логика подсчета активных пользователей
        return 42L; // Заглушка
    }
}

@Service
@Slf4j
public class HealthCheckService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    @Scheduled(fixedRate = 30000) // Каждые 30 секунд
    public void performHealthChecks() {
        // Проверка внешних сервисов
        checkExternalServices();

        // Проверка базы данных
        checkDatabaseConnectivity();

        // Проверка дискового пространства
        checkDiskSpace();

        // Проверка памяти
        checkMemoryUsage();
    }

    private void checkExternalServices() {
        // Проверка зависимых сервисов
        List<String> services = Arrays.asList(
            "http://user-service/health",
            "http://product-service/health",
            "http://order-service/health"
        );

        for (String serviceUrl : services) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(serviceUrl, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.debug("Service {} is healthy", serviceUrl);
                } else {
                    log.warn("Service {} returned status {}", serviceUrl, response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Service {} is not accessible", serviceUrl, e);
            }
        }
    }

    private void checkDatabaseConnectivity() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                log.debug("Database connection is healthy");
            } else {
                log.warn("Database connection is not valid");
            }
        } catch (SQLException e) {
            log.error("Database connectivity check failed", e);
        }
    }

    private void checkDiskSpace() {
        File file = new File("/");
        long freeSpace = file.getFreeSpace();
        long totalSpace = file.getTotalSpace();
        double freePercentage = (double) freeSpace / totalSpace * 100;

        if (freePercentage < 10) {
            log.warn("Low disk space: {}% free", String.format("%.2f", freePercentage));
        } else {
            log.debug("Disk space OK: {}% free", String.format("%.2f", freePercentage));
        }
    }

    private void checkMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double usedPercentage = (double) usedMemory / totalMemory * 100;

        if (usedPercentage > 90) {
            log.warn("High memory usage: {}% used", String.format("%.2f", usedPercentage));
        } else {
            log.debug("Memory usage OK: {}% used", String.format("%.2f", usedPercentage));
        }
    }
}
```

## Производительность и оптимизация

### **JVM** оптимизации для **Spring Boot**

```properties
# application.properties для production
# JVM оптимизации
java.runtime.version=17
spring.jmx.enabled=false

# Connection pooling
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# HTTP server оптимизации
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10
server.tomcat.connection-timeout=20s
server.tomcat.keep-alive-timeout=20s

# Logging оптимизации
logging.level.root=INFO
logging.level.com.example=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Cache оптимизации
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=10m

# Actuator оптимизации
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true
```

### Асинхронная обработка

```java
// Приложение с асинхронным выполнением и планировщиком
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AsyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncApplication.class, args);
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}

@Service
@Slf4j
public class AsyncService {

    @Async
    public CompletableFuture<String> processAsync(String input) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Processing {} asynchronously", input);
            simulateWork(2000); // Имитация работы
            return "Processed: " + input.toUpperCase();
        });
    }

    @Async
    public void sendEmailAsync(String email, String content) {
        log.info("Sending email to {} asynchronously", email);
        simulateWork(1000); // Имитация отправки email
        log.info("Email sent to {}", email);
    }

    @Async
    @Retryable(value = {RemoteServiceException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void callExternalService(String data) {
        log.info("Calling external service with data: {}", data);
        // Имитация вызова внешнего сервиса с возможными ошибками
        if (Math.random() > 0.7) {
            throw new RemoteServiceException("Service temporarily unavailable");
        }
        simulateWork(500);
    }

    private void simulateWork(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

@RestController
@RequestMapping("/api/async")
@Slf4j
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @PostMapping("/process")
    public CompletableFuture<ResponseEntity<String>> processAsync(@RequestBody String input) {
        return asyncService.processAsync(input)
                .thenApply(result -> ResponseEntity.ok(result))
                .exceptionally(throwable -> {
                    log.error("Async processing failed", throwable);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Processing failed: " + throwable.getMessage());
                });
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        asyncService.sendEmailAsync(request.getEmail(), request.getContent());
        return ResponseEntity.accepted().body("Email queued for sending");
    }

    @PostMapping("/external")
    public ResponseEntity<String> callExternal(@RequestBody String data) {
        asyncService.callExternalService(data);
        return ResponseEntity.accepted().body("External service call initiated");
    }

    @GetMapping("/batch")
    public CompletableFuture<ResponseEntity<List<String>>> processBatch(@RequestParam List<String> items) {
        List<CompletableFuture<String>> futures = items.stream()
                .map(asyncService::processAsync)
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .thenApply(results -> ResponseEntity.ok(results));
    }
}

@Configuration
public class SchedulingConfig {

    @Autowired
    private AsyncService asyncService;

    @Scheduled(fixedRate = 60000) // Каждую минуту
    public void scheduledTask() {
        log.info("Running scheduled task");
        asyncService.callExternalService("scheduled-data");
    }

    @Scheduled(cron = "0 0 2 * * ?") // Каждый день в 2:00
    public void nightlyCleanup() {
        log.info("Running nightly cleanup");
        // Логика очистки
    }
}
```

### Кэширование

```java
// Кеш на Caffeine с кастомным KeyGenerator
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .weakKeys()
                .recordStats());

        return cacheManager;
    }

    @Bean
    public KeyGenerator customKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder key = new StringBuilder();
            key.append(target.getClass().getSimpleName())
               .append("_")
               .append(method.getName())
               .append("_")
               .append(Arrays.toString(params));
            return key.toString();
        };
    }
}

@Service
@CacheConfig(cacheNames = "users")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(key = "#id")
    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Cacheable(key = "#email")
    public UserDTO findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Cacheable(key = "#page + '_' + #size + '_' + T(java.util.Arrays).toString(#sort)")
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @CacheEvict(key = "#user.id")
    @CacheEvict(key = "#user.email", condition = "#user.email != null")
    public UserDTO save(UserDTO user) {
        User entity = convertToEntity(user);
        User saved = userRepository.save(entity);
        return convertToDTO(saved);
    }

    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @CacheEvict(allEntries = true)
    public void evictAll() {
        // Очистка всего кэша пользователей
    }

    @Caching(evict = {
        @CacheEvict(value = "users", key = "#user.id"),
        @CacheEvict(value = "departments", key = "#user.department.id")
    })
    public UserDTO updateUser(UserDTO user) {
        User entity = convertToEntity(user);
        User updated = userRepository.save(entity);
        return convertToDTO(updated);
    }

    private UserDTO convertToDTO(User user) {
        // Конвертация
        return new UserDTO();
    }

    private User convertToEntity(UserDTO dto) {
        // Конвертация
        return new User();
    }
}

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private UserService userService;

    @GetMapping("/stats")
    public ResponseEntity<CacheStats> getCacheStats() {
        // Получение статистики кэша
        Cache userCache = cacheManager.getCache("users");
        if (userCache instanceof CaffeineCache) {
            com.github.benmanes.caffeine.cache.stats.CacheStats stats =
                ((CaffeineCache) userCache).getNativeCache().stats();

            CacheStats cacheStats = CacheStats.builder()
                    .hitCount(stats.hitCount())
                    .missCount(stats.missCount())
                    .evictionCount(stats.evictionCount())
                    .loadCount(stats.loadCount())
                    .hitRate(stats.hitRate())
                    .build();

            return ResponseEntity.ok(cacheStats);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/evict/{cacheName}")
    public ResponseEntity<Void> evictCache(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

## Развертывание

### **Docker** с **Spring Boot**

```dockerfile
# Multi-stage Dockerfile для Spring Boot
FROM maven:3.8.6-openjdk-17-slim AS build

WORKDIR /app

# Копирование файлов проекта
COPY pom.xml .
COPY src ./src

# Сборка приложения
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Установка дополнительных инструментов
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        dumb-init && \
    rm -rf /var/lib/apt/lists/*

# Создание пользователя
RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

# Копирование JAR файла
COPY --from=build /app/target/*.jar app.jar

# Изменение прав
RUN chown -R spring:spring /app

# Переключение на пользователя без привилегий
USER spring

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Использование dumb-init для правильной обработки сигналов
ENTRYPOINT ["dumb-init", "--"]

# JVM оптимизации для контейнеров
CMD ["java", \
     "-XX:+UseContainerSupport", \
     "-XX:MaxRAMPercentage=75.0", \
     "-XX:+UseG1GC", \
     "-XX:+UseCompressedOops", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-jar", \
     "app.jar"]
```

```yaml
# docker-compose.yml для Spring Boot приложения
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/app
      - SPRING_DATASOURCE_USERNAME=app
      - SPRING_DATASOURCE_PASSWORD=password
      - SPRING_REDIS_HOST=redis
    depends_on:
      - db
      - redis
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    restart: unless-stopped

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=app
      - POSTGRES_USER=app
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d app"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: unless-stopped

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml:ro
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
    restart: unless-stopped

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana
    restart: unless-stopped

volumes:
  postgres_data:
  redis_data:
  grafana_data:
```

### **Kubernetes** развертывание

```yaml
# Deployment для Spring Boot приложения
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-boot-app
  labels:
    app: spring-boot-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-boot-app
  template:
    metadata:
      labels:
        app: spring-boot-app
    spec:
      containers:
      - name: app
        image: my-registry/spring-boot-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "k8s"
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        - name: JAVA_OPTS
          value: "-Xmx512m -Xms256m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 3
          failureThreshold: 3
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
      imagePullSecrets:
      - name: registry-secret

---
# Service
apiVersion: v1
kind: Service
metadata:
  name: spring-boot-service
spec:
  selector:
    app: spring-boot-app
  ports:
  - port: 80
    targetPort: 8080
    protocol: TCP
  type: ClusterIP

---
# Ingress
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: spring-boot-ingress
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - api.example.com
    secretName: spring-boot-tls
  rules:
  - host: api.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: spring-boot-service
            port:
              number: 80

---
# ConfigMap для конфигурации
apiVersion: v1
kind: ConfigMap
metadata:
  name: spring-boot-config
data:
  application-k8s.yml: |
    spring:
      datasource:
        url: jdbc:postgresql://postgres-service:5432/app
      redis:
        host: redis-service
      kafka:
        bootstrap-servers: kafka-service:9092
    management:
      endpoints:
        web:
          exposure:
            include: health,info,metrics,prometheus
      metrics:
        export:
          prometheus:
            enabled: true

---
# Horizontal Pod Autoscaler
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: spring-boot-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: spring-boot-app
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

## Лучшие практики

### 1. Структура проекта

```text
src/main/java/com/example/
├── Application.java              # Main application class
├── config/                       # Configuration classes
│   ├── DatabaseConfig.java
│   ├── SecurityConfig.java
│   └── CacheConfig.java
├── controller/                   # REST controllers
│   ├── UserController.java
│   └── ProductController.java
├── service/                      # Business logic
│   ├── UserService.java
│   └── ProductService.java
├── repository/                   # Data access
│   ├── UserRepository.java
│   └── ProductRepository.java
├── model/                        # Domain models
│   ├── User.java
│   └── Product.java
├── dto/                          # Data transfer objects
│   ├── UserDTO.java
│   └── ProductDTO.java
└── exception/                    # Custom exceptions
    ├── UserNotFoundException.java
    └── ValidationException.java
```

### 2. Конфигурация

```java
// Production: primary/readonly DataSource и роутинг
@Configuration
@Profile("production")
public class ProductionConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "app.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "app.datasource.readonly")
    public DataSource readOnlyDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public RoutingDataSource routingDataSource(
            @Qualifier("primaryDataSource") DataSource primary,
            @Qualifier("readOnlyDataSource") DataSource readOnly) {

        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("primary", primary);
        targetDataSources.put("readonly", readOnly);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(primary);

        return routingDataSource;
    }
}

@Component
public class DataSourceRouter implements RoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        // Логика определения источника данных
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ?
               "readonly" : "primary";
    }
}
```

### 3. Обработка ошибок

```java
// Глобальный перехват ValidationException и формирование ErrorResponse
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException e, WebRequest request) {

        log.warn("Validation error: {}", e.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(BAD_REQUEST.value())
                .error("Validation Failed")
                .message(e.getMessage())
                .path(getRequestPath(request))
                .validationErrors(extractValidationErrors(e))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException e, WebRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(NOT_FOUND.value())
                .error("Resource Not Found")
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.status(NOT_FOUND).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException e, WebRequest request) {

        log.error("Data integrity violation", e);

        String userMessage = extractUserFriendlyMessage(e);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(CONFLICT.value())
                .error("Data Integrity Error")
                .message(userMessage)
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.status(CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception e, WebRequest request) {

        log.error("Unexpected error", e);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "unknown";
    }

    private Map<String, String> extractValidationErrors(ValidationException e) {
        // Извлечение ошибок валидации
        return Collections.emptyMap();
    }

    private String extractUserFriendlyMessage(DataIntegrityViolationException e) {
        // Преобразование технических ошибок в понятные сообщения
        String message = e.getMostSpecificCause().getMessage();
        if (message.contains("duplicate key")) {
            return "A record with this information already exists";
        }
        return "Data constraint violation";
    }
}
```

### 4. Тестирование

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class IntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertySource registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    @Test
    public void fullIntegrationTest() {
        // Создание пользователя через API
        UserDTO userDTO = UserDTO.builder()
                .name("Integration Test")
                .email("integration@example.com")
                .build();

        ResponseEntity<UserDTO> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/users", userDTO, UserDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Проверка в базе данных
        UserDTO createdUser = createResponse.getBody();
        Optional<User> savedUser = userRepository.findById(createdUser.getId());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("integration@example.com");

        // Получение через API
        ResponseEntity<UserDTO> getResponse = restTemplate.getForEntity(
                createResponse.getHeaders().getLocation(), UserDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("Integration Test");
    }
}
```

### 5. Мониторинг

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/metrics/custom")
    public ResponseEntity<Map<String, Object>> getCustomMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // HTTP метрики
        Counter httpRequests = meterRegistry.find("http.server.requests").counter();
        metrics.put("totalHttpRequests", httpRequests != null ? httpRequests.count() : 0);

        // Бизнес метрики
        Counter ordersCreated = meterRegistry.find("orders.created").counter();
        metrics.put("totalOrdersCreated", ordersCreated != null ? ordersCreated.count() : 0);

        // Производительность
        Timer orderCreationTimer = meterRegistry.find("order.creation.time").timer();
        if (orderCreationTimer != null) {
            metrics.put("averageOrderCreationTime", orderCreationTimer.mean(TimeUnit.MILLISECONDS));
            metrics.put("orderCreationCount", orderCreationTimer.count());
        }

        return ResponseEntity.ok(metrics);
    }

    @PostMapping("/maintenance/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> performMaintenance() {
        // Логика обслуживания
        maintenanceService.performCleanup();

        return ResponseEntity.ok("Maintenance completed successfully");
    }
}
```

## Решение проблем

| Проблема | Возможная причина | Действие |
|----------|-------------------|----------|
| Приложение не запускается | Конфликт портов, неверная конфигурация | Проверить `server.port`, логи при старте |
| Bean не найден | Отсутствует `@Component`/`@Service`, неправильный `@ComponentScan` | Добавить аннотации, проверить пакеты |
| Ошибки подключения к БД | Неверный connection string, БД недоступна | Проверить `spring.datasource.*`, доступность БД |
| 404 на endpoints | Неверный `@RequestMapping`, порядок конфигурации | Проверить пути, порядок `WebMvcConfigurer` |
| Медленные запросы | N+1, отсутствие индексов | Проверить логирование SQL, профилирование |

### Заключение

**Spring Boot** предоставляет мощную платформу для быстрой разработки **production-ready** приложений. Он значительно упрощает создание **enterprise-grade** приложений, предоставляя:**

- **Авто-конфигурацию** для быстрого старта
- **Встроенные решения** для распространенных задач
- **Мониторинг и метрики** из коробки
- **Гибкость** для кастомизации под специфические нужды
- **Обширную экосистему starter**'ов и интеграций

Ключ к успешному использованию **Spring Boot** - понимание его возможностей и правильное применение **best practices** в конкретном контексте проекта.
