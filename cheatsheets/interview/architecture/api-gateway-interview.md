---
title: "Вопросы на собеседовании: API Gateway"
description: "Полное покрытие паттерна API Gateway: маршрутизация, аутентификация, rate limiting, трансформация запросов, агрегация, Spring Cloud Gateway, фильтры, предикаты, BFF, безопасность, CORS, версионирование API."
tags:
  - interview
  - architecture
  - api-gateway-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "API Gateway"
  - "API Gateway interview"
  - "API Gateway собеседование"
prerequisites:
  - "[[api-gateway]]"
next: []
updated: "2026-05-14"
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

**API Gateway** -- единая точка входа для всех клиентских запросов в микросервисной архитектуре. Он принимает входящий запрос, маршрутизирует его к нужному backend-сервису (или сразу к нескольким), при необходимости агрегирует ответы и возвращает результат клиенту. По сути это фасад перед группой сервисов: клиент видит один API, а за ним скрыта вся внутренняя структура системы.

Схема потока: разные клиенты обращаются к единому Gateway, а тот разводит запросы по backend-сервисам.

- `Web Client` → `API Gateway`
- `Mobile Client` → `API Gateway`
- `Partner API` → `API Gateway`
- `API Gateway` → `User Service`
- `API Gateway` → `Order Service`
- `API Gateway` → `Product Service`
- `API Gateway` → `Payment Service`

**Какие проблемы он решает:**

- **Единая точка входа** -- клиенты обращаются к одному URL вместо десятков адресов сервисов.
- **Скрытие внутренней структуры** -- клиент не знает, сколько сервисов за Gateway и где они расположены; сервисы можно дробить и перемещать, не ломая клиента.
- **Инкапсуляция cross-cutting concerns** -- аутентификация, логирование, rate limiting пишутся один раз в Gateway, а не дублируются в каждом сервисе.
- **Упрощение клиентского кода** -- клиент не занимается обнаружением сервисов и агрегацией данных; вместо пяти вызовов делает один.
- **Протокольная трансляция** -- наружу можно отдавать REST, а внутри общаться по gRPC.

**Что было бы без Gateway:** каждый клиент знал бы адреса всех сервисов и сам обрабатывал бы ошибки, аутентификацию и ретраи. Эта логика расползлась бы по всем клиентам (web, mobile, партнёры), а любое изменение топологии сервисов ломало бы их. Gateway убирает это дублирование и тесную связанность.

---

## Q2. (!) Какие основные обязанности API Gateway?

API Gateway берёт на себя инфраструктурные задачи (cross-cutting concerns), которые иначе пришлось бы реализовывать в каждом сервисе. Их удобно разбить на три группы: маршрутизация трафика, безопасность и контроль нагрузки, наблюдаемость.

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

**Что важно на собеседовании.** Граница ответственности проходит между инфраструктурной и бизнес-логикой. Всё из таблицы выше -- инфраструктура, ей место в Gateway. Бизнес-правила (расчёт цены, валидация заказа, права на конкретный ресурс) **никогда** не должны жить в Gateway: это превращает его в «толстый» God Gateway -- классический anti-паттерн (см. Q27). Если для маршрутизации нужно знать бизнес-смысл данных -- это сигнал, что логику тянут не туда.

---

## Q3. (!) Чем API Gateway отличается от Reverse Proxy?

Короткий ответ: **Reverse Proxy** просто перенаправляет и балансирует трафик, а **API Gateway** управляет всем жизненным циклом API. Gateway -- это reverse proxy «на стероидах»: он умеет всё то же самое плюс аутентификацию, rate limiting по пользователю, агрегацию ответов и API-менеджмент. Это частый вопрос: интервьюер проверяет, понимаете ли вы границу между инфраструктурным компонентом и архитектурным паттерном.

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

**Где граница размывается.** Различие не абсолютное, а по объёму функциональности: reverse proxy решает задачу транспорта, Gateway -- задачу управления API. Хорошая иллюстрация -- `Kong`: это `Nginx` (reverse proxy), на который навесили плагины аутентификации, rate limiting и аналитики, и так он стал полноценным API Gateway. То есть один продукт может играть обе роли в зависимости от конфигурации.

---

## Q4. Какие преимущества и недостатки у паттерна API Gateway?

Суть компромисса: Gateway убирает сложность из клиентов и сервисов, но сам становится критически важным звеном, через которое идёт весь трафик.

**Плюсы:**

- **Упрощение клиентского кода** -- один endpoint вместо десятков адресов.
- **Инкапсуляция** -- внутреннюю структуру можно менять, не затрагивая клиентов.
- **Централизация cross-cutting concerns** -- безопасность и логирование пишутся один раз (DRY), а не дублируются по сервисам.
- **Адаптация API под клиента** -- можно отдавать разным клиентам разные представления (паттерн BFF).
- **Протокольная изоляция** -- наружу REST, внутри gRPC/AMQP.

**Минусы:**

