# Ansible: Основы

**Комплексное руководство по использованию Ansible — инструмента для автоматизации конфигурации и развертывания.**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Ansible Documentation](https://docs.ansible.com/) - Официальная документация

### См. также
- `../terraform/terraform-basics.md` - Terraform
- `../pulumi/pulumi-basics.md` - Pulumi

## Содержание

- [Введение в Ansible](#введение-в-ansible)
- [Playbooks](#playbooks)
- [Inventory](#inventory)
- [Modules](#modules)
- [Roles](#roles)

## Введение в Ansible

**Ansible** — инструмент автоматизации для конфигурации, развертывания и оркестрации, использующий YAML для описания задач.

### Основные возможности

- Agentless архитектура
- Идемпотентность
- Простой YAML синтаксис
- Большая библиотека модулей

## Playbooks

```yaml
# playbook.yml - основной файл Ansible playbook
# Playbook описывает набор задач для выполнения на хостах

---
# Playbook для настройки веб-сервера
- name: Configure web server
  hosts: webservers  # Группа хостов из inventory
  become: yes        # Выполнение с правами sudo
  
  tasks:
    # Задача: обновление пакетов
    - name: Update apt cache
      apt:
        update_cache: yes
        cache_valid_time: 3600
    
    # Задача: установка пакетов
    - name: Install required packages
      apt:
        name:
          - nginx
          - python3
          - python3-pip
        state: present  # Убедиться что пакеты установлены
    
    # Задача: копирование конфигурационного файла
    - name: Copy nginx configuration
      copy:
        src: nginx.conf
        dest: /etc/nginx/nginx.conf
        owner: root
        group: root
        mode: '0644'
      notify: restart nginx  # Триггер для handler
    
    # Задача: запуск сервиса
    - name: Start and enable nginx
      systemd:
        name: nginx
        state: started
        enabled: yes
  
  handlers:
    # Handler: перезапуск nginx (выполняется по notify)
    - name: restart nginx
      systemd:
        name: nginx
        state: restarted
```

## Inventory

```ini
# inventory.ini - описание хостов для управления
# Inventory определяет какие хосты доступны для Ansible

[webservers]
# Группа веб-серверов
web1.example.com ansible_host=192.168.1.10 ansible_user=ubuntu
web2.example.com ansible_host=192.168.1.11 ansible_user=ubuntu

[dbservers]
# Группа серверов баз данных
db1.example.com ansible_host=192.168.1.20 ansible_user=ubuntu

[webservers:vars]
# Переменные для группы webservers
nginx_port=80
nginx_worker_processes=4

[all:vars]
# Переменные для всех хостов
ansible_ssh_private_key_file=~/.ssh/id_rsa
ansible_python_interpreter=/usr/bin/python3
```

## Modules

```yaml
# Примеры использования различных модулей Ansible

- name: File operations
  hosts: localhost
  tasks:
    # Создание директории
    - name: Create directory
      file:
        path: /opt/myapp
        state: directory
        owner: appuser
        group: appuser
        mode: '0755'
    
    # Копирование файла
    - name: Copy file
      copy:
        src: app.conf
        dest: /opt/myapp/app.conf
        mode: '0644'
    
    # Создание символической ссылки
    - name: Create symlink
      file:
        src: /opt/myapp/app.conf
        dest: /etc/app.conf
        state: link

- name: Service management
  hosts: webservers
  become: yes
  tasks:
    # Управление сервисом
    - name: Ensure service is running
      systemd:
        name: nginx
        state: started
        enabled: yes
    
    # Перезапуск сервиса
    - name: Restart service
      systemd:
        name: nginx
        state: restarted
```

## Roles

```yaml
# Структура Ansible role
# roles/nginx/tasks/main.yml
---
- name: Install nginx
  apt:
    name: nginx
    state: present

- name: Configure nginx
  template:
    src: nginx.conf.j2
    dest: /etc/nginx/nginx.conf
  notify: restart nginx

# Использование role в playbook
- name: Setup nginx
  hosts: webservers
  become: yes
  roles:
    - nginx
```

---

*Обновлено: 2026-01-25*
