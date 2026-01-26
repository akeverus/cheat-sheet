# Protocol Buffers (Protobuf)

Protocol Buffers - это язык-агностичный бинарный формат сериализации структурированных данных от Google. Предоставляет эффективную и быструю альтернативу JSON/XML для сериализации данных.

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Protocol Buffers](https://protobuf.dev/) - Официальный сайт
- [Protocol Buffers GitHub](https://github.com/protocolbuffers/protobuf) - Репозиторий проекта
- [Protocol Buffers Documentation](https://protobuf.dev/docs/) - Документация

### См. также
- `../libraries/jackson.md` - Jackson для JSON
- `../libraries/java-gson.md` - Gson для JSON

## Содержание

- [Основные возможности](#основные-возможности)
  - [Определение схемы (.proto файлы)](#определение-схемы-proto-файлы)
  - [Генерация Java кода](#генерация-java-кода)
  - [Базовое использование](#базовое-использование)
  - [Работа с JSON](#работа-с-json)
  - [Oneof поля](#oneof-поля)
  - [Map поля](#map-поля)
  - [Any тип](#any-тип)
  - [Extensions](#extensions)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Определение сервиса](#определение-сервиса)
- [gRPC Integration](#grpc-integration)
  - [gRPC клиент](#grpc-клиент)
  - [gRPC асинхронный клиент](#grpc-асинхронный-клиент)
  - [gRPC сервер](#grpc-сервер)
- [Performance Optimization](#performance-optimization)
  - [Lazy Parsing](#lazy-parsing)
  - [Direct ByteBuffer](#direct-bytebuffer)
  - [Object Reuse](#object-reuse)
  - [Message Merging](#message-merging)
- [Schema Evolution](#schema-evolution)
  - [Добавление полей](#добавление-полей)
  - [Reserved поля](#reserved-поля)
  - [Обновление enum](#обновление-enum)
- [Custom Options](#custom-options)
  - [Определение опций](#определение-опций)
  - [Использование опций](#использование-опций)
- [Validation](#validation)
  - [Custom Validators](#custom-validators)
  - [Schema Validation](#schema-validation)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration](#configuration)
  - [gRPC Service](#grpc-service)
  - [Web MVC Controller](#web-mvc-controller)
- [Testing](#testing)
  - [Unit Testing](#unit-testing)
  - [gRPC Testing](#grpc-testing)
- [Performance Comparison](#performance-comparison)
  - [Protobuf vs JSON vs XML](#protobuf-vs-json-vs-xml)
- [Migration и Best Practices](#migration-и-best-practices)
  - [Migration from JSON](#migration-from-json)
  - [Best Practices](#best-practices)
- [Advanced Features](#advanced-features)
  - [Dynamic Messages](#dynamic-messages)
  - [Custom Code Generation](#custom-code-generation)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Debugging](#debugging)
- [Experimental Features](#experimental-features)
  - [Proto4 (предварительная версия)](#proto4-предварительная-версия)
  - [Zero-copy сериализация](#zero-copy-сериализация)

## Основные возможности

### Определение схемы (.proto файлы)
```protobuf
syntax = "proto3";

package com.example;

option java_package = "com.example.protobuf";
option java_outer_classname = "UserProto";

// Сообщение пользователя
message User {
  int32 id = 1;
  string name = 2;
  string email = 3;
  repeated string tags = 4;
  Address address = 5;
  Status status = 6;

  enum Status {
    ACTIVE = 0;
    INACTIVE = 1;
    SUSPENDED = 2;
  }

  message Address {
    string street = 1;
    string city = 2;
    string country = 3;
    string postal_code = 4;
  }
}

// Сервис для работы с пользователями
service UserService {
  rpc GetUser(GetUserRequest) returns (User);
  rpc CreateUser(CreateUserRequest) returns (CreateUserResponse);
  rpc ListUsers(ListUsersRequest) returns (ListUsersResponse);
}

message GetUserRequest {
  int32 user_id = 1;
}

message CreateUserRequest {
  User user = 1;
}

message CreateUserResponse {
  int32 user_id = 1;
  string message = 2;
}

message ListUsersRequest {
  int32 page = 1;
  int32 limit = 2;
}

message ListUsersResponse {
  repeated User users = 1;
  int32 total_count = 2;
}
```

### Генерация Java кода
```bash
# Генерация Java классов из .proto файла
protoc --java_out=src/main/java user.proto

# Или через Maven/Gradle плагин
```

### Базовое использование
```java
// Создание объекта
UserProto.User user = UserProto.User.newBuilder()
    .setId(1)
    .setName("John Doe")
    .setEmail("john@example.com")
    .addTags("developer")
    .addTags("java")
    .setAddress(UserProto.User.Address.newBuilder()
        .setStreet("123 Main St")
        .setCity("New York")
        .setCountry("USA")
        .setPostalCode("10001")
        .build())
    .setStatus(UserProto.User.Status.ACTIVE)
    .build();

// Сериализация в байты
byte[] data = user.toByteArray();

// Десериализация из байт
UserProto.User deserializedUser = UserProto.User.parseFrom(data);

// Сериализация в OutputStream
user.writeTo(outputStream);

// Десериализация из InputStream
UserProto.User userFromStream = UserProto.User.parseFrom(inputStream);
```

### Работа с JSON
```java
// Конвертация в JSON
JsonFormat.Printer printer = JsonFormat.printer();
String json = printer.print(user);

// Конвертация из JSON
JsonFormat.Parser parser = JsonFormat.parser();
UserProto.User.Builder builder = UserProto.User.newBuilder();
parser.merge(json, builder);
UserProto.User userFromJson = builder.build();
```

## Продвинутые возможности

### Oneof поля
```protobuf
message SearchRequest {
  string query = 1;

  oneof search_type {
    string exact_match = 2;
    string fuzzy_match = 3;
    Range range = 4;
  }

  message Range {
    int32 min = 1;
    int32 max = 2;
  }
}
```

```java
/**
 * Использование oneof в Protocol Buffers
 * oneof позволяет установить только одно поле из группы полей (mutually exclusive)
 */
// Создание SearchRequest с установленным полем exact_match
// В oneof можно установить только одно поле: либо exact_match, либо fuzzy_match, либо range
SearchRequest request1 = SearchRequest.newBuilder()
    .setQuery("java")                              // Обычное поле (не в oneof)
    .setExactMatch("java programming")             // Устанавливается exact_match (это поле в oneof)
    // После установки exact_match, fuzzy_match и range автоматически очищаются
    .build();

// Проверка, какое поле установлено в oneof
// Используем has* методы для проверки какое поле oneof установлено
if (request.hasExactMatch()) {
    // Поле exact_match установлено - получаем его значение
    String exact = request.getExactMatch();
    // Используем exact для точного поиска
} else if (request.hasFuzzyMatch()) {
    // Поле fuzzy_match установлено - получаем его значение
    String fuzzy = request.getFuzzyMatch();
    // Используем fuzzy для нечеткого поиска
} else if (request.hasRange()) {
    // Поле range установлено - получаем его значение
    SearchRequest.Range range = request.getRange();
    // Используем range для поиска в диапазоне значений
}
// Если ни одно поле oneof не установлено, все has* методы вернут false
```

### Map поля
```protobuf
message Config {
  map<string, string> properties = 1;
  map<string, Value> complex_values = 2;
}

message Value {
  oneof value_type {
    string string_value = 1;
    int32 int_value = 2;
    bool bool_value = 3;
  }
}
```

```java
/**
 * Работа с map полями в Protocol Buffers
 * Map позволяет хранить пары ключ-значение в сообщении
 */
// Создание Config с map полем properties
// Map в Protobuf автоматически преобразуется в Java Map
Config config = Config.newBuilder()
    .putProperties("app.name", "MyApp")        // Добавляем пару ключ-значение в map
    .putProperties("app.version", "1.0.0")     // Добавляем еще одну пару
    // putProperties добавляет или обновляет значение по ключу
    .build();

// Доступ к map - получение Java Map из Protobuf сообщения
// getPropertiesMap() возвращает неизменяемый Map<String, String>
Map<String, String> properties = config.getPropertiesMap();
String appName = properties.get("app.name");    // Получаем значение по ключу "app.name"

// Итерация по map - перебор всех пар ключ-значение
for (Map.Entry<String, String> entry : config.getPropertiesMap().entrySet()) {
    // Выводим каждую пару ключ-значение
    System.out.println(entry.getKey() + " = " + entry.getValue());
    // Output:
    // app.name = MyApp
    // app.version = 1.0.0
}
```

### Any тип
```protobuf
import "google/protobuf/any.proto";

message Message {
  string sender = 1;
  google.protobuf.Any payload = 2;
}
```

```java
/**
 * Работа с Any типом в Protocol Buffers
 * Any позволяет упаковать любое Protobuf сообщение в другое сообщение (типобезопасная упаковка)
 */
// Упаковка произвольного сообщения в Any
// Создаем User сообщение для упаковки
UserProto.User user = UserProto.User.newBuilder()
    .setName("John")  // Устанавливаем имя пользователя
    .build();

// Упаковываем User в Any и добавляем в Message
Message message = Message.newBuilder()
    .setSender("system")              // Устанавливаем отправителя сообщения
    .setPayload(Any.pack(user))        // Упаковываем User в Any через Any.pack()
    // Any.pack() создает Any объект содержащий сериализованный User
    .build();

// Распаковка Any обратно в конкретный тип
try {
    // Проверяем что payload содержит User тип
    // is() проверяет тип упакованного сообщения без распаковки
    if (message.getPayload().is(UserProto.User.class)) {
        // Распаковываем Any в User через unpack()
        // unpack() десериализует Any и возвращает объект указанного типа
        UserProto.User unpackedUser = message.getPayload().unpack(UserProto.User.class);
        System.out.println("User: " + unpackedUser.getName());  // Используем распакованный User
    }
} catch (InvalidProtocolBufferException e) {
    // Обработка ошибки распаковки (если тип не совпадает или данные повреждены)
    e.printStackTrace();
}
```

### Extensions
```protobuf
message Person {
  string name = 1;
  extensions 100 to 199;
}

extend Person {
  optional string nickname = 100;
  optional int32 age = 101;
}
```

```java
// Работа с extensions
Person person = Person.newBuilder()
    .setName("John")
    .setExtension(PersonProto.nickname, "Johnny")
    .setExtension(PersonProto.age, 30)
    .build();

// Доступ к extensions
String nickname = person.getExtension(PersonProto.nickname);
Integer age = person.getExtension(PersonProto.age);
```

## gRPC Integration

### Определение сервиса
```protobuf
service UserService {
  rpc GetUser(GetUserRequest) returns (User);
  rpc CreateUser(CreateUserRequest) returns (CreateUserResponse);
  rpc GetUsersStream(GetUsersRequest) returns (stream User);
  rpc UploadFile(stream FileChunk) returns (UploadResponse);
}
```

### gRPC клиент
```java
// Создание blocking stub
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
    .usePlaintext()
    .build();

UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

// Unary call
GetUserRequest request = GetUserRequest.newBuilder().setUserId(1).build();
User user = stub.getUser(request);

// Server streaming
Iterator<User> users = stub.getUsersStream(GetUsersRequest.newBuilder().build());
while (users.hasNext()) {
    User user = users.next();
    System.out.println("User: " + user.getName());
}

channel.shutdown();
```

### gRPC асинхронный клиент
```java
// Async stub
UserServiceGrpc.UserServiceStub asyncStub = UserServiceGrpc.newStub(channel);

// Unary async call
asyncStub.getUser(request, new StreamObserver<User>() {
    @Override
    public void onNext(User user) {
        System.out.println("Received user: " + user.getName());
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onCompleted() {
        System.out.println("Call completed");
    }
});
```

### gRPC сервер
```java
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void getUser(GetUserRequest request, StreamObserver<User> responseObserver) {
        try {
            // Получение пользователя из базы данных
            User user = getUserFromDatabase(request.getUserId());

            responseObserver.onNext(user);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                .withDescription("Database error")
                .withCause(e)
                .asRuntimeException());
        }
    }

    @Override
    public void getUsersStream(GetUsersRequest request, StreamObserver<User> responseObserver) {
        // Server streaming
        List<User> users = getAllUsers();
        for (User user : users) {
            responseObserver.onNext(user);
            try {
                Thread.sleep(100); // Имитация задержки
            } catch (InterruptedException e) {
                responseObserver.onError(Status.CANCELLED.asRuntimeException());
                return;
            }
        }
        responseObserver.onCompleted();
    }
}
```

## Performance Optimization

### Lazy Parsing
```java
// Использование parseFrom для lazy parsing
UserProto.User user = UserProto.User.parseFrom(data);

// Поля парсятся только при первом доступе
String name = user.getName(); // Парсинг происходит здесь
```

### Direct ByteBuffer
```java
// Использование DirectByteBuffer для zero-copy
ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
buffer.put(data);
buffer.flip();

UserProto.User user = UserProto.User.parseFrom(CodedInputStream.newInstance(buffer));
```

### Object Reuse
```java
// Повторное использование билдера для уменьшения аллокаций
UserProto.User.Builder builder = UserProto.User.newBuilder();

for (UserEntity entity : entities) {
    builder.clear();
    UserProto.User user = builder
        .setId(entity.getId())
        .setName(entity.getName())
        .build();

    // Использование user
    processUser(user);
}
```

### Message Merging
```java
// Слияние сообщений
UserProto.User baseUser = UserProto.User.newBuilder()
    .setId(1)
    .setName("John")
    .build();

UserProto.User additionalInfo = UserProto.User.newBuilder()
    .setEmail("john@example.com")
    .setStatus(UserProto.User.Status.ACTIVE)
    .build();

// Слияние
UserProto.User mergedUser = UserProto.User.newBuilder(baseUser)
    .mergeFrom(additionalInfo)
    .build();

// Результат: id=1, name="John", email="john@example.com", status=ACTIVE
```

## Schema Evolution

### Добавление полей
```protobuf
// Version 1
message User {
  string name = 1;
}

// Version 2 - добавление новых полей
message User {
  string name = 1;
  string email = 2;    // Новое поле
  int32 age = 3;       // Новое поле
}
```

```java
// Код version 1 может читать данные version 2
// Новые поля будут иметь default значения
User user = User.parseFrom(oldData);
String name = user.getName();     // OK
String email = user.getEmail();   // "" (default для string)
int age = user.getAge();         // 0 (default для int32)
```

### Reserved поля
```protobuf
message User {
  reserved 4, 8 to 10;      // Зарезервированные номера полей
  reserved "old_field";     // Зарезервированные имена

  string name = 1;
  string email = 2;
  int32 age = 3;
}
```

### Обновление enum
```protobuf
enum Status {
  ACTIVE = 0;
  INACTIVE = 1;
  SUSPENDED = 2;
  // DELETED = 3;  // Не удалять, использовать reserved
}

// Лучше:
enum Status {
  ACTIVE = 0;
  INACTIVE = 1;
  SUSPENDED = 2;
  reserved 3;      // Зарезервировано для DELETED
}
```

## Custom Options

### Определение опций
```protobuf
import "google/protobuf/descriptor.proto";

extend google.protobuf.FieldOptions {
  optional string field_description = 50000;
  optional bool sensitive = 50001;
}

extend google.protobuf.MessageOptions {
  optional string table_name = 50002;
}
```

### Использование опций
```protobuf
message User {
  option (table_name) = "users";

  string name = 1 [(field_description) = "User's full name"];
  string password = 2 [(sensitive) = true];
  string email = 3 [(field_description) = "Email address"];
}
```

```java
// Доступ к опциям через reflection
Descriptors.Descriptor descriptor = UserProto.User.getDescriptor();

// Опции сообщения
MessageOptions options = descriptor.getOptions();
String tableName = options.getExtension(UserProto.tableName);

// Опции полей
for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
    FieldOptions fieldOptions = field.getOptions();

    if (fieldOptions.hasExtension(UserProto.fieldDescription)) {
        String description = fieldOptions.getExtension(UserProto.fieldDescription);
        System.out.println(field.getName() + ": " + description);
    }

    if (fieldOptions.getExtension(UserProto.sensitive)) {
        // Обработка sensitive поля
    }
}
```

## Validation

### Custom Validators
```java
public class ProtobufValidator {

    public ValidationResult validate(UserProto.User user) {
        ValidationResult result = new ValidationResult();

        if (user.getName().isEmpty()) {
            result.addError("name", "Name is required");
        }

        if (user.getEmail().isEmpty()) {
            result.addError("email", "Email is required");
        } else if (!isValidEmail(user.getEmail())) {
            result.addError("email", "Invalid email format");
        }

        if (user.getAge() < 0 || user.getAge() > 150) {
            result.addError("age", "Age must be between 0 and 150");
        }

        return result;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
```

### Schema Validation
```java
public class SchemaValidator {

    public void validateSchema(Class<? extends Message> messageClass) {
        Descriptors.Descriptor descriptor = getDescriptor(messageClass);

        // Проверка required полей
        for (Descriptors.FieldDescriptor field : descriptor.getFields()) {
            if (field.isRequired() && !field.hasDefaultValue()) {
                throw new ValidationException("Required field missing: " + field.getName());
            }
        }

        // Проверка constraints
        validateFieldConstraints(descriptor);
    }

    private Descriptors.Descriptor getDescriptor(Class<? extends Message> clazz) {
        try {
            Method method = clazz.getMethod("getDescriptor");
            return (Descriptors.Descriptor) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get descriptor", e);
        }
    }
}
```

## Spring Boot Integration

### Configuration
```java
@Configuration
public class ProtobufConfig {

    @Bean
    public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }

    @Bean
    public RestTemplate restTemplate(ProtobufHttpMessageConverter converter) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }
}
```

### gRPC Service
```java
@Service
public class GrpcUserService {

    @Autowired
    private UserRepository userRepository;

    public User getUser(int userId) {
        UserEntity entity = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

        return User.newBuilder()
            .setId(entity.getId())
            .setName(entity.getName())
            .setEmail(entity.getEmail())
            .build();
    }

    public User createUser(User user) {
        UserEntity entity = new UserEntity();
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());

        UserEntity saved = userRepository.save(entity);

        return User.newBuilder(saved).build();
    }
}
```

### Web MVC Controller
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private GrpcUserService userService;

    @GetMapping(value = "/{id}", produces = "application/x-protobuf")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PostMapping(consumes = "application/x-protobuf", produces = "application/x-protobuf")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
```

## Testing

### Unit Testing
```java
public class ProtobufTest {

    @Test
    public void testUserSerialization() {
        // Создание тестового объекта
        UserProto.User user = UserProto.User.newBuilder()
            .setId(1)
            .setName("John Doe")
            .setEmail("john@example.com")
            .build();

        // Сериализация
        byte[] data = user.toByteArray();

        // Десериализация
        UserProto.User deserialized = UserProto.User.parseFrom(data);

        // Проверка
        assertEquals(user.getId(), deserialized.getId());
        assertEquals(user.getName(), deserialized.getName());
        assertEquals(user.getEmail(), deserialized.getEmail());
    }

    @Test
    public void testJsonConversion() throws Exception {
        UserProto.User user = UserProto.User.newBuilder()
            .setName("John")
            .setEmail("john@example.com")
            .build();

        JsonFormat.Printer printer = JsonFormat.printer();
        String json = printer.print(user);

        // Проверка JSON структуры
        assertTrue(json.contains("\"name\":\"John\""));
        assertTrue(json.contains("\"email\":\"john@example.com\""));

        // Обратная конвертация
        JsonFormat.Parser parser = JsonFormat.parser();
        UserProto.User.Builder builder = UserProto.User.newBuilder();
        parser.merge(json, builder);
        UserProto.User fromJson = builder.build();

        assertEquals(user.getName(), fromJson.getName());
    }
}
```

### gRPC Testing
```java
@SpringBootTest
@DirtiesContext
public class GrpcServiceTest {

    @Autowired
    private GrpcServerTestFixture grpcServerTestFixture;

    private UserServiceGrpc.UserServiceBlockingStub stub;

    @BeforeEach
    void setUp() {
        stub = UserServiceGrpc.newBlockingStub(grpcServerTestFixture.getChannel());
    }

    @Test
    void testGetUser() {
        GetUserRequest request = GetUserRequest.newBuilder()
            .setUserId(1)
            .build();

        User response = stub.getUser(request);

        assertEquals(1, response.getId());
        assertEquals("Test User", response.getName());
    }
}
```

## Performance Comparison

### Protobuf vs JSON vs XML
```java
public class SerializationBenchmark {

    private static final int ITERATIONS = 10000;
    private final Gson gson = new Gson();

    @Benchmark
    public void protobufSerialization() {
        UserProto.User user = createTestUser();

        for (int i = 0; i < ITERATIONS; i++) {
            byte[] data = user.toByteArray();
            // Use data
        }
    }

    @Benchmark
    public void jsonSerialization() {
        User user = createTestUserPojo();

        for (int i = 0; i < ITERATIONS; i++) {
            String json = gson.toJson(user);
            // Use json
        }
    }

    private UserProto.User createTestUser() {
        return UserProto.User.newBuilder()
            .setId(1)
            .setName("John Doe")
            .setEmail("john@example.com")
            .build();
    }

    private User createTestUserPojo() {
        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        return user;
    }
}
```

## Migration и Best Practices

### Migration from JSON
```java
// Преобразование JSON в Protobuf
public UserProto.User convertFromJson(String json) throws Exception {
    JsonFormat.Parser parser = JsonFormat.parser();
    UserProto.User.Builder builder = UserProto.User.newBuilder();
    parser.merge(json, builder);
    return builder.build();
}

// Преобразование Protobuf в JSON
public String convertToJson(UserProto.User user) throws Exception {
    JsonFormat.Printer printer = JsonFormat.printer()
        .includingDefaultValueFields()
        .preservingProtoFieldNames();
    return printer.print(user);
}
```

### Best Practices
```protobuf
// Рекомендации по schema design
syntax = "proto3";

package com.company.product.v1;

option java_package = "com.company.product.protobuf";
option java_outer_classname = "ProductProto";

// Использовать осмысленные имена
message Product {
  // ID всегда первым полем
  int64 id = 1;

  // Обязательные поля
  string name = 2;
  string sku = 3;

  // Опциональные поля
  optional string description = 4;
  repeated string tags = 5;

  // Вложенные сообщения
  ProductDetails details = 6;
  repeated Price prices = 7;

  // Enum с 0 значением как default
  Status status = 8;

  enum Status {
    ACTIVE = 0;
    INACTIVE = 1;
    DISCONTINUED = 2;
  }

  message ProductDetails {
    string manufacturer = 1;
    string model = 2;
    Dimensions dimensions = 3;
  }

  message Dimensions {
    double length = 1;
    double width = 2;
    double height = 3;
    string unit = 4;
  }

  message Price {
    string currency = 1;
    double amount = 2;
    string region = 3;
  }
}
```

## Advanced Features

### Dynamic Messages
```java
// Работа с динамическими сообщениями
Descriptors.Descriptor descriptor = UserProto.User.getDescriptor();

// Создание динамического сообщения
DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
builder.setField(descriptor.findFieldByName("name"), "John");
builder.setField(descriptor.findFieldByName("email"), "john@example.com");

DynamicMessage message = builder.build();

// Сериализация
byte[] data = message.toByteArray();

// Парсинг обратно
DynamicMessage parsed = DynamicMessage.parseFrom(descriptor, data);
```

### Custom Code Generation
```java
// Кастомный кодогенератор
public class CustomProtobufGenerator extends Generator {

    @Override
    public List<GeneratedFile> generate(Descriptors.FileDescriptor fileDescriptor,
                                      String parameter,
                                      GeneratorContext context) {
        List<GeneratedFile> files = new ArrayList<>();

        for (Descriptors.Descriptor messageType : fileDescriptor.getMessageTypes()) {
            String className = messageType.getName() + "Validator";
            String content = generateValidatorClass(messageType);
            files.add(GeneratedFile.newBuilder()
                .setName(className + ".java")
                .setContent(content)
                .build());
        }

        return files;
    }

    private String generateValidatorClass(Descriptors.Descriptor descriptor) {
        // Генерация кода валидатора
        StringBuilder sb = new StringBuilder();
        sb.append("public class ").append(descriptor.getName()).append("Validator {\n");
        // ... генерация методов валидации
        sb.append("}\n");
        return sb.toString();
    }
}
```

## Troubleshooting

### Common Issues
```java
public class ProtobufTroubleshooting {

    // Проблема: InvalidProtocolBufferException
    public User safeParse(byte[] data) {
        try {
            return User.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            // Логировать ошибку
            return User.getDefaultInstance(); // Возвращать default
        }
    }

    // Проблема: Required fields
    public boolean isValid(User user) {
        return !user.getName().isEmpty() &&
               !user.getEmail().isEmpty() &&
               user.getId() > 0;
    }

    // Проблема: Version compatibility
    public User migrateFromOldVersion(byte[] oldData) {
        try {
            return User.parseFrom(oldData);
        } catch (InvalidProtocolBufferException e) {
            // Попытка миграции из старой версии
            return migrateUserData(oldData);
        }
    }

    private User migrateUserData(byte[] oldData) {
        // Логика миграции данных
        return User.newBuilder()
            .setName("Unknown")  // Default values
            .setEmail("")
            .build();
    }
}
```

### Debugging
```java
public class ProtobufDebugger {

    // Логирование сериализации
    public byte[] serializeWithLogging(User user) {
        System.out.println("Serializing User:");
        System.out.println("  ID: " + user.getId());
        System.out.println("  Name: " + user.getName());
        System.out.println("  Email: " + user.getEmail());

        byte[] data = user.toByteArray();
        System.out.println("  Serialized size: " + data.length + " bytes");

        return data;
    }

    // Логирование десериализации
    public User deserializeWithLogging(byte[] data) throws InvalidProtocolBufferException {
        System.out.println("Deserializing " + data.length + " bytes");

        User user = User.parseFrom(data);

        System.out.println("Deserialized User:");
        System.out.println("  ID: " + user.getId());
        System.out.println("  Name: " + user.getName());
        System.out.println("  Email: " + user.getEmail());

        return user;
    }

    // JSON представление для отладки
    public String toJson(User user) throws InvalidProtocolBufferException {
        JsonFormat.Printer printer = JsonFormat.printer()
            .includingDefaultValueFields()
            .preservingProtoFieldNames();

        return printer.print(user);
    }
}
```

## Experimental Features

### Proto4 (предварительная версия)
```protobuf
// Proto4 синтаксис (экспериментальный)
edition = "2023";

package com.example;

message User {
  // Новые возможности proto4
  field int32 id = 1 [features.field_presence = EXPLICIT];
  field string name = 2;
  field optional string email = 3;  // Явно optional
}
```

### Zero-copy сериализация
```java
// Экспериментальная zero-copy сериализация
public class ZeroCopySerializer {

    public void serializeZeroCopy(User user, WritableByteChannel channel) throws IOException {
        // Прямая запись в канал без копирования
        CodedOutputStream output = CodedOutputStream.newInstance(channel);
        user.writeTo(output);
        output.flush();
    }

    public User deserializeZeroCopy(ReadableByteChannel channel) throws IOException {
        // Прямое чтение из канала
        CodedInputStream input = CodedInputStream.newInstance(channel);
        return User.parseFrom(input);
    }
}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация Protocol Buffers](https://developers.google.com/protocol-buffers)
- [Protocol Buffers Guide](https://developers.google.com/protocol-buffers/docs/overview)
- [gRPC Documentation](https://grpc.io/docs/)
- [Proto3 Language Guide](https://developers.google.com/protocol-buffers/docs/proto3)

## См. также
- [gRPC Java](libraries.md) - gRPC для Java
- [Gson](java-gson.md) - JSON сериализация
- [Jackson](jackson.md) - JSON обработка
