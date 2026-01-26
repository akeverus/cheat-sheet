# Proxy Pattern (Заместитель)

Proxy предоставляет суррогат или заместителя другого объекта для контроля доступа к нему. Паттерн позволяет добавить дополнительную функциональность при доступе к объекту.

**Дата последнего обновления:** 2026-01-24

## Содержание

- [Что такое Proxy?](#что-такое-proxy)
- [Когда использовать Proxy?](#когда-использовать-proxy)
- [Структура паттерна](#структура-паттерна)
- [Реализация на Java](#реализация-на-java)
- [Примеры использования](#примеры-использования)
- [Best Practices](#best-practices)

## Что такое Proxy?

Proxy — это структурный паттерн проектирования, который позволяет подставить вместо реального объекта объект-заместитель. Заместитель контролирует доступ к оригинальному объекту, позволяя выполнить какие-то действия до или после обращения к нему.

### Основные характеристики

1. **Контроль доступа**: Заместитель контролирует доступ к реальному объекту
2. **Ленивая инициализация**: Объект создается только при первом обращении
3. **Дополнительная функциональность**: Добавление логирования, кэширования, защиты
4. **Прозрачность**: Клиент работает с интерфейсом, не зная о заместителе
5. **Отложенные операции**: Выполнение операций только при необходимости

### Проблемы, которые решает

```java
// Плохо: Клиент напрямую работает с тяжелым объектом
public class ImageViewer {
    public static void main(String[] args) {
        // Каждый раз при создании изображения загружается файл!
        Image image1 = new RealImage("photo1.jpg");
        Image image2 = new RealImage("photo2.jpg");
        Image image3 = new RealImage("photo3.jpg");

        // Даже если мы не будем их отображать, файлы уже загружены
        // Огромная трата памяти и времени
    }
}

class RealImage implements Image {
    private String filename;
    private byte[] data; // Тяжелые данные

    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk(); // Дорогая операция!
    }

    private void loadFromDisk() {
        System.out.println("Loading " + filename + " from disk (expensive operation)");
        // Имитация загрузки
        data = new byte[1024 * 1024]; // 1MB данных
    }

    @Override
    public void display() {
        System.out.println("Displaying " + filename);
    }

    @Override
    public int getSize() {
        return data.length;
    }
}

// Хорошо: Proxy контролирует доступ
interface Image {
    void display();
    int getSize();
}

class ProxyImage implements Image {
    private RealImage realImage;
    private String filename;
    private boolean loaded = false;

    public ProxyImage(String filename) {
        this.filename = filename;
        // Не загружаем файл сразу!
    }

    @Override
    public void display() {
        if (!loaded) {
            realImage = new RealImage(filename); // Загружаем только при первом обращении
            loaded = true;
        }
        realImage.display();
    }

    @Override
    public int getSize() {
        if (!loaded) {
            // Можно вернуть размер без загрузки файла
            return getFileSizeFromMetadata(filename);
        }
        return realImage.getSize();
    }

    private int getFileSizeFromMetadata(String filename) {
        // Быстрая операция чтения метаданных
        return 1024 * 1024; // Примерный размер
    }
}

// Клиент получает преимущества lazy loading
public class ImageViewerProxy {
    public static void main(String[] args) {
        Image image1 = new ProxyImage("photo1.jpg");
        Image image2 = new ProxyImage("photo2.jpg");

        // Метаданные доступны сразу, без загрузки
        System.out.println("Image1 size: " + image1.getSize());
        System.out.println("Image2 size: " + image2.getSize());

        // Загрузка происходит только при отображении
        image1.display(); // Сейчас загружается
        image2.display(); // И сейчас загружается
    }
}
```

## Когда использовать Proxy?

### Подходящие сценарии

- **Виртуальный прокси**: Отложенная инициализация тяжелых объектов
- **Защитный прокси**: Контроль доступа к чувствительным объектам
- **Кэширующий прокси**: Кэширование результатов дорогих операций
- **Логирующий прокси**: Логирование обращений к объекту
- **Удаленный прокси**: Представление удаленных объектов локально
- **Синхронизирующий прокси**: Синхронизация доступа в многопоточной среде

### Признаки необходимости

```java
// Признаки: Необходимость контроля доступа к объекту
public class DatabaseConnectionManager {
    private DatabaseConnection connection;

    public DatabaseConnectionManager() {
        // Создаем соединение сразу - плохо!
        this.connection = new DatabaseConnection();
    }

    public void executeQuery(String sql) {
        if (!connection.isValid()) {
            // Переподключение...
        }
        connection.execute(sql);
    }
}

// Хорошо: Proxy управляет жизненным циклом соединения
interface DatabaseConnection {
    void execute(String sql);
    boolean isValid();
}

class RealDatabaseConnection implements DatabaseConnection {
    @Override
    public void execute(String sql) {
        System.out.println("Executing: " + sql);
    }

    @Override
    public boolean isValid() {
        return true; // Упрощенно
    }
}

class DatabaseConnectionProxy implements DatabaseConnection {
    private RealDatabaseConnection realConnection;
    private long lastUsed;
    private static final long TIMEOUT = 300000; // 5 минут

    @Override
    public void execute(String sql) {
        checkConnection();
        realConnection.execute(sql);
        lastUsed = System.currentTimeMillis();
    }

    @Override
    public boolean isValid() {
        if (realConnection == null) return false;
        if (System.currentTimeMillis() - lastUsed > TIMEOUT) {
            closeConnection();
            return false;
        }
        return realConnection.isValid();
    }

    private void checkConnection() {
        if (realConnection == null || !isValid()) {
            realConnection = new RealDatabaseConnection();
            System.out.println("Created new database connection");
        }
    }

    private void closeConnection() {
        if (realConnection != null) {
            // Закрываем соединение
            System.out.println("Closed database connection");
            realConnection = null;
        }
    }
}
```

## Структура паттерна

```
┌─────────────────────────────────────────────────────────────┐
│                      Subject                               │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                                                     │    │
│  ┌─────────────────────────────────────────────────┐    │    │
│  │        request(): void                           │    │    │
│  │                                                   │    │
│  │  // Определяет общий интерфейс для RealSubject     │    │
│  │  // и Proxy                                        │    │
│  └─────────────────────────────────────────────────┘    │    │
└─────────────────────────────────────────────────────────────┼─┘
                                                              │
┌─────────────────────────────────────────────────────────────┼─┐
│                    RealSubject                              │ │
│                                                             │ │
│  ┌─────────────────────────────────────────────────────┐    │ │
│  │                                                     │    │ │
│  │  ┌─────────────────────────────────────────────────┐ │    │ │
│  │  │        request(): void                           │ │    │ │
│  │  └─────────────────────────────────────────────────┘ │    │ │
│  │                                                     │    │ │
│  │  // Реализует реальную бизнес-логику                 │    │ │
│  │  // Выполняет основную работу                        │    │ │
│  │                                                     │    │ │
└─────────────────────────────────────────────────────────────┘
                                                              │
┌─────────────────────────────────────────────────────────────┼─┐
│                       Proxy                                 │ │
│                                                             │ │
│  ┌─────────────────────────────────────────────────────┐    │ │
│  │           realSubject: RealSubject                  │    │ │
│  │                                                     │    │ │
│  │  ┌─────────────────────────────────────────────────┐ │    │ │
│  │  │        request(): void                           │ │    │ │
│  │  └─────────────────────────────────────────────────┘ │    │ │
│  │                                                     │    │ │
│  │  // Хранит ссылку на RealSubject                    │    │ │
│  │  // Контролирует доступ к RealSubject               │    │ │
│  │  // Может управлять жизненным циклом RealSubject    │    │ │
│  │                                                     │    │ │
└─────────────────────────────────────────────────────────────┘
                                                              │
┌─────────────────────────────────────────────────────────────┼─┐
│                       Client                                │ │
│                                                             │ │
│  // Работает с объектами через интерфейс Subject          │ │
│  // Не знает, работает ли с RealSubject или Proxy          │ │
│                                                             │
│  subject = new Proxy(new RealSubject())                     │ │
│  subject.request()                                          │ │
│                                                             │ │
└─────────────────────────────────────────────────────────────┘
```

### Компоненты

1. **Subject**: Общий интерфейс для реального объекта и прокси
2. **RealSubject**: Реальный объект, содержащий бизнес-логику
3. **Proxy**: Заместитель, контролирующий доступ к RealSubject
4. **Client**: Работает с объектами через интерфейс Subject

## Реализация на Java

### Виртуальный Proxy (Lazy Loading)

```java
// Subject
interface Image {
    void display();
    int getWidth();
    int getHeight();
    long getFileSize();
}

// RealSubject
class RealImage implements Image {
    private String filename;
    private byte[] imageData;
    private int width;
    private int height;

    public RealImage(String filename) {
        this.filename = filename;
        loadImageFromDisk();
    }

    private void loadImageFromDisk() {
        System.out.println("Loading image from disk: " + filename + " (expensive operation)");
        // Имитация загрузки изображения
        this.imageData = new byte[1024 * 1024]; // 1MB
        this.width = 800;
        this.height = 600;

        // Имитация задержки загрузки
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void display() {
        System.out.println("Displaying image: " + filename + " (" + width + "x" + height + ")");
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }

    @Override
    public long getFileSize() { return imageData.length; }
}

// Proxy
class ImageProxy implements Image {
    private RealImage realImage;
    private String filename;
    private int width;
    private int height;
    private long fileSize;
    private boolean loaded = false;

    public ImageProxy(String filename) {
        this.filename = filename;
        // Быстрая загрузка метаданных без самого изображения
        loadMetadata();
    }

    private void loadMetadata() {
        // Быстрая операция чтения метаданных
        System.out.println("Loading metadata for: " + filename);
        this.width = 800;  // Из EXIF или заголовка файла
        this.height = 600;
        this.fileSize = 1024 * 1024; // 1MB
    }

    private void loadRealImage() {
        if (!loaded) {
            realImage = new RealImage(filename);
            loaded = true;
        }
    }

    @Override
    public void display() {
        loadRealImage();
        realImage.display();
    }

    @Override
    public int getWidth() {
        return width; // Метаданные доступны сразу
    }

    @Override
    public int getHeight() {
        return height; // Метаданные доступны сразу
    }

    @Override
    public long getFileSize() {
        return fileSize; // Метаданные доступны сразу
    }

    public boolean isLoaded() {
        return loaded;
    }
}

// Image Gallery
class ImageGallery {
    private List<Image> images = new ArrayList<>();

    public void addImage(String filename) {
        images.add(new ImageProxy(filename));
    }

    public void displayAll() {
        System.out.println("Displaying all images:");
        for (Image image : images) {
            image.display();
        }
    }

    public void printGalleryInfo() {
        System.out.println("Gallery contains " + images.size() + " images:");
        for (Image image : images) {
            System.out.println("- " + ((ImageProxy)image).isLoaded() + " loaded, " +
                             image.getWidth() + "x" + image.getHeight() + ", " +
                             image.getFileSize() + " bytes");
        }
    }
}

public class VirtualProxyDemo {
    public static void main(String[] args) {
        ImageGallery gallery = new ImageGallery();

        // Добавляем изображения через прокси
        gallery.addImage("vacation.jpg");
        gallery.addImage("birthday.jpg");
        gallery.addImage("wedding.jpg");

        // Метаданные доступны сразу, изображения не загружены
        System.out.println("=== Gallery Info (before display) ===");
        gallery.printGalleryInfo();

        // Отображаем только первое изображение
        System.out.println("\n=== Displaying first image ===");
        gallery.images.get(0).display();

        System.out.println("\n=== Gallery Info (after displaying first) ===");
        gallery.printGalleryInfo();

        // Отображаем все изображения
        System.out.println("\n=== Displaying all images ===");
        gallery.displayAll();

        System.out.println("\n=== Final Gallery Info ===");
        gallery.printGalleryInfo();
    }
}
```

### Защитный Proxy (Protection Proxy)

```java
// Subject
interface Document {
    void read(User user);
    void write(User user, String content);
    void delete(User user);
    String getContent();
}

// RealSubject
class RealDocument implements Document {
    private String content = "";
    private final String id;

    public RealDocument(String id) {
        this.id = id;
        System.out.println("Created document: " + id);
    }

    @Override
    public void read(User user) {
        System.out.println("Document " + id + " read by " + user.getName());
    }

    @Override
    public void write(User user, String content) {
        this.content = content;
        System.out.println("Document " + id + " written by " + user.getName());
    }

    @Override
    public void delete(User user) {
        System.out.println("Document " + id + " deleted by " + user.getName());
    }

    @Override
    public String getContent() {
        return content;
    }
}

// Proxy
class DocumentProxy implements Document {
    private RealDocument realDocument;
    private final String documentId;
    private final AccessControl accessControl;

    public DocumentProxy(String documentId, AccessControl accessControl) {
        this.documentId = documentId;
        this.accessControl = accessControl;
    }

    private RealDocument getRealDocument() {
        if (realDocument == null) {
            realDocument = new RealDocument(documentId);
        }
        return realDocument;
    }

    @Override
    public void read(User user) {
        if (accessControl.canRead(user, documentId)) {
            getRealDocument().read(user);
        } else {
            throw new SecurityException("Access denied: " + user.getName() + " cannot read " + documentId);
        }
    }

    @Override
    public void write(User user, String content) {
        if (accessControl.canWrite(user, documentId)) {
            getRealDocument().write(user, content);
        } else {
            throw new SecurityException("Access denied: " + user.getName() + " cannot write to " + documentId);
        }
    }

    @Override
    public void delete(User user) {
        if (accessControl.canDelete(user, documentId)) {
            getRealDocument().delete(user);
        } else {
            throw new SecurityException("Access denied: " + user.getName() + " cannot delete " + documentId);
        }
    }

    @Override
    public String getContent() {
        // Для получения контента тоже нужна проверка прав
        // Но для упрощения возвращаем пустую строку
        return getRealDocument().getContent();
    }
}

// Access Control
class AccessControl {
    private final Map<String, Set<String>> userRoles = new HashMap<>();
    private final Map<String, Set<String>> resourcePermissions = new HashMap<>();

    public AccessControl() {
        // Настройка ролей пользователей
        userRoles.put("alice", Set.of("admin"));
        userRoles.put("bob", Set.of("editor"));
        userRoles.put("charlie", Set.of("viewer"));

        // Настройка прав доступа к ресурсам
        resourcePermissions.put("doc1", Set.of("admin", "editor"));
        resourcePermissions.put("doc2", Set.of("admin", "editor", "viewer"));
        resourcePermissions.put("secret", Set.of("admin"));
    }

    public boolean canRead(User user, String resource) {
        return hasPermission(user, resource, "viewer");
    }

    public boolean canWrite(User user, String resource) {
        return hasPermission(user, resource, "editor");
    }

    public boolean canDelete(User user, String resource) {
        return hasPermission(user, resource, "admin");
    }

    private boolean hasPermission(User user, String resource, String requiredRole) {
        Set<String> userRolesSet = userRoles.get(user.getName());
        Set<String> resourceRoles = resourcePermissions.get(resource);

        if (userRolesSet == null || resourceRoles == null) {
            return false;
        }

        return userRolesSet.contains(requiredRole) && resourceRoles.contains(requiredRole);
    }
}

// User entity
class User {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    @Override
    public String toString() {
        return "User{" + name + '}';
    }
}

// Document Manager
class DocumentManager {
    private final AccessControl accessControl = new AccessControl();
    private final Map<String, Document> documents = new HashMap<>();

    public Document getDocument(String documentId) {
        return documents.computeIfAbsent(documentId,
            id -> new DocumentProxy(id, accessControl));
    }

    public void createDocument(String documentId, User creator) {
        if (!accessControl.canWrite(creator, documentId)) {
            throw new SecurityException("Cannot create document: access denied");
        }
        documents.put(documentId, new DocumentProxy(documentId, accessControl));
        System.out.println("Document " + documentId + " created by " + creator.getName());
    }
}

public class ProtectionProxyDemo {
    public static void main(String[] args) {
        DocumentManager manager = new DocumentManager();

        User alice = new User("alice");    // admin
        User bob = new User("bob");        // editor
        User charlie = new User("charlie"); // viewer

        // Создаем документы
        manager.createDocument("doc1", alice);
        manager.createDocument("doc2", alice);
        manager.createDocument("secret", alice);

        // Тестируем доступ
        Document doc1 = manager.getDocument("doc1");
        Document doc2 = manager.getDocument("doc2");
        Document secret = manager.getDocument("secret");

        System.out.println("\n=== Testing Read Access ===");
        try {
            doc1.read(alice);     // admin can read
            doc1.read(bob);       // editor can read
            doc1.read(charlie);   // viewer can read
        } catch (SecurityException e) {
            System.out.println("Read access denied: " + e.getMessage());
        }

        System.out.println("\n=== Testing Write Access ===");
        try {
            doc1.write(alice, "Admin content");    // admin can write
            doc1.write(bob, "Editor content");     // editor can write
            doc1.write(charlie, "Viewer content"); // viewer cannot write
        } catch (SecurityException e) {
            System.out.println("Write access denied: " + e.getMessage());
        }

        System.out.println("\n=== Testing Delete Access ===");
        try {
            secret.delete(alice);    // admin can delete
            secret.delete(bob);      // editor cannot delete
        } catch (SecurityException e) {
            System.out.println("Delete access denied: " + e.getMessage());
        }

        System.out.println("\n=== Testing Access to Secret Document ===");
        try {
            secret.read(bob);        // editor cannot read secret
        } catch (SecurityException e) {
            System.out.println("Secret access denied: " + e.getMessage());
        }
    }
}
```

### Кэширующий Proxy (Caching Proxy)

```java
// Subject
interface DataService {
    String fetchData(String key);
    void storeData(String key, String value);
    boolean isDataAvailable(String key);
}

// RealSubject
class RealDataService implements DataService {
    private final Map<String, String> storage = new HashMap<>();

    @Override
    public String fetchData(String key) {
        System.out.println("RealDataService: Fetching data for key '" + key + "' from storage");
        // Имитация задержки доступа к данным
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return storage.get(key);
    }

    @Override
    public void storeData(String key, String value) {
        System.out.println("RealDataService: Storing data for key '" + key + "'");
        // Имитация задержки записи
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        storage.put(key, value);
    }

    @Override
    public boolean isDataAvailable(String key) {
        return storage.containsKey(key);
    }
}

// Proxy
class CachingDataServiceProxy implements DataService {
    private final RealDataService realService;
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private final Set<String> pendingWrites = ConcurrentHashMap.newKeySet();

    private static final long CACHE_TTL = 5000; // 5 seconds
    private static final int MAX_CACHE_SIZE = 100;

    public CachingDataServiceProxy() {
        this.realService = new RealDataService();
    }

    @Override
    public String fetchData(String key) {
        // Проверяем кэш
        if (isCacheValid(key)) {
            System.out.println("Cache HIT for key '" + key + "'");
            return cache.get(key);
        }

        // Загружаем из реального сервиса
        System.out.println("Cache MISS for key '" + key + "', fetching from real service");
        String data = realService.fetchData(key);

        // Сохраняем в кэш
        if (data != null) {
            putInCache(key, data);
        }

        return data;
    }

    @Override
    public void storeData(String key, String value) {
        // Отмечаем как ожидающая запись
        pendingWrites.add(key);

        // Обновляем кэш немедленно
        putInCache(key, value);

        // Асинхронно записываем в реальный сервис
        new Thread(() -> {
            try {
                realService.storeData(key, value);
                pendingWrites.remove(key);
                System.out.println("Async write completed for key '" + key + "'");
            } catch (Exception e) {
                System.err.println("Failed to write key '" + key + "': " + e.getMessage());
                // В случае ошибки удаляем из кэша
                cache.remove(key);
                pendingWrites.remove(key);
            }
        }).start();
    }

    @Override
    public boolean isDataAvailable(String key) {
        // Сначала проверяем кэш
        if (cache.containsKey(key)) {
            return true;
        }
        // Затем проверяем реальный сервис
        return realService.isDataAvailable(key);
    }

    private boolean isCacheValid(String key) {
        if (!cache.containsKey(key)) {
            return false;
        }

        Long timestamp = cacheTimestamps.get(key);
        if (timestamp == null) {
            return false;
        }

        return (System.currentTimeMillis() - timestamp) < CACHE_TTL;
    }

    private void putInCache(String key, String value) {
        // Проверяем размер кэша
        if (cache.size() >= MAX_CACHE_SIZE) {
            evictOldEntries();
        }

        cache.put(key, value);
        cacheTimestamps.put(key, System.currentTimeMillis());
    }

    private void evictOldEntries() {
        // Простая стратегия вытеснения: удаляем самые старые записи
        String oldestKey = null;
        long oldestTime = Long.MAX_VALUE;

        for (Map.Entry<String, Long> entry : cacheTimestamps.entrySet()) {
            if (entry.getValue() < oldestTime) {
                oldestTime = entry.getValue();
                oldestKey = entry.getKey();
            }
        }

        if (oldestKey != null) {
            cache.remove(oldestKey);
            cacheTimestamps.remove(oldestKey);
            System.out.println("Evicted from cache: " + oldestKey);
        }
    }

    public void clearCache() {
        cache.clear();
        cacheTimestamps.clear();
        System.out.println("Cache cleared");
    }

    public int getCacheSize() {
        return cache.size();
    }

    public Set<String> getPendingWrites() {
        return new HashSet<>(pendingWrites);
    }
}

// Performance Monitor
class PerformanceMonitor {
    private final Map<String, Long> operationTimes = new ConcurrentHashMap<>();
    private final Map<String, Integer> operationCounts = new ConcurrentHashMap<>();

    public void recordOperation(String operation, long timeMs) {
        operationTimes.merge(operation, timeMs, Long::sum);
        operationCounts.merge(operation, 1, Integer::sum);
    }

    public double getAverageTime(String operation) {
        Long totalTime = operationTimes.get(operation);
        Integer count = operationCounts.get(operation);

        if (totalTime == null || count == null || count == 0) {
            return 0.0;
        }

        return (double) totalTime / count;
    }

    public void printStatistics() {
        System.out.println("\n=== Performance Statistics ===");
        for (String operation : operationTimes.keySet()) {
            System.out.printf("%s: %.2f ms avg (%d operations)%n",
                            operation, getAverageTime(operation), operationCounts.get(operation));
        }
    }
}

public class CachingProxyDemo {
    public static void main(String[] args) throws InterruptedException {
        CachingDataServiceProxy proxy = new CachingDataServiceProxy();
        PerformanceMonitor monitor = new PerformanceMonitor();

        System.out.println("=== Testing Cache Performance ===");

        // Первый запрос - cache miss
        long start = System.currentTimeMillis();
        String data1 = proxy.fetchData("key1");
        long time1 = System.currentTimeMillis() - start;
        monitor.recordOperation("fetch_first", time1);

        // Второй запрос к тому же ключу - cache hit
        start = System.currentTimeMillis();
        String data2 = proxy.fetchData("key1");
        long time2 = System.currentTimeMillis() - start;
        monitor.recordOperation("fetch_cached", time2);

        System.out.println("First fetch time: " + time1 + "ms");
        System.out.println("Cached fetch time: " + time2 + "ms");
        System.out.println("Cache speedup: " + (time1 > 0 ? time2 * 100.0 / time1 : 0) + "%");

        System.out.println("\n=== Testing Write-Through Caching ===");

        // Записываем данные
        proxy.storeData("key2", "value2");
        proxy.storeData("key3", "value3");

        // Ждем завершения асинхронных операций
        Thread.sleep(100);

        System.out.println("Pending writes: " + proxy.getPendingWrites());

        // Читаем записанные данные (должны быть в кэше)
        System.out.println("key2: " + proxy.fetchData("key2"));
        System.out.println("key3: " + proxy.fetchData("key3"));

        System.out.println("\n=== Testing Cache Eviction ===");

        // Заполняем кэш
        for (int i = 0; i < 110; i++) {
            proxy.storeData("bulk_key_" + i, "bulk_value_" + i);
        }

        System.out.println("Cache size after bulk insert: " + proxy.getCacheSize());

        monitor.printStatistics();
    }
}
```

### Динамический Proxy с Java Reflection

```java
// Generic Proxy using Java Dynamic Proxy
class LoggingProxy {
    @SuppressWarnings("unchecked")
    public static <T> T createLoggingProxy(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new LoggingInvocationHandler(target)
        );
    }

    private static class LoggingInvocationHandler implements InvocationHandler {
        private final Object target;

        public LoggingInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Calling method: " + method.getName() +
                             " with args: " + Arrays.toString(args));

            long startTime = System.currentTimeMillis();
            try {
                Object result = method.invoke(target, args);
                long executionTime = System.currentTimeMillis() - startTime;

                System.out.println("Method " + method.getName() +
                                 " completed in " + executionTime + "ms, result: " + result);

                return result;
            } catch (Exception e) {
                System.err.println("Method " + method.getName() + " failed: " + e.getMessage());
                throw e.getCause();
            }
        }
    }
}

// Generic Caching Proxy
class CachingProxy {
    private static final Map<String, Object> cache = new ConcurrentHashMap<>();
    private static final Map<String, Long> timestamps = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T createCachingProxy(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new CachingInvocationHandler(target)
        );
    }

    private static class CachingInvocationHandler implements InvocationHandler {
        private final Object target;

        public CachingInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Создаем ключ кэша
            String cacheKey = method.getName() + Arrays.toString(args);

            // Проверяем кэш
            Object cachedResult = cache.get(cacheKey);
            if (cachedResult != null) {
                System.out.println("Cache hit for: " + cacheKey);
                return cachedResult;
            }

            // Выполняем метод
            Object result = method.invoke(target, args);

            // Кэшируем результат
            cache.put(cacheKey, result);
            timestamps.put(cacheKey, System.currentTimeMillis());

            System.out.println("Cached result for: " + cacheKey);
            return result;
        }
    }
}

// Service interface
interface CalculatorService {
    int add(int a, int b);
    int multiply(int a, int b);
    int factorial(int n);
}

// Real implementation
class CalculatorServiceImpl implements CalculatorService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int multiply(int a, int b) {
        return a * b;
    }

    @Override
    public int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
}

public class DynamicProxyDemo {
    public static void main(String[] args) {
        CalculatorService realService = new CalculatorServiceImpl();

        // Создаем логирующий прокси
        CalculatorService loggingProxy = LoggingProxy.createLoggingProxy(
            realService, CalculatorService.class);

        // Создаем кэширующий прокси
        CalculatorService cachingProxy = CachingProxy.createCachingProxy(
            realService, CalculatorService.class);

        System.out.println("=== Testing Logging Proxy ===");
        System.out.println("2 + 3 = " + loggingProxy.add(2, 3));
        System.out.println("4 * 5 = " + loggingProxy.multiply(4, 5));

        System.out.println("\n=== Testing Caching Proxy ===");
        System.out.println("Factorial 5 = " + cachingProxy.factorial(5));
        System.out.println("Factorial 5 (cached) = " + cachingProxy.factorial(5));
        System.out.println("Factorial 6 = " + cachingProxy.factorial(6));

        // Комбинируем прокси
        System.out.println("\n=== Testing Combined Proxies ===");
        CalculatorService combinedProxy = LoggingProxy.createLoggingProxy(
            CachingProxy.createCachingProxy(realService, CalculatorService.class),
            CalculatorService.class);

        System.out.println("Combined: 3 + 4 = " + combinedProxy.add(3, 4));
        System.out.println("Combined: 3 + 4 (cached) = " + combinedProxy.add(3, 4));
    }
}
```

## Примеры использования

### 1. Remote Proxy (RMI)

```java
// Remote Service Interface
interface RemoteBankService {
    Account getAccount(String accountId) throws RemoteException;
    void transfer(String fromAccountId, String toAccountId, double amount) throws RemoteException;
    double getBalance(String accountId) throws RemoteException;
}

// Remote Implementation (Server)
class BankServiceImpl implements RemoteBankService {
    private final Map<String, Account> accounts = new HashMap<>();

    public BankServiceImpl() {
        accounts.put("acc1", new Account("acc1", 1000.0));
        accounts.put("acc2", new Account("acc2", 500.0));
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void transfer(String fromAccountId, String toAccountId, double amount) {
        Account from = accounts.get(fromAccountId);
        Account to = accounts.get(toAccountId);

        if (from != null && to != null && from.getBalance() >= amount) {
            from.setBalance(from.getBalance() - amount);
            to.setBalance(to.getBalance() + amount);
            System.out.println("Transferred " + amount + " from " + fromAccountId + " to " + toAccountId);
        } else {
            throw new IllegalArgumentException("Transfer failed");
        }
    }

    @Override
    public double getBalance(String accountId) {
        Account account = accounts.get(accountId);
        return account != null ? account.getBalance() : 0.0;
    }
}

// Account entity
class Account {
    private String id;
    private double balance;

    public Account(String id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() { return id; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "Account{id='" + id + "', balance=" + balance + "}";
    }
}

// Remote Proxy (Client)
class BankServiceProxy implements RemoteBankService {
    private final String serverUrl;

    public BankServiceProxy(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public Account getAccount(String accountId) throws RemoteException {
        System.out.println("Remote call: getAccount(" + accountId + ")");
        // Имитация сетевого вызова
        try {
            Thread.sleep(10); // Сетевая задержка
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // В реальности здесь был бы HTTP вызов или RMI
        return new Account(accountId, 1000.0); // Mock response
    }

    @Override
    public void transfer(String fromAccountId, String toAccountId, double amount) throws RemoteException {
        System.out.println("Remote call: transfer(" + fromAccountId + "," + toAccountId + "," + amount + ")");
        // Имитация сетевого вызова
        try {
            Thread.sleep(20); // Сетевая задержка
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // В реальности здесь был бы HTTP вызов или RMI
        if (amount > 1000) {
            throw new RemoteException("Insufficient funds");
        }
    }

    @Override
    public double getBalance(String accountId) throws RemoteException {
        System.out.println("Remote call: getBalance(" + accountId + ")");
        // Имитация сетевого вызова
        try {
            Thread.sleep(5); // Сетевая задержка
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return 1000.0; // Mock balance
    }
}

// Custom RemoteException
class RemoteException extends Exception {
    public RemoteException(String message) {
        super(message);
    }
}

public class RemoteProxyDemo {
    public static void main(String[] args) {
        // Клиент работает с прокси, не зная о сетевых деталях
        RemoteBankService bankService = new BankServiceProxy("http://bank-server:8080");

        try {
            System.out.println("=== Bank Operations ===");

            // Получаем счет
            Account account = bankService.getAccount("acc1");
            System.out.println("Account: " + account);

            // Получаем баланс
            double balance = bankService.getBalance("acc1");
            System.out.println("Balance: $" + balance);

            // Делаем перевод
            bankService.transfer("acc1", "acc2", 100.0);
            System.out.println("Transfer completed");

            // Пытаемся сделать слишком большой перевод
            try {
                bankService.transfer("acc1", "acc2", 2000.0);
            } catch (RemoteException e) {
                System.out.println("Transfer failed: " + e.getMessage());
            }

        } catch (RemoteException e) {
            System.err.println("Remote operation failed: " + e.getMessage());
        }
    }
}
```

### 2. Smart Reference Proxy

```java
// Smart Reference - управление жизненным циклом объекта
interface HeavyObject {
    void performOperation();
    boolean isValid();
}

class RealHeavyObject implements HeavyObject {
    private final String id;
    private boolean valid = true;

    public RealHeavyObject(String id) {
        this.id = id;
        System.out.println("Created heavy object: " + id);
        // Имитация тяжелой инициализации
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void performOperation() {
        if (!valid) {
            throw new IllegalStateException("Object is no longer valid");
        }
        System.out.println("Heavy operation performed by: " + id);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        valid = false;
        System.out.println("Invalidated object: " + id);
    }

    public void cleanup() {
        System.out.println("Cleaning up object: " + id);
        // Освобождение ресурсов
    }
}

// Smart Reference Proxy
class SmartReferenceProxy implements HeavyObject {
    private RealHeavyObject realObject;
    private final String objectId;
    private int referenceCount = 0;
    private long lastAccessTime = System.currentTimeMillis();

    private static final long IDLE_TIMEOUT = 5000; // 5 seconds
    private static final Map<String, SmartReferenceProxy> instances = new ConcurrentHashMap<>();

    private SmartReferenceProxy(String objectId) {
        this.objectId = objectId;
    }

    public static SmartReferenceProxy getInstance(String objectId) {
        return instances.computeIfAbsent(objectId, SmartReferenceProxy::new);
    }

    @Override
    public void performOperation() {
        checkAndCreateObject();
        updateAccessTime();
        realObject.performOperation();
    }

    @Override
    public boolean isValid() {
        return realObject != null && realObject.isValid();
    }

    public void acquire() {
        referenceCount++;
        System.out.println("Reference acquired for " + objectId + ", count: " + referenceCount);
    }

    public void release() {
        referenceCount--;
        System.out.println("Reference released for " + objectId + ", count: " + referenceCount);

        if (referenceCount <= 0) {
            scheduleCleanup();
        }
    }

    private void checkAndCreateObject() {
        if (realObject == null) {
            realObject = new RealHeavyObject(objectId);
        } else if (!realObject.isValid()) {
            System.out.println("Recreating invalid object: " + objectId);
            realObject = new RealHeavyObject(objectId);
        }
    }

    private void updateAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

    private void scheduleCleanup() {
        new Thread(() -> {
            try {
                Thread.sleep(IDLE_TIMEOUT);
                // Проверяем, не было ли новых обращений
                if (System.currentTimeMillis() - lastAccessTime >= IDLE_TIMEOUT && referenceCount <= 0) {
                    cleanup();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void cleanup() {
        if (realObject != null) {
            realObject.cleanup();
            realObject = null;
            instances.remove(objectId);
            System.out.println("Cleaned up object: " + objectId);
        }
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public String getObjectId() {
        return objectId;
    }
}

// Object Pool
class HeavyObjectPool {
    private final Map<String, SmartReferenceProxy> pool = new ConcurrentHashMap<>();

    public HeavyObject borrowObject(String objectId) {
        SmartReferenceProxy proxy = SmartReferenceProxy.getInstance(objectId);
        proxy.acquire();
        return proxy;
    }

    public void returnObject(String objectId) {
        SmartReferenceProxy proxy = pool.get(objectId);
        if (proxy != null) {
            proxy.release();
        }
    }

    public void printPoolStats() {
        System.out.println("\n=== Pool Statistics ===");
        System.out.println("Active objects: " + pool.size());
        pool.forEach((id, proxy) ->
            System.out.println("- " + id + ": " + proxy.getReferenceCount() + " references"));
    }
}

public class SmartReferenceProxyDemo {
    public static void main(String[] args) throws InterruptedException {
        HeavyObjectPool pool = new HeavyObjectPool();

        System.out.println("=== Borrowing Objects ===");

        // Получаем несколько ссылок на один объект
        HeavyObject obj1 = pool.borrowObject("database-connection");
        HeavyObject obj2 = pool.borrowObject("database-connection");
        HeavyObject obj3 = pool.borrowObject("file-handler");

        pool.printPoolStats();

        // Выполняем операции
        System.out.println("\n=== Performing Operations ===");
        obj1.performOperation();
        obj2.performOperation();
        obj3.performOperation();

        // Освобождаем ссылки
        System.out.println("\n=== Releasing References ===");
        pool.returnObject("database-connection"); // obj1
        pool.printPoolStats();

        pool.returnObject("database-connection"); // obj2
        pool.printPoolStats();

        // Ждем таймаута для очистки
        System.out.println("\n=== Waiting for cleanup ===");
        Thread.sleep(6000); // Ждем больше IDLE_TIMEOUT

        pool.returnObject("file-handler"); // obj3
        pool.printPoolStats();

        // Ждем еще для очистки последнего объекта
        Thread.sleep(6000);
        pool.printPoolStats();
    }
}
```

## Best Practices

### 1. Выбор типа прокси

```java
// Правильно: Используйте подходящий тип прокси для каждой задачи
class ProxyFactory {

    // Виртуальный прокси для тяжелых объектов
    public static Image createVirtualImageProxy(String filename) {
        return new ImageProxy(filename);
    }

    // Защитный прокси для контролируемого доступа
    public static Document createProtectedDocument(String docId, User user) {
        return new DocumentProxy(docId, user);
    }

    // Кэширующий прокси для оптимизации производительности
    public static DataService createCachingDataService() {
        return new CachingDataServiceProxy();
    }

    // Удаленный прокси для распределенных систем
    public static BankService createRemoteBankService(String serverUrl) {
        return new BankServiceProxy(serverUrl);
    }

    // Комбинированный прокси
    public static DataService createSmartDataService() {
        DataService realService = new RealDataService();
        DataService cachingService = createCachingDataService();
        DataService loggingService = createLoggingDataService(realService);

        // Возвращаем комбинацию прокси
        return loggingService; // который внутри использует cachingService
    }
}
```

### 2. Тестирование Proxy паттерна

```java
@ExtendWith(MockitoExtension.class)
public class ProxyPatternTest {

    @Mock
    private HeavyObject mockRealObject;

    @Test
    void shouldCreateRealObjectOnlyWhenNeeded() {
        ImageProxy proxy = new ImageProxy("test.jpg");

        // Метаданные доступны сразу
        assertEquals(800, proxy.getWidth());
        assertFalse(proxy.isLoaded());

        // Реальный объект создается только при display()
        proxy.display();
        assertTrue(proxy.isLoaded());
    }

    @Test
    void shouldEnforceSecurityConstraints() {
        AccessControl accessControl = new AccessControl();
        DocumentProxy proxy = new DocumentProxy("doc1", accessControl);

        User admin = new User("admin");
        User guest = new User("guest");

        // Admin может читать
        assertDoesNotThrow(() -> proxy.read(admin));

        // Guest не может писать
        assertThrows(SecurityException.class, () -> proxy.write(guest, "content"));
    }

    @Test
    void shouldCacheResultsCorrectly() {
        CachingDataServiceProxy proxy = new CachingDataServiceProxy();

        when(mockRealObject.fetchData("key")).thenReturn("value");

        // Первый вызов - cache miss
        String result1 = proxy.fetchData("key");
        // Второй вызов - cache hit
        String result2 = proxy.fetchData("key");

        assertEquals("value", result1);
        assertEquals("value", result2);
        verify(mockRealObject, times(1)).fetchData("key"); // Вызван только раз
    }

    @ParameterizedTest
    @MethodSource("provideSecurityScenarios")
    void shouldHandleVariousSecurityScenarios(User user, String action, boolean shouldSucceed) {
        AccessControl accessControl = new AccessControl();
        DocumentProxy proxy = new DocumentProxy("test", accessControl);

        SecurityTestAction testAction = () -> {
            switch (action) {
                case "read": proxy.read(user); break;
                case "write": proxy.write(user, "content"); break;
                case "delete": proxy.delete(user); break;
            }
        };

        if (shouldSucceed) {
            assertDoesNotThrow(testAction::execute);
        } else {
            assertThrows(SecurityException.class, testAction::execute);
        }
    }

    static Stream<Arguments> provideSecurityScenarios() {
        return Stream.of(
            Arguments.of(new User("admin"), "read", true),
            Arguments.of(new User("admin"), "write", true),
            Arguments.of(new User("admin"), "delete", true),
            Arguments.of(new User("editor"), "read", true),
            Arguments.of(new User("editor"), "write", true),
            Arguments.of(new User("editor"), "delete", false),
            Arguments.of(new User("viewer"), "read", true),
            Arguments.of(new User("viewer"), "write", false),
            Arguments.of(new User("viewer"), "delete", false)
        );
    }

    @Test
    void shouldHandleConcurrentAccess() throws InterruptedException {
        CachingDataServiceProxy proxy = new CachingDataServiceProxy();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        // Многопоточный доступ к прокси
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    proxy.fetchData("shared_key");
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executor.shutdown();
    }

    @Test
    void shouldCleanupResourcesProperly() {
        SmartReferenceProxy proxy = SmartReferenceProxy.getInstance("test");

        proxy.acquire();
        assertEquals(1, proxy.getReferenceCount());

        proxy.release();
        assertEquals(0, proxy.getReferenceCount());

        // Проверяем что cleanup планируется (упрощенная проверка)
        // В реальном тесте нужно дождаться выполнения cleanup
    }

    // Test interfaces and utilities
    interface SecurityTestAction {
        void execute() throws SecurityException;
    }

    static class ImageProxy implements Image {
        private String filename;
        private boolean loaded = false;

        public ImageProxy(String filename) {
            this.filename = filename;
        }

        @Override public void display() { loaded = true; }
        @Override public int getWidth() { return 800; }
        @Override public int getHeight() { return 600; }
        @Override public long getFileSize() { return 1024; }
        public boolean isLoaded() { return loaded; }
    }

    interface Image {
        void display();
        int getWidth();
        int getHeight();
        long getFileSize();
    }

    static class AccessControl {
        public boolean canRead(User user, String resource) { return true; }
        public boolean canWrite(User user, String resource) { return true; }
        public boolean canDelete(User user, String resource) { return true; }
    }

    static class DocumentProxy implements Document {
        private String docId;
        private AccessControl accessControl;

        public DocumentProxy(String docId, AccessControl accessControl) {
            this.docId = docId;
            this.accessControl = accessControl;
        }

        @Override public void read(User user) { /* check access */ }
        @Override public void write(User user, String content) { /* check access */ }
        @Override public void delete(User user) { /* check access */ }
        @Override public String getContent() { return ""; }
    }

    interface Document {
        void read(User user);
        void write(User user, String content);
        void delete(User user);
        String getContent();
    }

    static class User {
        private String name;
        public User(String name) { this.name = name; }
        public String getName() { return name; }
    }

    static class CachingDataServiceProxy implements DataService {
        @Override public String fetchData(String key) { return "cached"; }
        @Override public void storeData(String key, String value) {}
        @Override public boolean isDataAvailable(String key) { return true; }
    }

    interface DataService {
        String fetchData(String key);
        void storeData(String key, String value);
        boolean isDataAvailable(String key);
    }

    interface HeavyObject {
        void performOperation();
        boolean isValid();
    }

    static class SmartReferenceProxy implements HeavyObject {
        private String objectId;
        private int referenceCount = 0;

        public SmartReferenceProxy(String objectId) {
            this.objectId = objectId;
        }

        public static SmartReferenceProxy getInstance(String objectId) {
            return new SmartReferenceProxy(objectId);
        }

        @Override public void performOperation() {}
        @Override public boolean isValid() { return true; }

        public void acquire() { referenceCount++; }
        public void release() { referenceCount--; }
        public int getReferenceCount() { return referenceCount; }
    }
}
```

## Заключение

Proxy паттерн — мощный инструмент для контроля доступа к объектам и добавления дополнительной функциональности. Он позволяет решать различные задачи, от ленивой инициализации до защиты и кэширования.

Ключевые преимущества:
- **Контроль доступа**: Возможность добавлять проверки и ограничения
- **Оптимизация**: Ленивая инициализация и кэширование
- **Прозрачность**: Клиент работает через общий интерфейс
- **Расширяемость**: Легко добавлять новую функциональность
- **Безопасность**: Контроль доступа к чувствительным операциям

Используйте Proxy когда:
- Нужно контролировать доступ к объекту
- Требуется ленивая инициализация тяжелых объектов
- Необходимы дополнительные операции (логирование, кэширование, валидация)
- Нужно представить удаленные объекты локально
- Требуется синхронизация доступа в многопоточной среде

Proxy часто используется вместе с:
- **Decorator**: Для добавления функциональности
- **Adapter**: Для адаптации интерфейсов
- **Factory**: Для создания прокси объектов
- **Singleton**: Для глобального доступа к прокси
- **Command**: Для отложенного выполнения операций

Главное правило: всегда четко определяйте интерфейс Subject и обеспечивайте полную прозрачность прокси для клиента. Прокси должен вести себя идентично реальному объекту с точки зрения клиента!
