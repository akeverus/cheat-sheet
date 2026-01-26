# DevOps и Инфраструктура (DevOps)

**Комплексные руководства по DevOps: контейнеры, CI/CD, облачные провайдеры, Infrastructure as Code**

## 📋 Описание

Этот раздел содержит детальные руководства по DevOps практикам: Docker, Kubernetes, CI/CD pipelines, облачные провайдеры (AWS, Azure, GCP), Infrastructure as Code (Terraform, Ansible, Pulumi).

## 📚 Структура раздела

### 🐳 [Containers](containers/)
**Контейнеризация**

#### [Docker](containers/docker/)
- **[Docker Basics](containers/docker/docker-basics.md)** - Основы Docker
- **[Docker Spring Boot](containers/docker/docker-spring-boot.md)** - Docker + Spring Boot

#### [Kubernetes](containers/kubernetes/)
- **Kubernetes Basics** - Основы Kubernetes
- **Kubernetes Advanced** - Продвинутые темы

### 🔄 [CI/CD](ci-cd/)
**Continuous Integration / Continuous Deployment**

#### [GitHub Actions](ci-cd/github-actions/)
- **[GitHub Actions](ci-cd/github-actions/)** - GitHub Actions

#### [Jenkins](ci-cd/jenkins/)
- **[Jenkins](ci-cd/jenkins/)** - Jenkins

#### [GitLab CI](ci-cd/gitlab-ci/)
- **[GitLab CI](ci-cd/gitlab-ci/)** - GitLab CI/CD

#### [Other CI/CD](ci-cd/)
- **[CircleCI](ci-cd/circle-ci/)** - CircleCI
- **[Azure DevOps](ci-cd/azure-devops/)** - Azure DevOps
- **[Tekton](ci-cd/tekton/)** - Tekton

### ☁️ [Cloud Providers](cloud-providers/)
**Облачные провайдеры**

#### [AWS](cloud-providers/aws/)
- **AWS Basics** - Основы AWS
- **AWS Services** - AWS сервисы

#### [Azure](cloud-providers/azure/)
- **Azure Basics** - Основы Azure

#### [GCP](cloud-providers/gcp/)
- **GCP Basics** - Основы GCP

### 🏗️ [Infrastructure as Code](infrastructure-as-code/)
**Infrastructure as Code**

#### [Terraform](infrastructure-as-code/terraform/)
- **Terraform Basics** - Основы Terraform

#### [Ansible](infrastructure-as-code/ansible/)
- **Ansible Basics** - Основы Ansible

#### [Pulumi](infrastructure-as-code/pulumi/)
- **Pulumi Basics** - Основы Pulumi

#### [Packer](infrastructure-as-code/packer/)
- **Packer Basics** - Основы Packer

### 📨 [Messaging](messaging/)
**Messaging системы**

- **Messaging Basics** - Основы messaging

### 📊 [Monitoring & Logging](monitoring-logging/)
**Мониторинг и логирование**

- **Monitoring & Logging** - Мониторинг и логирование

## 🎯 Для кого этот раздел

### DevOps Engineers
- **Контейнеризация** - Docker, Kubernetes
- **CI/CD** - настройка pipelines
- **Infrastructure as Code** - Terraform, Ansible
- **Cloud** - AWS, Azure, GCP

### Developers
- **Docker для разработки** - контейнеризация приложений
- **CI/CD интеграция** - автоматизация деплоя
- **Cloud basics** - основы облачных платформ

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. Containers
   ├── Docker Basics
   └── Docker Compose

2. CI/CD
   ├── GitHub Actions
   └── GitLab CI

3. Cloud Basics
   └── AWS / Azure / GCP Basics
```

### Для опытных
```
1. Kubernetes
   ├── Kubernetes Basics
   ├── Kubernetes Advanced
   └── Kubernetes Networking

2. Infrastructure as Code
   ├── Terraform
   ├── Ansible
   └── Pulumi

3. Advanced DevOps
   ├── Service Mesh
   ├── GitOps
   └── Cloud Native
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Monitoring](../monitoring/)** - Мониторинг и observability
- **[Architecture](../architecture/)** - Cloud architecture
- **[Interview](../interview/devops/)** - Вопросы на собеседовании
- **[Security](../security/)** - Infrastructure security

### Специфичные связи
- **Docker** → [Interview](../interview/devops/docker-interview.md)
- **Kubernetes** → [Interview](../interview/devops/kubernetes-interview.md)
- **Terraform** → [Infrastructure](../infrastructure/terraform-advanced.md)

## 📚 Полезные ресурсы

### Официальная документация
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Terraform Documentation](https://www.terraform.io/docs)
- [AWS Documentation](https://docs.aws.amazon.com/)

### Учебные материалы
- [Kubernetes Tutorial](https://kubernetes.io/docs/tutorials/)
- [Terraform Learn](https://learn.hashicorp.com/terraform)

## 🎯 Следующие шаги

После изучения DevOps:
1. **Изучите мониторинг** → [Monitoring](../monitoring/)
2. **Освойте безопасность** → [Security](../security/)
3. **Подготовьтесь к интервью** → [Interview](../interview/devops/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../messaging/)

*Обновлено: 2026-01-25*
