---
title: "Spring MVC: Полное руководство по веб-фреймворку"
description: "Комплексное руководство по Spring MVC: DispatcherServlet, контроллеры, view resolution, interceptors, exception handling, form handling, file upload и best practices"
tags: ["spring", "mvc", "web", "dispatcherservlet", "controller", "view", "interceptor", "java"]
difficulty: "intermediate"
prerequisites: ["spring/spring-core.md", "spring/spring-boot.md"]
next: ["spring/spring-rest.md", "spring/spring-security.md"]
updated: "2025-01-16"
related: ["spring/spring-boot.md", "java/java-basics.md"]
---

# Spring MVC: Полное руководство по веб-фреймворку

## Введение в Spring MVC

Spring MVC (Model-View-Controller) - это мощный веб-фреймворк, построенный на основе паттерна MVC. Он предоставляет гибкую архитектуру для создания веб-приложений с разделением логики представления, бизнес-логики и данных.

### Основные компоненты Spring MVC

1. **DispatcherServlet**: Front Controller, который обрабатывает все входящие запросы
2. **Handler Mapping**: Определяет, какой контроллер обработает запрос
3. **Controller**: Обрабатывает запрос и возвращает модель и view
4. **View Resolver**: Определяет, какое представление использовать
5. **Model**: Данные, передаваемые в представление

### Архитектура Spring MVC

```
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

DispatcherServlet является центральным компонентом Spring MVC. Он действует как Front Controller и обрабатывает все входящие HTTP-запросы.

### Настройка DispatcherServlet

#### Java Configuration

```java
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

#### XML Configuration

