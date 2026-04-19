---
title: "Redis: Pub/Sub"
description: "Полное руководство по Pub/Sub в Redis: публикация, подписка, паттерны, использование, best practices"
tags:
  - redis
  - pubsub
  - messaging
  - publish
  - subscribe
  - patterns
difficulty: "intermediate"
prerequisites: ["databases/redis-basics.md"]
next: ["databases/redis-streams.md"]
updated: "2026-02-06"
related: ["databases/redis-basics.md", "databases/redis-streams.md"]
---

# **Redis**: **Pub**/**Sub**

## Полезные ссылки

### Официальная документация
- [Redis Documentation](https://redis.io/docs/) — официальная документация
- [Redis Pub/Sub](https://redis.io/docs/manual/pubsub/) — Pub/Sub

### См. также
- [redis-basics.md](redis-basics.md) — основы Redis
- [redis-streams.md](redis-streams.md) — потоки

## Содержание

- [Введение в Pub/Sub](#введение-в-pubsub)
- [Базовое использование](#базовое-использование)
- [Программное использование](#программное-использование)
- [Мониторинг Pub/Sub](#мониторинг-pubsub)
- [Use Cases](#use-cases)
- [Лучшие практики](#лучшие-практики)
- [Advanced Pub/Sub Patterns](#advanced-pubsub-patterns)
- [Performance Optimization](#performance-optimization)
- [Advanced Pub/Sub Patterns (Java версии)](#advanced-pubsub-patterns-java-версии)

## Введение в **Pub**/**Sub**

**Pub**/**Sub** (**Publish-Subscribe**) - это паттерн **messaging**, где отправители (**publishers**) отправляют сообщения, не зная конкретных получателей. Получатели (**subscribers**) подписываются на интересующие их каналы и получают сообщения.

### Основные концепции

- **Publisher**: Отправляет сообщения в каналы
- **Subscriber**: Подписывается на каналы и получает сообщения
- **Channel**: Тема, на которую подписываются
- **Pattern**: Шаблон для подписки на несколько каналов

---

## Базовое использование

### Публикация сообщений

```redis
# Публикация в канал
PUBLISH news "Breaking news: Redis 7.0 released!"

# Публикация в несколько каналов
PUBLISH news "News message"
PUBLISH sports "Sports message"
```

### Подписка на каналы

```redis
# Подписка на один канал
SUBSCRIBE news

# Подписка на несколько каналов
SUBSCRIBE news sports technology

# Отписка от каналов
UNSUBSCRIBE news
UNSUBSCRIBE  # От всех каналов
```

### **Pattern Subscription**

```redis
# Подписка по паттерну
PSUBSCRIBE news.*

# Отписка от паттерна
PUNSUBSCRIBE news.*

# От всех паттернов
PUNSUBSCRIBE
```

---

## Программное использование

### **Java Publisher**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Publisher {
    private JedisPool jedisPool;
    
    public Publisher(String host, int port) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        this.jedisPool = new JedisPool(poolConfig, host, port);
    }
    
    public long publish(String channel, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            long subscribers = jedis.publish(channel, message);
            System.out.println("Message published to " + subscribers + " subscribers");
            return subscribers;
        }
    }
    
    public void publishContinuously(String channel, long intervalMs) throws InterruptedException {
        int counter = 0;
        while (true) {
            String message = "Message " + counter;
            publish(channel, message);
            counter++;
            Thread.sleep(intervalMs);
        }
    }
    
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
```

### **Java Subscriber**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Subscriber {
    private JedisPool jedisPool;
    private JedisPubSub jedisPubSub;
    
    public Subscriber(String host, int port) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        this.jedisPool = new JedisPool(poolConfig, host, port);
    }
    
    public void subscribe(String... channels) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println("Received: " + message + " on channel " + channel);
                }
                
                @Override
                public void onPMessage(String pattern, String channel, String message) {
                    System.out.println("Received: " + message + " on pattern " + pattern);
                }
            };
            jedis.subscribe(jedisPubSub, channels);
        }
    }
    
    public void psubscribe(String... patterns) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedisPubSub = new JedisPubSub() {
                @Override
                public void onPMessage(String pattern, String channel, String message) {
                    System.out.println("Received: " + message + " on pattern " + pattern);
                }
            };
            jedis.psubscribe(jedisPubSub, patterns);
        }
    }
    
    public void unsubscribe(String... channels) {
        if (jedisPubSub != null) {
            jedisPubSub.unsubscribe(channels);
        }
    }
    
    public void close() {
        if (jedisPubSub != null) {
            jedisPubSub.unsubscribe();
        }
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}
```

---

## Мониторинг **Pub**/**Sub**

### Команды мониторинга

```redis
# Список активных каналов
PUBSUB CHANNELS

# Каналы с паттерном
PUBSUB CHANNELS news*

# Количество подписчиков на канал
PUBSUB NUMSUB news sports

# Количество подписчиков на паттерны
PUBSUB NUMPAT
```

---

## **Use Cases**

### **Event Broadcasting**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class EventBroadcaster {
    private JedisPool jedisPool;
    private Gson gson;
    
    public EventBroadcaster(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.gson = new Gson();
    }
    
    public void broadcastEvent(String eventType, Object eventData) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, Object> message = new HashMap<>();
            message.put("type", eventType);
            message.put("data", eventData);
            message.put("timestamp", System.currentTimeMillis());
            
            String jsonMessage = gson.toJson(message);
            jedis.publish("events:" + eventType, jsonMessage);
        }
    }
}
```

### **Real-Time Notifications**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.google.gson.Gson;

public class NotificationService {
    private JedisPool jedisPool;
    private Gson gson;
    
    public NotificationService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.gson = new Gson();
    }
    
    public void sendNotification(String userId, Object notification) {
        try (Jedis jedis = jedisPool.getResource()) {
            String channel = "notifications:" + userId;
            String jsonNotification = gson.toJson(notification);
            jedis.publish(channel, jsonNotification);
        }
    }
}
```

---

## Лучшие практики

1. **Используйте именованные каналы** для организации
2. **Обрабатывайте отключения** подписчиков
3. **Используйте паттерны** для группировки каналов
4. **Мониторьте количество подписчиков**
5. **Обрабатывайте ошибки** при публикации

## **Advanced Pub**/**Sub Patterns**

### **Message Routing**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.function.BiConsumer;

public class MessageRouter {
    private JedisPool jedisPool;
    private Gson gson;
    
    public MessageRouter(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.gson = new Gson();
    }
    
    public void routeMessage(String messageType, Object messageData) {
        try (Jedis jedis = jedisPool.getResource()) {
            String routingKey = "messages:" + messageType;
            String jsonMessage = gson.toJson(messageData);
            jedis.publish(routingKey, jsonMessage);
        }
    }
    
    public void subscribeToTypes(String[] messageTypes, BiConsumer<String, String> handler) {
        try (Jedis jedis = jedisPool.getResource()) {
            String[] patterns = Arrays.stream(messageTypes)
                .map(type -> "messages:" + type)
                .toArray(String[]::new);
            
            JedisPubSub pubsub = new JedisPubSub() {
                @Override
                public void onPMessage(String pattern, String channel, String message) {
                    handler.accept(pattern, message);
                }
            };
            jedis.psubscribe(pubsub, patterns);
        }
    }
}
```

### **Fan-Out Pattern**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.HashMap;
import java.util.Map;

public class FanOutPublisher {
    private JedisPool jedisPool;
    
    public FanOutPublisher(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public Map<String, Long> fanOut(String[] channels, String message) {
        Map<String, Long> results = new HashMap<>();
        try (Jedis jedis = jedisPool.getResource()) {
            for (String channel : channels) {
                long subscribers = jedis.publish(channel, message);
                results.put(channel, subscribers);
            }
        }
        return results;
    }
}
```

### **Request-Response Pattern**

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RequestResponse {
    private JedisPool jedisPool;
    private Gson gson;
    
    public RequestResponse(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.gson = new Gson();
    }
    
    public CompletableFuture<String> request(String requestId, String channel, Object requestData) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        try (Jedis jedis = jedisPool.getResource()) {
            String responseChannel = "response:" + requestId;
            
            JedisPubSub pubsub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    if (channel.equals(responseChannel)) {
                        future.complete(message);
                        unsubscribe();
                    }
                }
            };
            
            // Подписаться на канал ответов
            new Thread(() -> {
                try (Jedis subscriberJedis = jedisPool.getResource()) {
                    subscriberJedis.subscribe(pubsub, responseChannel);
                }
            }).start();
            
            // Отправить запрос
            Map<String, Object> message = new HashMap<>();
            message.put("request_id", requestId);
            message.put("response_channel", responseChannel);
            message.put("data", requestData);
            
            String jsonMessage = gson.toJson(message);
            jedis.publish(channel, jsonMessage);
        }
        
        return future;
    }
}
```

## **Advanced Pub**/**Sub Patterns**

### **Message Queue Pattern**

```java
// Java пример message queue
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageQueue {
    private JedisPool jedisPool;
    private String queueName;
    private ObjectMapper objectMapper;

    public MessageQueue(JedisPool jedisPool, String queueName) {
        this.jedisPool = jedisPool;
        this.queueName = queueName;
        this.objectMapper = new ObjectMapper();
    }

    public long enqueue(Object message) {
        try (Jedis jedis = jedisPool.getResource()) {
            String jsonMessage = objectMapper.writeValueAsString(message);
            return jedis.publish(queueName, jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enqueue message", e);
        }
    }

    public <T> T dequeue(Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Для демонстрации - в реальности нужен более сложный механизм
            // Redis Pub/Sub не предназначен для очередей, лучше использовать Lists
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to dequeue message", e);
        }
    }
}
```

### **Topic-Based Routing**

```java
// Java пример topic router
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.BiConsumer;
import java.util.List;
import java.util.stream.Collectors;

public class TopicRouter {
    private JedisPool jedisPool;
    private ObjectMapper objectMapper;

    public TopicRouter(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.objectMapper = new ObjectMapper();
    }

    public long publishToTopic(String topic, Object message) {
        try (Jedis jedis = jedisPool.getResource()) {
            String channel = "topic:" + topic;
            String jsonMessage = objectMapper.writeValueAsString(message);
            return jedis.publish(channel, jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish to topic", e);
        }
    }

    public void subscribeToTopics(List<String> topics, BiConsumer<String, String> handler) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> channels = topics.stream()
                .map(topic -> "topic:" + topic)
                .collect(Collectors.toList());

            jedis.subscribe(new redis.clients.jedis.JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    String topic = channel.replace("topic:", "");
                    handler.accept(topic, message);
                }
            }, channels.toArray(new String[0]));
        } catch (Exception e) {
            throw new RuntimeException("Failed to subscribe to topics", e);
        }
    }
}
```

### **Event Sourcing Pattern**

```java
// Java пример event store
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventStore {
    private JedisPool jedisPool;
    private ObjectMapper objectMapper;

    public EventStore(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        this.objectMapper = new ObjectMapper();
    }

    public String publishEvent(String eventType, Object eventData) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, Object> event = new HashMap<>();
            event.put("type", eventType);
            event.put("data", eventData);
            event.put("timestamp", Instant.now().toEpochMilli());
            String eventId = UUID.randomUUID().toString();
            event.put("id", eventId);

            // Сохранить в stream для истории
            Map<String, String> eventMap = new HashMap<>();
            eventMap.put("type", eventType);
            eventMap.put("data", objectMapper.writeValueAsString(eventData));
            eventMap.put("timestamp", String.valueOf(event.get("timestamp")));
            eventMap.put("id", eventId);

            jedis.xadd("events", jedis.xaddArgs().maxLen(1000), eventMap);

            // Опубликовать для подписчиков
            String channel = "events:" + eventType;
            String jsonEvent = objectMapper.writeValueAsString(event);
            jedis.publish(channel, jsonEvent);

            return eventId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    public void subscribeToEvents(List<String> eventTypes, Consumer<Map<String, Object>> handler) {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> patterns = eventTypes.stream()
                .map(et -> "events:" + et)
                .collect(java.util.stream.Collectors.toList());

            jedis.psubscribe(new redis.clients.jedis.JedisPubSub() {
                @Override
                public void onPMessage(String pattern, String channel, String message) {
                    try {
                        Map<String, Object> event = objectMapper.readValue(message, Map.class);
                        handler.accept(event);
                    } catch (Exception e) {
                        System.err.println("Failed to parse event: " + e.getMessage());
                    }
                }
            }, patterns.toArray(new String[0]));
        } catch (Exception e) {
            throw new RuntimeException("Failed to subscribe to events", e);
        }
    }
}
```

## **Performance Optimization**

### **Connection Pooling for Pub**/**Sub**

```java
// Java пример пула PubSub соединений
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PubSubPool {
    private JedisPool jedisPool;
    private int poolSize;
    private Queue<JedisPubSub> pubsubPool;

    public PubSubPool(JedisPool jedisPool, int poolSize) {
        this.jedisPool = jedisPool;
        this.poolSize = poolSize;
        this.pubsubPool = new ConcurrentLinkedQueue<>();
    }

    public JedisPubSub getPubSub() {
        JedisPubSub pubsub = pubsubPool.poll();
        if (pubsub == null) {
            // Создать новое соединение если пул пуст
            try {
                Jedis jedis = jedisPool.getResource();
                pubsub = new JedisPubSub();
                // В реальности здесь нужно настроить pubsub
            } catch (Exception e) {
                throw new RuntimeException("Failed to create PubSub connection", e);
            }
        }
        return pubsub;
    }

    public void returnPubSub(JedisPubSub pubsub) {
        if (pubsubPool.size() < poolSize) {
            pubsub.unsubscribe();
            pubsubPool.offer(pubsub);
        } else {
            // Закрыть соединение если пул полон
            try {
                pubsub.unsubscribe();
            } catch (Exception e) {
                // Игнорировать ошибки при закрытии
            }
        }
    }
}
```

## **Advanced Pub**/**Sub Patterns** (**Java версии**)

**Все примеры выше уже представлены в **Java**. Дополнительные паттерны:**

### **Message Queue Pattern** (**Java**)

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import com.google.gson.Gson;

public class MessageQueue {
    private JedisPool jedisPool;
    private String queueName;
    private Gson gson;
    
    public MessageQueue(JedisPool jedisPool, String queueName) {
        this.jedisPool = jedisPool;
        this.queueName = queueName;
        this.gson = new Gson();
    }
    
    public long enqueue(Object message) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.publish(queueName, gson.toJson(message));
        }
    }
    
    public String dequeue(long timeoutMs) {
        try (Jedis jedis = jedisPool.getResource()) {
            JedisPubSub pubsub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    // Обработка сообщения
                }
            };
            
            jedis.subscribe(pubsub, queueName);
            return null;
        }
    }
}
```

### **Message Batching** (**Java**)

```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import java.util.ArrayList;
import java.util.List;

public class BatchedPublisher {
    private JedisPool jedisPool;
    private int batchSize;
    private long flushIntervalMs;
    private List<Message> messageQueue;
    private long lastFlush;
    
    public BatchedPublisher(JedisPool jedisPool, int batchSize, long flushIntervalMs) {
        this.jedisPool = jedisPool;
        this.batchSize = batchSize;
        this.flushIntervalMs = flushIntervalMs;
        this.messageQueue = new ArrayList<>();
        this.lastFlush = System.currentTimeMillis();
    }
    
    public void publish(String channel, String message) {
        synchronized (messageQueue) {
            messageQueue.add(new Message(channel, message));
            
            if (messageQueue.size() >= batchSize) {
                flush();
            } else if (System.currentTimeMillis() - lastFlush > flushIntervalMs) {
                flush();
            }
        }
    }
    
    public void flush() {
        synchronized (messageQueue) {
            if (messageQueue.isEmpty()) {
                return;
            }
            
            try (Jedis jedis = jedisPool.getResource()) {
                Pipeline pipe = jedis.pipelined();
                for (Message msg : messageQueue) {
                    pipe.publish(msg.channel, msg.message);
                }
                pipe.sync();
                messageQueue.clear();
                lastFlush = System.currentTimeMillis();
            }
        }
    }
    
    private static class Message {
        String channel;
        String message;
        
        Message(String channel, String message) {
            this.channel = channel;
            this.message = message;
        }
    }
}
```

---

- [Redis Pub/Sub](https://redis.io/docs/manual/pubsub/)

---

