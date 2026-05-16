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


> [!mcq] В чём разница между AMI, EBS и instance store, и где будет жить база данных PostgreSQL на EC2?
>
> - [ ] A. AMI и EBS — это одно и то же: и AMI, и EBS — это «диски» в AWS, разница только в названии (AMI для Windows-инстансов, EBS — для Linux). Instance store — это бэкап-механизм, который автоматически реплицирует данные между AZ.
>
>     **Что на самом деле.** AMI — это **шаблон** (snapshot OS + software + configs), используется для запуска новых инстансов; это не работающий диск. EBS — **persistent block storage** (виртуальный диск, attached к инстансу). Instance store — **локальный NVMe SSD** на физическом хосте, **теряется при stop/terminate**, никакой репликации между AZ. Базу данных PostgreSQL — на EBS (gp3 или io2), потому что instance store потеряет данные при первом же maintenance reboot.
>
>     **Откуда путаница.** Новички видят «AMI» в списке storage-сервисов AWS Console и предполагают, что это disk. AMI там потому, что физически хранится в S3 (для backed-by-S3) или как набор EBS-snapshots.
>
>     **Если бы это было правдой.** Нельзя было бы создать AMI из running-инстанса (а на самом деле это базовая операция — «save state as AMI»), и не существовало бы EBS-backed AMI как отдельной категории.
>
> - [x] **B. AMI (Amazon Machine Image) — это шаблон (snapshot OS + предустановленный софт + конфиги) для запуска новых EC2-инстансов; сам по себе не «работает», только клонирует state. EBS (Elastic Block Store) — persistent виртуальный диск, attached к инстансу по сети (over-the-wire); сохраняется при stop/start/terminate (если не Delete-on-Termination), может перемещаться между инстансами в той же AZ, поддерживает snapshots в S3. Instance store — локальный NVMe SSD физически в шасси хоста, очень быстрый (микросекундные latency), но теряется при stop/hibernate/terminate/hardware failure. Базу PostgreSQL — однозначно на EBS (gp3 или io2 для high-IOPS); instance store — только для эфемерных данных (tmp-каталоги, Spark shuffle, локальные кэши, которые можно перестроить).**
>
>     **Развёрнутое объяснение.** Это три **разных абстракции** разного назначения: AMI — образ/шаблон, EBS — сетевой диск, instance store — локальный диск. EBS = «портативный SSD по сети» (durability ~99.999%, snapshots), instance store = «физический диск в сервере» (нулевая durability, максимальная скорость). Для stateful нагрузок (БД, любая persistence) — EBS; для stateless с эфемерным кэшем — instance store экономит деньги и даёт latency-win.
>
>     **Пример.** PostgreSQL prod: `r6g.2xlarge` + 2× `io2` 500 GB (provisioned 10 000 IOPS), Multi-AZ через streaming replication. ElasticSearch с реплицируемыми shards: `i4i.2xlarge` (NVMe instance store) — данные локальные, при потере узла кластер ребалансит из других реплик.
>
>     **Когда применять.** AMI — для immutable infrastructure (golden image → ASG launch template). EBS — для всего stateful: БД, файловое хранилище приложения, persistent volumes. Instance store — для distributed stores с встроенной репликацией (Cassandra, ES, Kafka на NVMe), временных файлов, scratch-space.
>
>     **Подводные камни.** EBS привязан к AZ — нельзя attach к инстансу в другой AZ (нужно snapshot → restore). Default `DeleteOnTermination=true` для root EBS-volume — при terminate инстанса диск удаляется. Instance store на t-instances вообще нет (только M/C/R/I с NVMe-вариантами).
>
>     **Связанные вопросы.** [[Q4]] EC2 families (I-family = instance store), [[Q7]] Auto Scaling (AMI в launch template), [[Q11]] EBS snapshots vs S3 backup.
>
> - [ ] C. EBS — это локальный диск на физическом хосте, а instance store — сетевой блочный сторадж в S3. Поэтому EBS быстрее, но теряется при перезагрузке, а instance store медленнее, но переживает рестарт.
>
>     **Что на самом деле.** Всё **с точностью до наоборот**. EBS = сетевой over-the-wire диск (выживает при reboot/stop/terminate), instance store = локальный NVMe в шасси (теряется при stop). EBS медленнее по latency (микросекунды + сеть = ~1ms), instance store быстрее (~50μs). EBS не хранится в S3 (его snapshots — да, но это другой объект).
>
>     **Откуда путаница.** Названия запутывающие: «Elastic Block Store» звучит как «локальный блочный диск», «instance store» звучит как «связанный с инстансом, значит сетевой». На деле — обратная аналогия.
>
>     **Если бы это было правдой.** Нельзя было бы detach EBS от одного инстанса и attach к другому (для локального диска такое невозможно), но AWS CLI команда `aws ec2 attach-volume` это делает ежедневно.
>
> - [ ] D. AMI — это runtime VM-инстанс с уже загруженным приложением (готовый к запуску контейнер на уровне VM), EBS — лог изменений AMI, instance store — кэш для быстрого доступа к EBS. Все три обязательны для запуска EC2.
>
>     **Что на самом деле.** AMI — статический **template** (не running VM), используется в `RunInstances` API для создания нового инстанса; запущенный AMI = EC2-инстанс. EBS — независимый сервис block storage, не «лог изменений AMI». Instance store — отдельный локальный диск, не кэш для EBS. Можно запустить EC2 вообще без EBS (instance store-backed AMI), и наоборот — EBS живёт без AMI.
>
>     **Откуда путаница.** Концепция «golden AMI» иногда подаётся как «запакованный микросервис», и новички экстраполируют до «AMI это контейнер».
>
>     **Если бы это было правдой.** Не было бы возможности обновлять running EC2 без замены AMI (но `apt upgrade` на running-инстансе работает), и не было бы separate billing для EBS (но в счёте AWS EBS — отдельная строка от EC2-hours).

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


