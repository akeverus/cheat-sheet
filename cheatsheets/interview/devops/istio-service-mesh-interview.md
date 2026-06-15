---
title: "Вопросы на собеседовании: Istio Service Mesh"
description: "Istio: service mesh для K8s, sidecar Envoy, traffic management (VirtualService, DestinationRule), security (mTLS, authz), observability, ambient mode (sidecar-less)"
tags:
  - interview
  - devops
  - istio-service-mesh-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Istio Service Mesh"
  - "Istio interview"
  - "Istio собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Istio Service Mesh`

`Istio` — самый популярный service mesh для Kubernetes. Создан Google, IBM, Lyft (2017). Использует **Envoy proxy** как sidecar. Даёт **управление трафиком, безопасность (mTLS), наблюдаемость** без изменения кода приложения. Альтернативы: Linkerd (проще), Consul Connect (мультиплатформенный), Cilium Service Mesh (на eBPF).

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Istio Documentation](https://istio.io/latest/docs/)
- [Envoy Proxy Documentation](https://www.envoyproxy.io/docs)
- [Istio Architecture](https://istio.io/latest/docs/ops/deployment/architecture/)
- [Istio Ambient Mode](https://istio.io/latest/docs/ambient/)
- [CNCF Service Mesh Landscape](https://landscape.cncf.io/category=service-mesh)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое service mesh?](#q1--что-такое-service-mesh)
- [Q2. (!) Что такое Istio?](#q2--что-такое-istio)
- [Q3. Как устроена архитектура Istio (control plane vs data plane)?](#q3-как-устроена-архитектура-istio-control-plane-vs-data-plane)
- [Q4. Что такое sidecar-паттерн и зачем тут Envoy?](#q4-что-такое-sidecar-паттерн-и-зачем-тут-envoy)

**Installation**
- [Q5. (!) Какие профили установки есть у Istio?](#q5--какие-профили-установки-есть-у-istio)
- [Q6. Чем отличается авто-инъекция sidecar от ручной?](#q6-чем-отличается-авто-инъекция-sidecar-от-ручной)

**Traffic management**
- [Q7. (!) Что такое VirtualService?](#q7--что-такое-virtualservice)
- [Q8. (!) Что такое DestinationRule?](#q8--что-такое-destinationrule)
- [Q9. Зачем нужен Gateway?](#q9-зачем-нужен-gateway)
- [Q10. (!) Как делать canary и blue-green через traffic splitting?](#q10--как-делать-canary-и-blue-green-через-traffic-splitting)
- [Q11. Как настроить retries, timeouts и circuit breaking?](#q11-как-настроить-retries-timeouts-и-circuit-breaking)
- [Q12. Что такое fault injection и зачем он нужен?](#q12-что-такое-fault-injection-и-зачем-он-нужен)
- [Q13. Что такое mirroring (shadow traffic)?](#q13-что-такое-mirroring-shadow-traffic)

**Security**
- [Q14. (!) Как работает mutual TLS (mTLS) в Istio?](#q14--как-работает-mutual-tls-mtls-в-istio)
- [Q15. (!) Что такое Authorization Policies?](#q15--что-такое-authorization-policies)
- [Q16. Чем различаются PeerAuthentication и RequestAuthentication?](#q16-чем-различаются-peerauthentication-и-requestauthentication)
- [Q17. Как Istio валидирует JWT-токены?](#q17-как-istio-валидирует-jwt-токены)

**Observability**
- [Q18. (!) Какие метрики Istio отдаёт в Prometheus?](#q18--какие-метрики-istio-отдаёт-в-prometheus)
- [Q19. Как устроен distributed tracing (Jaeger, Tempo)?](#q19-как-устроен-distributed-tracing-jaeger-tempo)
- [Q20. Что такое access logs в Istio и чем за них платят?](#q20-что-такое-access-logs-в-istio-и-чем-за-них-платят)
- [Q21. Зачем нужен Kiali?](#q21-зачем-нужен-kiali)

**Ambient mode**
- [Q22. (!) Что такое ambient mode и почему он без sidecar?](#q22--что-такое-ambient-mode-и-почему-он-без-sidecar)
- [Q23. Что делают ztunnel и waypoint proxy?](#q23-что-делают-ztunnel-и-waypoint-proxy)

**Production**
- [Q24. (!) Чем Istio отличается от Linkerd и Consul Connect?](#q24--чем-istio-отличается-от-linkerd-и-consul-connect)
- [Q25. Какие у Istio минусы?](#q25-какие-у-istio-минусы)
- [Q26. Когда стоит и когда не стоит использовать service mesh?](#q26-когда-стоит-и-когда-не-стоит-использовать-service-mesh)

## Q1. (!) Что такое service mesh?

**Service mesh** — это выделенный инфраструктурный слой, который берёт на себя всю сетевую коммуникацию между сервисами (service-to-service). Логика связи — повторы, шифрование, маршрутизация — выносится из кода приложения в инфраструктуру.

Идея в том, что в микросервисной системе каждый сервис вынужден сам решать одни и те же задачи: ретраи при сбоях, таймауты, mTLS, сбор метрик. Mesh забирает эти задачи себе, чтобы код приложения занимался только бизнес-логикой.

**Что даёт mesh:**
- **Управление трафиком** — маршрутизация, балансировка нагрузки, повторы (retries), таймауты
- **Безопасность** — mTLS, авторизация между сервисами
- **Наблюдаемость** — метрики, трейсы, логи из коробки, без инструментации кода

**Как устроен:** sidecar-прокси (по одному рядом с каждым pod) перехватывает трафик сервиса, а control plane централизованно раздаёт прокси конфигурацию.

Путь вызова из `App A` в `App B` проходит через цепочку прокси:

- `App A` соединён со своим `Envoy Sidecar`
- этот sidecar связан с `Envoy Sidecar` целевого pod-а
- тот, в свою очередь, отдаёт трафик в `App B`

Над этой цепочкой стоит **Control Plane (Istiod)** — он связан (управляющим каналом, не на пути запросов) с каждым из sidecar-ов и раздаёт им конфигурацию.

**Прозрачно для кода** — приложение отправляет обычный HTTP/gRPC-запрос и не знает, что его перехватывает sidecar. Никаких библиотек и SDK подключать не нужно.

**Компромисс:** богатая функциональность достаётся ценой эксплуатационной сложности и дополнительного хопа через прокси на каждом вызове.

## Q2. (!) Что такое Istio?

**Istio** — самая популярная open-source-реализация service mesh для Kubernetes (с 2017 года, создан Google, IBM и Lyft). Это конкретный продукт, реализующий концепцию mesh из Q1.

**Из чего состоит:**
- **Envoy proxy** (data plane) — sidecar в каждом pod, гоняет реальный трафик
- **Istiod** (control plane) — единый управляющий компонент, который конфигурирует все Envoy-прокси

**Что Istio закрывает на практике:**
- Маршрутизация между сервисами для безопасных раскаток (canary, blue-green)
- Безопасность по принципу zero-trust (mTLS между всеми сервисами, авторизация)
- Наблюдаемость (метрики, трейсы, логи) без правки кода
- Устойчивость к сбоям (retries, circuit breaker, таймауты)

**Главный компромисс:** Istio даёт больше всего возможностей среди всех mesh, но за это платят самой высокой среди аналогов сложностью эксплуатации.

## Q3. Как устроена архитектура Istio (control plane vs data plane)?

Istio разделён на два слоя: **data plane** обрабатывает сам трафик, **control plane** этим трафиком управляет. Ключевое: control plane НЕ стоит на пути запросов — он только раздаёт конфигурацию, поэтому его сбой не рвёт уже идущий трафик.

**Data plane** — это сами прокси:
- Sidecar-прокси **Envoy** (по одному на pod)
- Гоняют реальный трафик сервиса (входящий и исходящий)
- Работают и на L4, и на L7

**Control plane (Istiod)** — мозг системы:
- Конфигурирует Envoy-прокси, раздавая им конфиг (push)
- Service discovery — берёт список сервисов из K8s API
- Выступает удостоверяющим центром (CA), выпуская mTLS-сертификаты
- Преобразует конфигурационные CRD (VirtualService и т.д.) в настройки Envoy

Связи между компонентами по порядку:

- `K8s API` → `Istiod (Control Plane)` — Istiod берёт из K8s API список сервисов
- `Istiod` → каждый из `Envoy Sidecar` — раздаёт им конфигурацию (config push)
- сами sidecar-ы (`Envoy Sidecar 1` ↔ `Envoy Sidecar 2` ↔ `Envoy Sidecar 3`) связаны между собой — это data plane, по которому идёт реальный трафик

**Историческая деталь:** до версии 1.5 control plane состоял из нескольких отдельных компонентов (Pilot, Citadel, Galley). Их объединили в один бинарник Istiod — это резко упростило установку и эксплуатацию, и на собеседовании про это часто спрашивают.

## Q4. Что такое sidecar-паттерн и зачем тут Envoy?

**Sidecar** — это паттерн, при котором рядом с контейнером приложения в том же pod запускают вспомогательный контейнер-прокси. Istio использует в роли sidecar **Envoy** — высокопроизводительный L7-прокси от Lyft (выпускник CNCF).

Почему именно sidecar, а не общий прокси на ноду: прокси в том же pod разделяет с приложением сетевой namespace, поэтому может перехватить весь трафик через iptables, и при этом изоляция между pod-ами сохраняется.

**Как работает sidecar в Istio:**
- Внедряется в каждый pod (как именно — см. Q6)
- Перехватывает ВЕСЬ трафик pod-а: и входящий, и исходящий
- Применяет к этому трафику политики Istio (маршрутизация, mTLS, лимиты)

```yaml
# Pod после injection
containers:
  - name: my-app
    image: my-app:1.0
  - name: istio-proxy   # injected automatically
    image: docker.io/istio/proxyv2:1.20
```

**Поток трафика:**
```
External → Pod IP → Envoy → App container
App → Envoy → External service
```

**Цена sidecar-подхода:** ~50-100 МБ RAM на каждый sidecar (а sidecar-ов столько же, сколько pod-ов) и +1-5 мс задержки на каждый хоп. Именно из-за этой цены появился ambient mode (Q22).

## Q5. (!) Какие профили установки есть у Istio?

```bash
istioctl install --set profile=demo  # quick start
istioctl install --set profile=default  # production base
istioctl install --set profile=minimal  # only Istiod
istioctl install --set profile=ambient  # ambient mode
```

Профиль — это готовый набор включённых компонентов под конкретный сценарий. Главное правило: для production берут **default**, а не demo, потому что demo включает всё подряд и небезопасен.

**Профили:**
- **default** — рекомендуемая база для production (минимум нужного)
- **demo** — все возможности включены, удобно для ознакомления, но не для прода
- **minimal** — только Istiod, без gateway-ов
- **empty** — только CRD, всё остальное устанавливаешь сам
- **ambient** — режим без sidecar (новее, см. Q22)

**Тонкая настройка:** если стандартного профиля мало, конфигурацию задают через CRD `IstioOperator`.

## Q6. Чем отличается авто-инъекция sidecar от ручной?

Sidecar нужно как-то добавить в pod. Есть два способа — автоматический (по метке на namespace) и ручной (через `istioctl`). На практике почти всегда используют авто-инъекцию.

**Авто-инъекция (рекомендуется):**
```bash
kubectl label namespace default istio-injection=enabled
```

После этой метки все новые pod-ы в namespace получают sidecar **автоматически** — за инъекцию отвечает mutating admission webhook Istio, который перехватывает создание pod-а и дописывает контейнер прокси. На уже запущенные pod-ы метка не действует, их нужно пересоздать.

**Ручная инъекция** — `istioctl` дописывает sidecar в манифест ещё до применения:
```bash
istioctl kube-inject -f deploy.yaml | kubectl apply -f -
```

**Как трафик попадает в Envoy:** вместе с прокси в pod добавляется init-контейнер, который правит iptables и перенаправляет весь входящий/исходящий трафик через Envoy. Поэтому приложению не нужно ничего знать о mesh.

**Отключить для конкретного pod:**
```yaml
metadata:
  annotations:
    sidecar.istio.io/inject: "false"
```

## Q7. (!) Что такое VirtualService?

**VirtualService** — CRD, который описывает, КУДА направить запрос: правила маршрутизации трафика к сервису. По сути это «if-цепочка» для трафика: при каком условии в какой destination его отправить.

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: reviews
spec:
  hosts:
    - reviews
  http:
    - match:
        - headers:
            user-agent:
              regex: ".*Chrome.*"
      route:
        - destination:
            host: reviews
            subset: v2
    - route:
        - destination:
            host: reviews
            subset: v1
```

В примере выше запросы с заголовком `user-agent`, содержащим `Chrome`, идут на версию v2, а все остальные — на v1.

**По каким признакам можно маршрутизировать:**
- По пути URL (`/api/v1/*`)
- По HTTP-заголовкам (как в примере)
- По HTTP-методу
- По весам — несколько destination с долями процентов (основа canary, см. Q10)

**Важно:** VirtualService решает «куда», но сами версии-подмножества (`subset: v1/v2`) определяются в DestinationRule (Q8). Эти два CRD почти всегда работают в паре.

## Q8. (!) Что такое DestinationRule?

**DestinationRule** — CRD, который описывает, КАК обращаться с трафиком уже ПОСЛЕ того, как VirtualService выбрал destination: политики для целевого сервиса. Здесь же определяются именованные подмножества (subsets), на которые ссылается VirtualService.

```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: reviews
spec:
  host: reviews
  trafficPolicy:
    loadBalancer:
      simple: LEAST_REQUEST
    connectionPool:
      tcp:
        maxConnections: 100
      http:
        http1MaxPendingRequests: 50
    outlierDetection:
      consecutive5xxErrors: 5
      interval: 30s
      baseEjectionTime: 30s
  subsets:
    - name: v1
      labels:
        version: v1
    - name: v2
      labels:
        version: v2
```

**Что задаётся в DestinationRule:**
- **Subsets** — именованные версии сервиса (v1, v2) по меткам pod-ов; на них ссылается VirtualService
- Стратегию балансировки нагрузки (round-robin, least-request и т.д.)
- Лимиты пула соединений (connection pool) — защита от перегрузки бэкенда
- Outlier detection — пассивная проверка здоровья: прокси сам исключает инстансы, начавшие отдавать 5xx (это и есть circuit breaking, см. Q11)
- Настройки TLS, включая mTLS

**Эмпирическое правило для запоминания пары:** VirtualService = «куда направить» (маршрутизация), DestinationRule = «как обходиться по прибытии» (балансировка, лимиты, версии).

## Q9. Зачем нужен Gateway?

**Gateway** — CRD, который управляет трафиком на ГРАНИЦЕ mesh: входящим из внешнего мира (ingress) или исходящим наружу (egress). VirtualService и DestinationRule рулят трафиком внутри mesh, а Gateway — точка входа снаружи.

```yaml
apiVersion: networking.istio.io/v1beta1
kind: Gateway
metadata:
  name: my-gateway
spec:
  selector:
    istio: ingressgateway
  servers:
    - port:
        number: 443
        name: https
        protocol: HTTPS
      tls:
        mode: SIMPLE
        credentialName: my-tls-cert
      hosts:
        - api.example.com
```

**Gateway сам по себе ничего не маршрутизирует** — он только открывает порт и терминирует TLS. Куда дальше пойдёт принятый трафик, решает связанный VirtualService: в нём нужно указать `gateways: [my-gateway]`.

**Чем отличается от Kubernetes Ingress:** Gateway заменяет стандартный Ingress, но даёт полный набор возможностей Istio (тонкая маршрутизация, mTLS, политики) для трафика, входящего в mesh.

## Q10. (!) Как делать canary и blue-green через traffic splitting?

Суть canary: новую версию выкатывают не сразу всем, а сначала пускают на неё небольшую долю трафика и смотрят на метрики. Делается это весами (`weight`) в VirtualService.

**Canary по весам** — 10% трафика на новую версию v2:
```yaml
http:
  - route:
      - destination:
          host: reviews
          subset: v1
        weight: 90
      - destination:
          host: reviews
          subset: v2
        weight: 10
```

**Постепенный сдвиг:** если метрики v2 в норме, веса плавно меняют 90/10 → 50/50 → 0/100. Blue-green — частный случай: переключение сразу 100/0 → 0/100 без промежуточных шагов.

**Canary по заголовку** — направить на новую версию только определённых пользователей (например, внутренних тестировщиков), а не случайную долю:
```yaml
http:
  - match:
      - headers:
          x-canary:
            exact: "true"
    route:
      - destination:
          host: reviews
          subset: v2
  - route:
      - destination:
          host: reviews
          subset: v1
```

Главная ценность — раскатки становятся безопасными: можно откатиться, просто поменяв веса, без пересборки и редеплоя.

**Автоматизация:** руками двигать веса на каждом шаге не нужно — Argo Rollouts и Flagger делают это сами, сверяясь с метриками (частота ошибок, задержка) и откатываясь при деградации.

## Q11. Как настроить retries, timeouts и circuit breaking?

Это три механизма устойчивости к сбоям, и все три настраиваются конфигом, без единой строчки в коде приложения. Retries и timeouts задаются в VirtualService, circuit breaking — в DestinationRule.

**Retries** — повтор запроса при сбое:
```yaml
http:
  - route:
      - destination: { host: reviews }
    retries:
      attempts: 3
      perTryTimeout: 2s
      retryOn: 5xx,gateway-error,connect-failure
```

**Timeouts** — максимальное время ожидания ответа, после которого запрос обрывается:
```yaml
http:
  - route:
      - destination: { host: reviews }
    timeout: 10s
```

**Circuit breaking** настраивается через `outlierDetection` в DestinationRule (Q8): прокси сам выкидывает из балансировки инстансы, которые начали сыпать ошибками, и тем самым не даёт сбою одного бэкенда положить остальные.

**Главное преимущество:** вся отказоустойчивость живёт в конфиге CRD, поэтому её можно менять на лету и единообразно для всех языков сервисов.

## Q12. Что такое fault injection и зачем он нужен?

**Fault injection** — это намеренное внесение отказов (задержек и ошибок) в трафик для chaos-тестирования. Цель — заранее проверить, как система ведёт себя при сбоях, пока они управляемы, а не ждать настоящего инцидента.

```yaml
http:
  - fault:
      delay:
        percentage:
          value: 10
        fixedDelay: 5s
      abort:
        percentage:
          value: 5
        httpStatus: 500
    route:
      - destination: { host: reviews }
```

**Эффект конфига выше:** 10% запросов искусственно задерживаются на 5 с, 5% получают ошибку 500 — и всё это без правки сервиса.

**Сценарий применения:** убедиться, что клиент корректно отрабатывает ретраи и таймауты (Q11) и не падает при ошибках бэкенда. Фактически это тест отказоустойчивости, настроенной в Q11.

## Q13. Что такое mirroring (shadow traffic)?

**Mirroring** — копирование боевого трафика на дополнительный destination, при этом ответы от него отбрасываются. Пользователь получает ответ только от основной версии, а копия идёт «в тень» на тестируемую.

```yaml
http:
  - route:
      - destination:
          host: reviews
          subset: v1
    mirror:
      host: reviews
      subset: v2
    mirrorPercentage:
      value: 100.0
```

- **v1** получает основной трафик, и именно его ответ уходит пользователю.
- **v2** получает зеркальную копию запросов, но его ответы игнорируются.

**Сценарий применения:** обкатать новую версию на реальном боевом трафике и нагрузке, ничем не рискуя для пользователей. В отличие от canary (Q10), где пользователи реально ходят на новую версию, здесь они вообще её не видят.

## Q14. (!) Как работает mutual TLS (mTLS) в Istio?

**mTLS** — взаимный TLS: не только клиент проверяет сертификат сервера (как в обычном HTTPS), но и сервер проверяет сертификат клиента. То есть обе стороны доказывают друг другу, кто они.

**Почему в Istio это удобно:** обычно mTLS — это боль с раздачей и ротацией сертификатов. Istio делает это полностью автоматически:
- Сертификаты **выпускаются автоматически** — Istiod выступает удостоверяющим центром (CA)
- **Ротация тоже автоматическая** (короткий TTL — часы), скомпрометированный сертификат живёт недолго
- **Идентичность сервиса = его service account** и проверяется криптографически, а не по IP, который легко подделать

```yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
spec:
  mtls:
    mode: STRICT  # require mTLS
```

**Режимы (PeerAuthentication):**
- `STRICT` — принимать только mTLS, незашифрованный трафик отклоняется
- `PERMISSIVE` — принимать и mTLS, и обычный трафик; это режим для миграции, чтобы не сломать ещё не подключённые к mesh сервисы
- `DISABLE` — mTLS выключен

**Эмпирическое правило миграции:** сначала включают `PERMISSIVE` на весь mesh, дожидаются, пока весь трафик пойдёт по mTLS, и только потом переключают на `STRICT`. В итоге получается сеть по принципу zero-trust — каждый сервис аутентифицирован.

## Q15. (!) Что такое Authorization Policies?

**AuthorizationPolicy** отвечает на вопрос «кому что МОЖНО» и стоит на уровень выше аутентификации. mTLS (Q14) лишь подтверждает, КТО обращается; авторизация решает, разрешено ли этому «кому» дёргать конкретный метод и путь.

```yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: web-only
spec:
  selector:
    matchLabels:
      app: api
  rules:
    - from:
        - source:
            principals: ["cluster.local/ns/default/sa/web"]
      to:
        - operation:
            methods: ["GET"]
            paths: ["/api/*"]
```

**Эффект конфига выше:** сервис `api` принимает только GET-запросы на `/api/*` и только от service account `web`. Всё остальное отклоняется.

**Паттерн default-deny** — рекомендуемый для zero-trust: сначала запретить вообще всё пустой политикой, а потом точечно разрешать нужное явными правилами Allow.
```yaml
spec:
  {}  # empty → deny all
```
Так нельзя случайно оставить открытый доступ: по умолчанию закрыто, открывается только то, что описано явно.

## Q16. Чем различаются PeerAuthentication и RequestAuthentication?

Это два разных слоя аутентификации, которые легко перепутать. Простая разбивка:
- **PeerAuthentication** аутентифицирует СЕРВИС-отправитель — настраивает mTLS на уровне workload (тот самый STRICT/PERMISSIVE из Q14).
- **RequestAuthentication** аутентифицирует КОНЕЧНОГО ПОЛЬЗОВАТЕЛЯ — проверяет JWT-токены во входящих запросах.

```yaml
apiVersion: security.istio.io/v1beta1
kind: RequestAuthentication
metadata:
  name: jwt-auth
spec:
  jwtRules:
    - issuer: "https://auth.example.com"
      jwksUri: "https://auth.example.com/.well-known/jwks.json"
```

**Эти два слоя дополняют друг друга:** peer-аутентификация (сервис-сервис) плюс request-аутентификация (пользователь) вместе дают полноценный zero-trust.

## Q17. Как Istio валидирует JWT-токены?

Валидация JWT в Istio — это связка двух CRD: один проверяет подпись токена, второй принимает решение на основе его claims. Разделение неслучайное: `RequestAuthentication` только подтверждает, что токен подлинный, но сам по себе ничего не запрещает — без `AuthorizationPolicy` запросы вообще без токена тоже пройдут.

```yaml
# RequestAuthentication validates JWT signature
apiVersion: security.istio.io/v1beta1
kind: RequestAuthentication
metadata:
  name: jwt
spec:
  jwtRules:
    - issuer: "https://auth.example.com"
      jwksUri: "https://auth.example.com/.well-known/jwks.json"

# AuthorizationPolicy uses JWT claims
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: require-jwt
spec:
  rules:
    - from:
        - source:
            requestPrincipals: ["*"]  # any authenticated
    - when:
        - key: request.auth.claims[role]
          values: ["admin"]
```

**Где происходит проверка:** прямо в Envoy, ещё до того как запрос дойдёт до приложения. Поэтому быстро и без единой строчки auth-кода в сервисе.

## Q18. (!) Какие метрики Istio отдаёт в Prometheus?

Каждый Envoy-sidecar автоматически собирает метрики по всему проходящему трафику и отдаёт их в формате Prometheus. Главная мысль: наблюдаемость появляется сама собой, без инструментирования кода приложения.

**Метрики, которые генерируются автоматически:**
- `istio_requests_total` (counter) — общее число запросов
- `istio_request_duration_milliseconds` (histogram) — задержка
- `istio_request_bytes`, `istio_response_bytes` — объём данных
- `istio_tcp_*` — аналогичные метрики для TCP-трафика

**Метки (labels)** обогащают каждую метрику измерениями: сервис-источник и назначение, код ответа, протокол и т.д. — именно по ним строят разрезы в дашбордах.

**На что это идёт:**
- Дашборды на уровне отдельных сервисов
- Алертинг по частоте ошибок и задержке
- Триггеры авто-масштабирования (например, KEDA)

## Q19. Как устроен distributed tracing (Jaeger, Tempo)?

Envoy на каждом хопе создаёт **trace span** и пробрасывает **трейс-заголовки (B3)** дальше — так получается сквозной трейс запроса через несколько сервисов.

```yaml
# Configure tracing
meshConfig:
  defaultConfig:
    tracing:
      sampling: 100  # % of requests traced
      zipkin:
        address: jaeger-collector:9411
```

**Подводный камень (его любят спрашивать):** Istio пробрасывает трейс-заголовки только между прокси. Внутри сервиса — от входящего запроса к исходящим — заголовки должен переносить сам код приложения (B3 или W3C). Если этого не делать, цепочка трейса рвётся на каждом сервисе. Istio context propagation внутри приложения за вас не сделает.

Подробнее — в [OpenTelemetry](../monitoring/opentelemetry-interview.md).

## Q20. Что такое access logs в Istio и чем за них платят?

```yaml
meshConfig:
  accessLogFile: /dev/stdout
  accessLogFormat: |
    [%START_TIME%] "%REQ(:METHOD)% %REQ(X-ENVOY-ORIGINAL-PATH?:PATH)% %PROTOCOL%"
    %RESPONSE_CODE% %RESPONSE_FLAGS% %BYTES_RECEIVED% %BYTES_SENT%
```

**Что это даёт:** Envoy пишет строчку лога на каждый запрос (формат настраивается), и эти логи уходят в ELK / Loki / Datadog. Получается детальная картина по каждому вызову.

**Чем платят:** на высоконагруженном mesh access logs порождают огромный объём данных и нагружают логовую инфраструктуру. Поэтому в проде их обычно включают выборочно или сэмплируют, а не пишут всё подряд.

## Q21. Зачем нужен Kiali?

**Kiali** — графический UI для Istio, дающий наглядную картину того, что происходит в mesh. Без него конфигурация и трафик остаются абстракцией в YAML; Kiali превращает их в визуальную топологию.

**Что показывает:**
- **Граф сервисов** — визуализация топологии: кто с кем общается
- Анимация трафика — потоки в реальном времени
- Валидация конфигурации — подсветка ошибок в VirtualService/DestinationRule
- Корреляция с трейсами
- Обзор состояния (health) сервисов

```bash
kubectl apply -f kiali.yaml
istioctl dashboard kiali
```

**На практике:** для эксплуатации Istio Kiali почти незаменим — без него отлаживать маршрутизацию и mTLS приходится вслепую по YAML и логам.

## Q22. (!) Что такое ambient mode и почему он без sidecar?

**Istio Ambient mode** (появился в 2022, GA в 2024) — это способ запустить mesh **без sidecar-контейнеров в каждом pod**. Он возник как ответ на главную боль классического Istio: цену sidecar-ов (RAM и задержка из Q4).

**Архитектура из двух слоёв:**
- **Layer 4 — ztunnel** — DaemonSet, по одному на ноду; берёт на себя mTLS и базовые L4-политики для всех pod-ов этой ноды
- **Layer 7 — waypoint proxy** — опциональный Envoy на namespace, подключается только когда реально нужны L7-возможности

Путь трафика из `App A` в `App B` в ambient mode:

- `App A` → `ztunnel (per node)` на своей ноде
- `ztunnel` ноды отправителя ↔ `ztunnel (per node)` ноды получателя — соединение между ними идёт по mTLS
- `ztunnel` ноды получателя → `App B`

Кроме того, `ztunnel` может опционально направить трафик в `Waypoint Proxy (per namespace)` — это и есть опциональный слой L7, подключаемый только при необходимости.

**Чем лучше sidecar:**
- **Pod не меняется** — достаточно opt-in метки, не нужно пересоздавать pod-ы и трогать манифесты
- **Меньше ресурсов** — один ztunnel на ноду вместо одного прокси на каждый pod
- **Дешевле** на большом mesh — экономия растёт с числом pod-ов
- **Плавнее внедрение** — менее инвазивно для приложений

**Компромиссы:**
- Технология новее и менее зрелая, чем sidecar
- Часть возможностей пока доступна только в sidecar-режиме

К **2025** ambient mode быстро набирает популярность, и для новых установок Istio его уже рекомендуют как стартовый вариант.

## Q23. Что делают ztunnel и waypoint proxy?

Это два компонента ambient mode (Q22) с чётким разделением: ztunnel закрывает L4 и есть всегда, waypoint закрывает L7 и подключается только при необходимости. Ключевая идея ambient — платить только за тот слой, который реально используешь.

**ztunnel** (zero-trust tunnel):
- DaemonSet, по одному на ноду
- Написан на Rust — лёгкий и быстрый
- Берёт на себя **L4 mTLS** и простую авторизацию
- Присутствует в ambient всегда — это базовый слой

**Waypoint proxy:**
- Это полноценный Envoy
- Разворачивается на **сервис** или **namespace**
- Даёт **L7-возможности**: retries, маршрутизацию трафика, RequestAuthentication
- Опционален — поднимается, только если эти L7-функции реально нужны

**Отсюда экономия:** если mesh небольшой и нужен только L4 (mTLS между сервисами), хватает одних ztunnel-ов — без тяжёлых Envoy-прокси в каждом pod это заметно дешевле sidecar-подхода.

## Q24. (!) Чем Istio отличается от Linkerd и Consul Connect?

Все три — service mesh, но с разными приоритетами. Если совсем коротко: Istio выбирают за максимум возможностей, Linkerd — за простоту и скорость, Consul Connect — за работу вне Kubernetes (VM, мультиплатформа).

| Критерий | Istio | Linkerd | Consul Connect |
|-----------|-------|---------|----------------|
| Сложность | **Высокая** | **Низкая** | Средняя |
| Производительность | Нормальная (Envoy тяжёлый) | **Отличная** (прокси на Rust) | Хорошая |
| Возможности | Больше всего | Подмножество | Мультиплатформенный |
| Распространённость | Самая высокая | Растёт | Средняя |
| Платформа | K8s | K8s | Мульти (VM, K8s) |
| Зрелость | Самый зрелый | Зрелый | Зрелый |
| Размер sidecar | ~50-100 МБ | ~30 МБ | ~50 МБ |
| Накладная задержка | 5-10 мс | **<1 мс** | 5 мс |

**Как выбрать под задачу:**
- **Нужен максимум возможностей и вы только в K8s** → Istio
- **Важны простота и производительность, тоже только K8s** → Linkerd
- **Нужна мультиплатформенность (VM + K8s) или вы уже в стеке HashiCorp** → Consul Connect

К **2025** для более простых сценариев чаще выбирают **Linkerd**, а **Istio** берут, когда реально нужен его полный набор возможностей.

## Q25. Какие у Istio минусы?

Главная мысль: почти все минусы Istio — оборотная сторона его богатства. За каждую возможность платят сложностью и ресурсами.

1. **Сложность** — крутая кривая обучения, много концепций сразу
2. **Расход ресурсов** — sidecar-ы Envoy дороги, когда pod-ов много
3. **Операционная нагрузка** — обновления и траблшутинг отнимают время команды
4. **Задержка** — +5-10 мс на каждый хоп через прокси
5. **Сложная отладка** — много слоёв, неочевидно, где именно ломается
6. **Разрастание конфигурации** — десятки CRD, легко запутаться
7. **Обратная совместимость** — между версиями иногда ломается
8. **Возможности простаивают** — большинство команд используют ~10% того, что Istio умеет

**Что меняет картину:** ambient mode (Q22) снимает заметную часть этих минусов — прежде всего расход ресурсов и инвазивность.

## Q26. Когда стоит и когда не стоит использовать service mesh?

Главный критерий — масштаб и зрелость системы: mesh окупается, только когда сервисов много и задачи связи действительно сложные. На маленькой системе его сложность не оправдывается.

**Mesh стоит брать, когда:**
- **Много микросервисов** (10+) со сложными взаимодействиями
- Нужен **mTLS** между сервисами (zero trust)
- Нужен **тонкий контроль трафика** (canary, A/B)
- Хочется **единообразной наблюдаемости** — метрики и трейсы без правки кода каждого сервиса
- Нужна **авторизация сервис-сервис**, а не только аутентификация

**Mesh не стоит брать, когда:**
- Сервисов мало (3-5) — накладные расходы не окупятся
- Потребности простые и закрываются **встроенными средствами K8s** (NetworkPolicies, Services)
- У команды нет ресурсов на эксплуатацию mesh (а это постоянная работа)
- Критична производительность и важна каждая миллисекунда

**Более лёгкие альтернативы, если mesh избыточен:**
- **Linkerd** — тот же mesh, но проще
- **Cilium Service Mesh** — на eBPF, без sidecar
- **Сборка из примитивов** — NetworkPolicies + cert-manager + OpenTelemetry

К **2025** многие команды признают, что полноценный mesh для их задач избыточен, и осознанно выбирают более простые стеки.

---

## See also

- [Linkerd](linkerd-interview.md) — main alternative
- [Consul Connect](consul-interview.md) — multi-platform alternative
- [Kubernetes](kubernetes-interview.md) — required platform
- [Микросервисы](../architecture/microservices-interview.md) — main use case
- [Cloud-native Patterns](../cloud/cloud-native-patterns-interview.md) — context
- [Zero Trust](../security/zero-trust-interview.md) — Istio enables
- [mTLS](../security/mtls-interview.md) — automatic via Istio
- [Application Security](../security/application-security-interview.md) — authz
- [OpenTelemetry](../monitoring/opentelemetry-interview.md) — Istio integrates
- [Observability](../monitoring/observability-interview.md) — context
- [Deployment Strategies](../cicd/deployment-strategies-interview.md) — canary через Istio
- [Resilience Patterns](../architecture/resilience-patterns-interview.md) — retries, circuit breaker
- [Networking](../architecture/networking-interview.md) — L4/L7 concepts

- [Ansible](ansible-interview.md)
- [ArgoCD и GitOps](argocd-interview.md)
- [HashiCorp Consul](consul-interview.md)
- [Docker](docker-interview.md)
- [Git](git-interview.md)
- [Gradle и Maven](gradle-maven-interview.md)
