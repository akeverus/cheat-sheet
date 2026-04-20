---
title: "Ansible Advanced"
description: "Ansible - это инструмент автоматизации ИТ-инфраструктуры с открытым исходным кодом, который позволяет автоматизировать конфигурацию, развертывание и управление серверами. Этот документ охватывает продвинутые концепции, паттерны и best practices для enterprise-grade автоматизации."
tags:
  - platform
  - iac
  - ansible-advanced
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Ansible Advanced**

**Ansible** - это инструмент автоматизации ИТ-инфраструктуры с открытым исходным кодом, который позволяет автоматизировать конфигурацию, развертывание и управление серверами. Этот документ охватывает продвинутые концепции, паттерны и **best practices** для **enterprise-grade** автоматизации.

## Полезные ссылки
- [Ansible Documentation](https://docs.ansible.com/)
- [Ansible Galaxy](https://galaxy.ansible.com/)
- [Molecule](https://molecule.readthedocs.io/)
- [Ansible Lint](https://ansible.readthedocs.io/projects/lint/)
- [AWX / Ansible Tower](https://github.com/ansible/awx)

## Содержание

- [Продвинутая архитектура Ansible](#продвинутая-архитектура-ansible)
  - [Структура enterprise проекта](#структура-enterprise-проекта)
  - [Конфигурация Ansible](#конфигурация-ansible)
- [Продвинутые инвентари](#продвинутые-инвентари)
  - [Динамические инвентари](#динамические-инвентари)
  - [Переменные и факты](#переменные-и-факты)
- [Продвинутые роли](#продвинутые-роли)
  - [Композитные роли](#композитные-роли)
  - [Условная логика в ролях](#условная-логика-в-ролях)
- [Продвинутые playbooks](#продвинутые-playbooks)
  - [Многофазные развертывания](#многофазные-развертывания)
  - [Канарские развертывания](#канарские-развертывания)
- [Продвинутые модули и плагины](#продвинутые-модули-и-плагины)
  - [Кастомные модули](#кастомные-модули)
  - [Кастомные фильтры](#кастомные-фильтры)
- [Testing и validation](#testing-и-validation)
  - [Molecule для тестирования ролей](#molecule-для-тестирования-ролей)
  - [Ansible Lint и проверка качества](#ansible-lint-и-проверка-качества)
- [CI/CD интеграция](#cicd-интеграция)
  - [Jenkins Pipeline](#jenkins-pipeline)
  - [GitHub Actions](#github-actions)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Оптимизация производительности](#оптимизация-производительности)
  - [Параллельное выполнение](#параллельное-выполнение)
- [Лучшие практики](#лучшие-практики)
  - [Enterprise patterns](#enterprise-patterns)
  - [Безопасность](#безопасность)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также)

## Продвинутая архитектура **Ansible**

### Структура **enterprise** проекта
Ниже — структура каталогов **enterprise Ansible**-проекта (**текст**).
```text
ansible-enterprise/
├── ansible.cfg                    # Конфигурация Ansible
├── inventory/                     # Инвентари
│   ├── production/
│   │   ├── group_vars/
│   │   │   ├── all.yml
│   │   │   ├── webservers.yml
│   │   │   └── databases.yml
│   │   ├── host_vars/
│   │   │   ├── web01.yml
│   │   │   └── web02.yml
│   │   └── inventory.ini
│   ├── staging/
│   │   ├── group_vars/
│   │   ├── host_vars/
│   │   └── inventory.ini
│   └── dynamic/
│       ├── aws_ec2.yml
│       ├── azure_rm.yml
│       └── gcp_compute.yml
├── playbooks/                     # Playbooks
│   ├── common/
│   │   ├── tasks/
│   │   │   ├── hardening.yml
│   │   │   ├── monitoring.yml
│   │   │   └── backup.yml
│   │   └── handlers/
│   │       └── main.yml
│   ├── web/
│   │   ├── apache.yml
│   │   ├── nginx.yml
│   │   └── load_balancer.yml
│   ├── database/
│   │   ├── postgresql.yml
│   │   ├── mysql.yml
│   │   └── mongodb.yml
│   ├── monitoring/
│   │   ├── prometheus.yml
│   │   ├── grafana.yml
│   │   └── alerting.yml
│   └── deployment/
│       ├── blue_green.yml
│       ├── canary.yml
│       └── rollback.yml
├── roles/                         # Роли
│   ├── common/
│   │   ├── defaults/
│   │   ├── files/
│   │   ├── handlers/
│   │   ├── tasks/
│   │   ├── templates/
│   │   ├── vars/
│   │   └── meta/
│   ├── java_app/
│   ├── nginx/
│   ├── postgresql/
│   └── monitoring/
├── collections/                   # Коллекции
│   ├── ansible.posix/
│   ├── community.general/
│   ├── community.kubernetes/
│   └── custom.enterprise/
├── plugins/                       # Плагины
│   ├── action/
│   ├── callback/
│   ├── filter/
│   ├── lookup/
│   └── modules/
├── library/                       # Кастомные модули
├── filter_plugins/                # Фильтры
├── tests/                         # Тесты
│   ├── integration/
│   ├── unit/
│   └── molecule/
├── docs/                          # Документация
├── scripts/                       # Скрипты
│   ├── deploy.sh
│   ├── rollback.sh
│   └── validate.sh
├── requirements.yml               # Зависимости
├── pyproject.toml                 # Python зависимости
├── .ansible-lint                  # Конфигурация линтера
├── .yamllint                      # Конфигурация YAML линтера
└── Makefile                       # Make команды
```

### Конфигурация **Ansible**
```ini
# ansible.cfg
[defaults]
inventory           = ./inventory
remote_user         = ansible
private_key_file    = ~/.ssh/id_rsa
host_key_checking   = False
retry_files_enabled = False
log_path           = ./logs/ansible.log
forks              = 50
poll_interval      = 15
timeout            = 30
gathering          = smart
fact_caching       = jsonfile
fact_caching_connection = ./cache
fact_caching_timeout = 86400

[inventory]
enable_plugins = aws_ec2, azure_rm, gcp_compute, yaml, ini

[ssh_connection]
ssh_args = -o ControlMaster=auto -o ControlPersist=60s -o ControlPath=/tmp/ansible-ssh-%h-%p-%r
pipelining = True
control_path_dir = /tmp/.ansible-cp

[accelerate]
accelerate_port = 5099
accelerate_timeout = 30
accelerate_connect_timeout = 5.0

[paramiko_connection]
record_host_keys = False

[colors]
verbose = blue
warn = bright purple
error = red
debug = dark gray
deprecate = purple
skip = cyan
unreachable = red
ok = green
changed = yellow
diff_add = green
diff_remove = red
diff_lines = cyan
```

## Продвинутые инвентари

### Динамические инвентари
```yaml
# inventory/dynamic/aws_ec2.yml
plugin: aws_ec2
regions:
  - us-east-1
  - us-west-2
filters:
  tag:Environment: production
  tag:Application: web
  instance-state-name: running
keyed_groups:
  - key: tags.Environment
    prefix: env
  - key: tags.Application
    prefix: app
  - key: tags.Role
    prefix: role
  - key: placement.region
    prefix: aws_region
  - key: platform
    prefix: os
hostnames:
  - tag:Name
  - private-ip-address
  - public-ip-address
compose:
  ansible_host: public_ip_address
  ansible_user: admin
  ansible_ssh_private_key_file: ~/.ssh/id_rsa
  region: placement.region
```

```yaml
# inventory/dynamic/azure_rm.yml
plugin: azure_rm
include_vm_resource_groups:
  - my-resource-group
filters:
  - tag.Environment: production
  - tag.Application: web
keyed_groups:
  - key: tags.Environment
    prefix: env
  - key: tags.Application
    prefix: app
  - key: location
    prefix: azure
hostnames:
  - name
  - public_ip_address
compose:
  ansible_host: public_ip_address
  ansible_user: azureuser
```

```python
# plugins/inventory/custom_inventory.py
#!/usr/bin/env python3

import json
import sys
from typing import Dict, List, Any

class InventoryModule:
    NAME = 'custom_inventory'

    def __init__(self):
        self.inventory = {}
        self.read_cli_args()

    def read_cli_args(self):
        # Parse command line arguments
        pass

    def parse(self, inventory, loader, path, cache=True):
        # Parse custom inventory source
        self.inventory = self._get_inventory()

        # Add hosts
        for host, hostvars in self.inventory.get('hosts', {}).items():
            self.inventory.add_host(host)
            for key, value in hostvars.items():
                self.inventory.set_variable(host, key, value)

        # Add groups
        for group, groupvars in self.inventory.get('groups', {}).items():
            self.inventory.add_group(group)
            for key, value in groupvars.items():
                self.inventory.set_variable(group, key, value)

    def _get_inventory(self) -> Dict[str, Any]:
        # Custom logic to fetch inventory
        # Could be from database, API, etc.
        return {
            'hosts': {
                'web01': {
                    'ansible_host': '10.0.1.10',
                    'ansible_user': 'admin',
                    'role': 'web'
                },
                'web02': {
                    'ansible_host': '10.0.1.11',
                    'ansible_user': 'admin',
                    'role': 'web'
                }
            },
            'groups': {
                'webservers': {
                    'hosts': ['web01', 'web02'],
                    'vars': {
                        'http_port': 80,
                        'max_clients': 200
                    }
                }
            }
        }

def main():
    inventory = InventoryModule()
    inventory.parse(None, None, None)

if __name__ == '__main__':
    main()
```

### Переменные и факты
```yaml
# inventory/production/group_vars/all.yml
---
# Global variables for all hosts
company_name: "Enterprise Corp"
environment: production
datacenter: aws-us-east-1

# Network configuration
domain_name: "enterprise.com"
dns_servers:
  - 8.8.8.8
  - 8.8.4.4

# Security
security_level: high
firewall_enabled: true
selinux_mode: enforcing

# Monitoring
monitoring_enabled: true
log_level: INFO
metrics_interval: 60

# Backup
backup_enabled: true
backup_retention_days: 30
backup_schedule: "0 2 * * *"

# Application settings
app_name: "enterprise-app"
app_version: "{{ lookup('env', 'APP_VERSION') | default('latest', true) }}"
app_port: 8080

# Database settings
db_host: "{{ groups['databases'][0] }}"
db_port: 5432
db_name: enterprise_db

# Cache settings
redis_host: "{{ groups['redis'][0] }}"
redis_port: 6379

# Load balancer
lb_algorithm: least_conn
lb_health_check:
  interval: 30
  timeout: 10
  unhealthy_threshold: 3
  healthy_threshold: 2
```

```yaml
# inventory/production/group_vars/webservers.yml
---
# Web server specific variables
nginx_version: "1.20.0"
nginx_worker_processes: "{{ ansible_processor_vcpus }}"
nginx_worker_connections: 1024

# SSL configuration
ssl_enabled: true
ssl_certificate: "/etc/ssl/certs/{{ domain_name }}.crt"
ssl_certificate_key: "/etc/ssl/private/{{ domain_name }}.key"
ssl_protocols: "TLSv1.2 TLSv1.3"

# PHP configuration (if applicable)
php_version: "8.1"
php_memory_limit: "256M"
php_max_execution_time: "30"

# Application deployment
app_user: "www-data"
app_group: "www-data"
app_root: "/var/www/{{ app_name }}"
app_logs: "/var/log/{{ app_name }}"

# Reverse proxy settings
proxy_read_timeout: 300
proxy_connect_timeout: 60
proxy_send_timeout: 60

# Rate limiting
rate_limit_requests: 100
rate_limit_burst: 200
rate_limit_window: 60

# Security headers
security_headers:
  - "X-Frame-Options: DENY"
  - "X-Content-Type-Options: nosniff"
  - "X-XSS-Protection: 1; mode=block"
  - "Strict-Transport-Security: max-age=31536000; includeSubDomains"
```

## Продвинутые роли

### Композитные роли
```yaml
# roles/java_app/meta/main.yml
---
dependencies:
  - role: common
    tags: [common]
  - role: java
    java_version: "{{ java_app_java_version | default('17') }}"
    tags: [java]
  - role: monitoring
    monitoring_type: "{{ java_app_monitoring | default('prometheus') }}"
    tags: [monitoring]
  - role: security
    security_level: "{{ java_app_security_level | default('high') }}"
    tags: [security]
```

```yaml
# roles/java_app/defaults/main.yml
---
# Java application defaults
java_app_name: "{{ app_name | default('java-app') }}"
java_app_version: "{{ app_version | default('latest') }}"
java_app_port: "{{ app_port | default(8080) }}"
java_app_java_version: "{{ java_version | default('17') }}"
java_app_heap_size: "{{ java_heap_size | default('1g') }}"
java_app_user: "{{ app_user | default('java-app') }}"
java_app_group: "{{ app_group | default('java-app') }}"
java_app_install_dir: "{{ app_install_dir | default('/opt') }}"
java_app_config_dir: "{{ app_config_dir | default('/etc/java-app') }}"
java_app_log_dir: "{{ app_log_dir | default('/var/log/java-app') }}"
java_app_data_dir: "{{ app_data_dir | default('/var/lib/java-app') }}"

# JVM settings
java_app_jvm_opts:
  - "-Xms{{ java_app_heap_size }}"
  - "-Xmx{{ java_app_heap_size }}"
  - "-XX:+UseG1GC"
  - "-XX:MaxGCPauseMillis=200"
  - "-XX:+PrintGCDetails"
  - "-XX:+PrintGCTimeStamps"
  - "-Xloggc:{{ java_app_log_dir }}/gc.log"

# Monitoring
java_app_monitoring: prometheus
java_app_metrics_port: "{{ java_app_port + 1 }}"

# Security
java_app_security_level: high
java_app_ssl_enabled: "{{ ssl_enabled | default(false) }}"
java_app_auth_enabled: true

# Database
java_app_db_url: "{{ db_url | default('jdbc:postgresql://localhost:5432/app') }}"
java_app_db_username: "{{ db_username | default('app') }}"
java_app_db_password: "{{ db_password }}"

# Cache
java_app_redis_host: "{{ redis_host | default('localhost') }}"
java_app_redis_port: "{{ redis_port | default(6379) }}"
```

### Условная логика в ролях
```yaml
# roles/java_app/tasks/main.yml
---
- name: Include OS-specific variables
  include_vars: "{{ item }}"
  with_first_found:
    - "{{ ansible_distribution | lower }}-{{ ansible_distribution_version | lower }}.yml"
    - "{{ ansible_distribution | lower }}-{{ ansible_distribution_major_version }}.yml"
    - "{{ ansible_distribution | lower }}.yml"
    - default.yml
  tags: [vars]

- name: Create application user
  user:
    name: "{{ java_app_user }}"
    group: "{{ java_app_group }}"
    system: yes
    shell: /sbin/nologin
    create_home: no
    state: present
  when: java_app_user != 'root'
  tags: [user]

- name: Create application directories
  file:
    path: "{{ item }}"
    state: directory
    owner: "{{ java_app_user }}"
    group: "{{ java_app_group }}"
    mode: '0755'
  loop:
    - "{{ java_app_install_dir }}"
    - "{{ java_app_config_dir }}"
    - "{{ java_app_log_dir }}"
    - "{{ java_app_data_dir }}"
  tags: [dirs]

- name: Download application artifact
  get_url:
    url: "{{ java_app_artifact_url }}"
    dest: "{{ java_app_install_dir }}/{{ java_app_name }}-{{ java_app_version }}.jar"
    owner: "{{ java_app_user }}"
    group: "{{ java_app_group }}"
    mode: '0644'
    checksum: "{{ java_app_artifact_checksum | default(omit) }}"
  register: download_result
  until: download_result is succeeded
  retries: 3
  delay: 10
  tags: [download]

- name: Create symbolic link to current version
  file:
    src: "{{ java_app_install_dir }}/{{ java_app_name }}-{{ java_app_version }}.jar"
    dest: "{{ java_app_install_dir }}/{{ java_app_name }}.jar"
    state: link
    owner: "{{ java_app_user }}"
    group: "{{ java_app_group }}"
  notify: restart java_app
  tags: [link]

- name: Create application configuration
  template:
    src: application.yml.j2
    dest: "{{ java_app_config_dir }}/application.yml"
    owner: "{{ java_app_user }}"
    group: "{{ java_app_group }}"
    mode: '0644'
    backup: yes
  notify: restart java_app
  tags: [config]

- name: Create systemd service
  template:
    src: systemd.service.j2
    dest: "/etc/systemd/system/{{ java_app_name }}.service"
    mode: '0644'
  notify: reload systemd
  tags: [service]

- name: Enable and start service
  systemd:
    name: "{{ java_app_name }}"
    enabled: yes
    state: started
    daemon_reload: yes
  tags: [service]

- name: Wait for application to be ready
  uri:
    url: "http://localhost:{{ java_app_port }}/actuator/health"
    status_code: 200
  register: health_check
  until: health_check.status == 200
  retries: 30
  delay: 10
  when: java_app_monitoring == 'spring-boot'
  tags: [healthcheck]

- name: Configure monitoring
  include_tasks: monitoring.yml
  when: java_app_monitoring is defined
  tags: [monitoring]

- name: Configure security
  include_tasks: security.yml
  when: java_app_security_level == 'high'
  tags: [security]
```

## Продвинутые **playbooks**

### Многофазные развертывания
```yaml
# playbooks/deployment/blue_green.yml
---
- name: Blue-Green Deployment
  hosts: load_balancers
  gather_facts: false
  vars:
    app_name: myapp
    blue_port: 8080
    green_port: 8081
    health_check_url: "/actuator/health"
    deployment_timeout: 300

  tasks:
    - name: Check current active environment
      uri:
        url: "http://{{ inventory_hostname }}:80{{ health_check_url }}"
        return_content: yes
      register: current_health
      failed_when: current_health.status != 200

    - name: Determine target environment
      set_fact:
        target_env: "{{ 'green' if (current_health.content | from_json).environment == 'blue' else 'blue' }}"
        target_port: "{{ green_port if (current_health.content | from_json).environment == 'blue' else blue_port }}"

    - name: Deploy to target environment
      include_role:
        name: java_app
        tasks_from: deploy
      vars:
        java_app_port: "{{ target_port }}"
        java_app_environment: "{{ target_env }}"
      delegate_to: "{{ groups[target_env][0] }}"

    - name: Health check target environment
      uri:
        url: "http://{{ groups[target_env][0] }}:{{ target_port }}{{ health_check_url }}"
        status_code: 200
      register: target_health
      until: target_health.status == 200
      retries: 30
      delay: 10
      timeout: "{{ deployment_timeout }}"

    - name: Switch load balancer to target environment
      uri:
        url: "http://{{ inventory_hostname }}/switch"
        method: POST
        body_format: json
        body:
          target: "{{ target_env }}"
          port: "{{ target_port }}"
      register: switch_result
      failed_when: switch_result.status != 200

    - name: Wait for traffic switch to complete
      pause:
        seconds: 30

    - name: Verify new environment is serving traffic
      uri:
        url: "http://{{ inventory_hostname }}:80{{ health_check_url }}"
        return_content: yes
      register: new_health
      failed_when: (new_health.content | from_json).environment != target_env

    - name: Shutdown old environment
      include_role:
        name: java_app
        tasks_from: shutdown
      vars:
        java_app_environment: "{{ 'blue' if target_env == 'green' else 'green' }}"
      delegate_to: "{{ groups['blue' if target_env == 'green' else 'green'][0] }}"

  handlers:
    - name: Notify deployment completion
      slack:
        token: "{{ slack_token }}"
        msg: "Blue-Green deployment completed: switched to {{ target_env }}"
        channel: "#deployments"
      when: switch_result is succeeded
```

### Канарские развертывания
```yaml
# playbooks/deployment/canary.yml
---
- name: Canary Deployment
  hosts: localhost
  connection: local
  gather_facts: false
  vars:
    app_name: myapp
    canary_percentage: 10
    monitoring_window: 300  # 5 minutes
    error_threshold: 5      # 5% error rate
    latency_threshold: 1000 # 1 second

  tasks:
    - name: Get current deployment info
      kubernetes.core.k8s_info:
        api_version: apps/v1
        kind: Deployment
        name: "{{ app_name }}"
        namespace: production
      register: current_deployment

    - name: Create canary deployment
      kubernetes.core.k8s:
        state: present
        definition:
          apiVersion: apps/v1
          kind: Deployment
          metadata:
            name: "{{ app_name }}-canary"
            namespace: production
            labels:
              app: "{{ app_name }}"
              version: canary
          spec:
            replicas: "{{ (current_deployment.resources[0].spec.replicas | int * canary_percentage / 100) | round | int }}"
            selector:
              matchLabels:
                app: "{{ app_name }}"
                version: canary
            template:
              metadata:
                labels:
                  app: "{{ app_name }}"
                  version: canary
              spec:
                containers:
                - name: app
                  image: "{{ app_name }}:{{ canary_version }}"
                  ports:
                  - containerPort: 8080

    - name: Wait for canary pods to be ready
      kubernetes.core.k8s_info:
        api_version: apps/v1
        kind: Deployment
        name: "{{ app_name }}-canary"
        namespace: production
      register: canary_deployment
      until: canary_deployment.resources[0].status.readyReplicas == canary_deployment.resources[0].spec.replicas
      retries: 30
      delay: 10

    - name: Monitor canary deployment
      include_tasks: monitor_canary.yml
      vars:
        monitoring_window: "{{ monitoring_window }}"
        error_threshold: "{{ error_threshold }}"
        latency_threshold: "{{ latency_threshold }}"

    - name: Decide deployment outcome
      set_fact:
        deployment_success: "{{ (error_rate | float) < error_threshold and (avg_latency | float) < latency_threshold }}"

    - name: Roll forward on success
      include_tasks: roll_forward.yml
      when: deployment_success

    - name: Roll back on failure
      include_tasks: roll_back.yml
      when: not deployment_success

  handlers:
    - name: Notify canary result
      slack:
        token: "{{ slack_token }}"
        msg: |
          Canary deployment {{ 'succeeded' if deployment_success else 'failed' }}
          Error rate: {{ error_rate }}%
          Avg latency: {{ avg_latency }}ms
        channel: "#deployments"
```

## Продвинутые модули и плагины

### Кастомные модули
```python
#!/usr/bin/env python3
# library/custom_module.py

import json
from ansible.module_utils.basic import AnsibleModule
from ansible.module_utils.urls import open_url

def main():
    module = AnsibleModule(
        argument_spec=dict(
            url=dict(type='str', required=True),
            method=dict(type='str', default='GET', choices=['GET', 'POST', 'PUT', 'DELETE']),
            body=dict(type='dict'),
            headers=dict(type='dict', default={}),
            timeout=dict(type='int', default=30),
            validate_certs=dict(type='bool', default=True),
            return_content=dict(type='bool', default=True),
        ),
        supports_check_mode=True
    )

    url = module.params['url']
    method = module.params['method']
    body = module.params.get('body')
    headers = module.params['headers']
    timeout = module.params['timeout']
    validate_certs = module.params['validate_certs']
    return_content = module.params['return_content']

    # Set default headers
    if 'Content-Type' not in headers:
        headers['Content-Type'] = 'application/json'

    # Convert body to JSON if it's a dict
    if isinstance(body, dict):
        body = json.dumps(body)
        headers['Content-Type'] = 'application/json'

    try:
        # Make HTTP request
        response = open_url(
            url,
            method=method,
            data=body,
            headers=headers,
            timeout=timeout,
            validate_certs=validate_certs
        )

        content = response.read().decode('utf-8') if return_content else None
        status_code = response.getcode()

        # Try to parse JSON response
        try:
            json_content = json.loads(content) if content else None
        except (ValueError, TypeError):
            json_content = None

        result = {
            'changed': method in ['POST', 'PUT', 'DELETE'],
            'status_code': status_code,
            'content': content,
            'json': json_content,
        }

        module.exit_json(result)

    except Exception as e:
        module.fail_json(msg=str(e))

if __name__ == '__main__':
    main()
```

### Кастомные фильтры
```python
# filter_plugins/custom_filters.py

import re
import hashlib
from datetime import datetime

def to_snake_case(value):
    """Convert camelCase to snake_case"""
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', value)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()

def hash_string(value, algorithm='sha256'):
    """Generate hash of a string"""
    if isinstance(value, str):
        value = value.encode('utf-8')
    return hashlib.new(algorithm, value).hexdigest()

def format_timestamp(timestamp, format='%Y-%m-%d %H:%M:%S'):
    """Format timestamp"""
    if isinstance(timestamp, (int, float)):
        dt = datetime.fromtimestamp(timestamp)
    elif isinstance(timestamp, str):
        dt = datetime.fromisoformat(timestamp.replace('Z', '+00:00'))
    else:
        dt = timestamp
    return dt.strftime(format)

def extract_version(version_string):
    """Extract version numbers from string"""
    match = re.search(r'(\d+)\.(\d+)\.(\d+)', version_string)
    if match:
        return {
            'major': int(match.group(1)),
            'minor': int(match.group(2)),
            'patch': int(match.group(3))
        }
    return None

def validate_email(email):
    """Validate email address format"""
    pattern = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
    return bool(re.match(pattern, email))

class FilterModule(object):
    def filters(self):
        return {
            'to_snake_case': to_snake_case,
            'hash_string': hash_string,
            'format_timestamp': format_timestamp,
            'extract_version': extract_version,
            'validate_email': validate_email,
        }
```

## Testing и **validation**

### Molecule для тестирования ролей
```yaml
# roles/java_app/molecule/default/molecule.yml
---
dependency:
  name: galaxy
  options:
    role-file: requirements.yml
    requirements-file: requirements.yml

driver:
  name: docker

platforms:
  - name: instance
    image: geerlingguy/docker-centos7-ansible:latest
    command: ${MOLECULE_DOCKER_COMMAND:-""}
    volumes:
      - /sys/fs/cgroup:/sys/fs/cgroup:ro
    privileged: true
    pre_build_image: true

provisioner:
  name: ansible
  playbooks:
    prepare: prepare.yml
    converge: converge.yml
    verify: verify.yml
  inventory:
    group_vars:
      all:
        java_app_name: test-app
        java_app_version: "1.0.0"
        java_app_port: 8080

verifier:
  name: ansible

scenario:
  name: default
  test_sequence:
    - dependency
    - cleanup
    - destroy
    - syntax
    - create
    - prepare
    - converge
    - idempotence
    - side_effect
    - verify
    - cleanup
    - destroy
```

```yaml
# roles/java_app/molecule/default/verify.yml
---
- name: Verify Java application
  hosts: all
  gather_facts: false

  tasks:
    - name: Check if Java is installed
      command: java -version
      register: java_version
      failed_when: java_version.rc != 0

    - name: Check if application JAR exists
      stat:
        path: "/opt/{{ java_app_name }}.jar"
      register: jar_file
      failed_when: not jar_file.stat.exists

    - name: Check if application is running
      uri:
        url: "http://localhost:{{ java_app_port }}/actuator/health"
        status_code: 200
      register: health_check
      failed_when: health_check.status != 200

    - name: Verify application logs
      command: "tail -20 /var/log/{{ java_app_name }}/app.log"
      register: app_logs
      failed_when: "'ERROR' in app_logs.stdout"

    - name: Check monitoring endpoint
      uri:
        url: "http://localhost:{{ java_app_metrics_port }}/metrics"
        status_code: 200
      register: metrics_check
      when: java_app_monitoring == 'prometheus'
```

### Ansible Lint и проверка качества
```yaml
# .ansible-lint
---
exclude_paths:
  - .git/
  - test/
  - molecule/

rules:
  # Enable specific rules
  yaml: enable
  name: enable
  command-instead-of-shell: enable
  command-instead-of-module: enable
  deprecated-command-syntax: enable
  risky-shell-pipe: enable

  # Disable some rules for specific cases
  line-length:
    max: 120
  truthy:
    check-keys: false

# Custom rules directory
rulesdir:
  - ./rules/

# Mock modules for testing
mock_modules:
  - custom_module

# Mock roles for testing
mock_roles:
  - test_role

# Use specific Ansible version for linting
use_default_rules: true
```

## CI/CD интеграция

### Jenkins Pipeline
```groovy
// Jenkinsfile
pipeline {
    agent {
        docker {
            image 'cytopia/ansible:latest-ansible'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        ANSIBLE_CONFIG = "${WORKSPACE}/ansible.cfg"
        ANSIBLE_INVENTORY = "${WORKSPACE}/inventory/${params.ENVIRONMENT}/"
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['staging', 'production'], description: 'Target environment')
        string(name: 'APP_VERSION', defaultValue: 'latest', description: 'Application version to deploy')
        booleanParam(name: 'DRY_RUN', defaultValue: false, description: 'Run in dry-run mode')
        booleanParam(name: 'ROLLBACK', defaultValue: false, description: 'Perform rollback instead of deployment')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Validate') {
            steps {
                sh 'ansible-lint playbooks/'
                sh 'yamllint playbooks/ inventory/'
                sh 'ansible-playbook --syntax-check playbooks/site.yml'
            }
        }

        stage('Test') {
            steps {
                sh 'molecule test --all'
            }
        }

        stage('Deploy to Staging') {
            when {
                expression { params.ENVIRONMENT == 'staging' }
            }
            steps {
                script {
                    if (params.ROLLBACK) {
                        sh 'ansible-playbook playbooks/rollback.yml -e "app_version=${params.APP_VERSION}"'
                    } else {
                        sh 'ansible-playbook playbooks/deploy.yml -e "app_version=${params.APP_VERSION} environment=staging"'
                    }
                }
            }
        }

        stage('Approval') {
            when {
                expression { params.ENVIRONMENT == 'production' }
            }
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    input message: 'Deploy to Production?', ok: 'Deploy'
                }
            }
        }

        stage('Deploy to Production') {
            when {
                expression { params.ENVIRONMENT == 'production' }
                expression { !params.ROLLBACK }
            }
            steps {
                sh 'ansible-playbook playbooks/deploy.yml -e "app_version=${params.APP_VERSION} environment=production"'
            }
        }

        stage('Rollback Production') {
            when {
                expression { params.ENVIRONMENT == 'production' }
                expression { params.ROLLBACK }
            }
            steps {
                sh 'ansible-playbook playbooks/rollback.yml -e "app_version=${params.APP_VERSION} environment=production"'
            }
        }

        stage('Verify') {
            steps {
                sh 'ansible-playbook playbooks/verify.yml'
            }
        }
    }

    post {
        always {
            sh 'ansible-playbook playbooks/cleanup.yml || true'
            archiveArtifacts artifacts: 'logs//*.log', allowEmptyArchive: true
            junit testResults: 'test-results//*.xml', allowEmptyResults: true
        }
        success {
            slackSend color: 'good', message: "Deployment to ${params.ENVIRONMENT} succeeded"
        }
        failure {
            slackSend color: 'danger', message: "Deployment to ${params.ENVIRONMENT} failed"
        }
    }
}
```

### GitHub Actions
```yaml
# .github/workflows/ansible.yml
name: Ansible

on:
  push:
    branches: [main]
    paths: ['ansible/', '.github/workflows/ansible.yml']
  pull_request:
    branches: [main]
    paths: ['ansible/', '.github/workflows/ansible.yml']

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Run ansible-lint
        uses: ansible/ansible-lint@main
        with:
          working-directory: ansible

      - name: Run yamllint
        run: |
          pip install yamllint
          yamllint ansible/

  test:
    runs-on: ubuntu-latest
    needs: lint
    steps:
      - uses: actions/checkout@v3

      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.9'

      - name: Install dependencies
        run: |
          python -m pip install --upgrade pip
          pip install molecule[docker] ansible

      - name: Run molecule tests
        run: |
          cd ansible
          molecule test --all

  deploy:
    runs-on: ubuntu-latest
    needs: test
    if: github.ref == 'refs/heads/main'
    environment: production
    steps:
      - uses: actions/checkout@v3

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1

      - name: Run Ansible playbook
        uses: dawidd6/action-ansible-playbook@v2
        with:
          playbook: ansible/playbooks/site.yml
          directory: ansible
          inventory: inventory/production
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          options: |
            --limit production
            --extra-vars "app_version=${{ github.sha }}"
```

## Производительность и оптимизация

### Оптимизация производительности
```ini
# ansible.cfg оптимизированный
[defaults]
forks = 50
poll_interval = 15
timeout = 30
gathering = smart
fact_caching = jsonfile
fact_caching_connection = /tmp/ansible_facts
fact_caching_timeout = 86400

[ssh_connection]
ssh_args = -o ControlMaster=auto -o ControlPersist=60s -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null
pipelining = True
control_path_dir = /tmp/.ansible-cp

[accelerate]
accelerate_port = 5099
accelerate_timeout = 30
accelerate_connect_timeout = 5.0
```

### Параллельное выполнение
```yaml
# playbook с оптимизацией
---
- name: Parallel deployment
  hosts: all
  strategy: free  # Allow tasks to run as soon as possible
  max_fail_percentage: 20  # Allow some failures
  serial: "20%"  # Process 20% of hosts at a time

  tasks:
    - name: Update package cache
      apt:
        update_cache: yes
        cache_valid_time: 3600
      async: 600  # Run asynchronously for 10 minutes
      poll: 0     # Don't wait for completion
      register: apt_update

    - name: Wait for apt update to complete
      async_status:
        jid: "{{ apt_update.ansible_job_id }}"
      register: job_result
      until: job_result.finished
      retries: 30
      delay: 10

    - name: Install packages
      apt:
        name: "{{ item }}"
        state: present
      loop: "{{ packages }}"
      throttle: 5  # Limit concurrent tasks

    - name: Configure service
      template:
        src: service.conf.j2
        dest: /etc/service/service.conf
      notify: restart service

  handlers:
    - name: Restart service
      service:
        name: myservice
        state: restarted
      throttle: 2  # Limit concurrent handler executions
```

## Лучшие практики

### Enterprise patterns
```yaml
# playbooks/common/site.yml
---
- name: Common configuration
  hosts: all
  become: true
  gather_facts: true
  any_errors_fatal: true
  max_fail_percentage: 0

  pre_tasks:
    - name: Validate connectivity
      ping:

    - name: Gather system facts
      setup:
        gather_subset:
          - min
          - hardware
          - network
          - virtual

  roles:
    - common
    - security
    - monitoring

- name: Application deployment
  hosts: app_servers
  become: true
  gather_facts: false
  serial: "{{ serial_batch_size | default(1) }}"
  max_fail_percentage: 20

  vars:
    app_deployment_timeout: 600
    app_health_check_retries: 30
    app_health_check_delay: 20

  pre_tasks:
    - name: Check available disk space
      assert:
        that:
          - ansible_facts.mounts | selectattr('mount', 'equalto', '/opt') | map(attribute='size_available') | first | int > 1073741824
        fail_msg: "Insufficient disk space on /opt (less than 1GB available)"

  roles:
    - java_app

  post_tasks:
    - name: Verify deployment
      uri:
        url: "http://localhost:{{ java_app_port }}/actuator/health"
        status_code: 200
      register: health_check
      until: health_check.status == 200
      retries: "{{ app_health_check_retries }}"
      delay: "{{ app_health_check_delay }}"
      timeout: "{{ app_deployment_timeout }}"

    - name: Notify monitoring
      uri:
        url: "{{ monitoring_webhook_url }}"
        method: POST
        body_format: json
        body:
          event: "deployment_completed"
          host: "{{ inventory_hostname }}"
          app: "{{ app_name }}"
          version: "{{ app_version }}"
      ignore_errors: true
```

### Безопасность
```yaml
# roles/security/tasks/main.yml
---
- name: Install security updates
  package:
    name: "*"
    state: latest
  when: ansible_os_family == 'Debian'

- name: Configure firewall
  ufw:
    state: enabled
    policy: deny
    rules:
      - rule: allow
        port: '22'
        proto: tcp
        from_ip: "{{ allowed_ssh_ips | default('0.0.0.0/0') }}"
      - rule: allow
        port: '80'
        proto: tcp
      - rule: allow
        port: '443'
        proto: tcp

- name: Disable root login
  lineinfile:
    path: /etc/ssh/sshd_config
    regexp: '^PermitRootLogin'
    line: 'PermitRootLogin no'
  notify: restart ssh

- name: Set password policies
  pamd:
    name: common-password
    type: password
    control: requisite
    module_path: pam_pwquality.so
    module_arguments:
      - retry=3
      - minlen=12
      - ucredit=-1
      - lcredit=-1
      - dcredit=-1
      - ocredit=-1

- name: Configure auditd
  template:
    src: auditd.conf.j2
    dest: /etc/audit/auditd.conf
  notify: restart auditd

- name: Install and configure fail2ban
  package:
    name: fail2ban
    state: present

- name: Configure fail2ban
  template:
    src: jail.local.j2
    dest: /etc/fail2ban/jail.local
  notify: restart fail2ban
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.
## См. также
- [[ansible|Ansible: основы]] — общий справочник Ansible
- [[ansible-basics|Ansible Basics]] — краткое введение
- [[terraform-basics|Terraform]] — Infrastructure as Code
- [[docker-basics|Docker]] — контейнеризация
- [[kubernetes-advanced|Kubernetes]] — оркестрация контейнеров
