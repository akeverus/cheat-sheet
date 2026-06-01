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
