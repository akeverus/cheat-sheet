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
>
> **Вопрос:** Зачем переходить с хранения секретов в `application.yml`/env vars на HashiCorp Vault?
>
> ---
>
> #### A) Vault — это просто шифрованный YAML-файл, который Spring подключает вместо `application.yml`, секреты лежат в Git, но в зашифрованном виде — ❌ Неверно
>
> **Что на самом деле:** Vault — это сетевой сервис (HTTP API), который хранит секреты в своём backend storage (Consul, integrated raft) и выдаёт их только аутентифицированным клиентам с действующим токеном. Никаких файлов в Git — клиент обращается к `https://vault:8200/v1/secret/data/...` и получает JSON.
>
> **Откуда путаница:** ассоциация с `git-crypt`, `SOPS`, `Ansible Vault` — там действительно зашифрованные файлы в репозитории. HashiCorp Vault — другой класс инструмента.
>
> **Если бы это было правдой:** ротация мастер-ключа требовала бы переписать всю историю Git, аудит «кто читал секрет» был бы невозможен (Git не логирует чтения), а dynamic secrets с TTL не существовали бы в принципе.
>
> ---
>
> #### B) Vault нужен только для соответствия требованиям PCI DSS / SOC2 — функционально env vars и `application.yml` дают то же самое, разница только в галочке аудитора — ❌ Неверно
>
> **Что на самом деле:** compliance — это побочный эффект. Главные технические преимущества: централизованная ротация без передеплоя, dynamic secrets (TTL-credentials генерируются on-demand), transit encryption-as-a-service, аудит-лог каждого чтения, fine-grained ACL через policies.
>
> **Откуда путаница:** аудиторы действительно требуют Vault-подобные решения, и многие команды внедряют его «для галочки».
>
> **Если бы это было правдой:** при компрометации pod-а с env vars злоумышленник получил бы статичный пароль на годы — у Vault dynamic credentials истекут через час и Vault сам revoke-нет lease в PostgreSQL.
>
> ---
>
> #### C) HashiCorp Vault — централизованное хранилище секретов с динамической генерацией credentials, transit encryption и аудит-логами; Spring Vault — клиентская библиотека, инкапсулирующая Vault HTTP API в `VaultTemplate` и `@VaultPropertySource` — ✓ Верно
>
> **Развёрнутое объяснение:** Vault решает несколько проблем одновременно. Static secrets (KV engine) — заменяют пароли в YAML. Dynamic secrets (database, AWS, PKI engines) — Vault создаёт уникальный username/password для каждого приложения с TTL, после которого автоматически revoke-ает. Transit engine — encryption-as-a-service: приложение шлёт plaintext, получает ciphertext, ключ никогда не покидает Vault. Аудит-лог пишет каждую операцию чтения с identity клиента. Spring Vault даёт идиоматичный Spring-доступ: `VaultTemplate` для прямого API, `spring-cloud-starter-vault-config` для подтягивания секретов в `Environment` на старте.
>
> **Пример:**
> ```yaml
> # bootstrap.yml — Spring Cloud Vault загружает секреты ДО application.yml
> spring:
>   cloud:
>     vault:
>       host: vault.prod.internal
>       port: 8200
>       scheme: https
>       authentication: KUBERNETES
>       kubernetes:
>         role: payment-service
>         service-account-token-file: /var/run/secrets/kubernetes.io/serviceaccount/token
>       kv:
>         enabled: true
>         backend: secret
>         version: 2
>         application-name: payment-service
> ```
>
> **Когда применять:** микросервисы в production, требования к ротации без передеплоя, dynamic DB credentials, mTLS через Vault PKI, encryption-as-a-service для PII (карты, паспорта), мульти-кластерные deployments с единой policy-моделью.
>
> **Подводные камни:** Vault — SPOF без HA-конфигурации (минимум 3 ноды с Raft); unsealing требует ключей Shamir's Secret Sharing (5 из 7 операторов); токен root никогда не использовать в приложениях; auto-unseal через cloud KMS обязателен в облаке.
>
> ---
>
> #### D) Vault полностью заменяет environment variables и Kubernetes Secrets — после внедрения Vault их использовать запрещено — ❌ Неверно
>
> **Что на самом деле:** env vars и K8s Secrets продолжают использоваться для bootstrap-конфигурации — нужно как-то передать в pod `VAULT_ROLE_ID`, путь к service account token, адрес Vault. Это chicken-and-egg: чтобы получить секреты, нужен initial credential. K8s Service Account JWT — стандартный bootstrap для Vault Kubernetes auth.
>
> **Откуда путаница:** маркетинговое позиционирование Vault как «убийцы env vars».
>
> **Если бы это было правдой:** невозможно было бы передать в pod даже URL Vault-сервера — он сам стал бы секретом без места хранения.
>
> **Связанные вопросы:** [[Q2]] — настройка Spring Vault; [[Q7]] — методы аутентификации; [[Q12]] — Vault Agent

