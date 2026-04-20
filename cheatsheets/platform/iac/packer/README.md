---
title: "Packer"
description: "Сборка идентичных машинных образов (AMI, Docker, VMware) из единой конфигурации HashiCorp Packer."
tags:
  - meta
  - index
  - platform
  - iac
  - packer
type: "index"
updated: "2026-04-17"
---
# Packer

Packer — open-source инструмент HashiCorp для сборки **golden images**: из одной HCL/JSON-конфигурации создаются идентичные образы для AWS AMI, Google Cloud Image, Azure Image, Docker, VMware, VirtualBox и других платформ. Packer не управляет запущенной инфраструктурой — он создаёт артефакт (образ), который потом разворачивается Terraform/Ansible/облачным auto-scaling.

Для кого: DevOps/SRE и platform-инженеры, которые хотят ускорить bootstrap VM, стандартизовать образы между окружениями (dev/stage/prod) и сократить разрыв между сборкой и рантаймом (immutable infrastructure).

## Полезные ссылки

### Основные документы
- [[packer-basics]] — установка, первый шаблон, builders
- [[packer]] — полное руководство: multi-cloud, provisioners, variables, post-processors

### Соседние разделы
- [[README|platform/iac/]] — родительский раздел IaC
- [[README|platform/iac/terraform/]] — развёртывание того, что собрал Packer
- [[README|platform/iac/ansible/]] — Ansible как provisioner внутри Packer
- [[README|platform/containers/docker/]] — альтернатива для контейнерных workload'ов

### Внешние ресурсы
- [Packer Documentation](https://developer.hashicorp.com/packer/docs)
- [Packer Builders](https://developer.hashicorp.com/packer/plugins/builders)
- [Packer Provisioners](https://developer.hashicorp.com/packer/plugins/provisioners)
- [Packer GitHub](https://github.com/hashicorp/packer)

## Содержание

- [Карта тем](#карта-тем)
- [Packer vs Terraform/Ansible/Docker](#packer-vs-terraformansibledocker)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Файл |
|------|------|
| Установка, HCL-шаблон | [[packer-basics]] |
| Builders: AWS, Azure, GCP, Docker, VMware | [[packer]] |
| Provisioners: shell, Ansible, Chef | [[packer]] |
| Variables и template functions | [[packer]] |
| Post-processors (Docker push, vagrant, manifest) | [[packer]] |
| Multi-cloud и parallel builds | [[packer]] |

## Packer vs Terraform/Ansible/Docker

| Инструмент | Что создаёт | Декларативный | State | Кейс |
|------------|-------------|---------------|-------|------|
| Packer | Образ (AMI, Docker image, VMDK) | Частично (HCL-конфиг) | Нет | Golden image, bake once |
| Terraform | Живую инфру | Да | Да | Deploy того, что уже собрано |
| Ansible | Состояние хоста | Идемпотентный procedural | Нет | Config management на живых машинах |
| Docker (build) | OCI-образ контейнера | Dockerfile | Нет | Контейнеризация приложения |

Packer и Docker решают похожую задачу («упакуй окружение»), но на разных уровнях: Packer — **образ целой ОС** (VM), Docker — **образ процесса** (контейнер). Часто используются вместе: Packer пекёт AMI для Kubernetes-узлов, Docker — контейнер с приложением.

## Когда использовать

- Bake AMI для ASG, чтобы new instances стартовали за секунды, а не минуты.
- Стандартизация образов dev/stage/prod — один и тот же базовый слой.
- Бейс-образ для Kubernetes worker node с предустановленным CNI/kubelet.
- Образы для bare-metal / VMware в энтерпрайзе.
- **Не подходит:** для быстро меняющихся приложений (пересборка долгая — используйте Docker), для конфигурации уже запущенных машин (Ansible).

## Маршруты чтения

- **Быстрый старт:** `packer-basics.md` → собрать свой первый AMI.
- **Продакшн:** `packer.md` → multi-cloud + Ansible как provisioner + CI.
- **Интеграция с Terraform:** `packer.md` + `../terraform/` — передача AMI ID через data sources.

## Куда идти дальше

- Deploy собранных образов — [[README]]
- Configuration management — [[README]]
- Контейнерная альтернатива — [[README]]
- Обзор IaC-подходов — [[iac-overview]]
