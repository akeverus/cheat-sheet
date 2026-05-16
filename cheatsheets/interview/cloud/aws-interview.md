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


> [!mcq] Что верно характеризует AWS как cloud-платформу?
>
> - [x] **A. AWS — облачная платформа от Amazon (с 2006), ~200+ сервисов, сгруппированных в категории: Compute (EC2, Lambda), Storage (S3, EBS), Databases (RDS, DynamoDB), Networking (VPC, CloudFront), Security (IAM, KMS), Analytics, AI/ML и другие.**
>
>     **Развёрнутое объяснение.** AWS — публичное облако с долей рынка ~32% (2025). Стартовала в 2006 с S3 и EC2. Сервисы делятся на функциональные категории, что облегчает навигацию и проектирование архитектуры. Большинству бэкендеров достаточно знать ~20 core-сервисов из 200+.
>
>     **Пример.** Типичный web-app: ALB (Networking) → EC2 ASG (Compute) → RDS PostgreSQL (Databases) → S3 для статики (Storage) → IAM-роли (Security) → CloudWatch для метрик (Management).
>
>     **Когда применять.** Если нужна managed-инфраструктура с глобальным присутствием (30+ regions), широким набором сервисов и pay-as-you-go биллингом. AWS — выбор по умолчанию для большинства enterprise/SaaS.
>
>     **Подводные камни.** Vendor lock-in (труднее мигрировать), сложный pricing (особенно data transfer out — частая причина «неожиданных» счетов), огромная поверхность IAM требует осторожности.
>
>     **Связанные вопросы.** [[Q2]] regions/AZ/edge, [[Q3]] pricing model, [[Q31]] Well-Architected Framework.
>
> - [ ] B. AWS — это исключительно IaaS-платформа (Infrastructure as a Service), предоставляющая только виртуальные машины и сетевые ресурсы; managed-сервисы (RDS, DynamoDB, Lambda) к AWS не относятся и предоставляются партнёрами.
>
>     **Что на самом деле.** AWS покрывает все три уровня — IaaS (EC2, VPC, EBS), PaaS (Elastic Beanstalk, App Runner, RDS) и SaaS-подобные managed-сервисы (Lambda, DynamoDB, SageMaker). Это полноценная multi-tier платформа от Amazon, а не партнёров.
>
>     **Откуда путаница.** Исторически AWS начинался с IaaS (EC2, S3 в 2006), и в старых учебниках его так и описывают. Сейчас 80%+ выручки даёт portfolio managed-сервисов выше IaaS.
>
>     **Если бы это было правдой.** Тогда у Amazon не было бы Aurora, DynamoDB, Bedrock, Lambda — а это флагманы. И невозможно было бы строить serverless-архитектуры на AWS, что противоречит реальности.
>
> - [ ] C. AWS — это open-source-проект, разрабатываемый сообществом под управлением Apache Foundation; Amazon лишь предоставляет хостинг по модели community edition.
>
>     **Что на самом деле.** AWS — proprietary commercial-сервис, на 100% принадлежащий Amazon.com Inc. Apache Foundation курирует другие проекты (Kafka, Spark, Cassandra), но не облачные платформы.
>
>     **Откуда путаница.** Amazon активно использует open-source (Linux, Kubernetes в EKS, PostgreSQL в RDS) и контрибьютит назад, но сама платформа — closed-source SaaS.
>
>     **Если бы это было правдой.** Можно было бы развернуть AWS у себя в дата-центре по аналогии с OpenStack — но такой возможности нет (есть только Outposts как managed-railout от Amazon).
>
> - [ ] D. Основные категории AWS — это «Бесплатные сервисы» и «Платные сервисы»; разделение по функциональности (Compute, Storage) отсутствует и используется только в маркетинге.
>
>     **Что на самом деле.** Категоризация по функциональности — официальная и используется в консоли AWS, документации и сертификациях. Free Tier — это pricing-модель (12 месяцев + always-free уровни), а не категория сервиса.
>
>     **Откуда путаница.** Новички часто фокусируются на Free Tier при первом знакомстве и воспринимают это как главное деление.
>
>     **Если бы это было правдой.** В AWS Console не было бы группировки «Compute», «Storage», «Databases» в навигации — но она там есть и используется ежедневно.

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


