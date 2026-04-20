---
title: "Cassandra: Основы - Полное руководство по распределенной NoSQL базе данных"
description: "Комплексное руководство по Apache Cassandra: архитектура, установка, модель данных, CQL и интеграция с Java Spring приложениями."
tags:
  - databases
  - nosql
  - cassandra-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Cassandra: Основы — Полное руководство по распределенной NoSQL базе данных

Комплексное руководство по **Apache Cassandra**: архитектура, установка, модель данных, **CQL** и интеграция с **Java Spring** приложениями.

## Полезные ссылки

### Официальная документация
- [Apache Cassandra Documentation](https://cassandra.apache.org/doc/latest/)
- [CQL Reference](https://cassandra.apache.org/doc/latest/cql/) — **Cassandra Query Language**
- [Cassandra Architecture](https://cassandra.apache.org/doc/latest/architecture/)

### Java интеграция
- [DataStax Java Driver](https://docs.datastax.com/en/developer/java-driver/)
- [Spring Data Cassandra](https://spring.io/projects/spring-data-cassandra)
- [Cassandra with Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.nosql.cassandra)

### Мониторинг и инструменты
- [Cassandra Reaper](https://cassandra-reaper.io/)
- [DataStax Astra](https://www.datastax.com/products/datastax-astra)
- [Cassandra Medusa](https://github.com/thelastpickle/cassandra-medusa)

### См. также
- [[mysql-basics|MySQL]] — сравнение с реляционными БД
- [[mongodb-basics|MongoDB]] — сравнение с документными БД
- [[cassandra-data-modeling|cassandra-data-modeling.md]] — моделирование данных в **Cassandra**
- [[cassandra-clustering|cassandra-clustering.md]] — кластеризация и масштабирование

## Содержание

- [Введение в **Cassandra**](#введение-в-cassandra)
  - [Ключевые особенности **Cassandra**](#ключевые-особенности-cassandra)
    - [Высокая доступность и масштабируемость](#высокая-доступность-и-масштабируемость)
    - [Гибкая модель данных](#гибкая-модель-данных)
    - [Мощные возможности](#мощные-возможности)
  - [Когда использовать **Cassandra**](#когда-использовать-cassandra)
    - [Идеально подходит для:](#идеально-подходит-для)
    - [Не подходит для:](#не-подходит-для)
  - [Версии **Cassandra**](#версии-cassandra)
    - [**Cassandra** 4.x (рекомендуемая)](#cassandra-4x-рекомендуемая)
    - [**Cassandra** 3.x (LTS)](#cassandra-3x-lts)
    - [**Astra** DB (Cloud)](#astra-db-cloud)
  - [Сравнение с другими базами данных](#сравнение-с-другими-базами-данных)
- [Архитектура **Cassandra**](#архитектура-cassandra)
  - [Компоненты системы](#компоненты-системы)
    - [Узлы (Nodes)](#узлы-nodes)
    - [Кластеры (Clusters)](#кластеры-clusters)
    - [Датацентры (Datacenters)](#датацентры-datacenters)
  - [**Ring** архитектура](#ring-архитектура)
    - [**Consistent Hashing**](#consistent-hashing)
    - [Преимущества **Ring** архитектуры:](#преимущества-ring-архитектуры)
  - [Компоненты узла](#компоненты-узла)
    - [**Storage Engine**](#storage-engine)
    - [**Gossip Protocol**](#gossip-protocol)
    - [**Snitch**](#snitch)
- [Установка и настройка](#установка-и-настройка)
  - [Установка на разных ОС](#установка-на-разных-ос)
    - [**Ubuntu**/**Debian**](#ubuntudebian)
- [Добавление репозитория](#добавление-репозитория)
- [Импорт ключа](#импорт-ключа)
- [Установка](#установка)
- [Проверка статуса](#проверка-статуса)
- [Проверка установки](#проверка-установки)
    - [**CentOS**/**RHEL**](#centosrhel)
- [Запуск](#запуск)
    - [**Docker**](#docker)
- [Запуск Cassandra в Docker](#запуск-cassandra-в-docker)
- [Подключение к CQLSH](#подключение-к-cqlsh)
- [Docker Compose](#docker-compose)
  - [Конфигурация **Cassandra**](#конфигурация-cassandra)
    - [Основной конфигурационный файл](#основной-конфигурационный-файл)
- [Сеть](#сеть)
- [Данные](#данные)
- [Репликация](#репликация)
- [Производительность](#производительность)
- [Логирование](#логирование)
    - [**JVM** настройки](#jvm-настройки)
- [jvm.options](#jvmoptions)
- [GC логирование](#gc-логирование)
- [JMX](#jmx)
    - [Тестирование кластера](#тестирование-кластера)
- [Проверка состояния узла](#проверка-состояния-узла)
- [Информация о кластере](#информация-о-кластере)
- [Информация об узле](#информация-об-узле)
- [Проверка с помощью CQLSH](#проверка-с-помощью-cqlsh)
- [Тестовые операции](#тестовые-операции)
- [Модель данных **Cassandra**](#модель-данных-cassandra)
  - [Иерархия данных](#иерархия-данных)
    - [**Keyspace**](#keyspace)
    - [**Table**](#table)
    - [**Row** и **Column**](#row-и-column)
  - [Типы колонок](#типы-колонок)
    - [**Regular Columns**](#regular-columns)
    - [**Static Columns**](#static-columns)
    - [**Counter Columns**](#counter-columns)
- [**Cassandra Query Language** (CQL)](#cassandra-query-language-cql)
  - [Основные команды **CQL**](#основные-команды-cql)
    - [**DDL** команды](#ddl-команды)
    - [**DML** команды](#dml-команды)
  - [Расширенные возможности **CQL**](#расширенные-возможности-cql)
    - [**JSON** поддержка](#json-поддержка)
    - [**Time-to-Live** (TTL)](#time-to-live-ttl)
    - [**Batch** операции](#batch-операции)
- [Подключение к **Cassandra**](#подключение-к-cassandra)
  - [**DataStax Java Driver**](#datastax-java-driver)
    - [Базовое подключение](#базовое-подключение)
    - [Пул подключений и конфигурация](#пул-подключений-и-конфигурация)
  - [**Spring Data Cassandra**](#spring-data-cassandra)
    - [Конфигурация **Spring**](#конфигурация-spring)
    - [Репозитории **Spring Data**](#репозитории-spring-data)
- [Основные операции **CRUD**](#основные-операции-crud)
  - [Создание данных](#создание-данных)
    - [Простая вставка](#простая-вставка)
  - [Чтение данных](#чтение-данных)
    - [Запросы по первичному ключу](#запросы-по-первичному-ключу)
  - [Обновление данных](#обновление-данных)
    - [**Update** операции](#update-операции)
  - [Удаление данных](#удаление-данных)
    - [**Delete** операции](#delete-операции)
- [Типы данных](#типы-данных)
  - [Примитивные типы](#примитивные-типы)
    - [Числовые типы](#числовые-типы)
    - [Строковые и временные типы](#строковые-и-временные-типы)
  - [Коллекции](#коллекции)
    - [**Set** (множество)](#set-множество)
    - [**List** (список)](#list-список)
    - [**Map** (словарь)](#map-словарь)
  - [Пользовательские типы (UDT)](#пользовательские-типы-udt)
    - [Создание **UDT**](#создание-udt)
    - [Работа с **UDT**](#работа-с-udt)
- [Ключи и партиционирование](#ключи-и-партиционирование)
  - [Структура первичного ключа](#структура-первичного-ключа)
    - [**Partition Key**](#partition-key)
    - [**Clustering Columns**](#clustering-columns)
  - [Стратегии партиционирования](#стратегии-партиционирования)
    - [**Partition Key** выбор](#partition-key-выбор)
- [Консистентность и репликация](#консистентность-и-репликация)
  - [Уровни консистентности](#уровни-консистентности)
    - [**Write Consistency**](#write-consistency)
    - [**Read Consistency**](#read-consistency)
  - [Репликация между датацентрами](#репликация-между-датацентрами)
    - [**NetworkTopologyStrategy**](#networktopologystrategy)
    - [Локальная консистентность](#локальная-консистентность)
  - [**Entity** маппинг](#entity-маппинг)
    - [Базовые **entity**](#базовые-entity)
    - [Репозитории](#репозитории)
  - [Сервисы](#сервисы)
    - [**CRUD** операции](#crud-операции)
- [Мониторинг и диагностика](#мониторинг-и-диагностика)
  - [**Nodetool** команды](#nodetool-команды)
    - [Проверка состояния кластера](#проверка-состояния-кластера)
- [Статус кластера](#статус-кластера)
- [Детальная информация об узле](#детальная-информация-об-узле)
- [Описание кластера](#описание-кластера)
- [Проверка потоков](#проверка-потоков)
- [Статистика кэша](#статистика-кэша)
    - [Диагностика проблем](#диагностика-проблем)
- [Проверка соединений](#проверка-соединений)
- [Статистика таблиц](#статистика-таблиц)
- [Проверка compaction](#проверка-compaction)
- [Очистка snapshots](#очистка-snapshots)
  - [**CQLSH** диагностика](#cqlsh-диагностика)
    - [Системные таблицы](#системные-таблицы)
    - [Диагностические запросы](#диагностические-запросы)
  - [**Java** диагностика](#java-диагностика)
- [**Best Practices**](#лучшие-практики)
  - [Проектирование схемы](#проектирование-схемы)
    - [1. Правильный выбор **partition key**](#1-правильный-выбор-partition-key)
    - [2. Оптимизация для паттернов запросов](#2-оптимизация-для-паттернов-запросов)
    - [3. Избегать **hot partitions**](#3-избегать-hot-partitions)
  - [Управление данными](#управление-данными)
    - [1. Использование **TTL** для временных данных](#1-использование-ttl-для-временных-данных)
    - [2. Компактификация и обслуживание](#2-компактификация-и-обслуживание)
- [Ручная компактификация (обычно не нужно)](#ручная-компактификация-обычно-не-нужно)
- [Очистка после удалений](#очистка-после-удалений)
- [Проверка уровня сжатия](#проверка-уровня-сжатия)
    - [3. Резервное копирование](#3-резервное-копирование)
- [Создание snapshot](#создание-snapshot)
- [Инкрементальное бэкап с Medusa](#инкрементальное-бэкап-с-medusa)
- [Восстановление](#восстановление)
    - [1. Оптимизация запросов](#1-оптимизация-запросов)
    - [2. Настройка консистентности](#2-настройка-консистентности)
    - [3. Пакетная обработка](#3-пакетная-обработка)
  - [Мониторинг](#мониторинг)
    - [1. Ключевые метрики](#1-ключевые-метрики)
    - [2. Алерты](#2-алерты)
    - [3. Регулярные проверки](#3-регулярные-проверки)
- [Ежедневные проверки](#ежедневные-проверки)
- [cassandra_daily_check.sh](#cassandra_daily_checksh)
- [Проверка логов на ошибки](#проверка-логов-на-ошибки)
- [Проверка дискового пространства](#проверка-дискового-пространства)
- [Проверка репликации](#проверка-репликации)
- [Troubleshooting](#решение-проблем)
- [FAQ](#частые-вопросы)
- [Заключение](#заключение)
  - [Преимущества **Cassandra**:](#преимущества-cassandra)
  - [Архитектурные особенности:](#архитектурные-особенности)
  - [Когда выбирать **Cassandra**:](#когда-выбирать-cassandra)
  - [Лучшие практики:](#лучшие-практики)

## Введение в Cassandra

**Apache Cassandra** — это высокопроизводительная, масштабируемая **NoSQL** база данных, разработанная для обработки больших объемов данных в распределенных системах. **Cassandra** была создана в **Facebook** и передана **Apache Software Foundation** в `2009` году.

### Ключевые особенности Cassandra

#### Высокая доступность и масштабируемость
- **Линейная масштабируемость** — добавление новых узлов без простоев
- **Отказоустойчивость** — работает при отказе нескольких узлов
- **Геораспределенность** — датацентры в разных регионах
- **Высокая производительность** — миллиарды операций в день

#### Гибкая модель данных
- **Широко-столбчатая модель** — гибкая схема без строгой структуры
- **Динамическое добавление колонок** — изменение схемы на лету
- **Иерархическая структура** — **keyspace** → **table** → **row** → **column**
- **Поддержка сложных типов** — коллекции, **UDT**, кортежи

#### Мощные возможности
- **CQL (Cassandra Query Language)** — **SQL**-подобный язык запросов
- **Вторичные индексы** — индексация не только по первичному ключу
- **Материализованные представления** — автоматическая денормализация
- **Триггеры и функции** — бизнес-логика на уровне базы данных

### Когда использовать Cassandra

#### Идеально подходит для:
- **Big Data** — петабайты данных
- **Высоконагруженные системы** — миллионы запросов в секунду
- **Глобальные приложения** — пользователи по всему миру
- **Time-series данные** — логи, метрики, события
- **IoT приложения** — данные от устройств
- **Content management** — каталоги, медиа

#### Не подходит для:
- **ACID транзакции** — ограниченная поддержка транзакций
- **Сложные `JOIN` операции** — нет поддержки **JOIN**
- **Реляционные запросы** — нереляционная модель
- **Небольшие объемы данных** — **overhead** для маленьких систем

### Версии Cassandra

#### Cassandra 4.x (рекомендуемая)
- **Virtual Tables** — системные таблицы в **CQL**
- **Audit Logging** — аудит всех операций
- **Transient Replication** — оптимизированная репликация
- **Zero `Copy` Streaming** — эффективная передача данных
- **Java 11+** — современная **Java**

#### Cassandra 3.x (LTS)
- Стабильная версия для **production**
- Полная поддержка **enterprise features**
- Обширная экосистема инструментов

#### Astra `DB` (Cloud)
- **Управляемая Cassandra** в облаке
- **Автоматическое масштабирование**
- **Встроенный мониторинг**
- **Pay-`as-you`-go** модель

### Сравнение с другими базами данных

| Характеристика | **Cassandra** | **MongoDB** | **PostgreSQL** | **MySQL** |
|---|---|---|---|---|
| **Модель данных** | **Wide Column** | **Document** | **Relational** | **Relational** |
| **Масштабируемость** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐ |
| **Производительность** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Консистентность** | Настраиваемая | **Strong**/**Eventual** | **ACID** | **ACID** |
| **Сложность** | Высокая | Средняя | Средняя | Низкая |
| **Use cases** | **Big Data**, **IoT** | **Apps**, **Content** | **OLTP**, **Analytics** | **Web apps** |

## Архитектура Cassandra

### Компоненты системы

#### Узлы (Nodes)
- **Отдельные серверы** с **Cassandra**
- **Хранение данных** и обработка запросов
- **Peer-`to-peer` архитектура** — все узлы равны
- **Автоматическое распределение** нагрузки

#### Кластеры (Clusters)
- **Группа узлов** работающих вместе
- **Общий keyspace** — логическое разделение данных
- **Репликация** между узлами
- **Отказоустойчивость** кластера

#### Датацентры (Datacenters)
- **Физическое разделение** по локациям
- **Изоляция сетевых сбоев**
- **Оптимизация latency** для пользователей
- **Разные уровни консистентности**

### Ring архитектура

#### Consistent Hashing

Схема кольца **consistent hashing**: диапазон токенов от 0 до 2^128−1 для распределения данных по узлам.

```text
# Токенное кольцо Cassandra: диапазоны по 2^128
Ring: 0 → 2^128 - 1
├── Node A: Token range 0-25
├── Node B: Token range 26-50
├── Node C: Token range 51-75
└── Node D: Token range 76-100
```

#### Преимущества Ring архитектуры:
- **Равномерное распределение** данных
- **Легкое добавление узлов** без перебалансировки
- **Предсказуемость** расположения данных
- **Отказоустойчивость** при потере узлов

### Компоненты узла

#### Storage Engine
- **Commit Log** — журнал всех изменений (WAL)
- **MemTable** — **in-memory** структура для новых данных
- **SSTable** — неизменяемые файлы на диске
- **Bloom Filter** — вероятностный фильтр для быстрого поиска

#### Gossip Protocol
- **Распространение информации** между узлами
- **Обнаружение отказов** узлов
- **Метаданные кластера** — состояние, нагрузка, версия
- **Децентрализованное** управление

#### Snitch
- **Определение топологии** сети
- **Стратегии репликации** по датацентрам
- **Оптимизация маршрутизации** запросов
- **Rack awareness** для отказоустойчивости

## Установка и настройка

### Установка на разных ОС

#### Ubuntu/Debian
```bash
# Добавление репозитория
echo "deb [signed-by=/usr/share/keyrings/apache-cassandra.gpg] https://debian.cassandra.apache.org 41x main" | sudo tee -a /etc/apt/sources.list.d/cassandra.sources.list

# Импорт ключа
curl -L https://downloads.apache.org/cassandra/KEYS | sudo apt-key add -

# Установка
sudo apt update
sudo apt install cassandra

# Проверка статуса
sudo systemctl status cassandra
sudo systemctl enable cassandra

# Проверка установки
cqlsh -e "SELECT cluster_name, listen_address FROM system.local;"
```

#### CentOS/RHEL
```bash
# Добавление репозитория
sudo tee /etc/yum.repos.d/cassandra.repo > /dev/null <<EOF
[cassandra]
name=Apache Cassandra
baseurl=https://downloads.apache.org/cassandra/redhat/41x/
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://downloads.apache.org/cassandra/KEYS
EOF

# Установка
sudo yum install cassandra

# Запуск
sudo systemctl start cassandra
sudo systemctl enable cassandra
```

#### Docker
```bash
# Запуск Cassandra в Docker
docker run --name cassandra-node \
  -p 9042:9042 \
  -p 7199:7199 \
  -p 9160:9160 \
  -v cassandra-data:/var/lib/cassandra \
  -e CASSANDRA_CLUSTER_NAME=MyCluster \
  -e CASSANDRA_DC=datacenter1 \
  -e CASSANDRA_RACK=rack1 \
  -d cassandra:4.0

# Подключение к CQLSH
docker exec -it cassandra-node cqlsh

# Docker Compose
version: '3.8'
services:
  cassandra:
    image: cassandra:4.0
    ports:
      - "9042:9042"    # CQL
      - "7199:7199"    # JMX
      - "9160:9160"    # Thrift (legacy)
    environment:
      CASSANDRA_CLUSTER_NAME: MyCluster
      CASSANDRA_DC: datacenter1
      CASSANDRA_RACK: rack1
      CASSANDRA_ENDPOINT_SNITCH: GossipingPropertyFileSnitch
    volumes:
      - cassandra_data:/var/lib/cassandra
      - ./cassandra.yaml:/etc/cassandra/cassandra.yaml

  cassandra-init:
    image: cassandra:4.0
    depends_on:
      - cassandra
    volumes:
      - ./init.cql:/init.cql
    command: /bin/bash -c "sleep 30 && cqlsh cassandra -f /init.cql"

volumes:
  cassandra_data:
```

### Конфигурация Cassandra

#### Основной конфигурационный файл
```yaml
# cassandra.yaml - основные настройки
cluster_name: 'MyCluster'
num_tokens: 256

# Сеть
listen_address: localhost
rpc_address: 0.0.0.0
rpc_port: 9160

# Данные
data_file_directories:
  - /var/lib/cassandra/data
commitlog_directory: /var/lib/cassandra/commitlog
saved_caches_directory: /var/lib/cassandra/saved_caches

# Репликация
endpoint_snitch: SimpleSnitch
seed_provider:
  - class_name: org.apache.cassandra.locator.SimpleSeedProvider
    parameters:
      - seeds: "127.0.0.1"

# Производительность
concurrent_reads: 32
concurrent_writes: 32
memtable_heap_space_in_mb: 2048
memtable_offheap_space_in_mb: 2048

# Логирование
commitlog_sync: periodic
commitlog_sync_period_in_ms: 10000
```

#### JVM настройки
```properties
# jvm.options
-Xms4G
-Xmx4G
-XX:+UseG1GC
-XX:G1RSetUpdatingPauseTimePercent=5
-XX:MaxGCPauseMillis=500
-XX:G1HeapRegionSize=16m
-XX:InitiatingHeapOccupancyPercent=70

# GC логирование
-Xlog:gc=info:file=/var/log/cassandra/gc.log:time:filecount=10,filesize=10M

# JMX
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=7199
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.authenticate=false
```

### Проверка установки

#### Тестирование кластера
```bash
# Проверка состояния узла
nodetool status

# Информация о кластере
nodetool describecluster

# Информация об узле
nodetool info

# Проверка с помощью CQLSH
cqlsh -e "SELECT cluster_name, data_center, rack, tokens FROM system.local;"

# Тестовые операции
cqlsh -e "
  CREATE KEYSPACE test WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
  CREATE TABLE test.users (id UUID PRIMARY KEY, name TEXT, email TEXT);
  INSERT INTO test.users (id, name, email) VALUES (uuid(), 'Test User', 'test@example.com');
  SELECT * FROM test.users;
"
```

## Модель данных Cassandra

### Иерархия данных

#### Keyspace
- **Логический контейнер** для таблиц
- **Настройки репликации** для всех таблиц
- **Durable writes** — запись в **commit log**
- **Аналог базы данных** в реляционных СУБД

```cql
-- Создание keyspace
CREATE KEYSPACE myapp
WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'datacenter1': 3,
  'datacenter2': 2
}
AND durable_writes = true;
```

#### Table
- **Основная единица хранения** данных
- **Первичный ключ** — **partition key** + **clustering columns**
- **Колонки** — гибкая схема, динамическое добавление
- **TTL** — автоматическое удаление данных

```cql
-- Создание таблицы
CREATE TABLE users (
  user_id UUID,
  email TEXT,
  name TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  preferences MAP<TEXT, TEXT>,
  tags SET<TEXT>,
  addresses LIST<FROZEN<address>>,
  PRIMARY KEY ((user_id), email)
) WITH CLUSTERING ORDER BY (email ASC);
```

#### Row и Column
- **Row** — строка данных с общим **partition key**
- **Column** — имя + значение + **timestamp**
- **Wide rows** — множество колонок в одной строке
- **Sparse data** — не все колонки обязаны присутствовать

### Типы колонок

#### Regular Columns
```cql
-- Таблица товаров с обычными и коллекционными типами колонок
CREATE TABLE products (
  product_id UUID PRIMARY KEY,
  name TEXT,
  description TEXT,
  price DECIMAL,
  category TEXT,
  tags SET<TEXT>,
  attributes MAP<TEXT, TEXT>,
  reviews LIST<FROZEN<review>>,
  created_at TIMESTAMP
);
```

#### Static Columns
```cql
-- Посты пользователя со статическими колонками имени и аватара
CREATE TABLE user_posts (
  user_id UUID,
  post_id TIMEUUID,
  content TEXT,
  created_at TIMESTAMP,
  -- Статическая колонка - одинакова для всех постов пользователя
  user_name TEXT STATIC,
  user_avatar TEXT STATIC,
  PRIMARY KEY ((user_id), post_id)
) WITH CLUSTERING ORDER BY (post_id DESC);
```

#### Counter Columns
```cql
-- Таблица просмотров страниц с счётчиками (COUNTER)
CREATE TABLE page_views (
  page_id UUID,
  date DATE,
  views COUNTER,
  unique_visitors COUNTER,
  PRIMARY KEY ((page_id), date)
);

-- Инкремент счетчиков
UPDATE page_views SET views = views + 1, unique_visitors = unique_visitors + 1
WHERE page_id = ? AND date = ?;
```

## Cassandra Query Language (CQL)

### Основные команды CQL

#### DDL команды
```cql
-- Создание keyspace
CREATE KEYSPACE ecommerce
WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'dc1': 3,
  'dc2': 2
};

-- Создание таблицы
CREATE TABLE products (
  id UUID PRIMARY KEY,
  name TEXT,
  price DECIMAL,
  category TEXT,
  tags SET<TEXT>,
  created_at TIMESTAMP
);

-- Изменение таблицы
ALTER TABLE products ADD description TEXT;
ALTER TABLE products DROP tags;
ALTER TABLE products RENAME name TO product_name;

-- Удаление таблицы
DROP TABLE products;
DROP KEYSPACE ecommerce;
```

#### DML команды
```cql
-- Вставка данных
INSERT INTO products (id, name, price, category, created_at)
VALUES (uuid(), 'Laptop', 999.99, 'electronics', toTimestamp(now()));

-- Выборка данных
SELECT id, name, price, category FROM products LIMIT 10;

-- Обновление данных
UPDATE products SET price = 899.99, category = 'computers'
WHERE id = 123e4567-e89b-12d3-a456-426614174000;

-- Удаление данных
DELETE FROM products WHERE id = 123e4567-e89b-12d3-a456-426614174000;
DELETE price FROM products WHERE id = 123e4567-e89b-12d3-a456-426614174000;
```

### Расширенные возможности CQL

#### JSON поддержка
```cql
-- Вставка JSON
INSERT INTO products JSON '{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Smartphone",
  "price": 599.99,
  "specs": {"ram": "8GB", "storage": "128GB"}
}';

-- Выборка JSON
SELECT JSON * FROM products WHERE id = 123e4567-e89b-12d3-a456-426614174000;

-- Обновление JSON
UPDATE products SET specs = specs + '{"color": "black"}'
WHERE id = 123e4567-e89b-12d3-a456-426614174000;
```

#### Time-to-Live (TTL)
```cql
-- Вставка с TTL
INSERT INTO sessions (session_id, user_id, data)
VALUES (?, ?, ?) USING TTL 3600; -- 1 час

-- Обновление TTL
UPDATE sessions USING TTL 1800
SET last_access = toTimestamp(now())
WHERE session_id = ?;

-- Проверка TTL
SELECT session_id, ttl(data) as time_to_live FROM sessions;
```

#### Batch операции
```cql
-- Атомарная batch операция
BEGIN BATCH
  INSERT INTO orders (id, user_id, total_amount, status)
  VALUES (uuid(), ?, ?, 'pending');

  UPDATE user_cart SET items = [] WHERE user_id = ?;

  INSERT INTO order_items (order_id, product_id, quantity, price)
  VALUES (?, ?, ?, ?);
APPLY BATCH;
```

## Подключение к Cassandra

### DataStax Java Driver

#### Базовое подключение
```java
// Конфигурация сессии Cassandra и сервис проверки подключения
@Configuration
public class CassandraConfig {

    @Bean
    public CqlSession cassandraSession() {
        return CqlSession.builder()
            .addContactPoint(new InetSocketAddress("localhost", 9042))
            .withKeyspace("myapp")
            .withAuthCredentials("username", "password")
            .withLocalDatacenter("datacenter1")
            .build();
    }
}

@Service
public class CassandraConnectionService {

    @Autowired
    private CqlSession session;

    public boolean testConnection() {
        try {
            ResultSet rs = session.execute("SELECT cluster_name FROM system.local");
            Row row = rs.one();
            System.out.println("Connected to cluster: " + row.getString("cluster_name"));
            return true;
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    public ClusterInfo getClusterInfo() {
        ResultSet rs = session.execute("""
            SELECT cluster_name, data_center, rack, release_version
            FROM system.local
            """);

        Row row = rs.one();
        return new ClusterInfo(
            row.getString("cluster_name"),
            row.getString("data_center"),
            row.getString("rack"),
            row.getString("release_version")
        );
    }
}
```

#### Пул подключений и конфигурация
```java
// Расширенная конфигурация с пулом и таймаутами
@Configuration
public class AdvancedCassandraConfig {

    @Bean
    public DriverConfigLoader driverConfigLoader() {
        return DriverConfigLoader.programmaticBuilder()
            .withString(DefaultDriverOption.CONTACT_POINTS, "localhost:9042,localhost:9043")
            .withString(DefaultDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, "datacenter1")
            .withInt(DefaultDriverOption.CONNECTION_POOL_LOCAL_SIZE, 4)
            .withInt(DefaultDriverOption.CONNECTION_POOL_REMOTE_SIZE, 2)
            .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(10))
            .withDuration(DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofSeconds(5))
            .withInt(DefaultDriverOption.REQUEST_PAGE_SIZE, 5000)
            .build();
    }

    @Bean
    public CqlSessionBuilder cqlSessionBuilder(DriverConfigLoader configLoader) {
        return CqlSession.builder()
            .withConfigLoader(configLoader)
            .withKeyspace("myapp")
            .withAuthCredentials("cassandra", "cassandra");
    }

    @Bean
    public CqlSession cassandraSession(CqlSessionBuilder sessionBuilder) {
        return sessionBuilder.build();
    }
}
```

### Spring Data Cassandra

#### Конфигурация Spring
```java
// Конфигурация Spring Data Cassandra: keyspace, контактные точки, репозитории
@Configuration
@EnableCassandraRepositories(basePackages = "com.example.repository")
public class SpringCassandraConfig extends AbstractCassandraConfiguration {

    @Override
    protected String getKeyspaceName() {
        return "myapp";
    }

    @Override
    protected String getContactPoints() {
        return "localhost";
    }

    @Override
    protected int getPort() {
        return 9042;
    }

    @Override
    protected String getLocalDataCenter() {
        return "datacenter1";
    }

    @Override
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = super.cluster();
        cluster.setUsername("cassandra");
        cluster.setPassword("cassandra");
        return cluster;
    }

    @Override
    public CassandraMappingContext cassandraMapping() {
        return new CassandraMappingContext();
    }

    @Bean
    public CassandraOperations cassandraTemplate() throws Exception {
        return new CassandraTemplate(session().getObject());
    }
}
```

#### Репозитории Spring Data
```java
// Репозиторий пользователей и сервис с CRUD через Spring Data
@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {

    // Автоматически генерируемые методы
    Optional<User> findById(UUID id);
    List<User> findAll();
    void deleteById(UUID id);

    // Кастомные запросы
    @Query("SELECT * FROM users WHERE email = ?0 ALLOW FILTERING")
    Optional<User> findByEmail(String email);

    @Query("SELECT * FROM users WHERE name LIKE ?0 ALLOW FILTERING")
    List<User> findByNameLike(String namePattern);

    @AllowFiltering
    List<User> findByAgeGreaterThan(int age);

    @AllowFiltering
    List<User> findByCityAndActive(String city, boolean active);
}

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CassandraOperations cassandraTemplate;

    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setCity(request.getCity());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public List<User> findUsersByCity(String city) {
        return userRepository.findByCityAndActive(city, true);
    }

    public void updateUserPreferences(UUID userId, Map<String, String> preferences) {
        cassandraTemplate.execute("UPDATE users SET preferences = preferences + ? WHERE id = ?",
            preferences, userId);
    }
}
```

## Основные операции CRUD

### Создание данных

#### Простая вставка
```java
// Вставка пользователя через подготовленное выражение
@Service
public class UserDataService {

    @Autowired
    private CqlSession session;

    public void createUser(User user) {
        PreparedStatement insertUser = session.prepare("""
            INSERT INTO users (id, email, name, age, city, active, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """);

        session.execute(insertUser.bind(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getAge(),
            user.getCity(),
            user.isActive(),
            user.getCreatedAt()
        ));
    }

    public void createUserBatch(List<User> users) {
        PreparedStatement insertUser = session.prepare("""
            INSERT INTO users (id, email, name, age, city, active, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """);

        BatchStatement batch = BatchStatement.newInstance(BatchType.UNLOGGED);

        for (User user : users) {
            batch.add(insertUser.bind(
                user.getId(), user.getEmail(), user.getName(),
                user.getAge(), user.getCity(), user.isActive(), user.getCreatedAt()
            ));
        }

        session.execute(batch);
    }
}
```

### Чтение данных

#### Запросы по первичному ключу
```java
// Выборка пользователей по id и по индексу
@Service
public class UserQueryService {

    @Autowired
    private CqlSession session;

    public Optional<User> findUserById(UUID userId) {
        PreparedStatement selectUser = session.prepare("""
            SELECT id, email, name, age, city, active, created_at
            FROM users WHERE id = ?
            """);

        ResultSet rs = session.execute(selectUser.bind(userId));
        Row row = rs.one();

        if (row != null) {
            User user = new User();
            user.setId(row.getUuid("id"));
            user.setEmail(row.getString("email"));
            user.setName(row.getString("name"));
            user.setAge(row.getInt("age"));
            user.setCity(row.getString("city"));
            user.setActive(row.getBoolean("active"));
            user.setCreatedAt(row.getLocalDateTime("created_at"));
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public List<User> findUsersByCity(String city) {
        PreparedStatement selectUsers = session.prepare("""
            SELECT id, email, name, age, city, active, created_at
            FROM users WHERE city = ? ALLOW FILTERING
            """);

        ResultSet rs = session.execute(selectUsers.bind(city));
        List<User> users = new ArrayList<>();

        for (Row row : rs) {
            User user = new User();
            user.setId(row.getUuid("id"));
            user.setEmail(row.getString("email"));
            user.setName(row.getString("name"));
            user.setAge(row.getInt("age"));
            user.setCity(row.getString("city"));
            user.setActive(row.getBoolean("active"));
            user.setCreatedAt(row.getLocalDateTime("created_at"));
            users.add(user);
        }

        return users;
    }

    public List<User> findUsersWithPaging(int pageSize, ByteBuffer pagingState) {
        Statement<?> statement = SimpleStatement.newInstance("""
            SELECT id, email, name, age, city, active, created_at FROM users
            """).setPageSize(pageSize);

        if (pagingState != null) {
            statement.setPagingState(pagingState);
        }

        ResultSet rs = session.execute(statement);
        List<User> users = new ArrayList<>();

        for (Row row : rs) {
            // mapping logic
            users.add(mapRowToUser(row));
        }

        // Save paging state for next request
        ByteBuffer nextPagingState = rs.getExecutionInfo().getPagingState();

        return users;
    }
}
```

### Обновление данных

#### Update операции
```java
// Обновление пользователя и частичное обновление полей
@Service
public class UserUpdateService {

    @Autowired
    private CqlSession session;

    public void updateUserProfile(UUID userId, String newName, Integer newAge) {
        PreparedStatement updateUser = session.prepare("""
            UPDATE users SET name = ?, age = ?, updated_at = ?
            WHERE id = ?
            """);

        session.execute(updateUser.bind(
            newName,
            newAge,
            LocalDateTime.now(),
            userId
        ));
    }

    public void updateUserPreferences(UUID userId, Map<String, String> preferences) {
        PreparedStatement updatePrefs = session.prepare("""
            UPDATE users SET preferences = preferences + ?, updated_at = ?
            WHERE id = ?
            """);

        session.execute(updatePrefs.bind(
            preferences,
            LocalDateTime.now(),
            userId
        ));
    }

    public void addUserTag(UUID userId, String tag) {
        PreparedStatement addTag = session.prepare("""
            UPDATE users SET tags = tags + ? WHERE id = ?
            """);

        Set<String> tagSet = new HashSet<>();
        tagSet.add(tag);

        session.execute(addTag.bind(tagSet, userId));
    }

    public void incrementUserStats(UUID userId) {
        PreparedStatement incrementStats = session.prepare("""
            UPDATE user_stats SET login_count = login_count + 1,
                                  last_login = ?
            WHERE user_id = ?
            """);

        session.execute(incrementStats.bind(
            LocalDateTime.now(),
            userId
        ));
    }
}
```

### Удаление данных

#### Delete операции
```java
// Удаление пользователя по id и условное удаление
@Service
public class UserDeleteService {

    @Autowired
    private CqlSession session;

    public void deleteUser(UUID userId) {
        PreparedStatement deleteUser = session.prepare("""
            DELETE FROM users WHERE id = ?
            """);

        session.execute(deleteUser.bind(userId));
    }

    public void deleteUserByEmail(String email) {
        PreparedStatement deleteByEmail = session.prepare("""
            DELETE FROM users WHERE email = ? ALLOW FILTERING
            """);

        session.execute(deleteByEmail.bind(email));
    }

    public void deactivateUser(UUID userId) {
        PreparedStatement deactivateUser = session.prepare("""
            UPDATE users SET active = false, deactivated_at = ?
            WHERE id = ?
            """);

        session.execute(deactivateUser.bind(LocalDateTime.now(), userId));
    }

    public void removeUserTag(UUID userId, String tag) {
        PreparedStatement removeTag = session.prepare("""
            UPDATE users SET tags = tags - ? WHERE id = ?
            """);

        Set<String> tagSet = new HashSet<>();
        tagSet.add(tag);

        session.execute(removeTag.bind(tagSet, userId));
    }

    public void deleteOldSessions(int daysOld) {
        PreparedStatement deleteOldSessions = session.prepare("""
            DELETE FROM user_sessions
            WHERE created_at < ?
            ALLOW FILTERING
            """);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        session.execute(deleteOldSessions.bind(cutoffDate));
    }
}
```

## Типы данных

### Примитивные типы

#### Числовые типы
```cql
CREATE TABLE measurements (
    sensor_id UUID,
    timestamp TIMESTAMP,
    temperature INT,           -- Целое число
    humidity FLOAT,            -- Вещественное число
    pressure DOUBLE,           -- Двойная точность
    voltage DECIMAL,           -- Произвольная точность
    reading_count BIGINT,      -- Большое целое
    PRIMARY KEY ((sensor_id), timestamp)
);

-- Вставка данных разных типов
INSERT INTO measurements (sensor_id, timestamp, temperature, humidity, pressure, voltage, reading_count)
VALUES (uuid(), toTimestamp(now()), 25, 65.5, 1013.25, 3.7, 1000000);
```

#### Строковые и временные типы
```cql
CREATE TABLE user_profiles (
    user_id UUID PRIMARY KEY,
    username TEXT,             -- Строка переменной длины
    email TEXT,
    bio TEXT,
    avatar_url TEXT,
    created_at TIMESTAMP,      -- Дата и время
    birth_date DATE,          -- Только дата
    last_login TIME,          -- Только время
    timezone TEXT
);

-- Работа с временными типами
INSERT INTO user_profiles (user_id, username, email, created_at, birth_date, last_login, timezone)
VALUES (
    uuid(),
    'john_doe',
    'john@example.com',
    toTimestamp(now()),
    '1990-05-15',
    '14:30:45.123456789',
    'America/New_York'
);
```

### Коллекции

#### Set (множество)
```cql
CREATE TABLE articles (
    article_id UUID PRIMARY KEY,
    title TEXT,
    content TEXT,
    tags SET<TEXT>,           -- Уникальные теги
    categories SET<TEXT>
);

-- Работа с set
INSERT INTO articles (article_id, title, content, tags, categories)
VALUES (
    uuid(),
    'Cassandra Tutorial',
    'Learn Cassandra...',
    {'cassandra', 'nosql', 'database'},
    {'tutorial', 'technology'}
);

-- Добавление элементов в set
UPDATE articles SET tags = tags + {'java'} WHERE article_id = ?;
UPDATE articles SET tags = tags - {'database'} WHERE article_id = ?;
```

#### List (список)
```cql
CREATE TABLE user_activity (
    user_id UUID,
    activity_date DATE,
    actions LIST<TEXT>,        -- Последовательность действий
    timestamps LIST<TIMESTAMP>,
    PRIMARY KEY ((user_id), activity_date)
) WITH CLUSTERING ORDER BY (activity_date DESC);

-- Работа с list
INSERT INTO user_activity (user_id, activity_date, actions, timestamps)
VALUES (
    uuid(),
    '2024-01-15',
    ['login', 'view_profile', 'edit_settings', 'logout'],
    [toTimestamp(now()), toTimestamp(now()), toTimestamp(now()), toTimestamp(now())]
);

-- Добавление в начало списка
UPDATE user_activity SET actions = ['new_action'] + actions WHERE user_id = ? AND activity_date = ?;

-- Добавление в конец списка
UPDATE user_activity SET actions = actions + ['logout'] WHERE user_id = ? AND activity_date = ?;
```

#### Map (словарь)
```cql
CREATE TABLE products (
    product_id UUID PRIMARY KEY,
    name TEXT,
    price DECIMAL,
    attributes MAP<TEXT, TEXT>,     -- Ключ-значение атрибуты
    specifications MAP<TEXT, TEXT>,
    metadata MAP<TEXT, TEXT>
);

-- Работа с map
INSERT INTO products (product_id, name, price, attributes, specifications)
VALUES (
    uuid(),
    'Laptop',
    999.99,
    {'brand': 'Dell', 'color': 'black', 'warranty': '2 years'},
    {'cpu': 'i7', 'ram': '16GB', 'storage': '512GB SSD'}
);

-- Обновление map
UPDATE products SET attributes = attributes + {'condition': 'new'} WHERE product_id = ?;
UPDATE products SET attributes['color'] = 'silver' WHERE product_id = ?;
```

### Пользовательские типы (UDT)

#### Создание UDT
```cql
-- Создание пользовательского типа для адреса
CREATE TYPE address (
    street TEXT,
    city TEXT,
    state TEXT,
    zip_code TEXT,
    country TEXT
);

-- Создание типа для контактов
CREATE TYPE contact_info (
    email TEXT,
    phone TEXT,
    website TEXT
);

-- Использование UDT в таблице
CREATE TABLE companies (
    company_id UUID PRIMARY KEY,
    name TEXT,
    description TEXT,
    headquarters FROZEN<address>,
    contacts FROZEN<contact_info>,
    offices LIST<FROZEN<address>>
);
```

#### Работа с UDT
```java
// Вставка компании с UDT (адрес, контакты, офисы)
@Service
public class CompanyService {

    @Autowired
    private CqlSession session;

    public void createCompany(Company company) {
        PreparedStatement insertCompany = session.prepare("""
            INSERT INTO companies (company_id, name, description, headquarters, contacts, offices)
            VALUES (?, ?, ?, ?, ?, ?)
            """);

        // Создание UDT значений
        UdtValue headquarters = session.getMetadata()
            .getKeyspace("myapp")
            .flatMap(ks -> ks.getUserDefinedType("address"))
            .map(addressType -> addressType.newValue()
                .setString("street", company.getHeadquarters().getStreet())
                .setString("city", company.getHeadquarters().getCity())
                .setString("state", company.getHeadquarters().getState())
                .setString("zip_code", company.getHeadquarters().getZipCode())
                .setString("country", company.getHeadquarters().getCountry()))
            .orElseThrow();

        UdtValue contacts = session.getMetadata()
            .getKeyspace("myapp")
            .flatMap(ks -> ks.getUserDefinedType("contact_info"))
            .map(contactType -> contactType.newValue()
                .setString("email", company.getContacts().getEmail())
                .setString("phone", company.getContacts().getPhone())
                .setString("website", company.getContacts().getWebsite()))
            .orElseThrow();

        session.execute(insertCompany.bind(
            company.getId(),
            company.getName(),
            company.getDescription(),
            headquarters,
            contacts,
            company.getOffices().stream()
                .map(this::addressToUdtValue)
                .collect(Collectors.toList())
        ));
    }

    private UdtValue addressToUdtValue(Address address) {
        return session.getMetadata()
            .getKeyspace("myapp")
            .flatMap(ks -> ks.getUserDefinedType("address"))
            .map(addressType -> addressType.newValue()
                .setString("street", address.getStreet())
                .setString("city", address.getCity())
                .setString("state", address.getState())
                .setString("zip_code", address.getZipCode())
                .setString("country", address.getCountry()))
            .orElseThrow();
    }
}
```

## Ключи и партиционирование

### Структура первичного ключа

#### Partition Key
```cql
-- Простой partition key
CREATE TABLE users (
    user_id UUID,
    email TEXT,
    name TEXT,
    PRIMARY KEY (user_id)
);

-- Составной partition key
CREATE TABLE user_posts (
    user_id UUID,
    post_id TIMEUUID,
    content TEXT,
    created_at TIMESTAMP,
    PRIMARY KEY ((user_id, post_id))
);
```

#### Clustering Columns
```cql
-- Partition key + clustering columns
CREATE TABLE user_events (
    user_id UUID,
    event_type TEXT,
    event_id TIMEUUID,
    data TEXT,
    timestamp TIMESTAMP,
    PRIMARY KEY ((user_id, event_type), event_id, timestamp)
) WITH CLUSTERING ORDER BY (event_id DESC, timestamp DESC);

-- Запросы по clustering columns
SELECT * FROM user_events WHERE user_id = ? AND event_type = ?;
SELECT * FROM user_events WHERE user_id = ? AND event_type = ? AND event_id > ?;
```

### Стратегии партиционирования

#### Partition Key выбор
```java
// Примеры выбора ключа партиции для разных сценариев
@Service
public class PartitioningStrategyService {

    // Плохой выбор - низкая кардинальность
    // PRIMARY KEY ((active), user_id) - все активные пользователи в одной партиции

    // Хороший выбор - высокая кардинальность
    // PRIMARY KEY (user_id) - равномерное распределение

    // Оптимальный выбор - составной ключ
    // PRIMARY KEY ((user_id, date), timestamp) - партиции по пользователю и дате

    public void demonstratePartitioningStrategies() {
        // Пример хорошего партиционирования для временных рядов
        String timeSeriesTable = """
            CREATE TABLE sensor_readings (
                sensor_id UUID,
                date DATE,
                timestamp TIMESTAMP,
                temperature DOUBLE,
                humidity DOUBLE,
                PRIMARY KEY ((sensor_id, date), timestamp)
            ) WITH CLUSTERING ORDER BY (timestamp DESC)
            """;

        // Пример партиционирования для геоданных
        String geoTable = """
            CREATE TABLE location_events (
                geohash TEXT,
                event_id TIMEUUID,
                user_id UUID,
                latitude DOUBLE,
                longitude DOUBLE,
                event_type TEXT,
                PRIMARY KEY ((geohash), event_id)
            ) WITH CLUSTERING ORDER BY (event_id DESC)
            """;
    }
}
```

## Консистентность и репликация

### Уровни консистентности

#### Write Consistency
```java
// Демонстрация уровней консистентности записи: ANY, ONE, QUORUM, ALL
@Service
public class ConsistencyService {

    @Autowired
    private CqlSession session;

    public void demonstrateConsistencyLevels() {
        // ANY - минимум 1 узел (быстро, но риск потери)
        SimpleStatement anyWrite = SimpleStatement.newInstance("""
            INSERT INTO user_logs (user_id, log_id, message, timestamp)
            VALUES (?, now(), ?, toTimestamp(now()))
            """).setConsistencyLevel(DefaultConsistencyLevel.ANY);

        // ONE - минимум 1 узел подтверждает
        SimpleStatement oneWrite = SimpleStatement.newInstance("""
            INSERT INTO user_preferences (user_id, preferences)
            VALUES (?, ?)
            """).setConsistencyLevel(DefaultConsistencyLevel.ONE);

        // QUORUM - большинство узлов в датацентре
        SimpleStatement quorumWrite = SimpleStatement.newInstance("""
            INSERT INTO orders (order_id, user_id, total_amount, status)
            VALUES (?, ?, ?, 'pending')
            """).setConsistencyLevel(DefaultConsistencyLevel.QUORUM);

        // ALL - все реплики подтверждают
        SimpleStatement allWrite = SimpleStatement.newInstance("""
            INSERT INTO financial_transactions (tx_id, account_id, amount, type)
            VALUES (?, ?, ?, ?)
            """).setConsistencyLevel(DefaultConsistencyLevel.ALL);
    }
}
```

#### Read Consistency
```java
// Уровни консистентности чтения: ONE, QUORUM, LOCAL_QUORUM
@Service
public class ReadConsistencyService {

    public void demonstrateReadConsistency() {
        // ONE - быстрый, но возможны устаревшие данные
        SimpleStatement oneRead = SimpleStatement.newInstance("""
            SELECT balance FROM accounts WHERE account_id = ?
            """).setConsistencyLevel(DefaultConsistencyLevel.ONE);

        // QUORUM - баланс скорости и консистентности
        SimpleStatement quorumRead = SimpleStatement.newInstance("""
            SELECT * FROM orders WHERE user_id = ? LIMIT 10
            """).setConsistencyLevel(DefaultConsistencyLevel.QUORUM);

        // ALL - максимальная консистентность
        SimpleStatement allRead = SimpleStatement.newInstance("""
            SELECT * FROM system_settings WHERE key = ?
            """).setConsistencyLevel(DefaultConsistencyLevel.ALL);

        // LOCAL_QUORUM - только локальный датацентр
        SimpleStatement localQuorum = SimpleStatement.newInstance("""
            SELECT * FROM user_profiles WHERE user_id = ?
            """).setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM);
    }
}
```

### Репликация между датацентрами

#### NetworkTopologyStrategy
```cql
-- Репликация между датацентрами
CREATE KEYSPACE ecommerce
WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'dc1': 3,      -- 3 реплики в dc1
  'dc2': 2,      -- 2 реплики в dc2
  'dc3': 1       -- 1 реплика в dc3
};

-- Для критически важных данных
CREATE KEYSPACE financial
WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'dc1': 5,
  'dc2': 5,
  'dc3': 3
};
```

#### Локальная консистентность
```java
// Консистентность в мульти-датацентровом кластере: LOCAL_ONE, LOCAL_QUORUM, EACH_QUORUM
@Service
public class MultiDcService {

    @Autowired
    private CqlSession session;

    public void demonstrateLocalConsistency() {
        // LOCAL_ONE - только локальный датацентр
        SimpleStatement localOne = SimpleStatement.newInstance("""
            SELECT * FROM user_cache WHERE user_id = ?
            """).setConsistencyLevel(DefaultConsistencyLevel.LOCAL_ONE);

        // LOCAL_QUORUM - кворум в локальном датацентре
        SimpleStatement localQuorum = SimpleStatement.newInstance("""
            SELECT * FROM products WHERE category = ? ALLOW FILTERING
            """).setConsistencyLevel(DefaultConsistencyLevel.LOCAL_QUORUM);

        // EACH_QUORUM - кворум в каждом датацентре
        SimpleStatement eachQuorum = SimpleStatement.newInstance("""
            SELECT balance FROM accounts WHERE account_id = ?
            """).setConsistencyLevel(DefaultConsistencyLevel.EACH_QUORUM);
    }

    public void handleNetworkPartition() {
        // Во время сетевых проблем использовать локальную консистентность
        SimpleStatement degradedRead = SimpleStatement.newInstance("""
            SELECT * FROM emergency_data
            """).setConsistencyLevel(DefaultConsistencyLevel.LOCAL_ONE);

        // Для критичных операций требовать кворум
        SimpleStatement criticalOperation = SimpleStatement.newInstance("""
            UPDATE system_status SET status = 'maintenance'
            """).setConsistencyLevel(DefaultConsistencyLevel.QUORUM);
    }
}
```

## Spring Data Cassandra

### Entity маппинг

#### Базовые entity
```java
// Сущность пользователя и составной ключ для постов
@Table("users")
public class User {

    @PrimaryKey
    private UUID id;

    @Column("email")
    private String email;

    @Column("name")
    private String name;

    @Column("age")
    private Integer age;

    @Column("city")
    private String city;

    @Column("active")
    private Boolean active;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("preferences")
    private Map<String, String> preferences;

    @Column("tags")
    private Set<String> tags;

    // Constructors, getters, setters
}

@PrimaryKeyClass
public class UserPostKey implements Serializable {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID userId;

    @PrimaryKeyColumn(name = "post_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID postId;

    // Constructors, getters, setters
}

@Table("user_posts")
public class UserPost {

    @PrimaryKey
    private UserPostKey key;

    @Column("content")
    private String content;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("likes")
    private Integer likes;

    @Column("tags")
    private Set<String> tags;

    // Constructors, getters, setters
}
```

#### Репозитории
```java
// Репозиторий и сервис для работы с пользователями через Spring Data
@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {

    // Автоматические методы
    Optional<User> findById(UUID id);
    List<User> findAll();
    List<User> findAllById(Iterable<UUID> ids);
    void deleteById(UUID id);
    boolean existsById(UUID id);

    // Кастомные методы
    @AllowFiltering
    List<User> findByAgeGreaterThan(Integer age);

    @AllowFiltering
    List<User> findByCityAndActive(String city, Boolean active);

    @Query("SELECT * FROM users WHERE email = ?0 ALLOW FILTERING")
    Optional<User> findByEmail(String email);

    @Query("SELECT * FROM users WHERE name LIKE ?0 ALLOW FILTERING")
    List<User> findByNameLike(String namePattern);

    // С paging
    Slice<User> findAll(Pageable pageable);

    @AllowFiltering
    Slice<User> findByCity(String city, Pageable pageable);
}

@Repository
public interface UserPostRepository extends CassandraRepository<UserPost, UserPostKey> {

    // Поиск по partition key
    List<UserPost> findByKeyUserId(UUID userId);

    // Поиск по clustering key
    List<UserPost> findByKeyUserIdAndKeyPostIdGreaterThan(UUID userId, UUID postId);

    // Сортировка
    List<UserPost> findByKeyUserIdOrderByKeyPostIdDesc(UUID userId);

    // Кастомные запросы
    @Query("SELECT * FROM user_posts WHERE user_id = ?0 LIMIT ?1")
    List<UserPost> findRecentPosts(UUID userId, int limit);

    @AllowFiltering
    List<UserPost> findByTagsContains(String tag);
}
```

### Сервисы

#### CRUD операции
```java
// CRUD для пользователей и постов через репозиторий и шаблон
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CassandraOperations cassandraOperations;

    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setCity(request.getCity());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setPreferences(new HashMap<>());
        user.setTags(new HashSet<>());

        return userRepository.save(user);
    }

    public Optional<User> getUser(UUID id) {
        return userRepository.findById(id);
    }

    public List<User> getUsersByCity(String city) {
        return userRepository.findByCityAndActive(city, true);
    }

    public User updateUser(UUID id, UpdateUserRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found: " + id);
        }

        User user = optionalUser.get();
        user.setName(request.getName());
        user.setAge(request.getAge());
        user.setCity(request.getCity());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    public void addUserTag(UUID userId, String tag) {
        cassandraOperations.execute("UPDATE users SET tags = tags + ? WHERE id = ?",
            Collections.singleton(tag), userId);
    }

    public void updateUserPreferences(UUID userId, Map<String, String> preferences) {
        cassandraOperations.execute("UPDATE users SET preferences = preferences + ? WHERE id = ?",
            preferences, userId);
    }
}
```

## Мониторинг и диагностика

### Nodetool команды

#### Проверка состояния кластера
```bash
# Статус кластера
nodetool status

# Детальная информация об узле
nodetool info

# Описание кластера
nodetool describecluster

# Проверка потоков
nodetool tpstats

# Статистика кэша
nodetool info | grep -A 10 "Cache"
```

#### Диагностика проблем
```bash
# Проверка соединений
nodetool netstats

# Статистика таблиц
nodetool tablestats

# Проверка compaction
nodetool compactionstats

# Очистка snapshots
nodetool listsnapshots
nodetool clearsnapshot
```

### CQLSH диагностика

#### Системные таблицы
```cql
-- Информация о кластере
SELECT * FROM system.local;

-- Информация о пирах
SELECT * FROM system.peers;

-- Статистика запросов (C* 4.0+)
SELECT * FROM system_views.query_metrics;

-- Логи медленных запросов
SELECT * FROM system_traces.events LIMIT 10;
```

#### Диагностические запросы
```cql
-- Проверка схемы
DESCRIBE KEYSPACES;
DESCRIBE TABLES;
DESCRIBE TABLE users;

-- Статистика по таблицам
SELECT keyspace_name, table_name, memtable_data_size, sstable_count
FROM system_schema.table_stats;

-- Проверка репликации
SELECT * FROM system_schema.keyspaces WHERE keyspace_name = 'myapp';
```

### Java диагностика

```java
// Проверка состояния кластера и статистики таблиц
@Service
public class CassandraMonitoringService {

    @Autowired
    private CqlSession session;

    public ClusterHealth checkClusterHealth() {
        // Проверка доступности узлов
        ResultSet localRs = session.execute("SELECT cluster_name, data_center, rack FROM system.local");
        ResultSet peersRs = session.execute("SELECT peer, data_center, rack FROM system.peers");

        List<NodeInfo> nodes = new ArrayList<>();

        // Локальный узел
        Row localRow = localRs.one();
        nodes.add(new NodeInfo(
            "local",
            localRow.getString("cluster_name"),
            localRow.getString("data_center"),
            localRow.getString("rack"),
            true
        ));

        // Пиры
        for (Row peerRow : peersRs) {
            nodes.add(new NodeInfo(
                peerRow.getInetAddress("peer").toString(),
                null, // cluster name одинаковый
                peerRow.getString("data_center"),
                peerRow.getString("rack"),
                true // Предполагаем доступность
            ));
        }

        return new ClusterHealth(nodes.size(), nodes);
    }

    public Map<String, Object> getTableStatistics(String keyspace, String table) {
        ResultSet rs = session.execute("""
            SELECT keyspace_name, table_name, memtable_data_size,
                   sstable_count, bloom_filter_false_positives,
                   bloom_filter_false_ratio
            FROM system_schema.table_stats
            WHERE keyspace_name = ? AND table_name = ?
            """, keyspace, table);

        if (rs.hasNext()) {
            Row row = rs.one();
            Map<String, Object> stats = new HashMap<>();
            stats.put("memtableDataSize", row.getLong("memtable_data_size"));
            stats.put("sstableCount", row.getLong("sstable_count"));
            stats.put("bloomFilterFalsePositives", row.getLong("bloom_filter_false_positives"));
            stats.put("bloomFilterFalseRatio", row.getDouble("bloom_filter_false_ratio"));
            return stats;
        }

        return new HashMap<>();
    }

    public List<QueryLatencyInfo> getQueryLatencyStats() {
        // В Cassandra 4.0+ можно получить статистику через JMX или system_views
        // Для демонстрации используем простой подход

        List<QueryLatencyInfo> latencies = new ArrayList<>();

        // Имитация получения данных о latency
        // В реальности это можно получить через JMX или system_views

        return latencies;
    }

    public void enableTracing(UUID requestId) {
        // Включение трассировки для диагностики
        session.execute("TRACING ON");

        // Выполнение запроса с трассировкой
        ResultSet rs = session.execute("SELECT * FROM users WHERE id = ?", requestId);

        // Получение трассировки
        ResultSet traceRs = session.execute("SELECT * FROM system_traces.events " +
            "WHERE session_id = (SELECT session_id FROM system_traces.sessions LIMIT 1) " +
            "ALLOW FILTERING");

        for (Row traceRow : traceRs) {
            System.out.println("Trace: " + traceRow.getString("activity"));
        }

        session.execute("TRACING OFF");
    }
}

// Вспомогательные классы
class ClusterHealth {
    private int nodeCount;
    private List<NodeInfo> nodes;
    // constructor, getters, setters
}

class NodeInfo {
    private String address;
    private String clusterName;
    private String dataCenter;
    private String rack;
    private boolean available;
    // constructor, getters, setters
}

class QueryLatencyInfo {
    private String queryType;
    private double avgLatencyMs;
    private double maxLatencyMs;
    // constructor, getters, setters
}
```

## Лучшие практики

### Проектирование схемы

#### 1. Правильный выбор partition key
```cql
-- Хорошо: Высокая кардинальность, равномерное распределение
CREATE TABLE user_events (
    user_id UUID,
    event_id TIMEUUID,
    event_type TEXT,
    data TEXT,
    timestamp TIMESTAMP,
    PRIMARY KEY ((user_id), event_id, timestamp)
);

-- Плохо: Низкая кардинальность, все данные в одной партиции
CREATE TABLE system_config (
    config_key TEXT PRIMARY KEY,
    config_value TEXT
);
```

#### 2. Оптимизация для паттернов запросов
```cql
-- Если нужны запросы по email, создать отдельную таблицу
CREATE TABLE users_by_email (
    email TEXT PRIMARY KEY,
    user_id UUID
);

-- Заполнение через batch
BEGIN BATCH
    INSERT INTO users (id, email, name) VALUES (?, ?, ?);
    INSERT INTO users_by_email (email, user_id) VALUES (?, ?);
APPLY BATCH;
```

#### 3. Избегать hot partitions
```cql
-- Плохо: Все заказы за сегодня в одной партиции
CREATE TABLE daily_orders (
    date DATE,
    order_id TIMEUUID,
    data TEXT,
    PRIMARY KEY ((date), order_id)
);

-- Хорошо: Распределение по пользователям
CREATE TABLE user_orders (
    user_id UUID,
    order_date DATE,
    order_id TIMEUUID,
    data TEXT,
    PRIMARY KEY ((user_id, order_date), order_id)
);
```

### Управление данными

#### 1. Использование TTL для временных данных
```cql
-- Автоматическое удаление сессий через 24 часа
INSERT INTO user_sessions (session_id, user_id, data)
VALUES (?, ?, ?) USING TTL 86400;

-- Продление сессии
UPDATE user_sessions USING TTL 86400
SET last_access = toTimestamp(now())
WHERE session_id = ?;
```

#### 2. Компактификация и обслуживание
```bash
# Ручная компактификация (обычно не нужно)
nodetool compact myapp users

# Очистка после удалений
nodetool garbagecollect myapp users

# Проверка уровня сжатия
nodetool tablehistograms myapp users
```

#### 3. Резервное копирование
```bash
# Создание snapshot
nodetool snapshot myapp

# Инкрементальное бэкап с Medusa
medusa backup --name daily_backup

# Восстановление
medusa restore --backup-name daily_backup
```

### Производительность

#### 1. Оптимизация запросов
```cql
-- Избегать ALLOW FILTERING
SELECT * FROM users WHERE city = 'New York' ALLOW FILTERING; -- Плохо

-- Использовать правильные индексы или денормализацию
CREATE TABLE users_by_city (
    city TEXT,
    user_id UUID,
    name TEXT,
    PRIMARY KEY ((city), user_id)
);
```

#### 2. Настройка консистентности
```java
// Для некритичных данных - более слабая консистентность
SimpleStatement fastRead = SimpleStatement.newInstance("""
    SELECT * FROM user_cache WHERE user_id = ?
    """).setConsistencyLevel(DefaultConsistencyLevel.ONE);

// Для важных данных - более сильная консистентность
SimpleStatement criticalRead = SimpleStatement.newInstance("""
    SELECT balance FROM accounts WHERE account_id = ?
    """).setConsistencyLevel(DefaultConsistencyLevel.QUORUM);
```

#### 3. Пакетная обработка
```java
// Пакетная вставка событий через UNLOGGED batch
@Service
public class BatchProcessingService {

    @Autowired
    private CqlSession session;

    public void processBatch(List<UserEvent> events) {
        PreparedStatement insertEvent = session.prepare("""
            INSERT INTO user_events (user_id, event_id, event_type, data, timestamp)
            VALUES (?, now(), ?, ?, toTimestamp(now()))
            """);

        // Unlogged batch для производительности
        BatchStatement batch = BatchStatement.newInstance(BatchType.UNLOGGED);

        for (UserEvent event : events) {
            batch.add(insertEvent.bind(
                event.getUserId(),
                event.getEventType(),
                event.getData()
            ));
        }

        session.execute(batch);
    }
}
```

### Мониторинг

#### 1. Ключевые метрики
- **Latency**: Время отклика запросов
- **Throughput**: Количество операций в секунду
- **Consistency level**: Уровень консистентности
- **Replication factor**: Фактор репликации

#### 2. Алерты
```sql
-- Создание таблицы для алертов
CREATE TABLE system_alerts (
    alert_id TIMEUUID PRIMARY KEY,
    alert_type TEXT,
    severity TEXT,
    message TEXT,
    node_address TEXT,
    timestamp TIMESTAMP
);

-- Пример алерта на высокую latency
INSERT INTO system_alerts (alert_id, alert_type, severity, message, node_address, timestamp)
VALUES (now(), 'latency', 'warning', 'Read latency > 100ms', 'node1', toTimestamp(now()));
```

#### 3. Регулярные проверки
```bash
# Ежедневные проверки
#!/bin/bash
# cassandra_daily_check.sh

# Проверка состояния кластера
nodetool status

# Проверка логов на ошибки
grep "ERROR\|WARN" /var/log/cassandra/system.log | tail -20

# Проверка дискового пространства
df -h | grep cassandra

# Проверка репликации
nodetool describecluster
```

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Запрос по неключевому столбцу медленный или не работает | Полный scan партиции или необходимость allow filtering | Спроектировать таблицу под запрос (partition key + clustering); избегать allow filtering на больших объёмах |
| UnavailableException, таймауты записи/чтения | Недостаточно реплик для уровня консистентности (QUORUM и т.д.) | Увеличить RF или снизить CL для операции; проверить состояние узлов (nodetool status) |
| Кластер не сходится после изменений | Неверный порядок или сбой при repair/rebuild | Проверить gossip, выполнить repair; при добавлении узла — правильный bootstrap и tokens |

## Частые вопросы

**Почему в Cassandra нет JOIN и подзапросов?** Модель ориентирована на партиционирование и денормализацию: каждая таблица под конкретный запрос, данные дублируются. JOIN потребовал бы cross-node запросов и убил бы производительность.

**Как выбрать partition key?** Ключ должен равномерно распределять данные и соответствовать запросам (часто запрос по partition key). Избегать слишком больших партиций (миллионы строк); при необходимости разбивать по времени или категории.

**Когда использовать QUORUM, когда ONE?** QUORUM — когда важна консистентность чтения после записи. ONE — для низкой латентности при допустимой eventual consistency. Для записи часто QUORUM, для чтения — по сценарию.

## Заключение

**Apache Cassandra** — мощная распределенная **NoSQL** база данных, идеально подходящая для высоконагруженных систем, требующих высокой доступности и масштабируемости. Ключевые особенности:**

### Преимущества Cassandra:

1. **Высокая производительность** — обработка миллионов операций в секунду
2. **Линейная масштабируемость** — добавление узлов без простоев
3. **Отказоустойчивость** — работа при отказе нескольких узлов
4. **Гибкая модель данных** — поддержка сложных структур
5. **Геораспределенность** — датацентры по всему миру

### Архитектурные особенности:

- **Peer-`to-peer` архитектура** — все узлы равны
- **Consistent hashing** — равномерное распределение данных
- **Настраиваемая консистентность** — баланс между скоростью и надежностью
- **CQL** — знакомый **SQL**-подобный язык запросов
- **Мощные возможности индексации** — вторичные индексы, материализованные представления

### Java интеграция:

- **DataStax `Java` Driver** — низкоуровневый драйвер
- **Spring `Data` Cassandra** — высокоуровневая абстракция
- **Reactive drivers** — асинхронная обработка
- **Object mapping** — автоматическое преобразование объектов

### Когда выбирать Cassandra:

- **Big `Data` приложения** с петабайтами данных
- **Глобальные системы** с пользователями по всему миру
- **Высоконагруженные OLTP** системы
- **Time-series данные** — логи, метрики, события
- **IoT платформы** с множеством устройств

### Лучшие практики:

1. **Правильное проектирование схемы** — оптимальные **partition keys**
2. **Выбор уровней консистентности** — баланс скорости и надежности
3. **Мониторинг и обслуживание** — регулярные проверки состояния
4. **Резервное копирование** — стратегия восстановления
5. **Масштабирование** — планирование роста кластера

**Cassandra** — это зрелая, проверенная временем технология, используемая крупнейшими компаниями мира для решения самых сложных задач хранения и обработки данных.

**Следующие темы:**
- [[cassandra-data-modeling]] — моделирование данных в **Cassandra**
- [[cassandra-queries]] — **CQL** запросы и оптимизация
- [[cassandra-clustering]] — кластеризация и масштабирование
- [[cassandra-performance]] — производительность и тюнинг
- [[cassandra-admin]] — администрирование **Cassandra**

Правильное использование **Cassandra** требует глубокого понимания распределенных систем и особенностей модели данных! 🚀

