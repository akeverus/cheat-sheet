---
title: "Вопросы на собеседовании: Application Security"
description: "Комплексное руководство по безопасности приложений: OWASP, Spring Security, шифрование, CORS, валидация, защита от атак"
tags:
  - interview
  - security
  - application-security-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Application Security"
  - "Application Security interview"
  - "Безопасность приложений"
prerequisites:
  - "[[application-security]]"
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `Application Security`

Комплексное руководство по вопросам собеседования на тему `Application Security` для `Senior Java Developer`. Включает
детальные объяснения концепций, практические примеры на `Java` + `Spring`, best practices и troubleshooting.

**Безопасность приложений** (`AppSec`) -- одна из ключевых тем на собеседованиях для Senior-разработчиков. Ожидается не только знание терминов, но и умение применять конкретные меры защиты в коде, понимание threat model и способность обосновать выбор решения с точки зрения бизнес-рисков. Тема тесно связана с [Spring Security](../frameworks/spring/spring-security-interview.md), [OWASP Top 10](owasp-top10-interview.md) и [OAuth 2.0](oauth2-interview.md).

## Полезные ссылки

### Официальная документация

- [OWASP Top 10](https://owasp.org/Top10/) -- список наиболее критичных рисков
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/) -- официальная документация Spring Security
- [Java Security Documentation](https://docs.oracle.com/en/java/javase/17/security/) -- безопасность в JDK
- [JWT.io](https://jwt.io/) -- визуальный debugger для JWT
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/) -- практические руководства
- [CORS with Spring (Baeldung)](https://www.baeldung.com/spring-cors) -- настройка CORS
- [Spring Security CSRF (Baeldung)](https://www.baeldung.com/spring-security-csrf) -- защита от CSRF
- [Spring Boot Bean Validation (Baeldung)](https://www.baeldung.com/spring-boot-bean-validation) -- валидация ввода
- [Prevent XSS in Spring (Baeldung)](https://www.baeldung.com/spring-prevent-xss) -- защита от XSS

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы безопасности приложений**
- [Q1. (!) Что такое безопасность приложений и почему она важна?](#q1--что-такое-безопасность-приложений-и-почему-она-важна)
- [Q2. Какие основные принципы безопасности приложений?](#q2-какие-основные-принципы-безопасности-приложений)
- [Q3. (!) Что такое Defense in Depth и как применить в Java-приложении?](#q3--что-такое-defense-in-depth-и-как-применить-в-java-приложении)

**Аутентификация и авторизация**
- [Q4. (!) Что такое аутентификация и авторизация?](#q4--что-такое-аутентификация-и-авторизация)
- [Q5. Какие методы аутентификации существуют?](#q5-какие-методы-аутентификации-существуют)
- [Q6. (!) Как реализовать безопасное хранение паролей?](#q6--как-реализовать-безопасное-хранение-паролей)
- [Q7. (!) Что такое JWT и как он работает?](#q7--что-такое-jwt-и-как-он-работает)
- [Q8. Как реализовать refresh-токены?](#q8-как-реализовать-refresh-токены)

**Защита от атак**
- [Q9. (!) Как защититься от SQL-инъекций?](#q9--как-защититься-от-sql-инъекций)
- [Q10. (!) Как защититься от XSS-атак?](#q10--как-защититься-от-xss-атак)
- [Q11. (!) Что такое CSRF и как от него защититься?](#q11--что-такое-csrf-и-как-от-него-защититься)
- [Q12. Как защититься от Deserialization-атак?](#q12-как-защититься-от-deserialization-атак)
- [Q13. Как защититься от SSRF?](#q13-как-защититься-от-ssrf-server-side-request-forgery)
- [Q14. Как защититься от Injection-атак помимо SQL?](#q14-как-защититься-от-injection-атак-помимо-sql)

**Шифрование и криптография**
- [Q15. (!) Как реализовать шифрование данных в Java?](#q15--как-реализовать-шифрование-данных-в-java)
- [Q16. В чём разница между симметричным и асимметричным шифрованием?](#q16-в-чём-разница-между-симметричным-и-асимметричным-шифрованием)
- [Q17. Как обеспечить шифрование данных at rest и in transit?](#q17-как-обеспечить-шифрование-данных-at-rest-и-in-transit)

**Валидация ввода**
- [Q18. (!) Как реализовать валидацию ввода в Spring Boot?](#q18--как-реализовать-валидацию-ввода-в-spring-boot)
- [Q19. Как валидировать RequestParams и PathVariables?](#q19-как-валидировать-requestparams-и-pathvariables)

**CORS и Security Headers**
- [Q20. (!) Что такое CORS и как настроить в Spring?](#q20--что-такое-cors-и-как-настроить-в-spring)
- [Q21. (!) Что такое Security Headers и какие использовать?](#q21--что-такое-security-headers-и-какие-использовать)
- [Q22. Что такое Content Security Policy?](#q22-что-такое-content-security-policy-csp)

**Безопасность API**
- [Q23. (!) Как обеспечить безопасность REST API?](#q23--как-обеспечить-безопасность-rest-api)
- [Q24. Как реализовать Rate Limiting?](#q24-как-реализовать-rate-limiting)

**OWASP и Threat Modeling**
- [Q25. (!) Что такое OWASP Top 10?](#q25--что-такое-owasp-top-10)
- [Q26. Что такое Threat Modeling?](#q26-что-такое-threat-modeling)

**Управление секретами и зависимостями**
- [Q27. (!) Что такое Secrets Management?](#q27--что-такое-secrets-management)
- [Q28. Как обеспечить безопасность зависимостей?](#q28-как-обеспечить-безопасность-зависимостей)

**Тестирование безопасности**
- [Q29. Что такое SAST и DAST?](#q29-что-такое-sast-и-dast)
- [Q30. Как проводить Security Code Review?](#q30-как-проводить-security-code-review)
- [Q31. Что такое Security Testing в CI/CD?](#q31-что-такое-security-testing-в-cicd)

**Безопасность инфраструктуры и операции**
- [Q32. Как обеспечить безопасность в контейнерах?](#q32-как-обеспечить-безопасность-в-контейнерах)
- [Q33. Что такое Security Misconfiguration?](#q33-что-такое-security-misconfiguration)
- [Q34. Как обеспечить безопасность файловых загрузок?](#q34-как-обеспечить-безопасность-файловых-загрузок)
- [Q35. Как обеспечить безопасность логирования?](#q35-как-обеспечить-безопасность-логирования)

**Security by Design и процессы**
- [Q36. Что такое Security by Design?](#q36-что-такое-security-by-design)
- [Q37. Что такое Secure SDLC?](#q37-что-такое-secure-sdlc)
- [Q38. (!) Как реагировать на инциденты безопасности?](#q38--как-реагировать-на-инциденты-безопасности)
- [Q39. Как обеспечить безопасность в микросервисной архитектуре?](#q39-как-обеспечить-безопасность-в-микросервисной-архитектуре)
- [Q40. Что такое Zero Trust Architecture?](#q40-что-такое-zero-trust-architecture)

**Продвинутые темы**
- [Q41. (!) Как интегрировать HashiCorp Vault для управления секретами в Spring Boot?](#q41--как-интегрировать-hashicorp-vault-для-управления-секретами-в-spring-boot)
- [Q42. Как использовать Kubernetes Secrets безопасно?](#q42-как-использовать-kubernetes-secrets-безопасно)
- [Q43. (!) Как настроить SAST в CI/CD для Java-проекта?](#q43--как-настроить-sast-в-cicd-для-java-проекта)
- [Q44. Что такое DAST и как его применять?](#q44-что-такое-dast-и-как-его-применять)
- [Q45. (!) Как настроить TLS и взаимную аутентификацию (mTLS) в Spring Boot?](#q45--как-настроить-tls-и-взаимную-аутентификацию-mtls-в-spring-boot)

---

## Q1. (!) Что такое безопасность приложений и почему она важна?

**Безопасность приложений** (`Application Security`, `AppSec`) -- комплекс мер и практик, направленных на защиту программного обеспечения от угроз, уязвимостей и атак, которые могут привести к несанкционированному доступу, утечке данных или нарушению работоспособности системы.

### Почему безопасность важна?

1. **Защита данных** -- предотвращение утечек персональных данных, финансовой информации, интеллектуальной собственности
2. **Регуляторные требования** -- `GDPR`, `HIPAA`, `PCI DSS`, ФЗ-152 (для РФ)
3. **Репутация и доверие** -- инцидент может стоить компании миллионы и потерю клиентов
4. **Финансовые потери** -- стоимость устранения последствий взлома в разы превышает стоимость превентивных мер
5. **Юридическая ответственность** -- штрафы и судебные иски

### Уровни безопасности

```mermaid
graph TB
    subgraph "Application Level"
        A1[Валидация ввода]
        A2[Аутентификация / Авторизация]
        A3[Шифрование данных]
        A4[Безопасный код]
    end
    subgraph "Infrastructure Level"
        I1[Firewall / WAF]
        I2[Network segmentation]
        I3[TLS / mTLS]
    end
    subgraph "Data Level"
        D1[Encryption at rest]
        D2[Encryption in transit]
        D3[Key management]
    end
    A1 --> I1
    A3 --> D1
    I3 --> D2
```

На собеседовании важно показать, что безопасность -- это не чеклист, а непрерывный процесс, встроенный в разработку.

## Q2. Какие основные принципы безопасности приложений?

### `CIA Triad`

| Принцип | Описание | Пример |
|---------|----------|--------|
| `Confidentiality` | Защита от несанкционированного доступа | Шифрование, контроль доступа |
| `Integrity` | Данные не изменены несанкционированно | Хэши, цифровые подписи, `HMAC` |
| `Availability` | Система доступна авторизованным пользователям | DDoS-защита, redundancy |

### `Principle of Least Privilege`

Пользователи и процессы получают минимально необходимые права:

- `RBAC` -- доступ по ролям
- `Just-in-time` доступ -- временные привилегии
- Регулярный пересмотр прав

### `Fail Secure`

При ошибке система должна переходить в безопасное состояние, а не открытое:

```java
public ResponseEntity<?> getResource(@PathVariable Long id) {
    try {
        if (!authService.hasAccess(currentUser(), id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(resourceService.findById(id));
    } catch (Exception e) {
        // Fail secure: при любой ошибке -- отказ, а не доступ
        log.error("Access check failed for resource {}", id, e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
```

### `Separation of Duties`

Критичные операции требуют участия нескольких лиц (например, деплой в production требует approve от другого разработчика).

## Q3. (!) Что такое `Defense in Depth` и как применить в Java-приложении?

**`Defense in Depth`** (эшелонированная защита) -- принцип многослойной безопасности, где каждый уровень предоставляет дополнительную защиту. Компрометация одного слоя не приводит к полному взлому.

```mermaid
graph LR
    Client[Клиент] --> WAF[WAF / CDN]
    WAF --> LB[Load Balancer + TLS]
    LB --> GW[API Gateway<br/>Rate limiting, Auth]
    GW --> App[Spring Boot App<br/>Validation, AuthZ]
    App --> DB[(БД<br/>Encryption at rest)]
    
    style WAF fill:#e74c3c,color:white
    style LB fill:#e67e22,color:white
    style GW fill:#f39c12,color:white
    style App fill:#27ae60,color:white
    style DB fill:#2980b9,color:white
```

### Слои защиты в Java/Spring-приложении

1. **Сетевой уровень** -- `WAF`, `firewall`, `IDS/IPS`
2. **Транспортный уровень** -- `TLS 1.3`, `mTLS` между сервисами
3. **API Gateway** -- `rate limiting`, аутентификация, фильтрация
4. **Приложение** -- валидация ввода, авторизация, безопасная сериализация
5. **Данные** -- шифрование at rest, маскирование `PII`

На собеседовании покажите, что знаете не только про код, но и про инфраструктурные слои. Подробнее об аутентификации -- в [паттернах аутентификации](authentication-authorization-patterns-interview.md).

## Q4. (!) Что такое аутентификация и авторизация?

### Аутентификация (`Authentication`)

Процесс проверки подлинности -- **кто вы?**

Факторы:
- **Something you know** -- пароль, `PIN`
- **Something you have** -- токен, смарт-карта, телефон
- **Something you are** -- биометрия

### Авторизация (`Authorization`)

Определение прав после аутентификации -- **что вам разрешено?**

Модели:
- `RBAC` (`Role-Based Access Control`) -- доступ по ролям
- `ABAC` (`Attribute-Based Access Control`) -- по атрибутам (отдел, локация, время)
- `ACL` (`Access Control Lists`) -- списки прав на конкретные ресурсы

### Пример разницы в коде

```java
@Service
@RequiredArgsConstructor
public class BankingService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // Аутентификация: ПРОВЕРКА ЛИЧНОСТИ
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }
        return user;
    }

    // Авторизация: ПРОВЕРКА ПРАВ
    @PreAuthorize("hasRole('MANAGER') and #from.ownerId == authentication.principal.id")
    public void transferMoney(Account from, Account to, BigDecimal amount) {
        // Spring Security проверяет роль и владение аккаунтом
        accountService.transfer(from, to, amount);
    }
}
```

Подробнее о паттернах -- в [вопросах по аутентификации](authentication-authorization-patterns-interview.md), о Spring Security -- в [Spring Security](../frameworks/spring/spring-security-interview.md), об OAuth -- в [OAuth 2.0](oauth2-interview.md).

## Q5. Какие методы аутентификации существуют?

### 1. `Basic Authentication`

Простейший метод -- `username:password` в `Base64`:

```http
Authorization: Basic dXNlcjpwYXNzd29yZA==
```

Проблемы: небезопасно без `HTTPS`, нет logout, пароль при каждом запросе.

### 2. `Session-Based Authentication`

Сервер создаёт сессию, отправляет `session ID` в cookie. Подходит для монолитных веб-приложений.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
            )
            .sessionManagement(session -> session
                .maximumSessions(1) // одна сессия на пользователя
                .maxSessionsPreventsLogin(true)
            );
        return http.build();
    }
}
```

### 3. `Token-Based Authentication` (`JWT`)

`Stateless` -- сервер не хранит состояние. Идеально для микросервисов и `SPA`.

### 4. `OAuth 2.0` / `OpenID Connect`

Делегированная авторизация. Роли: `Resource Owner`, `Client`, `Authorization Server`, `Resource Server`. Подробнее -- в [OAuth 2.0](oauth2-interview.md).

### 5. `Multi-Factor Authentication` (`MFA`)

Комбинация двух и более факторов. Рекомендуется для критичных систем.

```mermaid
sequenceDiagram
    participant U as Пользователь
    participant App as Приложение
    participant MFA as MFA Provider
    U->>App: Логин + пароль
    App->>App: Проверка пароля
    App->>MFA: Запрос второго фактора
    MFA->>U: SMS / TOTP код
    U->>App: Ввод кода
    App->>App: Проверка кода
    App->>U: Доступ предоставлен
```

## Q6. (!) Как реализовать безопасное хранение паролей?

**Никогда не храните пароли в открытом виде!**

### Правильный подход: хэширование с солью

```java
@Service
@RequiredArgsConstructor
public class UserService {

    // Spring Security сам управляет солью и work factor
    private final PasswordEncoder passwordEncoder;

    public void createUser(String username, String plainPassword) {
        String hashedPassword = passwordEncoder.encode(plainPassword);
        User user = new User(username, hashedPassword);
        userRepository.save(user);
    }

    public boolean authenticate(String username, String plainPassword) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(plainPassword, user.getPassword()))
                .orElse(false);
    }
}

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // DelegatingPasswordEncoder поддерживает миграцию алгоритмов
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
```

### Сравнение алгоритмов

| Алгоритм | Особенности | Рекомендация |
|----------|-------------|--------------|
| `bcrypt` | Адаптивный, встроенная соль, широко используется | Хороший выбор по умолчанию |
| `scrypt` | Память-зависимый, дороже для атакующего | Лучше `bcrypt` для GPU-атак |
| `Argon2` | Победитель `Password Hashing Competition`, настраиваемый | Рекомендуется для новых проектов |
| `PBKDF2` | Стандарт NIST, поддержка в JDK | Приемлемо, но уступает `Argon2` |

### Лучшие практики

1. Используйте `DelegatingPasswordEncoder` -- поддерживает миграцию алгоритмов
2. Увеличивайте work factor со временем (компенсация роста производительности железа)
3. `Rate limiting` на логин -- защита от `brute-force`
4. Не раскрывайте, существует ли пользователь (одинаковое сообщение "Invalid credentials")

## Q7. (!) Что такое `JWT` и как он работает?

`JWT` (`JSON Web Token`) -- компактный, самодостаточный стандарт для передачи информации между сторонами в виде `JSON`-объекта, подписанного цифровой подписью.

### Структура `JWT`: `header.payload.signature`

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsInJvbGVzIjpbIlVTRVIiXX0.signature
```

**Header**: алгоритм и тип токена. **Payload**: claims (утверждения). **Signature**: подпись для проверки целостности.

### Реализация в Spring Boot

```java
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms:3600000}")
    private long expirationMs;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
```

### Преимущества и недостатки

| Преимущества | Недостатки |
|-------------|-----------|
| `Stateless` -- не нужна серверная сессия | Нельзя отозвать до истечения `exp` |
| Самодостаточный -- содержит все claims | Размер больше, чем session ID |
| Кросс-доменный -- работает между сервисами | Компрометация секрета -- катастрофа |
| Масштабируемый | Не храните чувствительные данные в payload |

**На собеседовании часто спрашивают**: как отозвать `JWT`? Ответ: blacklist (например, в `Redis`), короткий `TTL` + refresh-токен, или переход на opaque tokens с introspection.

## Q8. Как реализовать refresh-токены?

Refresh-токен решает проблему короткого `TTL` у `access token`: пользователю не нужно логиниться заново.

```mermaid
sequenceDiagram
    participant Client
    participant Auth as Auth Server
    participant API as Resource Server
    
    Client->>Auth: POST /login (credentials)
    Auth->>Client: access_token (15 мин) + refresh_token (7 дней)
    
    Client->>API: GET /api/data + access_token
    API->>Client: 200 OK
    
    Note over Client,API: access_token истёк
    Client->>API: GET /api/data + expired access_token
    API->>Client: 401 Unauthorized
    
    Client->>Auth: POST /refresh + refresh_token
    Auth->>Client: new access_token + new refresh_token
```

```java
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/auth/refresh")
    public TokenResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService
                .findByToken(request.refreshToken())
                .orElseThrow(() -> new TokenRefreshException("Invalid refresh token"));

        refreshTokenService.verifyExpiration(refreshToken);

        // Ротация: старый refresh token инвалидируется
        refreshTokenService.deleteByToken(refreshToken.getToken());

        String newAccessToken = jwtService.generateToken(refreshToken.getUser());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(
                refreshToken.getUser().getId());

        return new TokenResponse(newAccessToken, newRefreshToken.getToken());
    }
}
```

Ключевой момент: **ротация refresh-токенов** -- при каждом обновлении старый токен инвалидируется, что защищает от replay-атак.

## Q9. (!) Как защититься от `SQL`-инъекций?

`SQL`-инъекция -- атака, при которой злоумышленник выполняет произвольный `SQL` через уязвимое приложение.

### Уязвимый код

```java
// НИКОГДА НЕ ДЕЛАЙТЕ ТАК!
public User findUser(String username) {
    String query = "SELECT * FROM users WHERE username = '" + username + "'";
    return jdbcTemplate.queryForObject(query, User.class);
}
// Атака: username = "' OR '1'='1" → SELECT * FROM users WHERE username = '' OR '1'='1'
```

### Защита: `Prepared Statements`

```java
@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password FROM users WHERE username = ?";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, new Object[]{username},
                            (rs, rowNum) -> new User(
                                    rs.getLong("id"),
                                    rs.getString("username"),
                                    rs.getString("password")
                            )));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
```

### Защита: `Spring Data JPA`

```java
public interface UserRepository extends JpaRepository<User, Long> {

    // Безопасно: Spring Data генерирует параметризованный запрос
    Optional<User> findByUsername(String username);

    // Безопасно: @Query с именованными параметрами
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    Optional<User> findActiveByEmail(@Param("email") String email);
}
```

### Дополнительные меры

1. **Валидация ввода** -- whitelist допустимых символов
2. **Least Privilege** -- учётная запись БД с минимальными правами
3. **ORM** -- `JPA`/`Hibernate` с параметризованными запросами
4. **Никогда** не конкатенируйте пользовательский ввод в `SQL`

## Q10. (!) Как защититься от `XSS`-атак?

`XSS` (`Cross-Site Scripting`) -- внедрение вредоносного скрипта в веб-страницу, который выполняется в браузере жертвы.

### Типы `XSS`

| Тип | Описание | Пример |
|-----|----------|--------|
| `Stored XSS` | Скрипт сохраняется на сервере (в БД) | Комментарий с `<script>` |
| `Reflected XSS` | Скрипт отражается в ответе сервера | `URL` с вредоносным параметром |
| `DOM-based XSS` | Скрипт модифицирует `DOM` в браузере | `innerHTML` с пользовательским вводом |

### Защита в Spring / Thymeleaf

```java
// 1. Thymeleaf автоматически экранирует вывод
// БЕЗОПАСНО: th:text экранирует HTML
// <div th:text="${post.content}"></div>

// НЕБЕЗОПАСНО: th:utext НЕ экранирует — избегайте!
// <div th:utext="${post.content}"></div>

// 2. Санитизация на сервере с OWASP Java Encoder
import org.owasp.encoder.Encode;

@Service
public class ContentService {

    public String sanitize(String userInput) {
        return Encode.forHtml(userInput);
    }

    public String sanitizeForJs(String userInput) {
        return Encode.forJavaScript(userInput);
    }
}

// 3. Использование Jsoup для очистки HTML
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public String cleanHtml(String dirtyHtml) {
    // Разрешаем только безопасные теги
    return Jsoup.clean(dirtyHtml, Safelist.basic());
}
```

### Настройка `CSP` для защиты от `XSS`

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.headers(headers -> headers
            .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'"))
            .xssProtection(xss -> xss.headerValue(
                    XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
    );
    return http.build();
}
```

## Q11. (!) Что такое `CSRF` и как от него защититься?

`CSRF` (`Cross-Site Request Forgery`) -- атака, при которой злоумышленник заставляет аутентифицированного пользователя выполнить нежелательное действие.

```mermaid
sequenceDiagram
    participant Victim as Жертва
    participant Bank as Банк (bank.com)
    participant Evil as Атакующий (evil.com)
    
    Victim->>Bank: Логин (получает session cookie)
    Evil->>Victim: Ссылка на evil.com
    Victim->>Evil: Открывает evil.com
    Evil->>Victim: Скрытая форма: POST bank.com/transfer
    Victim->>Bank: POST /transfer (cookie отправляется автоматически!)
    Bank->>Bank: Перевод выполнен
```

### Защита в Spring Security

Начиная с `Spring Security 4.x`, `CSRF`-защита включена по умолчанию для session-based приложений.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Для веб-приложений с сессиями: CSRF включён по умолчанию
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            );
        return http.build();
    }
}
```

### Когда можно отключить `CSRF`?

Для `stateless REST API` с токенной аутентификацией (`JWT` в `Authorization` header) `CSRF` не нужен, так как браузер не отправляет токен автоматически:

```java
// Для stateless REST API
http.csrf(csrf -> csrf.disable())
    .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
```

### `SameSite` cookies

```java
@Bean
public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setSameSite("Strict"); // или "Lax"
    serializer.setUseSecureCookie(true);
    serializer.setUseHttpOnlyCookie(true);
    return serializer;
}
```

## Q12. Как защититься от `Deserialization`-атак?

Десериализация недоверенных данных -- одна из самых опасных уязвимостей в Java (`OWASP A8`). Стандартный `ObjectInputStream` позволяет выполнить произвольный код.

### Правила защиты

```java
// ОПАСНО: никогда не десериализуйте из недоверенного источника
ObjectInputStream ois = new ObjectInputStream(untrustedInput);
Object obj = ois.readObject(); // RCE!

// БЕЗОПАСНО: используйте JSON вместо Java Serialization
@RestController
public class ApiController {

    private final ObjectMapper objectMapper;

    @PostMapping("/api/data")
    public ResponseEntity<?> processData(@Valid @RequestBody DataRequest request) {
        // Jackson десериализует JSON — безопаснее, чем Java Serialization
        // @Valid обеспечивает валидацию
        return ResponseEntity.ok(service.process(request));
    }
}
```

### Если Java Serialization неизбежна

```java
// Whitelist разрешённых классов
ObjectInputFilter filter = ObjectInputFilter.Config.createFilter(
        "com.myapp.model.*;!*"); // разрешаем только свои классы
ObjectInputStream ois = new ObjectInputStream(input);
ois.setObjectInputFilter(filter);
```

Подробнее о сериализации -- в [Java Serialization](../programming-languages/java/java-serialization-interview.md).

## Q13. Как защититься от `SSRF` (`Server-Side Request Forgery`)?

`SSRF` -- атака, при которой приложение делает запрос к внутреннему ресурсу по `URL`, контролируемому атакующим.

### Защита

```java
@Service
public class UrlFetchService {

    private static final Set<String> ALLOWED_HOSTS = Set.of(
            "api.example.com", "cdn.example.com");

    public String fetch(String url) {
        URI uri = URI.create(url);

        // 1. Проверка схемы
        if (!"https".equals(uri.getScheme())) {
            throw new SecurityException("Only HTTPS allowed");
        }

        // 2. Whitelist хостов
        if (!ALLOWED_HOSTS.contains(uri.getHost())) {
            throw new SecurityException("Host not allowed: " + uri.getHost());
        }

        // 3. Блокировка внутренних IP (127.0.0.1, 10.*, 172.16-31.*, 192.168.*)
        InetAddress addr = InetAddress.getByName(uri.getHost());
        if (addr.isLoopbackAddress() || addr.isSiteLocalAddress()
                || addr.isLinkLocalAddress()) {
            throw new SecurityException("Internal addresses blocked");
        }

        return restTemplate.getForObject(url, String.class);
    }
}
```

Ключевые меры: whitelist доменов, блокировка приватных `IP`-диапазонов, `DNS rebinding` protection (проверяйте resolved `IP`, а не hostname).

## Q14. Как защититься от `Injection`-атак помимо `SQL`?

### `OS Command Injection`

```java
// ОПАСНО:
Runtime.getRuntime().exec("ping " + userInput);

// БЕЗОПАСНО: whitelist и ProcessBuilder
public void ping(String host) {
    if (!host.matches("[a-zA-Z0-9.\\-]+")) {
        throw new IllegalArgumentException("Invalid host");
    }
    new ProcessBuilder("ping", "-c", "1", host).start();
}
```

### `LDAP Injection`

```java
// ОПАСНО: конкатенация
String filter = "(uid=" + username + ")";

// БЕЗОПАСНО: LdapQueryBuilder в Spring LDAP
LdapQuery query = LdapQueryBuilder.query()
        .where("uid").is(username);
ldapTemplate.search(query, mapper);
```

### `Log Injection`

```java
// ОПАСНО: лог-инъекция (CRLF injection для подделки записей)
log.info("User login: " + username);

// БЕЗОПАСНО: санитизация
log.info("User login: {}", username.replaceAll("[\\r\\n]", "_"));
```

Общий принцип: **никогда** не передавайте пользовательский ввод напрямую в команды, запросы или логи без валидации.

## Q15. (!) Как реализовать шифрование данных в Java?

### Симметричное шифрование (`AES`)

```java
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AesEncryption {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;

    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        byte[] encrypted = cipher.doFinal(data);

        // Prepend IV to ciphertext
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return result;
    }

    public static byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(encryptedData, GCM_IV_LENGTH, encryptedData.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        return cipher.doFinal(ciphertext);
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }
}
```

**Важно**: используйте `AES/GCM/NoPadding` (authenticated encryption) вместо устаревшего `AES/ECB` или `AES/CBC`. `GCM` обеспечивает и конфиденциальность, и целостность данных.

## Q16. В чём разница между симметричным и асимметричным шифрованием?

| Параметр | Симметричное (`AES`) | Асимметричное (`RSA`) |
|----------|---------------------|----------------------|
| Ключи | Один общий ключ | Пара: публичный + приватный |
| Скорость | Быстрое | Медленное (в 100-1000x) |
| Применение | Шифрование данных | Обмен ключами, цифровые подписи |
| Проблема | Безопасная передача ключа | Размер сообщения ограничен |

### Гибридная схема (как в `TLS`)

```mermaid
sequenceDiagram
    participant Client
    participant Server
    Client->>Server: Client Hello (поддерживаемые алгоритмы)
    Server->>Client: Server Hello + сертификат (RSA public key)
    Client->>Client: Генерация AES session key
    Client->>Server: Зашифрованный session key (RSA)
    Server->>Server: Расшифровка session key (RSA private)
    Note over Client,Server: Дальше всё шифруется AES session key
    Client->>Server: Данные (AES)
    Server->>Client: Данные (AES)
```

На практике `RSA` используется для обмена ключами, а `AES` -- для шифрования самих данных.

## Q17. Как обеспечить шифрование данных `at rest` и `in transit`?

### `Encryption in Transit`

- **`TLS 1.3`** для всех внешних коммуникаций
- **`mTLS`** между микросервисами (mutual authentication)
- Конфигурация в Spring Boot:

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-type: PKCS12
    key-store-password: ${KEYSTORE_PASSWORD}
    protocol: TLS
    enabled-protocols: TLSv1.3
```

### `Encryption at Rest`

- Шифрование БД (Transparent Data Encryption в PostgreSQL, Oracle)
- Шифрование файлов на уровне ОС или облака (`AWS KMS`, `Azure Key Vault`)
- Шифрование чувствительных полей в приложении (column-level encryption)

```java
@Entity
public class User {

    @Convert(converter = EncryptedStringConverter.class)
    private String ssn; // SSN шифруется перед сохранением в БД

    @Convert(converter = EncryptedStringConverter.class)
    private String creditCardNumber;
}

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private final EncryptionService encryptionService;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptionService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptionService.decrypt(dbData);
    }
}
```

## Q18. (!) Как реализовать валидацию ввода в `Spring Boot`?

Валидация ввода -- первая линия защиты от большинства атак. `Spring Boot` использует `Hibernate Validator` (реализация `Bean Validation`).

### Валидация DTO

```java
public record CreateUserRequest(
        @NotBlank(message = "Имя обязательно")
        @Size(min = 2, max = 50, message = "Имя: от 2 до 50 символов")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Только буквы, цифры, подчёркивание")
        String username,

        @NotBlank
        @Email(message = "Некорректный email")
        String email,

        @NotBlank
        @Size(min = 8, max = 128)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "Пароль: минимум одна строчная, заглавная и цифра")
        String password
) {}
```

### Контроллер с `@Valid`

```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    // Обработка ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
```

### Кастомный валидатор

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoHtmlValidator.class)
public @interface NoHtml {
    String message() default "HTML-теги не разрешены";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class NoHtmlValidator implements ConstraintValidator<NoHtml, String> {
    private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]+>");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        return value == null || !HTML_PATTERN.matcher(value).find();
    }
}
```

Валидация должна быть и на уровне контроллера, и на уровне сервиса -- `Defense in Depth` на уровне приложения.

## Q19. Как валидировать `RequestParams` и `PathVariables`?

Помимо `@RequestBody`, важно валидировать параметры запроса и path-переменные:

```java
@RestController
@RequestMapping("/api/products")
@Validated // обязательно на уровне класса!
public class ProductController {

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(
            @PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(
            @RequestParam @NotBlank @Size(max = 100) String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(productService.search(query, page, size));
    }
}
```

Аннотация `@Validated` на классе контроллера активирует валидацию для `@RequestParam` и `@PathVariable` (в отличие от `@Valid`, которая работает только для `@RequestBody`).

## Q20. (!) Что такое `CORS` и как настроить в `Spring`?

`CORS` (`Cross-Origin Resource Sharing`) -- механизм, позволяющий веб-странице запрашивать ресурсы с другого домена. Браузер по умолчанию блокирует кросс-доменные запросы (`Same-Origin Policy`).

### Способ 1: `@CrossOrigin` на контроллере

```java
@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "https://frontend.example.com",
             methods = {RequestMethod.GET, RequestMethod.POST},
             maxAge = 3600)
public class DataController {
    // ...
}
```

### Способ 2: Глобальная конфигурация через `WebMvcConfigurer`

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://frontend.example.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### Способ 3: Через `Spring Security` (рекомендуемый)

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://frontend.example.com"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```

**Частая ошибка на собеседовании**: забывают, что `Spring Security` имеет собственный `CORS`-фильтр, который применяется **до** `WebMvcConfigurer`. Если используете `Spring Security`, настраивайте `CORS` через `http.cors()`.

**Никогда** не используйте `allowedOrigins("*")` с `allowCredentials(true)` -- это уязвимость.

## Q21. (!) Что такое `Security Headers` и какие использовать?

`HTTP`-заголовки безопасности -- дополнительный слой защиты, настраиваемый на уровне сервера.

### Конфигурация в Spring Security

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.headers(headers -> headers
            // Принудительный HTTPS
            .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000))
            // Защита от clickjacking
            .frameOptions(frame -> frame.deny())
            // Защита от MIME sniffing
            .contentTypeOptions(Customizer.withDefaults())
            // Content Security Policy
            .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self'; " +
                            "style-src 'self' 'unsafe-inline'; img-src 'self' data:"))
            // Referrer Policy
            .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            // Permissions Policy (отключаем ненужные API браузера)
            .permissionsPolicy(permissions -> permissions
                    .policy("camera=(), microphone=(), geolocation=()"))
    );
    return http.build();
}
```

### Таблица заголовков

| Заголовок | Значение | Защита от |
|-----------|----------|-----------|
| `Strict-Transport-Security` | `max-age=31536000; includeSubDomains` | `MITM`, downgrade-атаки |
| `X-Content-Type-Options` | `nosniff` | `MIME` sniffing |
| `X-Frame-Options` | `DENY` | `Clickjacking` |
| `Content-Security-Policy` | `default-src 'self'` | `XSS`, data injection |
| `Referrer-Policy` | `strict-origin-when-cross-origin` | Утечка `URL` |
| `Permissions-Policy` | `camera=(), microphone=()` | Злоупотребление API |

## Q22. Что такое `Content Security Policy` (`CSP`)?

`CSP` -- `HTTP`-заголовок, ограничивающий источники контента, который может загружать страница. Это одна из самых мощных защит от `XSS`.

### Пример поэтапного внедрения

```
# 1. Начать с Report-Only (не блокирует, только отчёты)
Content-Security-Policy-Report-Only: default-src 'self'; report-uri /csp-report

# 2. После анализа отчётов — включить enforcement
Content-Security-Policy: default-src 'self'; script-src 'self' cdn.example.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; report-uri /csp-report
```

### Обработка `CSP`-отчётов

```java
@RestController
public class CspReportController {

    @PostMapping(value = "/csp-report", consumes = "application/csp-report")
    public ResponseEntity<Void> handleCspReport(@RequestBody String report) {
        log.warn("CSP violation: {}", report);
        return ResponseEntity.ok().build();
    }
}
```

Важные директивы: `default-src`, `script-src`, `style-src`, `img-src`, `connect-src`, `frame-ancestors` (замена `X-Frame-Options`).

## Q23. (!) Как обеспечить безопасность `REST API`?

### Многослойная защита API

```mermaid
graph TB
    Client[Клиент] --> TLS[TLS 1.3]
    TLS --> RL[Rate Limiting]
    RL --> Auth[Аутентификация<br/>JWT / OAuth 2.0]
    Auth --> AuthZ[Авторизация<br/>RBAC / ABAC]
    AuthZ --> Val[Валидация ввода<br/>Bean Validation]
    Val --> Logic[Бизнес-логика]
    Logic --> Audit[Аудит-лог]
```

### Чеклист безопасности API

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ApiSecurityConfig {

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. HTTPS обязательно
            .requiresChannel(channel -> channel.anyRequest().requiresSecure())
            // 2. CSRF не нужен для stateless API
            .csrf(csrf -> csrf.disable())
            // 3. Stateless сессии
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 4. JWT аутентификация
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            // 5. Авторизация по эндпоинтам
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            // 6. CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 7. Security headers
            .headers(headers -> headers
                    .contentTypeOptions(Customizer.withDefaults())
                    .frameOptions(frame -> frame.deny()));

        return http.build();
    }
}
```

Для `GraphQL`: ограничение глубины и сложности запросов, авторизация на уровне полей, rate limiting по стоимости запроса. Подробнее об API -- в [HTTP/REST](../api/http-rest-interview.md).

## Q24. Как реализовать `Rate Limiting`?

`Rate limiting` -- защита от `brute-force`, `DDoS` и злоупотреблений API.

### Реализация через `Bucket4j`

```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String clientIp = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(clientIp, this::createBucket);

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", "60");
            response.getWriter().write("Rate limit exceeded");
        }
    }

    private Bucket createBucket(String key) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
                .build();
    }
}
```

Для production: используйте Redis-backed rate limiter (`Resilience4j`, `Spring Cloud Gateway` rate limiter), чтобы лимиты работали на кластере.

## Q25. (!) Что такое `OWASP Top 10`?

`OWASP Top 10` (2021) -- список 10 наиболее критичных рисков безопасности веб-приложений:

| # | Категория | Пример |
|---|-----------|--------|
| A01 | `Broken Access Control` | Доступ к чужим данным через `IDOR` |
| A02 | `Cryptographic Failures` | Хранение паролей без хэширования |
| A03 | `Injection` | `SQL`, `XSS`, `Command Injection` |
| A04 | `Insecure Design` | Отсутствие threat modeling |
| A05 | `Security Misconfiguration` | Default пароли, verbose ошибки |
| A06 | `Vulnerable Components` | Устаревшие библиотеки с `CVE` |
| A07 | `Authentication Failures` | Слабые пароли, отсутствие `MFA` |
| A08 | `Data Integrity Failures` | Небезопасная десериализация |
| A09 | `Logging Failures` | Отсутствие аудит-логов |
| A10 | `SSRF` | Запрос к внутренним сервисам |

Подробный разбор каждой категории -- в [OWASP Top 10](owasp-top10-interview.md).

На собеседовании ожидают, что вы свяжете каждый риск с конкретной мерой защиты и сможете показать код.

## Q26. Что такое `Threat Modeling`?

Систематический анализ угроз при проектировании системы.

### Методология `STRIDE`

| Угроза | Описание | Мера защиты |
|--------|----------|-------------|
| `Spoofing` | Подделка личности | Аутентификация, `MFA` |
| `Tampering` | Изменение данных | Целостность, `HMAC`, подпись |
| `Repudiation` | Отказ от действий | Аудит-логи, digital signatures |
| `Information Disclosure` | Утечка информации | Шифрование, контроль доступа |
| `Denial of Service` | Отказ в обслуживании | Rate limiting, redundancy |
| `Elevation of Privilege` | Повышение привилегий | Least privilege, `RBAC` |

### Процесс

```mermaid
graph LR
    A[Определить активы] --> B[Построить DFD]
    B --> C[Применить STRIDE]
    C --> D[Оценить риски<br/>DREAD/CVSS]
    D --> E[Определить<br/>меры защиты]
    E --> F[Валидировать<br/>в CI/CD]
```

`DFD` (Data Flow Diagram) -- диаграмма потоков данных, основа для threat modeling. Проводить на ранних этапах и при существенных изменениях архитектуры.

## Q27. (!) Что такое `Secrets Management`?

Секреты (пароли, ключи API, токены, сертификаты) **никогда** не должны находиться в коде или репозитории.

### Инструменты

| Инструмент | Описание |
|-----------|----------|
| `HashiCorp Vault` | Универсальное хранилище секретов |
| `AWS Secrets Manager` | Управление секретами в AWS |
| `Azure Key Vault` | Управление секретами в Azure |
| `Spring Cloud Config` | Конфигурация с шифрованием |
| `Kubernetes Secrets` | Секреты в K8s (зашифрованные etcd) |

### Интеграция с Spring Boot

```java
// 1. Через переменные окружения (простой вариант)
// application.yml
// spring.datasource.password: ${DB_PASSWORD}

// 2. Через Spring Cloud Vault
@Configuration
public class VaultConfig {

    @Value("${spring.datasource.password}")
    private String dbPassword; // подтягивается из Vault автоматически
}
```

```yaml
# bootstrap.yml для Spring Cloud Vault
spring:
  cloud:
    vault:
      uri: https://vault.example.com:8200
      authentication: APPROLE
      app-role:
        role-id: ${VAULT_ROLE_ID}
        secret-id: ${VAULT_SECRET_ID}
      kv:
        backend: secret
        default-context: myapp
```

### Чеклист

1. Сканирование репозитория на секреты (`git-secrets`, `truffleHog`, `gitleaks`)
2. Ротация секретов по расписанию
3. Аудит доступа к секретам
4. Минимальный срок жизни для токенов
5. `.gitignore` для файлов с конфигурацией (`.env`, `application-local.yml`)

## Q28. Как обеспечить безопасность зависимостей?

Уязвимые зависимости (`OWASP A06`) -- одна из самых частых причин взломов.

### Автоматизация

```groovy
// build.gradle: OWASP Dependency Check
plugins {
    id 'org.owasp.dependencycheck' version '9.0.9'
}

dependencyCheck {
    failBuildOnCVSS = 7 // блокировать сборку при CVSS >= 7
    suppressionFile = "owasp-suppression.xml"
}
```

### Инструменты

| Инструмент | Тип | Интеграция |
|-----------|-----|-----------|
| `OWASP Dependency Check` | Gradle/Maven plugin | CI pipeline |
| `Snyk` | SaaS | GitHub, GitLab |
| `Dependabot` | GitHub native | Pull requests |
| `Renovate` | Open source | Любой VCS |
| `Trivy` | Контейнеры + зависимости | CI + Docker |

### Практики

1. Сканирование в `CI` -- блокировка merge при критичных `CVE`
2. Регулярные обновления -- автоматические PR от `Dependabot`/`Renovate`
3. `SBOM` (`Software Bill of Materials`) -- полный реестр зависимостей
4. Не использовать заброшенные библиотеки (нет релизов > 2 лет)

## Q29. Что такое `SAST` и `DAST`?

| Параметр | `SAST` | `DAST` |
|----------|--------|--------|
| Полное название | Static Application Security Testing | Dynamic Application Security Testing |
| Что анализирует | Исходный код | Работающее приложение |
| Когда | На этапе сборки | На staging/prod |
| Инструменты | `SonarQube`, `Checkmarx`, `FindSecBugs` | `OWASP ZAP`, `Burp Suite` |
| Плюсы | Раннее обнаружение, покрытие кода | Находит runtime-уязвимости |
| Минусы | Ложные срабатывания | Требует работающее окружение |

### Дополнительные типы тестирования

- **`IAST`** (`Interactive`) -- агент в runtime, меньше false positives
- **`SCA`** (`Software Composition Analysis`) -- анализ зависимостей
- **`RASP`** (`Runtime Application Self-Protection`) -- защита в runtime

Комбинируйте `SAST` + `DAST` + `SCA` для полного покрытия.

## Q30. Как проводить `Security Code Review`?

### Чеклист ревью

1. **Валидация ввода** -- все входные данные проверяются
2. **Параметризованные запросы** -- нет конкатенации `SQL`
3. **Секреты** -- нет хардкода паролей/ключей
4. **Хэширование паролей** -- `bcrypt`/`Argon2`, не `MD5`/`SHA-1`
5. **Авторизация** -- каждый эндпоинт защищён
6. **Десериализация** -- нет `ObjectInputStream` с внешними данными
7. **Логирование** -- без `PII`, паролей, токенов
8. **Зависимости** -- нет известных `CVE`
9. **Ошибки** -- не раскрывают stack trace пользователю
10. **`CORS`** -- не используется `*` с `credentials`

### Инструменты автоматизации

```groovy
// SpotBugs + FindSecBugs
plugins {
    id 'com.github.spotbugs' version '6.0.7'
}

dependencies {
    spotbugsPlugins 'com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0'
}

spotbugs {
    effort = 'max'
    reportLevel = 'low'
}
```

## Q31. Что такое `Security Testing` в `CI/CD`?

### Pipeline с security gates

```mermaid
graph LR
    Code[Код] --> SAST[SAST<br/>SonarQube]
    SAST --> SCA[SCA<br/>Dependency Check]
    SCA --> Build[Сборка]
    Build --> Image[Docker Image Scan<br/>Trivy]
    Image --> Deploy[Deploy to Staging]
    Deploy --> DAST[DAST<br/>OWASP ZAP]
    DAST --> Prod[Production]
    
    SAST -.->|Блокировка| Fail[Fail]
    SCA -.->|Critical CVE| Fail
    Image -.->|Vulnerabilities| Fail
```

### Метрики безопасности

- **Mean Time to Remediate** (MTTR) -- время до исправления уязвимости
- **Vulnerability Density** -- количество уязвимостей на 1000 строк кода
- **Security Debt** -- накопленные неисправленные уязвимости
- **False Positive Rate** -- доля ложных срабатываний

Подробнее о CI/CD -- в [Pipeline Design](../cicd/pipeline-design-interview.md).

## Q32. Как обеспечить безопасность в контейнерах?

### Best practices для `Dockerfile`

```dockerfile
# 1. Минимальный базовый образ
FROM eclipse-temurin:21-jre-alpine

# 2. Не запускать от root
RUN addgroup -S app && adduser -S app -G app
USER app

# 3. Копировать только необходимое
COPY --chown=app:app build/libs/app.jar /app/app.jar

# 4. Healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# 5. Не хранить секреты в образе
# Секреты инжектятся через env или volume
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### Чеклист

1. **Минимальный образ** -- `distroless` или `Alpine` (меньше attack surface)
2. **Non-root user** -- `USER` в `Dockerfile`
3. **Сканирование образов** -- `Trivy`, `Clair`, `Snyk Container`
4. **Подписанные образы** -- `Docker Content Trust` / `Cosign`
5. **Read-only filesystem** -- `--read-only` при запуске
6. **Ограничение ресурсов** -- `CPU`/memory limits
7. **Network Policies** -- изоляция в `Kubernetes`

Подробнее -- в [Docker](../devops/docker-interview.md) и [Kubernetes](../devops/kubernetes-interview.md).

## Q33. Что такое `Security Misconfiguration`?

Неправильная конфигурация -- одна из самых частых уязвимостей (`OWASP A05`).

### Типичные ошибки

```java
// ПЛОХО: stack trace в production
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception e) {
        return ResponseEntity.status(500).body(e.getMessage()); // утечка информации!
    }
}

// ХОРОШО: безопасная обработка ошибок
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        log.error("Internal error", e); // логируем для себя
        return ResponseEntity.status(500)
                .body(new ErrorResponse("Internal server error", "ERR-500"));
    }
}
```

### Чеклист конфигурации

- Отключить `Swagger UI` в production
- Убрать default credentials
- Отключить ненужные `Actuator` endpoints
- Не выводить stack trace пользователю
- Обновлять зависимости и базовые образы
- Использовать `IaC` для воспроизводимости конфигурации

```yaml
# application-prod.yml
spring:
  autoconfigure:
    exclude: org.springdoc.core.configuration.SpringDocConfiguration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: never
server:
  error:
    include-stacktrace: never
    include-message: never
```

## Q34. Как обеспечить безопасность файловых загрузок?

```java
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "application/pdf");
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10 MB

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file) {
        // 1. Проверка размера
        if (file.getSize() > MAX_SIZE) {
            return ResponseEntity.badRequest().body("File too large");
        }

        // 2. Проверка типа (по содержимому, не по расширению!)
        String contentType = file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().body("File type not allowed");
        }

        // 3. Проверка magic bytes
        if (!isValidMagicBytes(file)) {
            return ResponseEntity.badRequest().body("Invalid file");
        }

        // 4. Генерация случайного имени (защита от path traversal)
        String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());

        // 5. Сохранение ВНЕ webroot
        Path target = Path.of("/var/uploads", filename);
        Files.copy(file.getInputStream(), target);

        return ResponseEntity.ok(filename);
    }
}
```

Дополнительно: сканирование антивирусом (`ClamAV`), `Content-Disposition: attachment` при отдаче, re-encode изображений для удаления метаданных.

## Q35. Как обеспечить безопасность логирования?

### Что логировать

- События аутентификации (успех/неудача)
- Изменения прав доступа
- Доступ к критичным данным
- Ошибки авторизации
- Административные действия

### Что НЕ логировать

```java
// ПЛОХО:
log.info("User {} logged in with password {}", username, password);
log.info("Credit card: {}", creditCardNumber);
log.info("Token: {}", jwtToken);

// ХОРОШО:
log.info("User {} logged in from IP {}", username, request.getRemoteAddr());
log.info("Payment processed for card ending in {}", last4Digits);
log.info("Token issued for user {}", username);
```

### Маскирование в Logback

```xml
<!-- logback-spring.xml -->
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
        <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

```java
// Кастомный маскирующий layout
@Component
public class SensitiveDataMasker {

    private static final Pattern CARD_PATTERN =
            Pattern.compile("\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b");

    public static String mask(String message) {
        return CARD_PATTERN.matcher(message)
                .replaceAll("****-****-****-$0".substring(message.length() - 4));
    }
}
```

Централизованное хранение (`ELK`, `SIEM`), алерты по аномалиям, ротация и удаление по политике. Подробнее -- в [Логирование](../logging/logging-interview.md) и [Observability](../monitoring/observability-interview.md).

## Q36. Что такое `Security by Design`?

Безопасность учитывается на **всех** этапах разработки, а не добавляется в конце.

### Принципы

1. **Минимизация attack surface** -- отключить всё, что не нужно
2. **Secure defaults** -- безопасная конфигурация из коробки
3. **Least privilege** -- минимальные права
4. **Defense in depth** -- многослойная защита
5. **Fail securely** -- при ошибке -- отказ, а не доступ
6. **Don't trust inputs** -- валидация всего
7. **Separation of duties** -- разделение ответственности

### На практике

- Threat modeling при проектировании новых фич
- Security requirements в бэклоге
- Secure coding guidelines для команды
- Автоматизация проверок в CI/CD
- Security champions в каждой команде

## Q37. Что такое `Secure SDLC`?

`Secure SDLC` -- интеграция безопасности во все фазы разработки:

```mermaid
graph LR
    Req[Требования<br/>Security Requirements] --> Design[Дизайн<br/>Threat Modeling]
    Design --> Code[Код<br/>Secure Coding + SAST]
    Code --> Test[Тесты<br/>Security Tests + DAST]
    Test --> Deploy[Деплой<br/>Hardening + Scanning]
    Deploy --> Monitor[Мониторинг<br/>SIEM + Alerting]
    Monitor --> Req
```

| Фаза | Активность | Инструменты |
|------|-----------|-------------|
| Требования | Security requirements, abuse cases | Шаблоны, чеклисты |
| Дизайн | Threat modeling (`STRIDE`) | `OWASP Threat Dragon` |
| Код | Secure coding, code review | `FindSecBugs`, `SonarQube` |
| Тесты | Penetration testing, fuzzing | `OWASP ZAP`, `Burp Suite` |
| Деплой | Image scanning, config hardening | `Trivy`, `Kube-bench` |
| Мониторинг | Аудит, алерты, incident response | `SIEM`, `Prometheus` |

## Q38. (!) Как реагировать на инциденты безопасности?

### Процесс `Incident Response`

```mermaid
graph LR
    D[1. Обнаружение<br/>Алерты, логи] --> I[2. Изоляция<br/>Блокировка доступа]
    I --> Inv[3. Расследование<br/>Логи, форензика]
    Inv --> Fix[4. Исправление<br/>Патч, обновление]
    Fix --> Rec[5. Восстановление<br/>Откат, коммуникация]
    Rec --> PM[6. Постмортем<br/>Причины, действия]
```

### Практические шаги

1. **Обнаружение** -- алерты (`SIEM`), отчёты пользователей, сканеры
2. **Изоляция** -- отключение скомпрометированных учёток, блокировка `IP`, revoke токенов
3. **Расследование** -- анализ логов, трейсов, определение scope и impact
4. **Исправление** -- патч уязвимости, ротация секретов
5. **Восстановление** -- откат данных при необходимости, коммуникация с пользователями
6. **Постмортем** -- blameless, root cause analysis, action items

**На собеседовании ожидают**: знание процесса, умение приоритизировать действия, понимание бизнес-impact. Регулярные учения (`tabletop exercises`) -- признак зрелой команды.

## Q39. Как обеспечить безопасность в микросервисной архитектуре?

### Вызовы

- Увеличенная attack surface (больше сервисов, сетевых вызовов)
- Управление идентификацией между сервисами
- Консистентная политика авторизации

### Решения

```mermaid
graph TB
    Client[Клиент] --> GW[API Gateway<br/>Аутентификация, Rate Limit]
    GW --> S1[Service A]
    GW --> S2[Service B]
    S1 -->|mTLS| S2
    S1 -->|mTLS| S3[Service C]
    
    IDP[Identity Provider<br/>Keycloak / Auth0] -.->|JWT validation| GW
    IDP -.->|Token introspection| S1
```

1. **API Gateway** -- единая точка аутентификации, rate limiting
2. **`mTLS`** -- взаимная аутентификация между сервисами (service mesh: `Istio`, `Linkerd`)
3. **`JWT` propagation** -- передача токена пользователя между сервисами
4. **Centralized IAM** -- `Keycloak`, `Auth0` для единого управления идентификацией
5. **Network Policies** -- сегментация сети в `Kubernetes`

Подробнее -- в [Микросервисы](../architecture/microservices-interview.md) и [Распределённые системы](../architecture/distributed-systems-interview.md).

## Q40. Что такое `Zero Trust Architecture`?

**`Zero Trust`** -- модель безопасности, в которой ни один запрос не считается доверенным, независимо от источника ("never trust, always verify").

### Принципы

1. **Verify explicitly** -- всегда аутентифицировать и авторизовать на основе всех доступных данных
2. **Least privilege access** -- минимальные права, `JIT` (Just-in-Time) доступ
3. **Assume breach** -- проектировать так, будто сеть уже скомпрометирована

### Реализация в Java/Spring

| Слой | Мера | Инструмент |
|------|------|-----------|
| Сеть | `mTLS`, Network Policies | `Istio`, `Calico` |
| Идентификация | `MFA`, `SSO`, token validation | `Keycloak`, `Okta` |
| Авторизация | `RBAC`/`ABAC` на каждом сервисе | `Spring Security`, `OPA` |
| Данные | Encryption at rest/in transit | `Vault`, `TLS` |
| Мониторинг | Continuous verification | `SIEM`, anomaly detection |

`Zero Trust` -- не продукт, а архитектурный подход. На собеседовании важно показать понимание принципов и конкретных технических мер для их реализации.

## Q41. (!) Как интегрировать `HashiCorp Vault` для управления секретами в `Spring Boot`?

**HashiCorp Vault** — централизованное хранилище секретов с аудитом, ротацией, тонким управлением доступом. Ключевое отличие от переменных окружения: секреты не попадают в конфиг-файлы, логи и не видны через `docker inspect`.

### Зависимости

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

### Конфигурация подключения

```yaml
# bootstrap.yml (загружается ДО application.yml)
spring:
  cloud:
    vault:
      host: vault.example.com
      port: 8200
      scheme: https
      authentication: KUBERNETES   # auth через k8s ServiceAccount token
      kubernetes:
        role: my-spring-app
        kubernetes-path: kubernetes
      kv:
        enabled: true
        backend: secret
        default-context: my-app    # секреты из secret/my-app
        profiles-path: my-app      # + secret/my-app/production
```

### Использование секретов

```java
// Vault автоматически подставляет значения в @Value
@Configuration
public class DataSourceConfig {

    @Value("${db.password}")          // из Vault: secret/my-app/db.password
    private String dbPassword;

    @Value("${api.encryption-key}")   // из Vault: secret/my-app/api.encryption-key
    private String encryptionKey;

    @Bean
    DataSource dataSource(
            @Value("${db.url}") String url,
            @Value("${db.username}") String username) {
        return DataSourceBuilder.create()
            .url(url)
            .username(username)
            .password(dbPassword)
            .build();
    }
}
```

### Динамические секреты (Database Secrets Engine)

```java
// Vault генерирует временные credentials для БД
@Service
public class DynamicDbService {

    @Autowired
    private VaultTemplate vaultTemplate;

    public DataSource getTemporaryDataSource() {
        // Vault создаёт временного пользователя БД с TTL
        VaultResponse response = vaultTemplate.read("database/creds/my-role");
        String username = (String) response.getData().get("username");
        String password = (String) response.getData().get("password");

        return DataSourceBuilder.create()
            .url("jdbc:postgresql://db:5432/mydb")
            .username(username)
            .password(password)  // автоматически истекает через TTL
            .build();
    }
}
```

### Lease renewal и ротация

```yaml
spring:
  cloud:
    vault:
      config:
        lifecycle:
          enabled: true          # автоматическое продление lease
          min-renewal: 10s
          expiry-threshold: 1m   # продлевать за 1 минуту до истечения
```

### Сравнение подходов хранения секретов

| Подход | Безопасность | Аудит | Ротация | Сложность |
|--------|-------------|-------|---------|----------|
| Hardcoded в коде | Критически низкая | Нет | Вручную | Нет |
| Переменные окружения | Низкая | Нет | Вручную | Нет |
| k8s Secrets (base64) | Средняя | Ограничен | Вручную | Низкая |
| k8s Secrets + RBAC + etcd encryption | Высокая | Да | Вручную | Средняя |
| HashiCorp Vault | Очень высокая | Полный | Автоматически | Высокая |
| AWS Secrets Manager | Очень высокая | CloudTrail | Автоматически | Средняя |

## Q42. Как использовать `Kubernetes Secrets` безопасно?

`k8s Secrets` по умолчанию хранятся в `etcd` в формате `base64` (не зашифрованы). Для production необходима дополнительная конфигурация.

### Проблемы дефолтной конфигурации

```bash
# base64 — НЕ шифрование!
echo "bXktc2VjcmV0" | base64 -d   # → my-secret

# Любой с доступом к etcd читает все secrets
kubectl get secret my-db-secret -o jsonpath='{.data.password}' | base64 -d
```

### Шифрование etcd at rest

```yaml
# encryption-config.yaml для kube-apiserver
apiVersion: apiserver.config.k8s.io/v1
kind: EncryptionConfiguration
resources:
  - resources:
      - secrets
    providers:
      - aescbc:
          keys:
            - name: key1
              secret: <base64-encoded-32-byte-key>
      - identity: {}   # fallback для незашифрованных (убираем после миграции)
```

### RBAC — минимальные права на Secrets

```yaml
# Только конкретный ServiceAccount читает конкретный Secret
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: secret-reader
  namespace: production
rules:
  - apiGroups: [""]
    resources: ["secrets"]
    resourceNames: ["my-app-db-secret"]   # только конкретный secret
    verbs: ["get"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: my-app-secret-binding
subjects:
  - kind: ServiceAccount
    name: my-app
    namespace: production
roleRef:
  kind: Role
  name: secret-reader
  apiGroup: rbac.authorization.k8s.io
```

### Монтирование как файл (предпочтительнее env vars)

```yaml
# Deployment: монтируем Secret как файл, не как env var
# Причина: env vars видны через /proc/<pid>/environ, попадают в core dumps
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
        - name: my-app
          env:
            # Плохо: secret в переменной окружения
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: my-db-secret
                  key: password
          volumeMounts:
            # Лучше: secret как файл с ограниченными правами
            - name: db-secret
              mountPath: /etc/secrets
              readOnly: true
      volumes:
        - name: db-secret
          secret:
            secretName: my-db-secret
            defaultMode: 0400   # только чтение владельцем
```

### Внешние провайдеры секретов

```yaml
# External Secrets Operator (синхронизация из Vault/AWS SM в k8s Secrets)
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: my-app-secret
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: vault-backend
    kind: SecretStore
  target:
    name: my-app-k8s-secret
  data:
    - secretKey: db-password
      remoteRef:
        key: secret/my-app
        property: db.password
```

## Q43. (!) Как настроить `SAST` в `CI/CD` для Java-проекта?

**SAST** (Static Application Security Testing) — анализ исходного кода на уязвимости без запуска приложения.

### Инструменты для Java

| Инструмент | Тип | Интеграция | Что находит |
|-----------|-----|-----------|------------|
| `SpotBugs` + `Find Security Bugs` | Бесплатный | Gradle/Maven/CI | SQL injection, XSS, crypto ошибки |
| `SonarQube` / `SonarCloud` | Freemium | CI Quality Gate | Широкий спектр, исторические тренды |
| `Checkmarx` | Платный | CI, IDE plugin | Enterprise, глубокий data flow |
| `Semgrep` | Бесплатный/платный | CI, pre-commit | Кастомные правила, быстрый |
| `OWASP Dependency-Check` | Бесплатный | Gradle/Maven | CVE в зависимостях |

### Gradle конфигурация SpotBugs + Find Security Bugs

```groovy
// build.gradle
plugins {
    id 'com.github.spotbugs' version '6.0.9'
}

spotbugs {
    toolVersion = '4.8.3'
    effort = 'max'
    reportLevel = 'low'
    // Нарушения Critical/High блокируют сборку
    ignoreFailures = false
}

spotbugsMain {
    reports {
        html.required = true
        xml.required = false
    }
    // Find Security Bugs plugin
    pluginClasspath = configurations.spotbugsPlugins
}

dependencies {
    spotbugsPlugins 'com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0'
}
```

### GitHub Actions CI pipeline

```yaml
# .github/workflows/security.yml
name: Security Checks

on: [push, pull_request]

jobs:
  sast:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      # SpotBugs + Find Security Bugs
      - name: Run SpotBugs
        run: ./gradlew spotbugsMain

      # OWASP Dependency Check
      - name: OWASP Dependency Check
        run: ./gradlew dependencyCheckAnalyze
        env:
          NVD_API_KEY: ${{ secrets.NVD_API_KEY }}

      # Semgrep SAST
      - name: Semgrep
        uses: semgrep/semgrep-action@v1
        with:
          config: >-
            p/java
            p/owasp-top-ten
            p/spring-security
        env:
          SEMGREP_APP_TOKEN: ${{ secrets.SEMGREP_APP_TOKEN }}

      # SonarQube Quality Gate
      - name: SonarQube Scan
        run: ./gradlew sonarqube
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
```

### SonarQube Quality Gate конфигурация

```groovy
// build.gradle
sonarqube {
    properties {
        property "sonar.projectKey", "my-app"
        property "sonar.host.url", System.env.SONAR_HOST_URL
        property "sonar.token", System.env.SONAR_TOKEN
        // Security hotspots обязательно на review
        property "sonar.security.sources", "src/main/java"
    }
}
```

**Важно**: SAST-инструменты дают false positives. Настройте suppression только с обоснованием в комментарии:

```java
@SuppressFBWarnings(
    value = "SQL_INJECTION_JDBC",
    justification = "Query is built from whitelist values only, not user input"
)
public List<Product> findByCategory(Category category) { ... }
```

## Q44. Что такое `DAST` и как его применять?

**DAST** (Dynamic Application Security Testing) — тестирование безопасности запущенного приложения через внешние атаки. Дополняет SAST: находит уязвимости, проявляющиеся только в runtime (неправильная конфигурация, бизнес-логика).

### DAST vs SAST

| Аспект | SAST | DAST |
|--------|------|------|
| Когда | Compile time | Runtime |
| Что нужно | Исходный код | Работающее приложение |
| Находит | Код с уязвимостями | Проявленные уязвимости |
| False positives | Много | Меньше |
| False negatives | Бизнес-логика | Код без входных точек |
| Скорость | Быстрый | Медленный |

### OWASP ZAP в CI/CD

```yaml
# docker-compose.test.yml — запуск приложения + ZAP
services:
  app:
    image: my-app:${VERSION}
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: test

  zap:
    image: ghcr.io/zaproxy/zaproxy:stable
    depends_on:
      app:
        condition: service_healthy
    command: zap-baseline.py
      -t http://app:8080
      -r zap-report.html
      -I    # не падать на предупреждениях (только ошибки)
    volumes:
      - ./reports:/zap/wrk
```

```yaml
# GitHub Actions
- name: DAST with OWASP ZAP
  uses: zaproxy/action-baseline@v0.12.0
  with:
    target: 'http://localhost:8080'
    rules_file_name: '.zap/rules.tsv'
    cmd_options: '-I'
    artifact_name: 'zap-report'
```

### Типичные находки DAST

| Уязвимость | Пример | DAST-обнаружение |
|-----------|--------|-----------------|
| Открытые конечные точки | `/actuator/env` | Сканирование endpoints |
| Security headers | Нет `X-Frame-Options` | Анализ ответов |
| Небезопасные cookies | Нет `HttpOnly` | Анализ Set-Cookie |
| Reflected XSS | `<script>` в параметрах | Автоматизированный фаззинг |
| Открытые перенаправления | `redirect?url=evil.com` | Тест с внешними URL |
| Устаревший TLS | TLS 1.0/1.1 | Проверка SSL конфигурации |

### Ограничения DAST

DAST не заменяет ручной pentest: не понимает бизнес-логику, не тестирует авторизацию между пользователями (IDOR), требует running environment. Используйте совместно: SAST (в CI на каждый PR) + DAST (в staging по расписанию) + ручной pentest (перед major release).

## Q45. (!) Как настроить `TLS` и взаимную аутентификацию (`mTLS`) в `Spring Boot`?

**TLS** обеспечивает шифрование канала и аутентификацию сервера. **mTLS** дополнительно требует от клиента предъявить сертификат — используется для service-to-service аутентификации.

### Базовая настройка TLS в Spring Boot

```yaml
# application.yml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: my-app
    # Запрет устаревших протоколов
    protocol: TLS
    enabled-protocols: TLSv1.3,TLSv1.2
    # Только безопасные cipher suites
    ciphers:
      - TLS_AES_256_GCM_SHA384          # TLS 1.3
      - TLS_AES_128_GCM_SHA256          # TLS 1.3
      - TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384   # TLS 1.2
```

### Генерация self-signed сертификата (для dev/test)

```bash
# Генерация keystore с self-signed cert
keytool -genkeypair \
  -alias my-app \
  -keyalg RSA \
  -keysize 4096 \
  -storetype PKCS12 \
  -keystore keystore.p12 \
  -storepass changeit \
  -validity 365 \
  -dname "CN=localhost, OU=Dev, O=Example, L=Moscow, ST=Moscow, C=RU"
```

### Настройка mTLS (mutual TLS)

```yaml
# application.yml — mTLS: требуем клиентский сертификат
server:
  ssl:
    enabled: true
    key-store: classpath:server-keystore.p12
    key-store-password: ${SERVER_KS_PASSWORD}
    key-store-type: PKCS12
    # Truststore содержит CA, которые подписали клиентские сертификаты
    trust-store: classpath:server-truststore.p12
    trust-store-password: ${SERVER_TS_PASSWORD}
    trust-store-type: PKCS12
    # REQUIRE — mTLS обязателен, WANT — опционален, NONE — отключён
    client-auth: REQUIRE
```

```java
// Использование клиентского сертификата для аутентификации
@Configuration
@EnableWebSecurity
public class MtlsSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .x509(x509 -> x509
                .subjectPrincipalRegex("CN=(.*?)(?:,|$)")  // извлекаем CN как username
                .userDetailsService(x509UserDetailsService()))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/internal/**").authenticated()
                .anyRequest().permitAll());
        return http.build();
    }

    @Bean
    UserDetailsService x509UserDetailsService() {
        // CN сертификата → UserDetails
        return username -> User.withUsername(username)
            .password("")
            .roles("SERVICE")
            .build();
    }
}
```

### mTLS WebClient (клиентская сторона)

```java
@Bean
WebClient secureWebClient(SslBundleRegistry sslBundleRegistry) {
    // Spring Boot 3.1+ SslBundles
    SslBundle sslBundle = sslBundleRegistry.getBundle("my-client-cert");

    HttpClient httpClient = HttpClient.create()
        .secure(spec -> spec.sslContext(
            SslContextBuilder.forClient()
                .keyManager(sslBundle.getManagers().getKeyManagerFactory())
                .trustManager(sslBundle.getManagers().getTrustManagerFactory())
                .protocols("TLSv1.3", "TLSv1.2")
                .build()));

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .build();
}
```

```yaml
# Конфигурация SslBundle
spring:
  ssl:
    bundle:
      jks:
        my-client-cert:
          keystore:
            location: classpath:client-keystore.p12
            password: ${CLIENT_KS_PASSWORD}
            type: PKCS12
          truststore:
            location: classpath:client-truststore.p12
            password: ${CLIENT_TS_PASSWORD}
```

### Проверка TLS конфигурации

```bash
# Проверка поддерживаемых протоколов и cipher suites
openssl s_client -connect my-app:8443 -tls1_1   # должно отказать
openssl s_client -connect my-app:8443 -tls1_3   # должно работать

# Проверка mTLS
openssl s_client \
  -connect my-service:8443 \
  -cert client.pem -key client-key.pem \
  -CAfile ca.pem
```

### TLS в Service Mesh (Istio)

В Kubernetes с `Istio` mTLS между сервисами настраивается декларативно без изменений в приложении:

```yaml
# PeerAuthentication: mTLS STRICT для неймспейса
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
  namespace: production
spec:
  mtls:
    mode: STRICT   # все inter-service connections должны быть mTLS
```

---

## See also

- [OWASP Top 10](owasp-top10-interview.md) — детальный разбор всех 10 категорий уязвимостей
- [Паттерны аутентификации и авторизации](authentication-authorization-patterns-interview.md) — RBAC, ABAC, JWT, MFA, Zero Trust
- [OAuth 2.0](oauth2-interview.md) — авторизационные flows, PKCE, токены
- [Spring Security](../frameworks/spring/spring-security-interview.md) — конфигурация Security FilterChain, аннотации
- [Docker](../devops/docker-interview.md) — безопасность контейнеров, non-root user, image scanning
- [Микросервисы](../architecture/microservices-interview.md) — безопасность в распределённых системах, mTLS
- [Kubernetes](../devops/kubernetes-interview.md) — Network Policy, Pod Security Admission, Secrets
- [Архитектура баз данных](../databases/database-architecture-interview.md) — защита данных, шифрование at rest
- [HTTP и REST](../api/http-rest-interview.md) — TLS, security headers, CORS

- [Authentication and Authorization Patterns](authentication-authorization-patterns-interview.md)
- [JWT](jwt-interview.md)
- [mTLS (Mutual TLS)](mtls-interview.md)
- [OAuth2](oauth2-interview.md)
- [OWASP Top 10](owasp-top10-interview.md)
- [Secrets Management](secrets-management-interview.md)
- [Шпаргалка: Безопасность приложений (Application Sec](../../security/application/application-security.md) — теория