> [!mcq] Как соотносятся Region, Availability Zone и Edge Location в AWS?
>
> - [ ] A. Region и Availability Zone — это синонимы (один data center в одном городе); Edge Location — это резервный AZ, который активируется только при failover основного.
>
>     **Что на самом деле.** Region содержит 3+ изолированных AZ (каждый — отдельный физический data center или кластер дата-центров). Edge Location — это точка CDN (CloudFront), не имеющая отношения к failover между AZ.
>
>     **Откуда путаница.** В маленьких regions кажется, что «всё в одном городе» — но даже там AZ физически разнесены на километры с отдельным питанием и сетью.
>
>     **Если бы это было правдой.** Multi-AZ deployment в RDS не имел бы смысла — но это краеугольная практика для high availability в AWS.
>
> - [x] **B. Region — географическая зона (us-east-1, eu-central-1) с 3+ изолированными Availability Zones (физическими data centers с отдельным питанием/сетью); Edge Location — точка CDN (CloudFront, 600+ globally) для low-latency доставки контента, не связана с compute-инфраструктурой региона.**
>
>     **Развёрнутое объяснение.** Иерархия: Region → AZ → конкретные ресурсы. AZ изолированы по power/network/cooling, но соединены low-latency сетью (<1ms) внутри региона. Edge Locations работают отдельно — кэшируют статику и терминируют HTTPS ближе к пользователю, чем ближайший Region.
>
>     **Пример.** Web-app в eu-central-1 (Frankfurt): EC2/RDS Multi-AZ across eu-central-1a/1b/1c для отказоустойчивости + CloudFront с edge-локациями в Москве/Стамбуле/Варшаве для быстрой доставки статики российским/европейским пользователям.
>
>     **Когда применять.** Multi-AZ — для production баз (RDS, Aurora) и stateful-сервисов; ASG across AZs — для stateless web-tier; CloudFront — для статики, API-кэширования и DDoS-защиты.
>
>     **Подводные камни.** Cross-AZ data transfer стоит денег ($0.01-0.02/GB) — heavy chatty workloads между AZ могут «дорожать». Edge Location не хранит данные постоянно — это кэш с TTL, не replacement для S3 cross-region replication.
>
>     **Связанные вопросы.** [[Q21]] CloudFront CDN, [[Q32]] Multi-AZ vs Multi-Region, [[Q13]] RDS.
>
> - [ ] C. Edge Location — это специальный тип AZ с GPU-инстансами для ML-workload; Regions включают только обычные AZ для CPU-задач.
>
>     **Что на самом деле.** GPU-инстансы (P-, G-семейства EC2) живут в обычных AZ внутри Regions, а не в Edge Locations. Edge Location — это сетевая точка CloudFront/Global Accelerator/Route53 без EC2-плоскости.
>
>     **Откуда путаница.** Появился Lambda@Edge — запуск кода в Edge Locations, что создаёт иллюзию compute «на краях». Но это очень ограниченный runtime (короткий timeout, нет VPC), а не полноценные AZ.
>
>     **Если бы это было правдой.** Можно было бы запросить g5.xlarge в Edge Location — но AWS Console такой опции не предоставляет, instance types доступны только в AZ внутри Region.
>
> - [ ] D. Availability Zone — это виртуальный конструкт без физической изоляции; AWS просто маркирует логические разделы одного data center как «AZ-a», «AZ-b» для совместимости с устаревшими API.
>
>     **Что на самом деле.** AZ — физически изолированные data centers (или кластеры зданий) с отдельным power, cooling, networking. AWS публикует это в whitepapers и SLA — Multi-AZ даёт реальную защиту от outage одного DC.
>
>     **Откуда путаница.** Идентификаторы AZ маппятся per-account (us-east-1a у вас и у соседа — разные физические AZ), что иногда воспринимается как «виртуальность».
>
>     **Если бы это было правдой.** Multi-AZ RDS не давал бы реальной отказоустойчивости (одновременный отказ всех AZ при сбое одного DC) — но история outage-ов AWS показывает обратное (часто падает один AZ, остальные работают).

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


