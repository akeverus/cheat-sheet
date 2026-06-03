---
title: "Вопросы на собеседовании: HashiCorp Vault"
description: "HashiCorp Vault: secrets management, KV engine, dynamic secrets, auth methods, policies, leases, transit encryption, PKI, integration с K8s, alternatives"
tags:
  - interview
  - devops
  - vault-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "HashiCorp Vault"
  - "Vault interview"
  - "HashiCorp Vault interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `HashiCorp Vault`

`HashiCorp Vault` — система управления секретами (secrets management). Централизованное хранилище и контроль доступа для credentials, API-ключей, сертификатов. Использует **динамические секреты**, **шифрование как сервис** (encryption-as-a-service) и **доступ на основе identity**. В 2023 году сменилась лицензия (BSL), появился форк **OpenBao** (Linux Foundation).

## Полезные ссылки

### Официальная документация и авторитетные источники

- [HashiCorp Vault Documentation](https://developer.hashicorp.com/vault/docs)
- [Vault Tutorials](https://developer.hashicorp.com/vault/tutorials)
- [OpenBao (open-source fork)](https://openbao.org/)
- [Vault Best Practices](https://learn.hashicorp.com/tutorials/vault/production-hardening)
- [SPIFFE/SPIRE](https://spiffe.io/) — identity standard

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Vault?](#q1--что-такое-vault)
- [Q2. (!) Статические vs динамические секреты — в чём разница?](#q2--статические-vs-динамические-секреты--в-чём-разница)
- [Q3. Архитектура Vault: sealed/unsealed и storage backend?](#q3-архитектура-vault-sealedunsealed-и-storage-backend)

**Secrets engines**
- [Q4. (!) KV (Key-Value) engine: чем v1 отличается от v2?](#q4--kv-key-value-engine-чем-v1-отличается-от-v2)
- [Q5. Database engine: как Vault выдаёт динамические credentials к БД?](#q5-database-engine-как-vault-выдаёт-динамические-credentials-к-бд)
- [Q6. AWS engine: динамические IAM-credentials?](#q6-aws-engine-динамические-iam-credentials)
- [Q7. (!) PKI engine: выпуск TLS-сертификатов?](#q7--pki-engine-выпуск-tls-сертификатов)
- [Q8. Transit engine: шифрование как сервис?](#q8-transit-engine-шифрование-как-сервис)

**Auth methods**
- [Q9. (!) Аутентификация по токенам?](#q9--аутентификация-по-токенам)
- [Q10. Что такое AppRole и зачем он нужен?](#q10-что-такое-approle-и-зачем-он-нужен)
- [Q11. (!) Как работает Kubernetes auth?](#q11--как-работает-kubernetes-auth)
- [Q12. IAM-аутентификация в AWS, Azure, GCP?](#q12-iam-аутентификация-в-aws-azure-gcp)
- [Q13. Аутентификация через JWT и OIDC?](#q13-аутентификация-через-jwt-и-oidc)

**Policies и access control**
- [Q14. (!) Политики Vault на HCL?](#q14--политики-vault-на-hcl)
- [Q15. Identity в Vault: entities и groups?](#q15-identity-в-vault-entities-и-groups)

**Operations**
- [Q16. (!) Запечатывание и распечатывание (sealing / unsealing)?](#q16--запечатывание-и-распечатывание-sealing--unsealing)
- [Q17. Auto-unseal через KMS?](#q17-auto-unseal-через-kms)
- [Q18. Высокая доступность (HA): Raft или Consul backend?](#q18-высокая-доступность-ha-raft-или-consul-backend)
- [Q19. Резервное копирование и аварийное восстановление?](#q19-резервное-копирование-и-аварийное-восстановление)

**Integration с приложениями**
- [Q20. (!) Что делает Vault Agent?](#q20--что-делает-vault-agent)
- [Q21. Развёртывание Vault в Kubernetes?](#q21-развёртывание-vault-в-kubernetes)
- [Q22. Sidecar injector в Kubernetes?](#q22-sidecar-injector-в-kubernetes)
- [Q23. Что такое External Secrets Operator (ESO)?](#q23-что-такое-external-secrets-operator-eso)

**Production**
- [Q24. (!) Рекомендации для production?](#q24--рекомендации-для-production)
- [Q25. (!) Vault против альтернатив (AWS Secrets Manager, Doppler)?](#q25--vault-против-альтернатив-aws-secrets-manager-doppler)
- [Q26. Смена лицензии на BSL и форк OpenBao?](#q26-смена-лицензии-на-bsl-и-форк-openbao)

## Q1. (!) Что такое Vault?

**HashiCorp Vault — централизованная система управления секретами** (с 2015 года). Решает базовую проблему: секреты (пароли, API-ключи, сертификаты) не должны лежать в коде, `.env`-файлах или конфиг-картах, где их легко украсть и невозможно проконтролировать. Vault даёт единую точку, через которую секреты выдаются, шифруются, отзываются и логируются.

**Ключевые возможности:**
- **Централизованное хранилище секретов** — вместо `.env`-файлов и захардкоженных credentials.
- **Динамические секреты** — короткоживущие, генерируются под запрос и автоматически отзываются.
- **Доступ на основе identity** — кто и к каким путям имеет доступ, задаётся гранулярными политиками.
- **Шифрование как сервис** — приложение шлёт данные в API encrypt/decrypt, ключи остаются внутри Vault.
- **PKI** — Vault выступает удостоверяющим центром и выпускает TLS-сертификаты.
- **Audit logs** — каждое обращение к секрету фиксируется, что даёт полную прослеживаемость.

**Где применяют:**
- Замена захардкоженных API-ключей и паролей к БД.
- Выпуск короткоживущих credentials к БД под конкретное приложение.
- Управление TLS-сертификатами.
- Шифрование данных на стороне приложения.
- Управление SSH-доступом.

Главная идея — **сместиться от статических секретов-«вечных» к динамическим и короткоживущим**, чтобы утечка одного credential не превращалась в бессрочный доступ.

## Q2. (!) Статические vs динамические секреты — в чём разница?

**Коротко:** статический секрет Vault хранит как есть и отдаёт всем, кому положено; динамический — генерирует уникальный экземпляр под каждый запрос, на короткое время, и сам же его потом отзывает.

**Статические секреты (static secrets):**
- Хранятся как есть (через KV engine).
- Значение не меняется со временем.
- Ротируются вручную.
- Примеры: API-ключи, значения конфигурации.

**Динамические секреты (dynamic secrets):**
- **Генерируются под запрос (on-demand)** — каждый потребитель получает свой credential.
- **Короткий TTL** (минуты/часы).
- **Автоматически отзываются** по истечении TTL.
- Примеры: credentials к БД (Vault создаёт отдельного пользователя по запросу), ключи AWS.

```bash
# Dynamic DB credentials
vault read database/creds/my-role
# Returns NEW user/password, expires in 1 hour
```

**Почему это важно:** у динамических секретов **ограниченный ущерб при компрометации** — credential короткоживущий, уникален для потребителя и отзывается. Утёкший пароль перестаёт работать через час, а по логам видно, чей именно экземпляр скомпрометирован. Статический же секрет, попав не в те руки, действует до ручной ротации и неотличим между потребителями.

## Q3. Архитектура Vault: sealed/unsealed и storage backend?

**Суть:** Vault шифрует все данные мастер-ключом, а сам мастер-ключ при старте недоступен. Поэтому Vault живёт в двух состояниях, и storage backend хранит только зашифрованный шифротекст.

**Состояния Vault:**
- **Sealed** (запечатан) — данные на диске зашифрованы, мастер-ключ Vault недоступен, запросы не обслуживаются. Это состояние по умолчанию при старте.
- **Unsealed** (распечатан) — мастер-ключ собран в памяти, Vault может расшифровывать данные и работать.

**Распечатывание (unsealing)** требует **unseal-ключей**. Применяется Shamir's Secret Sharing: мастер-ключ делится на N частей, и для сборки нужно пороговое число M из N. Так ни один человек в одиночку не может распечатать Vault.

**Storage backend (бэкенд хранения):**
- Vault — это слой шифрования и контроля доступа; сами байты он хранит во внешнем бэкенде.
- Варианты: **Integrated Storage (Raft, рекомендуется)**, Consul, файловая система, S3, MySQL, PostgreSQL и т. д.
- Всё **шифруется перед записью** в бэкенд, поэтому доступ к самому хранилищу не даёт доступа к секретам.

**Основные компоненты:**
- **Vault Server** — ядро, обрабатывающее запросы.
- **Storage backend** — где лежит зашифрованное состояние.
- **Auth methods** — как клиенты доказывают, кто они.
- **Secrets engines** — что Vault выдаёт (KV, БД, PKI и т. д.).
- **Audit devices** — куда пишется журнал обращений.

## Q4. (!) KV (Key-Value) engine: чем v1 отличается от v2?

KV — это движок для хранения **статических** секретов «ключ-значение». Главное отличие версий: v2 добавляет версионирование и историю, v1 просто перезаписывает значение.

**KV v1** (устаревший):
- Простой key-value.
- Без версионирования.
- Запись затирает предыдущее значение без следа.

**KV v2** (рекомендуется):
- **Версионирование** — хранится история значений, можно прочитать любую прошлую версию.
- **Мягкое удаление (soft delete) + восстановление** — удалённую версию можно вернуть.
- **Метаданные** — CAS (check-and-set против гонок записи) и кастомные метаданные.
- **Subkeys** — доступ к структуре секрета без раскрытия значений.

```bash
# Enable KV v2
vault secrets enable -version=2 kv

# Write secret
vault kv put kv/myapp/db password=secret123

# Read
vault kv get kv/myapp/db

# Read previous version
vault kv get -version=1 kv/myapp/db
```

**Рекомендация:** для новых развёртываний всегда выбирайте KV v2 — версионирование и восстановление окупаются при любой ошибочной перезаписи секрета.

## Q5. Database engine: как Vault выдаёт динамические credentials к БД?

```bash
# Configure engine
vault secrets enable database

vault write database/config/postgres \
  plugin_name=postgresql-database-plugin \
  connection_url="postgres://vault@localhost/myapp" \
  allowed_roles="readonly,readwrite"

# Define role
vault write database/roles/readonly \
  db_name=postgres \
  creation_statements="CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}'; GRANT SELECT ON ALL TABLES IN SCHEMA public TO \"{{name}}\";" \
  default_ttl=1h \
  max_ttl=24h

# Get dynamic credentials
vault read database/creds/readonly
# Returns username + password, valid 1 hour
```

**Как это работает по шагам:** на запрос `database/creds/<role>` Vault сам выполняет в БД `creation_statements`, заводя нового пользователя с ограниченными правами. Приложение получает свежие username/password, работает с ними, а по истечении TTL Vault удаляет этого пользователя из БД. Никто не хранит постоянный пароль к базе — Vault держит лишь админ-подключение для управления.

**Зачем так:** у каждого приложения свой короткоживущий доступ, утечка пароля локализуется и сама протухает, а в логах БД видно, под каким пользователем шли запросы.

**Поддерживаемые БД:** PostgreSQL, MySQL, Mongo, Cassandra, Oracle, Redis, MS-SQL и др.

## Q6. AWS engine: динамические IAM-credentials?

```bash
vault secrets enable aws
vault write aws/config/root \
  access_key=... secret_key=... region=us-east-1

vault write aws/roles/my-role \
  credential_type=iam_user \
  policy_document=@policy.json

# Get credentials
vault read aws/creds/my-role
# Returns ephemeral AWS access_key + secret
```

Та же модель, что и у database engine, но для облака: Vault динамически создаёт **IAM-пользователя** (или выдаёт временные STS-токены) по настроенной IAM-политике. Приложение их использует, а после истечения TTL Vault сам удаляет IAM-пользователя. Так в AWS не остаётся постоянных ключей.

**Сценарий применения:** разработчику нужен доступ к AWS на 1 час для отладки — Vault выдаёт эфемерные credentials, которые автоматически исчезнут, и забывать «отозвать ключ вручную» не приходится.

## Q7. (!) PKI engine: выпуск TLS-сертификатов?

**PKI engine превращает Vault в удостоверяющий центр (Certificate Authority, CA).** Вместо ручного выпуска и долгоживущих сертификатов Vault по запросу подписывает короткоживущие сертификаты для сервисов, а отзыв и ротация становятся автоматическими.

```bash
vault secrets enable pki

# Generate root CA
vault write pki/root/generate/internal \
  common_name="my-ca" ttl=87600h

# Issue certificate
vault write pki/issue/my-role \
  common_name="api.example.com" ttl=24h
```

**Сценарии применения:**
- mTLS между микросервисами — выпуск короткоживущих сертификатов под каждый сервис.
- HTTPS-сертификаты для внутренних нужд (альтернатива Let's Encrypt, который выпускает только публичные).
- Подпись кода (code signing).
- SSH CA для подписи SSH-ключей.

**Интеграция:** `cert-manager` в K8s умеет использовать Vault PKI как issuer, автоматически продлевая сертификаты подов.

## Q8. Transit engine: шифрование как сервис?

**Transit engine — это шифрование как сервис: Vault шифрует и расшифровывает данные по API, но сами данные никогда не хранит.** Приложение не управляет ключами и не реализует криптографию само — оно отдаёт plaintext, получает ciphertext (и наоборот), а ключи остаются под управлением Vault.

```bash
vault secrets enable transit
vault write -f transit/keys/my-key

# Encrypt
vault write transit/encrypt/my-key plaintext=$(echo -n "sensitive" | base64)
# Returns ciphertext

# Decrypt
vault write transit/decrypt/my-key ciphertext="vault:v1:..."
# Returns base64 plaintext
```

**Что это даёт приложению:**
- Vault хранит и защищает ключи шифрования.
- Приложение отправляет данные и получает их в зашифрованном/расшифрованном виде.
- Ключи никогда не покидают Vault — даже скомпрометированное приложение их не вынесет.

**Ротация ключей** прозрачная: префикс `vault:v1:` в ciphertext помечает версию ключа, поэтому после ротации старые данные по-прежнему расшифровываются, а новые шифруются новым ключом.

**Сценарий применения:** соответствие требованиям PCI / HIPAA — приложение шифрует чувствительные поля в БД через Vault, не реализуя криптографию и управление ключами у себя.

## Q9. (!) Аутентификация по токенам?

**Токены — базовый механизм аутентификации Vault.** Любой другой auth-метод (AppRole, Kubernetes, OIDC) в итоге выдаёт клиенту именно Vault-токен, с которым тот ходит за секретами. Токен несёт привязанные политики и TTL.

```bash
# Get token
vault token create -policy=my-policy -ttl=1h

# Use
export VAULT_TOKEN="hvs.CAESI..."
vault read kv/myapp/db
```

**Свойства токена:**
- **TTL** — токен автоматически истекает.
- **Политики** — определяют, что токену разрешено.
- **Продление / отзыв** — токен можно продлить (renew) до max TTL или отозвать досрочно.
- **Типы:** обычные, periodic, batch.

**Periodic-токены** не имеют max TTL и продлеваются бесконечно, пока их регулярно обновляют — удобно для долгоживущих сервисов. **Batch-токены** легковесны (не пишутся в storage), хороши для большого числа короткоживущих операций, но не поддерживают часть возможностей обычных токенов.

## Q10. Что такое AppRole и зачем он нужен?

**AppRole — auth-метод для приложений (machine-to-machine), у которых нет облачного или K8s-identity.** Приложение доказывает, кто оно, парой Role ID + Secret ID и получает Vault-токен.

```bash
vault auth enable approle

vault write auth/approle/role/my-app \
  policies=app-policy \
  token_ttl=1h \
  token_max_ttl=4h

# Get role-id (knowledge factor)
vault read auth/approle/role/my-app/role-id

# Get secret-id (possession factor)
vault write -f auth/approle/role/my-app/secret-id

# Login
vault write auth/approle/login \
  role_id=... secret_id=...
```

**Два фактора (по аналогии с логином/паролем):**
- **Role ID** — идентифицирует приложение, долгоживущий, не секрет сам по себе.
- **Secret ID** — собственно credential, короткоживущий и одноразовый/ограниченного использования.

**Как разносят факторы:** Role ID кладут в конфигурацию приложения, а Secret ID доставляют отдельным каналом (например, как CI/CD-секрет или через доверенного посредника). Смысл разделения — чтобы утечка одного фактора не давала доступ: компрометация Role ID без Secret ID бесполезна, и наоборот.

## Q11. (!) Как работает Kubernetes auth?

**Kubernetes auth аутентифицирует поды по их service account-токенам.** Под уже имеет смонтированный JWT-токен своего service account — Vault проверяет его подлинность через Kubernetes API и, если SA соответствует роли, выдаёт Vault-токен. Никаких отдельных секретов в под раздавать не нужно.

```bash
vault auth enable kubernetes
vault write auth/kubernetes/config \
  kubernetes_host="https://kubernetes.default.svc"

vault write auth/kubernetes/role/my-app \
  bound_service_account_names=my-app \
  bound_service_account_namespaces=default \
  policies=app-policy ttl=1h

# In pod
vault write auth/kubernetes/login \
  role=my-app jwt=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
```

**Поток:** под берёт смонтированный SA-токен → отправляет его в Vault на `auth/kubernetes/login` → Vault валидирует токен через Kubernetes TokenReview API и сверяет SA с `bound_service_account_*` роли → возвращает Vault-токен → под читает секреты.

**Рекомендация:** это предпочтительный способ аутентификации приложений в Kubernetes — identity берётся из самого кластера, без захардкоженных credentials.

## Q12. IAM-аутентификация в AWS, Azure, GCP?

**Cloud IAM auth аутентифицирует нагрузки (workloads) по их облачной IAM-identity.** EC2-инстанс, Lambda или managed identity уже «принадлежат» облаку — Vault проверяет эту принадлежность через API провайдера и выдаёт токен. Общих секретов между приложением и Vault не существует вовсе.

```bash
vault auth enable aws

vault write auth/aws/role/my-app \
  auth_type=iam \
  bound_iam_principal_arn=arn:aws:iam::123:role/my-app \
  policies=app-policy ttl=1h

# In EC2 / Lambda
vault login -method=aws role=my-app
```

**Преимущество:** identity берётся из облака и проверяется Vault, поэтому нет ни одного секрета, который можно было бы украсть или забыть ротировать.

Та же идея реализована для Azure (managed identity) и GCP (service accounts).

## Q13. Аутентификация через JWT и OIDC?

**OIDC-метод** позволяет входить в Vault через внешнего OIDC-провайдера (Auth0, Okta, Google, GitHub). Это интерактивный браузерный флоу для людей — фактически SSO в Vault.

```bash
vault auth enable oidc
vault write auth/oidc/config \
  oidc_discovery_url="https://accounts.google.com" \
  oidc_client_id="..." \
  oidc_client_secret="..."

vault login -method=oidc
# Browser opens для login
```

**OIDC = SSO для людей:** сотрудники логинятся в Vault через корпоративный identity-провайдер, отдельные пароли Vault не заводятся.

**JWT auth = тот же механизм для машин:** Vault принимает уже готовый подписанный JWT-токен и проверяет его по JWKS провайдера, без интерактивного браузерного редиректа. Подходит, когда токен выдаёт CI/CD или другая система.

## Q14. (!) Политики Vault на HCL?

**Политика (policy) — это список путей и разрешённых над ними операций, заданный на HCL.** Vault работает по принципу default-deny: что явно не разрешено — запрещено. Политика привязывается к токену (или роли/entity) и определяет его права.

```hcl
# Read-only access к KV
path "kv/data/myapp/*" {
  capabilities = ["read", "list"]
}

# Read & write to specific path
path "kv/data/myapp/db" {
  capabilities = ["create", "read", "update", "delete"]
}

# Deny anything else (default-deny implicit)
```

**Capabilities (возможности на пути):**
- `create`, `read`, `update`, `delete`, `list` — обычные операции над секретами по пути.
- `sudo` — нужен для привилегированных root-защищённых эндпоинтов.
- `deny` — явный запрет, перебивает любые разрешения (полезно вырезать исключение из более широкого правила).

**Применить политику к токену / роли:**
```bash
vault token create -policy=my-policy
```

## Q15. Identity в Vault: entities и groups?

**Identity-слой связывает разные способы входа одного субъекта в единую сущность.** Один человек может логиниться через GitHub, LDAP и OIDC — это разные aliases, но один и тот же **entity**. Политики удобнее вешать на entity/группу, а не на каждый отдельный alias.

```
Entity: alice
  ├── alias: alice@github (GitHub auth)
  ├── alias: alice@company (LDAP auth)
  └── alias: alice@oidc (OIDC auth)
```

**Group** — набор entity (например, «команда платежей»), к которому привязываются общие политики.

**Зачем это нужно:** политики, навешенные на entity/группы, дают централизованное управление доступом — права меняются в одном месте и применяются ко всем способам входа субъекта, независимо от того, через какой auth-метод он зашёл.

## Q16. (!) Запечатывание и распечатывание (sealing / unsealing)?

**Vault стартует запечатанным (sealed):** данные на диске зашифрованы, мастер-ключ недоступен, сервис не обслуживает запросы. Это сделано специально — без явного распечатывания Vault бесполезен для атакующего.

**Чтобы распечатать,** нужно предоставить **пороговое число M** unseal-ключей из общего числа N. Каждый `unseal` добавляет одну долю; когда набрано M, Vault восстанавливает мастер-ключ в памяти и переходит в unsealed.

```bash
# Initialize (generates keys)
vault operator init -key-shares=5 -key-threshold=3
# Returns 5 unseal keys + initial root token
# DISTRIBUTE keys к 5 different operators (Shamir's Secret Sharing)

# Unseal (need 3 keys)
vault operator unseal <key1>
vault operator unseal <key2>
vault operator unseal <key3>
# Now operational
```

**Зачем:** даже если атакующий скомпрометирует сервер Vault — данные зашифрованы, а unseal-ключи распределены между разными людьми.

**Перезапуск** = снова sealed. Production-кластер нужно распечатывать при каждом рестарте.

## Q17. Auto-unseal через KMS?

**Auto-unseal убирает ручное распечатывание:** Vault доверяет хранение части мастер-ключа облачному KMS (AWS KMS, GCP KMS, Azure Key Vault) и распечатывается автоматически при старте. Это решает главную операционную боль — иначе после каждого рестарта пришлось бы вручную собирать кворум unseal-ключей.

```hcl
seal "awskms" {
  region     = "us-east-1"
  kms_key_id = "alias/vault-key"
}
```

**Эффект:** при старте Vault сам обращается к KMS, расшифровывает мастер-ключ и распечатывается — без участия операторов.

**Компромисс:** появляется зависимость от облачного KMS. Если KMS недоступен, Vault остаётся sealed и не поднимется до восстановления KMS.

**Рекомендация:** в production auto-unseal предпочтителен — он сильно упрощает эксплуатацию и автоскейлинг кластера.

## Q18. Высокая доступность (HA): Raft или Consul backend?

**HA в Vault строится вокруг одного active-узла и нескольких standby.** Записи принимает только active; standby готовы перехватить лидерство при его падении. Разница — в том, где хранится реплицируемое состояние.

**Integrated Storage (Raft)** — рекомендуемый подход (с 2020 года):
- Встроен в Vault, отдельных сервисов не нужно.
- Кластер из 3–5 узлов с консенсусом Raft.
- Active/standby: только active принимает записи и реплицирует их на остальных.
- Автоматический failover при потере лидера.

**Consul backend** — более старый подход: состояние и выбор лидера живут во внешнем кластере Consul, который нужно отдельно разворачивать и обслуживать.

```hcl
storage "raft" {
  path = "/vault/data"
  node_id = "vault-node-1"
}
ha_storage "raft" { ... }
```

**Рекомендация:** для новых кластеров используйте Raft (Integrated Storage) — он стал дефолтом, а Consul backend для Vault считается устаревшим и добавляет лишний компонент в эксплуатацию.

## Q19. Резервное копирование и аварийное восстановление?

**База — это снапшоты Raft-состояния**, которые можно снять и восстановить одной командой. Для более серьёзных сценариев Enterprise добавляет репликацию между кластерами.

**Snapshots:**
```bash
vault operator raft snapshot save backup.snap
vault operator raft snapshot restore backup.snap
```

**Disaster Recovery (Enterprise)** — на случай потери целого кластера:
- DR-репликация на вторичный кластер.
- Вторичный кластер — read-only standby.
- При аварии на него можно сделать failover (promote).

**Performance Replication (Enterprise)** — для масштабирования по регионам:
- Активные кластеры в нескольких регионах.
- Локальное чтение секретов рядом с приложениями, без хождения в один центральный кластер.

**Важный нюанс безопасности:** хотя в storage данные зашифрованы, **снапшот при восстановлении даёт доступ к расшифрованному состоянию**, поэтому файлы бэкапов нужно хранить зашифрованными и под строгим контролем доступа.

## Q20. (!) Что делает Vault Agent?

**Vault Agent — вспомогательный демон (или sidecar), который берёт на себя работу с Vault за приложение, не знающее про Vault.** Он сам аутентифицируется, держит токен свежим и рендерит секреты в файлы, которые приложение просто читает.

```hcl
# vault-agent.hcl
auto_auth {
  method "kubernetes" {
    config = {
      role = "my-app"
    }
  }
  sink "file" {
    config = {
      path = "/vault/secret"
    }
  }
}

template {
  source      = "/vault/template/db.tpl"
  destination = "/vault/secret/db.env"
}
```

**Возможности:**
- **Auto-auth** — сам логинится в Vault и автоматически продлевает токен.
- **Templates** — рендерит секреты в файлы (например, в `.env` с переменными окружения) и обновляет их при ротации.
- **Caching** — кэширует ответы Vault, снижая нагрузку на сервер.

**Главное преимущество:** приложение просто читает файл и не содержит ни строчки Vault-SDK — вся интеграция вынесена в агент.

## Q21. Развёртывание Vault в Kubernetes?

**Сам Vault разворачивают в K8s через официальный Helm chart** `hashicorp/vault`. Чарт поднимает HA-кластер на Raft, настраивает auto-unseal, TLS и persistent storage.

```bash
helm install vault hashicorp/vault \
  --set "server.ha.enabled=true" \
  --set "server.ha.replicas=3"
```

**Что настраивает чарт:**
- HA-кластер на Raft (несколько реплик с консенсусом).
- Auto-unseal с облачным KMS, чтобы поды поднимались без ручного распечатывания.
- TLS для API и межузлового трафика.
- Persistent storage для данных Raft.

**Не путать** с Vault Agent Injector (Q22) — этот чарт ставит сам сервер Vault, а инжектор отвечает за доставку секретов в поды приложений.

## Q22. Sidecar injector в Kubernetes?

**Vault Sidecar Injector — это mutating webhook, который по аннотациям пода автоматически добавляет в него sidecar-контейнер с Vault Agent.** Разработчику не нужно вручную прописывать sidecar в манифест — достаточно повесить аннотации, и инжектор сам встроит агент и init-контейнер.

```yaml
# Pod annotations
vault.hashicorp.com/agent-inject: "true"
vault.hashicorp.com/role: "my-app"
vault.hashicorp.com/agent-inject-secret-db: "kv/myapp/db"
vault.hashicorp.com/agent-inject-template-db: |
  {{ with secret "kv/myapp/db" -}}
  DB_USER={{ .Data.data.user }}
  DB_PASSWORD={{ .Data.data.password }}
  {{- end }}
```

**Как это работает:** sidecar (Vault Agent) аутентифицируется по Kubernetes auth, получает секреты, рендерит их по шаблону и пишет в общий с приложением volume — приложение просто читает `/vault/secrets/db`.

**Главный плюс:** в коде приложения нет ни Vault SDK, ни логики аутентификации — только чтение файлов. Минус — на каждый под добавляется лишний контейнер.

## Q23. Что такое External Secrets Operator (ESO)?

**External Secrets Operator (ESO) — оператор Kubernetes, который синхронизирует внешние хранилища секретов (Vault, AWS, GCP, Azure) в нативные K8s Secrets.** Подход иной, чем у sidecar-инжектора: вместо доставки секретов внутрь пода ESO создаёт обычный Secret, а приложения работают с ним как всегда.

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: db-credentials
spec:
  secretStoreRef:
    name: vault-store
  target:
    name: db-secret  # K8s Secret name
  data:
    - secretKey: password
      remoteRef:
        key: kv/myapp/db
        property: password
```

**Как это работает:** по ресурсу `ExternalSecret` оператор читает значения из Vault, складывает их в обычный K8s Secret и держит его в синхронизации. Приложения монтируют Secret как переменные окружения или файлы — без знания о Vault.

**Сравнение с Vault Agent injector:** ESO проще встроить, если у вас уже выстроен процесс вокруг нативных K8s Secrets. Компромисс — расшифрованный секрет лежит в etcd как обычный K8s Secret (с базовым base64-кодированием), тогда как у sidecar-инжектора секрет доставляется напрямую в под, минуя K8s Secret.

## Q24. (!) Рекомендации для production?

Чек-лист сводится к трём принципам: Vault должен **переживать рестарты без ручной возни** (1–2, 7–8), **никому не давать лишнего** (5–6, 12) и **оставлять следы** (4, 10).

1. **Auto-unseal** в production через облачный KMS — чтобы кластер поднимался сам.
2. **HA-кластер** из 3+ узлов на Raft.
3. **TLS повсюду** — на Vault API и в межузловом трафике.
4. **Включённые audit logs** (file, syslog или socket device) — для прослеживаемости каждого обращения.
5. Политики по принципу **наименьших привилегий** (least privilege).
6. **Короткие TTL** для токенов и секретов — чтобы утечка быстро протухала.
7. **Регулярные бэкапы** — зашифрованные снапшоты.
8. Протестированный (а не только написанный) план **disaster recovery**.
9. **Автоматизация ротации секретов**.
10. **Мониторинг** — телеметрия в Prometheus.
11. **Immutable infrastructure** — конфигурация Vault в git, развёртывание через Terraform.
12. **Сетевая изоляция** — приватная подсеть, без публичного IP.

## Q25. (!) Vault против альтернатив (AWS Secrets Manager, Doppler)?

**Ключевая развилка:** managed-решения облаков (Secrets Manager, Key Vault, Secret Manager) проще, но привязаны к своему облаку и слабее в динамических секретах; Vault и его форки — мощнее и cloud-agnostic, но требуют эксплуатации.

| Инструмент | Хостинг | Cloud-agnostic | Динамические секреты | Стоимость |
|------|---------|----------------|-----------------|------|
| **HashiCorp Vault** | Self-host или облако | Да | Да | Бесплатный OSS, Enterprise — платно |
| **AWS Secrets Manager** | Управляется AWS | Нет (только AWS) | Ограниченно (ротация RDS) | $0.40/секрет/месяц |
| **Azure Key Vault** | Управляется Azure | Нет | Ограниченно | $0.03/10K операций |
| **GCP Secret Manager** | Управляется GCP | Нет | Ограниченно | $0.06/секрет/месяц |
| **Doppler** | SaaS | Да | Нет | $0-$25/месяц/пользователь |
| **1Password Secrets** | SaaS | Да | Нет | Подписка |
| **Infisical** | Self-host или SaaS | Да | Да | Бесплатный OSS |
| **OpenBao** | Self-host (форк Vault) | Да | Да | Бесплатный OSS |

**Итог:** Vault — самый мощный инструмент, но и самый сложный в эксплуатации (его нужно разворачивать, распечатывать, держать в HA).

**Как выбирать:**
- **Multi-cloud или тяжёлые сценарии** (динамические секреты, PKI, transit, единая identity-модель) — выигрывает Vault.
- **Простое приложение целиком на AWS** — проще и дешевле взять managed AWS Secrets Manager, не тратя ресурсы на эксплуатацию Vault.

## Q26. Смена лицензии на BSL и форк OpenBao?

**В 2023 году HashiCorp сменила лицензию Vault: MPL 2.0 → BSL** (Business Source License). Это и стало причиной появления форка.

**Что меняет BSL:**
- Нельзя предлагать Vault **как сервис** третьим сторонам без коммерческой лицензии (удар прежде всего по облачным провайдерам и конкурентам HashiCorp).
- В остальном Vault остаётся бесплатным для self-host и модификации.

**Реакция сообщества:** Linux Foundation сделала форк Vault — **OpenBao** (2024 год, полностью под MPL 2.0), чтобы сохранить гарантированно открытую версию.

**Статус OpenBao:**
- Активная разработка под крылом Linux Foundation.
- Совместимость с HCL-конфигами Vault — миграция близка к drop-in.
- Со временем кодовые базы будут расходиться, и полная совместимость не гарантирована.
- Внедрён в IBM, GitLab и других компаниях.

**Как выбирать:**
- **Остаться на HashiCorp Vault** — самый зрелый, больше всего фич, но под BSL.
- **Перейти на OpenBao** — если критична гарантированно полностью open-source лицензия.
- **Наблюдать за экосистемой** — OpenBao набирает популярность, и расклад ещё может измениться.

---

## See also

- [Consul](consul-interview.md) — другой инструмент HashiCorp
- [Ansible](ansible-interview.md) — для распространения секретов
- [Istio](istio-service-mesh-interview.md) — mTLS через Vault PKI
- [Secrets Management](../security/secrets-management-interview.md) — концепции
- [Application Security](../security/application-security-interview.md) — контекст
- [Kubernetes](kubernetes-interview.md) — Vault в K8s
- [Микросервисы](../architecture/microservices-interview.md) — секреты для микросервисов
- [Cloud-native Patterns](../cloud/cloud-native-patterns-interview.md) — управление секретами
- [AWS](../cloud/aws-interview.md) — сравнение с Secrets Manager
- [OAuth2](../security/oauth2-interview.md) — флоу аутентификации
- [JWT](../security/jwt-interview.md) — аутентификация по JWT
- [Zero Trust](../security/zero-trust-interview.md) — Vault как ключевой компонент

- [Ansible](ansible-interview.md)
- [ArgoCD и GitOps](argocd-interview.md)
- [HashiCorp Consul](consul-interview.md)
- [Docker](docker-interview.md)
- [Git](git-interview.md)
- [Gradle и Maven](gradle-maven-interview.md)