> [!mcq] Как работает `Auto Scaling Group` с target tracking policy и зачем нужны параметры `min`/`max`/`desired`?
>
> - [ ] A) ASG поднимает фиксированное число инстансов из `desired`, а `min`/`max` нужны только для красоты в консоли AWS
>     - **Что на самом деле:** `desired` — это лишь текущая «уставка», которую ASG активно меняет в ответ на scaling policies. `min` — жёсткий пол (группа не опустится ниже даже при нулевой нагрузке), `max` — потолок (защита от cost-blowup и runaway scaling при DDoS/баге). Эти границы — контракт с инфраструктурой, а не косметика.
>     - **Откуда путаница:** в простых сценариях `min=desired=max` — там действительно ничего не меняется, и кажется, что границы декоративны.
>     - **Если бы это было правдой:** не было бы смысла отделять `PutScalingPolicy` от `UpdateAutoScalingGroup` в API — но они разделены именно потому, что границы статичные, а `desired` динамический.
> - [ ] B) Target tracking — это cron-расписание, которое каждый час пересчитывает число инстансов по времени суток
>     - **Что на самом деле:** target tracking — реактивная политика по `CloudWatch`-метрике (CPU, `RequestCountPerTarget`, custom). ASG сам поддерживает заданное значение (например, средний CPU 50%), добавляя/убирая инстансы по мере отклонения. Cron-логика — это отдельная политика `Scheduled scaling`.
>     - **Откуда путаница:** target tracking и scheduled scaling часто комбинируют (расписание под прогнозируемые пики + target tracking под непредсказуемые), и их функции склеиваются.
>     - **Если бы это было правдой:** ASG не реагировал бы на внезапный всплеск трафика между «cron-тиками» — но реальный target tracking масштабирует за минуты после превышения порога.
> - [x] **C) ASG автоматически меняет число EC2-инстансов между `min` и `max`; target tracking держит выбранную метрику (например CPU 50%) близко к цели, добавляя или убирая инстансы по `CloudWatch`-алармам**
>     - **Развёрнутое объяснение:** `Auto Scaling Group` — control plane для группы одинаковых EC2 за `Launch Template`. Параметры `min`/`max`/`desired` задают границы и текущее состояние. Scaling policies (target tracking, step, scheduled, predictive) автоматически меняют `desired` по метрикам. Target tracking — самая удобная: указываешь метрику и target value, ASG сам создаёт `CloudWatch alarms` и регулирует группу.
>     - **Пример:** ASG `web-asg` с `min=2, max=10, desired=4` и target tracking по `ASGAverageCPUUtilization=50%`. При CPU 80% ASG добавляет инстансы (до 10), при CPU 20% — убирает (до 2). За `Application Load Balancer` это даёт автоматический horizontal scale-out для stateless web-приложений.
>     - **Когда применять:** stateless workloads с переменной нагрузкой (web, API, воркеры), batch-обработка с предсказуемыми пиками (scheduled), ML-инференс (target tracking по `RequestCountPerTarget`). Для stateful (БД, брокеры) ASG обычно не подходит — там нужен ручной/Operator-driven scaling.
>     - **Подводные камни:** cold start новых инстансов (boot AMI + warmup приложения 2–5 мин) — используйте `warm pools` и `lifecycle hooks`. Target tracking по CPU плохо работает для I/O-bound сервисов — берите `RequestCountPerTarget` или custom metric. `max` без cost-alert опасен: cascade failure может разогнать группу до потолка и сжечь бюджет.
>     - **Связанные вопросы:** [[Q4]] про EC2 instance types (что попадает в `Launch Template`), [[Q5]] про Spot Instances (можно использовать в ASG через mixed instances policy), [[Q8]] про Lambda (альтернатива ASG для serverless workloads).
> - [ ] D) `Auto Scaling Group` — это vertical scaling: AWS меняет размер инстанса (с `t3.small` на `t3.large`) при росте нагрузки
>     - **Что на самом деле:** ASG делает только horizontal scaling — добавляет/убирает одинаковые инстансы по `Launch Template`. Vertical scaling требует stop → resize → start вручную или через скрипт, и в AWS это не автоматизировано на уровне ASG.
>     - **Откуда путаница:** оба механизма — это «scaling», и новички часто не различают horizontal/vertical в облаке.
>     - **Если бы это было правдой:** `Launch Template` не задавал бы фиксированный `InstanceType` — но он обязателен, а смена типа — это новая версия template и rolling replacement, а не runtime-resize.


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