- **Единая точка отказа (SPOF)** -- если упадёт Gateway, недоступной станет вся система, поэтому он обязан быть высокодоступным.
- **Дополнительная задержка** -- каждый запрос проходит лишний сетевой hop.
- **Сложность эксплуатации** -- ещё один компонент, который надо деплоить, мониторить и масштабировать.
- **Риск «толстого» Gateway** -- соблазн положить в него бизнес-логику (anti-паттерн, см. Q27).
- **Bottleneck** -- весь трафик идёт через одну точку, и она легко становится узким местом.

**Как смягчить минусы.** Большинство рисков снимается тремя правилами: Gateway должен быть **stateless** (состояние -- во внешнем Redis), **горизонтально масштабируемым** (несколько инстансов за балансировщиком против SPOF) и содержать **только инфраструктурную логику** (против «толстого» Gateway).

---

## Q5. Что такое паттерн BFF (Backend for Frontend)?

**BFF (Backend for Frontend)** -- вариация паттерна `API Gateway`, при которой вместо одного общего Gateway создаётся **отдельный Gateway под каждый тип клиента**, заточенный под его потребности. Web-приложение, мобильный клиент и IoT-устройство получают свои персональные backend'ы вместо «универсального» API, который пытается угодить всем сразу.

Схема: у каждого типа клиента -- свой BFF, и каждый BFF обращается к нужному ему набору сервисов.

- `Web App` → `BFF for Web` → `User Service`, `Product Service`
- `Mobile App` → `BFF for Mobile` → `User Service`, `Notification Service`
- `IoT Device` → `BFF for IoT` → `Telemetry Service`

**Зачем разделять Gateway по клиентам:**

- **Разные форматы данных** -- мобильному клиенту с медленной сетью нужны компактные ответы, веб-клиенту -- полные; общий API заставлял бы мобильник тащить лишнее.
- **Разные паттерны агрегации** -- одна страница мобильного приложения может собирать данные сразу из 5 сервисов, и эта склейка специфична именно для неё.
- **Разные требования к безопасности** -- `OAuth 2.0` для web, `API key` для IoT.
- **Независимые релизные циклы** -- команда мобильной разработки владеет своим BFF и катит его в своём темпе, не согласовывая изменения с web-командой.

**Компромисс:** BFF убирает «универсальный» API ценой дублирования -- появляется несколько Gateway, у каждого свой код и эксплуатация. Оправдан, когда требования клиентов реально расходятся; для пары похожих клиентов хватит одного Gateway.

**Подробнее** о реализации BFF с `Spring Cloud Gateway` и `OAuth 2.0` -- в [вопросах по Spring Cloud](../frameworks/spring/spring-cloud-interview.md).

---

## Q6. (!) Что такое Spring Cloud Gateway и на чём он основан?

**Spring Cloud Gateway** -- реактивный API Gateway из экосистемы `Spring Cloud`, построенный на `Spring WebFlux` и `Project Reactor`. Ключевой момент: он работает на `Netty`, а не на `Tomcat`, поэтому обработка неблокирующая. Один поток не «висит» в ожидании ответа от backend, а обслуживает множество соединений -- это критично для Gateway, который по природе своей в основном ждёт ответы downstream-сервисов (I/O-bound нагрузка).

Внутренний путь запроса через `Spring Cloud Gateway` (на `Netty + WebFlux`):

- `Client` → `Spring Cloud Gateway` (`Netty + WebFlux`)
- → `Handler Mapping` → `Web Handler` → `Pre-Filters`
- → `Proxied Service` (целевой сервис)
- → `Post-Filters` → обратно к `Client`

**Ключевые характеристики:**

- **Реактивный стек** -- `WebFlux` + `Reactor Netty`, неблокирующий I/O
- **Интеграция с Spring Cloud** -- `Eureka`, `Consul`, `Resilience4j`, `Spring Security`
- **Декларативная и программная конфигурация** -- YAML или Java DSL
- **Расширяемость** -- кастомные фильтры и предикаты
- **Поддержка WebSocket** -- проксирование WebSocket соединений

**Частая ошибка на собеседовании и в коде:** `Spring Cloud Gateway` несовместим с `Spring MVC` (Servlet-стек). Gateway живёт на реактивном WebFlux-стеке, а `spring-boot-starter-web` тянет Servlet-контейнер -- два стека в одном приложении конфликтуют, и Gateway не стартует. Поэтому в зависимостях должен быть только реактивный стартер:

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

Это три кирпичика, из которых складывается вся конфигурация `Spring Cloud Gateway`. Их связь проще всего описать так: **Route** говорит «куда», **Predicate** -- «при каких условиях», **Filter** -- «что по дороге сделать».

**Route** (маршрут) -- основная единица конфигурации; описывает, куда направить запрос. Состоит из:
- `id` -- уникальный идентификатор маршрута;
- `uri` -- целевой адрес backend-сервиса;
- `predicates` -- условия, по которым выбирается этот маршрут;
- `filters` -- модификации запроса и ответа.

**Predicate** (предикат) -- условие, которому должен удовлетворять входящий запрос, чтобы сработал маршрут (по пути, методу, заголовку и т.д.). Технически это `java.util.function.Predicate<ServerWebExchange>`: либо подходит, либо нет. Запрос «примеряется» к маршрутам, и берётся первый подходящий.

