---
title: "PostgreSQL: Backup и Restore"
description: "Полное руководство по резервному копированию и восстановлению PostgreSQL: pg_dump, pg_restore, pg_basebackup, Continuous Archiving, Point-in-Time Recovery (PITR), стратегии бэкапов"
tags: ["postgresql", "backup", "restore", "pg_dump", "pg_basebackup", "pitr", "continuous-archiving"]
difficulty: "advanced"
prerequisites: ["databases/postgres-basics.md", "databases/postgres-admin.md"]
next: ["databases/postgres-replication.md", "databases/postgres-high-availability.md"]
updated: "2026-01-16"
related: ["databases/postgres-monitoring.md", "databases/postgres-troubleshooting.md"]
---

# PostgreSQL: Backup и Restore

## Введение в резервное копирование PostgreSQL

Резервное копирование - критически важная часть управления базой данных. PostgreSQL предоставляет несколько методов резервного копирования, каждый из которых подходит для различных сценариев использования.

### Типы резервного копирования

1. **Логический дамп (Logical Dump)**: pg_dump, pg_dumpall
2. **Физический бэкап (Physical Backup)**: pg_basebackup, файловая система
3. **Continuous Archiving**: Непрерывное архивирование WAL файлов
4. **Point-in-Time Recovery (PITR)**: Восстановление на определенный момент времени

### Выбор метода резервного копирования

- **Логический дамп**: Для небольших баз, миграций, выборочного восстановления
- **Физический бэкап**: Для больших баз, быстрого восстановления
- **Continuous Archiving**: Для критически важных баз, требующих минимальной потери данных

---

## Логический дамп: pg_dump

pg_dump создает текстовый или бинарный файл с SQL командами для восстановления базы данных.

### Базовое использование pg_dump

#### 1. Текстовый формат (SQL)

```bash
# Дамп всей базы данных
pg_dump -h localhost -U postgres -d mydb > mydb.sql

# Дамп с сжатием
pg_dump -h localhost -U postgres -d mydb | gzip > mydb.sql.gz

# Дамп с указанием файла
pg_dump -h localhost -U postgres -d mydb -f mydb.sql
```

#### 2. Custom формат (сжатый бинарный)

```bash
# Custom формат (рекомендуется)
pg_dump -h localhost -U postgres -d mydb -Fc -f mydb.dump

# С указанием уровня сжатия
pg_dump -h localhost -U postgres -d mydb -Fc -Z 9 -f mydb.dump
```

#### 3. Directory формат (параллельный дамп)

```bash
# Directory формат для параллельного восстановления
pg_dump -h localhost -U postgres -d mydb -Fd -f mydb_backup

# С указанием количества потоков
pg_dump -h localhost -U postgres -d mydb -Fd -j 4 -f mydb_backup
```

#### 4. Tar формат

```bash
# Tar формат
pg_dump -h localhost -U postgres -d mydb -Ft -f mydb.tar
```

### Параметры pg_dump

#### Основные параметры

```bash
# Дамп только схемы (без данных)
pg_dump -h localhost -U postgres -d mydb --schema-only -f schema.sql

# Дамп только данных (без схемы)
pg_dump -h localhost -U postgres -d mydb --data-only -f data.sql

# Дамп конкретной таблицы
pg_dump -h localhost -U postgres -d mydb -t users -f users.sql

# Дамп конкретной схемы
pg_dump -h localhost -U postgres -d mydb -n public -f public_schema.sql

# Исключить таблицы
pg_dump -h localhost -U postgres -d mydb -T logs -f mydb_no_logs.sql

# Дамп с владельцами и привилегиями
pg_dump -h localhost -U postgres -d mydb --no-owner --no-privileges -f mydb.sql
```

#### Дополнительные параметры

```bash
# Вербозный вывод
pg_dump -h localhost -U postgres -d mydb -v -f mydb.sql

# Игнорировать ошибки
pg_dump -h localhost -U postgres -d mydb --ignore-version -f mydb.sql

# Блокировать таблицы
pg_dump -h localhost -U postgres -d mydb --lock-wait-timeout=10000 -f mydb.sql

# Дамп с использованием snapshot
pg_dump -h localhost -U postgres -d mydb --snapshot=snapshot_name -f mydb.sql
```

