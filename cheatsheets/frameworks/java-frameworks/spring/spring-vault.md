---
title: "Spring Vault: Полное руководство по управлению секретами"
description: "Комплексное руководство по Spring Vault: интеграция с HashiCorp Vault, secrets management, authentication, encryption и best practices"
tags: ["spring", "vault", "secrets", "security", "hashiCorp", "java"]
difficulty: "intermediate"
prerequisites: ["spring/spring-boot.md", "spring/spring-security.md"]
next: ["spring/spring-security.md"]
updated: "2026-02-11"
related: ["spring/spring-boot.md", "spring/spring-security.md"]
---

# Spring Vault: Полное руководство по управлению секретами



## Полезные ссылки

[Официальная документация Spring](https://docs.spring.io/)
[Spring Projects](https://spring.io/projects)

## Содержание

- [Spring Vault: Полное руководство по управлению секретами](#spring-vault-полное-руководство-по-управлению-секретами)
- [Введение в Spring Vault](#введение-в-spring-vault)
  - [Основные возможности](#основные-возможности)
  - [Архитектура Spring Vault](#архитектура-spring-vault)
- [Настройка Spring Vault](#настройка-spring-vault)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Vault Configuration](#vault-configuration)
  - [Java Configuration](#java-configuration)
- [Аутентификация](#аутентификация)
  - [Token Authentication](#token-authentication)
  - [AppRole Authentication](#approle-authentication)
  - [AWS Authentication](#aws-authentication)
- [Работа с секретами](#работа-с-секретами)
  - [KV Secrets](#kv-secrets)
  - [Vault Repositories](#vault-repositories)
- [Dynamic Secrets](#dynamic-secrets)
  - [Database Credentials](#database-credentials)
  - [PKI Certificates](#pki-certificates)
- [Lease Management](#lease-management)
  - [Lease Renewal](#lease-renewal)
- [Encryption](#encryption)
  - [Transit Encryption](#transit-encryption)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте правильный метод аутентификации](#1-используйте-правильный-метод-аутентификации)
  - [2. Управляйте lease правильно](#2-управляйте-lease-правильно)
  - [3. Используйте encryption для чувствительных данных](#3-используйте-encryption-для-чувствительных-данных)
  - [4. Настраивайте правильные пути](#4-настраивайте-правильные-пути)
  - [5. Обрабатывайте ошибки](#5-обрабатывайте-ошибки)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Vault Operations](#vault-operations)
  - [Environment Variables Integration](#environment-variables-integration)
  - [Health Check](#health-check)
  - [Metrics](#metrics)
  - [Kubernetes Authentication](#kubernetes-authentication)
  - [Azure Authentication](#azure-authentication)
  - [GCP Authentication](#gcp-authentication)
  - [LDAP Authentication](#ldap-authentication)
  - [Certificate Authentication](#certificate-authentication)
  - [Secret Versioning](#secret-versioning)
  - [Secret Metadata](#secret-metadata)
  - [Transit Key Management](#transit-key-management)
  - [Data Key Generation](#data-key-generation)
  - [Rewrap Operation](#rewrap-operation)
  - [Batch Operations](#batch-operations)
  - [Hash and Sign Operations](#hash-and-sign-operations)
  - [HSM Integration](#hsm-integration)
  - [Error Handling](#error-handling)
  - [Configuration Properties Binding](#configuration-properties-binding)
  - [Spring Cloud Vault Integration](#spring-cloud-vault-integration)
  - [Vault Configuration Properties](#vault-configuration-properties)
  - [Vault Secrets Injection](#vault-secrets-injection)
  - [Vault Response Wrapping](#vault-response-wrapping)
  - [Vault Policies](#vault-policies)
  - [Vault Audit Logging](#vault-audit-logging)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Spring Vault**

**Spring Vault** предоставляет интеграцию с **HashiCorp Vault** для безопасного управления секретами, ключами шифрования и другими конфиденциальными данными.

### Основные возможности

- **Secrets Management**: Управление секретами
- **Authentication**: Различные методы аутентификации
- **Encryption**: Шифрование данных
- **Dynamic Secrets**: Динамические секреты
- **Lease Management**: Управление **lease**

### Архитектура **Spring Vault**

```text
┌─────────────────────────────────────────────────────────┐
│              Application                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Vault      │  │   Vault      │  │   Vault      │  │
│  │   Template   │  │   Operations │  │   Repositories│  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Spring Vault Client                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Session    │  │   Lease      │  │   Auth       │  │
│  │   Management │  │   Management │  │   Management │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              HashiCorp Vault                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   KV Store   │  │   PKI        │  │   Database   │  │
│  │   Secrets    │  │   Secrets    │  │   Secrets    │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## Настройка **Spring Vault**

### Зависимости

**Зависимости **spring-vault-core** и **spring-`boot-starter`-vault** (**pom.xml**):**

```xml
<dependency>
    <groupId>org.springframework.vault</groupId>
    <artifactId>spring-vault-core</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-vault</artifactId>
</dependency>
```

### Конфигурация

```properties
# Vault Configuration
spring.cloud.vault.uri=http://localhost:8200
spring.cloud.vault.authentication=TOKEN
spring.cloud.vault.token=my-token
spring.cloud.vault.kv.enabled=true
spring.cloud.vault.kv.backend=secret
spring.cloud.vault.kv.application-name=my-app
```

### **Java Configuration**

```java
// Конфигурация VaultTemplate и подключения к Vault
@Configuration
public class VaultConfig {
    
    @Bean
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setHost("localhost");
        endpoint.setPort(8200);
        endpoint.setScheme("http");
        
        ClientAuthentication clientAuthentication = new TokenAuthentication("my-token");
        ClientFactory clientFactory = ClientHttpRequestFactoryFactory.create(
            new ClientOptions(), new SslConfiguration());
        
        RestTemplate restTemplate = VaultClients.createRestTemplate(
            clientFactory, endpoint);
        
        return new VaultTemplate(endpoint, clientAuthentication, restTemplate);
    }
}
```

## Аутентификация

### **Token Authentication**

```java
@Configuration
public class TokenAuthConfig {
    
    @Bean
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication("my-vault-token");
    }
}
```

### **AppRole Authentication**

```java
@Configuration
public class AppRoleAuthConfig {
    
    @Bean
    public ClientAuthentication clientAuthentication() {
        AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder()
            .roleId(AppRoleAuthenticationOptions.RoleId.provided("my-role-id"))
            .secretId(AppRoleAuthenticationOptions.SecretId.provided("my-secret-id"))
            .build();
        
        return new AppRoleAuthentication(options, restOperations());
    }
    
    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }
}
```

### **AWS Authentication**

```java
@Configuration
public class AwsAuthConfig {
    
    @Bean
    public ClientAuthentication clientAuthentication() {
        AwsIamAuthenticationOptions options = AwsIamAuthenticationOptions.builder()
            .role("my-aws-role")
            .build();
        
        return new AwsIamAuthentication(options, restOperations());
    }
}
```

## Работа с секретами

### `KV` **Secrets**

```java
// Чтение и запись секретов через VaultTemplate
@Service
public class VaultSecretService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public void writeSecret(String path, Map<String, Object> data) {
        vaultTemplate.write("secret/data/" + path, data);
    }
    
    public Map<String, Object> readSecret(String path) {
        VaultResponseSupport<Map> response = vaultTemplate.read(
            "secret/data/" + path, Map.class);
        return response != null ? response.getData() : null;
    }
    
    public void deleteSecret(String path) {
        vaultTemplate.delete("secret/data/" + path);
    }
}
```

### **Vault Repositories**

```java
@VaultRepository
public interface SecretRepository extends CrudRepository<Secret, String> {
    Optional<Secret> findByKey(String key);
}

@Entity
public class Secret {
    @Id
    private String key;
    private String value;
    
    // Getters and setters
}

@Service
public class SecretService {
    
    @Autowired
    private SecretRepository secretRepository;
    
    public Secret saveSecret(Secret secret) {
        return secretRepository.save(secret);
    }
    
    public Optional<Secret> findSecret(String key) {
        return secretRepository.findByKey(key);
    }
}
```

## Dynamic Secrets

### **Database Credentials**

```java
@Service
public class DynamicSecretService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public DatabaseCredentials getDatabaseCredentials(String role) {
        VaultResponseSupport<DatabaseCredentials> response = vaultTemplate.read(
            "database/creds/" + role, DatabaseCredentials.class);
        return response != null ? response.getData() : null;
    }
}
```

### **PKI Certificates**

```java
@Service
public class PkiService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public CertificateIssueRequest issueCertificate(String role, String commonName) {
        CertificateIssueRequest request = new CertificateIssueRequest();
        request.setCommonName(commonName);
        
        VaultResponseSupport<CertificateBundle> response = vaultTemplate.write(
            "pki/issue/" + role, request, CertificateBundle.class);
        return response != null ? response.getData() : null;
    }
}
```

## Lease Management

### **Lease Renewal**

```java
@Service
public class LeaseService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    @Autowired
    private LeaseRenewalScheduler leaseRenewalScheduler;
    
    public void renewLease(String leaseId) {
        leaseRenewalScheduler.renewLease(leaseId);
    }
    
    public void revokeLease(String leaseId) {
        vaultTemplate.write("sys/leases/revoke", Map.of("lease_id", leaseId));
    }
}
```

## Encryption

### **Transit Encryption**

```java
@Service
public class EncryptionService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public String encrypt(String keyName, String plaintext) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        return transitOperations.encrypt(keyName, plaintext);
    }
    
    public String decrypt(String keyName, String ciphertext) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        return transitOperations.decrypt(keyName, ciphertext);
    }
}
```

## Лучшие практики

### 1. Используйте правильный метод аутентификации

```java
// ✅ Хорошо - для production
@Bean
public ClientAuthentication clientAuthentication() {
    return new AppRoleAuthentication(...);
}
```

### 2. Управляйте **lease** правильно

```java
// ✅ Хорошо
@Bean
public LeaseRenewalScheduler leaseRenewalScheduler() {
    return new LeaseRenewalScheduler(...);
}
```

### 3. Используйте **encryption** для чувствительных данных

```java
// ✅ Хорошо
String encrypted = encryptionService.encrypt("my-key", sensitiveData);
```

### 4. Настраивайте правильные пути

```java
// ✅ Хорошо
vaultTemplate.write("secret/data/my-app/config", data);
```

### 5. Обрабатывайте ошибки

```java
// ✅ Хорошо
try {
    vaultTemplate.write(path, data);
} catch (VaultException e) {
    log.error("Vault error", e);
    // Обработка ошибки
}
```

## Продвинутые возможности

### **Vault Operations**

```java
@Service
public class VaultOperationsService {
    
    @Autowired
    private VaultOperations vaultOperations;
    
    public void writeSecret(String path, Object data) {
        vaultOperations.write(path, data);
    }
    
    public <T> T readSecret(String path, Class<T> type) {
        VaultResponseSupport<T> response = vaultOperations.read(path, type);
        return response != null ? response.getData() : null;
    }
    
    public void deleteSecret(String path) {
        vaultOperations.delete(path);
    }
    
    public List<String> listSecrets(String path) {
        return vaultOperations.list(path);
    }
}
```

### **Environment Variables Integration**

```java
@Configuration
public class VaultEnvironmentConfig {
    
    @Bean
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = VaultEndpoint.from(
            URI.create(System.getenv("VAULT_ADDR")));
        
        ClientAuthentication clientAuthentication = new TokenAuthentication(
            System.getenv("VAULT_TOKEN"));
        
        return new VaultTemplate(endpoint, clientAuthentication);
    }
}
```

### **Health Check**

```java
@Component
public class VaultHealthIndicator implements HealthIndicator {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    @Override
    public Health health() {
        try {
            vaultTemplate.doWithVault(vaultOperations -> {
                vaultOperations.read("sys/health");
                return null;
            });
            
            return Health.up()
                .withDetail("vault", "Available")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### **Metrics**

```java
@Component
public class VaultMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter secretsRead;
    private final Counter secretsWritten;
    private final Timer vaultOperationTime;
    
    public VaultMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.secretsRead = Counter.builder("vault.secrets.read")
            .description("Number of secrets read")
            .register(meterRegistry);
        this.secretsWritten = Counter.builder("vault.secrets.written")
            .description("Number of secrets written")
            .register(meterRegistry);
        this.vaultOperationTime = Timer.builder("vault.operation.time")
            .description("Vault operation time")
            .register(meterRegistry);
    }
    
    public <T> T measureOperation(String operation, Supplier<T> supplier) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            T result = supplier.get();
            if (operation.equals("read")) {
                secretsRead.increment();
            } else if (operation.equals("write")) {
                secretsWritten.increment();
            }
            return result;
        } finally {
            sample.stop(vaultOperationTime);
        }
    }
}
```

### **Kubernetes Authentication**

```java
@Configuration
public class KubernetesAuthConfig {
    
    @Bean
    public ClientAuthentication clientAuthentication() {
        KubernetesAuthenticationOptions options = KubernetesAuthenticationOptions.builder()
            .role("my-kubernetes-role")
            .jwtSupplier(() -> {
                // Получение JWT токена из Kubernetes
                return readServiceAccountToken();
            })
            .build();
        
        return new KubernetesAuthentication(options, restOperations());
    }
    
    private String readServiceAccountToken() {
        try {
            return new String(Files.readAllBytes(
                Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/token")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read service account token", e);
        }
    }
}
```

### **Azure Authentication**

```java
@Configuration
public class AzureAuthConfig {
    
    @Bean
    public ClientAuthentication clientAuthentication() {
        AzureMsiAuthenticationOptions options = AzureMsiAuthenticationOptions.builder()
            .role("my-azure-role")
            .build();
        
        return new AzureMsiAuthentication(options, restOperations());
    }
}
```

### **GCP Authentication**

```java
@Configuration
public class GcpAuthConfig {
    
    @Bean
    public ClientAuthentication clientAuthentication() {
        GcpComputeAuthenticationOptions options = GcpComputeAuthenticationOptions.builder()
            .role("my-gcp-role")
            .build();
        
        return new GcpComputeAuthentication(options, restOperations());
    }
}
```

### **LDAP Authentication**

```java
@Configuration
public class LdapAuthConfig {
    
    @Bean
    public ClientAuthentication clientAuthentication() {
        LdapAuthenticationOptions options = LdapAuthenticationOptions.builder()
            .username("my-username")
            .password("my-password")
            .build();
        
        return new LdapAuthentication(options, restOperations());
    }
}
```

### **Certificate Authentication**

```java
@Configuration
public class CertificateAuthConfig {
    
    @Bean
    public ClientAuthentication clientAuthentication() {
        ClientCertificateAuthenticationOptions options = ClientCertificateAuthenticationOptions.builder()
            .certificate(readCertificate())
            .privateKey(readPrivateKey())
            .build();
        
        return new ClientCertificateAuthentication(options, restOperations());
    }
    
    private X509Certificate readCertificate() {
        // Чтение сертификата
        return null;
    }
    
    private PrivateKey readPrivateKey() {
        // Чтение приватного ключа
        return null;
    }
}
```

### **Secret Versioning**

```java
@Service
public class VersionedSecretService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public void writeVersionedSecret(String path, Map<String, Object> data) {
        // Запись с версионированием (KV v2)
        vaultTemplate.write("secret/data/" + path, data);
    }
    
    public Map<String, Object> readVersionedSecret(String path, Integer version) {
        VaultResponseSupport<Map> response = vaultTemplate.read(
            "secret/data/" + path + "?version=" + version, Map.class);
        return response != null ? response.getData() : null;
    }
    
    public void deleteVersionedSecret(String path, Integer version) {
        vaultTemplate.write("secret/delete/" + path, 
            Map.of("versions", Collections.singletonList(version)));
    }
}
```

### **Secret Metadata**

```java
@Service
public class SecretMetadataService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public Map<String, Object> getSecretMetadata(String path) {
        VaultResponseSupport<Map> response = vaultTemplate.read(
            "secret/metadata/" + path, Map.class);
        return response != null ? response.getData() : null;
    }
    
    public void updateSecretMetadata(String path, Map<String, Object> metadata) {
        vaultTemplate.write("secret/metadata/" + path, metadata);
    }
}
```

### **Transit Key Management**

```java
@Service
public class TransitKeyService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public void createTransitKey(String keyName) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        transitOperations.createKey(keyName);
    }
    
    public void rotateTransitKey(String keyName) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        transitOperations.rotateKey(keyName);
    }
    
    public void deleteTransitKey(String keyName) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        transitOperations.deleteKey(keyName);
    }
    
    public Map<String, Object> getTransitKeyInfo(String keyName) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        return transitOperations.getKey(keyName);
    }
}
```

### **Data Key Generation**

```java
@Service
public class DataKeyService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public Plaintext generateDataKey(String keyName) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        return transitOperations.createDataKey(keyName);
    }
    
    public Plaintext generateWrappedDataKey(String keyName) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        return transitOperations.createDataKey(keyName, VaultTransitOperations.CreateKeyRequest.builder()
            .plaintext(false)
            .build());
    }
}
```

### **Rewrap Operation**

```java
@Service
public class RewrapService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public String rewrap(String keyName, String ciphertext) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        return transitOperations.rewrap(keyName, ciphertext);
    }
}
```

### **Batch Operations**

```java
@Service
public class BatchVaultService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public List<String> batchEncrypt(String keyName, List<String> plaintexts) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        List<Plaintext> plaintextList = plaintexts.stream()
            .map(Plaintext::of)
            .collect(Collectors.toList());
        
        List<Ciphertext> ciphertexts = transitOperations.encrypt(keyName, plaintextList);
        return ciphertexts.stream()
            .map(Ciphertext::getCiphertext)
            .collect(Collectors.toList());
    }
    
    public List<String> batchDecrypt(String keyName, List<String> ciphertexts) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        List<Ciphertext> ciphertextList = ciphertexts.stream()
            .map(Ciphertext::of)
            .collect(Collectors.toList());
        
        List<Plaintext> plaintexts = transitOperations.decrypt(keyName, ciphertextList);
        return plaintexts.stream()
            .map(Plaintext::asString)
            .collect(Collectors.toList());
    }
}
```

### **Hash and Sign Operations**

```java
@Service
public class HashSignService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public String hash(String algorithm, String input) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        return transitOperations.hash(algorithm, Plaintext.of(input));
    }
    
    public String sign(String keyName, String input) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        return transitOperations.sign(keyName, Plaintext.of(input));
    }
    
    public boolean verify(String keyName, String signature, String input) {
        VaultTransitOperations transitOperations = vaultTemplate.opsForTransit();
        return transitOperations.verify(keyName, Signature.of(signature), Plaintext.of(input));
    }
}
```

### **HSM Integration**

```java
@Configuration
public class HsmConfig {
    
    @Bean
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setHost("localhost");
        endpoint.setPort(8200);
        
        // Настройка для работы с HSM
        ClientOptions clientOptions = new ClientOptions();
        SslConfiguration sslConfiguration = SslConfiguration.builder()
            .trustStore(new File("truststore.jks"))
            .trustStorePassword("password")
            .build();
        
        ClientFactory clientFactory = ClientHttpRequestFactoryFactory.create(
            clientOptions, sslConfiguration);
        
        RestTemplate restTemplate = VaultClients.createRestTemplate(
            clientFactory, endpoint);
        
        ClientAuthentication clientAuthentication = new TokenAuthentication("my-token");
        
        return new VaultTemplate(endpoint, clientAuthentication, restTemplate);
    }
}
```

### **Error Handling**

```java
@Service
public class ErrorHandlingVaultService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public Map<String, Object> readSecretWithRetry(String path) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                VaultResponseSupport<Map> response = vaultTemplate.read(path, Map.class);
                return response != null ? response.getData() : null;
            } catch (VaultException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("Failed to read secret after {} retries", maxRetries, e);
                    throw e;
                }
                try {
                    Thread.sleep(1000 * retryCount); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during retry", ie);
                }
            }
        }
        return null;
    }
}
```

### **Configuration Properties Binding**

```java
@ConfigurationProperties(prefix = "vault")
public class VaultProperties {
    private String uri;
    private String token;
    private String authentication;
    private Kv kv = new Kv();
    
    // Getters and setters
    
    public static class Kv {
        private boolean enabled;
        private String backend;
        private String applicationName;
        
        // Getters and setters
    }
}

@Configuration
@EnableConfigurationProperties(VaultProperties.class)
public class VaultPropertiesConfig {
    
    @Autowired
    private VaultProperties vaultProperties;
    
    @Bean
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = VaultEndpoint.from(
            URI.create(vaultProperties.getUri()));
        
        ClientAuthentication clientAuthentication = createAuthentication();
        
        return new VaultTemplate(endpoint, clientAuthentication);
    }
    
    private ClientAuthentication createAuthentication() {
        switch (vaultProperties.getAuthentication()) {
            case "TOKEN":
                return new TokenAuthentication(vaultProperties.getToken());
            case "APPROLE":
                // Настройка AppRole
                return null;
            default:
                throw new IllegalArgumentException(
                    "Unknown authentication: " + vaultProperties.getAuthentication());
        }
    }
}
```

### **Spring Cloud Vault Integration**

```java
@Configuration
@EnableConfigurationProperties(VaultProperties.class)
public class SpringCloudVaultConfig {
    
    @Bean
    public VaultTemplate vaultTemplate(VaultProperties properties) {
        VaultEndpoint endpoint = VaultEndpoint.from(
            URI.create(properties.getUri()));
        
        ClientAuthentication clientAuthentication = createAuthentication(properties);
        
        return new VaultTemplate(endpoint, clientAuthentication);
    }
    
    private ClientAuthentication createAuthentication(VaultProperties properties) {
        // Создание аутентификации на основе свойств
        return new TokenAuthentication(properties.getToken());
    }
}
```

### **Vault Configuration Properties**

```java
@ConfigurationProperties(prefix = "spring.cloud.vault")
public class VaultConfigProperties {
    private String uri;
    private String authentication;
    private String token;
    private Kv kv = new Kv();
    private Database database = new Database();
    private Pki pki = new Pki();
    
    // Getters and setters
    
    public static class Kv {
        private boolean enabled;
        private String backend;
        private String applicationName;
        // Getters and setters
    }
    
    public static class Database {
        private boolean enabled;
        private String role;
        // Getters and setters
    }
    
    public static class Pki {
        private boolean enabled;
        private String role;
        // Getters and setters
    }
}
```

### **Vault Secrets Injection**

```java
@Service
public class SecretsInjectionService {
    
    @Value("${vault.secret.database.password}")
    private String databasePassword;
    
    @Value("${vault.secret.api.key}")
    private String apiKey;
    
    public void useSecrets() {
        // Использование секретов из Vault
        connectToDatabase(databasePassword);
        callExternalApi(apiKey);
    }
}
```

### **Vault Response Wrapping**

```java
@Service
public class ResponseWrappingService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public String wrapSecret(String secret) {
        VaultResponseSupport<Map> response = vaultTemplate.write(
            "sys/wrapping/wrap", 
            Map.of("secret", secret),
            Map.class);
        
        return (String) response.getData().get("token");
    }
    
    public String unwrapSecret(String wrappingToken) {
        VaultResponseSupport<Map> response = vaultTemplate.read(
            "sys/wrapping/unwrap",
            Map.class,
            Map.of("token", wrappingToken));
        
        return (String) response.getData().get("secret");
    }
}
```

### **Vault Policies**

```java
@Service
public class PolicyService {
    
    @Autowired
    private VaultTemplate vaultTemplate;
    
    public void createPolicy(String policyName, String policyContent) {
        vaultTemplate.write("sys/policies/acl/" + policyName, 
            Map.of("policy", policyContent));
    }
    
    public String getPolicy(String policyName) {
        VaultResponseSupport<Map> response = vaultTemplate.read(
            "sys/policies/acl/" + policyName, Map.class);
        return response != null ? (String) response.getData().get("policy") : null;
    }
}
```

### **Vault Audit Logging**

```java
@Configuration
public class AuditLoggingConfig {
    
    @Bean
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setHost("localhost");
        endpoint.setPort(8200);
        
        // Настройка audit logging
        ClientOptions clientOptions = new ClientOptions();
        clientOptions.setReadTimeout(Duration.ofSeconds(5));
        clientOptions.setConnectionTimeout(Duration.ofSeconds(5));
        
        ClientAuthentication clientAuthentication = new TokenAuthentication("my-token");
        
        return new VaultTemplate(endpoint, clientAuthentication);
    }
}
```


## Заключение

**Spring Vault** предоставляет мощные инструменты для безопасного управления секретами. Правильное использование аутентификации (**Token, `AppRole`, `AWS`, `Kubernetes`, `Azure`, `GCP`, `LDAP`, Certificate**), работы с секретами, **dynamic secrets**, **lease management**, **encryption**, **transit operations**, **versioning**, **metadata**, **batch operations**, **hash**/**sign operations**, **HSM integration**, **error handling**, **Spring Cloud Vault integration**, **response wrapping**, **policies**, **audit logging** и других продвинутых возможностей позволяет создавать безопасные приложения с централизованным управлением конфиденциальными данными.

## Дополнительные ресурсы

- [**Spring Vault** Documentation](https://docs.spring.io/spring-vault/reference/)
- [HashiCorp **Vault** Documentation](https://developer.hashicorp.com/vault/docs)
- [**Spring Cloud Vault**](https://spring.io/projects/spring-cloud-vault)
- [**Vault** API](https://developer.hashicorp.com/vault/api-docs)
- [**Vault Best Practices**](https://developer.hashicorp.com/vault/docs/best-practices)
- [**Vault Security**](https://developer.hashicorp.com/vault/docs/internals/security)
