---
title: "Операционные системы"
description: "Кратко: Комплексное руководство по операционным системам - от архитектуры ядра до управления процессами, памятью и файловыми системами с практическими примерами на Java."
tags:
  - basics
  - operating-systems
  - operating-systems-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Операционные системы

Кратко: Комплексное руководство по операционным системам — от архитектуры ядра до управления процессами, памятью и файловыми системами с практическими примерами на **Java**.

## Полезные ссылки

### Официальная документация
- [Linux Kernel Documentation](https://www.kernel.org/doc/html/latest/) — документация ядра **Linux**

### Ресурсы
- [Operating System Concepts](https://www.os-book.com/OS10/) — основные концепции ОС

### См. также
- [Computer Science](../computer-science/computer-science-basics.md)
- [Компьютерные сети](../networks/)

- [Linux и Bash: практический справочник](linux-handbook.md)
- [Принципы чистого кода](../clean-code.md)
- [Основы Git](../git-basics.md)
- [Java: основы](../../languages/java/java-basics.md)
## Содержание

- [Введение в операционные системы](#введение-в-операционные-системы)
  - [Основные функции ОС](#основные-функции-ос)
- [Архитектура операционных систем](#архитектура-операционных-систем)
  - [Архитектуры ядра](#архитектуры-ядра)
- [Управление процессами](#управление-процессами)
  - [Жизненный цикл процесса](#жизненный-цикл-процесса)
  - [Алгоритмы планирования](#алгоритмы-планирования)
- [Управление памятью](#управление-памятью)
  - [Стратегии выделения памяти](#стратегии-выделения-памяти)
  - [Виртуальная память](#виртуальная-память)
- [Файловые системы](#файловые-системы)
  - [Структура файловой системы](#структура-файловой-системы)
- [Ввод-вывод и устройства](#ввод-вывод-и-устройства)
  - [Драйверы устройств](#драйверы-устройств)
- [Синхронизация и параллелизм](#синхронизация-и-параллелизм)
  - [Проблемы синхронизации](#проблемы-синхронизации)
- [Безопасность операционной системы](#безопасность-операционной-системы)
  - [Аутентификация и авторизация](#аутентификация-и-авторизация)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Ключевые концепции](#ключевые-концепции)
  - [Практические рекомендации](#практические-рекомендации)
  - [Важность изучения ОС](#важность-изучения-ос)

## Введение в операционные системы

Операционная система (ОС) — это программное обеспечение, управляющее аппаратными ресурсами компьютера и предоставляющее интерфейс для пользовательских программ.

### Основные функции ОС

```java
/**
 * Основные функции операционной системы
 */
public class OSFunctions {

    /**
     * Перечисление основных функций ОС
     */
    public enum OSFunction {
        PROCESS_MANAGEMENT("Управление процессами",
            "Создание, планирование, синхронизация и завершение процессов"),

        MEMORY_MANAGEMENT("Управление памятью",
            "Выделение и освобождение памяти, виртуальная память, защита памяти"),

        FILE_SYSTEM("Файловая система",
            "Организация, хранение и доступ к файлам и директориям"),

        DEVICE_MANAGEMENT("Управление устройствами",
            "Управление аппаратными устройствами через драйверы"),

        USER_INTERFACE("Пользовательский интерфейс",
            "Предоставление интерфейса для взаимодействия с пользователем"),

        SECURITY("Безопасность",
            "Защита ресурсов, аутентификация, авторизация"),

        NETWORKING("Сетевое взаимодействие",
            "Управление сетевыми подключениями и протоколами"),

        ERROR_HANDLING("Обработка ошибок",
            "Обнаружение и восстановление после ошибок");

        private final String name;
        private final String description;

        OSFunction(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    /**
     * Типы операционных систем
     */
    public enum OSType {
        BATCH_SYSTEMS("Пакетные системы", "Выполнение задач пакетами без взаимодействия"),
        TIME_SHARING("Системы разделения времени", "Многопользовательские системы с интерактивностью"),
        REAL_TIME("Системы реального времени", "Гарантированные сроки отклика"),
        DISTRIBUTED("Распределенные системы", "Несколько компьютеров работают как единое целое"),
        EMBEDDED("Встроенные системы", "Специализированные ОС для устройств"),
        MOBILE("Мобильные ОС", "ОС для смартфонов и планшетов");

        private final String name;
        private final String description;

        OSType(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    /**
     * Демонстрация функций ОС
     */
    public void demonstrateOSFunctions() {
        System.out.println("=== Основные функции ОС ===");
        for (OSFunction function : OSFunction.values()) {
            System.out.println(function.getName() + ": " + function.getDescription());
        }

        System.out.println("\n=== Типы ОС ===");
        for (OSType type : OSType.values()) {
            System.out.println(type.name + ": " + type.description);
        }
    }
}
```

## Архитектура операционных систем

ОС могут иметь различную архитектуру в зависимости от требований и аппаратной платформы.

### Архитектуры ядра

```java
/**
 * Различные архитектуры ядер ОС
 */
public class KernelArchitectures {

    /**
     * Монолитное ядро
     * Все компоненты ядра работают в одном адресном пространстве
     */
    public static class MonolithicKernel {
        // Все компоненты в одном пространстве
        private ProcessManager processManager;
        private MemoryManager memoryManager;
        private FileSystemManager fileSystemManager;
        private DeviceManager deviceManager;

        public MonolithicKernel() {
            this.processManager = new ProcessManager();
            this.memoryManager = new MemoryManager();
            this.fileSystemManager = new FileSystemManager();
            this.deviceManager = new DeviceManager();
        }

        /**
         * Все функции доступны напрямую
         */
        public void handleSystemCall(String call) {
            switch (call) {
                case "fork":
                    processManager.createProcess();
                    break;
                case "malloc":
                    memoryManager.allocateMemory(1024);
                    break;
                case "open":
                    fileSystemManager.openFile("test.txt");
                    break;
                case "read":
                    deviceManager.readFromDevice("disk0");
                    break;
            }
        }

        // Заглушки для компонентов
        static class ProcessManager { void createProcess() { System.out.println("Процесс создан"); } }
        static class MemoryManager { void allocateMemory(int size) { System.out.println("Память выделена: " + size + " байт"); } }
        static class FileSystemManager { void openFile(String filename) { System.out.println("Файл открыт: " + filename); } }
        static class DeviceManager { void readFromDevice(String device) { System.out.println("Чтение с устройства: " + device); } }
    }

    /**
     * Микроядро
     * Только базовые функции в ядре, остальные - в пользовательском пространстве
     */
    public static class MicroKernel {
        // Только базовые функции
        private IPCManager ipcManager;  // Межпроцессное взаимодействие
        private Scheduler scheduler;    // Планировщик

        public MicroKernel() {
            this.ipcManager = new IPCManager();
            this.scheduler = new Scheduler();
        }

        /**
         * Большинство функций делегируется серверным процессам
         */
        public void handleSystemCall(String call) {
            // Отправка сообщения соответствующему серверу
            ipcManager.sendMessageToServer(call, getServerForCall(call));
        }

        private String getServerForCall(String call) {
            switch (call) {
                case "fork": return "process_server";
                case "malloc": return "memory_server";
                case "open": return "filesystem_server";
                case "read": return "device_server";
                default: return "unknown_server";
            }
        }

        // Заглушки
        static class IPCManager { void sendMessageToServer(String call, String server) { System.out.println("Отправка " + call + " серверу " + server); } }
        static class Scheduler { void schedule() { System.out.println("Планирование задач"); } }
    }

    /**
     * Гибридное ядро (модульное)
     * Комбинация монолитного и микроядерного подходов
     */
    public static class HybridKernel {
        // Базовые компоненты в ядре
        private CoreComponents core;

        // Загружаемые модули
        private Map<String, KernelModule> modules;

        public HybridKernel() {
            this.core = new CoreComponents();
            this.modules = new HashMap<>();
        }

        public void loadModule(String name, KernelModule module) {
            modules.put(name, module);
            System.out.println("Модуль загружен: " + name);
        }

        public void handleSystemCall(String call) {
            // Сначала проверяем встроенные функции
            if (core.handleBuiltInCall(call)) {
                return;
            }

            // Ищем подходящий модуль
            for (KernelModule module : modules.values()) {
                if (module.canHandle(call)) {
                    module.handle(call);
                    return;
                }
            }

            System.out.println("Неизвестный системный вызов: " + call);
        }

        interface KernelModule {
            boolean canHandle(String call);
            void handle(String call);
        }

        static class CoreComponents {
            boolean handleBuiltInCall(String call) {
                if (call.equals("yield")) {
                    System.out.println("Переключение контекста");
                    return true;
                }
                return false;
            }
        }
    }

    /**
     * Демонстрация архитектур ядер
     */
    public static void demonstrateArchitectures() {
        System.out.println("=== Архитектуры ядер ОС ===");

        System.out.println("\n1. Монолитное ядро (Linux, Unix):");
        MonolithicKernel monoKernel = new MonolithicKernel();
        monoKernel.handleSystemCall("fork");
        monoKernel.handleSystemCall("malloc");

        System.out.println("\n2. Микроядро (Minix, QNX):");
        MicroKernel microKernel = new MicroKernel();
        microKernel.handleSystemCall("fork");
        microKernel.handleSystemCall("malloc");

        System.out.println("\n3. Гибридное ядро (Windows NT, macOS):");
        HybridKernel hybridKernel = new HybridKernel();

        // Загрузка модулей
        hybridKernel.loadModule("network", new KernelModule() {
            public boolean canHandle(String call) { return call.startsWith("net_"); }
            public void handle(String call) { System.out.println("Обработка сетевого вызова: " + call); }
        });

        hybridKernel.handleSystemCall("yield");      // Встроенная функция
        hybridKernel.handleSystemCall("net_send");   // Через модуль
        hybridKernel.handleSystemCall("unknown");    // Неизвестный вызов
    }
}
```

## Управление процессами

Процессы — это выполняющиеся программы, и ОС отвечает за их создание, планирование и завершение.

### Жизненный цикл процесса

```java
/**
 * Управление жизненным циклом процессов
 */
public class ProcessManagement {

    /**
     * Состояния процесса
     */
    public enum ProcessState {
        NEW("Создан", "Процесс только что создан"),
        READY("Готов", "Ожидает выделения процессора"),
        RUNNING("Выполняется", "Активно использует процессор"),
        WAITING("Ожидает", "Ожидает события или ресурса"),
        TERMINATED("Завершен", "Процесс завершен");

        private final String name;
        private final String description;

        ProcessState(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    /**
     * Блок управления процессом (PCB)
     */
    public static class ProcessControlBlock {
        private int processId;
        private ProcessState state;
        private int priority;
        private long creationTime;
        private long cpuTime;
        private Map<String, Object> resources;
        private int[] registers;  // Сохраненные регистры
        private int programCounter;
        private Map<String, Integer> memoryMap;  // Карта памяти

        public ProcessControlBlock(int pid) {
            this.processId = pid;
            this.state = ProcessState.NEW;
            this.priority = 0;
            this.creationTime = System.currentTimeMillis();
            this.cpuTime = 0;
            this.resources = new HashMap<>();
            this.registers = new int[16];  // 16 регистров
            this.programCounter = 0;
            this.memoryMap = new HashMap<>();
        }

        public void saveContext(int[] currentRegisters, int pc) {
            System.arraycopy(currentRegisters, 0, registers, 0, registers.length);
            this.programCounter = pc;
        }

        public void restoreContext(int[] targetRegisters) {
            System.arraycopy(registers, 0, targetRegisters, 0, registers.length);
        }

        public void setState(ProcessState state) {
            System.out.println("Процесс " + processId + ": " + this.state + " -> " + state);
            this.state = state;
        }

        public ProcessState getState() { return state; }
        public int getProcessId() { return processId; }
        public int getPriority() { return priority; }
    }

    /**
     * Планировщик процессов
     */
    public static class ProcessScheduler {
        private Queue<ProcessControlBlock> readyQueue;
        private ProcessControlBlock currentProcess;
        private int timeQuantum;  // Квант времени для Round Robin

        public ProcessScheduler(int timeQuantum) {
            this.readyQueue = new LinkedList<>();
            this.timeQuantum = timeQuantum;
            this.currentProcess = null;
        }

        /**
         * Добавление процесса в очередь готовых
         */
        public void addProcess(ProcessControlBlock pcb) {
            pcb.setState(ProcessState.READY);
            readyQueue.add(pcb);
        }

        /**
         * Переключение контекста (context switch)
         */
        public void contextSwitch() {
            if (currentProcess != null) {
                // Сохранение контекста текущего процесса
                currentProcess.saveContext(new int[16], 100);  // Имитация

                if (currentProcess.getState() == ProcessState.RUNNING) {
                    // Возврат в очередь готовых
                    currentProcess.setState(ProcessState.READY);
                    readyQueue.add(currentProcess);
                }
            }

            // Выбор следующего процесса
            if (!readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                currentProcess.setState(ProcessState.RUNNING);

                // Восстановление контекста
                int[] registers = new int[16];
                currentProcess.restoreContext(registers);

                System.out.println("Переключение на процесс " + currentProcess.getProcessId());
            } else {
                currentProcess = null;
            }
        }

        /**
         * Обработка таймера (Round Robin)
         */
        public void handleTimerInterrupt() {
            if (currentProcess != null) {
                System.out.println("Таймер истек для процесса " + currentProcess.getProcessId());
                contextSwitch();
            }
        }

        public ProcessControlBlock getCurrentProcess() {
            return currentProcess;
        }

        public int getQueueSize() {
            return readyQueue.size();
        }
    }

    /**
     * Создание и управление процессами
     */
    public static class ProcessCreator {
        private int nextProcessId = 1;
        private ProcessScheduler scheduler;

        public ProcessCreator(ProcessScheduler scheduler) {
            this.scheduler = scheduler;
        }

        /**
         * Создание нового процесса
         */
        public ProcessControlBlock createProcess() {
            ProcessControlBlock pcb = new ProcessControlBlock(nextProcessId++);
            System.out.println("Создан процесс " + pcb.getProcessId());

            // Добавление в планировщик
            scheduler.addProcess(pcb);

            return pcb;
        }

        /**
         * Завершение процесса
         */
        public void terminateProcess(ProcessControlBlock pcb) {
            pcb.setState(ProcessState.TERMINATED);
            System.out.println("Процесс " + pcb.getProcessId() + " завершен");
        }
    }

    /**
     * Демонстрация управления процессами
     */
    public static void demonstrateProcessManagement() {
        System.out.println("=== Управление процессами ===");

        // Создание планировщика
        ProcessScheduler scheduler = new ProcessScheduler(100);  // 100мс квант
        ProcessCreator creator = new ProcessCreator(scheduler);

        // Создание процессов
        ProcessControlBlock p1 = creator.createProcess();
        ProcessControlBlock p2 = creator.createProcess();
        ProcessControlBlock p3 = creator.createProcess();

        System.out.println("\nСостояния процессов:");
        System.out.println("P1: " + p1.getState());
        System.out.println("P2: " + p2.getState());
        System.out.println("P3: " + p3.getState());

        // Имитация планирования
        System.out.println("\nПланирование процессов:");
        for (int i = 0; i < 5; i++) {
            scheduler.contextSwitch();
            if (scheduler.getCurrentProcess() != null) {
                System.out.println("Текущий процесс: " + scheduler.getCurrentProcess().getProcessId());

                // Имитация истечения кванта времени
                if (i % 2 == 1) {
                    scheduler.handleTimerInterrupt();
                }
            }
        }

        // Завершение процесса
        if (scheduler.getCurrentProcess() != null) {
            creator.terminateProcess(scheduler.getCurrentProcess());
            scheduler.contextSwitch();  // Переключение на следующий
        }

        System.out.println("\nОчередь готовых процессов: " + scheduler.getQueueSize());
    }
}
```

### Алгоритмы планирования

```java
/**
 * Алгоритмы планирования процессов
 */
public class SchedulingAlgorithms {

    /**
     * Процесс для планирования
     */
    public static class ScheduledProcess {
        private String name;
        private int arrivalTime;    // Время прибытия
        private int burstTime;     // Время выполнения
        private int remainingTime; // Оставшееся время
        private int priority;      // Приоритет
        private int waitingTime;   // Время ожидания
        private int turnaroundTime; // Время выполнения

        public ScheduledProcess(String name, int arrivalTime, int burstTime) {
            this.name = name;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
            this.priority = 0;
        }

        public ScheduledProcess(String name, int arrivalTime, int burstTime, int priority) {
            this(name, arrivalTime, burstTime);
            this.priority = priority;
        }

        // Геттеры и сеттеры
        public String getName() { return name; }
        public int getArrivalTime() { return arrivalTime; }
        public int getBurstTime() { return burstTime; }
        public int getRemainingTime() { return remainingTime; }
        public void setRemainingTime(int time) { this.remainingTime = time; }
        public int getPriority() { return priority; }
        public int getWaitingTime() { return waitingTime; }
        public void setWaitingTime(int time) { this.waitingTime = time; }
        public int getTurnaroundTime() { return turnaroundTime; }
        public void setTurnaroundTime(int time) { this.turnaroundTime = time; }
    }

    /**
     * FCFS (First Come, First Served)
     */
    public static class FCFSScheduler {
        public void schedule(List<ScheduledProcess> processes) {
            System.out.println("=== FCFS Планирование ===");

            int currentTime = 0;
            int totalWaitingTime = 0;
            int totalTurnaroundTime = 0;

            for (ScheduledProcess process : processes) {
                // Ожидание до прибытия процесса
                if (currentTime < process.getArrivalTime()) {
                    currentTime = process.getArrivalTime();
                }

                int waitingTime = currentTime - process.getArrivalTime();
                int turnaroundTime = waitingTime + process.getBurstTime();

                process.setWaitingTime(waitingTime);
                process.setTurnaroundTime(turnaroundTime);

                totalWaitingTime += waitingTime;
                totalTurnaroundTime += turnaroundTime;

                currentTime += process.getBurstTime();

                System.out.printf("Процесс %s: Ожидание=%d, Выполнение=%d%n",
                                process.getName(), waitingTime, turnaroundTime);
            }

            System.out.printf("Среднее время ожидания: %.2f%n",
                            (double) totalWaitingTime / processes.size());
            System.out.printf("Среднее время выполнения: %.2f%n",
                            (double) totalTurnaroundTime / processes.size());
        }
    }

    /**
     * SJF (Shortest Job First)
     */
    public static class SJFScheduler {
        public void schedule(List<ScheduledProcess> processes) {
            System.out.println("=== SJF Планирование ===");

            List<ScheduledProcess> readyQueue = new ArrayList<>();
            int currentTime = 0;
            int completed = 0;
            int totalWaitingTime = 0;
            int totalTurnaroundTime = 0;

            // Сортировка по времени прибытия
            processes.sort(Comparator.comparingInt(ScheduledProcess::getArrivalTime));

            while (completed < processes.size()) {
                // Добавление прибывших процессов в очередь
                for (ScheduledProcess p : processes) {
                    if (p.getArrivalTime() <= currentTime && !readyQueue.contains(p) &&
                        p.getRemainingTime() > 0) {
                        readyQueue.add(p);
                    }
                }

                if (readyQueue.isEmpty()) {
                    currentTime++;
                    continue;
                }

                // Выбор процесса с минимальным оставшимся временем
                ScheduledProcess shortest = readyQueue.stream()
                    .min(Comparator.comparingInt(ScheduledProcess::getRemainingTime))
                    .orElse(null);

                readyQueue.remove(shortest);

                // Выполнение на 1 единицу времени
                shortest.setRemainingTime(shortest.getRemainingTime() - 1);
                currentTime++;

                if (shortest.getRemainingTime() == 0) {
                    completed++;
                    int turnaroundTime = currentTime - shortest.getArrivalTime();
                    int waitingTime = turnaroundTime - shortest.getBurstTime();

                    shortest.setWaitingTime(waitingTime);
                    shortest.setTurnaroundTime(turnaroundTime);

                    totalWaitingTime += waitingTime;
                    totalTurnaroundTime += turnaroundTime;

                    System.out.printf("Процесс %s завершен: Ожидание=%d, Выполнение=%d%n",
                                    shortest.getName(), waitingTime, turnaroundTime);
                }
            }

            System.out.printf("Среднее время ожидания: %.2f%n",
                            (double) totalWaitingTime / processes.size());
            System.out.printf("Среднее время выполнения: %.2f%n",
                            (double) totalTurnaroundTime / processes.size());
        }
    }

    /**
     * Round Robin
     */
    public static class RoundRobinScheduler {
        private int timeQuantum;

        public RoundRobinScheduler(int timeQuantum) {
            this.timeQuantum = timeQuantum;
        }

        public void schedule(List<ScheduledProcess> processes) {
            System.out.println("=== Round Robin Планирование (квант=" + timeQuantum + ") ===");

            Queue<ScheduledProcess> readyQueue = new LinkedList<>();
            int currentTime = 0;
            int totalWaitingTime = 0;
            int totalTurnaroundTime = 0;

            // Добавление процессов, прибывших в время 0
            for (ScheduledProcess p : processes) {
                if (p.getArrivalTime() == 0) {
                    readyQueue.add(p);
                }
            }

            while (!readyQueue.isEmpty()) {
                ScheduledProcess current = readyQueue.poll();

                int executionTime = Math.min(timeQuantum, current.getRemainingTime());
                current.setRemainingTime(current.getRemainingTime() - executionTime);
                currentTime += executionTime;

                // Добавление новых прибывших процессов
                for (ScheduledProcess p : processes) {
                    if (p.getArrivalTime() <= currentTime && p.getArrivalTime() > currentTime - executionTime &&
                        p != current && !readyQueue.contains(p) && p.getRemainingTime() > 0) {
                        readyQueue.add(p);
                    }
                }

                if (current.getRemainingTime() > 0) {
                    // Процесс не завершен, возвращаем в очередь
                    readyQueue.add(current);
                } else {
                    // Процесс завершен
                    int turnaroundTime = currentTime - current.getArrivalTime();
                    int waitingTime = turnaroundTime - current.getBurstTime();

                    current.setWaitingTime(waitingTime);
                    current.setTurnaroundTime(turnaroundTime);

                    totalWaitingTime += waitingTime;
                    totalTurnaroundTime += turnaroundTime;

                    System.out.printf("Процесс %s завершен: Ожидание=%d, Выполнение=%d%n",
                                    current.getName(), waitingTime, turnaroundTime);
                }
            }

            System.out.printf("Среднее время ожидания: %.2f%n",
                            (double) totalWaitingTime / processes.size());
            System.out.printf("Среднее время выполнения: %.2f%n",
                            (double) totalTurnaroundTime / processes.size());
        }
    }

    /**
     * Демонстрация алгоритмов планирования
     */
    public static void demonstrateScheduling() {
        // Создание тестовых процессов
        List<ScheduledProcess> processes = Arrays.asList(
            new ScheduledProcess("P1", 0, 6),
            new ScheduledProcess("P2", 1, 4),
            new ScheduledProcess("P3", 2, 8),
            new ScheduledProcess("P4", 3, 3)
        );

        // FCFS
        FCFSScheduler fcfs = new FCFSScheduler();
        fcfs.schedule(new ArrayList<>(processes));

        // SJF
        SJFScheduler sjf = new SJFScheduler();
        sjf.schedule(new ArrayList<>(processes));

        // Round Robin
        RoundRobinScheduler rr = new RoundRobinScheduler(3);
        rr.schedule(new ArrayList<>(processes));
    }
}
```

## Управление памятью

ОС отвечает за эффективное использование оперативной памяти.

### Стратегии выделения памяти

```java
/**
 * Стратегии управления памятью
 */
public class MemoryManagement {

    /**
     * Блок памяти
     */
    public static class MemoryBlock {
        private int startAddress;
        private int size;
        private boolean allocated;
        private String processId;

        public MemoryBlock(int start, int size) {
            this.startAddress = start;
            this.size = size;
            this.allocated = false;
            this.processId = null;
        }

        public boolean isAllocated() { return allocated; }
        public void allocate(String processId) {
            this.allocated = true;
            this.processId = processId;
        }
        public void deallocate() {
            this.allocated = false;
            this.processId = null;
        }
        public int getSize() { return size; }
        public String getProcessId() { return processId; }
        public int getStartAddress() { return startAddress; }
        public int getEndAddress() { return startAddress + size - 1; }
    }

    /**
     * First Fit - первый подходящий блок
     */
    public static class FirstFitAllocator {
        private List<MemoryBlock> memoryBlocks;

        public FirstFitAllocator(int totalMemory, int blockSize) {
            memoryBlocks = new ArrayList<>();
            // Разделение на блоки фиксированного размера
            for (int i = 0; i < totalMemory; i += blockSize) {
                int size = Math.min(blockSize, totalMemory - i);
                memoryBlocks.add(new MemoryBlock(i, size));
            }
        }

        public Integer allocate(String processId, int requestedSize) {
            for (MemoryBlock block : memoryBlocks) {
                if (!block.isAllocated() && block.getSize() >= requestedSize) {
                    block.allocate(processId);
                    System.out.println("Выделено " + requestedSize + " байт процессу " +
                                     processId + " по адресу " + block.getStartAddress());
                    return block.getStartAddress();
                }
            }
            System.out.println("Не удалось выделить память процессу " + processId);
            return null;
        }

        public void deallocate(String processId) {
            for (MemoryBlock block : memoryBlocks) {
                if (block.isAllocated() && processId.equals(block.getProcessId())) {
                    block.deallocate();
                    System.out.println("Освобождена память процесса " + processId +
                                     " по адресу " + block.getStartAddress());
                    return;
                }
            }
        }

        public void displayMemory() {
            System.out.println("Состояние памяти:");
            for (MemoryBlock block : memoryBlocks) {
                String status = block.isAllocated() ?
                    "Занято (" + block.getProcessId() + ")" : "Свободно";
                System.out.println("Адрес " + block.getStartAddress() + "-" + block.getEndAddress() +
                                 ": " + status);
            }
        }
    }

    /**
     * Best Fit - наилучший подходящий блок
     */
    public static class BestFitAllocator {
        private List<MemoryBlock> memoryBlocks;

        public BestFitAllocator(int totalMemory, int blockSize) {
            memoryBlocks = new ArrayList<>();
            for (int i = 0; i < totalMemory; i += blockSize) {
                int size = Math.min(blockSize, totalMemory - i);
                memoryBlocks.add(new MemoryBlock(i, size));
            }
        }

        public Integer allocate(String processId, int requestedSize) {
            MemoryBlock bestBlock = null;
            int smallestWaste = Integer.MAX_VALUE;

            for (MemoryBlock block : memoryBlocks) {
                if (!block.isAllocated() && block.getSize() >= requestedSize) {
                    int waste = block.getSize() - requestedSize;
                    if (waste < smallestWaste) {
                        smallestWaste = waste;
                        bestBlock = block;
                    }
                }
            }

            if (bestBlock != null) {
                bestBlock.allocate(processId);
                System.out.println("Best Fit: Выделено " + requestedSize + " байт процессу " +
                                 processId + " по адресу " + bestBlock.getStartAddress());
                return bestBlock.getStartAddress();
            }

            System.out.println("Best Fit: Не удалось выделить память процессу " + processId);
            return null;
        }

        public void deallocate(String processId) {
            for (MemoryBlock block : memoryBlocks) {
                if (block.isAllocated() && processId.equals(block.getProcessId())) {
                    block.deallocate();
                    System.out.println("Освобождена память процесса " + processId);
                    return;
                }
            }
        }
    }

    /**
     * Worst Fit - наихудший подходящий блок
     */
    public static class WorstFitAllocator {
        private List<MemoryBlock> memoryBlocks;

        public WorstFitAllocator(int totalMemory, int blockSize) {
            memoryBlocks = new ArrayList<>();
            for (int i = 0; i < totalMemory; i += blockSize) {
                int size = Math.min(blockSize, totalMemory - i);
                memoryBlocks.add(new MemoryBlock(i, size));
            }
        }

        public Integer allocate(String processId, int requestedSize) {
            MemoryBlock worstBlock = null;
            int largestSize = -1;

            for (MemoryBlock block : memoryBlocks) {
                if (!block.isAllocated() && block.getSize() >= requestedSize) {
                    if (block.getSize() > largestSize) {
                        largestSize = block.getSize();
                        worstBlock = block;
                    }
                }
            }

            if (worstBlock != null) {
                worstBlock.allocate(processId);
                System.out.println("Worst Fit: Выделено " + requestedSize + " байт процессу " +
                                 processId + " по адресу " + worstBlock.getStartAddress());
                return worstBlock.getStartAddress();
            }

            System.out.println("Worst Fit: Не удалось выделить память процессу " + processId);
            return null;
        }

        public void deallocate(String processId) {
            for (MemoryBlock block : memoryBlocks) {
                if (block.isAllocated() && processId.equals(block.getProcessId())) {
                    block.deallocate();
                    System.out.println("Освобождена память процесса " + processId);
                    return;
                }
            }
        }
    }

    /**
     * Демонстрация стратегий выделения памяти
     */
    public static void demonstrateMemoryAllocation() {
        System.out.println("=== Стратегии выделения памяти ===");

        int totalMemory = 1000;
        int blockSize = 100;

        // First Fit
        System.out.println("\n--- First Fit ---");
        FirstFitAllocator firstFit = new FirstFitAllocator(totalMemory, blockSize);
        firstFit.allocate("P1", 150);
        firstFit.allocate("P2", 200);
        firstFit.allocate("P3", 100);
        firstFit.displayMemory();
        firstFit.deallocate("P2");
        firstFit.allocate("P4", 120);

        // Best Fit
        System.out.println("\n--- Best Fit ---");
        BestFitAllocator bestFit = new BestFitAllocator(totalMemory, blockSize);
        bestFit.allocate("P1", 150);
        bestFit.allocate("P2", 200);
        bestFit.allocate("P3", 100);

        // Worst Fit
        System.out.println("\n--- Worst Fit ---");
        WorstFitAllocator worstFit = new WorstFitAllocator(totalMemory, blockSize);
        worstFit.allocate("P1", 150);
        worstFit.allocate("P2", 200);
        worstFit.allocate("P3", 100);
    }
}
```

### Виртуальная память

```java
/**
 * Реализация виртуальной памяти
 */
public class VirtualMemory {

    /**
     * Страница памяти
     */
    public static class Page {
        private int pageNumber;
        private int frameNumber;  // -1 если не загружена
        private boolean valid;    // Бит валидности
        private boolean dirty;    // Бит модификации
        private long lastAccess;  // Время последнего доступа

        public Page(int pageNumber) {
            this.pageNumber = pageNumber;
            this.frameNumber = -1;
            this.valid = false;
            this.dirty = false;
            this.lastAccess = 0;
        }

        // Геттеры и сеттеры
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public boolean isDirty() { return dirty; }
        public void setDirty(boolean dirty) { this.dirty = dirty; }
        public int getFrameNumber() { return frameNumber; }
        public void setFrameNumber(int frame) { this.frameNumber = frame; }
        public long getLastAccess() { return lastAccess; }
        public void updateAccess() { this.lastAccess = System.currentTimeMillis(); }
    }

    /**
     * Таблица страниц
     */
    public static class PageTable {
        private Map<Integer, Page> pages;
        private int pageSize;

        public PageTable(int pageSize) {
            this.pages = new HashMap<>();
            this.pageSize = pageSize;
        }

        public Page getPage(int virtualAddress) {
            int pageNumber = virtualAddress / pageSize;
            return pages.computeIfAbsent(pageNumber, Page::new);
        }

        public int translateAddress(int virtualAddress) {
            Page page = getPage(virtualAddress);
            if (!page.isValid()) {
                throw new PageFaultException("Page fault: страница " + (virtualAddress / pageSize) + " не загружена");
            }

            int offset = virtualAddress % pageSize;
            return page.getFrameNumber() * pageSize + offset;
        }

        public void loadPage(int pageNumber, int frameNumber) {
            Page page = pages.computeIfAbsent(pageNumber, Page::new);
            page.setFrameNumber(frameNumber);
            page.setValid(true);
            page.updateAccess();
        }

        public Collection<Page> getAllPages() {
            return pages.values();
        }
    }

    /**
     * Исключение page fault
     */
    public static class PageFaultException extends RuntimeException {
        public PageFaultException(String message) {
            super(message);
        }
    }

    /**
     * Менеджер виртуальной памяти
     */
    public static class VirtualMemoryManager {
        private PageTable pageTable;
        private boolean[] physicalFrames;  // Доступные физические фреймы
        private Map<Integer, byte[]> diskStorage;  // Имитация диска
        private int nextFrame;

        public VirtualMemoryManager(int numFrames, int pageSize) {
            this.pageTable = new PageTable(pageSize);
            this.physicalFrames = new boolean[numFrames];
            this.diskStorage = new HashMap<>();
            this.nextFrame = 0;
        }

        /**
         * Чтение из виртуальной памяти
         */
        public byte read(int virtualAddress) {
            try {
                int physicalAddress = pageTable.translateAddress(virtualAddress);
                return readFromPhysicalMemory(physicalAddress);
            } catch (PageFaultException e) {
                System.out.println(e.getMessage());
                handlePageFault(virtualAddress / pageTable.pageSize);
                // Повторная попытка после обработки page fault
                int physicalAddress = pageTable.translateAddress(virtualAddress);
                return readFromPhysicalMemory(physicalAddress);
            }
        }

        /**
         * Запись в виртуальную память
         */
        public void write(int virtualAddress, byte value) {
            try {
                int physicalAddress = pageTable.translateAddress(virtualAddress);
                writeToPhysicalMemory(physicalAddress, value);
                pageTable.getPage(virtualAddress).setDirty(true);
            } catch (PageFaultException e) {
                System.out.println(e.getMessage());
                handlePageFault(virtualAddress / pageTable.pageSize);
                // Повторная попытка
                int physicalAddress = pageTable.translateAddress(virtualAddress);
                writeToPhysicalMemory(physicalAddress, value);
                pageTable.getPage(virtualAddress).setDirty(true);
            }
        }

        /**
         * Обработка page fault
         */
        private void handlePageFault(int pageNumber) {
            System.out.println("Обработка page fault для страницы " + pageNumber);

            // Поиск свободного фрейма
            int freeFrame = findFreeFrame();

            if (freeFrame == -1) {
                // Все фреймы заняты, нужна замена страницы
                freeFrame = replacePage();
            }

            // Загрузка страницы с диска
            loadPageFromDisk(pageNumber, freeFrame);
        }

        private int findFreeFrame() {
            for (int i = 0; i < physicalFrames.length; i++) {
                if (!physicalFrames[i]) {
                    physicalFrames[i] = true;
                    return i;
                }
            }
            return -1;
        }

        private int replacePage() {
            // Простая стратегия: FIFO
            int frameToReplace = nextFrame;
            nextFrame = (nextFrame + 1) % physicalFrames.length;

            // Сохранение грязной страницы на диск
            for (Map.Entry<Integer, Page> entry : pageTable.pages.entrySet()) {
                Page page = entry.getValue();
                if (page.getFrameNumber() == frameToReplace && page.isValid()) {
                    if (page.isDirty()) {
                        savePageToDisk(entry.getKey(), frameToReplace);
                    }
                    page.setValid(false);
                    break;
                }
            }

            return frameToReplace;
        }

        private void loadPageFromDisk(int pageNumber, int frameNumber) {
            // Имитация загрузки с диска
            System.out.println("Загрузка страницы " + pageNumber + " во фрейм " + frameNumber);
            pageTable.loadPage(pageNumber, frameNumber);
        }

        private void savePageToDisk(int pageNumber, int frameNumber) {
            // Имитация сохранения на диск
            System.out.println("Сохранение страницы " + pageNumber + " на диск");
            diskStorage.put(pageNumber, new byte[4096]);  // 4KB страница
        }

        // Заглушки для физической памяти
        private byte readFromPhysicalMemory(int address) { return 0; }
        private void writeToPhysicalMemory(int address, byte value) { }
    }

    /**
     * Демонстрация виртуальной памяти
     */
    public static void demonstrateVirtualMemory() {
        System.out.println("=== Виртуальная память ===");

        VirtualMemoryManager vmm = new VirtualMemoryManager(4, 4096);  // 4 фрейма, 4KB страницы

        // Имитация доступа к памяти
        System.out.println("Чтение из виртуального адреса 0:");
        byte data = vmm.read(0);

        System.out.println("Запись в виртуальный адрес 8192:");
        vmm.write(8192, (byte) 42);

        System.out.println("Чтение из виртуального адреса 16384 (вызовет page fault):");
        data = vmm.read(16384);

        System.out.println("Повторное чтение из виртуального адреса 16384:");
        data = vmm.read(16384);
    }
}
```

## Файловые системы

Файловые системы организуют хранение и доступ к данным на дисках.

### Структура файловой системы

```java
/**
 * Структура файловой системы
 */
public class FileSystemStructure {

    /**
     * Файл
     */
    public static class File {
        private String name;
        private long size;
        private Date creationDate;
        private Date modificationDate;
        private String permissions;
        private List<Integer> blocks;  // Номера блоков на диске

        public File(String name) {
            this.name = name;
            this.size = 0;
            this.creationDate = new Date();
            this.modificationDate = new Date();
            this.permissions = "rw-r--r--";
            this.blocks = new ArrayList<>();
        }

        public void write(byte[] data) {
            // Имитация записи
            this.size = data.length;
            this.modificationDate = new Date();

            // Выделение блоков
            int blocksNeeded = (int) Math.ceil((double) data.length / 4096);  // 4KB блоки
            for (int i = 0; i < blocksNeeded; i++) {
                blocks.add(allocateBlock());
            }
        }

        private int allocateBlock() {
            // Упрощенная аллокация блоков
            return (int) (Math.random() * 10000);
        }

        public String getInfo() {
            return String.format("Файл: %s, Размер: %d байт, Создан: %s, Изменен: %s",
                               name, size, creationDate, modificationDate);
        }
    }

    /**
     * Директория
     */
    public static class Directory {
        private String name;
        private Directory parent;
        private Map<String, File> files;
        private Map<String, Directory> subdirectories;

        public Directory(String name, Directory parent) {
            this.name = name;
            this.parent = parent;
            this.files = new HashMap<>();
            this.subdirectories = new HashMap<>();
        }

        public void addFile(File file) {
            files.put(file.name, file);
        }

        public void addDirectory(Directory dir) {
            subdirectories.put(dir.name, dir);
        }

        public File getFile(String name) {
            return files.get(name);
        }

        public Directory getDirectory(String name) {
            return subdirectories.get(name);
        }

        public String getPath() {
            if (parent == null) return "/";
            return parent.getPath() + name + "/";
        }

        public void listContents() {
            System.out.println("Содержимое директории " + getPath() + ":");

            System.out.println("Файлы:");
            for (File file : files.values()) {
                System.out.println("  " + file.name);
            }

            System.out.println("Директории:");
            for (Directory dir : subdirectories.values()) {
                System.out.println("  " + dir.name + "/");
            }
        }
    }

    /**
     * Файловая система
     */
    public static class FileSystem {
        private Directory root;
        private Map<Integer, byte[]> diskBlocks;  // Имитация дисковых блоков
        private int blockSize;

        public FileSystem(int blockSize) {
            this.root = new Directory("", null);
            this.diskBlocks = new HashMap<>();
            this.blockSize = blockSize;
        }

        public Directory getRoot() {
            return root;
        }

        /**
         * Создание файла
         */
        public File createFile(String path, byte[] data) {
            String[] parts = path.split("/");
            Directory current = root;

            // Навигация к родительской директории
            for (int i = 1; i < parts.length - 1; i++) {
                current = current.getDirectory(parts[i]);
                if (current == null) {
                    throw new IllegalArgumentException("Путь не найден: " + path);
                }
            }

            String fileName = parts[parts.length - 1];
            File file = new File(fileName);
            file.write(data);
            current.addFile(file);

            System.out.println("Создан файл: " + path);
            return file;
        }

        /**
         * Чтение файла
         */
        public byte[] readFile(String path) {
            File file = findFile(path);
            if (file == null) {
                throw new IllegalArgumentException("Файл не найден: " + path);
            }

            // Имитация чтения из блоков
            byte[] data = new byte[(int) file.size];
            // В реальности данные читались бы из diskBlocks

            System.out.println("Прочитан файл: " + path);
            return data;
        }

        private File findFile(String path) {
            String[] parts = path.split("/");
            Directory current = root;

            for (int i = 1; i < parts.length - 1; i++) {
                current = current.getDirectory(parts[i]);
                if (current == null) return null;
            }

            return current.getFile(parts[parts.length - 1]);
        }

        /**
         * Создание директории
         */
        public Directory createDirectory(String path) {
            String[] parts = path.split("/");
            Directory current = root;

            for (int i = 1; i < parts.length; i++) {
                Directory next = current.getDirectory(parts[i]);
                if (next == null) {
                    next = new Directory(parts[i], current);
                    current.addDirectory(next);
                    System.out.println("Создана директория: " + next.getPath());
                }
                current = next;
            }

            return current;
        }

        /**
         * Удаление файла или директории
         */
        public boolean delete(String path) {
            String[] parts = path.split("/");
            Directory current = root;

            for (int i = 1; i < parts.length - 1; i++) {
                current = current.getDirectory(parts[i]);
                if (current == null) return false;
            }

            String name = parts[parts.length - 1];

            if (current.files.containsKey(name)) {
                current.files.remove(name);
                System.out.println("Удален файл: " + path);
                return true;
            } else if (current.subdirectories.containsKey(name)) {
                current.subdirectories.remove(name);
                System.out.println("Удалена директория: " + path);
                return true;
            }

            return false;
        }
    }

    /**
     * Демонстрация файловой системы
     */
    public static void demonstrateFileSystem() {
        System.out.println("=== Файловая система ===");

        FileSystem fs = new FileSystem(4096);

        // Создание директорий
        fs.createDirectory("/home");
        fs.createDirectory("/home/user");
        fs.createDirectory("/home/user/documents");

        // Создание файлов
        File file1 = fs.createFile("/home/user/README.txt", "Hello, World!".getBytes());
        File file2 = fs.createFile("/home/user/documents/notes.txt", "My notes...".getBytes());

        System.out.println("\nИнформация о файлах:");
        System.out.println(file1.getInfo());

        // Чтение файла
        byte[] data = fs.readFile("/home/user/README.txt");

        // Список содержимого
        Directory userDir = fs.getRoot().getDirectory("home").getDirectory("user");
        userDir.listContents();

        // Удаление
        fs.delete("/home/user/README.txt");
        userDir.listContents();
    }
}
```

## Ввод-вывод и устройства

ОС управляет взаимодействием с аппаратными устройствами.

### Драйверы устройств

```java
/**
 * Система драйверов устройств
 */
public class DeviceDrivers {

    /**
     * Интерфейс драйвера устройства
     */
    public interface DeviceDriver {
        String getDeviceType();
        boolean initialize();
        void shutdown();
        Object read();
        void write(Object data);
        boolean isReady();
    }

    /**
     * Драйвер жесткого диска
     */
    public static class HardDiskDriver implements DeviceDriver {
        private boolean initialized;
        private Map<Long, byte[]> diskBlocks;
        private static final int BLOCK_SIZE = 512;

        public HardDiskDriver() {
            this.diskBlocks = new HashMap<>();
            this.initialized = false;
        }

        @Override
        public String getDeviceType() {
            return "Hard Disk";
        }

        @Override
        public boolean initialize() {
            // Имитация инициализации диска
            System.out.println("Инициализация жесткого диска...");
            try {
                Thread.sleep(100);  // Имитация задержки
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            initialized = true;
            System.out.println("Жесткий диск инициализирован");
            return true;
        }

        @Override
        public void shutdown() {
            System.out.println("Отключение жесткого диска...");
            initialized = false;
        }

        @Override
        public Object read() {
            if (!initialized) return null;

            // Имитация чтения блока
            long blockNumber = (long) (Math.random() * 1000000);
            byte[] data = diskBlocks.computeIfAbsent(blockNumber, k -> new byte[BLOCK_SIZE]);

            System.out.println("Прочитан блок " + blockNumber + " с диска");
            return data;
        }

        @Override
        public void write(Object data) {
            if (!initialized || !(data instanceof byte[])) return;

            byte[] blockData = (byte[]) data;
            long blockNumber = (long) (Math.random() * 1000000);
            diskBlocks.put(blockNumber, Arrays.copyOf(blockData, BLOCK_SIZE));

            System.out.println("Записан блок " + blockNumber + " на диск");
        }

        @Override
        public boolean isReady() {
            return initialized;
        }
    }

    /**
     * Драйвер сетевой карты
     */
    public static class NetworkCardDriver implements DeviceDriver {
        private boolean initialized;
        private Queue<byte[]> receiveBuffer;
        private String ipAddress;

        public NetworkCardDriver(String ipAddress) {
            this.ipAddress = ipAddress;
            this.receiveBuffer = new LinkedList<>();
            this.initialized = false;
        }

        @Override
        public String getDeviceType() {
            return "Network Card";
        }

        @Override
        public boolean initialize() {
            System.out.println("Инициализация сетевой карты " + ipAddress + "...");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            initialized = true;
            System.out.println("Сетевая карта инициализирована");
            return true;
        }

        @Override
        public void shutdown() {
            System.out.println("Отключение сетевой карты...");
            initialized = false;
        }

        @Override
        public Object read() {
            if (!initialized || receiveBuffer.isEmpty()) return null;

            byte[] packet = receiveBuffer.poll();
            System.out.println("Получен сетевой пакет (" + packet.length + " байт)");
            return packet;
        }

        @Override
        public void write(Object data) {
            if (!initialized || !(data instanceof byte[])) return;

            byte[] packet = (byte[]) data;
            System.out.println("Отправлен сетевой пакет (" + packet.length + " байт) на " + ipAddress);

            // Имитация получения ответа
            byte[] response = ("HTTP/1.1 200 OK\r\nContent-Length: 0\r\n\r\n").getBytes();
            receiveBuffer.offer(response);
        }

        @Override
        public boolean isReady() {
            return initialized;
        }

        public void receivePacket(byte[] packet) {
            if (initialized) {
                receiveBuffer.offer(packet);
                System.out.println("Пакет добавлен в буфер приема");
            }
        }
    }

    /**
     * Менеджер устройств
     */
    public static class DeviceManager {
        private Map<String, DeviceDriver> drivers;

        public DeviceManager() {
            this.drivers = new HashMap<>();
        }

        public void registerDriver(String deviceId, DeviceDriver driver) {
            if (driver.initialize()) {
                drivers.put(deviceId, driver);
                System.out.println("Драйвер для устройства " + deviceId + " зарегистрирован");
            } else {
                System.out.println("Не удалось инициализировать драйвер для " + deviceId);
            }
        }

        public void unregisterDriver(String deviceId) {
            DeviceDriver driver = drivers.get(deviceId);
            if (driver != null) {
                driver.shutdown();
                drivers.remove(deviceId);
                System.out.println("Драйвер для устройства " + deviceId + " отключен");
            }
        }

        public Object readFromDevice(String deviceId) {
            DeviceDriver driver = drivers.get(deviceId);
            if (driver != null && driver.isReady()) {
                return driver.read();
            }
            System.out.println("Устройство " + deviceId + " недоступно");
            return null;
        }

        public void writeToDevice(String deviceId, Object data) {
            DeviceDriver driver = drivers.get(deviceId);
            if (driver != null && driver.isReady()) {
                driver.write(data);
            } else {
                System.out.println("Устройство " + deviceId + " недоступно");
            }
        }

        public void listDevices() {
            System.out.println("Зарегистрированные устройства:");
            for (Map.Entry<String, DeviceDriver> entry : drivers.entrySet()) {
                String status = entry.getValue().isReady() ? "готово" : "недоступно";
                System.out.println("  " + entry.getKey() + " (" + entry.getValue().getDeviceType() + "): " + status);
            }
        }
    }

    /**
     * Демонстрация работы с устройствами
     */
    public static void demonstrateDevices() {
        System.out.println("=== Управление устройствами ===");

        DeviceManager deviceManager = new DeviceManager();

        // Регистрация устройств
        HardDiskDriver hdd = new HardDiskDriver();
        NetworkCardDriver nic = new NetworkCardDriver("192.168.1.100");

        deviceManager.registerDriver("hdd0", hdd);
        deviceManager.registerDriver("eth0", nic);

        deviceManager.listDevices();

        // Работа с жестким диском
        System.out.println("\n--- Работа с жестким диском ---");
        byte[] diskData = {1, 2, 3, 4, 5};
        deviceManager.writeToDevice("hdd0", diskData);
        Object readData = deviceManager.readFromDevice("hdd0");

        // Работа с сетевой картой
        System.out.println("\n--- Работа с сетевой картой ---");
        String httpRequest = "GET / HTTP/1.1\r\nHost: example.com\r\n\r\n";
        deviceManager.writeToDevice("eth0", httpRequest.getBytes());
        Object response = deviceManager.readFromDevice("eth0");

        // Отключение устройств
        deviceManager.unregisterDriver("hdd0");
        deviceManager.unregisterDriver("eth0");

        deviceManager.listDevices();
    }
}
```

## Синхронизация и параллелизм

ОС обеспечивает координацию между параллельными процессами.

### Проблемы синхронизации

```java
/**
 * Классические проблемы синхронизации
 */
public class SynchronizationProblems {

    /**
     * Проблема "Производитель-Потребитель"
     */
    public static class ProducerConsumerProblem {
        private Queue<Integer> buffer;
        private int capacity;
        private final Object lock = new Object();

        public ProducerConsumerProblem(int capacity) {
            this.capacity = capacity;
            this.buffer = new LinkedList<>();
        }

        public void produce(int item) throws InterruptedException {
            synchronized (lock) {
                while (buffer.size() == capacity) {
                    System.out.println("Буфер полон, производитель ждет...");
                    lock.wait();
                }

                buffer.add(item);
                System.out.println("Произведен: " + item + ", размер буфера: " + buffer.size());
                lock.notifyAll();
            }
        }

        public int consume() throws InterruptedException {
            synchronized (lock) {
                while (buffer.isEmpty()) {
                    System.out.println("Буфер пуст, потребитель ждет...");
                    lock.wait();
                }

                int item = buffer.poll();
                System.out.println("Потреблен: " + item + ", размер буфера: " + buffer.size());
                lock.notifyAll();

                return item;
            }
        }
    }

    /**
     * Проблема "Обед философов"
     */
    public static class DiningPhilosophersProblem {
        private static final int NUM_PHILOSOPHERS = 5;
        private Object[] forks = new Object[NUM_PHILOSOPHERS];

        public DiningPhilosophersProblem() {
            for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                forks[i] = new Object();
            }
        }

        public void philosopher(int id) throws InterruptedException {
            int leftFork = id;
            int rightFork = (id + 1) % NUM_PHILOSOPHERS;

            while (true) {
                // Размышление
                System.out.println("Философ " + id + " размышляет");
                Thread.sleep((long) (Math.random() * 1000));

                // Попытка взять вилки
                synchronized (forks[Math.min(leftFork, rightFork)]) {
                    synchronized (forks[Math.max(leftFork, rightFork)]) {
                        // Еда
                        System.out.println("Философ " + id + " ест");
                        Thread.sleep((long) (Math.random() * 1000));
                    }
                }
            }
        }
    }

    /**
     * Проблема "Читатели-Писатели"
     */
    public static class ReadersWritersProblem {
        private int readers = 0;
        private boolean writing = false;
        private final Object lock = new Object();

        public void read(int readerId) throws InterruptedException {
            synchronized (lock) {
                while (writing) {
                    System.out.println("Читатель " + readerId + " ждет, пока писатель закончит...");
                    lock.wait();
                }
                readers++;
                System.out.println("Читатель " + readerId + " начал чтение, читателей: " + readers);
            }

            // Имитация чтения
            Thread.sleep((long) (Math.random() * 1000));

            synchronized (lock) {
                readers--;
                System.out.println("Читатель " + readerId + " закончил чтение, читателей: " + readers);
                if (readers == 0) {
                    lock.notifyAll();  // Уведомить ожидающих писателей
                }
            }
        }

        public void write(int writerId) throws InterruptedException {
            synchronized (lock) {
                while (writing || readers > 0) {
                    System.out.println("Писатель " + writerId + " ждет...");
                    lock.wait();
                }
                writing = true;
                System.out.println("Писатель " + writerId + " начал запись");
            }

            // Имитация записи
            Thread.sleep((long) (Math.random() * 1000));

            synchronized (lock) {
                writing = false;
                System.out.println("Писатель " + writerId + " закончил запись");
                lock.notifyAll();  // Уведомить всех ожидающих
            }
        }
    }

    /**
     * Демонстрация проблем синхронизации
     */
    public static void demonstrateSynchronization() {
        System.out.println("=== Проблемы синхронизации ===");

        // Producer-Consumer
        System.out.println("\n--- Producer-Consumer ---");
        ProducerConsumerProblem pc = new ProducerConsumerProblem(3);

        // Запуск потоков производителей и потребителей
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    pc.produce(i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    pc.consume();
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Readers-Writers
        System.out.println("\n--- Readers-Writers ---");
        ReadersWritersProblem rw = new ReadersWritersProblem();

        Thread writer = new Thread(() -> {
            try {
                rw.write(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread reader1 = new Thread(() -> {
            try {
                rw.read(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread reader2 = new Thread(() -> {
            try {
                rw.read(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        writer.start();
        reader1.start();
        reader2.start();

        try {
            writer.join();
            reader1.join();
            reader2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

## Безопасность операционной системы

ОС обеспечивает защиту ресурсов и данных.

### Аутентификация и авторизация

```java
/**
 * Система аутентификации и авторизации
 */
public class OSAuthentication {

    /**
     * Пользователь системы
     */
    public static class User {
        private String username;
        private String passwordHash;
        private Set<String> groups;
        private Map<String, Set<String>> permissions;  // ресурс -> разрешения

        public User(String username, String password) {
            this.username = username;
            this.passwordHash = hashPassword(password);
            this.groups = new HashSet<>();
            this.permissions = new HashMap<>();
        }

        private String hashPassword(String password) {
            // Упрощенный хеш (в реальности использовать bcrypt/scrypt)
            return String.valueOf(password.hashCode());
        }

        public boolean authenticate(String password) {
            return hashPassword(password).equals(passwordHash);
        }

        public void addToGroup(String group) {
            groups.add(group);
        }

        public void grantPermission(String resource, String permission) {
            permissions.computeIfAbsent(resource, k -> new HashSet<>()).add(permission);
        }

        public boolean hasPermission(String resource, String permission) {
            Set<String> resourcePermissions = permissions.get(resource);
            return resourcePermissions != null && resourcePermissions.contains(permission);
        }

        public boolean isInGroup(String group) {
            return groups.contains(group);
        }
    }

    /**
     * Менеджер безопасности
     */
    public static class SecurityManager {
        private Map<String, User> users;
        private User currentUser;

        public SecurityManager() {
            this.users = new HashMap<>();
            initializeDefaultUsers();
        }

        private void initializeDefaultUsers() {
            User admin = new User("admin", "admin123");
            admin.addToGroup("admin");
            admin.grantPermission("file", "read");
            admin.grantPermission("file", "write");
            admin.grantPermission("process", "kill");
            users.put("admin", admin);

            User user = new User("user", "user123");
            user.addToGroup("users");
            user.grantPermission("file", "read");
            users.put("user", user);
        }

        public boolean login(String username, String password) {
            User user = users.get(username);
            if (user != null && user.authenticate(password)) {
                currentUser = user;
                System.out.println("Пользователь " + username + " вошел в систему");
                return true;
            }
            System.out.println("Ошибка аутентификации для пользователя " + username);
            return false;
        }

        public void logout() {
            if (currentUser != null) {
                System.out.println("Пользователь " + currentUser.username + " вышел из системы");
                currentUser = null;
            }
        }

        public boolean checkPermission(String resource, String permission) {
            if (currentUser == null) {
                System.out.println("Доступ запрещен: пользователь не аутентифицирован");
                return false;
            }

            if (currentUser.hasPermission(resource, permission)) {
                System.out.println("Доступ разрешен: " + resource + ":" + permission);
                return true;
            }

            System.out.println("Доступ запрещен: недостаточно прав для " + resource + ":" + permission);
            return false;
        }

        public User getCurrentUser() {
            return currentUser;
        }

        public void createUser(String username, String password) {
            if (currentUser != null && currentUser.isInGroup("admin")) {
                User newUser = new User(username, password);
                users.put(username, newUser);
                System.out.println("Создан новый пользователь: " + username);
            } else {
                System.out.println("Недостаточно прав для создания пользователя");
            }
        }
    }

    /**
     * Демонстрация аутентификации
     */
    public static void demonstrateAuthentication() {
        System.out.println("=== Аутентификация и авторизация ===");

        SecurityManager security = new SecurityManager();

        // Попытка доступа без аутентификации
        security.checkPermission("file", "read");

        // Аутентификация обычного пользователя
        security.login("user", "user123");
        security.checkPermission("file", "read");
        security.checkPermission("file", "write");
        security.checkPermission("process", "kill");

        security.logout();

        // Аутентификация администратора
        security.login("admin", "admin123");
        security.checkPermission("file", "write");
        security.checkPermission("process", "kill");

        // Создание нового пользователя
        security.createUser("newuser", "password123");

        // Вход под новым пользователем
        security.logout();
        security.login("newuser", "password123");
        security.checkPermission("file", "read");

        security.logout();
    }
}
```


## Решение проблем

- **Deadlock** — проверить порядок захвата блокировок (единый порядок по всем потокам); использовать таймауты и tryLock; см. раздел «Синхронизация».
- **Высокая загрузка CPU / много контекстных переключений** — профилировать (top, perf); проверить размер пула потоков и блокирующие вызовы.
- **OOM в Java** — проверить heap, Metaspace, утечки; jmap, jstack; см. раздел «Управление памятью» и подраздел по Java.

## Частые вопросы

- **Чем процесс отличается от потока?** Процесс — изолированный экземпляр программы с собственным адресным пространством; поток — единица выполнения внутри процесса, общая память.
- **Что такое виртуальная память?** Абстракция: каждый процесс видит свой адресный диапазон; страницы отображаются на физическую память или swap; см. раздел «Управление памятью».
- **См. также:** разделы «Процессы и потоки», «Синхронизация» и «Лучшие практики» в документе.


## Заключение

Операционные системы — это сложные программные системы, управляющие аппаратными ресурсами компьютера и предоставляющие интерфейс для пользовательских приложений. Они включают в себя множество компонентов: управление процессами, памятью, файлами, устройствами, безопасность.

### Ключевые концепции

1. **Архитектура ОС**: Монолитные, микроядерные и гибридные архитектуры
2. **Управление процессами**: Создание, планирование, синхронизация процессов
3. **Управление памятью**: Выделение, виртуальная память, защита памяти
4. **Файловые системы**: Организация хранения и доступа к данным
5. **Ввод-вывод**: Управление аппаратными устройствами через драйверы
6. **Синхронизация**: Решение проблем параллельного выполнения
7. **Безопасность**: Аутентификация, авторизация, защита ресурсов

### Практические рекомендации

- **Изучайте исходный код**: **Linux**, **FreeBSD**, **Windows** для понимания реализации
- **Экспериментируйте**: Создавайте собственные мини-ОС или модифицируйте существующие
- **Читайте документацию**: Официальные руководства по конкретным ОС
- **Практикуйте администрирование**: Работа с серверами, настройка систем
- **Изучайте безопасность**: Понимание уязвимостей и методов защиты

### Важность изучения ОС

- **Фундаментальные знания**: Понимание работы компьютеров на низком уровне
- **Системное мышление**: Способность анализировать сложные системы
- **Разработка ПО**: Лучшее понимание ограничений и возможностей платформы
- **Безопасность**: Знание принципов защиты систем
- **Оптимизация**: Способность улучшать производительность приложений

Операционные системы — это одна из самых важных областей **computer science**, которая влияет на все аспекты разработки программного обеспечения!
