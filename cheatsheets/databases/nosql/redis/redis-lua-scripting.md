---
title: "Redis: Lua Scripting"
description: "Полное руководство по Lua скриптингу в Redis: EVAL, EVALSHA, атомарные операции, оптимизация, best practices"
tags: ["redis", "lua", "scripting", "eval", "evalsha", "atomic", "transactions"]
difficulty: "advanced"
prerequisites: ["databases/redis-basics.md", "databases/redis-data-structures.md"]
next: ["databases/redis-pubsub.md", "databases/redis-transactions.md"]
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-data-structures.md"]
---

# **Redis**: **Lua Scripting**

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Scripting](https://redis.io/docs/manual/programmability/eval-intro/) — Lua-скрипты

### См. также
- [redis-basics.md](redis-basics.md) — основы Redis
- [redis-transactions.md](redis-transactions.md) — транзакции

## Содержание

- [Введение в **Lua Scripting**](#введение-в-lua-scripting)
  - [Преимущества **Lua Scripting**](#преимущества-lua-scripting)
- [Базовое использование](#базовое-использование)
  - [**EVAL** команда](#eval-команда)
  - [**EVALSHA** команда](#evalsha-команда)
- [Основные функции **Redis** в **Lua**](#основные-функции-redis-в-lua)
  - [**redis.call**()](#redis-call)
  - [**redis.pcall**()](#redis-pcall)
  - [**redis.error_reply**()](#redis-errorreply)
  - [**redis.status_reply**()](#redis-statusreply)
- [Практические примеры](#практические-примеры)
  - [Атомарный перевод денег](#атомарный-перевод-денег)
  - [**Rate Limiting**](#rate-limiting)
  - [**Distributed Lock**](#distributed-lock)
  - [**Cache Aside Pattern**](#cache-aside-pattern)
- [Оптимизация **Lua** скриптов](#оптимизация-lua-скриптов)
  - [Лучшие практики](#лучшие-практики)
  - [Оптимизация производительности](#оптимизация-производительности)
- [**Advanced Lua Patterns**](#advanced-lua-patterns)
  - [**Atomic Operations**](#atomic-operations)
  - [**Batch Operations**](#batch-operations)
  - [**Conditional Logic**](#conditional-logic)
  - [**Error Handling**](#error-handling)
- [**Performance Optimization**](#performance-optimization)
  - [**Script Caching**](#script-caching)
  - [**Minimizing Round Trips**](#minimizing-round-trips)
- [**Real-World Examples**](#real-world-examples)
  - [**Shopping Cart Operations**](#shopping-cart-operations)
  - [**Leaderboard Update**](#leaderboard-update)
  - [**Distributed Counter**](#distributed-counter)
- [**Advanced Script Management**](#advanced-script-management)
  - [**Script Loading and Caching**](#script-loading-and-caching)
  - [**Script Optimization Techniques**](#script-optimization-techniques)
  - [**Error Handling Patterns**](#error-handling-patterns)
- [**Real-World Script Examples**](#real-world-script-examples)
  - [**Distributed Rate Limiter**](#distributed-rate-limiter)
  - [**Atomic Counter with Expiry**](#atomic-counter-with-expiry)
  - [**Conditional List Operations**](#conditional-list-operations)
  - [**Hash Field Updates**](#hash-field-updates)
- [Лучшие практики производительности скриптов](#лучшие-практики-производительности-скриптов)
  - [**Minimizing Redis Calls**](#minimizing-redis-calls)
  - [**Using Local Variables**](#using-local-variables)
  - [**Avoiding Large Loops**](#avoiding-large-loops)
- [**Script Testing**](#script-testing)
  - [**Unit Testing Lua Scripts**](#unit-testing-lua-scripts)

## Введение в **Lua Scripting**

**Lua** скриптинг в **Redis** позволяет выполнять сложные операции атомарно на сервере, что критически важно для обеспечения консистентности данных и оптимизации производительности.

### Преимущества **Lua Scripting**

- **Атомарность**: Все команды выполняются атомарно
- **Производительность**: Меньше **round-trips** между клиентом и сервером
- **Консистентность**: Гарантия целостности данных
- **Гибкость**: Возможность реализации сложной логики

---

## Базовое использование

### **EVAL** команда

```redis
# Базовый синтаксис
EVAL script numkeys key [key ...] arg [arg ...]

# Простой пример
EVAL "return redis.call('GET', KEYS[1])" 1 mykey

# С аргументами
EVAL "return redis.call('SET', KEYS[1], ARGV[1])" 1 mykey "value"
```

### **EVALSHA** команда

```redis
# Загрузка скрипта
SCRIPT LOAD "return redis.call('GET', KEYS[1])"
# Возвращает SHA1 хэш: 4e6d8fc8bb01276962cce5371fa795a7763657ae

# Выполнение по хэшу
EVALSHA 4e6d8fc8bb01276962cce5371fa795a7763657ae 1 mykey
```

---

## Основные функции **Redis** в **Lua**

### **redis.call**()

```lua
-- Выполнение команды Redis
local value = redis.call('GET', 'mykey')
redis.call('SET', 'mykey', 'newvalue')

-- Команды возвращают значения
local result = redis.call('INCR', 'counter')
```

### **redis.pcall**()

```lua
-- Выполнение команды с обработкой ошибок
local result = redis.pcall('GET', 'mykey')
if result['err'] then
    -- Обработка ошибки
    return nil
end
```

### **redis.error_reply**()

```lua
-- Возврат ошибки
if condition then
    return redis.error_reply('Error message')
end
```

### **redis.status_reply**()

```lua
-- Возврат статуса
return redis.status_reply('OK')
```

---

## Практические примеры

### Атомарный перевод денег

```lua
-- transfer.lua
local from_account = KEYS[1]
local to_account = KEYS[2]
local amount = tonumber(ARGV[1])

-- Получить балансы
local from_balance = tonumber(redis.call('GET', from_account) or 0)
local to_balance = tonumber(redis.call('GET', to_account) or 0)

-- Проверить достаточность средств
if from_balance < amount then
    return redis.error_reply("Insufficient funds")
end

-- Выполнить перевод
redis.call('SET', from_account, from_balance - amount)
redis.call('SET', to_account, to_balance + amount)

-- Вернуть новые балансы
return {from_balance - amount, to_balance + amount}
```

```bash
# Выполнение скрипта
redis-cli --eval transfer.lua from_account to_account , 100
```

### **Rate Limiting**

```lua
-- rate_limit.lua
local key = KEYS[1]
local window = tonumber(ARGV[1])
local limit = tonumber(ARGV[2])

-- Использовать sorted set для хранения запросов
local now = redis.call('TIME')[1]
local window_start = now - window

-- Удалить старые записи
redis.call('ZREMRANGEBYSCORE', key, '-inf', window_start)

-- Добавить текущий запрос
redis.call('ZADD', key, now, now)

-- Установить TTL
redis.call('EXPIRE', key, window)

-- Проверить лимит
local current_requests = redis.call('ZCARD', key)
if current_requests > limit then
    return redis.error_reply("Rate limit exceeded")
end

return current_requests
```

### **Distributed Lock**

```lua
-- acquire_lock.lua
local lock_key = KEYS[1]
local lock_value = ARGV[1]
local ttl = tonumber(ARGV[2])

-- Попытаться установить lock
local result = redis.call('SET', lock_key, lock_value, 'NX', 'PX', ttl)

if result then
    return 1  -- Lock acquired
else
    return 0  -- Lock not acquired
end
```

```lua
-- release_lock.lua
local lock_key = KEYS[1]
local lock_value = ARGV[1]

-- Проверить, что lock принадлежит нам
if redis.call('GET', lock_key) == lock_value then
    redis.call('DEL', lock_key)
    return 1
else
    return 0
end
```

### **Cache Aside Pattern**

```lua
-- cache_aside.lua
local key = KEYS[1]
local ttl = tonumber(ARGV[1])

-- Попытаться получить из кэша
local cached = redis.call('GET', key)
if cached then
    return cached
end

-- Здесь была бы загрузка из БД
-- Для примера возвращаем nil
return nil
```

---

## Оптимизация **Lua** скриптов

### Лучшие практики

1. **Используйте EVALSHA** вместо **EVAL** для часто используемых скриптов
2. **Минимизируйте количество команд Redis** в скрипте
3. **Используйте локальные переменные** для повторяющихся значений
4. **Избегайте больших циклов** в **Lua**
5. **Обрабатывайте ошибки** через **redis.pcall**()

### Оптимизация производительности

```lua
-- Плохо: Множественные вызовы
for i = 1, 100 do
    redis.call('SET', 'key' .. i, 'value' .. i)
end

-- Хорошо: Использование Pipeline через Lua
local results = {}
for i = 1, 100 do
    table.insert(results, redis.call('SET', 'key' .. i, 'value' .. i))
end
return results
```

## **Advanced Lua Patterns**

### **Atomic Operations**

```lua
-- Атомарное обновление с проверкой
local key = KEYS[1]
local expected_value = ARGV[1]
local new_value = ARGV[2]

local current_value = redis.call('GET', key)
if current_value == expected_value then
    redis.call('SET', key, new_value)
    return 1
else
    return 0
end
```

### **Batch Operations**

```lua
-- Пакетная обработка ключей
local keys = redis.call('KEYS', ARGV[1])
local results = {}

for i = 1, #keys do
    local value = redis.call('GET', keys[i])
    table.insert(results, {keys[i], value})
end

return results
```

### **Conditional Logic**

```lua
-- Условная логика
local key = KEYS[1]
local threshold = tonumber(ARGV[1])

local value = tonumber(redis.call('GET', key) or 0)

if value > threshold then
    redis.call('SET', key, 0)
    return 'reset'
else
    redis.call('INCR', key)
    return 'incremented'
end
```

### **Error Handling**

```lua
-- Обработка ошибок
local key = KEYS[1]

local result = redis.pcall('GET', key)
if result['err'] then
    return redis.error_reply('Failed to get key: ' .. result['err'])
end

return result
```

## **Performance Optimization**

### **Script Caching**

```bash
# Загрузка скрипта один раз
SCRIPT LOAD "$(cat script.lua)"

# Использование SHA для выполнения
EVALSHA <sha1> numkeys key [key ...] arg [arg ...]
```

### **Minimizing Round Trips**

```lua
-- Плохо: Множественные round-trips
local value1 = redis.call('GET', 'key1')
local value2 = redis.call('GET', 'key2')
local value3 = redis.call('GET', 'key3')

-- Хорошо: Одна операция
local values = redis.call('MGET', 'key1', 'key2', 'key3')
```

## **Real-World Examples**

### **Shopping Cart Operations**

```lua
-- add_to_cart.lua
local user_id = ARGV[1]
local product_id = ARGV[2]
local quantity = tonumber(ARGV[3])
local cart_key = 'cart:' .. user_id

-- Получить текущее количество
local current_qty = tonumber(redis.call('HGET', cart_key, product_id) or 0)

-- Обновить количество
redis.call('HSET', cart_key, product_id, current_qty + quantity)

-- Установить TTL
redis.call('EXPIRE', cart_key, 3600)

return current_qty + quantity
```

### **Leaderboard Update**

```lua
-- update_leaderboard.lua
local leaderboard_key = KEYS[1]
local player_id = ARGV[1]
local score_delta = tonumber(ARGV[2])

-- Обновить счет
local new_score = redis.call('ZINCRBY', leaderboard_key, score_delta, player_id)

-- Получить ранг
local rank = redis.call('ZREVRANK', leaderboard_key, player_id)

return {new_score, rank}
```

### **Distributed Counter**

```lua
-- distributed_counter.lua
local counter_key = KEYS[1]
local increment = tonumber(ARGV[1])
local max_value = tonumber(ARGV[2])

local current = tonumber(redis.call('GET', counter_key) or 0)
local new_value = current + increment

if new_value > max_value then
    return redis.error_reply('Counter would exceed maximum')
end

redis.call('SET', counter_key, new_value)
return new_value
```

## **Advanced Script Management**

### **Script Loading and Caching**

```bash
# Загрузка скрипта
SCRIPT LOAD "$(cat script.lua)"

# Проверка существования скрипта
SCRIPT EXISTS <sha1> [sha1 ...]

# Удаление скрипта из кэша
SCRIPT FLUSH

# Убить выполняющийся скрипт
SCRIPT KILL
```

### **Script Optimization Techniques**

```lua
-- Использование локальных переменных
local key_prefix = "user:"
local user_id = ARGV[1]
local key = key_prefix .. user_id

-- Избегать повторных вызовов
local value = redis.call('GET', key)
if value then
    return value
else
    -- Загрузка из БД (симуляция)
    local db_value = "loaded_from_db"
    redis.call('SET', key, db_value)
    return db_value
end
```

### **Error Handling Patterns**

```lua
-- Комплексная обработка ошибок
local function safe_get(key)
    local result = redis.pcall('GET', key)
    if result['err'] then
        return nil, result['err']
    end
    return result, nil
end

local value, err = safe_get(KEYS[1])
if err then
    return redis.error_reply('Failed to get key: ' .. err)
end

return value
```

## **Real-World Script Examples**

### **Distributed Rate Limiter**

```lua
-- distributed_rate_limiter.lua
local key = KEYS[1]
local window = tonumber(ARGV[1])
local limit = tonumber(ARGV[2])

local now = redis.call('TIME')[1]
local window_start = now - window

-- Использовать sorted set для хранения запросов
redis.call('ZREMRANGEBYSCORE', key, '-inf', window_start)
redis.call('ZADD', key, now, now)
redis.call('EXPIRE', key, window)

local count = redis.call('ZCARD', key)
if count > limit then
    return {0, count, limit}
else
    return {1, count, limit}
end
```

### **Atomic Counter with Expiry**

```lua
-- atomic_counter_with_expiry.lua
local key = KEYS[1]
local increment = tonumber(ARGV[1])
local ttl = tonumber(ARGV[2])

local current = tonumber(redis.call('GET', key) or 0)
local new_value = current + increment

redis.call('SET', key, new_value)
redis.call('EXPIRE', key, ttl)

return new_value
```

### **Conditional List Operations**

```lua
-- conditional_list_ops.lua
local list_key = KEYS[1]
local value = ARGV[1]
local max_length = tonumber(ARGV[2])

-- Добавить в начало списка
redis.call('LPUSH', list_key, value)

-- Обрезать до максимальной длины
local length = redis.call('LLEN', list_key)
if length > max_length then
    redis.call('LTRIM', list_key, 0, max_length - 1)
end

return redis.call('LLEN', list_key)
```

### **Hash Field Updates**

```lua
-- hash_field_updates.lua
local hash_key = KEYS[1]
local field = ARGV[1]
local increment = tonumber(ARGV[2])
local max_value = tonumber(ARGV[3])

local current = tonumber(redis.call('HGET', hash_key, field) or 0)
local new_value = current + increment

if new_value > max_value then
    return redis.error_reply('Value would exceed maximum')
end

redis.call('HSET', hash_key, field, new_value)
return new_value
```

## Лучшие практики производительности скриптов

### **Minimizing Redis Calls**

```lua
-- Плохо: Множественные вызовы
local value1 = redis.call('GET', 'key1')
local value2 = redis.call('GET', 'key2')
local value3 = redis.call('GET', 'key3')

-- Хорошо: Один вызов
local values = redis.call('MGET', 'key1', 'key2', 'key3')
```

### **Using Local Variables**

```lua
-- Плохо: Повторные вычисления
if redis.call('GET', 'key1') == 'value' then
    redis.call('SET', 'key2', redis.call('GET', 'key1'))
end

-- Хорошо: Использование локальной переменной
local value = redis.call('GET', 'key1')
if value == 'value' then
    redis.call('SET', 'key2', value)
end
```

### **Avoiding Large Loops**

```lua
-- Плохо: Большой цикл с вызовами Redis
for i = 1, 10000 do
    redis.call('SET', 'key' .. i, 'value' .. i)
end

-- Хорошо: Использование Pipeline через Lua
-- Или разбить на батчи
local batch_size = 100
for i = 1, 10000, batch_size do
    local batch = {}
    for j = i, math.min(i + batch_size - 1, 10000) do
        table.insert(batch, 'key' .. j)
        table.insert(batch, 'value' .. j)
    end
    redis.call('MSET', unpack(batch))
end
```

## **Script Testing**

### **Unit Testing Lua Scripts**

```java
// Java пример тестирования Lua скриптов
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class LuaScriptTest {
    private JedisPool jedisPool;
    private Jedis jedis;

    @BeforeEach
    void setUp() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(10);
        jedisPool = new JedisPool(config, "localhost", 6379);
        jedis = jedisPool.getResource();
        jedis.flushDB();
    }

    @AfterEach
    void tearDown() {
        jedis.close();
        jedisPool.close();
    }

    @Test
    void testTransferScript() {
        // Lua скрипт перевода денег
        String script = """
            local from = KEYS[1]
            local to = KEYS[2]
            local amount = tonumber(ARGV[1])
            local from_bal = tonumber(redis.call('GET', from) or 0)
            local to_bal = tonumber(redis.call('GET', to) or 0)
            if from_bal < amount then
                return redis.error_reply('Insufficient funds')
            end
            redis.call('SET', from, from_bal - amount)
            redis.call('SET', to, to_bal + amount)
            return {from_bal - amount, to_bal + amount}
            """;

        String sha = jedis.scriptLoad(script);

        // Установить начальные балансы
        jedis.set("account1", "100");
        jedis.set("account2", "50");

        // Выполнить перевод
        List<Long> result = (List<Long>) jedis.evalsha(sha, 2, "account1", "account2", "30");

        // Проверить результаты
        assertEquals(70L, result.get(0));
        assertEquals(80L, result.get(1));
        assertEquals("70", jedis.get("account1"));
        assertEquals("80", jedis.get("account2"));
    }
}
```

---

- [Redis Lua Scripting](https://redis.io/docs/manual/programmability/eval-intro/)
- [Lua 5.1 Reference](https://www.lua.org/manual/5.1/)

---

**Дата последнего обновления:** 2026-02-06


