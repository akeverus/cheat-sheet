---
title: "Terraform"
description: "Декларативная IaC-платформа HashiCorp: HCL, провайдеры, state, модули, workspaces и enterprise-практики."
tags:
  - meta
  - index
  - platform
  - iac
  - terraform
type: "index"
updated: "2026-04-17"
---
# Terraform

Terraform — самый распространённый инструмент IaC: декларативный HCL-язык, огромная экосистема провайдеров (AWS, Azure, GCP, Kubernetes, GitHub, Datadog — тысячи), явное управление state, модули как единица переиспользования. Terraform отвечает за **provisioning** (создать VPC, VM, БД, DNS), а не configuration management (для этого Ansible).

Для кого: DevOps/SRE, platform- и cloud-инженеры, которые поднимают облачную инфраструктуру, управляют ею через Git (GitOps-подход) и хотят воспроизводимость между окружениями.

## Полезные ссылки

### Основные документы
- [terraform-basics.md](terraform-basics.md) — установка, первый provider, базовые команды
- [terraform.md](terraform.md) — полное руководство: state, модули, data sources, workflows
- [terraform-advanced.md](terraform-advanced.md) — remote state, workspaces, policy, enterprise

### Соседние разделы
- [platform/iac/](../README.md) — родительский раздел
- [platform/iac/pulumi/](../pulumi/README.md) — альтернатива на полноценных ЯП
- [platform/iac/ansible/](../ansible/README.md) — configuration management поверх
- [platform/iac/packer/](../packer/README.md) — сборка образов для TF-deploy
- [platform/cloud-providers/](../../cloud-providers/README.md) — провайдеры AWS/GCP/Azure

### Внешние ресурсы
- [Terraform Documentation](https://developer.hashicorp.com/terraform/docs)
- [Terraform Registry](https://registry.terraform.io/) — провайдеры и модули
- [AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [Terraform Best Practices](https://www.terraform-best-practices.com/)
- [tfsec](https://aquasecurity.github.io/tfsec/), [Checkov](https://www.checkov.io/) — статический анализ

## Содержание

- [Карта тем](#карта-тем)
- [Terraform vs Pulumi/Ansible/Packer](#terraform-vs-pulumiansiblepacker)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Файл |
|------|------|
| HCL, providers, resources | [terraform-basics.md](terraform-basics.md) |
| init/plan/apply/destroy | [terraform-basics.md](terraform-basics.md) |
| State и его хранение (remote, lock) | [terraform.md](terraform.md) |
| Модули и их структура | [terraform.md](terraform.md) |
| Data sources | [terraform.md](terraform.md) |
| Workspaces, окружения dev/stage/prod | [terraform-advanced.md](terraform-advanced.md) |
| Terraform Cloud / Enterprise | [terraform-advanced.md](terraform-advanced.md) |
| Sentinel / OPA для policy | [terraform-advanced.md](terraform-advanced.md) |

## Terraform vs Pulumi/Ansible/Packer

| Инструмент | Парадигма | Язык | State | Экосистема |
|------------|-----------|------|-------|-----------|
| Terraform | Declarative | HCL | Да, явный, с lock | Самая большая: 3000+ провайдеров |
| Pulumi | Imperative/declarative | TS/Py/Go/Java/.NET | Да (Pulumi Cloud) | Растущая, использует те же провайдеры |
| Ansible | Procedural (idempotent) | YAML | Нет | 3000+ модулей, но другой слой |
| Packer | Procedural | HCL/JSON | Нет (артефакт = образ) | Builders + Provisioners |

Terraform выигрывает за счёт **зрелости экосистемы** и **устоявшихся паттернов** (модули в Registry, best practices, CI-инструменты вроде Atlantis, tfsec). Минусы HCL — ограниченная выразительность (циклы, условия через `for_each`/`count`/dynamic), трудная типизация. State — и сила (понятно, что меняется), и слабость (нужно хранить, блокировать, бэкапить).

## Когда использовать

- Provisioning облачной инфры (VPC, EC2, RDS, IAM) — почти всегда Terraform.
- Kubernetes-ресурсы через Terraform provider — спорно; для ванильного k8s часто удобнее Helm/kubectl.
- Конфигурация SaaS (GitHub teams, Datadog dashboards, Cloudflare) — отличный кейс.
- **Не подходит:** настройка пакетов на живой VM (Ansible), сборка AMI (Packer), быстрые ad-hoc скрипты.

## Маршруты чтения

- **Первый день:** `terraform-basics.md` → развернуть S3 bucket + VPC.
- **Полный курс:** `terraform-basics.md` → `terraform.md` → `terraform-advanced.md`.
- **Production-ready:** + remote state (S3 + DynamoDB lock) + tfsec в CI + модули из Registry.

## Куда идти дальше

- Альтернатива на ЯП — [../pulumi/README.md](../pulumi/README.md)
- Configuration management — [../ansible/README.md](../ansible/README.md)
- Сборка образов — [../packer/README.md](../packer/README.md)
- Облачные провайдеры — [../../cloud-providers/README.md](../../cloud-providers/README.md)
