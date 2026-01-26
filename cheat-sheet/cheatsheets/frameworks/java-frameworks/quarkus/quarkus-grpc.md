---
title: "Quarkus: gRPC - Remote Procedure Calls"
description: "Полное руководство по gRPC в Quarkus: service definition, client/server, streaming, interceptors и best practices"
tags: ["quarkus", "grpc", "rpc", "microservices", "java"]
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-reactive.md"]
updated: "2025-01-16"
related: ["quarkus-core.md", "quarkus-reactive.md"]
---

# Quarkus: gRPC - Remote Procedure Calls

## Введение

Quarkus предоставляет полную поддержку gRPC для создания высокопроизводительных микросервисов. gRPC использует Protocol Buffers для сериализации и HTTP/2 для транспорта, обеспечивая эффективную коммуникацию между сервисами.

### Основные возможности

- **gRPC Services**: Определение и реализация gRPC сервисов
- **gRPC Clients**: Создание gRPC клиентов
- **Streaming**: Поддержка streaming RPC
- **Interceptors**: Перехватчики для обработки запросов
- **Error Handling**: Обработка ошибок

## Service Definition

### Protocol Buffers

Определение сервиса в .proto файле:

```protobuf
syntax = "proto3";

package com.example;

service UserService {
    rpc GetUser (GetUserRequest) returns (User);
    rpc CreateUser (CreateUserRequest) returns (User);
    rpc ListUsers (ListUsersRequest) returns (stream User);
}

message GetUserRequest {
    int64 id = 1;
}

message CreateUserRequest {
    string name = 1;
    string email = 2;
}

message ListUsersRequest {
    int32 page = 1;
    int32 size = 2;
}

message User {
    int64 id = 1;
    string name = 2;
    string email = 3;
}
```

## gRPC Server

### Service Implementation

Реализация gRPC сервиса:

```java
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.Multi;

@GrpcService
public class UserServiceImpl implements UserService {
    
    @Override
    public Uni<User> getUser(GetUserRequest request) {
        return Uni.createFrom().item(() -> {
            User user = userRepository.findById(request.getId());
            return User.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();
        });
    }
    
    @Override
    public Uni<User> createUser(CreateUserRequest request) {
        return Uni.createFrom().item(() -> {
            User user = userRepository.save(
                new User(request.getName(), request.getEmail())
            );
            return User.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();
        });
    }
    
    @Override
    public Multi<User> listUsers(ListUsersRequest request) {
        return Multi.createFrom().items(
            userRepository.findAll(request.getPage(), request.getSize())
                .stream()
                .map(user -> User.newBuilder()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build())
        );
    }
}
```

## gRPC Client

### Client Configuration

Настройка клиента:

```properties
# application.properties
quarkus.grpc.clients.userservice.host=localhost
quarkus.grpc.clients.userservice.port=9000
```

### Client Usage

Использование клиента:

```java
import io.quarkus.grpc.GrpcClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import io.smallrye.mutiny.Uni;

@Path("/proxy")
public class GrpcProxyResource {
    
    @Inject
    @GrpcClient("userservice")
    UserService userService;
    
    @GET
    @Path("/users/{id}")
    public Uni<User> getUser(@PathParam("id") Long id) {
        GetUserRequest request = GetUserRequest.newBuilder()
            .setId(id)
            .build();
        return userService.getUser(request);
    }
}
```

## Streaming

### Server Streaming

Server-side streaming:

```java
@GrpcService
public class StreamingServiceImpl implements StreamingService {
    
    @Override
    public Multi<Data> streamData(StreamRequest request) {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .onItem().transform(tick -> generateData())
            .select().first(request.getCount());
    }
}
```

### Client Streaming

Client-side streaming:

```java
@GrpcService
public class ClientStreamingServiceImpl implements ClientStreamingService {
    
    @Override
    public Uni<Summary> processStream(Multi<Data> stream) {
        return stream
            .onItem().transform(this::process)
            .collect().with(Collectors.summarizingInt(Data::getValue))
            .map(summary -> Summary.newBuilder()
                .setCount(summary.getCount())
                .setSum(summary.getSum())
                .build());
    }
}
```

## Interceptors

### Server Interceptor

Серверный interceptor:

```java
import io.grpc.ServerInterceptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Metadata;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoggingServerInterceptor implements ServerInterceptor {
    
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        System.out.println("gRPC call: " + call.getMethodDescriptor().getFullMethodName());
        return next.startCall(call, headers);
    }
}
```

## Error Handling

### gRPC Status

Обработка ошибок:

```java
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

@GrpcService
public class ErrorHandlingServiceImpl implements UserService {
    
    @Override
    public Uni<User> getUser(GetUserRequest request) {
        return Uni.createFrom().item(() -> {
            User user = userRepository.findById(request.getId());
            if (user == null) {
                throw Status.NOT_FOUND
                    .withDescription("User not found")
                    .asRuntimeException();
            }
            return convertToProto(user);
        });
    }
}
```

## Best Practices

### 1. Используйте streaming для больших данных

```java
// ✅ Хорошо
public Multi<User> listUsers(ListUsersRequest request) {
    return Multi.createFrom().items(users.stream());
}
```

### 2. Обрабатывайте ошибки правильно

