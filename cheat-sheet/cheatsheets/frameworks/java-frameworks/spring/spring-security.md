---
title: "Spring Security"
description: "Комплексное руководство по Spring Security: аутентификация, авторизация, OAuth 2.0, JWT, безопасность REST API, защита от уязвимостей"
tags: ["spring-security", "security", "authentication", "authorization", "oauth2", "jwt", "rest-api"]
difficulty: "intermediate"
prerequisites: ["spring/spring-boot.md", "java/java-basics.md"]
next: ["interview/spring-security.md"]
updated: "2026-01-11"
---

# Spring Security

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [Введение](#введение)
- [Руководство по Spring Security](#руководство-по-spring-security)
  - [Настройка Spring Security](#настройка-spring-security)
  - [Аннотация @Secured](#аннотация-secured)
  - [Аннотация @RolesAllowed](#аннотация-rolesallowed)
  - [Аннотации @PreAuthorize и @PostAuthorize](#аннотации-preauthorize-и-postauthorize)
  - [@PostAuthorize](#postauthorize)
  - [Аннотации @PreFilter и @PostFilter](#аннотации-prefilter-и-postfilter)
  - [Мета-аннотации безопасности](#мета-аннотации-безопасности)
  - [Аннотации на уровне класса](#аннотации-на-уровне-класса)
  - [Комбинирование аннотаций безопасности](#комбинирование-аннотаций-безопасности)
  - [Важные замечания](#важные-замечания)
  - [Тестирование Spring Security](#тестирование-spring-security)
    - [@WithMockUser](#withmockuser)
    - [@WithAnonymousUser](#withanonymoususer)
    - [@WithUserDetails](#withuserdetails)
  - [Мета-аннотации для тестирования](#мета-аннотации-для-тестирования)
- [Процесс регистрации](#процесс-регистрации)
  - [DTO для регистрации](#dto-для-регистрации)
  - [Валидация электронной почты](#валидация-электронной-почты)
  - [Валидация пароля](#валидация-пароля)
  - [Контроллер регистрации](#контроллер-регистрации)
  - [UserService](#userservice)
  - [UserDetailsService](#userdetailsservice)
- [Активировать новую учетную запись по электронной почте](#активировать-новую-учетную-запись-по-электронной-почте)
  - [Событие регистрации](#событие-регистрации)
  - [Подтверждение регистрации](#подтверждение-регистрации)
  - [Проверка enabled пользователя](#проверка-enabled-пользователя)
- [Сделать API регистрации RESTful](#сделать-api-регистрации-restful)
  - [Обработка исключений](#обработка-исключений)

## Полезные ссылки

- [Официальная документация Spring Security](https://docs.spring.io/spring-security/reference/index.html)

## Введение

**Аутентификация** - процедура проверки подлинности, например, проверка подлинности пользователя путем сравнения введенного им пароля с паролем, сохраненным в базе данных.

**Авторизация** - предоставление определенному лицу или группе лиц прав на выполнение определенных действий.

## Руководство по Spring Security

Как правило, мы могли бы защитить наш сервисный уровень, например, ограничив роли, которые могут выполнять определенный метод, и протестировать его с помощью специальной поддержки тестирования безопасности на уровне метода**.

В этом руководстве мы рассмотрим использование некоторых аннотаций безопасности**. Затем мы сосредоточимся на тестировании безопасности нашего метода с помощью различных стратегий**.

### Настройка Spring Security

Во-первых, чтобы использовать **Spring** **Method** **Security,** нам нужно добавить зависимость **spring-security-config:**

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
</dependency>
```

Если мы хотим использовать **Spring** **Boot,** мы можем использовать зависимость **spring-boot-starter-security,** которая включает **spring-security-config:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Далее нам нужно включить глобальную безопасность методов:

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {}
```

1. Свойство **prePostEnabled** включает аннотации Spring Security до/после.
2. Свойство **secureEnabled** определяет, следует ли включить аннотацию **@Secured.**
3. Свойство **jsr250Enabled** позволяет нам использовать аннотацию **@RoleAllowed.**

### Аннотация @Secured

Аннотация **@Secured** используется для указания списка ролей в методе**. Таким образом, пользователь может получить доступ к этому методу только в том случае, если у него есть хотя бы одна из указанных ролей**.

Давайте определим метод **getUsername:**

```java
@Secured("ROLE_VIEWER")
public String getUsername() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return securityContext.getAuthentication().getName();
}
```

Здесь аннотация **@Secured(**"**ROLE_VIEWER**"**)** определяет, что только пользователи с ролью **ROLE_VIEWER** могут выполнять метод **getUsername.**

Кроме того, мы можем определить список ролей в аннотации **@Secured:**

```java
@Secured({"ROLE_VIEWER", "ROLE_EDITOR"})
public boolean isValidUsername(String username) {
    return userRoleRepository.isValidUsername(username);
}
```

В этом случае в конфигурации указано, что если у пользователя есть **ROLE_VIEWER** или **ROLE_EDITOR,** этот пользователь может вызвать метод **isValidUsername.**

### Аннотация @RolesAllowed

Аннотация **@RoleAllowed** является эквивалентной аннотацией **JSR-250** аннотации **@Secured.**

По сути, мы можем использовать аннотацию **@RoleAllowed** аналогично **@Secured.**

Таким образом, мы могли бы переопределить методы **getUsername** и **isValidUsername:**

```java
@RolesAllowed("ROLE_VIEWER")
public String getUsername2() {
    // ...
}

@RolesAllowed({"ROLE_VIEWER", "ROLE_EDITOR"})
public boolean isValidUsername2(String username) {
    // ...
}
```

Точно так же только пользователь с ролью **ROLE_VIEWER** может выполнить **getUsername2.**

Опять же, пользователь может вызывать **isValidUsername2** только в том случае, если у него есть хотя бы одна из ролей **ROLE_VIEWER** или **ROLER_EDITOR.**

### Аннотации @PreAuthorize и @PostAuthorize

Аннотации **@PreAuthorize** и **@PostAuthorize** обеспечивают управление доступом на основе выражений**. Итак, предикаты можно писать с помощью **SpEL** **(Spring** **Expression** **Language).**

Аннотация **@PreAuthorize** проверяет данное выражение перед входом в метод, тогда как аннотация **@PostAuthorize** проверяет его после выполнения метода и может изменить результат**.

Теперь давайте объявим метод **getUsernameInUpperCase,** как показано ниже:

```java
@PreAuthorize("hasRole('ROLE_VIEWER')")
public String getUsernameInUpperCase() {
    return getUsername().toUpperCase();
}
```

**@PreAuthorize ("hasRole('ROLE_VIEWER')")** имеет то же значение, что и **@Secured("ROLE_VIEWER"),** которое мы использовали в предыдущем разделе**. Следовательно, аннотацию **@Secured({"ROLE_VIEWER", "ROLE_EDITOR"})** можно заменить на **@PreAuthorize("hasRole('ROLE_VIEWER')** **or** **hasRole('ROLE_EDITOR')"):**

```java
@PreAuthorize("hasRole('ROLE_VIEWER') or hasRole('ROLE_EDITOR')")
public boolean isValidUsername3(String username) {
    // ...
}
```

Более того, мы можем использовать аргумент метода как часть выражения:

```java
@PreAuthorize("#username == authentication.principal.username")
public String getMyRoles(String username) {
    // ...
}
```

Здесь пользователь может вызывать метод **getMyRoles** только в том случае, если значение аргумента имя пользователя совпадает с именем пользователя текущего принципала**.

### @PostAuthorize

Стоит отметить, что выражения **@PreAuthorize** можно заменить выражениями **@PostAuthorize.**

Давайте перепишем **getMyRoles:**

```java
@PostAuthorize("#username == authentication.principal.username")
public String getMyRoles2(String username) {
    // ...
}
```

Однако в предыдущем примере авторизация задерживалась после выполнения целевого метода**.

Кроме того, аннотация **@PostAuthorize** предоставляет возможность доступа к результату метода:

```java
@PostAuthorize("returnObject.username == authentication.principal.nickName")
public CustomUser loadUserDetail(String username) {
    return userRoleRepository.loadUserByUserName(username);
}
```

Здесь метод **loadUserDetail** будет успешно выполнен только в том случае, если имя пользователя возвращенного **CustomUser** равно псевдониму текущего принципала проверки подлинности**.

В этом разделе мы в основном используем простые выражения **Spring.** Для более сложных сценариев мы можем создавать собственные выражения безопасности**.

### Аннотации @PreFilter и @PostFilter

**Spring** **Security** предоставляет аннотацию **@PreFilter** для фильтрации аргумента коллекции перед выполнением метода:

```java
@PreFilter("filterObject!= authentication.principal.username")
public String joinUsernames(List<String> usernames) {
    return usernames.stream().collect(Collectors.joining(";"));
}
```

В этом примере мы объединяем все имена пользователей, кроме того, который прошел проверку подлинности**.

Здесь в нашем выражении мы используем имя **filterObject** для представления текущего объекта в коллекции**.

Однако, если метод имеет более одного аргумента, который является типом коллекции, нам нужно использовать свойство **filterTarget,** чтобы указать, какой аргумент мы хотим отфильтровать:

```java
@PreFilter(value = "filterObject!= authentication.principal.username", filterTarget = "usernames")
public String joinUsernamesAndRoles(List<String> usernames, List<String> roles) {
    return usernames.stream().collect(Collectors.joining(";"))
        + ":" + roles.stream().collect(Collectors.joining(";"));
}
```

Кроме того, мы также можем отфильтровать возвращаемую коллекцию метода с помощью аннотации **@PostFilter:**

```java
@PostFilter("filterObject!= authentication.principal.username")
public List<String> getAllUsernamesExceptCurrent() {
    return userRoleRepository.getAllUsernames();
}
```

В этом случае имя **filterObject** относится к текущему объекту в возвращаемой коллекции**.

С этой конфигурацией **Spring** **Security** будет перебирать возвращенный список и удалять все значения, соответствующие имени пользователя принципала**.

### Мета-аннотации безопасности

Обычно мы оказываемся в ситуации, когда мы защищаем разные методы, используя одну и ту же конфигурацию безопасности**.

В этом случае мы можем определить мета-аннотацию безопасности:

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('VIEWER')")
public @interface IsViewer {
}
```

Затем мы можем напрямую использовать аннотацию **@IsViewer** для защиты нашего метода:

```java
@IsViewer
public String getUsername4() {
    // ...
}
```

Мета-аннотации безопасности - отличная идея, потому что они добавляют больше семантики и отделяют нашу бизнес-логику от структуры безопасности**.

### Аннотации на уровне класса

Если мы обнаружим, что используем одну и ту же аннотацию безопасности для каждого метода в одном классе, мы можем рассмотреть возможность размещения этой аннотации на уровне класса:

```java
@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SystemService {
    public String getSystemYear() {
        // ...
    }
    
    public String getSystemDate() {
        // ...
    }
}
```

В приведенном выше примере правило безопасности **hasRole(**'**ROLE_ADMIN**'**)** будет применяться как к методам **getSystemYear,** так и к методам **getSystemDate.**

### Комбинирование аннотаций безопасности

Мы также можем использовать несколько аннотаций безопасности в одном методе:

```java
@PreAuthorize("#username == authentication.principal.username")
@PostAuthorize("returnObject.username == authentication.principal.nickName")
public CustomUser securedLoadUserDetail(String username) {
    return userRoleRepository.loadUserByUserName(username);
}
```

Таким образом**, Spring** проверит авторизацию как до, так и после выполнения метода **secureLoadUserDetail.**

### Важные замечания

Есть два момента, которые мы хотели бы напомнить в отношении безопасности методов:

1. По умолчанию проксирование **Spring** **AOP** используется для применения безопасности метода**. Если защищенный метод **A** вызывается другим методом в том же классе, безопасность в **A** полностью игнорируется**. Это означает, что метод **A** будет выполняться без какой-либо проверки безопасности**. То же самое относится и к частным методам**.
2. **Spring** **SecurityContext** привязан к потоку**. По умолчанию контекст безопасности не распространяется на дочерние потоки**. Для получения дополнительной информации обратитесь к нашей статье Распространение контекста безопасности **Spring.**

### Тестирование Spring Security

Чтобы протестировать **Spring** **Security** с помощью **JUnit,** нам нужна зависимость **spring-security-test:**

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
</dependency>
```

Затем давайте настроим простой тест **Spring** **Integration,** указав бегун и конфигурацию **ApplicationContext:**

```java
@RunWith(SpringRunner.class)
@ContextConfiguration
public class MethodSecurityIntegrationTest {
    // ...
}
```

#### @WithMockUser

Поскольку здесь мы используем аннотацию **@Secured,** для вызова метода требуется, чтобы пользователь прошел аутентификацию**. В противном случае мы получим исключение **AuthenticationCredentialsNotFoundException.**

Итак, нам нужно предоставить пользователю возможность протестировать наш защищенный метод**.

Для этого мы украшаем тестовый метод **@WithMockUser** и предоставляем пользователя и роли:

```java
@Test
@WithMockUser(username = "john", roles = {"VIEWER"})
public void givenRoleViewer_whenCallGetUsername_thenReturnUsername() {
    String userName = userRoleService.getUsername();
    assertEquals("john", userName);
}
```

Мы предоставили аутентифицированного пользователя с именем пользователя **john** и ролью **ROLE_VIEWER.** Если мы не указываем имя пользователя или роль, имя пользователя по умолчанию - **user,** а роль по умолчанию - **ROLE_USER.**

Обратите внимание, что здесь нет необходимости добавлять префикс **ROLE_,** потому что **Spring** **Security** добавит этот префикс автоматически**.

Если мы не хотим иметь этот префикс, мы можем рассмотреть возможность использования полномочий вместо роли**.

Например, объявим метод **getUsernameInLowerCase:**

```java
@PreAuthorize("hasAuthority('SYS_ADMIN')")
public String getUsernameLC() {
    return getUsername().toLowerCase();
}
```

Мы могли бы проверить это, используя авторитеты:

```java
@Test
@WithMockUser(username = "JOHN", authorities = {"SYS_ADMIN"})
public void givenAuthoritySysAdmin_whenCallGetUsernameLC_thenReturnUsername() {
    String username = userRoleService.getUsernameInLowerCase();
    assertEquals("john", username);
}
```

Удобно, если мы хотим использовать одного и того же пользователя для многих тестовых случаев, мы можем объявить аннотацию **@WithMockUser** в тестовом классе:

```java
@RunWith(SpringRunner.class)
@ContextConfiguration
@WithMockUser(username = "john", roles = {"VIEWER"})
public class MockUserAtClassLevelIntegrationTest {
    // ...
}
```

#### @WithAnonymousUser

Если бы мы хотели запустить наш тест как анонимный пользователь, мы могли бы использовать аннотацию **@WithAnonymousUser:**

```java
@Test(expected = AccessDeniedException.class)
@WithAnonymousUser
public void givenAnomynousUser_whenCallGetUsername_thenAccessDenied() {
    userRoleService.getUsername();
}
```

В приведенном выше примере мы ожидаем возникновения **AccessDeniedException,** поскольку анонимному пользователю не предоставлена ​​роль **ROLE_VIEWER** или полномочия **SYS_ADMIN.**

#### @WithUserDetails

Для большинства приложений обычно используется пользовательский класс в качестве принципала проверки подлинности**. В этом случае пользовательский класс должен реализовать **org.springframework.security.core.userdetails.** Интерфейс сведений о пользователе**.

В этой статье мы объявляем класс **CustomUser,** который расширяет существующую реализацию **UserDetails,** то есть **org.springframework.security.core.userdetails.** Пользователь:

```java
public class CustomUser extends User {
    private String nickName;
    // ...
}
```

Если бы мы хотели протестировать этот метод, мы могли бы предоставить реализацию **UserDetailsService,** которая могла бы загружать нашего **CustomUser** на основе имени пользователя:

```java
@Test
@WithUserDetails(value = "john", userDetailsServiceBeanName = "userDetailService")
public void whenJohn_callLoadUserDetail_thenOK() {
    CustomUser user = userService.loadUserDetail("jane");
    assertEquals("jane", user.getNickName());
}
```

Здесь аннотация **@WithUserDetails** указывает, что мы будем использовать **UserDetailsService** для инициализации нашего аутентифицированного пользователя**. На службу ссылается свойство **userDetailsServiceBeanName.** Этот **UserDetailsService** может быть реальной реализацией или подделкой для целей тестирования**.

Кроме того, служба будет использовать значение свойства **value** в качестве имени пользователя для загрузки **UserDetails.**

Для удобства мы также можем украсить аннотацией **@WithUserDetails** на уровне класса, аналогично тому, что мы сделали с аннотацией **@WithMockUser.**

### Мета-аннотации для тестирования

Мы часто обнаруживаем, что снова и снова используем одних и тех же пользователей**/**ролей в различных тестах**.

Для таких ситуаций удобно создать мета-аннотацию**.

Снова взглянув на предыдущий пример **@WithMockUser(username=**"**john**"**, roles=** {"**VIEWER**"}**),** мы можем объявить мета-аннотацию:

```java
@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = "john", roles = "VIEWER")
public @interface WithMockJohnViewer {}
```

Затем мы можем просто использовать **@WithMockJohnViewer** в нашем тесте:

```java
@Test
@WithMockJohnViewer
public void givenMockedJohnViewer_whenCallGetUsername_thenReturnUsername() {
    String userName = userRoleService.getUsername();
    assertEquals("john", userName);
}
```

Точно так же мы можем использовать мета-аннотации для создания доменных пользователей с помощью **@WithUserDetails.**

## Процесс регистрации

Во-первых, давайте реализуем простую страницу регистрации, отображающую следующие поля:

1. Имя **(**имя и фамилия**)**
2. Электронную почту
3. Пароль **(**и поле подтверждения пароля**)**

### DTO для регистрации

Создадим **DTO** для регистрации пользователя:

```java
@PasswordMatches
public class UserDto {
    @NotNull
    @NotEmpty
    private String firstName;
    
    @NotNull
    @NotEmpty
    private String lastName;
    
    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;
    
    @NotNull
    @NotEmpty
    private String password;
    
    private String matchingPassword;
    
    // getters and setters
}
```

Обратите внимание, что мы использовали стандартные аннотации **javax.validation** для полей объекта **DTO.** Позже мы также собираемся реализовать наши собственные аннотации проверки для проверки формата адреса электронной почты, а также для подтверждения пароля**.

### Валидация электронной почты

Давайте создадим пользовательский валидатор для проверки адреса электронной почты**. Мы собираемся создать для этого пользовательский валидатор, а также пользовательскую аннотацию валидации - назовем это **@ValidEmail.**

Небольшое примечание**: мы запускаем нашу собственную аннотацию вместо **@Email** **Hibernate,** потому что **Hibernate** считает допустимым старый формат адресов интрасети**: **myaddress@myserver,** что не годится**.

Вот аннотация для проверки электронной почты и настраиваемый валидатор:

```java
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
    String message() default "Invalid email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

Обычный **EmailValidator**:

```java
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    private Pattern pattern;
    private Matcher matcher;
    private static final String EMAIL_PATTERN = 
        "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" 
        + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Z]{2,})$";
    
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return validateEmail(email);
    }
    
    private boolean validateEmail(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
```

### Валидация пароля

Нам также нужны настраиваемые аннотации и валидатор, чтобы убедиться, что поля пароля и **matchPassword** совпадают:

Пользовательская аннотация для проверки подтверждения пароля:

```java
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
    String message() default "Passwords don't match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

Пользовательский валидатор, который будет вызываться этой аннотацией, показан ниже:

```java
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        UserDto user = (UserDto) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}
```

Теперь к нашему объекту **UserDto** нужно применить аннотацию **@PasswordMatches:**

```java
@PasswordMatches
public class UserDto {
    // ...
}
```

### Контроллер регистрации

**Sign-Up** ссылка на входе странице будет принимать пользователь на регистрационную страницу**. Этот бэкэнд для этой страницы находится в контроллере регистрации и отображается в **«/user/registration»:**

```java
@GetMapping("/user/registration")
public String showRegistrationForm(WebRequest request, Model model) {
    UserDto userDto = new UserDto();
    model.addAttribute("user", userDto);
    return "registration";
}
```

Когда контроллер получает запрос **«/user/registration»,** он создает новый объект **UserDto,** который поддерживает регистрационную форму, связывает ее и возвращает - довольно просто**.

**Далее -** давайте посмотрим на проверки, которые контроллер будет выполнять при регистрации новой учетной записи:

1. Все обязательные поля заполнены **(**нет пустых или пустых полей**)**
2. Электронный адрес действителен **(**правильно сформирован**)**
3. Поле подтверждения пароля совпадает с полем пароля
4. Аккаунт еще не существует

Для простых проверок мы будем использовать готовые аннотации проверки **bean-**компонентов для объекта **DTO** - такие аннотации, как **@NotNull, @NotEmpty** и т**. д**.

**Чтобы запустить процесс проверки, мы просто аннотируем объект на уровне контроллера с помощью аннотации @Valid:**

```java
@PostMapping("/user/registration")
public ModelAndView registerUserAccount(
    @ModelAttribute("user") @Valid UserDto userDto,
    HttpServletRequest request, Errors errors) {
    // ...
}
```

### UserService

Контроллер **registerUserAccount** метод вызывает **UserService** объект:

```java
@PostMapping("/user/registration")
public ModelAndView registerUserAccount(
    @ModelAttribute("user") @Valid UserDto userDto,
    HttpServletRequest request, Errors errors) {
    try {
        User registered = userService.registerNewUserAccount(userDto);
        // ...
    } catch (UserAlreadyExistException uaeEx) {
        mav.addObject("message", "An account for that username/email already exists.");
        return mav;
    }
    return new ModelAndView("successRegister", "user", userDto);
}
```

Пользовательская служба проверяет наличие повторяющихся писем:

```java
@Service
@Transactional
public class UserService implements IUserService {
    @Autowired
    private UserRepository repository;
    
    @Override
    public User registerNewUserAccount(UserDto userDto) throws UserAlreadyExistException {
        if (emailExist(userDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: "
                + userDto.getEmail());
        }
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setRoles(Arrays.asList("ROLE_USER"));
        return repository.save(user);
    }
    
    private boolean emailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }
}
```

### UserDetailsService

Мы реализуем настраиваемую службу **UserDetailsService** для проверки учетных данных для входа в систему из уровня сохраняемости**.

Начнем с реализации службы пользовательских данных о пользователях:

```java
@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword().toLowerCase(), enabled, accountNonExpired,
            credentialsNonExpired, accountNonLocked, getAuthorities(user.getRoles()));
    }
    
    private static List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
```

Чтобы включить новую пользовательскую службу в конфигурации **Spring** **Security,** нам просто нужно добавить ссылку на **UserDetailsService** внутри элемента **authentication-manager** и добавить **bean-**компонент **UserDetailsService:**

```java
@Autowired
private MyUserDetailsService userDetailsService;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
}
```

## Активировать новую учетную запись по электронной почте

Механизм подтверждения регистрации вынуждает пользователя ответить на электронное письмо **«**Подтвердить регистрацию**»,** отправленное после успешной регистрации, чтобы подтвердить свой адрес электронной почты и активировать свою учетную запись**. Пользователь делает это, щелкая уникальную ссылку активации, отправленную ему по электронной почте**.

Следуя этой логике, новый зарегистрированный пользователь не сможет войти в систему, пока этот процесс не будет завершен**.

Мы будем использовать простой токен проверки в качестве ключевого артефакта, с помощью которого проверяется пользователь**.

**VerificationToken** организация должна соответствовать следующим критериям:

1. Он должен ссылаться на пользователя **(**через однонаправленную связь**)**
2. Он будет создан сразу после регистрации
3. Срок его действия истечет в течение **24** часов после его создания**.
4. Имеет уникальное, случайно сгенерированное значение

Требования **2** и **3** являются частью логики регистрации**. Два других реализованы в простой сущности **VerificationToken:**

```java
@Entity
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String token;
    
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    
    private Date expiryDate;
    
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
```

Обратите внимание на значение **nullable = false** для пользователя, чтобы обеспечить целостность и согласованность данных в ассоциации **VerificationToken <-> User.**

Первоначально, когда Пользователь зарегистрирован, в этом активном поле будет установлено значение **false.** В процессе проверки аккаунта - в случае успеха - это станет правдой**.

Давайте начнем с добавления поля в нашу сущность **User:**

```java
public class User {
    @Column(name = "enabled")
    private boolean enabled;
    
    public User() {
        super();
        this.enabled = false;
    }
}
```

Обратите внимание, как мы также установили для этого поля значение по умолчанию **false.**

### Событие регистрации

Давайте добавим два дополнительных элемента бизнес-логики к варианту использования регистрации пользователя:

1. Создайте **VerificationToken** для пользователя и сохраните его
2. Отправка сообщения электронной почты для подтверждения счетов - который включает в себя ссылку для подтверждения с **VerificationToken** по стоимости

Эти два дополнительных элемента логики не должны выполняться контроллером напрямую, поскольку они являются **«**дополнительными**»** внутренними задачами**.

Контроллер опубликует **SpringApplicationEvent,** чтобы запустить выполнение этих задач**. Это так же просто, как внедрить **ApplicationEventPublisher** и затем использовать его для публикации завершения регистрации:

```java
@Autowired
ApplicationEventPublisher eventPublisher

@PostMapping("/user/registration")
public ModelAndView registerUserAccount(
    @ModelAttribute("user") @Valid UserDto userDto,
    HttpServletRequest request, Errors errors) {
    try {
        User registered = userService.registerNewUserAccount(userDto);
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(
            new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
    } catch (UserAlreadyExistException uaeEx) {
        ModelAndView mav = new ModelAndView("registration", "user", userDto);
        mav.addObject("message", "An account for that username/email already exists.");
        return mav;
    } catch (RuntimeException ex) {
        return new ModelAndView("emailError", "user", userDto);
    }
    return new ModelAndView("successRegister", "user", userDto);
}
```

Еще одна вещь, на которую следует обратить внимание**, -** это блок **try-catch,** окружающий публикацию события**. Этот фрагмент кода будет отображать страницу ошибки всякий раз, когда возникает исключение в логике, выполняемой после публикации события, которым в данном случае является отправка электронного письма**.

Давайте теперь посмотрим на фактическую реализацию этого нового события **OnRegistrationCompleteEvent,** которое отправляет наш контроллер, а также на слушателя, который будет его обрабатывать:

```java
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;
    
    public OnRegistrationCompleteEvent(User user, Locale locale, String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
```

**RegistrationListener** обрабатывает событие **OnRegistrationCompleteEvent.**

```java
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private IUserService service;
    
    @Autowired
    private MessageSource messages;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }
    
    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "/regitrationConfirm.html?token=" + token;
        String message = messages.getMessage("message.regSucc", null, event.getLocale());
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        mailSender.send(email);
    }
}
```

Здесь метод **confirmRegistration** получит событие **OnRegistrationCompleteEvent,** извлечет из него всю необходимую информацию о пользователе, создаст токен подтверждения, сохранит его, а затем отправит его в качестве параметра в ссылке **«**Подтвердить регистрацию**».**

Как упоминалось выше, любое исключение **javax.mail.AuthenticationFailedException,** созданное **JavaMailSender,** будет обрабатываться контроллером**.

### Подтверждение регистрации

Когда пользователь получает ссылку **«**Подтвердить регистрацию**»,** он должен щелкнуть по ней**.

Как только они это сделают, контроллер извлечет значение параметра токена в результирующем запросе **GET** и будет использовать его для включения **User.**

```java
@Autowired
private IUserService service;

@GetMapping("/regitrationConfirm")
public String confirmRegistration(
    WebRequest request, Model model, @RequestParam("token") String token) {
    Locale locale = request.getLocale();
    VerificationToken verificationToken = service.getVerificationToken(token);
    if (verificationToken == null) {
        String message = messages.getMessage("auth.message.invalidToken", null, locale);
        model.addAttribute("message", message);
        return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }
    User user = verificationToken.getUser();
    Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
        String messageValue = messages.getMessage("auth.message.expired", null, locale);
        model.addAttribute("message", messageValue);
        return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }
    user.setEnabled(true);
    service.saveRegisteredUser(user);
    return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
}
```

Пользователь будет перенаправлен на страницу ошибки с соответствующим сообщением, если:

1. **VerificationToken** не существует, по какой-то причине
2. Срок действия **VerificationToken** истек

Если ошибок не обнаружено, пользователь включен**.

### Проверка enabled пользователя

Нам нужно добавить код, который будет проверять, включен ли пользователь:

```java
@Autowired
UserRepository userRepository;

public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email);
    if (user == null) {
        throw new UsernameNotFoundException("No user found with username: " + email);
    }
    boolean enabled = user.isEnabled();
    boolean accountNonExpired = true;
    boolean credentialsNonExpired = true;
    boolean accountNonLocked = true;
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword().toLowerCase(), enabled, accountNonExpired,
        credentialsNonExpired, accountNonLocked, getAuthorities(user.getRoles()));
}
```

## Сделать API регистрации RESTful

Начнем с основной операции **Register:**

```java
@PostMapping("/user/registration")
public GenericResponse registerUserAccount(@Valid UserDto accountDto, HttpServletRequest request) {
    logger.debug("Registering user account with information: {}", accountDto);
    User registered = createUserAccount(accountDto);
    if (registered == null) {
        throw new UserAlreadyExistException();
    }
    String appUrl = request.getContextPath();
    eventPublisher.publishEvent(
        new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
    return new GenericResponse("success");
}
```

Итак, чем это отличается от исходной реализации, ориентированной на **MVC?**

Вот оно:

1. запрос теперь правильно сопоставлен с **HTTP** **POST**
2. теперь мы возвращаем правильный **DTO** и маршалируем его прямо в тело **Response**
3. мы больше не занимаемся обработкой ошибок в методе

Мы также удаляем старую функцию **showRegistrationPage(),** поскольку она не требуется просто для отображения страницы регистрации**.

### Обработка исключений

Наряду с **RESTful API** логика обработки исключений, конечно же, станет более зрелой**.

Мы используем тот же механизм **@ControllerAdvice** для чистой обработки исключений, создаваемых приложением - и теперь нам нужен новый тип исключения**.

Это исключение **BindException,** которое возникает при проверке **UserDto(если он недействителен).** Мы переопределим метод **handleBindException()** **ResponseEntityExceptionHandler** по умолчанию, чтобы добавить ошибки в тело ответа:

```java
@Override
protected ResponseEntity<Object> handleBindException(
    BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    logger.error("400 Status Code", ex);
    BindingResult result = ex.getBindingResult();
    GenericResponse bodyOfResponse = new GenericResponse(result.getFieldErrors(), result.getGlobalErrors());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
}
```

Нам также нужно будет обработать наше настраиваемое исключение **Exception** **UserAlreadyExistException,** которое выдается, когда пользователь регистрируется с уже существующим адресом электронной почты:

```java
@ExceptionHandler({UserAlreadyExistException.class})
public ResponseEntity<Object> handleUserAlreadyExist(RuntimeException ex, WebRequest request) {
    logger.error("409 Status Code", ex);
    GenericResponse bodyOfResponse = new GenericResponse(
        messages.getMessage("message.regError", null, request.getLocale()), "UserAlreadyExist");
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
}
```

Нам также необходимо улучшить реализацию **GenericResponse,** чтобы удерживать эти ошибки проверки:

```java
public class GenericResponse {
    public GenericResponse(List<FieldError> fieldErrors, List<ObjectError> globalErrors) {
        super();
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.message = mapper.writeValueAsString(fieldErrors);
            this.error = mapper.writeValueAsString(globalErrors);
        } catch (JsonProcessingException e) {
            this.message = "";
            this.error = "";
        }
    }
}
```

