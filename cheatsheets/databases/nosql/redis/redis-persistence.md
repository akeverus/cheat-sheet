---
title: "Redis: Персистентность"
description: "Полное руководство по персистентности в Redis: RDB snapshots, AOF (Append Only File), конфигурация, оптимизация, восстановление данных"
tags:
  - redis
  - persistence
  - rdb
  - aof
  - backup
  - recovery
difficulty: "intermediate"
prerequisites: ["databases/redis-basics.md"]
next: ["databases/redis-replication.md", "databases/redis-backup-restore.md"]
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-performance.md"]
---

# **Redis**: Персистентность

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Persistence](https://redis.io/docs/management/persistence/) — персистентность RDB и AOF

### См. также
- [[redis-basics|redis-basics.md]] — основы Redis
- [[redis-replication|redis-replication.md]] — репликация

## Содержание

- [Введение в персистентность **Redis**](#введение-в-персистентность-redis)
  - [Типы персистентности](#типы-персистентности)
  - [Выбор стратегии персистентности](#выбор-стратегии-персистентности)
- [**RDB** (**Redis `Database` Backup**)](#rdb-redis-database-backup)
  - [Настройка **RDB**](#настройка-rdb)
  - [Процесс создания **RDB**](#процесс-создания-rdb)
  - [Оптимизация **RDB**](#оптимизация-rdb)
  - [Восстановление из **RDB**](#восстановление-из-rdb)
  - [Преимущества и недостатки **RDB**](#преимущества-и-недостатки-rdb)
- [**AOF** (**Append `Only` File**)](#aof-append-only-file)
  - [Настройка **AOF**](#настройка-aof)
  - [Формат **AOF**](#формат-aof)
  - [Перезапись **AOF**](#перезапись-aof)
  - [Процесс перезаписи **AOF**](#процесс-перезаписи-aof)
  - [Восстановление из **AOF**](#восстановление-из-aof)
  - [Оптимизация **AOF**](#оптимизация-aof)
  - [Преимущества и недостатки **AOF**](#преимущества-и-недостатки-aof)
- [Гибридный подход (**RDB + AOF**)](#гибридный-подход-rdb-aof)
  - [Настройка гибридного подхода](#настройка-гибридного-подхода)
  - [Как это работает](#как-это-работает)
  - [Преимущества гибридного подхода](#преимущества-гибридного-подхода)
- [Мониторинг персистентности](#мониторинг-персистентности)
  - [Команды для мониторинга](#команды-для-мониторинга)
  - [Метрики персистентности](#метрики-персистентности)
  - [Скрипт мониторинга](#скрипт-мониторинга)
- [Резервное копирование](#резервное-копирование)
  - [Автоматическое резервное копирование **RDB**](#автоматическое-резервное-копирование-rdb)
  - [Резервное копирование **AOF**](#резервное-копирование-aof)
  - [Резервное копирование в облако](#резервное-копирование-в-облако)
- [Восстановление данных](#восстановление-данных)
  - [Восстановление из **RDB**](#восстановление-из-rdb-1)
  - [Восстановление из **AOF**](#восстановление-из-aof-1)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Оптимизация **RDB**](#оптимизация-rdb-1)
  - [Оптимизация **AOF**](#оптимизация-aof-1)
  - [Оптимизация дисковых операций](#оптимизация-дисковых-операций)
- [Решение проблем](#решение-проблем)
  - [Проблема: **RDB** не создается](#проблема-rdb-не-создается)
  - [Проблема: **AOF** файл поврежден](#проблема-aof-файл-поврежден)
  - [Проблема: Медленное восстановление](#проблема-медленное-восстановление)
- [Лучшие практики](#лучшие-практики)
  - [Конфигурация](#конфигурация)
  - [Резервное копирование](#резервное-копирование-1)
  - [Мониторинг](#мониторинг)
- [**Advanced Persistence Configuration**](#advanced-persistence-configuration)
  - [**Tuning RDB Performance**](#tuning-rdb-performance)
  - [**Tuning AOF Performance**](#tuning-aof-performance)
  - [**Disk** I/O **Optimization**](#disk-i-o-optimization)
- [**Persistence Strategies** by **Use Case**](#persistence-strategies-by-use-case)
  - [**High Availability Setup**](#high-availability-setup)
  - [**Performance-Optimized Setup**](#performance-optimized-setup)
  - [**Memory-Constrained Setup**](#memory-constrained-setup)
- [**Monitoring and Alerting**](#monitoring-and-alerting)
  - [**Persistence Health Check Script**](#persistence-health-check-script)
  - [**Prometheus Metrics**](#prometheus-metrics)
- [**Disaster Recovery Planning**](#disaster-recovery-planning)
  - [**Recovery Time Objectives** (**RTO**)](#recovery-time-objectives-rto)
  - [**Recovery Point Objectives** (**RPO**)](#recovery-point-objectives-rpo)
  - [**Recovery Procedures**](#recovery-procedures)
- [**Performance Benchmarks**](#performance-benchmarks)
  - [**RDB** vs **AOF Performance**](#rdb-vs-aof-performance)
  - [**Disk** I/O **Impact**](#disk-i-o-impact)
- [**Advanced Backup Strategies**](#advanced-backup-strategies)
  - [**Incremental Backups**](#incremental-backups)
  - [**Point-in-Time Recovery**](#point-in-time-recovery)
- [**Best Practices Summary**](#best-practices-summary)
  - [**Configuration**](#configuration)
  - [**Backup**](#backup)
  - [**Monitoring**](#monitoring)
  - [**Recovery**](#recovery)
- [**Advanced Persistence Patterns**](#advanced-persistence-patterns)
  - [**Incremental Backups**](#incremental-backups-1)
  - [**Point-in-Time Recovery**](#point-in-time-recovery-1)

## Введение в персистентность **Redis**

**Redis** — это **in-memory** база данных, но она поддерживает различные механизмы персистентности для сохранения данных на диск. Понимание этих механизмов критически важно для обеспечения надежности и восстановления данных.

### Типы персистентности

1. **RDB (**Redis Database Backup**)**: Снимки состояния базы данных
2. **AOF (**Append Only File**)**: Лог всех операций записи
3. **Гибридный подход**: Комбинация **RDB** и **AOF**

### Выбор стратегии персистентности

- **RDB**: Быстрые снимки, минимальное использование диска, возможна потеря данных
- **AOF**: Максимальная надежность, больше места на диске, медленнее восстановление
- **Гибридный**: Баланс между надежностью и производительностью


## **RDB** (**Redis `Database` Backup**)

**RDB** создает снимки состояния базы данных в определенные моменты времени. Это компактный бинарный формат, который позволяет быстро сохранять и восстанавливать данные.

### Настройка **RDB**

#### Конфигурация в **redis.conf**

```conf
# Сохранение через интервалы
save 900 1      # Сохранить если минимум 1 ключ изменился за 900 секунд
save 300 10     # Сохранить если минимум 10 ключей изменились за 300 секунд
save 60 10000   # Сохранить если минимум 10000 ключей изменились за 60 секунд

# Отключить автоматическое сохранение
save ""

# Имя файла RDB
dbfilename dump.rdb

# Директория для RDB файлов
dir /var/lib/redis

# Сжатие RDB
rdbcompression yes

# Проверка целостности
rdbchecksum yes

# Остановить запись при ошибке сохранения
stop-writes-on-bgsave-error yes
```

#### Ручное сохранение

```redis
# Синхронное сохранение (блокирует сервер)
SAVE

# Асинхронное сохранение (не блокирует)
BGSAVE

# Проверка статуса последнего сохранения
LASTSAVE
```

### Процесс создания **RDB**

```bash
# Redis создает RDB через fork процесса
# 1. Родительский процесс продолжает обслуживать клиентов
# 2. Дочерний процесс записывает данные на диск
# 3. После завершения дочерний процесс завершается

# Проверка процесса сохранения
redis-cli INFO persistence
```

### Оптимизация **RDB**

```conf
# Использование сжатия для экономии места
rdbcompression yes

# Оптимизация для больших баз данных
stop-writes-on-bgsave-error no  # Продолжать работу при ошибке

# Использование фонового сохранения
# BGSAVE автоматически используется при достижении условий save
```

### Восстановление из **RDB**

```bash
# Redis автоматически загружает RDB при запуске
# Файл должен находиться в директории, указанной в dir

# Ручное восстановление
# 1. Остановить Redis
sudo systemctl stop redis

# 2. Скопировать RDB файл в директорию данных
cp backup.rdb /var/lib/redis/dump.rdb

# 3. Запустить Redis
sudo systemctl start redis

# Redis автоматически загрузит данные из dump.rdb
```

### Преимущества и недостатки **RDB**

**Преимущества:**
- Компактный формат
- Быстрое восстановление
- Минимальное влияние на производительность
- Подходит для резервного копирования

**Недостатки:**
- Возможна потеря данных между снимками
- Может быть медленным для больших баз данных
- Не сохраняет точное состояние на момент сбоя


## **AOF** (**Append `Only` File**)

**AOF** сохраняет каждую операцию записи в лог-файл. При перезапуске **Redis** воспроизводит эти команды для восстановления состояния.

### Настройка **AOF**

#### Конфигурация в **redis.conf**

```conf
# Включить AOF
appendonly yes

# Имя AOF файла
appendfilename "appendonly.aof"

# Политика синхронизации
appendfsync always      # Синхронизация при каждой команде (самый безопасный)
appendfsync everysec    # Синхронизация раз в секунду (рекомендуется)
appendfsync no          # Синхронизация через ОС (самый быстрый)

# Перезапись AOF
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb

# Загружать AOF при старте
aof-load-truncated yes

# Использовать RDB префикс для ускорения
aof-use-rdb-preamble yes
```

#### Политики синхронизации

```conf
# always: Максимальная надежность, но медленнее
appendfsync always

# everysec: Баланс между надежностью и производительностью
appendfsync everysec

# no: Максимальная производительность, но возможна потеря данных
appendfsync no
```

### Формат **AOF**

```redis
# Пример содержимого AOF файла
*2
$6
SELECT
$1
0
*3
$3
SET
$3
key
$5
value
*2
$3
GET
$3
key
```

### Перезапись **AOF**

**AOF** файл может расти очень большим. **Redis** автоматически перезаписывает его, создавая компактную версию.

```redis
# Ручная перезапись AOF
BGREWRITEAOF

# Автоматическая перезапись настраивается через:
auto-aof-rewrite-percentage 100  # Перезаписать если размер увеличился на 100%
auto-aof-rewrite-min-size 64mb   # Минимальный размер для перезаписи
```

### Процесс перезаписи **AOF**

```bash
# 1. Redis создает новый AOF файл
# 2. Записывает текущее состояние базы данных
# 3. Добавляет новые команды в буфер
# 4. После завершения заменяет старый файл новым
```

### Восстановление из **AOF**

```bash
# Redis автоматически загружает AOF при запуске
# Если AOF файл поврежден, Redis попытается восстановить его

# Ручное восстановление поврежденного AOF
redis-check-aof --fix appendonly.aof

# Проверка целостности AOF
redis-check-aof appendonly.aof
```

### Оптимизация **AOF**

```conf
# Использование RDB префикса для ускорения загрузки
aof-use-rdb-preamble yes

# Оптимизация синхронизации
no-appendfsync-on-rewrite yes  # Не синхронизировать во время перезаписи

# Увеличение буфера для перезаписи
aof-rewrite-incremental-fsync yes
```

### Преимущества и недостатки **AOF**

**Преимущества:**
- Максимальная надежность
- Минимальная потеря данных
- Легко читаемый формат
- Можно редактировать вручную

**Недостатки:**
- Больший размер файла
- Медленнее восстановление
- Больше операций записи на диск


## Гибридный подход (**RDB + AOF**)

Комбинация **RDB** и **AOF** обеспечивает баланс между надежностью и производительностью.

### Настройка гибридного подхода

```conf
# Включить оба механизма
save 900 1
save 300 10
save 60 10000
appendonly yes
appendfsync everysec

# Использовать RDB префикс в AOF
aof-use-rdb-preamble yes
```

### Как это работает

1. **RDB снимки**: Создаются периодически для быстрого восстановления
2. **AOF лог**: Записывает все операции между снимками
3. **При загрузке**: **Redis** сначала загружает **RDB**, затем применяет команды из **AOF**

### Преимущества гибридного подхода

- Быстрое восстановление из **RDB**
- Минимальная потеря данных благодаря **AOF**
- Оптимизированное использование диска
- Гибкость в настройке


## Мониторинг персистентности

### Команды для мониторинга

```redis
# Информация о персистентности
INFO persistence

# Статус последнего сохранения
LASTSAVE

# Проверка статуса BGSAVE
INFO persistence | grep rdb_bgsave_in_progress

# Проверка статуса перезаписи AOF
INFO persistence | grep aof_rewrite_in_progress
```

### Метрики персистентности

```redis
# RDB метрики
rdb_last_save_time          # Время последнего сохранения
rdb_last_bgsave_status      # Статус последнего BGSAVE
rdb_last_bgsave_time_sec    # Время последнего BGSAVE
rdb_current_bgsave_time_sec # Время текущего BGSAVE

# AOF метрики
aof_enabled                  # AOF включен
aof_rewrite_in_progress     # Перезапись в процессе
aof_rewrite_scheduled       # Перезапись запланирована
aof_last_rewrite_time_sec   # Время последней перезаписи
aof_current_rewrite_time_sec # Время текущей перезаписи
aof_last_bgrewrite_status   # Статус последней перезаписи
aof_last_write_status       # Статус последней записи
```

### Скрипт мониторинга

```bash
#!/bin/bash
# monitor_persistence.sh

REDIS_HOST="localhost"
REDIS_PORT=6379

# Получить информацию о персистентности
INFO=$(redis-cli -h $REDIS_HOST -p $REDIS_PORT INFO persistence)

# Проверить статус RDB
RDB_STATUS=$(echo "$INFO" | grep "rdb_last_bgsave_status" | cut -d: -f2 | tr -d ' ')
if [ "$RDB_STATUS" != "ok" ]; then
    echo "WARNING: RDB last save status is $RDB_STATUS"
fi

# Проверить статус AOF
AOF_STATUS=$(echo "$INFO" | grep "aof_last_write_status" | cut -d: -f2 | tr -d ' ')
if [ "$AOF_STATUS" != "ok" ]; then
    echo "WARNING: AOF last write status is $AOF_STATUS"
fi

# Проверить наличие активных операций
BGSAVE=$(echo "$INFO" | grep "rdb_bgsave_in_progress" | cut -d: -f2 | tr -d ' ')
AOF_REWRITE=$(echo "$INFO" | grep "aof_rewrite_in_progress" | cut -d: -f2 | tr -d ' ')

if [ "$BGSAVE" = "1" ]; then
    echo "INFO: RDB save in progress"
fi

if [ "$AOF_REWRITE" = "1" ]; then
    echo "INFO: AOF rewrite in progress"
fi
```


## Резервное копирование

### Автоматическое резервное копирование **RDB**

```bash
#!/bin/bash
# backup_rdb.sh

REDIS_DATA_DIR="/var/lib/redis"
BACKUP_DIR="/backup/redis"
DATE=$(date +%Y%m%d_%H%M%S)

# Создать директорию для бэкапов
mkdir -p "$BACKUP_DIR"

# Выполнить BGSAVE
redis-cli BGSAVE

# Ждать завершения сохранения
while [ "$(redis-cli LASTSAVE)" = "$(redis-cli LASTSAVE)" ]; do
    sleep 1
done

# Скопировать RDB файл
cp "$REDIS_DATA_DIR/dump.rdb" "$BACKUP_DIR/dump_$DATE.rdb"

# Сжать бэкап
gzip "$BACKUP_DIR/dump_$DATE.rdb"

# Удалить старые бэкапы (старше 7 дней)
find "$BACKUP_DIR" -name "dump_*.rdb.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR/dump_$DATE.rdb.gz"
```

### Резервное копирование **AOF**

```bash
#!/bin/bash
# backup_aof.sh

REDIS_DATA_DIR="/var/lib/redis"
BACKUP_DIR="/backup/redis/aof"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p "$BACKUP_DIR"

# Скопировать AOF файл
cp "$REDIS_DATA_DIR/appendonly.aof" "$BACKUP_DIR/appendonly_$DATE.aof"

# Сжать бэкап
gzip "$BACKUP_DIR/appendonly_$DATE.aof"

# Удалить старые бэкапы
find "$BACKUP_DIR" -name "appendonly_*.aof.gz" -mtime +7 -delete

echo "AOF backup completed: $BACKUP_DIR/appendonly_$DATE.aof.gz"
```

### Резервное копирование в облако

```bash
#!/bin/bash
# backup_to_s3.sh

REDIS_DATA_DIR="/var/lib/redis"
S3_BUCKET="my-redis-backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Создать RDB снимок
redis-cli BGSAVE
while [ "$(redis-cli LASTSAVE)" = "$(redis-cli LASTSAVE)" ]; do
    sleep 1
done

# Загрузить в S3
aws s3 cp "$REDIS_DATA_DIR/dump.rdb" "s3://$S3_BUCKET/rdb/dump_$DATE.rdb"

# Загрузить AOF
aws s3 cp "$REDIS_DATA_DIR/appendonly.aof" "s3://$S3_BUCKET/aof/appendonly_$DATE.aof"

echo "Backup uploaded to S3"
```


## Восстановление данных

### Восстановление из **RDB**

```bash
#!/bin/bash
# restore_from_rdb.sh

BACKUP_FILE="$1"
REDIS_DATA_DIR="/var/lib/redis"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file.rdb>"
    exit 1
fi

# Остановить Redis
sudo systemctl stop redis

# Создать резервную копию текущих данных
cp "$REDIS_DATA_DIR/dump.rdb" "$REDIS_DATA_DIR/dump.rdb.backup"

# Скопировать бэкап
cp "$BACKUP_FILE" "$REDIS_DATA_DIR/dump.rdb"

# Установить правильные права
chown redis:redis "$REDIS_DATA_DIR/dump.rdb"
chmod 644 "$REDIS_DATA_DIR/dump.rdb"

# Запустить Redis
sudo systemctl start redis

echo "Restore completed"
```

### Восстановление из **AOF**

```bash
#!/bin/bash
# restore_from_aof.sh

BACKUP_FILE="$1"
REDIS_DATA_DIR="/var/lib/redis"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file.aof>"
    exit 1
fi

# Остановить Redis
sudo systemctl stop redis

# Создать резервную копию
cp "$REDIS_DATA_DIR/appendonly.aof" "$REDIS_DATA_DIR/appendonly.aof.backup"

# Скопировать бэкап
cp "$BACKUP_FILE" "$REDIS_DATA_DIR/appendonly.aof"

# Проверить целостность
redis-check-aof --fix "$REDIS_DATA_DIR/appendonly.aof"

# Установить права
chown redis:redis "$REDIS_DATA_DIR/appendonly.aof"
chmod 644 "$REDIS_DATA_DIR/appendonly.aof"

# Запустить Redis
sudo systemctl start redis

echo "Restore completed"
```


## Оптимизация производительности

### Оптимизация **RDB**

```conf
# Использование сжатия
rdbcompression yes

# Оптимизация для больших баз данных
stop-writes-on-bgsave-error no

# Использование инкрементальной синхронизации
rdb-save-incremental-fsync yes
```

### Оптимизация **AOF**

```conf
# Использование RDB префикса
aof-use-rdb-preamble yes

# Оптимизация синхронизации
no-appendfsync-on-rewrite yes
aof-rewrite-incremental-fsync yes

# Настройка автоматической перезаписи
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
```

### Оптимизация дисковых операций

```conf
# Использование быстрого диска (SSD)
# Настройка файловой системы для производительности
# Использование отдельного диска для данных Redis
```


## Решение проблем

### Проблема: **RDB** не создается

```bash
# Проверить настройки save
redis-cli CONFIG GET save

# Проверить права на директорию
ls -la /var/lib/redis

# Проверить место на диске
df -h /var/lib/redis

# Проверить логи
tail -f /var/log/redis/redis-server.log
```

### Проблема: **AOF** файл поврежден

```bash
# Проверить целостность
redis-check-aof appendonly.aof

# Восстановить поврежденный файл
redis-check-aof --fix appendonly.aof

# Проверить размер файла
ls -lh appendonly.aof
```

### Проблема: Медленное восстановление

```bash
# Проверить размер файлов
ls -lh /var/lib/redis/

# Использовать RDB префикс в AOF
redis-cli CONFIG SET aof-use-rdb-preamble yes

# Оптимизировать AOF
redis-cli BGREWRITEAOF
```


## Лучшие практики

### Конфигурация

1. **Используйте гибридный подход** для **production**
2. **Настройте автоматические снимки** через **save**
3. **Используйте everysec** для **appendfsync**
4. **Включите `RDB` префикс** в **AOF**
5. **Настройте автоматическую перезапись AOF**

### Резервное копирование

1. **Регулярно создавайте бэкапы RDB** и **AOF**
2. **Храните бэкапы в разных местах**
3. **Тестируйте восстановление** регулярно
4. **Мониторьте размер файлов** персистентности
5. **Автоматизируйте процесс** резервного копирования

### Мониторинг

1. **Отслеживайте статус** сохранений
2. **Мониторьте размер файлов RDB** и **AOF**
3. **Проверяйте время** операций сохранения
4. **Настройте алерты** на ошибки персистентности
5. **Регулярно проверяйте** целостность файлов

## **Advanced Persistence Configuration**

### **Tuning RDB Performance**

```conf
# Оптимизация для больших баз данных
# Увеличить интервалы сохранения для снижения нагрузки
save 3600 1      # Сохранять раз в час если есть изменения
save 1800 10     # Сохранять каждые 30 минут если >=10 изменений
save 300 1000    # Сохранять каждые 5 минут если >=1000 изменений

# Отключить сохранение на диск для тестирования
save ""

# Оптимизация процесса сохранения
stop-writes-on-bgsave-error no  # Продолжать работу при ошибке
rdbcompression yes              # Использовать сжатие
rdbchecksum yes                 # Проверка целостности
```

### **Tuning AOF Performance**

```conf
# Оптимизация синхронизации
appendfsync everysec  # Баланс между надежностью и производительностью

# Оптимизация перезаписи
auto-aof-rewrite-percentage 100  # Перезаписывать при увеличении на 100%
auto-aof-rewrite-min-size 64mb   # Минимальный размер для перезаписи

# Не синхронизировать во время перезаписи
no-appendfsync-on-rewrite yes

# Инкрементальная синхронизация
aof-rewrite-incremental-fsync yes

# Использование RDB префикса для ускорения
aof-use-rdb-preamble yes
```

### **Disk** I/O **Optimization**

```conf
# Использование отдельного диска для данных
dir /mnt/redis-data

# Оптимизация файловой системы
# Использовать ext4 или xfs с правильными настройками
# noatime для снижения операций записи

# Настройка ядра Linux
# vm.overcommit_memory = 1
# net.core.somaxconn = 65535
```

## **Persistence Strategies** by **Use Case**

### **High Availability Setup**

```conf
# Максимальная надежность
appendonly yes
appendfsync always
save 60 1
save 300 10
save 900 1
aof-use-rdb-preamble yes
```

### **Performance-Optimized Setup**

```conf
# Максимальная производительность
appendonly yes
appendfsync everysec
save 900 1
save 300 10
save 60 10000
aof-use-rdb-preamble yes
no-appendfsync-on-rewrite yes
```

### **Memory-Constrained Setup**

```conf
# Минимальное использование диска
appendonly no
save 3600 1
save 1800 10
rdbcompression yes
```

## **Monitoring and Alerting**

### **Persistence Health Check Script**

```java
// Java пример проверки здоровья персистентности
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import java.time.Instant;
import java.time.Duration;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class PersistenceHealthChecker {
    private JedisPool jedisPool;

    public PersistenceHealthChecker(String host, int port, String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        this.jedisPool = new JedisPool(config, host, port, 2000, password);
    }

    public int checkPersistenceHealth() {
        List<String> issues = new ArrayList<>();

        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> persistenceInfo = jedis.info("persistence");

            // Проверить RDB
            String rdbSaveStatus = persistenceInfo.get("rdb_last_bgsave_status");
            if (!"ok".equals(rdbSaveStatus)) {
                issues.add("RDB last save status: " + rdbSaveStatus);
            }

            String rdbInProgress = persistenceInfo.get("rdb_bgsave_in_progress");
            if ("1".equals(rdbInProgress)) {
                issues.add("RDB save in progress");
            }

            // Проверить AOF
            String aofEnabled = persistenceInfo.get("aof_enabled");
            if ("1".equals(aofEnabled)) {
                String aofWriteStatus = persistenceInfo.get("aof_last_write_status");
                if (!"ok".equals(aofWriteStatus)) {
                    issues.add("AOF last write status: " + aofWriteStatus);
                }

                String aofRewrite = persistenceInfo.get("aof_rewrite_in_progress");
                if ("1".equals(aofRewrite)) {
                    issues.add("AOF rewrite in progress");
                }
            }

            // Проверить время последнего сохранения
            String lastSave = persistenceInfo.get("rdb_last_save_time");
            if (lastSave != null) {
                long lastSaveTime = Long.parseLong(lastSave);
                long currentTime = Instant.now().getEpochSecond();
                long timeDiff = currentTime - lastSaveTime;

                if (timeDiff > 3600) { // Больше часа
                    issues.add("Last RDB save was " + (timeDiff / 60) + " minutes ago");
                }
            }

            if (!issues.isEmpty()) {
                System.out.println("WARNING: Persistence issues detected:");
                issues.forEach(issue -> System.out.println("  - " + issue));
                return 1;
            } else {
                System.out.println("OK: Persistence is healthy");
                return 0;
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return 2;
        }
    }

    public void close() {
        jedisPool.close();
    }

    public static void main(String[] args) {
        PersistenceHealthChecker checker = new PersistenceHealthChecker(
            "localhost", 6379, null
        );
        int result = checker.checkPersistenceHealth();
        checker.close();
        System.exit(result);
    }
}
```

### **Prometheus Metrics**

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'redis-persistence'
    static_configs:
      - targets: ['localhost:9121']
```

```bash
# Redis Exporter для Prometheus
docker run -d \
  --name redis-exporter \
  -p 9121:9121 \
  -e REDIS_ADDR=redis://localhost:6379 \
  oliver006/redis_exporter
```

## **Disaster Recovery Planning**

### **Recovery Time Objectives** (**RTO**)

```bash
# RTO зависит от размера данных и типа персистентности
# RDB: Быстрое восстановление (секунды-минуты)
# AOF: Медленнее восстановление (минуты-часы)

# Тестирование времени восстановления
time redis-server --dbfilename test.rdb --dir /tmp
```

### **Recovery Point Objectives** (**RPO**)

```bash
# RPO зависит от настроек персистентности
# RDB: Возможна потеря данных между снимками
# AOF with always: Минимальная потеря (только последняя команда)
# AOF with everysec: Потеря до 1 секунды данных
```

### **Recovery Procedures**

```bash
#!/bin/bash
# disaster_recovery.sh

BACKUP_DIR="/backup/redis"
REDIS_DATA_DIR="/var/lib/redis"
LATEST_RDB=$(ls -t $BACKUP_DIR/rdb/*.rdb | head -1)
LATEST_AOF=$(ls -t $BACKUP_DIR/aof/*.aof | head -1)

echo "Starting disaster recovery..."

# Остановить Redis
systemctl stop redis

# Восстановить RDB
if [ -f "$LATEST_RDB" ]; then
    echo "Restoring RDB from $LATEST_RDB"
    cp "$LATEST_RDB" "$REDIS_DATA_DIR/dump.rdb"
fi

# Восстановить AOF
if [ -f "$LATEST_AOF" ]; then
    echo "Restoring AOF from $LATEST_AOF"
    cp "$LATEST_AOF" "$REDIS_DATA_DIR/appendonly.aof"
    redis-check-aof --fix "$REDIS_DATA_DIR/appendonly.aof"
fi

# Установить права
chown redis:redis "$REDIS_DATA_DIR"/*
chmod 644 "$REDIS_DATA_DIR"/*

# Запустить Redis
systemctl start redis

echo "Disaster recovery completed"
```

## **Performance Benchmarks**

### **RDB** vs **AOF Performance**

```bash
# Бенчмарк записи
redis-benchmark -t set -n 100000 -d 256

# Бенчмарк с RDB
redis-benchmark -t set -n 100000 -d 256 --rdb-compression

# Бенчмарк с AOF
redis-benchmark -t set -n 100000 -d 256 --aof
```

### **Disk** I/O **Impact**

```bash
# Мониторинг дисковых операций
iostat -x 1

# Мониторинг во время сохранения
watch -n 1 'iostat -x | grep -A 1 "Device"'
```

## **Advanced Backup Strategies**

### **Incremental Backups**

```bash
#!/bin/bash
# incremental_backup.sh

REDIS_DATA_DIR="/var/lib/redis"
BACKUP_DIR="/backup/redis/incremental"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p "$BACKUP_DIR"

# Создать RDB снимок
redis-cli BGSAVE
while [ "$(redis-cli LASTSAVE)" = "$(redis-cli LASTSAVE)" ]; do
    sleep 1
done

# Копировать только измененные файлы
rsync -av --link-dest="$BACKUP_DIR/latest" \
    "$REDIS_DATA_DIR/" \
    "$BACKUP_DIR/$DATE/"

# Обновить симлинк latest
rm -f "$BACKUP_DIR/latest"
ln -s "$DATE" "$BACKUP_DIR/latest"

echo "Incremental backup completed"
```

### **Point-in-Time Recovery**

```bash
#!/bin/bash
# point_in_time_recovery.sh

TARGET_TIME="$1"  # Unix timestamp
REDIS_DATA_DIR="/var/lib/redis"
BACKUP_DIR="/backup/redis"

# Найти ближайший RDB снимок до целевого времени
RDB_BACKUP=$(find "$BACKUP_DIR/rdb" -name "*.rdb" -newermt "@$TARGET_TIME" | head -1)

# Найти AOF файлы после RDB снимка
AOF_FILES=$(find "$BACKUP_DIR/aof" -name "*.aof" -newer "$RDB_BACKUP")

# Восстановить RDB
cp "$RDB_BACKUP" "$REDIS_DATA_DIR/dump.rdb"

# Применить AOF до целевого времени
# (упрощенная версия, требует дополнительной обработки)
for aof_file in $AOF_FILES; do
    # Применить команды из AOF до целевого времени
    # ...
done
```

## **Best Practices Summary**

### **Configuration**

1. **Используйте гибридный подход** (**RDB + AOF**) для **production**
2. **Настройте автоматические снимки** через **save**
3. **Используйте everysec** для **appendfsync** (**баланс**)
4. **Включите `RDB` префикс** в **AOF** для ускорения
5. **Настройте автоматическую перезапись AOF**

### **Backup**

1. **Регулярно создавайте бэкапы** (**ежедневно минимум**)
2. **Храните бэкапы в разных местах** (**локально и в облаке**)
3. **Тестируйте восстановление** еженедельно
4. **Мониторьте размер файлов** персистентности
5. **Автоматизируйте процесс** резервного копирования

### **Monitoring**

1. **Отслеживайте статус** сохранений через **INFO persistence**
2. **Мониторьте размер файлов RDB** и **AOF**
3. **Проверяйте время** операций сохранения
4. **Настройте алерты** на ошибки персистентности
5. **Регулярно проверяйте** целостность файлов

### **Recovery**

1. **Документируйте процедуры** восстановления
2. **Тестируйте восстановление** регулярно
3. **Храните несколько версий** бэкапов
4. **Проверяйте целостность** перед восстановлением
5. **Имейте план** на случай сбоя

## **Advanced Persistence Patterns**

### **Incremental Backups**

```bash
#!/bin/bash
# incremental_backup_strategy.sh

REDIS_DATA_DIR="/var/lib/redis"
BACKUP_DIR="/backup/redis"
DATE=$(date +%Y%m%d_%H%M%S)

# RDB snapshot
redis-cli BGSAVE
while [ "$(redis-cli LASTSAVE)" = "$(redis-cli LASTSAVE)" ]; do
    sleep 1
done

# Копировать только измененные файлы
rsync -av --link-dest="$BACKUP_DIR/latest" \
    "$REDIS_DATA_DIR/" \
    "$BACKUP_DIR/$DATE/"

# Обновить симлинк
rm -f "$BACKUP_DIR/latest"
ln -s "$DATE" "$BACKUP_DIR/latest"
```

### **Point-in-Time Recovery**

```bash
#!/bin/bash
# point_in_time_recovery.sh

TARGET_TIME="$1"  # Unix timestamp
REDIS_DATA_DIR="/var/lib/redis"
BACKUP_DIR="/backup/redis"

# Найти ближайший RDB снимок
RDB_BACKUP=$(find "$BACKUP_DIR/rdb" -name "*.rdb" -newermt "@$TARGET_TIME" | head -1)

# Восстановить RDB
cp "$RDB_BACKUP" "$REDIS_DATA_DIR/dump.rdb"

# Применить AOF до целевого времени
# (требует дополнительной обработки AOF файла)
```


- [Redis Persistence](https://redis.io/docs/management/persistence/)
- [Redis RDB](https://redis.io/docs/management/persistence/#rdb-snapshotting)
- [Redis AOF](https://redis.io/docs/management/persistence/#append-only-file)

