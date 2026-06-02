---
title: "Вопросы на собеседовании: Secrets Management"
description: "Secrets management: tools (Vault, AWS Secrets Manager, GCP Secret Manager, Azure Key Vault, Doppler), rotation, dynamic secrets, K8s integration (External Secrets Operator), GitOps secrets"
tags:
  - interview
  - security
  - secrets-management-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Secrets Management"
  - "Secrets management interview"
  - "Secret rotation interview"
prerequisites:
  - "[[secrets-management]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Secrets Management`

Secrets management — безопасная работа с чувствительными учётными данными (пароли, API-ключи, сертификаты). Ключевые понятия: **централизованное хранилище, ротация, динамические секреты, шифрование at rest, аудит, least privilege**. Инструменты: **Vault, AWS Secrets Manager, GCP Secret Manager, Azure Key Vault, Doppler**. Критично для безопасности и compliance.

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

**Secret** — чувствительные учётные данные:
- Пароли к базам данных
- API-ключи (Stripe, AWS, ...)
- TLS-сертификаты / приватные ключи
- Ключи шифрования
- OAuth-токены
- SSH-ключи

**Зачем нужно отдельное управление:**
- **Централизация** (одно место против разбросанных .env-файлов)
- **Журналы аудита** (кто и когда обращался)
- **Ротация** (периодическая автоматическая смена)
- **Отзыв** (мгновенное отключение скомпрометированных)
- **Шифрование** (at rest, in transit)
- **Least privilege** (детальный доступ)
- **Compliance** (SOC2, PCI, HIPAA)

## Q2. (!) Bad practices (что НЕ делать)?

❌ **Захардкожены в исходном коде** (коммиты в git → публичная утечка)
❌ **Открытым текстом в конфигурационных файлах**
❌ **Закоммиченные plain `.env`-файлы**
❌ **Slack/email** для передачи секретов
❌ **Один и тот же секрет** в dev / staging / prod
❌ **Долгоживущие общие секреты** (без ротации)
❌ **Вывод секретов** в логи / сообщения об ошибках
❌ **Секреты в Docker-образах** (видны в слоях)
❌ **Слишком широкие IAM-права** (один пользователь имеет доступ ко всему)
❌ **Отсутствие журналирования аудита**

**Реальные инциденты** — утечки на GitHub, массовые брутфорс-атаки на закоммиченные секреты.

**GitGuardian** ежедневно сканирует GitHub на предмет утёкших секретов — находит **миллионы в год**.

## Q3. Static vs dynamic secrets?

**Статические:** хранятся как есть.
- Ротируются вручную
- Долгоживущие
- Примеры: API-ключи, конфигурация

**Динамические:** генерируются по запросу.
- Короткий TTL (минуты-часы)
- Автоматически отзываются по истечении TTL
- Примеры: учётные данные к БД (Vault создаёт пользователя)

**Динамические предпочтительны** для:
- Доступа к базам данных
- Облака (AWS, GCP)
- SSH-доступа
- Сторонних API (где поддерживается)

**Эффект:** ущерб от компрометации ограничен (учётные данные короткоживущие).

## Q4. (!) HashiCorp Vault?

**Vault** — самое популярное OSS-решение для управления секретами.

**Возможности:**
- KV-хранилище
- Динамические секреты (БД, облако, SSH)
- PKI (выпуск сертификатов)
- Transit-шифрование (encryption-as-a-service)
- Multi-tenant (namespaces, политики)
- Множество методов аутентификации

```bash
# Write secret
vault kv put secret/myapp/db password=secret123

# Read
vault kv get secret/myapp/db

# Dynamic DB credentials
vault read database/creds/my-role
# Returns NEW user/password, expires in 1 hour
```

Подробнее — в [Vault](../devops/vault-interview.md).

## Q5. (!) AWS Secrets Manager?

**Управляемое AWS** хранилище секретов.

```bash
# Create
aws secretsmanager create-secret --name myapp/db --secret-string '{"username":"admin","password":"secret"}'

# Retrieve
aws secretsmanager get-secret-value --secret-id myapp/db
```

**Возможности:**
- **Авторотация** (встроенная для RDS, Redshift, DocumentDB)
- **Шифрование** (KMS)
- **Репликация между регионами**
- **Resource-based policies** (IAM)
- **Аудит** через CloudTrail

**Стоимость:** $0.40 за секрет в месяц + плата за API-вызовы.

**Лучше всего для:** AWS-native приложений.