### Восстановление из pg_dump

#### Восстановление из SQL файла

```bash
# Восстановление из текстового файла
psql -h localhost -U postgres -d mydb < mydb.sql

# Или через psql
psql -h localhost -U postgres -d mydb -f mydb.sql
```

#### Восстановление из Custom формата

```bash
# Восстановление из custom формата
pg_restore -h localhost -U postgres -d mydb -v mydb.dump

# Восстановление только схемы
pg_restore -h localhost -U postgres -d mydb --schema-only -v mydb.dump

# Восстановление только данных
pg_restore -h localhost -U postgres -d mydb --data-only -v mydb.dump

# Восстановление конкретной таблицы
pg_restore -h localhost -U postgres -d mydb -t users -v mydb.dump

# Восстановление с созданием базы данных
pg_restore -h localhost -U postgres -d mydb --create -v mydb.dump
```

#### Восстановление из Directory формата

```bash
# Восстановление из directory формата (параллельно)
pg_restore -h localhost -U postgres -d mydb -j 4 -v mydb_backup
```

### pg_dumpall

pg_dumpall создает дамп всех баз данных кластера PostgreSQL.

```bash
# Дамп всех баз данных
pg_dumpall -h localhost -U postgres > all_databases.sql

# Дамп только глобальных объектов (роли, табличные пространства)
pg_dumpall -h localhost -U postgres --globals-only > globals.sql

# Дамп только схем
pg_dumpall -h localhost -U postgres --schema-only > schemas.sql

# Восстановление
psql -h localhost -U postgres < all_databases.sql
```

---

## Физический бэкап: pg_basebackup

pg_basebackup создает физическую копию файлов данных PostgreSQL. Это быстрый метод для больших баз данных.

### Базовое использование pg_basebackup

#### 1. Простой бэкап

```bash
# Базовый бэкап
pg_basebackup -h localhost -U postgres -D /backup/postgresql -Ft -z -P

# С указанием формата
pg_basebackup -h localhost -U postgres -D /backup/postgresql -Fp -P
```

#### 2. Параметры pg_basebackup

```bash
# Tar формат с сжатием
pg_basebackup -h localhost -U postgres -D /backup/postgresql -Ft -z -P

# Plain формат (копирование файлов)
pg_basebackup -h localhost -U postgres -D /backup/postgresql -Fp -P

# С указанием слота репликации
pg_basebackup -h localhost -U postgres -D /backup/postgresql -S backup_slot -Fp -P

# С указанием checkpoint
pg_basebackup -h localhost -U postgres -D /backup/postgresql -c fast -Fp -P

# С указанием label
pg_basebackup -h localhost -U postgres -D /backup/postgresql -l "Backup $(date +%Y%m%d)" -Fp -P
```

#### 3. Восстановление из pg_basebackup

```bash
# Остановить PostgreSQL
sudo systemctl stop postgresql

# Удалить старые данные
rm -rf /var/lib/postgresql/data/*

# Восстановить из бэкапа
tar -xzf /backup/postgresql/base.tar.gz -C /var/lib/postgresql/data/
tar -xzf /backup/postgresql/pg_wal.tar.gz -C /var/lib/postgresql/data/

# Или для plain формата
cp -r /backup/postgresql/* /var/lib/postgresql/data/

# Настроить recovery (если нужно)
echo "restore_command = 'cp /backup/wal_archive/%f %p'" >> /var/lib/postgresql/data/postgresql.conf

# Запустить PostgreSQL
sudo systemctl start postgresql
```

---

## Continuous Archiving и Point-in-Time Recovery (PITR)

Continuous Archiving позволяет архивировать WAL файлы для восстановления на любой момент времени.

### Настройка Continuous Archiving

#### 1. Настройка postgresql.conf

```conf
# Включить WAL архивирование
wal_level = replica

# Команда архивирования
archive_mode = on
archive_command = 'test ! -f /backup/wal_archive/%f && cp %p /backup/wal_archive/%f'

# Или с использованием rsync
archive_command = 'test ! -f /backup/wal_archive/%f && rsync -a %p /backup/wal_archive/%f'

# Или с использованием scp
archive_command = 'test ! -f /backup/wal_archive/%f && scp %p backup_server:/backup/wal_archive/%f'
```

