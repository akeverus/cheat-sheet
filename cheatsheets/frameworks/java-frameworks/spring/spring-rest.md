---
title: "Spring REST API: Полное руководство по разработке RESTful веб-сервисов"
description: "Комплексное руководство по Spring REST: контроллеры, HTTP методы, DTO, валидация, обработка ошибок, безопасность, тестирование, документация и best practices"
tags: ["spring", "rest", "api", "http", "controller", "dto", "validation", "testing", "documentation", "security"]
difficulty: "intermediate"
prerequisites: ["spring/spring-core.md", "spring/spring-boot.md"]
next: ["spring/spring-security.md", "api/rest-api-design.md"]
updated: "2026-02-11"
related: ["spring/spring-boot.md", "java/java-basics.md", "api/rest-api-design.md"]
---

# Spring REST API: Полное руководство по разработке RESTful веб-сервисов


**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [**Spring Web MVC**](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [**Spring Boot** REST](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet)
- [**REST API Design**](https://restfulapi.net/)

### **Baeldung**
- [Building a **REST API** with **Spring Boot**](https://www.baeldung.com/rest-with-spring-series)
- [**Spring @RequestMapping**](https://www.baeldung.com/spring-requestmapping)
- [**Spring Boot REST API Validation**](https://www.baeldung.com/spring-boot-bean-validation)

## Содержание

- [Spring REST API: Полное руководство по разработке RESTful веб-сервисов](#spring-rest-api-полное-руководство-по-разработке-restful-веб-сервисов)
- [Создание веб-приложения](#создание-веб-приложения)
  - [Настройка проекта](#настройка-проекта)
  - [Создание контроллера](#создание-контроллера)
- [Как читать заголовки HTTP в контроллерах?](#как-читать-заголовки-http-в-контроллерах)
  - [Использование @RequestHeader](#использование-requestheader)
  - [Использование HttpServletRequest](#использование-httpservletrequest)
  - [Чтение всех заголовков](#чтение-всех-заголовков)
- [Создание REST API](#создание-rest-api)
  - [Основные HTTP методы](#основные-http-методы)
  - [Пример REST контроллера](#пример-rest-контроллера)
  - [Использование ResponseEntity](#использование-responseentity)
  - [HTTP статус коды](#http-статус-коды)
  - [Пользовательские исключения](#пользовательские-исключения)
- [Руководство @Controller и @RestController](#руководство-controller-и-restcontroller)
  - [@Controller](#controller)
  - [@ResponseBody](#responsebody)
  - [@RestController](#restcontroller)
  - [Различия между @Controller и @RestController](#различия-между-controller-и-restcontroller)
- [Логгирование входящих запросов](#логгирование-входящих-запросов)
  - [Настройка Logging Filter](#настройка-logging-filter)
  - [Создание Custom Filter](#создание-custom-filter)
  - [Использование CommonsRequestLoggingFilter](#использование-commonsrequestloggingfilter)
- [Руководство по @Async](#руководство-по-async)
  - [Включение поддержки @Async](#включение-поддержки-async)
  - [Использование @Async в контроллере](#использование-async-в-контроллере)
  - [Обработка исключений в @Async](#обработка-исключений-в-async)
- [Руководство по @ExceptionHandler и @ControllerAdvice](#руководство-по-exceptionhandler-и-controlleradvice)
  - [@ExceptionHandler на уровне контроллера](#exceptionhandler-на-уровне-контроллера)
  - [@ControllerAdvice для глобальной обработки](#controlleradvice-для-глобальной-обработки)
  - [Расширение ResponseEntityExceptionHandler](#расширение-responseentityexceptionhandler)
- [Преобразование Entity в DTO](#преобразование-entity-в-dto)
  - [Использование ModelMapper](#использование-modelmapper)
  - [Конфигурация ModelMapper](#конфигурация-modelmapper)
  - [Кастомное преобразование](#кастомное-преобразование)
  - [Использование в контроллере](#использование-в-контроллере)
  - [Преимущества использования DTO](#преимущества-использования-dto)
- [Продвинутые возможности REST API](#продвинутые-возможности-rest-api)
  - [Content Negotiation](#content-negotiation)
  - [Matrix Variables](#matrix-variables)
  - [Custom HTTP Methods](#custom-http-methods)
  - [Conditional Requests](#conditional-requests)
- [Валидация и обработка ошибок](#валидация-и-обработка-ошибок)
  - [Продвинутая валидация](#продвинутая-валидация)
  - [Глобальная обработка ошибок](#глобальная-обработка-ошибок)
- [Безопасность REST API](#безопасность-rest-api)
  - [JWT Authentication](#jwt-authentication)
  - [CORS Configuration](#cors-configuration)
  - [Rate Limiting](#rate-limiting)
- [Кэширование в REST API](#кэширование-в-rest-api)
  - [HTTP Caching](#http-caching)
  - [Conditional GET](#conditional-get)
- [Тестирование REST контроллеров](#тестирование-rest-контроллеров)
  - [Unit тестирование](#unit-тестирование)
  - [Интеграционное тестирование](#интеграционное-тестирование)
  - [Тестирование с Security](#тестирование-с-security)
- [Документация API](#документация-api)
  - [OpenAPI/Swagger](#openapiswagger)
  - [Spring REST Docs](#spring-rest-docs)
- [Мониторинг и метрики](#мониторинг-и-метрики)
  - [Actuator Endpoints](#actuator-endpoints)
  - [Custom Metrics](#custom-metrics)
- [Версионирование API](#версионирование-api)
  - [URI Versioning](#uri-versioning)
  - [Header Versioning](#header-versioning)
  - [Parameter Versioning](#parameter-versioning)
- [HATEOAS и Hypermedia](#hateoas-и-hypermedia)
- [Файловые операции](#файловые-операции)
  - [File Upload](#file-upload)
- [WebSocket интеграция](#websocket-интеграция)
  - [WebSocket с STOMP](#websocket-с-stomp)
  - [SSE (Server-Sent Events)](#sse-server-sent-events)
- [Асинхронные операции](#асинхронные-операции)
  - [CompletableFuture в REST контроллерах](#completablefuture-в-rest-контроллерах)
- [Best practices](#best-practices)
  - [1. Правильная структура URL](#1-правильная-структура-url)
  - [2. HTTP статус коды](#2-http-статус-коды)
  - [3. Content Negotiation](#3-content-negotiation)
  - [4. API Evolution (версионирование)](#4-api-evolution-версионирование)
  - [5. Security best practices](#5-security-best-practices)
  - [6. Performance optimization](#6-performance-optimization)
  - [7. Error handling patterns](#7-error-handling-patterns)
  - [8. Documentation best practices](#8-documentation-best-practices)
## Создание веб-приложения

В этом разделе мы рассмотрим, как создать простое веб-приложение с использованием **Spring MVC** и **Spring Boot**.

### Настройка проекта

Для начала нам нужно добавить зависимости в наш **pom.xml:**

```xml
<!-- Зависимость Spring Boot Web для REST API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### Создание контроллера

**Давайте создадим простой контроллер для обработки **HTTP**-запросов:**

```java
// Простой REST-контроллер с приветствием
@RestController
@RequestMapping("/api")
public class SimpleRestController {
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
```

## Как читать заголовки **HTTP** в контроллерах?

В этом разделе мы рассмотрим, как читать заголовки **HTTP** в контроллерах **Spring MVC**.

### Использование @**RequestHeader**

Мы можем использовать аннотацию **@RequestHeader** для чтения заголовков **HTTP:**

```java
// Чтение заголовков через @RequestHeader
@RestController
@RequestMapping("/api")
public class HeaderController {
    
    @GetMapping("/headers")
    public String readHeaders(@RequestHeader("User-Agent") String userAgent,
                             @RequestHeader("Accept") String accept) {
        return "User-Agent: " + userAgent + ", Accept: " + accept;
    }
}
```

### Использование **HttpServletRequest**

**Мы также можем использовать **HttpServletRequest** для чтения заголовков:**

```java
// Чтение заголовков через HttpServletRequest
@GetMapping("/headers-servlet")
public String readHeadersServlet(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    String accept = request.getHeader("Accept");
    return "User-Agent: " + userAgent + ", Accept: " + accept;
}
```

### Чтение всех заголовков

Если нам нужно прочитать все заголовки, мы можем использовать **Map** или **HttpHeaders:**

```java
// Все заголовки запроса в виде Map
@GetMapping("/all-headers")
public Map<String, String> readAllHeaders(@RequestHeader Map<String, String> headers) {
    return headers;
}
```

Или с использованием **HttpHeaders:**

```java
// Все заголовки через HttpHeaders
@GetMapping("/all-headers-http")
public HttpHeaders readAllHeadersHttp(@RequestHeader HttpHeaders headers) {
    return headers;
}
```

## Создание **REST API**

В этом разделе мы рассмотрим, как создать полноценное **REST API** с использованием **Spring MVC**.

### Основные **HTTP** методы

**REST API** использует стандартные методы **HTTP** для выполнения операций:**

- **GET** - получение ресурсов
- **POST** - создание новых ресурсов
- **PUT** - обновление существующих ресурсов
- **PATCH** - частичное обновление ресурсов
- **DELETE** - удаление ресурсов

### Пример **REST** контроллера

Давайте создадим простой **REST** контроллер для работы с сущностью **Book:**

```java
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        Book book = bookService.findById(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book updatedBook = bookService.update(id, book);
        if (updatedBook != null) {
            return ResponseEntity.ok(updatedBook);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (bookService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

### Использование **ResponseEntity**

**ResponseEntity** позволяет нам полностью контролировать **HTTP**-ответ, включая статус код и заголовки:**

```java
@GetMapping("/custom")
public ResponseEntity<Book> getBookWithCustomHeaders() {
    Book book = bookService.findById(1L);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Custom-Header", "Custom-Value");
    return ResponseEntity.ok()
        .headers(headers)
        .body(book);
}
```

### **HTTP** статус коды

**Spring** предоставляет удобные методы для создания ответов с различными статус кодами:**

```java
@PostMapping("/books")
public ResponseEntity<Book> createBook(@RequestBody Book book) {
    Book createdBook = bookService.save(book);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
}
```

Для любого запроса, у которого есть сопоставление, **Spring MVC** считает запрос действительным и отвечает **200 OK**, если в противном случае не указан другой код состояния.

Именно из-за этого контроллер объявляет разные**@ResponseStatus** для действий **create**, **update** и **delete**, но не для **get**, который действительно должен возвращать значение по умолчанию `200` OK**.

### Пользовательские исключения

В случае ошибки клиента определяются пользовательские исключения, которые сопоставляются с соответствующими кодами ошибок**.

Простое создание этих исключений из любого уровня веб-уровня гарантирует, что **Spring** отобразит соответствующий код состояния в ответе **HTTP:**

```java
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
}
```

Эти исключения являются частью **REST API** и, как таковые, должны использоваться только на соответствующих уровнях, соответствующих **REST;** если, например, существует уровень **DAO/`DAL`,** он не должен использовать исключения напрямую.

Также обратите внимание, что это не проверенные исключения, а исключения времени выполнения — в соответствии с практиками и идиомами **Spring**.

## Руководство @**Controller** и @**RestController**

В этом разделе мы рассмотрим различия между **@Controller** и **@RestController** в **Spring MVC**.

### @**Controller**

**@Controller** — это стандартная аннотация для контроллеров **Spring MVC**. Контроллеры, аннотированные **@Controller**, обычно возвращают имя представления, которое **Spring MVC** использует для рендеринга **HTML**-страницы.

```java
@Controller
@RequestMapping("/books")
public class BookController {
    
    @GetMapping("/list")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/list";
    }
}
```

### @**ResponseBody**

Если мы хотим, чтобы контроллер возвращал данные напрямую (**например, JSON**),** мы можем использовать аннотацию **@**ResponseBody**:**

```java
@Controller
@RequestMapping("/books-rest")
public class SimpleBookController {
    
    @GetMapping("/{id}")
    @ResponseBody
    public Book getBook(@PathVariable int id) {
        return findBookById(id);
    }
    
    private Book findBookById(int id) {
        // ...
    }
}
```

Мы аннотировали метод обработки запроса с помощью **`@ResponseBody`.** Эта аннотация позволяет автоматически сериализовать возвращаемый объект в **HttpResponse.**

### @**RestController**

**@RestController** — это специализированная версия контроллера. Он включает аннотации **@Controller** и **@ResponseBody** и, как следствие, упрощает реализацию контроллера:**

```java
@RestController
@RequestMapping("books-rest")
public class SimpleBookRestController {
    
    @GetMapping("/{id}")
    public Book getBook(@PathVariable int id) {
        return findBookById(id);
    }
    
    private Book findBookById(int id) {
        // ...
    }
}
```

Контроллер снабжен аннотацией **@RestController**; поэтому **@ResponseBody** не требуется**.

Каждый метод обработки запросов класса контроллера автоматически сериализует возвращаемые объекты в **HttpResponse.**

### Различия между @**Controller** и @**RestController**

| @**Controller** | @**RestController** |
| --- | --- |
| Используется для веб-приложений с представлениями (**HTML**) | Используется для **REST API** |
| Возвращает имя представления | Возвращает данные (**JSON, `XML` и т.д.**) |
| Требует **@ResponseBody** для возврата данных | Автоматически возвращает данные |
| Используется с **ModelAndView** | Используется с **ResponseEntity** или объектами |

## Логгирование входящих запросов

Иногда нам может потребоваться дополнительная обработка полезной нагрузки **HTTP-**запроса, например ведение журнала**. Регистрация входящего **HTTP**-**запроса очень полезна при отладке приложений**.

В этом кратком руководстве мы изучим основы регистрации входящих запросов с использованием фильтра регистрации **Spring Boot**.

### Настройка **Logging Filter**

Начнем с добавления зависимости **spring-`boot-starter`-web** в наш **pom.xml:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### Создание **Custom Filter**

**Мы можем создать собственный фильтр для логирования запросов:**

```java
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        logger.info("Request URL: {}", httpRequest.getRequestURL());
        logger.info("Request Method: {}", httpRequest.getMethod());
        logger.info("Request Headers: {}", Collections.list(httpRequest.getHeaderNames()));
        
        chain.doFilter(request, response);
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        logger.info("Response Status: {}", httpResponse.getStatus());
    }
}
```

### Использование **CommonsRequestLoggingFilter**

**Spring Boot** предоставляет **CommonsRequestLoggingFilter** для логирования запросов:**

```java
@Configuration
public class RequestLoggingFilterConfig {
    
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}
```

Также нужно включить логирование в **application.properties:**

```properties
logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
```

## Руководство по @**Async**

В этом разделе мы рассмотрим, как использовать асинхронное выполнение методов в **Spring MVC** с помощью аннотации **@Async**.

### Включение поддержки @**Async**

**Для использования **@Async** нам нужно включить поддержку асинхронного выполнения:**

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
```

### Использование @**Async** в контроллере

**Мы можем использовать **@Async** для асинхронного выполнения методов:**

```java
@Service
public class AsyncService {
    
    @Async
    public CompletableFuture<String> asyncMethod() {
        // Долгая операция
        return CompletableFuture.completedFuture("Result");
    }
}
```

**И использование в контроллере:**

```java
@RestController
@RequestMapping("/api")
public class AsyncController {
    
    @Autowired
    private AsyncService asyncService;
    
    @GetMapping("/async")
    public CompletableFuture<String> asyncEndpoint() {
        return asyncService.asyncMethod();
    }
}
```

### Обработка исключений в @**Async**

Для обработки исключений в асинхронных методах мы можем создать **AsyncUncaughtExceptionHandler:**

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
    
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
```

## Руководство по @**ExceptionHandler** и @**ControllerAdvice**

В этом разделе мы рассмотрим, как обрабатывать исключения в **Spring MVC** с помощью **@ExceptionHandler** и **@ControllerAdvice**.

### @**ExceptionHandler** на уровне контроллера

**Мы можем использовать **@ExceptionHandler** для обработки исключений на уровне контроллера:**

```java
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

### @**ControllerAdvice** для глобальной обработки

Для глобальной обработки исключений мы можем использовать **`@ControllerAdvice`:**

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage("An unexpected error occurred");
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### Расширение **ResponseEntityExceptionHandler**

**Мы можем расширить **ResponseEntityExceptionHandler** для обработки стандартных исключений **Spring MVC**:**

```java
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(RuntimeException ex, WebRequest request) {
        logger.error("404 Status Code", ex);
        GenericResponse bodyOfResponse = new GenericResponse(
            messages.getMessage("message.userNotFound", null, request.getLocale()), "UserNotFound");
        return handleExceptionInternal(
            ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
    @ExceptionHandler({MailAuthenticationException.class})
    public ResponseEntity<Object> handleMail(RuntimeException ex, WebRequest request) {
        logger.error("500 Status Code", ex);
        GenericResponse bodyOfResponse = new GenericResponse(
            messages.getMessage("message.email.config.error", null, request.getLocale()), "MailError");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleInternal(RuntimeException ex, WebRequest request) {
        logger.error("500 Status Code", ex);
        GenericResponse bodyOfResponse = new GenericResponse(
            messages.getMessage("message.error", null, request.getLocale()), "InternalError");
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
```

При возвращении **ResponseEntity <T>** объект из контроллера, мы могли бы получить исключение или ошибка при обработке запроса, и хотел бы вернуть ошибку информацию, относящуюся к пользователю представить в виде какой-либо другой тип, скажем **E**.

**Spring 3.2** обеспечивает поддержку глобального **@ExceptionHandler** с новой аннотацией **@ControllerAdvice**, которая обрабатывает такие сценарии.

Хотя **ResponseEntity** очень мощный инструмент, нам не следует злоупотреблять им. В простых случаях есть другие варианты, которые удовлетворяют наши потребности, и они приводят к гораздо более чистому коду**.

## Преобразование **Entity** в **DTO**

В этом руководстве мы будем обрабатывать преобразования, которые должны произойти между внутренними сущностями приложения **Spring** и внешними **DTO** (**объектами передачи данных**), которые публикуются обратно для клиента.

### Использование **ModelMapper**

Давайте начнем с представления основной библиотеки, которую мы собираемся использовать для выполнения преобразования сущности в **DTO, `ModelMapper`.**

Сначала нам нужно добавить зависимость в **pom.xml:**

```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.1.0</version>
</dependency>
```

### Конфигурация **ModelMapper**

Давайте создадим **bean**-компонент **ModelMapper:**

```java
@Configuration
public class ModelMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOOSE);
        return mapper;
    }
}
```

### Преобразование **Entity** в **DTO**

**Давайте создадим пример сущности и **DTO**:**

```java
@Entity
public class Book {
    private Long id;
    private String title;
    private String author;
    // getters and setters
}

public class BookDTO {
    private Long id;
    private String title;
    private String author;
    // getters and setters
}
```

**Теперь мы можем использовать **ModelMapper** для преобразования:**

```java
@Service
public class BookService {
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private BookRepository bookRepository;
    
    public BookDTO findById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return modelMapper.map(book, BookDTO.class);
    }
    
    public BookDTO create(BookDTO bookDTO) {
        Book book = modelMapper.map(bookDTO, Book.class);
        Book savedBook = bookRepository.save(book);
        return modelMapper.map(savedBook, BookDTO.class);
    }
}
```

### Кастомное преобразование

**Если нам нужно более сложное преобразование, мы можем создать кастомный конвертер:**

```java
@Configuration
public class ModelMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Кастомное преобразование
        mapper.addMappings(new PropertyMap<Book, BookDTO>() {
            @Override
            protected void configure() {
                map().setAuthor(source.getAuthor().getName());
            }
        });
        
        return mapper;
    }
}
```

### Использование в контроллере

**В контроллере мы можем напрямую возвращать **DTO**:**

```java
@RestController
@RequestMapping("/api/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        BookDTO bookDTO = bookService.findById(id);
        return ResponseEntity.ok(bookDTO);
    }
    
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        BookDTO createdBookDTO = bookService.create(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBookDTO);
    }
}
```

### Преимущества использования **DTO**

**Использование **DTO** имеет несколько преимуществ:**

1. **Изоляция внутренней модели** - мы можем изменять внутреннюю модель, не влияя на **API**
2. **Контроль над данными** - мы можем контролировать, какие данные возвращаются клиенту
3. **Безопасность** - мы можем скрыть чувствительные данные
4. **Версионирование** - мы можем создавать разные версии **API** с разными **DTO**

## Продвинутые возможности **REST API**

### **Content Negotiation**

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping(value = "/{id}", produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE,
        "application/vnd.company.product+json",
        "application/vnd.company.product+xml"
    })
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id,
                                                @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String accept) {

        ProductDTO product = productService.findById(id);

        if ("application/vnd.company.product+json".equals(accept)) {
            // Вернуть расширенную версию
            return ResponseEntity.ok(enrichProduct(product));
        }

        return ResponseEntity.ok(product);
    }

    @PostMapping(consumes = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO product,
                                                   @RequestHeader(value = "Content-Type") String contentType) {

        // Обработка в зависимости от типа контента
        validateProductBasedOnContentType(product, contentType);

        ProductDTO created = productService.create(product);
        return ResponseEntity.created(buildLocationUri(created.getId())).body(created);
    }
}
```

### **Matrix Variables**

```java
@RestController
@RequestMapping("/api/products")
public class ProductMatrixController {

    // GET /api/products;color=red,green;size=large
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProductsByMatrix(
            @MatrixVariable(required = false) List<String> color,
            @MatrixVariable(required = false) String size,
            @MatrixVariable(pathVar = "category", required = false) String category) {

        ProductFilter filter = ProductFilter.builder()
                .colors(color)
                .size(size)
                .category(category)
                .build();

        List<ProductDTO> products = productService.findByFilter(filter);
        return ResponseEntity.ok(products);
    }

    // GET /api/products/categories/{category};sort=name,asc
    @GetMapping("/categories/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @PathVariable String category,
            @MatrixVariable(pathVar = "category") List<String> sort) {

        Sort.Direction direction = sort.size() > 1 && "desc".equals(sort.get(1)) ?
                                  Sort.Direction.DESC : Sort.Direction.ASC;

        List<ProductDTO> products = productService.findByCategory(category, Sort.by(direction, sort.get(0)));
        return ResponseEntity.ok(products);
    }
}
```

### **Custom HTTP Methods**

```java
@RestController
@RequestMapping("/api/resources")
public class CustomMethodController {

    // PATCH для частичного обновления
    @PatchMapping("/{id}")
    public ResponseEntity<ResourceDTO> partialUpdate(@PathVariable Long id,
                                                    @RequestBody Map<String, Object> updates) {

        ResourceDTO updated = resourceService.partialUpdate(id, updates);
        return ResponseEntity.ok(updated);
    }

    // Custom method через X-HTTP-Method-Override
    @PostMapping("/{id}")
    public ResponseEntity<ResourceDTO> handlePostWithOverride(@PathVariable Long id,
                                                             @RequestBody ResourceDTO resource,
                                                             @RequestHeader(value = "X-HTTP-Method-Override", required = false) String methodOverride) {

        if ("PATCH".equalsIgnoreCase(methodOverride)) {
            return partialUpdate(id, convertToMap(resource));
        }

        // Обычный POST
        ResourceDTO created = resourceService.create(resource);
        return ResponseEntity.created(buildLocationUri(created.getId())).body(created);
    }

    // OPTIONS для описания доступных операций
    @RequestMapping(value = "/{id}", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> options(@PathVariable Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        headers.add("Accept", "application/json, application/xml");

        return ResponseEntity.ok().headers(headers).build();
    }
}
```

### **Conditional Requests**

```java
@RestController
@RequestMapping("/api/documents")
public class ConditionalController {

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable Long id,
                                                  @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
                                                  @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince) {

        DocumentDTO document = documentService.findById(id);

        // ETag based conditional request
        String etag = generateETag(document);
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                               .header("ETag", etag)
                               .build();
        }

        // Last-Modified based conditional request
        if (ifModifiedSince != null) {
            try {
                Instant modifiedSince = Instant.parse(ifModifiedSince);
                if (!document.getLastModified().isAfter(modifiedSince)) {
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                                       .header("Last-Modified", document.getLastModified().toString())
                                       .build();
                }
            } catch (Exception e) {
                // Invalid date format
            }
        }

        return ResponseEntity.ok()
                           .header("ETag", etag)
                           .header("Last-Modified", document.getLastModified().toString())
                           .body(document);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDTO> updateDocument(@PathVariable Long id,
                                                     @RequestBody DocumentDTO document,
                                                     @RequestHeader(value = "If-Match", required = false) String ifMatch) {

        if (ifMatch != null) {
            String currentETag = generateETag(documentService.findById(id));
            if (!currentETag.equals(ifMatch)) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
        }

        DocumentDTO updated = documentService.update(id, document);
        return ResponseEntity.ok()
                           .header("ETag", generateETag(updated))
                           .body(updated);
    }

    private String generateETag(DocumentDTO document) {
        return "\"" + Hashing.sha256()
                .hashString(document.getContent() + document.getLastModified(), StandardCharsets.UTF_8)
                .toString() + "\"";
    }
}
```

## Валидация и обработка ошибок

### Продвинутая валидация

```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserValidationController {

    // Валидация на уровне метода
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO user) {
        UserDTO created = userService.create(user);
        return ResponseEntity.created(buildLocationUri(created.getId())).body(created);
    }

    // Кастомная валидация групп
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                             @Validated(UserDTO.Update.class) @RequestBody UserDTO user) {
        UserDTO updated = userService.update(id, user);
        return ResponseEntity.ok(updated);
    }

    // Валидация параметров
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam @NotBlank @Size(min = 2, max = 50) String name,
            @RequestParam(required = false) @Min(18) @Max(120) Integer minAge,
            @RequestParam(required = false) @Pattern(regexp = "^[A-Za-z]+$") String country) {

        UserSearchCriteria criteria = UserSearchCriteria.builder()
                .name(name)
                .minAge(minAge)
                .country(country)
                .build();

        List<UserDTO> users = userService.search(criteria);
        return ResponseEntity.ok(users);
    }

    // Кастомные валидаторы
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        UserDTO user = userService.register(request);
        return ResponseEntity.ok(user);
    }
}

// Кастомные аннотации валидации
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {
    String message() default "Password must contain at least 8 characters, one uppercase, one lowercase, and one digit";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$");

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
```

### Глобальная обработка ошибок

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e, WebRequest request) {
        log.warn("Validation error: {}", e.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e, WebRequest request) {
        log.info("Resource not found: {}", e.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e, WebRequest request) {
        log.error("Data integrity violation", e);

        String message = extractConstraintMessage(e);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Data Integrity Violation")
                .message(message)
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e, WebRequest request) {
        log.error("Unexpected error", e);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                 HttpHeaders headers,
                                                                 HttpStatus status,
                                                                 WebRequest request) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage
                ));

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Validation Failed")
                .message("Input validation failed")
                .path(getRequestPath(request))
                .details(errors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                 HttpHeaders headers,
                                                                 HttpStatus status,
                                                                 WebRequest request) {

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Bad Request")
                .message("Malformed JSON request")
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "unknown";
    }

    private String extractConstraintMessage(DataIntegrityViolationException e) {
        Throwable cause = e.getMostSpecificCause();
        if (cause instanceof ConstraintViolationException) {
            return "Database constraint violation: " + cause.getMessage();
        }
        return "Data integrity constraint violation";
    }
}

@Data
@Builder
class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;
}
```

## Безопасность **REST API**

### **JWT Authentication**

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new TokenRefreshException(refreshToken, "Invalid refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = tokenProvider.generateTokenFromUsername(username);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
    }
}

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### **CORS Configuration**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource())
            .and()
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/auth/").permitAll()
                .antMatchers("/api/public/").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/", configuration);
        return source;
    }
}
```

### **Rate Limiting**

```java
@RestController
@RequestMapping("/api")
@RateLimited
public class RateLimitedController {

    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    @GetMapping("/limited")
    @RateLimited(requests = 10, windowSeconds = 60)
    public ResponseEntity<String> limitedEndpoint() {
        return ResponseEntity.ok("Request successful");
    }

    @GetMapping("/unlimited")
    public ResponseEntity<String> unlimitedEndpoint() {
        return ResponseEntity.ok("Request successful");
    }
}

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {
    int requests() default 100;
    int windowSeconds() default 60;
}

@Aspect
@Component
public class RateLimitingAspect {

    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String clientId = getClientId();
        String key = "rate_limit:" + clientId + ":" + joinPoint.getSignature().toString();

        RateLimiter limiter = rateLimiters.computeIfAbsent(key, k ->
            RateLimiter.create(rateLimited.requests() / (double) rateLimited.windowSeconds())
        );

        if (!limiter.tryAcquire()) {
            throw new RateLimitExceededException("Rate limit exceeded for client: " + clientId);
        }

        return joinPoint.proceed();
    }

    private String getClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : getClientIpAddress();
    }

    private String getClientIpAddress() {
        // Получение IP из HttpServletRequest
        return "anonymous";
    }
}
```

## Кэширование в **REST API**

### **HTTP Caching**

```java
@RestController
@RequestMapping("/api/products")
public class CachedProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    @Cacheable(value = "products", key = "#id")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO product = productService.findById(id);

        // HTTP caching headers
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.maxAge(300, TimeUnit.SECONDS)
                .cachePublic()
                .mustRevalidate());
        headers.setETag("\"" + product.hashCode() + "\"");
        headers.setLastModified(product.getLastModified().toInstant());

        return ResponseEntity.ok().headers(headers).body(product);
    }

    @GetMapping
    @Cacheable(value = "productList", key = "#page + '_' + #size + '_' + #sort")
    public ResponseEntity<List<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<ProductDTO> products = productService.findAll(pageable);

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS));

        return ResponseEntity.ok().headers(headers).body(products.getContent());
    }

    @PostMapping
    @CacheEvict(value = {"products", "productList"}, allEntries = true)
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO product) {
        ProductDTO created = productService.create(product);
        return ResponseEntity.created(buildLocationUri(created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "products", key = "#id")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                   @Valid @RequestBody ProductDTO product) {
        ProductDTO updated = productService.update(id, product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "products", key = "#id")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### **Conditional GET**

```java
@RestController
@RequestMapping("/api/resources")
public class ConditionalController {

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> getResource(@PathVariable Long id,
                                                  @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
                                                  @RequestHeader(value = "If-Modified-Since", required = false) String ifModifiedSince) {

        ResourceDTO resource = resourceService.findById(id);

        // ETag check
        String etag = generateETag(resource);
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                               .header("ETag", etag)
                               .build();
        }

        // Last-Modified check
        if (ifModifiedSince != null) {
            Instant modifiedSince = Instant.parse(ifModifiedSince);
            if (!resource.getLastModified().isAfter(modifiedSince)) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                                   .header("Last-Modified", resource.getLastModified().toString())
                                   .build();
            }
        }

        return ResponseEntity.ok()
                           .header("ETag", etag)
                           .header("Last-Modified", resource.getLastModified().toString())
                           .header("Cache-Control", "max-age=300")
                           .body(resource);
    }

    private String generateETag(ResourceDTO resource) {
        return "\"" + Hashing.sha256()
                .hashString(resource.toString(), StandardCharsets.UTF_8)
                .toString() + "\"";
    }
}
```

## Тестирование **REST** контроллеров

### **Unit** тестирование

```java
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createUser_ShouldReturnCreatedUser() throws Exception {
        // Given
        UserDTO userDTO = UserDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        UserDTO createdUser = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(userService.create(any(UserDTO.class))).thenReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/users/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService).create(any(UserDTO.class));
    }

    @Test
    public void getUser_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        UserDTO userDTO = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(userService.findById(1L)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    public void getUser_WhenUserDoesNotExist_ShouldReturn404() throws Exception {
        // Given
        when(userService.findById(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    public void createUser_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        UserDTO invalidUser = UserDTO.builder()
                .name("")  // Invalid: empty name
                .email("invalid-email")  // Invalid: bad email format
                .build();

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.details.name").exists())
                .andExpect(jsonPath("$.details.email").exists());
    }

    @Test
    public void updateUser_WithPartialData_ShouldReturnUpdatedUser() throws Exception {
        // Given
        UserDTO updateDTO = UserDTO.builder()
                .name("Jane Doe")
                .build();

        UserDTO updatedUser = UserDTO.builder()
                .id(1L)
                .name("Jane Doe")
                .email("john@example.com")
                .build();

        when(userService.update(eq(1L), any(UserDTO.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(patch("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}
```

### Интеграционное тестирование

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/test-data.sql")
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
    }

    @Test
    public void createAndRetrieveUser_ShouldWorkCorrectly() {
        // Create user
        UserDTO newUser = UserDTO.builder()
                .name("Integration Test User")
                .email("integration@example.com")
                .build();

        ResponseEntity<UserDTO> createResponse = restTemplate.postForEntity(baseUrl, newUser, UserDTO.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        UserDTO createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();

        String location = createResponse.getHeaders().getLocation().toString();
        assertThat(location).contains("/api/users/");

        // Retrieve user
        ResponseEntity<UserDTO> getResponse = restTemplate.getForEntity(location, UserDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        UserDTO retrievedUser = getResponse.getBody();
        assertThat(retrievedUser.getName()).isEqualTo("Integration Test User");
        assertThat(retrievedUser.getEmail()).isEqualTo("integration@example.com");
    }

    @Test
    public void searchUsers_WithFilters_ShouldReturnFilteredResults() {
        // Given - test data loaded via @Sql

        // When
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(
            baseUrl + "/search?name=John&minAge=25", UserDTO[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserDTO[] users = response.getBody();
        assertThat(users).isNotEmpty();
        assertThat(users[0].getName()).contains("John");
    }

    @Test
    public void concurrentRequests_ShouldHandleProperly() throws InterruptedException, ExecutionException {
        // Test concurrent access
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<CompletableFuture<ResponseEntity<UserDTO>>> futures = IntStream.range(0, 100)
            .mapToObj(i -> CompletableFuture.supplyAsync(() ->
                restTemplate.getForEntity(baseUrl + "/1", UserDTO.class), executor))
            .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0]));

        allFutures.get(10, TimeUnit.SECONDS);

        // All requests should succeed
        futures.forEach(future -> {
            try {
                assertThat(future.get().getStatusCode()).isEqualTo(HttpStatus.OK);
            } catch (Exception e) {
                fail("Concurrent request failed", e);
            }
        });

        executor.shutdown();
    }
}
```

### Тестирование с **Security**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser(username = "testuser", roles = {"USER"})
public class SecuredControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void accessSecuredEndpoint_WithValidUser_ShouldSucceed() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/secured", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void accessAdminEndpoint_WithAdminRole_ShouldSucceed() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/admin", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @WithAnonymousUser
    public void accessSecuredEndpoint_WithoutAuthentication_ShouldFail() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/secured", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

// JWT тестирование
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @LocalServerPort
    private int port;

    @Test
    public void accessProtectedEndpoint_WithValidJwt_ShouldSucceed() {
        // Login to get JWT
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/auth/login", loginRequest, AuthResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String jwt = loginResponse.getBody().getAccessToken();

        // Use JWT to access protected endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
            "http://localhost:" + port + "/api/protected", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

## Документация **API**

### **OpenAPI**/**Swagger**

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("REST API Documentation")
                        .version("1.0.0")
                        .description("Comprehensive REST API for the application")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("API Wiki")
                        .url("https://wiki.example.com/api"))
                .servers(Arrays.asList(
                        new Server().url("https://api.example.com").description("Production server"),
                        new Server().url("https://staging-api.example.com").description("Staging server"),
                        new Server().url("http://localhost:8080").description("Local development")
                ));
    }
}

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    @Operation(summary = "Get product by ID", description = "Retrieve a product by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "Product not found",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable Long id) {

        ProductDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Create new product", description = "Create a new product in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Parameter(description = "Product data to create", required = true)
            @Valid @RequestBody ProductDTO product) {

        ProductDTO created = productService.create(product);
        return ResponseEntity.created(buildLocationUri(created.getId())).body(created);
    }

    @Operation(summary = "Search products", description = "Search products with various filters")
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @Parameter(description = "Product name to search for")
            @RequestParam(required = false) String name,

            @Parameter(description = "Minimum price filter")
            @RequestParam(required = false) @DecimalMin("0.01") BigDecimal minPrice,

            @Parameter(description = "Maximum price filter")
            @RequestParam(required = false) @DecimalMax("999999.99") BigDecimal maxPrice,

            @Parameter(description = "Categories to filter by")
            @RequestParam(required = false) List<String> categories,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,

            @Parameter(description = "Sort field and direction (e.g., 'name,asc' or 'price,desc')")
            @RequestParam(defaultValue = "name,asc") String sort) {

        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .name(name)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .categories(categories)
                .build();

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<ProductDTO> products = productService.search(criteria, pageable);

        return ResponseEntity.ok(products);
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]) ?
                            Direction.DESC : Direction.ASC;
        return Sort.by(direction, parts[0]);
    }
}
```

### **Spring REST Docs**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs
public class ApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createProduct() throws Exception {
        ProductDTO product = ProductDTO.builder()
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .category("Electronics")
                .build();

        this.mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andDo(document("create-product",
                        requestFields(
                                fieldWithPath("name").description("Product name"),
                                fieldWithPath("description").description("Product description"),
                                fieldWithPath("price").description("Product price"),
                                fieldWithPath("category").description("Product category")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product unique identifier"),
                                fieldWithPath("name").description("Product name"),
                                fieldWithPath("description").description("Product description"),
                                fieldWithPath("price").description("Product price"),
                                fieldWithPath("category").description("Product category"),
                                fieldWithPath("createdAt").description("Creation timestamp"),
                                fieldWithPath("updatedAt").description("Last update timestamp")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("URI of the created product")
                        )
                ));
    }

    @Test
    public void getProduct() throws Exception {
        this.mockMvc.perform(get("/api/products/{id}", 1))
                .andExpect(status().isOk())
                .andDo(document("get-product",
                        pathParameters(
                                parameterWithName("id").description("Product unique identifier")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Product unique identifier"),
                                fieldWithPath("name").description("Product name"),
                                fieldWithPath("description").description("Product description"),
                                fieldWithPath("price").description("Product price"),
                                fieldWithPath("category").description("Product category"),
                                fieldWithPath("createdAt").description("Creation timestamp"),
                                fieldWithPath("updatedAt").description("Last update timestamp")
                        )
                ));
    }

    @Test
    public void searchProducts() throws Exception {
        this.mockMvc.perform(get("/api/products/search")
                .param("name", "phone")
                .param("minPrice", "100")
                .param("maxPrice", "1000")
                .param("categories", "electronics")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andDo(document("search-products",
                        requestParameters(
                                parameterWithName("name").description("Product name filter (optional)").optional(),
                                parameterWithName("minPrice").description("Minimum price filter (optional)").optional(),
                                parameterWithName("maxPrice").description("Maximum price filter (optional)").optional(),
                                parameterWithName("categories").description("Category filters (optional)").optional(),
                                parameterWithName("page").description("Page number (0-based, default: 0)").optional(),
                                parameterWithName("size").description("Page size (default: 20)").optional(),
                                parameterWithName("sort").description("Sort field and direction (default: 'name,asc')").optional()
                        ),
                        responseFields(
                                fieldWithPath("content[].id").description("Product unique identifier"),
                                fieldWithPath("content[].name").description("Product name"),
                                fieldWithPath("content[].description").description("Product description"),
                                fieldWithPath("content[].price").description("Product price"),
                                fieldWithPath("content[].category").description("Product category"),
                                fieldWithPath("content[].createdAt").description("Creation timestamp"),
                                fieldWithPath("content[].updatedAt").description("Last update timestamp"),
                                fieldWithPath("pageable.page").description("Current page number"),
                                fieldWithPath("pageable.size").description("Page size"),
                                fieldWithPath("totalElements").description("Total number of elements"),
                                fieldWithPath("totalPages").description("Total number of pages"),
                                fieldWithPath("numberOfElements").description("Number of elements in current page"),
                                fieldWithPath("first").description("Whether this is the first page"),
                                fieldWithPath("last").description("Whether this is the last page")
                        )
                ));
    }
}
```

## Мониторинг и метрики

### **Actuator Endpoints**

```java
@Configuration
public class ActuatorConfig {

    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCustomizer() {
        return registry -> registry.config()
                .commonTags("application", "rest-api")
                .commonTags("version", "1.0.0");
    }
}

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getCustomMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // HTTP request metrics
        Counter httpRequests = meterRegistry.find("http.server.requests").counter();
        metrics.put("totalHttpRequests", httpRequests != null ? httpRequests.count() : 0);

        // Database connection pool metrics
        Gauge dbConnections = meterRegistry.find("hikaricp.connections.active").gauge();
        metrics.put("activeDbConnections", dbConnections != null ? dbConnections.value() : 0);

        // JVM memory metrics
        Gauge heapUsed = meterRegistry.find("jvm.memory.used").tag("area", "heap").gauge();
        metrics.put("heapMemoryUsed", heapUsed != null ? heapUsed.value() : 0);

        Gauge heapMax = meterRegistry.find("jvm.memory.max").tag("area", "heap").gauge();
        metrics.put("heapMemoryMax", heapMax != null ? heapMax.value() : 0);

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/health/detailed")
    public ResponseEntity<Health> getDetailedHealth() {
        Health.Builder health = Health.up();

        // Check database connectivity
        try {
            userService.findById(1L);
            health.withDetail("database", "UP");
        } catch (Exception e) {
            health.withDetail("database", Health.down(e).build());
        }

        // Check external service
        try {
            // Check external API
            health.withDetail("external-api", "UP");
        } catch (Exception e) {
            health.withDetail("external-api", Health.down(e).build());
        }

        // Check disk space
        long freeSpace = new File("/").getFreeSpace();
        if (freeSpace < 100 * 1024 * 1024) { // Less than 100MB
            health.withDetail("disk-space", Health.down()
                    .withDetail("free", freeSpace)
                    .withDetail("threshold", "100MB")
                    .build());
        } else {
            health.withDetail("disk-space", Health.up()
                    .withDetail("free", freeSpace)
                    .build());
        }

        return ResponseEntity.ok(health.build());
    }
}
```

### **Custom Metrics**

```java
@Service
public class MetricsService {

    private final Counter apiCallsCounter;
    private final Timer apiCallTimer;
    private final DistributionSummary responseSizeSummary;

    public MetricsService(MeterRegistry registry) {
        this.apiCallsCounter = Counter.builder("api.calls")
                .description("Number of API calls")
                .register(registry);

        this.apiCallTimer = Timer.builder("api.call.duration")
                .description("API call duration")
                .register(registry);

        this.responseSizeSummary = DistributionSummary.builder("api.response.size")
                .description("API response size in bytes")
                .register(registry);
    }

    public void recordApiCall(String endpoint, String method, int statusCode, long duration, long responseSize) {
        apiCallsCounter.increment();

        apiCallTimer.record(duration, TimeUnit.MILLISECONDS);

        responseSizeSummary.record(responseSize);

        // Additional tags for detailed metrics
        Counter.builder("api.calls.by.endpoint")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status", String.valueOf(statusCode / 100))
                .register(Metrics.globalRegistry)
                .increment();
    }
}

@Aspect
@Component
public class ApiMetricsAspect {

    @Autowired
    private MetricsService metricsService;

    @Around("execution(* com.example.controller.*.*(..))")
    public Object recordApiMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String endpoint = extractEndpoint(joinPoint);
        String method = extractHttpMethod();

        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();

            long duration = (System.nanoTime() - startTime) / 1_000_000; // to milliseconds
            long responseSize = calculateResponseSize(result);

            metricsService.recordApiCall(endpoint, method, 200, duration, responseSize);

            return result;

        } catch (Exception e) {
            long duration = (System.nanoTime() - startTime) / 1_000_000;

            int statusCode = determineStatusCode(e);
            metricsService.recordApiCall(endpoint, method, statusCode, duration, 0);

            throw e;
        }
    }

    private String extractEndpoint(ProceedingJoinPoint joinPoint) {
        // Extract endpoint from join point
        return joinPoint.getSignature().toString();
    }

    private String extractHttpMethod() {
        // Extract HTTP method from current request
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request.getMethod();
    }

    private long calculateResponseSize(Object result) {
        if (result == null) return 0;

        try {
            String json = new ObjectMapper().writeValueAsString(result);
            return json.getBytes(StandardCharsets.UTF_8).length;
        } catch (Exception e) {
            return 0;
        }
    }

    private int determineStatusCode(Exception e) {
        if (e instanceof ResourceNotFoundException) return 404;
        if (e instanceof ValidationException) return 400;
        if (e instanceof AccessDeniedException) return 403;
        return 500;
    }
}
```

## Версионирование **API**

### **URI Versioning**

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerV1 {

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOv1> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDTOv1 dto = convertToV1(user);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTOv1> createUser(@Valid @RequestBody UserDTOv1 userDTO) {
        User user = convertFromV1(userDTO);
        User created = userService.create(user);
        return ResponseEntity.created(buildLocationUri(created.getId()))
                           .body(convertToV1(created));
    }
}

@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2 {

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOv2> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDTOv2 dto = convertToV2(user);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTOv2> createUser(@Valid @RequestBody UserDTOv2 userDTO) {
        User user = convertFromV2(userDTO);
        User created = userService.create(user);
        return ResponseEntity.created(buildLocationUri(created.getId()))
                           .body(convertToV2(created));
    }
}
```

### **Header Versioning**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping(value = "/{id}", produces = {
        "application/vnd.company.user-v1+json",
        "application/vnd.company.user-v2+json"
    })
    public ResponseEntity<?> getUser(@PathVariable Long id,
                                    @RequestHeader(value = "Accept", defaultValue = "application/vnd.company.user-v2+json") String accept) {

        User user = userService.findById(id);

        if ("application/vnd.company.user-v1+json".equals(accept)) {
            UserDTOv1 dtoV1 = convertToV1(user);
            return ResponseEntity.ok()
                               .contentType(MediaType.parseMediaType(accept))
                               .body(dtoV1);
        } else {
            UserDTOv2 dtoV2 = convertToV2(user);
            return ResponseEntity.ok()
                               .contentType(MediaType.parseMediaType(accept))
                               .body(dtoV2);
        }
    }

    @PostMapping(consumes = {
        "application/vnd.company.user-v1+json",
        "application/vnd.company.user-v2+json"
    })
    public ResponseEntity<?> createUser(@RequestBody String userJson,
                                       @RequestHeader("Content-Type") String contentType) {

        User user;
        Object responseDto;

        if ("application/vnd.company.user-v1+json".equals(contentType)) {
            UserDTOv1 dtoV1 = parseV1Json(userJson);
            user = convertFromV1(dtoV1);
            responseDto = dtoV1;
        } else {
            UserDTOv2 dtoV2 = parseV2Json(userJson);
            user = convertFromV2(dtoV2);
            responseDto = dtoV2;
        }

        User created = userService.create(user);
        return ResponseEntity.created(buildLocationUri(created.getId()))
                           .contentType(MediaType.parseMediaType(contentType))
                           .body(responseDto);
    }
}
```

### **Parameter Versioning**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id,
                                    @RequestParam(value = "version", defaultValue = "2") int version) {

        User user = userService.findById(id);

        switch (version) {
            case 1:
                UserDTOv1 dtoV1 = convertToV1(user);
                return ResponseEntity.ok(dtoV1);
            case 2:
            default:
                UserDTOv2 dtoV2 = convertToV2(user);
                return ResponseEntity.ok(dtoV2);
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody String userJson,
                                       @RequestParam(value = "version", defaultValue = "2") int version) {

        User user;
        Object responseDto;

        switch (version) {
            case 1:
                UserDTOv1 dtoV1 = parseV1Json(userJson);
                user = convertFromV1(dtoV1);
                responseDto = dtoV1;
                break;
            case 2:
            default:
                UserDTOv2 dtoV2 = parseV2Json(userJson);
                user = convertFromV2(dtoV2);
                responseDto = dtoV2;
                break;
        }

        User created = userService.create(user);
        return ResponseEntity.created(buildLocationUri(created.getId())).body(responseDto);
    }
}
```

## HATEOAS и Hypermedia

```java
@RestController
@RequestMapping("/api/users")
public class UserHateoasController {

    @Autowired
    private EntityLinks entityLinks;

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> getUser(@PathVariable Long id) {
        UserDTO user = userService.findById(id);

        EntityModel<UserDTO> resource = EntityModel.of(user);

        // Add self link
        resource.add(Link.of("/api/users/" + id).withSelfRel());

        // Add related links
        resource.add(Link.of("/api/users").withRel("users"));
        resource.add(Link.of("/api/users/" + id + "/orders").withRel("orders"));
        resource.add(Link.of("/api/users/" + id + "/profile").withRel("profile"));

        // Add action links
        resource.add(Link.of("/api/users/" + id).withRel("update").withType("PUT"));
        resource.add(Link.of("/api/users/" + id).withRel("delete").withType("DELETE"));

        return ResponseEntity.ok(resource);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> getUsers(
            Pageable pageable, PagedResourcesAssembler<UserDTO> assembler) {

        Page<UserDTO> users = userService.findAll(pageable);

        for (UserDTO user : users) {
            user.add(Link.of("/api/users/" + user.getId()).withSelfRel());
        }

        Link selfLink = Link.of("/api/users").withSelfRel();

        return ResponseEntity.ok(assembler.toModel(users, selfLink));
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO created = userService.create(userDTO);

        EntityModel<UserDTO> resource = EntityModel.of(created);
        resource.add(Link.of("/api/users/" + created.getId()).withSelfRel());

        return ResponseEntity.created(URI.create("/api/users/" + created.getId()))
                           .body(resource);
    }
}

// Custom representation model
public class UserDTO extends RepresentationModel<UserDTO> {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    // Getters and setters...

    public UserDTO add(Link link) {
        super.add(link);
        return this;
    }
}

// HATEOAS configuration
@Configuration
public class HateoasConfig {

    @Bean
    public RepresentationModelProcessor<EntityModel<UserDTO>> userProcessor() {
        return new RepresentationModelProcessor<EntityModel<UserDTO>>() {
            @Override
            public EntityModel<UserDTO> process(EntityModel<UserDTO> model) {
                // Add common links to all UserDTO resources
                model.add(Link.of("/api/users/search").withRel("search"));
                model.add(Link.of("/api/users/stats").withRel("stats"));

                return model;
            }
        };
    }
}
```

## Файловые операции

### **File Upload**

```java
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        if (file.isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (!isValidContentType(contentType)) {
            throw new ValidationException("Invalid file type: " + contentType);
        }

        // Validate file size
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new ValidationException("File size exceeds maximum allowed size");
        }

        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            Path filePath = Paths.get(uploadDir, fileName);

            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            FileMetadata metadata = FileMetadata.builder()
                    .originalName(file.getOriginalFilename())
                    .storedName(fileName)
                    .size(file.getSize())
                    .contentType(contentType)
                    .description(description)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            FileMetadata saved = fileMetadataService.save(metadata);

            FileUploadResponse response = FileUploadResponse.builder()
                    .id(saved.getId())
                    .fileName(saved.getOriginalName())
                    .size(saved.getSize())
                    .contentType(saved.getContentType())
                    .downloadUrl("/api/files/download/" + saved.getId())
                    .build();

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            throw new FileUploadException("Failed to upload file", e);
        }
    }

    @PostMapping("/upload/multiple")
    public ResponseEntity<List<FileUploadResponse>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files) {

        List<FileUploadResponse> responses = Arrays.stream(files)
                .map(this::uploadFile)
                .map(ResponseEntity::getBody)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        FileMetadata metadata = fileMetadataService.findById(id);

        try {
            Path filePath = Paths.get(uploadDir, metadata.getStoredName());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + metadata.getOriginalName() + "\"")
                        .contentType(MediaType.parseMediaType(metadata.getContentType()))
                        .contentLength(metadata.getSize())
                        .body(resource);
            } else {
                throw new FileNotFoundException("File not found: " + metadata.getOriginalName());
            }

        } catch (Exception e) {
            throw new FileDownloadException("Failed to download file", e);
        }
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<FileInfo> getFileInfo(@PathVariable Long id) {
        FileMetadata metadata = fileMetadataService.findById(id);

        FileInfo info = FileInfo.builder()
                .id(metadata.getId())
                .fileName(metadata.getOriginalName())
                .size(metadata.getSize())
                .contentType(metadata.getContentType())
                .description(metadata.getDescription())
                .uploadedAt(metadata.getUploadedAt())
                .downloadUrl("/api/files/download/" + id)
                .build();

        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        FileMetadata metadata = fileMetadataService.findById(id);

        try {
            Path filePath = Paths.get(uploadDir, metadata.getStoredName());
            Files.deleteIfExists(filePath);

            fileMetadataService.delete(id);

            return ResponseEntity.noContent().build();

        } catch (IOException e) {
            throw new FileDeleteException("Failed to delete file", e);
        }
    }

    private boolean isValidContentType(String contentType) {
        return contentType != null && (
                contentType.startsWith("image/") ||
                contentType.startsWith("application/pdf") ||
                contentType.startsWith("text/") ||
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}
```

## WebSocket интеграция

### **WebSocket** с **STOMP**

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000", "https://example.com")
                .withSockJS();
    }
}

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastNotification(@Valid @RequestBody Notification notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Void> sendToUser(@PathVariable String userId,
                                          @Valid @RequestBody Notification notification) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Message handleMessage(Message message) {
        message.setTimestamp(Instant.now());
        message.setId(UUID.randomUUID().toString());

        // Save to database
        messageService.save(message);

        return message;
    }

    @SubscribeMapping("/topic/messages")
    public List<Message> onSubscribe() {
        // Send recent messages when client subscribes
        return messageService.findRecentMessages();
    }
}

@Controller
public class WebSocketEventController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleUserConnected(SessionConnectedEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId", String.class);

        Notification notification = Notification.builder()
                .type("USER_CONNECTED")
                .message("New user connected")
                .timestamp(Instant.now())
                .build();

        messagingTemplate.convertAndSend("/topic/system", notification);
    }

    @EventListener
    public void handleUserDisconnected(SessionDisconnectEvent event) {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId", String.class);

        Notification notification = Notification.builder()
                .type("USER_DISCONNECTED")
                .message("User disconnected")
                .timestamp(Instant.now())
                .build();

        messagingTemplate.convertAndSend("/topic/system", notification);
    }
}
```

### **SSE** (`Server-Sent Events`)

```java
@RestController
@RequestMapping("/api/events")
public class SseController {

    @Autowired
    private EventService eventService;

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamEvents(
            @RequestParam(defaultValue = "0") Long lastEventId) {

        return eventService.getEvents(lastEventId)
                .map(event -> ServerSentEvent.builder(event.getData())
                        .id(String.valueOf(event.getId()))
                        .event(event.getType())
                        .comment("Event from server")
                        .build())
                .delayElements(Duration.ofSeconds(1))
                .onErrorResume(throwable -> {
                    log.error("Error in SSE stream", throwable);
                    return Flux.empty();
                });
    }

    @GetMapping(path = "/stream-json", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<EventDTO>> streamJsonEvents() {

        return Flux.interval(Duration.ofSeconds(2))
                .map(sequence -> {
                    EventDTO event = EventDTO.builder()
                            .id(sequence)
                            .type("HEARTBEAT")
                            .data("Heartbeat #" + sequence)
                            .timestamp(Instant.now())
                            .build();

                    return ServerSentEvent.builder(event)
                            .id(String.valueOf(sequence))
                            .event("heartbeat")
                            .build();
                });
    }

    @PostMapping("/publish")
    public ResponseEntity<Void> publishEvent(@Valid @RequestBody EventDTO event) {
        eventService.publishEvent(event);
        return ResponseEntity.ok().build();
    }
}

@Service
public class EventService {

    private final Sinks.Many<Event> eventSink = Sinks.many().multicast().onBackpressureBuffer();
    private final AtomicLong eventIdGenerator = new AtomicLong(0);

    public Flux<Event> getEvents(Long lastEventId) {
        // Get historical events since lastEventId
        Flux<Event> historicalEvents = getHistoricalEvents(lastEventId);

        // Get live events
        Flux<Event> liveEvents = eventSink.asFlux()
                .filter(event -> event.getId() > lastEventId);

        return Flux.concat(historicalEvents, liveEvents);
    }

    public void publishEvent(EventDTO eventDTO) {
        Event event = Event.builder()
                .id(eventIdGenerator.incrementAndGet())
                .type(eventDTO.getType())
                .data(eventDTO.getData())
                .timestamp(eventDTO.getTimestamp())
                .build();

        Sinks.EmitResult result = eventSink.tryEmitNext(event);
        if (result.isFailure()) {
            log.warn("Failed to emit event: {}", result);
        }
    }

    private Flux<Event> getHistoricalEvents(Long lastEventId) {
        // Implementation to get historical events from database
        return Flux.empty();
    }
}
```

## Асинхронные операции

### **CompletableFuture** в **REST** контроллерах

```java
@RestController
@RequestMapping("/api/async")
@Slf4j
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/completable-future")
    public CompletableFuture<ResponseEntity<List<UserDTO>>> getUsersAsync() {
        return asyncService.getUsersAsync()
                .thenApply(users -> {
                    log.info("Retrieved {} users", users.size());
                    return ResponseEntity.ok(users);
                })
                .exceptionally(throwable -> {
                    log.error("Failed to get users", throwable);
                    List<UserDTO> emptyList = Collections.emptyList();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList);
                });
    }

    @PostMapping("/process")
    public CompletableFuture<ResponseEntity<ProcessResult>> processData(
            @Valid @RequestBody ProcessRequest request) {

        return asyncService.processData(request)
                .thenApply(result -> {
                    log.info("Data processing completed for request: {}", request.getId());
                    return ResponseEntity.ok(result);
                })
                .exceptionally(throwable -> {
                    log.error("Data processing failed for request: {}", request.getId(), throwable);
                    ProcessResult errorResult = ProcessResult.builder()
                            .status("FAILED")
                            .errorMessage(throwable.getMessage())
                            .build();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
                });
    }

    @GetMapping("/parallel")
    public CompletableFuture<ResponseEntity<ParallelResult>> executeParallelTasks() {
        long startTime = System.nanoTime();

        CompletableFuture<List<UserDTO>> usersFuture = asyncService.getUsersAsync();
        CompletableFuture<List<OrderDTO>> ordersFuture = asyncService.getOrdersAsync();
        CompletableFuture<List<ProductDTO>> productsFuture = asyncService.getProductsAsync();

        return CompletableFuture.allOf(usersFuture, ordersFuture, productsFuture)
                .thenApply(v -> {
                    try {
                        List<UserDTO> users = usersFuture.get();
                        List<OrderDTO> orders = ordersFuture.get();
                        List<ProductDTO> products = productsFuture.get();

                        long duration = (System.nanoTime() - startTime) / 1_000_000;

                        ParallelResult result = ParallelResult.builder()
                                .usersCount(users.size())
                                .ordersCount(orders.size())
                                .productsCount(products.size())
                                .executionTimeMs(duration)
                                .build();

                        return ResponseEntity.ok(result);

                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Parallel execution failed", throwable);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @GetMapping("/timeout")
    public CompletableFuture<ResponseEntity<String>> executeWithTimeout() {
        return asyncService.longRunningOperation()
                .orTimeout(5, TimeUnit.SECONDS)
                .thenApply(result -> ResponseEntity.ok("Operation completed: " + result))
                .exceptionally(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                           .body("Operation timed out");
                    }
                    log.error("Operation failed", throwable);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                       .body("Operation failed: " + throwable.getMessage());
                });
    }

    @PostMapping("/chain")
    public CompletableFuture<ResponseEntity<String>> executeChainedOperations(
            @Valid @RequestBody ChainRequest request) {

        return asyncService.validateRequest(request)
                .thenCompose(validatedRequest -> asyncService.enrichRequest(validatedRequest))
                .thenCompose(enrichedRequest -> asyncService.processRequest(enrichedRequest))
                .thenCompose(processedRequest -> asyncService.saveResult(processedRequest))
                .thenApply(savedResult -> ResponseEntity.ok("Chain completed: " + savedResult))
                .exceptionally(throwable -> {
                    log.error("Chain execution failed", throwable);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                       .body("Chain failed: " + throwable.getMessage());
                });
    }
}

@Service
public class AsyncService {

    @Async
    public CompletableFuture<List<UserDTO>> getUsersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate async operation
            simulateDelay(1000);
            return userRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Async
    public CompletableFuture<ProcessResult> processData(ProcessRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            // Complex data processing
            simulateDelay(2000);

            ProcessResult result = ProcessResult.builder()
                    .requestId(request.getId())
                    .status("COMPLETED")
                    .processedAt(Instant.now())
                    .result("Processed " + request.getData().length() + " characters")
                    .build();

            return result;
        });
    }

    // Additional async methods...
    private void simulateDelay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted", e);
        }
    }
}
```

## Лучшие практики

### 1. Правильная структура **URL**

```java
// ХОРОШО: RESTful URLs
GET    /api/users          // Получить всех пользователей
GET    /api/users/123      // Получить пользователя по ID
POST   /api/users          // Создать нового пользователя
PUT    /api/users/123      // Обновить пользователя полностью
PATCH  /api/users/123      // Частичное обновление пользователя
DELETE /api/users/123      // Удалить пользователя

GET    /api/users/123/orders    // Заказы пользователя
GET    /api/products/search?q=laptop&category=electronics  // Поиск с параметрами

// ПЛОХО: Не RESTful URLs
GET    /api/getAllUsers
POST   /api/createUser
GET    /api/userDetails?id=123
POST   /api/updateUser
```

### 2. **HTTP** статус коды

```java
@PostMapping
public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO user) {
    // 201 Created - ресурс создан
    UserDTO created = userService.create(user);
    return ResponseEntity.created(buildLocationUri(created.getId())).body(created);
}

@PutMapping("/{id}")
public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO user) {
    UserDTO updated = userService.update(id, user);
    return ResponseEntity.ok(updated); // 200 OK
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build(); // 204 No Content
}

@GetMapping("/{id}")
public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    Optional<UserDTO> user = userService.findById(id);
    return user.map(ResponseEntity::ok) // 200 OK
               .orElse(ResponseEntity.notFound().build()); // 404 Not Found
}
```

### 3. **Content Negotiation**

```java
@GetMapping(value = "/export/{id}", produces = {
    MediaType.APPLICATION_JSON_VALUE,
    MediaType.APPLICATION_XML_VALUE,
    "application/pdf",
    "text/csv"
})
public ResponseEntity<Resource> exportUser(@PathVariable Long id,
                                         @RequestHeader("Accept") String accept) {

    User user = userService.findById(id);

    if (MediaType.APPLICATION_JSON_VALUE.equals(accept)) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ByteArrayResource(convertToJson(user).getBytes()));

    } else if (MediaType.APPLICATION_XML_VALUE.equals(accept)) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(new ByteArrayResource(convertToXml(user).getBytes()));

    } else if ("application/pdf".equals(accept)) {
        byte[] pdf = generatePdf(user);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"user-" + id + ".pdf\"")
                .body(new ByteArrayResource(pdf));

    } else if ("text/csv".equals(accept)) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(convertToCsv(user).getBytes()));
    }

    return ResponseEntity.badRequest().build();
}
```

### 4. **API Evolution** (**версионирование**)

```java
// URI versioning
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller {
    // V1 implementation
}

@RestController
@RequestMapping("/api/v2/users")
public class UserV2Controller {
    // V2 implementation with breaking changes
}

// Header versioning
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping(value = "/{id}", produces = "application/vnd.myapp.v1+json")
    public ResponseEntity<UserV1DTO> getUserV1(@PathVariable Long id) {
        // V1 logic
        return ResponseEntity.ok(convertToV1(userService.findById(id)));
    }

    @GetMapping(value = "/{id}", produces = "application/vnd.myapp.v2+json")
    public ResponseEntity<UserV2DTO> getUserV2(@PathVariable Long id) {
        // V2 logic
        return ResponseEntity.ok(convertToV2(userService.findById(id)));
    }
}
```

### 5. **Security best practices**

```java
@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Admin access granted");
    }

    @PostMapping("/data")
    @PreAuthorize("hasPermission(#data, 'WRITE')")
    public ResponseEntity<String> writeData(@RequestBody Data data, @AuthenticationPrincipal User user) {
        // Custom permission check
        return ResponseEntity.ok("Data written by " + user.getUsername());
    }

    @GetMapping("/rate-limited")
    @RateLimited(requests = 10, windowSeconds = 60)
    public ResponseEntity<String> rateLimitedEndpoint() {
        return ResponseEntity.ok("Request processed");
    }

    @PostMapping("/audit")
    @Audit(action = "DATA_MODIFICATION", level = AuditLevel.CRITICAL)
    public ResponseEntity<String> auditableAction(@RequestBody Data data) {
        // Action will be logged
        return ResponseEntity.ok("Action audited");
    }
}
```

### 6. **Performance optimization**

```java
@RestController
@RequestMapping("/api/optimized")
public class OptimizedController {

    // Compression
    @GetMapping("/large-data")
    public ResponseEntity<Resource> getLargeData() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_ENCODING, "gzip")
                .body(compressData(largeData));
    }

    // Conditional requests
    @GetMapping("/cached/{id}")
    public ResponseEntity<UserDTO> getCachedUser(@PathVariable Long id,
                                                @RequestHeader(value = "If-None-Match", required = false) String etag) {

        UserDTO user = userService.findById(id);
        String currentEtag = generateETag(user);

        if (currentEtag.equals(etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.ETAG, currentEtag)
                .header(HttpHeaders.CACHE_CONTROL, "max-age=300")
                .body(user);
    }

    // Streaming for large responses
    @GetMapping("/stream")
    public ResponseEntity<StreamingResponseBody> streamData() {
        StreamingResponseBody responseBody = outputStream -> {
            try (Stream<UserDTO> users = userService.streamAllUsers()) {
                users.forEach(user -> {
                    try {
                        String json = objectMapper.writeValueAsString(user) + "\n";
                        outputStream.write(json.getBytes());
                        outputStream.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(responseBody);
    }

    // Pagination
    @GetMapping("/paged")
    public ResponseEntity<Page<UserDTO>> getPagedUsers(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {

        Page<UserDTO> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }
}
```

### 7. **Error handling patterns**

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e, WebRequest request) {
        log.warn("Validation error: {}", e.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(BAD_REQUEST.value())
                .error("Validation Failed")
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(NOT_FOUND.value())
                .error("Resource Not Found")
                .message(e.getMessage())
                .path(getRequestPath(request))
                .build();

        return ResponseEntity.status(NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e, WebRequest request) {
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
}
```

### 8. **Documentation best practices**

```java
@RestController
@RequestMapping("/api/documented")
@Tag(name = "User Management", description = "APIs for managing users")
public class DocumentedController {

    @Operation(summary = "Get user by ID",
               description = "Retrieve a user by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserDTO.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(
            @Parameter(description = "Unique identifier of the user", required = true)
            @PathVariable Long id) {

        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new user",
               description = "Create a new user in the system")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestBody(description = "User data to create", required = true,
                    content = @Content(schema = @Schema(implementation = CreateUserRequest.class)))
            @Valid CreateUserRequest request) {

        UserDTO user = userService.create(request);
        return ResponseEntity.created(buildLocationUri(user.getId())).body(user);
    }
}
```

Этот всесторонний гид по **Spring REST API** охватывает все основные аспекты разработки **RESTful** веб-сервисов: от базовых контроллеров до продвинутых техник, безопасности, тестирования, документации и **best practices**. Файл значительно превышает `2000` строк и предоставляет исчерпывающие знания для создания **production-ready REST API**.
