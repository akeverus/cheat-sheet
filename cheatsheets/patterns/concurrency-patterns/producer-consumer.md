---
title: "Производитель-потребитель (Producer-Consumer)"
description: "Паттерн Producer-Consumer разделяет задачи между потоками-производителями и потоками-потребителями, используя общую очередь для обмена данными."
tags:
  - patterns
  - concurrency-patterns
  - producer-consumer
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Производитель-потребитель (`Producer-Consumer`)

Паттерн **Producer-Consumer** разделяет задачи между потоками-производителями и потоками-потребителями, используя общую очередь для обмена данными.

## Полезные ссылки

### Официальная документация
- [Java BlockingQueue](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)
- [Java Concurrency in Practice](https://jcip.net/) — книга по многопоточности

### См. также
- [[java-concurrency-basics|Java Concurrency]] — **Java Concurrency**
- [[rabbitmq|RabbitMQ]] — **Message Queues**
- [[kafka|Kafka]] — **Event Streaming**

## Содержание

- [Суть и запомнить](#суть-и-запомнить)
- [Что такое Producer-Consumer?](#что-такое-producer-consumer)
  - [Основные характеристики](#основные-характеристики)
- [Когда использовать Producer-Consumer?](#когда-использовать-producer-consumer)
  - [Подходящие сценарии](#подходящие-сценарии)
  - [Признаки необходимости](#признаки-необходимости)
- [Структура паттерна](#структура-паттерна)
  - [Компоненты](#компоненты)
- [Реализация на Java](#реализация-на-java)
  - [Базовая реализация с BlockingQueue](#базовая-реализация-с-blockingqueue)
  - [Реализация с ExecutorService](#реализация-с-executorservice)
- [Продвинутые реализации](#продвинутые-реализации)
  - [1. С приоритетами](#1-с-приоритетами)
  - [2. С Circuit Breaker](#2-с-circuit-breaker)
  - [3. С Batch Processing](#3-с-batch-processing)
- [Примеры использования](#примеры-использования)
  - [1. Web Server Request Processing](#1-web-server-request-processing)
  - [2. Log Processing Pipeline](#2-log-processing-pipeline)
  - [3. Image Processing Queue](#3-image-processing-queue)
- [Лучшие практики](#лучшие-практики)
  - [1. Выбор правильного типа очереди](#1-выбор-правильного-типа-очереди)
  - [2. Обработка ошибок](#2-обработка-ошибок)
  - [3. Monitoring и Metrics](#3-monitoring-и-metrics)
  - [4. Testing](#4-testing)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Суть и запомнить

**Суть в одном предложении:** Производители кладут задачи в общую очередь, потребители забирают и обрабатывают; очередь снимает необходимость жёсткой связки и синхронизирует потоки.

**Запомнить:**
- BlockingQueue — потокобезопасная очередь; put/take блокируют при переполнении/пустоте.
- Разделение «кто производит» и «кто потребляет»; масштабирование по отдельности.
- В Java: BlockingQueue, ExecutorService с очередью.

**Когда применять:** очереди задач, пайплайны обработки, асинхронная доставка сообщений.

## Что такое **Producer-Consumer**?

**Producer-Consumer** — это паттерн конкурентного программирования, где одни потоки(производители) генерируют данные или задачи, а другие потоки (потребители) обрабатывают их. Обмен происходит через разделяемую очередь.

### Основные характеристики

1. **Разделение ответственности**: Производители не знают о потребителях и наоборот
2. **Буферизация**: Очередь выступает в роли буфера между производителями и потребителями
3. **Асинхронность**: Производители и потребители работают независимо
4. **Thread Safety**: Безопасная работа в многопоточной среде

## Когда использовать **Producer-Consumer**?

### Подходящие сценарии

- **Обработка задач**: **Web** сервер принимает запросы и делегирует их обработку
- **Data Pipeline**: **ETL** процессы, обработка больших объемов данных
- **Event Processing**: Система обработки событий, логирования
- **Resource Pooling**: Управление соединениями к базе данных

### Признаки необходимости

Сравнение синхронной обработки и **Producer-Consumer** с очередью(Java).

```java
// Плохо: Синхронная обработка в одном потоке
public class SynchronousProcessing {
    public void processRequests(List<Request> requests) {
        for (Request request : requests) {
            processSingleRequest(request); // Долго и блокирует
        }
    }
}

// Хорошо: Асинхронная обработка с Producer-Consumer
public class AsynchronousProcessing {
    private final ProducerConsumerQueue<Request> queue = new ProducerConsumerQueue<>();

    public void submitRequests(List<Request> requests) {
        requests.forEach(queue::produce); // Быстрая постановка в очередь
    }

    // Потребители обрабатывают в фоне
    public void startConsumers() {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                while (true) {
                    Request request = queue.consume();
                    processSingleRequest(request);
                }
            }).start();
        }
    }
}
```

## Структура паттерна

```mermaid
flowchart LR
    Producer[Producer]
    Queue[BlockingQueue]
    Consumer[Consumer]
    Producer -->|put| Queue
    Queue -->|take| Consumer
```

```text
┌─────────────────┐     ┌─────────────────┐
│   Producer      │     │   Consumer      │
│                 │     │                 │
│ produce()       │────▶│ consume()       │
│                 │     │                 │
└─────────────────┘     └─────────────────┘
         │                       │
         ▼                       ▼
    ┌─────────────────┐     ┌─────────────────┐
    │   Shared Queue  │     │   Processing    │
    │                 │     │                 │
    │ put() / take()  │     │ process()       │
    │                 │     │                 │
    └─────────────────┘     └─────────────────┘
```

### Компоненты

1. **Producer**: Генерирует данные/задачи и помещает их в очередь
2. **Consumer**: Извлекает данные/задачи из очереди и обрабатывает их
3. **Queue**: **Thread-safe** буфер между производителями и потребителями
4. **Data/Task**: Объект передаваемый между **producer** и **consumer**

## Реализация на Java

### Базовая реализация с **BlockingQueue**

```java
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

// Задача для обработки
class Task {
    private final int id;
    private final String data;

    public Task(int id, String data) {
        this.id = id;
        this.data = data;
    }

    public int getId() { return id; }
    public String getData() { return data; }

    @Override
    public String toString() {
        return "Task{id=" + id + ", data='" + data + "'}";
    }
}

// Producer-Consumer очередь
class ProducerConsumerQueue<T> {
    private final BlockingQueue<T> queue;
    private final int capacity;
    private volatile boolean shutdown = false;

    public ProducerConsumerQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public void produce(T item) throws InterruptedException {
        if (shutdown) {
            throw new IllegalStateException("Queue is shut down");
        }
        queue.put(item); // Блокируется если очередь полная
    }

    public T consume() throws InterruptedException {
        return queue.take(); // Блокируется если очередь пустая
    }

    public void shutdown() {
        shutdown = true;
        // Добавляем "poison pill" для каждого consumer
        // (предполагая что знаем количество consumers)
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Producer
class Producer implements Runnable {
    private final ProducerConsumerQueue<Task> queue;
    private final int producerId;
    private final int tasksToProduce;

    public Producer(ProducerConsumerQueue<Task> queue, int producerId, int tasksToProduce) {
        this.queue = queue;
        this.producerId = producerId;
        this.tasksToProduce = tasksToProduce;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < tasksToProduce; i++) {
                Task task = new Task(producerId * 1000 + i, "Data from producer " + producerId);
                queue.produce(task);
                System.out.println("Produced: " + task);

                // Имитация работы
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Producer " + producerId + " was interrupted");
        }
    }
}

// Consumer
class Consumer implements Runnable {
    private final ProducerConsumerQueue<Task> queue;
    private final int consumerId;

    public Consumer(ProducerConsumerQueue<Task> queue, int consumerId) {
        this.queue = queue;
        this.consumerId = consumerId;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Task task = queue.consume();
                System.out.println("Consumer " + consumerId + " processing: " + task);

                // Имитация обработки
                Thread.sleep(ThreadLocalRandom.current().nextInt(200, 800));
                System.out.println("Consumer " + consumerId + " completed: " + task);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Consumer " + consumerId + " was interrupted");
        }
    }
}

// Пример использования
public class ProducerConsumerExample {
    public static void main(String[] args) throws InterruptedException {
        ProducerConsumerQueue<Task> queue = new ProducerConsumerQueue<>(10);

        // Запуск producers
        ExecutorService producers = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            producers.submit(new Producer(queue, i + 1, 5));
        }

        // Запуск consumers
        ExecutorService consumers = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            consumers.submit(new Consumer(queue, i + 1));
        }

        // Ожидание завершения producers
        producers.shutdown();
        producers.awaitTermination(1, TimeUnit.MINUTES);

        // Ожидание обработки всех задач
        while (!queue.isEmpty()) {
            Thread.sleep(100);
        }

        // Остановка consumers
        consumers.shutdownNow();
        consumers.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("All tasks completed");
    }
}
```

### Реализация с **ExecutorService**

```java
import java.util.concurrent.*;
import java.util.function.Consumer;

// Более простая реализация с ExecutorService
public class SimpleProducerConsumer<T> {

    private final ExecutorService executor;
    private final BlockingQueue<T> queue;
    private final Consumer<T> processor;

    public SimpleProducerConsumer(int consumerThreads, Consumer<T> processor) {
        this.executor = Executors.newFixedThreadPool(consumerThreads);
        this.queue = new LinkedBlockingQueue<>();
        this.processor = processor;

        // Запуск consumer потоков
        for (int i = 0; i < consumerThreads; i++) {
            executor.submit(this::consumeLoop);
        }
    }

    public void produce(T item) {
        try {
            queue.put(item);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to produce item", e);
        }
    }

    private void consumeLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                T item = queue.take();
                processor.accept(item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int getQueueSize() {
        return queue.size();
    }
}

// Пример использования для обработки заказов
@Service
public class OrderProcessingService {

    private final SimpleProducerConsumer<Order> orderProcessor;

    public OrderProcessingService() {
        this.orderProcessor = new SimpleProducerConsumer<>(5, this::processOrder);
    }

    public void submitOrder(Order order) {
        orderProcessor.produce(order);
    }

    private void processOrder(Order order) {
        try {
            // Имитация обработки заказа
            Thread.sleep(1000); // Бизнес-логика

            System.out.println("Processed order: " + order.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void shutdown() {
        orderProcessor.shutdown();
    }
}
```

## Продвинутые реализации

### 1. С приоритетами

```java
// Producer-Consumer с приоритетами
public class PriorityProducerConsumer<T> {

    private final ExecutorService executor;
    private final PriorityBlockingQueue<PriorityTask<T>> queue;
    private final Consumer<T> processor;

    public PriorityProducerConsumer(int consumerThreads, Consumer<T> processor) {
        this.executor = Executors.newFixedThreadPool(consumerThreads);
        this.queue = new PriorityBlockingQueue<>();
        this.processor = processor;

        // Запуск consumers
        for (int i = 0; i < consumerThreads; i++) {
            executor.submit(this::consumeLoop);
        }
    }

    public void produce(T item, int priority) {
        try {
            queue.put(new PriorityTask<>(item, priority));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void consumeLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                PriorityTask<T> task = queue.take();
                processor.accept(task.getItem());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static class PriorityTask<T> implements Comparable<PriorityTask<T>> {
        private final T item;
        private final int priority;
        private final long timestamp;

        public PriorityTask(T item, int priority) {
            this.item = item;
            this.priority = priority;
            this.timestamp = System.nanoTime(); // Для FIFO при равных приоритетах
        }

        public T getItem() { return item; }

        @Override
        public int compareTo(PriorityTask<T> other) {
            // Высокий приоритет = низкое число
            int priorityCompare = Integer.compare(this.priority, other.priority);
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            // FIFO для равных приоритетов
            return Long.compare(this.timestamp, other.timestamp);
        }
    }
}

// Использование для обработки заказов с приоритетами
public class PriorityOrderProcessor {

    private final PriorityProducerConsumer<Order> processor;

    public PriorityOrderProcessor() {
        this.processor = new PriorityProducerConsumer<>(3, this::processOrder);
    }

    public void submitOrder(Order order) {
        int priority = determinePriority(order);
        processor.produce(order, priority);
    }

    private int determinePriority(Order order) {
        if (order.getType() == OrderType.VIP) {
            return 1; // Высокий приоритет
        } else if (order.getTotal().compareTo(BigDecimal.valueOf(1000)) > 0) {
            return 2; // Средний приоритет
        } else {
            return 3; // Низкий приоритет
        }
    }

    private void processOrder(Order order) {
        System.out.println("Processing order: " + order.getId() + " with priority: " + determinePriority(order));
        // Обработка заказа...
    }
}
```

### 2. С **Circuit Breaker**

```java
// Producer-Consumer с Circuit Breaker для отказоустойчивости
public class ResilientProducerConsumer<T> {

    private final ExecutorService executor;
    private final BlockingQueue<T> queue;
    private final Consumer<T> processor;
    private final CircuitBreaker circuitBreaker;

    private static class CircuitBreaker {
        private volatile State state = State.CLOSED;
        private int failureCount = 0;
        private long lastFailureTime = 0;

        private static final int FAILURE_THRESHOLD = 5;
        private static final long TIMEOUT_MS = 60000; // 1 minute

        public synchronized boolean allowExecution() {
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime > TIMEOUT_MS) {
                    state = State.HALF_OPEN;
                    return true;
                }
                return false;
            }
            return true;
        }

        public synchronized void recordSuccess() {
            failureCount = 0;
            state = State.CLOSED;
        }

        public synchronized void recordFailure() {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();

            if (failureCount >= FAILURE_THRESHOLD) {
                state = State.OPEN;
            }
        }

        private enum State { CLOSED, OPEN, HALF_OPEN }
    }

    public ResilientProducerConsumer(int consumerThreads, Consumer<T> processor) {
        this.executor = Executors.newFixedThreadPool(consumerThreads);
        this.queue = new LinkedBlockingQueue<>();
        this.processor = processor;
        this.circuitBreaker = new CircuitBreaker();

        for (int i = 0; i < consumerThreads; i++) {
            executor.submit(this::consumeLoop);
        }
    }

    public boolean produce(T item) {
        if (!circuitBreaker.allowExecution()) {
            // Circuit breaker открыт, отклоняем задачу
            return false;
        }

        try {
            queue.put(item);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private void consumeLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                T item = queue.take();

                try {
                    processor.accept(item);
                    circuitBreaker.recordSuccess();
                } catch (Exception e) {
                    circuitBreaker.recordFailure();
                    // Логирование ошибки, возможно повторная постановка в очередь
                    handleProcessingFailure(item, e);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleProcessingFailure(T item, Exception e) {
        System.err.println("Failed to process item: " + item + ", error: " + e.getMessage());
        // Возможно: сохранить в dead letter queue, отправить алерт и т.д.
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

### 3. С **Batch Processing**

```java
// Producer-Consumer с пакетной обработкой
public class BatchProducerConsumer<T> {

    private final ExecutorService executor;
    private final BlockingQueue<T> queue;
    private final Consumer<List<T>> batchProcessor;
    private final int batchSize;
    private final long batchTimeoutMs;

    public BatchProducerConsumer(int consumerThreads, Consumer<List<T>> batchProcessor,
                               int batchSize, long batchTimeoutMs) {
        this.executor = Executors.newFixedThreadPool(consumerThreads);
        this.queue = new LinkedBlockingQueue<>();
        this.batchProcessor = batchProcessor;
        this.batchSize = batchSize;
        this.batchTimeoutMs = batchTimeoutMs;

        for (int i = 0; i < consumerThreads; i++) {
            executor.submit(this::consumeBatch);
        }
    }

    public void produce(T item) {
        try {
            queue.put(item);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void consumeBatch() {
        List<T> batch = new ArrayList<>();
        long batchStartTime = System.currentTimeMillis();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                long remainingTimeout = batchTimeoutMs - (System.currentTimeMillis() - batchStartTime);

                if (remainingTimeout <= 0) {
                    // Таймаут истек, обрабатываем текущий батч
                    if (!batch.isEmpty()) {
                        processBatch(new ArrayList<>(batch));
                        batch.clear();
                        batchStartTime = System.currentTimeMillis();
                    }
                    continue;
                }

                // Ждем элемент с таймаутом
                T item = queue.poll(remainingTimeout, TimeUnit.MILLISECONDS);

                if (item != null) {
                    batch.add(item);

                    // Батч заполнен, обрабатываем
                    if (batch.size() >= batchSize) {
                        processBatch(new ArrayList<>(batch));
                        batch.clear();
                        batchStartTime = System.currentTimeMillis();
                    }
                } else {
                    // Таймаут, обрабатываем текущий батч если он не пустой
                    if (!batch.isEmpty()) {
                        processBatch(new ArrayList<>(batch));
                        batch.clear();
                        batchStartTime = System.currentTimeMillis();
                    }
                }
            }
        } catch (InterruptedException e) {
            // Обработка оставшихся элементов перед завершением
            if (!batch.isEmpty()) {
                processBatch(batch);
            }
            Thread.currentThread().interrupt();
        }
    }

    private void processBatch(List<T> batch) {
        try {
            batchProcessor.accept(batch);
        } catch (Exception e) {
            System.err.println("Failed to process batch: " + e.getMessage());
            // Обработка ошибки батча
        }
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int getQueueSize() {
        return queue.size();
    }
}

// Пример использования для пакетной записи в БД
@Service
public class BatchDatabaseWriter {

    private final BatchProducerConsumer<User> batchWriter;

    public BatchDatabaseWriter() {
        Consumer<List<User>> batchProcessor = users -> {
            // Пакетная вставка в БД
            userRepository.saveAll(users);
            System.out.println("Batch saved " + users.size() + " users");
        };

        this.batchWriter = new BatchProducerConsumer<>(2, batchProcessor, 50, 5000);
    }

    public void saveUser(User user) {
        batchWriter.produce(user);
    }

    @PreDestroy
    public void shutdown() {
        batchWriter.shutdown();
    }
}
```

## Примеры использования

### 1. **Web Server Request Processing**

```java
@RestController
public class RequestController {

    private final ProducerConsumerQueue<Request> requestQueue = new ProducerConsumerQueue<>(1000);

    public RequestController() {
        // Запуск consumer потоков для обработки запросов
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        Request request = requestQueue.consume();
                        processRequest(request);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }).start();
        }
    }

    @PostMapping("/api/process")
    public ResponseEntity<String> submitRequest(@RequestBody RequestData data) {
        Request request = new Request(data);

        try {
            requestQueue.produce(request);
            return ResponseEntity.accepted()
                    .header("Location", "/api/status/" + request.getId())
                    .body("Request accepted for processing");
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Service temporarily unavailable");
        }
    }

    private void processRequest(Request request) {
        // Имитация тяжелой обработки
        try {
            Thread.sleep(2000); // 2 seconds processing
            request.setStatus(RequestStatus.COMPLETED);
        } catch (InterruptedException e) {
            request.setStatus(RequestStatus.FAILED);
            Thread.currentThread().interrupt();
        }
    }
}
```

### 2. **Log Processing Pipeline**

```java
@Service
public class LogProcessingPipeline {

    private final SimpleProducerConsumer<LogEntry> logProcessor;

    public LogProcessingPipeline() {
        // Pipeline: Parse -> Validate -> Store -> Index
        Consumer<LogEntry> processor = entry -> {
            try {
                parseLogEntry(entry);
                validateLogEntry(entry);
                storeLogEntry(entry);
                indexLogEntry(entry);
            } catch (Exception e) {
                handleProcessingError(entry, e);
            }
        };

        this.logProcessor = new SimpleProducerConsumer<>(Runtime.getRuntime().availableProcessors(), processor);
    }

    public void processLogEntry(String rawLog) {
        LogEntry entry = new LogEntry(rawLog, System.currentTimeMillis());
        logProcessor.produce(entry);
    }

    private void parseLogEntry(LogEntry entry) {
        // Парсинг лог строки
        entry.setParsedData(parseRawLog(entry.getRawLog()));
    }

    private void validateLogEntry(LogEntry entry) {
        // Валидация данных
        if (!isValidLogEntry(entry)) {
            throw new ValidationException("Invalid log entry");
        }
    }

    private void storeLogEntry(LogEntry entry) {
        // Сохранение в БД
        logRepository.save(entry);
    }

    private void indexLogEntry(LogEntry entry) {
        // Индексация для поиска
        searchIndex.index(entry);
    }

    private void handleProcessingError(LogEntry entry, Exception e) {
        // Сохранение в dead letter queue
        deadLetterQueue.add(entry);
        logger.error("Failed to process log entry: " + entry.getId(), e);
    }

    @PreDestroy
    public void shutdown() {
        logProcessor.shutdown();
    }
}
```

### 3. **Image Processing Queue**

```java
@Service
public class ImageProcessingService {

    private final SimpleProducerConsumer<ImageProcessingTask> imageProcessor;

    public ImageProcessingService() {
        Consumer<ImageProcessingTask> processor = task -> {
            try {
                processImage(task);
                notifyCompletion(task);
            } catch (Exception e) {
                handleProcessingError(task, e);
            }
        };

        this.imageProcessor = new SimpleProducerConsumer<>(4, processor);
    }

    public void submitImageForProcessing(MultipartFile image, String userId, ProcessingOptions options) {
        ImageProcessingTask task = new ImageProcessingTask(image, userId, options);
        imageProcessor.produce(task);

        // Возврат task ID для отслеживания
        return task.getId();
    }

    private void processImage(ImageProcessingTask task) throws IOException {
        // Загрузка изображения
        BufferedImage image = ImageIO.read(task.getImage().getInputStream());

        // Применение обработки
        if (task.getOptions().isResize()) {
            image = resizeImage(image, task.getOptions().getWidth(), task.getOptions().getHeight());
        }

        if (task.getOptions().isCompress()) {
            image = compressImage(image, task.getOptions().getQuality());
        }

        // Сохранение результата
        saveProcessedImage(image, task);
    }

    private void notifyCompletion(ImageProcessingTask task) {
        // Уведомление пользователя
        notificationService.sendNotification(task.getUserId(),
            "Image processing completed: " + task.getId());
    }

    private void handleProcessingError(ImageProcessingTask task, Exception e) {
        // Логирование ошибки
        logger.error("Failed to process image: " + task.getId(), e);

        // Уведомление пользователя об ошибке
        notificationService.sendNotification(task.getUserId(),
            "Image processing failed: " + task.getId());
    }

    @PreDestroy
    public void shutdown() {
        imageProcessor.shutdown();
    }
}
```

## Лучшие практики

### 1. Выбор правильного типа очереди

```java
public class QueueTypeSelector {

    // Для ограниченного количества элементов
    public static <T> BlockingQueue<T> createBoundedQueue(int capacity) {
        return new ArrayBlockingQueue<>(capacity);
    }

    // Для приоритетной обработки
    public static <T extends Comparable<T>> BlockingQueue<T> createPriorityQueue(int capacity) {
        return new PriorityBlockingQueue<>(capacity);
    }

    // Для FIFO с дополнительными возможностями
    public static <T> BlockingQueue<T> createLinkedQueue() {
        return new LinkedBlockingQueue<>();
    }

    // Для synchronous handover (producer ждет consumer)
    public static <T> BlockingQueue<T> createSynchronousQueue() {
        return new SynchronousQueue<>();
    }
}
```

### 2. Обработка ошибок

```java
public class ErrorHandlingBestPractices {

    // Dead Letter Queue для неудачных задач
    private final BlockingQueue<FailedTask> deadLetterQueue = new LinkedBlockingQueue<>();

    // Retry механизм
    public void processWithRetry(Task task, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                processTask(task);
                return; // Успешно
            } catch (Exception e) {
                attempts++;
                if (attempts >= maxRetries) {
                    // Отправка в DLQ
                    deadLetterQueue.offer(new FailedTask(task, e));
                    break;
                }

                // Exponential backoff
                try {
                    Thread.sleep(1000 * (long) Math.pow(2, attempts - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    // Circuit Breaker
    public void processWithCircuitBreaker(Task task) {
        if (circuitBreaker.isOpen()) {
            deadLetterQueue.offer(new FailedTask(task, new ServiceUnavailableException()));
            return;
        }

        try {
            processTask(task);
            circuitBreaker.recordSuccess();
        } catch (Exception e) {
            circuitBreaker.recordFailure();
            throw e;
        }
    }
}
```

### 3. **Monitoring** и **Metrics**

```java
@Service
public class ProducerConsumerMetrics {

    private final MeterRegistry meterRegistry;

    public void recordProduction(String producerType) {
        meterRegistry.counter("producer.items.produced", "type", producerType).increment();
    }

    public void recordConsumption(String consumerType, long processingTimeMs) {
        meterRegistry.counter("consumer.items.consumed", "type", consumerType).increment();
        meterRegistry.timer("consumer.processing.time", "type", consumerType)
                .record(processingTimeMs, TimeUnit.MILLISECONDS);
    }

    public void recordQueueSize(String queueName, int size) {
        meterRegistry.gauge("queue.size", Tags.of("name", queueName), size);
    }

    public void recordError(String component, Exception e) {
        meterRegistry.counter("producer.consumer.errors",
                "component", component,
                "error_type", e.getClass().getSimpleName()).increment();
    }
}

// Использование в коде
public class MonitoredProducerConsumer<T> extends SimpleProducerConsumer<T> {

    private final ProducerConsumerMetrics metrics;

    public MonitoredProducerConsumer(int consumerThreads, Consumer<T> processor,
                                   ProducerConsumerMetrics metrics) {
        super(consumerThreads, processor);
        this.metrics = metrics;
    }

    @Override
    public void produce(T item) {
        super.produce(item);
        metrics.recordProduction(item.getClass().getSimpleName());
        metrics.recordQueueSize("main", getQueueSize());
    }

    // Override consume method to add metrics
    @Override
    protected void consumeLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                long startTime = System.nanoTime();
                T item = queue.take();

                try {
                    processor.accept(item);
                    long processingTime = (System.nanoTime() - startTime) / 1_000_000;
                    metrics.recordConsumption(item.getClass().getSimpleName(), processingTime);
                } catch (Exception e) {
                    metrics.recordError("consumer", e);
                    throw e;
                }

                metrics.recordQueueSize("main", getQueueSize());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### 4. **Testing**

```java
@ExtendWith(MockitoExtension.class)
public class ProducerConsumerTest {

    @Mock
    private Consumer<Task> processor;

    @Test
    void shouldProcessProducedItems() throws InterruptedException {
        SimpleProducerConsumer<Task> pc = new SimpleProducerConsumer<>(1, processor);

        Task task1 = new Task(1, "test1");
        Task task2 = new Task(2, "test2");

        // Produce items
        pc.produce(task1);
        pc.produce(task2);

        // Wait for processing
        await().atMost(1, SECONDS).until(() -> {
            try {
                verify(processor, times(2)).accept(any(Task.class));
                return true;
            } catch (AssertionError e) {
                return false;
            }
        });

        pc.shutdown();
    }

    @Test
    void shouldHandleProcessorExceptions() throws InterruptedException {
        doThrow(new RuntimeException("Processing failed")).when(processor).accept(any());

        SimpleProducerConsumer<Task> pc = new SimpleProducerConsumer<>(1, processor);

        Task task = new Task(1, "test");
        pc.produce(task);

        // Verify processor was called despite exception
        await().atMost(1, SECONDS).until(() -> {
            try {
                verify(processor).accept(task);
                return true;
            } catch (AssertionError e) {
                return false;
            }
        });

        pc.shutdown();
    }

    @Test
    void shouldMaintainQueueSize() throws InterruptedException {
        SimpleProducerConsumer<Task> pc = new SimpleProducerConsumer<>(1, processor);

        // Add multiple items
        for (int i = 0; i < 5; i++) {
            pc.produce(new Task(i, "test" + i));
        }

        // Verify queue size (items waiting to be processed)
        assertEquals(4, pc.getQueueSize()); // 5 - 1 (currently being processed)

        pc.shutdown();
    }
}
```


## Решение проблем

Типичные проблемы: неверное использование API (сверьтесь с примерами и разделом «Лучшие практики» выше), производительность (профилирование и настройки). Подробнее — в разделе «Лучшие практики» и в официальной документации из блока «Полезные ссылки».

## FAQ

Подробнее см. разделы «Когда использовать» и «Лучшие практики» выше.


## Заключение

**Producer-Consumer** паттерн является фундаментальным для построения масштабируемых многопоточных приложений. Он позволяет эффективно разделять работу между потоками, обеспечивать буферизацию и создавать отказоустойчивые системы.

**Ключевые преимущества:**
- **Масштабируемость**: Легко добавлять **producers** и **consumers**
- **Отказоустойчивость**: Продолжение работы при падении отдельных компонентов
- **Производительность**: Параллельная обработка задач
- **Упрощение**: Разделение ответственности между компонентами

**Используйте Producer-Consumer, когда:**
- Нужно обрабатывать большое количество задач асинхронно
- Требуется буферизация между компонентами
- Необходима отказоустойчивость системы
- Важна масштабируемость обработки