> [!mcq] Что такое AWS Lambda и когда её выбирать вместо EC2/контейнеров?
>
> - [ ] A) Lambda — это виртуальная машина, которая постоянно работает и её нужно явно останавливать, чтобы не платить за idle время
>     - **Что на самом деле:** Lambda — serverless event-driven runtime. Инстанс контейнера поднимается на время обработки события и засыпает после `idle timeout` (обычно 5–15 минут). Платите только за `invocations + GB-seconds` фактического выполнения, а не за idle. Никаких start/stop API у функции нет — управляется автоматически.
>     - **Откуда путаница:** новички переносят EC2-модель «инстанс работает, пока не остановлю» на Lambda и боятся забыть «выключить» функцию.
>     - **Если бы это было правдой:** счёт за функцию, обрабатывающую 100 событий в сутки по 200 мс, был бы как за `t3.micro` 24/7 (~$8/мес) — а реальный счёт для такой нагрузки на Lambda ~$0.00 (попадает во Free Tier).
> - [ ] B) Lambda подходит только для синхронных HTTP-запросов через API Gateway; никакие другие источники событий она не поддерживает
>     - **Что на самом деле:** Lambda интегрирована с десятками источников: `S3` (object events), `DynamoDB Streams` (CDC), `SQS`/`SNS` (очереди), `Kinesis` (стримы), `EventBridge` (cron + custom events), `Cognito` (auth triggers), `Step Functions` (оркестрация), `IoT`. API Gateway — лишь один из триггеров.
>     - **Откуда путаница:** первые туториалы по Lambda — обычно «hello world REST API через API Gateway», и эта связка запоминается как единственный сценарий.
>     - **Если бы это было правдой:** не существовало бы паттернов S3-event → image resize, DynamoDB Stream → search index, SQS → batch processing — а они базовые для serverless-архитектур.
> - [ ] C) Lambda держит state между вызовами в локальной файловой системе `/tmp`, и можно полагаться на этот state как на надёжное хранилище
>     - **Что на самом деле:** `/tmp` (512 MB по умолчанию, до 10 GB настройкой) — эфемерный, очищается при cold start и не разделяется между инстансами. Для persistence используют `S3`, `DynamoDB`, `EFS` (можно подмонтировать к Lambda), `RDS`. Lambda — stateless по дизайну.
>     - **Откуда путаница:** между warm invocations контейнер действительно переиспользуется, и временные файлы в `/tmp` живут — это иногда используют как локальный кэш. Но как primary storage — нельзя.
>     - **Если бы это было правдой:** не понадобились бы интеграции Lambda с `EFS`/`S3`/`DynamoDB` — но они стандарт для любой нетривиальной функции.
> - [x] **D) AWS Lambda — serverless compute, запускающий код в ответ на события (HTTP, S3, DynamoDB Stream, SQS, schedule); pricing «per invocation + per ms × memory», масштабируется до нуля и до тысяч параллельных инстансов автоматически**
>     - **Развёрнутое объяснение:** Lambda — это event-driven function-as-a-service. Вы загружаете код (Python, Node, Java, Go, Rust, custom runtime) и handler — AWS сам управляет инфраструктурой. На каждое событие поднимается изолированный экземпляр функции (Firecracker microVM), он обрабатывает событие и засыпает. Параллелизм — до тысяч одновременных вызовов без преднастройки. Платите только за фактическое выполнение: число запусков × длительность × выделенная память.
>     - **Пример:** `def handler(event, context): return {"statusCode": 200, "body": "Hello"}` — функция за API Gateway отдаёт HTTP-ответ. Триггер из S3: при загрузке `s3://uploads/photo.jpg` Lambda автоматически вызывается, генерирует thumbnail и пишет в `s3://thumbnails/`. Cost: 1M invocations × 200 ms × 128 MB ≈ $0.20.
>     - **Когда применять:** event-driven workloads (S3 events, DynamoDB Streams, SQS), low-traffic API с непредсказуемой нагрузкой, scheduled jobs (вместо cron+EC2), glue-логика между сервисами, batch-обработка коротких задач. Сильно дешевле EC2 при низком/неравномерном RPS.
>     - **Подводные камни:** **cold start** (100 мс–1 c для Python/Node, до 5–10 c для Java/.NET) — критично для low-latency API; используйте `Provisioned Concurrency` или GraalVM native-image. **15-минутный timeout** на одно выполнение — для длинных задач нужен Step Functions или Fargate. **VPC ENI** добавляет overhead при первом запросе. Cost при высоком устойчивом RPS становится выше EC2 — после ~80% utilization 24/7 экономичнее контейнеры.
>     - **Связанные вопросы:** [[Q7]] про Auto Scaling Groups (альтернатива на EC2 для устойчивых нагрузок), [[Q9]] про S3 (типичный источник событий для Lambda), отдельный файл [AWS Lambda](aws-lambda-interview.md) для deep dive.


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


