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

`AWS (Amazon Web Services)` — лидер cloud рынка (2025: ~32% доли). Сотни сервисов от compute (EC2) до AI (Bedrock, SageMaker). На интервью бэкендеру важно знать: core services (EC2, S3, RDS, IAM, VPC), managed databases, networking, CDN, мониторинг и **Well-Architected Framework** (5 pillars).

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

`AWS` — облачная платформа от Amazon (с 2006). Лидер рынка ~32% доли (2025).

**Категории:**
- **Compute** — EC2, Lambda, ECS, EKS, Fargate, Batch
- **Storage** — S3, EBS, EFS, FSx, Glacier
- **Databases** — RDS, Aurora, DynamoDB, ElastiCache, Redshift, Neptune
- **Networking** — VPC, Route53, CloudFront, ELB, API Gateway
- **Security** — IAM, KMS, Secrets Manager, WAF, Shield, GuardDuty
- **Analytics** — Athena, EMR, Glue, Kinesis, MSK, QuickSight
- **AI/ML** — SageMaker, Bedrock, Comprehend, Rekognition
- **Developer Tools** — CodePipeline, CodeBuild, CodeDeploy
- **Management** — CloudWatch, CloudFormation, CloudTrail, Config

200+ сервисов. Большинству бэкендеров знакомо ~20.

## Q2. (!) Regions, Availability Zones, Edge Locations?

```
Region (us-east-1, eu-central-1)
  ├── AZ a (data center)
  ├── AZ b (data center)
  └── AZ c (data center)
```

**Region** — географическая зона (Ohio, Frankfurt, Tokyo). 30+ regions globally в 2025.

**Availability Zone (AZ)** — physical data center в region (3+ per region). Изолированные (power, networking) → если один AZ упал, другие работают.

**Edge Location** — точки CDN (CloudFront), 600+ globally. Для low latency пользователям.

**Best practice:** **Multi-AZ** для high availability (RDS Multi-AZ, EC2 across AZs).

## Q3. AWS pricing model?

**Pay-as-you-go** для большинства services:
- **EC2:** per-hour или per-second (Linux)
- **S3:** per-GB stored + per-request + data transfer out
- **Lambda:** per-invocation + per-GB-second compute
- **RDS:** per-hour instance + storage + IOPS

**Pricing modifiers:**
- **Data transfer:** в region — обычно бесплатно, **между regions $$$**, **outbound к internet $$$**
- **Reserved/Savings Plans** — discounts за commitment
- **Spot** — до 90% discount (interruptible)

**Free Tier:** 12 месяцев на новый аккаунт + always-free услуги (S3 5GB, Lambda 1M req/мес, etc.)

**Critical:** **data transfer out** — самая частая cause неприятных счетов.

## Q4. (!) EC2 — instance types, families?

**Instance families:**

| Family | Назначение | Examples |
|--------|-----------|----------|
| **T** (Burstable) | Веб-серверы, dev | t3.micro, t4g.medium |
| **M** (General Purpose) | Balanced | m5.large, m7g.xlarge |
| **C** (Compute-optimized) | Compute-heavy | c5.2xlarge, c7g |
| **R** (Memory-optimized) | In-memory caches, DBs | r5.4xlarge, r7g |
| **X** (Memory-extreme) | Big in-memory DBs | x1.32xlarge |
| **I** (Storage-optimized) | NVMe SSD | i3en.large |
| **G, P** (GPU) | ML, graphics | g5.xlarge, p4d |
| **Inf, Trn** | AI inference/training | inf2, trn1 |

**Naming:** `c7g.xlarge` = C-family, gen 7, **G**raviton (ARM), xlarge size.

**Graviton (ARM)** — обычно 20% дешевле + лучше perf/$. Default 2025.

## Q5. (!) Reserved Instances, Spot Instances, Savings Plans?

| Тип | Discount | Commitment | Use case |
|-----|----------|------------|----------|
| **On-Demand** | 0% | Нет | Dev, unpredictable |
| **Spot** | up to 90% | Может быть прерван | Batch, fault-tolerant |
| **Reserved Instances (RI)** | up to 72% | 1-3 года | Predictable workloads |
| **Savings Plans** | up to 72% | 1-3 года, $/hr commitment | Гибче чем RI |

**Savings Plans** flexible — applicable к разным instance types в region.

