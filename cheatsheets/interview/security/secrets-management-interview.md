---
title: "Вопросы на собеседовании: Secrets Management"
description: "Secrets management: tools (Vault, AWS Secrets Manager, GCP Secret Manager, Azure Key Vault, Doppler), rotation, dynamic secrets, K8s integration (External Secrets Operator), GitOps secrets"
tags:
  - interview
  - security
  - secrets-management-interview
aliases:
  - "Secrets management interview"
  - "Secrets management собеседование"
  - "Secret rotation interview"
  - "Sealed secrets interview"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Secrets Management`

Secrets management — handling sensitive credentials (passwords, API keys, certificates) securely. Concepts: **central store, rotation, dynamic secrets, encryption at rest, audit, least privilege**. Tools: **Vault, AWS Secrets Manager, GCP Secret Manager, Azure Key Vault, Doppler**. Critical для security и compliance.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [HashiCorp Vault](https://developer.hashicorp.com/vault/docs)
- [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/)
- [Azure Key Vault](https://docs.microsoft.com/en-us/azure/key-vault/)
- [GCP Secret Manager](https://cloud.google.com/secret-manager/docs)
- [External Secrets Operator](https://external-secrets.io/)
- [Sealed Secrets (Bitnami)](https://github.com/bitnami-labs/sealed-secrets)
- [SOPS (Mozilla)](https://github.com/getsops/sops)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое secret и зачем management?](#q1--что-такое-secret-и-зачем-management)
- [Q2. (!) Bad practices (что НЕ делать)?](#q2--bad-practices-что-не-делать)
- [Q3. Static vs dynamic secrets?](#q3-static-vs-dynamic-secrets)

**Tools**
- [Q4. (!) HashiCorp Vault?](#q4--hashicorp-vault)
- [Q5. (!) AWS Secrets Manager?](#q5--aws-secrets-manager)
- [Q6. AWS Parameter Store vs Secrets Manager?](#q6-aws-parameter-store-vs-secrets-manager)
- [Q7. GCP Secret Manager?](#q7-gcp-secret-manager)
- [Q8. Azure Key Vault?](#q8-azure-key-vault)
- [Q9. Doppler, Infisical, 1Password Secrets?](#q9-doppler-infisical-1password-secrets)

**Kubernetes**
- [Q10. (!) K8s Secrets — насколько secure?](#q10--k8s-secrets--насколько-secure)
- [Q11. (!) External Secrets Operator?](#q11--external-secrets-operator)
- [Q12. Sealed Secrets (Bitnami)?](#q12-sealed-secrets-bitnami)
- [Q13. SOPS для encrypted secrets в git?](#q13-sops-для-encrypted-secrets-в-git)

**Lifecycle**
- [Q14. (!) Secret rotation?](#q14--secret-rotation)
- [Q15. Dynamic secrets (Vault DB engine)?](#q15-dynamic-secrets-vault-db-engine)
- [Q16. Secrets revocation?](#q16-secrets-revocation)

**Application integration**
- [Q17. (!) Как app reads secrets?](#q17--как-app-reads-secrets)
- [Q18. Vault Agent / Sidecar Injector?](#q18-vault-agent--sidecar-injector)
- [Q19. AWS IRSA (IAM Roles for Service Accounts)?](#q19-aws-irsa-iam-roles-for-service-accounts)

**GitOps secrets**
- [Q20. (!) Как handle secrets в GitOps?](#q20--как-handle-secrets-в-gitops)

**Best practices**
- [Q21. (!) Best practices?](#q21--best-practices)
- [Q22. (!) Common pitfalls?](#q22--common-pitfalls)

## Q1. (!) Что такое secret и зачем management?

**Secret** = sensitive credential:
- Database passwords
- API keys (Stripe, AWS, ...)
- TLS certificates / private keys
- Encryption keys
- OAuth tokens
- SSH keys

**Зачем dedicated management:**
- **Centralized** (one place vs scattered .env files)
- **Audit logs** (who accessed когда)
- **Rotation** (auto-rotate periodically)
- **Revocation** (instantly disable compromised)
- **Encryption** (at rest, in transit)
- **Least privilege** (fine-grained access)
- **Compliance** (SOC2, PCI, HIPAA)

## Q2. (!) Bad practices (что НЕ делать)?

❌ **Hardcoded в source code** (commits к git → public via leak)
❌ **Plain text в config files**
❌ **Plain `.env` files** committed
❌ **Slack/email** для sharing secrets
❌ **Same secret** в dev / staging / prod
❌ **Long-lived shared secrets** (no rotation)
❌ **Print secrets** в logs / error messages
❌ **Secrets в Docker images** (visible в layers)
❌ **Wide IAM permissions** (one user accesses everything)
❌ **No audit logging**

**Real incidents** — GitHub leaks, mass-bruteforce attacks against committed secrets.

**GitGuardian** scans GitHub для leaked secrets daily — finds **millions per year**.

## Q3. Static vs dynamic secrets?

**Static:** stored as-is.
- Manually rotated
- Long-lived
- Examples: API keys, config

**Dynamic:** generated on-demand.
- Short TTL (minutes-hours)
- Auto-revoked after TTL
- Examples: DB credentials (Vault generates user)

**Dynamic preferred** для:
- Database access
- Cloud (AWS, GCP)
- SSH access
- Third-party APIs (where supported)

**Effect:** breach impact limited (credentials short-lived).

## Q4. (!) HashiCorp Vault?

**Vault** — most popular OSS secrets management.

**Capabilities:**
- KV store
- Dynamic secrets (DB, cloud, SSH)
- PKI (issue certificates)
- Transit encryption (encryption-as-a-service)
- Multi-tenant (namespaces, policies)
- Multiple auth methods

```bash
# Write secret
vault kv put secret/myapp/db password=secret123

