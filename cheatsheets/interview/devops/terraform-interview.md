---
title: "Вопросы на собеседовании: Terraform"
description: "Полное покрытие Terraform для подготовки к собеседованию: IaC концепции, HCL синтаксис, state management, модули, workspaces, CI/CD интеграция, Terraform Cloud, управление секретами, best practices."
tags:
  - interview
  - devops
  - terraform-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Terraform"
  - "Terraform interview"
  - "Terraform собеседование"
prerequisites:
  - "[[terraform]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Terraform`

Полное покрытие `Terraform` для подготовки к собеседованию: концепции `IaC`, `HCL`-синтаксис, управление `state`, модули, `workspaces`, `CI/CD`-интеграция, `Terraform Cloud`, работа в команде, управление секретами и сравнение с альтернативами.

**`Terraform`** (HashiCorp) — инструмент `Infrastructure as Code` с декларативным подходом: описываешь желаемое состояние инфраструктуры в `HCL`-файлах, а `Terraform` сам вычисляет план изменений и применяет его. Поддерживает сотни `provider`-ов (AWS, GCP, Azure, Kubernetes и др.). Является де-факто стандартом для управления облачной инфраструктурой в среде [Kubernetes](kubernetes-interview.md), [CI/CD-пайплайнов](../cicd/pipeline-design-interview.md) и [микросервисных](../architecture/microservices-interview.md) платформ.

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

**Infrastructure as Code** (`IaC`) — описание инфраструктуры (серверов, сетей, балансировщиков) кодом в текстовых файлах вместо ручной настройки через консоль провайдера. Тот же код, прогнанный заново, всегда даёт ту же инфраструктуру — это и есть главная ценность.

**Почему это важно.** Ручная настройка через UI не оставляет следов: непонятно, кто и что менял, нельзя откатиться, нельзя воспроизвести окружение один-в-один. IaC решает это, превращая инфраструктуру в обычный код в репозитории.

**Что это даёт:**
- **Воспроизводимость** — dev, staging и prod создаются из одного кода и поэтому идентичны; пропадает класс багов «на проде иначе, чем на стейдже».
- **Версионирование** — каждое изменение проходит через `git`: видно историю, можно сделать rollback на предыдущую версию.
- **Автоматизация** — инфраструктура раскатывается из `CI/CD` без ручных кликов, что убирает человеческие ошибки.
- **Документация** — код сам и есть актуальное описание инфраструктуры; отдельная wiki не устаревает, потому что её просто нет.
- **Масштабируемость** — новое окружение создаётся копированием конфигурации, а не неделями ручной работы.

**Два подхода к IaC:**
- **Декларативный** (`Terraform`, `CloudFormation`) — описываешь *что* должно получиться, а инструмент сам решает, как этого достичь.
- **Императивный** (`Ansible`, shell-скрипты) — описываешь *последовательность шагов* для достижения результата.

---

## Q2. (!) Что такое Terraform и чем он отличается от других IaC-инструментов?

**Terraform** — инструмент `IaC` от HashiCorp с открытым исходным кодом (лицензия `BSL 1.1` с версии 1.6; форк `OpenTofu` — под `MPL 2.0`). Его суть: ты декларативно описываешь желаемую инфраструктуру в `HCL`, а `Terraform` сравнивает это описание с реальностью и приводит мир к нужному виду.

**Что делает Terraform особенным:**
- **Декларативный** — описываешь конечное состояние в `HCL`, не пошаговый алгоритм.
- **Мультиоблачный** — один инструмент и язык для 3000+ провайдеров (AWS, GCP, Azure, Kubernetes), тогда как, например, `CloudFormation` живёт только в AWS.
- **State-based** — запоминает, что именно создал, в файле `terraform.tfstate`; благодаря этому отличает «нужно создать» от «уже создано, нужно лишь обновить».
- **Plan → Apply** — изменения применяются в два шага: сначала показывает план, потом исполняет; это даёт возможность ревью до того, как что-то реально поменяется.
- **Идемпотентность** — повторный `apply` на неизменной конфигурации ничего не делает: целевое состояние уже достигнуто.

**Отличия от конкурентов:**

| Инструмент | Подход | Охват | Язык |
|------------|--------|-------|------|
| `Terraform` | Декларативный | Мультиоблачный | `HCL` |
| `Ansible` | Императивный (агентлесс) | Конфигурация ОС | `YAML` |
| `Pulumi` | Декларативный/Императивный | Мультиоблачный | Python/TS/Go |
| `CloudFormation` | Декларативный | Только AWS | `JSON`/`YAML` |

---

## Q3. В чём разница между декларативным и императивным подходом в IaC?

Разница в том, *что именно* ты пишешь в коде: целевой результат или путь к нему.

**Декларативный подход** (Terraform, CloudFormation) — описываешь *желаемое конечное состояние*, а инструмент сам вычисляет, какие шаги нужны, чтобы к нему прийти. Идемпотентен по природе: если состояние уже достигнуто, ничего не делается. Пример формулировки: «У меня должны быть 3 инстанса EC2 с такими-то параметрами».

**Императивный подход** (Ansible, Bash) — описываешь *последовательность действий*, и порядок операций контролируешь ты сам. Идемпотентность не бесплатна: её приходится добиваться явными проверками (`when: not exists`), иначе повторный запуск создаст дубль. Пример формулировки: «Выполни команду создания инстанса EC2».

**На практике граница размыта.** `Ansible` поддерживает элементы декларативности (`state: present/absent`), а `Terraform` позволяет вставлять императивные действия через `local-exec`. Но базовая ментальная модель остаётся разной: Terraform думает состоянием, Ansible — шагами.

---

## Q4. Как Terraform определяет изменения, которые нужно применить?

`Terraform` сопоставляет три картины мира — это называется трёхстороннее сравнение (`three-way diff`):

