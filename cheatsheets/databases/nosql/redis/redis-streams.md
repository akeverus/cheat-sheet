---
title: "Redis: Streams"
description: "Полное руководство по Redis Streams: создание потоков, чтение, consumer groups, обработка сообщений, мониторинг"
tags:
  - redis
  - streams
  - messaging
  - consumer-groups
  - xadd
  - xread
  - xgroup
difficulty: "advanced"
prerequisites: ["databases/redis-basics.md", "databases/redis-pubsub.md"]
next: ["databases/redis-troubleshooting.md"]
updated: "2026-04-20"
related: ["databases/redis-basics.md", "databases/redis-pubsub.md"]
---

# Redis: Streams

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Streams](https://redis.io/docs/data-types/streams/) — потоки

### См. также
- [[redis-basics|redis-basics.md]] — основы Redis
- [[redis-pubsub|redis-pubsub.md]] — Pub/Sub

## Содержание

- [Введение в Redis Streams](#введение-в-redis-streams)
  - [Основные возможности](#основные-возможности)
- [Базовые операции](#базовые-операции)
  - [Добавление сообщений](#добавление-сообщений)
  - [Чтение из потока](#чтение-из-потока)
- [Consumer Groups](#consumer-groups)
  - [Создание Consumer Group](#создание-consumer-group)
  - [Чтение из группы](#чтение-из-группы)
  - [Подтверждение обработки](#подтверждение-обработки)
  - [Управление группами](#управление-группами)
- [Управление потоками](#управление-потоками)
  - [Удаление сообщений](#удаление-сообщений)
  - [Обрезка потока](#обрезка-потока)
  - [Длина потока](#длина-потока)
- [Программное использование](#программное-использование)
  - [Java Producer](#java-producer)
  - [Java Consumer](#java-consumer)
- [Лучшие практики](#лучшие-практики)
- [Advanced Stream Operations](#advanced-stream-operations)
  - [Pending Messages](#pending-messages)
  - [Message Processing Patterns](#message-processing-patterns)
  - [Stream Aggregation](#stream-aggregation)
- [Advanced Stream Patterns](#advanced-stream-patterns)
  - [Dead Letter Queue](#dead-letter-queue)
  - [Stream Aggregation](#stream-aggregation-1)
  - [Stream Replay](#stream-replay)
- [Advanced Stream Patterns](#advanced-stream-patterns-1)
  - [Dead Letter Queue](#dead-letter-queue-1)
  - [Stream Aggregation](#stream-aggregation-2)
  - [Stream Replay](#stream-replay-1)

## Введение в Redis Streams

**Redis Streams** — это структура данных для хранения логов сообщений, добавленная в **Redis** `5.0`. **Streams** обеспечивают гарантии доставки, **consumer groups** и позволяют обрабатывать сообщения в порядке их поступления.

### Основные возможности

- **Гарантии доставки**: Сообщения не теряются
- **Consumer Groups**: Распределенная обработка сообщений
- **Time-based queries**: Запросы по временным меткам
- **Automatic acknowledgment**: Подтверждение обработки
- **Message persistence**: Сохранение истории сообщений


## Базовые операции

### Добавление сообщений

```redis
# Добавление сообщения в поток
XADD mystream * sensor-id "sensor1" temperature "25.5" humidity "60"

# С указанием ID
XADD mystream 1640995200000-0 sensor-id "sensor2" temperature "24.8"

# Добавление с максимальной длиной
XADD mystream MAXLEN 1000 * sensor-id "sensor3" temperature "26.0"
```

### Чтение из потока

```redis
# Чтение всех сообщений
XRANGE mystream - +

# Чтение по диапазону времени
XRANGE mystream 1640995200000 1640995260000

# Обратное чтение (последние сообщения)
XREVRANGE mystream + - COUNT 10

# Чтение новых сообщений
XREAD COUNT 2 STREAMS mystream 0
XREAD BLOCK 5000 STREAMS mystream $
```


## Consumer Groups

### Создание Consumer Group

```redis
# Создание группы
XGROUP CREATE mystream mygroup 0 MKSTREAM

# Создание группы с указанием ID
XGROUP CREATE mystream mygroup 1640995200000-0

# Создание группы для существующего потока
XGROUP CREATE mystream mygroup $
```

### Чтение из группы

```redis
# Чтение из группы
XREADGROUP GROUP mygroup consumer1 COUNT 1 STREAMS mystream >

# Чтение с блокировкой
XREADGROUP GROUP mygroup consumer1 BLOCK 5000 COUNT 1 STREAMS mystream >
```

### Подтверждение обработки

```redis
# Подтверждение одного сообщения
XACK mystream mygroup 1640995200000-0

# Подтверждение нескольких сообщений
XACK mystream mygroup 1640995200000-0 1640995201000-0
```

### Управление группами

```redis
# Информация о группах
XINFO GROUPS mystream

# Информация о потребителях
XINFO CONSUMERS mystream mygroup

# Информация о потоке
XINFO STREAM mystream

# Удаление потребителя
XGROUP DELCONSUMER mystream mygroup consumer1
```


## Управление потоками

### Удаление сообщений

```redis
# Удаление сообщения
XDEL mystream 1640995200000-0

# Удаление нескольких сообщений
XDEL mystream 1640995200000-0 1640995201000-0
```

### Обрезка потока

```redis
# Обрезка до определенной длины
XTRIM mystream MAXLEN 1000

# Приблизительная обрезка (быстрее)
XTRIM mystream MAXLEN ~ 1000

# Обрезка по минимальному ID
XTRIM mystream MINID 1640995200000-0
```

### Длина потока

```redis
# Количество сообщений в потоке
XLEN mystream
```


## Программное использование

### Java Producer

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntryID;
import java.util.HashMap;
import java.util.Map;

public class StreamProducer {
    private JedisPool jedisPool;
    private String streamName;

    public StreamProducer(JedisPool jedisPool, String streamName) {
        this.jedisPool = jedisPool;
        this.streamName = streamName;
    }

    public StreamEntryID produce(Map<String, String> data) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.xadd(streamName, StreamEntryID.NEW_ENTRY, data);
        }
    }

    public void produceContinuously(long intervalMs) throws InterruptedException {
        int counter = 0;
        while (true) {
            Map<String, String> message = new HashMap<>();
            message.put("counter", String.valueOf(counter));
            message.put("timestamp", String.valueOf(System.currentTimeMillis()));
            message.put("data", "message_" + counter);

            produce(message);
            counter++;
            Thread.sleep(intervalMs);
        }
    }
}
```

### Java Consumer

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class StreamConsumer {
    private JedisPool jedisPool;
    private String streamName;
    private String groupName;
    private String consumerName;

    public StreamConsumer(JedisPool jedisPool, String streamName,
                         String groupName, String consumerName) {
        this.jedisPool = jedisPool;
        this.streamName = streamName;
        this.groupName = groupName;
        this.consumerName = consumerName;
    }

    public List<Map.Entry<String, List<StreamEntry>>> consume(int count, long blockMs) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.xreadGroup(
                groupName,
                consumerName,
                count,
                blockMs,
                false,
                java.util.Collections.singletonMap(streamName, ">")
            );
        }
    }

    public void acknowledge(StreamEntryID messageId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.xack(streamName, groupName, messageId);
        }
    }

    public void processMessages(Consumer<Map<String, String>> handler) {
        while (true) {
            List<Map.Entry<String, List<StreamEntry>>> messages = consume(1, 1000);

            for (Map.Entry<String, List<StreamEntry>> stream : messages) {
                for (StreamEntry entry : stream.getValue()) {
                    try {
                        handler.accept(entry.getFields());
                        acknowledge(entry.getID());
                    } catch (Exception e) {
                        System.err.println("Error processing message " + entry.getID() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
}
```


## Лучшие практики

1. **Используйте `Consumer` Groups** для распределенной обработки
2. **Подтверждайте обработку** сообщений через **XACK**
3. **Настройте правильный MAXLEN** для управления размером потока
4. **Обрабатывайте ошибки** при чтении сообщений
5. **Мониторьте pending messages** для обнаружения проблем

## Advanced Stream Operations

### Pending Messages

```redis
# Просмотр pending сообщений
XPENDING mystream mygroup

# Пending сообщения для конкретного потребителя
XPENDING mystream mygroup - + 10 consumer1

# Заявка на обработку pending сообщений
XCLAIM mystream mygroup consumer2 3600000 1640995200000-0
```

### Message Processing Patterns

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.StreamEntryID;
import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ReliableStreamConsumer {
    private JedisPool jedisPool;
    private String streamName;
    private String groupName;
    private String consumerName;
    private Gson gson;

    public ReliableStreamConsumer(JedisPool jedisPool, String streamName,
                                 String groupName, String consumerName) {
        this.jedisPool = jedisPool;
        this.streamName = streamName;
        this.groupName = groupName;
        this.consumerName = consumerName;
        this.gson = new Gson();
    }

    public void processWithRetry(Consumer<Map<String, String>> handler, int maxRetries) {
        while (true) {
            List<Map.Entry<String, List<StreamEntry>>> messages = consume(1, 1000);

            for (Map.Entry<String, List<StreamEntry>> stream : messages) {
                for (StreamEntry entry : stream.getValue()) {
                    int retries = 0;
                    boolean success = false;

                    while (retries < maxRetries && !success) {
                        try {
                            handler.accept(entry.getFields());
                            acknowledge(entry.getID());
                            success = true;
                        } catch (Exception e) {
                            retries++;
                            if (retries >= maxRetries) {
                                sendToDLQ(entry.getID(), entry.getFields(), e.getMessage());
                            } else {
                                try {
                                    Thread.sleep((long) Math.pow(2, retries) * 1000);
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private List<Map.Entry<String, List<StreamEntry>>> consume(int count, long blockMs) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.xreadGroup(
                groupName,
                consumerName,
                count,
                blockMs,
                false,
                java.util.Collections.singletonMap(streamName, ">")
            );
        }
    }

    private void acknowledge(StreamEntryID messageId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.xack(streamName, groupName, messageId);
        }
    }

    private void sendToDLQ(StreamEntryID originalId, Map<String, String> data, String error) {
        try (Jedis jedis = jedisPool.getResource()) {
            String dlqStream = streamName + ":dlq";
            Map<String, String> dlqMessage = new java.util.HashMap<>();
            dlqMessage.put("original_id", originalId.toString());
            dlqMessage.put("data", gson.toJson(data));
            dlqMessage.put("error", error);
            dlqMessage.put("timestamp", String.valueOf(System.currentTimeMillis()));
            jedis.xadd(dlqStream, StreamEntryID.NEW_ENTRY, dlqMessage);
        }
    }
}
```

### Stream Aggregation

```lua
-- Агрегация данных из потока
local stream_key = KEYS[1]
local start_id = ARGV[1]
local end_id = ARGV[2]

local messages = redis.call('XRANGE', stream_key, start_id, end_id)
local sum = 0
local count = 0

for i = 1, #messages do
    local msg = messages[i]
    local value = tonumber(msg[2][2])  -- Предполагаем числовое значение
    if value then
        sum = sum + value
        count = count + 1
    end
end

return {sum, count, sum / count}
```

## Advanced Stream Patterns

### Dead Letter Queue

```java
public class StreamProcessorWithDLQ {
    def __init__(self, redis_client, stream_name, group_name, consumer_name):
        self.redis = redis_client
        self.stream_name = stream_name
        self.group_name = group_name
        self.consumer_name = consumer_name
        self.dlq_stream = f"{stream_name}:dlq"

    def process_with_dlq(self, handler, max_retries=3):
        """Обработка с отправкой в DLQ при ошибках"""
        for msg_id, data in self.consume():
            retries = 0
            success = False

            while retries < max_retries and not success:
                try:
                    handler(data)
                    self.acknowledge(msg_id)
                    success = True
                except Exception as e:
                    retries += 1
                    if retries >= max_retries:
                        # Отправить в DLQ
                        self.send_to_dlq(msg_id, data, str(e))
                    else:
                        time.sleep(2  retries)

    def send_to_dlq(self, original_id, data, error):
        """Отправка в dead letter queue"""
        dlq_message = {
            'original_id': original_id,
            'original_stream': self.stream_name,
            'data': json.dumps(data),
            'error': error,
            'timestamp': time.time(),
            'retry_count': 3
        }
        self.redis.xadd(self.dlq_stream, dlq_message, id='*')
```

### Stream Aggregation

```java
public class StreamAggregator {
    def __init__(self, redis_client, source_stream, target_stream):
        self.redis = redis_client
        self.source_stream = source_stream
        self.target_stream = target_stream

    def aggregate_by_window(self, window_seconds=60):
        """Агрегация данных по временным окнам"""
        last_id = '0'

        while True:
            messages = self.redis.xread(
                {self.source_stream: last_id},
                count=100,
                block=1000
            )

            if not messages:
                continue

            stream, msgs = messages[0]
            window_data = {}

            for msg_id, data in msgs:
                timestamp = int(msg_id.split('-')[0]) / 1000
                window = int(timestamp / window_seconds) * window_seconds

                if window not in window_data:
                    window_data[window] = []
                window_data[window].append(data)

            # Агрегировать и отправить в целевой поток
            for window, data_list in window_data.items():
                aggregated = self.aggregate_data(data_list)
                self.redis.xadd(
                    self.target_stream,
                    aggregated,
                    id=f"{int(window * 1000)}-0"
                )

            last_id = msgs[-1][0]

    def aggregate_data(self, data_list):
        """Агрегация данных"""
        # Пример: подсчет среднего значения
        values = [float(d.get('value', 0)) for d in data_list if 'value' in d]
        if values:
            return {
                'count': len(values),
                'sum': sum(values),
                'avg': sum(values) / len(values),
                'min': min(values),
                'max': max(values)
            }
        return {'count': 0}
```

### Stream Replay

```java
public class StreamReplayer {
    def __init__(self, redis_client, stream_name):
        self.redis = redis_client
        self.stream_name = stream_name

    def replay_from_id(self, start_id, end_id=None):
        """Воспроизведение сообщений из потока"""
        if end_id:
            messages = self.redis.xrange(self.stream_name, start_id, end_id)
        else:
            messages = self.redis.xrange(self.stream_name, start_id, '+')

        for msg_id, data in messages:
            yield msg_id, data

    def replay_to_stream(self, source_id, target_stream, start_id, end_id=None):
        """Воспроизведение в другой поток"""
        for msg_id, data in self.replay_from_id(start_id, end_id):
            self.redis.xadd(target_stream, data, id='*')
```

## Advanced Stream Patterns

### Dead Letter Queue

```java
public class StreamProcessorWithDLQ {
    def __init__(self, redis_client, stream_name, group_name, consumer_name):
        self.redis = redis_client
        self.stream_name = stream_name
        self.group_name = group_name
        self.consumer_name = consumer_name
        self.dlq_stream = f"{stream_name}:dlq"

    def process_with_dlq(self, handler, max_retries=3):
        """Обработка с отправкой в DLQ при ошибках"""
        for msg_id, data in self.consume():
            retries = 0
            success = False

            while retries < max_retries and not success:
                try:
                    handler(data)
                    self.acknowledge(msg_id)
                    success = True
                except Exception as e:
                    retries += 1
                    if retries >= max_retries:
                        # Отправить в DLQ
                        self.send_to_dlq(msg_id, data, str(e))
                    else:
                        time.sleep(2  retries)

    def send_to_dlq(self, original_id, data, error):
        """Отправка в dead letter queue"""
        dlq_message = {
            'original_id': original_id,
            'original_stream': self.stream_name,
            'data': json.dumps(data),
            'error': error,
            'timestamp': time.time(),
            'retry_count': 3
        }
        self.redis.xadd(self.dlq_stream, dlq_message, id='*')
```

### Stream Aggregation

```java
public class StreamAggregator {
    def __init__(self, redis_client, source_stream, target_stream):
        self.redis = redis_client
        self.source_stream = source_stream
        self.target_stream = target_stream

    def aggregate_by_window(self, window_seconds=60):
        """Агрегация данных по временным окнам"""
        last_id = '0'

        while True:
            messages = self.redis.xread(
                {self.source_stream: last_id},
                count=100,
                block=1000
            )

            if not messages:
                continue

            stream, msgs = messages[0]
            window_data = {}

            for msg_id, data in msgs:
                timestamp = int(msg_id.split('-')[0]) / 1000
                window = int(timestamp / window_seconds) * window_seconds

                if window not in window_data:
                    window_data[window] = []
                window_data[window].append(data)

            # Агрегировать и отправить в целевой поток
            for window, data_list in window_data.items():
                aggregated = self.aggregate_data(data_list)
                self.redis.xadd(
                    self.target_stream,
                    aggregated,
                    id=f"{int(window * 1000)}-0"
                )

            last_id = msgs[-1][0]

    def aggregate_data(self, data_list):
        """Агрегация данных"""
        # Пример: подсчет среднего значения
        values = [float(d.get('value', 0)) for d in data_list if 'value' in d]
        if values:
            return {
                'count': len(values),
                'sum': sum(values),
                'avg': sum(values) / len(values),
                'min': min(values),
                'max': max(values)
            }
        return {'count': 0}
```

### Stream Replay

```java
public class StreamReplayer {
    def __init__(self, redis_client, stream_name):
        self.redis = redis_client
        self.stream_name = stream_name

    def replay_from_id(self, start_id, end_id=None):
        """Воспроизведение сообщений из потока"""
        if end_id:
            messages = self.redis.xrange(self.stream_name, start_id, end_id)
        else:
            messages = self.redis.xrange(self.stream_name, start_id, '+')

        for msg_id, data in messages:
            yield msg_id, data

    def replay_to_stream(self, source_id, target_stream, start_id, end_id=None):
        """Воспроизведение в другой поток"""
        for msg_id, data in self.replay_from_id(start_id, end_id):
            self.redis.xadd(target_stream, data, id='*')
```


- [Redis Streams](https://redis.io/docs/data-types/streams/)
- [Redis Streams Tutorial](https://redis.io/docs/data-types/streams-tutorial/)