# Read
vault kv get secret/myapp/db

# Dynamic DB credentials
vault read database/creds/my-role
# Returns NEW user/password, expires in 1 hour
```

Подробнее — в [[vault-interview|Vault]].

## Q5. (!) AWS Secrets Manager?

**AWS managed** secrets store.

```bash
# Create
aws secretsmanager create-secret --name myapp/db --secret-string '{"username":"admin","password":"secret"}'

# Retrieve
aws secretsmanager get-secret-value --secret-id myapp/db
```

**Features:**
- **Auto-rotation** (RDS, Redshift, DocumentDB built-in)
- **Encryption** (KMS)
- **Cross-region replication**
- **Resource-based policies** (IAM)
- **Audit** через CloudTrail

**Cost:** $0.40/secret/month + API calls.

**Best для:** AWS-native apps.

## Q6. AWS Parameter Store vs Secrets Manager?

| Critterion | Parameter Store | Secrets Manager |
|-----------|----------------|-----------------|
| Cost | **Free** (Standard) | $0.40/secret/month |
| Auto-rotation | No | **Yes** |
| Cross-region replication | No | Yes |
| Max value size | 4 KB / 8 KB | 64 KB |
| API rate limits | Lower | Higher |
| Use case | Config, simple secrets | Sensitive credentials |

**Common pattern:**
- **Parameter Store** для config (URLs, feature flags, non-sensitive)
- **Secrets Manager** для credentials (DB, API keys)

**Both encrypted с KMS.**

## Q7. GCP Secret Manager?

```bash
# Create
echo -n "secret_value" | gcloud secrets create my-secret --data-file=-

