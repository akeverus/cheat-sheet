---
title: "Terraform"
description: "Terraform - это инструмент для инфраструктурного программирования с открытым исходным кодом. Terraform позволяет определять инфраструктуру как код, управлять ее жизненным циклом и обеспечивать предсказуемые развертывания в различных облачных провайдерах и локальных системах."
tags:
  - platform
  - iac
  - terraform
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Terraform

**Terraform** — это инструмент для инфраструктурного программирования с открытым исходным кодом. **Terraform** позволяет определять инфраструктуру как код, управлять ее жизненным циклом и обеспечивать предсказуемые развертывания в различных облачных провайдерах и локальных системах.

## Полезные ссылки
- [Официальная документация Terraform](https://developer.hashicorp.com/terraform/docs)
- [Terraform Registry](https://registry.terraform.io/)
- [Terraform GitHub](https://github.com/hashicorp/terraform)
- [AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [Azure Provider](https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs)
- [Google Cloud Provider](https://registry.terraform.io/providers/hashicorp/google/latest/docs)

## Содержание

- [Основы Terraform](#основы-terraform)
  - [Архитектура Terraform](#архитектура-terraform)
  - [Установка и настройка](#установка-и-настройка)
    - [Linux/macOS](#linuxmacos)
    - [Windows](#windows)
    - [Docker](#docker)
- [Структура проекта](#структура-проекта)
  - [Базовая структура](#базовая-структура)
  - [Файлы конфигурации](#файлы-конфигурации)
    - [main.tf](#maintf)
    - [variables.tf](#variablestf)
    - [outputs.tf](#outputstf)
- [Работа с состояниями](#работа-с-состояниями)
  - [Backend конфигурации](#backend-конфигурации)
    - [S3 Backend](#s3-backend)
    - [Remote Backend](#remote-backend)
  - [Управление состоянием](#управление-состоянием)
- [Модули](#модули)
  - [Создание модуля](#создание-модуля)
    - [modules/vpc/main.tf](#modulesvpcmaintf)
    - [modules/vpc/variables.tf](#modulesvpcvariablestf)
    - [Использование модуля](#использование-модуля)
- [Рабочие процессы](#рабочие-процессы)
  - [Основные команды](#основные-команды)
  - [Продвинутые команды](#продвинутые-команды)
- [Data Sources](#data-sources)
  - [Работа с существующими ресурсами](#работа-с-существующими-ресурсами)
- [Управление зависимостями](#управление-зависимостями)
  - [Meta-arguments](#meta-arguments)
  - [Dynamic blocks](#dynamic-blocks)
- [Провайдеры](#провайдеры)
  - [Множественные провайдеры](#множественные-провайдеры)
- [Лучшие практики](#лучшие-практики)
  - [Организация кода](#организация-кода)
  - [Безопасность](#безопасность)
  - [CI/CD интеграция](#cicd-интеграция)
  - [Тестирование](#тестирование)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [Отладка](#отладка)
- [См. также](#см-также)

## Основы Terraform

### Архитектура Terraform

Краткое описание компонентов **Terraform**: конфигурация (.tf), **state**, провайдеры, модули, **workspaces**.

Ниже — комментарии к основным компонентам **Terraform** (HCL).
```hcl
# Основные компоненты:
# - Configuration: файлы *.tf с описанием инфраструктуры
# - State: текущее состояние инфраструктуры (terraform.tfstate)
# - Providers: плагины для взаимодействия с API различных систем
# - Modules: переиспользуемые компоненты конфигурации
# - Workspaces: изоляция состояний для разных сред
```

### Установка и настройка

#### Linux/macOS
```bash
# Скачивание и установка
wget https://releases.hashicorp.com/terraform/1.5.0/terraform_1.5.0_linux_amd64.zip
unzip terraform_1.5.0_linux_amd64.zip
sudo mv terraform /usr/local/bin/

# Проверка установки
terraform version

# Автодополнение
terraform -install-autocomplete
```

#### Windows
```powershell
# Через Chocolatey
choco install terraform

# Через winget
winget install HashiCorp.Terraform

# Проверка установки
terraform version
```

#### Docker
```bash
# Запуск Terraform в контейнере
docker run --rm -it \
  -v $(pwd):/workspace \
  -w /workspace \
  hashicorp/terraform:latest \
  init

# С кастомным образом
FROM hashicorp/terraform:latest
RUN apk add --no-cache \
    aws-cli \
    google-cloud-sdk \
    azure-cli
```

## Структура проекта

### Базовая структура
```text
terraform-project/
├── main.tf                 # Основная конфигурация
├── variables.tf           # Переменные
├── outputs.tf             # Выходы
├── terraform.tfvars       # Значения переменных
├── terraform.tfstate      # Состояние (не коммитить)
├── .terraform/            # Кэш провайдеров
├── modules/               # Локальные модули
│   └── vpc/
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
└── environments/          # Разные среды
    ├── dev/
    │   ├── main.tf
    │   └── terraform.tfvars
    └── prod/
        ├── main.tf
        └── terraform.tfvars
```

### Файлы конфигурации

#### main.tf
```hcl
# Определение провайдера
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Конфигурация backend
  backend "s3" {
    bucket         = "my-terraform-state"
    key            = "terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
}

# Конфигурация провайдера AWS
provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Environment = var.environment
      Project     = var.project_name
      ManagedBy   = "Terraform"
    }
  }
}

# Создание VPC
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "${var.project_name}-vpc"
  }
}

# Создание подсетей
resource "aws_subnet" "public" {
  count             = length(var.public_subnet_cidrs)
  vpc_id            = aws_vpc.main.id
  cidr_block        = var.public_subnet_cidrs[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name = "${var.project_name}-public-${count.index + 1}"
  }
}

# Internet Gateway
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-igw"
  }
}

# Route Table
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "${var.project_name}-public-rt"
  }
}

# Security Group
resource "aws_security_group" "web" {
  name_prefix = "${var.project_name}-web-"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${var.project_name}-web-sg"
  }
}
```

#### variables.tf
```hcl
# Переменные для конфигурации
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "my-project"
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.micro"
}

variable "instance_count" {
  description = "Number of EC2 instances"
  type        = number
  default     = 1
}

# Валидация переменных
variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be one of: dev, staging, prod."
  }
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"

  validation {
    condition     = can(cidrhost(var.vpc_cidr, 0))
    error_message = "Must be a valid CIDR block."
  }
}
```

#### outputs.tf
```hcl
# Выходы для использования другими конфигурациями
output "vpc_id" {
  description = "ID of the created VPC"
  value       = aws_vpc.main.id
}

output "vpc_cidr" {
  description = "CIDR block of the created VPC"
  value       = aws_vpc.main.cidr_block
}

output "public_subnet_ids" {
  description = "IDs of the created public subnets"
  value       = aws_subnet.public[*].id
}

output "security_group_id" {
  description = "ID of the web security group"
  value       = aws_security_group.web.id
}

output "internet_gateway_id" {
  description = "ID of the Internet Gateway"
  value       = aws_internet_gateway.main.id
}

# Чувствительные выходы
output "database_password" {
  description = "Database password"
  value       = aws_db_instance.main.password
  sensitive   = true
}

# Выходы с описательными именами
output "load_balancer_dns_name" {
  description = "DNS name of the load balancer"
  value       = aws_lb.main.dns_name
}

output "bastion_public_ip" {
  description = "Public IP of the bastion host"
  value       = aws_instance.bastion.public_ip
}
```

## Работа с состояниями

### Backend конфигурации

#### S3 Backend
```hcl
terraform {
  backend "s3" {
    bucket         = "my-terraform-state-bucket"
    key            = "terraform/state"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true

    # Для изоляции сред
    # key = "environments/${var.environment}/terraform.tfstate"
  }
}

# Создание S3 bucket для state
resource "aws_s3_bucket" "terraform_state" {
  bucket = "my-terraform-state-bucket"

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
}

# DynamoDB для блокировок
resource "aws_dynamodb_table" "terraform_locks" {
  name         = "terraform-locks"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }
}
```

#### Remote Backend
```hcl
terraform {
  backend "remote" {
    hostname     = "app.terraform.io"
    organization = "my-organization"

    workspaces {
      name = "my-workspace"
    }
  }
}
```

### Управление состоянием
```bash
# Инициализация backend
terraform init

# Проверка состояния
terraform state list
terraform state show aws_instance.example

# Импорт существующего ресурса
terraform import aws_instance.example i-1234567890abcdef0

# Удаление ресурса из состояния (без удаления самого ресурса)
terraform state rm aws_instance.example

# Перемещение ресурса в состоянии
terraform state mv aws_instance.old aws_instance.new

# Обновление состояния
terraform refresh

# Показ изменений в состоянии
terraform plan -refresh-only
```

## Модули

### Создание модуля
```text
modules/vpc/
├── main.tf
├── variables.tf
├── outputs.tf
└── README.md
```

#### modules/vpc/main.tf
```hcl
# VPC Module
resource "aws_vpc" "this" {
  cidr_block           = var.cidr_block
  enable_dns_hostnames = var.enable_dns_hostnames
  enable_dns_support   = var.enable_dns_support

  tags = merge(
    {
      Name = var.name
    },
    var.tags
  )
}

# Internet Gateway
resource "aws_internet_gateway" "this" {
  vpc_id = aws_vpc.this.id

  tags = merge(
    {
      Name = "${var.name}-igw"
    },
    var.tags
  )
}

# Public Subnets
resource "aws_subnet" "public" {
  count = length(var.public_subnets)

  vpc_id                  = aws_vpc.this.id
  cidr_block              = var.public_subnets[count.index]
  availability_zone       = var.azs[count.index]
  map_public_ip_on_launch = true

  tags = merge(
    {
      Name = "${var.name}-public-${var.azs[count.index]}"
    },
    var.tags,
    {
      "kubernetes.io/role/elb" = "1"
    }
  )
}

# Private Subnets
resource "aws_subnet" "private" {
  count = length(var.private_subnets)

  vpc_id            = aws_vpc.this.id
  cidr_block        = var.private_subnets[count.index]
  availability_zone = var.azs[count.index]

  tags = merge(
    {
      Name = "${var.name}-private-${var.azs[count.index]}"
    },
    var.tags,
    {
      "kubernetes.io/role/internal-elb" = "1"
    }
  )
}

# NAT Gateway
resource "aws_nat_gateway" "this" {
  count = var.enable_nat_gateway ? 1 : 0

  allocation_id = aws_eip.nat[0].id
  subnet_id     = aws_subnet.public[0].id

  tags = merge(
    {
      Name = "${var.name}-nat"
    },
    var.tags
  )

  depends_on = [aws_internet_gateway.this]
}

# Route Tables
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.this.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.this.id
  }

  tags = merge(
    {
      Name = "${var.name}-public"
    },
    var.tags
  )
}

resource "aws_route_table" "private" {
  count = var.enable_nat_gateway ? 1 : 0

  vpc_id = aws_vpc.this.id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.this[0].id
  }

  tags = merge(
    {
      Name = "${var.name}-private"
    },
    var.tags
  )
}

# Route Table Associations
resource "aws_route_table_association" "public" {
  count = length(var.public_subnets)

  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "private" {
  count = length(var.private_subnets)

  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private[0].id
}
```

#### modules/vpc/variables.tf
```hcl
variable "name" {
  description = "Name to be used on all the resources as identifier"
  type        = string
  default     = ""
}

variable "cidr_block" {
  description = "The CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "azs" {
  description = "A list of availability zones in the region"
  type        = list(string)
  default     = []
}

variable "public_subnets" {
  description = "A list of public subnets inside the VPC"
  type        = list(string)
  default     = []
}

variable "private_subnets" {
  description = "A list of private subnets inside the VPC"
  type        = list(string)
  default     = []
}

variable "enable_dns_hostnames" {
  description = "Should be true to enable DNS hostnames in the VPC"
  type        = bool
  default     = true
}

variable "enable_dns_support" {
  description = "Should be true to enable DNS support in the VPC"
  type        = bool
  default     = true
}

variable "enable_nat_gateway" {
  description = "Should be true if you want to provision NAT Gateways for each of your private networks"
  type        = bool
  default     = false
}

variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
  default     = {}
}
```

#### Использование модуля
```hcl
module "vpc" {
  source = "./modules/vpc"

  name = "my-vpc"
  cidr_block = "10.0.0.0/16"

  azs             = ["us-east-1a", "us-east-1b", "us-east-1c"]
  public_subnets  = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  private_subnets = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]

  enable_nat_gateway = true

  tags = {
    Environment = "dev"
    Project     = "my-app"
  }
}

# Доступ к выходам модуля
output "vpc_id" {
  value = module.vpc.vpc_id
}

output "public_subnet_ids" {
  value = module.vpc.public_subnet_ids
}

output "private_subnet_ids" {
  value = module.vpc.private_subnet_ids
}
```

## Рабочие процессы

### Основные команды
```bash
# Инициализация проекта
terraform init

# Валидация конфигурации
terraform validate

# Форматирование кода
terraform fmt

# Проверка синтаксиса
terraform fmt -check
terraform validate

# Планирование изменений
terraform plan

# Сохранение плана в файл
terraform plan -out=tfplan

# Применение изменений
terraform apply

# Применение сохраненного плана
terraform apply tfplan

# Уничтожение инфраструктуры
terraform destroy

# Показ состояния
terraform show

# Вывод графической зависимости
terraform graph | dot -Tsvg > graph.svg
```

### Продвинутые команды
```bash
# Работа с workspaces
terraform workspace list
terraform workspace select dev
terraform workspace create staging
terraform workspace delete old-env

# Импорт ресурсов
terraform import aws_instance.example i-1234567890abcdef0

# Taint (пометить ресурс для пересоздания)
terraform taint aws_instance.example

# Untaint
terraform untaint aws_instance.example

# Force unlock state
terraform force-unlock LOCK_ID

# Debug режим
TF_LOG=DEBUG terraform apply

# Parallel execution control
terraform apply -parallelism=1

# Targeting specific resources
terraform apply -target=aws_instance.example

# Refresh specific resources
terraform refresh -target=aws_instance.example
```

## Data Sources

### Работа с существующими ресурсами
```hcl
# Получение данных о существующей инфраструктуре
data "aws_vpc" "selected" {
  id = var.vpc_id
}

data "aws_subnets" "example" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.selected.id]
  }
}

data "aws_ami" "ubuntu" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

data "aws_caller_identity" "current" {}

data "aws_region" "current" {}

data "template_file" "user_data" {
  template = file("${path.module}/user-data.sh")
  vars = {
    database_url = var.database_url
    environment  = var.environment
  }
}

# Использование data sources
resource "aws_instance" "example" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = var.instance_type
  subnet_id     = data.aws_subnets.example.ids[0]

  user_data = data.template_file.user_data.rendered

  tags = {
    Name        = "example-instance"
    Environment = var.environment
    Owner       = data.aws_caller_identity.current.account_id
    Region      = data.aws_region.current.name
  }
}
```

## Управление зависимостями

### Meta-arguments
```hcl
# depends_on - явное указание зависимостей
resource "aws_instance" "web" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t3.micro"

  depends_on = [
    aws_nat_gateway.example,
    aws_route_table_association.example
  ]
}

# count - создание множественных ресурсов
resource "aws_instance" "web" {
  count = var.instance_count

  ami           = data.aws_ami.ubuntu.id
  instance_type = var.instance_type

  tags = {
    Name = "web-server-${count.index + 1}"
  }
}

# for_each - итерация по коллекциям
resource "aws_instance" "web" {
  for_each = var.instances

  ami           = data.aws_ami.ubuntu.id
  instance_type = each.value.instance_type
  subnet_id     = each.value.subnet_id

  tags = {
    Name = each.key
  }
}

# lifecycle - управление жизненным циклом
resource "aws_instance" "example" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = var.instance_type

  lifecycle {
    create_before_destroy = true
    ignore_changes = [
      tags["LastModified"]
    ]
    prevent_destroy = false
  }
}
```

### Dynamic blocks
```hcl
# Динамические блоки для повторяющихся конфигураций
resource "aws_security_group" "example" {
  name_prefix = "example-"
  vpc_id      = aws_vpc.example.id

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
}

variable "ingress_rules" {
  type = list(object({
    from_port   = number
    to_port     = number
    protocol    = string
    cidr_blocks = list(string)
    description = string
  }))
  default = [
    {
      from_port   = 80
      to_port     = 80
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
      description = "HTTP"
    },
    {
      from_port   = 443
      to_port     = 443
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
      description = "HTTPS"
    }
  ]
}
```

## Провайдеры

### Множественные провайдеры
```hcl
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.0"
    }
  }
}

# AWS Provider
provider "aws" {
  region = var.aws_region

  assume_role {
    role_arn = var.aws_role_arn
  }
}

# Kubernetes Provider
provider "kubernetes" {
  host                   = data.aws_eks_cluster.cluster.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority.0.data)
  token                  = data.aws_eks_cluster_auth.cluster.token
}

# Helm Provider
provider "helm" {
  kubernetes {
    host                   = data.aws_eks_cluster.cluster.endpoint
    cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority.0.data)
    token                  = data.aws_eks_cluster_auth.cluster.token
  }
}
```

## Лучшие практики

### Организация кода
```hcl
# Структура больших проектов
terraform-live/
├── modules/           # Переиспользуемые модули
│   ├── network/
│   ├── compute/
│   ├── storage/
│   └── monitoring/
├── environments/      # Среды развертывания
│   ├── dev/
│   │   ├── us-east-1/
│   │   └── eu-west-1/
│   ├── staging/
│   └── prod/
└── shared/           # Общие ресурсы
    ├── account/
    ├── global/
    └── security/

# Использование wrapper скриптов
#!/bin/bash
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting Terraform deployment...${NC}"

# Validate
echo -e "${YELLOW}Validating configuration...${NC}"
terraform validate

# Format
echo -e "${YELLOW}Formatting code...${NC}"
terraform fmt -check

# Plan
echo -e "${YELLOW}Planning changes...${NC}"
terraform plan -out=tfplan

# Apply
read -p "Do you want to apply these changes? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${GREEN}Applying changes...${NC}"
    terraform apply tfplan
else
    echo -e "${RED}Deployment cancelled.${NC}"
fi
```

### Безопасность
```hcl
# Шифрование чувствительных данных
resource "aws_kms_key" "terraform" {
  description             = "KMS key for Terraform state encryption"
  deletion_window_in_days = 7

  tags = {
    Name = "terraform-kms-key"
  }
}

resource "aws_s3_bucket" "terraform_state" {
  bucket = "my-terraform-state-bucket"

  versioning {
    enabled = true
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        kms_master_key_id = aws_kms_key.terraform.arn
        sse_algorithm     = "aws:kms"
      }
    }
  }
}

# Управление секретами
variable "database_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

# Использование AWS Secrets Manager
data "aws_secretsmanager_secret_version" "database" {
  secret_id = "prod/database"
}

locals {
  database_config = jsondecode(data.aws_secretsmanager_secret_version.database.secret_string)
}

# Использование HashiCorp Vault
provider "vault" {
  address = "https://vault.example.com:8200"
  token   = var.vault_token
}

data "vault_generic_secret" "database" {
  path = "secret/database"
}

# Защита от accidental deletions
resource "aws_s3_bucket" "important_data" {
  bucket = "important-data-bucket"

  lifecycle {
    prevent_destroy = true
  }
}
```

### CI/CD интеграция
```yaml
# GitHub Actions
name: Terraform CI/CD

on:
  push:
    branches: [main]
    paths: ['terraform/']
  pull_request:
    branches: [main]
    paths: ['terraform/']

jobs:
  terraform:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout
      uses: actions/checkout@v3

    - name: Setup Terraform
      uses: hashicorp/setup-terraform@v2
      with:
        terraform_version: "1.5.0"

    - name: Terraform Format
      run: terraform fmt -check
      working-directory: terraform

    - name: Terraform Validate
      run: terraform validate
      working-directory: terraform

    - name: Terraform Plan
      run: terraform plan -no-color
      working-directory: terraform
      env:
        AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
        AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}

    - name: Terraform Apply
      if: github.ref == 'refs/heads/main' && github.event_name == 'push'
      run: terraform apply -auto-approve
      working-directory: terraform
      env:
        AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
        AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
```

### Тестирование
```bash
# Kitchen-Terraform для тестирования модулей
# .kitchen.yml
---
driver:
  name: terraform

provisioner:
  name: terraform

platforms:
  - name: aws

suites:
  - name: default
    driver:
      variables:
        instance_count: 1
    verifier:
      inspec_tests:
        - test/integration/default

# Terratest для Go
package test

import (
    "testing"
    "github.com/gruntwork-io/terratest/modules/terraform"
    "github.com/stretchr/testify/assert"
)

func TestTerraformAwsVpc(t *testing.T) {
    t.Parallel()

    terraformOptions := &terraform.Options{
        TerraformDir: "../examples/aws-vpc",
        Vars: map[string]interface{}{
            "name": "test-vpc",
            "cidr_block": "10.0.0.0/16",
        },
    }

    defer terraform.Destroy(t, terraformOptions)
    terraform.InitAndApply(t, terraformOptions)

    vpcId := terraform.Output(t, terraformOptions, "vpc_id")
    assert.NotEmpty(t, vpcId)

    // Additional assertions
    vpcCidr := terraform.Output(t, terraformOptions, "vpc_cidr")
    assert.Equal(t, "10.0.0.0/16", vpcCidr)
}
```

## Решение проблем

### Распространенные проблемы
```bash
# Проблема: State lock
# Решение: Force unlock
terraform force-unlock LOCK_ID

# Проблема: Provider version conflicts
# Решение: Указать конкретные версии
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0.0"
    }
  }
}

# Проблема: Resource dependencies
# Решение: Использовать depends_on или implicit dependencies
resource "aws_instance" "web" {
  depends_on = [aws_nat_gateway.example]
}

# Проблема: State corruption
# Решение: Backup state и recovery
cp terraform.tfstate terraform.tfstate.backup
terraform state pull > terraform.tfstate

# Проблема: Slow execution
# Решение: Parallel execution control
terraform apply -parallelism=5

# Проблема: Large state files
# Решение: Использовать remote state и workspaces
terraform workspace select $ENVIRONMENT
```

### Отладка
```bash
# Debug logging
export TF_LOG=DEBUG
export TF_LOG_PATH=./terraform.log

# Verbose output
terraform plan -verbose

# Graph visualization
terraform graph -type=plan | dot -Tsvg > graph.svg

# State inspection
terraform state list
terraform state show aws_instance.example

# Output values
terraform output
terraform output -json

# Validate syntax
terraform validate
terraform fmt -check
```
## См. также
- [[terraform-advanced|Terraform Advanced]] — продвинутые паттерны Terraform
- [[ansible|Ansible]] — конфигурационное управление
- [[pulumi|Pulumi]] — инфраструктура как код на языках программирования
- [[aws-basics|AWS]] — Amazon Web Services
- [[kubernetes-basics|Kubernetes]] — оркестрация контейнеров
