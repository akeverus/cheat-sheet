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
public Order createOrder(OrderRequest request) {
    log.info("Operation started");
    return doCreate(request);
}

public Order cancelOrder(long orderId) {
    log.info("Operation started");
    return doCancel(orderId);
}
```

Общее сообщение можно записывать в advice, а в методах оставить вызовы `doCreate(request)` и `doCancel(orderId)`. Pointcut ограничивает применение advice нужными операциями.

**Итог:** AOP делает код чище, убирает дублирование и упрощает поддержку cross-cutting concerns — каждый метод занимается только своим делом, а сквозная логика живёт отдельно.

## Q2. Какие ключевые термины AOP нужно знать?

Шесть базовых понятий, на которых строится весь AOP. Удобно читать их как ответы на вопросы «где?», «что?» и «когда применяется?»:

```text
Вызов: orderService.create(request)
Правило отбора: публичные методы сервисного слоя.
Действие: записать имя метода перед выполнением.
Как называются эти три части в терминологии AOP?
```

Выполнение `create` соответствует join point, правило отбора соответствует pointcut, а запись имени метода соответствует advice. Introduction добавляет интерфейс прокси и предоставляет реализацию этого интерфейса.

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

CGLIB генерирует подкласс целевого класса в рантайме и переопределяет его методы, вставляя в них вызов advice. Именно из механизма «наследник + override» вытекает ключевое ограничение: **CGLIB не позволяет проксировать `final`-класс или перехватить `final`-метод** — их нельзя унаследовать или переопределить. JDK-прокси может перехватить вызов через интерфейс, даже когда класс реализации или сам метод объявлен `final`.

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
// Spring работает в proxy-режиме. Клиент вызвал processOrder через прокси,
// и к моменту вызова saveOrder внешняя транзакция уже открыта.
@Service
public class OrderService {
    @Transactional
    public void processOrder(Long id) {
        validateOrder(id);
        saveOrder(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrder(Long id) {
        repository.save(id);
    }
}
```

В этом примере `saveOrder(id)` вызывается на том же объекте. Перехватчик транзакций для `saveOrder` не запускается, поэтому отдельной транзакции `REQUIRES_NEW` нет. Операция сохранения выполняется в открытой внешней транзакции.

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

3. **Переместить метод в другой бин** — лучшее архитектурное решение: вызов метода через внедрённый прокси другого бина позволяет применить его транзакционные настройки.

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
// UserService зарегистрирован в Spring, логирующий аспект настроен
// на вызовы UserService.doSomething(). Оба вызова выполняются извне.
UserService manual = new UserService();
UserService managed = context.getBean(UserService.class);
manual.doSomething();
managed.doSomething();
```

При обычном создании через `new` вызов через `manual` идёт напрямую. Если Spring обернул бин в подходящий прокси, вызов через `managed` проходит через advice. Ограничения `final` относятся к CGLIB. При JDK-прокси вызов через интерфейс может быть перехвачен даже при `final`-реализации метода. [Механизмы проксирования](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html).

## Q7. Как объявить аспект в Spring?

`@Aspect` помечает класс для обработки средствами Spring AOP. Чтобы Spring обработал этот аспект, его экземпляр регистрируют как бин.

```java
// Поддержка @AspectJ включена, пакет входит в component scan.
// Класс должен обнаруживаться сканированием и участвовать в Spring AOP.
public class LoggingAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Calling {}", joinPoint.getSignature().getName());
    }
}
```

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

При component scan к `@Aspect` добавляют `@Component` или другой подходящий стереотип. При явной регистрации через фабричный метод `@Bean` достаточно оставить `@Aspect` на классе. Advice-методы описывают поведение и точки его применения. [Объявление аспекта](https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/at-aspectj.html).

## Q8. Что такое `@EnableAspectJAutoProxy` и когда он нужен?

`@EnableAspectJAutoProxy` включает обработку бинов с `@Aspect` и создание прокси для подходящих целевых бинов.

```text
A. Обычный Spring с Java-конфигурацией, без XML-настройки AOP.
B. Spring Boot с включённой AOP-автоконфигурацией и AspectJ в classpath.
В обоих приложениях зарегистрирован бин аспекта.
В каком случае нужно явно включить обработку аспектов и какие настройки она позволяет задать?
```

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
- **Spring Boot** — обычно не нужно. `AopAutoConfiguration` сам включает авто-проксирование при наличии AspectJ в classpath (стартер `spring-boot-starter-aop`). Аннотацию пишут руками, только если нужно поменять параметры (например, `exposeProxy = true`).
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
// Для вызова create через Spring-бин advice должен получить
// значение metricName, указанное в аннотации метода.
@Service
public class OrderService {
    @Timed(metricName = "order.create")
    public Order create(OrderRequest req) {
        return repository.create(req);
    }
}
```

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

    @Around(value = "@annotation(timed)", argNames = "timed")
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

