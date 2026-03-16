---
title: "MySQL: Репликация и высокая доступность - Полное руководство по кластеризации"
description: "Комплексное руководство по репликации MySQL: настройка, мониторинг, failover и стратегии высокой доступности."
tags: ["databases", "relational", "mysql-replication"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **MySQL**: Репликация и высокая доступность - Полное руководство по кластеризации

Комплексное руководство по репликации **MySQL**: настройка, мониторинг, **failover** и стратегии высокой доступности.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [MySQL Replication](https://dev.mysql.com/doc/refman/8.0/en/replication.html)
- [Replication Configuration](https://dev.mysql.com/doc/refman/8.0/en/replication-configuration.html)
- [Group Replication](https://dev.mysql.com/doc/refman/8.0/en/group-replication.html)

### Высокая доступность
- [MySQL High Availability](https://dev.mysql.com/doc/refman/8.0/en/ha-overview.html)
- [InnoDB Cluster](https://dev.mysql.com/doc/mysql-shell/8.0/en/mysql-innodb-cluster.html)
- [MySQL Router](https://dev.mysql.com/doc/mysql-router/8.0/en/)

### Инструменты
- [MySQL Shell](https://dev.mysql.com/doc/mysql-shell/8.0/en/)
- [MySQL Enterprise Monitor](https://dev.mysql.com/doc/refman/8.0/en/enterprise-monitor.html)
- [Percona XtraBackup](https://docs.percona.com/percona-xtrabackup/8.0/)

### См. также
- [Основы](mysql-basics.md) — **MySQL**
- [Производительность](mysql-performance.md) — производительность сервера
- [Администрирование](mysql-admin.md) — администрирование **MySQL**
- [Высокая доступность и инфраструктура](mysql-replication.md) — инфраструктура

## Содержание

- [Введение в репликацию **MySQL**](#введение-в-репликацию-mysql)
  - [Что такое репликация **MySQL**](#что-такое-репликация-mysql)
    - [Преимущества репликации:](#преимущества-репликации)
    - [Типы репликации **MySQL**:](#типы-репликации-mysql)
  - [Терминология](#терминология)
    - [Основные понятия](#основные-понятия)
    - [Статусы репликации](#статусы-репликации)
- [Архитектура репликации](#архитектура-репликации)
  - [Компоненты репликации](#компоненты-репликации)
    - [**Master** сервер](#master-сервер)
    - [**Slave** сервер](#slave-сервер)
    - [Процесс репликации](#процесс-репликации)
  - [Типы топологий репликации](#типы-топологий-репликации)
    - [**Master-Master** (**Active-Active**)](#master-master-active-active)
    - [**Master-Slave** с **Cascade**](#master-slave-с-cascade)
    - [**Ring Replication**](#ring-replication)
- [Настройка асинхронной репликации](#настройка-асинхронной-репликации)
  - [Подготовка серверов](#подготовка-серверов)
    - [Конфигурация **Master**](#конфигурация-master)
- [/etc/mysql/mysql.conf.d/mysqld.cnf](#etcmysqlmysqlconfdmysqldcnf)
- [Уникальный ID сервера](#уникальный-id-сервера)
- [Включение binary log](#включение-binary-log)
- [База данных для репликации (опционально)](#база-данных-для-репликации-опционально)
- [binlog_do_db = myapp](#binlog_do_db-myapp)
- [Исключение баз данных из репликации (опционально)](#исключение-баз-данных-из-репликации-опционально)
- [binlog_ignore_db = mysql](#binlog_ignore_db-mysql)
- [binlog_ignore_db = information_schema](#binlog_ignore_db-information_schema)
- [binlog_ignore_db = performance_schema](#binlog_ignore_db-performance_schema)
- [Формат binary log](#формат-binary-log)
- [Время жизни binary logs (дни)](#время-жизни-binary-logs-дни)
- [Размер binary log перед ротацией](#размер-binary-log-перед-ротацией)
- [Синхронизация binary log на диск](#синхронизация-binary-log-на-диск)
- [Пропуск ошибок репликации (осторожно!)](#пропуск-ошибок-репликации-осторожно)
- [slave_skip_errors = 1062,1452](#slave_skip_errors-10621452)
    - [Конфигурация **Slave**](#конфигурация-slave)
- [Уникальный ID сервера (отличный от master)](#уникальный-id-сервера-отличный-от-master)
- [Relay log](#relay-log)
- [Информация о master](#информация-о-master)
- [Автоматическое восстановление после ошибок](#автоматическое-восстановление-после-ошибок)
- [slave_net_timeout = 60](#slave_net_timeout-60)
- [Пропуск ошибок (только для восстановления!)](#пропуск-ошибок-только-для-восстановления)
- [slave_skip_errors = ddl_exist_errors](#slave_skip_errors-ddl_exist_errors)
  - [Настройка **Master**](#настройка-master)
    - [Создание пользователя для репликации](#создание-пользователя-для-репликации)
    - [Создание бэкапа для **Slave**](#создание-бэкапа-для-slave)
- [Создание консистентного бэкапа с mysqldump](#создание-консистентного-бэкапа-с-mysqldump)
- [Или с Percona XtraBackup](#или-с-percona-xtrabackup)
- [Восстановление на slave](#восстановление-на-slave)
  - [Настройка **Slave**](#настройка-slave)
    - [Подключение к **Master**](#подключение-к-master)
    - [Настройка **GTID**](#настройка-gtid)
      - [Включение **GTID** на **Master**](#включение-gtid-на-master)
- [my.cnf](#mycnf)
- [GTID](#gtid)
- [Дополнительные настройки](#дополнительные-настройки)
      - [Настройка **Slave** с **GTID**](#настройка-slave-с-gtid)
  - [Мониторинг базовой репликации](#мониторинг-базовой-репликации)
    - [Проверка статуса](#проверка-статуса)
    - [Диагностика проблем](#диагностика-проблем)
- [Полусинхронная репликация](#полусинхронная-репликация)
  - [Настройка полусинхронной репликации](#настройка-полусинхронной-репликации)
    - [На **Master**](#на-master)
    - [На **Slave**](#на-slave)
  - [Преимущества полусинхронной репликации](#преимущества-полусинхронной-репликации)
    - [Гарантии **durability**](#гарантии-durability)
    - [Производительность](#производительность)
  - [Ограничения и компромиссы](#ограничения-и-компромиссы)
    - [**Trade-offs**](#trade-offs)
- [**Group Replication**](#group-replication)
  - [Архитектура **Group Replication**](#архитектура-group-replication)
    - [Принцип работы](#принцип-работы)
    - [Режимы работы](#режимы-работы)
  - [Настройка **Group Replication**](#настройка-group-replication)
    - [Конфигурация всех серверов](#конфигурация-всех-серверов)
- [my.cnf для всех серверов](#mycnf-для-всех-серверов)
- [GTID обязательны](#gtid-обязательны)
- [SSL для безопасности](#ssl-для-безопасности)
- [Single-primary mode (по умолчанию)](#single-primary-mode-по-умолчанию)
    - [Инициализация группы](#инициализация-группы)
  - [Управление **Group Replication**](#управление-group-replication)
    - [Мониторинг состояния](#мониторинг-состояния)
    - [Переключение **Primary**](#переключение-primary)
    - [Восстановление после сбоя](#восстановление-после-сбоя)
- [**InnoDB Cluster**](#innodb-cluster)
  - [Компоненты **InnoDB Cluster**](#компоненты-innodb-cluster)
    - [**MySQL Shell**](#mysql-shell)
- [Установка MySQL Shell](#установка-mysql-shell)
- [Подключение](#подключение)
    - [**MySQL Router**](#mysql-router)
- [Установка MySQL Router](#установка-mysql-router)
- [Настройка для балансировки нагрузки](#настройка-для-балансировки-нагрузки)
  - [Создание **InnoDB Cluster**](#создание-innodb-cluster)
- [На каждом сервере создать пользователя для кластера](#на-каждом-сервере-создать-пользователя-для-кластера)
    - [Создание кластера через **MySQL Shell**](#создание-кластера-через-mysql-shell)
  - [Управление **InnoDB Cluster**](#управление-innodb-cluster)
    - [Операции с кластером](#операции-с-кластером)
    - [Автоматическое восстановление](#автоматическое-восстановление)
- [Мониторинг репликации](#мониторинг-репликации)
  - [Системные таблицы](#системные-таблицы)
    - [**Replication status**](#replication-status)
    - [**Performance Schema**](#performance-schema)
  - [Мониторинг задержки репликации](#мониторинг-задержки-репликации)
    - [**Seconds_Behind_Master**](#seconds_behind_master)
    - [**Heartbeat** таблица](#heartbeat-таблица)
  - [Мониторинг **Group Replication**](#мониторинг-group-replication)
    - [Статус группы](#статус-группы)
- [Управление и обслуживание](#управление-и-обслуживание)
  - [Ротация **Binary Logs**](#ротация-binary-logs)
    - [Автоматическая ротация](#автоматическая-ротация)
    - [Настройка хранения](#настройка-хранения)
- [Время жизни binary logs](#время-жизни-binary-logs)
- [Максимальный размер binary log](#максимальный-размер-binary-log)
- [Максимальное количество binary logs](#максимальное-количество-binary-logs)
  - [Перестройка репликации](#перестройка-репликации)
    - [Полная перестройка **Slave**](#полная-перестройка-slave)
    - [Переключение **Master-Slave** ролей](#переключение-master-slave-ролей)
- [**Failover** и восстановление](#failover-и-восстановление)
  - [Автоматический **Failover**](#автоматический-failover)
    - [Скрипт для **MHA** (**Master High Availability**)](#скрипт-для-mha-master-high-availability)
- [Установка MHA](#установка-mha)
- [Конфигурация MHA](#конфигурация-mha)
- [/etc/mha/app.conf](#etcmhaappconf)
- [Запуск MHA Manager](#запуск-mha-manager)
    - [**Orchestrator**](#orchestrator)
- [Установка Orchestrator](#установка-orchestrator)
- [Настройка](#настройка)
- [/etc/orchestrator.conf.json](#etcorchestratorconfjson)
- [Запуск](#запуск)
  - [Ручной **Failover**](#ручной-failover)
    - [Процедура переключения](#процедура-переключения)
    - [Восстановление **Master**](#восстановление-master)
    - [**Point-in-Time Recovery**](#point-in-time-recovery)
- [Распределенная репликация](#распределенная-репликация)
  - [**Multi-Source Replication**](#multi-source-replication)
    - [Настройка нескольких **Master**](#настройка-нескольких-master)
    - [Использование](#использование)
  - [Геораспределенная репликация](#геораспределенная-репликация)
    - [Настройка через **WAN**](#настройка-через-wan)
- [Конфигурация для медленных соединений](#конфигурация-для-медленных-соединений)
- [Увеличение таймаутов](#увеличение-таймаутов)
- [Сжатие репликации](#сжатие-репликации)
- [Размер пакетов](#размер-пакетов)
- [Кэширование](#кэширование)
    - [Мониторинг **WAN** репликации](#мониторинг-wan-репликации)
- [Безопасность репликации](#безопасность-репликации)
  - [**SSL** для репликации](#ssl-для-репликации)
    - [Настройка **SSL**](#настройка-ssl)
- [SSL настройки](#ssl-настройки)
- [Требовать SSL для репликации](#требовать-ssl-для-репликации)
    - [Настройка репликации с **SSL**](#настройка-репликации-с-ssl)
  - [Безопасность учетных записей](#безопасность-учетных-записей)
    - [Принцип наименьших привилегий](#принцип-наименьших-привилегий)
  - [Защита от несанкционированного доступа](#защита-от-несанкционированного-доступа)
    - [**Firewall** правила](#firewall-правила)
- [Разрешить только репликационный порт](#разрешить-только-репликационный-порт)
- [Или более строго](#или-более-строго)
- [Блокировать все остальные подключения к MySQL](#блокировать-все-остальные-подключения-к-mysql)
    - [Мониторинг подключений](#мониторинг-подключений)
- [Производительность репликации](#производительность-репликации)
  - [Оптимизация **Master**](#оптимизация-master)
    - [Конфигурация **Binary Log**](#конфигурация-binary-log)
- [Оптимальные настройки binary log](#оптимальные-настройки-binary-log)
- [Синхронизация](#синхронизация)
- [Размер и ротация](#размер-и-ротация)
- [Формат](#формат)
    - [Оптимизация **InnoDB** для репликации](#оптимизация-innodb-для-репликации)
- [Буферный пул](#буферный-пул)
- [Лог файлы](#лог-файлы)
- [Flush настройки](#flush-настройки)
  - [Оптимизация **Slave**](#оптимизация-slave)
- [Множественные SQL threads](#множественные-sql-threads)
- [Релей лог](#релей-лог)
- [Кэши](#кэши)
    - [Оптимизация для чтения](#оптимизация-для-чтения)
  - [Мониторинг производительности](#мониторинг-производительности)
    - [Метрики репликации](#метрики-репликации)
    - [Оптимизация запросов для репликации](#оптимизация-запросов-для-репликации)
- [**Troubleshooting**](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
    - [Репликация остановлена](#репликация-остановлена)
    - [Большая задержка репликации](#большая-задержка-репликации)
    - [Повреждение данных](#повреждение-данных)
  - [Диагностические команды](#диагностические-команды)
    - [Детальная диагностика](#детальная-диагностика)
    - [Логи и отладка](#логи-и-отладка)
    - [Скрипт восстановления](#скрипт-восстановления)
- [auto_recovery.sh](#auto_recoverysh)
- [**Best Practices**](#лучшие-практики)
  - [Проектирование репликации](#проектирование-репликации)
    - [Выбор топологии](#выбор-топологии)
    - [Размер кластера](#размер-кластера)
  - [Безопасность](#безопасность)
    - [Принципы безопасности](#принципы-безопасности)
  - [Мониторинг](#мониторинг)
    - [Ключевые метрики](#ключевые-метрики)
    - [Автоматизация мониторинга](#автоматизация-мониторинга)
  - [Обслуживание](#обслуживание)
    - [Регулярные задачи](#регулярные-задачи)
    - [Резервное копирование](#резервное-копирование)
    - [Оптимизация](#оптимизация)
    - [Масштабирование](#масштабирование)
  - [Документирование](#документирование)
    - [Важные документы](#важные-документы)
- [Troubleshooting](#решение-проблем)
  - [Ключевые преимущества репликации:](#ключевые-преимущества-репликации)
  - [Выбор решения:](#выбор-решения)
  - [Критические факторы успеха:](#критические-факторы-успеха)
  - [Будущие тенденции:](#будущие-тенденции)

## Введение в репликацию **MySQL**

### Что такое репликация **MySQL**

**Репликация MySQL** — это процесс копирования данных с одного сервера **MySQL** (**master**) на один или несколько других серверов (**slaves**). Это обеспечивает:**

#### Преимущества репликации:
- **Высокая доступность** — резервные серверы для **failover**
- **Масштабируемость чтения** — распределение нагрузки чтения
- **Резервное копирование** — горячее резервное копирование
- **Аналитика** — отдельные серверы для отчетов
- **Геораспределенность** — серверы в разных датацентрах

#### Типы репликации **MySQL**:
- **Асинхронная** — **master** не ждет подтверждения от **slave**
- **Полусинхронная** — **master** ждет подтверждения от одного **slave**
- **Group Replication** — синхронная репликация группы серверов
- **InnoDB Cluster** — полная кластерная решения

### Терминология

#### Основные понятия

Термины репликации **MySQL**: **Master** (**Primary**), **Slave** (**Replica**), **Binary Log**, **GTID**.

```text
Master (Primary) - основной сервер, источник данных
Slave (Replica)  - подчиненный сервер, копия данных
Binary Log       - журнал изменений на master
Relay Log        - журнал для применения на slave
Position         - позиция в binary log
GTID             - Global Transaction Identifier
```

#### Статусы репликации
```
Running    - репликация работает нормально
Stopped    - репликация остановлена
Error      - ошибка репликации
Connecting - попытка подключения
```

## Архитектура репликации

### Компоненты репликации

#### **Master** сервер
- **Binary Log** — журнал всех изменений данных
- **Dump Thread** — поток для отправки данных **slave**'ам
- **Binary Log Coordinates** — позиция для репликации

#### **Slave** сервер
- **IO Thread** — получает данные от **master**
- **SQL Thread** — применяет изменения к данным
- **Relay Log** — промежуточный журнал изменений
- **Master Info** — информация о подключении к **master**

#### Процесс репликации
```
1. Изменение данных на Master
2. Запись в Binary Log
3. Отправка изменений Slave (IO Thread)
4. Запись в Relay Log
5. Применение изменений (SQL Thread)
6. Подтверждение применения
```

### Типы топологий репликации

#### Один **Master** - Множество **Slaves**
```
Master
├── Slave 1 (Read-only)
├── Slave 2 (Read-only)
└── Slave 3 (Backup)
```
**Использование:** Масштабирование чтения, резервное копирование

#### **Master-Master** (**Active-Active**)
```
Master A ↔ Master B
```
**Использование:** Высокая доступность, распределенная нагрузка

#### **Master-Slave** с **Cascade**
```
Master
└── Slave 1
    ├── Slave 1.1
    └── Slave 1.2
```
**Использование:** Снижение нагрузки на **master**, геораспределение

#### **Ring Replication**
```
Master A → Master B → Master C → Master A
```
**Использование:** Специфические случаи, требует осторожности

## Настройка асинхронной репликации

### Подготовка серверов

#### Конфигурация **Master**
```ini
# /etc/mysql/mysql.conf.d/mysqld.cnf
[mysqld]
# Уникальный ID сервера
server-id = 1

# Включение binary log
log_bin = /var/log/mysql/mysql-bin.log

# База данных для репликации (опционально)
# binlog_do_db = myapp

# Исключение баз данных из репликации (опционально)
# binlog_ignore_db = mysql
# binlog_ignore_db = information_schema
# binlog_ignore_db = performance_schema

# Формат binary log
binlog_format = ROW

# Время жизни binary logs (дни)
expire_logs_days = 7

# Размер binary log перед ротацией
max_binlog_size = 100M

# Синхронизация binary log на диск
sync_binlog = 1

# Пропуск ошибок репликации (осторожно!)
# slave_skip_errors = 1062,1452
```

#### Конфигурация **Slave**
```ini
# /etc/mysql/mysql.conf.d/mysqld.cnf
[mysqld]
# Уникальный ID сервера (отличный от master)
server-id = 2

# Relay log
relay_log = /var/log/mysql/mysql-relay-bin.log

# Информация о master
master_info_repository = TABLE

# Автоматическое восстановление после ошибок
# slave_net_timeout = 60

# Пропуск ошибок (только для восстановления!)
# slave_skip_errors = ddl_exist_errors
```

### Настройка **Master**

#### Создание пользователя для репликации
```sql
-- На master сервере
CREATE USER 'repl'@'%' IDENTIFIED BY 'replication_password';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';

-- Проверка привилегий
SHOW GRANTS FOR 'repl'@'%';

-- Заблокировать таблицы для создания консистентного бэкапа
FLUSH TABLES WITH READ LOCK;

-- Получить текущую позицию binary log
SHOW MASTER STATUS;

-- Разблокировать таблицы (после создания бэкапа)
UNLOCK TABLES;
```

#### Создание бэкапа для **Slave**
```bash
# Создание консистентного бэкапа с mysqldump
mysqldump --all-databases --master-data --single-transaction > backup.sql

# Или с Percona XtraBackup
xtrabackup --backup --target-dir=/tmp/backup

# Восстановление на slave
mysql < backup.sql
```

### Настройка **Slave**

#### Подключение к **Master**
```sql
-- На slave сервере
CHANGE MASTER TO
    MASTER_HOST = 'master-host',
    MASTER_USER = 'repl',
    MASTER_PASSWORD = 'replication_password',
    MASTER_LOG_FILE = 'mysql-bin.000001',
    MASTER_LOG_POS = 12345;

-- Для GTID (MySQL 5.6+)
CHANGE MASTER TO
    MASTER_HOST = 'master-host',
    MASTER_USER = 'repl',
    MASTER_PASSWORD = 'replication_password',
    MASTER_AUTO_POSITION = 1;

-- Запуск репликации
START SLAVE;

-- Проверка статуса
SHOW SLAVE STATUS\G
```

#### Настройка **GTID**

##### Включение **GTID** на **Master**
```ini
# my.cnf
[mysqld]
# GTID
gtid_mode = ON
enforce_gtid_consistency = ON

# Дополнительные настройки
binlog_checksum = NONE
```

##### Настройка **Slave** с **GTID**
```sql
-- Автоматическое позиционирование
CHANGE MASTER TO
    MASTER_HOST = 'master-host',
    MASTER_USER = 'repl',
    MASTER_PASSWORD = 'replication_password',
    MASTER_AUTO_POSITION = 1;

START SLAVE;
```

### Мониторинг базовой репликации

#### Проверка статуса
```sql
-- Статус репликации
SHOW SLAVE STATUS\G

-- Важные поля для проверки:
-- Slave_IO_Running: Yes
-- Slave_SQL_Running: Yes
-- Seconds_Behind_Master: 0 (или небольшое число)
-- Last_Error: (пустое)

-- Статус master
SHOW MASTER STATUS;

-- Список binary logs
SHOW BINARY LOGS;

-- Просмотр событий в binary log
SHOW BINLOG EVENTS IN 'mysql-bin.000001' LIMIT 10;
```

#### Диагностика проблем
```sql
-- Если репликация остановлена
SHOW SLAVE STATUS\G

-- Проверка ошибок
-- Last_Error: Описание ошибки
-- Last_Errno: Код ошибки

-- Пропуск ошибок (осторожно!)
SET GLOBAL sql_slave_skip_counter = 1;
START SLAVE;

-- Или для GTID
SET GTID_NEXT = 'server-uuid:transaction-id';
BEGIN; COMMIT;
SET GTID_NEXT = AUTOMATIC;
START SLAVE;
```

## Полусинхронная репликация

### Настройка полусинхронной репликации

#### На **Master**
```sql
-- Установка плагина
INSTALL PLUGIN rpl_semi_sync_master SONAME 'semisync_master.so';

-- Включение
SET GLOBAL rpl_semi_sync_master_enabled = 1;

-- Перезапуск (или добавление в my.cnf)
-- rpl_semi_sync_master_enabled = 1

-- Проверка
SHOW VARIABLES LIKE 'rpl_semi_sync_master%';
```

#### На **Slave**
```sql
-- Установка плагина
INSTALL PLUGIN rpl_semi_sync_slave SONAME 'semisync_slave.so';

-- Включение
SET GLOBAL rpl_semi_sync_slave_enabled = 1;

-- Перезапуск slave threads
STOP SLAVE IO_THREAD;
START SLAVE IO_THREAD;

-- Проверка
SHOW VARIABLES LIKE 'rpl_semi_sync_slave%';
```

### Преимущества полусинхронной репликации

#### Гарантии **durability**
- **Master ждет подтверждения** от хотя бы одного **slave**
- **Данные не теряются** при сбое **master**
- **Автоматический failover** возможен без потери данных

#### Производительность
- **Минимальный overhead** по сравнению с синхронной репликацией
- **Подходит для большинства приложений**
- **Настраиваемое время ожидания**

### Ограничения и компромиссы

#### **Trade-offs**
```sql
-- Время ожидания (по умолчанию 10 секунд)
SET GLOBAL rpl_semi_sync_master_timeout = 10000; -- 10 секунд

-- Если slave не отвечает вовремя, репликация становится асинхронной
-- Возможна потеря производительности при медленных slave
-- Требует как минимум 2 slave для надежности
```

## **Group Replication**

### Архитектура **Group Replication**

#### Принцип работы
```
Серверы образуют группу
Каждый сервер знает о состоянии группы
Транзакции согласовываются между серверами
Автоматический выбор Primary сервера
```

#### Режимы работы
- **Single-Primary** — один **writable** сервер, остальные **read-only**
- **Multi-Primary** — все серверы **writable** (**требует осторожности**)

### Настройка **Group Replication**

#### Конфигурация всех серверов
```ini
# my.cnf для всех серверов
[mysqld]
# GTID обязательны
gtid_mode = ON
enforce_gtid_consistency = ON

# Group Replication
plugin_load_add = 'group_replication.so'
group_replication_group_name = 'aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee'
group_replication_start_on_boot = OFF
group_replication_local_address = 'server-ip:33061'
group_replication_group_seeds = 'server1:33061,server2:33061,server3:33061'
group_replication_bootstrap_group = OFF

# SSL для безопасности
group_replication_recovery_use_ssl = ON

# Single-primary mode (по умолчанию)
group_replication_single_primary_mode = ON
```

#### Инициализация группы
```sql
-- На первом сервере (bootstrap)
SET GLOBAL group_replication_bootstrap_group = ON;
START GROUP_REPLICATION;
SET GLOBAL group_replication_bootstrap_group = OFF;

-- На остальных серверах
START GROUP_REPLICATION;

-- Проверка статуса
SELECT * FROM performance_schema.replication_group_members;
```

### Управление **Group Replication**

#### Мониторинг состояния
```sql
-- Статус членов группы
SELECT
    member_id,
    member_host,
    member_port,
    member_state,
    member_role
FROM performance_schema.replication_group_members;

-- Статус группы
SELECT
    group_name,
    member_count,
    primary_member_id
FROM performance_schema.replication_group_members
GROUP BY group_name;
```

#### Переключение **Primary**
```sql
-- Просмотр текущего primary
SELECT variable_value AS primary_uuid
FROM performance_schema.global_variables
WHERE variable_name = 'group_replication_primary_member';

-- Принудительное переключение
SELECT group_replication_set_as_primary(member_id)
FROM performance_schema.replication_group_members
WHERE member_host = 'new-primary-host';
```

#### Восстановление после сбоя
```sql
-- Перезапуск Group Replication
STOP GROUP_REPLICATION;
START GROUP_REPLICATION;

-- Принудительное перезапуск группы (только если все серверы недоступны)
SET GLOBAL group_replication_bootstrap_group = ON;
START GROUP_REPLICATION;
SET GLOBAL group_replication_bootstrap_group = OFF;
```

## **InnoDB Cluster**

### Компоненты **InnoDB Cluster**

#### **MySQL Shell**
```bash
# Установка MySQL Shell
sudo apt install mysql-shell

# Подключение
mysqlsh --uri root@localhost:3306
```

#### **MySQL Router**
```bash
# Установка MySQL Router
sudo apt install mysql-router

# Настройка для балансировки нагрузки
mysqlrouter --bootstrap root@server1:3306 --directory /opt/mysqlrouter
```

### Создание **InnoDB Cluster**

#### Подготовка серверов
```bash
# На каждом сервере создать пользователя для кластера
mysql -u root -p -e "
  CREATE USER 'clusteradmin'@'%' IDENTIFIED BY 'clusterpass';
  GRANT ALL PRIVILEGES ON *.* TO 'clusteradmin'@'%' WITH GRANT OPTION;
  INSTALL PLUGIN group_replication SONAME 'group_replication.so';
"
```

#### Создание кластера через **MySQL Shell**
```javascript
// Подключение к MySQL Shell
var cluster = dba.createCluster('myCluster');

// Добавление серверов
cluster.addInstance('clusteradmin@server2:3306');
cluster.addInstance('clusteradmin@server3:3306');

// Проверка статуса
cluster.status();
```

### Управление **InnoDB Cluster**

#### Операции с кластером
```javascript
// Статус кластера
cluster.status();

// Перезапуск кластера
cluster.rebootClusterFromCompleteOutage();

// Добавление нового сервера
cluster.addInstance('clusteradmin@newserver:3306');

// Удаление сервера
cluster.removeInstance('server2:3306');

// Настройка MySQL Router
cluster.setupRouterInstance('root@router-host:3306');
```

#### Автоматическое восстановление
```javascript
// Включение автоматического восстановления
cluster.setOption('autoRejoinTries', 3);

// Проверка опций
cluster.options();
```

## Мониторинг репликации

### Системные таблицы

#### **Replication status**
```sql
-- Детальный статус репликации
SHOW SLAVE STATUS\G

-- Ключевые метрики:
-- Slave_IO_Running: Connecting/Yes/No
-- Slave_SQL_Running: Connecting/Yes/No
-- Seconds_Behind_Master: задержка в секундах
-- Master_Log_File: текущий binary log
-- Read_Master_Log_Pos: позиция чтения
-- Exec_Master_Log_Pos: позиция выполнения

-- Для Group Replication
SELECT * FROM performance_schema.replication_group_member_stats;
```

#### **Performance Schema**
```sql
-- Статистика репликации
SELECT
    channel_name,
    service_state,
    last_error_number,
    last_error_message,
    last_error_timestamp
FROM performance_schema.replication_connection_status;

-- Производительность репликации
SELECT
    thread_id,
    name,
    type,
    processlist_user,
    processlist_host,
    processlist_db,
    processlist_command,
    processlist_time
FROM performance_schema.threads
WHERE name LIKE '%replica%';
```

### Мониторинг задержки репликации

#### **Seconds_Behind_Master**
```sql
-- Текущая задержка
SHOW SLAVE STATUS\G -- Seconds_Behind_Master

-- Историческая задержка
CREATE TABLE replication_lag_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    measured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    seconds_behind_master INT,
    slave_host VARCHAR(100)
);

-- Сбор статистики
DELIMITER //

CREATE PROCEDURE monitor_replication_lag()
BEGIN
    INSERT INTO replication_lag_history (seconds_behind_master, slave_host)
    SELECT
        VARIABLE_VALUE,
        @@hostname
    FROM performance_schema.global_status
    WHERE VARIABLE_NAME = 'Seconds_Behind_Master';
END //

DELIMITER ;

-- Создание события для мониторинга
CREATE EVENT replication_monitor
ON SCHEDULE EVERY 1 MINUTE
DO CALL monitor_replication_lag();
```

#### **Heartbeat** таблица
```sql
-- Создание heartbeat таблицы на master
CREATE TABLE heartbeat (
    id INT PRIMARY KEY AUTO_INCREMENT,
    server_id INT,
    ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Вставка heartbeat записей
INSERT INTO heartbeat (server_id) VALUES (@@server_id);

-- Проверка задержки на slave
SELECT
    TIMESTAMPDIFF(SECOND, ts, NOW()) AS lag_seconds
FROM heartbeat
WHERE server_id = <master_server_id>
ORDER BY ts DESC
LIMIT 1;
```

### Мониторинг **Group Replication**

#### Статус группы
```sql
-- Члены группы
SELECT
    member_id,
    member_host,
    member_port,
    member_state,
    member_role,
    member_version
FROM performance_schema.replication_group_members;

-- Статистика группы
SELECT
    group_name,
    member_count,
    primary_member_id,
    communication_stack
FROM performance_schema.replication_group_members
GROUP BY group_name;

-- События группы
SELECT
    event_name,
    count_star,
    sum_timer_wait / 1000000000 AS total_time_sec
FROM performance_schema.events_waits_summary_by_event_name
WHERE event_name LIKE 'group_r%'
ORDER BY sum_timer_wait DESC;
```

## Управление и обслуживание

### Ротация **Binary Logs**

#### Автоматическая ротация
```sql
-- Проверка текущих binary logs
SHOW BINARY LOGS;

-- Принудительная ротация
FLUSH BINARY LOGS;

-- Удаление старых binary logs
PURGE BINARY LOGS BEFORE '2024-01-01 00:00:00';

-- Удаление по количеству
PURGE BINARY LOGS TO 'mysql-bin.000100';
```

#### Настройка хранения
```ini
# my.cnf
[mysqld]
# Время жизни binary logs
expire_logs_days = 7

# Максимальный размер binary log
max_binlog_size = 100M

# Максимальное количество binary logs
max_binlog_files = 10
```

### Перестройка репликации

#### Полная перестройка **Slave**
```sql
-- Остановка репликации
STOP SLAVE;

-- Сброс состояния
RESET SLAVE ALL;

-- Создание нового бэкапа master
-- mysqldump --all-databases --master-data > backup.sql

-- Восстановление на slave
-- mysql < backup.sql

-- Повторная настройка
CHANGE MASTER TO
    MASTER_HOST = 'master-host',
    MASTER_USER = 'repl',
    MASTER_PASSWORD = 'password',
    MASTER_AUTO_POSITION = 1;

START SLAVE;
```

#### Переключение **Master-Slave** ролей
```sql
-- На текущем master: остановить прием записей
FLUSH TABLES WITH READ LOCK;

-- На slave: дождаться применения всех изменений
SELECT MASTER_POS_WAIT('mysql-bin.000001', 12345);

-- На slave: остановить репликацию
STOP SLAVE;

-- На новом master: получить позицию
SHOW MASTER STATUS;

-- На старом slave (новый master): разблокировать
UNLOCK TABLES;

-- На новом slave: настроить репликацию на новый master
CHANGE MASTER TO MASTER_HOST = 'new-master';
START SLAVE;
```

## **Failover** и восстановление

### Автоматический **Failover**

#### Скрипт для **MHA** (**Master `High` Availability**)
```bash
# Установка MHA
sudo apt install mha4mysql-manager mha4mysql-node

# Конфигурация MHA
# /etc/mha/app.conf
[server default]
manager_workdir=/var/log/mha/app
manager_log=/var/log/mha/app/manager.log

[server1]
hostname=server1
candidate_master=1

[server2]
hostname=server2
candidate_master=1

# Запуск MHA Manager
masterha_manager --conf=/etc/mha/app.conf
```

#### **Orchestrator**
```bash
# Установка Orchestrator
wget https://github.com/github/orchestrator/releases/download/v3.2.6/orchestrator_3.2.6_amd64.deb
sudo dpkg -i orchestrator_3.2.6_amd64.deb

# Настройка
# /etc/orchestrator.conf.json
{
  "ListenAddress": ":3000",
  "MySQLTopologyUser": "orchestrator",
  "MySQLTopologyPassword": "password"
}

# Запуск
sudo systemctl start orchestrator
```

### Ручной **Failover**

#### Процедура переключения
```sql
-- 1. Проверить состояние репликации
SHOW SLAVE STATUS\G

-- 2. Остановить прием новых подключений на master
SET GLOBAL read_only = 1;

-- 3. Дождаться применения всех изменений на slave
SELECT MASTER_POS_WAIT(MASTER_LOG_FILE, MASTER_LOG_POS);

-- 4. Остановить репликацию на slave
STOP SLAVE;

-- 5. Повысить slave до master
RESET MASTER;

-- 6. Разблокировать старый master (если возможно)
SET GLOBAL read_only = 0;

-- 7. Перенаправить приложения на новый master
-- Обновить конфигурацию приложений
-- Обновить DNS или load balancer
```

### Восстановление после сбоя

#### Восстановление **Master**
```sql
-- 1. Запустить MySQL
sudo systemctl start mysql

-- 2. Проверить целостность данных
mysqlcheck --all-databases

-- 3. Восстановить из бэкапа если необходимо
mysql < backup.sql

-- 4. Настроить как slave нового master
CHANGE MASTER TO
    MASTER_HOST = 'new-master',
    MASTER_AUTO_POSITION = 1;

START SLAVE;
```

#### **Point-in-Time Recovery**
```sql
-- Восстановление до определенного момента
mysqlbinlog --start-datetime="2024-01-01 12:00:00" \
    --stop-datetime="2024-01-01 13:00:00" \
    mysql-bin.000001 mysql-bin.000002 | mysql
```

## Распределенная репликация

### **Multi-Source Replication**

#### Настройка нескольких **Master**
```sql
-- Добавление второго master
CHANGE MASTER TO
    MASTER_HOST = 'master2',
    MASTER_USER = 'repl',
    MASTER_PASSWORD = 'password',
    MASTER_AUTO_POSITION = 1
FOR CHANNEL 'master2';

-- Запуск репликации от второго master
START SLAVE FOR CHANNEL 'master2';

-- Проверка каналов
SHOW SLAVE STATUS FOR CHANNEL 'master1'\G
SHOW SLAVE STATUS FOR CHANNEL 'master2'\G
```

#### Использование
- **Консолидация данных** из нескольких источников
- **Миграция данных** между системами
- **Федеративные базы данных**

### Геораспределенная репликация

#### Настройка через **WAN**
```ini
# Конфигурация для медленных соединений
[mysqld]
# Увеличение таймаутов
slave_net_timeout = 60
slave_read_timeout = 300

# Сжатие репликации
slave_compressed_protocol = 1

# Размер пакетов
max_allowed_packet = 256M

# Кэширование
slave_transaction_retries = 10
```

#### Мониторинг **WAN** репликации
```sql
-- Задержка сети
SELECT
    Seconds_Behind_Master,
    (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(ts)) AS network_lag
FROM heartbeat
ORDER BY ts DESC LIMIT 1;

-- Пропускная способность
SHOW STATUS LIKE 'Binlog_cache_disk_use';
SHOW STATUS LIKE 'Binlog_cache_use';
```

## Безопасность репликации

### **SSL** для репликации

#### Настройка **SSL**
```ini
# my.cnf
[mysqld]
# SSL настройки
ssl_ca = /etc/mysql/ssl/ca.pem
ssl_cert = /etc/mysql/ssl/server-cert.pem
ssl_key = /etc/mysql/ssl/server-key.pem

# Требовать SSL для репликации
require_secure_transport = ON
```

#### Настройка репликации с **SSL**
```sql
-- Настройка slave с SSL
CHANGE MASTER TO
    MASTER_HOST = 'master-host',
    MASTER_USER = 'repl',
    MASTER_PASSWORD = 'password',
    MASTER_SSL = 1,
    MASTER_SSL_CA = '/etc/mysql/ssl/ca.pem',
    MASTER_SSL_CERT = '/etc/mysql/ssl/client-cert.pem',
    MASTER_SSL_KEY = '/etc/mysql/ssl/client-key.pem';
```

### Безопасность учетных записей

#### Принцип наименьших привилегий
```sql
-- Создание пользователя только для репликации
CREATE USER 'repl_user'@'slave-host' IDENTIFIED BY 'strong_password';
GRANT REPLICATION SLAVE ON *.* TO 'repl_user'@'slave-host' REQUIRE SSL;

-- Ограничение по IP
CREATE USER 'repl_user'@'192.168.1.%' IDENTIFIED BY 'password';
GRANT REPLICATION SLAVE ON *.* TO 'repl_user'@'192.168.1.%';

-- Временный пользователь для настройки
CREATE USER 'temp_repl'@'%' IDENTIFIED BY 'temp_pass';
GRANT REPLICATION SLAVE ON *.* TO 'temp_repl'@'%';
-- После настройки: DROP USER 'temp_repl'@'%';
```

### Защита от несанкционированного доступа

#### **Firewall** правила
```bash
# Разрешить только репликационный порт
sudo ufw allow from slave-ip to any port 3306

# Или более строго
sudo ufw allow from slave-ip to any port 3306 proto tcp

# Блокировать все остальные подключения к MySQL
sudo ufw deny 3306
```

#### Мониторинг подключений
```sql
-- Просмотр активных подключений
SELECT
    user,
    host,
    command,
    time,
    state,
    info
FROM information_schema.processlist
WHERE user = 'repl';

-- Логирование подключений
SET GLOBAL log_warnings = 2;
```

## Производительность репликации

### Оптимизация **Master**

#### Конфигурация **Binary Log**
```ini
# Оптимальные настройки binary log
[mysqld]
# Синхронизация
sync_binlog = 1
innodb_flush_log_at_trx_commit = 1

# Размер и ротация
max_binlog_size = 100M
expire_logs_days = 7

# Кэширование
binlog_cache_size = 4M
max_binlog_cache_size = 512M

# Формат
binlog_format = ROW
binlog_row_image = MINIMAL
```

#### Оптимизация **InnoDB** для репликации
```ini
[mysqld]
# Буферный пул
innodb_buffer_pool_size = 2G

# Лог файлы
innodb_log_file_size = 256M
innodb_log_files_in_group = 2

# Flush настройки
innodb_flush_method = O_DIRECT
innodb_flush_neighbors = 0
```

### Оптимизация **Slave**

#### Конфигурация **Slave**
```ini
[mysqld]
# Множественные SQL threads
slave_parallel_workers = 4
slave_parallel_type = LOGICAL_CLOCK

# Релей лог
relay_log_recovery = 1
relay_log_info_repository = TABLE

# Кэши
slave_transaction_retries = 10
slave_checkpoint_period = 300
```

#### Оптимизация для чтения
```sql
-- Настройка read-only slave
SET GLOBAL read_only = 1;
SET GLOBAL super_read_only = 1;

-- Исключение slave из нагрузки master
-- Использовать slave для отчетов и аналитики
```

### Мониторинг производительности

#### Метрики репликации
```sql
-- Скорость репликации
SHOW STATUS LIKE 'Binlog%';
SHOW STATUS LIKE 'Relay%';

-- Задержка репликации
SELECT
    Seconds_Behind_Master,
    (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(last_update)) AS lag_seconds
FROM slave_status;

-- Производительность I/O и SQL threads
SELECT
    thread_id,
    name,
    type,
    processlist_time,
    processlist_state
FROM performance_schema.threads
WHERE name LIKE '%slave%';
```

#### Оптимизация запросов для репликации
```sql
-- Избегать long-running транзакций
-- Использовать короткие транзакции
-- Оптимизировать запросы для быстрого выполнения

-- Мониторинг медленных запросов
SELECT
    sql_text,
    exec_count,
    avg_timer_wait / 1000000000 AS avg_time_sec
FROM performance_schema.events_statements_summary_by_digest
WHERE avg_timer_wait > 5000000000  -- > 5 секунд
ORDER BY avg_timer_wait DESC;
```

## Решение проблем

### Распространенные проблемы

#### Репликация остановлена
```sql
-- Проверка статуса
SHOW SLAVE STATUS\G

-- Возможные причины:
-- Last_Error: Описание ошибки
-- Last_Errno: Код ошибки

-- Для ошибок дублирования ключей (1062)
SET GLOBAL sql_slave_skip_counter = 1;
START SLAVE;

-- Для ошибок отсутствия таблиц (1146)
-- Проверить консистентность схемы
```

#### Большая задержка репликации
```sql
-- Диагностика
SHOW PROCESSLIST; -- Проверить выполняющиеся запросы

-- Возможные причины:
-- Медленные запросы на slave
-- Недостаток ресурсов (CPU, память, диск)
-- Сетевая задержка

-- Оптимизация:
-- Увеличить slave_parallel_workers
-- Оптимизировать запросы
-- Улучшить сетевое подключение
```

#### Повреждение данных
```sql
-- Проверка целостности
mysqlcheck --all-databases

-- Восстановление из бэкапа
mysql < backup.sql

-- Перестройка репликации
STOP SLAVE;
RESET SLAVE ALL;
-- Настроить заново
```

### Диагностические команды

#### Детальная диагностика
```sql
-- Полная информация о репликации
SHOW SLAVE STATUS\G

-- Ошибки репликации
SELECT
    last_error_number,
    last_error_message,
    last_error_timestamp
FROM performance_schema.replication_connection_status;

-- Статистика подключения
SELECT
    channel_name,
    service_state,
    last_heartbeat_timestamp,
    received_transaction_set
FROM performance_schema.replication_connection_status;
```

#### Логи и отладка
```sql
-- Включение подробного логирования
SET GLOBAL log_error_verbosity = 3;

-- Просмотр ошибок
SHOW ERROR LOGS;

-- Логи репликации
tail -f /var/log/mysql/error.log | grep -i replica

-- Binary log анализ
mysqlbinlog --base64-output=DECODE-ROWS mysql-bin.000001 | head -100
```

### Автоматическое восстановление

#### Скрипт восстановления
```bash
#!/bin/bash
# auto_recovery.sh

SLAVE_STATUS=$(mysql -e "SHOW SLAVE STATUS\G" | grep "Slave_IO_Running\|Slave_SQL_Running")

if echo "$SLAVE_STATUS" | grep -q "No"; then
    echo "Replication broken, attempting recovery..."

    # Остановить
    mysql -e "STOP SLAVE;"

    # Пропустить ошибку
    mysql -e "SET GLOBAL sql_slave_skip_counter = 1;"

    # Запустить
    mysql -e "START SLAVE;"

    # Проверить
    sleep 5
    mysql -e "SHOW SLAVE STATUS\G" | grep "Running"
fi
```

## Лучшие практики

### Проектирование репликации

#### Выбор топологии
1. **Один `Master` - Множество Slaves** для большинства случаев
2. **Master-Master** только при необходимости записи везде
3. **Group Replication** для высокой доступности
4. **Каскадная репликация** для снижения нагрузки на **master**

#### Размер кластера
- **2-3 сервера** для базовой `HA`
- **3-5 серверов** для **production** с нагрузкой
- **5+ серверов** для критически важных систем

### Безопасность

#### Принципы безопасности
1. **SSL для всех подключений репликации**
2. **Ограниченные привилегии** для пользователей репликации
3. **Firewall** на уровне сети
4. **Регулярный аудит** привилегий и логов

### Мониторинг

#### Ключевые метрики
1. **Seconds_Behind_Master** — задержка репликации
2. **Статус потоков** — `IO` и **SQL threads**
3. **Размер binary logs** — объем передаваемых данных
4. **Производительность** — время выполнения запросов

#### Автоматизация мониторинга
```sql
-- Создание алертов
DELIMITER //

CREATE PROCEDURE check_replication_health()
BEGIN
    DECLARE slave_status TEXT;
    DECLARE lag INT;

    SELECT GROUP_CONCAT(
        CONCAT('IO:', Slave_IO_Running, ' SQL:', Slave_SQL_Running)
    ) INTO slave_status
    FROM information_schema.processlist
    WHERE command = 'Slave';

    SELECT VARIABLE_VALUE INTO lag
    FROM information_schema.global_status
    WHERE VARIABLE_NAME = 'Seconds_Behind_Master';

    IF slave_status NOT LIKE '%Yes%Yes%' OR lag > 300 THEN
        -- Отправить алерт (интеграция с системой мониторинга)
        INSERT INTO alerts (message, severity, created_at)
        VALUES (CONCAT('Replication issue: ', slave_status, ' Lag: ', lag), 'CRITICAL', NOW());
    END IF;
END //

DELIMITER ;
```

### Обслуживание

#### Регулярные задачи
1. **Мониторинг состояния** репликации
2. **Ротация binary logs** для предотвращения роста
3. **Проверка целостности** данных
4. **Обновление конфигурации** при изменении нагрузки

#### Резервное копирование
1. **Резервные копии** с **master** и **slaves**
2. **Тестирование восстановления** из бэкапа
3. **Документирование** процедур **failover**

### Производительность

#### Оптимизация
1. **Быстрые диски** для **binary logs**
2. **Достаточная память** для буферов
3. **Оптимизированные запросы** на **master**
4. **Настройка сетевых параметров**

#### Масштабирование
1. **Read slaves** для масштабирования чтения
2. **Shard'инг** при росте нагрузки
3. **Кэширование** на уровне приложения
4. **Архитектурные улучшения**

### Документирование

#### Важные документы
1. **Топология репликации** с `IP` адресами и ролями
2. **Процедуры failover** с шагами и ответственными
3. **Контакты** для экстренных случаев
4. **История изменений** конфигурации

Репликация **MySQL** — это фундаментальная технология для обеспечения высокой доступности, масштабируемости и надежности баз данных **MySQL**. От простых **master-slave** конфигураций до сложных кластерных решений, репликация позволяет строить отказоустойчивые и производительные системы.

### Ключевые преимущества репликации:

1. **Высокая доступность** — автоматический **failover** и восстановление
2. **Масштабируемость** — распределение нагрузки чтения
3. **Надежность** — защита от потери данных
4. **Гибкость** — различные топологии для разных нужд

### Выбор решения:

- **Асинхронная репликация** — простота и производительность
- **Полусинхронная** — баланс между надежностью и скоростью
- **Group Replication** — автоматическая кластеризация
- **InnoDB Cluster** — полное **enterprise** решение

### Критические факторы успеха:

1. **Правильное проектирование** топологии
2. **Непрерывный мониторинг** состояния
3. **Регулярное обслуживание** и оптимизация
4. **Тестирование** сценариев отказов
5. **Документирование** всех процедур

### Будущие тенденции:

- **Автоматизация** управления кластерами
- **Контейнеризация** и оркестрация (**Kubernetes**)
- **Многооблачные** решения
- **ИИ-ассистированное** управление производительностью

Репликация **MySQL** продолжает развиваться, предлагая все более надежные и простые в управлении решения для высокой доступности баз данных. 🏗️

**Следующие темы:**
- [Производительность](mysql-performance.md) — тюнинг сервера **MySQL**
- [Администрирование](mysql-admin.md) — обслуживание **MySQL**
- [Инфраструктура](mysql-replication.md) — высокая доступность

Эффективная репликация — это искусство баланса между надежностью, производительностью и сложностью управления! 🔄