> [!mcq] Как устроен ценообразование AWS и что чаще всего «дороже, чем ожидаешь»?
>
> - [ ] A. AWS использует исключительно flat monthly subscription (как Netflix): фиксированная плата за account, после которой все сервисы доступны без дополнительных счетов.
>
>     **Что на самом деле.** Основная модель AWS — pay-as-you-go: оплата по факту использования (EC2 per-second, S3 per-GB, Lambda per-invocation). Subscription-планов с unlimited-доступом ко всему нет.
>
>     **Откуда путаница.** Есть Support Plans (Developer/Business/Enterprise) с фиксированной ценой, но это плата за поддержку, а не за infrastructure.
>
>     **Если бы это было правдой.** Не было бы кейсов «accidental $50,000 bill» из-за неправильно настроенного S3 или Lambda — но они регулярно случаются у новичков.
>
> - [ ] B. Data transfer внутри одного Region (между AZ) — всегда полностью бесплатен; outbound к internet — также бесплатен в рамках Free Tier неограниченно.
>
>     **Что на самом деле.** Cross-AZ data transfer стоит $0.01-0.02/GB в обе стороны. Outbound к internet — $0.05-0.09/GB после 100GB free per month, и Free Tier здесь именно ограниченный, а не unlimited.
>
>     **Откуда путаница.** В одном AZ data transfer действительно бесплатен; новички экстраполируют это на «весь Region».
>
>     **Если бы это было правдой.** Не было бы оптимизаций cross-AZ traffic (например, использование AZ-aware routing в Kafka/Cassandra для удешевления).
>
> - [x] **C. AWS использует pay-as-you-go (EC2 per-hour/second, S3 per-GB + per-request, Lambda per-invocation + per-GB-second) с modifier-ами: Reserved Instances/Savings Plans (до 72% скидка за commitment), Spot Instances (до 90% за interruptibility), Free Tier (12 месяцев + always-free лимиты). Самая частая «неожиданность» — data transfer out к internet и между Regions.**
>
>     **Развёрнутое объяснение.** Биллинг гранулярный — платишь только за фактическое использование. Discounts достигаются через commitment (RI/SP на 1-3 года) или принятие риска прерывания (Spot — AWS может забрать инстанс с уведомлением за 2 минуты). Data transfer — самая коварная часть pricing, так как считается отдельно от compute/storage.
>
>     **Пример.** Web-app: EC2 t3.medium On-Demand ($30/мес) + RDS db.t3.medium ($55/мес) + S3 100GB ($2.3/мес) + 500GB data transfer out ($45/мес) → suddenly transfer-cost = 35% bill.
>
>     **Когда применять.** Predictable steady workload → Savings Plans (3 года All Upfront ≈ 60% скидка). Fault-tolerant batch/ML → Spot Fleet. Spiky или dev → On-Demand. Прототипирование → Free Tier.
>
>     **Подводные камни.** NAT Gateway тарифицируется за GB обработанного трафика ($0.045/GB) + $0.045/hour — может стоить больше, чем EC2 за ним. CloudWatch Logs ingestion ($0.50/GB) скрытно дорожает при verbose logging. Inter-Region transfer ($0.02/GB) дороже cross-AZ.
>
>     **Связанные вопросы.** [[Q5]] Reserved/Spot/Savings Plans, [[Q33]] Cost optimization tips, [[Q34]] частые ошибки.
>
> - [ ] D. Цена EC2 рассчитывается только по CPU-часам; storage (EBS), networking и операционная система (Windows-лицензия) включены бесплатно во все instance types.
>
>     **Что на самом деле.** EC2 hourly rate включает compute + базовый network, но EBS-тома, public IPv4 (с 2024 платный), data transfer и Windows-лицензия — отдельные line items в bill.
>
>     **Откуда путаница.** Linux on-demand цена выглядит как «всё включено», но на практике 30-50% bill — это EBS + transfer + другие add-ons.
>
>     **Если бы это было правдой.** RDS не имел бы отдельной строки «Storage (GB-month)» и «IOPS» в pricing — но они там есть и часто превышают cost самого instance.

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


