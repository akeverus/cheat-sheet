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

`Microsoft Azure` — облачный провайдер №2 (~24% рынка, 2025). Сильные стороны: **интеграция с enterprise** (Active Directory, Office 365, Windows), **AI** (тесная интеграция с OpenAI), **гибридное облако** (Azure Arc). На интервью спрашивают: VMs, AKS, App Service, Functions, Cosmos DB, AAD/Entra ID.

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

`Microsoft Azure` — облако Microsoft (с 2010). №2 по доле рынка (~24%, 2025).

**Сильные стороны:**
- **Интеграция с enterprise** — Active Directory (Entra ID), Office 365
- **Гибридное облако** — Azure Arc, Stack
- **AI** — эксклюзивное партнёрство с OpenAI (Azure OpenAI Service)
- **Экосистема .NET** — лучший выбор для C#/Windows
- **Compliance** — широкий каталог сертификаций

**Слабые стороны на фоне AWS:**
- Менее согласованный UI/UX (legacy-сервисы)
- Документация сложнее
- Меньше возможностей в open-source
- Надёжность — иногда сбои (известные outages)

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

**Subscription** = граница биллинга (как AWS account).
**Resource Group** = логический контейнер внутри subscription. Все ресурсы должны лежать в RG.
**Management Group** = организует subscriptions (для политик).

**Преимущество:** Resource Groups позволяют **удалить всё одновременно**.

## Q3. Regions и Availability Zones?

**Region** — географическая зона (East US, West Europe). Более 60 регионов.

**Availability Zones** — физические дата-центры внутри региона (3+ на регион для регионов с поддержкой AZ).

**Region pairs** — Azure связывает регионы в пары (East US ↔ West US) для disaster recovery.

**Не все регионы** поддерживают AZ (старые регионы).

## Q4. (!) Azure VMs?

**Azure Virtual Machines** — IaaS, аналог AWS EC2.

**Серии VM:**
- **B** (Burstable) — переменные нагрузки
- **D** (General purpose) — рабочая лошадка, общего назначения
- **F** (Compute optimized) — нагрузки, интенсивные по CPU
- **E** (Memory optimized) — большие базы данных, оптимизация по памяти
- **L** (Storage optimized) — локальный SSD, оптимизация по хранилищу
- **N** (GPU) — задачи с GPU
- **H** (HPC) — высокопроизводительные вычисления

**Тарификация:**
- Pay-as-you-go
- **Reserved Instances** — 1–3 года, скидка до 72%
- **Spot VMs** — скидка до 90%
- **Hybrid Benefit** — принести свои лицензии Windows/SQL Server

## Q5. (!) AKS (Azure Kubernetes Service)?

**AKS** — управляемый Kubernetes на Azure.

**Особенности:**
- Бесплатный control plane (платишь только за nodes)
- Интеграция с Entra ID
- Автомасштабирование
- Сеть на Azure CNI или kubenet
- Кластеры с несколькими зонами (multi-zone)
- Поддержка Windows-контейнеров

**На фоне GKE:** GKE считается чуть более зрелым, но AKS догнал. Для компаний на стеке Microsoft AKS — естественный выбор.

## Q6. (!) Azure Functions?

**Azure Functions** — FaaS, аналог AWS Lambda.

**Планы хостинга:**
- **Consumption** — оплата за выполнение (настоящий serverless)
- **Premium** — заранее прогретые инстансы (без cold start)
- **Dedicated** (App Service Plan) — работа на VM

**Триггеры:**
- HTTP-запрос
- по таймеру (Timer)
- Blob Storage
- Queue Storage / Service Bus
- Event Hubs
- изменения в Cosmos DB

**Языки:** C#, JavaScript, Python, Java, PowerShell, Rust, Go (custom).

```csharp
[FunctionName("HelloHttp")]
public static IActionResult Run(
    [HttpTrigger(AuthorizationLevel.Anonymous, "get")] HttpRequest req)
{
    return new OkObjectResult("Hello");
}
```

