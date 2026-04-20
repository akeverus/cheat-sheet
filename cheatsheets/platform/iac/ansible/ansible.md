---
title: "Ansible"
description: "Ansible - это инструмент для автоматизации IT-инфраструктуры, конфигурационного управления и развертывания приложений. Ansible использует декларативный подход к описанию желаемого состояния системы и выполняет автоматизацию через SSH без установки агентов."
tags:
  - platform
  - iac
  - ansible
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Ansible

**Ansible** — это инструмент для автоматизации `IT`-инфраструктуры, конфигурационного управления и развертывания приложений. **Ansible** использует декларативный подход к описанию желаемого состояния системы и выполняет автоматизацию через **SSH** без установки агентов.

## Полезные ссылки
- [Ansible Documentation](https://docs.ansible.com/)
- [Ansible Galaxy](https://galaxy.ansible.com/)
- [Ansible GitHub](https://github.com/ansible/ansible)
- [AWX Project](https://github.com/ansible/awx) — веб-интерфейс для **Ansible**
- [Molecule](https://molecule.readthedocs.io/) — тестирование ролей
- [Ansible Vault](https://docs.ansible.com/ansible/latest/vault_guide/index.html) — шифрование секретов


### См. также
- [[iac-overview|Infrastructure as Code (IaC) — обзор]]
## Содержание

- [Основы Ansible](#основы-ansible)
  - [Архитектура Ansible](#архитектура-ansible)
  - [Установка](#установка)
    - [Linux](#linux)
    - [macOS](#macos)
    - [Windows](#windows)
  - [Базовая конфигурация](#базовая-конфигурация)
- [Inventory](#inventory)
  - [Статический inventory](#статический-inventory)
  - [Динамический inventory](#динамический-inventory)
  - [AWS EC2 динамический inventory](#aws-ec2-динамический-inventory)
- [Playbooks](#playbooks)
  - [Структура playbook](#структура-playbook)
  - [Основные модули](#основные-модули)
    - [Package management](#package-management)
    - [File operations](#file-operations)
    - [Service management](#service-management)
    - [User management](#user-management)
- [Roles](#roles)
  - [Структура роли](#структура-роли)
  - [Создание роли](#создание-роли)
  - [Использование ролей](#использование-ролей)
- [Templates (Jinja2)](#templates-jinja2)
  - [Основы шаблонизации](#основы-шаблонизации)
  - [Переменные и факты](#переменные-и-факты)
- [Ansible Vault](#ansible-vault)
  - [Шифрование секретов](#шифрование-секретов)
  - [Использование vault](#использование-vault)
- [Ansible Tower / AWX](#ansible-tower-awx)
  - [Установка AWX](#установка-awx)
  - [Job Templates](#job-templates)
- [Тестирование и валидация](#тестирование-и-валидация)
  - [Molecule для тестирования ролей](#molecule-для-тестирования-ролей)
  - [Синтаксическая валидация](#синтаксическая-валидация)
- [Лучшие практики](#лучшие-практики)
  - [Организация структуры](#организация-структуры)
  - [Безопасность](#безопасность)
  - [Производительность](#производительность)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [Отладка](#отладка)
- [См. также](#см-также-1)

## Основы Ansible

### Архитектура Ansible

Краткое описание компонентов **Ansible**: **control node**, **managed nodes**, **inventory**, **playbooks**, модули, плагины, **roles**.

```yaml
# Основные компоненты:
# - Control Node: машина с установленным Ansible
# - Managed Nodes: целевые машины для управления
# - Inventory: список управляемых хостов
# - Playbooks: описания автоматизации (YAML)
# - Modules: исполняемые единицы (команды, скрипты)
# - Plugins: расширения функциональности
# - Roles: переиспользуемые компоненты
```

### Установка

#### Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install ansible

# CentOS/RHEL
sudo yum install ansible
# или
sudo dnf install ansible

# Fedora
sudo dnf install ansible

# Arch Linux
sudo pacman -S ansible

# Через pip
pip install ansible
```

#### macOS
```bash
# Через Homebrew
brew install ansible

# Через pip
pip install ansible
```

#### Windows
```bash
# Через WSL
# или через pip в virtualenv
pip install ansible

# Через Chocolatey
choco install ansible
```

### Базовая конфигурация
```ini
# /etc/ansible/ansible.cfg
[defaults]
inventory = /etc/ansible/hosts
remote_user = ansible
ask_pass = false
ask_sudo_pass = false

host_key_checking = false
gathering = smart
fact_caching = memory

# Retry files
retry_files_enabled = true
retry_files_save_path = ~/.ansible-retry

# Logging
log_path = /var/log/ansible.log

[privilege_escalation]
become = true
become_method = sudo
become_user = root
become_ask_pass = false

[ssh_connection]
ssh_args = -o ControlMaster=auto -o ControlPersist=60s -o StrictHostKeyChecking=no
control_path_dir = /tmp/.ansible-cp
pipelining = true
```

## Inventory

### Статический inventory
```ini
# /etc/ansible/hosts
[webservers]
web1.example.com ansible_user=ubuntu ansible_ssh_private_key_file=/home/user/.ssh/id_rsa
web2.example.com ansible_user=ubuntu ansible_ssh_private_key_file=/home/user/.ssh/id_rsa

[databases]
db1.example.com ansible_user=centos
db2.example.com ansible_user=centos

[loadbalancers]
lb1.example.com

# Группы могут содержать другие группы
[production:children]
webservers
databases
loadbalancers

# Переменные для групп
[webservers:vars]
ansible_python_interpreter=/usr/bin/python3
http_port=80
max_clients=200

[databases:vars]
db_port=5432
db_name=myapp
```

### Динамический inventory
```python
#!/usr/bin/env python3
# dynamic_inventory.py
import json
import sys

def get_inventory():
    return {
        "_meta": {
            "hostvars": {
                "web1.example.com": {
                    "ansible_user": "ubuntu",
                    "ansible_ssh_private_key_file": "/home/user/.ssh/web_key"
                },
                "web2.example.com": {
                    "ansible_user": "ubuntu",
                    "ansible_ssh_private_key_file": "/home/user/.ssh/web_key"
                }
            }
        },
        "webservers": {
            "hosts": ["web1.example.com", "web2.example.com"],
            "vars": {
                "env": "production",
                "app_version": "1.2.3"
            }
        },
        "databases": {
            "hosts": ["db1.example.com", "db2.example.com"]
        },
        "production": {
            "children": ["webservers", "databases"]
        }
    }

if __name__ == "__main__":
    if len(sys.argv) == 2 and sys.argv[1] == "--list":
        print(json.dumps(get_inventory()))
    elif len(sys.argv) == 3 and sys.argv[1] == "--host":
        print(json.dumps({}))
    else:
        print("Usage: {} --list or {} --host <hostname>".format(sys.argv[0], sys.argv[0]))
```

### AWS EC2 динамический inventory
```python
#!/usr/bin/env python3
# aws_ec2_inventory.py
import boto3
import json
from collections import defaultdict

def get_instances():
    ec2 = boto3.client('ec2', region_name='us-east-1')

    response = ec2.describe_instances(
        Filters=[
            {'Name': 'instance-state-name', 'Values': ['running']}
        ]
    )

    inventory = defaultdict(lambda: {'hosts': [], 'vars': {}})
    hostvars = {}

    for reservation in response['Reservations']:
        for instance in reservation['Instances']:
            instance_id = instance['InstanceId']
            public_ip = instance.get('PublicIpAddress')
            private_ip = instance.get('PrivateIpAddress')

            if not public_ip and not private_ip:
                continue

            # Определение группы по тегам
            group_name = 'ungrouped'
            for tag in instance.get('Tags', []):
                if tag['Key'] == 'Environment':
                    group_name = tag['Value']
                elif tag['Key'] == 'Name':
                    hostname = tag['Value']

            ip = public_ip or private_ip
            inventory[group_name]['hosts'].append(ip)

            hostvars[ip] = {
                'ansible_host': ip,
                'instance_id': instance_id,
                'instance_type': instance['InstanceType'],
                'availability_zone': instance['Placement']['AvailabilityZone']
            }

            # Добавление тегов как переменных
            for tag in instance.get('Tags', []):
                hostvars[ip][f"ec2_tag_{tag['Key'].lower()}"] = tag['Value']

    inventory['_meta'] = {'hostvars': hostvars}
    return inventory

if __name__ == "__main__":
    print(json.dumps(get_instances(), indent=2))
```

## Playbooks

### Структура playbook
```yaml
---
# web-server.yml
- name: Configure web servers
  hosts: webservers
  become: true
  vars:
    http_port: 80
    https_port: 443
    app_user: webapp
    app_dir: /opt/myapp

  pre_tasks:
    - name: Update package cache
      apt:
        update_cache: yes
      when: ansible_os_family == "Debian"

    - name: Install EPEL on CentOS
      yum:
        name: epel-release
        state: present
      when: ansible_os_family == "RedHat"

  roles:
    - common
    - nginx
    - nodejs
    - myapp

  post_tasks:
    - name: Verify services
      service:
        name: "{{ item }}"
        state: started
        enabled: yes
      loop:
        - nginx
        - myapp

  handlers:
    - name: restart nginx
      service:
        name: nginx
        state: restarted

    - name: restart myapp
      service:
        name: myapp
        state: restarted
```

### Основные модули

#### Package management
```yaml
---
- name: Install packages
  hosts: all
  become: true
  tasks:
    - name: Install Apache on Ubuntu
      apt:
        name: apache2
        state: present
      when: ansible_os_family == "Debian"

    - name: Install httpd on CentOS
      yum:
        name: httpd
        state: present
      when: ansible_os_family == "RedHat"

    - name: Install multiple packages
      package:
        name:
          - curl
          - wget
          - vim
        state: present

    - name: Remove package
      apt:
        name: apache2
        state: absent
      when: ansible_os_family == "Debian"
```

#### File operations
```yaml
---
- name: File operations
  hosts: webservers
  become: true
  tasks:
    - name: Create directory
      file:
        path: /opt/myapp
        state: directory
        owner: webapp
        group: webapp
        mode: '0755'

    - name: Copy file
      copy:
        src: files/app.jar
        dest: /opt/myapp/app.jar
        owner: webapp
        group: webapp
        mode: '0644'

    - name: Copy directory
      copy:
        src: files/config/
        dest: /etc/myapp/
        owner: webapp
        group: webapp
        mode: '0644'

    - name: Template configuration
      template:
        src: templates/app.conf.j2
        dest: /etc/myapp/app.conf
        owner: webapp
        group: webapp
        mode: '0644'
      notify: restart myapp

    - name: Download file
      get_url:
        url: https://example.com/app.jar
        dest: /opt/myapp/app.jar
        checksum: sha256:abc123...
```

#### Service management
```yaml
---
- name: Manage services
  hosts: webservers
  become: true
  tasks:
    - name: Start service
      service:
        name: nginx
        state: started

    - name: Enable service
      service:
        name: nginx
        enabled: yes

    - name: Restart service
      service:
        name: nginx
        state: restarted

    - name: Reload service
      service:
        name: nginx
        state: reloaded

    - name: Stop service
      service:
        name: nginx
        state: stopped

    - name: Ensure service is running
      systemd:
        name: myapp
        state: started
        enabled: yes
        daemon_reload: yes
```

#### User management
```yaml
---
- name: Manage users
  hosts: webservers
  become: true
  tasks:
    - name: Create user
      user:
        name: webapp
        comment: Web Application User
        shell: /bin/bash
        create_home: yes
        home: /home/webapp
        group: webapp
        groups: docker
        append: yes

    - name: Set authorized key
      authorized_key:
        user: webapp
        state: present
        key: "{{ lookup('file', 'keys/webapp.pub') }}"

    - name: Create SSH directory
      file:
        path: /home/webapp/.ssh
        state: directory
        owner: webapp
        group: webapp
        mode: '0700'

    - name: Copy SSH key
      copy:
        src: keys/webapp
        dest: /home/webapp/.ssh/id_rsa
        owner: webapp
        group: webapp
        mode: '0600'
```

## Roles

### Структура роли
```text
roles/
├── common/
│   ├── tasks/
│   │   └── main.yml
│   ├── handlers/
│   │   └── main.yml
│   ├── vars/
│   │   └── main.yml
│   ├── defaults/
│   │   └── main.yml
│   ├── files/
│   ├── templates/
│   ├── meta/
│   │   └── main.yml
│   └── README.md
├── nginx/
│   ├── tasks/
│   │   ├── install.yml
│   │   ├── configure.yml
│   │   └── main.yml
│   ├── handlers/
│   │   └── main.yml
│   ├── vars/
│   │   └── main.yml
│   ├── defaults/
│   │   └── main.yml
│   ├── templates/
│   │   ├── nginx.conf.j2
│   │   └── site.conf.j2
│   ├── files/
│   │   └── ssl/
│   ├── meta/
│   │   └── main.yml
│   └── README.md
```

### Создание роли
```yaml
# roles/common/tasks/main.yml
---
- name: Update package cache
  package:
    update_cache: yes
  when: ansible_os_family == "Debian"

- name: Install common packages
  package:
    name:
      - curl
      - wget
      - vim
      - htop
    state: present

- name: Configure timezone
  timezone:
    name: "{{ timezone | default('UTC') }}"

- name: Create admin user
  user:
    name: admin
    shell: /bin/bash
    create_home: yes
    groups: sudo
    append: yes
```

```yaml
# roles/common/defaults/main.yml
---
timezone: UTC
admin_user: admin
common_packages:
  - curl
  - wget
  - vim
  - htop
```

```yaml
# roles/common/vars/main.yml
---
admin_password: "$6$rounds=4096$admin_salt$admin_hash"
```

```yaml
# roles/common/meta/main.yml
---
dependencies:
  - role: prerequisites
    when: ansible_os_family == "RedHat"
```

### Использование ролей
```yaml
---
# site.yml
- name: Configure all servers
  hosts: all
  roles:
    - common

- name: Configure web servers
  hosts: webservers
  roles:
    - role: nginx
      vars:
        nginx_port: 80
        nginx_ssl: false
    - role: nodejs
      node_version: "18"
    - role: myapp
      app_version: "1.2.3"
      app_port: 3000
```

## Templates (Jinja2)

### Основы шаблонизации
```jinja2
{# templates/nginx.conf.j2 #}
user {{ nginx_user | default('www-data') }};
worker_processes {{ nginx_worker_processes | default('auto') }};
pid /run/nginx.pid;

events {
    worker_connections {{ nginx_worker_connections | default(1024) }};
    use epoll;
    multi_accept on;
}

http {
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # SSL settings
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # Logging
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    # Virtual hosts
    {% for site in nginx_sites %}
    server {
        listen {{ site.port | default(80) }};
        server_name {{ site.server_name }};

        location / {
            proxy_pass http://{{ site.upstream }};
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        {% if site.ssl | default(false) %}
        listen 443 ssl http2;
        ssl_certificate {{ site.ssl_cert }};
        ssl_certificate_key {{ site.ssl_key }};
        {% endif %}
    }
    {% endfor %}
}
```

### Переменные и факты
```yaml
# group_vars/webservers.yml
---
nginx_user: www-data
nginx_worker_processes: auto
nginx_worker_connections: 1024

nginx_sites:
  - server_name: api.example.com
    port: 80
    upstream: "127.0.0.1:8080"
    ssl: true
    ssl_cert: /etc/ssl/certs/api.example.com.crt
    ssl_key: /etc/ssl/private/api.example.com.key

  - server_name: app.example.com
    port: 80
    upstream: "127.0.0.1:3000"

# Использование фактов
- name: Configure based on facts
  hosts: all
  tasks:
    - name: Show system facts
      debug:
        msg: |
          OS: {{ ansible_distribution }} {{ ansible_distribution_version }}
          CPUs: {{ ansible_processor_count }}
          Memory: {{ ansible_memory_mb.real.total }}MB

    - name: Configure based on OS
      include_tasks: "{{ ansible_os_family | lower }}.yml"
```

## Ansible Vault

### Шифрование секретов
```bash
# Создание encrypted файла
ansible-vault create secrets.yml

# Редактирование encrypted файла
ansible-vault edit secrets.yml

# Шифрование существующего файла
ansible-vault encrypt secrets.yml

# Расшифровка файла
ansible-vault decrypt secrets.yml

# Просмотр encrypted файла
ansible-vault view secrets.yml

# Смена пароля
ansible-vault rekey secrets.yml
```

### Использование vault
```yaml
# secrets.yml (encrypted)
---
database:
  host: db.example.com
  port: 5432
  name: myapp
  user: app_user
  password: super_secret_password

aws:
  access_key: AKIXXXXXXXXXXXXXXXXX
  secret_key: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

ssl:
  cert: |
    -----BEGIN CERTIFICATE-----
    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...
    -----END CERTIFICATE-----
  key: |
    -----BEGIN PRIVATE KEY-----
    MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...
    -----END PRIVATE KEY-----
```

```yaml
# playbook.yml
---
- name: Deploy application
  hosts: webservers
  vars_files:
    - secrets.yml

  tasks:
    - name: Configure database connection
      template:
        src: templates/app-config.properties.j2
        dest: /etc/myapp/config.properties
      vars:
        db_host: "{{ database.host }}"
        db_port: "{{ database.port }}"
        db_name: "{{ database.name }}"
        db_user: "{{ database.user }}"
        db_password: "{{ database.password }}"

    - name: Configure AWS credentials
      template:
        src: templates/aws-credentials.j2
        dest: /home/appuser/.aws/credentials
        owner: appuser
        group: appuser
        mode: '0600'
      vars:
        aws_access_key: "{{ aws.access_key }}"
        aws_secret_key: "{{ aws.secret_key }}"

    - name: Install SSL certificate
      copy:
        content: "{{ ssl.cert }}"
        dest: /etc/ssl/certs/myapp.crt
        mode: '0644'
      notify: reload nginx

    - name: Install SSL private key
      copy:
        content: "{{ ssl.key }}"
        dest: /etc/ssl/private/myapp.key
        mode: '0600'
      notify: reload nginx
```

## Ansible Tower / AWX

### Установка AWX
```bash
# Установка через Docker Compose
git clone https://github.com/ansible/awx.git
cd awx
git checkout 21.10.0

# Настройка inventory
cp installer/inventory.example installer/inventory
# Редактировать inventory файл

# Запуск установки
ansible-playbook -i installer/inventory installer/install.yml
```

### Job Templates
```yaml
# job_template.yml
---
name: "Deploy Web Application"
description: "Deploy web application to target servers"
job_type: "run"
inventory: "Web Servers"
project: "web-app-deploy"
playbook: "deploy.yml"

credentials:
  - "SSH Key"
  - "Vault Password"

extra_vars:
  app_version: "1.2.3"
  environment: "production"

survey_enabled: true
survey_spec:
  name: "Deployment Parameters"
  description: "Parameters for application deployment"
  spec:
    - question_name: "App Version"
      question_description: "Version of application to deploy"
      required: true
      type: "text"
      variable: "app_version"
    - question_name: "Environment"
      question_description: "Target environment"
      required: true
      type: "multiplechoice"
      variable: "environment"
      choices:
        - "development"
        - "staging"
        - "production"
```

## Тестирование и валидация

### Molecule для тестирования ролей
```yaml
# molecule/default/molecule.yml
---
dependency:
  name: galaxy
driver:
  name: docker
platforms:
  - name: instance
    image: geerlingguy/docker-ubuntu2004-ansible:latest
    command: ${MOLECULE_DOCKER_COMMAND:-""}
    volumes:
      - /sys/fs/cgroup:/sys/fs/cgroup:ro
    privileged: true
    pre_build_image: true
provisioner:
  name: ansible
verifier:
  name: ansible
```

```yaml
# molecule/default/playbook.yml
---
- name: Converge
  hosts: all
  roles:
    - role: nginx
      vars:
        nginx_port: 80
        nginx_user: www-data
```

```yaml
# molecule/default/verify.yml
---
- name: Verify
  hosts: all
  tasks:
    - name: Check nginx service
      service:
        name: nginx
        state: started
      register: nginx_status

    - name: Verify nginx is running
      assert:
        that:
          - nginx_status.state == "started"

    - name: Check nginx port
      wait_for:
        port: 80
        timeout: 10

    - name: Verify nginx configuration
      command: nginx -t
      register: nginx_test
      failed_when: nginx_test.rc != 0
```

### Синтаксическая валидация
```bash
# Проверка синтаксиса playbook
ansible-playbook --syntax-check playbook.yml

# Проверка на lint
ansible-lint playbook.yml

# Dry run
ansible-playbook --check playbook.yml

# Limit execution to specific hosts
ansible-playbook --limit webservers playbook.yml

# Step by step execution
ansible-playbook --step playbook.yml
```

## Лучшие практики

### Организация структуры
```bash
# Рекомендуемая структура проекта
ansible-project/
├── inventory/
│   ├── hosts.ini
│   ├── group_vars/
│   │   ├── all.yml
│   │   ├── webservers.yml
│   │   └── databases.yml
│   └── host_vars/
│       ├── web01.yml
│       └── db01.yml
├── playbooks/
│   ├── site.yml
│   ├── webservers.yml
│   └── databases.yml
├── roles/
│   ├── common/
│   ├── nginx/
│   ├── postgresql/
│   └── monitoring/
├── collections/
│   └── requirements.yml
├── library/
│   └── custom_modules/
├── filter_plugins/
│   └── custom_filters.py
├── ansible.cfg
├── requirements.txt
└── README.md
```

### Безопасность
```yaml
# ansible.cfg
[defaults]
vault_password_file = ~/.ansible/vault_pass
host_key_checking = true
sudo_user = root
timeout = 30

[ssh_connection]
ssh_args = -o ControlMaster=auto -o ControlPersist=60s -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no
control_path_dir = ~/.ansible/cp

# Ограничение прав на inventory
---
- name: Secure inventory
  hosts: all
  tasks:
    - name: Restrict inventory file permissions
      file:
        path: /etc/ansible/hosts
        owner: ansible
        group: ansible
        mode: '0600'
      delegate_to: localhost

# Использование become для privilege escalation
---
- name: Install packages with sudo
  hosts: webservers
  become: true
  become_method: sudo
  become_user: root
  tasks:
    - name: Install nginx
      package:
        name: nginx
        state: present
```

### Производительность
```yaml
# ansible.cfg для производительности
[defaults]
forks = 50
gathering = smart
fact_caching = jsonfile
fact_caching_connection = /tmp/ansible_facts
fact_caching_timeout = 86400

[ssh_connection]
pipelining = true
control_path_dir = ~/.ansible/cp
control_master = auto
control_persist = 60s

# Оптимизация playbook
---
- name: Optimized deployment
  hosts: webservers
  strategy: free  # Параллельное выполнение
  serial: 10      # Ограничение на количество хостов
  max_fail_percentage: 20

  tasks:
    - name: Update cache (with cache)
      apt:
        update_cache: yes
        cache_valid_time: 3600
      when: ansible_os_family == "Debian"

    - name: Install packages
      package:
        name: "{{ common_packages }}"
        state: present
      async: 600  # Асинхронное выполнение
      poll: 10    # Проверка каждые 10 секунд
```

## Решение проблем

### Распространенные проблемы
```bash
# Проблема: SSH connection failed
# Решение: Проверить SSH ключи и known_hosts
ansible all -m ping --private-key=~/.ssh/ansible_key

# Проблема: Permission denied
# Решение: Использовать become
ansible webservers -m command -a "whoami" --become --become-user=root

# Проблема: Task hangs
# Решение: Установить timeout
ansible webservers -m shell -a "sleep 300" --timeout=30

# Проблема: Module not found
# Решение: Проверить установку модуля
ansible-doc <module_name>

# Проблема: Facts gathering slow
# Решение: Отключить или оптимизировать gathering
- hosts: all
  gather_facts: false
  tasks:
    - name: Manually gather required facts
      setup:
        filter: ansible_distribution*

# Проблема: Playbook fails on first error
# Решение: Использовать ignore_errors или failed_when
- name: Try to install package
  package:
    name: nonexistent-package
    state: present
  ignore_errors: yes
  register: install_result

- name: Check result
  debug:
    msg: "Installation failed but continuing"
  when: install_result.failed
```

### Отладка
```yaml
# Отладка playbook
---
- name: Debug playbook
  hosts: webservers
  tasks:
    - name: Show all variables
      debug:
        var: hostvars[inventory_hostname]
        verbosity: 2

    - name: Show facts
      debug:
        var: ansible_facts
      when: ansible_facts is defined

    - name: Show registered variables
      debug:
        var: result
      when: result is defined

    - name: Pause execution
      pause:
        prompt: "Continue? (yes/no)"
      register: pause_result
      when: debug_mode | default(false)

    - name: Conditional task execution
      command: echo "This will only run in debug mode"
      when: debug_mode | default(false)
```

```bash
# Verbose output
ansible-playbook -v playbook.yml      # -v для verbose
ansible-playbook -vv playbook.yml     # -vv для более подробного
ansible-playbook -vvv playbook.yml    # -vvv для debug

# Debug specific task
ansible-playbook --start-at-task="Install nginx" playbook.yml

# Syntax check
ansible-playbook --syntax-check playbook.yml

# List tasks
ansible-playbook --list-tasks playbook.yml

# List hosts
ansible-playbook --list-hosts playbook.yml
```
## См. также
- [[ansible-advanced|Ansible Advanced]] — продвинутые паттерны Ansible
- [[terraform|Terraform]] — инфраструктура как код
- [[docker-basics|Docker]] — контейнеризация
- [[kubernetes-basics|Kubernetes]] — оркестрация контейнеров
