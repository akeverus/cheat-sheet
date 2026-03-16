---
title: "Вопросы на собеседовании: Application Security"
description: "Комплексное руководство по вопросам собеседования на тему Application Security для Senior Java Developer. Включает"
tags: ["interview", "security", "application-security-interview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вопросы на собеседовании: `Application Security`

Комплексное руководство по вопросам собеседования на тему `Application Security` для `Senior Java Developer`. Включает
детальные объяснения концепций, практические примеры на `Java` + `Spring`, best practices и troubleshooting.

Дата последнего обновления: 2026-02-04

Краткое введение: комплексное руководство по вопросам собеседования на тему Application Security для Senior Java Developer.

## Полезные ссылки

### Официальная документация

- [OWASP Top 10](https://owasp.org/Top10/)
- [Java Security Documentation](https://docs.oracle.com/en/java/javase/17/security/)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [OWASP Cheat Sheet](https://cheatsheetseries.owasp.org/)

### См. также

- [`owasp-top10-interview.md`](owasp-top10-interview.md) — OWASP Top 10
- [`authentication-authorization-patterns-interview.md`](authentication-authorization-patterns-interview.md) — паттерны аутентификации
- [`../frameworks/spring/spring-security-interview.md`](../frameworks/spring/spring-security-interview.md) — Spring Security

## Содержание

- [Полезные ссылки](#полезные-ссылки)

**Основы безопасности приложений**
- [Q1. Что такое безопасность приложений и почему она важна?](#q1-что-такое-безопасность-приложений-и-почему-она-важна)
- [Q2. Какие основные принципы безопасности приложений?](#q2-какие-основные-принципы-безопасности-приложений)

**Аутентификация и авторизация**
- [Q3. Что такое аутентификация и авторизация?](#q3-что-такое-аутентификация-и-авторизация)
- [Q4. Какие методы аутентификации существуют?](#q4-какие-методы-аутентификации-существуют)
- [Q5. Как реализовать безопасное хранение паролей?](#q5-как-реализовать-безопасное-хранение-паролей)
- [Q6. Что такое JWT и как он работает?](#q6-что-такое-jwt-и-как-он-работает)

**Защита от атак (SQL, XSS, CSRF)**
- [Q7. Как защититься от SQL-инъекций?](#q7-как-защититься-от-sql-инъекций)
- [Q8. Как защититься от XSS-атак?](#q8-как-защититься-от-xss-атак)
- [Q9. Что такое CSRF и как от него защититься?](#q9-что-такое-csrf-и-как-от-него-защититься)
- [Q10. Как реализовать шифрование данных?](#q10-как-реализовать-шифрование-данных)
- [Q11. Что такое OWASP Top 10 и как защититься?](#q11-что-такое-owasp-top-10-и-как-защититься)

**Безопасность API и веб-интерфейсов**
- [Q12. Как обеспечить безопасность API (REST, GraphQL)?](#q12-как-обеспечить-безопасность-api-rest-graphql)
- [Q13. Что такое Security Headers и какие использовать?](#q13-что-такое-security-headers-и-какие-использовать)
- [Q14. Как защититься от Injection-атак?](#q14-как-защититься-от-injection-атак)
- [Q15. Что такое Content Security Policy (CSP)?](#q15-что-такое-content-security-policy-csp)
- [Q16. Как обеспечить безопасность зависимостей?](#q16-как-обеспечить-безопасность-зависимостей)
- [Q17. Что такое Secrets Management?](#q17-что-такое-secrets-management)
- [Q18. Как проводить Security Code Review?](#q18-как-проводить-security-code-review)
- [Q19. Что такое SAST и DAST?](#q19-что-такое-sast-и-dast)
- [Q20. Как обеспечить безопасность в контейнерах?](#q20-как-обеспечить-безопасность-в-контейнерах)

**Security by Design и реагирование на инциденты**
- [Q21. Что такое Security by Design?](#q21-что-такое-security-by-design)
- [Q22. Как защититься от Deserialization-атак?](#q22-как-защититься-от-deserialization-атак)
- [Q23. Что такое Threat Modeling?](#q23-что-такое-threat-modeling)
- [Q24. Как обеспечить безопасность логирования?](#q24-как-обеспечить-безопасность-логирования)
- [Q25. Что такое Security Misconfiguration?](#q25-что-такое-security-misconfiguration)
- [Q26. Как защититься от SSRF (Server-Side Request Forgery)?](#q26-как-защититься-от-ssrf-server-side-request-forgery)
- [Q27. Что такое Secure SDLC?](#q27-что-такое-secure-sdlc)
- [Q28. Как обеспечить безопасность файловых загрузок?](#q28-как-обеспечить-безопасность-файловых-загрузок)
- [Q29. Что такое Security Testing в CI/CD?](#q29-что-такое-security-testing-в-cicd)
- [Q30. Как реагировать на инциденты безопасности?](#q30-как-реагировать-на-инциденты-безопасности)

## Q1. Что такое безопасность приложений и почему она важна?

Безопасность приложений — это комплекс мер и практик, направленных на защиту программного обеспечения от различных
угроз, уязвимостей и атак, которые могут привести к несанкционированному доступу, утечке данных или нарушению
работоспособности системы.

### Почему безопасность важна?

1. Защита конфиденциальных данных: Предотвращение утечек персональных данных, финансовой информации, интеллектуальной
   собственности
2. Соблюдение регуляторных требований: `GDPR`, `HIPAA`, `PCI DSS` и другие стандарты
3. Поддержание доверия пользователей: Защита репутации компании и доверие клиентов
4. Финансовые потери: Предотвращение убытков от атак и восстановления систем
5. Юридические последствия: Избежание штрафов и судебных исков

### Уровни безопасности

#### 1. `Application Level Security`

- Безопасность кода приложения
- Валидация ввода
- Аутентификация и авторизация
- Шифрование данных

#### 2. `Infrastructure Level Security`

- `Firewall` и network security
- `Secure` configurations
- `Monitoring` и logging

#### 3. `Data Level Security`

- `Encryption` at rest
- `Encryption` in transit
- `Secure` storage

## Q2. Какие основные принципы безопасности приложений?

### `CIA Triad` (`Confidentiality`, `Integrity`, `Availability`)

#### 1. `Confidentiality` (Конфиденциальность)

Защита информации от несанкционированного доступа. Примеры:

- Шифрование данных
- Контроль доступа
- `Secure` communication protocols

#### 2. `Integrity` (Целостность)

Обеспечение корректности и полноты данных. Примеры:

- `Hash` функции для проверки целостности
- `Digital` signatures
- `Input validation`

#### 3. `Availability` (Доступность)

Обеспечение доступности системы для авторизованных пользователей. Примеры:

- `Redundancy` и failover
- DDoS protection
- `Resource` management

### `Defense in Depth` (Многоуровневая защита)

Принцип многослойной защиты, где каждый уровень предоставляет дополнительную защиту. Уровни защиты:

1. `Network Level`: `Firewall`, `IDS / IPS`
2. `Application Level`: `Input validation`, authentication
3. `Data Level`: `Encryption`, access control
4. `Host Level`: `OS` hardening, antivirus

### `Principle` of `Least Privilege` (Принцип наименьших привилегий)

Пользователи и процессы должны иметь минимально необходимые права для выполнения своих задач. Применение:

- `Role-based` access control (`RBAC`)
- `Just-in-time` access
- `Regular` review of permissions

## Q3. Что такое аутентификация и авторизация?

### Аутентификация (`Authentication`)

Аутентификация — это процесс проверки подлинности пользователя или системы. Цель: Убедиться, что пользователь является
тем, кем себя называет. Методы:

- `Something` you know: Пароль, `PIN`
- `Something` you have: Токен, смарт-карта, телефон
- `Something` you are: Биометрия (отпечаток пальца, лицо)

### Авторизация (`Authorization`)

Авторизация — это процесс определения прав и разрешений пользователя после успешной аутентификации. Цель: Определить,
какие действия пользователь может выполнять. Модели:

- `Role-Based Access Control` (`RBAC`)
- `Attribute-Based Access Control` (`ABAC`)
- `Access Control Lists` (`ACL`)

### Разница между `Authentication` и `Authorization`

```java
// Пример: Банковское приложение
public class BankingService {

    // Аутентификация: Проверка личности пользователя
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        throw new AuthenticationException("Invalid credentials");
    }

    // Авторизация: Проверка прав на операцию
    public void transferMoney(User user, Account from, Account to, BigDecimal amount) {
        if (!hasPermission(user, "TRANSFER_MONEY")) {
            throw new AuthorizationException("Insufficient permissions");
        }
        if (!ownsAccount(user, from)) {
            throw new AuthorizationException("Not account owner");
        }
        // Выполнение перевода
    }

    private boolean hasPermission(User user, String permission) {
        return user.getRoles().stream().anyMatch(role -> role.getPermissions().contains(permission));
    }

    private boolean ownsAccount(User user, Account account) {
        return account.getOwnerId().equals(user.getId());
    }
}
```

## Q4. Какие методы аутентификации существуют?

### 1. `Basic Authentication`

Простейший метод, использующий username:password в base64 кодировке. Пример `HTTP` запроса:

```http
Authorization: Basic dXNlcjpwYXNzd29yZA==
```

Проблемы:

- Небезопасно без `HTTPS`
- Нет механизма logout
- Пароль передается при каждом запросе

### 2. `Session-Based Authentication`

Использование серверных сессий для отслеживания аутентифицированных пользователей. Процесс:

1. Пользователь логинится
2. Сервер создает сессию и отправляет session `ID` в cookie
3. Клиент отправляет session `ID` с каждым запросом
4. Сервер проверяет сессию

Пример с `Spring Security`:

```java

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/login").permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login").and().logout().logoutSuccessUrl("/login?logout");
    }
}
```

### 3. `Token-Based Authentication`

Использование токенов (`JWT`, OAuth tokens) для аутентификации. Преимущества:

- `Stateless` (сервер не хранит состояние)
- Масштабируемость
- Подходит для микросервисов

### 4. `OAuth 2`.0

Протокол авторизации для делегированного доступа. Роли:

- `Resource Owner`: Владелец ресурса
- `Client`: Приложение, запрашивающее доступ
- `Authorization Server`: Сервер авторизации
- `Resource Server`: Сервер ресурсов

### 5. `Multi-Factor Authentication` (`MFA`)

Использование нескольких факторов аутентификации. Факторы:

- `Something` you know (password)
- `Something` you have (`SMS`, app)
- `Something` you are (biometrics)

## Q5. Как реализовать безопасное хранение паролей?

### Никогда не храните пароли в открытом виде!Плохо:

```java
// НИКОГДА НЕ ДЕЛАЙТЕ ТАК!
user.setPassword(plainPassword);
userRepository.

save(user);
```

### Правильный подход: Хэширование с солью

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void createUser(String username, String plainPassword) {
        // Генерация соли и хэширование
        String hashedPassword = passwordEncoder.encode(plainPassword);

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);

        userRepository.save(user);
    }

    public boolean authenticate(String username, String plainPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }

        // Проверка хэша
        return passwordEncoder.matches(plainPassword, user.getPassword());
    }
}
```

### Алгоритмы хэширования паролей

#### 1. bcrypt

- Адаптивный алгоритм (можно увеличивать сложность)
- Встроенная соль
- Широко используется

#### 2. scrypt

- Память-зависимый алгоритм
- Более безопасный, чем bcrypt
- Медленнее

#### 3. `Argon2`

- Победитель `Password Hashing Competition`
- Настраиваемая сложность
- Рекомендуется для новых проектов

### Лучшие практики

1. Используйте сильные алгоритмы: bcrypt, scrypt, `Argon2`
2. Добавляйте соль: Уникальная соль для каждого пароля
3. Регулярно обновляйте хэши: При изменении алгоритма
4. Ограничивайте попытки: Защита от `brute-force` атак
5. Используйте `HTTPS`: Всегда шифруйте трафик

## Q6. Что такое `JWT` и как он работает?

`JWT` (`JSON` Web `Token`) — это стандарт для создания токенов доступа, который определяет компактный и самодостаточный способ
передачи информации между сторонами в виде `JSON` объекта. ### Структура `JWT`

`JWT` состоит из трех частей, разделенных точками: `header.payload.signature`

#### 1. `Header` (Заголовок)

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### 2. `Payload` (Полезная нагрузка)

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022,
  "exp": 1516242622,
  "roles": [
    "USER",
    "ADMIN"
  ]
}
```

#### 3. `Signature` (Подпись)

```text
HMACSHA256(
 base64UrlEncode(header) + "." +
 base64UrlEncode(payload),
 secret
)
```

### Как работает `JWT`?

```java
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

    private final String SECRET_KEY = "mySecretKey";

    public String generateToken(User user) {
        return Jwts.builder().setSubject(user.getUsername()).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 часа.claim("roles", user.getRoles()).signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}
```

### Преимущества `JWT`

1. `Stateless`: Сервер не хранит состояние сессий
2. `Self-contained`: Токен содержит всю необходимую информацию
3. `Cross-domain`: Работает между разными доменами
4. `Scalable`: Легко масштабируется

### Недостатки `JWT`

1. Неотзываемость: Токен нельзя отозвать до истечения срока
2. Размер: Большие токены увеличивают размер запросов
3. Безопасность секрета: Компрометация секрета опасна

## Q7. Как защититься от `SQL`-инъекций?

`SQL`-инъекция — это атака, при которой злоумышленник может выполнить произвольный `SQL` код через уязвимое приложение. ###
Уязвимый код (Плохо)

```java
// НИКОГДА НЕ ДЕЛАЙТЕ ТАК!
public User findUser(String username) {
    String query = "SELECT * FROM users WHERE username = '" + username + "'";
    return jdbcTemplate.queryForObject(query, User.class);
}

// Атака: username = "' OR '1'='1"
// Результат: SELECT * FROM users WHERE username = '' OR '1'='1'
```

### Защита: `Prepared Statements`

```java

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username =?";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, new Object[]{username},
                            (rs, rowNum) -> {
                                User user = new User();
                                user.setId(rs.getLong("id"));
                                user.setUsername(rs.getString("username"));
                                user.setPassword(rs.getString("password"));
                                return user;
                            }));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
```

### Защита: `JPA Criteria API`

```java

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findByUsername(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.select(root).where(cb.equal(root.get("username"), username));

        try {
            return Optional.of(
                    entityManager.createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
```

### Дополнительные меры защиты

1. Валидация ввода: Проверяйте и санитизируйте все входные данные
2. `Least Privilege`: Используйте учетные записи БД с минимальными правами
3. `Stored Procedures`: Используйте хранимые процедуры вместо динамического `SQL`
4. `ORM`: Используйте `ORM` фреймворки (`JPA`, `Hibernate`) с параметризованными запросами

## Q8. Как защититься от `XSS`-атак?

`XSS` (`Cross-Site Scripting`) — это атака, при которой злоумышленник может внедрить вредоносный скрипт в веб-страницу,
который будет выполнен в браузере жертвы. ### Типы `XSS`

#### 1. `Stored XSS` (Хранимый)

Скрипт сохраняется на сервере и выполняется при каждом просмотре страницы. #### 2. `Reflected XSS` (Отраженный)
Скрипт отражается в ответе сервера и выполняется сразу. #### 3. `DOM-based XSS`
Скрипт модифицирует `DOM` в браузере. ### Защита от `XSS`

#### 1. Экранирование вывода

```java

@Controller
public class BlogController {

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        return "post";
    }
}

// В шаблоне Thymeleaf (автоматическое экранирование)
<div th:text="${post.content}"></div>

// В шаблоне JSP (явное экранирование)
<c:
out value = "${post.content}"
escapeXml="true"/>
```

#### 2. `Content Security Policy` (`CSP`)

```java

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers ->
                headers.contentSecurityPolicy(
                        "default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline'; " +
                                "style-src 'self' 'unsafe-inline'"
                )
        );
        return http.build();
    }
}
```

#### 3. Валидация ввода

```java
public class CommentValidator {

    private static final Pattern SCRIPT_PATTERN =
            Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE);

    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // Удаляем script теги
        input = SCRIPT_PATTERN.matcher(input).replaceAll("");

        // Экранируем специальные символы
        input = input.replaceAll("<", "<").replaceAll(">", ">").replaceAll("\"", "&quot;").replaceAll("'", "&#x27;").replaceAll("&", "&");

        return input;
    }
}
```

#### 4. Использование безопасных фреймворков

- `Spring Security`: Автоматическая защита от `XSS`
- `OWASP Java Encoder`: Библиотека для безопасного кодирования
- JSoup: Для очистки `HTML` контента

## Q9. Что такое `CSRF` и как от него защититься?

`CSRF` (`Cross-Site Request Forgery`) — это атака, при которой злоумышленник заставляет пользователя выполнить нежелательное
действие в веб-приложении, в котором пользователь аутентифицирован. ### Как работает `CSRF`?

1. Пользователь логинится в уязвимое приложение
2. Злоумышленник создает вредоносную страницу с формой
3. При посещении этой страницы браузер автоматически отправляет запрос с куками пользователя
4. Приложение выполняет действие, думая что это легитимный запрос

### Защита от `CSRF`

#### 1. `CSRF Token`

```java

@Controller
public class TransferController {

    @PostMapping("/transfer")
    public String transfer(@RequestParam String toAccount,
                           @RequestParam BigDecimal amount,
                           @RequestParam("_csrf") String csrfToken,
                           HttpServletRequest request) {

        // Spring Security автоматически проверяет CSRF token
        // Если токен не совпадает, выбрасывается исключение

        transferService.transfer(toAccount, amount);
        return "redirect:/success";
    }
}
```

#### 2. Настройка `Spring Security`

```java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        return http.build();
    }
}
```

#### 3. `SameSite Cookies`

```java

@Configuration
public class CookieConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setSameSite("strict"); // Или "lax"
        return serializer;
    }
}
```

#### 4. Проверка `Origin / Referer Headers`

```java
public class CsrfProtectionFilter extends OncePerRequestFilter {

    private static final String[] ALLOWED_ORIGINS = {"https://myapp.com"};

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        if (!isAllowedOrigin(origin) && !isAllowedReferer(referer)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF detected");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowedOrigin(String origin) {
        return origin != null && Arrays.asList(ALLOWED_ORIGINS).contains(origin);
    }

    private boolean isAllowedReferer(String referer) {
        // Проверка referer header
        return referer != null && referer.startsWith("https://myapp.com");
    }
}
```

### Дополнительные меры

1. Используйте `POST` для изменяющих операций
2. Не храните чувствительные данные в куках
3. Регулярно обновляйте сессии
4. Используйте `HTTPS`

## Q10. Как реализовать шифрование данных?

### Симметричное шифрование

Использует один и тот же ключ для шифрования и дешифрования. ```java
import `javax.crypto`.`Cipher`;
import `javax.crypto`.`KeyGenerator`;
import `javax.crypto`.`SecretKey`;
import `javax.crypto.spec`.`SecretKeySpec`;
import `java.util`.`Base64`;

public class `SymmetricEncryption` {

private static final `String ALGORITHM` = "`AES`";
private static final int KEY_SIZE = `256`;

public static `SecretKey generateKey`() throws `Exception` {
`KeyGenerator keyGen` = `KeyGenerator.getInstance(ALGORITHM)`;
keyGen.init(KEY_SIZE);
return keyGen.generateKey();
}

public static `String` encrypt(`String` data, `SecretKey` key) throws `Exception` {
`Cipher` cipher = `Cipher.getInstance(ALGORITHM)`;
cipher.init(Cipher.ENCRYPT_MODE, key);
byte[] `encryptedBytes` = cipher.`doFinal`(data.`getBytes()`);
return `Base64`. `getEncoder()`.`encodeToString`(`encryptedBytes`);
}

public static `String` decrypt(`String encryptedData`, `SecretKey` key) throws `Exception` {
`Cipher` cipher = `Cipher.getInstance(ALGORITHM)`;
cipher.init(Cipher.DECRYPT_MODE, key);
byte[] `decryptedBytes` = cipher.`doFinal`(`Base64`. `getDecoder()`.decode(`encryptedData`));
return new `String`(`decryptedBytes`);
}
}

```java

### Асимметричное шифрование

Использует пару ключей: публичный для шифрования, приватный для дешифрования. ```java
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;

public class AsymmetricEncryption {

 private static final String ALGORITHM = "RSA";
 private static final int KEY_SIZE = 2048;

 public static KeyPair generateKeyPair() throws Exception {
 KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
 keyGen.initialize(KEY_SIZE);
 return keyGen.generateKeyPair();
 }

 public static String encrypt(String data, PublicKey publicKey) throws Exception {
 Cipher cipher = Cipher.getInstance(ALGORITHM);
 cipher.init(Cipher.ENCRYPT_MODE, publicKey);
 byte[] encryptedBytes = cipher.doFinal(data.getBytes());
 return Base64. getEncoder().encodeToString(encryptedBytes);
 }

 public static String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
 Cipher cipher = Cipher.getInstance(ALGORITHM);
 cipher.init(Cipher.DECRYPT_MODE, privateKey);
 byte[] decryptedBytes = cipher.doFinal(Base64. getDecoder().decode(encryptedData));
 return new String(decryptedBytes);
 }
}
```

### Хэширование (одностороннее шифрование)

```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtils {

    public static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // С солью для защиты от rainbow tables
    public static String hashWithSalt(String input, String salt) throws NoSuchAlgorithmException {
        return sha256(input + salt);
    }

    // Генерация соли
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
```

### Рекомендации по шифрованию

1. Используйте проверенные алгоритмы: `AES` для симметричного, `RSA` для асимметричного
2. Правильное управление ключами: Храните ключи securely, используйте key rotation
3. Используйте `TLS / HTTPS`: Шифруйте данные в транзите
4. Регулярно обновляйте алгоритмы: Следите за устаревшими алгоритмами
5. Используйте готовые библиотеки: Не реализуйте шифрование самостоятельно

## Q11. Что такое `OWASP Top 10` и как защититься?

`OWASP Top 10` — список 10 наиболее критичных рисков безопасности веб-приложений: (1) `Broken Access Control`, (2) `Cryptographic Failures`, (3) `Injection`, (4) `Insecure Design`, (5) `Security Misconfiguration`, (6) `Vulnerable Components`, (7) `Authentication Failures`, (8) `Data Integrity Failures`, (9) `Logging Failures`, (10) `SSRF`. Защита: обучение команды, код-ревью, `SAST / DAST`, регулярные обновления, принцип наименьших привилегий.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q12. Как обеспечить безопасность `API` (`REST`, `GraphQL`)?

Аутентификация: `JWT`, `OAuth 2`.0, `API` keys. Авторизация: проверка прав на каждый эндпоинт; `RBAC / ABAC`. Валидация: входных данных (`Bean Validation`, схемы). `Rate / IP`. `HTTPS`: обязательно. `CORS`: ограничить origin. Логирование: запросов и ошибок. Для `GraphQL`: ограничение глубины и сложности запросов; авторизация на уровне полей.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q13. Что такое `Security Headers` и какие использовать?

`HTTP`-заголовки для защиты: **Strict-Transport-Security** (`HSTS`, принудительный `HTTPS`), **X-Content-Type-Options**: nosniff (защита от `MIME` sniffing), **X-Frame-Options**: `DENY` (защита от clickjacking), **Content-Security-Policy** (`CSP`, защита от `XSS`), **X-XSS-Protection** (устарел, `CSP` предпочтительнее). В `Spring Security`: `headers().defaultsDisabled()` и явная настройка нужных заголовков.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q14. Как защититься от `Injection`-атак?

`SQL Injection`: параметризованные запросы (`PreparedStatement`, `JPA Criteria`); не конкатенировать `SQL`. `LDAP`, `OS Command Injection`: валидация входных данных, whitelist допустимых символов; не передавать пользовательский ввод в команды. `NoSQL Injection`: параметризованные запросы (`MongoDB`, `Elasticsearch`). Общее: валидация, экранирование, минимальные привилегии.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q15. Что такое `Content Security Policy` (`CSP`)?

`CSP` — `HTTP`-заголовок, ограничивающий источники контента (скрипты, стили, изображения): **Content-Security-Policy**: `default-src` 'self'; `script-src` 'self' `cdn.example.com`. Защита от `XSS`: браузер блокирует inline-скрипты и скрипты с недоверенных источников. `report-uri` для отчётов о нарушениях. Включать постепенно (сначала **Content-Security-Policy-Report-Only**).

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q16. Как обеспечить безопасность зависимостей?

Регулярный аудит: `OWASP Dependency Check`, `Snyk`, `Dependabot`, `Renovate`. Обновление уязвимых зависимостей; приоритет по severity (`CVSS`). Минимизация зависимостей; не использовать неподдерживаемые библиотеки. Сканирование в `CI`; блокировка merge при критичных уязвимостях. `Software Bill` of `Materials` (`SBOM`) для трассируемости.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q17. Что такое `Secrets Management`?

Секреты (пароли, ключи, токены) не в коде и не в репозитории. Хранение: `Vault`, `AWS Secrets Manager`, `Azure` Key `Vault`, переменные окружения (для простых случаев). Ротация секретов; минимальный срок жизни. Доступ по ролям; аудит использования. В приложении: инъекция секретов при старте; не логировать секреты.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q18. Как проводить `Security Code Review`?

Чек-лист: (1) Валидация входных данных; (2) Параметризованные запросы; (3) Отсутствие секретов в коде; (4) Правильное хеширование паролей; (5) Авторизация на защищённых эндпоинтах; (6) Безопасная десериализация; (7) Логирование без чувствительных данных. Инструменты: `SpotBugs`, `FindSecBugs`, `SonarQube` с security rules. Обучение команды `OWASP Top 10`.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q19. Что такое `SAST` и `DAST`?

`SAST` (`Static Application Security Testing`) — анализ исходного кода без выполнения (линтеры, `SonarQube`, `Checkmarx`). Находит уязвимости на ранних этапах; ложные срабатывания. `DAST` (`Dynamic Application Security Testing`) — тестирование работающего приложения (сканеры, `OWASP ZAP`, `Burp Suite`). Находит runtime-уязвимости; требует работающее окружение. Комбинировать оба подхода.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q20. Как обеспечить безопасность в контейнерах?

Минимальный базовый образ (distroless, `Alpine`); не запускать от root (`USER` в `Dockerfile`); сканирование образов (`Trivy`, `Clair`); подписанные образы. Секреты не в образе; инъекция через переменные окружения или `Vault`. Ограничение ресурсов (`CPU`, память); сетевая изоляция (`NetworkPolicy`). Регулярное обновление базовых образов.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q21. Что такое Security by Design?

Безопасность учитывается на всех этапах разработки (требования, дизайн, код, тесты, деплой). `Threat` modeling при проектировании; security requirements в бэклоге; secure coding standards; автоматизация проверок (`SAST`, `DAST`, dependency scan) в `CI`. Обучение команды; код-ревью с фокусом на безопасность. Безопасность — не «потом», а с начала.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q22. Как защититься от `Deserialization`-атак?

Не десериализовать недоверенные данные (`Java Serialization` уязвима). Альтернативы: `JSON` (`Jackson`, `Gson`), `Protobuf`; не использовать `ObjectInputStream` с внешними данными. `Whitelist` классов при десериализации; валидация перед десериализацией. `look-ahead-deserialization` (проверка класса до создания объекта). В `Spring` — `@JsonTypeInfo` с осторожностью.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q23. Что такое `Threat Modeling`?

Систематический анализ угроз при проектировании: идентификация активов (данные, сервисы), угроз (кто, что, как), уязвимостей и мер защиты. Методологии: `STRIDE` (`Spoofing`, `Tampering`, `Repudiation`, `Information Disclosure`, `Denial of Service`, `Elevation of Privilege`), `PASTA`, `DREAD`. Результат: приоритизированный список рисков и митигаций. Проводить на ранних этапах и при крупных изменениях.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q24. Как обеспечить безопасность логирования?

Не логировать пароли, токены, `PII`; маскировать чувствительные поля. Защита логов: доступ по ролям; шифрование при хранении (если требуется). Логировать события безопасности (аутентификация, доступ, изменения прав) для аудита и расследования. Централизованное хранение (`SIEM`); алерты по аномалиям. Ротация и удаление старых логов по политике.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q25. Что такое `Security Misconfiguration`?

Неправильная конфигурация (default пароли, открытые порты, verbose ошибки, отключённые обновления) — частая уязвимость. Защита: hardening checklist; автоматизация конфигурации (`IaC`); сканирование конфигурации (`Kube-bench`, `AWS Config`). Не использовать default credentials; отключать ненужные сервисы; не выводить stack trace в production.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q26. Как защититься от `SSRF` (`Server-Side Request Forgery`)?

`SSRF` — атака, при которой приложение делает запрос к внутреннему ресурсу по `URL`, контролируемому атакующим. Защита: whitelist допустимых доменов; не передавать пользовательский `URL` напрямую в `HTTP`-клиент; валидация и парсинг `URL`; блокировка внутренних `IP` (`127.0`.0.1, `169.254`.*, private ranges). Использовать `DNS` rebinding protection.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q27. Что такое `Secure SDLC`?

`Secure Software Development Lifecycle` — интеграция безопасности во все фазы разработки: требования (security requirements), дизайн (threat modeling), код (secure coding, `SAST`), тесты (security tests, `DAST`), деплой (hardening, мониторинг). Обучение команды; security champions в командах; регулярные аудиты. Автоматизация проверок в `CI / CD`.

Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом: как решение ведёт себя под нагрузкой, при сбоях и в процессе сопровождения. На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики и проверяемый сценарий.

## Q28. Как обеспечить безопасность файловых загрузок?

Валидация типа файла (не только по расширению; проверять magic bytes); ограничение размера; сканирование антивирусом (`ClamAV`); хранение вне webroot; генерация случайных имён файлов. Не выполнять загруженные файлы; не отдавать файлы напрямую (`Content-Disposition`, `Content-Type`). Для изображений — ре-encode для удаления метаданных.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q29. Что такое `Security Testing` в `CI / CD`?

Автоматизация проверок безопасности в `pipeline`: `SAST` (`SonarQube`, `Checkmarx`), dependency scan (`OWASP Dependency Check`, `Snyk`), сканирование образов (`Trivy`), `DAST` на staging (`OWASP ZAP`). Блокировка merge при критичных находках. Регулярные penetration tests вручную. Метрики: количество уязвимостей, время до исправления.

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.

## Q30. Как реагировать на инциденты безопасности?

План реагирования: (1) Обнаружение (алерты, логи, отчёты); (2) Изоляция (блокировка доступа, отключение скомпрометированных учёток); (3) Расследование (логи, трейсы, форензика); (4) Исправление (патч, обновление); (5) Восстановление (откат данных, коммуникация); (6) Постмортем (причины, действия). Документировать процесс; регулярные учения (tabletop exercises).

Для production-систем дополнительно стоит описать модель угроз, контроль доступа по принципу least privilege и процесс реагирования на инциденты. На собеседовании обычно ожидают, что вы свяжете техническую меру с риском для бизнеса и с проверяемыми контрольными точками в CI/CD.
