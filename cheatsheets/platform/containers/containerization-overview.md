---
title: "Containerization — обзор"
description: "Кратко: обзор контейнеризации — Docker, Kubernetes, оркестрация, образы, runtime, практики и связь с devops."
tags:
  - platform
  - containers
  - containerization-overview
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Containerization — обзор

Кратко: обзор контейнеризации — **Docker**, **Kubernetes**, оркестрация, образы, **runtime**, практики и связь с **devops**.

## Полезные ссылки

### Официальная документация
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)

### См. также
- [[README|Platform]] — раздел платформы
- [[containerization-overview|Контейнеры]] — **Docker**, **Kubernetes**
- [[iac-overview|IaC]] — **Infrastructure as Code**

## Содержание

- [Введение](#введение)
- [Контейнеры и образы](#контейнеры-и-образы)
- [Docker](#docker)
- [Kubernetes и оркестрация](#kubernetes-и-оркестрация)
- [Runtime и стандарты](#runtime-и-стандарты)
- [Безопасность](#безопасность)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Заключение](#заключение)

## Введение

**Контейнеризация** — упаковка приложения и зависимостей в изолируемую единицу (образ и контейнер), выполняемую в единообразной среде. **Docker** — самый распространённый инструмент сборки образов и запуска контейнеров; **Kubernetes** — оркестратор для масштабирования, развёртывания и управления контейнерами в кластере. Документ даёт обзор концепций, инструментов и связей с **devops**.

**Ключевые понятия:** **image**, **container**, **Dockerfile**, **pod**, **deployment**, **service**, **namespace**, **Helm**, **OCI**, **CRI**.


## Контейнеры и образы

**Образ** — неизменяемый шаблон: файловая система и метаданные. **Контейнер** — запущенный экземпляр образа с изолированным пространством имён и cgroups (на **Linux**). Контейнеры разделяют ядро хоста, в отличие от виртуальных машин. Основной контент по **Docker** и **Kubernetes** — в [[containerization-overview|контейнеры]].


## Docker

**Docker** — платформа для сборки образов (**Dockerfile**, `docker build`), хранения в **registry** (Docker Hub), запуска контейнеров (`docker run`). **Docker Compose** — оркестрация многоконтейнерных приложений на одном хосте. См. [[docker-basics|Docker]], [[docker-advanced|Docker Advanced]], [[docker-compose|Docker Compose]].


## Kubernetes и оркестрация

**Kubernetes** — оркестратор: **Pods**, **Deployments**, **Services**, **ConfigMaps**, **Secrets**, **Ingress**. Масштабирование, самовосстановление, обновления без даунтайма. **Helm** — пакетный менеджер для **Kubernetes** (charts). См. [[kubernetes-basics|Kubernetes]], [[kubernetes-advanced|Kubernetes Advanced]].


## Runtime и стандарты

**OCI** (Open Container Initiative) — спецификации образов и runtime. **containerd**, **CRI-O** — runtime, совместимые с **Kubernetes** (CRI). **runc** — низкоуровневый runtime по умолчанию для **Docker** и **containerd**.


## Безопасность

Принцип наименьших привилегий: образы без root, read-only файловая система где возможно, сканирование образов на уязвимости (**Trivy**, **Snyk**). Секреты — через **Kubernetes Secrets** или внешние хранилища (Vault), не в образах.


## Лучшие практики

- Минимальные базовые образы (**Alpine**, **distroless**); многоэтапная сборка в **Dockerfile**.
- Один процесс на контейнер; корректная обработка сигналов (graceful shutdown).
- Версионирование образов по тегам; не использовать `latest` в production.
- Ресурсные лимиты (**CPU**, память) в **Kubernetes**; **liveness** и **readiness** probes.


## Решение проблем

| Проблема | Действие |
|----------|----------|
| Контейнер не стартует | Проверить логи `docker logs` / `kubectl logs`; образ и команду запуска |
| Out of memory | Задать лимиты памяти; проверить утечки в приложении |
| Сеть между подами | Проверить **Services**, **NetworkPolicies**, **DNS** в кластере |


## Частые вопросы

**Docker vs Kubernetes?** **Docker** — сборка и запуск контейнеров на одном хосте. **Kubernetes** — оркестрация множества контейнеров на кластере (масштабирование, балансировка, обновления).

**Когда достаточно Docker Compose?** Для разработки и небольших деплоев на одном сервере. Для production с высокой доступностью и масштабированием — **Kubernetes** или managed-сервисы (**EKS**, **GKE**, **AKS**).


## Глоссарий

| Термин | Описание |
|--------|----------|
| **image** | Неизменяемый шаблон для создания контейнеров |
| **container** | Запущенный экземпляр образа с изоляцией |
| **Dockerfile** | Инструкции для сборки образа **Docker** |
| **pod** | Минимальная единица развёртывания в **Kubernetes** (один или несколько контейнеров) |
| **deployment** | Ресурс **Kubernetes** для управления репликами **Pods** |
| **service** | Сетевой доступ к **Pods** в **Kubernetes** |
| **OCI** | **Open Container Initiative** — стандарты образов и runtime |
| **CRI** | **Container Runtime Interface** — интерфейс runtime в **Kubernetes** |


## Заключение

Контейнеризация обеспечивает переносимость и единообразие окружения; **Docker** и **Kubernetes** — основа современного **devops**. Используйте минимальные образы, лимиты ресурсов и сканирование безопасности. См. [[containerization-overview|Контейнеры]] и [[iac-overview|IaC]].

**Дата:** 2026-02-03


