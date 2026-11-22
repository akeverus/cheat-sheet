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
updated: "2026-05-15"
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

**HashiCorp Vault** — централизованное хранилище секретов, которое не просто хранит пароли и ключи, а ещё умеет генерировать учётные данные на лету, шифровать данные как сервис и вести аудит каждого обращения. Идея: секреты живут в одном защищённом месте, а не размазаны по конфигам десятков сервисов.

**Проблема без Vault** — секреты лежат прямо в конфигурации:
- Пароли БД в `application.yml` или env vars → при утечке конфига (Git, дамп образа, лог) утекают и все секреты.
- Ротация пароля превращается в перевыкатку конфигов всех сервисов, которые им пользуются.
- Никто не знает, кто и когда читал секрет — нет аудита, нет возможности расследовать инцидент.

**Как Vault это решает**:
- Приложение аутентифицируется и получает токен → по токену запрашивает секрет → Vault отдаёт статичный секрет или **генерирует динамический** под конкретный запрос.
- Секреты ротируются централизованно: меняешь в одном месте, сервисы подхватывают новые значения.
- Каждое обращение пишется в аудит-лог — видно кто, когда и какой секрет получил.

**Spring Vault** — это Spring-интеграция, скрывающая работу с HTTP API Vault за привычными абстракциями (`VaultTemplate`, `@Value`, property source), чтобы приложению не приходилось руками дёргать REST.

## Q2. Как настроить Spring Vault?

Настройка сводится к трём шагам: подключить зависимость, указать адрес Vault и способ аутентификации. Есть два пути — декларативный через `spring-cloud-starter-vault-config` (секреты сами подтягиваются в `Environment` ещё на старте) и программный через `AbstractVaultConfiguration` (полный контроль над эндпоинтом и аутентификацией в коде).

**Зависимость** — `spring-vault-core` даёт только `VaultTemplate` для ручной работы, а cloud-стартер добавляет авто-конфигурацию и интеграцию с property source:

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

Конфигурация кладётся в `bootstrap.yml`, потому что Vault должен инициализироваться **до** `application.yml` — иначе секреты не успеют попасть в `Environment` к моменту создания бинов.

**Важно про версии**: bootstrap-фаза deprecated и выключена по умолчанию начиная со Spring Cloud 2020.x — `bootstrap.yml` работает только при подключении легаси-стартера `spring-cloud-starter-bootstrap`. Современный стандарт — строка `spring.config.import: vault://` прямо в `application.yml`: механизм config data импортирует секреты на старте без отдельной bootstrap-фазы, а остальные `spring.cloud.vault.*` свойства задаются там же.

Программный вариант нужен, когда декларативной настройки не хватает (нестандартный эндпоинт, кастомная аутентификация). Наследуемся от `AbstractVaultConfiguration` и переопределяем две точки — адрес Vault и способ входа:

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

## Q3. Как читать секреты из Vault?

Есть три способа, и выбор зависит от того, нужен ли контроль над процессом чтения:

- **`VaultTemplate`** — прямое чтение по пути в рантайме. Подходит, когда секрет нужен динамически (например, dynamic credentials), и ты хочешь видеть lease, TTL, метаданные ответа.
- **`@Value` + Spring Cloud Vault** — самый прозрачный вариант: секреты подтянуты в `Environment` на старте, и `@Value` работает с ними как с обычными property. Приложение даже не «знает», что значение пришло из Vault.
- **`@VaultPropertySource`** — добавляет конкретный путь Vault как источник свойств на уровне `@Configuration`, без полноценного Spring Cloud.

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

## Q4. Что такое Dynamic Secrets и как их использовать?

В Vault настроена роль PostgreSQL `my-role` с TTL аренды по умолчанию один час. Что выдаёт этот вызов и что происходит после истечения аренды?

```java
VaultResponseSupport<Map<String, Object>> response =
    vaultTemplate.read("database/creds/my-role");
```

**Разбор.** **Dynamic Secrets** — учётные данные, которые Vault создаёт по запросу и выдаёт на ограниченный срок. Для этого сценария каждый запрос создаёт отдельную пару для временного пользователя PostgreSQL.

**Как это работает на примере PostgreSQL**: Vault выполняет SQL создания роли и возвращает пару `username` / `password`, идентификатор аренды и её длительность. При истечении аренды Vault запускает отзыв. Будет ли пользователь удалён, зависит от настроенных команд отзыва и результата их выполнения в PostgreSQL.

