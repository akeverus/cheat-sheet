---
title: "HikariCP: Высокопроизводительный Connection Pool"
description: "Комплексное руководство по использованию HikariCP — самого быстрого и надежного JDBC connection pool для Java приложений."
tags:
  - libraries
  - java
  - java-hikaricp
difficulty: "intermediate"
prerequisites:
  - java-lombok

next: []

updated: "2026-04-20"
---
# HikariCP: Высокопроизводительный Connection Pool

**Комплексное руководство по использованию `HikariCP` — самого быстрого и надежного `JDBC` connection pool для `Java` приложений.**

## Полезные ссылки

### Официальная документация
- [HikariCP GitHub](https://github.com/brettwooldridge/HikariCP) — репозиторий **HikariCP**
- [HikariCP Wiki](https://github.com/brettwooldridge/HikariCP/wiki) — подробная документация
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby) — настройки

### Интеграция
- [Spring Boot HikariCP](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.datasource) — **Spring Boot** интеграция
- [HikariCP Maven](https://mvnrepository.com/artifact/com.zaxxer/HikariCP) — **Maven** зависимости
- [HikariCP Performance](https://github.com/brettwooldridge/HikariCP/wiki/Down-the-Rabbit-Hole) — производительность

## Содержание

- [Введение в HikariCP](#введение-в-hikaricp)
  - [Почему HikariCP?](#почему-hikaricp)
  - [Как работает HikariCP?](#как-работает-hikaricp)
  - [Преимущества и недостатки](#преимущества-и-недостатки)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Базовая настройка](#базовая-настройка)
- [Базовая конфигурация](#базовая-конфигурация)
  - [Основные параметры](#основные-параметры)
  - [Настройки для разных СУБД](#настройки-для-разных-субд)
- [Расширенные настройки](#расширенные-настройки)
  - [Connection testing](#connection-testing)
  - [Timeout настройки](#timeout-настройки)
  - [Pool sizing](#pool-sizing)
- [Мониторинг и метрики](#мониторинг-и-метрики)
  - [Базовый мониторинг](#базовый-мониторинг)
  - [Интеграция с Micrometer](#интеграция-с-micrometer)
  - [Pool статистика](#pool-статистика)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Автоматическая конфигурация](#автоматическая-конфигурация)
  - [Кастомная конфигурация](#кастомная-конфигурация)
  - [Множественные DataSource](#множественные-datasource)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Оптимальные настройки](#оптимальные-настройки)
  - [Connection pooling стратегии](#connection-pooling-стратегии)
- [Устранение неисправностей](#устранение-неисправностей)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [Recovery стратегии](#recovery-стратегии)
- [Best practices](#best-practices)
  - [1. Правильный размер пула](#1-правильный-размер-пула)
  - [2. Настройки таймаутов](#2-настройки-таймаутов)
  - [3. Мониторинг и алертинг](#3-мониторинг-и-алертинг)
  - [4. Обработка ошибок и восстановление](#4-обработка-ошибок-и-восстановление)
  - [5. Тестирование](#5-тестирование)
- [Заключение](#заключение)
  - [Преимущества HikariCP](#преимущества-hikaricp)
  - [Основные паттерны использования](#основные-паттерны-использования)
  - [Когда использовать HikariCP](#когда-использовать-hikaricp)
  - [Сравнение с альтернативами](#сравнение-с-альтернативами)
- [См. также](#см-также)

## Введение в HikariCP

**HikariCP** — это высокопроизводительный **JDBC connection pool** для **Java**. Он является самым быстрым и надежным **connection pool**, используемым по умолчанию в **Spring Boot**.

### Почему HikariCP?

**HikariCP** предлагает множество преимуществ:**

1. **Высокая производительность** — Самый быстрый **JDBC connection pool**
2. **Надежность** — Минимальное количество багов и проблем
3. **Простота использования** — Минимальная конфигурация
4. **Низкое потребление памяти** — Оптимизированное использование ресурсов
5. **Автоматическое восстановление** — Восстановление после сбоев БД
6. **Современная архитектура** — Использование современных **Java features**
7. **Широкая поддержка** — Поддержка всех основных СУБД
8. **Активное развитие** — Регулярные обновления и улучшения

### Как работает HikariCP?

**HikariCP** использует несколько оптимизаций для достижения высокой производительности:**

- **FastList** — Оптимизированная структура данных для управления соединениями
- **ConcurrentBag** — **Thread-safe** коллекция для конкурентного доступа
- **Нет reflection** — Прямые вызовы методов для максимальной скорости
- **Connection proxy** — Легковесные прокси объекты
- **Automatic eviction** — Автоматическое удаление проблемных соединений

### Преимущества и недостатки

**Преимущества:**
- Быстрый старт и работа
- Низкое потребление **CPU** и памяти
- Отличная стабильность
- Простая конфигурация
- Хорошая документация

**Недостатки:**
- Ограниченные возможности кастомизации
- Меньше метрик по сравнению с другими пулами
- Не подходит для специфических требований

## Установка и настройка

### Maven

Зависимость **Maven** для **HikariCP** (JDBC connection pool).

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

### Gradle

```kotlin
dependencies {
    implementation("com.zaxxer:HikariCP:5.0.1")
}
```

### Базовая настройка

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class HikariConfiguration {

    public static DataSource createDataSource() {
        HikariConfig config = new HikariConfig();

        // Базовые настройки подключения
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/myapp");
        config.setUsername("myuser");
        config.setPassword("mypassword");
        config.setDriverClassName("org.postgresql.Driver");

        // Настройки пула
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000); // 30 секунд
        config.setIdleTimeout(600000);     // 10 минут
        config.setMaxLifetime(1800000);    // 30 минут

        return new HikariDataSource(config);
    }

    // Использование
    public static void main(String[] args) {
        DataSource dataSource = createDataSource();

        try (Connection conn = dataSource.getConnection()) {
            // Работа с соединением
            System.out.println("Connected to database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

## Базовая конфигурация

### Основные параметры

```java
public class BasicConfiguration {

    public static HikariDataSource createConfiguredDataSource() {
        HikariConfig config = new HikariConfig();

        // === ПОДКЛЮЧЕНИЕ К БД ===
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/myapp");
        config.setUsername("myuser");
        config.setPassword("mypassword");

        // Явное указание драйвера (опционально для новых версий)
        config.setDriverClassName("org.postgresql.Driver");

        // === НАСТРОЙКИ ПУЛА ===
        // Максимальное количество соединений в пуле
        config.setMaximumPoolSize(20);

        // Минимальное количество idle соединений
        config.setMinimumIdle(5);

        // Время ожидания соединения (миллисекунды)
        config.setConnectionTimeout(30000); // 30 сек

        // Время жизни idle соединения (миллисекунды)
        config.setIdleTimeout(600000); // 10 мин

        // Максимальное время жизни соединения (миллисекунды)
        config.setMaxLifetime(1800000); // 30 мин

        // === ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ ===
        // Название пула (для логирования)
        config.setPoolName("MyAppPool");

        // Автоматическое закрытие утечек соединений
        config.setLeakDetectionThreshold(60000); // 1 мин

        return new HikariDataSource(config);
    }
}
```

### Настройки для разных СУБД

```java
/
 * Конфигурации HikariCP для различных СУБД
 * Каждая СУБД имеет свои особенности и оптимальные настройки пула соединений
 */
public class DatabaseConfigurations {

    /
     * Создание HikariDataSource для PostgreSQL
     * PostgreSQL требует специальных настроек SSL и оптимизаций пула
     * @return настроенный HikariDataSource для PostgreSQL
     */
    public static HikariDataSource createPostgreSQLDataSource() {
        // Создаем конфигурацию HikariCP
        HikariConfig config = new HikariConfig();

        // Базовые настройки подключения к PostgreSQL
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/myapp");  // URL подключения к БД
        config.setUsername("user");      // Имя пользователя для подключения
        config.setPassword("password");  // Пароль для подключения

        // PostgreSQL специфические настройки SSL
        // Включаем SSL для безопасного подключения
        config.addDataSourceProperty("ssl", "true");
        // Используем фабрику SSL которая не проверяет сертификаты (для разработки)
        config.addDataSourceProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");

        // Оптимизации размера пула для PostgreSQL
        config.setMaximumPoolSize(20);  // Максимальное количество соединений в пуле
        config.setMinimumIdle(5);       // Минимальное количество idle соединений

        // Создаем и возвращаем DataSource с настроенной конфигурацией
        return new HikariDataSource(config);
    }

    /
     * Создание HikariDataSource для MySQL
     * MySQL требует включения кэширования prepared statements для производительности
     * @return настроенный HikariDataSource для MySQL
     */
    public static HikariDataSource createMySQLDataSource() {
        // Создаем конфигурацию HikariCP
        HikariConfig config = new HikariConfig();

        // Базовые настройки подключения к MySQL
        config.setJdbcUrl("jdbc:mysql://localhost:3306/myapp");  // URL подключения к MySQL БД
        config.setUsername("user");      // Имя пользователя для подключения
        config.setPassword("password");  // Пароль для подключения

        // MySQL специфические настройки для оптимизации производительности
        // Включаем кэширование prepared statements на стороне клиента
        config.addDataSourceProperty("cachePrepStmts", "true");        // Кэшировать prepared statements
        config.addDataSourceProperty("prepStmtCacheSize", "250");      // Размер кэша prepared statements
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");  // Максимальная длина SQL для кэширования
        config.addDataSourceProperty("useServerPrepStmts", "true");     // Использовать prepared statements на сервере

        // Создаем и возвращаем DataSource с настроенной конфигурацией
        return new HikariDataSource(config);
    }

    /
     * Создание HikariDataSource для Oracle
     * Oracle требует меньшего размера пула и отключения auto-commit для производительности
     * @return настроенный HikariDataSource для Oracle
     */
    public static HikariDataSource createOracleDataSource() {
        // Создаем конфигурацию HikariCP
        HikariConfig config = new HikariConfig();

        // Базовые настройки подключения к Oracle
        config.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe");  // URL подключения к Oracle БД (thin driver)
        config.setUsername("user");      // Имя пользователя для подключения
        config.setPassword("password");  // Пароль для подключения

        // Oracle специфические настройки
        // Oracle не любит большое количество соединений - используем меньший пул
        config.setMaximumPoolSize(10);  // Максимальное количество соединений (меньше чем для PostgreSQL/MySQL)
        config.setMinimumIdle(2);       // Минимальное количество idle соединений

        // Отключение auto-commit для лучшей производительности
        // В Oracle явное управление транзакциями более эффективно
        config.setAutoCommit(false);

        // Создаем и возвращаем DataSource с настроенной конфигурацией
        return new HikariDataSource(config);
    }
}
```

## Расширенные настройки

### Connection testing

```java
public class ConnectionTestingConfiguration {

    public static HikariDataSource createWithConnectionTesting() {
        HikariConfig config = new HikariConfig();

        // === CONNECTION TESTING ===
        // Запрос для проверки соединения
        config.setConnectionTestQuery("SELECT 1");

        // Автоматическая проверка соединений
        config.setValidationTimeout(5000); // 5 сек

        // Проверка при получении из пула
        config.setConnectionTimeout(30000);

        return new HikariDataSource(config);
    }

    // Кастомная проверка соединения
    public static HikariDataSource createWithCustomHealthCheck() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://localhost:5432/myapp");
        config.setUsername("user");
        config.setPassword("password");

        // Отключение стандартной проверки
        config.setConnectionTestQuery(null);

        // Кастомная логика проверки
        config.setHealthCheckRegistry(new CustomHealthCheckRegistry());

        return new HikariDataSource(config);
    }
}
```

### Timeout настройки

```java
public class TimeoutConfiguration {

    public static HikariDataSource createWithTimeouts() {
        HikariConfig config = new HikariConfig();

        // === TIMEOUT НАСТРОЙКИ ===

        // Время ожидания получения соединения из пула (миллисекунды)
        // Если все соединения заняты, запрос будет ждать
        config.setConnectionTimeout(30000); // 30 сек

        // Максимальное время жизни соединения (миллисекунды)
        // Соединение будет закрыто и пересоздано после этого времени
        config.setMaxLifetime(1800000); // 30 мин

        // Время жизни idle соединения (миллисекунды)
        // Idle соединение будет закрыто после этого времени
        config.setIdleTimeout(600000); // 10 мин

        // Время ожидания валидации соединения (миллисекунды)
        config.setValidationTimeout(5000); // 5 сек

        // Время обнаружения утечек соединений (миллисекунды)
        // Логирование предупреждения если соединение не закрыто вовремя
        config.setLeakDetectionThreshold(60000); // 1 мин

        // Таймаут для инициализации пула (миллисекунды)
        config.setInitializationFailTimeout(60000); // 1 мин

        return new HikariDataSource(config);
    }
}
```

### Pool sizing

```java
public class PoolSizingConfiguration {

    public static HikariDataSource createOptimalPool() {
        HikariConfig config = new HikariConfig();

        // === ОПТИМАЛЬНЫЙ РАЗМЕР ПУЛА ===

        // Формула: connections = ((core_count * 2) + effective_spindle_count)
        // Для сервера с 4 ядрами и SSD: (4 * 2) + 1 = 9
        int optimalPoolSize = calculateOptimalPoolSize();
        config.setMaximumPoolSize(optimalPoolSize);

        // Минимальный размер пула
        // Рекомендуется: maximumPoolSize / 2 или меньше
        config.setMinimumIdle(Math.max(2, optimalPoolSize / 2));

        // Максимальное количество соединений на одну операцию
        // Обычно равно maximumPoolSize
        config.setMaximumPoolSize(optimalPoolSize);

        return new HikariDataSource(config);
    }

    private static int calculateOptimalPoolSize() {
        // Получение количества ядер CPU
        int cores = Runtime.getRuntime().availableProcessors();

        // Для БД на том же сервере: cores * 2
        // Для удаленной БД: (cores * 2) + 1
        // Для высокой нагрузки: cores * 4

        boolean isRemoteDatabase = true; // Зависит от конфигурации

        if (isRemoteDatabase) {
            return (cores * 2) + 1;
        } else {
            return cores * 2;
        }
    }

    // Адаптивный размер пула
    public static HikariDataSource createAdaptivePool() {
        HikariConfig config = new HikariConfig();

        // Начать с консервативного размера
        int initialPoolSize = Math.max(2, Runtime.getRuntime().availableProcessors());

        config.setMaximumPoolSize(initialPoolSize * 2); // Резерв для пиковых нагрузок
        config.setMinimumIdle(initialPoolSize);

        // Метрики для мониторинга и адаптации
        config.setMetricRegistry(createMetricsRegistry());

        return new HikariDataSource(config);
    }

    private static MetricRegistry createMetricsRegistry() {
        // Интеграция с Micrometer или Dropwizard Metrics
        return new MetricRegistry();
    }
}
```

## Мониторинг и метрики

### Базовый мониторинг

```java
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.MetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;

public class MonitoringConfiguration {

    public static HikariDataSource createMonitoredDataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://localhost:5432/myapp");
        config.setUsername("user");
        config.setPassword("password");

        // Включение метрик
        config.setMetricsTrackerFactory(new CustomMetricsTrackerFactory());

        return new HikariDataSource(config);
    }
}

class CustomMetricsTrackerFactory implements MetricsTrackerFactory {

    @Override
    public MetricsTracker create(String poolName, PoolStats poolStats) {
        return new CustomMetricsTracker(poolName, poolStats);
    }
}

class CustomMetricsTracker implements MetricsTracker {

    private final String poolName;

    public CustomMetricsTracker(String poolName, PoolStats poolStats) {
        this.poolName = poolName;
    }

    @Override
    public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
        System.out.println("[" + poolName + "] Connection created in " + connectionCreatedMillis + "ms");
    }

    @Override
    public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
        long millis = elapsedAcquiredNanos / 1_000_000;
        if (millis > 1000) { // Логировать только медленные операции
            System.out.println("[" + poolName + "] Connection acquired in " + millis + "ms");
        }
    }

    @Override
    public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
        System.out.println("[" + poolName + "] Connection used for " + elapsedBorrowedMillis + "ms");
    }

    @Override
    public void recordConnectionTimeout() {
        System.err.println("[" + poolName + "] Connection timeout occurred!");
    }
}
```

### Интеграция с Micrometer

```java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jdbc.HikariDataSourceMetrics;

@Configuration
public class MetricsConfiguration {

    @Bean
    public HikariDataSource hikariDataSource(MeterRegistry meterRegistry) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://localhost:5432/myapp");
        config.setUsername("user");
        config.setPassword("password");
        config.setPoolName("MyAppPool");

        HikariDataSource dataSource = new HikariDataSource(config);

        // Интеграция с Micrometer
        HikariDataSourceMetrics.monitor(meterRegistry, dataSource, "db.pool");

        return dataSource;
    }
}
```

### Pool статистика

```java
@Service
public class PoolMonitoringService {

    private final HikariDataSource dataSource;

    public PoolMonitoringService(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PoolStats getPoolStats() {
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

        return new PoolStats(
            poolMXBean.getTotalConnections(),
            poolMXBean.getActiveConnections(),
            poolMXBean.getIdleConnections(),
            poolMXBean.getThreadsAwaitingConnection(),
            poolMXBean.getConnectionsCreated(),
            poolMXBean.getConnectionsDestroyed()
        );
    }

    public void logPoolStatus() {
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

        System.out.println("=== HikariCP Pool Status ===");
        System.out.println("Total connections: " + poolMXBean.getTotalConnections());
        System.out.println("Active connections: " + poolMXBean.getActiveConnections());
        System.out.println("Idle connections: " + poolMXBean.getIdleConnections());
        System.out.println("Waiting threads: " + poolMXBean.getThreadsAwaitingConnection());
        System.out.println("Connections created: " + poolMXBean.getConnectionsCreated());
        System.out.println("Connections destroyed: " + poolMXBean.getConnectionsDestroyed());

        // Проверка на проблемы
        if (poolMXBean.getThreadsAwaitingConnection() > 0) {
            System.out.println("WARNING: Threads are waiting for connections!");
        }

        if (poolMXBean.getActiveConnections() == poolMXBean.getTotalConnections()) {
            System.out.println("WARNING: All connections are active!");
        }
    }

    // Планировщик для периодического мониторинга
    @Scheduled(fixedRate = 300000) // Каждые 5 минут
    public void scheduledPoolMonitoring() {
        logPoolStatus();
    }

    static class PoolStats {
        final int total;
        final int active;
        final int idle;
        final int waiting;
        final long created;
        final long destroyed;

        PoolStats(int total, int active, int idle, int waiting, long created, long destroyed) {
            this.total = total;
            this.active = active;
            this.idle = idle;
            this.waiting = waiting;
            this.created = created;
            this.destroyed = destroyed;
        }
    }
}
```

## Интеграция с Spring Boot

### Автоматическая конфигурация

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: myuser
    password: mypassword
    driver-class-name: org.postgresql.Driver

    # HikariCP настройки (Spring Boot автоматически использует HikariCP)
    hikari:
      pool-name: MyAppPool
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000

      # Дополнительные свойства
      data-source-properties:
        ssl: true
        sslfactory: org.postgresql.ssl.NonValidatingFactory
```

### Кастомная конфигурация

```java
@Configuration
public class DataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public DataSource dataSource(HikariConfig hikariConfig, MeterRegistry meterRegistry) {
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        // Кастомные настройки
        dataSource.setPoolName("MyAppPool");
        dataSource.setRegisterMbeans(true); // JMX мониторинг

        // Метрики
        HikariDataSourceMetrics.monitor(meterRegistry, dataSource, "db.pool");

        return dataSource;
    }

    // Репозиторий для работы с соединениями
    @Bean
    public ConnectionRepository connectionRepository(DataSource dataSource) {
        return new ConnectionRepository(dataSource);
    }
}

@Repository
public class ConnectionRepository {

    private final DataSource dataSource;

    public ConnectionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void executeQuery(String sql) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Result: " + rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query execution failed", e);
        }
    }

    public <T> T executeInTransaction(Function<Connection, T> operation) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                T result = operation.apply(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Transaction failed", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Connection failed", e);
        }
    }
}
```

### Множественные DataSource

```java
@Configuration
public class MultiDataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

@Service
public class MultiDataSourceService {

    private final JdbcTemplate primaryJdbc;
    private final JdbcTemplate secondaryJdbc;

    public MultiDataSourceService(
            @Qualifier("primaryJdbcTemplate") JdbcTemplate primaryJdbc,
            @Qualifier("secondaryJdbcTemplate") JdbcTemplate secondaryJdbc) {
        this.primaryJdbc = primaryJdbc;
        this.secondaryJdbc = secondaryJdbc;
    }

    public void readFromPrimary() {
        List<Map<String, Object>> results = primaryJdbc.queryForList("SELECT * FROM users");
        // Обработка результатов из основной БД
    }

    public void readFromSecondary() {
        List<Map<String, Object>> results = secondaryJdbc.queryForList("SELECT * FROM reports");
        // Обработка результатов из вторичной БД
    }
}
```

## Оптимизация производительности

### Оптимальные настройки

```java
@Configuration
public class OptimizedHikariConfiguration {

    @Bean
    public DataSource optimizedDataSource() {
        HikariConfig config = new HikariConfig();

        // === ПРОИЗВОДИТЕЛЬНЫЕ НАСТРОЙКИ ===

        // Размер пула
        int poolSize = calculateOptimalPoolSize();
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(Math.max(2, poolSize / 2));

        // Таймауты
        config.setConnectionTimeout(30000);     // 30 сек
        config.setIdleTimeout(600000);          // 10 мин
        config.setMaxLifetime(1800000);         // 30 мин
        config.setValidationTimeout(5000);      // 5 сек

        // Подготовленные запросы
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        // Другие оптимизации
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        // Мониторинг
        config.setLeakDetectionThreshold(60000);

        return new HikariDataSource(config);
    }

    private int calculateOptimalPoolSize() {
        int cores = Runtime.getRuntime().availableProcessors();

        // Для CPU-bound операций: cores * 2
        // Для IO-bound операций: cores * 4
        // Для смешанных: cores * 3

        return cores * 3;
    }
}
```

### Connection pooling стратегии

```java
public class PoolingStrategies {

    // Стратегия для read-heavy приложений
    public static HikariDataSource createReadHeavyPool() {
        HikariConfig config = new HikariConfig();

        // Больше соединений для чтения
        int cores = Runtime.getRuntime().availableProcessors();
        config.setMaximumPoolSize(cores * 4);
        config.setMinimumIdle(cores * 2);

        // Короткие таймауты
        config.setConnectionTimeout(10000); // 10 сек
        config.setIdleTimeout(300000);      // 5 мин

        return new HikariDataSource(config);
    }

    // Стратегия для write-heavy приложений
    public static HikariDataSource createWriteHeavyPool() {
        HikariConfig config = new HikariConfig();

        // Меньше соединений для записи (избегать конфликтов)
        int cores = Runtime.getRuntime().availableProcessors();
        config.setMaximumPoolSize(cores * 2);
        config.setMinimumIdle(cores);

        // Длинные таймауты для транзакций
        config.setConnectionTimeout(60000); // 1 мин
        config.setIdleTimeout(600000);      // 10 мин
        config.setMaxLifetime(3600000);     // 1 час

        // Отключение auto-commit для транзакций
        config.setAutoCommit(false);

        return new HikariDataSource(config);
    }

    // Стратегия для batch операций
    public static HikariDataSource createBatchPool() {
        HikariConfig config = new HikariConfig();

        // Специфические настройки для batch
        config.setMaximumPoolSize(5); // Ограниченное количество для batch
        config.setMinimumIdle(2);

        // Длинные таймауты
        config.setConnectionTimeout(120000); // 2 мин
        config.setIdleTimeout(1800000);      // 30 мин

        // Batch специфические свойства
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(config);
    }
}
```

## Устранение неисправностей

### Распространенные проблемы

```java
@Service
@Slf4j
public class TroubleshootingService {

    private final HikariDataSource dataSource;

    public TroubleshootingService(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Диагностика проблем с пулом
    public void diagnosePoolIssues() {
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

        log.info("=== Pool Diagnostics ===");
        log.info("Total connections: {}", poolMXBean.getTotalConnections());
        log.info("Active connections: {}", poolMXBean.getActiveConnections());
        log.info("Idle connections: {}", poolMXBean.getIdleConnections());
        log.info("Waiting threads: {}", poolMXBean.getThreadsAwaitingConnection());

        // Проверка на проблемы
        if (poolMXBean.getThreadsAwaitingConnection() > 0) {
            log.warn("Threads are waiting for connections! Consider increasing maximumPoolSize");
        }

        if (poolMXBean.getActiveConnections() == poolMXBean.getTotalConnections()) {
            log.warn("All connections are active! Pool may be exhausted");
        }

        long totalConnections = poolMXBean.getConnectionsCreated();
        long destroyedConnections = poolMXBean.getConnectionsDestroyed();

        if (destroyedConnections > totalConnections * 0.1) {
            log.warn("High connection churn detected. Connections are being destroyed frequently");
        }
    }

    // Мониторинг утечек соединений
    @Scheduled(fixedRate = 60000) // Каждую минуту
    public void monitorConnectionLeaks() {
        // HikariCP автоматически обнаруживает утечки
        // Здесь мы можем добавить дополнительную логику
        log.debug("Connection leak monitoring active");
    }

    // Принудительное обновление пула
    public void refreshPool() {
        log.info("Refreshing connection pool...");
        dataSource.getHikariPoolMXBean().softEvictConnections();
        log.info("Pool refreshed");
    }

    // Обработка таймаутов соединений
    public void handleConnectionTimeout(SQLTimeoutException e) {
        log.error("Connection timeout occurred", e);
        diagnosePoolIssues();

        // Возможные действия:
        // 1. Увеличить connectionTimeout
        // 2. Увеличить maximumPoolSize
        // 3. Проверить состояние БД
        // 4. Проверить сетевые проблемы
    }

    // Обработка ошибок подключения
    public void handleConnectionError(SQLException e) {
        log.error("Connection error occurred", e);

        if (e.getSQLState() != null) {
            switch (e.getSQLState()) {
                case "08003": // Connection does not exist
                case "08006": // Connection failure
                    log.error("Database connection lost. Attempting to reconnect...");
                    refreshPool();
                    break;
                case "53300": // Too many connections
                    log.error("Too many connections to database");
                    break;
                default:
                    log.error("Unknown connection error: {}", e.getSQLState());
            }
        }
    }
}
```

### Recovery стратегии

```java
@Configuration
public class RecoveryConfiguration {

    @Bean
    public DataSource resilientDataSource() {
        HikariConfig config = new HikariConfig();

        // === НАСТРОЙКИ ВОССТАНОВЛЕНИЯ ===

        // Автоматическое восстановление после сбоев
        config.setInitializationFailTimeout(60000); // 1 мин на инициализацию

        // Проверка соединений
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);

        // Быстрое обнаружение проблем
        config.setLeakDetectionThreshold(30000); // 30 сек

        // Обработка исключений
        config.setExceptionOverride(new CustomSQLExceptionOverride());

        return new HikariDataSource(config);
    }
}

class CustomSQLExceptionOverride implements SQLExceptionOverride {

    @Override
    public OverrideResult override(SQLException sqlException, HikariConfig config) {
        String sqlState = sqlException.getSQLState();

        if (sqlState != null) {
            // Временные ошибки - повторить
            if (sqlState.startsWith("08") || // Connection errors
                sqlState.equals("53300")) {  // Too many connections
                return OverrideResult.RETRY;
            }

            // Фатальные ошибки - не повторять
            if (sqlState.startsWith("42") || // Syntax errors
                sqlState.startsWith("23")) { // Integrity constraint violations
                return OverrideResult.DO_NOT_RETRY;
            }
        }

        // По умолчанию - не повторять
        return OverrideResult.DO_NOT_RETRY;
    }
}
```

## Best practices

### 1. Правильный размер пула

```java
// ✅ Хорошо - расчет размера пула
@Configuration
public class PoolSizingBestPractices {

    @Bean
    public DataSource properlySizedDataSource() {
        HikariConfig config = new HikariConfig();

        // Расчет оптимального размера пула
        int optimalSize = calculateOptimalPoolSize();
        config.setMaximumPoolSize(optimalSize);
        config.setMinimumIdle(Math.max(2, optimalSize / 2));

        return new HikariDataSource(config);
    }

    private int calculateOptimalPoolSize() {
        int cores = Runtime.getRuntime().availableProcessors();
        boolean isRemoteDatabase = true; // Зависит от архитектуры
        boolean isHighLoad = false;      // Зависит от нагрузки

        if (isRemoteDatabase && isHighLoad) {
            return cores * 4;
        } else if (isRemoteDatabase) {
            return (cores * 2) + 1;
        } else {
            return cores * 2;
        }
    }

    // ❌ Плохо - фиксированный размер
    @Bean
    public DataSource badPoolSizing() {
        HikariConfig config = new HikariConfig();

        // Не учитывает характеристики системы
        config.setMaximumPoolSize(50); // Слишком много для маленького сервера
        config.setMinimumIdle(10);     // Слишком мало для высокой нагрузки

        return new HikariDataSource(config);
    }
}
```

### 2. Настройки таймаутов

```java
// ✅ Хорошо - правильные таймауты
@Configuration
public class TimeoutBestPractices {

    @Bean
    public DataSource timeoutConfiguredDataSource() {
        HikariConfig config = new HikariConfig();

        // === РЕКОМЕНДУЕМЫЕ ТАЙМАУТЫ ===

        // Время ожидания соединения
        config.setConnectionTimeout(30000);     // 30 сек - достаточно для большинства случаев

        // Время жизни соединения
        config.setMaxLifetime(1800000);         // 30 мин - баланс между производительностью и стабильностью

        // Время жизни idle соединения
        config.setIdleTimeout(600000);          // 10 мин - своевременное освобождение ресурсов

        // Валидация соединения
        config.setValidationTimeout(5000);      // 5 сек - быстрая проверка

        // Обнаружение утечек
        config.setLeakDetectionThreshold(60000); // 1 мин - разумное время для обнаружения

        return new HikariDataSource(config);
    }

    // ❌ Плохо - экстремальные таймауты
    @Bean
    public DataSource badTimeoutDataSource() {
        HikariConfig config = new HikariConfig();

        config.setConnectionTimeout(5000);      // Слишком мало - частые таймауты
        config.setMaxLifetime(3600000);         // Слишком много - старые соединения
        config.setIdleTimeout(300000);          // Слишком мало - частое пересоздание

        return new HikariDataSource(config);
    }
}
```

### 3. Мониторинг и алертинг

```java
// ✅ Хорошо - комплексный мониторинг
@Service
@Slf4j
public class MonitoringBestPractices {

    private final HikariDataSource dataSource;
    private final MeterRegistry meterRegistry;

    public MonitoringBestPractices(HikariDataSource dataSource, MeterRegistry meterRegistry) {
        this.dataSource = dataSource;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initMonitoring() {
        // Регистрация метрик
        HikariDataSourceMetrics.monitor(meterRegistry, dataSource, "db.pool");

        // Кастомные метрики
        Gauge.builder("hikaricp.pool.size", dataSource.getHikariPoolMXBean(),
                HikariPoolMXBean::getTotalConnections)
            .register(meterRegistry);

        Gauge.builder("hikaricp.pool.active", dataSource.getHikariPoolMXBean(),
                HikariPoolMXBean::getActiveConnections)
            .register(meterRegistry);
    }

    @Scheduled(fixedRate = 30000) // Каждые 30 сек
    public void checkPoolHealth() {
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

        int activeConnections = poolMXBean.getActiveConnections();
        int totalConnections = poolMXBean.getTotalConnections();
        int waitingThreads = poolMXBean.getThreadsAwaitingConnection();

        // Алертинг
        if (waitingThreads > 5) {
            log.error("CRITICAL: {} threads waiting for database connections!", waitingThreads);
            // Отправить алерт администратору
        }

        if (activeConnections == totalConnections && totalConnections > 0) {
            log.warn("WARNING: All {} connections are active", totalConnections);
        }

        // Логирование статистики
        log.debug("Pool status - Active: {}, Total: {}, Waiting: {}",
            activeConnections, totalConnections, waitingThreads);
    }

    // Health check для Spring Boot Actuator
    @Bean
    public HealthIndicator dbPoolHealthIndicator() {
        return () -> {
            HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

            int waitingThreads = poolMXBean.getThreadsAwaitingConnection();
            int activeConnections = poolMXBean.getActiveConnections();
            int totalConnections = poolMXBean.getTotalConnections();

            if (waitingThreads > 10) {
                return Health.down()
                    .withDetail("waitingThreads", waitingThreads)
                    .withDetail("message", "Too many threads waiting for connections")
                    .build();
            }

            if (activeConnections == totalConnections && totalConnections > 0) {
                return Health.down()
                    .withDetail("activeConnections", activeConnections)
                    .withDetail("totalConnections", totalConnections)
                    .withDetail("message", "Pool exhausted")
                    .build();
            }

            return Health.up()
                .withDetail("activeConnections", activeConnections)
                .withDetail("totalConnections", totalConnections)
                .withDetail("waitingThreads", waitingThreads)
                .build();
        };
    }
}
```

### 4. Обработка ошибок и восстановление

```java
// ✅ Хорошо - graceful error handling
@Service
@Slf4j
public class ErrorHandlingBestPractices {

    private final DataSource dataSource;
    private final RetryTemplate retryTemplate;

    public ErrorHandlingBestPractices(DataSource dataSource) {
        this.dataSource = dataSource;

        // Настройка retry для временных ошибок
        this.retryTemplate = RetryTemplate.builder()
            .maxAttempts(3)
            .fixedBackoff(1000) // 1 сек между попытками
            .retryOn(SQLTransientException.class)
            .build();
    }

    public <T> T executeWithRetry(Function<Connection, T> operation) {
        return retryTemplate.execute(context -> {
            try (Connection conn = dataSource.getConnection()) {
                return operation.apply(conn);
            } catch (SQLTransientException e) {
                log.warn("Transient database error, attempt {}: {}", context.getRetryCount(), e.getMessage());
                throw e; // Retry
            } catch (SQLException e) {
                log.error("Database error: {}", e.getMessage());
                throw new DataAccessException("Database operation failed", e);
            }
        });
    }

    // Circuit breaker паттерн
    private final CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("dbCircuitBreaker");

    public <T> T executeWithCircuitBreaker(Function<Connection, T> operation) {
        return circuitBreaker.decorateFunction(conn -> operation.apply(conn))
            .apply(getConnection());
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Graceful degradation
    public List<User> getUsersWithFallback() {
        try {
            return executeWithRetry(conn -> {
                // Основная логика получения пользователей
                return queryUsers(conn);
            });
        } catch (Exception e) {
            log.error("Failed to get users from database, using cache", e);
            // Fallback к кэшу или пустому списку
            return getUsersFromCache();
        }
    }

    private List<User> queryUsers(Connection conn) throws SQLException {
        // Реальная логика запроса
        return new ArrayList<>();
    }

    private List<User> getUsersFromCache() {
        // Логика получения из кэша
        return new ArrayList<>();
    }
}
```

### 5. Тестирование

```java
// ✅ Хорошо - тестирование пула соединений
@SpringBootTest
@Testcontainers
public class HikariConnectionPoolTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HikariDataSource hikariDataSource;

    @Test
    public void testConnectionPoolConfiguration() {
        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

        // Проверка настроек пула
        assertThat(poolMXBean.getTotalConnections()).isGreaterThan(0);
        assertThat(poolMXBean.getActiveConnections()).isLessThanOrEqualTo(
            hikariDataSource.getMaximumPoolSize());
    }

    @Test
    public void testConnectionAcquisition() throws SQLException {
        // Получение соединения
        try (Connection conn = dataSource.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isValid(5)).isTrue();

            // Выполнение запроса
            try (PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                 ResultSet rs = stmt.executeQuery()) {

                assertThat(rs.next()).isTrue();
                assertThat(rs.getInt(1)).isEqualTo(1);
            }
        }
    }

    @Test
    public void testConnectionPooling() throws SQLException {
        List<Connection> connections = new ArrayList<>();

        // Получение нескольких соединений
        for (int i = 0; i < 5; i++) {
            connections.add(dataSource.getConnection());
        }

        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

        // Проверка что соединения берутся из пула
        assertThat(poolMXBean.getActiveConnections()).isEqualTo(5);

        // Закрытие соединений
        for (Connection conn : connections) {
            conn.close();
        }

        // Проверка что соединения вернулись в пул
        assertThat(poolMXBean.getActiveConnections()).isEqualTo(0);
        assertThat(poolMXBean.getIdleConnections()).isGreaterThanOrEqualTo(5);
    }

    @Test
    public void testConnectionLeakDetection() throws InterruptedException {
        // Получение соединения без закрытия
        Connection leakedConnection = dataSource.getConnection();

        // Ожидание обнаружения утечки
        Thread.sleep(65000); // Больше чем leakDetectionThreshold

        // Проверка что утечка была обнаружена (в логах)
        // В реальном тесте можно проверить логи или метрики
    }

    @Test
    public void testPoolExhaustion() throws SQLException {
        int maxPoolSize = hikariDataSource.getMaximumPoolSize();
        List<Connection> connections = new ArrayList<>();

        // Исчерпание пула
        for (int i = 0; i < maxPoolSize; i++) {
            connections.add(dataSource.getConnection());
        }

        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();
        assertThat(poolMXBean.getActiveConnections()).isEqualTo(maxPoolSize);

        // Попытка получить еще одно соединение должна вызвать таймаут
        long startTime = System.currentTimeMillis();
        try {
            dataSource.getConnection();
            fail("Should have timed out");
        } catch (SQLException e) {
            long duration = System.currentTimeMillis() - startTime;
            assertThat(duration).isGreaterThanOrEqualTo(
                hikariDataSource.getConnectionTimeout() - 1000); // Погрешность
        } finally {
            // Очистка
            for (Connection conn : connections) {
                conn.close();
            }
        }
    }
}
```


## Заключение

**HikariCP** — это высокопроизводительный и надежный **JDBC connection pool**, который является стандартом для **Java** приложений. Он обеспечивает оптимальное управление соединениями с базой данных и высокую производительность.

### Преимущества HikariCP

1. **Высокая производительность** — Самый быстрый **connection pool** для **Java**
2. **Надежность** — Минимальное количество багов и проблем
3. **Простота использования** — Минимальная конфигурация
4. **Низкое потребление памяти** — Оптимизированное использование ресурсов
5. **Автоматическое восстановление** — Восстановление после сбоев БД
6. **Современная архитектура** — Использование современных **Java features**
7. **Широкая поддержка** — Поддержка всех основных СУБД
8. **Активное развитие** — Регулярные обновления и улучшения

### Основные паттерны использования

1. **Optimal `Pool Sizing` паттерн** — Правильный расчет размера пула
2. **Timeout `Configuration` паттерн** — Настройка таймаутов
3. **Monitoring паттерн** — Комплексный мониторинг пула
4. **Error `Handling` паттерн** — **Graceful** обработка ошибок
5. **Recovery паттерн** — Автоматическое восстановление

### Когда использовать HikariCP

**Рекомендуется:**
- **Enterprise** приложения с высокими требованиями к производительности
- Микросервисная архитектура
- Приложения с высокой нагрузкой на БД
- Системы с большим количеством одновременных подключений
- Проекты где важна надежность подключений к БД

**Особенно полезно:**
- В **Spring Boot** приложениях (используется по умолчанию)
- При работе с **PostgreSQL**, **MySQL**, **Oracle**
- В **distributed** системах
- При необходимости **connection pooling**
- В высоконагруженных приложениях

### Сравнение с альтернативами

| **Pool** | Преимущества | Недостатки |
|------|-------------|------------|
| **HikariCP** | Быстрый, надежный, простой | Меньше кастомизации |
| **Apache DBCP2** | Функциональный, гибкий | Медленнее, сложнее |
| **Tomcat JDBC** | Хороший для **Tomcat** | Ограниченная поддержка |
| **C3P0** | Зрелый, функциональный | Старый, менее производительный |
| **Vibur DBCP** | Быстрый, современный | Меньше пользователей |

**HikariCP** рекомендуется как основной **connection pool** для большинства **Java** проектов, особенно **enterprise** приложений с высокими требованиями к производительности и надежности.


[⬆ Наверх](../)

## См. также

- [[java-apache-httpclient|Apache HttpClient: Мощный HTTP клиент для Java]]
- [[java-apache-poi|Apache POI]]
- [[java-bean-validation|Bean Validation (JSR-380 / Jakarta Validation 3.0)]]
- [[java-http-clients|HTTP-клиенты в Java]]
- [[java-jackson|Jackson: JSON-сериализация в Java]]