#### 2. Создание директории для архива

```bash
mkdir -p /backup/wal_archive
chown postgres:postgres /backup/wal_archive
chmod 700 /backup/wal_archive
```

#### 3. Настройка архивирования с помощью скрипта

Создайте скрипт `/usr/local/bin/archive_wal.sh`:

```bash
#!/bin/bash
# archive_wal.sh

ARCHIVE_DIR="/backup/wal_archive"
WAL_FILE="$1"
WAL_NAME=$(basename "$WAL_FILE")

# Проверить, существует ли файл
if [ -f "$ARCHIVE_DIR/$WAL_NAME" ]; then
    exit 0
fi

# Скопировать файл
cp "$WAL_FILE" "$ARCHIVE_DIR/$WAL_NAME"

# Проверить успешность копирования
if [ $? -eq 0 ]; then
    exit 0
else
    echo "Archive failed: $WAL_FILE" >&2
    exit 1
fi
```

Сделайте скрипт исполняемым:

```bash
chmod +x /usr/local/bin/archive_wal.sh
chown postgres:postgres /usr/local/bin/archive_wal.sh
```

Обновите postgresql.conf:

```conf
archive_command = '/usr/local/bin/archive_wal.sh %p'
```

### Point-in-Time Recovery (PITR)

#### 1. Создание базового бэкапа

```bash
# Создать базовый бэкап
pg_basebackup -h localhost -U postgres -D /backup/base_backup -Ft -z -P

# Записать LSN базового бэкапа
pg_basebackup -h localhost -U postgres -D /backup/base_backup -Ft -z -P -l "Base backup $(date +%Y%m%d_%H%M%S)"
```

#### 2. Восстановление на определенный момент времени

```bash
# Остановить PostgreSQL
sudo systemctl stop postgresql

# Удалить старые данные
rm -rf /var/lib/postgresql/data/*

# Восстановить базовый бэкап
tar -xzf /backup/base_backup/base.tar.gz -C /var/lib/postgresql/data/
tar -xzf /backup/base_backup/pg_wal.tar.gz -C /var/lib/postgresql/data/

# Создать recovery.conf (PostgreSQL < 12)
cat > /var/lib/postgresql/data/recovery.conf << EOF
restore_command = 'cp /backup/wal_archive/%f %p'
recovery_target_time = '2026-01-16 14:30:00'
recovery_target_action = 'promote'
EOF

# Для PostgreSQL 12+ создать recovery.signal
touch /var/lib/postgresql/data/recovery.signal

# Настроить postgresql.conf (PostgreSQL 12+)
cat >> /var/lib/postgresql/data/postgresql.conf << EOF
restore_command = 'cp /backup/wal_archive/%f %p'
recovery_target_time = '2026-01-16 14:30:00'
recovery_target_action = 'promote'
EOF

# Запустить PostgreSQL
sudo systemctl start postgresql
```

#### 3. Параметры восстановления

```conf
# Восстановление до определенного времени
recovery_target_time = '2026-01-16 14:30:00'

# Восстановление до определенного транзакции
recovery_target_xid = '12345'

# Восстановление до определенного LSN
recovery_target_lsn = '0/12345678'

# Восстановление до определенного имени
recovery_target_name = 'my_recovery_point'

# Действие после достижения цели
recovery_target_action = 'promote'  # или 'pause', 'shutdown'

# Включить временную шкалу
recovery_target_timeline = 'latest'
```

### Создание точек восстановления

```sql
-- Создать именованную точку восстановления
SELECT pg_create_restore_point('before_migration_20260116');

-- Проверить точки восстановления
SELECT * FROM pg_restore_points;
```

---

## Стратегии резервного копирования

### Ежедневные полные бэкапы