> [!mcq] Как устроена адресация объектов в S3 и почему имя bucket должно быть уникальным глобально, а не в пределах аккаунта?
>
> - [x] **A) В S3 объект адресуется парой `bucket + key`; bucket — это namespace в пределах региона, но его имя должно быть уникальным глобально, потому что bucket доступен по virtual-hosted URL `https://<bucket>.s3.<region>.amazonaws.com/<key>` и AWS DNS должен резолвить его однозначно**
>     - **Развёрнутое объяснение:** S3 — это object storage, где каждый объект идентифицируется тройкой `(region, bucket, key)`. Bucket создаётся в конкретном регионе, но его имя (`my-bucket`) — это глобальный DNS-неймспейс, потому что AWS публикует bucket как поддомен `s3.amazonaws.com`. Поэтому два разных аккаунта в разных регионах не могут иметь bucket с одинаковым именем — DNS не сможет различить запросы. Key (`path/to/file.json`) — это просто строка внутри bucket; «папки» в S3 — это иллюзия консоли, реально иерархии нет.
>     - **Пример:** `s3://my-company-prod-uploads/users/123/avatar.png` — bucket `my-company-prod-uploads` (имя уникально на весь мир), key `users/123/avatar.png`. URL `https://my-company-prod-uploads.s3.eu-central-1.amazonaws.com/users/123/avatar.png`. Объекты до 5 TB, durability 11 nines (99.999999999%), доступность 99.99%, strong read-after-write consistency (с 2020 — раньше была eventual).
>     - **Когда применять:** хранение статики (картинки, видео, бэкапы), data lake для аналитики (Parquet/CSV для Athena/Redshift Spectrum), артефакты CI/CD, hosting статических сайтов, источник событий для Lambda. Default-выбор для любого «положить blob в облако».
>     - **Подводные камни:** глобальные имена ведут к **enumeration**: если bucket `my-company-backups` существует, атакующий может это узнать через DNS, не имея доступа. Поэтому используйте непредсказуемые префиксы (`acme-prod-uploads-7f3a1b2c`). Старая практика «много мелких файлов в одном префиксе» уже не вызывает hot-partition (с 2018 S3 scales автоматически), но `LIST` на bucket с миллионами объектов всё ещё медленный — используйте `Inventory` или базу метаданных. Public access блокируется по умолчанию — включить случайно сложнее, чем раньше, но всё ещё возможно.
>     - **Связанные вопросы:** [[Q10]] про S3 storage classes (выбор класса под access pattern), [[Q11]] про EBS vs EFS vs S3 (когда что), [[Q12]] про versioning и lifecycle policies, [[Q8]] про Lambda (S3 events как trigger).
> - [ ] B) Объект в S3 адресуется только числовым `object_id`, который AWS назначает автоматически; имя bucket — это просто человекочитаемая метка для UI, никакой роли в API не играет
>     - **Что на самом деле:** ключ (key) задаётся клиентом при `PUT`, это произвольная строка до 1024 байт (`users/123/photo.jpg`, `2026/05/15/log.json`). Никакого числового ID S3 не присваивает. Bucket name — обязательная часть запроса (`PUT /key HTTP/1.1\nHost: bucket.s3.amazonaws.com`), без него API-вызов невозможен.
>     - **Откуда путаница:** в традиционных object stores (Swift, Ceph) объект иногда адресуется внутренним ID — и эту модель ошибочно переносят на S3.
>     - **Если бы это было правдой:** не существовало бы паттерна `s3://bucket/year=2026/month=05/file.parquet` для Hive-партиционирования в data lake — но это стандарт для Athena/Spark.
> - [ ] C) Имена bucket уникальны только в пределах AWS-аккаунта; два разных аккаунта могут спокойно иметь bucket `my-bucket` — конфликта нет, потому что аккаунты изолированы
>     - **Что на самом деле:** имя bucket — глобальный namespace на весь AWS. Если bucket `my-bucket` создан в любом аккаунте мира, в вашем аккаунте создать его уже нельзя — получите `BucketAlreadyExists`. Это следствие DNS-based addressing (`bucket.s3.amazonaws.com` должен резолвиться однозначно).
>     - **Откуда путаница:** для большинства ресурсов AWS (EC2 instance, RDS, IAM role) имена действительно изолированы аккаунтом, и эту модель ошибочно расширяют на S3.
>     - **Если бы это было правдой:** можно было бы взять имя `aws-logs` или `backup` для своего bucket — но обе попытки немедленно вернут `BucketAlreadyExists`, потому что они давно заняты.
> - [ ] D) S3 хранит каждый файл только в одном дата-центре и не реплицирует автоматически; для durability нужно вручную включать репликацию между регионами
>     - **Что на самом деле:** Standard storage class реплицирует объект между минимум 3 Availability Zones (AZ) в регионе — это и даёт durability 11 nines из коробки. Cross-region replication (CRR) — отдельная опциональная фича для DR/compliance, но базовая внутрирегиональная репликация уже включена.
>     - **Откуда путаница:** «One Zone-IA» storage class действительно хранит данные только в одной AZ (за это и дешевле), и это специальный случай экстраполируют на весь S3.
>     - **Если бы это было правдой:** при падении одной AZ (например, отключение питания в `eu-central-1a`) Standard-bucket терял бы доступ к части объектов — но за всю историю S3 такого не случалось именно благодаря multi-AZ replication.


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


