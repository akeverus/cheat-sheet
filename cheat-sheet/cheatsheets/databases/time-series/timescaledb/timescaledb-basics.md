# TimescaleDB: PostgreSQL для временных рядов

**Комплексное руководство по использованию TimescaleDB — расширения PostgreSQL для работы с временными рядами.**

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [TimescaleDB Documentation](https://docs.timescale.com/) - Официальная документация
- [TimescaleDB GitHub](https://github.com/timescale/timescaledb) - Репозиторий проекта

### См. также
- `../influxdb/README.md` - InfluxDB временные ряды
- `../../relational/postgresql/postgres-basics.md` - PostgreSQL основы

## Содержание

- [Введение в TimescaleDB](#введение-в-timescaledb)
- [Установка](#установка)
- [Создание hypertable](#создание-hypertable)
- [Запросы временных рядов](#запросы-временных-рядов)
- [Интеграция с Java](#интеграция-с-java)

## Введение в TimescaleDB

**TimescaleDB** — это расширение PostgreSQL, оптимизированное для временных рядов. Сохраняет совместимость с PostgreSQL и добавляет специализированные функции.

### Основные возможности

- Автоматическое партиционирование по времени
- Оптимизированные запросы временных рядов
- Continuous aggregates
- Retention policies
- Compression

## Установка

```bash
# Docker установка
docker run -d --name timescaledb \
  -p 5432:5432 \
  -e POSTGRES_PASSWORD=password \
  timescale/timescaledb:latest-pg14
```

## Создание hypertable

```sql
-- Создание обычной таблицы
CREATE TABLE sensor_data (
    time TIMESTAMPTZ NOT NULL,
    sensor_id INTEGER NOT NULL,
    temperature DOUBLE PRECISION,
    humidity DOUBLE PRECISION
);

-- Преобразование в hypertable
SELECT create_hypertable('sensor_data', 'time');
```

## Запросы временных рядов

```sql
-- Запросы с временными окнами
SELECT 
    time_bucket('1 hour', time) AS hour,
    AVG(temperature) AS avg_temp
FROM sensor_data
WHERE time > NOW() - INTERVAL '24 hours'
GROUP BY hour
ORDER BY hour;
```

## Интеграция с Java

```java
// Использование стандартного PostgreSQL JDBC драйвера
// TimescaleDB полностью совместим с PostgreSQL
Connection conn = DriverManager.getConnection(
    "jdbc:postgresql://localhost:5432/mydb",
    "user", "password"
);
```

---

*Обновлено: 2026-01-25*
