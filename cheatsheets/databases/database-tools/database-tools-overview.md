---
title: "Обзор инструментов для баз данных"
description: "Полное руководство по инструментам для работы с базами данных: клиенты (GUI и CLI), миграции схемы (Flyway, Liquibase), мониторинг, резервное копирование, профилирование и безопасность. Документ покрывает экосистему инструментов для разработчиков и администраторов БД."
tags:
  - databases
  - database-tools
  - database-tools-overview
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Обзор инструментов для баз данных

Полное руководство по инструментам для работы с базами данных: клиенты (GUI и CLI), миграции схемы (Flyway, Liquibase), мониторинг, резервное копирование, профилирование и безопасность. Документ покрывает экосистему инструментов для разработчиков и администраторов БД.

## Полезные ссылки

- [Flyway Documentation](https://documentation.red-gate.com/flyway/)
- [Liquibase Documentation](https://docs.liquibase.com/)
- [DBeaver](https://dbeaver.io/)
- [DataGrip (JetBrains)](https://www.jetbrains.com/datagrip/)
- [Базы данных — раздел проекта](../)

## См. также

- [[postgres-basics|PostgreSQL Basics]]
- [[README|MySQL]]
- [[README|MongoDB]]
- [[rest-api-design|REST API Design]]

## Содержание

- [См. также](#см-также)
- [Введение](#введение)
  - [Категории инструментов](#категории-инструментов)
- [Клиенты баз данных (GUI)](#клиенты-баз-данных-gui)
  - [**DBeaver**](#dbeaver)
  - [**DataGrip** (JetBrains)](#datagrip-jetbrains)
  - [**pgAdmin** (PostgreSQL)](#pgadmin-postgresql)
  - [**MySQL Workbench**](#mysql-workbench)
  - [Общие рекомендации по **GUI**](#общие-рекомендации-по-gui)
- [Клиенты и утилиты (CLI)](#клиенты-и-утилиты-cli)
  - [**PostgreSQL**: **psql**](#postgresql-psql)
- [Подключение к базе](#подключение-к-базе)
- [Выполнение SQL-файла](#выполнение-sql-файла)
- [Вывод в CSV](#вывод-в-csv)
  - [**MySQL**: **mysql**](#mysql-mysql)
- [Подключение](#подключение)
- [Выполнение файла](#выполнение-файла)
  - [**MongoDB**: **mongosh**](#mongodb-mongosh)
- [Подключение к локальному MongoDB](#подключение-к-локальному-mongodb)
- [Выполнение скрипта](#выполнение-скрипта)
- [Миграции схемы: **Flyway**](#миграции-схемы-flyway)
  - [Концепция](#концепция)
  - [Пример структуры каталога](#пример-структуры-каталога)
  - [Пример миграции **V1**](#пример-миграции-v1)
  - [Интеграция с **Java** (Spring Boot)](#интеграция-с-java-spring-boot)
  - [Откат (undo) в **Flyway**](#откат-undo-в-flyway)
  - [**Best Practices** для **Flyway**](#лучшие-практики-для-flyway)
- [Миграции схемы: **Liquibase**](#миграции-схемы-liquibase)
  - [Формат **changelog** (YAML)](#формат-changelog-yaml)
- [db/changelog/changelog-master.yaml](#dbchangelogchangelog-masteryaml)
- [db/changelog/changes/V1-create-users.yaml](#dbchangelogchangesv1-create-usersyaml)
  - [**SQL** в **Liquibase**](#sql-в-liquibase)
  - [Интеграция с **Spring Boot**](#интеграция-с-spring-boot)
  - [Откат в **Liquibase**](#откат-в-liquibase)
  - [**Best Practices** для **Liquibase**](#лучшие-практики-для-liquibase)
- [Сравнение **Flyway** и **Liquibase**](#сравнение-flyway-и-liquibase)
- [Мониторинг и профилирование](#мониторинг-и-профилирование)
  - [Медленные запросы (PostgreSQL)](#медленные-запросы-postgresql)
  - [Метрики и дашборды](#метрики-и-дашборды)
  - [Профилирование в приложении (Java)](#профилирование-в-приложении-java)
- [Резервное копирование и восстановление](#резервное-копирование-и-восстановление)
  - [**PostgreSQL**](#postgresql)
- [Дамп одной базы (custom format, сжатие)](#дамп-одной-базы-custom-format-сжатие)
- [Восстановление](#восстановление)
  - [**MySQL**](#mysql)
- [Дамп](#дамп)
  - [**MongoDB**](#mongodb)
- [Безопасность и управление доступом](#безопасность-и-управление-доступом)
- [Инструменты для конкретных СУБД](#инструменты-для-конкретных-субд)
- [**Best Practices**](#лучшие-практики)
  - [Настройка **Flyway** в **Maven**](#настройка-flyway-в-maven)
  - [Настройка **Liquibase** в **Maven**](#настройка-liquibase-в-maven)
  - [Пример полного набора миграций **Flyway** (V1–V5)](#пример-полного-набора-миграций-flyway-v1v5)
  - [Профилирование запросов в **PostgreSQL** (pg_stat_statements)](#профилирование-запросов-в-postgresql-pg_stat_statements)
  - [Стратегии резервного копирования](#стратегии-резервного-копирования)
  - [Инструменты для ER-моделирования](#инструменты-для-er-моделирования)
  - [Работа с секретами (пароли БД)](#работа-с-секретами-пароли-бд)
  - [Типичные проблемы и решения](#типичные-проблемы-и-решения)

## Введение

Инструменты для баз данных охватывают: подключение и выполнение запросов (клиенты), управление версиями схемы (миграции), мониторинг производительности, резервное копирование и администрирование. Выбор инструментов зависит от используемой СУБД (PostgreSQL, `MySQL`, `MongoDB` и др.) и от роли: разработчик, **DBA** или **DevOps**.

### Категории инструментов

| Категория | Назначение | Примеры |
|-----------|------------|---------|
| **GUI**-клиенты | Подключение к БД, выполнение **SQL**, просмотр данных, `ER`-диаграммы | **DBeaver**, **DataGrip**, **pgAdmin**, **MySQL Workbench** |
| **CLI**-клиенты | Скрипты, автоматизация, **CI/CD** | **psql**, **mysql**, **mongosh** |
| Миграции | Версионирование **DDL** и данных | **Flyway**, **Liquibase** |
| Мониторинг | Метрики, медленные запросы, блокировки | **pg_stat_statements**, **PMM**, **Datadog** |
| **Backup**/**Restore** | Резервное копирование и восстановление | **pg_dump**, **mysqldump**, **mongodump** |


## Клиенты баз данных (GUI)

### DBeaver

Универсальный бесплатный клиент с поддержкой многих СУБД (PostgreSQL, `MySQL`, `Oracle`, `SQL Server`, `MongoDB` и др.).

**Основные возможности:**
- Подключение по **JDBC**/нативным драйверам
- Редактор **SQL** с подсветкой и автодополнением
- `ER`-диаграммы, экспорт данных (CSV, `JSON`, SQL)
- Редактирование данных в табличном виде
- Просмотр планов выполнения (EXPLAIN)

**Пример подключения к `PostgreSQL` (строка подключения):**
```text
# Пример JDBC URL для подключения к PostgreSQL
jdbc:postgresql://localhost:5432/mydb?user=app&password=secret
```

### DataGrip (JetBrains)

Платный **IDE**-клиент от **JetBrains** с глубокой интеграцией в среду разработки.

**Основные возможности:**
- Умное автодополнение **SQL** с учётом схемы
- Рефакторинг переименований (таблицы, колонки) по проекту
- Консоль с историей запросов и сохранёнными сниппетами
- Поддержка многих СУБД и облачных БД (Redshift, `BigQuery` и др.)

### pgAdmin (PostgreSQL)

Официальный **GUI** для **PostgreSQL**: веб-интерфейс или десктоп.

**Основные возможности:**
- Управление серверами, базами, схемами, таблицами
- **Query Tool** с визуализацией планов
- Мониторинг активных запросов и блокировок
- Резервное копирование и восстановление через интерфейс

### MySQL Workbench

Официальный клиент **MySQL**: моделирование, **SQL**-редактор, администрирование.

**Основные возможности:**
- **EER**-диаграммы и генерация **DDL**
- Администрирование пользователей и привилегий
- Репликация и мониторинг

### Общие рекомендации по GUI

- Для мульти-СУБД проектов удобны **DBeaver** или **DataGrip**.
- Для глубокой работы с **PostgreSQL** — **pgAdmin** или **DataGrip**.
- Для **MySQL**-центричной инфраструктуры — **MySQL Workbench**.
- Всегда используйте отдельные учётные записи с минимальными привилегиями для разработки и не храните пароли в открытом виде (используйте менеджеры паролей или системные хранилища).


## Клиенты и утилиты (CLI)

### PostgreSQL: psql

Интерактивный терминальный клиент и режим выполнения скриптов.

```bash
# Подключение к базе
psql -h localhost -p 5432 -U app -d mydb

# Выполнение SQL-файла
psql -h localhost -U app -d mydb -f schema.sql

# Вывод в CSV
psql -h localhost -U app -d mydb -c "SELECT * FROM users" -A -F ','
```

### MySQL: mysql

```bash
# Подключение
mysql -h localhost -P 3306 -u app -p mydb

# Выполнение файла
mysql -h localhost -u app -p mydb < schema.sql
```

### MongoDB: mongosh

```bash
# Подключение к локальному MongoDB
mongosh "mongodb://localhost:27017/mydb"

# Выполнение скрипта
mongosh "mongodb://localhost:27017/mydb" --file script.js
```

**CLI**-инструменты незаменимы в скриптах развёртывания, **CI/CD** и автоматизации резервного копирования.


## Миграции схемы: Flyway

**Flyway** — инструмент миграций на основе нумерованных **SQL**-скриптов (или `Java`-миграций). Поддерживает множество СУБД через **JDBC**.

### Концепция

- Каждая миграция — один файл (или один `Java`-класс).
- Имя файла определяет порядок: `V{**version**}__{**description**}.**sql**`, например `V1__create_users_table.sql`.
- **Flyway** хранит историю в служебной таблице `flyway_schema_history` и выполняет только новые миграции.

### Пример структуры каталога

```text
# Каталог миграций Flyway: версии V1, V2, V3 по порядку
src/main/resources/db/migration/
  V1__create_users_table.sql
  V2__add_roles.sql
  V3__create_orders_table.sql
```

### Пример миграции V1

```sql
-- V1__create_users_table.sql
CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

### Интеграция с Java (Spring Boot)

**В `pom.xml` добавляется зависимость:**

```xml
<!-- Зависимости Flyway для PostgreSQL -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

**В `application.yml`:**

```yaml
# Включение Flyway и путь к миграциям
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

При старте приложения **Flyway** автоматически применит все неприменённые миграции.

### Откат (undo) в Flyway

В коммерческой версии **Flyway Teams** поддерживаются **undo**-миграции. В **Community**-версии откат делают вручную: создают новую миграцию, отменяющую изменения (DROP/ALTER), и при необходимости откатывают данные отдельными скриптами.

### Лучшие практики для Flyway

- Миграции делайте неизменяемыми: после применения в общую историю не редактировать.
- Один логический шаг — одна миграция.
- Используйте транзакции там, где СУБД поддерживает (PostgreSQL — по умолчанию на весь скрипт).
- Для больших данных предпочитайте отдельные скрипты данных или загрузку вне **Flyway**.


## Миграции схемы: Liquibase

**Liquibase** описывает изменения в декларативном формате (XML, `YAML`, JSON) или в **SQL**. Поддерживает изменения структуры и данных, теги (tag) для отката к версии.

### Формат changelog (YAML)

```yaml
# db/changelog/changelog-master.yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/V1-create-users.yaml
  - include:
      file: db/changelog/changes/V2-add-roles.yaml
```

```yaml
# db/changelog/changes/V1-create-users.yaml
databaseChangeLog:
  - changeSet:
      id: 1
      author: dev
      changes:
        - createTable:
            tableName: users
            columns:
              - column:
                  name: id
                  type: BIGSERIAL
                  constraints:
                    primaryKey: true
              - column:
                  name: email
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: name
                  type: VARCHAR(255)
              - column:
                  name: created_at
                  type: TIMESTAMP WITH TIME ZONE
                  defaultValueComputed: CURRENT_TIMESTAMP
  - changeSet:
      id: 2
      author: dev
      changes:
        - createIndex:
            indexName: idx_users_email
            tableName: users
            columns:
              - column:
                  name: email
```

### SQL в Liquibase

**Можно подключать «сырые» **SQL**-файлы:**

```yaml
# Подключение SQL-файла в changeSet
- changeSet:
    id: 3
    author: dev
    changes:
      - sqlFile:
          path: db/changelog/sql/V3-create-orders.sql
```

### Интеграция с Spring Boot

```xml
<!-- Зависимость Liquibase для Spring Boot -->
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

```yaml
# Путь к master changelog в Spring
spring:
  liquibase:
    change-log: classpath:db/changelog/changelog-master.yaml
```

### Откат в Liquibase

**В **changeSet** можно описать `rollback`:**

```yaml
# changeSet с откатом (dropIndex)
- changeSet:
    id: 2
    author: dev
    changes:
      - createIndex:
          indexName: idx_users_email
          tableName: users
          columns:
            - column: { name: email }
    rollback:
      - dropIndex:
          indexName: idx_users_email
          tableName: users
```

Команда `**liquibase rollback** <**tag**>` откатывает базу к указанному тегу.

### Лучшие практики для Liquibase

- Разбивайте **changelog** на файлы по версиям/фичам и подключайте через **include**.
- Всегда задавайте осмысленный **rollback** для обратимых изменений.
- Используйте теги перед рискованными релизами.
- Не меняйте уже применённые **changeSet**; новые изменения — только новыми **changeSet**.


## Сравнение Flyway и Liquibase

| Критерий | **Flyway** | **Liquibase** |
|----------|--------|-----------|
| Формат | **SQL** (и Java) | **XML**, **YAML**, **JSON**, **SQL** |
| Сложность | Проще, «только **SQL**» | Богаче (preconditions, rollback, теги) |
| Откат | Вручную или **Flyway Teams** | Встроенный **rollback** и **rollback** по тегам |
| Порядок | По имени файла (версия) | По порядку в **changelog** |
| Поддержка СУБД | Много через **JDBC** | Много, плюс учёт различий СУБД |

Выбор: **Flyway** — когда достаточно линейных **SQL**-миграций и команда привыкла к «всё в **SQL**». **Liquibase** — когда нужны откаты, единый **changelog** для нескольких сред и абстракция поверх разных СУБД.


## Мониторинг и профилирование

### Медленные запросы (PostgreSQL)

- Включение логирования медленных запросов в `postgresql.conf`: `**log_min_duration_statement** = 1000` (мс).
- Расширение **pg_stat_statements** даёт агрегированную статистику по тексту запроса (время, вызовы, строки).

### Метрики и дашборды

- **Prometheus + экспортеры** (postgres_exporter, mysql_exporter) — сбор метрик.
- **Grafana** — дашборды по метрикам БД и приложения.
- **Percona `Monitoring and Management` (PMM)** — готовые дашборды для **MySQL**/**MongoDB**.

### Профилирование в приложении (Java)

- **Hibernate Statistics** (включить в конфиге) — количество запросов, время.
- Логирование **SQL** с таймингами (например, `spring.`jpa.show-`sql` + `Logback` с паттерном времени).
- Микрометр + трассировка (Micrometer, OpenTelemetry) для связи запросов приложения с вызовами к БД.

Рекомендация: в **production** не включать вывод полного **SQL**; использовать метрики и трейсы вместо этого.


## Резервное копирование и восстановление

### PostgreSQL

```bash
# Дамп одной базы (custom format, сжатие)
pg_dump -h localhost -U app -Fc -f mydb_backup.dump mydb

# Восстановление
pg_restore -h localhost -U app -d mydb_new -Fc mydb_backup.dump
```

### MySQL

```bash
# Дамп
mysqldump -h localhost -u app -p mydb > mydb.sql

# Восстановление
mysql -h localhost -u app -p mydb < mydb.sql
```

### MongoDB

```bash
# Дамп и восстановление базы MongoDB
mongodump --uri="mongodb://localhost:27017/mydb" --out=/backup/mydb
mongorestore --uri="mongodb://localhost:27017/mydb" /backup/mydb
```

Резервное копирование должно быть автоматизировано (cron, планировщик в облаке) с проверкой восстановления и хранением копий вне основного сервера.


## Безопасность и управление доступом

- **Подключение:** по возможности только **TLS**; пароли и строки подключения — в секретах (переменные окружения, vault), не в коде.
- **Учётные записи:** отдельные пользователи для приложения, для миграций, для админов; принцип наименьших привилегий.
- **Аудит:** при необходимости включать логирование доступа к чувствительным таблицам или использовать встроенные механизмы аудита СУБД.
- **Инструменты: GUI**/**CLI** подключать с теми же ограничениями (отдельный пользователь с ограниченными правами для разработки/отладки).


## Инструменты для конкретных СУБД

- **PostgreSQL: psql**, **pgAdmin**, **DBeaver**, **DataGrip**; **pg_dump**/**pg_restore**; **pg_stat_statements**.
- **MySQL: mysql**, **MySQL Workbench**, **DBeaver**, **DataGrip**; **mysqldump**; **Percona Toolkit** при необходимости.
- **MongoDB: mongosh**, **Compass**, **DBeaver**; **mongodump**/**mongorestore**.
- **Oracle/`SQL Server`:** фирменные студии (SQL `Developer`, SSMS) плюс **DBeaver**/**DataGrip** для унификации.

Выбор инструментов для «полного покрытия» темы: минимум один **GUI**-клиент, один **CLI** для скриптов, один инструмент миграций (Flyway или Liquibase), настроенный мониторинг и автоматизированный **backup**/**restore**.


## Лучшие практики

1. **Версионирование схемы:** используйте только один инструмент миграций в проекте (Flyway или Liquibase); не смешивайте с ручными **DDL** в продовой БД.
2. **Неизменяемость миграций:** после применения миграции не редактировать; новые изменения — только новыми файлами/**changeSet**.
3. **Транзакции:** где возможно, выполняйте миграции в одной транзакции (PostgreSQL по умолчанию в `Flyway` так и делает).
4. **Резервные копии:** перед массовыми миграциями или обновлениями делать бэкап и проверять восстановление.
5. **Права доступа:** для приложения и для миграций использовать отдельные учётные записи с минимально необходимыми привилегиями.
6. **Мониторинг:** включить сбор метрик (подключения, медленные запросы, ошибки) и реагировать на деградацию.
7. **Документация:** хранить описание схемы (ER, глоссарий) в репозитории и обновлять при изменении миграций.
8. **CI/`CD`:** в пайплайне запускать миграции на тестовой копии БД и проверять, что приложение стартует после миграций.

### Настройка Flyway в Maven

**Полный цикл сборки с проверкой миграций:**

```xml
<!-- Flyway Maven Plugin: migrate, info, validate -->
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <version>9.22.3</version>
    <configuration>
        <url>jdbc:postgresql://localhost:5432/mydb</url>
        <user>${db.user}</user>
        <password>${db.password}</password>
        <locations>
            <location>classpath:db/migration</location>
        </locations>
        <validateOnMigrate>true</validateOnMigrate>
    </configuration>
</plugin>
```

Команды: `**mvn flyway**:**migrate`, `mvn flyway**:**info`, `mvn flyway**:**validate**`.

### Настройка Liquibase в Maven

```xml
<!-- Liquibase Maven Plugin: update, rollback -->
<plugin>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-maven-plugin</artifactId>
    <version>4.24.0</version>
    <configuration>
        <propertyFile>src/main/resources/liquibase.properties</propertyFile>
    </configuration>
</plugin>
```

В `liquibase.properties`: `changeLogFile`, `url`, `username`, `password`. Команды: `**mvn liquibase**:**update`, `mvn liquibase**:**rollback** -**Dliquibase.rollbackTag**=**pre-release**`.

### Пример полного набора миграций Flyway (V1–V5)

```sql
-- V1__create_users_table.sql (см. выше)

-- V2__add_roles.sql
CREATE TABLE roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
ALTER TABLE users ADD COLUMN role_id BIGINT REFERENCES roles(id);
INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');

-- V3__create_orders_table.sql
CREATE TABLE orders (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id),
    total      DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_orders_user_id ON orders(user_id);

-- V4__add_audit_columns.sql
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE users ADD COLUMN updated_by VARCHAR(255);
-- backfill
UPDATE users SET updated_at = created_at, updated_by = 'system' WHERE updated_at IS NULL;

-- V5__create_index_for_search.sql
CREATE INDEX idx_users_name_lower ON users(LOWER(name));
```

Каждая миграция должна быть идемпотентной по смыслу для окружения (новые объекты создаются один раз; данные обновляются с учётом уже применённых изменений).

### Профилирование запросов в PostgreSQL (pg_stat_statements)

**Включение расширения:**

```sql
-- Включение pg_stat_statements для учёта статистики запросов
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;
```

В `postgresql.conf`: `**shared_preload_libraries** = '**pg_stat_statements**'`, при необходимости задать `pg_stat_statements.max` и перезапустить кластер.

**Пример анализа тяжёлых запросов:**

```sql
-- Топ-20 запросов по суммарному времени выполнения
SELECT
    query,
    calls,
    total_exec_time::numeric(10,2) AS total_ms,
    mean_exec_time::numeric(10,2)   AS mean_ms,
    rows
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 20;
```

Используйте результат для оптимизации индексов и формулировки запросов.

### Стратегии резервного копирования

- **Полный дамп** (ежедневно или еженедельно): **pg_dump** -Fc, **mysqldump**, **mongodump**.
- **Инкрементальный/непрерывный: WAL**-архивирование (PostgreSQL), бинарные логи (MySQL), **oplog** (MongoDB) для **point-in-time recovery**.
- **Хранение:** отдельный диск/объектное хранилище; шифрование и ограничение доступа.
- **Проверка:** периодическое восстановление в тестовое окружение и проверка целостности.

### Инструменты для `ER`-моделирования

- **DBeaver / `DataGrip`:** генерация `ER` по существующей БД.
- **dbdiagram.io, `draw.io`:** описание схемы вручную для документации.
- **Liquibase/`Flyway`:** схема как код; `ER`-диаграммы можно генерировать из миграций (например, через сторонние утилиты или экспорт из БД после применения миграций).

Использование `ER`-диаграмм помогает согласовывать изменения схемы в команде и онбордить новых разработчиков.

### Работа с секретами (пароли БД)

- Не хранить пароли в коде и в открытых конфигах в репозитории.
- Использовать переменные окружения (`SPRING_DATASOURCE_PASSWORD`, `DB_PASSWORD`) или секрет-менеджеры (HashiCorp `Vault`, облачные `Secrets` Manager).
- В **CI/CD** подставлять секреты из защищённого хранилища пайплайна.

### Типичные проблемы и решения

| Проблема | Решение |
|----------|---------|
| Миграция падает в прод | Проверить на копии продовых данных; при необходимости писать откат (Liquibase rollback или новая миграция в Flyway). |
| Долгие миграции блокируют таблицы | Разбивать на этапы; тяжёлые **ALTER** выполнять в окне обслуживания; использовать **online**-инструменты СУБД где возможно. |
| Разные версии схемы в **dev** и **prod** | Строго один источник правды (репозиторий миграций); все окружения приводятся к одному состоянию через миграции. |
| Утечка учётки БД из кода | Ротация паролей; использование **IAM**/интеграции облака вместо статичных паролей где возможно. |

Документ в совокупности с разделами по конкретным СУБД (PostgreSQL, `MySQL`, MongoDB) в проекте даёт полное покрытие темы инструментов для баз данных: от выбора клиента до миграций, мониторинга, резервного копирования и безопасности.


