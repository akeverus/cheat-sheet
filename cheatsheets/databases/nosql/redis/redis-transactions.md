---
title: "Redis: Транзакции"
description: "Полное руководство по транзакциям в Redis: MULTI/EXEC, WATCH, оптимистическая блокировка, атомарность, best practices"
tags:
  - redis
  - transactions
  - multi
  - exec
  - watch
  - atomic
  - locking
difficulty: "intermediate"
prerequisites: ["databases/redis-basics.md"]
next: ["databases/redis-lua-scripting.md"]
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-lua-scripting.md"]
---

# Redis: Транзакции

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Transactions](https://redis.io/docs/manual/transactions/) — транзакции

### См. также
- [[redis-basics|redis-basics.md]] — основы Redis
- [[redis-lua-scripting|redis-lua-scripting.md]] — Lua-скрипты

## Содержание

- [Введение в транзакции Redis](#введение-в-транзакции-redis)
  - [Основные концепции](#основные-концепции)
- [Базовые транзакции](#базовые-транзакции)
  - [Простая транзакция](#простая-транзакция)
  - [Отмена транзакции](#отмена-транзакции)
- [WATCH — Оптимистическая блокировка](#watch-оптимистическая-блокировка)
  - [Базовое использование](#базовое-использование)
  - [Пример использования](#пример-использования)
- [Обработка ошибок в транзакциях](#обработка-ошибок-в-транзакциях)
  - [Команды с ошибками](#команды-с-ошибками)
  - [Проверка перед выполнением](#проверка-перед-выполнением)
- [Лучшие практики](#лучшие-практики)
- [Advanced Transaction Patterns](#advanced-transaction-patterns)
  - [Conditional Transactions](#conditional-transactions)
  - [Batch Operations with Transactions](#batch-operations-with-transactions)
  - [Transaction with Rollback Logic](#transaction-with-rollback-logic)
- [Transaction vs Lua Scripts](#transaction-vs-lua-scripts)
  - [Когда использовать транзакции](#когда-использовать-транзакции)
  - [Когда использовать Lua скрипты](#когда-использовать-lua-скрипты)
- [Transaction Monitoring](#transaction-monitoring)
  - [Tracking Transaction Performance](#tracking-transaction-performance)
- [Transaction Patterns](#transaction-patterns)
  - [Optimistic Locking Pattern](#optimistic-locking-pattern)
  - [Transaction with Validation](#transaction-with-validation)
- [Transaction Monitoring](#transaction-monitoring-1)
  - [Tracking Transaction Performance](#tracking-transaction-performance-1)
- [Transaction Patterns](#transaction-patterns-1)
  - [Optimistic Locking Pattern](#optimistic-locking-pattern-1)
  - [Transaction with Validation](#transaction-with-validation-1)
  - [Batch Operations with Transactions](#batch-operations-with-transactions-1)
  - [Transaction with Rollback Logic](#transaction-with-rollback-logic-1)
- [Transaction vs Lua Scripts](#transaction-vs-lua-scripts-1)
  - [Когда использовать транзакции](#когда-использовать-транзакции-1)
  - [Когда использовать Lua скрипты](#когда-использовать-lua-скрипты-1)

## Введение в транзакции Redis

Транзакции в **Redis** обеспечивают атомарное выполнение группы команд. Все команды в транзакции выполняются последовательно и атомарно, без вмешательства других команд.

### Основные концепции

- **MULTI**: Начало транзакции
- **EXEC**: Выполнение транзакции
- **DISCARD**: Отмена транзакции
- **WATCH**: Оптимистическая блокировка ключей


## Базовые транзакции

### Простая транзакция

```redis
# Начало транзакции
MULTI

# Команды в транзакции
SET key1 "value1"
SET key2 "value2"
INCR counter

# Выполнение транзакции
EXEC

# Результат: [OK, OK, 1]
```

### Отмена транзакции

```redis
# Начало транзакции
MULTI

# Команды
SET key1 "value1"
SET key2 "value2"

# Отмена транзакции
DISCARD
```


## WATCH — Оптимистическая блокировка

### Базовое использование

```redis
# Наблюдение за ключом
WATCH mykey

# Начало транзакции
MULTI

# Изменение ключа
INCR mykey

# Выполнение
EXEC

# Если mykey был изменен другой транзакцией,
# EXEC вернет nil и транзакция не выполнится
```

### Пример использования

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

public class MoneyTransfer {
    private JedisPool jedisPool;

    public MoneyTransfer(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public boolean transferMoney(String fromAccount, String toAccount, int amount) {
        while (true) {
            try (Jedis jedis = jedisPool.getResource()) {
                // Наблюдать за обоими счетами
                jedis.watch(fromAccount, toAccount);

                // Получить балансы
                String fromBalanceStr = jedis.get(fromAccount);
                String toBalanceStr = jedis.get(toAccount);
                int fromBalance = fromBalanceStr != null ? Integer.parseInt(fromBalanceStr) : 0;
                int toBalance = toBalanceStr != null ? Integer.parseInt(toBalanceStr) : 0;

                // Проверить достаточность средств
                if (fromBalance < amount) {
                    jedis.unwatch();
                    return false;
                }

                // Начать транзакцию
                Transaction transaction = jedis.multi();
                transaction.set(fromAccount, String.valueOf(fromBalance - amount));
                transaction.set(toAccount, String.valueOf(toBalance + amount));
                List<Object> result = transaction.exec();

                // Если транзакция выполнена успешно
                if (result != null && !result.isEmpty()) {
                    return true;
                }
                // Иначе повторить попытку
            } catch (JedisException e) {
                // Ключ был изменен, повторить попытку
                continue;
            }
        }
    }
}
```


## Обработка ошибок в транзакциях

### Команды с ошибками

```redis
# Транзакция с ошибкой
MULTI
SET key1 "value1"
INCR key2  # Ошибка, если key2 не число
SET key3 "value3"
EXEC

# Redis выполнит команды до ошибки,
# затем остановится и вернет ошибку
```

### Проверка перед выполнением

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import java.util.List;
import java.util.Map;

public class SafeTransaction {
    private JedisPool jedisPool;

    public SafeTransaction(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public List<Object> safeTransaction(List<Operation> operations) {
        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();

            for (Operation op : operations) {
                if ("set".equals(op.getType())) {
                    transaction.set(op.getKey(), op.getValue());
                } else if ("incr".equals(op.getType())) {
                    // Проверить, что значение числовое
                    String current = jedis.get(op.getKey());
                    if (current != null && !current.matches("\\d+")) {
                        throw new IllegalArgumentException("Key " + op.getKey() + " is not numeric");
                    }
                    transaction.incr(op.getKey());
                }
            }

            try {
                return transaction.exec();
            } catch (Exception e) {
                System.err.println("Transaction failed: " + e.getMessage());
                return null;
            }
        }
    }

    public static class Operation {
        private String type;
        private String key;
        private String value;

        // Constructors, getters...
    }
}
```


## Лучшие практики

1. **Используйте WATCH** для оптимистической блокировки
2. **Обрабатывайте ошибки** при выполнении транзакций
3. **Избегайте длинных транзакций** для лучшей производительности
4. **Используйте Lua скрипты** для сложной логики
5. **Проверяйте результаты EXEC** перед использованием

## Advanced Transaction Patterns

### Conditional Transactions

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConditionalTransaction {
    private JedisPool jedisPool;

    public ConditionalTransaction(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public boolean conditionalUpdate(String key, Predicate<String> condition,
                                     Function<String, String> update) {
        while (true) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.watch(key);
                String currentValue = jedis.get(key);

                if (!condition.test(currentValue)) {
                    jedis.unwatch();
                    return false;
                }

                Transaction transaction = jedis.multi();
                String newValue = update.apply(currentValue);
                transaction.set(key, newValue);
                List<Object> result = transaction.exec();

                if (result != null && !result.isEmpty()) {
                    return true;
                }
            } catch (Exception e) {
                // Ключ был изменен, повторить попытку
                continue;
            }
        }
    }
}
```

### Batch Operations with Transactions

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import java.util.List;
import java.util.Map;

public class BatchUpdate {
    private JedisPool jedisPool;

    public BatchUpdate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public List<Object> batchUpdate(Map<String, String> updates) {
        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();

            for (Map.Entry<String, String> entry : updates.entrySet()) {
                transaction.set(entry.getKey(), entry.getValue());
            }

            return transaction.exec();
        }
    }
}
```

### Transaction with Rollback Logic

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import java.util.ArrayList;
import java.util.List;

public class TransactionWithRollback {
    private JedisPool jedisPool;
    private List<RollbackOperation> rollbackOps;

    public TransactionWithRollback(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.rollbackOps = new ArrayList<>();
    }

    public boolean executeWithRollback(List<Operation> operations) {
        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();

            // Сохранить текущие значения для отката
            for (Operation op : operations) {
                if ("set".equals(op.getType())) {
                    String oldValue = jedis.get(op.getKey());
                    rollbackOps.add(new RollbackOperation("set", op.getKey(), oldValue));
                    transaction.set(op.getKey(), op.getValue());
                }
            }

            try {
                List<Object> result = transaction.exec();
                if (result != null && !result.isEmpty()) {
                    rollbackOps.clear();
                    return true;
                }
                return false;
            } catch (Exception e) {
                rollback();
                throw new RuntimeException(e);
            }
        }
    }

    public void rollback() {
        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();

            for (RollbackOperation op : rollbackOps) {
                if ("set".equals(op.getType())) {
                    if (op.getValue() != null) {
                        transaction.set(op.getKey(), op.getValue());
                    } else {
                        transaction.del(op.getKey());
                    }
                }
            }

            transaction.exec();
            rollbackOps.clear();
        }
    }

    private static class RollbackOperation {
        private String type;
        private String key;
        private String value;

        RollbackOperation(String type, String key, String value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

        String getType() { return type; }
        String getKey() { return key; }
        String getValue() { return value; }
    }
}
```

## Transaction vs Lua Scripts

### Когда использовать транзакции

- Простые атомарные операции
- Оптимистическая блокировка через **WATCH**
- Группировка нескольких команд

### Когда использовать Lua скрипты

- Сложная логика с условиями
- Необходимость обработки ошибок внутри скрипта
- Минимизация **round-trips**

## Transaction Monitoring

### Tracking Transaction Performance

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionMonitor {
    private JedisPool jedisPool;
    private Map<String, Integer> stats;

    public TransactionMonitor(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.stats = new HashMap<>();
        this.stats.put("total", 0);
        this.stats.put("successful", 0);
        this.stats.put("failed", 0);
        this.stats.put("watch_errors", 0);
    }

    public List<Object> executeWithMonitoring(List<Operation> operations) {
        stats.put("total", stats.get("total") + 1);

        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();

            for (Operation op : operations) {
                if ("set".equals(op.getType())) {
                    transaction.set(op.getKey(), op.getValue());
                } else if ("get".equals(op.getType())) {
                    transaction.get(op.getKey());
                }
            }

            List<Object> result = transaction.exec();

            if (result != null && !result.isEmpty()) {
                stats.put("successful", stats.get("successful") + 1);
                return result;
            } else {
                stats.put("failed", stats.get("failed") + 1);
                return null;
            }
        } catch (Exception e) {
            stats.put("watch_errors", stats.get("watch_errors") + 1);
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> getStats() {
        return new HashMap<>(stats);
    }
}
```

## Transaction Patterns

### Optimistic Locking Pattern

```java
public static boolean optimisticUpdate(redis_client, key, update_func, max_retries=10):
    """Оптимистическое обновление с повторными попытками"""
    for attempt in range(max_retries):
        try:
            redis_client.watch(key)
            current_value = redis_client.get(key)
            new_value = update_func(current_value)

            pipe = redis_client.pipeline()
            pipe.multi()
            pipe.set(key, new_value)
            result = pipe.execute()

            if result:
                return new_value
        except redis.WatchError:
            if attempt == max_retries - 1:
                raise Exception("Max retries exceeded")
            time.sleep(0.1 * (attempt + 1))  # Exponential backoff

    raise Exception("Failed to update")
```

### Transaction with Validation

```java
public static List<Object> validatedTransaction(redis_client, operations, validators):
    """Транзакция с валидацией"""
    pipe = redis_client.pipeline()

    # Валидация перед транзакцией
    for i, op in enumerate(operations):
        if i in validators:
            validator = validators[i]
            if not validator(redis_client, op):
                raise ValueError(f"Validation failed for operation {i}")

    # Выполнение транзакции
    pipe.multi()
    for op in operations:
        if op['type'] == 'set':
            pipe.set(op['key'], op['value'])

    return pipe.execute()
```

## Transaction Monitoring

### Tracking Transaction Performance

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionMonitor {
    private JedisPool jedisPool;
    private Map<String, Integer> stats;

    public TransactionMonitor(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.stats = new HashMap<>();
        this.stats.put("total", 0);
        this.stats.put("successful", 0);
        this.stats.put("failed", 0);
        this.stats.put("watch_errors", 0);
    }

    public List<Object> executeWithMonitoring(List<Operation> operations) {
        stats.put("total", stats.get("total") + 1);

        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();

            for (Operation op : operations) {
                if ("set".equals(op.getType())) {
                    transaction.set(op.getKey(), op.getValue());
                } else if ("get".equals(op.getType())) {
                    transaction.get(op.getKey());
                }
            }

            List<Object> result = transaction.exec();

            if (result != null && !result.isEmpty()) {
                stats.put("successful", stats.get("successful") + 1);
                return result;
            } else {
                stats.put("failed", stats.get("failed") + 1);
                return null;
            }
        } catch (Exception e) {
            stats.put("watch_errors", stats.get("watch_errors") + 1);
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> getStats() {
        return new HashMap<>(stats);
    }
}
```

## Transaction Patterns

### Optimistic Locking Pattern

```java
public static boolean optimisticUpdate(redis_client, key, update_func, max_retries=10):
    """Оптимистическое обновление с повторными попытками"""
    for attempt in range(max_retries):
        try:
            redis_client.watch(key)
            current_value = redis_client.get(key)
            new_value = update_func(current_value)

            pipe = redis_client.pipeline()
            pipe.multi()
            pipe.set(key, new_value)
            result = pipe.execute()

            if result:
                return new_value
        except redis.WatchError:
            if attempt == max_retries - 1:
                raise Exception("Max retries exceeded")
            time.sleep(0.1 * (attempt + 1))  # Exponential backoff

    raise Exception("Failed to update")
```

### Transaction with Validation

```java
public static List<Object> validatedTransaction(redis_client, operations, validators):
    """Транзакция с валидацией"""
    pipe = redis_client.pipeline()

    # Валидация перед транзакцией
    for i, op in enumerate(operations):
        if i in validators:
            validator = validators[i]
            if not validator(redis_client, op):
                raise ValueError(f"Validation failed for operation {i}")

    # Выполнение транзакции
    pipe.multi()
    for op in operations:
        if op['type'] == 'set':
            pipe.set(op['key'], op['value'])

    return pipe.execute()
```

### Batch Operations with Transactions

```java
public static List<Object> batchUpdate(redis_client, updates):
    """Пакетное обновление в транзакции"""
    pipe = redis_client.pipeline()
    pipe.multi()

    for key, value in updates.items():
        pipe.set(key, value)

    return pipe.execute()
```

### Transaction with Rollback Logic

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import java.util.ArrayList;
import java.util.List;

public class TransactionWithRollback {
    private JedisPool jedisPool;
    private List<RollbackOperation> rollbackOps;

    public TransactionWithRollback(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.rollbackOps = new ArrayList<>();
    }

    public boolean executeWithRollback(List<Operation> operations) {
        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();

            // Сохранить текущие значения для отката
            for (Operation op : operations) {
                if ("set".equals(op.getType())) {
                    String oldValue = jedis.get(op.getKey());
                    rollbackOps.add(new RollbackOperation("set", op.getKey(), oldValue));
                    transaction.set(op.getKey(), op.getValue());
                }
            }

            try {
                List<Object> result = transaction.exec();
                if (result != null && !result.isEmpty()) {
                    rollbackOps.clear();
                    return true;
                }
                return false;
            } catch (Exception e) {
                rollback();
                throw new RuntimeException(e);
            }
        }
    }

    public void rollback() {
        try (Jedis jedis = jedisPool.getResource()) {
            Transaction transaction = jedis.multi();

            for (RollbackOperation op : rollbackOps) {
                if ("set".equals(op.getType())) {
                    if (op.getValue() != null) {
                        transaction.set(op.getKey(), op.getValue());
                    } else {
                        transaction.del(op.getKey());
                    }
                }
            }

            transaction.exec();
            rollbackOps.clear();
        }
    }

    private static class RollbackOperation {
        private String type;
        private String key;
        private String value;

        RollbackOperation(String type, String key, String value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

        String getType() { return type; }
        String getKey() { return key; }
        String getValue() { return value; }
    }
}
```

## Transaction vs Lua Scripts

### Когда использовать транзакции

- Простые атомарные операции
- Оптимистическая блокировка через **WATCH**
- Группировка нескольких команд

### Когда использовать Lua скрипты

- Сложная логика с условиями
- Необходимость обработки ошибок внутри скрипта
- Минимизация **round-trips**


- [Redis Transactions](https://redis.io/docs/manual/transactions/)