> [!mcq] Как читать имя EC2-instance типа `c7g.2xlarge` и зачем нужны разные families?
>
> - [ ] A. `c7g` означает «cloud generation 7», буква в конце — это произвольный маркетинговый суффикс Amazon. Families (T/M/C/R/X/I/G) — это просто разные ценовые тиры одного и того же оборудования; разработчику безопаснее всегда брать самую большую букву (X), потому что она новее.
>
>     **Что на самом деле.** В имени `c7g.2xlarge`: `c` — это **семейство** (Compute-optimized, высокий CPU/RAM ratio), `7` — поколение (новее = быстрее и дешевле), `g` — суффикс архитектуры (`g` = AWS Graviton ARM). Families — это аппаратно-разные профили: C — CPU-bound, R — память, I — NVMe-диск, G — GPU. Брать X для веб-сервера — это в 10+ раз переплата за память, которая не нужна.
>
>     **Откуда путаница.** Новички видят «больше букв = круче» и не различают поколение, family и архитектуру. Маркетинг AWS действительно усложняет: одних только M-family есть m5, m5a, m5n, m6i, m6g, m7g — но каждая буква имеет смысл.
>
>     **Если бы это было правдой.** На X1.32xlarge (4 TB RAM) можно было бы запускать t3.micro-нагрузки за разумные деньги — но 1 час X1.32xlarge стоит ~$13, тогда как t3.micro ~$0.01. То есть в 1300 раз дороже.
>
> - [ ] B. Главное правило выбора — всегда брать burstable T-family (t3, t4g), потому что они «умные» и автоматически разгоняются под любую нагрузку. M, C, R — это устаревшие family, оставленные для обратной совместимости со старыми AWS-аккаунтами.
>
>     **Что на самом деле.** T-family — это **bursty** instances с CPU credits: они работают на ~20% baseline CPU и могут «бёрстить» до 100% за счёт накопленных кредитов. Если кредиты кончились (long sustained CPU), T-instance деградирует до baseline. M/C/R — current-generation production-grade families, рассчитанные на постоянную нагрузку. T-family НЕ подходит для CPU-heavy сервисов.
>
>     **Откуда путаница.** Default-выбор в AWS Console часто t3.micro (потому что Free Tier), и новички экстраполируют: «раз Amazon советует — значит универсальный выбор».
>
>     **Если бы это было правдой.** Базы данных PostgreSQL под нагрузкой на t3.medium через пару часов упёрлись бы в baseline и стали отвечать с latency в секунды — что и происходит у новичков, выбравших T для prod-БД.
>
> - [ ] C. Suffix `g` (как в `c7g`) означает «green/energy-efficient» — это маркетинговая метка для углеродно-нейтральных регионов. На x86-нагрузках Graviton-инстансы работают идентично Intel/AMD, никаких изменений в коде не требуется.
>
>     **Что на самом деле.** `g` — это **Graviton**, ARM64-процессоры собственной разработки AWS. Они на ~20% дешевле и на 10–40% производительнее x86 на той же цене, но это **другая ISA**: бинарники, скомпилированные под x86, на Graviton не запустятся. Java/Python/Go — работают без правок (нужны ARM-сборки runtime), но C/C++/native-зависимости (psycopg2, librdkafka) надо пересобирать.
>
>     **Откуда путаница.** AWS активно продвигает Graviton как «sustainable choice», и в маркетинге фигурируют carbon-savings — отсюда ассоциация с «green».
>
>     **Если бы это было правдой.** Docker-образы amd64 запускались бы на Graviton без проблем — но на практике без `--platform=linux/arm64` или multi-arch image вы получите `exec format error`.
>
> - [x] **D. `c7g.2xlarge` декодируется так: `c` — семейство Compute-optimized (высокий CPU/RAM ratio для CPU-bound нагрузок), `7` — седьмое поколение (newer = faster + cheaper per perf), `g` — Graviton ARM64 (≈20% дешевле x86 при сопоставимой/лучшей производительности), `2xlarge` — размер (8 vCPU, 16 GB RAM). Families нужны, чтобы подобрать профиль ресурсов под нагрузку: C — для CPU, R/X — для RAM, I — для NVMe, G/P — для GPU.**
>
>     **Развёрнутое объяснение.** Каждое семейство оптимизировано под класс задач, и при ошибке выбора вы либо переплачиваете (взяв R вместо C для CPU-bound), либо упираетесь в ресурс (взяв C для in-memory cache). Поколение влияет на price/perf: переход 5→7 обычно даёт +20–40% perf при той же цене. Suffix кодирует процессор: `g` — Graviton (ARM), `a` — AMD EPYC, `i` — Intel, без буквы — Intel (legacy).
>
>     **Пример.** Микросервис на Spring Boot с CPU-bound JSON-парсингом: `c7g.large` (2 vCPU, 4 GB, ~$0.07/hr) вместо `m5.large` (2 vCPU, 8 GB, ~$0.10/hr) — экономия 30% и +20% перф. Redis-кэш с 64 GB working-set: `r7g.2xlarge` (8 vCPU, 64 GB), а не `c7g.16xlarge` (тоже 64 GB, но в 4 раза дороже за ненужные vCPU).
>
>     **Когда применять.** Перед запуском prod-нагрузки в EC2/EKS/ECS: профилируйте CPU vs RAM vs IO, выберите family под bottleneck, возьмите последнее поколение (7/6) и `g`-вариант, если стек ARM-совместим (Java, Go, Python, Node работают из коробки).
>
>     **Подводные камни.** Graviton требует ARM-сборок Docker-образов (multi-arch или явный `--platform`). T-family непригоден для sustained-нагрузки из-за CPU credits. Smaller-than-large (nano/micro/small) на M/C/R не существуют — только на T.
>
>     **Связанные вопросы.** [[Q5]] Reserved/Spot/Savings Plans для оптимизации цены, [[Q7]] Auto Scaling Groups, [[Q3]] AWS pricing model.

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


