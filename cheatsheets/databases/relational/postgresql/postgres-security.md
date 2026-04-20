---
title: "PostgreSQL: Безопасность"
description: "Полное руководство по безопасности PostgreSQL: роли и привилегии, Row Level Security (RLS), SSL/TLS соединения, аудит и логирование, шифрование данных, best practices"
tags:
  - postgresql
  - security
  - rls
  - ssl
  - encryption
  - roles
  - privileges
difficulty: "advanced"
prerequisites: ["databases/postgres-basics.md", "databases/postgres-admin.md"]
next: ["databases/postgres-monitoring.md", "databases/postgres-troubleshooting.md"]
updated: "2026-04-20"
related: ["databases/postgres-admin.md", "databases/postgres-replication.md"]
---

# PostgreSQL: Безопасность

## Полезные ссылки

### Официальная документация
- [PostgreSQL Documentation](https://www.postgresql.org/docs/) — официальная документация
- [PostgreSQL Security](https://www.postgresql.org/docs/current/security.html) — раздел по безопасности

### См. также
- [postgres-basics.md](postgres-basics.md) — основы PostgreSQL
- [postgres-admin.md](postgres-admin.md) — администрирование

- [Redis: Безопасность](../../nosql/redis/redis-security.md)
- [TLS / SSL: handshake, сертификаты, конфигурация](../../../security/infrastructure/tls-ssl.md)
- [Безопасность данных (Data Security)](../../../security/data/data-security.md)
## Содержание

- [Введение в безопасность PostgreSQL](#введение-в-безопасность-postgresql)
  - [Основные области безопасности](#основные-области-безопасности)
- [Роли и привилегии](#роли-и-привилегии)
  - [Создание ролей](#создание-ролей)
  - [Управление привилегиями](#управление-привилегиями)
    - [Привилегии на уровне базы данных](#привилегии-на-уровне-базы-данных)
    - [Привилегии на уровне схемы](#привилегии-на-уровне-схемы)
    - [Привилегии на уровне таблицы](#привилегии-на-уровне-таблицы)
    - [Привилегии на уровне столбца](#привилегии-на-уровне-столбца)
  - [Управление ролями](#управление-ролями)
- [Row Level Security (RLS)](#row-level-security-rls)
  - [Включение RLS](#включение-rls)
  - [Создание политик](#создание-политик)
    - [Политика для SELECT](#политика-для-select)
    - [Политика для INSERT](#политика-для-insert)
    - [Политика для UPDATE](#политика-для-update)
    - [Политика для DELETE](#политика-для-delete)
  - [Сложные политики](#сложные-политики)
  - [Управление политиками](#управление-политиками)
- [SSL/TLS соединения](#ssltls-соединения)
  - [Настройка SSL на сервере](#настройка-ssl-на-сервере)
    - [Генерация сертификатов](#генерация-сертификатов)
    - [Настройка postgresql.conf](#настройка-postgresqlconf)
    - [Настройка pg_hba.conf](#настройка-pg_hbaconf)
  - [Настройка SSL на клиенте](#настройка-ssl-на-клиенте)
  - [Режимы sslmode](#режимы-sslmode)
- [Аудит и логирование](#аудит-и-логирование)
  - [Настройка логирования](#настройка-логирования)
    - [postgresql.conf](#postgresqlconf)
  - [pgAudit расширение](#pgaudit-расширение)
  - [Аудит через триггеры](#аудит-через-триггеры)
- [Шифрование данных](#шифрование-данных)
  - [Шифрование на уровне приложения](#шифрование-на-уровне-приложения)
    - [Использование pgcrypto](#использование-pgcrypto)
    - [Шифрование данных](#шифрование-данных-1)
  - [Шифрование на уровне диска](#шифрование-на-уровне-диска)
    - [Transparent Data Encryption (TDE)](#transparent-data-encryption-tde)
- [Лучшие практики безопасности](#лучшие-практики-безопасности)
  - [Управление доступом](#управление-доступом)
  - [Защита паролей](#защита-паролей)
  - [Сетевая безопасность](#сетевая-безопасность)
  - [Мониторинг](#мониторинг)
- [Решение проблем](#решение-проблем)
  - [Проблема: Пользователь не может подключиться](#проблема-пользователь-не-может-подключиться)
  - [Проблема: RLS блокирует доступ](#проблема-rls-блокирует-доступ)
  - [Проблема: SSL соединение не устанавливается](#проблема-ssl-соединение-не-устанавливается)
- [Advanced Security Features](#advanced-security-features)
  - [Column-Level Encryption](#column-level-encryption)
  - [Dynamic RLS Policies](#dynamic-rls-policies)
  - [Time-Based Access Control](#time-based-access-control)
  - [IP-Based Access Control](#ip-based-access-control)
- [Security Hardening](#security-hardening)
  - [Удаление небезопасных функций](#удаление-небезопасных-функций)
  - [Ограничение доступа к системным каталогам](#ограничение-доступа-к-системным-каталогам)
  - [Защита от SQL Injection](#защита-от-sql-injection)
  - [Защита от DoS атак](#защита-от-dos-атак)
- [Advanced RLS Patterns](#advanced-rls-patterns)
  - [Multi-Tenant Isolation](#multi-tenant-isolation)
  - [Hierarchical Access Control](#hierarchical-access-control)
  - [Time-Based RLS](#time-based-rls)
- [SSL/TLS Advanced Configuration](#ssltls-advanced-configuration)
  - [Client Certificate Authentication](#client-certificate-authentication)
  - [Certificate Generation for Clients](#certificate-generation-for-clients)
  - [SSL Connection Monitoring](#ssl-connection-monitoring)
- [Advanced Auditing](#advanced-auditing)
  - [Comprehensive Audit System](#comprehensive-audit-system)
  - [Query-Level Auditing](#query-level-auditing)
- [Password Management](#password-management)
  - [Password Policies](#password-policies)
  - [Password Expiration](#password-expiration)
- [Network Security](#network-security)
  - [Firewall Configuration](#firewall-configuration)
  - [VPN Integration](#vpn-integration)
- [Security Monitoring](#security-monitoring)
  - [Real-Time Security Monitoring](#real-time-security-monitoring)
  - [Failed Login Attempts Tracking](#failed-login-attempts-tracking)
- [Compliance and Regulations](#compliance-and-regulations)
  - [GDPR Compliance](#gdpr-compliance)
  - [Data Retention Policies](#data-retention-policies)
- [Security Best Practices Summary](#security-best-practices-summary)
  - [Authentication](#authentication)
  - [Authorization](#authorization)
  - [Encryption](#encryption)
  - [Monitoring](#monitoring)
- [Security Automation](#security-automation)
  - [Автоматическая ротация паролей](#автоматическая-ротация-паролей)
  - [Автоматическая проверка безопасности](#автоматическая-проверка-безопасности)
- [Security Incident Response](#security-incident-response)
  - [Процедура реагирования на инциденты](#процедура-реагирования-на-инциденты)
  - [Автоматическое блокирование подозрительной активности](#автоматическое-блокирование-подозрительной-активности)
- [Advanced RLS Scenarios](#advanced-rls-scenarios)
  - [Dynamic Policy Based on User Attributes](#dynamic-policy-based-on-user-attributes)
  - [Cross-Table RLS](#cross-table-rls)
- [Encryption Best Practices](#encryption-best-practices)
  - [Key Management](#key-management)
  - [Encrypted Column with Automatic Key Rotation](#encrypted-column-with-automatic-key-rotation)
- [Security Testing](#security-testing)
  - [Penetration Testing Queries](#penetration-testing-queries)
  - [Security Audit Queries](#security-audit-queries)
- [Compliance Frameworks](#compliance-frameworks)
  - [PCI DSS Compliance](#pci-dss-compliance)
  - [HIPAA Compliance](#hipaa-compliance)
- [Security Checklist](#security-checklist)
  - [Initial Setup](#initial-setup)
  - [Regular Maintenance](#regular-maintenance)
  - [Incident Response](#incident-response)

## Введение в безопасность PostgreSQL

Безопасность **PostgreSQL** включает управление доступом, шифрование данных, аудит и множество других аспектов защиты данных.

### Основные области безопасности

1. **Аутентификация**: Проверка личности пользователей
2. **Авторизация**: Управление правами доступа
3. **Шифрование**: Защита данных при передаче и хранении
4. **Аудит**: Отслеживание действий пользователей
5. **Row `Level` Security**: Защита на уровне строк


## Роли и привилегии

### Создание ролей

**Примеры создания ролей с разными правами:**

```sql
-- Создать роль
CREATE ROLE app_user WITH LOGIN PASSWORD 'secure_password';

-- Создать роль с дополнительными правами
CREATE ROLE admin_user WITH
    LOGIN
    PASSWORD 'secure_password'
    CREATEDB
    CREATEROLE
    SUPERUSER;

-- Создать роль без возможности входа (группа)
CREATE ROLE readonly_group;
```

### Управление привилегиями

#### Привилегии на уровне базы данных

```sql
-- Предоставить подключение к базе данных
GRANT CONNECT ON DATABASE mydb TO app_user;

-- Отозвать подключение
REVOKE CONNECT ON DATABASE mydb FROM app_user;
```

#### Привилегии на уровне схемы

```sql
-- Предоставить использование схемы
GRANT USAGE ON SCHEMA public TO app_user;

-- Предоставить все привилегии на схему
GRANT ALL ON SCHEMA public TO app_user;

-- Предоставить создание объектов в схеме
GRANT CREATE ON SCHEMA public TO app_user;
```

#### Привилегии на уровне таблицы

```sql
-- Предоставить SELECT
GRANT SELECT ON TABLE users TO app_user;

-- Предоставить несколько привилегий
GRANT SELECT, INSERT, UPDATE ON TABLE users TO app_user;

-- Предоставить все привилегии
GRANT ALL ON TABLE users TO app_user;

-- Предоставить привилегии на все таблицы
GRANT SELECT ON ALL TABLES IN SCHEMA public TO app_user;

-- Предоставить привилегии на будущие таблицы
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT ON TABLES TO app_user;
```

#### Привилегии на уровне столбца

```sql
-- Предоставить SELECT на конкретные столбцы
GRANT SELECT (id, name, email) ON TABLE users TO app_user;

-- Отозвать SELECT на столбец
REVOKE SELECT (password) ON TABLE users FROM app_user;
```

### Управление ролями

```sql
-- Добавить роль в другую роль
GRANT admin_role TO app_user;

-- Удалить роль из другой роли
REVOKE admin_role FROM app_user;

-- Просмотр ролей
\du

-- Просмотр привилегий
\dp table_name
```


## Row Level Security (RLS)

**Row Level Security** позволяет ограничивать доступ к строкам таблицы на основе политик.

### Включение RLS

```sql
-- Включить RLS на таблице
ALTER TABLE users ENABLE ROW LEVEL SECURITY;

-- Проверить статус RLS
SELECT tablename, rowsecurity
FROM pg_tables
WHERE tablename = 'users';
```

### Создание политик

#### Политика для SELECT

```sql
-- Пользователи могут видеть только свои данные
CREATE POLICY user_select_policy ON users
FOR SELECT
TO app_user
USING (id = current_setting('app.user_id')::integer);
```

#### Политика для INSERT

```sql
-- Пользователи могут вставлять только свои данные
CREATE POLICY user_insert_policy ON users
FOR INSERT
TO app_user
WITH CHECK (id = current_setting('app.user_id')::integer);
```

#### Политика для UPDATE

```sql
-- Пользователи могут обновлять только свои данные
CREATE POLICY user_update_policy ON users
FOR UPDATE
TO app_user
USING (id = current_setting('app.user_id')::integer)
WITH CHECK (id = current_setting('app.user_id')::integer);
```

#### Политика для DELETE

```sql
-- Пользователи могут удалять только свои данные
CREATE POLICY user_delete_policy ON users
FOR DELETE
TO app_user
USING (id = current_setting('app.user_id')::integer);
```

### Сложные политики

```sql
-- Политика на основе роли
CREATE POLICY manager_policy ON orders
FOR ALL
TO manager_role
USING (
    department_id = (
        SELECT department_id
        FROM employees
        WHERE id = current_setting('app.employee_id')::integer
    )
);

-- Политика с использованием функций
CREATE POLICY time_based_policy ON sensitive_data
FOR SELECT
TO app_user
USING (
    current_setting('app.user_role') = 'admin'
    OR created_at < NOW() - INTERVAL '1 year'
);
```

### Управление политиками

```sql
-- Просмотр политик
SELECT * FROM pg_policies WHERE tablename = 'users';

-- Изменить политику
ALTER POLICY user_select_policy ON users
USING (id = current_setting('app.user_id')::integer AND active = true);

-- Удалить политику
DROP POLICY user_select_policy ON users;
```


## SSL/TLS соединения

### Настройка SSL на сервере

#### Генерация сертификатов

```bash
# Создать приватный ключ
openssl genrsa -out server.key 2048

# Создать сертификат
openssl req -new -x509 -key server.key -days 365 -out server.crt

# Установить права
chmod 600 server.key
chown postgres:postgres server.key server.crt
```

#### Настройка postgresql.conf

```conf
# Включить SSL
ssl = on

# Пути к сертификатам
ssl_cert_file = 'server.crt'
ssl_key_file = 'server.key'

# Опционально: CA сертификат
ssl_ca_file = 'ca.crt'

# Минимальная версия TLS
ssl_min_protocol_version = 'TLSv1.2'
```

#### Настройка pg_hba.conf

```conf
# Требовать SSL для всех подключений
hostssl    all    all    0.0.0.0/0    md5

# Или для конкретной базы данных
hostssl    mydb    app_user    192.168.1.0/24    md5
```

### Настройка SSL на клиенте

```bash
# Подключение с SSL
psql "host=localhost dbname=mydb user=app_user sslmode=require"

# Подключение с проверкой сертификата
psql "host=localhost dbname=mydb user=app_user sslmode=verify-full sslrootcert=ca.crt"
```

### Режимы sslmode

- `disable`: **SSL** отключен
- `allow`: **SSL** опционален
- `prefer`: **SSL** предпочтителен
- `require`: **SSL** обязателен
- `verify-ca`: **SSL** обязателен + проверка `CA`
- `verify-full`: **SSL** обязателен + проверка `CA` и **hostname**


## Аудит и логирование

### Настройка логирования

#### postgresql.conf

```conf
# Включить логирование
logging_collector = on
log_directory = 'log'
log_filename = 'postgresql-%Y-%m-%d_%H%M%S.log'

# Уровни логирования
log_min_messages = warning
log_min_error_statement = error
log_min_duration_statement = 1000  # Логировать запросы > 1 секунды

# Логирование подключений
log_connections = on
log_disconnections = on
log_duration = on

# Логирование DDL
log_statement = 'ddl'  # или 'all', 'mod', 'none'

# Логирование медленных запросов
log_min_duration_statement = 1000
log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h '
```

### pgAudit расширение

```sql
-- Установить pgAudit
CREATE EXTENSION pgaudit;

-- Настроить аудит
ALTER SYSTEM SET pgaudit.log = 'read,write';
ALTER SYSTEM SET pgaudit.log_catalog = off;
ALTER SYSTEM SET pgaudit.log_parameter = on;
ALTER SYSTEM SET pgaudit.log_statement_once = off;
```

### Аудит через триггеры

```sql
-- Создать таблицу аудита
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    table_name TEXT,
    operation TEXT,
    old_data JSONB,
    new_data JSONB,
    changed_by TEXT,
    changed_at TIMESTAMP DEFAULT NOW()
);

-- Функция аудита
CREATE OR REPLACE FUNCTION audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO audit_log (table_name, operation, old_data, new_data, changed_by)
    VALUES (
        TG_TABLE_NAME,
        TG_OP,
        row_to_json(OLD)::JSONB,
        row_to_json(NEW)::JSONB,
        current_user
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Применить к таблице
CREATE TRIGGER users_audit
AFTER INSERT OR UPDATE OR DELETE ON users
FOR EACH ROW EXECUTE FUNCTION audit_trigger();
```


## Шифрование данных

### Шифрование на уровне приложения

#### Использование pgcrypto

```sql
-- Установить расширение
CREATE EXTENSION pgcrypto;

-- Хеширование паролей
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100),
    password_hash TEXT
);

-- Вставка с хешированием
INSERT INTO users (username, password_hash)
VALUES ('user1', crypt('password123', gen_salt('bf', 10)));

-- Проверка пароля
SELECT * FROM users
WHERE username = 'user1'
AND password_hash = crypt('password123', password_hash);
```

#### Шифрование данных

```sql
-- Шифрование AES
SELECT encrypt('sensitive data', 'encryption_key', 'aes');

-- Расшифровка
SELECT decrypt(encrypted_data, 'encryption_key', 'aes');

-- Хранение зашифрованных данных
CREATE TABLE sensitive_data (
    id SERIAL PRIMARY KEY,
    encrypted_field BYTEA
);

INSERT INTO sensitive_data (encrypted_field)
VALUES (encrypt('secret data', 'my_key', 'aes'));
```

### Шифрование на уровне диска

#### Transparent Data Encryption (TDE)

**PostgreSQL** не поддерживает нативный **TDE**, но можно использовать:**

1. **LUKS**: Шифрование на уровне блочного устройства
2. **Encrypted filesystems**: Шифрование файловой системы
3. **Database-level encryption**: Расширения для шифрования


## Лучшие практики безопасности

### Управление доступом

1. **Принцип наименьших привилегий**: Предоставлять только необходимые права
2. **Разделение ролей**: Разные роли для разных задач
3. **Регулярный аудит**: Проверка прав доступа
4. **Удаление неиспользуемых ролей**: Очистка старых аккаунтов

### Защита паролей

1. **Сильные пароли**: Минимум 12 символов, смешанный регистр, цифры, символы
2. **Хеширование**: Всегда хешировать пароли
3. **Смена паролей**: Регулярная смена паролей
4. **Хранение**: Не хранить пароли в открытом виде

### Сетевая безопасность

1. **SSL/TLS**: Всегда использовать для удаленных подключений
2. **Firewall**: Ограничить доступ к порту **PostgreSQL**
3. **VPN**: Использовать **VPN** для доступа к базе данных
4. **IP whitelist**: Ограничить доступ по `IP` адресам

### Мониторинг

1. **Логирование**: Включить детальное логирование
2. **Аудит**: Отслеживать все действия
3. **Алерты**: Настроить уведомления о подозрительной активности
4. **Регулярные проверки**: Аудит безопасности


## Решение проблем

### Проблема: Пользователь не может подключиться

**Решение:**
```sql
-- Проверить права подключения
SELECT * FROM pg_roles WHERE rolname = 'app_user';

-- Предоставить права
GRANT CONNECT ON DATABASE mydb TO app_user;

-- Проверить pg_hba.conf
```

### Проблема: RLS блокирует доступ

**Решение:**
```sql
-- Проверить политики
SELECT * FROM pg_policies WHERE tablename = 'users';

-- Временно отключить RLS для отладки
ALTER TABLE users DISABLE ROW LEVEL SECURITY;

-- Проверить настройки сессии
SHOW app.user_id;
```

### Проблема: SSL соединение не устанавливается

**Решение:**
```bash
# Проверить сертификаты
openssl x509 -in server.crt -text -noout

# Проверить права
ls -l server.key server.crt

# Проверить логи
tail -f /var/log/postgresql/postgresql-*.log
```

## Advanced Security Features

### Column-Level Encryption

```sql
-- Создать функцию для шифрования столбцов
CREATE OR REPLACE FUNCTION encrypt_column(
    data TEXT,
    key TEXT
)
RETURNS BYTEA AS $$
BEGIN
    RETURN encrypt(data::bytea, key, 'aes');
END;
$$ LANGUAGE plpgsql;

-- Создать функцию для расшифровки
CREATE OR REPLACE FUNCTION decrypt_column(
    encrypted_data BYTEA,
    key TEXT
)
RETURNS TEXT AS $$
BEGIN
    RETURN decrypt(encrypted_data, key, 'aes')::TEXT;
END;
$$ LANGUAGE plpgsql;

-- Использовать в таблице
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100),
    email_encrypted BYTEA,
    phone_encrypted BYTEA
);

-- Вставка с шифрованием
INSERT INTO users (username, email_encrypted, phone_encrypted)
VALUES (
    'user1',
    encrypt_column('user@example.com', 'encryption_key'),
    encrypt_column('+1234567890', 'encryption_key')
);

-- Чтение с расшифровкой
SELECT
    id,
    username,
    decrypt_column(email_encrypted, 'encryption_key') AS email,
    decrypt_column(phone_encrypted, 'encryption_key') AS phone
FROM users;
```

### Dynamic RLS Policies

```sql
-- Создать функцию для динамических политик
CREATE OR REPLACE FUNCTION get_user_tenant_id()
RETURNS INTEGER AS $$
BEGIN
    RETURN current_setting('app.tenant_id', true)::INTEGER;
END;
$$ LANGUAGE plpgsql STABLE;

-- Использовать в политике
CREATE POLICY tenant_isolation_policy ON orders
FOR ALL
TO app_user
USING (tenant_id = get_user_tenant_id())
WITH CHECK (tenant_id = get_user_tenant_id());
```

### Time-Based Access Control

```sql
-- Политика с временными ограничениями
CREATE POLICY time_based_access ON sensitive_data
FOR SELECT
TO app_user
USING (
    current_setting('app.user_role') = 'admin'
    OR (
        current_setting('app.user_role') = 'user'
        AND created_at < NOW() - INTERVAL '1 year'
    )
);
```

### IP-Based Access Control

```sql
-- Функция для проверки IP
CREATE OR REPLACE FUNCTION check_allowed_ip()
RETURNS BOOLEAN AS $$
DECLARE
    client_ip INET;
    allowed_ips INET[];
BEGIN
    client_ip := inet_client_addr();
    allowed_ips := ARRAY[
        '192.168.1.0/24'::INET,
        '10.0.0.0/8'::INET
    ];

    RETURN client_ip = ANY(allowed_ips);
END;
$$ LANGUAGE plpgsql STABLE;

-- Использовать в политике
CREATE POLICY ip_based_policy ON admin_data
FOR ALL
TO app_user
USING (check_allowed_ip());
```

## Security Hardening

### Удаление небезопасных функций

```sql
-- Отозвать права на опасные функции
REVOKE EXECUTE ON FUNCTION pg_read_file(TEXT) FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION pg_ls_dir(TEXT) FROM PUBLIC;
REVOKE EXECUTE ON FUNCTION pg_stat_file(TEXT) FROM PUBLIC;
```

### Ограничение доступа к системным каталогам

```sql
-- Ограничить доступ к pg_catalog
REVOKE ALL ON SCHEMA pg_catalog FROM PUBLIC;
REVOKE ALL ON SCHEMA information_schema FROM PUBLIC;

-- Предоставить доступ только суперпользователю
GRANT USAGE ON SCHEMA pg_catalog TO postgres;
```

### Защита от SQL Injection

```sql
-- Использовать prepared statements
PREPARE safe_query(TEXT) AS
SELECT * FROM users WHERE username = $1;

EXECUTE safe_query('user1');

-- Использовать параметризованные запросы
CREATE OR REPLACE FUNCTION safe_user_lookup(p_username TEXT)
RETURNS TABLE(id INTEGER, username TEXT, email TEXT) AS $$
BEGIN
    RETURN QUERY
    SELECT u.id, u.username, u.email
    FROM users u
    WHERE u.username = p_username;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

### Защита от DoS атак

```sql
-- Ограничить количество подключений
ALTER ROLE app_user WITH CONNECTION LIMIT 10;

-- Ограничить время выполнения запросов
ALTER ROLE app_user SET statement_timeout = '30s';

-- Ограничить использование памяти
ALTER ROLE app_user SET work_mem = '16MB';
```

## Advanced RLS Patterns

### Multi-Tenant Isolation

```sql
-- Создать таблицу с tenant_id
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    tenant_id INTEGER NOT NULL,
    order_date DATE,
    amount DECIMAL(10,2)
);

-- Включить RLS
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

-- Политика для изоляции tenant
CREATE POLICY tenant_isolation ON orders
FOR ALL
TO app_user
USING (tenant_id = current_setting('app.tenant_id')::INTEGER)
WITH CHECK (tenant_id = current_setting('app.tenant_id')::INTEGER);

-- Установить tenant_id для сессии
SET app.tenant_id = '1';
```

### Hierarchical Access Control

```sql
-- Политика с иерархией доступа
CREATE POLICY hierarchical_access ON documents
FOR ALL
TO app_user
USING (
    owner_id = current_setting('app.user_id')::INTEGER
    OR department_id IN (
        SELECT department_id
        FROM employees
        WHERE id = current_setting('app.user_id')::INTEGER
    )
    OR EXISTS (
        SELECT 1 FROM permissions
        WHERE document_id = documents.id
        AND user_id = current_setting('app.user_id')::INTEGER
    )
);
```

### Time-Based RLS

```sql
-- Политика с временными ограничениями
CREATE POLICY time_based_rls ON audit_logs
FOR SELECT
TO auditor_role
USING (
    created_at >= NOW() - INTERVAL '90 days'
    OR current_setting('app.audit_level') = 'full'
);
```

## SSL/TLS Advanced Configuration

### Client Certificate Authentication

```conf
# postgresql.conf
ssl = on
ssl_cert_file = 'server.crt'
ssl_key_file = 'server.key'
ssl_ca_file = 'ca.crt'
ssl_crl_file = 'ca.crl'

# pg_hba.conf
hostssl    all    all    0.0.0.0/0    cert
```

### Certificate Generation for Clients

```bash
# Создать CA
openssl req -new -x509 -days 3650 -keyout ca.key -out ca.crt

# Создать client key
openssl genrsa -out client.key 2048

# Создать client certificate request
openssl req -new -key client.key -out client.csr

# Подписать client certificate
openssl x509 -req -in client.csr -CA ca.crt -CAkey ca.key \
    -CAcreateserial -out client.crt -days 365
```

### SSL Connection Monitoring

```sql
-- Проверить SSL соединения
SELECT
    pid,
    usename,
    application_name,
    client_addr,
    ssl,
    sslversion,
    sslcipher
FROM pg_stat_ssl
JOIN pg_stat_activity USING (pid);
```

## Advanced Auditing

### Comprehensive Audit System

```sql
-- Создать расширенную таблицу аудита
CREATE TABLE comprehensive_audit (
    id BIGSERIAL PRIMARY KEY,
    event_time TIMESTAMPTZ DEFAULT NOW(),
    event_type TEXT,
    schema_name TEXT,
    table_name TEXT,
    user_name TEXT,
    application_name TEXT,
    client_addr INET,
    client_port INTEGER,
    session_id TEXT,
    command_tag TEXT,
    query_text TEXT,
    old_data JSONB,
    new_data JSONB,
    changed_columns TEXT[],
    transaction_id BIGINT
);

-- Функция для комплексного аудита
CREATE OR REPLACE FUNCTION comprehensive_audit_trigger()
RETURNS TRIGGER AS $$
DECLARE
    old_json JSONB;
    new_json JSONB;
    changed_cols TEXT[];
BEGIN
    IF TG_OP = 'DELETE' THEN
        old_json := row_to_json(OLD)::JSONB;
        new_json := NULL;
    ELSIF TG_OP = 'UPDATE' THEN
        old_json := row_to_json(OLD)::JSONB;
        new_json := row_to_json(NEW)::JSONB;

        -- Определить измененные столбцы
        SELECT array_agg(key) INTO changed_cols
        FROM jsonb_each(old_json)
        WHERE value IS DISTINCT FROM new_json->key;
    ELSE
        old_json := NULL;
        new_json := row_to_json(NEW)::JSONB;
    END IF;

    INSERT INTO comprehensive_audit (
        event_type,
        schema_name,
        table_name,
        user_name,
        application_name,
        client_addr,
        command_tag,
        old_data,
        new_data,
        changed_columns,
        transaction_id
    ) VALUES (
        TG_OP,
        TG_TABLE_SCHEMA,
        TG_TABLE_NAME,
        current_user,
        current_setting('application_name', true),
        inet_client_addr(),
        current_setting('transaction_isolation', true),
        old_json,
        new_json,
        changed_cols,
        txid_current()
    );

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;
```

### Query-Level Auditing

```sql
-- Аудит на уровне запросов
CREATE OR REPLACE FUNCTION audit_query()
RETURNS EVENT_TRIGGER AS $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT * FROM pg_event_trigger_ddl_commands()
    LOOP
        INSERT INTO comprehensive_audit (
            event_type,
            schema_name,
            table_name,
            user_name,
            query_text
        ) VALUES (
            r.command_tag,
            r.schema_name,
            r.object_identity,
            current_user,
            current_query()
        );
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Создать event trigger
CREATE EVENT TRIGGER audit_ddl
ON ddl_command_end
EXECUTE FUNCTION audit_query();
```

## Password Management

### Password Policies

```sql
-- Создать функцию для проверки сложности пароля
CREATE OR REPLACE FUNCTION validate_password(password TEXT)
RETURNS BOOLEAN AS $$
BEGIN
    -- Минимум 12 символов
    IF length(password) < 12 THEN
        RAISE EXCEPTION 'Password must be at least 12 characters long';
    END IF;

    -- Должен содержать заглавные буквы
    IF password !~ '[A-Z]' THEN
        RAISE EXCEPTION 'Password must contain uppercase letters';
    END IF;

    -- Должен содержать строчные буквы
    IF password !~ '[a-z]' THEN
        RAISE EXCEPTION 'Password must contain lowercase letters';
    END IF;

    -- Должен содержать цифры
    IF password !~ '[0-9]' THEN
        RAISE EXCEPTION 'Password must contain digits';
    END IF;

    -- Должен содержать специальные символы
    IF password !~ '[!@#$%^&*(),.?":{}|<>]' THEN
        RAISE EXCEPTION 'Password must contain special characters';
    END IF;

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- Использовать при смене пароля
CREATE OR REPLACE FUNCTION change_password(
    username TEXT,
    old_password TEXT,
    new_password TEXT
)
RETURNS VOID AS $$
BEGIN
    -- Проверить старый пароль
    IF NOT EXISTS (
        SELECT 1 FROM users
        WHERE username = change_password.username
        AND password_hash = crypt(old_password, password_hash)
    ) THEN
        RAISE EXCEPTION 'Invalid old password';
    END IF;

    -- Проверить новый пароль
    PERFORM validate_password(new_password);

    -- Обновить пароль
    UPDATE users
    SET password_hash = crypt(new_password, gen_salt('bf', 12))
    WHERE username = change_password.username;
END;
$$ LANGUAGE plpgsql;
```

### Password Expiration

```sql
-- Добавить поле для срока действия пароля
ALTER TABLE users ADD COLUMN password_expires_at TIMESTAMPTZ;

-- Функция для проверки срока действия
CREATE OR REPLACE FUNCTION check_password_expiry()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.password_expires_at IS NOT NULL
       AND NEW.password_expires_at < NOW() THEN
        RAISE EXCEPTION 'Password has expired. Please change your password.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Применить триггер
CREATE TRIGGER password_expiry_check
BEFORE SELECT ON users
FOR EACH ROW EXECUTE FUNCTION check_password_expiry();
```

## Network Security

### Firewall Configuration

```bash
# iptables rules для PostgreSQL
# Разрешить только локальные подключения
iptables -A INPUT -p tcp --dport 5432 -s 127.0.0.1 -j ACCEPT

# Разрешить подключения из определенной подсети
iptables -A INPUT -p tcp --dport 5432 -s 192.168.1.0/24 -j ACCEPT

# Заблокировать все остальные подключения
iptables -A INPUT -p tcp --dport 5432 -j DROP

# Сохранить правила
iptables-save > /etc/iptables/rules.v4
```

### VPN Integration

```conf
# pg_hba.conf для VPN
# Разрешить подключения только через VPN
hostssl    all    all    10.8.0.0/24    md5
hostssl    all    all    10.9.0.0/24    md5

# Блокировать все остальные
host       all    all    0.0.0.0/0      reject
```

## Security Monitoring

### Real-Time Security Monitoring

```sql
-- Создать представление для мониторинга безопасности
CREATE VIEW security_monitoring AS
SELECT
    pid,
    usename,
    application_name,
    client_addr,
    state,
    query_start,
    state_change,
    wait_event_type,
    wait_event,
    query
FROM pg_stat_activity
WHERE state = 'active'
AND query NOT LIKE '%pg_stat_activity%'
ORDER BY query_start;

-- Функция для обнаружения подозрительной активности
CREATE OR REPLACE FUNCTION detect_suspicious_activity()
RETURNS TABLE(
    pid INTEGER,
    usename TEXT,
    client_addr INET,
    query TEXT,
    risk_level TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        a.pid,
        a.usename,
        a.client_addr,
        a.query,
        CASE
            WHEN a.query ILIKE '%DROP%' THEN 'HIGH'
            WHEN a.query ILIKE '%DELETE%' THEN 'MEDIUM'
            WHEN a.query ILIKE '%UPDATE%' THEN 'LOW'
            ELSE 'INFO'
        END AS risk_level
    FROM pg_stat_activity a
    WHERE a.state = 'active'
    AND a.query NOT LIKE '%pg_stat_activity%'
    AND (
        a.query ILIKE '%DROP%'
        OR a.query ILIKE '%TRUNCATE%'
        OR a.query ILIKE '%ALTER%'
    );
END;
$$ LANGUAGE plpgsql;
```

### Failed Login Attempts Tracking

```sql
-- Создать таблицу для отслеживания неудачных попыток входа
CREATE TABLE failed_login_attempts (
    id SERIAL PRIMARY KEY,
    username TEXT,
    client_addr INET,
    attempt_time TIMESTAMPTZ DEFAULT NOW(),
    reason TEXT
);

-- Функция для блокировки после множественных попыток
CREATE OR REPLACE FUNCTION check_failed_attempts(p_username TEXT)
RETURNS BOOLEAN AS $$
DECLARE
    attempt_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO attempt_count
    FROM failed_login_attempts
    WHERE username = p_username
    AND attempt_time > NOW() - INTERVAL '15 minutes';

    IF attempt_count >= 5 THEN
        RAISE EXCEPTION 'Account temporarily locked due to multiple failed login attempts';
    END IF;

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;
```

## Compliance and Regulations

### GDPR Compliance

```sql
-- Функция для удаления персональных данных (GDPR)
CREATE OR REPLACE FUNCTION gdpr_delete_user_data(p_user_id INTEGER)
RETURNS VOID AS $$
BEGIN
    -- Анонимизировать данные
    UPDATE users
    SET
        email = 'deleted_' || id || '@deleted.local',
        phone = NULL,
        address = NULL
    WHERE id = p_user_id;

    -- Удалить связанные данные
    DELETE FROM user_preferences WHERE user_id = p_user_id;
    DELETE FROM user_sessions WHERE user_id = p_user_id;

    -- Логировать удаление
    INSERT INTO audit_log (
        table_name,
        operation,
        changed_by,
        notes
    ) VALUES (
        'users',
        'GDPR_DELETE',
        current_user,
        format('User %s data deleted per GDPR request', p_user_id)
    );
END;
$$ LANGUAGE plpgsql;
```

### Data Retention Policies

```sql
-- Функция для автоматического удаления старых данных
CREATE OR REPLACE FUNCTION apply_retention_policy()
RETURNS VOID AS $$
BEGIN
    -- Удалить данные старше 7 лет
    DELETE FROM audit_logs
    WHERE created_at < NOW() - INTERVAL '7 years';

    -- Анонимизировать данные старше 3 лет
    UPDATE user_data
    SET
        personal_info = NULL,
        notes = 'Data anonymized per retention policy'
    WHERE created_at < NOW() - INTERVAL '3 years'
    AND personal_info IS NOT NULL;
END;
$$ LANGUAGE plpgsql;

-- Запланировать через pg_cron
SELECT cron.schedule('retention-policy', '0 2 * * 0',
    'SELECT apply_retention_policy();');
```

## Security Best Practices Summary

### Authentication

1. **Использовать сильные пароли** с проверкой сложности
2. **Реализовать двухфакторную аутентификацию** где возможно
3. **Ограничить количество попыток входа**
4. **Использовать `SSL`/TLS** для всех подключений
5. **Регулярно менять пароли** администраторов

### Authorization

1. **Принцип наименьших привилегий**
2. **Использовать RLS** для изоляции данных
3. **Разделять роли** по функциональности
4. **Регулярно аудировать права доступа**
5. **Удалять неиспользуемые роли**

### Encryption

1. **Шифровать данные при передаче** (SSL/TLS)
2. **Шифровать чувствительные данные** в БД
3. **Использовать сильные алгоритмы** шифрования
4. **Безопасно хранить ключи** шифрования
5. **Регулярно обновлять сертификаты**

### Monitoring

1. **Включить детальное логирование**
2. **Мониторить подозрительную активность**
3. **Настроить алерты** на критичные события
4. **Регулярно проверять логи**
5. **Вести аудит всех изменений**

## Security Automation

### Автоматическая ротация паролей

```sql
-- Создать функцию для ротации паролей
CREATE OR REPLACE FUNCTION rotate_passwords()
RETURNS VOID AS $$
DECLARE
    user_rec RECORD;
    new_password TEXT;
BEGIN
    FOR user_rec IN
        SELECT rolname FROM pg_roles
        WHERE rolcanlogin = true
        AND rolname NOT IN ('postgres', 'replicator')
    LOOP
        -- Генерировать новый пароль
        new_password := gen_random_uuid()::TEXT;

        -- Обновить пароль
        EXECUTE format('ALTER ROLE %I WITH PASSWORD %L',
            user_rec.rolname, new_password);

        -- Логировать изменение
        RAISE NOTICE 'Password rotated for user: %', user_rec.rolname;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Запланировать через pg_cron
SELECT cron.schedule('rotate-passwords', '0 0 1 * *',
    'SELECT rotate_passwords();');
```

### Автоматическая проверка безопасности

```sql
-- Функция для проверки безопасности
CREATE OR REPLACE FUNCTION security_audit_check()
RETURNS TABLE(
    check_name TEXT,
    status TEXT,
    message TEXT
) AS $$
BEGIN
    RETURN QUERY
    -- Проверка ролей без паролей
    SELECT
        'Roles without passwords'::TEXT,
        CASE WHEN COUNT(*) > 0 THEN 'WARNING' ELSE 'OK' END,
        format('%s roles without passwords', COUNT(*))
    FROM pg_roles
    WHERE rolcanlogin = true
    AND rolpassword IS NULL

    UNION ALL

    -- Проверка суперпользователей
    SELECT
        'Superuser count'::TEXT,
        CASE WHEN COUNT(*) > 2 THEN 'WARNING' ELSE 'OK' END,
        format('%s superusers found', COUNT(*))
    FROM pg_roles
    WHERE rolsuper = true

    UNION ALL

    -- Проверка SSL
    SELECT
        'SSL connections'::TEXT,
        CASE WHEN COUNT(*) = 0 THEN 'WARNING' ELSE 'OK' END,
        format('%s SSL connections', COUNT(*))
    FROM pg_stat_ssl
    WHERE ssl = true;
END;
$$ LANGUAGE plpgsql;
```

## Security Incident Response

### Процедура реагирования на инциденты

```sql
-- Создать таблицу для отслеживания инцидентов
CREATE TABLE security_incidents (
    id SERIAL PRIMARY KEY,
    incident_type TEXT NOT NULL,
    severity TEXT NOT NULL,
    description TEXT,
    detected_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ,
    resolved_by TEXT,
    actions_taken TEXT[]
);

-- Функция для регистрации инцидента
CREATE OR REPLACE FUNCTION log_security_incident(
    p_type TEXT,
    p_severity TEXT,
    p_description TEXT
)
RETURNS INTEGER AS $$
DECLARE
    incident_id INTEGER;
BEGIN
    INSERT INTO security_incidents (
        incident_type,
        severity,
        description
    ) VALUES (
        p_type,
        p_severity,
        p_description
    ) RETURNING id INTO incident_id;

    -- Отправить уведомление для критичных инцидентов
    IF p_severity = 'CRITICAL' THEN
        PERFORM pg_notify('security_alert',
            format('Critical security incident: %s', p_description));
    END IF;

    RETURN incident_id;
END;
$$ LANGUAGE plpgsql;
```

### Автоматическое блокирование подозрительной активности

```sql
-- Функция для блокировки пользователя
CREATE OR REPLACE FUNCTION block_user(p_username TEXT)
RETURNS VOID AS $$
BEGIN
    -- Заблокировать роль
    EXECUTE format('ALTER ROLE %I WITH NOLOGIN', p_username);

    -- Завершить активные сессии
    PERFORM pg_terminate_backend(pid)
    FROM pg_stat_activity
    WHERE usename = p_username;

    -- Логировать блокировку
    PERFORM log_security_incident(
        'USER_BLOCKED',
        'HIGH',
        format('User %s has been blocked', p_username)
    );
END;
$$ LANGUAGE plpgsql;
```

## Advanced RLS Scenarios

### Dynamic Policy Based on User Attributes

```sql
-- Создать функцию для получения прав пользователя
CREATE OR REPLACE FUNCTION get_user_permissions(p_user_id INTEGER)
RETURNS TABLE(
    can_read BOOLEAN,
    can_write BOOLEAN,
    can_delete BOOLEAN,
    departments INTEGER[]
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        u.can_read,
        u.can_write,
        u.can_delete,
        u.department_access
    FROM user_permissions u
    WHERE u.user_id = p_user_id;
END;
$$ LANGUAGE plpgsql STABLE;

-- Использовать в политике
CREATE POLICY dynamic_permissions ON documents
FOR ALL
TO app_user
USING (
    EXISTS (
        SELECT 1 FROM get_user_permissions(
            current_setting('app.user_id')::INTEGER
        ) p
        WHERE
            (TG_OP = 'SELECT' AND p.can_read)
            OR (TG_OP IN ('INSERT', 'UPDATE') AND p.can_write)
            OR (TG_OP = 'DELETE' AND p.can_delete)
    )
);
```

### Cross-Table RLS

```sql
-- Политика с проверкой связанных таблиц
CREATE POLICY cross_table_rls ON orders
FOR ALL
TO app_user
USING (
    EXISTS (
        SELECT 1 FROM customers c
        WHERE c.id = orders.customer_id
        AND c.tenant_id = current_setting('app.tenant_id')::INTEGER
        AND c.status = 'active'
    )
);
```

## Encryption Best Practices

### Key Management

```sql
-- Создать таблицу для хранения ключей
CREATE TABLE encryption_keys (
    id SERIAL PRIMARY KEY,
    key_name TEXT UNIQUE NOT NULL,
    key_value TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    expires_at TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT true
);

-- Функция для получения ключа
CREATE OR REPLACE FUNCTION get_encryption_key(p_key_name TEXT)
RETURNS TEXT AS $$
DECLARE
    key_value TEXT;
BEGIN
    SELECT key_value INTO key_value
    FROM encryption_keys
    WHERE key_name = p_key_name
    AND is_active = true
    AND (expires_at IS NULL OR expires_at > NOW());

    IF key_value IS NULL THEN
        RAISE EXCEPTION 'Encryption key not found or expired';
    END IF;

    RETURN key_value;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

### Encrypted Column with Automatic Key Rotation

```sql
-- Функция для шифрования с ротацией ключей
CREATE OR REPLACE FUNCTION encrypt_with_rotation(
    data TEXT,
    key_name TEXT DEFAULT 'default_key'
)
RETURNS BYTEA AS $$
DECLARE
    key_value TEXT;
BEGIN
    key_value := get_encryption_key(key_name);
    RETURN encrypt(data::bytea, key_value, 'aes');
END;
$$ LANGUAGE plpgsql;
```

## Security Testing

### Penetration Testing Queries

```sql
-- Проверка на SQL injection уязвимости
-- (Только для тестирования!)

-- Проверка на возможность выполнения системных команд
SELECT * FROM pg_stat_activity WHERE query LIKE '%pg_read_file%';

-- Проверка доступа к системным каталогам
SELECT * FROM pg_tables WHERE schemaname = 'pg_catalog';

-- Проверка прав на опасные функции
SELECT
    p.proname,
    p.proacl
FROM pg_proc p
WHERE p.proname IN ('pg_read_file', 'pg_ls_dir', 'pg_stat_file');
```

### Security Audit Queries

```sql
-- Аудит прав доступа
SELECT
    grantee,
    table_schema,
    table_name,
    privilege_type
FROM information_schema.table_privileges
WHERE grantee NOT IN ('postgres', 'PUBLIC')
ORDER BY grantee, table_schema, table_name;

-- Аудит ролей
SELECT
    rolname,
    rolsuper,
    rolcreaterole,
    rolcreatedb,
    rolcanlogin,
    rolpassword IS NOT NULL AS has_password
FROM pg_roles
ORDER BY rolname;

-- Аудит RLS политик
SELECT
    schemaname,
    tablename,
    policyname,
    permissive,
    roles,
    cmd,
    qual,
    with_check
FROM pg_policies
ORDER BY schemaname, tablename, policyname;
```

## Compliance Frameworks

### PCI DSS Compliance

```sql
-- Функция для маскирования данных карт
CREATE OR REPLACE FUNCTION mask_card_number(card_number TEXT)
RETURNS TEXT AS $$
BEGIN
    IF length(card_number) < 4 THEN
        RETURN '';
    END IF;

    RETURN '---' || right(card_number, 4);
END;
$$ LANGUAGE plpgsql;

-- Политика для доступа к данным карт
CREATE POLICY pci_data_access ON payment_cards
FOR SELECT
TO app_user
USING (
    current_setting('app.user_role') = 'payment_processor'
    AND current_setting('app.pci_compliant') = 'true'
);
```

### HIPAA Compliance

```sql
-- Функция для логирования доступа к медицинским данным
CREATE OR REPLACE FUNCTION hipaa_access_log()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO hipaa_audit_log (
        user_id,
        patient_id,
        access_type,
        accessed_at,
        ip_address
    ) VALUES (
        current_setting('app.user_id')::INTEGER,
        NEW.patient_id,
        TG_OP,
        NOW(),
        inet_client_addr()
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Применить к таблице с медицинскими данными
CREATE TRIGGER hipaa_audit_trigger
AFTER SELECT OR INSERT OR UPDATE OR DELETE ON patient_records
FOR EACH ROW EXECUTE FUNCTION hipaa_access_log();
```

## Security Checklist

### Initial Setup

- [ ] Изменить пароль суперпользователя
- [ ] Создать отдельные роли для приложений
- [ ] Настроить **SSL**/**TLS**
- [ ] Настроить **firewall**
- [ ] Включить логирование
- [ ] Настроить **RLS** для чувствительных таблиц
- [ ] Отключить небезопасные функции
- [ ] Настроить резервное копирование

### Regular Maintenance

- [ ] Проверять права доступа ежемесячно
- [ ] Ротировать пароли каждые 90 дней
- [ ] Обновлять **SSL** сертификаты
- [ ] Проверять логи на подозрительную активность
- [ ] Обновлять **PostgreSQL** и расширения
- [ ] Проводить **security** аудит
- [ ] Тестировать процедуры восстановления
- [ ] Обновлять документацию

### Incident Response

- [ ] Иметь план реагирования на инциденты
- [ ] Настроить мониторинг и алерты
- [ ] Документировать все инциденты
- [ ] Регулярно тестировать процедуры
- [ ] Обучать команду процедурам


- [PostgreSQL Security](https://www.postgresql.org/docs/)
- [Row Level Security](https://www.postgresql.org/docs/)
- [SSL/TLS](https://www.postgresql.org/docs/current/ssl-tcp.html)
- [pgAudit](https://www.postgresql.org/docs/)