```java
// Dynamic PostgreSQL credentials
@Service
@RequiredArgsConstructor
public class DynamicDbService {
    private final VaultTemplate vaultTemplate;

    public void readCredentials() {
        VaultResponseSupport<Map<String, Object>> lease =
            vaultTemplate.read("database/creds/my-role");

        String username = (String) lease.getData().get("username");
        String password = (String) lease.getData().get("password");
        String leaseId = lease.getLeaseId();
        long leaseSeconds = lease.getLeaseDuration();  // секунды, тип long

        log.info("Got dynamic credentials, TTL: {}s", leaseSeconds);
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

**Зачем это нужно**: радиус поражения при утечке минимален. Отдельная роль позволяет связать действия в журналах БД с конкретной выдачей. В приведённой настройке `VALID UNTIL` ограничивает новые подключения по паролю независимо от успешности удаления роли. Этот срок сам по себе не завершает существующие подключения.

## Q5. Что такое SecretLeaseContainer и зачем он нужен?

Приложение получает учётные данные БД с ограниченным TTL. Пул нужно настроить при первой выдаче и обновлять после получения новой пары. Как связать жизненный цикл секрета с обновлением пула?

```text
Путь: database/creds/my-role
TTL аренды при выдаче: 1 час
Получатель пары: пул соединений с PostgreSQL
```

**Разбор.** `SecretLeaseContainer` — компонент Spring Vault, который отслеживает аренду секрета и продлевает её, когда это поддерживается. Он также может запросить новую пару после истечения аренды.

Порог `expiryThreshold` участвует в расчёте момента продления и определении истечения аренды. Режим `RequestedSecret.rotating` запрашивает новый секрет после истечения. Для выделенного контейнера этого примера зарегистрируйте слушатель до добавления запроса секрета, чтобы получить и первоначальное событие.

```java
@Configuration
public class VaultLeaseConfig {

    @Bean
    public SecretLeaseContainer leaseContainer(VaultOperations vaultOperations,
                                               TaskScheduler scheduler) {
        SecretLeaseContainer container = new SecretLeaseContainer(vaultOperations, scheduler);
        container.setExpiryThreshold(Duration.ofMinutes(1));
        return container;
    }
}

@Service
@RequiredArgsConstructor
public class DatabaseCredentialService {
    private final SecretLeaseContainer leaseContainer;

    @PostConstruct
    public void requestCredentials() {
        leaseContainer.addLeaseListener(event -> {
            if (event instanceof SecretLeaseCreatedEvent created) {
                // вызывается при получении или ротации
                Map<String, Object> secrets = created.getSecrets();
                reconfigureDataSource(
                    (String) secrets.get("username"),
                    (String) secrets.get("password")
                );
            }
        });
        leaseContainer.addRequestedSecret(RequestedSecret.rotating("database/creds/my-role"));
    }
}
```

**Суть**: слушатель из `addLeaseListener` получает `SecretLeaseCreatedEvent` и при первом получении секрета, и при каждой ротации. Обработчик применяет новую пару к пулу. Ошибки выдачи или продления и переход уже открытых соединений требуют отдельной обработки.

## Q6. Как работает Transit Secrets Engine (шифрование как сервис)?

Ключ `my-key` уже существует. Как повлияет ротация на попытку расшифровать ранее полученный шифротекст?

```java
VaultTransitOperations transit = vaultTemplate.opsForTransit();
Ciphertext saved = transit.encrypt("my-key", Plaintext.of("example"));
transit.rotate("my-key");
Plaintext restored = transit.decrypt("my-key", saved);
```

**Разбор.** Transit Engine — это «шифрование как сервис»: приложение отправляет данные в Vault, получает шифротекст, а для обычных операций шифрования и расшифровки приложение не получает ключ. Доступ к операции расшифровки регулируется политикой Vault. Vault при этом ничего не хранит из переданных данных, только выполняет криптооперацию.

```java
@Service
@RequiredArgsConstructor
public class EncryptionService {
    private final VaultTemplate vaultTemplate;

    public String encrypt(String plaintext) {
        VaultTransitOperations transit = vaultTemplate.opsForTransit();
        Plaintext input = Plaintext.of(plaintext);
        Ciphertext result = transit.encrypt("my-key", input);
        return result.getCiphertext();  // vault:v1:abc123...
    }

    public String decrypt(String ciphertext) {
        VaultTransitOperations transit = vaultTemplate.opsForTransit();
        Ciphertext input = Ciphertext.of(ciphertext);
        Plaintext result = transit.decrypt("my-key", input);
        return result.asString();
    }

