---
title: "GCP Services"
description: "Google Cloud Platform предоставляет более 150 сервисов для различных задач - от вычислений и хранения данных до машинного обучения и IoT. Этот документ охватывает основные сервисы GCP, их назначение, use cases и лучшие практики использования в production средах. Документ дополняе"
tags:
  - platform
  - cloud-providers
  - gcp-services
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **GCP Services**

**Google Cloud Platform** предоставляет более `150` сервисов для различных задач - от вычислений и хранения данных до машинного обучения и **IoT**. Этот документ охватывает основные сервисы **GCP**, их назначение, **use cases** и лучшие практики использования в **production** средах. Документ дополняет [GCP Basics](gcp-basics.md) более глубоким погружением в конкретные сервисы.

## Полезные ссылки
- [Google Cloud Documentation](https://cloud.google.com/docs)
- [Google Cloud Free Tier](https://cloud.google.com/free)
- [Google Cloud Pricing Calculator](https://cloud.google.com/products/calculator)
- [Google Cloud Architecture Center](https://cloud.google.com/architecture)
- [Cloud SDK Documentation](https://cloud.google.com/sdk/docs)

## Содержание

- [Compute Services](#compute-services)
  - [Compute Engine](#compute-engine)
  - [Google Kubernetes Engine (GKE)](#google-kubernetes-engine-gke)
  - [Cloud Run](#cloud-run)
  - [Cloud Functions](#cloud-functions)
- [Storage Services](#storage-services)
  - [Cloud Storage](#cloud-storage)
  - [Bigtable](#bigtable)
- [Database Services](#database-services)
  - [Cloud SQL](#cloud-sql)
  - [Firestore](#firestore)
  - [BigQuery](#bigquery)
- [Networking & Content Delivery](#networking-content-delivery)
  - [Cloud Load Balancing](#cloud-load-balancing)
  - [Cloud CDN](#cloud-cdn)
- [Security Services](#security-services)
  - [Cloud Identity-Aware Proxy (IAP)](#cloud-identity-aware-proxy-iap)
  - [Cloud Armor](#cloud-armor)
  - [Binary Authorization](#binary-authorization)
- [Analytics & Big Data](#analytics-big-data)
  - [BigQuery](#bigquery-1)
  - [Dataflow](#dataflow)
  - [Pub/Sub](#pubsub)
- [Machine Learning & AI](#machine-learning-ai)
  - [Vertex AI](#vertex-ai)
  - [AI Platform Prediction](#ai-platform-prediction)
- [Developer Tools](#developer-tools)
  - [Cloud Build](#cloud-build)
  - [Cloud Source Repositories](#cloud-source-repositories)
- [Management & Governance](#management-governance)
  - [Cloud Asset Inventory](#cloud-asset-inventory)
  - [Resource Manager](#resource-manager)
  - [Cloud Scheduler](#cloud-scheduler)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Compute Services

### Compute Engine

**Что это:** Виртуальные машины в облаке **GCP**.

**Machine `Types` и их назначение:**

Пример типов машин **GCP** и назначения (**YAML**).
```yaml
# Типы машин GCP и назначение — general-purpose, compute-optimized, memory-optimized и др.
machine-types:
  # Типы машин: general-purpose, compute-optimized, memory-optimized и др.
  general-purpose:
    types: "N1, N2, N2D, E2"
    use-case: "Web servers, databases, development"
    cpu-memory: "Balanced ratio"
    features: "Sustained use discounts"

  compute-optimized:
    types: "C2, C2D"
    use-case: "High-performance computing, gaming"
    cpu-memory: "High CPU-to-memory ratio"
    features: "Up to 60 vCPUs"

  memory-optimized:
    types: "M1, M2"
    use-case: "Large in-memory databases, SAP HANA"
    cpu-memory: "High memory-to-CPU ratio"
    features: "Up to 12 TB RAM"

  accelerator-optimized:
    types: "A2, A3"
    use-case: "ML training, HPC, graphics rendering"
    gpu: "A100, H100 GPUs"
    features: "High bandwidth networking"

  storage-optimized:
    types: "Z3"
    use-case: "Data-intensive workloads"
    storage: "Up to 9 TB local SSD"
    features: "High IOPS"
```

**Instance `Groups` и `Auto Scaling`:**
```yaml
# Managed Instance Group
managed-instance-group:
  name: "mig-web-prod"
  zone: "us-central1-a"
  instance_template: "template-web-app"
  target_size: 3
  auto_healing_policies:
    - health_check: "projects/my-project/global/healthChecks/http-basic-check"
      initial_delay_sec: 300
  update_policy:
    type: "PROACTIVE"
    minimal_action: "REPLACE"
    max_surge:
      fixed: 1
    max_unavailable:
      fixed: 1
    replacement_method: "SUBSTITUTE"

# Autoscaling Policy
autoscaling-policy:
  name: "autoscaling-web"
  target: "https://www.googleapis.com/compute/v1/projects/my-project/zones/us-central1-a/instanceGroupManagers/mig-web-prod"
  autoscaling_policy:
    min_num_replicas: 2
    max_num_replicas: 10
    cool_down_period_sec: 60
    cpu_utilization:
      utilization_target: 0.6
    load_balancing_utilization:
      utilization_target: 0.8
    custom_metric_utilizations:
      - metric: "custom.googleapis.com/myapp/queue_depth"
        utilization_target: 0.7
        utilization_target_type: "GAUGE"
```

**Preemptible VMs для cost optimization:**
```yaml
# Preemptible VM Instance
preemptible-vm:
  name: "vm-batch-processing"
  machine_type: "n1-standard-4"
  scheduling:
    preemptible: true
    on_host_maintenance: "TERMINATE"
    automatic_restart: false
  disks:
    - boot: true
      initialize_params:
        image: "projects/ubuntu-os-cloud/global/images/family/ubuntu-2004-lts"
        disk_size_gb: 50
  metadata:
    shutdown-script: |
      #!/bin/bash
      # Save work before shutdown
      gsutil cp /tmp/results gs://my-results-bucket/

# Spot VM (новое название для Preemptible)
spot-vm:
  name: "vm-ml-training"
  machine_type: "a2-highgpu-1g"
  scheduling:
    provisioning_model: "SPOT"
    instance_termination_action: "STOP"
    on_host_maintenance: "TERMINATE"
  guest_accelerators:
    - accelerator_type: "nvidia-tesla-a100"
      accelerator_count: 1
```

### Google Kubernetes Engine (**GKE**)

**Что это:** Управляемый **Kubernetes** сервис.

**GKE `Autopilot`:**
```yaml
# GKE Autopilot Cluster
gke-autopilot-cluster:
  name: "gke-autopilot-prod"
  location: "us-central1"
  enable_autopilot: true
  network: "projects/my-project/global/networks/vpc-prod"
  subnetwork: "projects/my-project/regions/us-central1/subnetworks/subnet-app"
  ip_allocation_policy:
    cluster_secondary_range_name: "pods"
    services_secondary_range_name: "services"
  private_cluster_config:
    enable_private_nodes: true
    enable_private_endpoint: false
    master_ipv4_cidr_block: "172.16.0.32/28"
  master_authorized_networks_config:
    enabled: true
    cidr_blocks:
      - cidr_block: "10.0.0.0/8"
        display_name: "VPC Network"
      - cidr_block: "192.168.0.0/16"
        display_name: "Office Network"
  release_channel:
    channel: "REGULAR"
  addons_config:
    http_load_balancing:
      disabled: false
    horizontal_pod_autoscaling:
      disabled: false
    gcp_filestore_csi_driver_config:
    enabled: true
  database_encryption:
    state: "ENCRYPTED"
    key_name: "projects/my-project/locations/us-central1/keyRings/my-keyring/cryptoKeys/my-key"
```

**GKE `Standard` с advanced features:**
```yaml
# GKE Standard Cluster with Advanced Features
gke-standard-cluster:
  name: "gke-standard-prod"
  location: "us-central1"
  initial_cluster_version: "1.24.5-gke.600"
  network: "projects/my-project/global/networks/vpc-prod"
  subnetwork: "projects/my-project/regions/us-central1/subnetworks/subnet-app"
  enable_kubernetes_alpha: false
  logging_service: "logging.googleapis.com/kubernetes"
  monitoring_service: "monitoring.googleapis.com/kubernetes"
  ip_allocation_policy:
    cluster_secondary_range_name: "pods"
    services_secondary_range_name: "services"
  private_cluster_config:
    enable_private_nodes: true
    enable_private_endpoint: false
    master_ipv4_cidr_block: "172.16.0.32/28"
  binary_authorization:
    evaluation_mode: "PROJECT_SINGLETON_POLICY_ENFORCE"
  network_policy:
      enabled: true
    provider: "CALICO"
  maintenance_policy:
    window:
      recurring_window:
        window:
          start_time: "2023-01-01T02:00:00Z"
          end_time: "2023-01-01T06:00:00Z"
        recurrence: "FREQ=WEEKLY;BYDAY=SA"
  node_pools:
    - name: "system-pool"
      initial_node_count: 3
      config:
        machine_type: "e2-medium"
        disk_size_gb: 100
        disk_type: "pd-standard"
        oauth_scopes:
          - "https://www.googleapis.com/auth/cloud-platform"
        service_account: "gke-system@my-project.iam.gserviceaccount.com"
        metadata:
          disable-legacy-endpoints: "true"
        labels:
          node-pool: "system"
        tags: ["gke-system"]
      management:
        auto_repair: true
        auto_upgrade: true
      upgrade_settings:
        max_surge: 1
        max_unavailable: 0
      autoscaling:
        enabled: true
        min_node_count: 3
        max_node_count: 5
```

### Cloud Run

**Что это: Serverless** контейнеры без управления инфраструктурой.

**Cloud `Run Service`:**
```yaml
# Cloud Run Service
cloud-run-service:
  name: "api-service"
  location: "us-central1"
metadata:
    annotations:
      run.googleapis.com/ingress: "all"
      run.googleapis.com/ingress-status: "all"
spec:
  template:
    metadata:
        annotations:
          autoscaling.knative.dev/maxScale: "100"
          run.googleapis.com/cpu-throttling: "false"
          run.googleapis.com/execution-environment: "gen2"
    spec:
      containers:
          - image: "gcr.io/my-project/api-service:v1.2.3"
          ports:
            - containerPort: 8080
                name: "http1"
          env:
              - name: "DATABASE_URL"
                value: "postgresql://..."
              - name: "REDIS_URL"
                value: "redis://..."
          resources:
            limits:
                cpu: "1000m"
                memory: "1Gi"
            startupProbe:
            httpGet:
                path: "/health"
              port: 8080
              initialDelaySeconds: 10
              timeoutSeconds: 5
              failureThreshold: 3
            livenessProbe:
            httpGet:
                path: "/health"
              port: 8080
              initialDelaySeconds: 60
              timeoutSeconds: 5
              periodSeconds: 30
        timeoutSeconds: 900
        serviceAccountName: "api-service@my-project.iam.gserviceaccount.com"
    traffic:
      - percent: 100
        latestRevision: true
```

**Cloud `Run Jobs`:**
```yaml
# Cloud Run Job
cloud-run-job:
  name: "batch-processing-job"
  location: "us-central1"
metadata:
    annotations:
      run.googleapis.com/execution-environment: "gen2"
spec:
    template:
spec:
        template:
          spec:
            containers:
              - image: "gcr.io/my-project/batch-processor:v1.0.0"
                env:
                  - name: "INPUT_BUCKET"
                    value: "gs://input-data-bucket"
                  - name: "OUTPUT_BUCKET"
                    value: "gs://processed-data-bucket"
                resources:
                  limits:
                    cpu: "2000m"
                    memory: "4Gi"
                command: ["python", "main.py"]
            timeoutSeconds: 3600
            serviceAccountName: "batch-job@my-project.iam.gserviceaccount.com"
            maxRetries: 3
```

### Cloud Functions

**Что это: Serverless** функции для **event-driven** приложений.

**HTTP `Functions`:**
```python
# HTTP Cloud Function
import functions_framework
from flask import jsonify
import os

@functions_framework.http
def process_request(request):
    """HTTP Cloud Function that processes requests.
    Args:
        request (flask.Request): The request object.
    Returns:
        The response text, or any set of values that can be turned into a
        Response object using `make_response`.
    """
    request_json = request.get_json(silent=True)
    request_args = request.args

    # Get parameters
    name = request_json.get('name') if request_json else request_args.get('name', 'World')

    # Process request
    result = {
        'message': f'Hello {name}!',
        'timestamp': os.environ.get('FUNCTION_EXECUTION_TIME'),
        'version': os.environ.get('K_REVISION')
    }

    return jsonify(result), 200
```

**Background `Functions`:**
```python
# Background Cloud Function for Cloud Storage
import functions_framework
from google.cloud import storage
import logging

@functions_framework.cloud_event
def process_file(cloud_event):
    """Cloud Function triggered by Cloud Storage events.
    Args:
        cloud_event: The CloudEvent object.
    """
    data = cloud_event.data

    bucket_name = data['bucket']
    file_name = data['name']
    event_type = data['eventType']

    logging.info(f'Processing file: {file_name} in bucket: {bucket_name}')

    # Process the file
    client = storage.Client()
    bucket = client.bucket(bucket_name)
    blob = bucket.blob(file_name)

    # Example: Process CSV file
    if file_name.endswith('.csv'):
    content = blob.download_as_text()
        lines = content.split('\n')
        processed_count = len([line for line in lines if line.strip()])

        logging.info(f'Processed {processed_count} lines from {file_name}')
    else:
        logging.info(f'Ignoring non-CSV file: {file_name}')
```

**Event-driven `Functions`:**
```python
# Cloud Function triggered by Pub/Sub
import functions_framework
import base64
import json
from google.cloud import bigquery

@functions_framework.cloud_event
def process_pubsub_message(cloud_event):
    """Cloud Function triggered by Pub/Sub messages.
    Args:
        cloud_event: The CloudEvent object.
    """
    # Decode the Pub/Sub message
    pubsub_message = base64.b64decode(cloud_event.data['message']['data']).decode('utf-8')
    message_data = json.loads(pubsub_message)

    logging.info(f'Received message: {message_data}')

    # Process the message (example: insert into BigQuery)
    client = bigquery.Client()
    table_id = 'my-project.my_dataset.events'

    rows_to_insert = [{
        'event_id': message_data['id'],
        'event_type': message_data['type'],
        'timestamp': message_data['timestamp'],
        'data': json.dumps(message_data['payload'])
    }]

    errors = client.insert_rows_json(table_id, rows_to_insert)
    if errors:
        logging.error(f'Failed to insert rows: {errors}')
    else:
        logging.info('Successfully inserted rows into BigQuery')
```

## Storage Services

### Cloud Storage

**Что это:** Объектное хранилище для неструктурированных данных.

**Advanced `Bucket Configuration`:**
```yaml
# Cloud Storage Bucket with Advanced Features
storage-bucket:
  name: "gs://my-prod-bucket"
  location: "us-central1"
  storage_class: "STANDARD"
  versioning:
    enabled: true
  cors:
    - origin: ["https://myapp.com"]
      method: ["GET", "POST", "PUT"]
      responseHeader: ["Content-Type", "Authorization"]
      maxAgeSeconds: 3600
  logging:
    logBucket: "gs://my-access-logs-bucket"
    logObjectPrefix: "access_logs/"
  website:
    mainPageSuffix: "index.html"
    notFoundPage: "404.html"
  iam_configuration:
    uniformBucketLevelAccess:
      enabled: true
  encryption:
    defaultKmsKeyName: "projects/my-project/locations/us-central1/keyRings/my-keyring/cryptoKeys/my-key"
  retentionPolicy:
    retentionPeriod: 2592000  # 30 days
  lifecycle:
    rule:
      - action:
          type: "SetStorageClass"
          storageClass: "NEARLINE"
        condition:
          age: 30
          matchesStorageClass: ["STANDARD"]
      - action:
          type: "SetStorageClass"
          storageClass: "COLDLINE"
        condition:
          age: 90
          matchesStorageClass: ["NEARLINE"]
      - action:
          type: "Delete"
        condition:
          age: 365
          matchesStorageClass: ["COLDLINE"]
  labels:
    environment: "production"
    application: "web-app"
    backup: "enabled"
```

**Signed URLs и `Access Control`:**
```python
# Generate Signed URL for private object access
from google.cloud import storage
import datetime

def generate_signed_url(bucket_name, object_name, expiration_minutes=60):
    """Generate a signed URL for accessing a private object."""
    storage_client = storage.Client()
    bucket = storage_client.bucket(bucket_name)
    blob = bucket.blob(object_name)

    # Generate signed URL
    url = blob.generate_signed_url(
        version="v4",
        expiration=datetime.timedelta(minutes=expiration_minutes),
        method="GET"
    )

    return url

# Usage
signed_url = generate_signed_url("my-private-bucket", "private-file.pdf")
print(f"Signed URL: {signed_url}")
```

**Transfer `Service` для data migration:**
```yaml
# Storage Transfer Service
storage-transfer-job:
  name: "transfer-from-aws-s3"
  description: "Transfer data from AWS S3 to GCS"
  project_id: "my-project"
  transfer_spec:
    aws_s3_data_source:
      bucket_name: "my-aws-bucket"
      aws_access_key:
        access_key_id: "{{aws_access_key}}"
        secret_access_key: "{{aws_secret_key}}"
    gcs_data_sink:
      bucket_name: "my-gcs-bucket"
      path: "transferred-data/"
    object_conditions:
      include_prefixes: ["data/"]
      exclude_prefixes: ["temp/"]
    transfer_options:
      delete_objects_from_source_after_transfer: false
      overwrite_objects_already_existing_in_sink: true
  schedule:
    schedule_start_date:
      year: 2023
      month: 1
      day: 1
    schedule_end_date:
      year: 2023
      month: 12
      day: 31
    start_time_of_day:
      hours: 2
      minutes: 0
      seconds: 0
    repeat_interval: "86400s"  # Daily
  status: "ENABLED"
```

### Bigtable

**Что это: NoSQL** база данных для больших объемов данных.

**Bigtable `Instance`:**
```yaml
# Bigtable Instance
bigtable-instance:
  name: "bigtable-prod"
  display_name: "Production Bigtable Instance"
  type: "PRODUCTION"
  labels:
    environment: "production"
    application: "analytics"

# Bigtable Cluster
bigtable-cluster:
  name: "bigtable-cluster-a"
  location: "us-central1-a"
  serve_nodes: 3
  default_storage_type: "SSD"
  kms_key_name: "projects/my-project/locations/us-central1/keyRings/my-keyring/cryptoKeys/my-key"
```

**Bigtable `Schema Design`:**
```python
# Bigtable Schema Design
from google.cloud import bigtable
import datetime

def create_table_with_schema():
    """Create Bigtable table with optimized schema."""
    client = bigtable.Client(project='my-project', admin=True)
    instance = client.instance('bigtable-prod')
    table = instance.table('user-analytics')

    # Define column families
    column_family_profile = table.column_family('profile')
    column_family_events = table.column_family('events')
    column_family_metrics = table.column_family('metrics')

    # Configure column families
    column_family_profile.max_versions = 1
    column_family_profile.gc_rule = bigtable.column_family.MaxAgeGC(30 * 24 * 60 * 60)  # 30 days

    column_family_events.max_versions = 100
    column_family_events.gc_rule = bigtable.column_family.MaxAgeGC(7 * 24 * 60 * 60)   # 7 days

    column_family_metrics.max_versions = 1
    column_family_metrics.gc_rule = bigtable.column_family.UnionGC([
        bigtable.column_family.MaxAgeGC(365 * 24 * 60 * 60),  # 1 year
        bigtable.column_family.MaxVersionsGC(10)
    ])

    # Create table
    table.create()

    return table

def insert_user_data(table, user_id, profile_data, events):
    """Insert user data into Bigtable."""
    row_key = f"user#{user_id}"

    row = table.direct_row(row_key)

    # Profile data (static)
    row.set_cell('profile', 'name', profile_data['name'])
    row.set_cell('profile', 'email', profile_data['email'])
    row.set_cell('profile', 'created_at', str(datetime.datetime.now()))

    # Events data (time series)
    for event in events:
        timestamp = event['timestamp']
        row.set_cell('events', f"{event['type']}#{timestamp}",
                    event['data'], timestamp=timestamp)

    # Metrics (aggregated)
    row.set_cell('metrics', 'total_events', str(len(events)))
    row.set_cell('metrics', 'last_activity', str(datetime.datetime.now()))

    row.commit()

    print(f"Inserted data for user: {user_id}")
```

## Database Services

### Cloud SQL

**Что это:** Управляемые реляционные базы данных.

**Cloud `SQL Instance`:**
```yaml
# Cloud SQL for PostgreSQL
cloud-sql-postgres:
  name: "postgres-prod"
  database_version: "POSTGRES_14"
  region: "us-central1"
  settings:
    tier: "db-custom-4-16384"
    disk_size: 100
    disk_type: "PD_SSD"
    availability_type: "REGIONAL"
    backup_configuration:
      enabled: true
      start_time: "03:00"
      location: "us-central1"
      transaction_log_retention_days: 7
      retained_backups: 30
    maintenance_window:
      day: 7
      hour: 2
      update_track: "stable"
    database_flags:
      - name: "max_connections"
        value: "1000"
      - name: "shared_preload_libraries"
        value: "pg_stat_statements"
      - name: "pg_stat_statements.track"
        value: "all"
    ip_configuration:
      ipv4_enabled: false
      private_network: "projects/my-project/global/networks/vpc-prod"
      authorized_networks: []
    location_preference:
      zone: "us-central1-a"
      secondary_zone: "us-central1-b"
    insights_config:
      query_insights_enabled: true
      record_application_tags: true
      record_client_address: true
```

**High `Availability Setup`:**
```yaml
# Cloud SQL with High Availability
cloud-sql-ha:
  name: "mysql-ha-prod"
  database_version: "MYSQL_8_0"
  region: "us-central1"
  settings:
    tier: "db-n1-standard-4"
    disk_size: 500
    disk_type: "PD_SSD"
    availability_type: "REGIONAL"
    backup_configuration:
      enabled: true
      start_time: "02:00"
      location: "us-central1"
      binary_log_enabled: true
      enabled_automated_backups: true
    data_disk_size_gb: 500
    data_disk_type: "PD_SSD"
    maintenance_window:
      day: 6
      hour: 23
      update_track: "stable"
    replication_type: "SYNCHRONOUS"
    ip_configuration:
      ipv4_enabled: false
      private_network: "projects/my-project/global/networks/vpc-prod"
    database_flags:
      - name: "innodb_buffer_pool_size"
        value: "4294967296"
      - name: "max_connections"
        value: "4000"
```

### Firestore

**Что это: NoSQL** документная база данных.

**Firestore `Database`:**
```yaml
# Firestore Database
firestore-database:
  name: "projects/my-project/databases/(default)"
  location_id: "nam5"
  type: "FIRESTORE_NATIVE"
  concurrency_mode: "OPTIMISTIC"
  app_engine_integration_mode: "DISABLED"
  key_prefix: "s"
  delete_protection_state: "DELETE_PROTECTION_ENABLED"
  point_in_time_recovery_enablement: "POINT_IN_TIME_RECOVERY_ENABLED"
  earliest_version_time: "2023-01-01T00:00:00Z"
```

**Firestore `Security Rules`:**
```javascript
// Firestore Security Rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read and write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Products are publicly readable but only admins can write
    match /products/{productId} {
      allow read: if true;
      allow write: if request.auth != null &&
                       request.auth.token.admin == true;
    }

    // Orders: users can read/write their own, admins can read all
    match /orders/{orderId} {
      allow read, write: if request.auth != null &&
                             (request.auth.uid == resource.data.userId ||
                              request.auth.token.admin == true);
    }

    // Cart is private to the user
    match /carts/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Analytics data - only for authenticated users with analytics role
    match /analytics/{document=} {
      allow read: if request.auth != null &&
                      request.auth.token.analytics == true;
    }
  }
}
```

### BigQuery

**Что это: Data warehouse** для аналитики больших данных.

**BigQuery `Dataset`:**
```sql
-- Create Dataset
CREATE SCHEMA `my-project.analytics_prod`
OPTIONS (
  location = 'us-central1',
  labels = [('environment', 'production'), ('team', 'data')]
);

-- Create Partitioned Table
CREATE TABLE `my-project.analytics_prod.user_events`
(
  event_id STRING,
  user_id INT64,
  event_type STRING,
  event_data JSON,
  event_timestamp TIMESTAMP,
  user_properties STRUCT<
    device STRING,
    os STRING,
    browser STRING,
    country STRING
  >
)
PARTITION BY DATE(event_timestamp)
CLUSTER BY user_id, event_type
OPTIONS (
  description = 'User events table for analytics',
  labels = [('table_type', 'events'), ('retention', '365d')]
);

-- Create External Table
CREATE EXTERNAL TABLE `my-project.analytics_prod.raw_logs`
(
  timestamp TIMESTAMP,
  level STRING,
  message STRING,
  service STRING,
  user_id INT64
)
OPTIONS (
  format = 'CSV',
  uris = ['gs://my-logs-bucket/logs/year=*/month=*/day=*/*.csv'],
  max_staleness = INTERVAL 1 HOUR
);
```

**BigQuery `ML`:**
```sql
-- Create ML Model for User Churn Prediction
CREATE OR REPLACE MODEL `my-project.analytics_prod.churn_model`
OPTIONS (
  model_type = 'logistic_reg',
  labels = ['churned']
) AS
SELECT
  user_id,
  days_since_last_login,
  total_orders,
  avg_order_value,
  support_tickets,
  days_since_registration,
  churned
FROM `my-project.analytics_prod.user_features`
WHERE split = 'train';

-- Make Predictions
SELECT
  user_id,
  predicted_churned,
  predicted_churned_probs[OFFSET(1)] as churn_probability
FROM ML.PREDICT(
  MODEL `my-project.analytics_prod.churn_model`,
  (
    SELECT
      user_id,
      days_since_last_login,
      total_orders,
      avg_order_value,
      support_tickets,
      days_since_registration
    FROM `my-project.analytics_prod.user_features`
    WHERE split = 'test'
  )
)
ORDER BY churn_probability DESC;
```

## Networking & **Content Delivery**

### Cloud Load Balancing

**Что это:** Глобальный **load balancer** с **SSL termination**.

**Global `HTTP`(S) `Load Balancer`:**
```yaml
# Global HTTP(S) Load Balancer
global-load-balancer:
  name: "glb-prod"
  load_balancing_scheme: "EXTERNAL_MANAGED"

  # Backend Services
  backend_services:
    - name: "backend-web"
      protocol: "HTTP"
      port_name: "http"
      timeout_sec: 30
      backends:
        - group: "projects/my-project/zones/us-central1-a/instanceGroups/ig-web-a"
          balancing_mode: "UTILIZATION"
          max_utilization: 0.8
        - group: "projects/my-project/zones/us-central1-b/instanceGroups/ig-web-b"
          balancing_mode: "UTILIZATION"
          max_utilization: 0.8
      health_checks:
        - name: "http-health-check"
          http_health_check:
            port_specification: "USE_SERVING_PORT"
            request_path: "/health"
          check_interval_sec: 5
          timeout_sec: 5
          healthy_threshold: 2
          unhealthy_threshold: 2

    - name: "backend-api"
      protocol: "HTTP"
      port_name: "http"
      timeout_sec: 60
      backends:
        - group: "projects/my-project/regions/us-central1/networkEndpointGroups/neg-api"
          balancing_mode: "RATE"
          max_rate_per_endpoint: 100

  # URL Maps
  url_maps:
    - name: "url-map-prod"
      default_service: "projects/my-project/global/backendServices/backend-web"
      host_rules:
        - hosts: ["api.myapp.com"]
          path_matcher: "api-matcher"
        - hosts: ["myapp.com", "www.myapp.com"]
          path_matcher: "web-matcher"
      path_matchers:
        - name: "api-matcher"
          default_service: "projects/my-project/global/backendServices/backend-api"
          path_rules:
            - paths: ["/v1/*", "/v2/*"]
              service: "projects/my-project/global/backendServices/backend-api-v1"
            - paths: ["/v3/*"]
              service: "projects/my-project/global/backendServices/backend-api-v3"

  # SSL Certificates
  ssl_certificates:
    - name: "ssl-cert-prod"
      managed:
        domains:
          - "myapp.com"
          - "www.myapp.com"
          - "api.myapp.com"

  # Target HTTPS Proxies
  target_https_proxies:
    - name: "https-proxy-prod"
      url_map: "projects/my-project/global/urlMaps/url-map-prod"
      ssl_certificates:
        - "projects/my-project/global/sslCertificates/ssl-cert-prod"
      ssl_policy: "projects/my-project/global/sslPolicies/modern-ssl-policy"

  # Global Addresses
  global_addresses:
    - name: "lb-ip-prod"
      address_type: "EXTERNAL"
      ip_version: "IPV4"

  # Forwarding Rules
  forwarding_rules:
    - name: "https-forwarding-rule"
      ip_protocol: "TCP"
      port_range: "443"
      target: "projects/my-project/global/targetHttpsProxies/https-proxy-prod"
      ip_address: "projects/my-project/global/addresses/lb-ip-prod"
      load_balancing_scheme: "EXTERNAL_MANAGED"
```

### Cloud CDN

**Что это: Content Delivery Network** для быстрой доставки контента.

**Cloud `CDN Configuration`:**
```yaml
# Cloud CDN Backend Bucket
backend-bucket:
  name: "backend-bucket-static"
  bucket_name: "my-static-assets-bucket"
  description: "Backend bucket for static assets"
  enable_cdn: true
  cdn_policy:
    cache_mode: "CACHE_ALL_STATIC"
    client_ttl: 3600
    default_ttl: 86400
    max_ttl: 86400
    negative_caching: true
    negative_caching_policy:
      code: 404
        ttl: 300
    cache_key_policy:
      include_host: true
      include_protocol: true
      include_query_string: true
      query_string_whitelist: ["v", "version"]
    signed_url_cache_max_age_sec: 7200

# Cloud CDN Backend Service
backend-service-cdn:
  name: "backend-service-cdn"
  enable_cdn: true
  cdn_policy:
    cache_mode: "USE_ORIGIN_HEADERS"
    client_ttl: 3600
    default_ttl: 3600
    max_ttl: 86400
    negative_caching: false
    cache_key_policy:
      include_host: true
      include_protocol: true
      include_query_string: false
      include_http_headers: ["Authorization"]
    signed_url_cache_max_age_sec: 0
    request_coalescing: true
    serve_while_stale: 86400
```

## Security Services

### Cloud Identity-Aware Proxy (**IAP**)

**Что это:** Контроль доступа к веб-приложениям.

**IAP `Configuration`:**
```yaml
# IAP Brand
iap-brand:
  name: "projects/my-project/brands/my-brand"
  support_email: "admin@mycompany.com"
  application_title: "My Application"

# IAP Client
iap-client:
  name: "projects/my-project/brands/my-brand/identityAwareProxyClients/my-client"
  display_name: "Web Client"
  secret: "{{iap-client-secret}}"

# Backend Service with IAP
backend-service-iap:
  name: "backend-service-web"
  iap:
    enabled: true
    oauth2_client_id: "projects/my-project/brands/my-brand/identityAwareProxyClients/my-client"
    oauth2_client_secret: "projects/my-project/secrets/iap-client-secret/versions/latest"
    oauth2_client_secret_sha256: "{{sha256-hash}}"
```

**IAP `Access Policies`:**
```yaml
# IAP Access Policy
iap-access-policy:
  name: "projects/my-project/iap_web/compute/services/default"
  access_settings:
    gcip_settings:
      tenant_ids: ["my-tenant-id"]
    cors_settings:
      allow_http_options: true
    oauth_settings:
      login_hint: "admin@mycompany.com"
    reauth_settings:
      method: "SECURE_KEY"
      maximum_age: "7200s"
      policy_type: "MINIMUM"
```

### Cloud Armor

**Что это: Web Application Firewall** (**WAF**) для **GCP**.

**Cloud Armor Security Policy:**
```yaml
# Cloud Armor Security Policy
security-policy:
  name: "security-policy-prod"
  description: "Security policy for production web application"
  type: "CLOUD_ARMOR"
  rules:
    - priority: 1000
      description: "Deny SQL injection attempts"
      match:
        expr:
          expression: "evaluatePreconfiguredExpr('sqli-stable')"
      action: "deny"
      preview: false

    - priority: 1001
      description: "Deny XSS attempts"
      match:
        expr:
          expression: "evaluatePreconfiguredExpr('xss-stable')"
      action: "deny"
      preview: false

    - priority: 1002
      description: "Rate limit API calls"
      match:
        expr:
          expression: "request.path.matches('/api/.*')"
      action: "throttle"
      rate_limit_options:
        conform_action: "allow"
        exceed_action: "deny"
        rate_limit_threshold:
          count: 100
          interval_sec: 60
      preview: false

    - priority: 1003
      description: "Block bad IP addresses"
      match:
        expr:
          expression: "inIpRange(origin.ip, '10.0.0.0/8')"
      action: "deny"
      preview: false

    - priority: 2147483647
      description: "Default allow rule"
      match:
        expr:
          expression: "true"
      action: "allow"
      preview: false

# Adaptive Protection
adaptive-protection:
  enabled: true
  layer7_ddos_defense_config:
    enable: true
    rule_visibility: "STANDARD"
```

### Binary Authorization

**Что это:** Контроль развертывания контейнеров.

**Binary `Authorization Policy`:**
```yaml
# Binary Authorization Policy
binary-authorization-policy:
  name: "projects/my-project/policy"
  description: "Binary Authorization policy for production"
  global_policy_evaluation_mode: "ENABLE"
  admission_whitelist_patterns:
    - name_pattern: "gcr.io/google_containers/*"
    - name_pattern: "gcr.io/google-containers/*"
    - name_pattern: "k8s.gcr.io/*"
  cluster_admission_rules:
    us-central1:
      gke-prod-cluster:
        evaluation_mode: "REQUIRE_ATTESTATION"
        enforcement_mode: "ENFORCED_BLOCK_AND_AUDIT_LOG"
        require_attestations_by:
          - "projects/my-project/attestors/build-attestor"
          - "projects/my-project/attestors/security-attestor"
  default_admission_rule:
    evaluation_mode: "REQUIRE_ATTESTATION"
    enforcement_mode: "ENFORCED_BLOCK_AND_AUDIT_LOG"
    require_attestations_by:
      - "projects/my-project/attestors/build-attestor"
```

**Attestor `Configuration`:**
```yaml
# Binary Authorization Attestor
attestor:
  name: "projects/my-project/attestors/build-attestor"
  description: "Attestor for build verification"
  user_owned_drydock_note:
    note_reference: "projects/my-project/notes/build-note"
    public_keys:
      - id: "build-key"
        pkix_public_key:
          public_key_pem: |
            -----BEGIN PUBLIC KEY-----
            MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...
            -----END PUBLIC KEY-----
          signature_algorithm: "RSA_PSS_4K"
```

## Analytics & **Big Data**

### BigQuery

**Что это: Serverless data warehouse**.

**BigQuery `Optimization`:**
```sql
-- Partitioned Table with Clustering
CREATE TABLE `my-project.analytics_prod.user_sessions`
(
  session_id STRING,
  user_id INT64,
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  device_type STRING,
  country STRING,
  page_views INT64,
  session_duration INT64
)
PARTITION BY DATE(start_time)
CLUSTER BY country, device_type
OPTIONS (
  description = "User sessions table with partitioning and clustering",
  labels = [('table_type', 'sessions'), ('retention', '365d')]
);

-- Materialized View for Performance
CREATE MATERIALIZED VIEW `my-project.analytics_prod.daily_user_metrics`
OPTIONS (
  enable_refresh = true,
  refresh_interval_minutes = 60
) AS
SELECT
  DATE(start_time) as date,
  country,
  device_type,
  COUNT(DISTINCT user_id) as unique_users,
  COUNT(*) as total_sessions,
  AVG(session_duration) as avg_session_duration,
  SUM(page_views) as total_page_views
FROM `my-project.analytics_prod.user_sessions`
WHERE start_time >= '2023-01-01'
GROUP BY date, country, device_type;

-- Authorized View for Data Sharing
CREATE VIEW `my-project.analytics_prod.public_user_metrics`
OPTIONS (
  description = "Public view of user metrics (aggregated)",
  labels = [('access', 'public')]
) AS
SELECT
  date,
  country,
  COUNT(*) as user_count
FROM `my-project.analytics_prod.daily_user_metrics`
GROUP BY date, country;
```

### Dataflow

**Что это: Managed service** для **stream** и **batch processing**.

**Dataflow `Pipeline`:**
```python
# Apache Beam Pipeline for Dataflow
import apache_beam as beam
from apache_beam.options.pipeline_options import PipelineOptions
from apache_beam.io.gcp.bigquery import WriteToBigQuery

def run_pipeline():
    """Dataflow pipeline for processing log data."""

    # Pipeline options
    options = PipelineOptions(
        project='my-project',
        region='us-central1',
        staging_location='gs://my-dataflow-staging/staging',
        temp_location='gs://my-dataflow-staging/temp',
        runner='DataflowRunner',
        job_name='log-processing-pipeline'
    )

    with beam.Pipeline(options=options) as p:
        # Read from Pub/Sub
        logs = (
            p
            | 'Read from Pub/Sub' >> beam.io.ReadFromPubSub(
                subscription='projects/my-project/subscriptions/log-subscription'
            )
            | 'Parse JSON' >> beam.Map(parse_log_entry)
            | 'Filter Errors' >> beam.Filter(lambda log: log['level'] == 'ERROR')
            | 'Add Timestamp' >> beam.Map(add_processing_timestamp)
            | 'Write to BigQuery' >> WriteToBigQuery(
                table='my-project:logs.error_logs',
                schema='timestamp:TIMESTAMP,level:STRING,message:STRING,service:STRING',
                create_disposition=beam.io.BigQueryDisposition.CREATE_IF_NEEDED,
                write_disposition=beam.io.BigQueryDisposition.WRITE_APPEND
            )
        )

def parse_log_entry(message):
    """Parse JSON log entry."""
    import json
    data = json.loads(message)
    return {
        'timestamp': data['timestamp'],
        'level': data['level'],
        'message': data['message'],
        'service': data['service']
    }

def add_processing_timestamp(log_entry):
    """Add processing timestamp."""
    from datetime import datetime
    log_entry['processed_at'] = datetime.utcnow().isoformat()
    return log_entry

if __name__ == '__main__':
    run_pipeline()
```

### Pub/**Sub**

**Что это: Messaging service** для **event-driven** архитектур.

**Pub/`Sub Topics` и `Subscriptions`:**
```yaml
# Pub/Sub Topic
pubsub-topic:
  name: "projects/my-project/topics/user-events"
  labels:
    environment: "production"
    team: "backend"

# Push Subscription
push-subscription:
  name: "projects/my-project/subscriptions/user-events-push"
  topic: "projects/my-project/topics/user-events"
  push_config:
    push_endpoint: "https://my-endpoint.com/pubsub/push"
    oidc_token:
      service_account_email: "pubsub@my-project.iam.gserviceaccount.com"
    attributes:
      x-goog-version: "v1"
  ack_deadline_seconds: 60
  message_retention_duration: "604800s"  # 7 days
  retain_acked_messages: false
  expiration_policy:
    ttl: "2678400s"  # 31 days

# Pull Subscription
pull-subscription:
  name: "projects/my-project/subscriptions/user-events-pull"
  topic: "projects/my-project/topics/user-events"
  ack_deadline_seconds: 600
  message_retention_duration: "86400s"  # 1 day
  retain_acked_messages: true
  expiration_policy:
    ttl: "2592000s"  # 30 days
  dead_letter_policy:
    dead_letter_topic: "projects/my-project/topics/user-events-dlq"
    max_delivery_attempts: 5
```

## Machine Learning & `AI`

### Vertex `AI`

**Что это: Unified** `ML` **platform**.

**AutoML `Model Training`:**
```python
# Vertex AI AutoML Training
from google.cloud import aiplatform

def train_automl_model():
    """Train an AutoML model using Vertex AI."""

    # Initialize Vertex AI
    aiplatform.init(project='my-project', location='us-central1')

    # Define training job
    dataset = aiplatform.TabularDataset.create(
        display_name='user-churn-dataset',
        gcs_source='gs://my-bucket/training-data/user_churn.csv'
    )

    job = aiplatform.AutoMLTabularTrainingJob(
        display_name='user-churn-automl',
        optimization_prediction_type='classification',
        column_specs={
            'days_since_last_login': 'numeric',
            'total_orders': 'numeric',
            'support_tickets': 'numeric',
            'account_age_days': 'numeric',
            'churned': 'categorical'
        }
    )

    # Train model
    model = job.run(
        dataset=dataset,
        target_column='churned',
        training_fraction_split=0.8,
        validation_fraction_split=0.1,
        test_fraction_split=0.1,
        budget_milli_node_hours=1000,
        disable_early_stopping=False
    )

    return model
```

**Custom `Training Job`:**
```python
# Custom Training with Vertex AI
from google.cloud import aiplatform
from google.cloud.aiplatform import custom_job

def run_custom_training():
    """Run custom training job on Vertex AI."""

    # Define custom job
    job = custom_job.CustomJob(
        display_name='custom-churn-model',
        worker_pool_specs=[
            {
                'machine_spec': {
                    'machine_type': 'n1-standard-4',
                    'accelerator_type': aiplatform.gapic.AcceleratorType.NVIDIA_TESLA_T4,
                    'accelerator_count': 1,
                },
                'replica_count': 1,
                'container_spec': {
                    'image_uri': 'gcr.io/my-project/churn-trainer:latest',
                    'command': ['python', 'train.py'],
                    'args': [
                        '--input-path', 'gs://my-bucket/training-data/',
                        '--output-path', 'gs://my-bucket/models/',
                        '--experiment-name', 'churn-prediction-v2'
                    ],
                },
            }
        ],
        base_output_dir='gs://my-bucket/training-output'
    )

    # Run job
    job.run(sync=True)

    # Get trained model
    model = aiplatform.Model.upload(
        display_name='custom-churn-model',
        artifact_uri='gs://my-bucket/models/',
        serving_container_image_uri='gcr.io/my-project/churn-predictor:latest'
    )

    return model
```

### `AI` **Platform Prediction**

**Что это: Serverless model serving**.

**Model `Deployment`:**
```python
# Deploy Model to AI Platform
from google.cloud import aiplatform

def deploy_model():
    """Deploy trained model for prediction."""

    aiplatform.init(project='my-project', location='us-central1')

    # Get existing model
    model = aiplatform.Model.list(filter='display_name="churn-model"')[0]

    # Create endpoint
    endpoint = aiplatform.Endpoint.create(
        display_name='churn-prediction-endpoint',
        location='us-central1'
    )

    # Deploy model to endpoint
    deployed_model = endpoint.deploy(
        model=model,
        deployed_model_display_name='churn-model-v1',
        machine_type='n1-standard-2',
        min_replica_count=1,
        max_replica_count=3,
        accelerator_type=None,
        accelerator_count=0
    )

    return endpoint, deployed_model
```

## Developer Tools

### Cloud Build

**Что это:** CI/CD **platform** на **GCP**.

**Cloud `Build Configuration`:**
```yaml
# cloudbuild.yaml
steps:
  # Build application
  - name: 'gcr.io/cloud-builders/docker'
    args: ['build', '-t', 'gcr.io/$PROJECT_ID/my-app:$COMMIT_SHA', '.']

  # Run tests
  - name: 'gcr.io/cloud-builders/docker'
    args: ['run', '--rm', 'gcr.io/$PROJECT_ID/my-app:$COMMIT_SHA', 'npm', 'test']

  # Push to Container Registry
  - name: 'gcr.io/cloud-builders/docker'
    args: ['push', 'gcr.io/$PROJECT_ID/my-app:$COMMIT_SHA']

  # Deploy to Cloud Run
  - name: 'gcr.io/google.com/cloudsdktool/cloud-sdk'
    entrypoint: gcloud
    args:
      - 'run'
      - 'deploy'
      - 'my-app-service'
      - '--image'
      - 'gcr.io/$PROJECT_ID/my-app:$COMMIT_SHA'
      - '--region'
      - 'us-central1'
      - '--platform'
      - 'managed'
      - '--allow-unauthenticated'

  # Update traffic
  - name: 'gcr.io/google.com/cloudsdktool/cloud-sdk'
    entrypoint: gcloud
    args:
      - 'run'
      - 'services'
      - 'update-traffic'
      - 'my-app-service'
      - '--to-latest'
      - '--platform'
      - 'managed'
      - '--region'
      - 'us-central1'

options:
  logging: CLOUD_LOGGING_ONLY
  machineType: 'E2_HIGHCPU_8'

timeout: '1200s'

# Build triggers
triggers:
  - name: 'push-to-main'
    description: 'Build on push to main branch'
    github:
      owner: 'myorg'
      name: 'my-app'
      push:
        branch: '^main$'
    substitutions:
      _SERVICE_NAME: 'my-app-prod'
```

**Advanced Cloud Build Patterns:**
```yaml
# Multi-stage build with caching
steps:
  # Cache dependencies
  - name: 'gcr.io/cloud-builders/go'
    args: ['mod', 'download']
    id: 'download-dependencies'

  # Build application
  - name: 'gcr.io/cloud-builders/go'
    args: ['build', '-o', 'app', './cmd/server']
    id: 'build-app'
    waitFor: ['download-dependencies']

  # Run tests
  - name: 'gcr.io/cloud-builders/go'
    args: ['test', './...']
    id: 'run-tests'
    waitFor: ['download-dependencies']

  # Build container
  - name: 'gcr.io/cloud-builders/docker'
    args: ['build', '-t', 'gcr.io/$PROJECT_ID/my-app:$COMMIT_SHA', '.']
    id: 'build-container'
    waitFor: ['build-app', 'run-tests']

  # Security scan
  - name: 'gcr.io/cloud-builders/gcloud'
    args:
      - 'beta'
      - 'container'
      - 'images'
      - 'describe'
      - 'gcr.io/$PROJECT_ID/my-app:$COMMIT_SHA'
      - '--format=value(scan.vulnerabilitySummary)'
    id: 'security-scan'
    waitFor: ['build-container']

# Build substitutions
substitutions:
  _NODE_ENV: 'production'
  _BUILD_NUMBER: '$BUILD_ID'

# Secrets
availableSecrets:
  secretManager:
    - versionName: 'projects/$PROJECT_ID/secrets/npm-token/versions/latest'
      env: 'NPM_TOKEN'

# Artifacts
artifacts:
  objects:
    location: 'gs://my-artifacts/$PROJECT_ID/$BUILD_ID/'
    paths: ['dist/', 'coverage/']
```

### Cloud Source Repositories

**Что это: Private Git repositories** в **GCP**.

**Repository `Configuration`:**
```yaml
# Cloud Source Repository
source-repository:
  name: "projects/my-project/repos/my-app"
  url: "https://source.developers.google.com/p/my-project/r/my-app"

# Repository Settings
repository-config:
  mirrors:
    - url: "https://github.com/myorg/my-app.git"
      name: "github-mirror"
      push: true
      pull: true

  triggers:
    - name: "build-on-push"
      description: "Trigger build on push to main"
      trigger_template:
        branch_name: "main"
        project_id: "my-project"
        repo_name: "my-app"
      filename: "cloudbuild.yaml"
      substitutions:
        _SERVICE_NAME: "my-app-prod"

  permissions:
    - role: "roles/source.writer"
      members:
        - "user:developer@mycompany.com"
        - "serviceAccount:build-service@my-project.iam.gserviceaccount.com"
```

## Management & **Governance**

### Cloud Asset Inventory

**Что это: Inventory** и **search** для **GCP resources**.

**Asset `Search`:**
```bash
# Search for resources
gcloud asset search-all-resources \
  --scope="projects/my-project" \
  --query="name:vm-web*" \
  --format="table(name, assetType, location)"

# Find unused resources
gcloud asset search-all-resources \
  --scope="projects/my-project" \
  --query="state:TERMINATED" \
  --asset-types="compute.googleapis.com/Instance"

# Search by labels
gcloud asset search-all-resources \
  --scope="projects/my-project" \
  --query="labels.environment=production AND labels.team=backend"
```

**Resource `Monitoring`:**
```yaml
# Export assets to BigQuery
gcloud asset export \
  --project=my-project \
  --output-bigquery-table="projects/my-project/datasets/asset_inventory/tables/daily_export" \
  --bigquery-table-partition-key="snapshot_date" \
  --content-type="resource"

# Monitor resource changes
gcloud asset feeds create my-feed \
  --project=my-project \
  --pubsub-topic="projects/my-project/topics/asset-changes" \
  --asset-types="compute.googleapis.com/Instance" \
  --condition="state != 'TERMINATED'"
```

### Resource Manager

**Что это: Organization** и **project management**.

**Organization `Policies`:**
```yaml
# Organization Policy Constraints
organization-policies:
  # Disable VM serial console
  - constraint: "constraints/compute.disableSerialPortAccess"
    enforcement: true

  # Restrict VM instance types
  - constraint: "constraints/compute.vmExternalIpAccess"
    enforcement: true
    values:
      - "projects/my-project"

  # Require OS Login
  - constraint: "constraints/compute.requireOsLogin"
    enforcement: true

  # Restrict storage locations
  - constraint: "constraints/gcp.resourceLocations"
    enforcement: true
    values:
      - "in:us-locations"

  # Require VPC-SC
  - constraint: "constraints/compute.requireVpcSecureConnect"
    enforcement: true
```

**Project `Liens`:**
```yaml
# Create project lien
gcloud alpha resource-manager liens create \
  --project=my-project \
  --restrictions="resourcemanager.projects.delete" \
  --reason="Production project - deletion protection" \
  --origin="user:admin@mycompany.com"

# List liens
gcloud alpha resource-manager liens list --project=my-project

# Delete lien
gcloud alpha resource-manager liens delete LIEN_ID --project=my-project
```

### Cloud Scheduler

**Что это: Managed cron job service**.

**Scheduled `Jobs`:**
```yaml
# Cloud Scheduler Job
scheduler-job:
  name: "projects/my-project/locations/us-central1/jobs/daily-backup"
  description: "Daily database backup job"
  schedule: "0 2 * * *"  # Daily at 2 AM
  time_zone: "America/New_York"
  http_target:
    uri: "https://my-app.com/api/backup"
    http_method: "POST"
    headers:
      Content-Type: "application/json"
      Authorization: "Bearer {{access-token}}"
    body: |
      {
        "database": "production",
        "type": "full"
      }
    oauth_token:
      service_account_email: "backup-service@my-project.iam.gserviceaccount.com"
      scope: "https://www.googleapis.com/auth/cloud-platform"

# Pub/Sub target
scheduler-pubsub-job:
  name: "projects/my-project/locations/us-central1/jobs/process-queue"
  description: "Process message queue every 15 minutes"
  schedule: "*/15 * * * *"
  time_zone: "UTC"
  pubsub_target:
    topic_name: "projects/my-project/topics/process-queue"
    data: "eyJtZXNzYWdlIjoiUHJvY2VzcyBxdWV1ZSJ9"  # Base64 encoded JSON
    attributes:
      priority: "high"
      source: "scheduler"
```

## Лучшие практики

- **Выбор сервисов:** используйте **managed**-сервисы (**Cloud SQL**, **Firestore**, **GKE**, **Cloud Run**) для снижения операционной нагрузки; **Compute Engine** — при необходимости полного контроля или специфичного ПО.
- **Безопасность:** минимальные права **IAM** по ролям и проектам; **Service Accounts** вместо ключей; шифрование по умолчанию; **Secret Manager** для секретов.
- **Стоимость:** коммитированные скидки (**Committed Use Discounts**) для стабильных нагрузок; **Preemptible**/**Spot** для **fault-tolerant**; теги и бюджеты для контроля затрат.
- **Надёжность:** мультизонное размещение для критичных БД и приложений; автоскейлинг и **health checks**; регулярные снимки и тесты восстановления.
- **Мониторинг: Cloud Monitoring** (**метрики, алерты, Uptime Checks**); **Cloud Trace** и **Profiler**; централизованные логи в **Logging** и интеграция с **BigQuery**.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [AWS Basics](aws-basics.md) — основы **AWS**
- [Azure Basics](azure-basics.md) — основы **Azure**
- [GCP Basics](gcp-basics.md) — основы **GCP**
- [Terraform](../iac/terraform/terraform-basics.md) — **Infrastructure as Code**
- [Kubernetes Advanced](../containers/kubernetes/kubernetes-advanced.md) — контейнерная оркестрация