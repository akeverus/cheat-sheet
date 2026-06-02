---
title: "Вопросы на собеседовании: GCP (Google Cloud Platform)"
description: "GCP: Compute Engine, GKE, Cloud Run, Cloud Functions, Cloud Storage, BigQuery, Pub/Sub, Spanner, Firestore, IAM, networking, отличия от AWS, цены"
tags:
  - interview
  - cloud
  - gcp-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "GCP"
  - "Google Cloud Platform"
  - "GCP interview"
prerequisites:
  - "[[gcp-basics]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `GCP (Google Cloud Platform)`

`GCP` — облако Google (~12% доли рынка в 2025, 3-е место после AWS и Azure). Сильные стороны: **BigQuery** (DWH), **Kubernetes** (Google создал K8s), **AI/ML** (Vertex AI, Gemini), **сеть** (лучшая в классе). На интервью спрашивают про: BigQuery, GKE, Cloud Run, Cloud Functions, IAM, отличия от AWS.

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

(!) Что такое GCP и его ключевые отличия?

`GCP (Google Cloud Platform)` — облако от Google (с 2008 года). 3-е место по доле рынка (~12% в 2025).

**Сильные стороны:**
- **BigQuery** — лучший облачный DWH (по мнению многих)
- **Kubernetes** — родина K8s (GKE — самый зрелый)
- **AI/ML** — Gemini, Vertex AI, TPU
- **Сеть** — глобальная сеть лучшая в классе
- **Аналитика данных** — Dataflow, Pub/Sub, Bigtable

**Слабые стороны:**
- Меньше сервисов, чем у AWS
- Меньше регионов (40 против 33)
- Слабее проникновение в enterprise (но растёт)

## Q2. (!) Regions, Zones, Multi-region?

```
Region (us-central1)
  ├── Zone us-central1-a
  ├── Zone us-central1-b
  └── Zone us-central1-c
```

**Region** — географическое расположение (Iowa, Frankfurt, ...).
**Zone** — физический дата-центр (3+ на регион).
**Multi-region** — сервисы автоматически реплицируются между несколькими регионами (Cloud Storage, Spanner).

**Multi-region storage** в Cloud Storage — единое пространство имён с репликацией. У AWS S3 прямого аналога нет (хотя есть Cross-Region Replication).

## Q3. Projects и organizations?

GCP организует ресурсы **по проектам** (в отличие от аккаунтов AWS):

```
Organization
  ├── Folder: Production
  │   ├── Project: my-app-prod
  │   └── Project: my-app-prod-data
  └── Folder: Development
      └── Project: my-app-dev
```

**Project** = изолированный контейнер для ресурсов (как аккаунт в AWS).

**Преимущество:** проще организовывать. **AWS** исторически не имел организационной структуры (потом добавили AWS Organizations).

## Q4. (!) Compute Engine (GCE) — VMs?

**Compute Engine** — IaaS, аналог AWS EC2.

**Типы машин (machine types):**
- **General-purpose** — N1, N2, N2D, E2 (самые дешёвые)
- **Compute-optimized** — C2, C3
- **Memory-optimized** — M1, M2, M3
- **GPU** — A2, G2 (NVIDIA), TPU

**Custom machine types** — задаёшь точное число CPU + объём RAM (в отличие от фиксированных размеров у AWS).

**Скидки:**
- **Sustained Use Discounts** — автоматические, до 30%
- **Committed Use Discounts** — на 1–3 года
- **Spot VMs (Preemptible)** — до 91% дешевле, могут быть прерваны

## Q5. (!) GKE (Google Kubernetes Engine)?

**GKE** — управляемый Kubernetes (Google создал K8s).

**Режимы:**
- **Standard** — полный контроль, ноды управляются вручную
- **Autopilot** (с 2021) — полностью управляемый, оплата за под, без управления нодами

**Особенности:**
- Автообновление версий K8s
- Автомасштабирование (кластера + подов)
- Workload Identity для интеграции с IAM
- Multi-cluster meshes (Anthos)

