---
title: "Вопросы на собеседовании: Spring AOP"
description: "Spring AOP: Aspect, Advice, Pointcut, JoinPoint, прокси-механизм (JDK/CGLIB), типы advice, pointcut-выражения, проблема self-invocation, тестирование"
tags:
  - interview
  - spring
  - spring-aop-interview
aliases:
  - "Spring AOP interview"
  - "Spring AOP собеседование"
  - "AOP interview questions"
  - "Aspect Oriented Programming interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring AOP`

`Spring AOP` — реализация аспектно-ориентированного программирования в Spring для вынесения cross-cutting concerns (логирование, транзакции, безопасность, метрики) в отдельные аспекты. Основана на динамических прокси — это ключевое отличие от полного AspectJ.

## Полезные ссылки

### Официальная документация

- [Spring AOP Reference](https://docs.spring.io/spring-framework/reference/core/aop.html) — полная документация
- [AspectJ Reference](https://www.eclipse.org/aspectj/doc/released/progguide/index.html) — язык pointcut-выражений

### Baeldung tutorials

- [Introduction to Spring AOP](https://www.baeldung.com/spring-aop) — основы
- [Comparing Spring AOP and AspectJ](https://www.baeldung.com/spring-aop-vs-aspectj)
- [Pointcut Expressions](https://www.baeldung.com/spring-aop-pointcut-tutorial)
- [Advice Types](https://www.baeldung.com/spring-aop-advice-tutorial)
- [Custom AOP Annotation](https://www.baeldung.com/spring-aop-annotation)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Концепции AOP**
- [Q1. (!) Что такое AOP и зачем он нужен?](#q1-что-такое-aop-и-зачем-он-нужен)
- [Q2. (!) Какие ключевые термины AOP нужно знать?](#q2-какие-ключевые-термины-aop-нужно-знать)
- [Q3. Чем Spring AOP отличается от AspectJ?](#q3-чем-spring-aop-отличается-от-aspectj)

**Прокси-механизм**
- [Q4. (!) Как Spring AOP создаёт прокси: JDK dynamic proxy vs CGLIB?](#q4-как-spring-aop-создаёт-прокси-jdk-dynamic-proxy-vs-cglib)
- [Q5. (!) Что такое проблема self-invocation и как её обойти?](#q5-что-такое-проблема-self-invocation-и-как-её-обойти)
- [Q6. На каких бинах Spring AOP не работает?](#q6-на-каких-бинах-spring-aop-не-работает)

**Конфигурация**
- [Q7. Как объявить аспект в Spring?](#q7-как-объявить-аспект-в-spring)
- [Q8. Что такое `@EnableAspectJAutoProxy` и когда он нужен?](#q8-что-такое-enableaspectjautoproxy-и-когда-он-нужен)

**Pointcut-выражения**
- [Q9. (!) Как работает `execution` pointcut-выражение?](#q9-как-работает-execution-pointcut-выражение)
- [Q10. Какие designators есть в Spring AOP?](#q10-какие-designators-есть-в-spring-aop)
- [Q11. Как использовать кастомную аннотацию как pointcut?](#q11-как-использовать-кастомную-аннотацию-как-pointcut)
- [Q12. Как переиспользовать pointcut-выражение через `@Pointcut`?](#q12-как-переиспользовать-pointcut-выражение-через-pointcut)

**Типы Advice**
- [Q13. (!) Какие типы Advice есть в Spring AOP?](#q13-какие-типы-advice-есть-в-spring-aop)
- [Q14. (!) Как работает `@Around` advice?](#q14-как-работает-around-advice)
- [Q15. Что такое `JoinPoint` и `ProceedingJoinPoint`?](#q15-что-такое-joinpoint-и-proceedingjoinpoint)
- [Q16. Как получить данные о перехваченном методе внутри advice?](#q16-как-получить-данные-о-перехваченном-методе-внутри-advice)
- [Q17. Как управлять порядком выполнения аспектов?](#q17-как-управлять-порядком-выполнения-аспектов)

**Практическое применение**
- [Q18. Как реализовать логирование через AOP?](#q18-как-реализовать-логирование-через-aop)
- [Q19. Как реализовать кастомный retry через AOP?](#q19-как-реализовать-кастомный-retry-через-aop)
- [Q20. Как измерить время выполнения метода через AOP?](#q20-как-измерить-время-выполнения-метода-через-aop)
- [Q21. Как Spring Transactions используют AOP под капотом?](#q21-как-spring-transactions-используют-aop-под-капотом)

**Тестирование**
- [Q22. Как тестировать AOP-аспекты?](#q22-как-тестировать-aop-аспекты)

---

## Q1. Что такое AOP и зачем он нужен?

**AOP (Aspect-Oriented Programming)** — парадигма программирования для вынесения **cross-cutting concerns** (сквозных функций) в отдельные модули — `аспекты`.

**Cross-cutting concerns** — логика, которая повторяется во многих частях приложения и не относится к бизнес-логике напрямую:

| Cross-cutting concern | Без AOP | С AOP |
|---|---|---|
| Логирование | В каждом методе | Один аспект |
| Транзакции | `@Transactional` (реализован через AOP) | Один аспект |
| Безопасность | Проверки в каждом сервисе | Один аспект |
| Метрики/трейсинг | В каждом методе | Один аспект |
| Retry-логика | Обёртки повсюду | Один аспект |

```java
// Без AOP — логирование в каждом методе:
public Order createOrder(OrderRequest req) {
    log.info("Creating order...");
    try {
        Order order = doCreate(req);
        log.info("Order created: {}", order.getId());
        return order;
    } catch (Exception e) {
        log.error("Failed to create order", e);
        throw e;
    }
}

// С AOP — бизнес-логика без лишнего:
public Order createOrder(OrderRequest req) {
    return doCreate(req);  // логирование в аспекте
}
```

**Итог:** AOP делает код чище, убирает дублирование и упрощает поддержку cross-cutting concerns.

> [!mcq]
> - [ ] AOP — это механизм замены наследования композицией на уровне байткода. | AOP не связан с выбором между наследованием и композицией. AOP — это отдельная парадигма для вынесения cross-cutting concerns (логирование, транзакции, безопасность) из бизнес-логики в аспекты.
> - [x] AOP — это парадигма для вынесения cross-cutting concerns в отдельные модули-аспекты. | Cross-cutting concerns — это логика (логирование, транзакции, метрики), которая повторяется во многих местах и не относится к бизнес-логике. AOP позволяет вынести её в один аспект, избавив бизнес-код от дублирования.
> - [ ] AOP — это способ описать зависимости между бинами декларативно через аннотации. | Декларативное описание зависимостей — это про IoC/DI (Inversion of Control / Dependency Injection). AOP решает другую задачу — вынесение сквозной функциональности в аспекты.
> - [ ] AOP — это синоним паттерна Decorator на уровне языка Java. | Паттерн Decorator — это один из возможных инструментов реализации AOP, но не синоним. AOP — это парадигма программирования, охватывающая больше: pointcut-выражения, weaving, типы advice.

## Q2. Какие ключевые термины AOP нужно знать?

```
JoinPoint    — конкретная точка выполнения (вызов метода)
Pointcut     — предикат: КАКИЕ JoinPoint-ы перехватывать
Advice       — ЧТО делать при совпадении Pointcut-а
Aspect       — модуль = Pointcut + Advice(s)
Weaving      — процесс применения Aspect-а к целевому объекту
Introduction — добавление нового интерфейса/поведения к классу
```

Мнемоника: **Aspect = Pointcut (где?) + Advice (что?)**

| Термин | Аналогия | Пример |
|---|---|---|
| `JoinPoint` | Перекрёсток | Вызов `orderService.create()` |
| `Pointcut` | Дорожный знак | "Все методы сервисного слоя" |
| `Advice` | Действие на знак | Логировать входные параметры |
| `Aspect` | Знак + действие вместе | Логирующий аспект |
| `Weaving` | Установка знака | Создание прокси для перехвата |

**Spring AOP поддерживает только один тип JoinPoint — вызов метода**. В отличие от AspectJ, нет перехвата конструктора, поля, статического инициализатора.

> [!mcq]
> - [ ] Spring AOP поддерживает перехват вызовов конструктора, полей и методов. | Spring AOP ограничен только перехватом вызовов методов. Перехват конструкторов и полей поддерживается только в полноценном AspectJ с bytecode weaving.
> - [x] Spring AOP поддерживает только один тип JoinPoint — вызов метода. | В отличие от AspectJ, Spring AOP работает исключительно через runtime proxy и может перехватывать только вызовы методов Spring-бинов. Конструкторы, статические инициализаторы и поля недоступны.
> - [ ] Spring AOP поддерживает перехват методов и конструкторов, но не полей. | Spring AOP ограничен только методами. Конструкторы не перехватываются ни при каких условиях в Spring AOP — для этого нужен AspectJ.
> - [ ] Spring AOP поддерживает только статические методы через @Around advice. | Spring AOP не перехватывает статические методы вообще — они не проходят через прокси. Перехватываются только instance-методы Spring-бинов.

## Q3. Чем Spring AOP отличается от AspectJ?

| Критерий | Spring AOP | AspectJ |
|---|---|---|
| Механизм | Runtime proxy | Bytecode weaving |
| JoinPoint types | Только вызов метода | Поле, конструктор, статик-блок, метод |
| Применимость | Только Spring beans | Любой Java-класс |
| Производительность | Прокси overhead | Нет overhead (compile-time) |
| Сложность setup | Простой (autoconfigure) | Нужен AspectJ compiler / agent |
| Self-invocation | Не перехватывает | Перехватывает |
| Использование синтаксиса | AspectJ annotations (но не weaver) | AspectJ annotations + weaver |

**Вывод:** Spring AOP — "достаточно хорошо" для 90% задач. AspectJ — когда нужны non-Spring бины, поля, конструкторы, или нет overhead от прокси.

> [!mcq]
> - [ ] Spring AOP применяет аспекты во время компиляции через bytecode weaving. | Spring AOP применяет аспекты во время выполнения (runtime) через динамические прокси. Bytecode weaving — это механизм AspectJ, а не Spring AOP.
> - [ ] Spring AOP перехватывает вызовы методов на не-Spring-бинах через агент JVM. | Spring AOP работает только со Spring-бинами и не использует JVM-агент. Для перехвата не-Spring-объектов нужен AspectJ с load-time или compile-time weaving.
> - [x] Spring AOP применяет аспекты во время выполнения через динамические прокси. | Spring AOP создаёт proxy-объект вокруг бина при его инициализации. Каждый вызов метода проходит через прокси, который применяет Advice. Это runtime-подход без изменения байткода.
> - [ ] Spring AOP применяет аспекты через аннотированный компилятор AspectJ. | Spring AOP использует аннотации AspectJ (@Aspect, @Before и т.д.) как синтаксис, но не использует сам компилятор AspectJ. Аспекты применяются через обычные Spring прокси.

## Q4. Как Spring AOP создаёт прокси: JDK dynamic proxy vs CGLIB?

Когда Spring видит `@Aspect`-бин, он создаёт прокси вокруг целевого объекта:

```
Клиент → Прокси-объект → [Advice срабатывает] → Целевой объект
```

**JDK Dynamic Proxy** (default, если класс реализует интерфейс):

```java
public interface OrderService { Order create(OrderRequest req); }
// Spring создаст java.lang.reflect.Proxy, реализующий OrderService
```

**CGLIB** (если класса нет интерфейса или `proxyTargetClass = true`):

```java
@Service
public class UserService { ... }  // нет интерфейса → CGLIB subclass
```

CGLIB генерирует подкласс целевого класса, переопределяя методы. Отсюда ограничение: **нельзя использовать AOP на `final`-классах и `final`-методах**.

```java
@EnableAspectJAutoProxy(proxyTargetClass = true)  // всегда CGLIB
@Configuration
public class AppConfig { }
```

**Практическое следствие:** Spring внедряет прокси вместо реального объекта. `@Autowired OrderService` — это прокси, не `OrderServiceImpl`.

> [!mcq]
> - [ ] Spring AOP использует JDK Dynamic Proxy только когда класс помечен @Proxy. | JDK Dynamic Proxy выбирается автоматически, когда бин реализует хотя бы один интерфейс. Никакой аннотации @Proxy не существует.
> - [ ] Spring AOP всегда использует CGLIB независимо от наличия интерфейсов. | По умолчанию Spring выбирает JDK Dynamic Proxy если бин реализует интерфейс. CGLIB используется только при отсутствии интерфейсов или при явном proxyTargetClass = true.
> - [x] Spring AOP использует JDK Dynamic Proxy когда бин реализует интерфейс, и CGLIB — когда нет. | JDK Dynamic Proxy создаёт реализацию интерфейса через java.lang.reflect.Proxy. CGLIB генерирует подкласс целевого класса. Выбор происходит автоматически по наличию интерфейса, если не задан proxyTargetClass = true.
> - [ ] Spring AOP использует CGLIB когда класс реализует интерфейс, и JDK Dynamic Proxy — когда нет. | Логика обратная: JDK Dynamic Proxy используется при наличии интерфейса (создаёт proxy, реализующий тот же интерфейс), CGLIB — при отсутствии интерфейса (создаёт подкласс).

## Q5. Что такое проблема self-invocation и как её обойти?

**Проблема:** когда метод вызывает другой метод **того же объекта** (`this.method()`), вызов идёт напрямую, минуя прокси → Advice не срабатывает.

```java
@Service
public class OrderService {

    @Transactional
    public void processOrder(Long id) {
        validateOrder(id);     // ⚠️ вызов через this — прокси не задействован!
        saveOrder(id);
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void saveOrder(Long id) { ... }  // транзакция НЕ создастся!
}
```

**Обходные пути:**

1. **Инжектировать сам себя** (Spring 4.3+):

```java
@Service
public class OrderService {

    @Autowired
    @Lazy
    private OrderService self;  // прокси инжектируется

    public void processOrder(Long id) {
        self.saveOrder(id);  // через прокси — работает!
    }
}
```

2. **Использовать `AopContext.currentProxy()`**:

```java
((OrderService) AopContext.currentProxy()).saveOrder(id);
// Нужно @EnableAspectJAutoProxy(exposeProxy = true)
```

3. **Переместить метод в другой бин** — лучшее архитектурное решение.

4. **Использовать AspectJ** вместо Spring AOP.

**Итог:** self-invocation — самая частая ловушка с Spring AOP. Лучшее решение — выделить в отдельный Spring-компонент.

> [!mcq]
> - [ ] Проблема self-invocation решается аннотацией @SelfProxy на методе. | Такой аннотации не существует. Spring AOP решает self-invocation через self-injection с @Lazy, через AopContext.currentProxy() или через вынос метода в другой бин.
> - [ ] Проблема self-invocation решается добавлением @EnableAspectJAutoProxy на классе сервиса. | @EnableAspectJAutoProxy добавляется на @Configuration-класс и включает обработку @Aspect-бинов. Это не решает проблему self-invocation — вызов this.method() всё равно обойдёт прокси.
> - [x] Лучшее решение проблемы self-invocation — вынести вызываемый метод в отдельный Spring-бин. | Когда метод находится в другом бине, Spring инжектирует прокси этого бина, и вызов проходит через прокси. Это архитектурно правильное решение, не требующее хаков вроде self-injection.
> - [ ] Проблема self-invocation автоматически решается при использовании CGLIB вместо JDK Proxy. | Оба механизма создают прокси-обёртку вокруг объекта. При вызове this.method() внутри объекта обращение идёт к реальному объекту, а не к прокси — независимо от типа прокси.

## Q6. На каких бинах Spring AOP не работает?

1. **Объекты не из Spring-контекста** — `new MyService()` (нет прокси)
2. **`final`-классы** — CGLIB не может создать подкласс
3. **`final`-методы** — CGLIB не может переопределить
4. **`private`-методы** — не видны прокси
5. **`static`-методы** — не перехватываются
6. **Конструкторы** — Spring AOP не перехватывает (только AspectJ)
7. **`@Bean` вне Spring-контекста** — если бин создан через `new`

```java
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example..*(..))")
    public void log() { }
}

// НЕ СРАБОТАЕТ:
UserService service = new UserService();  // не Spring bean
service.doSomething();

// СРАБОТАЕТ:
@Autowired UserService service;  // Spring-прокси
service.doSomething();
```

> [!mcq]
> - [ ] Spring AOP перехватывает вызовы private-методов внутри Spring-бина через CGLIB. | Spring AOP не перехватывает private-методы даже при CGLIB. Прокси работает только с методами, которые можно переопределить в подклассе — private-методы не видны подклассу.
> - [ ] Spring AOP работает на любом объекте, созданном через new, если он помечен @Component. | Аннотация @Component сама по себе не делает объект Spring-бином. Чтобы AOP работал, объект должен быть создан и управляться Spring-контейнером — только тогда Spring создаст прокси.
> - [x] Spring AOP не работает на static-методах, final-классах и private-методах. | Static-методы не перехватываются прокси. Final-классы и final-методы нельзя расширить через CGLIB. Private-методы не видны из прокси-подкласса. Для всего этого нужен AspectJ с bytecode weaving.
> - [ ] Spring AOP перехватывает конструкторы при включённом proxyTargetClass = true. | proxyTargetClass = true лишь заставляет Spring использовать CGLIB вместо JDK Proxy. Конструкторы не перехватываются Spring AOP ни при каких настройках — это ограничение runtime-прокси подхода.

## Q7. Как объявить аспект в Spring?

```java
@Aspect              // говорит Spring, что это аспект
@Component           // регистрирует как Spring-бин
public class LoggingAspect {

    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Calling: {}", joinPoint.getSignature().getName());
    }
}
```

Минимальные требования:
1. `@Aspect` — маркер для AspectJ annotation processor
2. `@Component` (или другой стереотип) — чтобы Spring создал бин
3. Хотя бы один advice-метод с pointcut

Без `@Component` (или аналога) Spring не увидит аспект. Без `@Aspect` Spring создаст бин, но не обработает его как аспект.

> [!mcq]
> - [ ] Аннотации @Aspect достаточно, чтобы Spring создал аспектный бин и применил его. | @Aspect только маркирует класс как аспект для AspectJ annotation processor. Без стереотипа (@Component, @Service и т.п.) Spring не создаст бин и аспект не будет применён.
> - [x] Аспект должен иметь и @Aspect, и @Component (или другой стереотип) — оба обязательны. | @Component (или аналог) нужен, чтобы Spring создал бин. @Aspect сообщает Spring, что этот бин — аспект, который нужно обработать и применить как прокси-interceptor. Оба требуются.
> - [ ] Аннотация @Component достаточна — Spring сам поймёт, что класс с pointcut-методами это аспект. | Spring не делает автоматического вывода "аспект по pointcut-методам". Без явного @Aspect класс будет обычным бином, и его @Before / @Around методы не будут применяться.
> - [ ] Аспект должен расширять AbstractAspect или реализовать интерфейс Aspect. | В Spring AOP нет такого базового класса или интерфейса. Аспект — это обычный класс с аннотацией @Aspect (из AspectJ) и стереотипом Spring (@Component).

## Q8. Что такое `@EnableAspectJAutoProxy` и когда он нужен?

`@EnableAspectJAutoProxy` активирует обработку `@Aspect`-бинов и создание прокси.

```java
@Configuration
@EnableAspectJAutoProxy
public class AppConfig { }

// Параметры:
@EnableAspectJAutoProxy(
    proxyTargetClass = true,  // всегда CGLIB (не только JDK proxy)
    exposeProxy = true        // позволяет использовать AopContext.currentProxy()
)
```

**В Spring Boot:** `@EnableAspectJAutoProxy` добавляется автоматически через `spring-boot-autoconfigure` при наличии `spring-aop` в classpath (через `AopAutoConfiguration`). В Spring Boot его можно не писать явно.

В plain Spring (без Boot): необходим явно или через `<aop:aspectj-autoproxy/>` в XML.

> [!mcq]
> - [ ] В Spring Boot нужно явно добавлять @EnableAspectJAutoProxy на главный класс. | Spring Boot автоматически регистрирует @EnableAspectJAutoProxy через AopAutoConfiguration при наличии spring-aop в classpath. Писать его явно не требуется — @Aspect-бины обработаются сами.
> - [x] @EnableAspectJAutoProxy активирует обработку @Aspect-бинов и создание прокси вокруг целевых объектов. | Аннотация регистрирует AnnotationAwareAspectJAutoProxyCreator, который сканирует все @Aspect-бины и создаёт прокси для бинов, подходящих под pointcut-выражения. Без неё аспекты не будут применяться.
> - [ ] Параметр proxyTargetClass = true заставляет Spring использовать JDK Dynamic Proxy вместо CGLIB. | Параметр работает наоборот: proxyTargetClass = true принудительно включает CGLIB, даже если бин реализует интерфейсы. Это полезно для перехвата всех public-методов, а не только интерфейсных.
> - [ ] @EnableAspectJAutoProxy нужно указывать только для аспектов, использующих @Around advice. | Аннотация не связана с типом advice — она включает обработку ЛЮБЫХ @Aspect-бинов (@Before, @After, @Around и т.д.). Без неё ни один тип advice не будет применяться.

## Q9. Как работает `execution` pointcut-выражение?

`execution` — самый распространённый designator. Синтаксис:

```
execution([modifier] return-type [class-pattern.]method-name(params) [throws exceptions])
```

```java
// Все публичные методы
execution(public * *(..))

// Все методы в пакете service (не рекурсивно)
execution(* com.example.service.*.*(..))

// Все методы в пакете и подпакетах
execution(* com.example.service..*.*(..))

// Метод с конкретным именем и любыми параметрами
execution(* com.example.OrderService.create(..))

// Метод с конкретными параметрами
execution(* com.example.OrderService.create(OrderRequest, Principal))

// Метод, возвращающий Order
execution(Order com.example..*.*(..) )

// Все методы интерфейса
execution(* com.example.OrderService+.*(..))  // + означает includes subtype
```

**Wildcards:**

| Символ | Значение |
|---|---|
| `*` | Один сегмент (один компонент пути / один тип) |
| `..` | Любое количество пакетов (в package path) или параметров (в args) |
| `+` | Тип + все подтипы |

> [!mcq]
> - [ ] Выражение `execution(* com.example.service.*.*(..))` перехватывает методы во всех подпакетах пакета service. | Одна звёздочка `*` в позиции пакета означает ровно один уровень. Для рекурсивного захвата подпакетов нужно `..`: `execution(* com.example.service..*.*(..))`.
> - [x] Выражение `execution(* com.example.service..*.*(..))` перехватывает методы во всех подпакетах пакета service. | Двойная точка `..` в позиции пакета означает "любое количество уровней пакетов". Это выражение захватит все классы в `com.example.service` и во всех его вложенных пакетах.
> - [ ] Выражение `execution(* com.example.service.*.*(..))` перехватывает методы только в классе `service`. | Это выражение перехватывает методы в классах, находящихся непосредственно в пакете `com.example.service`, но не в подпакетах. Шаблон `*.*(..)` означает "любой класс в пакете, любой метод с любыми параметрами".
> - [ ] Символ `+` в pointcut-выражении означает "один или более методов в классе". | Символ `+` в execution-выражении означает "тип и все его подтипы". Например, `OrderService+` захватит `OrderService` и все классы, которые его реализуют или расширяют.

## Q10. Какие designators есть в Spring AOP?

| Designator | Описание | Пример |
|---|---|---|
| `execution` | По сигнатуре метода | `execution(* save*(..))` |
| `within` | Все методы класса/пакета | `within(com.example.service.*)` |
| `this` | Прокси реализует тип | `this(OrderService)` |
| `target` | Целевой объект является типом | `target(OrderService)` |
| `args` | Аргументы определённого типа | `args(OrderRequest,..)` |
| `@annotation` | Метод помечен аннотацией | `@annotation(Loggable)` |
| `@within` | Класс помечен аннотацией | `@within(Service)` |
| `@target` | Целевой объект аннотирован | `@target(Repository)` |
| `@args` | Аргумент аннотирован | `@args(Validated)` |
| `bean` | По имени Spring-бина (Spring only) | `bean(orderService)` |

```java
// Перехватить только методы классов с @Service
@Before("@within(org.springframework.stereotype.Service)")
public void onServiceMethod(JoinPoint jp) { }

// Передать аннотацию как параметр advice
@Before("@annotation(loggable)")
public void onAnnotated(JoinPoint jp, Loggable loggable) {
    // loggable.level() доступно
}
```

> [!mcq]
> - [ ] Designator `within` отбирает методы по сигнатуре, аналогично `execution`. | Designator within отбирает по принадлежности к классу или пакету, а не по сигнатуре метода. Для отбора по сигнатуре используется execution.
> - [ ] Designator `bean` — это стандартный designator AspectJ для отбора по имени бина. | Designator bean — это расширение Spring AOP, его нет в стандартном AspectJ. Он позволяет отбирать JoinPoint-ы по имени Spring-бина (например, bean(orderService)).
> - [x] Designator `@annotation` перехватывает методы, помеченные указанной аннотацией, а `@within` — методы в классах, помеченных аннотацией. | @annotation работает на уровне метода: целевой метод должен иметь аннотацию. @within работает на уровне класса: весь класс должен быть помечен. Разница важна при проектировании аспектов.
> - [ ] Designator `args` отбирает методы по возвращаемому типу. | args отбирает по типам аргументов метода. Например, args(OrderRequest, ..) перехватит методы, первый аргумент которых имеет тип OrderRequest. Возвращаемый тип задаётся через execution.

## Q11. Как использовать кастомную аннотацию как pointcut?

```java
// 1. Объявляем аннотацию
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timed {
    String metricName() default "";
}

// 2. Помечаем методы
@Service
public class OrderService {
    @Timed(metricName = "order.create")
    public Order create(OrderRequest req) { ... }
}

// 3. Пишем аспект
@Aspect
@Component
public class MetricsAspect {

    @Around("@annotation(timed)")
    public Object measureTime(ProceedingJoinPoint pjp, Timed timed) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            metrics.record(timed.metricName(), elapsed);
        }
    }
}
```

Ключевой момент: имя параметра в `@annotation(timed)` должно совпадать с именем параметра в advice-методе.

> [!mcq]
> - [ ] Кастомная аннотация для pointcut должна иметь @Retention(SOURCE). | С @Retention(SOURCE) аннотация исчезает после компиляции и не видна в runtime. Spring AOP работает в runtime, поэтому нужна @Retention(RUNTIME), иначе pointcut не сработает.
> - [x] Чтобы получить саму аннотацию как параметр advice, имя в выражении `@annotation(x)` должно совпадать с именем параметра метода. | Spring связывает аннотацию, найденную на перехваченном методе, с параметром advice по имени. Если написать @annotation(timed) и метод принимает параметр Timed timed, аннотация будет передана туда.
> - [ ] Аннотация для pointcut должна расширять интерфейс java.lang.annotation.Aspect. | Такого интерфейса не существует. Любая обычная Java-аннотация с @Retention(RUNTIME) и правильным @Target может использоваться как pointcut через @annotation-designator.
> - [ ] Pointcut по аннотации работает только если аннотация помечена @Inherited. | @Inherited влияет на наследование аннотации между классами, а не на работу AOP. Spring AOP перехватывает методы по факту наличия аннотации, независимо от @Inherited.

## Q12. Как переиспользовать pointcut-выражение через `@Pointcut`?

`@Pointcut` позволяет именовать pointcut и ссылаться на него из нескольких advice:

```java
@Aspect
@Component
public class ServiceAspect {

    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceLayer() { }  // тело пустое, нужен только метод-маркер

    @Pointcut("@annotation(com.example.annotation.Audited)")
    public void auditedMethod() { }

    @Pointcut("serviceLayer() && auditedMethod()")  // комбинирование
    public void auditedServiceMethod() { }

    @Before("serviceLayer()")
    public void beforeService(JoinPoint jp) { log.debug("Service method: {}", jp.getSignature()); }

    @Around("auditedServiceMethod()")
    public Object audit(ProceedingJoinPoint pjp) throws Throwable {
        // ...
        return pjp.proceed();
    }
}
```

Операторы для комбинирования: `&&` (and), `||` (or), `!` (not).

> [!mcq]
> - [ ] Метод, помеченный @Pointcut, должен возвращать boolean с логикой проверки. | Метод @Pointcut должен возвращать void и иметь пустое тело — это лишь маркер для имени pointcut-выражения. Сама логика отбора описана в строке-аргументе аннотации.
> - [x] @Pointcut-методы позволяют именовать и переиспользовать pointcut-выражения, комбинируя через &&, ||, !. | Именованные pointcut-методы можно ссылаться из @Before, @Around и других advice, а также комбинировать: `serviceLayer() && auditedMethod()`. Это DRY-подход для сложных условий.
> - [ ] Pointcut-выражения нельзя переиспользовать между разными классами-аспектами. | Можно ссылаться на @Pointcut из другого класса по полному имени: `com.example.CommonPointcuts.serviceLayer()`. Это удобно для централизации pointcut-ов в отдельном классе.
> - [ ] Оператор `&` в pointcut означает "и", а `|` — "или". | В pointcut-выражениях используются двойные операторы: `&&` (и), `||` (или), `!` (не). Одиночные `&` и `|` не поддерживаются синтаксисом AspectJ.

## Q13. Какие типы Advice есть в Spring AOP?

| Тип | Аннотация | Когда выполняется | Может изменить результат? |
|---|---|---|---|
| Before | `@Before` | До метода | Нет |
| AfterReturning | `@AfterReturning` | После успешного возврата | Да (может изменить returning-объект) |
| AfterThrowing | `@AfterThrowing` | При выбросе исключения | Нет (но может обёртывать) |
| After (finally) | `@After` | Всегда (finally) | Нет |
| Around | `@Around` | Оборачивает вызов | Да (полный контроль) |

```java
@Before("serviceLayer()")
public void before() { }

@AfterReturning(pointcut = "serviceLayer()", returning = "result")
public void afterReturning(Object result) { }

@AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
public void afterThrowing(Exception ex) { }

@After("serviceLayer()")
public void afterFinally() { }  // выполнится даже при исключении
```

**Порядок при одном JoinPoint:** `@Around(before) → @Before → метод → @Around(after) → @AfterReturning / @AfterThrowing → @After`

> [!mcq]
> - [ ] @After выполняется только при успешном завершении метода, аналогично @AfterReturning. | @After — аналог finally-блока: выполняется всегда, независимо от того, было ли исключение. @AfterReturning выполняется только при успешном возврате без исключения.
> - [ ] @AfterThrowing может подавить исключение и вернуть нормальный результат. | @AfterThrowing не может подавить исключение или изменить поток выполнения. Для этого нужен @Around, который может перехватить исключение и вернуть значение вместо броска.
> - [x] @After выполняется всегда — и при успехе, и при исключении, аналогично finally-блоку. | @After гарантированно выполнится после метода независимо от результата. Это поведение аналогично Java-блоку finally и полезно для освобождения ресурсов или завершающей логики.
> - [ ] @Around не может изменить возвращаемое значение метода. | @Around имеет полный контроль над выполнением: может изменить аргументы через pjp.proceed(newArgs), изменить возвращаемое значение, подавить исключение или вообще не вызывать реальный метод.

## Q14. Как работает `@Around` advice?

`@Around` — самый мощный тип: полностью оборачивает вызов метода.

```java
@Around("execution(* com.example.service.*.*(..))")
public Object around(ProceedingJoinPoint pjp) throws Throwable {
    String method = pjp.getSignature().toShortString();
    log.info("Before: {}", method);

    Object result;
    try {
        result = pjp.proceed();          // вызов целевого метода
        log.info("After returning: {} → {}", method, result);
    } catch (Exception e) {
        log.error("Exception in: {}", method, e);
        throw e;  // перебросить — иначе исключение "проглотится"
    }

    return result;  // ОБЯЗАТЕЛЬНО вернуть результат
}
```

**Ключевые моменты:**
- `pjp.proceed()` — вызывает следующий advice или реальный метод
- `pjp.proceed(args)` — вызов с изменёнными аргументами
- Если не вызвать `proceed()` — реальный метод не выполнится
- Если не вернуть результат — вызывающий код получит `null`
- Метод должен объявлять `throws Throwable`

> [!mcq]
> - [ ] Если @Around advice не вызывает pjp.proceed(), Spring автоматически вызовет реальный метод. | Spring не вызывает реальный метод автоматически. Если @Around advice не вызвал pjp.proceed(), реальный метод не выполнится вовсе — вызывающий код получит то, что вернул advice.
> - [x] Если @Around advice не вызывает pjp.proceed(), реальный метод не выполнится. | Именно @Around advice отвечает за вызов реального метода через pjp.proceed(). Это даёт полный контроль: можно не вызывать метод (кэшированный ответ), изменить аргументы или результат.
> - [ ] @Around advice всегда должен объявлять возвращаемый тип void. | @Around advice должен возвращать Object, так как он может изменить результат метода. Тип void не позволит вернуть результат вызывающему коду — тот получит null.
> - [ ] pjp.proceed() можно вызвать только один раз внутри @Around advice. | pjp.proceed() можно вызвать несколько раз, что используется в retry-логике. Каждый вызов выполняет реальный метод (или следующий advice в цепочке).

## Q15. Что такое `JoinPoint` и `ProceedingJoinPoint`?

`JoinPoint` — объект с информацией о перехваченном вызове:

```java
@Before("serviceLayer()")
public void before(JoinPoint jp) {
    jp.getSignature();           // Signature метода (имя, тип, класс)
    jp.getArgs();                // Object[] — аргументы метода
    jp.getTarget();              // Целевой объект
    jp.getThis();                // Прокси-объект
    jp.getKind();                // "method-execution", ...

    // Для более детальной информации:
    MethodSignature sig = (MethodSignature) jp.getSignature();
    sig.getMethod();             // java.lang.reflect.Method
    sig.getReturnType();
    sig.getParameterNames();
}
```

`ProceedingJoinPoint` — расширение `JoinPoint` для `@Around`: добавляет `proceed()`.

```java
@Around("serviceLayer()")
public Object around(ProceedingJoinPoint pjp) throws Throwable {
    // pjp — всё что в JoinPoint +
    pjp.proceed();         // вызвать метод с оригинальными args
    pjp.proceed(newArgs);  // вызвать метод с изменёнными args
}
```

`ProceedingJoinPoint` доступен **только** в `@Around`.

> [!mcq]
> - [ ] ProceedingJoinPoint можно использовать как параметр в @Before и @After advice. | ProceedingJoinPoint доступен ТОЛЬКО в @Around, потому что только он обладает методом proceed(). В @Before / @After используется обычный JoinPoint без возможности продолжить или изменить вызов.
> - [x] ProceedingJoinPoint расширяет JoinPoint и добавляет метод proceed() для вызова целевого метода. | ProceedingJoinPoint наследует всю информацию из JoinPoint (signature, args, target, this) и добавляет proceed() и proceed(args). Это делает его пригодным только для @Around, где advice контролирует выполнение целевого метода.
> - [ ] JoinPoint.getThis() возвращает целевой объект, а getTarget() — прокси. | Логика обратная: getTarget() возвращает целевой (реальный) объект, getThis() — прокси-объект (через который пришёл вызов). Это важно при работе с self-invocation.
> - [ ] pjp.proceed(newArgs) может изменить только количество аргументов, но не их значения. | pjp.proceed(Object[] args) передаёт новый массив аргументов целевому методу. Количество и типы должны соответствовать сигнатуре, но значения могут быть полностью изменены — это используется для sanitizing или normalization.

## Q16. Как получить данные о перехваченном методе внутри advice?

```java
@Around("@annotation(loggable)")
public Object logMethod(ProceedingJoinPoint pjp, Loggable loggable) throws Throwable {
    MethodSignature sig = (MethodSignature) pjp.getSignature();
    Method method = sig.getMethod();

    // Параметры и аргументы
    String[] paramNames = sig.getParameterNames();
    Object[] args = pjp.getArgs();
    for (int i = 0; i < paramNames.length; i++) {
        log.debug("  {} = {}", paramNames[i], args[i]);
    }

    // Аннотации метода
    Loggable annotation = method.getAnnotation(Loggable.class);

    // Тип возврата
    Class<?> returnType = sig.getReturnType();

    // Имя класса
    String className = pjp.getTarget().getClass().getSimpleName();

    return pjp.proceed();
}
```

Для доступа к `parameterNames` нужно компилировать с `-parameters` флагом (или Spring Boot делает это автоматически).

> [!mcq]
> - [ ] Имена параметров метода всегда доступны через MethodSignature.getParameterNames() без настроек компиляции. | Имена параметров доступны только при компиляции с флагом -parameters. Без него MethodSignature.getParameterNames() вернёт arg0, arg1 и т.д. Spring Boot по умолчанию добавляет этот флаг.
> - [x] Для получения java.lang.reflect.Method нужно привести Signature к MethodSignature и вызвать getMethod(). | JoinPoint.getSignature() возвращает общий Signature. Для доступа к Method (для чтения аннотаций, типов параметров) нужно привести к MethodSignature — это стандартный приём в advice.
> - [ ] pjp.getArgs() возвращает неизменяемый список аргументов метода. | getArgs() возвращает обычный массив Object[], но изменение его элементов не повлияет на целевой метод — для этого нужно передать новый массив в pjp.proceed(newArgs).
> - [ ] pjp.getTarget().getClass() всегда возвращает класс прокси, а не реальный класс. | getTarget() возвращает именно целевой (реальный) объект, поэтому getClass() даст настоящий класс. Если нужен класс прокси — используйте getThis().getClass().

## Q17. Как управлять порядком выполнения аспектов?

Если несколько аспектов применяются к одному JoinPoint, их порядок определяется через `@Order`:

```java
@Aspect
@Component
@Order(1)   // меньше число = выше приоритет = "снаружи" оборачивает
public class SecurityAspect { }

@Aspect
@Component
@Order(2)
public class TransactionAspect { }

@Aspect
@Component
@Order(3)
public class LoggingAspect { }
```

```
Входящий вызов → Security(1) → Transaction(2) → Logging(3) → Метод
                                                              ↑
Возврат        ← Security(1) ← Transaction(2) ← Logging(3) ←
```

Аспект с меньшим `@Order` стоит "снаружи" — его `@Before` выполняется раньше, а `@After`/`@AfterReturning` — позже.

Без явного `@Order` порядок не определён. `Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE`.

> [!mcq]
> - [ ] Аспект с @Order(1) выполняет @Before последним среди всех аспектов. | Аспект с меньшим числом в @Order имеет более высокий приоритет и стоит "снаружи" цепочки. Его @Before выполняется первым, а @AfterReturning — последним.
> - [x] Аспект с @Order(1) выполняет @Before первым — он находится "снаружи" цепочки аспектов. | @Order(1) означает наивысший приоритет среди аспектов. Такой аспект оборачивает остальные: его @Before выполняется до других аспектов, а @After/@AfterReturning — после всех остальных.
> - [ ] Без явного @Order аспекты выполняются в алфавитном порядке имён классов. | Без явного @Order порядок выполнения аспектов не определён. Не следует полагаться на какой-либо подразумеваемый порядок — всегда используйте @Order там, где важна последовательность.
> - [ ] Аспект с @Order(Integer.MAX_VALUE) выполняет @Before первым. | Integer.MAX_VALUE — наименьший приоритет (самый внутренний). Наивысший приоритет у Integer.MIN_VALUE (Ordered.HIGHEST_PRECEDENCE), что соответствует @Order(Integer.MIN_VALUE) или @Order(1) при сравнении с другими.

## Q18. Как реализовать логирование через AOP?

```java
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.example..service.*.*(..))")
    public void serviceMethods() { }

    @Around("serviceMethods()")
    public Object logCall(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();

        log.debug("→ {} args={}", method, Arrays.toString(args));
        long start = System.nanoTime();

        try {
            Object result = pjp.proceed();
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            log.debug("← {} result={} [{}ms]", method, result, elapsed);
            return result;
        } catch (Exception e) {
            log.error("✗ {} threw {}", method, e.getMessage());
            throw e;
        }
    }
}
```

**Осторожно:** не логировать пароли, токены, чувствительные данные в аргументах. Используйте `@JsonIgnore`-like маркеры или фильтруйте по типу параметра/аннотации.

> [!mcq]
> - [ ] Для логирования аргументов и результата достаточно @Before advice. | @Before не видит результат метода — он вызывается ДО выполнения. Для логирования и входа, и выхода нужен @Around (с try/catch) или пара @Before + @AfterReturning.
> - [x] Логирующий аспект на @Around позволяет залогировать входные аргументы, результат, длительность и исключения в одном месте. | @Around оборачивает весь вызов: можно залогировать args до pjp.proceed(), замерить время через System.nanoTime(), залогировать result или exception. Это удобнее, чем разносить по нескольким advice.
> - [ ] При логировании следует всегда выводить все аргументы метода как есть. | Нельзя логировать чувствительные данные: пароли, токены, номера карт, персональные данные. Нужно применять фильтры или маркерные аннотации (@Sensitive) для исключения таких полей из логов.
> - [ ] Для получения имени метода в advice используется pjp.getName(). | У ProceedingJoinPoint нет метода getName(). Имя метода извлекается через pjp.getSignature().getName() или pjp.getSignature().toShortString() для более компактной записи.

## Q19. Как реализовать кастомный retry через AOP?

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    int attempts() default 3;
    Class<? extends Throwable>[] on() default {RuntimeException.class};
    long delayMs() default 100;
}

@Aspect
@Component
@Slf4j
public class RetryAspect {

    @Around("@annotation(retry)")
    public Object withRetry(ProceedingJoinPoint pjp, Retry retry) throws Throwable {
        Throwable lastException = null;
        for (int i = 0; i < retry.attempts(); i++) {
            try {
                return pjp.proceed();
            } catch (Throwable e) {
                if (!isRetryable(e, retry.on())) throw e;
                lastException = e;
                log.warn("Retry {}/{} for {} after {}ms",
                    i + 1, retry.attempts(), pjp.getSignature().getName(), retry.delayMs());
                Thread.sleep(retry.delayMs());
            }
        }
        throw lastException;
    }

    private boolean isRetryable(Throwable e, Class<? extends Throwable>[] types) {
        return Arrays.stream(types).anyMatch(t -> t.isInstance(e));
    }
}
```

> [!mcq]
> - [ ] Retry-аспект можно реализовать через @Before advice, вызывая метод повторно при исключении. | @Before не имеет возможности перехватить исключение или повторно вызвать метод — это однократный перехват до вызова. Для retry нужен @Around с циклом и pjp.proceed() внутри try/catch.
> - [x] Retry через AOP реализуется @Around advice с циклом, в котором вызывается pjp.proceed() в try/catch. | @Around позволяет перехватить исключение, проверить его тип (retryable?), сделать задержку и снова вызвать pjp.proceed(). Если все попытки провалены — пробрасываем последнее исключение.
> - [ ] Для retry обязательно использовать Spring Retry, своя реализация через AOP невозможна. | Spring Retry — удобная готовая библиотека, но retry-логику можно реализовать своим @Around advice. Это типичный пример применения AOP и часто встречается в интервью-задачах.
> - [ ] В retry-аспекте pjp.proceed() нужно вызывать только один раз, иначе будет дублирование. | Именно многократный вызов pjp.proceed() внутри цикла даёт повторные попытки. Каждый вызов выполняет целевой метод заново. Это легитимный и единственный способ реализовать retry через @Around.

## Q20. Как измерить время выполнения метода через AOP?

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timed {
    String value() default "";
}

@Aspect
@Component
@RequiredArgsConstructor
public class TimingAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(timed)")
    public Object time(ProceedingJoinPoint pjp, Timed timed) throws Throwable {
        String name = timed.value().isEmpty()
            ? pjp.getSignature().toShortString()
            : timed.value();

        return Timer.builder(name)
            .register(meterRegistry)
            .record(() -> {
                try { return pjp.proceed(); }
                catch (Throwable e) { throw new RuntimeException(e); }
            });
    }
}
```

В реальных проектах используют `@Timed` из Micrometer — он работает аналогично, но интегрирован с Spring Boot Actuator.

> [!mcq]
> - [ ] Замер времени через @Before и @After обеспечит корректный расчёт длительности метода. | В раздельных @Before и @After сложно корректно передать start между advice — нужен ThreadLocal или подобное. @Around — естественный выбор: start и end находятся в одном методе.
> - [x] Измерение времени удобнее реализовать @Around advice — start до pjp.proceed(), end после, в finally-блоке. | @Around даёт естественную структуру: зафиксировать System.nanoTime() до вызова, вызвать pjp.proceed(), в finally посчитать разницу. Это гарантирует запись метрики даже при исключении.
> - [ ] Для измерения времени в Spring Boot всегда нужно писать кастомный AOP-аспект. | В Spring Boot есть готовая аннотация @Timed из Micrometer, интегрированная с Actuator. Она покрывает большинство случаев без написания своего аспекта. Кастомный @Timed пишут только при специфических требованиях.
> - [ ] System.currentTimeMillis() точнее System.nanoTime() для замера длительности методов. | System.nanoTime() предназначен именно для измерения интервалов — он монотонный и имеет наносекундную точность. currentTimeMillis() может прыгать при NTP-синхронизации и имеет миллисекундную точность.

## Q21. Как Spring Transactions используют AOP под капотом?

`@Transactional` — это именно `@Around`-аспект. Упрощённо:

```
@Transactional на методе
        ↓
Spring создаёт прокси
        ↓
TransactionInterceptor (реализует MethodInterceptor)
        ↓
@Around advice:
  1. Открыть транзакцию (begin)
  2. pjp.proceed() — выполнить метод
  3. Если ok → commit
  4. Если RuntimeException → rollback
  5. Если CheckedException → commit (по умолчанию!)
```

```java
// Под капотом похоже на это:
@Around("@annotation(Transactional)")
public Object handleTransaction(ProceedingJoinPoint pjp) throws Throwable {
    TransactionStatus tx = txManager.getTransaction(txDef);
    try {
        Object result = pjp.proceed();
        txManager.commit(tx);
        return result;
    } catch (RuntimeException e) {
        txManager.rollback(tx);
        throw e;
    }
}
```

Отсюда и следует проблема self-invocation с `@Transactional`: вызов `this.save()` не проходит через прокси → транзакция не создаётся.

> [!mcq]
> - [ ] @Transactional реализован через @Before advice, который открывает транзакцию перед методом. | @Transactional реализован через @Around advice (TransactionInterceptor), который оборачивает метод полностью: открывает транзакцию, вызывает метод и фиксирует или откатывает результат.
> - [ ] @Transactional по умолчанию откатывает транзакцию при любом исключении, включая checked. | По умолчанию @Transactional откатывает только при RuntimeException и Error. Checked-исключения не откатывают транзакцию — это нужно настраивать явно через rollbackFor = Exception.class.
> - [x] @Transactional реализован через @Around advice, который открывает транзакцию, вызывает метод и делает commit или rollback. | TransactionInterceptor — это реализация MethodInterceptor, аналог @Around advice. Он полностью оборачивает метод: открывает транзакцию до вызова и делает commit/rollback после.
> - [ ] @Transactional реализован через @AfterReturning advice, который коммитит результат после метода. | @AfterReturning не имеет возможности открыть транзакцию до метода или откатить при исключении. @Transactional требует @Around-семантики для полного контроля над жизненным циклом транзакции.

## Q22. Как тестировать AOP-аспекты?

**Вариант 1 — Integration test (Spring-контекст):**

```java
@SpringBootTest
class LoggingAspectTest {

    @Autowired OrderService orderService;  // прокси, аспект задействован
    @SpyBean LoggingAspect aspect;         // шпионить за вызовами

    @Test
    void orderCreate_shouldBeLogged() {
        orderService.create(request);
        verify(aspect, times(1)).logCall(any());
    }
}
```

**Вариант 2 — Unit test напрямую:**

```java
@Test
void logMethod_shouldLogCallInfo() throws Throwable {
    LoggingAspect aspect = new LoggingAspect();
    ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
    MethodSignature sig = mock(MethodSignature.class);

    when(pjp.getSignature()).thenReturn(sig);
    when(sig.toShortString()).thenReturn("OrderService.create(..)");
    when(pjp.proceed()).thenReturn(new Order());

    Object result = aspect.logCall(pjp);

    verify(pjp).proceed();
    assertNotNull(result);
}
```

**Вариант 3 — @AspectJTestUtils (AspectJ):**
Для сложных аспектов используют AspectJ Weaver в тестах:
`aspectOf(MyAspect.class)` дает доступ к текущему инстансу аспекта.

**Итог:** лучший подход — integration test с реальным Spring-контекстом, где аспект применяется через прокси.

> [!mcq]
> - [ ] Unit-тест с `new MyAspect()` и вызов обычного метода гарантирует проверку работы аспекта в Spring-контексте. | Создание аспекта через new не запускает Spring-прокси, поэтому unit-тест проверяет только внутреннюю логику метода-advice. Для проверки применения аспекта через прокси нужен integration-тест.
> - [x] Лучший подход — @SpringBootTest с реальным бином-целью, где аспект применяется через прокси автоматически. | В интеграционном тесте Spring создаёт прокси вокруг бина, и вызов метода проходит через advice. Это единственный способ проверить, что pointcut-выражение действительно матчит целевые методы.
> - [ ] Для unit-теста аспекта нужно создавать настоящий прокси через java.lang.reflect.Proxy вручную. | В unit-тесте аспекта достаточно замокать ProceedingJoinPoint (для @Around) и напрямую вызвать метод-advice. Ручное создание прокси переусложняет тест и не требуется.
> - [ ] В @Around advice нельзя замокать ProceedingJoinPoint — только вызвать реальный метод. | ProceedingJoinPoint — обычный интерфейс и отлично мокается через Mockito. Можно задать поведение getSignature(), proceed(), getArgs() и проверить, как advice взаимодействует с ним.

---

## See also

- [Spring Framework](spring-framework-interview.md) — IoC, DI, BeanFactory, ApplicationContext — основа, на которой работает AOP
- [Spring Boot](spring-boot-interview.md) — автоконфигурация AopAutoConfiguration, @EnableAspectJAutoProxy
- [Spring Data JPA](spring-data-jpa-interview.md) — @Transactional работает через AOP; N+1, транзакционный контекст
- [Spring Security](spring-security-interview.md) — @Secured, @PreAuthorize — security через AOP
- [Spring MVC](spring-mvc-interview.md) — обработка запросов, фильтры vs. AOP (разные уровни перехвата)
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — Proxy паттерн (JDK/CGLIB), Decorator паттерн
- [Java Annotations](../../programming-languages/java/java-annotations-interview.md) — @Aspect, @Pointcut, @Before как retention=RUNTIME аннотации
- [Java Core](../../programming-languages/java/java-core-interview.md) — java.lang.reflect.Proxy, dynamic proxy механизм
- [Unit Testing](../../testing/unit-testing-interview.md) — тестирование аспектов через mock ProceedingJoinPoint
- [Spring Boot Actuator](spring-boot-actuator-interview.md) — @Timed из Micrometer как AOP-based метрика
- [Шпаргалка: Spring AOP: Полное руководство по аспект](../../../frameworks/java-frameworks/spring/spring-aop.md) — теория
