# Вопросы на собеседовании: OWASP Top 10

**Комплексное руководство по вопросам собеседования на тему OWASP Top 10 для Senior Java Developer. Включает детальные объяснения концепций, практические примеры на Java + Spring, best practices и troubleshooting.**

**Дата последнего обновления:** 2026-01-25


Понимание OWASP Top 10 критически важно для Senior Java Developer, поскольку это фундаментальные знания о безопасности веб-приложений. OWASP Top 10 регулярно обновляется и отражает текущие тенденции в области безопасности.Дата последнего обновления: 2026-01-24

## Полезные ссылки

### Официальная документация
- [OWASP Top 10 2021](https://owasp.org/www-project-top-ten/)
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/)

### Ресурсы
- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)

### См. также
- `application-security-interview.md` - Общая безопасность приложений
- `../../frameworks/spring/spring-security-interview.md` - Spring Security

## Содержание

- [Введение в OWASP Top 10](#введение-в-owasp-top-10)
- [A01:2021 - Broken Access Control](#a012021---broken-access-control)
- [A02:2021 - Cryptographic Failures](#a022021---cryptographic-failures)
- [A03:2021 - Injection](#a032021---injection)
- [A04:2021 - Insecure Design](#a042021---insecure-design)
- [A05:2021 - Security Misconfiguration](#a052021---security-misconfiguration)
- [A06:2021 - Vulnerable Components](#a062021---vulnerable-components)
- [A07:2021 - Identification and Authentication Failures](#a072021---identification-and-authentication-failures)
- [A08:2021 - Software Integrity Failures](#a082021---software-integrity-failures)
- [A09:2021 - Security Logging and Monitoring Failures](#a092021---security-logging-and-monitoring-failures)
- [A10:2021 - Server-Side Request Forgery](#a102021---server-side-request-forgery)

## Введение в OWASP Top 10

OWASP Top 10 — это стандартный список наиболее критичных рисков безопасности веб-приложений, составляемый Open Web Application Security Project. Список обновляется каждые 3-4 года на основе анализа реальных инцидентов безопасности.

### Почему OWASP Top 10 важен?

1. Фокус на реальных угрозах: Основан на анализе реальных атак
2. Приоритизация: Помогает фокусироваться на наиболее опасных уязвимостях
3. Методология: Предоставляет CWE mappings и detection methods
4. Обновления: Регулярно обновляется для отражения текущих угроз

### OWASP Risk Rating Methodology

Каждая уязвимость оценивается по формуле:Risk = Likelihood × Impact**

Где:
- **Likelihood** = Threat Agent × Vulnerability × Technical Impact
- **Impact** = Technical Impact × Business Impact

## A01:2021 - Broken Access Control

Нарушение контроля доступа — это ситуация, когда пользователь может получить доступ к ресурсам или выполнить действия, на которые у него нет прав.

### Распространенные проблемы

#### 1. URL-based Access Control Bypass

**Уязвимый код:
```java
// Плохо: Нет проверки прав доступа
@GetMapping("/api/users/{id}")
public User getUser(@PathVariable Long id) {
 return userRepository.findById(id).orElseThrow();
}
```

**Исправленный код:
```java
@GetMapping("/api/users/{id}")
public User getUser(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
 // Проверка: пользователь может видеть только свои данные или имеет роль ADMIN
 if (!currentUser.getId().equals(id) &&!currentUser.hasRole("ADMIN")) {
 throw new AccessDeniedException("Access denied");
 }
 return userRepository.findById(id).orElseThrow();
}
```

#### 2. Privilege Escalation

**Пример атаки:
```
GET /api/admin/users HTTP/1.1
Cookie: session=regular_user_session
```
Злоумышленник пытается получить доступ к административным функциям.

#### 3. IDOR (Insecure Direct Object References)

**Уязвимый код:
```java
@PostMapping("/api/accounts/{accountId}/transfer")
public void transfer(@PathVariable Long accountId, @RequestBody TransferRequest request) {
 Account account = accountRepository.findById(accountId).orElseThrow();
 // Нет проверки принадлежности аккаунта пользователю!
 transferService.transfer(account, request.getToAccount(), request.getAmount());
}
```

### Защита от Broken Access Control

#### 1. Role-Based Access Control (RBAC)

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
 
 @Override
 protected void configure(HttpSecurity http) throws Exception {
 http.authorizeRequests().antMatchers("/api/admin/**").hasRole("ADMIN").antMatchers("/api/user/**").hasAnyRole("USER", "ADMIN").anyRequest().authenticated();
 }
}

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
 
 @PreAuthorize("hasRole('ADMIN') or #account.ownerId == authentication.principal.id")
 @GetMapping("/{id}")
 public Account getAccount(@PathVariable Long id) {
 return accountRepository.findById(id).orElseThrow();
 }
}
```

#### 2. Attribute-Based Access Control (ABAC)

```java
@Component
public class AccountPermissionEvaluator implements PermissionEvaluator {
 
 @Override
 public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
 if (targetDomainObject instanceof Account) {
 Account account = (Account) targetDomainObject;
 User user = (User) authentication.getPrincipal();
 
 // Пользователь может видеть аккаунт если он владелец или админ
 return account.getOwnerId().equals(user.getId()) || user.hasRole("ADMIN");
 }
 return false;
 }
}
```

#### 3. Defense in Depth

- **URL Protection: Защищайте URLs на уровне контроллеров
- **Service Layer Security: Проверяйте права на уровне сервисов
- **Data Layer Security: Используйте Row Level Security в БД

## A02:2021 - Cryptographic Failures

Криптографические неудачи включают проблемы с шифрованием, неправильное использование криптографических функций и слабые алгоритмы.

### Распространенные проблемы

#### 1. Хранение паролей в открытом виде

**Уязвимый код:
```java
// НИКОГДА НЕ ДЕЛАЙТЕ ТАК!
user.setPassword(request.getPassword());
userRepository.save(user);
```

#### 2. Использование устаревших алгоритмов

**Уязвимые алгоритмы:
- MD5, SHA-1 для хэширования паролей
- DES, 3DES для шифрования
- RC4 stream cipher

#### 3. Неправильное использование HTTPS

**Проблемы:
- Смешивание HTTP и HTTPS
- Не проверка SSL сертификатов
- Использование self-signed сертификатов в production

### Защита от Cryptographic Failures

#### 1. Безопасное хранение паролей

```java
@Service
public class PasswordService {
 
 private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
 
 public String encodePassword(String rawPassword) {
 return passwordEncoder.encode(rawPassword);
 }
 
 public boolean matches(String rawPassword, String encodedPassword) {
 return passwordEncoder.matches(rawPassword, encodedPassword);
 }
}

// Настройка в Spring Security
@Configuration
public class SecurityConfig {
 
 @Bean
 public PasswordEncoder passwordEncoder() {
 return new BCryptPasswordEncoder(12); // strength = 12
 }
}
```

#### 2. Современные алгоритмы шифрования

```java
public class EncryptionUtils {
 
 private static final String ALGORITHM = "AES/GCM/NoPadding";
 private static final int KEY_SIZE = 256;
 private static final int IV_SIZE = 12; // 96 bits для GCM
 
 public static SecretKey generateKey() throws Exception {
 KeyGenerator keyGen = KeyGenerator.getInstance("AES");
 keyGen.init(KEY_SIZE);
 return keyGen.generateKey();
 }
 
 public static String encrypt(String data, SecretKey key) throws Exception {
 Cipher cipher = Cipher.getInstance(ALGORITHM);
 byte[] iv = new byte[IV_SIZE];
 new SecureRandom().nextBytes(iv);
 
 GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
 cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
 
 byte[] encryptedData = cipher.doFinal(data.getBytes());
 
 // Префикс с IV для дешифрования
 byte[] combined = new byte[iv.length + encryptedData.length];
 System.arraycopy(iv, 0, combined, 0, iv.length);
 System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
 
 return Base64. getEncoder().encodeToString(combined);
 }
 
 public static String decrypt(String encryptedData, SecretKey key) throws Exception {
 Cipher cipher = Cipher.getInstance(ALGORITHM);
 byte[] combined = Base64. getDecoder().decode(encryptedData);
 
 byte[] iv = new byte[IV_SIZE];
 byte[] data = new byte[combined.length - IV_SIZE];
 
 System.arraycopy(combined, 0, iv, 0, iv.length);
 System.arraycopy(combined, iv.length, data, 0, data.length);
 
 GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
 cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
 
 return new String(cipher.doFinal(data));
 }
}
```

#### 3. Правильная настройка HTTPS

```java
@Configuration
public class SSLConfig {
 
 @Bean
 public TomcatServletWebServerFactory servletContainer() {
 TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
 @Override
 protected void postProcessContext(Context context) {
 SecurityConstraint securityConstraint = new SecurityConstraint();
 securityConstraint.setUserConstraint("CONFIDENTIAL");
 
 SecurityCollection collection = new SecurityCollection();
 collection.addPattern("/*");
 securityConstraint.addCollection(collection);
 
 context.addConstraint(securityConstraint);
 }
 };
 
 // Настройка SSL
 tomcat.addAdditionalTomcatConnectors(redirectConnector());
 return tomcat;
 }
 
 private Connector redirectConnector() {
 Connector connector = new Connector("org.apache.coyote.http11. Http11NioProtocol");
 connector.setScheme("http");
 connector.setPort(8080);
 connector.setSecure(false);
 connector.setRedirectPort(8443);
 return connector;
 }
}
```

## A03:2021 - Injection

Injection уязвимости возникают, когда ненадежные данные передаются интерпретатору как часть команды или запроса.

### Типы Injection

#### 1. SQL Injection

**Уязвимый код:
```java
// НИКОГДА НЕ ДЕЛАЙТЕ ТАК!
String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
jdbcTemplate.queryForList(query);
```

**Исправленный код:
```java
@Repository
public class UserRepository {
 
 @Autowired
 private JdbcTemplate jdbcTemplate;
 
 public Optional<User> findByUsernameAndPassword(String username, String password) {
 String sql = "SELECT * FROM users WHERE username =? AND password =?";
 try {
 return Optional.ofNullable(
 jdbcTemplate.queryForObject(sql, new Object[]{username, password}, 
 (rs, rowNum) -> mapToUser(rs)));
 } catch (EmptyResultDataAccessException e) {
 return Optional.empty();
 }
 }
 
 private User mapToUser(ResultSet rs) throws SQLException {
 User user = new User();
 user.setId(rs.getLong("id"));
 user.setUsername(rs.getString("username"));
 user.setPassword(rs.getString("password"));
 return user;
 }
}
```

#### 2. Command Injection

**Уязвимый код:
```java
// НИКОГДА НЕ ДЕЛАЙТЕ ТАК!
Process process = Runtime.getRuntime().exec("ping " + hostname);
```

**Исправленный код:
```java
public class NetworkUtils {
 
 private static final Pattern HOSTNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9.-]+$");
 
 public static boolean pingHost(String hostname) {
 if (!isValidHostname(hostname)) {
 throw new IllegalArgumentException("Invalid hostname");
 }
 
 try {
 Process process = new ProcessBuilder("ping", "-c", "1", hostname).redirectErrorStream(true).start();
 
 return process.waitFor() == 0;
 } catch (Exception e) {
 return false;
 }
 }
 
 private static boolean isValidHostname(String hostname) {
 return hostname!= null && HOSTNAME_PATTERN.matcher(hostname).matches();
 }
}
```

#### 3. LDAP Injection

**Уязвимый код:
```java
// Поиск пользователей в LDAP
String filter = "(uid=" + username + ")";
NamingEnumeration<SearchResult> results = context.search(baseDN, filter, controls);
```

**Исправленный код:
```java
public class LdapUserService {
 
 public List<User> searchUsers(String username) {
 // Экранирование специальных символов LDAP
 String escapedUsername = escapeLdapValue(username);
 String filter = "(uid=" + escapedUsername + ")";
 
 // Использование prepared statements для LDAP
 return ldapTemplate.search("", filter, new UserMapper());
 }
 
 private String escapeLdapValue(String value) {
 return value.replace("\\", "\\\\").replace("*", "\\*").replace("(", "\\(").replace(")", "\\)").replace("\0", "\\00");
 }
}
```

### Общие методы защиты от Injection

#### 1. Валидация и санитизация ввода

```java
public class InputValidator {
 
 private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
 private static final Pattern EMAIL_PATTERN = 
 Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
 
 public static boolean isValidUsername(String username) {
 return username!= null && 
 username.length() >= 3 && 
 username.length() <= 50 &&
 ALPHANUMERIC_PATTERN.matcher(username).matches();
 }
 
 public static boolean isValidEmail(String email) {
 return email!= null && EMAIL_PATTERN.matcher(email).matches();
 }
 
 public static String sanitizeHtml(String input) {
 if (input == null) return null;
 
 // Удаление потенциально опасных тегов
 return input.replaceAll("<script[^>]*>.*?</script>", "").replaceAll("<[^>]+>", "");
 }
}
```

#### 2. Использование ORM

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
 
 // JPA автоматически защищает от SQL injection
 Optional<User> findByUsername(String username);
 
 Optional<User> findByUsernameAndEmail(String username, String email);
 
 @Query("SELECT u FROM User u WHERE u.username =:username AND u.status =:status")
 Optional<User> findActiveUser(@Param("username") String username, 
 @Param("status") UserStatus status);
}
```

#### 3. Prepared Statements для NoSQL

```java
@Repository
public class MongoUserRepository {
 
 @Autowired
 private MongoTemplate mongoTemplate;
 
 public Optional<User> findByUsername(String username) {
 // Правильное использование MongoDB Query
 Query query = Query.query(Criteria.where("username").is(username));
 return Optional.ofNullable(mongoTemplate.findOne(query, User.class));
 }
 
 // НИКОГДА НЕ ДЕЛАЙТЕ ТАК!
 // String jsonQuery = "{$where: \"this.username == '" + username + "'\"}";
 // mongoTemplate.find(new BasicQuery(jsonQuery), User.class);
}
```

## A04:2021 - Insecure Design

Insecure Design — это категория уязвимостей, связанных с недостатками в архитектуре и дизайне системы безопасности.

### Принципы Secure Design

#### 1. Defense in Depth (Многоуровневая защита)

```java
public class SecureAccountService {
 
 private final AccountRepository accountRepository;
 private final AuditService auditService;
 private final NotificationService notificationService;
 
 @Transactional
 public void transferMoney(Account from, Account to, BigDecimal amount) {
 // 1. Валидация на уровне сервиса
 validateTransfer(from, to, amount);
 
 // 2. Проверка баланса
 if (from.getBalance().compareTo(amount) < 0) {
 auditService.logFailedTransfer(from, to, amount, "INSUFFICIENT_FUNDS");
 throw new InsufficientFundsException();
 }
 
 // 3. Выполнение перевода
 from.debit(amount);
 to.credit(amount);
 
 // 4. Аудит
 auditService.logSuccessfulTransfer(from, to, amount);
 
 // 5. Уведомление
 notificationService.notifyTransfer(from, to, amount);
 }
 
 private void validateTransfer(Account from, Account to, BigDecimal amount) {
 if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
 throw new IllegalArgumentException("Invalid amount");
 }
 if (from.equals(to)) {
 throw new IllegalArgumentException("Cannot transfer to same account");
 }
 // Дополнительные проверки...
 }
}
```

#### 2. Fail-Safe Defaults

```java
public class PermissionManager {
 
 private final Map<String, Set<String>> userPermissions = new ConcurrentHashMap<>();
 
 // По умолчанию - никаких прав
 public boolean hasPermission(String username, String permission) {
 return userPermissions.getOrDefault(username, Collections.emptySet()).contains(permission);
 }
 
 // Явное предоставление прав
 public void grantPermission(String username, String permission) {
 userPermissions.computeIfAbsent(username, k -> new HashSet<>()).add(permission);
 }
 
 // Явное отзывание прав
 public void revokePermission(String username, String permission) {
 Set<String> permissions = userPermissions.get(username);
 if (permissions!= null) {
 permissions.remove(permission);
 }
 }
}
```

#### 3. Secure by Design Patterns

**Circuit Breaker Pattern:
```java
public class CircuitBreakerTransferService {
 
 private final TransferService transferService;
 private final CircuitBreaker circuitBreaker;
 
 public void transfer(Account from, Account to, BigDecimal amount) {
 if (circuitBreaker.isOpen()) {
 throw new ServiceUnavailableException("Transfer service is temporarily unavailable");
 }
 
 try {
 transferService.transfer(from, to, amount);
 circuitBreaker.recordSuccess();
 } catch (Exception e) {
 circuitBreaker.recordFailure();
 throw e;
 }
 }
}
```

## A05:2021 - Security Misconfiguration

Security Misconfiguration включает неправильную настройку безопасности, использование настроек по умолчанию и другие конфигурационные проблемы.

### Распространенные проблемы

#### 1. Настройки по умолчанию

- Административные аккаунты с default паролями
- Включенные debugging features в production
- Необходимые headers безопасности отключены

#### 2. Избыточные права

- Приложения запускаются с root правами
- БД пользователей имеют admin права
- Cloud storage buckets публично доступны

#### 3. Отсутствие hardening

- Устаревшее ПО
- Не отключенные ненужные сервисы
- Отсутствие rate limiting

### Защита от Security Misconfiguration

#### 1. Security Headers

```java
@Configuration
public class SecurityHeadersConfig {
 
 @Bean
 public FilterRegistrationBean<HeaderFilter> headerFilter() {
 FilterRegistrationBean<HeaderFilter> registrationBean = new FilterRegistrationBean<>();
 registrationBean.setFilter(new HeaderFilter());
 registrationBean.addUrlPatterns("/*");
 return registrationBean;
 }
 
 public static class HeaderFilter implements Filter {
 
 @Override
 public void doFilter(ServletRequest request, ServletResponse response, 
 FilterChain chain) throws IOException, ServletException {
 
 HttpServletResponse httpResponse = (HttpServletResponse) response;
 
 // Security Headers
 httpResponse.setHeader("X-Content-Type-Options", "nosniff");
 httpResponse.setHeader("X-Frame-Options", "DENY");
 httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
 httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
 httpResponse.setHeader("Content-Security-Policy", "default-src 'self'");
 httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
 
 chain.doFilter(request, response);
 }
 }
}
```

#### 2. Environment-specific Configuration

```yaml
# application.yml
spring:
 profiles:
 active: ${SPRING_PROFILES_ACTIVE:development}
 
---
spring:
 config:
 activate:
 on-profile: production
 
 security:
 require-ssl: true
 
 datasource:
 url: ${DATABASE_URL}
 username: ${DB_USERNAME}
 password: ${DB_PASSWORD}
 
management:
 endpoints:
 web:
 exposure:
 include: health,info,prometheus
 base-path: /internal
 
---
spring:
 config:
 activate:
 on-profile: development
 
 datasource:
 url: jdbc:h2:mem:testdb
 username: sa
 password: ""
 
 h2:
 console:
 enabled: true
 
management:
 endpoints:
 web:
 exposure:
 include: "*"
```

#### 3. Infrastructure as Code Security

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
 app:
 image: myapp:latest
 environment:
 - SPRING_PROFILES_ACTIVE=production
 - JAVA_OPTS=-Xmx2g -Xms2g -XX:+UseG1GC
 security_opt:
 - no-new-privileges:true
 read_only: true
 tmpfs:
 - /tmp
 user: "1001:1001" # non-root user
 networks:
 - secure_network

networks:
 secure_network:
 driver: bridge
 internal: true
```

## A06:2021 - Vulnerable Components

Использование компонентов с известными уязвимостями — одна из наиболее распространенных проблем безопасности.

### Типы vulnerable components

#### 1. Direct Vulnerabilities

- Устаревшие версии библиотек
- ПО с известными CVE
- Unsupported компоненты

#### 2. Indirect Vulnerabilities

- Зависимости зависимостей
- Транзитивные уязвимости
- Уязвимости в runtime окружении

### Методы выявления уязвимостей

#### 1. Dependency Scanning

```xml
<!-- pom.xml -->
<build>
 <plugins>
 <plugin>
 <groupId>org.owasp</groupId>
 <artifactId>dependency-check-maven</artifactId>
 <version>7.1.1</version>
 <executions>
 <execution>
 <goals>
 <goal>check</goal>
 </goals>
 </execution>
 </executions>
 </plugin>
 </plugins>
</build>
```

#### 2. Snyk/Open Source Vulnerabilities

```yaml
#.github/workflows/security.yml
name: Security Scan
on:
 push:
 branches: [ main ]
 pull_request:
 branches: [ main ]

jobs:
 security:
 runs-on: ubuntu-latest
 steps:
 - uses: actions/checkout@v3
 - name: Run Snyk to check for vulnerabilities
 uses: snyk/actions/maven@master
 env:
 SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
 with:
 args: --severity-threshold=high
```

#### 3. Automated Updates

```xml
<!-- pom.xml - Dependabot configuration -->
<dependencyManagement>
 <dependencies>
 <!-- Regularly updated dependencies -->
 <dependency>
 <groupId>org.springframework.boot</groupId>
 <artifactId>spring-boot-starter-parent</artifactId>
 <version>3.0.0</version>
 <type>pom</type>
 <scope>import</scope>
 </dependency>
 </dependencies>
</dependencyManagement>
```

### Best Practices

1. Регулярные обновления: Мониторьте и обновляйте зависимости
2. Minimal Dependencies: Используйте только необходимые компоненты
3. Security Monitoring: Настройте оповещения о новых уязвимостях
4. SBOM (Software Bill of Materials): Ведите учет всех компонентов

## A07:2021 - Identification and Authentication Failures

Проблемы с идентификацией и аутентификацией пользователей.

### Распространенные проблемы

#### 1. Credential Stuffing

Атаки с использованием скомпрометированных учетных данных из других сервисов.

#### 2. Brute Force Attacks

Автоматизированные попытки подбора паролей.

#### 3. Weak Password Policies

Отсутствие требований к сложности паролей.

### Защита от Authentication Failures

#### 1. Multi-Factor Authentication (MFA)

```java
@Configuration
@EnableWebSecurity
public class MfaSecurityConfig {
 
 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 http.authorizeRequests().antMatchers("/login").permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login").and().logout().logoutSuccessUrl("/login").and().sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(false);
 
 return http.build();
 }
}
```

#### 2. Rate Limiting

```java
@RestController
@RequestMapping("/auth")
@RateLimited
public class AuthController {
 
 private final AuthService authService;
 private final RateLimiter rateLimiter;
 
 @PostMapping("/login")
 public ResponseEntity<?> login(@RequestBody LoginRequest request, 
 HttpServletRequest httpRequest) {
 
 String clientIp = getClientIp(httpRequest);
 
 if (!rateLimiter.allowRequest(clientIp)) {
 return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many login attempts. Please try again later.");
 }
 
 try {
 AuthenticationResponse response = authService.authenticate(request);
 rateLimiter.recordSuccess(clientIp);
 return ResponseEntity.ok(response);
 } catch (BadCredentialsException e) {
 rateLimiter.recordFailure(clientIp);
 throw e;
 }
 }
 
 private String getClientIp(HttpServletRequest request) {
 String xForwardedFor = request.getHeader("X-Forwarded-For");
 if (xForwardedFor!= null &&!xForwardedFor.isEmpty()) {
 return xForwardedFor.split(",")[0].trim();
 }
 return request.getRemoteAddr();
 }
}
```

#### 3. Account Lockout

```java
@Service
public class AuthService {
 
 private final UserRepository userRepository;
 private final PasswordEncoder passwordEncoder;
 private final Cache<String, Integer> loginAttemptsCache;
 
 public AuthenticationResponse authenticate(LoginRequest request) {
 User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
 
 String cacheKey = "login_attempts:" + user.getId();
 Integer attempts = loginAttemptsCache.getIfPresent(cacheKey);
 
 if (attempts!= null && attempts >= 5) {
 throw new AccountLockedException("Account is temporarily locked due to too many failed attempts");
 }
 
 if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
 loginAttemptsCache.put(cacheKey, attempts!= null? attempts + 1: 1);
 throw new BadCredentialsException("Invalid credentials");
 }
 
 // Успешная аутентификация - сброс счетчика
 loginAttemptsCache.invalidate(cacheKey);
 
 return generateToken(user);
 }
}
```

## A08:2021 - Software Integrity Failures

Проблемы с целостностью ПО и данных.

### Типы проблем

#### 1. Unsigned Software

Использование неподписанного или неправильно подписанного ПО.

#### 2. Automatic Updates from Untrusted Sources

Загрузка обновлений из ненадежных источников.

#### 3. CI/CD Pipeline Vulnerabilities

Уязвимости в процессах сборки и развертывания.

### Защита от Integrity Failures

#### 1. Code Signing

```bash
# Создание keystore для подписи
keytool -genkeypair -alias mykey -keyalg RSA -keysize 2048 -keystore keystore.jks

# Подпись JAR файла
jarsigner -keystore keystore.jks -signedjar signed-app.jar app.jar mykey
```

#### 2. Secure CI/CD Pipeline

```yaml
#.github/workflows/secure-build.yml
name: Secure Build and Deploy
on:
 push:
 branches: [ main ]

jobs:
 build:
 runs-on: ubuntu-latest
 
 steps:
 - uses: actions/checkout@v3
 with:
 fetch-depth: 0
 
 - name: Verify commit signatures
 run: |
 # Проверка подписей коммитов
 git log --show-signature --oneline -10
 
 - name: Security scan
 uses: github/super-linter@v4
 
 - name: Dependency check
 uses: dependency-check/Dependency-Check_Action@main
 
 - name: Build and sign
 run: |./mvnw clean package
 jarsigner -keystore keystore.jks -signedjar signed-app.jar target/app.jar mykey
 
 - name: Deploy to staging
 if: success()
 run: |
 # Secure deployment with checksum verification
 sha256sum signed-app.jar > app.sha256
 # Deploy logic...
```

#### 3. SBOM (Software Bill of Materials)

```xml
<!-- pom.xml -->
<build>
 <plugins>
 <plugin>
 <groupId>org.cyclonedx</groupId>
 <artifactId>cyclonedx-maven-plugin</artifactId>
 <version>2.7.4</version>
 <executions>
 <execution>
 <phase>package</phase>
 <goals>
 <goal>makeBom</goal>
 </goals>
 </execution>
 </executions>
 </plugin>
 </plugins>
</build>
```

## A09:2021 - Security Logging and Monitoring Failures

Недостаточное логирование и мониторинг событий безопасности.

### Важность Security Logging

1. Detection: Выявление атак и подозрительной активности
2. Investigation: Анализ инцидентов безопасности
3. Compliance: Соответствие требованиям регуляторов
4. Forensics: Судебные расследования

### Что логировать

#### 1. Authentication Events

```java
@Service
public class AuthService {
 
 private final Logger logger = LoggerFactory.getLogger(AuthService.class);
 
 public AuthenticationResponse authenticate(LoginRequest request) {
 String username = request.getUsername();
 
 try {
 User user = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("User not found"));
 
 if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
 logger.warn("Failed login attempt for user: {} from IP: {}", 
 username, getClientIp());
 throw new BadCredentialsException("Invalid credentials");
 }
 
 logger.info("Successful login for user: {} from IP: {}", 
 username, getClientIp());
 
 return generateToken(user);
 
 } catch (BadCredentialsException e) {
 logger.warn("Failed login attempt for user: {} from IP: {}", 
 username, getClientIp());
 throw e;
 }
 }
}
```

#### 2. Authorization Failures

```java
@Aspect
@Component
public class SecurityAuditAspect {
 
 private final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
 
 @AfterThrowing(pointcut = "@annotation(org.springframework.security.access.prepost.PreAuthorize)", 
 throwing = "ex")
 public void logAuthorizationFailure(JoinPoint joinPoint, Exception ex) {
 if (ex instanceof AccessDeniedException) {
 String methodName = joinPoint.getSignature().toString();
 String username = SecurityContextHolder.getContext().getAuthentication().getName();
 
 auditLogger.warn("Authorization failure - User: {}, Method: {}, Reason: {}", 
 username, methodName, ex.getMessage());
 }
 }
}
```

#### 3. Sensitive Operations

```java
@Service
public class AccountService {
 
 private final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
 
 @Transactional
 public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
 Account fromAccount = accountRepository.findById(fromAccountId).orElseThrow(() -> new AccountNotFoundException("Source account not found"));
 
 Account toAccount = accountRepository.findById(toAccountId).orElseThrow(() -> new AccountNotFoundException("Destination account not found"));
 
 String username = SecurityContextHolder.getContext().getAuthentication().getName();
 
 // Логирование чувствительной операции
 securityLogger.info("Money transfer initiated - User: {}, From: {}, To: {}, Amount: {}", 
 username, fromAccountId, toAccountId, amount);
 
 try {
 // Выполнение перевода
 fromAccount.debit(amount);
 toAccount.credit(amount);
 
 accountRepository.save(fromAccount);
 accountRepository.save(toAccount);
 
 securityLogger.info("Money transfer completed successfully - Transaction ID: {}", 
 generateTransactionId());
 
 } catch (Exception e) {
 securityLogger.error("Money transfer failed - User: {}, From: {}, To: {}, Amount: {}, Error: {}", 
 username, fromAccountId, toAccountId, amount, e.getMessage());
 throw e;
 }
 }
}
```

### Security Monitoring

#### 1. Log Aggregation

```yaml
# logback-spring.xml
<configuration>
 <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
 
 <!-- Security logs -->
 <appender name="SECURITY" class="ch.qos.logback.core.rolling.RollingFileAppender">
 <file>logs/security.log</file>
 <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
 <fileNamePattern>logs/security.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
 <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
 <maxFileSize>100MB</maxFileSize>
 </timeBasedFileNamingAndTriggeringPolicy>
 </rollingPolicy>
 <encoder>
 <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
 </encoder>
 </appender>
 
 <logger name="SECURITY" level="INFO" additivity="false">
 <appender-ref ref="SECURITY"/>
 </logger>
 
 <logger name="AUDIT" level="WARN" additivity="false">
 <appender-ref ref="SECURITY"/>
 </logger>
</configuration>
```

#### 2. Alerting

```java
@Component
public class SecurityEventListener {
 
 private final AlertService alertService;
 
 @EventListener
 public void handleFailedLogin(FailedLoginEvent event) {
 // Проверка на brute force атаку
 int recentFailures = getRecentFailures(event.getUsername());
 
 if (recentFailures >= 5) {
 alertService.sendAlert("Brute force attack detected for user: " + event.getUsername());
 }
 
 if (recentFailures >= 10) {
 // Автоматическая блокировка аккаунта
 accountService.lockAccount(event.getUsername());
 alertService.sendAlert("Account locked due to multiple failed login attempts: " + event.getUsername());
 }
 }
 
 @EventListener
 public void handleSuspiciousActivity(SuspiciousActivityEvent event) {
 alertService.sendAlert("Suspicious activity detected: " + event.getDescription());
 }
}
```

## A10:2021 - Server-Side Request Forgery

SSRF позволяет злоумышленнику заставить сервер выполнить запросы к внутренним ресурсам или внешним сервисам.

### Как работает SSRF

#### 1. Basic SSRF

```java
// Уязвимый код
@GetMapping("/fetch")
public String fetchUrl(@RequestParam String url) {
 // Нет валидации URL!
 return restTemplate.getForObject(url, String.class);
}

// Атака: /fetch?url=http://internal-service/admin/users
// Или: /fetch?url=file:///etc/passwd
```

#### 2. Blind SSRF

```java
@PostMapping("/webhook")
public void processWebhook(@RequestBody WebhookPayload payload) {
 // Сервер отправляет запросы на URL из payload
 // Злоумышленник может заставить сервер сканировать внутреннюю сеть
 restTemplate.postForObject(payload.getCallbackUrl(), payload.getData(), Void.class);
}
```

### Защита от SSRF

#### 1. URL Validation and Whitelisting

```java
@Service
public class UrlValidator {
 
 private static final List<String> ALLOWED_HOSTS = Arrays.asList(
 "api.github.com", "api.twitter.com", "api.linkedin.com"
 );
 
 private static final List<String> ALLOWED_SCHEMES = Arrays.asList("http", "https");
 
 public boolean isValidUrl(String url) {
 try {
 URI uri = new URI(url);
 
 // Проверка схемы
 if (!ALLOWED_SCHEMES.contains(uri.getScheme())) {
 return false;
 }
 
 // Проверка хоста
 String host = uri.getHost();
 if (host == null ||!ALLOWED_HOSTS.contains(host)) {
 return false;
 }
 
 // Проверка на localhost и private IP
 if (isLocalhostOrPrivate(host)) {
 return false;
 }
 
 return true;
 
 } catch (URISyntaxException e) {
 return false;
 }
 }
 
 private boolean isLocalhostOrPrivate(String host) {
 try {
 InetAddress address = InetAddress.getByName(host);
 
 // Проверка на localhost
 if (address.isLoopbackAddress()) {
 return true;
 }
 
 // Проверка на private IP ranges
 return address.isSiteLocalAddress() || 
 address.isLinkLocalAddress() ||
 isPrivateNetwork(address.getHostAddress());
 
 } catch (UnknownHostException e) {
 return true; // Блокируем неизвестные хосты
 }
 }
 
 private boolean isPrivateNetwork(String ip) {
 // 10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16
 return ip.startsWith("10.") ||
 (ip.startsWith("172.") && isInRange(ip, 16, 31)) ||
 ip.startsWith("192.168.");
 }
 
 private boolean isInRange(String ip, int start, int end) {
 try {
 int secondOctet = Integer.parseInt(ip.split("\\.")[1]);
 return secondOctet >= start && secondOctet <= end;
 } catch (Exception e) {
 return false;
 }
 }
}
```

#### 2. Network-Level Protection

```java
@Configuration
public class HttpClientConfig {
 
 @Bean
 public RestTemplate restTemplate() {
 // Настройка HttpClient с ограничениями
 HttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(20).setMaxConnPerRoute(10).setConnectionTimeToLive(30, TimeUnit.SECONDS).build();
 
 return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
 }
 
 @Bean
 public WebClient webClient() {
 // Настройка WebClient с таймаутами
 return WebClient.builder().clientConnector(new ReactorClientHttpConnector(
 HttpClient.create().responseTimeout(Duration.ofSeconds(10)).doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(10)).addHandlerLast(new WriteTimeoutHandler(10))))).build();
 }
}
```

#### 3. Service Architecture Protection

```java
@RestController
@RequestMapping("/api/external")
public class ExternalApiController {
 