    public void rotateKey() {
        vaultTemplate.opsForTransit().rotate("my-key");
    }
}
```

**Зачем версия ключа в шифротексте**: префикс `vault:v1:...` хранит версию ключа прямо в шифротексте. Благодаря этому ротация (`rotate`) безопасна — новые данные шифруются ключом `v2`, но старые `vault:v1:...` по-прежнему расшифровываются, потому что Vault видит версию и берёт нужный ключ. Это работает, пока версия ключа сохранена и разрешена для расшифровки, в частности пока она не ниже `min_decryption_version`. Если старую версию планируют вывести из использования, шифротекст можно заранее обновить через `rewrap`.

## Q7. Какие методы аутентификации поддерживает Spring Vault?

Главный вопрос аутентификации в Vault — «курица и яйцо»: чтобы получить секреты, нужен токен, но токен — тоже секрет. Каждый метод решает эту начальную проблему доверия по-своему: где-то опираемся на платформу (Kubernetes Service Account, IAM-роль AWS), где-то на разделение секрета между сборкой и рантаймом (AppRole), где-то на TLS-сертификат.

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

**Почему AppRole для production microservices**: секрет делится на две части. Role ID (публичная, как логин) зашивается в образ, а Secret ID (приватная, как пароль) выдаётся в рантайме через CI/CD или оркестратор и живёт коротко. Даже если кто-то достанет образ, одного Role ID для входа недостаточно.

## Q8. Как использовать Vault PKI для динамических TLS-сертификатов?

Vault PKI превращает Vault в собственный центр сертификации (CA): по запросу он выпускает короткоживущий TLS-сертификат с приватным ключом, не привлекая людей и внешний CA. Приложение указывает common name, SAN-имена и TTL — и сразу получает готовую пару cert + private key:

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

        return vaultTemplate.opsForPki().issueCertificate("myapp-role", request);
    }
}
```

**Разбор.** `opsForPki()` использует точку монтирования `pki` по умолчанию. Вызов обращается к `POST /v1/pki/issue/myapp-role`. В данных ответа Vault есть `certificate`, `private_key`, `issuing_ca` и `serial_number`.

**Где это применяют**:
- **mTLS между сервисами** — каждый сервис получает свой сертификат от общего CA, и они аутентифицируют друг друга по нему.
- **Автоматическое обновление** — приложение перевыпускает сертификат само, без тикета в DevOps и ручной замены.
- **Короткий TTL вместо годовых сертификатов** — сертификат на часы/дни. Это снижает риск (украденный cert быстро протухает) и убирает боль с отзывом: не успел истечь срок — выпустишь новый, а старый умрёт сам.

## Q9. Как Spring Cloud Vault интегрируется с Spring Boot PropertySource?

Spring Cloud Vault подключает Vault как ещё один `PropertySource`: секреты загружаются на старте и становятся обычными property, доступными через `@Value` и `Environment`. Приложение работает с ними так же, как с любым свойством из `application.yml` — разницы в коде нет.

Пример ниже использует `bootstrap.yml`, но помни: с Spring Cloud 2020.x bootstrap-фаза deprecated (включается только легаси-стартером `spring-cloud-starter-bootstrap`). В современных проектах тот же результат даёт `spring.config.import: vault://` в обычном `application.yml` — Vault подключается как источник config data, а порядок приоритетов путей ниже остаётся тем же.

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

**Логика приоритетов**: чем специфичнее путь, тем выше приоритет. Сначала Vault ищет секрет в самом узком контексте (`secret/myapp/production` — конкретное приложение в конкретном профиле), затем расширяет до приложения вообще и, наконец, до общего `application`. Это даёт удобную каскадную конфигурацию: общие значения кладёшь в `secret/application`, а переопределения для конкретного сервиса или профиля — в более узкие пути.

Чтобы обновить секреты без рестарта, помечаем бин `@RefreshScope` — после `POST /actuator/refresh` он пересоздаётся с перечитанными из Vault значениями:

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

## Q10. Как тестировать приложение с Spring Vault?

Два уровня тестов под две задачи:

- **Integration-тест на Testcontainers** — поднимает настоящий Vault в Docker и проверяет всю цепочку: аутентификацию, чтение секретов, интеграцию с `Environment`. `@DynamicPropertySource` подставляет host/port контейнера в конфиг до старта контекста.
- **Unit-тест с мокнутым `VaultTemplate`** — для проверки логики сервиса без сети и контейнеров. Быстрый, изолированный: мокаешь ответ Vault и проверяешь, что сервис правильно достаёт из него поле.

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

