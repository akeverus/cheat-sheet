---
title: "Micronaut: Scheduling — Task Scheduling и Async Execution"
description: "Полное руководство по планированию задач в Micronaut: @Scheduled, task scheduling, async execution и best practices"
tags:
  - micronaut
  - scheduling
  - tasks
  - async
  - cron
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-core.md"]
next: ["micronaut-reactive.md", "micronaut-testing.md"]
updated: "2026-04-20"
related: ["micronaut-core.md", "micronaut-reactive.md"]
---

# Micronaut: Scheduling — Task Scheduling и Async Execution

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Scheduling](#настройка-scheduling)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [@Scheduled Annotation](#scheduled-annotation)
  - [Fixed Rate](#fixed-rate)
  - [Fixed Delay](#fixed-delay)
  - [Cron Expression](#cron-expression)
- [Async Execution](#async-execution)
  - [Async Scheduled Tasks](#async-scheduled-tasks)
  - [Async Configuration](#async-configuration)
- [Task Management](#task-management)
  - [Conditional Scheduling](#conditional-scheduling)
  - [Task with Parameters](#task-with-parameters)
- [Error Handling](#error-handling)
  - [Error Handling in Scheduled Tasks](#error-handling-in-scheduled-tasks)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте правильные интервалы](#1-используйте-правильные-интервалы)
  - [2. Обрабатывайте ошибки](#2-обрабатывайте-ошибки)
  - [3. Используйте async для долгих задач](#3-используйте-async-для-долгих-задач)
  - [4. Настраивайте thread pool правильно](#4-настраивайте-thread-pool-правильно)
  - [5. Используйте conditional scheduling](#5-используйте-conditional-scheduling)
- [Dynamic Scheduling](#dynamic-scheduling)
  - [Programmatic Scheduling](#programmatic-scheduling)
  - [Conditional Task Execution](#conditional-task-execution)
- [Task Monitoring](#task-monitoring)
  - [Task Metrics](#task-metrics)
- [Distributed Scheduling](#distributed-scheduling)
  - [Cluster-aware Scheduling](#cluster-aware-scheduling)
- [Task Coordination](#task-coordination)
  - [Task Locking](#task-locking)
- [Scheduled Task Dependencies](#scheduled-task-dependencies)
  - [Task Chaining](#task-chaining)
- [Task Retry](#task-retry)
  - [Retry Configuration](#retry-configuration)
- [Job Scheduling](#job-scheduling)
  - [Job Scheduling with Quartz](#job-scheduling-with-quartz)
- [Scheduled Task Dependencies](#scheduled-task-dependencies-1)
  - [Task Chaining](#task-chaining-1)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut** предоставляет мощные инструменты для планирования задач с поддержкой **cron**-выражений, фиксированных интервалов и асинхронного выполнения. Это позволяет создавать приложения с автоматическим выполнением задач по расписанию.

### Основные возможности

- **@Scheduled**: Аннотация для планирования задач
- **Cron Expressions**: Поддержка **cron**-выражений
- **Fixed `Rate`/Delay**: Фиксированные интервалы
- **Async Execution**: Асинхронное выполнение задач
- **Task Management**: Управление задачами

## Настройка Scheduling

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut:micronaut-scheduling")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  application:
    name: my-app
```

## @Scheduled Annotation

### Fixed Rate

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

@Singleton
public class ScheduledTasks {

    @Scheduled(fixedRate = "5m")
    public void executeEvery5Minutes() {
        System.out.println("Executing every 5 minutes");
        // Выполнение задачи
    }

    @Scheduled(fixedRate = "1h")
    public void executeEveryHour() {
        System.out.println("Executing every hour");
        // Выполнение задачи
    }
}
```

### Fixed Delay

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

@Singleton
public class ScheduledTasks {

    @Scheduled(fixedDelay = "10s")
    public void executeWithDelay() {
        System.out.println("Executing with 10 second delay");
        // Выполнение задачи
    }
}
```

### Cron Expression

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

@Singleton
public class ScheduledTasks {

    @Scheduled(cron = "0 0 12 * * ?")
    public void executeAtNoon() {
        System.out.println("Executing at noon every day");
        // Выполнение задачи
    }

    @Scheduled(cron = "0 0 0 * * MON")
    public void executeEveryMonday() {
        System.out.println("Executing every Monday at midnight");
        // Выполнение задачи
    }

    @Scheduled(cron = "0 */15 * * * ?")
    public void executeEvery15Minutes() {
        System.out.println("Executing every 15 minutes");
        // Выполнение задачи
    }
}
```

## Async Execution

### Async Scheduled Tasks

```java
import io.micronaut.scheduling.annotation.Async;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class AsyncScheduledTasks {

    @Scheduled(fixedRate = "1m")
    @Async
    public CompletableFuture<Void> executeAsync() {
        return CompletableFuture.runAsync(() -> {
            System.out.println("Executing asynchronously");
            // Асинхронное выполнение задачи
        });
    }
}
```

### Async Configuration

**application.yml:**

```yaml
micronaut:
  executors:
    scheduled:
      core-pool-size: 5
      maximum-pool-size: 10
```

## Task Management

### Conditional Scheduling

```java
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Singleton
@Requires(property = "scheduling.enabled", value = "true")
public class ConditionalScheduledTasks {

    @Scheduled(fixedRate = "5m")
    public void executeIfEnabled() {
        System.out.println("Executing if scheduling is enabled");
        // Выполнение задачи
    }
}
```

### Task with Parameters

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import jakarta.inject.Named;

@Singleton
public class ParameterizedScheduledTasks {
    private final String taskName;

    public ParameterizedScheduledTasks(@Named("taskName") String taskName) {
        this.taskName = taskName;
    }

    @Scheduled(fixedRate = "1m")
    public void executeWithParameter() {
        System.out.println("Executing task: " + taskName);
        // Выполнение задачи с параметром
    }
}
```

## Error Handling

### Error Handling in Scheduled Tasks

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ErrorHandlingScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingScheduledTasks.class);

    @Scheduled(fixedRate = "5m")
    public void executeWithErrorHandling() {
        try {
            // Выполнение задачи
            processData();
        } catch (Exception e) {
            log.error("Error executing scheduled task", e);
            // Обработка ошибки
        }
    }

    private void processData() {
        // Логика обработки данных
    }
}
```

## Лучшие практики

### 1. Используйте правильные интервалы

```java
// ✅ Хорошо
@Scheduled(fixedRate = "5m")
public void executeTask() {
    // ...
}
```

### 2. Обрабатывайте ошибки

```java
// ✅ Хорошо
@Scheduled(fixedRate = "1m")
public void executeTask() {
    try {
        // Логика задачи
    } catch (Exception e) {
        log.error("Error", e);
    }
}
```

### 3. Используйте async для долгих задач

```java
// ✅ Хорошо
@Scheduled(fixedRate = "1m")
@Async
public CompletableFuture<Void> executeLongTask() {
    // Долгая задача
}
```

### 4. Настраивайте thread pool правильно

```yaml
# ✅ Хорошо
micronaut:
  executors:
    scheduled:
      core-pool-size: 5
```

### 5. Используйте conditional scheduling

```java
// ✅ Хорошо
@Requires(property = "scheduling.enabled", value = "true")
public class ScheduledTasks {
    // ...
}
```

## Dynamic Scheduling

### Programmatic Scheduling

```java
import io.micronaut.scheduling.TaskScheduler;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Singleton
public class DynamicScheduler {
    private final TaskScheduler taskScheduler;

    public DynamicScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public ScheduledFuture<?> scheduleTask(Runnable task, Duration delay) {
        return taskScheduler.schedule(delay, task);
    }

    public ScheduledFuture<?> scheduleTaskAtFixedRate(
            Runnable task,
            Duration initialDelay,
            Duration period) {
        return taskScheduler.scheduleAtFixedRate(initialDelay, period, task);
    }
}
```

### Conditional Task Execution

```java
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Singleton
@Requires(property = "tasks.enabled", value = "true")
public class ConditionalTasks {

    @Scheduled(fixedRate = "5m")
    @Requires(property = "tasks.cleanup.enabled", value = "true")
    public void cleanupTask() {
        // Выполнение задачи очистки
    }
}
```

## Task Monitoring

### Task Metrics

```java
import io.micronaut.scheduling.annotation.Scheduled;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.inject.Singleton;

@Singleton
public class MonitoredScheduledTasks {
    private final Counter taskExecutionCounter;
    private final Timer taskExecutionTimer;

    public MonitoredScheduledTasks(MeterRegistry meterRegistry) {
        this.taskExecutionCounter = Counter.builder("scheduled.tasks.executed")
            .description("Number of scheduled tasks executed")
            .register(meterRegistry);
        this.taskExecutionTimer = Timer.builder("scheduled.tasks.execution.time")
            .description("Scheduled task execution time")
            .register(meterRegistry);
    }

    @Scheduled(fixedRate = "1m")
    public void monitoredTask() {
        Timer.Sample sample = Timer.start(taskExecutionTimer);
        try {
            // Выполнение задачи
            processData();
            taskExecutionCounter.increment();
        } finally {
            sample.stop(taskExecutionTimer);
        }
    }

    private void processData() {
        // Логика обработки данных
    }
}
```

## Distributed Scheduling

### Cluster-aware Scheduling

```java
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Singleton
@Requires(property = "cluster.leader", value = "true")
public class LeaderOnlyScheduledTasks {

    @Scheduled(fixedRate = "5m")
    public void leaderOnlyTask() {
        // Задача выполняется только на лидере кластера
        System.out.println("Executing leader-only task");
    }
}
```

## Task Coordination

### Task Locking

```java
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.cache.SyncCache;
import jakarta.inject.Singleton;
import jakarta.inject.Named;
import java.util.concurrent.locks.Lock;

@Singleton
public class CoordinatedScheduledTasks {
    private final SyncCache<String, Boolean> lockCache;

    public CoordinatedScheduledTasks(@Named("locks") SyncCache<String, Boolean> lockCache) {
        this.lockCache = lockCache;
    }

    @Scheduled(fixedRate = "1m")
    public void coordinatedTask() {
        String lockKey = "task:coordinated";
        Boolean lockAcquired = lockCache.get(lockKey, () -> {
            // Попытка получить блокировку
            return true;
        });

        if (lockAcquired) {
            try {
                // Выполнение задачи
                processData();
            } finally {
                lockCache.invalidate(lockKey);
            }
        }
    }

    private void processData() {
        // Логика обработки данных
    }
}
```

## Scheduled Task Dependencies

### Task Chaining

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

@Singleton
public class ChainedScheduledTasks {
    private volatile boolean firstTaskCompleted = false;

    @Scheduled(fixedRate = "5m")
    public void firstTask() {
        // Выполнение первой задачи
        processFirstTask();
        firstTaskCompleted = true;
    }

    @Scheduled(fixedRate = "5m")
    public void secondTask() {
        if (firstTaskCompleted) {
            // Выполнение второй задачи после первой
            processSecondTask();
            firstTaskCompleted = false;
        }
    }
}
```

## Task Retry

### Retry Configuration

```java
import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Singleton;

@Singleton
public class RetryableScheduledTasks {

    @Scheduled(fixedRate = "1m")
    @Retryable(attempts = "3", delay = "1s")
    public void retryableTask() {
        // Задача с автоматическим retry при ошибках
        processData();
    }
}
```

## Job Scheduling

### Job Scheduling with Quartz

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

@Singleton
public class QuartzScheduledJob {

    @Scheduled(cron = "0 0 2 * * ?")
    public void runNightlyJob() {
        // Выполнение ночной задачи
        processNightlyData();
    }

    @Scheduled(cron = "0 */30 * * * ?")
    public void runHalfHourlyJob() {
        // Выполнение задачи каждые 30 минут
        processData();
    }
}
```

## Scheduled Task Dependencies

### Task Chaining

```java
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;

@Singleton
public class ChainedScheduledTasks {
    private volatile boolean firstTaskCompleted = false;

    @Scheduled(fixedRate = "5m")
    public void firstTask() {
        // Выполнение первой задачи
        processFirstTask();
        firstTaskCompleted = true;
    }

    @Scheduled(fixedRate = "5m")
    public void secondTask() {
        if (firstTaskCompleted) {
            // Выполнение второй задачи после первой
            processSecondTask();
            firstTaskCompleted = false;
        }
    }
}
```


## Заключение

**Micronaut Scheduling** предоставляет мощные инструменты для планирования задач. Поддержка **cron**-выражений, фиксированных интервалов, асинхронного выполнения, условного планирования, обработки ошибок, динамического планирования, мониторинга, распределенного планирования, координации задач, **task chaining**, **retry**, **job scheduling**, **task dependencies** и других продвинутых возможностей позволяет создавать приложения с автоматическим выполнением задач по расписанию.

## Дополнительные ресурсы

- [**Micronaut Scheduling** Documentation](https://micronaut-projects.github.io/micronaut-scheduling/latest/guide/)
- [**Cron Expression** Guide](https://crontab.guru/)
- [**Task Scheduling Best Practices**](https://docs.spring.io/spring-framework/reference/integration/scheduling.html)
- [Quartz Scheduler](https://www.quartz-scheduler.org/documentation/)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