> [!mcq] Чем отличаются On-Demand, Spot, Reserved Instances и Savings Plans, и какую модель выбирать для production-БД vs batch ML-training?
>
> - [x] **A. On-Demand — pay-per-second без обязательств (0% скидки), для непредсказуемых dev-нагрузок. Spot — биржевые «излишки» AWS со скидкой до 90%, но AWS может прервать инстанс за 2 минуты — годится для fault-tolerant batch (ML training, CI/CD). Reserved Instances — обязательство на 1/3 года в конкретной family/AZ, до 72% скидки, для **predictable production** (БД, ALB-таргеты). Savings Plans — то же, но commitment по $/hr, а не по конкретному типу инстанса: гибче перекомпоновывается между family/regions. Для prod-БД — RI или Savings Plans (стабильность + скидка); для batch ML-training — Spot (прерывания терпимы, выигрыш в цене огромный).**
>
>     **Развёрнутое объяснение.** Модели — это **trade-off между гибкостью и ценой**. Чем больше обязательств вы берёте на себя (длительность + специфичность), тем больше скидка. Spot — особый случай: вы платите за капасити, которую AWS может забрать; в обмен — до 90% off. Savings Plans появились в 2019 как «улучшенный RI»: вместо «привяжись к m5.large в us-east-1a» вы коммитите «$10/hr compute spend» и можете крутить любые EC2/Fargate/Lambda в любом регионе.
>
>     **Пример.** Production PostgreSQL на db.r6g.2xlarge 24/7 — берёте 3-year Compute Savings Plan: cost from ~$880/mo → ~$370/mo (58% off). Eventnight ML training на 200x p3.2xlarge в течение 4 часов — Spot: $3.06/hr × 0.3 = ~$0.92/hr × 200 × 4 = ~$735 вместо $2450 on-demand.
>
>     **Когда применять.** RI/SP — для всего, что running 24/7 (БД, baseline ALB-таргеты, ASG min-capacity). Spot — для прерываемых задач: batch jobs, ML training, CI runners, stateless workers за SQS. On-Demand — для пиков (burst поверх RI baseline), dev/test, новых нагрузок до изучения паттерна.
>
>     **Подводные камни.** RI привязывает к family/AZ — если переедете на новое поколение, скидка не переносится (Savings Plans гибче). Spot interruption rate в среднем 5%, но в популярных типах может быть 20%+ — нужна диверсификация по типам/AZ. Standard RI продаются на Marketplace, Convertible — нет.
>
>     **Связанные вопросы.** [[Q3]] AWS pricing model, [[Q7]] Auto Scaling Groups (mixed instances policy для Spot+On-Demand), [[Q4]] EC2 families.
>
> - [ ] B. Spot Instances — это просто «дешёвый On-Demand»: цена ниже на 30–50% за счёт того, что AWS использует более старое оборудование. Прерываний не бывает, поэтому Spot безопасно использовать для production-БД и любых stateful-нагрузок.
>
>     **Что на самом деле.** Spot — это **биржевой механизм для излишков capacity**, не «старое железо». Аппаратно Spot и On-Demand идентичны. Главное свойство Spot — **interruption**: AWS может забрать инстанс с уведомлением за 2 минуты, если capacity нужна On-Demand-клиентам. Скидка до 90% — именно компенсация этого риска. Использовать Spot для БД — гарантированный data-loss при первом же reclaim.
>
>     **Откуда путаница.** Маркетинг AWS подчёркивает «save up to 90%», и новички упускают разницу между «можно сэкономить» и «можно потерять инстанс посреди транзакции».
>
>     **Если бы это было правдой.** Spot-инстансы стоили бы как RI (по фиксированному дисконту), не было бы spot-price истории и метрик `aws ec2 describe-spot-price-history` — но они есть и видно, как цена скачет.
>
> - [ ] C. Reserved Instances и Savings Plans — это одно и то же, просто Savings Plans — переименованный RI после ребрендинга 2020 года. Разницы в гибкости нет: оба заставляют выбрать конкретный instance type и AZ на 1/3 года.
>
>     **Что на самом деле.** Это **разные продукты**. RI коммитит к конкретному (family, size, region/AZ, OS, tenancy). Standard RI — фиксированный, Convertible RI — можно менять family. Savings Plans коммитят к $/hr **общих compute-расходов** — applicable к EC2 (Compute SP), Fargate, Lambda (Compute SP) или конкретной family (EC2 Instance SP). Compute SP — самый гибкий: можно перекидывать spend между регионами/family/services.
>
>     **Откуда путаница.** Оба дают скидку ~72% за commitment 3 года — и со стороны выглядят похоже. Но Savings Plans именно были созданы как ответ на «жёсткость» RI.
>
>     **Если бы это было правдой.** AWS не выпустил бы Savings Plans в 2019 (за 10 лет после RI), и не было бы отдельной AWS-страницы с сравнением «RI vs SP». А она есть, и сам AWS рекомендует SP как default.
>
> - [ ] D. Для production-БД, работающей 24/7, оптимальная модель — Spot Instances, потому что любая БД должна быть отказоустойчивой (multi-AZ replication, snapshots), и кратковременные прерывания Spot покрываются стандартным DR-планом. Reserved Instances устарели.
>
>     **Что на самом наделе.** Spot interruptions реальны и регулярны (5–20% в месяц на популярных типах). DR-план рассчитан на **редкие** failure-events (1–2 раза в год), а не на еженедельные форс-рестарты primary-инстанса. Каждое Spot-прерывание = failover replica, повышенная latency 1–5 минут, риск split-brain. Для prod-БД — RI или Savings Plans, никаких Spot.
>
>     **Откуда путаница.** В блогах встречаются success-stories «we run Cassandra on Spot» — но это специфичные кейсы peer-to-peer-БД, где потеря узла бесшовна. Для PostgreSQL/MySQL primary это катастрофа.
>
>     **Если бы это было правдой.** AWS RDS поддерживал бы Spot для primary-инстансов — но RDS вообще не предлагает Spot, только On-Demand/RI/SP. Это прямой сигнал от AWS, что Spot не для prod-БД.

