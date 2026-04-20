---
title: "Вопросы на собеседовании: Ansible"
description: "Ansible: agentless config management, YAML playbooks, modules, roles, inventory, Ansible Vault, idempotency, vs Puppet/Chef/Salt, AWX/Tower, Ansible Galaxy"
tags:
  - interview
  - devops
  - ansible-interview
aliases:
  - "Ansible interview"
  - "Ansible собеседование"
  - "Configuration management interview"
  - "Ansible Playbook interview"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Ansible`

`Ansible` — agentless configuration management и automation tool. Создан Michael DeHaan (2012), acquired Red Hat (2015). Использует **SSH** (не агенты), **YAML playbooks**, **idempotent** modules. Доминирует в config management market. Альтернативы: Puppet, Chef, SaltStack.

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

**Ansible** — open-source IT automation tool. Created Michael DeHaan (creator Cobbler too) в 2012, acquired by Red Hat 2015.

**Use cases:**
- **Configuration management** (install/configure software)
- **Application deployment**
- **Orchestration** (multi-step workflows)
- **Provisioning** (cloud resources, paired с Terraform)
- **Continuous delivery** (deploy via CI/CD)
- **Network automation** (Cisco, Juniper, etc.)
- **Security automation**

**Key characteristics:**
- **Agentless** (uses SSH, WinRM)
- **YAML-based** playbooks
- **Idempotent** modules
- **Push-based** (control node → managed nodes)

## Q2. (!) Agentless — преимущество?

**Vs Puppet, Chef** (agent-based):
- ❌ Install/manage agent на каждой managed node
- ❌ Agent updates separate
- ❌ Agent security risks

**Ansible (agentless):**
- ✅ Just SSH (already on Linux servers)
- ✅ No agent install / upgrade
- ✅ Easier to start
- ✅ Less attack surface

**Trade-off:**
- ❌ SSH overhead per task (slower for huge fleets)
- ❌ Harder для **state monitoring** (no agent reporting back)

**В 2025** — agentless approach **doминирует** в new tools (Salt also has agentless mode).

## Q3. (!) Idempotency — что это и зачем?

**Idempotent** — running task multiple times = same result.

**Example:**
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
- Safe to **re-run** playbook
- **Convergent** — system reaches desired state
- **No side effects** при partial failures

**Most Ansible modules idempotent**. Use `command` / `shell` carefully (not idempotent by default).

## Q4. (!) Inventory — статический и динамический?

**Static inventory** (INI или YAML):
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

**Dynamic inventory** — generated from external source:
- AWS EC2 (auto-discover)
- Azure
- GCP
- Kubernetes
- Consul, Vault
- Custom scripts

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

**Cloud environments** обычно требуют dynamic inventory (instances ephemeral).

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

**Run:**
```bash
ansible-playbook -i inventory deploy.yml
```

## Q6. (!) Roles?

**Role** — reusable structured collection (tasks, vars, files, templates).

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

**Use в playbook:**
```yaml
- hosts: webservers
  roles:
    - nginx
    - app-deploy
