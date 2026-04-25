---
title: "Вопросы на собеседовании: HashiCorp Vault"
description: "HashiCorp Vault: secrets management, KV engine, dynamic secrets, auth methods, policies, leases, transit encryption, PKI, integration с K8s, alternatives"
tags:
  - interview
  - devops
  - vault-interview
aliases:
  - "Vault interview"
  - "HashiCorp Vault interview"
  - "Vault собеседование"
  - "Secrets management interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `HashiCorp Vault`

`HashiCorp Vault` — secrets management система. Centralized storage и access control для credentials, API keys, certificates. Использует **dynamic secrets**, **encryption-as-a-service**, **identity-based access**. С 2023 — license changed (BSL), форк **OpenBao** (Linux Foundation).

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

**HashiCorp Vault** — secrets management system (с 2015).

**Core capabilities:**
- **Centralized secrets storage** (instead .env files, hardcoded creds)
- **Dynamic secrets** — short-lived, auto-rotated
- **Identity-based access** — fine-grained policies
- **Encryption-as-a-service** — encrypt/decrypt API
- **PKI** — issue TLS certificates
- **Audit logs** — full traceability


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Применения:**
- Replace hardcoded API keys, DB passwords
- Issue short-lived DB credentials
- TLS cert management
- Application encryption
- SSH access management

## Q2. (!) Static vs dynamic secrets?

**Static secrets:**
- Stored as-is (KV engine)
- Same value over time
- Manually rotated
- Examples: API keys, config values

**Dynamic secrets:**
- **Generated on-demand**
- **Short TTL** (minutes/hours)
- **Auto-revoked**
- Examples: DB credentials (Vault creates user on demand), AWS keys

```bash
# Dynamic DB credentials
vault read database/creds/my-role
# Returns NEW user/password, expires in 1 hour
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Преимущество dynamic:** **breach impact limited** — credentials short-lived, revoked.

## Q3. Architecture (sealed/unsealed, storage backend)?

**Vault states:**
- **Sealed** — encrypted, не accept requests (default at start)
- **Unsealed** — operational

**Unsealing:** requires **unseal keys** (Shamir's Secret Sharing — split в N pieces, threshold M of N to unseal).

**Storage backend:**
- Vault doesn't store data itself
- Backed by: **Integrated Storage (Raft, recommended)**, Consul, Filesystem, S3, MySQL, PostgreSQL, etc.
- All data **encrypted** before write к backend


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Components:**
- Vault Server
- Storage backend
- Auth methods
- Secrets engines
- Audit devices

## Q4. (!) KV (Key-Value) engine — v1 vs v2?

**KV v1** (legacy):
- Simple key-value
- No versioning
- Overwrite secrets

**KV v2** (recommended):
- **Versioning** — keep history
- **Soft delete + restore**
- **Metadata** (CAS, custom)
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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**В 2025** — always KV v2 для new deployments.

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

**Vault creates DB user**, application uses, Vault revokes after TTL.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Supported DBs:** PostgreSQL, MySQL, Mongo, Cassandra, Oracle, Redis, MS-SQL, и др.

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

Vault creates **IAM user** dynamically. Application uses, Vault revokes (deletes IAM user) after TTL.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Use case:** developer needs AWS access for 1 hour debugging. Vault provides ephemeral creds.

## Q7. (!) PKI engine (TLS certificates)?

**Vault как Certificate Authority (CA)**.

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
- mTLS между microservices — issue short-lived certs
- HTTPS certs (alternative Let's Encrypt для internal)
- Code signing
- SSH CA для signing keys


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Cert-manager** в K8s integrates с Vault PKI.

## Q8. Transit engine (encryption-as-a-service)?

**Vault encrypts/decrypts data — never stores it.**

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

**Application uses Vault как encryption API**:
- Vault holds encryption keys
- Application sends data, gets encrypted/decrypted
- Keys never leave Vault

**Key rotation** automatic, transparent.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Use case:** PCI / HIPAA compliance, app encrypts data в DB через Vault.

## Q9. (!) Token authentication?

**Default auth** — token-based.

```bash
# Get token
vault token create -policy=my-policy -ttl=1h