**Durable Functions** — для stateful-воркфлоу (аналог Step Functions).

## Q7. (!) App Service?

**App Service** — управляемый PaaS для веб-приложений.

**Особенности:**
- Автомасштабирование, балансировка нагрузки
- Интеграция CI/CD (GitHub Actions, Azure DevOps)
- Свои домены, SSL, аутентификация (через Entra ID)
- Поддержка множества языков (.NET, Node, Java, Python, PHP)
- Slots (для blue-green деплоя)

**Тарифные уровни:**
- Free / Shared (для тестирования)
- Basic / Standard / Premium / Isolated (от базового до изолированного)

**App Service Plan** = вычислительные ресурсы, можно разместить несколько приложений.

В **2025** App Service остаётся самым популярным выбором для **классических веб-приложений** на Azure.

## Q8. Container Instances, Container Apps?

**Container Instances (ACI)** — один контейнер, без оркестрации. Быстрый запуск контейнера, без накладных расходов.

**Container Apps** (с 2022) — управляемые serverless-контейнеры (облегчённый AKS). Построены на Kubernetes, KEDA, Dapr.

| Сервис | Сценарий применения |
|---------|----------|
| ACI | Один контейнер, batch-задачи |
| Container Apps | Микросервисы, автомасштабирование, управляемый сервис |
| AKS | Полный контроль, сложные конфигурации |

**Container Apps** — эквивалент GCP Cloud Run.

## Q9. (!) Azure Storage account types?

Azure Storage account — единое пространство имён для:
- **Blob Storage** — объекты (как S3)
- **File Storage** — SMB/NFS-шары
- **Queue Storage** — простой обмен сообщениями
- **Table Storage** — NoSQL (legacy, используется в Cosmos DB Tables)
- **Disk Storage** — управляемые диски для VMs

**Типы аккаунтов:**
- **Standard general-purpose v2** — самый распространённый, общего назначения
- **Premium Block Blob** — низкая задержка
- **Premium File Shares** — премиальные файловые шары
- **Premium Page Blobs** — для VMs

## Q10. Blob Storage tiers?

| Tier | Сценарий применения | Стоимость |
|------|----------|------|
| **Hot** | Частый доступ | Дорогое хранение, дешёвый доступ |
| **Cool** | Редкий (≥30 дней) | Дешевле хранение, дороже доступ |
| **Cold** (с 2023) | Очень редкий (≥90 дней) | Ещё дешевле |
| **Archive** | Почти никогда | Самое дешёвое, извлечение часами |

**Lifecycle management** — автоматический переход между tier'ами.

## Q11. (!) Azure SQL Database?

**Azure SQL Database** — управляемый Microsoft SQL Server.

**Варианты развёртывания:**
- **Single Database** — отдельная БД
- **Elastic Pool** — общие ресурсы между БД
- **Managed Instance** — полная совместимость с SQL Server

**Уровни сервиса:**
- **DTU-based** (legacy) — связка compute + IO
- **vCore-based** (рекомендуется) — раздельные compute + storage
  - **General Purpose**, **Business Critical** (in-memory), **Hyperscale** (scale-out)

**Azure SQL** — без вопросов лучшее место, чтобы запускать SQL Server.

## Q12. (!) Cosmos DB?

**Cosmos DB** — мультимодельная, глобально распределённая NoSQL. Жемчужина Azure.

**Особенности:**
- **Мультимодельность** — Document (Mongo, JSON), Key-Value, Column (Cassandra), Graph (Gremlin)
- **Глобальная распределённость** — репликация по нескольким регионам
- **5 уровней согласованности** — Strong, Bounded staleness, Session, Consistent prefix, Eventual
- **Задержка в единицы миллисекунд** при любом масштабе
- **SLA 99,999%** для multi-region записей
- **Serverless** или provisioned RU

**Тарификация:** **Request Units (RU/s)** — абстрактная единица производительности.

**Сценарии применения:**
- IoT
- персонализация
- рекомендации в реальном времени
- глобальные приложения
- мобильные бэкенды

