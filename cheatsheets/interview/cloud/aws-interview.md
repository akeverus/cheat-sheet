---
title: "Вопросы на собеседовании: AWS"
description: "AWS базовый: EC2, S3, RDS, DynamoDB, VPC, IAM, CloudFront, Route53, ELB, Auto Scaling, CloudWatch, well-architected framework, regions/AZs, pricing, security best practices"
tags:
  - interview
  - cloud
  - aws-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "AWS"
  - "AWS interview"
  - "Amazon Web Services interview"
prerequisites:
  - "[[aws-basics]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `AWS`

`AWS (Amazon Web Services)` — лидер облачного рынка (2025: около 32% доли). Сотни сервисов: от вычислений (EC2) до AI (Bedrock, SageMaker). На собеседовании бэкендеру важно знать основные сервисы (EC2, S3, RDS, IAM, VPC), управляемые базы данных, сети, CDN, мониторинг и **Well-Architected Framework** (5 опор-pillars).

## Полезные ссылки

### Официальная документация и авторитетные источники

- [AWS Documentation](https://docs.aws.amazon.com/)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
- [AWS Free Tier](https://aws.amazon.com/free/)
- [AWS Architecture Center](https://aws.amazon.com/architecture/)
- [AWS Whitepapers](https://aws.amazon.com/whitepapers/)
- [Last Week in AWS (newsletter, Corey Quinn)](https://www.lastweekinaws.com/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое AWS и какие основные категории сервисов?](#q1--что-такое-aws-и-какие-основные-категории-сервисов)
- [Q2. (!) Regions, Availability Zones, Edge Locations?](#q2--regions-availability-zones-edge-locations)
- [Q3. AWS pricing model?](#q3-aws-pricing-model)

**Compute**
- [Q4. (!) EC2 — instance types, families?](#q4--ec2--instance-types-families)
- [Q5. (!) Reserved Instances, Spot Instances, Savings Plans?](#q5--reserved-instances-spot-instances-savings-plans)
- [Q6. AMI, EBS, instance store?](#q6-ami-ebs-instance-store)
- [Q7. (!) Auto Scaling Groups?](#q7--auto-scaling-groups)
- [Q8. Lambda — кратко (deep dive в отдельном файле)?](#q8-lambda--кратко-deep-dive-в-отдельном-файле)

**Storage**
- [Q9. (!) S3 — основные понятия?](#q9--s3--основные-понятия)
- [Q10. (!) S3 storage classes?](#q10--s3-storage-classes)
- [Q11. EBS vs EFS vs S3?](#q11-ebs-vs-efs-vs-s3)
- [Q12. S3 versioning, lifecycle policies?](#q12-s3-versioning-lifecycle-policies)

**Databases**
- [Q13. (!) RDS — какие движки, особенности?](#q13--rds--какие-движки-особенности)
- [Q14. (!) Aurora vs RDS PostgreSQL/MySQL?](#q14--aurora-vs-rds-postgresqlmysql)
- [Q15. (!) DynamoDB — что особенного?](#q15--dynamodb--что-особенного)
- [Q16. ElastiCache (Redis, Memcached)?](#q16-elasticache-redis-memcached)
- [Q17. Other DBs (DocumentDB, Neptune, Timestream)?](#q17-other-dbs-documentdb-neptune-timestream)

**Networking**
- [Q18. (!) VPC, subnets, route tables?](#q18--vpc-subnets-route-tables)
- [Q19. (!) Security Groups vs NACLs?](#q19--security-groups-vs-nacls)
- [Q20. ALB, NLB, CLB?](#q20-alb-nlb-clb)
- [Q21. CloudFront CDN?](#q21-cloudfront-cdn)
- [Q22. Route53?](#q22-route53)
- [Q23. (!) NAT Gateway, Internet Gateway?](#q23--nat-gateway-internet-gateway)

**IAM и Security**
- [Q24. (!) IAM — Users, Roles, Policies?](#q24--iam--users-roles-policies)
- [Q25. (!) IAM роли для EC2/Lambda — best practice?](#q25--iam-роли-для-ec2lambda--best-practice)
- [Q26. KMS, Secrets Manager, Parameter Store?](#q26-kms-secrets-manager-parameter-store)

**Messaging и интеграции**
- [Q27. (!) SQS vs SNS vs EventBridge?](#q27--sqs-vs-sns-vs-eventbridge)
- [Q28. Kinesis vs MSK (Kafka)?](#q28-kinesis-vs-msk-kafka)

**Monitoring**
- [Q29. (!) CloudWatch — metrics, logs, alarms?](#q29--cloudwatch--metrics-logs-alarms)
- [Q30. X-Ray для tracing?](#q30-x-ray-для-tracing)

**Best practices**
- [Q31. (!) Well-Architected Framework — 5 pillars?](#q31--well-architected-framework--5-pillars)
- [Q32. (!) Multi-AZ vs Multi-Region?](#q32--multi-az-vs-multi-region)
- [Q33. Cost optimization tips?](#q33-cost-optimization-tips)
- [Q34. (!) Какие частые ошибки в AWS?](#q34--какие-частые-ошибки-в-aws)

## Q1. (!) Что такое AWS и какие основные категории сервисов?

`AWS` — облачная платформа от Amazon (с 2006). Лидер рынка с долей около 32% (2025).

**Категории:**
- **Compute** (вычисления) — EC2, Lambda, ECS, EKS, Fargate, Batch
- **Storage** (хранилище) — S3, EBS, EFS, FSx, Glacier
- **Databases** (базы данных) — RDS, Aurora, DynamoDB, ElastiCache, Redshift, Neptune
- **Networking** (сети) — VPC, Route53, CloudFront, ELB, API Gateway
- **Security** (безопасность) — IAM, KMS, Secrets Manager, WAF, Shield, GuardDuty
- **Analytics** (аналитика) — Athena, EMR, Glue, Kinesis, MSK, QuickSight
- **AI/ML** — SageMaker, Bedrock, Comprehend, Rekognition
- **Developer Tools** (инструменты разработчика) — CodePipeline, CodeBuild, CodeDeploy
- **Management** (управление) — CloudWatch, CloudFormation, CloudTrail, Config

Более 200 сервисов. Большинству бэкендеров знакомо около 20.

## Q2. (!) Regions, Availability Zones, Edge Locations?

```
Region (us-east-1, eu-central-1)
  ├── AZ a (data center)
  ├── AZ b (data center)
  └── AZ c (data center)
```

**Region** (регион) — географическая зона (Ohio, Frankfurt, Tokyo). На 2025 год — более 30 регионов по миру.

**Availability Zone (AZ)** — физический дата-центр внутри региона (3 и более на регион). Изолированы по питанию и сети → если один AZ упал, остальные работают.

**Edge Location** — точки присутствия CDN (CloudFront), более 600 по миру. Дают низкую задержку для пользователей.

**Рекомендация:** **Multi-AZ** для высокой доступности (RDS Multi-AZ, EC2 в нескольких AZ).

## Q3. AWS pricing model?

**Pay-as-you-go** (плати по факту использования) для большинства сервисов:
- **EC2:** за час или за секунду (Linux)
- **S3:** за хранимый GB + за запросы + за исходящий трафик
- **Lambda:** за вызов + за GB-секунды вычислений
- **RDS:** за час работы инстанса + хранилище + IOPS

**Что меняет стоимость:**
- **Передача данных:** внутри региона — обычно бесплатно, **между регионами — дорого**, **исходящий трафик в интернет — дорого**
- **Reserved/Savings Plans** — скидки за обязательство по объёму
- **Spot** — скидка до 90% (инстанс могут прервать)

**Free Tier:** 12 месяцев для нового аккаунта + всегда-бесплатные услуги (S3 5 GB, Lambda 1 млн запросов в месяц и т.д.)

**Важно:** **исходящий трафик (data transfer out)** — самая частая причина неприятных счетов.

## Q4. (!) EC2 — instance types, families?

**Семейства инстансов:**

| Семейство | Назначение | Примеры |
|--------|-----------|----------|
| **T** (Burstable) | Веб-серверы, dev | t3.micro, t4g.medium |
| **M** (General Purpose) | Сбалансированные | m5.large, m7g.xlarge |
| **C** (Compute-optimized) | Нагрузка на CPU | c5.2xlarge, c7g |
| **R** (Memory-optimized) | Кеши в памяти, БД | r5.4xlarge, r7g |
| **X** (Memory-extreme) | Большие БД в памяти | x1.32xlarge |
| **I** (Storage-optimized) | NVMe SSD | i3en.large |
| **G, P** (GPU) | ML, графика | g5.xlarge, p4d |
| **Inf, Trn** | AI inference/training | inf2, trn1 |

**Расшифровка имени:** `c7g.xlarge` = семейство C, поколение 7, **G**raviton (ARM), размер xlarge.

**Graviton (ARM)** — обычно на 20% дешевле и лучше по соотношению производительность/цена. Выбор по умолчанию в 2025.

## Q5. (!) Reserved Instances, Spot Instances, Savings Plans?

| Тип | Скидка | Обязательство | Когда применять |
|-----|----------|------------|----------|
| **On-Demand** | 0% | Нет | Dev, непредсказуемая нагрузка |
| **Spot** | до 90% | Могут прервать | Batch, отказоустойчивые задачи |
| **Reserved Instances (RI)** | до 72% | 1–3 года | Предсказуемая нагрузка |
| **Savings Plans** | до 72% | 1–3 года, обязательство в $/час | Гибче чем RI |

**Savings Plans** гибкие — применяются к разным типам инстансов в пределах региона.

**Spot Instances** подходят для:
- обучения ML-моделей
- пакетной (batch) обработки
- CI/CD-задач
- stateless веб-серверов (с auto-scaling)

**Не подходят для:** баз данных и stateful-сервисов с длительным временем жизни.

## Q6. AMI, EBS, instance store?

**AMI (Amazon Machine Image)** — шаблон для EC2-инстансов. Содержит ОС, ПО и конфигурации.

**EBS (Elastic Block Store)** — постоянное блочное хранилище, подключаемое к EC2. Данные сохраняются при stop/start.

**Instance store** — локальный NVMe SSD на физическом хосте. **Теряется** при остановке. Очень быстрый, для временных данных.

**Типы EBS-томов:**
- **gp3** (по умолчанию в 2025) — General Purpose SSD, сбалансированный
- **io2** — Provisioned IOPS, для БД
- **st1** — HDD, оптимизирован под throughput
- **sc1** — HDD, холодные данные

## Q7. (!) Auto Scaling Groups?

**ASG** — автоматическое масштабирование EC2-инстансов на основе метрик.

```
Min: 2, Max: 10, Desired: 4

Metric: CPU > 70% → scale up (+1)
Metric: CPU < 30% → scale down (-1)
```

**Политики масштабирования:**
- **Target tracking** (рекомендуется) — удерживай целевую метрику (например, 50% CPU)
- **Step scaling** — дискретные шаги по срабатыванию alarm'ов
- **Scheduled** — известные паттерны (бизнес-часы)
- **Predictive** — ML прогнозирует трафик

**ASG за load balancer'ом (ALB)** — автомасштабирование + распределение трафика.

## Q8. Lambda — кратко (deep dive в отдельном файле)?

**AWS Lambda** — serverless-вычисления. Запускает код в ответ на события:
- HTTP (API Gateway)
- события S3
- DynamoDB streams
- сообщения SQS
- по расписанию (CloudWatch Events)

```python
def handler(event, context):
    return {"statusCode": 200, "body": "Hello"}
```

**Тарификация:** за вызов + за миллисекунды × объём памяти.

Подробнее — в [AWS Lambda](aws-lambda-interview.md).

## Q9. (!) S3 — основные понятия?

**S3 (Simple Storage Service)** — объектное хранилище. De facto стандарт облачного хранилища.

**Понятия:**
- **Bucket** — пространство имён для объектов (имя уникально глобально)
- **Object** — файл (любого размера до 5 TB)
- **Key** — путь/имя внутри bucket
- **Region** — bucket создаётся в конкретном регионе

```
s3://my-bucket/path/to/file.json
   bucket    key
```

**Свойства:**
- **11 девяток надёжности** (durability 99.999999999%)
- **99.99% доступности**
- строгая консистентность (с 2020)
- поддержка версионирования
- шифрование данных при хранении (at rest) и при передаче (in transit)

## Q10. (!) S3 storage classes?

| Класс | Задержка | Стоимость (за GB/мес) | Когда применять |
|-------|---------|---------------------|----------|
| **Standard** | мс | $0.023 | Горячие данные |
| **Intelligent-Tiering** | мс | Авто-тиринг | Неизвестные паттерны доступа |
| **Standard-IA** (Infrequent Access) | мс | $0.0125 | Бэкапы |
| **One Zone-IA** | мс | $0.01 | Восстановимые данные |
| **Glacier Instant Retrieval** | мс | $0.004 | Архив с быстрым доступом |
| **Glacier Flexible** | минуты–часы | $0.0036 | Архив |
| **Glacier Deep Archive** | 12 ч | $0.00099 | Compliance |

**Lifecycle policies:** автоматический переход между классами (например, через 30 дней → IA, через 90 → Glacier).

**Разница в цене:** 23-кратная между Standard и Deep Archive. Оптимизация очень важна.

## Q11. EBS vs EFS vs S3?

| Хранилище | Тип | Монтирование | Когда применять |
|---------|-----|-------|----------|
| **EBS** | Блочное хранилище | Один EC2 (но есть multi-attach) | Базы данных, корень ОС |
| **EFS** | NFS-файловая система | Множество EC2 | Общая файловая система |
| **S3** | Объектное хранилище | Через API, не монтируется | Файлы, бэкапы, веб-ассеты |
| **FSx** | Специализированная ФС | Несколько вариантов | Lustre (HPC), Windows, ZFS |

**По размеру:** лимит EBS — 64 TiB на том; EFS и S3 — петабайтный масштаб.

## Q12. S3 versioning, lifecycle policies?

**Versioning** (версионирование) — каждое изменение создаёт новую версию.

```
my-file.json (v1)
my-file.json (v2)
my-file.json (v3, current)
```

**Применение:**
- защита от случайных удалений
- compliance (история для аудита)
- восстановление после ransomware

**Lifecycle policies:**
```json
{
  "Rules": [{
    "Id": "ArchiveOldData",
    "Filter": {"Prefix": "logs/"},
    "Status": "Enabled",
    "Transitions": [
      {"Days": 30, "StorageClass": "STANDARD_IA"},
      {"Days": 90, "StorageClass": "GLACIER_IR"},
      {"Days": 365, "StorageClass": "DEEP_ARCHIVE"}
    ],
    "Expiration": {"Days": 2555}
  }]
}
```

## Q13. (!) RDS — какие движки, особенности?

**RDS (Relational Database Service)** — управляемые (managed) реляционные БД.

**Движки:**
- PostgreSQL
- MySQL
- MariaDB
- Oracle
- SQL Server
- **Aurora** (совместима с PostgreSQL/MySQL, AWS-native)

**Что берёт на себя managed-сервис:**
- автоматические бэкапы
- восстановление на момент времени (point-in-time recovery)
- Multi-AZ репликация (высокая доступность)
- read-реплики (масштабирование чтения)
- патчинг, обновления

**Что не входит в managed:** проектирование схемы, запросы, индексы — это твоя зона ответственности.

## Q14. (!) Aurora vs RDS PostgreSQL/MySQL?

**Aurora** — проприетарный движок AWS, **совместимый с PostgreSQL/MySQL**.

| Критерий | RDS PostgreSQL | Aurora PostgreSQL |
|----------|----------------|-------------------|
| Производительность | Обычная | **в 3–5 раз быстрее** |
| Хранилище | Подключённый EBS | Распределённое (cluster volume) |
| Реплики | До 5 | До 15 (async, малое отставание) |
| Failover | Вручную или 1–2 мин | Автоматический, < 30 сек |
| Масштабирование хранилища | Вручную | Авто (10 GB → 128 TB) |
| Стоимость | Дешевле | Примерно на 20% дороже |
| Совместимость | Полный PG | 99% PG (часть расширений недоступна) |

**Aurora Serverless v2** — автомасштабирование вплоть до нуля (для нерегулярной, прерывистой нагрузки).

В **2025** Aurora — **выбор по умолчанию** для новых RDS-нагрузок.

## Q15. (!) DynamoDB — что особенного?

**DynamoDB** — управляемая NoSQL: key-value + документное хранилище.

**Особенности:**
- задержка в **единицы миллисекунд** (single-digit ms)
- **auto-scaling** или provisioned-ёмкость
- **Global Tables** — мультирегиональная репликация
- **Streams** — захват изменений (change data capture)
- **DAX** — кеш в памяти (микросекунды)
- опция **строгой консистентности**
- **TTL** — автоудаление просроченных элементов
- без схемы (schemaless), но primary key обязателен

**Тарификация:**
- **On-demand** — за запрос (для непредсказуемой нагрузки)
- **Provisioned** — заранее выделенные RCU/WCU (дешевле при стабильной нагрузке)

**Лучшие практики:**
- тщательно проектируй **partition key** (избегай hot-партиций)
- используй **GSI** (Global Secondary Indexes) для запросов по полям, не входящим в PK
- применяй **single-table design** для связанных данных (вместо множества таблиц)

## Q16. ElastiCache (Redis, Memcached)?

**ElastiCache** — управляемый Redis или Memcached.

**Возможности Redis:**
- персистентность (RDB, AOF)
- репликация, cluster mode
- Pub/Sub, streams
- скриптинг на Lua
- TTL

**Memcached:**
- более простой key-value
- многопоточный
- нет персистентности

**В 2025** — почти всегда **Redis** (больше возможностей). Memcached — для простого кеширования, где нужен максимальный throughput.

**ElastiCache for Redis OSS** против **MemoryDB** (надёжный, durable Redis-совместимый) — под разные сценарии.

## Q17. Other DBs (DocumentDB, Neptune, Timestream)?

**DocumentDB** — совместима с MongoDB (но не идентична ему).
**Neptune** — графовая БД (Gremlin, SPARQL).
**Timestream** — БД для временных рядов (time-series).
**Keyspaces** — совместима с Cassandra.
**OpenSearch** — форк Elasticsearch.
**Redshift** — хранилище данных (data warehouse) — см. [Data Warehousing](../data-engineering/data-warehousing-interview.md).

В **2025** выбор сильно зависит от сценария. Для большинства задач — **Aurora** или **DynamoDB**.

## Q18. (!) VPC, subnets, route tables?

**VPC (Virtual Private Cloud)** — изолированная сеть в регионе AWS.

```
VPC (10.0.0.0/16)
├── Subnet public (10.0.1.0/24) — AZ a
│   └── Internet Gateway
├── Subnet private (10.0.2.0/24) — AZ a
│   └── NAT Gateway → Internet
└── Subnet private DB (10.0.3.0/24) — AZ a
    └── No internet access
```

**Компоненты:**
- **Subnets** (подсети) — диапазоны IP в пределах AZ. Public (с IGW) / private (без) / isolated.
- **Route tables** — куда направлять трафик
- **Internet Gateway** — для public-подсетей
- **NAT Gateway** — исходящий доступ в интернет для private-подсетей
- **Security Groups, NACLs** — правила firewall

**Рекомендация:** подсети в нескольких AZ, разделение public ↔ private.

## Q19. (!) Security Groups vs NACLs?

| Критерий | Security Groups | NACLs |
|-----------|----------------|-------|
| Уровень | Уровень инстанса | Уровень подсети |
| Stateful | **Да** | Нет |
| Правила | Только Allow | Allow + Deny |
| По умолчанию | Запрет всего входящего, разрешён исходящий | Разрешено всё |
| Порядок | Оцениваются все | Пронумерованы, по первому совпадению |

**Security Groups (рекомендуется):**
- «разрешить порт 80 откуда угодно»
- «разрешить порт 5432 только из SG приложения»

**NACLs:** для блокировки конкретных IP/диапазонов (например, чёрный список).

В большинстве случаев — только **Security Groups**. NACLs — для особых случаев.

## Q20. ALB, NLB, CLB?

| LB | Уровень | Когда применять |
|----|-------|----------|
| **ALB (Application LB)** | Layer 7 (HTTP) | Веб-приложения, микросервисы |
| **NLB (Network LB)** | Layer 4 (TCP/UDP) | Высокий throughput, статические IP, игры |
| **GWLB (Gateway LB)** | Layer 3 | Трафик к firewall'ам/системам инспекции |
| **CLB (Classic LB)** | Layer 4/7 | **Legacy** (избегать) |

**Возможности ALB:**
- маршрутизация по пути (`/api/*` → сервис A, `/static/*` → сервис B)
- маршрутизация по хосту
- аутентификация (Cognito, OIDC)
- интеграция с WAF
- терминация SSL

**ALB** — выбор по умолчанию для большинства веб-приложений.

## Q21. CloudFront CDN?

**CloudFront** — глобальный CDN от AWS (более 600 edge locations).

**Сценарии использования:**
- статические ассеты (HTML, CSS, JS, картинки)
- стриминг видео
- ускорение динамического контента (с origin shield)
- кеширование API

```
Client → CloudFront edge (cached) → S3 / ALB / Lambda@Edge / EC2
```

**Возможности:**
- **Lambda@Edge** — выполнение кода на edge (auth, редиректы)
- **Origin failover** — резервирование нескольких origin'ов
- **Signed URLs/cookies** — контент с ограниченным доступом
- **интеграция с WAF** — защита от DDoS и угроз OWASP

**Стоимость:** обычно дешевле, чем отдавать напрямую из S3 (нет платы за исходящий трафик).

## Q22. Route53?

**Route53** — управляемый DNS-сервис.

**Политики маршрутизации:**
- **Simple** — один IP/значение
- **Weighted** — A/B-тестирование (% трафика на разные цели)
- **Latency-based** — ближайший по задержке регион
- **Geolocation** — по стране пользователя
- **Failover** — primary/secondary
- **Multivalue answer** — round-robin
- **Geoproximity** — по расстоянию

**Health checks** — автоматический failover при недоступности.

```
yourdomain.com → ALB (us-east-1)
              → ALB (eu-west-1)
              (latency-based routing)
```

## Q23. (!) NAT Gateway, Internet Gateway?

**Internet Gateway (IGW):**
- подключается к VPC
- двунаправленная связь VPC ↔ интернет
- для **public-подсетей**

**NAT Gateway:**
- располагается в public-подсети
- даёт **исходящий** (outbound) доступ в интернет для private-инстансов
- **не пропускает** входящий трафик (private остаётся приватным)

```
Public subnet:  EC2 ←→ IGW ←→ Internet
Private subnet: EC2 → NAT Gateway → IGW → Internet (outbound only)
```

**Стоимость:** NAT Gateway **дорогой** ($0.045/час + $0.045 за обработанный GB). Частый сюрприз в счёте.

## Q24. (!) IAM — Users, Roles, Policies?

**IAM (Identity and Access Management)** — управление доступом.

**Понятия:**
- **User** — человек или сервис (с access keys)
- **Group** — набор пользователей
- **Role** — роль, которую можно принять (assume) — для сервисов, federated-доступа, cross-account
- **Policy** — JSON-документ с правами (permissions)

```json
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Action": ["s3:GetObject", "s3:PutObject"],
    "Resource": "arn:aws:s3:::my-bucket/*"
  }]
}
```

**Типы политик:**
- **Identity-based** — привязаны к пользователю/роли
- **Resource-based** — привязаны к ресурсу (например, S3 bucket policy)
- **Permission boundaries** — максимально допустимый набор прав

## Q25. (!) IAM роли для EC2/Lambda — best practice?

**Используй IAM-роли, а не access keys в коде.**

```python
# BAD — hardcoded
client = boto3.client("s3", aws_access_key_id="...", aws_secret_access_key="...")

# GOOD — IAM role attached к EC2/Lambda
client = boto3.client("s3")  # automatically uses instance role
```

**EC2 IAM Role** — instance profile, привязанный к EC2. Учётные данные автоматически ротируются через metadata service.

**Lambda execution role** — у каждой Lambda есть своя роль.

**Принцип наименьших привилегий (least privilege):** только нужные права, а не `s3:*`.

## Q26. KMS, Secrets Manager, Parameter Store?

**KMS (Key Management Service)** — управляемые ключи шифрования. Используется при шифровании S3, EBS, RDS.

**Secrets Manager:**
- хранит пароли, API-ключи, учётные данные БД
- автоматическая ротация (RDS, Aurora)
- стоимость: $0.40 за секрет в месяц + API-вызовы

**Systems Manager Parameter Store:**
- бесплатен для standard-параметров
- иерархический (`/myapp/dev/db_url`)
- автоматической ротации нет (только вручную)

**Что когда выбирать:**
- **Secrets Manager** — учётные данные с ротацией
- **Parameter Store** — конфигурация (переменные окружения)
- **KMS** — ключи шифрования (используются через Secrets Manager / Parameter Store)

## Q27. (!) SQS vs SNS vs EventBridge?

| Сервис | Тип | Когда применять |
|---------|-----|----------|
| **SQS** | Очередь (point-to-point) | Очереди воркеров, развязка (decoupling) |
| **SNS** | Pub/Sub-топик | Уведомления, fanout |
| **EventBridge** | Шина событий (event bus) | Сложная маршрутизация, схемы, интеграции |
| **MSK / Kinesis** | Стриминг | Потоки событий, real-time |

**SQS:**
- **Standard** — at-least-once, порядок может нарушаться
- **FIFO** — exactly-once, с сохранением порядка (ограниченный throughput)

**SNS** — push-доставка подписчикам (HTTP, SQS, Lambda, email, SMS).

**EventBridge:**
- более 100 SaaS-интеграций (Stripe, Auth0)
- реестр схем (schema registry)
- правила фильтрации событий
- повтор (replay) событий

**В 2025** — EventBridge **по умолчанию** для event-driven, SQS — для простых очередей.

## Q28. Kinesis vs MSK (Kafka)?

**Kinesis Data Streams** — AWS-native стриминг. Похож на Kafka.

**MSK (Managed Streaming for Kafka)** — управляемый Apache Kafka.

| Критерий | Kinesis | MSK (Kafka) |
|----------|---------|-------------|
| AWS-native | Да | Управляемый Kafka |
| Throughput | Лимиты на shard | Выше (shards = partitions) |
| Retention | До 365 дней | Настраивается (обычно 7 дней) |
| Тарификация | За shard-час + payload | За broker-час |
| Экосистема | Только AWS | Стандартные Kafka-инструменты |

**Когда что:**
- уже есть Kafka-стек / нужны Kafka-инструменты → **MSK**
- тесная интеграция с AWS → **Kinesis**

## Q29. (!) CloudWatch — metrics, logs, alarms?

**CloudWatch** — мониторинг для AWS.

**Компоненты:**
- **Metrics** — числовые данные (CPU, задержка, кастомные)
- **Logs** — логи приложений и системы
- **Alarms** — оповещения при превышении порогов метрик
- **Dashboards** — визуализации
- **Logs Insights** — SQL-подобные запросы к логам
- **Synthetics** — мониторинг доступности (canaries)
- **RUM, X-Ray** — frontend / распределённый трейсинг

```python
# Custom metric
cloudwatch.put_metric_data(
    Namespace='MyApp',
    MetricData=[{
        'MetricName': 'OrdersProcessed',
        'Value': 42,
        'Unit': 'Count'
    }]
)
```

**Стоимость:** приём логов (logs ingestion) может быть **очень дорогим**. Оптимизируй уровни логирования.

## Q30. X-Ray для tracing?

**AWS X-Ray** — сервис распределённого трейсинга.

```python
from aws_xray_sdk.core import xray_recorder

@xray_recorder.capture('process_order')
def process_order(order_id):
    # auto-traced
    ...
```

Визуализирует:
- карту сервисов (service map)
- трейсы запросов (задержка по каждому сервису)
- ошибки

**Альтернативы:** OpenTelemetry + Jaeger/Datadog/Honeycomb обычно лучше (не привязаны к вендору).

## Q31. (!) Well-Architected Framework — 5 pillars?

Фреймворк AWS для проектирования хороших архитектур.

1. **Operational Excellence** (операционное совершенство) — автоматизируй, наблюдай, улучшай
2. **Security** (безопасность) — IAM, шифрование, защита сети
3. **Reliability** (надёжность) — отказоустойчивость, восстановление
4. **Performance Efficiency** (эффективность производительности) — правильный подбор ресурсов, мониторинг
5. **Cost Optimization** (оптимизация затрат) — правильная модель тарификации, устранение потерь
6. **Sustainability** (устойчивость, добавлен в 2021) — влияние на окружающую среду

**Well-Architected Tool** — автоматизированный аудит собственной архитектуры.

## Q32. (!) Multi-AZ vs Multi-Region?

**Multi-AZ:**
- реплики в **разных AZ** одного региона
- защита от отказа AZ
- стандарт для production
- синхронная репликация (RDS Multi-AZ)
- задержка: примерно 1–2 мс

**Multi-Region:**
- реплики в **разных регионах**
- защита от отказа целого региона (редкость!)
- аварийное восстановление (disaster recovery)
- асинхронная репликация
- задержка: 50–300 мс
- **намного дороже и сложнее**

**Крупные компании** делают multi-region. Большинству достаточно **multi-AZ**.

## Q33. Cost optimization tips?

1. **Right-sizing** — не выделяй ресурсы с запасом (over-provision)
2. **Reserved Instances / Savings Plans** для стабильной нагрузки
3. **Spot Instances** для batch-задач
4. **S3 lifecycle policies** — IA, Glacier
5. **останавливай dev/test-инстансы** на ночь (по расписанию)
6. **удаляй неиспользуемое** — EBS-тома, снапшоты, IP-адреса
7. **CloudFront** для статических ассетов (дешевле исходящего трафика S3)
8. **VPC endpoints** для S3/DynamoDB (чтобы избежать затрат на NAT Gateway)
9. **сжимай данные** перед передачей
10. **Cost Explorer + Budgets** — мониторинг

**Инструменты:** Cost Explorer, Trusted Advisor, **Vantage** (сторонний), Spot Advisor.

## Q34. (!) Какие частые ошибки в AWS?

1. **публичные S3-бакеты** — утечки данных (инцидент Capital One)
2. **нет MFA на root-аккаунте**
3. **захардкоженные access keys** в коде/git
4. **открытые security groups** (`0.0.0.0/0` для SSH)
5. **затраты на NAT Gateway** — сюрприз в счёте
6. **исходящий трафик (data transfer out)** — недооценивают стоимость
7. **бэкапы не тестируют** — бэкап не работает, пока его не проверишь
8. **один AZ** — production без резервирования
9. **нет мониторинга/алертов** — инциденты остаются незамеченными
10. **ресурсы не тегируют** — невозможно отнести затраты к проектам

**Профилактика:** AWS Config, GuardDuty, Trusted Advisor, IAM Access Analyzer.

---

## See also

- [AWS Lambda](aws-lambda-interview.md) — serverless deep dive
- [Serverless](serverless-interview.md) — концепции
- [GCP](gcp-interview.md) — конкурент
- [Azure](azure-interview.md) — конкурент
- [Cloud-native Patterns](cloud-native-patterns-interview.md) — best practices
- [Kubernetes](../devops/kubernetes-interview.md) — EKS
- [Микросервисы](../architecture/microservices-interview.md) — где AWS живёт
- [PostgreSQL](../databases/postgresql-interview.md) — RDS, Aurora
- [Redis](../databases/redis-interview.md) — ElastiCache
- [Apache Kafka](../messaging/kafka-interview.md) — MSK
- [Application Security](../security/application-security-interview.md) — IAM, security
- [Observability](../monitoring/observability-interview.md) — CloudWatch, X-Ray
- [Caching](../architecture/caching-strategies-interview.md) — CloudFront, ElastiCache
- [Scalability](../architecture/scalability-patterns-interview.md) — Auto Scaling, multi-region

- [Azure](azure-interview.md)
- [Cloud-native Patterns](cloud-native-patterns-interview.md)
- [GCP (Google Cloud Platform)](gcp-interview.md)
- [Serverless](serverless-interview.md)
- [AI Agents](../ai-ml/ai-agents-interview.md)