```bash
#!/bin/bash
# daily_backup.sh

BACKUP_DIR="/backup/postgresql/daily"
DATE=$(date +%Y%m%d)
DB_NAME="mydb"

# Создать директорию
mkdir -p "$BACKUP_DIR/$DATE"

# Полный бэкап
pg_dump -h localhost -U postgres -d "$DB_NAME" -Fc -f "$BACKUP_DIR/$DATE/$DB_NAME.dump"

# Сжать бэкап
gzip "$BACKUP_DIR/$DATE/$DB_NAME.dump"

# Удалить бэкапы старше 30 дней
find "$BACKUP_DIR" -type d -mtime +30 -exec rm -rf {} \;

echo "Backup completed: $BACKUP_DIR/$DATE"
```

### Инкрементальные бэкапы с WAL архивированием

```bash
#!/bin/bash
# incremental_backup.sh

BACKUP_DIR="/backup/postgresql"
BASE_BACKUP_DIR="$BACKUP_DIR/base"
WAL_ARCHIVE_DIR="$BACKUP_DIR/wal_archive"
DATE=$(date +%Y%m%d_%H%M%S)

# Еженедельный базовый бэкап
if [ $(date +%u) -eq 1 ]; then
    mkdir -p "$BASE_BACKUP_DIR/$DATE"
    pg_basebackup -h localhost -U postgres -D "$BASE_BACKUP_DIR/$DATE" -Ft -z -P -l "Weekly backup $DATE"
    
    # Удалить старые базовые бэкапы (старше 4 недель)
    find "$BASE_BACKUP_DIR" -type d -mtime +28 -exec rm -rf {} \;
fi

# Очистка старых WAL файлов (старше 2 недель)
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

# Ежедневный логический бэкап (для быстрого восстановления отдельных таблиц)
mkdir -p "$LOGICAL_BACKUP_DIR/$DATE"
pg_dump -h localhost -U postgres -d mydb -Fc -f "$LOGICAL_BACKUP_DIR/$DATE/mydb.dump"
gzip "$LOGICAL_BACKUP_DIR/$DATE/mydb.dump"

# Еженедельный физический бэкап (для полного восстановления)
if [ $(date +%u) -eq 1 ]; then
    mkdir -p "$PHYSICAL_BACKUP_DIR/$DATE"
    pg_basebackup -h localhost -U postgres -D "$PHYSICAL_BACKUP_DIR/$DATE" -Ft -z -P
fi

# WAL архивирование (непрерывно)
# Настроено в postgresql.conf

# Очистка старых бэкапов
find "$LOGICAL_BACKUP_DIR" -type d -mtime +30 -exec rm -rf {} \;
find "$PHYSICAL_BACKUP_DIR" -type d -mtime +90 -exec rm -rf {} \;
find "$WAL_ARCHIVE_DIR" -type f -mtime +14 -delete
```

---

## Автоматизация резервного копирования

### Использование cron

```bash
# Добавить в crontab
crontab -e

# Ежедневный бэкап в 2:00 ночи
0 2 * * * /usr/local/bin/daily_backup.sh >> /var/log/postgresql_backup.log 2>&1

# Еженедельный бэкап в воскресенье в 1:00 ночи
0 1 * * 0 /usr/local/bin/weekly_backup.sh >> /var/log/postgresql_backup.log 2>&1
```

### Использование systemd timer

Создайте `/etc/systemd/system/postgresql-backup.service`:

```ini
[Unit]
Description=PostgreSQL Daily Backup
After=postgresql.service

[Service]
Type=oneshot
User=postgres
ExecStart=/usr/local/bin/daily_backup.sh
```

Создайте `/etc/systemd/system/postgresql-backup.timer`:

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

Активируйте timer:

```bash
sudo systemctl enable postgresql-backup.timer
sudo systemctl start postgresql-backup.timer
```

---

## Восстановление из бэкапов

### Восстановление полной базы данных

```bash
# Остановить приложение
sudo systemctl stop myapp

# Остановить PostgreSQL
sudo systemctl stop postgresql

# Удалить старую базу данных
dropdb -h localhost -U postgres mydb

# Создать новую базу данных
createdb -h localhost -U postgres mydb

# Восстановить из бэкапа
pg_restore -h localhost -U postgres -d mydb -v mydb.dump

# Или из сжатого файла
gunzip -c mydb.dump.gz | psql -h localhost -U postgres -d mydb

# Запустить PostgreSQL
sudo systemctl start postgresql

# Запустить приложение
sudo systemctl start myapp
```