## Q6. AWS Parameter Store vs Secrets Manager?

| Критерий | Parameter Store | Secrets Manager |
|-----------|----------------|-----------------|
| Стоимость | **Бесплатно** (Standard) | $0.40 за секрет в месяц |
| Авторотация | Нет | **Да** |
| Репликация между регионами | Нет | Да |
| Макс. размер значения | 4 KB / 8 KB | 64 KB |
| Лимиты API | Ниже | Выше |
| Сценарий | Конфигурация, простые секреты | Чувствительные учётные данные |

**Распространённый паттерн:**
- **Parameter Store** для конфигурации (URL, feature-флаги, нечувствительные данные)
- **Secrets Manager** для учётных данных (БД, API-ключи)

**Оба шифруются через KMS.**

## Q7. GCP Secret Manager?

```bash
# Create
echo -n "secret_value" | gcloud secrets create my-secret --data-file=-

# Retrieve
gcloud secrets versions access latest --secret="my-secret"
```

**Возможности:**
- Версионирование (несколько версий секрета)
- **Доступ на основе IAM**
- **CMEK** (ключи шифрования, управляемые клиентом)
- Журналы аудита (Cloud Audit Logs)

**Стоимость:** $0.06 за секрет в месяц + плата за доступ.

## Q8. Azure Key Vault?

```bash
az keyvault secret set --vault-name MyVault --name MySecret --value "secretValue"
az keyvault secret show --vault-name MyVault --name MySecret
```

**Возможности:**
- Секреты, **ключи** (с HSM-бэкендом), **сертификаты**
- Soft delete + защита от очистки (purge protection)
- Интеграция с managed identities
- Опция **HSM** (FIPS 140-2 Level 2/3)

**Стоимость:** $0.03 за 10K операций + стоимость сертификатов.

**Лучше всего для:** Azure-native приложений, сценариев с жёстким compliance.

## Q9. Doppler, Infisical, 1Password Secrets?

**Doppler** (SaaS):
- Простой CLI
- Работа с несколькими окружениями
- Синхронизация на множество платформ
- $0-25 за пользователя в месяц

**Infisical** (open-source + SaaS):
- Self-host или облако
- Бесплатная OSS-версия
- Современный UI
- Быстро развивается

**1Password Secrets** (расширение 1Password):
- Привычный UX 1Password
- Функции, ориентированные на разработчиков
- Интеграция с CLI

**Сценарий:** небольшие команды, проще чем Vault, больше возможностей, чем у Parameter Store.

## Q10. (!) K8s Secrets — насколько secure?

**K8s Secrets** — значения в **кодировке Base64**, хранящиеся в etcd.

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
- **Base64 ≠ шифрование** (любой, у кого есть доступ, декодирует)
- Хранятся в etcd (часто **без шифрования**)
- Все администраторы кластера могут читать
- Не предназначены для **высокого уровня безопасности**

**Меры смягчения:**
1. **Шифровать etcd at rest** (`--encryption-provider-config`)
2. **RBAC** на ресурсы Secret
3. **Использовать внешний secret manager** (Vault, AWS Secrets Manager)
4. **Sealed Secrets** для GitOps

**Best practice:** **не используйте «голые» K8s Secrets для настоящих секретов** — используйте внешнее хранилище + синхронизацию.

## Q11. (!) External Secrets Operator?

**External Secrets Operator (ESO)** — K8s-оператор, синхронизирует внешние хранилища секретов в K8s Secrets.

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

**Эффект:** ESO забирает данные из Vault → создаёт K8s Secret. Поды читают его обычным образом.

**Поддерживаемые бэкенды:**
- HashiCorp Vault
- AWS Secrets Manager
- AWS Parameter Store
- GCP Secret Manager
- Azure Key Vault
- 1Password
- Doppler
- И другие

**Самый популярный паттерн** для связки K8s + внешние секреты в 2025 году.

## Q12. Sealed Secrets (Bitnami)?

**Проблема:** хочется хранить секреты в git (GitOps), но git небезопасен.

**Sealed Secrets** — зашифрованные секреты, которые можно безопасно коммитить в git.

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

**Контроллер Sealed Secrets** в кластере расшифровывает → создаёт K8s Secret.

**Плюсы:** дружит с GitOps. **Минусы:** ключи привязаны к конкретному кластеру.

## Q13. SOPS для encrypted secrets в git?

**SOPS (Mozilla)** — шифрует файлы (YAML, JSON, ENV) с разными бэкендами (KMS, PGP, age).

