---
title: "Vavr"
description: "Vavr (ранее известная как Javaslang) - это функциональная библиотека для Java 8+, предоставляющая неизменяемые структуры данных и функциональное API. Делает Java более функциональным языком."
tags:
  - libraries
  - java
  - java-vavr
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Vavr

**Vavr** (ранее известная как Javaslang) — это функциональная библиотека для **Java** 8+, предоставляющая неизменяемые структуры данных и функциональное **API**. Делает **Java** более функциональным языком.

## Полезные ссылки

### Официальная документация
- [Vavr Documentation](https://docs.vavr.io/) — официальная документация
- [Vavr GitHub](https://github.com/vavr-io/vavr) — репозиторий проекта
- [Vavr User Guide](https://docs.vavr.io/) — руководство пользователя

### См. также
- [[java-streams-fp|Java Streams и FP]] — **Java Streams** и функциональное программирование
- [[strategy|Strategy]] — функциональные паттерны

## Содержание

- [Основные возможности](#основные-возможности)
  - [Option (замена Optional)](#option-замена-optional)
  - [Try (обработка исключений)](#try-обработка-исключений)
  - [Either (обработка ошибок с типами)](#either-обработка-ошибок-с-типами)
  - [Validation (валидация с накоплением ошибок)](#validation-валидация-с-накоплением-ошибок)
- [Неизменяемые коллекции](#неизменяемые-коллекции)
  - [List](#list)
  - [Set](#set)
  - [Map](#map)
  - [Queue и Stack](#queue-и-stack)
- [Pattern Matching](#pattern-matching)
  - [Match API](#match-api)
  - [When pattern](#when-pattern)
- [Tuple](#tuple)
  - [Создание и использование](#создание-и-использование)
- [Lazy evaluation](#lazy-evaluation)
  - [Lazy](#lazy)
- [Function composition](#function-composition)
  - [Function](#function)
  - [Memoization](#memoization)
- [Property и Lens](#property-и-lens)
  - [Property](#property)
  - [Lens](#lens)
- [Concurrent programming](#concurrent-programming)
  - [Future](#future)
  - [Promise](#promise)
- [JSON processing](#json-processing)
  - [Vavr Jackson module](#vavr-jackson-module)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration](#configuration)
  - [Service Layer с Vavr](#service-layer-с-vavr)
  - [Controller с Vavr](#controller-с-vavr)
- [Testing](#testing)
  - [Unit Testing с Vavr](#unit-testing-с-vavr)
  - [Property-based Testing](#property-based-testing)
- [Performance Considerations](#performance-considerations)
  - [Memory usage](#memory-usage)
  - [Lazy evaluation benefits](#lazy-evaluation-benefits)
- [Migration Guide](#migration-guide)
  - [From Java Optional to Vavr Option](#from-java-optional-to-vavr-option)
  - [From Java Stream to Vavr Collections](#from-java-stream-to-vavr-collections)
  - [From try-catch to Try](#from-try-catch-to-try)
- [Лучшие практики](#лучшие-практики)
  - [When to use Vavr](#when-to-use-vavr)
  - [Error Handling Patterns](#error-handling-patterns)
- [Advanced Features](#advanced-features)
  - [Type classes и Higher-kinded types](#type-classes-и-higher-kinded-types)
  - [Trampoline для stack-safe recursion](#trampoline-для-stack-safe-recursion)
- [Experimental Features](#experimental-features)
  - [Vavr 1.0 Features (Future)](#vavr-10-features-future)
- [Решение проблем](#решение-проблем)
  - [Common Issues](#common-issues)
  - [Debugging](#debugging)
- [См. также](#см-также-1)

## Основные возможности

### Option (замена Optional)

Создание **Option**, проверка наличия значения и функциональные операции (map, flatMap).

```java
import io.vavr.control.Option;

// Создание Option
Option<String> some = Option.of("value");
Option<String> none = Option.none();
Option<String> nullable = Option.of(null); // будет None

// Проверка наличия значения
if (some.isDefined()) {
    System.out.println(some.get()); // "value"
}

if (none.isEmpty()) {
    System.out.println("No value");
}

// Безопасный доступ
String result = some.getOrElse("default");
String result2 = none.getOrElse("default"); // "default"

// Функциональные операции
Option<String> upper = some.map(String::toUpperCase); // Some("VALUE")
Option<String> flatMapped = some.flatMap(s -> Option.of(s + "!")); // Some("value!")
```

### Try (обработка исключений)
```java
import io.vavr.control.Try;

// Безопасное выполнение кода, который может бросить исключение
Try<Integer> divide(int a, int b) {
    return Try.of(() -> a / b);
}

// Использование
Try<Integer> result = divide(10, 2);
if (result.isSuccess()) {
    System.out.println("Result: " + result.get());
}

Try<Integer> failure = divide(10, 0);
if (failure.isFailure()) {
    System.out.println("Error: " + failure.getCause().getMessage());
}

// Функциональная обработка
Try<Integer> mapped = result.map(x -> x * 2);
Try<String> recovered = failure.recover(ArithmeticException.class, ex -> "Division by zero");

// Chain operations
Try<String> chained = Try.of(() -> readFile("data.txt"))
    .map(String::toUpperCase)
    .filter(s -> s.length() > 10)
    .getOrElse("Default content");
```

### Either (обработка ошибок с типами)
```java
import io.vavr.control.Either;

// Either представляет значение типа Left (ошибка) или Right (успех)
Either<String, Integer> divide(int a, int b) {
    if (b == 0) {
        return Either.left("Division by zero");
    }
    return Either.right(a / b);
}

// Использование
Either<String, Integer> result = divide(10, 2);
if (result.isRight()) {
    System.out.println("Result: " + result.get());
}

Either<String, Integer> error = divide(10, 0);
if (error.isLeft()) {
    System.out.println("Error: " + error.getLeft());
}

// Функциональные операции
Either<String, Integer> doubled = result.map(x -> x * 2);
Either<String, String> mappedError = error.mapLeft(err -> "Error: " + err);
```

### Validation (валидация с накоплением ошибок)
```java
import io.vavr.control.Validation;

// Validation позволяет накапливать несколько ошибок
Validation<List<String>, User> validateUser(String name, String email, int age) {
    return Validation.combine(
        validateName(name),
        validateEmail(email),
        validateAge(age)
    ).ap(User::new);
}

Validation<String, String> validateName(String name) {
    return name != null && !name.trim().isEmpty()
        ? Validation.valid(name.trim())
        : Validation.invalid("Name is required");
}

Validation<String, String> validateEmail(String email) {
    return email != null && email.contains("@")
        ? Validation.valid(email.toLowerCase())
        : Validation.invalid("Invalid email format");
}

Validation<String, Integer> validateAge(int age) {
    return age >= 18 && age <= 120
        ? Validation.valid(age)
        : Validation.invalid("Age must be between 18 and 120");
}

// Использование
Validation<List<String>, User> validation = validateUser("John", "john@", 15);

if (validation.isValid()) {
    User user = validation.get();
    System.out.println("User created: " + user);
} else {
    List<String> errors = validation.getError();
    errors.forEach(System.out::println);
    // Output:
    // Invalid email format
    // Age must be between 18 and 120
}
```

## Неизменяемые коллекции

### List
```java
import io.vavr.collection.List;

/
 * Демонстрация работы с Vavr List - неизменяемым функциональным списком
 * Vavr List предоставляет функциональный API для работы со списками
 */

// Создание списков - различные способы создания Vavr List
List<String> empty = List.empty();  // Создание пустого списка
List<String> list = List.of("Java", "Scala", "Kotlin");  // Создание списка из элементов
List<String> fromIterable = List.ofAll(Arrays.asList(1, 2, 3));  // Создание из Java Iterable (автоматическое преобразование типов)

// Основные операции - все операции возвращают новый список (immutability)
List<String> appended = list.append("Groovy");        // Добавление элемента в конец списка (создает новый список)
List<String> prepended = list.prepend("Clojure");     // Добавление элемента в начало списка (создает новый список)
List<String> combined = list.appendAll(List.of("Haskell", "F#"));  // Добавление всех элементов другого списка в конец

// Функциональные операции - высокоуровневые операции преобразования
List<String> uppercased = list.map(String::toUpperCase);  // Преобразование каждого элемента (map функция)
List<String> filtered = list.filter(s -> s.length() > 4);  // Фильтрация элементов по условию (только элементы длиной > 4)
List<String> flatMapped = list.flatMap(s -> List.of(s, s.toLowerCase()));  // FlatMap - преобразование с "разворачиванием" результата

// Агрегация - операции для получения сводной информации о списке
int totalLength = list.map(String::length).sum().intValue();  // Сумма длин всех строк в списке
Optional<String> longest = list.maxBy(String::length);        // Поиск элемента с максимальной длиной
boolean allLong = list.forAll(s -> s.length() > 3);           // Проверка что все элементы удовлетворяют условию (длина > 3)

// Группировка и разбиение - операции для организации данных
Map<Integer, List<String>> byLength = list.groupBy(String::length);  // Группировка элементов по длине строки
Tuple2<List<String>, List<String>> partitioned = list.partition(s -> s.contains("a"));  // Разбиение на два списка: с "a" и без "a"
```

### Set
```java
import io.vavr.collection.Set;

/
 * Демонстрация работы с Vavr Set - неизменяемым функциональным множеством
 * Set автоматически гарантирует уникальность элементов
 */

// Создание множеств - различные способы создания Vavr Set
Set<String> set = HashSet.of("Java", "Scala", "Kotlin");  // Создание множества из элементов (дубликаты автоматически удаляются)
Set<String> fromJava = HashSet.ofAll(javaSet);             // Создание из Java Set (конвертация в Vavr Set)

// Операции множеств - все операции возвращают новое множество (immutability)
Set<String> added = set.add("Groovy");                      // Добавление элемента (создает новое множество)
Set<String> removed = set.remove("Java");                  // Удаление элемента (создает новое множество)
Set<String> union = set.union(HashSet.of("Haskell", "F#"));  // Объединение множеств (все элементы из обоих множеств)
Set<String> intersection = set.intersect(HashSet.of("Java", "Python", "Scala"));  // Пересечение множеств (только общие элементы)
Set<String> difference = set.diff(HashSet.of("Java", "C++"));  // Разность множеств (элементы из первого множества, которых нет во втором)

// Функциональные операции аналогичны List - те же операции преобразования
Set<String> mapped = set.map(String::toUpperCase);        // Преобразование каждого элемента в верхний регистр
Set<String> filtered = set.filter(s -> s.startsWith("S")); // Фильтрация элементов начинающихся с "S"
```

### Map
```java
import io.vavr.collection.Map;

/
 * Демонстрация работы с Vavr Map - неизменяемой функциональной картой (ключ-значение)
 * Map предоставляет функциональный API для работы с парами ключ-значение
 */

// Создание карт - различные способы создания Vavr Map
Map<String, Integer> map = HashMap.of("Java", 1995, "Scala", 2003, "Kotlin", 2011);  // Создание карты из пар ключ-значение
Map<String, Integer> fromJava = HashMap.ofAll(javaMap);  // Создание из Java Map (конвертация в Vavr Map)

// Основные операции - все операции возвращают новую карту (immutability)
Map<String, Integer> updated = map.put("Groovy", 2003);  // Добавление/обновление пары ключ-значение (создает новую карту)
Option<Integer> year = map.get("Java");                  // Получение значения по ключу (возвращает Option для безопасной работы с null)
Integer orDefault = map.getOrElse("Python", 1991);      // Получение значения или значения по умолчанию если ключ не найден

// Функциональные операции - высокоуровневые операции преобразования карты
Map<String, String> mapped = map.map((key, value) -> Tuple.of(key.toUpperCase(), value + " year"));  // Преобразование ключей и значений
Map<String, Integer> filtered = map.filter((key, value) -> value > 2000);  // Фильтрация по условию (только значения > 2000)
Map<String, Integer> flatMapped = map.flatMap((key, value) ->  // FlatMap - преобразование с возможностью удаления элементов
    value > 2000 ? HashMap.of(key + "_new", value) : HashMap.empty()  // Если значение > 2000, создаем новую пару, иначе пустая карта
);
```

### Queue и Stack
```java
import io.vavr.collection.Queue;
import io.vavr.collection.Stack;

// Queue (FIFO)
Queue<String> queue = Queue.of("first", "second", "third");
Tuple2<String, Queue<String>> dequeued = queue.dequeue(); // ("first", Queue("second", "third"))
Queue<String> enqueued = queue.enqueue("fourth");

// Stack (LIFO)
Stack<String> stack = Stack.of("bottom", "middle", "top");
Tuple2<String, Stack<String>> popped = stack.pop(); // ("top", Stack("bottom", "middle"))
Stack<String> pushed = stack.push("new_top");
```

## Pattern Matching

### Match API
```java
import static io.vavr.API.*;
import static io.vavr.Predicates.*;

// Простое pattern matching
String result = Match(option).of(
    Case($Some($()), "Found value"),
    Case($None(), "No value")
);

// С values
Integer value = 42;
String type = Match(value).of(
    Case($(is(0)), "zero"),
    Case($(isGreaterThan(0)), "positive"),
    Case($(), "negative")
);

// С коллекциями
List<Integer> numbers = List.of(1, 2, 3, 4, 5);
String description = Match(numbers).of(
    Case($Cons($(1), $()), "Starts with 1"),
    Case($Nil(), "Empty list"),
    Case($(), "Other list")
);

// Комплексный пример
Either<String, Integer> either = Either.right(42);
String result2 = Match(either).of(
    Case($Left($()), error -> "Error: " + error),
    Case($Right($(isGreaterThan(50))), value -> "Big number: " + value),
    Case($Right($()), value -> "Small number: " + value)
);
```

### When pattern
```java
// Условное выполнение
Option<String> maybeValue = Option.of("test");

String result = when(maybeValue.isDefined() && maybeValue.get().length() > 3)
    .then(() -> "Long value: " + maybeValue.get())
    .otherwise(() -> "Default value");

// С несколькими условиями
int number = 15;
String category = when(number % 15 == 0)
    .then("FizzBuzz")
    .when(number % 3 == 0)
    .then("Fizz")
    .when(number % 5 == 0)
    .then("Buzz")
    .otherwise(() -> String.valueOf(number));
```

## Tuple

### Создание и использование
```java
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;

// Создание кортежей
Tuple2<String, Integer> person = Tuple.of("John", 30);
Tuple3<String, String, Integer> address = Tuple.of("John", "New York", 10001);

// Доступ к элементам
String name = person._1;
Integer age = person._2;

// Разложение
Tuple2<String, Integer> tuple = Tuple.of("test", 123);
String s = tuple._1;
Integer i = tuple._2;

// Map операции
Tuple2<String, Integer> mapped = person.map(String::toUpperCase, x -> x + 1);
// ("JOHN", 31)

// Transform
String combined = person.transform((n, a) -> n + " is " + a + " years old");
// "John is 30 years old"
```

## Lazy evaluation

### Lazy
```java
import io.vavr.Lazy;

// Создание lazy значения
Lazy<Double> lazyPi = Lazy.of(() -> {
    System.out.println("Computing PI...");
    return Math.PI;
});

// Значение вычисляется только при первом доступе
System.out.println("Before access");
double pi = lazyPi.get(); // Здесь происходит вычисление
System.out.println("PI: " + pi);

// Повторные вызовы возвращают кешированное значение
double pi2 = lazyPi.get(); // Кеш используется

// Проверка вычислено ли значение
boolean computed = lazyPi.isEvaluated();

// Map и flatMap
Lazy<String> lazyString = lazyPi.map(d -> "PI is " + d);
Lazy<Integer> lazyLength = lazyString.map(String::length);
```

## Function composition

### Function
```java
import io.vavr.Function1;
import io.vavr.Function2;

// Создание функций
Function1<String, String> toUpper = String::toUpperCase;
Function1<String, Integer> length = String::length;
Function1<Integer, String> toString = Object::toString;

// Композиция
Function1<String, String> composed = toUpper.andThen(length).andThen(toString);
// toUpper -> length -> toString

String result = composed.apply("hello"); // "5"

// Частичное применение
Function2<Integer, Integer, Integer> add = (a, b) -> a + b;
Function1<Integer, Integer> add5 = add.apply(5); // Частичное применение первого аргумента

int result2 = add5.apply(3); // 8

// Currying
Function1<Integer, Function1<Integer, Integer>> curriedAdd = add.curried();
Function1<Integer, Integer> add10 = curriedAdd.apply(10);
int result3 = add10.apply(5); // 15
```

### Memoization
```java
// Мемоизация для оптимизации
Function1<Integer, Integer> expensiveFunction = n -> {
    System.out.println("Computing for " + n);
    return n * n; // Дорогая операция
};

Function1<Integer, Integer> memoized = Function1.of(expensiveFunction).memoized();

System.out.println(memoized.apply(5)); // Вычисление
System.out.println(memoized.apply(5)); // Из кеша
System.out.println(memoized.apply(6)); // Вычисление
```

## Property и Lens

### Property
```java
// Property для безопасного доступа к полям
Property<User, String> nameProp = Property.of(User::getName, User::setName);

// Получение значения
User user = new User("John", "Doe");
String name = nameProp.get(user);

// Установка значения
User updated = nameProp.set(user, "Jane");

// Существование свойства
boolean exists = nameProp.exists(user, "John"::equals);
```

### Lens
```java
// Lens для глубокого обновления неизменяемых структур
Lens<User, Address> addressLens = Lens.of(User::getAddress, User::withAddress);
Lens<Address, String> cityLens = Lens.of(Address::getCity, Address::withCity);

// Композиция lenses
Lens<User, String> cityLens = addressLens.andThen(cityLens);

// Обновление вложенного поля
User updated = cityLens.set(user, "New York");

// Получение вложенного поля
String city = cityLens.get(user);
```

## Concurrent programming

### Future
```java
import io.vavr.concurrent.Future;

// Создание Future
Future<String> future = Future.of(() -> {
    Thread.sleep(1000);
    return "Hello from future!";
});

// Блокирующее получение
String result = future.get();

// Асинхронная обработка
future.onSuccess(s -> System.out.println("Success: " + s))
      .onFailure(t -> System.err.println("Error: " + t.getMessage()));

// Функциональные операции
Future<String> mapped = future.map(String::toUpperCase);
Future<Integer> flatMapped = future.flatMap(s -> Future.of(() -> s.length()));

// Combinators
Future<String> future1 = Future.of(() -> "Hello");
Future<String> future2 = Future.of(() -> "World");

Future<String> combined = future1.zip(future2, (a, b) -> a + " " + b);
```

### Promise
```java
import io.vavr.concurrent.Promise;

// Создание Promise
Promise<String> promise = Promise.make();

// Выполнение в другом потоке
new Thread(() -> {
    try {
        Thread.sleep(1000);
        promise.success("Completed!");
    } catch (Exception e) {
        promise.failure(e);
    }
}).start();

// Получение Future
Future<String> future = promise.future();

// Ожидание результата
String result = future.get();
```

## JSON processing

### Vavr Jackson module
```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr-jackson</artifactId>
    <version>0.10.4</version>
</dependency>
```

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.jackson.datatype.VavrModule;

// Регистрация модуля
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new VavrModule());

// Теперь Jackson может работать с Vavr типами
List<String> list = List.of("a", "b", "c");
String json = mapper.writeValueAsString(list); // ["a","b","c"]

List<String> deserialized = mapper.readValue(json,
    new TypeReference<List<String>>() {});
```

## Spring Boot Integration

### Configuration
```java
@Configuration
public class VavrConfig {

    // Преобразование Java Optional в Vavr Option
    @Bean
    public Converter<Optional<?>, Option<?>> optionalToOptionConverter() {
        return new Converter<Optional<?>, Option<?>>() {
            @Override
            public Option<?> convert(Optional<?> source) {
                return Option.ofOptional(source);
            }
        };
    }

    // Преобразование Vavr Option в Java Optional
    @Bean
    public Converter<Option<?>, Optional<?>> optionToOptionalConverter() {
        return new Converter<Option<?>, Optional<?>>() {
            @Override
            public Optional<?> convert(Option<?> source) {
                return source.toJavaOptional();
            }
        };
    }
}
```

### Service Layer с Vavr
```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Try<User> findUserById(Long id) {
        return Try.of(() -> {
            UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
            return convertToUser(entity);
        });
    }

    public Validation<List<String>, User> createUser(CreateUserRequest request) {
        return Validation.combine(
            validateName(request.getName()),
            validateEmail(request.getEmail()),
            validateAge(request.getAge())
        ).ap(User::new);
    }

    public Either<String, List<User>> findUsersByStatus(UserStatus status) {
        try {
            List<User> users = userRepository.findByStatus(status).stream()
                .map(this::convertToUser)
                .collect(Collectors.toList());
            return Either.right(users);
        } catch (Exception e) {
            return Either.left("Failed to fetch users: " + e.getMessage());
        }
    }

    private Validation<String, String> validateName(String name) {
        return name != null && !name.trim().isEmpty()
            ? Validation.valid(name.trim())
            : Validation.invalid("Name is required");
    }

    private Validation<String, String> validateEmail(String email) {
        return email != null && email.contains("@")
            ? Validation.valid(email)
            : Validation.invalid("Invalid email");
    }

    private Validation<String, Integer> validateAge(Integer age) {
        return age != null && age >= 18 && age <= 120
            ? Validation.valid(age)
            : Validation.invalid("Age must be between 18 and 120");
    }
}
```

### Controller с Vavr
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.findUserById(id)
            .map(user -> ResponseEntity.ok(user))
            .getOrElse(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request)
            .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user))
            .mapError(errors -> ResponseEntity.badRequest().body(Map.of("errors", errors)))
            .fold(Function.identity(), Function.identity());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getUsersByStatus(@PathVariable UserStatus status) {
        return userService.findUsersByStatus(status)
            .fold(
                error -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", error)),
                users -> ResponseEntity.ok(users)
            );
    }
}
```

## Testing

### Unit Testing с Vavr
```java
public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void testFindUserById_Success() {
        // Given
        Long userId = 1L;
        UserEntity entity = new UserEntity(userId, "John", "john@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(entity));

        // When
        Try<User> result = userService.findUserById(userId);

        // Then
        assertTrue(result.isSuccess());
        User user = result.get();
        assertEquals(userId, user.getId());
        assertEquals("John", user.getName());
    }

    @Test
    void testFindUserById_NotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Try<User> result = userService.findUserById(userId);

        // Then
        assertTrue(result.isFailure());
        assertTrue(result.getCause() instanceof UserNotFoundException);
    }

    @Test
    void testCreateUser_ValidData() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John", "john@example.com", 30);

        // When
        Validation<List<String>, User> result = userService.createUser(request);

        // Then
        assertTrue(result.isValid());
        User user = result.get();
        assertEquals("John", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals(30, user.getAge());
    }

    @Test
    void testCreateUser_InvalidData() {
        // Given
        CreateUserRequest request = new CreateUserRequest("", "invalid-email", 150);

        // When
        Validation<List<String>, User> result = userService.createUser(request);

        // Then
        assertTrue(result.isInvalid());
        List<String> errors = result.getError();
        assertTrue(errors.contains("Name is required"));
        assertTrue(errors.contains("Invalid email"));
        assertTrue(errors.contains("Age must be between 18 and 120"));
    }
}
```

### Property-based Testing
```java
@Property
public void optionLaws(@ForAll @IntRange(min = 0, max = 100) int value) {
    Option<Integer> option = Option.of(value);

    // Identity law: option.flatMap(x -> Option.of(x)) == option
    assertEquals(option, option.flatMap(x -> Option.of(x)));

    // Associativity law
    Function1<Integer, Option<Integer>> f = x -> Option.of(x * 2);
    Function1<Integer, Option<String>> g = x -> Option.of(String.valueOf(x));

    Option<String> left = option.flatMap(f).flatMap(g);
    Option<String> right = option.flatMap(x -> f.apply(x).flatMap(g));

    assertEquals(left, right);
}
```

## Performance Considerations

### Memory usage
```java
// Vavr коллекции могут использовать больше памяти чем Java коллекции
// из-за неизменяемости и структурного шарринга

// Для больших коллекций рассмотрите использование mutable версий
// или конвертацию в Java коллекции для операций массовой обработки

public List<String> processLargeList(List<String> largeList) {
    // Конвертация в Java список для массовой обработки
    java.util.List<String> javaList = largeList.toJavaList();

    // Массовые операции
    javaList.replaceAll(String::toUpperCase);

    // Конвертация обратно
    return List.ofAll(javaList);
}
```

### Lazy evaluation benefits
```java
// Lazy evaluation может значительно улучшить производительность
public Lazy<List<String>> getExpensiveData() {
    return Lazy.of(() -> {
        // Дорогая операция, выполняется только при вызове get()
        return List.of(fetchFromDatabase(), fetchFromCache(), fetchFromFile());
    });
}

// Использование
Lazy<List<String>> data = getExpensiveData();

// Данные не загружаются до этого момента
if (someCondition) {
    List<String> actualData = data.get();
    // Теперь данные загружены
}
```

## Migration Guide

### From Java Optional to Vavr Option
```java
// Java Optional
Optional<String> optional = Optional.of("value");
String result = optional.orElse("default");
boolean present = optional.isPresent();

// Vavr Option
Option<String> option = Option.of("value");
String result = option.getOrElse("default");
boolean defined = option.isDefined();
boolean empty = option.isEmpty();
```

### From Java Stream to Vavr Collections
```java
// Java Stream
List<String> result = list.stream()
    .filter(s -> s.length() > 3)
    .map(String::toUpperCase)
    .collect(Collectors.toList());

// Vavr Collection
List<String> result = list
    .filter(s -> s.length() > 3)
    .map(String::toUpperCase);
```

### From try-catch to Try
```java
// Java try-catch
String result;
try {
    result = riskyOperation();
} catch (Exception e) {
    result = "default";
    log.error("Operation failed", e);
}

// Vavr Try
Try<String> tried = Try.of(this::riskyOperation);
String result = tried.getOrElse("default");
tried.onFailure(e -> log.error("Operation failed", e));
```

## Лучшие практики

### When to use Vavr
```java
public class VavrBestPractices {

    // Используйте Option вместо null checks
    public Option<String> findUserName(Long userId) {
        return Option.of(userRepository.findById(userId))
            .map(User::getName);
    }

    // Используйте Try для операций, которые могут fail
    public Try<User> createUser(CreateUserRequest request) {
        return Try.of(() -> {
            validateRequest(request);
            return userRepository.save(convertToEntity(request));
        });
    }

    // Используйте Validation для бизнес-валидации
    public Validation<List<String>, Order> validateOrder(OrderRequest request) {
        return Validation.combine(
            validateItems(request.getItems()),
            validateAddress(request.getAddress()),
            validatePayment(request.getPayment())
        ).ap(Order::new);
    }

    // Используйте Either для операций с двумя возможными исходами
    public Either<String, User> authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Either.left("User not found");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Either.left("Invalid password");
        }
        return Either.right(user);
    }

    // Используйте immutable collections для thread safety
    private final List<String> configValues = List.of("value1", "value2", "value3");

    public List<String> getConfigValues() {
        return configValues; // Thread-safe, immutable
    }
}
```

### Error Handling Patterns
```java
public class ErrorHandlingPatterns {

    // Railway oriented programming
    public Try<Result> processData(Data input) {
        return Try.of(() -> validate(input))
            .flatMap(this::enrich)
            .flatMap(this::transform)
            .flatMap(this::save);
    }

    // Recovery patterns
    public Try<String> readFileWithFallback(String path) {
        return Try.of(() -> readFile(path))
            .recover(FileNotFoundException.class, ex -> "Default content")
            .recover(IOException.class, ex -> {
                log.error("IO error", ex);
                return "Error content";
            });
    }

    // Validation accumulation
    public Validation<List<String>, ProcessedData> validateAndProcess(RawData data) {
        return Validation.combine(
            validateField1(data.getField1()),
            validateField2(data.getField2()),
            validateField3(data.getField3())
        ).ap((f1, f2, f3) -> new ProcessedData(f1, f2, f3));
    }
}
```

## Advanced Features

### Type classes и Higher-kinded types
```java
// Vavr предоставляет некоторые type class паттерны
public interface Functor<T, F extends Functor<?, ?>> {
    <U> F map(Function1<? super T, ? extends U> f);
}

// Пример использования с коллекциями
List<Integer> numbers = List.of(1, 2, 3);
List<String> strings = numbers.map(Object::toString);

// Monad pattern
public interface Monad<T, M extends Monad<?, ?>> extends Functor<T, M> {
    <U> M flatMap(Function1<? super T, ? extends M> f);
}
```

### Trampoline для stack-safe recursion
```java
import io.vavr.control.Trampoline;

// Stack-safe recursion с большими данными
public static Trampoline<Integer> sum(List<Integer> numbers) {
    return numbers.isEmpty()
        ? Trampoline.done(0)
        : Trampoline.more(() -> sum(numbers.tail()).map(s -> s + numbers.head()));
}

// Использование
List<Integer> largeList = List.range(1, 100000);
int result = sum(largeList).run();
```

## Experimental Features

### Vavr `1.0` Features (Future)
```java
// Предполагаемые будущие возможности
// (на основе текущих development планов)

// Более глубокая Kotlin интеграция
// Pattern matching improvements
// Новые коллекции (Vector, etc.)
// Лучшая Scala interoperability

// Пример возможного API
Future<String> future = Future.of(() -> "result");

// Новые операторы (предположительно)
Option<String> result = future.toOption();
```

## Решение проблем

### Common Issues
```java
public class VavrTroubleshooting {

    // Проблема: StackOverflowError при recursion
    // Решение: Использовать Trampoline
    public Trampoline<Integer> safeFactorial(int n) {
        return n <= 1
            ? Trampoline.done(1)
            : Trampoline.more(() -> safeFactorial(n - 1).map(res -> res * n));
    }

    // Проблема: Memory leaks с Lazy
    // Решение: Не хранить ссылки на Lazy в долгоживущих объектах
    public class MemorySafeExample {
        public Option<String> getData() {
            return Option.of(computeExpensiveData()); // Не Lazy.of()
        }
    }

    // Проблема: Performance с маленькими коллекциями
    // Решение: Использовать Java коллекции для < 10 элементов
    public List<String> optimizeCollection(List<String> input) {
        return input.size() < 10
            ? List.ofAll(input.toJavaList()) // Java list operations
            : input; // Vavr operations
    }
}
```

### Debugging
```java
public class VavrDebugger {

    // Логирование операций с Option
    public <T> Option<T> logOption(Option<T> option, String operation) {
        return option
            .onEmpty(() -> log.debug("{}: Empty", operation))
            .map(value -> {
                log.debug("{}: {}", operation, value);
                return value;
            });
    }

    // Логирование Try операций
    public <T> Try<T> logTry(Try<T> tried, String operation) {
        return tried
            .onSuccess(value -> log.debug("{} success: {}", operation, value))
            .onFailure(error -> log.error("{} failed: {}", operation, error.getMessage()));
    }

    // Pretty printing коллекций
    public <T> String prettyPrint(Collection<T> collection) {
        return collection.isEmpty()
            ? "[]"
            : collection.mkString("[", ", ", "]");
    }
}
```


## Полезные ссылки
- [Официальная документация Vavr](https://www.vavr.io/)
- [Vavr User Guide](https://www.vavr.io/vavr-docs/)
- [GitHub репозиторий](https://github.com/vavr-io/vavr)
- [Vavr Examples](https://github.com/vavr-io/vavr/tree/master/vavr-examples)

## См. также
- [[java-streams-fp|Java Streams]] — **Java** 8 **Streams**
- [[README|Паттерны]] — Функциональные паттерны
- [[scala-collections|Scala Collections]] — **Scala** коллекции

