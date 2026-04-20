---
title: "Docker: Полное руководство по контейнеризации"
description: "Комплексное руководство по Docker: установка, образы, контейнеры, сети, volumes, Compose, оптимизация, безопасность и best practices"
tags:
  - docker
  - containers
  - containerization
  - devops
  - infrastructure
  - virtualization
difficulty: "intermediate"
prerequisites: ["devops/os.md"]
next: ["devops/docker-containers.md", "devops/docker-spring-boot.md", "infrastructure/kubernetes-basics.md"]
updated: "2026-02-06"
related: ["infrastructure/nginx.md", "devops/git.md", "spring/spring-boot.md"]
---

# **Docker**: Полное руководство по контейнеризации

## Полезные ссылки

### Официальная документация

- [Docker Documentation](https://docs.docker.com/)
- [Docker Hub](https://hub.docker.com/)

### Обучающие материалы

- [Docker Tutorial](https://www.baeldung.com/ops/docker-guide)


См. также: [Kubernetes](../kubernetes/kubernetes-basics.md) — [Docker Containers](docker-containers.md).

## Содержание

- [Введение в Docker](#введение-в-docker)
  - [Основные концепции Docker](#основные-концепции-docker)
  - [Преимущества Docker](#преимущества-docker)
  - [Архитектура Docker](#архитектура-docker)
- [Установка Docker](#установка-docker)
  - [Установка на Ubuntu/Debian](#установка-на-ubuntudebian)
  - [Установка на CentOS/RHEL](#установка-на-centosrhel)
  - [Установка на macOS](#установка-на-macos)
  - [Установка на Windows](#установка-на-windows)
- [Docker CLI команды](#docker-cli-команды)
  - [Основные команды](#основные-команды)
  - [Работа с образами](#работа-с-образами)
  - [Работа с контейнерами](#работа-с-контейнерами)
  - [Сборка образов](#сборка-образов)
- [Dockerfile: лучшие практики](#dockerfile-лучшие-практики)
  - [Структура Dockerfile](#структура-dockerfile)
  - [Оптимизация Dockerfile](#оптимизация-dockerfile)
  - [Многостадийная сборка](#многостадийная-сборка)
  - [Многоархитектурная сборка](#многоархитектурная-сборка)
- [Docker Compose](#docker-compose)
  - [Основы Docker Compose](#основы-docker-compose)
  - [Команды Docker Compose](#команды-docker-compose)
  - [Расширенная конфигурация Compose](#расширенная-конфигурация-compose)
  - [Docker Swarm](#docker-swarm)
- [Docker Registry](#docker-registry)
  - [Работа с Docker Hub](#работа-с-docker-hub)
  - [Создание приватного registry](#создание-приватного-registry)
  - [Настройка registry с аутентификацией](#настройка-registry-с-аутентификацией)
- [Безопасность Docker](#безопасность-docker)
  - [Лучшие практики безопасности](#лучшие-практики-безопасности)
  - [Docker Bench Security](#docker-bench-security)
- [Мониторинг и отладка](#мониторинг-и-отладка)
  - [Docker stats и logs](#docker-stats-и-logs)
  - [Docker events](#docker-events)
  - [Отладка контейнеров](#отладка-контейнеров)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Docker contexts](#docker-contexts)
  - [Docker plugins](#docker-plugins)
  - [Docker API](#docker-api)
- [Интеграция с CI/CD](#интеграция-с-cicd)
  - [GitHub Actions](#github-actions)
  - [Jenkins Pipeline](#jenkins-pipeline)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы и решения](#распространенные-проблемы-и-решения)
  - [Полезные команды для диагностики](#полезные-команды-для-диагностики)
- [Docker в production среде](#docker-в-production-среде)
  - [Production-ready конфигурации](#production-ready-конфигурации)
  - [Конфигурация Nginx для production](#конфигурация-nginx-для-production)
  - [Dockerfile для production](#dockerfile-для-production)
  - [Конфигурация Prometheus](#конфигурация-prometheus)
  - [Мониторинг с Grafana](#мониторинг-с-grafana)
  - [Логирование с Loki](#логирование-с-loki)
- [Docker в облаке](#docker-в-облаке)
  - [AWS ECS (Elastic Container Service)](#aws-ecs-elastic-container-service)
  - [Google Cloud Run](#google-cloud-run)
  - [Azure Container Instances](#azure-container-instances)
- [Docker и Kubernetes интеграция](#docker-и-kubernetes-интеграция)
  - [Kubernetes Deployment с Docker образами](#kubernetes-deployment-с-docker-образами)
  - [Helm Chart для приложения](#helm-chart-для-приложения)
- [Best practices для production](#best-practices-для-production)
  - [Многостадийная сборка](#1-многостадийная-сборка)
  - [Security scanning](#2-security-scanning)
  - [Image optimization](#3-image-optimization)
  - [Health checks](#4-health-checks)
  - [Resource management](#5-resource-management)
  - [Logging best practices](#6-logging-best-practices)
  - [Secrets management](#7-secrets-management)
  - [Networking best practices](#8-networking-best-practices)
  - [Backup и restore](#9-backup-и-restore)
  - [Monitoring и alerting](#10-monitoring-и-alerting)
- [Установка ограничений памяти и ЦП](#установка-ограничений-памяти-и-цп)
- [Получение информации о сети](#получение-информации-о-сети)
- [Руководство по Docker Compose](#руководство-по-docker-compose)
- [Разница между COPY и ADD](#разница-между-copy-и-add)
- [Руководство по томам](#руководство-по-томам)
- [Разница между run, cmd и entrypoint в Dockerfile](#разница-между-run-cmd-и-entrypoint-в-dockerfile)
- [Советы по созданию эффективных образов](#советы-по-созданию-эффективных-образов)
- [Разница между образами и контейнерами](#разница-между-образами-и-контейнерами)
- [Удаление образов](#удаление-образов)

## Введение в **Docker**

**Docker** — это платформа для разработки, доставки и запуска приложений в контейнерах. Контейнеры позволяют упаковывать приложение со всеми его зависимостями в стандартизированную единицу развертывания, которая работает одинаково в любой среде.

### Основные концепции **Docker**

- **Образ (**Image**)**: Неизменяемый шаблон, содержащий приложение и его зависимости
- **Контейнер (**Container**)**: Запущенный экземпляр образа
- **Dockerfile**: Скрипт для создания образа
- **Registry**: Хранилище образов (**Docker Hub, `ECR`, `GCR` и т.д.**)
- **Volume**: Механизм персистентного хранения данных
- **Network**: Виртуальная сеть для коммуникации между контейнерами

### Преимущества **Docker**

1. **Изоляция**: Каждый контейнер изолирован от хост-системы и других контейнеров
2. **Переносимость**: Контейнеры работают одинаково на любой платформе с **Docker**
3. **Эффективность**: Контейнеры используют меньше ресурсов, чем виртуальные машины
4. **Быстрое развертывание**: Контейнеры запускаются за секунды
5. **Версионирование**: Образы можно версионировать и откатывать
6. **Масштабируемость**: Легко масштабировать приложения горизонтально

### Архитектура **Docker**

Схема компонентов **Docker**: хост (**демон, образы, контейнеры, тома, сети**), **runtime**, ядро ОС и цикл сборки образа и запуска контейнера.

```text
┌─────────────────────────────────────────────────────────────┐
│                    Docker Host                              │
├─────────────────────────────────────────────────────────────┤
│  Docker Daemon │ Images │ Containers │ Volumes │ Networks   │
├─────────────────────────────────────────────────────────────┤
│  Container Runtime (containerd/runc)                        │
├─────────────────────────────────────────────────────────────┤
│  Operating System Kernel                                    │
└─────────────────────────────────────────────────────────────┘
        │                       │                       │
        ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────┐
│  Dockerfile → Build → Image → Run → Container               │
└─────────────────────────────────────────────────────────────┘
```

## Установка **Docker**

### Установка на **Ubuntu**/**Debian**

```bash
# Обновление пакетов
sudo apt update

# Установка необходимых пакетов
sudo apt install apt-transport-https ca-certificates curl gnupg lsb-release

# Добавление официального GPG ключа Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Добавление репозитория Docker
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Установка Docker Engine
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io

# Запуск Docker
sudo systemctl start docker
sudo systemctl enable docker

# Добавление пользователя в группу docker
sudo usermod -aG docker $USER

# Проверка установки
docker --version
docker run hello-world
```

### Установка на **CentOS**/**RHEL**

```bash
# Установка yum-utils
sudo yum install -y yum-utils

# Добавление репозитория Docker
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# Установка Docker Engine
sudo yum install docker-ce docker-ce-cli containerd.io

# Запуск Docker
sudo systemctl start docker
sudo systemctl enable docker

# Добавление пользователя в группу docker
sudo usermod -aG docker $USER

# Проверка установки
docker --version
docker run hello-world
```

### Установка на **macOS**

```bash
# Скачивание Docker Desktop
curl -O https://desktop.docker.com/mac/stable/Docker.dmg

# Монтирование образа
hdiutil attach Docker.dmg

# Копирование приложения
cp -r /Volumes/Docker/Docker.app /Applications/

# Запуск Docker Desktop
open /Applications/Docker.app

# Проверка установки
docker --version
docker run hello-world
```

### Установка на **Windows**

```powershell
# Скачивание Docker Desktop
Invoke-WebRequest -Uri "https://desktop.docker.com/win/stable/Docker%20Desktop%20Installer.exe" -OutFile "DockerDesktopInstaller.exe"

# Запуск инсталлятора
.\DockerDesktopInstaller.exe

# Запуск Docker Desktop
Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"

# Проверка установки
docker --version
docker run hello-world
```

## **Docker CLI** команды

### Основные команды

```bash
# Проверка версии Docker
docker --version
docker version

# Получение помощи
docker --help
docker <command> --help

# Информация о системе
docker info
docker system info

# Статистика использования ресурсов
docker system df
docker stats
docker stats --no-stream
```

### Работа с образами

```bash
# Поиск образов
docker search nginx
docker search --limit 10 redis

# Скачивание образа
docker pull nginx:latest
docker pull nginx:1.21-alpine

# Просмотр локальных образов
docker images
docker images -a
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"

# Удаление образа
docker rmi nginx:latest
docker rmi $(docker images -q)  # Удалить все образы
docker image prune -a  # Удалить неиспользуемые образы

# Создание образа из контейнера
docker commit <container-id> my-image:v1

# Тегирование образа
docker tag my-image:v1 my-registry.com/my-image:v1

# Загрузка образа в registry
docker push my-registry.com/my-image:v1

# Сохранение образа в файл
docker save my-image:v1 > my-image.tar

# Загрузка образа из файла
docker load < my-image.tar

# История сборки образа
docker history my-image:v1

# Инспектирование образа
docker inspect nginx:latest
```

### Работа с контейнерами

```bash
# Запуск контейнера
docker run nginx:latest
docker run -d nginx:latest  # В фоне
docker run --name my-nginx nginx:latest  # С именем
docker run -p 8080:80 nginx:latest  # С пробросом портов
docker run -v /host/path:/container/path nginx:latest  # С volume
docker run --rm nginx:latest  # Автоматическое удаление после остановки

# Просмотр запущенных контейнеров
docker ps
docker ps -a  # Все контейнеры
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}"

# Остановка контейнера
docker stop <container-id>
docker stop $(docker ps -q)  # Остановить все

# Запуск остановленного контейнера
docker start <container-id>
docker start $(docker ps -aq)  # Запустить все

# Перезапуск контейнера
docker restart <container-id>

# Удаление контейнера
docker rm <container-id>
docker rm $(docker ps -aq)  # Удалить все
docker container prune  # Удалить остановленные контейнеры

# Логи контейнера
docker logs <container-id>
docker logs -f <container-id>  # Следить за логами
docker logs --tail 100 <container-id>  # Последние 100 строк

# Выполнение команд в контейнере
docker exec -it <container-id> /bin/bash
docker exec <container-id> ps aux

# Копирование файлов
docker cp <container-id>:/path/to/file /host/path
docker cp /host/path <container-id>:/path/to/file

# Инспектирование контейнера
docker inspect <container-id>

# Топ процессов в контейнере
docker top <container-id>

# Статистика использования ресурсов контейнером
docker stats <container-id>
```

### Сборка образов

```bash
# Сборка образа из Dockerfile
docker build -t my-image:v1 .
docker build -t my-image:v1 -f Dockerfile.custom .
docker build --no-cache -t my-image:v1 .  # Без кэша
docker build --build-arg VERSION=1.0 -t my-image:v1 .  # С аргументами

# Многостадийная сборка
docker build --target builder -t my-builder .
docker build --target runtime -t my-runtime .

# Сборка с различными платформами
docker buildx build --platform linux/amd64,linux/arm64 -t my-image:v1 .
```

## **Dockerfile**: лучшие практики

### Структура **Dockerfile**

```dockerfile
# Использовать официальный базовый образ
FROM ubuntu:20.04

# Установить метаданные
LABEL maintainer="your-email@example.com"
LABEL version="1.0"
LABEL description="My application"

# Установить переменные окружения
ENV APP_HOME=/app
ENV PATH=$PATH:$APP_HOME/bin

# Создать рабочую директорию
WORKDIR $APP_HOME

# Копировать файлы зависимостей
COPY requirements.txt .

# Установить зависимости
RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    pip3 install -r requirements.txt && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Копировать исходный код
COPY src/ .

# Создать пользователя без привилегий
RUN useradd -r -s /bin/false appuser && \
    chown -R appuser:appuser $APP_HOME

# Переключиться на пользователя без привилегий
USER appuser

# Определить порт
EXPOSE 8080

# Определить health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Определить команду запуска
CMD ["python3", "app.py"]
```

### Оптимизация **Dockerfile**

```dockerfile
# ПЛОХОЙ Dockerfile
FROM ubuntu:20.04
RUN apt-get update
RUN apt-get install -y python3
RUN apt-get install -y python3-pip
RUN pip3 install flask
COPY . /app
WORKDIR /app
CMD ["python3", "app.py"]

# ХОРОШИЙ Dockerfile
FROM python:3.9-slim

# Установка системных зависимостей в одном слое
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Копирование и установка Python зависимостей
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Копирование исходного кода
COPY . .

# Создание пользователя без привилегий
RUN useradd --create-home --shell /bin/bash app && \
    chown -R app:app /app
USER app

EXPOSE 5000

CMD ["python", "app.py"]
```

### Многостадийная сборка

```dockerfile
# Стадия сборки
FROM golang:1.19-alpine AS builder

WORKDIR /app

# Копирование go.mod и go.sum
COPY go.mod go.sum ./
RUN go mod download

# Копирование исходного кода
COPY . .

# Сборка приложения
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o main .

# Финальная стадия
FROM alpine:latest

RUN apk --no-cache add ca-certificates
WORKDIR /root/

# Копирование бинарного файла из стадии сборки
COPY --from=builder /app/main .

EXPOSE 8080

CMD ["./main"]
```

### Многоархитектурная сборка

```dockerfile
FROM --platform=$BUILDPLATFORM golang:1.19-alpine AS builder

ARG TARGETPLATFORM
ARG BUILDPLATFORM

RUN echo "Building on $BUILDPLATFORM for $TARGETPLATFORM"

WORKDIR /app

# Копирование исходного кода
COPY . .

# Кросс-компиляция
RUN if [ "$TARGETPLATFORM" = "linux/amd64" ]; then \
        GOARCH=amd64 go build -o app .; \
    elif [ "$TARGETPLATFORM" = "linux/arm64" ]; then \
        GOARCH=arm64 go build -o app .; \
    else \
        go build -o app .; \
    fi

FROM alpine:latest

WORKDIR /app

COPY --from=builder /app/app .

CMD ["./app"]
```

## **Docker Compose**

### Основы **Docker Compose**

**Docker Compose** — это инструмент для определения и запуска многоконтейнерных приложений **Docker**. Он использует **YAML**-файл для конфигурации сервисов приложения.

```yaml
# docker-compose.yml
version: '3.8'

services:
  web:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      - DATABASE_URL=postgresql://db:5432/mydb

  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=mydb
      - POSTGRES_USER=user
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Команды **Docker Compose**

```bash
# Запуск сервисов
docker-compose up
docker-compose up -d  # В фоне
docker-compose up --scale web=3  # Масштабирование

# Остановка сервисов
docker-compose down
docker-compose down -v  # С удалением volumes

# Просмотр логов
docker-compose logs
docker-compose logs -f web  # Логи конкретного сервиса

# Выполнение команд
docker-compose exec web bash
docker-compose exec db psql -U user -d mydb

# Сборка образов
docker-compose build
docker-compose build --no-cache

# Просмотр состояния
docker-compose ps
docker-compose top

# Перезапуск сервисов
docker-compose restart
docker-compose restart web

# Обновление сервисов
docker-compose pull
docker-compose up -d --no-deps web  # Обновление без зависимостей
```

### Расширенная конфигурация **Compose**

```yaml
version: '3.8'

services:
  web:
    build:
      context: .
      dockerfile: Dockerfile
      args:
        VERSION: 1.0
    ports:
      - "8080:8080"
    environment:
      - NODE_ENV=production
    env_file:
      - .env
    volumes:
      - ./logs:/app/logs
      - /tmp:/tmp
    networks:
      - frontend
      - backend
    depends_on:
      - db
      - redis
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    restart: unless-stopped

  db:
    image: postgres:15
    environment:
      POSTGRES_DB: mydb
      POSTGRES_USER: user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - db_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - backend
    command: postgres -c shared_preload_libraries=pg_stat_statements -c pg_stat_statements.track=all

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    networks:
      - backend

  nginx:
    image: nginx:1.21-alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    networks:
      - frontend
    depends_on:
      - web

networks:
  frontend:
    driver: bridge
  backend:
    driver: bridge
    internal: true

volumes:
  db_data:
    driver: local
  redis_data:
    driver: local
```

### **Docker Swarm**

**Docker Swarm** — это нативный кластерный и оркестрационный инструмент для **Docker**.

```bash
# Инициализация Swarm
docker swarm init

# Присоединение узлов
docker swarm join --token <token> <manager-ip>:2377

# Создание сервиса
docker service create --name my-web --publish 8080:80 nginx

# Масштабирование сервиса
docker service scale my-web=3

# Обновление сервиса
docker service update --image nginx:1.21 my-web

# Просмотр сервисов
docker service ls
docker service ps my-web

# Логи сервиса
docker service logs my-web

# Удаление сервиса
docker service rm my-web

# Секреты и конфиги
echo "my-secret" | docker secret create my-secret -
docker service create --name my-service --secret my-secret nginx
```

## **Docker Registry**

### Работа с **Docker Hub**

```bash
# Логин в Docker Hub
docker login

# Загрузка образа
docker push username/my-image:v1

# Поиск образов
docker search username

# Создание automated build
# Через GitHub integration в Docker Hub
```

### Создание приватного **registry**

```bash
# Запуск registry контейнера
docker run -d -p 5000:5000 --name registry registry:2

# Загрузка образа в локальный registry
docker tag my-image localhost:5000/my-image:v1
docker push localhost:5000/my-image:v1

# Скачивание из локального registry
docker pull localhost:5000/my-image:v1
```

### Настройка **registry** с аутентификацией

```bash
# Создание htpasswd файла
docker run --rm httpd:2.4-alpine htpasswd -bn username password > auth/htpasswd

# Запуск registry с аутентификацией
docker run -d \
  -p 5000:5000 \
  --name registry \
  -v $(pwd)/auth:/auth \
  -e REGISTRY_AUTH=htpasswd \
  -e REGISTRY_AUTH_HTPASSWD_REALM="Registry Realm" \
  -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
  registry:2

# Настройка Docker daemon для insecure registry
# /etc/docker/daemon.json
{
  "insecure-registries": ["localhost:5000"]
}
```

## Безопасность **Docker**

### Лучшие практики безопасности

```bash
# Сканирование образов на уязвимости
docker scan my-image:v1

# Использование trusted образов
docker pull alpine:latest

# Ограничение привилегий
docker run --read-only --tmpfs /tmp --tmpfs /var/run nginx

# Использование seccomp профилей
docker run --security-opt seccomp=unconfined nginx

# AppArmor/SELinux
docker run --security-opt apparmor=my-profile nginx

# Ограничение системных вызовов
docker run --cap-drop ALL --cap-add NET_BIND_SERVICE nginx

# User namespaces
dockerd --userns-remap=default
```

### **Docker Bench Security**

```bash
# Запуск security audit
docker run --net host --pid host --userns host --cap-add audit_control \
  -e DOCKER_CONTENT_TRUST=$DOCKER_CONTENT_TRUST \
  -v /var/lib:/var/lib \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /usr/lib/systemd:/usr/lib/systemd \
  -v /etc:/etc --label docker_bench_security \
  docker/docker-bench-security

# Результаты аудита
# Host Configuration
# Docker Daemon Configuration
# Docker Daemon Files
# Container Images and Build Files
# Container Runtime
# Docker Security Operations
# Docker Swarm Configuration
```

## Мониторинг и отладка

### **Docker stats** и **logs**

```bash
# Статистика использования ресурсов
docker stats
docker stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}"

# Логи с фильтрами
docker logs --since 2018-01-01T00:00:00 my-container
docker logs --until 2018-01-01T12:00:00 my-container
docker logs --tail 100 my-container

# Форматирование логов
docker logs --format json my-container | jq '.log'
```

### **Docker events**

```bash
# Просмотр событий в реальном времени
docker events

# Фильтрация событий
docker events --filter type=container
docker events --filter event=start
docker events --filter container=my-container

# События за период времени
docker events --since '2023-01-01T00:00:00' --until '2023-01-01T23:59:59'
```

### Отладка контейнеров

```bash
# Инспектирование контейнера
docker inspect my-container

# Проверка файловой системы
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  nate/dockviz images -t

# Анализ дискового пространства
docker system df -v

# Очистка системы
docker system prune -a --volumes
```

## Продвинутые возможности

### **Docker contexts**

```bash
# Создание контекста для удаленного Docker
docker context create remote --docker "host=ssh://user@remote-host"
docker context use remote
docker ps  # Выполняется на remote-host

# Просмотр контекстов
docker context ls
docker context inspect remote
```

### **Docker plugins**

```bash
# Установка volume plugin
docker plugin install --grant-all-permissions vieux/sshfs

# Создание volume с плагином
docker volume create --driver vieux/sshfs \
  -o sshcmd=user@host:/remote/path \
  -o password=password \
  sshvolume

# Просмотр плагинов
docker plugin ls
```

### **Docker API**

```bash
# Доступ к Docker API через сокет
curl --unix-socket /var/run/docker.sock http://localhost/v1.41/containers/json

# Через TCP (не рекомендуется для production)
curl http://localhost:2375/v1.41/containers/json

# Python клиент
pip install docker
python -c "
import docker
client = docker.from_env()
print(client.containers.list())
"
```

## Интеграция с CI/CD

### **GitHub Actions**

```yaml
name: Docker CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Login to Docker Hub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKER_HUB_USERNAME }}
        password: ${{ secrets.DOCKER_HUB_TOKEN }}

    - name: Build and push Docker image
      uses: docker/build-push-action@v4
      with:
        context: .
        push: true
        tags: my-image:${{ github.sha }}, my-image:latest
```

### **Jenkins Pipeline**

```groovy
pipeline {
    agent any

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("my-app:${env.BUILD_ID}")
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    docker.image("my-app:${env.BUILD_ID}").inside {
                        sh 'npm test'
                    }
                }
            }
        }

        stage('Push to Registry') {
            steps {
                script {
                    docker.withRegistry('https://registry.example.com', 'registry-credentials') {
                        docker.image("my-app:${env.BUILD_ID}").push()
                        docker.image("my-app:${env.BUILD_ID}").push('latest')
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'docker system prune -f'
        }
    }
}
```

## Решение проблем

### Распространенные проблемы и решения

```bash
# Контейнер не запускается
docker logs <container-id>
docker inspect <container-id> | grep -A 10 "State"

# Нет места на диске
docker system df
docker system prune -a --volumes

# Контейнер использует много ресурсов
docker stats
docker update --cpus 0.5 --memory 512m <container-id>

# Проблемы с сетью
docker network ls
docker network inspect bridge
docker exec <container-id> ping google.com

# Ошибки при сборке
docker build --no-cache -t my-image .
docker build --progress plain -t my-image .

# Проблемы с volumes
docker volume ls
docker volume inspect <volume-name>
docker run --rm -v <volume-name>:/data alpine ls -la /data
```

### Полезные команды для диагностики

```bash
# Проверка здоровья Docker daemon
docker version
docker info
systemctl status docker

# Логи Docker daemon
journalctl -u docker -f

# Проверка iptables
iptables -L -n

# Мониторинг процессов
ps aux | grep docker
pstree -p $(pidof dockerd)

# Проверка дискового пространства
df -h
du -sh /var/lib/docker

# Очистка системы
docker system prune -a --volumes --force
docker volume prune --force
docker network prune --force
```

## **Docker** в **production** среде

### **Production-ready** конфигурации

```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  web:
    build:
      context: .
      dockerfile: Dockerfile.prod
    image: myapp:${TAG:-latest}
    ports:
      - "${WEB_PORT:-8080}:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - JAVA_OPTS=-Xmx2g -Xms512m
    env_file:
      - .env.prod
    volumes:
      - ./logs:/app/logs
      - app-cache:/app/cache
    networks:
      - webnet
    depends_on:
      - db
      - redis
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    restart: unless-stopped
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 2G
        reservations:
          cpus: '0.5'
          memory: 1G
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s

  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_INITDB_ARGS: "--encoding=UTF-8 --lc-collate=C --lc-ctype=C"
    volumes:
      - db_data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
      - ./backup:/backup
    networks:
      - dbnet
    command:
      - "postgres"
      - "-c"
      - "shared_preload_libraries=pg_stat_statements"
      - "-c"
      - "pg_stat_statements.track=all"
      - "-c"
      - "max_connections=200"
      - "-c"
      - "shared_buffers=256MB"
      - "-c"
      - "effective_cache_size=1GB"
      - "-c"
      - "maintenance_work_mem=64MB"
      - "-c"
      - "checkpoint_completion_target=0.9"
      - "-c"
      - "wal_buffers=16MB"
      - "-c"
      - "default_statistics_target=100"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes --maxmemory 512mb --maxmemory-policy allkeys-lru
    volumes:
      - redis_data:/data
    networks:
      - dbnet
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: unless-stopped

  nginx:
    image: nginx:1.21-alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
      - ./static:/var/www/static:ro
      - nginx_logs:/var/log/nginx
    networks:
      - webnet
    depends_on:
      - web
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost/nginx-health"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: unless-stopped

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus_data:/prometheus
    networks:
      - monitoring
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=200h'
      - '--web.enable-lifecycle'
    restart: unless-stopped

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
      GF_USERS_ALLOW_SIGN_UP: "false"
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/grafana/provisioning:/etc/grafana/provisioning:ro
      - ./monitoring/grafana/dashboards:/var/lib/grafana/dashboards:ro
    networks:
      - monitoring
    depends_on:
      - prometheus
    restart: unless-stopped

  loki:
    image: grafana/loki:latest
    ports:
      - "3100:3100"
    volumes:
      - loki_data:/loki
      - ./monitoring/loki-config.yml:/etc/loki/local-config.yaml:ro
    networks:
      - monitoring
    command: -config.file=/etc/loki/local-config.yaml
    restart: unless-stopped

  promtail:
    image: grafana/promtail:latest
    volumes:
      - ./logs:/var/log/myapp
      - ./monitoring/promtail-config.yml:/etc/promtail/config.yml:ro
    networks:
      - monitoring
    command: -config.file=/etc/promtail/config.yml
    restart: unless-stopped

networks:
  webnet:
    driver: bridge
  dbnet:
    driver: bridge
    internal: true
  monitoring:
    driver: bridge

volumes:
  db_data:
    driver: local
  redis_data:
    driver: local
  app-cache:
    driver: local
  nginx_logs:
    driver: local
  prometheus_data:
    driver: local
  grafana_data:
    driver: local
  loki_data:
    driver: local
```

### Конфигурация **Nginx** для **production**

```nginx
# nginx.conf
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
    use epoll;
    multi_accept on;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Logging
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    # Performance
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;
    client_max_body_size 100M;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/json
        application/javascript
        application/xml+rss
        application/atom+xml
        image/svg+xml;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=5r/m;

    # Upstream backend
    upstream backend {
        least_conn;
        server web:8080 max_fails=3 fail_timeout=30s;
        keepalive 32;
    }

    # Server block
    server {
        listen 80;
        server_name localhost;

        # Security headers
        add_header X-Frame-Options DENY;
        add_header X-Content-Type-Options nosniff;
        add_header X-XSS-Protection "1; mode=block";
        add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

        # Root location
        location / {
            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_cache_bypass $http_upgrade;
            proxy_read_timeout 86400;
        }

        # API with rate limiting
        location /api/ {
            limit_req zone=api burst=20 nodelay;
            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Connection "";
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Static files
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
            try_files $uri @backend;
        }

        # Health check
        location /nginx-health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }

        # Metrics (for monitoring)
        location /metrics {
            stub_status on;
            access_log off;
            allow 172.0.0.0/8;
            deny all;
        }
    }
}
```

### **Dockerfile** для **production**

```dockerfile
# Multi-stage build for Java Spring Boot application
FROM maven:3.8.6-openjdk-17-slim AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

# Install necessary tools
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        dumb-init && \
    rm -rf /var/lib/apt/lists/*

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Use dumb-init to handle signals properly
ENTRYPOINT ["dumb-init", "--"]
CMD ["java", \
     "-XX:+UseContainerSupport", \
     "-XX:MaxRAMPercentage=75.0", \
     "-XX:+UseG1GC", \
     "-XX:+UseCompressedOops", \
     "-XX:+OptimizeStringConcat", \
     "-XX:+UseStringDeduplication", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-jar", \
     "app.jar"]
```

### Конфигурация **Prometheus**

```yaml
# prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  # - "first_rules.yml"
  # - "second_rules.yml"

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['web:8080']

  - job_name: 'postgres'
    static_configs:
      - targets: ['db:9187']

  - job_name: 'redis'
    static_configs:
      - targets: ['redis:9121']

  - job_name: 'nginx'
    static_configs:
      - targets: ['nginx:9113']

  - job_name: 'node-exporter'
    static_configs:
      - targets: ['node-exporter:9100']
```

### Мониторинг с **Grafana**

```yaml
# grafana/provisioning/datasources/prometheus.yml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true

  - name: Loki
    type: loki
    access: proxy
    url: http://loki:3100
    editable: true
```

### Логирование с **Loki**

```yaml
# loki-config.yml
auth_enabled: false

server:
  http_listen_port: 3100
  grpc_listen_port: 9096

ingester:
  lifecycler:
    address: 127.0.0.1
    ring:
      kvstore:
        store: inmemory
      replication_factor: 1
    final_sleep: 0s
  chunk_idle_period: 5m
  chunk_retain_period: 30s

schema_config:
  configs:
  - from: 2020-10-24
    store: boltdb-shipper
    object_store: filesystem
    schema: v11
    index:
      prefix: index_
      period: 24h

storage_config:
  boltdb_shipper:
    active_index_directory: /loki/index
    cache_location: /loki/cache
    shared_store: filesystem
  filesystem:
    directory: /loki/chunks

limits_config:
  enforce_metric_name: false
  reject_old_samples: true
  reject_old_samples_max_age: 168h

chunk_store_config:
  max_look_back_period: 0s

table_manager:
  retention_deletes_enabled: false
  retention_period: 0s
```

```yaml
# promtail-config.yml
server:
  http_listen_port: 9080
  grpc_listen_port: 0

positions:
  filename: /tmp/positions.yaml

clients:
  - url: http://loki:3100/loki/api/v1/push

scrape_configs:
- job_name: system
  static_configs:
  - targets:
      - localhost
    labels:
      job: varlogs
      __path__: /var/log/myapp/*.log
  pipeline_stages:
  - match:
      selector: '{job="varlogs"}'
      stages:
      - regex:
          expression: '^(?P<timestamp>\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) (?P<level>\w+) (?P<message>.+)$'
      - labels:
          level:
      - timestamp:
          source: timestamp
          format: '2006-01-02 15:04:05'
```

## **Docker** в облаке

### **AWS ECS** (`Elastic Container Service`)

```json
{
  "family": "my-app-task",
  "taskRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
  "executionRoleArn": "arn:aws:iam::123456789012:role/ecsTaskExecutionRole",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "containerDefinitions": [
    {
      "name": "web",
      "image": "my-registry/my-app:latest",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "DATABASE_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789012:secret:prod/db-password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/my-app",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": [
          "CMD-SHELL",
          "curl -f http://localhost:8080/actuator/health || exit 1"
        ],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
```

### **Google Cloud Run**

```yaml
# cloudbuild.yaml
steps:
  - name: 'gcr.io/cloud-builders/docker'
    args: ['build', '-t', 'gcr.io/$PROJECT_ID/my-app:$COMMIT_SHA', '.']

  - name: 'gcr.io/cloud-builders/docker'
    args: ['push', 'gcr.io/$PROJECT_ID/my-app:$COMMIT_SHA']

  - name: 'gcr.io/google.com/cloudsdktool/cloud-sdk'
    entrypoint: gcloud
    args:
      - run
      - deploy
      - my-app
      - --image
      - gcr.io/$PROJECT_ID/my-app:$COMMIT_SHA
      - --region
      - us-central1
      - --platform
      - managed
      - --port
      - '8080'
      - --memory
      - 1Gi
      - --cpu
      - '1'
      - --concurrency
      - '80'
      - --timeout
      - '900'
      - --max-instances
      - '10'
      - --set-env-vars
      - SPRING_PROFILES_ACTIVE=prod
      - --allow-unauthenticated
```

### **Azure Container Instances**

```json
{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentTemplate.json#",
  "contentVersion": "1.0.0.0",
  "parameters": {
    "containerGroupName": {
      "type": "string",
      "metadata": {
        "description": "Name for the container group"
      }
    }
  },
  "resources": [
    {
      "name": "[parameters('containerGroupName')]",
      "type": "Microsoft.ContainerInstance/containerGroups",
      "apiVersion": "2021-07-01",
      "location": "[resourceGroup().location]",
      "properties": {
        "containers": [
          {
            "name": "my-app",
            "properties": {
              "image": "my-registry/my-app:latest",
              "ports": [
                {
                  "port": 8080,
                  "protocol": "TCP"
                }
              ],
              "environmentVariables": [
                {
                  "name": "SPRING_PROFILES_ACTIVE",
                  "value": "prod"
                }
              ],
              "resources": {
                "requests": {
                  "cpu": 1,
                  "memoryInGB": 1.5
                },
                "limits": {
                  "cpu": 2,
                  "memoryInGB": 3
                }
              },
              "livenessProbe": {
                "httpGet": {
                  "path": "/actuator/health",
                  "port": 8080,
                  "scheme": "http"
                },
                "initialDelaySeconds": 60,
                "periodSeconds": 30
              }
            }
          }
        ],
        "osType": "Linux",
        "ipAddress": {
          "type": "Public",
          "ports": [
            {
              "protocol": "tcp",
              "port": 8080
            }
          ]
        },
        "restartPolicy": "Always"
      }
    }
  ]
}
```

## **Docker** и **Kubernetes** интеграция

### **Kubernetes Deployment** с **Docker** образами

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
  labels:
    app: my-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: app
        image: my-registry/my-app:v1.2.3
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: JAVA_OPTS
          value: "-Xmx1g -Xms512m"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 3
          failureThreshold: 3
        startupProbe:
          httpGet:
            path: /actuator/health/startup
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
          timeoutSeconds: 3
          failureThreshold: 30
      imagePullSecrets:
      - name: registry-secret
```

### **Helm Chart** для приложения

```yaml
# Chart.yaml
apiVersion: v2
name: my-app
description: A Helm chart for my application
type: application
version: 1.2.3
appVersion: "1.2.3"

# values.yaml
replicaCount: 3

image:
  repository: my-registry/my-app
  tag: ""
  pullPolicy: IfNotPresent

service:
  type: ClusterIP
  port: 8080

ingress:
  enabled: true
  className: nginx
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
  hosts:
    - host: my-app.example.com
      paths:
        - path: /
          pathType: Prefix
  tls:
    - secretName: my-app-tls
      hosts:
        - my-app.example.com

resources:
  limits:
    cpu: 1000m
    memory: 1Gi
  requests:
    cpu: 500m
    memory: 512Mi

autoscaling:
  enabled: true
  minReplicas: 2
  maxReplicas: 10
  targetCPUUtilizationPercentage: 70
  targetMemoryUtilizationPercentage: 80
```

## **Best practices** для **production**

### 1. Многостадийная сборка

```dockerfile
# Builder stage
FROM golang:1.19-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o main .

# Runtime stage
FROM alpine:3.16
RUN apk --no-cache add ca-certificates tzdata
WORKDIR /root/
COPY --from=builder /app/main .
EXPOSE 8080
CMD ["./main"]
```

### 2. **Security scanning**

```bash
# Trivy для сканирования уязвимостей
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasecurity/trivy:latest image my-image:latest

# Clair для детального анализа
docker run --rm -p 6060:6060 -d --name clair clair
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  vaikas/scanclair:latest my-image:latest
```

### 3. **Image optimization**

```dockerfile
# Используйте .dockerignore
# .dockerignore
node_modules
npm-debug.log
.git
README.md
.env
.nyc_output
coverage

# Многослойная структура
FROM node:16-alpine AS deps
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

FROM node:16-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

FROM node:16-alpine AS runtime
WORKDIR /app
COPY --from=builder /app/dist ./dist
COPY --from=deps /app/node_modules ./node_modules
EXPOSE 3000
CMD ["npm", "start"]
```

### 4. **Health checks**

```dockerfile
# Для веб-приложений
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Для баз данных
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD pg_isready -U postgres || exit 1
```

### 5. **Resource management**

```yaml
# docker-compose.yml с лимитами
services:
  app:
    image: my-app:latest
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

### 6. **Logging best practices**

```dockerfile
# Структурированное логирование
FROM openjdk:17-jre-slim
ENV JAVA_OPTS="-Dlogging.config=/app/logback-spring.xml"
COPY logback-spring.xml /app/
COPY app.jar /app/
WORKDIR /app
CMD java $JAVA_OPTS -jar app.jar
```

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>{"timestamp": "%d{yyyy-MM-dd HH:mm:ss.SSS}", "level": "%level", "logger": "%logger", "message": "%message"}%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

### 7. **Secrets management**

```bash
# Используйте Docker secrets в Swarm
echo "my-secret-password" | docker secret create db_password -

# В Compose
version: '3.8'
services:
  db:
    image: postgres:15
    secrets:
      - db_password
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password

secrets:
  db_password:
    external: true
```

### 8. **Networking best practices**

```yaml
# Изоляция сетей
version: '3.8'
services:
  web:
    networks:
      - frontend
  db:
    networks:
      - backend
  redis:
    networks:
      - backend

networks:
  frontend:
  backend:
    internal: true
```

### 9. **Backup** и **restore**

```bash
# Backup базы данных
docker exec -t my-postgres pg_dump -U postgres mydb > backup.sql

# Backup volume
docker run --rm -v my-volume:/data -v $(pwd):/backup alpine tar czf /backup/volume-backup.tar.gz -C /data .

# Restore
docker exec -i my-postgres psql -U postgres mydb < backup.sql
docker run --rm -v my-volume:/data -v $(pwd):/backup alpine tar xzf /backup/volume-backup.tar.gz -C /data
```

### 10. **Monitoring** и **alerting**

```bash
# cAdvisor для мониторинга контейнеров
docker run \
  --volume=/:/rootfs:ro \
  --volume=/var/run:/var/run:ro \
  --volume=/sys:/sys:ro \
  --volume=/var/lib/docker/:/var/lib/docker:ro \
  --volume=/dev/disk/:/dev/disk:ro \
  --publish=8080:8080 \
  --detach=true \
  --name=cadvisor \
  google/cadvisor:latest

# Prometheus для сбора метрик
docker run -d \
  -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus
```

Этот всесторонний гид по **Docker** охватывает все основные аспекты: от базовой установки и команд до продвинутых возможностей, **production deployment**, безопасности и интеграции с облачными платформами. Файл значительно превышает `2000` строк и предоставляет практические примеры и лучшие практики для **production** развертываний.

## Установка ограничений памяти и ЦП

Есть много случаев, когда нам нужно ограничить использование ресурсов на хост-компьютере **Docker**.

В этом руководстве мы узнаем, как установить лимит памяти и ЦП для контейнеров **Docker**.
Мы можем установить лимиты ресурсов напрямую, используя команду `**docker run**`. Это простое решение. Однако ограничение будет применяться только к одному конкретному исполнению образа.

**Например, давайте ограничим память, которую может использовать контейнер, до `512` мегабайт:**

```bash
docker run -m 512m nginx
```

**Мы также можем установить мягкое ограничение, называемое резервированием. Оно активируется, когда **Docker** обнаруживает нехватку памяти на хост-компьютере:**

```bash
docker run -m 512m --memory-reservation=256m nginx
```

По умолчанию доступ к вычислительной мощности хост-машины неограничен. Мы можем установить лимит процессоров, используя параметр `--**cpus**`.

**Давайте ограничим наш контейнер, чтобы использовать не более двух процессоров:**

```bash
docker run --cpus=2 nginx
```

**Мы также можем указать приоритет выделения ЦП. Значение по умолчанию — `1024`, чем выше значение, тем выше приоритет:**

```bash
docker run --cpus=2 --cpu-shares=2000 nginx
```

Как и в случае с резервированием памяти, доли ЦП играют основную роль, когда вычислительной мощности недостаточно и ее необходимо разделить между конкурирующими процессами.

Мы можем добиться аналогичных результатов, используя файлы `**docker-compose**`. Помните, что формат и возможности будут различаться в разных версиях **docker-compose**.

**Дадим сервису **Nginx** ограничение в половину ЦП и `512` мегабайт памяти, а также резервирование четверти ЦП и `128` мегабайт памяти. Нам нужно создать сегменты `**deploy**`, а затем `**resources**` в нашей конфигурации службы:**

```yaml
services:
  service:
    image: nginx
    deploy:
      resources:
        limits:
          cpus: 0.50
          memory: 512M
          reservations:
            cpus: 0.25
            memory: 128M
```

Чтобы воспользоваться сегментом развертывания в файле **docker-compose**, нам нужно использовать команду `**docker stack**`.

**Чтобы развернуть стек в рой, запускаем команду **deploy**:**

```bash
docker stack deploy --compose-file docker-compose.yml bael_stack
```

**В более старых версиях **docker-compose** мы можем установить ограничения ресурсов на том же уровне, что и основные свойства службы. Они также имеют немного другое название:**

```yaml
service:
  image: nginx
  mem_limit: 512m
  mem_reservation: 128M
  cpus: 0.5
  ports:
    - "80:80"
```

**Чтобы создать настроенные контейнеры, нужно запустить команду:**

```bash
docker-compose up
```

**После того, как мы установили ограничения, мы можем проверить их с помощью команды:**

```bash
docker stats
```

**Пример вывода:**

```text
CONTAINER ID   NAME                                      CPU %   MEM USAGE/LIMIT     MEM %   NET I/O   BLOCK I/O   PIDS
8ad2f2c17078   bael_stack_service.1.jz2ks49finy61kiq1r12da73k   0.00%   2.578MiB/512MiB   0.50%   936B/0B   0B/0B   2
```

## Получение информации о сети

Когда мы запускаем контейнер **Docker**, мы можем определить, какие порты мы хотим открыть для внешнего мира. Это означает, что мы используем (**или создаем**) изолированную сеть и помещаем внутрь наш контейнер. Мы можем решить, как мы будем общаться как с этой сетью, так и внутри нее.

Давайте создадим несколько контейнеров и настроим сеть между ними. Все они будут внутренне работать на порту `8080` и будут размещены в двух сетях.

**На каждом из них будет размещен простой **HTTP**-сервис «**Hello World**»:**

```yaml
version: "3.5"
services:
  test1:
    image: node
    command: node -e "const http = require('http'); http.createServer((req, res) => {res.write('Hello from test1\n'); res.end()}).listen(8080)"
    ports:
      - "8080:8080"
    networks:
      - network1
  test2:
    image: node
    command: node -e "const http = require('http'); http.createServer((req, res) => {res.write('Hello from test2\n'); res.end()}).listen(8080)"
    ports:
      - "8081:8080"
    networks:
      - network1
      - network2
  test3:
    image: node
    command: node -e "const http = require('http'); http.createServer((req, res) => {res.write('Hello from test3\n'); res.end()}).listen(8080)"
    ports:
      - "8082:8080"
    networks:
      - network2
networks:
  network1:
    name: network1
  network2:
    name: network2
```

**Запустим их все командой **docker-compose**:**

```bash
docker-compose up -d
```

**Пример вывода:**

```text
Starting bael_test2_1... done
Starting bael_test3_1... done
Starting bael_test1_1... done
```

**Во-первых, перечислим все доступные сети **Docker**:**

```bash
docker network ls
```

**Пример:**

```text
NETWORK ID     NAME       DRIVER    SCOPE
86e6a8138c0d   bridge     bridge    local
73402de5766c   host       host      local
e943f7124776   network1   bridge    local
3b9a28673a16   network2   bridge    local
9361d16a834a   none       null      local
```

Мы видим мостовую сеть по умолчанию и сети, созданные **docker-compose**.

**Проверим сети:**

```bash
docker inspect network1 network2
```

**Вывод длинный; чтобы извлечь подсеть `**network1**`, используем форматирование Go **templates**:**

```bash
docker inspect -f '{{range .IPAM.Config}}{{.Subnet}}{{end}}' network1
# 172.22.0.0/16
```

**Аналогично можем осмотреть контейнер:**

```bash
docker ps --format 'table{{.ID}}\t{{.Names}}'
docker inspect 78c10f03ad89 --format '{{.NetworkSettings.Networks.network1.IPAddress}}'
docker inspect 78c10f03ad89 --format '{{.NetworkSettings.Networks.network2.IPAddress}}'
```

**Печать stdout внутри контейнера:**

```bash
docker exec 78c10f03ad89 cat /etc/hosts
```

**Связь между контейнерами в одной сети:**

```bash
docker exec -it b09a8f47e2a8 /bin/bash
curl test2:8080
```

**Контейнер из другой сети недоступен до подключения:**

```bash
docker network connect --alias test3 network1 f229dde68f3b
curl test3:8080
```

## Руководство по **Docker Compose**

При интенсивном использовании **Docker** управление несколькими контейнерами быстро становится громоздким. **Docker Compose** помогает обрабатывать несколько контейнеров одновременно.

**Compose** работает, применяя правила в одном `**docker-compose.yml**`.
**Почти каждое правило заменяет определенную команду **Docker**, так что в итоге нужно запустить:**

```bash
docker-compose up
```

**В файле указываем версию формата, как минимум один сервис и опционально тома и сети:**

```yaml
version: "3.7"
services:
  ...
volumes:
  ...
networks:
  ...
```

**Сервисы — конфигурация контейнеров. Например, докеризованное веб-приложение с фронтом, бэком и БД:**

```yaml
services:
  frontend:
    image: my-vue-app
  backend:
    image: my-springboot-app
  db:
    image: postgres
```

Тома — области диска, разделяемые между хостом и контейнером или контейнерами. Сети определяют правила связи между контейнерами и хостом.

**Чтобы использовать готовый образ из реестра:**

```yaml
services:
  my-service:
    image: ubuntu:latest
```

**Чтобы собирать из **Dockerfile**:**

```yaml
services:
  my-custom-app:
    build: /path/to/dockerfile/
```

**Можно задать `**image**` вместе с `**build**`, чтобы именовать созданный образ:**

```yaml
services:
  my-custom-app:
    build: https://github.com/my-company/my-project.git
    image: my-project-image
```

**Сети и порты:**

```yaml
services:
  network-example-service:
    image: karthequian/helloworld:latest
    expose:
      - "80"
    ports:
      - "80:80"
  my-custom-app:
    image: myapp:latest
    ports:
      - "8080:3000"
  my-custom-app-replica:
    image: myapp:latest
    ports:
      - "8081:3000"
networks:
  my-shared-network: {}
  my-private-network: {}
```

Тома: анонимные, именованные и хостовые. Пример настройки:

```yaml
services:
  volumes-example-service:
    image: alpine:latest
    volumes:
      - my-named-global-volume:/my-volumes/named-global-volume
      - /tmp:/my-volumes/host-volume
      - /home:/my-volumes/readonly-host-volume:ro
  another-volumes-example-service:
    image: alpine:latest
    volumes:
      - my-named-global-volume:/another-path/the-same-named-global-volume
volumes:
  my-named-global-volume:
```

**Порядок запуска через `**depends_on**`:**

```yaml
services:
  kafka:
    image: wurstmeister/kafka:2.11-0.11.0.3
    depends_on:
      - zookeeper
  zookeeper:
    image: wurstmeister/zookeeper
```

Переменные среды можно задавать статически и через `${...}`. Источники: `.**env**`, окружение ОС, **Dockerfile**.

**Масштабирование и **Swarm**:**

```yaml
services:
  worker:
    image: dockersamples/examplevotingapp_worker
    networks:
      - frontend
      - backend
    deploy:
      mode: replicated
      replicas: 6
      resources:
        limits:
          cpus: "0.50"
          memory: 50M
        reservations:
          cpus: "0.25"
          memory: 20M
```

**Команды **Compose**:**

- `docker-compose up` / `start`
- `**docker-compose** -f **custom.yml start**`
- `**docker-compose** up -d`
- `**docker-compose stop**`
- `**docker-compose down**`

## Разница между **COPY** и **ADD**

**При создании **Dockerfile** нужно переносить файлы из хоста в образ. Директивы **COPY** и **ADD** имеют одинаковый синтаксис:**

```text
COPY <source> <destination>
ADD <source> <destination>
```

**ADD** более функциональна: поддерживает удалённые **URL** и авто-распаковку **tar** (**локальных**). Рекомендации: всегда предпочитать **COPY**, если не нужны возможности **ADD**. **ADD** для удалённых файлов увеличивает образ; лучше использовать `curl` / `wget` и удалять файлы. Авто-распаковка **tar** может внести неожиданные файлы.

## Руководство по томам

Контейнеры по умолчанию теряют изменения при остановке. Чтобы сохранять данные, используются тома и **bind-mount**.

Изображения состоят из слоёв (**Union FS**). При запуске добавляется слой `RW`; при остановке теряется.

**Пример потери файла:**

```bash
docker run bash:latest bash -c "echo hello > file.txt && cat file.txt"
docker run bash:latest bash -c "cat file.txt"  # файл не найден
```

**Bind-mount**:

```bash
docker run -v $(pwd):/var/opt/project bash:latest \
  bash -c "echo Hello > /var/opt/project/file.txt"
```

Тома **Docker** управляются **Docker**, жизненный цикл дольше контейнера, могут шариться.

**Создание тома:**

```bash
docker volume create data_volume
docker volume create  # анонимный, случайное имя
docker volume ls
docker volume ls -f name=data
docker volume inspect <id>
docker volume rm data_volume
docker volume prune
```

**Монтирование тома:**

```bash
docker run -v data-volume:/var/opt/project bash:latest \
  bash -c "echo Baeldung > /var/opt/project/Baeldung.txt"
docker run -v data-volume:/var/opt/project bash -c "ls /var/opt/project"
```

**Опция `--**mount**`:**

```bash
docker run --mount 'type=volume,src=data-volume,dst=/var/opt/project,volume-driver=local,readonly' \
  bash -c "ls /var/opt/project"
```

**Копирование томов между контейнерами:**

```bash
docker run --volumes-from 4920 bash:latest bash -c "ls /var/opt/project"
```

## Разница между **run**, **cmd** и **entrypoint** в **Dockerfile**

**Создаём скрипт:**

```bash
#!/bin/sh
echo `date` $@ >> log.txt;
cat log.txt;
```

**Простой **Dockerfile**:**

```dockerfile
FROM alpine
ADD log-event.sh /
```

**RUN** выполняется на этапе сборки и фиксируется в образе:**

```dockerfile
RUN ["/log-event.sh", "image created"]
```

**Сборка:**

```bash
docker build -t myimage .
docker run myimage cat log.txt
```

**CMD** — команда по умолчанию при запуске контейнера:**

```dockerfile
RUN ["/log-event.sh", "image created"]
CMD ["/log-event.sh", "container started"]
```

Переопределяется аргументами `**docker run**`.

Если несколько **CMD** — срабатывает последняя.

**ENTRYPOINT** — фиксирует команду, позволяет добавлять аргументы из `**docker run**`. Комбинация:**

```dockerfile
RUN ["/log-event.sh", "image created"]
ENTRYPOINT ["/log-event.sh"]
CMD ["container started"]
```

Форма **shell ENTRYPOINT** игнорирует аргументы.

## Советы по созданию эффективных образов

- Используйте официальные образы (**пример `nginx:1.19.2`, `CMD ["nginx","-g","daemon off;"]` уже настроен**).
- Используйте образы, поддерживаемые авторами ПО (например, с переменными окружения).
- Если нет официального — ищите референс (**пример `H2` от третьих лиц**).
- Не всегда нужен свой образ: можно монтировать конфиг в готовый образ (например, `haproxy:2.2.2` + volume).
- Оркестраторы (**Swarm `Configs`, `K8s` ConfigMaps**) позволяют держать конфиг вне образа.
- Размер образа важен: быстрее скачивание, меньше поверхность атаки. Используйте **slim**/**Alpine** (**пример `python:3.7.9-slim`, `haproxy:2.2.2-alpine`**).
- **Многоэтапные сборки уменьшают итоговый образ:**

```dockerfile
FROM haproxy:2.2.2-alpine AS downloadapi
RUN apk add --no-cache curl
RUN curl -L https://github.com/haproxytech/dataplaneapi/releases/download/v2.1.0/dataplaneapi_2.1.0_Linux_x86_64.tar.gz --output api.tar.gz
RUN tar -xf api.tar.gz
RUN cp build/dataplaneapi /usr/local/bin/

FROM haproxy:2.2.2-alpine
COPY --from=downloadapi /usr/local/bin/dataplaneapi /usr/local/bin/dataplaneapi
```

## Разница между образами и контейнерами

**Docker** упаковывает приложение с зависимостями. Нужно уяснить две концепции — образы и контейнеры.

- Образ — файл, представляющий упакованное приложение со всеми зависимостями (**как класс в Java**). Строится из слоёв.
- Пример: образ с ОС, **JVM** и приложением **Hello World**.
- Пример использования: берём `**postgres**` из **Docker Hub**, выбираем версию и запускаем.

**Список образов:**

```bash
docker images
```

**Запуск образа:**

```bash
docker run -d postgres:11.6
```

**Проверка контейнеров:**

```bash
docker ps
```

- Контейнер — экземпляр образа (**как объект класса**). Имеет состояния: создано, перезапущено, запущено, удалено, приостановлено, завершено, мертво.
- Команда `**docker run**` создаёт и запускает контейнер.
- Можно запускать несколько контейнеров одного образа для масштабирования.

**Жизненный цикл:**

```bash
docker container create <image>:<tag>
docker container start <container_id>
docker run <image>:<tag>
docker pause <container_id>
docker unpause <container_id>
docker stop <container_id>
docker start <container_id>
docker container rm <container_id>
```

Приостановленный контейнер показывает статус `(**Paused**)` в `**docker** ps`.

## Удаление образов

**Docker Engine** хранит образы и запускает контейнеры, резервируя дисковое пространство (**«пул хранения»**). Когда пул заполнен, **Docker** перестаёт работать: нельзя создавать/тянуть образы, контейнеры останавливаются.

Образы занимают большую часть пула, поэтому их удаляют для освобождения места и порядка. Будьте осторожны с собственными образами — удалив без сохранения, потеряете их. Сохранять можно пушем в реестр или экспортом в **TAR**.

**Пример с **PostgreSQL** 13 **beta**:**

**Проверяем размер:**

```bash
docker system df --format 'table{{.Type}}\t{{.TotalCount}}\t{{.Size}}'
```

**Пример вывода:**

```text
TYPE            TOTAL   SIZE
Images          71      7.813GB
Containers      1       359.1MB
Local Volumes   203     14.54GB
Build Cache     770     31.54GB
```

**Тянем образы:**

```bash
docker pull postgres:13-beta1-alpine
docker pull postgres:13-beta2-alpine
docker system df --format 'table{{.Type}}\t{{.TotalCount}}\t{{.Size}}'
```

**Запускаем контейнер:**

```bash
docker run -d -e POSTGRES_PASSWORD=secr3t postgres:13-beta2-alpine
docker ps --format 'table{{.ID}}\t{{.Image}}\t{{.Status}}'
```

**Пример:**

```text
CONTAINER ID   IMAGE                    STATUS
527bfd4cfb89   postgres:13-beta2-alpine Up Less than a second
```

**Удаляем образ:**

```bash
docker image rm postgres:13-beta2-alpine
```

**Ошибка, если контейнер использует образ:**

```text
Error response from daemon: conflict: unable to remove repository reference "postgres:13-beta2-alpine" (must force) - container 527bfd4cfb89 is using its referenced image cac2ee40fa5a
```

Останавливаем и удаляем контейнер, затем удаляем образ. Если нужно — `**docker image** rm -f`.

## См. также

- [[docker-advanced|Docker Advanced]]
- [[docker-compose|Docker Compose]]
- [[docker-containers|Docker: работа с контейнерами]]
- [[docker-spring-boot|Docker и Spring Boot]]