## Q6. AMI, EBS, instance store?

**AMI (Amazon Machine Image)** — template для EC2 instances. Содержит OS, software, configs.

**EBS (Elastic Block Store)** — persistent block storage attached к EC2. Сохраняется при stop/start.

**Instance store** — local NVMe SSD на физическом host. **Теряется** при stop. Очень быстрый, для temp data.

**EBS volume types:**
- **gp3** (default 2025) — General Purpose SSD, balanced
- **io2** — Provisioned IOPS, для DBs
- **st1** — HDD throughput-optimized
- **sc1** — HDD cold


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. (!) Auto Scaling Groups? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Lambda — кратко (deep dive в отдельном файле)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. (!) S3 — основные понятия? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. (!) S3 storage classes? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq] Команда хранит 50 TB сырых логов в S3 «на всякий случай»: обращаются к данным несколько раз в год, retrieval допустим за минуты-часы, durability должна оставаться 11 девяток. Какой storage class даст минимальный счёт без потери практичности?
>
> - [ ] S3 Standard, чтобы всегда был миллисекундный доступ
>
>     Это hot-storage класс с ценой ≈ $0.023/GB-month — на 50 TB это ≈ $1 150 в месяц, тогда как кейс «несколько раз в год» использует <1% возможностей класса.
>
>     ❌ Последствие: переплата в десятки раз — Standard для архивных логов полностью обесценивает идею tiered storage.
>
> - [x] S3 Glacier Flexible Retrieval с lifecycle policy на финальное expiration
>
>     Glacier Flexible стоит ≈ $0.0036/GB-month (×6.4 дешевле Standard-IA, ×64 дешевле Standard) и допускает retrieval за минуты-часы — это ровно то, что описано в кейсе.
>
>     Lifecycle policy автоматизирует transition в Glacier и финальное expiration, чтобы логи не жили бесконечно после retention-периода.
>
>     Ключевая идея: storage class выбирается по паре «частота доступа × допустимая latency retrieval». Раз обращаемся раз в квартал и можно подождать минуты — Glacier Flexible выигрывает по TCO с большим отрывом и сохраняет 11 девяток durability.
>
> - [ ] One Zone-IA, потому что он дешевле Standard-IA
>
>     One Zone-IA хранит данные только в одной AZ — durability падает до уровня одной зоны (теряется при её отказе), требование «11 девяток» нарушается.
>
>     ❌ Последствие: при отказе AZ часть логов восстановить нечем, а экономия в $0.0025/GB не оправдывает риск.
>
> - [ ] Glacier Deep Archive без оценки retrieval-времени
>
>     Deep Archive самый дешёвый (≈ $0.00099/GB), но retrieval идёт до 12 часов; «несколько раз в год за минуты» в этот SLA не укладывается.
>
>     ❌ Последствие: первый же запрос на логи превратится в инцидент длиной полсуток + неожиданный счёт за retrieval-запросы.