`@annotation(timed)` связывает экземпляр `Timed` с параметром advice, поэтому внутри доступен `timed.metricName()`. Переменную выражения можно сопоставить параметру явно через `argNames`. В этом списке первый параметр типа `ProceedingJoinPoint` разрешено опустить. Если `argNames` отсутствует, Spring пытается определить имена по метаданным или вывести привязку из выражения и сигнатуры. Runtime retention нужна, чтобы аннотация была доступна при выполнении. Здесь `Timed` является пользовательской аннотацией. [Связывание параметров advice](https://docs.spring.io/spring-framework/reference/7.0/core/aop/ataspectj/advice.html).

## Q12. Как переиспользовать pointcut-выражение через `@Pointcut`?

`@Pointcut` выносит выражение в отдельный именованный метод-маркер, чтобы не дублировать одну и ту же строку в каждом advice. Это даёт переиспользование, осмысленные имена и возможность комбинировать pointcut-ы между собой через `&&`, `||`, `!`:

```java
@Before("execution(* com.example.service.*.*(..))")
public void beforeService(JoinPoint jp) {
    log.debug("Service method: {}", jp.getSignature());
}

@Around("execution(* com.example.service.*.*(..)) && @annotation(com.example.annotation.Audited)")
public Object audit(ProceedingJoinPoint jp) throws Throwable {
    return auditCall(jp);
}
```

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
| Before | `@Before` | До метода | Может остановить вызов, выбросив исключение |
| AfterReturning | `@AfterReturning` | После успешного возврата | Может изменить состояние доступного mutable-объекта |
| AfterThrowing | `@AfterThrowing` | При выбросе исключения | Нет (но может обёртывать) |
| After (finally) | `@After` | Всегда (finally) | Нет |
| Around | `@Around` | Оборачивает вызов | Да (полный контроль) |

```text
На один метод совпали пять advice одного аспекта. Каждый записывает
своё событие, а внешняя обёртка отмечает вход и выход в finally.
A. Метод возвращает изменяемый объект Result.
B. Метод выбрасывает IllegalStateException.
В каком порядке появятся события A и B? Как заменить возвращаемую ссылку?
```

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

**Порядок для разных типов advice одного аспекта.** При нормальном возврате порядок такой: вход в `@Around`, `@Before`, метод, `@AfterReturning`, `@After`, выход из `@Around`. При исключении метода вместо `@AfterReturning` выполняется подходящий `@AfterThrowing`. Исключение из вложенной цепочки выходит из `proceed()`, поэтому код после вызова на успешном пути пропускается, а `finally` во внешнем advice выполняется. `@AfterReturning` получает доступ к возвращённому объекту и может изменить его состояние. Чтобы вернуть вызывающему другую ссылку, используют `@Around`. [Порядок advice](https://docs.spring.io/spring-framework/reference/7.0/core/aop/ataspectj/advice.html#_advice_ordering).

**Эмпирическое правило:** если advice только реагирует на событие (залогировать, обновить метрику) — берите подходящий «обзорный» тип; если нужно подменить аргументы/результат, поймать исключение или вовсе не вызывать метод — только `@Around`.

## Q14. Как работает `@Around` advice?

`@Around` — самый мощный тип advice: он полностью оборачивает вызов и сам решает, когда (и вызывать ли вообще) выполнить целевой метод через `pjp.proceed()`. Код до `proceed()` — это «before», код после — «after», а `try/catch` вокруг — это «throwing/finally» в одном месте. Поэтому через `@Around` реализуют транзакции, retry, кэширование, тайминги.

```text
Два вызова catalog.find(42) проходят через один аспект.
Для первого вызова в кэше есть Product, для второго записи нет.
При попадании репозиторий трогать нельзя. При промахе нужно выполнить
обычный поиск и вернуть его результат либо дать исключению выйти наружу.
Как построить advice и что увидит вызывающий код в каждом случае?
```

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
- Если advice возвращает `null` вместо результата, вызывающий код получит `null` при ссылочном типе возврата
- **Проглотить исключение** (не пробросить из `catch`) — ошибка потеряется, вызывающий решит, что всё хорошо
- Если advice пропускает исключения из `proceed()` без обработки, его сигнатура обычно объявляет `throws Throwable`

В сценарии с кэшем advice может вернуть сохранённый `Product` сразу. При промахе он выполняет `proceed()` и возвращает результат вложенной цепочки. При исключении исходный сбой можно пропустить наружу.

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
// Клиент обращается к Spring-бину через JDK dynamic proxy.
interface OrderService {
    Order create(long customerId, int itemCount);
}

class DefaultOrderService implements OrderService {
    @Loggable
    public Order create(long customerId, int itemCount) {
        return new Order(customerId, itemCount);
    }
}

Order order = orderService.create(42L, 3);
```

```java
@Around("@annotation(loggable)")
public Object logMethod(ProceedingJoinPoint pjp, Loggable loggable) throws Throwable {
    MethodSignature sig = (MethodSignature) pjp.getSignature();
    Method method = AopUtils.getMostSpecificMethod(
        sig.getMethod(), pjp.getTarget().getClass());

    // Параметры и аргументы
    String[] paramNames = sig.getParameterNames();
    Object[] args = pjp.getArgs();
    if (paramNames != null) {
        log.debug("Параметры: {}", Arrays.toString(paramNames));
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

При JDK-прокси `MethodSignature.getMethod()` может вернуть метод интерфейса. Аннотация метода реализации читается с метода целевого класса, например через `AopUtils.getMostSpecificMethod`. Если экземпляр аннотации уже передан через `@annotation(loggable)`, можно использовать его напрямую.

`MethodSignature.getParameterNames()` может вернуть `null`, если Spring не нашёл метаданные имён. Для Java обычно сохраняют их с помощью `-parameters`. Синтетические имена вроде `arg0` возвращает Java Reflection через `Parameter.getName()` при отсутствии имени в метаданных. [Реализация MethodInvocationProceedingJoinPoint](https://raw.githubusercontent.com/spring-projects/spring-framework/main/spring-aop/src/main/java/org/springframework/aop/aspectj/MethodInvocationProceedingJoinPoint.java).

## Q17. Как управлять порядком выполнения аспектов?

Когда к одному методу применяются несколько аспектов, они выстраиваются в цепочку прокси-вокруг-прокси, и порядок задаётся аннотацией `@Order` (или интерфейсом `Ordered`). Правило простое: **меньше число → выше приоритет → аспект «снаружи»**, то есть его `@Before` срабатывает раньше всех, а `@After` — позже всех (как у вложенных скобок).

```text
Один вызов совпал с тремя @Around advice. Каждое выполняет вложенный
вызов один раз и пишет события до него и после успешного возврата.
LoggingAspect     @Order(3)   logging.enter, logging.exit
SecurityAspect    @Order(1)   security.enter, security.exit
TransactionAspect @Order(2)   transaction.enter, transaction.exit
Целевой метод пишет method и завершается успешно. Каков порядок событий?
```

```
Входящий вызов → Security(1) → Transaction(2) → Logging(3) → Метод
                                                              ↑
Возврат        ← Security(1) ← Transaction(2) ← Logging(3) ←
```

Security оборачивает Transaction, а тот оборачивает Logging. События идут в порядке `security.enter → transaction.enter → logging.enter → method → logging.exit → transaction.exit → security.exit`.

**Подводный камень:** без явного `@Order` порядок между аспектами не определён, поэтому для зависимых аспектов (например, безопасность должна проверяться до открытия транзакции) его задают всегда. Крайние значения: `Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE` (самый внешний), `LOWEST_PRECEDENCE = Integer.MAX_VALUE`.

## Q18. Как реализовать логирование через AOP?

Классический сценарий: один `@Around`-аспект на сервисный слой логирует имя метода, длительность и исход вызова. Так логирование живёт в одном месте, а бизнес-методы остаются чистыми:

```text
orders.create(requestA) возвращает созданный Order.
orders.create(requestB) выбрасывает OrderRejectedException.
Нужно записать имя метода, длительность и исход обоих вызовов.
Запросы и результаты могут содержать секреты. Вызывающий код должен
получить прежний результат либо исходное исключение.
```

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
        log.debug("Calling {}", method);
        long start = System.nanoTime();

        try {
            Object result = pjp.proceed();
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            log.debug("{} succeeded in {} ms", method, elapsed);
            return result;
        } catch (Throwable e) {
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            log.debug("{} failed in {} ms ({})", method, elapsed, e.getClass().getSimpleName());
            throw e;
        }
    }
}
```

**Подводные камни:**
- **Безопасность.** Сырые аргументы, результат и сообщение исключения могут содержать секреты. В примере логируются только имя метода, длительность, исход и класс ошибки.
- **Уровень логирования.** Используйте `debug`, а не `info`, чтобы детальные трейсы не засоряли прод-логи; включать их по необходимости.

## Q19. Как реализовать кастомный retry через AOP?

Retry — учебный пример мощи `@Around`: advice крутит `proceed()` в цикле, перехватывает исключение, проверяет, входит ли оно в список «повторяемых», и при необходимости ждёт и пробует снова. Параметры (число попыток, типы исключений, задержка) удобно вынести в свою аннотацию:

```text
catalog.load(42) можно повторять после TemporaryCatalogException.
Общий лимит: три попытки, включая первоначальный вызов.
Пауза между попытками: 100 мс.
Последовательность исходов: TemporaryCatalogException,
TemporaryCatalogException, успешный результат.
Сколько вызовов и пауз будет? Что произойдёт, если третья попытка тоже завершится ошибкой?
```

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
        int maxAttempts = retry.attempts();
        long delayMs = retry.delayMs();
        if (maxAttempts < 1 || delayMs < 0) {
            throw new IllegalArgumentException("Invalid retry settings");
        }
        for (int attempt = 1; ; attempt++) {
            try {
                return pjp.proceed();
            } catch (Throwable failure) {
                if (attempt == maxAttempts || !isRetryable(failure, retry.on())) {
                    throw failure;
                }
                log.warn("Retrying {} after attempt {}", pjp.getSignature().getName(), attempt);
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw interrupted;
                }
            }
        }
    }

    private boolean isRetryable(Throwable e, Class<? extends Throwable>[] types) {
        return Arrays.stream(types).anyMatch(t -> t.isInstance(e));
    }
}
```

В заданном сценарии будет три вызова и две паузы. При повторяемом сбое на последней попытке вызывающему передаётся исходное исключение этой попытки. Метод или интерфейс прокси должен допускать `InterruptedException`, если прерывание нужно передать вызывающему как checked-исключение.

**Нюанс:** не повторяемые исключения пробрасываются сразу, а после исчерпания попыток летит последнее пойманное. Готовые средства retry позволяют настроить задержки и фильтрацию исключений. В Spring Framework 7 есть своя аннотация `@Retryable` из пакета `org.springframework.resilience.annotation`. У неё `maxRetries` задаёт число повторов после первой попытки, тогда как `attempts` в примере задаёт общий лимит. [Spring Framework Resilience Features](https://docs.spring.io/spring-framework/reference/core/resilience.html).

## Q20. Как измерить время выполнения метода через AOP?

Принцип тот же, что у retry, только проще: `@Around` засекает время вокруг `proceed()` и пишет его в метрику. Чтобы получить полноценную метрику (а не просто лог), результат отдают в `Timer` Micrometer — запись остаётся в настроенном реестре метрик:

```text
catalog.load(42) возвращает Product, catalog.load(99) выбрасывает
CatalogUnavailableException. Нужно добавить обе длительности
в Micrometer Timer, вернуть исходный Product и сохранить исключение.
Измеряются синхронные вызовы Spring-бина через прокси.
```

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

        Timer timer = meterRegistry.timer(name);
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return pjp.proceed();
        } finally {
            sample.stop(timer);
        }
    }
}
```

`finally` останавливает замер при обычном возврате и исключении. При штатной записи метрики результат или исходное исключение из `proceed()` передаётся дальше без обёртки.

Для произвольного Spring-бина можно зарегистрировать `TimedAspect` как бин с нужным `MeterRegistry`, включить Spring AOP и поставить `io.micrometer.core.annotation.Timed` на метод, доступный через прокси. Для Prometheus отдельно подключают соответствующий registry и настраивают экспорт, например экспозицию `prometheus` в Actuator для сбора по HTTP. [Micrometer Timers](https://docs.micrometer.io/micrometer/reference/concepts/timers.html).

## Q21. Как Spring Transactions используют AOP под капотом?

```java
// Императивный Spring-сервис. Клиент вызывает importOrder через прокси.
// Активной транзакции до вызова нет. Действуют стандартные правила
// отката, а insertOrder завершается без ошибок.
@Transactional(propagation = Propagation.REQUIRED)
public void importOrder(Order order) throws IOException {
    repository.insertOrder(order);
    throw new IOException("Import interrupted");
}
```

`TransactionInterceptor` реализует AOP Alliance `MethodInterceptor`. При вызове через прокси он определяет транзакционные атрибуты метода, обращается к подходящему менеджеру транзакций и вызывает следующую часть цепочки через `MethodInvocation.proceed()`. После вызова инфраструктура завершает транзакцию согласно исходу и правилам отката.

В этом примере `REQUIRED` открывает транзакцию. При стандартных правилах `IOException` как checked-исключение само по себе не вызывает откат. Если транзакция не помечена как rollback-only и коммит проходит успешно, запись фиксируется, а исключение передаётся вызывающему. По умолчанию откат запрашивается для `RuntimeException` и `Error`. Для отката при `IOException` можно задать `rollbackFor = IOException.class`.

При self-invocation вызов аннотированного метода не проходит через прокси. Транзакционные настройки этого внутреннего метода не применяются отдельно, а работа может продолжаться в уже открытой внешней транзакции. [Using @Transactional](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html).

## Q22. Как тестировать AOP-аспекты?

Главный нюанс: аспект применяется только через прокси, поэтому в обычном unit-тесте, где вы создаёте сервис через `new`, advice не сработает. Отсюда два уровня тестов — интеграционный (проверяет, что аспект реально *вплетается*) и юнит (проверяет *логику* самого advice в изоляции).

**Вариант 1 — Integration test (Spring-контекст).** Берём бин из контекста — он уже обёрнут прокси, аспект задействован. Проверяем сам факт перехвата:

```java
// Цель: проверить, отбирает ли pointcut вызов create, и отдельно
// проверить поведение advice, когда целевой метод бросает исключение.
@SpringBootTest
class LoggingAspectTest {
    @Autowired OrderService orderService;

    @Test
    void createOrder() {
        orderService.create(validRequest());
    }
}
```

```java
@SpringBootTest
class LoggingAspectTest {

    @Autowired OrderService orderService;  // прокси, аспект задействован
    @MockitoSpyBean LoggingAspect aspect;

    @Test
    void orderCreate_shouldBeLogged() {
        orderService.create(validRequest());
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

Для проверки границы pointcut добавь вызов, который должен остаться вне него, и убедись, что количество вызовов advice не выросло. Для проверки аварийной ветки в отдельном unit-тесте настрой `pjp.proceed()` на выброс заранее созданного исключения и проверь через `assertSame`, что advice выбросил тот же экземпляр. `@MockitoSpyBean` доступна в Spring Framework 6.2+. [Bean Overriding in Tests](https://docs.spring.io/spring/reference/testing/testcontext-framework/bean-overriding.html).

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