**Spot Instances** для:
- ML training
- Batch processing
- CI/CD jobs
- Stateless web servers (с auto-scaling)

**Не для:** databases, stateful long-running services.

## Q6. AMI, EBS, instance store?

**AMI (Amazon Machine Image)** — template для EC2 instances. Содержит OS, software, configs.

**EBS (Elastic Block Store)** — persistent block storage attached к EC2. Сохраняется при stop/start.

**Instance store** — local NVMe SSD на физическом host. **Теряется** при stop. Очень быстрый, для temp data.

**EBS volume types:**
- **gp3** (default 2025) — General Purpose SSD, balanced
- **io2** — Provisioned IOPS, для DBs
- **st1** — HDD throughput-optimized
- **sc1** — HDD cold

## Q7. (!) Auto Scaling Groups?

**ASG** — автоматическое масштабирование EC2 instances based on metrics.

```
Min: 2, Max: 10, Desired: 4

Metric: CPU > 70% → scale up (+1)
Metric: CPU < 30% → scale down (-1)
```

**Scaling policies:**
- **Target tracking** (recommended) — поддерживай target metric (e.g., 50% CPU)
- **Step scaling** — discrete steps based on alarms
- **Scheduled** — известные patterns (бизнес-часы)
- **Predictive** — ML predicts traffic

**ASG за load balancer (ALB)** — auto-scale + traffic distribution.

## Q8. Lambda — кратко (deep dive в отдельном файле)?

**AWS Lambda** — serverless compute. Запускает код в response на events:
- HTTP (API Gateway)
- S3 events
- DynamoDB streams
- SQS messages
- Scheduled (CloudWatch Events)

```python
def handler(event, context):
    return {"statusCode": 200, "body": "Hello"}
```

**Pricing:** per-invocation + per-ms × memory.

Подробнее — в [AWS Lambda](aws-lambda-interview.md).

## Q9. (!) S3 — основные понятия?

**S3 (Simple Storage Service)** — object storage. Стандарт de facto для cloud storage.

**Concepts:**
- **Bucket** — namespace для objects (globally unique name)
- **Object** — file (любого размера до 5 TB)
- **Key** — path/name внутри bucket
- **Region** — bucket creates в конкретном region

```
s3://my-bucket/path/to/file.json
   bucket    key
```

**Properties:**
- **11 nines durability** (99.999999999%)
- **99.99% availability**
- Strong consistency (с 2020)
- Versioning support
- Encryption at rest и in transit

## Q10. (!) S3 storage classes?

| Class | Latency | Cost (per GB/month) | Use case |
|-------|---------|---------------------|----------|
| **Standard** | ms | $0.023 | Hot data |
| **Intelligent-Tiering** | ms | Auto-tier | Unknown access patterns |
| **Standard-IA** (Infrequent Access) | ms | $0.0125 | Backups |
| **One Zone-IA** | ms | $0.01 | Re-creatable data |
| **Glacier Instant Retrieval** | ms | $0.004 | Archive с быстрым retrieval |
| **Glacier Flexible** | min-hours | $0.0036 | Archive |
| **Glacier Deep Archive** | 12h | $0.00099 | Compliance |

**Lifecycle policies:** auto-transition между classes (e.g., после 30 дней → IA, 90 → Glacier).

**Cost difference:** 23x между Standard и Deep Archive. Optimization очень важна.

## Q11. EBS vs EFS vs S3?

| Storage | Тип | Mount | Use case |
|---------|-----|-------|----------|
| **EBS** | Block storage | Один EC2 (но multi-attach есть) | Databases, OS root |
| **EFS** | NFS file system | Множество EC2 | Shared filesystem |
| **S3** | Object storage | Через API, не mounted | Files, backups, web assets |
| **FSx** | Specialized FS | Multiple types | Lustre (HPC), Windows, ZFS |

**По размеру:** EBS limit 64 TiB per volume; EFS, S3 — petascale.

## Q12. S3 versioning, lifecycle policies?

**Versioning** — каждое изменение → новая версия.

```
my-file.json (v1)
my-file.json (v2)
my-file.json (v3, current)
```

**Применение:**
- Защита от accidental deletes
- Compliance (audit history)
- Recovery from ransomware

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

**RDS (Relational Database Service)** — managed реляционные БД.