# Use
export VAULT_TOKEN="hvs.CAESI..."
vault read kv/myapp/db
```

**Token properties:**
- TTL (auto-expires)
- Policies (what allowed)
- Renewable / revocable
- Periodic / batch tokens


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Periodic tokens** — auto-renewed indefinitely if used regularly.

## Q10. AppRole?

**AppRole** — auth для **applications** (machine-to-machine).

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

**Two factors:**
- **Role ID** — identifies app (long-lived)
- **Secret ID** — credentials (short-lived)


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Usage:** distribute Role ID via config, Secret ID separately (CI/CD secret, etc.).

## Q11. (!) Kubernetes auth?

**Kubernetes auth** — authenticates pods using **service account tokens**.

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

**Pod gets token** → exchanges с Vault → gets Vault token → reads secrets.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Best practice** для apps в K8s.

## Q12. AWS, Azure, GCP IAM auth?

**Cloud IAM auth** — authenticate workloads using cloud's IAM identity.

```bash
vault auth enable aws

vault write auth/aws/role/my-app \
  auth_type=iam \
  bound_iam_principal_arn=arn:aws:iam::123:role/my-app \
  policies=app-policy ttl=1h

# In EC2 / Lambda
vault login -method=aws role=my-app
```

**Identity from cloud** verified by Vault — no shared secrets.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
Same idea для Azure (managed identity) и GCP (service accounts).

## Q13. JWT/OIDC?

**OIDC** — auth via OIDC provider (Auth0, Okta, Google, GitHub).

```bash
vault auth enable oidc
vault write auth/oidc/config \
  oidc_discovery_url="https://accounts.google.com" \
  oidc_client_id="..." \
  oidc_client_secret="..."

vault login -method=oidc
# Browser opens для login
```

**SSO** — users login через corporate SSO к Vault.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**JWT auth** — generic JWT tokens (not full OIDC flow).

## Q14. (!) Policies (HCL)?

**Policy** = list of allowed paths + operations.

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

**Capabilities:**
- `create`, `read`, `update`, `delete`, `list`
- `sudo` — required для some root operations
- `deny` — explicit deny


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Apply policy к token / role:**
```bash
vault token create -policy=my-policy
```

## Q15. Identity (entities, groups)?

**Entity** = user (across multiple auth methods).

```
Entity: alice
  ├── alias: alice@github (GitHub auth)
  ├── alias: alice@company (LDAP auth)
  └── alias: alice@oidc (OIDC auth)
```

**Group** = collection of entities.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Policies attached к entities/groups** — централизованное управление.

## Q16. (!) Sealing / unsealing?

**Vault starts SEALED** — encrypted, non-functional.

**To unseal:** provide **threshold (M)** unseal keys (out of total N).

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

**Зачем:** даже если attacker compromises Vault server — данные encrypted, unseal keys distributed.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Restart** = sealed again. Production cluster needs unseal каждый restart.

## Q17. Auto-unseal (KMS)?

**Auto-unseal** — Vault uses cloud KMS (AWS KMS, GCP KMS, Azure Key Vault) для unsealing.

```hcl
seal "awskms" {
  region     = "us-east-1"
  kms_key_id = "alias/vault-key"
}
```

**Effect:** Vault auto-unseals on startup using KMS-managed key.

**Trade-off:** Vault dependent на cloud KMS. If KMS down → Vault sealed.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**В production** — recommended (operational simplicity).

## Q18. HA (Raft, Consul backend)?

**Vault HA modes:**

**Integrated Storage (Raft)** — recommended (с 2020):
- Built-in
- 3-5 nodes cluster
- Active/standby (only active accepts writes)
- Failover automatic

**Consul backend** — older approach, separate Consul cluster.

```hcl
storage "raft" {
  path = "/vault/data"
  node_id = "vault-node-1"
}
ha_storage "raft" { ... }
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**В 2025** — Raft default. Consul backend obsolete для Vault.

## Q19. Backup / disaster recovery?

**Snapshots:**
```bash
vault operator raft snapshot save backup.snap
vault operator raft snapshot restore backup.snap
```

**Disaster Recovery (Enterprise):**
- DR replication к secondary cluster
- Read-only standby
- Failover capability

**Performance Replication (Enterprise):**
- Multi-region active clusters
- Local secrets reads


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Backups should be encrypted** (snapshots contain unencrypted data!).

## Q20. (!) Vault Agent?

**Vault Agent** — sidecar / daemon for apps that don't natively support Vault.

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

**Capabilities:**
- **Auto-auth** — automatically refreshes Vault token
- **Templates** — render secrets к files (e.g., env vars)
- **Caching** — reduces Vault load


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
App reads file, не нужно знать про Vault.

## Q21. Vault Operator (Kubernetes)?

**Vault Helm chart** для deploy в K8s.