**Filter** (фильтр) -- компонент, который что-то делает с запросом или ответом: pre-filter правит запрос до отправки в backend, post-filter -- ответ перед возвратом клиенту. Именно фильтры выполняют всю «работу» Gateway: аутентификацию, добавление заголовков, rate limiting, трансформацию.

Как связаны Route, Predicate и Filter на пути запроса:

- `HTTP Request` → проверка предиката (`Predicate Match?`):
  - если **Да** → `Pre-Filter 1` → ... → `Pre-Filter N` → `Backend Service`;
  - если **Нет** → переход к следующему маршруту (`Next Route`).
- После ответа backend: `Post-Filter N` → ... → `Post-Filter 1` → `HTTP Response` клиенту.

---

## Q8. Как настроить маршрутизацию через YAML и Java DSL?

Маршруты задаются двумя способами: декларативно в YAML или программно через Java DSL. Логика одна и та же -- набор `Route` с предикатами и фильтрами; различается только форма записи.

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

**Что выбрать.** YAML удобен для простых статичных маршрутов: его правят DevOps, он легко выносится в Config Server и меняется без пересборки. Java DSL берут, когда в маршрутизации появляются условия, динамические значения, циклы или ссылки на бины кастомных фильтров -- то, что в YAML выразить неудобно или невозможно.

---

## Q9. Какие встроенные предикаты (Route Predicates) существуют?

Предикаты определяют, при каких условиях запрос попадёт на маршрут. `Spring Cloud Gateway` поставляет готовый набор `RoutePredicateFactory` -- их хватает для большинства задач, кастомный предикат нужен редко:

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

Несколько предикатов в одном маршруте комбинируются по логическому **И** -- запрос обязан удовлетворять всем сразу:

```yaml
predicates:
  - Path=/api/v2/**
  - Method=GET
  - Header=Accept, application/json
  # Запрос должен удовлетворять ВСЕМ условиям
```

---

## Q10. (!) Какие типы фильтров существуют в Spring Cloud Gateway?

Фильтры классифицируют по двум независимым осям: **по области применения** (один маршрут или все) и **по фазе выполнения** (до или после backend). Один и тот же фильтр одновременно характеризуется обеими осями.

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

Кастомный `GatewayFilter` для конкретного маршрута создаётся как наследник `AbstractGatewayFilterFactory`. Класс-фабрика принимает типобезопасный объект `Config` (его поля задаются в YAML через `args`) и возвращает сам фильтр из метода `apply`. Пример ниже замеряет время обработки запроса и логирует медленные:

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

**Как тут устроены фазы.** Один фильтр совмещает pre- и post-логику благодаря реактивной природе: код **до** `chain.filter(exchange)` выполняется в pre-фазе (фиксируем `startTime`), а код в `then(...)` -- в post-фазе, когда ответ от backend уже получен (считаем `duration`). Имя фабрики `RequestTimingGatewayFilterFactory` даёт короткое имя фильтра в YAML -- `RequestTiming` (суффикс `GatewayFilterFactory` отбрасывается).

---

## Q12. Что такое GlobalFilter и чем он отличается от GatewayFilter?

**`GlobalFilter`** применяется ко **всем** маршрутам автоматически, как только бин зарегистрирован, -- его не надо прописывать в каждом маршруте. Этим он и отличается от `GatewayFilter`, который привязан к конкретному маршруту. Поэтому GlobalFilter -- естественное место для сквозных задач: логирования, аутентификации, трейсинга, которые нужны на каждом запросе. Пример ниже навешивает на любой запрос сквозной `X-Request-Id`:

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

Все фильтры -- и глобальные, и привязанные к маршруту -- собираются в единую цепочку и сортируются по значению `order` (интерфейс `Ordered`). Порядок принципиален: аутентификация должна отработать раньше rate limiting, а логирование ответа -- позже всех. Ключевая особенность -- цепочка проходится **дважды**, «туда и обратно»:

Проход цепочки «туда» (pre-фаза, по возрастанию `order`):

- `Входящий запрос`
- → `Global Pre-Filter` (`order = -2`)
- → `Global Pre-Filter` (`order = -1`)
- → `Route Pre-Filter` (`order = 1`)
- → `Route Pre-Filter` (`order = 2`)
- → `Backend Service`

Проход «обратно» (post-фаза, в обратном порядке -- по убыванию `order`):

- `Backend Service`
- → `Route Post-Filter` (`order = 2`)
- → `Route Post-Filter` (`order = 1`)
- → `Global Post-Filter` (`order = -1`)
- → `Global Post-Filter` (`order = -2`)
- → `Ответ клиенту`

**Правила порядка:**

1. **Pre-фаза** -- фильтры идут в порядке возрастания `order` (от меньшего к большему).
2. **Post-фаза** -- в обратном порядке (от большего к меньшему), как при разворачивании стека.
3. `GlobalFilter` и `GatewayFilter` сортируются **вместе** в одной цепочке -- глобальный фильтр с `order = 5` встанет между двумя маршрутными с `order = 4` и `order = 6`.
4. Без явного `order` фильтры выполняются в порядке объявления.