## Q2. Как настроить Spring Vault?

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
>
> **Вопрос:** Spring Cloud Vault конфигурация задаётся в `bootstrap.yml`, а не в `application.yml`. Почему?
>
> ---
>
> #### A) `bootstrap.yml` — устаревший формат из Spring Boot 1.x, в Spring Boot 3.x обе конфигурации работают одинаково, разницы нет — ❌ Неверно
>
> **Что на самом деле:** `bootstrap.yml` загружается через специальный `Bootstrap ApplicationContext`, который инициализируется ДО основного контекста — именно в этот момент Spring Cloud Vault аутентифицируется и подтягивает секреты в `Environment`. В Spring Boot 2.4+ появилась альтернатива через `spring.config.import: vault://...`, но bootstrap context остаётся актуальным для legacy-конфигураций.
>
> **Откуда путаница:** многие гайды для Spring Boot 3 действительно показывают `spring.config.import` вместо `bootstrap.yml`, создавая впечатление deprecation.
>
> **Если бы это было правдой:** `@Value("${db.password}")` в `application.yml` не резолвилось бы, потому что Vault ещё не запрошен — placeholder остался бы нерешённым и контекст упал бы с `IllegalArgumentException`.
>
> ---
>
> #### B) Чтобы Vault-секреты попали в `Environment` ДО создания `@ConfigurationProperties`, `@Value`-биндов и DataSource bean-ов, конфигурация подключения к Vault должна быть доступна на самой ранней фазе старта — это роль `bootstrap.yml` или `spring.config.import: vault://` в Spring Boot 2.4+ — ✓ Верно
>
> **Развёрнутое объяснение:** Spring Boot имеет двухфазную модель загрузки. Bootstrap phase: маленький контекст с минимальным набором bean-ов, отвечающий за внешнюю конфигурацию (Vault, Config Server). Application phase: основной контекст с вашими бизнес-bean-ами. Vault-клиент должен жить в bootstrap phase, потому что значения, которые он подтягивает (например, `spring.datasource.password`), нужны при создании `DataSource` в application phase. Если положить `spring.cloud.vault.*` в `application.yml` — клиент попытается создаться слишком поздно, secrets не попадут в `Environment` к моменту, когда они нужны.
>
> **Пример:**
> ```yaml
> # bootstrap.yml — обязательно для legacy подхода
> spring:
>   application:
>     name: payment-service
>   cloud:
>     vault:
>       host: vault.prod.internal
>       port: 8200
>       scheme: https
>       authentication: APPROLE
>       app-role:
>         role-id: ${VAULT_ROLE_ID}
>         secret-id: ${VAULT_SECRET_ID}
>       kv:
>         enabled: true
>         backend: secret
>         application-name: payment-service
> ```
> ```yaml
> # ИЛИ современный подход (Spring Boot 2.4+, Spring Cloud 2020.0+)
> # application.yml
> spring:
>   config:
>     import: "vault://secret/payment-service"
>   cloud:
>     vault:
>       host: vault.prod.internal
>       authentication: APPROLE
> ```
>
> **Когда применять:** bootstrap.yml — при использовании `spring-cloud-starter-bootstrap`; `spring.config.import` — для новых проектов на Spring Boot 2.4+, более явная фазированная загрузка без скрытого bootstrap context.
>
> **Подводные камни:** в Spring Boot 2.4+ bootstrap context отключён по умолчанию — нужна явная зависимость `spring-cloud-starter-bootstrap`, иначе `bootstrap.yml` игнорируется. Профили в bootstrap (`bootstrap-prod.yml`) работают отдельно от application-профилей.
>
> ---
>
> #### C) `bootstrap.yml` хранится в зашифрованном виде через `jasypt` и сам по себе является секретом — поэтому его нельзя коммитить в Git — ❌ Неверно
>
> **Что на самом деле:** `bootstrap.yml` — обычный YAML, его именно коммитят в Git. Он содержит только конфигурацию подключения к Vault (адрес, метод аутентификации), но НЕ секреты. `VAULT_ROLE_ID` и `VAULT_SECRET_ID` подставляются через env vars/Kubernetes Secrets в момент запуска.
>
> **Откуда путаница:** ассоциация со словом «secret» в имени `VaultSecretId`.
>
> **Если бы это было правдой:** AppRole-механизм был бы бесполезен — суть AppRole в том, что Role ID можно безопасно зашить в образ, а Secret ID — короткоживущий wrapping token от CI/CD.
>
> ---
>
> #### D) `bootstrap.yml` нужен только для Spring Cloud Config Server — для Vault используется только `application.yml` — ❌ Неверно
>
> **Что на самом деле:** оба компонента (Config Server и Vault) являются «property source loader»-ами уровня bootstrap. Любая внешняя система, которая поставляет свойства до основного контекста, конфигурируется в bootstrap phase. Vault в этом смысле архитектурно идентичен Config Server.
>
> **Откуда путаница:** исторически bootstrap.yml появился именно для Config Server, и многие воспринимают его как «config-server-specific».
>
> **Если бы это было правдой:** Spring Cloud Vault не смог бы заменять `${db.password}` в `application.yml` — placeholder остался бы строкой `${db.password}`.
>
> **Связанные вопросы:** [[Q1]] — зачем Vault; [[Q3]] — чтение секретов; [[Q9]] — интеграция с PropertySource