1. **Desired state** — чего ты хочешь: конфигурация в `.tf`-файлах.
2. **Known state** — что Terraform создал в прошлый раз: последнее известное состояние в `terraform.tfstate`.
3. **Actual state** — что есть на самом деле: реальное состояние ресурсов в API провайдера (подтягивается при `terraform refresh`).

Три картины нужны, чтобы различать твои изменения и чужие. Сравнив *desired* с *actual*, Terraform видит, что нужно поменять; сравнив *known* с *actual* — замечает drift (кто-то поменял ресурс мимо Terraform).

**Алгоритм `plan`:**
1. Читает `.tf`-файлы и state.
2. Опционально делает `refresh` (по умолчанию включён) — обновляет actual state из API.
3. Сравнивает desired и actual state, вычисляя разницу.
4. Строит граф зависимостей (DAG), чтобы понять порядок операций.
5. Генерирует план с пометками: `+` создать, `-` удалить, `~` изменить на месте, `-/+` пересоздать (удалить и создать заново).

---

## Q5. (!) Что такое provider в Terraform?

**Provider** — плагин-переводчик между `Terraform` и API конкретной платформы (AWS, GCP, Azure, Kubernetes, GitHub и др.). Сам Terraform не знает, как создать S3-бакет, — это знает AWS-провайдер. Каждый провайдер приносит свой набор типов `resource` и `data source`, которыми ты затем оперируешь в конфигурации.

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

**Как это работает:**
- Провайдеры скачиваются при `terraform init` из `registry.terraform.io` — как зависимости при `npm install`.
- Точная версия фиксируется в `.terraform.lock.hcl` (прямой аналог `package-lock.json`), чтобы у всей команды и в CI был один и тот же провайдер.
- Один тип провайдера можно подключить несколько раз через `alias` — например, чтобы работать сразу в двух регионах AWS из одной конфигурации.

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

Оба описывают объект провайдера, но различаются тем, кто им владеет: `resource` Terraform создаёт и контролирует, `data source` только читает.

**Resource** — объект инфраструктуры под полным управлением `Terraform`: он его создаёт, изменяет и удаляет. Попадает в state и в граф зависимостей.

```hcl
resource "aws_s3_bucket" "my_bucket" {
  bucket = "my-unique-bucket-name"
  tags = {
    Environment = "prod"
  }
}
```

**Data source** — запрос на чтение информации об уже существующем объекте, которым Terraform не владеет. Ничего не создаёт и не меняет, просто подтягивает данные (например, ID последнего образа Ubuntu), чтобы подставить их в свои ресурсы.

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

**Ключевое различие:** `resource` Terraform создаёт и держит под управлением; `data` только читает внешние данные. Если удалить `resource` из кода — Terraform удалит и сам объект; если удалить `data` — просто перестанет его читать, объект останется на месте.

---

## Q7. (!) Что такое variable и output в Terraform?

`variable` — это вход конфигурации, `output` — выход. Вместе они превращают модуль в подобие функции: на входе параметры, на выходе результаты.

**Variable** — входной параметр: то, что подаётся в конфигурацию извне. Благодаря переменным один и тот же код переиспользуется для разных окружений (dev/prod) без копипасты. Можно навесить `validation`, чтобы отсечь заведомо неверные значения ещё на этапе plan.

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

Значение переменной можно задать несколькими способами; если их несколько, побеждает источник с более высоким приоритетом (от высшего к низшему):
1. Флаги CLI `-var` и `-var-file` — перебивают всё остальное.
2. Переменные окружения `TF_VAR_<name>`.
3. Файлы `terraform.tfvars` / `*.auto.tfvars`.
4. Значение `default` в объявлении — fallback, если больше нигде не задано.

**Output** — выходное значение, которое модуль (или корневой конфиг) отдаёт наружу.

```hcl
output "bucket_arn" {
  value       = aws_s3_bucket.my_bucket.arn
  description = "ARN of the S3 bucket"
  sensitive   = false
}
```

`output` нужен в двух ролях: передать данные из child-модуля в родителя (например, отдать `vpc_id` наружу) и вытащить значения наружу для `CI/CD`-пайплайна через `terraform output`.

---

## Q8. Что такое locals и когда их использовать?

**Locals** — именованные значения, вычисляемые внутри конфигурации. В отличие от `variable`, их нельзя задать извне: это не вход, а внутренний «псевдоним» для выражения. Удобно думать о них как о локальных константах модуля — определил один раз, ссылаешься через `local.<имя>` где угодно.

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

**Когда применять:**
- Вынести повторяющееся выражение в одно место (правишь раз, а не в десяти ресурсах).
- Хранить вычисляемые значения, которые не должны задаваться извне.
- Собирать составные теги и метаданные (`common_tags`), общие для всех ресурсов.
- Нормализовать значения через функции (`lower()`, `replace()`) перед использованием.

---

## Q9. (!) Чем отличается count от for_each?

Оба создают несколько копий ресурса, но по-разному их адресуют: `count` — по числовому индексу, `for_each` — по стабильному ключу. Это и определяет, какой из них безопаснее.

**count** — создаёт N экземпляров, адресуемых по индексу `[0]`, `[1]`, `[2]`. Берётся, когда ресурсы действительно взаимозаменяемы и порядок неважен.

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

**for_each** — создаёт по экземпляру на каждый элемент коллекции (map или set of strings), адресуя их по ключу `["web"]`, `["api"]`. Ключ привязан к смыслу, а не к позиции в списке.

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

**Ключевое различие и почему оно критично.** Удали элемент из середины `count`-списка — и все, что были за ним, сдвинутся по индексу: бывший `[2]` станет `[1]`. Terraform не понимает, что это «тот же» ресурс под новым индексом, поэтому пересоздаёт половину инфраструктуры. У `for_each` ключи стабильны: удаление `["api"]` не трогает `["web"]` и `["db"]`. Поэтому для production правило простое: предпочитай `for_each`, а `count` оставляй для условного включения ресурса (`count = var.enabled ? 1 : 0`) или для заведомо безымянных одинаковых копий.

---