**Практический вывод:** чем меньше `order`, тем «снаружи» фильтр -- он первым видит запрос и последним трогает ответ. Поэтому аутентификации дают сильно отрицательный `order` (например, `-100`), чтобы она отсекала неавторизованные запросы до всей остальной обработки.

---

## Q14. Какие встроенные фильтры для трансформации запросов и ответов существуют?

Трансформация заголовков и пути запроса покрывается встроенными фильтрами -- кастомный код не нужен. Для тела запроса/ответа есть `ModifyRequestBody`/`ModifyResponseBody`. Ниже -- типовой набор для запроса (правка заголовков, пути, параметров, размера):

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

Для тела ответа удобен `modifyResponseBody` в Java DSL -- например, чтобы обернуть ответ backend в общий envelope-формат, не трогая сам сервис:

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

Главная идея: аутентификацию выгодно вынести в Gateway, чтобы каждый сервис не валидировал токен заново. Gateway проверяет токен один раз на границе системы, а внутрь сервисам отдаёт уже доверенную информацию о пользователе. Есть два способа это сделать.

**Подход 1: Gateway как точка аутентификации** (рекомендуемый)

Gateway сам проверяет токен и прокидывает извлечённые данные пользователя downstream-сервисам через заголовки. Сервисы внутри периметра доверяют этим заголовкам и больше не разбирают JWT -- логика валидации живёт в одном месте:

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

Вместо ручного фильтра можно отдать всю работу `Spring Security`: настроить Gateway как OAuth 2.0 Resource Server, и он сам провалидирует JWT, проверит подпись по ключам из `issuer-uri` и сопоставит роли с правилами доступа. Меньше своего кода, но и меньше контроля над тем, что прокидывается downstream.

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

**Что выбрать.** Подход 2 на Spring Security -- стандарт для production: проверка подписи, ротация ключей и сопоставление ролей работают «из коробки». Подход 1 берут, когда нужен полный контроль над тем, что именно прокидывается downstream, или когда токен нестандартный. Обратите внимание: `csrf().disable()` здесь уместен, потому что Gateway -- это stateless API без cookie-сессий, для которого CSRF неактуален.

---

## Q16. Как настроить CORS в Spring Cloud Gateway?

CORS (Cross-Origin Resource Sharing) логично настраивать именно на Gateway: он -- единая точка входа для браузерных запросов, и правила кросс-доменного доступа достаточно описать один раз здесь, а не в каждом сервисе. Браузер сначала шлёт preflight-запрос `OPTIONS`, и Gateway отвечает на него заголовками `Access-Control-Allow-*`.

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

**Частый баг -- дублирование заголовков.** Если CORS настроен и на Gateway, и на backend-сервисе, в ответе окажется два значения `Access-Control-Allow-Origin`. Браузер по спецификации воспринимает это как ошибку и блокирует ответ. Решение -- либо настраивать CORS только в одном месте (на Gateway), либо склеивать дубли фильтром `DedupeResponseHeader`:

```yaml
filters:
  - DedupeResponseHeader=Access-Control-Allow-Origin Access-Control-Allow-Credentials, RETAIN_UNIQUE
```

---

## Q17. Как реализовать OAuth 2.0 Token Relay через Gateway?

**Token Relay** -- паттерн, при котором `API Gateway` сам выступает `OAuth 2.0 Client`: проводит пользователя через логин на Authorization Server, получает access token и затем «передаёт» (relay) его downstream-сервисам в заголовке `Authorization`. Ключевая выгода -- браузеру не отдаётся access token: он остаётся в защищённой сессии на Gateway, а наружу клиент работает по обычной cookie-сессии. Это основа BFF-паттерна безопасности для SPA.

Поток Token Relay по шагам. Участники: `User`, `API Gateway` (выступает `OAuth2 Client`), `Auth Server` (`Keycloak`), `Backend Service` (`Resource Server`).

1. `User` → `API Gateway`: `GET /api/resource` (без токена).
2. `API Gateway` → `Auth Server`: redirect на страницу логина (`Redirect to login`).
3. `User` → `Auth Server`: вводит credentials.
4. `Auth Server` → `API Gateway`: возвращает `Authorization Code`.
5. `API Gateway` → `Auth Server`: обмен кода на токен (`Exchange code → Access Token`).
6. `API Gateway` → `Backend Service`: `GET /resource` + `Bearer Token`.
7. `Backend Service` → `API Gateway`: `Response`.
8. `API Gateway` → `User`: `Response`.

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

Вся магия -- в одной строке `TokenRelay=`: этот фильтр достаёт access token из текущей OAuth2-сессии и автоматически подставляет `Authorization: Bearer <token>` в каждый запрос к backend. Самому писать код извлечения и проброса токена не нужно.

---

## Q18. (!) Как реализовать Rate Limiting в API Gateway?