 private final UrlValidator urlValidator;
 private final ExternalApiService externalApiService;
 
 @GetMapping("/fetch")
 public ResponseEntity<?> fetchExternalData(@RequestParam String url) {
 
 // Валидация URL
 if (!urlValidator.isValidUrl(url)) {
 return ResponseEntity.badRequest().body("Invalid or forbidden URL");
 }
 
 try {
 // Использование отдельного сервиса с ограничениями
 String data = externalApiService.fetchData(url);
 return ResponseEntity.ok(data);
 
 } catch (Exception e) {
 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching external data");
 }
 }
}

@Service
public class ExternalApiService {
 
 private final RestTemplate restTemplate;
 
 public String fetchData(String url) {
 // Дополнительные проверки и ограничения
 if (url.length() > 2048) {
 throw new IllegalArgumentException("URL too long");
 }
 
 // Таймаут и ограничения размера ответа
 ResponseEntity<String> response = restTemplate.exchange(
 url, HttpMethod.GET, null, String.class);
 
 String body = response.getBody();
 if (body!= null && body.length() > 10 * 1024 * 1024) { // 10MB limit
 throw new IllegalArgumentException("Response too large");
 }
 
 return body;
 }
}
```

### Дополнительные меры защиты

1. DNS Resolution Control: Контроль DNS разрешений
2. Network Segmentation: Разделение сети на сегменты
3. Rate Limiting: Ограничение количества запросов
4. Monitoring: Мониторинг исходящих запросов

## Заключение

OWASP Top 10 представляет собой фундаментальные знания о безопасности веб-приложений. Каждая категория уязвимостей требует комплексного подхода к защите, включая правильную архитектуру, безопасное кодирование, регулярные обновления и мониторинг. Senior Java Developer должен не только знать эти уязвимости, но и уметь реализовывать эффективные меры защиты на всех уровнях приложения.

