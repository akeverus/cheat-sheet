---
title: "Kubernetes"
description: "Оркестрация контейнеров: pod/deployment/service, сеть, storage, безопасность и эксплуатация кластера."
tags:
  - meta
  - index
  - platform
  - containers
  - kubernetes
type: "index"
updated: "2026-04-20"
---
# Kubernetes

Kubernetes (k8s) — оркестратор контейнеров: распределяет pod'ы по узлам, следит за их здоровьем, делает service discovery, балансировку, rolling update и auto-scaling. K8s стал де-факто стандартом для production-контейнеров в микросервисной архитектуре. Работает с любым OCI-образом — обычно собранным через Docker (см. `../docker/`).

Для кого: DevOps/SRE и backend-инженеры, которые пишут манифесты, дебажат pod'ы, настраивают ingress и понимают, почему pod перезапускается. Раздел покрывает основы и ключевые подсистемы; углублённые темы (operators, GitOps) вынесены в соседние документы.

## Полезные ссылки

### Основные документы
- [[kubernetes-basics]] — концепции, kubectl, Deployment/StatefulSet/Service
- [[kubernetes-networking]] — Service, Ingress, NetworkPolicy, DNS
- [[kubernetes-storage]] — PV, PVC, StorageClass, CSI
- [[kubernetes-security]] — RBAC, Pod Security Standards, secrets
- [[kubernetes-advanced]] — CRD, operators, HPA/VPA, scheduling

### Соседние разделы
- [[README|platform/containers/]] — родительский раздел
- [[README|platform/containers/docker/]] — сборка образов
- [[kubernetes-cloud|platform/cloud-providers/kubernetes-cloud.md]] — EKS/GKE/AKS
- [[README|platform/ci-cd/]] — deploy через ArgoCD/Flux
- [[README|security/infrastructure/]] — hardening кластера

### Внешние ресурсы
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Kubernetes API Reference](https://kubernetes.io/docs/reference/kubernetes-api/)
- [CNCF Landscape](https://landscape.cncf.io/)
- [CIS Kubernetes Benchmark](https://www.cisecurity.org/benchmark/kubernetes)
- [kubectl cheat sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)

## Содержание

- [Карта тем](#карта-тем)
- [Docker vs Kubernetes: различия](#docker-vs-kubernetes-различия)
- [Когда нужен Kubernetes](#когда-нужен-kubernetes)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Файл |
|------|------|
| Архитектура кластера, API-server | [[kubernetes-basics]] |
| Pod, Deployment, StatefulSet, Job | [[kubernetes-basics]] |
| Service, Ingress, NetworkPolicy | [[kubernetes-networking]] |
| PV, PVC, StorageClass | [[kubernetes-storage]] |
| RBAC, ServiceAccount, PSS | [[kubernetes-security]] |
| CRD, operators | [[kubernetes-advanced]] |
| HPA, VPA, Cluster Autoscaler | [[kubernetes-advanced]] |
| Helm, Kustomize (кратко) | [[kubernetes-advanced]] |

## Docker vs Kubernetes: различия

| Аспект | Docker | Kubernetes |
|--------|--------|-----------|
| Уровень | Один хост | Кластер из многих узлов |
| Задача | Сборка и запуск контейнера | Оркестрация, self-healing, scaling |
| Unit | Контейнер | Pod (группа контейнеров) |
| Сеть | bridge/overlay внутри хоста | Plugin CNI (Calico, Cilium) + Service/Ingress |
| Storage | volumes/bind mounts | PV/PVC через CSI |
| Конфигурация | CLI + docker-compose | YAML-манифесты, декларативно |
| Типичное использование | Dev, single-host | Production, масштаб |

Docker собирает образ — Kubernetes его запускает. Это взаимодополняющие инструменты, а не альтернативы.

## Когда нужен Kubernetes

- **Нужен:** 10+ сервисов, множественные реплики, zero-downtime деплой, динамическое масштабирование, мульти-AZ.
- **Избыточен:** монолит на одном сервере, маленький stateful-сервис, low-traffic side-projects (используйте Docker Compose, systemd или PaaS).
- **Managed вместо self-hosted:** EKS/GKE/AKS — почти всегда разумный выбор для команд без отдельной platform-команды.

## Маршруты чтения

- **Backend-разработчик (день):** `kubernetes-basics.md` + раздел Service/Ingress в `kubernetes-networking.md`.
- **DevOps-инженер (неделя):** все пять документов + Helm + GitOps (ArgoCD/Flux) в `../../ci-cd/`.
- **Production hardening:** `kubernetes-security.md` + CIS Kubernetes Benchmark + `../../../security/infrastructure/`.

## Куда идти дальше

- Docker и сборка образов — [[README]]
- Managed K8s у облачных провайдеров — [[kubernetes-cloud]]
- GitOps и деплой — [[README]]
- Security и RBAC — [[README]]
