---
title: "Вопросы на собеседовании: Балансировка нагрузки"
description: "Полное руководство по балансировке нагрузки: L4/L7, алгоритмы, Nginx, HAProxy, Spring Cloud LoadBalancer, Kubernetes Ingress, health checks, DNS-based LB"
tags:
  - interview
  - architecture
  - load-balancing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Балансировка нагрузки"
  - "Load Balancing interview"
  - "Load Balancing собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Балансировка нагрузки`

Балансировка нагрузки — ключевой элемент высоконагруженных систем, обеспечивающий распределение трафика между серверами для повышения доступности, отказоустойчивости и масштабируемости. На собеседованиях по архитектуре и System Design тема возникает практически всегда.

**Балансировка нагрузки** (Load Balancing) — процесс распределения входящих сетевых запросов между несколькими серверами. Покрывает уровни L4/L7, алгоритмы распределения, конкретные инструменты (`Nginx`, `HAProxy`, `Spring Cloud LoadBalancer`), облачные и Kubernetes-решения, а также мониторинг и отказоустойчивость.

## Полезные ссылки

### Официальная документация

- [Nginx — HTTP Load Balancing](https://nginx.org/en/docs/http/load_balancing.html) — базовая конфигурация upstream
- [Nginx — upstream module](https://nginx.org/en/docs/http/ngx_http_upstream_module.html) — полный справочник директив upstream
- [HAProxy Configuration Manual](https://www.haproxy.org/download/2.8/doc/configuration.txt) — конфигурация HAProxy
- [Spring Cloud LoadBalancer](https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/loadbalancer.html) — клиентская балансировка в Spring
- [AWS Elastic Load Balancing](https://docs.aws.amazon.com/elasticloadbalancing/) — документация ALB/NLB/CLB
- [Kubernetes Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/) — маршрутизация в Kubernetes
- [Introduction to Spring Cloud Load Balancer (Baeldung)](https://www.baeldung.com/spring-cloud-load-balancer) — клиентская балансировка в Spring Cloud
- [Service Discovery in Microservices (Baeldung)](https://www.baeldung.com/cs/service-discovery-microservices) — service discovery и балансировка
- [An Example of Load Balancing with Zuul and Eureka (Baeldung)](https://www.baeldung.com/zuul-load-balancing) — балансировка с Zuul и Eureka
- [Spring Cloud Netflix Eureka (Baeldung)](https://www.baeldung.com/spring-cloud-netflix-eureka) — service registry и client-side LB

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы балансировки (L4, L7, TLS, health check)**
- [Q1. (!) Что такое балансировка нагрузки на уровне L4 и L7?](#q1--что-такое-балансировка-нагрузки-на-уровне-l4-и-l7)
- [Q2. Что такое TLS termination и где его делать?](#q2-что-такое-tls-termination-и-где-его-делать)
- [Q3. (!) Что такое health check и зачем он нужен?](#q3--что-такое-health-check-и-зачем-он-нужен)
- [Q4. Что такое sticky session (session affinity) и когда его использовать?](#q4-что-такое-sticky-session-session-affinity-и-когда-его-использовать)
- [Q5. Чем отличается активная балансировка от пассивной?](#q5-чем-отличается-активная-балансировка-от-пассивной)

**Алгоритмы балансировки**
- [Q6. (!) Что такое Round Robin и когда его применять?](#q6--что-такое-round-robin-и-когда-его-применять)
- [Q7. Что такое Weighted Round Robin?](#q7-что-такое-weighted-round-robin)
- [Q8. Что такое Least Connections и когда его применять?](#q8-что-такое-least-connections-и-когда-его-применять)
- [Q9. Что такое Least Response Time?](#q9-что-такое-least-response-time)
- [Q10. Как выбрать алгоритм балансировки для микросервисов?](#q10-как-выбрать-алгоритм-балансировки-для-микросервисов)

**Конфигурация Nginx**
- [Q11. (!) Как настроить upstream балансировку в Nginx?](#q11--как-настроить-upstream-балансировку-в-nginx)
- [Q12. Как настроить health checks и failover в Nginx?](#q12-как-настроить-health-checks-и-failover-в-nginx)
- [Q13. Как настроить rate limiting в Nginx?](#q13-как-настроить-rate-limiting-в-nginx)

**Конфигурация HAProxy**
- [Q14. (!) Как настроить балансировку в HAProxy?](#q14--как-настроить-балансировку-в-haproxy)
- [Q15. Как настроить health checks в HAProxy?](#q15-как-настроить-health-checks-в-haproxy)

**Клиентская балансировка (Spring Cloud LoadBalancer)**
- [Q16. (!) Что такое клиентская и серверная балансировка?](#q16--что-такое-клиентская-и-серверная-балансировка)
- [Q17. Как настроить Spring Cloud LoadBalancer?](#q17-как-настроить-spring-cloud-loadbalancer)
- [Q18. Как реализовать кастомную стратегию балансировки в Spring Cloud?](#q18-как-реализовать-кастомную-стратегию-балансировки-в-spring-cloud)

**Архитектура и стратегии**
- [Q19. Что такое blue-green и canary в контексте балансировки?](#q19-что-такое-blue-green-и-canary-в-контексте-балансировки)
- [Q20. (!) Как обеспечить отказоустойчивость балансировщика?](#q20--как-обеспечить-отказоустойчивость-балансировщика)
- [Q21. Что такое connection draining (deregistration delay)?](#q21-что-такое-connection-draining-deregistration-delay)

**Облачные решения и Kubernetes**
- [Q22. Что такое Layer 4 и Layer 7 load balancer в облаке?](#q22-что-такое-layer-4-и-layer-7-load-balancer-в-облаке)
- [Q23. (!) Как настроить health check в Kubernetes для Pod?](#q23--как-настроить-health-check-в-kubernetes-для-pod)
- [Q24. Что такое Ingress и как он связан с балансировкой?](#q24-что-такое-ingress-и-как-он-связан-с-балансировкой)
- [Q25. Как обеспечить zero-downtime при деплое за балансировщиком?](#q25-как-обеспечить-zero-downtime-при-деплое-за-балансировщиком)

**DNS-based и глобальная балансировка**
- [Q26. (!) Что такое DNS-based load balancing?](#q26--что-такое-dns-based-load-balancing)
- [Q27. Что такое geographic load balancing (global load balancing)?](#q27-что-такое-geographic-load-balancing-global-load-balancing)
- [Q28. Что такое cross-zone load balancing в AWS?](#q28-что-такое-cross-zone-load-balancing-в-aws)

**Продвинутые темы**
- [Q29. Что такое consistent hashing и когда его использовать?](#q29-что-такое-consistent-hashing-и-когда-его-использовать)
- [Q30. Как настроить балансировку для gRPC?](#q30-как-настроить-балансировку-для-grpc)
- [Q31. Как настроить балансировку для WebSocket соединений?](#q31-как-настроить-балансировку-для-websocket-соединений)
- [Q32. Что такое path-based routing в L7 балансировщике?](#q32-что-такое-path-based-routing-в-l7-балансировщике)
- [Q33. Как обеспечить балансировку для stateful приложений?](#q33-как-обеспечить-балансировку-для-stateful-приложений)
- [Q34. (!) Как мониторить и анализировать работу балансировщика?](#q34--как-мониторить-и-анализировать-работу-балансировщика)
- [Q35. Как балансировщик обрабатывает медленные бэкенды?](#q35-как-балансировщик-обрабатывает-медленные-бэкенды)

**Продвинутые алгоритмы и паттерны**
- [Q36. (!) Что такое IP Hash и когда его использовать?](#q36-что-такое-ip-hash-и-когда-его-использовать)
- [Q37. Что такое алгоритм Power of Two Choices?](#q37-что-такое-алгоритм-power-of-two-choices)
- [Q38. (!) Что такое Maglev hashing?](#q38-что-такое-maglev-hashing)
- [Q39. Что такое Anycast балансировка и когда её применять?](#q39-что-такое-anycast-балансировка-и-когда-её-применять)
- [Q40. Как балансировка нагрузки реализована в Service Mesh (Istio/Envoy)?](#q40-как-балансировка-нагрузки-реализована-в-service-mesh-istioenvoy)

---

## Q1. (!) Что такое балансировка нагрузки на уровне L4 и L7?

Балансировка нагрузки работает на разных уровнях модели OSI. Два основных уровня — транспортный (L4) и прикладной (L7).

**L4 (Transport layer)** — балансировка по `IP`-адресу и порту. Балансировщик не анализирует содержимое трафика (`HTTP`, `gRPC`). Работает с `TCP`/`UDP` пакетами напрямую.

**L7 (Application layer)** — балансировка по содержимому запроса: `URL`, заголовки, cookie, тело запроса. Работает с `HTTP`/`HTTPS` и понимает протокол приложения.

```mermaid
graph TB
    Client[Клиент] --> L4{L4 Load Balancer}
    Client --> L7{L7 Load Balancer}

    subgraph "L4 — Transport Layer"
        L4 -->|TCP/UDP по IP:port| S1[Server 1]
        L4 -->|TCP/UDP по IP:port| S2[Server 2]
    end

    subgraph "L7 — Application Layer"
        L7 -->|/api/users| US[User Service]
        L7 -->|/api/orders| OS[Order Service]
        L7 -->|/static/*| CDN[Static CDN]
    end
```

| Критерий | L4 | L7 |
|----------|----|----|
| Анализ содержимого | Нет | Да (`URL`, headers, cookies) |
| Протоколы | Любые (`TCP`/`UDP`) | `HTTP`/`HTTPS` |
| Маршрутизация по пути | Нет | Да |
| `TLS` termination | Нет (pass-through) | Да |
| Латентность | Ниже | Выше (анализ L7) |
| Примеры | `AWS NLB`, `HAProxy` (mode tcp) | `AWS ALB`, `Nginx`, `HAProxy` (mode http) |

**Что хотят услышать на собеседовании:** L4 быстрее, но «слепой» — подходит для `TCP`/`UDP` трафика (БД, `gRPC`). L7 медленнее, но умнее — умеет маршрутизировать по `URL`, заголовкам, делать `TLS` termination. В реальности часто используют оба: L4 для входного трафика + L7 для маршрутизации внутри кластера.


> [!mcq] В чём ключевое отличие L4-балансировщика от L7-балансировщика?
>
> - [x] **A) L4 распределяет трафик по `IP:port` без анализа содержимого, L7 принимает решения по `URL`, заголовкам и cookie**
>     - **Развёрнутое объяснение:** L4 (`AWS NLB`, `HAProxy` mode tcp) работает на транспортном уровне и видит только пятёрку «src/dst IP + порт + протокол». L7 (`Nginx`, `AWS ALB`, `HAProxy` mode http) разбирает прикладной протокол целиком — путь, метод, заголовки, тело — и потому может маршрутизировать `/api/users` на одни поды, а `/static/*` на CDN.
>     - **Пример:** `Nginx` с `location /api/orders { proxy_pass http://orders_pool; }` — это L7-роутинг по `URL`. А `HAProxy` в `mode tcp` для проксирования `PostgreSQL` на `5432` — это L4, ему всё равно, что внутри пакета.
>     - **Когда применять:** L4 — для не-HTTP трафика (`gRPC` без header-routing, `TCP` БД, `UDP` DNS) и максимальной пропускной способности. L7 — когда нужен path-based routing, `TLS` termination, WAF, sticky session по cookie, A/B-тесты по заголовку.
>     - **Подводные камни:** L4 не может делать `TLS` termination без pass-through (сертификаты остаются на бэкендах); L7 добавляет латентность из-за парсинга `HTTP` и требует CPU на TLS. Часто используют двухуровневую схему: L4 на входе для DDoS-устойчивости + L7 внутри для роутинга.
>     - **Связанные вопросы:** [[load-balancing-interview#Q2]] про `TLS` termination, [[load-balancing-interview#Q40]] про L7-балансировку в `Envoy`/Istio.
> - [ ] B) L4 работает только с `HTTP`, а L7 — с `TCP` и `UDP` любыми протоколами
>     - **Что на самом деле:** ровно наоборот. L4 — это `TCP`/`UDP` и любой протокол поверх них (включая `HTTP`, но без его понимания). L7 специализирован именно на `HTTP`/`HTTPS` (и `gRPC`/`WebSocket` как надстройках над `HTTP`).
>     - **Откуда путаница:** многие сталкиваются только с `HTTP` LB (`Nginx`/`ALB`) и считают, что «балансировка = HTTP-роутинг», а L4 ассоциируют с чем-то экзотическим.
>     - **Если бы это было правдой:** не существовало бы `AWS NLB` для `TCP`-БД и `UDP DNS`, и `HAProxy mode tcp` был бы бесполезен — но это базовый сценарий продакшна.
> - [ ] C) L4 и L7 — это просто названия для двух поколений `HAProxy`, разницы в принципе работы нет
>     - **Что на самом деле:** L4/L7 — это уровни модели OSI (transport vs application), а не версии продукта. Один и тот же `HAProxy` умеет работать и как L4 (`mode tcp`), и как L7 (`mode http`) — это режимы, не поколения.
>     - **Откуда путаница:** маркетинговые статьи иногда говорят про «next-gen L7 load balancers», создавая впечатление эволюции.
>     - **Если бы это было правдой:** не было бы смысла в специализированных L4-устройствах вроде `F5 BIG-IP LTM` или `AWS NLB` рядом с L7-`ALB`.
> - [ ] D) L7-балансировщик быстрее L4, потому что использует HTTP-кеширование на уровне ядра
>     - **Что на самом деле:** L4 всегда быстрее L7 — он не парсит `HTTP`, не терминирует `TLS`, обрабатывает пакеты ближе к kernel-space (часто через `XDP`/`DPDK`). L7 вынужденно медленнее из-за разбора прикладного протокола.
>     - **Откуда путаница:** L7 умеет кешировать ответы (`Nginx` `proxy_cache`), и это путают со «скоростью балансировки» — но кеш экономит походы к бэкенду, а не ускоряет сам LB.
>     - **Если бы это было правдой:** `AWS NLB` (L4) не рекламировался бы как «millions of requests per second with ultra-low latency», а `ALB` (L7) — как «feature-rich, but with higher latency».


## Q2. Что такое TLS termination и где его делать?

`TLS termination` — расшифровка `HTTPS`-трафика на балансировщике. К бэкендам трафик идёт уже по `HTTP` (или по mTLS во внутренней сети).

```mermaid
graph LR
    C[Клиент] -->|HTTPS / TLS| LB[Load Balancer]
    LB -->|HTTP| B1[Backend 1]
    LB -->|HTTP| B2[Backend 2]

    style LB fill:#f9f,stroke:#333,stroke-width:2px
```

**Плюсы:**
- Централизованное управление сертификатами (одно место обновления)
- Разгрузка бэкендов от шифрования (CPU-intensive операция)
- Возможность инспекции трафика на L7

**Минусы:**
- Трафик между балансировщиком и бэкендом не зашифрован (допустимо во внутренней сети)
- При необходимости `end-to-end` шифрования используют `TLS` re-encryption или `mTLS`

Пример конфигурации `TLS` termination в `Nginx`:

```nginx
server {
    listen 443 ssl;
    server_name api.example.com;

    ssl_certificate     /etc/nginx/ssl/api.example.com.crt;
    ssl_certificate_key /etc/nginx/ssl/api.example.com.key;
    ssl_protocols       TLSv1.2 TLSv1.3;
    ssl_ciphers         HIGH:!aNULL:!MD5;

    location / {
        proxy_pass http://backend_pool;  # HTTP к бэкендам
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

В `Kubernetes` TLS termination настраивается через `Ingress`:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: api-ingress
spec:
  tls:
    - hosts:
        - api.example.com
      secretName: api-tls-secret
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: api-service
                port:
                  number: 80
```


> [!mcq] Что происходит с `HTTPS`-трафиком при `TLS termination` на балансировщике и где это применяют?
>
> - [ ] A) Балансировщик пересылает зашифрованный пакет дальше, и бэкенд сам расшифровывает его ключом, который синхронизируется с LB
>     - **Что на самом деле:** это описание `TLS pass-through`, а не termination. При termination LB сам расшифровывает трафик и дальше идёт `HTTP` (или новый `TLS` при re-encryption). Никакой «синхронизации ключей» с бэкендом нет — у бэкенда нет приватного ключа сертификата вообще.
>     - **Откуда путаница:** pass-through и termination — два режима работы с `TLS` на LB, и их легко перепутать при первом знакомстве.
>     - **Если бы это было правдой:** терялся бы главный плюс termination — централизованное хранение сертификатов, и L7-роутинг был бы невозможен (LB не видит `HTTP`-заголовков в шифрованном пакете).
> - [x] **B) Балансировщик расшифровывает `HTTPS` и шлёт бэкендам уже `HTTP` (или `mTLS` во внутренней сети); сертификаты централизованы на LB**
>     - **Развёрнутое объяснение:** `TLS termination` означает, что зашифрованное соединение «обрывается» на балансировщике. LB имеет приватный ключ и сертификат, расшифровывает запрос, может прочитать `HTTP`-заголовки/URL для L7-роутинга и WAF, а к бэкендам отправляет уже `HTTP` или поднимает новое `TLS`-соединение (re-encryption / `mTLS`) для зашифрованной внутренней сети.
>     - **Пример:** `Nginx` с `listen 443 ssl; ssl_certificate api.example.com.crt; proxy_pass http://backend_pool;` — классический termination. В `Kubernetes` Ingress с `tls.secretName: api-tls-secret` делает то же самое на уровне ingress-контроллера.
>     - **Когда применять:** когда нужен L7-роутинг, WAF, централизованное обновление сертификатов (Let's Encrypt в одном месте), разгрузка CPU бэкендов от шифрования. Между LB и бэкендами в `VPC`/Kubernetes часто оставляют `HTTP` — внутренняя сеть считается доверенной.
>     - **Подводные камни:** трафик LB→бэкенд незашифрован, что недопустимо при compliance-требованиях (PCI DSS, HIPAA) — тогда нужно re-encryption или `mTLS`. Бэкенд не видит оригинальный `client IP` и `scheme` — нужно прокидывать `X-Forwarded-For`/`X-Forwarded-Proto`.
>     - **Связанные вопросы:** [[load-balancing-interview#Q1]] про L7 vs L4, [[load-balancing-interview#Q3]] про health checks (тоже работают через LB).
> - [ ] C) `TLS termination` — это процесс закрытия `TCP`-соединения после получения `RST`-пакета, не связан с шифрованием
>     - **Что на самом деле:** «termination» в `TLS` означает завершение зашифрованного канала на конкретном узле, а не закрытие TCP. RST/FIN — это про разрыв TCP-соединения и к TLS отношения не имеет.
>     - **Откуда путаница:** слово «termination» в сетях используется в разных контекстах (TCP termination, session termination, TLS termination), и их легко смешать.
>     - **Если бы это было правдой:** не было бы директивы `ssl_certificate` в `Nginx`-конфиге для termination — расшифровка никак не связана с разрывом соединения.
> - [ ] D) При termination LB и бэкенд используют один общий `TLS`-сеанс, и бэкенд видит оригинальный сертификат клиента
>     - **Что на самом деле:** TLS-сеанс обрывается на LB. Между LB и бэкендом — отдельный канал (`HTTP` или новый `TLS`). Клиентский сертификат (mTLS от клиента) бэкенд не получит автоматически — его нужно прокидывать через заголовки (`X-Client-Cert`, `X-SSL-Client-S-DN`).
>     - **Откуда путаница:** в режиме pass-through бэкенд действительно видит оригинальный TLS, и эту модель ошибочно переносят на termination.
>     - **Если бы это было правдой:** не нужны были бы заголовки `X-Forwarded-*` и `proxy_set_header` — но они стандартная практика именно потому, что бэкенд не имеет прямого доступа к клиентскому TLS-сеансу.


## Q3. (!) Что такое health check и зачем он нужен?

`Health check` — периодическая проверка состояния бэкенда. Нерабочие узлы автоматически исключаются из пула; после восстановления — возвращаются.

**Типы health checks:**
- **HTTP** — `GET /health`, ожидание кода `200`
- **TCP** — проверка доступности порта (соединение устанавливается/нет)
- **gRPC** — через `gRPC Health Checking Protocol`
- **Command/exec** — выполнение команды внутри контейнера

**Конфигурация health check в Nginx:**

```nginx
upstream backend_pool {
    zone backend_pool 64k;

    server backend1.example.com:8080 max_fails=3 fail_timeout=30s;
    server backend2.example.com:8080 max_fails=3 fail_timeout=30s;
    server backend3.example.com:8080 max_fails=3 fail_timeout=30s backup;
}
```

> `max_fails=3` — узел исключается после 3 неудачных запросов; `fail_timeout=30s` — через 30 секунд повторная попытка. `backup` — резервный сервер, получает трафик только при недоступности основных.

**Конфигурация health check в HAProxy:**

```haproxy
backend app_servers
    option httpchk GET /health
    http-check expect status 200

    server app1 10.0.0.1:8080 check inter 5s fall 3 rise 2
    server app2 10.0.0.2:8080 check inter 5s fall 3 rise 2
    server app3 10.0.0.3:8080 check inter 5s fall 3 rise 2
```

> `inter 5s` — интервал проверки; `fall 3` — исключить после 3 неудач; `rise 2` — вернуть после 2 успехов.

**Конфигурация health check в Kubernetes:**

```yaml
containers:
  - name: app
    image: myapp:latest
    ports:
      - containerPort: 8080
    livenessProbe:
      httpGet:
        path: /actuator/health/liveness
        port: 8080
      initialDelaySeconds: 30
      periodSeconds: 10
      failureThreshold: 3
    readinessProbe:
      httpGet:
        path: /actuator/health/readiness
        port: 8080
      initialDelaySeconds: 10
      periodSeconds: 5
      failureThreshold: 2
    startupProbe:
      httpGet:
        path: /actuator/health
        port: 8080
      failureThreshold: 30
      periodSeconds: 2
```

**Важно:** endpoint health check должен быть быстрым и не зависеть от внешних сервисов. В Spring Boot используйте `Spring Boot Actuator` с разделением на liveness и readiness группы.


> [!mcq] Зачем балансировщику нужен health check и что произойдёт, если его отключить?
>
> - [ ] A) Health check нужен только для логирования аптайма; на маршрутизацию запросов не влияет, его можно безопасно отключить
>     - **Что на самом деле:** health check — это активная часть control plane: его результат напрямую определяет, попадает ли узел в пул для маршрутизации. В `Nginx` `max_fails=3` исключает узел из upstream; в `HAProxy` `fall 3` помечает сервер как DOWN; в Kubernetes readinessProbe убирает Pod из Service Endpoints.
>     - **Откуда путаница:** в дашбордах метрик health-status действительно используется для алертов про аптайм, и это видимая часть. Но «невидимая» — это исключение мёртвых узлов из роутинга.
>     - **Если бы это было правдой:** при падении пода/ВМ трафик продолжал бы идти на мёртвый узел, клиенты получали бы 502/connection refused, и SLO рушилось бы при любой замене инстанса.
> - [ ] B) Health check нужен, чтобы балансировщик автоматически масштабировал бэкенды (создавал новые узлы при высокой нагрузке)
>     - **Что на самом деле:** автомасштабирование — это задача `HPA`/`Cluster Autoscaler`/`ASG`, а не балансировщика. Health check лишь определяет «жив/мёртв», а решения о добавлении инстансов принимаются по метрикам CPU/RPS отдельной control plane.
>     - **Откуда путаница:** оба механизма работают вместе (новый под создаётся через HPA → readiness стал OK → LB добавляет его в пул), и их функции склеиваются в восприятии.
>     - **Если бы это было правдой:** в `Nginx`/`HAProxy` была бы директива «scale_up_when_unhealthy» — её нет, потому что LB не управляет жизненным циклом бэкендов.
> - [x] **C) Health check периодически проверяет состояние бэкенда (HTTP `/health`, TCP-порт, gRPC); нерабочие узлы исключаются из пула, восстановившиеся возвращаются обратно**
>     - **Развёрнутое объяснение:** LB отправляет проверочные запросы (`GET /health` для `HTTP`, `connect()` для `TCP`, `grpc.health.v1.Health/Check` для `gRPC`) с настроенным интервалом. После N подряд неудачных ответов узел помечается как unhealthy и исключается из роутинга; после M подряд успешных — возвращается. Это даёт автоматическую устойчивость к падениям без вмешательства человека.
>     - **Пример:** `HAProxy` `option httpchk GET /health; server app1 10.0.0.1:8080 check inter 5s fall 3 rise 2` — проверка каждые 5 секунд, исключить после 3 неудач, вернуть после 2 успехов. В Kubernetes — `livenessProbe`/`readinessProbe`/`startupProbe` на разных стадиях жизни Pod.
>     - **Когда применять:** всегда в production. Разделяйте liveness (нужен ли рестарт), readiness (готов ли принимать трафик) и startup (для долгого warmup, например Spring Boot). Эндпоинт `/health` должен быть быстрым и не зависеть от внешних сервисов — иначе падение БД каскадом убьёт все поды через liveness.
>     - **Подводные камни:** слишком агрессивные probe (interval 1s, threshold 1) дают false positive при GC-паузах; слишком мягкие (interval 30s, fall 5) — клиенты долго получают ошибки. Health check на «глубокий» endpoint, проверяющий БД, превращает любой сбой БД в массовый рестарт подов — обычно делают shallow `/liveness` и deep `/readiness`.
>     - **Связанные вопросы:** [[load-balancing-interview#Q1]] про L4/L7 (health checks на разных уровнях), [[load-balancing-interview#Q2]] про `TLS` (для HTTPS health check), [[load-balancing-interview#Q4]] про sticky sessions (когда узел upbecomes unhealthy, sticky-привязка теряется).
> - [ ] D) Health check работает только для базы данных, для веб-сервисов используется только `keepalive`
>     - **Что на самом деле:** health check универсален — HTTP-сервисы, gRPC, TCP-сервисы, БД, очереди — все имеют свои health checks. `keepalive` — это про переиспользование TCP-соединений (TCP keepalive / HTTP keep-alive), он не проверяет работоспособность приложения, только живость TCP-канала.
>     - **Откуда путаница:** `keepalive`-пакеты иногда называют «health-check на уровне TCP», но они проверяют только соединение, а не приложение (БД может висеть, но TCP-keepalive отвечать).
>     - **Если бы это было правдой:** в `Nginx`/`HAProxy`/`Envoy` не существовало бы директив `httpchk`/`option httpchk`/`health_checks` — но они базовая часть конфигурации любого LB.


## Q4. Что такое sticky session (session affinity) и когда его использовать?

`Sticky session` — запросы одного клиента (по cookie или `IP`) направляются на один и тот же бэкенд. Нужен, когда состояние сессии хранится в памяти приложения.

**Реализация в Nginx:**

```nginx
upstream backend_pool {
    ip_hash;  # привязка по IP клиента
    server backend1.example.com:8080;
    server backend2.example.com:8080;
    server backend3.example.com:8080;
}

# Альтернатива — через cookie (Nginx Plus / OpenResty):
upstream backend_pool_cookie {
    sticky cookie srv_id expires=1h domain=.example.com path=/;
    server backend1.example.com:8080;
    server backend2.example.com:8080;
}
```

**Реализация в Kubernetes:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: my-app
  sessionAffinity: ClientIP
  sessionAffinityConfig:
    clientIP:
      timeoutSeconds: 3600
  ports:
    - port: 80
      targetPort: 8080
```

| Метод | Плюсы | Минусы |
|-------|-------|--------|
| `ip_hash` | Просто, без cookie | Не работает за `NAT` |
| Cookie | Надёжнее, гранулярнее | Требует поддержки cookie |

**Рекомендация:** избегать sticky session. Предпочтительно stateless приложение с общим хранилищем сессий в [Redis](../databases/redis-interview.md) или `Memcached`. Sticky session — только для legacy систем, где невозможно вынести состояние.


> [!mcq] В каком сценарии sticky session оправдан как временное решение?
>
> - [ ] A) Полностью stateless `REST API` за `Nginx` с шаблоном `12-factor`
>   - Почему неверно: при stateless-архитектуре любой инстанс обработает запрос — sticky session даст только лишнюю связанность и неравномерную нагрузку.
>   - Последствие: на rolling-update часть подов окажется перегружена «приклеенными» клиентами, остальные простаивают — деградация p95 без видимой причины.
>
> - [ ] B) Современный микросервис, где состояние сессии вынесено в `Redis`
>   - Почему неверно: общее внешнее хранилище сессий — антипод sticky session; любые инстансы взаимозаменяемы и привязка по `IP`/cookie бесполезна.
>   - Последствие: при отказе «приклеенного» пода клиент потеряет логин, хотя в `Redis` сессия есть — лишний инцидент в support.
>
> - [ ] C) `Kubernetes`-сервис с `sessionAffinity: None` и горизонтальным автоскейлингом
>   - Почему неверно: `sessionAffinity: None` явно отключает «прилипание», а `HPA` рассчитан на равномерное распределение между подами.
>   - Последствие: попытка включить `ClientIP`-affinity сломает балансировку при автоскейлинге — новые поды долго не получат трафик.
>
> - [x] D) Legacy-приложение, хранящее `HttpSession` в памяти JVM, пока идёт миграция на внешнее хранилище
>   - Почему правильно: sticky session — компромисс именно для in-memory `HttpSession`/`@SessionScope`, когда переписать код сразу нельзя; `ip_hash` или sticky cookie сохраняют пользователя на одном инстансе до выноса сессии в `Redis`.
>   - Что произойдёт: пользователь не теряет корзину/логин при балансировке, команда выигрывает время на рефакторинг к stateless.
>   - Метрика успеха: `session_lost_total` стремится к нулю, p95 стабилен; после миграции в `Redis` sticky отключается.
>   - Best practice: фиксировать sticky session как явный технический долг с дедлайном и тикетом на вынос состояния — иначе он остаётся навсегда.

## Q5. Чем отличается активная балансировка от пассивной?

**Активная** — балансировщик сам проверяет бэкенды (health check `GET /health` каждые N секунд) и проактивно исключает нерабочие. Быстрее выявляет сбои.

**Пассивная** — балансировщик считает узел нерабочим только после неудачных ответов на реальные запросы клиентов. Проще в настройке, но клиент может получить ошибку до исключения узла.

```mermaid
graph TB
    subgraph "Активная проверка"
        LB1[Load Balancer] -->|GET /health каждые 5с| B1[Backend 1 ✅]
        LB1 -->|GET /health каждые 5с| B2[Backend 2 ❌]
        LB1 -.->|исключён из пула| B2
    end

    subgraph "Пассивная проверка"
        LB2[Load Balancer] -->|реальный запрос| B3[Backend 3 ✅]
        LB2 -->|реальный запрос → 502| B4[Backend 4 ❌]
        LB2 -.->|исключён после N ошибок| B4
    end
```

**Практика в Kubernetes:** `livenessProbe` и `readinessProbe` — активные проверки. При провале `readinessProbe` под исключается из `Service` и не получает трафик. При провале `livenessProbe` — под перезапускается.

Для критичных сервисов предпочтительна **активная** проверка с интервалом 5-10 секунд и порогом 2-3 неудачи. Метрики (число healthy/unhealthy бэкендов) экспортировать в `Prometheus` для алертинга — подробнее в [вопросах по мониторингу](../monitoring/metrics-tracing-interview.md).


> [!mcq] В чём ключевое отличие активного health check от пассивного на балансировщике?
>
> - [x] A) Активный сам периодически опрашивает бэкенды (`GET /health`), пассивный реагирует только на ошибки реальных клиентских запросов
>   - Почему правильно: активная проверка делает синтетический probe по расписанию и исключает узел до того, как клиент столкнётся с ошибкой; пассивная — ждёт, пока бэкенд начнёт отдавать 5xx/timeouts на боевом трафике, и только тогда помечает его как unhealthy.
>   - Что произойдёт: при отказе пода активный LB снимет трафик за интервал probe (5–10 с), пассивный — после N клиентских ошибок, которые увидят пользователи.
>   - Метрика успеха: `error_rate` во время failover близок к нулю при активной проверке; `mean_time_to_eject` ≈ interval × failure_threshold.
>   - Best practice: для критичных сервисов — активные `readinessProbe`/`livenessProbe` с интервалом 5–10 с и порогом 2–3 неудачи; пассивная — как cheap fallback для legacy.
>
> - [ ] B) Активный требует отдельного агента на каждом бэкенде, пассивный работает без агентов
>   - Почему неверно: активный health check — это HTTP/TCP-запрос балансировщика к стандартному эндпоинту (`/health`, `/actuator/health`), никакого специального агента не нужно.
>   - Последствие: команда начнёт городить sidecar-агенты «для активных проверок», усложняя деплой и обслуживание без выигрыша.
>
> - [ ] C) Активный балансировщик распределяет трафик случайно, пассивный — по `Round Robin`
>   - Почему неверно: активность/пассивность относится исключительно к способу обнаружения сбоев, а не к алгоритму распределения трафика.
>   - Последствие: путаница терминов приводит к неверным решениям в архитектуре — например, выбору алгоритма «по умолчанию» вместо осознанного.
>
> - [ ] D) Активная проверка означает, что приложение само сообщает балансировщику о готовности через push, пассивная — pull
>   - Почему неверно: модель ровно обратная — балансировщик pull-ит health (`GET /health`); push используется в service registry (например, `Eureka` heartbeat), но это не про L4/L7 health checks.
>   - Последствие: попытка построить «push health» поверх `Nginx`/`HAProxy` — лишний код и кастомный exporter вместо стандартного probe.

## Q6. (!) Что такое Round Robin и когда его применять?

`Round Robin` — запросы распределяются по узлам по очереди циклически: 1-й запрос — узел A, 2-й — B, 3-й — C, 4-й — снова A.

```mermaid
graph LR
    R[Запросы: 1,2,3,4,5,6] --> LB[Round Robin LB]
    LB -->|1, 4| A[Server A]
    LB -->|2, 5| B[Server B]
    LB -->|3, 6| C[Server C]
```

**Когда применять:**
- Узлы одинаковой производительности
- Короткие stateless запросы (типичные `REST API`)
- Нет необходимости учитывать текущую загрузку

**Конфигурация в Nginx** (используется по умолчанию):

```nginx
upstream backend_pool {
    server backend1.example.com:8080;
    server backend2.example.com:8080;
    server backend3.example.com:8080;
}
```

**Конфигурация в HAProxy:**

```haproxy
backend app_servers
    balance roundrobin
    server app1 10.0.0.1:8080 check
    server app2 10.0.0.2:8080 check
    server app3 10.0.0.3:8080 check
```

**Недостатки:** не учитывает текущую загрузку узлов. Если один узел обрабатывает тяжёлый запрос, он всё равно получит следующий по очереди.


> [!mcq] В каком сценарии `Round Robin` — рабочий выбор, а в каком он даст неравномерную нагрузку?
>
> - [ ] A) Идеален для `WebSocket`-чата с долгими соединениями: распределяет коннекты «по очереди»
>   - Почему неверно: `Round Robin` не учитывает текущее число активных соединений; при длинных `WebSocket`-сессиях узлы быстро расходятся по нагрузке (один забит, другой пуст).
>   - Последствие: «горячие» инстансы упираются в лимит сокетов и начинают рвать соединения, на остальных простаивает CPU.
>
> - [x] B) Подходит для коротких stateless `REST`-запросов на однородных бэкендах одинаковой мощности
>   - Почему правильно: `Round Robin` циклически шлёт запросы (1→A, 2→B, 3→C, 4→A); при равной производительности и коротком времени обработки нагрузка распределяется почти равномерно — это default в `Nginx upstream` и `HAProxy balance roundrobin`.
>   - Что произойдёт: загрузка CPU/RPS по подам близка; deploy/scaling предсказуем, p95 стабилен.
>   - Метрика успеха: разброс RPS между подами в пределах ±5–10%; `active_connections` примерно равны.
>   - Best practice: использовать как стартовый алгоритм для stateless микросервисов; переключаться на `least_conn`/`least_time` только при доказанной неравномерности нагрузки.
>
> - [ ] C) Лучший выбор для бэкендов с разной производительностью (старый сервер + два новых)
>   - Почему неверно: `Round Robin` шлёт одинаковое число запросов всем — слабый узел захлебнётся, мощные простаивают; для разнородных узлов нужен `Weighted Round Robin`.
>   - Последствие: латентность p95 растёт из-за «узкого» узла, error rate именно на нём, ложные срабатывания health checks.
>
> - [ ] D) Гарантирует, что один клиент всегда попадёт на один и тот же бэкенд
>   - Почему неверно: это свойство sticky session / `ip_hash`, а не `Round Robin`; обычный RR при каждом запросе выбирает следующий узел по кругу.
>   - Последствие: команда полагается на «прилипание», которого нет — in-memory кэш или `HttpSession` начнут терять состояние между запросами.

## Q7. Что такое Weighted Round Robin?

`Weighted Round Robin` — каждому узлу задаётся вес; узлы с большим весом получают пропорционально больше запросов. Применяют при разной производительности узлов или для постепенного ввода нового инстанса (canary deploy).

**Конфигурация в Nginx:**

```nginx
upstream backend_pool {
    server backend1.example.com:8080 weight=5;  # мощный сервер
    server backend2.example.com:8080 weight=3;  # средний
    server backend3.example.com:8080 weight=1;  # новый / canary
}
```

**Конфигурация в HAProxy:**

```haproxy
backend app_servers
    balance roundrobin
    server app1 10.0.0.1:8080 weight 5 check
    server app2 10.0.0.2:8080 weight 3 check
    server app3 10.0.0.3:8080 weight 1 check
```

В примере выше за 9 запросов: A получит 5, B получит 3, C получит 1. Удобно для [canary-деплоев](../cicd/deployment-strategies-interview.md): новому инстансу ставят вес 1, постепенно увеличивая.


> [!mcq] Зачем в Weighted Round Robin задаются разные веса для серверов?
>
> - [ ] **A.** Чтобы каждый сервер получал строго одинаковое число запросов независимо от веса.
>
>     Это описание обычного `Round Robin` без весов. Сам смысл WRR в том, чтобы распределение было **пропорциональным весу**, а не равномерным.
>
>     ❌ Последствие: на слабый сервер пойдёт столько же запросов, сколько на мощный, латентность p99 деградирует на слабом узле.
>
> - [ ] **B.** Чтобы вес определял приоритет — сервер с большим весом обслуживается первым, остальные ждут.
>
>     Путаница с **приоритетной очередью**. WRR не делает приоритизацию запросов — все запросы равны, балансировщик просто распределяет их пропорционально.
>
>     ❌ Последствие: ожидание «приоритета» в очереди создаст ложную модель — на деле все серверы работают параллельно.
>
> - [x] **C.** Чтобы пропорция запросов между серверами соответствовала их относительной производительности или роли (например, canary).
>
>     Корректное описание. За цикл из суммарного веса (`5+3+1=9`) мощный сервер получает 5/9 запросов, средний — 3/9, новый/canary — 1/9. Это позволяет учитывать **разную производительность** узлов и **постепенно вводить новый инстанс** ([canary deploy](../cicd/deployment-strategies-interview.md)).
>
>     **Конфигурация в Nginx:**
>     ```nginx
>     upstream backend {
>         server s1 weight=5;  # мощный
>         server s2 weight=3;  # средний
>         server s3 weight=1;  # canary
>     }
>     ```
>
>     **Когда выбирать:** гетерогенный парк серверов; постепенный rollout новой версии; миграция трафика между ДЦ.
>
> - [ ] **D.** Чтобы вес контролировал число активных соединений на сервере — большой вес держит больше connections.
>
>     Это описание **Least Connections**, а не WRR. WRR не смотрит на текущее число соединений, он распределяет по фиксированной пропорции.
>
>     ❌ Последствие: при длинных WebSocket-сессиях WRR забьёт слабый сервер своей долей и не среагирует — для такой нагрузки нужен `least_conn`.

## Q8. Что такое Least Connections и когда его применять?

`Least Connections` — запрос направляется на узел с наименьшим числом активных соединений. Подходит для длинных сессий (`WebSocket`, потоковая передача, тяжёлые запросы), когда `Round Robin` даёт неравномерную нагрузку.

**Конфигурация в Nginx:**

```nginx
upstream backend_pool {
    least_conn;
    server backend1.example.com:8080;
    server backend2.example.com:8080;
    server backend3.example.com:8080;
}
```

**Конфигурация в HAProxy:**

```haproxy
backend app_servers
    balance leastconn
    server app1 10.0.0.1:8080 check
    server app2 10.0.0.2:8080 check
    server app3 10.0.0.3:8080 check
```

**Когда использовать вместо Round Robin:**
- Запросы значительно различаются по длительности
- `WebSocket` / long-polling соединения
- Тяжёлые отчёты или пакетная обработка


> [!mcq] Когда алгоритм Least Connections предпочтительнее Round Robin?
>
> - [ ] **A.** Когда все запросы короткие и одинаковые по длительности.
>
>     В таком сценарии `Round Robin` уже даёт равномерную нагрузку — отслеживать число соединений нет смысла, это лишний overhead на балансировщике.
>
>     ❌ Последствие: усложнение без выгоды; для коротких stateless HTTP-запросов проще и быстрее обычный RR.
>
> - [ ] **B.** Когда нужно учитывать географическую близость клиента к серверу.
>
>     Это задача **GeoDNS** или `least_time` (latency-based), а не Least Connections. LC смотрит только на счётчик активных соединений, не на сеть.
>
>     ❌ Последствие: на запрос из Европы балансировщик может выбрать «свободный» сервер в США — пользователь получит лишние 150 ms RTT.
>
> - [ ] **C.** Когда требуется привязать клиента к одному и тому же серверу на всю сессию.
>
>     Это **sticky session / session affinity**, а не Least Connections. LC намеренно перераспределяет нагрузку между всеми узлами.
>
>     ❌ Последствие: in-memory сессия на старом сервере «потеряется», логика, ожидающая локального state, сломается.
>
> - [x] **D.** Когда запросы существенно различаются по длительности — длинные WebSocket-соединения, тяжёлые отчёты, стриминг.
>
>     Правильный кейс. `Round Robin` распределяет **счётчик запросов**, а не реальную нагрузку — если один сервер случайно получил несколько долгих WebSocket-соединений, он будет перегружен. `Least Connections` направляет новый запрос туда, где **меньше всего активных connections** прямо сейчас.
>
>     **Конфигурация Nginx:**
>     ```nginx
>     upstream backend {
>         least_conn;
>         server s1; server s2; server s3;
>     }
>     ```
>
>     **Применять для:** WebSocket / long-polling; тяжёлые отчёты или batch-эндпоинты; smtp/streaming-протоколов с разной длительностью.
>
>     **Ограничение:** не учитывает CPU/RAM сервера — только число соединений. Для гетерогенного парка комбинируйте с весами (`least_conn` + `weight`).

## Q9. Что такое Least Response Time?

`Least Response Time` — запрос направляется на узел с наименьшей задержкой ответа (или комбинацией задержки и числа соединений). Минимизирует латентность для клиента.

**Реализация:** `HAProxy` поддерживает аналог через `balance hdr` с health check latency. `AWS ALB` выбирает таргеты с наименьшим числом активных запросов. `Nginx Plus` предоставляет директиву `least_time`:

```nginx
# Nginx Plus (коммерческая версия)
upstream backend_pool {
    least_time header;  # по времени первого байта ответа
    server backend1.example.com:8080;
    server backend2.example.com:8080;
}
```

**Когда применять:** когда узлы расположены в разных дата-центрах или имеют разную производительность, и цель — минимальная p95/p99 латентность.


> [!mcq] По какому критерию Least Response Time выбирает целевой узел?
>
> - [x] **A.** По наименьшему времени ответа (или комбинации latency и активных соединений) с целью минимизировать p95/p99 задержки клиента.
>
>     Корректно. Алгоритм отслеживает **время ответа** (`TTFB` или полное время) и направляет запрос на сервер с минимальной задержкой. Это критично, когда узлы расположены в разных ДЦ или имеют разную производительность — `Round Robin` и `Least Connections` этого не учитывают.
>
>     **Реализации:**
>     - `Nginx Plus`: директива `least_time header | last_byte`.
>     - `AWS ALB`: выбирает таргеты с наименьшим числом активных запросов (приближение к LRT).
>     - `HAProxy`: косвенно через `balance leastconn` с health-check latency.
>
>     ```nginx
>     # Nginx Plus
>     upstream backend {
>         least_time header;  # минимум TTFB
>         server s1; server s2;
>     }
>     ```
>
>     **Когда применять:** разные ДЦ; гетерогенные машины; жёсткий SLA по p95/p99 латентности.
>
> - [ ] **B.** По наименьшему числу активных соединений, без учёта времени ответа.
>
>     Это **Least Connections**, а не Least Response Time. LC смотрит на counter соединений и игнорирует фактическую latency узла — медленный сервер с малым числом connections всё равно получит запрос.
>
>     ❌ Последствие: на «свободный, но тормозящий» сервер пойдёт трафик, p99 латентность вырастет.
>
> - [ ] **C.** По хэшу IP-адреса клиента для привязки запросов к одному серверу.
>
>     Это **IP Hash / Consistent Hashing**, нужен для sticky-сессий и cache-locality. LRT не использует хэширование — он динамически выбирает быстрейший узел.
>
>     ❌ Последствие: при IP Hash клиент с медленного маршрута будет вечно прибит к медленному серверу.
>
> - [ ] **D.** Случайным образом среди серверов, прошедших health check.
>
>     Это **Random** алгоритм (есть в HAProxy `balance random`). Он не оптимизирует латентность, а полагается на закон больших чисел.
>
>     ❌ Последствие: при малом RPS случайный выбор регулярно попадёт на самый медленный узел — нет защиты от деградации p99.

## Q10. Как выбрать алгоритм балансировки для микросервисов?

Выбор зависит от типа нагрузки, требований к латентности и архитектуры:

| Сценарий | Рекомендуемый алгоритм | Почему |
|----------|----------------------|--------|
| Короткие `REST` запросы, stateless | `Round Robin` | Простота, равномерность |
| Разная мощность серверов | `Weighted Round Robin` | Учёт производительности |
| Длинные запросы, `WebSocket` | `Least Connections` | Учёт текущей загрузки |
| Минимальная латентность критична | `Least Response Time` | Выбор самого быстрого узла |
| Кэширование, sharding | `Consistent Hashing` | Локальность данных |
| Обязательное состояние на узле | `Sticky Session` | Привязка клиента к узлу |

**Рекомендация:** для [микросервисов](microservices-interview.md) — stateless приложение + `Round Robin`. Если нужна более умная балансировка — `Least Connections`. Sticky session — крайняя мера; лучше вынести состояние в [Redis](../databases/redis-interview.md).


> [!mcq] Какой алгоритм балансировки выбрать для stateless REST-микросервиса с одинаковыми по мощности подами?
>
> - [ ] A) `Least Response Time` — он всегда даёт минимальную латентность и не имеет недостатков
>     - **Что на самом деле:** `Least Response Time` хорош, когда есть реальная разница в скорости узлов (разный hardware, разные регионы), но для одинаковых stateless-подов он даёт ту же картину, что и Round Robin, плюс дополнительные накладные расходы на сбор метрик ответа и алгоритм взвешивания.
>     - **Откуда путаница:** название звучит как «всегда самый быстрый», и кажется, что лучше выбирать его «на всякий случай».
>     - **Если бы это было правдой:** все балансировщики использовали бы `Least Response Time` по умолчанию, но в реальности `Round Robin` — дефолт в `Nginx`/`HAProxy`, а `Least Response Time` есть только в коммерческих сборках (`Nginx Plus`).
> - [x] **B) `Round Robin` — простой, равномерный, не требует состояния и хорошо ложится на stateless под одинаковой мощности**
>     - **Развёрнутое объяснение:** для stateless REST с короткими запросами и идентичными подами `Round Robin` оптимален: трафик распределяется равномерно, балансировщику не нужно хранить счётчики соединений или метрики латентности, а каждый запрос можно обслужить на любом узле. Это дефолт в `Nginx upstream` и `HAProxy` именно из-за такой типичной микросервисной нагрузки.
>     - **Пример:** `upstream api { server pod1; server pod2; server pod3; }` без явного указания алгоритма — Nginx использует Round Robin и равномерно ротирует поды. Для Kubernetes Service `ClusterIP` kube-proxy в режиме iptables даёт фактически Round Robin между подами.
>     - **Когда применять:** короткие REST-запросы (`<100ms`), stateless приложение, одинаковые поды по CPU/RAM, отсутствие sticky session. Это «золотой стандарт» для микросервисов в Kubernetes.
>     - **Подводные камни:** Round Robin плох, если запросы сильно отличаются по длительности (один тянет 5s, остальные — 50ms): «толстые» запросы могут сконцентрироваться на одном узле. В таком случае стоит переключиться на `Least Connections`. Также Round Robin игнорирует фактическую загрузку — если под уже на 100% CPU, ему всё равно прилетит свой запрос.
>     - **Связанные вопросы:** [[load-balancing-interview#Q6]] про сам Round Robin, [[load-balancing-interview#Q8]] про Least Connections как альтернативу.
> - [ ] C) `Sticky Session` по cookie — это единственный надёжный способ балансировки в production
>     - **Что на самом деле:** sticky session нужен только когда состояние пользователя живёт **на узле** (in-memory session, локальный кэш). Для stateless сервиса sticky session — антипаттерн: он мешает равномерному распределению и ломает graceful rollout (часть юзеров остаётся «прибита» к старому поду).
>     - **Откуда путаница:** в монолитах с server-side session sticky был необходим, и эта привычка переносится на микросервисы по инерции.
>     - **Если бы это было правдой:** Kubernetes не использовал бы Round Robin как дефолт в Service, а вся документация Nginx не начиналась бы со слов «по умолчанию round-robin, и этого обычно достаточно».
> - [ ] D) `Consistent Hashing` по `client_id` — потому что он гарантирует «один клиент = один под» во всех сценариях
>     - **Что на самом деле:** `Consistent Hashing` нужен для **локальности данных** (кэширование, sharding), а не для обычных stateless REST. Он привязывает клиента к поду, что снижает hit-rate локальных кэшей при перебалансировке только частично, но при этом ломает равномерность нагрузки, если распределение клиентов неравномерно.
>     - **Откуда путаница:** consistent hashing звучит как «умный round-robin» и часто упоминается в архитектурных гайдах по Cassandra/Memcached.
>     - **Если бы это было правдой:** не нужен был бы Redis для общего кэша — каждый сервис распределял бы свои данные через hashing на инстансах, но это работает только в специфичных кэш-сценариях, а не для обычного API.

## Q11. (!) Как настроить upstream балансировку в Nginx?

Базовая конфигурация `Nginx` upstream с различными алгоритмами и параметрами:

```nginx
# Определение upstream-пула серверов
upstream api_backend {
    # Алгоритм балансировки (по умолчанию round-robin)
    least_conn;

    # Зона разделяемой памяти для worker-процессов
    zone api_backend 64k;

    server 10.0.0.1:8080 weight=5 max_fails=3 fail_timeout=30s;
    server 10.0.0.2:8080 weight=3 max_fails=3 fail_timeout=30s;
    server 10.0.0.3:8080 weight=1 max_fails=3 fail_timeout=30s;
    server 10.0.0.4:8080 backup;  # резервный сервер

    keepalive 32;  # HTTP keep-alive к бэкендам
}

server {
    listen 80;
    server_name api.example.com;

    location / {
        proxy_pass http://api_backend;

        # Заголовки для бэкенда
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Таймауты
        proxy_connect_timeout 5s;
        proxy_read_timeout 30s;
        proxy_send_timeout 10s;

        # Retry при ошибках — перенаправить на следующий upstream
        proxy_next_upstream error timeout http_502 http_503;
        proxy_next_upstream_tries 2;

        # Keep-alive к upstream
        proxy_http_version 1.1;
        proxy_set_header Connection "";
    }
}
```

**Ключевые параметры:**
- `weight` — вес сервера для распределения
- `max_fails` / `fail_timeout` — пассивный health check
- `backup` — резервный сервер (трафик только при недоступности основных)
- `proxy_next_upstream` — автоматический retry на следующий сервер при ошибке
- `keepalive` — пул persistent connections к бэкендам (снижает латентность)


> [!mcq] Какую роль играют параметры `max_fails=3 fail_timeout=30s` в `Nginx upstream`?
>
> - [ ] A) `max_fails` ограничивает количество соединений к серверу, а `fail_timeout` — максимальную длительность одного запроса
>     - **Что на самом деле:** `max_fails` — это **число неуспешных попыток до того, как сервер пометится как недоступный**, а `fail_timeout` имеет двойное значение: окно, в котором считаются эти fails, и длительность, на которую сервер выводится из пула. С количеством соединений эти параметры не связаны — для лимита соединений есть директива `max_conns`.
>     - **Откуда путаница:** название `fail_timeout` звучит как «таймаут на запрос», но фактический таймаут запроса задаётся через `proxy_connect_timeout`/`proxy_read_timeout`.
>     - **Если бы это было правдой:** при больших значениях `max_fails` Nginx бы душил пропускную способность одного сервера, и не было бы смысла в отдельной директиве `max_conns` — но это две независимые механики.
> - [ ] B) Они задают активный health check: Nginx сам ходит на `/health` каждые `fail_timeout` секунд, а после `max_fails` неуспехов исключает сервер
>     - **Что на самом деле:** в OSS Nginx **нет активных health-проверок** — это фича только `Nginx Plus` (директива `health_check`). `max_fails`/`fail_timeout` реализуют **пассивный** health check: статус сервера обновляется по результатам реальных пользовательских запросов, без отдельных HTTP-зондов.
>     - **Откуда путаница:** в HAProxy и AWS ALB активные health checks включены по умолчанию, и легко ожидать того же от Nginx.
>     - **Если бы это было правдой:** не было бы коммерческой ценности у Nginx Plus и не существовало бы экосистемы внешних health-checker-ов вроде `nginx-amplify` или `consul-template`.
> - [x] **C) Это пассивный health check: если в окне `fail_timeout=30s` накопится `max_fails=3` ошибок, сервер исключается из upstream на следующие 30 секунд**
>     - **Развёрнутое объяснение:** Nginx OSS считает «неуспехами» те ошибки, что перечислены в `proxy_next_upstream` (по умолчанию `error timeout`). Если в скользящем окне длиной `fail_timeout` накопилось `max_fails` таких ошибок, сервер помечается недоступным на длительность `fail_timeout`. После истечения окна Nginx снова попробует отправить туда запрос — если успешно, сервер возвращается в ротацию.
>     - **Пример:** `server backend1:8080 max_fails=3 fail_timeout=30s;` — три ошибки за 30 секунд → сервер исключён на 30 секунд → следующий запрос (после окна) станет «проверочным». Это работает без специальных health-эндпоинтов, прямо на боевом трафике.
>     - **Когда применять:** в OSS Nginx это стандартный механизм failover для микросервисов в Kubernetes/VM. Если нужен **активный** check со специальным `/health` эндпоинтом и тюнингом частоты — переходить на Nginx Plus или ставить перед Nginx внешний health-checker.
>     - **Подводные камни:** `max_fails=0` **отключает** проверку (сервер считается всегда доступным) — частая ошибка при копипасте. Также пассивный check «узнаёт» о падении только при реальной ошибке клиента, то есть первые `max_fails` запросов после деградации получат `5xx`. Третий нюанс: если все серверы помечены недоступными, Nginx использует `no live upstreams while connecting` — на это нужен `backup` сервер.
>     - **Связанные вопросы:** [[load-balancing-interview#Q3]] про health checks в целом, [[load-balancing-interview#Q5]] про активную vs пассивную балансировку, [[load-balancing-interview#Q12]] про конфигурацию failover.
> - [ ] D) `max_fails` — это retry: после ошибки Nginx 3 раза повторит запрос на том же сервере, и только потом переключится
>     - **Что на самом деле:** retry на следующий сервер регулируется `proxy_next_upstream` и `proxy_next_upstream_tries`, и работает в рамках **одного клиентского запроса**. `max_fails` же это «банковский счёт» ошибок сервера во времени, не имеющий отношения к попыткам повтора одного запроса.
>     - **Откуда путаница:** слово «fails» и число 3 ассоциируется с «3 попытки», что сливает в одно две независимые механики.
>     - **Если бы это было правдой:** при `max_fails=3` каждый ответ 502 от бэкенда задерживал бы клиента на сумму трёх таймаутов — это было бы видно в любом APM, но в реальности retry идёт на **другой** сервер, а не на тот же.

## Q12. Как настроить health checks и failover в Nginx?

`Nginx` Open Source поддерживает **пассивные** health checks через `max_fails` / `fail_timeout`. Активные health checks доступны в `Nginx Plus`.

**Пассивные health checks (Open Source):**

```nginx
upstream backend_pool {
    server backend1:8080 max_fails=3 fail_timeout=30s;
    server backend2:8080 max_fails=3 fail_timeout=30s;
    server backend3:8080 max_fails=3 fail_timeout=30s;
    server backup1:8080 backup;  # только при падении основных
}

server {
    location / {
        proxy_pass http://backend_pool;

        # Какие ошибки считать "fail" для max_fails
        proxy_next_upstream error timeout http_500 http_502 http_503 http_504;
        proxy_next_upstream_tries 3;
        proxy_next_upstream_timeout 10s;
    }
}
```

**Активные health checks (Nginx Plus):**

```nginx
upstream backend_pool {
    zone backend_pool 64k;
    server backend1:8080;
    server backend2:8080;

    # Активная проверка каждые 5 секунд
    health_check interval=5s fails=3 passes=2 uri=/health;
    health_check match=health_ok;
}

match health_ok {
    status 200;
    body ~ "UP";
}
```


> [!mcq] Чем принципиально отличается активный health check в `Nginx Plus` от пассивного в OSS Nginx?
>
> - [ ] A) Активный check проверяет TLS-сертификаты, а пассивный — только TCP-соединение, поэтому в OSS Nginx нельзя балансировать HTTPS-бэкенды
>     - **Что на самом деле:** OSS Nginx прекрасно балансирует HTTPS-бэкенды (через `proxy_pass https://...` или `stream` модуль) — разница между активным и пассивным health check **не в протоколе**, а в способе обнаружения проблем. Сертификаты валидируются при установке TLS-соединения независимо от типа health check.
>     - **Откуда путаница:** активный check может включать кастомную валидацию через `match { status 200; body ~ "UP"; }`, и это путают с TLS-валидацией.
>     - **Если бы это было правдой:** все production-стенды с HTTPS-бэкендами вынужденно покупали бы Nginx Plus, но в реальности OSS Nginx — стандарт для балансировки HTTPS-микросервисов в Kubernetes.
> - [ ] B) Активный check запускается только при деплое нового сервера, а пассивный — постоянно
>     - **Что на самом деле:** ровно наоборот по периодичности. **Активный** check работает **постоянно** по расписанию (`health_check interval=5s`) — Nginx сам периодически опрашивает `/health` эндпоинт. **Пассивный** check «срабатывает» только когда реальный пользовательский трафик упирается в ошибку.
>     - **Откуда путаница:** слово «активный» можно интерпретировать как «специально активируемый», но в терминологии балансировщиков «активный» = «инициируемый балансировщиком», а «пассивный» = «вытекающий из трафика».
>     - **Если бы это было правдой:** активный check был бы бесполезен в проде (сервер падает не только при деплое), и не было бы смысла платить за Nginx Plus.
> - [ ] C) Пассивный check умеет проверять кастомную логику в body ответа (`body ~ "UP"`), а активный — нет
>     - **Что на самом деле:** всё наоборот. Активный check в Nginx Plus поддерживает богатую кастомизацию: `match { status 200..399; header Content-Type ~ "application/json"; body ~ '"status":"UP"'; }`. Пассивный же реагирует только на ошибки соединения и статус-коды из списка `proxy_next_upstream` — он в принципе не парсит body, потому что body уже стримится клиенту.
>     - **Откуда путаница:** в обоих случаях есть слово «check», и не очевидно, какой из них «умнее».
>     - **Если бы это было правдой:** Nginx Plus не стоил бы $2500/инстанс/год, потому что главная фича активного check — именно гибкая проверка состояния, недоступная пассивному.
> - [x] **D) Активный check сам генерирует фоновые запросы к `/health` по расписанию и выявляет падение **до** того, как реальный пользователь получит ошибку; пассивный обнаруживает проблему только по факту неудачного клиентского запроса**
>     - **Развёрнутое объяснение:** активный health check (директивы `health_check interval=5s fails=3 passes=2 uri=/health` + `match`) работает как отдельный фоновый scheduler внутри Nginx: воркер сам ходит на каждый upstream-сервер по заданному URI с заданной частотой, и по результату решает, исключить ли сервер из ротации. Падение детектится за десятки секунд **без участия клиентского трафика**. Пассивный же check (`max_fails`/`fail_timeout`) реактивен: пока пользователь не получит ошибку, балансировщик не узнает о деградации, то есть `max_fails` запросов гарантированно «съедятся» как `5xx`.
>     - **Пример:** активный — `upstream { zone backend 64k; server b1:8080; health_check interval=5s fails=3 passes=2 uri=/health; }` плюс `match health_ok { status 200; body ~ "UP"; }`. Пассивный — `upstream { server b1:8080 max_fails=3 fail_timeout=30s; }`.
>     - **Когда применять:** активный — когда нужно «не светить клиенту 502» и есть SLA на latency p99/error rate (платёжные сервисы, API gateway). Пассивный — на бэкендах с большим RPS, где первые 3 ошибки в 30-секундном окне не критичны, и не хочется платить за Nginx Plus.
>     - **Подводные камни:** активный check добавляет нагрузку на бэкенды (каждые 5s × количество балансировщиков × количество апстримов — может быть значимо). Также `/health` эндпоинт должен быть **lightweight** и **независим** от внешних зависимостей, иначе при деградации БД активный check выкинет все поды из пула одновременно. Пассивный страдает от «холодного старта»: после рестарта Nginx сервер считается живым, и первые запросы пойдут на упавший узел.
>     - **Связанные вопросы:** [[load-balancing-interview#Q3]] про health check как концепцию, [[load-balancing-interview#Q5]] про активную vs пассивную балансировку, [[load-balancing-interview#Q11]] про базовую конфигурацию Nginx upstream.

## Q13. Как настроить rate limiting в Nginx?

`Rate limiting` — ограничение частоты запросов для защиты бэкендов от перегрузки. Реализуется через модуль `ngx_http_limit_req_module`.

```nginx
# Определение зоны ограничения (в блоке http)
http {
    # 10 req/s per IP, зона 10MB (~160k IP-адресов)
    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;

    # Ограничение по API-ключу
    limit_req_zone $http_x_api_key zone=apikey_limit:10m rate=100r/s;

    server {
        location /api/ {
            # burst=20 — очередь до 20 запросов сверх лимита
            # nodelay — не задерживать burst-запросы
            limit_req zone=api_limit burst=20 nodelay;
            limit_req_status 429;

            proxy_pass http://api_backend;
        }

        location /api/heavy/ {
            # Более строгий лимит для тяжёлых эндпоинтов
            limit_req zone=api_limit burst=5 nodelay;
            limit_req_status 429;

            proxy_pass http://api_backend;
        }
    }
}
```

При превышении лимита клиент получает `429 Too Many Requests`. Подробнее о rate limiting в контексте API — в [вопросах по HTTP/REST](../api/http-rest-interview.md).


> [!mcq] Что делает директива `limit_req zone=api_limit burst=20 nodelay;` в Nginx?
>
> - [x] **Разрешает до 10 req/s на IP, при превышении принимает в очередь до 20 burst-запросов и обрабатывает их немедленно без задержки**
>
>   Это и есть **token bucket с burst-ёмкостью**: `rate=10r/s` задаёт скорость пополнения, `burst=20` — глубину очереди, `nodelay` отключает задержку между burst-запросами (обрабатываются сразу, а не равномерно).
>
>   **Когда применять:** API с пиковой неравномерной нагрузкой — пользователь может отправить серию запросов залпом (например, при загрузке страницы), а не строго по 10 в секунду.
>
>   **Когда поломается:** без `nodelay` burst-запросы будут искусственно задержаны до `rate` — это превратит limiter в throttler и сломает UX. Без `burst` — 11-й запрос за секунду сразу получит `429`, что слишком жёстко для реальных клиентов.
>
>   **Best practice:** для API ставьте `burst` равным 2-3× от ожидаемого RPS на пике, всегда добавляйте `nodelay`. Для тяжёлых эндпоинтов (`/api/heavy/`) держите отдельную зону со строгим лимитом.
>
> - [ ] **Жёстко ограничивает все запросы до 20 в секунду, всё сверх — отбрасывает**
>
>   Путаница в параметрах: `20` — это `burst` (глубина очереди), а не `rate`. `Rate` задаётся в `limit_req_zone` и здесь равен `10r/s`. И `nodelay` не «отбрасывает», а наоборот разрешает burst без задержки.
>
> - [ ] **Создаёт зону памяти 20MB для хранения IP-адресов клиентов**
>
>   Размер зоны задаётся в `limit_req_zone ... zone=api_limit:10m` (10MB), а не в `limit_req`. Здесь `20` — это глубина очереди burst-запросов, не размер памяти.
>
> - [ ] **Включает балансировку запросов между 20 backend-серверами без задержки**
>
>   `limit_req` — модуль rate limiting, он не имеет отношения к балансировке. Балансировка настраивается через `upstream` блок и директиву `proxy_pass`. Параметр `burst=20` относится к очереди rate-limit, а не к серверам.

## Q14. (!) Как настроить балансировку в HAProxy?

`HAProxy` — высокопроизводительный балансировщик с богатыми возможностями L4/L7. Базовая конфигурация:

```haproxy
global
    maxconn 50000
    log /dev/log local0
    stats socket /var/run/haproxy.sock mode 600

defaults
    mode http
    log global
    option httplog
    option dontlognull
    timeout connect 5s
    timeout client  30s
    timeout server  30s
    timeout http-request 10s
    retries 3

# Статистика HAProxy (dashboard)
listen stats
    bind *:8404
    stats enable
    stats uri /stats
    stats refresh 10s

# Frontend — точка входа
frontend http_front
    bind *:80
    bind *:443 ssl crt /etc/haproxy/certs/example.pem

    # ACL для маршрутизации
    acl is_api path_beg /api
    acl is_static path_beg /static

    # Path-based routing
    use_backend api_servers if is_api
    use_backend static_servers if is_static
    default_backend app_servers

# Backend — пул серверов
backend api_servers
    balance leastconn
    option httpchk GET /health
    http-check expect status 200

    server api1 10.0.0.1:8080 check inter 5s fall 3 rise 2 weight 5
    server api2 10.0.0.2:8080 check inter 5s fall 3 rise 2 weight 3
    server api3 10.0.0.3:8080 check inter 5s fall 3 rise 2 weight 1

backend app_servers
    balance roundrobin
    option httpchk GET /health
    cookie SERVERID insert indirect nocache

    server app1 10.0.1.1:8080 check cookie app1
    server app2 10.0.1.2:8080 check cookie app2

backend static_servers
    balance roundrobin
    server static1 10.0.2.1:80 check
    server static2 10.0.2.2:80 check
```

**Ключевые возможности HAProxy:**
- `ACL` — гибкая маршрутизация по path, headers, cookies
- `cookie SERVERID insert` — sticky sessions через cookie
- Встроенный dashboard (`stats enable`)
- Поддержка `SSL/TLS` termination


> [!mcq] Что делает связка `cookie SERVERID insert indirect nocache` + `server app1 ... cookie app1` в HAProxy backend?
>
> - [ ] **Шифрует cookie клиента секретным ключом сервера для защиты сессии от MITM-атаки**
>
>   HAProxy сюда не шифрует cookie — он только вставляет имя сервера как plain-text идентификатор. Защита от MITM достигается через `SSL/TLS termination` (`bind *:443 ssl crt ...`), а не через cookie persistence.
>
> - [x] **Включает sticky sessions: HAProxy вставляет cookie `SERVERID=app1` и при следующих запросах направляет клиента на тот же backend по значению cookie**
>
>   Это **application cookie persistence** — балансировщик сам управляет cookie (`insert` — создаёт, `indirect` — удаляет перед передачей на backend, `nocache` — запрещает кэшировать ответ с этой cookie). Каждый `server` получает свой идентификатор (`cookie app1`), который попадает в значение cookie.
>
>   **Когда применять:** legacy-приложения со stateful in-memory сессией, где миграция на Redis/Hazelcast невозможна, а распределённую сессию между нодами не настроить.
>
>   **Когда поломается:** при падении `app1` клиент с `SERVERID=app1` получит ошибку, если HAProxy не настроен на failover. Также ломает horizontal autoscaling — новые поды не получают трафик, пока активные клиенты не перерегистрируются.
>
>   **Best practice:** предпочитайте stateless-приложения с внешним session store (Redis). Sticky sessions — это техдолг. Если без них нельзя — обязательно настройте `option redispatch` и используйте короткий TTL cookie.
>
> - [ ] **Настраивает rate limiting по cookie: при превышении лимита запросов на один SERVERID — HAProxy возвращает 429**
>
>   HAProxy умеет rate limiting через `stick-table`, но синтаксис другой (`http-request track-sc0 ...`). Директива `cookie SERVERID insert` отвечает только за session affinity, не за лимиты.
>
> - [ ] **Кэширует ответы backend в cookie на стороне HAProxy для ускорения повторных запросов**
>
>   HAProxy не является кэширующим прокси (`cache` модуль есть, но настраивается через `http-request cache-use`, а не через `cookie`). `nocache` здесь означает «запретить промежуточным кэшам сохранять ответ с этой cookie», а не «включить кэш».

## Q15. Как настроить health checks в HAProxy?

`HAProxy` поддерживает развитые активные и пассивные health checks:

```haproxy
backend app_servers
    balance roundrobin

    # HTTP health check
    option httpchk GET /actuator/health
    http-check expect status 200

    # Дополнительные проверки ответа
    http-check expect rstring "UP"

    # Параметры проверки на каждый сервер
    # inter — интервал проверки
    # fall  — число неудач для исключения
    # rise  — число успехов для возврата
    # slowstart — постепенный ввод после восстановления
    server app1 10.0.0.1:8080 check inter 3s fall 3 rise 2 slowstart 60s
    server app2 10.0.0.2:8080 check inter 3s fall 3 rise 2 slowstart 60s
    server app3 10.0.0.3:8080 check inter 3s fall 3 rise 2 slowstart 60s backup

    # Таймаут health check
    timeout check 2s
```

**`slowstart`** — после восстановления сервер получает нагрузку постепенно (за 60 секунд вес растёт от 0 до назначенного). Это предотвращает «шторм» запросов на только что ожившый сервер.


> [!mcq] Что делает параметр `slowstart 60s` у `server app1 ... check inter 3s fall 3 rise 2 slowstart 60s` в HAProxy?
>
> - [ ] **Задерживает первый health check на 60 секунд после старта HAProxy для прогрева JVM на backend**
>
>   Начальная задержка проверки не настраивается через `slowstart`. HAProxy начинает health-проверки сразу после старта с интервалом `inter`. Для прогрева JVM на backend используют `application warmup` внутри сервиса, а не параметр балансировщика.
>
> - [ ] **Ограничивает скорость передачи данных на backend до 60 секунд для медленных клиентов**
>
>   Это путаница со `slowloris`-защитой или rate limiting. Скорость трафика к backend контролируется через `timeout server`, `maxconn` и stick-tables, а не через `slowstart`. Здесь параметр не имеет отношения к данным клиентов.
>
> - [x] **После восстановления сервера (rise=2 успешных проверки) HAProxy постепенно повышает его вес от 0 до полного за 60 секунд, защищая от шторма запросов на «холодный» backend**
>
>   Это **gradual warmup механизм**: сервер, который только что вернулся из недоступного состояния, не получает 100% своей доли трафика сразу. Вес растёт линейно за `slowstart` интервал — на 30-й секунде сервер получит ~50% назначенной нагрузки.
>
>   **Когда применять:** JVM-приложения с JIT-компиляцией, сервисы с прогревом кэшей (Caffeine, EhCache), connection pool warmup. Без `slowstart` ожившая нода получит резкий всплеск трафика и снова может упасть (cascade failure).
>
>   **Когда поломается:** если `slowstart` слишком короткий (< 30s) — JVM не успеет прогреться. Если слишком длинный (> 5min) — нагрузка на здоровые ноды затянется, что может вызвать их деградацию. Также `slowstart` не работает при первом старте сервера — только после `fall` → `rise` цикла.
>
>   **Best practice:** для JVM-сервисов ставьте `slowstart 60-120s`, синхронизируйте с `application warmup` и health endpoint, который начинает возвращать `UP` только после прогрева пула соединений и кэшей.
>
> - [ ] **Включает retry-логику: HAProxy повторяет запрос на backend до 60 раз при ошибке**
>
>   Retry-логика настраивается через `retries N` в `defaults` или `backend`, а не через `slowstart`. Здесь `60s` — это интервал прогрева веса, а не количество ретраев. `retries 3` в `defaults` блоке означает 3 попытки переподключения к серверу.

## Q16. (!) Что такое клиентская и серверная балансировка?

**Серверная** — отдельный компонент (`Nginx`, `AWS ALB`, `HAProxy`) принимает все запросы и распределяет на бэкенды. Клиент не знает о бэкендах.

**Клиентская** — клиент (приложение, `SDK`) сам выбирает узел из списка, полученного из service discovery (`Consul`, `Eureka`). Нет единой точки отказа балансировщика.

```mermaid
graph TB
    subgraph "Серверная балансировка"
        C1[Клиент] --> LB[Load Balancer]
        LB --> S1[Service 1]
        LB --> S2[Service 2]
        LB --> S3[Service 3]
    end

    subgraph "Клиентская балансировка"
        C2[Клиент + LB логика] --> SD[Service Discovery]
        SD -.->|список инстансов| C2
        C2 --> S4[Service 1]
        C2 --> S5[Service 2]
        C2 --> S6[Service 3]
    end
```

| Критерий | Серверная | Клиентская |
|----------|-----------|------------|
| Single point of failure | Да (балансировщик) | Нет |
| Дополнительный hop | Да | Нет (прямое соединение) |
| Сложность клиента | Низкая | Выше (LB логика в клиенте) |
| Примеры | `Nginx`, `HAProxy`, `AWS ALB` | `Spring Cloud LoadBalancer`, `gRPC` client LB |

В [микросервисной архитектуре](microservices-interview.md) часто комбинируют оба подхода: внешний трафик через серверный балансировщик, межсервисные вызовы — через клиентскую балансировку.


> [!mcq]
> - [ ] **Серверная и клиентская балансировка — это разные названия одного и того же подхода: внешний компонент типа Nginx распределяет запросы**
>
>   Это противоречит самому различию. Серверная балансировка действительно реализуется через отдельный компонент (`Nginx`, `HAProxy`, `AWS ALB`), но клиентская — это когда клиент САМ выбирает узел из списка инстансов, полученного из Service Discovery (`Eureka`, `Consul`). Эти подходы принципиально различаются по архитектуре и SPOF.
>
> - [ ] **Клиентская балансировка — это балансировка нагрузки между клиентами (browser-side), а серверная — между серверами**
>
>   Смешение терминов. «Клиент» здесь — это сервис-потребитель (например, микросервис A, вызывающий микросервис B), а не браузер пользователя. Клиентская балансировка означает, что логика выбора инстанса находится в клиентском приложении (через `Spring Cloud LoadBalancer`, `gRPC client LB`), а не в браузере.
>
> - [ ] **При клиентской балансировке клиент всегда подключается к фиксированному адресу, а серверная использует round-robin**
>
>   Алгоритм (round-robin, least-connections, weighted) — это ортогональная характеристика, не зависящая от типа балансировки. И серверная, и клиентская могут использовать любой алгоритм. Принципиальное различие: КТО принимает решение о выборе инстанса — внешний компонент или сам клиент.
>
> - [x] **Серверная — отдельный компонент (Nginx, AWS ALB) принимает все запросы и распределяет на бэкенды; клиентская — клиент сам выбирает узел из Service Discovery, нет SPOF балансировщика**
>
>   Корректное определение. **Серверная:** все запросы идут через единый балансировщик (`Nginx`, `HAProxy`, `AWS ALB`) — он становится single point of failure, добавляется лишний hop. **Клиентская:** клиент получает список инстансов из Service Discovery (`Eureka`, `Consul`) и сам выбирает узел через библиотеку (`Spring Cloud LoadBalancer`, `gRPC client LB`) — нет SPOF, прямое соединение, но клиент сложнее. На практике комбинируют: внешний трафик через серверный LB, межсервисные вызовы — через клиентский.
>
>   **Practical:** для внешнего трафика (north-south) используй `AWS ALB`/`Nginx`; для межсервисных вызовов (east-west) в Kubernetes — service mesh (`Istio`) или client-side LB (`Spring Cloud LoadBalancer`).

## Q17. Как настроить Spring Cloud LoadBalancer?

`Spring Cloud LoadBalancer` — замена устаревшего `Netflix Ribbon` для клиентской балансировки в Spring-экосистеме.

**Зависимости:**

```groovy
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
}
```

**Конфигурация WebClient с балансировкой:**

```java
@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced  // включает клиентскую балансировку
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}

@Service
public class OrderService {

    private final WebClient webClient;

    public OrderService(WebClient.Builder webClientBuilder) {
        // "user-service" — имя из Service Discovery
        this.webClient = webClientBuilder
            .baseUrl("http://user-service")
            .build();
    }

    public Mono<User> getUser(Long userId) {
        return webClient.get()
            .uri("/api/users/{id}", userId)
            .retrieve()
            .bodyToMono(User.class);
    }
}
```

**Конфигурация RestTemplate с балансировкой:**

```java
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Service
public class PaymentService {

    private final RestTemplate restTemplate;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Order getOrder(Long orderId) {
        // "order-service" резолвится через Service Discovery
        return restTemplate.getForObject(
            "http://order-service/api/orders/{id}",
            Order.class, orderId
        );
    }
}
```

По умолчанию используется `Round Robin`. Подробнее о Service Discovery и Spring Cloud — в [вопросах по Spring Cloud](../frameworks/spring/spring-cloud-interview.md).


> [!mcq]
> - [x] **Подключить `spring-cloud-starter-loadbalancer` + Service Discovery (Eureka), создать `@LoadBalanced` бин WebClient.Builder/RestTemplate, обращаться по логическому имени сервиса (`http://user-service/...`)**
>
>   Корректный путь. **Шаги:** (1) зависимости `spring-cloud-starter-loadbalancer` + `spring-cloud-starter-netflix-eureka-client`; (2) `@Bean @LoadBalanced public WebClient.Builder ...` — аннотация включает interceptor, который резолвит логическое имя сервиса через Service Discovery; (3) в коде использовать URL вида `http://user-service/api/users/{id}` — `user-service` это имя из Eureka, а не DNS. По умолчанию используется Round Robin. Балансировка происходит на клиенте, без отдельного LB-компонента.
>
>   **Practical:** не забудь поставить `@LoadBalanced` — без неё `WebClient`/`RestTemplate` попытается резолвить `user-service` как DNS-имя и упадёт с `UnknownHostException`.
>
> - [ ] **Прописать список IP-адресов всех инстансов в `application.yml` под ключом `spring.cloud.loadbalancer.servers` и указать алгоритм через `loadbalancer.algorithm: round-robin`**
>
>   Нет такого свойства в Spring Cloud LoadBalancer, и hardcode IP-адресов противоречит самой идее динамической балансировки. Список инстансов берётся из Service Discovery (`Eureka`, `Consul`) или из `ServiceInstanceListSupplier`, а не из конфига. Так балансировщик автоматически узнаёт о новых/упавших инстансах через регистрацию в discovery service.
>
> - [ ] **Добавить `@EnableLoadBalancing` на главный класс приложения и использовать аннотацию `@LoadBalanceClient(name = "user-service")` на каждом методе сервиса**
>
>   Аннотации `@EnableLoadBalancing` в Spring Cloud LoadBalancer не существует (это путаница с устаревшим `@EnableEurekaClient`). Реальная аннотация `@LoadBalancerClient(name = "...")` ставится на конфиг-класс для подмены балансировщика на кастомный, а не на каждый метод. Включение происходит автоматически при подключении starter-а + `@LoadBalanced` на бин клиента.
>
> - [ ] **Заменить `RestTemplate` на `Netflix Ribbon`, добавив `@RibbonClient(name = "user-service")` — это единственный поддерживаемый способ клиентской балансировки в Spring**
>
>   `Netflix Ribbon` устарел и удалён из Spring Cloud 2020.0+ (Ilford release train). Современная замена — `Spring Cloud LoadBalancer`, который и работает с `@LoadBalanced WebClient`/`RestTemplate`. Использование Ribbon в новых проектах = технический долг и проблемы с обновлениями Spring Boot.

## Q18. Как реализовать кастомную стратегию балансировки в Spring Cloud?

`Spring Cloud LoadBalancer` позволяет заменить стандартный `Round Robin` на собственную стратегию:

```java
// Кастомная стратегия: выбор инстанса с минимальным временем ответа
public class LeastResponseTimeLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private final ObjectProvider<ServiceInstanceListSupplier> supplier;
    private final ConcurrentMap<String, AtomicLong> responseTimeMap =
        new ConcurrentHashMap<>();

    public LeastResponseTimeLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        return supplier.getIfAvailable()
            .get(request)
            .next()
            .map(this::selectInstance);
    }

    private Response<ServiceInstance> selectInstance(
            List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            return new EmptyResponse();
        }
        ServiceInstance best = instances.stream()
            .min(Comparator.comparingLong(
                i -> responseTimeMap
                    .getOrDefault(i.getInstanceId(), new AtomicLong(0))
                    .get()))
            .orElse(instances.get(0));

        return new DefaultResponse(best);
    }
}

// Конфигурация для конкретного сервиса
@LoadBalancerClient(
    name = "user-service",
    configuration = UserServiceLBConfig.class
)
public class LoadBalancerConfiguration { }

class UserServiceLBConfig {
    @Bean
    public ReactorServiceInstanceLoadBalancer customLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> supplier) {
        return new LeastResponseTimeLoadBalancer(supplier);
    }
}
```


> [!mcq]
> - [ ] **Достаточно унаследоваться от `RoundRobinLoadBalancer` и переопределить метод `chooseServer()` — Spring автоматически подхватит её через component scan**
>
>   Component scan не подхватывает балансировщик автоматически — нужна явная привязка к конкретному сервису через `@LoadBalancerClient(name = "...", configuration = ...)`. Метод называется `choose(Request)` и возвращает `Mono<Response<ServiceInstance>>`, а не `chooseServer()` (это API устаревшего Netflix Ribbon). Корректный путь — реализовать `ReactorServiceInstanceLoadBalancer`.
>
> - [x] **Реализовать `ReactorServiceInstanceLoadBalancer.choose(Request)`, получить инстансы через `ServiceInstanceListSupplier`, выбрать подходящий и вернуть `Mono<Response<ServiceInstance>>`; зарегистрировать через `@LoadBalancerClient(name, configuration)`**
>
>   Корректный путь. **Шаги:** (1) класс реализует `ReactorServiceInstanceLoadBalancer` с методом `choose(Request) -> Mono<Response<ServiceInstance>>`; (2) через `ObjectProvider<ServiceInstanceListSupplier>` получаешь список живых инстансов из Service Discovery; (3) применяешь свою логику выбора (например, least response time, sticky session по header, локация); (4) возвращаешь `new DefaultResponse(instance)` или `new EmptyResponse()`. Регистрация через `@LoadBalancerClient(name = "user-service", configuration = UserServiceLBConfig.class)` + `@Bean` в этом конфиге — это привязывает кастомный балансировщик ТОЛЬКО к указанному сервису.
>
>   **Practical:** не клади конфиг-класс с балансировщиком в `@ComponentScan` главного приложения — Spring создаст один балансировщик глобально вместо per-service. Конфиг должен быть в отдельном пакете.
>
> - [ ] **Прописать алгоритм в `application.yml`: `spring.cloud.loadbalancer.strategy: custom` и указать FQN класса в `spring.cloud.loadbalancer.class`**
>
>   Таких свойств в Spring Cloud LoadBalancer нет. Кастомная стратегия регистрируется только через Java-конфигурацию с `@LoadBalancerClient` и `@Bean`. YAML-конфиг позволяет переключать готовые `ServiceInstanceListSupplier` (health-check, zone-preference, retry), но не подменять сам алгоритм выбора.
>
> - [ ] **Создать реализацию `IRule` из Netflix Ribbon и зарегистрировать её через `@RibbonClient` — Spring Cloud LoadBalancer построен поверх Ribbon**
>
>   `IRule` — это API устаревшего Netflix Ribbon. Spring Cloud LoadBalancer не построен поверх Ribbon — это его полная замена (Ribbon удалён из Spring Cloud 2020.0+). Использование `IRule`/`@RibbonClient` в новых проектах не работает или работает только через legacy-зависимости. Правильный API — `ReactorServiceInstanceLoadBalancer`.

## Q19. Что такое blue-green и canary в контексте балансировки?

**Blue-green** — два идентичных окружения. Трафик идёт на одно (blue); при деплое поднимается новая версия (green); после проверки балансировщик переключает весь трафик.

**Canary** — часть трафика (1-10%) направляется на новую версию, остальная — на старую. Постепенное увеличение доли.

```mermaid
graph TB
    subgraph "Blue-Green Deploy"
        LB1[Load Balancer] -->|100% трафика| Blue[Blue v1.0]
        LB1 -.->|0% → 100% переключение| Green[Green v2.0]
    end

    subgraph "Canary Deploy"
        LB2[Load Balancer] -->|90%| Stable[Stable v1.0]
        LB2 -->|10%| Canary[Canary v2.0]
    end
```

**Реализация canary в Nginx через weight:**

```nginx
upstream api_backend {
    server stable-v1.example.com:8080 weight=9;   # 90% трафика
    server canary-v2.example.com:8080 weight=1;    # 10% трафика
}
```

**Реализация canary в HAProxy:**

```haproxy
backend api_servers
    balance roundrobin
    server stable1 10.0.0.1:8080 weight 90 check
    server stable2 10.0.0.2:8080 weight 90 check
    server canary1 10.0.0.3:8080 weight 10 check
```

Подробнее о стратегиях деплоя — в [вопросах по стратегиям деплоя](../cicd/deployment-strategies-interview.md).


> [!mcq] В чём ключевое отличие blue-green деплоя от canary в контексте балансировки?
>
> - [ ] A) Blue-green постепенно увеличивает долю трафика на новую версию с 1% до 100%, а canary мгновенно переключает весь трафик
>     - **Что на самом деле:** наоборот. Blue-green — это мгновенное переключение 0%→100% после готовности окружения. Canary — постепенный rollout (1% → 10% → 50% → 100%) с проверкой метрик на каждом шаге.
>     - **Откуда путаница:** оба термина про прогрессивный деплой, и легко запомнить названия, но забыть, какое именно поведение скрыто за каждым.
>     - **Если бы это было правдой:** canary не имел бы смысла как стратегия снижения риска — внезапное переключение всего трафика равноценно blue-green flip.
> - [x] **B) Blue-green держит два идентичных окружения и переключает 100% трафика разом, canary направляет 1-10% трафика на новую версию и постепенно увеличивает долю**
>     - **Развёрнутое объяснение:** Blue-green — два параллельных окружения (blue = текущее, green = новое). Балансировщик в любой момент времени отправляет 100% трафика на одно из них; деплой = переключение указателя. Canary — внутри одного пула одновременно крутятся обе версии (stable + canary), и балансировщик через веса (`weight=9` / `weight=1` в Nginx) делит трафик в выбранной пропорции, постепенно сдвигая её в сторону новой версии.
>     - **Пример:** `upstream { server stable weight=9; server canary weight=1; }` в Nginx даёт 10% на canary. Полный blue-green в Kubernetes — два Deployment с label `version=blue|green` и Service, в котором selector меняется одним `kubectl patch`.
>     - **Когда применять:** blue-green — когда деплой должен быть атомарным и мгновенно откатываемым (банковские транзакции, релизы со схемой БД, требующие full cutover). Canary — когда нужен реальный production-сигнал до полной раскатки (метрики ошибок, latency на маленькой выборке пользователей).
>     - **Подводные камни:** blue-green удваивает потребление ресурсов на время деплоя и требует совместимости БД между версиями; canary требует, чтобы метрики были statistically significant на маленькой выборке, и нужен механизм sticky session (чтобы один и тот же пользователь не «прыгал» между версиями).
>     - **Связанные вопросы:** [[load-balancing-interview#Q7]] про weighted round robin как механизм canary, [[load-balancing-interview#Q21]] про connection draining при переключении, [[load-balancing-interview#Q25]] про zero-downtime деплой.
> - [ ] C) Blue-green делит трафик по геолокации (синий регион = EU, зелёный = US), а canary — по типу устройства
>     - **Что на самом деле:** ни blue-green, ни canary не привязаны к геолокации или устройствам. Это стратегии version rollout, ортогональные географическому роутингу (`Q27` про geographic load balancing).
>     - **Откуда путаница:** цвета «синий/зелёный» вызывают ассоциации с географическими картами, но это просто условные имена двух окружений.
>     - **Если бы это было правдой:** для одно-регионального сервиса blue-green был бы недоступен — но это базовая стратегия деплоя для любого сервиса.
> - [ ] D) Blue-green работает только на L4-балансировщиках, а canary — только на L7 с инспекцией HTTP-заголовков
>     - **Что на самом деле:** обе стратегии реализуются на любом уровне балансировки. Blue-green = переключение upstream-пула (работает и на L4 NLB через target group switch). Canary через weights работает в `HAProxy mode tcp` (L4) точно так же, как в `mode http` (L7).
>     - **Откуда путаница:** L7-роутинг по заголовкам (например, canary только для bucket `internal-users`) — это расширение canary, и оно требует L7. Но базовый weighted canary доступен и на L4.
>     - **Если бы это было правдой:** `AWS NLB` (L4) не поддерживал бы target group weights — но он умеет это с 2019 года.


## Q20. (!) Как обеспечить отказоустойчивость балансировщика?

Балансировщик — single point of failure. Для отказоустойчивости используют пару балансировщиков:

```mermaid
graph TB
    DNS[DNS / Floating IP] --> Active[LB Active]
    DNS -.->|failover| Passive[LB Passive]
    Active --> B1[Backend 1]
    Active --> B2[Backend 2]
    Passive -.-> B1
    Passive -.-> B2

    Active <-->|VRRP heartbeat| Passive
```

**Active-Passive** — один активен, второй в standby. При падении активного — автоматический failover через `VRRP` (Virtual Router Redundancy Protocol) или keepalived.

**Active-Active** — оба принимают трафик (через `DNS Round Robin` или `Anycast`). При падении одного — весь трафик на второй.

**Конфигурация keepalived для VRRP:**

```
vrrp_instance VI_1 {
    state MASTER
    interface eth0
    virtual_router_id 51
    priority 100
    advert_int 1

    authentication {
        auth_type PASS
        auth_pass mysecret
    }

    virtual_ipaddress {
        192.168.1.100/24
    }
}
```

**В облаке:** `AWS ALB`/`NLB` — managed, отказоустойчивость «из коробки» (multi-AZ). `GCP` Global Load Balancer — глобальная балансировка с `Anycast`.


> [!mcq] Как обеспечить отказоустойчивость самого балансировщика, чтобы он не стал single point of failure?
>
> - [ ] A) Поставить балансировщик на самом мощном железе и настроить graceful restart — этого достаточно для HA
>     - **Что на самом деле:** мощное железо не спасает от падения хоста, обновления ядра, сетевой ошибки или сбоя в самом процессе. SPOF = одна машина, и никакая её надёжность не даёт HA на уровне сервиса.
>     - **Откуда путаница:** в небольших инсталляциях балансировщик часто работает «как есть» годами, и кажется, что один экземпляр достаточен.
>     - **Если бы это было правдой:** не существовали бы `keepalived`, `VRRP`, multi-AZ ALB — но это базовые элементы любой production-инсталляции.
> - [ ] B) Использовать только клиентскую балансировку — тогда отдельный балансировщик не нужен и проблема SPOF исчезает
>     - **Что на самом деле:** клиентская балансировка решает проблему SPOF для межсервисных вызовов, но не для входящего трафика (внешний клиент не подключён к Service Discovery). Для north-south трафика балансировщик нужен в любом случае.
>     - **Откуда путаница:** микросервисные команды действительно отказываются от центрального LB между сервисами в пользу `Spring Cloud LoadBalancer`/Istio, но внешний LB остаётся.
>     - **Если бы это было правдой:** не нужны были бы `AWS ALB`, `Cloudflare`, `Nginx Ingress` — но они стандарт для входного трафика.
> - [x] **C) Развернуть пару балансировщиков в схеме Active-Passive с VRRP/keepalived (общий floating IP) или Active-Active через DNS Round Robin / Anycast; в облаке использовать managed multi-AZ LB**
>     - **Развёрнутое объяснение:** Active-Passive — два экземпляра LB, один активен, второй в standby и принимает виртуальный `IP` через `VRRP` (`keepalived`) при падении мастера; failover за секунды. Active-Active — оба балансировщика принимают трафик одновременно через `DNS Round Robin` (несколько A-записей) или `Anycast` (один IP анонсируется из обоих узлов через BGP). Managed-варианты (`AWS ALB`, `GCP HTTP(S) LB`) уже HA «из коробки» благодаря multi-AZ архитектуре.
>     - **Пример:** `vrrp_instance VI_1 { state MASTER; virtual_ipaddress { 192.168.1.100 } }` в `keepalived` поднимает floating IP, который мигрирует при падении мастера. В `AWS` ALB с `subnets = [subnet-az-a, subnet-az-b, subnet-az-c]` автоматически распределяется по трём AZ.
>     - **Когда применять:** Active-Passive — для on-prem с двумя физическими узлами (простая схема, понятный failover). Active-Active — когда нужно горизонтальное масштабирование самого LB (миллионы RPS). Managed multi-AZ — облако, не хочется управлять keepalived.
>     - **Подводные камни:** VRRP требует общего L2-сегмента (не работает между AZ облака без overlay). DNS Round Robin страдает от TTL-кеширования клиентов (failover за минуты, не секунды). Anycast требует BGP и контроля над сетью — не доступен в типичной аренде VPS.
>     - **Связанные вопросы:** [[load-balancing-interview#Q21]] про connection draining (тоже про zero-downtime), [[load-balancing-interview#Q39]] про Anycast, [[load-balancing-interview#Q26]] про DNS-based LB.
> - [ ] D) Запустить балансировщик внутри Kubernetes Pod с `restartPolicy: Always` — kubelet поднимет его при падении
>     - **Что на самом деле:** kubelet перезапустит контейнер за десятки секунд, но в это время трафик не обрабатывается. Это не HA, а recovery. Плюс kubelet не помогает при падении ноды.
>     - **Откуда путаница:** `restartPolicy: Always` ассоциируется с надёжностью, но это recovery-механизм, а не HA.
>     - **Если бы это было правдой:** Kubernetes не нуждался бы в `Service` с типом `LoadBalancer` для входящего трафика — но это базовый объект для HA Ingress.


## Q21. Что такое connection draining (deregistration delay)?

`Connection draining` — при исключении узла из пула балансировщик не направляет новые запросы, но даёт завершиться уже установленным соединениям в течение заданного времени.

```mermaid
sequenceDiagram
    participant LB as Load Balancer
    participant S as Server (draining)
    participant C as Клиент

    Note over LB,S: Сервер выводится из пула
    LB->>S: Прекращение новых запросов
    C->>S: Существующие запросы продолжают работу
    S->>C: Ответы на текущие запросы
    Note over S: Таймаут draining (30с)
    Note over S: Сервер останавливается
```

**Реализация в Kubernetes:**

```yaml
spec:
  terminationGracePeriodSeconds: 60  # время на завершение
  containers:
    - name: app
      lifecycle:
        preStop:
          exec:
            command: ["sh", "-c", "sleep 15"]  # дать время на deregistration
```

**В AWS ALB:** параметр `deregistration_delay.timeout_seconds` (по умолчанию 300 секунд).

**В Nginx:** при reload конфигурации старые worker-процессы продолжают обслуживать существующие соединения, пока новые workers обрабатывают новые запросы. Параметр `worker_shutdown_timeout` контролирует максимальное время.


> [!mcq] Что произойдёт с трафиком, когда узел исключается из пула при включённом `connection draining` (deregistration delay = 60s)?
>
> - [x] **A) Балансировщик перестаёт направлять новые запросы на узел, но даёт завершиться уже установленным соединениям в течение 60 секунд, после чего узел останавливается**
>     - **Развёрнутое объяснение:** при исключении из пула узел переходит в состояние `draining`. Балансировщик помечает его как `out of service` для новых запросов, но не разрывает существующие TCP-соединения. Через `deregistration_delay.timeout_seconds` (`AWS ALB`), `worker_shutdown_timeout` (`Nginx`) или `terminationGracePeriodSeconds` (Kubernetes) узел реально отключается.
>     - **Пример:** в Kubernetes `terminationGracePeriodSeconds: 60` + `preStop: sleep 15` даёт следующую последовательность: SIGTERM → preStop ждёт 15s (пока kube-proxy обновит iptables) → endpoint исключается из Service → существующие запросы достраиваются → SIGKILL через 60s. В `AWS ALB` параметр `deregistration_delay.timeout_seconds = 300` по умолчанию.
>     - **Когда применять:** ВСЕГДА при rolling deployment, scale-down, ротации узлов. Без draining новые соединения «обрываются» в момент удаления, и клиенты видят 502/503.
>     - **Подводные камни:** длинные WebSocket/SSE-соединения могут не завершиться за timeout — нужен либо больший grace period, либо graceful shutdown в самом приложении (отправить close-frame). Для batch-обработчиков с long polling timeout должен быть больше типичной длины запроса.
>     - **Связанные вопросы:** [[load-balancing-interview#Q20]] про отказоустойчивость LB, [[load-balancing-interview#Q25]] про zero-downtime деплой, [[load-balancing-interview#Q31]] про WebSocket-балансировку.
> - [ ] B) Балансировщик мгновенно сбрасывает все соединения с узлом, отправляя `RST` клиентам, чтобы не задерживать вывод узла
>     - **Что на самом деле:** это поведение БЕЗ draining. Цель draining — именно избежать обрыва существующих соединений, дав запросам завершиться нормально (200 OK), а не получить `connection reset`.
>     - **Откуда путаница:** при отсутствии конфигурации `deregistration_delay` некоторые балансировщики действительно режут соединения сразу — и этот сценарий ошибочно считают нормой.
>     - **Если бы это было правдой:** rolling update в Kubernetes был бы с гарантией 502 на каждом перевыпуске пода — но обычно zero-downtime достижим именно благодаря draining.
> - [ ] C) Балансировщик буферизует новые запросы и держит их в очереди 60 секунд, потом переадресует на оставшиеся узлы
>     - **Что на самом деле:** новые запросы не буферизуются, а сразу маршрутизируются на другие активные узлы пула. Draining касается только уже установленных соединений к выходящему узлу.
>     - **Откуда путаница:** есть отдельная концепция `request queueing` (`HAProxy` `maxconn` + queue), и её путают с draining.
>     - **Если бы это было правдой:** при scale-down у пользователей появлялась бы латентность ~60s — но скейлинг для них прозрачен.
> - [ ] D) Узел продолжает принимать новые запросы, но балансировщик помечает их специальным заголовком `X-Draining: true` для логирования
>     - **Что на самом деле:** суть draining — НЕ направлять новые запросы. Никаких заголовков балансировщик не добавляет.
>     - **Откуда путаница:** в health-check ответах сами приложения иногда возвращают `503` с body вроде `"draining"`, и это смешивают с поведением LB.
>     - **Если бы это было правдой:** draining ничем не отличался бы от обычной работы — но смысл именно в исключении из ротации.


## Q22. Что такое Layer 4 и Layer 7 load balancer в облаке?

Облачные провайдеры предоставляют managed балансировщики обоих уровней:

| Провайдер | L4 | L7 |
|-----------|----|----|
| AWS | `NLB` (Network Load Balancer) | `ALB` (Application Load Balancer) |
| GCP | Network Load Balancer | HTTP(S) Load Balancer |
| Azure | Azure Load Balancer | Application Gateway |

**AWS NLB (L4):**
- Миллионы запросов в секунду, ультранизкая латентность
- Static IP / Elastic IP
- Подходит для `TCP`/`UDP`, `gRPC`, не-HTTP протоколы

**AWS ALB (L7):**
- Path-based и host-based routing
- `TLS` termination через `ACM` (бесплатные сертификаты)
- Интеграция с `WAF`, `Cognito`
- Поддержка `WebSocket`, `HTTP/2`

**Выбор:** L4 для не-HTTP протоколов и минимальной латентности. L7 для HTTP/HTTPS с маршрутизацией по содержимому. Подробнее об облачной инфраструктуре — в [паттернах масштабирования](scalability-patterns-interview.md).


> [!mcq] Какой managed-балансировщик выбрать в AWS для распределения `gRPC` трафика с минимальной латентностью?
>
> - [ ] A) AWS ALB (Application Load Balancer) — он L7 и поэтому всегда быстрее и универсальнее NLB
>     - **Что на самом деле:** L7 всегда медленнее L4 из-за парсинга `HTTP`. `ALB` поддерживает `gRPC` (через `HTTP/2`), но даёт более высокую латентность, чем `NLB`. Если важна минимальная задержка и не нужны L7-фичи (path-routing, WAF, header-based routing) — `NLB` подходит лучше.
>     - **Откуда путаница:** «более новый» и «L7» часто ассоциируется с «лучше», но это разные задачи.
>     - **Если бы это было правдой:** `AWS NLB` не рекламировался бы как «ultra-low latency, millions of requests per second» — но это его официальная позиция.
> - [x] **B) AWS NLB (Network Load Balancer) для минимальной латентности с static IP; ALB — если нужны path-based routing, gRPC method routing или WAF на уровне балансировщика**
>     - **Развёрнутое объяснение:** `NLB` — L4-балансировщик, не парсит `HTTP/2`, обрабатывает пакеты ближе к kernel-space, даёт миллионы RPS с задержкой в микросекунды и поддерживает static `IP`/`Elastic IP`. Для `gRPC` без необходимости L7-роутинга (например, балансировка между подами одного сервиса) `NLB` оптимален. `ALB` нужен, когда `gRPC` методы из разных сервисов идут на одном порту и их нужно роутить по `path` (например, `/users.UserService/*` → user-pods, `/orders.OrderService/*` → order-pods).
>     - **Пример:** Kubernetes `Service` с `type: LoadBalancer` и аннотацией `service.beta.kubernetes.io/aws-load-balancer-type: nlb` создаст `NLB` для internal gRPC между сервисами. `AWS ALB Ingress Controller` с `target-type: ip` нужен, если хочется L7-роутинг gRPC по methods.
>     - **Когда применять:** `NLB` — `gRPC`/`TCP`/`UDP`, статический IP для allowlist клиента, минимальная latency. `ALB` — `HTTP`/`HTTPS`/`gRPC` с маршрутизацией по содержимому, интеграция с `Cognito`/`WAF`, `WebSocket`.
>     - **Подводные камни:** `NLB` не делает `TLS termination` для гRPC до недавнего времени (теперь делает через `ACM`); `NLB` пропускает client IP через `proxy protocol v2` — приложение должно уметь его распарсить. `ALB` берёт ~$0.0225/час + LCU — обычно дороже, чем `NLB` под высокой нагрузкой.
>     - **Связанные вопросы:** [[load-balancing-interview#Q1]] про L4 vs L7, [[load-balancing-interview#Q30]] про балансировку gRPC, [[load-balancing-interview#Q24]] про Kubernetes Ingress.
> - [ ] C) AWS Classic Load Balancer — он поддерживает оба уровня и универсален для любого трафика
>     - **Что на самом деле:** `Classic Load Balancer` (CLB) — устаревший продукт, AWS не рекомендует его для новых инсталляций. `gRPC` он не поддерживает (нет `HTTP/2` end-to-end). Современный выбор — `ALB` (L7) или `NLB` (L4).
>     - **Откуда путаница:** старая документация и legacy CloudFormation-шаблоны часто содержат `CLB`.
>     - **Если бы это было правдой:** AWS не релизил бы `ALB`/`NLB` как отдельные продукты — но это разделение существует именно потому, что универсальный CLB проигрывал по производительности и фичам.
> - [ ] D) AWS API Gateway — это единственный сервис, поддерживающий `gRPC` балансировку в AWS
>     - **Что на самом деле:** `API Gateway` (REST/HTTP API) НЕ поддерживает `gRPC` нативно. `gRPC` идёт через `ALB`/`NLB`/`App Mesh`. API Gateway — для REST/HTTP с трансформацией, throttling, API keys, но не для бинарного `HTTP/2` трафика.
>     - **Откуда путаница:** API Gateway — общеупотребительный термин, и кажется, что он умеет всё.
>     - **Если бы это было правдой:** `AWS App Mesh` и интеграция `ALB` с `gRPC` (2020 год) не разрабатывались бы — но это базовые продукты для gRPC.


## Q23. (!) Как настроить health check в Kubernetes для Pod?

В [Kubernetes](../devops/kubernetes-interview.md) три типа проб:

- **`startupProbe`** — проверка при запуске. Пока не пройдёт, liveness и readiness не запускаются. Для приложений с долгим стартом.
- **`livenessProbe`** — приложение живо? При провале — перезапуск контейнера.
- **`readinessProbe`** — приложение готово принимать трафик? При провале — исключение из `Service`.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-service
spec:
  replicas: 3
  template:
    spec:
      containers:
        - name: api
          image: api-service:2.1.0
          ports:
            - containerPort: 8080
          startupProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            failureThreshold: 30
            periodSeconds: 2
            # Макс. ожидание старта: 30 * 2 = 60с
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            periodSeconds: 10
            failureThreshold: 3
            timeoutSeconds: 3
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            periodSeconds: 5
            failureThreshold: 2
            timeoutSeconds: 3
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "500m"
```

**Spring Boot Actuator** конфигурация для Kubernetes проб:

```yaml
# application.yml
management:
  endpoint:
    health:
      probes:
        enabled: true
      group:
        readiness:
          include: db, redis, kafka
        liveness:
          include: ping
  health:
    livenessState:
      enabled: true
    readinessState:
      enabled: true
```

**Важно:** `livenessProbe` не должна зависеть от внешних сервисов (БД, Redis) — иначе при падении БД все поды будут перезапущены. Внешние зависимости проверяет только `readinessProbe`.


> [!mcq] Почему `livenessProbe` не должна проверять внешние зависимости (БД, Redis), а `readinessProbe` — может?
>
> - [ ] A) `livenessProbe` запускается реже, чем `readinessProbe`, поэтому проверки внешних сервисов в ней неэффективны
>     - **Что на самом деле:** дело не в частоте, а в последствиях провала. `livenessProbe` запускает рестарт контейнера, `readinessProbe` — исключение из `Service` endpoints. Проверка БД в liveness приведёт к катастрофическому каскаду перезапусков.
>     - **Откуда путаница:** действительно `periodSeconds` для liveness обычно выше (10s) против readiness (5s), но это следствие, а не причина.
>     - **Если бы это было правдой:** документация Kubernetes не подчёркивала бы именно семантическую разницу проб, а просто увеличивала бы `periodSeconds`.
> - [ ] B) `livenessProbe` работает только с TCP/Exec проверками, а readiness — единственная, которая умеет HTTP
>     - **Что на самом деле:** обе пробы поддерживают `httpGet`, `tcpSocket`, `exec`, `grpc`. Технических ограничений на тип проверки нет — есть только семантические рекомендации.
>     - **Откуда путаница:** в простых примерах liveness часто делают TCP-проверкой порта, а readiness — HTTP-эндпоинтом, и это закрепляется как правило.
>     - **Если бы это было правдой:** в Kubernetes API спецификации проб различались бы — но они идентичны.
> - [x] **C) Провал `livenessProbe` ведёт к рестарту Pod, и если БД временно недоступна — все поды одновременно начнут перезапускаться, создавая каскад отказа; `readinessProbe` лишь исключает Pod из Service endpoints, давая мягкую деградацию**
>     - **Развёрнутое объяснение:** `livenessProbe` отвечает на вопрос «жив ли процесс?» — провал значит, что приложение в неконсистентном состоянии (deadlock, OOM-зависание) и его нужно рестартить. Если поставить туда проверку БД — кратковременная недоступность БД (failover мастера на 30s) убьёт ВСЕ поды одновременно, и при восстановлении БД они снова не пройдут проверку (стартующее приложение пытается коннектиться → fail → SIGKILL), формируя crash-loop. `readinessProbe` отвечает «готов ли Pod принимать трафик?» — провал убирает endpoint из `Service`, балансировщик не шлёт запросы, но Pod остаётся живым и сам восстановится, когда БД вернётся.
>     - **Пример:** `livenessProbe: httpGet /actuator/health/liveness` в Spring Boot, где `liveness-state: ping` (без БД); `readinessProbe: httpGet /actuator/health/readiness` с `group.readiness.include: db, redis, kafka` — провалится при сбое БД и Pod исключится из ротации до восстановления зависимостей.
>     - **Когда применять:** liveness — для детекции deadlock'ов внутри JVM/runtime приложения; readiness — для контроля участия в трафике с учётом всех зависимостей. Между ними `startupProbe` — для медленно стартующих приложений (Java/JVM warm-up).
>     - **Подводные камни:** не путать `liveness` и `startup`: для медленного старта используй `startupProbe`, иначе liveness убьёт Pod до того, как приложение успеет инициализироваться. Если приложение в OOM-pause, readiness тоже провалится — но этого мало для рестарта, нужен именно liveness (или JVM `-XX:+ExitOnOutOfMemoryError`).
>     - **Связанные вопросы:** [[load-balancing-interview#Q3]] про health checks вообще, [[load-balancing-interview#Q24]] про Ingress и Service endpoints, [[load-balancing-interview#Q25]] про zero-downtime.
> - [ ] D) Это устаревшая рекомендация — современные версии Kubernetes 1.20+ автоматически защищают liveness от каскадных рестартов через rate limiting
>     - **Что на самом деле:** в Kubernetes нет механизма rate-limit на liveness рестарты (есть `failureThreshold` и backoff между рестартами одного контейнера, но это не защищает от одновременного провала всех реплик). Рекомендация по сей день актуальна.
>     - **Откуда путаница:** Kubernetes действительно добавляет фичи, и кажется, что любая «гигиена» уже встроена.
>     - **Если бы это было правдой:** официальная документация Kubernetes 1.30+ убрала бы предупреждение о внешних зависимостях в liveness — но оно там по-прежнему.


## Q24. Что такое Ingress и как он связан с балансировкой?

`Ingress` — объект Kubernetes для маршрутизации внешнего HTTP/HTTPS трафика в `Service`. Контроллер (`Nginx Ingress Controller`, `Traefik`, `AWS ALB Ingress Controller`) реализует правила маршрутизации.

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: api-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/proxy-body-size: "10m"
    nginx.ingress.kubernetes.io/rate-limit: "100"
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - api.example.com
      secretName: api-tls
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /api/users
            pathType: Prefix
            backend:
              service:
                name: user-service
                port:
                  number: 8080
          - path: /api/orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 8080
          - path: /
            pathType: Prefix
            backend:
              service:
                name: frontend
                port:
                  number: 80
```

**Цепочка:** Клиент → DNS → `Ingress Controller` (L7 LB) → `Service` → `Pod`. `Service` распределяет трафик между подами через `kube-proxy` / iptables.


> [!mcq] Какую роль играет `Ingress` в Kubernetes по отношению к балансировке нагрузки?
>
> - [ ] A) `Ingress` — это L4-балансировщик, заменяющий внешний `AWS NLB` для внутреннего трафика кластера
>     - **Что на самом деле:** `Ingress` — это L7-объект, оперирующий HTTP/HTTPS-маршрутизацией по `host` и `path`. Для L4 (TCP/UDP) используется `Service` типа `LoadBalancer` или `NodePort`. И `Ingress` обычно работает в паре с внешним L4-LB (NLB) для приёма трафика.
>     - **Откуда путаница:** оба — точки входа в кластер, и легко спутать их роли.
>     - **Если бы это было правдой:** в Ingress-спецификации не было бы `paths`, `tls`, `host` — но это его ключевые поля.
> - [ ] B) `Ingress` — это сам Pod с `Nginx`, который нужно запускать с `hostNetwork: true` для приёма трафика
>     - **Что на самом деле:** `Ingress` — это объект API (декларативные правила), а его исполнитель — `Ingress Controller` (отдельный Deployment с Nginx/Traefik/HAProxy). Хотя в bare-metal действительно иногда запускают контроллер с `hostNetwork`, это деталь установки, а не суть Ingress.
>     - **Откуда путаница:** в туториалах часто показывают именно установку контроллера, а правила Ingress остаются на втором плане.
>     - **Если бы это было правдой:** не имело бы смысла иметь несколько Ingress-объектов в одном namespace — но это базовый паттерн.
> - [x] **C) `Ingress` — декларативный API-объект Kubernetes, описывающий правила L7-роутинга по `host`/`path`, который реализует `Ingress Controller` (Nginx, Traefik, HAProxy); сам контроллер слушает внешний LB и распределяет трафик в Service → Pod**
>     - **Развёрнутое объяснение:** `Ingress` — это YAML-манифест с правилами вроде «`api.example.com/api/users` → `Service user-service:8080`». Контроллер (`Ingress Controller`) — это Deployment с прокси (Nginx/Traefik), который смотрит на эти манифесты через Watch API и рендерит свою конфигурацию. Внешний трафик приходит на L4-LB (`AWS NLB`/`MetalLB`/`NodePort`), оттуда — на контроллер, контроллер выполняет L7-роутинг, выбирает целевой `Service`, а `kube-proxy`/`iptables` доставляет пакет до одного из эндпоинтов (Pod).
>     - **Пример:** в `Ingress` указываешь `path: /api/users → backend: service: name: user-service`, в кластере крутится `nginx-ingress-controller`, на нём аннотация `kubernetes.io/ingress.class: nginx`; внешний `AWS NLB` слушает порты 80/443 контроллера. TLS-секрет `api-tls` (`secretName`) подсовывается в Nginx через Kubernetes Secret.
>     - **Когда применять:** для HTTP/HTTPS трафика, требующего path-based и host-based routing, TLS termination в кластере, rate limiting через аннотации контроллера. Для TCP/UDP (БД, не-HTTP протоколы) — `Service: LoadBalancer` напрямую.
>     - **Подводные камни:** Ingress API имеет ограниченную выразительность (нет header-routing в стандарте, нет canary через weights без аннотаций) — для сложных сценариев используют `Gateway API` (новый объект K8s) или `Istio VirtualService`. Аннотации контроллер-специфичны: `nginx.ingress.kubernetes.io/rewrite-target` не работает в Traefik.
>     - **Связанные вопросы:** [[load-balancing-interview#Q1]] про L7, [[load-balancing-interview#Q22]] про облачные LB, [[load-balancing-interview#Q23]] про probes (тоже про Service endpoints).
> - [ ] D) `Ingress` устарел в Kubernetes 1.25+ и заменён на `IngressGateway` из service mesh (Istio) — других вариантов нет
>     - **Что на самом деле:** `Ingress` поддерживается как стабильный API (`networking.k8s.io/v1`). Есть новый `Gateway API`, который призван заменить Ingress на дистанции, но Ingress не deprecated. `IngressGateway` Istio — это надстройка над Envoy, не часть Kubernetes API.
>     - **Откуда путаница:** активная разработка `Gateway API` создаёт ощущение «всё, старое мертво», но миграция — на годы.
>     - **Если бы это было правдой:** kubectl explain ingress показывал бы deprecation warning — но не показывает.


## Q25. Как обеспечить zero-downtime при деплое за балансировщиком?

Ключевые элементы zero-downtime деплоя:

1. **Rolling Update** — новые поды поднимаются, старые удаляются постепенно
2. **Readiness Probe** — под получает трафик только когда готов
3. **Connection Draining** — текущие запросы завершаются до остановки
4. **PreStop hook** — задержка перед остановкой для deregistration

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-service
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1         # максимум 1 лишний под
      maxUnavailable: 0   # всегда доступны все реплики
  template:
    spec:
      terminationGracePeriodSeconds: 60
      containers:
        - name: api
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            periodSeconds: 5
          lifecycle:
            preStop:
              exec:
                # Ждём, пока kube-proxy обновит iptables
                command: ["sh", "-c", "sleep 15"]
```

**Порядок остановки пода:**
1. Pod получает `SIGTERM`
2. `preStop` hook выполняется (sleep 15s)
3. Pod исключается из `Service` endpoints
4. Существующие запросы завершаются
5. По истечении `terminationGracePeriodSeconds` — `SIGKILL`

Подробнее — в [вопросах по стратегиям деплоя](../cicd/deployment-strategies-interview.md).


> [!mcq] Что нужно настроить вместе, чтобы rolling update в Kubernetes происходил без обрыва клиентских соединений (zero-downtime)?
>
> - [ ] A) Достаточно `strategy.type: RollingUpdate` — kubelet и kube-proxy всё остальное сделают автоматически
>     - **Что на самом деле:** без `readinessProbe` новый Pod получит трафик сразу, ещё не успев прогреть JVM/коннекшн-пулы — клиенты увидят 500. Без `preStop`-задержки и `terminationGracePeriodSeconds` старый Pod исключится из endpoints одновременно с тем, как kube-proxy ещё не пересобрал iptables, и часть запросов уйдёт «в никуда».
>     - **Откуда путаница:** RollingUpdate — деплой-стратегия, и интуитивно кажется, что в ней всё zero-downtime «встроено».
>     - **Если бы это было правдой:** Kubernetes-документация не настаивала бы на комбинации probes + preStop — но это явная рекомендация.
> - [ ] B) Установить `maxUnavailable: 50%` и `maxSurge: 0` — это даёт быстрый rollout без лишних ресурсов
>     - **Что на самом деле:** `maxUnavailable: 50%` означает, что половина подов может быть недоступна одновременно — это уже не zero-downtime, а graceful degradation. Для zero-downtime: `maxUnavailable: 0` (всегда доступны все реплики) + `maxSurge: 1` (поднимаем один лишний для замены).
>     - **Откуда путаница:** `maxUnavailable: 50%` действительно ускоряет rollout, но за счёт SLA.
>     - **Если бы это было правдой:** дефолтные значения `maxUnavailable: 25%, maxSurge: 25%` не существовали бы как «средняя» позиция — и явное `maxUnavailable: 0` не упоминалось бы в HA-гайдах.
> - [x] **C) `strategy: RollingUpdate` с `maxUnavailable: 0` + `readinessProbe` для контроля участия в трафике + `preStop: sleep 15` + `terminationGracePeriodSeconds: 60` для draining**
>     - **Развёрнутое объяснение:** zero-downtime требует обоих концов: (а) новые Pod не получают трафик до готовности — это `readinessProbe`; (б) старые Pod не теряют трафик резко — это `preStop` + `terminationGracePeriodSeconds`. `maxUnavailable: 0` гарантирует, что в любой момент live-реплик не меньше, чем `replicas`. `maxSurge: 1` поднимает дополнительный Pod, ждёт его `Ready`, потом удаляет один старый. При SIGTERM старого Pod выполняется `preStop` (sleep 15 — чтобы kube-proxy успел обновить iptables на всех нодах), endpoint исключается из Service, существующие соединения достраиваются в течение `terminationGracePeriodSeconds`, после чего SIGKILL.
>     - **Пример:** Deployment с `maxSurge: 1, maxUnavailable: 0`, `readinessProbe: httpGet /actuator/health/readiness periodSeconds: 5`, `lifecycle.preStop.exec.command: ["sh","-c","sleep 15"]`, `terminationGracePeriodSeconds: 60`. На уровне приложения — graceful shutdown в Spring (`server.shutdown: graceful`).
>     - **Когда применять:** для всех stateless-сервисов с внешним трафиком. Для stateful (`StatefulSet` БД) — другая стратегия (`OnDelete`/`partition`).
>     - **Подводные камни:** `preStop sleep` нужен, потому что исключение из endpoints — асинхронный процесс (Endpoints controller → kube-proxy на всех нодах → iptables update). 15s — эмпирическое число, в больших кластерах может потребоваться 30s. Если у приложения долгие WebSocket-соединения, `terminationGracePeriodSeconds` нужно увеличивать до их типичной длительности или встраивать «мягкое» отключение в приложение.
>     - **Связанные вопросы:** [[load-balancing-interview#Q21]] про connection draining, [[load-balancing-interview#Q23]] про probes, [[load-balancing-interview#Q19]] про blue-green/canary (альтернативные стратегии).
> - [ ] D) Использовать только blue-green деплой — RollingUpdate в Kubernetes не даёт реального zero-downtime
>     - **Что на самом деле:** RollingUpdate с правильной настройкой даёт zero-downtime для stateless приложений. Blue-green — альтернатива с другим балансом (атомарное переключение vs прогрессивное), но не «единственный путь».
>     - **Откуда путаница:** blue-green воспринимается как «надёжный» из-за полного дублирования окружения.
>     - **Если бы это было правдой:** RollingUpdate не был бы default-стратегией Deployment — но он именно такой.


## Q26. (!) Что такое DNS-based load balancing?

`DNS-based load balancing` — распределение трафика через DNS-записи. DNS-сервер возвращает разные IP-адреса для одного доменного имени.

```mermaid
graph TB
    Client[Клиент] -->|DNS запрос: api.example.com| DNS[DNS Server]
    DNS -->|ответ: 10.0.0.1| Client
    DNS -->|ответ: 10.0.0.2| Client2[Другой клиент]
    DNS -->|ответ: 10.0.0.3| Client3[Третий клиент]

    Client --> S1[Server 10.0.0.1]
    Client2 --> S2[Server 10.0.0.2]
    Client3 --> S3[Server 10.0.0.3]
```

**Методы DNS-балансировки:**

| Метод | Описание | Пример |
|-------|----------|--------|
| Round Robin DNS | Циклический возврат IP | Несколько A-записей |
| Weighted DNS | Вес для каждой записи | `Route 53` weighted routing |
| Geolocation DNS | По географии клиента | `Route 53` geolocation |
| Latency-based DNS | По минимальной латентности | `Route 53` latency routing |
| Failover DNS | Переключение при отказе | `Route 53` failover |

**Плюсы:**
- Нет единой точки отказа
- Глобальное распределение нагрузки
- Работает до L4/L7 балансировщика

**Минусы:**
- Кэширование DNS (TTL) — медленная реакция на изменения
- Нет учёта текущей нагрузки серверов
- Клиент может кэшировать IP и игнорировать TTL

**Пример конфигурации AWS Route 53 (weighted):**

```
# A-запись с весом 70 (основной регион)
api.example.com  A  10.0.0.1  TTL=60  Weight=70  SetId=primary

# A-запись с весом 30 (вторичный регион)  
api.example.com  A  10.0.1.1  TTL=60  Weight=30  SetId=secondary
```

**На практике** DNS-балансировку используют в комбинации с L4/L7: DNS направляет трафик на ближайший регион, а внутри региона L7-балансировщик распределяет между серверами.


> [!mcq] Какой принципиальный недостаток DNS-based load balancing относительно L4/L7 LB?
>
> - [x] **A) DNS-ответы кешируются клиентами и резолверами по TTL — реакция на падение узла занимает минуты, и DNS не знает текущей нагрузки на серверы**
>     - **Развёрнутое объяснение:** DNS возвращает `IP`-адреса с указанным TTL (типично 60-300s). Клиент или его resolver кеширует ответ, и до истечения TTL продолжает ходить на «мёртвый» IP. Сам DNS-сервер не получает feedback'а от серверов — он не знает, кто перегружен, а кто свободен; он лишь раздаёт IP по статическому правилу (round-robin/weighted/geo). Поэтому failover в DNS — это минуты, а не секунды как у L4/L7 LB с health checks.
>     - **Пример:** `Route 53` с TTL=60s, failover policy — узел падает в 12:00:00, health check заметит в 12:00:30, DNS-record обновится в 12:00:35. Клиенты с TTL=60 будут ходить на мёртвый IP до 12:01:00, плюс многие OS-resolver-ы агрессивно кешируют дольше TTL (Java JVM по умолчанию вообще forever).
>     - **Когда применять:** на верхнем уровне глобальной инфраструктуры (gateway-region → конкретный регион), как первичный гео-роутинг. Внутри региона — обычный L4/L7 LB с быстрым failover.
>     - **Подводные камни:** Java cache TTL для DNS по умолчанию `networkaddress.cache.ttl = -1` (forever) — нужно явно ставить в `java.security` или ENV. Браузеры кешируют DNS отдельно от OS. CDN-провайдеры (Cloudflare, Fastly) используют DNS + Anycast комбинированно, чтобы обойти TTL-проблему.
>     - **Связанные вопросы:** [[load-balancing-interview#Q20]] про HA балансировщика, [[load-balancing-interview#Q27]] про geographic load balancing, [[load-balancing-interview#Q39]] про Anycast как альтернативу.
> - [ ] B) DNS не поддерживает HTTPS-трафик, поэтому DNS LB подходит только для нешифрованного HTTP
>     - **Что на самом деле:** DNS возвращает IP-адреса, и протокол поверх (HTTP, HTTPS, gRPC, что угодно) не имеет значения. DNS — это резолюция имени в адрес, она происходит до TLS handshake.
>     - **Откуда путаница:** новый `DNS-over-HTTPS` (DoH) шифрует САМ DNS-трафик, и это путают с тем, что DNS-LB якобы «не умеет HTTPS».
>     - **Если бы это было правдой:** `api.example.com` через HTTPS вообще не работал бы с DNS Round Robin — но это работает на любом сайте.
> - [ ] C) DNS-LB работает только в одной подсети — нельзя распределить трафик между разными VPC или дата-центрами
>     - **Что на самом деле:** DNS работает поверх любых сетей — у него глобальная adressация. Это его главное преимущество для гео-балансировки между датацентрами, регионами, облаками.
>     - **Откуда путаница:** L4-балансировщики действительно ограничены VPC, и эту особенность ошибочно переносят на DNS.
>     - **Если бы это было правдой:** `Route 53 latency routing` между регионами не существовал бы — но это базовая фича.
> - [ ] D) DNS-LB требует поддержки `EDNS Client Subnet` от клиентов, а большинство ОС его не поддерживает
>     - **Что на самом деле:** ECS — это расширение, помогающее CDN определить локацию клиента, а не обязательное условие DNS-LB. Базовый round-robin/weighted DNS работает без ECS.
>     - **Откуда путаница:** ECS обсуждается в контексте `geo-DNS` точности, и его роль преувеличивают.
>     - **Если бы это было правдой:** DNS RR не работал бы для большинства интернета — но это базовая техника с 1990-х.


## Q27. Что такое geographic load balancing (global load balancing)?

`Geographic load balancing` — направление пользователей на ближайший дата-центр через `DNS` или `Anycast` IP.

```mermaid
graph TB
    EU[Пользователь EU] -->|DNS geolocation| LB_EU[LB Европа]
    US[Пользователь US] -->|DNS geolocation| LB_US[LB Америка]
    ASIA[Пользователь Asia] -->|DNS geolocation| LB_ASIA[LB Азия]

    LB_EU --> DC_EU[DC Франкфурт]
    LB_US --> DC_US[DC Вирджиния]
    LB_ASIA --> DC_ASIA[DC Токио]

    DC_EU <-.->|репликация данных| DC_US
    DC_US <-.->|репликация данных| DC_ASIA
```

**Реализации:**
- **AWS Route 53** — geolocation и latency-based routing
- **Cloudflare** — `Anycast` + geo-routing
- **GCP Global Load Balancer** — единый `Anycast` IP, автоматическая гео-маршрутизация

**Ключевые проблемы:**
- Синхронизация данных между регионами (eventual consistency) — см. [паттерны согласованности](consistency-patterns-interview.md)
- Failover при падении целого региона
- Compliance (GDPR — данные EU-пользователей в EU)


> [!mcq] Что является главным compliance-риском geographic load balancing для глобального API?
>
> - [ ] A) Высокая latency для пользователей из малых стран без локального дата-центра — это нарушает SLA по производительности
>     - **Что на самом деле:** это вопрос производительности, а не compliance. Compliance — это про законы (GDPR, локализация данных), а не про SLO.
>     - **Откуда путаница:** latency и compliance оба обсуждаются как «требования», но это разные домены.
>     - **Если бы это было правдой:** geo-LB вообще не выпускался бы для глобальных сервисов, так как latency для удалённых пользователей неизбежна — но именно поэтому он и применяется.
> - [x] **B) Данные пользователей могут оказаться в дата-центре в стране с другим режимом защиты персональных данных, что нарушает GDPR/local data residency требования; нужна eventual consistency между регионами и compliance-aware routing**
>     - **Развёрнутое объяснение:** geographic LB направляет пользователя в ближайший дата-центр, но «ближайший» может быть не «правильным» с точки зрения compliance. GDPR требует, чтобы данные граждан EU обрабатывались и хранились в EU (или у партнёров с adequacy decision). Если EU-пользователь по ошибке маршрутизации попадёт в US-DC и там запишется его персональные данные, это нарушение. Дополнительно: eventual consistency между регионами создаёт окно, когда обновление пользователя в EU ещё не реплицировалось в US, и пользователь в роуминге видит старые данные.
>     - **Пример:** `Route 53 geolocation routing` с правилами «`continent EU` → `lb-eu-frankfurt`, `country RU` → отдельный region (Россия), default → `lb-us-virginia`». Plus rejection правила: если из EU придёт запрос с `Authorization` от non-EU пользователя — отдельный домен.
>     - **Когда применять:** для глобальных сервисов с регуляторными требованиями — обязательно с явным mapping региона на dataset. При проектировании учитывать data residency как первичный constraint, latency — вторичный.
>     - **Подводные камни:** failover между регионами при отказе одного создаёт compliance-нарушение (трафик из EU уходит в US, потому что EU-регион упал) — нужно либо дублирование внутри одного юрисдикционного блока (Frankfurt + Dublin), либо явный отказ обслуживать с предупреждением. VPN и proxy-сервисы пользователя ломают geo-IP-routing — нужна гарантия через JWT-claim или явный header.
>     - **Связанные вопросы:** [[load-balancing-interview#Q26]] про DNS-based LB (механизм geo-routing), [[load-balancing-interview#Q39]] про Anycast, [[load-balancing-interview#Q28]] про cross-zone в одном регионе.
> - [ ] C) Geographic LB требует наличия серверов в каждой стране мира — это экономически невыгодно
>     - **Что на самом деле:** geographic LB работает с тем количеством регионов, сколько их есть. Пользователи попадают на ближайший из доступных — не обязательно в своей стране.
>     - **Откуда путаница:** идеальная гео-балансировка ассоциируется с покрытием «каждой страны», но это не требование.
>     - **Если бы это было правдой:** AWS работал бы во всех 195 странах — но в реальности 30+ регионов, и этого хватает для глобальных сервисов.
> - [ ] D) Geographic LB несовместим с CDN — нужно выбирать одно из двух
>     - **Что на самом деле:** geographic LB и CDN — дополняющие друг друга технологии. CDN (Cloudflare/Fastly) кеширует статику ближе к пользователю и обычно интегрирован с гео-роутингом. Origin за CDN-ом может быть в одном регионе или в нескольких с гео-балансировкой.
>     - **Откуда путаница:** оба про «близость к пользователю», и кажется, что они дублируют друг друга.
>     - **Если бы это было правдой:** Cloudflare не имел бы opcion `origin pool` с гео-маршрутизацией — но это базовая фича.


## Q28. Что такое cross-zone load balancing в AWS?

`Cross-zone load balancing` — распределение трафика между узлами в разных зонах доступности (AZ).

```mermaid
graph TB
    subgraph "Cross-zone ВКЛЮЧЁН"
        LB1[ALB] -->|33%| A1[AZ-A: 1 инстанс]
        LB1 -->|33%| B1[AZ-B: 1 инстанс]
        LB1 -->|33%| C1[AZ-C: 1 инстанс]
    end

    subgraph "Cross-zone ВЫКЛЮЧЕН"
        LB2[NLB] -->|50%| A2[AZ-A: 2 инстанса → 25% каждый]
        LB2 -->|50%| B2[AZ-B: 1 инстанс → 50%]
    end
```

**При включённом cross-zone:** трафик распределяется равномерно по **всем** узлам во всех AZ. Лучшее использование ресурсов.

**При выключенном:** трафик распределяется равномерно **по AZ**, а внутри AZ — по узлам. Если в AZ-A 2 инстанса, а в AZ-B 1, то инстанс в AZ-B получит вдвое больше нагрузки.

**Рекомендация:** для `ALB` cross-zone включён по умолчанию (бесплатно). Для `NLB` — выключен (включение платное из-за inter-AZ трафика). Включать для равномерной загрузки; выключать для экономии трафика между AZ.


> [!mcq] Cross-zone load balancing включён на ALB. В AZ-A — 2 инстанса, в AZ-B — 1 инстанс. Как распределится трафик?
>
> - [ ] A) 50% → AZ-A (поровну между двумя инстансами по 25%), 50% → AZ-B (один инстанс 50%) — это «классическое» поведение AZ-aware балансировщика
>     - **Что на самом деле:** это поведение БЕЗ cross-zone (как у `NLB` по умолчанию). При включённом cross-zone трафик распределяется по ВСЕМ инстансам равномерно, независимо от AZ.
>     - **Откуда путаница:** «AZ» в названии создаёт впечатление, что распределение происходит по AZ.
>     - **Если бы это было правдой:** не было бы смысла в опции `cross-zone load balancing enabled/disabled` — но это конкретный переключатель в AWS.
> - [x] **B) Каждый из трёх инстансов получит примерно 33% трафика (равномерно по всем endpoints во всех AZ), потому что cross-zone включён**
>     - **Развёрнутое объяснение:** `cross-zone load balancing` означает, что LB-узел в каждой AZ может направлять трафик в инстансы любой AZ. Балансировщик «видит» весь пул целиком и распределяет запросы поровну между всеми эндпоинтами. В нашем примере 3 инстанса → каждый получает 1/3 трафика. Это лучшая утилизация ресурсов, но возникает inter-AZ трафик (LB-узел в AZ-A шлёт запрос в инстанс AZ-B — это платный data transfer).
>     - **Пример:** для `AWS ALB` cross-zone включён ВСЕГДА (нельзя выключить, бесплатно). Для `AWS NLB`/`GLB` — выключено по умолчанию, включается через атрибут `load_balancing.cross_zone.enabled = true` (платно из-за inter-AZ трафика). В Kubernetes аналог — `externalTrafficPolicy: Cluster` (cross-node маршрутизация) vs `Local` (только локальные поды).
>     - **Когда применять:** включать cross-zone, когда инстансы распределены неравномерно по AZ (autoscaling может дать 5 в одной AZ и 1 в другой) — иначе перекос. Выключать, когда хочется минимизировать inter-AZ трафик (дорогой) и/или сохранить client IP через `Local` policy.
>     - **Подводные камни:** inter-AZ трафик в AWS платный (~$0.01/GB) — для NLB на 1Tb/день это +$300/месяц. Если выключить cross-zone и в одной AZ упадут все инстансы, балансировщик в этой AZ останется без таргетов и вернёт 502 — нужно либо мониторить «healthy targets per AZ», либо принять cross-zone стоимость.
>     - **Связанные вопросы:** [[load-balancing-interview#Q22]] про ALB/NLB, [[load-balancing-interview#Q27]] про geographic LB, [[load-balancing-interview#Q20]] про HA.
> - [ ] C) Только инстансы AZ-A (2 шт.) получат трафик, потому что у них больше реплик — cross-zone не учитывает AZ-B
>     - **Что на самом деле:** трафик идёт во все живые таргеты независимо от AZ. AZ-B-инстанс получит ту же долю.
>     - **Откуда путаница:** «больше реплик = больше трафика» воспринимается как разумная стратегия, но это неверная модель работы LB.
>     - **Если бы это было правдой:** smart autoscaling, поднимающий новые инстансы в перегруженной AZ, не балансировал бы нагрузку — но он работает.
> - [ ] D) AZ-B-инстанс получит 50% трафика, AZ-A-инстансы по 25% — балансировщик предпочитает менее загруженную AZ
>     - **Что на самом деле:** Round Robin (дефолт ALB) не «предпочитает» AZ — он раздаёт по всем эндпоинтам равномерно (по 1/N).
>     - **Откуда путаница:** Least Connections мог бы дать такой эффект, но он не дефолтный алгоритм ALB.
>     - **Если бы это было правдой:** при добавлении новой реплики в AZ-A её доля трафика была бы меньше, чем у инстанса в AZ-B — но в реальности они получают одинаковую долю.


## Q29. Что такое consistent hashing и когда его использовать?

`Consistent hashing` — алгоритм, при котором ключ (например, `userId`) определяет целевой узел. При изменении числа узлов перераспределяется только часть запросов (1/N), а не все.

```mermaid
graph LR
    subgraph "Хэш-кольцо"
        direction TB
        H[Hash Ring 0..2^32]
        N1["Node A (pos 1000)"]
        N2["Node B (pos 4000)"]
        N3["Node C (pos 7000)"]
    end

    K1["key: user-123 → hash 2500"] --> N2
    K2["key: user-456 → hash 5500"] --> N3
    K3["key: user-789 → hash 800"] --> N1
```

**Применение:**
- Кэширование — запросы с одним ключом попадают на один узел кэша (локальность данных)
- Sharding — распределение данных по узлам
- Session affinity без cookie

**Конфигурация в Nginx:**

```nginx
upstream cache_backend {
    hash $request_uri consistent;
    server cache1.example.com:8080;
    server cache2.example.com:8080;
    server cache3.example.com:8080;
}
```

**Конфигурация в HAProxy:**

```haproxy
backend cache_servers
    balance uri  # хэширование по URI
    hash-type consistent
    server cache1 10.0.0.1:8080 check
    server cache2 10.0.0.2:8080 check
    server cache3 10.0.0.3:8080 check
```

**Виртуальные узлы (vnodes):** для более равномерного распределения каждый физический узел представлен множеством точек на кольце (обычно 100-200 vnodes).


> [!mcq] В чём ключевое преимущество consistent hashing над обычным `hash % N` при изменении числа серверов?
>
> - [ ] A) Consistent hashing работает быстрее (O(1) lookup), потому что использует битовую арифметику вместо деления
>     - **Что на самом deле:** скорость не главное преимущество. И обычный `hash % N`, и базовый ring consistent hashing — это O(log N) на ring lookup (бинарный поиск по точкам). Главное преимущество — стабильность маппинга при изменении числа узлов.
>     - **Откуда путаница:** Maglev consistent hashing действительно даёт O(1) через lookup table, но это специальная разновидность, не общая особенность consistent hashing.
>     - **Если бы это было правдой:** не было бы смысла именно в «consistent» в названии — но именно консистентность маппинга при добавлении/удалении узлов даёт алгоритму смысл.
> - [x] **B) При добавлении или удалении узла перераспределяется только ~1/N ключей (часть кольца, принадлежащая выходящему/входящему узлу), а не все ключи — что критично для кешей и шардинга**
>     - **Развёрнутое объяснение:** в обычном `hash(key) % N` при изменении N (с 4 на 5 узлов) меняется значение `% N` для ПОЧТИ КАЖДОГО ключа — то есть 80% запросов начинают идти на другие узлы. Для кеша это означает 80% miss-ratio = эффективный invalidate всего кеша. Consistent hashing решает это, размещая узлы на «кольце» по хешу: ключ идёт к ближайшему узлу по часовой стрелке. При добавлении узла он «отнимает» свою долю кольца только у одного соседа — затрагивается 1/N ключей.
>     - **Пример:** `Nginx upstream { hash $request_uri consistent; ... }` — кеширующий пул, где `user-123` всегда идёт на cache-2, пока пул не меняется. При добавлении cache-4 только ~25% ключей переедут (те, чьи позиции на кольце оказались ближе к cache-4, чем к старым узлам). В `HAProxy`: `balance uri; hash-type consistent`. Применяется в `Cassandra` (партиционирование), `Memcached` (клиентская hash-ring библиотека), `Redis Cluster` (16384 hash slots — дискретный вариант).
>     - **Когда применять:** распределённые кеши (`Memcached`/`Redis`), partition-based БД (`Cassandra`), session affinity без cookie (по `user_id`), DHT (Chord). Везде, где локальность данных важна и/или дорого инвалидировать кеш.
>     - **Подводные камни:** базовая реализация даёт неравномерное распределение — узел A может получить 40% кольца, а B — 10%. Решается виртуальными узлами (vnodes) — каждый физический узел представлен 100-200 точками на кольце; тогда распределение приближается к равномерному. Hot keys всё равно остаются hot — consistent hashing не балансирует нагрузку по ключам, только по диапазонам.
>     - **Связанные вопросы:** [[load-balancing-interview#Q36]] про IP Hash (тот же принцип на IP), [[load-balancing-interview#Q38]] про Maglev hashing (улучшенный consistent), [[load-balancing-interview#Q33]] про stateful приложения.
> - [ ] C) Consistent hashing гарантирует равномерное распределение нагрузки между серверами без виртуальных узлов
>     - **Что на самом деле:** базовый consistent hashing неравномерен — нужны vnodes (виртуальные узлы) для равномерности. Maglev hashing даёт равномерность встроено, но это отдельный алгоритм.
>     - **Откуда путаница:** реклама consistent hashing часто упоминает «равномерное распределение» как часть преимуществ, забывая про vnodes.
>     - **Если бы это было правдой:** не было бы дискуссий про оптимальное число vnodes (100, 200, 1000) — но эта проблема обсуждается во всех гайдах.
> - [ ] D) Consistent hashing шифрует данные с использованием хеш-функции, защищая от атак на маппинг ключей
>     - **Что на самом деле:** consistent hashing — это алгоритм маршрутизации, не шифрования. Хеш-функция нужна для детерминированного выбора узла, а не для защиты данных.
>     - **Откуда путаница:** «hashing» в безопасности ассоциируется с шифрованием паролей.
>     - **Если бы это было правдой:** consistent hashing был бы в OWASP guides по шифрованию — но он в Distributed Systems guides.


## Q30. Как настроить балансировку для gRPC?

`gRPC` использует `HTTP/2` с мультиплексированием — одно TCP-соединение обслуживает множество запросов. Это создаёт проблему: L4-балансировщик распределит соединения, но все запросы по одному соединению пойдут на один сервер.

**Решения:**

1. **L7-балансировка** — балансировщик понимает `HTTP/2` фреймы:

```nginx
# Nginx с gRPC (L7)
upstream grpc_backend {
    least_conn;
    server backend1:9090;
    server backend2:9090;
}

server {
    listen 443 ssl http2;

    location / {
        grpc_pass grpc://grpc_backend;
        grpc_set_header Host $host;
    }
}
```

2. **Клиентская балансировка** — `gRPC` клиент сам распределяет запросы:

```java
// gRPC Java client с round_robin
ManagedChannel channel = ManagedChannelBuilder
    .forTarget("dns:///my-service.default.svc.cluster.local:9090")
    .defaultLoadBalancingPolicy("round_robin")
    .usePlaintext()
    .build();
```

3. **Service Mesh** (Istio, Linkerd) — sidecar proxy (`Envoy`) балансирует на уровне отдельных gRPC-вызовов.

**В Kubernetes:** стандартный `Service` (L4) не балансирует gRPC запросы эффективно. Используйте headless `Service` + клиентскую балансировку или `Istio`.


> [!mcq] Почему L4-балансировщик (`AWS NLB`, `HAProxy mode tcp`) плохо распределяет `gRPC` трафик между бэкендами?
>
> - [x] **A) `gRPC` использует `HTTP/2` мультиплексирование — все запросы клиента идут по одному TCP-соединению; L4 балансирует соединения, а не запросы, поэтому все вызовы попадают на один бэкенд**
>     - **Развёрнутое объяснение:** `HTTP/2` (на котором работает `gRPC`) использует одно TCP-соединение для множества параллельных stream'ов (запросов). Клиент устанавливает одно соединение с балансировщиком и через него шлёт тысячи gRPC-вызовов. L4 видит «одно соединение» и направляет его на один бэкенд — все вызовы оседают там, остальные бэкенды простаивают. Решения: (а) L7-балансировщик, понимающий `HTTP/2` фреймы и балансирующий на уровне stream'ов; (б) клиентская балансировка с несколькими subchannels; (в) service mesh с sidecar-proxy, балансирующим на уровне RPC.
>     - **Пример:** `Nginx` с `grpc_pass grpc://grpc_backend` — L7 для gRPC. Java gRPC-клиент с `defaultLoadBalancingPolicy("round_robin")` и `dns:///service.namespace.svc:9090` использует headless `Service` Kubernetes и сам распределяет вызовы. Istio с `Envoy`-sidecar балансирует gRPC-вызовы автоматически благодаря `LEAST_REQUEST`.
>     - **Когда применять:** Nginx L7-grpc — если хочется централизованный балансировщик с TLS termination. Клиентская балансировка — для микросервисов между собой (нет лишнего hop). Service mesh — когда нужно outlier detection, circuit breaker, retry с backoff без правки приложений.
>     - **Подводные камни:** Kubernetes стандартный `Service` (kube-proxy) — это L4 (iptables), поэтому gRPC через обычный Service распределяется плохо. Нужен либо headless `Service` (`clusterIP: None`) + клиентская балансировка, либо service mesh, либо ingress-controller с gRPC support. `HTTP/2` keep-alive по умолчанию долгий — клиент может «прилипнуть» к одному поду на часы, если у него стабильная нагрузка.
>     - **Связанные вопросы:** [[load-balancing-interview#Q1]] про L4 vs L7, [[load-balancing-interview#Q22]] про cloud LB (ALB vs NLB для gRPC), [[load-balancing-interview#Q40]] про Istio service mesh.
> - [ ] B) `gRPC` использует UDP вместо TCP, а L4-балансировщики не поддерживают UDP-балансировку
>     - **Что на самом деле:** `gRPC` работает поверх `HTTP/2` поверх `TCP`. UDP не используется (есть экспериментальный `gRPC over QUIC`, но это не стандарт). И большинство L4-балансировщиков (`NLB`) поддерживают UDP.
>     - **Откуда путаница:** HTTP/3 (QUIC) действительно UDP, и это переносят на gRPC.
>     - **Если бы это было правдой:** в Wireshark gRPC-трафик показывался бы как UDP — но он показывается как HTTP/2 (TCP).
> - [ ] C) `gRPC` шифрует трафик через mTLS, и L4 не может его расшифровать для распределения по path
>     - **Что на самом деле:** L4 не должен расшифровывать трафик (это L7-работа). И mTLS — это режим работы, а не обязательный для gRPC. Проблема балансировки gRPC — в мультиплексировании HTTP/2, а не в шифровании.
>     - **Откуда путаница:** mTLS часто упоминают рядом с gRPC, и кажется, что это причина любой проблемы.
>     - **Если бы это было правдой:** plaintext gRPC (`usePlaintext()`) балансировался бы на L4 нормально — но и в plaintext-режиме L4 не работает корректно.
> - [ ] D) gRPC требует sticky session по `Authorization` заголовку, что L4-балансировщик не умеет
>     - **Что на самом деле:** gRPC не требует sticky session (это stateless RPC по своей природе). И L4 действительно не читает заголовки — но не в этом главная проблема балансировки.
>     - **Откуда путаница:** некоторые stateful gRPC-сервисы могут использовать affinity, но это особенность конкретного приложения.
>     - **Если бы это было правдой:** stateless gRPC балансировался бы L4 нормально — но не балансируется ни в каком режиме.


## Q31. Как настроить балансировку для WebSocket соединений?

`WebSocket` — долгоживущие соединения. Ключевые особенности:
- Соединение устанавливается через HTTP Upgrade и живёт часы/дни
- Все сообщения по одному соединению идут на один сервер
- При падении сервера соединение разрывается

**Конфигурация в Nginx:**

```nginx
upstream websocket_backend {
    ip_hash;  # sticky session для WebSocket
    server ws1.example.com:8080;
    server ws2.example.com:8080;
}

server {
    location /ws {
        proxy_pass http://websocket_backend;

        # Обязательно для WebSocket
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        # Увеличенные таймауты для долгих соединений
        proxy_read_timeout 3600s;
        proxy_send_timeout 3600s;
    }
}
```

**Конфигурация в HAProxy:**

```haproxy
frontend ws_front
    bind *:80
    acl is_websocket hdr(Upgrade) -i WebSocket
    use_backend ws_servers if is_websocket

backend ws_servers
    balance leastconn
    timeout server 3600s
    timeout tunnel 3600s
    server ws1 10.0.0.1:8080 check
    server ws2 10.0.0.2:8080 check
```

**Рекомендация:** для масштабирования WebSocket приложений — хранить состояние сессий в [Redis](../databases/redis-interview.md) Pub/Sub, чтобы сообщения доставлялись клиентам на любом сервере.


> [!mcq] Что нужно настроить в Nginx для корректной балансировки WebSocket-соединений (часовая сессия)?
>
> - [ ] A) Только `proxy_pass http://backend` — Nginx автоматически распознаёт `Upgrade: websocket` и переключает соединение в туннель
>     - **Что на самом деле:** Nginx по умолчанию проксирует HTTP/1.0 и не пробрасывает `Upgrade`/`Connection` заголовки. Без явного `proxy_http_version 1.1` + `proxy_set_header Upgrade $http_upgrade` + `proxy_set_header Connection "upgrade"` WebSocket-handshake обламывается, клиент видит «`Connection: close`».
>     - **Откуда путаница:** в простых HTTP-кейсах действительно достаточно `proxy_pass`, и эту простоту переносят на WebSocket.
>     - **Если бы это было правдой:** официальная документация Nginx не публиковала бы отдельный раздел «WebSocket proxying» — но он там есть.
> - [x] **B) `proxy_http_version 1.1` + `proxy_set_header Upgrade $http_upgrade` + `proxy_set_header Connection "upgrade"` + увеличить `proxy_read_timeout` до часов; добавить `ip_hash` (или sticky cookie) для прилипания клиента к одному узлу**
>     - **Развёрнутое объяснение:** WebSocket стартует как HTTP/1.1 запрос с заголовками `Upgrade: websocket`, `Connection: Upgrade`. Nginx должен (а) использовать HTTP/1.1 при проксировании, (б) явно пробросить эти заголовки (`$http_upgrade` — динамическое значение), (в) держать соединение долго (`proxy_read_timeout 3600s` — час по дефолту это слишком мало). Дополнительно: все сообщения WebSocket идут по одному соединению на один сервер, и при `Round Robin` второй XHR-запрос клиента уйдёт на другой узел и не найдёт там сессию — поэтому нужен `ip_hash` или sticky cookie.
>     - **Пример:** `upstream websocket_backend { ip_hash; server ws1:8080; server ws2:8080; }` + `location /ws { proxy_pass http://websocket_backend; proxy_http_version 1.1; proxy_set_header Upgrade $http_upgrade; proxy_set_header Connection "upgrade"; proxy_read_timeout 3600s; }`. Для масштаба — состояние сессий в `Redis Pub/Sub`, чтобы доставка сообщений работала на любом узле.
>     - **Когда применять:** для real-time приложений (чат, биржевые котировки, multiplayer-игры, IoT-сенсоры). Альтернативно — SSE (Server-Sent Events) проще балансировать (обычный HTTP, но требует `proxy_buffering off`).
>     - **Подводные камни:** `ip_hash` ломается за NAT (много клиентов = один IP) — лучше sticky cookie через `sticky cookie srv_id` (только Nginx Plus) или явный routing по userId. Long-lived соединения мешают rolling deployment — при остановке Pod клиенты массово реконнектятся; нужна graceful disconnection (отправить `close frame` с retry instruction) и `terminationGracePeriodSeconds` >= типичной длительности сессии.
>     - **Связанные вопросы:** [[load-balancing-interview#Q4]] про sticky session, [[load-balancing-interview#Q21]] про connection draining, [[load-balancing-interview#Q33]] про stateful приложения.
> - [ ] C) Использовать `mode tcp` в `Nginx stream` блоке — L4 балансировка автоматически поддерживает WebSocket
>     - **Что на самом деле:** L4-балансировка действительно работает для WebSocket (TCP туннель), но теряется L7-роутинг (нельзя направить `/ws` на одни узлы, `/api` на другие) и TLS termination. Это допустимый вариант для чистого WS-кластера, но требует отдельной точки входа.
>     - **Откуда путаница:** «WS — это TCP» воспринимается как «L4 = решение», но WS стартует с HTTP-handshake, и L7 даёт больше контроля.
>     - **Если бы это было правдой:** L7-балансировщики не публиковали бы конфиги для WS — но они стандарт.
> - [ ] D) WebSocket не требует балансировки — каждый клиент должен подключаться напрямую к конкретному серверу через DNS-записи
>     - **Что на самом деле:** прямое подключение убивает HA (клиент привязан к конкретному узлу — узел упал, клиент не переключится). И масштабирование становится ручной настройкой DNS.
>     - **Откуда путаница:** в малых системах действительно иногда обходятся без балансировщика, но это не паттерн для production.
>     - **Если бы это было правдой:** не существовало бы конфигов WS-балансировки в Nginx/HAProxy/Envoy — но они есть везде.


## Q32. Что такое path-based routing в L7 балансировщике?

`Path-based routing` — маршрутизация запросов на разные бэкенды по пути URL. Базовый инструмент L7-балансировки.

**Конфигурация в Nginx:**

```nginx
upstream user_service    { server 10.0.1.1:8080; server 10.0.1.2:8080; }
upstream order_service   { server 10.0.2.1:8080; server 10.0.2.2:8080; }
upstream payment_service { server 10.0.3.1:8080; server 10.0.3.2:8080; }

server {
    listen 80;

    # Маршрутизация по path
    location /api/users {
        proxy_pass http://user_service;
    }

    location /api/orders {
        proxy_pass http://order_service;
    }

    location /api/payments {
        proxy_pass http://payment_service;
    }

    # Маршрутизация по regex
    location ~ ^/api/v[0-9]+/products {
        proxy_pass http://product_service;
    }

    # Default backend
    location / {
        return 404 '{"error": "route not found"}';
    }
}
```

**Конфигурация в HAProxy (ACL):**

```haproxy
frontend http_front
    bind *:80

    acl path_users   path_beg /api/users
    acl path_orders  path_beg /api/orders
    acl path_static  path_beg /static

    use_backend user_servers   if path_users
    use_backend order_servers  if path_orders
    use_backend static_servers if path_static
    default_backend app_servers
```

Path-based routing упрощает архитектуру [микросервисов](microservices-interview.md) без необходимости отдельного API Gateway.


> [!mcq] Какое из утверждений про path-based routing на L7 верно?
>
> - [ ] A) Path-based routing работает на L4-балансировщиках через TCP SNI (Server Name Indication) и не требует расшифровки трафика
>     - **Что на самом деле:** SNI — это `host`-based routing, доступный на L4 благодаря тому, что hostname передаётся в TLS ClientHello в plain text. `path` находится внутри HTTP-запроса (после TLS handshake) и недоступен без расшифровки. Поэтому path-based routing — это исключительно L7-фича.
>     - **Откуда путаница:** SNI часто упоминают вместе с path-routing, и кажется, что это одна и та же техника.
>     - **Если бы это было правдой:** Path-routing работал бы на `AWS NLB` — но он только на ALB.
> - [x] **B) Path-based routing — это L7-функция (требует парсинга HTTP-запроса) для маршрутизации `/api/users` → user-pool, `/api/orders` → order-pool; реализуется через `location` в Nginx, `acl path_beg` в HAProxy, `paths` в Kubernetes Ingress**
>     - **Развёрнутое объяснение:** path-based routing — базовый механизм L7-балансировки, позволяющий разделить трафик нескольких сервисов на одной точке входа по структуре URL. Балансировщик читает HTTP-запрос (для HTTPS — после TLS termination), смотрит на `path` и выбирает upstream-пул. В Nginx — через `location /api/users { proxy_pass http://user_pool; }`, причём порядок и тип (`prefix`, `regex`) важны. В HAProxy — через `acl path_users path_beg /api/users` + `use_backend user_servers if path_users`. В Kubernetes — через `Ingress paths: - path: /api/users, pathType: Prefix, backend: user-service`.
>     - **Пример:** монолит API на одном домене `api.example.com`, под капотом — разные микросервисы. `/api/users/*` → user-service, `/api/orders/*` → order-service, `/api/payments/*` → payment-service. Path-routing позволяет не делать отдельные субдомены и обходиться без API Gateway, если нужен только роутинг.
>     - **Когда применять:** при разделении монолита на микросервисы с сохранением единого URL для клиента; когда не нужны фичи API Gateway (трансформации, auth, throttling); внутри Kubernetes-кластера через Ingress. Альтернативы — host-based routing (`api.users.example.com`) или API Gateway (`Kong`, `AWS API Gateway`).
>     - **Подводные камни:** в Nginx `location` matching имеет тонкие правила: exact (`=`) → prefix → regex, и неправильный порядок ломает роутинг. В Kubernetes Ingress `pathType: Prefix` означает префиксный match, `Exact` — точное совпадение. Аннотация `nginx.ingress.kubernetes.io/rewrite-target` нужна, если хочется убрать prefix перед передачей бэкенду (`/api/users/123` → backend получает `/123`).
>     - **Связанные вопросы:** [[load-balancing-interview#Q1]] про L7-роутинг, [[load-balancing-interview#Q11]] про Nginx upstream, [[load-balancing-interview#Q24]] про Kubernetes Ingress.
> - [ ] C) Path-based routing замедляет балансировщик в 10 раз по сравнению с round-robin без анализа path
>     - **Что на самом деле:** парсинг path — это парсинг первой строки HTTP-запроса (которая всё равно парсится для проксирования). Overhead — микросекунды на запрос, заметен только на ультра-низко-латентных нагрузках.
>     - **Откуда путаница:** в гайдах сравнивают «L4 vs L7 latency», и абстрактно L7 медленнее, но не «в 10 раз» именно из-за path.
>     - **Если бы это было правдой:** Path-routing не использовался бы повсеместно — но он базовый паттерн.
> - [ ] D) В Kubernetes path-based routing настраивается только через `Istio VirtualService`, в стандартном `Ingress` его нет
>     - **Что на самом деле:** стандартный Kubernetes `Ingress` (`networking.k8s.io/v1`) — это именно про path-based и host-based routing. Istio VirtualService — расширенный вариант с traffic shifting, retries, fault injection.
>     - **Откуда путаница:** Istio часто продвигают как «продвинутый Ingress», и забывают про базовый Kubernetes API.
>     - **Если бы это было правдой:** в Kubernetes без Istio нельзя было бы маршрутизировать HTTP — но это базовая функциональность.


## Q33. Как обеспечить балансировку для stateful приложений?

Три подхода для stateful приложений:

**1. Sticky Session** — привязка клиента к серверу (самый простой, но хрупкий):
```nginx
upstream backend {
    ip_hash;
    server backend1:8080;
    server backend2:8080;
}
```

**2. Общее хранилище (рекомендуется)** — состояние в Redis/Memcached, приложение stateless:

```java
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
public class SessionConfig {

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory("redis-cluster", 6379);
    }
}
```

```yaml
# application.yml
spring:
  session:
    store-type: redis
  redis:
    host: redis-cluster
    port: 6379
```

**3. Sharding по ключу** — consistent hashing, запросы с одним ключом на один узел:
```nginx
upstream backend {
    hash $arg_userId consistent;
    server backend1:8080;
    server backend2:8080;
    server backend3:8080;
}
```

**Рекомендация:** вариант 2 (Redis) — наиболее надёжный. Приложение stateless, горизонтально масштабируется, при падении узла сессии не теряются. Подробнее о Redis — в [вопросах по Redis](../databases/redis-interview.md).


> [!mcq] Какой подход к stateful приложениям обеспечивает максимальную надёжность и горизонтальное масштабирование?
>
> - [ ] A) Sticky session через `ip_hash` — простое решение, обеспечивающее привязку клиента к серверу без изменения приложения
>     - **Что на самом деле:** sticky session работает, но хрупкая: при падении узла все привязанные к нему сессии теряются (пользователи разлогиниваются); при NAT много клиентов попадают на один узел (перекос нагрузки); при scale-up распределение нагрузки не работает мгновенно (сессии остаются на старых узлах). Это технический долг, не «надёжное решение».
>     - **Откуда путаница:** sticky session — самый простой в настройке, и это путают с «лучшим».
>     - **Если бы это было правдой:** все серверные платформы продолжали бы продвигать sticky session — но рекомендации движутся в сторону stateless приложений.
> - [x] **B) Вынести состояние сессий во внешнее общее хранилище (Redis, Memcached) — приложение становится stateless, любой узел обслуживает любой запрос, при падении узла сессии не теряются**
>     - **Развёрнутое объяснение:** sticky session — это компромисс, делающий приложение псевдо-stateless. Лучший подход — фактически сделать его stateless: вынести state из памяти приложения в общее хранилище. Java/Spring имеет `spring-session-data-redis` — добавляешь зависимость + `@EnableRedisHttpSession`, и HTTP-сессии автоматически идут в Redis. После этого балансировщик может использовать любой алгоритм (Round Robin, Least Connections), при падении узла сессии не теряются, scale-up даёт мгновенный эффект, при rolling deployment пользователь не разлогинивается.
>     - **Пример:** `@Configuration @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)` + `LettuceConnectionFactory("redis-cluster", 6379)`. В application.yml: `spring.session.store-type: redis`. Аналогично в Node.js — `express-session` с `connect-redis`. В Python Django — `django.contrib.sessions.backends.cache` с Redis backend.
>     - **Когда применять:** ВСЕГДА, когда есть возможность. Это де-факто стандарт современных веб-приложений. Альтернатива — JWT-токены с состоянием в самом токене (полностью stateless без внешнего хранилища, но с компромиссом инвалидации).
>     - **Подводные камни:** Redis становится новой SPOF — нужен Redis Cluster или Sentinel для HA. Latency сети до Redis (~1ms) добавляется к каждому запросу — для очень latency-sensitive нагрузок может быть критично. Размер сессии в Redis ограничен (рекомендация: < 100KB), иначе деградирует производительность. Безопасность: данные сессии в Redis должны шифроваться при чувствительности (PCI/HIPAA).
>     - **Связанные вопросы:** [[load-balancing-interview#Q4]] про sticky session (компромиссный путь), [[load-balancing-interview#Q29]] про consistent hashing (для sharding session). См. также [вопросы по Redis](../databases/redis-interview.md).
> - [ ] C) Sharding по `userId` через consistent hashing — каждый пользователь всегда попадает на один сервер, состояние хранится локально на этом сервере
>     - **Что на самом деле:** consistent hashing решает проблему распределения, но не отказоустойчивости: при падении «своего» узла пользователь теряет сессию (или ждёт восстановления узла). Это улучшение над `ip_hash`, но всё равно не stateless.
>     - **Откуда путаница:** consistent hashing — продвинутая техника, и её ассоциируют с «лучшей» архитектурой.
>     - **Если бы это было правдой:** большие веб-сервисы (Google, Facebook, Twitter) не использовали бы Memcached/Redis как session store — но они используют.
> - [ ] D) Использовать `Database session` — хранить сессии в основной БД (PostgreSQL) с индексом на session_id
>     - **Что на самом деле:** работает, но добавляет нагрузку на основную БД (каждый запрос = SELECT + UPDATE сессии = 2 SQL-запроса), что для production-нагрузок неприемлемо. PostgreSQL-сессии используют в low-traffic админках, не в продуктовых API.
>     - **Откуда путаница:** «у нас уже есть БД» — соблазнительно использовать её для всего.
>     - **Если бы это было правдой:** не было бы отдельных session-store решений (Redis, Memcached) — но они существуют именно для разгрузки БД.


## Q34. (!) Как мониторить и анализировать работу балансировщика?

Ключевые метрики для [мониторинга](../monitoring/metrics-tracing-interview.md) балансировщика:

| Метрика | Описание | Алерт |
|---------|----------|-------|
| Active connections per backend | Текущие соединения | Разброс > 30% |
| RPS per backend | Запросы в секунду | Неравномерность |
| Latency p50/p95/p99 | Задержка ответа | p99 > 200ms |
| Error rate (4xx, 5xx) | Процент ошибок | > 5% |
| Healthy backends | Число живых узлов | < N-1 |
| Bandwidth | Трафик in/out | Аномалии |

**Nginx: включение метрик:**

```nginx
# stub_status для базовых метрик
server {
    listen 8081;
    location /nginx_status {
        stub_status;
        allow 10.0.0.0/8;
        deny all;
    }
}

# Формат логов для анализа
log_format upstream_log '$remote_addr - $request '
    'upstream: $upstream_addr '
    'status: $upstream_status '
    'response_time: $upstream_response_time '
    'connect_time: $upstream_connect_time';
```

**HAProxy: встроенный dashboard + Prometheus exporter:**

```haproxy
listen stats
    bind *:8404
    stats enable
    stats uri /stats
    stats refresh 10s

# Prometheus endpoint
frontend prometheus
    bind *:8405
    http-request use-service prometheus-exporter if { path /metrics }
    no log
```

**Prometheus запросы для алертинга:**

```promql
# Процент 5xx ошибок за 5 минут
sum(rate(haproxy_server_http_responses_total{code="5xx"}[5m]))
/ sum(rate(haproxy_server_http_responses_total[5m])) > 0.05

# Число unhealthy бэкендов
haproxy_server_status{state="DOWN"} > 0

# p99 латентность бэкенда
histogram_quantile(0.99,
  rate(nginx_upstream_response_duration_seconds_bucket[5m])) > 0.2
```

Экспортировать метрики в [систему наблюдаемости](../monitoring/observability-interview.md) (`Prometheus` + `Grafana`) для визуализации и алертинга.


> [!mcq] Какой набор метрик балансировщика критичен для диагностики неравномерной нагрузки бэкендов?
>
> - [ ] A) Только агрегированная RPS на балансировщик и общая error rate — этого достаточно для оценки здоровья кластера
>     - **Что на самом деле:** агрегированные метрики скрывают перекосы. Если один бэкенд получает 80% трафика, а остальные простаивают, общий RPS будет «нормальный», но первый бэкенд перегреется. Нужны метрики ПО КАЖДОМУ бэкенду отдельно: per-backend RPS, per-backend latency, per-backend connections.
>     - **Откуда путаница:** Grafana-дашборды часто показывают агрегаты «top of the page», и это создаёт иллюзию полноты.
>     - **Если бы это было правдой:** в HAProxy stats не показывали бы строки по каждому серверу — но это базовый отчёт.
> - [x] **B) Per-backend метрики: активные соединения, RPS, p50/p95/p99 latency, error rate (4xx/5xx), healthy/unhealthy статус; алерты на разброс > 30% между бэкендами и p99 > SLO**
>     - **Развёрнутое объяснение:** для диагностики неравномерной нагрузки нужны метрики в разрезе каждого бэкенда: (1) `active_connections` — текущие соединения, должны быть примерно равны для Least Connections; (2) `requests_per_second` — RPS на бэкенд, должен быть пропорционален весу (если веса равны); (3) latency p50/p95/p99 — задержка ответа, по перцентилям; разница в p99 между бэкендами > 50% сигнализирует о проблеме одного из них; (4) `error_rate` 4xx/5xx — процент ошибок отдельно по бэкенду; (5) `healthy_backends_count` — алерт при <N-1. PromQL: `sum(rate(nginx_upstream_requests_total[5m])) by (upstream)` для per-backend RPS; `histogram_quantile(0.99, rate(nginx_upstream_response_duration_seconds_bucket[5m])) by (upstream)` для per-backend p99.
>     - **Пример:** Nginx — формат логов `log_format upstream_log '$upstream_addr status: $upstream_status response_time: $upstream_response_time'` + `nginx-prometheus-exporter`. HAProxy — встроенный `stats uri /stats` + endpoint `/metrics` для Prometheus. ALB — CloudWatch metrics `TargetResponseTime`, `HTTPCode_Target_4XX_Count` по `LoadBalancer + TargetGroup + AvailabilityZone`. Dashboards в Grafana с per-backend breakdown и алерты в Alertmanager.
>     - **Когда применять:** в production ВСЕГДА. Сразу при настройке нового балансировщика — экспортёр метрик + дашборд + алерты. Без этого диагностика инцидентов превращается в догадки.
>     - **Подводные камни:** Nginx OSS не отдаёт per-backend метрики из коробки — нужен Nginx Plus или `nginx-prometheus-exporter` с парсингом access log. ALB не показывает latency БЭКЕНДА отдельно от полной transaction — нужно различать `RequestProcessingTime` (LB), `TargetResponseTime` (бэкенд), `ResponseProcessingTime` (LB). Высокая cardinality метрик per-backend взрывает Prometheus storage — фильтровать только важные перцентили.
>     - **Связанные вопросы:** [[load-balancing-interview#Q12]] про health checks Nginx, [[load-balancing-interview#Q15]] про HAProxy health, [[load-balancing-interview#Q35]] про медленные бэкенды (требует именно per-backend latency).
> - [ ] C) Достаточно метрик инфраструктуры (CPU, memory) на хостах балансировщика и бэкендов — приложение-специфичные метрики избыточны
>     - **Что на самом деле:** CPU/memory показывают, что у сервера есть ресурсы, но не показывают, что балансировщик правильно распределяет нагрузку. Бэкенд может иметь 30% CPU, но получать 80% запросов и медленно отвечать из-за внутренней очереди.
>     - **Откуда путаница:** инфраструктурные команды часто фокусируются на host-метриках.
>     - **Если бы это было правдой:** не нужны были бы exporters для Nginx/HAProxy — но они есть для каждого LB.
> - [ ] D) Метрик не нужно — балансировщик автоматически выравнивает нагрузку через Round Robin, и любые проблемы решаются перезапуском
>     - **Что на самом деле:** Round Robin не гарантирует равномерную нагрузку (один медленный бэкенд накопит запросы); перезапуск без диагностики причины — антипаттерн (та же проблема вернётся).
>     - **Откуда путаница:** в малых системах действительно «всё работает», и observability воспринимается как излишество.
>     - **Если бы это было правдой:** SRE-практики не существовали бы — но это базовая дисциплина в production.


## Q35. Как балансировщик обрабатывает медленные бэкенды?

Проблема: один медленный бэкенд может «заразить» весь пул, если балансировщик продолжает отправлять ему запросы.

**Поведение при разных алгоритмах:**

| Алгоритм | Поведение | Риск |
|----------|-----------|------|
| `Round Robin` | Отправляет равномерно — медленный перегружается | Высокий |
| `Least Connections` | Медленный накапливает соединения → меньше новых | Средний |
| `Least Response Time` | Медленный автоматически получает меньше | Низкий |

**Защита в Nginx:**

```nginx
upstream backend_pool {
    least_conn;
    server backend1:8080 max_fails=3 fail_timeout=30s;
    server backend2:8080 max_fails=3 fail_timeout=30s;
    server backend3:8080 max_fails=3 fail_timeout=30s;
}

server {
    location / {
        proxy_pass http://backend_pool;

        # Таймауты — не ждать медленный бэкенд вечно
        proxy_connect_timeout 3s;
        proxy_read_timeout 10s;

        # При таймауте — retry на следующий сервер
        proxy_next_upstream error timeout http_502 http_503;
        proxy_next_upstream_tries 2;
        proxy_next_upstream_timeout 15s;
    }
}
```

**Защита в HAProxy:**

```haproxy
backend app_servers
    balance leastconn
    timeout server 10s
    timeout queue 5s

    option httpchk GET /health
    http-check expect status 200

    # slowstart — постепенный ввод после восстановления
    server app1 10.0.0.1:8080 check inter 3s fall 3 rise 2 slowstart 30s
    server app2 10.0.0.2:8080 check inter 3s fall 3 rise 2 slowstart 30s

    # Ограничение соединений на сервер
    server app3 10.0.0.3:8080 check maxconn 100
```

**Рекомендации:**
- Использовать `Least Connections` или `Least Response Time`
- Настроить таймауты (`proxy_read_timeout`, `timeout server`)
- Включить `proxy_next_upstream` для автоматического retry
- Мониторить p95/p99 по каждому бэкенду отдельно
- Настроить circuit breaker на уровне приложения (подробнее в [Spring Cloud](../frameworks/spring/spring-cloud-interview.md))


> [!mcq] Какой алгоритм и какая защита подходят, чтобы один медленный бэкенд не «заразил» весь пул через Round Robin?
>
> - [ ] A) Усилить Round Robin, увеличив частоту health checks до 1 раза в секунду — балансировщик будет быстрее замечать медленные бэкенды и исключать их
>     - **Что на самом деле:** health check проверяет «жив/мёртв», не «быстрый/медленный». Медленный бэкенд может проходить health check (`/health` отвечает за 50ms), но при этом отвечать обычным запросам за 5s. Частота health checks не решает проблему медленных, но рабочих бэкендов.
>     - **Откуда путаница:** health checks — известный инструмент, и его применяют по аналогии.
>     - **Если бы это было правдой:** не существовали бы circuit breaker и outlier detection — но это базовые паттерны.
> - [x] **B) Переключить на Least Connections / Least Response Time + настроить таймауты (`proxy_read_timeout 10s`) + `proxy_next_upstream` для retry на следующий сервер + `slowstart` при возврате восстановленного узла**
>     - **Развёрнутое объяснение:** Round Robin отправляет запросы равномерно — медленный бэкенд получает столько же новых запросов, сколько остальные, и накапливает очередь. Least Connections (или его улучшенная версия Least Response Time / Power of Two Choices) видит, что у медленного бэкенда больше активных соединений, и направляет меньше новых. Таймауты (`proxy_read_timeout 10s` в Nginx, `timeout server 10s` в HAProxy) не дают клиенту висеть бесконечно на медленном бэкенде. `proxy_next_upstream error timeout http_502 http_503` автоматически реитерирует запрос на следующий узел при ошибке/таймауте. `slowstart 30s` (HAProxy) или `slow_start 30s` (Nginx Plus) даёт восстановленному узлу время на прогрев — постепенно увеличивает его вес, чтобы он не получил сразу полную нагрузку и снова не упал. Дополнительно — circuit breaker на уровне приложения через Resilience4j/Spring Cloud.
>     - **Пример:** `upstream backend_pool { least_conn; server backend1:8080 max_fails=3 fail_timeout=30s; ... } location / { proxy_pass http://backend_pool; proxy_read_timeout 10s; proxy_next_upstream error timeout http_502 http_503; proxy_next_upstream_tries 2; }`. В HAProxy: `balance leastconn` + `timeout server 10s` + `option redispatch` + `server app1 ... check inter 3s fall 3 rise 2 slowstart 30s`.
>     - **Когда применять:** для всех production-балансировщиков сервисов с переменной нагрузкой. Round Robin — только когда все бэкенды абсолютно одинаковы по производительности и нагрузка предсказуема.
>     - **Подводные камни:** `proxy_next_upstream` может удвоить нагрузку на бэкенды при массовой деградации (retry storm) — нужно ограничивать `proxy_next_upstream_tries` и иметь circuit breaker. `slowstart` без `Nginx Plus` недоступен в OSS Nginx — альтернатива через HAProxy или Envoy. `Least Connections` плохо работает в short-lived соединениях (HTTP/2 multiplexing) — там лучше Least Request (P2C).
>     - **Связанные вопросы:** [[load-balancing-interview#Q8]] про Least Connections, [[load-balancing-interview#Q9]] про Least Response Time, [[load-balancing-interview#Q37]] про Power of Two Choices.
> - [ ] C) Увеличить `proxy_read_timeout` до 5 минут, чтобы все запросы успели завершиться, и продолжать слать на медленный бэкенд
>     - **Что на самом деле:** удлинение таймаута усугубляет проблему — клиенты ждут дольше, очередь на медленном бэкенде растёт быстрее, ресурсы (потоки, память) тают. Нужно НАОБОРОТ — короткий таймаут + retry на другой узел.
>     - **Откуда путаница:** «не падать с таймаутом» воспринимается как cooperative поведение.
>     - **Если бы это было правдой:** SLO для p99 latency был бы не нужен — но он критический показатель.
> - [ ] D) Убрать медленный бэкенд из пула вручную, после восстановления — вернуть; автоматизация не нужна, так как у нас on-call инженеры
>     - **Что на самом деле:** ручная реакция занимает минуты, за которые клиенты получают тысячи 5xx. Автоматизация (health checks, outlier detection, circuit breaker) — основа SRE.
>     - **Откуда путаница:** в малых командах действительно реагируют вручную, но это не масштабируемо.
>     - **Если бы это было правдой:** не было бы автоматизированных feature-toggle и circuit breaker-ов — но они стандарт.


## Q36. (!) Что такое IP Hash и когда его использовать?

**IP Hash** — алгоритм балансировки, при котором бэкенд выбирается детерминированно на основе `IP`-адреса клиента: `hash(client_ip) % N`. Один и тот же клиент всегда попадает на один и тот же сервер.

```mermaid
graph LR
    C1["Client 1<br/>IP: 10.0.0.1"] -->|"hash(10.0.0.1)%3=0"| S1[Server 1]
    C2["Client 2<br/>IP: 10.0.0.2"] -->|"hash(10.0.0.2)%3=1"| S2[Server 2]
    C3["Client 3<br/>IP: 10.0.0.3"] -->|"hash(10.0.0.3)%3=0"| S1
    C4["Client 4<br/>IP: 10.0.0.4"] -->|"hash(10.0.0.4)%3=2"| S3[Server 3]
```

**Когда применять:**
- Stateful-приложения с локальными сессиями (альтернатива sticky cookies)
- Кэш на уровне бэкенда — один пользователь → один сервер → выше hit rate
- `WebSocket`-соединения (долгоживущие)

**Конфигурация в Nginx:**
```nginx
upstream backend {
    ip_hash;
    server backend1.example.com;
    server backend2.example.com;
    server backend3.example.com;
}
```

**Проблемы:**
- При `NAT` много клиентов выглядят как один IP → перегрузка одного бэкенда
- При добавлении/удалении серверов меняется маппинг (используйте `consistent hashing` как альтернативу)
- Не учитывает реальную нагрузку на сервер

**В HAProxy** аналог — `balance source`:
```haproxy
backend app_servers
    balance source
    hash-type consistent  # consistent hashing вместо modulo
    server app1 10.0.0.1:8080 check
    server app2 10.0.0.2:8080 check
```


> [!mcq] Каков главный риск использования IP Hash для sticky session в публичном API?
>
> - [ ] A) IP Hash медленнее Round Robin из-за вычисления MD5/SHA для каждого запроса — это даёт +10% к latency
>     - **Что на самом деле:** hash IP-адреса вычисляется за наносекунды (это 32-битное число), overhead незаметен. Скорость — не проблема IP Hash.
>     - **Откуда путаница:** «hashing» воспринимается как тяжёлая операция по аналогии с криптохешами.
>     - **Если бы это было правдой:** IP Hash не использовался бы в продакшне — но он используется.
> - [x] **B) NAT и корпоративные прокси: множество клиентов выходят в интернет под одним публичным IP → все они попадают на один бэкенд, создавая хот-споты и неравномерную нагрузку**
>     - **Развёрнутое объяснение:** IP Hash вычисляет `hash(client_ip) % N` и направляет клиента на детерминированный бэкенд. Это работает для уникальных IP. Но в реальности: (а) корпоративные сети используют NAT — все сотрудники одной компании выходят под одним IP, и весь их трафик попадает на один бэкенд; (б) мобильные операторы используют CGNAT (Carrier-Grade NAT) — миллионы абонентов под одним IP; (в) VPN и публичные Wi-Fi точки тоже агрегируют пользователей. В результате один бэкенд получает 30%+ трафика, остальные простаивают.
>     - **Пример:** API популярного SaaS-приложения с IP Hash. Один крупный корпоративный клиент с 5000 сотрудниками выходит под единым IP — все 5000 запросов в секунду оседают на одном из 10 бэкендов. Этот бэкенд становится hot spot и его p99 растёт. При scale-out нагрузка не размазывается.
>     - **Когда применять:** только в специфичных случаях, где предположение «client_ip = уникальный пользователь» выполняется: внутренние корпоративные API без NAT, IoT с уникальными статическими IP, специальные приложения. Для публичного API — НЕ использовать. Лучшие альтернативы: sticky cookie (`Nginx Plus`, `HAProxy cookie SERVERID insert`), session в Redis, consistent hashing по `userId` из JWT.
>     - **Подводные камни:** при добавлении/удалении бэкенда меняется `% N`, и почти все клиенты «переезжают» на другой узел — теряются sticky-сессии массово. Решение — `hash $remote_addr consistent` (consistent hashing вместо modulo) в Nginx или `hash-type consistent` в HAProxy. Это не решает проблему NAT, но смягчает её при scale-out.
>     - **Связанные вопросы:** [[load-balancing-interview#Q4]] про sticky session, [[load-balancing-interview#Q29]] про consistent hashing, [[load-balancing-interview#Q33]] про stateful приложения.
> - [ ] C) IP Hash не работает с IPv6 — поддерживается только IPv4, что делает его несовместимым с современными мобильными сетями
>     - **Что на самом деле:** IP Hash работает с обеими версиями IP (`ip_hash` в Nginx с 1.3.2 поддерживает IPv6). Технических ограничений нет.
>     - **Откуда путаница:** некоторые legacy-конфигурации действительно работали только с IPv4.
>     - **Если бы это было правдой:** мобильные операторы (IPv6-native) не использовали бы Nginx — но это базовый стек.
> - [ ] D) IP Hash раскрывает реальный IP клиента бэкендам, что нарушает GDPR
>     - **Что на самом деле:** IP передаётся через стандартный `X-Forwarded-For` независимо от алгоритма балансировки. IP Hash использует client IP внутри LB для выбора бэкенда — никакого специального «раскрытия» не происходит. GDPR требует обоснованной обработки IP, но не запрещает её.
>     - **Откуда путаница:** GDPR — общая «страшилка», его применяют к любым техническим решениям.
>     - **Если бы это было правдой:** все балансировщики были бы non-compliant — но они законно работают в EU.


## Q37. Что такое алгоритм Power of Two Choices?

**Power of Two Choices** (`P2C`) — вероятностный алгоритм, дающий нагрузку ближе к оптимальному распределению, чем Round Robin, при этом не требующий глобального знания нагрузки.

**Алгоритм:**
1. Случайно выбрать **два** бэкенда из пула
2. Из этих двух выбрать тот, у которого **меньше активных соединений** (или ниже latency)
3. Отправить запрос на выбранный бэкенд

```java
public ServerInstance selectServer(List<ServerInstance> pool) {
    // Случайно выбираем 2 кандидата
    int idx1 = random.nextInt(pool.size());
    int idx2;
    do {
        idx2 = random.nextInt(pool.size());
    } while (idx2 == idx1 && pool.size() > 1);

    ServerInstance s1 = pool.get(idx1);
    ServerInstance s2 = pool.get(idx2);

    // Выбираем с меньшим числом активных соединений
    return s1.activeConnections() <= s2.activeConnections() ? s1 : s2;
}
```

**Почему работает:** математически доказано, что `P2C` даёт максимальную нагрузку `O(log log N)` при N серверах, тогда как случайный выбор даёт `O(log N / log log N)` — существенное улучшение.

**Применение:** `Envoy` (режим `LEAST_REQUEST`), `Nginx Plus`, `HAProxy` (`leastconn` с randomized selection). Хорошо работает в сервис-меш, где у каждого sidecar-proxy есть локальная статистика соединений.


> [!mcq] Почему Power of Two Choices даёт лучшее распределение, чем чисто случайный выбор бэкенда?
>
> - [ ] A) P2C использует криптографически стойкий random для выбора, что обеспечивает абсолютную равномерность распределения
>     - **Что на самом деле:** криптостойкость не нужна — обычный `random()` достаточен. Преимущество P2C не в качестве random, а в учёте текущей нагрузки при выборе из двух кандидатов.
>     - **Откуда путаница:** «better random» часто упоминают в контексте hashing-алгоритмов.
>     - **Если бы это было правдой:** P2C-имплементации использовали бы `SecureRandom` — но они используют обычный `Random` (это hot path).
> - [ ] B) P2C делает round-robin между ВСЕМИ парами серверов — это математически эквивалентно перебору
>     - **Что на самом деле:** P2C не перебирает все пары — он на каждом запросе случайно выбирает только ОДНУ пару (2 из N серверов). Это вероятностный алгоритм.
>     - **Откуда путаница:** название «two choices» наводит на мысль о систематическом переборе.
>     - **Если бы это было правдой:** P2C имел бы сложность O(N²) — но он O(1).
> - [x] **C) Выбор из 2 случайных и взятие менее загруженного даёт максимальную нагрузку O(log log N) против O(log N / log log N) у случайного выбора — математически доказано, что «второй взгляд» драматически снижает hot spot вероятности**
>     - **Развёрнутое объяснение:** при чистом случайном выборе вероятность, что какой-то бэкенд получит больше остальных, растёт как `log N / log log N` (для N серверов). При P2C — снижается до `log log N`, что радикально лучше. Интуиция: даже если первый выбор попал на перегруженный сервер, второй выбор почти наверняка попадёт на другой, и из двух алгоритм выберет менее загруженный. Это «второй шанс», обходящий проблему «случайно несколько раз попасть на тот же». Не требуется глобальное знание нагрузки — только локальная статистика по 2 кандидатам, что подходит для распределённых LB.
>     - **Пример:** в Envoy режим `LEAST_REQUEST` использует P2C: на каждый запрос выбирает 2 случайных endpoint, сравнивает `active_requests` и шлёт на меньший. Параметр `choice_count: 3` (default 2) расширяет до P-of-K. Аналогично в Nginx Plus / HAProxy randomized leastconn. В Java: `int idx1 = random.nextInt(pool.size()); int idx2 = random.nextInt(pool.size()); return pool.get(idx1).activeConnections() <= pool.get(idx2).activeConnections() ? pool.get(idx1) : pool.get(idx2);`.
>     - **Когда применять:** в service mesh (Envoy/Istio) — стандарт; для микросервисов с переменной нагрузкой; вместо обычного Least Connections (P2C дёшевле — не нужно сканировать весь пул при каждом запросе). Особенно хорош для большого N (100+ серверов), где сканирование Least Connections стоит дорого.
>     - **Подводные камни:** P2C требует локальной статистики `active_requests` у балансировщика — для централизованного LB это просто (одна машина видит все), для распределённого sidecar-роя (Envoy) каждый sidecar видит только свои соединения, что слегка снижает оптимальность. При неоднородных серверах (разная мощность) P2C даёт неоптимальное распределение — нужны weights или другой алгоритм.
>     - **Связанные вопросы:** [[load-balancing-interview#Q8]] про Least Connections, [[load-balancing-interview#Q9]] про Least Response Time, [[load-balancing-interview#Q40]] про Envoy/Istio.
> - [ ] D) P2C использует ML-модель для предсказания нагрузки на бэкенды и заранее планирует распределение
>     - **Что на самом деле:** P2C — простой статистический алгоритм без ML. Решение принимается в текущий момент по текущему состоянию двух кандидатов.
>     - **Откуда путаница:** ML-balancing — отдельное направление исследований, и его путают с P2C.
>     - **Если бы это было правдой:** P2C не реализовывался бы в 50 строк кода в Envoy — но он именно такой.


## Q38. (!) Что такое Maglev hashing?

**Maglev** — алгоритм consistent hashing, разработанный в Google для балансировщика нагрузки `Maglev` (используется для Google Search, YouTube, Gmail). Обеспечивает:
- **Равномерное** распределение нагрузки (в отличие от классического consistent hashing)
- **Минимальное перемешивание** при добавлении/удалении серверов
- **Быстрый lookup** через lookup table

**Принцип (упрощённо):**
1. Строится lookup table размером `M` (простое число, >> числа серверов)
2. Каждый сервер вычисляет два хеша: `offset` и `skip`
3. Заполнение таблицы: каждый сервер «претендует» на позиции по формуле `(offset + i*skip) % M`
4. Lookup: `table[hash(flow) % M]`

```mermaid
graph TD
    F[Входящий пакет] -->|"hash(src_ip, dst_ip, src_port, dst_port)"| HV[Hash Value]
    HV -->|"% M (lookup table size)"| LT[Lookup Table]
    LT -->|"server index"| S[Backend Server]
    style LT fill:#51cf66,color:#fff
```

**Сравнение с Ring Consistent Hashing:**

| Критерий | Ring CH | Maglev |
|----------|---------|--------|
| Равномерность | Нет (нужны виртуальные ноды) | Да (встроена) |
| При добавлении сервера | ~1/N трафика перераспределяется | Минимальное перераспределение |
| Скорость lookup | O(log N) | O(1) |
| Использование | `Cassandra`, `Redis Cluster` | Google LB, `Katran` (Facebook) |

**Применение в Open Source:** `Katran` (Facebook/Meta), некоторые реализации `Envoy`.


> [!mcq] Чем Maglev hashing принципиально отличается от классического ring consistent hashing?
>
> - [ ] A) Maglev использует криптографическую хеш-функцию (SHA-256), а ring consistent hashing — нет, поэтому Maglev безопаснее
>     - **Что на самом деле:** обе техники используют обычные нерастущие хеши (MurmurHash, FNV, SipHash). Безопасность — не основной критерий выбора, и оба алгоритма не предназначены для криптографических задач.
>     - **Откуда путаница:** «Google product» = «крутая криптография» — ложная ассоциация.
>     - **Если бы это было правдой:** Maglev был бы медленнее ring CH из-за тяжёлых хешей — но он быстрее (O(1) lookup).
> - [x] **B) Maglev строит фиксированную lookup-таблицу размером M (простое число) с псевдослучайным заполнением, даёт O(1) lookup и встроенную равномерность без vnodes; ring CH использует O(log N) поиск по кольцу и требует vnodes для равномерности**
>     - **Развёрнутое объяснение:** ring consistent hashing размещает точки серверов на круге `[0, 2³²)` по хешу и ищет ближайший к ключу узел через бинарный поиск (O(log N)). Распределение получается неравномерным из-за случайности позиций, для выравнивания добавляют виртуальные узлы (по 100-200 на физический). Maglev строит lookup-таблицу `T[0..M-1]` (M = простое число, ~65537), где каждая позиция указывает на конкретный backend. Заполнение использует два хеша на сервер (`offset`, `skip`) и алгоритм round-robin claim: каждый сервер по очереди претендует на позицию `(offset + i*skip) % M`, занимая её, если она свободна. Lookup: `T[hash(key) % M]` — O(1). Равномерность встроена в алгоритм (каждый сервер получает примерно M/N позиций), без vnodes. При изменении пула таблица пересобирается с минимальным перемещением: в среднем 1% ключей переедут при добавлении одного сервера.
>     - **Пример:** Google Maglev LB (исходный продукт), Katran (Facebook/Meta) на L4 для DDoS-устойчивости и быстрого пакета processing. В Envoy: `lb_policy: MAGLEV` + `consistent_hash_lb_config.table_size: 65537`. На уровне приложения — оригинальный Maglev paper (NSDI 2016) даёт алгоритм заполнения.
>     - **Когда применять:** L4-балансировка с миллионами пакетов в секунду, где важен O(1) lookup; кластеры с частыми изменениями (autoscaling) и требованием минимального reshuffle ключей; CDN edge nodes. Альтернатива ring CH в распределённых БД (`Cassandra`, `DynamoDB`) — там ring выигрывает за счёт простоты.
>     - **Подводные камни:** размер таблицы M влияет на качество распределения и память; рекомендация M = 65537 (простое, ~64K записей × 4 байта = 256KB на пул). При большом числе бэкендов (1000+) построение таблицы становится дорогим — нужно пересчитывать при каждом изменении. Maglev требует одинаковый размер таблицы у всех LB-узлов в кластере для согласованности маршрутизации — это координация при rollout.
>     - **Связанные вопросы:** [[load-balancing-interview#Q29]] про consistent hashing, [[load-balancing-interview#Q37]] про P2C, [[load-balancing-interview#Q40]] про Envoy в Service Mesh.
> - [ ] C) Maglev — это только для L7-роутинга по HTTP-заголовкам, а ring consistent hashing — для L4 IP-based
>     - **Что на самом деле:** оба алгоритма универсальны — работают с любым ключом (IP, header, URI). Maglev изначально создан для L4 (Google sees IP flow), но применим везде.
>     - **Откуда путаница:** Cassandra ring CH работает с partition key — это L7-ассоциация, и обратное переносят на Maglev.
>     - **Если бы это было правдой:** Cassandra не использовала бы ring CH (она L7 для запросов), но использует.
> - [ ] D) Maglev работает только с TCP, а ring consistent hashing — с TCP и UDP
>     - **Что на самом деле:** оба — алгоритмы выбора backend по ключу, независимые от протокола. Используются и для TCP-потоков, и для UDP, и для HTTP-роутинга.
>     - **Откуда путаница:** Maglev в Google применяется для L4 TCP/UDP, и эту специфику обобщают.
>     - **Если бы это было правдой:** Maglev не подходил бы для DNS (UDP) — но Maglev применяют именно для DNS.


## Q39. Что такое Anycast балансировка и когда её применять?

**Anycast** — сетевой паттерн, при котором один и тот же `IP`-адрес анонсируется из нескольких географических точек. Маршрутизаторы направляют трафик к **ближайшему** (по метрике BGP) узлу.

```mermaid
graph TD
    User1["User (EU)"] -->|"Anycast IP: 1.2.3.4"| EU[PoP EU<br/>Frankfurt]
    User2["User (US)"] -->|"Anycast IP: 1.2.3.4"| US[PoP US<br/>Virginia]
    User3["User (APAC)"] -->|"Anycast IP: 1.2.3.4"| APAC[PoP APAC<br/>Singapore]

    EU --> Origin[Origin Servers]
    US --> Origin
    APAC --> Origin
```

**Как работает:**
1. Три дата-центра анонсируют `1.2.3.4` в BGP
2. Маршрутизаторы интернета направляют пакеты к ближайшему `PoP`
3. При отказе одного `PoP` BGP перенаправляет трафик автоматически

**Когда применять:**
- `CDN` и глобальные сети доставки (Cloudflare, Fastly используют anycast)
- `DNS`-серверы (root DNS работает на anycast)
- `DDoS` mitigation — трафик распределяется по всем PoP
- Глобальные API с требованием минимальной latency

**Ограничения:**
- Не подходит для stateful TCP-сессий (failover = разрыв соединения)
- Нет гарантии, что один клиент всегда попадёт на один PoP (маршруты BGP могут меняться)
- Сложнее отлаживать — куда ушёл конкретный запрос?

**Сравнение с GeoDNS:**
- `GeoDNS` — TTL-based, возможна задержка failover (TTL несколько минут)
- `Anycast` — failover за секунды (BGP конвергенция)


> [!mcq] Почему Anycast не подходит для долгоживущих TCP-сессий, а CDN-трафик через него работает прекрасно?
>
> - [ ] A) Anycast не поддерживает TCP вообще — он работает только с UDP-протоколами (DNS, QUIC)
>     - **Что на самом деле:** Anycast — это про IP-роутинг, он работает с любым протоколом поверх IP (TCP, UDP, ICMP). HTTPS через Cloudflare идёт через Anycast IP и работает поверх TCP.
>     - **Откуда путаница:** root DNS на Anycast — самый известный пример, и его обобщают.
>     - **Если бы это было правдой:** Cloudflare не работал бы для HTTPS — но он работает.
> - [x] **B) BGP-маршрутизация может перенаправить пакеты на другой PoP при изменении сетевой топологии (link flap, новый peering), и существующая TCP-сессия попадёт на узел без её состояния → reset; CDN-трафик короткий (HTTP запрос-ответ), поэтому переключение не критично**
>     - **Развёрнутое объяснение:** Anycast — это анонс одного IP из нескольких локаций через BGP. Маршрутизаторы интернета направляют пакеты к «лучшему» PoP по BGP-метрикам. Но «лучший» может измениться в любой момент: link flap у провайдера, новый peering, BGP-конвергенция после отказа. Если у клиента уже была установлена TCP-сессия с PoP-A, а маршрут вдруг переключился на PoP-B, то PoP-B не знает про эту сессию (нет shared state TCP) — он ответит RST, и клиент потеряет соединение. Для коротких HTTP-запросов это редкое явление и легко переживается (browser retry). Для долгоживущих TCP (WebSocket, SSH, длинные gRPC streams) — серьёзная проблема, нужны другие техники (sticky routing через L7 LB после Anycast L3).
>     - **Пример:** Cloudflare Anycast обрабатывает короткие HTTPS-запросы — пользователь зашёл, получил страницу, переключился — никаких проблем. WebSocket через Anycast Cloudflare работает за счёт того, что Cloudflare поддерживает sticky session внутри своей сети через session ID, не на чистом Anycast. Игровой сервер (UDP, real-time) на чистом Anycast будет терять «соединения» периодически.
>     - **Когда применять Anycast:** CDN (короткий трафик); DNS (UDP, отдельные запросы); DDoS mitigation (распределение по PoP); BGP-failover за секунды (быстрее, чем DNS TTL). Когда НЕ применять: stateful long-lived TCP (Database connections, SSH-туннели, WebSocket без recovery), gRPC bidirectional streams.
>     - **Подводные камни:** отладка Anycast сложна — куда именно ушёл запрос? Через `traceroute` к Anycast IP видишь маршрут, но он может меняться. Анонсы BGP требуют контроля над сетью — не доступны в стандартном облаке, нужен `AWS Global Accelerator` (managed Anycast) или собственный AS. Anycast-IP — это публичный ресурс, требующий координации с провайдерами.
>     - **Связанные вопросы:** [[load-balancing-interview#Q20]] про HA, [[load-balancing-interview#Q26]] про DNS LB (сравнение с Anycast), [[load-balancing-interview#Q27]] про geographic LB.
> - [ ] C) Anycast несовместим с TLS из-за SNI — каждое соединение требует пересогласования сертификата
>     - **Что на самом деле:** все Anycast-PoP имеют одинаковые сертификаты для домена — TLS-handshake работает прозрачно. SNI содержит hostname, и любой PoP отдаёт правильный сертификат.
>     - **Откуда путаница:** распределённые сертификаты ассоциируются с проблемами sync, но Cloudflare/Fastly давно эту задачу решают.
>     - **Если бы это было правдой:** HTTPS через Cloudflare не работал бы — но это базовая фича.
> - [ ] D) Anycast хуже для CDN, чем GeoDNS — потому что BGP-маршрутизация не учитывает реальную latency
>     - **Что на самом деле:** BGP-маршрутизация выбирает «ближайший» PoP по числу хопов, но GeoDNS тоже неточен (IP-base geolocation). На практике Anycast чаще быстрее GeoDNS, потому что failover за секунды (vs минут с TTL).
>     - **Откуда путаница:** «GeoDNS точнее» — устаревшее утверждение из эпохи до Cloudflare-сетей.
>     - **Если бы это было правдой:** Cloudflare/Fastly использовали бы только GeoDNS — но они используют Anycast.


## Q40. Как балансировка нагрузки реализована в Service Mesh (Istio/Envoy)?

В `Service Mesh` балансировка происходит на уровне **sidecar-proxy** (`Envoy`) рядом с каждым подом, а не на централизованном балансировщике. Это даёт более гибкий контроль и L7-фичи без изменения кода приложения.

```mermaid
graph LR
    subgraph "Pod A"
        A[App A] --> EA[Envoy sidecar]
    end
    subgraph "Pod B1"
        EA --> B1[App B]
        B1 --> EB1[Envoy sidecar]
    end
    subgraph "Pod B2"
        EA --> B2[App B]
        B2 --> EB2[Envoy sidecar]
    end
    CP[Control Plane<br/>Istio/xDS] -.->|"конфигурация"| EA
    CP -.-> EB1
    CP -.-> EB2
```

**Алгоритмы балансировки в Envoy:**

| Алгоритм | Описание |
|----------|---------|
| `ROUND_ROBIN` | По умолчанию, равномерно |
| `LEAST_REQUEST` | Power of Two Choices (P2C) |
| `RANDOM` | Случайный выбор |
| `RING_HASH` | Consistent hashing по header/cookie |
| `MAGLEV` | Maglev hashing (stable mapping) |

**Конфигурация через Istio DestinationRule:**

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: order-service
spec:
  host: order-service
  trafficPolicy:
    loadBalancer:
      simple: LEAST_REQUEST
    connectionPool:
      tcp:
        maxConnections: 100
      http:
        h2UpgradePolicy: UPGRADE
    outlierDetection:
      consecutive5xxErrors: 5
      interval: 10s
      baseEjectionTime: 30s
      maxEjectionPercent: 50
```

**Преимущества Service Mesh перед классическим LB:**
- Балансировка с учётом реальной latency (не только соединения)
- `Circuit breaker` и `outlier detection` из коробки
- `Traffic shifting` для canary/blue-green без изменения кода
- `Retries` с backoff на уровне proxy


> [!mcq] Чем балансировка в Service Mesh (Istio/Envoy) принципиально лучше классического централизованного балансировщика?
>
> - [ ] A) Service Mesh заменяет балансировщик на DNS-резолюцию — нет дополнительного hop, поэтому latency ниже
>     - **Что на самом деле:** в Service Mesh ВСЕГДА есть sidecar-proxy (`Envoy`) рядом с каждым подом — это локальный hop через `localhost`, не «отсутствие hop». Преимущество — не в исключении hop, а в распределённости и фичах sidecar-proxy.
>     - **Откуда путаница:** клиентская балансировка (Spring Cloud LoadBalancer) действительно без hop, и её путают с Service Mesh.
>     - **Если бы это было правдой:** Istio не имел бы `Envoy` sidecar-контейнеров — но именно они исполняют LB-логику.
> - [ ] B) Service Mesh использует ML-балансировку, предсказывая будущую нагрузку, чего нет в Nginx/HAProxy
>     - **Что на самом деле:** Envoy/Istio используют стандартные алгоритмы (Round Robin, Least Request/P2C, Random, Ring Hash, Maglev). Никакого ML на уровне выбора endpoint нет.
>     - **Откуда путаница:** «mesh» ассоциируется с сетевыми эффектами и предсказательными моделями.
>     - **Если бы это было правдой:** в `DestinationRule.trafficPolicy.loadBalancer` были бы ML-параметры — но там простой `simple: LEAST_REQUEST`.
> - [ ] C) Service Mesh работает только на L4 (TCP) и поэтому быстрее всех L7-балансировщиков
>     - **Что на самом деле:** Envoy — это L7-балансировщик (понимает HTTP/1.1, HTTP/2, gRPC), и он не быстрее raw L4-NLB. Преимущество — функциональность (retries, circuit breaker, outlier detection), не сырой throughput.
>     - **Откуда путаница:** sidecar-proxy воспринимается как «лёгкий», и приписывается ему свойства L4.
>     - **Если бы это было правдой:** Envoy не поддерживал бы HTTP-header-based routing — но это его базовая фича.
> - [x] **D) Балансировка происходит локально в sidecar-proxy (Envoy) рядом с клиентом, что даёт: L7-балансировку с учётом latency (P2C, LEAST_REQUEST), встроенный circuit breaker и outlier detection, traffic shifting для canary без кода, retries с backoff — всё через декларативные DestinationRule, без изменений в приложении**
>     - **Развёрнутое объяснение:** в классической архитектуре с централизованным LB (Nginx/HAProxy/ALB): все запросы идут через один балансировщик, который видит только часть картины (соединения с собой), а circuit breaker, retries, outlier detection — это либо в приложении (Resilience4j), либо в LB (но тогда они одинаковы для всех клиентов). В Service Mesh каждый Pod имеет свой Envoy-sidecar, который: (а) знает локальный пул бэкендов через xDS API из Istio Control Plane; (б) балансирует с учётом реальной latency через P2C (LEAST_REQUEST); (в) автоматически делает retries с exponential backoff на 5xx; (г) исключает «больные» endpoints через `outlierDetection.consecutive5xxErrors`; (д) реализует canary/blue-green через `VirtualService.http.route[].weight` без изменения приложения. Всё это конфигурируется CRD'ами (`DestinationRule`, `VirtualService`), которые применяются на лету и через Control Plane (Istiod) распространяются на все sidecar'ы.
>     - **Пример:** `DestinationRule: loadBalancer.simple: LEAST_REQUEST + outlierDetection.consecutive5xxErrors: 5, baseEjectionTime: 30s` — Envoy балансирует с P2C, исключает endpoint на 30 секунд после 5 пятисоток подряд. `VirtualService: http: - route: - destination: subset: v1 weight: 90 - destination: subset: v2 weight: 10` — canary 10% на v2.
>     - **Когда применять:** для микросервисной архитектуры в Kubernetes (10+ сервисов с межсервисными вызовами), где нужен единый подход к resilience и observability без копирования кода. Альтернатива — клиентские библиотеки (Resilience4j, Spring Cloud) с теми же фичами, но требующими разработки и поддержки.
>     - **Подводные камни:** overhead — каждый Pod получает дополнительный контейнер (Envoy ~30MB RAM, ~5-10% CPU). При большом числе подов это существенно. Istio Control Plane (Istiod) — отдельный сервис, требует мониторинга. Конфигурация сложнее, чем простой Nginx: нужно понимать xDS, разделение `VirtualService`/`DestinationRule`, `ServiceEntry` для external traffic. Для маленьких систем (1-5 сервисов) Istio — overkill.
>     - **Связанные вопросы:** [[load-balancing-interview#Q16]] про клиентскую vs серверную балансировку, [[load-balancing-interview#Q37]] про P2C, [[load-balancing-interview#Q30]] про gRPC (где Service Mesh особенно выигрывает).


---

## See also

- [Паттерны масштабируемости](scalability-patterns-interview.md) — горизонтальное масштабирование и autoscaling
- [Микросервисы](microservices-interview.md) — service discovery и межсервисная балансировка
- [Распределённые системы](distributed-systems-interview.md) — репликация и отказоустойчивость за балансировщиком
- [Kubernetes](../devops/kubernetes-interview.md) — Ingress, Service и балансировка в кластере
- [Стратегии кэширования](caching-strategies-interview.md) — снижение нагрузки на бэкенды через кэширование
- [Spring Cloud](../frameworks/spring/spring-cloud-interview.md) — Spring Cloud LoadBalancer и интеграция со Service Discovery
- [Паттерны отказоустойчивости](resilience-patterns-interview.md) — Circuit Breaker и health check при балансировке
- [API Gateway](api-gateway-interview.md) — централизованная точка входа для микросервисов
- [BFF Pattern](bff-pattern-interview.md) — Backend-for-Frontend и его место за балансировщиком
- [CAP-теорема](cap-theorem-interview.md) — компромиссы согласованности и доступности
- [Clean Architecture](clean-architecture-interview.md) — слои приложения за балансировщиком
- [Паттерны согласованности](consistency-patterns-interview.md) — eventual consistency между регионами