> [!mcq] Что RDS делает managed-ом, а что остаётся ответственностью команды?
>
> - [x] **AWS управляет backup, patching, failover, репликами; схема, индексы и запросы — на команде**
>
>     **Развёрнутое объяснение.** RDS снимает с команды операционку: автоматические бэкапы с PITR (хранение до 35 дней), minor/major patching по maintenance window, Multi-AZ standby с автоматическим failover за 60–120 секунд, до 5 read replicas (15 для Aurora). Но *логическая* модель данных — это всё ещё ваша работа: денормализация, индексы, EXPLAIN, vacuum policy, partitioning. AWS не подскажет, что у вас seq scan по 50 млн строк.
>
>     **Пример.** Postgres RDS db.r6g.xlarge, Multi-AZ. Команда не настроила индекс на `orders.user_id` — p99 запроса 4 секунды. Patching и backup AWS делает, но запрос-то всё равно тормозит. Добавили индекс → 12 мс. RDS не виноват.
>
>     **Когда применять.** Стандартные OLTP-нагрузки, где нужна реляционная БД без DBA в штате. Если нужна максимальная производительность и масштабирование — Aurora. Если нужен полный контроль над движком (custom extensions, кастомный postgresql.conf) — EC2 self-managed.
>
>     **Подводные камни.** Multi-AZ ≠ read replica: standby в Multi-AZ недоступен для чтения, это просто горячий резерв. Storage autoscaling включается отдельно. Major version upgrade требует downtime даже на Multi-AZ. Snapshot восстанавливается в *новый* instance — старый endpoint не переедет.
>
>     **Связанные вопросы.** [[Q14]] про Aurora, [[Q15]] про DynamoDB как альтернативу для не-реляционных нагрузок.
>
> - [ ] RDS — это просто EC2 с предустановленной БД, AWS ничего не автоматизирует
>
>     **Что на самом деле.** RDS — полноценный managed-сервис: автоматические снапшоты, PITR, Multi-AZ failover, patching по расписанию. Это его ключевое value над «EC2 + apt install postgresql».
>
>     **Откуда путаница.** Под капотом RDS действительно крутится EC2 (видно в billing как косвенные расходы), но AWS закрывает SSH, отдаёт только endpoint и забирает на себя весь lifecycle.
>
>     **Если бы это было правдой.** Не было бы смысла платить +30% к цене EC2: команды бы продолжали ставить Postgres вручную и сами писать cron на pg_dump.
>
> - [ ] RDS поддерживает только MySQL и PostgreSQL — для Oracle/SQL Server нужен EC2
>
>     **Что на самом деле.** RDS поддерживает 6 движков: MySQL, MariaDB, PostgreSQL, Oracle, SQL Server и Aurora (PG/MySQL-compatible). Oracle и SQL Server идут с BYOL-моделью лицензирования.
>
>     **Откуда путаница.** В community-туториалах обычно фигурируют MySQL/PostgreSQL, потому что они open-source и дешевле. Oracle/SQL Server редко встречаются в стартапах.
>
>     **Если бы это было правдой.** Enterprise-миграции с on-prem Oracle на AWS делались бы только через EC2 self-managed, а сервис RDS for Oracle с его license-included опцией не существовал.
>
> - [ ] В RDS можно делать произвольные DDL и тюнить любой параметр postgresql.conf
>
>     **Что на самом деле.** RDS даёт parameter groups — подмножество настроек движка. Часть параметров (например, `shared_preload_libraries` ограничен whitelist-ом, нет доступа к файловой системе, нет superuser-роли в чистом виде) недоступна. DDL внутри БД — да, можно, но операционные ручки урезаны.
>
>     **Откуда путаница.** В обычной БД на VM команда привыкла менять `postgresql.conf` руками и делать `ALTER SYSTEM`. На RDS это работает через parameter group + reboot.
>
>     **Если бы это было правдой.** Не было бы AWS-фичи RDS Custom (отдельный продукт именно для случаев, когда нужен root-доступ к ОС и движку).

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


