---
title: "Docker Advanced"
description: "Docker - это платформа для разработки, доставки и запуска приложений в контейнерах. Этот документ охватывает продвинутые концепции, best practices и enterprise-grade паттерны работы с Docker."
tags: ["platform", "containers", "docker-advanced"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Docker Advanced**

**Docker** - это платформа для разработки, доставки и запуска приложений в контейнерах. Этот документ охватывает продвинутые концепции, **best practices** и **enterprise-grade** паттерны работы с **Docker**.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки
- [Docker Documentation](https://docs.docker.com/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Docker Security](https://docs.docker.com/engine/security/)
- [BuildKit](https://docs.docker.com/build/buildkit/)
- [Docker Compose](docker-compose.md)
- [Docker Swarm](https://docs.docker.com/engine/swarm/)

## Содержание

- [Продвинутая архитектура образов](#продвинутая-архитектура-образов)
  - [Multi-stage builds](#multi-stage-builds)
  - [Advanced layer optimization](#advanced-layer-optimization)
- [Security hardening](#security-hardening)
  - [Security best practices](#security-best-practices)
  - [Security scanning](#security-scanning)
- [Networking](#networking)
  - [Advanced networking](#advanced-networking)
  - [Service discovery](#service-discovery)
  - [DNS и service aliases](#dns-и-service-aliases)
- [Storage](#storage)
  - [Advanced volume management](#advanced-volume-management)
  - [Persistent data patterns](#persistent-data-patterns)
- [Docker Compose advanced](#docker-compose-advanced)
  - [Production-ready compose](#production-ready-compose)
  - [Environment management](#environment-management)
- [Docker Swarm](#docker-swarm)
  - [Swarm mode cluster](#swarm-mode-cluster)
  - [Swarm monitoring](#swarm-monitoring)
- [Docker Registry](#docker-registry)
  - [Private registry](#private-registry)
  - [Registry API](#registry-api)
- [Performance optimization](#performance-optimization)
  - [Runtime optimization](#runtime-optimization)
  - [Build optimization](#build-optimization)
  - [Resource management](#resource-management)
- [Monitoring и logging](#monitoring-и-logging)
  - [Container monitoring](#container-monitoring)
  - [Advanced logging](#advanced-logging)
- [CI/CD integration](#cicd-integration)
  - [GitHub Actions](#github-actions)
  - [Jenkins pipeline](#jenkins-pipeline)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [Debug containers](#debug-containers)
  - [Performance debugging](#performance-debugging)
- [Best practices](#best-practices)
  - [Production deployment](#production-deployment)
  - [Image management](#image-management)
- [См. также](#см-также)

## Продвинутая архитектура образов

### **Multi-stage builds**

Ниже — пример **multi-stage Dockerfile** для **Java**-приложения (**dockerfile**).
```dockerfile
# Multi-stage build для Java приложения
FROM maven:3.9.0-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Скачиваем зависимости для кэширования
RUN mvn dependency:go-offline -B

# Собираем приложение
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Устанавливаем timezone и локаль
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Europe/Moscow /etc/localtime && \
    echo "Europe/Moscow" > /etc/timezone

# Создаем пользователя для безопасности
RUN addgroup -g 1001 -S appuser && \
    adduser -S -D -H -u 1001 -h /app -s /sbin/nologin -G appuser -g appuser appuser

WORKDIR /app

# Копируем JAR из build stage
COPY --from=build /app/target/*.jar app.jar

# Создаем tmp директорию для приложения
RUN mkdir -p /tmp && chown -R appuser:appuser /tmp

USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
```

### **Advanced layer optimization**
```dockerfile
# Оптимизация слоев для Python приложения
FROM python:3.11-slim AS base

# Устанавливаем системные зависимости
RUN apt-get update && apt-get install -y \
    build-essential \
    libpq-dev \
    libffi-dev \
    && rm -rf /var/lib/apt/lists/*

# Создаем виртуальное окружение
RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Устанавливаем Python зависимости
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# ---

FROM python:3.11-slim AS runtime

# Устанавливаем runtime зависимости
RUN apt-get update && apt-get install -y \
    libpq5 \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd -r appuser && useradd -r -g appuser appuser

# Копируем виртуальное окружение
COPY --from=base /opt/venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Создаем директорию приложения
WORKDIR /app
RUN chown appuser:appuser /app

# Копируем исходный код
COPY --chown=appuser:appuser . .

USER appuser

EXPOSE 8000

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD python -c "import requests; requests.get('http://localhost:8000/health')"

CMD ["python", "app.py"]
```

## **Security hardening**

### **Security best practices**
```dockerfile
# Безопасный образ с минимальными привилегиями
FROM alpine:3.17

# Устанавливаем обновления безопасности
RUN apk update && apk upgrade && \
    apk add --no-cache \
    ca-certificates \
    curl \
    && rm -rf /var/cache/apk/*

# Создаем непривилегированного пользователя
RUN addgroup -g 1001 -S appgroup && \
    adduser -S -D -H -u 1001 -h /app -s /sbin/nologin -G appgroup -g appgroup appuser

# Устанавливаем правильные права
RUN mkdir -p /app && \
    chown -R appuser:appgroup /app && \
    chmod 755 /app

WORKDIR /app

# Копируем бинарный файл
COPY --chown=appuser:appgroup ./myapp /app/myapp

# Устанавливаем capabilities только на необходимые
RUN apk add --no-cache libcap && \
    setcap 'cap_net_bind_service=+ep' /app/myapp

USER appuser

EXPOSE 8080

# Используем exec form для правильной обработки сигналов
ENTRYPOINT ["/app/myapp"]
```

### **Security scanning**
```bash
# Сканирование образов на уязвимости
docker scan myapp:latest

# Использование Trivy
trivy image myapp:latest

# Clair для детального анализа
clair-scanner --ip 192.168.1.100 myapp:latest

# Anchore Engine
anchore-cli image add myapp:latest
anchore-cli image vuln myapp:latest all

# Snyk
snyk container test myapp:latest --file=Dockerfile

# Проверка на секреты
trufflehog --regex --entropy=False file://./Dockerfile
```

## **Networking**

### **Advanced networking**
```bash
# Создание custom networks
docker network create --driver bridge --subnet 172.20.0.0/16 --gateway 172.20.0.1 mynetwork

# Overlay network для Swarm
docker network create --driver overlay --subnet 10.0.9.0/24 myoverlay

# Macvlan для прямого доступа к физической сети
docker network create -d macvlan \
  --subnet=192.168.1.0/24 \
  --gateway=192.168.1.1 \
  -o parent=eth0 \
  macvlan-net

# IPv6 поддержка
docker network create --ipv6 --subnet=2001:db8::/64 ipv6-net
```

### **Service discovery**
```yaml
# Docker Compose с service discovery
version: '3.8'
services:
  web:
    image: nginx:alpine
    networks:
      - frontend
      - backend
    depends_on:
      - api

  api:
    build: .
    networks:
      - backend
    environment:
      - DATABASE_URL=api_db:5432

  db:
    image: postgres:15
    networks:
      - backend
    environment:
      - POSTGRES_PASSWORD=secret

networks:
  frontend:
    driver: bridge
  backend:
    driver: bridge
    internal: true  # Изолированная сеть
```

### **DNS** и **service aliases**
```bash
# DNS конфигурация
docker run --dns 8.8.8.8 --dns 8.8.4.4 nginx

# Custom DNS search domains
docker run --dns-search example.com nginx

# Hostname и domain
docker run --hostname myhost --domain example.com nginx

# Extra hosts
docker run --add-host db:192.168.1.100 nginx

# Service aliases
docker run --name web --network mynet nginx
docker run --name api --network mynet --network-alias api-service myapi
```

## **Storage**

### **Advanced volume management**
```bash
# Создание named volumes
docker volume create --driver local \
  --opt type=tmpfs \
  --opt device=tmpfs \
  --opt o=size=100m,uid=1000 \
  mytmpfs

# NFS volume
docker volume create --driver local \
  --opt type=nfs \
  --opt o=addr=192.168.1.100,rw \
  --opt device=:/path/to/dir \
  nfsvolume

# Bind mount с опциями
docker run -v /host/path:/container/path:ro,Z nginx

# Volume plugins
docker plugin install --grant-all-permissions vieux/sshfs
docker volume create --driver vieux/sshfs \
  -o sshcmd=user@host:/remote/path \
  -o password=secret \
  sshvolume

# Backup volumes
docker run --rm -v myvolume:/data -v $(pwd):/backup alpine tar czf /backup/backup.tar.gz -C /data .
```

### **Persistent data patterns**
```dockerfile
# Pattern для stateful приложений
FROM postgres:15

# Создаем volume для данных
VOLUME /var/lib/postgresql/data

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD pg_isready -U postgres -d postgres

# Custom entrypoint для инициализации
COPY docker-entrypoint-initdb.d /docker-entrypoint-initdb.d
COPY init.sql /docker-entrypoint-initdb.d/

EXPOSE 5432

CMD ["postgres"]
```

## **Docker Compose advanced**

### **Production-ready compose**
```yaml
version: '3.8'

services:
  web:
    build:
      context: .
      dockerfile: Dockerfile.prod
      args:
        - BUILD_ENV=production
    image: myapp/web:${TAG:-latest}
    deploy:
      replicas: 3
      resources:
        limits:
          cpus: '0.50'
          memory: 512M
        reservations:
          cpus: '0.25'
          memory: 256M
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
      placement:
        constraints:
          - node.role == worker
          - node.labels.type == web
    networks:
      - frontend
      - backend
    depends_on:
      - redis
      - db
    secrets:
      - db_password
    configs:
      - nginx_config
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes --requirepass ${REDIS_PASSWORD}
    deploy:
      placement:
        constraints:
          - node.labels.type == cache
    volumes:
      - redis_data:/data
    networks:
      - backend

  db:
    image: postgres:15
    environment:
      POSTGRES_DB: myapp
      POSTGRES_USER: myapp
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password
    deploy:
      placement:
        constraints:
          - node.labels.type == database
    volumes:
      - db_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    secrets:
      - db_password
    networks:
      - backend

networks:
  frontend:
    driver: overlay
  backend:
    driver: overlay
    internal: true

volumes:
  redis_data:
    driver: local
  db_data:
    driver: local

secrets:
  db_password:
    file: ./secrets/db_password.txt

configs:
  nginx_config:
    file: ./nginx.conf
```

### **Environment management**
```yaml
# docker-compose.override.yml для development
version: '3.8'
services:
  web:
    build:
      context: .
      dockerfile: Dockerfile.dev
    volumes:
      - .:/app
      - /app/node_modules
    environment:
      - DEBUG=true
      - NODE_ENV=development
    ports:
      - "3000:3000"

  db:
    image: postgres:15
    environment:
      POSTGRES_DB: myapp_dev
      POSTGRES_USER: dev
      POSTGRES_PASSWORD: dev123
    ports:
      - "5432:5432"
    volumes:
      - dev_db_data:/var/lib/postgresql/data

volumes:
  dev_db_data:
```

## **Docker Swarm**

### **Swarm mode cluster**
```bash
# Инициализация Swarm
docker swarm init --advertise-addr 192.168.1.100

# Добавление worker нод
docker swarm join-token worker
# На worker ноде выполнить полученную команду

# Добавление manager нод
docker swarm join-token manager

# Просмотр кластера
docker node ls

# Создание overlay сети
docker network create --driver overlay --attachable mynet

# Развертывание stack
docker stack deploy -c docker-compose.yml mystack

# Масштабирование сервисов
docker service scale mystack_web=5

# Обновление сервиса
docker service update --image myapp:v2 mystack_web

# Rolling update
docker service update \
  --update-parallelism 2 \
  --update-delay 10s \
  --update-failure-action rollback \
  --image myapp:v2 \
  mystack_web

# Secrets в Swarm
echo "mysecret" | docker secret create my_secret -
docker service create --secret my_secret nginx

# Configs в Swarm
docker config create nginx_config nginx.conf
docker service create --config nginx_config nginx
```

### **Swarm monitoring**
```bash
# Состояние кластера
docker node ls
docker service ls
docker stack ls

# Логи сервисов
docker service logs mystack_web

# Инспектирование сервисов
docker service inspect mystack_web

# Статистика использования ресурсов
docker stats

# Health checks
docker service ps mystack_web
```

## **Docker Registry**

### **Private registry**
```bash
# Запуск private registry
docker run -d \
  -p 5000:5000 \
  --restart=always \
  --name registry \
  -v /opt/registry:/var/lib/registry \
  -e REGISTRY_STORAGE_FILESYSTEM_ROOTDIRECTORY=/var/lib/registry \
  registry:2

# С аутентификацией
docker run -d \
  -p 5000:5000 \
  --restart=always \
  --name registry \
  -v /opt/registry:/var/lib/registry \
  -v /opt/registry/auth:/auth \
  -e REGISTRY_AUTH=htpasswd \
  -e REGISTRY_AUTH_HTPASSWD_REALM="Registry Realm" \
  -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
  registry:2

# Создание пользователя
docker run --rm \
  --entrypoint htpasswd \
  httpd:2 -bn myuser mypassword > /opt/registry/auth/htpasswd

# Push образа
docker tag myapp:latest localhost:5000/myapp:latest
docker push localhost:5000/myapp:latest

# Pull образа
docker pull localhost:5000/myapp:latest
```

### **Registry API**
```bash
# Список репозиториев
curl -X GET http://localhost:5000/v2/_catalog

# Список тегов
curl -X GET http://localhost:5000/v2/myapp/tags/list

# Удаление образа
curl -X DELETE http://localhost:5000/v2/myapp/manifests/latest

# Garbage collection
docker exec registry bin/registry garbage-collect /etc/docker/registry/config.yml
```

## **Performance optimization**

### **Runtime optimization**
```dockerfile
# Оптимизированный runtime
FROM alpine:latest

# Устанавливаем зависимости в один слой
RUN apk add --no-cache \
    ca-certificates \
    curl \
    && rm -rf /var/cache/apk/* \
    && mkdir -p /app

WORKDIR /app

# Копируем только необходимые файлы
COPY --from=builder /app/myapp /app/myapp

# Используем scratch для минимального размера
FROM scratch
COPY --from=0 /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=0 /app/myapp /myapp

EXPOSE 8080
ENTRYPOINT ["/myapp"]
```

### **Build optimization**
```dockerfile
# BuildKit для параллельной сборки
# syntax=docker/dockerfile:1.4

FROM golang:1.19-alpine AS builder

WORKDIR /app

# Кэшируем зависимости Go
RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=bind,source=go.sum,target=go.sum \
    --mount=type=bind,source=go.mod,target=go.mod \
    go mod download

# Копируем исходный код
COPY . .

# Собираем с кэшированием
RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=cache,target=/root/.cache/go-build \
    go build -o myapp .

FROM alpine:latest

RUN apk add --no-cache ca-certificates
COPY --from=builder /app/myapp /myapp

EXPOSE 8080
CMD ["/myapp"]
```

### **Resource management**
```bash
# Ограничение ресурсов
docker run --cpus=0.5 --memory=512m --memory-swap=1g nginx

# CPU shares
docker run --cpu-shares=512 nginx

# CPU pinning
docker run --cpuset-cpus=0-3 nginx

# IO limits
docker run --device-read-bps /dev/sda:1mb --device-write-bps /dev/sda:1mb nginx

# Block IO weight
docker run --blkio-weight=100 nginx

# Memory limits with swap
docker run --memory=512m --memory-swap=1g nginx

# Out of memory killer
docker run --oom-kill-disable nginx
```

## **Monitoring** и **logging**

### **Container monitoring**
```bash
# Docker stats API
curl --unix-socket /var/run/docker.sock http://localhost/containers/json

# Prometheus metrics
docker run -d \
  -p 9323:9323 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  prom/container-exporter

# cAdvisor для детального мониторинга
docker run -d \
  --volume=/:/rootfs:ro \
  --volume=/var/run:/var/run:ro \
  --volume=/sys:/sys:ro \
  --volume=/var/lib/docker/:/var/lib/docker:ro \
  --publish=8080:8080 \
  --detach=true \
  --name=cadvisor \
  google/cadvisor:latest
```

### **Advanced logging**
```bash
# JSON logging с опциями
docker run --log-driver json-file \
  --log-opt max-size=10m \
  --log-opt max-file=3 \
  --log-opt labels=production,status \
  --log-opt env=OS,TEST \
  nginx

# Syslog
docker run --log-driver syslog \
  --log-opt syslog-address=tcp://192.168.1.100:514 \
  --log-opt syslog-facility=daemon \
  --log-opt tag="{{.Name}}/{{.ID}}" \
  nginx

# Fluentd
docker run --log-driver fluentd \
  --log-opt fluentd-address=192.168.1.100:24224 \
  --log-opt tag=docker.{{.Name}} \
  nginx

# AWS CloudWatch
docker run --log-driver awslogs \
  --log-opt awslogs-region=us-east-1 \
  --log-opt awslogs-group=my-log-group \
  --log-opt awslogs-stream=my-log-stream \
  nginx
```

## CI/CD **integration**

### **GitHub Actions**
```yaml
name: Build and Push Docker Image

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2

    - name: Login to Docker Hub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKERHUB_USERNAME }}
        password: ${{ secrets.DOCKERHUB_TOKEN }}

    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v4
      with:
        images: myapp
        tags: |
          type=ref,event=branch
          type=ref,event=pr
          type=sha,prefix={{branch}}-
          type=raw,value=latest,enable={{is_default_branch}}

    - name: Build and push
      uses: docker/build-push-action@v4
      with:
        context: .
        push: true
        tags: ${{ steps.meta.outputs.tags }}
        labels: ${{ steps.meta.outputs.labels }}
        cache-from: type=gha
        cache-to: type=gha,mode=max
        platforms: linux/amd64,linux/arm64

    - name: Scan image
      uses: aquasecurity/trivy-action@master
      with:
        scan-type: 'image'
        scan-ref: 'myapp:latest'
        format: 'sarif'
        output: 'trivy-results.sarif'

    - name: Upload Trivy scan results
      uses: github/codeql-action/upload-sarif@v2
      if: always()
      with:
        sarif_file: 'trivy-results.sarif'
```

### **Jenkins pipeline**
```groovy
pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'myapp'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Build') {
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").inside {
                        sh 'npm test'
                    }
                }
            }
        }

        stage('Security Scan') {
            steps {
                script {
                    sh "docker run --rm -v /var/run/docker.sock:/var/run/docker.sock " +
                       "aquasec/trivy:latest image --exit-code 0 --no-progress " +
                       "--format table ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }

        stage('Push') {
            steps {
                script {
                    docker.withRegistry('https://registry.example.com', 'registry-credentials') {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push('latest')
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh "kubectl set image deployment/myapp app=${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "kubectl rollout status deployment/myapp"
                }
            }
        }
    }

    post {
        always {
            script {
                sh "docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG} || true"
            }
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
```

## Решение проблем

### Распространенные проблемы
```bash
# Container не запускается
docker logs <container_id>
docker inspect <container_id> | jq '.State'

# Ошибка "no space left on device"
docker system df
docker system prune -a --volumes

# Проблемы с сетью
docker network ls
docker network inspect bridge
docker exec <container> cat /etc/resolv.conf

# Permission denied
docker run --user $(id -u):$(id -g) myapp

# Container exits immediately
docker run -it myapp /bin/bash
# или
docker run myapp tail -f /dev/null

# DNS проблемы
docker run --dns 8.8.8.8 myapp
docker exec <container> nslookup google.com

# Memory issues
docker stats
docker run --memory=512m myapp

# Storage issues
docker volume ls
docker volume inspect <volume>
```

### **Debug containers**
```bash
# Вход в running container
docker exec -it <container> /bin/bash

# Вход в stopped container
docker run -it --volumes-from <stopped_container> ubuntu /bin/bash

# Копирование файлов из container
docker cp <container>:/path/to/file /host/path

# Создание debug образа
FROM myapp:latest
RUN apt-get update && apt-get install -y \
    curl \
    net-tools \
    dnsutils \
    && rm -rf /var/cache/apt/lists/*
CMD ["sleep", "infinity"]
```

### **Performance debugging**
```bash
# CPU profiling
docker stats --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}"

# Memory analysis
docker run --rm -it \
  --pid=container:<container_name> \
  --net=container:<container_name> \
  --cap-add sys_ptrace \
  nicolaka/netshoot \
  ps aux

# Disk I/O monitoring
docker run --rm -it \
  --pid=container:<container_name> \
  nicolaka/netshoot \
  iotop -o

# Network monitoring
docker run --rm -it \
  --net=container:<container_name> \
  nicolaka/netshoot \
  tcpdump -i eth0
```

## **Best practices**

### **Production deployment**
```bash
# Health checks
docker run -d \
  --name myapp \
  --health-cmd='curl -f http://localhost:8080/health || exit 1' \
  --health-interval=30s \
  --health-timeout=10s \
  --health-retries=3 \
  --health-start-period=60s \
  myapp

# Resource limits
docker run -d \
  --name myapp \
  --cpus=1.0 \
  --memory=1g \
  --memory-swap=2g \
  --oom-kill-disable \
  --restart=unless-stopped \
  myapp

# Logging configuration
docker run -d \
  --name myapp \
  --log-driver=json-file \
  --log-opt max-size=100m \
  --log-opt max-file=5 \
  --log-opt labels=com.example.app \
  myapp

# Security options
docker run -d \
  --name myapp \
  --user=1001:1001 \
  --cap-drop=ALL \
  --cap-add=NET_BIND_SERVICE \
  --security-opt=no-new-privileges \
  --read-only \
  --tmpfs=/tmp \
  myapp
```

### **Image management**
```bash
# Multi-architecture builds
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --tag myapp:latest \
  --push \
  .

# Image signing
docker trust key generate mykey
docker trust signer add --key mykey.pub myapp myregistry.com/myapp

# SBOM generation
docker sbom myapp:latest

# Vulnerability scanning
docker scout cves myapp:latest

# Image optimization
docker build --squash -t myapp:optimized .

# Layer analysis
docker history myapp:latest
dive myapp:latest
```
## См. также
- [Kubernetes](../kubernetes/kubernetes-basics.md) — оркестрация контейнеров
- [Terraform](../../iac/terraform/terraform.md) — инфраструктура как код
- [Ansible](../../iac/ansible/ansible.md) — конфигурационное управление
- [Prometheus](../../../monitoring/metrics/prometheus.md) — мониторинг
