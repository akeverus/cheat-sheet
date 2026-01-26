# Singleton Pattern (Одиночка)

Singleton гарантирует, что у класса есть только один экземпляр, и предоставляет глобальную точку доступа к этому экземпляру.

**Дата последнего обновления:** 2026-01-24

## Полезные ссылки

### Официальная документация
- [Java Singleton Pattern](https://docs.oracle.com/javase/8/docs/api/java/lang/Runtime.html)
- [Effective Java - Item 3](https://www.amazon.com/Effective-Java-Joshua-Bloch/dp/0134685997)

### См. также
- `../concurrency-patterns/double-checked-locking.md` - Double-Checked Locking
- `../../java/java-basics.md` - Java Basics
- `../../frameworks/java-frameworks/spring/spring-beans.md` - Spring Beans

## Содержание

- [Что такое Singleton?](#что-такое-singleton)
  - [Основные характеристики](#основные-характеристики)
  - [Проблемы, которые решает](#проблемы-которые-решает)
- [Когда использовать Singleton?](#когда-использовать-singleton)
  - [Подходящие сценарии](#подходящие-сценарии)
  - [Признаки необходимости](#признаки-необходимости)
- [Реализация на Java](#реализация-на-java)
  - [Базовая реализация (не thread-safe)](#базовая-реализация-не-thread-safe)
  - [Thread-Safe реализация (synchronized)](#thread-safe-реализация-synchronized)
  - [Double-Checked Locking (рекомендуемый подход)](#double-checked-locking-рекомендуемый-подход)
  - [Holder Class (самый безопасный способ)](#holder-class-самый-безопасный-способ)
  - [Enum Singleton](#enum-singleton)
- [Продвинутые реализации](#продвинутые-реализации)
  - [1. Configurable Singleton](#1-configurable-singleton)
  - [2. Singleton с жизненным циклом](#2-singleton-с-жизненным-циклом)
  - [3. Registry of Singletons](#3-registry-of-singletons)
- [Примеры использования](#примеры-использования)
  - [1. Database Connection Pool](#1-database-connection-pool)
  - [2. Application Logger](#2-application-logger)
  - [3. Configuration Manager](#3-configuration-manager)
- [Best Practices](#best-practices)
  - [1. Выбор правильной реализации](#1-выбор-правильной-реализации)
  - [2. Избегание распространенных ошибок](#2-избегание-распространенных-ошибок)

## Что такое Singleton?

Singleton — это порождающий паттерн проектирования, который гарантирует, что у класса есть только один экземпляр, и предоставляет глобальную точку доступа к этому экземпляру.

### Основные характеристики

1. **Единственный экземпляр**: Гарантируется только один объект класса
2. **Глобальный доступ**: Доступ к экземпляру из любой точки приложения
3. **Ленивая инициализация**: Объект создается только при первом обращении
4. **Thread Safety**: Безопасная работа в многопоточной среде

### Проблемы, которые решает

```java
// Плохо: Множественные экземпляры одного ресурса
public class DatabaseConnection {
    public DatabaseConnection() {
        // Создание соединения - дорогая операция
        connectToDatabase();
    }
}

public class Application {
    public void method1() {
        DatabaseConnection conn1 = new DatabaseConnection(); // Новое соединение
    }

    public void method2() {
        DatabaseConnection conn2 = new DatabaseConnection(); // Еще одно соединение
    }
}

// Хорошо: Один экземпляр для всего приложения
public class DatabaseConnectionPool {
    private static DatabaseConnectionPool instance;

    private DatabaseConnectionPool() {
        initializePool();
    }

    public static DatabaseConnectionPool getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionPool();
        }
        return instance;
    }
}
```

## Когда использовать Singleton?

### Подходящие сценарии

- **Ресурсы приложения**: Connection pools, caches, configurations
- **Логирование**: Logger instances, audit services
- **Конфигурация**: Application settings, environment variables
- **Hardware интерфейсы**: Device drivers, sensors
- **Системные сервисы**: File system, network services

### Признаки необходимости

```java
// Признаки: Ресурс должен быть уникальным в приложении
public class Indicators {

    // Один объект конфигурации для всего приложения
    public class ConfigurationManager {
        // Должен быть только один экземпляр
    }

    // Один пул соединений с БД
    public class DatabaseConnectionPool {
        // Должен быть только один пул
    }

    // Один логгер для всего приложения
    public class ApplicationLogger {
        // Должен быть только один логгер
    }

    // Один менеджер кэша
    public class CacheManager {
        // Должен быть только один менеджер
    }
}
```

## Реализация на Java

### Базовая реализация (не thread-safe)

```java
/**
 * Базовая реализация Singleton (не потокобезопасная)
 * Использовать только в однопоточных приложениях
 */
public class BasicSingleton {

    private static BasicSingleton instance;

    // Приватный конструктор предотвращает создание экземпляров
    private BasicSingleton() {
        // Инициализация
        System.out.println("BasicSingleton created");
    }

    // Глобальная точка доступа
    public static BasicSingleton getInstance() {
        if (instance == null) {
            instance = new BasicSingleton();
        }
        return instance;
    }

    public void doSomething() {
        System.out.println("BasicSingleton doing something");
    }
}

// Использование
public class BasicSingletonDemo {
    public static void main(String[] args) {
        BasicSingleton singleton1 = BasicSingleton.getInstance();
        BasicSingleton singleton2 = BasicSingleton.getInstance();

        System.out.println(singleton1 == singleton2); // true

        singleton1.doSomething();
    }
}
```

### Thread-Safe реализация (synchronized)

```java
/**
 * Thread-safe Singleton с synchronized методом
 * Производительность: низкая из-за синхронизации при каждом вызове
 */
public class SynchronizedSingleton {

    private static SynchronizedSingleton instance;

    private SynchronizedSingleton() {
        // Имитация тяжелой инициализации
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("SynchronizedSingleton created by " + Thread.currentThread().getName());
    }

    // synchronized гарантирует thread-safety, но блокирует все вызовы
    public static synchronized SynchronizedSingleton getInstance() {
        if (instance == null) {
            instance = new SynchronizedSingleton();
        }
        return instance;
    }

    public void doSomething() {
        System.out.println("SynchronizedSingleton working in " + Thread.currentThread().getName());
    }
}

// Демонстрация проблем производительности
public class SynchronizedSingletonDemo {
    public static void main(String[] args) {
        Runnable task = () -> {
            SynchronizedSingleton singleton = SynchronizedSingleton.getInstance();
            singleton.doSomething();
        };

        // Создаем много потоков - каждый вызов getInstance() будет синхронизирован
        for (int i = 0; i < 10; i++) {
            new Thread(task, "Thread-" + i).start();
        }
    }
}
```

### Double-Checked Locking (рекомендуемый подход)

```java
/**
 * Double-Checked Locking Singleton (рекомендуемый подход)
 * Thread-safe и производительный
 */
public class DclSingleton {

    // volatile гарантирует видимость изменений между потоками
    private static volatile DclSingleton instance;

    private DclSingleton() {
        // Имитация инициализации
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("DclSingleton created by " + Thread.currentThread().getName());
    }

    public static DclSingleton getInstance() {
        // Первая проверка без синхронизации
        DclSingleton result = instance;

        if (result == null) {
            // Синхронизированная блокировка только для создания
            synchronized (DclSingleton.class) {
                result = instance;

                // Вторая проверка внутри synchronized блока
                if (result == null) {
                    instance = result = new DclSingleton();
                }
            }
        }

        return result;
    }

    public void doSomething() {
        System.out.println("DclSingleton working in " + Thread.currentThread().getName());
    }
}

// Тестирование производительности
public class DclSingletonDemo {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.nanoTime();

        Runnable task = () -> {
            DclSingleton singleton = DclSingleton.getInstance();
            singleton.doSomething();
        };

        Thread[] threads = new Thread[20];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(task);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.nanoTime();
        System.out.println("Total time: " + (endTime - startTime) / 1_000_000 + "ms");
    }
}
```

### Holder Class (самый безопасный способ)

```java
/**
 * Holder Class Singleton - самый безопасный и производительный подход
 * Инициализация происходит при загрузке класса
 */
public class HolderSingleton {

    // Приватный конструктор
    private HolderSingleton() {
        System.out.println("HolderSingleton created");
    }

    // Holder класс инициализируется при первом обращении к getInstance()
    private static class Holder {
        private static final HolderSingleton INSTANCE = new HolderSingleton();
    }

    // Глобальная точка доступа
    public static HolderSingleton getInstance() {
        return Holder.INSTANCE;
    }

    public void doSomething() {
        System.out.println("HolderSingleton working");
    }

    // Метод для тестирования
    static void reset() {
        // Невозможно сбросить без reflection (что и хорошо)
    }
}

// Преимущества Holder Class:
// - Thread-safe автоматически
// - Ленивая инициализация
// - Нет проблем с memory model
// - Исключения в конструкторе корректно обрабатываются
// - Невозможно сломать через reflection (без дополнительных усилий)
```

### Enum Singleton

```java
/**
 * Enum Singleton - самый простой и надежный способ
 * Гарантирует единственность даже при сериализации/десериализации
 */
public enum EnumSingleton {

    INSTANCE;

    private String data;

    // Конструктор вызывается автоматически при загрузке enum
    EnumSingleton() {
        System.out.println("EnumSingleton created");
        this.data = "default";
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void doSomething() {
        System.out.println("EnumSingleton working with data: " + data);
    }

    // Преимущества Enum Singleton:
    // - Thread-safe автоматически
    // - Защита от reflection attacks
    // - Гарантированная единственность
    // - Автоматическая сериализация
    // - Нельзя наследоваться (что обычно не нужно)
}

// Использование
public class EnumSingletonDemo {
    public static void main(String[] args) {
        EnumSingleton singleton1 = EnumSingleton.INSTANCE;
        EnumSingleton singleton2 = EnumSingleton.INSTANCE;

        System.out.println(singleton1 == singleton2); // true

        singleton1.setData("test data");
        System.out.println(singleton2.getData()); // "test data"
    }
}
```

## Продвинутые реализации

### 1. Configurable Singleton

```java
// Singleton с конфигурацией
public class ConfigurableSingleton {

    private static volatile ConfigurableSingleton instance;
    private final String config;

    private ConfigurableSingleton(String config) {
        this.config = config;
        initializeWithConfig(config);
    }

    public static ConfigurableSingleton getInstance(String config) {
        ConfigurableSingleton result = instance;

        if (result == null) {
            synchronized (ConfigurableSingleton.class) {
                result = instance;
                if (result == null) {
                    instance = result = new ConfigurableSingleton(config);
                }
            }
        }

        return result;
    }

    private void initializeWithConfig(String config) {
        System.out.println("Initializing with config: " + config);
        // Инициализация на основе конфигурации
    }

    public String getConfig() {
        return config;
    }
}
```

### 2. Singleton с жизненным циклом

```java
// Singleton с управлением жизненным циклом
public class LifecycleSingleton implements AutoCloseable {

    private static volatile LifecycleSingleton instance;
    private final ExecutorService executor;
    private volatile boolean closed = false;

    private LifecycleSingleton() {
        this.executor = Executors.newFixedThreadPool(5);
        System.out.println("LifecycleSingleton initialized");
    }

    public static LifecycleSingleton getInstance() {
        LifecycleSingleton result = instance;

        if (result == null) {
            synchronized (LifecycleSingleton.class) {
                result = instance;
                if (result == null) {
                    instance = result = new LifecycleSingleton();
                }
            }
        }

        if (result.closed) {
            throw new IllegalStateException("Singleton is closed");
        }

        return result;
    }

    public Future<String> submitTask(String input) {
        if (closed) {
            throw new IllegalStateException("Singleton is closed");
        }

        return executor.submit(() -> processTask(input));
    }

    private String processTask(String input) {
        if (closed) {
            throw new IllegalStateException("Processing cancelled");
        }

        try {
            Thread.sleep(100);
            return "Processed: " + input;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted: " + input;
        }
    }

    @Override
    public void close() {
        if (!closed) {
            synchronized (LifecycleSingleton.class) {
                if (!closed) {
                    closed = true;
                    executor.shutdown();
                    try {
                        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                            executor.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        executor.shutdownNow();
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("LifecycleSingleton closed");
                }
            }
        }
    }

    // Для тестирования
    public static void reset() {
        synchronized (LifecycleSingleton.class) {
            if (instance != null) {
                instance.close();
                instance = null;
            }
        }
    }
}
```

### 3. Registry of Singletons

```java
// Реестр синглтонов для управления множеством синглтонов
public class SingletonRegistry {

    private static final Map<Class<?>, Object> registry = new ConcurrentHashMap<>();
    private static final Object lock = new Object();

    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> singletonClass) {
        Object instance = registry.get(singletonClass);

        if (instance == null) {
            synchronized (lock) {
                instance = registry.get(singletonClass);
                if (instance == null) {
                    try {
                        instance = singletonClass.getDeclaredConstructor().newInstance();
                        registry.put(singletonClass, instance);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create singleton: " + singletonClass.getName(), e);
                    }
                }
            }
        }

        return (T) instance;
    }

    public static void register(Class<?> singletonClass, Object instance) {
        synchronized (lock) {
            registry.put(singletonClass, instance);
        }
    }

    public static void unregister(Class<?> singletonClass) {
        synchronized (lock) {
            registry.remove(singletonClass);
        }
    }

    public static void clear() {
        synchronized (lock) {
            registry.clear();
        }
    }

    public static int size() {
        return registry.size();
    }
}

// Пример использования реестра
public class RegistryDemo {

    public static class ServiceA {
        public void doWork() {
            System.out.println("ServiceA working");
        }
    }

    public static class ServiceB {
        public void doWork() {
            System.out.println("ServiceB working");
        }
    }

    public static void main(String[] args) {
        // Получение экземпляров через реестр
        ServiceA serviceA1 = SingletonRegistry.getInstance(ServiceA.class);
        ServiceA serviceA2 = SingletonRegistry.getInstance(ServiceA.class);

        System.out.println(serviceA1 == serviceA2); // true

        ServiceB serviceB = SingletonRegistry.getInstance(ServiceB.class);

        serviceA1.doWork();
        serviceB.doWork();

        System.out.println("Registry size: " + SingletonRegistry.size());
    }
}
```

## Примеры использования

### 1. Database Connection Pool

```java
@Service
public class DatabaseConnectionPool {

    private static volatile DatabaseConnectionPool instance;
    private final List<Connection> connections;
    private final int maxConnections;

    private DatabaseConnectionPool() {
        this.maxConnections = 10;
        this.connections = new ArrayList<>();

        initializeConnections();
    }

    public static DatabaseConnectionPool getInstance() {
        DatabaseConnectionPool result = instance;

        if (result == null) {
            synchronized (DatabaseConnectionPool.class) {
                result = instance;
                if (result == null) {
                    instance = result = new DatabaseConnectionPool();
                }
            }
        }

        return result;
    }

    private void initializeConnections() {
        for (int i = 0; i < maxConnections; i++) {
            try {
                Connection conn = DriverManager.getConnection(
                    "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
                connections.add(conn);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create connection", e);
            }
        }
    }

    public Connection getConnection() throws SQLException {
        synchronized (connections) {
            if (connections.isEmpty()) {
                throw new SQLException("No available connections");
            }
            return connections.remove(connections.size() - 1);
        }
    }

    public void returnConnection(Connection connection) {
        synchronized (connections) {
            if (connections.size() < maxConnections) {
                connections.add(connection);
            } else {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // Log error
                }
            }
        }
    }

    public int getAvailableConnections() {
        synchronized (connections) {
            return connections.size();
        }
    }
}
```

### 2. Application Logger

```java
@Service
public class ApplicationLogger {

    private static volatile ApplicationLogger instance;
    private final Logger logger;

    private ApplicationLogger() {
        // Настройка логгера
        logger = LoggerFactory.getLogger(ApplicationLogger.class);
        configureLogger();
    }

    public static ApplicationLogger getInstance() {
        ApplicationLogger result = instance;

        if (result == null) {
            synchronized (ApplicationLogger.class) {
                result = instance;
                if (result == null) {
                    instance = result = new ApplicationLogger();
                }
            }
        }

        return result;
    }

    private void configureLogger() {
        // Настройка аппендеров, форматтеров и т.д.
        System.out.println("Logger configured");
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    // Асинхронное логирование
    public void infoAsync(String message) {
        CompletableFuture.runAsync(() -> logger.info(message))
            .exceptionally(throwable -> {
                System.err.println("Failed to log: " + throwable.getMessage());
                return null;
            });
    }

    // Структурированное логирование
    public void logEvent(String eventType, Map<String, Object> data) {
        logger.info("Event: {} - Data: {}", eventType, data);
    }
}
```

### 3. Configuration Manager

```java
@Service
public class ConfigurationManager {

    private static volatile ConfigurationManager instance;
    private volatile Properties configuration;
    private final Path configPath;

    private ConfigurationManager() {
        this.configPath = Paths.get("application.properties");
        loadConfiguration();
    }

    public static ConfigurationManager getInstance() {
        ConfigurationManager result = instance;

        if (result == null) {
            synchronized (ConfigurationManager.class) {
                result = instance;
                if (result == null) {
                    instance = result = new ConfigurationManager();
                }
            }
        }

        return result;
    }

    public String getProperty(String key) {
        Properties config = configuration;
        if (config == null) {
            synchronized (this) {
                config = configuration;
                if (config == null) {
                    loadConfiguration();
                    config = configuration;
                }
            }
        }
        return config.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public void setProperty(String key, String value) {
        synchronized (this) {
            if (configuration == null) {
                configuration = new Properties();
            }
            configuration.setProperty(key, value);
            saveConfiguration();
        }
    }

    private void loadConfiguration() {
        Properties props = new Properties();
        if (Files.exists(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                props.load(in);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load configuration", e);
            }
        }
        configuration = props;
    }

    private void saveConfiguration() {
        try (OutputStream out = Files.newOutputStream(configPath)) {
            configuration.store(out, "Auto-generated configuration");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save configuration", e);
        }
    }

    // Hot reload
    public void reloadConfiguration() {
        synchronized (this) {
            configuration = null;
        }
    }
}
```

## Best Practices

### 1. Выбор правильной реализации

```java
public class SingletonSelectionGuide {

    // Используйте Enum Singleton когда:
    // - Нужна абсолютная гарантия единственности
    // - Важна сериализация
    // - Защита от reflection важна
    // - Простая логика без параметров
    public enum SimpleService {
        INSTANCE;

        public void doWork() {
            // implementation
        }
    }

    // Используйте Holder Class когда:
    // - Нужна ленивая инициализация
    // - Исключения в конструкторе должны корректно обрабатываться
    // - Thread-safety важна, но нет параметров
    public static class ComplexService {
        private static class Holder {
            static final ComplexService INSTANCE = new ComplexService();
        }

        public static ComplexService getInstance() {
            return Holder.INSTANCE;
        }
    }

    // Используйте DCL когда:
    // - Нужна ленивая инициализация с параметрами
    // - Важна производительность
    // - Конструктор может бросать исключения
    public static class ConfigurableService {
        private static volatile ConfigurableService instance;

        public static ConfigurableService getInstance(String config) {
            ConfigurableService result = instance;

            if (result == null) {
                synchronized (ConfigurableService.class) {
                    result = instance;
                    if (result == null) {
                        instance = result = new ConfigurableService(config);
                    }
                }
            }

            return result;
        }

        private final String config;
        private ConfigurableService(String config) {
            this.config = config;
        }
    }
}
```

### 2. Избегание распространенных ошибок

```java
public class SingletonPitfalls {

    // ОШИБКА: Serializable без readResolve
    public static class BrokenSerializableSingleton implements Serializable {
        private static final long serialVersionUID = 1L;
        private static BrokenSerializableSingleton instance = new BrokenSerializableSingleton();

        private BrokenSerializableSingleton() {}

        public static BrokenSerializableSingleton getInstance() {
            return instance;
        }
    }
    // Проблема: Десериализация создаст новый экземпляр

    // ПРАВИЛЬНО: С readResolve методом
    public static class CorrectSerializableSingleton implements Serializable {
        private static final long serialVersionUID = 1L;
        private static CorrectSerializableSingleton instance = new CorrectSerializableSingleton();

        private CorrectSerializableSingleton() {}

        public static CorrectSerializableSingleton getInstance() {
            return instance;
        }

        // Гарантирует единственность при десериализации
        private Object readResolve() {
            return instance;
        }
    }

    // ОШИБКА: Cloneable без clone override
    public static class BrokenCloneableSingleton implements Cloneable {
        private static BrokenCloneableSingleton instance = new BrokenCloneableSingleton();

        private BrokenCloneableSingleton() {}

        public static BrokenCloneableSingleton getInstance() {
            return instance;
        }
    }
    // Проблема: clone() создаст новый экземпляр

    // ПРАВИЛЬНО: Override clone()
    public static class CorrectCloneableSingleton implements Cloneable {
        private static CorrectCloneableSingleton instance = new CorrectCloneableSingleton();

        private CorrectCloneableSingleton() {}

        public static CorrectCloneableSingleton getInstance() {
            return instance;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException("Singleton cannot be cloned");
        }
    }

    // ОШИБКА: Reflection может сломать singleton
    public static class ReflectionVulnerableSingleton {
        private static ReflectionVulnerableSingleton instance = new ReflectionVulnerableSingleton();

        private ReflectionVulnerableSingleton() {
            // Можно обойти через reflection
        }

        public static ReflectionVulnerableSingleton getInstance() {
            return instance;
        }
    }
    // Решение: Использовать enum или проверку в конструкторе
}

```

### 3. Тестирование Singleton

```java
@ExtendWith(MockitoExtension.class)
public class SingletonTest {

    @Test
    void shouldReturnSameInstance() {
        DclSingleton instance1 = DclSingleton.getInstance();
        DclSingleton instance2 = DclSingleton.getInstance();

        assertSame(instance1, instance2, "Should return same instance");
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        AtomicReference<DclSingleton> instanceRef = new AtomicReference<>();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(10);

        Runnable task = () -> {
            try {
                startLatch.await();
                DclSingleton instance = DclSingleton.getInstance();
                instanceRef.compareAndSet(null, instance);
                endLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Запускаем 10 потоков одновременно
        for (int i = 0; i < 10; i++) {
            new Thread(task).start();
        }

        startLatch.countDown();
        endLatch.await();

        // Все потоки должны получить один и тот же экземпляр
        DclSingleton expectedInstance = instanceRef.get();
        for (int i = 0; i < 9; i++) {
            assertSame(expectedInstance, DclSingleton.getInstance());
        }
    }

    @Test
    void shouldHandleExceptionsInConstructor() {
        assertThrows(RuntimeException.class, () -> {
            FailingSingleton.getInstance();
        });

        // Повторный вызов должен бросить то же исключение
        assertThrows(RuntimeException.class, () -> {
            FailingSingleton.getInstance();
        });
    }

    @Test
    void shouldBeSerializable() throws IOException, ClassNotFoundException {
        // Сериализация
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(SerializableSingleton.getInstance());
        oos.close();

        // Десериализация
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        SerializableSingleton deserialized = (SerializableSingleton) ois.readObject();

        assertSame(SerializableSingleton.getInstance(), deserialized);
    }

    // Test classes
    static class FailingSingleton {
        private static volatile FailingSingleton instance;

        private FailingSingleton() {
            throw new RuntimeException("Constructor failed");
        }

        public static FailingSingleton getInstance() {
            FailingSingleton result = instance;

            if (result == null) {
                synchronized (FailingSingleton.class) {
                    result = instance;
                    if (result == null) {
                        instance = result = new FailingSingleton();
                    }
                }
            }

            return result;
        }
    }

    static class SerializableSingleton implements Serializable {
        private static final long serialVersionUID = 1L;
        private static SerializableSingleton instance = new SerializableSingleton();

        private SerializableSingleton() {}

        public static SerializableSingleton getInstance() {
            return instance;
        }

        private Object readResolve() {
            return instance;
        }
    }
}
```

## Заключение

Singleton — один из наиболее важных и широко используемых паттернов в enterprise разработке. Он гарантирует единственность экземпляра и предоставляет глобальную точку доступа.

Ключевые преимущества:
- **Контроль ресурсов**: Гарантирует единственность дорогих ресурсов
- **Глобальный доступ**: Удобный доступ из любой точки приложения
- **Ленивая инициализация**: Создание только при необходимости
- **Thread Safety**: Безопасная работа в многопоточной среде

Используйте Singleton когда:
- Ресурс должен быть уникальным в приложении
- Нужен глобальный доступ к сервису
- Создание объекта дорогое или требует координации
- Важна thread safety

Выбирайте правильную реализацию:
- **Enum**: Для простых случаев с абсолютной гарантией
- **Holder Class**: Для ленивой инициализации без параметров
- **DCL**: Для параметризованных синглтонов с высокой производительностью
- **Synchronized**: Только для простых однопоточных приложений