```bash
# Encrypt с AWS KMS
sops --encrypt --kms arn:aws:kms:... secrets.yaml > secrets.enc.yaml

# Decrypt
sops --decrypt secrets.enc.yaml
```

**Результат:** YAML с зашифрованными значениями (ключи остаются открытыми) — можно безопасно коммитить в git.

```yaml
db:
  password: ENC[AES256_GCM,data:abc123...,type:str]
```

**Интеграция с инструментами:**
- Helm Secrets
- ArgoCD
- Flux
- Terraform

**Сценарий:** GitOps, секреты для IaC, конфигурации для нескольких окружений.

## Q14. (!) Secret rotation?

**Зачем:** у скомпрометированного секрета ограниченное окно действия.

**Стратегии ротации:**

**Вручную:**
- Раз в квартал / год
- Риск: про неё забывают, болезненный процесс

**Авторотация:**
- AWS Secrets Manager: автоматически для RDS, Redshift, DocumentDB
- Динамические секреты Vault: каждый запрос = новые учётные данные
- Кастомные Lambda-функции: для чего угодно

**Паттерн:**
1. Создать новые учётные данные
2. Обновить приложения, чтобы они использовали новые
3. Проверить
4. Отозвать старые

**Подводные камни:**
- **Приложения должны перечитывать** секреты (в идеале без перезапуска)
- **База данных** обновляется одновременно
- **Пулы соединений** должны обновляться

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

**Каждый экземпляр приложения** получает уникальные учётные данные. По истечении TTL → отзыв.

**Преимущества:**
- **Учётные данные на каждый экземпляр** (точный аудит)
- **Автоматический отзыв**
- **Короткое окно компрометации**

**Поддерживаются:** PostgreSQL, MySQL, MongoDB, Cassandra, Oracle, Redis, AWS, GCP и др.

## Q16. Secrets revocation?

**Скомпрометированный секрет** нужно отозвать немедленно.

**Статические секреты:**
1. Сгенерировать новый
2. Обновить приложения
3. Удалить старый в хранилище секретов
4. Пересоздать соединения

**Динамические секреты:**
- Vault: API отзыва lease
- Cloud IAM: удалить IAM-пользователя / роль

**Скорость важна:** чем быстрее отзыв, тем меньше ущерб от компрометации.

**Журналы аудита** — убедиться, что после отзыва секрет больше не использовался.

## Q17. (!) Как app reads secrets?

**Паттерны:**

**1. Прямой вызов API:**
```python
import boto3
client = boto3.client('secretsmanager')
secret = client.get_secret_value(SecretId='myapp/db')
```

**2. Sidecar (Vault Agent):**
- Sidecar забирает секреты, пишет в общий volume
- Приложение читает файл

**3. Init-контейнер:**
- Init-контейнер забирает секреты, монтирует в основной контейнер

**4. Оператор** (External Secrets Operator):
- Синхронизация в K8s Secret
- Приложение читает K8s Secret обычным образом

**5. Переменные окружения (внедряются при старте):**
- K8s Secrets монтируются как env
- Vault Agent рендерит шаблоны

**Best practice:** предпочитайте файлы (смонтированные volume) переменным окружения (env-переменные утекают в дочерние процессы и логи).

## Q18. Vault Agent / Sidecar Injector?

**Vault Agent** — демон рядом с приложением:
- Автоматически аутентифицируется в Vault
- Забирает секреты
- Кэширует
- Рендерит шаблоны → файлы
- Обновляет lease

**Sidecar Injector (K8s):**
- Автоматическое внедрение sidecar-а Vault Agent по аннотациям
- Приложение читает секреты из каталога `/vault/secrets/`

```yaml
metadata:
  annotations:
    vault.hashicorp.com/agent-inject: "true"
    vault.hashicorp.com/role: "myapp"
    vault.hashicorp.com/agent-inject-secret-db: "secret/myapp/db"
```

**Эффект:** приложениям не нужен Vault SDK — только чтение файлов.

## Q19. AWS IRSA (IAM Roles for Service Accounts)?

**IRSA** — под в EKS получает **IAM-роль** через ServiceAccount.

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: my-app
  annotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::123:role/MyAppRole