Из коробки есть фильтр `RequestRateLimiter` -- он считает лимиты в `Redis` по алгоритму `Token Bucket`. Почему именно Redis: Gateway масштабируется горизонтально, и счётчики обязаны быть общими для всех инстансов, иначе клиент с лимитом «10 rps» при трёх инстансах получит фактические 30. Внешнее хранилище делает лимит точным независимо от числа инстансов Gateway.

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

**Key Resolver** -- ответ на вопрос «лимит на кого?». Он вычисляет ключ, по которому ведётся счётчик: на пользователя, на IP или на API key. Это важное архитектурное решение: лимит по IP легко обходится сменой адреса и бьёт по пользователям за общим NAT, тогда как лимит по пользователю (из JWT) точнее и привязан к конкретному клиенту.

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

При превышении лимита Gateway возвращает `429 Too Many Requests` и кладёт в ответ заголовки, по которым клиент может понять своё состояние и притормозить:
- `X-RateLimit-Remaining` -- сколько запросов ещё осталось в текущем окне;
- `X-RateLimit-Replenish-Rate` -- скорость пополнения токенов;
- `X-RateLimit-Burst-Capacity` -- максимальный допустимый всплеск.

---

## Q19. (!) Как интегрировать Circuit Breaker с API Gateway?

Circuit Breaker на Gateway защищает от каскадных сбоев: если один backend «лёг» и перестал отвечать, Gateway перестаёт его дёргать, отдаёт быстрый fallback и не даёт зависающим запросам исчерпать свой пул соединений. Реализуется через интеграцию с `Resilience4j` -- фильтр `CircuitBreaker`. Часто его ставят в паре с `Retry`: сначала несколько повторов на временную ошибку, и только при устойчивых сбоях открывается «рубильник».

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

**Fallback-контроллер** -- то, что Gateway вернёт клиенту, когда «рубильник» открыт. Параметр `fallbackUri: forward:/fallback/orders` внутренне перенаправляет запрос на этот эндпоинт. Главное -- отдать осмысленный деградированный ответ (заглушку, кэш, понятную ошибку), а не повисший таймаут:

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

Подробнее о паттернах отказоустойчивости -- в [вопросах по Resilience-паттернам](resilience-patterns-interview.md).

---

## Q20. (!) Как API Gateway интегрируется с Service Discovery?

Без Service Discovery адреса backend-сервисов пришлось бы хардкодить, что невозможно в облаке, где инстансы постоянно создаются и умирают с новыми IP. Интеграция строится вокруг префикса `lb://` в `uri`: вместо конкретного хоста Gateway пишет `lb://service-name` и на лету спрашивает реестр (`Eureka`, `Consul`, `Kubernetes`), где сейчас живые инстансы этого сервиса, после чего балансирует запросы между ними.

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

Схема интеграции с Service Discovery и балансировки:

- `API Gateway` по `lb://user-service` обращается к `Load Balancer` (`Spring Cloud LoadBalancer`).
- `Load Balancer` распределяет запросы по `Round Robin` между инстансами: `User Service :8081`, `User Service :8082`, `User Service :8083`.
- `API Gateway` ↔ `Eureka Server`: поиск в реестре (`Registry Lookup`).
- Каждый инстанс (`:8081`, `:8082`, `:8083`) ↔ `Eureka Server`: регистрация (`Registration`).

**Нюанс auto-locator.** При `discovery.locator.enabled=true` Gateway сам генерирует маршрут для каждого сервиса в реестре: `/SERVICE-NAME/**` -> `lb://SERVICE-NAME`. Это удобно на старте и в dev, но в production опасно: наружу автоматически экспонируются **все** сервисы, включая внутренние, которые не должны быть доступны клиентам. Поэтому в проде локатор обычно выключают и объявляют маршруты явно -- так контролируешь, что именно публикуется.

---

## Q21. Как реализовать балансировку нагрузки через Gateway?

Балансировку выполняет `Spring Cloud LoadBalancer` (пришёл на смену устаревшему `Ribbon`). Важно, что это **клиентская** балансировка: список инстансов берётся из Service Discovery, и сам Gateway выбирает, на какой инстанс отправить запрос -- без отдельного балансировщика-посредника. По умолчанию работает Round Robin, но стратегию можно переопределить для конкретного сервиса:

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

Подробнее о стратегиях балансировки -- в [вопросах по балансировке нагрузки](load-balancing-interview.md).

---

## Q22. Поддерживает ли Spring Cloud Gateway WebSocket?

Да, и нативно -- благодаря реактивному стеку на Reactor Netty. Чтобы проксировать WebSocket, в `uri` маршрута указывают схему `ws://` (или `wss://` для TLS) вместо `http://`:

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

**Особенности, о которых спрашивают:**

- Gateway выполняет `HTTP Upgrade` -- обычный HTTP-запрос «повышается» до постоянного WebSocket-соединения.
- Поддерживаются `ws://` и `wss://` (с TLS).
- **Главный нюанс:** pre/post-фильтры срабатывают только на начальном `Upgrade`-запросе, а не на каждом WebSocket-фрейме. Поэтому аутентификацию и rate limiting на отдельные сообщения внутри установленного соединения через обычные фильтры не сделать -- их проверяют один раз при рукопожатии.
- Таймауты для WebSocket настраиваются отдельно от HTTP (долгоживущее соединение не должно рваться по обычному HTTP-таймауту):

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

