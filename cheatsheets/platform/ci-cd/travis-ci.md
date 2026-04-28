---
title: "Travis CI"
description: "Travis CI - hosted CI платформа с legacy-контекстом в 2026; документ охватывает поддержку существующих pipeline, troubleshooting и мягкую миграцию на современные CI/CD-системы."
tags:
  - platform
  - ci-cd
  - travis-ci
type: "overview"
difficulty: "intermediate"
aliases:
  - "Travis CI"
prerequisites: []
next:
  - "[[github-actions]]"
updated: "2026-04-20"
---
# Travis CI

**Travis** `CI` - это **hosted continuous integration** платформа, которая особенно популярна среди **open source** проектов. **Travis** `CI` предоставляет бесплатный сервис для публичных репозиториев и платные планы для приватных репозиториев. Этот документ охватывает конфигурацию, продвинутые паттерны и **best practices** для использования **Travis** `CI` в различных сценариях.

> `Travis CI` рассматривается как legacy-опция: документ ориентирован на поддержку существующих пайплайнов и безопасную миграцию.

## Полезные ссылки
- [Travis CI Documentation](https://docs.travis-ci.com/)
- [Travis CI API](https://developer.travis-ci.com/)
- [Travis CI GitHub](https://github.com/travis-ci)
- [Travis CI Enterprise](https://www.travis-ci.com/enterprise)

## Содержание

- [Актуальность Travis CI в 2026](#актуальность-travis-ci-в-2026)
- [Основы Travis CI](#основы-travis-ci)
  - [Структура проекта Travis CI](#структура-проекта-travis-ci)
  - [Минимальная конфигурация](#минимальная-конфигурация)
  - [Полная структура конфигурации](#полная-структура-конфигурации)
- [Конфигурация .travis.yml](#конфигурация-travisyml)
  - [Языки и среды выполнения](#языки-и-среды-выполнения)
  - [Custom environments](#custom-environments)
  - [Multi-OS builds](#multi-os-builds)
- [Матрицы сборок](#матрицы-сборок)
  - [Build matrix основы](#build-matrix-основы)
  - [Продвинутая матрица](#продвинутая-матрица)
  - [Conditional builds](#conditional-builds)
  - [Parallel jobs](#parallel-jobs)
- [Deployments и Releases](#deployments-и-releases)
  - [GitHub Releases](#github-releases)
  - [Heroku deployment](#heroku-deployment)
  - [AWS deployment](#aws-deployment)
  - [Docker deployment](#docker-deployment)
- [Интеграция с внешними сервисами](#интеграция-с-внешними-сервисами)
  - [Code coverage](#code-coverage)
  - [Code quality tools](#code-quality-tools)
  - [Slack notifications](#slack-notifications)
  - [Database services](#database-services)
- [Security и Best Practices](#security-и-best-practices)
  - [Encrypted variables](#encrypted-variables)
  - [Secure deployments](#secure-deployments)
  - [Branch protection](#branch-protection)
- [Enterprise Features](#enterprise-features)
  - [Travis CI Enterprise](#travis-ci-enterprise)
  - [Multi-tenant setup](#multi-tenant-setup)
  - [Audit и compliance](#audit-и-compliance)
  - [Custom workers](#custom-workers)
- [Миграция и Troubleshooting](#миграция-и-troubleshooting)
  - [Миграция с Travis CI .org на .com (исторический контекст)](#миграция-с-travis-ci-org-на-com-исторический-контекст)
  - [Troubleshooting распространенных проблем](#troubleshooting-распространенных-проблем)
  - [Performance optimization](#performance-optimization)
  - [Migration guide от других CI/CD систем](#migration-guide-от-других-cicd-систем)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Актуальность Travis CI в 2026

`Travis CI` остается рабочим вариантом для части legacy-проектов, но для новых репозиториев чаще выбирают `GitHub Actions` или `GitLab CI` из-за более активной экосистемы и удобной интеграции.

Когда `Travis CI` все еще уместен:
- уже есть стабильный pipeline и перенос не дает заметной бизнес-ценности;
- проект привязан к существующей инфраструктуре/секретам/процессам в Travis;
- команда поддерживает смешанный стек CI и использует Travis как часть migration-плана.

Рекомендуемый подход: рассматривать этот документ как практический гайд для поддержки и мягкой миграции, а не как дефолтный выбор CI для новых проектов.

## Основы Travis `CI`

### Структура проекта Travis `CI`
Ниже — структура проекта **Travis** `CI` (текст).
```text
my-project/
├── .travis.yml          # Основной конфигурационный файл
├── .travis/             # Дополнительные конфигурационные файлы
│   ├── before_script.sh
│   ├── deploy.sh
│   └── notifications.yml
├── scripts/             # Скрипты сборки
│   ├── build.sh
│   ├── test.sh
│   └── deploy.sh
├── Dockerfile           # Для custom environments
└── docker-compose.yml   # Для multi-service apps
```

### Минимальная конфигурация
Пример минимальной конфигурации .**travis.yml** (YAML).
```yaml
# .travis.yml - минимальная конфигурация
language: node_js
node_js:
  - "16"
  - "18"

script: npm test
```

### Полная структура конфигурации
```yaml
# .travis.yml - полная конфигурация
# Выбор языка/среды
language: ruby
rvm:
  - 2.7
  - 3.0

# Или использовать Docker
# services: docker

# Или custom environment
# dist: focal
# os: linux

# Cache dependencies
cache:
  directories:
    - node_modules
    - $HOME/.cache/pip
    - $HOME/.npm
    - $HOME/.m2

# Environment variables
env:
  global:
    - NODE_ENV=test
    - CI=true
  matrix:
    - TEST_SUITE=unit
    - TEST_SUITE=integration

# Build matrix
matrix:
  include:
    - name: "Node 16 on Ubuntu"
      os: linux
      dist: focal
      node_js: "16"
    - name: "Node 18 on Ubuntu"
      os: linux
      dist: focal
      node_js: "18"
    - name: "Node 16 on Windows"
      os: windows
      node_js: "16"
  exclude:
    - node_js: "16"
      env: TEST_SUITE=integration
  allow_failures:
    - node_js: "18"

# Build lifecycle
before_install:
  - echo "Before install step"

install:
  - npm ci

before_script:
  - echo "Before script step"

script:
  - npm run lint
  - npm test
  - npm run build

after_success:
  - echo "After success step"
  - bash <(curl -s https://codecov.io/bash)

after_failure:
  - echo "After failure step"

before_deploy:
  - echo "Before deploy step"

deploy:
  provider: heroku
  api_key: $HEROKU_API_KEY
  app: my-app-name
  on:
    branch: main

after_deploy:
  - echo "After deploy step"

# Notifications
notifications:
  email:
    recipients:
      - team@example.com
    on_success: change
    on_failure: always
  slack:
    secure: encrypted_slack_token
  webhooks:
    urls:
      - https://my-webhook.example.com/travis
    on_success: always
    on_failure: always
```

## Конфигурация .travis.yml

### Языки и среды выполнения
```yaml
# Node.js
language: node_js
node_js:
  - "14"
  - "16"
  - "18"

# Python
language: python
python:
  - "3.7"
  - "3.8"
  - "3.9"

# Ruby
language: ruby
rvm:
  - 2.7.0
  - 3.0.0
  - ruby-head

# Java
language: java
jdk:
  - openjdk8
  - openjdk11
  - openjdk17

# Go
language: go
go:
  - "1.18"
  - "1.19"
  - "1.20"

# PHP
language: php
php:
  - "7.4"
  - "8.0"
  - "8.1"

# .NET
language: csharp
mono: none
dotnet: 6.0
solution: MySolution.sln

# Rust
language: rust
rust:
  - stable
  - beta
  - nightly

# Elixir
language: elixir
elixir:
  - "1.13"
otp_release:
  - "24.0"
```

### Custom environments
```yaml
# Custom Ubuntu environment
dist: focal    # Ubuntu 20.04
os: linux

# Или Ubuntu 18.04
dist: bionic

# Windows
os: windows
# Windows Server 2019 by default

# macOS
os: osx
osx_image: xcode13.2  # macOS 12.0 Monterey

# Custom Docker environment
services: docker

before_install:
  - docker build -t myapp .
  - docker run -d -p 3000:3000 myapp

script:
  - docker ps
  - curl localhost:3000
```

### Multi-OS builds
```yaml
# .travis.yml - multi-OS сборки
matrix:
  include:
    - name: "Linux Ubuntu 20.04"
      os: linux
      dist: focal
      language: node_js
      node_js: "18"
      env: OS=linux

    - name: "macOS Monterey"
      os: osx
      osx_image: xcode13.2
      language: node_js
      node_js: "18"
      env: OS=macos

    - name: "Windows Server 2019"
      os: windows
      language: node_js
      node_js: "18"
      env: OS=windows

script:
  - |
    if [ "$OS" = "linux" ]; then
      echo "Running on Linux"
      npm run test:linux
    elif [ "$OS" = "macos" ]; then
      echo "Running on macOS"
      npm run test:macos
    elif [ "$OS" = "windows" ]; then
      echo "Running on Windows"
      npm run test:windows
    fi

cache:
  directories:
    - node_modules
```

## Матрицы сборок

### Build matrix основы
```yaml
# Простая матрица
language: node_js
node_js:
  - "14"
  - "16"
  - "18"

env:
  - TEST_SUITE=unit
  - TEST_SUITE=integration
  - TEST_SUITE=e2e

# Результат: 3 версии Node.js × 3 test suites = 9 комбинаций
```

### Продвинутая матрица
```yaml
# .travis.yml - продвинутая матрица сборок
language: python

matrix:
  include:
    # Linux builds
    - name: "Python 3.7 on Ubuntu"
      os: linux
      dist: focal
      python: "3.7"
      env: TOXENV=py37

    - name: "Python 3.8 on Ubuntu"
      os: linux
      dist: focal
      python: "3.8"
      env: TOXENV=py38

    - name: "Python 3.9 on Ubuntu"
      os: linux
      dist: focal
      python: "3.9"
      env: TOXENV=py39

    # macOS builds
    - name: "Python 3.8 on macOS"
      os: osx
      osx_image: xcode13.2
      python: "3.8"
      env: TOXENV=py38-macos

    # Windows builds
    - name: "Python 3.8 on Windows"
      os: windows
      python: "3.8"
      env: TOXENV=py38-windows

  exclude:
    # Исключить некоторые комбинации
    - python: "3.7"
      env: TOXENV=py37

  allow_failures:
    # Разрешить падение этих сборок
    - os: windows
    - python: "3.9"

  fast_finish: true  # Завершить сборку при первом падении

script:
  - tox

cache:
  directories:
    - $HOME/.cache/pip
```

### Conditional builds
```yaml
# Условные сборки
language: node_js
node_js: "18"

# Условная логика для разных веток
branches:
  only:
    - main
    - develop
    - /^release\/.*$/

env:
  global:
    - NODE_ENV=test

# Условные stages
jobs:
  include:
    - stage: test
      name: "Unit Tests"
      script: npm run test:unit
      if: branch = main OR branch =~ /^release/

    - stage: test
      name: "Integration Tests"
      script: npm run test:integration
      if: branch = main

    - stage: test
      name: "E2E Tests"
      script: npm run test:e2e
      if: branch = main

    - stage: deploy
      name: "Deploy to Staging"
      script: npm run deploy:staging
      if: branch = develop

    - stage: deploy
      name: "Deploy to Production"
      script: npm run deploy:prod
      if: tag IS present
```

### Parallel jobs
```yaml
# Параллельные задания
language: node_js
node_js: "18"

env:
  global:
    - CI=true

jobs:
  include:
    - stage: test
      name: "Lint"
      script: npm run lint

    - stage: test
      name: "Unit Tests"
      script: npm run test:unit

    - stage: test
      name: "Integration Tests"
      script: npm run test:integration

    - stage: test
      name: "E2E Tests"
      script: npm run test:e2e

    - stage: security
      name: "Security Scan"
      script: npm audit && npm run security-scan

    - stage: performance
      name: "Performance Tests"
      script: npm run test:performance

    - stage: deploy
      name: "Deploy"
      script: npm run deploy
      if: branch = main
```

## Deployments и Releases

### GitHub Releases
```yaml
# .travis.yml - GitHub Releases
language: node_js
node_js: "18"

script:
  - npm run build
  - npm test

deploy:
  provider: releases
  api_key: $GITHUB_TOKEN
  file:
    - "dist/app.js"
    - "dist/app.js.map"
  file_glob: true
  skip_cleanup: true
  on:
    tags: true
    repo: myorg/myrepo

# Создание release notes
before_deploy:
  - echo "Building release notes..."
  - |
    if [[ $TRAVIS_TAG =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
      # Generate changelog for version tags
      npm run changelog
    fi

after_deploy:
  - echo "Release $TRAVIS_TAG deployed to GitHub"
```

### Heroku deployment
```yaml
# Heroku deployment
language: ruby
rvm: 3.0.0

script: bundle exec rake test

deploy:
  provider: heroku
  api_key: $HEROKU_API_KEY
  app:
    master: myapp-production
    develop: myapp-staging
  run:
    - "rake db:migrate"
    - "rake db:seed"

# Или для нескольких apps
# deploy:
#   - provider: heroku
#     api_key: $HEROKU_API_KEY
#     app: myapp-staging
#     on: develop
#   - provider: heroku
#     api_key: $HEROKU_API_KEY
#     app: myapp-production
#     on: master
```

### AWS deployment
```yaml
# AWS S3 deployment
language: node_js
node_js: "18"

script:
  - npm run build

deploy:
  provider: s3
  access_key_id: $AWS_ACCESS_KEY_ID
  secret_access_key: $AWS_SECRET_ACCESS_KEY
  bucket: my-app-bucket
  region: us-east-1
  local_dir: dist
  skip_cleanup: true
  on:
    branch: main

# AWS Elastic Beanstalk
# deploy:
#   provider: elasticbeanstalk
#   access_key_id: $AWS_ACCESS_KEY_ID
#   secret_access_key: $AWS_SECRET_ACCESS_KEY
#   region: us-east-1
#   app: my-app
#   env: production
#   bucket_name: elasticbeanstalk-us-east-1-123456789
#   bucket_path: my-app
#   on:
#     branch: main
```

### Docker deployment
```yaml
# Docker Hub и deployment
language: minimal

services: docker

script:
  - docker build -t myorg/myapp:$TRAVIS_TAG .
  - docker run myorg/myapp:$TRAVIS_TAG npm test

deploy:
  provider: script
  script: bash docker_push.sh
  on:
    branch: main

# docker_push.sh
#!/bin/bash
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push myorg/myapp:$TRAVIS_TAG

# Создание latest tag
if [[ $TRAVIS_TAG =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  docker tag myorg/myapp:$TRAVIS_TAG myorg/myapp:latest
  docker push myorg/myapp:latest
fi
```

## Интеграция с внешними сервисами

### Code coverage
```yaml
# Codecov integration
language: node_js
node_js: "18"

script:
  - npm run test:coverage

after_success:
  - bash <(curl -s https://codecov.io/bash) -f coverage/lcov.info

# Coveralls
# after_success:
#   - npm install -g coveralls
#   - coveralls < coverage/lcov.info
```

### Code quality tools
```yaml
# Code Climate
language: node_js
node_js: "18"

script:
  - npm run test

after_script:
  - ./cc-test-reporter after-build --exit-code $TRAVIS_TEST_RESULT

# SonarQube
# addons:
#   sonarcloud:
#     organization: "myorg"
#     token: $SONAR_TOKEN

# script:
#   - sonar-scanner
```

### Slack notifications
```yaml
# Slack notifications
notifications:
  slack:
    rooms:
      secure: encrypted_slack_token
    on_success: change
    on_failure: always
    template:
      - "Build <%{build_url}|#%{build_number}> (%{branch} - %{commit} : %{author}): %{message}"
      - "Change view: %{compare_url}"
      - "Build details: %{build_url}"

# Webhook notifications
notifications:
  webhooks:
    urls:
      - https://my-ci-server.example.com/travis/webhook
    on_success: always
    on_failure: always
    on_start: true
```

### Database services
```yaml
# Database services
services:
  - postgresql
  - redis-server
  - mongodb
  - mysql
  - elasticsearch

# Custom database setup
before_script:
  - psql -c 'create database test_db;' -U postgres
  - mysql -e 'CREATE DATABASE test_db;'
  - mongo test_db --eval 'db.createCollection("test_collection")'

# Или использовать Docker для databases
services: docker

before_script:
  - docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=password postgres:13
  - docker run -d -p 6379:6379 redis:6
  - docker run -d -p 27017:27017 mongo:5
```

## Security и Best Practices

### Encrypted variables
```yaml
# Шифрование чувствительных данных
# travis encrypt GITHUB_TOKEN=your_github_token --add
# travis encrypt DOCKER_PASSWORD=your_docker_password --add

env:
  global:
    secure: encrypted_github_token
    secure: encrypted_docker_password

# Шифрование файлов
# travis encrypt-file deploy_key.pem --add
# travis encrypt-file secrets.tar.gz --add

before_install:
  - openssl aes-256-cbc -K $encrypted_key -iv $encrypted_iv -in deploy_key.pem.enc -out deploy_key.pem -d
  - chmod 600 deploy_key.pem
```

### Secure deployments
```yaml
# Безопасные deployments с проверками
language: node_js
node_js: "18"

script:
  - npm run lint
  - npm run test
  - npm run security-audit

before_deploy:
  - echo "Running security checks..."
  - npm audit --audit-level high
  - |
    if [[ $TRAVIS_PULL_REQUEST != "false" ]]; then
      echo "Skipping deploy for PR"
      exit 0
    fi

deploy:
  provider: script
  script: bash secure_deploy.sh
  skip_cleanup: true
  on:
    branch: main
    condition: $TRAVIS_SECURE_ENV_VARS = true

# secure_deploy.sh
#!/bin/bash
set -e

# Additional security checks
echo "Verifying build integrity..."
# Verify checksums, signatures, etc.

# Secure deployment
echo "Deploying securely..."
# Use secure protocols, verify certificates, etc.
```

### Branch protection
```yaml
# Защита веток и conditional deployments
language: node_js
node_js: "18"

branches:
  only:
    - main
    - develop
    - /^feature\/.*$/
    - /^hotfix\/.*$/

jobs:
  include:
    - stage: security
      name: "Security Scan"
      script: npm run security-scan
      if: branch IN (main, develop)

    - stage: test
      name: "Full Test Suite"
      script: npm run test:full
      if: branch = main

    - stage: test
      name: "Quick Tests"
      script: npm run test:quick
      if: branch =~ ^feature/

    - stage: deploy
      name: "Staging Deploy"
      script: npm run deploy:staging
      if: branch = develop AND type != pull_request

    - stage: deploy
      name: "Production Deploy"
      script: npm run deploy:prod
      if: branch = main AND type != pull_request
```

## Enterprise Features

### Travis `CI` Enterprise
```yaml
# Enterprise конфигурация
language: java
jdk: openjdk11

# Enterprise-specific settings
enterprise: true

# Custom enterprise endpoints
# endpoints:
#   api: https://travis-ci.enterprise.com/api
#   source: https://travis-ci.enterprise.com/source

# SSO integration
# sso:
#   enabled: true
#   url: https://sso.company.com

# LDAP integration
# ldap:
#   enabled: true
#   host: ldap.company.com
#   port: 389
#   base_dn: dc=company,dc=com
```

### Multi-tenant setup
```yaml
# Multi-tenant Travis CI
# Использование organizations для разделения проектов

# .travis.yml для org1
org: mycompany-org1
language: node_js

# .travis.yml для org2
org: mycompany-org2
language: python

# Shared configuration через templates
# Можно использовать shared .travis.yml templates
# или наследование конфигураций через before_script
```

### Audit и compliance
```yaml
# Audit logging для enterprise
language: node_js
node_js: "18"

before_install:
  - echo "Audit: Build started by $TRAVIS_COMMIT_AUTHOR on $(date)"

script:
  - npm run audit-build
  - npm test

after_script:
  - |
    echo "Audit: Build completed with status $TRAVIS_TEST_RESULT"
    echo "Duration: $TRAVIS_BUILD_DURATION seconds"
    echo "Commit: $TRAVIS_COMMIT"
    echo "Branch: $TRAVIS_BRANCH"

# Compliance checks
before_deploy:
  - echo "Compliance: Checking security requirements..."
  - npm run compliance-check
  - |
    if [[ $TRAVIS_BUILD_NUMBER -gt 1000 ]]; then
      echo "Compliance: Build number exceeds threshold"
      exit 1
    fi
```

### Custom workers
```yaml
# Custom worker configuration для enterprise
# В Travis CI Enterprise можно настроить custom workers

# worker.yml
name: custom-worker-01
host: worker.company.com
languages:
  - java
  - node_js
  - python
capacity: 4  # concurrent jobs

# Или через environment variables
env:
  TRAVIS_WORKER_NAME: custom-worker-01
  TRAVIS_WORKER_HOST: worker.company.com
  TRAVIS_WORKER_CAPACITY: 4
```

## Миграция и Troubleshooting

### Миграция с Travis `CI` .org на .com (исторический контекст)
```bash
# Миграция с travis-ci.org на travis-ci.com
# 1. Обновить webhook URL в GitHub
# 2. Перегенерировать API tokens
# 3. Обновить .travis.yml если нужно
# 4. Проверить encrypted variables

# Скрипт для проверки миграции
#!/bin/bash
echo "Checking Travis CI migration..."

# Проверить webhook
curl -H "Authorization: token $GITHUB_TOKEN" \
     https://api.github.com/repos/$TRAVIS_REPO_SLUG/hooks \
     | grep travis-ci.com

# Проверить build status
curl -H "Travis-API-Version: 3" \
     -H "Authorization: token $TRAVIS_TOKEN" \
     https://api.travis-ci.com/repo/$TRAVIS_REPO_SLUG/builds \
     | jq '.builds[0].state'
```

### Troubleshooting распространенных проблем
```yaml
# Troubleshooting конфигурация
language: node_js
node_js: "18"

# Увеличить timeout для долгих сборок
script:
  - timeout 30m npm run test:e2e

# Или использовать Travis-specific timeouts
# travis_wait npm run test:e2e

# Debug mode для troubleshooting
env:
  - CI_DEBUG=true

before_script:
  - |
    if [[ $CI_DEBUG == "true" ]]; then
      echo "Debug mode enabled"
      set -x
      env | sort
    fi

# Логирование для debugging
script:
  - |
    echo "Starting build..."
    npm run build 2>&1 | tee build.log
    echo "Build completed with exit code $?"

after_failure:
  - |
    echo "Build failed. Collecting debug information..."
    ls -la
    cat build.log
    npm --version
    node --version
    echo "Environment variables:"
    env | grep TRAVIS
```

### Performance optimization
```yaml
# Оптимизация производительности
language: node_js
node_js: "18"

# Cache для ускорения сборок
cache:
  directories:
    - node_modules
    - $HOME/.npm
    - $HOME/.cache/pip

# Parallel execution
script:
  - npm run lint &
  - npm run test:unit &
  - npm run test:integration &
  wait

# Или использовать Travis matrix для параллелизации
matrix:
  fast_finish: true
  include:
    - name: "Lint"
      script: npm run lint
    - name: "Unit Tests"
      script: npm run test:unit
    - name: "Integration Tests"
      script: npm run test:integration

# Skip unnecessary steps
before_install:
  - |
    if [[ $TRAVIS_PULL_REQUEST == "false" ]]; then
      echo "Full build for branch $TRAVIS_BRANCH"
    else
      echo "PR build - skipping some steps"
      export SKIP_HEAVY_TESTS=true
    fi

script:
  - npm run build
  - npm run test
  - |
    if [[ $SKIP_HEAVY_TESTS != "true" ]]; then
      npm run test:heavy
    fi
```

### Migration guide от других CI/CD систем
```yaml
# Миграция с Jenkins
# Jenkins -> Travis CI mapping:
# Jenkinsfile -> .travis.yml
# stages -> jobs with stages
# when conditions -> if conditions
# credentials -> encrypted variables

language: node_js
node_js: "18"

jobs:
  include:
    - stage: build
      script: npm run build
    - stage: test
      script: npm test
    - stage: deploy
      script: npm run deploy
      if: branch = main

# Миграция с GitLab CI
# .gitlab-ci.yml -> .travis.yml
# image -> language or services: docker
# before_script -> before_script
# script -> script
# after_script -> after_success/after_failure

language: node_js
node_js: "18"

before_script:
  - npm ci

script:
  - npm run lint
  - npm run test
  - npm run build

after_success:
  - npm run coverage

# Миграция с CircleCI
# .circleci/config.yml -> .travis.yml
# executors -> language/services
# commands -> before_script/script
# workflows -> matrix/jobs
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [Jenkins](jenkins.md) — **Self-hosted CI/CD**
- [GitLab CI](gitlab-ci.md)
- [GitHub Actions](github-actions.md)
- [CircleCI](circleci.md) — **Cloud CI/CD**
- [Azure DevOps](azure-devops.md)
