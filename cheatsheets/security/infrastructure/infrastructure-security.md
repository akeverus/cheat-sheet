---
title: "Безопасность инфраструктуры (Infrastructure Security)"
description: "Сетевая безопасность, хосты и ОС, контейнеры, Kubernetes, облако и принципы минимально необходимого доступа."
tags:
  - security
  - infrastructure
  - infrastructure-security
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Безопасность инфраструктуры (Infrastructure Security)

Сетевая безопасность, хосты и ОС, контейнеры, `Kubernetes`, облако и принципы минимально необходимого доступа.


## Полезные ссылки

### Официальная документация
- [Kubernetes Security](https://kubernetes.io/docs/concepts/security/) — безопасность `Kubernetes`
- [CIS Benchmarks](https://www.cisecurity.org/cis-benchmarks/) — бенчмарки безопасности для различных систем
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework) — фреймворк для управления кибербезопасностью
- [AWS Security Best Practices](https://aws.amazon.com/security/security-resources/) — лучшие практики безопасности `AWS`
- [Azure Security Documentation](https://docs.microsoft.com/en-us/azure/security/) — документация по безопасности `Azure`

### Ресурсы
- [OWASP Container Security](https://cheatsheetseries.owasp.org/cheatsheets/Docker_Security_Cheat_Sheet.html) — рекомендации по безопасности контейнеров
- [Kubernetes Security Best Practices](https://kubernetes.io/docs/concepts/security/pod-security-standards/) — стандарты безопасности подов

### См. также
- [Data Security](../data/data-security.md) — безопасность данных и шифрование
- [Secrets Management](../data/secrets-management.md) — управление секретами
- [Security Practices](../security-practices.md) — практики безопасности


- [TLS / SSL: handshake, сертификаты, конфигурация](tls-ssl.md)
- [Spring Boot — Полное руководство](../../frameworks/spring/spring-boot.md)
## Содержание

- [Сетевая безопасность](#сетевая-безопасность)
  - [Публичные сервисы](#публичные-сервисы)
  - [Сетевая сегментация](#сетевая-сегментация)
  - [Межсетевые экраны](#межсетевые-экраны)
  - [WAF](#waf)
- [Хосты и операционные системы](#хосты-и-операционные-системы)
  - [Обновления](#обновления)
  - [Минимизация поверхности атаки](#минимизация-поверхности-атаки)
  - [Настройка SSH](#настройка-ssh)
  - [Централизованное логирование](#централизованное-логирование)
  - [Практические шаги](#практические-шаги)
- [Контейнеры и образы](#контейнеры-и-образы)
  - [Безопасные образы](#безопасные-образы)
  - [Сборка](#сборка)
  - [Сканирование образов](#сканирование-образов)
  - [Ограничение прав контейнера](#ограничение-прав-контейнера)
- [Kubernetes и оркестрация](#kubernetes-и-оркестрация)
  - [RBAC](#rbac)
  - [NetworkPolicy](#networkpolicy)
  - [Pod Security Standards](#pod-security-standards)
  - [OPA/Kyverno](#opakyverno)
  - [Практический минимум](#практический-минимум)
- [Облако](#облако)
  - [IAM](#iam)
  - [Управляемые сервисы](#управляемые-сервисы)
  - [Шифрование](#шифрование)
  - [Логирование и мониторинг](#логирование-и-мониторинг)
  - [Практические рекомендации](#практические-рекомендации)
- [Минимальный чек‑лист](#минимальный-чеклист)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Сетевая безопасность

Фундамент: контроль того, какие системы общаются друг с другом, какие порты открыты, какой трафик разрешён.

**Принцип:** по умолчанию всё запрещено разрешаются только необходимые соединения.

### Публичные сервисы

- Доступ только через `80`/`443`, через `load balancer` / `reverse proxy`.
- Админка — **не** из интернета, только через `IP`-фильтрацию или `VPN`.
- `WAF` для фильтрации подозрительных запросов.
- `DDoS`-защита на уровне провайдера/облака.

Пример `security group` (`AWS`):

```yaml
SecurityGroup:
  Ingress:
    - Port: 443
      Protocol: TCP
      Source: 0.0.0.0/0
      Description: HTTPS from internet
    - Port: 80
      Protocol: TCP
      Source: 0.0.0.0/0
      Description: HTTP redirect to HTTPS
  Egress:
    - Port: 8080
      Protocol: TCP
      Source: internal-app-sg
      Description: To application servers
```

### Сетевая сегментация

Разделение инфраструктуры на зоны с разным уровнем доверия:

| Подсеть | Назначение | Пример |
|---------|-----------|--------|
| Публичная | Балансировщики нагрузки | `10.0.1.0/24` |
| Приложения | Серверы приложений | `10.0.2.0/24` |
| Данные | Базы данных | `10.0.3.0/24` |
| Управление | Мониторинг, администрирование | `10.0.4.0/24` |

У каждой подсети — свои правила `firewall`. БД доступна только серверам приложений, не из интернета.

### Межсетевые экраны

- Политика «всё запрещено по умолчанию».
- Логировать все блокировки — для обнаружения атак и отладки.
- Регулярный ревью правил — убирать устаревшие.

```bash
iptables -P INPUT DROP
iptables -P FORWARD DROP
iptables -P OUTPUT ACCEPT

iptables -A INPUT -i lo -j ACCEPT
iptables -A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT
iptables -A INPUT -p tcp --dport 22 -s 10.0.4.0/24 -j ACCEPT
iptables -A INPUT -p tcp --dport 80 -j ACCEPT
iptables -A INPUT -p tcp --dport 443 -j ACCEPT
iptables -A INPUT -j LOG --log-prefix "DROPPED: "
```

### `WAF`

`WAF` защищает от `SQL Injection`, `XSS`, `CSRF` и подозрительных паттернов. Размещать перед приложением, обновлять правила, использовать режим обучения перед включением блокировок.


## Хосты и операционные системы

Компрометация одного хоста — отправная точка для атаки на всю инфраструктуру.

### Обновления

- **Критичные патчи** — немедленно (даже внепланово).
- **Регулярные обновления** — окна патчинга с предварительным тестированием на `dev`/`stage`.
- Для некритичных систем — автоматические обновления безопасности.

```bash
apt-get install unattended-upgrades

cat > /etc/apt/apt.conf.d/50unattended-upgrades <<EOF
Unattended-Upgrade::Allowed-Origins {
    "\${distro_id}:\${distro_codename}-security";
};
Unattended-Upgrade::Remove-Unused-Kernel-Packages "true";
Unattended-Upgrade::Remove-Unused-Dependencies "true";
EOF
```

### Минимизация поверхности атаки

- Удалять неиспользуемые пакеты и сервисы.
- Отключать ненужные сетевые сервисы.
- Минимальные дистрибутивы (`alpine`, `distroless`).
- Регулярный аудит установленного ПО.

```bash
dpkg -l                                            # все пакеты
apt list --upgradable                              # устаревшие
systemctl list-units --type=service --state=running # запущенные сервисы
```

### Настройка `SSH`

```bash
# /etc/ssh/sshd_config
PasswordAuthentication no
PubkeyAuthentication yes
PermitRootLogin no
AllowUsers admin deploy
MaxAuthTries 3
MaxSessions 2
Protocol 2
ClientAliveInterval 300
ClientAliveCountMax 2
```

- Только `SSH`-ключи, пароли отключены.
- Логин под `root` запрещён.
- `fail2ban` для блокировки после неудачных попыток.
- Прямой `SSH` на `prod` — через `bastion`-хост или `VPN`.

### Централизованное логирование

Логи со всех серверов в одном месте — для обнаружения атак и аудита.

- `rsyslog` центральный сервер.
- `ELK Stack`, `Splunk` — для анализа и визуализации.
- Облачные решения: `CloudWatch` (`AWS`), `Azure Monitor`, `Cloud Logging` (`GCP`).

```bash
# /etc/rsyslog.conf
*.* @@log-server.example.com:514
```

### Практические шаги

- Конфигурационный менеджмент (`Ansible`/`Chef`/`Puppet`) или `IaC` для единообразия.
- Мониторинг изменений конфигурации (`AIDE`, `Tripwire`).
- Регулярный аудит прав доступа, удаление неиспользуемых учёток.


## Контейнеры и образы

### Безопасные образы

- **Минимальный базовый образ:** `distroless` (только `runtime`) или `alpine`.
- Только **официальные образы** из доверенных реестров.
- Фиксация версий тегов — **не** `latest`.
- **Не запускать от `root`** — создать непривилегированного пользователя.
- **Никаких секретов в образе** — передавать через переменные окружения, `Docker secrets` или `Vault`.

```dockerfile
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java17-debian11
WORKDIR /app
COPY --from=builder /app/target/app.jar app.jar
USER 65534:65534
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Сборка

- Фиксация версий базовых образов и их регулярное обновление.
- Сканеры уязвимостей в `CI` — образы с критичными `CVE` не попадают в прод.
- `Multi-stage` сборки — инструменты и временные файлы не попадают в финальный образ.
- `.dockerignore` — исключить `.git`, `.env`, `*.log`, `target/`.

```text
.git
.gitignore
*.md
.env
*.log
target/
.idea/
*.iml
```

### Сканирование образов

- `Trivy` — быстрый, простой, идеален для `CI`.
- `Clair` — детальный анализ по слоям.
- **`Snyk`, `Aqua Security`** — коммерческие решения.

```yaml
# .github/workflows/security.yml
name: Security Scan
on: [push, pull_request]
jobs:
  scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Build image
        run: docker build -t myapp:latest .
      - name: Run Trivy vulnerability scanner
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: 'myapp:latest'
          format: 'sarif'
          output: 'trivy-results.sarif'
```

### Ограничение прав контейнера

- Непривилегированный пользователь.
- `Read-only` файловая система где возможно.
- Ограниченные `capabilities`.
- `--privileged` — **только** в крайних случаях.


## `Kubernetes` и оркестрация

По умолчанию многие механизмы безопасности **не включены** — без настройки кластер уязвим.

### `RBAC`

Принцип минимальных привилегий — каждый пользователь/сервис имеет только необходимые права.

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: pod-reader
  namespace: production
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: read-pods
  namespace: production
subjects:
- kind: User
  name: developer
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role
  name: pod-reader
  apiGroup: rbac.authorization.k8s.io
```

- Отдельные роли для разработчиков, операторов, администраторов.
- `ServiceAccount` для приложений с минимальными правами.
- Регулярный аудит.

### `NetworkPolicy`

Без `NetworkPolicy` все поды общаются друг с другом. Это нужно ограничить.

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: db-access
  namespace: production
spec:
  podSelector:
    matchLabels:
      app: database
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: backend
    ports:
    - protocol: TCP
      port: 5432
```

### Pod Security Standards

| Уровень | Суть |
|---------|------|
| `Restricted` | Максимально строгие политики — для прода |
| `Baseline` | Минимальные требования — блокирует только опасное |
| `Privileged` | Без ограничений — **не** для прода |

### `OPA`/`Kyverno`

Для сложных политик, не покрываемых стандартными механизмами:

- `OPA` — универсальный движок политик (язык `Rego`).
- `Kyverno` — специализированный для `Kubernetes` (`YAML`-политики).

```yaml
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata:
  name: require-labels
spec:
  validationFailureAction: enforce
  rules:
  - name: check-labels
    match:
      resources:
        kinds:
        - Pod
    validate:
      message: "Labels 'app' and 'version' are required"
      pattern:
        metadata:
          labels:
            app: "?*"
            version: "?*"
```

### Практический минимум

- Один `namespace` на окружение/проект с отдельными `Role`/`RoleBinding`.
- Запрет `hostPath`-томов и `privileged`-контейнеров через `PodSecurity`/`OPA`/`Kyverno`.
- Ревью `Ingress`-манифестов — какие хосты/пути открыты наружу.
- `Secrets` для чувствительных данных (не `ConfigMap`).
- Регулярное обновление кластера и компонентов.
- Аудит событий кластера.


## Облако

Ответственность разделена: провайдер отвечает за платформу, клиент — за настройку своих ресурсов.

### `IAM`

- **Роли** для сервисов вместо статических ключей (например, `EC2`-инстанс с ролью для `S3`).
- `MFA` для административных аккаунтов.
- Регулярный ревью прав доступа, принцип минимальных привилегий.

```json
{
  "Version": "2012-10-17",
  "Statement": [{
      "Effect": "Allow",
    "Action": ["s3:GetObject", "s3:ListBucket"],
    "Resource": ["arn:aws:s3:::my-bucket/*", "arn:aws:s3:::my-bucket"],
      "Condition": {
      "IpAddress": { "aws:SourceIp": "10.0.0.0/8" }
    }
  }]
}
```

### Управляемые сервисы

Используйте управляемые БД (`RDS`, `Cloud SQL`), очереди (`SQS`, `Pub/Sub`), кэши (`ElastiCache`). Провайдер берёт на себя обновления, бэкапы, мониторинг. Но правильная настройка (доступ из нужных подсетей, а не из интернета) — ваша зона ответственности.

### Шифрование

- **В покое** — `EBS`, `S3`, `RDS` с шифрованием через `KMS`.
- **В транзите** — `TLS` для всех соединений.
- **Управление ключами** — через `KMS`/`Key Vault`/`Cloud KMS`.

```bash
aws s3api put-bucket-encryption \
  --bucket my-secure-bucket \
  --server-side-encryption-configuration '{
    "Rules": [{
      "ApplyServerSideEncryptionByDefault": {
        "SSEAlgorithm": "aws:kms",
        "KMSMasterKeyID": "arn:aws:kms:..."
      }
    }]
  }'
```

### Логирование и мониторинг

| Облако | Аудит | Метрики/логи | Безопасность |
|--------|-------|-------------|-------------|
| `AWS` | `CloudTrail` | `CloudWatch` | `Security Hub`, `GuardDuty` |
| `Azure` | `Activity Log` | `Azure Monitor` | `Security Center` |
| `GCP` | `Cloud Audit Logs` | `Cloud Logging` | `Security Command Center` |

Включить логирование всех админских действий. Алерты на аномалии. Хранить логи ≥ 90 дней.

### Практические рекомендации

- Отдельные аккаунты/проекты для `prod`, `dev`, `stage`.
- `IAM`-роли для сервисов, не статические ключи.
- Инфраструктура как код (`Terraform`/`CloudFormation`) — версионирование, ревью, воспроизводимость.
- Регулярный аудит конфигурации и использование облачных сервисов безопасности.


## Минимальный чек‑лист

**Обязательно:**

- [ ] Внешние порты сведены к минимуму (только `80`/`443` для веба).
- [ ] Обновления ОС и пакетов устанавливаются регулярно.
- [ ] Контейнеры не запускаются от `root`, образы сканируются на `CVE`.
- [ ] `RBAC` включён в `Kubernetes`, настроены `NetworkPolicy`.
- [ ] В облаке — `IAM`-роли для сервисов, не «вечные» ключи.
- [ ] Логи собираются централизованно, хранятся ≥ 90 дней.

**Дополнительно:**

- [ ] Инфраструктура описана как код (`Terraform`/`CloudFormation`).
- [ ] Регулярный процесс патчинга с документированными окнами.
- [ ] Периодический запуск `CIS`-бенчмарков и `config`-сканеров.


## Решение проблем

| Симптом | Вероятная причина | Решение |
|---------|------------------|---------|
| Не удаётся подключиться к сервису извне | Правила `firewall`/`security groups` блокируют трафик | Проверить порты, `security groups`, таблицы маршрутизации |
| Контейнер не подключается к БД | `NetworkPolicy` блокирует трафик, или неверные `security groups` | Проверить `NetworkPolicy`, `security groups`, `DNS`-резолюцию |
| Образ не проходит сканирование | Устаревший базовый образ или `CVE` в зависимостях | Обновить базовый образ и зависимости, накатить патчи |
| Нет прав в `Kubernetes` | Неправильная `Role`/`RoleBinding` | Проверить `RBAC`, `namespace`, политики безопасности |
| `WAF` блокирует легитимный трафик | Слишком строгие правила | Использовать режим обучения, добавить исключения, тонкая настройка правил |
| Утечка данных из облачного хранилища | Публичный доступ к бакету, широкие `IAM`-политики | Закрыть публичный доступ, пересмотреть `IAM`, включить шифрование и логирование |
| Компрометация контейнера | Уязвимость в образе, запуск от `root`, утечка секретов | Обновить образ, запустить от непривилегированного пользователя, ротировать секреты |
| Несанкционированный доступ к хосту | Слабые пароли, устаревшее ПО, открытый `SSH` | `SSH`-ключи вместо паролей, обновления, `bastion`-хост, `fail2ban` |


## Частые вопросы

**Как часто обновлять инфраструктуру?**
Критичные патчи — немедленно. Регулярные обновления безопасности — ежемесячно/ежеквартально. Всё тестировать на `dev`/`stage` перед применением в `prod`.

**Нужен ли `WAF` для всех приложений?**
Рекомендуется для публичных веб-приложений и обязателен при работе с чувствительными данными или требованиях `PCI DSS`. Для внутренних сервисов достаточно `firewall`.

**Как защитить гибридное облако?**
Единые политики безопасности для всех окружений, централизованный `IAM`/`SSO`, единое логирование, `VPN`/`private link` между площадками.

**Автоматизация vs ручной контроль?**
Автоматизация — для рутины (сканирование, мониторинг, применение политик). Ручной контроль — для критичных изменений и расследования инцидентов. Одно дополняет другое.

**Как защититься от инсайдеров?**
Минимальные привилегии, разделение обязанностей, аудит всех действий, `MFA` для критичных операций, мониторинг аномальной активности.

**Нужно ли шифровать всё?**
Обязательно: чувствительные данные в покое, данные в транзите через публичные сети, бэкапы. Желательно: все данные в покое и внутренние соединения.

**Безопасность микросервисов?**
`Service mesh` (`Istio`/`Linkerd`) для `mTLS` между сервисами, `NetworkPolicy` для сетевой изоляции, централизованное управление секретами (`Vault`), единые стандарты безопасности.

**Что делать при обнаружении уязвимости?**
Оценить `CVSS`, определить затронутые системы, применить временные меры (блокировка через `firewall`), накатить патч, проверить исправление, задокументировать.

**Безопасность в `DevOps`?**
`SAST`/`SCA` в `CI`, сканирование образов при сборке, политики через `OPA`/`Kyverno`, секреты в `Vault`, `DAST` на `staging`. Подробнее — в [Security Practices](../security-practices.md).