### Восстановление отдельных таблиц

```bash
# Восстановить только таблицу users
pg_restore -h localhost -U postgres -d mydb -t users -v mydb.dump

# Восстановить несколько таблиц
pg_restore -h localhost -U postgres -d mydb -t users -t orders -v mydb.dump
```

### Восстановление на другой сервер

```bash
# На исходном сервере
pg_dump -h source_host -U postgres -d mydb -Fc -f mydb.dump

# Скопировать на целевой сервер
scp mydb.dump target_host:/backup/

# На целевом сервере
pg_restore -h localhost -U postgres -d mydb -v /backup/mydb.dump
```

---

## Тестирование восстановления

### Скрипт тестирования восстановления

```bash
#!/bin/bash
# test_restore.sh

BACKUP_FILE="$1"
TEST_DB="test_restore_$(date +%Y%m%d_%H%M%S)"

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file>"
    exit 1
fi

# Создать тестовую базу данных
createdb -h localhost -U postgres "$TEST_DB"

# Восстановить бэкап
if pg_restore -h localhost -U postgres -d "$TEST_DB" -v "$BACKUP_FILE"; then
    echo "Restore test successful"
    
    # Проверить целостность
    psql -h localhost -U postgres -d "$TEST_DB" -c "VACUUM ANALYZE;"
    
    # Удалить тестовую базу
    dropdb -h localhost -U postgres "$TEST_DB"
    
    exit 0
else
    echo "Restore test failed"
    dropdb -h localhost -U postgres "$TEST_DB"
    exit 1
fi
```

### Автоматическое тестирование

```bash
#!/bin/bash
# auto_test_restore.sh

BACKUP_DIR="/backup/postgresql/daily"
LATEST_BACKUP=$(ls -t "$BACKUP_DIR"/*/mydb.dump.gz | head -1)

if [ -z "$LATEST_BACKUP" ]; then
    echo "No backup found"
    exit 1
fi

# Распаковать бэкап
TEMP_DIR=$(mktemp -d)
gunzip -c "$LATEST_BACKUP" > "$TEMP_DIR/mydb.dump"

# Тестировать восстановление
/usr/local/bin/test_restore.sh "$TEMP_DIR/mydb.dump"

# Очистить временные файлы
rm -rf "$TEMP_DIR"
```

---

## Мониторинг резервного копирования

### Проверка статуса бэкапов

```sql
-- Проверить последний бэкап
SELECT 
    pg_stat_file('/backup/postgresql/daily/' || to_char(now(), 'YYYYMMDD') || '/mydb.dump.gz') 
    AS last_backup;

-- Проверить размер бэкапа
SELECT pg_size_pretty(
    (pg_stat_file('/backup/postgresql/daily/' || to_char(now(), 'YYYYMMDD') || '/mydb.dump.gz')).size
) AS backup_size;
```

### Скрипт мониторинга

```bash
#!/bin/bash
# monitor_backups.sh

BACKUP_DIR="/backup/postgresql/daily"
ALERT_EMAIL="admin@example.com"
MAX_AGE_HOURS=25

# Проверить последний бэкап
LATEST_BACKUP=$(find "$BACKUP_DIR" -name "mydb.dump.gz" -type f -mtime -1 | head -1)

if [ -z "$LATEST_BACKUP" ]; then
    echo "ALERT: No recent backup found" | mail -s "Backup Alert" "$ALERT_EMAIL"
    exit 1
fi

# Проверить возраст бэкапа
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

---

## Best Practices

### Планирование

1. **Регулярные бэкапы**: Ежедневные полные бэкапы для критических баз
2. **Множественные копии**: Хранить бэкапы в разных местах
3. **Тестирование**: Регулярно тестировать восстановление
4. **Документация**: Документировать процедуры восстановления

### Безопасность

1. **Шифрование**: Шифровать бэкапы при хранении
2. **Права доступа**: Ограничить доступ к бэкапам
3. **Пароли**: Не хранить пароли в скриптах

### Производительность

1. **Параллелизм**: Использовать параллельные бэкапы для больших баз
2. **Сжатие**: Использовать сжатие для экономии места
3. **Инкрементальные бэкапы**: Использовать WAL архивирование для минимизации потери данных

---

## Troubleshooting

### Проблема: pg_dump завершается с ошибкой

**Причины:**
- Недостаточно прав доступа
- Проблемы с подключением
- Недостаточно места на диске

**Решение:**
```bash
# Проверить права
psql -h localhost -U postgres -c "\du"