## Q11. EBS vs EFS vs S3?

| Storage | Тип | Mount | Use case |
|---------|-----|-------|----------|
| **EBS** | Block storage | Один EC2 (но multi-attach есть) | Databases, OS root |
| **EFS** | NFS file system | Множество EC2 | Shared filesystem |
| **S3** | Object storage | Через API, не mounted | Files, backups, web assets |
| **FSx** | Specialized FS | Multiple types | Lustre (HPC), Windows, ZFS |

**По размеру:** EBS limit 64 TiB per volume; EFS, S3 — petascale.


> [!mcq] CI-фермa из 200 EC2-инстансов компилирует общий монорепозиторий: воркерам нужен один и тот же writable workspace одновременно, latency должна быть как у локального диска, файлы — POSIX (chmod, symlink). Что выбрать?
>
> - [ ] Подключить EBS-том с multi-attach ко всем 200 инстансам
>
>     Multi-attach у EBS io2 поддерживает максимум 16 одновременных подключений в одной AZ и требует кластерной FS (GFS2/OCFS2), которой EC2 «из коробки» не имеет.
>
>     ❌ Последствие: 200 нод не подключатся, а без кластерной FS параллельные writes повреждают block-устройство — workspace становится corrupted.
>
> - [ ] Положить workspace в S3 и читать через S3 API
>
>     S3 — object storage без mount-семантики: нет POSIX (chmod/rename/symlink), нет partial-write, latency запроса в десятки мс против субмиллисекундных у NFS.
>
>     ❌ Последствие: компилятор и линкер, ожидающие обычную ФС, либо ломаются на отсутствии rename/lock, либо тормозят в десятки раз; сборка из «5 минут» превращается в часы.
>
> - [x] Смонтировать EFS на все 200 инстансов как общий NFS-том
>
>     EFS — fully managed NFSv4 file system, монтируется одновременно на тысячи EC2 в нескольких AZ, поддерживает полный POSIX (права, locks, symlink) и автоматически масштабирует throughput.
>
>     Это ровно сценарий «shared writable filesystem для большого пула воркеров»: общий cache артефактов, кросс-нодные temp-файлы, общий workspace.
>
>     Ключевая идея: выбор между EBS/EFS/S3 — это выбор по двум осям: «block vs file vs object» и «один EC2 vs много EC2». «Много EC2 + writable + POSIX» однозначно ведёт в EFS (или FSx, если нужна Lustre/Windows-специфика).
>
> - [ ] Создать на одном инстансе EBS-том и раздавать его остальным по SSH/SCP
>
>     Это превращает один EC2 в bottleneck и SPOF: его сеть, диск и CPU становятся пределом для всей фермы; consistency между копиями придётся гарантировать вручную.
>
>     ❌ Последствие: при падении «мастер»-ноды вся CI-ферма стоит, а синхронизация через SCP добавляет минуты к каждому build-у и регулярно расходится.

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


