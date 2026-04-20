---
title: "GCP Basics"
description: "Google Cloud Platform (GCP) — набор облачных сервисов от Google, предоставляющий вычислительные ресурсы, хранение данных, машинное обучение и другие сервисы через глобальную сеть дата-центров. Этот документ охватывает фундаментальные концепции GCP, архитектуру платформы, модели р"
tags:
  - platform
  - cloud-providers
  - gcp-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# GCP Basics

**Google Cloud Platform** (GCP) — набор облачных сервисов от **Google**, предоставляющий вычислительные ресурсы, хранение данных, машинное обучение и другие сервисы через глобальную сеть дата-центров. Этот документ охватывает фундаментальные концепции **GCP**, архитектуру платформы, модели развертывания и основные сервисы для начинающих пользователей.

## Полезные ссылки
- [Google Cloud Documentation](https://cloud.google.com/docs)
- [Google Cloud Free Tier](https://cloud.google.com/free)
- [Google Cloud Pricing Calculator](https://cloud.google.com/products/calculator)
- [Google Cloud Architecture Center](https://cloud.google.com/architecture)
- [Cloud SDK Documentation](https://cloud.google.com/sdk/docs)

## Содержание

- [Основы GCP](#основы-gcp)
  - [GCP Architecture](#gcp-architecture)
  - [Service Models](#service-models)
  - [Deployment Models](#deployment-models)
- [GCP Regions и Zones](#gcp-regions-и-zones)
  - [GCP Regions](#gcp-regions)
  - [Zones и High Availability](#zones-и-high-availability)
  - [SLA (Service Level Agreement)](#sla-service-level-agreement)
- [Projects и Organizations](#projects-и-organizations)
  - [GCP Projects](#gcp-projects)
  - [Organizations](#organizations)
- [Identity и Access Management](#identity-и-access-management)
  - [Google Cloud IAM](#google-cloud-iam)
  - [Service Accounts](#service-accounts)
  - [Workload Identity Federation](#workload-identity-federation)
- [Networking Basics](#networking-basics)
  - [Virtual Private Cloud (VPC)](#virtual-private-cloud-vpc)
  - [Firewall Rules](#firewall-rules)
  - [Load Balancing](#load-balancing)
- [Storage Basics](#storage-basics)
  - [Cloud Storage](#cloud-storage)
  - [Persistent Disk](#persistent-disk)
- [Compute Basics](#compute-basics)
  - [Compute Engine](#compute-engine)
  - [Managed Instance Groups](#managed-instance-groups)
  - [Google Kubernetes Engine (GKE)](#google-kubernetes-engine-gke)
  - [Cloud Functions](#cloud-functions)
- [Cloud SDK и CLI](#cloud-sdk-и-cli)
  - [gcloud CLI](#gcloud-cli)
  - [Terraform with GCP](#terraform-with-gcp)
- [Monitoring и Logging](#monitoring-и-logging)
  - [Cloud Monitoring](#cloud-monitoring)
  - [Cloud Logging](#cloud-logging)
- [Billing и Cost Management](#billing-и-cost-management)
  - [GCP Pricing Models](#gcp-pricing-models)
  - [Cost Management Tools](#cost-management-tools)
  - [Resource Labels](#resource-labels)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Основы GCP

### GCP Architecture

Схема глобальной инфраструктуры **Google Cloud Platform** (регионы, зоны, категории сервисов).

```mermaid
flowchart TD
    GCP["Google Cloud Platform Global Infrastructure"]
    GCP --> Geo["Geography (География)"]
    Geo --> AM["Americas (Америки)"]
    Geo --> EU["Europe (Европа)"]
    Geo --> AP["Asia Pacific (Азиатско-Тихоокеанский регион)"]
    Geo --> MEA["Middle East & Africa (Ближний Восток и Африка)"]

    GCP --> Region["Region (Регион) — us-central1"]
    Region --> Zones["Zones (Зоны) — us-central1-a, us-central1-b, us-central1-c"]
    Region --> RS["Regional Services (Региональные сервисы)"]
    Region --> ZS["Zonal Services (Зональные сервисы)"]

    GCP --> Cat["GCP Services Categories"]
    Cat --> Compute["Compute (Вычисления)"]
    Cat --> Storage["Storage (Хранение)"]
    Cat --> Networking["Networking (Сеть)"]
    Cat --> DB["Databases (Базы данных)"]
    Cat --> AI["AI & Machine Learning"]
    Cat --> IoT["IoT & Edge"]
    Cat --> Sec["Security & Identity"]
    Cat --> DevOps["DevOps"]
    Cat --> Analytics["Analytics"]
    Cat --> MG["Management & Governance"]
```

### Service Models
```yaml
# Infrastructure as a Service (IaaS)
iaas-services:
  description: "Полный контроль над инфраструктурой"
  examples:
    - Compute Engine (VMs)
    - Persistent Disk
    - VPC Networks
  responsibility:
    - application: customer
    - data: customer
    - runtime: customer
    - middleware: customer
    - os: customer
    - virtualization: google
    - servers: google
    - storage: google
    - networking: google

# Platform as a Service (PaaS)
paas-services:
  description: "Фокус на приложении, GCP управляет инфраструктурой"
  examples:
    - App Engine
    - Cloud Functions
    - Cloud Run
    - Firebase
  responsibility:
    - application: customer
    - data: customer
    - runtime: customer
    - middleware: google
    - os: google
    - virtualization: google
    - servers: google
    - storage: google
    - networking: google

# Software as a Service (SaaS)
saas-services:
  description: "Готовое приложение"
  examples:
    - Gmail
    - Google Docs
    - Google Drive
    - BigQuery BI Engine
  responsibility:
    - application: google
    - data: customer
    - runtime: google
    - middleware: google
    - os: google
    - virtualization: google
    - servers: google
    - storage: google
    - networking: google
```

### Deployment Models
```yaml
# Public Cloud
public-cloud:
  description: "Сервисы предоставляются через публичный интернет"
  advantages:
    - global_reach: true
    - massive_scale: true
    - pay_as_you_go: true
  use_cases:
    - web_applications
    - data_analytics
    - machine_learning

# Private Cloud
private-cloud:
  description: "Инфраструктура развернута локально"
  advantages:
    - data_sovereignty: true
    - compliance: "строгое"
    - low_latency: true
  use_cases:
    - regulated_industries
    - air_gapped_networks
    - legacy_systems

# Hybrid Cloud
hybrid-cloud:
  description: "Комбинация public и private cloud"
  advantages:
    - flexibility: true
    - cost_optimization: true
    - seamless_integration: true
  use_cases:
    - lift_and_shift_migration
    - edge_computing
    - disaster_recovery
```

## GCP Regions и Zones

### GCP Regions
```yaml
# Popular GCP Regions
gcp-regions:
  - name: "us-central1"
    location: "Council Bluffs, Iowa, USA"
    zones: ["us-central1-a", "us-central1-b", "us-central1-c", "us-central1-f"]
    features: "Low latency to US"

  - name: "europe-west1"
    location: "St. Ghislain, Belgium"
    zones: ["europe-west1-b", "europe-west1-c", "europe-west1-d"]
    features: "EU data residency"

  - name: "asia-southeast1"
    location: "Jurong West, Singapore"
    zones: ["asia-southeast1-a", "asia-southeast1-b", "asia-southeast1-c"]
    features: "Asia Pacific coverage"

  - name: "australia-southeast1"
    location: "Sydney, Australia"
    zones: ["australia-southeast1-a", "australia-southeast1-b", "australia-southeast1-c"]
    features: "Oceania coverage"

# Region Selection Criteria
region-selection:
  compliance:
    - data_residency_requirements
    - regulatory_compliance
    - industry_standards

  performance:
    - user_proximity
    - network_latency
    - inter_service_latency

  cost:
    - regional_pricing
    - data_transfer_costs
    - available_services

  features:
    - service_availability
    - regional_features
    - carbon_neutral_regions
```

### Zones и High Availability
```yaml
# Zone Architecture
availability-zones:
  zone_a:
    name: "us-central1-a"
    datacenter: "Primary DC A"
    power_grid: "Grid Alpha"
    network_segment: "Segment A"

  zone_b:
    name: "us-central1-b"
    datacenter: "Primary DC B"
    power_grid: "Grid Beta"
    network_segment: "Segment B"

  zone_c:
    name: "us-central1-c"
    datacenter: "Primary DC C"
    power_grid: "Grid Gamma"
    network_segment: "Segment C"

# Zonal Services (Zone-specific)
zonal-services:
  - compute_engine_vms
  - persistent_disks
  - local_ssd
  - gpu_instances

# Regional Services (Region-wide)
regional-services:
  - regional_managed_instance_groups
  - regional_disks
  - cloud_sql_read_replicas
  - memorystore_redis

# Global Services (Global scope)
global-services:
  - cloud_storage
  - cloud_dns
  - cloud_load_balancing
  - cloud_cdn

# Multi-regional Services
multi-regional-services:
  - bigquery_datasets
  - cloud_spanner
  - cloud_firestore
```

### SLA (`Service Level Agreement`)
```yaml
# GCP SLA Examples
service-slas:
  compute_engine:
    single_instance: "99.9%"
    managed_instance_groups: "99.99%"
    regional_managed_instance_groups: "99.95%"

  cloud_storage:
    multi_regional: "99.95%"
    regional: "99.9%"
    nearline: "99%"
    coldline: "99%"

  bigquery:
    storage: "99.9%"
    queries: "99.9%"

  cloud_functions:
    execution_time: "99.5%"
    error_rate: "99.5%"

# SLA Calculation
sla-calculation:
  serial_services: "SLA1 × SLA2 × SLA3"
  parallel_services: "1 - ((1-SLA1) × (1-SLA2) × (1-SLA3))"
  composite_sla: "MIN(SLA1, SLA2, SLA3)"
```

## Projects и Organizations

### GCP Projects
```yaml
# Project Structure
gcp-project:
  name: "my-production-project"
  project_id: "my-prod-project-12345"
  project_number: "123456789012"
  labels:
    environment: "production"
    application: "ecommerce"
    owner: "platform-team"
    cost-center: "cc-12345"

  apis:
    - compute.googleapis.com
    - storage.googleapis.com
    - bigquery.googleapis.com
    - monitoring.googleapis.com

  quotas:
    - metric: "compute.googleapis.com/cpus"
      limit: 100
      usage: 45

  billing_account: "01A23B-456C78-9D012E"

# Project Best Practices
project-best-practices:
  naming:
    pattern: "{environment}-{application}-{team}"
    examples:
      - "prod-ecommerce-platform"
      - "dev-analytics-data"
      - "staging-mobile-app"

  organization:
    by_environment: "Separate dev/staging/prod"
    by_team: "Team-specific projects"
    by_application: "Application boundaries"
    by_regulatory: "Compliance boundaries"

  limits:
    apis_per_project: 50
    projects_per_organization: 4000
    resources_per_project: 10000
```

### Organizations
```yaml
# Organization Structure
organization:
  name: "My Company"
  organization_id: "123456789012"
  owner:
    directory_customer_id: "C01234567"
    domain: "mycompany.com"

  folders:
    - name: "Production"
      folder_id: "folders/123456789012"
      parent: "organizations/123456789012"

    - name: "Development"
      folder_id: "folders/987654321098"
      parent: "organizations/123456789012"

  projects:
    - name: "prod-app"
      parent: "folders/123456789012"
    - name: "dev-app"
      parent: "folders/987654321098"

# Organization Policies
organization-policies:
  constraints:
    - constraint: "constraints/compute.disableSerialPortAccess"
      enforcement: true
    - constraint: "constraints/storage.uniformBucketLevelAccess"
      enforcement: true
    - constraint: "constraints/iam.allowedPolicyMemberDomains"
      enforcement: true
      values:
        - "mycompany.com"

  boolean_policies:
    - constraint: "constraints/compute.skipDefaultNetworkCreation"
      enforcement: true
```

## Identity и Access Management

### Google Cloud IAM
```yaml
# IAM Roles Hierarchy
iam-hierarchy:
  organization_level:
    - Organization Administrator
    - Organization Policy Administrator
    - Billing Account Administrator

  folder_level:
    - Folder Admin
    - Folder IAM Admin
    - Folder Viewer

  project_level:
    - Project Owner
    - Project Editor
    - Project Viewer

  resource_level:
    - Compute Admin
    - Storage Admin
    - Network Admin

# Custom Role Definition
custom-role:
  name: "roles/myCompany.databaseAdmin"
      title: "Database Administrator"
  description: "Can manage databases and perform backups"
      permissions:
        - "cloudsql.instances.connect"
    - "cloudsql.instances.create"
    - "cloudsql.instances.delete"
    - "cloudsql.instances.update"
    - "cloudsql.databases.create"
    - "cloudsql.databases.delete"
        - "cloudsql.databases.update"
    - "cloudsql.backupRuns.create"
    - "cloudsql.backupRuns.delete"
  stage: "GA"
```

### Service Accounts
```yaml
# Service Account
service-account:
  name: "my-service@my-project.iam.gserviceaccount.com"
  display_name: "My Application Service Account"
  description: "Service account for web application"
  disabled: false

  roles:
    - role: "roles/storage.objectAdmin"
      condition:
        title: "Restrict to production bucket"
        expression: "resource.name.startsWith('projects/_/buckets/my-prod-bucket')"

# Service Account Keys
service-account-key:
  type: "service_account"
  project_id: "my-project"
  private_key_id: "abcdef123456"
  private_key: "-----BEGIN PRIVATE KEY-----\n..."
  client_email: "my-service@my-project.iam.gserviceaccount.com"
  client_id: "123456789012345678901"
  auth_uri: "https://accounts.google.com/o/oauth2/auth"
  token_uri: "https://oauth2.googleapis.com/token"
  auth_provider_x509_cert_url: "https://www.googleapis.com/oauth2/v1/certs"
```

### Workload Identity Federation
```yaml
# Workload Identity Pool
workload-identity-pool:
  name: "github-actions-pool"
  display_name: "GitHub Actions Pool"
  description: "Identity pool for GitHub Actions"
  disabled: false

  providers:
    - name: "github-provider"
      display_name: "GitHub Provider"
      provider_config:
        oidc:
          issuer_uri: "https://token.actions.githubusercontent.com"
      attribute_mapping:
        google.subject: "assertion.sub"
        attribute.actor: "assertion.actor"
        attribute.repository: "assertion.repository"

# Workload Identity Pool Provider
pool-provider:
  name: "github-provider"
  display_name: "GitHub Provider"
  provider_config:
    oidc:
      issuer_uri: "https://token.actions.githubusercontent.com"
  attribute_mapping:
    google.subject: "assertion.sub"
    attribute.actor: "assertion.actor"
    attribute.repository: "assertion.repository"
  attribute_condition: "assertion.repository == 'myorg/myrepo'"
```

## Networking Basics

### Virtual Private Cloud (VPC)
```yaml
# VPC Network
vpc-network:
  name: "vpc-prod"
  auto_create_subnetworks: false
  routing_mode: "REGIONAL"
  mtu: 1460

  subnets:
    - name: "subnet-app"
      ip_cidr_range: "10.0.1.0/24"
      region: "us-central1"
      private_ip_google_access: true
      secondary_ip_ranges:
        - range_name: "pods"
          ip_cidr_range: "10.1.0.0/16"
        - range_name: "services"
          ip_cidr_range: "10.2.0.0/20"

    - name: "subnet-data"
      ip_cidr_range: "10.0.2.0/24"
      region: "us-central1"
      private_ip_google_access: true

# VPC Peering
vpc-peering:
  name: "peer-dev-to-prod"
  network: "projects/dev-project/global/networks/vpc-dev"
  peer_network: "projects/prod-project/global/networks/vpc-prod"
  export_custom_routes: false
  import_custom_routes: false

# Shared VPC
shared-vpc:
  host_project: "host-project"
  service_projects:
    - "service-project-1"
    - "service-project-2"

  subnets:
    - name: "shared-subnet"
      project: "host-project"
      region: "us-central1"
      ip_cidr_range: "10.0.0.0/24"
      shared_with:
        - project: "service-project-1"
          role: "roles/compute.networkUser"
        - project: "service-project-2"
          role: "roles/compute.networkUser"
```

### Firewall Rules
```yaml
# VPC Firewall Rules
firewall-rules:
  allow-internal:
    name: "fw-allow-internal"
    network: "vpc-prod"
    direction: "INGRESS"
    priority: 65534
    source_ranges: ["10.0.0.0/8"]
    target_tags: ["internal"]
    allowed:
      - ip_protocol: "tcp"
        ports: ["0-65535"]
      - ip_protocol: "udp"
        ports: ["0-65535"]
      - ip_protocol: "icmp"

  allow-ssh:
    name: "fw-allow-ssh"
    network: "vpc-prod"
    direction: "INGRESS"
    priority: 1000
    source_ranges: ["35.235.240.0/20"]  # IAP IP range
    target_tags: ["ssh-access"]
    allowed:
      - ip_protocol: "tcp"
        ports: ["22"]

  allow-http:
    name: "fw-allow-http"
    network: "vpc-prod"
    direction: "INGRESS"
    priority: 1000
    source_ranges: ["0.0.0.0/0"]
    target_tags: ["http-server"]
    allowed:
      - ip_protocol: "tcp"
        ports: ["80"]

  deny-all:
    name: "fw-deny-all"
    network: "vpc-prod"
    direction: "INGRESS"
    priority: 65535
    source_ranges: ["0.0.0.0/0"]
    denied:
      - ip_protocol: "all"
```

### Load Balancing
```yaml
# Global HTTP(S) Load Balancer
http-load-balancer:
  name: "glb-prod"
  load_balancing_scheme: "EXTERNAL_MANAGED"

  backend_services:
    - name: "backend-service"
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

  url_maps:
    - name: "url-map"
      default_service: "projects/my-project/global/backendServices/backend-service"

  target_http_proxies:
    - name: "http-proxy"
      url_map: "projects/my-project/global/urlMaps/url-map"

  forwarding_rules:
    - name: "http-forwarding-rule"
      ip_protocol: "TCP"
      port_range: "80"
      target: "projects/my-project/global/targetHttpProxies/http-proxy"
      load_balancing_scheme: "EXTERNAL_MANAGED"
```

## Storage Basics

### Cloud Storage
```yaml
# Storage Bucket
storage-bucket:
  name: "gs://my-prod-bucket"
  location: "US"
  storage_class: "STANDARD"
  versioning:
        enabled: true
  logging:
    log_bucket: "gs://my-logs-bucket"
    log_object_prefix: "access_logs/"
  lifecycle:
    rule:
      - action:
          type: "SetStorageClass"
          storage_class: "COLDLINE"
        condition:
          age: 365
          matches_storage_class: ["STANDARD"]
      - action:
          type: "Delete"
        condition:
          age: 2555  # 7 years
  iam_configuration:
    uniform_bucket_level_access:
      enabled: true
  encryption:
    default_kms_key_name: "projects/my-project/locations/us/keyRings/my-keyring/cryptoKeys/my-key"

# Storage Classes
storage-classes:
  standard:
    description: "Frequently accessed data"
    availability: "99.99%"
    retrieval_cost: "Free"
    minimum_duration: "None"

  nearline:
    description: "Accessed less than once per month"
    availability: "99.95%"
    retrieval_cost: "$0.01/GB"
    minimum_duration: "30 days"

  coldline:
    description: "Accessed less than once per quarter"
    availability: "99.9%"
    retrieval_cost: "$0.05/GB"
    minimum_duration: "90 days"

  archive:
    description: "Accessed less than once per year"
    availability: "99.9%"
    retrieval_cost: "$0.12/GB"
    minimum_duration: "365 days"
```

### Persistent Disk
```yaml
# Persistent Disk
persistent-disk:
  name: "pd-data"
  size_gb: 100
  type: "pd-ssd"
  zone: "us-central1-a"
  labels:
    environment: "production"
    disk-type: "data"

# Regional Persistent Disk
regional-disk:
  name: "pd-regional-data"
  size_gb: 100
  type: "pd-ssd"
  region: "us-central1"
  replica_zones: ["us-central1-a", "us-central1-b"]

# Disk Snapshot
disk-snapshot:
  name: "snapshot-pd-data-20231201"
  source_disk: "projects/my-project/zones/us-central1-a/disks/pd-data"
  storage_locations: ["us-central1"]
  labels:
    backup-type: "daily"
    environment: "production"
```

## Compute Basics

### Compute Engine
```yaml
# VM Instance
compute-instance:
  name: "vm-web-01"
  zone: "us-central1-a"
  machine_type: "n1-standard-2"
  network_interfaces:
    - network: "projects/my-project/global/networks/vpc-prod"
      subnetwork: "projects/my-project/regions/us-central1/subnetworks/subnet-app"
      access_configs:
        - name: "External NAT"
          type: "ONE_TO_ONE_NAT"
  disks:
    - boot: true
      auto_delete: true
      initialize_params:
        source_image: "projects/ubuntu-os-cloud/global/images/family/ubuntu-2004-lts"
        disk_size_gb: 50
        disk_type: "pd-ssd"
    - device_name: "pd-data"
      source: "projects/my-project/zones/us-central1-a/disks/pd-data"
      auto_delete: false
  metadata:
    startup-script: |
      #!/bin/bash
      apt-get update
      apt-get install -y nginx
      systemctl start nginx
  service_accounts:
    - email: "default"
      scopes: ["https://www.googleapis.com/auth/cloud-platform"]
  tags:
    - "http-server"
    - "web-app"

# Instance Template
instance-template:
  name: "template-web-app"
  properties:
    machine_type: "n1-standard-2"
    network_interfaces:
      - network: "vpc-prod"
        access_configs:
          - name: "External NAT"
            type: "ONE_TO_ONE_NAT"
    disks:
      - device_name: "boot"
        type: "PERSISTENT"
        boot: true
        auto_delete: true
        initialize_params:
          source_image: "projects/ubuntu-os-cloud/global/images/family/ubuntu-2004-lts"
    metadata:
      startup-script-url: "gs://my-bucket/scripts/startup.sh"
    service_accounts:
      - email: "my-service@my-project.iam.gserviceaccount.com"
        scopes: ["https://www.googleapis.com/auth/cloud-platform"]
    tags:
      items: ["http-server", "web-app"]
```

### Managed Instance Groups
```yaml
# Managed Instance Group
managed-instance-group:
  name: "mig-web-app"
  zone: "us-central1-a"
  instance_template: "projects/my-project/global/instanceTemplates/template-web-app"
  target_size: 3
  auto_healing_policies:
    - health_check: "projects/my-project/global/healthChecks/http-health-check"
      initial_delay_sec: 300
  update_policy:
    type: "PROACTIVE"
    minimal_action: "REPLACE"
    max_surge:
      fixed: 1
    max_unavailable:
      fixed: 0
    replacement_method: "RECREATE"

# Regional Managed Instance Group
regional-instance-group:
  name: "mig-regional-web"
  region: "us-central1"
  instance_template: "projects/my-project/global/instanceTemplates/template-web-app"
  target_size: 6
  distribution_policy:
    zones:
      - zone: "us-central1-a"
        target_shape: 50
      - zone: "us-central1-b"
        target_shape: 50
```

### Google Kubernetes Engine (GKE)
```yaml
# GKE Cluster
gke-cluster:
  name: "gke-prod-cluster"
  location: "us-central1"
  initial_cluster_version: "1.24.5-gke.600"
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
  node_pools:
    - name: "default-pool"
      initial_node_count: 3
  config:
        machine_type: "n1-standard-2"
        disk_size_gb: 100
        disk_type: "pd-ssd"
        oauth_scopes:
          - "https://www.googleapis.com/auth/cloud-platform"
        service_account: "gke-service@my-project.iam.gserviceaccount.com"
        metadata:
          disable-legacy-endpoints: "true"
        labels:
          environment: "production"
        tags: ["gke-node"]
      management:
        auto_repair: true
        auto_upgrade: true
      autoscaling:
        enabled: true
        min_node_count: 1
        max_node_count: 10
  addons_config:
    http_load_balancing:
      disabled: false
    horizontal_pod_autoscaling:
      disabled: false
    network_policy_config:
      disabled: false
  network_policy:
    enabled: true
```

### Cloud Functions
```python
# Cloud Function
def hello_world(request):
    """HTTP Cloud Function.
    Args:
        request (flask.Request): The request object.
    Returns:
        The response text, or any set of values that can be turned into a
        Response object using `make_response`.
    """
    request_json = request.get_json(silent=True)
    request_args = request.args

    if request_json and 'name' in request_json:
        name = request_json['name']
    else:
        name = request_args.get('name', 'World')

    return f'Hello {name}!'

# Cloud Function Configuration
cloud-function-config:
  name: "function-hello-world"
  description: "Simple Hello World function"
  runtime: "python39"
  region: "us-central1"
  entry_point: "hello_world"
  source_archive_bucket: "gs://my-functions-bucket"
  source_archive_object: "function-source.zip"
  trigger_http: true
  available_memory_mb: 256
  timeout: "60s"
  max_instances: 100
  service_account_email: "cloud-functions@my-project.iam.gserviceaccount.com"
  environment_variables:
    LOG_LEVEL: "INFO"
    PROJECT_ID: "my-project"
  ingress_settings: "ALLOW_ALL"
```

## Cloud SDK и CLI

### gcloud CLI
```bash
# Authentication
gcloud auth login
gcloud auth activate-service-account --key-file=key.json
gcloud config set project my-project

# Project Management
gcloud projects list
gcloud config set project my-prod-project

# Compute Engine
gcloud compute instances create vm-web-01 \
  --zone=us-central1-a \
  --machine-type=n1-standard-2 \
  --image-family=ubuntu-2004-lts \
  --image-project=ubuntu-os-cloud \
  --boot-disk-size=50GB \
  --boot-disk-type=pd-ssd \
  --network=vpc-prod \
  --subnet=subnet-app \
  --tags=http-server,web-app

gcloud compute instances list --filter="zone:(us-central1-a)"
gcloud compute ssh vm-web-01 --zone=us-central1-a

# Storage
gcloud storage buckets create gs://my-bucket --location=US
gcloud storage cp my-file.txt gs://my-bucket/
gcloud storage ls gs://my-bucket/

# Kubernetes Engine
gcloud container clusters create gke-cluster \
  --zone=us-central1-a \
  --num-nodes=3 \
  --machine-type=n1-standard-2 \
  --network=vpc-prod \
  --subnetwork=subnet-app

gcloud container clusters get-credentials gke-cluster --zone=us-central1-a
kubectl get nodes

# IAM
gcloud iam service-accounts create my-service \
  --display-name="My Service Account"

gcloud projects add-iam-policy-binding my-project \
  --member="serviceAccount:my-service@my-project.iam.gserviceaccount.com" \
  --role="roles/storage.objectAdmin"
```

### Terraform with GCP
```hcl
# GCP Provider
terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.0"
    }
  }
}

provider "google" {
  project = "my-project"
  region  = "us-central1"
  zone    = "us-central1-a"
}

# VPC Network
resource "google_compute_network" "vpc" {
  name                    = "vpc-prod"
  auto_create_subnetworks = false
}

# Subnet
resource "google_compute_subnetwork" "subnet" {
  name          = "subnet-app"
  network       = google_compute_network.vpc.id
  ip_cidr_range = "10.0.1.0/24"
  region        = "us-central1"
}

# VM Instance
resource "google_compute_instance" "vm" {
  name         = "vm-web-01"
  machine_type = "n1-standard-2"
  zone         = "us-central1-a"

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2004-lts"
      size  = 50
      type  = "pd-ssd"
    }
  }

  network_interface {
    network    = google_compute_network.vpc.id
    subnetwork = google_compute_subnetwork.subnet.id

    access_config {
      // Ephemeral public IP
    }
  }

  metadata_startup_script = <<-EOF
    #!/bin/bash
    apt-get update
    apt-get install -y nginx
    systemctl start nginx
  EOF

  service_account {
    email  = google_service_account.vm.email
    scopes = ["cloud-platform"]
  }

  tags = ["http-server", "web-app"]
}

# Service Account
resource "google_service_account" "vm" {
  account_id   = "vm-service"
  display_name = "VM Service Account"
}
```

## Monitoring и Logging

### Cloud Monitoring
```yaml
# Cloud Monitoring Workspace
monitoring-workspace:
  name: "projects/my-project"
  metrics_scope: "projects/my-project"

# Uptime Check
uptime-check:
  name: "check-web-app"
  display_name: "Web App Uptime Check"
  monitored_resource:
    type: "uptime_url"
    labels:
      host: "myapp.com"
  http_check:
    path: "/health"
    port: 443
    use_ssl: true
    validate_ssl: true
  timeout: "10s"
  period: "300s"

# Alert Policy
alert-policy:
  name: "high-cpu-alert"
  display_name: "High CPU Usage Alert"
  conditions:
    - display_name: "CPU usage > 80%"
      condition_threshold:
        filter: 'metric.type="compute.googleapis.com/instance/cpu/utilization" AND resource.type="gce_instance"'
        aggregations:
          - alignment_period: "300s"
            per_series_aligner: "ALIGN_MEAN"
        comparison: "COMPARISON_GT"
        duration: "300s"
        threshold_value: 0.8
        trigger:
          count: 1
  notification_channels:
    - "projects/my-project/notificationChannels/123456789"
  alert_strategy:
    auto_close: "1800s"
```

### Cloud Logging
```yaml
# Log Sink
log-sink:
  name: "sink-to-storage"
  destination: "storage.googleapis.com/my-logs-bucket"
  filter: |
    resource.type="gce_instance"
    AND severity>=WARNING
  include_children: false

# Log Metric
log-metric:
  name: "error-count"
  description: "Count of error log entries"
  filter: |
    resource.type="gce_instance"
    severity=ERROR
  metric_descriptor:
    metric_kind: "DELTA"
    value_type: "INT64"
    unit: "1"
    labels:
      - key: "instance_name"
        value_type: "STRING"
        description: "Instance name"
  label_extractors:
    instance_name: "EXTRACT(resource.labels.instance_name)"

# Log-based Alert
log-alert:
  name: "error-alert"
  condition:
    title: "Error log threshold exceeded"
    condition:
      condition_matched_log:
        filter: |
          resource.type="gce_instance"
          severity>=ERROR
        label_extractors:
          instance_name: "EXTRACT(resource.labels.instance_name)"
  notification_channels:
    - "projects/my-project/notificationChannels/123456789"
```

## Billing и Cost Management

### GCP Pricing Models
```yaml
# Pricing Models
pricing-models:
  on_demand:
    description: "Pay for what you use, no commitments"
    discount: "None"
    flexibility: "Maximum"

  committed_use_discounts:
    description: "1-3 year commitments for cost savings"
    discount: "Up to 70%"
    cud_types:
      - compute: "VM instances, GPUs"
      - storage: "Persistent disks, filestore"
      - bigquery: "BigQuery slots"

  sustained_use_discounts:
    description: "Automatic discounts for continuous usage"
    discount: "Up to 30%"
    threshold: "25% of month usage"

  preemptible_instances:
    description: "Short-lived instances at discount"
    discount: "Up to 80%"
    max_runtime: "24 hours"
    availability: "Best effort"

  spot_instances:
    description: "Preemptible VMs with longer runtime"
    discount: "Up to 91%"
    max_runtime: "No limit"
    availability: "Best effort"
```

### Cost Management Tools
```yaml
# Budget
budget:
  name: "monthly-budget"
  display_name: "Monthly Cloud Budget"
  budget_filter:
    projects:
      - "projects/my-project"
    credit_types_treatment: "INCLUDE_ALL_CREDITS"
  amount:
    specified_amount:
      currency_code: "USD"
      units: "1000"
    # Or use last period amount
    # last_period_amount: true
  threshold_rules:
    - threshold_percent: 50.0
      spend_basis: "CURRENT_SPEND"
    - threshold_percent: 75.0
      spend_basis: "CURRENT_SPEND"
    - threshold_percent: 90.0
      spend_basis: "CURRENT_SPEND"
    - threshold_percent: 100.0
      spend_basis: "FORECASTED_SPEND"
  all_updates_rule:
    pubsub_topic: "projects/my-project/topics/budget-notifications"
    schema_version: "1.0"

# Cost Export
cost-export:
  name: "daily-cost-export"
  display_name: "Daily Cost Export to BigQuery"
  description: "Export detailed cost data daily"
  content_type: "RESOURCE"
  dataset_destination:
    dataset_id: "cost_data"
    table_prefix: "gcp_billing_export"
  export_frequency: "DAILY"

# Cost Recommendations
cost-recommendations:
  idle_resources:
    - type: "IDLE_VM"
      description: "VM not used for 7+ days"
    - type: "IDLE_DISK"
      description: "Disk not accessed for 30+ days"

  rightsizing:
    - type: "CHANGE_MACHINE_TYPE"
      description: "VM over-provisioned"
    - type: "DELETE_UNUSED_DISK"
      description: "Unused persistent disks"

  commitments:
    - type: "COMMITTED_USE_DISCOUNT"
      description: "Recommend CUD for steady usage"
    - type: "SUSTAINED_USE_DISCOUNT"
      description: "Automatic SUD applied"
```

### Resource Labels
```yaml
# Resource Labeling Strategy
labeling-strategy:
  mandatory_labels:
    - key: "environment"
      values: ["development", "staging", "production"]
      description: "Deployment environment"

    - key: "team"
      values: ["platform", "backend", "frontend", "data"]
      description: "Responsible team"

    - key: "application"
      values: ["web-app", "api", "worker", "database"]
      description: "Application component"

    - key: "cost-center"
      values: ["cc-12345", "cc-67890"]
      description: "Cost allocation"

  optional_labels:
    - key: "project"
      description: "Project identifier"
    - key: "version"
      description: "Application version"
    - key: "owner"
      description: "Contact person"

# Label Inheritance
label-inheritance:
  organization_level:
    labels:
      organization: "mycompany"
      compliance: "soc2"

  folder_level:
    labels:
      business_unit: "ecommerce"
      data_classification: "internal"

  project_level:
    labels:
      environment: "production"
      backup_schedule: "daily"
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [[aws-basics|AWS Basics]] — основы **AWS**
- [[azure-basics|Azure Basics]] — основы **Azure**
- [[gcp-services|GCP Services]] — сервисы **GCP**
- [[terraform-basics|Terraform]] — **Infrastructure as Code**
- [[kubernetes-advanced|Kubernetes Advanced]] — контейнерная оркестрация