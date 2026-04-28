---
title: "MySQL: Администрирование и обслуживание — Полное руководство по управлению MySQL"
description: "Комплексное руководство по администрированию MySQL: установка, конфигурация, обслуживание, резервное копирование и восстановление."
tags:
  - databases
  - relational
  - mysql-admin
type: "overview"
difficulty: "intermediate"
aliases:
  - "MySQL"
  - "mysql admin"
prerequisites:
  - "[[mysql-basics]]"
next:
  - "[[mysql-replication]]"
updated: "2026-04-20"
---
# MySQL: Администрирование и обслуживание — Полное руководство по управлению MySQL

Комплексное руководство по администрированию **MySQL**: установка, конфигурация, обслуживание, резервное копирование и восстановление.

## Полезные ссылки

### Официальная документация
- [MySQL Server Administration](https://dev.mysql.com/doc/refman/8.0/en/administration.html)
- [Server Administration](https://dev.mysql.com/doc/refman/8.0/en/server-administration.html)
- [Backup and Recovery](https://dev.mysql.com/doc/refman/8.0/en/backup-and-recovery.html)

### Инструменты администрирования
- [MySQL Workbench](https://dev.mysql.com/doc/workbench/en/)
- [phpMyAdmin](https://www.phpmyadmin.net/docs/)
- [Adminer](https://www.adminer.org/) — лёгкий веб-интерфейс
- [Percona Toolkit](https://docs.percona.com/percona-toolkit/)

### Мониторинг и обслуживание
- [MySQL Enterprise Monitor](https://dev.mysql.com/doc/mysql-monitor/en/)
- [Percona Monitoring and Management](https://docs.percona.com/percona-monitoring-and-management/)
- [Prometheus MySQL Exporter](https://github.com/prometheus/mysqld_exporter) и [Grafana](https://grafana.com/docs/)

### См. также
- [mysql-basics.md](mysql-basics.md) — основы **MySQL**
- [mysql-performance.md](mysql-performance.md) — производительность и тюнинг
- [mysql-replication.md](mysql-replication.md) — репликация и высокая доступность
- [mysql-queries.md](mysql-queries.md) — запросы и мониторинг

## Содержание

- [Установка и первоначальная настройка](#установка-и-первоначальная-настройка)
  - [Установка на разных ОС](#установка-на-разных-ос)
    - [Ubuntu/Debian](#ubuntudebian)
    - [CentOS/RHEL](#centosrhel)
    - [Docker](#docker)
    - [macOS (Homebrew)](#macos-homebrew)
  - [Первоначальная конфигурация](#первоначальная-конфигурация)
    - [Основной конфигурационный файл](#основной-конфигурационный-файл)
    - [Проверка конфигурации](#проверка-конфигурации)
- [Конфигурация сервера](#конфигурация-сервера)
  - [Оптимизация для разных нагрузок](#оптимизация-для-разных-нагрузок)
    - [OLTP (транзакционные системы)](#oltp-транзакционные-системы)
    - [OLAP (аналитические системы)](#olap-аналитические-системы)
    - [Высоконагруженные системы](#высоконагруженные-системы)
  - [Динамическая конфигурация](#динамическая-конфигурация)
    - [Изменение настроек без перезапуска](#изменение-настроек-без-перезапуска)
    - [Опасные изменения](#опасные-изменения)
  - [Мониторинг конфигурации](#мониторинг-конфигурации)
    - [Проверка текущих настроек](#проверка-текущих-настроек)
- [Управление пользователями и правами](#управление-пользователями-и-правами)
  - [Создание пользователей](#создание-пользователей)
    - [Локальные пользователи](#локальные-пользователи)
    - [Современная аутентификация (MySQL 8.0+)](#современная-аутентификация-mysql-80)
  - [Управление правами](#управление-правами)
    - [Гранты и привилегии](#гранты-и-привилегии)
    - [Ролевая модель (MySQL 8.0+)](#ролевая-модель-mysql-80)
  - [Аудит пользователей](#аудит-пользователей)
    - [Мониторинг активности](#мониторинг-активности)
    - [Управление паролями](#управление-паролями)
- [Резервное копирование](#резервное-копирование)
  - [Логические бэкапы (mysqldump)](#логические-бэкапы-mysqldump)
    - [Полная резервная копия](#полная-резервная-копия)
    - [Инкрементальные бэкапы](#инкрементальные-бэкапы)
  - [Физические бэкапы (Percona XtraBackup)](#физические-бэкапы-percona-xtrabackup)
    - [Горячий бэкап](#горячий-бэкап)
    - [Восстановление из XtraBackup](#восстановление-из-xtrabackup)
  - [Автоматизированное резервное копирование](#автоматизированное-резервное-копирование)
    - [Скрипт для автоматизации](#скрипт-для-автоматизации)
    - [Cron задание](#cron-задание)
- [Восстановление данных](#восстановление-данных)
  - [Восстановление из mysqldump](#восстановление-из-mysqldump)
    - [Полное восстановление](#полное-восстановление)
    - [Point-in-Time Recovery](#point-in-time-recovery)
  - [Восстановление из XtraBackup](#восстановление-из-xtrabackup-1)
    - [Быстрое восстановление](#быстрое-восстановление)
  - [Восстановление отдельных таблиц](#восстановление-отдельных-таблиц)
    - [Из полного бэкапа](#из-полного-бэкапа)
  - [Тестирование восстановления](#тестирование-восстановления)
    - [План тестирования](#план-тестирования)
- [Обслуживание и оптимизация](#обслуживание-и-оптимизация)
  - [Регулярное обслуживание](#регулярное-обслуживание)
    - [ANALYZE TABLE](#analyze-table)
    - [OPTIMIZE TABLE](#optimize-table)
    - [CHECK TABLE](#check-table)
  - [Ротация логов](#ротация-логов)
    - [Binary logs](#binary-logs)
    - [Общие логи](#общие-логи)
  - [Управление дисковым пространством](#управление-дисковым-пространством)
    - [Мониторинг использования диска](#мониторинг-использования-диска)
    - [Архивация старых данных](#архивация-старых-данных)
- [Мониторинг и диагностика](#мониторинг-и-диагностика)
  - [Performance Schema](#performance-schema)
    - [Включение мониторинга](#включение-мониторинга)
    - [Мониторинг запросов](#мониторинг-запросов)
  - [Системные метрики](#системные-метрики)
    - [Нагрузка на сервер](#нагрузка-на-сервер)
    - [InnoDB метрики](#innodb-метрики)
  - [Графана + Prometheus](#графана-prometheus)
    - [Настройка экспортера](#настройка-экспортера)
    - [Dashboard в Grafana](#dashboard-в-grafana)
- [Безопасность](#безопасность)
  - [Защита сервера](#защита-сервера)
    - [Firewall настройки](#firewall-настройки)
    - [SSL/TLS шифрование](#ssltls-шифрование)
  - [Аудит и логирование](#аудит-и-логирование)
    - [General Query Log](#general-query-log)
    - [Audit Plugin (MySQL Enterprise)](#audit-plugin-mysql-enterprise)
  - [Управление уязвимостями](#управление-уязвимостями)
    - [Регулярные обновления](#регулярные-обновления)
    - [Безопасная конфигурация](#безопасная-конфигурация)
- [Масштабирование и высокая доступность](#масштабирование-и-высокая-доступность)
  - [Read/Write Splitting](#readwrite-splitting)
    - [Настройка ProxySQL](#настройка-proxysql)
  - [Автоматическое failover](#автоматическое-failover)
    - [MHA (Master High Availability)](#mha-master-high-availability)
    - [Orchestrator](#orchestrator)
  - [Кластеризация](#кластеризация)
    - [MySQL InnoDB Cluster](#mysql-innodb-cluster)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
    - [MySQL не запускается](#mysql-не-запускается)
    - [Медленные запросы](#медленные-запросы)
    - [Высокое использование памяти](#высокое-использование-памяти)
    - [Проблемы репликации](#проблемы-репликации)
  - [Диагностические инструменты](#диагностические-инструменты)
    - [Percona Toolkit](#percona-toolkit)
    - [MySQL Enterprise Monitor](#mysql-enterprise-monitor)
- [Автоматизация администрирования](#автоматизация-администрирования)
  - [Скрипты обслуживания](#скрипты-обслуживания)
    - [Ежедневное обслуживание](#ежедневное-обслуживание)
    - [Мониторинг с алертами](#мониторинг-с-алертами)
  - [Инфраструктура как код](#инфраструктура-как-код)
    - [Docker Compose для разработки](#docker-compose-для-разработки)
    - [Ansible для production](#ansible-для-production)
  - [Проектирование и архитектура](#проектирование-и-архитектура)
    - [1. Правильное планирование](#1-правильное-планирование)
    - [2. Безопасность прежде всего](#2-безопасность-прежде-всего)
    - [3. Производительность и масштабируемость](#3-производительность-и-масштабируемость)
  - [Администрирование](#администрирование)
    - [1. Автоматизация рутинных задач](#1-автоматизация-рутинных-задач)
    - [2. Мониторинг и оповещения](#2-мониторинг-и-оповещения)
    - [3. Резервное копирование и восстановление](#3-резервное-копирование-и-восстановление)
  - [Обслуживание и оптимизация](#обслуживание-и-оптимизация-1)
    - [1. Регулярное обслуживание](#1-регулярное-обслуживание)
    - [2. Управление ресурсами](#2-управление-ресурсами)
    - [3. Безопасность и compliance](#3-безопасность-и-compliance)
  - [Масштабирование и высокая доступность](#масштабирование-и-высокая-доступность-1)
    - [1. Горизонтальное масштабирование](#1-горизонтальное-масштабирование)
    - [2. Вертикальное масштабирование](#2-вертикальное-масштабирование)
    - [3. Disaster Recovery](#3-disaster-recovery)
  - [Документирование и обучение](#документирование-и-обучение)
    - [1. Документация](#1-документация)
    - [2. Обучение команды](#2-обучение-команды)
  - [Непрерывное улучшение](#непрерывное-улучшение)
    - [1. Регулярные аудиты](#1-регулярные-аудиты)
    - [2. Автоматизация и DevOps](#2-автоматизация-и-devops)
    - [3. Планирование и бюджетирование](#3-планирование-и-бюджетирование)
- [Лучшие практики](#лучшие-практики)

## Установка и первоначальная настройка

### Установка на разных ОС

#### Ubuntu/Debian

Команды установки и первоначальной настройки **MySQL** на **Ubuntu**/**Debian**.

```bash
# Обновление системы
sudo apt update && sudo apt upgrade -y

# Установка MySQL Server
sudo apt install mysql-server

# Проверка статуса
sudo systemctl status mysql

# Включение автозапуска
sudo systemctl enable mysql

# Безопасная настройка (интерактивно)
sudo mysql_secure_installation

# Или ручная настройка
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'StrongPassword123!';"
```

#### CentOS/RHEL
```bash
# Установка из репозитория
sudo yum install mysql-server

# Или для CentOS 8+
sudo dnf install mysql-server

# Запуск сервиса
sudo systemctl start mysqld
sudo systemctl enable mysqld

# Получение временного пароля
sudo grep 'temporary password' /var/log/mysqld.log

# Безопасная настройка
sudo mysql_secure_installation
```

#### Docker
```bash
# Запуск MySQL в Docker
docker run --name mysql-container \
  -e MYSQL_ROOT_PASSWORD=my-secret-pw \
  -e MYSQL_DATABASE=myapp \
  -e MYSQL_USER=myuser \
  -e MYSQL_PASSWORD=mypass \
  -p 3306:3306 \
  -v mysql-data:/var/lib/mysql \
  -d mysql:8.0

# Подключение к контейнеру
docker exec -it mysql-container mysql -u root -p

# Docker Compose
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: myapp
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppassword
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./my.cnf:/etc/mysql/my.cnf
volumes:
  mysql_data:
```

#### macOS (Homebrew)
```bash
# Установка через Homebrew
brew install mysql

# Запуск сервиса
brew services start mysql

# Безопасная настройка
mysql_secure_installation

# Или ручная настройка
mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'NewPassword123!';"
```

### Первоначальная конфигурация

#### Основной конфигурационный файл
```ini
# /etc/mysql/mysql.conf.d/mysqld.cnf или /etc/my.cnf

[mysqld]
# Основные настройки
bind-address = 0.0.0.0
port = 3306
socket = /var/run/mysqld/mysqld.sock

# Данные
datadir = /var/lib/mysql
tmpdir = /tmp

# Логирование
log_error = /var/log/mysql/error.log
slow_query_log = 1
slow_query_log_file = /var/log/mysql/mysql-slow.log
long_query_time = 2

# Безопасность
skip_name_resolve
max_connections = 100

# InnoDB настройки
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M

# MyISAM (если используется)
key_buffer_size = 256M

[mysql]
# Настройки клиента
default-character-set = utf8mb4

[client]
# Настройки подключения
default-character-set = utf8mb4
```

#### Проверка конфигурации
```bash
# Проверка синтаксиса конфигурации
mysqld --help --verbose | head -20

# Тестовый запуск
sudo systemctl stop mysql
sudo mysqld --initialize-insecure --user=mysql
sudo systemctl start mysql

# Проверка логов
tail -f /var/log/mysql/error.log
```

## Конфигурация сервера

### Оптимизация для разных нагрузок

#### OLTP (транзакционные системы)
```ini
[mysqld]
# Высокая производительность транзакций
innodb_buffer_pool_size = 4G
innodb_log_file_size = 512M
innodb_flush_log_at_trx_commit = 1
innodb_thread_concurrency = 16

# Подключения
max_connections = 200
innodb_max_dirty_pages_pct = 90

# Кэширование
query_cache_size = 256M
query_cache_type = ON
table_open_cache = 4096
```

#### OLAP (аналитические системы)
```ini
[mysqld]
# Оптимизация для чтения
innodb_buffer_pool_size = 8G
key_buffer_size = 2G

# Временные таблицы
tmp_table_size = 512M
max_heap_table_size = 512M

# Сортировка
sort_buffer_size = 4M
read_rnd_buffer_size = 1M

# MyISAM для аналитики
default_storage_engine = MyISAM
```

#### Высоконагруженные системы
```ini
[mysqld]
# Максимальная производительность
innodb_buffer_pool_size = 16G
innodb_buffer_pool_instances = 16
innodb_log_file_size = 1G
innodb_log_files_in_group = 3

# Подключения
max_connections = 1000
max_connect_errors = 1000000

# Потоки
thread_cache_size = 100
thread_stack = 256K

# Сеть
max_allowed_packet = 64M
net_buffer_length = 1M
```

### Динамическая конфигурация

#### Изменение настроек без перезапуска
```sql
-- Изменение глобальных переменных
SET GLOBAL max_connections = 150;
SET GLOBAL innodb_buffer_pool_size = 2147483648; -- 2GB

-- Проверка текущих значений
SHOW VARIABLES LIKE 'max_connections';
SHOW VARIABLES LIKE 'innodb_buffer_pool%';

-- Сохранение в конфигурационный файл
-- Необходимо добавить в my.cnf для постоянного применения
```

#### Опасные изменения
```sql
-- Эти изменения требуют перезапуска
SET GLOBAL innodb_log_file_size = 1073741824; -- 1GB
-- Требует остановки MySQL, удаления ib_logfile*, перезапуска

-- Изменение datadir требует полной миграции данных
```

### Мониторинг конфигурации

#### Проверка текущих настроек
```sql
-- Все переменные
SHOW VARIABLES;

-- Фильтр по шаблону
SHOW VARIABLES LIKE 'innodb%';
SHOW VARIABLES LIKE '%buffer%';

-- Глобальный статус
SHOW GLOBAL STATUS;

-- Сравнение с рекомендуемыми значениями
SELECT
    'innodb_buffer_pool_size' AS variable,
    @@innodb_buffer_pool_size / 1024 / 1024 / 1024 AS current_gb,
    (@@global.max_connections * 10) / 1024 AS recommended_mb
FROM dual;
```

## Управление пользователями и правами

### Создание пользователей

#### Локальные пользователи
```sql
-- Создание пользователя с паролем
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'StrongPassword123!';

-- Пользователь для репликации
CREATE USER 'repl'@'%' IDENTIFIED BY 'ReplPassword456!';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';

-- Временный пользователь
CREATE USER 'temp_user'@'localhost' IDENTIFIED BY 'TempPass789!'
PASSWORD EXPIRE INTERVAL 30 DAY;

-- Пользователь с ограничением ресурсов
CREATE USER 'limited_user'@'localhost'
IDENTIFIED BY 'LimitedPass101!'
WITH MAX_QUERIES_PER_HOUR 100
     MAX_UPDATES_PER_HOUR 50
     MAX_CONNECTIONS_PER_HOUR 10;
```

#### Современная аутентификация (MySQL 8.0+)
```sql
-- Использование caching_sha2_password (рекомендуется)
CREATE USER 'modern_user'@'localhost'
IDENTIFIED WITH caching_sha2_password BY 'ModernPass123!';

-- MySQL Native Password (для совместимости)
CREATE USER 'legacy_user'@'localhost'
IDENTIFIED WITH mysql_native_password BY 'LegacyPass456!';

-- SHA-256 Password
CREATE USER 'sha256_user'@'localhost'
IDENTIFIED WITH sha256_password BY 'Sha256Pass789!';
```

### Управление правами

#### Гранты и привилегии
```sql
-- Полный доступ к базе данных
GRANT ALL PRIVILEGES ON myapp.* TO 'app_user'@'localhost';

-- Ограниченные права
GRANT SELECT, INSERT, UPDATE ON myapp.users TO 'readonly_user'@'localhost';
GRANT SELECT ON myapp.* TO 'readonly_user'@'localhost';

-- Права на конкретные операции
GRANT CREATE, ALTER, DROP ON myapp.* TO 'schema_admin'@'localhost';
GRANT PROCESS, SHOW DATABASES ON *.* TO 'monitor_user'@'localhost';

-- Права на хранимые процедуры
GRANT EXECUTE ON PROCEDURE myapp.calculate_stats TO 'app_user'@'localhost';

-- Права на представления
GRANT SELECT ON myapp.user_summary TO 'analyst'@'localhost';
```

#### Ролевая модель (MySQL 8.0+)
```sql
-- Создание ролей
CREATE ROLE 'app_developer';
CREATE ROLE 'app_analyst';
CREATE ROLE 'app_admin';

-- Назначение привилегий ролям
GRANT SELECT, INSERT, UPDATE, DELETE ON myapp.* TO 'app_developer';
GRANT SELECT ON myapp.* TO 'app_analyst';
GRANT ALL PRIVILEGES ON myapp.* TO 'app_admin';

-- Создание пользователей и назначение ролей
CREATE USER 'dev_user'@'localhost' IDENTIFIED BY 'DevPass123!';
GRANT 'app_developer' TO 'dev_user'@'localhost';

CREATE USER 'analyst_user'@'localhost' IDENTIFIED BY 'AnalystPass456!';
GRANT 'app_analyst' TO 'analyst_user'@'localhost';

-- Активация ролей
SET DEFAULT ROLE 'app_developer' FOR 'dev_user'@'localhost';
SET DEFAULT ROLE 'app_analyst' FOR 'analyst_user'@'localhost';
```

### Аудит пользователей

#### Мониторинг активности
```sql
-- Просмотр всех пользователей
SELECT user, host, authentication_string, password_expired, account_locked
FROM mysql.user
ORDER BY user;

-- Проверка привилегий пользователя
SHOW GRANTS FOR 'app_user'@'localhost';

-- Просмотр активных подключений
SELECT user, host, db, command, time, state, info
FROM information_schema.processlist
ORDER BY time DESC;

-- История подключений (MySQL 8.0+)
SELECT
    user,
    host,
    event_name,
    count_star,
    sum_timer_wait / 1000000000 AS total_time_sec
FROM performance_schema.events_waits_summary_by_user_by_event_name
WHERE user IS NOT NULL
ORDER BY sum_timer_wait DESC;
```

#### Управление паролями
```sql
-- Изменение пароля
ALTER USER 'app_user'@'localhost' IDENTIFIED BY 'NewStrongPassword123!';

-- Сброс пароля root
-- Остановить MySQL
sudo systemctl stop mysql

-- Запустить в безопасном режиме
sudo mysqld_safe --skip-grant-tables &

-- Изменить пароль
mysql -u root
UPDATE mysql.user SET authentication_string = PASSWORD('NewRootPassword123!')
WHERE user = 'root' AND host = 'localhost';
FLUSH PRIVILEGES;
EXIT;

-- Перезапустить MySQL
sudo systemctl restart mysql

-- Истечение пароля
ALTER USER 'temp_user'@'localhost' PASSWORD EXPIRE;

-- Блокировка/разблокировка аккаунта
ALTER USER 'suspicious_user'@'localhost' ACCOUNT LOCK;
ALTER USER 'suspicious_user'@'localhost' ACCOUNT UNLOCK;
```

## Резервное копирование

### Логические бэкапы (mysqldump)

#### Полная резервная копия
```bash
# Резервная копия всех баз данных
mysqldump --all-databases \
  --user=root \
  --password \
  --single-transaction \
  --routines \
  --triggers \
  --events > full_backup.sql

# Резервная копия конкретной базы
mysqldump myapp \
  --user=backup_user \
  --password \
  --single-transaction \
  --routines \
  --triggers > myapp_backup.sql

# Только структура (без данных)
mysqldump --no-data myapp > myapp_schema.sql

# Только данные (без структуры)
mysqldump --no-create-info myapp > myapp_data.sql
```

#### Инкрементальные бэкапы
```bash
# Бэкап binary logs
mysql -e "FLUSH LOGS;"

# Копирование binary logs
cp /var/log/mysql/mysql-bin.* /backup/binary-logs/

# Point-in-time recovery
mysqlbinlog --start-datetime="2024-01-01 00:00:00" \
  --stop-datetime="2024-01-01 23:59:59" \
  mysql-bin.000001 > recovery.sql
```

### Физические бэкапы (Percona XtraBackup)

#### Горячий бэкап
```bash
# Установка XtraBackup
sudo apt install percona-xtrabackup-80

# Создание полного бэкапа
xtrabackup --backup \
  --user=backup_user \
  --password=backup_password \
  --target-dir=/backup/full_backup

# Подготовка бэкапа
xtrabackup --prepare --target-dir=/backup/full_backup

# Инкрементальный бэкап
xtrabackup --backup \
  --user=backup_user \
  --password=backup_password \
  --target-dir=/backup/inc_backup \
  --incremental-basedir=/backup/full_backup

# Подготовка инкрементального бэкапа
xtrabackup --prepare \
  --apply-log-only \
  --target-dir=/backup/full_backup \
  --incremental-dir=/backup/inc_backup
```

#### Восстановление из XtraBackup
```bash
# Остановка MySQL
sudo systemctl stop mysql

# Удаление старых данных
sudo rm -rf /var/lib/mysql/*

# Восстановление
xtrabackup --copy-back --target-dir=/backup/full_backup

# Исправление прав
sudo chown -R mysql:mysql /var/lib/mysql

# Запуск MySQL
sudo systemctl start mysql
```

### Автоматизированное резервное копирование

#### Скрипт для автоматизации
```bash
#!/bin/bash
# mysql_backup.sh

BACKUP_DIR="/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

# Создание директории
mkdir -p $BACKUP_DIR

# Полная резервная копия
mysqldump --all-databases \
  --user=backup_user \
  --password=$BACKUP_PASSWORD \
  --single-transaction \
  --routines \
  --triggers \
  --events > $BACKUP_DIR/full_backup_$DATE.sql

# Сжатие
gzip $BACKUP_DIR/full_backup_$DATE.sql

# Binary logs
mysql -e "FLUSH LOGS;"
cp /var/log/mysql/mysql-bin.* $BACKUP_DIR/binary_logs_$DATE/

# Очистка старых бэкапов
find $BACKUP_DIR -name "*.gz" -mtime +$RETENTION_DAYS -delete
find $BACKUP_DIR -name "binary_logs_*" -mtime +$RETENTION_DAYS -delete

# Отчет
echo "Backup completed: $BACKUP_DIR/full_backup_$DATE.sql.gz"
```

#### Cron задание
```bash
# Ежедневный бэкап в 2:00
0 2 * * * /path/to/mysql_backup.sh

# Ежечасный бэкап binary logs
0 * * * * mysql -e "FLUSH LOGS;"
```

## Восстановление данных

### Восстановление из mysqldump

#### Полное восстановление
```bash
# Остановка приложений
# ...

# Восстановление
mysql --user=root --password < full_backup.sql

# Или для конкретной базы
mysql --user=root --password myapp < myapp_backup.sql

# Проверка
mysql -e "SHOW DATABASES;"
mysql -e "USE myapp; SHOW TABLES;"
```

#### Point-in-Time Recovery
```bash
# Восстановление полного бэкапа
mysql < full_backup.sql

# Применение binary logs до нужного момента
mysqlbinlog --start-datetime="2024-01-01 00:00:00" \
  --stop-datetime="2024-01-01 12:00:00" \
  mysql-bin.000001 mysql-bin.000002 | mysql

# Или по позиции
mysqlbinlog --start-position=12345 \
  --stop-position=67890 \
  mysql-bin.000001 | mysql
```

### Восстановление из XtraBackup

#### Быстрое восстановление
```bash
# Остановка MySQL
sudo systemctl stop mysql

# Переименование старого datadir
mv /var/lib/mysql /var/lib/mysql_old

# Восстановление
xtrabackup --copy-back --target-dir=/backup/full_backup

# Исправление прав
chown -R mysql:mysql /var/lib/mysql

# Запуск MySQL
sudo systemctl start mysql

# Проверка
mysql -e "SHOW DATABASES;"
```

### Восстановление отдельных таблиц

#### Из полного бэкапа
```bash
# Извлечение таблицы из бэкапа
sed -n '/^-- Table structure for table `users`/,/^-- Table structure for table `/p' full_backup.sql > users_backup.sql

# Создание временной базы
mysql -e "CREATE DATABASE temp_restore;"

# Восстановление таблицы
mysql temp_restore < users_backup.sql

# Копирование данных
mysql -e "CREATE TABLE myapp.users_restored AS SELECT * FROM temp_restore.users;"

# Очистка
mysql -e "DROP DATABASE temp_restore;"
```

### Тестирование восстановления

#### План тестирования
```sql
-- Создание тестовой среды
CREATE DATABASE test_restore;

-- Восстановление в тестовую базу
mysql test_restore < backup_to_test.sql;

-- Проверка целостности
mysql test_restore -e "
  SELECT 'Users count:' as check_type, COUNT(*) as value FROM users
  UNION ALL
  SELECT 'Orders count:', COUNT(*) FROM orders
  UNION ALL
  SELECT 'Products count:', COUNT(*) FROM products;
"

-- Проверка связей
mysql test_restore -e "
  SELECT 'Orphaned orders:' as issue,
         COUNT(*) as count
  FROM orders o
  LEFT JOIN users u ON o.user_id = u.id
  WHERE u.id IS NULL;
"

-- Очистка тестовой среды
DROP DATABASE test_restore;
```

## Обслуживание и оптимизация

### Регулярное обслуживание

#### ANALYZE TABLE
```sql
-- Анализ таблиц для обновления статистики
ANALYZE TABLE users, orders, products;

-- Анализ всех таблиц в базе
mysql -e "
  SELECT CONCAT('ANALYZE TABLE ', table_schema, '.', table_name, ';')
  FROM information_schema.tables
  WHERE table_schema = 'myapp'
    AND table_type = 'BASE TABLE'
" | mysql

-- Проверка актуальности статистики
SELECT
    table_name,
    table_rows,
    avg_row_length,
    data_length / 1024 / 1024 AS data_mb,
    index_length / 1024 / 1024 AS index_mb
FROM information_schema.tables
WHERE table_schema = 'myapp';
```

#### OPTIMIZE TABLE
```sql
-- Оптимизация таблиц (перестройка и дефрагментация)
OPTIMIZE TABLE users, orders, products;

-- Для больших таблиц по одной
mysql -e "
  SET SESSION sql_log_bin = 0; -- Не логировать в binary log
  OPTIMIZE TABLE large_table;
"

-- Проверка фрагментации
SELECT
    table_name,
    data_free / 1024 / 1024 AS fragmentation_mb,
    (data_length + index_length) / 1024 / 1024 AS total_size_mb,
    ROUND(data_free / (data_length + index_length) * 100, 2) AS fragmentation_pct
FROM information_schema.tables
WHERE table_schema = 'myapp'
  AND data_free > 1024 * 1024; -- > 1MB фрагментации
```

#### CHECK TABLE
```sql
-- Проверка целостности таблиц
CHECK TABLE users, orders, products;

-- Расширенная проверка
CHECK TABLE users EXTENDED;

-- Быстрая проверка
CHECK TABLE users QUICK;

-- Проверка с исправлением (для MyISAM)
REPAIR TABLE myisam_table;
```

### Ротация логов

#### Binary logs
```sql
-- Просмотр binary logs
SHOW BINARY LOGS;

-- Ротация логов
FLUSH LOGS;

-- Удаление старых логов
PURGE BINARY LOGS BEFORE '2024-01-01 00:00:00';

-- Удаление по количеству
PURGE BINARY LOGS TO 'mysql-bin.000100';

-- Автоматическая ротация (cron)
0 */6 * * * mysql -e "FLUSH LOGS;"  # Каждые 6 часов
```

#### Общие логи
```sql
-- Ротация error log
sudo systemctl reload mysql  # Перечитывает конфигурацию

# Или
mysql -e "FLUSH ERROR LOGS;"

# Ротация slow query log
mysql -e "FLUSH SLOW LOGS;"

# Настройка logrotate
cat > /etc/logrotate.d/mysql << EOF
/var/log/mysql/*.log {
    daily
    rotate 30
    missingok
    compress
    postrotate
        systemctl reload mysql
    endscript
}
EOF
```

### Управление дисковым пространством

#### Мониторинг использования диска
```sql
-- Размер баз данных
SELECT
    table_schema AS database_name,
    ROUND(SUM(data_length + index_length) / 1024 / 1024 / 1024, 2) AS size_gb,
    COUNT(*) AS tables_count
FROM information_schema.tables
GROUP BY table_schema
ORDER BY size_gb DESC;

-- Размер таблиц
SELECT
    table_name,
    ROUND((data_length + index_length) / 1024 / 1024, 2) AS size_mb,
    table_rows,
    ROUND((data_length + index_length) / table_rows, 2) AS avg_row_size
FROM information_schema.tables
WHERE table_schema = 'myapp'
ORDER BY data_length + index_length DESC;
```

#### Архивация старых данных
```sql
-- Перемещение старых данных в архивную таблицу
CREATE TABLE orders_archive LIKE orders;

INSERT INTO orders_archive
SELECT * FROM orders
WHERE order_date < '2020-01-01';

DELETE FROM orders WHERE order_date < '2020-01-01';

-- Оптимизация после очистки
OPTIMIZE TABLE orders;

-- Автоматическая архивация (событие)
DELIMITER //

CREATE EVENT monthly_archive
ON SCHEDULE EVERY 1 MONTH STARTS '2024-02-01 02:00:00'
DO
BEGIN
    INSERT INTO orders_archive
    SELECT * FROM orders
    WHERE order_date < DATE_SUB(CURDATE(), INTERVAL 2 YEAR);

    DELETE FROM orders
    WHERE order_date < DATE_SUB(CURDATE(), INTERVAL 2 YEAR);

    OPTIMIZE TABLE orders;
END //

DELIMITER ;
```

## Мониторинг и диагностика

### Performance Schema

#### Включение мониторинга
```sql
-- Включение Performance Schema
UPDATE performance_schema.setup_instruments
SET ENABLED = 'YES', TIMED = 'YES'
WHERE NAME LIKE 'statement/%'
   OR NAME LIKE 'wait/%';

-- Создание сводного отчета
SELECT
    NOW() AS timestamp,
    (SELECT COUNT(*) FROM performance_schema.processlist WHERE command != 'Sleep') AS active_connections,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Queries') AS total_queries,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Slow_queries') AS slow_queries,
    (SELECT VARIABLE_VALUE FROM performance_schema.global_status WHERE VARIABLE_NAME = 'Innodb_buffer_pool_hit_rate') AS buffer_hit_rate
FROM dual;
```

#### Мониторинг запросов
```sql
-- Медленные запросы
SELECT
    sql_text,
    exec_count,
    avg_timer_wait / 1000000000 AS avg_time_sec,
    (sum_timer_wait / sum_timer_wait_total) * 100 AS pct_of_total_time
FROM performance_schema.events_statements_summary_by_digest
WHERE avg_timer_wait > 1000000000
ORDER BY sum_timer_wait DESC
LIMIT 10;

-- Ожидания по типам
SELECT
    event_name,
    count_star,
    sum_timer_wait / 1000000000 AS total_time_sec,
    avg_timer_wait / 1000000000 AS avg_time_sec
FROM performance_schema.events_waits_summary_global_by_event_name
WHERE event_name NOT LIKE 'wait/synch/%'
ORDER BY sum_timer_wait DESC
LIMIT 10;
```

### Системные метрики

#### Нагрузка на сервер
```sql
-- Использование ресурсов
SELECT
    'Uptime' AS metric,
    VARIABLE_VALUE / 3600 AS hours
FROM performance_schema.global_status
WHERE VARIABLE_NAME = 'Uptime'

UNION ALL

SELECT
    'Connections per hour',
    VARIABLE_VALUE / (VARIABLE_VALUE2 / 3600)
FROM performance_schema.global_status s1
JOIN performance_schema.global_status s2 ON s2.VARIABLE_NAME = 'Uptime'
WHERE s1.VARIABLE_NAME = 'Connections'

UNION ALL

SELECT
    'Queries per second',
    VARIABLE_VALUE / VARIABLE_VALUE2
FROM performance_schema.global_status s1
JOIN performance_schema.global_status s2 ON s2.VARIABLE_NAME = 'Uptime'
WHERE s1.VARIABLE_NAME = 'Queries';
```

#### InnoDB метрики
```sql
-- Статус InnoDB
SHOW ENGINE INNODB STATUS\G

-- Метрики буферного пула
SELECT
    pool_id,
    pool_size / 1024 / 1024 AS pool_size_mb,
    free_buffers,
    database_pages,
    old_database_pages,
    modified_database_pages,
    pending_decompress,
    pending_reads,
    pending_flush_lru,
    pending_flush_list,
    pages_made_young,
    pages_not_made_young
FROM information_schema.innodb_buffer_pool_stats;
```

### Графана + Prometheus

#### Настройка экспортера
```bash
# Установка MySQL Exporter
wget https://github.com/prometheus/mysqld_exporter/releases/download/v0.14.0/mysqld_exporter-0.14.0.linux-amd64.tar.gz
tar -xzf mysqld_exporter-0.14.0.linux-amd64.tar.gz
sudo mv mysqld_exporter-0.14.0.linux-amd64/mysqld_exporter /usr/local/bin/

# Создание пользователя для мониторинга
mysql -e "
  CREATE USER 'exporter'@'localhost' IDENTIFIED BY 'exporter_password' WITH MAX_USER_CONNECTIONS 3;
  GRANT PROCESS, REPLICATION CLIENT, SELECT ON *.* TO 'exporter'@'localhost';
"

# Создание сервиса systemd
sudo tee /etc/systemd/system/mysql_exporter.service > /dev/null <<EOF
[Unit]
Description=Prometheus MySQL Exporter
After=network.target

[Service]
User=prometheus
ExecStart=/usr/local/bin/mysqld_exporter \
  -config.my-cnf /etc/mysql_exporter.cnf \
  -web.listen-address=:9104

[Install]
WantedBy=multi-user.target
EOF

# Конфигурационный файл
sudo tee /etc/mysql_exporter.cnf > /dev/null <<EOF
[client]
user=exporter
password=exporter_password
EOF
```

#### Dashboard в Grafana
```json
{
  "title": "MySQL Overview",
  "panels": [
    {
      "title": "Active Connections",
      "targets": [
        {
          "expr": "mysql_global_status_threads_connected",
          "legendFormat": "Active Connections"
        }
      ]
    },
    {
      "title": "Queries per Second",
      "targets": [
        {
          "expr": "rate(mysql_global_status_queries[5m])",
          "legendFormat": "QPS"
        }
      ]
    },
    {
      "title": "Buffer Pool Hit Rate",
      "targets": [
        {
          "expr": "mysql_global_status_innodb_buffer_pool_hit_rate",
          "legendFormat": "Hit Rate %"
        }
      ]
    }
  ]
}
```

## Безопасность

### Защита сервера

#### Firewall настройки
```bash
# Разрешить только локальные подключения
sudo ufw allow from 127.0.0.1 to any port 3306

# Для приложений на том же сервере
sudo ufw allow from 192.168.1.0/24 to any port 3306

# Запретить внешние подключения
sudo ufw deny 3306

# Проверка правил
sudo ufw status
```

#### SSL/TLS шифрование
```ini
# my.cnf - SSL настройки
[mysqld]
# Путь к сертификатам
ssl_ca = /etc/mysql/ssl/ca.pem
ssl_cert = /etc/mysql/ssl/server-cert.pem
ssl_key = /etc/mysql/ssl/server-key.pem

# Требовать SSL
require_secure_transport = ON
```

```sql
-- Создание SSL пользователя
CREATE USER 'ssl_user'@'%'
IDENTIFIED BY 'ssl_password'
REQUIRE SSL;

-- Проверка SSL подключений
SHOW STATUS LIKE 'Ssl%';

-- В приложении
jdbc:mysql://localhost:3306/myapp?useSSL=true&requireSSL=true&verifyServerCertificate=false
```

### Аудит и логирование

#### General Query Log
```ini
[mysqld]
# Включение общего лога запросов
general_log = 1
general_log_file = /var/log/mysql/mysql.log

# Или логирование в таблицу
general_log = 1
general_log_file = mysql.general_log
```

#### Audit Plugin (MySQL Enterprise)
```sql
-- Установка audit plugin
INSTALL PLUGIN audit_log SONAME 'audit_log.so';

-- Настройка
SET GLOBAL audit_log_policy = 'ALL';
SET GLOBAL audit_log_include_accounts = 'app_user@%,admin@%';

-- Просмотр аудита
SELECT *
FROM mysql.audit_log
WHERE timestamp >= '2024-01-01'
ORDER BY timestamp DESC;
```

### Управление уязвимостями

#### Регулярные обновления
```bash
# Проверка версии
mysql --version

# Обновление на Ubuntu/Debian
sudo apt update && sudo apt upgrade mysql-server

# Обновление на CentOS/RHEL
sudo yum update mysql-server

# Проверка известных уязвимостей
# Используйте инструменты вроде OpenVAS или Nessus
```

#### Безопасная конфигурация
```ini
[mysqld]
# Отключение опасных функций
skip_symbolic_links = 1
local_infile = 0

# Ограничение ресурсов
max_connections = 100
max_user_connections = 10

# Безопасные пароли
validate_password_policy = STRONG
validate_password_length = 12

# Отключение старых протоколов
require_secure_transport = ON
```

## Масштабирование и высокая доступность

### Read/Write Splitting

#### Настройка ProxySQL
```bash
# Установка ProxySQL
wget https://github.com/sysown/proxysql/releases/download/v2.4.4/proxysql_2.4.4-ubuntu20_amd64.deb
sudo dpkg -i proxysql_2.4.4-ubuntu20_amd64.deb

# Настройка
sudo systemctl start proxysql
sudo systemctl enable proxysql

# Подключение к административной консоли
mysql -u admin -padmin -h 127.0.0.1 -P 6032

# Настройка серверов
INSERT INTO mysql_servers (hostgroup_id, hostname, port) VALUES
(1, 'master-host', 3306),  -- Write group
(2, 'slave1-host', 3306),  -- Read group
(2, 'slave2-host', 3306);  -- Read group

# Настройка правил маршрутизации
INSERT INTO mysql_query_rules (rule_id, active, match_pattern, destination_hostgroup, apply)
VALUES (1, 1, '^SELECT.*', 2, 1);  -- SELECT -> read group

INSERT INTO mysql_query_rules (rule_id, active, match_pattern, destination_hostgroup, apply)
VALUES (2, 1, '.*', 1, 1);  -- Other queries -> write group

# Загрузка конфигурации
LOAD MYSQL SERVERS TO RUNTIME;
LOAD MYSQL QUERY RULES TO RUNTIME;
SAVE MYSQL SERVERS TO DISK;
SAVE MYSQL QUERY RULES TO DISK;
```

### Автоматическое failover

#### MHA (Master `High` Availability)
```bash
# Установка MHA
sudo apt install mha4mysql-manager mha4mysql-node

# Конфигурация
# /etc/mha/app.conf
[server default]
manager_workdir = /var/log/mha/app
manager_log = /var/log/mha/app/manager.log
master_ip_failover_script = /usr/local/bin/master_ip_failover
master_ip_online_change_script = /usr/local/bin/master_ip_online_change

[server master]
hostname = master-host
candidate_master = 1

[server slave1]
hostname = slave1-host
candidate_master = 1

[server slave2]
hostname = slave2-host
candidate_master = 1

# Запуск мониторинга
masterha_manager --conf=/etc/mha/app.conf
```

#### Orchestrator
```bash
# Установка Orchestrator
wget https://github.com/github/orchestrator/releases/download/v3.2.6/orchestrator_3.2.6_amd64.deb
sudo dpkg -i orchestrator_3.2.6_amd64.deb

# Настройка
sudo systemctl start orchestrator
sudo systemctl enable orchestrator

# Web интерфейс: http://localhost:3000
```

### Кластеризация

#### MySQL InnoDB Cluster
```bash
# Установка MySQL Shell
sudo apt install mysql-shell

# Создание кластера
mysqlsh --uri root@master-host:3306

# В MySQL Shell:
var cluster = dba.createCluster('myCluster');
cluster.addInstance('root@slave1-host:3306');
cluster.addInstance('root@slave2-host:3306');
cluster.status();
```

## Решение проблем

### Распространенные проблемы

#### MySQL не запускается
```bash
# Проверка статуса
sudo systemctl status mysql

# Просмотр логов
sudo tail -f /var/log/mysql/error.log

# Распространенные причины:
# - Недостаточно памяти
# - Поврежденные файлы данных
# - Ошибки в конфигурации
# - Блокировка порта

# Исправление
sudo systemctl stop mysql
sudo mv /var/lib/mysql/ib_logfile* /tmp/  # Удалить лог файлы InnoDB
sudo systemctl start mysql
```

#### Медленные запросы
```sql
-- Поиск медленных запросов
SELECT
    sql_text,
    exec_count,
    avg_timer_wait / 1000000000 AS avg_time_sec
FROM performance_schema.events_statements_summary_by_digest
WHERE avg_timer_wait > 5000000000  -- > 5 секунд
ORDER BY avg_timer_wait DESC
LIMIT 10;

-- Анализ конкретного запроса
EXPLAIN FORMAT=JSON SELECT ...;

-- Возможные решения:
-- Создание индексов
-- Оптимизация запросов
-- Увеличение ресурсов
-- Кэширование
```

#### Высокое использование памяти
```sql
-- Проверка использования памяти
SELECT
    'Buffer Pool' AS component,
    @@innodb_buffer_pool_size / 1024 / 1024 / 1024 AS gb
FROM dual

UNION ALL

SELECT
    'Query Cache',
    @@query_cache_size / 1024 / 1024 / 1024
FROM dual;

-- Снижение размера кэшей
SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB
SET GLOBAL query_cache_size = 67108864; -- 64MB
```

#### Проблемы репликации
```sql
-- Проверка статуса репликации
SHOW SLAVE STATUS\G

-- Распространенные ошибки:
-- Last_Error: Описание ошибки
-- Duplicate entry (1062) - дублирование ключей
-- Could not execute (1032) - отсутствующие записи

-- Пропуск ошибок (осторожно!)
STOP SLAVE;
SET GLOBAL sql_slave_skip_counter = 1;
START SLAVE;

-- Перестройка репликации
STOP SLAVE;
RESET SLAVE ALL;
CHANGE MASTER TO MASTER_AUTO_POSITION = 1;
START SLAVE;
```

### Диагностические инструменты

#### Percona Toolkit
```bash
# Анализ индексов
pt-duplicate-key-checker --host localhost --user root --password

# Поиск неиспользуемых индексов
pt-index-usage --host localhost slow.log

# Анализ медленных запросов
pt-query-digest slow.log
```

#### MySQL Enterprise Monitor
```sql
-- Автоматическая диагностика
-- Предупреждения о проблемах
-- Рекомендации по оптимизации
-- Исторический анализ
```

## Автоматизация администрирования

### Скрипты обслуживания

#### Ежедневное обслуживание
```bash
#!/bin/bash
# daily_mysql_maintenance.sh

LOG_FILE="/var/log/mysql/maintenance.log"

echo "$(date): Starting daily maintenance" >> $LOG_FILE

# Анализ таблиц
mysql -e "
  SELECT CONCAT('ANALYZE TABLE ', table_schema, '.', table_name, ';')
  FROM information_schema.tables
  WHERE table_schema NOT IN ('mysql', 'information_schema', 'performance_schema')
    AND table_type = 'BASE TABLE'
" | mysql 2>> $LOG_FILE

# Оптимизация фрагментированных таблиц
mysql -e "
  SELECT CONCAT('OPTIMIZE TABLE ', table_schema, '.', table_name, ';')
  FROM information_schema.tables
  WHERE table_schema NOT IN ('mysql', 'information_schema', 'performance_schema')
    AND data_free > 1024 * 1024 * 10
" | mysql 2>> $LOG_FILE

# Ротация логов
mysql -e "FLUSH LOGS;" 2>> $LOG_FILE

# Очистка старых бэкапов (если автоматизированы)
find /backup/mysql -name "*.gz" -mtime +30 -delete 2>> $LOG_FILE

echo "$(date): Maintenance completed" >> $LOG_FILE
```

#### Мониторинг с алертами
```bash
#!/bin/bash
# mysql_monitor.sh

# Пороги
MAX_CONNECTIONS=80
MIN_BUFFER_HIT_RATE=95
MAX_SLOW_QUERIES_PER_MINUTE=10

# Получение метрик
CONNECTIONS=$(mysql -e "SHOW STATUS LIKE 'Threads_connected'" | awk 'NR==2{print $2}')
BUFFER_HIT_RATE=$(mysql -e "SHOW STATUS LIKE 'Innodb_buffer_pool_hit_rate'" | awk 'NR==2{print $2}')
SLOW_QUERIES=$(mysql -e "SHOW STATUS LIKE 'Slow_queries'" | awk 'NR==2{print $2}')

# Проверка порогов
if [ "$CONNECTIONS" -gt "$MAX_CONNECTIONS" ]; then
    echo "ALERT: High connections: $CONNECTIONS" | mail -s "MySQL Alert" admin@example.com
fi

if [ "$(echo "$BUFFER_HIT_RATE < $MIN_BUFFER_HIT_RATE" | bc -l)" -eq 1 ]; then
    echo "ALERT: Low buffer hit rate: $BUFFER_HIT_RATE%" | mail -s "MySQL Alert" admin@example.com
fi

# Логирование
echo "$(date): Connections=$CONNECTIONS, BufferHitRate=$BUFFER_HIT_RATE%, SlowQueries=$SLOW_QUERIES" >> /var/log/mysql/monitor.log
```

### Инфраструктура как код

#### Docker Compose для разработки
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: myapp
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppassword
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./my.cnf:/etc/mysql/my.cnf
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    command: --default-authentication-plugin=mysql_native_password

  adminer:
    image: adminer
    ports:
      - "8080:8080"
    depends_on:
      - mysql

volumes:
  mysql_data:
```

#### Ansible для production

### Проектирование и архитектура

#### 1. Правильное планирование
- **Анализ требований** перед установкой
- **Выбор подходящей версии MySQL**
- **Планирование capacity** и роста
- **Документирование архитектуры**

#### 2. Безопасность прежде всего
- **Принцип наименьших привилегий**
- **SSL для всех подключений**
- **Регулярные аудиты безопасности**
- **Мониторинг подозрительной активности**

#### 3. Производительность и масштабируемость
- **Оптимизация конфигурации** под нагрузку
- **Мониторинг и профилирование**
- **Планирование роста** и масштабирования
- **Регулярные тесты производительности**

### Администрирование

#### 1. Автоматизация рутинных задач
- **Скрипты для резервного копирования**
- **Автоматическое обслуживание**
- **Мониторинг с алертами**
- **Документирование процедур**

#### 2. Мониторинг и оповещения
- **Ключевые метрики** производительности
- **Алерты** на проблемы
- **Исторический анализ** тенденций
- **Прогнозирование** роста

#### 3. Резервное копирование и восстановление
- **Регулярные бэкапы** по расписанию
- **Тестирование восстановления**
- **Хранение бэкапов** в безопасном месте
- **Документированные процедуры** восстановления

### Обслуживание и оптимизация

#### 1. Регулярное обслуживание
- **ANALYZE TABLE** для обновления статистики
- **OPTIMIZE TABLE** для дефрагментации
- **CHECK TABLE** для проверки целостности
- **Ротация логов** для управления пространством

#### 2. Управление ресурсами
- **Мониторинг использования CPU**, памяти, диска
- **Оптимизация конфигурации** под нагрузку
- **Управление подключениями** и пулами
- **Кэширование** для улучшения производительности

#### 3. Безопасность и compliance
- **Регулярные обновления** и патчи
- **Аудит доступа** и активности
- **Шифрование** чувствительных данных
- **Соответствие** стандартам безопасности

### Масштабирование и высокая доступность

#### 1. Горизонтальное масштабирование
- **Read replicas** для масштабирования чтения
- **Sharding** для больших объемов данных
- **Кластеризация** для высокой доступности
- **Load balancing** для распределения нагрузки

#### 2. Вертикальное масштабирование
- **Увеличение ресурсов** сервера
- **Оптимизация приложений**
- **Кэширование** на разных уровнях
- **Архитектурные улучшения**

#### 3. Disaster Recovery
- **Геораспределенные** реплики
- **Автоматическое failover**
- **Тестирование** сценариев катастроф
- **Планы восстановления** бизнеса

### Документирование и обучение

#### 1. Документация
- **Архитектурные решения** и обоснования
- **Процедуры** установки и настройки
- **Runbooks** для распространенных проблем
- **Контакты** и **escalation paths**

#### 2. Обучение команды
- **Понимание архитектуры** системы
- **Навыки troubleshooting**
- **Процедуры восстановления**
- **Best practices** администрирования

### Непрерывное улучшение

#### 1. Регулярные аудиты
- **Проверка конфигурации** на соответствие **best practices**
- **Анализ производительности** и узких мест
- **Оценка безопасности** и уязвимостей
- **Проверка резервных копий**

#### 2. Автоматизация и DevOps
- **Infrastructure as Code** для воспроизводимости
- **CI/CD** для баз данных
- **Мониторинг as Code**
- **Автоматизированное тестирование**

#### 3. Планирование и бюджетирование
- **Capacity planning** для роста
- **Бюджетирование** ресурсов и лицензий
- **Планы** модернизации и обновлений
- **Risk assessment** и **mitigation**

## Лучшие практики

Регулярно выполняйте резервное копирование и проверяйте возможность восстановления: автоматизированные бэкапы без тестов восстановления создают ложное чувство безопасности. Настройте мониторинг ключевых метрик (QPS, задержки, использование диска, репликационный лаг) и алерты с разумными порогами, чтобы обнаруживать проблемы до влияния на пользователей.

Используйте отдельные учётные записи с минимальными привилегиями для приложений и администрирования; избегайте работы под `root`. Документируйте изменения конфигурации и версии, применяйте изменения по стадиям (dev staging production) и имейте план отката для критичных правок.

Администрирование **MySQL** — это комплексная дисциплина, требующая глубокого понимания базы данных, операционной системы, сетевых технологий и принципов высокой доступности. Правильное администрирование обеспечивает надежность, производительность и безопасность критически важных систем.

**Следующие темы:**
- [mysql-performance](mysql-performance.md) — мониторинг и тюнинг
- [mysql-replication](mysql-replication.md) — высокая доступность
- Резервное копирование — см. документацию **MySQL**

Профессиональное администрирование — это сочетание технических навыков, системного мышления и постоянного обучения!


