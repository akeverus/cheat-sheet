---
title: "Вопросы на собеседовании: Azure"
description: "Microsoft Azure: VMs, AKS, App Service, Functions, Cosmos DB, Storage, Synapse Analytics, Service Bus, Entra ID (Azure AD), отличия от AWS, enterprise integration"
tags:
  - interview
  - cloud
  - azure-interview
aliases:
  - "Azure interview"
  - "Microsoft Azure interview"
  - "Azure собеседование"
  - "Cosmos DB interview"
  - "AKS interview"
difficulty: "intermediate"
updated: "2026-04-19"
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

## Q3. Regions и Availability Zones?

**Region** — geographical area (East US, West Europe). 60+ regions.

**Availability Zones** — physical data centers в region (3+ per region для AZ-enabled regions).

**Region pairs** — Azure pairs regions (East US ↔ West US) для disaster recovery.

**Не все regions** имеют AZ support (older regions).

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

## Q8. Container Instances, Container Apps?

**Container Instances (ACI)** — single container, no orchestration. Quick container, no overhead.

**Container Apps** (с 2022) — managed serverless containers (AKS-light). Built на Kubernetes, KEDA, Dapr.

| Service | Use case |
|---------|----------|
| ACI | Single container, batch tasks |
| Container Apps | Microservices, auto-scaling, managed |
| AKS | Full control, complex setups |

**Container Apps** — equivalent GCP Cloud Run.

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

## Q10. Blob Storage tiers?

| Tier | Use case | Cost |
|------|----------|------|
| **Hot** | Frequent access | Highest storage, lowest access |
| **Cool** | Infrequent (≥30 days) | Lower storage, higher access |
| **Cold** (с 2023) | Rare (≥90 days) | Even cheaper |
| **Archive** | Almost never | Cheapest, hours to retrieve |

**Lifecycle management** — auto-transition между tiers.

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

## Q13. Database for PostgreSQL/MySQL?

**Azure Database for PostgreSQL/MySQL** — managed open-source DBs.

**Tiers:**
- Burstable (small)
- General Purpose
- Memory Optimized
- **Hyperscale (Citus)** — для PostgreSQL, distributed (sharded)

С **2024** — **Azure Database for PostgreSQL Flexible Server** — main offering.

## Q14. Azure Cache для Redis?

**Azure Cache for Redis** — managed Redis. Аналог AWS ElastiCache, GCP Memorystore.

**Tiers:**
- Basic — single node
- Standard — replication
- Premium — clustering, persistence
- Enterprise — Redis Enterprise features

С **2024** Microsoft anunciated **Azure Managed Redis** — preview новой версии.

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

## Q20. Azure Data Factory?

**ADF** — managed ETL/ELT. Аналог AWS Glue, GCP Cloud Data Fusion.

**Особенности:**
- Visual UI для pipelines
- 90+ data connectors
- Code-free transformations (Mapping Data Flows)
- Custom Python/Spark activities
- CI/CD integration

В **2025** — Microsoft pushes **Microsoft Fabric** (new unified platform), но ADF still in use.

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

- [[aws-interview|AWS]] — главный конкурент
- [[gcp-interview|GCP]] — другой конкурент
- [[serverless-interview|Serverless]] — Functions, Container Apps
- [[cloud-native-patterns-interview|Cloud-native Patterns]] — patterns
- [[kubernetes-interview|Kubernetes]] — AKS
- [[microservices-interview|Микросервисы]] — на Azure
- [[postgresql-interview|PostgreSQL]] — Database for PostgreSQL
- [[redis-interview|Redis]] — Azure Cache for Redis
- [[kafka-interview|Apache Kafka]] — Event Hubs
- [[rabbitmq-interview|RabbitMQ]] — Service Bus
- [[application-security-interview|Application Security]] — Entra ID
- [[llm-basics-interview|LLM Basics]] — Azure OpenAI
- [[mlops-interview|MLOps]] — Azure ML
- [[oauth2-interview|OAuth2]] — Entra ID identity

- [[aws-interview|AWS]]
- [[aws-lambda-interview|AWS Lambda]]
- [[cloud-native-patterns-interview|Cloud-native Patterns]]
- [[gcp-interview|GCP (Google Cloud Platform)]]
- [[serverless-interview|Serverless]]
- [[ai-agents-interview|AI Agents]]
