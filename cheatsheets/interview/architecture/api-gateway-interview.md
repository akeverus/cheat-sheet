---
title: "Вопросы на собеседовании: API Gateway"
description: "Полное покрытие паттерна API Gateway: маршрутизация, аутентификация, rate limiting, трансформация запросов, агрегация, Spring Cloud Gateway, фильтры, предикаты, BFF, безопасность, CORS, версионирование API."
tags:
  - interview
  - architecture
  - api-gateway-interview
aliases:
  - "API Gateway interview"
  - "API Gateway собеседование"
  - "Spring Cloud Gateway"
  - "API шлюз"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `API Gateway`

Полное покрытие паттерна `API Gateway`: маршрутизация, аутентификация, `rate limiting`, трансформация запросов, агрегация, `Spring Cloud Gateway`, фильтры, предикаты, `BFF`, безопасность, `CORS`, версионирование API.

**API Gateway** -- один из ключевых паттернов микросервисной архитектуры. На собеседованиях ожидают глубокое понимание его роли, ответственностей, различий с `reverse proxy`, а также практический опыт с `Spring Cloud Gateway` или аналогами (`Kong`, `Nginx`, `AWS API Gateway`). Этот файл охватывает теорию паттерна, архитектуру `Spring Cloud Gateway`, фильтры, предикаты, безопасность и продвинутые сценарии.

## Полезные ссылки

### Официальная документация