## Q11. Как реализовать автоматическую ротацию секретов?

HikariCP подключается через JDBC `DataSource`. В пуле есть свободные и занятые соединения. Vault выдал новую пару. Новые соединения должны использовать её, а занятые должны завершить текущую работу. Как обновить пул?

```text
Источник новой пары: database/creds/my-role
Состояние пула: есть свободные и занятые соединения
Требование: дать занятым соединениям завершить работу
```

**Разбор.** Ротация по расписанию — альтернатива `SecretLeaseContainer` из Q5, когда нужен полный контроль над моментом и логикой обновления. По расписанию (`@Scheduled`) сервис запрашивает у Vault новую пару credentials и обновляет ими пул соединений.

**Ключевой момент** — `softEvictConnections()`: новые credentials прописываются в `HikariDataSource`, но активные соединения не рвутся резко. Hikari помечает старые соединения на «мягкое» закрытие — они дорабатывают текущие операции и заменяются новыми с обновлёнными учётными данными. Мягкое вытеснение управляет соединениями внутри пула. Срок старой пары и момент её отзыва определяет Vault, поэтому завершение работы нужно согласовать с действием старой аренды и настройками БД. В примере используется версия HikariCP с `HikariConfigMXBean.setCredentials`, которая обновляет пару вместе. Изменение применяется при создании новых соединений через JDBC `DataSource`.

```java
@Component
@RequiredArgsConstructor
public class SecretRotationManager {
    private final VaultTemplate vault;
    private final HikariDataSource dataSource;

    // Ротация каждые 30 минут через Spring Scheduling
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void rotateDatabaseCredentials() {
        try {
            VaultResponseSupport<Map<String, Object>> response =
                vault.read("database/creds/my-role");

            String newUsername = (String) response.getData().get("username");
            String newPassword = (String) response.getData().get("password");

            // Обновление connection pool без рестарта
            dataSource.getHikariConfigMXBean()
                .setCredentials(Credentials.of(newUsername, newPassword));
            dataSource.getHikariPoolMXBean().softEvictConnections();

            log.info("DB credentials rotated successfully");
        } catch (Exception e) {
            log.error("Failed to rotate credentials", e);
        }
    }
}
```

## Q12. Что такое Vault Agent и зачем он нужен?

**Vault Agent** — sidecar-процесс, который берёт на себя всю работу с Vault, чтобы приложению не пришлось знать про Vault вообще. Он:
1. Аутентифицируется в Vault (auto-auth) — приложение освобождено от логики получения токена.
2. Получает секреты и рендерит их в файлы по шаблону.
3. Сам обновляет файлы при ротации секретов.

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

Vault Agent монтирует отрендеренный файл `/vault/secrets/config` в pod, а Spring Boot подхватывает его через `spring.config.location` как обычный property-файл.

**Когда выбирать этот подход**: приложению не нужны ни клиент Vault, ни логика аутентификации — оно просто читает файл конфигурации. Вся сложность вынесена в sidecar, поэтому подход хорош для legacy-приложений и языков без удобной библиотеки Vault.

## Q13. Какова разница между KV v1 и KV v2?

Коротко: **KV v2 добавляет версионирование и метаданные**, KV v1 — это простой key-value без истории. v2 хранит несколько последних версий каждого секрета, поддерживает мягкое удаление (с возможностью восстановить) и пишет метаданные (время создания, номер версии). Платой за это идёт другой формат пути — данные лежат под `data/`.

| Критерий | KV v1 | KV v2 |
|----------|-------|-------|
| Версионирование | Нет | Да (последние N версий) |
| Путь | `secret/myapp` | `secret/data/myapp` |
| Мягкое удаление | Нет | Да (recover возможен) |
| Metadata | Нет | Да (creation time, version) |
| Spring Cloud Vault | — | **дефолт**: `backend-version: 2` (deprecated — версия определяется автоматически) |

```yaml
spring:
  cloud:
    vault:
      kv:
        backend: secret
        backend-version: 2  # это и так дефолт; свойство deprecated — версия определяется при чтении
```

**Подводный камень с путём**: в KV v2 физический путь содержит `data/` (`secret/data/myapp/config`), хотя логически ты работаешь с `secret/myapp/config`. Spring Cloud Vault сам подставляет префикс `data/` при указании `version: 2`, поэтому в конфиге пишешь логический путь. А вот при ручных запросах через `VaultTemplate` к KV v2 префикс нужно учитывать самому — частый источник «секрет не читается, хотя он там есть».

