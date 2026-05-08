---
title: "Вопросы на собеседовании: Java Optional"
description: "Java Optional: создание, извлечение значений, map/flatMap/filter, anti-patterns, Java 9+ методы"
tags:
  - interview
  - programming-languages
  - java-optional-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java Optional"
  - "Java Optional interview"
  - "Java Optional собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Java Optional`

`Optional<T>` — контейнер, который явно выражает возможность отсутствия значения. Введён в Java 8 для замены `null`-возвратов из методов. На собеседованиях проверяют понимание назначения, разницы методов и типичных ошибок использования.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Optional JavaDoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/Optional.html) — полный API Optional
- [Baeldung: Java Optional Guide](https://www.baeldung.com/java-optional) — подробный туториал

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Создание и проверка**
- [Q1. (!) Что такое Optional и зачем он нужен?](#q1-что-такое-optional-и-зачем-он-нужен)
- [Q2. Как создать Optional?](#q2-как-создать-optional)
- [Q3. Чем isPresent() отличается от isEmpty()?](#q3-чем-ispresent-отличается-от-isempty)

**Извлечение значения**
- [Q4. (!) Чем orElse() отличается от orElseGet()?](#q4-чем-orelse-отличается-от-orelseget)
- [Q5. Когда использовать orElseThrow()?](#q5-когда-использовать-orelsethrow)
- [Q6. Почему get() считается плохой практикой?](#q6-почему-get-считается-плохой-практикой)

**Трансформация**
- [Q7. (!) Чем map() отличается от flatMap() в Optional?](#q7-чем-map-отличается-от-flatmap-в-optional)
- [Q8. Как работает filter() в Optional?](#q8-как-работает-filter-в-optional)
- [Q9. Как работают ifPresent() и ifPresentOrElse()?](#q9-как-работают-ifpresent-и-ifpresentorelse)

**Java 9+ методы**
- [Q10. Что делает метод or() в Optional?](#q10-что-делает-метод-or-в-optional)
- [Q11. Для чего нужен Optional.stream()?](#q11-для-чего-нужен-optionalstream)

**Антипаттерны**
- [Q12. (!) Какие антипаттерны использования Optional существуют?](#q12-какие-антипаттерны-использования-optional-существуют)
- [Q13. Почему Optional нельзя использовать как параметр метода?](#q13-почему-optional-нельзя-использовать-как-параметр-метода)
- [Q14. Можно ли хранить Optional в полях класса?](#q14-можно-ли-хранить-optional-в-полях-класса)
- [Q15. Когда Optional не нужен?](#q15-когда-optional-не-нужен)

---

## Q1. (!) Что такое Optional и зачем он нужен?

`Optional<T>` — контейнер-обёртка, который либо содержит значение (`present`), либо пуст (`empty`). Основная цель — **явно выразить в сигнатуре метода**, что результат может отсутствовать.

**До Optional:**
```java
// Возвращает null — не видно из сигнатуры
public User findUserByEmail(String email) {
    return db.query(email); // может вернуть null
}

// Вызывающий код забывает проверить:
User user = findUserByEmail("x@y.com");
user.getName(); // NullPointerException
```

**С Optional:**
```java
public Optional<User> findUserByEmail(String email) {
    return Optional.ofNullable(db.query(email));
}

// Вызывающий код вынужден обработать отсутствие:
findUserByEmail("x@y.com")
    .map(User::getName)
    .orElse("Аноним");
```

**Итог:** `Optional` — контракт в типе, а не комментарий. Не устраняет `null` в коде, но делает его обработку явной.

> [!mcq]
> - [ ] `Optional<T>` — замена всем `null`-значениям в Java, позволяет полностью исключить `NullPointerException` из кода. | `Optional` не устраняет `null` в коде полностью. Он выражает возможное отсутствие значения в сигнатуре метода, но `null` по-прежнему существует и может возникнуть там, где `Optional` не применяется.
> - [x] `Optional<T>` — контейнер-обёртка, который явно выражает в сигнатуре метода, что результат может отсутствовать. | `Optional` делает возможное отсутствие значения частью контракта метода. Вызывающий код вынужден явно обработать оба случая — значение есть или его нет — вместо того чтобы случайно получить `NullPointerException`.
> - [ ] `Optional<T>` — аналог `null`, рекомендованный для хранения в полях JPA-сущностей и DTO для улучшения читаемости. | `Optional` не предназначен для хранения в полях классов. Он не реализует `Serializable`, плохо работает с JPA и Jackson, и считается анти-паттерном в полях сущностей. Его назначение — возвращаемые значения методов.
> - [ ] `Optional<T>` — инструмент для обработки `null` в параметрах методов, заменяющий перегрузку методов в современной Java. | Использование `Optional` как параметра метода — признанный анти-паттерн. Caller всё равно может передать `null` вместо `Optional.empty()`, что приведёт к `NullPointerException`. Для параметров лучше использовать перегрузку или `@Nullable`.

---

## Q2. Как создать Optional?

| Метод | Когда использовать |
|---|---|
| `Optional.empty()` | Явно вернуть пустой Optional |
| `Optional.of(value)` | Значение гарантированно не `null` |
| `Optional.ofNullable(value)` | Значение может быть `null` |

```java
Optional<String> empty = Optional.empty();

Optional<String> nonNull = Optional.of("hello");  // NPE если null

String maybeNull = null;
Optional<String> nullable = Optional.ofNullable(maybeNull); // → empty
```

**Ошибка:**
```java
Optional.of(null); // → NullPointerException немедленно
```

Используйте `Optional.of()` только когда уверены, что значение не `null` (например, результат `new Object()`). В остальных случаях — `ofNullable`.

> [!mcq]
> - [ ] `Optional.of(null)` возвращает `Optional.empty()`, так как `null` автоматически преобразуется в пустой Optional. | `Optional.of(null)` немедленно бросает `NullPointerException`. Именно для ситуаций, когда значение может быть `null`, предназначен `Optional.ofNullable(value)`. ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.
> - [ ] `Optional.ofNullable(null)` бросает `NullPointerException`, так как не допускает передачи `null`. | `Optional.ofNullable(null)` возвращает `Optional.empty()` — это его основное назначение. `NullPointerException` бросает только `Optional.of(null)`. ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.
> - [x] `Optional.of(value)` бросает `NullPointerException` если `value == null`, а `Optional.ofNullable(value)` возвращает `Optional.empty()`. | `Optional.of()` предназначен для значений, которые гарантированно не `null`. Если есть сомнение, нужно использовать `ofNullable()`, который безопасно преобразует `null` в `Optional.empty()`.
> - [ ] `Optional.empty()` и `Optional.ofNullable(null)` — разные объекты, каждый вызов создаёт новый экземпляр `Optional`. | `Optional.empty()` возвращает один и тот же синглтон. `Optional.ofNullable(null)` тоже возвращает `Optional.empty()`. Оба результата ссылаются на один и тот же пустой Optional.

---

## Q3. Чем isPresent() отличается от isEmpty()?

| Метод | Версия | Возвращает `true` когда |
|---|---|---|
| `isPresent()` | Java 8 | Значение присутствует |
| `isEmpty()` | Java 11 | Значение отсутствует |

```java
Optional<String> opt = Optional.of("value");
opt.isPresent(); // true
opt.isEmpty();   // false

Optional<String> empty = Optional.empty();
empty.isPresent(); // false
empty.isEmpty();   // true
```

`isEmpty()` — синтаксический сахар для `!opt.isPresent()`. Улучшает читаемость в условиях:

```java
// Менее читаемо:
if (!opt.isPresent()) { ... }

// Более читаемо:
if (opt.isEmpty()) { ... }
```

> [!mcq]
> - [ ] `isEmpty()` — метод доступен с Java 8 как симметричная альтернатива `isPresent()`. | `isEmpty()` появился только в Java 11, а не в Java 8. В Java 8 для проверки отсутствия значения приходилось писать `!opt.isPresent()`. ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [x] `isEmpty()` — метод доступен с Java 11 как синтаксический сахар для `!isPresent()`. | `isEmpty()` добавлен в Java 11 именно для улучшения читаемости. Вместо `!opt.isPresent()` можно написать `opt.isEmpty()`, что лучше читается в условиях вроде `if (opt.isEmpty())`.
> - [ ] `isEmpty()` — метод доступен с Java 9 как синтаксический сахар для `!isPresent()`. | `isEmpty()` появился в Java 11, а не в Java 9. В Java 9 в `Optional` добавили методы `or()`, `ifPresentOrElse()` и `stream()`, но не `isEmpty()`. ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] `isEmpty()` и `isPresent()` — независимые методы: `opt.isEmpty()` может вернуть `false` даже если `opt.isPresent()` тоже `false`. | `isEmpty()` — строго инверсия `isPresent()`. Если `isPresent()` возвращает `false`, то `isEmpty()` всегда вернёт `true`, и наоборот. ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

---

## Q4. (!) Чем orElse() отличается от orElseGet()?

Оба возвращают значение из Optional или дефолтное значение — но **orElse() вычисляет дефолт всегда**, а **orElseGet() — только если Optional пуст**.

```java
String result1 = Optional.of("value")
    .orElse(expensiveDefault());  // expensiveDefault() ВЫЗЫВАЕТСЯ всегда!

String result2 = Optional.of("value")
    .orElseGet(() -> expensiveDefault());  // expensiveDefault() НЕ вызывается
```

**Когда это важно:**
```java
// Плохо: DB-запрос выполняется даже если Optional не пустой
Optional.ofNullable(cache.get(key))
    .orElse(database.findById(key));  // всегда ходит в базу

// Хорошо: DB-запрос только при cache miss
Optional.ofNullable(cache.get(key))
    .orElseGet(() -> database.findById(key));
```

**Итог:** используйте `orElseGet()` когда дефолтное значение вычисляется дорого (запрос к БД, HTTP-вызов, создание объекта).

> [!mcq]
> - [ ] `orElse(T)` вычисляет аргумент лениво — только если Optional пуст, а `orElseGet(Supplier)` вычисляет аргумент всегда. | Это наоборот: `orElse(T)` вычисляет аргумент **всегда**, потому что Java передаёт аргументы методов до вызова. `orElseGet(Supplier)` вычисляет значение лениво — только если Optional действительно пуст.
> - [x] `orElse(T)` вычисляет аргумент всегда, а `orElseGet(Supplier)` — только если Optional пуст. | `orElse(T)` получает уже вычисленный аргумент, поэтому дорогие операции (запрос к БД) выполнятся даже если Optional непустой. `orElseGet(Supplier)` откладывает вычисление до момента, когда Optional действительно пуст.
> - [ ] `orElse(T)` и `orElseGet(Supplier)` — полностью эквивалентны по поведению, разница только в синтаксисе. | Поведение различается именно в момент вычисления аргумента. При дорогих операциях замена `orElse(db.query())` на `orElseGet(() -> db.query())` может существенно улучшить производительность.
> - [ ] `orElseGet(Supplier)` работает медленнее `orElse(T)` из-за накладных расходов на создание лямбды, поэтому по умолчанию рекомендуется `orElse`. | Накладные расходы на лямбду пренебрежимо малы. Наоборот, `orElseGet()` экономит время при непустом Optional за счёт ленивого вычисления дефолтного значения.

---

## Q5. Когда использовать orElseThrow()?

`orElseThrow()` бросает исключение если Optional пуст — для случаев, когда отсутствие значения является ошибкой.

```java
// Java 10+: бросает NoSuchElementException
User user = userRepository.findById(id)
    .orElseThrow();

// Кастомное исключение:
User user = userRepository.findById(id)
    .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
```

Хорошо ложится в слой сервиса:
```java
public User getUser(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User " + id));
}
```

> [!mcq]
> - [ ] `orElseThrow()` без аргументов бросает `NullPointerException` если Optional пуст. | Метод `orElseThrow()` без аргументов (Java 10+) бросает `NoSuchElementException`, а не `NullPointerException`. `NullPointerException` возникает при попытке обратиться к `null`, но здесь Optional просто пустой.
> - [x] `orElseThrow()` используется когда отсутствие значения — это ошибка, например при поиске сущности по ID, которая должна существовать. | `orElseThrow()` превращает пустой Optional в исключение. Это удобно в сервисном слое при работе с `findById()`: если сущность обязана быть, отсутствие говорит об ошибке и требует остановки выполнения с понятным сообщением.
> - [ ] `orElseThrow()` следует применять в любой цепочке с Optional, чтобы гарантировать извлечение значения. | Не в любой цепочке отсутствие значения является ошибкой. Иногда пустой Optional — валидный случай, и тогда уместнее `orElse()`, `orElseGet()` или `ifPresent()`. `orElseThrow()` нужен только когда отсутствие — действительно ошибка.
> - [ ] `orElseThrow(() -> ex)` вычисляет Supplier заранее при создании цепочки, поэтому плохо подходит для дорогих исключений. | Supplier в `orElseThrow()` вычисляется лениво — только если Optional пуст. Это позволяет формировать детальное сообщение с данными (например, `"User " + id`) без накладных расходов для непустых Optional.

---

## Q6. Почему get() считается плохой практикой?

`get()` бросает `NoSuchElementException` если Optional пуст — то есть мы получаем ту же проблему, что и с `NullPointerException`.

```java
Optional<String> opt = Optional.empty();
opt.get(); // → NoSuchElementException: No value present
```

**Антипаттерн:**
```java
if (opt.isPresent()) {
    String s = opt.get();  // плохо: дублирование проверки + get()
}

// Лучше:
opt.ifPresent(s -> process(s));
// или:
String s = opt.orElse("default");
```

`get()` изредка оправдан только внутри `if (opt.isPresent())`, но даже тогда есть более идиоматичные способы.

> [!mcq]
> - [ ] `get()` — предпочтительный способ извлечения значения из Optional, аналогичный `.get()` в `Map`. | `get()` считается плохой практикой, так как бросает `NoSuchElementException` на пустом Optional. Это воспроизводит ту же проблему, что и `NullPointerException`, ради устранения которой и создавался `Optional`.
> - [ ] `get()` возвращает `null` если Optional пуст, что делает его безопасной альтернативой другим методам извлечения. | `get()` на пустом Optional бросает `NoSuchElementException`, а не возвращает `null`. Для безопасного получения значения с дефолтом служат `orElse()`, `orElseGet()` и `orElseThrow()`.
> - [x] `get()` бросает `NoSuchElementException` если Optional пуст, что считается плохой практикой — лучше использовать `orElse()`, `orElseGet()` или `ifPresent()`. | Вызов `get()` без предварительной проверки `isPresent()` приводит к исключению. Паттерн `isPresent() + get()` — это анти-паттерн, потому что более идиоматичны `orElse()`, `map()` и `ifPresent()`.
> - [ ] `get()` — устаревший метод, удалённый в Java 11 в пользу `orElseThrow()`. | `get()` не удалён, он помечен как нежелательный (`@Deprecated`) только в Java 10, однако по-прежнему присутствует в API. `orElseThrow()` без аргументов является его рекомендованной заменой с Java 10.

---

## Q7. (!) Чем map() отличается от flatMap() в Optional?

**`map()`** — применяет функцию к значению, результат оборачивает в `Optional`:
```java
Optional<String> name = Optional.of("  hello  ");
Optional<String> trimmed = name.map(String::trim);  // Optional["hello"]
```

**`flatMap()`** — применяет функцию, которая сама возвращает `Optional`, избегая `Optional<Optional<T>>`:
```java
public class User {
    public Optional<String> getEmail() { ... }
}

// map создаёт Optional<Optional<String>>:
Optional<Optional<String>> wrong = Optional.of(user).map(User::getEmail);

// flatMap разворачивает:
Optional<String> email = Optional.of(user).flatMap(User::getEmail);  // Optional<String>
```

**Цепочка с flatMap:**
```java
Optional<String> city = Optional.of(user)
    .flatMap(User::getAddress)     // User → Optional<Address>
    .flatMap(Address::getCity)     // Address → Optional<City>
    .map(City::getName);           // City → String
```

**Итог:** `map` — для обычных функций `T → R`; `flatMap` — для функций `T → Optional<R>`.

> [!mcq]
> - [ ] `map()` и `flatMap()` в Optional полностью взаимозаменяемы — разница только в предпочтениях разработчика. | Они не взаимозаменяемы. Если применить `map()` к методу, возвращающему `Optional<T>`, получится `Optional<Optional<T>>`. `flatMap()` специально предназначен для функций `T → Optional<R>` и «разворачивает» вложенный Optional.
> - [ ] `flatMap()` принимает функцию `T → R` и оборачивает результат в `Optional`, а `map()` принимает функцию `T → Optional<R>` без дополнительной обёртки. | Это описание перепутало методы. `map()` принимает `T → R` и оборачивает в `Optional`. `flatMap()` принимает `T → Optional<R>` и не оборачивает повторно, избегая `Optional<Optional<T>>`.
> - [x] `map()` принимает функцию `T → R` и оборачивает результат в `Optional`, а `flatMap()` принимает функцию `T → Optional<R>` и не оборачивает повторно. | `map()` подходит для обычных преобразований. `flatMap()` нужен когда маппинг-функция сама возвращает `Optional` — например `User::getEmail` где `getEmail()` возвращает `Optional<String>`.
> - [ ] `map()` в Optional работает только с примитивными типами, а `flatMap()` — с объектами. | Оба метода работают с любыми ссылочными типами через generics. Принципиальное отличие — в типе функции, которую они принимают: `T → R` для `map()` и `T → Optional<R>` для `flatMap()`.

---

## Q8. Как работает filter() в Optional?

`filter(predicate)` проверяет значение: если предикат `true` — Optional остаётся, если `false` — становится пустым.

```java
Optional<Integer> age = Optional.of(25);

Optional<Integer> adult = age.filter(a -> a >= 18);  // Optional[25]
Optional<Integer> senior = age.filter(a -> a >= 60); // Optional.empty()
```

Удобно для валидации в цепочке:
```java
Optional<User> activeAdmin = userRepository.findById(id)
    .filter(User::isActive)
    .filter(u -> u.hasRole(Role.ADMIN));
```

> [!mcq]
> - [ ] `filter(predicate)` бросает `NoSuchElementException` если предикат возвращает `false`. | `filter()` не бросает исключений. Если предикат возвращает `false`, Optional просто становится пустым (`Optional.empty()`). Если предикат возвращает `true` — Optional остаётся с тем же значением.
> - [x] `filter(predicate)` возвращает тот же Optional если предикат `true`, или `Optional.empty()` если предикат `false` или Optional изначально пуст. | `filter()` — безопасная операция: она не создаёт исключений и удобно встраивается в цепочки маппинга. На пустом Optional `filter()` не вызывает предикат и просто возвращает `Optional.empty()`.
> - [ ] `filter(predicate)` выбрасывает `IllegalArgumentException` если Optional пуст, так как предикату нечего проверять. | На пустом Optional `filter()` не вызывает предикат вообще и просто возвращает `Optional.empty()`. Это позволяет безопасно использовать `filter()` в цепочках без проверок на пустоту.
> - [ ] `filter(predicate)` преобразует Optional — если предикат `false`, значение заменяется на `null` внутри Optional вместо возврата пустого Optional. | `Optional` не может содержать `null`. Если предикат возвращает `false`, `filter()` возвращает `Optional.empty()`, а не Optional с `null` внутри. ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

---

## Q9. Как работают ifPresent() и ifPresentOrElse()?

**`ifPresent(consumer)`** — выполняет действие если значение есть:
```java
Optional.of("hello").ifPresent(System.out::println);  // выводит "hello"
Optional.empty().ifPresent(System.out::println);       // ничего
```

**`ifPresentOrElse(consumer, runnable)`** (Java 9+) — выполняет одно из двух действий:
```java
Optional.of("hello")
    .ifPresentOrElse(
        v -> System.out.println("Значение: " + v),
        () -> System.out.println("Значение отсутствует")
    );
```

Заменяет конструкцию `if (opt.isPresent()) { ... } else { ... }`.

> [!mcq]
> - [ ] `ifPresent(Consumer)` выполняет действие и возвращает `Optional` для дальнейшего использования в цепочке. | `ifPresent()` возвращает `void`, а не `Optional`. Это терминальная операция: после неё цепочку не продолжить. Если нужна цепочка с побочным эффектом, используется `peek()` в Stream API или возврат к трансформациям через `map()`.
> - [x] `ifPresent(Consumer)` выполняет Consumer только если значение есть, а `ifPresentOrElse(Consumer, Runnable)` дополнительно выполняет Runnable если Optional пуст. | `ifPresentOrElse()` (Java 9+) удобно заменяет `if (opt.isPresent()) { ... } else { ... }`. Он делает два действия симметричными в одном выражении, без необходимости хранить Optional в переменной.
> - [ ] `ifPresentOrElse()` доступен с Java 8 наравне с `ifPresent()`. | `ifPresentOrElse()` добавлен в Java 9, а не Java 8. В Java 8 был только `ifPresent()`, и для обработки пустого случая приходилось явно проверять `isPresent()`.
> - [ ] `ifPresent(Consumer)` бросает `NoSuchElementException` если Optional пуст, чтобы сигнализировать об ошибке. | `ifPresent()` на пустом Optional просто не вызывает Consumer и завершает выполнение без исключения. Именно поэтому он безопасен в цепочках и удобен как альтернатива явным `null`-проверкам.

---

## Q10. Что делает метод or() в Optional?

`or(supplier)` (Java 9+) — если Optional пуст, возвращает альтернативный `Optional` из Supplier. Ленивое вычисление.

```java
Optional<String> result = Optional.<String>empty()
    .or(() -> Optional.of("fallback"));
// result = Optional["fallback"]

// Цепочка fallback-ов:
Optional<String> value = tryCache()
    .or(this::tryDatabase)
    .or(this::tryRemoteApi);
```

Отличие от `orElse`/`orElseGet`: возвращает `Optional`, а не само значение — удобно в цепочках.

> [!mcq]
> - [ ] `or(Supplier)` возвращает само значение типа `T`, а не `Optional<T>`, что делает его полным аналогом `orElseGet()`. | `or()` возвращает `Optional<T>`, а не `T`. Это ключевое отличие от `orElseGet()`, который возвращает само значение. `or()` удобен именно для цепочек, где нужно продолжать работу с Optional.
> - [x] `or(Supplier<Optional<T>>)` возвращает исходный Optional если он непустой, или Optional из Supplier если пустой — результат всегда `Optional<T>`. | `or()` добавлен в Java 9 именно для построения цепочек fallback-стратегий: `tryCache().or(this::tryDatabase).or(this::tryRemoteApi)`. Каждый шаг возвращает `Optional`, что позволяет продолжать цепочку.
> - [ ] `or(Supplier<Optional<T>>)` — аналог `orElseGet()`, добавленный в Java 9 как его псевдоним с другим возвращаемым типом. | `or()` и `orElseGet()` не псевдонимы. `orElseGet()` «извлекает» значение из Optional, возвращая `T`. `or()` оставляет результат в `Optional`, что принципиально для цепочек.
> - [ ] `or(Supplier<Optional<T>>)` вычисляет Supplier всегда, независимо от того, пуст ли исходный Optional. | `or()` — ленивый метод: Supplier вызывается только если исходный Optional пуст. Если Optional содержит значение, Supplier не вызывается вовсе. ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

---

## Q11. Для чего нужен Optional.stream()?

`stream()` (Java 9+) конвертирует `Optional` в `Stream<T>` с 0 или 1 элементом. Удобно при работе со Stream API.

```java
// Фильтрация Optional из списка
List<Optional<String>> optionals = List.of(
    Optional.of("a"), Optional.empty(), Optional.of("b")
);

List<String> values = optionals.stream()
    .flatMap(Optional::stream)  // убирает пустые Optional
    .collect(Collectors.toList());
// ["a", "b"]
```

До Java 9 нужно было:
```java
.filter(Optional::isPresent).map(Optional::get)
// vs теперь:
.flatMap(Optional::stream)
```

> [!mcq]
> - [ ] `Optional.stream()` доступен с Java 8 и возвращает бесконечный поток значений Optional. | `Optional.stream()` добавлен в Java 9, а не Java 8. Он возвращает поток с одним элементом если Optional непустой, или пустой поток если Optional пуст — никогда не бесконечный.
> - [x] `Optional.stream()` доступен с Java 9 и возвращает поток с одним элементом если Optional непустой, или пустой поток если Optional пуст. | `Optional.stream()` позволяет удобно интегрировать Optional в цепочки Stream API. Вместо громоздкого `.filter(Optional::isPresent).map(Optional::get)` достаточно `.flatMap(Optional::stream)`.
> - [ ] `Optional.stream()` доступен с Java 9 и всегда возвращает поток ровно с одним элементом — `null` если Optional пуст. | `Optional.stream()` возвращает пустой поток (`Stream.empty()`) для пустого Optional, а не поток с `null`. Это позволяет использовать его в `flatMap` для фильтрации пустых Optional.
> - [ ] `Optional.stream()` доступен с Java 11 и возвращает поток с одним элементом если Optional непустой, или пустой поток если Optional пуст. | `Optional.stream()` добавлен в Java 9, а не Java 11. В Java 11 в `Optional` добавили метод `isEmpty()`. ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

---

## Q12. (!) Какие антипаттерны использования Optional существуют?

**1. isPresent() + get() вместо орElse/map:**
```java
// Плохо
if (opt.isPresent()) {
    return opt.get();
} else {
    return "default";
}

// Хорошо
return opt.orElse("default");
```

**2. Optional как параметр метода:**
```java
// Плохо — caller может передать null
public void process(Optional<String> name) { }
// Хорошо — перегрузка
public void process(String name) { }
public void process() { }
```

**3. Optional в полях сущности:**
```java
// Плохо
class User {
    private Optional<String> phone;  // не сериализуется в JSON/JPA корректно
}
// Хорошо
class User {
    private String phone;  // null для отсутствия
}
```

**4. orElse с дорогим вычислением:**
```java
// Плохо — db.find() вызывается всегда
Optional.ofNullable(cache.get(k)).orElse(db.find(k));
// Хорошо — ленивое вычисление
Optional.ofNullable(cache.get(k)).orElseGet(() -> db.find(k));
```

**5. Оборачивание коллекций:**
```java
// Плохо
public Optional<List<User>> getUsers() { ... }
// Хорошо
public List<User> getUsers() { return Collections.emptyList(); }  // пустой список
```

> [!mcq]
> - [ ] Оборачивание коллекций в `Optional<List<T>>` — рекомендованный приём для явного различия между «список не найден» и «список пустой». | Оборачивание коллекции в Optional — анти-паттерн. Пустая коллекция (`Collections.emptyList()`) уже выражает отсутствие элементов однозначно и проще в использовании. Добавление `Optional` создаёт два почти одинаковых состояния и лишние проверки у вызывающего кода.
> - [x] Использование `isPresent() + get()`, Optional как параметр метода, Optional в полях сущности и оборачивание коллекций в Optional — всё это анти-паттерны. | Эти случаи приводят к хрупкому коду или противоречат назначению `Optional`. Замены: `orElse()`/`map()`, перегрузка методов, `null`-поле с Optional-геттером, пустая коллекция вместо `Optional<List<T>>`.
> - [ ] `orElse(expensiveDefault())` и `orElseGet(() -> expensiveDefault())` ведут себя одинаково — оба вычисляют default лениво. | `orElse()` вычисляет аргумент всегда, а `orElseGet()` — только если Optional пуст. Использование `orElse()` с дорогой операцией — один из ключевых анти-паттернов, ухудшающий производительность без необходимости.
> - [ ] `Optional` в полях сущности безопасно сериализуется через Jackson и Hibernate без дополнительной конфигурации. | `Optional` не реализует `Serializable`, Hibernate не умеет маппить его на колонку, а Jackson требует специальной настройки модуля. Поэтому Optional в полях сущности — анти-паттерн.

---

## Q13. Почему Optional нельзя использовать как параметр метода?

Если метод принимает `Optional<String>`, caller всё равно может передать `null` вместо `Optional.empty()`, что снова приведёт к `NullPointerException`:

```java
public void notify(Optional<String> email) {
    email.ifPresent(this::sendEmail);  // NPE если email == null
}

notify(null);  // Компилятор не запрещает — NPE в рантайме
```

**Правильные альтернативы:**
- Перегрузка методов: `notify()` и `notify(String email)`
- `@Nullable` аннотация (с соответствующим null-check)
- Явный `null`-параметр с проверкой внутри

`Optional` создан для **возвращаемых значений**, не для параметров.

> [!mcq]
> - [ ] Компилятор Java запрещает передачу `null` вместо `Optional` в параметр метода, поэтому такой подход полностью безопасен. | Компилятор не запрещает передачу `null` вместо `Optional`. Вызов `notify(null)` успешно компилируется, но при обращении к методам Optional внутри метода бросает `NullPointerException`. Это и есть главная проблема паттерна.
> - [x] Caller может передать `null` вместо `Optional.empty()`, что приведёт к `NullPointerException` — `Optional` создан для возвращаемых значений, а не параметров. | Проблема не решается через Optional-параметр: вместо простого `null` теперь два способа выразить отсутствие (`null` и `Optional.empty()`). Вместо этого используется перегрузка методов, `@Nullable` или явный `null`-параметр.
> - [ ] Использование Optional в параметре метода улучшает читаемость API, поэтому это рекомендованный паттерн в Java. | Наоборот, это официально осуждается разработчиками JDK (Brian Goetz и другие). `Optional` спроектирован именно как возвращаемый тип, а не как средство передачи значения. Для параметров используется перегрузка или `@Nullable`.
> - [ ] Optional-параметр безопасен, если метод помечен аннотацией `@NotNull` — тогда компилятор запретит передачу `null`. | Аннотации вроде `@NotNull` не обрабатываются стандартным javac, они лишь подсказка для IDE и статических анализаторов. В рантайме `null` всё равно можно передать, что приведёт к `NullPointerException`.

---

## Q14. Можно ли хранить Optional в полях класса?

Технически — да. На практике — **не рекомендуется**:

- `Optional` не реализует `Serializable` → проблемы с JPA, JSON (Jackson), RMI
- Jackson по умолчанию не сериализует `Optional` в `null`/значение без конфигурации
- Hibernate не умеет маппировать `Optional<String>` на колонку

```java
// Плохо
@Entity
class User {
    private Optional<String> phone;  // Hibernate падает
}

// Хорошо
@Entity
class User {
    private String phone;  // null === отсутствует
    
    public Optional<String> getPhone() {
        return Optional.ofNullable(phone);  // Optional только в геттере
    }
}
```

**Итог:** поле `String`, геттер `Optional<String>` — золотой стандарт для сущностей.

> [!mcq]
> - [ ] Хранить `Optional` в полях сущности безопасно, так как он реализует `Serializable` с Java 11. | `Optional` не реализует `Serializable` ни в одной версии Java — это сознательное решение разработчиков JDK. Сериализация через `Serializable`, JPA и другие механизмы работает некорректно или требует специальной конфигурации.
> - [x] Технически можно, но не рекомендуется: поле храним как `String` (с `null`), а в геттере возвращаем `Optional.ofNullable(field)`. | Это «золотой стандарт» для сущностей: поле совместимо с JPA, JSON-сериализацией и `Serializable`, а публичное API через геттер возвращает `Optional` и делает контракт явным. Лучшее сочетание инфраструктурных требований и удобства API.
> - [ ] Hibernate и Jackson умеют из коробки корректно маппить `Optional<String>` как поле сущности без дополнительной настройки. | Hibernate не поддерживает `Optional<T>` как тип колонки. Jackson сериализует `Optional` нестандартно и требует подключения `Jdk8Module`. Это основная причина, по которой `Optional` не кладут в поля.
> - [ ] Использование `Optional` в полях даёт выигрыш в производительности, так как избегает `null`-проверок. | Наоборот, каждое поле-`Optional` — это лишняя аллокация объекта-обёртки при создании экземпляра. `null` в поле не требует аллокаций и остаётся более экономным для моделей данных.

---

## Q15. Когда Optional не нужен?

`Optional` добавляет накладные расходы (аллокация объекта) и не всегда оправдан:

| Ситуация | Что использовать |
|---|---|
| Пустая коллекция | `Collections.emptyList()` |
| Поле сущности/DTO | `null` с `@Nullable` аннотацией |
| Параметр метода | Перегрузка или `@Nullable` |
| `private` методы | `null` (внутренний контракт) |
| Горячий путь кода | `null` (нет аллокаций) |
| `void` метод | `ifPresent()` → просто `if (x != null)` |

**Optional нужен** когда:
- Метод возвращает значение, которого **может не быть** (поиск по ID, find by criteria)
- Хотите вынудить вызывающий код явно обработать оба случая
- Строите цепочку `map`/`filter`/`flatMap` без явных `null`-проверок

> [!mcq]
> - [ ] `Optional` следует использовать везде, где возможен `null` — это лучшая защита от `NullPointerException`. | Optional — не серебряная пуля. У него есть накладные расходы (аллокация обёртки), и в ряде случаев он избыточен: для полей сущностей, параметров, коллекций, горячих путей и private-методов проще использовать `null` или пустую коллекцию.
> - [x] `Optional` не нужен для пустых коллекций (используй `Collections.emptyList()`), параметров, полей сущности и горячего кода — он нужен для возвращаемых значений поисковых методов. | `Optional` предназначен именно для возвращаемых значений методов, где отсутствие результата — часть контракта (например, `findById()`). Для других случаев существуют более подходящие альтернативы: пустые коллекции, перегрузка методов, `@Nullable`-поля.
> - [ ] Вместо пустого списка `Collections.emptyList()` лучше возвращать `Optional<List<T>>` — это более выразительно. | Оборачивание коллекции в `Optional` — анти-паттерн. Пустая коллекция и так однозначно сигнализирует об отсутствии элементов, а `Optional<List<T>>` создаёт два похожих состояния (пустой Optional и Optional с пустым списком), что усложняет обработку.
> - [ ] В горячих путях (внутри циклов, hot-code) использование `Optional` предпочтительнее `null` для улучшения производительности. | Каждый `Optional` — это аллокация дополнительного объекта. В горячем коде это создаёт нагрузку на GC и замедляет выполнение. Для производительных участков `null` остаётся более экономным выбором.

---

## See also

- [Java Core](java-core-interview.md) — null в Java, NullPointerException
- [Java Stream API](java-stream-interview.md) — Optional.stream(), flatMap с Optional
- [Java 8](java-8-interview.md) — Optional как нововведение Java 8
- [Java Generics](java-generics-interview.md) — Optional<T> и система типов
- [Functional Interfaces](java-functional-interface-interview.md) — Supplier в orElseGet, Consumer в ifPresent
- [Java Exceptions](java-exceptions-interview.md) — orElseThrow и кастомные исключения
- [Spring Data JPA](../../frameworks/spring/spring-data-jpa-interview.md) — Optional в Repository.findById()
- [Java Collections](java-collections-interview.md) — когда пустой список лучше Optional