**Считается** **золотым стандартом** управляемого K8s. **EKS, AKS** — догоняют, но первопроходец — GKE.

Подробнее — в [Kubernetes](../devops/kubernetes-interview.md).

## Q6. (!) Cloud Run — managed containers?

**Cloud Run** — serverless-контейнеры. Просто деплоишь Docker-образ, а GCP его запускает.

```bash
gcloud run deploy my-service --image gcr.io/my-project/my-image --port 8080
```

**Особенности:**
- **Автомасштабирование** до нуля (как Lambda) — платишь только во время работы
- Запуск **по HTTP** или по событию
- Таймаут запроса до **60 минут** (против 15 минут у Lambda)
- До 32 ГБ памяти, 8 vCPU
- Несколько одновременных запросов на инстанс (у Lambda — 1)
- WebSocket, HTTP/2, gRPC

**В 2025** — **самый популярный serverless-вариант в GCP** (для большинства случаев лучше, чем Cloud Functions).

**Cloud Run Jobs** — для batch-задач (без HTTP).

## Q7. (!) Cloud Functions — FaaS?

**Cloud Functions** — аналог AWS Lambda.

**Поколения:**
- **Gen 1** — изначальный FaaS (с ограничениями)
- **Gen 2** (с 2022) — построен на Cloud Run, мощнее

**Триггеры:** HTTP, Pub/Sub, Cloud Storage, Firestore, Eventarc.

```python
def hello_http(request):
    return "Hello"
```

```bash
gcloud functions deploy hello-http \
  --gen2 --runtime=python311 --trigger-http
```

**В 2025** — Cloud Functions теряет долю в пользу **Cloud Run** (более гибкого).

## Q8. App Engine?

**App Engine** (с 2008) — изначальный PaaS в GCP. Деплоишь код, а GCP управляет всем остальным.

**Standard** — ограниченный рантайм, самый быстрый холодный старт.
**Flexible** — на базе Docker, больше контроля.

В **2025** — App Engine **устаревший**. Новые проекты выбирают **Cloud Run**.

## Q9. (!) Cloud Storage — vs S3?

**Cloud Storage** — объектное хранилище, аналог S3.

**Классы хранения:**
- **Standard** — горячие данные
- **Nearline** — обращения реже 1 раза в месяц, $0.01/ГБ
- **Coldline** — обращения реже 1 раза в квартал
- **Archive** — долгосрочное хранение, $0.0012/ГБ

**Отличия от S3:**
- **Единое пространство имён** (multi-region) — с автоматической репликацией
- **Версионирование объектов и lifecycle-политики**
- **Строгая согласованность (strong consistency)**
- **Права на уровне объекта** (как в S3)
- HMAC-ключи для S3-совместимого API

**Стоимость egress (исходящего трафика)** — у GCP **дешевле**, чем у AWS, в большинстве случаев.

## Q10. Persistent Disks?

**Persistent Disks** — блочное хранилище для GCE (аналог EBS).

**Типы:**
- **pd-standard** — HDD, самый дешёвый
- **pd-balanced** — SSD, сбалансированный
- **pd-ssd** — SSD, производительный
- **pd-extreme** — максимальный IOPS

**Hyperdisk** (новее) — ещё больше вариантов для экстремальных нагрузок.

**Преимущества над EBS:**
- Встроенное расписание авто-снапшотов
- Можно снять снапшот живого диска без проблем с согласованностью
- Региональные диски (реплицируются между 2 зонами)

## Q11. Filestore?

**Filestore** — управляемый NFS (аналог AWS EFS).

**Уровни (tiers):**
- Basic — небольшой объём (до 1 ТБ)
- Enterprise — высокая доступность
- High Scale — петабайты
- Zonal — одна зона

Для файловых систем, общих для нескольких VM.

## Q12. (!) Cloud SQL?

**Cloud SQL** — управляемые реляционные БД.

**Движки:**
- PostgreSQL
- MySQL
- SQL Server

**Возможности:**
- Автоматические бэкапы, PITR (point-in-time recovery)
- High Availability (HA) — репликация между зонами
- Read-реплики
- Аутентификация на основе IAM
- Доступ по приватному IP (через VPC)

