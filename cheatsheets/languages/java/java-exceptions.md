---
title: "Java: обработка исключений"
description: "Комплексное руководство по обработке исключений в Java: типы исключений, best practices, паттерны и интеграция с современными фреймворками."
tags:
  - languages
  - java
  - java-exceptions
difficulty: "intermediate"
prerequisites: []
next:
  - java-memory-model
updated: "2026-04-20"
---
# Java: обработка исключений

Комплексное руководство по обработке исключений в Java: типы исключений, best practices, паттерны и интеграция с современными фреймворками.


### См. также
- [[java-exceptions-interview|Вопросы на собеседовании]] — подготовка к интервью

## Полезные ссылки

### Официальная документация
- [Java Exceptions](https://docs.oracle.com/javase/tutorial/essential/exceptions/) — официальный туториал
- [Throwable API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Throwable.html) — **API** исключений
- [Best Practices](https://www.oracle.com/java/technologies/javase/codeconventions-exceptions.html) — лучшие практики **Oracle**

### Дополнительные ресурсы
- [Baeldung Exceptions](https://www.baeldung.com/java-exceptions) — статьи по исключениям
- [Exception Handling Patterns](https://www.baeldung.com/java-exception-handling-best-practices) — паттерны обработки
- [Spring Exception Handling](https://www.baeldung.com/exception-handling-for-rest-with-spring) — **Spring MVC**

## Содержание

- [Введение в исключения](#введение-в-исключения)
  - [Почему исключения важны?](#почему-исключения-важны)
  - [Основные концепции](#основные-концепции)
- [Типы исключений](#типы-исключений)
  - [Иерархия исключений](#иерархия-исключений)
  - [Checked vs Unchecked исключения](#checked-vs-unchecked-исключения)
  - [Стандартные исключения](#стандартные-исключения)
    - [RuntimeException подклассы](#runtimeexception-подклассы)
    - [Checked исключения](#checked-исключения)
- [Обработка исключений](#обработка-исключений)
  - [Try-Catch-Finally блоки](#try-catch-finally-блоки)
  - [Multi-Catch и Union Types](#multi-catch-и-union-types)
  - [Перевыброс исключений](#перевыброс-исключений)
  - [Chain of exceptions](#chain-of-exceptions)
- [Создание исключений](#создание-исключений)
  - [Кастомные исключения](#кастомные-исключения)
  - [Exception Builder Pattern](#exception-builder-pattern)
- [Best practices](#best-practices)
  - [Когда использовать исключения](#когда-использовать-исключения)
  - [Обработка исключений](#обработка-исключений-1)
  - [Производительность](#производительность)
  - [Логирование исключений](#логирование-исключений)
- [Интеграция с фреймворками](#интеграция-с-фреймворками)
  - [Spring Framework](#spring-framework)
  - [JUnit Testing](#junit-testing)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Ключевые принципы](#ключевые-принципы)
  - [Best practices summary](#best-practices-summary)
  - [Интеграция с экосистемой](#интеграция-с-экосистемой)
- [См. также](#см-также)

## Введение в исключения

**Исключения (Exceptions)** — это механизм в **Java** для обработки ошибочных ситуаций, которые возникают во время выполнения программы. Вместо возврата кодов ошибок, **Java** использует исключения для сигнализации о проблемах.

### Почему исключения важны?

1. **Безопасность типов** — Компилятор проверяет обработку **checked** исключений
2. **Читаемость кода** — Логика обработки ошибок отделена от основного кода
3. **Отладка** — Исключения содержат **stack trace** для диагностики
4. **Восстановление** — Возможность **graceful degradation**
5. **Стандартизация** — Единый механизм обработки ошибок

### Основные концепции

**Базовая структура **try-catch-finally**:**

```java
// Базовая структура обработки исключений
try {
    // Код, который может выбросить исключение
    riskyOperation();
} catch (SpecificException e) {
    // Обработка конкретного типа исключения
    handleSpecificException(e);
} catch (GeneralException e) {
    // Обработка более общего типа
    handleGeneralException(e);
} finally {
    // Код, который выполнится всегда
    cleanup();
}
```

## Типы исключений

### Иерархия исключений

```java
// Все исключения наследуются от Throwable
public class Throwable implements Serializable {
    // ...
}

// Error - серьезные проблемы, от которых нельзя восстановиться
public class Error extends Throwable {
    // OutOfMemoryError, StackOverflowError, etc.
}

// Exception - проблемы, от которых можно восстановиться
public class Exception extends Throwable {
    // IOException, SQLException, etc.
}

// RuntimeException - непроверяемые исключения
public class RuntimeException extends Exception {
    // NullPointerException, IllegalArgumentException, etc.
}
```

### Checked vs Unchecked исключения

```java
// Checked исключения - должны быть обработаны или объявлены
public void readFile(String path) throws IOException {
    try (FileInputStream fis = new FileInputStream(path)) {
        // чтение файла
    }
    // IOException является checked - компилятор требует обработки
}

// Unchecked исключения - не требуют обязательной обработки
public void processData(String data) {
    if (data == null) {
        throw new IllegalArgumentException("Data cannot be null");
        // IllegalArgumentException extends RuntimeException - unchecked
    }
    // Обработка не требуется, но рекомендуется
}
```

### Стандартные исключения

#### RuntimeException подклассы

```java
// NullPointerException - доступ к null ссылке
public void nullPointerExample() {
    String str = null;
    str.length(); // NullPointerException
}

// IllegalArgumentException - некорректный аргумент
public void validateAge(int age) {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException("Age must be between 0 and 150");
    }
}

// IllegalStateException - некорректное состояние объекта
public class Connection {
    private boolean connected = false;

    public void sendData(String data) {
        if (!connected) {
            throw new IllegalStateException("Connection is not established");
        }
        // отправка данных
    }
}

// UnsupportedOperationException - неподдерживаемая операция
public class ReadOnlyList<T> extends ArrayList<T> {
    @Override
    public boolean add(T element) {
        throw new UnsupportedOperationException("List is read-only");
    }
}
```

#### Checked исключения

```java
// IOException и подклассы
public void fileOperations() throws IOException {
    // FileNotFoundException - файл не найден
    try (FileInputStream fis = new FileInputStream("nonexistent.txt")) {
        // ...
    }

    // EOFException - конец файла при чтении
    DataInputStream dis = new DataInputStream(fis);
    while (true) {
        try {
            dis.readInt(); // может выбросить EOFException
        } catch (EOFException e) {
            break; // конец файла
        }
    }
}

// SQLException - ошибки базы данных
public User findUserById(Long id) throws SQLException {
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {

        stmt.setLong(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new User(rs.getLong("id"), rs.getString("name"));
            }
            throw new SQLException("User not found with id: " + id);
        }
    }
}
```

## Обработка исключений

### Try-`Catch`-Finally блоки

```java
public class ExceptionHandlingExamples {

    // Базовый try-catch
    public void basicTryCatch() {
        try {
            int result = divide(10, 0);
            System.out.println("Result: " + result);
        } catch (ArithmeticException e) {
            System.err.println("Division by zero: " + e.getMessage());
        }
    }

    // Множественные catch блоки
    public void multipleCatchBlocks() {
        try {
            processFile("input.txt");
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    // Finally блок
    public void finallyBlock() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("data.txt");
            // работа с файлом
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // этот код выполнится всегда
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e.getMessage());
                }
            }
        }
    }

    // Try-with-resources (автоматическое закрытие ресурсов)
    public void tryWithResources() {
        try (FileInputStream fis = new FileInputStream("data.txt");
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            // работа с потоками
            int data = bis.read();
            System.out.println("Read: " + data);

        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        }
        // ресурсы автоматически закрыты
    }

    private int divide(int a, int b) {
        return a / b;
    }

    private void processFile(String filename) throws IOException {
        // имитация обработки файла
        throw new IOException("Simulated IO error");
    }
}
```

### Multi-Catch и Union Types

```java
public class AdvancedCatch {

    // Multi-catch (Java 7+)
    public void multiCatchExample() {
        try {
            riskyOperation();
        } catch (IOException | SQLException e) {
            // обработка обоих типов исключений
            System.err.println("IO or SQL error: " + e.getMessage());
            // e является final - нельзя переприсвоить
        }
    }

    // Более сложный multi-catch
    public void complexMultiCatch() {
        try {
            networkOperation();
        } catch (ConnectException | SocketTimeoutException e) {
            // специфичные network ошибки
            handleNetworkError(e);
        } catch (IOException e) {
            // другие IO ошибки
            handleGeneralIoError(e);
        }
    }

    private void riskyOperation() throws IOException, SQLException {
        // имитация рискованной операции
        if (Math.random() > 0.5) {
            throw new IOException("Random IO error");
        } else {
            throw new SQLException("Random SQL error");
        }
    }

    private void networkOperation() throws IOException {
        // имитация сетевой операции
        double rand = Math.random();
        if (rand < 0.3) {
            throw new ConnectException("Connection failed");
        } else if (rand < 0.6) {
            throw new SocketTimeoutException("Timeout");
        } else {
            throw new IOException("General network error");
        }
    }

    private void handleNetworkError(IOException e) {
        System.err.println("Network error: " + e.getMessage());
        // логика восстановления сети
    }

    private void handleGeneralIoError(IOException e) {
        System.err.println("General IO error: " + e.getMessage());
        // общая обработка IO ошибок
    }
}
```

### Перевыброс исключений

```java
public class ExceptionRethrowing {

    // Простой перевыброс
    public void simpleRethrow() throws IOException {
        try {
            readFile("data.txt");
        } catch (IOException e) {
            // логирование ошибки
            System.err.println("Failed to read file: " + e.getMessage());
            throw e; // перевыброс оригинального исключения
        }
    }

    // Перевыброс с обертыванием
    public void wrapAndRethrow() throws ApplicationException {
        try {
            externalServiceCall();
        } catch (RemoteException e) {
            throw new ApplicationException("Service call failed", e);
        }
    }

    // Перевыброс с частичным стектрейсом (Java 7+)
    public void rethrowWithSuppressed() throws IOException {
        try {
            complexOperation();
        } catch (IOException mainException) {
            try {
                cleanupResources();
            } catch (IOException cleanupException) {
                // добавление suppressed исключения
                mainException.addSuppressed(cleanupException);
            }
            throw mainException;
        }
    }

    // Использование try-with-resources для suppressed исключений
    public void resourceManagementWithSuppressed() throws IOException {
        try (AutoCloseableResource resource1 = new AutoCloseableResource("Resource1");
             AutoCloseableResource resource2 = new AutoCloseableResource("Resource2")) {

            // основная операция
            performOperation();

        } catch (Exception e) {
            // если при закрытии ресурсов возникнут исключения,
            // они будут добавлены как suppressed
            throw new IOException("Operation failed", e);
        }
    }

    private void readFile(String filename) throws IOException {
        throw new IOException("File read error");
    }

    private void externalServiceCall() throws RemoteException {
        throw new RemoteException("Service unavailable");
    }

    private void complexOperation() throws IOException {
        throw new IOException("Complex operation failed");
    }

    private void cleanupResources() throws IOException {
        throw new IOException("Cleanup failed");
    }

    private void performOperation() throws IOException {
        throw new IOException("Operation failed");
    }

    // Кастомные исключения
    static class ApplicationException extends Exception {
        public ApplicationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static class RemoteException extends Exception {
        public RemoteException(String message) {
            super(message);
        }
    }

    static class AutoCloseableResource implements AutoCloseable {
        private final String name;

        public AutoCloseableResource(String name) {
            this.name = name;
            System.out.println("Opening " + name);
        }

        @Override
        public void close() throws IOException {
            System.out.println("Closing " + name);
            if (Math.random() > 0.7) {
                throw new IOException("Failed to close " + name);
            }
        }
    }
}
```

### Chain of exceptions

```java
public class ExceptionChaining {

    // Создание цепочки исключений
    public void chainedExceptions() {
        try {
            level1();
        } catch (Exception e) {
            System.err.println("Final handler: " + e.getMessage());
            System.err.println("Caused by: " + e.getCause().getMessage());

            // Вывод всей цепочки
            Throwable current = e;
            while (current != null) {
                System.out.println("Exception: " + current.getClass().getSimpleName() +
                                 " - " + current.getMessage());
                current = current.getCause();
            }
        }
    }

    private void level1() throws CustomException {
        try {
            level2();
        } catch (IOException e) {
            throw new CustomException("Level 1 failed", e);
        }
    }

    private void level2() throws IOException {
        try {
            level3();
        } catch (SQLException e) {
            throw new IOException("Database access failed", e);
        }
    }

    private void level3() throws SQLException {
        throw new SQLException("Connection timeout");
    }

    // Кастомное исключение
    static class CustomException extends Exception {
        public CustomException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
```

## Создание исключений

### Кастомные исключения

```java
// Базовое кастомное исключение
public class CustomApplicationException extends Exception {
    public CustomApplicationException(String message) {
        super(message);
    }

    public CustomApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Исключение с дополнительными полями
public class ValidationException extends RuntimeException {
    private final List<String> validationErrors;
    private final String fieldName;

    public ValidationException(String message, String fieldName, List<String> errors) {
        super(message);
        this.fieldName = fieldName;
        this.validationErrors = new ArrayList<>(errors);
    }

    public List<String> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    public String getFieldName() {
        return fieldName;
    }
}

// Использование кастомного исключения
public class UserValidator {
    public void validateUser(User user) {
        List<String> errors = new ArrayList<>();

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            errors.add("Name is required");
        }

        if (user.getEmail() == null || !isValidEmail(user.getEmail())) {
            errors.add("Valid email is required");
        }

        if (user.getAge() < 0 || user.getAge() > 150) {
            errors.add("Age must be between 0 and 150");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("User validation failed", "user", errors);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }
}
```

### Exception Builder Pattern

```java
public class ExceptionBuilders {

    // Builder для исключений
    public static class ErrorContext {
        private String operation;
        private String resource;
        private Map<String, Object> contextData = new HashMap<>();
        private Throwable cause;

        public ErrorContext operation(String operation) {
            this.operation = operation;
            return this;
        }

        public ErrorContext resource(String resource) {
            this.resource = resource;
            return this;
        }

        public ErrorContext context(String key, Object value) {
            this.contextData.put(key, value);
            return this;
        }

        public ErrorContext cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public BusinessException build() {
            String message = String.format("Operation '%s' failed for resource '%s'",
                                         operation, resource);
            BusinessException exception = new BusinessException(message, cause);
            exception.setContext(contextData);
            return exception;
        }
    }

    public static ErrorContext error() {
        return new ErrorContext();
    }

    // Использование
    public void processPayment(Payment payment) {
        try {
            paymentService.process(payment);
        } catch (PaymentException e) {
            throw error()
                .operation("processPayment")
                .resource("payment:" + payment.getId())
                .context("amount", payment.getAmount())
                .context("currency", payment.getCurrency())
                .cause(e)
                .build();
        }
    }

    // Кастомное исключение с контекстом
    static class BusinessException extends RuntimeException {
        private Map<String, Object> context = new HashMap<>();

        public BusinessException(String message, Throwable cause) {
            super(message, cause);
        }

        public void setContext(Map<String, Object> context) {
            this.context = new HashMap<>(context);
        }

        public Map<String, Object> getContext() {
            return Collections.unmodifiableMap(context);
        }

        @Override
        public String toString() {
            return super.toString() + " Context: " + context;
        }
    }

    // Модельные классы
    static class Payment {
        private String id;
        private BigDecimal amount;
        private String currency;

        // getters/setters
        public String getId() { return id; }
        public BigDecimal getAmount() { return amount; }
        public String getCurrency() { return currency; }
    }

    static class PaymentException extends Exception {
        public PaymentException(String message) {
            super(message);
        }
    }

    // Сервис
    private Object paymentService = new Object(); // mock
}
```

## Best practices

### Когда использовать исключения

```java
public class ExceptionUsageGuidelines {

    // ✅ Использовать исключения для исключительных ситуаций
    public User findUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    // ❌ НЕ использовать исключения для контроля потока
    public boolean isUserExists(Long id) {
        try {
            findUserById(id);
            return true;
        } catch (UserNotFoundException e) {
            return false; // Плохая практика!
        }
    }

    // ✅ Правильный способ проверки существования
    public boolean isUserExistsProper(Long id) {
        return userRepository.existsById(id);
    }

    // ✅ Использовать checked исключения для recoverable ошибок
    public void transferMoney(Account from, Account to, BigDecimal amount) throws InsufficientFundsException {
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        // выполнение перевода
    }

    // ✅ Использовать unchecked исключения для programming errors
    public void processOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Order must contain at least one item");
        }
        // обработка заказа
    }

    // Кастомные исключения
    static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    static class InsufficientFundsException extends Exception {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }

    // Модельные классы
    static class User {
        private Long id;
        private String name;
        // getters/setters
    }

    static class Account {
        private BigDecimal balance;
        public BigDecimal getBalance() { return balance; }
    }

    static class Order {
        private List<OrderItem> items = new ArrayList<>();
        public List<OrderItem> getItems() { return items; }
    }

    static class OrderItem {
        // item details
    }

    // Mock репозиторий
    private static class UserRepository {
        public User findById(Long id) { return null; }
        public boolean existsById(Long id) { return false; }
    }

    private UserRepository userRepository = new UserRepository();
}
```

### Обработка исключений

```java
public class ExceptionHandlingBestPractices {

    // ✅ Правильная обработка: конкретные исключения в правильном порядке
    public void properExceptionHandling() {
        try {
            processData(readDataFromFile("input.txt"));
        } catch (FileNotFoundException e) {
            // Обработка отсутствия файла
            logger.error("Input file not found: {}", e.getMessage());
            throw new ApplicationException("Configuration file missing", e);
        } catch (ValidationException e) {
            // Обработка ошибок валидации
            logger.warn("Data validation failed: {}", e.getMessage());
            sendValidationErrorNotification(e.getErrors());
        } catch (ProcessingException e) {
            // Обработка ошибок обработки
            logger.error("Data processing failed", e);
            rollbackTransaction();
            throw e; // Перевыброс
        } catch (Exception e) {
            // Обработка неожиданных ошибок
            logger.error("Unexpected error", e);
            throw new ApplicationException("Unexpected error occurred", e);
        }
    }

    // ❌ Неправильный порядок catch блоков
    public void wrongCatchOrder() {
        try {
            riskyOperation();
        } catch (Exception e) { // Перехватывает все исключения
            handleException(e);
        } catch (IOException e) { // Этот блок никогда не выполнится!
            handleIOException(e);
        }
    }

    // ✅ Правильный порядок: от конкретного к общему
    public void correctCatchOrder() {
        try {
            riskyOperation();
        } catch (IOException e) { // Конкретное исключение
            handleIOException(e);
        } catch (Exception e) { // Общее исключение
            handleException(e);
        }
    }

    // ✅ Не глотить исключения молча
    public void dontSwallowExceptions() {
        try {
            unreliableOperation();
        } catch (Exception e) {
            // ❌ Плохо: исключение игнорируется
            // Ничего не делаем
        }
    }

    // ✅ Правильная обработка
    public void properExceptionHandling() {
        try {
            unreliableOperation();
        } catch (RecoverableException e) {
            // Попытка восстановления
            logger.warn("Recoverable error, attempting recovery", e);
            attemptRecovery(e);
        } catch (Exception e) {
            // Логирование и перевыброс
            logger.error("Unrecoverable error", e);
            throw new ApplicationException("Operation failed", e);
        }
    }

    // ✅ Использование finally для cleanup
    public void properResourceCleanup() {
        Connection connection = null;
        try {
            connection = createConnection();
            performDatabaseOperation(connection);
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            throw new DatabaseException("Database operation failed", e);
        } finally {
            // Всегда закрываем соединение
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.warn("Failed to close connection", e);
                }
            }
        }
    }

    // ✅ Try-with-resources для автоматического закрытия
    public void tryWithResources() {
        try (Connection connection = createConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                processUser(resultSet);
            }

        } catch (SQLException e) {
            logger.error("Database query failed", e);
            throw new DatabaseException("Query execution failed", e);
        }
        // Все ресурсы автоматически закрыты
    }

    private void processData(String data) throws ValidationException, ProcessingException {
        // имитация обработки
    }

    private String readDataFromFile(String filename) throws FileNotFoundException {
        // имитация чтения
        return "data";
    }

    private void sendValidationErrorNotification(List<String> errors) {
        // отправка уведомления
    }

    private void rollbackTransaction() {
        // откат транзакции
    }

    private void riskyOperation() throws IOException {
        // рискованная операция
    }

    private void handleException(Exception e) {
        logger.error("Exception handled", e);
    }

    private void handleIOException(IOException e) {
        logger.error("IO Exception handled", e);
    }

    private void unreliableOperation() throws Exception {
        // ненадежная операция
    }

    private void attemptRecovery(RecoverableException e) {
        // попытка восстановления
    }

    private Connection createConnection() throws SQLException {
        // создание соединения
        return null;
    }

    private void performDatabaseOperation(Connection connection) throws SQLException {
        // операция с БД
    }

    private void processUser(ResultSet resultSet) throws SQLException {
        // обработка пользователя
    }

    // Кастомные исключения
    static class ApplicationException extends RuntimeException {
        public ApplicationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static class ValidationException extends RuntimeException {
        private final List<String> errors;

        public ValidationException(List<String> errors) {
            this.errors = errors;
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    static class ProcessingException extends Exception {
        public ProcessingException(String message) {
            super(message);
        }
    }

    static class RecoverableException extends Exception {
        public RecoverableException(String message) {
            super(message);
        }
    }

    static class DatabaseException extends RuntimeException {
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Logger mock
    private static final Object logger = new Object();
}
```

### Производительность

```java
public class ExceptionPerformance {

    // ❌ Плохо: исключения в цикле
    public List<String> badExceptionHandling(List<String> inputs) {
        List<String> results = new ArrayList<>();

        for (String input : inputs) {
            try {
                String result = processInput(input);
                results.add(result);
            } catch (ValidationException e) {
                // Игнорирование ошибки - плохо для производительности
                results.add("ERROR");
            }
        }

        return results;
    }

    // ✅ Хорошо: предварительная валидация
    public List<String> goodExceptionHandling(List<String> inputs) {
        List<String> results = new ArrayList<>();

        for (String input : inputs) {
            if (isValidInput(input)) {
                String result = processInput(input);
                results.add(result);
            } else {
                results.add("INVALID");
            }
        }

        return results;
    }

    // ✅ Использовать исключения только для исключительных ситуаций
    public Optional<User> findUserSafely(Long id) {
        try {
            return Optional.of(userRepository.findById(id));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    // ✅ Создание исключений не должно быть дорогим
    public void validateInput(String input) {
        // Плохо: создание нового исключения при каждой проверке
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        // Хорошо: предварительная валидация
        Objects.requireNonNull(input, "Input cannot be null");
    }

    private boolean isValidInput(String input) {
        return input != null && !input.trim().isEmpty();
    }

    private String processInput(String input) throws ValidationException {
        if (input.length() < 3) {
            throw new ValidationException("Input too short");
        }
        return input.toUpperCase();
    }

    // Кастомные исключения
    static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
    }

    // Mock классы
    static class User {
        private Long id;
        // getters/setters
    }

    static class UserRepository {
        public User findById(Long id) throws NotFoundException {
            throw new NotFoundException("User not found");
        }
    }

    private UserRepository userRepository = new UserRepository();
}
```

### Логирование исключений

```java
public class ExceptionLogging {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionLogging.class);

    // ✅ Правильное логирование исключений
    public void properExceptionLogging() {
        try {
            riskyOperation();
        } catch (ValidationException e) {
            // Логирование с контекстом
            logger.warn("Validation failed for user {}: {}", userId, e.getMessage(), e);
            throw e; // Перевыброс
        } catch (ExternalServiceException e) {
            // Логирование с уровнем ERROR для внешних служб
            logger.error("External service call failed for operation {}", operationId, e);

            // Попытка восстановления
            if (isRetryable(e)) {
                retryOperation();
            } else {
                throw new ApplicationException("External service unavailable", e);
            }
        } catch (Exception e) {
            // Логирование неожиданных исключений
            logger.error("Unexpected error in operation {}", operationId, e);

            // Отправка алерта
            alertService.sendAlert("Unexpected error", e);

            throw new ApplicationException("Internal error occurred", e);
        }
    }

    // ✅ Избегать логирования чувствительной информации
    public void secureExceptionLogging() {
        try {
            authenticateUser(username, password);
        } catch (AuthenticationException e) {
            // ❌ Плохо: логирование пароля
            // logger.warn("Authentication failed for user {} with password {}", username, password, e);

            // ✅ Хорошо: безопасное логирование
            logger.warn("Authentication failed for user {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    // ✅ Структурированное логирование
    public void structuredExceptionLogging() {
        try {
            processPayment(paymentId, amount);
        } catch (PaymentException e) {
            // Структурированное логирование с MDC
            MDC.put("paymentId", paymentId.toString());
            MDC.put("amount", amount.toString());
            MDC.put("operation", "processPayment");

            try {
                logger.error("Payment processing failed", e);
                throw e;
            } finally {
                MDC.clear();
            }
        }
    }

    private void riskyOperation() throws ValidationException, ExternalServiceException {
        // имитация рискованной операции
    }

    private boolean isRetryable(ExternalServiceException e) {
        return e.getStatusCode() >= 500;
    }

    private void retryOperation() {
        // повторная попытка
    }

    private void authenticateUser(String username, String password) throws AuthenticationException {
        // аутентификация
    }

    private void processPayment(Long paymentId, BigDecimal amount) throws PaymentException {
        // обработка платежа
    }

    // Mock переменные
    private Long userId = 123L;
    private String operationId = "op-456";
    private String username = "user@example.com";
    private String password = "secret";
    private Long paymentId = 789L;
    private BigDecimal amount = new BigDecimal("100.00");
    private Object alertService = new Object();

    // Кастомные исключения
    static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    static class ExternalServiceException extends Exception {
        private int statusCode;

        public ExternalServiceException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    static class PaymentException extends Exception {
        public PaymentException(String message) {
            super(message);
        }
    }

    static class ApplicationException extends RuntimeException {
        public ApplicationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
```

## Интеграция с фреймворками

### Spring Framework

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Обработка валидационных ошибок
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());

        return new ErrorResponse("VALIDATION_ERROR", errors);
    }

    // Обработка бизнес исключений
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleBusinessException(BusinessException ex) {
        logger.warn("Business error: {}", ex.getMessage(), ex);
        return new ErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    // Обработка системных ошибок
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGenericException(Exception ex) {
        logger.error("Unexpected error", ex);
        return new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
    }

    // Обработка отсутствия ресурса
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse("NOT_FOUND", ex.getMessage());
    }

    // DTO для ошибок
    public static class ErrorResponse {
        private final String errorCode;
        private final Object message;
        private final LocalDateTime timestamp;

        public ErrorResponse(String errorCode, Object message) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }

        // getters
        public String getErrorCode() { return errorCode; }
        public Object getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }

    // Кастомные исключения
    public static class BusinessException extends RuntimeException {
        private final String errorCode;

        public BusinessException(String errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}

// Использование в контроллере
@RestController
public class UserController {

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("USER_EXISTS", "User with this email already exists");
        }

        User user = userService.createUser(request);
        return user;
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
```

### JUnit Testing

```java
public class ExceptionTesting {

    @Test
    void testExpectedException() {
        // Старый способ (JUnit 4)
        try {
            calculator.divide(10, 0);
            fail("Expected ArithmeticException");
        } catch (ArithmeticException e) {
            assertEquals("/ by zero", e.getMessage());
        }
    }

    @Test
    void testExpectedExceptionWithAssertThrows() {
        // JUnit 5 способ
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> {
            calculator.divide(10, 0);
        });

        assertEquals("/ by zero", exception.getMessage());
    }

    @Test
    void testCustomException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userValidator.validateUser(null, "invalid-email", 200);
        });

        assertTrue(exception.getErrors().contains("Name is required"));
        assertTrue(exception.getErrors().contains("Invalid email"));
        assertTrue(exception.getErrors().contains("Age must be between 0 and 150"));
    }

    @Test
    void testExceptionHierarchy() {
        // Проверка что исключение является подклассом
        assertThrows(RuntimeException.class, () -> {
            throw new IllegalArgumentException("test");
        });
    }

    @Test
    void testExceptionMessage() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Invalid argument: value");
        });

        assertTrue(exception.getMessage().contains("Invalid argument"));
    }

    // Кастомные исключения для тестирования
    static class ValidationException extends RuntimeException {
        private final List<String> errors;

        public ValidationException(List<String> errors) {
            this.errors = errors;
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    static class Calculator {
        public int divide(int a, int b) {
            return a / b;
        }
    }

    static class UserValidator {
        public void validateUser(String name, String email, int age) {
            List<String> errors = new ArrayList<>();

            if (name == null || name.trim().isEmpty()) {
                errors.add("Name is required");
            }

            if (email == null || !email.contains("@")) {
                errors.add("Invalid email");
            }

            if (age < 0 || age > 150) {
                errors.add("Age must be between 0 and 150");
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }
        }
    }

    // Mock объекты
    private Calculator calculator = new Calculator();
    private UserValidator userValidator = new UserValidator();
}
```


## Решение проблем

При `SuppressedException` в `try-with-resources` проверьте порядок закрытия ресурсов: исключения добавляются в обратном порядке. При «exception in thread» без stack trace включите `-XX:+PrintStackTrace` или используйте `e.printStackTrace()`. При утечке из-за невыброшенных исключений в `finally` избегайте `return` в `finally` — используйте `try-with-resources`.

## Частые вопросы

**Когда использовать checked vs unchecked?** — Checked для recoverable ошибок (IO, сеть), unchecked для логических ошибок (NPE, IllegalArgumentException). Не оборачивайте unchecked в checked без необходимости.

**Нужно ли логировать и перевыбрасывать?** — Либо логируйте и обрабатывайте на месте, либо перевыбрасывайте. Не делайте оба — это дублирует записи в логах.

## Заключение

Обработка исключений — это фундаментальный аспект **Java** программирования, который влияет на надежность, поддерживаемость и пользовательский опыт приложений.

### Ключевые принципы

1. **Использовать checked исключения для recoverable ошибок**
2. **Использовать unchecked исключения для programming errors**
3. **Создавать descriptive сообщения об ошибках**
4. **Не игнорировать исключения молча**
5. **Использовать `try-with-resources` для автоматического закрытия ресурсов**
6. **Логировать исключения с достаточным контекстом**
7. **Создавать custom исключения для бизнес логики**

### Best practices summary

- **Catch конкретные исключения перед общими**
- **Использовать finally для cleanup**
- **Перевыбрасывать исключения с дополнительным контекстом**
- **Создавать custom исключения с meaningful сообщениями**
- **Тестировать обработку исключений**
- **Логировать исключения на appropriate уровне**
- **Использовать `try-with-resources` для ресурсов**

### Интеграция с экосистемой

- **Spring Framework**: @**ControllerAdvice** для глобальной обработки
- **JUnit**: **assertThrows** для тестирования исключений
- **SLF4J**: Структурированное логирование исключений
- **Jackson**: Сериализация исключений в **JSON**

Правильная обработка исключений делает код более надежным, поддерживаемым и **user-friendly**. Исключения должны использоваться для сигнализации о проблемах, а не для контроля потока выполнения программы.


[⬆ Наверх](./#java-cheatsheets)

## См. также

- [[java-annotations-reflection|Java Annotations и Reflection]]
- [[java-basics|Java: основы]]
- [[java-collections-converting|Java Collections: конвертирование]]
- [[java-collections-list|Java Collections: List]]
- [[java-collections-map|Java Collections: Map]]
