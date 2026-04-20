---
title: "Infrastructure as Code (IaC) — обзор"
description: "Кратко: обзор Infrastructure as Code — Terraform, Ansible, Pulumi, Packer, декларативное и императивное описание инфраструктуры, практики."
tags:
  - platform
  - iac
  - iac-overview
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Infrastructure as Code** (**IaC**) — обзор

Кратко: обзор **Infrastructure as Code** — **Terraform**, **Ansible**, **Pulumi**, **Packer**, декларативное и императивное описание инфраструктуры, практики.

## Полезные ссылки

### Официальная документация
- [Terraform](https://www.terraform.io/docs)
- [Ansible](https://docs.ansible.com/)
- [Pulumi](https://www.pulumi.com/docs/)

### См. также
- [Platform](../README.md) — раздел инфраструктуры
- [[terraform-basics|Terraform Basics]] — практическая работа с Terraform
- [[ansible-basics|Ansible Basics]] — автоматизация конфигурации
- [[pulumi-basics|Pulumi Basics]] — IaC на языках программирования
- [[containerization-overview|Containerization]] — контейнеризация

## Содержание

- [Введение](#введение)
- [Terraform](#terraform)
- [Ansible](#ansible)
- [Pulumi](#pulumi)
- [Packer](#packer)
- [Сравнение и выбор](#сравнение-и-выбор)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Заключение](#заключение)

## Введение

**Infrastructure as Code** — описание и управление инфраструктурой (серверы, сети, облачные ресурсы) в виде кода: версионирование, повторяемость, ревью. **Terraform** — декларативный **HCL**, мульти-провайдер. **Ansible** — императивный **YAML**, конфигурация и оркестрация. **Pulumi** — **IaC** на языках программирования (**TypeScript**, **Python**, **Go**). **Packer** — сборка образов для облаков и **VM**. Основы — в [обзоре IaC](iac-overview.md).

**Ключевые понятия:** **state**, **idempotency**, **provider**, **module**, **playbook**, **role**, **template**.


## Terraform

**Terraform** (**HashiCorp**) — декларативное описание ресурсов в **HCL**. **State** хранит текущее состояние; `terraform plan` / `apply` приводят инфраструктуру к описанному виду. Провайдеры: **AWS**, **GCP**, **Azure**, **Kubernetes** и др. Модули для переиспользования. См. [Terraform Basics](terraform/terraform-basics.md), [Terraform Advanced](terraform/terraform-advanced.md).


## Ansible

**Ansible** — императивный подход: **playbooks** (**YAML**), **roles**, **inventory**. Без агентов (SSH); идемпотентность через модули. Конфигурация серверов, развёртывание приложений, оркестрация. См. [Ansible Basics](ansible/ansible-basics.md), [Ansible Advanced](ansible/ansible-advanced.md).


## Pulumi

**Pulumi** — **IaC** на языках программирования: **TypeScript**, **Python**, **Go**, **C#**. Объектная модель и IDE-поддержка; state в **Pulumi Cloud** или в выбранном бэкенде. Поддержка облаков и **Kubernetes**. См. [Pulumi Basics](pulumi/pulumi-basics.md).


## Packer

**Packer** — сборка образов (**AMI**, **GCP**, **Azure**, **Docker** и др.) из конфигурации и провижинеров (**Shell**, **Ansible**). Один шаблон — несколько платформ. См. [Packer Basics](packer/packer-basics.md), [Packer](packer/packer.md).


## Сравнение и выбор

| Инструмент | Стиль | Назначение |
|------------|--------|------------|
| **Terraform** | Декларативный (HCL) | Облачная инфраструктура, ресурсы |
| **Ansible** | Императивный (YAML) | Конфигурация, оркестрация, развёртывание |
| **Pulumi** | Декларативный (код) | Инфраструктура с полной мощью ЯП |
| **Packer** | Образы | Сборка VM/контейнерных образов |

Часто комбинируют: **Terraform** для облака, **Ansible** для настройки хостов; **Packer** для образов.

### Практический decision guide

1. **Нужно массово создавать/менять облачные ресурсы** -> начинайте с **Terraform**.
2. **Нужно настраивать ОС/пакеты/сервисы на уже созданных хостах** -> используйте **Ansible**.
3. **Нужна сложная логика и строгая типизация в IaC-коде** -> рассмотрите **Pulumi**.
4. **Нужно стандартизировать образы VM/AMI/контейнеров** -> добавьте **Packer** в pipeline.
5. **Нужен production-поток** -> типичный стек: `Packer` (образы) -> `Terraform` (ресурсы) -> `Ansible` (конфигурация).


## Лучшие практики

- Версионировать **IaC** в **Git**; проверки в **CI** (`terraform validate`, `terraform plan`).
- **State** — в удалённом бэкенде с блокировкой; не коммитить секреты в state.
- Модули и **roles** для переиспользования; теги и именование ресурсов единообразно.


## Решение проблем

| Проблема | Действие |
|----------|----------|
| **Terraform** state расходится с реальностью | Импорт ресурсов или ручная корректировка state; осторожно с `terraform destroy` |
| **Ansible** не находит хост | Проверить **inventory**, **SSH** доступ, переменные |
| **Pulumi** конфликт ресурсов | Проверить логи; при необходимости refresh и повторный deploy |


## Частые вопросы

**Terraform или Pulumi?** **Terraform** — привычный **HCL**, большое сообщество. **Pulumi** — если нужна логика на языке программирования и типизация. Оба поддерживают основные облака.

**Ansible вместо Terraform?** **Ansible** не хранит state инфраструктуры; для создания/удаления облачных ресурсов чаще используют **Terraform** или **Pulumi**. **Ansible** — для конфигурации уже созданных ресурсов.


## Глоссарий

| Термин | Описание |
|--------|----------|
| **state** | Текущее состояние ресурсов (**Terraform** state) |
| **provider** | Плагин для работы с облаком или сервисом |
| **module** | Переиспользуемый блок конфигурации (**Terraform** / **Pulumi**) |
| **playbook** | Сценарий выполнения задач в **Ansible** |
| **role** | Набор задач и шаблонов в **Ansible** |
| **idempotency** | Повторное применение даёт тот же результат |


## Заключение

**IaC** обеспечивает повторяемость и контроль версий инфраструктуры. **Terraform**, **Ansible**, **Pulumi**, **Packer** покрывают создание ресурсов, конфигурацию и образы. См. [обзор IaC](iac-overview.md) и [Containerization](../containers/containerization-overview.md).

**Дата:** 2026-02-11