```xml
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

### Жизненный цикл DispatcherServlet

1. **Инициализация**: Загрузка конфигурации и создание ApplicationContext
2. **Обработка запроса**: 
   - Определение Handler
   - Выполнение Interceptors (preHandle)
   - Вызов Handler Method
   - Выполнение Interceptors (postHandle)
   - Обработка исключений
   - Рендеринг View
   - Выполнение Interceptors (afterCompletion)
3. **Уничтожение**: Очистка ресурсов

### Настройка DispatcherServlet в Spring Boot

В Spring Boot DispatcherServlet настраивается автоматически:

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Можно настроить через application.properties:

```properties
# Настройка DispatcherServlet
spring.mvc.servlet.path=/
spring.mvc.throw-exception-if-no-handler-found=true
spring.mvc.static-path-pattern=/static/**
```

## Controllers

Контроллеры в Spring MVC обрабатывают HTTP-запросы и возвращают ответы.

### @Controller

```java
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/list";
    }
    
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/detail";
    }
}
```

### @RestController

```java
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
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

### Request Mapping

#### Базовое маппирование

```java
@Controller
public class MappingController {
    
    @RequestMapping("/home")
    public String home() {
        return "home";
    }
    
    @GetMapping("/users")
    public String getUsers() {
        return "users";
    }
    
    @PostMapping("/users")
    public String createUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";
    }
    
    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, 
                            @ModelAttribute User user) {
        userService.update(id, user);
        return "redirect:/users";
    }
    
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/users";
    }
}
```

#### Path Variables

```java
@Controller
@RequestMapping("/users")
public class UserController {
    
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user/detail";
    }
    
    @GetMapping("/{userId}/orders/{orderId}")
    public String getUserOrder(
            @PathVariable Long userId,
            @PathVariable Long orderId,
            Model model) {
        Order order = orderService.findByUserAndOrder(userId, orderId);
        model.addAttribute("order", order);
        return "order/detail";
    }
    
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

#### Request Parameters

```java
@Controller
@RequestMapping("/users")
public class UserController {
    
    @GetMapping("/search")
    public String searchUsers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        List<User> users = userService.search(name, page, size);
        model.addAttribute("users", users);
        return "users/list";
    }
    
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

#### Request Headers

```java
@Controller
public class HeaderController {
    
    @GetMapping("/data")
    public String getData(@RequestHeader String authorization, Model model) {
        model.addAttribute("auth", authorization);
        return "data";
    }
    
    @GetMapping("/info")
    public String getInfo(
            @RequestHeader("User-Agent") String userAgent,
            Model model) {
        model.addAttribute("userAgent", userAgent);
        return "info";
    }
    
    @GetMapping("/custom")
    public String getCustom(
            @RequestHeader(value = "X-Custom-Header", required = false) String custom,
            Model model) {
        model.addAttribute("custom", custom);
        return "custom";
    }
}
```

#### Request Body

```java
@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PostMapping("/batch")
    public ResponseEntity<List<User>> createUsers(@RequestBody List<User> users) {
        List<User> created = userService.saveAll(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        User updated = userService.update(id, user);
        return ResponseEntity.ok(updated);
    }
}
```

### Model Attributes

#### @ModelAttribute на параметре

```java
@Controller
@RequestMapping("/users")
public class UserController {
    
    @PostMapping
    public String createUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";
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

#### @ModelAttribute на методе

```java
@Controller
@RequestMapping("/users")
public class UserController {
    
    @ModelAttribute("roles")
    public List<Role> populateRoles() {
        return roleService.findAll();
    }
    
    @ModelAttribute("user")
    public User getUser(@PathVariable(required = false) Long id) {
        if (id != null) {
            return userService.findById(id);
        }
        return new User();
    }
    
    @GetMapping("/form")
    public String showForm() {
        return "users/form";
    }
}
```

### Session Attributes

```java
@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {
    
    @ModelAttribute("cart")
    public Cart getCart() {
        return new Cart();
    }
    
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

View Resolver определяет, какое представление использовать для рендеринга ответа.

### InternalResourceViewResolver (JSP)

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}
```

### Thymeleaf View Resolver

```java
@Configuration
public class ThymeleafConfig {
    
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
    
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        return engine;
    }
    
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
}
```

### Content Negotiation

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorParameter(true)
            .parameterName("format")
            .ignoreAcceptHeader(false)
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML);
    }
}
```

### Custom View

```java
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class PdfViewResolver implements ViewResolver {
    
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (viewName.startsWith("pdf:")) {
            String templateName = viewName.substring(4);
            return new PdfView(templateName);
        }
        return null;
    }
}

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
        // ... логика генерации PDF
    }
    
    @Override
    public String getContentType() {
        return "application/pdf";
    }
}
```

## Interceptors

Interceptors позволяют перехватывать запросы и ответы для выполнения дополнительной логики.

### HandlerInterceptor

```java
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        logger.info("Request URL: {}", request.getRequestURL());
        logger.info("Request Method: {}", request.getMethod());
        return true;
    }
    
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

### Регистрация Interceptor

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns("/static/**", "/css/**", "/js/**");
        
        registry.addInterceptor(new AuthenticationInterceptor())
            .addPathPatterns("/admin/**", "/api/**");
    }
}
```

### Authentication Interceptor

```java
public class AuthenticationInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return false;
        }
        
        String token = authHeader.substring(7);
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

Spring MVC предоставляет несколько способов обработки исключений.

### @ExceptionHandler на уровне контроллера

```java
@Controller
@RequestMapping("/users")
public class UserController {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound(UserNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }
    
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

### @ControllerAdvice для глобальной обработки

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error", "An error occurred: " + ex.getMessage());
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "USER_NOT_FOUND",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
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

### @ResponseStatus

```java
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
            throw new UserNotFoundException(id);
        }
        return user;
    }
}
```

## Form Handling

### Простая форма

```java
@Controller
@RequestMapping("/users")
public class UserController {
    
    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }
    
    @PostMapping
    public String submitForm(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";
    }
}
```

### Thymeleaf форма

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
@Controller
@RequestMapping("/users")
public class UserController {
    
    @PostMapping
    public String submitForm(
            @Valid @ModelAttribute User user,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/form";
        }
        userService.save(user);
        return "redirect:/users";
    }
}
```

## File Upload

### Multipart Configuration

```java
@Configuration
public class MultipartConfig {
    
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(10485760); // 10MB
        resolver.setMaxInMemorySize(4096);
        return resolver;
    }
}
```

### File Upload Controller

```java
@Controller
@RequestMapping("/files")
public class FileController {
    
    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file,
            Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "File is empty");
            return "upload/error";
        }
        
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get("uploads/" + file.getOriginalFilename());
            Files.write(path, bytes);
            
            model.addAttribute("message", "File uploaded successfully");
            return "upload/success";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload file");
            return "upload/error";
        }
    }
}
```

## Best Practices

### 1. Используйте @RestController для REST API

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

### 3. Используйте @ControllerAdvice для глобальной обработки ошибок

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

### 5. Используйте правильные HTTP методы

```java
// ✅ Хорошо
@GetMapping("/users")
@PostMapping("/users")
@PutMapping("/users/{id}")
@DeleteMapping("/users/{id}")
```

## Заключение

Spring MVC предоставляет мощный и гибкий фреймворк для создания веб-приложений. Понимание архитектуры DispatcherServlet, контроллеров, view resolution, interceptors и обработки исключений критично для эффективной разработки на Spring MVC.

## Дополнительные ресурсы

- [Spring MVC Documentation](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Boot Web](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html)
- [Baeldung Spring MVC](https://www.baeldung.com/spring-mvc)

