---
title: "Вопросы на собеседовании: GCP (Google Cloud Platform)"
description: "GCP: Compute Engine, GKE, Cloud Run, Cloud Functions, Cloud Storage, BigQuery, Pub/Sub, Spanner, Firestore, IAM, networking, отличия от AWS, цены"
tags:
  - interview
  - cloud
  - gcp-interview
aliases:
  - "GCP interview"
  - "Google Cloud interview"
  - "GCP собеседование"
  - "BigQuery interview"
  - "Cloud Run interview"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `GCP (Google Cloud Platform)`

`GCP` — облако Google (~12% доли рынка 2025, 3-е место после AWS, Azure). Сильные стороны: **BigQuery** (DWH), **Kubernetes** (Google создал K8s), **AI/ML** (Vertex AI, Gemini), **networking** (best in class). На интервью знают: BigQuery, GKE, Cloud Run, Cloud Functions, IAM, отличия от AWS.

Дата последнего обновления: 2026-04-19

## Полезные ссылки

### Официальная документация и авторитетные источники

- [GCP Documentation](https://cloud.google.com/docs)
- [Google Cloud Architecture Framework](https://cloud.google.com/architecture/framework)
- [GCP Pricing](https://cloud.google.com/pricing)
- [GCP Free Tier](https://cloud.google.com/free)
- [Google SRE Book](https://sre.google/books/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое GCP и его ключевые отличия?](#q1--что-такое-gcp-и-его-ключевые-отличия)
- [Q2. (!) Regions, Zones, Multi-region?](#q2--regions-zones-multi-region)
- [Q3. Projects и organizations?](#q3-projects-и-organizations)

**Compute**
- [Q4. (!) Compute Engine (GCE) — VMs?](#q4--compute-engine-gce--vms)
- [Q5. (!) GKE (Google Kubernetes Engine)?](#q5--gke-google-kubernetes-engine)
- [Q6. (!) Cloud Run — managed containers?](#q6--cloud-run--managed-containers)
- [Q7. (!) Cloud Functions — FaaS?](#q7--cloud-functions--faas)
- [Q8. App Engine?](#q8-app-engine)

**Storage**
- [Q9. (!) Cloud Storage — vs S3?](#q9--cloud-storage--vs-s3)
- [Q10. Persistent Disks?](#q10-persistent-disks)
- [Q11. Filestore?](#q11-filestore)

**Databases**
- [Q12. (!) Cloud SQL?](#q12--cloud-sql)
- [Q13. (!) Cloud Spanner?](#q13--cloud-spanner)
- [Q14. Firestore?](#q14-firestore)
- [Q15. Cloud Bigtable?](#q15-cloud-bigtable)
- [Q16. Memorystore (Redis, Memcached)?](#q16-memorystore-redis-memcached)

**Analytics**
- [Q17. (!) BigQuery — что особенного?](#q17--bigquery--что-особенного)
- [Q18. Dataflow (Apache Beam)?](#q18-dataflow-apache-beam)
- [Q19. Pub/Sub vs Kafka?](#q19-pubsub-vs-kafka)
- [Q20. Dataproc (Hadoop/Spark)?](#q20-dataproc-hadoopspark)

**Networking и Security**
- [Q21. (!) VPC, subnets, firewalls?](#q21--vpc-subnets-firewalls)
- [Q22. Cloud CDN, Cloud Load Balancing?](#q22-cloud-cdn-cloud-load-balancing)
- [Q23. (!) IAM в GCP — отличия от AWS?](#q23--iam-в-gcp--отличия-от-aws)
- [Q24. Service accounts?](#q24-service-accounts)

**AI/ML**
- [Q25. (!) Vertex AI?](#q25--vertex-ai)
- [Q26. Gemini API?](#q26-gemini-api)

**Сравнения**
- [Q27. (!) GCP vs AWS — strengths/weaknesses?](#q27--gcp-vs-aws--strengthsweaknesses)
- [Q28. Когда выбирать GCP?](#q28-когда-выбирать-gcp)

## Q1. (!) Что такое GCP и его ключевые отличия?

`GCP (Google Cloud Platform)` — облако от Google (с 2008). 3-е место по market share (~12%, 2025).

**Strong areas:**
- **BigQuery** — лучший cloud DWH (по мнению многих)
- **Kubernetes** — родина K8s (GKE — most mature)
- **AI/ML** — Gemini, Vertex AI, TPUs
- **Networking** — best-in-class global network
- **Data analytics** — Dataflow, Pub/Sub, Bigtable

**Weak areas:**
- Меньше services чем AWS
- Меньше regions (40 vs 33)
- Меньшее enterprise adoption (но растёт)

## Q2. (!) Regions, Zones, Multi-region?

```
Region (us-central1)
  ├── Zone us-central1-a
  ├── Zone us-central1-b
  └── Zone us-central1-c
```

**Region** — географическая location (Iowa, Frankfurt, ...).
**Zone** — physical data center (3+ per region).
**Multi-region** — services replicated across multiple regions automatically (Cloud Storage, Spanner).

**Multi-region storage** в Cloud Storage — single namespace, replicated. AWS S3 не имеет аналога (хотя есть Cross-Region Replication).

## Q3. Projects и organizations?

GCP имеет **проектную** организацию resources (vs AWS accounts):

```
Organization
  ├── Folder: Production
  │   ├── Project: my-app-prod
  │   └── Project: my-app-prod-data
  └── Folder: Development
      └── Project: my-app-dev
```

**Project** = isolated container для resources (как AWS account).

**Преимущество:** легче organize. **AWS** historically не имел org structure (потом добавили AWS Organizations).

## Q4. (!) Compute Engine (GCE) — VMs?

**Compute Engine** — IaaS, аналог AWS EC2.

**Machine types:**
- **General-purpose** — N1, N2, N2D, E2 (cheapest)
- **Compute-optimized** — C2, C3
- **Memory-optimized** — M1, M2, M3
- **GPU** — A2, G2 (NVIDIA), TPU

**Custom machine types** — выбираешь exact CPU + RAM (vs AWS fixed sizes).

**Discounts:**
- **Sustained Use Discounts** — automatic, до 30%
- **Committed Use Discounts** — 1-3 years
- **Spot VMs (Preemptible)** — до 91% off, могут быть прерваны

## Q5. (!) GKE (Google Kubernetes Engine)?

**GKE** — managed Kubernetes (Google создал K8s).

**Modes:**
- **Standard** — full control, manage nodes
- **Autopilot** (с 2021) — fully managed, pay per pod, no node management

**Особенности:**
- Auto-upgrade К8s versions
- Auto-scaling (cluster + pod)
- Workload Identity для IAM integration
- Multi-cluster meshes (Anthos)

**Считается** **gold standard** managed K8s. **EKS, AKS** — догоняют, но GKE pioneers.

Подробнее — в [Kubernetes](../devops/kubernetes-interview.md).

## Q6. (!) Cloud Run — managed containers?

**Cloud Run** — serverless containers. Просто deploy Docker image, GCP runs.

```bash
gcloud run deploy my-service --image gcr.io/my-project/my-image --port 8080
```

**Особенности:**
- **Auto-scaling** к zero (как Lambda) — pay only when running
- **HTTP-triggered** или event-triggered
- До **60 minutes** request timeout (vs Lambda 15 min)
- До 32 GB memory, 8 vCPU
- Concurrent requests per instance (vs Lambda 1)
- WebSocket, HTTP/2, gRPC

**В 2025** — **самый popular GCP serverless option** (лучше чем Cloud Functions для большинства cases).

**Cloud Run Jobs** — для batch-like tasks (без HTTP).

## Q7. (!) Cloud Functions — FaaS?

**Cloud Functions** — аналог AWS Lambda.

**Generations:**
- **Gen 1** — original FaaS (limited)
- **Gen 2** (2022+) — built on Cloud Run, more powerful

**Triggers:** HTTP, Pub/Sub, Cloud Storage, Firestore, Eventarc.

```python
def hello_http(request):
    return "Hello"
```

```bash
gcloud functions deploy hello-http \
  --gen2 --runtime=python311 --trigger-http
```

**В 2025** — Cloud Functions теряет долю в favor **Cloud Run** (более flexible).

## Q8. App Engine?

**App Engine** (с 2008) — original GCP PaaS. Deploy code, GCP manages everything.

**Standard** — restricted runtime, fastest cold start.
**Flexible** — Docker-based, more control.

В **2025** — App Engine **legacy**. Новые проекты выбирают **Cloud Run**.

## Q9. (!) Cloud Storage — vs S3?

**Cloud Storage** — object storage, аналог S3.

**Storage classes:**
- **Standard** — hot data
- **Nearline** — accessed < 1x/month, $0.01/GB
- **Coldline** — accessed < 1x/quarter
- **Archive** — long-term, $0.0012/GB

**Особенности vs S3:**
- **Single namespace** (multi-region) — auto-replicated
- **Object versioning, lifecycle**
- **Strong consistency**
- **Per-object permissions** (как S3)
- HMAC keys для S3-compatible API

**Egress costs** — у GCP **дешевле** чем AWS для большинства случаев.

## Q10. Persistent Disks?

**Persistent Disks** — block storage для GCE (аналог EBS).

**Types:**
- **pd-standard** — HDD, cheapest
- **pd-balanced** — SSD, balanced
- **pd-ssd** — SSD, performance
- **pd-extreme** — highest IOPS

**Hyperdisk** (новее) — even more options для extreme workloads.

**Преимущества над EBS:**
- Auto-snapshot scheduling built-in
- Можно snapshot live disk без consistency issues
- Regional disks (replicated across 2 zones)

## Q11. Filestore?

**Filestore** — managed NFS (аналог AWS EFS).

**Tiers:**
- Basic — small (under 1 TB)
- Enterprise — high availability
- High Scale — petabyte
- Zonal — single zone

Для shared filesystems между VMs.

## Q12. (!) Cloud SQL?

**Cloud SQL** — managed реляционные БД.

**Engines:**
- PostgreSQL
- MySQL
- SQL Server

**Features:**
- Automated backups, PITR (point-in-time recovery)
- High Availability (HA) — multi-zone replication
- Read replicas
- IAM-based authentication
- Private IP access (через VPC)

В **GCP** также: **AlloyDB** (новое, 2022) — PostgreSQL-compatible с лучшим performance (~4x). Конкурент AWS Aurora.

## Q13. (!) Cloud Spanner?

**Cloud Spanner** — globally distributed, **strongly consistent** SQL database. Уникален для GCP.

**Особенности:**
- **Horizontally scalable** SQL
- **Strong external consistency** (через TrueTime — atomic clocks)
- **5 nines availability** (multi-region)
- ACID transactions across regions
- PostgreSQL-compatible interface
- Serverless mode

**Use cases:**
- Global financial systems
- Multi-region apps требующие consistency
- Apps outgrowing single PostgreSQL

**Стоит:** $$$$. Только для real scale.

В **2025** — единственный production globally-distributed SQL DB. Конкуренты (CockroachDB, YugabyteDB, TiDB) — open-source альтернативы.

## Q14. Firestore?

**Firestore** — NoSQL document database (NoSQL, аналог DynamoDB / MongoDB).

**Особенности:**
- **Realtime sync** — клиенты получают updates автоматически
- Mobile/web SDKs (offline support)
- Strong consistency
- Hierarchical data (collections → documents → subcollections)
- Auto-scale

**Modes:**
- **Native mode** — recommended, real-time, mobile
- **Datastore mode** — backwards compatible with old Datastore

**Use cases:** mobile apps, real-time chat, collaborative apps.

## Q15. Cloud Bigtable?

**Cloud Bigtable** — wide-column NoSQL (аналог HBase, Cassandra).

**Особенности:**
- **Petascale** (миллионы reads/writes per sec)
- Low latency (~10ms)
- HBase-compatible API
- Single-cell row level transactions

**Use cases:**
- Time-series data (financial, IoT, monitoring)
- AdTech analytics
- Personalization
- ML feature serving

Реально для **large scale** (минимум $700/month за base cluster).

## Q16. Memorystore (Redis, Memcached)?

**Memorystore** — managed Redis или Memcached.

Аналог AWS ElastiCache. Standard features.

## Q17. (!) BigQuery — что особенного?

**BigQuery** — serverless DWH. Crown jewel GCP.

**Особенности:**
- **Serverless** — нет infrastructure
- **Petabyte scale** queries за seconds
- **Pay per query** (по scanned bytes) или flat-rate
- **SQL** (standard ANSI)
- **Built-in ML** (BigQuery ML — `CREATE MODEL ... USING SQL`)
- **Native multi-cloud** (BigQuery Omni — query AWS S3, Azure)
- **Streaming inserts** (real-time)
- **Geospatial functions**

```sql
CREATE MODEL `mydataset.mymodel`
OPTIONS(model_type='linear_reg') AS
SELECT label, feature1, feature2 FROM `mydataset.training`;
```

**Cost trap:** pay-per-byte-scanned. Без partitioning/clustering можно случайно "сжечь" $1000.

В **2025** — **самый popular** DWH (наряду с Snowflake).

Подробнее — в [Data Warehousing](../data-engineering/data-warehousing-interview.md).

## Q18. Dataflow (Apache Beam)?

**Dataflow** — managed **Apache Beam** runtime.

**Особенности:**
- **Unified batch + streaming**
- Auto-scaling workers
- Exactly-once processing
- Integration с BigQuery, Pub/Sub, GCS

```java
pipeline.apply(PubsubIO.readStrings().fromTopic(topic))
    .apply(Window.into(FixedWindows.of(Duration.standardSeconds(60))))
    .apply(Count.<String>perElement())
    .apply(BigQueryIO.write().to(table));
```

**Vs AWS Kinesis Data Analytics, Azure Stream Analytics:** Dataflow более mature.

Конкурирует с **Spark Streaming, Flink**. Но **Dataflow managed** — easier ops.

Подробнее — в [Flink](../data-engineering/apache-flink-interview.md) (для сравнения streaming).

## Q19. Pub/Sub vs Kafka?

**Pub/Sub** — managed messaging. Аналог AWS SNS/SQS, Kafka.

**Features:**
- **Global** — single endpoint, multi-region
- **At-least-once delivery**
- **Push or pull** subscriptions
- **Dead letter topics**
- **Filtering**
- **Ordering keys** (FIFO per key)
- **Schema validation**

**vs Kafka:**
- Pub/Sub fully managed (no clusters)
- Auto-scaling
- No partition planning needed
- Higher latency (50-200 ms typical)
- Less control

**Use Pub/Sub когда:** хочешь simple managed messaging.
**Use Kafka когда:** нужен low latency, throughput >> 10K msg/sec, replay из historical offsets, ecosystem (Kafka Streams, Connect).

## Q20. Dataproc (Hadoop/Spark)?

**Dataproc** — managed **Hadoop, Spark, Hive, Presto, Flink**.

**Особенности:**
- Spin up cluster в **90 seconds**
- Auto-scaling
- Per-second pricing
- Ephemeral clusters (spin up, run job, destroy)

**Когда:** legacy Hadoop/Spark workloads, не хочешь self-host.

В **2025** — для new workloads чаще выбирают **Dataflow** или **Spark в Databricks**.

## Q21. (!) VPC, subnets, firewalls?

**GCP VPC** уникальна:

- **Global** by default — VPC spans all regions (vs AWS regional)
- **Subnets per region** — automatic in VPC
- **Firewall rules** — global (apply across regions)

```
my-vpc (global)
  ├── subnet-us (us-central1, 10.0.0.0/20)
  ├── subnet-eu (europe-west1, 10.1.0.0/20)
  └── subnet-asia (asia-east1, 10.2.0.0/20)
```

**Преимущество:** один VPC для всех regions. **AWS** — VPC per region.

## Q22. Cloud CDN, Cloud Load Balancing?

**Cloud Load Balancing** — global, single anycast IP.
- Auto-distributes traffic к closest region
- Layer 4 (TCP/UDP) и Layer 7 (HTTP)
- Auto-scaling
- Built-in DDoS protection

**Cloud CDN** — global CDN, integrated с Load Balancing.

**Преимущество:** **single global IP** для service в multi-region (AWS требует Route53 latency-based routing для аналогичного).

Best-in-class networking — наследие Google Search infrastructure.

## Q23. (!) IAM в GCP — отличия от AWS?

**GCP IAM:**
- **Roles** = collection of permissions (vs AWS Policies)
- **Members** = users, groups, service accounts
- **Bindings** на resource: `member + role`

**Role types:**
- **Basic** — Owner, Editor, Viewer (legacy, broad permissions)
- **Predefined** — granular per-service (`roles/storage.objectViewer`)
- **Custom** — own combinations

```python
# Grant role на project
gcp set-iam-policy --member=user:alice@example.com \
  --role=roles/storage.objectAdmin
```

**Hierarchy:** Organization → Folder → Project → Resource. Permissions inherited.

**vs AWS:** GCP проще для most cases. AWS более granular но сложнее.

## Q24. Service accounts?

**Service account** — non-human identity для services (apps, workloads).

```bash
gcloud iam service-accounts create my-sa \
  --display-name "My Service Account"

# Attach к VM
gcloud compute instances create my-vm \
  --service-account=my-sa@project.iam.gserviceaccount.com
```

**Best practices:**
- One SA per workload (least privilege)
- **Workload Identity** для GKE (no JSON keys в pods)
- **Federated identities** для cross-cloud (AWS EKS → GCP via OIDC)

## Q25. (!) Vertex AI?

**Vertex AI** — unified ML platform GCP. Все ML services в одном месте.

**Components:**
- **Vertex AI Studio** — playground для Gemini
- **Model Garden** — pre-trained models (Gemini, Llama, Anthropic, ...)
- **Pipelines** — Kubeflow Pipelines managed
- **Model Registry** — model versioning
- **Feature Store** — managed feature store
- **Workbench** — managed Jupyter
- **Endpoints** — model serving
- **AutoML** — no-code training

В **2025** — VertexAI один из лидеров managed ML platforms (наряду с SageMaker, Azure ML).

## Q26. Gemini API?

**Gemini** — Google's frontier LLM (бывший Bard, PaLM 2). Конкурент GPT-4, Claude.

**Models:**
- **Gemini 2.0 Flash** — fast, cheap
- **Gemini 2.0 Pro** — balanced
- **Gemini 2.0 Ultra** — top quality
- **Gemini 2.5 Pro** — latest (в 2025)

**Особенности:**
- **Native multimodal** (text + image + video + audio)
- **1M-2M context window** (рекордный)
- **Code execution** built-in
- **Grounded with Google Search**

```python
from google import generativeai as genai
genai.configure(api_key="...")
model = genai.GenerativeModel('gemini-2.5-pro')
response = model.generate_content("Hello")
```

Подробнее — в [LLM Basics](../ai-ml/llm-basics-interview.md).

## Q27. (!) GCP vs AWS — strengths/weaknesses?

**GCP strengths:**
- BigQuery (best DWH)
- Kubernetes (родина K8s, GKE most mature)
- Networking (best-in-class global)
- AI/ML (TPUs, Gemini)
- Pricing (часто чуть дешевле)
- Better defaults (multi-zone by default, etc.)

**GCP weaknesses:**
- Меньше services
- Меньше enterprise tooling
- Меньше regions (40 vs 33)
- Smaller community
- Меньше ecosystem (third-party tools чаще для AWS)

**AWS strengths:**
- Largest service catalog
- Most mature
- Largest community
- Best documentation/training
- Most enterprise contracts

## Q28. Когда выбирать GCP?

**Выбирай GCP когда:**
- **Heavy data analytics** — BigQuery лучший
- **Kubernetes-first** — GKE excellent
- **AI/ML** — Vertex AI, Gemini, TPUs
- **Global apps** — networking strong
- **Cost-conscious** для steady workloads
- Уже Google ecosystem (Workspace)

**Не выбирать:**
- Уже AWS shop с large investment
- Нужны nichevye services AWS только имеет
- Compliance / regional requirements не покрыты GCP

В **2025** GCP — отличный выбор для **data-heavy** или **K8s-native** projects.

---

## See also

- [AWS](aws-interview.md) — главный конкурент
- [Azure](azure-interview.md) — второй конкурент
- [Serverless](serverless-interview.md) — Cloud Run, Cloud Functions
- [Cloud-native Patterns](cloud-native-patterns-interview.md) — paterns
- [Kubernetes](../devops/kubernetes-interview.md) — GKE
- [Data Warehousing](../data-engineering/data-warehousing-interview.md) — BigQuery deep dive
- [Apache Flink](../data-engineering/apache-flink-interview.md) — vs Dataflow
- [Apache Kafka](../messaging/kafka-interview.md) — vs Pub/Sub
- [PostgreSQL](../databases/postgresql-interview.md) — Cloud SQL
- [Микросервисы](../architecture/microservices-interview.md) — где live на GCP
- [LLM Basics](../ai-ml/llm-basics-interview.md) — Gemini
- [MLOps](../ai-ml/mlops-interview.md) — Vertex AI
- [Application Security](../security/application-security-interview.md) — IAM
