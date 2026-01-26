# Delegate (Делегирование)

Паттерн Delegate (Делегирование) позволяет объекту передать выполнение определенных операций другому объекту. Во многих случаях делегирование предпочтительнее наследования. Kotlin отлично поддерживает это на уровне языка с помощью ключевого слова `by`.

**Дата последнего обновления:** 2025-01-15

## Содержание

- [Полезные ссылки](#�-олезн�-е-�-�-�-лки)
  - [Официальная документация](#�-�-и�-иал�-ная-док�-мен�-а�-ия)
  - [Baeldung](#baeldung)
  - [См. также](#�-м-�-акже)
- [Оглавление](#�-главление)
- [Kotlin Implementation](#kotlin-implementation)



## Полезные ссылки

### Официальная документация
- [Kotlin Delegation](https://kotlinlang.org/docs/delegation.html)

### Baeldung
- [Kotlin Delegation](https://www.baeldung.com/kotlin-delegation)
- [Java Delegation Pattern](https://www.baeldung.com/java-delegation-pattern)

### См. также
- [Decorator (Декоратор)](../structural/02-decorator-dekorator.md)
- [Proxy (Прокси)](../structural/01-proxy-proksi.md)

## Оглавление

- [Введение](#введение)
- [Kotlin Implementation](#kotlin-implementation)
- [Java Implementation](#java-implementation)
- [Варианты использования](#варианты-использования)
- [Важные замечания](#важные-замечания)

## Kotlin Implementation

В этом уроке мы поговорим о встроенной в Kotlin поддержке шаблона делегирования и увидим ее в действии.

Во-первых, давайте предположим, что у нас есть пример кода со структурой ниже в сторонней библиотеке:

```kotlin
interface Producer {
    fun produce(): String
}

class ProducerImpl: Producer {
    override fun produce() = "ProducerImpl"
}
```

Далее украсим имеющуюся реализацию с помощью ключевого слова `by` и добавим дополнительную необходимую обработку:

```kotlin
class EnhancedProducer(private val delegate: Producer): Producer by delegate {
    override fun produce() = "${delegate.produce()} and EnhancedProducer"
}
```

Итак, в этом примере мы указали, что класс EnhancedProducer будет инкапсулировать объект делегата типа Producer. Кроме того, он может использовать функциональные возможности реализации Producer.

Наконец, давайте проверим, что он работает так, как ожидалось:

```kotlin
val producer = EnhancedProducer(ProducerImpl())
assertThat(producer.produce()).isEqualTo("ProducerImpl and EnhancedProducer")
```

## Варианты использования

Теперь давайте рассмотрим два распространенных варианта использования шаблона делегирования.

Во-первых, мы можем использовать шаблон делегирования для реализации нескольких интерфейсов с использованием существующих реализаций:

```kotlin
class CompositeService: UserService by UserServiceImpl(), MessageService by MessageServiceImpl()
```

Во-вторых, мы можем использовать делегирование для улучшения существующей реализации.

Последнее — это то, что мы сделали в предыдущем разделе. Но более реальный пример, такой как приведенный ниже, особенно полезен, когда мы не можем изменить существующую реализацию — например, код сторонней библиотеки:

```kotlin
class SynchronizedProducer(private val delegate: Producer): Producer by delegate {
    private val lock = ReentrantLock()

    override fun produce(): String {
        lock.withLock {
            return delegate.produce()
        }
    }
}
```

## Важные замечания

Теперь нам нужно всегда помнить, что делегат ничего не знает о декораторе. Таким образом, мы не должны пробовать с ними подход, подобный методу шаблона GoF.

Рассмотрим пример:

```kotlin
interface Service {
    val seed: Int
    fun serve(action: (Int) -> Unit)
}

class ServiceImpl: Service {
    override val seed = 1
    override fun serve(action: (Int) -> Unit) {
        action(seed)
    }
}

class ServiceDecorator: Service by ServiceImpl() {
    override val seed = 2
}
```

Здесь делегат (ServiceImpl) использует свойство, определенное в общем интерфейсе, и мы переопределяем его в декораторе (ServiceDecorator). Однако это не влияет на обработку делегата:

```kotlin
val service = ServiceDecorator()
service.serve {
    assertThat(it).isEqualTo(1)
}
```

Наконец, важно отметить, что в Kotlin мы можем делегировать не только интерфейсы, но и отдельные свойства.

## Java Implementation

В Java паттерн Delegate может быть реализован через композицию:

```java
public interface Producer {
    String produce();
}

public class ProducerImpl implements Producer {
    @Override
    public String produce() {
        return "ProducerImpl";
    }
}

public class EnhancedProducer implements Producer {
    private final Producer delegate;

    public EnhancedProducer(Producer delegate) {
        this.delegate = delegate;
    }

    @Override
    public String produce() {
        return delegate.produce() + " and EnhancedProducer";
    }
}
```

Использование:

```java
Producer producer = new EnhancedProducer(new ProducerImpl());
String result = producer.produce();
// result = "ProducerImpl and EnhancedProducer"
```

### Реализация нескольких интерфейсов

```java
public interface UserService {
    void createUser(String name);
}

public interface MessageService {
    void sendMessage(String message);
}

public class UserServiceImpl implements UserService {
    @Override
    public void createUser(String name) {
        System.out.println("Creating user: " + name);
    }
}

public class MessageServiceImpl implements MessageService {
    @Override
    public void sendMessage(String message) {
        System.out.println("Sending message: " + message);
    }
}

public class CompositeService implements UserService, MessageService {
    private final UserService userService;
    private final MessageService messageService;

    public CompositeService(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public void createUser(String name) {
        userService.createUser(name);
    }

    @Override
    public void sendMessage(String message) {
        messageService.sendMessage(message);
    }
}
```

Использование:

```java
CompositeService service = new CompositeService(
    new UserServiceImpl(),
    new MessageServiceImpl()
);
service.createUser("John");
service.sendMessage("Hello");
```
