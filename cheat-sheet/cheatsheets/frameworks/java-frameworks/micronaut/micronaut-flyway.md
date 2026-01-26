---
title: "Micronaut: Flyway - Database Migrations"
description: "Полное руководство по Flyway в Micronaut: database migrations, versioning, rollback и best practices"
tags: ["micronaut", "flyway", "migrations", "database", "versioning", "java", "kotlin"]
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-data.md"]
next: ["micronaut-data.md", "micronaut-testing.md"]
updated: "2025-01-16"
related: ["micronaut-data.md", "micronaut-testing.md"]
---

# Micronaut: Flyway - Database Migrations

## Введение

Micronaut предоставляет отличную поддержку Flyway для управления миграциями базы данных. Это позволяет версионировать схему БД и автоматически применять изменения.

### Основные возможности

- **Database Migrations**: Версионирование схемы БД
- **Automatic Migration**: Автоматическое применение миграций
- **Version Control**: Контроль версий миграций
- **Rollback Support**: Поддержка отката миграций
- **Multiple Databases**: Поддержка различных БД

## Настройка Flyway

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.flyway:micronaut-flyway")
}
```

### Конфигурация

**application.yml:**

```yaml
flyway:
  enabled: true
  locations: classpath:db/migration
  baseline-on-migrate: true
```

## Migration Files

### SQL Migrations

**db/migration/V1__create_users.sql:**

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    age INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**db/migration/V2__add_indexes.sql:**

```sql
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_age ON users(age);
```

### Java Migrations

```java
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import java.sql.Statement;

public class V3__add_columns extends BaseJavaMigration {
    
    @Override
    public void migrate(Context context) throws Exception {
        try (Statement statement = context.getConnection().createStatement()) {
            statement.execute("ALTER TABLE users ADD COLUMN phone VARCHAR(20)");
        }
    }
}
```

## Migration Naming

### Naming Convention

```
V{version}__{description}.sql
```

Примеры:
- `V1__create_users.sql`
- `V2__add_indexes.sql`
- `V3__add_phone_column.sql`

## Best Practices

### 1. Используйте правильные имена миграций

```sql
-- ✅ Хорошо
V1__create_users.sql
V2__add_indexes.sql
```

### 2. Не изменяйте уже примененные миграции

```sql
-- ❌ Плохо - не изменяйте V1 после применения
-- ✅ Хорошо - создайте новую миграцию V2
```

### 3. Тестируйте миграции на тестовой БД

```bash
# ✅ Хорошо
./gradlew flywayMigrate -Pflyway.url=jdbc:postgresql://localhost:5432/testdb
```

## Flyway Callbacks

### Callback Hooks

```java
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import jakarta.inject.Singleton;

@Singleton
public class FlywayCallback implements Callback {
    
    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_MIGRATE;
    }
    
    @Override
    public void handle(Event event, Context context) {
        if (event == Event.AFTER_MIGRATE) {
            log.info("Migration completed successfully");
        }
    }
}
```

## Multiple Databases

### Multiple DataSource Configuration

**application.yml:**

```yaml
datasources:
  default:
    url: jdbc:postgresql://localhost:5432/db1
  secondary:
    url: jdbc:postgresql://localhost:5432/db2

flyway:
  datasources:
    default:
      enabled: true
      locations: classpath:db/migration/default
    secondary:
      enabled: true
      locations: classpath:db/migration/secondary
```

## Flyway Baseline

### Baseline Configuration

**application.yml:**

```yaml
flyway:
  baseline-on-migrate: true
  baseline-version: 0
  baseline-description: "Initial baseline"
```

### Baseline Migration

```bash
# Создание baseline для существующей БД
./gradlew flywayBaseline
```

## Flyway Clean

### Clean Operation

```bash
# Очистка схемы БД (осторожно!)
./gradlew flywayClean
```

**application.yml:**

```yaml
flyway:
  clean-disabled: false  # По умолчанию true для безопасности
```

## Flyway Validate

### Validation

```bash
# Проверка миграций
./gradlew flywayValidate
```

### Validation Configuration

**application.yml:**

```yaml
flyway:
  validate-on-migrate: true
  validate-migration-naming: true
```

## Flyway Info

### Migration Info

```bash
# Информация о миграциях
./gradlew flywayInfo
```

### Info Service

```java
import org.flywaydb.core.Flyway;
import jakarta.inject.Singleton;

@Singleton
public class FlywayInfoService {
    private final Flyway flyway;
    
    public FlywayInfoService(Flyway flyway) {
        this.flyway = flyway;
    }
    
    public MigrationInfo[] getMigrationInfo() {
        return flyway.info().all();
    }
    
    public MigrationInfo[] getPendingMigrations() {
        return flyway.info().pending();
    }
}
```

## Заключение

Micronaut Flyway предоставляет мощные инструменты для управления миграциями базы данных. Поддержка SQL и Java миграций, версионирования, автоматического применения, rollback, callbacks, multiple databases, baseline, clean, validate, info и других продвинутых возможностей позволяет эффективно управлять схемой БД.

## Дополнительные ресурсы

- [Micronaut Flyway Documentation](https://micronaut-projects.github.io/micronaut-flyway/latest/guide/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Flyway Best Practices](https://flywaydb.org/documentation/usage/best-practices/)
- [Flyway Commands](https://flywaydb.org/documentation/usage/commandline/)