## Q3. Как читать секреты из Vault?

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
>
> **Вопрос:** Когда вы пишете `@Value("${db.password}")` с Spring Cloud Vault, в какой момент происходит обращение к Vault и что произойдёт, если значение в Vault изменится?
>
> ---
>
> #### A) `@Value` динамически читает Vault при каждом обращении к полю — изменения в Vault видны мгновенно — ❌ Неверно
>
> **Что на самом деле:** `@Value` — это инъекция значения один раз при создании bean-а. Spring подставляет строку из `Environment` в поле, после этого никаких обращений к Vault не происходит. Это обычное property resolution, идентичное `@Value` из `application.yml`.
>
> **Откуда путаница:** ожидание, что Vault-интеграция «магически» делает поля reactive. Это работа `@RefreshScope` или явного `VaultTemplate`-вызова, не `@Value`.
>
> **Если бы это было правдой:** каждый вызов `userRepository.findById()` дёргал бы Vault для re-resolution `${spring.datasource.password}` — latency и DDoS на Vault.
>
> ---
>
> #### B) `@Value` резолвится один раз при создании bean-а на старте — для runtime-обновления нужны `@RefreshScope` + `/actuator/refresh`, либо прямой `VaultTemplate.read()`, либо `VaultLeaseContainer` с callback на ротацию — ✓ Верно
>
> **Развёрнутое объяснение:** Spring Cloud Vault на старте подтягивает секреты в `Environment` через `PropertySource`. `@Value("${db.password}")` обращается к `Environment.getProperty()` ровно один раз — при инстанциировании bean-а. Чтобы получить новое значение после ротации в Vault, нужно либо: (1) пометить bean `@RefreshScope` и вызвать `POST /actuator/refresh` — тогда bean пересоздаётся и `@Value` резолвится заново; (2) использовать `VaultTemplate` напрямую — каждый вызов `vaultTemplate.read("secret/...")` идёт в Vault; (3) подписаться на ротацию через `VaultLeaseContainer.requestRotatingSecret()` — callback вызывается при автоматическом продлении lease.
>
> **Пример:**
> ```java
> // Способ 1: VaultTemplate — прямое чтение, без кэширования
> @Service
> @RequiredArgsConstructor
> public class SecretService {
>     private final VaultTemplate vaultTemplate;
>
>     public String getDatabasePassword() {
>         VaultResponseSupport<Map<String, Object>> response =
>             vaultTemplate.read("secret/data/myapp/db");
>         return (String) response.getData().get("password");
>     }
> }
>
> // Способ 2: @RefreshScope для runtime-обновления @Value
> @Component
> @RefreshScope
> public class ApiKeyHolder {
>     @Value("${external.api.key}")
>     private String apiKey;
>     // POST /actuator/refresh пересоздаёт bean, apiKey перечитывается из Vault
> }
> ```
>
> **Когда применять:** статичные конфиги (URL внешнего API) — `@Value`; периодически ротируемые секреты — `@RefreshScope`; dynamic credentials с TTL (DB, AWS) — `VaultLeaseContainer`.
>
> **Подводные камни:** `@RefreshScope` создаёт CGLIB-прокси, что ломает финальные классы и kotlin-data-class-ы; bean пересоздаётся целиком — все его поля сбрасываются (включая накопленные кэши); вызов `/actuator/refresh` без security exposes конфигурацию.
>
> ---
>
> #### C) `@VaultPropertySource` работает в runtime — при каждом `${...}`-resolve обращается к Vault — ❌ Неверно
>
> **Что на самом деле:** `@VaultPropertySource` регистрирует `PropertySource` один раз при инициализации контекста — он подтягивает все ключи из указанного пути и кэширует их в Environment. Дальше работает как обычный `PropertySource`. Никакого runtime-resolve в Vault при каждом `${...}` нет.
>
> **Откуда путаница:** имя «PropertySource» воспринимается как «активный источник», но это просто snapshot ключей.
>
> **Если бы это было правдой:** `@VaultPropertySource` имел бы массивные performance-проблемы — каждое property-resolution = HTTP-вызов в Vault.
>
> ---
>
> #### D) Чтение секретов из Vault невозможно через Spring beans — нужно использовать только Vault CLI или HTTP API напрямую — ❌ Неверно
>
> **Что на самом деле:** Spring Vault предоставляет три идиоматичных Spring-способа: (1) `@Value` через Spring Cloud Vault PropertySource (startup-time); (2) `VaultTemplate` для программного API (runtime); (3) `@VaultPropertySource` для декларативной подгрузки конкретного пути в Environment.
>
> **Откуда путаница:** недопонимание разделения ответственности между Vault CLI (для операторов/DevOps) и Spring Vault (для приложений).
>
> **Если бы это было правдой:** существование `spring-vault-core`, `spring-cloud-starter-vault-config` и `VaultTemplate` было бы бессмысленным.
>
> **Связанные вопросы:** [[Q4]] — dynamic secrets; [[Q5]] — VaultLeaseContainer; [[Q9]] — PropertySource integration

