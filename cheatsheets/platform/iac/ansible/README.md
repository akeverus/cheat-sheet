---
title: "Ansible"
description: "Агентless-автоматизация конфигурации и деплоя через SSH: playbooks, roles, inventory, Vault и Molecule."
tags:
  - meta
  - index
  - platform
  - iac
  - ansible
type: "index"
updated: "2026-04-20"
---
# Ansible

Ansible — инструмент автоматизации от Red Hat: описывает желаемое состояние серверов и приложений в YAML-playbooks и выполняет их через SSH без установки агентов. Основные единицы — inventory (список хостов), playbook (сценарий), role (переиспользуемый модуль). Сильные стороны: низкий порог входа, огромная библиотека модулей, идемпотентность и отличная подсистема секретов (Ansible Vault).

Для кого: DevOps/SRE, которые конфигурируют серверы (пакеты, пользователей, сервисы), деплоят приложения на VM, обновляют конфиги в проде или готовят узлы к вхождению в кластер. Не путать с Terraform: Ansible про **configuration management**, Terraform — про **provisioning**.

## Полезные ссылки

### Основные документы
- [ansible-basics](ansible-basics.md) — установка, первый playbook, inventory
- [ansible](ansible.md) — полное руководство: roles, handlers, Jinja2, Vault
- [ansible-advanced](ansible-advanced.md) — dynamic inventory, молекула, CI, custom modules

### Соседние разделы
- [platform/iac/](../../../basics/README.md) — родительский раздел IaC
- [platform/iac/terraform/](../../../basics/README.md) — провижининг облачной инфры
- [platform/iac/packer/](../../../basics/README.md) — сборка базовых образов
- [platform/ci-cd/](../../../basics/README.md) — запуск playbooks из пайплайна

### Внешние ресурсы
- [Ansible Documentation](https://docs.ansible.com/)
- [Ansible Galaxy](https://galaxy.ansible.com/)
- [Molecule](https://molecule.readthedocs.io/) — тестирование ролей
- [Ansible Vault](https://docs.ansible.com/ansible/latest/vault_guide/index.html)
- [AWX](https://github.com/ansible/awx) — UI/Tower

## Содержание

- [Карта тем](#карта-тем)
- [Ansible vs Terraform/Pulumi/Packer](#ansible-vs-terraformpulumipacker)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Файл |
|------|------|
| Установка, inventory, первый playbook | [ansible-basics](ansible-basics.md) |
| Playbook, tasks, handlers | [ansible](ansible.md) |
| Roles и их структура | [ansible](ansible.md) |
| Jinja2-шаблоны | [ansible](ansible.md) |
| Vault (шифрование секретов) | [ansible](ansible.md) |
| Dynamic inventory (AWS EC2 и др.) | [ansible-advanced](ansible-advanced.md) |
| Molecule, тесты, CI | [ansible-advanced](ansible-advanced.md) |

## Ansible vs Terraform/Pulumi/Packer

| Инструмент | Парадигма | State | Провайдеры | Основной кейс |
|------------|-----------|-------|-----------|---------------|
| Ansible | Procedural (YAML, идемпотентные модули) | Нет (работает по факту на хосте) | 3000+ модулей (SSH, cloud, network) | Настройка ОС и приложений на существующих хостах |
| Terraform | Declarative (HCL) | Да (remote state в S3/GCS/Cloud) | Провайдеры для облаков и SaaS | Provision: создать VM, VPC, БД |
| Pulumi | Imperative (Python/TS/Go/Java) | Да (Pulumi Cloud/S3) | Те же, что у Terraform | Provision с полноценным ЯП |
| Packer | Procedural (HCL/JSON) | Нет (артефакт = образ) | Builders (AWS AMI, Docker, VMware) | Сборка golden image |

Типичный пайплайн: **Terraform** поднимает VPC+VM **Packer** готовит базовый образ **Ansible** тонко настраивает запущенные машины.

## Когда использовать

- Настройка существующих серверов/VM (пакеты, пользователи, конфиги).
- Roll-out конфигурации на сотни хостов (`forks`, параллелизм).
- Деплой приложения на VM (если не контейнеризировано).
- Bootstrap Kubernetes-узлов, подготовка bare-metal.
- **Не подходит:** provisioning инфры (возьмите Terraform), сборка AMI (Packer), оркестрация контейнеров (K8s).

## Маршруты чтения

- **Быстрый старт (пара часов):** `ansible-basics.md` написать inventory + playbook на install nginx.
- **Полный курс:** `ansible-basics.md` `ansible.md` `ansible-advanced.md`.
- **Продакшн:** все три документа + Molecule-тесты + интеграция с `platform/ci-cd/`.

## Куда идти дальше

- Provisioning инфры — [README](../../../basics/README.md)
- Сборка образов — [README](../../../basics/README.md)
- IaC с полноценным языком — [README](../../../basics/README.md)
- CI/CD-интеграция — [README](../../../basics/README.md)
