# Вопросы на собеседовании: Application Security

**Комплексное руководство по вопросам собеседования на тему Application Security для Senior Java Developer. Включает детальные объяснения концепций, практические примеры на Java + Spring, best practices и troubleshooting.**

**Дата последнего обновления:** 2026-01-25


Безопасность приложений является критически важным аспектом для Senior Java Developer. Понимание угроз безопасности, методов защиты и безопасных практик разработки позволяет создавать надежные и защищенные приложения.Дата последнего обновления: 2026-01-24

## Полезные ссылки

### Официальная документация
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Java Security Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/security/)

### Ресурсы
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [OWASP Cheat Sheet](https://cheatsheetseries.owasp.org/)

### См. также
- `owasp-top10-interview.md` - OWASP Top 10
- `authentication-authorization-patterns-interview.md` - Паттерны аутентификации
- `../../frameworks/spring/spring-security-interview.md` - Spring Security

## Содержание

- [Q1. Что такое безопасность приложений и почему она важна?](#q1-что-такое-безопасность-приложений-и-почему-она-важна)
- [Q2. Какие основные принципы безопасности приложений?](#q2-какие-основные-принципы-безопасности-приложений)
- [Q3. Что такое аутентификация и авторизация?](#q3-что-такое-аутентификация-и-авторизация)
- [Q4. Какие методы аутентификации существуют?](#q4-какие-методы-аутентификации-существуют)
- [Q5. Как реализовать безопасное хранение паролей?](#q5-как-реализовать-безопасное-хранение-паролей)
- [Q6. Что такое JWT и как он работает?](#q6-что-такое-jwt-и-как-он-работает)
- [Q7. Как защититься от SQL-инъекций?](#q7-как-защититься-от-sql-инъекций)
- [Q8. Как защититься от XSS-атак?](#q8-как-защититься-от-xss-атак)
- [Q9. Что такое CSRF и как от него защититься?](#q9-что-такое-csrf-и-как-от-него-защититься)
- [Q10. Как реализовать шифрование данных?](#q10-как-реализовать-шифрование-данных)

## Q1. Что такое безопасность приложений и почему она важна?

Безопасность приложений — это комплекс мер и практик, направленных на защиту программного обеспечения от различных угроз, уязвимостей и атак, которые могут привести к несанкционированному доступу, утечке данных или нарушению работоспособности системы.

### Почему безопасность важна?

1. Защита конфиденциальных данных: Предотвращение утечек персональных данных, финансовой информации, интеллектуальной собственности
2. Соблюдение регуляторных требований: GDPR, HIPAA, PCI DSS и другие стандарты
3. Поддержание доверия пользователей: Защита репутации компании и доверие клиентов
4. Финансовые потери: Предотвращение убытков от атак и восстановления систем
5. Юридические последствия: Избежание штрафов и судебных исков

### Уровни безопасности

#### 1. Application Level Security
- Безопасность кода приложения
- Валидация ввода
- Аутентификация и авторизация
- Шифрование данных

#### 2. Infrastructure Level Security
- Firewall и network security
- Secure configurations
- Monitoring и logging

#### 3. Data Level Security
- Encryption at rest
- Encryption in transit
- Secure storage

## Q2. Какие основные принципы безопасности приложений?

### CIA Triad (Confidentiality, Integrity, Availability)

#### 1. Confidentiality (Конфиденциальность)
Защита информации от несанкционированного доступа.Примеры:
- Шифрование данных
- Контроль доступа
- Secure communication protocols

#### 2. Integrity (Целостность)
Обеспечение корректности и полноты данных.Примеры:
- Hash функции для проверки целостности
- Digital signatures
- Input validation

#### 3. Availability (Доступность)
Обеспечение доступности системы для авторизованных пользователей.Примеры:
- Redundancy и failover
- DDoS protection
- Resource management

### Defense in Depth (Многоуровневая защита)

Принцип многослойной защиты, где каждый уровень предоставляет дополнительную защиту.Уровни защиты:
1. Network Level: Firewall, IDS/IPS
2. Application Level: Input validation, authentication
3. Data Level: Encryption, access control
4. Host Level: OS hardening, antivirus

### Principle of Least Privilege (Принцип наименьших привилегий)

Пользователи и процессы должны иметь минимально необходимые права для выполнения своих задач.Применение:
- Role-based access control (RBAC)
- Just-in-time access
- Regular review of permissions

## Q3. Что такое аутентификация и авторизация?

### Аутентификация (Authentication)

Аутентификация — это процесс проверки подлинности пользователя или системы.Цель: Убедиться, что пользователь является тем, кем себя называет.Методы:
- Something you know: Пароль, PIN
- Something you have: Токен, смарт-карта, телефон
- Something you are: Биометрия (отпечаток пальца, лицо)

### Авторизация (Authorization)

Авторизация — это процесс определения прав и разрешений пользователя после успешной аутентификации.Цель: Определить, какие действия пользователь может выполнять.Модели:
- Role-Based Access Control (RBAC)
- Attribute-Based Access Control (ABAC)
- Access Control Lists (ACL)

### Разница между Authentication и Authorization

```java
// Пример: Банковское приложение
public class BankingService {
 
 // Аутентификация: Проверка личности пользователя
 public User authenticate(String username, String password) {
 User user = userRepository.findByUsername(username);
 if (user!= null && passwordEncoder.matches(password, user.getPassword())) {
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

### 1. Basic Authentication

Простейший метод, использующий username:password в base64 кодировке.Пример HTTP запроса:
```
Authorization: Basic dXNlcjpwYXNzd29yZA==
```

**Проблемы:
- Небезопасно без HTTPS
- Нет механизма logout
- Пароль передается при каждом запросе

### 2. Session-Based Authentication

Использование серверных сессий для отслеживания аутентифицированных пользователей.Процесс:
1. Пользователь логинится
2. Сервер создает сессию и отправляет session ID в cookie
3. Клиент отправляет session ID с каждым запросом
4. Сервер проверяет сессию

**Пример с Spring Security:
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

### 3. Token-Based Authentication

Использование токенов (JWT, OAuth tokens) для аутентификации.Преимущества:
- Stateless (сервер не хранит состояние)
- Масштабируемость
- Подходит для микросервисов

### 4. OAuth 2.0

Протокол авторизации для делегированного доступа.Роли:
- Resource Owner: Владелец ресурса
- Client: Приложение, запрашивающее доступ
- Authorization Server: Сервер авторизации
- Resource Server: Сервер ресурсов

### 5. Multi-Factor Authentication (MFA)

Использование нескольких факторов аутентификации.Факторы:
- Something you know (password)
- Something you have (SMS, app)
- Something you are (biometrics)

## Q5. Как реализовать безопасное хранение паролей?

### Никогда не храните пароли в открытом виде!Плохо:
```java
// НИКОГДА НЕ ДЕЛАЙТЕ ТАК!
user.setPassword(plainPassword);
userRepository.save(user);
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

#### 3. Argon2
- Победитель Password Hashing Competition
- Настраиваемая сложность
- Рекомендуется для новых проектов

### Лучшие практики

1. Используйте сильные алгоритмы: bcrypt, scrypt, Argon2
2. Добавляйте соль: Уникальная соль для каждого пароля
3. Регулярно обновляйте хэши: При изменении алгоритма
4. Ограничивайте попытки: Защита от brute-force атак
5. Используйте HTTPS: Всегда шифруйте трафик

## Q6. Что такое JWT и как он работает?

JWT (JSON Web Token) — это стандарт для создания токенов доступа, который определяет компактный и самодостаточный способ передачи информации между сторонами в виде JSON объекта.

### Структура JWT

JWT состоит из трех частей, разделенных точками: `header.payload.signature`

#### 1. Header (Заголовок)
```json
{
 "alg": "HS256",
 "typ": "JWT"
}
```

#### 2. Payload (Полезная нагрузка)
```json
{
 "sub": "1234567890",
 "name": "John Doe",
 "iat": 1516239022,
 "exp": 1516242622,
 "roles": ["USER", "ADMIN"]
}
```

#### 3. Signature (Подпись)
```
HMACSHA256(
 base64UrlEncode(header) + "." +
 base64UrlEncode(payload),
 secret
)
```

### Как работает JWT?

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

### Преимущества JWT

1. Stateless: Сервер не хранит состояние сессий
2. Self-contained: Токен содержит всю необходимую информацию
3. Cross-domain: Работает между разными доменами
4. Scalable: Легко масштабируется

### Недостатки JWT

1. Неотзываемость: Токен нельзя отозвать до истечения срока
2. Размер: Большие токены увеличивают размер запросов
3. Безопасность секрета: Компрометация секрета опасна

## Q7. Как защититься от SQL-инъекций?

SQL-инъекция — это атака, при которой злоумышленник может выполнить произвольный SQL код через уязвимое приложение.

### Уязвимый код (Плохо)

```java
// НИКОГДА НЕ ДЕЛАЙТЕ ТАК!
public User findUser(String username) {
 String query = "SELECT * FROM users WHERE username = '" + username + "'";
 return jdbcTemplate.queryForObject(query, User.class);
}

// Атака: username = "' OR '1'='1"
// Результат: SELECT * FROM users WHERE username = '' OR '1'='1'
```

### Защита: Prepared Statements

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

### Защита: JPA Criteria API

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
2. Least Privilege: Используйте учетные записи БД с минимальными правами
3. Stored Procedures: Используйте хранимые процедуры вместо динамического SQL
4. ORM: Используйте ORM фреймворки (JPA, Hibernate) с параметризованными запросами

## Q8. Как защититься от XSS-атак?

XSS (Cross-Site Scripting) — это атака, при которой злоумышленник может внедрить вредоносный скрипт в веб-страницу, который будет выполнен в браузере жертвы.

### Типы XSS

#### 1. Stored XSS (Хранимый)
Скрипт сохраняется на сервере и выполняется при каждом просмотре страницы.

#### 2. Reflected XSS (Отраженный)
Скрипт отражается в ответе сервера и выполняется сразу.

#### 3. DOM-based XSS
Скрипт модифицирует DOM в браузере.

### Защита от XSS

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
<c:out value="${post.content}" escapeXml="true"/>
```

#### 2. Content Security Policy (CSP)

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
 input = input.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("'", "&#x27;").replaceAll("&", "&amp;");
 
 return input;
 }
}
```

#### 4. Использование безопасных фреймворков

- **Spring Security: Автоматическая защита от XSS
- **OWASP Java Encoder: Библиотека для безопасного кодирования
- **JSoup: Для очистки HTML контента

## Q9. Что такое CSRF и как от него защититься?

CSRF (Cross-Site Request Forgery) — это атака, при которой злоумышленник заставляет пользователя выполнить нежелательное действие в веб-приложении, в котором пользователь аутентифицирован.

### Как работает CSRF?

1. Пользователь логинится в уязвимое приложение
2. Злоумышленник создает вредоносную страницу с формой
3. При посещении этой страницы браузер автоматически отправляет запрос с куками пользователя
4. Приложение выполняет действие, думая что это легитимный запрос

### Защита от CSRF

#### 1. CSRF Token

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

#### 2. Настройка Spring Security

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

#### 3. SameSite Cookies

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

#### 4. Проверка Origin/Referer Headers

```java
public class CsrfProtectionFilter extends OncePerRequestFilter {
 
 private static final String[] ALLOWED_ORIGINS = {"https://myapp.com"};
 
 @Override
 protected void doFilterInternal(HttpServletRequest request,
 HttpServletResponse response,
 FilterChain filterChain) throws ServletException, IOException {
 
 String origin = request.getHeader("Origin");
 String referer = request.getHeader("Referer");
 
 if (!isAllowedOrigin(origin) &&!isAllowedReferer(referer)) {
 response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF detected");
 return;
 }
 
 filterChain.doFilter(request, response);
 }
 
 private boolean isAllowedOrigin(String origin) {
 return origin!= null && Arrays.asList(ALLOWED_ORIGINS).contains(origin);
 }
 
 private boolean isAllowedReferer(String referer) {
 // Проверка referer header
 return referer!= null && referer.startsWith("https://myapp.com");
 }
}
```

### Дополнительные меры

1. Используйте POST для изменяющих операций**
2. Не храните чувствительные данные в куках**
3. Регулярно обновляйте сессии**
4. Используйте HTTPS**

## Q10. Как реализовать шифрование данных?

### Симметричное шифрование

Использует один и тот же ключ для шифрования и дешифрования.

```java
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SymmetricEncryption {
 
 private static final String ALGORITHM = "AES";
 private static final int KEY_SIZE = 256;
 
 public static SecretKey generateKey() throws Exception {
 KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
 keyGen.init(KEY_SIZE);
 return keyGen.generateKey();
 }
 
 public static String encrypt(String data, SecretKey key) throws Exception {
 Cipher cipher = Cipher.getInstance(ALGORITHM);
 cipher.init(Cipher.ENCRYPT_MODE, key);
 byte[] encryptedBytes = cipher.doFinal(data.getBytes());
 return Base64. getEncoder().encodeToString(encryptedBytes);
 }
 
 public static String decrypt(String encryptedData, SecretKey key) throws Exception {
 Cipher cipher = Cipher.getInstance(ALGORITHM);
 cipher.init(Cipher.DECRYPT_MODE, key);
 byte[] decryptedBytes = cipher.doFinal(Base64. getDecoder().decode(encryptedData));
 return new String(decryptedBytes);
 }
}
```

### Асимметричное шифрование

Использует пару ключей: публичный для шифрования, приватный для дешифрования.

```java
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
 for (byte b: hash) {
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
 return Base64. getEncoder().encodeToString(salt);
 }
}
```

### Best Practices шифрования

1. Используйте проверенные алгоритмы: AES для симметричного, RSA для асимметричного
2. Правильное управление ключами: Храните ключи securely, используйте key rotation
3. Используйте TLS/HTTPS: Шифруйте данные в транзите
4. Регулярно обновляйте алгоритмы: Следите за устаревшими алгоритмами
5. Используйте готовые библиотеки: Не реализуйте шифрование самостоятельно

## Заключение

Безопасность приложений требует комплексного подхода, включающего правильную аутентификацию, авторизацию, валидацию ввода, защиту от распространенных уязвимостей и шифрование чувствительных данных. Использование современных фреймворков безопасности и следование best practices позволяет создавать надежные и защищенные приложения.