# Проверить подключение
psql -h localhost -U postgres -d mydb -c "SELECT 1;"

# Проверить место на диске
df -h
```

### Проблема: Восстановление занимает много времени

**Причины:**
- Большой размер бэкапа
- Медленный диск
- Недостаточно ресурсов

**Решение:**
```bash
# Использовать параллельное восстановление
pg_restore -j 4 -d mydb mydb.dump

# Оптимизировать настройки PostgreSQL перед восстановлением
# Увеличить shared_buffers, work_mem
```

### Проблема: WAL архивирование не работает

**Причины:**
- Неправильная команда архивирования
- Недостаточно места
- Проблемы с правами

**Решение:**
```bash
# Проверить логи
tail -f /var/log/postgresql/postgresql-*.log

# Проверить настройки
psql -c "SHOW archive_mode;"
psql -c "SHOW archive_command;"

# Протестировать команду архивирования вручную
```

## Advanced Backup Strategies

### Streaming Replication для бэкапов

```bash
# Создать реплику для бэкапов
pg_basebackup -h primary_host -U replicator -D /backup/replica -Ft -z -P -S backup_replica

# Настроить реплику для бэкапов
# В postgresql.conf реплики:
hot_standby = on
max_standby_streaming_delay = 30s
```

### Incremental Backups с pgBackRest

```bash
# Установка pgBackRest
# Ubuntu/Debian
sudo apt-get install pgbackrest

# Настройка pgbackrest.conf
[global]
repo1-path=/backup/pgbackrest
repo1-retention-full=2
repo1-retention-diff=4

[mydb]
pg1-path=/var/lib/postgresql/data

# Создать базовый бэкап
pgbackrest --stanza=mydb --type=full backup

# Создать инкрементальный бэкап
pgbackrest --stanza=mydb --type=incr backup

# Восстановить из бэкапа
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

# Создать бэкап
pg_dump -h localhost -U postgres -d mydb -Fc -f "$BACKUP_FILE"

# Загрузить в S3
aws s3 cp "$BACKUP_FILE" "s3://$S3_BUCKET/$S3_PATH"

# Удалить локальный файл
rm "$BACKUP_FILE"

# Удалить старые бэкапы (старше 30 дней)
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

# Создать бэкап
pg_dump -h localhost -U postgres -d mydb -Fc -f "$BACKUP_FILE"

# Загрузить в GCS
gsutil cp "$BACKUP_FILE" "$GCS_PATH"

# Удалить локальный файл
rm "$BACKUP_FILE"
```

## Advanced Restore Techniques

### Selective Table Restore

```sql
-- Создать функцию для выборочного восстановления
CREATE OR REPLACE FUNCTION restore_table_from_backup(
    backup_file TEXT,
    table_name TEXT,
    schema_name TEXT DEFAULT 'public'
)
RETURNS VOID AS $$
BEGIN
    -- Восстановить только указанную таблицу
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
# Восстановление из старой версии PostgreSQL в новую
# 1. Создать дамп из старой версии
/usr/lib/postgresql/12/bin/pg_dump -h old_host -U postgres -d mydb -Fc -f mydb_old.dump

# 2. Восстановить в новую версию
/usr/lib/postgresql/14/bin/pg_restore -h new_host -U postgres -d mydb -v mydb_old.dump

# 3. Обновить расширения
psql -h new_host -U postgres -d mydb -c "ALTER EXTENSION ALL UPDATE;"
```

### Point-in-Time Recovery Advanced

```bash
# Восстановление с использованием recovery target
# Создать recovery.conf (PostgreSQL < 12) или настроить postgresql.conf (PostgreSQL 12+)

