# MongoDB: Основы документо-ориентированной NoSQL базы данных

Комплексное руководство по основам MongoDB: архитектура, установка, основные концепции, документы и BSON.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [MongoDB Manual](https://docs.mongodb.com/manual/)
- [MongoDB Getting Started](https://docs.mongodb.com/manual/tutorial/getting-started/)
- [MongoDB Installation](https://docs.mongodb.com/manual/installation/)

### Baeldung
- [Introduction to MongoDB](https://www.baeldung.com/mongodb)
- [MongoDB with Spring Boot](https://www.baeldung.com/spring-data-mongodb-tutorial)

### См. также
- `databases/postgres-basics.md` - Сравнение с реляционными базами данных
- `databases/redis-basics.md` - Другая NoSQL база данных

## Содержание

- [Введение в MongoDB](#введение-в-mongodb)
- [Установка и настройка MongoDB](#установка-и-настройка-mongodb)
- [Основные концепции MongoDB](#основные-концепции-mongodb)
- [Работа с MongoDB Shell](#работа-с-mongodb-shell)
- [Работа с базами данных](#работа-с-базами-данных)
- [Коллекции и документы](#коллекции-и-документы)
- [Структура данных](#структура-данных)
- [MongoDB Compass](#mongodb-compass)
- [Best Practices для MongoDB](#best-practices-для-mongodb)
- [Заключение](#заключение)

## Введение в MongoDB

**MongoDB** — это высокопроизводительная, документо-ориентированная NoSQL база данных с открытым исходным кодом. MongoDB хранит данные в формате BSON (Binary JSON) и обеспечивает гибкую схему данных, горизонтальную масштабируемость и богатый набор функций для работы с данными.

### Почему MongoDB?

MongoDB была разработана для решения проблем традиционных реляционных баз данных в современных приложениях:

1. **Гибкая схема данных** - документы могут иметь разную структуру
2. **Горизонтальная масштабируемость** - шардирование для обработки больших объемов данных
3. **Высокая производительность** - оптимизированная для чтения и записи
4. **Разработан для JSON/BSON** - естественная интеграция с современными приложениями
5. **Rich Query Language** - мощные возможности для запросов и агрегаций

### Архитектура MongoDB

```
┌─────────────────────────────────────────────────────────────┐
│                          MongoDB                            │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │                Application Layer                   │    │
│  │  • Drivers (Java, Python, Node.js, etc.)           │    │
│  │  • ODM Libraries (Spring Data, Mongoose, etc.)     │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │               MongoDB Server                        │    │
│  │  • mongod (Database Server)                        │    │
│  │  • mongos (Query Router)                           │    │
│  │  • config servers                                  │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Storage Layer                          │    │
│  │  • WiredTiger Storage Engine                       │    │
│  │  • MMAPv1 (legacy)                                 │    │
│  │  • In-Memory                                        │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │               Operating System                      │    │
│  │  • Filesystem (XFS, EXT4)                          │    │
│  │  • Memory Management                               │    │
│  │  • I/O Scheduling                                  │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### Основные характеристики MongoDB

- **Документо-ориентированная модель**: Данные хранятся в виде JSON-подобных документов
- **Гибкая схема**: Не требует предварительного определения структуры данных
- **Горизонтальная масштабируемость**: Поддержка шардирования для распределения данных
- **Высокая производительность**: Оптимизированные индексы и in-memory операции
- **Репликация**: Автоматическое копирование данных для отказоустойчивости
- **Мощный aggregation framework**: Для сложных запросов и аналитики
- **Поддержка транзакций**: ACID транзакции для нескольких документов
- **Rich indexing**: B-tree, geospatial, text, hash индексы

### Варианты использования MongoDB

#### Content Management Systems
```javascript
// Хранение контента с метаданными
{
  _id: ObjectId("..."),
  title: "My Blog Post",
  content: "Full text content...",
  author: "John Doe",
  tags: ["mongodb", "nosql", "tutorial"],
  publishedAt: ISODate("2023-01-01T00:00:00Z"),
  metadata: {
    wordCount: 1250,
    readingTime: 5,
    category: "Technology"
  }
}
```

#### IoT и Big Data
```javascript
// Сенсорные данные
{
  sensorId: "temp_sensor_001",
  timestamp: ISODate("2023-01-01T12:00:00Z"),
  location: {
    type: "Point",
    coordinates: [37.7749, -122.4194]
  },
  readings: {
    temperature: 23.5,
    humidity: 65.2,
    pressure: 1013.25
  },
  battery: 85.3
}
```

#### E-commerce каталоги
```javascript
// Продукты с вариативными атрибутами
{
  _id: ObjectId("..."),
  name: "Wireless Headphones",
  brand: "TechBrand",
  category: "Electronics",
  price: {
    base: 199.99,
    currency: "USD",
    discount: 0.1
  },
  variants: [
    { color: "Black", size: "S", stock: 15 },
    { color: "White", size: "M", stock: 8 },
    { color: "Blue", size: "L", stock: 12 }
  ],
  specifications: {
    "Battery Life": "30 hours",
    "Connectivity": "Bluetooth 5.0",
    "Weight": "250g"
  },
  reviews: [
    {
      userId: ObjectId("..."),
      rating: 5,
      comment: "Great sound quality!",
      createdAt: ISODate("2023-01-15T00:00:00Z")
    }
  ]
}
```

### Сравнение с реляционными базами данных

| Аспект | MongoDB | Реляционные СУБД |
|--------|---------|-------------------|
| **Модель данных** | Документы (BSON) | Таблицы с фиксированной схемой |
| **Схема** | Гибкая (schema-less) | Строгая схема |
| **Запросы** | JSON-подобные | SQL |
| **Масштабируемость** | Горизонтальная | Вертикальная |
| **Транзакции** | Много-документные (с 4.0) | ACID по умолчанию |
| **JOIN** | $lookup в aggregation | Встроенные JOIN |
| **Индексы** | B-tree, geospatial, text | B-tree, hash, etc. |
| **ACID** | Да (с ограничениями) | Да |
| **Консистентность** | Eventual consistency | Immediate consistency |

### Преимущества MongoDB

1. **Гибкость схемы**: Легко адаптировать структуру данных без миграций
2. **Масштабируемость**: Горизонтальное масштабирование через шардирование
3. **Производительность**: Быстрые чтение/запись для больших объемов данных
4. **Разработчик-friendly**: JSON-подобный формат данных
5. **Rich Query Language**: Мощные возможности для запросов и агрегаций
6. **Community и Ecosystem**: Большое сообщество и множество инструментов

### Недостатки MongoDB

1. **ACID транзакции**: Ограниченная поддержка для сложных транзакций (до версии 4.0)
2. **JOIN операции**: Отсутствие встроенных JOIN, требуется денормализация
3. **Потребление памяти**: Высокое потребление RAM для индексов
4. **Согласованность**: Eventual consistency в распределенных системах
5. **Learning curve**: Новый подход к моделированию данных

## Установка и настройка MongoDB

### Системные требования

#### Hardware требования
- **RAM**: Минимум 4GB, рекомендуется 8GB+
- **CPU**: 2+ ядер, рекомендуется 4+ ядер
- **Disk**: 10GB+ свободного места, SSD рекомендуется
- **Network**: Стабильное сетевое подключение

#### Software требования
- **Операционная система**: Linux, macOS, Windows
- **Filesystem**: XFS, EXT4, NTFS
- **Dependencies**: None (MongoDB включает все необходимое)

### Установка на Ubuntu/Debian

```bash
# Обновление системы
sudo apt update && sudo apt upgrade -y

# Импорт GPG ключа MongoDB
curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | \
   sudo gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg --dearmor

# Добавление репозитория MongoDB
echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list

# Обновление пакетов и установка MongoDB
sudo apt-get update
sudo apt-get install -y mongodb-org

# Создание директорий для данных и логов
sudo mkdir -p /data/db /var/log/mongodb
sudo chown -R mongodb:mongodb /data/db /var/log/mongodb

# Запуск MongoDB
sudo systemctl start mongod
sudo systemctl enable mongod

# Проверка статуса
sudo systemctl status mongod

# Проверка установки
mongod --version
mongo --version
```

### Установка на CentOS/RHEL

```bash
# Создание файла репозитория
cat > /etc/yum.repos.d/mongodb-org-7.0.repo << 'EOF'
[mongodb-org-7.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/7.0/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-7.0.asc
EOF

# Установка MongoDB
sudo yum install -y mongodb-org

# Создание директорий
sudo mkdir -p /data/db /var/log/mongodb
sudo chown -R mongod:mongod /data/db /var/log/mongodb

# Запуск сервиса
sudo systemctl start mongod
sudo systemctl enable mongod

# Проверка
mongod --version
```

### Установка на macOS

```bash
# Используя Homebrew
brew tap mongodb/brew
brew install mongodb-community

# Создание директорий
sudo mkdir -p /data/db /var/log/mongodb
sudo chown -R $(whoami) /data/db /var/log/mongodb

# Запуск MongoDB
brew services start mongodb-community

# Или запуск вручную
mongod --dbpath /data/db --logpath /var/log/mongodb.log
```

### Установка через Docker

```bash
# Запуск MongoDB в Docker
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -v mongodb_data:/data/db \
  -v mongodb_config:/data/configdb \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=securepassword \
  mongo:7.0

# Проверка
docker ps
docker logs mongodb

# Подключение к контейнеру
docker exec -it mongodb mongo -u admin -p securepassword
```

### Конфигурационный файл MongoDB

**Файл mongod.conf** — это основной конфигурационный файл MongoDB в формате YAML. Он определяет все аспекты поведения сервера MongoDB: хранение данных, сеть, безопасность, репликацию и производительность.

**Расположение файла:**
- **Linux**: `/etc/mongod.conf`
- **macOS**: `/usr/local/etc/mongod.conf`
- **Docker**: Монтируется как volume
- **Windows**: `C:\Program Files\MongoDB\Server\{version}\bin\mongod.cfg`

**Структура конфигурационного файла:**

```yaml
# ========================================
# MongoDB Configuration File
# ========================================

# СИСТЕМНЫЕ НАСТРОЙКИ
# Настройки логирования системы
systemLog:
  # Куда писать логи: file или syslog
  destination: file
  # Путь к файлу логов
  path: /var/log/mongodb/mongod.log
  # Режим записи: append (дописывать) или overwrite (перезаписывать)
  logAppend: true
  # Формат timestamp в логах
  timeStampFormat: iso8601-local
  # Ротация логов: rename или reopen
  logRotate: reopen
  # Уровень детализации логирования
  verbosity: 0
  # Максимальный размер лога перед ротацией
  maxLogSizeKB: 10240

# УПРАВЛЕНИЕ ПРОЦЕССОМ
# Настройки управления процессом MongoDB
processManagement:
  # Запускать в фоне (как демон)
  fork: true
  # Путь к PID файлу
  pidFilePath: /var/run/mongodb/mongod.pid
  # Время ожидания graceful shutdown в миллисекундах
  shutdownTimeoutMillis: 15000

# СЕТЕВЫЕ НАСТРОЙКИ
# Конфигурация сетевых интерфейсов и протоколов
net:
  # IP адреса для привязки (0.0.0.0 - все интерфейсы)
  bindIp: 127.0.0.1,192.168.1.100
  # IPv6 поддержка
  ipv6: false
  # Порт MongoDB (стандартный 27017)
  port: 27017
  # Максимальное количество одновременных подключений
  maxIncomingConnections: 65536
  # Wire protocol сжатие для уменьшения сетевого трафика
  compression:
    # Доступные компрессоры: snappy, zlib, zstd
    compressors: snappy,zlib

# НАСТРОЙКИ ХРАНЕНИЯ ДАННЫХ
# Конфигурация подсистемы хранения
storage:
  # Директория для хранения данных
  dbPath: /data/db
  # Настройки journal (WAL - Write Ahead Log)
  journal:
    # Включить journal для durability
    enabled: true
    # Интервал commit в миллисекундах
    commitIntervalMs: 100
  # Storage engine (WiredTiger по умолчанию)
  engine: wiredTiger
  # Специфические настройки WiredTiger
  wiredTiger:
    engineConfig:
      # Размер кэша в GB (рекомендуется 50-80% от RAM)
      cacheSizeGB: 2
      # Размер overflow кэша
      maxCacheOverflowSizeGB: 0.5
      # Директория для индексов
      directoryForIndexes: true
    collectionConfig:
      # Компрессор для коллекций
      blockCompressor: snappy
    indexConfig:
      # Сжатие префиксов в индексах
      prefixCompression: true
  # Настройки для директорий
  directoryPerDB: true
  # Sync период для данных на диск
  syncPeriodSecs: 60

# НАСТРОЙКИ РЕПЛИКАЦИИ
# Конфигурация для replica sets
replication:
  # Имя replica set (обязательно для репликации)
  replSetName: rs0
  # Роль в кластере (по умолчанию normal)
  replSetMemberState: normal
  # Опции для secondary nodes
  secondaryIndexPrefetch: all
  # Read concern настройки
  enableMajorityReadConcern: true
  # Настройки oplog
  oplogSizeMB: 1024

# НАСТРОЙКИ БЕЗОПАСНОСТИ
# Конфигурация аутентификации и авторизации
security:
  # Включить аутентификацию пользователей
  authorization: enabled
  # Кластеризация с keyfile аутентификацией
  keyFile: /etc/mongo/keyfile
  # Шифрование кластерного трафика
  clusterAuthMode: keyFile
  # Шифрование данных в покое
  encryptionCipherMode: AES256-CBC
  # Ограничение JavaScript выполнения
  javascriptEnabled: false
  # Redact чувствительные данные в логах
  redactClientLogData: true

# НАСТРОЙКИ ШАРДИРОВАНИЯ
# Конфигурация для шардированных кластеров
sharding:
  # Роль в кластере: configsvr, shardsvr, или none
  clusterRole: none
  # Archive перемещенные chunks
  archiveMovedChunks: true

# НАСТРОЙКИ ОПЕРАЦИЙ
# Конфигурация различных операций БД
operationProfiling:
  # Режим профилирования: off, slowOp, all
  mode: slowOp
  # Порог для медленных операций в миллисекундах
  slowOpThresholdMs: 100
  # Порог для sample rate
  slowOpSampleRate: 1.0

# НАСТРОЙКИ АУДИТА
# Логирование операций для compliance
auditLog:
  # Куда писать аудит логи
  destination: file
  # Путь к файлу аудит логов
  path: /var/log/mongodb/audit.log
  # Формат: JSON или BSON
  format: JSON
  # Фильтр для аудита (JSON)
  filter: '{ "users": { "$in": ["admin", "root"] } }'

# ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ
# Прочие параметры для тонкой настройки
setParameter:
  # Максимальный размер aggregation pipeline cache
  wiredTigerMaxCacheOverflowSizeGB: 0.5
  # Включить диагностические данные
  diagnosticDataCollectionEnabled: true
  # Настройки для testing
  enableTestCommands: false
```

**Различные конфигурации для разных сред:**

**Development конфигурация:**
```yaml
systemLog:
  destination: file
  path: /var/log/mongodb/mongod.log
  logAppend: false  # Перезаписывать при каждом запуске

storage:
  dbPath: /data/db
  journal:
    enabled: false  # Быстрее, но риск потери данных

net:
  bindIp: 127.0.0.1  # Только localhost
  port: 27017

security:
  authorization: disabled  # Без аутентификации

# Меньше логов для development
systemLog:
  verbosity: 1
```

**Production конфигурация:**
```yaml
systemLog:
  destination: file
  path: /var/log/mongodb/mongod.log
  logRotate: reopen
  timeStampFormat: iso8601-utc

storage:
  dbPath: /data/db
  wiredTiger:
    engineConfig:
      cacheSizeGB: 8  # 50-80% от RAM
      directoryForIndexes: true
    collectionConfig:
      blockCompressor: zstd  # Лучшее сжатие
    indexConfig:
      prefixCompression: true

net:
  bindIp: 0.0.0.0  # Все интерфейсы
  port: 27017
  compression:
    compressors: [snappy, zstd]

security:
  authorization: enabled
  keyFile: /etc/mongo/keyfile
  javascriptEnabled: false
  redactClientLogData: true

replication:
  replSetName: rs0
  enableMajorityReadConcern: true

auditLog:
  destination: file
  path: /var/log/mongodb/audit.log
  format: JSON
```

**High-performance конфигурация:**
```yaml
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 16
      maxCacheOverflowSizeGB: 2
    collectionConfig:
      blockCompressor: none  # Без сжатия для скорости

net:
  maxIncomingConnections: 10000
  compression:
    compressors: [snappy]

operationProfiling:
  mode: off  # Отключить профилирование для производительности

setParameter:
  wiredTigerMaxCacheOverflowSizeGB: 4
  wiredTigerCacheSizeGB: 20
```

**Cloud/Managed конфигурация:**
```yaml
# Для MongoDB Atlas или других managed сервисов
# Многие параметры управляются провайдером

storage:
  # Ограниченные возможности настройки
  wiredTiger:
    engineConfig:
      cacheSizeGB: 4  # Управляется автоматически

net:
  # Network настройки часто фиксированы
  bindIp: 0.0.0.0
  port: 27017

security:
  # Безопасность часто преднастроена
  authorization: enabled

# Monitoring часто интегрирован
setParameter:
  diagnosticDataCollectionEnabled: true
```

**Валидация конфигурации:**
```bash
# Проверить синтаксис конфигурационного файла
mongod --config /etc/mongod.conf --validate

# Запустить с новой конфигурацией
mongod --config /etc/mongod.conf

# Перечитать конфигурацию без перезапуска
db.adminCommand({setParameter: 1, "param": "value"})
```

**Мониторинг конфигурации:**
```javascript
// Проверить текущую конфигурацию
db.serverCmdLineOpts()

// Посмотреть статус сервера
db.serverStatus()

// Проверить параметры репликации
rs.status()

// Мониторить производительность
db.serverStatus().wiredTiger
```
  bindIp: 127.0.0.1  # localhost только, для production указать IP или 0.0.0.0

# Процесс управления
processManagement:
  timeZoneInfo: /usr/share/zoneinfo
  fork: true  # Запуск в фоне

# Безопасность
security:
  authorization: enabled  # Включить аутентификацию

# Репликация (для development отключено)
# replication:
#   replSetName: "rs0"

# Профилирование (для development)
operationProfiling:
  slowOpThresholdMs: 100
  mode: slowOp
```

### Запуск с конфигурацией

```bash
# Запуск с конфигурационным файлом
mongod --config /etc/mongod.conf

# Проверка конфигурации
mongod --config /etc/mongod.conf --validate
```

## Основные концепции MongoDB

### Терминология

| MongoDB | Реляционные СУБД | Описание |
|---------|-------------------|----------|
| Database | Database | Контейнер для коллекций |
| Collection | Table | Группа документов |
| Document | Row | Запись данных в формате BSON |
| Field | Column | Поле документа |
| Index | Index | Структура для быстрого поиска |
| Embedded Document | Join | Вложенный документ |
| Reference | Foreign Key | Ссылка на другой документ |

### Документы и BSON

MongoDB хранит данные в формате **BSON** (Binary JSON):

```javascript
// Пример BSON документа
{
  _id: ObjectId("507f1f77bcf86cd799439011"),
  name: "John Doe",
  age: 30,
  email: "john@example.com",
  address: {
    street: "123 Main St",
    city: "Anytown",
    zipCode: "12345"
  },
  tags: ["developer", "mongodb"],
  createdAt: ISODate("2023-01-01T00:00:00Z"),
  isActive: true,
  score: 85.5
}
```

#### BSON типы данных

| Тип | Описание | Пример |
|-----|----------|---------|
| ObjectId | Уникальный идентификатор | `ObjectId("507f1f77bcf86cd799439011")` |
| String | Строка текста | `"Hello World"` |
| Integer | Целые числа | `42`, `NumberLong("123456789")` |
| Double | Числа с плавающей точкой | `3.14159` |
| Boolean | Логические значения | `true`, `false` |
| Date | Дата и время | `ISODate("2023-01-01T00:00:00Z")` |
| Array | Массивы | `[1, 2, 3]`, `["a", "b", "c"]` |
| Object | Вложенные документы | `{name: "John", age: 30}` |
| Null | Null значение | `null` |
| Binary | Бинарные данные | `BinData(0, "SGVsbG8gV29ybGQ=")` |
| Regex | Регулярные выражения | `/pattern/flags` |

### ObjectId

**ObjectId** — это 12-байтовый BSON тип, который гарантированно уникален в коллекции:

```
┌─────────┬─────────┬─────────┬─────────┐
│ 4 bytes │ 3 bytes │ 2 bytes │ 3 bytes │
│ timestamp │ machine  │ pid     │ counter │
└─────────┴─────────┴─────────┴─────────┘
```

```javascript
// Создание ObjectId
var objectId = ObjectId(); // Автоматически генерируется
var objectId = ObjectId("507f1f77bcf86cd799439011"); // Из строки

// Получение компонентов
objectId.getTimestamp(); // Дата создания
objectId.toString();     // Строковое представление
```

### Embedded Documents vs References

#### Embedded Documents (денормализация)
```javascript
// Пользователь с embedded адресом
{
  _id: ObjectId("..."),
  name: "John Doe",
  email: "john@example.com",
  address: {
    street: "123 Main St",
    city: "Anytown",
    zipCode: "12345",
    country: "USA"
  }
}
```

#### References (нормализация)
```javascript
// Пользователь с ссылкой на адрес
{
  _id: ObjectId("..."),
  name: "John Doe",
  email: "john@example.com",
  addressId: ObjectId("...")
}

// Отдельная коллекция адресов
{
  _id: ObjectId("..."),
  userId: ObjectId("..."),
  street: "123 Main St",
  city: "Anytown",
  zipCode: "12345",
  country: "USA"
}
```

## Работа с MongoDB Shell

### Подключение к MongoDB

```bash
# Локальное подключение
mongo

# Подключение к конкретному хосту и порту
mongo --host localhost --port 27017

# Подключение с аутентификацией
mongo --host localhost --port 27017 -u admin -p password --authenticationDatabase admin

# Подключение к replica set
mongo "mongodb://host1:27017,host2:27017,host3:27017/mydb?replicaSet=rs0"
```

### Основные команды MongoDB Shell

#### Навигация по базам данных
```javascript
// Показать все базы данных
show dbs

// Переключиться на базу данных
use mydb

// Показать текущую базу данных
db

// Создать базу данных (создается при первом использовании)
use newdb
```

#### Работа с коллекциями
```javascript
// Показать коллекции в текущей БД
show collections

// Создать коллекцию
db.createCollection("users")

// Создать коллекцию с опциями
db.createCollection("logs", {
  capped: true,      // Ограниченная коллекция
  size: 1000000,     // Максимальный размер в байтах
  max: 1000          // Максимальное количество документов
})

// Удалить коллекцию
db.users.drop()

// Получить статистику коллекции
db.users.stats()
```

### CRUD операции в MongoDB Shell

#### Create (Вставка)
```javascript
// Вставка одного документа
db.users.insertOne({
  name: "John Doe",
  email: "john@example.com",
  age: 30,
  createdAt: new Date()
})

// Вставка нескольких документов
db.users.insertMany([
  { name: "Jane Doe", email: "jane@example.com", age: 28 },
  { name: "Bob Smith", email: "bob@example.com", age: 35 }
])
```

#### Read (Чтение)
```javascript
// Найти все документы
db.users.find()

// Найти с условием
db.users.find({ age: { $gte: 30 } })

// Найти один документ
db.users.findOne({ email: "john@example.com" })

// С проекцией (выбор полей)
db.users.find({}, { name: 1, email: 1, _id: 0 })

// С сортировкой и лимитом
db.users.find()
  .sort({ age: -1 })
  .limit(5)
```

#### Update (Обновление)
```javascript
// Обновить один документ
db.users.updateOne(
  { email: "john@example.com" },
  { $set: { age: 31, updatedAt: new Date() } }
)

// Обновить несколько документов
db.users.updateMany(
  { age: { $lt: 30 } },
  { $set: { category: "young" } }
)

// Заменить весь документ
db.users.replaceOne(
  { email: "john@example.com" },
  {
    name: "John Doe",
    email: "john@example.com",
    age: 32,
    status: "active",
    updatedAt: new Date()
  }
)
```

#### Delete (Удаление)
```javascript
// Удалить один документ
db.users.deleteOne({ email: "john@example.com" })

// Удалить несколько документов
db.users.deleteMany({ age: { $lt: 18 } })

// Удалить все документы
db.users.deleteMany({})
```

### Индексы в MongoDB Shell

```javascript
// Создать индекс
db.users.createIndex({ email: 1 })

// Создать уникальный индекс
db.users.createIndex({ email: 1 }, { unique: true })

// Создать compound индекс
db.users.createIndex({ name: 1, age: -1 })

// Показать индексы
db.users.getIndexes()

// Удалить индекс
db.users.dropIndex("email_1")
```

### Агрегация в MongoDB Shell

```javascript
// Простая агрегация
db.orders.aggregate([
  { $match: { status: "completed" } },
  { $group: { _id: "$customerId", total: { $sum: "$amount" } } },
  { $sort: { total: -1 } },
  { $limit: 10 }
])

// Агрегация с lookup (JOIN)
db.orders.aggregate([
  { $match: { status: "completed" } },
  {
    $lookup: {
      from: "customers",
      localField: "customerId",
      foreignField: "_id",
      as: "customer"
    }
  },
  { $unwind: "$customer" },
  { $project: { customerName: "$customer.name", total: 1 } }
])
```

## Работа с базами данных

### Создание и управление базами данных

```javascript
// Создание базы данных
use ecommerce

// Вставка документа создаст базу данных
db.products.insertOne({
  name: "Laptop",
  price: 999.99,
  category: "Electronics"
})

// Проверка создания
show dbs
```

### Системные базы данных

MongoDB имеет несколько системных баз данных:

```javascript
// Admin database - системная информация и аутентификация
use admin

// Config database - информация о шардировании
use config

// Local database - oplog и локальная информация
use local

// Показать только пользовательские базы данных
show dbs  // Не покажет admin, config, local если они пустые
```

### Управление базами данных

```javascript
// Получить статистику базы данных
db.stats()

// Получить размер базы данных
db.stats().dataSize

// Переименовать базу данных (требует admin прав)
db.adminCommand({
  renameCollection: "olddb.collection",
  to: "newdb.collection"
})

// Удалить базу данных
use mydb
db.dropDatabase()
```

## Коллекции и документы

### Типы коллекций

#### Regular Collections
```javascript
// Создание обычной коллекции
db.createCollection("users")

// Автоматическое создание при вставке
db.products.insertOne({ name: "Product 1", price: 10.99 })
```

#### Capped Collections
```javascript
// Создание ограниченной коллекции
db.createCollection("logs", {
  capped: true,
  size: 1000000,  // Максимальный размер в байтах
  max: 1000       // Максимальное количество документов
})

// Capped коллекции автоматически удаляют старые документы
// Подходят для логов, кэша, очередей
```

#### Time Series Collections (MongoDB 5.0+)
```javascript
// Создание коллекции временных рядов
db.createCollection("sensor_readings", {
  timeseries: {
    timeField: "timestamp",
    metaField: "sensorId",
    granularity: "minutes"
  }
})

// Вставка данных временных рядов
db.sensor_readings.insertMany([
  {
    sensorId: "temp_001",
    timestamp: new Date(),
    temperature: 23.5,
    humidity: 65.2
  }
])
```

### Управление коллекциями

```javascript
// Переименование коллекции
db.users.renameCollection("customers")

// Клонирование коллекции
db.users.aggregate([
  { $out: "users_backup" }
])

// Валидация коллекции
db.runCommand({ validate: "users" })

// Компакт коллекции
db.runCommand({ compact: "users" })
```

### Документы

#### Валидация документов

```javascript
// Создание коллекции с валидацией
db.createCollection("users", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["name", "email"],
      properties: {
        name: {
          bsonType: "string",
          description: "must be a string and is required"
        },
        email: {
          bsonType: "string",
          pattern: "^.+@.+$",
          description: "must be a valid email"
        },
        age: {
          bsonType: "int",
          minimum: 0,
          maximum: 120,
          description: "must be between 0 and 120"
        }
      }
    }
  }
})

// Проверка уровня валидации
db.getCollectionInfos({ name: "users" })[0].options.validator
```

## Структура данных

### Data Modeling в MongoDB

#### Embedded Documents (One-to-One, One-to-Many)
```javascript
// Пользователь с embedded профилем
{
  _id: ObjectId("..."),
  username: "johndoe",
  email: "john@example.com",
  profile: {
    firstName: "John",
    lastName: "Doe",
    dateOfBirth: ISODate("1990-01-01"),
    address: {
      street: "123 Main St",
      city: "Anytown",
      country: "USA"
    }
  },
  createdAt: ISODate("2023-01-01")
}
```

#### References (Many-to-Many)
```javascript
// Пост в блоге
{
  _id: ObjectId("..."),
  title: "MongoDB Tutorial",
  content: "Full content...",
  authorId: ObjectId("..."),  // Reference to user
  tags: ["mongodb", "tutorial"],
  createdAt: ISODate("2023-01-01")
}

// Комментарии к посту
{
  _id: ObjectId("..."),
  postId: ObjectId("..."),    // Reference to post
  authorId: ObjectId("..."),  // Reference to user
  content: "Great tutorial!",
  createdAt: ISODate("2023-01-01")
}
```

#### Массивы в документах
```javascript
// Продукт с массивом отзывов
{
  _id: ObjectId("..."),
  name: "Wireless Headphones",
  price: 199.99,
  reviews: [
    {
      userId: ObjectId("..."),
      rating: 5,
      comment: "Excellent sound quality!",
      createdAt: ISODate("2023-01-01")
    },
    {
      userId: ObjectId("..."),
      rating: 4,
      comment: "Good battery life",
      createdAt: ISODate("2023-01-02")
    }
  ]
}
```

### Нормализация vs Денормализация

#### Нормализация (как в реляционных БД)
```javascript
// Отдельные коллекции
db.users.insertOne({
  _id: ObjectId("..."),
  name: "John Doe",
  email: "john@example.com"
})

db.orders.insertOne({
  _id: ObjectId("..."),
  userId: ObjectId("..."),  // Reference
  products: [ObjectId("..."), ObjectId("...")],
  total: 299.99
})
```

#### Денормализация (оптимизация для чтения)
```javascript
// Денормализованный документ
db.orders.insertOne({
  _id: ObjectId("..."),
  user: {
    id: ObjectId("..."),
    name: "John Doe",
    email: "john@example.com"
  },
  products: [
    {
      id: ObjectId("..."),
      name: "Laptop",
      price: 999.99
    }
  ],
  total: 999.99
})
```

### Atomic Operations

```javascript
// Атомарное обновление массива
db.users.updateOne(
  { _id: ObjectId("...") },
  {
    $push: { posts: newPostId },
    $inc: { postCount: 1 },
    $set: { lastPostAt: new Date() }
  }
)

// Атомарное обновление с условием
db.products.updateOne(
  { _id: productId, stock: { $gte: quantity } },
  {
    $inc: { stock: -quantity },
    $push: { 
      soldItems: {
        quantity: quantity,
        soldAt: new Date(),
        customerId: customerId
      }
    }
  }
)
```

## MongoDB Compass

### Установка и настройка

```bash
# Скачать с официального сайта
# https://www.mongodb.com/try/download/compass

# Или установить через пакетный менеджер
# Ubuntu/Debian
wget https://downloads.mongodb.com/compass/mongodb-compass_1.35.0_amd64.deb
sudo dpkg -i mongodb-compass_1.35.0_amd64.deb

# CentOS/RHEL
wget https://downloads.mongodb.com/compass/mongodb-compass-1.35.0.x86_64.rpm
sudo rpm -i mongodb-compass-1.35.0.x86_64.rpm

# macOS
brew install --cask mongodb-compass
```

### Подключение к MongoDB

1. **Запуск Compass**
2. **Создание нового подключения**
3. **Ввод URI**: `mongodb://localhost:27017`
4. **Для аутентификации**: `mongodb://username:password@localhost:27017/mydb`
5. **Для replica set**: `mongodb://host1:27017,host2:27017,host3:27017/mydb?replicaSet=rs0`

### Основные функции Compass

#### Просмотр данных
- **Databases**: Просмотр всех баз данных
- **Collections**: Просмотр коллекций в базе данных
- **Documents**: Просмотр и редактирование документов
- **Indexes**: Управление индексами
- **Schema**: Анализ схемы данных

#### CRUD операции
- **Insert Document**: Вставка новых документов
- **Edit Document**: Редактирование существующих документов
- **Delete Document**: Удаление документов
- **Bulk Operations**: Массовые операции

#### Анализ производительности
- **Explain Plan**: Анализ планов выполнения запросов
- **Index Usage**: Статистика использования индексов
- **Slow Queries**: Просмотр медленных запросов
- **Aggregation Pipeline Builder**: Визуальный конструктор агрегаций

#### Импорт/Экспорт данных
- **JSON/CSV Import**: Импорт данных из файлов
- **Export Collection**: Экспорт коллекций в JSON/CSV
- **MongoDB Tools Integration**: Использование mongodump/mongorestore

## Best Practices для MongoDB

### 1. Дизайн схемы

#### Правильное использование embedded documents
```javascript
// Хорошо: Читаемые вместе данные
{
  userId: ObjectId("..."),
  profile: {
    name: "John",
    email: "john@example.com",
    address: { /* ... */ }
  }
}

// Плохо: Часто обновляемые данные
{
  userId: ObjectId("..."),
  posts: [/* 1000+ постов */]  // Слишком большой документ
}
```

#### Оптимальный размер документов
- **Максимальный размер документа**: 16MB
- **Рекомендуемый размер**: < 1MB для большинства документов
- **Использовать GridFS**: Для файлов > 16MB

### 2. Индексирование

#### Создавайте индексы на часто используемые поля
```javascript
// Индексы для поиска
db.users.createIndex({ email: 1 }, { unique: true })
db.products.createIndex({ category: 1, price: -1 })

// Compound индексы для сортировки
db.posts.createIndex({ author: 1, createdAt: -1 })

// Partial индексы для оптимизации
db.logs.createIndex(
  { level: 1, createdAt: 1 },
  { partialFilterExpression: { level: "ERROR" } }
)
```

#### Избегайте избыточного индексирования
- Каждый индекс занимает место и замедляет вставку
- Удаляйте неиспользуемые индексы
- Мониторьте использование индексов

### 3. Connection Management

#### Используйте connection pooling
```javascript
// В приложениях всегда используйте connection pool
// Не создавайте новое соединение для каждого запроса

// Java пример
MongoClientSettings settings = MongoClientSettings.builder()
    .applyConnectionString(new ConnectionString(uri))
    .applyToConnectionPoolSettings(builder ->
        builder.maxSize(20).minSize(5)
    )
    .build();

MongoClient client = MongoClients.create(settings);
```

### 4. Обработка ошибок

#### Graceful error handling
```javascript
try {
    // MongoDB операция
    db.users.insertOne(document);
} catch (error) {
    if (error.code === 11000) {
        // Duplicate key error
        console.log("Document already exists");
    } else if (error.code === 121) {
        // Document validation error
        console.log("Document validation failed");
    } else {
        // Other error
        console.log("Unexpected error:", error);
    }
}
```

### 5. Производительность

#### Оптимизация запросов
```javascript
// Используйте projection для уменьшения данных
db.users.find(
    { age: { $gte: 18 } },
    { name: 1, email: 1 }  // Только нужные поля
)

// Используйте limit для пагинации
db.posts.find()
    .sort({ createdAt: -1 })
    .limit(20)
    .skip(0)  // Но избегайте большого skip
```

#### Мониторинг ресурсов
```javascript
// Регулярно проверяйте
db.serverStatus().mem         // Использование памяти
db.serverStatus().connections // Активные соединения
db.serverStatus().opcounters  // Операции по типам

// Настройка monitoring alerts
// - Высокое использование CPU
// - Недостаток памяти
// - Большое количество соединений
// - Медленные запросы
```

### 6. Безопасность

#### Включите аутентификацию
```javascript
// Включить authorization в mongod.conf
security:
  authorization: enabled

// Создать пользователей с минимальными правами
db.createUser({
  user: "appuser",
  pwd: "securepassword",
  roles: [
    { role: "readWrite", db: "mydb" }
  ]
})
```

#### Шифрование данных
```javascript
// Включить TLS/SSL
net:
  ssl:
    mode: requireSSL
    PEMKeyFile: /etc/ssl/mongodb.pem
    CAFile: /etc/ssl/ca.pem
```

### 7. Backup и Recovery

#### Регулярные бэкапы
```bash
# Ежедневный backup
mongodump --db mydb --out /backup/$(date +%Y%m%d)

# С compression
mongodump --db mydb --gzip --out /backup/

# Point-in-time recovery
mongodump --db mydb --oplog
```

#### Тестирование восстановления
```bash
# Тестируйте restore регулярно
mongorestore --db mydb /backup/mydb

# Проверяйте целостность данных после restore
db.users.count()
db.runCommand({ validate: "users" })
```

## Заключение

**MongoDB** — это мощная и гибкая NoSQL база данных, которая отлично подходит для современных приложений, требующих быстрой разработки и масштабируемости.

### Ключевые концепции освоены:

1. **Документо-ориентированная модель** — данные в формате BSON
2. **Гибкая схема** — отсутствие строгих ограничений на структуру
3. **Масштабируемость** — горизонтальное масштабирование через шардирование
4. **Индексы** — оптимизация запросов и сортировки
5. **Aggregation Framework** — мощные возможности для аналитики

### Основные преимущества:

- **Быстрая разработка** — нет необходимости в схеме и миграциях
- **Естественная интеграция** — JSON-подобный формат для веб-приложений
- **Высокая производительность** — оптимизированные индексы и запросы
- **Горизонтальная масштабируемость** — шардирование для больших данных
- **Rich ecosystem** — множество драйверов и инструментов

### Когда использовать MongoDB:

✅ **Современные веб-приложения** — JSON API, микросервисы
✅ **Big Data и аналитика** — гибкая схема для разнообразных данных  
✅ **Content Management** — хранение контента и метаданных
✅ **IoT приложения** — обработка сенсорных данных
✅ **Real-time приложения** — быстрая вставка и чтение
✅ **Прототипирование** — быстрая итерация без схемы

### Когда НЕ использовать MongoDB:

❌ **Сложные транзакции** — если нужны ACID транзакции между многими таблицами
❌ **Комплексные JOIN** — если требуется много связей между данными
❌ **Строгая схема** — если важна целостность данных на уровне схемы
❌ **SQL знания** — если команда предпочитает реляционные базы данных

MongoDB продолжает развиваться и улучшаться. С выходом новых версий добавляются новые возможности: улучшенные транзакции, time series коллекции, encrypted storage и многое другое.

Для успешной работы с MongoDB важно понимать принципы data modeling, правильно использовать индексы и мониторить производительность. С правильным подходом MongoDB может значительно ускорить разработку и обеспечить высокую производительность приложений. 🚀

## Введение в MongoDB

**MongoDB** — это высокопроизводительная, документо-ориентированная NoSQL база данных с открытым исходным кодом. MongoDB хранит данные в формате BSON (Binary JSON) и обеспечивает гибкую схему данных, горизонтальную масштабируемость и богатый набор функций для работы с данными.

### Почему MongoDB?

MongoDB была разработана для решения проблем традиционных реляционных баз данных в современных приложениях:

1. **Гибкая схема данных** - документы могут иметь разную структуру
2. **Горизонтальная масштабируемость** - шардирование для обработки больших объемов данных
3. **Высокая производительность** - оптимизированная для чтения и записи
4. **Разработан для JSON/BSON** - естественная интеграция с современными приложениями
5. **Rich Query Language** - мощный язык запросов с поддержкой геоданных, текста, агрегаций

### Архитектура MongoDB

```
┌─────────────────────────────────────────────────────────────────┐
│                          MongoDB Cluster                        │
├─────────────────────────────────────────────────────────────────┤
│  Config Servers │ Mongos Routers │ Shard Servers │ Replica Sets │
├─────────────────────────────────────────────────────────────────┤
│                    MongoDB Core Engine                          │
├─────────────────────────────────────────────────────────────────┤
│  Database │ Collections │ Documents │ Indexes │ Aggregation    │
├─────────────────────────────────────────────────────────────────┤
│                    Storage Engine (WiredTiger)                  │
├─────────────────────────────────────────────────────────────────┤
│                     Operating System                            │
└─────────────────────────────────────────────────────────────────┘
```

#### Ключевые компоненты:

- **Config Servers**: хранят метаданные кластера и конфигурацию шардинга
- **Mongos Routers**: маршрутизаторы запросов, распределяют нагрузку между шардами
- **Shard Servers**: хранят данные, каждый шард содержит подмножество данных
- **Replica Sets**: обеспечивают отказоустойчивость и высокую доступность

### Сравнение с реляционными базами данных

| Аспект | MongoDB | Реляционные БД (PostgreSQL) |
|--------|---------|-----------------------------|
| **Схема** | Гибкая (схема-less) | Строгая схема |
| **Модель данных** | Документы (JSON/BSON) | Таблицы с отношениями |
| **Масштабируемость** | Горизонтальная | Вертикальная |
| **ACID транзакции** | Поддержка (с 4.0) | Полная поддержка |
| **JOIN операции** | $lookup (агрегация) | JOIN с оптимизацией |
| **Индексы** | B-tree, текстовые, гео | B-tree, hash, GiST, GIN |
| **Производительность** | Высокая для больших данных | Высокая для сложных запросов |
| **Использование** | Big Data, real-time | Enterprise приложения |

### Преимущества MongoDB

#### Гибкость схемы
```java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
``````java
// Java + Spring implementation available
```