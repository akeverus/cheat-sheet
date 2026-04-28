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
aliases:
  - "Kubernetes"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kubernetes

Kubernetes (k8s) — оркестратор контейнеров: распределяет pod'ы по узлам, следит за их здоровьем, делает service discovery, балансировку, rolling update и auto-scaling. K8s стал де-факто стандартом для production-контейнеров в микросервисной архитектуре. Работает с любым OCI-образом — обычно собранным через Docker (см. `../docker/`).

Для кого: DevOps/SRE и backend-инженеры, которые пишут манифесты, дебажат pod'ы, настраивают ingress и понимают, почему pod перезапускается. Раздел покрывает основы и ключевые подсистемы; углублённые темы (operators, GitOps) вынесены в соседние документы.

## Полезные ссылки

### Основные документы
- [kubernetes-basics](kubernetes-basics.md) — концепции, kubectl, Deployment/StatefulSet/Service
- [kubernetes-networking](kubernetes-networking.md) — Service, Ingress, NetworkPolicy, DNS
- [kubernetes-storage](kubernetes-storage.md) — PV, PVC, StorageClass, CSI
- [kubernetes-security](kubernetes-security.md) — RBAC, Pod Security Standards, secrets
- [kubernetes-advanced](kubernetes-advanced.md) — CRD, operators, HPA/VPA, scheduling
- [helm](helm.md) — пакетный менеджер: charts, templates, releases, hooks, OCI
- [kustomize](kustomize.md) — overlays для dev/stage/prod, patches, generators
- [istio](istio.md) — service mesh: sidecar/ambient, mTLS, traffic shifting, observability

### Соседние разделы
- [platform/containers/](../../../basics/README.md) — родительский раздел
- [platform/containers/docker/](../../../basics/README.md) — сборка образов
- [platform/cloud-providers/kubernetes-cloud.md](../../cloud-providers/kubernetes-cloud.md) — EKS/GKE/AKS
- [platform/ci-cd/](../../../basics/README.md) — deploy через ArgoCD/Flux
- [security/infrastructure/](../../../basics/README.md) — hardening кластера

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
| Архитектура кластера, API-server | [kubernetes-basics](kubernetes-basics.md) |
| Pod, Deployment, StatefulSet, Job | [kubernetes-basics](kubernetes-basics.md) |
| Service, Ingress, NetworkPolicy | [kubernetes-networking](kubernetes-networking.md) |
| PV, PVC, StorageClass | [kubernetes-storage](kubernetes-storage.md) |
| RBAC, ServiceAccount, PSS | [kubernetes-security](kubernetes-security.md) |
| CRD, operators | [kubernetes-advanced](kubernetes-advanced.md) |
| HPA, VPA, Cluster Autoscaler | [kubernetes-advanced](kubernetes-advanced.md) |
| Helm: charts, templates, releases | [helm](helm.md) |
| Kustomize: overlays и patches | [kustomize](kustomize.md) |

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

- Docker и сборка образов — [README](../../../basics/README.md)
- Managed K8s у облачных провайдеров — [kubernetes-cloud](../../cloud-providers/kubernetes-cloud.md)
- GitOps и деплой — [README](../../../basics/README.md)
- Security и RBAC — [README](../../../basics/README.md)