```

**Эффект:** под использует AWS SDK без учётных данных в env / файлах. AWS автоматически выдаёт временные учётные данные.

**Учётные данные короткоживущие** (1 час, авторотация).

**Никакого распространения секретов** — доступ на основе IAM.

**Эквиваленты:**
- **GCP Workload Identity** — для GKE
- **Azure Workload Identity** — для AKS

**Best practice:** предпочитайте cloud-native IAM распространению секретов.

## Q20. (!) Как handle secrets в GitOps?

**Проблема:** GitOps = git как источник истины. **Но секреты нельзя класть в git открытым текстом.**

**Решения:**

1. **Sealed Secrets** — зашифрованы, можно коммитить
2. **SOPS** — зашифрованные файлы в git
3. **External Secrets Operator** — ссылки на внешнее хранилище (Vault, AWS)
4. **Bitnami Secrets Manager**
5. **CSI Secrets Store Driver**

**Паттерн:**
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

**Сам секрет** хранится только в Vault. В git лежит **ссылка**, а не значение.

## Q21. (!) Best practices?

1. **Никогда не коммитьте** секреты в систему контроля версий
2. **Централизованное** хранилище секретов (Vault, AWS Secrets Manager)
3. **Авторотация** для долгоживущих секретов
4. **Динамические секреты** где возможно (БД, облако)
5. **Короткие TTL** для учётных данных
6. Доступ по принципу **least privilege**
7. **Журналирование аудита** всех обращений
8. **Шифрование at rest + in transit**
9. **По возможности никаких секретов в env-переменных** (используйте смонтированные файлы)
10. **Cloud IAM** вместо распространения секретов (IRSA, Workload Identity)
11. **Сканирование секретов** в CI (gitleaks, GitGuardian)
12. **Немедленная ротация** при подозрении на компрометацию
13. **Разные секреты** для каждого окружения (dev/staging/prod)
14. План **disaster recovery** для хранилища секретов
15. **MFA** для доступа к UI управления секретами

## Q22. (!) Common pitfalls?

1. **Захардкоженные секреты** — утекают через git, логи
2. **Один секрет на все окружения** — компрометация dev = компрометация prod
3. **Отсутствие ротации** — старые учётные данные накапливаются
4. **Слишком широкие права** — сервис имеет доступ ко всем секретам
5. **Логирование секретов** (тела запросов, сообщения об ошибках)
6. **Секреты в слоях Docker-образа** (видны)
7. **Отсутствие аудита** — непонятно, скомпрометировано или нет
8. **Потеря unseal-ключей Vault** — Vault невозможно восстановить
9. **Отсутствие бэкапа** хранилища секретов (катастрофическая потеря)
10. **Передача через Slack/email**
11. **Переиспользование** API-ключей между приложениями
12. **Отсутствие задокументированной процедуры отзыва**
13. **Секреты в логах CI/CD** (скрыты, но находимы)

**Инструменты для обнаружения:**
- **gitleaks, GitGuardian, TruffleHog** — сканирование git на утёкшие секреты
- **CodeQL, Semgrep** — статический анализ
- **Trivy, Grype** — сканирование контейнеров

В **2025 году** управление секретами — это **базовая гигиена безопасности**. Из-за отсутствия этих практик каждый год случается множество утечек.

---

## See also

- [HashiCorp Vault](../devops/vault-interview.md) — main tool
- [Zero Trust](zero-trust-interview.md) — context
- [mTLS](mtls-interview.md) — alternative auth
- [Supply Chain Security](supply-chain-security-interview.md)
- [Application Security](application-security-interview.md) — общая
- [OAuth2](oauth2-interview.md) — token management
- [JWT](jwt-interview.md) — short-lived tokens
- [AWS](../cloud/aws-interview.md) — Secrets Manager, Parameter Store
- [GCP](../cloud/gcp-interview.md) — Secret Manager
- [Azure](../cloud/azure-interview.md) — Key Vault
- [Kubernetes](../devops/kubernetes-interview.md) — Secrets, ESO
- [Микросервисы](../architecture/microservices-interview.md) — context
- [Cloud-native Patterns](../cloud/cloud-native-patterns-interview.md) — context
- [Git](../devops/git-interview.md) — secret scanning

- [Application Security](application-security-interview.md)
- [Authentication and Authorization Patterns](authentication-authorization-patterns-interview.md)
- [JWT](jwt-interview.md)
- [mTLS (Mutual TLS)](mtls-interview.md)
- [OAuth2](oauth2-interview.md)
- [OWASP Top 10](owasp-top10-interview.md)
- [Шпаргалка: Управление секретами (Secrets Management](../../security/data/secrets-management.md) — теория