```java
// ✅ Хорошо
if (user == null) {
    throw Status.NOT_FOUND
        .withDescription("User not found")
        .asRuntimeException();
}
```

### 3. Используйте interceptors для cross-cutting concerns

```java
// ✅ Хорошо
@ApplicationScoped
public class LoggingInterceptor implements ServerInterceptor {
    // Логирование всех запросов
}
```

## Bidirectional Streaming

### Full Duplex Streaming

Двунаправленный streaming:

```java
@GrpcService
public class BidirectionalStreamingServiceImpl implements BidirectionalStreamingService {
    
    @Override
    public Multi<Response> bidirectionalStream(Multi<Request> requests) {
        return requests
            .onItem().transform(this::processRequest)
            .onFailure().retry().withBackOff(Duration.ofSeconds(1));
    }
}
```

## Metadata

### Request Metadata

Работа с метаданными:

```java
import io.grpc.Metadata;
import io.grpc.Context;

@GrpcService
public class MetadataServiceImpl implements UserService {
    
    @Override
    public Uni<User> getUser(GetUserRequest request) {
        Metadata metadata = Context.current().get(Metadata.KEY);
        String userId = metadata.get(Metadata.Key.of("user-id", Metadata.ASCII_STRING_MARSHALLER));
        
        return Uni.createFrom().item(() -> {
            // Использование метаданных
            return convertToProto(userRepository.findById(request.getId()));
        });
    }
}
```

## Security

### gRPC Security

Безопасность gRPC:

```properties
# application.properties
quarkus.grpc.server.ssl.certificate=server.crt
quarkus.grpc.server.ssl.key=server.key
quarkus.grpc.server.use-ssl=true
```

### Authentication

Аутентификация:

```java
import io.grpc.ServerInterceptor;
import io.grpc.Metadata;

@ApplicationScoped
public class AuthInterceptor implements ServerInterceptor {
    
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        String token = headers.get(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER));
        if (!isValidToken(token)) {
            call.close(Status.UNAUTHENTICATED, new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }
        
        return next.startCall(call, headers);
    }
}
```

## Best Practices

### 1. Используйте streaming для больших данных

```java
// ✅ Хорошо
public Multi<User> listUsers(ListUsersRequest request) {
    return Multi.createFrom().items(users.stream());
}
```

### 2. Обрабатывайте ошибки правильно

```java
// ✅ Хорошо
if (user == null) {
    throw Status.NOT_FOUND
        .withDescription("User not found")
        .asRuntimeException();
}
```

### 3. Используйте interceptors для cross-cutting concerns

```java
// ✅ Хорошо
@ApplicationScoped
public class LoggingInterceptor implements ServerInterceptor {
    // Логирование всех запросов
}
```

### 4. Используйте метаданные для передачи контекста

```java
// ✅ Хорошо
Metadata metadata = new Metadata();
metadata.put(Metadata.Key.of("user-id", Metadata.ASCII_STRING_MARSHALLER), userId);
```

### 5. Настраивайте SSL для production

```properties
# ✅ Хорошо
quarkus.grpc.server.use-ssl=true
quarkus.grpc.server.ssl.certificate=server.crt
```

## gRPC Performance Optimization

### Connection Pooling

Оптимизация пула соединений:

```properties
quarkus.grpc.clients.user-service.keep-alive-time=30s
quarkus.grpc.clients.user-service.keep-alive-timeout=5s
quarkus.grpc.clients.user-service.max-connection-idle=10s
```

### Message Compression

Сжатие сообщений:

```properties
quarkus.grpc.server.compression=gzip
quarkus.grpc.clients.user-service.compression=gzip
```

### Load Balancing

Балансировка нагрузки:

```properties
quarkus.grpc.clients.user-service.hosts=localhost:9000,localhost:9001,localhost:9002
```

## Advanced gRPC Patterns

### Circuit Breaker Pattern

Реализация Circuit Breaker:

```java
@GrpcService
public class CircuitBreakerUserService implements UserService {
    
    @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5)
    @Override
    public Uni<User> getUser(GetUserRequest request) {
        return userRepository.findById(request.getId())
            .map(this::toUserProto);
    }
}
```

### Retry Pattern

Реализация retry:

```java
@ApplicationScoped
public class RetryUserServiceClient {
    
    @Inject
    @GrpcClient("user-service")
    UserServiceGrpc.UserServiceBlockingStub userService;
    
    public User getUserWithRetry(Long id) {
        return Retry.withExponentialBackoff()
            .maxAttempts(3)
            .get(() -> userService.getUser(
                GetUserRequest.newBuilder().setId(id).build()
            ));
    }
}
```

## Заключение

Quarkus gRPC предоставляет мощные инструменты для создания высокопроизводительных микросервисов. Поддержка gRPC services, clients, streaming, interceptors, error handling, metadata, security и других продвинутых возможностей позволяет создавать эффективные системы межсервисной коммуникации. Правильное использование gRPC паттернов, обработка ошибок, безопасность и оптимизация производительности являются ключевыми аспектами создания надежных микросервисов.

## Дополнительные ресурсы

- [Quarkus gRPC Guide](https://quarkus.io/guides/grpc)
- [gRPC Documentation](https://grpc.io/docs/)
- [Protocol Buffers](https://developers.google.com/protocol-buffers)
- [gRPC Best Practices](https://grpc.io/docs/guides/best-practices/)