**Engines:**
- PostgreSQL
- MySQL
- MariaDB
- Oracle
- SQL Server
- **Aurora** (PostgreSQL/MySQL compatible, AWS-native)

**Managed features:**
- Automated backups
- Point-in-time recovery
- Multi-AZ replication (high availability)
- Read replicas (scale reads)
- Patching, upgrades

**Не managed:** schema design, queries, indexes — твоя ответственность.

## Q14. (!) Aurora vs RDS PostgreSQL/MySQL?

**Aurora** — proprietary AWS engine, **PostgreSQL/MySQL compatible**.

| Критерий | RDS PostgreSQL | Aurora PostgreSQL |
|----------|----------------|-------------------|
| Performance | Standard | **3-5x faster** |
| Storage | EBS attached | Distributed (cluster volume) |
| Replicas | Up to 5 | Up to 15 (async, low lag) |
| Failover | Manual or 1-2 min | Automatic, < 30 sec |
| Scaling storage | Manual | Auto (10GB → 128TB) |
| Cost | Cheaper | ~20% more expensive |
| Compatibility | Full PG | 99% PG (some extensions missing) |

**Aurora Serverless v2** — auto-scaling до zero (для intermittent workloads).

В **2025** — Aurora **default** для новых RDS workloads.

## Q15. (!) DynamoDB — что особенного?

**DynamoDB** — managed NoSQL, key-value + document store.

**Особенности:**
- **Single-digit ms** latency
- **Auto-scaling** или provisioned capacity
- **Global Tables** — multi-region replication
- **Streams** — change data capture
- **DAX** — in-memory cache (microseconds)
- **Strong consistency** option
- **TTL** — auto-delete expired items
- Schemaless (но primary key обязателен)

**Pricing:**
- **On-demand** — per-request (для unpredictable)
- **Provisioned** — pre-allocated RCU/WCU (cheaper для steady)

**Best practices:**
- Design **partition key** carefully (avoid hot partitions)
- Use **GSI** (Global Secondary Indexes) для queries по non-PK
- **Single-table design** для related data (вместо multiple tables)

## Q16. ElastiCache (Redis, Memcached)?

**ElastiCache** — managed Redis или Memcached.

**Redis features:**
- Persistence (RDB, AOF)
- Replication, cluster mode
- Pub/Sub, streams
- Lua scripting
- TTL

**Memcached:**
- Simpler key-value
- Multi-threaded
- Нет persistence

**В 2025** — почти всегда **Redis** (более features). Memcached — для simple caching где нужен max throughput.

**ElastiCache for Redis OSS** vs **MemoryDB** (durable Redis-compatible) — для разных use cases.

**DocumentDB** — MongoDB-compatible (но не такой же).
**Neptune** — graph database (Gremlin, SPARQL).
**Timestream** — time-series.
**Keyspaces** — Cassandra-compatible.
**OpenSearch** — fork Elasticsearch.
**Redshift** — data warehouse (см. [Data Warehousing](../data-engineering/data-warehousing-interview.md)).

В **2025** — выбор сильно зависит от use case. Для большинства — **Aurora** или **DynamoDB**.

**VPC (Virtual Private Cloud)** — isolated network в AWS region.

```
VPC (10.0.0.0/16)
├── Subnet public (10.0.1.0/24) — AZ a
│   └── Internet Gateway
├── Subnet private (10.0.2.0/24) — AZ a
│   └── NAT Gateway → Internet
└── Subnet private DB (10.0.3.0/24) — AZ a
    └── No internet access
```

**Components:**
- **Subnets** — IP ranges в AZ. Public (с IGW) / private (без) / isolated.
- **Route tables** — куда направлять traffic
- **Internet Gateway** — для public subnets
- **NAT Gateway** — outbound internet для private subnets
- **Security Groups, NACLs** — firewall rules

**Best practice:** multi-AZ subnets, public ↔ private separation.

| Critterion | Security Groups | NACLs |
|-----------|----------------|-------|
| Level | Instance level | Subnet level |
| Stateful | **Yes** | No |
| Rules | Allow only | Allow + Deny |
| Default | Deny all inbound, allow outbound | Allow all |
| Order | All evaluated | Numbered, first match |

**Security Groups (recommended):**
- "Allow port 80 from anywhere"
- "Allow port 5432 from app SG only"