**Ловушка по стоимости:** можно случайно перевыделить RU/s → дорого.

## Q13. Database for PostgreSQL/MySQL?

**Azure Database for PostgreSQL/MySQL** — управляемые open-source-БД.

**Уровни:**
- Burstable (небольшие)
- General Purpose — общего назначения
- Memory Optimized — оптимизация по памяти
- **Hyperscale (Citus)** — для PostgreSQL, распределённый (шардированный)

С **2024** основное предложение — **Azure Database for PostgreSQL Flexible Server**.

## Q14. Azure Cache для Redis?

**Azure Cache for Redis** — управляемый Redis. Аналог AWS ElastiCache, GCP Memorystore.

**Уровни:**
- Basic — один узел
- Standard — репликация
- Premium — кластеризация, персистентность
- Enterprise — возможности Redis Enterprise

С **2024** Microsoft анонсировала **Azure Managed Redis** — preview новой версии.

## Q15. (!) VNet, NSG?

**VNet (Virtual Network)** — аналог AWS VPC.

**NSG (Network Security Group)** — правила firewall (аналог Security Groups + NACLs).

```
VNet (10.0.0.0/16)
  ├── Subnet: web (10.0.1.0/24) + NSG-web
  ├── Subnet: app (10.0.2.0/24) + NSG-app
  └── Subnet: data (10.0.3.0/24) + NSG-data (no internet)
```

**Application Security Groups (ASG)** — логически группируют VMs для правил firewall.

## Q16. Application Gateway, Front Door?

**Application Gateway** — балансировщик нагрузки уровня L7 (HTTP) внутри региона.
- Маршрутизация по пути (path-based)
- Интеграция с WAF
- Терминация SSL

**Azure Front Door** — глобальный балансировщик L7 + CDN.
- Глобальный anycast
- Отказоустойчивость между регионами (multi-region failover)
- WAF
- Терминация SSL

**Load Balancer** — уровень L4 (TCP/UDP).
**Traffic Manager** — балансировка на уровне DNS.

## Q17. (!) Entra ID (бывший Azure AD)?

**Microsoft Entra ID** (переименован из Azure Active Directory в 2023) — провайдер идентификации.

**Особенности:**
- **SSO** для тысяч SaaS-приложений
- **Conditional Access** — гранулярные политики
- **MFA** встроена
- **Passwordless** (FIDO2)
- сценарии **B2B / B2C**
- **Identity Governance** (жизненный цикл, access reviews)

**Жемчужина Microsoft Cloud.** Огромная база установок в enterprise.

**Интеграция** с ресурсами Azure — RBAC через Entra ID.

```bash
az role assignment create --assignee user@contoso.com \
  --role "Storage Blob Data Reader" --scope /subscriptions/.../my-storage
```

## Q18. Managed Identities?

**Managed Identity** — автоматически создаваемая Entra ID identity для ресурса Azure (VM, App Service, Function).

```csharp
// No credentials в коде!
var credential = new DefaultAzureCredential();
var blobClient = new BlobServiceClient(uri, credential);
```

**Типы:**
- **System-assigned** — привязана к жизненному циклу ресурса
- **User-assigned** — независимая, можно переиспользовать совместно

Эквивалент **AWS IAM Roles for EC2/Lambda** или **GCP Service Accounts**.

## Q19. (!) Synapse Analytics?

**Azure Synapse Analytics** — единая платформа аналитики.

**Включает:**
- **Synapse SQL** — хранилище данных (DWH, аналог Snowflake)
- **Apache Spark pools** — пулы Apache Spark
- **Data Explorer** (Kusto)
- **Pipelines** (интеграция данных)

**Synapse SQL:**
- **Dedicated SQL pool** — provisioned-DWH
- **Serverless SQL pool** — запросы к данным в Azure Storage без их загрузки

В **2025** Synapse — ответ Microsoft на BigQuery / Snowflake. Но есть проблемы с надёжностью и UX. Многие выбирают **Microsoft Fabric** (новую объединённую платформу).