> [!mcq] Чем Aurora принципиально отличается от RDS PostgreSQL/MySQL?
>
> - [ ] Aurora — это просто RDS с улучшенным железом (NVMe, больше CPU), движок и архитектура хранения те же
>
>     **Что на самом деле.** Aurora — отдельный движок, совместимый с PostgreSQL/MySQL по протоколу и диалекту, но с переписанным storage-layer: distributed cluster volume, который реплицирует данные 6-way по 3 AZ и масштабируется автоматически от 10 GB до 128 TB.
>
>     **Откуда путаница.** В консоли AWS Aurora живёт «внутри» RDS как тип движка — кажется, что это просто пресет.
>
>     **Если бы это было правдой.** Failover Aurora не укладывался бы в <30 секунд, replica lag не был бы суб-100 мс, и не существовало бы Aurora Serverless v2 как принципиально другой billing-модели.
>
> - [x] **У Aurora переписан storage: log-structured 6-way репликация по 3 AZ, автоскейл диска и до 15 low-lag read replicas**
>
>     **Развёрнутое объяснение.** Aurora не пишет страницы данных как обычный Postgres/MySQL — она отправляет redo-log записи в распределённое хранилище, которое само применяет их к 6 копиям. Это даёт: failover <30 секунд (новый writer берёт уже-горячий cluster volume), до 15 reader replicas с лагом в единицы миллисекунд (общее хранилище — реплика не догоняет, а читает то же самое), автоскейл диска без downtime, точка восстановления — любая секунда в окне backup retention.
>
>     **Пример.** Высоконагруженный API с 50k RPS read и 5k RPS write. На RDS PostgreSQL Multi-AZ — failover 90 сек, до 5 реплик с лагом 200–500 мс. Мигрировали на Aurora PG — failover 12 сек, 8 реплик с лагом 8 мс, p99 чтения упал с 40 мс до 14 мс. Стоимость +22%, но bottleneck исчез.
>
>     **Когда применять.** OLTP с высоким read:write ratio и требованием низкого RPO/RTO. Multi-region active-passive через Aurora Global Database. Burst-нагрузки с непредсказуемым паттерном — Aurora Serverless v2 (auto-scale ACU).
>
>     **Подводные камни.** Не все Postgres extensions поддерживаются (нет TimescaleDB, ограничен `pg_cron`). Cross-region replication через Global Database — отдельный продукт, не бесплатный. I/O-billing в стандартной Aurora съедает бюджет на write-heavy нагрузках — нужна Aurora I/O-Optimized. Major version upgrade всё равно требует blue/green deployment для нулевого downtime.
>
>     **Связанные вопросы.** [[Q13]] про базовый RDS, [[Q15]] про DynamoDB как NoSQL-альтернативу.
>
> - [ ] Aurora работает только с MySQL, для PostgreSQL нужен обычный RDS
>
>     **Что на самом деле.** Aurora изначально (2014) запускалась как MySQL-compatible, но Aurora PostgreSQL вышла в 2017 и сегодня является default-рекомендацией AWS для новых нагрузок.
>
>     **Откуда путаница.** Старые статьи и презентации re:Invent до 2017 года описывают только MySQL-вариант.
>
>     **Если бы это было правдой.** Команды на Postgres не получали бы преимущества Aurora и оставались бы на RDS PG — но реально это самый быстрорастущий сегмент Aurora.
>
> - [ ] Aurora дешевле RDS, потому что AWS оптимизировала storage и снизила цену
>
>     **Что на самом деле.** Aurora стоит примерно на 20% дороже эквивалентного RDS по compute, плюс отдельная плата за storage (~$0.10/GB-month) и I/O (если не I/O-Optimized). Цена — это плата за архитектурные преимущества, а не дисконт.
>
>     **Откуда путаница.** Aurora *может* быть дешевле в TCO на write-heavy нагрузках с I/O-Optimized тарифом или на serverless для intermittent workloads — но базовая цена выше.
>
>     **Если бы это было правдой.** AWS не продвигала бы Aurora I/O-Optimized как отдельный pricing-режим для случаев, когда I/O становится главным cost-driver.

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