# Retrieve
gcloud secrets versions access latest --secret="my-secret"
```

**Features:**
- Versioning (multiple versions of secret)
- **IAM-based access**
- **CMEK** (customer-managed encryption keys)
- Audit logs (Cloud Audit Logs)

**Cost:** $0.06/secret/month + access fees.

## Q8. Azure Key Vault?

```bash
az keyvault secret set --vault-name MyVault --name MySecret --value "secretValue"
az keyvault secret show --vault-name MyVault --name MySecret
```

**Features:**
- Secrets, **keys** (HSM-backed), **certificates**
- Soft delete + purge protection
- Managed identities integration
- **HSM** option (FIPS 140-2 Level 2/3)

**Cost:** $0.03/10K ops + cert costs.

**Best для:** Azure-native apps, compliance-heavy.

## Q9. Doppler, Infisical, 1Password Secrets?

**Doppler** (SaaS):
- Easy CLI
- Multi-environment workflows
- Sync к multiple platforms
- $0-25/user/month

**Infisical** (open-source + SaaS):
- Self-host или cloud
- Free OSS
- Modern UI
- Growing rapidly

**1Password Secrets** (extension 1Password):
- Familiar 1Password UX
- Developer-focused features
- CLI integration

**Use case:** smaller teams, simpler than Vault, more features than Parameter Store.

## Q10. (!) K8s Secrets — насколько secure?

**K8s Secrets** = **Base64 encoded** values stored в etcd.

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: my-secret
type: Opaque
data:
  password: c2VjcmV0   # base64("secret")
```

**Проблемы:**
- **Base64 ≠ encryption** (anyone с access decodes)
- Stored в etcd (часто **unencrypted**)
- All cluster admins can read
- Not designed для **high security**

**Mitigations:**
1. **Encrypt etcd at rest** (`--encryption-provider-config`)
2. **RBAC** на Secret resources
3. **Use external secret manager** (Vault, AWS Secrets Manager)
4. **Sealed Secrets** для GitOps

**Best practice:** **don't use bare K8s Secrets для real secrets** — use external store + sync.

## Q11. (!) External Secrets Operator?

**External Secrets Operator (ESO)** — K8s operator, syncs external secret stores к K8s Secrets.

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: db-credentials
spec:
  secretStoreRef:
    name: vault-store
    kind: SecretStore
  target:
    name: db-secret
  data:
    - secretKey: password
      remoteRef:
        key: secret/myapp/db
        property: password
```

**Effect:** ESO fetches from Vault → creates K8s Secret. Pods read normally.

**Supported backends:**
- HashiCorp Vault
- AWS Secrets Manager
- AWS Parameter Store
- GCP Secret Manager
- Azure Key Vault
- 1Password
- Doppler
- And more

**Most popular pattern** для K8s + external secrets в 2025.

## Q12. Sealed Secrets (Bitnami)?

**Problem:** want secrets в git (GitOps), но git ≠ secure.

**Sealed Secrets** — encrypted secrets safely committed к git.

```bash
# Encrypt
echo -n "mysecretpassword" | kubectl create secret generic my-secret \
  --dry-run=client --from-file=password=/dev/stdin -o yaml | \
  kubeseal -o yaml > sealed-secret.yaml

# sealed-secret.yaml содержит encrypted data — safe для git
```

```yaml
apiVersion: bitnami.com/v1alpha1
kind: SealedSecret
metadata:
  name: my-secret
spec:
  encryptedData:
    password: AgBKj9... (encrypted)
```

**Sealed Secrets controller** в cluster decrypts → creates K8s Secret.

**Pros:** GitOps-friendly. **Cons:** keys tied к specific cluster.

## Q13. SOPS для encrypted secrets в git?

**SOPS (Mozilla)** — encrypts files (YAML, JSON, ENV) с various backends (KMS, PGP, age).

```bash
# Encrypt с AWS KMS
sops --encrypt --kms arn:aws:kms:... secrets.yaml > secrets.enc.yaml

# Decrypt
sops --decrypt secrets.enc.yaml
```

**Result:** YAML с values encrypted (keys plain), commit safely к git.

```yaml
db:
  password: ENC[AES256_GCM,data:abc123...,type:str]
