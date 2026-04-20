---
title: "Redis: Структуры данных"
description: "Полное руководство по структурам данных Redis: Strings, Lists, Sets, Hashes, Sorted Sets, Streams, Bitmaps, HyperLogLog, Geospatial"
tags:
  - redis
  - data-structures
  - strings
  - lists
  - sets
  - hashes
  - sorted-sets
  - streams
difficulty: "intermediate"
prerequisites: ["databases/redis-basics.md"]
next: ["databases/redis-persistence.md", "databases/redis-replication.md"]
updated: "2026-04-20"
related: ["databases/redis-basics.md", "databases/redis-performance.md"]
---

# Redis: Структуры данных

## Полезные ссылки

- [Redis Data Types](https://redis.io/docs/data-types/)
- [Redis Commands](https://redis.io/commands/)
- [Redis Patterns](https://redis.io/docs/manual/patterns/)


## Содержание

- [Введение в структуры данных Redis](#введение-в-структуры-данных-redis)
  - [Типы структур данных](#типы-структур-данных)
- [Strings (Строки)](#strings-строки)
  - [Базовые операции](#базовые-операции)
  - [Атомарные операции](#атомарные-операции)
  - [Операции со строками](#операции-со-строками)
  - [Битовые операции](#битовые-операции)
  - [Use Cases для Strings](#use-cases-для-strings)
- [Lists (Списки)](#lists-списки)
  - [Базовые операции](#базовые-операции-1)
  - [Извлечение элементов](#извлечение-элементов)
  - [Модификация списков](#модификация-списков)
  - [Атомарные операции](#атомарные-операции-1)
  - [Use Cases для Lists](#use-cases-для-lists)
- [Sets (Множества)](#sets-множества)
  - [Базовые операции](#базовые-операции-2)
  - [Операции над множествами](#операции-над-множествами)
  - [Извлечение элементов](#извлечение-элементов-1)
  - [Перемещение элементов](#перемещение-элементов)
  - [Use Cases для Sets](#use-cases-для-sets)
- [Hashes (Хэши)](#hashes-хэши)
  - [Базовые операции](#базовые-операции-3)
  - [Множественные операции](#множественные-операции)
  - [Числовые операции](#числовые-операции)
  - [Итерация по полям](#итерация-по-полям)
  - [Use Cases для Hashes](#use-cases-для-hashes)
- [Sorted Sets (Отсортированные множества)](#sorted-sets-отсортированные-множества)
  - [Базовые операции](#базовые-операции-4)
  - [Диапазонные операции](#диапазонные-операции)
  - [Модификация](#модификация)
  - [Операции над множествами](#операции-над-множествами-1)
  - [Лексикографические операции](#лексикографические-операции)
  - [Use Cases для Sorted Sets](#use-cases-для-sorted-sets)
- [Streams (Потоки)](#streams-потоки)
  - [Базовые операции](#базовые-операции-5)
  - [Consumer Groups](#consumer-groups)
  - [Управление потоком](#управление-потоком)
  - [Use Cases для Streams](#use-cases-для-streams)
- [Bitmaps (Битовые массивы)](#bitmaps-битовые-массивы)
  - [Базовые операции](#базовые-операции-6)
  - [Битовые операции](#битовые-операции-1)
  - [Use Cases для Bitmaps](#use-cases-для-bitmaps)
- [HyperLogLog](#hyperloglog)
  - [Базовые операции](#базовые-операции-7)
  - [Use Cases для HyperLogLog](#use-cases-для-hyperloglog)
- [Geospatial (Геопространственные данные)](#geospatial-геопространственные-данные)
  - [Базовые операции](#базовые-операции-8)
  - [Поиск по радиусу](#поиск-по-радиусу)
  - [Геохэши](#геохэши)
  - [Use Cases для Geospatial](#use-cases-для-geospatial)
- [Выбор правильной структуры данных](#выбор-правильной-структуры-данных)
  - [Сравнительная таблица](#сравнительная-таблица)
  - [Рекомендации по выбору](#рекомендации-по-выбору)
- [Advanced Patterns and Examples](#advanced-patterns-and-examples)
  - [Strings: Advanced Use Cases](#strings-advanced-use-cases)
    - [Caching with Expiration](#caching-with-expiration)
    - [Distributed Counters](#distributed-counters)
  - [Lists: Queue Implementation](#lists-queue-implementation)
  - [Sets: Tag System](#sets-tag-system)
  - [Hashes: User Profile Management](#hashes-user-profile-management)
  - [Sorted Sets: Leaderboard Implementation](#sorted-sets-leaderboard-implementation)
  - [Streams: Event Logging System](#streams-event-logging-system)
  - [Bitmaps: User Activity Tracking](#bitmaps-user-activity-tracking)
- [См. также](#см-также)

## Введение в структуры данных Redis

**Redis** поддерживает множество типов структур данных, каждая из которых оптимизирована для определенных **use cases**. Понимание особенностей каждой структуры данных критически важно для эффективного использования **Redis**.

### Типы структур данных

1. **Strings**: Простые строки и бинарные данные
2. **Lists**: Упорядоченные коллекции строк
3. **Sets**: Неупорядоченные коллекции уникальных строк
4. **Hashes**: Карты полей и значений
5. **Sorted Sets**: Упорядоченные множества с оценками
6. **Streams**: Логи сообщений (Redis 5.0+)
7. **Bitmaps**: Битовые массивы
8. **HyperLogLog**: Приблизительный подсчет уникальных элементов
9. **Geospatial**: Геопространственные данные


## Strings (Строки)

**Strings** — это самый простой тип данных в **Redis**. Строки могут содержать текст, числа или бинарные данные до 512MB.

### Базовые операции

```redis
# Установка значения
SET mykey "Hello Redis"
SET counter "100"
SET binary_data "\x00\x01\x02"

# Получение значения
GET mykey
GET counter

# Проверка существования
EXISTS mykey

# Удаление
DEL mykey

# Получение длины строки
STRLEN mykey
```

### Атомарные операции

```redis
# Инкремент
INCR counter           # +1
INCRBY counter 5       # +5
INCRBYFLOAT counter 1.5 # +1.5

# Декремент
DECR counter           # -1
DECRBY counter 3       # -3

# Установка только если ключ не существует
SETNX mykey "value"

# Установка только если ключ существует
SET mykey "value" XX

# Получение и установка атомарно
GETSET mykey "new_value"
```

### Операции со строками

```redis
# Добавление к строке
APPEND mykey " World"

# Получение подстроки
GETRANGE mykey 0 4     # "Hello"

# Установка подстроки
SETRANGE mykey 6 "Redis"

# Множественные операции
MSET key1 "value1" key2 "value2" key3 "value3"
MGET key1 key2 key3
MSETNX key1 "value1" key2 "value2"
```

### Битовые операции

```redis
# Установка бита
SETBIT mykey 7 1

# Получение бита
GETBIT mykey 7

# Подсчет установленных битов
BITCOUNT mykey
BITCOUNT mykey 0 10    # В диапазоне

# Битовая операция AND
BITOP AND dest key1 key2

# Битовая операция OR
BITOP OR dest key1 key2

# Битовая операция XOR
BITOP XOR dest key1 key2

# Битовая операция NOT
BITOP NOT dest key

# Поиск первого установленного бита
BITPOS mykey 1
BITPOS mykey 1 0 10    # В диапазоне
```

### Use Cases для Strings

1. **Кэширование**: Простые значения
2. **Счетчики**: **INCR**/**DECR** операции
3. **Сессии**: Хранение сессионных данных
4. **Битовая аналитика**: **SETBIT**/**BITCOUNT** для аналитики


## Lists (Списки)

**Lists** — это упорядоченные коллекции строк, реализованные как **linked lists**. Операции с начала и конца списка очень быстрые (**O(1)).

### Базовые операции

```redis
# Добавление в начало
LPUSH mylist "world"
LPUSH mylist "hello"     # ["hello", "world"]

# Добавление в конец
RPUSH mylist "redis"     # ["hello", "world", "redis"]

# Получение элементов
LRANGE mylist 0 -1       # Все элементы
LRANGE mylist 0 1        # Первые 2 элемента
LRANGE mylist -2 -1      # Последние 2 элемента

# Длина списка
LLEN mylist

# Получение элемента по индексу
LINDEX mylist 0
LINDEX mylist -1        # Последний элемент
```

### Извлечение элементов

```redis
# Извлечение из начала
LPOP mylist

# Извлечение из конца
RPOP mylist

# Блокирующее извлечение
BLPOP mylist 10          # Ждать до 10 секунд
BRPOP mylist 10

# Блокирующее извлечение из нескольких списков
BLPOP list1 list2 10
BRPOP list1 list2 10
```

### Модификация списков

```redis
# Вставка элемента
LINSERT mylist BEFORE "world" "beautiful"
LINSERT mylist AFTER "hello" "wonderful"

# Установка значения по индексу
LSET mylist 1 "newvalue"

# Удаление элементов
LREM mylist 2 "value"    # Удалить 2 вхождения "value"
LREM mylist -2 "value"  # Удалить 2 последних вхождения
LREM mylist 0 "value"   # Удалить все вхождения

# Обрезка списка
LTRIM mylist 1 3        # Оставить элементы с индекса 1 по 3
```

### Атомарные операции

```redis
# Атомарное перемещение элемента
RPOPLPUSH source dest
BRPOPLPUSH source dest 10
```

### Use Cases для Lists

1. **Очереди**: **LPUSH**/**RPOP** для **FIFO** очередей
2. **Стеки**: **LPUSH**/**LPOP** для **LIFO** стеков
3. **Timeline**: Хранение временных линий
4. **Ограниченные коллекции**: **LTRIM** для ограничения размера


## Sets (Множества)

**Sets** — это неупорядоченные коллекции уникальных строк. Операции добавления, удаления и проверки принадлежности выполняются за `O(1)`.

### Базовые операции

```redis
# Добавление элементов
SADD myset "member1"
SADD myset "member2" "member3"

# Получение всех элементов
SMEMBERS myset

# Количество элементов
SCARD myset

# Проверка принадлежности
SISMEMBER myset "member1"

# Удаление элементов
SREM myset "member1"
```

### Операции над множествами

```redis
# Создание множеств
SADD set1 "a" "b" "c"
SADD set2 "c" "d" "e"

# Объединение
SUNION set1 set2         # {"a", "b", "c", "d", "e"}

# Пересечение
SINTER set1 set2         # {"c"}

# Разность
SDIFF set1 set2         # {"a", "b"}

# Сохранение результатов
SUNIONSTORE dest set1 set2
SINTERSTORE dest set1 set2
SDIFFSTORE dest set1 set2
```

### Извлечение элементов

```redis
# Случайный элемент (не удаляет)
SRANDMEMBER myset
SRANDMEMBER myset 3     # 3 случайных элемента

# Случайный элемент (удаляет)
SPOP myset
SPOP myset 3            # 3 случайных элемента с удалением
```

### Перемещение элементов

```redis
# Перемещение элемента между множествами
SMOVE source dest "member"
```

### Use Cases для Sets

1. **Теги**: Хранение тегов объектов
2. **Уникальные значения**: Гарантия уникальности
3. **Операции над множествами**: **UNION**, **INTERSECTION**, **DIFFERENCE**
4. **Случайный выбор**: **SRANDMEMBER** для случайных выборок


## Hashes (Хэши)

**Hashes** — это карты полей и значений, идеально подходящие для представления объектов. Хэши оптимизированы для использования памяти.

### Базовые операции

```redis
# Установка поля
HSET user:1000 name "John Doe"
HSET user:1000 email "john@example.com"
HSET user:1000 age 30

# Получение поля
HGET user:1000 name

# Получение всех полей и значений
HGETALL user:1000

# Получение всех ключей
HKEYS user:1000

# Получение всех значений
HVALS user:1000

# Количество полей
HLEN user:1000
```

### Множественные операции

```redis
# Множественная установка
HMSET user:1000 city "New York" country "USA" registered true

# Множественное получение
HMGET user:1000 name email age

# Установка только если поле не существует
HSETNX user:1000 phone "1234567890"
```

### Числовые операции

```redis
# Инкремент числового поля
HINCRBY user:1000 age 1
HINCRBYFLOAT user:1000 balance 10.5

# Получение длины строкового поля
HSTRLEN user:1000 name
```

### Итерация по полям

```redis
# Постраничная итерация
HSCAN user:1000 0 MATCH name* COUNT 10
```

### Use Cases для Hashes

1. **Объекты**: Представление объектов с полями
2. **Профили пользователей**: Хранение данных профиля
3. **Конфигурация**: Хранение настроек
4. **Эффективное использование памяти**: Оптимизация для небольших объектов


## Sorted Sets (Отсортированные множества)

**Sorted Sets** — это множества, где каждый элемент имеет оценку (score). Элементы автоматически сортируются по оценке.

### Базовые операции

```redis
# Добавление элементов с оценками
ZADD leaderboard 1500 "player1"
ZADD leaderboard 1200 "player2" 1800 "player3" 900 "player4"

# Получение элементов по рангу
ZRANGE leaderboard 0 -1              # По возрастанию
ZREVRANGE leaderboard 0 -1           # По убыванию
ZRANGE leaderboard 0 -1 WITHSCORES  # С оценками

# Получение ранга элемента
ZRANK leaderboard "player3"         # По возрастанию
ZREVRANK leaderboard "player3"       # По убыванию

# Получение оценки элемента
ZSCORE leaderboard "player2"

# Количество элементов
ZCARD leaderboard
```

### Диапазонные операции

```redis
# Элементы в диапазоне оценок
ZRANGEBYSCORE leaderboard 1000 2000
ZREVRANGEBYSCORE leaderboard 2000 1000

# Элементы в диапазоне оценок с лимитом
ZRANGEBYSCORE leaderboard 1000 2000 LIMIT 0 10

# Количество элементов в диапазоне
ZCOUNT leaderboard 1000 2000

# Сумма оценок в диапазоне
ZSUM leaderboard 1000 2000
```

### Модификация

```redis
# Инкремент оценки
ZINCRBY leaderboard 100 "player1"

# Удаление элементов
ZREM leaderboard "player4"
ZREMRANGEBYRANK leaderboard 0 1     # Удалить первые 2
ZREMRANGEBYSCORE leaderboard 0 999  # Удалить с оценками < 1000
```

### Операции над множествами

```redis
# Объединение sorted sets
ZUNIONSTORE dest 2 set1 set2 WEIGHTS 1 2 AGGREGATE SUM

# Пересечение sorted sets
ZINTERSTORE dest 2 set1 set2 WEIGHTS 1 2 AGGREGATE MAX
```

### Лексикографические операции

```redis
# Добавление элементов с одинаковыми оценками
ZADD myzset 0 "apple" 0 "banana" 0 "cherry"

# Диапазон по лексикографическому порядку
ZRANGEBYLEX myzset "[b" "(c"        # От "b" до "c"
ZRANGEBYLEX myzset "-" "+"          # Все элементы

# Количество элементов в лексикографическом диапазоне
ZLEXCOUNT myzset "[b" "(c"

# Удаление в лексикографическом диапазоне
ZREMRANGEBYLEX myzset "[b" "(c"
```

### Use Cases для Sorted Sets

1. **Leaderboards**: Рейтинги и таблицы лидеров
2. **Временные ряды**: Данные с временными метками
3. **Приоритетные очереди**: Очереди с приоритетами
4. **Ранжирование**: Сортировка по различным критериям


## Streams (Потоки)

**Streams** — это структура данных для хранения логов сообщений, добавленная в **Redis** `5.0`. **Streams** поддерживают **consumer groups** и обеспечивают гарантии доставки.

### Базовые операции

```redis
# Добавление сообщения в поток
XADD mystream * sensor-id "sensor1" temperature "25.5" humidity "60"
XADD mystream * sensor-id "sensor2" temperature "24.8" humidity "65"

# Чтение из потока
XRANGE mystream - +                    # Все сообщения
XRANGE mystream 1640995200000 1640995260000  # По временному диапазону
XRANGE mystream - + COUNT 10          # С лимитом

# Обратное чтение
XREVRANGE mystream + -                # Последние сообщения
XREVRANGE mystream + - COUNT 10

# Длина потока
XLEN mystream

# Информация о потоке
XINFO STREAM mystream
```

### Consumer Groups

```redis
# Создание consumer group
XGROUP CREATE mystream mygroup 0 MKSTREAM

# Чтение из группы
XREADGROUP GROUP mygroup consumer1 COUNT 1 STREAMS mystream >

# Подтверждение обработки
XACK mystream mygroup 1640995200000-0

# Информация о группах
XINFO GROUPS mystream
XINFO CONSUMERS mystream mygroup

# Удаление consumer
XGROUP DELCONSUMER mystream mygroup consumer1
```

### Управление потоком

```redis
# Удаление сообщений
XDEL mystream 1640995200000-0

# Обрезка потока
XTRIM mystream MAXLEN 1000             # Оставить последние 1000
XTRIM mystream MAXLEN ~ 1000           # Приблизительно 1000
XTRIM mystream MINID 1640995200000-0   # Удалить старше ID

# Блокирующее чтение
XREAD BLOCK 5000 STREAMS mystream $
XREADGROUP GROUP mygroup consumer1 BLOCK 5000 COUNT 1 STREAMS mystream >
```

### Use Cases для Streams

1. **Event Logging**: Логирование событий
2. **Message Queues**: Очереди сообщений с гарантиями
3. **Time Series**: Временные ряды данных
4. **Activity Feeds**: Ленты активности


## Bitmaps (Битовые массивы)

**Bitmaps** — это эффективный способ работы с битовыми массивами. **Bitmaps** используют минимальное количество памяти.

### Базовые операции

```redis
# Установка бита
SETBIT user:1000:login:2023-01-01 1 1
SETBIT user:1000:login:2023-01-02 2 1
SETBIT user:1000:login:2023-01-03 3 1

# Получение бита
GETBIT user:1000:login:2023-01-01 1

# Подсчет установленных битов
BITCOUNT user:1000:login:2023-01-01
BITCOUNT user:1000:login:2023-01-01 0 10  # В диапазоне

# Поиск первого установленного бита
BITPOS user:1000:login:2023-01-01 1
BITPOS user:1000:login:2023-01-01 1 0 10  # В диапазоне
```

### Битовые операции

```redis
# AND операция
BITOP AND dest key1 key2

# OR операция
BITOP OR dest key1 key2

# XOR операция
BITOP XOR dest key1 key2

# NOT операция
BITOP NOT dest key
```

### Use Cases для Bitmaps

1. **Аналитика**: Отслеживание активности пользователей
2. **Фильтры Bloom**: Реализация **Bloom** фильтров
3. **Битовые флаги**: Хранение множества булевых флагов
4. **Статистика**: Подсчет уникальных событий


## HyperLogLog

**HyperLogLog** — это вероятностная структура данных для приблизительного подсчета уникальных элементов с минимальным использованием памяти.

### Базовые операции

```redis
# Добавление элементов
PFADD visitors:2023-01-01 user1 user2 user3 user1

# Подсчет уникальных элементов
PFCOUNT visitors:2023-01-01

# Объединение HyperLogLog
PFMERGE visitors:2023-01 visitors:2023-01-01 visitors:2023-01-02
PFCOUNT visitors:2023-01
```

### Use Cases для HyperLogLog

1. **Уникальные посетители**: Подсчет уникальных пользователей
2. **Cardinality**: Приблизительный подсчет уникальности
3. **Аналитика**: Статистика с минимальным использованием памяти


## Geospatial (Геопространственные данные)

**Geospatial** — это специальный тип данных для работы с географическими координатами, реализованный поверх **Sorted Sets**.

### Базовые операции

```redis
# Добавление геоданных
GEOADD cities 13.361389 38.115556 "Palermo" 15.087269 37.502669 "Catania"
GEOADD cities 12.52441 41.99123 "Rome" 2.15899 41.38879 "Barcelona"

# Получение координат
GEOPOS cities Palermo

# Получение расстояния
GEODIST cities Palermo Catania km
GEODIST cities Palermo Catania m
GEODIST cities Palermo Catania mi
GEODIST cities Palermo Catania ft
```

### Поиск по радиусу

```redis
# Поиск в радиусе
GEORADIUS cities 15 37 200 km
GEORADIUS cities 15 37 200 km WITHCOORD
GEORADIUS cities 15 37 200 km WITHDIST
GEORADIUS cities 15 37 200 km WITHCOORD WITHDIST
GEORADIUS cities 15 37 200 km COUNT 5

# Поиск по элементу
GEORADIUSBYMEMBER cities Palermo 200 km
GEORADIUSBYMEMBER cities Palermo 200 km WITHCOORD WITHDIST
```

### Геохэши

```redis
# Получение геохэша
GEOHASH cities Palermo

# Геохэш можно использовать для поиска в других системах
```

### Use Cases для Geospatial

1. **Поиск поблизости**: Поиск объектов в радиусе
2. **Расстояния**: Расчет расстояний между точками
3. **Геокодирование**: Хранение и поиск по координатам
4. **Логистика**: Оптимизация маршрутов


## Выбор правильной структуры данных

### Сравнительная таблица

| Структура | Сложность операций | **Use Case** |
|-----------|-------------------|----------|
| **Strings** | `O(1)` для **GET**/**SET** | Простые значения, счетчики |
| **Lists** | `O(1)` для **LPUSH**/**RPOP** | Очереди, стеки |
| **Sets** | `O(1)` для **SADD**/**SISMEMBER** | Уникальные значения, теги |
| **Hashes** | `O(1)` для **HGET**/**HSET** | Объекты, профили |
| **Sorted Sets** | `O(log N)` для **ZADD**/**ZRANGE** | Рейтинги, временные ряды |
| **Streams** | `O(1)` для **XADD** | Логи, очереди сообщений |
| **Bitmaps** | `O(1)` для **SETBIT**/**GETBIT** | Аналитика, флаги |
| **HyperLogLog** | `O(1)` для **PFADD** | Приблизительный подсчет |

### Рекомендации по выбору

1. **Простые значения** **Strings**
2. **Упорядоченные коллекции** **Lists**
3. **Уникальные значения** **Sets**
4. **Объекты с полями** **Hashes**
5. **Рейтинги и сортировка** **Sorted Sets**
6. **Логи и события** **Streams**
7. **Битовые операции** **Bitmaps**
8. **Приблизительный подсчет** **HyperLogLog**
9. **Географические данные** **Geospatial**

## Advanced Patterns and Examples

### Strings: Advanced Use Cases

#### Caching with Expiration

```java
// Java пример кэширования с истечением
public class CacheManager {
    private JedisPool jedisPool;

    public CacheManager(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void cache(String key, String value, int ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttlSeconds, value);
        }
    }

    public String getCached(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public void refreshCache(String key, int ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.expire(key, ttlSeconds);
        }
    }
}
```

#### Distributed Counters

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class DistributedCounter {
    private JedisPool jedisPool;

    public DistributedCounter(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public long increment(String key, long amount) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incrBy(key, amount);
        }
    }

    public long decrement(String key, long amount) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decrBy(key, amount);
        }
    }

    public long get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            return value != null ? Long.parseLong(value) : 0;
        }
    }

    public void reset(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public long incrementWithExpiry(String key, long amount, int ttl) {
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipe = jedis.pipelined();
            Response<Long> result = pipe.incrBy(key, amount);
            pipe.expire(key, ttl);
            pipe.sync();
            return result.get();
        }
    }
}
```

### Lists: Queue Implementation

```java
// Java пример реализации очереди
public class RedisQueue {
    private JedisPool jedisPool;
    private String queueName;

    public RedisQueue(JedisPool jedisPool, String queueName) {
        this.jedisPool = jedisPool;
        this.queueName = queueName;
    }

    public void enqueue(String item) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.rpush(queueName, item);
        }
    }

    public String dequeue() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpop(queueName);
        }
    }

    public String blockingDequeue(int timeout) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> result = jedis.blpop(timeout, queueName);
            return result != null && result.size() > 1 ? result.get(1) : null;
        }
    }

    public long size() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(queueName);
        }
    }
}
```

### Sets: Tag System

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.Set;

public class TagSystem {
    private JedisPool jedisPool;

    public TagSystem(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void addTags(String itemId, String... tags) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "item:" + itemId + ":tags";
            jedis.sadd(key, tags);
            // Также добавить элемент в тег
            for (String tag : tags) {
                jedis.sadd("tag:" + tag + ":items", itemId);
            }
        }
    }

    public Set<String> getTags(String itemId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "item:" + itemId + ":tags";
            return jedis.smembers(key);
        }
    }

    public Set<String> getItemsByTag(String tag) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers("tag:" + tag + ":items");
        }
    }

    public Set<String> getItemsByTags(String... tags) {
        if (tags.length == 0) {
            return java.util.Collections.emptySet();
        }
        try (Jedis jedis = jedisPool.getResource()) {
            String[] keys = new String[tags.length];
            for (int i = 0; i < tags.length; i++) {
                keys[i] = "tag:" + tags[i] + ":items";
            }
            return jedis.sinter(keys);
        }
    }

    public void removeTag(String itemId, String tag) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.srem("item:" + itemId + ":tags", tag);
            jedis.srem("tag:" + tag + ":items", itemId);
        }
    }
}
```

### Hashes: User Profile Management

```java
// Java пример управления профилями пользователей
public class UserProfileManager {
    private JedisPool jedisPool;

    public UserProfileManager(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void createProfile(String userId, Map<String, String> profile) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "user:" + userId;
            jedis.hset(key, profile);
            jedis.expire(key, 86400); // 24 часа
        }
    }

    public Map<String, String> getProfile(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "user:" + userId;
            return jedis.hgetAll(key);
        }
    }

    public void updateField(String userId, String field, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "user:" + userId;
            jedis.hset(key, field, value);
        }
    }

    public void incrementAge(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "user:" + userId;
            jedis.hincrBy(key, "age", 1);
        }
    }
}
```

### Sorted Sets: Leaderboard Implementation

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import java.util.Map;
import java.util.Set;

public class Leaderboard {
    private JedisPool jedisPool;
    private String name;

    public Leaderboard(JedisPool jedisPool, String leaderboardName) {
        this.jedisPool = jedisPool;
        this.name = leaderboardName;
    }

    public void addPlayer(String playerId, double score) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(name, score, playerId);
        }
    }

    public double incrementScore(String playerId, double increment) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zincrby(name, increment, playerId);
        }
    }

    public Long getRank(String playerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long rank = jedis.zrevrank(name, playerId);
            return rank != null ? rank + 1 : null;
        }
    }

    public Set<Tuple> getTopPlayers(int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrangeWithScores(name, 0, count - 1);
        }
    }

    public Set<Tuple> getPlayersAround(String playerId, int rangeSize) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long rank = jedis.zrevrank(name, playerId);
            if (rank == null) {
                return java.util.Collections.emptySet();
            }

            long start = Math.max(0, rank - rangeSize);
            long end = rank + rangeSize;
            return jedis.zrevrangeWithScores(name, start, end);
        }
    }

    public Double getScore(String playerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zscore(name, playerId);
        }
    }
}
```

### Streams: Event Logging System

```java
// Java пример системы логирования событий
public class EventLogger {
    private JedisPool jedisPool;
    private String streamName;

    public EventLogger(JedisPool jedisPool, String streamName) {
        this.jedisPool = jedisPool;
        this.streamName = streamName;
    }

    public String logEvent(String eventType, Map<String, String> data) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> fields = new HashMap<>();
            fields.put("event_type", eventType);
            fields.put("timestamp", String.valueOf(System.currentTimeMillis()));
            fields.putAll(data);

            return jedis.xadd(streamName, StreamEntryID.NEW_ENTRY, fields);
        }
    }

    public List<Map<String, String>> readEvents(int count) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<Entry> entries = jedis.xrange(streamName, null, null, count);
            return entries.stream()
                .map(entry -> entry.getFields())
                .collect(Collectors.toList());
        }
    }

    public void createConsumerGroup(String groupName) {
        try (Jedis jedis = jedisPool.getResource()) {
            try {
                jedis.xgroupCreate(streamName, groupName,
                    new StreamEntryID("0"), true);
            } catch (JedisDataException e) {
                // Group already exists
            }
        }
    }
}
```

### Bitmaps: User Activity Tracking

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ActivityTracker {
    private JedisPool jedisPool;

    public ActivityTracker(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void markActive(String userId, LocalDate date) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "activity:" + userId + ":" + date.format(DateTimeFormatter.ISO_DATE);
            // Используем день месяца как бит
            int day = date.getDayOfMonth();
            jedis.setbit(key, day, true);
            // Установить TTL на 32 дня
            jedis.expire(key, 32 * 86400);
        }
    }

    public boolean isActive(String userId, LocalDate date) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "activity:" + userId + ":" + date.format(DateTimeFormatter.ISO_DATE);
            int day = date.getDayOfMonth();
            return jedis.getbit(key, day);
        }
    }

    public long getActiveDays(String userId, LocalDate date) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "activity:" + userId + ":" + date.format(DateTimeFormatter.ISO_DATE);
            return jedis.bitcount(key);
        }
    }

    public int getConsecutiveDays(String userId, LocalDate startDate, LocalDate endDate) {
        // Реализация через анализ битов
        int maxConsecutive = 0;
        int currentConsecutive = 0;

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            if (isActive(userId, current)) {
                currentConsecutive++;
                maxConsecutive = Math.max(maxConsecutive, currentConsecutive);
            } else {
                currentConsecutive = 0;
            }
            current = current.plusDays(1);
        }

        return maxConsecutive;
    }
}
```
        **max_consecutive** = 0
        **current_consecutive** = 0

        # Итерация по датам
        # ... (упрощенная версия)
        **return max_consecutive**
```text

## Memory Optimization

### Choosing the Right Data Structure

```
# Плохо: Множество отдельных ключей
`SET` user:1000:name "`John`"
`SET` user:1000:email "john`@example`.com"
`SET` user:1000:age "30"

# Хорошо: Использование хэша
`HSET` user:1000 name "`John`" email "john`@example`.com" age "30"

# Плохо: Хранение списка как строки
`SET` mylist "item1,item2,item3"

# Хорошо: Использование списка
`LPUSH` mylist "item1" "item2" "item3"
```text

### Memory-Efficient Patterns

```
# Использование небольших хэшей для группировки
# Вместо множества ключей user:1000:field1, user:1000:field2
# Используйте один хэш user:1000 с полями field1, field2

# Использование `Sorted Sets` для временных рядов
# Вместо множества ключей timestamp:value
# Используйте один `Sorted Set` с timestamp как score

# Использование `Bitmaps` для булевых флагов
# Вместо множества ключей flag:user:1000:day:1
# Используйте `Bitmap` с битами
```text

## Performance Considerations

### Operation Complexity

```
# `O(1)` операции
`GET` key
`SET` key value
`SADD` set member
`HSET` hash field value
`ZADD` zset score member

# `O(N)` операции
`KEYS` pattern          # Избегать в production!
`SMEMBERS` set          # Для больших множеств
`LRANGE list 0` -1      # Для больших списков

# `O(log N)` операции
`ZRANGE zset 0` -1      # Для sorted sets
`ZRANK` zset member     # Для sorted sets
```text

### Лучшие практики

1. Используйте SCAN вместо KEYS для перебора ключей
2. Используйте Pipeline для множественных операций
3. Используйте хэши вместо множества отдельных ключей
4. Ограничивайте размер списков и множеств
5. Используйте TTL для автоматической очистки

## Real-World Integration Examples

### E-Commerce: Shopping Cart

```
// `Java` пример корзины покупок
public class `ShoppingCart` {
    private `JedisPool jedisPool`;
    private static final int CART_TTL = 7 24  `3600`; // 7 дней

    public `ShoppingCart`(`JedisPool jedisPool`) {
        this.`jedisPool` = `jedisPool`;
    }

    public void `addItem`(`String userId`, `String productId`, int quantity) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            `String cartKey` = "cart:" + `userId`;
            jedis.`hincrBy`(`cartKey`, `productId`, quantity);
            `jedis.expire`(`cartKey`, CART_TTL);
        }
    }

    public void `removeItem`(`String userId`, `String productId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            `String cartKey` = "cart:" + `userId`;
            `jedis.hdel`(`cartKey`, `productId`);
        }
    }

    public Map<`String`, `String`> `getCart`(`String userId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            `String cartKey` = "cart:" + `userId`;
            return jedis.`hgetAll`(`cartKey`);
        }
    }

    public void `clearCart`(`String userId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            `String cartKey` = "cart:" + `userId`;
            `jedis.del`(`cartKey`);
        }
    }
}
```text

### Social Media: Followers System

```
// `Java` пример системы подписчиков
public class `FollowersSystem` {
    private `JedisPool jedisPool`;

    public `FollowersSystem`(`JedisPool jedisPool`) {
        this.`jedisPool` = `jedisPool`;
    }

    public void follow(`String followerId`, `String followeeId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            // Добавить в список подписок
            `jedis.sadd`("user:" + `followerId` + ":following", `followeeId`);
            // Добавить в список подписчиков
            `jedis.sadd`("user:" + `followeeId` + ":followers", `followerId`);
            // Обновить счетчики
            jedis.`hincrBy`("user:" + `followerId` + ":stats", "`following_count`", 1);
            jedis.`hincrBy`("user:" + `followeeId` + ":stats", "`followers_count`", 1);
        }
    }

    public void unfollow(`String followerId`, `String followeeId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            `jedis.srem`("user:" + `followerId` + ":following", `followeeId`);
            `jedis.srem`("user:" + `followeeId` + ":followers", `followerId`);
            jedis.`hincrBy`("user:" + `followerId` + ":stats", "`following_count`", -1);
            jedis.`hincrBy`("user:" + `followeeId` + ":stats", "`followers_count`", -1);
        }
    }

    public `java.util`.Set<`String`> `getFollowers`(`String userId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            return `jedis.smembers`("user:" + `userId` + ":followers");
        }
    }

    public `java.util`.Set<`String`> `getFollowing`(`String userId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            return `jedis.smembers`("user:" + `userId` + ":following");
        }
    }

    public `java.util`.Set<`String`> `getMutualFollowers`(`String user1Id`, `String user2Id`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            return `jedis.sinter`(
                "user:" + `user1Id` + ":followers",
                "user:" + `user2Id` + ":followers"
            );
        }
    }

    public boolean `isFollowing`(`String followerId`, `String followeeId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            return `jedis.sismember`("user:" + `followerId` + ":following", `followeeId`);
        }
    }
}
```text

### Analytics: Page View Counter

```
// `Java` пример счетчика просмотров страниц
public class `PageViewCounter` {
    private `JedisPool jedisPool`;

    public `PageViewCounter`(`JedisPool jedisPool`) {
        this.`jedisPool` = `jedisPool`;
    }

    public void `incrementView`(`String pageId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            `String today` = `LocalDate`.now().`toString()`;
            `String key` = "pageviews:" + `pageId` + ":" + today;
            `jedis.incr`(key);
            `jedis.expire`(key, `86400` * 7); // 7 дней
        }
    }

    public long `getViews`(`String pageId`, `String date`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            `String key` = "pageviews:" + `pageId` + ":" + date;
            `String value` = `jedis.get`(key);
            return value != `null` ? `Long`.`parseLong`(value) : 0;
        }
    }

    public long `getTotalViews`(`String pageId`, int days) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            long total = 0;
            for (int i = 0; i < days; i++) {
                `String date` = `LocalDate`.now().`minusDays`(i).`toString()`;
                `String key` = "pageviews:" + `pageId` + ":" + date;
                `String value` = `jedis.get`(key);
                if (value != `null`) {
                    total += `Long`.`parseLong`(value);
                }
            }
            return total;
        }
    }
}
```text

### Gaming: Player Statistics

```
// `Java` пример статистики игроков
public class `PlayerStats` {
    private `JedisPool jedisPool`;

    public `PlayerStats`(`JedisPool jedisPool`) {
        this.`jedisPool` = `jedisPool`;
    }

    public void `recordKill`(`String playerId`, `String enemyId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            // Увеличить счетчик убийств
            jedis.`hincrBy`("player:" + `playerId` + ":stats", "kills", 1);
            // Добавить в список убитых врагов
            `jedis.sadd`("player:" + `playerId` + ":killed", `enemyId`);
            // Обновить рейтинг
            `jedis.zincrby`("leaderboard", 10, `playerId`);
        }
    }

    public void `recordDeath`(`String playerId`, `String killerId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            jedis.`hincrBy`("player:" + `playerId` + ":stats", "deaths", 1);
            `jedis.sadd`("player:" + `playerId` + ":`killed_by`", `killerId`);
            `jedis.zincrby`("leaderboard", -5, `playerId`);
        }
    }

    public double `getKDRatio`(`String playerId`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            `java.util`.`List`<`String`> stats = `jedis.hmget`(
                "player:" + `playerId` + ":stats",
                "kills", "deaths"
            );

            int kills = `Integer`.`parseInt`(`stats.get`(0) != `null` ? `stats.get`(0) : "0");
            int deaths = `Integer`.`parseInt`(`stats.get`(1) != `null` ? `stats.get`(1) : "0");

            return deaths > 0 ? (double) kills / deaths : kills;
        }
    }

    public `java.util`.Set<`java.util`.Map.`Entry`<`String`, `Double`>> `getTopKillers`(int count) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            return jedis.`zrevrangeWithScores`("leaderboard", 0, count — 1);
        }
    }
}
```text

## Advanced Data Structure Patterns

### Time-Series with Sorted Sets

```
# Хранение временных рядов
# Используем timestamp как score
`ZADD` temperature:2023-01-16 `1642320000` "25.5"
`ZADD` temperature:2023-01-16 `1642323600` "26.0"
`ZADD` temperature:2023-01-16 `1642327200` "25.8"

# Получение данных за период
`ZRANGEBYSCORE` temperature:2023-01-16 `1642320000 1642327200`

# Агрегация данных
`ZCOUNT` temperature:2023-01-16 `1642320000 1642327200`
```text

### Rate Limiting with Sorted Sets

```
-- Lua скрипт для rate limiting
local key = `KEYS`[1]
local window = tonumber(`ARGV`[1])
local limit = tonumber(`ARGV`[2])

local now = `redis.call`('`TIME`')[1]
local `window_start` = now — window

-- Удалить старые записи
`redis.call`('`ZREMRANGEBYSCORE`', key, '-inf', `window_start`)

-- Добавить текущий запрос
`redis.call`('`ZADD`', key, now, now)

-- Установить `TTL`
`redis.call`('`EXPIRE`', key, window)

-- Проверить лимит
local current = `redis.call`('`ZCARD`', key)
if current > limit then
    return {0, current}
else
    return {1, current}
end
```text

### Distributed Lock with Strings

```
-- Lua скрипт для distributed lock
local `lock_key` = `KEYS`[1]
local `lock_value` = `ARGV`[1]
local ttl = tonumber(`ARGV`[2])

-- Попытаться установить lock
local result = `redis.call`('`SET`', `lock_key`, `lock_value`, '`NX`', '`PX`', ttl)

if result then
    `return 1`  — `Lock acquired`
else
    `return 0`  — `Lock not acquired`
end
```text

### Cache-Aside Pattern

```
// `Java` пример `cache-aside` pattern
public class `CacheAsideCache` {
    private `JedisPool jedisPool`;
    private `Database database`; // Предполагаем наличие БД

    public `CacheAsideCache`(`JedisPool jedisPool`, `Database database`) {
        this.`jedisPool` = `jedisPool`;
        `this.database` = database;
    }

    public `String get`(`String key`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            // Попытка получить из кэша
            `String value` = `jedis.get`(key);
            if (value != `null`) {
                return value;
            }

            // Получить из БД
            value = `database.get`(key);
            if (value != `null`) {
                // Сохранить в кэш
                `jedis.setex`(key, `3600`, value);
            }
            return value;
        }
    }

    public void set(`String key`, `String value`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            // Обновить БД
            `database.set`(key, value);
            // Обновить кэш
            `jedis.setex`(key, `3600`, value);
        }
    }

    public void delete(`String key`) {
        try (`Jedis jedis` = `jedisPool`.`getResource()`) {
            // Удалить из БД
            `database.delete`(key);
            // Удалить из кэша
            `jedis.del`(key);
        }
    }
}
```text

## Лучшие практики

### Memory Efficiency

```
# Использование небольших хэшей
# Вместо:
`SET` user:1000:name "`John`"
`SET` user:1000:email "john`@example`.com"
# Используйте:
`HSET` user:1000 name "`John`" email "john`@example`.com"

# Использование `Sorted Sets` для временных рядов
# Вместо множества ключей timestamp:value
# Используйте один `Sorted Set` с timestamp как score

# Использование `Bitmaps` для булевых флагов
# Вместо множества ключей flag:user:1000:day:1
# Используйте `Bitmap` с битами
```text

### Performance Optimization

```
# Использование `Pipeline` для множественных операций
# Вместо:
`SET` key1 "value1"
`SET` key2 "value2"
`SET` key3 "value3"
# Используйте `Pipeline`:
`MULTI`
`SET` key1 "value1"
`SET` key2 "value2"
`SET` key3 "value3"
`EXEC`

# Использование `SCAN` вместо `KEYS`
# Вместо:
`KEYS` user:*
# Используйте:
`SCAN 0 MATCH` user:* `COUNT 100`
```text

## Решение проблем

### Issue: Large Sets/Lists

```
# Проблема: Большие множества или списки замедляют операции
# Решение: Разбить на несколько меньших структур

# Вместо одного большого множества:
`SADD all_users` user1 user2 ... user1000000

# Используйте несколько множеств:
`SADD` users:0 user1 user2 ... user10000
`SADD` users:1 user10001 user10002 ... user20000
```text

### Issue: Memory Usage

```
# Проблема: Высокое использование памяти
# Решение: Использовать более эффективные структуры данных

# Использовать хэши вместо множества ключей
# Использовать `Bitmaps` для булевых значений
# Использовать `HyperLogLog` для приблизительного подсчета
# Установить `TTL` для временных данных
```text

### Issue: Slow Operations

```
# Проблема: Медленные операции
# Решение: Проверить сложность операций

# Избегать `O(N)` операций на больших структурах
# Использовать индексы (`Sorted Sets`) для поиска
# Использовать `Pipeline` для множественных операций
```text
```


## См. также

- [[redis-basics|Redis: Основы]]
- [[redis-clustering|Redis: Кластеризация]]
- [[redis-geospatial|Redis: Геопространственные данные]]
- [[redis-high-availability|Redis: Высокая доступность]]
- [[redis-lua-scripting|Redis: Lua Scripting]]
