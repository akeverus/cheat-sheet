---
title: "Quarkus: Scheduling - Планирование задач"
description: "Полное руководство по планированию задач в Quarkus: @Scheduled, cron expressions, async scheduling, job management и best practices"
tags: ["quarkus", "scheduling", "cron", "jobs", "tasks", "java"]
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-reactive.md"]
updated: "2025-01-16"
related: ["quarkus-core.md", "quarkus-reactive.md"]
---

# Quarkus: Scheduling - Планирование задач

## Введение

Quarkus предоставляет простой и мощный механизм планирования задач через аннотацию @Scheduled. Это позволяет выполнять периодические задачи, cron jobs и другие запланированные операции.

### Основные возможности

- **@Scheduled**: Планирование задач
- **Cron Expressions**: Поддержка cron синтаксиса
- **Async Scheduling**: Асинхронное выполнение задач
- **Job Management**: Управление задачами

## Basic Scheduling

### Simple Scheduled Task

Простая запланированная задача:

```java
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ScheduledTasks {
    
    @Scheduled(every = "10s")
    void everyTenSeconds() {
        System.out.println("Executing every 10 seconds");
    }
}
```

### Cron Expression

Использование cron выражений:

```java
@ApplicationScoped
public class CronTasks {
    
    @Scheduled(cron = "0 0 * * * ?")  // Каждый час
    void everyHour() {
        System.out.println("Executing every hour");
    }
    
    @Scheduled(cron = "0 0 0 * * ?")  // Каждый день в полночь
    void everyDay() {
        System.out.println("Executing every day");
    }
}
```

## Advanced Scheduling

### Conditional Scheduling

Условное планирование:

```java
@ApplicationScoped
public class ConditionalScheduling {
    
    @Scheduled(every = "1m", skipExecutionIf = SkipPredicate.class)
    void conditionalTask() {
        // Выполняется только если условие выполнено
    }
    
    public static class SkipPredicate implements Scheduled.SkipPredicate {
        @Override
        public boolean test(ScheduledExecution execution) {
            // Логика пропуска задачи
            return shouldSkip();
        }
    }
}
```

### Async Scheduling

Асинхронное выполнение задач:

```java
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AsyncScheduledTasks {
    
    @Scheduled(every = "5s")
    Uni<Void> asyncTask() {
        return Uni.createFrom().item(() -> {
            // Асинхронная обработка
            processData();
            return null;
        });
    }
}
```

## Job Management

### Job Identity

Идентификация задач:

```java
@ApplicationScoped
public class IdentifiedTasks {
    
    @Scheduled(identity = "my-task", every = "10s")
    void identifiedTask() {
        // Задача с идентификатором
    }
}
```

### Programmatic Scheduling

Программное планирование:

```java
import io.quarkus.scheduler.Scheduler;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProgrammaticScheduling {
    
    @Inject
    Scheduler scheduler;
    
    public void scheduleTask() {
        scheduler.newJob("my-job")
            .setInterval(Duration.ofSeconds(10))
            .setTask(execution -> {
                // Выполнение задачи
            })
            .schedule();
    }
}
```

## Best Practices

### 1. Используйте cron для сложных расписаний

```java
// ✅ Хорошо
@Scheduled(cron = "0 0 2 * * ?")  // Каждый день в 2:00
void dailyTask() {
    // ...
}
```

### 2. Обрабатывайте ошибки в задачах

```java
// ✅ Хорошо
@Scheduled(every = "10s")
void taskWithErrorHandling() {
    try {
        processData();
    } catch (Exception e) {
        logError(e);
    }
}
```

### 3. Используйте async для долгих задач

```java
// ✅ Хорошо
@Scheduled(every = "1m")
Uni<Void> longRunningTask() {
    return processAsync().replaceWithVoid();
}
```

## Cron Expressions

### Cron Syntax

Синтаксис cron выражений:

```
┌───────────── секунда (0-59)
│ ┌─────────── минута (0-59)
│ │ ┌───────── час (0-23)
│ │ │ ┌─────── день месяца (1-31)
│ │ │ │ ┌───── месяц (1-12)
│ │ │ │ │ ┌─── день недели (0-7, где 0 и 7 = воскресенье)
│ │ │ │ │ │
* * * * * *
```

### Common Cron Patterns

Часто используемые cron паттерны:

```java
@ApplicationScoped
public class CommonCronPatterns {
    
    @Scheduled(cron = "0 0 * * * ?")      // Каждый час
    void everyHour() {}
    
    @Scheduled(cron = "0 0 0 * * ?")      // Каждый день в полночь
    void everyDay() {}
    
    @Scheduled(cron = "0 0 0 ? * MON")    // Каждый понедельник в полночь
    void everyMonday() {}
    
    @Scheduled(cron = "0 0 0 1 * ?")      // Первое число каждого месяца
    void firstOfMonth() {}
    
    @Scheduled(cron = "0 0/15 * * * ?")  // Каждые 15 минут
    void everyFifteenMinutes() {}
    
    @Scheduled(cron = "0 0 9-17 * * MON-FRI")  // Каждый час с 9 до 17 в рабочие дни
    void businessHours() {}
}
```

## Scheduled Task Lifecycle

### Task Execution Context

Контекст выполнения задачи:

```java
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContextAwareTasks {
    
    @Scheduled(every = "10s")
    void taskWithContext(ScheduledExecution execution) {
        System.out.println("Scheduled fire time: " + execution.getScheduledFireTime());
        System.out.println("Actual fire time: " + execution.getFireTime());
    }
}
```

### Task Cancellation

Отмена задач:

```java
import io.quarkus.scheduler.Scheduler;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CancellableTasks {
    
    @Inject
    Scheduler scheduler;
    
    public void cancelTask(String taskId) {
        scheduler.unschedule(taskId);
    }
    
    public void pauseTask(String taskId) {
        scheduler.pause(taskId);
    }
    
    public void resumeTask(String taskId) {
        scheduler.resume(taskId);
    }
}
```

## Error Handling

### Task Error Handling

Обработка ошибок в задачах:

```java
@ApplicationScoped
public class ErrorHandlingTasks {
    
    @Scheduled(every = "10s")
    void taskWithErrorHandling() {
        try {
            processData();
        } catch (Exception e) {
            logError(e);
            // Задача продолжит выполняться по расписанию
        }
    }
    
    @Scheduled(every = "1m")
    Uni<Void> reactiveTaskWithErrorHandling() {
        return processAsync()
            .onFailure().invoke(this::logError)
            .onFailure().recoverWithItem(() -> null)
            .replaceWithVoid();
    }
}
```

### Retry Logic

Логика повторных попыток:

```java
import io.smallrye.mutiny.Uni;
import java.time.Duration;

@ApplicationScoped
public class RetryTasks {
    
    @Scheduled(every = "5m")
    Uni<Void> taskWithRetry() {
        return processAsync()
            .onFailure().retry()
                .withBackOff(Duration.ofSeconds(1))
                .atMost(3)
            .replaceWithVoid();
    }
}
```

## Task Coordination

### Distributed Scheduling

Распределенное планирование:

```properties
# application.properties
quarkus.scheduler.cluster.enabled=true
quarkus.scheduler.cluster.start-delay=10s
```

### Task Locking

Блокировка задач:

```java
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LockedTasks {
    
    @Scheduled(every = "1m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    void lockedTask() {
        // Выполняется только если предыдущий запуск завершен
    }
}
```

## Monitoring и Metrics

### Task Metrics

Метрики задач:

```java
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MonitoredTasks {
    
    @Inject
    MeterRegistry registry;
    
    @Scheduled(every = "10s")
    void monitoredTask() {
        long startTime = System.currentTimeMillis();
        try {
            processData();
            registry.counter("scheduled.task.success", "task", "monitoredTask").increment();
        } catch (Exception e) {
            registry.counter("scheduled.task.failure", "task", "monitoredTask").increment();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            registry.timer("scheduled.task.duration", "task", "monitoredTask").record(duration, TimeUnit.MILLISECONDS);
        }
    }
}
```

## Best Practices

### 1. Используйте cron для сложных расписаний

```java
// ✅ Хорошо
@Scheduled(cron = "0 0 2 * * ?")  // Каждый день в 2:00
void dailyTask() {
    // ...
}
```

### 2. Обрабатывайте ошибки в задачах

```java
// ✅ Хорошо
@Scheduled(every = "10s")
void taskWithErrorHandling() {
    try {
        processData();
    } catch (Exception e) {
        logError(e);
    }
}
```

### 3. Используйте async для долгих задач

```java
// ✅ Хорошо
@Scheduled(every = "1m")
Uni<Void> longRunningTask() {
    return processAsync().replaceWithVoid();
}
```

### 4. Используйте блокировку для критических задач

```java
// ✅ Хорошо
@Scheduled(every = "1m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
void criticalTask() {
    // ...
}
```

### 5. Мониторьте выполнение задач

```java
// ✅ Хорошо
@Scheduled(every = "10s")
void monitoredTask() {
    long start = System.currentTimeMillis();
    try {
        processData();
    } finally {
        recordMetrics(System.currentTimeMillis() - start);
    }
}
```

## Advanced Scheduling Patterns

### Job Queue Pattern

Реализация очереди задач:

```java
@ApplicationScoped
public class JobQueueService {
    
    private final Queue<Job> jobQueue = new ConcurrentLinkedQueue<>();
    
    @Scheduled(every = "1s")
    void processJobQueue() {
        Job job = jobQueue.poll();
        if (job != null) {
            processJob(job);
        }
    }
    
    public void enqueueJob(Job job) {
        jobQueue.offer(job);
    }
}
```

### Priority Scheduling

Планирование с приоритетами:

```java
@ApplicationScoped
public class PriorityScheduler {
    
    private final PriorityQueue<PriorityJob> jobQueue = 
        new PriorityQueue<>(Comparator.comparing(PriorityJob::getPriority));
    
    @Scheduled(every = "1s")
    void processPriorityJobs() {
        PriorityJob job = jobQueue.poll();
        if (job != null) {
            executeJob(job);
        }
    }
}
```

### Distributed Scheduling

Распределенное планирование:

```java
@ApplicationScoped
public class DistributedScheduler {
    
    @Inject
    RedisClient redisClient;
    
    @Scheduled(every = "10s")
    void distributedTask() {
        String lockKey = "task:lock";
        String lockValue = UUID.randomUUID().toString();
        
        // Попытка получить блокировку
        if (acquireLock(lockKey, lockValue, 30)) {
            try {
                executeTask();
            } finally {
                releaseLock(lockKey, lockValue);
            }
        }
    }
    
    private boolean acquireLock(String key, String value, int ttl) {
        // Реализация через Redis SET NX EX
        return redisClient.set(key, value, SetArgs.Builder.nx().ex(ttl));
    }
}
```

## Task Monitoring and Metrics

### Execution Time Tracking

Отслеживание времени выполнения:

```java
@ApplicationScoped
public class MonitoredScheduledTask {
    
    @Inject
    MeterRegistry registry;
    
    @Scheduled(every = "1m")
    void monitoredTask() {
        Timer.Sample sample = Timer.start(registry);
        try {
            executeTask();
        } finally {
            sample.stop(registry.timer("scheduled.task.duration", 
                Tags.of("task", "monitoredTask")));
        }
    }
}
```

### Task Success/Failure Tracking

Отслеживание успешных и неудачных выполнений:

```java
@ApplicationScoped
public class TrackedScheduledTask {
    
    @Inject
    MeterRegistry registry;
    
    @Scheduled(every = "10s")
    void trackedTask() {
        try {
            executeTask();
            registry.counter("scheduled.task.success", 
                Tags.of("task", "trackedTask")).increment();
        } catch (Exception e) {
            registry.counter("scheduled.task.failure", 
                Tags.of("task", "trackedTask", "error", e.getClass().getSimpleName()))
                .increment();
            throw e;
        }
    }
}
```

## Advanced Scheduling Patterns

### Conditional Task Execution

Условное выполнение задач:

```java
@ApplicationScoped
public class ConditionalScheduledTask {
    
    @ConfigProperty(name = "task.enabled")
    boolean taskEnabled;
    
    @Scheduled(every = "1m")
    void conditionalTask() {
        if (taskEnabled) {
            executeTask();
        }
    }
}
```

### Task Dependencies

Зависимости между задачами:

```java
@ApplicationScoped
public class DependentTasks {
    
    private volatile boolean firstTaskCompleted = false;
    
    @Scheduled(every = "10s")
    void firstTask() {
        // Первая задача
        processData();
        firstTaskCompleted = true;
    }
    
    @Scheduled(every = "5s")
    void secondTask() {
        // Вторая задача выполняется только после первой
        if (firstTaskCompleted) {
            processDependentData();
        }
    }
}
```

### Dynamic Schedule Configuration

Динамическая конфигурация расписания:

```java
@ApplicationScoped
public class DynamicScheduleService {
    
    @Inject
    Scheduler scheduler;
    
    public void scheduleTask(String cronExpression) {
        scheduler.newJob("dynamic-job")
            .setCron(cronExpression)
            .setTask(execution -> {
                // Выполнение задачи
            })
            .schedule();
    }
}
```

## Заключение

Quarkus Scheduling предоставляет мощные инструменты для планирования задач. Поддержка @Scheduled, cron expressions, async scheduling, job management, error handling, task coordination, monitoring и других продвинутых возможностей позволяет создавать эффективные системы планирования задач. Правильное использование расписаний, обработка ошибок, координация задач и мониторинг являются ключевыми аспектами создания надежных систем планирования.

## Дополнительные ресурсы

- [Quarkus Scheduling Guide](https://quarkus.io/guides/scheduler)
- [Cron Expression Guide](https://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html)
- [Quartz Scheduler](https://www.quartz-scheduler.org/)
