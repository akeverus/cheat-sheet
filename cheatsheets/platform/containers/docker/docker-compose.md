---
title: "Docker Compose"
description: "Docker Compose - это инструмент для определения и запуска multi-container приложений Docker. С помощью Compose можно описать всю архитектуру приложения в YAML файле и запустить все сервисы одной командой. Это идеальный инструмент для разработки, тестирования и локального разверты"
tags:
  - platform
  - containers
  - docker-compose
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Docker Compose**

**Docker Compose** - это инструмент для определения и запуска **multi-container** приложений **Docker**. С помощью **Compose** можно описать всю архитектуру приложения в **YAML** файле и запустить все сервисы одной командой. Это идеальный инструмент для разработки, тестирования и локального развертывания.

## Полезные ссылки
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Compose File Reference](https://docs.docker.com/compose/compose-file/)
- [Docker Compose GitHub](https://github.com/docker/compose)
- [Best Practices](https://docs.docker.com/compose/production/)
- [Production Considerations](https://docs.docker.com/compose/production/)

## Содержание

- [Основы Docker Compose](#основы-docker-compose)
  - [Установка и базовая настройка](#установка-и-базовая-настройка)
- [Структура compose файла](#структура-compose-файла)
  - [Минимальный пример](#минимальный-пример)
  - [Полная структура](#полная-структура)
- [Конфигурация сервисов](#конфигурация-сервисов)
  - [Build контекст](#build-контекст)
  - [Переменные окружения](#переменные-окружения)
  - [Volumes и mounts](#volumes-и-mounts)
  - [Networking](#networking)
  - [Health checks](#health-checks)
  - [Зависимости сервисов](#зависимости-сервисов)
- [Secrets и configs](#secrets-и-configs)
  - [Управление секретами](#управление-секретами)
  - [Конфигурационные файлы](#конфигурационные-файлы)
- [Deploy и scaling](#deploy-и-scaling)
  - [Production deployment](#production-deployment)
  - [Blue-Green deployment](#blue-green-deployment)
- [Environment management](#environment-management)
  - [Override файлы](#override-файлы)
  - [Запуск разных сред](#запуск-разных-сред)
- [Расширенные возможности](#расширенные-возможности)
  - [Extends](#extends)
  - [YAML anchors](#yaml-anchors)
  - [Profiles](#profiles)
  - [Запуск с профилями](#запуск-с-профилями)
- [Команды Docker Compose](#команды-docker-compose)
  - [Основные команды](#основные-команды)
  - [Продвинутые команды](#продвинутые-команды)
- [Интеграция с другими инструментами](#интеграция-с-другими-инструментами)
  - [Makefile для удобства](#makefile-для-удобства)
  - [Integration с CI/CD](#integration-с-cicd)
- [Best practices](#best-practices)
  - [Производственная настройка](#производственная-настройка)
  - [Безопасность](#безопасность)
  - [Мониторинг и логирование](#мониторинг-и-логирование)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [Debug режим](#debug-режим)
  - [Performance optimization](#performance-optimization)
- [См. также](#см-также)

## Основы **Docker Compose**

### Установка и базовая настройка

#### Установка

Ниже — команды установки **Docker Compose** (**bash**).
```bash
# Linux
sudo curl -L "https://github.com/docker/compose/releases/download/v2.17.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Проверка установки
docker-compose --version

# macOS (через Homebrew)
brew install docker-compose

# Windows
# Docker Desktop включает Docker Compose
```

#### Версии и совместимость
```yaml
# Всегда указывайте версию формата
version: '3.8'

# Соответствие версий:
# version: '2'     - Docker Engine 1.10.0+
# version: '2.1'   - Docker Engine 1.12.0+
# version: '3'     - Docker Engine 1.13.0+
# version: '3.1'   - Docker Engine 1.13.1+
# version: '3.8'   - Docker Engine 19.03.0+
```

## Структура **compose** файла

### Минимальный пример
```yaml
version: '3.8'

services:
  web:
    image: nginx:alpine
    ports:
      - "8080:80"

  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: mysecretpassword
```

### Полная структура
```yaml
version: '3.8'

# Определение сервисов
services:
  web:
    build: .
    ports:
      - "3000:3000"
    volumes:
      - .:/app
    environment:
      - NODE_ENV=development
    depends_on:
      - db
      - redis

  api:
    build:
      context: ./api
      dockerfile: Dockerfile.prod
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=postgresql://user:pass@db:5432/myapp
    depends_on:
      - db
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  db:
    image: postgres:15
    environment:
      POSTGRES_DB: myapp
      POSTGRES_USER: user
      POSTGRES_PASSWORD: pass
    volumes:
      - db_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"

# Определение сетей
networks:
  frontend:
    driver: bridge
  backend:
    driver: bridge
    internal: true

# Определение volumes
volumes:
  db_data:
    driver: local
  redis_data:
    driver: local

# Конфигурации (configs)
configs:
  nginx_config:
    file: ./nginx.conf

# Секреты (secrets)
secrets:
  db_password:
    file: ./secrets/db_password.txt
```

## Конфигурация сервисов

### **Build** контекст
```yaml
services:
  web:
    # Простая сборка из текущей директории
    build: .

  api:
    # Сборка с кастомными параметрами
    build:
      context: ./api
      dockerfile: Dockerfile.prod
      args:
        BUILD_ENV: production
        NODE_VERSION: 18
      cache_from:
        - myapp/api:latest
      target: production

  worker:
    # Сборка с несколькими контекстами
    build:
      context: .
      dockerfile: ./docker/worker.Dockerfile
```

### Переменные окружения
```yaml
services:
  web:
    # Из файла
    env_file:
      - .env
      - .env.prod

    # Inline переменные
    environment:
      NODE_ENV: production
      PORT: 3000
      DATABASE_URL: postgresql://user:pass@db:5432/myapp

    # Переменные из других сервисов
    environment:
      - REDIS_URL=redis://redis:6379
      - API_URL=http://api:8080

  db:
    # Переменные с подстановкой
    environment:
      POSTGRES_DB: ${DB_NAME:-myapp}
      POSTGRES_USER: ${DB_USER:-user}
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password
```

### **Volumes** и **mounts**
```yaml
services:
  web:
    volumes:
      # Named volume
      - web_data:/app/data

      # Bind mount (host path)
      - ./src:/app/src

      # Bind mount с правами
      - ./logs:/app/logs:rw

      # tmpfs для временных данных
      - type: tmpfs
        target: /tmp
        tmpfs:
          size: 100m

  db:
    volumes:
      # Volume с опциями
      - type: volume
        source: db_data
        target: /var/lib/postgresql/data
        volume:
          nocopy: true

      # Bind mount для конфигурации
      - type: bind
        source: ./postgres.conf
        target: /etc/postgresql/postgresql.conf
        read_only: true
```

### **Networking**
```yaml
services:
  web:
    networks:
      - frontend
      - backend
    # Кастомный hostname
    hostname: web.internal
    # Aliases в сети
    networks:
      frontend:
        aliases:
          - webapp
          - app.local

  api:
    networks:
      - backend
    # Статический IP
    networks:
      backend:
        ipv4_address: 172.20.0.10

  db:
    networks:
      - backend
    # Изоляция сети
    networks:
      backend:
        aliases:
          - postgres.internal

# Определение сетей
networks:
  frontend:
    driver: bridge
    driver_opts:
      com.docker.network.bridge.name: myapp_frontend
    ipam:
      driver: default
      config:
        - subnet: 172.21.0.0/16
          gateway: 172.21.0.1

  backend:
    driver: bridge
    internal: true  # Без внешнего доступа
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

### **Health checks**
```yaml
services:
  web:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3000/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
      disable: false

  api:
    healthcheck:
      test: ["CMD-SHELL", "python -c 'import requests; requests.get(\"http://localhost:8080/health\")'"]
      interval: 60s
      timeout: 30s
      retries: 5
      start_period: 60s

  db:
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5
```

### Зависимости сервисов
```yaml
services:
  web:
    depends_on:
      - db
      - redis
    # Условное ожидание health check
    depends_on:
      db:
        condition: service_healthy
      redis:
        condition: service_started

  api:
    depends_on:
      - db
      - cache
    # Порядок запуска
    depends_on:
      db:
        condition: service_healthy

  worker:
    depends_on:
      - queue
      - db
    # Масштабирование после зависимостей
    deploy:
      replicas: 3
```

## **Secrets** и **configs**

### Управление секретами
```yaml
# Создание секретов
echo "mysecretpassword" | docker secret create db_password -

# Использование секретов
services:
  db:
    secrets:
      - db_password
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password

  api:
    secrets:
      - source: api_key
        target: /app/api_key.txt
        mode: 0400

secrets:
  db_password:
    external: true
    name: myapp_db_password

  api_key:
    file: ./secrets/api_key.txt
```

### Конфигурационные файлы
```yaml
services:
  nginx:
    configs:
      - source: nginx_config
        target: /etc/nginx/nginx.conf
        mode: 0444

  app:
    configs:
      - source: app_config
        target: /app/config.json

configs:
  nginx_config:
    file: ./nginx.conf

  app_config:
    external: true
    name: myapp_config
```

## **Deploy** и **scaling**

### **Production deployment**
```yaml
version: '3.8'

services:
  web:
    image: myapp/web:latest
    deploy:
      mode: replicated
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
      update_config:
        parallelism: 2
        delay: 10s
        failure_action: rollback
        monitor: 60s
        max_failure_ratio: 0.3
      rollback_config:
        parallelism: 2
        delay: 10s
        failure_action: pause
        monitor: 60s
        max_failure_ratio: 0.3

  api:
    image: myapp/api:latest
    deploy:
      mode: global  # Одна реплика на каждом узле
      placement:
        constraints:
          - node.role == worker

  db:
    image: postgres:15
    deploy:
      placement:
        constraints:
          - node.labels.type == database
      restart_policy:
        condition: unless-stopped
```

### **Blue-Green deployment**
```yaml
version: '3.8'

services:
  web-blue:
    image: myapp/web:v1
    ports:
      - "8080:80"
    deploy:
      replicas: 3
      update_config:
        parallelism: 1
        delay: 30s

  web-green:
    image: myapp/web:v2
    ports:
      - "8081:80"  # Другой порт пока
    deploy:
      replicas: 0  # Начинаем с 0 реплик

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    configs:
      - source: nginx_config
        target: /etc/nginx/nginx.conf
    depends_on:
      - web-blue
      - web-green

configs:
  nginx_config:
    file: ./nginx-blue.conf  # Сначала blue
```

## **Environment management**

### **Override** файлы
```yaml
# docker-compose.yml (base)
version: '3.8'

services:
  web:
    build: .
    environment:
      - NODE_ENV=development
    ports:
      - "3000:3000"

  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=myapp
      - POSTGRES_PASSWORD=password
```

```yaml
# docker-compose.override.yml (development)
version: '3.8'

services:
  web:
    volumes:
      - .:/app
      - /app/node_modules
    environment:
      - DEBUG=true
    ports:
      - "3001:3000"  # Другой порт для dev

  db:
    ports:
      - "5432:5432"  # Доступ к БД снаружи
    volumes:
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
```

```yaml
# docker-compose.prod.yml (production)
version: '3.8'

services:
  web:
    image: myapp/web:latest
    environment:
      - NODE_ENV=production
    deploy:
      replicas: 3
      resources:
        limits:
          memory: 512M
          cpus: '0.5'

  db:
    image: postgres:15
    environment:
      - POSTGRES_PASSWORD_FILE=/run/secrets/db_password
    volumes:
      - db_data:/var/lib/postgresql/data
    deploy:
      placement:
        constraints:
          - node.labels.type == database

secrets:
  db_password:
    external: true

volumes:
  db_data:
    external: true
```

### Запуск разных сред
```bash
# Development (использует override по умолчанию)
docker-compose up

# Production
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up

# Testing
docker-compose -f docker-compose.yml -f docker-compose.test.yml up

# Custom environment
docker-compose --env-file .env.prod up
```

## Расширенные возможности

### **Extends**
```yaml
version: '3.8'

# Базовый сервис
x-base-service: &base-service
  image: nginx:alpine
  volumes:
    - ./logs:/var/log/nginx
  restart: unless-stopped
  logging:
    driver: json-file
    options:
      max-size: "10m"
      max-file: "3"

services:
  web:
    <<: *base-service
    ports:
      - "80:80"
    environment:
      - SERVICE_NAME=web

  api:
    <<: *base-service
    ports:
      - "8080:80"
    environment:
      - SERVICE_NAME=api
```

### **YAML anchors**
```yaml
version: '3.8'

x-logging: &default-logging
  driver: json-file
  options:
    max-size: "10m"
    max-file: "3"

x-deploy-web: &deploy-web
  resources:
    limits:
      memory: 512M
      cpus: '0.5'
    reservations:
      memory: 256M
      cpus: '0.25'
  restart_policy:
    condition: on-failure
    delay: 5s

services:
  web:
    image: nginx:alpine
    logging: *default-logging
    deploy: *deploy-web

  api:
    image: myapp/api:latest
    logging: *default-logging
    deploy:
      <<: *deploy-web
      replicas: 3
```

### **Profiles**
```yaml
version: '3.8'

services:
  web:
    image: nginx:alpine
    ports:
      - "80:80"
    profiles:
      - web

  api:
    image: myapp/api:latest
    ports:
      - "8080:8080"
    profiles:
      - api

  db:
    image: postgres:15
    ports:
      - "5432:5432"
    profiles:
      - db
      - dev

  monitoring:
    image: prom/prometheus
    ports:
      - "9090:9090"
    profiles:
      - monitoring
```

### Запуск с профилями
```bash
# Только web сервисы
docker-compose --profile web up

# Web + API
docker-compose --profile web --profile api up

# Все сервисы
docker-compose --profile "*" up

# Development окружение (включая db)
docker-compose --profile dev up
```

## Команды **Docker Compose**

### Основные команды
```bash
# Создание и запуск
docker-compose up                    # Запуск в foreground
docker-compose up -d                # Запуск в background
docker-compose up --build          # Сборка и запуск
docker-compose up --scale web=3    # Масштабирование

# Управление сервисами
docker-compose start               # Запуск остановленных сервисов
docker-compose stop                # Остановка сервисов
docker-compose restart             # Перезапуск сервисов
docker-compose pause               # Приостановка
docker-compose unpause             # Возобновление

# Уничтожение
docker-compose down                # Остановка и удаление контейнеров
docker-compose down -v            # Удаление volumes
docker-compose down --rmi all     # Удаление образов

# Просмотр состояния
docker-compose ps                  # Список контейнеров
docker-compose logs               # Логи сервисов
docker-compose logs -f web        # Следить за логами
docker-compose top                # Процессы в контейнерах

# Другие команды
docker-compose build              # Сборка образов
docker-compose pull               # Скачивание образов
docker-compose push               # Отправка образов
docker-compose config             # Валидация конфигурации
docker-compose exec web bash      # Выполнение команд
```

### Продвинутые команды
```bash
# Работа с отдельными сервисами
docker-compose up web              # Только web сервис
docker-compose build api          # Сборка только api
docker-compose restart db         # Перезапуск только db

# Override файлы
docker-compose -f docker-compose.yml -f docker-compose.override.yml up
docker-compose -f docker-compose.yml -f docker-compose.prod.yml config

# Environment variables
docker-compose --env-file .env.prod up
docker-compose run --env NODE_ENV=test api npm test

# Isolation
docker-compose up --no-deps web   # Запуск без зависимостей
docker-compose run --rm web bash # Одноразовый контейнер

# Parallel execution
docker-compose build --parallel   # Параллельная сборка
docker-compose pull --parallel    # Параллельное скачивание

# Health checks
docker-compose ps                  # Показывает статус health
docker-compose events             # Мониторинг событий
```

## Интеграция с другими инструментами

### **Makefile** для удобства
```makefile
.PHONY: up down build logs clean

# Development
dev:
	docker-compose -f docker-compose.yml -f docker-compose.override.yml up -d

dev-build:
	docker-compose -f docker-compose.yml -f docker-compose.override.yml up --build -d

# Production
prod:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

prod-deploy:
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml pull
	docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Testing
test:
	docker-compose -f docker-compose.yml -f docker-compose.test.yml up --abort-on-container-exit

# Utility
logs:
	docker-compose logs -f

logs-web:
	docker-compose logs -f web

shell:
	docker-compose exec web bash

shell-db:
	docker-compose exec db psql -U postgres -d myapp

# Cleanup
clean:
	docker-compose down -v --rmi all
	docker system prune -f

clean-volumes:
	docker-compose down -v
	docker volume prune -f
```

### **Integration** с CI/CD
```yaml
# GitHub Actions
name: CI/CD Pipeline

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Build and start services
        run: |
          docker-compose -f docker-compose.yml -f docker-compose.test.yml up -d
          docker-compose -f docker-compose.yml -f docker-compose.test.yml exec -T api npm test
          docker-compose -f docker-compose.yml -f docker-compose.test.yml down

  deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3

      - name: Deploy to production
        run: |
          echo "${{ secrets.DOCKER_HUB_TOKEN }}" | docker login -u ${{ secrets.DOCKER_HUB_USERNAME }} --password-stdin
          docker-compose -f docker-compose.yml -f docker-compose.prod.yml pull
          docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
          docker-compose -f docker-compose.yml -f docker-compose.prod.yml exec -T api npm run migrate
```

## **Best practices**

### Производственная настройка
```yaml
version: '3.8'

services:
  web:
    image: myapp/web:${TAG:-latest}
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '0.5'
        reservations:
          memory: 256M
          cpus: '0.25'
      restart_policy:
        condition: on-failure
        max_attempts: 3
      update_config:
        parallelism: 2
        delay: 30s
        failure_action: rollback

    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
        labels: "service,version"

  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password
    volumes:
      - db_data:/var/lib/postgresql/data
    deploy:
      placement:
        constraints:
          - node.labels.storage == fast
      restart_policy:
        condition: unless-stopped

secrets:
  db_password:
    external: true

volumes:
  db_data:
    driver: local
```

### Безопасность
```yaml
version: '3.8'

services:
  web:
    image: myapp/web:latest
    user: "1001:1001"  # Non-root user
    security_opt:
      - no-new-privileges:true
    cap_drop:
      - ALL
    cap_add:
      - NET_BIND_SERVICE
    read_only: true
    tmpfs:
      - /tmp:noexec,nosuid,size=100m
    volumes:
      - logs:/var/log:rw

  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD_FILE: /run/secrets/db_password
    secrets:
      - db_password
    security_opt:
      - no-new-privileges:true
    user: "70:70"  # PostgreSQL user

secrets:
  db_password:
    external: true

volumes:
  logs:
    driver: local
```

### Мониторинг и логирование
```yaml
version: '3.8'

services:
  web:
    logging:
      driver: fluentd
      options:
        fluentd-address: "127.0.0.1:24224"
        tag: "docker.{{.Name}}"
        labels: "service,version,environment"

  prometheus:
    image: prom/prometheus
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'

  grafana:
    image: grafana/grafana
    environment:
      GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/dashboards:/var/lib/grafana/dashboards

volumes:
  prometheus_data:
  grafana_data:
```

## Решение проблем

### Распространенные проблемы
```bash
# Container fails to start
docker-compose logs web
docker-compose exec web /bin/bash

# Service dependencies
docker-compose up --no-deps web  # Test single service
docker-compose config            # Validate configuration

# Port conflicts
docker-compose ps
netstat -tlnp | grep :80

# Volume permissions
docker-compose exec web ls -la /app
docker-compose exec web id

# Memory issues
docker stats
docker-compose logs | grep "Out of memory"

# Network issues
docker-compose exec web ping db
docker network ls
docker network inspect myapp_default

# Configuration validation
docker-compose config
docker-compose config --services
docker-compose config --volumes

# Clean restart
docker-compose down -v --rmi all
docker system prune -f
docker-compose up --build
```

### **Debug** режим
```bash
# Verbose logging
docker-compose --verbose up

# Debug specific service
docker-compose run --rm web bash

# Check service health
docker-compose ps
docker-compose exec web curl http://localhost/health

# Monitor resource usage
docker stats $(docker-compose ps -q)

# View service logs
docker-compose logs -f --tail=100 web

# Inspect containers
docker inspect $(docker-compose ps -q web)

# Debug networking
docker-compose exec web nslookup db
docker-compose exec web cat /etc/hosts
```

### **Performance optimization**
```yaml
# Optimized configuration
version: '3.8'

services:
  web:
    build:
      context: .
      dockerfile: Dockerfile
      target: production  # Multi-stage build
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '0.5'
        reservations:
          memory: 256M
          cpus: '0.25'
    restart: unless-stopped

  db:
    image: postgres:15
    command: >
      postgres
      -c shared_buffers=256MB
      -c effective_cache_size=1GB
      -c maintenance_work_mem=64MB
      -c checkpoint_completion_target=0.9
      -c wal_buffers=16MB
      -c default_statistics_target=100
    deploy:
      resources:
        limits:
          memory: 2GB
          cpus: '1.0'
        reservations:
          memory: 1GB
          cpus: '0.5'
```
## См. также
- [[docker-basics|Docker Basics]] — основы **Docker**
- [[docker-advanced|Docker Advanced]] — продвинутые концепции **Docker**
- [[kubernetes-basics|Kubernetes]] — оркестрация контейнеров
- [[terraform|Terraform]] — инфраструктура как код