В **GCP** также есть **AlloyDB** (появился в 2022) — PostgreSQL-совместимый с лучшей производительностью (~в 4 раза). Конкурент AWS Aurora.

## Q13. (!) Cloud Spanner?

**Cloud Spanner** — глобально распределённая SQL-база со **строгой согласованностью**. Уникальна для GCP.

**Особенности:**
- **Горизонтально масштабируемый** SQL
- **Строгая внешняя согласованность** (через TrueTime — атомные часы)
- **Доступность «пять девяток»** (multi-region)
- ACID-транзакции между регионами
- PostgreSQL-совместимый интерфейс
- Serverless-режим

**Сценарии использования:**
- Глобальные финансовые системы
- Multi-region приложения, требующие согласованности
- Приложения, переросшие один инстанс PostgreSQL

**Цена:** $$$$. Только для действительно большого масштаба.

В **2025** — единственная глобально распределённая SQL-БД в продакшене. Конкуренты (CockroachDB, YugabyteDB, TiDB) — open-source альтернативы.

## Q14. Firestore?

**Firestore** — документная NoSQL-база (аналог DynamoDB / MongoDB).

**Особенности:**
- **Синхронизация в реальном времени** — клиенты получают обновления автоматически
- SDK для mobile/web (поддержка офлайн-режима)
- Строгая согласованность
- Иерархические данные (collections → documents → subcollections)
- Автомасштабирование

**Режимы:**
- **Native mode** — рекомендуемый, реальное время, mobile
- **Datastore mode** — обратная совместимость со старым Datastore

**Сценарии использования:** мобильные приложения, чат в реальном времени, приложения для совместной работы.

## Q15. Cloud Bigtable?

**Cloud Bigtable** — wide-column NoSQL (аналог HBase, Cassandra).

**Особенности:**
- **Петабайтный масштаб** (миллионы чтений/записей в секунду)
- Низкая задержка (~10 мс)
- HBase-совместимый API
- Транзакции на уровне строки (одна ячейка)

**Сценарии использования:**
- Временные ряды (финансы, IoT, мониторинг)
- AdTech-аналитика
- Персонализация
- Раздача ML-фич (feature serving)

Реально оправдан только на **большом масштабе** (минимум $700/месяц за базовый кластер).

## Q16. Memorystore (Redis, Memcached)?

**Memorystore** — управляемый Redis или Memcached.

Аналог AWS ElastiCache. Стандартный набор возможностей.

## Q17. (!) BigQuery — что особенного?

**BigQuery** — serverless DWH. Жемчужина GCP.

**Особенности:**
- **Serverless** — никакой инфраструктуры
- Запросы **петабайтного масштаба** за секунды
- **Оплата за запрос** (по объёму просканированных байт) или фиксированный тариф
- **SQL** (стандарт ANSI)
- **Встроенный ML** (BigQuery ML — `CREATE MODEL ... USING SQL`)
- **Нативный multi-cloud** (BigQuery Omni — запросы к AWS S3, Azure)
- **Streaming-вставки** (в реальном времени)
- **Геопространственные функции**

```sql
CREATE MODEL `mydataset.mymodel`
OPTIONS(model_type='linear_reg') AS
SELECT label, feature1, feature2 FROM `mydataset.training`;
```

**Ловушка по стоимости:** оплата за каждый просканированный байт. Без партиционирования/кластеризации можно случайно «сжечь» $1000.

В **2025** — **самый популярный** DWH (наряду со Snowflake).

Подробнее — в [Data Warehousing](../data-engineering/data-warehousing-interview.md).

## Q18. Dataflow (Apache Beam)?

**Dataflow** — управляемый рантайм **Apache Beam**.

**Особенности:**
- **Единый движок для batch + streaming**
- Автомасштабирование воркеров
- Обработка exactly-once
- Интеграция с BigQuery, Pub/Sub, GCS