# Восстановление до определенного времени
recovery_target_time = '2026-01-16 14:30:00'
recovery_target_action = 'promote'

# Восстановление до определенной транзакции
recovery_target_xid = '12345'
recovery_target_action = 'pause'

# Восстановление до определенного LSN
recovery_target_lsn = '0/12345678'
recovery_target_action = 'shutdown'

# Восстановление до именованной точки восстановления
recovery_target_name = 'before_migration'
recovery_target_action = 'promote'
```

## Backup Verification

### Automated Backup Verification

```sql
-- Создать функцию для проверки целостности бэкапа
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
    
    -- Создать тестовую базу данных
    EXECUTE format('CREATE DATABASE %I', test_db);
    
    BEGIN
        -- Восстановить бэкап
        PERFORM pg_restore(
            '-h', 'localhost',
            '-U', 'postgres',
            '-d', test_db,
            '-v',
            backup_file
        );
        
        -- Проверить целостность
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
    
    -- Удалить тестовую базу данных
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

# Проверить формат бэкапа
if file "$BACKUP_FILE" | grep -q "PostgreSQL custom database dump"; then
    echo "OK: Valid PostgreSQL custom format backup"
else
    echo "ERROR: Invalid backup format"
    exit 1
fi

# Проверить размер бэкапа
BACKUP_SIZE=$(stat -f%z "$BACKUP_FILE" 2>/dev/null || stat -c%s "$BACKUP_FILE" 2>/dev/null)
if [ "$BACKUP_SIZE" -lt 1000 ]; then
    echo "ERROR: Backup file too small"
    exit 1
fi

# Проверить целостность сжатого файла
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
-- Создать таблицу для отслеживания бэкапов
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

-- Функция для записи статуса бэкапа
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

# Проверить последний бэкап
LATEST_BACKUP=$(find "$BACKUP_DIR" -name "*.dump*" -type f -mtime -1 | head -1)

if [ -z "$LATEST_BACKUP" ]; then
    echo "ALERT: No recent backup found in $BACKUP_DIR" | \
        mail -s "PostgreSQL Backup Alert" "$ALERT_EMAIL"
    exit 1
fi

# Проверить размер бэкапа
BACKUP_SIZE=$(stat -f%z "$LATEST_BACKUP" 2>/dev/null || stat -c%s "$LATEST_BACKUP" 2>/dev/null)
MIN_SIZE=1000000  # 1MB

if [ "$BACKUP_SIZE" -lt "$MIN_SIZE" ]; then
    echo "ALERT: Backup file is too small: $BACKUP_SIZE bytes" | \
        mail -s "PostgreSQL Backup Alert" "$ALERT_EMAIL"
    exit 1
fi

# Проверить возраст бэкапа
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

### DR Plan Template

```bash
#!/bin/bash
# disaster_recovery_plan.sh

# Шаг 1: Остановить приложение
echo "Stopping application..."
sudo systemctl stop myapp

# Шаг 2: Остановить PostgreSQL
echo "Stopping PostgreSQL..."
sudo systemctl stop postgresql

# Шаг 3: Выбрать бэкап для восстановления
BACKUP_FILE="/backup/postgresql/daily/20260116/mydb.dump"

# Шаг 4: Удалить поврежденные данные
echo "Removing corrupted data..."
rm -rf /var/lib/postgresql/data/*

# Шаг 5: Восстановить из бэкапа
echo "Restoring from backup..."
pg_restore -h localhost -U postgres -d mydb -v "$BACKUP_FILE"

# Шаг 6: Проверить целостность
echo "Verifying database integrity..."
psql -h localhost -U postgres -d mydb -c "VACUUM ANALYZE;"

# Шаг 7: Запустить PostgreSQL
echo "Starting PostgreSQL..."
sudo systemctl start postgresql

# Шаг 8: Проверить статус
echo "Checking PostgreSQL status..."
sudo systemctl status postgresql

# Шаг 9: Запустить приложение
echo "Starting application..."
sudo systemctl start myapp

echo "Disaster recovery completed"
```

### Recovery Time Objectives (RTO) and Recovery Point Objectives (RPO)

