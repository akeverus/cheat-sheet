---
title: "GitLab CI/CD"
description: "GitLab CI/CD — встроенная система continuous integration и continuous delivery в GitLab. Она позволяет автоматизировать процесс сборки, тестирования и развертывания приложений с помощью YAML конфигурации. Этот документ охватывает enterprise-grade паттерны, продвинутые pipeline ко"
tags:
  - platform
  - ci-cd
  - gitlab-ci
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# GitLab CI/CD

**GitLab** CI/CD — встроенная система **continuous integration** и **continuous delivery** в **GitLab**. Она позволяет автоматизировать процесс сборки, тестирования и развертывания приложений с помощью **YAML** конфигурации. Этот документ охватывает **enterprise-grade** паттерны, продвинутые **pipeline** конфигурации и **best practices** для использования **GitLab** CI/CD в **production** средах.

## Полезные ссылки
- [GitLab CI/CD Documentation](https://docs.gitlab.com/ee/ci/)
- [GitLab Runner](https://docs.gitlab.com/runner/)
- [GitLab CI/CD Pipeline Reference](https://docs.gitlab.com/ee/ci/yaml/)
- [GitLab Auto DevOps](https://docs.gitlab.com/ee/topics/autodevops/)
- [GitLab Container Registry](https://docs.gitlab.com/ee/user/packages/container_registry/)

## Содержание

- [Основы GitLab CI/CD](#основы-gitlab-cicd)
  - [Структура проекта](#структура-проекта)
  - [Базовый .gitlab-ci.yml](#базовый-gitlab-ciyml)
- [Продвинутые Pipeline паттерны](#продвинутые-pipeline-паттерны)
  - [Multi-environment deployment](#multi-environment-deployment)
  - [Parallel execution и matrix builds](#parallel-execution-и-matrix-builds)
  - [Child pipelines и parent-child relationships](#child-pipelines-и-parent-child-relationships)
  - [Dynamic pipeline generation](#dynamic-pipeline-generation)
- [GitLab Runner конфигурация](#gitlab-runner-конфигурация)
  - [Docker executor](#docker-executor)
  - [Kubernetes executor](#kubernetes-executor)
  - [SSH executor](#ssh-executor)
- [Security и Compliance](#security-и-compliance)
  - [Secret management](#secret-management)
  - [SAST и DAST integration](#sast-и-dast-integration)
  - [Compliance pipelines](#compliance-pipelines)
- [Monitoring и Metrics](#monitoring-и-metrics)
  - [Pipeline metrics](#pipeline-metrics)
  - [Prometheus integration](#prometheus-integration)
- [Performance Optimization](#performance-optimization)
  - [Pipeline optimization](#pipeline-optimization)
  - [Resource optimization](#resource-optimization)
- [GitOps и Infrastructure as Code](#gitops-и-infrastructure-as-code)
  - [GitOps pipeline](#gitops-pipeline)
  - [Multi-cluster deployment](#multi-cluster-deployment)
- [Решение проблем](#решение-проблем)
  - [Common issues](#common-issues)
  - [Pipeline failure recovery](#pipeline-failure-recovery)
  - [Performance debugging](#performance-debugging)
- [См. также](#см-также)

## Основы GitLab CI/CD

### Структура проекта
Ниже — структура проекта с **GitLab** CI/CD (текст).
```text
my-project/
├── .gitlab-ci.yml          # Основной CI/CD конфигурационный файл
├── .gitlab/               # GitLab специфические файлы
│   ├── issue_templates/
│   ├── merge_request_templates/
│   └── CODEOWNERS
├── scripts/               # CI/CD скрипты
│   ├── build.sh
│   ├── test.sh
│   ├── deploy.sh
│   └── docker-build.sh
├── docker/                # Docker файлы
│   ├── Dockerfile
│   ├── Dockerfile.test
│   └── docker-compose.yml
├── helm/                  # Kubernetes manifests
│   ├── Chart.yaml
│   ├── values.yaml
│   └── templates/
├── ansible/               # Ansible плейбуки
└── terraform/             # Infrastructure as Code
```

### Базовый .gitlab-ci.yml
```yaml
# .gitlab-ci.yml - базовая конфигурация
stages:
  - validate
  - test
  - build
  - deploy
  - cleanup

variables:
  DOCKER_DRIVER: overlay2
  DOCKER_TLS_CERTDIR: "/certs"
  DOCKER_HOST: tcp://docker:2376
  DOCKER_TLS_VERIFY: 1
  DOCKER_CERT_PATH: "$DOCKER_TLS_CERTDIR/client"
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"
  GRADLE_OPTS: "-Dorg.gradle.daemon=false"

cache:
  paths:
    - .m2/repository/
    - node_modules/
    - .gradle/caches/
    - .gradle/wrapper/

# Template for common job configuration
.job_template: &job_template
  image: docker:latest
  services:
    - docker:20.10.21-dind
  before_script:
    - docker info

# Validation stage
validate:
  <<: *job_template
  stage: validate
  script:
    - echo "Validating code..."
    - ./scripts/validate.sh
  only:
    - merge_requests
    - main
    - develop

# Test stage
test:
  <<: *job_template
  stage: test
  script:
    - echo "Running tests..."
    - ./scripts/test.sh
  coverage: '/TOTAL.*\s+(\d+%)$/'
  artifacts:
    reports:
      coverage_report:
        coverage_format: cobertura
        path: target/site/cobertura/coverage.xml
      junit: target/surefire-reports/TEST-*.xml
    expire_in: 1 week
  only:
    - merge_requests
    - main
    - develop

# Build stage
build:
  <<: *job_template
  stage: build
  script:
    - echo "Building application..."
    - ./scripts/build.sh
  artifacts:
    paths:
      - target/*.jar
      - build/libs/*.jar
    expire_in: 1 week
  only:
    - main
    - develop
    - tags

# Deploy to staging
deploy_staging:
  <<: *job_template
  stage: deploy
  script:
    - echo "Deploying to staging..."
    - ./scripts/deploy.sh staging
  environment:
    name: staging
    url: https://staging.example.com
  only:
    - develop
  when: manual

# Deploy to production
deploy_production:
  <<: *job_template
  stage: deploy
  script:
    - echo "Deploying to production..."
    - ./scripts/deploy.sh production
  environment:
    name: production
    url: https://production.example.com
  only:
    - main
    - tags
  when: manual

# Cleanup
cleanup:
  <<: *job_template
  stage: cleanup
  script:
    - echo "Cleaning up..."
    - docker system prune -f
  when: always
```

## Продвинутые Pipeline паттерны

### Multi-environment deployment
```yaml
# .gitlab-ci.yml - multi-environment deployment
stages:
  - build
  - test
  - deploy
  - promote

variables:
  DOCKER_IMAGE_TAG: $CI_COMMIT_REF_SLUG-$CI_COMMIT_SHORT_SHA

# Build once, deploy everywhere
build:
  stage: build
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$DOCKER_IMAGE_TAG .
    - docker push $CI_REGISTRY_IMAGE:$DOCKER_IMAGE_TAG
  only:
    - merge_requests
    - main
    - develop

test:
  stage: test
  script:
    - docker run --rm $CI_REGISTRY_IMAGE:$DOCKER_IMAGE_TAG ./run-tests.sh
  coverage: '/TOTAL.*\s+(\d+%)$/'
  artifacts:
    reports:
      coverage_report:
        coverage_format: cobertura
        path: coverage/cobertura-coverage.xml
      junit: test-results/junit-report.xml

# Dynamic environments based on branch/tag
deploy_dynamic:
  stage: deploy
  script:
    - ./deploy.sh $CI_ENVIRONMENT_NAME
  environment:
    name: $CI_COMMIT_REF_SLUG
    url: https://$CI_COMMIT_REF_SLUG.example.com
    on_stop: stop_dynamic
  only:
    - branches
    - tags
  except:
    - main

stop_dynamic:
  stage: deploy
  script:
    - ./stop-environment.sh $CI_ENVIRONMENT_NAME
  environment:
    name: $CI_COMMIT_REF_SLUG
    action: stop
  when: manual
  only:
    - branches
    - tags
  except:
    - main

# Staging deployment
deploy_staging:
  stage: deploy
  script:
    - ./deploy.sh staging $DOCKER_IMAGE_TAG
  environment:
    name: staging
    url: https://staging.example.com
  only:
    - develop
  dependencies:
    - build

# Production deployment with approval
deploy_production:
  stage: deploy
  script:
    - ./deploy.sh production $DOCKER_IMAGE_TAG
  environment:
    name: production
    url: https://production.example.com
  only:
    - main
    - tags
  when: manual
  dependencies:
    - build

# Rollback job
rollback_production:
  stage: promote
  script:
    - ./rollback.sh production
  environment:
    name: production
    url: https://production.example.com
  when: manual
  only:
    - main
    - tags
```

### Parallel execution и matrix builds
```yaml
# .gitlab-ci.yml - parallel execution
stages:
  - build
  - test
  - deploy

variables:
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository"

cache:
  paths:
    - .m2/repository/
    - target/

# Build job
build:
  stage: build
  script:
    - mvn compile -q
  artifacts:
    paths:
      - target/classes/
    expire_in: 1 hour

# Parallel test execution
test:unit:
  stage: test
  script:
    - mvn test -Dtest=UnitTest*
  artifacts:
    reports:
      junit: target/surefire-reports/TEST-*.xml
    expire_in: 1 week
  dependencies:
    - build

test:integration:
  stage: test
  script:
    - mvn verify -Pintegration-tests
  artifacts:
    reports:
      junit: target/failsafe-reports/TEST-*.xml
    expire_in: 1 week
  dependencies:
    - build

test:performance:
  stage: test
  script:
    - mvn test -Dtest=PerformanceTest*
  artifacts:
    reports:
      junit: target/surefire-reports/TEST-*.xml
    expire_in: 1 week
  dependencies:
    - build

test:security:
  stage: test
  script:
    - mvn dependency-check:check
  artifacts:
    reports:
      sast: dependency-check-report.json
    expire_in: 1 week

# Matrix build for multiple environments
build_matrix:
  stage: build
  script:
    - echo "Building for $RUNTIME_VERSION on $OS"
    - ./build.sh $RUNTIME_VERSION
  parallel:
    matrix:
      - RUNTIME_VERSION: ["11", "17", "21"]
        OS: ["ubuntu", "alpine"]
  artifacts:
    paths:
      - build/libs/*.jar
    expire_in: 1 hour

# Cross-platform testing
test_matrix:
  stage: test
  script:
    - echo "Testing on $BROWSER and $OS"
    - ./run-tests.sh $BROWSER
  parallel:
    matrix:
      - BROWSER: ["chrome", "firefox", "safari"]
        OS: ["linux", "macos", "windows"]
  artifacts:
    reports:
      junit: test-results/junit-*.xml
    expire_in: 1 week
```

### Child pipelines и parent-child relationships
```yaml
# Parent pipeline - .gitlab-ci.yml
stages:
  - trigger
  - deploy

# Trigger child pipeline
trigger_child:
  stage: trigger
  script:
    - echo "Triggering child pipeline..."
  trigger:
    include:
      - local: 'child-pipeline.yml'
    strategy: depend

# Deploy after child pipeline success
deploy_after_child:
  stage: deploy
  script:
    - echo "Deploying after child pipeline completion"
  needs:
    - pipeline: $CI_PIPELINE_ID
  when: manual
```

```yaml
# Child pipeline - child-pipeline.yml
stages:
  - build
  - test
  - package

build_child:
  stage: build
  script:
    - echo "Building in child pipeline"
    - mvn compile

test_child:
  stage: test
  script:
    - echo "Testing in child pipeline"
    - mvn test

package_child:
  stage: package
  script:
    - echo "Packaging in child pipeline"
    - mvn package
  artifacts:
    paths:
      - target/*.jar
```

### Dynamic pipeline generation
```yaml
# .gitlab-ci.yml - dynamic pipeline
stages:
  - generate
  - execute

generate_pipeline:
  stage: generate
  script:
    - apt-get update && apt-get install -y jq
    - |
      # Generate dynamic jobs based on project structure
      cat > generated-jobs.yml << EOF
      stages:
        - build
        - test

      # Dynamically generate build jobs
      $(for service in services/*; do
        if [ -d "$service" ]; then
          service_name=$(basename "$service")
          cat << JOB_EOF
      build_$service_name:
        stage: build
        script:
          - cd $service
          - ./build.sh
        artifacts:
          paths:
            - $service/target/*.jar
          expire_in: 1 hour

      test_$service_name:
        stage: test
        script:
          - cd $service
          - ./test.sh
        dependencies:
          - build_$service_name

      JOB_EOF
        fi
      done)
      EOF
  artifacts:
    paths:
      - generated-jobs.yml

execute_generated:
  stage: execute
  trigger:
    include:
      - artifact: generated-jobs.yml
        job: generate_pipeline
    strategy: depend
```

## GitLab Runner конфигурация

### Docker executor
```toml
# config.toml - GitLab Runner configuration
concurrent = 4
check_interval = 0

[[runners]]
  name = "docker-runner"
  url = "https://gitlab.example.com/"
  token = "RUNNER_TOKEN"
  executor = "docker"
  [runners.docker]
    tls_verify = false
    image = "docker:latest"
    privileged = true
    disable_cache = false
    volumes = ["/certs/client", "/cache"]
    shm_size = 0
  [runners.cache]
    Type = "s3"
    ServerAddress = "s3.amazonaws.com"
    AccessKey = "ACCESS_KEY"
    SecretKey = "SECRET_KEY"
    BucketName = "runner-cache"
    Insecure = false
```

### Kubernetes executor
```toml
# config.toml - Kubernetes executor
concurrent = 10
check_interval = 0

[[runners]]
  name = "kubernetes-runner"
  url = "https://gitlab.example.com/"
  token = "RUNNER_TOKEN"
  executor = "kubernetes"
  [runners.kubernetes]
    host = ""
    bearer_token_overwrite_allowed = false
    image = "ubuntu:20.04"
    namespace = "gitlab-runners"
    privileged = false
    service_account_overwrite_allowed = ""
    pod_annotations_overwrite_allowed = ""
    [runners.kubernetes.node_selector]
      "kubernetes.io/os" = "linux"
    [runners.kubernetes.affinity]
      nodeaffinity:
        requiredDuringSchedulingIgnoredDuringExecution:
          nodeSelectorTerms:
          - matchExpressions:
            - key: kubernetes.io/os
              operator: In
              values:
              - linux
  [runners.kubernetes.volumes]
    [[runners.kubernetes.volumes.empty_dir]]
      name = "docker-certs"
      mount_path = "/certs/client"
      medium = "Memory"
  [runners.cache]
    Type = "s3"
    ServerAddress = "s3.amazonaws.com"
    AccessKey = "ACCESS_KEY"
    SecretKey = "SECRET_KEY"
    BucketName = "runner-cache"
```

### SSH executor
```toml
# config.toml - SSH executor
concurrent = 1
check_interval = 0

[[runners]]
  name = "ssh-runner"
  url = "https://gitlab.example.com/"
  token = "RUNNER_TOKEN"
  executor = "ssh"
  [runners.ssh]
    host = "build-server.example.com"
    port = "22"
    user = "gitlab-runner"
    password = ""
    identity_file = "/home/gitlab-runner/.ssh/id_rsa"
  [runners.cache]
    Type = "s3"
    ServerAddress = "s3.amazonaws.com"
    AccessKey = "ACCESS_KEY"
    SecretKey = "SECRET_KEY"
    BucketName = "runner-cache"
```

## Security и Compliance

### Secret management
```yaml
# .gitlab-ci.yml - secrets management
stages:
  - build
  - deploy

variables:
  # GitLab predefined variables
  DOCKER_REGISTRY: $CI_REGISTRY
  DOCKER_USERNAME: $CI_REGISTRY_USER
  DOCKER_PASSWORD: $CI_REGISTRY_PASSWORD

  # Custom secrets
  AWS_ACCESS_KEY_ID: $AWS_ACCESS_KEY
  AWS_SECRET_ACCESS_KEY: $AWS_SECRET_KEY
  DATABASE_URL: $PRODUCTION_DB_URL

build:
  stage: build
  script:
    - docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD $DOCKER_REGISTRY
    - docker build -t myapp:$CI_COMMIT_SHA .
    - docker push myapp:$CI_COMMIT_SHA

deploy:
  stage: deploy
  script:
    - aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
    - aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
    - ./deploy-to-aws.sh
  environment:
    name: production
    url: https://myapp.example.com
  only:
    - main
  when: manual
```

### SAST и DAST integration
```yaml
# .gitlab-ci.yml - security scanning
stages:
  - build
  - test
  - security
  - deploy

include:
  - template: Security/SAST.gitlab-ci.yml
  - template: Security/Secret-Detection.gitlab-ci.yml
  - template: Security/Dependency-Scanning.gitlab-ci.yml

# Custom security jobs
security_audit:
  stage: security
  script:
    - npm audit --audit-level=moderate
    - safety check --json > security-report.json
  artifacts:
    reports:
      sast: security-report.json
    expire_in: 1 week
  allow_failure: true

dependency_check:
  stage: security
  script:
    - mvn org.owasp:dependency-check-maven:check
  artifacts:
    reports:
      dependency_scanning: target/dependency-check-report.json
    expire_in: 1 week

container_scanning:
  stage: security
  script:
    - docker run --rm -v $(pwd):/tmp grype --output json /tmp > container-scan.json
  artifacts:
    reports:
      container_scanning: container-scan.json
    expire_in: 1 week

license_check:
  stage: security
  script:
    - licensee --json > licenses.json
    - |
      # Check for forbidden licenses
      if jq -e '.[] | select(.license.key | IN("MS-PL", "WTFPL", "JSON"))' licenses.json > /dev/null; then
        echo "Forbidden license detected!"
        exit 1
      fi
  artifacts:
    reports:
      license_scanning: licenses.json
    expire_in: 1 week
```

### Compliance pipelines
```yaml
# .gitlab-ci.yml - compliance pipeline
stages:
  - validate
  - compliance
  - build
  - deploy

validate_commit:
  stage: validate
  script:
    - |
      # Check commit message format
      if ! echo "$CI_COMMIT_MESSAGE" | grep -qE "^(feat|fix|docs|style|refactor|test|chore)(\(.+\))?: .{1,}"; then
        echo "Commit message does not follow conventional commits format"
        exit 1
      fi

validate_code:
  stage: validate
  script:
    - ./scripts/code-quality-check.sh
  artifacts:
    reports:
      codequality: gl-code-quality-report.json
    expire_in: 1 week

check_compliance:
  stage: compliance
  script:
    - ./scripts/compliance-check.sh
  artifacts:
    reports:
      metrics: compliance-metrics.json
    expire_in: 1 week

audit_trail:
  stage: compliance
  script:
    - |
      # Log all CI/CD activities
      echo "{\"timestamp\": \"$CI_JOB_STARTED_AT\", \"job\": \"$CI_JOB_NAME\", \"stage\": \"$CI_JOB_STAGE\", \"user\": \"$GITLAB_USER_EMAIL\", \"commit\": \"$CI_COMMIT_SHA\"}" >> audit.log
  artifacts:
    paths:
      - audit.log
    expire_in: 1 month
  when: always
```

## Monitoring и Metrics

### Pipeline metrics
```yaml
# .gitlab-ci.yml - pipeline monitoring
stages:
  - build
  - test
  - metrics
  - deploy

build:
  stage: build
  script:
    - mvn compile
  after_script:
    - |
      # Send build metrics to monitoring
      curl -X POST https://monitoring.example.com/api/v1/metrics \
        -H "Content-Type: application/json" \
        -d "{
          \"metric\": \"build_duration\",
          \"value\": $CI_JOB_DURATION,
          \"labels\": {
            \"project\": \"$CI_PROJECT_NAME\",
            \"branch\": \"$CI_COMMIT_REF_NAME\",
            \"job\": \"$CI_JOB_NAME\"
          }
        }"

test:
  stage: test
  script:
    - mvn test
  coverage: '/TOTAL.*\s+(\d+%)$/'
  after_script:
    - |
      # Extract and send test metrics
      TOTAL_TESTS=$(grep -oP 'Tests run: \K\d+' target/surefire-reports/*.txt | tail -1)
      FAILED_TESTS=$(grep -oP 'Failures: \K\d+' target/surefire-reports/*.txt | tail -1)
      COVERAGE=$(echo $CI_MERGE_REQUEST_TARGET_BRANCH_NAME | grep -oP '\d+(?=%)' || echo "0")

      curl -X POST https://monitoring.example.com/api/v1/metrics \
        -H "Content-Type: application/json" \
        -d "{
          \"metric\": \"test_results\",
          \"value\": $TOTAL_TESTS,
          \"labels\": {
            \"project\": \"$CI_PROJECT_NAME\",
            \"total_tests\": \"$TOTAL_TESTS\",
            \"failed_tests\": \"$FAILED_TESTS\",
            \"coverage\": \"$COVERAGE\"
          }
        }"

collect_metrics:
  stage: metrics
  script:
    - |
      # Collect comprehensive pipeline metrics
      echo "Pipeline Metrics:" > pipeline-metrics.txt
      echo "Duration: $CI_PIPELINE_DURATION seconds" >> pipeline-metrics.txt
      echo "Jobs: $CI_PIPELINE_JOBS" >> pipeline-metrics.txt
      echo "Created: $CI_PIPELINE_CREATED_AT" >> pipeline-metrics.txt
      echo "Finished: $CI_PIPELINE_FINISHED_AT" >> pipeline-metrics.txt
  artifacts:
    paths:
      - pipeline-metrics.txt
    expire_in: 30 days
  when: always
```

### Prometheus integration
```yaml
# prometheus.yml - GitLab metrics collection
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'gitlab-runner'
    static_configs:
      - targets: ['runner1:9252', 'runner2:9252']
    metrics_path: '/metrics'

  - job_name: 'gitlab-ci-pipelines'
    gitlab_sd_configs:
      - gitlab_url: 'https://gitlab.example.com'
        project: 'mygroup/myproject'
    relabel_configs:
      - source_labels: ['__meta_gitlab_pipeline_status']
        target_label: 'pipeline_status'
      - source_labels: ['__meta_gitlab_pipeline_ref']
        target_label: 'pipeline_ref'
```

## Performance Optimization

### Pipeline optimization
```yaml
# .gitlab-ci.yml - optimized pipeline
stages:
  - validate
  - build
  - test
  - deploy

variables:
  DOCKER_BUILDKIT: 1
  COMPOSE_DOCKER_CLI_BUILD: 1
  MAVEN_OPTS: "-Dmaven.repo.local=$CI_PROJECT_DIR/.m2/repository -Dmaven.artifact.threads=10"
  GRADLE_OPTS: "-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true"

cache:
  key: ${CI_COMMIT_REF_SLUG}
  paths:
    - .m2/repository/
    - .gradle/caches/
    - .gradle/wrapper/
    - node_modules/
    - .cache/pip/

# Fast feedback jobs
lint:
  stage: validate
  script:
    - ./scripts/lint.sh
  cache:
    policy: pull
  artifacts:
    reports:
      codequality: gl-code-quality-report.json
    expire_in: 1 week

security_scan:
  stage: validate
  script:
    - ./scripts/security-scan.sh
  cache:
    policy: pull
  artifacts:
    reports:
      sast: gl-sast-report.json
    expire_in: 1 week

# Parallel build
build_app:
  stage: build
  script:
    - mvn compile -T 4 -q
  cache:
    policy: push
  artifacts:
    paths:
      - target/classes/
    expire_in: 1 hour

build_docker:
  stage: build
  script:
    - docker build --target builder -t builder:$CI_COMMIT_SHA .
    - docker build --cache-from builder:$CI_COMMIT_SHA -t app:$CI_COMMIT_SHA .
  needs:
    - build_app
  artifacts:
    images:
      - app:$CI_COMMIT_SHA

# Parallel testing
test_unit:
  stage: test
  script:
    - mvn test -Dtest=/*Test.java -DfailIfNoTests=false
  cache:
    policy: pull
  artifacts:
    reports:
      junit: target/surefire-reports/TEST-*.xml
    expire_in: 1 week
  needs:
    - build_app

test_integration:
  stage: test
  script:
    - mvn verify -Pintegration-tests
  services:
    - postgres:13
    - redis:6
  variables:
    POSTGRES_DB: testdb
    POSTGRES_USER: testuser
    POSTGRES_PASSWORD: testpass
  cache:
    policy: pull
  artifacts:
    reports:
      junit: target/failsafe-reports/TEST-*.xml
    expire_in: 1 week
  needs:
    - build_app

test_performance:
  stage: test
  script:
    - ./scripts/performance-test.sh
  cache:
    policy: pull
  artifacts:
    reports:
      performance: performance-results.json
    expire_in: 1 week
  needs:
    - build_docker
  allow_failure: true
```

### Resource optimization
```yaml
# .gitlab-ci.yml - resource optimization
stages:
  - build
  - test
  - deploy

variables:
  KUBERNETES_CPU_REQUEST: "500m"
  KUBERNETES_MEMORY_REQUEST: "1Gi"
  KUBERNETES_CPU_LIMIT: "2000m"
  KUBERNETES_MEMORY_LIMIT: "4Gi"

build:
  stage: build
  image:
    name: maven:3.8.6-openjdk-17
    entrypoint: [""]
  script:
    - mvn clean package -DskipTests
  cache:
    key: ${CI_COMMIT_REF_SLUG}-maven
    paths:
      - .m2/repository
  artifacts:
    paths:
      - target/*.jar
    expire_in: 1 hour

test:
  stage: test
  image:
    name: maven:3.8.6-openjdk-17
    entrypoint: [""]
  services:
    - name: postgres:13
      alias: db
      command: ["postgres", "-c", "shared_preload_libraries=pg_stat_statements"]
  variables:
    POSTGRES_DB: testdb
    POSTGRES_USER: testuser
    POSTGRES_PASSWORD: testpass
    DATABASE_URL: "postgresql://testuser:testpass@db:5432/testdb"
  script:
    - mvn test -Dspring.profiles.active=test
  cache:
    key: ${CI_COMMIT_REF_SLUG}-maven
    policy: pull
  artifacts:
    reports:
      junit: target/surefire-reports/TEST-*.xml
    expire_in: 1 week

deploy:
  stage: deploy
  image: alpine:latest
  before_script:
    - apk add --no-cache curl
  script:
    - ./deploy.sh
  environment:
    name: production
    url: https://myapp.example.com
  only:
    - main
  when: manual
```

## GitOps и Infrastructure as Code

### GitOps pipeline
```yaml
# .gitlab-ci.yml - GitOps pipeline
stages:
  - validate
  - plan
  - apply
  - promote

variables:
  TF_VAR_environment: $CI_COMMIT_REF_NAME

validate:
  stage: validate
  script:
    - cd terraform
    - terraform init
    - terraform validate
    - terraform fmt -check
    - tflint

plan:
  stage: plan
  script:
    - cd terraform
    - terraform plan -out=tfplan
  dependencies:
    - validate
  artifacts:
    paths:
      - terraform/tfplan
    expire_in: 1 hour

apply_staging:
  stage: apply
  script:
    - cd terraform
    - terraform apply tfplan
  dependencies:
    - plan
  environment:
    name: staging
  only:
    - develop
  when: manual

apply_production:
  stage: apply
  script:
    - cd terraform
    - terraform apply tfplan
  dependencies:
    - plan
  environment:
    name: production
  only:
    - main
  when: manual

promote_to_production:
  stage: promote
  script:
    - |
      # Create merge request for production deployment
      curl -X POST \
        -H "PRIVATE-TOKEN: $GITLAB_API_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{
          \"source_branch\": \"staging\",
          \"target_branch\": \"main\",
          \"title\": \"Promote staging to production\",
          \"description\": \"Automated promotion from staging to production\"
        }" \
        "https://gitlab.example.com/api/v4/projects/$CI_PROJECT_ID/merge_requests"
  only:
    - staging
  when: manual
```

### Multi-cluster deployment
```yaml
# .gitlab-ci.yml - multi-cluster deployment
stages:
  - build
  - deploy

variables:
  KUBE_CONFIG_STAGING: $KUBE_CONFIG_STAGING
  KUBE_CONFIG_PRODUCTION: $KUBE_CONFIG_PRODUCTION

build:
  stage: build
  script:
    - docker build -t myapp:$CI_COMMIT_SHA .
    - docker save myapp:$CI_COMMIT_SHA > app.tar
  artifacts:
    paths:
      - app.tar
    expire_in: 1 hour

deploy_staging:
  stage: deploy
  script:
    - export KUBECONFIG=$KUBE_CONFIG_STAGING
    - kubectl config use-context staging-cluster
    - docker load < app.tar
    - helm upgrade --install myapp ./helm -f helm/values-staging.yaml --set image.tag=$CI_COMMIT_SHA
  environment:
    name: staging
    kubernetes:
      namespace: staging
  only:
    - develop

deploy_production:
  stage: deploy
  script:
    - export KUBECONFIG=$KUBE_CONFIG_PRODUCTION
    - kubectl config use-context production-cluster
    - docker load < app.tar
    - helm upgrade --install myapp ./helm -f helm/values-production.yaml --set image.tag=$CI_COMMIT_SHA
    - kubectl rollout status deployment/myapp
  environment:
    name: production
    kubernetes:
      namespace: production
  only:
    - main
  when: manual
```

## Решение проблем

### Common issues
```yaml
# .gitlab-ci.yml - debug pipeline
stages:
  - debug
  - build

debug_info:
  stage: debug
  script:
    - echo "=== GitLab CI Environment Variables ==="
    - env | grep -E "^CI_|GITLAB_" | sort
    - echo "=== System Info ==="
    - uname -a
    - cat /etc/os-release
    - echo "=== Docker Info ==="
    - docker --version
    - docker info
    - echo "=== Runner Info ==="
    - cat /proc/cpuinfo | grep -c processor
    - free -h
    - df -h
  artifacts:
    paths:
      - debug.log
    expire_in: 1 day
  when: manual

# Conditional debugging
debug_on_failure:
  stage: build
  script:
    - echo "This job runs only on failure"
    - ./debug-scripts/failure-analysis.sh
  when: on_failure
  allow_failure: true
```

### Pipeline failure recovery
```yaml
# .gitlab-ci.yml - failure recovery
stages:
  - build
  - test
  - deploy
  - recovery

build:
  stage: build
  script:
    - ./build.sh
  retry:
    max: 2
    when:
      - runner_system_failure
      - stuck_or_timeout_failure

test:
  stage: test
  script:
    - ./test.sh
  retry:
    max: 1
    when:
      - script_failure
  artifacts:
    when: always
    paths:
      - test-results/
    expire_in: 1 week

deploy:
  stage: deploy
  script:
    - ./deploy.sh
  retry:
    max: 0  # No retry for deployment
  environment:
    name: production
    on_stop: rollback

rollback:
  stage: recovery
  script:
    - ./rollback.sh
  environment:
    name: production
    action: stop
  when: manual
  allow_failure: true
```

### Performance debugging
```yaml
# .gitlab-ci.yml - performance monitoring
stages:
  - build
  - test
  - report

variables:
  PIPELINE_START_TIME: ""

before_script:
  - export PIPELINE_START_TIME=$(date +%s)

build:
  stage: build
  script:
    - echo "Build start: $(date)"
    - ./build.sh
    - echo "Build end: $(date)"

test:
  stage: test
  script:
    - echo "Test start: $(date)"
    - ./test.sh
    - echo "Test end: $(date)"
  after_script:
    - |
      # Calculate job duration
      JOB_DURATION=$(( $(date +%s) - PIPELINE_START_TIME ))
      echo "Job duration: ${JOB_DURATION} seconds"

      # Send metrics
      curl -X POST https://monitoring.example.com/api/v1/metrics \
        -H "Content-Type: application/json" \
        -d "{
          \"metric\": \"gitlab_ci_job_duration\",
          \"value\": $JOB_DURATION,
          \"labels\": {
            \"project\": \"$CI_PROJECT_NAME\",
            \"job\": \"$CI_JOB_NAME\",
            \"stage\": \"$CI_JOB_STAGE\"
          }
        }"

performance_report:
  stage: report
  script:
    - |
      # Generate performance report
      TOTAL_DURATION=$(( $(date +%s) - PIPELINE_START_TIME ))
      echo "Total pipeline duration: ${TOTAL_DURATION} seconds" > performance-report.txt
      echo "Jobs executed: $CI_NODE_TOTAL" >> performance-report.txt
      echo "Parallel jobs: $CI_NODE_INDEX" >> performance-report.txt
  artifacts:
    paths:
      - performance-report.txt
    expire_in: 7 days
  when: always
```
## См. также
- [[jenkins|Jenkins]] — альтернативная **CI/CD** платформа
- [[github-actions|GitHub Actions]] — **CI/CD** в **GitHub**
- [[docker-basics|Docker]] — контейнеризация
- [[kubernetes-advanced|Kubernetes]] — оркестрация контейнеров
- [[terraform-basics|Terraform]] — **Infrastructure as Code**
