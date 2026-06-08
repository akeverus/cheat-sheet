---
title: "Вопросы на собеседовании: Spring AOP"
description: "Spring AOP: Aspect, Advice, Pointcut, JoinPoint, прокси-механизм (JDK/CGLIB), типы advice, pointcut-выражения, проблема self-invocation, тестирование"
tags:
  - interview
  - spring
  - spring-aop-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring AOP"
  - "Spring AOP interview"
  - "Spring AOP собеседование"
prerequisites:
  - "[[spring-aop]]"
next: []
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
- [Q1. Что такое AOP и зачем он нужен?](#q1-что-такое-aop-и-зачем-он-нужен)
- [Q2. Какие ключевые термины AOP нужно знать?](#q2-какие-ключевые-термины-aop-нужно-знать)
- [Q3. Чем Spring AOP отличается от AspectJ?](#q3-чем-spring-aop-отличается-от-aspectj)

**Прокси-механизм**
- [Q4. Как Spring AOP создаёт прокси: JDK dynamic proxy vs CGLIB?](#q4-как-spring-aop-создаёт-прокси-jdk-dynamic-proxy-vs-cglib)
- [Q5. Что такое проблема self-invocation и как её обойти?](#q5-что-такое-проблема-self-invocation-и-как-её-обойти)
- [Q6. На каких бинах Spring AOP не работает?](#q6-на-каких-бинах-spring-aop-не-работает)

**Конфигурация**
- [Q7. Как объявить аспект в Spring?](#q7-как-объявить-аспект-в-spring)
- [Q8. Что такое `@EnableAspectJAutoProxy` и когда он нужен?](#q8-что-такое-enableaspectjautoproxy-и-когда-он-нужен)

**Pointcut-выражения**
- [Q9. Как работает `execution` pointcut-выражение?](#q9-как-работает-execution-pointcut-выражение)
- [Q10. Какие designators есть в Spring AOP?](#q10-какие-designators-есть-в-spring-aop)
- [Q11. Как использовать кастомную аннотацию как pointcut?](#q11-как-использовать-кастомную-аннотацию-как-pointcut)
- [Q12. Как переиспользовать pointcut-выражение через `@Pointcut`?](#q12-как-переиспользовать-pointcut-выражение-через-pointcut)

**Типы Advice**
- [Q13. Какие типы Advice есть в Spring AOP?](#q13-какие-типы-advice-есть-в-spring-aop)
- [Q14. Как работает `@Around` advice?](#q14-как-работает-around-advice)
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

**AOP (Aspect-Oriented Programming)** — парадигма программирования, которая выносит **cross-cutting concerns** (сквозную функциональность) в отдельные модули-аспекты, чтобы не размазывать её по всему коду.

Главная проблема, которую решает AOP: некоторая логика (логирование, транзакции, безопасность) нужна в десятках методов, но к бизнес-задаче этих методов отношения не имеет. Без AOP её приходится копировать в каждый метод — код раздувается, дублируется и тяжелеет в поддержке. AOP позволяет описать такую логику **один раз** в аспекте и декларативно применить её ко всем нужным методам.

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

**Итог:** AOP делает код чище, убирает дублирование и упрощает поддержку cross-cutting concerns — каждый метод занимается только своим делом, а сквозная логика живёт отдельно.

## Q2. Какие ключевые термины AOP нужно знать?

Шесть базовых понятий, на которых строится весь AOP. Удобно читать их как ответы на вопросы «где?», «что?» и «когда применяется?»:

```
JoinPoint    — конкретная точка выполнения (вызов метода)
Pointcut     — предикат: КАКИЕ JoinPoint-ы перехватывать
Advice       — ЧТО делать при совпадении Pointcut-а
Aspect       — модуль = Pointcut + Advice(s)
Weaving      — процесс применения Aspect-а к целевому объекту
Introduction — добавление нового интерфейса/поведения к классу
```

Главное запомнить связку: **Aspect = Pointcut (где перехватывать?) + Advice (что делать?)**. Pointcut отбирает точки, Advice описывает действие, а Weaving физически «вплетает» это действие в выполнение — в Spring AOP через прокси.

Аналогия с дорожным движением делает термины наглядными:

| Термин | Аналогия | Пример |
|---|---|---|
| `JoinPoint` | Перекрёсток | Вызов `orderService.create()` |
| `Pointcut` | Дорожный знак | "Все методы сервисного слоя" |
| `Advice` | Действие на знак | Логировать входные параметры |
| `Aspect` | Знак + действие вместе | Логирующий аспект |
| `Weaving` | Установка знака | Создание прокси для перехвата |

**Важное ограничение:** Spring AOP поддерживает только один тип JoinPoint — вызов метода. В отличие от AspectJ, перехватить конструктор, чтение/запись поля или статический инициализатор нельзя.

## Q3. Чем Spring AOP отличается от AspectJ?

Коротко: **Spring AOP** работает в рантайме через прокси и перехватывает только вызовы методов Spring-бинов; **AspectJ** модифицирует байткод (на этапе компиляции или загрузки класса) и может перехватывать что угодно в любом Java-классе. Spring AOP проще в настройке, но беднее по возможностям.

| Критерий | Spring AOP | AspectJ |
|---|---|---|
| Механизм | Runtime proxy | Bytecode weaving |
| JoinPoint types | Только вызов метода | Поле, конструктор, статик-блок, метод |
| Применимость | Только Spring beans | Любой Java-класс |
| Производительность | Прокси overhead | Нет overhead (compile-time) |
| Сложность setup | Простой (autoconfigure) | Нужен AspectJ compiler / agent |
| Self-invocation | Не перехватывает | Перехватывает |
| Использование синтаксиса | AspectJ annotations (но не weaver) | AspectJ annotations + weaver |

**Вывод:** Spring AOP покрывает ~90% реальных задач (логирование, транзакции, метрики на уровне сервисных методов) и не требует отдельного компилятора. AspectJ берут, когда нужно перехватывать не-Spring-объекты, поля или конструкторы, обойти проблему self-invocation или убрать overhead прокси.

## Q4. Как Spring AOP создаёт прокси: JDK dynamic proxy vs CGLIB?

Spring AOP не меняет байткод целевого класса — вместо этого он оборачивает бин в **прокси**, и все вызовы идут сначала через прокси, который запускает advice, а потом делегирует реальному объекту:

```
Клиент → Прокси-объект → [Advice срабатывает] → Целевой объект
```

Технологию прокси Spring выбирает автоматически по одному правилу: **есть интерфейс — JDK Dynamic Proxy, нет интерфейса — CGLIB**.

**JDK Dynamic Proxy** (по умолчанию, если класс реализует интерфейс):

```java
public interface OrderService { Order create(OrderRequest req); }
// Spring создаст java.lang.reflect.Proxy, реализующий OrderService
```

**CGLIB** (если у класса нет интерфейса или задан `proxyTargetClass = true`):

```java
@Service
public class UserService { ... }  // нет интерфейса → CGLIB subclass
```

CGLIB генерирует подкласс целевого класса в рантайме и переопределяет его методы, вставляя в них вызов advice. Именно из механизма «наследник + override» вытекает ключевое ограничение: **AOP не работает на `final`-классах и `final`-методах** — их нельзя унаследовать или переопределить.

С версии Spring Boot 2.x CGLIB используется по умолчанию даже при наличии интерфейса (`proxyTargetClass=true` выставлен в автоконфигурации), чтобы избежать сюрпризов с инъекцией по конкретному типу.

```java
@EnableAspectJAutoProxy(proxyTargetClass = true)  // всегда CGLIB
@Configuration
public class AppConfig { }
```

**Практическое следствие:** во все зависимости Spring внедряет именно прокси, а не реальный объект. Поле `@Autowired OrderService` ссылается на прокси, а не на `OrderServiceImpl` напрямую — поэтому advice и срабатывает на каждом вызове извне. Это же объясняет проблему self-invocation из Q5: внутренние вызовы `this.method()` прокси не проходят.

## Q5. Что такое проблема self-invocation и как её обойти?

**Суть:** advice вешается на прокси, а не на сам объект. Когда метод вызывает другой метод **того же объекта** через `this.method()`, вызов идёт напрямую внутри реального объекта, минуя прокси — и advice не срабатывает. Прокси «видит» только вызовы, приходящие снаружи.

Самый болезненный случай — `@Transactional` и `@Cacheable`: они реализованы через AOP, поэтому при self-invocation новая транзакция или кэширование просто не включаются, и баг проявляется молча.

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

**Обходные пути** (от самого чистого к самому хитрому):

1. **Инжектировать сам себя** (Spring 4.3+) — поле получает прокси, и вызов через него снова проходит advice:

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

2. **Достать текущий прокси через `AopContext.currentProxy()`** — работает, но связывает код с инфраструктурой Spring AOP:

```java
((OrderService) AopContext.currentProxy()).saveOrder(id);
// Нужно @EnableAspectJAutoProxy(exposeProxy = true)
```

3. **Переместить метод в другой бин** — лучшее архитектурное решение: вызов между разными бинами всегда идёт через прокси, проблема исчезает сама собой.

4. **Перейти на AspectJ** — он вплетает advice в байткод, поэтому перехватывает и внутренние вызовы тоже.

**Итог:** self-invocation — самая частая ловушка Spring AOP, и опаснее всего она с `@Transactional`. Предпочтительное решение — выделить метод в отдельный Spring-компонент, а не латать дыру через self-injection или `AopContext`.

## Q6. На каких бинах Spring AOP не работает?

Все ограничения вытекают из одного факта: Spring AOP — это **прокси вокруг Spring-бина, перехватывающий внешние вызовы методов**. Что не проходит через прокси или что прокси не может переопределить — то и не перехватывается:

1. **Объекты не из Spring-контекста** — созданные через `new MyService()`, у них просто нет прокси
2. **`final`-классы** — CGLIB не может создать подкласс
3. **`final`-методы** — CGLIB не может их переопределить
4. **`private`-методы** — не видны прокси (вызываются только изнутри, через `this`)
5. **`static`-методы** — не привязаны к экземпляру, прокси их не перехватывает
6. **Конструкторы** — Spring AOP перехватывает только вызовы методов (конструкторы — это уже AspectJ)
7. **Внутренние вызовы (`this.method()`)** — обходят прокси, см. проблему self-invocation в Q5

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

## Q7. Как объявить аспект в Spring?

Нужны две аннотации: `@Aspect` помечает класс как аспект, а `@Component` (или другой стереотип) регистрирует его как Spring-бин. Без второй Spring вообще не узнает о классе:

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
3. Хотя бы один advice-метод с pointcut-выражением

Почему обе аннотации обязательны: `@Aspect` сам по себе бин не создаёт, поэтому без `@Component` Spring аспект не увидит. И наоборот — без `@Aspect` Spring создаст обычный бин, но не станет обрабатывать его advice-методы. Нужны обе.

## Q8. Что такое `@EnableAspectJAutoProxy` и когда он нужен?

`@EnableAspectJAutoProxy` — это «выключатель» Spring AOP: он регистрирует постпроцессор, который сканирует `@Aspect`-бины и оборачивает целевые бины в прокси. Без него аспекты есть, но не применяются.

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

Два параметра управляют поведением прокси:
- `proxyTargetClass = true` — всегда использовать CGLIB, даже если у класса есть интерфейс.
- `exposeProxy = true` — класть текущий прокси в `ThreadLocal`, чтобы он был доступен через `AopContext.currentProxy()` (нужно для обхода self-invocation из Q5).

**Когда писать явно:**
- **Spring Boot** — обычно не нужно. `AopAutoConfiguration` сам включает авто-проксирование при наличии `spring-aop` в classpath (стартер `spring-boot-starter-aop`). Аннотацию пишут руками, только если нужно поменять параметры (например, `exposeProxy = true`).
- **Plain Spring без Boot** — нужно обязательно: либо `@EnableAspectJAutoProxy`, либо `<aop:aspectj-autoproxy/>` в XML. Иначе аспекты молча не сработают.

## Q9. Как работает `execution` pointcut-выражение?

`execution` — самый распространённый designator: он отбирает методы по их сигнатуре (модификатор, тип возврата, пакет/класс, имя, параметры). Читается выражение как «шаблон сигнатуры», где звёздочки и `..` — это wildcards.

```
execution([modifier] return-type [class-pattern.]method-name(params) [throws exceptions])
```

Каждую часть можно заменить на `*` (любое значение этого сегмента). Разберём на примерах от общего к частному:

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

**Wildcards** — три символа, которые нужно знать наизусть:

| Символ | Значение |
|---|---|
| `*` | Один сегмент (один компонент пути / один тип) |
| `..` | Любое количество пакетов (в package path) или параметров (в args) |
| `+` | Тип + все подтипы |

**Ловушка на собеседовании:** разница между `.*` (методы прямо в пакете) и `..*` (методы в пакете и всех подпакетах) — частый источник промахов, когда аспект «не ловит» нужные методы.

## Q10. Какие designators есть в Spring AOP?

Designator (указатель) — это «тип» pointcut-выражения. Их можно разбить на две группы: отбирающие методы по **сигнатуре/расположению** (`execution`, `within`, `this`, `target`, `args`, `bean`) и по **аннотациям** (`@annotation`, `@within`, `@target`, `@args`). Несколько designator-ов комбинируются операторами `&&`, `||`, `!`.

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

Особенно полезен трюк с annotation-designator-ами: имя аннотации можно «связать» с параметром advice и получить доступ к её атрибутам прямо внутри метода:

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

На практике чаще всего используют `execution` (по слою/пакету) и `@annotation` (по своей аннотации) — остальные нужны реже.

## Q11. Как использовать кастомную аннотацию как pointcut?

Это самый чистый способ применять аспект адресно: вместо хрупкого `execution`-шаблона по пакетам вы помечаете нужные методы своей аннотацией, а pointcut `@annotation(...)` ловит именно их. Три шага: объявить аннотацию с `RUNTIME`-retention, повесить её на методы, написать аспект.

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

**Ключевой момент:** имя в `@annotation(timed)` должно совпадать с именем параметра `Timed timed` в advice-методе — по этому совпадению Spring и связывает аннотацию с параметром, передавая её экземпляр внутрь. Так вы получаете доступ к её атрибутам (здесь — `timed.metricName()`). Аннотация обязана иметь `@Retention(RUNTIME)`, иначе в рантайме её просто не будет видно.

## Q12. Как переиспользовать pointcut-выражение через `@Pointcut`?

`@Pointcut` выносит выражение в отдельный именованный метод-маркер, чтобы не дублировать одну и ту же строку в каждом advice. Это даёт переиспользование, осмысленные имена и возможность комбинировать pointcut-ы между собой через `&&`, `||`, `!`:

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

Тело метода-маркера всегда пустое — он нужен только чтобы повесить на него имя и `@Pointcut`. Ссылаться можно и на pointcut-ы из других аспектов, указав полное имя метода (`com.example.Pointcuts.serviceLayer()`).

Операторы для комбинирования: `&&` (и), `||` (или), `!` (не).

## Q13. Какие типы Advice есть в Spring AOP?

Пять типов advice отличаются моментом срабатывания относительно вызова метода и тем, насколько они могут вмешаться в результат. Первые четыре «обзорные» (просто реагируют на событие), пятый — `@Around` — единственный, кто полностью контролирует вызов:

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

**Порядок при одном JoinPoint:** `@Around(до proceed) → @Before → метод → @Around(после proceed) → @AfterReturning / @AfterThrowing → @After`

**Эмпирическое правило:** если advice только реагирует на событие (залогировать, обновить метрику) — берите подходящий «обзорный» тип; если нужно подменить аргументы/результат, поймать исключение или вовсе не вызывать метод — только `@Around`.

## Q14. Как работает `@Around` advice?

`@Around` — самый мощный тип advice: он полностью оборачивает вызов и сам решает, когда (и вызывать ли вообще) выполнить целевой метод через `pjp.proceed()`. Код до `proceed()` — это «before», код после — «after», а `try/catch` вокруг — это «throwing/finally» в одном месте. Поэтому через `@Around` реализуют транзакции, retry, кэширование, тайминги.

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

**Ключевые моменты** (и типичные ошибки):
- `pjp.proceed()` — вызывает следующий advice в цепочке или, если их больше нет, реальный метод
- `pjp.proceed(args)` — то же, но с подменёнными аргументами
- **Забыть `proceed()`** — реальный метод просто не выполнится (частая причина «метод ничего не делает»)
- **Забыть `return result`** — вызывающий код молча получит `null`
- **Проглотить исключение** (не пробросить из `catch`) — ошибка потеряется, вызывающий решит, что всё хорошо
- Сигнатура обязана объявлять `throws Throwable`, потому что `proceed()` может бросить что угодно

## Q15. Что такое `JoinPoint` и `ProceedingJoinPoint`?

Оба — это «контекст перехваченного вызова», который Spring передаёт в advice первым параметром. Разница в одной возможности: `ProceedingJoinPoint` умеет запускать целевой метод (`proceed()`), а `JoinPoint` — нет.

`JoinPoint` доступен в любом advice и даёт информацию о вызове, но управлять им не может:

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

`ProceedingJoinPoint` — наследник `JoinPoint`, добавляющий `proceed()`. Именно поэтому он применим только в `@Around`: только там advice сам отвечает за запуск метода.

```java
@Around("serviceLayer()")
public Object around(ProceedingJoinPoint pjp) throws Throwable {
    // pjp — всё что в JoinPoint +
    pjp.proceed();         // вызвать метод с оригинальными args
    pjp.proceed(newArgs);  // вызвать метод с изменёнными args
}
```

**Запомнить:** `getThis()` возвращает прокси, `getTarget()` — реальный объект под прокси; путать их — частая ошибка. А `ProceedingJoinPoint` существует **только** в `@Around`.

## Q16. Как получить данные о перехваченном методе внутри advice?

Всё начинается с `getSignature()`, который для метода нужно привести к `MethodSignature` — через него открывается доступ к рефлексии: самому `Method`, типу возврата, именам параметров. Аргументы берутся отдельно через `getArgs()`, а целевой класс — через `getTarget().getClass()`:

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

**Подводный камень:** имена параметров (`getParameterNames()`) доступны только если класс скомпилирован с флагом `-parameters` — иначе вы получите `arg0`, `arg1`. Spring Boot включает этот флаг автоматически, но в plain-проектах его легко забыть.

## Q17. Как управлять порядком выполнения аспектов?

Когда к одному методу применяются несколько аспектов, они выстраиваются в цепочку прокси-вокруг-прокси, и порядок задаётся аннотацией `@Order` (или интерфейсом `Ordered`). Правило простое: **меньше число → выше приоритет → аспект «снаружи»**, то есть его `@Before` срабатывает раньше всех, а `@After` — позже всех (как у вложенных скобок).

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

В примере выше Security оборачивает Transaction, а тот — Logging: на входе цепочка идёт от внешнего к внутреннему, на выходе — в обратном порядке.

**Подводный камень:** без явного `@Order` порядок между аспектами не определён, поэтому для зависимых аспектов (например, безопасность должна проверяться до открытия транзакции) его задают всегда. Крайние значения: `Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE` (самый внешний), `LOWEST_PRECEDENCE = Integer.MAX_VALUE`.

## Q18. Как реализовать логирование через AOP?

Классический сценарий: один `@Around`-аспект на сервисный слой логирует вход (имя метода + аргументы), выход (результат + длительность) и ошибки. Так логирование живёт в одном месте, а бизнес-методы остаются чистыми:

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

**Подводные камни:**
- **Безопасность.** `Arrays.toString(args)` слепо выведет в лог пароли, токены, персональные данные. Фильтруйте чувствительные аргументы по типу/аннотации (`@JsonIgnore`-подобные маркеры) либо вообще не логируйте `args` на чувствительных методах.
- **Уровень логирования.** Используйте `debug`, а не `info`, чтобы детальные трейсы не засоряли прод-логи; включать их по необходимости.

## Q19. Как реализовать кастомный retry через AOP?

Retry — учебный пример мощи `@Around`: advice крутит `proceed()` в цикле, перехватывает исключение, проверяет, входит ли оно в список «повторяемых», и при необходимости ждёт и пробует снова. Параметры (число попыток, типы исключений, задержка) удобно вынести в свою аннотацию:

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

**Нюанс:** не повторяемые исключения пробрасываются сразу (`if (!isRetryable(...)) throw e`), а после исчерпания попыток летит последнее пойманное. В проде вместо самописного аспекта обычно берут готовый Spring Retry (`@Retryable`) — он умеет backoff, jitter и recovery-методы, — но самописный вариант показывает, что под капотом работает ровно та же логика на `@Around`.

## Q20. Как измерить время выполнения метода через AOP?

Принцип тот же, что у retry, только проще: `@Around` засекает время вокруг `proceed()` и пишет его в метрику. Чтобы получить полноценную метрику (а не просто лог), результат отдают в `Timer` Micrometer — тогда измерение попадёт в Prometheus/Actuator:

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

**Рекомендация:** в реальных проектах свой аспект почти не пишут — берут готовый `@Timed` из Micrometer (`io.micrometer.core.annotation.Timed`). Внутри он устроен так же (AOP вокруг метода), но уже интегрирован со Spring Boot Actuator и автоматически экспортирует метрики. Самописный вариант полезен, только когда нужна нестандартная логика именования или тегирования.

## Q21. Как Spring Transactions используют AOP под капотом?

`@Transactional` — не магия, а обычный `@Around`-аспект (`TransactionInterceptor`). Прокси перехватывает вызов помеченного метода, открывает транзакцию до `proceed()`, а после — коммитит или откатывает в зависимости от исхода. Схематично:

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

Из этой реализации напрямую вытекают две классические особенности `@Transactional`:
- **Откат только на `RuntimeException`/`Error`** по умолчанию. Checked-исключение приводит к коммиту — если нужен откат, явно указывают `rollbackFor`.
- **Self-invocation не работает.** Вызов `this.save()` идёт мимо прокси (см. Q5), поэтому `TransactionInterceptor` не запускается и транзакция не создаётся. Это самая частая причина «почему `@Transactional` не сработал».

## Q22. Как тестировать AOP-аспекты?

Главный нюанс: аспект применяется только через прокси, поэтому в обычном unit-тесте, где вы создаёте сервис через `new`, advice не сработает. Отсюда два уровня тестов — интеграционный (проверяет, что аспект реально *вплетается*) и юнит (проверяет *логику* самого advice в изоляции).

**Вариант 1 — Integration test (Spring-контекст).** Берём бин из контекста — он уже обёрнут прокси, аспект задействован. Проверяем сам факт перехвата:

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

**Вариант 2 — Unit test напрямую.** Создаём аспект руками и мокаем `ProceedingJoinPoint` — прокси и контекст Spring не нужны, проверяется только логика advice (вызвал ли `proceed()`, что вернул, как обработал исключение):

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

**Вариант 3 — AspectJ Weaver в тестах.** Для сложных аспектов, которые тестируют через настоящий weaving: `aspectOf(MyAspect.class)` даёт доступ к текущему инстансу аспекта. Нужен редко.

**Итог:** оптимальная стратегия — комбинация. Integration-тест подтверждает, что аспект *срабатывает* (прокси на месте, pointcut матчит), а unit-тест на мокнутом `ProceedingJoinPoint` детально проверяет *поведение* advice без накладных расходов на Spring-контекст.

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
