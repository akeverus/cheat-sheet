---
title: "Micronaut: WebSocket - Real-time Communication и STOMP"
description: "Полное руководство по WebSocket в Micronaut: real-time коммуникации, STOMP, SockJS, чаты, уведомления и best practices"
tags:
  - micronaut
  - websocket
  - stomp
  - sockjs
  - real-time
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-http.md", "micronaut-reactive.md"]
updated: "2026-02-11"
related: ["micronaut-http.md", "micronaut-reactive.md"]
---

# Micronaut: WebSocket - Real-time Communication и STOMP



## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: WebSocket - Real-time Communication и STOMP](#micronaut-websocket-real-time-communication-и-stomp)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка WebSocket](#настройка-websocket)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Basic WebSocket](#basic-websocket)
  - [WebSocket Server](#websocket-server)
  - [WebSocket Client](#websocket-client)
- [STOMP](#stomp)
  - [STOMP Server](#stomp-server)
- [Broadcasting](#broadcasting)
  - [Message Broadcasting](#message-broadcasting)
- [Лучшие практики](#лучшие-практики)
  - [1. Обрабатывайте ошибки соединения](#1-обрабатывайте-ошибки-соединения)
  - [2. Используйте heartbeat для поддержания соединения](#2-используйте-heartbeat-для-поддержания-соединения)
  - [3. Ограничивайте количество соединений](#3-ограничивайте-количество-соединений)
- [WebSocket with Authentication](#websocket-with-authentication)
  - [Secured WebSocket](#secured-websocket)
- [WebSocket with Reactive Streams](#websocket-with-reactive-streams)
  - [Reactive WebSocket](#reactive-websocket)
- [WebSocket Message Types](#websocket-message-types)
  - [Binary Messages](#binary-messages)
  - [JSON Messages](#json-messages)
- [WebSocket Rooms](#websocket-rooms)
  - [Room-based Messaging](#room-based-messaging)
- [WebSocket Error Handling](#websocket-error-handling)
  - [Error Handling](#error-handling)
- [WebSocket Compression](#websocket-compression)
  - [Compression Configuration](#compression-configuration)
- [WebSocket Subprotocols](#websocket-subprotocols)
  - [Custom Subprotocol](#custom-subprotocol)
- [WebSocket Session Management](#websocket-session-management)
  - [Session Tracking](#session-tracking)
- [WebSocket Heartbeat](#websocket-heartbeat)
  - [Keep-alive Mechanism](#keep-alive-mechanism)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет отличную поддержку **WebSocket** для создания **real-time** приложений. Поддержка **STOMP**, **SockJS** и других протоколов позволяет создавать интерактивные приложения с двусторонней коммуникацией.

### Основные возможности

- **WebSocket Server**: Сервер **WebSocket**
- **STOMP**: Поддержка **STOMP** протокола
- **SockJS**: **Fallback** для старых браузеров
- **Real-time Messaging**: **Real-time** обмен сообщениями
- **Broadcasting**: Широковещательная рассылка

## Настройка WebSocket

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut:micronaut-websocket")
    implementation("io.micronaut:micronaut-websocket-server")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  server:
    websocket:
      enabled: true
      path: /ws
```

## Basic WebSocket

### **WebSocket Server**

```java
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

@ServerWebSocket("/ws/chat")
public class ChatWebSocket {
    
    @OnOpen
    public void onOpen(WebSocketSession session) {
        System.out.println("Client connected: " + session.getId());
        session.send("Welcome to chat!");
    }
    
    @OnMessage
    public void onMessage(String message, WebSocketSession session) {
        System.out.println("Received message: " + message);
        session.send("Echo: " + message);
    }
    
    @OnClose
    public void onClose(WebSocketSession session) {
        System.out.println("Client disconnected: " + session.getId());
    }
}
```

### **WebSocket Client**

```java
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.ClientWebSocket;
import io.micronaut.websocket.annotation.OnMessage;

@ClientWebSocket("/ws/chat")
public interface ChatClient {
    
    void send(String message);
    
    @OnMessage
    void onMessage(String message);
}
```

## STOMP

### **STOMP Server**

```java
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.OnClose;

@ServerWebSocket("/ws/stomp")
public class StompWebSocket {
    
    @OnOpen
    public void onOpen(WebSocketSession session) {
        // Инициализация STOMP соединения
    }
    
    @OnMessage
    public void onMessage(String message, WebSocketSession session) {
        // Обработка STOMP сообщений
    }
    
    @OnClose
    public void onClose(WebSocketSession session) {
        // Закрытие STOMP соединения
    }
}
```

## Broadcasting

### **Message Broadcasting**

```java
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import jakarta.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ServerWebSocket("/ws/broadcast")
public class BroadcastWebSocket {
    private static final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }
    
    @OnMessage
    public void onMessage(String message, WebSocketSession session) {
        // Отправка сообщения всем подключенным клиентам
        sessions.values().forEach(s -> s.send(message));
    }
}
```

## Лучшие практики

### 1. Обрабатывайте ошибки соединения

```java
// ✅ Хорошо
@OnError
public void onError(Throwable error, WebSocketSession session) {
    log.error("WebSocket error", error);
}
```

### 2. Используйте **heartbeat** для поддержания соединения

```java
// ✅ Хорошо
@Scheduled(fixedRate = "30s")
public void sendHeartbeat() {
    sessions.values().forEach(session -> session.send("ping"));
}
```

### 3. Ограничивайте количество соединений

```java
// ✅ Хорошо
private static final int MAX_CONNECTIONS = 100;

@OnOpen
public void onOpen(WebSocketSession session) {
    if (sessions.size() >= MAX_CONNECTIONS) {
        session.close();
        return;
    }
    sessions.put(session.getId(), session);
}
```

## WebSocket with Authentication

### **Secured WebSocket**

```java
import io.micronaut.security.annotation.Secured;
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;

@Secured("ROLE_USER")
@ServerWebSocket("/ws/secure-chat")
public class SecuredChatWebSocket {
    
    @OnOpen
    public void onOpen(WebSocketSession session) {
        // Проверка аутентификации
        String username = session.getUriVariables().get("username", String.class);
        System.out.println("Authenticated user connected: " + username);
    }
    
    @OnMessage
    public void onMessage(String message, WebSocketSession session) {
        // Обработка сообщений от аутентифицированных пользователей
        session.send("Echo: " + message);
    }
}
```

## WebSocket with Reactive Streams

### **Reactive WebSocket**

```java
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ServerWebSocket("/ws/reactive-chat")
public class ReactiveChatWebSocket {
    
    @OnMessage
    public Mono<String> onMessage(String message, WebSocketSession session) {
        return Mono.fromCallable(() -> {
            // Обработка сообщения
            return "Processed: " + message;
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
}
```

## WebSocket Message Types

### **Binary Messages**

```java
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.WebSocketSession;

@ServerWebSocket("/ws/binary")
public class BinaryWebSocket {
    
    @OnOpen
    public void onOpen(WebSocketSession session) {
        // Инициализация соединения для бинарных сообщений
    }
    
    @OnMessage
    public void onMessage(byte[] data, WebSocketSession session) {
        // Обработка бинарных данных
        System.out.println("Received binary data: " + data.length + " bytes");
        session.send(data);
    }
}
```

### **JSON Messages**

```java
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServerWebSocket("/ws/json")
public class JsonWebSocket {
    private final ObjectMapper objectMapper;
    
    public JsonWebSocket(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @OnMessage
    public void onMessage(String json, WebSocketSession session) {
        try {
            User user = objectMapper.readValue(json, User.class);
            // Обработка JSON сообщения
            String response = objectMapper.writeValueAsString(user);
            session.send(response);
        } catch (Exception e) {
            session.send("Error: " + e.getMessage());
        }
    }
}
```

## WebSocket Rooms

### **Room-based Messaging**

```java
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerWebSocket("/ws/room/{roomId}")
public class RoomWebSocket {
    private static final ConcurrentHashMap<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(String roomId, WebSocketSession session) {
        rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);
        broadcastToRoom(roomId, "User joined room: " + session.getId(), session);
    }
    
    @OnMessage
    public void onMessage(String roomId, String message, WebSocketSession session) {
        broadcastToRoom(roomId, message, session);
    }
    
    private void broadcastToRoom(String roomId, String message, WebSocketSession sender) {
        Set<WebSocketSession> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            roomSessions.forEach(session -> {
                if (session != sender && session.isOpen()) {
                    session.send(message);
                }
            });
        }
    }
}
```

## WebSocket Error Handling

### **Error Handling**

```java
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.micronaut.websocket.annotation.OnError;
import io.micronaut.websocket.WebSocketSession;

@ServerWebSocket("/ws/error-handling")
public class ErrorHandlingWebSocket {
    
    @OnError
    public void onError(Throwable error, WebSocketSession session) {
        log.error("WebSocket error for session: " + session.getId(), error);
        try {
            session.send("Error occurred: " + error.getMessage());
        } catch (Exception e) {
            log.error("Failed to send error message", e);
        }
    }
}
```

## WebSocket Compression

### **Compression Configuration**

**application.yml:**

```yaml
micronaut:
  server:
    websocket:
      compression:
        enabled: true
```

## WebSocket Subprotocols

### **Custom Subprotocol**

```java
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.micronaut.websocket.annotation.OnOpen;

@ServerWebSocket(value = "/ws/custom", subprotocols = "custom-protocol")
public class CustomProtocolWebSocket {
    
    @OnOpen
    public void onOpen(WebSocketSession session) {
        // Обработка соединения с custom subprotocol
    }
}
```

## WebSocket Session Management

### **Session Tracking**

```java
import io.micronaut.websocket.WebSocketSession;
import jakarta.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SessionManager {
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    public void registerSession(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }
    
    public void unregisterSession(String userId) {
        sessions.remove(userId);
    }
    
    public Optional<WebSocketSession> getSession(String userId) {
        return Optional.ofNullable(sessions.get(userId));
    }
}
```

## WebSocket Heartbeat

### **Keep-alive Mechanism**

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

@Singleton
public class WebSocketHeartbeatService {
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    
    @Scheduled(fixedRate = "30s")
    public void sendHeartbeat() {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                session.send("ping");
            } else {
                sessions.remove(session);
            }
        });
    }
}
```




## Заключение

**Micronaut WebSocket** предоставляет мощные инструменты для создания **real-time** приложений. Поддержка **WebSocket**, **STOMP**, **SockJS**, **broadcasting**, **authentication**, **reactive streams**, **binary messages**, **JSON messages**, **rooms**, **error handling**, **compression**, **subprotocols**, **session management**, **heartbeat** и других продвинутых возможностей позволяет создавать интерактивные приложения с двусторонней коммуникацией.

## Дополнительные ресурсы

- [**Micronaut WebSocket** Documentation](https://micronaut-projects.github.io/micronaut-websocket/latest/guide/)
- [**WebSocket** API (MDN)](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)
- [**STOMP** Protocol](https://stomp.github.io/)
- [SockJS Documentation](https://github.com/sockjs/sockjs-client)
- [**WebSocket Best Practices**](https://www.html5rocks.com/en/tutorials/websockets/basics/)