**NACLs:** для blocking specific IPs/ranges (например, blacklist).

В большинстве случаев — только **Security Groups**. NACLs для специальных cases.

| LB | Layer | Use case |
|----|-------|----------|
| **ALB (Application LB)** | Layer 7 (HTTP) | Web apps, microservices |
| **NLB (Network LB)** | Layer 4 (TCP/UDP) | High throughput, static IPs, gaming |
| **GWLB (Gateway LB)** | Layer 3 | Traffic to firewalls/inspection |
| **CLB (Classic LB)** | Layer 4/7 | **Legacy** (avoid) |

**ALB features:**
- Path-based routing (`/api/*` → service A, `/static/*` → service B)
- Host-based routing
- Authentication (Cognito, OIDC)
- WAF integration
- SSL termination

**ALB** — default для most web apps.

**CloudFront** — global CDN от AWS (600+ edge locations).

**Use cases:**
- Static assets (HTML, CSS, JS, images)
- Video streaming
- Dynamic content acceleration (с origin shield)
- API caching

```
Client → CloudFront edge (cached) → S3 / ALB / Lambda@Edge / EC2
```

**Features:**
- **Lambda@Edge** — execute code at edge (auth, redirects)
- **Origin failover** — multi-origin redundancy
- **Signed URLs/cookies** — restricted content
- **WAF integration** — DDoS, OWASP protection

**Cost:** обычно дешевле чем serving из S3 directly (нет outbound transfer charges).

**Route53** — managed DNS service.

**Routing policies:**
- **Simple** — one IP/value
- **Weighted** — A/B testing (% traffic к разным targets)
- **Latency-based** — closest region
- **Geolocation** — based on user country
- **Failover** — primary/secondary
- **Multivalue answer** — round-robin
- **Geoproximity** — distance-based

**Health checks** — auto-failover при недоступности.

```
yourdomain.com → ALB (us-east-1)
              → ALB (eu-west-1)
              (latency-based routing)
```

**Internet Gateway (IGW):**
- Attached к VPC
- Двунаправленная связь VPC ↔ internet
- Для **public subnets**

**NAT Gateway:**
- В public subnet
- Позволяет **outbound** internet к private instances
- **Не позволяет** inbound (private остаётся private)

```
Public subnet:  EC2 ←→ IGW ←→ Internet
Private subnet: EC2 → NAT Gateway → IGW → Internet (outbound only)
```

**Cost:** NAT Gateway **дорогой** ($0.045/hour + $0.045/GB processed). Surprise в bill.

**IAM (Identity and Access Management)** — управление доступом.

**Concepts:**
- **User** — человек или service (с access keys)
- **Group** — collection of users
- **Role** — assumable identity (для services, federated, cross-account)
- **Policy** — JSON document с permissions

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

**Policy types:**
- **Identity-based** — attached к user/role
- **Resource-based** — attached к resource (S3 bucket policy)
- **Permission boundaries** — max permissions

**Используй IAM roles, не access keys в коде.**

```python
# BAD — hardcoded
client = boto3.client("s3", aws_access_key_id="...", aws_secret_access_key="...")

# GOOD — IAM role attached к EC2/Lambda
client = boto3.client("s3")  # automatically uses instance role
```

**EC2 IAM Role** — instance profile attached к EC2. Credentials auto-rotated через metadata service.

**Lambda execution role** — каждая Lambda имеет role.

**Principle of least privilege:** только нужные permissions, не `s3:*`.

**KMS (Key Management Service)** — managed encryption keys. Используется S3, EBS, RDS encryption.

**Secrets Manager:**
- Store passwords, API keys, DB credentials
- Auto-rotation (RDS, Aurora)
- Cost: $0.40 per secret per month + API calls

**Systems Manager Parameter Store:**
- Free для standard parameters
- Hierarchical (`/myapp/dev/db_url`)
- Not auto-rotation (manual)

**Когда что:**
- **Secrets Manager** — credentials с rotation
- **Parameter Store** — config (environment variables)
- **KMS** — encryption keys (через Secrets Manager / Parameter Store)

| Service | Тип | Use case |
|---------|-----|----------|
| **SQS** | Queue (point-to-point) | Worker queues, decoupling |
| **SNS** | Pub/Sub topic | Notifications, fanout |
| **EventBridge** | Event bus | Complex routing, schemas, integrations |
| **MSK / Kinesis** | Streaming | Event streams, real-time |

