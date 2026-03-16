---
title: "Spring MVC: Полное руководство по веб-фреймворку"
description: "Комплексное руководство по Spring MVC: DispatcherServlet, контроллеры, view resolution, interceptors, exception handling, form handling, file upload и best practices"
tags: ["spring", "mvc", "web", "dispatcherservlet", "controller", "view", "interceptor", "java"]
difficulty: "intermediate"
prerequisites: ["spring/spring-core.md", "spring/spring-boot.md"]
next: ["spring/spring-rest.md", "spring/spring-security.md"]
updated: "2026-02-11"
related: ["spring/spring-boot.md", "java/java-basics.md"]
---

# Spring MVC: Полное руководство по веб-фреймворку



## Полезные ссылки

[Официальная документация Spring](https://docs.spring.io/)
[Spring Projects](https://spring.io/projects)

## Содержание

- [Введение в Spring MVC](#введение-в-spring-mvc)
  - [Основные компоненты Spring MVC](#основные-компоненты-spring-mvc)
  - [Архитектура Spring MVC](#архитектура-spring-mvc)
- [DispatcherServlet](#dispatcherservlet)
  - [Настройка DispatcherServlet](#настройка-dispatcherservlet)
    - [Java Configuration](#java-configuration)
    - [XML Configuration](#xml-configuration)
  - [Жизненный цикл DispatcherServlet](#жизненный-цикл-dispatcherservlet)
  - [Настройка DispatcherServlet в Spring Boot](#настройка-dispatcherservlet-в-spring-boot)
- [Controllers](#controllers)
  - [@Controller](#controller)
  - [@RestController](#restcontroller)
  - [Request Mapping](#request-mapping)
    - [Базовое маппирование](#базовое-маппирование)
    - [Path Variables](#path-variables)
    - [Request Parameters](#request-parameters)
    - [Request Headers](#request-headers)
    - [Request Body](#request-body)
  - [Model Attributes](#model-attributes)
    - [@ModelAttribute на параметре](#modelattribute-на-параметре)
    - [@ModelAttribute на методе](#modelattribute-на-методе)
  - [Session Attributes](#session-attributes)
- [View Resolution](#view-resolution)
  - [InternalResourceViewResolver (JSP)](#internalresourceviewresolver-jsp)
  - [Thymeleaf View Resolver](#thymeleaf-view-resolver)
  - [Content Negotiation](#content-negotiation)
  - [Custom View](#custom-view)
- [Interceptors](#interceptors)
  - [HandlerInterceptor](#handlerinterceptor)
  - [Регистрация Interceptor](#регистрация-interceptor)
  - [Authentication Interceptor](#authentication-interceptor)
- [Exception Handling](#exception-handling)
  - [@ExceptionHandler на уровне контроллера](#exceptionhandler-на-уровне-контроллера)
  - [@ControllerAdvice для глобальной обработки](#controlleradvice-для-глобальной-обработки)
  - [@ResponseStatus](#responsestatus)
- [Form Handling](#form-handling)
  - [Простая форма](#простая-форма)
  - [Thymeleaf форма](#thymeleaf-форма)
  - [Валидация формы](#валидация-формы)
- [File Upload](#file-upload)
  - [Multipart Configuration](#multipart-configuration)
  - [File Upload Controller](#file-upload-controller)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте @RestController для REST API](#1-используйте-restcontroller-для-rest-api)
  - [2. Разделяйте контроллеры по функциональности](#2-разделяйте-контроллеры-по-функциональности)
  - [3. Используйте @ControllerAdvice для глобальной обработки ошибок](#3-используйте-controlleradvice-для-глобальной-обработки-ошибок)
  - [4. Валидируйте входные данные](#4-валидируйте-входные-данные)
  - [5. Используйте правильные HTTP методы](#5-используйте-правильные-http-методы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Spring MVC**

**Spring MVC** (**Model-`View`-Controller**) - это мощный веб-фреймворк, построенный на основе паттерна **MVC**. Он предоставляет гибкую архитектуру для создания веб-приложений с разделением логики представления, бизнес-логики и данных.

### Основные компоненты **Spring MVC**

1. **DispatcherServlet**: **Front Controller**, который обрабатывает все входящие запросы
2. **Handler Mapping**: Определяет, какой контроллер обработает запрос
3. **Controller**: Обрабатывает запрос и возвращает модель и **view**
4. **View Resolver**: Определяет, какое представление использовать
5. **Model**: Данные, передаваемые в представление

### Архитектура **Spring MVC**

```text
# Обработка запроса: DispatcherServlet → HandlerMapping → Controller → View
┌─────────────────────────────────────────────────────────┐
│                    HTTP Request                          │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              DispatcherServlet                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Handler    │  │  Controller │  │   Model &    │  │
│  │   Mapping    │─▶│             │─▶│   View Name │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              View Resolver                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   View       │  │   Template   │  │   Response   │  │
│  │   Selection  │─▶│   Engine     │─▶│   Rendering  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                    HTTP Response                         │
└─────────────────────────────────────────────────────────┘
```

## DispatcherServlet

**DispatcherServlet** является центральным компонентом **Spring MVC**. Он действует как **Front Controller** и обрабатывает все входящие **HTTP**-запросы.

### Настройка **DispatcherServlet**

#### **Java Configuration**

```java
// Регистрация DispatcherServlet и маппинг корня приложения
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer 
        extends AbstractAnnotationConfigDispatcherServletInitializer {
    
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { AppConfig.class };
    }
    
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { WebConfig.class };
    }
    
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
    
    @Override
    protected void customizeRegistration(
            ServletRegistration.Dynamic registration) {
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
    }
}
```

#### **XML Configuration**

```xml
<!-- Регистрация DispatcherServlet в web.xml -->
<web-app>
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>
            org.springframework.web.servlet.DispatcherServlet
        </servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/spring-mvc-config.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```

### Жизненный цикл **DispatcherServlet**

1. **Инициализация**: Загрузка конфигурации и создание **ApplicationContext**
2. **Обработка запроса**:**
   - Определение **Handler**
   - Выполнение **Interceptors** (**preHandle**)
   - Вызов **Handler Method**
   - Выполнение **Interceptors** (**postHandle**)
   - Обработка исключений
   - Рендеринг **View**
   - Выполнение **Interceptors** (**afterCompletion**)
3. **Уничтожение**: Очистка ресурсов

### Настройка **DispatcherServlet** в **Spring Boot**

**В **Spring Boot DispatcherServlet** настраивается автоматически:**

```java
// Spring Boot автоматически настраивает DispatcherServlet
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Можно настроить через **application.properties**:**

```properties
# Настройка DispatcherServlet
spring.mvc.servlet.path=/
spring.mvc.throw-exception-if-no-handler-found=true
spring.mvc.static-path-pattern=/static/
```

## Controllers

Контроллеры в **Spring MVC** обрабатывают **HTTP**-запросы и возвращают ответы.

### @**Controller**

```java
// @Controller - возвращает имя View для рендеринга
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    
    // Получение списка пользователей
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);  // Передача в шаблон
        return "users/list";  // Имя View (Thymeleaf, JSP и т.д.)
    }
    
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/detail";
    }
}
```

### @**RestController**

```java
// @RestController = @Controller + @ResponseBody (JSON/XML ответ)
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
    // Возвращает JSON напрямую (без View)
    @GetMapping
    public List<User> listUsers() {
        return userService.findAll();
    }
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

### **Request Mapping**

#### Базовое маппирование

```java
// Различные HTTP методы маппинга
@Controller
public class MappingController {
    
    // @RequestMapping - универсальный (любой HTTP метод)
    @RequestMapping("/home")
    public String home() {
        return "home";
    }
    
    @GetMapping("/users")  // GET запрос
    public String getUsers() {
        return "users";
    }
    
    @PostMapping("/users")  // POST запрос (создание)
    public String createUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";  // Редирект после POST
    }
    
    @PutMapping("/users/{id}")  // PUT запрос (обновление)
    public String updateUser(@PathVariable Long id, 
                            @ModelAttribute User user) {
        userService.update(id, user);
        return "redirect:/users";
    }
    
    @DeleteMapping("/users/{id}")  // DELETE запрос (удаление)
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/users";
    }
}
```

#### **Path Variables**

```java
// @PathVariable - извлечение параметров из URL пути
@Controller
@RequestMapping("/users")
public class UserController {
    
    // /users/123 -> id = 123
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user/detail";
    }
    
    // Несколько path variables: /users/1/orders/42
    @GetMapping("/{userId}/orders/{orderId}")
    public String getUserOrder(
            @PathVariable Long userId,
            @PathVariable Long orderId,
            Model model) {
        Order order = orderService.findByUserAndOrder(userId, orderId);
        model.addAttribute("order", order);
        return "order/detail";
    }
    
    // Вложенные ресурсы: /users/1/posts/2/comments/3
    @GetMapping("/{id}/posts/{postId}/comments/{commentId}")
    public String getComment(
            @PathVariable Long id,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Model model) {
        Comment comment = commentService.find(id, postId, commentId);
        model.addAttribute("comment", comment);
        return "comment/detail";
    }
}
```

#### **Request Parameters**

```java
// @RequestParam - извлечение параметров из query string (?name=value)
@Controller
@RequestMapping("/users")
public class UserController {
    
    // /users/search?name=John&page=0&size=20
    @GetMapping("/search")
    public String searchUsers(
            @RequestParam String name,  // Обязательный параметр
            @RequestParam(defaultValue = "0") int page,  // Со значением по умолчанию
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        List<User> users = userService.search(name, page, size);
        model.addAttribute("users", users);
        return "users/list";
    }
    
    // Опциональные параметры (required = false)
    @GetMapping("/filter")
    public String filterUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer age,
            Model model) {
        List<User> users = userService.filter(name, email, age);
        model.addAttribute("users", users);
        return "users/list";
    }
    
    // Коллекция: /users/tags?tags=java&tags=spring
    @GetMapping("/tags")
    public String getUsersByTags(
            @RequestParam List<String> tags,
            Model model) {
        List<User> users = userService.findByTags(tags);
        model.addAttribute("users", users);
        return "users/list";
    }
}
```

#### **Request Headers**

```java
// @RequestHeader - извлечение HTTP заголовков
@Controller
public class HeaderController {
    
    // Заголовок по имени параметра
    @GetMapping("/data")
    public String getData(@RequestHeader String authorization, Model model) {
        model.addAttribute("auth", authorization);
        return "data";
    }
    
    // Явное указание имени заголовка
    @GetMapping("/info")
    public String getInfo(
            @RequestHeader("User-Agent") String userAgent,
            Model model) {
        model.addAttribute("userAgent", userAgent);
        return "info";
    }
    
    // Опциональный заголовок
    @GetMapping("/custom")
    public String getCustom(
            @RequestHeader(value = "X-Custom-Header", required = false) String custom,
            Model model) {
        model.addAttribute("custom", custom);
        return "custom";
    }
}
```

#### **Request Body**

```java
// @RequestBody - десериализация JSON/XML из тела запроса
@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
    // JSON -> объект User
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // Коллекция объектов из JSON массива
    @PostMapping("/batch")
    public ResponseEntity<List<User>> createUsers(@RequestBody List<User> users) {
        List<User> created = userService.saveAll(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // Комбинация @PathVariable и @RequestBody
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        User updated = userService.update(id, user);
        return ResponseEntity.ok(updated);
    }
}
```

### **Model Attributes**

#### @**ModelAttribute** на параметре

```java
// @ModelAttribute - привязка данных формы к объекту
@Controller
@RequestMapping("/users")
public class UserController {
    
    // Данные из формы (form-data) -> объект User
    @PostMapping
    public String createUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";  // Паттерн PRG (Post-Redirect-Get)
    }
    
    @PutMapping("/{id}")
    public String updateUser(
            @PathVariable Long id,
            @ModelAttribute User user) {
        userService.update(id, user);
        return "redirect:/users";
    }
}
```

#### @**ModelAttribute** на методе

```java
// @ModelAttribute на методе - заполнение Model перед обработчиком
@Controller
@RequestMapping("/users")
public class UserController {
    
    // Вызывается перед каждым обработчиком - добавляет "roles" в Model
    @ModelAttribute("roles")
    public List<Role> populateRoles() {
        return roleService.findAll();
    }
    
    // Условная логика для получения или создания объекта
    @ModelAttribute("user")
    public User getUser(@PathVariable(required = false) Long id) {
        if (id != null) {
            return userService.findById(id);  // Редактирование
        }
        return new User();  // Создание
    }
    
    @GetMapping("/form")
    public String showForm() {
        return "users/form";  // roles и user уже в Model
    }
}
```

### **Session Attributes**

```java
// @SessionAttributes - хранение данных между запросами в сессии
@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")  // Сохраняет "cart" в HTTP сессии
public class CartController {
    
    // Создание корзины при первом обращении
    @ModelAttribute("cart")
    public Cart getCart() {
        return new Cart();
    }
    
    // Модификация корзины сохраняется в сессии
    @PostMapping("/add")
    public String addToCart(
            @ModelAttribute("cart") Cart cart,
            @RequestParam Long productId) {
        Product product = productService.findById(productId);
        cart.addItem(product);
        return "redirect:/cart";
    }
    
    @GetMapping
    public String viewCart(@ModelAttribute("cart") Cart cart, Model model) {
        model.addAttribute("cart", cart);
        return "cart/view";
    }
}
```

## View Resolution

**View Resolver** определяет, какое представление использовать для рендеринга ответа.

### **InternalResourceViewResolver** (**JSP**)

```java
// Конфигурация ViewResolver для JSP
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    // InternalResourceViewResolver - для JSP шаблонов
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");  // Путь к JSP файлам
        resolver.setSuffix(".jsp");  // Расширение
        return resolver;
    }
}
```

### **Thymeleaf View Resolver**

```java
// Конфигурация Thymeleaf шаблонизатора
@Configuration
public class ThymeleafConfig {
    
    // Resolver для поиска шаблонов
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");  // Путь к шаблонам
        resolver.setSuffix(".html");  // Расширение файлов
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
    
    // Движок шаблонизатора
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        return engine;
    }
    
    // View Resolver для интеграции с Spring MVC
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
}
```

### **Content Negotiation**

```java
// Content Negotiation - выбор формата ответа (JSON/XML)
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorParameter(true)  // Разрешить ?format=json
            .parameterName("format")
            .ignoreAcceptHeader(false)  // Учитывать Accept header
            .defaultContentType(MediaType.APPLICATION_JSON)  // По умолчанию JSON
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML);
    }
}
```

### **Custom View**

```java
// Кастомный ViewResolver для генерации PDF
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class PdfViewResolver implements ViewResolver {
    
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        // Обработка view имён вида "pdf:report"
        if (viewName.startsWith("pdf:")) {
            String templateName = viewName.substring(4);
            return new PdfView(templateName);
        }
        return null;  // Передать следующему resolver
    }
}

// Кастомный View для рендеринга PDF
public class PdfView implements View {
    private final String templateName;
    
    public PdfView(String templateName) {
        this.templateName = templateName;
    }
    
    @Override
    public void render(Map<String, ?> model, 
                      HttpServletRequest request,
                      HttpServletResponse response) throws Exception {
        // Генерация PDF из шаблона
        response.setContentType("application/pdf");
        // ... логика генерации PDF с использованием iText/Flying Saucer
    }
    
    @Override
    public String getContentType() {
        return "application/pdf";
    }
}
```

## Interceptors

**Interceptors** позволяют перехватывать запросы и ответы для выполнения дополнительной логики.

### **HandlerInterceptor**

```java
// HandlerInterceptor - перехватчик HTTP запросов
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    // preHandle - выполняется ДО обработчика (return false = прерывание)
    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);  // Сохраняем время старта
        logger.info("Request URL: {}", request.getRequestURL());
        logger.info("Request Method: {}", request.getMethod());
        return true;  // Продолжить выполнение
    }
    
    // postHandle - выполняется ПОСЛЕ обработчика, но ДО рендеринга View
    @Override
    public void postHandle(HttpServletRequest request,
                          HttpServletResponse response,
                          Object handler,
                          ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        logger.info("Request processing time: {}ms", executeTime);
        
        if (modelAndView != null) {
            modelAndView.addObject("executeTime", executeTime);
        }
    }
    
    // afterCompletion - выполняется ПОСЛЕ рендеринга View (всегда)
    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) throws Exception {
        if (ex != null) {
            logger.error("Exception occurred: ", ex);
        }
        logger.info("Request completed");
    }
}
```

### Регистрация **Interceptor**

```java
// Регистрация Interceptor в конфигурации
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Логирование для всех URL кроме статики
        registry.addInterceptor(new LoggingInterceptor())
            .addPathPatterns("/**")  // Все пути
            .excludePathPatterns("/static/**", "/css/**", "/js/**");  // Исключения
        
        // Аутентификация только для admin и api
        registry.addInterceptor(new AuthenticationInterceptor())
            .addPathPatterns("/admin/**", "/api/**");
    }
}
```

### **Authentication Interceptor**

```java
// Interceptor для проверки JWT токена
public class AuthenticationInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        
        // Проверка наличия Bearer токена
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return false;  // Прерывание запроса
        }
        
        String token = authHeader.substring(7);  // Извлечение токена
        if (!isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Invalid token\"}");
            return false;
        }
        
        return true;
    }
    
    private boolean isValidToken(String token) {
        // Валидация токена
        return true;
    }
}
```

## Exception Handling

**Spring MVC** предоставляет несколько способов обработки исключений.

### @**ExceptionHandler** на уровне контроллера

```java
// @ExceptionHandler на уровне контроллера - локальная обработка
@Controller
@RequestMapping("/users")
public class UserController {
    
    // Обработка UserNotFoundException только в этом контроллере
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound(UserNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }
    
    // Возврат JSON ошибки через ResponseEntity
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            ex.getMessage(),
            ex.getErrors()
        );
        return ResponseEntity.badRequest().body(error);
    }
}
```

### @**ControllerAdvice** для глобальной обработки

```java
// @ControllerAdvice - глобальная обработка исключений для всех контроллеров
@ControllerAdvice
public class GlobalExceptionHandler {
    
    // Fallback для всех необработанных исключений
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "An error occurred: " + ex.getMessage());
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
    
    // Обработка конкретного типа исключения
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "USER_NOT_FOUND",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // Обработка ошибок валидации @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Сбор всех ошибок полей
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed",
            errors
        );
        return ResponseEntity.badRequest().body(error);
    }
}
```

### @**ResponseStatus**

```java
// @ResponseStatus - автоматический HTTP статус при выбросе исключения
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }
}

@Controller
@RequestMapping("/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);  // Автоматически 404
        }
        return user;
    }
}
```

## Form Handling

### Простая форма

```java
// Базовый паттерн формы: GET показывает, POST обрабатывает
@Controller
@RequestMapping("/users")
public class UserController {
    
    // Показать пустую форму
    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("user", new User());  // Пустой объект для формы
        return "users/form";
    }
    
    // Обработка отправленной формы
    @PostMapping
    public String submitForm(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";  // PRG: Post-Redirect-Get
    }
}
```

### **Thymeleaf** форма

```html
<form th:action="@{/users}" th:object="${user}" method="post">
    <div>
        <label>Name:</label>
        <input type="text" th:field="*{name}" />
        <span th:if="${#fields.hasErrors('name')}" th:errors="*{name}"></span>
    </div>
    
    <div>
        <label>Email:</label>
        <input type="email" th:field="*{email}" />
        <span th:if="${#fields.hasErrors('email')}" th:errors="*{email}"></span>
    </div>
    
    <div>
        <label>Age:</label>
        <input type="number" th:field="*{age}" />
        <span th:if="${#fields.hasErrors('age')}" th:errors="*{age}"></span>
    </div>
    
    <button type="submit">Submit</button>
</form>
```

### Валидация формы

```java
// Валидация с @Valid и BindingResult
@Controller
@RequestMapping("/users")
public class UserController {
    
    @PostMapping
    public String submitForm(
            @Valid @ModelAttribute User user,  // @Valid активирует JSR-380
            BindingResult bindingResult) {  // Результаты валидации
        if (bindingResult.hasErrors()) {
            return "users/form";  // Вернуть форму с ошибками
        }
        userService.save(user);
        return "redirect:/users";
    }
}
```

## File Upload

### **Multipart Configuration**

```java
// Конфигурация загрузки файлов
@Configuration
public class MultipartConfig {
    
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(10485760);  // Максимум 10MB
        resolver.setMaxInMemorySize(4096);  // Порог записи на диск
        return resolver;
    }
}
```

### **File Upload Controller**

```java
// Контроллер загрузки файлов
@Controller
@RequestMapping("/files")
public class FileController {
    
    // Обработка multipart/form-data с файлом
    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file,  // Файл из формы
            Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "File is empty");
            return "upload/error";
        }
        
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get("uploads/" + file.getOriginalFilename());
            Files.write(path, bytes);  // Сохранение на диск
            
            model.addAttribute("message", "File uploaded successfully");
            return "upload/success";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload file");
            return "upload/error";
        }
    }
}
```

## Лучшие практики

### 1. Используйте @**RestController** для **REST API**

```java
// ✅ Хорошо
@RestController
@RequestMapping("/api/users")
public class UserRestController {
    // ...
}

// ❌ Плохо - для REST API
@Controller
@RequestMapping("/api/users")
public class UserController {
    // ...
}
```

### 2. Разделяйте контроллеры по функциональности

```java
// ✅ Хорошо
@Controller
@RequestMapping("/users")
public class UserController {
    // Только операции с пользователями
}

@Controller
@RequestMapping("/orders")
public class OrderController {
    // Только операции с заказами
}
```

### 3. Используйте @**ControllerAdvice** для глобальной обработки ошибок

```java
// ✅ Хорошо
@ControllerAdvice
public class GlobalExceptionHandler {
    // Централизованная обработка ошибок
}
```

### 4. Валидируйте входные данные

```java
// ✅ Хорошо
@PostMapping
public String createUser(@Valid @ModelAttribute User user, BindingResult result) {
    if (result.hasErrors()) {
        return "users/form";
    }
    // ...
}
```

### 5. Используйте правильные **HTTP** методы

```java
// ✅ Хорошо
@GetMapping("/users")
@PostMapping("/users")
@PutMapping("/users/{id}")
@DeleteMapping("/users/{id}")
```


## Заключение

**Spring MVC** предоставляет мощный и гибкий фреймворк для создания веб-приложений. Понимание архитектуры **DispatcherServlet**, контроллеров, **view resolution**, **interceptors** и обработки исключений критично для эффективной разработки на **Spring MVC**.

## Дополнительные ресурсы

- [**Spring MVC** Documentation](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [**Spring Boot** Web](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html)
- [Baeldung **Spring MVC**](https://www.baeldung.com/spring-mvc-tutorial)
