---
title: "Вопросы на собеседовании: Azure"
description: "Microsoft Azure: VMs, AKS, App Service, Functions, Cosmos DB, Storage, Synapse Analytics, Service Bus, Entra ID (Azure AD), отличия от AWS, enterprise integration"
tags:
  - interview
  - cloud
  - azure-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Azure"
  - "Azure interview"
  - "Microsoft Azure interview"
prerequisites:
  - "[[azure-basics]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Azure`

`Microsoft Azure` — №2 cloud provider (~24% market share, 2025). Сильные стороны: **enterprise integration** (Active Directory, Office 365, Windows), **AI** (тесная интеграция с OpenAI), **hybrid cloud** (Azure Arc). На интервью знают: VMs, AKS, App Service, Functions, Cosmos DB, AAD/Entra ID.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Azure Documentation](https://learn.microsoft.com/azure/)
- [Azure Architecture Center](https://learn.microsoft.com/azure/architecture/)
- [Microsoft Cloud Adoption Framework](https://learn.microsoft.com/azure/cloud-adoption-framework/)
- [Azure Pricing Calculator](https://azure.microsoft.com/pricing/calculator/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Azure?](#q1--что-такое-azure)
- [Q2. (!) Subscriptions, Resource Groups?](#q2--subscriptions-resource-groups)
- [Q3. Regions и Availability Zones?](#q3-regions-и-availability-zones)

**Compute**
- [Q4. (!) Azure VMs?](#q4--azure-vms)
- [Q5. (!) AKS (Azure Kubernetes Service)?](#q5--aks-azure-kubernetes-service)
- [Q6. (!) Azure Functions?](#q6--azure-functions)
- [Q7. (!) App Service?](#q7--app-service)
- [Q8. Container Instances, Container Apps?](#q8-container-instances-container-apps)

**Storage**
- [Q9. (!) Azure Storage account types?](#q9--azure-storage-account-types)
- [Q10. Blob Storage tiers?](#q10-blob-storage-tiers)

**Databases**
- [Q11. (!) Azure SQL Database?](#q11--azure-sql-database)
- [Q12. (!) Cosmos DB?](#q12--cosmos-db)
- [Q13. Database for PostgreSQL/MySQL?](#q13-database-для-postgresqlmysql)
- [Q14. Azure Cache for Redis?](#q14-azure-cache-для-redis)

**Networking**
- [Q15. (!) VNet, NSG?](#q15--vnet-nsg)
- [Q16. Application Gateway, Front Door?](#q16-application-gateway-front-door)

**Identity**
- [Q17. (!) Entra ID (бывший Azure AD)?](#q17--entra-id-бывший-azure-ad)
- [Q18. Managed Identities?](#q18-managed-identities)

**Analytics**
- [Q19. (!) Synapse Analytics?](#q19--synapse-analytics)
- [Q20. Azure Data Factory?](#q20-azure-data-factory)
- [Q21. Event Hubs vs Service Bus?](#q21-event-hubs-vs-service-bus)

**AI/ML**
- [Q22. (!) Azure OpenAI Service?](#q22--azure-openai-service)
- [Q23. Azure Machine Learning?](#q23-azure-machine-learning)

**Сравнения**
- [Q24. (!) Azure vs AWS — strengths?](#q24--azure-vs-aws--strengths)
- [Q25. (!) Когда выбирать Azure?](#q25--когда-выбирать-azure)

## Q1. (!) Что такое Azure?

`Microsoft Azure` — облако Microsoft (с 2010). №2 по market share (~24%, 2025).

**Strong areas:**
- **Enterprise integration** — Active Directory (Entra ID), Office 365
- **Hybrid cloud** — Azure Arc, Stack
- **AI** — exclusive partnership с OpenAI (Azure OpenAI Service)
- **.NET ecosystem** — best для C#/Windows
- **Compliance** — широкий каталог сертификаций

**Weaknesses vs AWS:**
- Менее consistent UI/UX (legacy services)
- Documentation сложнее
- Меньше features в open-source
- Reliability — иногда issues (известные outages)


> [!mcq]
> - [ ] Azure лучший выбор для любых workloads дешевле AWS | ❌ ПОСЛЕДСТВИЕ: стоимость зависит от workload; Azure выгодна при Windows/SQL лицензиях, но не универсально дешевле
> - [ ] Azure не поддерживает Linux и open-source workloads | ❌ ПОСЛЕДСТВИЕ: Azure полностью поддерживает Linux, Kubernetes, Python, Java; open-source это не слабость Azure
> - [x] Azure сильнее всего для enterprise с Microsoft-стеком: Active Directory, .NET, Office 365, гибридный сценарий через Azure Arc | ✓ ПРИМЕНЯТЬ: организации с Microsoft лицензиями, Active Directory, hybrid cloud 📋 ПРАВИЛО: Azure = Microsoft ecosystem + enterprise identity + hybrid cloud 🔗 См. Q24
> - [ ] Azure используется только для .NET/Windows приложений | ❌ ПОСЛЕДСТВИЕ: Azure поддерживает все технологические стеки; уникальность — именно интеграция с Microsoft-продуктами, а не ограничение

## Q2. (!) Subscriptions, Resource Groups?

```
Tenant (Entra ID directory)
  ├── Management Group: Production
  │   ├── Subscription: app-prod
  │   │   └── Resource Group: webapp-rg
  │   │       ├── App Service
  │   │       └── SQL Database
  │   └── Subscription: app-prod-data
  └── Management Group: Development
      └── Subscription: app-dev
```

**Subscription** = billing boundary (как AWS account).
**Resource Group** = logical container внутри subscription. Все resources должны быть в RG.
**Management Group** = organize subscriptions (для policies).

**Преимущество:** Resource Groups позволяют **delete всё одновременно**.


> [!mcq]
> - [x] Subscription = billing boundary; Resource Group = logical container ресурсов внутри subscription; удаление RG удаляет все ресурсы в нём | ✓ ПРИМЕНЯТЬ: изолировать prod/dev по subscriptions, группировать ресурсы одного приложения в RG 📋 ПРАВИЛО: RG = корзина ресурсов; delete RG = delete all inside 🔗 См. Q3
> - [ ] Subscription = один region; Resource Group = billing unit | ❌ ПОСЛЕДСТВИЕ: Subscription охватывает все regions; billing — на уровне Subscription в целом, не на уровне RG
> - [ ] Resource Group может содержать только один тип ресурсов | ❌ ПОСЛЕДСТВИЕ: RG — логический контейнер без ограничений на типы; App Service + SQL + Storage в одном RG — норма
> - [ ] Management Group = альтернативное название Resource Group | ❌ ПОСЛЕДСТВИЕ: Management Group организует Subscriptions (применяет Azure Policies); Resource Group — контейнер ресурсов внутри Subscription

## Q3. Regions и Availability Zones?

**Region** — geographical area (East US, West Europe). 60+ regions.

**Availability Zones** — physical data centers в region (3+ per region для AZ-enabled regions).

**Region pairs** — Azure pairs regions (East US ↔ West US) для disaster recovery.

**Не все regions** имеют AZ support (older regions).


> [!mcq]
> - [ ] Availability Zones есть во всех Azure Regions | ❌ ПОСЛЕДСТВИЕ: AZ поддерживают только новые регионы; старые regions (например, некоторые в Азии) не имеют AZ — нужна cross-region репликация
> - [ ] Region pair используется только для ручного disaster recovery | ❌ ПОСЛЕДСТВИЕ: Azure автоматически приоритизирует восстановление paired-регионов; некоторые сервисы (Azure Storage GRS) автоматически реплицируются в pair
> - [x] Region = geographic area; AZ = физически изолированные ДЦ в region (3+); Region Pairs = geo-redundancy для DR | ✓ ПРИМЕНЯТЬ: критические системы — zone-redundant deployment; DR план — использовать region pair 📋 ПРАВИЛО: AZ = защита от ДЦ failure; Region Pair = защита от regional disaster 🔗 См. Q2
> - [ ] Все регионы имеют ровно 3 Availability Zones | ❌ ПОСЛЕДСТВИЕ: количество AZ варьируется; некоторые regions имеют только 1-2 AZ или вообще не поддерживают AZ

## Q4. (!) Azure VMs?

**Azure Virtual Machines** — IaaS, аналог AWS EC2.

**VM series:**
- **B** (Burstable) — variable workloads
- **D** (General purpose) — workhorse
- **F** (Compute optimized) — CPU-intensive
- **E** (Memory optimized) — large databases
- **L** (Storage optimized) — local SSD
- **N** (GPU)
- **H** (HPC)

**Pricing:**
- Pay-as-you-go
- **Reserved Instances** — 1-3 years, 72% off
- **Spot VMs** — up to 90% off
- **Hybrid Benefit** — bring Windows/SQL Server licenses


> [!mcq]
> - [ ] Azure Reserved Instances дают скидку 30% на 1 год | ❌ ПОСЛЕДСТВИЕ: Reserved Instances дают до 72% скидки; 30% — это примерно Spot VMs для определённых сценариев, не RI
> - [ ] Spot VMs гарантируют 99.9% доступность | ❌ ПОСЛЕДСТВИЕ: Spot VMs могут быть выгнаны Azure в любой момент при нехватке capacity; для stateless/batch workloads, не для production
> - [x] Azure Hybrid Benefit позволяет использовать существующие Windows/SQL Server лицензии → экономия до 85%; Spot VMs — до 90% для прерываемых workloads | ✓ ПРИМЕНЯТЬ: Hybrid Benefit при миграции on-premise Windows; Spot для CI/CD, batch jobs 📋 ПРАВИЛО: Hybrid Benefit = bring your license → до 85% off 🔗 См. Q1
> - [ ] VM series выбирается только по CPU — memory и storage не учитываются | ❌ ПОСЛЕДСТВИЕ: серия VM определяет соотношение CPU/memory/storage; E-series — memory-optimized для БД, L-series — storage-optimized для NVMe

## Q5. (!) AKS (Azure Kubernetes Service)?

**AKS** — managed Kubernetes на Azure.

**Особенности:**
- Free control plane (только nodes стоят)
- Integration с Entra ID
- Auto-scaling
- Azure CNI или kubenet networking
- Multi-zone clusters
- Windows containers support

**vs GKE:** GKE считается слегка лучше matured, но AKS догнал. Для Microsoft shops AKS — natural choice.


> [!mcq]
> - [ ] AKS берёт плату за control plane (master nodes) | ❌ ПОСЛЕДСТВИЕ: AKS control plane бесплатен; платишь только за worker nodes (VM), storage, load balancer
> - [ ] AKS не поддерживает Windows containers | ❌ ПОСЛЕДСТВИЕ: AKS поддерживает Windows node pools для Windows containers; одна из отличительных особенностей перед GKE
> - [x] AKS = managed Kubernetes; бесплатный control plane; интеграция с Azure AD, RBAC, ACR, Azure Monitor; автоматическое обновление нод | ✓ ПРИМЕНЯТЬ: microservices на Azure, особенно Microsoft-shop + Windows containers 📋 ПРАВИЛО: AKS = free control plane + Azure-native integrations 🔗 См. Q5
> - [ ] AKS требует самостоятельной установки Kubernetes на VM | ❌ ПОСЛЕДСТВИЕ: AKS — полностью managed; Azure управляет control plane, API server, etcd; пользователь управляет только worker nodes

## Q6. (!) Azure Functions?

**Azure Functions** — FaaS, аналог AWS Lambda.

**Hosting plans:**
- **Consumption** — pay per execution (true serverless)
- **Premium** — pre-warmed instances (no cold start)
- **Dedicated** (App Service Plan) — running на VM

**Triggers:**
- HTTP
- Timer
- Blob Storage
- Queue Storage / Service Bus
- Event Hubs
- Cosmos DB changes

**Languages:** C#, JavaScript, Python, Java, PowerShell, Rust, Go (custom).

```csharp
[FunctionName("HelloHttp")]
public static IActionResult Run(
    [HttpTrigger(AuthorizationLevel.Anonymous, "get")] HttpRequest req)
{
    return new OkObjectResult("Hello");
}
```

**Durable Functions** — для stateful workflows (analog Step Functions).


> [!mcq]
> - [ ] Consumption plan гарантирует отсутствие cold start | ❌ ПОСЛЕДСТВИЕ: Consumption plan имеет cold start (до нескольких секунд); Premium plan с pre-warmed instances устраняет cold start
> - [ ] Durable Functions нужны для stateless HTTP-triggered функций | ❌ ПОСЛЕДСТВИЕ: Durable Functions — для stateful workflows с orchestration (аналог Step Functions); обычный HTTP trigger не требует Durable
> - [x] Consumption plan = true serverless, pay-per-execution; Premium = pre-warmed, no cold start; Durable Functions = stateful workflows | ✓ ПРИМЕНЯТЬ: event-driven processing → Consumption; latency-critical → Premium plan 📋 ПРАВИЛО: Consumption = дёшево + cold start; Premium = дорого + тепло 🔗 См. Q6
> - [ ] Azure Functions работают только с C# и JavaScript | ❌ ПОСЛЕДСТВИЕ: поддерживаются C#, JavaScript, Python, Java, PowerShell, кастомные runtimes (Go, Rust)

## Q7. (!) App Service?

**App Service** — managed PaaS для веб-приложений.

**Особенности:**
- Auto-scaling, load balancing
- CI/CD integration (GitHub Actions, Azure DevOps)
- Custom domains, SSL, auth (через Entra ID)
- Multiple language support (.NET, Node, Java, Python, PHP)
- Slots (для blue-green deployment)

**Pricing tiers:**
- Free / Shared (для testing)
- Basic / Standard / Premium / Isolated

**App Service Plan** = compute resources, можно host multiple apps.

В **2025** — App Service самый популярный для **classic web apps** на Azure.


> [!mcq]
> - [ ] App Service требует Docker-образа для деплоя | ❌ ПОСЛЕДСТВИЕ: App Service поддерживает прямой деплой кода (.NET, Node, Java, Python) без Docker; контейнеры — опциональны
> - [ ] App Service Slots используются только для A/B тестирования | ❌ ПОСЛЕДСТВИЕ: Slots — основной механизм blue-green deployment; swap slots = zero-downtime release, не только A/B testing
> - [x] App Service = PaaS для веб-приложений; встроенные Slots для blue-green; auto-scaling; CI/CD из GitHub/Azure DevOps | ✓ ПРИМЕНЯТЬ: классические web apps без Kubernetes overhead; .NET/Java/Node production 📋 ПРАВИЛО: App Service = managed PaaS + Slots = blue-green without complexity 🔗 См. Q5
> - [ ] App Service не поддерживает custom domains и SSL | ❌ ПОСЛЕДСТВИЕ: App Service полностью поддерживает custom domains, SSL/TLS certificates, managed certificate от Azure

## Q8. Container Instances, Container Apps?

**Container Instances (ACI)** — single container, no orchestration. Quick container, no overhead.

**Container Apps** (с 2022) — managed serverless containers (AKS-light). Built на Kubernetes, KEDA, Dapr.

| Service | Use case |
|---------|----------|
| ACI | Single container, batch tasks |
| Container Apps | Microservices, auto-scaling, managed |
| AKS | Full control, complex setups |

**Container Apps** — equivalent GCP Cloud Run.


> [!mcq]
> - [ ] Container Apps = полный аналог AKS без ограничений | ❌ ПОСЛЕДСТВИЕ: Container Apps managed serverless — нет прямого доступа к Kubernetes API; для сложных scenarios (custom CRDs, direct K8s control) нужен AKS
> - [x] ACI = single container без orchestration (batch jobs); Container Apps = serverless microservices на K8s/KEDA; AKS = полный контроль K8s | ✓ ПРИМЕНЯТЬ: ACI для одноразовых задач; Container Apps для авто-scaling microservices 📋 ПРАВИЛО: ACI=simple, ContainerApps=serverless-k8s, AKS=full-k8s 🔗 См. Q5
> - [ ] Container Apps не поддерживает auto-scaling | ❌ ПОСЛЕДСТВИЕ: Container Apps использует KEDA (Kubernetes Event-Driven Autoscaling) — scale-to-zero включительно на основе событий
> - [ ] ACI поддерживает stateful orchestration нескольких контейнеров | ❌ ПОСЛЕДСТВИЕ: ACI — single container или simple container groups без orchestration; для stateful multi-container workflows нужен AKS или Container Apps

## Q9. (!) Azure Storage account types?

Azure Storage account — единый namespace для:
- **Blob Storage** — objects (как S3)
- **File Storage** — SMB/NFS shares
- **Queue Storage** — simple messaging
- **Table Storage** — NoSQL (legacy, used в Cosmos DB Tables)
- **Disk Storage** — managed disks для VMs

**Account types:**
- **Standard general-purpose v2** — most common
- **Premium Block Blob** — low latency
- **Premium File Shares**
- **Premium Page Blobs** — для VMs


> [!mcq]
> - [ ] Azure Blob Storage — это только для хранения файлов веб-сайтов | ❌ ПОСЛЕДСТВИЕ: Blob Storage — объектное хранилище общего назначения: бэкапы, медиафайлы, данные аналитики, логи; не ограничено веб-контентом
> - [ ] Queue Storage и Service Bus — одно и то же | ❌ ПОСЛЕДСТВИЕ: Queue Storage — простые FIFO очереди без гарантий; Service Bus — enterprise messaging с topic/subscription, DLQ, сессиями
> - [x] Blob (objects) + File (SMB/NFS) + Queue (messaging) + Table (NoSQL) + Disk (VM) — все в одном Storage Account | ✓ ПРИМЕНЯТЬ: Blob для object storage; Premium Block Blob для low-latency writes 📋 ПРАВИЛО: Storage Account = unified namespace для 5 типов хранилища 🔗 См. Q10
> - [ ] Premium Block Blob используется для VMs disks | ❌ ПОСЛЕДСТВИЕ: Premium Page Blobs — для VM disks; Premium Block Blob — для low-latency append/block operations (logs, analytics)

## Q10. Blob Storage tiers?

| Tier | Use case | Cost |
|------|----------|------|
| **Hot** | Frequent access | Highest storage, lowest access |
| **Cool** | Infrequent (≥30 days) | Lower storage, higher access |
| **Cold** (с 2023) | Rare (≥90 days) | Even cheaper |
| **Archive** | Almost never | Cheapest, hours to retrieve |

**Lifecycle management** — auto-transition между tiers.


> [!mcq]
> - [ ] Archive tier обеспечивает мгновенный доступ к данным | ❌ ПОСЛЕДСТВИЕ: Archive — самый дешёвый tier, но rehydration занимает часы; для мгновенного доступа нужен Hot или Cool
> - [x] Hot = частый доступ; Cool (≥30 дней) = редкий; Archive = почти никогда (rehydration hours); Lifecycle Management автоматически перемещает между тиерами | ✓ ПРИМЕНЯТЬ: Archive для compliance backups; Lifecycle policy для автоматической оптимизации затрат 📋 ПРАВИЛО: дешевле tier → дольше rehydration; Hot=быстро+дорого, Archive=медленно+дёшево 🔗 См. Q9
> - [ ] Lifecycle Management нельзя применить к Blob Storage | ❌ ПОСЛЕДСТВИЕ: Lifecycle Management — встроенная функция Blob Storage для автоматического перемещения и удаления объектов по возрасту
> - [ ] Cool tier обеспечивает более низкую цену доступа чем Hot | ❌ ПОСЛЕДСТВИЕ: Cool tier имеет БОЛЕЕ ВЫСОКУЮ цену за доступ (per-request), но НИЖЕ цену за хранение; оптимален только для редко читаемых данных

## Q11. (!) Azure SQL Database?

**Azure SQL Database** — managed Microsoft SQL Server.

**Deployment options:**
- **Single Database** — standalone DB
- **Elastic Pool** — share resources между DBs
- **Managed Instance** — full SQL Server compatibility

**Service tiers:**
- **DTU-based** (legacy) — bundled compute + IO
- **vCore-based** (recommended) — separate compute + storage
  - **General Purpose**, **Business Critical** (in-memory), **Hyperscale** (scale-out)

**Azure SQL** — hands down лучшее place для running SQL Server.


> [!mcq]
> - [ ] Elastic Pool позволяет разным БД использовать разный SQL Server engine | ❌ ПОСЛЕДСТВИЕ: Elastic Pool — механизм совместного использования CPU/memory ресурсов; все БД в пуле — SQL Server одной версии
> - [ ] SQL Managed Instance совместим с Azure SQL Database API 1:1 | ❌ ПОСЛЕДСТВИЕ: Managed Instance = полный SQL Server compatibility; Azure SQL Database — подмножество SQL Server без некоторых agent jobs, linked servers
> - [x] Single DB = standalone; Elastic Pool = shared DTU/vCore между несколькими БД; Managed Instance = full SQL Server compat (для migration on-premise) | ✓ ПРИМЕНЯТЬ: Managed Instance при миграции on-premise SQL Server без изменений приложения 📋 ПРАВИЛО: Managed Instance = lift-and-shift SQL Server; SQL DB = cloud-native SQL 🔗 См. Q12
> - [ ] Azure SQL Database не поддерживает автоматические backups | ❌ ПОСЛЕДСТВИЕ: Azure SQL Database включает автоматические backups (full weekly, differential daily, log every 5-12 min) с PITR до 35 дней

## Q12. (!) Cosmos DB?

**Cosmos DB** — multi-model, globally distributed NoSQL. Crown jewel Azure.

**Особенности:**
- **Multi-model** — Document (Mongo, JSON), Key-Value, Column (Cassandra), Graph (Gremlin)
- **Globally distributed** — multi-region replication
- **5 consistency levels** — Strong, Bounded staleness, Session, Consistent prefix, Eventual
- **Single-digit ms latency** at any scale
- **99.999% SLA** для multi-region writes
- **Serverless** или provisioned RU

**Pricing:** **Request Units (RU/s)** — abstract performance unit.

**Use cases:**
- IoT
- Personalization
- Real-time recommendations
- Global apps
- Mobile backends

**Cost trap:** RU/s можно accidentally overprovision → expensive.


> [!mcq]
> - [ ] Cosmos DB поддерживает только document (JSON) модель данных | ❌ ПОСЛЕДСТВИЕ: Cosmos DB — multi-model: document (Mongo), key-value, column (Cassandra API), graph (Gremlin) — одна БД, несколько API
> - [ ] RU/s в Cosmos DB = количество запросов в секунду | ❌ ПОСЛЕДСТВИЕ: RU/s — абстрактная единица потребления ресурсов (CPU + IO + memory); один запрос может стоить 1 RU или 100 RU в зависимости от размера документа
> - [ ] Cosmos DB поддерживает только Eventual Consistency | ❌ ПОСЛЕДСТВИЕ: Cosmos DB предлагает 5 уровней консистентности: Strong, Bounded Staleness, Session, Consistent Prefix, Eventual — выбор на уровне аккаунта/запроса
> - [x] Cosmos DB = multi-model globally distributed NoSQL с 5 consistency levels, <10ms latency, 99.999% SLA для multi-region writes; цена в RU/s | ✓ ПРИМЕНЯТЬ: глобальные приложения с разными continents; IoT с высоким throughput 📋 ПРАВИЛО: Cosmos DB = planet-scale NoSQL; выбирать API по существующей технологии 🔗 См. Q11

## Q13. Database for PostgreSQL/MySQL?

**Azure Database for PostgreSQL/MySQL** — managed open-source DBs.

**Tiers:**
- Burstable (small)
- General Purpose
- Memory Optimized
- **Hyperscale (Citus)** — для PostgreSQL, distributed (sharded)

С **2024** — **Azure Database for PostgreSQL Flexible Server** — main offering.


> [!mcq]
> - [ ] PostgreSQL Flexible Server не поддерживает high availability | ❌ ПОСЛЕДСТВИЕ: Flexible Server поддерживает zone-redundant HA с standby в другой AZ; автоматический failover за 30-60 секунд
> - [x] Azure Database for PostgreSQL Flexible Server = managed PostgreSQL с zone-redundant HA; Hyperscale (Citus) для distributed sharding; автоматические backups | ✓ ПРИМЕНЯТЬ: open-source PostgreSQL workloads без управления инфраструктурой 📋 ПРАВИЛО: Flexible Server = managed PG; Hyperscale = distributed sharding для large-scale 🔗 См. Q11
> - [ ] Hyperscale (Citus) используется для MySQL, не PostgreSQL | ❌ ПОСЛЕДСТВИЕ: Hyperscale (Citus) — исключительно PostgreSQL extension для distributed queries и horizontal sharding
> - [ ] Azure Database for PostgreSQL не совместим со стандартным PostgreSQL | ❌ ПОСЛЕДСТВИЕ: Azure Database for PostgreSQL — полностью совместимый managed PostgreSQL; существующие приложения без изменений

## Q14. Azure Cache для Redis?

**Azure Cache for Redis** — managed Redis. Аналог AWS ElastiCache, GCP Memorystore.

**Tiers:**
- Basic — single node
- Standard — replication
- Premium — clustering, persistence
- Enterprise — Redis Enterprise features

С **2024** Microsoft anunciated **Azure Managed Redis** — preview новой версии.


> [!mcq]
> - [ ] Azure Cache for Redis Basic tier поддерживает replication и failover | ❌ ПОСЛЕДСТВИЕ: Basic — single node, нет HA; Standard tier добавляет replication; Premium — clustering и persistence
> - [x] Premium tier: clustering + RDB/AOF persistence; Standard: replication без clustering; Basic: dev/test без HA | ✓ ПРИМЕНЯТЬ: production → Standard минимум; high throughput + persistence → Premium 📋 ПРАВИЛО: Basic=dev, Standard=HA, Premium=cluster+persist 🔗 См. Q12
> - [ ] Redis Enterprise на Azure не предоставляет дополнительных возможностей vs Premium | ❌ ПОСЛЕДСТВИЕ: Enterprise tier добавляет Redis модули (RediSearch, RedisJSON, RedisTimeSeries), active-active geo-replication
> - [ ] Azure Cache for Redis не поддерживает Redis Cluster mode | ❌ ПОСЛЕДСТВИЕ: Premium tier поддерживает Redis Cluster с шардированием; до 10 шардов в одном кластере

## Q15. (!) VNet, NSG?

**VNet (Virtual Network)** — аналог AWS VPC.

**NSG (Network Security Group)** — firewall rules (аналог Security Groups + NACLs).

```
VNet (10.0.0.0/16)
  ├── Subnet: web (10.0.1.0/24) + NSG-web
  ├── Subnet: app (10.0.2.0/24) + NSG-app
  └── Subnet: data (10.0.3.0/24) + NSG-data (no internet)
```

**Application Security Groups (ASG)** — group VMs logically для firewall rules.


> [!mcq]
> - [ ] NSG применяется только к отдельным VM, не ко всей подсети | ❌ ПОСЛЕДСТВИЕ: NSG применяется на двух уровнях: subnet (все VM в подсети) или NIC (конкретная VM); subnet-level обычно предпочтительнее
> - [ ] VNet Peering требует VPN gateway и зашифрованного туннеля | ❌ ПОСЛЕДСТВИЕ: VNet Peering использует Microsoft backbone без VPN overhead и шифрования; для encrypted connectivity нужен VPN Gateway или ExpressRoute
> - [x] VNet = изолированная сеть; NSG = stateful firewall на subnet/NIC; ASG = логическая группировка VM для удобства NSG-правил | ✓ ПРИМЕНЯТЬ: тиерная архитектура web/app/data в отдельных суbnets с NSG между ними 📋 ПРАВИЛО: VNet=isolation, NSG=firewall, ASG=logical VM group 🔗 См. Q16
> - [ ] NSG — это stateless firewall, где каждый пакет оценивается независимо | ❌ ПОСЛЕДСТВИЕ: NSG — stateful; если входящее соединение разрешено, ответный трафик автоматически разрешён без явного outbound правила

## Q16. Application Gateway, Front Door?

**Application Gateway** — Layer 7 (HTTP) load balancer внутри region.
- Path-based routing
- WAF integration
- SSL termination

**Azure Front Door** — global L7 load balancer + CDN.
- Global anycast
- Multi-region failover
- WAF
- SSL termination

**Load Balancer** — Layer 4 (TCP/UDP).
**Traffic Manager** — DNS-based load balancing.


> [!mcq]
> - [ ] Application Gateway — глобальный L7 load balancer с CDN | ❌ ПОСЛЕДСТВИЕ: Application Gateway — regional L7; Azure Front Door — глобальный anycast с CDN и WAF для multi-region scenarios
> - [ ] Traffic Manager работает на L7 (HTTP) | ❌ ПОСЛЕДСТВИЕ: Traffic Manager — DNS-based routing (не proxy); решение принимается на уровне DNS, не HTTP; нет SSL termination
> - [x] App Gateway = regional L7 (path-based routing, WAF, SSL); Front Door = global anycast CDN+WAF; Load Balancer = L4; Traffic Manager = DNS routing | ✓ ПРИМЕНЯТЬ: глобальное приложение → Front Door; regional → App Gateway 📋 ПРАВИЛО: Front Door=global L7+CDN, AppGateway=regional L7 🔗 См. Q15
> - [ ] WAF доступен только в Azure Front Door, не в Application Gateway | ❌ ПОСЛЕДСТВИЕ: WAF интегрирован в оба: Application Gateway WAF (regional) и Front Door WAF (global); выбор по scope

## Q17. (!) Entra ID (бывший Azure AD)?

**Microsoft Entra ID** (rebranded из Azure Active Directory в 2023) — identity provider.

**Особенности:**
- **SSO** для thousands SaaS apps
- **Conditional Access** — granular policies
- **MFA** built-in
- **Passwordless** (FIDO2)
- **B2B / B2C** scenarios
- **Identity Governance** (lifecycle, access reviews)

**Crown jewel Microsoft Cloud.** Huge enterprise install base.

**Integration** в Azure resources — RBAC через Entra ID.

```bash
az role assignment create --assignee user@contoso.com \
  --role "Storage Blob Data Reader" --scope /subscriptions/.../my-storage
```


> [!mcq]
> - [ ] Entra ID — это только on-premise Active Directory в облаке | ❌ ПОСЛЕДСТВИЕ: Entra ID — cloud-native IdP с SSO для SaaS apps, Conditional Access, MFA; on-premise AD синхронизируется через Entra Connect
> - [ ] Conditional Access — это просто MFA policy | ❌ ПОСЛЕДСТВИЕ: Conditional Access — granular policies с conditions (user location, device compliance, app risk score) + controls (MFA, block, require compliant device)
> - [x] Entra ID = cloud IdP: SSO для SaaS, Conditional Access, MFA, Passwordless (FIDO2), B2B/B2C; RBAC в Azure ресурсах через Entra ID | ✓ ПРИМЕНЯТЬ: enterprise SSO + Azure RBAC — всегда через Entra ID 📋 ПРАВИЛО: Entra ID = Microsoft cloud identity platform для Azure + M365 + SaaS 🔗 См. Q18
> - [ ] Entra ID не поддерживает external identities (B2B) | ❌ ПОСЛЕДСТВИЕ: Entra ID External ID включает B2B (партнёры с их corporate identity) и B2C (consumer identity с social providers)

## Q18. Managed Identities?

**Managed Identity** — auto-created Entra ID identity для Azure resource (VM, App Service, Function).

```csharp
// No credentials в коде!
var credential = new DefaultAzureCredential();
var blobClient = new BlobServiceClient(uri, credential);
```

**Types:**
- **System-assigned** — tied to resource lifecycle
- **User-assigned** — independent, can be shared

Equivalent **AWS IAM Roles for EC2/Lambda** или **GCP Service Accounts**.


> [!mcq]
> - [ ] System-assigned Managed Identity можно использовать в нескольких VM одновременно | ❌ ПОСЛЕДСТВИЕ: System-assigned привязана к одному ресурсу и удаляется вместе с ним; User-assigned — независима и может использоваться несколькими ресурсами
> - [ ] Managed Identity требует хранить client secret в Key Vault | ❌ ПОСЛЕДСТВИЕ: Managed Identity полностью устраняет необходимость в credentials — Azure автоматически ротирует токены; никаких secrets не нужно
> - [x] System-assigned = tied to resource lifecycle; User-assigned = independent, shareable; оба позволяют коду получать токены без credentials через DefaultAzureCredential | ✓ ПРИМЕНЯТЬ: VM/Function нужен доступ к Storage/KeyVault → Managed Identity вместо connection string 📋 ПРАВИЛО: Managed Identity = no credentials in code; DefaultAzureCredential работает локально и в Azure 🔗 См. Q17
> - [ ] Managed Identity работает только с Azure Storage, не с другими сервисами | ❌ ПОСЛЕДСТВИЕ: Managed Identity поддерживается всеми Azure сервисами: Key Vault, SQL, Service Bus, Cosmos DB, любые Azure AD-защищённые ресурсы

## Q19. (!) Synapse Analytics?

**Azure Synapse Analytics** — unified analytics platform.

**Includes:**
- **Synapse SQL** — DWH (analog Snowflake)
- **Apache Spark pools**
- **Data Explorer** (Kusto)
- **Pipelines** (data integration)

**Synapse SQL:**
- **Dedicated SQL pool** — provisioned DWH
- **Serverless SQL pool** — query data в Azure Storage без loading

В **2025** Synapse — Microsoft answer на BigQuery / Snowflake. Но reliability и UX issues есть. Многие выбирают **Microsoft Fabric** (новая объединённая платформа).


> [!mcq]
> - [ ] Synapse Serverless SQL pool требует загрузки данных в DWH перед запросами | ❌ ПОСЛЕДСТВИЕ: Serverless SQL pool — query-in-place поверх Azure Data Lake без ETL; платишь за TB processed, не за provisioned compute
> - [ ] Synapse Analytics — замена Azure Data Factory для ETL pipelines | ❌ ПОСЛЕДСТВИЕ: Synapse включает интегрированные Pipelines (аналог ADF), но ADF — отдельный зрелый сервис; Microsoft Fabric — новая объединённая платформа
> - [x] Synapse = DWH (Dedicated SQL pool) + Serverless SQL (query-in-place) + Spark + Pipelines в одном workspace | ✓ ПРИМЕНЯТЬ: unified analytics над Azure Data Lake с поддержкой SQL и Spark в одном месте 📋 ПРАВИЛО: Dedicated SQL = provisioned DWH; Serverless = pay-per-query on Data Lake 🔗 См. Q20
> - [ ] Synapse Dedicated SQL pool автоматически масштабируется без паузы | ❌ ПОСЛЕДСТВИЕ: Dedicated SQL pool нужно вручную scale up/down (или автопауза); это отличие от Serverless который scale автоматически

## Q20. Azure Data Factory?

**ADF** — managed ETL/ELT. Аналог AWS Glue, GCP Cloud Data Fusion.

**Особенности:**
- Visual UI для pipelines
- 90+ data connectors
- Code-free transformations (Mapping Data Flows)
- Custom Python/Spark activities
- CI/CD integration

В **2025** — Microsoft pushes **Microsoft Fabric** (new unified platform), но ADF still in use.


> [!mcq]
> - [ ] ADF не поддерживает on-premise источники данных | ❌ ПОСЛЕДСТВИЕ: ADF поддерживает on-premise через Self-hosted Integration Runtime; 90+ connectors включают SQL Server on-prem, Oracle, SAP
> - [ ] Mapping Data Flows требует написания Spark кода | ❌ ПОСЛЕДСТВИЕ: Mapping Data Flows — code-free visual ETL, генерирует Spark код автоматически; кастомный код — через Custom Activity
> - [x] ADF = managed ETL/ELT с 90+ connectors, visual pipeline designer, CI/CD; аналог AWS Glue + orchestration | ✓ ПРИМЕНЯТЬ: data ingestion из разных источников в Data Lake/Synapse без custom Spark 📋 ПРАВИЛО: ADF = no-code ETL для Azure data pipelines 🔗 См. Q19
> - [ ] ADF и Synapse Pipelines — полностью разные продукты без пересечения | ❌ ПОСЛЕДСТВИЕ: Synapse Pipelines — встроенный ADF-like сервис внутри Synapse workspace; ADF — standalone; оба основаны на одном engine

## Q21. Event Hubs vs Service Bus?

**Event Hubs** — high-throughput event streaming. Аналог Kafka.
- Millions events/sec
- Partitions, consumer groups
- Apache Kafka protocol compatible (с Premium tier)

**Service Bus** — enterprise messaging. Аналог RabbitMQ.
- Queues и Topics (pub/sub)
- Transactions, sessions, dead-letter
- AMQP 1.0
- Lower throughput чем Event Hubs

| Service | Use case |
|---------|----------|
| Event Hubs | Streaming, telemetry, IoT |
| Service Bus | Business messaging, transactions |


> [!mcq]
> - [ ] Event Hubs и Service Bus взаимозаменяемы для любых messaging scenarios | ❌ ПОСЛЕДСТВИЕ: Event Hubs = streaming (IoT, telemetry) millions/sec без ordering guarantee; Service Bus = business messaging с transactions, sessions, DLQ
> - [x] Event Hubs = high-throughput streaming (Kafka-compatible); Service Bus = enterprise messaging с queues/topics, transactions, DLQ | ✓ ПРИМЕНЯТЬ: Event Hubs для телеметрии/IoT; Service Bus для бизнес-транзакций с гарантией доставки 📋 ПРАВИЛО: EventHubs=stream, ServiceBus=messaging 🔗 См. Q6
> - [ ] Service Bus поддерживает миллионы событий/сек как Event Hubs | ❌ ПОСЛЕДСТВИЕ: Service Bus оптимизирован для enterprise messaging (тысячи сообщений/сек); Event Hubs — для streaming миллионов событий/сек
> - [ ] Event Hubs не поддерживает Apache Kafka protocol | ❌ ПОСЛЕДСТВИЕ: Event Hubs Premium tier поддерживает Kafka protocol compatibility; существующие Kafka приложения подключаются без изменений кода

## Q22. (!) Azure OpenAI Service?

**Azure OpenAI Service** — exclusive Microsoft offering. Hosting OpenAI models (GPT-4, etc.) на Azure infrastructure.

**Преимущества vs OpenAI directly:**
- **Enterprise compliance** (GDPR, HIPAA, SOC)
- **Private network** access
- **Customer-managed keys** (BYOK)
- **No data used для OpenAI training**
- **SLA, support** через Microsoft

**Use case:** enterprise хочет GPT-4, но не может использовать public OpenAI API.

В **2025** — самый популярный enterprise way использовать OpenAI models.


> [!mcq]
> - [ ] Azure OpenAI Service — то же что публичный OpenAI API с другим endpoint | ❌ ПОСЛЕДСТВИЕ: Azure OpenAI добавляет enterprise compliance (GDPR, HIPAA), private network access, BYOK, данные НЕ используются для обучения моделей OpenAI
> - [ ] Azure OpenAI Service доступен публично без approval process | ❌ ПОСЛЕДСТВИЕ: Azure OpenAI требует application для доступа (enterprise vetting); не открыт как публичный OpenAI API
> - [x] Azure OpenAI = GPT-4/models на Azure инфраструктуре с enterprise compliance: GDPR, private VNet, BYOK, данные не идут на обучение | ✓ ПРИМЕНЯТЬ: enterprise с compliance requirements (HIPAA, GDPR) кому нужен GPT-4 в private сети 📋 ПРАВИЛО: Azure OpenAI = OpenAI models + enterprise security guarantees 🔗 См. Q1
> - [ ] Azure OpenAI Service не поддерживает fine-tuning моделей | ❌ ПОСЛЕДСТВИЕ: Azure OpenAI поддерживает fine-tuning для GPT-3.5-turbo и других моделей; данные fine-tuning хранятся в изолированном Azure tenant

## Q23. Azure Machine Learning?

**Azure ML** — managed ML platform. Equivalent SageMaker, Vertex AI.

**Features:**
- Workspace для проекта
- Compute clusters (CPU/GPU)
- AutoML
- Model registry
- Endpoints (real-time, batch)
- MLflow integration
- Designer (no-code)

В **2025** — конкурирует с Vertex AI, SageMaker. Tightly integrated с Azure DevOps, GitHub.


> [!mcq]
> - [ ] Azure ML поддерживает только AutoML без ручных экспериментов | ❌ ПОСЛЕДСТВИЕ: Azure ML поддерживает полный MLOps lifecycle: ручные эксперименты, AutoML, custom training jobs, model registry, real-time/batch endpoints
> - [x] Azure ML = managed ML platform с compute clusters, AutoML, MLflow, model registry, endpoints; аналог SageMaker/Vertex AI | ✓ ПРИМЕНЯТЬ: enterprise ML с Azure DevOps CI/CD и интеграцией с Azure Data Services 📋 ПРАВИЛО: Azure ML = managed MLOps на Azure; tightly integrated с Azure ecosystem 🔗 См. Q22
> - [ ] MLflow интеграция недоступна в Azure ML | ❌ ПОСЛЕДСТВИЕ: Azure ML полностью интегрирован с MLflow для experiment tracking, model logging, и deployment; MLflow SDK работает нативно
> - [ ] Azure ML Designer требует написания Python кода | ❌ ПОСЛЕДСТВИЕ: Designer — no-code drag-and-drop интерфейс для создания ML pipelines; кастомный код нужен только для Custom Script Module

## Q24. (!) Azure vs AWS — strengths?

**Azure strengths:**
- **Enterprise integration** — AD, Office 365
- **Hybrid cloud** — Azure Arc, Azure Stack
- **AI** — Azure OpenAI Service exclusive
- **.NET, Windows, SQL Server** — best home
- **Compliance** — самый широкий portfolio
- **Microsoft contracts** — bundled discounts

**Azure weaknesses:**
- **Reliability** — заметные outages (DNS, AAD, etc.)
- **Documentation** — fragmented across services
- **UI/UX** — inconsistent (legacy)
- **Open-source ecosystem** — менее integrated
- **Pricing** — confusing models (DTU, RU, ...)


> [!mcq]
> - [ ] AWS имеет более широкий enterprise compliance portfolio чем Azure | ❌ ПОСЛЕДСТВИЕ: Azure имеет наибольшее количество compliance certifications (90+) среди облачных провайдеров, особенно для госсектора и regulated industries
> - [ ] Azure лучший выбор для open-source first стратегии | ❌ ПОСЛЕДСТВИЕ: AWS имеет более зрелую open-source экосистему (больше managed OSS services); Azure — преимущество в Microsoft-стеке, не в open-source
> - [x] Azure strengths: enterprise Microsoft integration (AD, Office 365), hybrid cloud (Arc), Azure OpenAI exclusive, .NET/SQL Server; weakness: reliability, UX consistency | ✓ ПРИМЕНЯТЬ: Microsoft-shop, hybrid cloud, enterprise compliance, OpenAI models 📋 ПРАВИЛО: Azure = Microsoft ecosystem + enterprise identity; выбирай для Windows/AD-dominated org 🔗 См. Q25
> - [ ] Azure reliability выше AWS и GCP по историческим данным | ❌ ПОСЛЕДСТВИЕ: Azure имел заметные outages (DNS, Entra ID outages); AWS исторически более стабилен; это известная слабость Azure

## Q25. (!) Когда выбирать Azure?

**Выбирай Azure когда:**
- **Уже Microsoft shop** (AD, Office 365, Windows servers)
- **.NET / SQL Server** workloads
- **Enterprise sales** — предложение Microsoft с бандлами
- **Hybrid cloud** — Azure Arc для on-prem
- **Compliance-heavy** industries (healthcare, government)
- Нужен **Azure OpenAI** specifically

**Не выбирай:**
- Cloud-native новый проект — AWS/GCP часто лучше
- Heavy Linux / open-source — AWS/GCP
- ML/Data — GCP / AWS чаще выбирают
- Startups — AWS/GCP enabled, Azure expensive

В **2025** Azure — **№1 для enterprise**, но новые startups редко выбирают.

---

## See also

- [AWS](aws-interview.md) — главный конкурент
- [GCP](gcp-interview.md) — другой конкурент
- [Serverless](serverless-interview.md) — Functions, Container Apps
- [Cloud-native Patterns](cloud-native-patterns-interview.md) — patterns
- [Kubernetes](../devops/kubernetes-interview.md) — AKS
- [Микросервисы](../architecture/microservices-interview.md) — на Azure
- [PostgreSQL](../databases/postgresql-interview.md) — Database for PostgreSQL
- [Redis](../databases/redis-interview.md) — Azure Cache for Redis
- [Apache Kafka](../messaging/kafka-interview.md) — Event Hubs
- [RabbitMQ](../messaging/rabbitmq-interview.md) — Service Bus
- [Application Security](../security/application-security-interview.md) — Entra ID
- [LLM Basics](../ai-ml/llm-basics-interview.md) — Azure OpenAI
- [MLOps](../ai-ml/mlops-interview.md) — Azure ML
- [OAuth2](../security/oauth2-interview.md) — Entra ID identity


> [!mcq]
> - [ ] Azure — лучший выбор для cloud-native стартапа без Microsoft истории | ❌ ПОСЛЕДСТВИЕ: cloud-native стартапы чаще выбирают AWS (зрелая экосистема) или GCP (data/ML); Azure дороже и сложнее для greenfield проектов
> - [ ] Azure выбирают когда нужен best-in-class Kubernetes | ❌ ПОСЛЕДСТВИЕ: GKE (GCP) считается наиболее mature managed Kubernetes; AKS хорош для Microsoft shops, но GKE — выбор для Kubernetes-first
> - [x] Azure выбирают при Microsoft ecosystem: AD/Office 365, .NET/SQL Server, hybrid on-prem, compliance-heavy industries, Azure OpenAI requirement | ✓ ПРИМЕНЯТЬ: enterprise с Active Directory, Windows-based on-prem migration, regulated industries 📋 ПРАВИЛО: Azure = Microsoft-first choice; для новых cloud-native проектов AWS/GCP чаще оптимальны 🔗 См. Q24
> - [ ] Azure — лучший выбор для тяжёлых ML/Data Science workloads | ❌ ПОСЛЕДСТВИЕ: GCP (BigQuery, Vertex AI, TPUs) и AWS (SageMaker, Redshift) чаще выбирают для ML/Data; Azure ML хорош, но не лидер в этой нише
