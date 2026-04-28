---
title: "Terraform Advanced"
description: "Terraform — это инструмент Infrastructure as Code для создания, изменения и управления инфраструктурой безопасным и эффективным способом. Этот документ охватывает продвинутые концепции, паттерны и best practices для enterprise-grade инфраструктуры."
tags:
  - platform
  - iac
  - terraform-advanced
type: "overview"
difficulty: "intermediate"
aliases:
  - "Terraform Advanced"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Terraform Advanced

**Terraform** — это инструмент **Infrastructure as Code** для создания, изменения и управления инфраструктурой безопасным и эффективным способом. Этот документ охватывает продвинутые концепции, паттерны и **best practices** для **enterprise-grade** инфраструктуры.

## Полезные ссылки
- [Terraform Documentation](https://developer.hashicorp.com/terraform/docs)
- [Terraform Registry](https://registry.terraform.io/)
- [Terraform Best Practices](https://developer.hashicorp.com/terraform/cloud-docs/recommended-practices)
- [Terragrunt](https://terragrunt.gruntwork.io/docs/)
- [OPA Terraform Policies](https://www.openpolicyagent.org/docs/latest/terraform/)
- [Checkov](https://www.checkov.io/)

## Содержание

- [Продвинутая архитектура Terraform](#продвинутая-архитектура-terraform)
  - [Модульная архитектура](#модульная-архитектура)
    - [Структура enterprise проекта](#структура-enterprise-проекта)
    - [Композитные модули](#композитные-модули)
    - [Переменные модуля](#переменные-модуля)
    - [Выходы модуля](#выходы-модуля)
- [Продвинутые паттерны Terraform](#продвинутые-паттерны-terraform)
  - [Dynamic blocks и for_each](#dynamic-blocks-и-for_each)
  - [Conditional resources](#conditional-resources)
  - [Complex data structures](#complex-data-structures)
- [State management](#state-management)
  - [Remote state с locking](#remote-state-с-locking)
  - [State manipulation](#state-manipulation)
- [Workspaces и environments](#workspaces-и-environments)
  - [Workspace management](#workspace-management)
  - [Dynamic workspace configuration](#dynamic-workspace-configuration)
- [Testing и validation](#testing-и-validation)
  - [Terratest integration](#terratest-integration)
  - [OPA policy testing](#opa-policy-testing)
- [CI/CD интеграция](#cicd-интеграция)
  - [GitHub Actions advanced workflow](#github-actions-advanced-workflow)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Parallel execution и caching](#parallel-execution-и-caching)
  - [Resource graph optimization](#resource-graph-optimization)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [State recovery](#state-recovery)
- [Лучшие практики](#лучшие-практики)
  - [Enterprise patterns](#enterprise-patterns)
  - [Security best practices](#security-best-practices)
- [См. также](#см-также)

## Продвинутая архитектура Terraform

### Модульная архитектура

#### Структура enterprise проекта
Ниже — структура каталогов **enterprise Terraform**-проекта (текст).
```text
terraform-enterprise/
├── environments/
│   ├── dev/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   ├── outputs.tf
│   │   └── terraform.tfvars
│   ├── staging/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   ├── outputs.tf
│   │   └── terraform.tfvars
│   └── prod/
│       ├── main.tf
│       ├── variables.tf
│       ├── outputs.tf
│       └── terraform.tfvars
├── modules/
│   ├── vpc/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   ├── outputs.tf
│   │   └── README.md
│   ├── ecs/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   ├── outputs.tf
│   │   └── README.md
│   ├── rds/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   ├── outputs.tf
│   │   └── README.md
│   └── monitoring/
│       ├── main.tf
│       ├── variables.tf
│       ├── outputs.tf
│       └── README.md
├── shared/
│   ├── providers.tf
│   ├── backend.tf
│   ├── versions.tf
│   └── global.tf
├── scripts/
│   ├── plan.sh
│   ├── apply.sh
│   ├── destroy.sh
│   └── validate.sh
├── tests/
│   ├── integration/
│   └── unit/
├── docs/
│   ├── ARCHITECTURE.md
│   ├── CONTRIBUTING.md
│   └── README.md
├── .terraform-version
├── .gitignore
└── Makefile
```

#### Композитные модули
```hcl
# modules/vpc/main.tf
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

locals {
  name_prefix = var.name_prefix != "" ? var.name_prefix : "${var.environment}-${var.project}"
  common_tags = {
    Environment = var.environment
    Project     = var.project
    ManagedBy   = "Terraform"
    Owner       = var.owner
  }
}

# VPC
resource "aws_vpc" "this" {
  cidr_block           = var.cidr_block
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-vpc"
  })
}

# Internet Gateway
resource "aws_internet_gateway" "this" {
  vpc_id = aws_vpc.this.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-igw"
  })
}

# Public Subnets
resource "aws_subnet" "public" {
  count = length(var.public_subnet_cidrs)

  vpc_id                  = aws_vpc.this.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-public-${count.index + 1}"
    Type = "public"
  })
}

# Private Subnets
resource "aws_subnet" "private" {
  count = length(var.private_subnet_cidrs)

  vpc_id            = aws_vpc.this.id
  cidr_block        = var.private_subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-private-${count.index + 1}"
    Type = "private"
  })
}

# NAT Gateways
resource "aws_eip" "nat" {
  count = var.enable_nat_gateway ? length(var.public_subnet_cidrs) : 0

  domain = "vpc"

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-nat-${count.index + 1}"
  })
}

resource "aws_nat_gateway" "this" {
  count = var.enable_nat_gateway ? length(var.public_subnet_cidrs) : 0

  allocation_id = aws_eip.nat[count.index].id
  subnet_id     = aws_subnet.public[count.index].id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-nat-${count.index + 1}"
  })

  depends_on = [aws_internet_gateway.this]
}

# Route Tables
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.this.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.this.id
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-public"
  })
}

resource "aws_route_table" "private" {
  count = var.enable_nat_gateway ? length(var.private_subnet_cidrs) : 0

  vpc_id = aws_vpc.this.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.this[count.index % length(aws_nat_gateway.this)].id
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-private-${count.index + 1}"
  })
}

# Route Table Associations
resource "aws_route_table_association" "public" {
  count = length(var.public_subnet_cidrs)

  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "private" {
  count = length(var.private_subnet_cidrs)

  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = var.enable_nat_gateway ? aws_route_table.private[count.index % length(aws_route_table.private)].id : aws_route_table.public.id
}

# VPC Endpoints
resource "aws_vpc_endpoint" "s3" {
  count = var.enable_s3_endpoint ? 1 : 0

  vpc_id       = aws_vpc.this.id
  service_name = "com.amazonaws.${var.region}.s3"

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-s3-endpoint"
  })
}

resource "aws_vpc_endpoint" "dynamodb" {
  count = var.enable_dynamodb_endpoint ? 1 : 0

  vpc_id       = aws_vpc.this.id
  service_name = "com.amazonaws.${var.region}.dynamodb"

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-dynamodb-endpoint"
  })
}

# VPC Flow Logs
resource "aws_flow_log" "this" {
  count = var.enable_flow_logs ? 1 : 0

  iam_role_arn    = aws_iam_role.vpc_flow_log[0].arn
  log_destination = aws_cloudwatch_log_group.vpc_flow_log[0].arn
  traffic_type    = "ALL"
  vpc_id          = aws_vpc.this.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-flow-log"
  })
}

# IAM Role for VPC Flow Logs
resource "aws_iam_role" "vpc_flow_log" {
  count = var.enable_flow_logs ? 1 : 0

  name = "${local.name_prefix}-vpc-flow-log-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "vpc-flow-logs.amazonaws.com"
        }
      }
    ]
  })

  tags = local.common_tags
}

resource "aws_iam_role_policy_attachment" "vpc_flow_log" {
  count = var.enable_flow_logs ? 1 : 0

  role       = aws_iam_role.vpc_flow_log[0].name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}
```

#### Переменные модуля
```hcl
# modules/vpc/variables.tf
variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
  default     = ""
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "project" {
  description = "Project name"
  type        = string
}

variable "owner" {
  description = "Owner of the resources"
  type        = string
  default     = "platform-team"
}

variable "region" {
  description = "AWS region"
  type        = string
}

variable "cidr_block" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets"
  type        = list(string)
  default     = ["10.0.10.0/24", "10.0.11.0/24", "10.0.12.0/24"]
}

variable "availability_zones" {
  description = "Availability zones"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

variable "enable_nat_gateway" {
  description = "Enable NAT Gateway for private subnets"
  type        = bool
  default     = true
}

variable "enable_s3_endpoint" {
  description = "Enable S3 VPC endpoint"
  type        = bool
  default     = true
}

variable "enable_dynamodb_endpoint" {
  description = "Enable DynamoDB VPC endpoint"
  type        = bool
  default     = true
}

variable "enable_flow_logs" {
  description = "Enable VPC flow logs"
  type        = bool
  default     = true
}
```

#### Выходы модуля
```hcl
# modules/vpc/outputs.tf
output "vpc_id" {
  description = "ID of the VPC"
  value       = aws_vpc.this.id
}

output "vpc_cidr_block" {
  description = "CIDR block of the VPC"
  value       = aws_vpc.this.cidr_block
}

output "public_subnet_ids" {
  description = "IDs of public subnets"
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "IDs of private subnets"
  value       = aws_subnet.private[*].id
}

output "internet_gateway_id" {
  description = "ID of the Internet Gateway"
  value       = aws_internet_gateway.this.id
}

output "nat_gateway_ids" {
  description = "IDs of NAT Gateways"
  value       = aws_nat_gateway.this[*].id
}

output "public_route_table_id" {
  description = "ID of public route table"
  value       = aws_route_table.public.id
}

output "private_route_table_ids" {
  description = "IDs of private route tables"
  value       = aws_route_table.private[*].id
}
```

## Продвинутые паттерны Terraform

### Dynamic blocks и for_each
```hcl
# Dynamic security groups
resource "aws_security_group" "dynamic" {
  name_prefix = "${var.name_prefix}-dynamic-"
  vpc_id      = var.vpc_id

  dynamic "ingress" {
    for_each = var.ingress_rules
    content {
      from_port   = ingress.value.from_port
      to_port     = ingress.value.to_port
      protocol    = ingress.value.protocol
      cidr_blocks = ingress.value.cidr_blocks
      description = ingress.value.description
    }
  }

  dynamic "egress" {
    for_each = var.egress_rules
    content {
      from_port   = egress.value.from_port
      to_port     = egress.value.to_port
      protocol    = egress.value.protocol
      cidr_blocks = egress.value.cidr_blocks
      description = egress.value.description
    }
  }

  tags = var.tags
}

# Multiple instances with for_each
resource "aws_instance" "servers" {
  for_each = var.server_config

  ami           = each.value.ami
  instance_type = each.value.instance_type
  subnet_id     = each.value.subnet_id

  tags = merge(var.tags, {
    Name = "${var.name_prefix}-${each.key}"
  })
}

# Dynamic IAM policies
data "aws_iam_policy_document" "dynamic" {
  dynamic "statement" {
    for_each = var.policy_statements
    content {
      effect    = statement.value.effect
      actions   = statement.value.actions
      resources = statement.value.resources

      dynamic "condition" {
        for_each = statement.value.conditions != null ? statement.value.conditions : []
        content {
          test     = condition.value.test
          variable = condition.value.variable
          values   = condition.value.values
        }
      }
    }
  }
}
```

### Conditional resources
```hcl
# Conditional resource creation
resource "aws_db_instance" "conditional" {
  count = var.create_database ? 1 : 0

  allocated_storage    = var.db_allocated_storage
  engine               = var.db_engine
  engine_version       = var.db_engine_version
  instance_class       = var.db_instance_class
  name                 = var.db_name
  username             = var.db_username
  password             = var.db_password
  parameter_group_name = var.db_parameter_group_name
  skip_final_snapshot  = var.db_skip_final_snapshot

  tags = var.tags
}

# Resource with conditional attributes
resource "aws_s3_bucket" "conditional" {
  bucket = var.bucket_name

  # Conditional versioning
  versioning {
    enabled = var.enable_versioning
  }

  # Conditional server-side encryption
  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = var.sse_algorithm
      }
      bucket_key_enabled = var.bucket_key_enabled
    }
  }

  # Conditional logging
  dynamic "logging" {
    for_each = var.enable_logging ? [1] : []
    content {
      target_bucket = var.log_bucket
      target_prefix = var.log_prefix
    }
  }

  tags = var.tags
}
```

### Complex data structures
```hcl
# Complex variable structures
variable "server_config" {
  description = "Configuration for servers"
  type = map(object({
    ami           = string
    instance_type = string
    subnet_id     = string
    user_data     = optional(string)
    volumes = optional(list(object({
      size = number
      type = string
      iops = optional(number)
    })), [])
    tags = optional(map(string), {})
  }))
  default = {}
}

variable "network_config" {
  description = "Network configuration"
  type = object({
    vpc = object({
      cidr_block = string
      subnets = map(object({
        cidr_block        = string
        availability_zone = string
        public           = bool
      }))
    })
    security_groups = map(object({
      name        = string
      description = string
      rules = list(object({
        type        = string
        from_port   = number
        to_port     = number
        protocol    = string
        cidr_blocks = list(string)
        description = string
      }))
    }))
  })
}

# Processing complex data
locals {
  # Flatten network configuration
  flattened_subnets = flatten([
    for vpc_key, vpc in var.network_config.vpc.subnets : {
      vpc_key           = vpc_key
      cidr_block        = vpc.cidr_block
      availability_zone = vpc.availability_zone
      public           = vpc.public
    }
  ])

  # Group by type
  public_subnets  = [for s in local.flattened_subnets : s if s.public]
  private_subnets = [for s in local.flattened_subnets : s if !s.public]

  # Transform security group rules
  security_group_rules = flatten([
    for sg_key, sg in var.network_config.security_groups : [
      for rule in sg.rules : {
        security_group = sg_key
        name          = sg.name
        type          = rule.type
        from_port     = rule.from_port
        to_port       = rule.to_port
        protocol      = rule.protocol
        cidr_blocks   = rule.cidr_blocks
        description   = rule.description
      }
    ]
  ])
}
```

## State management

### Remote state с locking
```hcl
# backend.tf
terraform {
  backend "s3" {
    bucket         = "terraform-state-bucket"
    key            = "enterprise/infrastructure.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "terraform-state-lock"
  }
}

# S3 bucket for state
resource "aws_s3_bucket" "terraform_state" {
  bucket = "terraform-state-bucket"

  versioning {
    enabled = true
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }

  tags = {
    Name        = "Terraform State Bucket"
    Environment = "shared"
  }
}

# DynamoDB table for locking
resource "aws_dynamodb_table" "terraform_lock" {
  name         = "terraform-state-lock"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = {
    Name        = "Terraform State Lock Table"
    Environment = "shared"
  }
}
```

### State manipulation
```bash
# State inspection
terraform state list
terraform state show aws_instance.example
terraform state pull > state.json

# State manipulation
terraform state mv aws_instance.old aws_instance.new
terraform state rm aws_instance.removed

# Import existing resources
terraform import aws_instance.example i-1234567890abcdef0

# Taint resources for recreation
terraform taint aws_instance.example

# Remove from state without destroying
terraform state rm aws_instance.example

# Advanced state operations
terraform state push state.json
terraform state replace-provider registry.terraform.io/-/aws registry.terraform.io/hashicorp/aws
```

## Workspaces и environments

### Workspace management
```bash
# Create and switch workspaces
terraform workspace new dev
terraform workspace new staging
terraform workspace new prod
terraform workspace select dev
terraform workspace list

# Workspace-specific variables
# dev.tfvars
environment = "dev"
instance_count = 1
instance_type = "t3.micro"

# staging.tfvars
environment = "staging"
instance_count = 2
instance_type = "t3.small"

# prod.tfvars
environment = "prod"
instance_count = 3
instance_type = "t3.medium"
```

### Dynamic workspace configuration
```hcl
# main.tf with workspace support
locals {
  workspace_config = {
    dev = {
      instance_count = 1
      instance_type  = "t3.micro"
      db_instance_class = "db.t3.micro"
    }
    staging = {
      instance_count = 2
      instance_type  = "t3.small"
      db_instance_class = "db.t3.small"
    }
    prod = {
      instance_count = 3
      instance_type  = "t3.medium"
      db_instance_class = "db.t3.medium"
    }
  }

  config = local.workspace_config[terraform.workspace]
}

resource "aws_instance" "app" {
  count         = local.config.instance_count
  instance_type = local.config.instance_type
  ami           = var.ami_id

  tags = {
    Name        = "${terraform.workspace}-app-${count.index + 1}"
    Environment = terraform.workspace
  }
}

resource "aws_db_instance" "main" {
  instance_class = local.config.db_instance_class
  engine         = "postgres"
  allocated_storage = 20

  tags = {
    Name        = "${terraform.workspace}-db"
    Environment = terraform.workspace
  }
}
```

## Testing и validation

### Terratest integration
```go
// test/terraform_test.go
package test

import (
    "testing"
    "github.com/gruntwork-io/terratest/modules/terraform"
    "github.com/gruntwork-io/terratest/modules/aws"
    "github.com/stretchr/testify/assert"
)

func TestTerraformVpc(t *testing.T) {
    t.Parallel()

    terraformOptions := &terraform.Options{
        TerraformDir: "../examples/vpc",
        Vars: map[string]interface{}{
            "name_prefix": "test",
            "environment": "test",
        },
    }

    defer terraform.Destroy(t, terraformOptions)
    terraform.InitAndApply(t, terraformOptions)

    // Verify VPC exists
    vpcId := terraform.Output(t, terraformOptions, "vpc_id")
    assert.NotEmpty(t, vpcId)

    // Verify VPC is available
    aws.AssertVpcExists(t, vpcId, "us-east-1")

    // Verify subnets exist
    publicSubnetIds := terraform.OutputList(t, terraformOptions, "public_subnet_ids")
    assert.Len(t, publicSubnetIds, 3)

    privateSubnetIds := terraform.OutputList(t, terraformOptions, "private_subnet_ids")
    assert.Len(t, privateSubnetIds, 3)

    // Verify security groups
    for _, subnetId := range publicSubnetIds {
        aws.AssertEc2SubnetExists(t, subnetId, "us-east-1")
    }
}

func TestTerraformDatabase(t *testing.T) {
    t.Parallel()

    terraformOptions := &terraform.Options{
        TerraformDir: "../examples/database",
        Vars: map[string]interface{}{
            "environment": "test",
            "db_name": "testdb",
        },
    }

    defer terraform.Destroy(t, terraformOptions)
    terraform.InitAndApply(t, terraformOptions)

    // Verify database exists and is accessible
    dbEndpoint := terraform.Output(t, terraformOptions, "db_endpoint")
    assert.NotEmpty(t, dbEndpoint)

    // Test database connectivity
    db := aws.GetRdsInstanceDetails(t, terraform.Output(t, terraformOptions, "db_instance_id"), "us-east-1")
    assert.Equal(t, "available", db.DBInstanceStatus)
}
```

### OPA policy testing
```rego
# policies/terraform.rego
package terraform

import input as tfplan

# Deny resources without required tags
deny[msg] {
    resource := tfplan.resource_changes[_]
    not has_required_tags(resource)
    msg := sprintf("Resource %s is missing required tags", [resource.address])
}

has_required_tags(resource) {
    required_tags := {"Environment", "Project", "Owner"}
    tags := resource.change.after.tags
    count({tag | required_tags[tag]; tags[tag]}) == count(required_tags)
}

# Deny public S3 buckets
deny[msg] {
    resource := tfplan.resource_changes[_]
    resource.type == "aws_s3_bucket"
    resource.change.after.acl == "public-read"
    msg := sprintf("S3 bucket %s has public ACL", [resource.address])
}

# Deny unrestricted security groups
deny[msg] {
    resource := tfplan.resource_changes[_]
    resource.type == "aws_security_group"
    rule := resource.change.after.ingress[_]
    rule.cidr_blocks[_] == "0.0.0.0/0"
    rule.from_port == 22
    msg := sprintf("Security group %s allows unrestricted SSH access", [resource.address])
}
```

## CI/CD интеграция

### GitHub Actions advanced workflow
```yaml
# .github/workflows/terraform.yml
name: 'Terraform'

on:
  push:
    branches: [main]
    paths: ['terraform/']
  pull_request:
    branches: [main]
    paths: ['terraform/']
  workflow_dispatch:
    inputs:
      environment:
        description: 'Environment to deploy'
        required: true
        default: 'dev'
        type: choice
        options:
          - dev
          - staging
          - prod

env:
  TF_VERSION: '1.5.0'
  TG_VERSION: '0.48.0'

jobs:
  terraform:
    name: 'Terraform'
    runs-on: ubuntu-latest
    environment: ${{ github.event.inputs.environment || 'dev' }}

    defaults:
      run:
        shell: bash
        working-directory: terraform

    steps:
      - name: Checkout
        uses: actions/checkout@v3

      - name: Setup Terraform
        uses: hashicorp/setup-terraform@v2
        with:
          terraform_version: ${{ env.TF_VERSION }}

      - name: Setup Terragrunt
        run: |
          wget -O terragrunt https://github.com/gruntwork-io/terragrunt/releases/download/v${TG_VERSION}/terragrunt_linux_amd64
          chmod +x terragrunt
          sudo mv terragrunt /usr/local/bin/

      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1

      - name: Terraform Format
        run: terraform fmt -check

      - name: Terraform Validate
        run: terraform validate

      - name: Terraform Plan
        id: plan
        run: |
          terraform plan -no-color -out=tfplan
          terraform show -no-color tfplan > tfplan.txt
        continue-on-error: true

      - name: Update Pull Request
        uses: actions/github-script@v6
        if: github.event_name == 'pull_request'
        env:
          PLAN: "terraform\n${{ steps.plan.outputs.stdout }}"
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          script: |
            const output = `#### Terraform Format and Validate 🖌\`${{ steps.fmt.outcome }}\`
            #### Terraform Plan 📖\`${{ steps.plan.outcome }}\`

            <details><summary>Show Plan</summary>

            \`\`\`\`\`\`terraform\n
            ${process.env.PLAN}
            \`\`\`\`\`\`

            </details>

            *Pushed by: @${{ github.actor }}, Action: \`${{ github.event_name }}\`*`;

            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: output
            })

      - name: Terraform Plan Status
        if: steps.plan.outcome == 'failure'
        run: exit 1

      - name: Terraform Apply
        if: |
          (github.ref == 'refs/heads/main' && github.event_name == 'push') ||
          (github.event_name == 'workflow_dispatch' && github.event.inputs.environment)
        run: terraform apply -auto-approve tfplan

      - name: Terraform Destroy
        if: github.event_name == 'workflow_dispatch' && github.event.inputs.destroy == 'true'
        run: terraform destroy -auto-approve

  security:
    name: 'Security Scan'
    runs-on: ubuntu-latest
    needs: terraform

    steps:
      - name: Checkout
        uses: actions/checkout@v3

      - name: Setup Terraform
        uses: hashicorp/setup-terraform@v2
        with:
          terraform_version: ${{ env.TF_VERSION }}

      - name: Terraform Init
        working-directory: terraform
        run: terraform init

      - name: Checkov Security Scan
        uses: bridgecrewio/checkov-action@v12
        with:
          directory: terraform/
          framework: terraform
          output_format: cli
          output_file_path: console,checkov-results.sarif

      - name: Upload SARIF file
        uses: github/codeql-action/upload-sarif@v2
        if: success() || failure()
        with:
          sarif_file: checkov-results.sarif
```

## Производительность и оптимизация

### Parallel execution и caching
```hcl
# Provider configuration for performance
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.region

  # Performance optimizations
  skip_metadata_api_check     = true
  skip_region_validation      = true
  skip_credentials_validation = true

  # Retry configuration
  retry_mode      = "adaptive"
  max_retries     = 3
  retry_delay     = 1
}
```

### Resource graph optimization
```hcl
# Parallel resource creation
resource "aws_security_group" "web" {
  name_prefix = "${var.name_prefix}-web-"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Depends on VPC creation
  depends_on = [aws_vpc.main]
}

resource "aws_security_group" "db" {
  name_prefix = "${var.name_prefix}-db-"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.web.id]
  }

  # Can be created in parallel with web SG
  depends_on = [aws_vpc.main]
}

# Use lifecycle to prevent unnecessary updates
resource "aws_instance" "web" {
  ami           = var.ami_id
  instance_type = var.instance_type

  lifecycle {
    ignore_changes = [
      ami,  # Ignore AMI changes for immutable infrastructure
      user_data,
    ]

    create_before_destroy = true  # Blue-green deployment
  }
}

# Data sources for dynamic lookups
data "aws_ami" "latest_amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "state"
    values = ["available"]
  }
}
```

## Решение проблем

### Распространенные проблемы
```bash
# Debug Terraform execution
export TF_LOG=DEBUG
export TF_LOG_PATH=terraform.log
terraform plan

# State lock issues
terraform force-unlock LOCK_ID

# Provider issues
terraform init -upgrade
terraform providers lock

# Module issues
terraform get -update
terraform init -reconfigure

# Variable issues
terraform validate
terraform plan -var-file=terraform.tfvars

# Performance issues
terraform plan -parallelism=10
terraform apply -parallelism=10

# Memory issues
export TF_CLI_ARGS="-memory-limit=1024"
```

### State recovery
```bash
# Backup state before operations
terraform state pull > backup.tfstate

# Recover from backup
terraform state push backup.tfstate

# Fix corrupted state
terraform refresh
terraform state list > resources.txt

# Migrate state between backends
terraform init -migrate-state
terraform state pull > state.json
# Edit state.json if needed
terraform state push state.json

# Handle resource drift
terraform plan -refresh-only
terraform apply -refresh-only
```

## Лучшие практики

### Enterprise patterns
```hcl
# globals.tf - Global constants
locals {
  common_tags = {
    Organization = "Enterprise Corp"
    ManagedBy    = "Terraform"
    Contact      = "platform@enterprise.com"
  }

  environments = {
    dev = {
      instance_type = "t3.micro"
      min_size     = 1
      max_size     = 3
    }
    staging = {
      instance_type = "t3.small"
      min_size     = 2
      max_size     = 5
    }
    prod = {
      instance_type = "t3.medium"
      min_size     = 3
      max_size     = 10
    }
  }

  regions = {
    primary   = "us-east-1"
    secondary = "us-west-2"
    tertiary  = "eu-west-1"
  }
}

# versions.tf - Version constraints
terraform {
  required_version = ">= 1.0, < 2.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }

    random = {
      source  = "hashicorp/random"
      version = "~> 3.0"
    }
  }
}

# providers.tf - Provider configurations
provider "aws" {
  region = var.region

  default_tags {
    tags = merge(local.common_tags, {
      Environment = var.environment
      Project     = var.project
    })
  }
}
```

### Security best practices
```hcl
# Security-focused configurations
resource "aws_s3_bucket" "secure" {
  bucket = var.bucket_name

  # Encryption
  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
      bucket_key_enabled = true
    }
  }

  # Versioning
  versioning {
    enabled = true
  }

  # Access logging
  logging {
    target_bucket = aws_s3_bucket.logs.id
    target_prefix = "s3-access-logs/"
  }

  # Public access block
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true

  tags = var.tags
}

# IAM with least privilege
resource "aws_iam_role" "application" {
  name = "${var.name_prefix}-app-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })

  tags = var.tags
}

resource "aws_iam_role_policy" "application" {
  name = "${var.name_prefix}-app-policy"
  role = aws_iam_role.application.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:PutObject"
        ]
        Resource = [
          "${aws_s3_bucket.app_data.arn}/*"
        ]
      },
      {
        Effect = "Allow"
        Action = [
          "cloudwatch:PutMetricData"
        ]
        Resource = "*"
      }
    ]
  })
}
```
## См. также
- [Terraform: основы](terraform.md) — общий справочник Terraform
- [Terraform Basics](terraform-basics.md) — краткое введение
- [AWS](../../cloud-providers/aws-basics.md) — Amazon Web Services
- [Ansible](../ansible/ansible-basics.md) — конфигурационное управление
- [Docker](../../containers/docker/docker-basics.md) — контейнеризация