Преимущество версионирования на Gateway в том, что разные версии API можно направлять на **физически разные сервисы** (`v1` и `v2` развёрнуты отдельно), а клиент об этом не знает -- он видит единый адрес. Есть три рабочие стратегии.

**1. Через URL-путь** -- самая явная: версия зашита в URL, маршрутизация тривиальна.

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

**2. Через заголовок** -- URL остаётся «чистым», версию выбирает заголовок-предикат (подробнее о плюсах/минусах -- в Q37):

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

**3. Canary-деплой через Weight-предикат** -- не совсем версионирование клиентом, а постепенный перевод трафика: основная часть запросов идёт на стабильную версию, малая доля -- на новую, чтобы проверить её на реальном трафике:

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

Предикат `Weight` распределяет запросы по указанным долям (90/10), позволяя плавно наращивать процент трафика на новую версию и быстро откатиться, если на ней полезли ошибки.

---

## Q24. Что такое API Composition/Aggregation и как реализовать через Gateway?

**API Composition** -- паттерн, при котором на один запрос клиента Gateway сам обращается к нескольким backend-сервисам, собирает их ответы и возвращает единый результат. Зачем: вместо 5 round-trip'ов от мобильного клиента по медленной сети делается 1, а параллельные внутренние вызовы происходят по быстрой сети дата-центра. Клиент получает готовую «склейку» для своего экрана.

Схема агрегации одного запроса `GET /api/dashboard`:

- `Client` → `API Gateway`: `GET /api/dashboard`.
- `API Gateway` параллельно (`parallel`) обращается к трём сервисам: `User Service`, `Order Service`, `Recommendation Service`.
- Ответы назад в `API Gateway`: от `User Service` -- `user data`, от `Order Service` -- `recent orders`, от `Recommendation Service` -- `recommendations`.
- `API Gateway` → `Client`: объединённый ответ (`aggregated response`).

**Реализация через WebClient в кастомном фильтре.** Ключевой приём -- `Mono.zip`: оба вызова стартуют параллельно, и Gateway ждёт оба сразу, а не последовательно. Так общая задержка равна максимуму из двух запросов, а не их сумме:

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

**Подводный камень.** Агрегация раздувает Gateway: в нём появляется знание о том, какие данные нужны конкретному экрану, -- по сути, бизнес-логика, которой здесь не место (см. anti-паттерн «толстый Gateway», Q27). Поэтому простую склейку держат в Gateway, а сложную -- выносят в отдельный BFF-сервис или решают через [CQRS](cqrs-event-sourcing-interview.md) с заранее посчитанными представлениями, где готовый ответ читается одним запросом.

---

## Q25. (!) Сравните Spring Cloud Gateway, Kong и Nginx как API Gateway

Если в двух словах: выбор определяется стеком команды и требованиями к производительности. `Spring Cloud Gateway` -- для Java-команд на Spring, `Kong` -- universal-решение с плагинами для polyglot-окружения, `Nginx` -- максимальная скорость на уровне инфраструктуры.

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

Gateway -- первая точка контакта со всем трафиком, поэтому его наблюдаемость особенно важна: проблема здесь видна раньше, чем где-либо ещё. Стандартный набор -- метрики (Micrometer + Prometheus), распределённая трассировка и централизованные access-логи.

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

Здесь Gateway играет ключевую роль в трассировке: как точка входа он **порождает** `traceId` (или принимает его от клиента) и прокидывает `traceId`/`spanId` дальше по заголовкам. Благодаря этому один запрос можно проследить сквозь все сервисы и увидеть, на каком звене он тормозит. Семплинг (`probability`) держат на 100% в dev и снижают до 10-20% в prod, чтобы не раздувать объём трейсов.

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

Большинство anti-паттернов сводятся к одному из двух нарушений: в Gateway тянут лишнюю ответственность (логику, состояние, агрегацию) либо забывают, что он -- критическая точка, и не страхуют его.

**1. «Толстый» Gateway (God Gateway)**

Самый частый. В Gateway переезжает бизнес-логика, он начинает «знать» предметную область и превращается в монолит-узкое-горло. Правило: в Gateway -- **только инфраструктура** (routing, auth, rate limiting), бизнес-правила -- в сервисах.

**2. Единая точка отказа без резервирования**

Gateway пропускает весь трафик, поэтому без нескольких инстансов за балансировщиком и health checks он становится SPOF -- падает он, падает вся система. Лечится горизонтальным масштабированием:

Топология горизонтального масштабирования:

- `Load Balancer` распределяет трафик на несколько инстансов: `Gateway Instance 1`, `Gateway Instance 2`, `Gateway Instance 3`.
- Каждый инстанс Gateway → `Backend Services`.

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

**Request/Response трансформация** -- правка входящих запросов (заголовки, путь, параметры) и исходящих ответов (фильтрация полей, смена формата) прямо на Gateway, **не трогая backend**. Это позволяет адаптировать внутренний API под внешний контракт, скрыть служебные детали и не плодить изменения в самих сервисах. Заголовки и путь покрываются встроенными фильтрами, тело -- кастомным кодом.