## Q4. Что такое Dynamic Secrets и как их использовать?

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
>
> **Вопрос:** Чем dynamic secrets отличаются от обычных static credentials в KV engine?
>
> ---
>
> #### A) Dynamic secrets — это просто KV-секреты с короткой TTL, Vault удаляет запись из KV после истечения времени — ❌ Неверно
>
> **Что на самом деле:** dynamic secrets — это credentials, которые Vault **создаёт on-demand** в целевой системе (PostgreSQL, MySQL, AWS, MongoDB) для каждого обращения. Vault не хранит их, а генерирует: создаёт нового пользователя в БД через `creation_statements`, отдаёт credentials клиенту, регистрирует lease, и по истечении TTL **дропает пользователя** в целевой системе через `revocation_statements`. KV engine хранит статичные значения и не имеет такой механики.
>
> **Откуда путаница:** оба механизма используют TTL, но смысл TTL разный: в KV — это «expire entry», в dynamic — «revoke созданного external resource».
>
> **Если бы это было правдой:** Vault не нуждался бы в database plugin и connection-конфигурации с root-доступом к БД для создания/удаления пользователей.
>
> ---
>
> #### B) Vault создаёт уникальные TTL-credentials в целевой системе (БД, AWS, PKI) по запросу приложения, регистрирует lease и автоматически revoke-ает их (DROP USER, IAM-key delete) после истечения TTL — каждое приложение получает свои creds — ✓ Верно
>
> **Развёрнутое объяснение:** ключевая идея — Vault выступает как **trusted authority**, который имеет root-доступ к целевой системе и делегирует ограниченные credentials. Для database engine это работает так: оператор настраивает в Vault connection (с root-паролем БД) и role с `creation_statements` (SQL для создания юзера) и `revocation_statements` (DROP USER). Приложение вызывает `vault read database/creds/payment-app` — Vault генерирует уникальный username `v-approle-payment-XXX`, выполняет SQL, отдаёт credentials с TTL и `lease_id`. Через TTL (или вручную `vault lease revoke`) Vault сам выполняет DROP USER. Никаких shared credentials между инстансами.
>
> **Пример:**
> ```java
> // Получение TTL-credentials для PostgreSQL
> @Service
> @RequiredArgsConstructor
> public class DynamicDbCredentialsProvider {
>     private final VaultTemplate vaultTemplate;
>
>     public DbCredentials fetch() {
>         VaultResponseSupport<Map<String, Object>> lease =
>             vaultTemplate.read("database/creds/payment-role");
>         return new DbCredentials(
>             (String) lease.getRequiredData().get("username"),  // v-approle-payment-7K2x...
>             (String) lease.getRequiredData().get("password"),
>             lease.getLeaseId(),                                  // database/creds/.../abc123
>             Duration.ofSeconds(lease.getLeaseDuration())         // 1 hour
>         );
>     }
> }
> ```
> ```hcl
> # Vault server config (terraform или vault CLI)
> resource "vault_database_secret_backend_role" "payment" {
>   name        = "payment-role"
>   backend     = "database"
>   db_name     = "postgres-prod"
>   default_ttl = 3600
>   max_ttl     = 86400
>   creation_statements = [
>     "CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}';",
>     "GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA app TO \"{{name}}\";"
>   ]
> }
> ```
>
> **Когда применять:** доступ к БД из микросервисов (каждый pod — свой user), доступ к AWS (STS-токены через aws engine), доступ к Consul/RabbitMQ, TLS-сертификаты через PKI engine. Особенно полезно когда нужно зафиксировать кто именно делал запрос в audit-логе целевой системы.
>
> **Подводные камни:** root-credentials БД в Vault — критический secret (компрометация = захват БД); `max_ttl` ограничивает максимальное продление — после него lease нельзя renew и нужно requesting новый; при revoke во время активного connection-pool пул резко получает auth-error — нужен graceful rotation через `softEvictConnections`; PostgreSQL `DROP USER` не сработает если у user есть owned objects — `revocation_statements` должны включать `REASSIGN OWNED` или `DROP OWNED`.
>
> ---
>
> #### C) Dynamic secrets — это секреты, которые Vault генерирует случайным образом и хранит в KV, отдавая клиенту при каждом запросе один и тот же сгенерированный пароль — ❌ Неверно
>
> **Что на самом деле:** dynamic secrets никогда не хранятся в Vault после генерации — Vault держит только lease metadata (когда revoke). Каждый вызов `vault read database/creds/...` возвращает **разные** credentials (если не запрашиваешь существующий lease по ID).
>
> **Откуда путаница:** ассоциация с password generators (Vaultwarden, 1Password), которые генерируют и сохраняют пароли.
>
> **Если бы это было правдой:** теряется главное преимущество dynamic secrets — изоляция между запросами и автоматический revoke в целевой системе.
>
> ---
>
> #### D) Dynamic secrets — экспериментальная фича, не предназначенная для production — все используют только KV — ❌ Неверно
>
> **Что на самом деле:** dynamic secrets — production-ready с 2016 года, активно используются в банках, fintech, healthcare. Database, AWS, PKI, SSH engines стабильны и имеют GA-статус. В крупных deployments dynamic credentials — основной use case Vault, а KV — вспомогательный для статичных секретов (API keys внешних сервисов).
>
> **Откуда путаница:** реальная сложность настройки (нужны permissions в целевой системе, понимание lease lifecycle) создаёт впечатление «не для production».
>
> **Если бы это было правдой:** существование `database-secret-engines`, `aws-secret-backend`, dedicated документации по PKI рабочим процессам было бы необъяснимо.
>
> **Связанные вопросы:** [[Q5]] — VaultLeaseContainer и lease renewal; [[Q11]] — автоматическая ротация; [[Q8]] — PKI engine