## Q14. Как обрабатывать ошибки при недоступности Vault?

Сервис читает секрет из Vault. Сейчас обращение завершилось временным сетевым сбоем. Как обработать каждый случай?

```text
A: в памяти есть последнее значение, срок его использования ещё не истёк.
B: пригодного значения в памяти нет.
```

**Разбор.** При временном сбое можно использовать последнее успешно прочитанное значение, если оно ещё пригодно и политика приложения допускает такое использование. Без пригодного секрета зависимая операция завершается явной ошибкой. Для обязательного при старте секрета сервис может оставаться неготовым. Пустая строка скрывает причину и передаёт потребителю некорректные учётные данные.

Circuit Breaker ограничивает обращения при повторяющихся временных сбоях. В примере адаптер `vaultReader` различает временную недоступность, отсутствие секрета и отказ в доступе. Последние два исхода передаются вызывающему коду напрямую и исключены из расчёта Circuit Breaker. Кэш хранит значение в памяти с установленной границей допустимого использования. Для арендованного секрета эта граница не должна выходить за срок аренды.

```java
public String getSecret(String path) {
    Supplier<CachedSecret> readFromVault = CircuitBreaker.decorateSupplier(
        circuitBreaker, () -> vaultReader.read(path));

    try {
        CachedSecret fresh = readFromVault.get();
        secretCache.put(path, fresh);
        return fresh.value();
    } catch (VaultTemporarilyUnavailableException | CallNotPermittedException failure) {
        CachedSecret cached = secretCache.get(path);
        if (cached != null && cached.usableUntil().isAfter(clock.instant())) {
            return cached.value();
        }
        throw new SecretUnavailableException(path, failure);
    }
}
```

Отслеживайте временные сбои и переходы Circuit Breaker по метрикам и оповещениям. Значения секретов в записи об ошибках не включайте.

## Q15. Какие best practices при работе с Spring Vault?

1. **Никогда не логировать секреты** — даже на уровне DEBUG. Один забытый `log.debug(password)` сводит на нет всю защиту: пароль попадёт в централизованные логи, к которым доступ шире, чем к Vault.

2. **В production — AppRole, а не статичный TOKEN** — токен сложно ротировать без рестарта, а скомпрометированный долгоживущий токен даёт доступ ко всему. AppRole делит секрет на части и работает по короткоживущему Secret ID (см. Q7).

3. **Минимальный TTL для динамических credentials** — чем короче живёт секрет, тем меньше окно для атаки при утечке. Истёкший секрет бесполезен.

4. **Включить аудит-логирование Vault** — `vault audit enable file file_path=/var/log/vault_audit.log`. Без аудита нельзя расследовать инцидент: кто и когда читал секрет.

5. **Отдельные роли и политики на каждую среду** — `prod-myapp-role`, `staging-myapp-role`. Так компрометация staging не открывает доступ к prod-секретам (изоляция blast radius).

6. **Graceful rotation** — при смене credentials обновлять пул соединений мягко (`softEvictConnections`), а не рвать активные соединения. Иначе ротация даёт всплеск ошибок (см. Q11).

7. **Health check Vault** — Spring Vault добавляет индикатор доступности Vault в `/actuator/health`. Это позволяет оркестратору и мониторингу заметить недоступность Vault раньше, чем она ударит по бизнес-запросам.

## See also

- [Secrets Management](../../security/secrets-management-interview.md) — общие вопросы по управлению секретами
- [Spring Security](spring-security-interview.md) — безопасность приложений
- [Kubernetes](../../devops/kubernetes-interview.md) — Vault + K8s интеграция через Service Account JWT
- [Spring Cloud](spring-cloud-interview.md) — Spring Cloud Config + Vault backend
- [Spring Boot](spring-boot-interview.md) — auto-configuration для Vault
- [HashiCorp Vault](../../devops/vault-interview.md) — основы Vault (policies, secret engines)
- [Application Security](../../security/application-security-interview.md) — общие паттерны безопасности
- [mTLS](../../security/mtls-interview.md) — взаимная TLS аутентификация
- [TLS/SSL](../../security/tls-ssl-interview.md) — управление сертификатами через Vault PKI
- [OAuth2](../../security/oauth2-interview.md) — Vault как источник OAuth2 client credentials
