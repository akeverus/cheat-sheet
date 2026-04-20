---
title: "Packer"
description: "Packer - это инструмент с открытым исходным кодом от HashiCorp для создания идентичных образов машин для различных платформ (AWS AMI, Docker containers, VMware VMs, VirtualBox и другие) из единого конфигурационного файла. Этот документ охватывает продвинутые паттерны, автоматизац"
tags:
  - platform
  - iac
  - packer
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Packer**

**Packer** - это инструмент с открытым исходным кодом от **HashiCorp** для создания идентичных образов машин для различных платформ (**AWS `AMI`, `Docker containers`, VMware VMs, `VirtualBox` и другие**) из единого конфигурационного файла. Этот документ охватывает продвинутые паттерны, автоматизацию и **best practices** для **enterprise** использования **Packer**.

## Полезные ссылки
- [Packer Documentation](https://developer.hashicorp.com/packer/docs)
- [Packer Builders](https://developer.hashicorp.com/packer/plugins/builders)
- [Packer Provisioners](https://developer.hashicorp.com/packer/plugins/provisioners)
- [Packer Post-Processors](https://developer.hashicorp.com/packer/plugins/post-processors)
- [Packer GitHub](https://github.com/hashicorp/packer)
- [Packer Registry](https://registry.terraform.io/namespaces/hashicorp)

## Содержание

- [Основы Packer](#основы-packer)
  - [Установка и настройка](#установка-и-настройка)
  - [Структура проекта](#структура-проекта)
  - [Основной конфигурационный файл](#основной-конфигурационный-файл)
- [Продвинутые builders](#продвинутые-builders)
  - [Multi-cloud builders](#multi-cloud-builders)
  - [Windows builders](#windows-builders)
- [Provisioners](#provisioners)
  - [Ansible provisioner](#ansible-provisioner)
  - [Shell provisioner с переменными](#shell-provisioner-с-переменными)
  - [File provisioner для конфигураций](#file-provisioner-для-конфигураций)
- [Variables и templates](#variables-и-templates)
  - [Variable validation](#variable-validation)
  - [Template functions](#template-functions)
- [Post-processors](#post-processors)
  - [Multi-post-processor pipelines](#multi-post-processor-pipelines)
  - [Artifactory integration](#artifactory-integration)
- [CI/CD интеграция](#cicd-интеграция)
  - [Jenkins pipeline](#jenkins-pipeline)
  - [GitHub Actions](#github-actions)
- [Enterprise patterns](#enterprise-patterns)
  - [Multi-environment builds](#multi-environment-builds)
  - [Parallel builds](#parallel-builds)
  - [Immutable infrastructure](#immutable-infrastructure)
- [Security hardening](#security-hardening)
  - [Vault integration](#vault-integration)
  - [CIS hardening](#cis-hardening)
- [Performance optimization](#performance-optimization)
  - [Build optimization](#build-optimization)
  - [Parallel builds](#parallel-builds-1)
- [Monitoring и logging](#monitoring-и-logging)
  - [Build telemetry](#build-telemetry)
  - [Metrics collection](#metrics-collection)
- [Решение проблем](#решение-проблем)
  - [Common issues](#common-issues)
  - [Log analysis](#log-analysis)
  - [Performance troubleshooting](#performance-troubleshooting)
- [Лучшие практики](#лучшие-практики)
  - [Project structure](#project-structure)
  - [Makefile для автоматизации](#makefile-для-автоматизации)
  - [Security best practices](#security-best-practices)
- [См. также](#см-также)

## Основы **Packer**

### Установка и настройка
Ниже — установка **Packer** на **Linux**/**macOS** (**bash**).
```bash
# Linux/macOS
curl -fsSL https://apt.releases.hashicorp.com/gpg | sudo apt-key add -
sudo apt-add-repository "deb [arch=amd64] https://apt.releases.hashicorp.com $(lsb_release -cs) main"
sudo apt-get update && sudo apt-get install packer

# macOS с Homebrew
brew tap hashicorp/tap
brew install hashicorp/tap/packer

# Проверка установки
packer version

# Автодополнение
packer -autocomplete-install
```

### Структура проекта
Ниже — структура каталогов **Packer**-проекта (**текст**).
```text
packer-project/
├── packer.json           # Основной конфигурационный файл
├── variables.json        # Переменные
├── scripts/              # Provisioning скрипты
│   ├── setup.sh
│   ├── install-app.sh
│   └── cleanup.sh
├── templates/            # Шаблоны конфигураций
│   ├── aws.json
│   ├── azure.json
│   └── docker.json
├── ansible/              # Ansible плейбуки
│   ├── playbook.yml
│   └── roles/
├── packer.d/             # Дополнительные конфигурации
└── output/               # Выходные файлы
```

### Основной конфигурационный файл
```json
{
  "variables": {
    "aws_region": "us-east-1",
    "instance_type": "t3.medium",
    "ami_name": "my-app-{{timestamp}}",
    "app_version": "1.0.0"
  },

  "builders": [
    {
      "type": "amazon-ebs",
      "region": "{{user `aws_region`}}",
      "instance_type": "{{user `instance_type`}}",
      "source_ami_filter": {
        "filters": {
          "virtualization-type": "hvm",
          "name": "amzn2-ami-hvm-*-x86_64-gp2",
          "root-device-type": "ebs"
        },
        "owners": ["amazon"],
        "most_recent": true
      },
      "ssh_username": "ec2-user",
      "ami_name": "{{user `ami_name`}}",
      "tags": {
        "Name": "MyApp",
        "Version": "{{user `app_version`}}",
        "BuiltBy": "Packer",
        "BuildDate": "{{isotime}}"
      }
    }
  ],

  "provisioners": [
    {
      "type": "shell",
      "inline": [
        "sudo yum update -y",
        "sudo yum install -y docker git"
      ]
    },
    {
      "type": "ansible-local",
      "playbook_file": "ansible/playbook.yml",
      "extra_arguments": ["--extra-vars", "app_version={{user `app_version`}}"]
    }
  ],

  "post-processors": [
    {
      "type": "manifest",
      "output": "packer-manifest.json",
      "strip_path": true
    }
  ]
}
```

## Продвинутые **builders**

### Multi-cloud builders
```json
{
  "builders": [
    {
      "name": "aws-ubuntu",
      "type": "amazon-ebs",
      "region": "{{user `aws_region`}}",
      "instance_type": "t3.medium",
      "source_ami_filter": {
        "filters": {
          "virtualization-type": "hvm",
          "name": "ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*",
          "root-device-type": "ebs"
        },
        "owners": ["099720109477"],
        "most_recent": true
      },
      "ssh_username": "ubuntu",
      "ami_name": "ubuntu-app-{{timestamp}}",
      "tags": {
        "OS": "Ubuntu",
        "App": "MyApp"
      }
    },
    {
      "name": "azure-ubuntu",
      "type": "azure-arm",
      "subscription_id": "{{user `azure_subscription_id`}}",
      "client_id": "{{user `azure_client_id`}}",
      "client_secret": "{{user `azure_client_secret`}}",
      "tenant_id": "{{user `azure_tenant_id`}}",
      "managed_image_resource_group_name": "{{user `azure_resource_group`}}",
      "managed_image_name": "ubuntu-app-{{timestamp}}",
      "os_type": "Linux",
      "image_publisher": "Canonical",
      "image_offer": "UbuntuServer",
      "image_sku": "18.04-LTS",
      "azure_tags": {
        "OS": "Ubuntu",
        "App": "MyApp"
      },
      "location": "{{user `azure_location`}}",
      "vm_size": "Standard_DS2_v2"
    },
    {
      "name": "docker-app",
      "type": "docker",
      "image": "ubuntu:20.04",
      "commit": true,
      "changes": [
        "EXPOSE 8080",
        "CMD [\"./myapp\"]"
      ]
    }
  ]
}
```

### Windows builders
```json
{
  "builders": [
    {
      "type": "amazon-ebs",
      "region": "{{user `aws_region`}}",
      "instance_type": "t3.medium",
      "source_ami_filter": {
        "filters": {
          "virtualization-type": "hvm",
          "name": "Windows_Server-2019-English-Full-Base-*",
          "root-device-type": "ebs"
        },
        "owners": ["amazon"],
        "most_recent": true
      },
      "communicator": "winrm",
      "winrm_username": "Administrator",
      "winrm_password": "{{user `winrm_password`}}",
      "winrm_timeout": "10m",
      "user_data_file": "scripts/winrm-setup.ps1",
      "ami_name": "windows-app-{{timestamp}}"
    }
  ],

  "provisioners": [
    {
      "type": "powershell",
      "inline": [
        "Set-ExecutionPolicy Bypass -Scope Process -Force",
        "[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072",
        "iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))"
      ]
    },
    {
      "type": "windows-restart",
      "restart_timeout": "5m"
    },
    {
      "type": "powershell",
      "script": "scripts/install-app.ps1"
    }
  ]
}
```

## Provisioners

### Ansible provisioner
```json
{
  "provisioners": [
    {
      "type": "ansible",
      "playbook_file": "ansible/playbook.yml",
      "ansible_env_vars": [
        "ANSIBLE_HOST_KEY_CHECKING=False",
        "ANSIBLE_SSH_ARGS='-o ForwardAgent=yes -o ControlMaster=auto -o ControlPersist=60s'"
      ],
      "extra_arguments": [
        "--extra-vars",
        "app_version={{user `app_version`}} environment={{user `environment`}}",
        "--vault-password-file",
        "/path/to/vault-password"
      ],
      "groups": ["webservers", "dbservers"],
      "host_alias": "packer-build"
    }
  ]
}
```

### Shell provisioner с переменными
```json
{
  "provisioners": [
    {
      "type": "shell",
      "environment_vars": [
        "APP_VERSION={{user `app_version`}}",
        "ENVIRONMENT={{user `environment`}}",
        "DATABASE_URL={{user `database_url`}}"
      ],
      "execute_command": "chmod +x {{.Path}}; {{.Vars}} sudo -E -S bash '{{.Path}}'",
      "scripts": [
        "scripts/update-system.sh",
        "scripts/install-dependencies.sh",
        "scripts/configure-app.sh",
        "scripts/cleanup.sh"
      ],
      "expect_disconnect": true
    }
  ]
}
```

### File provisioner для конфигураций
```json
{
  "provisioners": [
    {
      "type": "file",
      "source": "config/app-config.json",
      "destination": "/etc/myapp/config.json"
    },
    {
      "type": "file",
      "sources": [
        "config/",
        "scripts/"
      ],
      "destination": "/tmp/packer-files/"
    },
    {
      "type": "file",
      "direction": "download",
      "source": "/var/log/myapp/install.log",
      "destination": "logs/install.log"
    }
  ]
}
```

## Variables и **templates**

### Variable validation
```json
{
  "variables": {
    "aws_region": {
      "default": "us-east-1",
      "description": "AWS region to build in"
    },
    "instance_type": {
      "default": "t3.medium",
      "description": "EC2 instance type"
    },
    "app_version": {
      "description": "Application version to build"
    },
    "environment": {
      "default": "development",
      "description": "Environment name",
      "validation": {
        "condition": "{{.Value}} == \"development\" || {{.Value}} == \"staging\" || {{.Value}} == \"production\"",
        "error_message": "Environment must be one of: development, staging, production"
      }
    },
    "database_url": {
      "description": "Database connection URL",
      "sensitive": true
    }
  }
}
```

### Template functions
```json
{
  "variables": {
    "timestamp": "{{timestamp}}",
    "isotime": "{{isotime}}",
    "uuid": "{{uuid}}",
    "build_number": "{{env `BUILD_NUMBER`}}",
    "git_commit": "{{env `GIT_COMMIT`}}",
    "aws_account_id": "{{env `AWS_ACCOUNT_ID`}}"
  },

  "builders": [
    {
      "type": "amazon-ebs",
      "ami_name": "myapp-{{user `app_version`}}-{{user `timestamp`}}-{{user `build_number`}}",
      "tags": {
        "Name": "MyApp-{{user `environment`}}",
        "Version": "{{user `app_version`}}",
        "BuildNumber": "{{user `build_number`}}",
        "GitCommit": "{{user `git_commit`}}",
        "BuiltAt": "{{user `isotime`}}",
        "BuiltBy": "Packer-{{user `uuid`}}"
      }
    }
  ]
}
```

## Post-processors

### Multi-post-processor pipelines
```json
{
  "post-processors": [
    {
      "type": "manifest",
      "output": "packer-manifest.json",
      "strip_path": true
    },
    {
      "type": "shell-local",
      "inline": [
        "echo 'Build completed at $(date)' >> build.log",
        "aws s3 cp packer-manifest.json s3://my-bucket/manifests/"
      ]
    },
    [
      {
        "type": "vsphere",
        "host": "{{user `vsphere_host`}}",
        "username": "{{user `vsphere_username`}}",
        "password": "{{user `vsphere_password`}}",
        "datacenter": "{{user `vsphere_datacenter`}}",
        "cluster": "{{user `vsphere_cluster`}}",
        "datastore": "{{user `vsphere_datastore`}}",
        "vm_name": "my-vm-{{timestamp}}",
        "only": ["vmware-iso"]
      },
      {
        "type": "compress",
        "output": "my-vm-{{timestamp}}.tar.gz",
        "only": ["vmware-iso"]
      }
    ],
    {
      "type": "docker-tag",
      "repository": "myregistry.com/myapp",
      "tags": ["{{user `app_version`}}", "latest"],
      "only": ["docker"]
    }
  ]
}
```

### Artifactory integration
```json
{
  "post-processors": [
    {
      "type": "artifactory",
      "url": "{{user `artifactory_url`}}",
      "username": "{{user `artifactory_username`}}",
      "password": "{{user `artifactory_password`}}",
      "repository": "docker-local",
      "force_overwrite": true,
      "properties": {
        "build.name": "myapp",
        "build.number": "{{user `build_number`}}",
        "build.timestamp": "{{isotime}}"
      }
    }
  ]
}
```

## CI/CD интеграция

### Jenkins pipeline
```groovy
// Jenkinsfile
pipeline {
    agent any

    parameters {
        string(name: 'APP_VERSION', defaultValue: '1.0.0', description: 'Application version')
        choice(name: 'ENVIRONMENT', choices: ['development', 'staging', 'production'], description: 'Target environment')
        booleanParam(name: 'PUBLISH_AMI', defaultValue: true, description: 'Publish AMI to marketplace')
    }

    stages {
        stage('Validate Packer') {
            steps {
                sh 'packer validate -var-file=variables.json packer.json'
            }
        }

        stage('Build AMI') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    sh """
                        packer build \\
                            -var 'app_version=${params.APP_VERSION}' \\
                            -var 'environment=${params.ENVIRONMENT}' \\
                            packer.json
                    """
                }
            }
        }

        stage('Publish AMI') {
            when {
                expression { params.PUBLISH_AMI }
            }
            steps {
                script {
                    def manifest = readJSON file: 'packer-manifest.json'
                    def amiId = manifest.builds[0].artifact_id.split(':')[1]

                    sh "aws ec2 create-tags --resources $amiId --tags Key=Published,Value=true"
                }
            }
        }

        stage('Update Terraform') {
            steps {
                sh """
                    sed -i 's/ami-.*/$amiId/' terraform/main.tf
                    git add terraform/main.tf
                    git commit -m "Update AMI to $amiId"
                    git push
                """
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'packer-manifest.json', allowEmptyArchive: true
            cleanWs()
        }
        success {
            echo 'Packer build completed successfully!'
        }
        failure {
            echo 'Packer build failed!'
        }
    }
}
```

### GitHub Actions
```yaml
# .github/workflows/packer.yml
name: Packer Build

on:
  push:
    branches: [main]
    paths:
      - 'packer/'
      - '.github/workflows/packer.yml'

jobs:
  packer:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout
      uses: actions/checkout@v3

    - name: Setup Packer
      uses: hashicorp/setup-packer@main
      with:
        version: latest

    - name: Packer Init
      run: packer init packer/

    - name: Packer Validate
      run: packer validate -var-file=packer/variables.json packer/

    - name: Configure AWS Credentials
      uses: aws-actions/configure-aws-credentials@v2
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: us-east-1

    - name: Packer Build
      run: |
        packer build \
          -var "app_version=${{ github.sha }}" \
          -var "build_number=${{ github.run_number }}" \
          packer/

    - name: Update AMI in Terraform
      run: |
        AMI_ID=$(jq -r '.builds[0].artifact_id' packer-manifest.json | cut -d: -f2)
        echo "New AMI: $AMI_ID"
        echo "AMI_ID=$AMI_ID" >> $GITHUB_ENV

    - name: Update Terraform Configuration
      uses: hashicorp/setup-terraform@v2

    - name: Terraform Plan
      run: |
        cd terraform
        terraform plan -var "ami_id=$AMI_ID" -out=tfplan

    - name: Terraform Apply
      run: |
        cd terraform
        terraform apply tfplan
```

## Enterprise patterns

### Multi-environment builds
```json
{
  "variables": {
    "environment": "development",
    "environments": {
      "development": {
        "instance_type": "t3.micro",
        "security_groups": ["sg-dev"],
        "subnet_id": "subnet-dev"
      },
      "staging": {
        "instance_type": "t3.medium",
        "security_groups": ["sg-staging"],
        "subnet_id": "subnet-staging"
      },
      "production": {
        "instance_type": "t3.large",
        "security_groups": ["sg-prod"],
        "subnet_id": "subnet-prod"
      }
    }
  },

  "builders": [
    {
      "type": "amazon-ebs",
      "region": "{{user `aws_region`}}",
      "instance_type": "{{user `environments`}}[{{user `environment`}}].instance_type",
      "security_group_ids": "{{user `environments`}}[{{user `environment`}}].security_groups",
      "subnet_id": "{{user `environments`}}[{{user `environment`}}].subnet_id",
      "ami_name": "myapp-{{user `environment`}}-{{timestamp}}",
      "tags": {
        "Environment": "{{user `environment`}}",
        "Name": "MyApp-{{user `environment`}}"
      }
    }
  ]
}
```

### Parallel builds
```json
{
  "builders": [
    {
      "name": "web-server",
      "type": "amazon-ebs",
      "source_ami": "{{user `base_ami`}}",
      "instance_type": "t3.micro",
      "ami_name": "web-server-{{timestamp}}"
    },
    {
      "name": "app-server",
      "type": "amazon-ebs",
      "source_ami": "{{user `base_ami`}}",
      "instance_type": "t3.medium",
      "ami_name": "app-server-{{timestamp}}"
    },
    {
      "name": "db-server",
      "type": "amazon-ebs",
      "source_ami": "{{user `base_ami`}}",
      "instance_type": "t3.large",
      "ami_name": "db-server-{{timestamp}}"
    }
  ],

  "provisioners": [
    {
      "type": "shell",
      "inline": ["echo 'Installing common packages'"],
      "only": ["web-server", "app-server"]
    },
    {
      "type": "shell",
      "inline": ["echo 'Installing web server'"],
      "only": ["web-server"]
    },
    {
      "type": "shell",
      "inline": ["echo 'Installing application server'"],
      "only": ["app-server"]
    },
    {
      "type": "shell",
      "inline": ["echo 'Installing database server'"],
      "only": ["db-server"]
    }
  ]
}
```

### Immutable infrastructure
```json
{
  "builders": [
    {
      "type": "amazon-ebs",
      "region": "{{user `aws_region`}}",
      "instance_type": "t3.medium",
      "source_ami_filter": {
        "filters": {
          "virtualization-type": "hvm",
          "name": "amzn2-ami-hvm-*-x86_64-gp2",
          "root-device-type": "ebs"
        },
        "owners": ["amazon"],
        "most_recent": true
      },
      "ssh_username": "ec2-user",
      "ami_name": "immutable-app-{{user `app_version`}}-{{timestamp}}",
      "ena_support": true,
      "sriov_support": true,
      "tags": {
        "Name": "ImmutableApp",
        "Version": "{{user `app_version`}}",
        "Immutable": "true",
        "BuildDate": "{{isotime}}"
      }
    }
  ],

  "provisioners": [
    {
      "type": "shell",
      "inline": [
        "sudo yum update -y",
        "sudo yum install -y awscli jq curl",
        "sudo systemctl enable amazon-ssm-agent",
        "sudo systemctl start amazon-ssm-agent"
      ]
    },
    {
      "type": "ansible-local",
      "playbook_file": "ansible/immutable-playbook.yml"
    },
    {
      "type": "shell",
      "inline": [
        "sudo yum clean all",
        "sudo rm -rf /var/cache/yum/*",
        "sudo rm -rf /tmp/*",
        "sudo rm -rf /var/tmp/*",
        "history -c"
      ]
    }
  ]
}
```

## Security hardening

### Vault integration
```json
{
  "provisioners": [
    {
      "type": "shell",
      "environment_vars": [
        "VAULT_ADDR={{user `vault_addr`}}",
        "VAULT_TOKEN={{user `vault_token`}}"
      ],
      "inline": [
        "curl -o vault.zip https://releases.hashicorp.com/vault/1.12.0/vault_1.12.0_linux_amd64.zip",
        "unzip vault.zip",
        "sudo mv vault /usr/local/bin/",
        "rm vault.zip"
      ]
    },
    {
      "type": "shell",
      "inline": [
        "vault kv get -field=database_password secret/myapp/database > /etc/myapp/db_password",
        "vault kv get -field=api_key secret/myapp/api > /etc/myapp/api_key",
        "chmod 600 /etc/myapp/db_password /etc/myapp/api_key"
      ]
    }
  ]
}
```

### CIS hardening
```bash
#!/bin/bash
# scripts/cis-hardening.sh

# Disable unused services
sudo systemctl disable avahi-daemon
sudo systemctl disable cups

# Configure SSH hardening
sudo sed -i 's/#PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config
sudo sed -i 's/#PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config
sudo sed -i 's/#PermitEmptyPasswords no/PermitEmptyPasswords no/' /etc/ssh/sshd_config

# Configure firewall
sudo yum install -y firewalld
sudo systemctl enable firewalld
sudo systemctl start firewalld
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload

# Install and configure auditd
sudo yum install -y audit
sudo systemctl enable auditd
sudo systemctl start auditd

# Configure logrotate
sudo sed -i 's/weekly/daily/' /etc/logrotate.conf
sudo sed -i 's/rotate 4/rotate 7/' /etc/logrotate.conf

echo "CIS hardening completed"
```

## Performance optimization

### Build optimization
```json
{
  "builders": [
    {
      "type": "amazon-ebs",
      "region": "{{user `aws_region`}}",
      "instance_type": "{{user `instance_type`}}",
      "ssh_pty": true,
      "ssh_timeout": "5m",
      "ami_block_device_mappings": [
        {
          "device_name": "/dev/xvda",
          "volume_type": "gp3",
          "volume_size": 20,
          "iops": 3000,
          "delete_on_termination": true
        }
      ],
      "launch_block_device_mappings": [
        {
          "device_name": "/dev/xvda",
          "volume_type": "gp3",
          "volume_size": 20,
          "iops": 3000,
          "delete_on_termination": true
        }
      ]
    }
  ],

  "provisioners": [
    {
      "type": "shell",
      "inline": [
        "sudo yum update -y",
        "sudo yum install -y yum-utils",
        "sudo yum-config-manager --enable epel",
        "sudo yum install -y htop iotop sysstat",
        "sudo systemctl enable sysstat"
      ]
    }
  ]
}
```

### Parallel builds
```bash
# Parallel execution
packer build -parallel-builds=2 packer.json

# With color output
packer build -color=false packer.json

# Force build (skip validation)
packer build -force packer.json

# Debug mode
PACKER_LOG=1 packer build packer.json

# Only specific builders
packer build -only=amazon-ebs packer.json
```

## Monitoring и **logging**

### Build telemetry
```json
{
  "post-processors": [
    {
      "type": "shell-local",
      "inline": [
        "echo 'Build completed successfully' >> packer-build.log",
        "echo 'Build time: $(date)' >> packer-build.log",
        "echo 'Builder: amazon-ebs' >> packer-build.log",
        "echo 'Region: {{user `aws_region`}}' >> packer-build.log",
        "aws s3 cp packer-build.log s3://my-logs/packer/"
      ]
    }
  ]
}
```

### Metrics collection
```json
{
  "provisioners": [
    {
      "type": "shell",
      "inline": [
        "# Install CloudWatch agent",
        "wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm",
        "sudo rpm -U amazon-cloudwatch-agent.rpm",
        "sudo systemctl enable amazon-cloudwatch-agent",
        "sudo systemctl start amazon-cloudwatch-agent"
      ]
    },
    {
      "type": "file",
      "source": "config/cloudwatch-config.json",
      "destination": "/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json"
    }
  ]
}
```

## Решение проблем

### Common issues
```bash
# Validate configuration
packer validate packer.json

# Debug build process
PACKER_LOG=1 packer build packer.json

# Inspect build
packer inspect packer.json

# Check version compatibility
packer version

# Clean up failed builds
aws ec2 describe-instances --filters "Name=tag:BuiltBy,Values=Packer" --query 'Reservations[*].Instances[*].[InstanceId,State.Name]' --output table
aws ec2 terminate-instances --instance-ids i-1234567890abcdef0

# Debug SSH connection
packer build -debug packer.json
# Then connect manually: ssh -i ~/.packer.d/tmp/packer-key ec2-user@instance-ip
```

### Log analysis
```bash
# View packer logs
tail -f packer.log

# Analyze build time
grep "Build " packer.log | tail -1

# Check for errors
grep -i "error\|failed\|timeout" packer.log

# Network debugging
tcpdump -i eth0 port 22 -w ssh-traffic.pcap

# Disk space issues
df -h
du -sh /var/cache/packer/
```

### Performance troubleshooting
```bash
# Monitor build performance
time packer build packer.json

# Check resource usage
top -p $(pgrep packer)

# Network latency
ping -c 5 ec2.amazonaws.com

# DNS resolution
nslookup ec2.amazonaws.com

# AWS API rate limits
aws sts get-caller-identity
```

## Лучшие практики

### Project structure
```
enterprise-packer/
├── packer.json                 # Main configuration
├── variables.json             # Variables for different environments
├── scripts/                   # Provisioning scripts
│   ├── common.sh             # Common setup
│   ├── app.sh                # Application specific
│   └── cleanup.sh            # Cleanup script
├── ansible/                   # Ansible playbooks
│   ├── roles/                # Ansible roles
│   ├── inventory/            # Dynamic inventory
│   └── playbooks/            # Playbooks for different components
├── config/                    # Configuration templates
│   ├── nginx.conf            # Nginx configuration
│   ├── app.properties        # Application properties
│   └── systemd.service       # Systemd service
├── docker/                    # Docker configurations
│   ├── Dockerfile            # Container definition
│   └── docker-compose.yml    # Local development
├── terraform/                 # Infrastructure as code
│   ├── main.tf               # Terraform configuration
│   └── variables.tf          # Terraform variables
├── ci/                        # CI/CD configurations
│   ├── jenkins/              # Jenkins pipelines
│   ├── github-actions/       # GitHub Actions
│   └── gitlab-ci/            # GitLab CI
├── docs/                      # Documentation
│   ├── README.md             # Project documentation
│   ├── build-process.md      # Build process documentation
│   └── troubleshooting.md    # Troubleshooting guide
├── Makefile                   # Build automation
└── .packerignore             # Files to ignore
```

### Makefile для автоматизации
```makefile
.PHONY: validate build clean deploy

# Variables
PACKER := packer
TERRAFORM := terraform
ANSIBLE := ansible-playbook

# Default target
all: validate build

# Validate configurations
validate:
	$(PACKER) validate -var-file=variables.json packer.json
	$(TERRAFORM) validate terraform/

# Build images
build:
	$(PACKER) build -var-file=variables.json packer.json

# Clean up
clean:
	rm -rf packer_cache/
	rm -f packer-manifest.json
	$(TERRAFORM) destroy -auto-approve terraform/

# Deploy infrastructure
deploy: build
	cd terraform && $(TERRAFORM) apply -auto-approve

# Run tests
test:
	$(ANSIBLE) -i ansible/inventory/test ansible/playbook.yml --tags test

# Lint code
lint:
	$(PACKER) fmt packer.json
	terraform fmt terraform/

# Security scan
security:
	trivy image --format json --output trivy-results.json $(AMI_ID)
```

### Security best practices
```json
{
  "variables": {
    "vault_addr": "https://vault.example.com:8200",
    "vault_token": "{{env `VAULT_TOKEN`}}"
  },

  "builders": [
    {
      "type": "amazon-ebs",
      "encrypt_boot": true,
      "kms_key_id": "{{user `kms_key_id`}}",
      "associate_public_ip_address": false,
      "security_group_ids": ["{{user `security_group_id`}}"],
      "tags": {
        "Security": "Hardened",
        "Encrypted": "true",
        "Compliance": "CIS"
      }
    }
  ],

  "provisioners": [
    {
      "type": "shell",
      "inline": [
        "# Remove sensitive data",
        "sudo shred -u /etc/ssh/*_key /etc/ssh/*_key.pub",
        "# Disable password authentication",
        "sudo sed -i 's/PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config",
        "# Install security updates",
        "sudo yum update --security -y"
      ]
    }
  ]
}
```
## См. также
- [[packer-basics|Packer Basics]] — краткое введение в Packer
- [[terraform-basics|Terraform]] — Infrastructure as Code
- [[ansible-basics|Ansible]] — Configuration Management
- [[docker-basics|Docker]] — контейнеризация
- [[aws-basics|AWS]] — Amazon Web Services