## Q5. Что такое VaultLeaseContainer и зачем он нужен?

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
>
> **Вопрос:** Зачем нужен `VaultLeaseContainer` при работе с dynamic secrets и что произойдёт без него?
>
> ---
>
> #### A) `VaultLeaseContainer` — это локальный кэш Vault-ответов, ускоряющий чтение секретов; без него каждый `vaultTemplate.read()` идёт в сеть, но функционально приложение работает корректно — ❌ Неверно
>
> **Что на самом деле:** `VaultLeaseContainer` — не кэш, а **lease lifecycle manager**. Он отслеживает выданные dynamic credentials с TTL, планирует через `TaskScheduler` фоновую задачу `renew lease` за `expiryThresholdPercentage` до истечения, и при невозможности renew (например, достигнут `max_ttl`) вызывает callback с новыми credentials через `vault.read()`. Кэширования ответов нет — есть управление жизненным циклом lease.
>
> **Откуда путаница:** слово «Container» воспринимается как Spring-context-aware кэш-контейнер.
>
> **Если бы это было правдой:** dynamic credentials всё равно истекли бы через TTL независимо от кэша, и приложение получило бы `PSQLException: FATAL: role does not exist` от закрытого Vault-ом пользователя БД.
>
> ---
>
> #### B) `VaultLeaseContainer` управляет жизненным циклом lease (renew до истечения TTL, ротация при достижении `max_ttl`) и через `requestRotatingSecret()` уведомляет callback о новых credentials — без него dynamic secrets просто истекут и приложение получит auth-error от целевой системы — ✓ Верно
>
> **Развёрнутое объяснение:** dynamic secrets (DB credentials, AWS STS) имеют два TTL: `default_ttl` (через сколько lease нуждается в renew) и `max_ttl` (максимальное время жизни даже с renew). `VaultLeaseContainer` решает обе проблемы: (1) renew — фоновый scheduler через `expiryThresholdPercentage` (по умолчанию 90% от TTL) вызывает `vault write sys/leases/renew` и продлевает lease; (2) ротация — когда renew невозможен (max_ttl исчерпан или Vault revoke-нул), контейнер вызывает `requestNewLease()`, получает новые credentials и триггерит callback `LeaseAwareConfigurer`. Без контейнера TTL истечёт незаметно и `DROP USER` в БД приведёт к `FATAL: role "v-approle-XXX" does not exist` на следующем DB-запросе.
>
> **Пример:**
> ```java
> @Configuration
> public class VaultLeaseConfig {
>     @Bean(destroyMethod = "destroy")
>     public SecretLeaseContainer leaseContainer(VaultOperations vaultOperations,
>                                                  TaskScheduler scheduler) {
>         SecretLeaseContainer container = new SecretLeaseContainer(vaultOperations, scheduler);
>         container.setExpiryThresholdSeconds(60);     // обновлять за 60s до истечения
>         container.setMinRenewalSeconds(10);
>         return container;
>     }
> }
>
> @Service
> @RequiredArgsConstructor
> public class DbCredentialsRotator {
>     private final SecretLeaseContainer leaseContainer;
>     private final HikariDataSource dataSource;
>
>     @PostConstruct
>     public void subscribe() {
>         RequestedSecret secret = RequestedSecret.rotating("database/creds/payment-role");
>         leaseContainer.addLeaseListener(event -> {
>             if (event instanceof SecretLeaseCreatedEvent created) {
>                 Map<String, Object> data = created.getSecrets();
>                 dataSource.setUsername((String) data.get("username"));
>                 dataSource.setPassword((String) data.get("password"));
>                 dataSource.getHikariPoolMXBean().softEvictConnections();
>             }
>         });
>         leaseContainer.addRequestedSecret(secret);
>     }
> }
> ```
>
> **Когда применять:** все случаи dynamic secrets — PostgreSQL/MySQL credentials, AWS STS-токены, RabbitMQ users, MongoDB users. Без него dynamic secrets превращаются в bombу замедленного действия.
>
> **Подводные камни:** callback вызывается из scheduler-thread — не блокировать долгими операциями; `softEvictConnections()` не закрывает активные транзакции, только idle connections — running queries продолжат работу со старым user-ом до завершения; при `max_ttl` лучше использовать `rotating` (новый lease) вместо `renewing` (renew существующего).
>
> ---
>
> #### C) `VaultLeaseContainer` — это deprecated класс из Spring Vault 1.x, в современных версиях нужно использовать только `@Scheduled` с ручным `vaultTemplate.read()` — ❌ Неверно
>
> **Что на самом деле:** `SecretLeaseContainer` (полное имя в современном API) — активно поддерживаемый компонент Spring Vault. Ручной `@Scheduled` не знает о lease lifecycle: он не различает «нужен renew» от «нужен новый lease после max_ttl», не получает события от Vault.
>
> **Откуда путаница:** многие туториалы показывают ручной `@Scheduled` как «простой» подход, и это создаёт впечатление, что специализированный container не нужен.
>
> **Если бы это было правдой:** Spring Vault документация и кодовая база не содержали бы `SecretLeaseContainer` как central abstraction для dynamic secrets.
>
> ---
>
> #### D) `VaultLeaseContainer` нужен только для KV v2 — для dynamic secrets используется отдельный механизм через `@DynamicSecret` — ❌ Неверно
>
> **Что на самом деле:** ровно наоборот — `SecretLeaseContainer` нужен **только для dynamic secrets** (database, aws, pki, ssh engines), потому что только у них есть lease с TTL. KV v1/v2 хранят статичные значения без lease — для их «обновления» используется `@RefreshScope` + `/actuator/refresh`. Аннотации `@DynamicSecret` в Spring Vault не существует.
>
> **Откуда путаница:** путаница между понятиями «динамичности» (KV v2 versioning) и «dynamic secrets» (TTL-credentials).
>
> **Если бы это было правдой:** Spring Vault имел бы две параллельные иерархии классов для одной задачи.
>
> **Связанные вопросы:** [[Q4]] — dynamic secrets; [[Q11]] — автоматическая ротация; [[Q3]] — `@Value` resolution

