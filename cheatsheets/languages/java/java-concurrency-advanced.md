---
title: "Java Concurrency: Advanced"
description: "Материал по теме Java Concurrency: Advanced в разделе cheatsheets."
tags:
  - languages
  - java
  - java-concurrency-advanced
difficulty: "intermediate"
prerequisites: []
next:
  - java-memory-model
updated: "2026-04-20"
---
# Java Concurrency: Advanced

## Полезные ссылки

### Официальная документация

- [Oracle Java Documentation](https://docs.oracle.com/en/java/)
- [Java API Documentation](https://docs.oracle.com/en/java/javase/17/docs/api/)

### Обучающие материалы

- [Java Tutorials](https://docs.oracle.com/javase/tutorial/)


## Содержание

- [Демон поток](#демон-поток)
- [Руководство по ExecutorService](#руководство-по-executorservice)
  - [ScheduledExecutorService](#scheduledexecutorservice)
  - [Распространенные ошибки](#распространенные-ошибки)
- [Руководство по Fork/Join](#руководство-по-forkjoin)
  - [Рекомендации](#рекомендации)
- [Пользовательские пулы потоков](#пользовательские-пулы-потоков)
- [Руководство по CountDownLatch](#руководство-по-countdownlatch)
- [Руководство по java.util.concurrent.Locks](#руководство-по-javautilconcurrentlocks)
  - [ReentrantLock](#reentrantlock)
  - [ReadWriteLock](#readwritelock)
  - [StampedLock](#stampedlock)
  - [Condition](#condition)
- [Ожидание завершения потоков в ExecutorService](#ожидание-завершения-потоков-в-executorservice)
- [Руководство по CompletableFuture](#руководство-по-completablefuture)
  - [Изменения в Java 9](#изменения-в-java-9)
- [Руководство по CyclicBarrier](#руководство-по-cyclicbarrier)
- [Руководство по ThreadLocalRandom](#руководство-по-threadlocalrandom)
  - [Внутренняя реализация](#внутренняя-реализация)
- [CyclicBarrier против CountDownLatch](#cyclicbarrier-против-countdownlatch)
  - [Основные различия](#основные-различия)
- [Что такое потокобезопасность и как ее достичь?](#что-такое-потокобезопасность-и-как-ее-достичь)
- [Как отложить выполнение кода?](#как-отложить-выполнение-кода)
- [Как остановить выполнение через определенное время?](#как-остановить-выполнение-через-определенное-время)
  - [Важные замечания](#важные-замечания)
  - [Дизайн с учетом прерывания](#дизайн-с-учетом-прерывания)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также)

## Демон поток

В этой короткой статье мы рассмотрим потоки демона в **Java** и посмотрим, для чего их можно использовать. Мы также объясним разницу между потоками демона и пользовательскими потоками.

**Java** предлагает два типа потоков: пользовательские потоки и потоки демона.

**Пользовательские потоки** — это потоки с высоким приоритетом. **JVM** будет ждать, пока любой пользовательский поток завершит свою задачу, прежде чем завершить ее.

С другой стороны, **потоки демона** — это потоки с низким приоритетом, единственная роль которых состоит в предоставлении услуг пользовательским потокам.

Поскольку потоки демона предназначены для обслуживания пользовательских потоков и необходимы только во время выполнения пользовательских потоков, они не будут препятствовать выходу **JVM** после завершения выполнения всех пользовательских потоков.

Вот почему бесконечные циклы, которые обычно существуют в потоках демона, не вызовут проблем, потому что любой код, включая блоки **finally**, не будет выполняться после того, как все пользовательские потоки закончат свое выполнение. По этой причине потоки демона не рекомендуются для задач ввода-вывода.

Однако из этого правила есть исключения. Плохо спроектированный код в потоках демона может помешать выходу **JVM**. Например, вызов **Thread.join()** в работающем потоке демона может заблокировать завершение работы приложения.

Потоки демона полезны для фоновых вспомогательных задач, таких как сборка мусора, освобождение памяти неиспользуемых объектов и удаление нежелательных записей из кэша. Большинство потоков **JVM** являются потоками демона.

**Чтобы установить поток как поток демона, все, что нам нужно сделать, это вызвать **Thread.`setDaemon()`**. В этом примере мы будем использовать класс **NewThread**, который расширяет класс **Thread**:**

```java
NewThread daemonThread = new NewThread();
daemonThread.setDaemon(true);
daemonThread.start();
```

Любой поток наследует статус демона создавшего его потока. Поскольку основной поток является пользовательским потоком, любой поток, созданный внутри основного метода, по умолчанию является пользовательским потоком.

**Метод **setDaemon()** может быть вызван только после того, как объект **Thread** создан, а поток еще не запущен. Попытка вызвать **setDaemon()** во время выполнения потока вызовет исключение **IllegalThreadStateException**:**

```java
@Test(expected = IllegalThreadStateException.class)
public void whenSetDaemonWhileRunning_thenIllegalThreadStateException() {
    NewThread daemonThread = new NewThread();
    daemonThread.start();
    daemonThread.setDaemon(true);
}
```

**Наконец, чтобы проверить, является ли поток потоком демона, мы можем просто вызвать метод **isDaemon()**:**

```java
@Test
public void whenCallIsDaemon_thenCorrect() {
    NewThread daemonThread = new NewThread();
    NewThread userThread = new NewThread();

    daemonThread.setDaemon(true);
    daemonThread.start();
    userThread.start();

    assertTrue(daemonThread.isDaemon());
    assertFalse(userThread.isDaemon());
}
```

## Руководство по ExecutorService

**ExecutorService** — это **API JDK**, упрощающий выполнение задач в асинхронном режиме. Вообще говоря, **ExecutorService** автоматически предоставляет пул потоков и **API** для назначения ему задач.

Самый простой способ создать **ExecutorService** — использовать один из фабричных методов класса **Executors**.

**Например, следующая строка кода создаст пул из **10** потоков:**

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
```

Существует несколько других фабричных методов для создания предопределенной службы **ExecutorService**, соответствующей конкретным случаям использования. Чтобы найти лучший метод для ваших нужд, обратитесь к официальной документации **Oracle**.

**Поскольку ExecutorService** — это интерфейс, можно использовать экземпляр любой его реализации. В пакете **java.util.concurrent** есть несколько реализаций на выбор, или вы можете создать свою собственную.

**Например, у класса **ThreadPoolExecutor** есть несколько конструкторов, которые мы можем использовать для настройки службы-исполнителя и ее внутреннего пула:**

```java
ExecutorService executorService = new ThreadPoolExecutor(
    1, 1, 0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<Runnable>()
);
```

Вы можете заметить, что приведенный выше код очень похож на исходный код фабричного метода **newSingleThreadExecutor()**. В большинстве случаев детальная ручная настройка не требуется.

**ExecutorService** может выполнять задачи **Runnable** и **Callable**. Для простоты в этой статье будут использоваться две примитивные задачи. Обратите внимание, что здесь мы используем лямбда-выражения вместо анонимных внутренних классов:**

```java
Runnable runnableTask = () -> {
    try {
        TimeUnit.MILLISECONDS.sleep(300);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
};

Callable<String> callableTask = () -> {
    TimeUnit.MILLISECONDS.sleep(300);
    return "Task's execution";
};

List<Callable<String>> callableTasks = new ArrayList<>();
callableTasks.add(callableTask);
callableTasks.add(callableTask);
callableTasks.add(callableTask);
```

Мы можем назначать задачи **ExecutorService**, используя несколько методов, включая **execute()**, который унаследован от интерфейса **Executor**, а также **submit()**, **invokeAny()** и **invokeAll()**.

**Метод **execute()** недействителен и не дает никакой возможности получить результат выполнения задачи или проверить статус задачи (выполняется ли она):**

```java
executorService.execute(runnableTask);
```

**submit()** отправляет задачу **Callable** или **Runnable** в **ExecutorService** и возвращает результат типа **Future**:**

```java
Future<String> future = executorService.submit(callableTask);
```

**invokeAny()** присваивает набор задач **ExecutorService**, вызывая выполнение каждой из них, и возвращает результат успешного выполнения одной задачи (если было успешное выполнение):**

```java
String result = executorService.invokeAny(callableTasks);
```

**invokeAll()** присваивает **ExecutorService** набор задач, вызывая выполнение каждой из них, и возвращает результат выполнения всех задач в виде списка объектов типа **Future**:**

```java
List<Future<String>> futures = executorService.invokeAll(callableTasks);
```

Прежде чем идти дальше, нам нужно обсудить еще два вопроса: закрытие **ExecutorService** и работа с типами возврата **Future**.

Как правило, **ExecutorService** не будет автоматически уничтожен, если нет задачи для обработки. Он останется в живых и будет ждать новой работы.

В некоторых случаях это очень полезно, например, когда приложению необходимо обрабатывать задачи, которые появляются нерегулярно, или количество задач неизвестно во время компиляции.

С другой стороны, приложение может достичь своего завершения, но не быть остановленным, потому что ожидающий **ExecutorService** заставит **JVM** продолжать работу.

Чтобы правильно закрыть **ExecutorService**, у нас есть **API**-интерфейсы **shutdown()** и **shutdownNow()**.

**Метод **shutdown()** не вызывает немедленного уничтожения **ExecutorService**. Это заставит **ExecutorService** прекратить принимать новые задачи и закрыться после того, как все запущенные потоки закончат свою текущую работу:**

```java
executorService.shutdown();
```

**Метод **shutdownNow()** пытается немедленно уничтожить **ExecutorService**, но не гарантирует, что все запущенные потоки будут остановлены одновременно:**

```java
List<Runnable> notExecutedTasks = executorService.shutdownNow();
```

Этот метод возвращает список задач, ожидающих обработки. Разработчик сам решает, что делать с этими задачами.

**Один хороший способ закрыть **ExecutorService** (который также рекомендуется Oracle) — использовать оба этих метода в сочетании с методом **awaitTermination()**:**

```java
executorService.shutdown();
try {
    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
        executorService.shutdownNow();
    }
} catch (InterruptedException e) {
    executorService.shutdownNow();
}
```

При таком подходе **ExecutorService** сначала перестанет принимать новые задачи, а затем будет ждать до указанного периода времени, пока все задачи будут выполнены. Если это время истекает, выполнение немедленно останавливается.

Методы **submit()** и **invokeAll()** возвращают объект или набор объектов типа **Future**, что позволяет нам получить результат выполнения задачи или проверить статус задачи (выполняется ли она).

**Интерфейс **Future** предоставляет специальный блокирующий метод **get()**, который возвращает фактический результат выполнения задачи **Callable** или **null** в случае задачи **Runnable**:**

```java
Future<String> future = executorService.submit(callableTask);
String result = null;

try {
    result = future.get();
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
```

Вызов метода **get()** во время выполнения задачи приведет к блокировке выполнения до тех пор, пока задача не выполнится должным образом и результат не будет доступен.

**При очень длительной блокировке, вызванной методом **get()**, производительность приложения может ухудшиться. Если полученные данные не критичны, можно избежать такой проблемы, используя таймауты:**

```java
String result = future.get(200, TimeUnit.MILLISECONDS);
```

Если период выполнения больше указанного (в данном случае `200` миллисекунд), будет выдано исключение **TimeoutException**.

Мы можем использовать метод **isDone()**, чтобы проверить, обработана ли уже назначенная задача или нет.

**Интерфейс **Future** также предусматривает отмену выполнения задачи с помощью метода **cancel()** и проверку отмены с помощью метода **isCancelled()**:**

```java
boolean canceled = future.cancel(true);
boolean isCancelled = future.isCancelled();
```

### ScheduledExecutorService

**ScheduledExecutorService** запускает задачи после некоторой предопределенной задержки и/или периодически.

Опять же, лучший способ создать экземпляр **ScheduledExecutorService** — использовать фабричные методы класса **Executors**.

**Для этого раздела мы используем **ScheduledExecutorService** с одним потоком:**

```java
ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
```

Чтобы запланировать выполнение отдельной задачи после фиксированной задержки, используйте метод **schedule()** службы **ScheduledExecutorService**.

**Два метода **schedule()** позволяют выполнять задачи **Runnable** или **Callable**:**

```java
Future<String> resultFuture = executorService.schedule(callableTask, 1, TimeUnit.SECONDS);
```

Метод **scheduleAtFixedRate()** позволяет нам периодически запускать задачу после фиксированной задержки. Приведенный выше код делает задержку на одну секунду перед выполнением **callableTask**.

**Следующий блок кода запустит задачу после первоначальной задержки в **100** миллисекунд. И после этого он будет запускать одну и ту же задачу каждые **450** миллисекунд:**

```java
Future<String> resultFuture = service.scheduleAtFixedRate(
    runnableTask, 100, 450, TimeUnit.MILLISECONDS
);
```

Если процессору требуется больше времени для выполнения назначенной задачи, чем параметр **period** метода **scheduleAtFixedRate()**, **ScheduledExecutorService** будет ждать завершения текущей задачи перед запуском следующей.

Если необходимо иметь фиксированную задержку между итерациями задачи, следует использовать **scheduleWithFixedDelay()**.

**Например, следующий код гарантирует **150-**миллисекундную паузу между окончанием текущего выполнения и началом другого:**

```java
service.scheduleWithFixedDelay(task, 100, 150, TimeUnit.MILLISECONDS);
```

В соответствии с контрактами методов **scheduleAtFixedRate()** и **scheduleWithFixedDelay()** период выполнения задачи завершится при завершении **ExecutorService** или при возникновении исключения во время выполнения задачи.

### Распространенные ошибки

После выпуска **Java 7** многие разработчики решили заменить инфраструктуру **ExecutorService** на инфраструктуру **fork/join**.

Однако это не всегда правильное решение. Несмотря на простоту и частый прирост производительности, связанный с **fork/join**, он снижает контроль разработчика над параллельным выполнением.

**ExecutorService** дает разработчику возможность контролировать количество генерируемых потоков и степень детализации задач, которые должны выполняться отдельными потоками. Наилучший вариант использования **ExecutorService** — обработка независимых задач, таких как транзакции или запросы по схеме «один поток на одну задачу».

Напротив, согласно документации **Oracle**, **fork/join** был разработан для ускорения работы, которую можно рекурсивно разбить на более мелкие части.

**Несмотря на относительную простоту **ExecutorService**, есть несколько распространенных ошибок:**

1. **Сохранение неиспользуемого `ExecutorService` в рабочем состоянии**: См. подробное объяснение выше о том, как завершить работу **ExecutorService**.
2. **Неправильная емкость пула потоков при использовании пула потоков фиксированной длины**: Очень важно определить, сколько потоков потребуется приложению для эффективного выполнения задач. Слишком большой пул потоков вызовет ненужные накладные расходы только на создание потоков, которые в основном будут находиться в режиме ожидания. Слишком малое количество может привести к тому, что приложение не будет отвечать из-за длительных периодов ожидания задач в очереди.
3. **Вызов метода get() класса `Future` после отмены задачи**: попытка получить результат уже отмененной задачи вызывает исключение **CancellationException**.
4. **Неожиданно долгая блокировка с помощью метода get() класса Future**: мы должны использовать тайм-ауты, чтобы избежать непредвиденных ожиданий.

## Руководство по Fork/Join

**Java 7** представила структуру **fork/join**. Он предоставляет инструменты, помогающие ускорить параллельную обработку, пытаясь использовать все доступные процессорные ядра. Это достигается за счет подхода «разделяй и властвуй».

На практике это означает, что платформа сначала «разветвляется», рекурсивно разбивая задачу на более мелкие независимые подзадачи, пока они не станут достаточно простыми для асинхронного выполнения.

После этого начинается «присоединение». Результаты всех подзадач рекурсивно объединяются в один результат. В случае задачи, возвращающей **void**, программа просто ждет, пока не запустится каждая подзадача.

Чтобы обеспечить эффективное параллельное выполнение, инфраструктура **fork/join** использует пул потоков, который называется **ForkJoinPool**. Этот пул управляет рабочими потоками типа **ForkJoinWorkerThread**.

**ForkJoinPool** — это сердце фреймворка. Это реализация **ExecutorService**, которая управляет рабочими потоками и предоставляет нам инструменты для получения информации о состоянии и производительности пула потоков.

Рабочие потоки могут одновременно выполнять только одну задачу, но **ForkJoinPool** не создает отдельный поток для каждой отдельной подзадачи. Вместо этого у каждого потока в пуле есть собственная двойная очередь (или deque, произносится как «колода»), в которой хранятся задачи.

Эта архитектура жизненно важна для балансировки рабочей нагрузки потока с помощью алгоритма кражи работы.

Проще говоря, свободные потоки пытаются «украсть» работу из деков занятых потоков.

По умолчанию рабочий поток получает задачи от головы своей очереди. Когда она пуста, поток берет задачу из хвоста дека другого занятого потока или из глобальной очереди входа, так как именно там, вероятно, находится самая большая часть работы.

Такой подход сводит к минимуму вероятность того, что потоки будут конкурировать за задачи. Это также уменьшает количество раз, когда потоку придется искать работу, поскольку он сначала работает с самыми большими доступными блоками работы.

В **Java 8** самый удобный способ получить доступ к экземпляру **ForkJoinPool** — использовать его статический метод **commonPool()**. Это предоставит ссылку на общий пул, который является пулом потоков по умолчанию для каждого **ForkJoinTask**.

Согласно документации **Oracle**, использование предопределенного общего пула снижает потребление ресурсов, поскольку это препятствует созданию отдельного пула потоков для каждой задачи.

```java
ForkJoinPool commonPool = ForkJoinPool.commonPool();
```

**Мы можем добиться такого же поведения в **Java 7**, создав **ForkJoinPool** и назначив его общедоступному статическому полю служебного класса:**

```java
public static ForkJoinPool forkJoinPool = new ForkJoinPool(2);
```

**Теперь мы можем легко получить к нему доступ:**

```java
ForkJoinPool forkJoinPool = PoolUtil.forkJoinPool;
```

С помощью конструкторов **ForkJoinPool** мы можем создать собственный пул потоков с определенным уровнем параллелизма, фабрикой потоков и обработчиком исключений. Здесь пул имеет уровень параллелизма **2**. Это означает, что пул будет использовать два процессорных ядра.

**ForkJoinTask** — это базовый тип для задач, выполняемых внутри **ForkJoinPool**. На практике следует расширить один из двух его подклассов: **RecursiveAction** для пустых задач и **RecursiveTask<V>** для задач, возвращающих значение. У них обоих есть абстрактный метод **compute()**, в котором определяется логика задачи.

В приведенном ниже примере мы используем строку с именем **workload** для представления единицы работы, которую необходимо обработать. В демонстрационных целях эта задача не имеет смысла: она просто записывает введенные данные в верхний регистр и регистрирует их.

Чтобы продемонстрировать поведение ветвления платформы, пример разбивает задачу, если рабочая нагрузка **.length()** превышает указанный порог, используя метод **createSubtask()**.

Строка рекурсивно делится на подстроки, создавая экземпляры **CustomRecursiveAction** на основе этих подстрок.

В результате метод возвращает **List<`CustomRecursiveAction`>**.

**Список отправляется в **ForkJoinPool** с помощью метода **invokeAll()**:**

```java
public class CustomRecursiveAction extends RecursiveAction {
    private String workload = "";
    private static final int THRESHOLD = 4;
    private static Logger logger = Logger.getAnonymousLogger();

    public CustomRecursiveAction(String workload) {
        this.workload = workload;
    }

    @Override
    protected void compute() {
        if (workload.length() > THRESHOLD) {
            ForkJoinTask.invokeAll(createSubtasks());
        } else {
            processing(workload);
        }
    }

    private List<CustomRecursiveAction> createSubtasks() {
        List<CustomRecursiveAction> subtasks = new ArrayList<>();
        String partOne = workload.substring(0, workload.length()/2);
        String partTwo = workload.substring(workload.length()/2, workload.length());
        subtasks.add(new CustomRecursiveAction(partOne));
        subtasks.add(new CustomRecursiveAction(partTwo));
        return subtasks;
    }

    private void processing(String work) {
        String result = work.toUpperCase();
        logger.info("This result - (" + result + ") - was processed by " + Thread.currentThread().getName());
    }
}
```

Мы можем использовать этот шаблон для разработки наших собственных классов **RecursiveAction**. Для этого мы создаем объект, представляющий общий объем работы, выбираем подходящий порог, определяем метод разделения работы и определяем метод выполнения работы.

Для задач, возвращающих значение, логика аналогична.

**Отличие в том, что результат по каждой подзадаче объединяется в один результат:**

```java
public class CustomRecursiveTask extends RecursiveTask<Integer> {
    private int[] arr;
    private static final int THRESHOLD = 20;

    public CustomRecursiveTask(int[] arr) {
        this.arr = arr;
    }

    @Override
    protected Integer compute() {
        if (arr.length > THRESHOLD) {
            return ForkJoinTask.invokeAll(createSubtasks())
                .stream()
                .mapToInt(ForkJoinTask::join)
                .sum();
        } else {
            return processing(arr);
        }
    }

    private Collection<CustomRecursiveTask> createSubtasks() {
        List<CustomRecursiveTask> dividedTasks = new ArrayList<>();
        dividedTasks.add(new CustomRecursiveTask(
            Arrays.copyOfRange(arr, 0, arr.length/2)));
        dividedTasks.add(new CustomRecursiveTask(
            Arrays.copyOfRange(arr, arr.length/2, arr.length)));
        return dividedTasks;
    }

    private Integer processing(int[] arr) {
        return Arrays.stream(arr)
            .filter(a -> a > 10 && a < 27)
            .map(a -> a * 10)
            .sum();
    }
}
```

В этом примере мы используем массив, хранящийся в поле **arr** класса **CustomRecursiveTask**, для представления работы. Метод **createSubtasks()** рекурсивно делит задачу на более мелкие части работы, пока каждая часть не станет меньше порогового значения. Затем метод **invokeAll()** отправляет подзадачи в общий пул и возвращает список **Future**.

Чтобы инициировать выполнение, для каждой подзадачи вызывается метод **join()**.

Мы достигли этого здесь, используя **Stream `API Java` 8**. Мы используем метод **sum()** как представление объединения подрезультатов в окончательный результат.

Мы можем использовать несколько подходов для отправки задач в пул потоков.

**Начнем с метода **submit()** или **execute()** (варианты их использования одинаковы):**

```java
forkJoinPool.execute(customRecursiveTask);
int result = customRecursiveTask.join();
```

**Метод **invoke()** разветвляет задачу и ожидает результата и не требует ручного присоединения:**

```java
int result = forkJoinPool.invoke(customRecursiveTask);
```

Метод **invokeAll()** является наиболее удобным способом отправки последовательности **ForkJoinTasks** в **ForkJoinPool**. Он принимает задачи в качестве параметров (две задачи, var args или коллекцию), разветвляется, а затем возвращает коллекцию объектов **Future** в том порядке, в котором они были созданы.

В качестве альтернативы мы можем использовать отдельные методы **fork()** и **join()**. Метод **fork()** отправляет задачу в пул, но не запускает ее выполнение. Для этой цели мы должны использовать метод **join()**.

**В случае **RecursiveAction** метод **join()** не возвращает ничего, кроме **null**; для **RecursiveTask<V>** возвращает результат выполнения задачи:**

```java
customRecursiveTaskFirst.fork();
result = customRecursiveTaskLast.join();
```

Здесь мы использовали метод **invokeAll()** для отправки последовательности подзадач в пул. Мы можем сделать ту же работу с **fork()** и **join()**, хотя это имеет последствия для упорядочения результатов.

Чтобы избежать путаницы, рекомендуется использовать метод **invokeAll()** для отправки более одной задачи в **ForkJoinPool**.

### Рекомендации

**Использование фреймворка **fork**/**join** может ускорить обработку больших задач, но для достижения этого результата мы должны следовать некоторым рекомендациям:**

1. **Используйте как можно меньше пулов потоков**: В большинстве случаев лучшим решением является использование одного пула потоков для каждого приложения или системы.
2. **Используйте общий пул потоков по умолчанию**: если не требуется специальная настройка.
3. **Используйте разумный порог для разделения `ForkJoinTask` на подзадачи**.
4. **Избегайте любых блокировок в ForkJoinTasks**.

## Пользовательские пулы потоков

В **Java 8** появилась концепция потоков **Stream** как эффективного способа выполнения массовых операций над данными. А параллельные потоки можно получить в средах, поддерживающих параллелизм.

Эти потоки могут иметь повышенную производительность за счет накладных расходов на многопоточность.

В этом кратком руководстве мы рассмотрим одно из самых больших ограничений **Stream API** и посмотрим, как заставить параллельный поток работать с пользовательским экземпляром **ThreadPool**, в качестве альтернативы — есть библиотека, которая обрабатывает это.

**Давайте начнем с простого примера — вызова метода **parallelStream** для любого из типов **Collection** — который вернет, возможно, параллельный **Stream**:**

```java
@Test
public void givenList_whenCallingParallelStream_shouldBeParallelStream() {
    List<Long> aList = new ArrayList<>();
    Stream<Long> parallelStream = aList.parallelStream();
    assertTrue(parallelStream.isParallel());
}
```

Обработка по умолчанию, которая происходит в таком потоке, использует **ForkJoinPool.`commonPool()`**, пул потоков, совместно используемый всем приложением.

На самом деле мы можем передать пользовательский **ThreadPool** при обработке потока.

**В следующем примере параллельный поток использует пользовательский **ThreadPool** для вычисления суммы длинных значений от **1** до **1 `000` 000** включительно:**

```java
@Test
public void giveRangeOfLongs_whenSummedInParallel_shouldBeEqualToExpectedTotal() throws InterruptedException, ExecutionException {
    long firstNum = 1;
    long lastNum = 1_000_000;
    List<Long> aList = LongStream.rangeClosed(firstNum, lastNum).boxed().collect(Collectors.toList());

    ForkJoinPool customThreadPool = new ForkJoinPool(4);
    long actualTotal = customThreadPool.submit(() -> aList.parallelStream().reduce(0L, Long::sum)).get();

    assertEquals((lastNum + firstNum) * lastNum/2, actualTotal);
}
```

Мы использовали конструктор **ForkJoinPool** с уровнем параллелизма **4**. Чтобы определить оптимальное значение для различных сред, требуется провести некоторые эксперименты, но хорошее эмпирическое правило заключается в простом выборе числа на основе количества ядер вашего процессора.

Далее мы обрабатывали содержимое параллельного **Stream**, суммируя их в вызове **reduce**.

Этот простой пример может не демонстрировать всю полезность использования пользовательского пула потоков, но преимущества становятся очевидными в ситуациях, когда мы не хотим связывать общий пул потоков с длительными задачами, такими как обработка данных из сетевого источника — или общий пул потоков используется другими компонентами приложения.

Если мы запустим тестовый метод, описанный выше, он пройдет. Все идет нормально.

Однако если мы создадим экземпляр класса **ForkJoinPool** в обычном методе так же, как в тестовом методе, это может привести к ошибке **OutOfMemoryError**.

Далее давайте более подробно рассмотрим причину утечки памяти.

Как мы говорили ранее, общий пул потоков по умолчанию используется всем приложением. Общий пул потоков — это статический экземпляр **ThreadPool**.

Поэтому утечки памяти не происходит, если мы используем пул потоков по умолчанию.

Теперь давайте рассмотрим наш метод тестирования. В тестовом методе мы создали объект **ForkJoinPool**. Когда тестовый метод завершится, объект **customThreadPool** не будет разыменовываться и собирать мусор — вместо этого он будет ожидать назначения новых задач.

То есть каждый раз, когда мы вызываем тестовый метод, будет создаваться новый объект **customThreadPool**, который не будет выпущен.

Решение проблемы довольно простое: выключите объект **customThreadPool** после того, как мы выполнили метод:

```java
try {
    long actualTotal = customThreadPool.submit(() -> aList.parallelStream().reduce(0L, Long::sum)).get();
    assertEquals((lastNum + firstNum) * lastNum/2, actualTotal);
} finally {
    customThreadPool.shutdown();
}
```

## Руководство по CountDownLatch

В этой статье мы дадим руководство по классу **CountDownLatch** и продемонстрируем, как его можно использовать, на нескольких практических примерах.

По сути, используя **CountDownLatch**, мы можем заставить поток блокироваться до тех пор, пока другие потоки не завершат данную задачу.

Проще говоря, **CountDownLatch** имеет поле счетчика, которое вы можете уменьшать по мере необходимости. Затем мы можем использовать его, чтобы заблокировать вызывающий поток, пока он не обнулится.

Если бы мы выполняли некоторую параллельную обработку, мы могли бы создать экземпляр **CountDownLatch** с тем же значением счетчика, что и количество потоков, с которыми мы хотим работать. Затем мы могли бы просто вызывать **countdown()** после завершения каждого потока, гарантируя, что зависимый поток, вызывающий **await()**, будет заблокирован до тех пор, пока не закончатся рабочие потоки.

**Давайте попробуем этот шаблон, создав **Worker** и используя поле **CountDownLatch**, чтобы сигнализировать о его завершении:**

```java
public class Worker implements Runnable {
    private List<String> outputScraper;
    private CountDownLatch countDownLatch;

    public Worker(List<String> outputScraper, CountDownLatch countDownLatch) {
        this.outputScraper = outputScraper;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        doSomeWork();
        outputScraper.add("Counted down");
        countDownLatch.countDown();
    }
}
```

**Затем давайте создадим тест, чтобы доказать, что мы можем заставить **CountDownLatch** ожидать завершения экземпляров **Worker**:**

```java
@Test
public void whenParallelProcessing_thenMainThreadWillBlockUntilCompletion() throws InterruptedException {
    List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch countDownLatch = new CountDownLatch(5);
    List<Thread> workers = Stream
        .generate(() -> new Thread(new Worker(outputScraper, countDownLatch)))
        .limit(5)
        .collect(toList());

    workers.forEach(Thread::start);
    countDownLatch.await();
    outputScraper.add("Latch released");

    assertThat(outputScraper).containsExactly(
        "Counted down",
        "Counted down",
        "Counted down",
        "Counted down",
        "Counted down",
        "Latch released"
    );
}
```

Естественно, "**Latch released**" всегда будет последним выводом, так как это зависит от освобождения **CountDownLatch**.

Обратите внимание, что если бы мы не вызвали **await()**, мы не смогли бы гарантировать порядок выполнения потоков, поэтому тест случайным образом провалился бы.

Если мы возьмем предыдущий пример, но на этот раз запустим тысячи потоков вместо пяти, вполне вероятно, что многие из более ранних потоков закончат обработку еще до того, как мы вызовем **start()** для более поздних. Это может затруднить попытку воспроизвести проблему параллелизма, поскольку мы не сможем заставить все наши потоки работать параллельно.

Чтобы обойти это, давайте заставим **CountdownLatch** работать иначе, чем в предыдущем примере. Вместо того, чтобы блокировать родительский поток до завершения некоторых дочерних потоков, мы можем заблокировать каждый дочерний поток до тех пор, пока не запустятся все остальные.

**Давайте изменим наш метод **run()**, чтобы он блокировался перед обработкой:**

```java
public class WaitingWorker implements Runnable {
    private List<String> outputScraper;
    private CountDownLatch readyThreadCounter;
    private CountDownLatch callingThreadBlocker;
    private CountDownLatch completedThreadCounter;

    public WaitingWorker(List<String> outputScraper,
                        CountDownLatch readyThreadCounter,
                        CountDownLatch callingThreadBlocker,
                        CountDownLatch completedThreadCounter) {
        this.outputScraper = outputScraper;
        this.readyThreadCounter = readyThreadCounter;
        this.callingThreadBlocker = callingThreadBlocker;
        this.completedThreadCounter = completedThreadCounter;
    }

    @Override
    public void run() {
        readyThreadCounter.countDown();
        try {
            callingThreadBlocker.await();
            doSomeWork();
            outputScraper.add("Counted down");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            completedThreadCounter.countDown();
        }
    }
}
```

**Теперь давайте изменим наш тест, чтобы он блокировался до тех пор, пока не запустятся все рабочие процессы, разблокировал рабочие процессы, а затем блокировался, пока рабочие не завершили работу:**

```java
@Test
public void whenDoingLotsOfThreadsInParallel_thenStartThemAtTheSameTime() throws InterruptedException {
    List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch readyThreadCounter = new CountDownLatch(5);
    CountDownLatch callingThreadBlocker = new CountDownLatch(1);
    CountDownLatch completedThreadCounter = new CountDownLatch(5);
    List<Thread> workers = Stream
        .generate(() -> new Thread(new WaitingWorker(outputScraper, readyThreadCounter, callingThreadBlocker, completedThreadCounter)))
        .limit(5)
        .collect(toList());

    workers.forEach(Thread::start);
    readyThreadCounter.await();
    outputScraper.add("Workers ready");
    callingThreadBlocker.countDown();
    completedThreadCounter.await();
    outputScraper.add("Workers complete");

    assertThat(outputScraper).containsExactly(
        "Workers ready",
        "Counted down",
        "Counted down",
        "Counted down",
        "Counted down",
        "Counted down",
        "Workers complete"
    );
}
```

Этот паттерн действительно полезен для попытки воспроизвести ошибки параллелизма, так как его можно использовать, чтобы заставить тысячи потоков пытаться выполнять некоторую логику параллельно.

**Иногда мы можем столкнуться с ситуацией, когда рабочие процессы завершаются с ошибкой до обратного отсчета **CountDownLatch**. Это может привести к тому, что он никогда не достигнет нуля, а **await()** никогда не завершится:**

```java
@Override
public void run() {
    if (true) {
        throw new RuntimeException("Oh dear, I'm a BrokenWorker");
    }
    countDownLatch.countDown();
    outputScraper.add("Counted down");
}
```

**Давайте изменим наш предыдущий тест, чтобы использовать **BrokenWorker**, чтобы показать, как **await()** будет блокироваться навсегда:**

```java
@Test
public void whenFailingToParallelProcess_thenMainThreadShouldGetNotGetStuck() throws InterruptedException {
    List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch countDownLatch = new CountDownLatch(5);
    List<Thread> workers = Stream
        .generate(() -> new Thread(new BrokenWorker(outputScraper, countDownLatch)))
        .limit(5)
        .collect(toList());

    workers.forEach(Thread::start);
    countDownLatch.await();
}
```

Ясно, что это не то поведение, которое нам нужно — для приложения было бы гораздо лучше продолжать работу, чем бесконечно блокироваться.

Чтобы обойти это, давайте добавим аргумент тайм-аута к нашему вызову **await()**.

```java
boolean completed = countDownLatch.await(3L, TimeUnit.SECONDS);
assertThat(completed).isFalse();
```

Как мы видим, тест в конце концов истечет, и **await()** вернет **false**.

## Руководство по java.`util.concurrent`.Locks

Проще говоря, блокировка — это более гибкий и сложный механизм синхронизации потоков, чем стандартный синхронизированный блок.

Интерфейс **Lock** существует со времен **Java 1.5**. Он определен внутри пакета **java.`util.concurrent`.lock** и предоставляет обширные операции для блокировки.

В этом руководстве мы рассмотрим различные реализации интерфейса блокировки и их приложения.

**Есть несколько различий между использованием синхронизированного блока и использованием **API** блокировки:**

1. **Синхронизированный блок полностью содержится в методе**: У нас могут быть операции **lock()** и **unlock()** из **API** блокировки в отдельных методах.
2. **Синхронизированный блок не поддерживает справедливость**: Любой поток может получить блокировку после освобождения, и никакие предпочтения не могут быть указаны. Мы можем добиться справедливости в **API-**интерфейсах блокировки, указав свойство справедливости. Это гарантирует, что самый длинный ожидающий поток получит доступ к блокировке.
3. **Поток блокируется, если он не может получить доступ к синхронизированному блоку**: **API** блокировки предоставляет метод **tryLock()**. Поток получает блокировку только в том случае, если он доступен и не удерживается каким-либо другим потоком. Это уменьшает время блокировки потока, ожидающего блокировки.
4. **Поток, который находится в состоянии «ожидания» получения доступа к синхронизированному блоку, не может быть прерван**: **API** блокировки предоставляет метод **lockInterruptably()**, который можно использовать для прерывания потока, когда он ожидает блокировки.

**Давайте посмотрим на методы в интерфейсе блокировки:**

1. **void lock()** — получить блокировку, если она доступна. Если блокировка недоступна, поток блокируется до тех пор, пока блокировка не будет снята.
2. **void `lockInterruptably()`** — похож на **lock()**, но позволяет прервать заблокированный поток и возобновить выполнение с помощью выброшенного исключения **java.lang.InterruptedException**.
3. **boolean `tryLock()`** — это неблокирующая версия метода **lock()**. Он пытается немедленно получить блокировку, возвращает **true**, если блокировка прошла успешно.
4. **boolean `tryLock`(long timeout, TimeUnit timeUnit)** — это похоже на **tryLock()**, за исключением того, что он ждет заданный тайм-аут, прежде чем отказаться от попытки получить **Lock**.
5. **void unlock()** разблокирует экземпляр **Lock**.

Заблокированный экземпляр всегда должен быть разблокирован, чтобы избежать взаимоблокировки.

**Рекомендуемый блок кода для использования блокировки должен содержать блоки **try/catch** и **finally**:**

```java
Lock lock = ...;
lock.lock();
try {
    // критическая секция
} finally {
    lock.unlock();
}
```

### ReentrantLock

В дополнение к интерфейсу **Lock** у нас есть интерфейс **ReadWriteLock**, который поддерживает пару блокировок: одну для операций только для чтения и одну для операции записи. Блокировка чтения может одновременно удерживаться несколькими потоками, пока нет записи.

Класс **ReentrantLock** реализует интерфейс блокировки. Он предлагает ту же семантику параллелизма и памяти, что и неявная блокировка монитора, доступ к которой осуществляется с помощью синхронизированных методов и инструкций, с расширенными возможностями.

**Давайте посмотрим, как мы можем использовать **ReentrantLock** для синхронизации:**

```java
public class SharedObject {
    ReentrantLock lock = new ReentrantLock();
    int counter = 0;

    public void perform() {
        lock.lock();
        try {
            counter++;
        } finally {
            lock.unlock();
        }
    }
}
```

Нам нужно убедиться, что мы оборачиваем вызовы **lock()** и **unlock()** в блок **try-finally**, чтобы избежать тупиковых ситуаций.

**Давайте посмотрим, как работает **tryLock()**:**

```java
public void performTryLock() {
    boolean isLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);

    if (isLockAcquired) {
        try {
            // критическая секция
        } finally {
            lock.unlock();
        }
    }
}
```

В этом случае поток, вызывающий **tryLock()**, будет ждать одну секунду и прекратит ожидание, если блокировка недоступна.

### ReadWriteLock

Класс **ReentrantReadWriteLock** реализует интерфейс **ReadWriteLock**.

**Давайте посмотрим на правила получения **ReadLock** или **WriteLock** потоком:**

1. **Блокировка чтения** — если ни один поток не получил блокировку записи или не запросил ее, несколько потоков могут получить блокировку чтения.
2. **Блокировка записи** — если ни один поток не читает и не пишет, только один поток может получить блокировку записи.

**Давайте посмотрим, как использовать **ReadWriteLock**:**

```java
public class SynchronizedHashMapWithReadWriteLock {
    Map<String,String> syncHashMap = new HashMap<>();
    ReadWriteLock lock = new ReentrantReadWriteLock();
    Lock writeLock = lock.writeLock();

    public void put(String key, String value) {
        try {
            writeLock.lock();
            syncHashMap.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    public String remove(String key) {
        try {
            writeLock.lock();
            return syncHashMap.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    Lock readLock = lock.readLock();

    public String get(String key) {
        try {
            readLock.lock();
            return syncHashMap.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public boolean containsKey(String key) {
        try {
            readLock.lock();
            return syncHashMap.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }
}
```

Для обоих методов записи нам нужно окружить критическую секцию блокировкой записи — доступ к ней может получить только один поток.

Для обоих методов чтения нам нужно окружить критическую секцию блокировкой чтения. Несколько потоков могут получить доступ к этому разделу, если не выполняется операция записи.

### StampedLock

**StampedLock** представлен в **Java 8**. Он также поддерживает блокировки чтения и записи.

**Однако методы получения блокировки возвращают отметку, которая используется для снятия блокировки или для проверки того, действительна ли блокировка:**

```java
public class StampedLockDemo {
    Map<String,String> map = new HashMap<>();
    private StampedLock lock = new StampedLock();

    public void put(String key, String value) {
        long stamp = lock.writeLock();
        try {
            map.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public String get(String key) throws InterruptedException {
        long stamp = lock.readLock();
        try {
            return map.get(key);
        } finally {
            lock.unlockRead(stamp);
        }
    }
}
```

Еще одна функция **StampedLock** — оптимистическая блокировка. В большинстве случаев операциям чтения не нужно ждать завершения операции записи, и в результате этого не требуется полноценная блокировка чтения.

**Вместо этого мы можем перейти на блокировку чтения:**

```java
public String readWithOptimisticLock(String key) {
    long stamp = lock.tryOptimisticRead();
    String value = map.get(key);

    if (!lock.validate(stamp)) {
        stamp = lock.readLock();
        try {
            return map.get(key);
        } finally {
            lock.unlock(stamp);
        }
    }

    return value;
}
```

### Condition

Класс **Condition** предоставляет потоку возможность ожидать выполнения некоторого условия при выполнении критической секции.

Это может произойти, когда поток получает доступ к критической секции, но не имеет необходимого условия для выполнения своей операции. Например, поток чтения может получить доступ к блокировке общей очереди, в которой еще нет данных для потребления.

Традиционно **Java** предоставляет методы **wait()**, **notify()** и **notifyAll()** для взаимодействия потоков.

**Условия имеют схожие механизмы, но мы также можем указать несколько условий:**

```java
public class ReentrantLockWithCondition {
    Stack<String> stack = new Stack<>();
    int CAPACITY = 5;
    ReentrantLock lock = new ReentrantLock();
    Condition stackEmptyCondition = lock.newCondition();
    Condition stackFullCondition = lock.newCondition();

    public void pushToStack(String item) {
        try {
            lock.lock();
            while(stack.size() == CAPACITY) {
                stackFullCondition.await();
            }
            stack.push(item);
            stackEmptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public String popFromStack() {
        try {
            lock.lock();
            while(stack.size() == 0) {
                stackEmptyCondition.await();
            }
            return stack.pop();
        } finally {
            stackFullCondition.signalAll();
            lock.unlock();
        }
    }
}
```

## Ожидание завершения потоков в ExecutorService

Инфраструктура **ExecutorService** упрощает обработку задач в нескольких потоках. Мы собираемся проиллюстрировать несколько сценариев, в которых мы ждем, пока потоки закончат свое выполнение.

Кроме того, мы покажем, как корректно закрыть **ExecutorService** и дождаться завершения выполнения уже запущенных потоков.

При использовании **Executor** мы можем закрыть его, вызвав методы **shutdown()** или **shutdownNow()**. Хотя он не будет ждать, пока все потоки перестанут выполняться.

Ожидание завершения существующих потоков может быть достигнуто с помощью метода **awaitTermination()**.

**Это блокирует поток до тех пор, пока все задачи не завершат свое выполнение или не будет достигнуто указанное время ожидания:**

```java
public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
    threadPool.shutdown();
    try {
        if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
            threadPool.shutdownNow();
        }
    } catch (InterruptedException ex) {
        threadPool.shutdownNow();
        Thread.currentThread().interrupt();
    }
}
```

Далее давайте рассмотрим другой подход к решению этой проблемы — использование **CountDownLatch** для сигнализации о завершении задачи.

Мы можем инициализировать его значением, представляющим количество раз, которое может быть уменьшено, прежде чем все потоки, вызвавшие метод **await()**, будут уведомлены.

**Например, если нам нужно, чтобы текущий поток ждал, пока другие **N** потоков закончат свое выполнение, мы можем инициализировать защелку, используя **N**:**

```java
ExecutorService WORKER_THREAD_POOL = Executors.newFixedThreadPool(10);
CountDownLatch latch = new CountDownLatch(2);

for (int i = 0; i < 2; i++) {
    WORKER_THREAD_POOL.submit(() -> {
        try {
            // выполнение задачи
            latch.countDown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    });
}

latch.await();
```

Первый подход, который мы можем использовать для запуска потоков, — это метод **invokeAll()**. Метод возвращает список объектов **Future** после завершения всех задач или истечения времени ожидания.

**Кроме того, мы должны отметить, что порядок возвращаемых объектов **Future** такой же, как в списке предоставленных объектов **Callable**:**

```java
ExecutorService WORKER_THREAD_POOL = Executors.newFixedThreadPool(10);
List<Callable<String>> callables = Arrays.asList(
    new DelayedCallable("fast thread", 100),
    new DelayedCallable("slow thread", 3000)
);

long startProcessingTime = System.currentTimeMillis();
List<Future<String>> futures = WORKER_THREAD_POOL.invokeAll(callables);
awaitTerminationAfterShutdown(WORKER_THREAD_POOL);

long totalProcessingTime = System.currentTimeMillis() - startProcessingTime;
assertTrue(totalProcessingTime >= 3000);

String firstThreadResponse = futures.get(0).get();
assertTrue("fast thread".equals(firstThreadResponse));

String secondThreadResponse = futures.get(1).get();
assertTrue("slow thread".equals(secondThreadResponse));
```

Другой подход к запуску нескольких потоков — использование **ExecutorCompletionService**. Он использует предоставленный **ExecutorService** для выполнения задач.

**Одно отличие от **invokeAll()** заключается в порядке, в котором возвращаются фьючерсы, представляющие выполненные задачи. **ExecutorCompletionService** использует очередь для хранения результатов в том порядке, в котором они были завершены, тогда как **invokeAll()** возвращает список, имеющий тот же последовательный порядок, что и итератор для данного списка задач:**

```java
CompletionService<String> service = new ExecutorCompletionService<>(WORKER_THREAD_POOL);
List<Callable<String>> callables = Arrays.asList(
    new DelayedCallable("fast thread", 100),
    new DelayedCallable("slow thread", 3000)
);

for (Callable<String> callable : callables) {
    service.submit(callable);
}
```

**Доступ к результатам можно получить с помощью метода **take()**:**

```java
long startProcessingTime = System.currentTimeMillis();
Future<String> future = service.take();
String firstThreadResponse = future.get();
long totalProcessingTime = System.currentTimeMillis() - startProcessingTime;

assertTrue("First response should be from the fast thread", "fast thread".equals(firstThreadResponse));
assertTrue(totalProcessingTime >= 100 && totalProcessingTime < 1000);

future = service.take();
String secondThreadResponse = future.get();
totalProcessingTime = System.currentTimeMillis() - startProcessingTime;

assertTrue("Last response should be from the slow thread", "slow thread".equals(secondThreadResponse));
assertTrue(totalProcessingTime >= 3000 && totalProcessingTime < 4000);

awaitTerminationAfterShutdown(WORKER_THREAD_POOL);
```

В зависимости от варианта использования у нас есть различные варианты ожидания завершения выполнения потоков.

**CountDownLatch** полезен, когда нам нужен механизм для уведомления одного или нескольких потоков о завершении набора операций, выполняемых другими потоками.

**ExecutorCompletionService** полезен, когда нам нужно как можно скорее получить доступ к результату задачи, и другие подходы, когда мы хотим дождаться завершения всех запущенных задач.

## Руководство по CompletableFuture

Это руководство представляет собой руководство по функциональным возможностям и вариантам использования класса **CompletableFuture**, который был представлен как улучшение **API** параллелизма **Java 8**.

Трудно рассуждать об асинхронных вычислениях. Обычно мы хотим думать о любом вычислении как о серии шагов, но в случае асинхронного вычисления действия, представленные в виде обратных вызовов, как правило, либо разбросаны по коду, либо глубоко вложены друг в друга. Все становится еще хуже, когда нам нужно обработать ошибки, которые могут возникнуть на одном из шагов.

Интерфейс **Future** был добавлен в **Java 5** как результат асинхронных вычислений, но в нем не было методов для объединения этих вычислений или обработки возможных ошибок.

В **Java 8** появился класс **CompletableFuture**. Наряду с интерфейсом **Future** он также реализовал интерфейс **CompletionStage**. Этот интерфейс определяет контракт для шага асинхронных вычислений, который мы можем комбинировать с другими шагами.

**CompletableFuture** — это одновременно строительный блок и фреймворк с примерно **50** различными методами для составления, объединения и выполнения шагов асинхронных вычислений и обработки ошибок.

Такой большой **API** может быть ошеломляющим, но в основном это относится к нескольким четким и четким вариантам использования.

Во-первых, класс **CompletableFuture** реализует интерфейс **Future**, поэтому мы можем использовать его как реализацию **Future**, но с дополнительной логикой завершения.

Например, мы можем создать экземпляр этого класса с конструктором без аргументов, чтобы представить какой-то будущий результат, передать его потребителям и завершить его в какой-то момент в будущем, используя метод **complete**. Потребители могут использовать метод **get**, чтобы заблокировать текущий поток, пока не будет предоставлен этот результат.

В приведенном ниже примере у нас есть метод, который создает экземпляр **CompletableFuture**, затем запускает некоторые вычисления в другом потоке и немедленно возвращает **Future**.

**Когда вычисление завершено, метод завершает **Future**, предоставляя результат методу **complete**:**

```java
public Future<String> calculateAsync() throws InterruptedException {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    Executors.newCachedThreadPool().submit(() -> {
        Thread.sleep(500);
        completableFuture.complete("Hello");
        return null;
    });

    return completableFuture;
}
```

Чтобы запустить вычисления, мы используем **Executor API**. Этот метод создания и завершения **CompletableFuture** можно использовать вместе с любым механизмом параллелизма или **API**, включая необработанные потоки.

Обратите внимание, что метод **calculateAsync** возвращает экземпляр **Future**.

Мы просто вызываем метод, получаем экземпляр **Future** и вызываем для него метод **get**, когда готовы заблокировать результат.

**Также обратите внимание, что метод **get** генерирует некоторые проверенные исключения, а именно **ExecutionException** (инкапсулирует исключение, возникшее во время вычисления) и **InterruptedException** (исключение, означающее, что поток, выполняющий метод, был прерван):**

```java
Future<String> completableFuture = calculateAsync();
String result = completableFuture.get();
assertEquals("Hello", result);
```

**Если мы уже знаем результат вычисления, мы можем использовать статический метод **completeFuture** с аргументом, представляющим результат этого вычисления. Следовательно, метод **get** из **Future** никогда не будет блокироваться, вместо этого немедленно возвращая этот результат:**

```java
Future<String> completableFuture = CompletableFuture.completedFuture("Hello");
String result = completableFuture.get();
assertEquals("Hello", result);
```

В качестве альтернативного сценария мы можем захотеть отменить выполнение **Future**.

Приведенный выше код позволяет нам выбрать любой механизм одновременного выполнения, но что, если мы хотим пропустить этот шаблон и просто выполнить какой-то код асинхронно?

Статические методы **runAsync** и **supplyAsync** позволяют создать экземпляр **CompletableFuture** из функциональных типов **Runnable** и **Supplier** соответственно.

И **Runnable**, и **Supplier** — это функциональные интерфейсы, которые позволяют передавать их экземпляры в виде лямбда-выражений благодаря новой функции **Java 8**.

**Интерфейс Runnable** — это тот же самый старый интерфейс, который используется в потоках, и он не позволяет возвращать значение.

**Интерфейс Supplier** — это универсальный функциональный интерфейс с одним методом, который не имеет аргументов и возвращает значение параметризованного типа.

**Это позволяет нам предоставить экземпляр **Supplier** в виде лямбда-выражения, которое выполняет вычисления и возвращает результат. Это так же просто, как:**

```java
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");
assertEquals("Hello", future.get());
```

**Самый общий способ обработки результата вычисления — передать его функции. Метод **thenApply** делает именно это; он принимает экземпляр **Function**, использует его для обработки результата и возвращает **Future**, который содержит значение, возвращаемое функцией:**

```java
CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future = completableFuture.thenApply(s -> s + " World");
assertEquals("Hello World", future.get());
```

Если нам не нужно возвращать значение по цепочке **Future**, мы можем использовать экземпляр функционального интерфейса **Consumer**. Его единственный метод принимает параметр и возвращает **void**.

**В **CompletableFuture** есть метод для этого варианта использования. Метод **thenAccept** получает **Consumer** и передает ему результат вычисления. Затем последний вызов **future.get()** возвращает экземпляр типа **Void**:**

```java
CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<Void> future = completableFuture.thenAccept(s -> System.out.println("Computation returned: " + s));
future.get();
```

**Наконец, если нам не нужно значение вычисления и мы не хотим возвращать какое-то значение в конце цепочки, мы можем передать лямбду **Runnable** методу **thenRun**. В следующем примере мы просто выводим строку в консоль после вызова **future.get()**:**

```java
CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<Void> future = completableFuture.thenRun(() -> System.out.println("Computation finished."));
future.get();
```

Лучшая часть **API CompletableFuture** — это возможность комбинировать экземпляры **CompletableFuture** в цепочке шагов вычислений.

Результатом этой цепочки является само **CompletableFuture**, которое допускает дальнейшую цепочку и комбинирование. Этот подход повсеместно используется в функциональных языках и часто упоминается как монадический шаблон проектирования.

В следующем примере мы используем метод **thenCompose** для последовательного связывания двух фьючерсов.

**Обратите внимание, что этот метод принимает функцию, которая возвращает экземпляр **CompletableFuture**. Аргумент этой функции является результатом предыдущего шага вычисления. Это позволяет нам использовать это значение внутри следующей лямбды **CompletableFuture**:**

```java
CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
    .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World"));

assertEquals("Hello World", completableFuture.get());
```

Метод **thenCompose** вместе с **thenApply** реализуют базовые строительные блоки монадического шаблона. Они тесно связаны с методами **map** и **flatMap** классов **Stream** и **Optional**, также доступных в **Java 8**.

Оба метода получают функцию и применяют ее к результату вычислений, но метод **thenCompose (flatMap)** получает функцию, которая возвращает другой объект того же типа. Эта функциональная структура позволяет составлять экземпляры этих классов как строительные блоки.

**Если мы хотим выполнить два независимых **Future** и что-то сделать с их результатами, мы можем использовать метод **thenCombine**, который принимает **Future** и **Function** с двумя аргументами для обработки обоих результатов:**

```java
CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
    .thenCombine(CompletableFuture.supplyAsync(() -> " World"), (s1, s2) -> s1 + s2);

assertEquals("Hello World", completableFuture.get());
```

**Более простой случай, когда мы хотим что-то сделать с двумя результатами **Futures**, но нам не нужно передавать какое-либо результирующее значение по цепочке **Future**. Метод **thenAcceptBoth** поможет:**

```java
CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
    .thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"),
                   (s1, s2) -> System.out.println(s1 + s2));
```

В наших предыдущих разделах мы показали примеры, касающиеся **thenApply()** и **thenCompose()**. Оба **API** помогают связывать разные вызовы **CompletableFuture**, но использование этих двух функций отличается.

Мы можем использовать этот метод для работы с результатом предыдущего вызова. Однако важно помнить, что возвращаемый тип будет объединен из всех вызовов.

**Так что этот метод полезен, когда мы хотим преобразовать результат вызова **CompletableFuture**:**

```java
CompletableFuture<Integer> finalResult = compute().thenApply(s -> s + 1);
```

**Метод **thenCompose()** похож на **thenApply()** в том смысле, что оба метода возвращают новую стадию завершения. Однако **thenCompose()** использует предыдущий этап в качестве аргумента. Он будет сглаживать и возвращать **Future** с результатом напрямую, а не вложенным **future**, как мы наблюдали в **thenApply()**:**

```java
CompletableFuture<Integer> computeAnother(Integer i) {
    return CompletableFuture.supplyAsync(() -> 10 + i);
}

CompletableFuture<Integer> finalResult = compute().thenCompose(this::computeAnother);
```

Поэтому, если идея состоит в том, чтобы объединить методы **CompletableFuture** в цепочку, то лучше использовать **thenCompose()**.

Также обратите внимание, что разница между этими двумя методами аналогична разнице между **map()** и **flatMap()**.

Когда нам нужно выполнить несколько фьючерсов параллельно, мы обычно хотим дождаться, пока все они будут выполнены, а затем обработать их объединенные результаты.

**Статический метод **CompletableFuture.allOf** позволяет дождаться завершения всех фьючерсов, предоставленных в виде **var-arg**:**

```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Beautiful");
CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3);
combinedFuture.get();

assertTrue(future1.isDone());
assertTrue(future2.isDone());
assertTrue(future3.isDone());
```

**Обратите внимание, что **CompletableFuture.`allOf()`** возвращает тип **CompletableFuture<`Void`>**. Ограничение этого метода в том, что он не возвращает комбинированные результаты всех фьючерсов. Вместо этого нам приходится вручную получать результаты из **Futures**. К счастью, метод **CompletableFuture.join()** и **Java 8 Streams API** упрощают задачу:**

```java
String combined = Stream.of(future1, future2, future3)
    .map(CompletableFuture::join)
    .collect(Collectors.joining(" "));

assertEquals("Hello Beautiful World", combined);
```

Метод **CompletableFuture.join()** аналогичен методу **get**, но выдает непроверенное исключение в случае, если **Future** не завершается нормально. Это позволяет использовать его как ссылку на метод в методе **Stream.map()**.

Для обработки ошибок в цепочке шагов асинхронных вычислений мы должны аналогичным образом адаптировать идиому **throw/catch**.

Вместо перехвата исключения в синтаксическом блоке класс **CompletableFuture** позволяет нам обрабатывать его в специальном методе дескриптора. Этот метод получает два параметра: результат вычисления (если оно завершилось успешно) и выброшенное исключение (если какой-то шаг вычисления не завершился нормально).

**В следующем примере мы используем метод **handle** для предоставления значения по умолчанию, когда асинхронное вычисление приветствия было завершено с ошибкой, поскольку не было указано имя:**

```java
String name = null;

CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
    if (name == null) {
        throw new RuntimeException("Computation error!");
    }
    return "Hello, " + name;
}).handle((s, t) -> s != null ? s : "Hello, Stranger!");

assertEquals("Hello, Stranger!", completableFuture.get());
```

**В качестве альтернативного сценария предположим, что мы хотим вручную завершить **Future** со значением, как в первом примере, но также иметь возможность завершить его с исключением. Именно для этого и предназначен метод **completeExceptionally**. Метод **completableFuture.get()** в следующем примере создает **ExecutionException** с **RuntimeException** в качестве причины:**

```java
CompletableFuture<String> completableFuture = new CompletableFuture<>();
completableFuture.completeExceptionally(new RuntimeException("Calculation failed!"));
completableFuture.get(); // ExecutionException
```

В приведенном выше примере мы могли бы обработать исключение асинхронно с помощью метода **handle**, но с помощью метода **get** мы можем использовать более типичный подход синхронной обработки исключений.

Большинство методов свободного **API** в классе **CompletableFuture** имеют два дополнительных варианта с постфиксом **Async**. Эти методы обычно предназначены для запуска соответствующего шага выполнения в другом потоке.

Методы без постфикса **Async** запускают следующую стадию выполнения, используя вызывающий поток. Напротив, метод **Async** без аргумента **Executor** выполняет шаг, используя общую реализацию пула **fork/join Executor**, доступ к которой осуществляется с помощью метода **ForkJoinPool.`commonPool()`**. Наконец, метод **Async** с аргументом **Executor** выполняет шаг, используя переданный **Executor**.

**Вот модифицированный пример, который обрабатывает результат вычисления с экземпляром **Function**. Единственным видимым отличием является метод **thenApplyAsync**, но под капотом приложение функции завернуто в экземпляр **ForkJoinTask** (подробнее о фреймворке Fork/Join см. в статье «Руководство по фреймворку Fork/Join в Java»). Это позволяет нам еще больше распараллелить наши вычисления и более эффективно использовать системные ресурсы:**

```java
CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future = completableFuture.thenApplyAsync(s -> s + " World");
assertEquals("Hello World", future.get());
```

### Изменения в Java 9

**Java 9** расширяет **API CompletableFuture** следующими изменениями:**

1. Добавлены новые фабричные методы
2. Поддержка задержек и тайм-аутов
3. Улучшенная поддержка подклассов и новые **API** экземпляров

**Исполнитель по:**

1. **Executor `defaultExecutor()`**
2. **CompletableFuture<U> `newIncompleteFuture()`**
3. **CompletableFuture<T> copy()**
4. **CompletionStage<T> `minimalCompletionStage()`**
5. **CompletableFuture<T> `completeAsync`(Supplier<? extends T> supplier, Executor executor)**
6. **CompletableFuture<T> `completeAsync`(Supplier<? extends T> supplier)**
7. **CompletableFuture<T> `orTimeout`(long timeout, TimeUnit unit)**
8. **CompletableFuture<T> `completeOnTimeout`(T value, long timeout, TimeUnit unit)**

**Также теперь у нас есть несколько статических служебных методов:**

1. **Executor `delayedExecutor`(long delay, TimeUnit unit, Executor executor)**
2. **Executor `delayedExecutor`(long delay, TimeUnit unit)**
3. **<U> `CompletionStage`<U> `completedStage`(U value)**
4. **<U> `CompletionStage`<U> `failedStage`(Throwable ex)**
5. **<U> `CompletableFuture`<U> `failedFuture`(Throwable ex)**

## Руководство по CyclicBarrier

**CyclicBarriers** — это конструкции синхронизации, которые были представлены в **Java 5** как часть пакета **java.util.concurrent**.

**CyclicBarrier** — это синхронизатор, который позволяет набору потоков ожидать друг от друга достижения общей точки выполнения, также называемой барьером.

**CyclicBarriers** используются в программах, в которых у нас есть фиксированное количество потоков, которые должны ждать, пока друг друга достигнут общей точки, прежде чем продолжить выполнение.

Барьер называется циклическим, потому что его можно использовать повторно после освобождения ожидающих потоков.

**Конструктор **CyclicBarrier** прост. Требуется одно целое число, обозначающее количество потоков, которым необходимо вызвать метод **await()** для экземпляра барьера, чтобы обозначить достижение общей точки выполнения:**

```java
public CyclicBarrier(int parties)
```

Потоки, которым необходимо синхронизировать свое выполнение, также называются сторонами, и вызов метода **await()** — это то, как мы можем зарегистрировать, что определенный поток достиг точки барьера.

Этот вызов является синхронным, и поток, вызывающий этот метод, приостанавливает выполнение до тех пор, пока указанное количество потоков не вызовет один и тот же метод на барьере. Такая ситуация, когда требуемое количество потоков вызвало **await()**, называется отключением барьера.

**При желании мы можем передать второй аргумент конструктору, который является экземпляром **Runnable**. Это имеет логику, которая будет выполняться последним потоком, преодолевающим барьер:**

```java
public CyclicBarrier(int parties, Runnable barrierAction)
```

**Чтобы увидеть **CyclicBarrier** в действии, давайте рассмотрим следующий сценарий:**

Существует операция, которую выполняет фиксированное количество потоков и сохраняет соответствующие результаты в списке. Когда все потоки завершают выполнение своих действий, один из них (обычно последний, преодолевший барьер) начинает обработку данных, полученных каждым из них.

**Давайте реализуем основной класс, в котором происходят все действия:**

```java
public class CyclicBarrierDemo {
    private CyclicBarrier cyclicBarrier;
    private List<List<Integer>> partialResults = Collections.synchronizedList(new ArrayList<>());
    private Random random = new Random();
    private int NUM_PARTIAL_RESULTS;
    private int NUM_WORKERS;
}
```

Этот класс довольно прост: **NUM_WORKERS** — это количество потоков, которые будут выполняться, а **NUM_PARTIAL_RESULTS** — это количество результатов, которые будет производить каждый из рабочих потоков.

Наконец, у нас есть **partialResults** — список, в котором будут храниться результаты каждого из этих рабочих потоков. Обратите внимание, что этот список является **SynchronizedList**, потому что несколько потоков будут записывать в него одновременно, а метод **add()** не является потокобезопасным для простого **ArrayList**.

**Теперь реализуем логику каждого из рабочих потоков:**

```java
public class CyclicBarrierDemo {
    class NumberCruncherThread implements Runnable {
        @Override
        public void run() {
            String thisThreadName = Thread.currentThread().getName();
            List<Integer> partialResult = new ArrayList<>();

            for (int i = 0; i < NUM_PARTIAL_RESULTS; i++) {
                Integer num = random.nextInt(10);
                System.out.println(thisThreadName + ": Crunching some numbers! Final result - " + num);
                partialResult.add(num);
            }

            partialResults.add(partialResult);

            try {
                System.out.println(thisThreadName + " waiting for others to reach barrier.");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
            } catch (BrokenBarrierException e) {
            }
        }
    }
}
```

Теперь мы реализуем логику, которая запускается при срабатывании барьера.

**Для простоты давайте просто добавим все числа в список частичных результатов:**

```java
public class CyclicBarrierDemo {
    class AggregatorThread implements Runnable {
        @Override
        public void run() {
            String thisThreadName = Thread.currentThread().getName();
            System.out.println(
                thisThreadName + ": Computing sum of " + NUM_WORKERS
                + " workers, having " + NUM_PARTIAL_RESULTS + " results each.");

            int sum = 0;
            for (List<Integer> threadResult : partialResults) {
                System.out.print("Adding ");
                for (Integer partialResult : threadResult) {
                    System.out.print(partialResult + " ");
                    sum += partialResult;
                }
                System.out.println();
            }
            System.out.println(thisThreadName + ": Final result = " + sum);
        }
    }
}
```

**Последним шагом будет создание **CyclicBarrier** и начало работы с помощью метода **main()**:**

```java
public class CyclicBarrierDemo {
    public void runSimulation(int numWorkers, int numberOfPartialResults) {
        NUM_PARTIAL_RESULTS = numberOfPartialResults;
        NUM_WORKERS = numWorkers;

        cyclicBarrier = new CyclicBarrier(NUM_WORKERS, new AggregatorThread());

        System.out.println("Spawning " + NUM_WORKERS
            + " worker threads to compute "
            + NUM_PARTIAL_RESULTS + " partial results each");

        for (int i = 0; i < NUM_WORKERS; i++) {
            Thread worker = new Thread(new NumberCruncherThread());
            worker.setName("Thread " + i);
            worker.start();
        }
    }

    public static void main(String[] args) {
        CyclicBarrierDemo demo = new CyclicBarrierDemo();
        demo.runSimulation(5, 3);
    }
}
```

В приведенном выше коде мы инициализировали циклический барьер с **5** потоками, каждый из которых производит **3** целых числа как часть своих вычислений и сохраняет их в результирующем списке.

Как только барьер отключен, последний поток, отключивший барьер, выполняет логику, указанную в **AggregatorThread**, а именно — складывает все числа, произведенные потоками.

**Вот результат одного выполнения вышеуказанной программы — каждое выполнение может создавать разные результаты, поскольку потоки могут создаваться в другом порядке:**

```text
Spawning 5 worker threads to compute 3 partial results each
Thread 0: Crunching some numbers! Final result - 6
Thread 0: Crunching some numbers! Final result - 2
Thread 0: Crunching some numbers! Final result - 2
Thread 0 waiting for others to reach barrier.
Thread 1: Crunching some numbers! Final result - 2
Thread 1: Crunching some numbers! Final result - 0
Thread 1: Crunching some numbers! Final result - 5
Thread 1 waiting for others to reach barrier.
Thread 3: Crunching some numbers! Final result - 6
Thread 3: Crunching some numbers! Final result - 4
Thread 3: Crunching some numbers! Final result - 0
Thread 3 waiting for others to reach barrier.
Thread 2: Crunching some numbers! Final result - 1
Thread 2: Crunching some numbers! Final result - 1
Thread 2: Crunching some numbers! Final result - 0
Thread 2 waiting for others to reach barrier.
Thread 4: Crunching some numbers! Final result - 9
Thread 4: Crunching some numbers! Final result - 3
Thread 4: Crunching some numbers! Final result - 5
Thread 4 waiting for others to reach barrier.
Thread 4: Computing final sum of 5 workers, having 3 results each.
Adding 6 2 2
Adding 2 0 5
Adding 6 4 0
Adding 1 1 0
Adding 9 3 5
Thread 4: Final result = 46
```

Как видно из приведенного выше вывода, поток **4** — это тот, который отключает барьер, а также выполняет логику финальной агрегации. Также нет необходимости, чтобы потоки выполнялись в том порядке, в котором они были запущены, как показано в приведенном выше примере.

## Руководство по ThreadLocalRandom

**Генерация случайных значений** — очень распространенная задача. Вот почему **Java** предоставляет класс **java.util.Random**.

Однако этот класс плохо работает в многопоточной среде.

Проще говоря, причина низкой производительности **Random** в многопоточной среде связана с конкуренцией, учитывая, что несколько потоков совместно используют один и тот же экземпляр **Random**.

Чтобы обойти это ограничение, **Java** представила класс **ThreadLocalRandom** в **JDK** 7 — для генерации случайных чисел в многопоточной среде.

Давайте посмотрим, как работает **ThreadLocalRandom** и как его использовать в реальных приложениях.

**ThreadLocalRandom** представляет собой комбинацию классов **ThreadLocal** и **Random** (подробнее об этом позже) и изолирован от текущего потока. Таким образом, он достигает лучшей производительности в многопоточной среде, просто избегая любого параллельного доступа к экземплярам **Random**.

Случайное число, полученное одним потоком, не зависит от другого потока, тогда как **java.util.Random** предоставляет случайные числа глобально.

Кроме того, в отличие от **Random**, **ThreadLocalRandom** не поддерживает явное задание начального значения. Вместо этого он переопределяет метод **setSeed(long seed)**, унаследованный от **Random**, чтобы всегда вызывать **UnsupportedOperationException** при вызове.

**На данный момент мы установили, что класс **Random** плохо работает в средах с большим числом параллельных вычислений. Чтобы лучше понять это, давайте посмотрим, как реализована одна из его основных операций, **next(int)**:**

```java
private final AtomicLong seed;

protected int next(int bits) {
    long oldseed, nextseed;
    AtomicLong seed = this.seed;
    do {
        oldseed = seed.get();
        nextseed = (oldseed * multiplier + addend) & mask;
    } while (!seed.compareAndSet(oldseed, nextseed));

    return (int)(nextseed >>> (48 - bits));
}
```

Это реализация **Java** для алгоритма **Linear Congruential Generator**. Очевидно, что все потоки используют одну и ту же переменную начального экземпляра.

Чтобы сгенерировать следующий случайный набор битов, он сначала пытается атомарно изменить общее начальное значение с помощью **compareAndSet** или, для краткости, **CAS**.

Когда несколько потоков пытаются одновременно обновить начальное число с помощью **CAS**, выигрывает один поток и обновляет начальное значение, а остальные проигрывают. Проигравшие потоки будут повторять один и тот же процесс снова и снова, пока не получат возможность обновить значение и, в конечном итоге, сгенерировать случайное число.

Этот алгоритм не блокируется, и разные потоки могут выполняться одновременно. Однако, когда конкуренция высока, количество сбоев и повторных попыток **CAS** значительно повлияет на общую производительность.

С другой стороны, **ThreadLocalRandom** полностью устраняет это соперничество, поскольку каждый поток имеет свой собственный экземпляр **Random** и, следовательно, свое собственное ограниченное семя.

Давайте теперь рассмотрим некоторые способы генерации случайных значений **int, long** и **double**.

Согласно документации **Oracle**, нам просто нужно вызвать метод **ThreadLocalRandom.current()**, и он вернет экземпляр **ThreadLocalRandom** для текущего потока. Затем мы можем генерировать случайные значения, вызывая доступные методы экземпляра класса.

**Давайте сгенерируем случайное значение **int** без каких-либо ограничений:**

```java
int unboundedRandomValue = ThreadLocalRandom.current().nextInt();
```

Далее давайте посмотрим, как мы можем сгенерировать случайное ограниченное целочисленное значение, то есть значение между заданным нижним и верхним пределом.

**Вот пример генерации случайного значения **int** от **0** до **100**:**

```java
int boundedRandomValue = ThreadLocalRandom.current().nextInt(0, 100);
```

Обратите внимание, что **0** — это нижний предел с включением, а **100** — исключительный верхний предел.

Мы можем генерировать случайные значения для **long** и **double**, вызывая методы **nextLong()** и **nextDouble()** аналогично тому, как показано в примерах выше.

**Java 8** также добавляет метод **nextGaussian()** для генерации следующего нормально распределенного значения со средним значением **0.0** и стандартным отклонением **1.0** от последовательности генератора.

Как и в случае с классом **Random**, мы также можем использовать методы **doubles()**, **ints()** и **longs()** для генерации потоков случайных значений.

Давайте посмотрим, как мы можем генерировать случайные значения в многопоточной среде, используя два класса, а затем сравним их производительность с помощью **JMH**.

**Во-первых, давайте создадим пример, в котором все потоки совместно используют один экземпляр **Random**. Здесь мы передаем задачу генерации случайного значения с использованием экземпляра **Random** в **ExecutorService**:**

```java
ExecutorService executor = Executors.newWorkStealingPool();
List<Callable<Integer>> callables = new ArrayList<>();
Random random = new Random();

for (int i = 0; i < 1000; i++) {
    callables.add(() -> {
        return random.nextInt();
    });
}

executor.invokeAll(callables);
```

**Вот результат использования **Random** с бенчмарками **JMH**:**

```text
# Run complete. Total time: 00:00:36
Benchmark Mode Cnt Score Error Units
ThreadLocalRandomBenchMarker.randomValuesUsingRandom avgt 20 771.613 ± 222.220 us/op
```

**Точно так же давайте теперь используем **ThreadLocalRandom** вместо экземпляра **Random**, который использует один экземпляр **ThreadLocalRandom** для каждого потока в пуле:**

```java
ExecutorService executor = Executors.newWorkStealingPool();
List<Callable<Integer>> callables = new ArrayList<>();

for (int i = 0; i < 1000; i++) {
    callables.add(() -> {
        return ThreadLocalRandom.current().nextInt();
    });
}

executor.invokeAll(callables);
```

**Вот результат использования **ThreadLocalRandom**:**

```text
# Run complete. Total time: 00:00:36
Benchmark Mode Cnt Score Error Units
ThreadLocalRandomBenchMarker.randomValuesUsingThreadLocalRandom avgt 20 624.911 ± 113.268 us/op
```

Наконец, сравнивая приведенные выше результаты **JMH** как для **Random**, так и для **ThreadLocalRandom**, мы ясно видим, что среднее время, необходимое для генерации **1000** случайных значений с использованием **Random**, составляет **772** микросекунды, тогда как с использованием **ThreadLocalRandom** оно составляет около **625** микросекунд.

Таким образом, мы можем сделать вывод, что **ThreadLocalRandom** более эффективен в высококонкурентной среде.

### Внутренняя реализация

Хорошей мысленной моделью является представление о **ThreadLocalRandom** как о комбинации классов **ThreadLocal** и **Random**. На самом деле эта ментальная модель была согласована с фактической реализацией до **Java 8**.

**Однако в **Java 8** это выравнивание полностью сломалось, поскольку **ThreadLocalRandom** стал **singleton**. Вот как метод **current()** выглядит в **Java 8+**:**

```java
static final ThreadLocalRandom instance = new ThreadLocalRandom();

public static ThreadLocalRandom current() {
    if (U.getInt(Thread.currentThread(), PROBE) == 0)
        localInit();
    return instance;
}
```

Это правда, что совместное использование одного глобального экземпляра **Random** приводит к неоптимальной производительности в условиях высокой конкуренции. Однако использование одного выделенного экземпляра на поток также является излишним.

**Вместо выделенного экземпляра **Random** для каждого потока каждый поток должен поддерживать только свое собственное начальное значение. Начиная с **Java 8**, сам класс **Thread** был модифицирован для сохранения начального значения:**

```java
public class Thread implements Runnable {
    @jdk.internal.vm.annotation.Contended("tlr")
    long threadLocalRandomSeed;

    @jdk.internal.vm.annotation.Contended("tlr")
    int threadLocalRandomProbe;

    @jdk.internal.vm.annotation.Contended("tlr")
    int threadLocalRandomSecondarySeed;
}
```

Переменная **threadLocalRandomSeed** отвечает за сохранение текущего начального значения для **ThreadLocalRandom**. Более того, вторичное семя **threadLocalRandomSecondarySeed** обычно используется внутри такими организациями, как **ForkJoinPool**.

**Эта реализация включает в себя несколько оптимизаций, чтобы сделать **ThreadLocalRandom** еще более производительным:**

1. **Предотвращение ложного совместного использования** с помощью аннотации **@Contended**, которая в основном добавляет достаточно заполнения, чтобы изолировать соперничающие переменные в их собственных строках кэша.
2. **Использование `sun.misc`.Unsafe** для обновления этих трех переменных вместо использования **Reflection API**.
3. **Избегание дополнительных операций поиска** в хеш-таблицах, связанных с реализацией **ThreadLocal**.

## CyclicBarrier против CountDownLatch

В этом руководстве мы сравним **CyclicBarrier** и **CountDownLatch** и попытаемся понять сходства и различия между ними.

Когда дело доходит до параллелизма, может быть сложно понять, для чего каждый из них предназначен.

Прежде всего, и **CountDownLatch**, и **CyclicBarrier** используются для управления многопоточными приложениями.

И оба они предназначены для выражения того, как данный поток или группа потоков должны ожидать.

**CountDownLatch** — это конструкция, которую поток ожидает, в то время как другие потоки отсчитывают защелку до тех пор, пока она не достигнет нуля.

Мы можем думать об этом как о блюде в ресторане, которое готовят. Независимо от того, какой повар приготовит сколько угодно **n** блюд, официант должен дождаться, пока все блюда не будут на тарелке. Если на тарелку помещается **n** предметов, любая кухарка будет отсчитывать защелку для каждого предмета, который она кладет на тарелку.

**CyclicBarrier** — это многократно используемая конструкция, в которой группа потоков вместе ожидает прибытия всех потоков. В этот момент барьер разрушается, и действие может быть предпринято по желанию.

Мы можем думать об этом как о группе друзей. Каждый раз, когда они планируют поесть в ресторане, они выбирают общую точку, где они могут встретиться. Там они ждут друг друга, и только когда все соберутся, они смогут пойти в ресторан, чтобы вместе поесть.

А для более подробной информации о каждом из них в отдельности обратитесь к нашим предыдущим руководствам по **CountDownLatch** и **CyclicBarrier** соответственно.

Давайте углубимся в некоторые семантические различия между этими двумя классами.

Как указано в определениях, **CyclicBarrier** позволяет нескольким потокам ожидать друг друга, тогда как **CountDownLatch** позволяет одному или нескольким потокам ожидать завершения ряда задач.

Короче говоря, **CyclicBarrier** поддерживает количество потоков, тогда как **CountDownLatch** поддерживает количество задач.

**В следующем коде мы определяем **CountDownLatch** со счетчиком, равным двум. Затем мы дважды вызываем **countDown()** из одного потока:**

```java
CountDownLatch countDownLatch = new CountDownLatch(2);

Thread t = new Thread(() -> {
    countDownLatch.countDown();
    countDownLatch.countDown();
});

t.start();
countDownLatch.await();

assertEquals(0, countDownLatch.getCount());
```

Как только защелка достигает нуля, вызов **await** возвращается.

Обратите внимание, что в этом случае мы смогли заставить один и тот же поток уменьшить счетчик дважды.

**CyclicBarrier**, однако, отличается в этом отношении.

**Как и в приведенном выше примере, мы создаем **CyclicBarrier**, снова со счетчиком два, и вызываем для него **await()**, на этот раз из того же потока:**

```java
CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

Thread t = new Thread(() -> {
    try {
        cyclicBarrier.await();
        cyclicBarrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
    }
});

t.start();
assertEquals(1, cyclicBarrier.getNumberWaiting());
assertFalse(cyclicBarrier.isBroken());
```

Первое отличие состоит в том, что ожидающие потоки сами являются барьером.

Во-вторых, и что более важно, второй метод **await()** бесполезен. Один поток не может отсчитывать барьер дважды.

В самом деле, поскольку **t** должен ждать, пока другой поток вызовет **await()** — чтобы довести счет до двух, — второй вызов **await()** для **t** фактически не будет вызван до тех пор, пока барьер уже не будет преодолен!

В нашем тесте барьер не был преодолен, потому что у нас есть только один ожидающий поток, а не два потока, которые потребуются для отключения барьера. Это также видно из метода **cyclicBarrier.`isBroken()`**, который возвращает **false**.

### Основные различия

1. **Повторное использование**: **CyclicBarrier** может быть использован повторно после того, как потоки освобождаются, тогда как **CountDownLatch** нельзя использовать повторно после того, как счетчик достигает нуля.
2. **Цель**: **CyclicBarrier** используется для синхронизации набора потоков, тогда как **CountDownLatch** используется для ожидания завершения набора операций другими потоками.
3. **Счетчик**: **CyclicBarrier** требует, чтобы определенное количество потоков вызывало **await()**, тогда как **CountDownLatch** требует, чтобы определенное количество операций вызывало **countDown()**.

## Что такое потокобезопасность и как ее достичь?

**Java** поддерживает многопоточность из коробки. Это означает, что за счет одновременного запуска байт-кода в отдельных рабочих потоках **JVM** способна повысить производительность приложения.

**Хотя многопоточность** — мощная функция, она имеет свою цену. В многопоточных средах нам нужно писать реализации потокобезопасным способом. Это означает, что разные потоки могут обращаться к одним и тем же ресурсам без выявления ошибочного поведения или получения непредсказуемых результатов. Эта методология программирования известна как «безопасность потоков».

В этом уроке мы рассмотрим различные подходы к его достижению.

В большинстве случаев ошибки в многопоточных приложениях являются результатом неправильного разделения состояния между несколькими потоками.

Итак, первый подход, который мы рассмотрим, заключается в обеспечении потокобезопасности с помощью реализаций без сохранения состояния.

Проще говоря, класс без состояния не имеет полей и не ссылается на поля из других классов. Значение конкретной переменной существует только в локальной области видимости параметра, возвращаемого из метода или локальной переменной в методе.

Использование локальных переменных гарантирует, что наш класс потокобезопасен, потому что каждый поток имеет свою собственную копию переменной. Более того, использование локальных переменных делает наш метод идиоматичным и более простым в использовании.

**Давайте создадим простой пример, который демонстрирует потокобезопасность с использованием локальных переменных:**

```java
public class StateClass {
    public int factorial(int number) {
        int result = 1; // локальная переменная

        for (int i = 2; i <= number; i++) {
            result *= i;
        }

        return result;
    }
}
```

В приведенном выше примере метод **factorial** использует только локальные переменные — **result** и **i** — для выполнения вычислений. Они хранятся в стеке потока, а не в общей памяти. Следовательно, этот метод потокобезопасен, поскольку каждый поток будет иметь свою собственную копию этих переменных.

Аналогично, мы можем создавать локальные переменные, которые являются ссылками на другие объекты. Мы можем также создавать локальные переменные, которые являются экземплярами неизменяемых объектов.

**Например, рассмотрим класс **StateClass**, который имеет собственное состояние, но не использует его совместно с другими потоками:**

```java
public class StateClass {
    private final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

    @Override
    public void run() {
        numbers.forEach(System.out::println);
    }
}
```

**Между тем, другой может содержать массив строк:**

```java
public class ThreadB extends Thread {
    private final List<String> letters = Arrays.asList("a", "b", "c", "d", "e", "f");

    @Override
    public void run() {
        letters.forEach(System.out::println);
    }
}
```

В обеих реализациях у классов есть собственное состояние, но оно не используется совместно с другими потоками. Таким образом, классы потокобезопасны.

Точно так же мы можем создавать локальные поля потока, назначая экземпляры **ThreadLocal** полю.

**Рассмотрим следующий класс **StateHolder**:**

```java
public class StateHolder {
    private final String state;
}
```

**Мы можем легко сделать его локальной переменной потока:**

```java
public class ThreadState {
    public static final ThreadLocal<StateHolder> statePerThread = new ThreadLocal<StateHolder>() {
        @Override
        protected StateHolder initialValue() {
            return new StateHolder("active");
        }
    };

    public static StateHolder getState() {
        return statePerThread.get();
    }
}
```

Локальные поля потока очень похожи на обычные поля класса, за исключением того, что каждый поток, который обращается к ним через сеттер/геттер, получает независимо инициализированную копию поля, так что каждый поток имеет свое собственное состояние.

Мы можем легко создавать потокобезопасные коллекции, используя набор оболочек синхронизации, включенных в структуру коллекций.

**Мы можем использовать, например, одну из этих оболочек синхронизации для создания потокобезопасной коллекции:**

```java
Collection<Integer> syncCollection = Collections.synchronizedCollection(new ArrayList<>());
Thread thread1 = new Thread(() -> syncCollection.addAll(Arrays.asList(1, 2, 3, 4, 5, 6)));
Thread thread2 = new Thread(() -> syncCollection.addAll(Arrays.asList(7, 8, 9, 10, 11, 12)));

thread1.start();
thread2.start();
```

Не будем забывать, что синхронизированные коллекции используют внутреннюю блокировку в каждом методе (мы рассмотрим внутреннюю блокировку позже).

Это означает, что к методам может обращаться только один поток за раз, в то время как другие потоки будут заблокированы до тех пор, пока метод не будет разблокирован первым потоком.

Таким образом, синхронизация снижает производительность из-за лежащей в основе логики синхронизированного доступа.

В качестве альтернативы синхронизированным коллекциям мы можем использовать параллельные коллекции для создания потокобезопасных коллекций.

**Java** предоставляет пакет **java.util.concurrent**, который содержит несколько параллельных коллекций, таких как **ConcurrentHashMap**:**

```java
Map<String,String> concurrentMap = new ConcurrentHashMap<>();
concurrentMap.put("1", "one");
concurrentMap.put("2", "two");
concurrentMap.put("3", "three");
```

В отличие от своих синхронизированных аналогов, параллельные коллекции обеспечивают потокобезопасность за счет разделения своих данных на сегменты. Например, в **ConcurrentHashMap** несколько потоков могут получать блокировки на разных сегментах карты, поэтому несколько потоков могут получить доступ к карте одновременно.

Параллельные коллекции намного более эффективны, чем синхронизированные коллекции, благодаря неотъемлемым преимуществам доступа к параллельным потокам.

Стоит отметить, что синхронизированные и параллельные коллекции делают потокобезопасной только саму коллекцию, а не ее содержимое.

Также можно добиться потокобезопасности с помощью набора атомарных классов, предоставляемых **Java**, включая **AtomicInteger**, **AtomicLong**, **AtomicBoolean** и **AtomicReference**.

Атомарные классы позволяют нам выполнять атомарные операции, которые являются потокобезопасными, без использования синхронизации. Атомарная операция выполняется в одной операции на машинном уровне.

**Чтобы понять проблему, которую это решает, давайте посмотрим на следующий класс **Counter**:**

```java
public class Counter {
    private int counter = 0;

    public void incrementCounter() {
        counter += 1;
    }

    public int getCounter() {
        return counter;
    }
}
```

Предположим, что в состоянии гонки два потока одновременно обращаются к методу **incrementCounter()**.

Теоретически окончательное значение поля счетчика будет равно **2**. Но мы просто не можем быть уверены в результате, потому что потоки выполняют один и тот же блок кода в одно и то же время, а приращение не является атомарным.

**Давайте создадим потокобезопасную реализацию класса **Counter**, используя объект **AtomicInteger**:**

```java
public class AtomicCounter {
    private final AtomicInteger counter = new AtomicInteger();

    public void incrementCounter() {
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }
}
```

Это потокобезопасно, потому что в то время как приращение **++** требует более одной операции, **incrementAndGet** является атомарным.

Предыдущие подходы очень хороши для коллекций и примитивов, но иногда нам потребуется больший контроль.

Итак, еще один распространенный подход, который мы можем использовать для обеспечения потокобезопасности, — это реализация синхронизированных методов.

Проще говоря, только один поток может получить доступ к синхронизированному методу в каждый момент времени, при этом блокируя доступ к этому методу из других потоков. Другие потоки останутся заблокированными до тех пор, пока не завершится первый поток или пока метод не вызовет исключение.

**Мы можем создать потокобезопасную версию **incrementCounter()** другим способом, сделав его синхронизированным методом:**

```java
public synchronized void incrementCounter() {
    counter += 1;
}
```

Мы создали синхронизированный метод, поставив перед сигнатурой метода ключевое слово **synchronized**.

Так как один поток за раз может получить доступ к синхронизированному методу, один поток будет выполнять метод **incrementCounter()**, а другие, в свою очередь, будут делать то же самое. Никакого перекрывающегося выполнения не произойдет.

Синхронизированные методы основаны на использовании «внутренних блокировок» или «блокировок монитора». Внутренняя блокировка — это неявный внутренний объект, связанный с конкретным экземпляром класса.

В многопоточном контексте термин «монитор» — это просто ссылка на роль, которую блокировка выполняет для связанного объекта, поскольку она обеспечивает монопольный доступ к набору указанных методов или инструкций.

Когда поток вызывает синхронизированный метод, он получает встроенную блокировку. После того, как поток завершит выполнение метода, он снимает блокировку, что позволяет другим потокам установить блокировку и получить доступ к методу.

Мы можем реализовать синхронизацию в методах экземпляра, статических методах и операторах (синхронизированных операторах).

Иногда синхронизация всего метода может быть излишней, если нам просто нужно сделать сегмент метода потокобезопасным.

**Чтобы проиллюстрировать этот вариант использования, давайте реорганизуем метод **incrementCounter()**:**

```java
public void incrementCounter() {
    synchronized(this) {
        counter += 1;
    }
}
```

Пример тривиален, но он показывает, как создать синхронизированный оператор. Предполагая, что теперь метод выполняет несколько дополнительных операций, которые не требуют синхронизации, мы только синхронизировали соответствующий раздел, изменяющий состояние, заключив его в синхронизированный блок.

В отличие от синхронизированных методов, синхронизированные операторы должны указывать объект, обеспечивающий встроенную блокировку, обычно ссылку **this**.

Синхронизация стоит дорого, поэтому с этой опцией мы можем синхронизировать только соответствующие части метода.

Мы можем немного улучшить потокобезопасную реализацию класса **Counter**, используя другой объект в качестве блокировки монитора вместо этого.

**Это не только обеспечивает скоординированный доступ к общему ресурсу в многопоточной среде, но также использует внешний объект для принудительного монопольного доступа к ресурсу:**

```java
public class ObjectLockCounter {
    private int counter = 0;
    private final Object lock = new Object();

    public void incrementCounter() {
        synchronized(lock) {
            counter += 1;
        }
    }
}
```

Мы используем простой экземпляр **Object** для принудительного взаимного исключения. Эта реализация немного лучше, так как обеспечивает безопасность на уровне блокировки.

**Несмотря на то, что мы можем использовать любой объект **Java** в качестве встроенной блокировки, мы должны избегать использования строк для целей блокировки:**

```java
public class Class1 {
    private static final String LOCK = "Lock";
}

public class Class2 {
    private static final String LOCK = "Lock";
}
```

На первый взгляд кажется, что эти два класса используют в качестве блокировки два разных объекта. Однако из-за интернирования строк эти два значения **"`Lock`"** могут на самом деле ссылаться на один и тот же объект в пуле строк. То есть **Class1** и **Class2** используют одну и ту же блокировку!

Это, в свою очередь, может привести к неожиданному поведению в параллельных контекстах.

В дополнение к **String** мы должны избегать использования каких-либо кэшируемых или повторно используемых объектов в качестве встроенных блокировок. Например, метод **Integer.`valueOf()`** кэширует небольшие числа. Поэтому вызов **Integer.`valueOf`(1)** возвращает один и тот же объект даже в разных классах.

Синхронизированные методы и блоки удобны для решения проблем с видимостью переменных среди потоков. Даже в этом случае значения обычных полей класса могут кэшироваться процессором. Следовательно, последующие обновления определенного поля, даже если они синхронизированы, могут быть невидимы для других потоков.

**Чтобы предотвратить эту ситуацию, мы можем использовать поля класса **volatile**:**

```java
public class Counter {
    private volatile int counter;
}
```

С помощью ключевого слова **volatile** мы указываем **JVM** и компилятору хранить переменную счетчика в основной памяти. Таким образом, мы гарантируем, что каждый раз, когда **JVM** считывает значение переменной счетчика, она фактически будет считывать его из основной памяти, а не из кеша ЦП. Аналогично, каждый раз, когда **JVM** записывает в переменную счетчика, значение будет записываться в основную память.

Кроме того, использование **volatile**-переменной гарантирует, что все переменные, видимые данному потоку, также будут считаны из основной памяти.

**Рассмотрим следующий пример:**

```java
public class User {
    private String name;
    private volatile int age;
}
```

В этом случае каждый раз, когда **JVM** записывает энергозависимую переменную **age** в основную память, она также записывает энергонезависимую переменную **name** в основную память. Это гарантирует, что последние значения обеих переменных будут храниться в основной памяти, поэтому последующие обновления переменных будут автоматически видны другим потокам.

Точно так же, если поток считывает значение энергозависимой переменной, все переменные, видимые потоку, также будут считаны из основной памяти.

Эта расширенная гарантия, которую обеспечивают переменные **volatile**, известна как полная гарантия видимости **volatile**.

**Java** предоставляет улучшенный набор реализаций **Lock**, чье поведение немного сложнее, чем встроенные блокировки, рассмотренные выше.

Со встроенными блокировками модель получения блокировки довольно жесткая: один поток получает блокировку, затем выполняет метод или блок кода и, наконец, снимает блокировку, чтобы другие потоки могли получить ее и получить доступ к методу.

Нет базового механизма, который проверяет потоки в очереди и предоставляет приоритетный доступ к потокам с наибольшим временем ожидания.

**Экземпляры **ReentrantLock** позволяют нам делать именно это, предотвращая нехватку ресурсов для потоков в очереди:**

```java
public class ReentrantLockCounter {
    private int counter;
    private final ReentrantLock reLock = new ReentrantLock(true);

    public void incrementCounter() {
        reLock.lock();
        try {
            counter += 1;
        } finally {
            reLock.unlock();
        }
    }
}
```

Конструктор **ReentrantLock** принимает необязательный логический параметр справедливости. Если установлено значение **true** и несколько потоков пытаются получить блокировку, **JVM** отдаст приоритет самому длинному ожидающему потоку и предоставит доступ к блокировке.

Другой мощный механизм, который мы можем использовать для достижения потокобезопасности, — это использование реализаций **ReadWriteLock**.

Блокировка **ReadWriteLock** фактически использует пару связанных блокировок, одну для операций только для чтения, а другую для операций записи.

В результате может быть много потоков, читающих ресурс, пока нет ни одного потока, записывающего в него. Более того, поток, пишущий в ресурс, не позволит другим потокам прочитать его.

**Вот как мы можем использовать блокировку **ReadWriteLock**:**

```java
public class ReentrantReadWriteLockCounter {
    private int counter;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public void incrementCounter() {
        writeLock.lock();
        try {
            counter += 1;
        } finally {
            writeLock.unlock();
        }
    }

    public int getCounter() {
        readLock.lock();
        try {
            return counter;
        } finally {
            readLock.unlock();
        }
    }
}
```

## Как отложить выполнение кода?

Программы на **Java** довольно часто добавляют задержку или паузу в своей работе. Это может быть полезно для стимуляции задачи или для приостановки выполнения до завершения другой задачи.

В этом руководстве будут описаны два способа реализации задержек в **Java**.

Когда программа **Java** запускается, она порождает процесс, который выполняется на хост-компьютере. Этот процесс содержит как минимум один поток — основной поток, в котором выполняется программа. Кроме того, **Java** поддерживает многопоточность, что позволяет приложениям создавать новые потоки, работающие параллельно или асинхронно с основным потоком.

**Быстрый и грязный способ сделать паузу в **Java** — это указать текущему потоку спать в течение определённого периода времени. Это можно сделать с помощью **Thread.sleep(миллисекунды)**:**

```java
try {
    Thread.sleep(secondsToSleep * 1000);
} catch (InterruptedException ie) {
    Thread.currentThread().interrupt();
}
```

Хорошей практикой является заключение метода **sleep** в блок **try/catch** на случай, если другой поток прервет спящий поток. В этом случае мы перехватываем **InterruptedException** и явно прерываем текущий поток, чтобы его можно было перехватить и обработать позже. Это более важно в многопоточной программе, но все же является хорошей практикой в однопоточной программе на случай, если позже мы добавим другие потоки.

**Для лучшей читабельности мы можем использовать **TimeUnit.`XXX`.sleep(y)**, где **XXX** — это единица времени, в течение которой нужно спать (SECONDS, MINUTES и т. д.), а **y** — это количество единиц, в течение которых нужно спать. Это использует **Thread.sleep** за кулисами. Вот пример синтаксиса **TimeUnit**:**

```java
try {
    TimeUnit.SECONDS.sleep(secondsToSleep);
} catch (InterruptedException ie) {
    Thread.currentThread().interrupt();
}
```

**Однако использование этих методов на основе потоков имеет некоторые недостатки:**

1. **Время сна не совсем точное**, особенно при использовании меньших приращений времени, таких как миллисекунды и наносекунды.
2. **При использовании внутри циклов сон будет немного дрейфовать** между итерациями цикла из-за выполнения другого кода, поэтому время выполнения может стать неточным после многих итераций.

**Java** предоставляет интерфейс **ScheduledExecutorService**, который является более надежным и точным решением. Этот интерфейс может запланировать выполнение кода один раз после указанной задержки или через фиксированные интервалы времени.

**Чтобы запустить фрагмент кода один раз после задержки, мы можем использовать метод расписания:**

```java
ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
executorService.schedule(Classname::someTask, delayInSeconds, TimeUnit.SECONDS);
```

В части **Classname::someTask** мы указываем метод, который будет выполняться после задержки:

- **someTask** — это имя метода, который мы хотим выполнить.
- **Classname** — это имя класса, содержащего метод **someTask**.

**Чтобы запускать задачу через фиксированные промежутки времени, мы можем использовать метод **scheduleAtFixedRate**:**

```java
ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
executorService.scheduleAtFixedRate(Classname::someTask, 0, delayInSeconds, TimeUnit.SECONDS);
```

Это приведет к многократному вызову метода **someTask** с паузой **delayInSeconds** между каждым вызовом.

Помимо предоставления дополнительных параметров времени, метод **ScheduledExecutorService** дает более точные временные интервалы, поскольку он предотвращает проблемы с дрейфом.

## Как остановить выполнение через определенное время?

В этой статье мы узнаем, как мы можем завершить длительное выполнение через определенное время. Мы рассмотрим различные решения этой проблемы. Кроме того, мы рассмотрим некоторые их подводные камни.

Представьте, что мы обрабатываем кучу элементов в цикле, например, некоторые сведения об элементах продукта в приложении электронной коммерции, но, возможно, нет необходимости заполнять все элементы.

Фактически, мы хотели бы обрабатывать только до определенного времени, а после этого мы хотим остановить выполнение и показать все, что список обработал до этого времени.

**Давайте посмотрим на быстрый пример:**

```java
long start = System.currentTimeMillis();
long end = start + 30 * 1000;

while (System.currentTimeMillis() < end) {
    // обработка элементов
}
```

**Здесь цикл прервется, если время превысит ограничение в **30** секунд. В приведенном выше решении есть несколько заслуживающих внимания моментов:**

1. **Низкая точность**: цикл может работать дольше установленного ограничения по времени. Это будет зависеть от времени, которое может занять каждая итерация. Например, если каждая итерация может занять до **7** секунд, то общее время может увеличиться до **35** секунд, что примерно на **17%** больше, чем желаемое ограничение времени в **30** секунд.
2. **Блокировка**: такая обработка в основном потоке может быть не очень хорошей идеей, поскольку она будет блокировать ее на долгое время. Вместо этого эти операции должны быть отделены от основного потока.

Здесь мы будем использовать отдельный поток для выполнения длительных операций. Основной поток отправит сигнал прерывания рабочему потоку по тайм-ауту.

Если рабочий поток все еще жив, он поймает сигнал и остановит свое выполнение. Если рабочий процесс завершится до истечения времени ожидания, это не повлияет на рабочий поток.

**Давайте посмотрим на рабочий поток:**

```java
class LongRunningTask implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < Long.MAX_VALUE; i++) {
            if (Thread.interrupted()) {
                return;
            }
            // выполнение работы
        }
    }
}
```

Здесь цикл **for** через **Long.MAX_VALUE** имитирует длительную операцию. Вместо этого может быть любая другая операция. Важно проверять флаг прерывания, потому что не все операции прерываемы. Поэтому в таких случаях мы должны вручную проверять флаг.

Кроме того, мы должны проверять этот флаг на каждой итерации, чтобы убедиться, что поток прекращает выполнение самого себя с задержкой не более одной итерации.

Далее мы рассмотрим три различных механизма отправки сигнала прерывания.

**В качестве альтернативы мы можем создать **TimerTask** для прерывания рабочего потока по истечении времени ожидания:**

```java
class TimeOutTask extends TimerTask {
    private Thread thread;
    private Timer timer;

    public TimeOutTask(Thread thread, Timer timer) {
        this.thread = thread;
        this.timer = timer;
    }

    @Override
    public void run() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            timer.cancel();
        }
    }
}
```

**Здесь мы определили **TimerTask**, который принимает рабочий поток во время его создания. Он прервет рабочий поток при вызове его метода **run**. Таймер запустит **TimerTask** после трехсекундной задержки:**

```java
Thread thread = new Thread(new LongRunningTask());
thread.start();

Timer timer = new Timer();
TimeOutTask timeOutTask = new TimeOutTask(thread, timer);
timer.schedule(timeOutTask, 3000);
```

**Мы также можем использовать метод **get** из **Future** вместо использования **Timer**:**

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
Future future = executor.submit(new LongRunningTask());

try {
    future.get(7, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    future.cancel(true);
} catch (Exception e) {
} finally {
    executor.shutdownNow();
}
```

Здесь мы использовали **ExecutorService** для отправки рабочего потока, который возвращает экземпляр **Future**, чей метод **get** заблокирует основной поток до указанного времени. Это вызовет исключение **TimeoutException** после указанного тайм-аута. В блоке **catch** мы прерываем рабочий поток, вызывая метод отмены для объекта **Future**.

Основное преимущество этого подхода по сравнению с предыдущим заключается в том, что он использует пул для управления потоком, в то время как **Timer** использует только один поток (без пула).

**Мы также можем использовать **ScheduledExecutorService** для прерывания задачи. Этот класс является расширением **ExecutorService** и предоставляет ту же функциональность с добавлением нескольких методов, которые имеют дело с планированием выполнения. Это может выполнить данную задачу после определенной задержки в заданных единицах времени:**

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
Future future = executor.submit(new LongRunningTask());
Runnable cancelTask = () -> future.cancel(true);

executor.schedule(cancelTask, 3000, TimeUnit.MILLISECONDS);
executor.shutdown();
```

Здесь мы создали запланированный пул потоков размером два с помощью метода **newScheduledThreadPool**. Метод расписания **ScheduledExecutorService#** принимает **Runnable**, значение задержки и единицу измерения задержки.

Приведенная выше программа планирует выполнение задачи через три секунды с момента отправки. Эта задача отменит исходную длительную задачу.

Обратите внимание, что, в отличие от предыдущего подхода, мы не блокируем основной поток, вызывая метод **Future#get**. Таким образом, это наиболее предпочтительный подход среди всех вышеупомянутых подходов.

### Важные замечания

Нет никакой гарантии, что выполнение будет остановлено через определенное время. Основная причина в том, что не все методы блокировки прерываемы. На самом деле существует лишь несколько четко определенных прерываемых методов. Итак, если поток прерван и установлен флаг, ничего больше не произойдет, пока он не достигнет одного из этих прерываемых методов.

Например, методы чтения и записи являются прерываемыми, только если они вызываются для потоков, созданных с помощью **InterruptibleChannel**. **BufferedReader** не является **InterruptibleChannel**. Таким образом, если поток использует его для чтения файла, вызов **interrupt()** в этом потоке, заблокированном в методе чтения, не имеет никакого эффекта.

Однако мы можем явно проверять наличие флага прерывания после каждого чтения в цикле. Это даст разумную гарантию остановки потока с некоторой задержкой. Но это не гарантирует остановки потока по истечении заданного времени, потому что мы не знаем, сколько времени может занять операция чтения.

С другой стороны, метод ожидания класса **Object** можно прерывать. Таким образом, поток, заблокированный в методе ожидания, немедленно вызовет **InterruptedException** после установки флага прерывания.

Мы можем определить методы блокировки, найдя в сигнатурах их методов исключение **InterruptedException**.

**Один важный совет** — избегать использования устаревшего метода **Thread.stop()**. Остановка потока заставляет его разблокировать все мониторы, которые он заблокировал. Это происходит из-за исключения **ThreadDeath**, которое распространяется вверх по стеку.

Если какой-либо из объектов, ранее защищенных этими мониторами, находился в несогласованном состоянии, несогласованные объекты становятся видимыми для других потоков. Это может привести к произвольному поведению, которое очень трудно обнаружить и обосновать.

### Дизайн с учетом прерывания

В предыдущем разделе мы подчеркнули важность прерываемых методов для скорейшей остановки выполнения. Поэтому наш код должен учитывать это ожидание с точки зрения дизайна.

Представьте, что нам нужно выполнить длительную задачу, и нам нужно убедиться, что она не займет больше времени, чем указано. Кроме того, предположим, что задачу можно разделить на отдельные шаги.

**Создадим класс для шагов задачи:**

```java
class Step {
    private static int MAX = Integer.MAX_VALUE/2;
    int number;

    public Step(int number) {
        this.number = number;
    }

    public void perform() throws InterruptedException {
        Random rnd = new Random();
        int target = rnd.nextInt(MAX);

        while (rnd.nextInt(MAX) != target) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }
}
```

Здесь метод **Step#perform** пытается найти целевое случайное целое число, запрашивая флаг на каждой итерации. Метод выдает **InterruptedException** при активации флага.

**Теперь давайте определим задачу, которая будет выполнять все шаги:**

```java
public class SteppedTask implements Runnable {
    private List<Step> steps;

    public SteppedTask(List<Step> steps) {
        this.steps = steps;
    }

    @Override
    public void run() {
        for (Step step : steps) {
            try {
                step.perform();
            } catch (InterruptedException e) {
                // обработать прерывание
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
```

В этом примере мы можем прервать задачу на любом шаге, что делает нашу реализацию более отзывчивой к прерываниям и позволяет нам завершить выполнение задачи в течение заданного периода времени.

## Лучшие практики

- **ExecutorService:** предпочитать пулы с ограниченным числом потоков (`Executors.newFixedThreadPool`, custom); не использовать неограниченные `newCachedThreadPool` в **production** без контроля; всегда вызывать **shutdown()** / **awaitTermination()**.
- **CompletableFuture:** использовать для цепочек асинхронных операций; обрабатывать исключения через **handle** / **exceptionally**; не блокировать в общем пуле — выделять отдельный пул для блокирующих задач.
- **Lock/`Condition`:** предпочитать над `synchronized` при необходимости таймаутов, **tryLock**, нескольких условий; всегда освобождать **lock** в **finally**.
- **CountDownLatch vs `CyclicBarrier`:** однократный барьер (ожидание N событий) — **CountDownLatch**; многократная синхронизация N потоков — **CyclicBarrier**.
- **Fork/`Join`:** применять для рекурсивного разбиения задач с чётким разделением; не дробить слишком мелко; избегать блокирующих операций в задачах.
- **Потокобезопасность:** минимизировать общее состояние; использовать потокобезопасные коллекции (`ConcurrentHashMap`, блокирующие очереди); документировать потокобезопасность **API**.
- **Прерывание:** проверять `Thread.interrupted()` в длительных циклах; восстанавливать флаг прерывания в **catch**: `Thread.currentThread().interrupt()`; не глотать **InterruptedException**.

## См. также

- [Java Annotations и Reflection](java-annotations-reflection.md)
- [Java: основы](java-basics.md)
- [Java Collections: конвертирование](java-collections-converting.md)
- [Java Collections: List](java-collections-list.md)
- [Java Collections: Map](java-collections-map.md)