```java
pipeline.apply(PubsubIO.readStrings().fromTopic(topic))
    .apply(Window.into(FixedWindows.of(Duration.standardSeconds(60))))
    .apply(Count.<String>perElement())
    .apply(BigQueryIO.write().to(table));
```

**Против AWS Kinesis Data Analytics, Azure Stream Analytics:** Dataflow более зрелый.

Конкурирует со **Spark Streaming, Flink**. Но **Dataflow управляемый** — проще в эксплуатации.

Подробнее — в [Flink](../data-engineering/apache-flink-interview.md) (для сравнения streaming-движков).

## Q19. Pub/Sub vs Kafka?

**Pub/Sub** — управляемый messaging. Аналог AWS SNS/SQS, Kafka.

**Возможности:**
- **Глобальный** — единая точка входа, multi-region
- **Доставка at-least-once**
- Подписки **push или pull**
- **Dead letter topics**
- **Фильтрация**
- **Ordering keys** (FIFO в рамках ключа)
- **Валидация схем**

**Против Kafka:**
- Pub/Sub полностью управляемый (нет кластеров)
- Автомасштабирование
- Не нужно планировать партиции
- Выше задержка (обычно 50–200 мс)
- Меньше контроля

**Используй Pub/Sub, когда:** нужен простой управляемый messaging.
**Используй Kafka, когда:** нужны низкая задержка, пропускная способность >> 10K сообщений/сек, реплей из исторических offset-ов, экосистема (Kafka Streams, Connect).

## Q20. Dataproc (Hadoop/Spark)?

**Dataproc** — управляемые **Hadoop, Spark, Hive, Presto, Flink**.

**Особенности:**
- Поднятие кластера за **90 секунд**
- Автомасштабирование
- Посекундная тарификация
- Эфемерные кластеры (поднял, выполнил задачу, удалил)

**Когда:** legacy-нагрузки на Hadoop/Spark, когда не хочешь хостить самостоятельно.

В **2025** — для новых нагрузок чаще выбирают **Dataflow** или **Spark в Databricks**.

## Q21. (!) VPC, subnets, firewalls?

**GCP VPC** уникальна:

- **Глобальна по умолчанию** — VPC охватывает все регионы (в отличие от региональной у AWS)
- **Подсети на регион** — создаются автоматически внутри VPC
- **Правила firewall** — глобальные (действуют во всех регионах)

```
my-vpc (global)
  ├── subnet-us (us-central1, 10.0.0.0/20)
  ├── subnet-eu (europe-west1, 10.1.0.0/20)
  └── subnet-asia (asia-east1, 10.2.0.0/20)
```

**Преимущество:** один VPC на все регионы. У **AWS** — отдельный VPC на каждый регион.

## Q22. Cloud CDN, Cloud Load Balancing?

**Cloud Load Balancing** — глобальный, единый anycast-IP.
- Автоматически распределяет трафик к ближайшему региону
- Layer 4 (TCP/UDP) и Layer 7 (HTTP)
- Автомасштабирование
- Встроенная защита от DDoS

**Cloud CDN** — глобальный CDN, интегрированный с Load Balancing.

**Преимущество:** **единый глобальный IP** для сервиса в multi-region (для аналогичного у AWS нужен Route53 с latency-based routing).

Сеть лучшая в классе — наследие инфраструктуры Google Search.

## Q23. (!) IAM в GCP — отличия от AWS?

**GCP IAM:**
- **Roles (роли)** = набор разрешений (в отличие от Policies у AWS)
- **Members (участники)** = пользователи, группы, service accounts
- **Bindings (привязки)** на ресурсе: `member + role`

**Типы ролей:**
- **Basic** — Owner, Editor, Viewer (legacy, широкие разрешения)
- **Predefined** — гранулярные, для каждого сервиса (`roles/storage.objectViewer`)
- **Custom** — собственные комбинации

```python
# Grant role на project
gcp set-iam-policy --member=user:alice@example.com \
  --role=roles/storage.objectAdmin
```

**Иерархия:** Organization → Folder → Project → Resource. Разрешения наследуются.

**Против AWS:** GCP проще в большинстве случаев. AWS более гранулярен, но сложнее.

