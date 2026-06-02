---
title: "Вопросы на собеседовании: Ansible"
description: "Ansible: agentless config management, YAML playbooks, modules, roles, inventory, Ansible Vault, idempotency, vs Puppet/Chef/Salt, AWX/Tower, Ansible Galaxy"
tags:
  - interview
  - devops
  - ansible-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Ansible"
  - "Ansible interview"
  - "Ansible собеседование"
prerequisites:
  - "[[ansible]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Ansible`

`Ansible` — agentless-система управления конфигурацией и автоматизации. Создана Michael DeHaan (2012), куплена Red Hat (2015). Использует **SSH** (без агентов), **YAML-плейбуки**, **идемпотентные** модули. Доминирует на рынке config management. Альтернативы: Puppet, Chef, SaltStack.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Ansible Documentation](https://docs.ansible.com/)
- [Ansible Galaxy](https://galaxy.ansible.com/) — community roles
- [Ansible Best Practices](https://docs.ansible.com/ansible/latest/tips_tricks/ansible_tips_tricks.html)
- [AWX (open-source Ansible Tower)](https://github.com/ansible/awx)
- [Red Hat Ansible Automation Platform](https://www.redhat.com/en/technologies/management/ansible)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Ansible?](#q1--что-такое-ansible)
- [Q2. (!) Agentless — преимущество?](#q2--agentless--преимущество)
- [Q3. (!) Idempotency — что это и зачем?](#q3--idempotency--что-это-и-зачем)

**Структура**
- [Q4. (!) Inventory — статический и динамический?](#q4--inventory--статический-и-динамический)
- [Q5. (!) Playbook — структура?](#q5--playbook--структура)
- [Q6. (!) Roles?](#q6--roles)
- [Q7. Modules?](#q7-modules)
- [Q8. Tasks, handlers?](#q8-tasks-handlers)

**Variables**
- [Q9. (!) Variables (group_vars, host_vars, playbook)?](#q9--variables-group_vars-host_vars-playbook)
- [Q10. Facts (auto-discovered)?](#q10-facts-auto-discovered)
- [Q11. Templates (Jinja2)?](#q11-templates-jinja2)

**Безопасность**
- [Q12. (!) Ansible Vault?](#q12--ansible-vault)
- [Q13. SSH best practices?](#q13-ssh-best-practices)

**Execution**
- [Q14. (!) ad-hoc commands vs playbooks?](#q14--ad-hoc-commands-vs-playbooks)
- [Q15. Check mode (dry-run)?](#q15-check-mode-dry-run)
- [Q16. Tags?](#q16-tags)
- [Q17. Parallel execution (forks)?](#q17-parallel-execution-forks)

**Galaxy и community**
- [Q18. (!) Ansible Galaxy?](#q18--ansible-galaxy)
- [Q19. Collections (с Ansible 2.10)?](#q19-collections-с-ansible-210)

**AWX / Tower**
- [Q20. (!) AWX vs Ansible Tower vs Automation Platform?](#q20--awx-vs-ansible-tower-vs-automation-platform)
- [Q21. Workflows, schedules?](#q21-workflows-schedules)

**Сравнения**
- [Q22. (!) Ansible vs Terraform?](#q22--ansible-vs-terraform)
- [Q23. (!) Ansible vs Puppet vs Chef?](#q23--ansible-vs-puppet-vs-chef)

**Production**
- [Q24. (!) Best practices?](#q24--best-practices)
- [Q25. Какие частые проблемы?](#q25-какие-частые-проблемы)

## Q1. (!) Что такое Ansible?

**Ansible** — open-source инструмент IT-автоматизации. Создан Michael DeHaan (автор и Cobbler) в 2012, куплен Red Hat в 2015.

**Сценарии применения:**
- **Управление конфигурацией** (установка/настройка ПО)
- **Деплой приложений**
- **Оркестрация** (многошаговые workflow)
- **Provisioning** (облачные ресурсы, в паре с Terraform)
- **Continuous delivery** (деплой через CI/CD)
- **Сетевая автоматизация** (Cisco, Juniper и т.д.)
- **Автоматизация безопасности**

**Ключевые характеристики:**
- **Agentless** (использует SSH, WinRM)
- **YAML-плейбуки**
- **Идемпотентные** модули
- **Push-модель** (control node → managed nodes)

## Q2. (!) Agentless — преимущество?

**В сравнении с Puppet, Chef** (agent-based):
- ❌ Нужно ставить и обслуживать агента на каждой managed node
- ❌ Обновления агента — отдельная задача
- ❌ Агент создаёт риски безопасности

**Ansible (agentless):**
- ✅ Достаточно SSH (уже есть на Linux-серверах)
- ✅ Не нужна установка / обновление агента
- ✅ Проще начать
- ✅ Меньше поверхность атаки

**Компромисс:**
- ❌ Накладные расходы SSH на каждую задачу (медленнее на огромных парках машин)
- ❌ Сложнее **отслеживать состояние** (агент не шлёт отчёты обратно)

**В 2025** agentless-подход **доминирует** в новых инструментах (у Salt тоже есть agentless-режим).

## Q3. (!) Idempotency — что это и зачем?

**Идемпотентность** — многократный запуск задачи даёт тот же результат.

**Пример:**
```yaml
# Idempotent — install only if not present
- name: Install nginx
  package:
    name: nginx
    state: present

# NOT idempotent — runs every time
- name: Run setup script
  command: /opt/setup.sh  # always executes
```

**Зачем:**
- Безопасно **перезапускать** плейбук
- **Сходимость** — система приходит к желаемому состоянию
- **Нет побочных эффектов** при частичных сбоях

**Большинство модулей Ansible идемпотентны**. С `command` / `shell` нужна осторожность (по умолчанию не идемпотентны).

## Q4. (!) Inventory — статический и динамический?

**Статический inventory** (INI или YAML):
```ini
# inventory.ini
[webservers]
web1.example.com
web2.example.com

[databases]
db1.example.com

[production:children]
webservers
databases
```

**Динамический inventory** — генерируется из внешнего источника:
- AWS EC2 (автообнаружение)
- Azure
- GCP
- Kubernetes
- Consul, Vault
- Самописные скрипты

```yaml
# aws_ec2.yml
plugin: aws_ec2
regions:
  - us-east-1
keyed_groups:
  - key: tags.Environment
```

```bash
ansible-inventory -i aws_ec2.yml --list
```

**Облачные окружения** обычно требуют динамический inventory (инстансы эфемерны).

## Q5. (!) Playbook — структура?

```yaml
# deploy.yml
---
- name: Deploy web servers
  hosts: webservers
  become: yes  # use sudo
  vars:
    nginx_port: 80

  tasks:
    - name: Install nginx
      package:
        name: nginx
        state: present

    - name: Copy config
      template:
        src: nginx.conf.j2
        dest: /etc/nginx/nginx.conf
      notify: Restart nginx

    - name: Ensure nginx running
      service:
        name: nginx
        state: started
        enabled: yes

  handlers:
    - name: Restart nginx
      service:
        name: nginx
        state: restarted
```

**Запуск:**
```bash
ansible-playbook -i inventory deploy.yml
```

## Q6. (!) Roles?

**Роль (role)** — переиспользуемый структурированный набор (tasks, vars, files, templates).

```
roles/
  nginx/
    tasks/
      main.yml
    handlers/
      main.yml
    templates/
      nginx.conf.j2
    defaults/
      main.yml      # default variables
    vars/
      main.yml      # higher-precedence variables
    files/
      static-asset.html
    meta/
      main.yml      # dependencies
```

**Использование в плейбуке:**
```yaml
- hosts: webservers
  roles:
    - nginx
    - app-deploy
```

**Переиспользуются** между проектами, распространяются через Galaxy.

## Q7. Modules?

**Модули (modules)** — единицы работы (по сути функции). 3000+ встроенных.

**Категории:**
- **Система** — file, service, user, package
- **Облако** — ec2, gcp_compute, azure_rm
- **Сетевые утилиты** — uri, get_url
- **БД** — postgresql_db, mysql_user
- **Контроль версий** — git, github
- **Файлы** — copy, template, lineinfile
- **Контейнеры** — docker_container, k8s

```yaml
# Examples
- file:
    path: /etc/myapp.conf
    state: touch

- service:
    name: nginx
    state: restarted

- ec2_instance:
    name: my-instance
    instance_type: t3.micro
    image: ami-...
```

**Свои модули** можно писать на Python.

## Q8. Tasks, handlers?

**Tasks** — выполняются по порядку.

**Handlers** — особые задачи, выполняются **только** если был `notify`, и **один раз** в конце play.

```yaml
tasks:
  - name: Update nginx config
    template:
      src: nginx.conf.j2
      dest: /etc/nginx/nginx.conf
    notify: Restart nginx  # marks handler

  - name: Update SSL cert
    copy:
      src: cert.pem
      dest: /etc/nginx/cert.pem
    notify: Restart nginx  # same handler — fires once

handlers:
  - name: Restart nginx
    service:
      name: nginx
      state: restarted
```

**Эффект:** если хоть одна из задач что-то изменила → handler срабатывает **один раз** в конце (без двойного рестарта).

## Q9. (!) Variables (group_vars, host_vars, playbook)?

**Приоритет (от низкого к высокому):**

1. **Role defaults** (`roles/*/defaults/main.yml`)
2. **Переменные из inventory-файла**
3. **`group_vars/all.yml`** (применяются ко всем хостам)
4. **`group_vars/<group>.yml`** (конкретная группа)
5. **`host_vars/<host>.yml`** (конкретный хост)
6. **`vars` плейбука**
7. **`vars` роли**
8. **extra vars `-e`** (наивысший приоритет)

```
inventory/
  group_vars/
    all.yml         # для всех
    webservers.yml  # для webservers группы
  host_vars/
    web1.yml        # для web1
```

**Переопределение через CLI:**
```bash
ansible-playbook deploy.yml -e "version=2.0"
```

## Q10. Facts (auto-discovered)?

**Facts** — информация, автоматически собираемая о managed nodes.

```yaml
- debug:
    msg: "OS is {{ ansible_facts['distribution'] }}"
- debug:
    msg: "Memory: {{ ansible_facts['memtotal_mb'] }} MB"
```

**Примеры:**
- `ansible_distribution` (Ubuntu, RHEL)
- `ansible_distribution_version`
- `ansible_memtotal_mb`
- `ansible_processor_count`
- `ansible_default_ipv4.address`
- `ansible_hostname`

**Отключить** для скорости: `gather_facts: no`.

**Свои facts:** файлы в `/etc/ansible/facts.d/` на managed node.

## Q11. Templates (Jinja2)?

```yaml
- template:
    src: nginx.conf.j2
    dest: /etc/nginx/nginx.conf
```

`nginx.conf.j2`:
```jinja2
worker_processes {{ ansible_processor_count }};

server {
    listen {{ nginx_port }};
    server_name {{ inventory_hostname }};

    {% for backend in backend_servers %}
    upstream {{ backend.name }} {
        server {{ backend.host }}:{{ backend.port }};
    }
    {% endfor %}
}
```

**Мощный механизм** — переменные, циклы, условия, фильтры.

## Q12. (!) Ansible Vault?

**Шифрует чувствительные данные** в плейбуках.

```bash
# Encrypt file
ansible-vault encrypt secrets.yml

# Edit
ansible-vault edit secrets.yml

# Decrypt
ansible-vault decrypt secrets.yml

# Encrypt single string
ansible-vault encrypt_string 'mypassword' --name 'db_password'
```

**Использование в плейбуке:**
```yaml
vars_files:
  - secrets.yml
```

```bash
ansible-playbook deploy.yml --ask-vault-pass
# or
ansible-playbook deploy.yml --vault-password-file ~/.vault_pass
```

**Best practice:** vault-файл + vault-id для нескольких окружений.

**Альтернатива:** интеграция с **Vault** (HashiCorp) для централизованного хранения секретов.

## Q13. SSH best practices?

```ini
# ansible.cfg
[defaults]
host_key_checking = False  # for dev (NOT production!)
forks = 50  # parallel connections
pipelining = True  # speed up

[ssh_connection]
ssh_args = -o ControlMaster=auto -o ControlPersist=60s
```

**Best practices:**
- **SSH-ключи**, а не пароли
- **Bastion / jump-хосты** для приватных сетей
- **Отдельный пользователь** для Ansible (не root)
- **Sudo** через `become: yes`
- Режим **`ansible-pull`** для pull-based сценариев

## Q14. (!) ad-hoc commands vs playbooks?

**Ad-hoc** — разовая команда:
```bash
ansible all -m ping
ansible webservers -m service -a "name=nginx state=restarted" --become
ansible all -a "uname -r"  # shell command
```

**Плейбук** — много задач, переиспользуется.

**Ad-hoc подходит для:**
- Быстрых проверок (`ping`)
- Разовых действий
- Изучения inventory

**Плейбуки — для:**
- Повторяемых задач
- Многошаговых workflow
- Автоматизации

## Q15. Check mode (dry-run)?

```bash
ansible-playbook deploy.yml --check
```

**Эффект:** показывает, что **изменилось бы**, без реальных изменений.

**Большинство модулей** поддерживают check mode. Некоторые (например, `command`/`shell`) в check mode пропускаются.

**В сочетании с `--diff`:** показывает diff файлов до внесения изменений.

```bash
ansible-playbook deploy.yml --check --diff
```

**Best practice:** запускать check + diff перед деплоем в production.

## Q16. Tags?

```yaml
tasks:
  - name: Install nginx
    package: name=nginx state=present
    tags: nginx, install

  - name: Copy SSL certs
    copy: src=cert.pem dest=/etc/ssl/
    tags: ssl, security
```

**Запустить только помеченные задачи:**
```bash
ansible-playbook deploy.yml --tags ssl
ansible-playbook deploy.yml --skip-tags install
```

**Сценарий:** быстро передеплоить только конфиг (пропустив шаги установки).

## Q17. Parallel execution (forks)?

```ini
# ansible.cfg
[defaults]
forks = 50  # default 5
```

Или для конкретного запуска:
```bash
ansible-playbook deploy.yml --forks 100
```

**Эффект:** Ansible выполняет задачи параллельно сразу на **N хостах**.

**Тюнинг:**
- Больше парк машин → больше forks (но с оглядкой на лимит SSH-соединений)
- Ограничены по памяти — снижайте forks

## Q18. (!) Ansible Galaxy?

**Ansible Galaxy** — community-хаб для **roles** и **collections**.

```bash
# Install role
ansible-galaxy install geerlingguy.nginx

# Install from requirements file
ansible-galaxy install -r requirements.yml
```

`requirements.yml`:
```yaml
roles:
  - name: geerlingguy.nginx
    version: 3.1.4
collections:
  - name: community.general
```

**40K+ ролей** на Galaxy. Качество разное — смотрите звёзды и свежесть обновлений.

## Q19. Collections (с Ansible 2.10)?

**Collections** — современная единица упаковки. Включает модули, роли, плагины, документацию.

```bash
ansible-galaxy collection install community.kubernetes
```

**Использование в плейбуке:**
```yaml
- hosts: all
  tasks:
    - community.kubernetes.k8s:
        name: my-pod
        ...
```

**Встроенная коллекция `ansible.builtin`** — базовые модули.

**Облачные коллекции:** `amazon.aws`, `google.cloud`, `azure.azcollection`.

## Q20. (!) AWX vs Ansible Tower vs Automation Platform?

**AWX** — open-source UI / платформа оркестрации для Ansible (upstream бывшего Tower).

**Ansible Tower** (платный, Red Hat) — enterprise-версия с поддержкой.

**Ansible Automation Platform (AAP)** — enterprise-предложение Red Hat (Tower + расширения).

**Возможности:**
- Web-интерфейс
- Планирование задач (job scheduling)
- Workflow (цепочки плейбуков)
- RBAC
- API
- Уведомления
- Дашборд, логи

**Self-host AWX:**
```bash
helm install awx awx-operator/awx
```

**Сценарий:** централизованный запуск Ansible для команды / enterprise.

## Q21. Workflows, schedules?

**Workflow** — цепочка из нескольких плейбуков с условной логикой.

```
Stage 1: Provision EC2 (Terraform call)
  ↓ on success
Stage 2: Configure servers (Ansible playbook)
  ↓ on success
Stage 3: Deploy app
  ↓ on failure
Stage 4 (cleanup): Rollback
```

**Schedules:** cron-подобные для периодического запуска плейбуков (проверки compliance, бэкапы).

## Q22. (!) Ansible vs Terraform?

| Аспект | Ansible | Terraform |
|--------|---------|-----------|
| Тип | Управление конфигурацией | Провижининг инфраструктуры |
| Состояние | **Stateless** (каждый запуск независим) | **Stateful** (файл состояния) |
| Подход | Процедурный (задачи по порядку) | Декларативный (желаемое состояние) |
| Идемпотентность | На уровне задачи | На уровне всей инфраструктуры |
| Провижининг в облаке | Нормально | **Отлично** |
| Управление конфигурацией | **Отлично** | Ограниченно |
| Обнаружение дрейфа | Ограниченно | Встроено |

**Часто используют вместе:**
- **Terraform** — провижинит инфраструктуру (VM, сети, БД)
- **Ansible** — настраивает ПО на VM

```
Terraform creates EC2 → Ansible installs nginx, copies config
```

## Q23. (!) Ansible vs Puppet vs Chef?

| Критерий | Ansible | Puppet | Chef |
|-----------|---------|--------|------|
| Архитектура | **Agentless** (SSH) | Agent-based | Agent-based |
| Язык | YAML | Puppet DSL (похож на Ruby) | Ruby |
| Подход | Процедурный | Декларативный | Процедурный |
| Push / Pull | Push | Pull | Pull |
| Порог входа | Низкий | Средний | Высокий |
| Распространённость (2025) | **Наибольшая** | Снижается | Снижается |
| Экосистема | Galaxy | Forge | Supermarket |

**Ansible победил** в 2010-2020-х за счёт **простоты + agentless**. Puppet и Chef — legacy в большинстве организаций.

**SaltStack** — четвёртый игрок, agent или agentless, быстрый.

## Q24. (!) Best practices?

1. **Идемпотентность** — всегда проверяйте повторный запуск
2. **Роли** — разбивайте большие плейбуки
3. **Иерархия переменных** — defaults → group_vars → host_vars
4. **Vault** для секретов (или интеграция с Vault)
5. **Контроль версий** — плейбуки в git
6. **Inventory в системе контроля версий** (иногда)
7. **Используйте `check --diff`** перед production
8. **Теги** для точечных перезапусков
9. **Тестируйте плейбуки** (фреймворк Molecule)
10. **Интеграция с CI/CD** — автозапуск по git push
11. **Никаких `command` / `shell`**, если есть подходящий модуль
12. **Документируйте через `name:`** в каждой задаче
13. **Избегайте `latest`** для версий пакетов (фиксируйте версию)
14. **Handlers** для рестартов (избегайте двойного рестарта)

## Q25. Какие частые проблемы?

1. **Проблемы с SSH-соединением** (firewall, ключи)
2. **Права `become`** — sudo не настроен
3. **Путаница с приоритетом переменных**
4. **Нарушения идемпотентности** (использование `command`)
5. **Медленное выполнение** (мало forks, сбор facts на огромном парке машин)
6. **Управление паролем Vault** — безопасный обмен
7. **Дрейф inventory** — устаревшие списки хостов
8. **Сложность плейбуков** — монолитные плейбуки невозможно сопровождать
9. **Различия окружений** — dev-плейбук падает в prod
10. **Отсутствие тестов** — ломает production

В **2025** Ansible — **стандарт** для управления конфигурацией. Часто дополняется Terraform для провижининга инфраструктуры.

---

## See also

- [HashiCorp Vault](vault-interview.md) — secrets integration
- [Consul](consul-interview.md) — dynamic inventory source
- [Terraform](terraform-interview.md) — для infrastructure
- [Docker](docker-interview.md) — Ansible deploys containers
- [Kubernetes](kubernetes-interview.md) — Ansible can manage k8s
- [Git](git-interview.md) — playbooks в git
- [Микросервисы](../architecture/microservices-interview.md) — deploy with Ansible
- [Cloud-native Patterns](../cloud/cloud-native-patterns-interview.md) — context
- [Deployment Strategies](../cicd/deployment-strategies-interview.md) — Ansible для deploys
- [Pipeline Design](../cicd/pipeline-design-interview.md) — CI/CD
- [Secrets Management](../security/secrets-management-interview.md) — Ansible Vault
- [Application Security](../security/application-security-interview.md) — secure playbooks

- [ArgoCD и GitOps](argocd-interview.md)
- [HashiCorp Consul](consul-interview.md)
- [Docker](docker-interview.md)
- [Git](git-interview.md)
- [Gradle и Maven](gradle-maven-interview.md)
- [Helm](helm-interview.md)
- [Шпаргалка: Ansible](../../platform/iac/ansible/ansible.md) — теория