- [Spring Cloud Gateway Reference](https://docs.spring.io/spring-cloud-gateway/reference/html/) -- официальная документация
- [Exploring the New Spring Cloud Gateway (Baeldung)](https://www.baeldung.com/spring-cloud-gateway) -- маршрутизация, фильтры, предикаты
- [Spring Cloud Series - The Gateway Pattern (Baeldung)](https://www.baeldung.com/spring-cloud-gateway-pattern) -- паттерн Gateway в Spring Cloud
- [Writing Custom Spring Cloud Gateway Filters (Baeldung)](https://www.baeldung.com/spring-cloud-custom-gateway-filters) -- кастомные фильтры
- [Spring Cloud Gateway WebFilter Factories (Baeldung)](https://www.baeldung.com/spring-cloud-gateway-webfilter-factories) -- встроенные WebFilter фабрики
- [API Gateway vs Reverse Proxy (Baeldung)](https://www.baeldung.com/cs/api-gateway-vs-reverse-proxy) -- сравнение API Gateway и Reverse Proxy
- [Using Spring Cloud Gateway with OAuth 2.0 (Baeldung)](https://www.baeldung.com/spring-cloud-gateway-oauth2) -- OAuth 2.0 паттерны
- [OAuth2 BFF With Spring Cloud Gateway (Baeldung)](https://www.baeldung.com/spring-cloud-gateway-bff-oauth2) -- паттерн Backend for Frontend

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы паттерна API Gateway**
- [Q1. (!) Что такое API Gateway и зачем он нужен?](#q1--что-такое-api-gateway-и-зачем-он-нужен)
- [Q2. (!) Какие основные обязанности API Gateway?](#q2--какие-основные-обязанности-api-gateway)
- [Q3. (!) Чем API Gateway отличается от Reverse Proxy?](#q3--чем-api-gateway-отличается-от-reverse-proxy)
- [Q4. Какие преимущества и недостатки у паттерна API Gateway?](#q4-какие-преимущества-и-недостатки-у-паттерна-api-gateway)
- [Q5. Что такое паттерн BFF (Backend for Frontend)?](#q5-что-такое-паттерн-bff-backend-for-frontend)

**Spring Cloud Gateway -- архитектура и конфигурация**
- [Q6. (!) Что такое Spring Cloud Gateway и на чём он основан?](#q6--что-такое-spring-cloud-gateway-и-на-чём-он-основан)
- [Q7. (!) Что такое Route, Predicate и Filter в Spring Cloud Gateway?](#q7--что-такое-route-predicate-и-filter-в-spring-cloud-gateway)
- [Q8. Как настроить маршрутизацию через YAML и Java DSL?](#q8-как-настроить-маршрутизацию-через-yaml-и-java-dsl)
- [Q9. Какие встроенные предикаты (Route Predicates) существуют?](#q9-какие-встроенные-предикаты-route-predicates-существуют)

**Фильтры Spring Cloud Gateway**
- [Q10. (!) Какие типы фильтров существуют в Spring Cloud Gateway?](#q10--какие-типы-фильтров-существуют-в-spring-cloud-gateway)
- [Q11. (!) Как написать кастомный GatewayFilter?](#q11--как-написать-кастомный-gatewayfilter)
- [Q12. Что такое GlobalFilter и чем он отличается от GatewayFilter?](#q12-что-такое-globalfilter-и-чем-он-отличается-от-gatewayfilter)
- [Q13. Как работает цепочка фильтров и порядок их выполнения?](#q13-как-работает-цепочка-фильтров-и-порядок-их-выполнения)
- [Q14. Какие встроенные фильтры для трансформации запросов и ответов существуют?](#q14-какие-встроенные-фильтры-для-трансформации-запросов-и-ответов-существуют)

**Безопасность и аутентификация**
- [Q15. (!) Как реализовать аутентификацию и авторизацию через API Gateway?](#q15--как-реализовать-аутентификацию-и-авторизацию-через-api-gateway)
- [Q16. Как настроить CORS в Spring Cloud Gateway?](#q16-как-настроить-cors-в-spring-cloud-gateway)
- [Q17. Как реализовать OAuth 2.0 Token Relay через Gateway?](#q17-как-реализовать-oauth-20-token-relay-через-gateway)

**Rate Limiting и отказоустойчивость**
- [Q18. (!) Как реализовать Rate Limiting в API Gateway?](#q18--как-реализовать-rate-limiting-в-api-gateway)
- [Q19. (!) Как интегрировать Circuit Breaker с API Gateway?](#q19--как-интегрировать-circuit-breaker-с-api-gateway)

**Интеграция с инфраструктурой**
- [Q20. (!) Как API Gateway интегрируется с Service Discovery?](#q20--как-api-gateway-интегрируется-с-service-discovery)
- [Q21. Как реализовать балансировку нагрузки через Gateway?](#q21-как-реализовать-балансировку-нагрузки-через-gateway)
- [Q22. Поддерживает ли Spring Cloud Gateway WebSocket?](#q22-поддерживает-ли-spring-cloud-gateway-websocket)

**Версионирование и агрегация API**
- [Q23. Как реализовать версионирование API через Gateway?](#q23-как-реализовать-версионирование-api-через-gateway)
- [Q24. Что такое API Composition/Aggregation и как реализовать через Gateway?](#q24-что-такое-api-compositionaggregation-и-как-реализовать-через-gateway)

**Сравнение решений и production-практики**
- [Q25. (!) Сравните Spring Cloud Gateway, Kong и Nginx как API Gateway](#q25--сравните-spring-cloud-gateway-kong-и-nginx-как-api-gateway)
- [Q26. Как организовать мониторинг и логирование API Gateway?](#q26-как-организовать-мониторинг-и-логирование-api-gateway)
- [Q27. Какие anti-паттерны связаны с API Gateway?](#q27-какие-anti-паттерны-связаны-с-api-gateway)

**Продвинутые темы**
- [Q28. (!) Как реализовать request/response трансформацию на уровне Gateway?](#q28--как-реализовать-requestresponse-трансформацию-на-уровне-gateway)
- [Q29. Как настроить Kong API Gateway для rate limiting и аутентификации?](#q29-как-настроить-kong-api-gateway-для-rate-limiting-и-аутентификации)
- [Q30. (!) Как организовать observability (метрики, трейсинг, логи) для API Gateway?](#q30--как-организовать-observability-метрики-трейсинг-логи-для-api-gateway)
- [Q31. (!) Алгоритмы Rate Limiting: Token Bucket, Leaky Bucket, Fixed Window, Sliding Window](#q31--алгоритмы-rate-limiting-token-bucket-leaky-bucket-fixed-window-sliding-window)
- [Q32. (!) API Gateway vs Service Mesh: когда что выбирать?](#q32--api-gateway-vs-service-mesh-когда-что-выбирать)
- [Q33. Как работает gRPC-Web через API Gateway?](#q33-как-работает-grpc-web-через-api-gateway)
- [Q34. WebSocket через API Gateway: sticky sessions и масштабирование](#q34-websocket-через-api-gateway-sticky-sessions-и-масштабирование)
- [Q35. API Gateway в serverless архитектуре (AWS API Gateway)](#q35-api-gateway-в-serverless-архитектуре-aws-api-gateway)
- [Q36. GraphQL через API Gateway: federation и schema stitching](#q36-graphql-через-api-gateway-federation-и-schema-stitching)
- [Q37. (!) Стратегии версионирования API через Gateway](#q37--стратегии-версионирования-api-через-gateway)
- [Q38. (!) Retry и Circuit Breaker на уровне API Gateway](#q38--retry-и-circuit-breaker-на-уровне-api-gateway)

---

## Q1. (!) Что такое API Gateway и зачем он нужен?

**API Gateway** -- это единая точка входа для всех клиентских запросов в микросервисной архитектуре. Он принимает входящие запросы, маршрутизирует их к соответствующим backend-сервисам, агрегирует результаты и возвращает ответ клиенту.

```mermaid
graph LR
    C1[Web Client] --> GW[API Gateway]
    C2[Mobile Client] --> GW
    C3[Partner API] --> GW
    GW --> S1[User Service]
    GW --> S2[Order Service]
    GW --> S3[Product Service]
    GW --> S4[Payment Service]
```

**Зачем нужен API Gateway:**

- **Единая точка входа** -- клиенты обращаются к одному URL вместо десятков сервисов
- **Скрытие внутренней структуры** -- клиенты не знают о количестве и расположении сервисов
- **Инкапсуляция cross-cutting concerns** -- аутентификация, логирование, rate limiting реализуются один раз
- **Упрощение клиентского кода** -- клиент не занимается обнаружением сервисов и агрегацией данных
- **Протокольная трансляция** -- например, внешний REST API -> внутренний gRPC

Без `API Gateway` каждый клиент должен был бы знать адреса всех сервисов, самостоятельно обрабатывать ошибки, аутентификацию и ретраи, что приводит к дублированию логики и тесной связанности.

---

## Q2. (!) Какие основные обязанности API Gateway?

API Gateway берёт на себя несколько ключевых cross-cutting concerns:

| Обязанность | Описание |
|---|---|
| **Маршрутизация** | Направление запросов к нужному backend-сервису по URL, заголовкам, параметрам |
| **Аутентификация/Авторизация** | Проверка `JWT`, `OAuth 2.0` токенов до передачи запроса сервису |
| **Rate Limiting** | Ограничение количества запросов от клиента за единицу времени |
| **Трансформация запросов** | Модификация заголовков, тела, query-параметров |
| **Агрегация** | Объединение ответов нескольких сервисов в один для клиента |
| **Кэширование** | Кэширование часто запрашиваемых ответов |
| **Балансировка нагрузки** | Распределение запросов между инстансами сервиса |
| **Circuit Breaker** | Защита от каскадных сбоев при недоступности backend |
| **Логирование и мониторинг** | Централизованный сбор метрик, трейсов, логов |
| **CORS** | Управление Cross-Origin Resource Sharing |
| **SSL Termination** | Завершение TLS на уровне Gateway |

На собеседовании важно уметь объяснить, какие обязанности стоит размещать в Gateway, а какие лучше оставить в самих сервисах. Бизнес-логика **никогда** не должна находиться в Gateway -- это anti-паттерн.

---

## Q3. (!) Чем API Gateway отличается от Reverse Proxy?

Это частый вопрос, который проверяет понимание архитектурных нюансов.

| Аспект | Reverse Proxy | API Gateway |
|---|---|---|
| **Уровень работы** | L4/L7 (TCP/HTTP) | L7 (Application) |
| **Основная задача** | Проксирование и балансировка | Управление API lifecycle |
| **Маршрутизация** | По URL/IP, простая | Сложная: по заголовкам, query, телу запроса |
| **Аутентификация** | Базовая или отсутствует | Полная: OAuth 2.0, JWT, API keys |
| **Трансформация** | Минимальная (заголовки) | Полная: заголовки, тело, протоколы |
| **Rate Limiting** | Базовый (по IP) | Продвинутый (по пользователю, API key, плану) |
| **Агрегация** | Нет | Да, объединение ответов нескольких сервисов |
| **API Management** | Нет | Версионирование, документация, аналитика |
| **Примеры** | `Nginx`, `HAProxy`, `Envoy` | `Kong`, `Spring Cloud Gateway`, `AWS API Gateway` |

**Ключевое различие**: `Reverse Proxy` -- это инфраструктурный компонент для проксирования трафика, а `API Gateway` -- это архитектурный паттерн с богатой функциональностью для управления API. На практике `Nginx` и `Kong` показывают, как `reverse proxy` может эволюционировать в `API Gateway` с добавлением плагинов.

---

## Q4. Какие преимущества и недостатки у паттерна API Gateway?

**Преимущества:**

- **Упрощение клиентского кода** -- один endpoint вместо десятков
- **Инкапсуляция** -- изменение внутренней структуры не затрагивает клиентов
- **Централизация cross-cutting concerns** -- DRY-принцип для безопасности, логирования
- **Возможность адаптации API** под разных клиентов (BFF)
- **Протокольная изоляция** -- внешний REST, внутренний gRPC/AMQP

**Недостатки:**

- **Единая точка отказа (SPOF)** -- Gateway должен быть высокодоступным
- **Дополнительная задержка** -- каждый запрос проходит через дополнительный hop
- **Сложность эксплуатации** -- ещё один компонент для деплоя, мониторинга, масштабирования
- **Риск "толстого" Gateway** -- соблазн положить бизнес-логику в Gateway
- **Bottleneck** -- весь трафик проходит через одну точку

Для минимизации рисков Gateway должен быть **stateless**, **горизонтально масштабируемым** и содержать **только инфраструктурную логику**.

---

## Q5. Что такое паттерн BFF (Backend for Frontend)?

**BFF (Backend for Frontend)** -- это вариация паттерна `API Gateway`, при которой для каждого типа клиента создаётся отдельный Gateway, адаптированный под его потребности.

```mermaid
graph TD
    WEB[Web App] --> BFF_WEB[BFF for Web]
    MOB[Mobile App] --> BFF_MOB[BFF for Mobile]
    IOT[IoT Device] --> BFF_IOT[BFF for IoT]
    BFF_WEB --> S1[User Service]
    BFF_WEB --> S2[Product Service]
    BFF_MOB --> S1
    BFF_MOB --> S3[Notification Service]
    BFF_IOT --> S4[Telemetry Service]
```

**Зачем нужен BFF:**

- **Разные форматы данных** -- мобильному клиенту нужны компактные ответы, веб-клиенту -- полные
- **Разные паттерны агрегации** -- одна страница мобильного приложения может требовать данных от 5 сервисов
- **Разные требования к безопасности** -- `OAuth 2.0` для web, `API key` для IoT
- **Независимые релизные циклы** -- команда мобильной разработки управляет своим BFF

**Подробнее** о реализации BFF с `Spring Cloud Gateway` и `OAuth 2.0` -- в [[spring-cloud-interview|вопросах по Spring Cloud]].

---

## Q6. (!) Что такое Spring Cloud Gateway и на чём он основан?

**Spring Cloud Gateway** -- это реактивный API Gateway из экосистемы `Spring Cloud`, построенный на `Spring WebFlux` и `Project Reactor`. Он работает на `Netty` вместо `Tomcat`, что обеспечивает неблокирующую обработку запросов.

```mermaid
graph LR
    CLIENT[Client] --> SCG[Spring Cloud Gateway<br/>Netty + WebFlux]
    SCG --> HM[Handler Mapping]
    HM --> WH[Web Handler]
    WH --> PRE[Pre-Filters]
    PRE --> PROXY[Proxied Service]
    PROXY --> POST[Post-Filters]
    POST --> CLIENT
```

**Ключевые характеристики:**

- **Реактивный стек** -- `WebFlux` + `Reactor Netty`, неблокирующий I/O
- **Интеграция с Spring Cloud** -- `Eureka`, `Consul`, `Resilience4j`, `Spring Security`
- **Декларативная и программная конфигурация** -- YAML или Java DSL
- **Расширяемость** -- кастомные фильтры и предикаты
- **Поддержка WebSocket** -- проксирование WebSocket соединений

**Важно**: `Spring Cloud Gateway` **несовместим** с `Spring MVC` (Servlet-стек). Нельзя использовать `spring-boot-starter-web` и `spring-cloud-starter-gateway` вместе -- это приведёт к конфликту.

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<!-- НЕ добавлять spring-boot-starter-web! -->
```

---

## Q7. (!) Что такое Route, Predicate и Filter в Spring Cloud Gateway?

Это три основных строительных блока `Spring Cloud Gateway`:

**Route** (маршрут) -- основная единица конфигурации. Определяет, куда направить запрос. Состоит из:
- `id` -- уникальный идентификатор
- `uri` -- целевой адрес backend-сервиса
- `predicates` -- условия для сопоставления запроса
- `filters` -- модификации запроса/ответа

**Predicate** (предикат) -- условие, которому должен соответствовать входящий запрос, чтобы маршрут был применён. Реализует `java.util.function.Predicate<ServerWebExchange>`.

**Filter** (фильтр) -- компонент для модификации запроса до отправки в backend (pre-filter) или ответа перед возвратом клиенту (post-filter).

```mermaid
graph LR
    REQ[HTTP Request] --> P{Predicate<br/>Match?}
    P -->|Да| F1[Pre-Filter 1]
    P -->|Нет| NEXT[Next Route]
    F1 --> F2[Pre-Filter N]
    F2 --> SVC[Backend Service]
    SVC --> PF1[Post-Filter N]
    PF1 --> PF2[Post-Filter 1]
    PF2 --> RES[HTTP Response]
```

---

## Q8. Как настроить маршрутизацию через YAML и Java DSL?

**YAML-конфигурация** (декларативный подход):

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/users/**
            - Method=GET,POST
          filters:
            - AddRequestHeader=X-Request-Source, gateway
            - RewritePath=/api/users/(?<segment>.*), /users/${segment}

        - id: order-service
          uri: lb://order-service  # через Service Discovery
          predicates:
            - Path=/api/orders/**
            - Header=X-Api-Version, v2
          filters:
            - CircuitBreaker=name=orderCB,fallbackUri=forward:/fallback/orders
```

**Java DSL** (программный подход):

```java
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("user-service", r -> r
                .path("/api/users/**")
                .and().method(HttpMethod.GET, HttpMethod.POST)
                .filters(f -> f
                    .addRequestHeader("X-Request-Source", "gateway")
                    .rewritePath("/api/users/(?<segment>.*)", "/users/${segment}"))
                .uri("http://localhost:8081"))
            .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .circuitBreaker(c -> c
                        .setName("orderCB")
                        .setFallbackUri("forward:/fallback/orders")))
                .uri("lb://order-service"))
            .build();
    }
}
```

**Когда что использовать**: YAML удобен для простых маршрутов и DevOps-конфигурации. Java DSL предпочтителен, когда логика маршрутизации содержит условия, динамические значения или кастомные фильтры.

---

## Q9. Какие встроенные предикаты (Route Predicates) существуют?

`Spring Cloud Gateway` предоставляет набор `RoutePredicateFactory`:

| Предикат | Описание | Пример |
|---|---|---|
| `Path` | Совпадение по пути | `Path=/api/users/**` |
| `Method` | HTTP-метод | `Method=GET,POST` |
| `Header` | Наличие/значение заголовка | `Header=X-Api-Key, \w+` |
| `Query` | Наличие/значение query-параметра | `Query=category, electronics` |
| `Host` | Хост запроса | `Host=**.example.com` |
| `Cookie` | Наличие/значение cookie | `Cookie=session, \w+` |
| `After` | Запрос после указанного времени | `After=2026-01-01T00:00:00+03:00` |
| `Before` | Запрос до указанного времени | `Before=2026-12-31T23:59:59+03:00` |
| `Between` | Запрос в интервале времени | `Between=..., ...` |
| `RemoteAddr` | IP-адрес клиента | `RemoteAddr=192.168.1.0/24` |
| `Weight` | Распределение трафика по весу | `Weight=group1, 80` |
| `CloudFoundryRouteService` | Cloud Foundry-специфичный | -- |

Предикаты комбинируются через логическое И:

```yaml
predicates:
  - Path=/api/v2/**
  - Method=GET
  - Header=Accept, application/json
  # Запрос должен удовлетворять ВСЕМ условиям
```

---

## Q10. (!) Какие типы фильтров существуют в Spring Cloud Gateway?

Фильтры делятся на несколько категорий:

**1. По области применения:**

| Тип | Описание | Пример |
|---|---|---|
| `GatewayFilter` | Применяется к конкретному маршруту | `AddRequestHeader`, `RewritePath` |
| `GlobalFilter` | Применяется ко всем маршрутам | Логирование, метрики, аутентификация |

**2. По фазе выполнения:**

| Фаза | Когда выполняется | Типичное использование |
|---|---|---|
| **Pre-filter** | До отправки запроса в backend | Аутентификация, добавление заголовков, rate limiting |
| **Post-filter** | После получения ответа от backend | Модификация ответа, добавление заголовков, логирование |

**3. Основные встроенные GatewayFilter фабрики:**

- **Заголовки**: `AddRequestHeader`, `AddResponseHeader`, `RemoveRequestHeader`, `RemoveResponseHeader`, `SetRequestHeader`, `SetResponseHeader`
- **Пути**: `RewritePath`, `StripPrefix`, `PrefixPath`, `SetPath`
- **Параметры**: `AddRequestParameter`, `RemoveRequestParameter`
- **Отказоустойчивость**: `CircuitBreaker`, `Retry`, `RequestRateLimiter`
- **Размер**: `RequestSize` -- ограничение размера тела запроса
- **Редиректы**: `RedirectTo` -- HTTP-редирект
- **Тело**: `ModifyRequestBody`, `ModifyResponseBody` -- трансформация тела

---

## Q11. (!) Как написать кастомный GatewayFilter?

Кастомный фильтр создаётся через наследование от `AbstractGatewayFilterFactory`:

```java
@Component
public class RequestTimingGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RequestTimingGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(RequestTimingGatewayFilterFactory.class);

    public RequestTimingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            long startTime = System.currentTimeMillis();
            String path = exchange.getRequest().getURI().getPath();

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long duration = System.currentTimeMillis() - startTime;
                if (config.isLogEnabled()) {
                    log.info("Request to {} took {} ms", path, duration);
                }
                if (duration > config.getThresholdMs()) {
                    log.warn("Slow request to {}: {} ms (threshold: {} ms)",
                             path, duration, config.getThresholdMs());
                }
            }));
        };
    }

    @Data
    public static class Config {
        private boolean logEnabled = true;
        private long thresholdMs = 1000;
    }
}
```

Использование в YAML:

```yaml
filters:
  - name: RequestTiming
    args:
      logEnabled: true
      thresholdMs: 500
```

Фильтр выше является **и pre-, и post-фильтром**: код до `chain.filter()` -- pre-фаза, код в `then()` -- post-фаза.

---

## Q12. Что такое GlobalFilter и чем он отличается от GatewayFilter?

**`GlobalFilter`** автоматически применяется ко всем маршрутам без явного указания в конфигурации, в отличие от `GatewayFilter`, который привязывается к конкретному маршруту.

```java
@Component
public class RequestLoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = UUID.randomUUID().toString();
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header("X-Request-Id", requestId)
            .build();

        log.info("[{}] {} {} from {}",
            requestId,
            exchange.getRequest().getMethod(),
            exchange.getRequest().getURI().getPath(),
            exchange.getRequest().getRemoteAddress());

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -1; // чем ниже число, тем раньше выполняется
    }
}
```

| Аспект | `GatewayFilter` | `GlobalFilter` |
|---|---|---|
| Область | Конкретный маршрут | Все маршруты |
| Конфигурация | В YAML/DSL маршрута | Автоматически через `@Component` |
| Создание | `AbstractGatewayFilterFactory` | Реализация `GlobalFilter` |
| Типичное применение | Трансформация, retry | Логирование, аутентификация, трейсинг |

---

## Q13. Как работает цепочка фильтров и порядок их выполнения?

Фильтры выполняются в определённом порядке, который контролируется интерфейсом `Ordered`:

```mermaid
graph TD
    REQ[Входящий запрос] --> GF1[Global Pre-Filter<br/>order = -2]
    GF1 --> GF2[Global Pre-Filter<br/>order = -1]
    GF2 --> RF1[Route Pre-Filter<br/>order = 1]
    RF1 --> RF2[Route Pre-Filter<br/>order = 2]
    RF2 --> SVC[Backend Service]
    SVC --> RF2P[Route Post-Filter<br/>order = 2]
    RF2P --> RF1P[Route Post-Filter<br/>order = 1]
    RF1P --> GF2P[Global Post-Filter<br/>order = -1]
    GF2P --> GF1P[Global Post-Filter<br/>order = -2]
    GF1P --> RES[Ответ клиенту]
```

**Правила порядка:**

1. **Pre-фильтры** -- выполняются в порядке возрастания `order` (от наименьшего к наибольшему)
2. **Post-фильтры** -- выполняются в обратном порядке (от наибольшего к наименьшему)
3. `GlobalFilter` и `GatewayFilter` объединяются в единую цепочку и сортируются вместе
4. Без явного `order` фильтры выполняются в порядке объявления

---

## Q14. Какие встроенные фильтры для трансформации запросов и ответов существуют?

**Трансформация запросов:**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: transform-example
          uri: http://backend:8080
          predicates:
            - Path=/api/**
          filters:
            # Добавление заголовка
            - AddRequestHeader=X-Tenant-Id, default
            # Удаление заголовка
            - RemoveRequestHeader=Cookie
            # Перезапись пути
            - RewritePath=/api/(?<segment>.*), /internal/${segment}
            # Удаление префикса (1 сегмент)
            - StripPrefix=1
            # Добавление query-параметра
            - AddRequestParameter=source, gateway
            # Ограничение размера запроса
            - RequestSize=5000000  # 5 MB
```

**Трансформация ответов (программно):**

```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("modify-response", r -> r
            .path("/api/data/**")
            .filters(f -> f.modifyResponseBody(String.class, String.class,
                (exchange, body) -> {
                    // Оборачиваем ответ в envelope
                    return Mono.just("{\"status\":\"ok\",\"data\":" + body + "}");
                }))
            .uri("http://data-service:8080"))
        .build();
}
```

---

## Q15. (!) Как реализовать аутентификацию и авторизацию через API Gateway?

Есть два подхода:

**Подход 1: Gateway как точка аутентификации** (рекомендуемый)

Gateway проверяет токен и передаёт информацию о пользователе downstream-сервисам через заголовки:

```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtDecoder jwtDecoder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7);
            Jwt jwt = jwtDecoder.decode(token);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", jwt.getSubject())
                .header("X-User-Roles", String.join(",", jwt.getClaimAsStringList("roles")))
                .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100; // выполняется одним из первых
    }
}
```

**Подход 2: Spring Security с OAuth 2.0 Resource Server**

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.example.com/realms/my-realm
```

```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/public/**").permitAll()
                .pathMatchers("/api/admin/**").hasRole("ADMIN")
                .anyExchange().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build();
    }
}
```

---

## Q16. Как настроить CORS в Spring Cloud Gateway?

**Глобальная CORS-конфигурация через YAML:**

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins:
              - "https://frontend.example.com"
              - "https://admin.example.com"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders: "*"
            exposedHeaders:
              - "X-Request-Id"
              - "X-Total-Count"
            allowCredentials: true
            maxAge: 3600
```

**Программная конфигурация:**

```java
@Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOrigin("https://frontend.example.com");
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsWebFilter(source);
}
```

**Важный нюанс**: если CORS настроен и на Gateway, и на backend-сервисе, заголовки могут дублироваться. Используйте фильтр `DedupeResponseHeader` для решения:

```yaml
filters:
  - DedupeResponseHeader=Access-Control-Allow-Origin Access-Control-Allow-Credentials, RETAIN_UNIQUE
```

---

## Q17. Как реализовать OAuth 2.0 Token Relay через Gateway?

**Token Relay** -- это паттерн, при котором `API Gateway` выступает `OAuth 2.0 Client`, получает токен от Authorization Server и передаёт (`relay`) его downstream-сервисам.

```mermaid
sequenceDiagram
    participant U as User
    participant GW as API Gateway<br/>(OAuth2 Client)
    participant AS as Auth Server<br/>(Keycloak)
    participant SVC as Backend Service<br/>(Resource Server)

    U->>GW: GET /api/resource (без токена)
    GW->>AS: Redirect to login
    U->>AS: Вводит credentials
    AS->>GW: Authorization Code
    GW->>AS: Exchange code → Access Token
    GW->>SVC: GET /resource + Bearer Token
    SVC->>GW: Response
    GW->>U: Response
```

**Конфигурация:**

```yaml
spring:
  security:
    oauth2:
      client:
        provider:
          keycloak:
            issuer-uri: https://auth.example.com/realms/my-realm
        registration:
          keycloak:
            client-id: gateway-client
            client-secret: ${KEYCLOAK_SECRET}
            scope: openid,profile
  cloud:
    gateway:
      routes:
        - id: protected-service
          uri: lb://protected-service
          predicates:
            - Path=/api/protected/**
          filters:
            - TokenRelay=  # передаёт токен downstream
```

Фильтр `TokenRelay` автоматически подставляет `Authorization: Bearer <token>` в запросы к backend-сервисам.

---

## Q18. (!) Как реализовать Rate Limiting в API Gateway?

`Spring Cloud Gateway` предоставляет встроенный `RequestRateLimiter` на основе `Redis` с алгоритмом `Token Bucket`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: rate-limited-service
          uri: lb://api-service
          predicates:
            - Path=/api/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10   # запросов в секунду
                redis-rate-limiter.burstCapacity: 20   # максимальный всплеск
                redis-rate-limiter.requestedTokens: 1  # токенов за запрос
                key-resolver: "#{@userKeyResolver}"
  data:
    redis:
      host: localhost
      port: 6379
```

**Key Resolver** определяет, по какому ключу считать лимиты:

```java
@Configuration
public class RateLimiterConfig {

    // По пользователю (из JWT)
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(
            exchange.getRequest().getHeaders().getFirst("X-User-Id"))
            .defaultIfEmpty("anonymous");
    }

    // По IP-адресу
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    // По API key
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.justOrEmpty(
            exchange.getRequest().getHeaders().getFirst("X-Api-Key"))
            .defaultIfEmpty("no-key");
    }
}
```

При превышении лимита Gateway возвращает `429 Too Many Requests` с заголовками:
- `X-RateLimit-Remaining` -- оставшееся количество запросов
- `X-RateLimit-Replenish-Rate` -- скорость пополнения
- `X-RateLimit-Burst-Capacity` -- максимальный всплеск

---

## Q19. (!) Как интегрировать Circuit Breaker с API Gateway?

`Spring Cloud Gateway` интегрируется с `Resilience4j` через фильтр `CircuitBreaker`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - name: CircuitBreaker
              args:
                name: orderServiceCB
                fallbackUri: forward:/fallback/orders
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY,SERVICE_UNAVAILABLE
                methods: GET
                backoff:
                  firstBackoff: 100ms
                  maxBackoff: 500ms
                  factor: 2

# Конфигурация Resilience4j
resilience4j:
  circuitbreaker:
    instances:
      orderServiceCB:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
  timelimiter:
    instances:
      orderServiceCB:
        timeoutDuration: 3s
```

**Fallback-контроллер:**

```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/orders")
    public Mono<Map<String, String>> ordersFallback() {
        return Mono.just(Map.of(
            "status", "SERVICE_UNAVAILABLE",
            "message", "Order service is temporarily unavailable. Please try again later."
        ));
    }
}
```

Подробнее о паттернах отказоустойчивости -- в [[resilience-patterns-interview|вопросах по Resilience-паттернам]].

---

## Q20. (!) Как API Gateway интегрируется с Service Discovery?

`Spring Cloud Gateway` автоматически интегрируется с `Eureka`, `Consul` или `Kubernetes Service Discovery` через префикс `lb://`:

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true            # автоматическая генерация маршрутов
          lower-case-service-id: true  # сервисы в lowercase
      routes:
        - id: user-service
          uri: lb://user-service   # lb:// = load-balanced через Service Discovery
          predicates:
            - Path=/api/users/**
  application:
    name: api-gateway

eureka:
  client:
    service-url:
      defaultZone: http://eureka:8761/eureka/
```

```mermaid
graph LR
    GW[API Gateway] -->|"lb://user-service"| LB[Load Balancer<br/>Spring Cloud LoadBalancer]
    LB -->|Round Robin| US1[User Service :8081]
    LB -->|Round Robin| US2[User Service :8082]
    LB -->|Round Robin| US3[User Service :8083]
    GW <-->|Registry Lookup| EUR[Eureka Server]
    US1 <-->|Registration| EUR
    US2 <-->|Registration| EUR
    US3 <-->|Registration| EUR
```

Когда `discovery.locator.enabled=true`, Gateway автоматически создаёт маршруты для всех сервисов в реестре: `/SERVICE-NAME/**` -> `lb://SERVICE-NAME`. Это удобно для разработки, но в production лучше явно объявлять маршруты для контроля над тем, какие сервисы экспонируются наружу.

---

## Q21. Как реализовать балансировку нагрузки через Gateway?

`Spring Cloud Gateway` использует `Spring Cloud LoadBalancer` (ранее -- `Ribbon`) для клиентской балансировки:

```java
@Configuration
public class LoadBalancerConfig {

    // Кастомная стратегия балансировки для конкретного сервиса
    @Bean
    @LoadBalancerClient(name = "heavy-service",
        configuration = HeavyServiceLBConfig.class)
    public ReactorLoadBalancer<ServiceInstance> weightedLoadBalancer(
            ServiceInstanceListSupplier supplier) {
        return new RandomLoadBalancer(supplier, "heavy-service");
    }
}
```

**Доступные стратегии:**

| Стратегия | Описание |
|---|---|
| `RoundRobinLoadBalancer` | Циклическое распределение (по умолчанию) |
| `RandomLoadBalancer` | Случайный выбор инстанса |
| Кастомная | Реализация `ReactorServiceInstanceLoadBalancer` |

Подробнее о стратегиях балансировки -- в [[load-balancing-interview|вопросах по балансировке нагрузки]].

---

## Q22. Поддерживает ли Spring Cloud Gateway WebSocket?

Да, `Spring Cloud Gateway` поддерживает проксирование `WebSocket`-соединений. Для этого используется схема `ws://` или `wss://`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: websocket-route
          uri: ws://localhost:8081
          predicates:
            - Path=/ws/**
```

Или через `Service Discovery`:

```yaml
routes:
  - id: websocket-service
    uri: lb:ws://notification-service
    predicates:
      - Path=/ws/notifications/**
```

**Особенности:**

- Gateway выполняет `HTTP Upgrade` для переключения на `WebSocket`-протокол
- Поддерживаются `ws://` и `wss://` (с TLS)
- Фильтры pre/post применяются только к начальному `Upgrade`-запросу, а не к каждому `WebSocket`-фрейму
- Таймауты для `WebSocket` настраиваются отдельно от HTTP:

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        websocket:
          max-frame-payload-length: 65536
```

---

## Q23. Как реализовать версионирование API через Gateway?

Несколько стратегий версионирования на уровне Gateway:

**1. Через URL-путь:**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service-v1
          uri: lb://user-service-v1
          predicates:
            - Path=/api/v1/users/**
          filters:
            - StripPrefix=2  # убирает /api/v1

        - id: user-service-v2
          uri: lb://user-service-v2
          predicates:
            - Path=/api/v2/users/**
          filters:
            - StripPrefix=2
```

**2. Через заголовок:**

```yaml
routes:
  - id: user-service-v1
    uri: lb://user-service-v1
    predicates:
      - Path=/api/users/**
      - Header=X-Api-Version, v1

  - id: user-service-v2
    uri: lb://user-service-v2
    predicates:
      - Path=/api/users/**
      - Header=X-Api-Version, v2
```

**3. Canary-деплой через Weight-предикат:**

```yaml
routes:
  - id: user-service-stable
    uri: lb://user-service-v1
    predicates:
      - Path=/api/users/**
      - Weight=user-group, 90  # 90% трафика

  - id: user-service-canary
    uri: lb://user-service-v2
    predicates:
      - Path=/api/users/**
      - Weight=user-group, 10  # 10% трафика
```

Предикат `Weight` полезен для постепенного переключения трафика на новую версию сервиса.

---

## Q24. Что такое API Composition/Aggregation и как реализовать через Gateway?

**API Composition** -- паттерн, при котором `API Gateway` вызывает несколько backend-сервисов, собирает их ответы и возвращает клиенту единый агрегированный результат.

```mermaid
graph LR
    C[Client] -->|"GET /api/dashboard"| GW[API Gateway]
    GW -->|parallel| US[User Service]
    GW -->|parallel| OS[Order Service]
    GW -->|parallel| RS[Recommendation Service]
    US -->|user data| GW
    OS -->|recent orders| GW
    RS -->|recommendations| GW
    GW -->|aggregated response| C
```

**Реализация через WebClient в кастомном фильтре:**

```java
@Component
public class DashboardAggregationFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!exchange.getRequest().getURI().getPath().equals("/api/dashboard")) {
            return chain.filter(exchange);
        }

        WebClient webClient = webClientBuilder.build();

        Mono<JsonNode> userInfo = webClient.get()
            .uri("lb://user-service/users/me")
            .retrieve().bodyToMono(JsonNode.class);

        Mono<JsonNode> recentOrders = webClient.get()
            .uri("lb://order-service/orders/recent")
            .retrieve().bodyToMono(JsonNode.class);

        return Mono.zip(userInfo, recentOrders)
            .flatMap(tuple -> {
                ObjectNode result = JsonNodeFactory.instance.objectNode();
                result.set("user", tuple.getT1());
                result.set("orders", tuple.getT2());

                byte[] bytes = result.toString().getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return exchange.getResponse().writeWith(Mono.just(buffer));
            });
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```

**Предостережение**: агрегация на уровне Gateway увеличивает его сложность. Для сложных сценариев агрегации лучше использовать отдельный BFF-сервис или [[cqrs-event-sourcing-interview|CQRS]] с предвычисленными представлениями.

---

## Q25. (!) Сравните Spring Cloud Gateway, Kong и Nginx как API Gateway

| Аспект | Spring Cloud Gateway | Kong | Nginx |
|---|---|---|---|
| **Язык/Стек** | Java, Spring WebFlux | Lua + OpenResty (Nginx) | C |
| **Модель** | Реактивная (Netty) | Event-driven | Event-driven |
| **Конфигурация** | YAML / Java DSL | Admin API / YAML / GUI | `nginx.conf` |
| **Расширение** | Java-фильтры | Lua-плагины | C-модули / Lua (OpenResty) |
| **Service Discovery** | Eureka, Consul, K8s | DNS, Consul, K8s | DNS, ручная |
| **Rate Limiting** | Redis Token Bucket | Встроенный (Redis/PostgreSQL) | `ngx_http_limit_req_module` |
| **Аутентификация** | Spring Security | Плагины (JWT, OAuth, LDAP) | Базовая / модули |
| **GUI** | Нет (только код) | Kong Manager / Konga | Nginx Plus Dashboard |
| **Производительность** | Хорошая (JVM warmup) | Очень высокая | Максимальная |
| **Экосистема Spring** | Нативная | Нет | Нет |
| **Лицензия** | Apache 2.0 | Apache 2.0 / Enterprise | BSD / Nginx Plus |

**Когда что выбрать:**

- **Spring Cloud Gateway** -- когда backend на Spring, нужна глубокая интеграция с Spring Security, Eureka, Resilience4j. Идеален для Java-команд
- **Kong** -- когда нужен language-agnostic Gateway с богатой экосистемой плагинов, GUI и enterprise-поддержкой
- **Nginx** -- когда нужна максимальная производительность, SSL termination, статический контент. Часто используется **перед** API Gateway как reverse proxy

На практике часто встречается комбинация: `Nginx/Envoy` (L4/L7 балансировка) -> `Kong/Spring Cloud Gateway` (API management).

---

## Q26. Как организовать мониторинг и логирование API Gateway?

**Метрики через Micrometer + Prometheus:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,gateway
  metrics:
    tags:
      application: api-gateway
    distribution:
      percentiles-histogram:
        spring.cloud.gateway.requests: true
```

**Ключевые метрики Gateway:**

| Метрика | Описание |
|---|---|
| `spring.cloud.gateway.requests` | Количество запросов (по маршруту, статусу) |
| `gateway.requests.duration` | Время обработки запроса |
| `resilience4j.circuitbreaker.state` | Состояние Circuit Breaker |
| `spring.cloud.gateway.routes.count` | Количество активных маршрутов |

**Distributed Tracing через Spring Cloud Sleuth / Micrometer Tracing:**

```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # 100% трейсов в dev, 10-20% в prod
  zipkin:
    tracing:
      endpoint: http://zipkin:9411/api/v2/spans
```

Gateway автоматически добавляет `traceId` и `spanId` в заголовки, обеспечивая сквозную трассировку запроса через все сервисы.

**Централизованное логирование:**

```java
@Component
public class AccessLogGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger("ACCESS_LOG");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.nanoTime();
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            log.info("{} {} {} {} {}ms",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI().getPath(),
                exchange.getResponse().getStatusCode(),
                exchange.getRequest().getRemoteAddress(),
                duration);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
```

---

## Q27. Какие anti-паттерны связаны с API Gateway?

**1. "Толстый" Gateway (God Gateway)**

Размещение бизнес-логики в Gateway. Gateway должен содержать **только инфраструктурную** логику (routing, auth, rate limiting). Бизнес-правила -- в сервисах.

**2. Единая точка отказа без резервирования**

Gateway -- критический компонент. Без горизонтального масштабирования и health checks он становится SPOF:

```mermaid
graph LR
    LB[Load Balancer] --> GW1[Gateway Instance 1]
    LB --> GW2[Gateway Instance 2]
    LB --> GW3[Gateway Instance 3]
    GW1 --> SVCS[Backend Services]
    GW2 --> SVCS
    GW3 --> SVCS
```

**3. Отсутствие timeouts и circuit breakers**

Без таймаутов медленный backend может "подвесить" весь Gateway, исчерпав пул соединений.

**4. Чрезмерная агрегация**

Сложная агрегация данных в Gateway увеличивает latency и coupling. Для сложных сценариев используйте отдельный BFF-сервис.

**5. Один Gateway на все типы клиентов**

Разные клиенты (web, mobile, IoT, партнёры) имеют разные требования к формату данных, безопасности и rate limiting. Используйте паттерн BFF.

**6. Хранение состояния в Gateway**

Gateway должен быть **stateless** для горизонтального масштабирования. Сессии, кэши и лимиты -- во внешних хранилищах (`Redis`, `Memcached`).

**7. Игнорирование мониторинга**

Gateway -- первая точка контакта. Без метрик, трейсов и алертов проблемы в маршрутизации остаются незамеченными до жалоб пользователей.

**На собеседовании** умение назвать anti-паттерны демонстрирует реальный production-опыт и зрелость архитектурного мышления.

## Q28. (!) Как реализовать request/response трансформацию на уровне Gateway?

**Request/Response трансформация** — изменение входящих запросов (добавление заголовков, маппинг путей) и исходящих ответов (фильтрация полей, изменение формата) на уровне Gateway без изменения backend-сервисов.

**Spring Cloud Gateway — встроенные фильтры трансформации:**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/v2/orders/**
          filters:
            # Замена префикса пути: /api/v2/orders → /orders
            - StripPrefix=2

            # Добавление заголовка к запросу (propagate user info)
            - AddRequestHeader=X-Source, api-gateway
            - AddRequestHeader=X-Request-Id, #{T(java.util.UUID).randomUUID()}

            # Удаление чувствительного заголовка из ответа
            - RemoveResponseHeader=X-Internal-Service-Name

            # Добавление параметра запроса
            - AddRequestParameter=version, v2

            # Переписывание пути с regex
            - RewritePath=/api/v2/(?<segment>.*), /$\{segment}
```

**Кастомный фильтр для трансформации тела запроса:**

```java
@Component
public class RequestBodyTransformFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerRequest request = ServerRequest.create(exchange,
            HandlerStrategies.withDefaults().messageReaders());

        Mono<String> modifiedBody = request.bodyToMono(String.class)
            .map(body -> {
                // Трансформация: добавить поле tenantId из JWT
                JsonNode node = objectMapper.readTree(body);
                ((ObjectNode) node).put("tenantId",
                    extractTenantFromJwt(exchange.getRequest()));
                return node.toString();
            });

        BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter =
            BodyInserters.fromPublisher(modifiedBody, String.class);

        // Обновляем запрос с изменённым телом
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange,
            exchange.getRequest().getHeaders());
        return bodyInserter.insert(outputMessage, new BodyInserterContext())
            .then(Mono.defer(() -> {
                ServerHttpRequest decorator = decorate(exchange, outputMessage);
                return chain.filter(exchange.mutate().request(decorator).build());
            }));
    }

    @Override
    public int getOrder() { return -1; }
}
```

**Паттерн трансформации ответа (скрытие внутренних полей):**

```java
@Component
public class ResponseSanitizationFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponseDecorator responseDecorator =
            new ServerHttpResponseDecorator(exchange.getResponse()) {
                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    return super.writeWith(
                        Flux.from(body).map(buf -> {
                            // Читаем, трансформируем, возвращаем
                            String json = readBuffer(buf);
                            String sanitized = removeInternalFields(json);
                            return exchange.getResponse().bufferFactory()
                                .wrap(sanitized.getBytes(StandardCharsets.UTF_8));
                        })
                    );
                }
            };
        return chain.filter(exchange.mutate()
            .response(responseDecorator).build());
    }
}
```

## Q29. Как настроить Kong API Gateway для rate limiting и аутентификации?

**Kong** — высокопроизводительный API Gateway на базе `Nginx` + `OpenResty`, управляемый через декларативный конфиг или Admin API. Широко используется как альтернатива Spring Cloud Gateway для polyglot-архитектур.

**Декларативная конфигурация (kong.yml / deck):**

```yaml
# kong.yml
_format_version: "3.0"

services:
  - name: order-service
    url: http://order-service:8080
    routes:
      - name: order-route
        paths:
          - /api/orders
        methods:
          - GET
          - POST
    plugins:
      # JWT аутентификация
      - name: jwt
        config:
          secret_is_base64: false
          claims_to_verify:
            - exp
          key_claim_name: iss

      # Rate Limiting (скользящее окно)
      - name: rate-limiting
        config:
          minute: 100       # 100 запросов в минуту
          hour: 5000
          policy: redis     # хранить счётчики в Redis (для кластера)
          redis_host: redis
          redis_port: 6379

      # Логирование
      - name: http-log
        config:
          http_endpoint: http://logstash:5044
          method: POST
          content_type: application/json
```

**Сравнение Kong vs Spring Cloud Gateway:**

| Критерий | Kong | Spring Cloud Gateway |
|----------|------|---------------------|
| **Производительность** | Выше (Nginx/C) | Хорошая (Reactor/Java) |
| **Экосистема** | 50+ готовых плагинов | Spring экосистема |
| **Polyglot** | Да (любой бэкенд) | Да |
| **Кастомизация** | Lua/Go плагины | Java фильтры |
| **Операционная сложность** | Выше (DB + Kong) | Ниже (часть Spring Boot) |
| **Динамический конфиг** | Admin API / KongMesh | Spring Cloud Config |

**Практический выбор:**
- **Kong** — когда нужна высокая производительность, много готовых плагинов, polyglot окружение
- **Spring Cloud Gateway** — когда команда Java-ориентирована, нужна тесная интеграция со Spring Cloud (Eureka, Config Server, Resilience4j)

## Q30. (!) Как организовать observability (метрики, трейсинг, логи) для API Gateway?

**Observability** для API Gateway критична: Gateway — единая точка входа, и любые проблемы здесь затрагивают весь трафик.

**Три столпа observability:**

```mermaid
graph TB
    GW[API Gateway] --> Metrics[Метрики\nPrometheus + Grafana]
    GW --> Traces[Трейсинг\nOpenTelemetry + Jaeger]
    GW --> Logs[Логи\nELK / Loki]
```

**Spring Cloud Gateway + Micrometer + OpenTelemetry:**

```yaml
# application.yml
management:
  metrics:
    tags:
      application: api-gateway
    distribution:
      percentiles-histogram:
        spring.cloud.gateway.requests: true
      slo:
        spring.cloud.gateway.requests: 50ms,200ms,500ms,1s,2s

  tracing:
    sampling:
      probability: 0.1  # 10% в prod (100% в dev)

otel:
  exporter:
    otlp:
      endpoint: http://otel-collector:4318
```

**Ключевые метрики API Gateway (Prometheus/Grafana):**

```promql
# RPS по маршрутам
sum(rate(spring_cloud_gateway_requests_total[1m])) by (routeId)

# Процент ошибок
sum(rate(spring_cloud_gateway_requests_total{outcome="SERVER_ERROR"}[5m]))
/ sum(rate(spring_cloud_gateway_requests_total[5m])) * 100

# p99 latency по маршрутам
histogram_quantile(0.99,
  sum(rate(spring_cloud_gateway_requests_seconds_bucket[5m])) by (le, routeId)
)

# Количество активных circuit breakers (OPEN)
spring_cloud_circuit_breaker_state{state="OPEN"}
```

**Структурированное логирование запросов:**

```java
@Component
public class AccessLogFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger("ACCESS_LOG");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        return chain.filter(exchange).doFinally(signal -> {
            long duration = System.currentTimeMillis() - startTime;
            HttpStatusCode status = exchange.getResponse().getStatusCode();

            log.info("""
                method={} path={} status={} duration={}ms traceId={} clientIp={}
                """.stripIndent().trim(),
                request.getMethod(),
                request.getPath(),
                status != null ? status.value() : "unknown",
                duration,
                exchange.getRequest().getHeaders().getFirst("traceparent"),
                request.getRemoteAddress()
            );
        });
    }

    @Override
    public int getOrder() { return Ordered.LOWEST_PRECEDENCE; }
}
```

**SLO для API Gateway (рекомендации):**
- **Availability**: 99.9% (< 8.7 ч. даунтайма/год)
- **Latency p50**: < 10мс оверхед Gateway
- **Latency p99**: < 50мс оверхед Gateway
- **Error rate**: < 0.1% 5xx ошибок (вызванных Gateway, не backend)

---

## Q31. (!) Алгоритмы Rate Limiting: Token Bucket, Leaky Bucket, Fixed Window, Sliding Window

Четыре основных алгоритма ограничения частоты запросов — каждый с разными характеристиками по точности, потреблению памяти и поведению при burst-трафике.

### Token Bucket

Клиент имеет «ведро» с токенами. Каждый запрос потребляет токен; токены пополняются с фиксированной скоростью. При пустом ведре запросы отклоняются.

```
bucket capacity = 100 tokens
refill rate     = 10 tokens/sec

request arrived → token available? → consume token → allow
                                   → no token      → reject (429)
```

**Плюсы:** допускает burst до размера ведра; интуитивная модель.  
**Минусы:** требует хранить состояние (capacity + current\_tokens + last\_refill).  
**Реализация в Spring Cloud Gateway:**

```yaml
filters:
  - name: RequestRateLimiter
    args:
      redis-rate-limiter.replenishRate: 10    # токенов/сек
      redis-rate-limiter.burstCapacity: 100   # размер ведра
      redis-rate-limiter.requestedTokens: 1
      key-resolver: "#{@userKeyResolver}"
```

Gateway хранит состояние ведра в Redis с помощью атомарного Lua-скрипта (два ключа: `{key}.tokens` и `{key}.timestamp`).

### Leaky Bucket

Запросы поступают в «ведро» (очередь) и обрабатываются с постоянной скоростью («вытекают»). Если ведро переполнено — запросы отбрасываются.

```
incoming requests → [queue / bucket] → process at fixed rate → downstream
                 → bucket full      → drop request
```

**Плюсы:** гарантирует равномерный поток к backend, защищает от burst.  
**Минусы:** добавляет задержку (queuing); высокий burst → большое время ожидания.  
**Применение:** защита downstream-сервисов с низкой пропускной способностью.

### Fixed Window

Временная шкала делится на окна фиксированной длины (например, 1 мин). Считается число запросов в текущем окне.

```
window: [00:00 – 01:00]  count = 0
request at 00:30         count = 1
request at 00:59         count = 99
request at 00:59         count = 100 → reject
window reset at 01:00    count = 0
```

**Проблема:** «граничный всплеск» — 100 запросов в 00:59 + 100 в 01:00 = 200 за 2 секунды.  
**Плюсы:** простота, O(1) память (один счётчик + TTL).  
**Реализация:** `INCR key + EXPIRE key` в Redis.

### Sliding Window (Log / Counter)

**Sliding Window Log:** хранит timestamp каждого запроса в множестве. Считает запросы в `[now - window, now]`.

```
# Redis ZSET: score = timestamp (ms)
ZADD key <timestamp> <uuid>
ZREMRANGEBYSCORE key 0 <now - window_ms>
ZCARD key  →  compare with limit
```

**Плюсы:** точное ограничение, нет граничного всплеска.  
**Минусы:** память O(requests\_per\_window), дороже для высоких лимитов.

**Sliding Window Counter (гибрид):** хранит счётчики двух соседних фиксированных окон и интерполирует:

```
rate = prev_window_count × (1 - elapsed/window) + curr_window_count
```

**Плюсы:** O(1) память, хорошая точность.  
**Применение:** Cloudflare использует этот подход.

### Сравнение алгоритмов

| Алгоритм | Burst | Точность | Память | Сложность |
|---|---|---|---|---|
| Token Bucket | разрешён | высокая | O(1) | средняя |
| Leaky Bucket | выравнивает | высокая | O(queue) | средняя |
| Fixed Window | граничный всплеск | низкая | O(1) | низкая |
| Sliding Log | нет | максимальная | O(N) | высокая |
| Sliding Counter | минимальный | высокая | O(1) | средняя |

---

## Q32. (!) API Gateway vs Service Mesh: когда что выбирать?

### API Gateway

Работает на **North-South** трафике (клиент → кластер). Основная задача — управление внешним входом.

```
Internet → [API Gateway] → Service A
                         → Service B
                         → Service C
```

**Функции:** аутентификация/авторизация, rate limiting, SSL termination, маршрутизация, трансформация запросов, API versioning, документация (Developer Portal).

**Примеры:** Kong, AWS API Gateway, Spring Cloud Gateway, Nginx, Traefik.

### Service Mesh

Работает на **East-West** трафике (сервис ↔ сервис внутри кластера). Реализуется через sidecar-прокси (Envoy) рядом с каждым подом.

```
Service A → [Envoy sidecar] → [Envoy sidecar] → Service B
```

**Функции:** mTLS между сервисами, observability (трейсинг, метрики), retry/circuit breaker, traffic splitting (канареечный деплой), load balancing.

**Примеры:** Istio (Envoy), Linkerd (Rust proxy), Consul Connect.

### Сравнение

| Критерий | API Gateway | Service Mesh |
|---|---|---|
| Трафик | North-South | East-West |
| Аутентификация | внешних клиентов | между сервисами (mTLS) |
| Rate Limiting | внешние лимиты | между сервисами |
| Операционная сложность | низкая | высокая |
| Область применения | граница кластера | внутри кластера |

### Когда что выбирать

**Только API Gateway** — стартап или небольшая система без мощной mesh-инфраструктуры; нет команды DevOps для операционного обслуживания Istio.

**Только Service Mesh** — сервисы общаются преимущественно между собой; нужен mTLS без кода в приложениях; нужен observability out-of-the-box.

**Оба вместе** (наиболее частый production-сценарий):

```
Internet → API Gateway (аутентификация, rate limit, SSL)
         → Service Mesh (mTLS, retry, circuit breaker между сервисами)
         → Service A, B, C
```

API Gateway не заменяет Service Mesh и наоборот — они дополняют друг друга.

---

## Q33. Как работает gRPC-Web через API Gateway?

### Проблема

gRPC использует HTTP/2 с двунаправленным стримингом, но браузеры не поддерживают gRPC напрямую (ограничения `fetch` API, отсутствие поддержки trailers). Поэтому gRPC-Web — облегчённый протокол-обёртка поверх HTTP/1.1 или HTTP/2.

### Схема работы

```
Browser (gRPC-Web client)
    → HTTP/1.1 POST /ServiceName/MethodName
    → Content-Type: application/grpc-web+proto
    → [API Gateway: gRPC-Web → gRPC транскодинг]
    → HTTP/2 gRPC к backend-сервису
```

### Реализация в Nginx / Envoy

Envoy (и Istio) поддерживают gRPC-Web фильтр нативно:

```yaml
# Envoy filter
http_filters:
  - name: envoy.filters.http.grpc_web
  - name: envoy.filters.http.router
```

Kong поддерживает плагин `grpc-web`. Spring Cloud Gateway — через кастомный фильтр или использование Envoy перед Gateway.

### Особенности

- **Trailers:** gRPC-Web инкапсулирует trailers в тело ответа (не в HTTP trailers), т.к. браузеры не читают HTTP trailers.
- **Стриминг:** gRPC-Web поддерживает server streaming, но не client/bidi streaming (полная поддержка через WebSocket транспорт в gRPC-Web).
- **Content-Type:** `application/grpc-web+proto` (binary) или `application/grpc-web+json` (для отладки).
- **CORS:** нужно явно разрешить заголовки `grpc-status`, `grpc-message`, `content-type`.

### Альтернатива: gRPC транскодинг в JSON

Gateway может транслировать HTTP/JSON ↔ gRPC автоматически через аннотации `google.api.http` в `.proto`:

```protobuf
service UserService {
  rpc GetUser(GetUserRequest) returns (User) {
    option (google.api.http) = {
      get: "/v1/users/{user_id}"
    };
  }
}
```

Envoy/Kong выполняют транскодинг по `.proto`-дескрипторам.

---

## Q34. WebSocket через API Gateway: sticky sessions и масштабирование

### Проблема

WebSocket — persistent-соединение (HTTP Upgrade). После установки соединения клиент и сервер обмениваются сообщениями без переустановки. Это создаёт проблемы при горизонтальном масштабировании Gateway.

### Sticky Sessions

Если Gateway горизонтально масштабируется, одно соединение всегда должно попадать к одному инстансу Gateway, который держит его состояние.

```
Client → [Load Balancer]
              → Gateway-1 (держит WS соединение клиента A)
              → Gateway-2 (держит WS соединение клиента B)
```

**Решение — IP Hash или Cookie-based sticky:**

```nginx
upstream gateways {
    ip_hash;  # один клиент → один upstream
    server gateway-1:8080;
    server gateway-2:8080;
}
```

В Kubernetes: `sessionAffinity: ClientIP` в Service, или аннотации Ingress (`nginx.ingress.kubernetes.io/affinity: cookie`).

### Spring Cloud Gateway + WebSocket

Spring Cloud Gateway поддерживает WebSocket проксирование нативно (Reactor Netty):

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: ws-route
          uri: ws://chat-service:8080
          predicates:
            - Path=/ws/**
```

### Проблемы при масштабировании

**1. In-memory state:** если сервер хранит состояние соединения в памяти (список активных соединений) — при рестарте соединения рвутся.

**Решение:** externalize state в Redis. Spring WebSocket с `spring-session` + Redis Pub/Sub для распределённого broadcast.

```java
// Broadcaster через Redis Pub/Sub
redisTemplate.convertAndSend("ws-channel", message);
```

**2. Health checks:** балансировщик должен проверять health именно на уровне L7, т.к. WebSocket keepalive ≠ HTTP health.

**3. Timeout:** большинство load balancer'ов имеют timeout на idle соединения (AWS ALB: 60 сек по умолчанию). Нужно настроить `ping/pong` heartbeat или увеличить таймаут:

```yaml
# AWS ALB idle timeout
aws_lb.idle_timeout = 3600  # 1 hour
```

**4. Horizontal Pod Autoscaler:** при добавлении новых подов существующие соединения не перераспределяются — клиенты остаются на старых подах. Нужно изящное завершение (`graceful shutdown`):

```java
// Отправить close frame перед остановкой
session.close(CloseStatus.SERVICE_RESTARTED);
```

---

## Q35. API Gateway в serverless архитектуре (AWS API Gateway)

### AWS API Gateway — варианты

**REST API** — полнофункциональный Gateway с моделями, валидацией, кастомными авторизаторами (Lambda Authorizer).

**HTTP API** — легковесный и дешевый (~70% дешевле REST API), JWT-авторизация нативно, минимум возможностей трансформации.

**WebSocket API** — управляет WebSocket соединениями, хранит connection ID в DynamoDB.

### Интеграция с Lambda

```
Client → API Gateway → Lambda Function → Response
```

**Lambda Proxy Integration:** весь запрос передаётся в Lambda как JSON, ответ тоже JSON:

```json
{
  "statusCode": 200,
  "headers": {"Content-Type": "application/json"},
  "body": "{\"message\": \"ok\"}"
}
```

**Lambda Authorizer:** кастомная функция для аутентификации/авторизации перед вызовом основной Lambda. Возвращает IAM Policy:

```json
{
  "principalId": "user123",
  "policyDocument": {
    "Statement": [{"Effect": "Allow", "Action": "execute-api:Invoke", "Resource": "arn:..."}]
  }
}
```

### Особенности serverless Gateway

**Cold Start:** Lambda функция «засыпает» при отсутствии трафика — первый запрос медленнее (100-500мс). Решение: Provisioned Concurrency.

**Timeout:** AWS API Gateway имеет жёсткий timeout 29 секунд на интеграцию — Lambda не может выполняться дольше для синхронных запросов.

**Rate Limiting:** встроен на уровне Usage Plans — `throttlingBurstLimit` (burst) + `throttlingRateLimit` (steady-state rps).

**Стоимость:** оплата за вызов (~$3.5 за млн запросов для HTTP API). При высоком трафике сравнивать с Fargate/EKS.

### Когда AWS API Gateway уместен

- Событийная архитектура с Lambda
- Прототипирование и MVP
- Нерегулярный трафик (serverless экономит при idle)
- Нужен быстрый старт без операционного overhead

---

## Q36. GraphQL через API Gateway: federation и schema stitching

### Зачем GraphQL через Gateway

GraphQL endpoint — единый (обычно `POST /graphql`), но логика может быть размазана по нескольким сервисам. Gateway решает: маршрутизацию к нужным сервисам, аутентификацию, rate limiting по сложности запроса.

### Schema Stitching (устаревший подход)

Gateway объединяет несколько GraphQL схем в одну, делегируя запросы к нужному сервису:

```
Client → Gateway (merged schema) → UserService (User schema)
                                 → ProductService (Product schema)
```

**Проблемы:** хрупкость при изменении схем, сложная отладка, Gateway знает о внутренних схемах.

### Apollo Federation (современный подход)

Каждый сервис владеет своей частью схемы. Специальная `@key` директива позволяет ссылаться на типы из других сервисов:

```graphql
# UserService
type User @key(fields: "id") {
  id: ID!
  name: String!
}

# OrderService
type Order {
  id: ID!
  user: User  # ссылка на тип из UserService
}
```

**Apollo Router** (или Gateway) строит план выполнения запроса:

```
Query { order { user { name } } }
  → OrderService: GET order.id, user.id
  → UserService:  GET user.name by user.id (representation)
  → merge results
```

### Rate Limiting для GraphQL

Стандартный rate limiting по RPS не подходит — один запрос может быть крайне сложным:

```graphql
{ users { orders { items { product { reviews { ... } } } } } }
```

**Query Complexity:** каждому полю назначается вес, запрос отклоняется при превышении лимита:

```java
// graphql-java
.instrumentation(new MaxQueryComplexityInstrumentation(100))
.instrumentation(new MaxQueryDepthInstrumentation(10))
```

**Persisted Queries:** клиент отправляет только hash запроса, Gateway кэширует и проксирует. Предотвращает произвольные запросы:

```
POST /graphql
{"id": "abc123hash"}  // вместо полного query body
```

---

## Q37. (!) Стратегии версионирования API через Gateway

### 1. URI Versioning

```
GET /api/v1/users
GET /api/v2/users
```

**Реализация в Gateway:**

```yaml
routes:
  - id: users-v1
    uri: lb://users-service-v1
    predicates:
      - Path=/api/v1/users/**
  - id: users-v2
    uri: lb://users-service-v2
    predicates:
      - Path=/api/v2/users/**
```

**Плюсы:** явность, простота кэширования, легко тестировать.  
**Минусы:** нарушает REST (URI должен идентифицировать ресурс, не версию).

### 2. Header Versioning

```
GET /api/users
Accept-Version: v2
# или
X-API-Version: 2
```

**Реализация через предикат на заголовок:**

```yaml
routes:
  - id: users-v2
    uri: lb://users-service
    predicates:
      - Path=/api/users/**
      - Header=Accept-Version, v2
  - id: users-v1
    uri: lb://users-service
    predicates:
      - Path=/api/users/**
```

### 3. Content Negotiation (Media Type Versioning)

```
GET /api/users
Accept: application/vnd.company.api+json;version=2
```

Чистый REST-подход, но сложнее в реализации и тестировании.

### 4. Query Parameter Versioning

```
GET /api/users?version=2
```

Простой, но загрязняет URL — не рекомендуется.

### Стратегии управления версиями на Gateway

**Canary по версиям:** новая версия API получает % трафика:

```yaml
filters:
  - name: RequestHeaderToRequestUri
  - name: SetPath
    args:
      template: /v2{path}
```

**Sunset заголовок:** информирование клиентов об устаревании:

```java
// GlobalFilter для устаревших версий
exchange.getResponse().getHeaders()
    .add("Sunset", "Sat, 31 Dec 2026 23:59:59 GMT");
exchange.getResponse().getHeaders()
    .add("Deprecation", "true");
```

**Рекомендация:** URI versioning — де-факто стандарт для публичных API. Header versioning — для внутренних или partner API. Поддерживать не более 2 версий одновременно, устанавливать deadline для депрекации.

---

## Q38. (!) Retry и Circuit Breaker на уровне API Gateway

### Retry на уровне Gateway

Retry помогает при временных сбоях (сетевые ошибки, 503 от перегруженного сервиса). Ключевые параметры: количество попыток, условия retry, backoff стратегия.

**Spring Cloud Gateway — Retry фильтр:**

```yaml
filters:
  - name: Retry
    args:
      retries: 3
      statuses: BAD_GATEWAY,SERVICE_UNAVAILABLE   # 502, 503
      methods: GET,HEAD                            # только идемпотентные!
      backoff:
        firstBackoff: 50ms
        maxBackoff: 500ms
        factor: 2
        basedOnPreviousValue: false
```

**Важно:** retry только для идемпотентных методов (GET, HEAD, OPTIONS). POST/PUT с retry без идемпотентности могут создавать дублирующиеся записи.

**Exponential Backoff с Jitter:**

```
retry 1: wait 50ms  + random(0-25ms)
retry 2: wait 100ms + random(0-50ms)
retry 3: wait 200ms + random(0-100ms)
```

Jitter предотвращает «thundering herd» — одновременный шторм повторных запросов от тысяч клиентов.

### Circuit Breaker на уровне Gateway

Circuit Breaker защищает от каскадных сбоев. Три состояния: CLOSED (работа в норме) → OPEN (запросы отклоняются) → HALF-OPEN (проверка).

**Spring Cloud Gateway + Resilience4j:**

```yaml
filters:
  - name: CircuitBreaker
    args:
      name: payment-cb
      fallbackUri: forward:/fallback/payment
      statusCodes:
        - 500
        - 503
```

```java
@Bean
public Customizer<ReactiveResilience4JCircuitBreakerFactory> cbConfig() {
    return factory -> factory.configure(builder -> builder
        .circuitBreakerConfig(CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .failureRateThreshold(50)          // 50% ошибок → OPEN
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build()
        ), "payment-cb");
}
```

**Fallback endpoint:**

```java
@RestController
public class FallbackController {
    @GetMapping("/fallback/payment")
    public Mono<ResponseEntity<Map<String, String>>> paymentFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Map.of(
                "message", "Payment service is temporarily unavailable",
                "retryAfter", "30"
            )));
    }
}
```

### Retry vs Circuit Breaker — взаимодействие

```
Request → [Circuit Breaker: CLOSED?]
              → yes → [Retry: attempt 1,2,3]
                           → success → response
                           → all failed → Circuit Breaker records failure
              → no (OPEN) → fallback immediately (fail fast)
```

**Антипаттерн:** retry поверх открытого Circuit Breaker — Resilience4j предотвращает это автоматически (CB перехватывает до retry).

**Таймауты:** всегда устанавливать timeout на уровне Gateway + retry:

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 1000   # 1s на установку соединения
        response-timeout: 5s    # 5s на ответ
```

Без таймаутов retry может «зависнуть» ожидая ответа.

---

## See also

- [[microservices-interview|Микросервисы]] — паттерн API Gateway как точка входа в микросервисную систему
- [[spring-cloud-interview|Spring Cloud]] — экосистема Spring Cloud: Service Discovery, Config Server, Circuit Breaker
- [[load-balancing-interview|Балансировка нагрузки]] — интеграция Gateway с Ribbon/Spring Cloud LoadBalancer для распределения трафика
- [[resilience-patterns-interview|Паттерны отказоустойчивости]] — Circuit Breaker, Retry и Bulkhead на уровне Gateway
- [[distributed-systems-interview|Распределённые системы]] — API Gateway как facade для распределённой системы
- [[networking-interview|Сетевые протоколы]] — HTTP/2, TLS termination и WebSocket proxying на уровне Gateway
- [[http-rest-interview|HTTP & REST]] — версионирование API, CORS и трансформация запросов/ответов
- [[caching-strategies-interview|Стратегии кэширования]] — кэширование ответов на уровне Gateway для снижения нагрузки

- [[bff-pattern-interview|BFF Pattern]]
- [[caching-strategies-interview|Стратегии кэширования]]
- [[cap-theorem-interview|CAP-теорема]]
- [[clean-architecture-interview|Clean Architecture]]
- [[consistency-patterns-interview|Паттерны согласованности]]
- [[cqrs-event-sourcing-interview|CQRS и Event Sourcing]]