> [!mcq] На bucket с критическими документами включили S3 versioning, но через полгода счёт за storage вырос в 4 раза, а bucket-листинг забит «удалёнными» файлами. Что нужно понимать про versioning + lifecycle, чтобы починить ситуацию правильно?
>
> - [ ] Просто выключить versioning — все старые версии исчезнут и счёт упадёт
>
>     Suspend versioning останавливает создание новых noncurrent версий, но уже накопленные версии и delete markers остаются на диске и продолжают тарифицироваться.
>
>     ❌ Последствие: счёт не падает мгновенно — придётся отдельно чистить старые версии, а после suspend bucket становится «полу-versioned» с противоречивым поведением операций.
>
> - [ ] Удалить файлы через консоль — delete сам сотрёт все версии и снизит cost
>
>     В versioned bucket обычный DELETE кладёт delete marker, а сами objects и их предыдущие версии остаются в storage и продолжают платиться.
>
>     ❌ Последствие: storage не уменьшается, а bucket-листинг ещё и заполняется delete markers, которые тоже занимают место и считаются объектами.
>
> - [ ] Включить MFA Delete и забыть про lifecycle — это решит проблему cost
>
>     MFA Delete — security-механизм против случайного удаления версий, он никак не влияет на стоимость хранения и не транзитит версии в дешёвые классы.
>
>     ❌ Последствие: cost-проблема не решается, а добавляется операционная боль: любое легитимное удаление версии теперь требует MFA-токена root-пользователя.
>
> - [x] Добавить lifecycle с `NoncurrentVersionTransition` в Glacier/IA и `NoncurrentVersionExpiration`, плюс `Expiration.ExpiredObjectDeleteMarker`
>
>     `NoncurrentVersionTransition` переносит старые версии в дешёвые классы (Standard-IA → Glacier → Deep Archive) по возрасту, `NoncurrentVersionExpiration` физически удаляет их после retention-периода.
>
>     `ExpiredObjectDeleteMarker: true` чистит «висящие» delete markers, у которых не осталось noncurrent версий, — именно они захламляют листинг.
>
>     Ключевая идея: в versioned bucket объект — это цепочка версий + опциональный delete marker, и каждая сущность платится. Lifecycle policy — единственный штатный способ управлять cost: транзит noncurrent в архив, удаление старых noncurrent, чистка delete markers, опционально `AbortIncompleteMultipartUpload` для забытых multipart-загрузок. Без lifecycle versioning превращается в утечку storage.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. (!) Aurora vs RDS PostgreSQL/MySQL? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. (!) DynamoDB — что особенного? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. ElastiCache (Redis, Memcached)? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. Other DBs (DocumentDB, Neptune, Timestream)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**DocumentDB** — MongoDB-compatible (но не такой же).
**Neptune** — graph database (Gremlin, SPARQL).
**Timestream** — time-series.
**Keyspaces** — Cassandra-compatible.
**OpenSearch** — fork Elasticsearch.
**Redshift** — data warehouse (см. [Data Warehousing](../data-engineering/data-warehousing-interview.md)).

В **2025** — выбор сильно зависит от use case. Для большинства — **Aurora** или **DynamoDB**.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. (!) VPC, subnets, route tables? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. (!) Security Groups vs NACLs? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. ALB, NLB, CLB? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. CloudFront CDN? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q22. Route53? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q23. (!) NAT Gateway, Internet Gateway? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. (!) IAM — Users, Roles, Policies? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. (!) IAM роли для EC2/Lambda — best practice? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. KMS, Secrets Manager, Parameter Store? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. (!) SQS vs SNS vs EventBridge? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q28. Kinesis vs MSK (Kafka)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q29. (!) CloudWatch — metrics, logs, alarms? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q30. X-Ray для tracing? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q31. (!) Well-Architected Framework — 5 pillars? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

AWS framework для design good architectures.

1. **Operational Excellence** — automate, observe, improve
2. **Security** — IAM, encryption, network protection
3. **Reliability** — fault tolerance, recovery
4. **Performance Efficiency** — right sizing, monitoring
5. **Cost Optimization** — right pricing model, eliminate waste
6. **Sustainability** (added 2021) — environmental impact

**Well-Architected Tool** — automated review своей architecture.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q32. (!) Multi-AZ vs Multi-Region? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q33. Cost optimization tips? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q34. (!) Какие частые ошибки в AWS? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [AWS Lambda](aws-lambda-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Azure](azure-interview.md)
- [Cloud-native Patterns](cloud-native-patterns-interview.md)
- [GCP (Google Cloud Platform)](gcp-interview.md)
- [Serverless](serverless-interview.md)
- [AI Agents](../ai-ml/ai-agents-interview.md)
