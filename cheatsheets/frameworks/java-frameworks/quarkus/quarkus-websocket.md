---
title: "Quarkus: WebSocket — Real-time Communication"
description: "Полное руководство по WebSocket в Quarkus: server endpoints, client endpoints, message handling, broadcasting и best practices"
tags:
  - quarkus
  - websocket
  - real-time
  - communication
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-rest.md"]
next: ["quarkus-rest.md", "quarkus-reactive.md"]
updated: "2026-04-20"
related: ["quarkus-rest.md", "quarkus-reactive.md"]
---

# Quarkus: WebSocket — Real-time Communication

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Server Endpoints](#server-endpoints)
  - [Basic WebSocket Endpoint](#basic-websocket-endpoint)
  - [Advanced Endpoint](#advanced-endpoint)
- [Client Endpoints](#client-endpoints)
  - [WebSocket Client](#websocket-client)
- [Message Types](#message-types)
  - [Text Messages](#text-messages)
  - [Binary Messages](#binary-messages)
- [Broadcasting](#broadcasting)
  - [Broadcast to All](#broadcast-to-all)
- [Reactive WebSocket](#reactive-websocket)
  - [Reactive Endpoint](#reactive-endpoint)
- [Лучшие практики](#лучшие-практики)
  - [1. Обрабатывайте ошибки](#1-обрабатывайте-ошибки)
  - [2. Используйте async для отправки](#2-используйте-async-для-отправки)
  - [3. Управляйте подключениями](#3-управляйте-подключениями)
- [Security](#security)
  - [Secured WebSocket](#secured-websocket)
- [Message Encoding/Decoding](#message-encodingdecoding)
  - [Custom Encoders/Decoders](#custom-encodersdecoders)
- [Connection Management](#connection-management)
  - [Session Management](#session-management)
- [WebSocket Performance Optimization](#websocket-performance-optimization)
  - [Connection Pooling](#connection-pooling)
  - [Message Batching](#message-batching)
- [Advanced WebSocket Patterns](#advanced-websocket-patterns)
  - [Message Queue Pattern](#message-queue-pattern)
  - [Heartbeat Pattern](#heartbeat-pattern)
- [WebSocket Connection Pooling](#websocket-connection-pooling)
  - [Connection Management](#connection-management-1)
  - [Message Queue for WebSocket](#message-queue-for-websocket)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Quarkus** предоставляет полную поддержку **WebSocket** для создания **real-time** приложений. Это позволяет устанавливать двустороннюю связь между клиентом и сервером для обмена сообщениями в реальном времени.

### Основные возможности

- **Server Endpoints**: **WebSocket** серверные **endpoints**
- **Client Endpoints**: **WebSocket** клиентские **endpoints**
- **Message Handling**: Обработка различных типов сообщений
- **Broadcasting**: Отправка сообщений всем подключенным клиентам
- **Reactive Support**: Реактивная поддержка

## Server Endpoints

### Basic WebSocket Endpoint

**Базовый **WebSocket endpoint**:**

```java
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Client connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received: " + message);
        // Отправка ответа
        session.getAsyncRemote().sendText("Echo: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Client disconnected: " + session.getId());
    }
}
```

### Advanced Endpoint

**Продвинутый **endpoint**:**

```java
import jakarta.websocket.OnError;
import jakarta.websocket.server.PathParam;

@ServerEndpoint("/chat/{room}")
public class RoomChatEndpoint {

    @OnOpen
    public void onOpen(@PathParam("room") String room, Session session) {
        session.getUserProperties().put("room", room);
        System.out.println("Client joined room: " + room);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String room = (String) session.getUserProperties().get("room");
        broadcastToRoom(room, message, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("Error: " + error.getMessage());
    }

    private void broadcastToRoom(String room, String message, Session sender) {
        // Логика broadcast
    }
}
```

## Client Endpoints

### WebSocket Client

**WebSocket** клиент:**

```java
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;

@ClientEndpoint
public class WebSocketClient {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received: " + message);
    }

    @OnClose
    public void onClose() {
        System.out.println("Disconnected from server");
    }
}
```

## Message Types

### Text Messages

**Текстовые сообщения:**

```java
@ServerEndpoint("/text")
public class TextEndpoint {

    @OnMessage
    public void onTextMessage(String message, Session session) {
        session.getAsyncRemote().sendText("Response: " + message);
    }
}
```

### Binary Messages

**Бинарные сообщения:**

```java
@ServerEndpoint("/binary")
public class BinaryEndpoint {

    @OnMessage
    public void onBinaryMessage(ByteBuffer data, Session session) {
        // Обработка бинарных данных
        session.getAsyncRemote().sendBinary(data);
    }
}
```

## Broadcasting

### Broadcast to All

**Отправка всем клиентам:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/broadcast")
@ApplicationScoped
public class BroadcastEndpoint {

    private static Set<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session sender) {
        sessions.forEach(session -> {
            if (session.isOpen() && !session.equals(sender)) {
                session.getAsyncRemote().sendText(message);
            }
        });
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }
}
```

## Reactive WebSocket

### Reactive Endpoint

**Реактивный **endpoint**:**

```java
import io.smallrye.mutiny.Multi;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/reactive")
public class ReactiveEndpoint {

    @OnMessage
    public Multi<String> onReactiveMessage(String message) {
        return Multi.createFrom().items(
            "Response 1: " + message,
            "Response 2: " + message,
            "Response 3: " + message
        );
    }
}
```

## Лучшие практики

### 1. Обрабатывайте ошибки

```java
// ✅ Хорошо
@OnError
public void onError(Session session, Throwable error) {
    logError(error);
}
```

### 2. Используйте async для отправки

```java
// ✅ Хорошо
session.getAsyncRemote().sendText(message);
```

### 3. Управляйте подключениями

```java
// ✅ Хорошо
@OnOpen
public void onOpen(Session session) {
    sessions.add(session);
}
```

## Security

### Secured WebSocket

**Защищенный **WebSocket**:**

```java
import jakarta.annotation.security.RolesAllowed;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/secure-chat")
@RolesAllowed("user")
public class SecuredChatEndpoint {

    @OnMessage
    public void onMessage(String message, Session session) {
        // Только авторизованные пользователи могут отправлять сообщения
        session.getAsyncRemote().sendText("Secure: " + message);
    }
}
```

## Message Encoding/Decoding

### Custom Encoders/Decoders

**Кастомные кодировщики:**

```java
import jakarta.websocket.Encoder;
import jakarta.websocket.Decoder;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageEncoder implements Encoder.Text<ChatMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String encode(ChatMessage message) throws EncodeException {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            throw new EncodeException(message, "Encoding error", e);
        }
    }
}

public class MessageDecoder implements Decoder.Text<ChatMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ChatMessage decode(String s) throws DecodeException {
        try {
            return objectMapper.readValue(s, ChatMessage.class);
        } catch (Exception e) {
            throw new DecodeException(s, "Decoding error", e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return s != null;
    }
}
```

**Использование:**

```java
@ServerEndpoint(value = "/chat", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class EncodedChatEndpoint {

    @OnMessage
    public void onMessage(ChatMessage message, Session session) {
        // Обработка декодированного сообщения
    }
}
```

## Connection Management

### Session Management

**Управление сессиями:**

```java
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/managed-chat")
@ApplicationScoped
public class ManagedChatEndpoint {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String userId = getUserId(session);
        sessions.put(userId, session);
        broadcastUserJoined(userId);
    }

    @OnClose
    public void onClose(Session session) {
        String userId = getUserId(session);
        sessions.remove(userId);
        broadcastUserLeft(userId);
    }

    private String getUserId(Session session) {
        return (String) session.getUserProperties().get("userId");
    }
}
```

## WebSocket Performance Optimization

### Connection Pooling

**Оптимизация пула соединений:**

```java
@ApplicationScoped
public class ConnectionPoolManager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final int MAX_CONNECTIONS = 1000;

    public void addSession(String userId, Session session) {
        if (sessions.size() < MAX_CONNECTIONS) {
            sessions.put(userId, session);
        } else {
            throw new TooManyConnectionsException();
        }
    }
}
```

### Message Batching

**Батчинг сообщений:**

```java
@ServerEndpoint("/chat")
public class BatchedChatEndpoint {

    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();

    @Scheduled(every = "100ms")
    void flushMessages() {
        if (!messageQueue.isEmpty()) {
            List<String> batch = new ArrayList<>();
            for (int i = 0; i < 10 && !messageQueue.isEmpty(); i++) {
                batch.add(messageQueue.poll());
            }
            broadcastBatch(batch);
        }
    }
}
```

## Advanced WebSocket Patterns

### Message Queue Pattern

**Очередь сообщений:**

```java
@ApplicationScoped
public class WebSocketMessageQueue {

    private final Map<String, Queue<Message>> userQueues = new ConcurrentHashMap<>();

    public void enqueueMessage(String userId, Message message) {
        userQueues.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>())
            .offer(message);
    }

    public void processQueue(String userId, Session session) {
        Queue<Message> queue = userQueues.get(userId);
        if (queue != null) {
            Message message;
            while ((message = queue.poll()) != null) {
                session.getAsyncRemote().sendObject(message);
            }
        }
    }
}
```

### Heartbeat Pattern

**Heartbeat** для поддержания соединений:**

```java
@ServerEndpoint("/chat")
public class HeartbeatChatEndpoint {

    @OnMessage
    public void onMessage(String message, Session session) {
        if ("ping".equals(message)) {
            session.getAsyncRemote().sendText("pong");
        }
    }

    @Scheduled(every = "30s")
    void sendHeartbeat() {
        // Отправка heartbeat всем подключенным клиентам
        broadcast("ping");
    }
}
```

## WebSocket Connection Pooling

### Connection Management

**Управление соединениями:**

```java
@ApplicationScoped
public class ConnectionPoolManager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final int MAX_CONNECTIONS = 1000;

    public void addSession(String userId, Session session) {
        if (sessions.size() < MAX_CONNECTIONS) {
            sessions.put(userId, session);
        } else {
            throw new TooManyConnectionsException();
        }
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    public Session getSession(String userId) {
        return sessions.get(userId);
    }
}
```

### Message Queue for WebSocket

**Очередь сообщений для **WebSocket**:**

```java
@ApplicationScoped
public class WebSocketMessageQueue {

    private final Map<String, Queue<Message>> userQueues = new ConcurrentHashMap<>();

    public void enqueueMessage(String userId, Message message) {
        userQueues.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>())
            .offer(message);
    }

    public void processQueue(String userId, Session session) {
        Queue<Message> queue = userQueues.get(userId);
        if (queue != null) {
            Message message;
            while ((message = queue.poll()) != null) {
                session.getAsyncRemote().sendObject(message);
            }
        }
    }
}
```


## Заключение

**Quarkus WebSocket** предоставляет мощные инструменты для создания **real-time** приложений. Поддержка **server** и **client endpoints**, различных типов сообщений, **broadcasting**, **reactive support**, **security**, **encoding**/**decoding**, **connection management** и других продвинутых возможностей позволяет создавать эффективные системы **real-time** коммуникации. Правильное использование **WebSocket** паттернов, обработка ошибок, управление подключениями и безопасность являются ключевыми аспектами создания надежных **real-time** приложений.

## Дополнительные ресурсы

- [**Quarkus WebSocket** Guide](https://quarkus.io/guides/websockets)
- [**Jakarta WebSocket** Specification](https://jakarta.ee/specifications/websocket/)
- [**WebSocket** Protocol (RFC 6455)](https://datatracker.ietf.org/doc/html/rfc6455)

## См. также

- [[quarkus-actuator|Quarkus: Actuator — Health Checks и Metrics]]
- [[quarkus-basics|Quarkus: Основы]]
- [[quarkus-cache|Quarkus: Cache — Кеширование данных]]
- [[quarkus-cloud|Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh]]
- [[quarkus-core|Quarkus: Core — CDI, Bean Scopes и Configuration]]