```

**Tools integration:**
- Helm Secrets
- ArgoCD
- Flux
- Terraform

**Use case:** GitOps, IaC secrets, multi-env configs.

## Q14. (!) Secret rotation?

**Why:** compromised secret has limited window.

**Rotation strategies:**

**Manual:**
- Quarterly / annually
- Risk: forgotten, painful

**Auto-rotation:**
- AWS Secrets Manager: automatic для RDS, Redshift, DocumentDB
- Vault dynamic secrets: each request = new credential
- Custom Lambda functions: для anything

**Pattern:**
1. Create new credential
2. Update apps к use new
3. Verify
4. Revoke old

**Caveats:**
- **Apps must reload** secrets (without restart ideally)
- **Database** updates simultaneously
- **Connection pools** must refresh

## Q15. Dynamic secrets (Vault DB engine)?

```bash
# Configure
vault write database/config/postgres \
  plugin_name=postgresql-database-plugin \
  connection_url="postgres://vault@host/db" \
  allowed_roles="readonly"

# Define role
vault write database/roles/readonly \
  db_name=postgres \
  creation_statements="CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}';" \
  default_ttl=1h \
  max_ttl=24h

# Get credentials
vault read database/creds/readonly
# Returns username + password, valid 1 hour
```

**Each app instance** gets unique credentials. After TTL → revoked.

**Benefits:**
- **Per-instance** credentials (audit precise)
- **Auto-revoked**
- **Compromise window short**

**Supported:** PostgreSQL, MySQL, MongoDB, Cassandra, Oracle, Redis, AWS, GCP, etc.

## Q16. Secrets revocation?

**Compromised secret** must be revoked immediately.

**Static secrets:**
1. Generate new
2. Update apps
3. Delete old в secret store
4. Rotate connections

**Dynamic secrets:**
- Vault: lease revocation API
- Cloud IAM: delete IAM user / role

**Speed matters:** the faster revocation, the smaller breach impact.

**Audit logs** — verify no further use after revocation.

## Q17. (!) Как app reads secrets?

**Patterns:**

**1. Direct API call:**
```python
import boto3
client = boto3.client('secretsmanager')
secret = client.get_secret_value(SecretId='myapp/db')
```

**2. Sidecar (Vault Agent):**
- Sidecar fetches secrets, writes к shared volume
- App reads file

**3. Init container:**
- Init container fetches secrets, mounts к main container

**4. Operator** (External Secrets Operator):
- Sync к K8s Secret
- App reads K8s Secret normally

**5. Environment variables (injected at startup):**
- К8s Secrets mounted as env
- Vault Agent renders templates

**Best practice:** prefer files (mounted volumes) over env vars (env vars leak в child processes, logs).

## Q18. Vault Agent / Sidecar Injector?

**Vault Agent** — daemon рядом с app:
- Auto-authenticates с Vault
- Fetches secrets
- Caches
- Renders templates → files
- Renews leases

**Sidecar Injector (K8s):**
- Annotation-based auto-inject Vault Agent sidecar
- App reads secrets from `/vault/secrets/` folder

```yaml
metadata:
  annotations:
    vault.hashicorp.com/agent-inject: "true"
    vault.hashicorp.com/role: "myapp"
    vault.hashicorp.com/agent-inject-secret-db: "secret/myapp/db"
```

**Effect:** apps no Vault SDK needed — just file reads.

## Q19. AWS IRSA (IAM Roles for Service Accounts)?

**IRSA** — pod в EKS gets **IAM role** через ServiceAccount.

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: my-app
  annotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::123:role/MyAppRole
```

**Effect:** pod uses AWS SDK без credentials в env / files. AWS auto-issues temporary credentials.

**Credentials short-lived** (1 hour, auto-rotated).

**No secrets distribution** — IAM-based access.

**Equivalent:**
- **GCP Workload Identity** — для GKE
- **Azure Workload Identity** — для AKS

