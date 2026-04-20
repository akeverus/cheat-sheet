---
title: "CircleCI"
description: "CircleCI - это облачная платформа для continuous integration и continuous delivery, которая предоставляет высокопроизводительные билды с использованием Docker контейнеров. Этот документ охватывает enterprise-grade конфигурации, продвинутые pipeline паттерны и best practices для и"
tags:
  - platform
  - ci-cd
  - circleci
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# CircleCI

**CircleCI** — это облачная платформа для **continuous integration** и **continuous delivery**, которая предоставляет высокопроизводительные билды с использованием **Docker** контейнеров. Этот документ охватывает **enterprise-grade** конфигурации, продвинутые **pipeline** паттерны и **best practices** для использования **CircleCI** в **production** средах.

## Полезные ссылки
- [CircleCI Documentation](https://circleci.com/docs/)
- [CircleCI Orbs](https://circleci.com/developer/orbs)
- [CircleCI Config Reference](https://circleci.com/docs/config-intro/)
- [CircleCI API](https://circleci.com/docs/api/v2/)
- [CircleCI GitHub](https://github.com/CircleCI-Public)

## Содержание

- [Основы CircleCI](#основы-circleci)
  - [Структура проекта](#структура-проекта)
  - [Базовый config.yml](#базовый-configyml)
- [Продвинутые Pipeline паттерны](#продвинутые-pipeline-паттерны)
  - [Dynamic Configuration](#dynamic-configuration)
  - [Matrix builds и параллельное выполнение](#matrix-builds-и-параллельное-выполнение)
  - [Pipeline triggers и scheduled builds](#pipeline-triggers-и-scheduled-builds)
- [Orbs и reusable components](#orbs-и-reusable-components)
  - [Создание кастомного orb](#создание-кастомного-orb)
  - [Использование orbs](#использование-orbs)
- [Security и Compliance](#security-и-compliance)
  - [Secrets management](#secrets-management)
  - [SAST и DAST scanning](#sast-и-dast-scanning)
- [Monitoring и Metrics](#monitoring-и-metrics)
  - [Pipeline metrics collection](#pipeline-metrics-collection)
  - [Performance optimization](#performance-optimization)
  - [Resource optimization](#resource-optimization)
  - [Caching strategies](#caching-strategies)
- [Enterprise Integration](#enterprise-integration)
  - [SSO и RBAC](#sso-и-rbac)
  - [Audit и Compliance](#audit-и-compliance)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Основы CircleCI

### Структура проекта
Ниже — структура каталогов **CircleCI**-проекта (текст).
```text
my-project/
├── .circleci/
│   ├── config.yml           # Основной конфигурационный файл
│   ├── dynamic-config.yml   # Динамическая конфигурация
│   └── continue-config.yml  # Конфигурация для продолжения
├── scripts/
│   ├── build.sh
│   ├── test.sh
│   ├── deploy.sh
│   └── setup.sh
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── helm/
│   ├── Chart.yaml
│   └── values.yaml
├── terraform/
│   ├── main.tf
│   └── variables.tf
└── .circleci-ignore          # Исключения для контекста сборки
```

### Базовый config.yml
Пример базового **config.yml** (YAML).
```yaml
# .circleci/config.yml
version: 2.1

# Определение исполнителей
executors:
  docker-executor:
    docker:
      - image: cimg/openjdk:17.0
    resource_class: medium
    working_directory: ~/repo

  machine-executor:
    machine:
      image: ubuntu-2204:current
    resource_class: large

# Определение команд
commands:
  install_dependencies:
    steps:
      - run:
          name: Install dependencies
          command: |
            sudo apt-get update
            sudo apt-get install -y curl wget git unzip

  run_tests:
    parameters:
      test_type:
        type: string
        default: "unit"
    steps:
      - run:
          name: Run << parameters.test_type >> tests
          command: |
            if [ "<< parameters.test_type >>" = "unit" ]; then
              mvn test
            elif [ "<< parameters.test_type >>" = "integration" ]; then
              mvn verify -Pintegration-tests
            fi
          no_output_timeout: 20m

# Определение джобов
jobs:
  build:
    executor: docker-executor
    steps:
      - checkout
      - install_dependencies
      - restore_cache:
          keys:
            - maven-{{ checksum "pom.xml" }}
            - maven-
      - run:
          name: Build application
          command: mvn clean compile -DskipTests
      - save_cache:
          paths:
            - ~/.m2/repository
          key: maven-{{ checksum "pom.xml" }}
      - persist_to_workspace:
          root: .
          paths:
            - target/

  test:
    executor: docker-executor
    steps:
      - checkout
      - install_dependencies
      - attach_workspace:
          at: .
      - restore_cache:
          keys:
            - maven-{{ checksum "pom.xml" }}
            - maven-
      - run_tests:
          test_type: unit
      - run:
          name: Generate test reports
          command: |
            mkdir -p test-results/junit
            find . -name "*.xml" -path "*/target/surefire-reports/*" -exec cp {} test-results/junit/ \;
      - store_test_results:
          path: test-results
      - store_artifacts:
          path: test-results
          destination: test-results

  deploy_staging:
    executor: machine-executor
    steps:
      - checkout
      - attach_workspace:
          at: .
      - run:
          name: Deploy to staging
          command: |
            ./scripts/deploy.sh staging
      - run:
          name: Run smoke tests
          command: |
            curl -f https://staging.example.com/health

  deploy_production:
    executor: machine-executor
    steps:
      - checkout
      - attach_workspace:
          at: .
      - run:
          name: Deploy to production
          command: |
            ./scripts/deploy.sh production
      - run:
          name: Verify deployment
          command: |
            curl -f https://production.example.com/health

# Определение workflows
workflows:
  version: 2
  build_and_deploy:
    jobs:
      - build:
          filters:
            branches:
              only: /.*/
      - test:
          requires:
            - build
          filters:
            branches:
              only: /.*/
      - deploy_staging:
          requires:
            - test
          filters:
            branches:
              only: develop
      - deploy_production:
          requires:
            - test
          filters:
            branches:
              only: main
            tags:
              only: /^v.*/
```

## Продвинутые Pipeline паттерны

### Dynamic Configuration
```yaml
# .circleci/config.yml - динамическая конфигурация
version: 2.1

setup: true

orbs:
  continuation: circleci/continuation@0.3.1

jobs:
  setup:
    executor: continuation/default
    steps:
      - checkout
      - run:
          name: Generate config
          command: |
            # Анализ изменений для определения нужных джобов
            if git diff --name-only HEAD~1 | grep -q "frontend/"; then
              echo "export BUILD_FRONTEND=true" >> $BASH_ENV
            fi
            if git diff --name-only HEAD~1 | grep -q "backend/"; then
              echo "export BUILD_BACKEND=true" >> $BASH_ENV
            fi
            if git diff --name-only HEAD~1 | grep -q "infrastructure/"; then
              echo "export BUILD_INFRA=true" >> $BASH_ENV
            fi

            # Генерация конфигурации
            python3 .circleci/generate-config.py > dynamic-config.yml
      - continuation/continue:
          configuration_path: dynamic-config.yml

workflows:
  setup:
    jobs:
      - setup
```

```python
# .circleci/generate-config.py
#!/usr/bin/env python3
import os
import yaml

def generate_config():
    config = {
        'version': 2.1,
        'jobs': {},
        'workflows': {
            'build_and_test': {
                'jobs': []
            }
        }
    }

    # Backend job
    if os.getenv('BUILD_BACKEND'):
        config['jobs']['backend'] = {
            'docker': [{'image': 'cimg/openjdk:17.0'}],
            'steps': [
                'checkout',
                {'run': {'name': 'Build backend', 'command': 'mvn clean package'}},
                {'persist_to_workspace': {'root': '.', 'paths': ['target/']}}
            ]
        }
        config['workflows']['build_and_test']['jobs'].append('backend')

    # Frontend job
    if os.getenv('BUILD_FRONTEND'):
        config['jobs']['frontend'] = {
            'docker': [{'image': 'cimg/node:18.0'}],
            'steps': [
                'checkout',
                {'run': {'name': 'Install dependencies', 'command': 'npm ci'}},
                {'run': {'name': 'Build frontend', 'command': 'npm run build'}},
                {'persist_to_workspace': {'root': '.', 'paths': ['dist/']}}
            ]
        }
        config['workflows']['build_and_test']['jobs'].append('frontend')

    # Infrastructure job
    if os.getenv('BUILD_INFRA'):
        config['jobs']['infrastructure'] = {
            'docker': [{'image': 'hashicorp/terraform:1.0.0'}],
            'steps': [
                'checkout',
                {'run': {'name': 'Terraform validate', 'command': 'cd infrastructure && terraform validate'}}
            ]
        }
        config['workflows']['build_and_test']['jobs'].append('infrastructure')

    return config

if __name__ == '__main__':
    config = generate_config()
    print(yaml.dump(config, default_flow_style=False))
```

### Matrix builds и параллельное выполнение
```yaml
# .circleci/config.yml - matrix builds
version: 2.1

orbs:
  node: circleci/node@5.0

executors:
  test-executor:
    parameters:
      node-version:
        type: string
        default: "18.0"
    docker:
      - image: cimg/node:<< parameters.node-version >>

jobs:
  test_matrix:
    parameters:
      node-version:
        type: string
      test-command:
        type: string
    executor:
      name: test-executor
      node-version: << parameters.node-version >>
    steps:
      - checkout
      - node/install-packages
      - run:
          name: Run tests
          command: << parameters.test-command >>
      - store_test_results:
          path: test-results

workflows:
  matrix_test:
    jobs:
      - test_matrix:
          name: "node-<< matrix.node-version >>-<< matrix.test-type >>"
          matrix:
            parameters:
              node-version: ["16.0", "18.0", "20.0"]
              test-command: ["npm run test:unit", "npm run test:integration"]
```

### Pipeline triggers и scheduled builds
```yaml
# .circleci/config.yml - triggers и schedules
version: 2.1

workflows:
  commit:
    when:
      not:
        equal: [ scheduled_pipeline, << pipeline.trigger_source >> ]
    jobs:
      - build:
          filters:
            branches:
              only: /.*/

  nightly:
    when:
      equal: [ scheduled_pipeline, << pipeline.trigger_source >> ]
    jobs:
      - security_scan:
          filters:
            branches:
              only: main
      - performance_test:
          filters:
            branches:
              only: main

  release:
    jobs:
      - release:
          type: approval
          filters:
            tags:
              only: /^v.*/
            branches:
              ignore: /.*/

jobs:
  build:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - run: echo "Building..."

  security_scan:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - run:
          name: Security scan
          command: |
            # Run security scans
            echo "Running security scans..."

  performance_test:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - run:
          name: Performance tests
          command: |
            # Run performance tests
            echo "Running performance tests..."

  release:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - run:
          name: Create release
          command: |
            # Create GitHub release
            echo "Creating release..."
```

## Orbs и reusable components

### Создание кастомного orb
```yaml
# Custom orb definition
version: 2.1

description: "Orb for Java/Spring Boot applications"

executors:
  openjdk:
    parameters:
      version:
        type: string
        default: "17"
    docker:
      - image: cimg/openjdk:<< parameters.version >>

commands:
  maven_cache:
    steps:
      - restore_cache:
          keys:
            - maven-{{ checksum "pom.xml" }}
            - maven-
      - save_cache:
          paths:
            - ~/.m2/repository
          key: maven-{{ checksum "pom.xml" }}

  build_jar:
    parameters:
      skip-tests:
        type: boolean
        default: false
    steps:
      - run:
          name: Build JAR
          command: |
            mvn clean package <<# parameters.skip-tests >>-DskipTests<</ parameters.skip-tests >>

  test_app:
    steps:
      - run:
          name: Run tests
          command: mvn test
      - store_test_results:
          path: target/surefire-reports

  sonar_scan:
    parameters:
      sonar-project-key:
        type: string
      sonar-organization:
        type: string
    steps:
      - run:
          name: SonarQube scan
          command: |
            mvn sonar:sonar \
              -Dsonar.projectKey=<< parameters.sonar-project-key >> \
              -Dsonar.organization=<< parameters.sonar-organization >>

jobs:
  build_and_test:
    executor: openjdk
    parameters:
      java-version:
        type: string
        default: "17"
    steps:
      - checkout
      - maven_cache
      - build_jar:
          skip-tests: true
      - test_app
      - persist_to_workspace:
          root: .
          paths:
            - target/*.jar

  deploy:
    docker:
      - image: cimg/base:stable
    parameters:
      environment:
        type: string
    steps:
      - attach_workspace:
          at: .
      - run:
          name: Deploy to << parameters.environment >>
          command: |
            echo "Deploying to << parameters.environment >>"
            # Deployment logic here

workflows:
  ci_cd:
    jobs:
      - build_and_test:
          java-version: "17"
      - deploy:
          name: "deploy_staging"
          environment: "staging"
          requires:
            - build_and_test
          filters:
            branches:
              only: develop
      - deploy:
          name: "deploy_production"
          environment: "production"
          requires:
            - build_and_test
          filters:
            branches:
              only: main
```

### Использование orbs
```yaml
# .circleci/config.yml - использование orbs
version: 2.1

orbs:
  aws-cli: circleci/aws-cli@3.1
  kubernetes: circleci/kubernetes@1.3
  docker: circleci/docker@2.1
  slack: circleci/slack@4.10
  sonarcloud: sonarsource/sonarcloud@1.1

executors:
  build-executor:
    docker:
      - image: cimg/openjdk:17.0
    resource_class: medium

jobs:
  build:
    executor: build-executor
    steps:
      - checkout
      - restore_cache:
          keys:
            - maven-{{ checksum "pom.xml" }}
      - run:
          name: Build application
          command: mvn clean package -DskipTests
      - save_cache:
          paths:
            - ~/.m2/repository
          key: maven-{{ checksum "pom.xml" }}
      - persist_to_workspace:
          root: .
          paths:
            - target/

  test:
    executor: build-executor
    steps:
      - checkout
      - attach_workspace:
          at: .
      - restore_cache:
          keys:
            - maven-{{ checksum "pom.xml" }}
      - run:
          name: Run tests
          command: mvn test
      - store_test_results:
          path: target/surefire-reports
      - sonarcloud/scan

  security_scan:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - run:
          name: Security scan
          command: |
            docker run --rm -v $(pwd):/src owasp/zap2docker-stable zap-baseline.py \
              -t https://staging.example.com \
              -r security-report.html
      - store_artifacts:
          path: security-report.html

  docker_build:
    executor: docker/docker-executor
    steps:
      - checkout
      - attach_workspace:
          at: .
      - docker/check
      - docker/build:
          image: myapp
          tag: "${CIRCLE_SHA1}"
      - docker/push:
          image: myapp
          tag: "${CIRCLE_SHA1}"

  deploy:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - kubernetes/install
      - aws-cli/install
      - aws-cli/configure:
          aws-access-key-id: AWS_ACCESS_KEY_ID
          aws-secret-access-key: AWS_SECRET_ACCESS_KEY
          aws-region: AWS_REGION
      - kubernetes/create-or-update-resource:
          resource-file-path: k8s/deployment.yaml
          resource-name: deployment/myapp
      - slack/notify:
          event: fail
          template: basic_fail_1

workflows:
  build_test_deploy:
    jobs:
      - build
      - test:
          requires:
            - build
      - security_scan:
          requires:
            - test
      - docker_build:
          requires:
            - security_scan
      - deploy:
          requires:
            - docker_build
          filters:
            branches:
              only: main
```

## Security и Compliance

### Secrets management
```yaml
# .circleci/config.yml - управление секретами
version: 2.1

commands:
  setup_secrets:
    steps:
      - run:
          name: Setup secrets
          command: |
            # Load secrets from various sources
            echo "Loading secrets..."

            # AWS Secrets Manager
            aws secretsmanager get-secret-value \
              --secret-id "circleci/myapp" \
              --query SecretString \
              --output text > secrets.json

            # HashiCorp Vault
            vault kv get -field=value secret/myapp/database > db_password.txt

            # Azure Key Vault
            az keyvault secret show \
              --name myapp-secret \
              --vault-name myapp-keyvault \
              --query value -o tsv > app_secret.txt

  cleanup_secrets:
    steps:
      - run:
          name: Cleanup secrets
          command: |
            # Secure cleanup
            shred -u secrets.json db_password.txt app_secret.txt || true
            rm -f secrets.json db_password.txt app_secret.txt || true

jobs:
  secure_build:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - setup_secrets
      - run:
          name: Build with secrets
          command: |
            # Use secrets in build process
            export DB_PASSWORD=$(cat db_password.txt)
            export APP_SECRET=$(cat app_secret.txt)

            # Build application
            ./build.sh
      - cleanup_secrets
      - run:
          name: Verify no secrets left
          command: |
            # Ensure no sensitive data remains
            if find . -name "*.txt" -o -name "*.json" | grep -E "(secret|password)"; then
              echo "Sensitive files found!"
              exit 1
            fi

workflows:
  secure_pipeline:
    jobs:
      - secure_build:
          context:
            - aws-credentials
            - vault-credentials
            - azure-credentials
```

### SAST и DAST scanning
```yaml
# .circleci/config.yml - security scanning
version: 2.1

orbs:
  sonarcloud: sonarsource/sonarcloud@1.1
  trivy: aquasecurity/trivy@0.3.0

jobs:
  sast_scan:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - run:
          name: SAST with SonarQube
          command: |
            # Install SonarScanner
            wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.8.0.2856-linux.zip
            unzip sonar-scanner-cli-*.zip
            export PATH=$PATH:$(pwd)/sonar-scanner-*/bin

            # Run scan
            sonar-scanner \
              -Dsonar.projectKey=myapp \
              -Dsonar.sources=. \
              -Dsonar.host.url=$SONAR_HOST_URL \
              -Dsonar.login=$SONAR_TOKEN

  dast_scan:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - run:
          name: DAST with OWASP ZAP
          command: |
            # Install ZAP
            docker run --rm -v $(pwd):/zap/wrk owasp/zap2docker-stable zap-baseline.py \
              -t https://staging.example.com \
              -r zap-report.html \
              -x zap-report.xml

            # Check for high severity issues
            if grep -q "High" zap-report.html; then
              echo "High severity security issues found!"
              exit 1
            fi
      - store_artifacts:
          path: zap-report.html

  container_scan:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - trivy/image_scan:
          image: myapp:latest
          format: json
          output: trivy-results.json
      - run:
          name: Check vulnerabilities
          command: |
            # Parse results and fail on critical issues
            critical_count=$(jq '.Results[].Vulnerabilities[] | select(.Severity == "CRITICAL") | .VulnerabilityID' trivy-results.json | wc -l)

            if [ "$critical_count" -gt 0 ]; then
              echo "Found $critical_count critical vulnerabilities!"
              exit 1
            fi
      - store_artifacts:
          path: trivy-results.json

  compliance_check:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - run:
          name: Compliance checks
          command: |
            # License compliance
            ./scripts/check-licenses.sh

            # Code quality checks
            ./scripts/code-quality.sh

            # Security policy compliance
            ./scripts/security-policy-check.sh

workflows:
  security_pipeline:
    jobs:
      - sast_scan
      - dast_scan:
          requires:
            - sast_scan
      - container_scan:
          requires:
            - dast_scan
      - compliance_check:
          requires:
            - container_scan
```

## Monitoring и Metrics

### Pipeline metrics collection
```yaml
# .circleci/config.yml - сбор метрик
version: 2.1

commands:
  collect_metrics:
    parameters:
      metric_name:
        type: string
      value:
        type: string
      tags:
        type: string
        default: ""
    steps:
      - run:
          name: Send metrics
          command: |
            # Send to DataDog
            curl -X POST "https://api.datadoghq.com/api/v1/series" \
              -H "Content-Type: application/json" \
              -H "DD-API-KEY: ${DD_API_KEY}" \
              -d "{
                \"series\": [{
                  \"metric\": \"circleci.<< parameters.metric_name >>\",
                  \"points\": [[$(date +%s), << parameters.value >>]],
                  \"tags\": [\"project:${CIRCLE_PROJECT_REPONAME}\", \"branch:${CIRCLE_BRANCH}\"<<# parameters.tags >>, << parameters.tags >><</ parameters.tags >>]
                }]
              }"

jobs:
  build_with_metrics:
    docker:
      - image: cimg/openjdk:17.0
    steps:
      - checkout
      - run:
          name: Record build start
          command: echo "BUILD_START=$(date +%s)" >> $BASH_ENV
      - run:
          name: Build application
          command: mvn clean package
      - run:
          name: Record build end
          command: |
            echo "BUILD_END=$(date +%s)" >> $BASH_ENV
            BUILD_DURATION=$((BUILD_END - BUILD_START))
      - collect_metrics:
          metric_name: "build.duration"
          value: "${BUILD_DURATION}"
      - collect_metrics:
          metric_name: "build.status"
          value: "1"
          tags: "status:success"

  test_with_metrics:
    docker:
      - image: cimg/openjdk:17.0
    steps:
      - checkout
      - run:
          name: Run tests
          command: |
            TEST_START=$(date +%s)
            mvn test
            TEST_END=$(date +%s)
            TEST_DURATION=$((TEST_END - TEST_START))

            # Extract test metrics
            TOTAL_TESTS=$(find . -name "*.xml" -exec grep -h "testsuite" {} \; | sed 's/.*tests="\([^"]*\)".*/\1/' | awk '{sum += $1} END {print sum}')
            FAILED_TESTS=$(find . -name "*.xml" -exec grep -h "testsuite" {} \; | sed 's/.*failures="\([^"]*\)".*/\1/' | awk '{sum += $1} END {print sum}')

            echo "TEST_DURATION=$TEST_DURATION" >> $BASH_ENV
            echo "TOTAL_TESTS=$TOTAL_TESTS" >> $BASH_ENV
            echo "FAILED_TESTS=$FAILED_TESTS" >> $BASH_ENV
      - collect_metrics:
          metric_name: "test.duration"
          value: "${TEST_DURATION}"
      - collect_metrics:
          metric_name: "test.total"
          value: "${TOTAL_TESTS}"
      - collect_metrics:
          metric_name: "test.failed"
          value: "${FAILED_TESTS}"

workflows:
  build_and_test:
    jobs:
      - build_with_metrics
      - test_with_metrics:
          requires:
            - build_with_metrics
```

### Performance optimization

### Resource optimization
```yaml
# .circleci/config.yml - оптимизация ресурсов
version: 2.1

executors:
  small:
    docker:
      - image: cimg/base:stable
    resource_class: small

  medium:
    docker:
      - image: cimg/base:stable
    resource_class: medium

  large:
    docker:
      - image: cimg/base:stable
    resource_class: large

  xlarge:
    docker:
      - image: cimg/base:stable
    resource_class: xlarge

commands:
  conditional_resource_class:
    parameters:
      small_command:
        type: string
      large_command:
        type: string
        default: ""
    steps:
      - run:
          name: Execute command based on resource class
          command: |
            if [ "$CIRCLE_NODE_TOTAL" = "1" ]; then
              echo "Running on small instance"
              << parameters.small_command >>
            else
              echo "Running on large instance"
              << parameters.large_command >>
            fi

jobs:
  optimized_build:
    executor: medium
    parallelism: 2
    steps:
      - checkout
      - run:
          name: Install dependencies
          command: |
            # Parallel dependency installation
            case $CIRCLE_NODE_INDEX in
              0)
                npm ci
                ;;
              1)
                pip install -r requirements.txt
                ;;
            esac
      - run:
          name: Build
          command: |
            # Parallel build
            if [ "$CIRCLE_NODE_INDEX" = "0" ]; then
              npm run build:frontend
            else
              python build_backend.py
            fi
      - persist_to_workspace:
          root: .
          paths:
            - "dist/"
            - "build/"

  parallel_test:
    executor: large
    parallelism: 4
    steps:
      - checkout
      - attach_workspace:
          at: .
      - run:
          name: Run parallel tests
          command: |
            # Split tests across parallel containers
            TEST_FILES=$(find . -name "*Test.java" | sort)
            TOTAL_TESTS=$(echo "$TEST_FILES" | wc -l)
            TESTS_PER_CONTAINER=$((TOTAL_TESTS / CIRCLE_NODE_TOTAL))
            START_INDEX=$((CIRCLE_NODE_INDEX * TESTS_PER_CONTAINER + 1))
            END_INDEX=$(((CIRCLE_NODE_INDEX + 1) * TESTS_PER_CONTAINER))

            if [ "$CIRCLE_NODE_INDEX" = "$((CIRCLE_NODE_TOTAL - 1))" ]; then
              END_INDEX=$TOTAL_TESTS
            fi

            TEST_BATCH=$(echo "$TEST_FILES" | sed -n "${START_INDEX},${END_INDEX}p")
            echo "$TEST_BATCH" | xargs mvn test -Dtest=
      - store_test_results:
          path: target/surefire-reports

workflows:
  optimized_pipeline:
    jobs:
      - optimized_build
      - parallel_test:
          requires:
            - optimized_build
```

### Caching strategies
```yaml
# .circleci/config.yml - продвинутое кэширование
version: 2.1

commands:
  smart_cache:
    parameters:
      cache_key:
        type: string
      cache_paths:
        type: string
      restore_key_fallback:
        type: string
        default: ""
    steps:
      - restore_cache:
          keys:
            - << parameters.cache_key >>-{{ .Branch }}-{{ checksum "<< parameters.cache_paths >>/dependency-file" }}
            - << parameters.cache_key >>-{{ .Branch }}
            <<# parameters.restore_key_fallback >>- << parameters.restore_key_fallback >><</ parameters.restore_key_fallback >>
            - << parameters.cache_key >>-
      - save_cache:
          paths:
            - << parameters.cache_paths >>
          key: << parameters.cache_key >>-{{ .Branch }}-{{ checksum "<< parameters.cache_paths >>/dependency-file" }}

jobs:
  cached_build:
    docker:
      - image: cimg/openjdk:17.0
    steps:
      - checkout

      # Maven cache
      - smart_cache:
          cache_key: "maven"
          cache_paths: "~/.m2/repository"
          restore_key_fallback: "maven-master"

      # Node.js cache
      - smart_cache:
          cache_key: "npm"
          cache_paths: "~/.npm"

      # Docker layer cache
      - setup_remote_docker:
          version: 20.10.14
      - run:
          name: Load Docker cache
          command: |
            set +e
            docker load < /tmp/docker-cache.tar || true
            set -e
      - run:
          name: Build Docker image
          command: |
            docker build --cache-from myapp:latest -t myapp:$CIRCLE_SHA1 .
            docker save myapp:$CIRCLE_SHA1 > /tmp/docker-cache.tar
      - save_cache:
          paths:
            - /tmp/docker-cache.tar
          key: docker-{{ .Branch }}-{{ epoch }}

      # Build artifacts
      - run:
          name: Build application
          command: mvn package -DskipTests

      # Cache build artifacts
      - save_cache:
          paths:
            - target/
          key: build-artifacts-{{ .Branch }}-{{ checksum "pom.xml" }}

workflows:
  cached_pipeline:
    jobs:
      - cached_build
```

## Enterprise Integration

### SSO и RBAC
```yaml
# .circleci/config.yml - enterprise features
version: 2.1

commands:
  setup_enterprise_auth:
    steps:
      - run:
          name: Configure enterprise authentication
          command: |
            # SAML/SSO configuration
            export SAML_IDP_URL="https://sso.example.com"
            export SAML_ENTITY_ID="circleci-myorg"

            # Configure API tokens
            curl -X POST https://circleci.com/api/v2/project/${CIRCLE_PROJECT_ID}/token \
              -H "Circle-Token: ${CIRCLE_TOKEN}" \
              -H "Content-Type: application/json" \
              -d '{
                "name": "enterprise-token",
                "scope": ["read", "write", "admin"]
              }'

  enforce_policies:
    steps:
      - run:
          name: Enforce security policies
          command: |
            # Check branch protection
            if [ "$CIRCLE_BRANCH" = "main" ]; then
              echo "Main branch deployment - additional checks required"

              # Check approvals
              APPROVALS=$(curl -s "https://circleci.com/api/v2/workflow/${CIRCLE_WORKFLOW_ID}" \
                -H "Circle-Token: ${CIRCLE_TOKEN}" | jq '.approvals')

              if [ "$APPROVALS" = "null" ] || [ "$APPROVALS" = "[]" ]; then
                echo "No approvals found for main branch deployment!"
                exit 1
              fi
            fi

            # Check security scan results
            if [ ! -f "security-scan-results.json" ]; then
              echo "Security scan results not found!"
              exit 1
            fi

            CRITICAL_VULNS=$(jq '.critical | length' security-scan-results.json)
            if [ "$CRITICAL_VULNS" -gt 0 ]; then
              echo "Critical security vulnerabilities found!"
              exit 1
            fi

jobs:
  enterprise_build:
    docker:
      - image: cimg/base:stable
    steps:
      - setup_enterprise_auth
      - enforce_policies
      - checkout
      - run:
          name: Enterprise build
          command: |
            echo "Building with enterprise controls..."

workflows:
  enterprise_pipeline:
    jobs:
      - enterprise_build:
          context:
            - enterprise-context
            - security-context
```

### Audit и Compliance
```yaml
# .circleci/config.yml - audit и compliance
version: 2.1

commands:
  audit_pipeline:
    steps:
      - run:
          name: Audit pipeline execution
          command: |
            # Collect audit information
            AUDIT_DATA=$(cat <<EOF
            {
              "timestamp": "$(date -Iseconds)",
              "pipeline_id": "$CIRCLE_WORKFLOW_ID",
              "project": "$CIRCLE_PROJECT_REPONAME",
              "branch": "$CIRCLE_BRANCH",
              "commit": "$CIRCLE_SHA1",
              "user": "$CIRCLE_USERNAME",
              "job": "$CIRCLE_JOB",
              "executor": "$CIRCLE_NODE_INDEX/$CIRCLE_NODE_TOTAL",
              "environment": {
                "CIRCLECI": "true",
                "CONTEXT": "$CIRCLE_OIDC_TOKEN_AUDIENCE"
              }
            }
            EOF
            )

            # Send to audit system
            curl -X POST https://audit.example.com/api/v1/events \
              -H "Content-Type: application/json" \
              -H "Authorization: Bearer ${AUDIT_TOKEN}" \
              -d "$AUDIT_DATA"

  compliance_check:
    steps:
      - run:
          name: Compliance validation
          command: |
            # Check license compliance
            ./scripts/check-licenses.sh

            # Check code quality standards
            ./scripts/code-quality-check.sh

            # Validate security policies
            ./scripts/security-policy-validation.sh

            # Check data handling compliance
            ./scripts/data-compliance-check.sh

jobs:
  audited_build:
    docker:
      - image: cimg/base:stable
    steps:
      - audit_pipeline
      - compliance_check
      - checkout
      - run:
          name: Build with audit trail
          command: |
            echo "Building with full audit trail..."
            # Build commands here

  compliance_scan:
    docker:
      - image: cimg/base:stable
    steps:
      - checkout
      - run:
          name: Comprehensive compliance scan
          command: |
            # Run compliance checks
            ./scripts/compliance-scan.sh --full

            # Generate compliance report
            ./scripts/generate-compliance-report.sh

            # Archive for audit
            mkdir -p compliance-reports
            cp compliance-report.json compliance-reports/
      - store_artifacts:
          path: compliance-reports/

workflows:
  audited_pipeline:
    jobs:
      - audited_build
      - compliance_scan:
          requires:
            - audited_build
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [[jenkins|Jenkins]] — альтернативная **CI/CD** платформа
- [[gitlab-ci|GitLab CI]]
- [[github-actions|GitHub Actions]] — **CI/CD** в **GitHub**
- [[docker-basics|Docker]] — контейнеризация
- [[kubernetes-advanced|Kubernetes]] — оркестрация контейнеров