**Spring Cloud Gateway -- встроенные фильтры трансформации:**

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

**Кастомный фильтр для трансформации тела запроса.** Тело -- особый случай: в реактивном стеке это поток `DataBuffer`, который читается один раз, поэтому после изменения тело нужно «закэшировать» (`CachedBodyOutputMessage`) и подменить запрос декоратором. Ниже -- добавление поля `tenantId` из JWT в JSON-тело:

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

**Трансформация ответа (скрытие внутренних полей).** Симметричная задача -- вычистить из ответа служебные поля, которые клиенту видеть не нужно. Делается через `ServerHttpResponseDecorator`: перехватываем поток тела ответа в `writeWith`, читаем, санируем и отдаём клиенту уже очищенный JSON:

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

**Kong** -- высокопроизводительный API Gateway на базе `Nginx` + `OpenResty`. Главное отличие от Spring Cloud Gateway: вся функциональность собирается из **плагинов**, которые декларативно навешиваются на сервис или маршрут (`jwt`, `rate-limiting`, `http-log` и т.д.), -- свой код почти не нужен. Конфигурация задаётся декларативно (`kong.yml`) или динамически через Admin API. Это делает Kong удобным для polyglot-архитектур, где backend написаны на разных языках.

**Декларативная конфигурация (kong.yml / deck).** Один сервис, на него навешаны три плагина -- JWT-аутентификация, rate limiting и логирование:

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
- **Kong** -- нужна высокая производительность, готовые плагины «из коробки» и polyglot-окружение, где Gateway не должен зависеть от языка backend.
- **Spring Cloud Gateway** -- команда Java-ориентирована и нужна тесная интеграция со Spring Cloud (Eureka, Config Server, Resilience4j); Gateway деплоится как обычное Spring Boot-приложение без отдельной БД.

## Q30. (!) Как организовать observability (метрики, трейсинг, логи) для API Gateway?

Observability для API Gateway критична потому, что он -- единая точка входа: любая проблема здесь бьёт по всему трафику, и именно здесь её видно первым. Observability стоит на трёх столпах -- **метрики** (что происходит в агрегате), **трейсинг** (путь конкретного запроса) и **логи** (детали отдельного события); по-настоящему полезны они вместе.

**Три столпа observability:**

От `API Gateway` идут три столпа observability:

- **Метрики** -- `Prometheus + Grafana`.
- **Трейсинг** -- `OpenTelemetry + Jaeger`.
- **Логи** -- `ELK / Loki`.

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

**SLO для API Gateway (рекомендации).** Важно мерить именно **оверхед Gateway** -- сколько он добавляет поверх backend, -- а не общую latency, иначе медленный сервис «спрячет» проблемы самого Gateway:
- **Availability**: 99.9% (< 8.7 ч. даунтайма/год).
- **Latency p50**: < 10 мс оверхеда Gateway.
- **Latency p99**: < 50 мс оверхеда Gateway.
- **Error rate**: < 0.1% 5xx-ошибок, вызванных именно Gateway, а не backend.

---

## Q31. (!) Алгоритмы Rate Limiting: Token Bucket, Leaky Bucket, Fixed Window, Sliding Window

Это классический вопрос на алгоритмы. Четыре подхода различаются по трём осям: разрешают ли всплеск (burst), насколько точны и сколько памяти едят. Главный водораздел -- как они ведут себя на границе временного окна: наивный Fixed Window пропускает двойной всплеск, остальные с этим борются по-разному.

### Token Bucket

Самый популярный (его и использует Spring Cloud Gateway). У клиента есть «ведро» на N токенов; каждый запрос забирает токен, токены пополняются с фиксированной скоростью. Пустое ведро -- запрос отклоняется. Накопленные токены и дают возможность всплеска: после паузы клиент может разом потратить полное ведро.

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

**Главный минус -- «граничный всплеск»:** 100 запросов в 00:59 и ещё 100 в 01:00 -- это 200 запросов за две секунды, хотя формально лимит «100/мин» не нарушен ни в одном окне. Именно эту дыру закрывают sliding-алгоритмы.  
**Плюсы:** проще некуда, O(1) памяти (один счётчик + TTL).  
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

Короткий ответ: это **не конкуренты, а дополняющие инструменты для разных видов трафика**. API Gateway управляет входом снаружи (клиент → кластер), Service Mesh -- общением сервисов между собой внутри кластера. Их часто путают, потому что оба занимаются маршрутизацией, retry и rate limiting, но делают это на разных границах.

### API Gateway

Работает на **North-South** трафике (клиент → кластер). Основная задача -- управление внешним входом в систему.

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

gRPC работает поверх HTTP/2 с двунаправленным стримингом и HTTP-trailers, но браузер до этого «не дотягивается»: `fetch` API не даёт контроля над HTTP/2-фреймами и не читает trailers. Поэтому появился **gRPC-Web** -- облегчённая обёртка поверх HTTP/1.1 или HTTP/2, понятная браузеру. Роль Gateway здесь -- транскодинг: принять gRPC-Web от браузера и превратить его в обычный gRPC для backend.

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