## Q24. Service accounts?

**Service account** — нечеловеческая identity для сервисов (приложений, нагрузок).

```bash
gcloud iam service-accounts create my-sa \
  --display-name "My Service Account"

# Attach к VM
gcloud compute instances create my-vm \
  --service-account=my-sa@project.iam.gserviceaccount.com
```

**Best practices:**
- Один SA на нагрузку (принцип наименьших привилегий)
- **Workload Identity** для GKE (без JSON-ключей в подах)
- **Federated identities** для cross-cloud (AWS EKS → GCP через OIDC)

## Q25. (!) Vertex AI?

**Vertex AI** — единая ML-платформа GCP. Все ML-сервисы в одном месте.

**Компоненты:**
- **Vertex AI Studio** — playground для Gemini
- **Model Garden** — предобученные модели (Gemini, Llama, Anthropic, ...)
- **Pipelines** — управляемые Kubeflow Pipelines
- **Model Registry** — версионирование моделей
- **Feature Store** — управляемый feature store
- **Workbench** — управляемый Jupyter
- **Endpoints** — раздача моделей (model serving)
- **AutoML** — обучение без кода

В **2025** — Vertex AI один из лидеров среди управляемых ML-платформ (наряду с SageMaker, Azure ML).

## Q26. Gemini API?

**Gemini** — флагманская LLM от Google (бывшие Bard, PaLM 2). Конкурент GPT-4, Claude.

**Модели:**
- **Gemini 2.0 Flash** — быстрая, дешёвая
- **Gemini 2.0 Pro** — сбалансированная
- **Gemini 2.0 Ultra** — топовое качество
- **Gemini 2.5 Pro** — самая свежая (в 2025)

**Особенности:**
- **Нативная мультимодальность** (текст + изображения + видео + аудио)
- **Контекстное окно 1M–2M** (рекордное)
- **Встроенное выполнение кода (code execution)**
- **Привязка к Google Search (grounding)**

```python
from google import generativeai as genai
genai.configure(api_key="...")
model = genai.GenerativeModel('gemini-2.5-pro')
response = model.generate_content("Hello")
```

Подробнее — в [LLM Basics](../ai-ml/llm-basics-interview.md).

## Q27. (!) GCP vs AWS — strengths/weaknesses?

**Сильные стороны GCP:**
- BigQuery (лучший DWH)
- Kubernetes (родина K8s, GKE — самый зрелый)
- Сеть (глобальная, лучшая в классе)
- AI/ML (TPU, Gemini)
- Цены (часто чуть дешевле)
- Более удачные настройки по умолчанию (multi-zone по умолчанию и т. д.)

**Слабые стороны GCP:**
- Меньше сервисов
- Меньше enterprise-инструментов
- Меньше регионов (40 против 33)
- Меньше сообщество
- Беднее экосистема (сторонние инструменты чаще делают под AWS)

**Сильные стороны AWS:**
- Самый большой каталог сервисов
- Самый зрелый
- Самое большое сообщество
- Лучшие документация и обучающие материалы
- Больше всего enterprise-контрактов

## Q28. Когда выбирать GCP?

**Выбирай GCP, когда:**
- **Активная аналитика данных** — BigQuery лучший
- **Kubernetes на первом месте** — GKE превосходен
- **AI/ML** — Vertex AI, Gemini, TPU
- **Глобальные приложения** — сильная сеть
- **Важна экономия** для стабильных нагрузок
- Уже используешь экосистему Google (Workspace)

**Не выбирать, когда:**
- Уже сделаны крупные инвестиции в AWS
- Нужны нишевые сервисы, которые есть только у AWS
- Требования по compliance / регионам не покрыты GCP

В **2025** GCP — отличный выбор для проектов с **большим объёмом данных** или **K8s-native** архитектурой.

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

- [AWS Lambda](aws-lambda-interview.md)
- [Azure](azure-interview.md)
- [Cloud-native Patterns](cloud-native-patterns-interview.md)
- [Serverless](serverless-interview.md)
- [AI Agents](../ai-ml/ai-agents-interview.md)
