---
title: "Вопросы на собеседовании: Java Reflection"
description: "Java Reflection API: Class, Method, Field, Constructor, InvocationHandler, динамические прокси, производительность, применение в фреймворках"
tags:
  - interview
  - java
  - java-reflection-interview
aliases:
  - "Java Reflection interview"
  - "Java Reflection собеседование"
  - "Reflection API вопросы"
  - "Java reflection questions"
difficulty: "intermediate"
updated: "2026-04-20"
---
# Вопросы на собеседовании: `Java Reflection`

`Java Reflection API` позволяет исследовать и изменять структуру классов, методов, полей и конструкторов во время выполнения. Основа Spring, Hibernate, Jackson, JUnit — всё это использует рефлексию. Часто задаётся в контексте "как фреймворки работают под капотом".

## Полезные ссылки

### Официальная документация

- [Java Reflection Tutorial (Oracle)](https://docs.oracle.com/javase/tutorial/reflect/) — официальный гайд
- [java.lang.reflect JavaDoc](https://docs.oracle.com/en/java/docs/api/java.base/java/lang/reflect/package-summary.html)

### Baeldung tutorials

- [Guide to Java Reflection](https://www.baeldung.com/java-reflection) — основы
- [Call Methods at Runtime Using Reflection](https://www.baeldung.com/java-method-reflection)
- [Retrieve Fields via Reflection](https://www.baeldung.com/java-reflection-class-fields)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Reflection в Java и для чего используется?](#q1-что-такое-reflection-в-java-и-для-чего-используется)
- [Q2. Как получить объект `Class<?>`?](#q2-как-получить-объект-class)
- [Q3. Что можно узнать через объект `Class<?>`?](#q3-что-можно-узнать-через-объект-class)

**Работа с полями**
- [Q4. (!) Как получить и изменить значение поля через рефлексию?](#q4-как-получить-и-изменить-значение-поля-через-рефлексию)
- [Q5. В чём разница между `getField()` и `getDeclaredField()`?](#q5-в-чём-разница-между-getfield-и-getdeclaredfield)

**Работа с методами**
- [Q6. (!) Как вызвать метод через рефлексию?](#q6-как-вызвать-метод-через-рефлексию)
- [Q7. Как вызвать статический метод через рефлексию?](#q7-как-вызвать-статический-метод-через-рефлексию)
- [Q8. Как вызвать приватный метод через рефлексию?](#q8-как-вызвать-приватный-метод-через-рефлексию)

**Конструкторы и создание объектов**
- [Q9. Как создать объект через рефлексию?](#q9-как-создать-объект-через-рефлексию)

**Аннотации и generics**
- [Q10. Как получить аннотации через рефлексию?](#q10-как-получить-аннотации-через-рефлексию)
- [Q11. Как получить generic-тип параметра через рефлексию?](#q11-как-получить-generic-тип-параметра-через-рефлексию)

**Динамические прокси**
- [Q12. (!) Что такое `java.lang.reflect.Proxy` и как работает?](#q12-что-такое-javalanglinreflectproxy-и-как-работает)

**Производительность и безопасность**
- [Q13. (!) Каковы недостатки и ограничения рефлексии?](#q13-каковы-недостатки-и-ограничения-рефлексии)
- [Q14. Что такое `setAccessible(true)` и какие есть риски?](#q14-что-такое-setaccessibletrue-и-какие-есть-риски)
- [Q15. Как рефлексия используется в Java-фреймворках?](#q15-как-рефлексия-используется-в-java-фреймворках)

**Method Handles**
- [Q16. Что такое `MethodHandle` и чем он лучше рефлексии?](#q16-что-такое-methodhandle-и-чем-он-лучше-рефлексии)

---

## Q1. Что такое Reflection в Java и для чего используется?

**Reflection** — API для исследования и изменения структуры Java-программы **во время выполнения (runtime)**: классов, методов, полей, конструкторов, аннотаций.

```
Компиляция → .class файл → ClassLoader → Class<?> объект
                                                 ↓
                              Reflection API: изучаем/изменяем структуру
```

**Где используется:**
- **Spring**: DI через конструкторы/поля, AOP proxy, `@Autowired`, `@Value`
- **Hibernate**: маппинг полей Entity → колонки БД
- **Jackson**: чтение/запись полей JSON-объектов
- **JUnit**: обнаружение `@Test`-методов и их вызов
- **Lombok**: (annotation processor, но анализирует через APT — близко к рефлексии)
- **Serialization**: `ObjectInputStream` обращается к полям через рефлексию

**Основные классы:**
- `java.lang.Class<T>` — представляет тип
- `java.lang.reflect.Field` — поле класса
- `java.lang.reflect.Method` — метод
- `java.lang.reflect.Constructor<T>` — конструктор
- `java.lang.reflect.Parameter` — параметр метода/конструктора

## Q2. Как получить объект `Class<?>`?

```java
// 1. Через .class (compile-time)
Class<String> c1 = String.class;
Class<int[]> c2 = int[].class;

// 2. Через getClass() у экземпляра
String str = "hello";
Class<?> c3 = str.getClass();

// 3. Через Class.forName() (runtime, по имени)
Class<?> c4 = Class.forName("java.util.ArrayList");
Class<?> c5 = Class.forName("com.example.MyClass");

// 4. Через ClassLoader
Class<?> c6 = Thread.currentThread()
    .getContextClassLoader()
    .loadClass("com.example.MyClass");

// 5. Через обёртку примитива
Class<?> c7 = Integer.TYPE;   // int (примитив)
Class<?> c8 = Integer.class;  // java.lang.Integer (обёртка)
```

`Class.forName()` — может выбросить `ClassNotFoundException` и инициализирует класс (запускает `<clinit>`). Используется в JDBC: `Class.forName("org.postgresql.Driver")`.

## Q3. Что можно узнать через объект `Class<?>`?

```java
Class<?> cls = Order.class;

// Основная информация
cls.getName();                // "com.example.Order"
cls.getSimpleName();          // "Order"
cls.getCanonicalName();       // "com.example.Order"
cls.getPackageName();         // "com.example"
cls.getSuperclass();          // родительский класс
cls.getInterfaces();          // реализованные интерфейсы
cls.isInterface();            // false
cls.isEnum();                 // false
cls.isRecord();               // false (Java 16+)
cls.isAnnotation();           // false

// Модификаторы
int mod = cls.getModifiers();
Modifier.isPublic(mod);       // true
Modifier.isAbstract(mod);     // false

// Члены класса
cls.getFields();              // public поля (включая унаследованные)
cls.getDeclaredFields();      // все поля только этого класса
cls.getMethods();             // public методы (+ унаследованные)
cls.getDeclaredMethods();     // все методы только этого класса
cls.getConstructors();        // public конструкторы
cls.getDeclaredConstructors(); // все конструкторы

// Аннотации
cls.getAnnotation(Entity.class);
cls.getAnnotations();
cls.getDeclaredAnnotations();
```

## Q4. Как получить и изменить значение поля через рефлексию?

```java
public class User {
    private String name;
    private final String id = UUID.randomUUID().toString();
    public int age;
}

User user = new User();
user.age = 30;

// Публичное поле — напрямую
Field ageField = User.class.getField("age");
int age = (int) ageField.get(user);           // 30
ageField.set(user, 31);

// Приватное поле — нужен setAccessible(true)
Field nameField = User.class.getDeclaredField("name");
nameField.setAccessible(true);                // открываем доступ
nameField.set(user, "Alice");
String name = (String) nameField.get(user);   // "Alice"

// final поле (до Java 17)
Field idField = User.class.getDeclaredField("id");
idField.setAccessible(true);
idField.set(user, "new-id");  // ⚠️ В Java 17+ может выбросить InaccessibleObjectException
```

**Важно (Java 17+ strong encapsulation):** в модульных приложениях `setAccessible(true)` для полей из закрытых модулей выбросит `InaccessibleObjectException`. Для открытия нужно `--add-opens` или `open module`.

## Q5. В чём разница между `getField()` и `getDeclaredField()`?

| Метод | Видит | Возвращает |
|---|---|---|
| `getField(name)` | public поля + унаследованные public | `Field` |
| `getDeclaredField(name)` | ВСЕ поля (private/protected/public) только этого класса | `Field` |
| `getFields()` | все public поля + унаследованные | `Field[]` |
| `getDeclaredFields()` | все поля только этого класса | `Field[]` |

То же правило для `getMethod/getDeclaredMethod` и `getConstructor/getDeclaredConstructor`.

```java
class Parent {
    public String parentField;
    private String parentPrivate;
}

class Child extends Parent {
    private String childField;
}

Class<?> cls = Child.class;

cls.getField("parentField");          // OK — public из Parent
cls.getDeclaredField("childField");   // OK — private в Child
cls.getField("childField");           // NoSuchFieldException — не public
cls.getDeclaredField("parentPrivate"); // NoSuchFieldException — не в Child
```

Для доступа к приватным полям РОДИТЕЛЯ: итерировать `getSuperclass()`.

## Q6. Как вызвать метод через рефлексию?

```java
public class Calculator {
    public int add(int a, int b) { return a + b; }
    private String format(double value) { return String.format("%.2f", value); }
}

Calculator calc = new Calculator();

// Публичный метод
Method addMethod = Calculator.class.getMethod("add", int.class, int.class);
int result = (int) addMethod.invoke(calc, 5, 3);   // 8

// Приватный метод
Method formatMethod = Calculator.class.getDeclaredMethod("format", double.class);
formatMethod.setAccessible(true);
String formatted = (String) formatMethod.invoke(calc, 3.14);  // "3.14"
```

`invoke()` бросает:
- `IllegalAccessException` — нет доступа (нужен `setAccessible`)
- `InvocationTargetException` — исключение из самого метода (обёрнуто)
- `IllegalArgumentException` — неверные аргументы

```java
try {
    Object result = method.invoke(target, args);
} catch (InvocationTargetException e) {
    throw e.getCause();  // достать оригинальное исключение
}
```

## Q7. Как вызвать статический метод через рефлексию?

```java
public class MathUtils {
    public static int square(int n) { return n * n; }
}

Method method = MathUtils.class.getMethod("square", int.class);
int result = (int) method.invoke(null, 5);  // null вместо instance → статический метод
// result = 25
```

`invoke(null, args)` — первый аргумент `null` означает "нет instance" = статический вызов.

## Q8. Как вызвать приватный метод через рефлексию?

```java
public class SecretService {
    private String generateToken(String userId) {
        return UUID.randomUUID() + ":" + userId;
    }
}

SecretService service = new SecretService();

Method method = SecretService.class.getDeclaredMethod("generateToken", String.class);
method.setAccessible(true);  // обязательно для private
String token = (String) method.invoke(service, "user-123");
```

**Применение в тестировании:** доступ к private-методам через рефлексию в тестах — сигнал проблем с дизайном. Лучше тестировать через public API. Но иногда оправдан (legacy code, сложный рефакторинг).

В Spring тестах — `ReflectionTestUtils.invokeMethod(bean, "privateMethod", args)`.

## Q9. Как создать объект через рефлексию?

```java
// Через Class.newInstance() — устаревший, бросает checked exceptions
// Object obj = MyClass.class.newInstance(); // deprecated since Java 9

// Через Constructor (рекомендован)
Class<?> cls = Order.class;

// Конструктор без аргументов
Constructor<?> noArgCtor = cls.getDeclaredConstructor();
noArgCtor.setAccessible(true);
Order order1 = (Order) noArgCtor.newInstance();

// Конструктор с аргументами
Constructor<?> ctor = cls.getDeclaredConstructor(String.class, BigDecimal.class);
Order order2 = (Order) ctor.newInstance("ref-001", new BigDecimal("99.99"));
```

`getDeclaredConstructor()` — найти по типам параметров. `Constructor.newInstance()` бросает `InstantiationException`, `IllegalAccessException`, `InvocationTargetException`.

**Применение:** Hibernate создаёт Entity через no-arg constructor. Jackson создаёт объекты через рефлексию при десериализации.

## Q10. Как получить аннотации через рефлексию?

```java
@Table(name = "orders")
@Entity
public class Order {
    @Column(name = "order_ref")
    @NotNull
    private String ref;

    @Transactional
    public void process() { }
}

Class<?> cls = Order.class;

// Аннотация на классе
Table table = cls.getAnnotation(Table.class);
String tableName = table.name();  // "orders"

// Все аннотации на классе
Annotation[] annotations = cls.getAnnotations();

// Аннотация на поле
Field ref = cls.getDeclaredField("ref");
Column column = ref.getAnnotation(Column.class);
String colName = column.name();  // "order_ref"

// Аннотация на методе
Method processMethod = cls.getDeclaredMethod("process");
Transactional tx = processMethod.getAnnotation(Transactional.class);

// Аннотации параметров метода
Method m = SomeClass.class.getMethod("doSomething", String.class);
Parameter[] params = m.getParameters();
for (Parameter p : params) {
    if (p.isAnnotationPresent(NotNull.class)) {
        // ...
    }
}
```

**Важно:** аннотация должна иметь `@Retention(RetentionPolicy.RUNTIME)` — иначе она недоступна через рефлексию (теряется после компиляции).

## Q11. Как получить generic-тип параметра через рефлексию?

Из-за `type erasure` generic-параметры теряются в runtime. Но у полей, методов и суперклассов параметры сохраняются в метаданных:

```java
public class UserRepository {
    private List<String> names;
    private Map<String, List<Integer>> cache;
}

// Получить generic-тип поля
Field namesField = UserRepository.class.getDeclaredField("names");
ParameterizedType type = (ParameterizedType) namesField.getGenericType();
Type elementType = type.getActualTypeArguments()[0];
// elementType = class java.lang.String

// Вложенный generic
Field cacheField = UserRepository.class.getDeclaredField("cache");
ParameterizedType cacheType = (ParameterizedType) cacheField.getGenericType();
Type valueType = cacheType.getActualTypeArguments()[1];
// valueType = java.util.List<java.lang.Integer>

// Через суперкласс (TypeReference trick)
abstract class TypeReference<T> {
    Type getType() { return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]; }
}
// Jackson использует именно это для new TypeReference<List<User>>() {}
```

## Q12. Что такое `java.lang.reflect.Proxy` и как работает?

`java.lang.reflect.Proxy` создаёт **динамический прокси-объект**, реализующий заданные интерфейсы:

```java
public interface OrderService {
    Order createOrder(OrderRequest req);
    void cancelOrder(Long id);
}

// InvocationHandler — обрабатывает все вызовы методов прокси
InvocationHandler handler = (proxy, method, args) -> {
    System.out.println("Before: " + method.getName());
    // вызов реального объекта (если нужен):
    Object result = method.invoke(realOrderService, args);
    System.out.println("After: " + method.getName());
    return result;
};

// Создание прокси
OrderService proxy = (OrderService) Proxy.newProxyInstance(
    OrderService.class.getClassLoader(),
    new Class<?>[] { OrderService.class },
    handler
);

proxy.createOrder(request);  // → вызовет handler.invoke()
```

**JDK Proxy работает только с интерфейсами**. Для классов без интерфейсов Spring использует CGLIB — generates bytecode subclass.

Spring AOP (при JDK Dynamic Proxy): `Proxy.newProxyInstance` + `InvocationHandler` с chain of `MethodInterceptor`-ов.

## Q13. Каковы недостатки и ограничения рефлексии?

**Производительность:**
- `method.invoke()` в 20-100x медленнее прямого вызова (lookup + boxing args)
- `Class.forName()` требует поиска в classpath
- Нет JIT-оптимизаций для reflective вызовов (нельзя инлайнить)

```java
// Benchmark: прямой вызов vs рефлексия (примерно)
// Direct:      ~1 ns/op
// Reflection:  ~20-100 ns/op (с setAccessible кеш — до 3-5ns)
```

**Безопасность:**
- `setAccessible(true)` нарушает инкапсуляцию
- Java 17+ Module System ограничивает доступ к закрытым пакетам
- `SecurityManager` (устарел в Java 17) мог блокировать

**Сложность и хрупкость:**
- Нет compile-time проверок — ошибки в рантайме
- Переименование поля/метода ломает reflective код
- Труднее читать и отлаживать

**GraalVM native image:**
- Статический анализ не знает о reflective вызовах
- Нужны `reflect-config.json` конфигурации

**Когда приемлемо:**
- В фреймворках (Spring, Hibernate) — один раз при инициализации
- В тестах (ReflectionTestUtils)
- Никогда в hot path бизнес-логики

## Q14. Что такое `setAccessible(true)` и какие есть риски?

`setAccessible(true)` — метод `AccessibleObject` (родитель `Field`, `Method`, `Constructor`), отключающий проверку прав доступа JVM:

```java
field.setAccessible(true);   // теперь можно читать/писать private поле
method.setAccessible(true);  // можно вызывать private/protected методы
```

**Риски:**
1. **Нарушение инкапсуляции** — код может зависеть от деталей реализации
2. **Security** — злоумышленник может изменить private поля (например, в `final`)
3. **Java 9+ modules** — `setAccessible(true)` выбросит `InaccessibleObjectException` для закрытых пакетов без явного `--add-opens`

```bash
# Открыть пакет для рефлексии (JVM флаг):
--add-opens java.base/java.lang=ALL-UNNAMED
```

**Java 9+ поведение:** если модуль не экспортирует пакет, `setAccessible(true)` — `InaccessibleObjectException`. Spring Boot устанавливает нужные `--add-opens` флаги автоматически.

**Кеширование:** `field.setAccessible(true)` дорого при каждом вызове. Нужно кешировать `Field`/`Method`/`Constructor` объекты.

## Q15. Как рефлексия используется в Java-фреймворках?

| Фреймворк | Применение рефлексии |
|---|---|
| **Spring** | `@Autowired` — inject через field/setter; AOP proxy; `@Value` injection; `@EventListener` обнаружение |
| **Hibernate** | Маппинг `@Entity` полей → колонки; создание объектов через no-arg constructor |
| **Jackson** | Сериализация/десериализация через field доступ или getter/setter обнаружение |
| **JUnit** | Обнаружение `@Test`, `@BeforeEach` методов; запуск тестов |
| **Mockito** | Создание mock-объектов (CGLIB или ByteBuddy); spy |
| **Lombok** | Annotation processor (APT) — compile-time, не рефлексия |
| **MapStruct** | Annotation processor — compile-time |

**Spring и рефлексия:**

```java
// Spring делает что-то подобное для @Autowired:
for (Field field : bean.getClass().getDeclaredFields()) {
    if (field.isAnnotationPresent(Autowired.class)) {
        field.setAccessible(true);
        Object dependency = context.getBean(field.getType());
        field.set(bean, dependency);
    }
}
```

**Кеширование в Spring:** `ReflectionUtils` кешируют Method/Field объекты через `ConcurrentHashMap`. Без кеширования рефлексия была бы недопустимо медленной.

## Q16. Что такое `MethodHandle` и чем он лучше рефлексии?

`MethodHandle` (Java 7, `java.lang.invoke`) — типизированный, производительный механизм для вызова методов:

```java
import java.lang.invoke.*;

// Lookup — точка доступа к MethodHandle
MethodHandles.Lookup lookup = MethodHandles.lookup();

// Получить handle для метода
MethodHandle mh = lookup.findVirtual(
    String.class,
    "substring",
    MethodType.methodType(String.class, int.class, int.class)
);

// Вызов
String result = (String) mh.invoke("Hello, World!", 0, 5);  // "Hello"

// Статический метод
MethodHandle staticMh = lookup.findStatic(
    Math.class,
    "max",
    MethodType.methodType(int.class, int.class, int.class)
);
int max = (int) staticMh.invoke(3, 7);  // 7
```

**Преимущества перед рефлексией:**

| Критерий | Reflection | MethodHandle |
|---|---|---|
| JIT-оптимизация | Нет (или ограниченная) | Да — может быть инлайнен |
| Type safety | Объект `Object` | Типизированный `MethodType` |
| Производительность | ~20-100 ns | Близка к прямому вызову после разогрева |
| Module access | `setAccessible` | `Lookup` с правами доступа |

**VarHandle** (Java 9) — аналог для полей с поддержкой memory ordering (volatile, acquire, release).

`MethodHandle` — основа `invokedynamic` инструкции JVM, которую использует лямбда-компилятор Java.

---

## See also

- [[java-annotations-interview|Java Annotations]] — @Retention(RUNTIME) как условие доступа через рефлексию
- [[java-core-interview|Java Core]] — ClassLoader, class loading mechanism
- [[java-initialization-interview|Java Initialization]] — `<clinit>`, `Class.forName()` инициализирует класс
- [[java-generics-interview|Java Generics]] — type erasure и как его обойти через `ParameterizedType`
- [[java-concurrency-interview|Java Concurrency]] — MethodHandle как thread-safe механизм вызова
- [[spring-aop-interview|Spring AOP]] — JDK Dynamic Proxy через `java.lang.reflect.Proxy`
- [[spring-framework-interview|Spring Framework]] — DI через рефлексию, BeanFactory, @Autowired
- [[spring-data-jpa-interview|Spring Data JPA]] — Hibernate использует рефлексию для маппинга Entity
- [[java-jackson-interview|Jackson]] — ObjectMapper использует рефлексию для сериализации
- [[jvm-interview|JVM]] — classloading, bytecode, invokedynamic, MethodHandle как JVM-уровень