## Q6. Как работает Transit Secrets Engine (шифрование как сервис)?

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
>
> **Вопрос:** Какая ключевая идея Transit Secrets Engine и чем он отличается от обычного шифрования через `javax.crypto` в приложении?
>
> ---
>
> #### A) Transit Engine хранит зашифрованные данные в Vault, приложение отправляет plaintext и получает обратно ссылку на зашифрованную запись — ❌ Неверно
>
> **Что на самом деле:** Transit Engine **никогда не хранит ни plaintext, ни ciphertext**. Это stateless encryption service: приложение шлёт plaintext, получает ciphertext (с префиксом `vault:v1:`), хранит ciphertext в своей БД. Для расшифровки приложение шлёт ciphertext обратно, получает plaintext. Vault хранит **только ключи**.
>
> **Откуда путаница:** ассоциация с KV engine, который действительно хранит данные.
>
> **Если бы это было правдой:** Vault при компрометации содержал бы всю чувствительную базу клиентов целиком — это противоречит security-модели «Vault держит ключи, БД держит данные».
>
> ---
>
> #### B) Transit Engine — encryption-as-a-service: приложение получает ciphertext через Vault API, ключ никогда не покидает Vault, ротация ключа не требует перешифрования старых данных (ciphertext помечен версией ключа `vault:v1:...`) — ✓ Верно
>
> **Развёрнутое объяснение:** Transit решает задачу «как шифровать PII без управления ключами в приложении». Принцип: (1) оператор создаёт named key через `vault write -f transit/keys/customer-pii type=aes256-gcm96`; (2) приложение вызывает `vault write transit/encrypt/customer-pii plaintext=<base64>` и получает `vault:v1:<base64-ciphertext>` где `v1` — версия ключа; (3) ciphertext сохраняется в БД; (4) для расшифровки — `vault write transit/decrypt/customer-pii ciphertext=...`. Ключ никогда не покидает Vault, что критично: компрометация app pod-а не даёт ничего — без действующего Vault-токена нельзя расшифровать ничего. Ротация: `vault write -f transit/keys/customer-pii/rotate` создаёт `v2`, новые шифрования используют `v2`, старые `vault:v1:` ciphertext-ы расшифровываются автоматически (Vault помнит все версии). Convergent encryption и derived keys дают детерминированное шифрование для поиска по зашифрованным полям.
>
> **Пример:**
> ```java
> @Service
> @RequiredArgsConstructor
> public class CustomerPiiEncryptionService {
>     private final VaultTemplate vaultTemplate;
>     private static final String KEY_NAME = "customer-pii";
>
>     public String encryptPassport(String passportNumber) {
>         TransitOperations transit = vaultTemplate.opsForTransit();
>         Ciphertext result = transit.encrypt(KEY_NAME, Plaintext.of(passportNumber));
>         return result.getCiphertext();  // "vault:v3:Hh7G..."
>     }
>
>     public String decryptPassport(String ciphertext) {
>         TransitOperations transit = vaultTemplate.opsForTransit();
>         return transit.decrypt(KEY_NAME, Ciphertext.of(ciphertext)).asString();
>     }
>
>     // Запускается раз в квартал — старые данные продолжают читаться
>     public void rotateKey() {
>         vaultTemplate.opsForTransit().rotate(KEY_NAME);
>     }
> }
> ```
>
> **Когда применять:** PII (паспорта, ИНН, карты), GDPR compliance (право на удаление = revoke key version), мульти-сервисная архитектура с единой политикой шифрования, audit-логи всех encrypt/decrypt операций, FIPS 140-2 compliance через Vault HSM-backed keys.
>
> **Подводные камни:** latency — каждый encrypt/decrypt это network call в Vault (batch API `transit/encrypt` для bulk); Vault становится hot dependency — нужно HA + caching стратегия для read-heavy decrypt; convergent encryption (детерминированное) уменьшает безопасность ради возможности поиска — использовать только когда необходимо; key deletion навсегда блокирует доступ к данным (нужны backups через `transit/backup/<key>`).
>
> ---
>
> #### C) Transit Engine — это TLS-туннель для шифрования трафика между микросервисами, замена mTLS — ❌ Неверно
>
> **Что на самом деле:** Transit Engine шифрует **данные**, а не транспорт. Для шифрования трафика используется TLS через Vault PKI (issue certificates), это разные engines с разными задачами.
>
> **Откуда путаница:** слово «transit» воспринимается как «in-transit encryption» (TLS).
>
> **Если бы это было правдой:** существование отдельного PKI engine для выпуска TLS-сертификатов было бы избыточным.
>
> ---
>
> #### D) Transit Engine — устаревший механизм из Vault 0.x, в современных версиях рекомендуется делать AES-шифрование в Java через `Cipher` и хранить ключ в Vault KV — ❌ Неверно
>
> **Что на самом деле:** Transit Engine — активно развиваемая часть Vault. Хранение AES-ключа в KV и шифрование в приложении ломает главное преимущество: компрометация app pod-а = чтение ключа из app memory = расшифровка всей БД. С Transit ключ никогда не попадает в приложение.
>
> **Откуда путаница:** «зачем сетевой round-trip, если можно зашифровать локально» — игнорирует security threat-модель.
>
> **Если бы это было правдой:** банки и healthcare не использовали бы Transit для PCI DSS / HIPAA compliance.
>
> **Связанные вопросы:** [[Q1]] — обзор Vault; [[Q8]] — PKI engine; [[Q15]] — best practices

