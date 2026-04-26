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
> - [ ] AOP — это механизм замены наследования композицией на уровне байткода. | Не про композицию vs наследование. ❌ ПОСЛЕДСТВИЕ: путаница приводит к попыткам "решить наследование через AOP" — overengineering, на деле AOP про отдельную задачу (cross-cutting concerns).
> - [x] AOP — это парадигма для вынесения cross-cutting concerns в отдельные модули-аспекты. | ✓ ПРИМЕНЯТЬ: для логирования (`@Loggable`), транзакций (`@Transactional` под капотом — Spring AOP), security (`@PreAuthorize`), retry (`@Retryable`), кеширования (`@Cacheable`), метрик (`@Timed` Micrometer). 📋 ПРАВИЛО: "AOP = вынести повторяющуюся cross-cutting логику в Aspect; бизнес-код остаётся чистым". 🔗 См. Q2 (термины), Q21 (Transactions через AOP), Q18 (логирование).
> - [ ] AOP — это способ описать зависимости между бинами декларативно через аннотации. | Это IoC/DI. ❌ ПОСЛЕДСТВИЕ: путаница AOP и DI приводит к ложным ожиданиям — разработчик считает что `@Aspect` решит circular dependencies или скоупы. Это разные парадигмы, AOP применяется поверх DI.
> - [ ] AOP — это синоним паттерна Decorator на уровне языка Java. | Decorator — один из инструментов. ❌ ПОСЛЕДСТВИЕ: попытка реализовать AOP "через Decorator" вместо Spring AOP — много ручного кода для proxy generation, нет pointcut DSL, нет weaving. Spring AOP уже всё это даёт.

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
> - [ ] Spring AOP поддерживает перехват вызовов конструктора, полей и методов. | Только методы. ❌ ПОСЛЕДСТВИЕ: разработчик пытается логировать `new Order()` через Spring AOP — pointcut-выражение `execution(Order.new(..))` не работает, аспект молча игнорируется. Для конструкторов нужен AspectJ с compile-time weaving.
> - [x] Spring AOP поддерживает только один тип JoinPoint — вызов метода. | ✓ ПРИМЕНЯТЬ: для метод-уровневых cross-cutting concerns (логирование вызовов сервисов, метрики выполнения, audit trail). Для перехвата создания объектов или модификации полей — AspectJ Maven/Gradle plugin с compile-time weaving. 📋 ПРАВИЛО: "Spring AOP JoinPoint = только method invocation; constructor/field/static — AspectJ". 🔗 См. Q3 (Spring AOP vs AspectJ), Q6 (где не работает), Q9 (execution pointcut).
> - [ ] Spring AOP поддерживает перехват методов и конструкторов, но не полей. | Только методы. ❌ ПОСЛЕДСТВИЕ: разработчик читает stale документацию или путает с AspectJ — пишет `@Before("execution(Order.new(..))")` для аудита создания, тратит время на отладку. Не работает.
> - [ ] Spring AOP поддерживает только статические методы через @Around advice. | Только instance-методы. ❌ ПОСЛЕДСТВИЕ: непонимание ограничений приводит к попыткам перехватить `Math.random()` или `LocalDateTime.now()` — Spring AOP не перехватывает static, потому что нет proxy для статики. Mockito/PowerMock — другое решение.

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
> - [ ] Spring AOP применяет аспекты во время компиляции через bytecode weaving. | Это AspectJ. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает compile-time woven код, ищет `aspectjrt` в classpath и AspectJ-compiler в build — не находит. Spring AOP runtime-only, без изменения байткода.
> - [ ] Spring AOP перехватывает вызовы методов на не-Spring-бинах через агент JVM. | Только Spring beans. ❌ ПОСЛЕДСТВИЕ: попытка использовать Spring AOP для legacy-кода вне Spring контекста (например, util-классов с `new`) не работает, дезориентирует команду. Решение: или превратить класс в `@Component`, или AspectJ load-time weaving (`-javaagent:aspectjweaver.jar`).
> - [x] Spring AOP применяет аспекты во время выполнения через динамические прокси. | ✓ ПРИМЕНЯТЬ: для 90% задач достаточно — `@Aspect` + `@Before/@Around`, autoconfig в Spring Boot. Для performance-critical или non-Spring scenarios — AspectJ. Понимание proxy-подхода важно для отладки `@Transactional`, `@Cacheable` (см. self-invocation Q5). 📋 ПРАВИЛО: "Spring AOP = runtime proxy (JDK Proxy / CGLIB); AspectJ = compile/load-time weaving". 🔗 См. Q4 (JDK vs CGLIB), Q5 (self-invocation), Q6 (где не работает).
> - [ ] Spring AOP применяет аспекты через аннотированный компилятор AspectJ. | Только синтаксис, не компилятор. ❌ ПОСЛЕДСТВИЕ: разработчик настраивает AspectJ Maven plugin для Spring AOP — лишняя работа, performance hit at compile-time, и Spring всё равно создаст runtime proxy. Конфликт двух механизмов.

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
> - [ ] Spring AOP использует JDK Dynamic Proxy только когда класс помечен @Proxy. | Аннотации @Proxy нет. ❌ ПОСЛЕДСТВИЕ: разработчик ищет `@Proxy` в Spring API, не находит — теряет время. Выбор автоматический по наличию интерфейса.
> - [ ] Spring AOP всегда использует CGLIB независимо от наличия интерфейсов. | Default — JDK proxy при наличии интерфейса. ❌ ПОСЛЕДСТВИЕ: команда добавляет `cglib` dependency в проект Spring Boot 3.x, не зная что он уже встроен; Spring Boot 2.x по умолчанию `proxyTargetClass=true`, Spring Boot до 2.0 — false. Разница может удивлять при миграциях.
> - [x] Spring AOP использует JDK Dynamic Proxy когда бин реализует интерфейс, и CGLIB — когда нет. | ✓ ПРИМЕНЯТЬ: для testability предпочитайте интерфейсы (JDK Proxy чище), для simplicity — без интерфейсов (CGLIB подкласс). Spring Boot 2.0+ default `proxyTargetClass=true` (всегда CGLIB) — для consistency. Проверка типа: `AopUtils.isJdkDynamicProxy(bean)` / `isCglibProxy(bean)`. 📋 ПРАВИЛО: "JDK Proxy = interface-based, CGLIB = subclass-based; final-классы и final-методы — CGLIB не работает". 🔗 См. Q3 (Spring AOP vs AspectJ), Q5 (self-invocation для обоих), Q6 (final-методы).
> - [ ] Spring AOP использует CGLIB когда класс реализует интерфейс, и JDK Dynamic Proxy — когда нет. | Обратная логика. ❌ ПОСЛЕДСТВИЕ: непонимание этого приводит к ошибкам конфигурации `proxyTargetClass` в обратную сторону, ломая существующие тесты которые делали cast `(MyServiceImpl) myService`.

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
> - [ ] Проблема self-invocation решается аннотацией @SelfProxy на методе. | Такой аннотации нет. ❌ ПОСЛЕДСТВИЕ: разработчик ищет несуществующую аннотацию, теряет время. Реальные решения — self-injection с `@Lazy` или вынос в другой бин.
> - [ ] Проблема self-invocation решается добавлением @EnableAspectJAutoProxy на классе сервиса. | Не решает self-invocation. ❌ ПОСЛЕДСТВИЕ: команда добавляет `@EnableAspectJAutoProxy(exposeProxy = true)` на сервис, ожидая магического решения — `@Transactional` всё равно не работает на nested-вызовах. Корень проблемы — proxy не видит `this.method()`.
> - [x] Лучшее решение проблемы self-invocation — вынести вызываемый метод в отдельный Spring-бин. | ✓ ПРИМЕНЯТЬ: для `@Transactional` REQUIRES_NEW логики — вынести метод в отдельный сервис; для multi-aspect chain (transaction + cache + retry) — каждый concern в свой компонент. Альтернатива при невозможности рефакторинга — `((MyService) AopContext.currentProxy()).method()` с `@EnableAspectJAutoProxy(exposeProxy = true)`. 📋 ПРАВИЛО: "self-invocation = bypass прокси; решение — вынос метода в другой Spring bean ИЛИ AopContext.currentProxy()". 🔗 См. Q4 (JDK vs CGLIB), Q21 (transactions self-invocation), Q3 (AspectJ как полное решение).
> - [ ] Проблема self-invocation автоматически решается при использовании CGLIB вместо JDK Proxy. | Не решает. ❌ ПОСЛЕДСТВИЕ: команда мигрирует на `proxyTargetClass=true`, ожидая что `this.method()` начнёт работать — поведение остаётся прежним, время потеряно. Self-invocation — fundamental ограничение runtime-proxy подхода (любого).

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
> - [ ] Spring AOP перехватывает вызовы private-методов внутри Spring-бина через CGLIB. | Private не перехватывается. ❌ ПОСЛЕДСТВИЕ: разработчик ставит `@Transactional` на private-метод — компилируется, но транзакция не создаётся. Subtle bug: тесты с mock проходят, в проде data inconsistency. IDE warning IntelliJ помогает.
> - [ ] Spring AOP работает на любом объекте, созданном через new, если он помечен @Component. | @Component без контекста — не bean. ❌ ПОСЛЕДСТВИЕ: типичная ошибка — `new MyService()` в тесте/main, ожидание что `@Transactional`/`@Cacheable` сработают. Без `ApplicationContext` Spring не оборачивает в proxy.
> - [x] Spring AOP не работает на static-методах, final-классах и private-методах. | ✓ ПРИМЕНЯТЬ: проверить applicability — public non-final method on Spring bean injected through interface. Для Kotlin — все классы и методы по умолчанию `final`, нужен `kotlin-spring` plugin (open всё, что @Configuration/@Service/@Component). 📋 ПРАВИЛО: "AOP works on: public + non-final + non-static method on injected Spring bean". 🔗 См. Q4 (CGLIB ограничения), Q5 (self-invocation), Q3 (AspectJ преодолевает ограничения).
> - [ ] Spring AOP перехватывает конструкторы при включённом proxyTargetClass = true. | Конструкторы недоступны. ❌ ПОСЛЕДСТВИЕ: команда хочет audit creation through `@PostConstruct` aspect, не работает. Альтернатива — `BeanPostProcessor` на bean creation, или AspectJ.

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
> - [ ] Аннотации @Aspect достаточно, чтобы Spring создал аспектный бин и применил его. | Нужен ещё @Component. ❌ ПОСЛЕДСТВИЕ: классическая ошибка — разработчик пишет `@Aspect public class LoggingAspect`, аспект молча не работает. Без bean-регистрации Spring проигнорирует. IDE и Spring не выдают warning.
> - [x] Аспект должен иметь и @Aspect, и @Component (или другой стереотип) — оба обязательны. | ✓ ПРИМЕНЯТЬ: либо `@Aspect + @Component`, либо `@Aspect + @Bean` в `@Configuration`. Альтернатива — Spring 6+ `@Aspect @ComponentScan` automatic detection. Для тестирования аспектов — `@SpringBootTest` поднимет full context с aspects. 📋 ПРАВИЛО: "Aspect = @Aspect (что это) + @Component (зарегистрировать как bean); оба обязательны". 🔗 См. Q8 (@EnableAspectJAutoProxy), Q1 (AOP концепции), Q22 (тестирование аспектов).
> - [ ] Аннотация @Component достаточна — Spring сам поймёт, что класс с pointcut-методами это аспект. | @Aspect обязателен. ❌ ПОСЛЕДСТВИЕ: разработчик удаляет `@Aspect` "избыточный" — методы `@Before`/`@After` остаются, но прокси не создаётся. `@Component` без `@Aspect` — обычный bean, который Spring не обрабатывает как аспект.
> - [ ] Аспект должен расширять AbstractAspect или реализовать интерфейс Aspect. | Никакого base class нет. ❌ ПОСЛЕДСТВИЕ: разработчик ищет несуществующий `AbstractAspect` в `org.springframework.aop`, теряет время. Spring AOP использует AspectJ-аннотации без обязательного inheritance.

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
> - [ ] В Spring Boot нужно явно добавлять @EnableAspectJAutoProxy на главный класс. | Boot autoconfig. ❌ ПОСЛЕДСТВИЕ: лишняя аннотация — `Spring Boot` уже включил через `AopAutoConfiguration`. Дублирование может конфликтовать с custom-настройками `proxyTargetClass`/`exposeProxy`.
> - [x] @EnableAspectJAutoProxy активирует обработку @Aspect-бинов и создание прокси вокруг целевых объектов. | ✓ ПРИМЕНЯТЬ: для plain Spring (не Boot) — обязательна; для Spring Boot — нужна только при кастомизации параметров (`proxyTargetClass`, `exposeProxy`). Регистрирует `AnnotationAwareAspectJAutoProxyCreator` — `BeanPostProcessor` который оборачивает targets в proxy. 📋 ПРАВИЛО: "@EnableAspectJAutoProxy = триггер AOP; в Spring Boot — auto, в plain Spring — manual". 🔗 См. Q7 (Aspect declaration), Q4 (proxyTargetClass), Q5 (exposeProxy для self-invocation).
> - [ ] Параметр proxyTargetClass = true заставляет Spring использовать JDK Dynamic Proxy вместо CGLIB. | Наоборот. ❌ ПОСЛЕДСТВИЕ: путаница приводит к неправильной конфигурации — команда хочет JDK Proxy для testability, ставит `proxyTargetClass=true` (хуже!), получает CGLIB. Правильно: `proxyTargetClass=false` (default in plain Spring) или не задавать.
> - [ ] @EnableAspectJAutoProxy нужно указывать только для аспектов, использующих @Around advice. | Все advice требуют. ❌ ПОСЛЕДСТВИЕ: предположение приводит к non-working `@Before` aspects, разработчик считает что Around-only требование. На деле — все advice типы требуют aspect-processing.

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
> - [ ] Выражение `execution(* com.example.service.*.*(..))` перехватывает методы во всех подпакетах пакета service. | Только один уровень. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает рекурсивное покрытие всех subpackages — например, `service.user.UserServiceImpl`, `service.order.OrderServiceImpl` — пишет `service.*` и аспект работает только в одном пакете. Subpackages игнорируются.
> - [x] Выражение `execution(* com.example.service..*.*(..))` перехватывает методы во всех подпакетах пакета service. | ✓ ПРИМЕНЯТЬ: `..` в pointcut для recursive package matching, для широкого покрытия (audit, метрики); `..` в args — "любые параметры"; `+` для type hierarchy (`MyService+` = всё подтипы). Тестируйте pointcut в `@SpringBootTest` — Spring создаёт `AnnotationAwareAspectJAutoProxyCreator` log на DEBUG для матчинга. 📋 ПРАВИЛО: "execution = method signature matcher; `..` recursive packages, `*` one segment, `+` subtype". 🔗 См. Q10 (designators), Q11 (custom annotation pointcut), Q12 (named pointcut).
> - [ ] Выражение `execution(* com.example.service.*.*(..))` перехватывает методы только в классе `service`. | Класс vs пакет confusion. ❌ ПОСЛЕДСТВИЕ: разработчик думает `service` — имя класса, не пакет; pointcut фактически работает по всем классам пакета `service`, что часто и нужно — но непонимание pattern приводит к over-matching.
> - [ ] Символ `+` в pointcut-выражении означает "один или более методов в классе". | `+` = subtypes. ❌ ПОСЛЕДСТВИЕ: разработчик пытается ограничить аспект "методами с конкретным числом overload'ов" через `+`, не работает. Для overload-matching используется args или argNames.

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
> - [ ] Designator `within` отбирает методы по сигнатуре, аналогично `execution`. | within = class/package, не signature. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `within(* save*(..))` ожидая matching по имени метода — синтаксическая ошибка, аспект не применяется. Используйте `execution` для signature, `within` для locality.
> - [ ] Designator `bean` — это стандартный designator AspectJ для отбора по имени бина. | Spring AOP extension. ❌ ПОСЛЕДСТВИЕ: попытка использовать `bean(orderService)` в pure AspectJ projects (compile-time weaving) — компиляционная ошибка. Spring-only designator.
> - [x] Designator `@annotation` перехватывает методы, помеченные указанной аннотацией, а `@within` — методы в классах, помеченных аннотацией. | ✓ ПРИМЕНЯТЬ: `@annotation` для method-level cross-cutting (`@Loggable`, `@Cacheable`, `@Retryable` patterns); `@within` для class-level (audit on `@RestController`, метрики на `@Service`). Передача аннотации в advice как параметр — `@Before("@annotation(loggable)") void log(JoinPoint jp, Loggable loggable)`. 📋 ПРАВИЛО: "@annotation = метод-уровень, @within = класс-уровень; @target = runtime класс vs @within = compile-time класс". 🔗 См. Q11 (custom annotation), Q9 (execution), Q12 (named pointcut).
> - [ ] Designator `args` отбирает методы по возвращаемому типу. | args = arguments, не return. ❌ ПОСЛЕДСТВИЕ: разработчик использует `args(Order)` для матчинга методов, возвращающих Order — pointcut работает иначе, фактически матчит методы, ПРИНИМАЮЩИЕ Order как аргумент. Bug in unexpected places.

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
> - [ ] Кастомная аннотация для pointcut должна иметь @Retention(SOURCE). | RUNTIME обязателен. ❌ ПОСЛЕДСТВИЕ: разработчик копирует boilerplate `@Retention(SOURCE)` (default Java) для кастомной `@Loggable`, аспект молча не работает. Класс-файл не содержит метаинформации, runtime reflection не видит аннотацию.
> - [x] Чтобы получить саму аннотацию как параметр advice, имя в выражении `@annotation(x)` должно совпадать с именем параметра метода. | ✓ ПРИМЕНЯТЬ: для аннотаций с параметрами — `@Loggable(level=DEBUG, masked={"password"})`, аспект читает `loggable.level()` для динамической логики. Pattern для построения custom Spring-style аннотаций (`@Cacheable`, `@Retryable` именно так устроены). 📋 ПРАВИЛО: "@Retention(RUNTIME) + @Target(METHOD) + matching parameter name in advice signature". 🔗 См. Q10 (designators), Q19 (retry через custom annotation), Q20 (timing через annotation).
> - [ ] Аннотация для pointcut должна расширять интерфейс java.lang.annotation.Aspect. | Такого нет. ❌ ПОСЛЕДСТВИЕ: разработчик ищет `Aspect` interface в `java.lang.annotation`, не находит — теряет время. Любая RUNTIME-аннотация подходит для pointcut.
> - [ ] Pointcut по аннотации работает только если аннотация помечена @Inherited. | @Inherited не нужен. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `@Inherited` "на всякий случай", но это влияет только на наследование аннотации в class hierarchy. AOP видит аннотацию по `Method.getAnnotation()`.

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
> - [ ] Метод, помеченный @Pointcut, должен возвращать boolean с логикой проверки. | Должен быть void с empty body. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `@Pointcut public boolean serviceLayer() { return true; }` — компилируется, но AOP игнорирует тело метода. Pointcut работает по строке-аргументу аннотации.
> - [x] @Pointcut-методы позволяют именовать и переиспользовать pointcut-выражения, комбинируя через &&, ||, !. | ✓ ПРИМЕНЯТЬ: создайте `CommonPointcuts` класс со всеми reusable pointcut'ами (`serviceLayer()`, `repositoryLayer()`, `auditedMethod()`); ссылайтесь по FQN. Комбинирование: `(serviceLayer() && auditedMethod()) || @annotation(Critical)`. 📋 ПРАВИЛО: "@Pointcut = именованное выражение; void method body; combine с &&, ||, !". 🔗 См. Q9 (execution синтаксис), Q10 (designators), Q11 (custom annotation).
> - [ ] Pointcut-выражения нельзя переиспользовать между разными классами-аспектами. | Между классами — можно. ❌ ПОСЛЕДСТВИЕ: команда копирует одинаковые pointcut-строки в каждый аспект, при изменении пакетной структуры приходится править все 5+ мест. Централизация в `CommonPointcuts.serviceLayer()` решает это.
> - [ ] Оператор `&` в pointcut означает "и", а `|` — "или". | Двойные операторы. ❌ ПОСЛЕДСТВИЕ: `serviceLayer() & auditedMethod()` — синтаксическая ошибка AspectJ, аспект не парсится. Spring логирует `IllegalArgumentException`, но новички не сразу понимают причину.

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
> - [ ] @After выполняется только при успешном завершении метода, аналогично @AfterReturning. | @After = finally. ❌ ПОСЛЕДСТВИЕ: разработчик использует @After для логирования "method completed successfully" — на деле логирует и для исключений, путает отчёты в наблюдаемости.
> - [ ] @AfterThrowing может подавить исключение и вернуть нормальный результат. | Только @Around. ❌ ПОСЛЕДСТВИЕ: команда пытается реализовать fallback в @AfterThrowing, исключение продолжает распространяться вверх по стеку. Для recovery — `@Around` с try/catch и return default.
> - [x] @After выполняется всегда — и при успехе, и при исключении, аналогично finally-блоку. | ✓ ПРИМЕНЯТЬ: `@After` для resource cleanup (close streams, release locks), de-registration; `@AfterReturning` с `returning="result"` для post-processing успешных результатов; `@AfterThrowing` с `throwing="ex"` для error tracking; `@Around` для full control. 📋 ПРАВИЛО: "@Before/@After (always) /@AfterReturning (success) /@AfterThrowing (error) /@Around (full)". 🔗 См. Q14 (@Around подробно), Q18 (logging), Q19 (retry на @Around).
> - [ ] @Around не может изменить возвращаемое значение метода. | Может всё. ❌ ПОСЛЕДСТВИЕ: непонимание возможностей приводит к избыточному коду — разработчик создаёт два advice (@Before + @AfterReturning) для caching, тогда как один @Around с `if (cached) return cached; else proceed; cache; return;` решает elegantly.

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
> - [ ] Если @Around advice не вызывает pjp.proceed(), Spring автоматически вызовет реальный метод. | Не вызовет. ❌ ПОСЛЕДСТВИЕ: разработчик "забывает" вызвать `pjp.proceed()`, метод молча возвращает `null` (если return type Object) или скипается. В проде user logs in, контроллер не вызывается, `null` JSON response.
> - [x] Если @Around advice не вызывает pjp.proceed(), реальный метод не выполнится. | ✓ ПРИМЕНЯТЬ: для caching — проверяй cache, если есть — return без proceed; для feature flags — если flag off, return default без proceed; для security — если auth fail, throw без proceed. Эта возможность — main причина выбора @Around над @Before. 📋 ПРАВИЛО: "@Around proceed() — обязательный для прохождения; пропуск = skip метод; используй для caching/circuit breaker/feature flags". 🔗 См. Q15 (ProceedingJoinPoint), Q19 (retry — multiple proceeds), Q20 (timing).
> - [ ] @Around advice всегда должен объявлять возвращаемый тип void. | Должен Object. ❌ ПОСЛЕДСТВИЕ: ошибка typing — `void around(ProceedingJoinPoint pjp)` приводит к runtime ошибке "Around advice must return value", аспект отказывается работать.
> - [ ] pjp.proceed() можно вызвать только один раз внутри @Around advice. | Можно много раз. ❌ ПОСЛЕДСТВИЕ: непонимание возможностей приводит к ручной реализации retry через while-loop с попытками per advice — overengineering. Multiple `proceed()` в одном @Around — стандартный pattern для retry.

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
> - [ ] ProceedingJoinPoint можно использовать как параметр в @Before и @After advice. | Только в @Around. ❌ ПОСЛЕДСТВИЕ: typing — `void before(ProceedingJoinPoint pjp)` для @Before приводит к runtime ошибке Spring AOP "Cannot use ProceedingJoinPoint with @Before".
> - [x] ProceedingJoinPoint расширяет JoinPoint и добавляет метод proceed() для вызова целевого метода. | ✓ ПРИМЕНЯТЬ: ProceedingJoinPoint имеет всё что у JoinPoint + `proceed()`/`proceed(args)`. Используйте `pjp.proceed(newArgs)` для sanitization (нормализация emails в lowercase) или masking sensitive data перед logging. 📋 ПРАВИЛО: "JoinPoint = read-only для @Before/@After; ProceedingJoinPoint = read+control для @Around". 🔗 См. Q14 (@Around), Q16 (Method info из advice), Q19 (retry с pjp.proceed).
> - [ ] JoinPoint.getThis() возвращает целевой объект, а getTarget() — прокси. | Обратная логика. ❌ ПОСЛЕДСТВИЕ: разработчик использует `getThis().getClass()` для логирования, получает имя `EnhancerByCGLIB$$...` (proxy-класс), confusion в логах. Правильно для логирования — `getTarget().getClass().getSimpleName()`.
> - [ ] pjp.proceed(newArgs) может изменить только количество аргументов, но не их значения. | Можно менять значения, не количество. ❌ ПОСЛЕДСТВИЕ: попытка добавить аргументы (с разной сигнатурой) приводит к runtime mismatch. Проще — изменить значения через mutation Object[] перед `proceed()`.

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
> - [ ] Имена параметров метода всегда доступны через MethodSignature.getParameterNames() без настроек компиляции. | Нужен `-parameters` flag. ❌ ПОСЛЕДСТВИЕ: разработчик логирует параметры в advice, в логах видит `arg0=foo, arg1=bar` вместо `userId=foo, action=bar` — useless для debugging. Spring Boot Maven plugin auto-добавляет flag, но Gradle проекты могут пропустить.
> - [x] Для получения java.lang.reflect.Method нужно привести Signature к MethodSignature и вызвать getMethod(). | ✓ ПРИМЕНЯТЬ: `Method method = ((MethodSignature) jp.getSignature()).getMethod()` для чтения аннотаций (`method.getAnnotation(MyAnno.class)`), типов параметров, return type. Полезно для multi-аннотационных аспектов. Для constructor — `ConstructorSignature` cast (только в AspectJ). 📋 ПРАВИЛО: "MethodSignature cast для access к Method API; нужно для аннотаций и parameter types". 🔗 См. Q15 (JoinPoint), Q11 (custom annotation), Q19 (retry читает аннотацию).
> - [ ] pjp.getArgs() возвращает неизменяемый список аргументов метода. | Mutable Object[]. ❌ ПОСЛЕДСТВИЕ: разработчик мутирует `args[0] = "modified"` ожидая что метод получит новое значение — на деле нет, нужен `pjp.proceed(newArgs)` для passing. Subtle bug.
> - [ ] pjp.getTarget().getClass() всегда возвращает класс прокси, а не реальный класс. | Реальный класс. ❌ ПОСЛЕДСТВИЕ: разработчик ждёт CGLIB-имя для proxy-detection в advice — получает `OrderService` (real). Для proxy-class — `getThis().getClass()`.

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
> - [ ] Аспект с @Order(1) выполняет @Before последним среди всех аспектов. | Обратная логика. ❌ ПОСЛЕДСТВИЕ: команда ставит security аспект `@Order(1)` ожидая что он "финальный", фактически он первый — security check выполняется до transaction setup, что иногда нужно, но иногда нет.
> - [x] Аспект с @Order(1) выполняет @Before первым — он находится "снаружи" цепочки аспектов. | ✓ ПРИМЕНЯТЬ: `@Order(1)` Security (auth check), `@Order(2)` Transaction (begin tx), `@Order(3)` Logging/Metrics, `@Order(Integer.MAX_VALUE)` — внутренние аспекты ближе к методу. `Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE`. 📋 ПРАВИЛО: "@Order меньше = снаружи (раньше @Before, позже @After); think 'matryoshka layers'". 🔗 См. Q21 (transaction order), Q18 (logging), Q5 (multi-aspect issues).
> - [ ] Без явного @Order аспекты выполняются в алфавитном порядке имён классов. | Не определён. ❌ ПОСЛЕДСТВИЕ: команда полагается на алфавитный порядок (`AuditAspect` → `BillingAspect` → `CacheAspect`), при добавлении нового аспекта `BatchAspect` нарушается ordering, изменения в проде. Always specify @Order.
> - [ ] Аспект с @Order(Integer.MAX_VALUE) выполняет @Before первым. | MAX_VALUE = lowest priority. ❌ ПОСЛЕДСТВИЕ: путаница приводит к "аспекты в обратном порядке" в дизайне — security оказывается внутренним вместо внешнего, что компрометирует layered defense.

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
> - [ ] Для логирования аргументов и результата достаточно @Before advice. | @Before — только входные данные. ❌ ПОСЛЕДСТВИЕ: один аспект на @Before логирует только args, результаты не видны — приходится дополнять @AfterReturning, разрозненная логика, трудно читать диагностику.
> - [x] Логирующий аспект на @Around позволяет залогировать входные аргументы, результат, длительность и исключения в одном месте. | ✓ ПРИМЕНЯТЬ: `@Around` для unified logging-aspect — start logging, измерить время через `System.nanoTime()`, log result/exception, всё в одном месте. Маскирование sensitive data — через `@Sensitive` маркер на полях DTO + custom redaction logic. 📋 ПРАВИЛО: "@Around для unified entry+exit+timing+error logging; mask sensitive data через annotations". 🔗 См. Q14 (@Around), Q20 (timing aspect), Q16 (Method info для advanced logging).
> - [ ] При логировании следует всегда выводить все аргументы метода как есть. | Mask sensitive. ❌ ПОСЛЕДСТВИЕ: classic data leak — пароли, токены, PII в production логах. GDPR violation, security incident. Capital One 2019 описал utility logger leak. Используйте `@Sensitive` маркеры или Logback masking patterns.
> - [ ] Для получения имени метода в advice используется pjp.getName(). | Нет такого метода. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `pjp.getName()`, IDE показывает ошибку — теряет 5 минут на поиск правильного API. Правильно — `pjp.getSignature().getName()` или `toShortString()`.

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
> - [ ] Retry-аспект можно реализовать через @Before advice, вызывая метод повторно при исключении. | @Before не видит исключения. ❌ ПОСЛЕДСТВИЕ: разработчик пытается реализовать retry через `@Before`, не имеет возможности catch — теряет время на rewriting на @Around. @Before — однонаправленный, без back-flow.
> - [x] Retry через AOP реализуется @Around advice с циклом, в котором вызывается pjp.proceed() в try/catch. | ✓ ПРИМЕНЯТЬ: для transient failures (HTTP timeout, DB deadlock); добавить exponential backoff (`Thread.sleep(delay * (2^attempt))`), jitter; для production предпочитать Spring Retry (`@Retryable`) или Resilience4j (`@Retry`) — battle-tested. Custom @Around — для специфических сценариев (custom retry policies). 📋 ПРАВИЛО: "@Around + loop + try/catch + sleep + multiple proceed = retry; production — Spring Retry/Resilience4j". 🔗 См. Q14 (@Around), Q11 (custom annotation), Q19 contains code example.
> - [ ] Для retry обязательно использовать Spring Retry, своя реализация через AOP невозможна. | Можно. ❌ ПОСЛЕДСТВИЕ: ложное предположение приводит к неоправданным dependency на Spring Retry в простых случаях. Для понимания AOP — реализация retry своими силами — отличное упражнение и типичный pattern.
> - [ ] В retry-аспекте pjp.proceed() нужно вызывать только один раз, иначе будет дублирование. | Multiple — допустимо. ❌ ПОСЛЕДСТВИЕ: непонимание возможности приводит к выкручиванию через ThreadLocal counters или внешние state machines. Multiple `proceed()` в одном @Around — стандарт для retry.

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
> - [ ] Замер времени через @Before и @After обеспечит корректный расчёт длительности метода. | Нужен ThreadLocal. ❌ ПОСЛЕДСТВИЕ: разработчик использует static field/instance variable для start time — race condition между concurrent requests, неправильные метрики. ThreadLocal обязателен, но это overkill — лучше @Around.
> - [x] Измерение времени удобнее реализовать @Around advice — start до pjp.proceed(), end после, в finally-блоке. | ✓ ПРИМЕНЯТЬ: production — Micrometer `@Timed` annotation (Spring Boot Actuator integration), отображение в Grafana через Prometheus. Custom @Around для unique requirements (per-tenant метрики, custom histograms). 📋 ПРАВИЛО: "@Around с System.nanoTime() + finally; в Spring Boot — Micrometer @Timed". 🔗 См. Q14 (@Around), Q18 (logging timing), Q11 (custom annotation pattern).
> - [ ] Для измерения времени в Spring Boot всегда нужно писать кастомный AOP-аспект. | Micrometer @Timed готов. ❌ ПОСЛЕДСТВИЕ: команда дублирует Micrometer functionality, пишет свой `@Timed` — лишняя работа, не учитывают percentiles, histograms. Используйте `io.micrometer.core.annotation.Timed` (auto-config в Spring Boot Actuator).
> - [ ] System.currentTimeMillis() точнее System.nanoTime() для замера длительности методов. | Наоборот. ❌ ПОСЛЕДСТВИЕ: разработчик использует `currentTimeMillis()`, в момент NTP-jump receives отрицательную длительность — метрика искажена. nanoTime() — монотонный, гарантированно > 0 для intervals.

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
> - [ ] @Transactional реализован через @Before advice, который открывает транзакцию перед методом. | Через @Around (TransactionInterceptor). ❌ ПОСЛЕДСТВИЕ: непонимание архитектуры приводит к неправильным debug-сценариям — разработчик ищет proxy creation issue в @Before chain, не находит. Все Spring core annotations (@Transactional, @Cacheable, @Async) — это @Around под капотом.
> - [ ] @Transactional по умолчанию откатывает транзакцию при любом исключении, включая checked. | Только RuntimeException/Error. ❌ ПОСЛЕДСТВИЕ: classic data inconsistency — `IOException` (checked) бросается в transactional методе, транзакция КОММИТИТСЯ. Order saved, но email не отправлен — partial state в БД. Решение: `@Transactional(rollbackFor = Exception.class)` или unchecked exceptions.
> - [x] @Transactional реализован через @Around advice, который открывает транзакцию, вызывает метод и делает commit или rollback. | ✓ ПРИМЕНЯТЬ: понимание этого помогает дебажить self-invocation (Q5), nested transactions, propagation modes (REQUIRED, REQUIRES_NEW, NESTED). `TransactionInterceptor` extends `MethodInterceptor` (AOP Alliance). Для тестирования — `@Transactional` на тесте rolls back changes after each test. 📋 ПРАВИЛО: "@Transactional = @Around на TransactionInterceptor; default rollback only on Runtime/Error; configure rollbackFor для checked". 🔗 См. Q5 (self-invocation), Q1 (AOP концепция), spring-transaction-interview для подробностей propagation/isolation.
> - [ ] @Transactional реализован через @AfterReturning advice, который коммитит результат после метода. | Не @AfterReturning. ❌ ПОСЛЕДСТВИЕ: путаница приводит к неправильным mental models при дебаге — разработчик ищет почему commit "не срабатывает после метода", тогда как transaction begin происходит до метода (нужен @Around).

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
> - [ ] Unit-тест с `new MyAspect()` и вызов обычного метода гарантирует проверку работы аспекта в Spring-контексте. | Не проходит через proxy. ❌ ПОСЛЕДСТВИЕ: false-positive тесты — unit test зелёный, в проде аспект не работает (например, pointcut не матчит классы из другого пакета). Integration test обязателен для pointcut verification.
> - [x] Лучший подход — @SpringBootTest с реальным бином-целью, где аспект применяется через прокси автоматически. | ✓ ПРИМЕНЯТЬ: `@SpringBootTest` + `@SpyBean MyAspect aspect` для verification вызовов; для unit — Mockito mock `ProceedingJoinPoint` для логики advice. Test slice `@SpringBootTest(classes = {MyAspect.class, TargetService.class})` для быстрой инициализации без full context. 📋 ПРАВИЛО: "Aspect testing = integration (@SpringBootTest + proxy) + unit (mock ProceedingJoinPoint)". 🔗 См. Q14 (@Around testing), Q5 (self-invocation testing), Q15 (ProceedingJoinPoint mocking).
> - [ ] Для unit-теста аспекта нужно создавать настоящий прокси через java.lang.reflect.Proxy вручную. | Mockito достаточен. ❌ ПОСЛЕДСТВИЕ: команда копирует boilerplate ручной proxy creation в каждый unit test — тесты становятся длинными и хрупкими, при изменении API теста ломаются разные тесты. Mock `ProceedingJoinPoint` — clean.
> - [ ] В @Around advice нельзя замокать ProceedingJoinPoint — только вызвать реальный метод. | ProceedingJoinPoint mockable. ❌ ПОСЛЕДСТВИЕ: команда не пишет unit-тесты на advice логику (только integration), purely-логические бaги (например, неправильный `nanoTime` math) находятся в проде. Unit + integration — обе нужны.

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