WebSocket -- это **постоянное** соединение (поднятое через HTTP Upgrade): после рукопожатия клиент и сервер обмениваются сообщениями по одному и тому же каналу, не переустанавливая его. Отсюда конфликт с горизонтальным масштабированием: обычный HTTP stateless и любой запрос идёт на любой инстанс, а WebSocket «прибит» к конкретному инстансу на всё время жизни соединения.

### Sticky Sessions

Раз соединение живёт на конкретном инстансе Gateway, который держит его состояние, балансировщик обязан направлять все пакеты этого клиента на тот же инстанс. Это и есть sticky sessions (липкие сессии).

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

В serverless главное отличие в том, что Gateway -- управляемый сервис без своих инстансов: AWS сам масштабирует его и берёт плату за запросы. Это снимает эксплуатацию, но накладывает жёсткие рамки (timeout, cold start), которые нужно знать.

### AWS API Gateway -- варианты

**REST API** -- полнофункциональный Gateway с моделями данных, валидацией и кастомными авторизаторами (Lambda Authorizer). Самый гибкий, но дороже.

**HTTP API** -- легковесный и дешёвый (~на 70% дешевле REST API), с нативной JWT-авторизацией, но с минимумом возможностей трансформации. Выбор по умолчанию, если не нужны фишки REST API.

**WebSocket API** -- управляет WebSocket-соединениями, храня их connection ID в DynamoDB (см. проблему состояния из Q34).

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

**Тайм-аут:** AWS API Gateway имеет жёсткий timeout 29 секунд на интеграцию — Lambda не может выполняться дольше для синхронных запросов.

**Rate Limiting:** встроен на уровне Usage Plans — `throttlingBurstLimit` (burst) + `throttlingRateLimit` (steady-state rps).

**Стоимость:** оплата за вызов (~$1.0 за млн запросов для HTTP API, ~$3.5 для REST API). При высоком трафике сравнивать с Fargate/EKS.

### Когда AWS API Gateway уместен

- Событийная архитектура с Lambda
- Прототипирование и MVP
- Нерегулярный трафик (serverless экономит при idle)
- Нужен быстрый старт без операционного overhead

---

## Q36. GraphQL через API Gateway: federation и schema stitching

### Зачем GraphQL через Gateway

GraphQL-endpoint всегда один (обычно `POST /graphql`), но за ним данные могут жить в разных сервисах. Возникает вопрос: как из одного запроса собрать ответ, части которого владеют разные команды? Это и решает Gateway -- объединяет схемы, маршрутизирует подзапросы к нужным сервисам и добавляет аутентификацию и rate limiting (который для GraphQL нетривиален -- см. ниже). Есть два подхода к объединению схем.

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

Здесь привычный лимит «N запросов в секунду» не работает: в GraphQL один-единственный запрос может быть сколь угодно тяжёлым -- глубоко вложенным и тянущим тысячи объектов. Считать надо не количество запросов, а их «вес»:

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

Есть четыре способа сообщить серверу нужную версию API; различаются они тем, **где** прячется номер версии -- в пути, заголовке, Accept-типе или query-параметре. Gateway на основе этого выбирает маршрут. Дальше -- по нарастанию «чистоты» и убыванию удобства.

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

Retry и Circuit Breaker решают разные задачи и работают вместе: **Retry** борется с **кратковременными** сбоями (мигнула сеть, один инстанс перегружен), повторяя запрос, а **Circuit Breaker** -- с **устойчивыми** (сервис лёг надолго), быстро отказывая, чтобы не копить зависшие запросы. Грубо: сначала несколько повторов, и если всё равно не вышло -- размыкаем цепь.

### Retry на уровне Gateway

Retry помогает при временных сбоях (сетевые ошибки, 503 от перегруженного сервиса). Ключевые параметры: число попыток, условия повтора (статусы и методы) и стратегия backoff.

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

- [Микросервисы](microservices-interview.md) — паттерн API Gateway как точка входа в микросервисную систему
- [Spring Cloud](../frameworks/spring/spring-cloud-interview.md) — экосистема Spring Cloud: Service Discovery, Config Server, Circuit Breaker
- [Балансировка нагрузки](load-balancing-interview.md) — интеграция Gateway с Ribbon/Spring Cloud LoadBalancer для распределения трафика
- [Паттерны отказоустойчивости](resilience-patterns-interview.md) — Circuit Breaker, Retry и Bulkhead на уровне Gateway
- [Распределённые системы](distributed-systems-interview.md) — API Gateway как facade для распределённой системы
- [Сетевые протоколы](networking-interview.md) — HTTP/2, TLS termination и WebSocket proxying на уровне Gateway
- [HTTP & REST](../api/http-rest-interview.md) — версионирование API, CORS и трансформация запросов/ответов
- [Стратегии кэширования](caching-strategies-interview.md) — кэширование ответов на уровне Gateway для снижения нагрузки