```sql
-- Создать таблицу для отслеживания RTO/RPO
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

-- Функция для расчета метрик восстановления
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
    
    -- Восстановить из бэкапа
    PERFORM pg_restore(
        '-h', 'localhost',
        '-U', 'postgres',
        '-d', current_database(),
        '-v',
        p_backup_file
    );
    
    recovery_end := NOW();
    recovery_time := EXTRACT(EPOCH FROM (recovery_end - recovery_start))::INTEGER;
    
    -- Получить время бэкапа из имени файла или метаданных
    -- Здесь упрощенная версия
    backup_time := NOW() - INTERVAL '24 hours';  -- Пример
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

# Использовать параллельный дамп с максимальным сжатием
pg_dump -h localhost -U postgres -d "$DB_NAME" \
    -Fd \
    -j 4 \
    -f "$BACKUP_DIR/$DATE" \
    -Z 9

# Дополнительное сжатие с помощью pigz (параллельный gzip)
tar -cf - "$BACKUP_DIR/$DATE" | pigz -p 4 > "$BACKUP_DIR/$DATE.tar.gz"

# Удалить несжатый каталог
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

# Еженедельный базовый бэкап
if [ $(date +%u) -eq 1 ]; then
    echo "Creating weekly base backup..."
    mkdir -p "$BASE_BACKUP_DIR/$DATE"
    pg_basebackup -h localhost -U postgres \
        -D "$BASE_BACKUP_DIR/$DATE" \
        -Ft -z -P \
        -l "Weekly base backup $DATE"
    
    # Удалить старые базовые бэкапы
    find "$BASE_BACKUP_DIR" -type d -mtime +28 -exec rm -rf {} \;
fi

# Ежедневный инкрементальный бэкап (только измененные данные)
echo "Creating incremental backup..."
pg_dump -h localhost -U postgres -d mydb \
    -Fc \
    -f "$INCREMENTAL_BACKUP_DIR/mydb_$DATE.dump" \
    --exclude-table=logs \
    --exclude-table=audit_log

# Очистка старых инкрементальных бэкапов
find "$INCREMENTAL_BACKUP_DIR" -type f -mtime +7 -delete

# Очистка старых WAL файлов
find "$WAL_ARCHIVE_DIR" -type f -mtime +14 -delete
```

## Best Practices Summary

### Backup Strategy

1. **Множественные методы**: Используйте комбинацию логических и физических бэкапов
2. **Регулярность**: Ежедневные полные бэкапы для критических баз
3. **Хранение**: Храните бэкапы в разных местах (локально и в облаке)
4. **Тестирование**: Регулярно тестируйте процедуры восстановления

### Security

1. **Шифрование**: Шифруйте бэкапы при хранении
2. **Права доступа**: Ограничьте доступ к бэкапам
3. **Аудит**: Ведите журнал всех операций бэкапа/восстановления

### Monitoring

1. **Автоматические проверки**: Настройте автоматическую проверку бэкапов
2. **Алерты**: Настройте уведомления о проблемах с бэкапами
3. **Метрики**: Отслеживайте размер, время создания и успешность бэкапов

---

## Полезные ссылки

- [PostgreSQL Backup Documentation](https://www.postgresql.org/docs/current/backup.html)
- [pg_dump Documentation](https://www.postgresql.org/docs/current/app-pgdump.html)
- [pg_restore Documentation](https://www.postgresql.org/docs/current/app-pgrestore.html)
- [pg_basebackup Documentation](https://www.postgresql.org/docs/current/app-pgbasebackup.html)
- [Point-in-Time Recovery](https://www.postgresql.org/docs/current/continuous-archiving.html)

---

**Дата последнего обновления:** 2026-01-16

## Содержание

- [Введение в резервное копирование PostgreSQL](#�-ведение-в-�-езе�-вное-копи�-ование-postgresql)
  - [Типы резервного копирования](#�-ип�-�-езе�-вного-копи�-ования)
  - [Выбор метода резервного копирования](#�-�-бо�-ме�-ода-�-езе�-вного-копи�-ования)
- [Логический дамп: pg_dump](#�-оги�-е�-кий-дамп-pg-dump)
  - [Базовое использование pg_dump](#�-азовое-и�-пол�-зование-pg-dump)