## Q10. Что такое dynamic блок?

**Dynamic-блок** генерирует повторяющиеся *вложенные* блоки из коллекции — то же, что `for_each`, но не для целого ресурса, а для блоков внутри него. Без него пришлось бы руками копировать одинаковые `ingress { ... }` под каждое правило файрвола.

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

**Сценарий применения:** вложенные блоки (`ingress`, `egress`, `tag`, `setting` и т.п.), которые нельзя задать обычным атрибутом-списком — их число и содержимое зависят от входных данных.

**Подводный камень:** не злоупотребляй. Dynamic-блоки сильно ухудшают читаемость; если правил два-три и они статичны, явные блоки понятнее.

---

## Q11. Как работают expressions и функции в HCL?

`HCL` — не просто язык разметки: внутри значений можно писать выражения с встроенными функциями для преобразования данных. Своих функций определять нельзя (в отличие от Pulumi), но штатного набора хватает для строк, коллекций, чисел и кодирования.

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

**Условные выражения** (тернарный оператор `условие ? если_да : если_нет`) — частый способ менять параметр в зависимости от окружения:

```hcl
instance_type = var.environment == "prod" ? "t3.medium" : "t3.micro"
```

Полный список функций — в [HCL Language Reference](https://developer.hashicorp.com/terraform/language); проверять их поведение удобно прямо в `terraform console` (см. Q21).

---

## Q12. Что такое depends_on и lifecycle?

Обычно Terraform сам выстраивает порядок: если ресурс B ссылается на атрибут ресурса A, значит A создаётся первым. `depends_on` и `lifecycle` нужны там, где этой автоматики не хватает.

**depends_on** — ручное указание зависимости, когда между ресурсами есть связь, но она не выражена через атрибуты. Классический случай: Lambda не ссылается напрямую на привязку политики к роли, но должна создаваться после неё — иначе при первом запуске не хватит прав.

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

**lifecycle** — настройка того, как Terraform обращается с ресурсом при изменениях и удалении. Каждый ключ закрывает свою боль: `create_before_destroy` — zero-downtime замена, `prevent_destroy` — защита от случайного `destroy` критичной БД, `ignore_changes` — мир с внешними изменениями (теги от автоматики, ротация пароля), `postcondition` — проверка инварианта после apply.

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

**Terraform state** — файл `terraform.tfstate` (`JSON`), который связывает ресурсы из твоего кода с реальными объектами в провайдере. По сути это «память» Terraform: запись о том, что `aws_s3_bucket.my_bucket` из конфигурации — это конкретный бакет `my-unique-bucket-name` в AWS.

**Зачем он нужен.** Без state Terraform при каждом запуске видел бы только код и не знал, что уже создано. Он либо пересоздавал бы всё заново, либо опрашивал бы весь API провайдера в поисках «своих» ресурсов. State решает это разом:
- **Идентификация** — Terraform знает, какие объекты он создавал, и обновляет их, а не плодит дубли.
- **План изменений** — на основе state строится граф зависимостей и вычисляется diff (см. Q4).
- **Производительность** — атрибуты кешируются в state, так что для plan не нужно опрашивать API по каждому ресурсу.

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

**Подводный камень:** state хранит все атрибуты ресурсов в открытом виде, включая пароли и токены. Поэтому его нельзя коммитить в `git` — только в remote backend с шифрованием (см. Q14, Q33).

---

## Q14. (!) В чём разница между local и remote backend?

Backend — это место, где живёт state. Разница между local и remote по сути одна: может ли над инфраструктурой безопасно работать команда.

**Local backend** (по умолчанию) — `terraform.tfstate` лежит файлом на твоём диске. Этого хватает для экспериментов и pet-проектов, но не для команды: нет блокировки (двое затрут state друг друга), нет шифрования, файл живёт только у тебя.

**Remote backend** — state хранится централизованно (S3, GCS, Azure Blob, Terraform Cloud) и доступен всей команде. Снимает ровно те проблемы, что есть у local:
- **Общий доступ** — все работают с одним state, а не с локальными копиями.
- **State locking** — блокировка не даёт двоим применять изменения одновременно (см. Q15).
- **Шифрование** — at rest и in transit, что критично из-за секретов в state.
- **Долговечность** — версионирование и бэкапы на стороне хранилища.

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

**State locking** — блокировка state на время `apply` (и записывающих операций), чтобы двое не меняли инфраструктуру одновременно. Это защита от гонки, которая иначе приводит к порче state.

**Что происходит без locking.** Два инженера (или два CI-пайплайна) запускают `apply` параллельно. Оба читают один state, оба пишут поверх — итог непредсказуем: записи в state теряются, Terraform «забывает» про часть ресурсов, реальная инфраструктура расходится с памятью. Восстанавливать такое вручную — больно.

**Как работает блокировка (на примере S3 + DynamoDB):**
1. Перед операцией `Terraform` создаёт запись-замок в `DynamoDB` с уникальным `LockID`.
2. Второй вызов видит существующий замок и сразу падает с ошибкой, а не лезет в state.
3. По завершении операции замок снимается, и следующий желающий может работать.

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

**Снятие зависшей блокировки.** Если процесс упал и не снял замок (например, агент CI убили), его снимают вручную — но только убедившись, что никто другой реально не выполняет операцию:
```bash
terraform force-unlock <LOCK_ID>
```

---

## Q16. Как настроить remote backend на базе S3 и DynamoDB?

Есть проблема курицы и яйца: backend хранит state, но сам бакет и таблицу для него нужно где-то создать. Поэтому их выносят в отдельный `bootstrap`-проект (или создают руками) с обычным local state — это разовая операция.

**Шаг 1 — создать хранилище и таблицу блокировок** (отдельный `bootstrap`-проект или вручную). Включи versioning на бакете (бэкап на случай порчи state) и шифрование (в state лежат секреты):

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

**Шаг 2 — подключить backend в основном проекте.** Поле `key` — это путь к state внутри бакета; давай каждому сервису/окружению свой `key`, чтобы их state не пересекались:

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

После добавления блока `backend` выполни `terraform init` — Terraform предложит перенести существующий local state в S3.

**Нюанс про DynamoDB:** этот способ блокировки постепенно уходит. В Terraform 1.10 у S3-backend появилась нативная блокировка lock-файлом в самом бакете — параметр `use_lockfile = true` (работает поверх условных записей S3), а в 1.11 механизм объявлен GA, и параметр `dynamodb_table` помечен как deprecated. В новых проектах можно обойтись одним бакетом без отдельной таблицы; DynamoDB-вариант всё равно стоит знать — он повсеместно встречается в существующих конфигурациях, и на собеседованиях спрашивают именно его.

---

## Q17. Какие операции доступны через команду terraform state?

Семейство `terraform state` — это прямые операции над «памятью» Terraform в обход обычного цикла plan/apply. Нужны для рефакторинга и починки рассинхрона между state и кодом.

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

**Когда что применять:**
- `state mv` — переименовать ресурс или унести его в модуль, не пересоздавая (современная альтернатива — декларативный `moved`-блок, см. Q41).
- `state rm` — «забыть» о ресурсе, оставив его живым в облаке (например, передать управление в другой проект). С Terraform 1.7 есть декларативная альтернатива — блок `removed` (парный к `moved`): объявляешь `removed { from = aws_s3_bucket.legacy }` с `lifecycle { destroy = false }`, и ресурс уходит из state через обычный plan/apply — изменение видно в diff и проходит ревью, без ручной правки state.
- `state pull` / `push` — ручное чтение и запись state целиком при сложных миграциях; пользоваться осторожно, лучше с предварительным бэкапом.

**Подводный камень:** эти команды правят state напрямую и не имеют отмены. Ошибка в `state rm`/`push` может рассинхронить state с реальностью — перед опасными операциями делай `terraform state pull > backup.tfstate`.

---

## Q18. Что такое terraform import и когда он применяется?

`terraform import` берёт под управление Terraform ресурс, который уже существует в провайдере, но Terraform о нём не знает. Импорт лишь *связывает* существующий объект с записью в state — он ничего не создаёт и не меняет в облаке.

**Сценарий применения:** ресурс создали вручную в AWS Console (или он достался от прежней команды), и теперь его надо завести в Terraform, не пересоздавая.

**Важно:** Terraform не умеет автоматически писать конфигурацию по импортированному ресурсу (в классическом CLI-варианте) — `.tf`-код пишешь сам, а импорт лишь наполняет state.

**Шаг 1 — написать конфигурацию-заготовку (placeholder):**

```hcl
resource "aws_s3_bucket" "existing" {
  bucket = "already-existing-bucket"
}
```

**Шаг 2 — импортировать:**

```bash
terraform import aws_s3_bucket.existing already-existing-bucket
```

**Шаг 3 — выполнить plan и довести конфигурацию до нулевого diff.** Цель — чтобы `terraform plan` показал «No changes»: это значит, что твой `.tf`-код точно описывает реальный ресурс.

**Начиная с Terraform 1.5** есть декларативный блок `import` прямо в HCL — он проходит ревью как часть кода и в связке с `-generate-config-out` умеет генерировать черновик конфигурации (подробнее в Q40):

```hcl
import {
  to = aws_s3_bucket.existing
  id = "already-existing-bucket"
}
```

---

## Q19. (!) Опишите полный рабочий цикл команд Terraform

Базовый цикл — `init → plan → apply`; остальные команды (`fmt`, `validate`, `destroy`) встраиваются вокруг него. Идея в том, что ничего не применяется вслепую: сначала `plan` показывает, что произойдёт, и только потом `apply` это делает.

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

**Полезные флаги plan/apply:**
- `-var="key=value"` — переопределить одну переменную из командной строки.
- `-var-file=prod.tfvars` — подгрузить набор переменных под окружение.
- `-refresh=false` — пропустить опрос API: быстрее, но план может не учесть свежий drift.
- `-target=resource` — ограничить операцию одним ресурсом. Это аварийный инструмент для разруливания зависимостей, а не повседневная практика: точечный apply легко рассинхронит state с кодом.

---

## Q20. Что делает terraform plan -out и почему это важно для CI/CD?

`plan -out` сохраняет вычисленный план в файл, а `apply <файл>` потом исполняет *ровно его*. Без `-out` команда `apply` сама пересчитывает план в момент запуска — и он может отличаться от того, что ревьюили.

```bash
terraform plan -out=tfplan.binary
terraform apply tfplan.binary
```

**Почему это ключ к безопасному CI/CD:**
1. `plan` и `apply` становятся отдельными шагами пайплайна, разнесёнными во времени.
2. `apply` применяет **именно проверенный план** — между ревью и применением мир не «уплывёт» незаметно.
3. В промежутке умещается человеческое ревью: diff из плана постится комментарием в PR.
4. Файл плана бинарный и привязан к конкретному state — подменить его незаметно нельзя.

**Подводный камень:** сохранённый план быстро устаревает. Если между `plan` и `apply` инфраструктуру изменили, `apply` сохранённого файла откажется работать — потребуется новый `plan`.

**Типичный CI/CD flow:**
```
PR → terraform plan -out=tfplan → review → merge → terraform apply tfplan
```

---

## Q21. Как использовать terraform console для отладки?

`terraform console` — интерактивный REPL: вводишь выражение `HCL`, сразу видишь результат. Работает в контексте текущего state, поэтому доступны и переменные, и атрибуты уже созданных ресурсов. Это способ проверить выражение до того, как вставлять его в конфигурацию.

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

**Сценарий применения:** отладить незнакомую функцию, проверить сложное `for`-выражение или подсмотреть фактическое значение атрибута ресурса, не запуская `apply`. Консоль работает только на чтение — навредить инфраструктуре ею нельзя.

---

## Q22. Что такое TF_LOG и какие уровни логирования есть?

`TF_LOG` — переменная окружения, включающая подробное логирование `Terraform`. По умолчанию Terraform молчит о внутренней кухне; когда что-то идёт не так на уровне провайдера или сети, `TF_LOG` показывает, что происходит под капотом.

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

**Сценарий применения:** отладить ошибки аутентификации, странности провайдера или неожиданное поведение refresh. На практике обычно хватает `DEBUG`; `TRACE` включают, когда нужно увидеть сами HTTP-запросы к API провайдера. Логи многословны — направляй их в файл через `TF_LOG_PATH`.

---

## Q23. (!) Что такое модуль в Terraform и зачем он нужен?

**Модуль** — переиспользуемый набор ресурсов с входами (`variable`) и выходами (`output`), то есть функция в мире Terraform. Технически модулем считается любая директория с `.tf`-файлами; «модуль» как приём — это вынос связанной группы ресурсов в отдельную папку, чтобы вызывать её много раз.

**Типы модулей:**
- **Root module** — папка, из которой ты запускаешь Terraform; точка входа.
- **Child module** — модуль, который root вызывает через блок `module {}`.
- **Published module** — модуль, опубликованный в `Terraform Registry` для общего пользования.

**Зачем выносить ресурсы в модуль:**
- **DRY** — описать VPC/EKS/RDS один раз и переиспользовать, а не копировать десятки строк по проектам.
- **Инкапсуляция** — спрятать детали реализации за простым интерфейсом из переменных; потребителю не нужно знать внутреннее устройство.
- **Стандартизация** — зашить в модуль правильные настройки (теги, шифрование, политики), чтобы команды не изобретали их заново и не ошибались.

---

## Q24. Как создать и использовать собственный модуль?

Минимальный модуль — это три файла по ролям: `variables.tf` (вход), `main.tf` (ресурсы), `outputs.tf` (выход). Разбивка по файлам — соглашение для читаемости; технически Terraform склеивает все `.tf` в папке.

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

**Использование модуля.** В вызове `source` указывает путь к модулю, остальные поля — это его входные переменные. Результаты модуля доступны снаружи как `module.<имя>.<output>`:

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

После добавления или изменения блока `module` нужен `terraform init`, чтобы Terraform подтянул его исходники.

---

## Q25. (!) Как подключить модуль из Terraform Registry или Git?

Источник модуля задаётся полем `source`, и Terraform по его синтаксису понимает, откуда тянуть код. Главное правило: для Registry-модулей можно (и нужно) указывать `version`, для Git-модулей версия задаётся через `?ref=` прямо в URL.

**Terraform Registry (публичный)** — самый удобный вариант: указываешь имя и `version`, обновления контролируешь ограничением версий:
```hcl
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.0"

  cluster_name    = "my-cluster"
  cluster_version = "1.29"
}
```

**Git-репозиторий** — для приватных модулей без публикации в Registry. Двойной слеш `//` отделяет корень репозитория от подпапки с модулем, `?ref=` пинит версию:
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

Версионирование модулей нужно по той же причине, что и пиннинг провайдеров: чтобы прод не «поехал» от внезапного изменения общего модуля. Стандартный подход — Git-теги по semver.

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

**Стратегии и где их применять:**
- `ref=v1.2.0` — жёсткий пин на конкретный тег; обязателен для prod, чтобы изменения модуля приезжали только осознанно.
- `ref=main` — всегда последний коммит; допустимо только в dev, в prod опасно (модуль может «уехать» без твоего ведома).
- `version = "~> 1.2"` — разрешить безопасные patch-обновления; работает только для Registry-модулей.

**Нюанс Registry-модулей:** при публикации версия берётся из Git-тега, а зафиксированные провайдеры пишутся в `.terraform.lock.hcl`.

---

## Q27. (!) Что такое Terraform Workspace?

**Workspace** — несколько независимых state для одной и той же конфигурации. Код общий, а состояние у каждого workspace своё, так что одной конфигурацией можно держать, например, dev и staging без копирования файлов. По умолчанию ты в workspace `default`.

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

Внутри конфигурации текущий workspace доступен как `terraform.workspace` — по нему обычно выбирают параметры окружения из map:

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

Это два способа развести окружения, и выбор между ними — это компромисс «меньше дублирования» против «больше изоляции». Workspaces дают один код на все окружения; директории — отдельную папку (и backend) на каждое.

| Критерий | Workspaces | Директории |
|----------|-----------|------------|
| State | Отдельный per-workspace | Отдельный per-директории |
| Конфигурация | Общая (одна) | Может отличаться |
| Backend | Один | Независимые |
| Гибкость | Ниже | Выше |
| Риск | Выше (общий код) | Ниже (изоляция) |
| Сложность | Меньше файлов | Больше файлов |

**Workspaces подходят, когда** окружения отличаются только параметрами, а не структурой, и проект ведёт одна небольшая команда. Минус общего кода: ошибка в `terraform.workspace`-логике и один неверный `select` могут задеть сразу несколько окружений — и об этом легко забыть, потому что prod выглядит как тот же код.

**Директории подходят, когда** окружения могут существенно расходиться, ими управляют разные команды или нужна жёсткая изоляция (например, разные AWS-аккаунты с независимыми backend). За изоляцию платишь дублированием файлов.

**Эмпирическое правило:** для prod-инфраструктуры обычно предпочитают директории — явная изоляция важнее экономии строк. Workspaces хороши для эфемерных окружений на проверку фичи.

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

**Terraform Cloud** (TFC, ныне **HCP Terraform**) — managed-платформа HashiCorp, которая берёт на себя то, что иначе пришлось бы собирать самому: хранение state, выполнение `plan`/`apply` на сервере, блокировки, интеграцию с VCS и политики. По сути это «Terraform как сервис» вокруг командной работы.

**Ключевые возможности:**
- **Remote state** — хранение и версионирование state с шифрованием «из коробки» (не нужно поднимать свой S3 + DynamoDB).
- **Remote execution** — `plan` и `apply` идут на серверах TFC, а не на чьём-то ноутбуке: единое окружение, логи и общие переменные.
- **State locking** — блокировка встроена, отдельная таблица не нужна.
- **VCS Integration** — `plan` запускается автоматически при PR, что даёт diff прямо в ревью.
- **Policy as Code** — `Sentinel` (или OPA) проверяет правила compliance и может запретить `apply`, нарушающий политику.
- **Private Registry** — приватные модули и провайдеры для всей организации.
- **SSO** — вход через корпоративный IdP.
- **Audit logging** — полная история кто-что-когда сделал.

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

Это одна и та же платформа в двух вариантах поставки. Terraform Cloud (сейчас SaaS-вариант называется HCP Terraform — см. Q36) хостит HashiCorp; Terraform Enterprise — та же функциональность, но разворачивается у тебя (self-hosted). Различие сводится к тому, *где* живут данные и насколько строгие требования к compliance.

| Параметр | Terraform Cloud | Terraform Enterprise |
|----------|----------------|---------------------|
| Размещение | SaaS (HashiCorp) | Self-hosted (on-prem / private cloud) |
| Бесплатный план | Да (Free-тариф — до 500 управляемых ресурсов) | Нет (только платная лицензия) |
| Контроль данных | Данные у HashiCorp | Данные у клиента |
| Compliance | Ограничен | Full (HIPAA, FedRAMP и др.) |
| SSO | Только платный план | Включён |
| Sentinel | Платный план | Включён |
| Network | Публичный интернет | Изолированная сеть |

**Про бесплатный план:** старый Free-план «до 5 пользователей» отменён в 2023 вместе с переходом на модель тарификации по ресурсам (RUM — resources under management). Сейчас бесплатный тариф ограничен не числом людей, а количеством управляемых ресурсов — до 500 (те же тарифы расписаны в Q36).

**Как выбирать:** `Terraform Cloud` закрывает потребности большинства команд без эксплуатационной возни. `Enterprise` берут, когда данные физически нельзя отдавать наружу — regulated-индустрии с требованиями к data residency (HIPAA, FedRAMP) или изолированные сети.

---

## Q31. (!) Как организовать работу с Terraform в команде?

Командная работа сводится к одному: state должен быть общим, защищённым блокировкой и никогда не попадать в git, а каждое изменение — проходить ревью. Остальное — детали реализации этих принципов.

1. **Remote state** — S3/GCS/Terraform Cloud, чтобы все работали с одним состоянием; local state в git недопустим (конфликты + утечка секретов).
2. **State locking** — DynamoDB или встроенный механизм, иначе параллельные `apply` испортят state (см. Q15).
3. **`.terraform.lock.hcl` в git** — фиксирует версии провайдеров, чтобы у всех и в CI был один и тот же набор.
4. **`.gitignore`** — исключить `*.tfstate`, `*.tfstate.backup`, `.terraform/`, `*.tfplan`: state и планы хранятся в backend, а не в репозитории.

```gitignore
# .gitignore для Terraform
.terraform/
*.tfstate
*.tfstate.backup
*.tfplan
.terraform.tfvars
override.tf
```

5. **Code review** — все изменения через PR, с выводом `terraform plan` в комментарии, чтобы ревьюер видел реальный эффект, а не только diff кода.
6. **Модульность** — переиспользуемые компоненты в модулях (см. Q23), чтобы стандарты задавались в одном месте.
7. **Разделение state** — отдельный state на сервис и окружение: меньше blast radius (зона риска при ошибке) и быстрее plan.
8. **Политики** — `Sentinel` или `Open Policy Agent` для governance: автоматический запрет опасных изменений до `apply`.

---

## Q32. Как интегрировать Terraform в CI/CD-пайплайн?

Идея пайплайна — разнести `plan` и `apply` по разным триггерам: на PR гоняется `plan` (показать diff, ничего не менять), а `apply` запускается только после merge в main, обычно с ручным подтверждением через защищённое окружение. Так изменения инфраструктуры всегда проходят ревью.

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

Ключевые приёмы здесь: job `apply` зависит от `plan` и запускается только на `main`, окружение `production` позволяет повесить required-reviewers (ручное одобрение перед выкаткой), а сам `apply` применяет план, уже проверенный на шаге `plan`. Креды передаются через `secrets`, а не хардкодятся.

**Нюанс:** в реальном пайплайне job `apply` в таком виде не отработает. Каждый job в GitHub Actions стартует на чистом раннере: в `apply` нет ни `checkout`, ни `terraform init`, а файл `tfplan`, созданный в job `plan`, на новый раннер сам не переедет — его нужно передать артефактом (`actions/upload-artifact` в конце `plan`, `actions/download-artifact` в начале `apply`). Пример сознательно упрощён, чтобы показать саму структуру разделения plan/apply; проговорить эту оговорку на собеседовании — маркер реального опыта.

---

## Q33. (!) Как управлять секретами в Terraform?

**Главная ловушка.** Любой секрет, который попадает в ресурс (пароль БД, токен), Terraform сохраняет в state-файл в открытом виде. Поэтому управление секретами в Terraform — это не только «не хардкодить в коде», но и «защитить state». Ниже стратегии, дополняющие друг друга.

**1. Переменные окружения** — секрет не лежит в коде, а подтягивается из секрет-хранилища в момент запуска через `TF_VAR_<name>`:
```bash
export TF_VAR_db_password="$(aws secretsmanager get-secret-value --secret-id prod/db/password --query SecretString --output text)"
```

**2. Пометка переменных как sensitive** — не шифрует state, но убирает значение из вывода `plan`/`apply` и логов CI, чтобы секрет не утёк через консоль:
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

**3. AWS Secrets Manager / HashiCorp Vault** — секрет вообще не задаётся снаружи, а читается из хранилища через `data source` прямо во время apply (учти: после этого он всё равно осядет в state):
```hcl
data "aws_secretsmanager_secret_version" "db_pass" {
  secret_id = "prod/db/password"
}

resource "aws_db_instance" "main" {
  password = data.aws_secretsmanager_secret_version.db_pass.secret_string
}
```

**4. Шифрование state** — закрывает корневую проблему (секреты в state) на уровне хранилища:
- S3 backend с SSE-S3 или SSE-KMS.
- Terraform Cloud шифрует state автоматически.

**5. Нативное state encryption — только в OpenTofu 1.7+** — шифрование самого state-файла внутри инструмента, не полагаясь на backend. Важно: это возможность форка OpenTofu, у HashiCorp Terraform встроенного шифрования state нет (см. Q37). Конфигурация в OpenTofu:
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

Если ты на HashiCorp Terraform, рабочая комбинация — пункт 4 (шифрование на стороне backend) плюс жёсткое ограничение доступа к state по IAM.

**6. Ephemeral values и write-only arguments (Terraform 1.10/1.11)** — канонический способ сделать так, чтобы секрет вообще не попадал в state. Ephemeral-значения (Terraform 1.10: ephemeral-ресурсы, ephemeral input/output variables) живут только в памяти на время операции и не сохраняются ни в state, ни в plan-файле. Write-only аргументы ресурсов (Terraform 1.11, например `password_wo` у `aws_db_instance`) принимают такие значения и передают провайдеру, никогда не записывая в state. Связка «ephemeral-чтение секрета из Vault/Secrets Manager → write-only аргумент» закрывает главную ловушку из начала ответа: секрета в state просто нет. Это же решает проблему пункта 3, где секрет из data source оседал в state.

**Антипаттерны:**
- Хранить `*.tfstate` в git.
- Хардкодить секреты в `.tf`-файлах.
- Хранить `terraform.tfvars` с секретами в git.

---

## Q34. (!) Чем Terraform отличается от Ansible, Pulumi и CloudFormation?

Главное, что нужно держать в голове: эти инструменты решают разные задачи, поэтому часто не конкуренты, а соседи.

**Terraform vs Ansible** — это provisioning против configuration management. `Terraform` создаёт облачные ресурсы (VM, сети, балансировщики); `Ansible` настраивает уже существующую машину (ОС, пакеты, конфиги). На практике они дополняют друг друга: `Terraform` поднимает VM → `Ansible` доводит её до рабочего состояния. Технически `Ansible` агентлесс (ходит по SSH/WinRM), а `Terraform` работает через API провайдеров.

**Terraform vs Pulumi** — спор о языке. `Pulumi` пишет IaC на привычных языках (Python, TypeScript, Go, C#), поэтому выигрывает там, где нужна сложная логика — циклы, условия, ООП, переиспользование как в обычном коде. `Terraform` — декларативный `HCL`: порог входа ниже, меньше boilerplate, но и гибкости меньше. На стороне Terraform — зрелость и огромная экосистема провайдеров; Pulumi, кстати, умеет переиспользовать `Terraform`-провайдеры через `pulumi-terraform-bridge`.

**Terraform vs CloudFormation** — мультиоблачность против нативности AWS. `CloudFormation` живёт только в AWS, зато интегрирован в неё нативно и не требует отдельного state-файла (состояние хранит сам AWS в стеке). `Terraform` один на все облака сразу (AWS + GCP + Azure + Kubernetes), что незаменимо в гетерогенной инфраструктуре. По мелочам: `CloudFormation` бесплатен, а `Terraform Cloud` платный (есть free tier), и `CloudFormation` обычно медленнее применяет изменения из-за поллинга событий стека.

---

## Q35. (!) Каковы best practices структуры Terraform-проекта?

Хорошая структура держится на двух идеях: переиспользуемая логика живёт в `modules/`, а её вызовы под конкретное окружение — в `environments/<env>/`. Так общий код не дублируется, а различия окружений локализованы.

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

**Чек-лист (что и зачем):**
- `terraform fmt` и `terraform validate` в pre-commit hook — единый стиль и отлов синтаксических ошибок до пуша.
- `tflint` — статический анализ на анти-паттерны и ошибки конфигурации.
- `tfsec` / `checkov` — security-сканирование: ловит открытые порты, незашифрованные бакеты и т.п.
- `terratest` — интеграционные тесты модулей на Go (см. Q38).
- Разделять state по сервисам — чтобы ошибка в одном не утянула всю инфраструктуру и чтобы `plan` оставался быстрым.
- `required_version` и `required_providers` — всегда пинить версии, иначе сборка не воспроизводима.
- `terraform-docs` — автогенерация документации модулей из переменных и outputs.
- **Эмпирическое правило:** один state = одна blast radius (зона риска при ошибке). Чем меньше state, тем меньше может сломаться за один `apply`.

---

## Q36. Что такое Terraform Cloud и HCP Terraform — возможности и отличия?

**Terraform Cloud** (теперь **HCP Terraform** — HashiCorp Cloud Platform) — managed-сервис, который снимает с команды эксплуатацию state и раннеров: даёт remote state, выполняет `plan`/`apply` на своих серверах и применяет политики перед изменениями. По сути это та же платформа, что в Q29, под новым брендом HCP.

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

**OpenTofu** — open-source форк Terraform, появившийся в 2023 году как реакция сообщества на смену лицензии: HashiCorp перевёл Terraform с открытой `MPL 2.0` на `BSL 1.1` (Business Source License), которая ограничивает создание конкурирующих managed-сервисов. Форк сохранил открытую лицензию и почти полную совместимость с Terraform.

### Предыстория

В августе 2023 HashiCorp перевёл Terraform под `BSL 1.1`. Это задело компании, чей бизнес построен вокруг Terraform (Spacelift, Env0, Gruntwork, Harness), — они инициировали форк `OpenTofu`, который затем перешёл под крыло **Linux Foundation** ради нейтрального управления.

### Лицензионные отличия

| | Terraform | OpenTofu |
|--|-----------|----------|
| Лицензия | BSL 1.1 | MPL 2.0 |
| Ограничения | Нельзя создавать конкурирующий managed-сервис | Нет ограничений |
| Управление | HashiCorp (IBM) | Linux Foundation |
| Реестр | registry.terraform.io | registry.opentofu.org |

### Функциональные отличия

- **State encryption** (OpenTofu 1.7+) — нативное шифрование state-файла (блок `encryption` с `key_provider`/`method`). Главное одностороннее преимущество OpenTofu: у HashiCorp Terraform встроенного шифрования state нет — только шифрование на стороне backend (см. Q33).
- **Provider functions** — есть в обоих инструментах: OpenTofu 1.7 и Terraform 1.8 получили provider-defined functions почти одновременно (апрель 2024), реализации независимые. Односторонним преимуществом OpenTofu это уже не является.
- **Early evaluation** (OpenTofu 1.8+) — переменные и locals можно использовать в `source` модулей и конфигурации backend; Terraform требует там статические литералы.
- **`tofu test`** — расширенный фреймворк тестирования.

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

**Итог по миграции:** для большинства проектов переход — это замена бинарника `terraform` на `tofu`; state и lock-файл совместимы, провайдеры берутся из реестра HashiCorp или `registry.opentofu.org`. Расхождения начинаются на новых фичах: state encryption есть только у OpenTofu, а provider functions оба инструмента реализовали независимо (OpenTofu 1.7 и Terraform 1.8) — конфигурации, завязанные на такие возможности, переносятся не один в один.

---

## Q38. Как тестировать Terraform-код — terraform test и Terratest?

Есть два уровня тестирования. `terraform test` — встроенный, на `HCL`, без сторонних языков; `Terratest` — внешний, на Go, для полноценных интеграционных сценариев. Выбор между ними — это компромисс «простота против мощности».

### terraform test (Terraform 1.6+ / OpenTofu 1.6+)

Встроенный фреймворк: тесты пишутся на том же `HCL` в файлах `.tftest.hcl`, поэтому не требуют знания Go. Блок `run` выполняет `plan` или `apply`, а `assert` проверяет условие на атрибутах ресурсов:

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

**Terratest** — Go-библиотека: реально применяет модуль в облаке, проверяет результат (через outputs, API, HTTP-запросы) и гарантированно убирает за собой через `defer terraform.Destroy`. Мощнее встроенного теста, но требует Go и платит за временные ресурсы.

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

**Drift** — расхождение между тем, что записано в `terraform.tfstate`, и тем, что реально в облаке. Возникает, когда ресурс меняют в обход Terraform: кто-то поправил настройку в консоли AWS, сработал автоскейлинг, отработала чужая автоматика. Drift опасен тем, что следующий `apply` может молча откатить эти изменения обратно к коду.

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

Что делать с обнаруженным расхождением — зависит от того, чьё изменение «правильное»: код или реальность.

1. **Изменение в облаке верное** → подтянуть код под реальность: обновить `.tf`, закоммитить, применить.
2. **Изменение ошибочное** → вернуть реальность к коду обычным `terraform apply`.
3. **Принять drift как новую норму** → `terraform apply -refresh-only` обновит state, не трогая ресурсы.
4. **Изменение легитимно и будет повторяться** (теги от автоматики, ручной тюнинг) → перестать на него реагировать через `lifecycle.ignore_changes`:

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

Импорт связывает уже существующий в облаке объект с записью в state — сам ресурс при этом не пересоздаётся. Есть два способа: классическая **CLI-команда** `terraform import` и более новый **блок `import`** (Terraform 1.5+), который предпочтительнее, потому что проходит ревью как код и умеет генерировать черновик конфигурации.

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

**`moved`-блок** (Terraform 1.1+) — декларативный способ сказать Terraform: «этот ресурс просто переименовали (или унесли в модуль), это тот же объект». Тогда Terraform лишь обновит адрес в state, **не удаляя и не пересоздавая** реальный ресурс. По сути это `terraform state mv` (Q17), но в коде и с прохождением через ревью.

### Проблема без moved блока

```hcl
# Было:
resource "aws_instance" "web_server" { ... }

# Стало (переименование):
resource "aws_instance" "web" { ... }
```

Без `moved`-блока Terraform видит исчезновение `web_server` и появление `web` как два разных события и делает **удалить** `web_server` + **создать** `web` — то есть пересоздаёт ресурс и ловит downtime.

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

Базовое правило: секреты не должны жить ни в `.tf`-файлах, ни в закоммиченных `terraform.tfvars` — иначе они утекут в Git-историю навсегда. Подходы ниже различаются тем, *откуда* секрет берётся в момент apply — от простой переменной окружения до полноценного хранилища.

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

Самый строгий вариант: секрет хранится в Vault с централизованным контролем доступа и ротацией, а Terraform читает его на лету через `data source`:

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

**Подводный камень:** секрет, прочитанный через `data source`, всё равно сохраняется в state в открытом виде — та же ловушка, что в Q33. Vault контролирует хранение, доступ и ротацию, но state нужно защищать отдельно. Канонический способ не сохранять секрет вовсе — ephemeral values (Terraform 1.10) и write-only аргументы (Terraform 1.11): ephemeral-значения живут только в памяти на время операции и не пишутся ни в state, ни в plan, а write-only аргумент ресурса передаёт секрет провайдеру без сохранения. Провайдеры (Vault, AWS) публикуют ephemeral-ресурсы для чтения секретов именно под этот сценарий.

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

- [Kubernetes](kubernetes-interview.md)
- [Docker](docker-interview.md)
- [CI/CD пайплайны](../cicd/pipeline-design-interview.md)
- [Стратегии деплоя](../cicd/deployment-strategies-interview.md)
- [Git](git-interview.md)
- [Распределённые системы](../architecture/distributed-systems-interview.md)
- [Application Security](../security/application-security-interview.md)
- [Шпаргалка: Terraform](../../platform/iac/terraform/terraform.md) — теория
