---
title: "PostgreSQL: Резервное копирование и восстановление"
description: "Руководство по резервному копированию и восстановлению PostgreSQL: pg_dump, pg_restore, pg_basebackup, Continuous Archiving, Point-in-Time Recovery (PITR), именованные точки восстановления"
tags:
  - postgresql
  - backup
  - restore
  - pg_dump
  - pg_basebackup
  - pitr
  - continuous-archiving
difficulty: "advanced"
prerequisites: ["databases/postgres-basics.md", "databases/postgres-admin.md"]
next: ["databases/postgres-replication.md", "databases/postgres-high-availability.md"]
updated: "2026-02-11"
related: ["databases/postgres-monitoring.md", "databases/postgres-troubleshooting.md"]
---

# PostgreSQL: Резервное копирование и восстановление

## Полезные ссылки

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) — официальная документация
- [PostgreSQL Backup and Restore](https://www.postgresql.org/docs/current/backup.html) — резервное копирование

### См. также
- [[postgres-basics|postgres-basics.md]] — основы PostgreSQL
- [[postgres-replication|postgres-replication.md]] — репликация

## Содержание

- [Резервное копирование и восстановление **PostgreSQL**](#резервное-копирование-и-восстановление-postgresql)
  - [Типы резервного копирования](#типы-резервного-копирования)
  - [Когда какой тип использовать](#когда-какой-тип-использовать)
- [Логический дамп: **pg_dump**](#логический-дамп-pgdump)
  - [Основные сценарии **pg_dump**](#основные-сценарии-pgdump)
  - [Опции **pg_dump**](#опции-pgdump)
  - [Восстановление из **pg_dump**](#восстановление-из-pgdump)
  - [**pg_dumpall**](#pgdumpall)
- [Физический снимок: **pg_basebackup**](#физический-снимок-pgbasebackup)
  - [Основные сценарии **pg_basebackup**](#основные-сценарии-pgbasebackup)
- [**Continuous Archiving** и **Point-in-Time Recovery** (PITR)](#continuous-archiving-и-point-in-time-recovery-pitr)
  - [Настройка **Continuous Archiving**](#настройка-continuous-archiving)
  - [**Point-in-Time Recovery** (PITR)](#point-in-time-recovery-pitr)
  - [Именованные точки восстановления](#именованные-точки-восстановления)
- [Примеры скриптов резервного копирования](#примеры-скриптов-резервного-копирования)
  - [Ежедневный логический дамп](#ежедневный-логический-дамп)
  - [Инкрементальный снимок с **WAL** архивацией](#инкрементальный-снимок-с-wal-архивацией)
  - [Планировщик **cron**](#планировщик-cron)
  - [Планировщик **systemd timer**](#планировщик-systemd-timer)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
  - [Проблема: **pg_dump** завершается с ошибкой](#проблема-pgdump-завершается-с-ошибкой)
  - [Проблема: медленное восстановление больших баз](#проблема-медленное-восстановление-больших-баз)
  - [Проблема: **WAL** архивирование не работает](#проблема-wal-архивирование-не-работает)
- [**Advanced Backup Strategies**](#advanced-backup-strategies)
  - [**Streaming Replication** для бэкапов](#streaming-replication-для-бэкапов)
  - [**Incremental Backups** с **pgBackRest**](#incremental-backups-с-pgbackrest)
  - [**Cloud Backups**](#cloud-backups)
- [**Advanced Restore Techniques**](#advanced-restore-techniques)
  - [**Selective Table Restore**](#selective-table-restore)
  - [**Cross-Version Restore**](#cross-version-restore)
  - [**Point-in-Time Recovery Advanced**](#point-in-time-recovery-advanced)
- [**Backup Verification**](#backup-verification)
  - [**Automated Backup Verification**](#automated-backup-verification)
  - [**Backup Integrity Checks**](#backup-integrity-checks)
- [**Backup Monitoring and Alerting**](#backup-monitoring-and-alerting)
  - [**Backup Status Monitoring**](#backup-status-monitoring)
  - [**Automated Backup Alerts**](#automated-backup-alerts)
- [**Disaster Recovery Planning**](#disaster-recovery-planning)
  - [`DR` **Plan Template**](#dr-plan-template)
  - [**Recovery Time Objectives** (RTO) **and Recovery Point Objectives** (RPO)](#recovery-time-objectives-rto-and-recovery-point-objectives-rpo)
- [**Backup Compression and Optimization**](#backup-compression-and-optimization)
  - [**Advanced Compression Strategies**](#advanced-compression-strategies)
  - [**Incremental Backup Strategy**](#incremental-backup-strategy)
- [**Best Practices Summary**](#best-practices-summary)
  - [**Backup Strategy**](#backup-strategy)
  - [**Security**](#security)
  - [**Monitoring**](#monitoring)

## Резервное копирование и восстановление PostgreSQL

Резервное копирование — важнейшая часть эксплуатации любой базы данных. **PostgreSQL** предоставляет несколько встроенных способов резервного копирования, каждый из которых подходит для разных сценариев восстановления.

### Типы резервного копирования

1. **Логический дамп (Logical Dump)**: **pg_dump**, **pg_dumpall**
2. **Физический снимок (Physical Backup)**: **pg_basebackup**, файловый снимок
3. **Continuous Archiving**: непрерывная архивация **WAL** файлов
4. **Point-in-`Time Recovery` (PITR)**: восстановление на заданный момент времени

### Когда какой тип использовать

- **Логический дамп**: для одной БД, перенос, миграции, выборочное восстановление
- **Физический снимок**: для всего кластера, полное восстановление
- **Continuous Archiving**: для любой точки во времени, минимальная потеря данных


## Логический дамп: pg_dump

**pg_dump** создаёт логический дамп одной БД в **SQL** формате для последующего восстановления.

### Основные сценарии pg_dump

#### 1. Текстовый дамп (SQL)

**Простой вызов **pg_dump** с сжатием вывода:**

```bash
# дамп одной БД в файл
pg_dump -h localhost -U postgres -d mydb > mydb.sql

# дамп со сжатием
pg_dump -h localhost -U postgres -d mydb | gzip > mydb.sql.gz

# дамп в указанный файл
pg_dump -h localhost -U postgres -d mydb -f mydb.sql
```

#### 2. Custom формат (сжатый бинарный)

```bash
# Custom формат (сжатый)
pg_dump -h localhost -U postgres -d mydb -Fc -f mydb.dump

# с максимальной степенью сжатия
pg_dump -h localhost -U postgres -d mydb -Fc -Z 9 -f mydb.dump
```

#### 3. Directory формат (параллельный дамп)

```bash
# Directory формат для параллельного восстановления
pg_dump -h localhost -U postgres -d mydb -Fd -f mydb_backup

# с указанием числа потоков
pg_dump -h localhost -U postgres -d mydb -Fd -j 4 -f mydb_backup
```

#### 4. Tar формат

```bash
# Tar формат
pg_dump -h localhost -U postgres -d mydb -Ft -f mydb.tar
```

### Опции pg_dump

#### Выбор объектов

```bash
# только схема БД (без данных)
pg_dump -h localhost -U postgres -d mydb --schema-only -f schema.sql

# только данные таблиц (без схемы)
pg_dump -h localhost -U postgres -d mydb --data-only -f data.sql

# дамп конкретной таблицы
pg_dump -h localhost -U postgres -d mydb -t users -f users.sql

# дамп конкретной схемы
pg_dump -h localhost -U postgres -d mydb -n public -f public_schema.sql

# исключить таблицу
pg_dump -h localhost -U postgres -d mydb -T logs -f mydb_no_logs.sql

# дамп без владельца и привилегий
pg_dump -h localhost -U postgres -d mydb --no-owner --no-privileges -f mydb.sql
```

#### Дополнительные опции

```bash
# подробный вывод
pg_dump -h localhost -U postgres -d mydb -v -f mydb.sql

# игнорировать версию
pg_dump -h localhost -U postgres -d mydb --ignore-version -f mydb.sql

# таймаут блокировки
pg_dump -h localhost -U postgres -d mydb --lock-wait-timeout=10000 -f mydb.sql

# дамп с именованным snapshot
pg_dump -h localhost -U postgres -d mydb --snapshot=snapshot_name -f mydb.sql
```

### Восстановление из pg_dump

#### Восстановление из SQL дампа

```bash
# восстановление из текстового дампа
psql -h localhost -U postgres -d mydb < mydb.sql

# или через psql
psql -h localhost -U postgres -d mydb -f mydb.sql
```

#### Восстановление из Custom дампа

```bash
# восстановление из custom дампа
pg_restore -h localhost -U postgres -d mydb -v mydb.dump

# восстановление только схемы
pg_restore -h localhost -U postgres -d mydb --schema-only -v mydb.dump

# восстановление только данных
pg_restore -h localhost -U postgres -d mydb --data-only -v mydb.dump

# восстановление конкретной таблицы
pg_restore -h localhost -U postgres -d mydb -t users -v mydb.dump

# восстановление с созданием БД
pg_restore -h localhost -U postgres -d mydb --create -v mydb.dump
```

#### Восстановление из Directory дампа

```bash
# восстановление из directory дампа (параллельно)
pg_restore -h localhost -U postgres -d mydb -j 4 -v mydb_backup
```

### pg_dumpall

**pg_dumpall** создаёт дамп всех БД кластера **PostgreSQL**.

```bash
# дамп всех БД кластера
pg_dumpall -h localhost -U postgres > all_databases.sql

# только глобальные объекты (роли, настройки)
pg_dumpall -h localhost -U postgres --globals-only > globals.sql

# только схемы всех БД
pg_dumpall -h localhost -U postgres --schema-only > schemas.sql

# восстановление
psql -h localhost -U postgres < all_databases.sql
```


## Физический снимок: pg_basebackup

**pg_basebackup** создаёт физический снимок всего кластера **PostgreSQL**. Для полного снимка нужна репликация или носитель.

### Основные сценарии pg_basebackup

#### 1. Базовый снимок

```bash
# базовый снимок
pg_basebackup -h localhost -U postgres -D /backup/postgresql -Ft -z -P

# в формате plain
pg_basebackup -h localhost -U postgres -D /backup/postgresql -Fp -P
```

#### 2. Опции pg_basebackup

```bash
# Tar формат со сжатием
pg_basebackup -h localhost -U postgres -D /backup/postgresql -Ft -z -P

# Plain формат (каталог данных)
pg_basebackup -h localhost -U postgres -D /backup/postgresql -Fp -P

# с указанием слота репликации
pg_basebackup -h localhost -U postgres -D /backup/postgresql -S backup_slot -Fp -P

# с параметром checkpoint
pg_basebackup -h localhost -U postgres -D /backup/postgresql -c fast -Fp -P

# с меткой label
pg_basebackup -h localhost -U postgres -D /backup/postgresql -l "Backup $(date +%Y%m%d)" -Fp -P
```

#### 3. Восстановление из pg_basebackup

```bash
# остановить PostgreSQL
sudo systemctl stop postgresql

# очистить каталог данных
rm -rf /var/lib/postgresql/data/*

# распаковать из архива
tar -xzf /backup/postgresql/base.tar.gz -C /var/lib/postgresql/data/
tar -xzf /backup/postgresql/pg_wal.tar.gz -C /var/lib/postgresql/data/

# или для plain снимка
cp -r /backup/postgresql/* /var/lib/postgresql/data/

# включить recovery (если нужно)
echo "restore_command = 'cp /backup/wal_archive/%f %p'" >> /var/lib/postgresql/data/postgresql.conf

# запустить PostgreSQL
sudo systemctl start postgresql
```


## Continuous Archiving и Point-in-Time Recovery (PITR)

**Continuous Archiving** — непрерывная архивация **WAL** файлов для восстановления на любой момент времени.

### Настройка Continuous Archiving

#### 1. Параметры postgresql.conf

```conf
# уровень WAL архивации
wal_level = replica

# включить архивацию
archive_mode = on
archive_command = 'test ! -f /backup/wal_archive/%f && cp %p /backup/wal_archive/%f'

# или с использованием rsync
archive_command = 'test ! -f /backup/wal_archive/%f && rsync -a %p /backup/wal_archive/%f'

# или с использованием scp
archive_command = 'test ! -f /backup/wal_archive/%f && scp %p backup_server:/backup/wal_archive/%f'
```

#### 2. Создание каталога архива WAL

```bash
mkdir -p /backup/wal_archive
chown postgres:postgres /backup/wal_archive
chmod 700 /backup/wal_archive
```

#### 3. Кастомная архивация через shell-скрипт

**Создать файл конфигурации (например, backup.conf):**

```bash
#!/bin/bash
# archive_wal.sh

ARCHIVE_DIR="/backup/wal_archive"
WAL_FILE="$1"
WAL_NAME=$(basename "$WAL_FILE")

# проверка, уже архивирован или нет
if [ -f "$ARCHIVE_DIR/$WAL_NAME" ]; then
    exit 0
fi

# копирование файла
cp "$WAL_FILE" "$ARCHIVE_DIR/$WAL_NAME"

# проверка успешности копирования
if [ $? -eq 0 ]; then
    exit 0
else
    echo "Archive failed: $WAL_FILE" >&2
    exit 1
fi
```

**Установить права на скрипт:**

```bash
chmod +x /usr/local/bin/archive_wal.sh
chown postgres:postgres /usr/local/bin/archive_wal.sh
```

**Добавить в **postgresql.conf**:**

```conf
archive_command = '/usr/local/bin/archive_wal.sh %p'
```

### Point-in-Time Recovery (PITR)

#### 1. Создание базового снимка

```bash
pg_basebackup -h localhost -U postgres -D /backup/base_backup -Ft -z -P

# с меткой LSN базового снимка
pg_basebackup -h localhost -U postgres -D /backup/base_backup -Ft -z -P -l "Base backup $(date +%Y%m%d_%H%M%S)"
```

#### 2. Восстановление на заданный момент времени

```bash
# остановить PostgreSQL
sudo systemctl stop postgresql

# очистить каталог данных
rm -rf /var/lib/postgresql/data/*

tar -xzf /backup/base_backup/base.tar.gz -C /var/lib/postgresql/data/
tar -xzf /backup/base_backup/pg_wal.tar.gz -C /var/lib/postgresql/data/

# создать recovery.conf (PostgreSQL < 12)
cat > /var/lib/postgresql/data/recovery.conf << EOF
restore_command = 'cp /backup/wal_archive/%f %p'
recovery_target_time = '2026-01-16 14:30:00'
recovery_target_action = 'promote'
EOF

# для PostgreSQL 12+ создать recovery.signal
touch /var/lib/postgresql/data/recovery.signal

# добавить в postgresql.conf (PostgreSQL 12+)
cat >> /var/lib/postgresql/data/postgresql.conf << EOF
restore_command = 'cp /backup/wal_archive/%f %p'
recovery_target_time = '2026-01-16 14:30:00'
recovery_target_action = 'promote'
EOF

# запустить PostgreSQL
sudo systemctl start postgresql
```

#### 3. Параметры восстановления

```conf
# восстановление на заданный момент времени
recovery_target_time = '2026-01-16 14:30:00'

# восстановление на заданную транзакцию
recovery_target_xid = '12345'

# восстановление на заданный LSN
recovery_target_lsn = '0/12345678'

# восстановление на заданную точку
recovery_target_name = 'my_recovery_point'

# действие после достижения цели
recovery_target_action = 'promote'  # или 'pause', 'shutdown'

# выбор ветки таймлайна
recovery_target_timeline = 'latest'
```

### Именованные точки восстановления

```sql
-- создание именованной точки восстановления
SELECT pg_create_restore_point('before_migration_20260116');

-- просмотр точек восстановления
SELECT * FROM pg_restore_points;
```


## Примеры скриптов резервного копирования

### Ежедневный логический дамп

```bash
#!/bin/bash
# daily_backup.sh

BACKUP_DIR="/backup/postgresql/daily"
DATE=$(date +%Y%m%d)
DB_NAME="mydb"

# создать каталог бэкапа
mkdir -p "$BACKUP_DIR/$DATE"

pg_dump -h localhost -U postgres -d "$DB_NAME" -Fc -f "$BACKUP_DIR/$DATE/$DB_NAME.dump"

gzip "$BACKUP_DIR/$DATE/$DB_NAME.dump"

# удалить бэкапы старше 30 дней
find "$BACKUP_DIR" -type d -mtime +30 -exec rm -rf {} \;

echo "Backup completed: $BACKUP_DIR/$DATE"
```

### Инкрементальный снимок с WAL архивацией

```bash
#!/bin/bash
# incremental_backup.sh

BACKUP_DIR="/backup/postgresql"
BASE_BACKUP_DIR="$BACKUP_DIR/base"
WAL_ARCHIVE_DIR="$BACKUP_DIR/wal_archive"
DATE=$(date +%Y%m%d_%H%M%S)

if [ $(date +%u) -eq 1 ]; then
    mkdir -p "$BASE_BACKUP_DIR/$DATE"
    pg_basebackup -h localhost -U postgres -D "$BASE_BACKUP_DIR/$DATE" -Ft -z -P -l "Weekly backup $DATE"

    find "$BASE_BACKUP_DIR" -type d -mtime +28 -exec rm -rf {} \;
fi

find "$WAL_ARCHIVE_DIR" -type f -mtime +14 -delete
```

### Комбинированная стратегия

```bash
#!/bin/bash
# combined_backup_strategy.sh

BACKUP_DIR="/backup/postgresql"
LOGICAL_BACKUP_DIR="$BACKUP_DIR/logical"
PHYSICAL_BACKUP_DIR="$BACKUP_DIR/physical"
WAL_ARCHIVE_DIR="$BACKUP_DIR/wal_archive"
DATE=$(date +%Y%m%d)

mkdir -p "$LOGICAL_BACKUP_DIR/$DATE"
pg_dump -h localhost -U postgres -d mydb -Fc -f "$LOGICAL_BACKUP_DIR/$DATE/mydb.dump"
gzip "$LOGICAL_BACKUP_DIR/$DATE/mydb.dump"

if [ $(date +%u) -eq 1 ]; then
    mkdir -p "$PHYSICAL_BACKUP_DIR/$DATE"
    pg_basebackup -h localhost -U postgres -D "$PHYSICAL_BACKUP_DIR/$DATE" -Ft -z -P
fi


find "$LOGICAL_BACKUP_DIR" -type d -mtime +30 -exec rm -rf {} \;
find "$PHYSICAL_BACKUP_DIR" -type d -mtime +90 -exec rm -rf {} \;
find "$WAL_ARCHIVE_DIR" -type f -mtime +14 -delete
```


## Автоматизация резервного копирования

### Планировщик cron

```bash
crontab -e

0 2 * * * /usr/local/bin/daily_backup.sh >> /var/log/postgresql_backup.log 2>&1

0 1 * * 0 /usr/local/bin/weekly_backup.sh >> /var/log/postgresql_backup.log 2>&1
```

### Планировщик systemd timer

**Восстановление из архива:**

```ini
[Unit]
Description=PostgreSQL Daily Backup
After=postgresql.service

[Service]
Type=oneshot
User=postgres
ExecStart=/usr/local/bin/daily_backup.sh
```

**Восстановление из архива:**

```ini
[Unit]
Description=PostgreSQL Daily Backup Timer
Requires=postgresql-backup.service

[Timer]
OnCalendar=daily
OnCalendar=02:00
Persistent=true

[Install]
WantedBy=timers.target
```

**Запуск **timer**:**

```bash
sudo systemctl enable postgresql-backup.timer
sudo systemctl start postgresql-backup.timer
```


## Восстановление из бэкапов

### Восстановление полной базы данных

```bash
sudo systemctl stop myapp

# остановить PostgreSQL
sudo systemctl stop postgresql

dropdb -h localhost -U postgres mydb

createdb -h localhost -U postgres mydb

pg_restore -h localhost -U postgres -d mydb -v mydb.dump

gunzip -c mydb.dump.gz | psql -h localhost -U postgres -d mydb

sudo systemctl start postgresql

sudo systemctl start myapp
```

### Восстановление отдельных таблиц

```bash
pg_restore -h localhost -U postgres -d mydb -t users -v mydb.dump

pg_restore -h localhost -U postgres -d mydb -t users -t orders -v mydb.dump
```

### Восстановление на другой сервер

```bash
pg_dump -h source_host -U postgres -d mydb -Fc -f mydb.dump

scp mydb.dump target_host:/backup/

pg_restore -h localhost -U postgres -d mydb -v /backup/mydb.dump
```


## Проверка восстановления

### Тест восстановления бэкапа

```bash
#!/bin/bash
# test_restore.sh

BACKUP_FILE="$1"
TEST_DB="test_restore_$(date +%Y%m%d_%H%M%S)"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file>"
    exit 1
fi

createdb -h localhost -U postgres "$TEST_DB"

if pg_restore -h localhost -U postgres -d "$TEST_DB" -v "$BACKUP_FILE"; then
    echo "Restore test successful"

    psql -h localhost -U postgres -d "$TEST_DB" -c "VACUUM ANALYZE;"

    dropdb -h localhost -U postgres "$TEST_DB"

    exit 0
else
    echo "Restore test failed"
    dropdb -h localhost -U postgres "$TEST_DB"
    exit 1
fi
```

### Автоматическая проверка восстановления

```bash
#!/bin/bash
# auto_test_restore.sh

BACKUP_DIR="/backup/postgresql/daily"
LATEST_BACKUP=$(ls -t "$BACKUP_DIR"/*/mydb.dump.gz | head -1)

if [ -z "$LATEST_BACKUP" ]; then
    echo "No backup found"
    exit 1
fi

TEMP_DIR=$(mktemp -d)
gunzip -c "$LATEST_BACKUP" > "$TEMP_DIR/mydb.dump"

/usr/local/bin/test_restore.sh "$TEMP_DIR/mydb.dump"

rm -rf "$TEMP_DIR"
```


## Мониторинг резервного копирования

### Проверка статуса бэкапов

```sql
SELECT
    pg_stat_file('/backup/postgresql/daily/' || to_char(now(), 'YYYYMMDD') || '/mydb.dump.gz')
    AS last_backup;

SELECT pg_size_pretty(
    (pg_stat_file('/backup/postgresql/daily/' || to_char(now(), 'YYYYMMDD') || '/mydb.dump.gz')).size
) AS backup_size;
```

### Система алертов

```bash
#!/bin/bash
# monitor_backups.sh

BACKUP_DIR="/backup/postgresql/daily"
ALERT_EMAIL="admin@example.com"
MAX_AGE_HOURS=25

LATEST_BACKUP=$(find "$BACKUP_DIR" -name "mydb.dump.gz" -type f -mtime -1 | head -1)

if [ -z "$LATEST_BACKUP" ]; then
    echo "ALERT: No recent backup found" | mail -s "Backup Alert" "$ALERT_EMAIL"
    exit 1
fi

BACKUP_AGE=$(find "$BACKUP_DIR" -name "mydb.dump.gz" -type f -mtime -1 -printf '%T@\n' | head -1)
CURRENT_TIME=$(date +%s)
AGE_HOURS=$(( (CURRENT_TIME - ${BACKUP_AGE%.*}) / 3600 ))

if [ "$AGE_HOURS" -gt "$MAX_AGE_HOURS" ]; then
    echo "ALERT: Backup is $AGE_HOURS hours old" | mail -s "Backup Alert" "$ALERT_EMAIL"
    exit 1
fi

echo "OK: Backup is $AGE_HOURS hours old"
exit 0
```


## Лучшие практики

### Стратегия и регламент

1. **Регулярные бэкапы**: выполняйте бэкапы по расписанию и проверяйте их успешность.
2. **Тестовые восстановления**: регулярно проверяйте восстановление на отдельном стенде.
3. **Мониторинг**: настройте оповещения при сбоях архивации и backup-задач.
4. **Документация**: поддерживайте актуальные runbook-инструкции восстановления.

### Безопасность

1. **Шифрование**: шифруйте бэкапы при хранении и передаче.
2. **Права доступа**: ограничивайте доступ к бэкапам по ролям.
3. **Аудит**: ведите журнал действий по backup/restore операциям.

### Отказоустойчивость

1. **Репликация**: используйте реплики для быстрого переключения при сбоях.
2. **PITR**: настраивайте восстановление на заданный момент времени.
3. **Архивация WAL**: включайте непрерывную архивацию для минимизации потерь данных.


## Решение проблем

### Проблема: pg_dump завершается с ошибкой

**Симптомы:**
- Недостаточно прав пользователя.
- Проблемы с подключением к серверу.
- Недостаточно места на диске.

**Решение:**
```bash
# Проверить роли
psql -h localhost -U postgres -c "\du"

# Проверить подключение
psql -h localhost -U postgres -d mydb -c "SELECT 1;"

# Проверить свободное место
df -h
```

### Проблема: медленное восстановление больших баз

**Симптомы:**
- Большой размер бэкапа.
- Медленная дисковая подсистема.
- Недостаточно памяти.

**Решение:**
```bash
# Восстановление с параллелизмом
pg_restore -j 4 -d mydb mydb.dump

# Оптимизируйте параметры PostgreSQL перед восстановлением
# Проверьте shared_buffers и work_mem
```

### Проблема: WAL архивирование не работает

**Симптомы:**
- Некорректный `archive_command`.
- Недоступен каталог архива.
- Ошибки прав доступа.

**Решение:**
```bash
# Проверить логи
tail -f /var/log/postgresql/postgresql-*.log

# Проверить настройки архивации
psql -c "SHOW archive_mode;"
psql -c "SHOW archive_command;"

# Перезапустить PostgreSQL после исправления конфигурации
```

## Advanced Backup Strategies

### Streaming Replication для бэкапов

```bash
pg_basebackup -h primary_host -U replicator -D /backup/replica -Ft -z -P -S backup_replica

hot_standby = on
max_standby_streaming_delay = 30s
```

### Incremental Backups с pgBackRest

```bash
# Ubuntu/Debian
sudo apt-get install pgbackrest

[global]
repo1-path=/backup/pgbackrest
repo1-retention-full=2
repo1-retention-diff=4

[mydb]
pg1-path=/var/lib/postgresql/data

pgbackrest --stanza=mydb --type=full backup

pgbackrest --stanza=mydb --type=incr backup

pgbackrest --stanza=mydb restore
```

### Cloud Backups

#### AWS S3 Backup

```bash
#!/bin/bash
# s3_backup.sh

BACKUP_FILE="/tmp/mydb_$(date +%Y%m%d_%H%M%S).dump"
S3_BUCKET="my-postgres-backups"
S3_PATH="backups/$(date +%Y/%m/%d)/"

pg_dump -h localhost -U postgres -d mydb -Fc -f "$BACKUP_FILE"

aws s3 cp "$BACKUP_FILE" "s3://$S3_BUCKET/$S3_PATH"

rm "$BACKUP_FILE"

aws s3 ls "s3://$S3_BUCKET/backups/" --recursive | \
    awk '$1 < "'$(date -d '30 days ago' +%Y-%m-%d)'" {print $4}' | \
    xargs -I {} aws s3 rm "s3://$S3_BUCKET/{}"
```

#### Google Cloud Storage Backup

```bash
#!/bin/bash
# gcs_backup.sh

BACKUP_FILE="/tmp/mydb_$(date +%Y%m%d_%H%M%S).dump"
GCS_BUCKET="my-postgres-backups"
GCS_PATH="gs://$GCS_BUCKET/backups/$(date +%Y/%m/%d)/"

pg_dump -h localhost -U postgres -d mydb -Fc -f "$BACKUP_FILE"

gsutil cp "$BACKUP_FILE" "$GCS_PATH"

rm "$BACKUP_FILE"
```

## Advanced Restore Techniques

### Selective Table Restore

```sql
CREATE OR REPLACE FUNCTION restore_table_from_backup(
    backup_file TEXT,
    table_name TEXT,
    schema_name TEXT DEFAULT 'public'
)
RETURNS VOID AS $$
BEGIN
    PERFORM pg_restore(
        '-h', 'localhost',
        '-U', 'postgres',
        '-d', current_database(),
        '-t', format('%s.%s', schema_name, table_name),
        '-v',
        backup_file
    );
END;
$$ LANGUAGE plpgsql;
```

### Cross-Version Restore

```bash
/usr/lib/postgresql/12/bin/pg_dump -h old_host -U postgres -d mydb -Fc -f mydb_old.dump

/usr/lib/postgresql/14/bin/pg_restore -h new_host -U postgres -d mydb -v mydb_old.dump

psql -h new_host -U postgres -d mydb -c "ALTER EXTENSION ALL UPDATE;"
```

### Point-in-Time Recovery Advanced

```bash

recovery_target_time = '2026-01-16 14:30:00'
recovery_target_action = 'promote'

recovery_target_xid = '12345'
recovery_target_action = 'pause'

recovery_target_lsn = '0/12345678'
recovery_target_action = 'shutdown'

recovery_target_name = 'before_migration'
recovery_target_action = 'promote'
```

## Backup Verification

### Automated Backup Verification

```sql
CREATE OR REPLACE FUNCTION verify_backup(backup_file TEXT)
RETURNS TABLE(
    check_name TEXT,
    status TEXT,
    message TEXT
) AS $$
DECLARE
    test_db TEXT;
BEGIN
    test_db := 'backup_test_' || extract(epoch from now())::TEXT;

    EXECUTE format('CREATE DATABASE %I', test_db);

    BEGIN
        PERFORM pg_restore(
            '-h', 'localhost',
            '-U', 'postgres',
            '-d', test_db,
            '-v',
            backup_file
        );

        PERFORM dblink_exec(
            format('dbname=%s', test_db),
            'VACUUM ANALYZE;'
        );

        RETURN QUERY
        SELECT
            'backup_restore'::TEXT,
            'OK'::TEXT,
            format('Backup restored successfully to %s', test_db);

    EXCEPTION
        WHEN OTHERS THEN
            RETURN QUERY
            SELECT
                'backup_restore'::TEXT,
                'FAILED'::TEXT,
                SQLERRM;
    END;

    EXECUTE format('DROP DATABASE IF EXISTS %I', test_db);
END;
$$ LANGUAGE plpgsql;
```

### Backup Integrity Checks

```bash
#!/bin/bash
# verify_backup_integrity.sh

BACKUP_FILE="$1"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file>"
    exit 1
fi

if file "$BACKUP_FILE" | grep -q "PostgreSQL custom database dump"; then
    echo "OK: Valid PostgreSQL custom format backup"
else
    echo "ERROR: Invalid backup format"
    exit 1
fi

BACKUP_SIZE=$(stat -f%z "$BACKUP_FILE" 2>/dev/null || stat -c%s "$BACKUP_FILE" 2>/dev/null)
if [ "$BACKUP_SIZE" -lt 1000 ]; then
    echo "ERROR: Backup file too small"
    exit 1
fi

if [[ "$BACKUP_FILE" == *.gz ]]; then
    if ! gzip -t "$BACKUP_FILE" 2>/dev/null; then
        echo "ERROR: Corrupted gzip file"
        exit 1
    fi
fi

echo "OK: Backup integrity check passed"
exit 0
```

## Backup Monitoring and Alerting

### Backup Status Monitoring

```sql
CREATE TABLE backup_history (
    id SERIAL PRIMARY KEY,
    backup_type TEXT NOT NULL,
    backup_file TEXT NOT NULL,
    backup_size BIGINT,
    backup_started_at TIMESTAMPTZ DEFAULT NOW(),
    backup_completed_at TIMESTAMPTZ,
    backup_status TEXT,
    error_message TEXT
);

CREATE OR REPLACE FUNCTION record_backup_status(
    p_type TEXT,
    p_file TEXT,
    p_size BIGINT,
    p_status TEXT,
    p_error TEXT DEFAULT NULL
)
RETURNS INTEGER AS $$
DECLARE
    backup_id INTEGER;
BEGIN
    INSERT INTO backup_history (
        backup_type,
        backup_file,
        backup_size,
        backup_completed_at,
        backup_status,
        error_message
    ) VALUES (
        p_type,
        p_file,
        p_size,
        NOW(),
        p_status,
        p_error
    ) RETURNING id INTO backup_id;

    RETURN backup_id;
END;
$$ LANGUAGE plpgsql;
```

### Automated Backup Alerts

```bash
#!/bin/bash
# backup_alert.sh

BACKUP_DIR="/backup/postgresql/daily"
ALERT_EMAIL="admin@example.com"
MAX_AGE_HOURS=25

LATEST_BACKUP=$(find "$BACKUP_DIR" -name "*.dump*" -type f -mtime -1 | head -1)

if [ -z "$LATEST_BACKUP" ]; then
    echo "ALERT: No recent backup found in $BACKUP_DIR" | \
        mail -s "PostgreSQL Backup Alert" "$ALERT_EMAIL"
    exit 1
fi

BACKUP_SIZE=$(stat -f%z "$LATEST_BACKUP" 2>/dev/null || stat -c%s "$LATEST_BACKUP" 2>/dev/null)
MIN_SIZE=1000000  # 1MB

if [ "$BACKUP_SIZE" -lt "$MIN_SIZE" ]; then
    echo "ALERT: Backup file is too small: $BACKUP_SIZE bytes" | \
        mail -s "PostgreSQL Backup Alert" "$ALERT_EMAIL"
    exit 1
fi

BACKUP_AGE=$(find "$BACKUP_DIR" -name "*.dump*" -type f -mtime -1 -printf '%T@\n' | head -1)
CURRENT_TIME=$(date +%s)
AGE_HOURS=$(( (CURRENT_TIME - ${BACKUP_AGE%.*}) / 3600 ))

if [ "$AGE_HOURS" -gt "$MAX_AGE_HOURS" ]; then
    echo "ALERT: Backup is $AGE_HOURS hours old (max: $MAX_AGE_HOURS)" | \
        mail -s "PostgreSQL Backup Alert" "$ALERT_EMAIL"
    exit 1
fi

echo "OK: Backup verified successfully"
exit 0
```

## Disaster Recovery Planning

### `DR` Plan Template

```bash
#!/bin/bash
# disaster_recovery_plan.sh

echo "Stopping application..."
sudo systemctl stop myapp

echo "Stopping PostgreSQL..."
sudo systemctl stop postgresql

BACKUP_FILE="/backup/postgresql/daily/20260116/mydb.dump"

echo "Removing corrupted data..."
rm -rf /var/lib/postgresql/data/*

echo "Restoring from backup..."
pg_restore -h localhost -U postgres -d mydb -v "$BACKUP_FILE"

echo "Verifying database integrity..."
psql -h localhost -U postgres -d mydb -c "VACUUM ANALYZE;"

echo "Starting PostgreSQL..."
sudo systemctl start postgresql

echo "Checking PostgreSQL status..."
sudo systemctl status postgresql

echo "Starting application..."
sudo systemctl start myapp

echo "Disaster recovery completed"
```

### Recovery Time Objectives (RTO) and Recovery Point Objectives (RPO)

```sql
CREATE TABLE recovery_metrics (
    id SERIAL PRIMARY KEY,
    recovery_type TEXT NOT NULL,
    recovery_started_at TIMESTAMPTZ,
    recovery_completed_at TIMESTAMPTZ,
    data_loss_seconds INTEGER,
    recovery_time_seconds INTEGER,
    backup_file TEXT,
    recovery_status TEXT
);

CREATE OR REPLACE FUNCTION calculate_recovery_metrics(
    p_recovery_type TEXT,
    p_backup_file TEXT
)
RETURNS TABLE(
    rto_seconds INTEGER,
    rpo_seconds INTEGER,
    recovery_status TEXT
) AS $$
DECLARE
    backup_time TIMESTAMP;
    recovery_start TIMESTAMP;
    recovery_end TIMESTAMP;
    data_loss INTEGER;
    recovery_time INTEGER;
BEGIN
    recovery_start := NOW();

    PERFORM pg_restore(
        '-h', 'localhost',
        '-U', 'postgres',
        '-d', current_database(),
        '-v',
        p_backup_file
    );

    recovery_end := NOW();
    recovery_time := EXTRACT(EPOCH FROM (recovery_end - recovery_start))::INTEGER;

    backup_time := NOW() - INTERVAL '24 hours';
    data_loss := EXTRACT(EPOCH FROM (recovery_start - backup_time))::INTEGER;

    RETURN QUERY
    SELECT
        recovery_time AS rto_seconds,
        data_loss AS rpo_seconds,
        'COMPLETED'::TEXT AS recovery_status;
END;
$$ LANGUAGE plpgsql;
```

## Backup Compression and Optimization

### Advanced Compression Strategies

```bash
#!/bin/bash
# optimized_backup.sh

DB_NAME="mydb"
BACKUP_DIR="/backup/postgresql"
DATE=$(date +%Y%m%d_%H%M%S)

pg_dump -h localhost -U postgres -d "$DB_NAME" \
    -Fd \
    -j 4 \
    -f "$BACKUP_DIR/$DATE" \
    -Z 9

tar -cf - "$BACKUP_DIR/$DATE" | pigz -p 4 > "$BACKUP_DIR/$DATE.tar.gz"

rm -rf "$BACKUP_DIR/$DATE"

echo "Optimized backup completed: $BACKUP_DIR/$DATE.tar.gz"
```

### Incremental Backup Strategy

```bash
#!/bin/bash
# incremental_backup_strategy.sh

BACKUP_DIR="/backup/postgresql"
BASE_BACKUP_DIR="$BACKUP_DIR/base"
INCREMENTAL_BACKUP_DIR="$BACKUP_DIR/incremental"
WAL_ARCHIVE_DIR="$BACKUP_DIR/wal_archive"
DATE=$(date +%Y%m%d_%H%M%S)

if [ $(date +%u) -eq 1 ]; then
    echo "Creating weekly base backup..."
    mkdir -p "$BASE_BACKUP_DIR/$DATE"
    pg_basebackup -h localhost -U postgres \
        -D "$BASE_BACKUP_DIR/$DATE" \
        -Ft -z -P \
        -l "Weekly base backup $DATE"

    find "$BASE_BACKUP_DIR" -type d -mtime +28 -exec rm -rf {} \;
fi

echo "Creating incremental backup..."
pg_dump -h localhost -U postgres -d mydb \
    -Fc \
    -f "$INCREMENTAL_BACKUP_DIR/mydb_$DATE.dump" \
    --exclude-table=logs \
    --exclude-table=audit_log

find "$INCREMENTAL_BACKUP_DIR" -type f -mtime +7 -delete

find "$WAL_ARCHIVE_DIR" -type f -mtime +14 -delete
```

## Best Practices Summary

### Backup Strategy

1. **Регулярные проверки**: комбинируйте логические и физические бэкапы.
2. **Тестирование**: регулярно проверяйте восстановление на тестовом контуре.
3. **Ротация**: храните бэкапы в разных зонах (локально и в облаке).
4. **Мониторинг**: контролируйте успешность задач и SLA восстановления.

### Security

1. **Шифрование**: шифруйте бэкапы при хранении.
2. **Права доступа**: ограничивайте доступ по ролям.
3. **Аудит**: ведите журнал действий по бэкапам и восстановлениям.

### Monitoring

1. **Автоматические проверки**: контролируйте актуальность последнего бэкапа.
2. **Алерты**: уведомляйте ответственных о проблемах.
3. **Метрики**: отслеживайте размер, время создания и длительность восстановления.


## Полезные ссылки

- [PostgreSQL Backup Documentation](https://www.postgresql.org/docs/)
- [pg_dump Documentation](https://www.postgresql.org/docs/)
- [pg_restore Documentation](https://www.postgresql.org/docs/)
- [pg_basebackup Documentation](https://www.postgresql.org/docs/)
- [Point-in-Time Recovery](https://www.postgresql.org/docs/)

