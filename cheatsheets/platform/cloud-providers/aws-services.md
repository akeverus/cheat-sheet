---
title: "AWS Services"
description: "AWS (Amazon Web Services) предоставляет более 200 сервисов для различных задач - от вычислений и хранения данных до машинного обучения и IoT. Этот документ охватывает основные сервисы AWS, их назначение, use cases и лучшие практики использования в production средах. Документ допо"
tags:
  - platform
  - cloud-providers
  - aws-services
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# AWS Services

**AWS** (Amazon Web Services) предоставляет более `200` сервисов для различных задач — от вычислений и хранения данных до машинного обучения и **IoT**. Этот документ охватывает основные сервисы **AWS**, их назначение, **use cases** и лучшие практики использования в **production** средах. Документ дополняет [[aws-basics|AWS Basics]] более глубоким погружением в конкретные сервисы.

## Полезные ссылки
- [AWS Services Overview](https://aws.amazon.com/products/)
- [AWS Documentation](https://docs.aws.amazon.com/)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
- [AWS Pricing Calculator](https://calculator.aws/)
- [AWS Free Tier](https://aws.amazon.com/free/)

## Содержание

- [Compute Services](#compute-services)
  - [EC2 (Elastic Compute Cloud)](#ec2-elastic-compute-cloud)
  - [Lambda (Serverless Functions)](#lambda-serverless-functions)
  - [ECS/Fargate (Container Services)](#ecsfargate-container-services)
  - [EKS (Elastic Kubernetes Service)](#eks-elastic-kubernetes-service)
- [Storage Services](#storage-services)
  - [S3 (Simple Storage Service)](#s3-simple-storage-service)
  - [EBS (Elastic Block Store)](#ebs-elastic-block-store)
  - [EFS (Elastic File System)](#efs-elastic-file-system)
- [Database Services](#database-services)
  - [RDS (Relational Database Service)](#rds-relational-database-service)
  - [DynamoDB](#dynamodb)
  - [Aurora (MySQL/PostgreSQL Compatible)](#aurora-mysqlpostgresql-compatible)
- [Networking & Content Delivery](#networking-content-delivery)
  - [VPC (Virtual Private Cloud)](#vpc-virtual-private-cloud)
  - [CloudFront](#cloudfront)
  - [API Gateway](#api-gateway)
- [Security Services](#security-services)
  - [IAM (Identity and Access Management)](#iam-identity-and-access-management)
  - [KMS (Key Management Service)](#kms-key-management-service)
  - [CloudTrail](#cloudtrail)
- [Analytics & Big Data](#analytics-big-data)
  - [Redshift](#redshift)
  - [EMR (Elastic MapReduce)](#emr-elastic-mapreduce)
- [Machine Learning & AI](#machine-learning-ai)
  - [SageMaker](#sagemaker)
  - [Rekognition](#rekognition)
- [Developer Tools](#developer-tools)
  - [CodeCommit](#codecommit)
  - [CodeBuild](#codebuild)
  - [CodePipeline](#codepipeline)
- [Management & Governance](#management-governance)
  - [CloudFormation](#cloudformation)
  - [Systems Manager (SSM)](#systems-manager-ssm)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Compute Services

### EC2 (`Elastic Compute Cloud`)

**Что это:** Виртуальные серверы в облаке **AWS**.

**Основные возможности:**

Пример типов инстансов **EC2** и сценариев использования (YAML).
```yaml
# EC2 Instance Types — типы инстансов и сценарии использования (general-purpose, compute-optimized и др.)
instance-types:
  general-purpose:     # T3, M5, M6g
    use-case: "Web servers, small databases"
    cpu-memory-ratio: "balanced"

  compute-optimized:   # C5, C6g
    use-case: "High-performance computing"
    cpu-memory-ratio: "high-cpu"

  memory-optimized:    # R5, R6g, X1e
    use-case: "In-memory databases, big data"
    cpu-memory-ratio: "high-memory"

  storage-optimized:   # I3, D2
    use-case: "Data warehousing, distributed file systems"
    cpu-memory-ratio: "high-storage"

  accelerated-computing: # P3, G4dn
    use-case: "Machine learning, graphics rendering"
    cpu-memory-ratio: "gpu-enabled"
```

**Launch `Templates` и `Auto Scaling`:**
```yaml
# Launch Template
launch-template:
  name: web-server-template
  image-id: ami-12345678
  instance-type: t3.micro
  key-name: my-key-pair
  security-group-ids:
    - sg-12345678
  user-data: |
    #!/bin/bash
    yum update -y
    yum install -y httpd
    systemctl start httpd
    systemctl enable httpd

---
# Auto Scaling Group
auto-scaling-group:
  name: web-asg
  launch-template:
    id: lt-12345678
    version: $Latest
  min-size: 2
  max-size: 10
  desired-capacity: 4
  availability-zones:
    - us-east-1a
    - us-east-1b
    - us-east-1c
  target-group-arns:
    - arn:aws:elasticloadbalancing:region:account:targetgroup/my-targets/12345678

  # Scaling Policies
  scaling-policies:
    - name: scale-out
      adjustment-type: ChangeInCapacity
      scaling-adjustment: 2
      cooldown: 300
    - name: scale-in
      adjustment-type: ChangeInCapacity
      scaling-adjustment: -1
      cooldown: 300

  # Target Tracking
  target-tracking:
    predefined-metric-type: ASGAverageCPUUtilization
    target-value: 70.0
```

**Spot `Instances` для cost optimization:**
```yaml
# Spot Instance Request
spot-instance-request:
  spot-price: 0.10  # Maximum price per hour
  instance-count: 5
  type: one-time    # or persistent
  launch-specification:
    image-id: ami-12345678
    instance-type: m5.large
    key-name: my-key-pair
    security-group-ids:
      - sg-12345678

# Spot Fleet для mixed instance types
spot-fleet-request:
  iam-fleet-role: arn:aws:iam::account:role/aws-ec2-spot-fleet-role
  allocation-strategy: diversified
  target-capacity: 20
  spot-price: 0.15
  launch-specifications:
    - instance-type: m5.large
      image-id: ami-12345678
      key-name: my-key-pair
      weighted-capacity: 1
    - instance-type: m5.xlarge
      image-id: ami-12345678
      key-name: my-key-pair
      weighted-capacity: 2
```

### Lambda (Serverless Functions)

**Что это: Serverless** вычислительная служба для запуска кода без управления серверами.

**Основные паттерны:**
```python
# Lambda Function для API Gateway
import json
import boto3

def lambda_handler(event, context):
    # API Gateway integration
    if event.get('httpMethod') == 'GET':
        return {
            'statusCode': 200,
            'headers': {
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            },
            'body': json.dumps({'message': 'Hello from Lambda!'})
        }

    # S3 Event processing
    if 'Records' in event:
        for record in event['Records']:
            if record['eventSource'] == 'aws:s3':
                bucket = record['s3']['bucket']['name']
                key = record['s3']['object']['key']
                process_file(bucket, key)

def process_file(bucket, key):
    s3 = boto3.client('s3')
    # Process file logic
    pass
```

**Lambda `Layers` для shared code:**
```yaml
# Lambda Layer
layer:
  name: common-layer
  description: Shared utilities and dependencies
  compatible-runtimes:
    - python3.8
    - python3.9
  content:
    s3-bucket: my-lambda-layers
    s3-key: common-layer.zip

# Function using layer
lambda-function:
  name: my-function
  runtime: python3.9
  layers:
    - arn:aws:lambda:region:account:layer:common-layer:1
  handler: app.lambda_handler
  code:
    s3-bucket: my-functions
    s3-key: function.zip
```

**Step `Functions` для orchestration:**
```yaml
# Step Function definition
step-function:
  name: order-processing
  definition:
    Comment: "Order processing workflow"
    StartAt: ValidateOrder
    States:
      ValidateOrder:
        Type: Task
        Resource: arn:aws:lambda:region:account:function:validate-order
        Next: ProcessPayment
        Catch:
          - ErrorEquals: ["ValidationError"]
            Next: SendErrorNotification

      ProcessPayment:
        Type: Task
        Resource: arn:aws:lambda:region:account:function:process-payment
        Next: UpdateInventory
        Catch:
          - ErrorEquals: ["PaymentError"]
            Next: RefundPayment

      UpdateInventory:
        Type: Task
        Resource: arn:aws:lambda:region:account:function:update-inventory
        Next: SendConfirmation

      SendConfirmation:
        Type: Task
        Resource: arn:aws:lambda:region:account:function:send-confirmation
        End: true

      SendErrorNotification:
        Type: Task
        Resource: arn:aws:lambda:region:account:function:send-error
        End: true

      RefundPayment:
        Type: Task
        Resource: arn:aws:lambda:region:account:function:refund-payment
        Next: SendErrorNotification
```

### ECS/Fargate (Container Services)

**Что это:** Управление контейнеризованными приложениями.

**ECS с `EC2`:**
```yaml
# ECS Cluster
ecs-cluster:
  cluster-name: production-cluster
  capacity-providers:
    - name: ec2-capacity
      auto-scaling-group-provider:
        auto-scaling-group-arn: arn:aws:autoscaling:region:account:autoScalingGroup:auto-scaling-group-id
        managed-scaling:
          status: ENABLED
          target-capacity: 80
          minimum-scaling-step-size: 1
          maximum-scaling-step-size: 10

# Task Definition
task-definition:
  family: web-app
  task-role-arn: arn:aws:iam::account:role/ecs-task-role
  execution-role-arn: arn:aws:iam::account:role/ecs-execution-role
  network-mode: awsvpc
  requires-compatibilities:
    - EC2
    - FARGATE
  cpu: 256
  memory: 512
  container-definitions:
    - name: web
      image: nginx:latest
      essential: true
      port-mappings:
        - container-port: 80
          host-port: 80
          protocol: tcp
      log-configuration:
        log-driver: awslogs
        options:
          awslogs-group: /ecs/web-app
          awslogs-region: us-east-1
          awslogs-stream-prefix: ecs

# Service
ecs-service:
  service-name: web-service
  cluster: production-cluster
  task-definition: web-app:1
  desired-count: 3
  launch-type: EC2
  load-balancers:
    - target-group-arn: arn:aws:elasticloadbalancing:region:account:targetgroup/my-targets/12345678
      container-name: web
      container-port: 80
  network-configuration:
    awsvpc-configuration:
      subnets:
        - subnet-12345
        - subnet-67890
      security-groups:
        - sg-12345678
```

**Fargate для serverless containers:**
```yaml
# Fargate Task Definition
fargate-task:
  family: api-service
  requires-compatibilities:
    - FARGATE
  network-mode: awsvpc
  cpu: 1024    # 1 vCPU
  memory: 2048 # 2 GB
  execution-role-arn: arn:aws:iam::account:role/ecs-execution-role
  container-definitions:
    - name: api
      image: my-api:latest
      essential: true
      port-mappings:
        - container-port: 3000
          protocol: tcp
      environment:
        - name: NODE_ENV
          value: production
      secrets:
        - name: DB_PASSWORD
          value-from: arn:aws:secretsmanager:region:account:secret:db-password
      log-configuration:
        log-driver: awslogs
        options:
          awslogs-group: /ecs/api-service
          awslogs-region: us-east-1
          awslogs-stream-prefix: ecs

# Fargate Service
fargate-service:
  service-name: api-service
  cluster: production-cluster
  task-definition: api-service:1
  desired-count: 2
  launch-type: FARGATE
  platform-version: 1.4.0
  network-configuration:
    awsvpc-configuration:
      subnets:
        - subnet-12345
        - subnet-67890
      security-groups:
        - sg-12345678
      assign-public-ip: ENABLED
```

### EKS (`Elastic Kubernetes Service`)

**Что это:** Управляемый **Kubernetes** сервис.

**EKS `Cluster configuration`:**
```yaml
# EKS Cluster
eks-cluster:
  name: production-cluster
  version: 1.24
  role-arn: arn:aws:iam::account:role/eks-service-role
  resources-vpc-config:
    subnet-ids:
      - subnet-12345
      - subnet-67890
      - subnet-99999
    security-group-ids:
      - sg-12345678

# Node Groups
managed-node-group:
  name: standard-nodes
  cluster-name: production-cluster
  node-role: arn:aws:iam::account:role/eks-node-role
  subnets:
    - subnet-12345
    - subnet-67890
  instance-types:
    - t3.medium
    - t3.large
  scaling-config:
    min-size: 2
    max-size: 10
    desired-size: 4
  update-config:
    max-unavailable-percentage: 25

# Fargate Profile
fargate-profile:
  name: fargate-profile
  cluster-name: production-cluster
  pod-execution-role-arn: arn:aws:iam::account:role/eks-fargate-role
  subnets:
    - subnet-12345
    - subnet-67890
  selectors:
    - namespace: fargate
```

**Kubernetes manifests для `EKS`:**
```yaml
# Deployment в EKS
apiVersion: apps/v1
kind: Deployment
metadata:
  name: web-app
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: web-app
  template:
    metadata:
      labels:
        app: web-app
    spec:
      containers:
        - name: web
          image: my-app:latest
          ports:
            - containerPort: 80
          env:
            - name: AWS_REGION
              value: us-east-1
          resources:
            requests:
              cpu: 100m
              memory: 128Mi
            limits:
              cpu: 500m
              memory: 512Mi
      serviceAccountName: web-app-sa

---
# Service Account с IAM roles
apiVersion: v1
kind: ServiceAccount
metadata:
  name: web-app-sa
  namespace: production
  annotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::account:role/web-app-role

---
# Ingress для ALB
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: web-app-ingress
  namespace: production
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
spec:
  rules:
    - host: myapp.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: web-app-service
                port:
                  number: 80
```

## Storage Services

### S3 (`Simple Storage Service`)

**Что это:** Объектное хранилище для любых данных.

**Bucket policies и permissions:**
```json
// S3 Bucket Policy для публичного чтения
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::my-bucket/*"
    }
  ]
}

// Bucket Policy для CloudFront access
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowCloudFrontServicePrincipal",
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::cloudfront:user/CloudFront Origin Access Identity E123456789"
      },
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::my-bucket/*"
    }
  ]
}
```

**Lifecycle policies:**
```yaml
# S3 Lifecycle Configuration
lifecycle-configuration:
  rules:
    - id: move-to-ia
      status: Enabled
      prefix: logs/
      transitions:
        - days: 30
          storage-class: STANDARD_IA
        - days: 90
          storage-class: GLACIER
        - days: 365
          storage-class: DEEP_ARCHIVE

    - id: delete-temp-files
      status: Enabled
      prefix: temp/
      expiration:
        days: 7

    - id: cleanup-incomplete-mpus
      status: Enabled
      abort-incomplete-multipart-upload:
        days-after-initiation: 7
```

**Static website hosting:**
```yaml
# S3 Static Website Configuration
website-configuration:
  index-document:
    suffix: index.html
  error-document:
    key: error.html
  routing-rules:
    - condition:
        http-error-code-returned-equals: 404
      redirect:
        protocol: https
        host-name: myapp.example.com
        replace-key-prefix-with: "#!"

# CloudFront Distribution для S3
cloudfront-distribution:
  origins:
    - domain-name: my-bucket.s3.amazonaws.com
      id: S3-my-bucket
      s3-origin-config:
        origin-access-identity: ""
  default-cache-behavior:
    target-origin-id: S3-my-bucket
    viewer-protocol-policy: redirect-to-https
    allowed-methods: ["GET", "HEAD", "OPTIONS"]
    cached-methods: ["GET", "HEAD"]
    forwarded-values:
      query-string: false
      cookies:
        forward: none
  enabled: true
  aliases:
    - myapp.example.com
```

### EBS (`Elastic Block Store`)

**Что это:** Блочное хранилище для **EC2 instances**.

**EBS `Volume types`:**
```yaml
# EBS Volume Types
volume-types:
  gp3:  # General Purpose SSD (recommended)
    iops: "3000-16000"
    throughput: "125-1000 MB/s"
    use-case: "Most workloads"
    cost: "$$"

  gp2:  # Previous generation GP
    iops: "100-16000"
    throughput: "128-250 MB/s"
    use-case: "General purpose"
    cost: "$$"

  io1/io2:  # Provisioned IOPS SSD
    iops: "100-64000 (io1), 100-256000 (io2)"
    throughput: "256-1000 MB/s (io1), 256-4000 MB/s (io2)"
    use-case: "High-performance databases"
    cost: "$$$$"

  st1:  # Throughput Optimized HDD
    iops: "500"
    throughput: "500 MB/s"
    use-case: "Big data, data warehouses"
    cost: "$"

  sc1:  # Cold HDD
    iops: "250"
    throughput: "250 MB/s"
    use-case: "Infrequent access"
    cost: "$"
```

**EBS `Snapshots` и backup:**
```yaml
# EBS Snapshot Automation
snapshot-automation:
  - name: daily-backup
    schedule: "cron(0 2 * * ? *)"  # 2 AM daily
    retention: 30
    volumes:
      - vol-12345678
      - vol-87654321

  - name: weekly-backup
    schedule: "cron(0 3 ? * SUN *)"  # 3 AM Sundays
    retention: 52
    volumes:
      - vol-11111111

# AMI creation для golden images
ami-creation:
  name: web-server-ami-{{Date}}
  description: Web server golden image
  instance-id: i-1234567890abcdef0
  no-reboot: true
  tags:
    - key: Environment
      value: production
    - key: Application
      value: web-server
```

### EFS (`Elastic File System`)

**Что это:** Управляемая файловая система для **AWS**.

**EFS для контейнеров:**
```yaml
# EFS для ECS
efs-volume-configuration:
  name: efs-volume
  efs-volume-configuration:
    file-system-id: fs-12345678
    root-directory: /data
    transit-encryption: ENABLED
    authorization-config:
      access-point-id: fsap-12345678
      iam: ENABLED

# Task Definition с EFS
task-definition:
  family: shared-storage-app
  volumes:
    - name: efs-volume
      efs-volume-configuration:
        file-system-id: fs-12345678
        root-directory: /shared
  container-definitions:
    - name: app
      image: my-app:latest
      mount-points:
        - source-volume: efs-volume
          container-path: /app/data
          read-only: false
```

**EFS `Access Points` для security:**
```yaml
# EFS Access Points
access-points:
  - client-token: web-access-point
    root-directory:
      path: /web
      creation-info:
        owner-uid: 1000
        owner-gid: 1000
        permissions: 755
    posix-user:
      uid: 1000
      gid: 1000

  - client-token: api-access-point
    root-directory:
      path: /api
      creation-info:
        owner-uid: 1001
        owner-gid: 1001
        permissions: 755
    posix-user:
      uid: 1001
      gid: 1001
```

## Database Services

### RDS (`Relational Database Service`)

**Что это:** Управляемые реляционные базы данных.

**Multi-AZ deployment:**
```yaml
# RDS Multi-AZ
rds-instance:
  db-instance-identifier: production-db
  db-instance-class: db.r5.large
  engine: mysql
  engine-version: 8.0.28
  master-username: admin
  master-user-password: "{{resolve:ssm:prod-db-password}}"
  db-name: myapp
  allocated-storage: 100
  storage-type: gp2
  multi-az: true
  backup-retention-period: 7
  backup-window: "03:00-04:00"
  maintenance-window: "sun:04:00-sun:05:00"
  vpc-security-group-ids:
    - sg-12345678
  db-subnet-group-name: production-db-subnet-group
  publicly-accessible: false
  storage-encrypted: true
  kms-key-id: alias/aws/rds
```

**Read `Replicas`:**
```yaml
# RDS Read Replica
read-replica:
  db-instance-identifier: production-db-replica
  source-db-instance-identifier: production-db
  db-instance-class: db.r5.large
  availability-zone: us-east-1b
  publicly-accessible: false
  storage-encrypted: true
  backup-retention-period: 0  # No backups for replicas
```

**RDS `Proxy` для connection pooling:**
```yaml
# RDS Proxy
rds-proxy:
  db-proxy-name: production-db-proxy
  engine-family: MYSQL
  auth:
    - auth-scheme: SECRETS
      iam-auth: DISABLED
      secret-arn: arn:aws:secretsmanager:region:account:secret:prod-db-secret
  role-arn: arn:aws:iam::account:role/rds-proxy-role
  vpc-subnet-ids:
    - subnet-12345
    - subnet-67890
  vpc-security-group-ids:
    - sg-12345678
  require-tls: true

# Target Group для Proxy
proxy-target-group:
  db-proxy-name: production-db-proxy
  target-group-name: default
  connection-pool-config:
    max-connections-percent: 100
    max-idle-connections-percent: 50
    connection-borrow-timeout: 120
  db-instance-identifiers:
    - production-db
```

### DynamoDB

**Что это: NoSQL** база данных с **key-value** и **document** моделями.

**Table design:**
```yaml
# DynamoDB Table
dynamodb-table:
  table-name: user-sessions
  attribute-definitions:
    - attribute-name: user_id
      attribute-type: S
    - attribute-name: session_id
      attribute-type: S
    - attribute-name: created_at
      attribute-type: N
  key-schema:
    - attribute-name: user_id
      key-type: HASH
    - attribute-name: session_id
      key-type: RANGE
  global-secondary-indexes:
    - index-name: created_at-index
      key-schema:
        - attribute-name: user_id
          key-type: HASH
        - attribute-name: created_at
          key-type: RANGE
      projection:
        projection-type: ALL
      provisioned-throughput:
        read-capacity-units: 5
        write-capacity-units: 5
  provisioned-throughput:
    read-capacity-units: 10
    write-capacity-units: 10
  stream-specification:
    stream-view-type: NEW_AND_OLD_IMAGES
```

**DynamoDB `Streams` и `Lambda`:**
```python
# Lambda для обработки DynamoDB Streams
import boto3
import json

def lambda_handler(event, context):
    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('user-sessions')

    for record in event['Records']:
        if record['eventName'] == 'INSERT':
            new_item = record['dynamodb']['NewImage']
            user_id = new_item['user_id']['S']

            # Process new session
            print(f"New session for user {user_id}")

        elif record['eventName'] == 'MODIFY':
            old_item = record['dynamodb']['OldImage']
            new_item = record['dynamodb']['NewImage']

            # Process session update
            print(f"Session updated for user {new_item['user_id']['S']}")
```

**Auto `Scaling` для `DynamoDB`:**
```yaml
# DynamoDB Auto Scaling
auto-scaling:
  table-name: user-sessions
  read-capacity:
    min-capacity: 5
    max-capacity: 100
    target-utilization: 70
  write-capacity:
    min-capacity: 5
    max-capacity: 50
    target-utilization: 70

# Global Tables для multi-region
global-table:
  global-table-name: user-sessions-global
  replication-group:
    - region-name: us-east-1
    - region-name: eu-west-1
    - region-name: ap-southeast-1
```

### Aurora (`MySQL/PostgreSQL Compatible`)

**Что это:** Высокопроизводительная реляционная база данных.

**Aurora `Serverless`:**
```yaml
# Aurora Serverless v2
aurora-cluster:
  cluster-identifier: production-aurora
  engine: aurora-mysql
  engine-version: 8.0.mysql_aurora.3.02.0
  master-username: admin
  master-user-password: "{{resolve:ssm:aurora-password}}"
  database-name: myapp
  vpc-security-group-ids:
    - sg-12345678
  db-subnet-group-name: production-db-subnet-group
  serverless-v2:
    min-capacity: 0.5   # 0.5 Aurora Capacity Units (ACUs)
    max-capacity: 16    # 16 ACUs
  backup-retention-period: 7
  preferred-backup-window: "03:00-04:00"
  preferred-maintenance-window: "sun:04:00-sun:05:00"
  storage-encrypted: true
  kms-key-id: alias/aws/rds
```

**Aurora `Global Database`:**
```yaml
# Aurora Global Database
global-cluster:
  global-cluster-identifier: global-aurora-cluster
  source-db-cluster-identifier: production-aurora
  engine: aurora-mysql
  engine-version: 8.0.mysql_aurora.3.02.0

# Secondary cluster в другом регионе
secondary-cluster:
  cluster-identifier: dr-aurora
  global-cluster-identifier: global-aurora-cluster
  engine: aurora-mysql
  engine-version: 8.0.mysql_aurora.3.02.0
  region: us-west-2
```

## Networking & Content Delivery

### VPC (`Virtual Private Cloud`)

**Что это:** Изолированная виртуальная сеть в **AWS**.

**VPC `Design patterns`:**
```yaml
# Multi-tier VPC Architecture
vpc-architecture:
  vpc:
    cidr-block: 10.0.0.0/16
    enable-dns-hostnames: true
    enable-dns-support: true

  subnets:
    public:
      - cidr: 10.0.1.0/24
        az: us-east-1a
        name: public-a
      - cidr: 10.0.2.0/24
        az: us-east-1b
        name: public-b

    private-app:
      - cidr: 10.0.10.0/24
        az: us-east-1a
        name: private-app-a
      - cidr: 10.0.11.0/24
        az: us-east-1b
        name: private-app-b

    private-data:
      - cidr: 10.0.20.0/24
        az: us-east-1a
        name: private-data-a
      - cidr: 10.0.21.0/24
        az: us-east-1b
        name: private-data-b

  gateways:
    internet-gateway:
      name: igw
      vpc-id: vpc-12345678

    nat-gateways:
      - name: nat-a
        subnet-id: subnet-public-a
        allocation-id: eipalloc-12345
      - name: nat-b
        subnet-id: subnet-public-b
        allocation-id: eipalloc-67890

  route-tables:
    public:
      routes:
        - destination-cidr-block: 0.0.0.0/0
          gateway-id: igw-12345678

    private-app:
      routes:
        - destination-cidr-block: 0.0.0.0/0
          nat-gateway-id: nat-12345

    private-data:
      routes:
        - destination-cidr-block: 0.0.0.0/0
          nat-gateway-id: nat-67890
```

**Security `Groups` и NACLs:**
```yaml
# Security Groups
security-groups:
  web-sg:
    name: web-security-group
    description: Security group for web servers
    vpc-id: vpc-12345678
    ingress:
      - from-port: 80
        to-port: 80
        protocol: tcp
        cidr-blocks: ["0.0.0.0/0"]
      - from-port: 443
        to-port: 443
        protocol: tcp
        cidr-blocks: ["0.0.0.0/0"]
      - from-port: 22
        to-port: 22
        protocol: tcp
        cidr-blocks: ["10.0.0.0/8"]
    egress:
      - from-port: 0
        to-port: 0
        protocol: -1
        cidr-blocks: ["0.0.0.0/0"]

  db-sg:
    name: database-security-group
    ingress:
      - from-port: 3306
        to-port: 3306
        protocol: tcp
        security-groups: [web-sg.id]

# Network ACLs
network-acls:
  public-nacl:
    vpc-id: vpc-12345678
    subnet-ids: [subnet-public-a, subnet-public-b]
    ingress:
      - rule-number: 100
        protocol: tcp
        from-port: 80
        to-port: 80
        cidr-block: 0.0.0.0/0
        rule-action: allow
      - rule-number: 200
        protocol: tcp
        from-port: 443
        to-port: 443
        cidr-block: 0.0.0.0/0
        rule-action: allow
    egress:
      - rule-number: 100
        protocol: -1
        from-port: 0
        to-port: 0
        cidr-block: 0.0.0.0/0
        rule-action: allow
```

### CloudFront

**Что это:** Глобальная **CDN** сеть.

**CloudFront `Distribution`:**
```yaml
# CloudFront Distribution
cloudfront-distribution:
  name: my-distribution
  origins:
    - domain-name: my-load-balancer-123456789.us-east-1.elb.amazonaws.com
      id: ALB-origin
      custom-origin-config:
        http-port: 80
        https-port: 443
        origin-protocol-policy: https-only
        origin-ssl-protocols:
          - TLSv1.2
    - domain-name: my-bucket.s3.amazonaws.com
      id: S3-origin
      s3-origin-config:
        origin-access-identity: oai-12345678

  default-cache-behavior:
    target-origin-id: ALB-origin
    viewer-protocol-policy: redirect-to-https
    allowed-methods: ["GET", "HEAD", "OPTIONS", "PUT", "POST", "PATCH", "DELETE"]
    cached-methods: ["GET", "HEAD", "OPTIONS"]
    forwarded-values:
      query-string: true
      cookies:
        forward: all
      headers:
        - Authorization
        - Host
    compress: true
    cache-policy-id: "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"  # CachingOptimized

  cache-behaviors:
    - path-pattern: "/api/*"
      target-origin-id: ALB-origin
      viewer-protocol-policy: https-only
      allowed-methods: ["GET", "HEAD", "OPTIONS", "PUT", "POST", "PATCH", "DELETE"]
      cache-policy-id: "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"
      origin-request-policy-id: "216adef6-5c7f-47e4-b989-5492eafa07d3"  # AllViewer

    - path-pattern: "/static/*"
      target-origin-id: S3-origin
      viewer-protocol-policy: https-only
      cache-policy-id: "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"

  enabled: true
  aliases:
    - api.myapp.com
    - static.myapp.com

  viewer-certificate:
    acm-certificate-arn: arn:aws:acm:us-east-1:account:certificate/certificate-id
    ssl-support-method: sni-only
    minimum-protocol-version: TLSv1.2_2021

  restrictions:
    geo-restriction:
      restriction-type: whitelist
      locations:
        - US
        - CA
        - GB
        - DE
```

**Lambda`@Edge` для edge computing:**
```javascript
// Lambda@Edge для authentication
'use strict';

exports.handler = (event, context, callback) => {
    const request = event.Records[0].cf.request;
    const headers = request.headers;

    // Check for authorization header
    const authHeader = headers.authorization;
    if (!authHeader) {
        callback(null, {
            status: '401',
            statusDescription: 'Unauthorized',
            headers: {
                'www-authenticate': [{ value: 'Basic' }]
            }
        });
        return;
    }

    // Validate JWT token or basic auth
    const token = authHeader[0].value.split(' ')[1];
    // Token validation logic here

    callback(null, request);
};
```

### API Gateway

**Что это:** Управляемый **API gateway** сервис.

**REST API configuration:**
```yaml
# API Gateway REST API
api-gateway:
  name: my-api
  description: My REST API
  endpoint-configuration:
    types:
      - REGIONAL
  resources:
    - path-part: users
      parent-id: root
      resource-methods:
        GET:
          method-responses:
            200:
              status-code: '200'
              response-models:
                application/json: UserList
          integration:
            type: aws_proxy
            integration-http-method: POST
            uri: arn:aws:apigateway:region:lambda:path/2015-03-31/functions/arn:aws:lambda:region:account:function:get-users/invocations
        POST:
          method-responses:
            201:
              status-code: '201'
          integration:
            type: aws_proxy
            integration-http-method: POST
            uri: arn:aws:apigateway:region:lambda:path/2015-03-31/functions/arn:aws:lambda:region:account:function:create-user/invocations

  stages:
    - stage-name: prod
      description: Production stage
      deployment-id: abc123
      method-settings:
        - http-method: '*'
          resource-path: '/*'
          throttling:
            burst-limit: 100
            rate-limit: 50
      variables:
        lambdaAlias: prod

  models:
    UserList:
      content-type: application/json
      schema: |
        {
          "$schema": "http://json-schema.org/draft-04/schema#",
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "id": {"type": "string"},
              "name": {"type": "string"},
              "email": {"type": "string"}
            }
          }
        }
```

**HTTP `API` (новый, более дешевый):**
```yaml
# API Gateway HTTP API
http-api:
  name: my-http-api
  protocol-type: HTTP
  cors-configuration:
    allow-credentials: false
    allow-headers:
      - content-type
      - x-amz-date
      - authorization
      - x-api-key
      - x-amz-security-token
    allow-methods:
      - "*"
    allow-origins:
      - "*"
    expose-headers:
      - date
      - keep-alive
    max-age: 86400

  routes:
    - route-key: GET /users
      target: integrations/my-lambda
    - route-key: POST /users
      target: integrations/my-lambda
    - route-key: GET /users/{id}
      target: integrations/my-lambda

  integrations:
    my-lambda:
      integration-type: AWS_PROXY
      connection-type: INTERNET
      integration-method: POST
      payload-format-version: "2.0"
      integration-uri: arn:aws:lambda:region:account:function:my-function

  stages:
    - stage-name: $default
      stage-variables:
        lambdaAlias: prod
      auto-deploy: true
```

## Security Services

### IAM (`Identity and Access Management`)

**Что это:** Управление доступом и идентификацией.

**IAM `Policies`:**
```json
// Least Privilege Policy для EC2
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ec2:DescribeInstances",
        "ec2:DescribeImages",
        "ec2:DescribeKeyPairs",
        "ec2:DescribeSecurityGroups",
        "ec2:DescribeSubnets",
        "ec2:DescribeVpcs"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "ec2:StartInstances",
        "ec2:StopInstances",
        "ec2:RebootInstances",
        "ec2:TerminateInstances"
      ],
      "Resource": "arn:aws:ec2:region:account:instance/*",
      "Condition": {
        "StringEquals": {
          "ec2:ResourceTag/Environment": "development"
        }
      }
    }
  ]
}

// S3 Bucket Policy с conditions
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::account:user/developer"
      },
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::my-bucket/*",
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": "192.168.1.0/24"
        },
        "DateGreaterThan": {
          "aws:CurrentTime": "2023-01-01T00:00:00Z"
        },
        "DateLessThan": {
          "aws:CurrentTime": "2023-12-31T23:59:59Z"
        }
      }
    }
  ]
}
```

**IAM `Roles` и `Cross`-`Account Access`:**
```yaml
# IAM Role для EC2
ec2-role:
  role-name: EC2-WebServer-Role
  assume-role-policy-document:
    Version: "2012-10-17"
    Statement:
      - Effect: Allow
        Principal:
          Service: ec2.amazonaws.com
        Action: sts:AssumeRole

  managed-policy-arns:
    - arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore
    - arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy

  inline-policies:
    - name: S3Access
      policy-document:
        Version: "2012-10-17"
        Statement:
          - Effect: Allow
            Action:
              - s3:GetObject
              - s3:PutObject
            Resource: arn:aws:s3:::my-app-bucket/*

# Cross-Account Role
cross-account-role:
  role-name: CrossAccountAccessRole
  assume-role-policy-document:
    Version: "2012-10-17"
    Statement:
      - Effect: Allow
        Principal:
          AWS: arn:aws:iam::trusted-account:root
        Action: sts:AssumeRole
        Condition:
          Bool:
            aws:MultiFactorAuthPresent: "true"
```

**IAM `Access Analyzer`:**
```yaml
# IAM Access Analyzer
access-analyzer:
  analyzer-name: account-analyzer
  type: ACCOUNT
  # or ORGANIZATION for organization-wide analysis

# Findings example
finding:
  id: finding-id
  principal:
    AWS: arn:aws:iam::account:user/username
  action:
    - s3:GetObject
  resource: arn:aws:s3:::bucket-name/*
  condition: {}
  is-public: false
  resource-type: AWS::S3::Bucket
  resource-owner-account: account
```

### KMS (`Key Management Service`)

**Что это:** Управление ключами шифрования.

**Envelope `Encryption`:**
```python
# KMS для envelope encryption
import boto3
from cryptography.fernet import Fernet

def encrypt_data(data, kms_key_id):
    # Generate data key
    kms = boto3.client('kms')
    response = kms.generate_data_key(
        KeyId=kms_key_id,
        KeySpec='AES_256'
    )

    # Encrypt data with data key
    data_key = response['Plaintext']
    encrypted_data_key = response['CiphertextBlob']

    # Use data key to encrypt actual data
    f = Fernet(data_key)
    encrypted_data = f.encrypt(data.encode())

    return {
        'encrypted_data': encrypted_data,
        'encrypted_data_key': encrypted_data_key
    }

def decrypt_data(encrypted_data, encrypted_data_key, kms_key_id):
    # Decrypt data key
    kms = boto3.client('kms')
    response = kms.decrypt(
        CiphertextBlob=encrypted_data_key
    )

    # Use decrypted data key to decrypt data
    data_key = response['Plaintext']
    f = Fernet(data_key)
    decrypted_data = f.decrypt(encrypted_data).decode()

    return decrypted_data
```

**KMS `Key Policies`:**
```json
// KMS Key Policy
{
  "Version": "2012-10-17",
  "Id": "key-policy",
  "Statement": [
    {
      "Sid": "Enable IAM User Permissions",
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::account:root"
      },
      "Action": "kms:*",
      "Resource": "*"
    },
    {
      "Sid": "Allow access for Key Administrators",
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::account:role/KMSAdminRole",
          "arn:aws:iam::account:user/KMSAdmin"
        ]
      },
      "Action": [
        "kms:Create*",
        "kms:Describe*",
        "kms:Enable*",
        "kms:List*",
        "kms:Put*",
        "kms:Update*",
        "kms:Revoke*",
        "kms:Disable*",
        "kms:Get*",
        "kms:Delete*",
        "kms:TagResource",
        "kms:UntagResource",
        "kms:ScheduleKeyDeletion",
        "kms:CancelKeyDeletion"
      ],
      "Resource": "*"
    },
    {
      "Sid": "Allow use of the key",
      "Effect": "Allow",
      "Principal": {
        "AWS": [
          "arn:aws:iam::account:role/EC2Role",
          "arn:aws:iam::account:role/LambdaRole"
        ]
      },
      "Action": [
        "kms:Encrypt",
        "kms:Decrypt",
        "kms:ReEncrypt*",
        "kms:GenerateDataKey*",
        "kms:DescribeKey"
      ],
      "Resource": "*"
    }
  ]
}
```

### CloudTrail

**Что это:** Аудит и мониторинг **API** вызовов.

**CloudTrail configuration:**
```yaml
# CloudTrail Trail
cloudtrail-trail:
  name: organization-trail
  s3-bucket-name: my-cloudtrail-bucket
  s3-key-prefix: logs
  sns-topic-name: cloudtrail-notifications
  include-global-service-events: true
  is-multi-region-trail: true
  enable-log-file-validation: true

  event-selectors:
    - read-write-type: All
      include-management-events: true
      data-resources:
        - type: AWS::S3::Object
          values:
            - arn:aws:s3:::my-bucket/*
        - type: AWS::Lambda::Function
          values:
            - arn:aws:lambda:region:account:function:*

  insight-selectors:
    - insight-type: ApiCallRateInsight
    - insight-type: ApiErrorRateInsight

# CloudWatch Alarms для CloudTrail
cloudtrail-alarms:
  - alarm-name: CloudTrailSecurityGroupChanges
    alarm-description: "Security Group Changes"
    metric-name: SecurityGroupChanges
    namespace: CloudTrailMetrics
    statistic: Sum
    period: 300
    threshold: 1
    comparison-operator: GreaterThanThreshold

  - alarm-name: CloudTrailRootLogin
    alarm-description: "Root Account Login"
    metric-name: RootLogin
    namespace: CloudTrailMetrics
    statistic: Sum
    period: 300
    threshold: 0
    comparison-operator: GreaterThanThreshold
```

## Analytics & Big Data

### Redshift

**Что это: Data warehouse** сервис.

**Redshift `Cluster`:**
```yaml
# Redshift Cluster
redshift-cluster:
  cluster-identifier: production-dw
  node-type: ra3.xlplus
  number-of-nodes: 3
  master-username: admin
  master-user-password: "{{resolve:ssm:redshift-password}}"
  db-name: analytics
  port: 5439
  vpc-security-group-ids:
    - sg-12345678
  cluster-subnet-group-name: redshift-subnet-group
  publicly-accessible: false
  encrypted: true
  kms-key-id: alias/aws/redshift
  enhanced-vpc-routing: true
  maintenance-track-name: current
  automated-snapshot-retention-period: 7
  manual-snapshot-retention-period: 365
  logging:
    enable: true
    bucket-name: redshift-logs-bucket
    s3-key-prefix: logs/
```

**Redshift `Spectrum` для `S3` data:**
```sql
-- External Schema для S3
CREATE EXTERNAL SCHEMA spectrum_schema
FROM DATA CATALOG
DATABASE 'spectrum_db'
IAM_ROLE 'arn:aws:iam::account:role/RedshiftSpectrumRole'
CREATE EXTERNAL DATABASE IF NOT EXISTS;

-- External Table
CREATE EXTERNAL TABLE spectrum_schema.sales_data (
    sale_id INTEGER,
    customer_id INTEGER,
    product_id INTEGER,
    sale_date DATE,
    amount DECIMAL(10,2)
)
STORED AS PARQUET
LOCATION 's3://my-data-bucket/sales/year=2023/month=01/'
TABLE PROPERTIES ('has_encrypted_data'='false');

-- Query combining Redshift и S3 data
SELECT
    c.customer_name,
    SUM(s.amount) as total_sales,
    COUNT(*) as order_count
FROM spectrum_schema.sales_data s
JOIN local_schema.customers c ON s.customer_id = c.customer_id
WHERE s.sale_date >= '2023-01-01'
GROUP BY c.customer_name
ORDER BY total_sales DESC;
```

### EMR (Elastic MapReduce)

**Что это: Managed Hadoop framework**.

**EMR `Cluster`:**
```yaml
# EMR Cluster
emr-cluster:
  name: data-processing-cluster
  release-label: emr-6.4.0
  service-role: EMR_DefaultRole
  applications:
    - Name: Hadoop
    - Name: Spark
    - Name: Hive
    - Name: Presto
  instances:
    master-instance-type: m5.xlarge
    master-instance-count: 1
    slave-instance-type: m5.large
    slave-instance-count: 4
    termination-protected: false
    keep-job-flow-alive-when-no-steps: true
    ec2-key-name: my-emr-key
    ec2-subnet-id: subnet-12345678

  configurations:
    - classification: spark-defaults
      properties:
        spark.sql.adaptive.enabled: "true"
        spark.sql.adaptive.coalescePartitions.enabled: "true"
        spark.serializer: "org.apache.spark.serializer.KryoSerializer"

    - classification: hive-site
      properties:
        hive.exec.dynamic.partition: "true"
        hive.exec.dynamic.partition.mode: "nonstrict"

  steps:
    - name: Setup Debugging
      action-on-failure: TERMINATE_CLUSTER
      hadoop-jar-step:
        jar: command-runner.jar
        args:
          - "state-pusher-script"

  bootstrap-actions:
    - name: Install Python packages
      script-bootstrap-action:
        path: s3://my-bucket/bootstrap.sh
```

**Spark на `EMR`:**
```python
# PySpark job для EMR
from pyspark.sql import SparkSession
from pyspark.sql.functions import col, sum, avg

def main():
    spark = SparkSession.builder \
        .appName("EMR Data Processing") \
        .getOrCreate()

    # Read data from S3
    df = spark.read.parquet("s3://my-data-bucket/input/")

    # Process data
    result = df.groupBy("category") \
        .agg(
            sum("revenue").alias("total_revenue"),
            avg("quantity").alias("avg_quantity"),
            count("*").alias("transaction_count")
        ) \
        .orderBy(col("total_revenue").desc())

    # Write results back to S3
    result.write \
        .mode("overwrite") \
        .parquet("s3://my-data-bucket/output/")

    spark.stop()

if __name__ == "__main__":
    main()
```

## Machine Learning & `AI`

### SageMaker

**Что это: Managed** `ML` **platform**.

**SageMaker `Training Job`:**
```python
# SageMaker Training Script
import boto3
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
import joblib

def train():
    # Load data from S3
    s3 = boto3.client('s3')
    s3.download_file('my-bucket', 'input/train.csv', 'train.csv')

    df = pd.read_csv('train.csv')
    X = df.drop('target', axis=1)
    y = df['target']

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    # Train model
    model = RandomForestClassifier(n_estimators=100, random_state=42)
    model.fit(X_train, y_train)

    # Save model
    joblib.dump(model, 'model.joblib')

    # Upload model to S3
    s3.upload_file('model.joblib', 'my-bucket', 'models/model.joblib')

if __name__ == "__main__":
    train()
```

**SageMaker `Endpoint`:**
```yaml
# SageMaker Model
sagemaker-model:
  model-name: my-model
  primary-container:
    image: 123456789012.dkr.ecr.us-east-1.amazonaws.com/my-model:latest
    model-data-url: s3://my-bucket/models/model.tar.gz
  execution-role-arn: arn:aws:iam::account:role/SageMakerRole

# SageMaker Endpoint Configuration
endpoint-config:
  endpoint-config-name: my-endpoint-config
  production-variants:
    - variant-name: primary
      model-name: my-model
      initial-instance-count: 1
      instance-type: ml.t2.medium
      initial-weight: 1

# SageMaker Endpoint
endpoint:
  endpoint-name: my-endpoint
  endpoint-config-name: my-endpoint-config
```

### Rekognition

**Что это: Computer vision** сервис.

**Image `Analysis`:**
```python
# Rekognition для image analysis
import boto3

def analyze_image(image_path):
    rekognition = boto3.client('rekognition')

    with open(image_path, 'rb') as image:
        response = rekognition.detect_labels(
            Image={'Bytes': image.read()},
            MaxLabels=10,
            MinConfidence=80
        )

    labels = []
    for label in response['Labels']:
        labels.append({
            'name': label['Name'],
            'confidence': label['Confidence']
        })

    return labels

def detect_faces(image_path):
    rekognition = boto3.client('rekognition')

    with open(image_path, 'rb') as image:
        response = rekognition.detect_faces(
            Image={'Bytes': image.read()},
            Attributes=['ALL']
        )

    faces = []
    for face in response['FaceDetails']:
        faces.append({
            'age_range': face['AgeRange'],
            'emotions': face['Emotions'],
            'gender': face['Gender'],
            'confidence': face['Confidence']
        })

    return faces
```

**Video `Analysis`:**
```python
# Rekognition для video analysis
import boto3

def analyze_video(video_s3_uri):
    rekognition = boto3.client('rekognition')

    # Start video analysis
    response = rekognition.start_label_detection(
        Video={
            'S3Object': {
                'Bucket': 'my-video-bucket',
                'Name': video_s3_uri
            }
        },
        MinConfidence=80,
        NotificationChannel={
            'SNSTopicArn': 'arn:aws:sns:region:account:video-analysis-topic',
            'RoleArn': 'arn:aws:iam::account:role/RekognitionRole'
        }
    )

    job_id = response['JobId']
    return job_id

def get_video_analysis_results(job_id):
    rekognition = boto3.client('rekognition')

    response = rekognition.get_label_detection(
        JobId=job_id,
        SortBy='TIMESTAMP'
    )

    labels = []
    for label in response['Labels']:
        labels.append({
            'timestamp': label['Timestamp'],
            'label': label['Label']['Name'],
            'confidence': label['Label']['Confidence']
        })

    return labels
```

## Developer Tools

### CodeCommit

**Что это: Managed Git repositories**.

**Repository setup:**
```bash
# Create CodeCommit repository
aws codecommit create-repository --repository-name my-app-repo --repository-description "My application repository"

# Clone repository
git clone https://git-codecommit.us-east-1.amazonaws.com/v1/repos/my-app-repo

# Configure credentials
git config --global credential.helper '!aws codecommit credential-helper $@'
git config --global credential.UseHttpPath true
```

**Branch protection:**
```yaml
# CodeCommit approval rules
approval-rule-template:
  approval-rule-template-name: require-approvals
  approval-rule-template-description: "Require at least one approval for main branch"
  approval-rule-template-content:
    Version: "2018-03-22"
    Statements:
      - Type: "Approvers"
        NumberOfApprovalsNeeded: 1
        ApprovalPoolMembers:
          - arn:aws:iam::account:user/approver1
          - arn:aws:iam::account:user/approver2

# Associate with repository
associate-approval-rule-template:
  repository-name: my-app-repo
  approval-rule-template-name: require-approvals
  branch-name: main
```

### CodeBuild

**Что это: Managed build service**.

**BuildSpec для `CodeBuild`:**
```yaml
# buildspec.yml
version: 0.2

env:
  variables:
    NODE_ENV: "test"
  parameter-store:
    API_KEY: "/myapp/api-key"
  secrets-manager:
    DB_PASSWORD: "prod/db:password"

phases:
  install:
    runtime-versions:
      nodejs: 16
    commands:
      - echo "Installing dependencies..."
      - npm ci

  pre_build:
    commands:
      - echo "Running pre-build steps..."
      - npm run lint

  build:
    commands:
      - echo "Building application..."
      - npm run build
      - npm run test:coverage

  post_build:
    commands:
      - echo "Running post-build steps..."
      - bash <(curl -s https://codecov.io/bash) -f coverage/lcov.info
      - echo "Build completed on $(date)"

artifacts:
  files:
    - dist//*
    - coverage//*
  discard-paths: no
  base-directory: .

cache:
  paths:
    - node_modules//*
    - ~/.npm//*

reports:
  coverage-report:
    files:
      - coverage/lcov.info
    file-format: COBERTURAXML
    base-directory: .
```

**CodeBuild `Project`:**
```yaml
# CodeBuild Project
codebuild-project:
  name: my-app-build
  description: Build project for my application
  source:
    type: CODECOMMIT
    location: https://git-codecommit.us-east-1.amazonaws.com/v1/repos/my-app-repo
    buildspec: buildspec.yml

  artifacts:
    type: S3
    location: my-build-artifacts-bucket
    path: builds/
    namespace-type: BUILD_ID
    packaging: ZIP

  environment:
    type: LINUX_CONTAINER
    image: aws/codebuild/amazonlinux2-x86_64-standard:3.0
    compute-type: BUILD_GENERAL1_SMALL
    privileged-mode: false
    environment-variables:
      - name: NODE_ENV
        value: production

  service-role: arn:aws:iam::account:role/CodeBuildServiceRole

  logs-config:
    s3-logs:
      status: ENABLED
      location: my-build-logs-bucket/logs/

  cache:
    type: S3
    location: my-build-cache-bucket/cache/
```

### CodePipeline

**Что это:** Managed CI/CD **service**.

**CodePipeline configuration:**
```yaml
# CodePipeline
codepipeline:
  name: my-app-pipeline
  role-arn: arn:aws:iam::account:role/CodePipelineServiceRole
  artifact-store:
    type: S3
    location: my-pipeline-artifacts-bucket

  stages:
    - name: Source
      actions:
        - name: SourceAction
          action-type-id:
            category: Source
            owner: AWS
            provider: CodeCommit
            version: '1'
          configuration:
            RepositoryName: my-app-repo
            BranchName: main
            PollForSourceChanges: false
          output-artifacts:
            - name: SourceArtifact
          run-order: 1

    - name: Build
      actions:
        - name: BuildAction
          action-type-id:
            category: Build
            owner: AWS
            provider: CodeBuild
            version: '1'
          configuration:
            ProjectName: my-app-build
          input-artifacts:
            - name: SourceArtifact
          output-artifacts:
            - name: BuildArtifact
          run-order: 1

    - name: Deploy
      actions:
        - name: DeployAction
          action-type-id:
            category: Deploy
            owner: AWS
            provider: CloudFormation
            version: '1'
          configuration:
            ActionMode: CREATE_UPDATE
            StackName: my-app-stack
            TemplatePath: BuildArtifact::cloudformation-template.yml
            ParameterOverrides: |
              Environment=production
          input-artifacts:
            - name: BuildArtifact
          run-order: 1
```

## Management & Governance

### CloudFormation

**Что это: Infrastructure** as **Code**.

**CloudFormation `Template`:**
```yaml
# CloudFormation Template
AWSTemplateFormatVersion: '2010-09-09'
Description: 'My Application Infrastructure'

Parameters:
  Environment:
    Type: String
    Default: development
    AllowedValues:
      - development
      - staging
      - production

  InstanceType:
    Type: String
    Default: t3.micro
    AllowedValues:
      - t3.micro
      - t3.small
      - t3.medium

Mappings:
  EnvironmentConfig:
    development:
      InstanceCount: 1
      DBInstanceClass: db.t3.micro
    staging:
      InstanceCount: 2
      DBInstanceClass: db.t3.small
    production:
      InstanceCount: 3
      DBInstanceClass: db.t3.medium

Resources:
  VPC:
    Type: AWS::EC2::VPC
    Properties:
      CidrBlock: 10.0.0.0/16
      EnableDnsHostnames: true
      EnableDnsSupport: true
      Tags:
        - Key: Name
          Value: !Sub '${AWS::StackName}-vpc'
        - Key: Environment
          Value: !Ref Environment

  WebServerSecurityGroup:
    Type: AWS::EC2::SecurityGroup
    Properties:
      GroupDescription: Security group for web servers
      VpcId: !Ref VPC
      SecurityGroupIngress:
        - IpProtocol: tcp
          FromPort: 80
          ToPort: 80
          CidrIp: 0.0.0.0/0
        - IpProtocol: tcp
          FromPort: 443
          ToPort: 443
          CidrIp: 0.0.0.0/0

  WebServerLaunchTemplate:
    Type: AWS::EC2::LaunchTemplate
    Properties:
      LaunchTemplateName: !Sub '${AWS::StackName}-web-lt'
      LaunchTemplateData:
        ImageId: ami-12345678
        InstanceType: !Ref InstanceType
        SecurityGroupIds:
          - !Ref WebServerSecurityGroup
        UserData:
          Fn::Base64: |
            #!/bin/bash
            yum update -y
            yum install -y httpd
            systemctl start httpd
            systemctl enable httpd

  WebServerAutoScalingGroup:
    Type: AWS::AutoScaling::AutoScalingGroup
    Properties:
      LaunchTemplate:
        LaunchTemplateId: !Ref WebServerLaunchTemplate
        Version: !GetAtt WebServerLaunchTemplate.LatestVersionNumber
      MinSize: !FindInMap [EnvironmentConfig, !Ref Environment, InstanceCount]
      MaxSize: !FindInMap [EnvironmentConfig, !Ref Environment, InstanceCount]
      DesiredCapacity: !FindInMap [EnvironmentConfig, !Ref Environment, InstanceCount]
      VPCZoneIdentifier:
        - !Ref PublicSubnet1
        - !Ref PublicSubnet2
      TargetGroupARNs:
        - !Ref WebServerTargetGroup

Outputs:
  VPCId:
    Description: VPC ID
    Value: !Ref VPC
    Export:
      Name: !Sub '${AWS::StackName}-vpc-id'

  WebServerSecurityGroupId:
    Description: Web Server Security Group ID
    Value: !Ref WebServerSecurityGroup
    Export:
      Name: !Sub '${AWS::StackName}-web-sg-id'
```

### Systems Manager (SSM)

**Что это:** Управление инстансами и конфигурацией.

**SSM `Parameters`:**
```bash
# Store parameters
aws ssm put-parameter \
  --name "/myapp/database/host" \
  --value "production-db.cluster-123456789.us-east-1.rds.amazonaws.com" \
  --type "String"

aws ssm put-parameter \
  --name "/myapp/database/password" \
  --value "my-secret-password" \
  --type "SecureString" \
  --key-id "alias/aws/ssm"

# Retrieve parameters
aws ssm get-parameter \
  --name "/myapp/database/host" \
  --with-decryption

# Use in application
DB_HOST=$(aws ssm get-parameter --name "/myapp/database/host" --query "Parameter.Value" --output text)
DB_PASSWORD=$(aws ssm get-parameter --name "/myapp/database/password" --with-decryption --query "Parameter.Value" --output text)
```

**SSM `Documents` для automation:**
```yaml
# SSM Document для backup
ssm-document:
  schema-version: "2.2"
  description: "Backup database and application data"
  parameters:
    DBInstanceIdentifier:
      type: String
      description: "RDS instance identifier"
    S3BucketName:
      type: String
      description: "S3 bucket for backups"

  main-steps:
    - name: CreateDBSnapshot
      action: aws:executeScript
      inputs:
        Runtime: python3.8
        Handler: create_snapshot
        Script: |
          import boto3
          import datetime

          def create_snapshot(event, context):
              rds = boto3.client('rds')
              timestamp = datetime.datetime.now().strftime('%Y-%m-%d-%H-%M-%S')

              response = rds.create_db_snapshot(
                  DBInstanceIdentifier=event['DBInstanceIdentifier'],
                  DBSnapshotIdentifier=f"{event['DBInstanceIdentifier']}-{timestamp}"
              )

              return {
                  'SnapshotIdentifier': response['DBSnapshot']['DBSnapshotIdentifier']
              }

    - name: BackupApplicationData
      action: aws:executeScript
      inputs:
        Runtime: python3.8
        Handler: backup_data
        Script: |
          import boto3
          import os

          def backup_data(event, context):
              s3 = boto3.client('s3')

              # Backup application files
              for root, dirs, files in os.walk('/var/app'):
                  for file in files:
                      local_path = os.path.join(root, file)
                      s3_path = local_path.replace('/var/app/', '')

                      s3.upload_file(
                          local_path,
                          event['S3BucketName'],
                          f"backups/{s3_path}"
                      )

              return {'Status': 'Backup completed'}
```

**Session `Manager`:**
```yaml
# SSM Session Manager для secure access
ssm-session-prefs:
  schema-version: "1.0"
  description: "Session Manager preferences"
  session-type: "Standard_Stream"
  inputs:
    s3-bucket-name: my-session-logs-bucket
    s3-key-prefix: session-logs/
    s3-encryption-enabled: true
    cloud-watch-log-group-name: /aws/ssm/session-logs
    cloud-watch-encryption-enabled: true
    idle-session-timeout: 60
    max-session-duration: 480
    run-as-enabled: false
    shell-profile:
      windows: |
        date
        whoami
      linux: |
        date
        whoami
        pwd
```

## Лучшие практики

- **Выбор сервисов:** используйте **managed**-сервисы (RDS, `Lambda`, EKS) для снижения операционной нагрузки; **EC2** — когда нужен полный контроль.
- **Безопасность:** минимальные **IAM**-права по ролям; шифрование данных в покое (S3, `EBS`, RDS) и при передаче; включите **CloudTrail** и **GuardDuty**.
- **Стоимость:** резервирования (Reserved Instances / Savings Plans) для стабильных нагрузок; **Spot** для **fault-tolerant**; теги и бюджеты для контроля затрат.
- **Надёжность:** мульти-`AZ` для критичных БД и приложений; автоскейлинг и **health checks**; регулярные бэкапы и тесты восстановления.
- **Мониторинг: CloudWatch** метрики и алерты; **X-Ray** для трейсинга; централизованные логи в **S3** или **OpenSearch**.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [[aws-basics|AWS Basics]] — основы **AWS**
- [[aws-iam|AWS IAM]] — управление доступом
- [[aws-networking|AWS Networking]] — сеть в **AWS**
- [[terraform-basics|Terraform]] — **Infrastructure as Code**