```bash
helm install vault hashicorp/vault \
  --set "server.ha.enabled=true" \
  --set "server.ha.replicas=3"
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Features:**
- HA Raft cluster
- Auto-unseal с cloud KMS
- TLS configuration
- Persistent storage

## Q22. Sidecar injector (K8s)?

**Vault Sidecar Injector** — auto-inject Vault Agent sidecar в pods (annotations-based).

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

**Effect:** sidecar fetches secrets, writes к shared volume, app reads from `/vault/secrets/db`.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**No Vault SDK** в app code — just file reads.

## Q23. External Secrets Operator?

**External Secrets Operator (ESO)** — K8s operator, syncs external secret stores (Vault, AWS, GCP, Azure) к K8s Secrets.

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

**Effect:** ESO fetches from Vault, creates K8s Secret. Apps use K8s Secret normally.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Vs Vault Agent injector:** ESO simpler если уже invested в K8s Secrets workflow.

## Q24. (!) Best practices?


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
1. **Auto-unseal** в production (cloud KMS)
2. **HA cluster** (3+ Raft nodes)
3. **TLS everywhere** (Vault API, between nodes)
4. **Audit logs enabled** (file, syslog, или socket device)
5. **Least privilege** policies
6. **Short TTLs** для tokens, secrets
7. **Periodic backups** (snapshots, encrypted)
8. **Disaster recovery** plan tested
9. **Secrets rotation** automation
10. **Monitoring** (telemetry → Prometheus)
11. **Immutable infrastructure** (Vault config in git, deployed via Terraform)
12. **Network isolation** (private subnet, no public IP)

## Q25. (!) Vault vs alternatives (AWS Secrets Manager, Doppler)?

| Tool | Hosting | Cloud-agnostic | Dynamic secrets | Cost |
|------|---------|----------------|-----------------|------|
| **HashiCorp Vault** | Self-host или cloud | Yes | Yes | Free OSS, Enterprise paid |
| **AWS Secrets Manager** | AWS managed | No (AWS-only) | Limited (RDS rotation) | $0.40/secret/month |
| **Azure Key Vault** | Azure managed | No | Limited | $0.03/10K ops |
| **GCP Secret Manager** | GCP managed | No | Limited | $0.06/secret/month |
| **Doppler** | SaaS | Yes | No | $0-$25/month/user |
| **1Password Secrets** | SaaS | Yes | No | Subscription |
| **Infisical** | Self-host или SaaS | Yes | Yes | Free OSS |
| **OpenBao** | Self-host (Vault fork) | Yes | Yes | Free OSS |

**Vault — most powerful, but complex ops.**


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**For multi-cloud / heavy use cases** — Vault wins. **For simple AWS apps** — Secrets Manager easier.

## Q26. License change (BSL) и OpenBao fork?

**В 2023** HashiCorp changed Vault license: **MPL 2.0 → BSL** (Business Source License).

**BSL restrictions:**
- Cannot offer Vault **as a service** к third parties (without commercial license)
- Otherwise — free для self-host, modify

**Linux Foundation forked** Vault → **OpenBao** (2024, fully MPL 2.0).

**OpenBao status в 2025:**
- Active development
- Compatible с Vault HCL configs
- Some divergence over time
- Adopted by IBM, GitLab, и others

**Choice:**
- **Stay HashiCorp Vault** — most features, mature
- **Switch к OpenBao** — fully open-source guaranteed
- **Watch ecosystem** — OpenBao gaining traction

---

## See also

- [Consul](consul-interview.md) — другой HashiCorp tool
- [Ansible](ansible-interview.md) — для secrets distribution
- [Istio](istio-service-mesh-interview.md) — mTLS via Vault PKI
- [Secrets Management](../security/secrets-management-interview.md) — concepts
- [Application Security](../security/application-security-interview.md) — context
- [Kubernetes](kubernetes-interview.md) — Vault в K8s
- [Микросервисы](../architecture/microservices-interview.md) — secrets для microservices
- [Cloud-native Patterns](../cloud/cloud-native-patterns-interview.md) — secrets management
- [AWS](../cloud/aws-interview.md) — Secrets Manager comparison
- [OAuth2](../security/oauth2-interview.md) — auth flows
- [JWT](../security/jwt-interview.md) — JWT auth
- [Zero Trust](../security/zero-trust-interview.md) — Vault key component


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
- [Ansible](ansible-interview.md)
- [ArgoCD и GitOps](argocd-interview.md)
- [HashiCorp Consul](consul-interview.md)
- [Docker](docker-interview.md)
- [Git](git-interview.md)
- [Gradle и Maven](gradle-maven-interview.md)
