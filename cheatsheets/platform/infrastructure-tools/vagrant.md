---
title: "Vagrant"
description: "Vagrant - это инструмент с открытым исходным кодом для создания и управления виртуальными средами разработки. Он позволяет описывать инфраструктуру в коде и автоматически создавать, настраивать и уничтожать виртуальные машины. Этот документ охватывает продвинутые паттерны использ"
tags: ["platform", "infrastructure-tools", "vagrant"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Vagrant**

**Vagrant** - это инструмент с открытым исходным кодом для создания и управления виртуальными средами разработки. Он позволяет описывать инфраструктуру в коде и автоматически создавать, настраивать и уничтожать виртуальные машины. Этот документ охватывает продвинутые паттерны использования **Vagrant** для **enterprise** разработки и тестирования.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки
- [Vagrant Documentation](https://www.vagrantup.com/docs)
- [Vagrant Boxes](https://www.vagrantup.com/docs/boxes)
- [Vagrant Plugins](https://www.vagrantup.com/docs/plugins)
- [VirtualBox Documentation](https://www.virtualbox.org/wiki/Documentation)
- [Ansible Documentation](https://docs.ansible.com/)

## Содержание

- [Основы Vagrant](#основы-vagrant)
  - [Установка и настройка](#установка-и-настройка)
  - [Структура проекта](#структура-проекта)
  - [Основной Vagrantfile](#основной-vagrantfile)
- [Продвинутые конфигурации](#продвинутые-конфигурации)
  - [Multi-machine setup](#multi-machine-setup)
  - [Dynamic inventory с переменными](#dynamic-inventory-с-переменными)
- [Provisioners](#provisioners)
  - [Shell provisioner](#shell-provisioner)
  - [Ansible provisioner](#ansible-provisioner)
  - [Puppet provisioner](#puppet-provisioner)
  - [Docker provisioner](#docker-provisioner)
- [Providers](#providers)
  - [VirtualBox provider](#virtualbox-provider)
  - [VMware provider](#vmware-provider)
  - [AWS provider](#aws-provider)
- [Plugins](#plugins)
  - [Полезные плагины](#полезные-плагины)
  - [Кастомный плагин](#кастомный-плагин)
- [Enterprise patterns](#enterprise-patterns)
  - [Multi-environment setup](#multi-environment-setup)
  - [Team development workflow](#team-development-workflow)
- [CI/CD интеграция](#cicd-интеграция)
  - [Jenkins pipeline](#jenkins-pipeline)
  - [GitHub Actions](#github-actions)
- [Security и compliance](#security-и-compliance)
  - [Security hardening](#security-hardening)
  - [Compliance automation](#compliance-automation)
- [Performance optimization](#performance-optimization)
  - [Resource management](#resource-management)
  - [Caching и optimization](#caching-и-optimization)
- [Решение проблем](#решение-проблем)
  - [Common issues](#common-issues)
  - [Performance troubleshooting](#performance-troubleshooting)
  - [Recovery procedures](#recovery-procedures)
- [Лучшие практики](#лучшие-практики)
  - [Project structure](#project-structure)
  - [Makefile automation](#makefile-automation)
  - [Version control](#version-control)
  - [Documentation](#documentation)
- [См. также](#см-также)

## Основы **Vagrant**

### Установка и настройка
Ниже — установка **Vagrant** на **Linux**/**macOS** (**bash**).
```bash
# Linux/macOS
# Установка через пакетный менеджер
sudo apt-get update && sudo apt-get install -y vagrant

# macOS с Homebrew
brew install vagrant

# Или скачивание с официального сайта
wget https://releases.hashicorp.com/vagrant/2.3.0/vagrant_2.3.0_linux_amd64.zip
unzip vagrant_2.3.0_linux_amd64.zip
sudo mv vagrant /usr/local/bin/

# Проверка установки
vagrant version

# Установка VirtualBox (основной провайдер)
sudo apt-get install -y virtualbox

# Установка VMware Workstation (альтернативный провайдер)
# Скачать и установить с официального сайта

# Установка дополнительных плагинов
vagrant plugin install vagrant-vbguest
vagrant plugin install vagrant-hostmanager
vagrant plugin install vagrant-cachier
```

### Структура проекта
```text
vagrant-project/
├── Vagrantfile              # Основной конфигурационный файл
├── .vagrant/                # Автоматически созданная директория
├── scripts/                 # Provisioning скрипты
│   ├── bootstrap.sh
│   ├── install-app.sh
│   └── configure.sh
├── ansible/                 # Ansible плейбуки
│   ├── playbook.yml
│   └── roles/
├── puppet/                  # Puppet манифесты
├── chef/                    # Chef рецепты
├── docker/                  # Docker конфигурации
├── config/                  # Дополнительные конфигурации
└── README.md
```

### Основной **Vagrantfile**
```ruby
# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  # Базовая конфигурация
  config.vm.box = "ubuntu/focal64"
  config.vm.box_version = "20211001.0.0"
  config.vm.hostname = "dev-machine"

  # Сетевые настройки
  config.vm.network "private_network", ip: "192.168.56.10"
  config.vm.network "forwarded_port", guest: 8080, host: 8080
  config.vm.network "forwarded_port", guest: 3306, host: 3306

  # Настройки виртуальной машины
  config.vm.provider "virtualbox" do |vb|
    vb.memory = "2048"
    vb.cpus = 2
    vb.name = "dev-machine"
    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    vb.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  end

  # Папки для синхронизации
  config.vm.synced_folder ".", "/vagrant", type: "nfs"
  config.vm.synced_folder "~/projects", "/home/vagrant/projects"

  # Provisioning
  config.vm.provision "shell", path: "scripts/bootstrap.sh"
  config.vm.provision "ansible_local" do |ansible|
    ansible.playbook = "ansible/playbook.yml"
  end

  # Плагины
  config.hostmanager.enabled = true
  config.hostmanager.manage_host = true
  config.hostmanager.manage_guest = true

  config.cache.scope = :box if Vagrant.has_plugin?("vagrant-cachier")
end
```

## Продвинутые конфигурации

### Multi-machine setup
```ruby
Vagrant.configure("2") do |config|
  # Общие настройки для всех машин
  config.vm.box = "ubuntu/focal64"
  config.vm.box_check_update = false

  # Настройки для веб-сервера
  config.vm.define "web" do |web|
    web.vm.hostname = "web-server"
    web.vm.network "private_network", ip: "192.168.56.11"

    web.vm.provider "virtualbox" do |vb|
      vb.memory = "1024"
      vb.cpus = 1
      vb.name = "web-server"
    end

    web.vm.provision "shell", inline: <<-SHELL
      sudo apt-get update
      sudo apt-get install -y nginx
      sudo systemctl enable nginx
      sudo systemctl start nginx
    SHELL
  end

  # Настройки для базы данных
  config.vm.define "db" do |db|
    db.vm.hostname = "db-server"
    db.vm.network "private_network", ip: "192.168.56.12"

    db.vm.provider "virtualbox" do |vb|
      vb.memory = "2048"
      vb.cpus = 2
      vb.name = "db-server"
    end

    db.vm.provision "shell", inline: <<-SHELL
      sudo apt-get update
      sudo apt-get install -y postgresql postgresql-contrib
      sudo systemctl enable postgresql
      sudo systemctl start postgresql
    SHELL
  end

  # Настройки для приложения
  config.vm.define "app", primary: true do |app|
    app.vm.hostname = "app-server"
    app.vm.network "private_network", ip: "192.168.56.13"

    app.vm.provider "virtualbox" do |vb|
      vb.memory = "4096"
      vb.cpus = 4
      vb.name = "app-server"
    end

    app.vm.provision "shell", inline: <<-SHELL
      sudo apt-get update
      sudo apt-get install -y openjdk-11-jdk maven
      # Копирование кода приложения
      cp -r /vagrant/app /home/vagrant/
      cd /home/vagrant/app && mvn clean package
    SHELL
  end
end
```

### Dynamic inventory с переменными
```ruby
# variables.rb
VAGRANT_ENV = {
  "boxes" => {
    "web" => {
      "box" => "ubuntu/focal64",
      "ip" => "192.168.56.11",
      "memory" => 1024,
      "cpus" => 1,
      "ports" => [
        {"guest" => 80, "host" => 8080},
        {"guest" => 443, "host" => 8443}
      ],
      "provision" => "web"
    },
    "db" => {
      "box" => "centos/8",
      "ip" => "192.168.56.12",
      "memory" => 2048,
      "cpus" => 2,
      "ports" => [
        {"guest" => 3306, "host" => 3306}
      ],
      "provision" => "db"
    },
    "cache" => {
      "box" => "debian/bullseye64",
      "ip" => "192.168.56.13",
      "memory" => 1024,
      "cpus" => 1,
      "ports" => [
        {"guest" => 6379, "host" => 6379}
      ],
      "provision" => "cache"
    }
  }
}

# Vagrantfile
require_relative 'variables'

Vagrant.configure("2") do |config|
  VAGRANT_ENV["boxes"].each do |name, box_config|
    config.vm.define name do |machine|
      machine.vm.box = box_config["box"]
      machine.vm.hostname = "#{name}-server"
      machine.vm.network "private_network", ip: box_config["ip"]

      # Порты
      box_config["ports"].each do |port|
        machine.vm.network "forwarded_port",
          guest: port["guest"],
          host: port["host"]
      end

      # Ресурсы
      machine.vm.provider "virtualbox" do |vb|
        vb.memory = box_config["memory"]
        vb.cpus = box_config["cpus"]
        vb.name = "#{name}-server"
      end

      # Provisioning
      case box_config["provision"]
      when "web"
        machine.vm.provision "shell", path: "scripts/web-setup.sh"
      when "db"
        machine.vm.provision "shell", path: "scripts/db-setup.sh"
      when "cache"
        machine.vm.provision "shell", path: "scripts/cache-setup.sh"
      end
    end
  end
end
```

## Provisioners

### Shell provisioner
```ruby
Vagrant.configure("2") do |config|
  config.vm.provision "shell", inline: <<-SHELL
    # Обновление системы
    sudo apt-get update
    sudo apt-get upgrade -y

    # Установка базовых пакетов
    sudo apt-get install -y curl wget git vim htop

    # Настройка timezone
    sudo timedatectl set-timezone Europe/Moscow

    # Создание пользователя разработчика
    sudo useradd -m -s /bin/bash dev
    echo "dev:password" | sudo chpasswd
    sudo usermod -aG sudo dev

    # Настройка SSH
    mkdir -p /home/vagrant/.ssh
    chmod 700 /home/vagrant/.ssh
    curl -s https://github.com/vagrant.keys >> /home/vagrant/.ssh/authorized_keys
    chmod 600 /home/vagrant/.ssh/authorized_keys
  SHELL

  # Выполнение скрипта
  config.vm.provision "shell", path: "scripts/bootstrap.sh",
    args: ["--verbose", "--debug"],
    privileged: false,
    keep_color: true,
    name: "bootstrap"

  # Перезагрузка после provisioning
  config.vm.provision "shell", inline: "sudo reboot",
    run: "always",
    name: "reboot"
end
```

### Ansible provisioner
```ruby
Vagrant.configure("2") do |config|
  # Ansible local provisioner
  config.vm.provision "ansible_local" do |ansible|
    ansible.playbook = "ansible/playbook.yml"
    ansible.inventory_path = "ansible/inventory"
    ansible.limit = "all"
    ansible.verbose = "v"
    ansible.extra_vars = {
      app_version: "1.0.0",
      environment: "development",
      db_host: "192.168.56.12",
      redis_host: "192.168.56.13"
    }
    ansible.tags = ["common", "app"]
    ansible.skip_tags = ["monitoring"]
  end

  # Ansible remote provisioner
  config.vm.provision "ansible" do |ansible|
    ansible.playbook = "ansible/playbook.yml"
    ansible.inventory_path = "ansible/inventory"
    ansible.host_key_checking = false
    ansible.ssh_args = "-o ForwardAgent=yes -o ControlMaster=auto"
    ansible.raw_arguments = ["--connection=paramiko", "--timeout=30"]
  end
end
```

### Puppet provisioner
```ruby
Vagrant.configure("2") do |config|
  config.vm.provision "puppet" do |puppet|
    puppet.manifests_path = "puppet/manifests"
    puppet.manifest_file = "site.pp"
    puppet.module_path = "puppet/modules"
    puppet.hiera_config_path = "puppet/hiera.yaml"
    puppet.facter = {
      "environment" => "development",
      "role" => "webserver"
    }
    puppet.options = "--verbose --debug"
  end
end
```

### Docker provisioner
```ruby
Vagrant.configure("2") do |config|
  # Docker daemon
  config.vm.provision "docker" do |d|
    d.pull_images "nginx:latest"
    d.pull_images "postgres:13"
    d.run "nginx",
      args: "-d --name web -p 80:80 -v /vagrant/html:/usr/share/nginx/html"
    d.run "postgres",
      args: "-d --name db -e POSTGRES_PASSWORD=password -v /vagrant/data:/var/lib/postgresql/data"
  end

  # Docker Compose
  config.vm.provision "docker_compose",
    compose_version: "1.29.2",
    yml: "/vagrant/docker-compose.yml",
    run: "always"
end
```

## Providers

### VirtualBox provider
```ruby
Vagrant.configure("2") do |config|
  config.vm.provider "virtualbox" do |vb|
    # Основные настройки
    vb.memory = "4096"
    vb.cpus = 4
    vb.name = "enterprise-vm"

    # Графический интерфейс
    vb.gui = false

    # Дисковое пространство
    vb.customize ["modifyvm", :id, "--vrde", "on"]
    vb.customize ["modifyvm", :id, "--vrdeport", "3389"]

    # Сетевое оборудование
    vb.customize ["modifyvm", :id, "--nictype1", "virtio"]
    vb.customize ["modifyvm", :id, "--nictype2", "virtio"]

    # USB
    vb.customize ["modifyvm", :id, "--usb", "on"]
    vb.customize ["modifyvm", :id, "--usbehci", "on"]

    # Shared folders
    vb.customize ["setextradata", :id, "VBoxInternal2/SharedFoldersEnableSymlinksCreate/vagrant", "1"]

    # CPU
    vb.customize ["modifyvm", :id, "--cpu-profile", "host"]
    vb.customize ["modifyvm", :id, "--cpuexecutioncap", "80"]

    # Storage
    vb.customize ["storagectl", :id, "--name", "SATA Controller", "--hostiocache", "on"]
    vb.customize ["createhd", "--filename", "disk2.vdi", "--size", 20480]
    vb.customize ["storageattach", :id, "--storagectl", "SATA Controller", "--port", 1, "--device", 0, "--type", "hdd", "--medium", "disk2.vdi"]
  end
end
```

### VMware provider
```ruby
Vagrant.configure("2") do |config|
  config.vm.provider "vmware_desktop" do |vmware|
    vmware.vmx["memsize"] = "4096"
    vmware.vmx["numvcpus"] = "4"
    vmware.vmx["displayName"] = "enterprise-vmware"

    # VMware specific settings
    vmware.vmx["ethernet0.virtualDev"] = "vmxnet3"
    vmware.vmx["scsi0.virtualDev"] = "pvscsi"

    # Performance optimizations
    vmware.vmx["sched.mem.pshare.enable"] = "FALSE"
    vmware.vmx["MemTrimRate"] = "0"
    vmware.vmx["MemAllowAutoScaleDown"] = "FALSE"

    # GUI
    vmware.gui = false
  end
end
```

### AWS provider
```ruby
Vagrant.configure("2") do |config|
  config.vm.provider "aws" do |aws, override|
    aws.access_key_id = ENV['AWS_ACCESS_KEY_ID']
    aws.secret_access_key = ENV['AWS_SECRET_ACCESS_KEY']
    aws.region = "us-east-1"

    aws.ami = "ami-12345678"
    aws.instance_type = "t3.medium"
    aws.keypair_name = "vagrant-aws"
    aws.security_groups = ["sg-12345678"]

    aws.tags = {
      'Name' => 'Vagrant-Machine',
      'Environment' => 'development'
    }

    override.vm.box = "dummy"
    override.ssh.username = "ubuntu"
    override.ssh.private_key_path = "~/.ssh/aws.pem"
  end
end
```

## Plugins

### Полезные плагины
```bash
# Управление хостами
vagrant plugin install vagrant-hostmanager

# Кэширование пакетов
vagrant plugin install vagrant-cachier

# Автоматическое обновление гостевых дополнений
vagrant plugin install vagrant-vbguest

# Управление снапшотами
vagrant plugin install vagrant-vbox-snapshot

# Docker интеграция
vagrant plugin install vagrant-docker-compose

# Ansible интеграция
vagrant plugin install vagrant-ansible

# AWS интеграция
vagrant plugin install vagrant-aws

# Proxy конфигурация
vagrant plugin install vagrant-proxyconf

# Time sync
vagrant plugin install vagrant-timezone

# Disk size management
vagrant plugin install vagrant-disksize
```

### Кастомный плагин
```ruby
# lib/vagrant-enterprise.rb
require "vagrant"

module VagrantPlugins
  module Enterprise
    class Plugin < Vagrant.plugin("2")
      name "enterprise"

      description <<-DESC
      Enterprise Vagrant plugin with additional features for large teams.
      DESC

      config "enterprise" do
        require_relative "config"
        Config
      end

      action_hook(:enterprise, :machine_action_up) do |hook|
        hook.append Action::SetupEnterprise
      end
    end
  end
end

# lib/vagrant-enterprise/config.rb
module VagrantPlugins
  module Enterprise
    class Config < Vagrant.plugin("2", :config)
      attr_accessor :team_name
      attr_accessor :project_name
      attr_accessor :environment
      attr_accessor :monitoring_enabled

      def initialize
        @team_name = UNSET_VALUE
        @project_name = UNSET_VALUE
        @environment = UNSET_VALUE
        @monitoring_enabled = UNSET_VALUE
      end

      def finalize!
        @team_name = "default" if @team_name == UNSET_VALUE
        @project_name = nil if @project_name == UNSET_VALUE
        @environment = "development" if @environment == UNSET_VALUE
        @monitoring_enabled = false if @monitoring_enabled == UNSET_VALUE
      end

      def validate(machine)
        errors = _detected_errors

        if !@team_name.is_a?(String)
          errors << "team_name must be a string"
        end

        errors
      end
    end
  end
end
```

## Enterprise patterns

### Multi-environment setup
```ruby
# environments.rb
ENVIRONMENTS = {
  "development" => {
    "box" => "ubuntu/focal64",
    "memory" => 2048,
    "cpus" => 2,
    "instances" => 3,
    "provision" => "dev"
  },
  "staging" => {
    "box" => "ubuntu/focal64",
    "memory" => 4096,
    "cpus" => 4,
    "instances" => 2,
    "provision" => "staging"
  },
  "production" => {
    "box" => "ubuntu/focal64",
    "memory" => 8192,
    "cpus" => 8,
    "instances" => 1,
    "provision" => "prod"
  }
}

# Vagrantfile
require_relative 'environments'

environment = ENV['VAGRANT_ENV'] || 'development'
config = ENVIRONMENTS[environment]

Vagrant.configure("2") do |vagrant|
  (1..config["instances"]).each do |i|
    vagrant.vm.define "#{environment}-#{i}" do |machine|
      machine.vm.box = config["box"]
      machine.vm.hostname = "#{environment}-#{i}"

      machine.vm.provider "virtualbox" do |vb|
        vb.memory = config["memory"]
        vb.cpus = config["cpus"]
        vb.name = "#{environment}-#{i}"
      end

      # Environment-specific provisioning
      case config["provision"]
      when "dev"
        machine.vm.provision "shell", path: "scripts/dev-setup.sh"
      when "staging"
        machine.vm.provision "ansible_local" do |ansible|
          ansible.playbook = "ansible/staging.yml"
        end
      when "prod"
        machine.vm.provision "shell", path: "scripts/prod-setup.sh"
      end
    end
  end
end
```

### Team development workflow
```ruby
Vagrant.configure("2") do |config|
  # Базовые настройки
  config.vm.box = "ubuntu/focal64"
  config.vm.hostname = "team-dev"

  # Team-specific networking
  config.vm.network "private_network", ip: "192.168.56.100"

  # Shared folders для команды
  config.vm.synced_folder "~/team-projects", "/team-projects",
    type: "nfs",
    mount_options: ["rw", "vers=3", "tcp"]

  # User management
  config.vm.provision "shell", inline: <<-SHELL
    # Создание пользователей команды
    users=("alice" "bob" "charlie")
    for user in "${users[@]}"; do
      sudo useradd -m -s /bin/bash $user
      sudo usermod -aG sudo $user
      echo "$user:password123" | sudo chpasswd
      sudo mkdir -p /home/$user/.ssh
      sudo chmod 700 /home/$user/.ssh
    done

    # Настройка sudo без пароля для разработчиков
    echo "%sudo ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/developers
  SHELL

  # Development tools
  config.vm.provision "shell", path: "scripts/dev-tools.sh"

  # IDE integration
  config.vm.provision "shell", inline: <<-SHELL
    # IntelliJ IDEA
    wget -O idea.tar.gz https://download.jetbrains.com/idea/ideaIU-2022.2.1.tar.gz
    sudo tar -xzf idea.tar.gz -C /opt/
    sudo ln -sf /opt/idea-IU-222.3739.54/bin/idea.sh /usr/local/bin/idea

    # VS Code
    wget -qO- https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -
    sudo add-apt-repository "deb [arch=amd64] https://packages.microsoft.com/repos/vscode stable main"
    sudo apt-get install -y code
  SHELL

  # Monitoring
  config.vm.provision "shell", inline: <<-SHELL
    # Grafana + Prometheus
    sudo docker run -d --name prometheus -p 9090:9090 prom/prometheus
    sudo docker run -d --name grafana -p 3000:3000 grafana/grafana
  SHELL
end
```

## CI/CD интеграция

### Jenkins pipeline
```groovy
// Jenkinsfile
pipeline {
    agent any

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['development', 'staging', 'production'], description: 'Target environment')
        booleanParam(name: 'DESTROY', defaultValue: false, description: 'Destroy environment after testing')
    }

    stages {
        stage('Validate Vagrant') {
            steps {
                sh 'vagrant validate'
            }
        }

        stage('Start Environment') {
            steps {
                sh "VAGRANT_ENV=${params.ENVIRONMENT} vagrant up --parallel"
            }
        }

        stage('Provision Environment') {
            steps {
                sh "VAGRANT_ENV=${params.ENVIRONMENT} vagrant provision"
            }
        }

        stage('Run Tests') {
            steps {
                sh "VAGRANT_ENV=${params.ENVIRONMENT} vagrant ssh -c 'cd /vagrant && ./run-tests.sh'"
            }
        }

        stage('Deploy Application') {
            steps {
                sh "VAGRANT_ENV=${params.ENVIRONMENT} vagrant ssh -c 'cd /vagrant && ./deploy.sh'"
            }
        }
    }

    post {
        always {
            script {
                if (params.DESTROY) {
                    sh "VAGRANT_ENV=${params.ENVIRONMENT} vagrant destroy -f"
                }
            }
            cleanWs()
        }
        success {
            echo 'Vagrant environment created and tested successfully!'
        }
        failure {
            echo 'Vagrant environment creation failed!'
            sh "VAGRANT_ENV=${params.ENVIRONMENT} vagrant destroy -f || true"
        }
    }
}
```

### GitHub Actions
```yaml
# .github/workflows/vagrant.yml
name: Vagrant

on:
  push:
    branches: [main]
    paths:
      - 'Vagrantfile'
      - 'scripts/'
      - 'ansible/'

jobs:
  vagrant:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout
      uses: actions/checkout@v3

    - name: Setup Vagrant
      run: |
        sudo apt-get update
        sudo apt-get install -y vagrant virtualbox

    - name: Cache Vagrant boxes
      uses: actions/cache@v3
      with:
        path: ~/.vagrant.d/boxes
        key: vagrant-box-${{ hashFiles('Vagrantfile') }}

    - name: Validate Vagrantfile
      run: vagrant validate

    - name: Start Vagrant environment
      run: vagrant up --parallel

    - name: Run provisioning
      run: vagrant provision

    - name: Run tests
      run: vagrant ssh -c "cd /vagrant && ./run-tests.sh"

    - name: Clean up
      if: always()
      run: vagrant destroy -f
```

## Security и **compliance**

### Security hardening
```ruby
Vagrant.configure("2") do |config|
  config.vm.provision "shell", inline: <<-SHELL
    # Отключение root SSH
    sudo sed -i 's/PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config

    # Изменение SSH порта
    sudo sed -i 's/#Port 22/Port 2222/' /etc/ssh/sshd_config
    sudo systemctl restart sshd

    # Настройка firewall
    sudo ufw --force enable
    sudo ufw allow 2222/tcp
    sudo ufw allow 80/tcp
    sudo ufw allow 443/tcp

    # SELinux/AppArmor
    sudo setenforce 1

    # Автоматические обновления безопасности
    sudo apt-get install -y unattended-upgrades
    sudo dpkg-reconfigure -f noninteractive unattended-upgrades

    # Audit logging
    sudo apt-get install -y auditd audispd-plugins
    sudo systemctl enable auditd
    sudo systemctl start auditd
  SHELL

  # Vault integration
  config.vm.provision "shell", inline: <<-SHELL
    # Установка Vault agent
    wget -O- https://apt.releases.hashicorp.com/gpg | sudo apt-key add -
    sudo apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main"
    sudo apt-get update && sudo apt-get install -y vault

    # Настройка Vault agent
    sudo mkdir -p /etc/vault
    cat > /etc/vault/config.hcl << EOF
    vault {
      address = "https://vault.example.com:8200"
      tls_skip_verify = false
    }

    auto_auth {
      method "aws" {
        mount_path = "auth/aws"
        config = {
          type = "iam"
          role = "dev-role"
        }
      }
    }

    template {
      source = "/etc/vault/db-credentials.tpl"
      destination = "/etc/myapp/db.conf"
    }
    EOF
  SHELL
end
```

### Compliance automation
```ruby
Vagrant.configure("2") do |config|
  # CIS hardening
  config.vm.provision "shell", path: "scripts/cis-hardening.sh"

  # STIG compliance
  config.vm.provision "ansible_local" do |ansible|
    ansible.playbook = "ansible/stig-compliance.yml"
    ansible.extra_vars = {
      stig_version: "RHEL-7",
      compliance_level: "high"
    }
  end

  # Custom security policies
  config.vm.provision "shell", inline: <<-SHELL
    # Проверка compliance
    sudo apt-get install -y lynis
    sudo lynis audit system --report-file /var/log/lynis-report.dat

    # Проверка на известные уязвимости
    sudo apt-get install -y debsecan
    sudo debsecan --format detail

    # Антивирус
    sudo apt-get install -y clamav
    sudo systemctl enable clamav-daemon
    sudo systemctl start clamav-daemon
    sudo clamscan -r /home --log=/var/log/clamav-scan.log
  SHELL
end
```

## Performance optimization

### Resource management
```ruby
Vagrant.configure("2") do |config|
  config.vm.provider "virtualbox" do |vb|
    # Оптимизация памяти
    vb.memory = 4096
    vb.customize ["modifyvm", :id, "--memoryballoon", "2048"]
    vb.customize ["modifyvm", :id, "--pagefusion", "on"]

    # CPU optimization
    vb.cpus = 4
    vb.customize ["modifyvm", :id, "--cpuexecutioncap", "90"]
    vb.customize ["modifyvm", :id, "--cpu-hotplug", "on"]

    # I/O optimization
    vb.customize ["storagectl", :id, "--name", "SATA Controller", "--hostiocache", "on"]
    vb.customize ["setextradata", :id, "VBoxInternal2/SharedFoldersEnableSymlinksCreate/v-root", "1"]

    # Network optimization
    vb.customize ["modifyvm", :id, "--nictype1", "virtio"]
    vb.customize ["modifyvm", :id, "--cableconnected1", "on"]
  end

  # NFS для лучшей производительности
  config.vm.synced_folder ".", "/vagrant",
    type: "nfs",
    mount_options: ["rw", "vers=3", "tcp", "nolock", "noatime", "nodiratime"]
end
```

### Caching и **optimization**
```ruby
Vagrant.configure("2") do |config|
  # Vagrant cachier для кэширования пакетов
  if Vagrant.has_plugin?("vagrant-cachier")
    config.cache.scope = :box
    config.cache.enable :apt
    config.cache.enable :gem
    config.cache.enable :npm
    config.cache.enable :pip
  end

  # Hostmanager для DNS
  if Vagrant.has_plugin?("vagrant-hostmanager")
    config.hostmanager.enabled = true
    config.hostmanager.manage_host = true
    config.hostmanager.manage_guest = true
    config.hostmanager.include_offline = true
  end

  # Disk size management
  if Vagrant.has_plugin?("vagrant-disksize")
    config.disksize.size = '50GB'
  end

  # Proxy configuration
  if Vagrant.has_plugin?("vagrant-proxyconf")
    config.proxy.http = "http://proxy.example.com:8080"
    config.proxy.https = "http://proxy.example.com:8080"
    config.proxy.no_proxy = "localhost,127.0.0.1,.example.com"
  end
end
```

## Решение проблем

### Common issues
```bash
# Проверка статуса
vagrant status

# Логи
vagrant ssh -c "tail -f /var/log/syslog"

# Перезапуск сервисов
vagrant ssh -c "sudo systemctl restart nginx"

# Очистка
vagrant destroy -f
vagrant box remove ubuntu/focal64
vagrant plugin uninstall vagrant-vbguest

# Debug mode
VAGRANT_LOG=debug vagrant up

# SSH debugging
vagrant ssh-config
ssh -i ~/.vagrant/machines/default/virtualbox/private_key vagrant@127.0.0.1 -p 2222

# Network issues
vagrant reload --provision
vagrant ssh -c "ip addr show"
vagrant ssh -c "ping -c 3 google.com"
```

### Performance troubleshooting
```bash
# Проверка ресурсов
vagrant ssh -c "free -h"
vagrant ssh -c "df -h"
vagrant ssh -c "top -b -n1 | head -20"

# Network performance
vagrant ssh -c "iperf -s" &
vagrant ssh -c "iperf -c localhost"

# Disk I/O
vagrant ssh -c "dd if=/dev/zero of=/tmp/test bs=1M count=1000"

# CPU usage
vagrant ssh -c "mpstat 1 5"

# Memory leaks
vagrant ssh -c "ps aux --sort=-%mem | head -10"
```

### Recovery procedures
```bash
# Восстановление из снапшота
vagrant snapshot list
vagrant snapshot restore default snapshot1

# Пересоздание VM
vagrant destroy -f
rm -rf .vagrant
vagrant up

# Очистка кэша
vagrant box remove --all --force
rm -rf ~/.vagrant.d/

# Force reload
vagrant reload --force
```

## Лучшие практики

### Project structure
```text
enterprise-vagrant/
├── Vagrantfile               # Основной конфигурационный файл
├── variables.rb             # Переменные и конфигурации
├── environments.rb          # Конфигурации сред
├── scripts/                 # Provisioning скрипты
│   ├── bootstrap.sh        # Базовая настройка
│   ├── dev-tools.sh        # Инструменты разработчика
│   ├── security.sh         # Security hardening
│   └── cleanup.sh          # Очистка
├── ansible/                 # Ansible плейбуки
│   ├── playbooks/          # Плейбуки для разных компонентов
│   ├── roles/              # Переиспользуемые роли
│   └── inventory/          # Инвентарь
├── puppet/                  # Puppet манифесты
│   ├── manifests/
│   ├── modules/
│   └── hiera.yaml
├── docker/                  # Docker конфигурации
│   ├── docker-compose.yml
│   └── Dockerfile
├── config/                  # Дополнительные конфигурации
│   ├── nginx.conf
│   ├── postgresql.conf
│   └── redis.conf
├── tests/                   # Тесты
│   ├── integration/
│   └── unit/
├── docs/                    # Документация
│   ├── README.md
│   ├── setup.md
│   └── troubleshooting.md
├── Makefile                 # Автоматизация задач
└── .gitignore
```

### Makefile automation
```makefile
.PHONY: up destroy provision test clean

# Переменные
VAGRANT = vagrant
ENVIRONMENT = development

# Основные команды
up:
	VAGRANT_ENV=$(ENVIRONMENT) $(VAGRANT) up --parallel

destroy:
	VAGRANT_ENV=$(ENVIRONMENT) $(VAGRANT) destroy -f

provision:
	VAGRANT_ENV=$(ENVIRONMENT) $(VAGRANT) provision

test:
	VAGRANT_ENV=$(ENVIRONMENT) $(VAGRANT) ssh -c "cd /vagrant && ./run-tests.sh"

clean:
	$(VAGRANT) destroy -f
	rm -rf .vagrant/
	rm -f Vagrantfile.backup

# Environment-specific команды
dev:
	$(MAKE) ENVIRONMENT=development up

staging:
	$(MAKE) ENVIRONMENT=staging up

prod:
	$(MAKE) ENVIRONMENT=production up

# Утилиты
status:
	$(VAGRANT) status

ssh:
	$(VAGRANT) ssh

logs:
	$(VAGRANT) ssh -c "tail -f /var/log/syslog"

backup:
	cp Vagrantfile Vagrantfile.backup

restore:
	cp Vagrantfile.backup Vagrantfile
```

### Version control
```bash
# .gitignore для Vagrant проектов
.vagrant/
*.log
.vagrantfile.backup
.env
.DS_Store

# Игнорировать чувствительные файлы
config/secrets.yml
ansible/vault_password
scripts/private_key

# Кэши и временные файлы
.cache/
tmp/
*.tmp
```

### Documentation
```markdown
# Enterprise Vagrant Environment

## Overview
Этот проект предоставляет полностью настроенную среду разработки для enterprise приложений.

## Prerequisites
- VirtualBox 6.1+
- Vagrant 2.2.19+
- 16GB RAM минимум
- 50GB свободного места на диске

## Quick Start
```bash
# Клонирование репозитория
git clone https://github.com/hashicorp/enterprise-vagrant.git
cd `enterprise-vagrant`

# Запуск среды
make dev

# Проверка статуса
make status

# Подключение к `VM`
make ssh
```

## Environments
- Development: Полная среда с инструментами разработчика
- Staging: Production-like среда для тестирования
- Production: Минимальная среда для production deployment

## Components
- Web Server: Nginx с SSL/TLS
- Application Server: Java 11 + Spring Boot
- Database: PostgreSQL с репликацией
- Cache: Redis кластер
- Monitoring: Prometheus + Grafana

## Troubleshooting
См. раздел «Решение проблем» выше и [официальную документацию Vagrant](https://www.vagrantup.com/docs).
```
## См. также
- [Docker](../containers/docker/docker-basics.md) — контейнеризация
- [Ansible](../iac/ansible/ansible-basics.md) — **Configuration Management**
- [Terraform](../iac/terraform/terraform-basics.md) — **Infrastructure as Code**
- [Jenkins](https://www.jenkins.io/doc/) — **CI/CD Pipeline**
