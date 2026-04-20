---
title: "Вопросы на собеседовании: Terraform"
description: "Полное покрытие Terraform для подготовки к собеседованию: IaC концепции, HCL синтаксис, state management, модули, workspaces, CI/CD интеграция, Terraform Cloud, управление секретами, best practices."
tags:
  - interview
  - devops
  - terraform-interview
aliases:
  - "Terraform interview"
  - "Terraform собеседование"
  - "Terraform вопросы"
  - "IaC Terraform"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `Terraform`

Полное покрытие `Terraform` для подготовки к собеседованию: концепции `IaC`, `HCL`-синтаксис, управление `state`, модули, `workspaces`, `CI/CD`-интеграция, `Terraform Cloud`, работа в команде, управление секретами и сравнение с альтернативами.

**`Terraform`** (HashiCorp) — инструмент `Infrastructure as Code` с декларативным подходом: описываешь желаемое состояние инфраструктуры в `HCL`-файлах, а `Terraform` сам вычисляет план изменений и применяет его. Поддерживает сотни `provider`-ов (AWS, GCP, Azure, Kubernetes и др.). Является де-факто стандартом для управления облачной инфраструктурой в среде [[kubernetes-interview|Kubernetes]], [[pipeline-design-interview|CI/CD-пайплайнов]] и [[microservices-interview|микросервисных]] платформ.

## Полезные ссылки

### Официальная документация

