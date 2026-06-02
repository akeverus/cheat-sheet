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
- [Q2. (!) Static vs dynamic secrets?](#q2--static-vs-dynamic-secrets)
- [Q3. Architecture (sealed/unsealed, storage backend)?](#q3-architecture-sealedunsealed-storage-backend)

**Secrets engines**
- [Q4. (!) KV (Key-Value) engine — v1 vs v2?](#q4--kv-key-value-engine--v1-vs-v2)
- [Q5. Database engine (dynamic DB credentials)?](#q5-database-engine-dynamic-db-credentials)
- [Q6. AWS engine (dynamic IAM credentials)?](#q6-aws-engine-dynamic-iam-credentials)
- [Q7. (!) PKI engine (TLS certificates)?](#q7--pki-engine-tls-certificates)
- [Q8. Transit engine (encryption-as-a-service)?](#q8-transit-engine-encryption-as-a-service)

**Auth methods**
- [Q9. (!) Token authentication?](#q9--token-authentication)
- [Q10. AppRole?](#q10-approle)
- [Q11. (!) Kubernetes auth?](#q11--kubernetes-auth)
- [Q12. AWS, Azure, GCP IAM auth?](#q12-aws-azure-gcp-iam-auth)
- [Q13. JWT/OIDC?](#q13-jwtoidc)

**Policies и access control**
- [Q14. (!) Policies (HCL)?](#q14--policies-hcl)
- [Q15. Identity (entities, groups)?](#q15-identity-entities-groups)

**Operations**
- [Q16. (!) Sealing / unsealing?](#q16--sealing--unsealing)
- [Q17. Auto-unseal (KMS)?](#q17-auto-unseal-kms)
- [Q18. HA (Raft, Consul backend)?](#q18-ha-raft-consul-backend)
- [Q19. Backup / disaster recovery?](#q19-backup--disaster-recovery)

**Integration с приложениями**
- [Q20. (!) Vault Agent?](#q20--vault-agent)
- [Q21. Vault Operator (Kubernetes)?](#q21-vault-operator-kubernetes)
- [Q22. Sidecar injector (K8s)?](#q22-sidecar-injector-k8s)
- [Q23. External Secrets Operator?](#q23-external-secrets-operator)

**Production**
- [Q24. (!) Best practices?](#q24--best-practices)
- [Q25. (!) Vault vs alternatives (AWS Secrets Manager, Doppler)?](#q25--vault-vs-alternatives-aws-secrets-manager-doppler)
- [Q26. License change (BSL) и OpenBao fork?](#q26-license-change-bsl-и-openbao-fork)

## Q1. (!) Что такое Vault?

**HashiCorp Vault** — система управления секретами (с 2015 года).

**Ключевые возможности:**
- **Централизованное хранилище секретов** (вместо `.env`-файлов и захардкоженных credentials)
- **Динамические секреты** — короткоживущие, с автоматической ротацией
- **Доступ на основе identity** — гранулярные политики
- **Шифрование как сервис** — API для encrypt/decrypt
- **PKI** — выпуск TLS-сертификатов
- **Audit logs** — полная прослеживаемость

**Применения:**
- Замена захардкоженных API-ключей и паролей к БД
- Выпуск короткоживущих credentials к БД
- Управление TLS-сертификатами
- Шифрование на стороне приложения
- Управление SSH-доступом

## Q2. (!) Static vs dynamic secrets?

**Статические секреты (static secrets):**
- Хранятся как есть (KV engine)
- Значение не меняется со временем
- Ротируются вручную
- Примеры: API-ключи, значения конфигурации

**Динамические секреты (dynamic secrets):**
- **Генерируются по запросу (on-demand)**
- **Короткий TTL** (минуты/часы)
- **Автоматически отзываются**
- Примеры: credentials к БД (Vault создаёт пользователя по запросу), ключи AWS

```bash
# Dynamic DB credentials
vault read database/creds/my-role
# Returns NEW user/password, expires in 1 hour
```

**Преимущество динамических секретов:** **ограниченный ущерб при компрометации** — credentials короткоживущие и отзываются.

## Q3. Architecture (sealed/unsealed, storage backend)?

**Состояния Vault:**
- **Sealed** (запечатан) — данные зашифрованы, запросы не принимаются (состояние по умолчанию при старте)
- **Unsealed** (распечатан) — работоспособен

**Распечатывание (unsealing):** требует **unseal keys** (Shamir's Secret Sharing — мастер-ключ делится на N частей, для распечатывания нужно M из N).

**Storage backend (бэкенд хранения):**
- Vault сам данные не хранит
- В качестве бэкенда: **Integrated Storage (Raft, рекомендуется)**, Consul, файловая система, S3, MySQL, PostgreSQL и т. д.
- Все данные **шифруются** перед записью в бэкенд

**Компоненты:**
- Vault Server
- Storage backend
- Методы аутентификации (auth methods)
- Secrets engines
- Audit devices

## Q4. (!) KV (Key-Value) engine — v1 vs v2?

**KV v1** (устаревший):
- Простой key-value
- Без версионирования
- Секреты перезаписываются

**KV v2** (рекомендуется):
- **Версионирование** — хранится история
- **Мягкое удаление (soft delete) + восстановление**
- **Метаданные** (CAS, кастомные)
- **Subkeys**

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

**В 2025 году** — для новых развёртываний всегда KV v2.

## Q5. Database engine (dynamic DB credentials)?

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

**Vault создаёт пользователя в БД**, приложение его использует, Vault отзывает после истечения TTL.

**Поддерживаемые БД:** PostgreSQL, MySQL, Mongo, Cassandra, Oracle, Redis, MS-SQL и др.

## Q6. AWS engine (dynamic IAM credentials)?

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

Vault динамически создаёт **IAM-пользователя**. Приложение его использует, Vault отзывает (удаляет IAM-пользователя) после истечения TTL.

**Use case:** разработчику нужен доступ к AWS на 1 час для отладки. Vault выдаёт эфемерные credentials.

## Q7. (!) PKI engine (TLS certificates)?

**Vault в роли удостоверяющего центра (Certificate Authority, CA)**.

```bash
vault secrets enable pki

# Generate root CA
vault write pki/root/generate/internal \
  common_name="my-ca" ttl=87600h

# Issue certificate
vault write pki/issue/my-role \
  common_name="api.example.com" ttl=24h
```

**Use case:**
- mTLS между микросервисами — выпуск короткоживущих сертификатов
- HTTPS-сертификаты (альтернатива Let's Encrypt для внутренних нужд)
- Подпись кода (code signing)
- SSH CA для подписи ключей

**Cert-manager** в K8s интегрируется с Vault PKI.

## Q8. Transit engine (encryption-as-a-service)?

**Vault шифрует/расшифровывает данные — но никогда их не хранит.**

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

**Приложение использует Vault как API шифрования**:
- Vault хранит ключи шифрования
- Приложение отправляет данные, получает зашифрованные/расшифрованные
- Ключи никогда не покидают Vault

**Ротация ключей** автоматическая и прозрачная.

**Use case:** соответствие PCI / HIPAA, приложение шифрует данные в БД через Vault.

## Q9. (!) Token authentication?

**Аутентификация по умолчанию** — на основе токенов.

```bash
# Get token
vault token create -policy=my-policy -ttl=1h

# Use
export VAULT_TOKEN="hvs.CAESI..."
vault read kv/myapp/db
```

**Свойства токена:**
- TTL (автоматически истекает)
- Политики (что разрешено)
- Продлеваемый / отзываемый
- Periodic / batch токены

**Periodic-токены** — продлеваются автоматически и бессрочно, если используются регулярно.

## Q10. AppRole?

**AppRole** — аутентификация для **приложений** (machine-to-machine).

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

**Два фактора:**
- **Role ID** — идентифицирует приложение (долгоживущий)
- **Secret ID** — credentials (короткоживущий)

**Использование:** Role ID раздаётся через конфигурацию, Secret ID — отдельно (как CI/CD-секрет и т. п.).

## Q11. (!) Kubernetes auth?

**Kubernetes auth** — аутентифицирует поды по **токенам service account**.

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

**Под получает токен** → обменивает его в Vault → получает токен Vault → читает секреты.

**Best practice** для приложений в K8s.

## Q12. AWS, Azure, GCP IAM auth?

**Cloud IAM auth** — аутентифицирует нагрузки (workloads) по IAM-identity облака.

```bash
vault auth enable aws

vault write auth/aws/role/my-app \
  auth_type=iam \
  bound_iam_principal_arn=arn:aws:iam::123:role/my-app \
  policies=app-policy ttl=1h

# In EC2 / Lambda
vault login -method=aws role=my-app
```

**Identity из облака** проверяется Vault — без общих секретов.

Та же идея для Azure (managed identity) и GCP (service accounts).

## Q13. JWT/OIDC?

**OIDC** — аутентификация через OIDC-провайдера (Auth0, Okta, Google, GitHub).

```bash
vault auth enable oidc
vault write auth/oidc/config \
  oidc_discovery_url="https://accounts.google.com" \
  oidc_client_id="..." \
  oidc_client_secret="..."

vault login -method=oidc
# Browser opens для login
```

**SSO** — пользователи входят в Vault через корпоративный SSO.

**JWT auth** — обычные JWT-токены (без полного OIDC-флоу).

## Q14. (!) Policies (HCL)?

**Политика (policy)** = список разрешённых путей и операций над ними.

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

**Capabilities (возможности):**
- `create`, `read`, `update`, `delete`, `list`
- `sudo` — требуется для некоторых root-операций
- `deny` — явный запрет

**Применить политику к токену / роли:**
```bash
vault token create -policy=my-policy
```

## Q15. Identity (entities, groups)?

**Entity** = пользователь (объединяет разные методы аутентификации).

```
Entity: alice
  ├── alias: alice@github (GitHub auth)
  ├── alias: alice@company (LDAP auth)
  └── alias: alice@oidc (OIDC auth)
```

**Group** = набор entity.

**Политики привязываются к entity/группам** — централизованное управление.

## Q16. (!) Sealing / unsealing?

**Vault стартует ЗАПЕЧАТАННЫМ (SEALED)** — данные зашифрованы, сервис нерабочий.

**Чтобы распечатать:** предоставить **пороговое число (M)** unseal-ключей (из общего числа N).

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

## Q17. Auto-unseal (KMS)?

**Auto-unseal** — Vault использует облачный KMS (AWS KMS, GCP KMS, Azure Key Vault) для распечатывания.

```hcl
seal "awskms" {
  region     = "us-east-1"
  kms_key_id = "alias/vault-key"
}
```

**Эффект:** Vault автоматически распечатывается при старте, используя ключ под управлением KMS.

**Компромисс:** Vault зависит от облачного KMS. Если KMS недоступен → Vault остаётся sealed.

**В production** — рекомендуется (проще в эксплуатации).

## Q18. HA (Raft, Consul backend)?

**Режимы HA в Vault:**

**Integrated Storage (Raft)** — рекомендуется (с 2020 года):
- Встроенный
- Кластер из 3-5 узлов
- Active/standby (только active принимает записи)
- Автоматический failover

**Consul backend** — более старый подход, отдельный кластер Consul.

```hcl
storage "raft" {
  path = "/vault/data"
  node_id = "vault-node-1"
}
ha_storage "raft" { ... }
```

**В 2025 году** — Raft по умолчанию. Consul backend для Vault устарел.

## Q19. Backup / disaster recovery?

**Snapshots:**
```bash
vault operator raft snapshot save backup.snap
vault operator raft snapshot restore backup.snap
```

**Disaster Recovery (Enterprise):**
- DR-репликация на вторичный кластер
- Read-only standby
- Возможность failover

**Performance Replication (Enterprise):**
- Active-кластеры в нескольких регионах
- Локальное чтение секретов

**Бэкапы должны быть зашифрованы** (снапшоты содержат незашифрованные данные!).

## Q20. (!) Vault Agent?

**Vault Agent** — sidecar / демон для приложений, которые не поддерживают Vault нативно.

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
- **Auto-auth** — автоматически обновляет токен Vault
- **Templates** — рендерит секреты в файлы (например, переменные окружения)
- **Caching** — снижает нагрузку на Vault

Приложение читает файл, и ему не нужно знать про Vault.

## Q21. Vault Operator (Kubernetes)?

**Vault Helm chart** для развёртывания в K8s.

```bash
helm install vault hashicorp/vault \
  --set "server.ha.enabled=true" \
  --set "server.ha.replicas=3"
```

**Возможности:**
- HA-кластер на Raft
- Auto-unseal с облачным KMS
- Настройка TLS
- Persistent storage

## Q22. Sidecar injector (K8s)?

**Vault Sidecar Injector** — автоматически инъектирует sidecar Vault Agent в поды (на основе аннотаций).

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

**Эффект:** sidecar получает секреты, пишет их в общий volume, приложение читает из `/vault/secrets/db`.

**Никакого Vault SDK** в коде приложения — только чтение файлов.

## Q23. External Secrets Operator?

**External Secrets Operator (ESO)** — оператор K8s, синхронизирует внешние хранилища секретов (Vault, AWS, GCP, Azure) с K8s Secrets.

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

**Эффект:** ESO забирает данные из Vault, создаёт K8s Secret. Приложения работают с K8s Secret как обычно.

**В сравнении с Vault Agent injector:** ESO проще, если вы уже выстроили рабочий процесс вокруг K8s Secrets.

## Q24. (!) Best practices?

1. **Auto-unseal** в production (облачный KMS)
2. **HA-кластер** (3+ узлов Raft)
3. **TLS повсюду** (Vault API, между узлами)
4. **Включённые audit logs** (file, syslog или socket device)
5. Политики по принципу **наименьших привилегий** (least privilege)
6. **Короткие TTL** для токенов и секретов
7. **Регулярные бэкапы** (снапшоты, зашифрованные)
8. Протестированный план **disaster recovery**
9. **Автоматизация ротации секретов**
10. **Мониторинг** (телеметрия → Prometheus)
11. **Immutable infrastructure** (конфигурация Vault в git, развёртывание через Terraform)
12. **Сетевая изоляция** (приватная подсеть, без публичного IP)

## Q25. (!) Vault vs alternatives (AWS Secrets Manager, Doppler)?

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

**Vault — самый мощный, но сложный в эксплуатации.**

**Для multi-cloud / тяжёлых сценариев** выигрывает Vault. **Для простых приложений на AWS** проще Secrets Manager.

## Q26. License change (BSL) и OpenBao fork?

**В 2023 году** HashiCorp сменила лицензию Vault: **MPL 2.0 → BSL** (Business Source License).

**Ограничения BSL:**
- Нельзя предлагать Vault **как сервис** третьим сторонам (без коммерческой лицензии)
- В остальном — бесплатно для self-host и модификации

**Linux Foundation сделала форк** Vault → **OpenBao** (2024 год, полностью MPL 2.0).

**Статус OpenBao в 2025 году:**
- Активная разработка
- Совместимость с HCL-конфигами Vault
- Со временем — некоторое расхождение
- Внедрён в IBM, GitLab и других компаниях

**Выбор:**
- **Остаться на HashiCorp Vault** — больше всего фич, зрелый
- **Перейти на OpenBao** — гарантированно полностью open-source
- **Наблюдать за экосистемой** — OpenBao набирает популярность

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