> [!mcq] Что важнее всего понимать про DynamoDB при проектировании схемы?
>
> - [ ] Можно сначала залить данные, а индексы и partition key добавить потом, как в RDBMS
>
>     **Что на самом деле.** В DynamoDB partition key выбирается ЗАРАНЕЕ и определяет физическое распределение данных по партициям. GSI можно добавить позже (online), но изменить primary key существующей таблицы нельзя — только пересоздать.
>
>     **Откуда путаница.** Опыт с Postgres/MySQL приучает к мышлению «сначала данные, потом индексы». DynamoDB переворачивает это: схема следует за access patterns.
>
>     **Если бы это было правдой.** Не существовало бы практики single-table design и concept of «pre-computing access patterns» — но именно это AWS пропагандирует в каждом DynamoDB-talk на re:Invent.
>
> - [ ] DynamoDB автомасштабируется бесшовно, hot partition — миф из старых версий
>
>     **Что на самом деле.** Hot partition остаётся реальной проблемой в 2025. Лимит ~3000 RCU и 1000 WCU на партицию. Если 80% запросов идёт по одному `userId=admin` — throttling даже на on-demand. Adaptive Capacity помогает, но не лечит плохую схему.
>
>     **Откуда путаница.** AWS улучшила adaptive capacity и instant adaptive scaling, и кажется, что теперь всё «просто работает».
>
>     **Если бы это было правдой.** Не было бы паттернов write-sharding (искусственное добавление суффикса к partition key) и не публиковались бы post-mortems с hot partition в 2024–2025.
>
> - [x] **Схема проектируется ОТ access patterns: partition key обеспечивает равномерное распределение, GSI закрывает остальные запросы**
>
>     **Развёрнутое объяснение.** DynamoDB — это не «NoSQL Postgres», это совсем другая модель. Сначала выписываете все запросы, которые приложение будет делать (по каким полям, с какой селективностью), затем подбираете partition key так, чтобы нагрузка размазывалась по партициям, а самые горячие запросы шли по PK. Для остальных запросов — GSI (отдельный индекс с собственным RCU/WCU). Single-table design объединяет несколько сущностей в одну таблицу с композитным sort key, чтобы делать join-подобные запросы за один Query.
>
>     **Пример.** E-commerce: таблица `Items` с PK=`PK` (например `USER#123`), SK=`SK` (например `ORDER#2026-05-15#001` или `PROFILE`). Один Query по `PK=USER#123, SK begins_with ORDER#` — все заказы пользователя за один запрос, 5–10 мс p99. GSI `GSI1PK=STATUS#PENDING` — все pending-заказы для админки.
>
>     **Когда применять.** Известные access patterns с предсказуемой кардинальностью. Серверлесс-архитектуры (DynamoDB + Lambda — нативная пара). High-scale key-value (session store, feature flags, leaderboards). Multi-region active-active через Global Tables.
>
>     **Подводные камни.** Scan дорогой и медленный — это последнее средство, не основной паттерн. Transactions ограничены 100 items / 4 MB. Streams хранят события только 24 часа — для durability нужен Kinesis/EventBridge. On-demand биллинг легко уходит в космос на bursty workloads без cap — мониторить и алертить по RCU/WCU.
>
>     **Связанные вопросы.** [[Q13]] про реляционные RDS, [[Q14]] про Aurora, когда нужны SQL-возможности.
>
> - [ ] DynamoDB поддерживает полноценный SQL и ACID-транзакции через PartiQL, поэтому миграция с Postgres тривиальна
>
>     **Что на самом деле.** PartiQL — SQL-подобный синтаксис поверх DynamoDB API, но это синтаксический сахар, а не реляционный движок. JOIN-ов нет, агрегаций нет, транзакции ограничены 100 items. Миграция с Postgres почти всегда требует переписывания модели данных, а не просто запросов.
>
>     **Откуда путаница.** AWS маркетит PartiQL как «SQL для DynamoDB», что создаёт ложное впечатление эквивалентности.
>
>     **Если бы это было правдой.** Не было бы целой индустрии консультантов по data modeling для DynamoDB и книги Алекса Дебри «The DynamoDB Book», посвящённой именно тому, что миграция требует переосмысления.

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
