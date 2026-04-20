---
title: "Управление секретами (Secrets Management)"
description: "HashiCorp Vault, облачные KMS, Kubernetes Secrets, rotation, dynamic secrets, обнаружение утечек и паттерны безопасной передачи секретов в приложения."
tags:
  - security
  - data
  - secrets
  - vault
  - kms
difficulty: "intermediate"
updated: "2026-04-17"
---
# Управление секретами (Secrets Management)

Secrets management — системное решение для хранения, раздачи, ротации и отзыва секретов (пароли БД, API-ключи, TLS-сертификаты, токены). Без централизованного хранилища секреты расползаются по git, Jenkins-credentials, env-файлам и Confluence; следы компрометации практически невозможно отследить.

Главные свойства зрелого решения: secrets не в git, secrets не в образах, короткоживущие токены вместо вечных, автоматическая ротация, audit-trail каждого обращения.

## Полезные ссылки

### Продукты
- [HashiCorp Vault](https://www.vaultproject.io/) — универсальный secrets manager, на рынке с 2015
- [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/) / [AWS KMS](https://aws.amazon.com/kms/)
- [GCP Secret Manager](https://cloud.google.com/secret-manager) / [GCP KMS](https://cloud.google.com/kms)
- [Azure Key Vault](https://azure.microsoft.com/en-us/products/key-vault)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)
- [External Secrets Operator](https://external-secrets.io/) — bridge между внешними vaults и K8s
- [Sealed Secrets (Bitnami)](https://github.com/bitnami-labs/sealed-secrets) — зашифрованные secrets в git

### Обнаружение утечек
- [gitleaks](https://github.com/gitleaks/gitleaks) — поиск секретов в git
- [trufflehog](https://github.com/trufflesecurity/trufflehog) — сканер репо
- [detect-secrets](https://github.com/Yelp/detect-secrets) — pre-commit hook

### Стандарты
- [OWASP Secrets Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)
- [NIST SP 800-57](https://csrc.nist.gov/publications/detail/sp/800-57-part-1/rev-5/final)

### Соседние разделы
- [Data Security](data-security.md)
- [Application Security](../application/application-security.md)
- [Infrastructure Security](../infrastructure/)
- [Kubernetes](../../platform/containers/kubernetes/)

## Содержание

- [Типы секретов и жизненный цикл](#типы-секретов-и-жизненный-цикл)
- [Модель зрелости](#модель-зрелости)
- [HashiCorp Vault](#hashicorp-vault)
- [Облачные KMS/Secrets Manager](#облачные-kmssecrets-manager)
- [Kubernetes Secrets](#kubernetes-secrets)
- [Dynamic secrets](#dynamic-secrets)
- [Ротация секретов](#ротация-секретов)
- [Передача секретов в приложение](#передача-секретов-в-приложение)
- [Обнаружение утечек в CI](#обнаружение-утечек-в-ci)
- [Чек-лист](#чек-лист)
- [Антипаттерны](#антипаттерны)

## Типы секретов и жизненный цикл

| Тип | TTL | Кто выпускает |
|-----|-----|---------------|
| Пароль БД (shared) | 90d | DBA, vault |
| Креды БД (per-app) | динамический 1h | Vault DB engine |
| API-ключ 3rd-party | до ротации | Vault KV, провайдер |
| JWT signing key | 1-2 года | KMS |
| TLS-сертификат | ≤398d | cert-manager, Let's Encrypt |
| OAuth refresh token | 30-90d | IdP |
| SSH-ключ | годы | Vault SSH engine |
| Cloud IAM-ключ | ≤90d (или workload identity) | cloud |

Каждый секрет имеет этапы: **создание → хранение → раздача → использование → ротация → отзыв → уничтожение**. Пропуск любого этапа — дыра.

## Модель зрелости

| Уровень | Описание | Проблемы |
|---------|----------|----------|
| L0: chaos | секреты в коде, конфигах, чатах | утечка через git, VCS history |
| L1: env vars | секреты в env/CI variables | видны в `ps`, `env`, логах |
| L2: encrypted files | `.env.gpg`, git-crypt, SOPS | ручное декодирование, нет аудита |
| L3: central vault | Vault/Secrets Manager | требует initial-auth, learning curve |
| L4: dynamic secrets | credentials выдаются on-demand | более сложная архитектура |
| L5: zero trust + attested identity | workload identity + mTLS | требует инфраструктуры (SPIFFE/SPIRE) |

Цель зрелой команды — L3+. Stretch — L4 для критичных систем.

## HashiCorp Vault

**Архитектура:** HA-кластер с Raft-backend, шифрование в покое, unseal с Shamir's Secret Sharing или auto-unseal через KMS.

**Secret engines:**

| Engine | Назначение |
|--------|-----------|
| `kv` (v2) | key-value с версионированием |
| `database` | dynamic creds для PostgreSQL/MySQL/Mongo/... |
| `pki` | внутренний CA, выпуск TLS-сертификатов |
| `transit` | encryption-as-a-service (без хранения DEK) |
| `aws`/`gcp`/`azure` | dynamic cloud-credentials |
| `ssh` | one-time SSH OTP/signed certs |
| `totp` | 2FA-токены |

**Auth methods:**
- `token`, `approle` — сервисы
- `kubernetes` — ServiceAccount-based
- `aws`, `gcp`, `azure` — IAM-based
- `oidc`, `ldap` — люди
- `cert`, `jwt` — mTLS / OIDC

**Пример:**

```bash
# Запись секрета
vault kv put secret/app/prod db.password=SuperSecret

# Чтение
vault kv get -field=db.password secret/app/prod

# Policy (минимальный доступ)
vault policy write app-prod - <<EOF
path "secret/data/app/prod" {
  capabilities = ["read"]
}
EOF

# AppRole
vault auth enable approle
vault write auth/approle/role/app-prod \
    token_policies="app-prod" \
    token_ttl=1h \
    token_max_ttl=4h
```

## Облачные KMS/Secrets Manager

**AWS:**
- KMS — шифрование, envelope encryption, HSM-backed (CloudHSM).
- Secrets Manager — KV-secrets, auto-rotation через Lambda.
- Parameter Store (SSM) — дешёвая альтернатива для не-критичного.

**GCP:**
- Cloud KMS — ключи, версии, CMEK для сервисов.
- Secret Manager — KV, версионирование, IAM-политики.

**Azure:**
- Key Vault — ключи, секреты, сертификаты в одном месте.
- Managed HSM — для критичных ключей.

**Выбор:** если вся инфра в облаке — нативный сервис проще (меньше операций). Если мульти-облако или on-prem — Vault.

## Kubernetes Secrets

**Ограничение:** K8s Secrets хранятся в etcd base64-encoded (НЕ шифрованы по умолчанию). Всегда включайте **encryption-at-rest** через KMS-provider.

```yaml
# EncryptionConfiguration
apiVersion: apiserver.config.k8s.io/v1
kind: EncryptionConfiguration
resources:
- resources: ["secrets"]
  providers:
  - kms:
      name: vault-kms
      endpoint: unix:///tmp/vault-kms.sock
      cachesize: 1000
  - identity: {}
```

**Паттерны:**

1. **Sealed Secrets** — `SealedSecret` CRD, шифрование по публичному ключу контроллера, можно коммитить в git.
2. **External Secrets Operator** — читает из Vault/AWS SM и создаёт нативные Secret-объекты.
3. **CSI Secret Store Driver** — монтирование секретов из vault как файлы в pod.
4. **Bank-Vaults / Vault Agent Injector** — sidecar подхватывает секреты в файл/переменную.

## Dynamic secrets

**Идея:** вместо вечных creds — выдавать short-lived по запросу. Компрометация = минимальное окно.

```bash
# PostgreSQL dynamic credentials в Vault
vault write database/config/my-postgres \
    plugin_name=postgresql-database-plugin \
    connection_url="postgresql://{{username}}:{{password}}@host:5432/db" \
    allowed_roles="readonly" \
    username=vault_root \
    password=...

vault write database/roles/readonly \
    db_name=my-postgres \
    creation_statements="CREATE USER \"{{name}}\" WITH PASSWORD '{{password}}' VALID UNTIL '{{expiration}}'; GRANT SELECT ON ALL TABLES IN SCHEMA public TO \"{{name}}\";" \
    default_ttl=1h max_ttl=24h

# Запрос
vault read database/creds/readonly
# Key                Value
# username           v-readonly-Xy7a...
# password           A1b-C2d...
# lease_duration     1h
```

При lease expire Vault автоматически удаляет пользователя в БД.

## Ротация секретов

| Секрет | Как часто | Механизм |
|--------|-----------|----------|
| JWT signing key | 1-2 года | KMS key versions + grace period |
| DB shared password | 90d | Vault rotate-root |
| DB per-app | 1h (dynamic) | lease renewal |
| TLS cert | ≤398d | cert-manager + ACME |
| API-ключи 3rd-party | по политике | ручная ротация + PR |
| SSH host keys | 1-2 года | Vault SSH engine |

**Правила:**
- Graceful rotation: старый ключ поддерживается во время перекатывания (overlap).
- Логировать все ротации.
- Мониторить expiring-soon-секреты (Prometheus-алерты).

## Передача секретов в приложение

**Варианты (от худшего к лучшему):**

| Способ | Проблема |
|--------|----------|
| CLI-arg (`--password=`) | виден в `ps`, `/proc/<pid>/cmdline` |
| Env var (`DB_PASS=...`) | виден процессам, попадает в дампы |
| Файл `.env` | риск коммита, читаем всем |
| Файл 0600 | ок, но требует init-скрипта |
| Vault Agent sidecar → файл | автоматическое обновление, audit |
| Direct API к Vault/SM | корректно, но надо уметь auth |
| Kubernetes projected volume | volumes монтируются кратко |

**Best practice в K8s:** External Secrets Operator или Vault CSI Driver → файл в tmpfs (0400, owner = app user) → приложение читает при старте и refreshes.

## Обнаружение утечек в CI

**Pre-commit hook:**

```yaml
# .pre-commit-config.yaml
repos:
  - repo: https://github.com/gitleaks/gitleaks
    rev: v8.18.0
    hooks:
      - id: gitleaks
```

**CI-шаг:**

```yaml
# GitHub Actions
- uses: gitleaks/gitleaks-action@v2
  with:
    args: detect --redact --log-level=info
```

**Регулярный скан:** trufflehog по всей истории, еженедельно. При находке — немедленный отзыв + rotation + audit (что делал злоумышленник).

## Чек-лист

- [ ] Ни одного секрета в git (gitleaks в CI)
- [ ] Ни одного секрета в Docker-образах (multi-stage build)
- [ ] Centralized vault (HashiCorp / cloud-native)
- [ ] Separation: dev/staging/prod — разные vault paths и policies
- [ ] Least privilege: каждое приложение видит только свои секреты
- [ ] Auth по identity (K8s SA, IAM role), а не статичный token
- [ ] Audit-log обращений к vault
- [ ] Мониторинг expiring secrets
- [ ] Ротация документирована, репетируется
- [ ] Процедура incident response при leak

## Антипаттерны

| Антипаттерн | Чем опасен | Правильно |
|-------------|-----------|-----------|
| Секреты в `application.yml` в git | утечка через форк, публичные реплики | Vault + Spring Cloud Config |
| Шаренные admin-пароли в команде | nobody knows who did what | per-user accounts + vault dynamic secrets |
| Ключи в Jenkins global credentials без expiry | вечные ключи, попадают в job-logs | credentials binding plugin + rotation |
| Kubernetes Secret без encryption-at-rest | база etcd утекла = секреты в plaintext | KMS provider |
| Hardcoded JWT-secret в image | смена = редеплой всех клиентов | KMS key rotation с кэшем публичных ключей |
| API-ключ в URL (`?token=`) | попадает в access-log, referer, истории | HTTP header `Authorization: Bearer` |
| `git commit -m "fix"` с секретом | остаётся в reflog и на форках | purge через BFG/git-filter-repo + rotation |
| Rollback деплоя возвращает старый ключ | rotation эффект отменён | обновлённые секреты + immutable releases |

## См. также

- [[data-security|Безопасность данных (Data Security)]]