**Best practice:** prefer cloud-native IAM over secret distribution.

## Q20. (!) Как handle secrets в GitOps?

**Problem:** GitOps = git as source of truth. **But secrets can't go в git plaintext.**

**Solutions:**

1. **Sealed Secrets** — encrypted, can commit
2. **SOPS** — encrypted files в git
3. **External Secrets Operator** — references к external store (Vault, AWS)
4. **Bitnami Secrets Manager**
5. **CSI Secrets Store Driver**

**Pattern:**
```yaml
# In git (committed)
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: db-creds
spec:
  secretStoreRef:
    name: vault-backend
  data:
    - secretKey: password
      remoteRef:
        key: secret/db
```

**Actual secret** только в Vault. Git contains **reference**, not value.

## Q21. (!) Best practices?

1. **Never commit** secrets к source control
2. **Centralized** secret store (Vault, AWS Secrets Manager)
3. **Auto-rotation** для long-lived secrets
4. **Dynamic secrets** где possible (DB, cloud)
5. **Short TTLs** для credentials
6. **Least privilege** access
7. **Audit logging** все access
8. **Encryption at rest + in transit**
9. **No secrets в env vars** ideally (use mounted files)
10. **Cloud IAM** over secret distribution (IRSA, Workload Identity)
11. **Secret scanning** в CI (gitleaks, GitGuardian)
12. **Rotate immediately** после suspected compromise
13. **Different secrets** per environment (dev/staging/prod)
14. **Disaster recovery** plan для secret store
15. **MFA** для access к secret management UI

## Q22. (!) Common pitfalls?

1. **Hardcoded secrets** — leak via git, logs
2. **Same secret across envs** — dev compromise = prod compromise
3. **No rotation** — old credentials accumulate
4. **Wide permissions** — service has access to all secrets
5. **Logging secrets** (request bodies, error messages)
6. **Secrets в Docker image** layers (visible)
7. **No audit** — don't know if compromised
8. **Lost vault unseal keys** — Vault unrecoverable
9. **No backup** secret store (catastrophic loss)
10. **Slack/email sharing**
11. **Reuse** API keys между apps
12. **No revocation procedure** documented
13. **Secrets в CI/CD logs** (hidden but findable)

**Tools для detection:**
- **gitleaks, GitGuardian, TruffleHog** — scan git для leaked secrets
- **CodeQL, Semgrep** — static analysis
- **Trivy, Grype** — container scanning

В **2025** secrets management — **fundamental security hygiene**. Multiple breaches yearly из-за поэтому practices missing.

---

## See also

- [[vault-interview|HashiCorp Vault]] — main tool
- [[zero-trust-interview|Zero Trust]] — context
- [[mtls-interview|mTLS]] — alternative auth
- [[supply-chain-security-interview|Supply Chain Security]]
- [[application-security-interview|Application Security]] — общая
- [[oauth2-interview|OAuth2]] — token management
- [[jwt-interview|JWT]] — short-lived tokens
- [[aws-interview|AWS]] — Secrets Manager, Parameter Store
- [[gcp-interview|GCP]] — Secret Manager
- [[azure-interview|Azure]] — Key Vault
- [[kubernetes-interview|Kubernetes]] — Secrets, ESO
- [[microservices-interview|Микросервисы]] — context
- [[cloud-native-patterns-interview|Cloud-native Patterns]] — context
- [[git-interview|Git]] — secret scanning

- [[application-security-interview|Application Security]]
- [[authentication-authorization-patterns-interview|Authentication and Authorization Patterns]]
- [[jwt-interview|JWT]]
- [[mtls-interview|mTLS (Mutual TLS)]]
- [[oauth2-interview|OAuth2]]
- [[owasp-top10-interview|OWASP Top 10]]
- [[secrets-management|Шпаргалка: Управление секретами (Secrets Management]] — теория
