---
title: "Docker"
description: "Контейнеризация приложений с Docker: образы, контейнеры, сети, volumes, Compose, оптимизация и безопасность."
tags:
  - meta
  - index
  - platform
  - containers
  - docker
type: "index"
aliases:
  - "Docker"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Docker

Docker — платформа для упаковки приложений в контейнеры: изолированные процессы со своей файловой системой и сетевым стеком, работающие поверх общего ядра Linux. Docker даёт воспроизводимую среду «build once, run anywhere», стандартизует артефакт деплоя (образ) и служит базой для оркестраторов, в первую очередь Kubernetes.

Для кого: backend-разработчики, DevOps и SRE, которые упаковывают сервисы, собирают multi-stage образы, пишут `docker-compose.yml` для локальной разработки и оптимизируют размер/безопасность образов перед продом.

## Полезные ссылки

### Основные документы
- [docker-basics](docker-basics.md) — установка, команды, образы, контейнеры, архитектура
- [docker-containers](docker-containers.md) — глубокая работа с контейнерами
- [docker-compose](docker-compose.md) — многоконтейнерные окружения
- [docker-advanced](docker-advanced.md) — сети, volumes, оптимизация, безопасность
- [docker-spring-boot](docker-spring-boot.md) — упаковка Spring Boot-приложений

### Соседние разделы
- [platform/containers/](../../../basics/README.md) — родительский раздел (связка Docker + Kubernetes)
- [platform/containers/kubernetes/](../../../basics/README.md) — оркестрация контейнеров
- [platform/ci-cd/](../../../basics/README.md) — сборка образов в пайплайне
- [security/infrastructure/](../../../basics/README.md) — hardening контейнеров

### Внешние ресурсы
- [Docker Documentation](https://docs.docker.com/)
- [Docker Hub](https://hub.docker.com/)
- [OCI Image Spec](https://github.com/opencontainers/image-spec)
- [Dockerfile best practices](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)
- [CIS Docker Benchmark](https://www.cisecurity.org/benchmark/docker)

## Содержание

- [Карта тем](#карта-тем)
- [Docker и Kubernetes: связка и различия](#docker-и-kubernetes-связка-и-различия)
- [Когда использовать](#когда-использовать)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Файл |
|------|------|
| Основные команды (`docker run/build/ps`) | [docker-basics](docker-basics.md) |
| Dockerfile, multi-stage | [docker-basics](docker-basics.md), [docker-advanced](docker-advanced.md) |
| Контейнеры и их жизненный цикл | [docker-containers](docker-containers.md) |
| Compose для локального стека | [docker-compose](docker-compose.md) |
| Volumes, bind mounts, tmpfs | [docker-advanced](docker-advanced.md) |
| Сети: bridge/host/overlay | [docker-advanced](docker-advanced.md) |
| Оптимизация размера образа | [docker-advanced](docker-advanced.md) |
| Упаковка Spring Boot | [docker-spring-boot](docker-spring-boot.md) |

## Docker и Kubernetes: связка и различия

Docker — это **runtime и build-toolchain** для одного хоста: собирает образ (OCI), запускает контейнеры, делает сети, тома. Kubernetes — **оркестратор** для множества хостов: планирует pod'ы (группы контейнеров), рестартует их, делает service discovery, балансировку, auto-scaling, rolling update.

- Dockerfile и docker build используются даже в проектах на k8s — это стандартный способ собрать образ.
- `docker run` и `docker-compose` — для **локальной разработки и smoke-тестов**; в проде почти всегда Kubernetes или managed-контейнеры (ECS, Cloud Run).
- Docker Swarm существует, но практически вытеснен Kubernetes.
- С 1.24 Kubernetes не использует Docker как runtime (только containerd/CRI-O), но OCI-образы из Docker Hub совместимы.

Итого: **Docker = образ + локальный запуск**, **Kubernetes = запуск множества контейнеров в проде**.

## Когда использовать

- **Docker без K8s:** локальный dev, compose-стеки для интеграционных тестов, однохостовый сервер (VPS, edge), CI-воркеры.
- **Docker + K8s:** микросервисная архитектура, динамический масштаб, multi-tenant платформа.
- **Без Docker:** serverless (Lambda, Cloud Functions) — хотя и там под капотом контейнеры, но сборка другая.

## Маршруты чтения

- **Новичок (1 день):** `docker-basics.md` `docker-containers.md` `docker-compose.md`.
- **Spring-разработчик:** `docker-basics.md` `docker-spring-boot.md` `docker-advanced.md` (multi-stage + layered jar).
- **Production readiness:** `docker-advanced.md` + CIS Docker Benchmark + [../../../security/infrastructure/](../../../basics/README.md).

## Куда идти дальше

- Оркестрация — [README](../../../basics/README.md)
- CI/CD-сборка образов — [README](../../../basics/README.md)
- Security-hardening — [README](../../../basics/README.md)
- Обзор контейнеризации — [containerization-overview](../containerization-overview.md)