## Q7. Какие методы аутентификации поддерживает Spring Vault?

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
>
> **Вопрос:** Какой метод аутентификации Spring Vault предпочтителен для production-микросервиса в Kubernetes и почему `TOKEN` не подходит?
>
> ---
>
> #### A) `TOKEN` — самый простой и безопасный метод: достаточно установить `VAULT_TOKEN` через Kubernetes Secret и приложение готово к работе — ❌ Неверно
>
> **Что на самом деле:** статический токен — антипаттерн для production. Токен попадает в Kubernetes Secret (зашифрован в etcd, но доступен любому с RBAC на pod), не имеет автоматической ротации, при компрометации pod-а действителен до явного revoke. Это эквивалент пароля БД в env vars — задача, которую Vault призван решить.
>
> **Откуда путаница:** простота настройки в туториалах — токен показывают первым как «hello world».
>
> **Если бы это было правдой:** Vault не разрабатывал бы 15+ alternative auth methods (AppRole, Kubernetes, AWS IAM, GCP, JWT/OIDC).
>
> ---
>
> #### B) Для Kubernetes-микросервиса — `KUBERNETES` auth: приложение использует свой Service Account JWT как proof of identity, Vault проверяет JWT через TokenReview API K8s и выдаёт короткоживущий Vault-токен; токен ротируется автоматически без участия CI/CD — ✓ Верно
>
> **Развёрнутое объяснение:** Kubernetes auth работает так. (1) Оператор настраивает в Vault: `vault write auth/kubernetes/config token_reviewer_jwt=@sa-token kubernetes_host=...` и `vault write auth/kubernetes/role/payment-app bound_service_account_names=payment-sa bound_service_account_namespaces=prod policies=payment-policy ttl=1h`. (2) Pod монтирует свой JWT в `/var/run/secrets/kubernetes.io/serviceaccount/token`. (3) Spring Vault при старте читает JWT и вызывает `vault write auth/kubernetes/login role=payment-app jwt=<jwt>`. (4) Vault через TokenReview API проверяет JWT в API server, убеждается что pod бежит с правильным SA в правильном namespace, выдаёт Vault-токен с TTL=1h. (5) Spring Vault автоматически renew-ит токен в фоне. Идентичность — это identity самого K8s; компрометация требует захвата SA в namespace, что значительно сложнее статичного токена.
>
> **Пример:**
> ```yaml
> # bootstrap.yml
> spring:
>   application:
>     name: payment-service
>   cloud:
>     vault:
>       host: vault.prod.internal
>       port: 8200
>       scheme: https
>       authentication: KUBERNETES
>       kubernetes:
>         role: payment-app                 # совпадает с auth/kubernetes/role/...
>         kubernetes-path: kubernetes        # mount path в Vault
>         service-account-token-file: /var/run/secrets/kubernetes.io/serviceaccount/token
>       kv:
>         enabled: true
>         backend: secret
>         application-name: payment-service
> ```
> ```yaml
> # k8s manifests/payment-deployment.yaml
> apiVersion: apps/v1
> kind: Deployment
> spec:
>   template:
>     spec:
>       serviceAccountName: payment-sa     # bound в Vault role
>       containers:
>       - name: app
>         image: payment-service:1.2.3
> ```
>
> **Когда применять:** Kubernetes-микросервисы (всегда предпочитать KUBERNETES auth); вне K8s — APPROLE (Role ID в образе, Secret ID через CI/CD wrapping); EC2/Lambda — AWS IAM auth через instance identity; on-premise legacy — TLS client cert auth.
>
> **Подводные камни:** TokenReview API K8s должен быть доступен с Vault-нод (обычно через master endpoint); SA-токен с Kubernetes 1.21+ это short-lived projected token — нужен обновлённый Spring Vault для повторного чтения файла; `bound_service_account_namespaces=*` — антипаттерн, всегда указывать конкретный namespace; Vault-токен после login имеет свой TTL — Spring Vault renew-ит его, но если pod не активен дольше `max_ttl`, придётся re-login (Spring Vault делает это автоматически).
>
> ---
>
> #### C) Для production всегда нужно использовать `LDAP` — корпоративная директория обеспечивает единую точку управления identity — ❌ Неверно
>
> **Что на самом деле:** LDAP — human auth (developer/operator аутентифицируется в Vault для управления). Для машинной аутентификации (приложение → Vault) используются machine-identity методы: Kubernetes, AppRole, AWS IAM, GCP. Приложение не имеет «логина и пароля LDAP».
>
> **Откуда путаница:** LDAP популярен в enterprise для SSO, и команды по инерции пытаются использовать его и для приложений.
>
> **Если бы это было правдой:** каждый pod нуждался бы в выделенном LDAP-пользователе и пароле, что воссоздаёт проблему «пароли в конфигах».
>
> ---
>
> #### D) Любой метод одинаково безопасен — выбор только эстетический, главное чтобы Vault был включён — ❌ Неверно
>
> **Что на самом деле:** выбор auth-метода — это центральное security-решение. TOKEN — самый слабый (статичный credential), AppRole — promotion-friendly, Kubernetes — лучший для K8s (identity делегируется control-plane), AWS IAM — лучший в AWS (identity = instance role). Каждый метод имеет свои threat-модели и трейдоффы.
>
> **Откуда путаница:** упрощение «Vault установлен — security готов».
>
> **Если бы это было правдой:** документация Vault не содержала бы 100+ страниц про auth methods и threat models.
>
> **Связанные вопросы:** [[Q2]] — настройка Spring Vault; [[Q1]] — обзор Vault; [[Q15]] — best practices

