# gRPC

Кратко: gRPC - высокопроизводительный фреймворк для удаленного вызова процедур. Protocol Buffers, HTTP/2, streaming, Spring Boot интеграция.

**Дата последнего обновления:** 2025-01-11

## Полезные ссылки

### Официальная документация
- [gRPC Documentation](https://grpc.io/docs/)
- [Protocol Buffers](https://developers.google.com/protocol-buffers)
- [gRPC Java](https://grpc.io/docs/languages/java/)

### Baeldung
- [gRPC Tutorial](https://www.baeldung.com/grpc)

### См. также
- `../api/graphql.md` - GraphQL
- `../spring/spring-boot.md` - Spring Boot
- `../libraries/jackson.md` - JSON обработка

## Содержание

- [Введение в gRPC](#введение-в-grpc)
- [Protocol Buffers](#protocol-buffers)
- [Типы сервисов](#типы-сервисов)
- [gRPC Java](#grpc-java)
- [Spring Boot и gRPC](#spring-boot-и-grpc)
- [Streaming](#streaming)
- [Load Balancing](#load-balancing)
- [Security](#security)
- [Monitoring](#monitoring)
- [Performance](#performance)
- [Migration from REST](#migration-from-rest)
- [Best Practices](#best-practices)

## Введение в gRPC

gRPC - это высокопроизводительный, открытый фреймворк для удаленного вызова процедур (RPC), разработанный Google. gRPC использует HTTP/2 для транспорта и Protocol Buffers для сериализации данных.

### Преимущества gRPC

- **Высокая производительность**: Бинарный протокол, HTTP/2 multiplexing
- **Строгая типизация**: Protocol Buffers обеспечивают типобезопасность
- **Многоязычная поддержка**: Генерация кода для 10+ языков
- **Streaming**: Bidirectional streaming
- **Встроенная поддержка**: Load balancing, tracing, health checks
- **Компактность**: Protocol Buffers компактнее JSON

### Архитектура

```
Client Application    gRPC Stub    Network    gRPC Server    Service Implementation
        │                 │           │             │                 │
        └─────────────────┼───────────┼─────────────┼─────────────────┘
                          │           │             │
                     HTTP/2      Protocol       gRPC
                    Transport    Buffers      Runtime
```

## Protocol Buffers

Protocol Buffers (protobuf) - это язык описания интерфейсов и формат сериализации данных.

### Определение сообщения

```protobuf
syntax = "proto3";

package com.example.user;

option java_multiple_files = true;
option java_package = "com.example.user";
option java_outer_classname = "UserProto";

// Определение сообщения
message User {
  int64 id = 1;
  string name = 2;
  string email = 3;
  repeated string roles = 4;
  Address address = 5;
  
  enum Status {
    ACTIVE = 0;
    INACTIVE = 1;
    SUSPENDED = 2;
  }
  
  Status status = 6;
}

message Address {
  string street = 1;
  string city = 2;
  string country = 3;
  string zip_code = 4;
}

message CreateUserRequest {
  string name = 1;
  string email = 2;
}

message CreateUserResponse {
  User user = 1;
}

message GetUserRequest {
  int64 id = 1;
}

message GetUserResponse {
  User user = 1;
}
```

### Определение сервиса

```protobuf
service UserService {
  rpc CreateUser(CreateUserRequest) returns (CreateUserResponse);
  rpc GetUser(GetUserRequest) returns (GetUserResponse);
  rpc GetUsers(GetUsersRequest) returns (stream User);
  rpc UpdateUsers(stream UpdateUserRequest) returns (stream UpdateUserResponse);
}
```

### Генерация кода

```xml
<!-- pom.xml -->
<build>
  <extensions>
    <extension>
      <groupId>kr.motd.maven</groupId>
      <artifactId>os-maven-plugin</artifactId>
      <version>1.7.1</version>
    </extension>
  </extensions>
  
  <plugins>
    <plugin>
      <groupId>org.xolstice.maven.plugins</groupId>
      <artifactId>protobuf-maven-plugin</artifactId>
      <version>0.6.1</version>
      <configuration>
        <protocArtifact>com.google.protobuf:protoc:3.21.12</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.53.0</pluginArtifact>
      </configuration>
      <executions>
        <execution>
          <goals>
            <goal>compile</goal>
            <goal>compile-custom</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

## Типы сервисов

### Unary RPC

```protobuf
service CalculatorService {
  rpc Add(AddRequest) returns (AddResponse);
}
```

```java
// Synchronous
AddResponse response = stub.add(AddRequest.newBuilder()
    .setA(10)
    .setB(20)
    .build());

// Asynchronous
stub.add(request, new StreamObserver<AddResponse>() {
    @Override
    public void onNext(AddResponse response) {
        System.out.println("Result: " + response.getResult());
    }
    
    @Override
    public void onError(Throwable t) {
        System.err.println("Error: " + t.getMessage());
    }
    
    @Override
    public void onCompleted() {
        System.out.println("Completed");
    }
});
```

### Server Streaming

```protobuf
service NotificationService {
  rpc Subscribe(SubscribeRequest) returns (stream Notification);
}
```

```java
stub.subscribe(request, new StreamObserver<Notification>() {
    @Override
    public void onNext(Notification notification) {
        System.out.println("Received: " + notification.getMessage());
    }
    
    @Override
    public void onError(Throwable t) {
        System.err.println("Stream error: " + t.getMessage());
    }
    
    @Override
    public void onCompleted() {
        System.out.println("Stream completed");
    }
});
```

### Client Streaming

```protobuf
service UploadService {
  rpc Upload(stream UploadRequest) returns (UploadResponse);
}
```

```java
StreamObserver<UploadRequest> requestObserver = stub.upload(new StreamObserver<UploadResponse>() {
    @Override
    public void onNext(UploadResponse response) {
        System.out.println("Upload completed: " + response.getFileId());
    }
    
    @Override
    public void onError(Throwable t) {
        System.err.println("Upload failed: " + t.getMessage());
    }
    
    @Override
    public void onCompleted() {
        System.out.println("Upload finished");
    }
});

// Send chunks
for (byte[] chunk : chunks) {
    requestObserver.onNext(UploadRequest.newBuilder()
        .setData(ByteString.copyFrom(chunk))
        .build());
}
requestObserver.onCompleted();
```

### Bidirectional Streaming

```protobuf
service ChatService {
  rpc Chat(stream ChatMessage) returns (stream ChatMessage);
}
```

```java
StreamObserver<ChatMessage> requestObserver = stub.chat(new StreamObserver<ChatMessage>() {
    @Override
    public void onNext(ChatMessage message) {
        System.out.println("Received: " + message.getContent());
    }
    
    @Override
    public void onError(Throwable t) {
        System.err.println("Chat error: " + t.getMessage());
    }
    
    @Override
    public void onCompleted() {
        System.out.println("Chat ended");
    }
});

// Send messages
requestObserver.onNext(ChatMessage.newBuilder()
    .setContent("Hello!")
    .build());
```

## gRPC Java

### Создание сервера

```java
public class UserServer {
    
    private Server server;
    
    private void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
            .addService(new UserServiceImpl())
            .build()
            .start();
            
        System.out.println("Server started on port " + port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server");
            UserServer.this.stop();
        }));
    }
    
    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
    
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        final UserServer server = new UserServer();
        server.start();
        server.blockUntilShutdown();
    }
}
```

### Реализация сервиса

```java
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    
    private final UserRepository userRepository;
    
    @Override
    public void createUser(CreateUserRequest request, 
                          StreamObserver<CreateUserResponse> responseObserver) {
        
        try {
            // Создание пользователя
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            
            User savedUser = userRepository.save(user);
            
            // Формирование ответа
            User protoUser = User.newBuilder()
                .setId(savedUser.getId())
                .setName(savedUser.getName())
                .setEmail(savedUser.getEmail())
                .build();
                
            CreateUserResponse response = CreateUserResponse.newBuilder()
                .setUser(protoUser)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                .withDescription("Failed to create user")
                .withCause(e)
                .asRuntimeException());
        }
    }
    
    @Override
    public void getUser(GetUserRequest request, 
                       StreamObserver<GetUserResponse> responseObserver) {
        
        try {
            User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            User protoUser = User.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();
                
            GetUserResponse response = GetUserResponse.newBuilder()
                .setUser(protoUser)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND
                .withDescription("User not found")
                .asRuntimeException());
        }
    }
}
```

### Создание клиента

```java
public class UserClient {
    
    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;
    private final UserServiceGrpc.UserServiceStub asyncStub;
    
    public UserClient(Channel channel) {
        blockingStub = UserServiceGrpc.newBlockingStub(channel);
        asyncStub = UserServiceGrpc.newStub(channel);
    }
    
    public void createUser(String name, String email) {
        CreateUserRequest request = CreateUserRequest.newBuilder()
            .setName(name)
            .setEmail(email)
            .build();
            
        CreateUserResponse response = blockingStub.createUser(request);
        
        System.out.println("Created user: " + response.getUser().getName());
    }
    
    public void getUser(long id) {
        GetUserRequest request = GetUserRequest.newBuilder()
            .setId(id)
            .build();
            
        GetUserResponse response = blockingStub.getUser(request);
        
        System.out.println("User: " + response.getUser().getName());
    }
    
    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build();
            
        try {
            UserClient client = new UserClient(channel);
            client.createUser("John Doe", "john@example.com");
            client.getUser(1);
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
```

## Spring Boot и gRPC

### Зависимости

```xml
<dependencies>
    <dependency>
        <groupId>net.devh</groupId>
        <artifactId>grpc-server-spring-boot-starter</artifactId>
        <version>3.1.0.RELEASE</version>
    </dependency>
    
    <dependency>
        <groupId>net.devh</groupId>
        <artifactId>grpc-client-spring-boot-starter</artifactId>
        <version>3.1.0.RELEASE</version>
    </dependency>
</dependencies>
```

### Конфигурация сервера

```yaml
# application.yml
grpc:
  server:
    port: 9090
  client:
    user-service:
      address: 'static://localhost:9090'
      negotiation-type: plaintext
```

### gRPC Service с Spring

```java
@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void createUser(CreateUserRequest request, 
                          StreamObserver<CreateUserResponse> responseObserver) {
        
        try {
            com.example.User user = userService.createUser(
                request.getName(), 
                request.getEmail()
            );
            
            User protoUser = User.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();
                
            CreateUserResponse response = CreateUserResponse.newBuilder()
                .setUser(protoUser)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }
}
```

### gRPC Client с Spring

```java
@Service
public class UserGrpcClient {
    
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    
    public com.example.User createUser(String name, String email) {
        CreateUserRequest request = CreateUserRequest.newBuilder()
            .setName(name)
            .setEmail(email)
            .build();
            
        CreateUserResponse response = userServiceStub.createUser(request);
        
        User protoUser = response.getUser();
        return new com.example.User(
            protoUser.getId(),
            protoUser.getName(),
            protoUser.getEmail()
        );
    }
    
    public com.example.User getUser(long id) {
        GetUserRequest request = GetUserRequest.newBuilder()
            .setId(id)
            .build();
            
        GetUserResponse response = userServiceStub.getUser(request);
        
        User protoUser = response.getUser();
        return new com.example.User(
            protoUser.getId(),
            protoUser.getName(),
            protoUser.getEmail()
        );
    }
}
```

## Streaming

### Server-side Streaming

```java
@GrpcService
public class NotificationService extends NotificationServiceGrpc.NotificationServiceImplBase {
    
    @Override
    public void subscribe(SubscribeRequest request, 
                         StreamObserver<Notification> responseObserver) {
        
        String userId = request.getUserId();
        
        // Имитация получения уведомлений
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        
        executor.scheduleAtFixedRate(() -> {
            try {
                Notification notification = Notification.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setUserId(userId)
                    .setMessage("New notification for " + userId)
                    .setTimestamp(System.currentTimeMillis())
                    .build();
                    
                responseObserver.onNext(notification);
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
        
        // Остановить через 1 минуту
        executor.schedule(() -> {
            responseObserver.onCompleted();
            executor.shutdown();
        }, 1, TimeUnit.MINUTES);
    }
}
```

### Client-side Streaming

```java
@GrpcService
public class FileUploadService extends FileUploadServiceGrpc.FileUploadServiceImplBase {
    
    @Override
    public StreamObserver<UploadRequest> upload(StreamObserver<UploadResponse> responseObserver) {
        
        return new StreamObserver<UploadRequest>() {
            private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            
            @Override
            public void onNext(UploadRequest request) {
                try {
                    buffer.write(request.getData().toByteArray());
                } catch (IOException e) {
                    responseObserver.onError(
                        Status.INTERNAL.withDescription("Failed to write data").asRuntimeException()
                    );
                }
            }
            
            @Override
            public void onError(Throwable t) {
                System.err.println("Upload failed: " + t.getMessage());
            }
            
            @Override
            public void onCompleted() {
                try {
                    // Сохранить файл
                    String fileId = saveFile(buffer.toByteArray());
                    
                    UploadResponse response = UploadResponse.newBuilder()
                        .setFileId(fileId)
                        .setSize(buffer.size())
                        .build();
                        
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                    
                } catch (Exception e) {
                    responseObserver.onError(
                        Status.INTERNAL.withDescription("Failed to save file").asRuntimeException()
                    );
                }
            }
        };
    }
    
    private String saveFile(byte[] data) {
        // Логика сохранения файла
        return UUID.randomUUID().toString();
    }
}
```

## Load Balancing

### Client-side Load Balancing

```java
ManagedChannel channel = ManagedChannelBuilder
    .forTarget("user-service:9090")
    .defaultLoadBalancingPolicy("round_robin")
    .usePlaintext()
    .build();
```

### Service Discovery

```java
// С Eureka
ManagedChannel channel = ManagedChannelBuilder
    .forTarget("user-service")
    .nameResolverFactory(new EurekaNameResolverProvider())
    .defaultLoadBalancingPolicy("round_robin")
    .usePlaintext()
    .build();
```

### Spring Cloud LoadBalancer

```yaml
# application.yml
grpc:
  client:
    user-service:
      address: 'discovery://user-service:9090'
      negotiation-type: plaintext
```

## Security

### SSL/TLS

```java
// Server с SSL
Server server = ServerBuilder.forPort(8443)
    .addService(new UserServiceImpl())
    .useTransportSecurity(
        new File("server.crt"),
        new File("server.pem")
    )
    .build();
```

```java
// Client с SSL
ManagedChannel channel = ManagedChannelBuilder
    .forAddress("localhost", 8443)
    .useTransportSecurity(
        TlsChannelCredentials.create(
            new File("ca.crt").toPath(),
            new File("client.crt").toPath(),
            new File("client.pem").toPath()
        )
    )
    .build();
```

### Authentication

```java
// JWT Authentication
@GrpcService
public class AuthenticatedService extends AuthenticatedServiceGrpc.AuthenticatedServiceImplBase {
    
    @Override
    public void protectedMethod(ProtectedRequest request, 
                               StreamObserver<ProtectedResponse> responseObserver) {
        
        // Извлечение токена из контекста
        String token = AUTHORIZATION_CONTEXT_KEY.get();
        
        if (!validateToken(token)) {
            responseObserver.onError(
                Status.UNAUTHENTICATED.withDescription("Invalid token").asRuntimeException()
            );
            return;
        }
        
        // Обработка запроса
        ProtectedResponse response = ProtectedResponse.newBuilder()
            .setMessage("Authenticated request processed")
            .build();
            
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

### Interceptor

```java
public class AuthInterceptor implements ServerInterceptor {
    
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, 
            Metadata headers, 
            ServerCallHandler<ReqT, RespT> next) {
        
        String token = headers.get(AUTHORIZATION_METADATA_KEY);
        
        Context context = Context.current().withValue(AUTHORIZATION_CONTEXT_KEY, token);
        
        return Contexts.interceptCall(context, call, headers, next);
    }
}
```

## Monitoring

### Health Checks

```java
@GrpcService
public class HealthService extends HealthGrpc.HealthImplBase {
    
    @Override
    public void check(HealthCheckRequest request, 
                     StreamObserver<HealthCheckResponse> responseObserver) {
        
        HealthCheckResponse response = HealthCheckResponse.newBuilder()
            .setStatus(HealthCheckResponse.ServingStatus.SERVING)
            .build();
            
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

### Metrics

```java
@GrpcService
public class MetricsService extends MetricsServiceGrpc.MetricsServiceImplBase {
    
    @Override
    public void getMetrics(MetricsRequest request, 
                          StreamObserver<MetricsResponse> responseObserver) {
        
        // Сбор метрик
        MetricsResponse response = MetricsResponse.newBuilder()
            .setActiveConnections(getActiveConnections())
            .setTotalRequests(getTotalRequests())
            .build();
            
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

### OpenTelemetry Integration

```java
@Configuration
public class TracingConfig {
    
    @Bean
    public ServerInterceptor tracingInterceptor() {
        return new OpenTelemetryServerInterceptor();
    }
    
    @Bean
    public ClientInterceptor clientTracingInterceptor() {
        return new OpenTelemetryClientInterceptor();
    }
}
```

## Performance

### Connection Pooling

```java
ManagedChannel channel = ManagedChannelBuilder
    .forAddress("localhost", 9090)
    .usePlaintext()
    .maxInboundMessageSize(10 * 1024 * 1024) // 10MB
    .keepAliveTime(30, TimeUnit.SECONDS)
    .keepAliveTimeout(10, TimeUnit.SECONDS)
    .build();
```

### Compression

```java
// Server с compression
Server server = ServerBuilder.forPort(9090)
    .addService(new UserServiceImpl())
    .compressorRegistry(
        CompressorRegistry.getDefaultInstance()
            .register(GzipCompressor.INSTANCE)
    )
    .build();
```

```java
// Client с compression
ManagedChannel channel = ManagedChannelBuilder
    .forAddress("localhost", 9090)
    .compressorRegistry(
        CompressorRegistry.getDefaultInstance()
            .register(GzipCompressor.INSTANCE)
    )
    .build();
```

### Async Processing

```java
@GrpcService
public class AsyncService extends AsyncServiceGrpc.AsyncServiceImplBase {
    
    @Override
    public void asyncOperation(AsyncRequest request, 
                              StreamObserver<AsyncResponse> responseObserver) {
        
        // Асинхронная обработка
        CompletableFuture.supplyAsync(() -> {
            // Долгая операция
            return processAsync(request);
        }).thenAccept(result -> {
            AsyncResponse response = AsyncResponse.newBuilder()
                .setResult(result)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }).exceptionally(throwable -> {
            responseObserver.onError(
                Status.INTERNAL.withDescription(throwable.getMessage()).asRuntimeException()
            );
            return null;
        });
    }
}
```

## Migration from REST

### Сравнение REST vs gRPC

| Аспект | REST | gRPC |
|--------|------|------|
| Протокол | HTTP/1.1 | HTTP/2 |
| Формат | JSON/XML | Protocol Buffers |
| Типизация | Нет | Строгая |
| Streaming | Ограниченный | Полный |
| Производительность | Средняя | Высокая |
| Кодогенерация | Нет | Да |

### Миграция шаг за шагом

1. **Определить API**: Создать .proto файлы
2. **Генерировать код**: Сгенерировать stubs
3. **Создать сервисы**: Реализовать gRPC сервисы
4. **Мигрировать клиентов**: Обновить клиентский код
5. **Тестирование**: Полное тестирование
6. **Мониторинг**: Настроить observability

### API Gateway для REST и gRPC

```java
@RestController
public class ApiGatewayController {
    
    @Autowired
    private UserGrpcClient userGrpcClient;
    
    @PostMapping("/api/users")
    public User createUser(@RequestBody CreateUserRequest request) {
        // REST API вызывает gRPC
        return userGrpcClient.createUser(request.getName(), request.getEmail());
    }
    
    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable Long id) {
        // REST API вызывает gRPC
        return userGrpcClient.getUser(id);
    }
}
```

## Best Practices

1. **Используйте Protocol Buffers правильно**: Определяйте сообщения четко и последовательно
2. **Обработка ошибок**: Используйте Status codes правильно
3. **Streaming разумно**: Не злоупотребляйте bidirectional streaming
4. **Безопасность**: Всегда используйте TLS в продакшене
5. **Мониторинг**: Настраивайте метрики и tracing
6. **Версионирование**: Планируйте изменения API
7. **Тестирование**: Тестируйте все типы RPC
8. **Документация**: Документируйте .proto файлы
9. **Load Balancing**: Используйте client-side load balancing
10. **Timeouts**: Настраивайте timeouts для всех вызовов

## Примеры

### Полный gRPC сервис с Spring Boot

```protobuf
// user-service.proto
syntax = "proto3";

package com.example.user;

option java_multiple_files = true;
option java_package = "com.example.user";

service UserService {
  rpc CreateUser(CreateUserRequest) returns (CreateUserResponse);
  rpc GetUser(GetUserRequest) returns (GetUserResponse);
  rpc GetUsers(GetUsersRequest) returns (stream User);
  rpc UpdateUser(UpdateUserRequest) returns (UpdateUserResponse);
}

message User {
  int64 id = 1;
  string name = 2;
  string email = 3;
  repeated string roles = 4;
  Status status = 5;
  
  enum Status {
    ACTIVE = 0;
    INACTIVE = 1;
  }
}

message CreateUserRequest {
  string name = 1;
  string email = 2;
}

message CreateUserResponse {
  User user = 1;
}

message GetUserRequest {
  int64 id = 1;
}

message GetUserResponse {
  User user = 1;
}

message GetUsersRequest {
  int32 page = 1;
  int32 size = 2;
}

message UpdateUserRequest {
  int64 id = 1;
  string name = 2;
  string email = 3;
}

message UpdateUserResponse {
  User user = 1;
}
```

### Spring Boot gRPC Server

```java
@SpringBootApplication
public class GrpcServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GrpcServerApplication.class, args);
    }
}

@Configuration
public class GrpcConfig {
    
    @Bean
    public ServerInterceptor authInterceptor() {
        return new AuthInterceptor();
    }
}

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void createUser(CreateUserRequest request, 
                          StreamObserver<CreateUserResponse> responseObserver) {
        
        try {
            com.example.domain.User user = userService.createUser(
                request.getName(), request.getEmail()
            );
            
            User protoUser = User.newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail())
                .setStatus(User.Status.ACTIVE)
                .build();
                
            CreateUserResponse response = CreateUserResponse.newBuilder()
                .setUser(protoUser)
                .build();
                
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }
    
    @Override
    public void getUsers(GetUsersRequest request, 
                        StreamObserver<User> responseObserver) {
        
        try {
            List<com.example.domain.User> users = userService.getUsers(
                request.getPage(), request.getSize()
            );
            
            for (com.example.domain.User user : users) {
                User protoUser = User.newBuilder()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .setStatus(User.Status.ACTIVE)
                    .build();
                    
                responseObserver.onNext(protoUser);
            }
            
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }
}
```

### Spring Boot gRPC Client

```java
@Configuration
public class GrpcClientConfig {
    
    @Bean
    @GrpcClient("user-service")
    public UserServiceGrpc.UserServiceBlockingStub userServiceStub() {
        return UserServiceGrpc.newBlockingStub(
            ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build()
        );
    }
}

@Service
public class UserGrpcClient {
    
    @Autowired
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    
    public com.example.domain.User createUser(String name, String email) {
        CreateUserRequest request = CreateUserRequest.newBuilder()
            .setName(name)
            .setEmail(email)
            .build();
            
        CreateUserResponse response = userServiceStub.createUser(request);
        
        User protoUser = response.getUser();
        return new com.example.domain.User(
            protoUser.getId(),
            protoUser.getName(),
            protoUser.getEmail()
        );
    }
    
    public List<com.example.domain.User> getUsers(int page, int size) {
        GetUsersRequest request = GetUsersRequest.newBuilder()
            .setPage(page)
            .setSize(size)
            .build();
            
        Iterator<User> users = userServiceStub.getUsers(request);
        
        List<com.example.domain.User> result = new ArrayList<>();
        users.forEachRemaining(protoUser -> {
            result.add(new com.example.domain.User(
                protoUser.getId(),
                protoUser.getName(),
                protoUser.getEmail()
            ));
        });
        
        return result;
    }
}
```

### REST API Gateway

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserGrpcClient userGrpcClient;
    
    @PostMapping
    public ResponseEntity<com.example.domain.User> createUser(@RequestBody CreateUserRequest request) {
        try {
            com.example.domain.User user = userGrpcClient.createUser(
                request.getName(), request.getEmail()
            );
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<com.example.domain.User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            List<com.example.domain.User> users = userGrpcClient.getUsers(page, size);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
```

Этот файл содержит детальное описание gRPC: от основных концепций до полной интеграции с Spring Boot, включая Protocol Buffers, все типы RPC, streaming, security, monitoring и best practices. Он охватывает все ключевые аспекты создания высокопроизводительных микросервисов с gRPC.