**SQS:**
- **Standard** — at-least-once, may reorder
- **FIFO** — exactly-once, ordered (limited throughput)

**SNS** — push к subscribers (HTTP, SQS, Lambda, email, SMS).

**EventBridge:**
- 100+ SaaS integrations (Stripe, Auth0)
- Schema registry
- Event filtering rules
- Replay events

**В 2025** — EventBridge **default** для event-driven, SQS для simple queues.

**Kinesis Data Streams** — AWS-native streaming. Похож на Kafka.

**MSK (Managed Streaming for Kafka)** — managed Apache Kafka.

| Критерий | Kinesis | MSK (Kafka) |
|----------|---------|-------------|
| AWS-native | Да | Управляемый Kafka |
| Throughput | Per-shard limits | Higher (shards = partitions) |
| Retention | До 365 days | Configurable (typically 7d) |
| Pricing | Per-shard-hour + payload | Per broker-hour |
| Ecosystem | AWS-only | Standard Kafka tools |

**Когда:**
- Уже Kafka стек / нужны Kafka tools → **MSK**
- Tight AWS integration → **Kinesis**

**CloudWatch** — мониторинг для AWS.

**Components:**
- **Metrics** — numerical data (CPU, latency, custom)
- **Logs** — application/system logs
- **Alarms** — alerts on metric thresholds
- **Dashboards** — visualizations
- **Logs Insights** — query logs SQL-like
- **Synthetics** — uptime monitoring (canaries)
- **RUM, X-Ray** — frontend/distributed tracing

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

**Стоимость:** logs ingestion может быть **очень дорогим**. Optimize log levels.

**AWS X-Ray** — distributed tracing service.

```python
from aws_xray_sdk.core import xray_recorder

@xray_recorder.capture('process_order')
def process_order(order_id):
    # auto-traced
    ...
```

Visualizes:
- Service map
- Request traces (latency per service)
- Errors

**Альтернативы:** OpenTelemetry + Jaeger/Datadog/Honeycomb обычно лучше (vendor-neutral).

AWS framework для design good architectures.

1. **Operational Excellence** — automate, observe, improve
2. **Security** — IAM, encryption, network protection
3. **Reliability** — fault tolerance, recovery
4. **Performance Efficiency** — right sizing, monitoring
5. **Cost Optimization** — right pricing model, eliminate waste
6. **Sustainability** (added 2021) — environmental impact

**Well-Architected Tool** — automated review своей architecture.

**Multi-AZ:**
- Replicas в **разных AZ** одного region
- Защита от AZ failures
- Standard для production
- Synchronous replication (RDS Multi-AZ)
- Latency: ~1-2 ms

**Multi-Region:**
- Replicas в **разных regions**
- Защита от entire region failure (rare!)
- Disaster recovery
- Asynchronous replication
- Latency: 50-300 ms
- **Намного дороже и сложнее**

**Big firms** делают multi-region. Большинству хватает **multi-AZ**.

1. **Right-sizing** — не over-provision
2. **Reserved Instances / Savings Plans** для steady workloads
3. **Spot Instances** для batch
4. **S3 lifecycle policies** — IA, Glacier
5. **Stop dev/test instances** ночью (scheduling)
6. **Delete unused** — EBS volumes, snapshots, IPs
7. **CloudFront** для static assets (cheaper than S3 outbound)
8. **VPC endpoints** для S3/DynamoDB (avoid NAT Gateway costs)
9. **Compress data** перед transfer
10. **Cost Explorer + Budgets** — мониторинг

**Tools:** Cost Explorer, Trusted Advisor, **Vantage** (3rd party), Spot Advisor.

1. **Public S3 buckets** — data leaks (Capital One incident)
2. **No MFA на root account**
3. **Hardcoded access keys** в коде/git
4. **Open security groups** (`0.0.0.0/0` для SSH)
5. **NAT Gateway costs** — surprise в bill
6. **Data transfer out** charges underestimated
7. **No backups testing** — backups не работают пока не проверишь
8. **Single AZ** — production без redundancy
9. **No monitoring/alerts** — incidents незамечены
10. **Not tagging resources** — невозможно cost attribute

**Prevention:** AWS Config, GuardDuty, Trusted Advisor, IAM Access Analyzer.

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
