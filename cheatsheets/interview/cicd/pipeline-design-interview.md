---
title: "Вопросы на собеседовании: Дизайн пайплайнов"
description: "CI/CD pipeline: этапы, Jenkins, GitHub Actions, GitLab CI, Gradle, Docker, тесты, безопасность, оптимизация"
tags:
  - interview
  - cicd
  - pipeline-design-interview
aliases:
  - "Дизайн пайплайнов"
  - "CI/CD pipeline interview"
  - "CI/CD собеседование"
  - "Pipeline design interview"
  - "Jenkins pipeline interview"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: Дизайн пайплайнов

Комплексное руководство по вопросам собеседования на тему дизайна `CI/CD` пайплайнов для Senior Java Developer. Охватывает `Jenkins`, `GitHub Actions`, `GitLab CI`, `Gradle`, `Docker`, стратегии тестирования, безопасность и оптимизацию.

## Полезные ссылки

### Официальная документация

- [Jenkins Pipeline](https://www.jenkins.io/doc/book/pipeline/) — документация по `Jenkins Pipeline`
- [GitLab CI/CD](https://docs.gitlab.com/ee/ci/) — документация `GitLab CI/CD`
- [GitHub Actions](https://docs.github.com/en/actions) — документация `GitHub Actions`
- [Gradle Build Tool](https://docs.gradle.org/current/userguide/userguide.html) — руководство `Gradle`
- [Intro to Jenkins Pipelines (Baeldung)](https://www.baeldung.com/ops/jenkins-pipelines) — обзор `Jenkins Pipeline`
- [CI/CD with Spring Boot (Baeldung)](https://www.baeldung.com/spring-boot-ci-cd) — `CI/CD` для `Spring Boot`
- [Dockerizing Spring Boot (Baeldung)](https://www.baeldung.com/spring-boot-docker-images) — `Docker`-образы `Spring Boot`

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы CI/CD pipeline**
- [Q1. (!) Что такое CI/CD pipeline и из каких этапов он состоит?](#q1--что-такое-cicd-pipeline-и-из-каких-этапов-он-состоит)
- [Q2. (!) Чем отличается CI от CD (continuous delivery vs continuous deployment)?](#q2--чем-отличается-ci-от-cd-continuous-delivery-vs-continuous-deployment)
- [Q3. Как организовать этапы pipeline: последовательно vs параллельно?](#q3-как-организовать-этапы-pipeline-последовательно-vs-параллельно)
- [Q4. (!) Что такое pipeline as code и зачем он нужен?](#q4--что-такое-pipeline-as-code-и-зачем-он-нужен)
- [Q5. (!) Как обеспечить быструю обратную связь в pipeline (fail fast)?](#q5--как-обеспечить-быструю-обратную-связь-в-pipeline-fail-fast)
- [Q6. Что такое quality gates и как их реализовать в pipeline?](#q6-что-такое-quality-gates-и-как-их-реализовать-в-pipeline)

**Jenkins Pipeline**
- [Q7. (!) Чем отличается Declarative Pipeline от Scripted Pipeline в Jenkins?](#q7--чем-отличается-declarative-pipeline-от-scripted-pipeline-в-jenkins)
- [Q8. Что такое Jenkins Shared Libraries и когда их использовать?](#q8-что-такое-jenkins-shared-libraries-и-когда-их-использовать)
- [Q9. Как организовать параллельные стадии в Jenkins Pipeline?](#q9-как-организовать-параллельные-стадии-в-jenkins-pipeline)

**GitHub Actions и GitLab CI**
- [Q10. (!) Как устроен workflow в GitHub Actions?](#q10--как-устроен-workflow-в-github-actions)
- [Q11. Что такое matrix build и когда его применять?](#q11-что-такое-matrix-build-и-когда-его-применять)
- [Q12. Как настроить CI/CD pipeline в GitLab CI?](#q12-как-настроить-cicd-pipeline-в-gitlab-ci)

**Артефакты, версионирование и кэширование**
- [Q13. (!) Что такое артефакты pipeline и как их версионировать?](#q13--что-такое-артефакты-pipeline-и-как-их-версионировать)
- [Q14. Как кэшировать зависимости в pipeline (Gradle, Maven, npm)?](#q14-как-кэшировать-зависимости-в-pipeline-gradle-maven-npm)
- [Q15. Как pipeline интегрируется с артефактным репозиторием (Nexus, Artifactory)?](#q15-как-pipeline-интегрируется-с-артефактным-репозиторием-nexus-artifactory)
- [Q16. Как обеспечить воспроизводимость сборки в pipeline?](#q16-как-обеспечить-воспроизводимость-сборки-в-pipeline)

**Сборка Java-проектов в pipeline**
- [Q17. (!) Как настроить Gradle для CI/CD pipeline?](#q17--как-настроить-gradle-для-cicd-pipeline)
- [Q18. Как использовать Gradle Build Cache в CI?](#q18-как-использовать-gradle-build-cache-в-ci)

**Окружения, approval и ветки**
- [Q19. Как организовать pipeline для нескольких окружений (dev, staging, prod)?](#q19-как-организовать-pipeline-для-нескольких-окружений-dev-staging-prod)
- [Q20. Что такое manual approval и когда его использовать?](#q20-что-такое-manual-approval-и-когда-его-использовать)
- [Q21. (!) Как pipeline связан с ветками Git (trunk-based, GitFlow)?](#q21--как-pipeline-связан-с-ветками-git-trunk-based-gitflow)
- [Q22. Что такое pipeline для pull request (PR pipeline)?](#q22-что-такое-pipeline-для-pull-request-pr-pipeline)

**Тесты в pipeline**
- [Q23. (!) Как организовать тесты в pipeline (unit, integration, e2e)?](#q23--как-организовать-тесты-в-pipeline-unit-integration-e2e)
- [Q24. Что такое smoke test после деплоя и когда его запускать?](#q24-что-такое-smoke-test-после-деплоя-и-когда-его-запускать)
- [Q25. Как запускать интеграционные тесты с Testcontainers в CI?](#q25-как-запускать-интеграционные-тесты-с-testcontainers-в-ci)

**Безопасность pipeline**
- [Q26. (!) Что такое секреты в pipeline и как их хранить?](#q26--что-такое-секреты-в-pipeline-и-как-их-хранить)
- [Q27. Как обеспечить безопасность pipeline (SAST, DAST, сканирование образов)?](#q27-как-обеспечить-безопасность-pipeline-sast-dast-сканирование-образов)

**Docker и контейнеры в pipeline**
- [Q28. (!) Как организовать сборку Docker-образа в pipeline?](#q28--как-организовать-сборку-docker-образа-в-pipeline)
- [Q29. Что такое multi-stage build и как он ускоряет pipeline?](#q29-что-такое-multi-stage-build-и-как-он-ускоряет-pipeline)

**Стратегии деплоя в pipeline**
- [Q30. Как реализовать blue-green и canary деплой в pipeline?](#q30-как-реализовать-blue-green-и-canary-деплой-в-pipeline)
- [Q31. (!) Как организовать откат (rollback) в pipeline?](#q31--как-организовать-откат-rollback-в-pipeline)
- [Q32. Как обеспечить идемпотентность этапов pipeline?](#q32-как-обеспечить-идемпотентность-этапов-pipeline)

**Специализированные pipeline**
- [Q33. Что такое pipeline для монолита vs микросервисов?](#q33-что-такое-pipeline-для-монолита-vs-микросервисов)
- [Q34. Как организовать pipeline для монорепозитория?](#q34-как-организовать-pipeline-для-монорепозитория)
- [Q35. Что такое pipeline для инфраструктуры (IaC, Terraform)?](#q35-что-такое-pipeline-для-инфраструктуры-iac-terraform)

**Мониторинг и оптимизация**
- [Q36. Как мониторить и оптимизировать время выполнения pipeline?](#q36-как-мониторить-и-оптимизировать-время-выполнения-pipeline)

**GitOps pipeline и DORA**
- [Q37. (!) Как выглядит GitOps-pipeline с разделением app и config репозиториев?](#q37--как-выглядит-gitops-pipeline-с-разделением-app-и-config-репозиториев)
- [Q38. Что такое DORA-метрики и как их улучшить через pipeline?](#q38-что-такое-dora-метрики-и-как-их-улучшить-через-pipeline)

---

## Основы `CI/CD` pipeline

## Q1. (!) Что такое `CI/CD` pipeline и из каких этапов он состоит?

`CI/CD pipeline` — автоматизированная цепочка шагов от изменения кода до развёртывания в продакшен. Каждый этап выполняет конкретную задачу и передаёт результат следующему.

**Типичные этапы:**

```mermaid
graph LR
    A[Checkout] --> B[Build]
    B --> C[Unit Tests]
    C --> D[Integration Tests]
    D --> E[Code Analysis]
    E --> F[Build Image]
    F --> G[Push to Registry]
    G --> H[Deploy Dev]
    H --> I[Deploy Staging]
    I --> J[Deploy Prod]
```

1. **`Checkout`** — получение кода из репозитория
2. **`Build`** — компиляция (`Gradle`, `Maven`)
3. **`Unit Tests`** — быстрые юнит-тесты
4. **`Integration Tests`** — тесты с БД, внешними сервисами (см. [интеграционное тестирование](../testing/integration-testing-interview.md))
5. **`Static Analysis`** — линтеры, `SonarQube`, `Checkstyle`
6. **`Build Image`** — сборка `Docker`-образа (см. [Docker](../devops/docker-interview.md))
7. **`Push to Registry`** — публикация образа в `Harbor`, `ECR`, `Nexus`
8. **`Deploy`** — развёртывание в окружения (dev -> staging -> prod)
9. **`Smoke/Health Check`** — проверка работоспособности после деплоя

Дополнительно: security scan (`Trivy`, `OWASP`), performance tests, approval gates.

## Q2. (!) Чем отличается `CI` от `CD` (`continuous delivery` vs `continuous deployment`)?

| Аспект | `CI` | `CD` (Delivery) | `CD` (Deployment) |
|--------|------|-----------------|-------------------|
| Цель | Быстрая обратная связь | Код всегда готов к релизу | Автоматический деплой |
| Деплой в prod | Нет | Ручной / по approval | Автоматический |
| Частота релизов | — | По решению команды | При каждом merge |
| Требования | Тесты, сборка | + staging, quality gates | + полная автоматизация |

**`CI` (`Continuous Integration`)** — при каждом коммите автоматически запускаются сборка и тесты. Цель — быстро выявить поломки и конфликты интеграции.

**`CD` (`Continuous Delivery`)** — код всегда в состоянии, готовом к деплою в prod. Деплой в prod требует ручного подтверждения или запуска.

**`CD` (`Continuous Deployment`)** — деплой в prod полностью автоматический после прохождения всех этапов `pipeline`.

На собеседовании важно объяснить, что `Continuous Delivery` и `Continuous Deployment` — разные практики: в delivery деплой в prod управляемый (approval), в deployment — автоматический. Большинство enterprise-команд используют `Continuous Delivery` с `manual approval` перед prod.

## Q3. Как организовать этапы pipeline: последовательно vs параллельно?

**Последовательное** выполнение — каждый этап стартует после успеха предыдущего. Подходит для зависимых шагов: сборка -> тесты -> образ.

**Параллельное** — независимые этапы выполняются одновременно. Ускоряет `pipeline` в 2-3 раза.

```mermaid
graph TD
    A[Build] --> B[Unit Tests]
    A --> C[Lint / Checkstyle]
    A --> D[SpotBugs]
    B --> E[Integration Tests]
    C --> E
    D --> E
    E --> F[Build Docker Image]
```

**Пример параллельных этапов в `GitHub Actions`:**

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 21 }
      - run: ./gradlew assemble

  unit-tests:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew test

  lint:
    needs: build
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew checkstyleMain

  integration-tests:
    needs: [unit-tests, lint]
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew integrationTest
```

Правило: параллелить всё, что не зависит друг от друга; последовательно — только когда этап требует артефакт предыдущего.

## Q4. (!) Что такое `pipeline as code` и зачем он нужен?

**`Pipeline as code`** — описание `pipeline` в файле внутри репозитория, а не через `UI` `CI`-системы.

| CI-система | Файл |
|------------|------|
| `Jenkins` | `Jenkinsfile` |
| `GitLab CI` | `.gitlab-ci.yml` |
| `GitHub Actions` | `.github/workflows/*.yml` |

**Преимущества:**
- **Версионирование** — изменения `pipeline` проходят `code review` через `PR`
- **Воспроизводимость** — при откате кода откатывается и `pipeline`
- **Аудит** — история изменений в `Git`
- **Единый источник правды** — нет рассинхронизации «код старый, `pipeline` новый»

**Пример `Jenkinsfile` (Declarative):**

```groovy
pipeline {
    agent { docker { image 'eclipse-temurin:21-jdk' } }
    stages {
        stage('Build') {
            steps { sh './gradlew assemble' }
        }
        stage('Test') {
            steps { sh './gradlew test' }
        }
        stage('Docker') {
            steps {
                sh 'docker build -t myapp:${GIT_COMMIT} .'
                sh 'docker push registry.example.com/myapp:${GIT_COMMIT}'
            }
        }
    }
    post {
        always { junit '**/build/test-results/**/*.xml' }
        failure { slackSend channel: '#builds', message: "Build failed: ${env.JOB_NAME}" }
    }
}
```

## Q5. (!) Как обеспечить быструю обратную связь в pipeline (`fail fast`)?

Принципы `fail fast`:

1. **Быстрые этапы первыми** — `lint`, `compile`, `unit tests` (секунды) перед `integration` и `e2e` (минуты)
2. **Остановка при падении** — не тратить ресурсы на следующие этапы при сломанном коде
3. **Параллельность** — независимые проверки одновременно
4. **Кэширование** — зависимости не скачиваются каждый раз
5. **Инкрементальная сборка** — собирать только изменённое

```mermaid
graph LR
    A[Lint<br/>10s] --> B[Compile<br/>30s]
    B --> C[Unit Tests<br/>1m]
    C --> D[Integration Tests<br/>5m]
    D --> E[E2E Tests<br/>15m]
    style A fill:#90EE90
    style B fill:#90EE90
    style C fill:#FFFF99
    style D fill:#FFD700
    style E fill:#FFA500
```

**В `GitHub Actions`:** `fail-fast: true` в матрице отменяет остальные jobs при падении одного. Зависимости между jobs (`needs`) гарантируют, что тяжёлые этапы не запускаются при сломанной сборке.

**Целевые метрики:** `lint` + `compile` < 1 мин; `unit tests` < 3 мин; полный `pipeline` < 15 мин. Если `pipeline` > 30 мин — нужна оптимизация.

## Q6. Что такое `quality gates` и как их реализовать в pipeline?

**`Quality gate`** — набор критериев, которые код должен пройти перед продвижением на следующий этап. Если хотя бы один критерий не выполнен — `pipeline` падает.

**Типичные quality gates:**
- Покрытие тестами >= 80% (`JaCoCo`)
- 0 критических/блокирующих issues в `SonarQube`
- Все `unit`/`integration` тесты зелёные
- Нет критических уязвимостей в зависимостях (`OWASP`)
- `Docker`-образ прошёл сканирование (`Trivy`)

**Пример `Gradle` с `JaCoCo` quality gate:**

```groovy
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80
            }
        }
        rule {
            element = 'CLASS'
            excludes = ['*.config.*', '*.dto.*']
            limit {
                counter = 'LINE'
                minimum = 0.70
            }
        }
    }
}

check.dependsOn jacocoTestCoverageVerification
```

В `SonarQube` quality gate настраивается через UI или `API`; результат проверяется в `pipeline` через `waitForQualityGate()` (в `Jenkins`) или через `sonar-quality-gate-check` action (в `GitHub Actions`).

---

## `Jenkins Pipeline`

## Q7. (!) Чем отличается `Declarative Pipeline` от `Scripted Pipeline` в `Jenkins`?

| Аспект | `Declarative` | `Scripted` |
|--------|--------------|-----------|
| Синтаксис | Структурированный (`pipeline {}`) | Произвольный `Groovy` (`node {}`) |
| Валидация | На этапе парсинга | Только при исполнении |
| Гибкость | Ограниченная, покрывает 90% случаев | Полная — любой `Groovy`-код |
| Обработка ошибок | Блок `post {}` | `try/catch/finally` |
| Blue Ocean UI | Полная поддержка | Частичная |
| Рекомендация | Для большинства проектов | Для сложной логики |

**`Declarative Pipeline`** (рекомендуется `Jenkins`):

```groovy
pipeline {
    agent any
    environment {
        REGISTRY = 'registry.example.com'
    }
    stages {
        stage('Build & Test') {
            steps {
                sh './gradlew clean build'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                    jacoco execPattern: '**/build/jacoco/*.exec'
                }
            }
        }
        stage('SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './gradlew sonar'
                }
                waitForQualityGate abortPipeline: true
            }
        }
        stage('Docker Build & Push') {
            when { branch 'main' }
            steps {
                sh "docker build -t ${REGISTRY}/myapp:${GIT_COMMIT[0..7]} ."
                sh "docker push ${REGISTRY}/myapp:${GIT_COMMIT[0..7]}"
            }
        }
    }
    post {
        failure {
            slackSend channel: '#ci-alerts', message: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
        }
    }
}
```

**`Scripted Pipeline`** — для сложных сценариев с условной логикой:

```groovy
node {
    try {
        stage('Build') { sh './gradlew assemble' }
        stage('Test')  { sh './gradlew test' }

        if (env.BRANCH_NAME == 'main') {
            stage('Deploy') {
                withCredentials([usernamePassword(credentialsId: 'registry', ...)]) {
                    sh 'docker push ...'
                }
            }
        }
    } catch (e) {
        slackSend message: "Build failed: ${e.message}"
        throw e
    } finally {
        junit '**/build/test-results/**/*.xml'
    }
}
```

На собеседовании рекомендуется отвечать, что `Declarative` — предпочтительный выбор для 90% случаев; `Scripted` — когда нужна нетривиальная логика, которую нельзя выразить декларативно.

## Q8. Что такое `Jenkins Shared Libraries` и когда их использовать?

**`Shared Libraries`** — переиспользуемые `Groovy`-библиотеки, подключаемые к `Jenkinsfile` из отдельного `Git`-репозитория. Позволяют вынести общую логику `pipeline` (сборка, деплой, нотификации) и использовать её в десятках проектов.

**Структура:**

```
jenkins-shared-library/
├── vars/
│   ├── buildJavaApp.groovy      # глобальные функции
│   └── deployToK8s.groovy
├── src/
│   └── com/example/pipeline/    # классы Groovy
└── resources/                    # шаблоны, конфиги
```

**Определение (`vars/buildJavaApp.groovy`):**

```groovy
def call(Map config = [:]) {
    pipeline {
        agent { docker { image config.jdkImage ?: 'eclipse-temurin:21-jdk' } }
        stages {
            stage('Build') { steps { sh './gradlew assemble' } }
            stage('Test')  { steps { sh './gradlew test' } }
            stage('Publish') {
                when { branch 'main' }
                steps { sh './gradlew publish' }
            }
        }
    }
}
```

**Использование в `Jenkinsfile`:**

```groovy
@Library('my-shared-lib') _
buildJavaApp(jdkImage: 'eclipse-temurin:21-jdk')
```

**Когда использовать:** более 3-5 проектов с одинаковой структурой `pipeline`; стандартизация процесса CI/CD в организации; вынос секретов и credentials management.

## Q9. Как организовать параллельные стадии в `Jenkins Pipeline`?

Директива `parallel` позволяет выполнять несколько веток одновременно:

```groovy
pipeline {
    agent any
    stages {
        stage('Build') {
            steps { sh './gradlew assemble' }
        }
        stage('Parallel Checks') {
            parallel {
                stage('Unit Tests') {
                    steps { sh './gradlew test' }
                }
                stage('Checkstyle') {
                    steps { sh './gradlew checkstyleMain' }
                }
                stage('SpotBugs') {
                    steps { sh './gradlew spotbugsMain' }
                }
            }
            failFast true  // при падении одного — остановить остальные
        }
        stage('Integration Tests') {
            steps { sh './gradlew integrationTest' }
        }
    }
}
```

`failFast true` останавливает все параллельные ветки при падении любой из них — экономит ресурсы и время. При поддержке нескольких нод `Jenkins` распределяет параллельные стадии по разным агентам.

---

## `GitHub Actions` и `GitLab CI`

## Q10. (!) Как устроен `workflow` в `GitHub Actions`?

`GitHub Actions` `workflow` — это `YAML`-файл в `.github/workflows/`, описывающий автоматизированный процесс.

**Ключевые концепции:**
- **`Workflow`** — весь процесс, запускаемый по триггеру
- **`Job`** — набор шагов, выполняемых на одном runner
- **`Step`** — атомарная единица (команда или action)
- **`Action`** — переиспользуемый компонент (аналог `Jenkins Shared Library`)

**Полный пример для `Java`/`Gradle` проекта:**

```yaml
name: CI/CD Pipeline
on:
  push:
    branches: [main, release/*]
  pull_request:
    branches: [main]

permissions:
  contents: read
  packages: write

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 21
          cache: gradle
      - run: ./gradlew assemble
      - uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: build/libs/*.jar

  test:
    needs: build
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16
        env:
          POSTGRES_DB: testdb
          POSTGRES_PASSWORD: test
        ports: ['5432:5432']
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 21, cache: gradle }
      - run: ./gradlew test integrationTest
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb
      - uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-reports
          path: build/reports/tests/

  docker:
    needs: test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}
      - uses: docker/build-push-action@v5
        with:
          push: true
          tags: ghcr.io/${{ github.repository }}:${{ github.sha }}

  deploy-staging:
    needs: docker
    runs-on: ubuntu-latest
    environment: staging
    steps:
      - run: kubectl set image deployment/myapp myapp=ghcr.io/${{ github.repository }}:${{ github.sha }}

  deploy-prod:
    needs: deploy-staging
    runs-on: ubuntu-latest
    environment:
      name: production
      url: https://myapp.example.com
    steps:
      - run: kubectl set image deployment/myapp myapp=ghcr.io/${{ github.repository }}:${{ github.sha }}
```

## Q11. Что такое `matrix build` и когда его применять?

**`Matrix build`** — запуск одного `workflow` с разными комбинациями параметров (версия `Java`, ОС, БД).

```yaml
jobs:
  test:
    strategy:
      fail-fast: true
      matrix:
        java: [17, 21]
        os: [ubuntu-latest, windows-latest]
    runs-on: ${{ matrix.os }}
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: ${{ matrix.java }}
          cache: gradle
      - run: ./gradlew test
```

**Когда применять:**
- Библиотеки с поддержкой нескольких версий `Java` (17, 21)
- Кроссплатформенные проекты
- Тестирование с разными СУБД (`PostgreSQL`, `MySQL`)

**Ограничения:** каждая комбинация — отдельный job; матрица `2 x 2` = 4 job. Не раздувать без необходимости. `fail-fast: true` останавливает все jobs при падении одного.

## Q12. Как настроить `CI/CD` pipeline в `GitLab CI`?

`GitLab CI` использует файл `.gitlab-ci.yml` в корне репозитория:

```yaml
stages:
  - build
  - test
  - analyze
  - docker
  - deploy

variables:
  GRADLE_OPTS: "-Dorg.gradle.daemon=false"

cache:
  key: ${CI_COMMIT_REF_SLUG}
  paths:
    - .gradle/

build:
  stage: build
  image: eclipse-temurin:21-jdk
  script:
    - ./gradlew assemble
  artifacts:
    paths:
      - build/libs/*.jar
    expire_in: 1 hour

unit-tests:
  stage: test
  image: eclipse-temurin:21-jdk
  script:
    - ./gradlew test
  artifacts:
    reports:
      junit: build/test-results/test/*.xml

integration-tests:
  stage: test
  image: eclipse-temurin:21-jdk
  services:
    - postgres:16
  variables:
    POSTGRES_DB: testdb
    POSTGRES_PASSWORD: test
  script:
    - ./gradlew integrationTest

sonar:
  stage: analyze
  script:
    - ./gradlew sonar -Dsonar.host.url=$SONAR_URL -Dsonar.token=$SONAR_TOKEN
  rules:
    - if: $CI_PIPELINE_SOURCE == "merge_request_event"

docker-build:
  stage: docker
  image: docker:24
  services:
    - docker:24-dind
  script:
    - docker build -t $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA .
    - docker push $CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
  rules:
    - if: $CI_COMMIT_BRANCH == "main"

deploy-prod:
  stage: deploy
  script:
    - kubectl set image deployment/myapp myapp=$CI_REGISTRY_IMAGE:$CI_COMMIT_SHA
  when: manual
  rules:
    - if: $CI_COMMIT_BRANCH == "main"
```

Ключевые отличия `GitLab CI` от `GitHub Actions`: встроенный container registry; `services` для sidecar-контейнеров; `rules` вместо `if`; `when: manual` для ручных этапов; `artifacts:reports:junit` для автоматического отображения результатов тестов в MR.

---

## Артефакты, версионирование и кэширование

## Q13. (!) Что такое артефакты pipeline и как их версионировать?

**Артефакты** — результат сборки (`jar`, `war`, `Docker`-образ), передаваемый между этапами или в registry.

**Стратегии версионирования:**

| Стратегия | Пример тега | Использование |
|-----------|-------------|---------------|
| `Semver` | `1.2.3` | Релизы, библиотеки |
| Git `SHA` | `abc1234` | Dev/staging образы |
| Semver + build | `1.2.3-rc.5` | Release candidate |
| Branch + SHA | `main-abc1234` | Feature-ветки |

**Правила:**
- Один артефакт — одна версия: не перезаписывать уже опубликованный тег
- В prod **никогда** не использовать `latest` — только явная версия
- `jar` публикуется в `Nexus`/`Artifactory`; образ — в container registry
- При откате — деплоить предыдущую версию артефакта, не пересобирать

**Пример `Gradle` publishing:**

```groovy
publishing {
    publications {
        maven(MavenPublication) {
            groupId = 'com.example'
            artifactId = 'myapp'
            version = project.version  // из gradle.properties или git tag
            from components.java
        }
    }
    repositories {
        maven {
            url = uri("https://nexus.example.com/repository/maven-releases/")
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}
```

## Q14. Как кэшировать зависимости в pipeline (`Gradle`, `Maven`, `npm`)?

Кэширование зависимостей ускоряет `pipeline` на 2-5 минут за счёт исключения повторного скачивания.

**`GitHub Actions` (встроенная поддержка `Gradle`):**

```yaml
- uses: actions/setup-java@v4
  with:
    distribution: temurin
    java-version: 21
    cache: gradle   # автоматически кэширует ~/.gradle
```

**`GitHub Actions` (ручной кэш для `Maven`):**

```yaml
- uses: actions/cache@v4
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    restore-keys: |
      ${{ runner.os }}-m2-
```

**`GitLab CI`:**

```yaml
cache:
  key:
    files:
      - build.gradle.kts
      - gradle/wrapper/gradle-wrapper.properties
  paths:
    - .gradle/caches/
    - .gradle/wrapper/
```

**Ключевые правила:**
- Ключ кэша = хэш lock-файла; при изменении зависимостей кэш пересоздаётся
- `restore-keys` — fallback на частичный кэш
- `TTL` кэша: `GitHub Actions` — 7 дней; `GitLab CI` — настраиваемый
- Не кэшировать `build/` — только зависимости

## Q15. Как pipeline интегрируется с артефактным репозиторием (`Nexus`, `Artifactory`)?

Артефактный репозиторий — единое место хранения и версионирования артефактов. `Pipeline` публикует артефакты после успешной сборки и тестов.

**Интеграция `Gradle` с `Nexus`:**

```groovy
// build.gradle.kts
publishing {
    repositories {
        maven {
            val releasesUrl = uri("https://nexus.example.com/repository/maven-releases/")
            val snapshotsUrl = uri("https://nexus.example.com/repository/maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}
```

**В `Jenkinsfile`:**

```groovy
stage('Publish') {
    when { branch 'main' }
    steps {
        withCredentials([usernamePassword(credentialsId: 'nexus-creds',
                usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD')]) {
            sh './gradlew publish'
        }
    }
}
```

**Promotion:** в `Artifactory` артефакт перемещается из `dev` в `release` репозиторий без пересборки. `Nexus` не поддерживает promotion нативно — используют staging-репозитории.

## Q16. Как обеспечить воспроизводимость сборки в pipeline?

**Воспроизводимость** — повторная сборка из того же коммита даёт идентичный артефакт.

**Меры:**
1. **Фиксированные версии зависимостей** — `Gradle` dependency locking, `Maven` с explicit versions, npm `package-lock.json`
2. **Фиксированный образ сборки** — `eclipse-temurin:21.0.2-jdk`, не `latest`
3. **`Gradle Wrapper`** — фиксированная версия `Gradle` в `gradle-wrapper.properties`
4. **Тегирование по коммиту** — трассируемость артефакта к коду

**`Gradle` dependency locking:**

```groovy
// build.gradle.kts
dependencyLocking {
    lockAllConfigurations()
}
```

```bash
# Генерация lock-файла
./gradlew dependencies --write-locks
# Файлы gradle/dependency-locks/*.lockfile коммитить в Git
```

**В `CI`:** использовать `./gradlew build --no-daemon` для стабильности; `Gradle Wrapper` (`./gradlew`) вместо системного `Gradle`; образ сборки с конкретным тегом.

---

## Сборка `Java`-проектов в pipeline

## Q17. (!) Как настроить `Gradle` для `CI/CD` pipeline?

`Gradle` — основной инструмент сборки в современных `Java`-проектах. Правильная настройка для `CI` критична для скорости и надёжности.

**`build.gradle.kts` для CI:**

```kotlin
plugins {
    java
    jacoco
    id("org.sonarqube") version "5.0.0.4638"
    id("com.github.ben-manes.versions") version "0.51.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    jvmArgs("-XX:+UseParallelGC")  // быстрый GC для тестов
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)  // для SonarQube
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit { minimum = "0.80".toBigDecimal() }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
```

**Ключевые флаги `Gradle` для CI:**

```bash
./gradlew build \
  --no-daemon \           # не тратить RAM на daemon
  --parallel \            # параллельная сборка модулей
  --build-cache \         # локальный/удалённый build cache
  -Dorg.gradle.workers.max=4
```

`--no-daemon` рекомендуется в `CI`, потому что daemon не переиспользуется между запусками. `--parallel` ускоряет multi-module проекты. `--build-cache` позволяет переиспользовать результаты задач между сборками.

## Q18. Как использовать `Gradle Build Cache` в `CI`?

`Gradle Build Cache` сохраняет результаты задач (`compile`, `test`) и переиспользует их при повторных сборках, если входные данные не изменились.

**Локальный кэш** (по умолчанию): `~/.gradle/caches/build-cache-*`

**Удалённый кэш** (для `CI`): все агенты сборки используют общее хранилище.

```kotlin
// settings.gradle.kts
buildCache {
    local {
        isEnabled = true
    }
    remote<HttpBuildCache> {
        url = uri("https://gradle-cache.example.com/cache/")
        isPush = System.getenv("CI") != null  // push только из CI
        credentials {
            username = System.getenv("CACHE_USER")
            password = System.getenv("CACHE_PASSWORD")
        }
    }
}
```

**В `GitHub Actions`:**

```yaml
- uses: gradle/actions/setup-gradle@v3
  with:
    cache-read-only: ${{ github.ref != 'refs/heads/main' }}
    # PR-ы только читают кэш; main — читает и пишет
```

Экономия: при изменении одного файла `Gradle` пересобирает только затронутые задачи; остальные берёт из кэша. На крупных проектах экономия — 50-70% времени сборки.

---

## Окружения, approval и ветки

## Q19. Как организовать pipeline для нескольких окружений (dev, staging, prod)?

**Принцип:** один образ собирается один раз и промотируется по окружениям. Меняется только конфигурация.

```mermaid
graph LR
    A[Build & Test] --> B[Build Docker Image<br/>myapp:abc1234]
    B --> C[Deploy Dev<br/>auto]
    C --> D[Integration Tests]
    D --> E[Deploy Staging<br/>auto]
    E --> F[Smoke Tests]
    F --> G[Deploy Prod<br/>manual approval]
```

**Подходы:**
1. **Один pipeline с этапами** — dev автоматически, staging после тестов, prod после approval
2. **`GitOps`** — `ArgoCD`/`Flux` синхронизирует кластер с `Git`-репозиторием; деплой = коммит в `Git` (подробнее в [Kubernetes](../devops/kubernetes-interview.md))

**Конфигурация по окружению:**
- `Kubernetes`: `ConfigMap`/`Secret` per namespace
- `Spring Boot`: `application-{profile}.yml`
- `CI`: environment-specific variables

**В `GitHub Actions`:**

```yaml
deploy-prod:
  needs: deploy-staging
  runs-on: ubuntu-latest
  environment:
    name: production
    url: https://myapp.example.com
  steps:
    - run: |
        helm upgrade myapp ./chart \
          --set image.tag=${{ github.sha }} \
          --values values-prod.yaml
```

`environment: production` с `required_reviewers` автоматически создаёт approval gate.

## Q20. Что такое `manual approval` и когда его использовать?

**`Manual approval`** — ручное подтверждение перехода к следующему этапу (обычно деплой в prod).

| CI-система | Механизм |
|-----------|----------|
| `Jenkins` | `input` step |
| `GitLab CI` | `when: manual` |
| `GitHub Actions` | Environment protection rules |

**Когда использовать:**
- Деплой в prod при `Continuous Delivery`
- Изменения схемы БД
- Инфраструктурные изменения (`Terraform apply` на prod)
- Регуляторные требования (SOX, PCI DSS)

**Когда НЕ использовать:**
- Высокочастотные деплои (10+ в день) — тормозит поток
- При наличии автоматического отката по метрикам (`Argo Rollouts`, `Flagger`)

**В `Jenkins`:**

```groovy
stage('Approve Prod Deploy') {
    steps {
        input message: 'Deploy to production?', submitter: 'tech-lead,devops'
    }
}
```

## Q21. (!) Как pipeline связан с ветками `Git` (`trunk-based`, `GitFlow`)?

```mermaid
graph TD
    subgraph "Trunk-based Development"
        A[main] --> B[short-lived feature branch]
        B -->|PR + CI| A
        A -->|full pipeline| C[Deploy]
    end

    subgraph "GitFlow"
        D[develop] --> E[feature/*]
        E -->|PR| D
        D --> F[release/*]
        F -->|deploy staging| G[Staging]
        F -->|merge| H[main]
        H -->|deploy prod| I[Prod]
    end
```

| Аспект | `Trunk-based` | `GitFlow` |
|--------|--------------|----------|
| Основная ветка | `main` | `develop` + `main` |
| Feature-ветки | Короткоживущие (1-2 дня) | Долгоживущие |
| Pipeline на PR | Сборка + unit тесты | Сборка + unit тесты |
| Pipeline на main | Полный + деплой | На develop — деплой в dev |
| Релизы | По каждому merge в main | Через release-ветку |
| Feature flags | Да, обязательно | Не обязательно |

**`Trunk-based`** упрощает `CI` и частые деплои; подходит для команд с `feature flags` (см. [стратегии деплоя](deployment-strategies-interview.md)). **`GitFlow`** — для проектов со строгим релизным циклом и длинными стабилизационными фазами.

В pipeline условия по ветке определяют набор этапов: `if: github.ref == 'refs/heads/main'` или `rules: if: $CI_COMMIT_BRANCH == "main"` в `GitLab`.

## Q22. Что такое pipeline для `pull request` (`PR pipeline`)?

**`PR pipeline`** — сокращённый `pipeline`, запускаемый при создании/обновлении `pull request`. Цель — проверить, что изменение не ломает сборку до merge.

**Типичный состав:** сборка, `unit` тесты, линтер, `SonarQube` analysis. **Без:** деплоя, тяжёлых e2e, публикации артефактов.

```yaml
# GitHub Actions — PR pipeline
on:
  pull_request:
    branches: [main]

jobs:
  pr-check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 21, cache: gradle }
      - run: ./gradlew build
      - name: SonarQube
        run: ./gradlew sonar
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

Результат отображается в `PR` как status check; merge возможен только при зелёном статусе (branch protection rules). `PR pipeline` должен быть быстрым (< 5 мин) для быстрой обратной связи автору.

---

## Тесты в pipeline

## Q23. (!) Как организовать тесты в pipeline (`unit`, `integration`, `e2e`)?

Тесты организуются по пирамиде тестирования (подробнее в [стратегиях тестирования](../testing/test-strategies-interview.md)):

```mermaid
graph TD
    A[E2E Tests<br/>немного, медленные<br/>5-15 мин] --> B[Integration Tests<br/>средне, умеренные<br/>2-5 мин]
    B --> C[Unit Tests<br/>много, быстрые<br/>< 1 мин]
    style C fill:#90EE90
    style B fill:#FFFF99
    style A fill:#FFA500
```

**Порядок в pipeline:**

1. **`Unit`** — быстрые, в начале; при падении — fail fast
2. **`Integration`** — с БД (`Testcontainers`), после `unit`
3. **`E2E`** — на развёрнутом окружении, после деплоя в staging

**`Gradle` — разделение тестов по source sets:**

```kotlin
// build.gradle.kts
sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

tasks.register<Test>("integrationTest") {
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    useJUnitPlatform()
    shouldRunAfter(tasks.test)
}

tasks.check {
    dependsOn(tasks.named("integrationTest"))
}
```

**В `GitHub Actions`:**

```yaml
jobs:
  unit-tests:
    needs: build
    steps:
      - run: ./gradlew test
  integration-tests:
    needs: build
    steps:
      - run: ./gradlew integrationTest
  e2e:
    needs: [deploy-staging]
    steps:
      - run: ./gradlew e2eTest
```

Для `PR` — только `unit` + lint; полные integration + e2e — на push в main.

## Q24. Что такое `smoke test` после деплоя и когда его запускать?

**`Smoke test`** — минимальный набор проверок сразу после деплоя: приложение поднялось и ключевые эндпоинты отвечают.

**Что проверять:**
- `GET /actuator/health` — 200 и `status: UP`
- `GET /actuator/readiness` — готовность принимать трафик
- Один-два критичных `API` (например, главная страница, авторизация)

**Пример скрипта `smoke test`:**

```bash
#!/bin/bash
set -euo pipefail

BASE_URL="${1:?Usage: smoke-test.sh <base-url>}"
MAX_RETRIES=10
RETRY_DELAY=5

for i in $(seq 1 $MAX_RETRIES); do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health" || true)
  if [ "$STATUS" = "200" ]; then
    echo "Health check passed"
    break
  fi
  echo "Attempt $i/$MAX_RETRIES: status=$STATUS, retrying in ${RETRY_DELAY}s..."
  sleep $RETRY_DELAY
done

[ "$STATUS" != "200" ] && echo "FAILED: health check" && exit 1

# Проверка критичного API
curl -sf "$BASE_URL/api/v1/version" > /dev/null || { echo "FAILED: version endpoint"; exit 1; }
echo "All smoke tests passed"
```

При падении `smoke test` — автоматический rollback (если настроен) или алерт команде. Отличие от полных e2e: `smoke` — 30 секунд; e2e — 15 минут.

## Q25. Как запускать интеграционные тесты с `Testcontainers` в `CI`?

`Testcontainers` поднимает реальные зависимости (`PostgreSQL`, `Kafka`, `Redis`) в `Docker`-контейнерах для тестов. В `CI` требуется доступ к `Docker daemon`.

**Пример теста:**

```java
@SpringBootTest
@Testcontainers
class OrderRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndFindOrder() {
        var order = new Order("item-1", BigDecimal.TEN);
        orderRepository.save(order);
        assertThat(orderRepository.findById(order.getId())).isPresent();
    }
}
```

**В `GitHub Actions`** `Docker` доступен из коробки на `ubuntu-latest`. Для ускорения можно использовать `services` вместо `Testcontainers` (без зависимости от `Docker-in-Docker`).

**В `Jenkins`** нужен agent с `Docker` или `DinD` (Docker-in-Docker). Альтернатива — запуск agent в `Docker` с примонтированным `/var/run/docker.sock`.

Подробнее о `Testcontainers` — в [интеграционном тестировании](../testing/integration-testing-interview.md).

---

## Безопасность pipeline

## Q26. (!) Что такое секреты в pipeline и как их хранить?

Секреты — пароли, токены, ключи для доступа к registry, БД, облаку. **Никогда** не хранить в коде или открытом виде в конфиге `pipeline`.

**Варианты хранения:**

| Подход | Примеры | Плюсы | Минусы |
|--------|---------|-------|--------|
| Встроенные в CI | `GitHub Secrets`, `GitLab CI Variables`, `Jenkins Credentials` | Простота | Ограниченная ротация |
| Внешнее хранилище | `HashiCorp Vault`, AWS `Secrets Manager` | Ротация, аудит, централизация | Сложность настройки |
| `OIDC` federation | `GitHub OIDC` → AWS/GCP | Без долгоживущих секретов | Требует настройки provider |

**Правила:**
- Маскировать секреты в логах (`CI`-системы делают это автоматически при правильной настройке)
- Ограничивать доступ к секретам по окружению и ролям
- Не использовать `echo $SECRET` — значение попадёт в лог
- Ротация: менять секреты регулярно; `Vault` — автоматическая ротация

**В `Jenkins`:**

```groovy
withCredentials([
    usernamePassword(credentialsId: 'nexus', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS'),
    string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')
]) {
    sh './gradlew publish sonar'
}
```

## Q27. Как обеспечить безопасность pipeline (`SAST`, `DAST`, сканирование образов)?

**Security gates в pipeline:**

```mermaid
graph LR
    A[Build] --> B[SAST<br/>SonarQube, SpotBugs]
    B --> C[Dependency Check<br/>OWASP, Snyk]
    C --> D[Build Image]
    D --> E[Image Scan<br/>Trivy, Grype]
    E --> F[DAST<br/>OWASP ZAP]
    F --> G[Deploy]
```

| Тип проверки | Инструмент | Когда |
|-------------|-----------|-------|
| `SAST` | `SonarQube`, `SpotBugs`, `Semgrep` | При сборке |
| Dependency scan | `OWASP Dependency Check`, `Snyk` | При сборке |
| Image scan | `Trivy`, `Grype`, `Clair` | После docker build |
| `DAST` | `OWASP ZAP` | После деплоя в staging |
| Secret scan | `gitleaks`, `trufflehog` | При каждом коммите |

**`Trivy` в `GitHub Actions`:**

```yaml
- name: Scan Docker image
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: myapp:${{ github.sha }}
    format: table
    exit-code: 1
    severity: CRITICAL,HIGH
```

**`OWASP Dependency Check` в `Gradle`:**

```kotlin
plugins {
    id("org.owasp.dependencycheck") version "9.0.9"
}

dependencyCheck {
    failBuildOnCVSS = 7.0f  // fail при CVSS >= 7 (High)
    formats = listOf("HTML", "JSON")
}
```

При критических уязвимостях `pipeline` падает — артефакт не публикуется и не деплоится.

---

## `Docker` и контейнеры в pipeline

## Q28. (!) Как организовать сборку `Docker`-образа в pipeline?

Сборка `Docker`-образа — типичный этап `pipeline` после успешных тестов. Подробнее о `Docker` — в [Docker](../devops/docker-interview.md).

**`Dockerfile` для `Spring Boot` (multi-stage):**

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN ./gradlew dependencies --no-daemon
COPY src/ src/
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN addgroup --system appuser && adduser --system --ingroup appuser appuser
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**В `GitHub Actions`:**

```yaml
- uses: docker/setup-buildx-action@v3
- uses: docker/login-action@v3
  with:
    registry: ghcr.io
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}
- uses: docker/build-push-action@v5
  with:
    context: .
    push: true
    tags: |
      ghcr.io/${{ github.repository }}:${{ github.sha }}
      ghcr.io/${{ github.repository }}:latest
    cache-from: type=gha
    cache-to: type=gha,mode=max
```

**Правила тегирования:**
- Dev/staging: `myapp:main-abc1234` (ветка + SHA)
- Release: `myapp:1.2.3` (semver из git tag)
- Prod: **никогда** `latest` — только конкретная версия

## Q29. Что такое `multi-stage build` и как он ускоряет pipeline?

**`Multi-stage build`** — `Dockerfile` с несколькими `FROM`-инструкциями: одна стадия для сборки, другая для runtime. Итоговый образ содержит только артефакт и `JRE`, без `JDK`, `Gradle`, исходников.

**Преимущества:**
- Размер образа: ~800 MB (`JDK` + `Gradle`) → ~300 MB (только `JRE` + `jar`)
- Безопасность: меньше attack surface
- Кэширование слоёв `Docker`: зависимости меняются редко → слой `COPY gradle/` кэшируется

**Spring Boot layered jar** — дополнительная оптимизация:

```dockerfile
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

# Извлечение слоёв из layered jar
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

Слои `dependencies` и `spring-boot-loader` меняются редко и кэшируются `Docker`; при изменении только кода пересобирается только слой `application` — экономия 2-3 минуты на push. Подробнее — в [Reusing Docker Layers with Spring Boot (Baeldung)](https://www.baeldung.com/docker-layers-spring-boot).

---

## Стратегии деплоя в pipeline

## Q30. Как реализовать `blue-green` и `canary` деплой в pipeline?

Подробно стратегии деплоя описаны в [стратегиях деплоя](deployment-strategies-interview.md). Здесь — как они реализуются в pipeline.

**`Blue-Green`:**

```mermaid
graph LR
    A[Build & Test] --> B[Deploy to Green]
    B --> C[Smoke Test Green]
    C --> D{Passed?}
    D -->|Yes| E[Switch Traffic<br/>Blue → Green]
    D -->|No| F[Rollback]
```

В `Kubernetes` — два `Deployment` (`blue` и `green`); `Service` переключает selector. В pipeline — этап переключения `Service` после smoke test.

**`Canary`:**

```yaml
# Argo Rollouts — canary strategy
apiVersion: argoproj.io/v1alpha1
kind: Rollout
spec:
  strategy:
    canary:
      steps:
        - setWeight: 5       # 5% трафика на новую версию
        - pause: { duration: 5m }
        - setWeight: 25
        - pause: { duration: 10m }
        - setWeight: 75
        - pause: { duration: 10m }
      canaryMetadata:
        labels:
          version: canary
```

Pipeline вызывает `kubectl apply` для `Rollout`; `Argo Rollouts` управляет постепенным переключением трафика и автоматическим откатом при деградации метрик.

## Q31. (!) Как организовать откат (`rollback`) в pipeline?

**Стратегии отката:**

| Подход | Скорость | Надёжность |
|--------|----------|------------|
| `kubectl rollout undo` | Секунды | Высокая |
| Деплой предыдущего образа | 1-2 мин | Высокая |
| Revert commit + pipeline | 5-15 мин | Средняя |
| `Argo Rollouts` auto-rollback | Автоматически | Высокая |

**Автоматический откат в pipeline:**

```groovy
// Jenkinsfile
stage('Deploy & Verify') {
    steps {
        sh 'kubectl apply -f k8s/deployment.yaml'
        sh 'kubectl rollout status deployment/myapp --timeout=300s'
    }
    post {
        failure {
            sh 'kubectl rollout undo deployment/myapp'
            slackSend message: "Deploy failed, rolled back: ${env.BUILD_URL}"
        }
    }
}
```

**В `GitHub Actions`:**

```yaml
- name: Deploy
  run: kubectl set image deployment/myapp myapp=${{ env.IMAGE }}
- name: Wait for rollout
  run: kubectl rollout status deployment/myapp --timeout=5m
- name: Smoke test
  run: ./scripts/smoke-test.sh https://myapp.example.com
- name: Rollback on failure
  if: failure()
  run: kubectl rollout undo deployment/myapp
```

Ключевой принцип: откат должен быть одной командой / одним нажатием кнопки, а не «пересобрать предыдущую версию».

## Q32. Как обеспечить идемпотентность этапов pipeline?

**Идемпотентность** — повторный запуск этапа даёт тот же результат и не ломает состояние.

**Меры:**
1. **Чистая среда** — каждый запуск в новом контейнере / VM (эфемерные runner)
2. **`./gradlew clean build`** — очистка перед сборкой
3. **Версионированные артефакты** — не перезаписывать опубликованный тег
4. **`Kubernetes` деплой** — `kubectl apply` идемпотентен по дизайну
5. **Миграции БД** (`Flyway`) — каждая миграция применяется один раз

**Антипаттерны:**
- Зависимость от глобального состояния между запусками
- `docker tag latest` → перезаписывается при каждом push
- `DROP TABLE IF EXISTS` в миграциях — теряет данные при повторном применении

Перезапуск упавшего `pipeline` не должен ломать окружение. Если этап не идемпотентен (например, отправка email) — защитить guard-условием или вынести в отдельный процесс.

---

## Специализированные pipeline

## Q33. Что такое pipeline для монолита vs микросервисов?

| Аспект | Монолит | Микросервисы |
|--------|---------|-------------|
| Репозиторий | Один | Один per service или monorepo |
| Pipeline | Один | Один per service |
| Сборка | Всего приложения | Только изменённого сервиса |
| Тесты | Все тесты | Тесты сервиса + контрактные |
| Деплой | Весь артефакт | Только изменённый сервис |
| Время | Длиннее (всё вместе) | Короче (один сервис) |
| Координация | Простая | Сложная (версии, зависимости) |

**Монолит:** один `Jenkinsfile` / workflow — сборка, тесты, образ, деплой.

**Микросервисы (repo per service):** каждый сервис — свой `pipeline`. При изменении общей библиотеки — триггер downstream `pipeline` или `Dependabot`/`Renovate` создаёт `PR` в потребителях.

**Микросервисы (monorepo):** path filters определяют, какие сервисы пересобирать (см. Q34). Контрактные тесты (`Spring Cloud Contract`, `Pact`) проверяют совместимость между сервисами в `pipeline`.

## Q34. Как организовать pipeline для монорепозитория?

В монорепо несколько проектов/сервисов в одном репозитории. `Pipeline` должен собирать только изменённое.

**`GitHub Actions` — path filters:**

```yaml
on:
  push:
    paths:
      - 'services/order-service/**'
      - 'libs/common/**'

jobs:
  build-order-service:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: ./gradlew :order-service:build
```

**`GitLab CI` — `rules:changes`:**

```yaml
build-order-service:
  rules:
    - changes:
        - services/order-service/**
        - libs/common/**
  script:
    - ./gradlew :order-service:build
```

**Инструменты для монорепо:**
- `Nx` / `Turborepo` — определение затронутых проектов по графу зависимостей
- `Gradle` multi-project — `./gradlew :affected-module:build`
- `Bazel` — инкрементальная сборка с кэшированием

**Правило:** при изменении shared-библиотеки пересобирать все зависимые сервисы. Граф зависимостей `Gradle` (`./gradlew dependencies`) помогает определить затронутые модули.

## Q35. Что такое pipeline для инфраструктуры (`IaC`, `Terraform`)?

`Pipeline` для `IaC` — применение изменений инфраструктуры через `CI/CD` (подробнее о `Kubernetes` — в [Kubernetes](../devops/kubernetes-interview.md)).

**Этапы:**

```mermaid
graph LR
    A[Checkout] --> B[terraform init]
    B --> C[terraform validate]
    C --> D[terraform plan]
    D --> E[Manual Approval]
    E --> F[terraform apply]
    F --> G[Verify]
```

**В `GitHub Actions`:**

```yaml
jobs:
  plan:
    runs-on: ubuntu-latest
    steps:
      - uses: hashicorp/setup-terraform@v3
      - run: terraform init
      - run: terraform validate
      - run: terraform plan -out=plan.tfplan
      - uses: actions/upload-artifact@v4
        with: { name: plan, path: plan.tfplan }

  apply:
    needs: plan
    runs-on: ubuntu-latest
    environment: production  # requires approval
    steps:
      - uses: actions/download-artifact@v4
        with: { name: plan }
      - run: terraform apply plan.tfplan
```

**Безопасность:** state в удалённом хранилище (`S3` + `DynamoDB` lock); блокировка state при параллельном запуске; секреты облака из `OIDC` federation (без долгоживущих ключей).

---

## Мониторинг и оптимизация

## Q36. Как мониторить и оптимизировать время выполнения pipeline?

**Метрики для мониторинга:**
- Среднее время `pipeline` (lead time)
- Время каждого этапа (bottleneck analysis)
- Процент успешных/неуспешных запусков
- Время ожидания runner (queue time)
- Частота flaky тестов

**Инструменты:**
- `Jenkins` — Pipeline Stage View, Blue Ocean, Prometheus plugin
- `GitHub Actions` — Actions tab, `gh run list --json`
- `GitLab CI` — CI/CD Analytics, Pipeline Charts
- Внешние — `Datadog CI Visibility`, `Grafana` + `Prometheus`

**Оптимизация (по приоритету):**

| Приём | Эффект |
|-------|--------|
| Кэширование зависимостей | -2-5 мин |
| Параллельные этапы | -30-50% времени |
| `Gradle Build Cache` | -50-70% при инкрементальных изменениях |
| `Docker layer caching` | -2-3 мин на сборку образа |
| Более мощные runner | -20-40% (CPU-bound задачи) |
| Выборочный запуск тестов на PR | -5-10 мин |
| Удаление дублирующихся этапов | Зависит от pipeline |

**Целевые показатели:**
- `PR pipeline`: < 5 мин
- Полный `pipeline` (до staging): < 15 мин
- Полный `pipeline` (до prod): < 30 мин

Если `pipeline` систематически превышает эти пороги — анализировать bottleneck (обычно это тесты или сборка `Docker`-образа) и применять соответствующие оптимизации.

---

## GitOps pipeline и DORA

## Q37. (!) Как выглядит GitOps-pipeline с разделением `app` и `config` репозиториев?

**GitOps-pipeline** разделяет CI (сборка и тесты) и CD (деплой через Git). Принцип: `CI` обновляет образ и фиксирует новый тег в отдельном `config`-репозитории; `Argo CD` или `Flux` синхронизирует `config`-репо с кластером.

**Схема взаимодействия:**

```mermaid
sequenceDiagram
    participant Dev as Developer
    participant AppRepo as App Repo
    participant CI as CI Pipeline
    participant Registry as Container Registry
    participant CfgRepo as Config Repo
    participant ArgoCD as Argo CD
    participant K8s as Kubernetes

    Dev->>AppRepo: git push (feature branch)
    AppRepo->>CI: webhook trigger
    CI->>CI: build + test + analysis
    CI->>Registry: docker push myapp:abc123
    CI->>CfgRepo: PR: update image.tag=abc123
    CfgRepo->>CfgRepo: Review + Merge
    ArgoCD->>CfgRepo: poll / webhook
    ArgoCD->>K8s: sync (apply manifests)
    K8s-->>ArgoCD: resource status
```

**Почему два репозитория?**

| Аргумент | Объяснение |
|----------|-----------|
| Разные права | CI-бот пишет в `config`-репо, не имеет доступа к `app`-репо production ветке |
| Аудит деплоев | История `config`-репо = история деплоев с review |
| Rollback | `git revert` в `config`-репо откатывает деплой |
| Drift detection | `Argo CD` видит расхождение кластера с `config`-репо |

**CI-шаг обновления `config`-репо (GitHub Actions):**

```yaml
- name: Update image tag in config repo
  env:
    GH_TOKEN: ${{ secrets.CONFIG_REPO_TOKEN }}
  run: |
    git clone https://github.com/org/gitops-config.git
    cd gitops-config

    # Обновить тег через yq
    yq e ".image.tag = \"${GITHUB_SHA::8}\"" -i \
      apps/myapp/overlays/staging/values.yaml

    git config user.email "ci-bot@example.com"
    git config user.name "CI Bot"
    git add .
    git commit -m "ci: update myapp staging to ${GITHUB_SHA::8}"
    git push
```

**Структура `config`-репо с Kustomize:**

```
gitops-config/
├── apps/
│   └── myapp/
│       ├── base/
│       │   ├── deployment.yaml    # image: myapp (без тега)
│       │   ├── service.yaml
│       │   └── kustomization.yaml
│       └── overlays/
│           ├── dev/
│           │   └── kustomization.yaml  # image.newTag: dev-latest
│           ├── staging/
│           │   └── kustomization.yaml  # image.newTag: abc123
│           └── prod/
│               └── kustomization.yaml  # image.newTag: 1.5.0
└── argocd/
    ├── myapp-dev.yaml     # Application CRD
    ├── myapp-staging.yaml
    └── myapp-prod.yaml
```

**Практика:** `Argo CD` в prod настраивать без `automated.prune` — удаление ресурсов только вручную; `selfHeal: true` — восстанавливать drift (ручные `kubectl` правки). `Flux` как альтернатива — `image automation` умеет сам обновлять тег в `config`-репо без CI-шага.

## Q38. Что такое DORA-метрики и как их улучшить через pipeline?

**DORA metrics** — четыре показателя зрелости DevOps (Google/DORA research):

| Метрика | Что измеряет | Как улучшить через pipeline |
|---------|-------------|----------------------------|
| **Deployment Frequency** | Как часто деплоим в prod | Trunk-based dev, feature flags, маленькие PR |
| **Lead Time for Changes** | Время от коммита до prod | Параллельные этапы, кэш, выборочные тесты |
| **Change Failure Rate** | % деплоев с инцидентом | Canary/blue-green, quality gates, smoke тесты |
| **Time to Restore** | Время восстановления | Автоматический rollback, on-call, Feature flags |

**Уровни зрелости:**

```mermaid
graph LR
    Low["Low\nDeploy freq: 1/месяц\nLead time: > 6 мес\nCFR: > 30%"] -->
    Medium["Medium\n1/неделю\n1 мес – неделя\n< 30%"] -->
    High["High\n1/день\n1 нед – 1 день\n< 15%"] -->
    Elite["Elite 🏆\nМного раз в день\n< 1 часа\n< 5%"]
```

**Конкретные изменения в pipeline для улучшения метрик:**

```yaml
# Lead Time: параллельные этапы (GitHub Actions)
jobs:
  lint:
    runs-on: ubuntu-latest
    steps: [...]         # ← параллельно с unit-tests

  unit-tests:
    runs-on: ubuntu-latest
    steps: [...]         # ← параллельно с lint

  integration-tests:
    needs: [lint, unit-tests]   # ← ждёт оба
    steps: [...]

# Change Failure Rate: canary-деплой с автоматическим анализом
  deploy-canary:
    needs: integration-tests
    steps:
      - run: |
          kubectl argo rollouts set image myapp myapp=image:$TAG
          # Argo Rollouts сам проверит метрики и откатит если нужно
```

**Инструменты измерения:**

```bash
# Four Keys (Google) — open source проект для сбора DORA метрик
# Интегрируется с GitHub, GitLab, Cloud Build

# Через GitHub CLI: собрать deployment frequency
gh run list \
  --workflow=deploy-prod.yml \
  --json createdAt,conclusion \
  --jq '[.[] | select(.conclusion=="success")] | length'
```

**Практика:** начинать с Lead Time — это самая управляемая метрика. Bottleneck обычно: медленные тесты (параллелизм + Gradle Build Cache), долгий ревью (процесс, не pipeline), ручные деплои в staging (автоматизировать). Deployment Frequency растёт при trunk-based development и feature flags — без них малые команды застревают на feature branches неделями.

---

## See also

- [Стратегии деплоя](deployment-strategies-interview.md) — `blue-green`, `canary`, `rolling update`
- [Docker](../devops/docker-interview.md) — контейнеризация, `Dockerfile`, `multi-stage build`
- [Kubernetes](../devops/kubernetes-interview.md) — оркестрация, `Helm`, `ArgoCD`
- [Git](../devops/git-interview.md) — ветвление, `trunk-based`, `GitFlow`
- [Стратегии тестирования](../testing/test-strategies-interview.md) — пирамида тестов, `TDD`
- [Test Automation](../testing/test-automation-interview.md) — автоматизация тестирования в CI/CD
- [Практики code review](../leadership/code-review-practices-interview.md) — quality gates и автоматизация проверок

- [Стратегии деплоя](deployment-strategies-interview.md)
- [AI Agents](../ai-ml/ai-agents-interview.md)
- [Embeddings](../ai-ml/embeddings-interview.md)
- [LLM Basics](../ai-ml/llm-basics-interview.md)
- [LLM Integration Patterns](../ai-ml/llm-integration-patterns-interview.md)
- [MLOps](../ai-ml/mlops-interview.md)