## Q8. Как использовать Vault PKI для динамических TLS-сертификатов?

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
>
> **Вопрос:** В чём ключевое преимущество Vault PKI engine перед традиционными статичными TLS-сертификатами от внутреннего CA?
>
> ---
>
> #### A) Vault PKI бесплатный, а внутренний CA на базе OpenSSL платный — это единственная разница — ❌ Неверно
>
> **Что на самом деле:** оба варианта могут быть бесплатными. Главное различие — automation и lifetime: Vault PKI на лету issue-ит сертификаты с короткими TTL (часы/дни), интегрирует ACL через Vault roles, automates renewal через Vault Agent. Статичный CA выдаёт годовые сертификаты, требует ручного rotation, не имеет встроенного механизма authorization.
>
> **Откуда путаница:** упрощённое сравнение «коммерческое vs open-source».
>
> **Если бы это было правдой:** существование dedicated PKI engine не имело бы смысла — достаточно было бы скрипта на OpenSSL.
>
> ---
>
> #### B) Vault PKI выдаёт короткоживущие сертификаты (часы/дни) on-demand с автоматическим renewal — это позволяет zero-downtime ротацию mTLS, узкие blast radius при компрометации, fine-grained ACL через Vault policies на каждый CN/SAN — ✓ Верно
>
> **Развёрнутое объяснение:** Vault PKI работает так. (1) Оператор создаёт CA через `vault write pki/root/generate/internal common_name=internal-ca` (или импортирует существующий). (2) Создаётся role: `vault write pki/roles/services-role allowed_domains=svc.cluster.local allow_subdomains=true max_ttl=24h`. (3) Приложение через Spring Vault вызывает `vaultTemplate.opsForPki().issueCertificate("pki/issue/services-role", request)` — Vault генерирует приватный ключ + сертификат, подписывает CA, возвращает x509 chain + PEM ключ. (4) Приложение использует сертификат для mTLS до истечения TTL, затем requests новый. Преимущества vs статичный CA: при компрометации сервиса blast radius ограничен временем жизни сертификата (часы), не годами; revocation через CRL/OCSP не нужна — сертификат сам истекает; идентичность приложения = Vault policy, проверяется при каждом issue; обновление CA не требует касания всех клиентов одновременно (по мере истечения).
>
> **Пример:**
> ```java
> @Service
> @RequiredArgsConstructor
> public class TlsCertificateProvider {
>     private final VaultTemplate vaultTemplate;
>
>     public KeyStore obtainServerKeyStore(String commonName) throws Exception {
>         VaultCertificateRequest request = VaultCertificateRequest.builder()
>             .commonName(commonName)                         // payment.svc.cluster.local
>             .altNames(List.of("payment", "payment.prod"))
>             .ttl(Duration.ofHours(24))
>             .build();
>         VaultCertificateResponse response =
>             vaultTemplate.opsForPki().issueCertificate("pki/issue/services-role", request);
>
>         CertificateBundle bundle = response.getRequiredData();
>         KeyStore keyStore = KeyStore.getInstance("PKCS12");
>         keyStore.load(null, null);
>         keyStore.setKeyEntry("server",
>             bundle.getPrivateKeySpec(),
>             "changeit".toCharArray(),
>             bundle.getX509CertificateChain().toArray(new X509Certificate[0]));
>         return keyStore;
>     }
> }
> ```
>
> **Когда применять:** mTLS между микросервисами в Kubernetes/Consul Connect; сертификаты для admin-доступа (короткие TTL — часы); IoT-устройства с автоматическим enrollment; intermediate CA для сегментации (Vault PKI as intermediate, подписанный корневым HSM-CA).
>
> **Подводные камни:** clock skew между Vault и клиентом — сертификат с TTL=1h может стать невалидным из-за разницы во времени; renewal должен начинаться задолго до истечения (хотя бы за 20% TTL); приватный ключ передаётся через сеть — Vault и клиент должны быть на TLS; root CA private key — критический secret, лучше HSM-backed или keep offline; rotation CA — нетривиально, нужны cross-signing или dual-CA период.
>
> ---
>
> #### C) Vault PKI работает только с самоподписанными сертификатами и не может выпускать сертификаты, доверенные внешними системами — ❌ Неверно
>
> **Что на самом деле:** Vault PKI может работать как intermediate CA, подписанный публичным CA (DigiCert, Let's Encrypt через external signing). Корневой CA можно держать offline, импортировать в Vault как intermediate с ограниченной name constraints.
>
> **Откуда путаница:** простые tutorials показывают только self-signed root, создавая впечатление limitation.
>
> **Если бы это было правдой:** Vault PKI был бы непригоден для публичных endpoint-ов, что противоречит реальной enterprise-практике.
>
> ---
>
> #### D) Vault PKI хранит уже выпущенные сертификаты в KV — приложение просто читает их оттуда — ❌ Неверно
>
> **Что на самом деле:** Vault PKI генерирует сертификаты **on-demand** при каждом `pki/issue/<role>`. Хранение в KV — антипаттерн (если приватный ключ в storage, любой с read-доступом получает ключ). Vault PKI хранит только CA private key и метаданные выпущенных сертификатов для CRL.
>
> **Откуда путаница:** ассоциация с обычным workflow «admin генерирует cert → сохраняет в storage → приложение читает».
>
> **Если бы это было правдой:** Vault PKI терял бы основное преимущество — ephemeral credentials.
>
> **Связанные вопросы:** [[Q4]] — dynamic secrets; [[Q6]] — Transit engine; [[Q15]] — best practices

## Q9. Как Spring Cloud Vault интегрируется с Spring Boot PropertySource?

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