```

**Reusable** across projects, shared via Galaxy.

## Q7. Modules?

**Modules** — units of work (~ functions). 3000+ built-in.

**Categories:**
- **System** — file, service, user, package
- **Cloud** — ec2, gcp_compute, azure_rm
- **Net Tools** — uri, get_url
- **DB** — postgresql_db, mysql_user
- **Source Control** — git, github
- **Files** — copy, template, lineinfile
- **Container** — docker_container, k8s

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

**Custom modules** can be written в Python.

## Q8. Tasks, handlers?

**Tasks** — execute по порядку.

**Handlers** — special tasks, executed **only** if `notify`d, **once** at end of play.

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

**Эффект:** if either task changed → handler fires **once** at end (no double restart).

## Q9. (!) Variables (group_vars, host_vars, playbook)?

**Precedence (low → high):**

1. **Role defaults** (`roles/*/defaults/main.yml`)
2. **Inventory file vars**
3. **`group_vars/all.yml`** (apply к all hosts)
4. **`group_vars/<group>.yml`** (specific group)
5. **`host_vars/<host>.yml`** (specific host)
6. **Playbook `vars`**
7. **Role `vars`**
8. **`-e` extra vars** (highest)

```
inventory/
  group_vars/
    all.yml         # для всех
    webservers.yml  # для webservers группы
  host_vars/
    web1.yml        # для web1
```

**Override через CLI:**
```bash
ansible-playbook deploy.yml -e "version=2.0"
```

## Q10. Facts (auto-discovered)?

**Facts** — info auto-collected про managed nodes.

```yaml
- debug:
    msg: "OS is {{ ansible_facts['distribution'] }}"
- debug:
    msg: "Memory: {{ ansible_facts['memtotal_mb'] }} MB"
```

**Examples:**
- `ansible_distribution` (Ubuntu, RHEL)
- `ansible_distribution_version`
- `ansible_memtotal_mb`
- `ansible_processor_count`
- `ansible_default_ipv4.address`
- `ansible_hostname`

**Disable** для speed: `gather_facts: no`.

**Custom facts:** files в `/etc/ansible/facts.d/` на managed node.

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

**Powerful** — variables, loops, conditionals, filters.

## Q12. (!) Ansible Vault?

**Encrypts sensitive data** в playbooks.

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

**Use в playbook:**
```yaml
vars_files:
  - secrets.yml
```

```bash
ansible-playbook deploy.yml --ask-vault-pass
# or
ansible-playbook deploy.yml --vault-password-file ~/.vault_pass
```

**Best practice:** vault file + vault-id для multiple environments.

**Alternative:** integrate с **Vault** (HashiCorp) для centralized secrets.

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
- **SSH keys**, not passwords
- **Bastion / jump hosts** для private networks
- **Specific user** для Ansible (not root)
- **Sudo** через `become: yes`
- **`ansible-pull`** mode для pull-based scenarios

## Q14. (!) ad-hoc commands vs playbooks?

**Ad-hoc** — one-off command:
```bash
ansible all -m ping
ansible webservers -m service -a "name=nginx state=restarted" --become
ansible all -a "uname -r"  # shell command
```

**Playbook** — multi-task, reusable.

**Use ad-hoc для:**
- Quick checks (`ping`)
- One-off actions
- Inventory exploration

**Playbooks для:**
- Repeatable tasks
- Multi-step workflows
- Automation

## Q15. Check mode (dry-run)?

```bash
ansible-playbook deploy.yml --check
```

**Effect:** show what **would** change, без actual modifications.

**Most modules** support check mode. Some (like `command`/`shell`) skip in check mode.

**Combined с `--diff`:** show file diffs before changes.

```bash
ansible-playbook deploy.yml --check --diff
```

**Best practice:** run check + diff перед production deployment.

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

**Run only tagged tasks:**
```bash
ansible-playbook deploy.yml --tags ssl
ansible-playbook deploy.yml --skip-tags install
```

**Use case:** quick re-deploy только config (skip install steps).

## Q17. Parallel execution (forks)?

```ini
# ansible.cfg
[defaults]
forks = 50  # default 5
```

Or per-run:
```bash
ansible-playbook deploy.yml --forks 100
```

**Effect:** Ansible parallelizes tasks across **N hosts** simultaneously.

**Tuning:**
- Larger fleet → higher forks (but limit by SSH connection capacity)
- Memory constrained — lower forks

## Q18. (!) Ansible Galaxy?

**Ansible Galaxy** — community hub для **roles** и **collections**.

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

**40K+ roles** на Galaxy. Quality varies — check stars, recent updates.

## Q19. Collections (с Ansible 2.10)?

**Collections** — modern packaging unit. Includes modules, roles, plugins, docs.

```bash
ansible-galaxy collection install community.kubernetes
```

**Use в playbook:**
```yaml
- hosts: all
  tasks:
    - community.kubernetes.k8s:
        name: my-pod
        ...
```

**Built-in `ansible.builtin`** collection — core modules.

**Cloud collections:** `amazon.aws`, `google.cloud`, `azure.azcollection`.

## Q20. (!) AWX vs Ansible Tower vs Automation Platform?

**AWX** — open-source Ansible UI / orchestration platform (former Tower upstream).

**Ansible Tower** (paid, Red Hat) — enterprise version с support.

**Ansible Automation Platform (AAP)** — Red Hat's enterprise offering (Tower + more).

**Features:**
- Web UI
- Job scheduling
- Workflows (chained playbooks)
- RBAC
- API
- Notifications
- Dashboard, logs

**Self-host AWX:**
```bash
helm install awx awx-operator/awx
```

**Use case:** centralized Ansible execution для team / enterprise.

## Q21. Workflows, schedules?

**Workflow** — chain multiple playbooks с conditional logic.

```
Stage 1: Provision EC2 (Terraform call)
  ↓ on success
Stage 2: Configure servers (Ansible playbook)
  ↓ on success
Stage 3: Deploy app
  ↓ on failure
Stage 4 (cleanup): Rollback
```

**Schedules:** cron-like для periodic playbook runs (compliance checks, backups).

## Q22. (!) Ansible vs Terraform?

| Aspect | Ansible | Terraform |
|--------|---------|-----------|
| Type | Configuration management | Infrastructure provisioning |
| State | **Stateless** (each run independent) | **Stateful** (state file) |
| Approach | Procedural (tasks в order) | Declarative (desired state) |
| Idempotency | Per-task | Whole infrastructure |
| Cloud provisioning | OK | **Excellent** |
| Config management | **Excellent** | Limited |
| Drift detection | Limited | Built-in |

**Often used together:**
- **Terraform** — provision infrastructure (VMs, networks, DBs)
- **Ansible** — configure software на VMs

```
Terraform creates EC2 → Ansible installs nginx, copies config
```

## Q23. (!) Ansible vs Puppet vs Chef?

| Critterion | Ansible | Puppet | Chef |
|-----------|---------|--------|------|
| Architecture | **Agentless** (SSH) | Agent-based | Agent-based |
| Language | YAML | Puppet DSL (Ruby-like) | Ruby |
| Approach | Procedural | Declarative | Procedural |
| Push / Pull | Push | Pull | Pull |
| Learning curve | Easy | Medium | Hard |
| Adoption (2025) | **Highest** | Declining | Declining |
| Ecosystem | Galaxy | Forge | Supermarket |

**Ansible won** в 2010-2020s due to **simplicity + agentless**. Puppet и Chef — legacy в большинстве organizations.

**SaltStack** — fourth player, agent или agentless, fast.

## Q24. (!) Best practices?

1. **Idempotency** — always test re-running
2. **Roles** — break large playbooks
3. **Variables hierarchy** — defaults → group_vars → host_vars
4. **Vault** для secrets (или integrate Vault)
5. **Source control** — playbooks в git
6. **Inventory in version control** (sometimes)
7. **Use `check --diff`** перед production
8. **Tags** для granular re-runs
9. **Test playbooks** (Molecule framework)
10. **CI/CD integration** — auto-run на git push
11. **No `command` / `shell`** when module exists
12. **Document with `name:`** на every task
13. **Avoid `latest`** для package versions (pin)
14. **Handlers** для restarts (avoid double restart)

## Q25. Какие частые проблемы?

1. **SSH connection issues** (firewall, keys)
2. **`become` permissions** — sudo not configured
3. **Variable precedence confusion**
4. **Idempotency violations** (using `command` блять)
5. **Slow execution** (low forks, fact gathering на huge fleet)
6. **Vault password management** — sharing securely
7. **Inventory drift** — outdated host lists
8. **Playbook complexity** — monolithic playbooks unmaintainable
9. **Environment differences** — dev playbook fails в prod
10. **No testing** — break production

В **2025** Ansible — **standard** для config management. Часто complemented Terraform для infrastructure provisioning.

---

## See also

- [[vault-interview|HashiCorp Vault]] — secrets integration
- [[consul-interview|Consul]] — dynamic inventory source
- [[terraform-interview|Terraform]] — для infrastructure
- [[docker-interview|Docker]] — Ansible deploys containers
- [[kubernetes-interview|Kubernetes]] — Ansible can manage k8s
- [[git-interview|Git]] — playbooks в git
- [[microservices-interview|Микросервисы]] — deploy with Ansible
- [[cloud-native-patterns-interview|Cloud-native Patterns]] — context
- [[deployment-strategies-interview|Deployment Strategies]] — Ansible для deploys
- [[pipeline-design-interview|Pipeline Design]] — CI/CD
- [[secrets-management-interview|Secrets Management]] — Ansible Vault
- [[application-security-interview|Application Security]] — secure playbooks

- [[argocd-interview|ArgoCD и GitOps]]
- [[consul-interview|HashiCorp Consul]]
- [[docker-interview|Docker]]
- [[git-interview|Git]]
- [[gradle-maven-interview|Gradle и Maven]]
- [[helm-interview|Helm]]
- [[ansible|Шпаргалка: Ansible]] — теория