## Q20. Azure Data Factory?

**ADF** — управляемый ETL/ELT. Аналог AWS Glue, GCP Cloud Data Fusion.

**Особенности:**
- Визуальный UI для pipeline'ов
- 90+ коннекторов к данным
- Трансформации без кода (Mapping Data Flows)
- Кастомные activities на Python/Spark
- Интеграция CI/CD

В **2025** Microsoft продвигает **Microsoft Fabric** (новую объединённую платформу), но ADF всё ещё используется.

## Q21. Event Hubs vs Service Bus?

**Event Hubs** — высокопроизводительный стриминг событий. Аналог Kafka.
- Миллионы событий/сек
- партиции, группы потребителей (consumer groups)
- Совместимость с протоколом Apache Kafka (на уровне Premium)

**Service Bus** — enterprise-обмен сообщениями. Аналог RabbitMQ.
- очереди (Queues) и темы (Topics, pub/sub)
- транзакции, сессии, dead-letter
- AMQP 1.0
- Ниже пропускная способность, чем у Event Hubs

| Сервис | Сценарий применения |
|---------|----------|
| Event Hubs | Стриминг, телеметрия, IoT |
| Service Bus | Бизнес-сообщения, транзакции |

## Q22. (!) Azure OpenAI Service?

**Azure OpenAI Service** — эксклюзивное предложение Microsoft. Хостинг моделей OpenAI (GPT-4 и т. д.) на инфраструктуре Azure.

**Преимущества против прямого OpenAI:**
- **Enterprise-compliance** (GDPR, HIPAA, SOC)
- Доступ через **частную сеть**
- **Customer-managed keys** (BYOK)
- **Данные не используются для обучения OpenAI**
- **SLA и поддержка** через Microsoft

**Сценарий:** enterprise хочет GPT-4, но не может использовать публичный OpenAI API.

В **2025** — самый популярный способ для enterprise использовать модели OpenAI.

## Q23. Azure Machine Learning?

**Azure ML** — управляемая ML-платформа. Эквивалент SageMaker, Vertex AI.

**Возможности:**
- Workspace для проекта
- вычислительные кластеры (CPU/GPU)
- AutoML
- Реестр моделей
- Endpoints (в реальном времени и пакетные)
- Интеграция с MLflow
- Designer (без кода)

В **2025** конкурирует с Vertex AI, SageMaker. Тесно интегрирована с Azure DevOps, GitHub.

## Q24. (!) Azure vs AWS — strengths?

**Сильные стороны Azure:**
- **Интеграция с enterprise** — AD, Office 365
- **Гибридное облако** — Azure Arc, Azure Stack
- **AI** — эксклюзивный Azure OpenAI Service
- **.NET, Windows, SQL Server** — лучший дом
- **Compliance** — самый широкий портфель
- **Контракты Microsoft** — скидки в составе бандлов

**Слабые стороны Azure:**
- **Надёжность** — заметные сбои (DNS, AAD и т. д.)
- **Документация** — разрозненная по сервисам
- **UI/UX** — несогласованный (legacy)
- **Экосистема open-source** — слабее интегрирована
- **Тарификация** — запутанные модели (DTU, RU, ...)

## Q25. (!) Когда выбирать Azure?

**Выбирай Azure когда:**
- **Уже на стеке Microsoft** (AD, Office 365, Windows-серверы)
- нагрузки на **.NET / SQL Server**
- **Enterprise-продажи** — предложение Microsoft с бандлами
- **Гибридное облако** — Azure Arc для on-prem
- отрасли с **жёстким compliance** (здравоохранение, госсектор)
- конкретно нужен **Azure OpenAI**

**Не выбирай:**
- Новый cloud-native проект — AWS/GCP часто лучше
- Много Linux / open-source — AWS/GCP
- ML/Data — чаще выбирают GCP / AWS
- Стартапы — AWS/GCP удобнее, Azure дорогой

В **2025** Azure — **№1 для enterprise**, но новые стартапы выбирают его редко.

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

