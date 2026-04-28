---
title: "Обзор инфраструктурных инструментов"
description: "Полное руководство по инструментам для управления инфраструктурой: Terraform, Ansible, Packer, Vagrant, Consul, Nginx — назначение, сценарии использования, базовые примеры, интеграция и Best Practices."
tags:
  - platform
  - infrastructure-tools
  - infrastructure-tools-overview
type: "overview"
difficulty: "intermediate"
aliases:
  - "infrastructure tools overview"
prerequisites: []
next:
  - "[[consul]]"
  - "[[vagrant]]"
updated: "2026-04-20"
---
# Обзор инфраструктурных инструментов

Полное руководство по инструментам для управления инфраструктурой: **Terraform**, **Ansible**, **Packer**, **Vagrant**, **Consul**, **Nginx** — назначение, сценарии использования, базовые примеры, интеграция и **Best Practices**.

## Полезные ссылки

- [Terraform Documentation](https://developer.hashicorp.com/terraform/docs)
- [Ansible Documentation](https://docs.ansible.com/)
- [Packer Documentation](https://developer.hashicorp.com/packer/docs)
- [Vagrant Documentation](https://www.vagrantup.com/docs)
- [Consul Documentation](https://developer.hashicorp.com/consul/docs)
- [Nginx Documentation](https://nginx.org/en/docs/)

## См. также

- [Platform](../../basics/README.md) — **Terraform Advanced**, **Ansible Advanced**, **Nginx Advanced**, **Packer**, **Vagrant**, **Consul**
- [Infrastructure as Code](../iac/iac-overview.md) — основы **Terraform** и **Ansible**
- [CI/CD](../../basics/README.md)

## Содержание

- [Введение](#введение)
- [Terraform — Infrastructure as Code](#terraform-infrastructure-as-code)
  - [Основные концепции](#основные-концепции)
  - [Пример: AWS EC2 и security group](#пример-aws-ec2-и-security-group)
  - [Best Practices для Terraform](#best-practices-для-terraform)
- [Ansible — конфигурационное управление](#ansible-конфигурационное-управление)
  - [Основные концепции](#основные-концепции-1)
  - [Пример playbook: установка Java и приложения](#пример-playbook-установка-java-и-приложения)
  - [Best Practices для Ansible](#best-practices-для-ansible)
- [Packer — создание образов](#packer-создание-образов)
  - [Пример: AMI с Java и приложением](#пример-ami-с-java-и-приложением)
  - [Best Practices для Packer](#best-practices-для-packer)
- [Vagrant — окружения для разработки](#vagrant-окружения-для-разработки)
  - [Пример Vagrantfile](#пример-vagrantfile)
- [Consul — Service Discovery и конфигурация](#consul-service-discovery-и-конфигурация)
  - [Основные возможности](#основные-возможности)
- [Nginx — веб-сервер и reverse proxy](#nginx-веб-сервер-и-reverse-proxy)
  - [Пример конфигурации: proxy к приложению и балансировка](#пример-конфигурации-proxy-к-приложению-и-балансировка)
- [Сравнение и комбинирование инструментов](#сравнение-и-комбинирование-инструментов)
- [Интеграция с CI/CD](#интеграция-с-cicd)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Лучшие практики](#лучшие-практики)
- [Шпаргалка команд](#шпаргалка-команд)

## Введение

Инфраструктурные инструменты решают задачи: описание инфраструктуры как код (IaC), конфигурация серверов, создание образов, локальные окружения для разработки, **service discovery** и балансировка. Выбор зависит от облака (AWS, `Azure`, `GCP`, on-premise) и от роли: разработчик, **DevOps**, **SRE**.

| Инструмент | Назначение | Когда использовать |
|------------|------------|---------------------|
| **Terraform** | **Provisioning** инфраструктуры (сети, ВМ, БД) | Создание и изменение облачных ресурсов декларативно |
| **Ansible** | **Configuration Management**, развёртывание приложений | Настройка ОС, установка пакетов, конфиги, без агента |
| **Packer** | Создание образов (AMI, `Vagrant box`, Docker) | Единый образ для всех окружений, **immutable infrastructure** |
| **Vagrant** | Локальные виртуальные машины для разработки | Окружение «как в проде» на рабочей станции |
| **Consul** | **Service Discovery**, конфигурация, **health checks** | Микросервисы, динамическая конфигурация |
| **Nginx** | Веб-сервер, **reverse proxy**, **load balancer** | Раздача статики, прокси к приложениям, **SSL**, балансировка |


## Terraform — Infrastructure as Code

**Terraform** описывает инфраструктуру в **HCL**-файлах (.tf). Провайдеры (AWS, Azure, GCP, Kubernetes и др.) создают и обновляют ресурсы. Состояние хранится в **state**-файле (локально или в удалённом backend — S3, Terraform Cloud).

### Основные концепции

- **Resource** — единица инфраструктуры (instance, security group, subnet).
- **Provider** — плагин для облака или системы (aws, azurerm, google, kubernetes).
- **State** — текущее состояние ресурсов; используется для планирования изменений.
- **Plan / Apply** — план изменений и их применение.

### Пример: AWS EC2 и security group

Пример **Terraform** (main.tf): провайдер **AWS**, **security group** для приложения, правила **ingress**/**egress**.

```hcl
# main.tf
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

resource "aws_security_group" "app" {
  name        = "app-sg"
  description = "Security group for application"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.admin_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "app" {
  ami           = var.ami_id
  instance_type = var.instance_type
  subnet_id     = var.subnet_id
  security_groups = [aws_security_group.app.id]

  tags = {
    Name = "app-server"
  }
}
```

Команды: `terraform init`, `terraform plan`, `terraform apply`, `terraform destroy`. **State** в **production** хранить в удалённом **backend** с блокировкой (S3 + DynamoDB, Terraform Cloud).

### Best Practices для Terraform

- Один **state** на логический кусок инфраструктуры; не один гигантский **state** на всё.
- Использовать модули для повторяемых фрагментов (сеть, кластер приложений).
- Переменные и выходы выносить в **variables.tf** и **outputs.tf**; секреты — через переменные окружения или **vault**, не в коде.
- Регулярно выполнять `terraform plan` в `CI` и применять через контролируемый процесс (MR, pipeline).

Подробнее: [Terraform Advanced](../iac/terraform/terraform-advanced.md), [terraform](../iac/terraform/terraform.md).


## Ansible — конфигурационное управление

**Ansible** управляет конфигурацией серверов без установки агента: подключение по **SSH**, выполнение модулей (package, copy, template, service и др.). Инвентарь (inventory) — список хостов и групп; плейбуки (playbooks) — сценарии задач.

### Основные концепции

- **Inventory** — хосты и группы (ini или yaml).
- **Playbook** — список **plays**: хосты, роли, задачи.
- **Role** — переиспользуемый набор задач, шаблонов, файлов.
- **Module** — атомарное действие (yum, apt, copy, template, systemd).

### Пример playbook: установка Java и приложения

```yaml
# playbook.yml
---
- name: Configure app servers
  hosts: app_servers
  become: yes
  vars:
    java_version: "17"
    app_jar: "/opt/app/application.jar"

  tasks:
    - name: Install OpenJDK
      ansible.builtin.package:
        name: "java-{{ java_version }}-openjdk-headless"
        state: present

    - name: Create app directory
      ansible.builtin.file:
        path: /opt/app
        state: directory
        mode: "0755"

    - name: Copy application JAR
      ansible.builtin.copy:
        src: "{{ playbook_dir }}/files/application.jar"
        dest: "{{ app_jar }}"
        mode: "0644"

    - name: Create systemd unit
      ansible.builtin.template:
        src: app.service.j2
        dest: /etc/systemd/system/app.service
      notify: restart app

    - name: Enable and start app
      ansible.builtin.systemd:
        name: app
        state: started
        enabled: yes
        daemon_reload: yes

  handlers:
    - name: restart app
      ansible.builtin.systemd:
        name: app
        state: restarted
```

Запуск: `ansible-playbook -i inventory playbook.yml`. Для секретов использовать **Ansible Vault** или внешний **vault**.

### Best Practices для Ansible

- Структурировать через **roles**; не писать один огромный **playbook**.
- Идемпотентность: задачи должны безопасно повторяться (модули это обеспечивают при корректном использовании).
- Переменные по приоритету: **defaults** в **role**, затем **group_vars**/**host_vars**, затем командная строка.
- Теги для выборочного запуска частей **playbook**.

Подробнее: [Ansible Advanced](../iac/ansible/ansible-advanced.md), [ansible](../iac/ansible/ansible.md).


## Packer — создание образов

**Packer** создаёт образы ВМ (AMI, Azure Image, GCP image, Vagrant box, Docker image) из одного описания. **Builder** определяет тип образа; **provisioners** (shell, Ansible и др.) настраивают ОС внутри образа.

### Пример: AMI с Java и приложением

```hcl
# packer.pkr.hcl
source "amazon-ebs" "app" {
  ami_name      = "app-{{timestamp}}"
  instance_type = "t3.micro"
  region        = var.aws_region
  source_ami_filter {
    filters = {
      name                = "amzn2-ami-hvm-*-x86_64-gp2"
      root-device-type    = "ebs"
      virtualization-type = "hvm"
    }
    owners      = ["amazon"]
    most_recent = true
  }
  ssh_username = "ec2-user"
}

build {
  sources = ["source.amazon-ebs.app"]

  provisioner "shell" {
    inline = [
      "sudo yum update -y",
      "sudo yum install -y java-17-amazon-corretto-headless"
    ]
  }

  provisioner "file" {
    source      = "application.jar"
    destination = "/tmp/application.jar"
  }

  provisioner "shell" {
    inline = [
      "sudo mv /tmp/application.jar /opt/app/application.jar",
      "sudo chmod 644 /opt/app/application.jar"
    ]
  }
}
```

Команды: `packer init .`, `packer build .`. Образы можно тегировать и распространять по окружениям.

### Best Practices для Packer

- Минимизировать образ: только необходимое ПО; обновления безопасности применять в момент сборки.
- Использовать **Ansible provisioner** для сложной настройки вместо длинных **shell**-скриптов.
- Хранить описание в репозитории; сборку запускать из `CI` (при коммите в main или по расписанию).

Подробнее: [packer](../iac/packer/packer.md).


## Vagrant — окружения для разработки

**Vagrant** поднимает виртуальные машины по описанию (Vagrantfile). Провайдеры: **VirtualBox**, **VMware**, **Hyper-V**, **libvirt**; **provisioners**: **shell**, **Ansible**, **Chef**. Удобно для локальной разработки «как в проде».

### Пример Vagrantfile

```ruby
Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/jammy64"
  config.vm.network "private_network", ip: "192.168.56.10"
  config.vm.provider "virtualbox" do |vb|
    vb.memory = "2048"
    vb.cpus = 2
  end
  config.vm.provision "shell", path: "bootstrap.sh"
  # или config.vm.provision "ansible" do |ansible|
  #   ansible.playbook = "playbook.yml"
  # end
end
```

Команды: `vagrant up`, `vagrant ssh`, `vagrant halt`, `vagrant destroy`. **Box** можно собрать через **Packer**.

Подробнее: [vagrant](vagrant.md).


## Consul — Service Discovery и конфигурация

**Consul** (HashiCorp) даёт: **service discovery** (регистрация сервисов и DNS / HTTP lookup), **health checks**, `KV` **store** для конфигурации. Режимы: **server** и **client**; клиенты приложений обращаются к локальному **agent**.

### Основные возможности

- **Service registration** — регистрация сервиса с адресом и портом.
- **Health checks** — **HTTP**, **TCP**, **script**; нездоровые экземпляры исключаются из ответов.
- **KV store** — ключ-значение для конфигурации; можно подписаться на изменения.
- **Multi-datacenter** — репликация между ЦОД.

Интеграция с приложением: через **Consul client API** или **sidecar** (например, Envoy). **Spring Cloud Consul** предоставляет интеграцию для **Java**.

Подробнее: [consul](consul.md).


## Nginx — веб-сервер и reverse proxy

**Nginx** используется как: статический файловый сервер, **reverse proxy** к приложениям (Java, `Node`, Go), **SSL**-терминация, **load balancer** (round-robin, `least_connections`, ip_hash).

### Пример конфигурации: proxy к приложению и балансировка

```nginx
upstream app_backend {
    least_conn;
    server 127.0.0.1:8080 weight=1;
    server 127.0.0.1:8081 weight=1;
    keepalive 32;
}

server {
    listen 80;
    server_name example.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name example.com;
    ssl_certificate     /etc/ssl/certs/example.crt;
    ssl_certificate_key /etc/ssl/private/example.key;

    location / {
        proxy_pass http://app_backend;
        proxy_http_version 1.1;
        proxy_set_header Connection "";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /health {
        access_log off;
        proxy_pass http://app_backend;
        proxy_connect_timeout 1s;
        proxy_read_timeout 1s;
    }
}
```

Подробнее: [Nginx Advanced](nginx-advanced.md).


## Сравнение и комбинирование инструментов

| Задача | Инструмент | Комментарий |
|--------|------------|-------------|
| Создать **VPC**, подсети, ВМ в облаке | **Terraform** | Декларативно, **state** в **backend** |
| Настроить ОС и установить приложение на ВМ | **Ansible** | После **Terraform** или внутри **Packer** |
| Собрать образ ВМ для всех окружений | **Packer** | **Provisioner** — **Ansible** или **shell** |
| Локальная среда для разработчика | **Vagrant** | **Provisioner** — **Ansible**, образ — **Packer** |
| Регистрация сервисов и конфиг | **Consul** | В микросервисной архитектуре |
| Прокси и балансировка перед приложением | **Nginx** | Перед одним или несколькими инстансами |

Типичная связка: **Terraform** создаёт инфраструктуру и ВМ; образы для ВМ собирает **Packer** с **Ansible** внутри; конфигурацию уже запущенных хостов при необходимости донастраивает **Ansible**; перед приложением ставится **Nginx**; в микросервисах добавляется **Consul**.


## Интеграция с CI/CD

- **Terraform:** в пайплайне — `terraform init`, `terraform plan` (артефакт плана), ручное или автоматическое `apply` на нужное окружение; **state** в удалённом **backend** с блокировкой.
- **Ansible:** запуск **playbook** из пайпа (ansible-playbook); инвентарь — динамический (из `Terraform output` или облачного API) или статический.
- **Packer:** сборка образов по коммиту в **main** или по тегу; образы тегируются и используются в **Terraform** (ami_id, image_id).
- **Vagrant:** обычно локально; при необходимости образы для **Vagrant** собираются через **Packer** и выкладываются в репозиторий **box**.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Лучшие практики

1. **Infrastructure as `Code`:** вся инфраструктура в репозитории; изменения только через код и код-ревью.
2. **State и секреты: Terraform state** — в удалённом **backend** с блокировкой; секреты не в коде (vault, переменные окружения, секреты CI).
3. **Идемпотентность: Ansible playbooks** и **Terraform** конфигурации рассчитаны на повторный запуск без ручного вмешательства.
4. **Модульность: Terraform** модули, **Ansible roles**, переиспользуемые **Packer** шаблоны.
5. **Версионирование образов:** образы **Packer** с тегами (дата, коммит); в **Terraform** использовать конкретные образы, обновление по контролируемому процессу.
6. **Документация: README** в репозитории инфраструктуры: назначение модулей, как запускать, зависимости (облако, образы).
7. **Безопасность:** минимальные привилегии для сервисных учёток **Terraform**/**Ansible**; аудит доступа к **state** и секретам.

## Шпаргалка команд

| Инструмент | Типовые команды |
|------------|------------------|
| **Terraform** | `terraform init`, `terraform plan`, `terraform apply`, `terraform destroy`, `terraform state list` |
| **Ansible** | `ansible-playbook playbook.yml`, `ansible all -m ping`, `ansible-inventory --list` |
| **Packer** | `packer init`, `packer build template.pkr.hcl` |
| **Vagrant** | `vagrant up`, `vagrant halt`, `vagrant ssh`, `vagrant destroy` |
| **Consul** | `consul agent -dev`, `consul catalog services`, `consul kv get key` |
| **Nginx** | `nginx -t` (проверка конфига), `nginx -s reload` |

Документ в совокупности с разделами [Platform](../../basics/README.md), [IaC](../iac/iac-overview.md), [CI/CD](../../basics/README.md) и практик эксплуатации.
