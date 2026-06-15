---
title: "Вопросы на собеседовании: Service Discovery"
description: "Service discovery в микросервисах: Consul, Eureka, etcd, ZooKeeper, Kubernetes DNS; client-side vs server-side; CAP trade-offs."
tags:
  - interview
  - architecture
  - service-discovery
type: "interview"
difficulty: "intermediate"
aliases:
  - "Service Discovery interview"
  - "Service Discovery собеседование"
  - "Consul Eureka etcd"
  - "Сервис-дискавери"
updated: "2026-05-21"
---

# Вопросы на собеседовании: `Service Discovery`

`Service Discovery` — механизм, через который микросервисы находят друг друга в сети с **динамическими IP и портами**. В монолите вызов — это локальный method call; в микросервисах за каждым вызовом стоит вопрос: «где сейчас находится `payments-service` и какой из его инстансов жив?». На собеседованиях по архитектуре ожидают понимание двух моделей (client-side / server-side), сравнения registries (`Consul`, `Eureka`, `etcd`, `ZooKeeper`, Kubernetes DNS), CAP-компромиссов и того, как health-checks и cache защищают от каскадных отказов.

## Полезные ссылки

### Официальная документация

- [HashiCorp Consul Documentation](https://developer.hashicorp.com/consul/docs) — Consul agents, gossip, KV.
- [Netflix Eureka Wiki](https://github.com/Netflix/eureka/wiki) — AP-режим, self-preservation.
- [etcd Documentation](https://etcd.io/docs/) — Raft, watch API, leases.
- [Apache ZooKeeper Documentation](https://zookeeper.apache.org/doc/current/) — ZAB, ephemeral znodes.
- [Kubernetes Service](https://kubernetes.io/docs/concepts/services-networking/service/) — Service, Endpoints, EndpointSlices.
- [CoreDNS Documentation](https://coredns.io/manual/toc/) — DNS в Kubernetes.
- [Spring Cloud DiscoveryClient](https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/common-abstractions.html) — абстракция.
- [Istio Service Discovery](https://istio.io/latest/docs/concepts/traffic-management/#service-discovery-and-load-balancing) — Pilot/istiod + xDS.

### Книги и статьи

- Sam Newman, *Building Microservices* (2nd ed.) — глава Service Discovery.
- Chris Richardson, *Microservices Patterns* — шаблоны Client-side / Server-side Discovery.
- [Netflix Tech Blog: Eureka 2.0](https://netflixtechblog.com/netflix-shares-cloud-load-balancing-and-failover-tool-eureka-c10647ef95e5)
- [microservices.io: Service Discovery](https://microservices.io/patterns/service-registry.html)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**

- [Q1. (!) Что такое service discovery и зачем он нужен?](#q1--что-такое-service-discovery-и-зачем-он-нужен)
- [Q2. (!) Service registry: что внутри?](#q2--service-registry-что-внутри)
- [Q3. Кто регистрирует инстанс: self-registration vs third-party registration](#q3-кто-регистрирует-инстанс-self-registration-vs-third-party-registration)
- [Q4. (!) Типы health checks: TTL, HTTP, TCP, gRPC](#q4--типы-health-checks-ttl-http-tcp-grpc)
- [Q5. Heartbeat, TTL и eviction policy](#q5-heartbeat-ttl-и-eviction-policy)

**Client-side vs Server-side**

- [Q6. (!) Client-side discovery — модель и flow](#q6--client-side-discovery--модель-и-flow)
- [Q7. (!) Server-side discovery — модель и flow](#q7--server-side-discovery--модель-и-flow)
- [Q8. (!) Плюсы и минусы: client-side vs server-side](#q8--плюсы-и-минусы-client-side-vs-server-side)
- [Q9. Кеш реестра на клиенте: stale entries и partition](#q9-кеш-реестра-на-клиенте-stale-entries-и-partition)

**Consul**

- [Q10. (!) Архитектура Consul: client / server agents, gossip](#q10--архитектура-consul-client--server-agents-gossip)
- [Q11. Consul: регистрация сервиса и health-check](#q11-consul-регистрация-сервиса-и-health-check)
- [Q12. DNS-интерфейс Consul и HTTP API](#q12-dns-интерфейс-consul-и-http-api)
- [Q13. Дополнительные возможности Consul: KV, multi-datacenter, ACL](#q13-дополнительные-возможности-consul-kv-multi-datacenter-acl)

**Eureka**

- [Q14. (!) Архитектура Eureka и почему она AP](#q14--архитектура-eureka-и-почему-она-ap)
- [Q15. Self-preservation mode — что это и зачем](#q15-self-preservation-mode--что-это-и-зачем)
- [Q16. Eureka client: регистрация и heartbeat](#q16-eureka-client-регистрация-и-heartbeat)

**etcd и ZooKeeper**

- [Q17. (!) Как устроен etcd: Raft, watch API, leases](#q17--как-устроен-etcd-raft-watch-api-leases)
- [Q18. Как устроен ZooKeeper: ZAB, ephemeral znodes, watches](#q18-как-устроен-zookeeper-zab-ephemeral-znodes-watches)
- [Q19. Почему etcd выиграл у ZooKeeper для облачных систем](#q19-почему-etcd-выиграл-у-zookeeper-для-облачных-систем)

**Kubernetes DNS**

- [Q20. (!) Объекты discovery в Kubernetes: Service / Endpoints / EndpointSlices](#q20--объекты-discovery-в-kubernetes-service--endpoints--endpointslices)
- [Q21. (!) CoreDNS — как Service резолвится в IP](#q21--coredns--как-service-резолвится-в-ip)
- [Q22. Headless Service и SRV-records](#q22-headless-service-и-srv-records)
- [Q23. Режимы kube-proxy: iptables / IPVS / eBPF](#q23-режимы-kube-proxy-iptables--ipvs--ebpf)

**Service mesh и облака**

- [Q24. Discovery в service mesh: Istio Pilot / istiod, Envoy xDS](#q24-discovery-в-service-mesh-istio-pilot--istiod-envoy-xds)
- [Q25. Managed-реестры в облаке: AWS Cloud Map, GCP Service Directory](#q25-managed-реестры-в-облаке-aws-cloud-map-gcp-service-directory)
- [Q26. Versioning через метаданные: canary, blue-green, A/B](#q26-versioning-через-метаданные-canary-blue-green-ab)

**CAP, HA и сравнения**

- [Q27. (!) CAP-компромиссы реестров: Eureka (AP) vs Consul / etcd / ZK (CP)](#q27--cap-компромиссы-реестров-eureka-ap-vs-consul--etcd--zk-cp)
- [Q28. Discovery как single point of failure — анти-паттерн и защита](#q28-discovery-как-single-point-of-failure--анти-паттерн-и-защита)
- [Q29. Spring Cloud DiscoveryClient — абстракция](#q29-spring-cloud-discoveryclient--абстракция)
- [Q30. Сравнительная таблица: Consul vs Eureka vs etcd vs ZooKeeper vs K8s DNS](#q30-сравнительная-таблица-consul-vs-eureka-vs-etcd-vs-zookeeper-vs-k8s-dns)

---

## Q1. (!) Что такое service discovery и зачем он нужен?

`Service Discovery` — это механизм, через который **клиент находит сетевой адрес (host:port) живого инстанса сервиса**, когда адреса непостоянны. Коротко: клиент знает логическое имя (`payments-service`), а discovery превращает его в адрес конкретного живого инстанса прямо в момент вызова.

**Почему статический config не работает в микросервисах.** В монолите вызов другого модуля — это локальный method call, адреса нет вообще. В микросервисах за каждым вызовом стоит вопрос «а где сейчас этот сервис и какой из его инстансов жив?», и ответ постоянно меняется:

- **Динамические IP.** Контейнер при рестарте получает новый IP (Kubernetes Pod, ECS task, Docker `--rm`). Hard-coded `payments-host=10.0.0.42` ломается каждый деплой.
- **Auto-scaling.** Сервис растёт с 3 до 30 инстансов и обратно — клиент не должен знать заранее их количество.
- **Failover.** Инстанс умер — discovery должен перестать его отдавать в течение секунд, а не минут.
- **Rolling deploy.** Старые инстансы выводятся, новые поднимаются — между ними секунды overlap-а.

**Что даёт discovery:**

- Логическое имя (`payments-service`) вместо IP — код не привязан к конкретной машине.
- Актуальный список живых инстансов с метаданными (zone, version, tags).
- Интеграцию с health-check — мёртвые инстансы автоматически выбывают из выдачи.
- Свободу выбора стратегии балансировки для клиента или LB (round-robin, locality-aware, weighted).

**Минимальный пример.** Вместо `RestTemplate.getForObject("http://10.0.0.42:8080/...", ...)` пишем `RestTemplate.getForObject("http://payments-service/...", ...)`, а под капотом `@LoadBalanced` / `DiscoveryClient` / DNS преобразует имя в адрес живого инстанса.

---

## Q2. (!) Service registry: что внутри?

`Service Registry` — это база данных живых инстансов: единый источник правды о том, кто сейчас доступен и по какому адресу. Discovery без неё невозможен — именно реестр клиент опрашивает, чтобы найти сервис. Минимальная запись об одном инстансе:

| Поле | Пример | Назначение |
|------|--------|-----------|
| `service_id` | `payments-service` | логическое имя |
| `instance_id` | `payments-7f9c-x8h2k` | уникальность |
| `host` | `10.0.4.17` | сетевой адрес |
| `port` | `8080` | порт |
| `metadata` | `zone=eu-west-1a, version=2.3.1, tags=[canary]` | для routing |
| `status` | `UP / DOWN / STARTING / OUT_OF_SERVICE` | для фильтрации |
| `last_heartbeat` | `2026-05-21T12:34:56Z` | для eviction |
| `health_check_url` | `http://10.0.4.17:8080/actuator/health` | для проверки |

**Дополнительно:**

- **Версия записи** (для оптимистичной блокировки / watch).
- **TTL** (через сколько секунд без heartbeat запись считается мёртвой).
- **Сертификаты / SPIFFE ID** в service mesh.

**Где физически живёт реестр** (определяет его CAP-поведение):

- Eureka — in-memory в server-инстансах с peer-to-peer репликацией (отсюда AP).
- Consul — Raft-лог среди server-агентов.
- etcd / ZK — Raft / ZAB-лог соответственно.
- Kubernetes — объекты `Endpoints` / `EndpointSlices`, лежащие в etcd (control plane).

---

## Q3. Кто регистрирует инстанс: self-registration vs third-party registration

Два подхода различаются тем, **кто кладёт запись в реестр** — сам сервис или внешний наблюдатель.

**Self-registration** — инстанс сам регистрирует себя при старте и шлёт heartbeat-ы.

```yaml
# Eureka client (Spring Boot)
spring:
  application:
    name: payments-service
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka:8761/eureka/
  instance:
    lease-renewal-interval-in-seconds: 30   # heartbeat
    lease-expiration-duration-in-seconds: 90 # TTL
```

- **Плюсы:** просто; инстанс сам знает свои метаданные (версия, билд) и кладёт их в реестр.
- **Минусы:** приложение жёстко связано с конкретной registry-библиотекой и знает её адрес — сменить реестр без правки кода нельзя.

**Third-party registration** — внешний компонент (registrator, Kubernetes controller) следит за жизненным циклом инстансов и сам пишет в реестр; приложение про discovery не знает вовсе.

- В Kubernetes `kubelet` сообщает API-серверу о готовности pod-а, а `EndpointController` обновляет `Endpoints`.
- В Consul отдельный Registrator (sidecar-контейнер) подписывается на Docker events и регистрирует контейнеры.

- **Плюсы:** приложение развязано с discovery — реестр можно сменить без редеплоя сервиса.
- **Минусы:** в архитектуре появляется лишний компонент, а метаданные ограничены тем, что видно снаружи (внешний наблюдатель не знает внутренней версии билда).

В Kubernetes-мире победил **third-party**: приложение публикует только `/health`, всё остальное делает платформа.

---

## Q4. (!) Типы health checks: TTL, HTTP, TCP, gRPC

Без health-check реестр быстро наполняется зомби-записями (инстанс упал, но запись осталась), и клиенты бьют в мёртвые адреса. Health-check — это способ реестра убедиться, что инстанс действительно жив. Четыре типа, от самого слабого по смыслу к самому сильному:

**TTL-based (push).** Инстанс сам шлёт heartbeat каждые `N` секунд. Если heartbeat не пришёл за `2N` — запись помечается DOWN. Так работает Eureka.
- **Плюс:** реестру не нужен сетевой доступ до инстанса — инициатива у клиента (удобно за NAT).
- **Минус:** факт «поток шлёт ping» не значит «сервис работает» — `/api/order` может быть сломан, а heartbeat идёт. False-positive «жив».

**HTTP (pull).** Реестр сам периодически зовёт `GET /actuator/health` и ждёт `200`. Так работают Consul и Kubernetes liveness/readiness probe.
- **Плюс:** проверяется **реальная функциональность** — внутри `/health` можно проверить connection к БД и downstream-сервисам.
- **Минус:** нагрузка на сервис от проверок и сложности с доступом через NAT (реестру нужно дотянуться до инстанса).

**TCP.** Реестр открывает соединение на порт и сразу закрывает. Годится, когда сервис не HTTP (например, кастомный бинарный протокол).
- **Минус:** «соединение установилось» ≠ «приложение обрабатывает запросы» — проверка ещё слабее HTTP.

**gRPC health checking protocol** (`grpc.health.v1.Health/Check`) — стандарт, реализован в Consul, Envoy, Kubernetes.

```yaml
# Kubernetes readinessProbe
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  periodSeconds: 10
  failureThreshold: 3
```

**Рекомендация:** разделять две пробы. `/health/liveness` отвечает «процесс жив, рестартить не надо», `/health/readiness` — «готов принимать трафик: БД подключилась, кеш прогрелся». Путать их опасно: если liveness начнёт зависеть от БД, временный сбой БД спровоцирует бессмысленный рестарт пода.

---

## Q5. Heartbeat, TTL и eviction policy

Эти четыре параметра определяют **скорость реакции** реестра на падение инстанса: чем они меньше, тем быстрее мёртвый инстанс исчезает из выдачи — но тем выше риск выкинуть живой инстанс из-за временного сетевого сбоя.

| Параметр | Что | Типичные значения |
|----------|-----|-------------------|
| `heartbeat interval` | как часто инстанс пингует registry | 5-30 сек |
| `TTL / lease` | через сколько без heartbeat — DOWN | 2-3 × heartbeat |
| `eviction interval` | как часто registry удаляет DOWN | 30-60 сек |
| `grace period` | время до полного удаления | 60-300 сек |

**Компромисс:**

- Короткий TTL → быстрая реакция, но больше ложных срабатываний при сетевом сбое (выкинули живой инстанс).
- Длинный TTL → стабильно, но клиенты долго бьют в уже мёртвый инстанс.

**Эмпирическое правило:** TTL должен покрывать один GC-pause плюс короткий сетевой сбой, но не превышать `circuit-breaker timeout` клиента. Логика проста: если circuit breaker отрезает инстанс за 5 сек, а реестр за 90, то реестр здесь уже не помощник — клиент защитит себя сам и быстрее.

Отсюда вывод: **надёжнее всего двойной механизм**. Реестр убирает мёртвых медленно (это защита от шторма ложных eviction'ов), а клиент через circuit breaker и retry реагирует на сбой конкретного инстанса мгновенно.

---

## Q6. (!) Client-side discovery — модель и flow

**Client-side discovery** — клиент сам опрашивает registry, держит у себя список инстансов и сам выбирает, в какой пойти. Балансировка живёт внутри клиента, между ним и сервером нет посредника.

Участники: `Client (order-service)`, `Service Registry`, три инстанса `payments #1/#2/#3`. Поток по шагам:

1. **Старт клиента — подписка.** `Client` → `Registry`: `GET /services/payments`. `Registry` отвечает списком `[#1@10.0.0.1, #2@10.0.0.2, #3@10.0.0.3]`. Клиент кеширует список и запускает refresh-таймер (30 сек).
2. **Каждый запрос — локальный LB.** Клиент локально выбирает инстанс: `pickInstance()` round-robin → `#2`. Затем `Client` → `payments #2`: `POST /pay`, `#2` отвечает `200 OK`.
3. **`#2` умирает.** Heartbeat от `#2` к `Registry` теряется (`heartbeat lost`); `Registry` выселяет `#2` по истечении TTL (`evict #2 after TTL`).
4. **Клиент обновляет cache.** `Client` → `Registry`: `GET /services/payments` (refresh). `Registry` возвращает уже `[#1, #3]`.

**Примеры:**

- **Netflix Eureka + Ribbon / Spring Cloud LoadBalancer** — классика, client держит in-memory snapshot реестра.
- **Consul + Consul Connect client SDK**.
- **gRPC custom resolver** — клиент держит подключения ко всем инстансам.

**Ключевые свойства:**

- Балансировка — на стороне клиента (round-robin, weighted, locality-aware), вся логика в клиентской библиотеке.
- Между клиентом и сервером **нет лишнего hop-а** — прямой TCP, минимальная latency.
- Клиент обязан **периодически обновлять cache** (типично раз в 30 сек) — иначе будет ходить по устаревшему списку.
- При partition с registry клиент работает по устаревшему cache (fail open) — деградирует мягко, а не падает.

---

## Q7. (!) Server-side discovery — модель и flow

**Server-side discovery** — клиент шлёт запрос на один известный endpoint (LB / proxy), а уже тот опрашивает registry и сам решает, в какой инстанс переслать. Клиент про реестр и инстансы не знает — вся логика discovery вынесена в посредника.

Участники: `Client (order-service)`, `Load Balancer / Proxy`, `Service Registry`, инстансы `payments #1/#2`. Поток по шагам:

1. **`LB` подписан на registry** (long-polling / watch). `LB` → `Registry`: `WATCH /services/payments`; `Registry` отдаёт список `[#1, #2]`.
2. `Client` → `LB`: `POST /payments/pay`.
3. `LB` локально выбирает инстанс: `pickInstance()` → `#1`.
4. `LB` → `payments #1`: `POST /pay`; `#1` отвечает `200 OK`; `LB` → `Client`: `200 OK`.
5. **`#1` умирает.** Heartbeat от `#1` к `Registry` теряется (`heartbeat lost`); `Registry` шлёт `LB` событие WATCH `remove #1`; `LB` обновляет кеш без `#1` (`cache without #1`).

**Примеры:**

- **Kubernetes Service** — `ClusterIP` + `kube-proxy` (iptables / IPVS) → endpoints.
- **AWS ELB / ALB / NLB** — managed LB опрашивает target group.
- **Nginx + nginx-resolver** — DNS-based discovery.
- **Service mesh** — Envoy sidecar опрашивает Pilot / istiod.

**Ключевые свойства:**

- Клиент видит **один endpoint** — ему не нужна библиотека discovery, достаточно обычного HTTP. Отсюда polyglot-friendly.
- Балансировка централизованная — логика в одном месте, меняется без редеплоя клиентов.
- Появляется лишний hop через LB → +latency (обычно 1-3 ms в одном AZ).
- LB сам становится критичной точкой и потенциальным bottleneck → его нужно держать в HA-кластере.

---

## Q8. (!) Плюсы и минусы: client-side vs server-side

Главный водораздел: client-side даёт минимальную latency и гибкую балансировку, но требует библиотеки в каждом языке; server-side прячет всю сложность за прокси ценой лишнего hop-а. Подробно по аспектам:

| Аспект | Client-side | Server-side |
|--------|-------------|-------------|
| **Hops** | 1 hop (client → server) | 2 hops (client → LB → server) |
| **Latency** | минимальная | +1-3 ms (proxy) |
| **Логика балансировки** | в каждом языке/клиенте | в одном месте (LB) |
| **Sticky / locality-aware** | легко (клиент знает свой AZ) | сложно (нужно передавать hint) |
| **Multi-language** | каждый язык нужна библиотека | прозрачно (HTTP — и достаточно) |
| **Деплой балансировки** | rolling клиентов → долго | один деплой LB |
| **Где CB / retry** | в клиенте | можно в LB (Envoy) |
| **Сетевой security** | mTLS между всеми клиентами и серверами | mTLS только до LB |
| **Когда выбирать** | Netflix-style, JVM-моноязык, low-latency | polyglot, Kubernetes, простота клиента |

**Гибрид:** service mesh (Istio + Envoy sidecar) — формально это server-side (через sidecar), но sidecar локален → нет inter-host hop, минимальная latency. Логика балансировки в sidecar, а не в приложении → polyglot-friendly. По сути, **best of both worlds**.

---

## Q9. Кеш реестра на клиенте: stale entries и partition

Клиент почти всегда держит **локальный snapshot** реестра. Иначе каждый запрос превращался бы в round-trip к registry, и под нагрузкой реестр упал бы первым. Цена кеша — записи могут устареть (stale), и это нужно осознанно контролировать.

**Параметры:**

- `refresh interval`: 5-30 сек (push через watch / pull по таймеру).
- `eviction on failure`: количество подряд failed запросов до того, как клиент сам выкинет инстанс из cache.
- `TTL cache`: даже если registry недоступен, использовать cache до N минут.

**Что делать, когда связь с registry потеряна** — два полярных подхода:

1. **Fail-open (stale read)** — продолжать работать со старым cache (так по умолчанию делает Eureka).
   - **Плюс:** сервис не падает каскадно вслед за registry — деградирует мягко.
   - **Минус:** какое-то время можем бить в инстансы, которые на самом деле уже мертвы.
2. **Fail-closed** — если registry недоступна, перестать принимать запросы.
   - Для production почти всегда плохо: отказ реестра кладёт весь сервис. Надёжность правильнее обеспечивать redundancy самого реестра, а не отказом клиента.

На практике для discovery предпочитают **fail-open**: устаревший список вреднее полной остановки сервиса лишь в редких случаях.

**Уроки production:**

- **Кеш на диск.** Eureka сохраняет последний snapshot в файл — после перезапуска клиент работает сразу, не дожидаясь первого refresh.
- **Проблема курицы и яйца.** Registry-сервер сам по себе тоже клиент discovery (для health-check между peer-ами). При холодном старте всего кластера получается замкнутый круг — нужен seed-config с явными адресами peer-ов.

---

## Q10. (!) Архитектура Consul: client / server agents, gossip

`Consul` — distributed service mesh от HashiCorp. Архитектурно это кластер из **agents** двух ролей: server-агенты хранят состояние и обеспечивают consistency, client-агенты стоят рядом с приложениями и проксируют запросы. Связывает их gossip-протокол.

**Server agents (3 или 5 в production):**

- Хранят state в Raft-логе → CP-система.
- Один — leader, остальные — followers.
- Здесь живут registry, KV, ACL.

**Client agents** (по одному на каждой машине / pod):

- Не хранят state, пересылают регистрации сервиса в server-кластер.
- Делают local health-check для регистрированных сервисов.
- DNS / HTTP API доступны локально (`127.0.0.1:8600` / `127.0.0.1:8500`).

**Gossip protocol (Serf, основан на SWIM):**

- Все агенты (client + server) знают про friend-list через gossip.
- При падении узла — gossip быстро (секунды) разносит новость по кластеру.
- LAN gossip (внутри DC) + WAN gossip (между DC).

**Multi-datacenter** — несколько server-кластеров, объединённых через WAN gossip; запросы между DC — через RPC forwarding.

Топология на примере двух датацентров:

- **Datacenter 1:**
  - Три server-агента в полной взаимной связке (Raft-кластер): `Server 1` ↔ `Server 2`, `Server 2` ↔ `Server 3 (leader)`, `Server 1` ↔ `Server 3`.
  - `Client agent host A` связан gossip-ом с `Server 1`; `Client agent host B` — gossip-ом с `Server 2`.
  - Приложения подключаются к локальным client-агентам: `App` → `Client agent host A`, `App` → `Client agent host B`.
- **Datacenter 2:**
  - Два server-агента: `Server 4 (leader)` ↔ `Server 5`.
- **Между DC:** `Server 3` ↔ `Server 4` через **WAN gossip**.

---

## Q11. Consul: регистрация сервиса и health-check

Сервис регистрируется в **локальном** агенте — либо JSON-файлом в `consul.d`, либо HTTP-PUT в его API. Дальше агент сам реплицирует запись в server-кластер; приложению не нужно знать адреса серверов.

```json
// /etc/consul.d/payments.json
{
  "service": {
    "name": "payments-service",
    "id": "payments-1",
    "address": "10.0.4.17",
    "port": 8080,
    "tags": ["v2.3.1", "canary"],
    "meta": {
      "version": "2.3.1",
      "zone": "eu-west-1a"
    },
    "checks": [
      {
        "name": "HTTP health",
        "http": "http://10.0.4.17:8080/actuator/health",
        "interval": "10s",
        "timeout": "2s",
        "deregister_critical_service_after": "1m"
      }
    ]
  }
}
```

**Health-check выполняет local-агент**, а не центральный реестр. Это важно для масштабирования: проверки распределены по машинам, и нагрузка на server-кластер не растёт с числом инстансов.

`deregister_critical_service_after` — это grace-period: если check висит в состоянии critical дольше указанного времени, инстанс удаляется из реестра автоматически.

---

## Q12. DNS-интерфейс Consul и HTTP API

Consul отдаёт список живых инстансов двумя интерфейсами — выбор зависит от того, насколько «умный» клиент.

**DNS** — для legacy-приложений, которые из коробки умеют только резолвить имена.

```bash
dig @127.0.0.1 -p 8600 payments-service.service.consul
# A 10.0.4.17
# A 10.0.4.18

dig @127.0.0.1 -p 8600 payments-service.service.consul SRV
# 0 1 8080 payments-1.node.dc1.consul
# 0 1 8080 payments-2.node.dc1.consul
```

- **Плюс:** нулевые изменения в коде — приложение просто резолвит имя.
- **Минус:** DNS ничего не знает про метаданные (tags, version), поэтому фильтрация по ним невозможна.

**HTTP API** — для современных клиентов.

```bash
curl 'http://127.0.0.1:8500/v1/health/service/payments-service?passing&tag=v2.3.1'
# JSON со всем — host, port, metadata, check status
```

- **Плюс:** есть блокирующий запрос `?wait=30s&index=X` (long-polling watch) — клиент получает обновления push-ом, без постоянного опроса.
- **Минус:** нужна библиотека-клиент (Spring Cloud Consul, hashicorp/consul/api), просто DNS-резолвером не обойтись.

В Spring Cloud Consul по умолчанию используется именно HTTP API с watch.

---

## Q13. Дополнительные возможности Consul: KV, multi-datacenter, ACL

Помимо discovery, Consul закрывает ещё три задачи — конфигурацию, мультидата-центр и авторизацию.

**KV-store** — встроенная key-value база (Raft-replicated). Применяется как:

- Distributed config (заменяет `application.yml`).
- Distributed locks (`acquire`).
- Leader election (через session + KV).

**Multi-datacenter** — Consul изначально проектировался под несколько DC.

- Каждый DC — независимый Raft-кластер.
- Cross-DC запросы: `payments-service.service.dc2.consul`.
- WAN gossip между DC, RPC forwarding.

**ACL** — token-based authorization. Можно ограничить, кто пишет / читает какие сервисы / KV-пути.

**Consul Connect** (service mesh) — встроенный mTLS + intentions (политики «кто к кому может ходить»), без отдельного service mesh.

---

## Q14. (!) Архитектура Eureka и почему она AP

`Eureka` — registry от Netflix, осознанно выбравший **availability важнее consistency** (AP в терминах CAP). Главная идея: реестр должен отвечать всегда, даже ценой устаревших данных.

**Топология:** несколько Eureka-серверов (типично 3) с **peer-to-peer репликацией** через REST. Никакого Raft или Paxos — именно отказ от кворума и делает систему AP, а не CP.

**Особенности AP-режима:**

- Запись принимается локально и **асинхронно** реплицируется на peers. Если peer недоступен — реплика догонится позже.
- При split-brain каждый peer продолжает обслуживать клиентов своим (возможно устаревшим) snapshot-ом.
- Stale read — допустимо: клиент получит чуть устаревший список, но registry **никогда не отказывает**.

**Почему именно AP** (по Netflix):

> «Лучше клиент пошлёт запрос в чуть устаревший инстанс, чем мы вернём ему ошибку "registry недоступна"».

В Netflix-сценарии (потоковое видео) — недоступность реестра = деградация всего сервиса. Stale registration хуже, чем нет registration вовсе.

```yaml
# eureka-server (Spring Cloud)
eureka:
  client:
    serviceUrl:
      defaultZone: http://peer1:8761/eureka/,http://peer2:8761/eureka/
    register-with-eureka: true   # сервер тоже регистрируется (для peer-discovery)
    fetch-registry: true
  server:
    enable-self-preservation: true
```

---

## Q15. Self-preservation mode — что это и зачем

**Проблема.** Допустим, между Eureka и большинством клиентов случился network partition. Heartbeat-ы перестают доходить, Eureka делает наивный вывод «все инстансы умерли» и начинает массово их evict-ить. Это **усугубляет аварию**: инстансы-то живы, просто сеть просела — а клиенты, которые пережили бы partition по своему stale cache, теперь получают из реестра пустой список.

**Решение — self-preservation mode.** Eureka рассуждает так: если за последние `N` минут пришло **слишком мало heartbeat-ов** (меньше порога ≈ 85% от ожидаемых), то проблема вероятнее не в инстансах, а **в самой Eureka или в сети**. В этом случае она:

- **Прекращает eviction** — лучше оставить stale-записи, чем выкинуть из реестра живые инстансы.
- Пишет в лог warning «SELF-PRESERVATION ACTIVE».
- Ждёт, пока доля heartbeat-ов восстановится.

**Условие срабатывания:** реальное число heartbeat-ов за минуту < `expected × renewalPercentThreshold (0.85)`.

```yaml
eureka:
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
    eviction-interval-timer-in-ms: 60000
```

**Компромисс:** на dev-окружении с 1-2 инстансами механизм часто срабатывает ложно — убил один инстанс, и доля heartbeat-ов резко проседает ниже порога, eviction замирает, мёртвый инстанс висит в реестре. Поэтому на dev self-preservation обычно выключают, а в prod оставляют.

---

## Q16. Eureka client: регистрация и heartbeat

Eureka-клиент подключается одним стартером Spring Cloud Netflix и дальше работает автоматически: регистрируется на старте, шлёт heartbeat-ы и тянет snapshot реестра. Конфигурация в `application.yml`:

```yaml
spring:
  application:
    name: payments-service
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8761/eureka/
    registry-fetch-interval-seconds: 30   # как часто клиент тянет snapshot
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30 # heartbeat
    lease-expiration-duration-in-seconds: 90 # TTL (3× heartbeat)
    metadata-map:
      version: 2.3.1
      zone: eu-west-1a
```

**Жизненный цикл:**

1. Startup → `POST /eureka/apps/PAYMENTS-SERVICE` (регистрация со статусом `STARTING`).
2. После `/actuator/health` = UP → `PUT` со статусом `UP`.
3. Каждые 30 сек → `PUT` heartbeat.
4. Shutdown hook → `DELETE` (graceful deregistration).

Первый раз клиент тянет полный snapshot реестра, дальше — только **delta** (что изменилось), это экономит трафик. Snapshot сохраняется на диск, поэтому после рестарта он доступен мгновенно, не дожидаясь первого fetch.

---

## Q17. (!) Как устроен etcd: Raft, watch API, leases

`etcd` — distributed key-value store от CoreOS, на котором держится control plane Kubernetes. В отличие от Eureka, это строго CP-система: лучше отказать в записи, чем разойтись в данных.

**Архитектура:**

- Raft consensus (CP-система): 3 или 5 узлов, запись принимает leader и реплицирует на majority — без кворума запись не подтверждается.
- gRPC API (HTTP-интерфейс из v2 устарел).

**Три механизма, на которых строится discovery:**

**Lease** — это TTL, привязанный к ключу. Инстанс берёт lease на 30 сек, привязывает к ней свой ключ `/services/payments/inst-1 → {host, port}` и каждые 10 сек шлёт `KeepAlive`, продлевая срок. Перестал слать (упал) — lease истекает, и etcd сам удаляет ключ. Так мёртвые инстансы исчезают без отдельного eviction-цикла.

**Watch API** — long-poll за изменениями всего префикса: клиент один раз подписывается и получает поток событий PUT/DELETE.

```bash
# Регистрация инстанса
etcdctl lease grant 30
# → lease 694d7... granted with TTL(30s)
etcdctl put --lease=694d7 /services/payments/inst-1 '{"host":"10.0.4.17","port":8080}'
etcdctl lease keep-alive 694d7 &

# Discovery (watch)
etcdctl watch --prefix /services/payments/
# → PUT /services/payments/inst-1 {...}
# → DELETE /services/payments/inst-1 (lease expired)
```

- **Плюсы:** строгая consistency (linearizable reads), нативный long-running watch, gRPC + protobuf эффективнее, чем JSON over HTTP.
- **Минусы:** Raft требует кворумной записи на каждый PUT — это оверхед, поэтому etcd не годится для high-write workload-ов.

В Kubernetes etcd хранит ВСЕ объекты (Pod, Service, Endpoint, ConfigMap…), и service discovery строится поверх него косвенно: `EndpointController` → `Endpoints` → CoreDNS / kube-proxy. То есть приложения с etcd напрямую не разговаривают.

---

## Q18. Как устроен ZooKeeper: ZAB, ephemeral znodes, watches

`Apache ZooKeeper` — старейший координационный сервис (от Yahoo, 2010), CP-система на собственном consensus-протоколе. Исторически он был стандартом для координации в больших Java-системах: Hadoop, Kafka (до KRaft), HBase, Solr.

**Архитектура:**

- ZAB (ZooKeeper Atomic Broadcast) — близок к Raft, leader + followers.
- 3 / 5 узлов, кворум.
- Иерархическая ФС (`/services/payments/inst-1`) с метаданными в каждом znode.

**Ephemeral znode** — узел, привязанный к сессии клиента. Отвалилась сессия (TCP close или session timeout) — znode удаляется автоматически. Это идеальный примитив для discovery: инстанс создаёт ephemeral-узел `/services/payments/inst-1`, и стоит ему умереть, как узел исчезает сам, без отдельного health-check. Это аналог etcd-lease, только через сессию.

**Watch** — одноразовая подписка на изменения znode (самого узла или его children). Сработала один раз — и всё, watch нужно пересоздавать заново. В этом ключевое отличие от etcd, где watch long-running: на ZooKeeper между триггером и пересозданием есть «слепое окно», в которое можно пропустить событие.

```python
# Псевдо-код
zk.create("/services/payments/inst-1", b'{"host":"10.0.4.17"}', ephemeral=True)
children = zk.get_children("/services/payments/", watch=update_cache)
```

**Где использовался:**

- Kafka brokers и controller (до KRaft в Kafka 3.x).
- HBase master election.
- Hadoop NameNode HA.
- Curator framework — упрощённое API.

---

## Q19. Почему etcd выиграл у ZooKeeper для облачных систем

Оба — CP-системы на consensus-протоколе, решающие одну задачу. Но в облаке и Kubernetes победил etcd, и дело не в алгоритме, а в эксплуатации и экосистеме. По пунктам:

| Аспект | ZooKeeper | etcd |
|--------|-----------|------|
| **Протокол** | custom TCP (Jute) | gRPC + protobuf |
| **API** | Java-centric, getChildren / setData | clean kv API + watch + lease |
| **Watch** | one-shot, нужно пересоздавать | long-running |
| **Установка** | JVM + ensemble config | один Go binary |
| **TLS / mTLS** | сложно настраивается | встроено |
| **Operational story** | heavy для small teams | lightweight |
| **Ecosystem** | Java мир (Kafka, HBase) | Cloud Native (k8s, CoreDNS, …) |
| **JVM-overhead** | + GC pauses | нет JVM |

Показательно, что и Kafka ушла от ZooKeeper к собственному KRaft (Raft внутри Kafka-кластера) — то есть даже в своей родной Java-экосистеме ZK признали legacy и операционной обузой.

**Но ZooKeeper не мёртв.** В уже работающих больших Hadoop / Kafka-инсталляциях он остаётся, а для классических coordination-задач (leader election, distributed lock) связка Curator + ZK — по-прежнему надёжный выбор. Проиграл он именно нишу cloud-native discovery, а не координацию вообще.

---

## Q20. (!) Объекты discovery в Kubernetes: Service / Endpoints / EndpointSlices

В Kubernetes discovery построен поверх трёх объектов: `Service` даёт стабильное имя и IP, `EndpointSlices` хранят реальные адреса живых pod-ов, а контроллеры держат это в синхроне. Pod-ы приходят и уходят, но Service остаётся неизменной точкой входа.

**Service** — стабильный сетевой identity для набора pod-ов. Какие именно pod-ы входят в Service, определяет селектор по label-ам.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: payments-service
spec:
  selector:
    app: payments       # выбирает pod-ы с label app=payments
  ports:
    - port: 80
      targetPort: 8080
  type: ClusterIP       # стабильный IP внутри кластера
```

**Endpoints** (legacy) / **EndpointSlices** (с k8s 1.21+, default) — actual IPs живых pod-ов.

```yaml
# kubectl get endpointslices
apiVersion: discovery.k8s.io/v1
kind: EndpointSlice
metadata:
  name: payments-service-abc12
  labels:
    kubernetes.io/service-name: payments-service
endpoints:
  - addresses: ["10.244.1.5"]
    conditions: { ready: true }
    targetRef: { kind: Pod, name: payments-7f9c-x8h2k }
  - addresses: ["10.244.2.7"]
    conditions: { ready: true }
ports:
  - port: 8080
```

**Кто за что отвечает:**

- **EndpointController / EndpointSliceController** — следит за pod-ами и поддерживает EndpointSlices в актуальном состоянии (добавляет/убирает адреса при смене ready/not-ready).
- **kube-proxy** — на каждом node читает Service + Endpoints и пишет правила iptables / IPVS для DNAT: трафик с ClusterIP перенаправляется на реальный pod IP.
- **CoreDNS** — резолвит имя `payments-service.default.svc.cluster.local` в ClusterIP.

**Зачем EndpointSlices пришли на смену Endpoints.** При 1000 pod-ах за одним Service единственный Endpoints-объект разрастается до огромного размера, и любое изменение (один pod стал ready) заставляет control plane разослать весь объект целиком всем подписчикам watch — дорого. EndpointSlices дробят список на куски по ~100 endpoints, поэтому при изменении пересылается только затронутый кусок, а не всё разом.

---

## Q21. (!) CoreDNS — как Service резолвится в IP

`CoreDNS` — DNS-сервер по умолчанию в Kubernetes (раньше эту роль выполнял `kube-dns`). Именно он превращает имя Service в IP, на который дальше pod шлёт трафик.

**Что резолвится:**

- `payments-service.default.svc.cluster.local` → A `10.96.123.45` (ClusterIP Service-а).
- `payments-service.default.svc.cluster.local` SRV → port + target node.
- `10-244-1-5.default.pod.cluster.local` → pod-IP (если включено).

**search domains** в `/etc/resolv.conf` pod-а:

```
search default.svc.cluster.local svc.cluster.local cluster.local
nameserver 10.96.0.10   # CoreDNS Service IP
options ndots:5
```

Благодаря этим search-доменам можно писать просто `payments-service` (короткое имя): resolver по очереди подставит `payments-service.default.svc.cluster.local`, `payments-service.svc.cluster.local`, … — и на первом же найдёт ответ.

**TTL** — обычно 30 сек (настраивается). Клиент обязан кешировать DNS-ответ: без кеша каждый вызов порождает DNS-запрос, и CoreDNS падает под нагрузкой.

**Внутри CoreDNS** работает `kubernetes`-plugin: он читает Service / Endpoints из API-сервера через watch (а не из etcd напрямую) и держит in-memory маппинг name → IP. Поэтому резолв быстрый и не нагружает etcd.

```
# Corefile
.:53 {
    kubernetes cluster.local in-addr.arpa ip6.arpa {
        pods insecure
    }
    forward . /etc/resolv.conf   # external DNS
    cache 30
}
```

---

## Q22. Headless Service и SRV-records

Разница в одном поле — `clusterIP`, но она меняет всю модель балансировки.

**Обычный Service** даёт **ClusterIP** — виртуальный IP, за которым kube-proxy балансирует на pod-ы. Клиент видит один IP и про отдельные инстансы ничего не знает (это server-side discovery).

**Headless Service** (`clusterIP: None`) — виртуального IP НЕТ. Вместо него DNS возвращает **A-record на каждый pod**, и клиент сам видит весь список адресов (это уже client-side discovery).

```yaml
apiVersion: v1
kind: Service
metadata:
  name: payments-headless
spec:
  clusterIP: None
  selector:
    app: payments
  ports:
    - port: 8080
```

```bash
dig payments-headless.default.svc.cluster.local
# A 10.244.1.5
# A 10.244.2.7
# A 10.244.3.9
```

**Зачем нужен:**

1. **Client-side load balancing** — клиент видит все IP и сам решает (gRPC, Java reactive).
2. **StatefulSet-ы** — каждый pod имеет stable DNS name (`payments-0.payments-headless...`), для master-replica топологий (Kafka, Cassandra, MongoDB).
3. **SRV-records** — резолвят `host:port`, удобно для не-стандартных портов.

```bash
dig SRV _http._tcp.payments-headless.default.svc.cluster.local
# 0 100 8080 payments-0.payments-headless.default.svc.cluster.local
# 0 100 8080 payments-1.payments-headless.default.svc.cluster.local
```

---

## Q23. Режимы kube-proxy: iptables / IPVS / eBPF

`kube-proxy` — компонент на каждом node, который превращает запрос на `ClusterIP` в запрос на конкретный pod-IP. Именно он физически реализует балансировку Service. Три режима отличаются тем, *как* это делается в ядре, и по-разному масштабируются:

**iptables (default):**

- На node добавляются DNAT-правила: «трафик на 10.96.123.45:80 → DNAT на 10.244.1.5:8080 с вероятностью 33%, …».
- Балансировка — статистическая (`statistic mode random`).
- Просто и стабильно, но при тысячах Service-ов iptables-цепочки растут → reload медленный, CPU на packet processing высокий.

**IPVS** (с k8s 1.11+):

- L4 LB в ядре Linux (IP Virtual Server, давний компонент).
- O(1) поиск (hash), а не O(N) как iptables.
- Поддерживает round-robin, least-connection, source-hashing.
- Лучше масштабируется на больших кластерах.

**eBPF / Cilium:**

- Полная замена kube-proxy (Cilium `kubeProxyReplacement: true`).
- DNAT и routing решения принимаются в eBPF-программах, исполняемых в ядре.
- Самый быстрый и более гибкий (L7-политики без sidecar).
- Требует Cilium в роли CNI — это уже выбор сетевого стека целиком, а не только kube-proxy.

С точки зрения кода приложения discovery во всех трёх режимах **выглядит одинаково** — это деталь kernel data plane, и приложение про неё не знает. Но на масштабе (>5k Services) выбор режима сильно влияет на latency и нагрузку на control plane.

---

## Q24. Discovery в service mesh: Istio Pilot / istiod, Envoy xDS

В service mesh discovery полностью «спрятан» за sidecar-прокси (обычно Envoy): приложение шлёт запрос в localhost, а sidecar сам знает, куда переслать. Реестром и балансировкой управляет control plane, приложение про них не знает.

**Архитектура Istio:**

- **istiod** (Pilot + Citadel + Galley в одном) — control plane.
  - Читает Service / Endpoint / EndpointSlice / VirtualService / DestinationRule из k8s.
  - Преобразует в Envoy-конфиг.
- **Envoy sidecar** в каждом pod-е (injection через mutating webhook).
- **xDS API** (gRPC streaming) — istiod пушит конфиг в Envoy: CDS (clusters), EDS (endpoints), LDS (listeners), RDS (routes), SDS (secrets).

Поток конфигурации и трафика:

- `K8s API server` → `istiod`: istiod через watch читает `Service` / `Endpoint`.
- `istiod` → `Envoy sidecar #1` и `istiod` → `Envoy sidecar #2`: пуш конфига через **xDS push**.
- Трафик приложений: `App #1` → `Envoy sidecar #1` → `Envoy sidecar #2` → `App #2` (запрос выходит через локальный sidecar и приходит в sidecar получателя).

**Что меняется по сравнению с «голым» k8s discovery:**

- **Локальная балансировка в sidecar** — нет hop через kube-proxy → меньше latency.
- **L7 routing** — по headers, version, source workload (canary 5% по `x-user-cohort`).
- **mTLS** автоматически (SPIFFE identity).
- **Observability** — все запросы проходят через Envoy → distributed tracing, metrics.

**Цена:** +CPU/memory на каждый pod, сложность config.

---

## Q25. Managed-реестры в облаке: AWS Cloud Map, GCP Service Directory

Это managed-аналоги Consul / Eureka от облачных провайдеров: реестр как сервис, который не нужно эксплуатировать самому.

**AWS Cloud Map:**

- Регистрация инстансов сервиса с метаданными.
- Резолвинг через DNS (Route 53) ИЛИ HTTP API.
- Интеграция с ECS (auto-registration) / EKS / Lambda.
- Health-check через Route 53.
- Multi-region не нативно — нужно объединять через global accelerator / multi-region setup.

**GCP Service Directory:**

- Аналогично — namespace → services → endpoints + metadata.
- Resolve через DNS (Cloud DNS) или REST.
- Интеграция с GKE и Cloud Run.

**Зачем managed:**

- Нет своего operational overhead — HA, backup, patching берёт на себя провайдер.
- Из коробки интеграция с IAM и audit log.
- Минус: платить нужно за каждый query / instance — на больших кластерах может выйти дорого.

**Когда выбирать:** когда команда не хочет сама поднимать и обслуживать Consul / etcd, а экосистема уже cloud-native (ECS / Lambda) — тогда managed-реестр интегрируется почти бесплатно по усилиям.

---

## Q26. Versioning через метаданные: canary, blue-green, A/B

Реестр умеет хранить при каждом инстансе **метаданные** (версия, track, зона) — и это превращает discovery из простого «найди адрес» в основу для управления трафиком. Зная версию инстанса, LB или клиент может направить часть трафика только на нужные инстансы.

**Метаданные:**

```json
{
  "service": "payments-service",
  "metadata": {
    "version": "2.3.1",
    "track": "canary",
    "zone": "eu-west-1a"
  }
}
```

**Canary deployment:**

- 1 инстанс с `track=canary` среди 9 с `track=stable`.
- LB / клиент-side фильтр направляет ~5% трафика на `track=canary` (Istio VirtualService по subsets, Envoy CDS endpoint-level weight).
- Monitoring → если canary ОК → постепенно увеличиваем долю.

**Blue-green:**

- Два набора инстансов: `color=blue` (current production) и `color=green` (new version).
- Все запросы идут в `blue`. На момент переключения — Service selector / route переключается на `green`.

**A/B testing:**

- Routing по header (`x-user-cohort=A` → `version=2`; `B` → `version=1`).
- В Istio:

```yaml
apiVersion: networking.istio.io/v1
kind: VirtualService
spec:
  hosts: [payments-service]
  http:
    - match:
        - headers:
            x-user-cohort: { exact: A }
      route:
        - destination: { host: payments-service, subset: v2 }
    - route:
        - destination: { host: payments-service, subset: v1 }
```

Вывод: discovery плюс метаданные — это **фундамент всего продвинутого traffic management**. Без метаданных у вас есть только плоский round-robin по всем инстансам, а значит ни canary, ни blue-green, ни A/B сделать нельзя — некуда направить «нужный» трафик.

---

## Q27. (!) CAP-компромиссы реестров: Eureka (AP) vs Consul / etcd / ZK (CP)

Service registry — это **распределённое состояние**, а значит на него распространяется CAP-теорема: при network partition нельзя одновременно иметь и consistency, и availability — приходится выбирать. Этот выбор и есть главное архитектурное различие между реестрами.

| Система | Тип | Что делает при partition | Когда подходит |
|---------|-----|--------------------------|---------------|
| **Eureka** | **AP** | продолжает отдавать stale списки; нет master | Netflix-style: больше hurt от unavailability, чем от stale data |
| **Consul** | **CP** | minority-партиция не обслуживает записи; linearizable-reads через leader блокируются | service mesh, secrets, KV-config |
| **etcd** | **CP** | minority partition не пишет; reads опционально linearizable | Kubernetes control plane |
| **ZooKeeper** | **CP** | minority blocked; ephemeral znodes сохраняются на majority side | classic coordination (Kafka, HBase) |
| **Kubernetes (через etcd)** | **CP** | API server не пишет в minority; data plane (kube-proxy cache) продолжает работать со stale endpoint slices | platform — лучше «работаем по старому», чем «не работаем» |

**Что важно понять:**

- **AP** — клиент может получить **устаревший список** (вплоть до мёртвого инстанса), но **никогда не получит ошибку «registry unavailable»**. Лишний неудачный запрос можно пережить ретраем.
- **CP** — клиент может получить ошибку во время partition, но если ответ пришёл, то он **гарантированно актуален**.
- Для discovery как такового почти всегда выгоднее **AP-стиль**: устаревший список — меньшее зло, чем полная недоступность реестра, ведь стучаться по нему нужно постоянно.
- Но как только реестр начинает хранить ещё и **distributed lock / leader election**, нужна **CP**: двух leader-ов одновременно иметь нельзя ни на секунду, тут stale-данные недопустимы.

**Правило:** для чистого service discovery → AP (или CP с агрессивным клиентским кешем). Для координации (lock, leader election) → строго CP.

---

## Q28. Discovery как single point of failure — анти-паттерн и защита

Discovery лежит на критическом пути каждого межсервисного вызова, поэтому соблазнительно сделать его обязательным — и тем самым превратить в single point of failure.

**Анти-паттерн:** «реестр недоступен → весь сервис лёг». Особенно опасен, когда:

- Клиент не кеширует — каждый запрос идёт через registry.
- Registry развёрнут в одном инстансе, без HA.
- Регистрация блокирующая — pod не стартует, пока не зарегистрировался в реестре.
- Heartbeat блокирующий — основной поток приложения завис на отправке heartbeat и не делает полезной работы.

**Защита:**

1. **Cluster registry** (3-5 nodes, не один).
2. **Локальный snapshot на клиенте** + disk persistence (Eureka так делает).
3. **Stale OK** — клиент работает по cache даже если registry down.
4. **Async heartbeat** — отдельный поток / event-loop, не блокирует основной workflow.
5. **Lazy registration** — pod становится ready после первого успешного health-check, не обязательно после registration.
6. **Multi-registry / fallback DNS** — если Eureka недоступна, fallback на DNS A-record.
7. **Circuit breaker на запросах к registry**.

**Реальный кейс (Facebook, 2021).** Ошибочный BGP-rollout убрал NS-серверы Facebook из интернета → DNS перестал резолвиться → отказали и сами сервисы, и внутренние инструменты восстановления, которые ходили в API через тот же DNS. **Урок:** out-of-band management (средства восстановления) не должны зависеть от той же системы discovery/DNS, которой они управляют, — иначе чинить аварию будет нечем.

---

## Q29. Spring Cloud DiscoveryClient — абстракция

Spring Cloud прячет разные реестры за единым интерфейсом `DiscoveryClient` — код приложения один и тот же для Eureka, Consul, ZooKeeper или Kubernetes, меняется только стартер на classpath.

```java
@RestController
public class PaymentsResolver {

    private final DiscoveryClient discoveryClient;

    public PaymentsResolver(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/instances")
    public List<ServiceInstance> instances() {
        return discoveryClient.getInstances("payments-service");
    }
}
```

`ServiceInstance` даёт `getHost()`, `getPort()`, `getMetadata()`, `getUri()`.

**Включение:** аннотация `@EnableDiscoveryClient` (опционально с Spring Boot 3 — auto-config работает по classpath).

**Реализации:**

- `spring-cloud-starter-netflix-eureka-client`
- `spring-cloud-starter-consul-discovery`
- `spring-cloud-starter-kubernetes-client` (читает Endpoints через k8s API)
- `spring-cloud-starter-zookeeper-discovery`
- `spring-cloud-starter-alibaba-nacos-discovery`

**LoadBalancer интеграция:**

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}

// Использование
restTemplate.getForObject("http://payments-service/pay", ...);  // имя, а не IP
```

`@LoadBalanced` подключает интерсептор, который через `DiscoveryClient` резолвит `payments-service` → конкретный URL и применяет балансировку (Spring Cloud LoadBalancer, не Ribbon с 2020).

---

## Q30. Сравнительная таблица: Consul vs Eureka vs etcd vs ZooKeeper vs K8s DNS

Сводная таблица для быстрого выбора. Главные оси сравнения — CAP-поведение (Eureka единственный AP), скорость реакции на падение инстанса и операционная сложность.

| Критерий | **Consul** | **Eureka** | **etcd** | **ZooKeeper** | **K8s DNS (CoreDNS + Service)** |
|----------|-----------|-----------|---------|---------------|--------------------------------|
| **CAP** | CP | AP | CP | CP | CP (etcd под капотом) |
| **Consensus** | Raft | peer-to-peer async | Raft | ZAB | Raft (etcd) |
| **Interface** | HTTP API + DNS | HTTP REST | gRPC | custom TCP (Jute) | DNS + k8s API |
| **Watch** | blocking query (long-poll) | delta + periodic fetch | streaming gRPC | one-shot watch | через k8s API watch |
| **Health checks** | HTTP / TCP / TTL / gRPC / script | TTL (heartbeat) | TTL via lease | ephemeral znode (session) | liveness / readiness probe |
| **Discovery latency после падения** | секунды (gossip + check) | до 90 сек (default lease) | секунды (lease expire) | секунды (session timeout) | 5-30 сек (probe + endpoint update) |
| **Multi-DC** | нативно (WAN gossip) | через federation, сложно | federation (etcd-mirror) | observers / cross-DC ensemble | через cluster federation |
| **KV store** | да | нет | да (primary) | да (через znode data) | через ConfigMap (косвенно) |
| **mTLS / security** | встроено (Consul Connect) | basic auth | TLS / RBAC | SASL / Kerberos | нативно (k8s RBAC + ServiceAccount) |
| **Operational** | средний | низкий | средний | высокий (JVM) | низкий (всё managed platform-ом) |
| **Когда выбирать** | service mesh + secrets + multi-DC | JVM-моноязык, AP важно | k8s, secrets, leader-election | legacy Kafka / HBase | k8s-native стек |
| **Когда НЕ выбирать** | если нужен AP | вне Netflix-style | не для high-write | новые проекты | вне Kubernetes |

**Эмпирическое правило выбора:**

- **В Kubernetes** → Kubernetes Service + CoreDNS + (опционально service mesh для L7).
- **Гибридное окружение / VM + K8s** → Consul.
- **Чистая JVM, моноязык, нужна максимальная availability** → Eureka.
- **Coordination + leader election + config** → etcd / ZooKeeper.
- **Cloud-native managed** → AWS Cloud Map / GCP Service Directory.

---

## See also

- [`microservices-interview.md`](microservices-interview.md) — общие паттерны микросервисов, контекст для discovery.
- [`distributed-systems-interview.md`](distributed-systems-interview.md) — consensus (Raft, Paxos, ZAB), gossip, CAP.
- [`load-balancing-interview.md`](load-balancing-interview.md) — L4 / L7 LB, round-robin, weighted, locality-aware — что выбирает клиент / sidecar.
- [`api-gateway-interview.md`](api-gateway-interview.md) — gateway тоже использует discovery, чтобы знать downstream-инстансы.
- [`cap-theorem-interview.md`](cap-theorem-interview.md) — теорема CAP, объяснение CP vs AP в контексте registry.
- [`resilience-patterns-interview.md`](resilience-patterns-interview.md) — circuit breaker, retry, fallback — как защищаться от stale entries.
- [`dns-interview.md`](dns-interview.md) — DNS как нижний слой discovery, TTL, SRV-records.
- [`../frameworks/spring/spring-cloud-interview.md`](../frameworks/spring/spring-cloud-interview.md) — Spring Cloud DiscoveryClient, LoadBalancer, Eureka / Consul стартеры.
- [`../system-design/system-design-interview.md`](../system-design/system-design-interview.md) — discovery как часть end-to-end архитектуры в system design.
