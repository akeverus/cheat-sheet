---
title: "Вопросы на собеседовании: Spring Vault"
description: "Spring Vault для интеграции с HashiCorp Vault: VaultTemplate, dynamic secrets, transit encryption, PKI, интеграция со Spring Cloud Config"
tags:
  - interview
  - spring
  - spring-vault-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Vault"
  - "Spring Vault interview"
  - "Spring Vault собеседование"
prerequisites:
  - "[[spring-vault]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Vault`

`Spring Vault` — официальная интеграция с `HashiCorp Vault` для управления секретами. Предоставляет `VaultTemplate`, автоматическое подтягивание секретов в `@Value`, поддержку dynamic secrets и transit encryption. Спрашивается в контексте security-first архитектур.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Vault Docs](https://docs.spring.io/spring-vault/docs/current/reference/html/) — официальная документация
- [HashiCorp Vault](https://www.vaultproject.io/docs) — документация Vault
- [Baeldung: Spring Vault](https://www.baeldung.com/spring-vault) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое HashiCorp Vault и зачем его использовать?

**HashiCorp Vault** — централизованное хранилище секретов с динамической генерацией учётных данных, шифрованием как сервисом и аудит-логами.

**Проблема без Vault**:
- Пароли БД в `application.yml` или env vars → утечка при компрометации конфига.
- Ротация паролей = обновление конфигов всех сервисов.
- Нет аудита кто и когда получил секрет.

**Решение через Vault**:
- Приложение получает токен → запрашивает секреты у Vault → Vault выдаёт (или генерирует динамически).
- Секреты автоматически ротируются.
- Полный аудит всех обращений.

**Spring Vault** — Spring-интеграция для работы с Vault API.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q2. Как настроить Spring Vault? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```xml
<dependency>
    <groupId>org.springframework.vault</groupId>
    <artifactId>spring-vault-core</artifactId>
</dependency>
<!-- или для Spring Boot auto-configuration -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

```yaml
# bootstrap.yml (Spring Cloud Vault)
spring:
  cloud:
    vault:
      host: vault.example.com
      port: 8200
      scheme: https
      authentication: APPROLE
      app-role:
        role-id: ${VAULT_ROLE_ID}
        secret-id: ${VAULT_SECRET_ID}
      kv:
        enabled: true
        backend: secret
        application-name: myapp
```

```java
// Программная конфигурация
@Configuration
public class VaultConfig extends AbstractVaultConfiguration {

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.example.com", 8200);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder()
            .roleId(AppRoleAuthenticationOptions.RoleId.provided(roleId))
            .secretId(AppRoleAuthenticationOptions.SecretId.provided(secretId))
            .build();
        return new AppRoleAuthentication(options, restOperations());
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q3. Как читать секреты из Vault? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
// 1. Через VaultTemplate (прямое чтение)
@Service
@RequiredArgsConstructor
public class SecretService {
    private final VaultTemplate vaultTemplate;

    public String getDatabasePassword() {
        VaultResponseSupport<Map<String, Object>> response =
            vaultTemplate.read("secret/myapp/db");
        return (String) response.getData().get("password");
    }
}
```

```java
// 2. Через @Value при использовании Spring Cloud Vault
@Component
public class DatabaseConfig {

    @Value("${db.password}")  // читается из Vault secret/myapp/db.password
    private String dbPassword;

    @Value("${db.username}")
    private String dbUsername;
}
```

```java
// 3. Через @VaultPropertySource
@Configuration
@VaultPropertySource("secret/myapp/config")
public class AppConfig {

    @Value("${api.key}")
    private String apiKey;
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q4. Что такое Dynamic Secrets и как их использовать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Dynamic Secrets** — учётные данные, генерируемые Vault on-demand с ограниченным временем жизни (TTL). После истечения TTL Vault автоматически отзывает их.

```java
// Dynamic PostgreSQL credentials
@Service
@RequiredArgsConstructor
public class DynamicDbService {
    private final VaultTemplate vaultTemplate;

    public DataSource createDataSource() {
        VaultResponseSupport<Map<String, Object>> lease =
            vaultTemplate.read("database/creds/my-role");

        String username = (String) lease.getData().get("username");
        String password = (String) lease.getData().get("password");
        String leaseId = lease.getLeaseId();
        Duration leaseDuration = lease.getLeaseDuration();

        log.info("Got dynamic credentials, TTL: {}", leaseDuration);
        // username = vault-token-abc123, TTL = 1 hour
        // Vault автоматически удалит этого пользователя из PostgreSQL
    }
}
```

```yaml
# В Vault настройки role:
# vault write database/roles/my-role \
#   db_name=my-postgresql-database \
#   creation_statements="CREATE ROLE \"{{name}}\" INHERIT LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}';" \
#   default_ttl="1h" \
#   max_ttl="24h"
```

Преимущество: даже если credentials утекут, они истекут автоматически.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Что такое VaultLeaseContainer и зачем он нужен? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`VaultLeaseContainer` — компонент Spring Vault для управления lease (аренда секрета). Автоматически продлевает lease до истечения TTL.

```java
@Configuration
public class VaultLeaseConfig {

    @Bean
    public VaultLeaseContainer leaseContainer(VaultOperations vaultOperations,
                                               TaskScheduler scheduler) {
        VaultLeaseContainer container = new VaultLeaseContainer(vaultOperations, scheduler);
        container.setExpiryThresholdPercentage(25);  // продлевать за 25% до истечения
        return container;
    }
}

@Service
@RequiredArgsConstructor
public class DatabaseCredentialService {
    private final VaultLeaseContainer leaseContainer;

    @PostConstruct
    public void requestCredentials() {
        leaseContainer.requestRotatingSecret("database/creds/my-role",
            credentials -> {
                // вызывается при получении или ротации
                reconfigureDataSource(
                    (String) credentials.getData().get("username"),
                    (String) credentials.getData().get("password")
                );
            }
        );
    }
}
```

Без `VaultLeaseContainer` credentials истекут и приложение упадёт — нужно явно продлевать или ротировать.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Как работает Transit Secrets Engine (шифрование как сервис)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Transit Engine позволяет зашифровать/расшифровать данные без необходимости управлять ключами в приложении.

```java
@Service
@RequiredArgsConstructor
public class EncryptionService {
    private final VaultTemplate vaultTemplate;

    public String encrypt(String plaintext) {
        TransitOperations transit = vaultTemplate.opsForTransit();
        Plaintext input = Plaintext.of(plaintext);
        Ciphertext result = transit.encrypt("my-key", input);
        return result.getCiphertext();  // vault:v1:abc123...
    }

    public String decrypt(String ciphertext) {
        TransitOperations transit = vaultTemplate.opsForTransit();
        Ciphertext input = Ciphertext.of(ciphertext);
        Plaintext result = transit.decrypt("my-key", input);
        return result.asString();
    }

    // Ротация ключа (старые данные остаются расшифровываемыми)
    public void rotateKey() {
        vaultTemplate.opsForTransit().rotate("my-key");
    }
}
```

Зашифрованный текст имеет вид `vault:v1:...` — версия ключа включена в шифротекст, Vault знает каким ключом расшифровывать.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Какие методы аутентификации поддерживает Spring Vault? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Метод | Применение | Описание |
|-------|-----------|----------|
| `TOKEN` | Development | Прямой статичный токен |
| `APPROLE` | Production (CI/CD, microservices) | Role ID + Secret ID |
| `KUBERNETES` | Kubernetes pods | Service Account JWT |
| `AWS` | EC2/Lambda | IAM роль или EC2 instance identity |
| `GCP` | GCP | Service Account или GCE metadata |
| `TLS` | On-premise | Клиентский TLS-сертификат |
| `LDAP/AD` | Enterprise | Корпоративная директория |

```yaml
# Kubernetes аутентификация
spring:
  cloud:
    vault:
      authentication: KUBERNETES
      kubernetes:
        role: myapp
        kubernetes-path: kubernetes
        service-account-token-file: /var/run/secrets/kubernetes.io/serviceaccount/token
```

**AppRole** — рекомендован для production microservices: Role ID зашит в образ, Secret ID вводится через CI/CD.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Как использовать Vault PKI для динамических TLS-сертификатов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
@Service
@RequiredArgsConstructor
public class CertificateService {
    private final VaultTemplate vaultTemplate;

    public VaultCertificateResponse generateCertificate(String commonName) {
        VaultCertificateRequest request = VaultCertificateRequest.builder()
            .commonName(commonName)
            .altNames(List.of("myapp.example.com"))
            .ttl(Duration.ofHours(24))
            .build();

        return vaultTemplate.opsForPki().issueCertificate("pki/issue/myapp-role", request);
        // Возвращает: certificate, private_key, issuing_ca, serial_number
    }
}
```

Vault PKI используется для:
- Взаимной TLS аутентификации (mTLS) между сервисами.
- Автоматического обновления сертификатов без DevOps-участия.
- Краткосрочные cert (часы/дни) вместо годовых.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Как Spring Cloud Vault интегрируется с Spring Boot PropertySource? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```yaml
# bootstrap.yml — загружается ДО application.yml
spring:
  cloud:
    vault:
      kv:
        enabled: true
        backend: secret
        profile-separator: '/'
        default-context: application
        application-name: myapp
```

```text
Порядок загрузки секретов из Vault:
1. secret/myapp/production  (profile-specific)
2. secret/myapp             (application-specific)
3. secret/application/production
4. secret/application       (default context)
```

Более специфичные секреты имеют более высокий приоритет и переопределяют менее специфичные.

```java
// Обновление секретов без рестарта (Spring Cloud Config)
@RefreshScope
@Component
public class ApiConfig {
    @Value("${external.api.key}")
    private String apiKey;
}
// POST /actuator/refresh → перечитывает секреты из Vault
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Как тестировать приложение с Spring Vault? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
// 1. VaultDevModeContainer (встроенный dev сервер через Testcontainers)
@SpringBootTest
@Testcontainers
class VaultIntegrationTest {

    @Container
    static VaultContainer<?> vault = new VaultContainer<>("hashicorp/vault:latest")
        .withVaultToken("test-token")
        .withSecretInVault("secret/myapp/db",
            "password=test-password",
            "username=test-user");

    @DynamicPropertySource
    static void vaultProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.vault.host", vault::getHost);
        registry.add("spring.cloud.vault.port", vault::getFirstMappedPort);
        registry.add("spring.cloud.vault.token", () -> "test-token");
    }
}
```

```java
// 2. MockVaultTemplate для unit тестов
@ExtendWith(MockitoExtension.class)
class SecretServiceTest {

    @Mock
    private VaultTemplate vaultTemplate;

    @InjectMocks
    private SecretService secretService;

    @Test
    void shouldReadDatabasePassword() {
        VaultResponseSupport<Map<String, Object>> mockResponse =
            mock(VaultResponseSupport.class);
        when(mockResponse.getData()).thenReturn(Map.of("password", "secret123"));
        when(vaultTemplate.read("secret/myapp/db")).thenReturn(mockResponse);

        String password = secretService.getDatabasePassword();
        assertThat(password).isEqualTo("secret123");
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Как реализовать автоматическую ротацию секретов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
@Component
@RequiredArgsConstructor
public class SecretRotationManager {
    private final VaultTemplate vault;
    private final DataSource dataSource;

    // Ротация каждые 30 минут через Spring Scheduling
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void rotateDatabaseCredentials() {
        try {
            VaultResponseSupport<Map<String, Object>> response =
                vault.read("database/creds/my-role");

            String newUsername = (String) response.getData().get("username");
            String newPassword = (String) response.getData().get("password");

            // Обновление connection pool без рестарта
            ((HikariDataSource) dataSource).setUsername(newUsername);
            ((HikariDataSource) dataSource).setPassword(newPassword);
            ((HikariDataSource) dataSource).getHikariPoolMXBean().softEvictConnections();

            log.info("DB credentials rotated successfully");
        } catch (Exception e) {
            log.error("Failed to rotate credentials", e);
        }
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Что такое Vault Agent и зачем он нужен? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Vault Agent** — sidecar-процесс рядом с приложением, который:
1. Аутентифицируется в Vault.
2. Получает секреты и записывает их в файлы.
3. Автоматически обновляет файлы при ротации.

```yaml
# Vault Agent Sidecar в Kubernetes (аннотации)
annotations:
  vault.hashicorp.com/agent-inject: "true"
  vault.hashicorp.com/role: "myapp"
  vault.hashicorp.com/agent-inject-secret-config: "secret/myapp/db"
  vault.hashicorp.com/agent-inject-template-config: |
    {{- with secret "secret/myapp/db" -}}
    spring.datasource.username={{ .Data.data.username }}
    spring.datasource.password={{ .Data.data.password }}
    {{- end -}}
```

Vault Agent монтирует файл `/vault/secrets/config` в pod. Spring Boot читает его через `spring.config.location`.

**Преимущество**: приложение не знает о Vault — просто читает файл конфигурации.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Какова разница между KV v1 и KV v2? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Критерий | KV v1 | KV v2 |
|----------|-------|-------|
| Версионирование | Нет | Да (последние N версий) |
| Путь | `secret/myapp` | `secret/data/myapp` |
| Мягкое удаление | Нет | Да (recover возможен) |
| Metadata | Нет | Да (creation time, version) |
| Spring Cloud Vault | По умолчанию | `kv.version: 2` |

```yaml
spring:
  cloud:
    vault:
      kv:
        backend: secret
        version: 2  # использовать KV v2
```

В KV v2 секреты хранятся в `data/` подпути: `secret/data/myapp/config`. Spring Cloud Vault автоматически добавляет префикс.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как обрабатывать ошибки при недоступности Vault? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
@Configuration
public class VaultFallbackConfig {

    @Bean
    @ConditionalOnProperty(name = "vault.fallback.enabled", havingValue = "true")
    public VaultOperations fallbackVaultOperations() {
        // In-memory fallback с pre-loaded secrets для dev/test
        return new InMemoryVaultOperations();
    }
}

// Circuit Breaker для Vault
@Service
@RequiredArgsConstructor
public class ResilientSecretService {
    private final VaultTemplate vaultTemplate;
    private final CircuitBreaker circuitBreaker;

    public String getSecret(String path) {
        return circuitBreaker.executeSupplier(() ->
            (String) vaultTemplate.read(path).getData().get("value"),
            throwable -> {
                log.error("Vault unavailable, using cached value", throwable);
                return secretCache.getOrDefault(path, "");
            }
        );
    }
}
```

**Лучшая практика**: кэшировать последние успешно полученные секреты. При недоступности Vault использовать кэш с оповещением по алертингу.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Какие best practices при работе с Spring Vault? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

1. **Никогда не логировать секреты** — даже в DEBUG уровне.

2. **Использовать AppRole, а не TOKEN в production** — токены сложнее ротировать.

3. **Устанавливать минимальный TTL** — динамические credentials с коротким TTL снижают риск утечки.

4. **Включить аудит-логирование Vault** — `vault audit enable file file_path=/var/log/vault_audit.log`.

5. **Отдельные роли для разных сред** — prod-myapp-role, staging-myapp-role с разными политиками.

6. **Graceful rotation** — при ротации обновлять пул соединений мягко (`softEvictConnections`), не закрывать резко.

7. **Health check Vault** — `/actuator/health` Spring Vault добавляет индикатор доступности Vault.

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Secrets Management](../../security/secrets-management-interview.md) — общие вопросы по управлению секретами ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.
- [Spring Security](spring-security-interview.md) — безопасность приложений
- [Kubernetes](../../devops/kubernetes-interview.md) — Vault + K8s интеграция через Service Account JWT
- [Spring Cloud](spring-cloud-interview.md) — Spring Cloud Config + Vault backend
- [Spring Boot](spring-boot-interview.md) — auto-configuration для Vault
- [HashiCorp Vault](../../devops/vault-interview.md) — основы Vault (policies, secret engines)
- [Application Security](../../security/application-security-interview.md) — общие паттерны безопасности
- [mTLS](../../security/mtls-interview.md) — взаимная TLS аутентификация
- [TLS/SSL](../../security/tls-ssl-interview.md) — управление сертификатами через Vault PKI
- [OAuth2](../../security/oauth2-interview.md) — Vault как источник OAuth2 client credentials
