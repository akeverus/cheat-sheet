---
title: "Platform Containers"
description: "Индекс раздела контейнеров: Docker, Kubernetes и практические маршруты изучения."
tags:
  - meta
  - index
  - platform
  - containers
type: "index"
updated: "2026-04-20"
---
# Platform Containers

Индекс раздела контейнеров и оркестрации.

## Полезные ссылки

[Docker Advanced](docker/docker-advanced.md)
[Docker Compose](docker/docker-compose.md)
[Kubernetes Advanced](kubernetes/kubernetes-advanced.md)

## Содержание

- [Быстрый маршрут](#быстрый-маршрут)
- [Практический маршрут: развернуть backend-сервис](#практический-маршрут-развернуть-backend-сервис)
- [См. также](#см-также)

## Быстрый маршрут

1. Начать с `containerization-overview.md`.
2. Освоить базу Docker: `docker-basics.md` -> `docker-containers.md` -> `docker-compose.md`.
3. Перейти к Kubernetes: `kubernetes-basics.md` -> `kubernetes-networking.md` -> `kubernetes-storage.md`.
4. Для production-фокуса пройти `kubernetes-security.md` и advanced-документы.

## Практический маршрут: развернуть backend-сервис

1. Подготовить Docker-образ сервиса: `docker/docker-spring-boot.md`.
2. Для локальной связки сервиса и зависимостей использовать `docker/docker-compose.md`.
3. Для cluster deployment перейти к `kubernetes/kubernetes-basics.md` и `kubernetes/kubernetes-networking.md`.
4. Проверить readiness/liveness и ресурсные лимиты перед выкладкой в production.

## См. также

- [README](../../basics/README.md) — интеграция контейнерной сборки в pipeline.
- [../../development/web-backend/backend-basics.md](../../development/web-backend/backend-basics.md) — backend-контекст для контейнеризации.