- [Terraform Documentation](https://developer.hashicorp.com/terraform/docs) — официальная документация
- [Terraform CLI Reference](https://developer.hashicorp.com/terraform/cli) — справочник команд CLI
- [Terraform Registry](https://registry.terraform.io/) — реестр провайдеров и модулей
- [HCL Language Reference](https://developer.hashicorp.com/terraform/language) — справочник языка HCL
- [Terraform Best Practices](https://developer.hashicorp.com/terraform/cloud-docs/recommended-practices) — рекомендованные практики
- [Terraform Module Development](https://developer.hashicorp.com/terraform/language/modules/develop) — разработка модулей

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы IaC и Terraform**
- [Q1. (!) Что такое Infrastructure as Code и зачем он нужен?](#q1--что-такое-infrastructure-as-code-и-зачем-он-нужен)
- [Q2. (!) Что такое Terraform и чем он отличается от других IaC-инструментов?](#q2--что-такое-terraform-и-чем-он-отличается-от-других-iac-инструментов)
- [Q3. В чём разница между декларативным и императивным подходом в IaC?](#q3-в-чём-разница-между-декларативным-и-императивным-подходом-в-iac)
- [Q4. Как Terraform определяет изменения, которые нужно применить?](#q4-как-terraform-определяет-изменения-которые-нужно-применить)

**Ключевые концепции HCL**
- [Q5. (!) Что такое provider в Terraform?](#q5--что-такое-provider-в-terraform)
- [Q6. (!) Что такое resource и data source?](#q6--что-такое-resource-и-data-source)
- [Q7. (!) Что такое variable и output в Terraform?](#q7--что-такое-variable-и-output-в-terraform)
- [Q8. Что такое locals и когда их использовать?](#q8-что-такое-locals-и-когда-их-использовать)
- [Q9. (!) Чем отличается count от for_each?](#q9--чем-отличается-count-от-for_each)
- [Q10. Что такое dynamic блок?](#q10-что-такое-dynamic-блок)
- [Q11. Как работают expressions и функции в HCL?](#q11-как-работают-expressions-и-функции-в-hcl)
- [Q12. Что такое depends_on и lifecycle?](#q12-что-такое-depends_on-и-lifecycle)

**State: управление состоянием**
- [Q13. (!) Что такое Terraform state и зачем он нужен?](#q13--что-такое-terraform-state-и-зачем-он-нужен)
- [Q14. (!) В чём разница между local и remote backend?](#q14--в-чём-разница-между-local-и-remote-backend)
- [Q15. (!) Что такое state locking и зачем он нужен?](#q15--что-такое-state-locking-и-зачем-он-нужен)
- [Q16. Как настроить remote backend на базе S3 и DynamoDB?](#q16-как-настроить-remote-backend-на-базе-s3-и-dynamodb)
- [Q17. Какие операции доступны через команду terraform state?](#q17-какие-операции-доступны-через-команду-terraform-state)
- [Q18. Что такое terraform import и когда он применяется?](#q18-что-такое-terraform-import-и-когда-он-применяется)

**Основные команды CLI**
- [Q19. (!) Опишите полный рабочий цикл команд Terraform](#q19--опишите-полный-рабочий-цикл-команд-terraform)
- [Q20. Что делает terraform plan -out и почему это важно для CI/CD?](#q20-что-делает-terraform-plan--out-и-почему-это-важно-для-cicd)
- [Q21. Как использовать terraform console для отладки?](#q21-как-использовать-terraform-console-для-отладки)
- [Q22. Что такое TF_LOG и какие уровни логирования есть?](#q22-что-такое-tf_log-и-какие-уровни-логирования-есть)

**Модули**
- [Q23. (!) Что такое модуль в Terraform и зачем он нужен?](#q23--что-такое-модуль-в-terraform-и-зачем-он-нужен)
- [Q24. Как создать и использовать собственный модуль?](#q24-как-создать-и-использовать-собственный-модуль)
- [Q25. (!) Как подключить модуль из Terraform Registry или Git?](#q25--как-подключить-модуль-из-terraform-registry-или-git)
- [Q26. Как версионировать модули?](#q26-как-версионировать-модули)

**Workspaces и управление окружениями**
- [Q27. (!) Что такое Terraform Workspace?](#q27--что-такое-terraform-workspace)
- [Q28. В чём разница между подходом workspace и разделением по директориям?](#q28-в-чём-разница-между-подходом-workspace-и-разделением-по-директориям)

**Backend и Terraform Cloud**
- [Q29. (!) Что такое Terraform Cloud и какие возможности он предоставляет?](#q29--что-такое-terraform-cloud-и-какие-возможности-он-предоставляет)
- [Q30. В чём разница между Terraform Cloud и Terraform Enterprise?](#q30-в-чём-разница-между-terraform-cloud-и-terraform-enterprise)

**Работа в команде и CI/CD**
- [Q31. (!) Как организовать работу с Terraform в команде?](#q31--как-организовать-работу-с-terraform-в-команде)
- [Q32. Как интегрировать Terraform в CI/CD-пайплайн?](#q32-как-интегрировать-terraform-в-cicd-пайплайн)

**Управление секретами**
- [Q33. (!) Как управлять секретами в Terraform?](#q33--как-управлять-секретами-в-terraform)

**Сравнение с альтернативами**
- [Q34. (!) Чем Terraform отличается от Ansible, Pulumi и CloudFormation?](#q34--чем-terraform-отличается-от-ansible-pulumi-и-cloudformation)

**Best practices**
- [Q35. (!) Каковы best practices структуры Terraform-проекта?](#q35--каковы-best-practices-структуры-terraform-проекта)

**Расширенные возможности и экосистема**
- [Q36. Что такое Terraform Cloud и HCP Terraform — возможности и отличия?](#q36-что-такое-terraform-cloud-и-hcp-terraform--возможности-и-отличия)
- [Q37. Что такое OpenTofu — форк Terraform, отличия и миграция?](#q37-что-такое-opentofu--форк-terraform-отличия-и-миграция)
- [Q38. Как тестировать Terraform-код — terraform test и Terratest?](#q38-как-тестировать-terraform-код--terraform-test-и-terratest)
- [Q39. Что такое drift detection в Terraform и как с ним работать?](#q39-что-такое-drift-detection-в-terraform-и-как-с-ним-работать)
- [Q40. Как импортировать существующую инфраструктуру в Terraform?](#q40-как-импортировать-существующую-инфраструктуру-в-terraform)
- [Q41. Что такое moved блок в Terraform и зачем он нужен?](#q41-что-такое-moved-блок-в-terraform-и-зачем-он-нужен)
- [Q42. Как управлять секретами в Terraform — Vault provider и переменные окружения?](#q42-как-управлять-секретами-в-terraform--vault-provider-и-переменные-окружения)

---

## Q1. (!) Что такое Infrastructure as Code и зачем он нужен?

**Infrastructure as Code** (`IaC`) — практика управления инфраструктурой через машиночитаемые файлы конфигурации вместо ручных операций.

**Преимущества:**
- **Воспроизводимость** — идентичная инфраструктура для dev/staging/prod.
- **Версионирование** — история изменений в `git`, возможность rollback.
- **Автоматизация** — интеграция с `CI/CD`, устранение человеческих ошибок.
- **Документирование** — код является актуальной документацией инфраструктуры.
- **Масштабируемость** — тиражирование окружений без ручного труда.

**Подходы:**
- **Декларативный** (`Terraform`, `CloudFormation`) — описываешь *что* хочешь получить.
- **Императивный** (`Ansible`, скрипты) — описываешь *как* достичь результата.

---

## Q2. (!) Что такое Terraform и чем он отличается от других IaC-инструментов?

**Terraform** — инструмент `IaC` от HashiCorp с открытым исходным кодом (лицензия `BSL 1.1` с версии 1.6; форк `OpenTofu` — под `MPL 2.0`).

**Ключевые характеристики:**
- **Декларативный** — описываешь конечное состояние в `HCL`.
- **Мультиоблачный** — поддерживает 3000+ провайдеров.
- **State-based** — хранит текущее состояние инфраструктуры в `terraform.tfstate`.
- **Plan → Apply** — двухфазное применение изменений.
- **Идемпотентность** — повторный `apply` на уже применённой конфигурации ничего не меняет.

**Отличия от конкурентов:**

| Инструмент | Подход | Охват | Язык |
|------------|--------|-------|------|
| `Terraform` | Декларативный | Мультиоблачный | `HCL` |
| `Ansible` | Императивный (агентлесс) | Конфигурация ОС | `YAML` |
| `Pulumi` | Декларативный/Императивный | Мультиоблачный | Python/TS/Go |
| `CloudFormation` | Декларативный | Только AWS | `JSON`/`YAML` |

---

## Q3. В чём разница между декларативным и императивным подходом в IaC?

**Декларативный подход** (Terraform, CloudFormation):
- Описываешь *желаемое конечное состояние*.
- Инструмент сам вычисляет необходимые шаги.
- Идемпотентен по природе.
- Пример: "У меня должны быть 3 инстанса EC2 с такими-то параметрами".

**Императивный подход** (Ansible, Bash):
- Описываешь *последовательность действий* для достижения результата.
- Разработчик контролирует порядок операций.
- Идемпотентность требует явных проверок (`when: not exists`).
- Пример: "Выполни команду создания инстанса EC2".

На практике `Ansible` поддерживает элементы декларативности (`state: present/absent`), а `Terraform` позволяет использовать `local-exec` для императивных действий.

---

## Q4. Как Terraform определяет изменения, которые нужно применить?

`Terraform` использует трёхстороннее сравнение (`three-way diff`):

1. **Desired state** — конфигурация в `.tf`-файлах.
2. **Known state** — последнее известное состояние в `terraform.tfstate`.
3. **Actual state** — реальное состояние ресурсов в API провайдера (обновляется при `terraform refresh`).

**Алгоритм:**
1. `terraform plan` читает `.tf`-файлы и state.
2. Опционально вызывает `refresh` (по умолчанию в `plan`).
3. Сравнивает desired vs actual state.
4. Строит dependency graph (DAG).
5. Генерирует план: `+` create, `-` destroy, `~` update, `-/+` replace.

---

## Q5. (!) Что такое provider в Terraform?

**Provider** — плагин, реализующий взаимодействие `Terraform` с конкретным API (AWS, GCP, Azure, Kubernetes, GitHub и др.). Каждый провайдер регистрирует набор `resource` и `data source` типов.

```hcl
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  required_version = ">= 1.5.0"
}

provider "aws" {
  region = var.aws_region
}
```

**Принципы:**
- Провайдеры скачиваются при `terraform init` из `registry.terraform.io`.
- Версия фиксируется в `.terraform.lock.hcl` (аналог `package-lock.json`).
- Можно подключить несколько провайдеров одного типа с помощью `alias`.

```hcl
provider "aws" {
  alias  = "us_east"
  region = "us-east-1"
}

provider "aws" {
  alias  = "eu_west"
  region = "eu-west-1"
}
```

---

## Q6. (!) Что такое resource и data source?

**Resource** — управляемый ресурс инфраструктуры, который `Terraform` создаёт, изменяет и удаляет.

```hcl
resource "aws_s3_bucket" "my_bucket" {
  bucket = "my-unique-bucket-name"
  tags = {
    Environment = "prod"
  }
}
```

**Data source** — источник данных для чтения информации о существующих ресурсах (не создаёт и не изменяет их).

```hcl
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-*-22.04-amd64-server-*"]
  }
}

resource "aws_instance" "web" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t3.micro"
}
```

**Ключевое различие:** `resource` — управляемое состояние; `data` — только чтение внешних данных.

---

## Q7. (!) Что такое variable и output в Terraform?

**Variable** — входной параметр конфигурации. Позволяет параметризировать модули и окружения.

```hcl
variable "instance_type" {
  type        = string
  description = "EC2 instance type"
  default     = "t3.micro"
  validation {
    condition     = contains(["t3.micro", "t3.small", "t3.medium"], var.instance_type)
    error_message = "Must be a valid instance type."
  }
}
```

Источники значений (приоритет от высшего к низшему):
1. `-var` и `-var-file` флаги CLI
2. Переменные окружения `TF_VAR_<name>`
3. `terraform.tfvars` / `*.auto.tfvars`
4. Значение `default` в объявлении

**Output** — экспортируемые значения модуля или корневого конфига.

```hcl
output "bucket_arn" {
  value       = aws_s3_bucket.my_bucket.arn
  description = "ARN of the S3 bucket"
  sensitive   = false
}
```

`output` используется для передачи данных между модулями или в `CI/CD`-пайплайн.

---

## Q8. Что такое locals и когда их использовать?

**Locals** — локальные именованные значения внутри конфигурации. Вычисляются один раз и не являются входными параметрами.

```hcl
locals {
  env         = terraform.workspace
  common_tags = {
    Environment = local.env
    Project     = var.project_name
    ManagedBy   = "terraform"
  }
  bucket_name = "${var.project_name}-${local.env}-assets"
}

resource "aws_s3_bucket" "assets" {
  bucket = local.bucket_name
  tags   = local.common_tags
}
```

**Когда использовать:**
- Вынос повторяющихся выражений.
- Вычисляемые значения, не являющиеся входными параметрами.
- Составные теги и метаданные.
- Нормализация значений (`lower()`, `replace()`).

---

## Q9. (!) Чем отличается count от for_each?

**count** — создаёт N одинаковых ресурсов, адресуемых по индексу.

```hcl
resource "aws_instance" "server" {
  count         = 3
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t3.micro"
  tags = {
    Name = "server-${count.index}"
  }
}
# Адрес: aws_instance.server[0], aws_instance.server[1], ...
```

**for_each** — создаёт ресурсы по коллекции (map или set of strings), адресуемые по ключу.

```hcl
variable "servers" {
  default = {
    web = "t3.micro"
    api = "t3.small"
    db  = "t3.medium"
  }
}

resource "aws_instance" "server" {
  for_each      = var.servers
  ami           = data.aws_ami.ubuntu.id
  instance_type = each.value
  tags = {
    Name = each.key
  }
}
# Адрес: aws_instance.server["web"], aws_instance.server["api"], ...
```

**Ключевое различие:** при удалении элемента из середины `count`-массива `Terraform` пересчитывает индексы и пересоздаёт ресурсы. `for_each` обращается по стабильному ключу — безопаснее для production-инфраструктуры.

---

## Q10. Что такое dynamic блок?

**Dynamic блок** позволяет динамически генерировать вложенные блоки конфигурации на основе коллекции.

```hcl
variable "ingress_rules" {
  default = [
    { port = 80,  protocol = "tcp", cidr = "0.0.0.0/0" },
    { port = 443, protocol = "tcp", cidr = "0.0.0.0/0" },
    { port = 22,  protocol = "tcp", cidr = "10.0.0.0/8" },
  ]
}

resource "aws_security_group" "web" {
  name = "web-sg"

  dynamic "ingress" {
    for_each = var.ingress_rules
    content {
      from_port   = ingress.value.port
      to_port     = ingress.value.port
      protocol    = ingress.value.protocol
      cidr_blocks = [ingress.value.cidr]
    }
  }
}
```

Используется для вложенных блоков (`ingress`, `egress`, `tag`, `setting` и др.), которые нельзя параметризировать через простые атрибуты.

---

## Q11. Как работают expressions и функции в HCL?

`HCL` поддерживает встроенные функции для преобразования данных.

```hcl
locals {
  # Строковые функции
  upper_env = upper(var.environment)          # "PROD"
  trimmed   = trimspace("  hello  ")          # "hello"
  joined    = join("-", ["app", "api", "v1"]) # "app-api-v1"

  # Коллекции
  filtered  = [for s in var.servers : s if s != "debug"]
  mapped    = { for k, v in var.tags : k => upper(v) }
  flat      = flatten([[1, 2], [3, 4]])       # [1, 2, 3, 4]

  # Числа и логика
  max_count = max(3, 5, 2)                    # 5
  coalesced = coalesce(var.name, "default")   # первое непустое

  # Кодирование
  encoded   = base64encode("secret")
  json_out  = jsonencode({ key = "value" })
}
```

**Условные выражения:**

```hcl
instance_type = var.environment == "prod" ? "t3.medium" : "t3.micro"
```

---

## Q12. Что такое depends_on и lifecycle?

**depends_on** — явная зависимость между ресурсами, когда неявная (через атрибуты) недостаточна.

```hcl
resource "aws_iam_role_policy_attachment" "attach" {
  role       = aws_iam_role.role.name
  policy_arn = aws_iam_policy.policy.arn
}

resource "aws_lambda_function" "fn" {
  # Lambda должна создаться ПОСЛЕ того, как роль получит политику
  depends_on = [aws_iam_role_policy_attachment.attach]
  # ...
}
```

**lifecycle** — управление поведением ресурса при изменениях.

```hcl
resource "aws_db_instance" "main" {
  # ...
  lifecycle {
    # Создать новый ресурс перед удалением старого (zero-downtime)
    create_before_destroy = true

    # Запретить случайное удаление
    prevent_destroy = true

    # Игнорировать изменения тегов, внесённые вне Terraform
    ignore_changes = [tags["LastModified"], password]

    # Дополнительные проверки после apply
    postcondition {
      condition     = self.status == "available"
      error_message = "DB instance must be available after creation."
    }
  }
}
```

---

## Q13. (!) Что такое Terraform state и зачем он нужен?

**Terraform state** — файл `terraform.tfstate` (формат `JSON`), хранящий маппинг между ресурсами в конфигурации и реальными объектами в провайдере.

**Зачем нужен:**
- `Terraform` знает, какие ресурсы он создавал — чтобы обновлять их, а не пересоздавать.
- Позволяет строить dependency graph и вычислять план изменений.
- Хранит metadata: `id` ресурса, все атрибуты, зависимости.

**Что содержит:**
```json
{
  "version": 4,
  "terraform_version": "1.6.0",
  "resources": [
    {
      "type": "aws_s3_bucket",
      "name": "my_bucket",
      "instances": [
        {
          "attributes": {
            "id": "my-unique-bucket-name",
            "arn": "arn:aws:s3:::my-unique-bucket-name"
          }
        }
      ]
    }
  ]
}
```

**Важно:** state может содержать чувствительные данные (пароли, токены) — нельзя хранить в `git`.

---

## Q14. (!) В чём разница между local и remote backend?

**Local backend** (по умолчанию):
- `terraform.tfstate` хранится локально на диске.
- Подходит только для одиночной разработки.
- Нет блокировки, нет шифрования.

**Remote backend**:
- State хранится централизованно (S3, GCS, Azure Blob, Terraform Cloud).
- Поддерживает блокировку (state locking).
- Шифрование at rest и in transit.
- Общий доступ для команды.

**Популярные remote backends:**

| Backend | Хранилище | Locking | Описание |
|---------|-----------|---------|----------|
| `s3` | AWS S3 | DynamoDB | Самый популярный для AWS |
| `gcs` | GCS | Встроенный | Для GCP |
| `azurerm` | Azure Blob | Lease-based | Для Azure |
| `remote` | Terraform Cloud | Встроенный | HashiCorp-managed |
| `http` | Любой HTTP | Встроенный | Универсальный |

---

## Q15. (!) Что такое state locking и зачем он нужен?

**State locking** — механизм блокировки файла state на время выполнения операций `plan` и `apply`, предотвращающий одновременные изменения от нескольких пользователей или `CI/CD`-агентов.

**Без locking:**
- Два инженера одновременно запускают `apply` → race condition → corrupt state → потеря ресурсов.

**С locking (S3 + DynamoDB):**
1. `Terraform` создаёт запись в `DynamoDB` с `LockID`.
2. Второй вызов обнаруживает блокировку и завершается ошибкой.
3. После завершения операции блокировка снимается.

```hcl
terraform {
  backend "s3" {
    bucket         = "my-terraform-state"
    key            = "prod/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "terraform-lock"  # таблица для locking
  }
}
```

**Снятие зависшей блокировки:**
```bash
terraform force-unlock <LOCK_ID>
```

---

## Q16. Как настроить remote backend на базе S3 и DynamoDB?

**Шаг 1 — создать ресурсы** (отдельный `bootstrap`-проект или вручную):

```hcl
resource "aws_s3_bucket" "terraform_state" {
  bucket = "my-company-terraform-state"
}

resource "aws_s3_bucket_versioning" "state_versioning" {
  bucket = aws_s3_bucket.terraform_state.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "state_enc" {
  bucket = aws_s3_bucket.terraform_state.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_dynamodb_table" "terraform_lock" {
  name         = "terraform-lock"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"
  attribute {
    name = "LockID"
    type = "S"
  }
}
```

**Шаг 2 — настроить backend в основном проекте:**

```hcl
terraform {
  backend "s3" {
    bucket         = "my-company-terraform-state"
    key            = "services/api/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "terraform-lock"
  }
}
```

---

## Q17. Какие операции доступны через команду terraform state?

```bash
# Просмотр всех ресурсов в state
terraform state list

# Просмотр атрибутов конкретного ресурса
terraform state show aws_s3_bucket.my_bucket

# Перемещение ресурса (переименование без пересоздания)
terraform state mv aws_instance.old aws_instance.new

# Удаление ресурса из state (без удаления реального ресурса)
terraform state rm aws_s3_bucket.legacy

# Скачать state файл
terraform state pull > backup.tfstate

# Загрузить state файл
terraform state push backup.tfstate
```

**Практические применения:**
- `state mv` — рефакторинг без destroy/create.
- `state rm` — "забыть" о ресурсе, не удаляя его.
- `state pull/push` — ручное управление state при миграции.

---

## Q18. Что такое terraform import и когда он применяется?

`terraform import` позволяет взять под управление `Terraform` ресурс, который уже существует в провайдере, но не описан в state.

**Сценарий:** ресурс создан вручную в AWS Console, нужно перенести его в `Terraform`.

**Шаг 1 — написать конфигурацию (placeholder):**

```hcl
resource "aws_s3_bucket" "existing" {
  bucket = "already-existing-bucket"
}
```

**Шаг 2 — импортировать:**

```bash
terraform import aws_s3_bucket.existing already-existing-bucket
```

**Шаг 3 — выполнить plan и доработать конфигурацию** до нулевого diff.

**Начиная с Terraform 1.5** доступен блок `import` прямо в HCL:

```hcl
import {
  to = aws_s3_bucket.existing
  id = "already-existing-bucket"
}
```

---

## Q19. (!) Опишите полный рабочий цикл команд Terraform

```bash
# 1. Инициализация: скачать провайдеры, настроить backend
terraform init

# 2. Форматирование кода (опционально)
terraform fmt -recursive

# 3. Валидация синтаксиса и схемы
terraform validate

# 4. Планирование изменений
terraform plan

# 4a. Сохранить план в файл (для CI/CD)
terraform plan -out=tfplan

# 5. Применение изменений
terraform apply

# 5a. Применить сохранённый план (детерминированно)
terraform apply tfplan

# 6. Уничтожение ресурсов
terraform destroy

# 6a. Уничтожение конкретного ресурса
terraform destroy -target=aws_instance.web
```

**Флаги plan:**
- `-var="key=value"` — переопределить переменную.
- `-var-file=prod.tfvars` — загрузить файл переменных.
- `-refresh=false` — не обновлять state из API (быстрее, но может быть неточно).
- `-target=resource` — применить только для конкретного ресурса.

---

## Q20. Что делает terraform plan -out и почему это важно для CI/CD?

```bash
terraform plan -out=tfplan.binary
terraform apply tfplan.binary
```

**Почему важно:**
1. `plan` и `apply` разделены во времени → отдельные шаги в пайплайне.
2. `apply` применяет **ровно тот план**, который был проверен → нет сюрпризов.
3. Между `plan` и `apply` может пройти review (PR-комментарий с diff).
4. Файл плана бинарный и подписанный — нельзя подменить.

**Типичный CI/CD flow:**
```
PR → terraform plan -out=tfplan → review → merge → terraform apply tfplan
```

---

## Q21. Как использовать terraform console для отладки?

`terraform console` — интерактивный REPL для вычисления выражений `HCL` в контексте текущего state.

```bash
terraform console
```

```hcl
# В консоли:
> var.environment
"prod"

> length(var.subnet_ids)
3

> join("-", ["app", var.environment])
"app-prod"

> aws_s3_bucket.my_bucket.arn
"arn:aws:s3:::my-unique-bucket-name"

> [for k, v in var.tags : "${k}=${v}"]
["Environment=prod", "Project=myapp"]
```

Полезно для отладки функций, `for`-выражений и обращения к атрибутам ресурсов.

---

## Q22. Что такое TF_LOG и какие уровни логирования есть?

`TF_LOG` — переменная окружения для включения подробного логирования `Terraform`.

```bash
# Уровни логирования (от менее к более подробному)
export TF_LOG=ERROR    # только ошибки
export TF_LOG=WARN     # предупреждения
export TF_LOG=INFO     # информационные сообщения
export TF_LOG=DEBUG    # детальная отладка
export TF_LOG=TRACE    # максимальная детализация (HTTP-запросы к API)

# Сохранить лог в файл
export TF_LOG_PATH=./terraform.log

# Логирование только провайдера
export TF_LOG_PROVIDER=DEBUG

terraform apply 2>&1 | tee apply.log
```

**Применение:** отладка ошибок аутентификации, проблем с провайдером, неожиданного поведения refresh.

---

## Q23. (!) Что такое модуль в Terraform и зачем он нужен?

**Модуль** — контейнер для набора ресурсов `Terraform`, объединённых логически. Любая директория с `.tf`-файлами является модулем.

**Типы модулей:**
- **Root module** — текущая рабочая директория (точка входа).
- **Child module** — вызываемый подмодуль.
- **Published module** — модуль в `Terraform Registry`.

**Зачем нужен:**
- **DRY** — вынос повторяющейся логики (VPC, EKS cluster, RDS).
- **Инкапсуляция** — скрытие деталей реализации.
- **Переиспользование** — стандартные модули для разных проектов/окружений.
- **Стандартизация** — принуждение к единым соглашениям.

---

## Q24. Как создать и использовать собственный модуль?

**Структура модуля:**

```
modules/
  vpc/
    main.tf        # ресурсы
    variables.tf   # входные параметры
    outputs.tf     # выходные значения
    README.md
```

**modules/vpc/variables.tf:**
```hcl
variable "cidr_block" {
  type    = string
  default = "10.0.0.0/16"
}

variable "environment" {
  type = string
}
```

**modules/vpc/main.tf:**
```hcl
resource "aws_vpc" "main" {
  cidr_block = var.cidr_block
  tags = {
    Name        = "${var.environment}-vpc"
    Environment = var.environment
  }
}
```

**modules/vpc/outputs.tf:**
```hcl
output "vpc_id" {
  value = aws_vpc.main.id
}
```

**Использование модуля:**
```hcl
module "vpc" {
  source      = "./modules/vpc"
  cidr_block  = "10.1.0.0/16"
  environment = "prod"
}

output "vpc_id" {
  value = module.vpc.vpc_id
}
```

---

## Q25. (!) Как подключить модуль из Terraform Registry или Git?

**Terraform Registry (публичный):**
```hcl
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.0"

  cluster_name    = "my-cluster"
  cluster_version = "1.29"
}
```

**Git репозиторий:**
```hcl
# По тегу
module "vpc" {
  source = "git::https://github.com/myorg/terraform-modules.git//vpc?ref=v1.2.0"
}

# По ветке
module "vpc" {
  source = "git::ssh://git@github.com/myorg/terraform-modules.git//vpc?ref=main"
}
```

**Приватный Registry (Terraform Cloud / Enterprise):**
```hcl
module "vpc" {
  source  = "app.terraform.io/my-org/vpc/aws"
  version = "1.0.0"
}
```

После добавления или изменения источника модуля необходимо выполнить `terraform init`.

---

## Q26. Как версионировать модули?

**Рекомендуемый подход — Git tags по semver:**

```bash
# Создать тег для новой версии модуля
git tag -a v1.2.0 -m "Add subnet outputs"
git push origin v1.2.0
```

**Использование конкретной версии:**
```hcl
module "vpc" {
  source = "git::https://github.com/myorg/tf-modules.git//vpc?ref=v1.2.0"
}
```

**Стратегии версионирования:**
- `ref=v1.2.0` — точная версия (рекомендуется для prod).
- `ref=main` — последний commit (только для dev).
- `version = "~> 1.2"` — patch updates (только для Registry).

**Для Registry-модулей:** версия берётся из Git-тега при публикации. `.terraform.lock.hcl` фиксирует хэш.

---

## Q27. (!) Что такое Terraform Workspace?

**Workspace** — изолированный экземпляр state в рамках одной конфигурации. По умолчанию существует workspace `default`.

```bash
# Список воркспейсов
terraform workspace list

# Создать новый
terraform workspace new staging

# Переключиться
terraform workspace select prod

# Удалить
terraform workspace delete staging
```

**Использование в конфигурации:**
```hcl
locals {
  env_config = {
    default = { instance_type = "t3.micro",  replicas = 1 }
    staging = { instance_type = "t3.small",  replicas = 2 }
    prod    = { instance_type = "t3.medium", replicas = 3 }
  }
  config = local.env_config[terraform.workspace]
}

resource "aws_instance" "app" {
  count         = local.config.replicas
  instance_type = local.config.instance_type
}
```

**State хранится отдельно:**
- `terraform.tfstate.d/staging/terraform.tfstate`
- `terraform.tfstate.d/prod/terraform.tfstate`

---

## Q28. В чём разница между подходом workspace и разделением по директориям?

| Критерий | Workspaces | Директории |
|----------|-----------|------------|
| State | Отдельный per-workspace | Отдельный per-директории |
| Конфигурация | Общая (одна) | Может отличаться |
| Backend | Один | Независимые |
| Гибкость | Ниже | Выше |
| Риск | Выше (общий код) | Ниже (изоляция) |
| Сложность | Меньше файлов | Больше файлов |

**Workspaces рекомендуются когда:**
- Окружения идентичны по конфигурации, отличаются только параметрами.
- Проект небольшой / одна команда.

**Директории рекомендуются когда:**
- Окружения могут существенно отличаться.
- Разные команды управляют разными окружениями.
- Нужна максимальная изоляция (разные AWS accounts).

**Популярная структура (Terragrunt-style):**
```
environments/
  dev/
    main.tf
    terraform.tfvars
  staging/
    main.tf
    terraform.tfvars
  prod/
    main.tf
    terraform.tfvars
modules/
  vpc/
  eks/
```

---

## Q29. (!) Что такое Terraform Cloud и какие возможности он предоставляет?

**Terraform Cloud** (TFC) — managed-платформа HashiCorp для совместного использования `Terraform`.

**Ключевые возможности:**
- **Remote state** — хранение и версионирование state с шифрованием.
- **Remote execution** — `plan` и `apply` выполняются в облаке, не на локальной машине.
- **State locking** — встроенный механизм блокировки.
- **VCS Integration** — автоматический `plan` при push в GitHub/GitLab/Bitbucket.
- **Policy as Code** — `Sentinel` для проверки compliance-правил перед `apply`.
- **Private Registry** — хранение приватных модулей и провайдеров.
- **SSO** — интеграция с корпоративным IdP.
- **Audit logging** — полная история операций.

**Конфигурация:**
```hcl
terraform {
  cloud {
    organization = "my-org"
    workspaces {
      name = "my-app-prod"
    }
  }
}
```

---

## Q30. В чём разница между Terraform Cloud и Terraform Enterprise?

| Параметр | Terraform Cloud | Terraform Enterprise |
|----------|----------------|---------------------|
| Размещение | SaaS (HashiCorp) | Self-hosted (on-prem / private cloud) |
| Бесплатный план | Да (до 5 пользователей) | Нет (только платная лицензия) |
| Контроль данных | Данные у HashiCorp | Данные у клиента |
| Compliance | Ограничен | Full (HIPAA, FedRAMP и др.) |
| SSO | Только платный план | Включён |
| Sentinel | Платный план | Включён |
| Network | Публичный интернет | Изолированная сеть |

**Выбор:** `Terraform Cloud` — для большинства компаний; `Enterprise` — для regulated industries с требованиями к data residency.

---

## Q31. (!) Как организовать работу с Terraform в команде?

**Ключевые принципы:**

1. **Remote state** — S3/GCS/Terraform Cloud, никакого local state в git.
2. **State locking** — DynamoDB или встроенный механизм.
3. **`.terraform.lock.hcl` в git** — фиксирует версии провайдеров.
4. **`.gitignore`** — исключить `*.tfstate`, `*.tfstate.backup`, `.terraform/`, `*.tfplan`.

```gitignore
# .gitignore для Terraform
.terraform/
*.tfstate
*.tfstate.backup
*.tfplan
.terraform.tfvars
override.tf
```

5. **Code review** — все изменения через PR с `terraform plan` в комментарии.
6. **Модульность** — выносить переиспользуемые компоненты в модули.
7. **Разделение state** — разные state-файлы для разных сервисов и окружений.
8. **Политики** — `Sentinel` или `Open Policy Agent` для governance.

---

## Q32. Как интегрировать Terraform в CI/CD-пайплайн?

**Типичный pipeline (GitHub Actions):**

```yaml
name: Terraform

on:
  pull_request:
    paths: ['terraform/**']
  push:
    branches: [main]
    paths: ['terraform/**']

jobs:
  plan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: hashicorp/setup-terraform@v3
        with:
          terraform_version: "1.6.0"

      - name: Terraform Init
        run: terraform init
        working-directory: ./terraform
        env:
          AWS_ACCESS_KEY_ID: ${{ secrets.AWS_ACCESS_KEY_ID }}
          AWS_SECRET_ACCESS_KEY: ${{ secrets.AWS_SECRET_ACCESS_KEY }}

      - name: Terraform Validate
        run: terraform validate
        working-directory: ./terraform

      - name: Terraform Plan
        run: terraform plan -out=tfplan -no-color 2>&1 | tee plan.txt
        working-directory: ./terraform

      - name: Comment PR
        uses: actions/github-script@v7
        with:
          script: |
            const plan = require('fs').readFileSync('terraform/plan.txt', 'utf8');
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: '```\n' + plan.slice(-65000) + '\n```'
            });

  apply:
    needs: plan
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    environment: production
    steps:
      - name: Terraform Apply
        run: terraform apply -auto-approve tfplan
```

---

## Q33. (!) Как управлять секретами в Terraform?

**Проблема:** `Terraform` может хранить чувствительные данные в state-файле в открытом виде.

**Стратегии:**

**1. Переменные окружения:**
```bash
export TF_VAR_db_password="$(aws secretsmanager get-secret-value --secret-id prod/db/password --query SecretString --output text)"
```

**2. Пометка переменных как sensitive:**
```hcl
variable "db_password" {
  type      = string
  sensitive = true  # скрывает из вывода plan/apply
}

output "db_endpoint" {
  value     = aws_db_instance.main.endpoint
  sensitive = false
}
```

**3. AWS Secrets Manager / HashiCorp Vault:**
```hcl
data "aws_secretsmanager_secret_version" "db_pass" {
  secret_id = "prod/db/password"
}

resource "aws_db_instance" "main" {
  password = data.aws_secretsmanager_secret_version.db_pass.secret_string
}
```

**4. Шифрование state:**
- S3 backend с SSE-S3 или SSE-KMS.
- Terraform Cloud шифрует state автоматически.

**5. Terraform 1.7+ State Encryption:**
```hcl
terraform {
  encryption {
    key_provider "pbkdf2" "main" {
      passphrase = var.state_passphrase
    }
    method "aes_gcm" "main" {
      keys = key_provider.pbkdf2.main
    }
    state {
      method = method.aes_gcm.main
    }
  }
}
```

**Антипаттерны:**
- Хранить `*.tfstate` в git.
- Хардкодить секреты в `.tf`-файлах.
- Хранить `terraform.tfvars` с секретами в git.

---

## Q34. (!) Чем Terraform отличается от Ansible, Pulumi и CloudFormation?

**Terraform vs Ansible:**
- `Terraform` — provisioning инфраструктуры (создание облачных ресурсов).
- `Ansible` — configuration management (настройка ОС, установка ПО).
- Часто используются вместе: `Terraform` создаёт VM → `Ansible` настраивает её.
- `Ansible` агентлесс (SSH/WinRM), `Terraform` работает через API провайдеров.

**Terraform vs Pulumi:**
- `Pulumi` — IaC на привычных языках (Python, TypeScript, Go, C#) — больше гибкости.
- `Terraform` — декларативный `HCL` — проще для начала, меньше boilerplate.
- `Pulumi` лучше для сложной логики (циклы, условия, ООП).
- `Terraform` — зрелее, больше провайдеров, огромная экосистема.
- `Pulumi` может использовать `Terraform`-провайдеры через `pulumi-terraform-bridge`.

**Terraform vs CloudFormation:**
- `CloudFormation` — только AWS (нет мультиоблачности).
- `CloudFormation` — нативная интеграция с AWS, нет отдельного state-файла.
- `Terraform` — мультиоблачный, единый инструмент для AWS + GCP + Azure + Kubernetes.
- `CloudFormation` бесплатен, `Terraform Cloud` — платный (есть free tier).
- `CloudFormation` медленнее применяет изменения (polling stack events).

---

## Q35. (!) Каковы best practices структуры Terraform-проекта?

**Рекомендуемая структура для монорепо:**

```
terraform/
  modules/           # переиспользуемые модули
    vpc/
      main.tf
      variables.tf
      outputs.tf
    eks/
    rds/
  environments/      # конфигурации окружений
    dev/
      main.tf        # вызовы модулей
      variables.tf
      terraform.tfvars
      backend.tf
    staging/
    prod/
  global/            # глобальные ресурсы (IAM, Route53)
```

**Соглашения именования:**
```hcl
# Ресурсы: <тип>_<имя>
resource "aws_s3_bucket" "app_assets" {}

# Переменные: snake_case
variable "instance_count" {}

# Теги: все ресурсы имеют базовые теги
tags = merge(local.common_tags, {
  Name = "${local.prefix}-instance"
})
```

**Чек-лист best practices:**
- `terraform fmt` и `terraform validate` в pre-commit hook.
- `tflint` — статический анализатор для Terraform.
- `tfsec` / `checkov` — security scanning.
- `terratest` — тестирование модулей на Go.
- Разделять state по сервисам: не держать всю инфраструктуру в одном state.
- `required_version` и `required_providers` — всегда указывать версии.
- Использовать `terraform-docs` для автогенерации документации модулей.
- Один state = одна `blast radius` (зона риска при ошибке).

---

## Q36. Что такое Terraform Cloud и HCP Terraform — возможности и отличия?

**Terraform Cloud** (теперь **HCP Terraform** — HashiCorp Cloud Platform) — managed-сервис для запуска Terraform в команде с remote state, remote runs и policy enforcement.

### Ключевые возможности HCP Terraform

| Функция | Описание |
|---------|----------|
| **Remote State** | Хранение `terraform.tfstate` в облаке с версионированием и блокировкой |
| **Remote Runs** | Выполнение `plan` и `apply` на серверах HCP (не на ноутбуке) |
| **VCS Integration** | Автоматический `plan` при PR, `apply` при merge в main |
| **Workspaces** | Изолированные окружения с отдельными state и переменными |
| **Variable Sets** | Общие переменные и секреты для нескольких workspaces |
| **Sentinel Policies** | Policy as Code — запрет применения без соответствия политикам (Plus tier) |
| **Audit Logging** | Аудит всех операций (Plus/Enterprise) |
| **Private Registry** | Хостинг приватных модулей и провайдеров |

### Уровни (тарифы)

```
Free     → remote state, до 500 ресурсов
Plus     → Sentinel, SSO, audit logs, self-hosted agents
Enterprise → on-premise, кастомные требования
```

### Конфигурация backend для HCP Terraform

```hcl
terraform {
  cloud {
    organization = "my-org"
    workspaces {
      name = "production"
      # или: tags = ["production"]
    }
  }
}
```

### Чем HCP Terraform отличается от Terraform Enterprise

| | HCP Terraform | Terraform Enterprise |
|--|---------------|---------------------|
| Развёртывание | SaaS (облако) | Self-hosted (on-premise) |
| Контроль данных | У HashiCorp | Полный контроль |
| Масштабирование | Автоматическое | Требует настройки |
| Стоимость | Per-resource | По контракту |

---

## Q37. Что такое OpenTofu — форк Terraform, отличия и миграция?

**OpenTofu** — open-source форк Terraform, созданный в 2023 году сообществом в ответ на смену лицензии HashiCorp с `MPL 2.0` на `BSL 1.1` (Business Source License), ограничивающей конкурентное использование.

### Предыстория

В августе 2023 HashiCorp перевёл Terraform под `BSL 1.1`. Крупные компании (Spacelift, Env0, Gruntwork, Harness) инициировали форк под именем `OpenTofu`, который вошёл в состав **Linux Foundation**.

### Лицензионные отличия

| | Terraform | OpenTofu |
|--|-----------|----------|
| Лицензия | BSL 1.1 | MPL 2.0 |
| Ограничения | Нельзя создавать конкурирующий managed-сервис | Нет ограничений |
| Управление | HashiCorp (IBM) | Linux Foundation |
| Реестр | registry.terraform.io | registry.opentofu.org |

### Функциональные отличия (OpenTofu опережает Terraform)

- **Provider functions** — вызов функций из провайдеров (OpenTofu 1.7+)
- **State encryption** — нативное шифрование state-файла (OpenTofu 1.7+)
- **`tofu test`** — расширенный фреймворк тестирования
- **`import` блок** — появился раньше, чем в Terraform

### Миграция с Terraform на OpenTofu

```bash
# 1. Установка OpenTofu
brew install opentofu

# 2. Замена команды (полная совместимость)
tofu init       # вместо terraform init
tofu plan       # вместо terraform plan
tofu apply      # вместо terraform apply

# 3. State совместим — дополнительная конвертация не нужна
# .terraform.lock.hcl тоже совместим для большинства провайдеров
```

**Провайдеры** в OpenTofu берутся из реестра HashiCorp (совместим) или `registry.opentofu.org`.

---

## Q38. Как тестировать Terraform-код — terraform test и Terratest?

### terraform test (Terraform 1.6+ / OpenTofu 1.6+)

Встроенный фреймворк тестирования — файлы `.tftest.hcl`:

```hcl
# tests/s3_bucket.tftest.hcl

# Переменные для теста
variables {
  bucket_name = "test-bucket-12345"
  environment = "test"
}

# Блок run — применяет и проверяет
run "bucket_is_created" {
  command = plan  # или apply

  assert {
    condition     = aws_s3_bucket.this.bucket == "test-bucket-12345"
    error_message = "Bucket name must match variable"
  }

  assert {
    condition     = aws_s3_bucket.this.tags["Environment"] == "test"
    error_message = "Environment tag must be set"
  }
}

run "bucket_is_private" {
  command = apply

  assert {
    condition     = aws_s3_bucket_acl.this.acl == "private"
    error_message = "Bucket must be private"
  }
}
```

```bash
# Запуск тестов
terraform test

# Только определённый файл
terraform test -filter=tests/s3_bucket.tftest.hcl
```

### Terratest (Go)

**Terratest** — Go-библиотека для интеграционного тестирования Terraform-модулей:

```go
package test

import (
    "testing"
    "github.com/gruntwork-io/terratest/modules/terraform"
    "github.com/stretchr/testify/assert"
)

func TestS3BucketModule(t *testing.T) {
    t.Parallel()

    terraformOptions := &terraform.Options{
        TerraformDir: "../modules/s3-bucket",
        Vars: map[string]interface{}{
            "bucket_name": "test-bucket-unique-id",
            "environment": "test",
        },
    }

    // Автоматически уничтожить после теста
    defer terraform.Destroy(t, terraformOptions)

    // Применить конфигурацию
    terraform.InitAndApply(t, terraformOptions)

    // Проверить outputs
    bucketID := terraform.Output(t, terraformOptions, "bucket_id")
    assert.Equal(t, "test-bucket-unique-id", bucketID)
}
```

### Сравнение подходов

| Подход | Когда использовать |
|--------|--------------------|
| `terraform validate` | Синтаксис и базовая валидация (всегда) |
| `tflint` | Статический анализ best practices |
| `terraform test` (.tftest.hcl) | Unit/integration тесты без Go |
| `Terratest` | Полные интеграционные тесты, сложные сценарии |
| `checkov` / `tfsec` | Security scanning |

---

## Q39. Что такое drift detection в Terraform и как с ним работать?

**Drift** — расхождение между состоянием инфраструктуры в `terraform.tfstate` и реальным состоянием облачных ресурсов (кто-то вручную изменил ресурс через консоль AWS).

### Обнаружение drift

```bash
# terraform plan обнаруживает drift автоматически
terraform plan

# Пример вывода при drift:
# ~ resource "aws_instance" "web" {
#   ~ instance_type = "t3.micro" -> "t3.small"  # кто-то изменил в консоли
#   }
```

**`terraform refresh`** (устарело) / **`terraform apply -refresh-only`** — обновить state без применения изменений:

```bash
# Обновить state из реального состояния (без apply)
terraform apply -refresh-only

# Показать что изменилось в реальности (без обновления state)
terraform plan -refresh=true
```

### Автоматический drift detection в CI/CD

```yaml
# .github/workflows/drift-detection.yml
name: Drift Detection
on:
  schedule:
    - cron: '0 9 * * 1-5'  # каждый рабочий день в 9:00

jobs:
  detect-drift:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Terraform Plan (drift check)
        run: |
          terraform init
          terraform plan -detailed-exitcode
        # exit code 2 = изменения обнаружены (drift)
        # exit code 0 = всё совпадает
```

### Стратегии ответа на drift

1. **Исправить код под реальность** — обновить `.tf` файлы, commit, apply
2. **Исправить реальность под код** — `terraform apply` (вернуть к желаемому состоянию)
3. **Refresh state** — `terraform apply -refresh-only` (принять drift как новую baseline)
4. **Lifecycle ignore** — игнорировать конкретные атрибуты:

```hcl
resource "aws_instance" "web" {
  instance_type = "t3.micro"

  lifecycle {
    ignore_changes = [
      instance_type,  # не реагировать на ручное изменение типа
      tags["LastModified"],
    ]
  }
}
```

---

## Q40. Как импортировать существующую инфраструктуру в Terraform?

Есть два подхода: **CLI-команда** `terraform import` (классический) и **`import` блок** (Terraform 1.5+, рекомендуемый).

### Классический import (CLI)

```bash
# terraform import <resource_address> <resource_id>
terraform import aws_s3_bucket.my_bucket my-existing-bucket-name
terraform import aws_instance.web i-0abc123def456789
terraform import aws_security_group.sg sg-0123456789abcdef0
```

После импорта в `terraform.tfstate` появится ресурс, но `.tf`-файл нужно написать вручную.

### import блок (Terraform 1.5+, OpenTofu 1.6+)

```hcl
# import.tf
import {
  to = aws_s3_bucket.my_bucket
  id = "my-existing-bucket-name"
}

import {
  to = aws_instance.web
  id = "i-0abc123def456789"
}
```

```bash
# Генерация .tf кода (Terraform 1.5+)
terraform plan -generate-config-out=generated.tf

# Применить import
terraform apply
```

**Преимущества `import` блока:**
- Часть конфигурации (ревью через PR)
- Генерация кода (`-generate-config-out`)
- Не нужно запоминать CLI-команды

### Типичный workflow импорта

```
1. Написать или сгенерировать .tf ресурс
2. Добавить import блок с реальным ID ресурса
3. terraform plan -generate-config-out=generated.tf
4. Проверить и скорректировать generated.tf
5. terraform apply
6. Удалить import блок (он одноразовый)
7. terraform plan — должно показать "No changes"
```

**Инструменты для автоматического импорта:**
- `terraformer` — reverse-engineering существующей инфраструктуры в `.tf`
- `tf-import-generator` — генерация import команд из state

---

## Q41. Что такое moved блок в Terraform и зачем он нужен?

**`moved` блок** (Terraform 1.1+) — декларативный способ сообщить Terraform, что ресурс был переименован или перемещён в другой модуль, **без удаления и пересоздания**.

### Проблема без moved блока

```hcl
# Было:
resource "aws_instance" "web_server" { ... }

# Стало (переименование):
resource "aws_instance" "web" { ... }
```

Terraform без `moved` блока сделает: **удалить** `web_server`, **создать** `web` — downtime!

### Решение: moved блок

```hcl
# Объявить перемещение (сохранить в репозитории до следующего apply)
moved {
  from = aws_instance.web_server
  to   = aws_instance.web
}

# Новое имя ресурса
resource "aws_instance" "web" { ... }
```

Terraform обновит state, **не трогая реальный ресурс**.

### Другие сценарии moved

```hcl
# Перемещение в модуль
moved {
  from = aws_s3_bucket.logs
  to   = module.storage.aws_s3_bucket.logs
}

# Рефакторинг count → for_each
moved {
  from = aws_instance.server[0]
  to   = aws_instance.server["web"]
}

moved {
  from = aws_instance.server[1]
  to   = aws_instance.server["api"]
}
```

### Когда убирать moved блок

`moved` блок можно удалить из кода **после** того, как все команды (CI/CD, коллеги) сделали `terraform apply` с ним. Если удалить раньше — Terraform снова попытается пересоздать ресурс.

**Рекомендация:** держать `moved` блоки минимум один релизный цикл.

---

## Q42. Как управлять секретами в Terraform — Vault provider и переменные окружения?

Хранить секреты в `.tf` файлах или `terraform.tfvars` нельзя (попадут в Git). Правильные подходы:

### 1. Переменные окружения (простейший подход)

```bash
# Terraform автоматически читает TF_VAR_<name>
export TF_VAR_db_password="supersecret"
export TF_VAR_api_key="my-api-key"

terraform apply
```

```hcl
variable "db_password" {
  type      = string
  sensitive = true  # скрывает значение из вывода plan/apply
}
```

### 2. HashiCorp Vault Provider

```hcl
provider "vault" {
  address = "https://vault.example.com"
  # Аутентификация через AWS IAM, Kubernetes, токен
}

# Читаем секрет из Vault
data "vault_generic_secret" "db" {
  path = "secret/production/database"
}

resource "aws_db_instance" "main" {
  password = data.vault_generic_secret.db.data["password"]
}
```

### 3. AWS Secrets Manager / SSM Parameter Store

```hcl
data "aws_secretsmanager_secret_version" "db_password" {
  secret_id = "prod/myapp/db_password"
}

resource "aws_db_instance" "main" {
  password = jsondecode(data.aws_secretsmanager_secret_version.db_password.secret_string)["password"]
}
```

### 4. Sensitive переменные в HCP Terraform

В HCP Terraform (Terraform Cloud) переменные помечаются как **Sensitive** — они шифруются и никогда не показываются в UI/логах:

```bash
# Через CLI
terraform workspace select production
# В UI: Workspace → Variables → Add sensitive variable
```

### Чего избегать

```hcl
# НИКОГДА — секреты в коде:
resource "aws_db_instance" "main" {
  password = "hardcoded-password"  # BAD
}

# НИКОГДА — секреты в tfvars в Git:
# terraform.tfvars: db_password = "secret"  # BAD, добавить в .gitignore!
```

### .gitignore для Terraform

```gitignore
*.tfvars          # файлы переменных (могут содержать секреты)
*.tfvars.json
!example.tfvars   # шаблоны-примеры без значений — ОК
terraform.tfstate
terraform.tfstate.*
.terraform/
```

---

## See also

- [[kubernetes-interview|Kubernetes]]
- [[docker-interview|Docker]]
- [[pipeline-design-interview|CI/CD пайплайны]]
- [[deployment-strategies-interview|Стратегии деплоя]]
- [[git-interview|Git]]
- [[distributed-systems-interview|Распределённые системы]]
- [[application-security-interview|Application Security]]
