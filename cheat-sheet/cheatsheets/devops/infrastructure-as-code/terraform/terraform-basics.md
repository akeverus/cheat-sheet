# Terraform: Основы Infrastructure as Code

**Комплексное руководство по использованию Terraform — инструмента для управления инфраструктурой как кодом.**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Terraform Documentation](https://www.terraform.io/docs) - Официальная документация HashiCorp

### См. также
- `../ansible/ansible-basics.md` - Ansible
- `../pulumi/pulumi-basics.md` - Pulumi

## Содержание

- [Введение в Terraform](#введение-в-terraform)
- [Основные концепции](#основные-концепции)
- [HCL синтаксис](#hcl-синтаксис)
- [Провайдеры](#провайдеры)
- [Модули](#модули)
- [Best Practices](#best-practices)

## Введение в Terraform

**Terraform** — инструмент Infrastructure as Code от HashiCorp для создания, изменения и версионирования инфраструктуры.

### Основные возможности

- Декларативная конфигурация
- Поддержка множества провайдеров
- Планирование изменений
- State management

## Основные концепции

```hcl
# Основной файл конфигурации Terraform
# HCL (HashiCorp Configuration Language) - язык конфигурации Terraform

# Провайдер - определяет какой облачный провайдер использовать
provider "aws" {
  region = "us-east-1"  # Регион AWS для создания ресурсов
}

# Ресурс - описание инфраструктурного компонента
resource "aws_instance" "web" {
  # Тип ресурса: aws_instance (EC2 инстанс)
  # Имя ресурса: web (используется для ссылок)
  
  ami           = "ami-0c55b159cbfafe1f0"  # ID образа Amazon Machine Image
  instance_type = "t2.micro"               # Тип инстанса (микро инстанс)
  
  tags = {
    Name = "Web Server"  # Теги для идентификации ресурса
  }
}

# Output - значения которые выводятся после применения
output "instance_ip" {
  value = aws_instance.web.public_ip  # Публичный IP созданного инстанса
}
```

## HCL синтаксис

```hcl
/**
 * HCL синтаксис для Terraform конфигураций
 */

# Переменные - параметры конфигурации
variable "instance_type" {
  description = "Тип EC2 инстанса"
  type        = string
  default     = "t2.micro"  # Значение по умолчанию
}

variable "environment" {
  description = "Окружение (dev, staging, prod)"
  type        = string
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be dev, staging, or prod."
  }
}

# Использование переменных
resource "aws_instance" "app" {
  instance_type = var.instance_type  # Использование переменной
  ami           = "ami-0c55b159cbfafe1f0"
  
  tags = {
    Environment = var.environment  # Использование переменной в тегах
  }
}

# Локальные значения - вычисляемые значения
locals {
  common_tags = {
    Project     = "MyProject"
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

# Использование локальных значений
resource "aws_instance" "server" {
  ami    = "ami-0c55b159cbfafe1f0"
  tags   = local.common_tags  # Использование локальных значений
}
```

## Провайдеры

```hcl
/**
 * Конфигурация различных провайдеров
 */

# AWS провайдер
provider "aws" {
  region  = "us-east-1"
  profile = "default"  # AWS профиль для аутентификации
}

# Azure провайдер
provider "azurerm" {
  features {}  # Блок features для Azure провайдера
  subscription_id = var.azure_subscription_id
  client_id       = var.azure_client_id
  client_secret   = var.azure_client_secret
  tenant_id       = var.azure_tenant_id
}

# Google Cloud провайдер
provider "google" {
  project = var.gcp_project_id
  region  = "us-central1"
}

# Docker провайдер (для локальной разработки)
provider "docker" {
  host = "unix:///var/run/docker.sock"
}
```

## Модули

```hcl
/**
 * Создание и использование модулей в Terraform
 * Модули позволяют переиспользовать конфигурацию
 */

# Определение модуля (в папке modules/ec2-instance/)
# modules/ec2-instance/main.tf
variable "instance_type" {
  type = string
}

variable "ami" {
  type = string
}

variable "tags" {
  type = map(string)
  default = {}
}

resource "aws_instance" "this" {
  ami           = var.ami
  instance_type = var.instance_type
  tags          = var.tags
}

output "instance_id" {
  value = aws_instance.this.id
}

# Использование модуля (в корневом модуле)
module "web_server" {
  source = "./modules/ec2-instance"  # Путь к модулю
  
  instance_type = "t2.micro"
  ami          = "ami-0c55b159cbfafe1f0"
  tags = {
    Name = "Web Server"
    Role = "Web"
  }
}

# Использование вывода модуля
resource "aws_eip" "web" {
  instance = module.web_server.instance_id  # Использование output модуля
}
```

## Best Practices

1. **Версионирование провайдеров** в `versions.tf`
2. **Использование модулей** для переиспользования
3. **Remote state** для командной работы
4. **Workspaces** для разных окружений

---

*Обновлено: 2026-01-25*
