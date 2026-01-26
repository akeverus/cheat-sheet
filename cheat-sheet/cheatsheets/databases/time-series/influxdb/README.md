# InfluxDB

**InfluxDB** — это высокопроизводительная база данных временных рядов, оптимизированная для хранения и анализа данных с временными метками.

**Дата последнего обновления:** 2026-01-24

## Содержание

- [Введение](#введение)
- [Основные возможности](#основные-возможности)
- [Архитектура](#архитектура)
- [Установка](#установка)
- [Полезные ссылки](#полезные-ссылки)

## Введение

InfluxDB разработана специально для работы с временными рядами — данными, которые изменяются со временем (метрики, логи, события).

## Основные возможности

- **Высокая производительность** — оптимизированная для операций записи и чтения временных данных
- **SQL-подобный язык запросов** — InfluxQL для сложных аналитических запросов
- **Retention policies** — автоматическое удаление старых данных
- **Continuous queries** — автоматическое агрегирование данных
- **Horizontal scaling** — кластерная архитектура

## Архитектура

InfluxDB использует TSM (Time-Structured Merge Tree) для хранения данных, что обеспечивает высокую производительность записи.

## Установка

```bash
# Docker
docker run -p 8086:8086 influxdb:latest

# Ubuntu/Debian
wget -qO- https://repos.influxdata.com/influxdb.key | sudo apt-key add -
echo "deb https://repos.influxdata.com/ubuntu focal stable" | sudo tee /etc/apt/sources.list.d/influxdb.list
sudo apt update && sudo apt install influxdb
```

## Полезные ссылки

- [Официальная документация](https://docs.influxdata.com/influxdb/)
- [InfluxDB University](https://university.influxdata.com/)

### См. также
- `../timescaledb/README.md` — PostgreSQL с поддержкой временных рядов
- `../../nosql/README.md` — NoSQL базы данных
