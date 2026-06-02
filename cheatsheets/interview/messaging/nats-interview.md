---
title: "Вопросы на собеседовании: NATS"
description: "NATS: lightweight messaging system, subjects, queue groups, JetStream (persistence), Core NATS vs JetStream, request-reply, KV, Object Store, NATS vs Kafka/RabbitMQ"
tags:
  - interview
  - messaging
  - nats-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "NATS"
  - "NATS interview"
  - "NATS собеседование"
prerequisites:
  - "[[nats]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `NATS`

`NATS` — лёгкая высокопроизводительная система обмена сообщениями. Incubating-проект CNCF (с 2018). Создан Дереком Коллисоном (ранее TIBCO, автор RabbitMQ). Известен **скоростью** (миллионы msg/sec на одной ноде), простотой и низким потреблением ресурсов. **Core NATS** = pub/sub, **JetStream** = персистентность (с 2020).

## Полезные ссылки

### Официальная документация и авторитетные источники

- [NATS Documentation](https://docs.nats.io/)
- [NATS GitHub](https://github.com/nats-io)
- [JetStream Documentation](https://docs.nats.io/nats-concepts/jetstream)
- [NATS by Example](https://natsbyexample.com/)
- [Synadia (commercial backer)](https://synadia.com/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое NATS?](#q1--что-такое-nats)
- [Q2. (!) NATS vs Kafka vs RabbitMQ?](#q2--nats-vs-kafka-vs-rabbitmq)
- [Q3. Архитектура NATS Server?](#q3-архитектура-nats-server)

**Core NATS**
- [Q4. (!) Subjects (вместо topics)?](#q4--subjects-вместо-topics)
- [Q5. Wildcards в subjects (`*`, `>`)?](#q5-wildcards-в-subjects--)
- [Q6. (!) Pub/Sub patterns?](#q6--pubsub-patterns)
- [Q7. (!) Queue groups (load balancing)?](#q7--queue-groups-load-balancing)
- [Q8. Request-Reply?](#q8-request-reply)

**JetStream (persistence)**
- [Q9. (!) Что такое JetStream?](#q9--что-такое-jetstream)
- [Q10. Streams в JetStream?](#q10-streams-в-jetstream)
- [Q11. (!) Consumers (durable, ephemeral)?](#q11--consumers-durable-ephemeral)
- [Q12. Retention policies?](#q12-retention-policies)
- [Q13. Replication через RAFT?](#q13-replication-через-raft)

**Дополнительные функции**
- [Q14. Key-Value store (built-in)?](#q14-key-value-store-built-in)
- [Q15. Object Store?](#q15-object-store)
- [Q16. NATS Mirroring и Sourcing?](#q16-nats-mirroring-и-sourcing)

**Cluster и масштабирование**
- [Q17. (!) NATS clustering?](#q17--nats-clustering)
- [Q18. Leaf nodes (edge)?](#q18-leaf-nodes-edge)
- [Q19. Super-cluster (multi-region)?](#q19-super-cluster-multi-region)

**Безопасность**
- [Q20. Authentication (NATS auth, JWT)?](#q20-authentication-nats-auth-jwt)
- [Q21. Accounts (multi-tenancy)?](#q21-accounts-multi-tenancy)

**Production**
- [Q22. (!) Когда выбрать NATS?](#q22--когда-выбрать-nats)
- [Q23. Когда не выбирать NATS?](#q23-когда-не-выбирать-nats)
- [Q24. Какие частые проблемы?](#q24-какие-частые-проблемы)

## Q1. (!) Что такое NATS?

**NATS** — open-source система обмена сообщениями (CNCF Incubating). Спроектирована ради:
- **Очень низкой latency** (микросекунды)
- **Высокого throughput** (миллионы msg/sec)
- **Простоты** (бинарник ~30 MB)
- **Cloud-native и edge**-развёртываний

**Два слоя:**
- **Core NATS** — pub/sub, легковесный, доставка **at-most-once**
- **JetStream** — слой персистентности для **at-least-once / exactly-once**

**Применения:**
- Коммуникация микросервисов
- IoT (миллионы устройств)
- Edge-вычисления
- Real-time приложения (чат, игры)
- Event-driven архитектуры

## Q2. (!) NATS vs Kafka vs RabbitMQ?

| Критерий | NATS | Kafka | RabbitMQ |
|----------|------|-------|----------|
| Latency | **Микросекунды** | мс | мс |
| Throughput на ноду | Миллионы msg/sec | Сотни тысяч/сек | Десятки тысяч/сек |
| Персистентность | JetStream опционально | Всегда | Опционально |
| Потребление ресурсов | **Очень низкое** (30 MB) | Высокое (JVM, гигабайты) | Среднее |
| Сложность настройки | Простая | Сложная | Средняя |
| Replay потока | JetStream (ограниченно) | **Первоклассный** | Ограниченно |
| Паттерны маршрутизации | Subjects + queue groups | Topics + partitions | Exchanges, bindings |
| Лучше всего для | Микросервисы, IoT, edge | Event streaming, big data | Enterprise-сообщения |

**Когда NATS** против остальных:
- **Низкая latency** — побеждает NATS
- **Stream processing на огромном масштабе** — Kafka
- **Сложная маршрутизация** — RabbitMQ
- **Легковесность, edge** — NATS

## Q3. Архитектура NATS Server?

**NATS Server** — единый бинарник на Go (~30 MB).

**Режимы:**
- **Standalone** — одиночная нода
- **Cluster** — пиры в режиме full mesh
- **Super-cluster** — несколько кластеров (регионы)
- **Leaf nodes** — edge-развёртывания

**Встроено:**
- Аутентификация (несколько методов)
- Авторизация (на основе subjects)
- HTTP-эндпоинт мониторинга
- TLS

Никаких внешних зависимостей (Apache Kafka требует ZooKeeper и т.д.).

## Q4. (!) Subjects (вместо topics)?

**Subject** — имя для маршрутизации сообщений. **Иерархическое**, разделённое точками.

```
orders.created
orders.updated
orders.cancelled

user.123.profile
user.123.orders
user.456.profile
```

**В сравнении с Kafka topics:**
- Subjects в NATS более гранулярны (миллионы — это дёшево)
- Wildcards для сопоставления сразу с несколькими subjects
- Нет концепции partitions (для Core NATS)

## Q5. Wildcards в subjects (`*`, `>`)?

**`*`** — wildcard на один токен.
**`>`** — wildcard на несколько токенов (должен быть последним).

```
orders.*           — matches orders.created, orders.updated (но не orders.shipping.scheduled)
orders.>           — matches orders.created, orders.shipping.scheduled, всё под orders
user.*.profile     — matches user.123.profile, user.456.profile
*.events.>         — matches user.events.X, system.events.X.Y
```

**Сценарий применения:** подписчики могут подписаться на широкий диапазон subjects.

## Q6. (!) Pub/Sub patterns?

**Publisher:**
```javascript
nc.publish("orders.created", JSON.stringify({orderId: 123}));
```

**Subscriber:**
```javascript
nc.subscribe("orders.*", msg => {
    console.log(`Got: ${msg.subject}`, msg.data);
});
```

**Все подписчики**, чей subject совпадает, получают сообщение (broadcast).

**Core NATS:** если подписчиков нет — сообщение **отбрасывается** (at-most-once).

## Q7. (!) Queue groups (load balancing)?

**Queue group** — несколько consumer'ов делят один subject, и каждое сообщение получает только **ОДИН** из них (round-robin).

```javascript
// Worker 1
nc.subscribe("orders.process", {queue: "workers"}, processOrder);

// Worker 2
nc.subscribe("orders.process", {queue: "workers"}, processOrder);

// Worker 3
nc.subscribe("orders.process", {queue: "workers"}, processOrder);

// Publish — only one worker gets message
nc.publish("orders.process", data);
```

**Эффективная балансировка нагрузки** для work queues.

**В сочетании с broadcast:** подписчики без queue и queue groups одновременно — каждая queue group получает 1 копию, а подписчики без queue получают каждое сообщение.

## Q8. Request-Reply?

**RPC-подобный паттерн** через NATS:

```javascript
// Server
nc.subscribe("calculator.add", msg => {
    const {a, b} = JSON.parse(msg.data);
    msg.respond(JSON.stringify({result: a + b}));
});

// Client
const response = await nc.request("calculator.add",
    JSON.stringify({a: 2, b: 3}),
    {timeout: 1000}
);
console.log(JSON.parse(response.data));  // {result: 5}
```

NATS использует **временные subjects** для ответов — управляются автоматически.

Latency **меньше миллисекунды** (по сравнению с HTTP).

## Q9. (!) Что такое JetStream?

**JetStream** (с 2020) — слой персистентности поверх NATS.

**Добавляет:**
- **Постоянное хранилище** (в памяти или на диске)
- **Replay** сообщений
- Доставку **at-least-once** / **exactly-once**
- **Репликацию** (RAFT)
- **Отслеживание состояния consumer'а**

**Обратно совместим** с Core NATS — тот же протокол, добавлены команды.

JetStream нужен для нагрузок с **персистентным обменом сообщениями**. Без JetStream NATS лучше подходит для realtime / fire-and-forget.

## Q10. Streams в JetStream?

**Stream** — постоянное хранилище для сообщений, чьи subjects совпадают с заданными.

```bash
nats stream add ORDERS \
  --subjects "orders.>" \
  --storage file \
  --replicas 3 \
  --max-age 7d \
  --max-msgs 10000000
```

Все сообщения, публикуемые в `orders.>`, сохраняются в stream `ORDERS`.

**Хранилище:**
- **File** — на диске, durable
- **Memory** — быстрое, эфемерное

## Q11. (!) Consumers (durable, ephemeral)?

**Consumer** = представление (view) над сообщениями stream'а.

**Durable consumer** — переживает рестарты, NATS отслеживает позицию (последний прочитанный sequence).

```bash
nats consumer add ORDERS order-processor \
  --target orders.process \
  --deliver all \
  --replay instant
```

**Ephemeral** — удаляется автоматически, когда подписчиков не остаётся.

**Pull против Push:**
- **Push** consumer — NATS сам отправляет сообщения подписчику
- **Pull** consumer — подписчик запрашивает сообщения (лучше для batch-обработки)

## Q12. Retention policies?

**На основе лимитов** (по умолчанию):
```
max_age: 7 days
max_msgs: 10M
max_bytes: 100GB
```

**На основе интереса (interest):** удаление после того, как все consumer'ы получили сообщение.

**Work queue:** удаление после того, как сообщение получил один consumer (семантика очереди).

```yaml
retention: limits | interest | workqueue
```

## Q13. Replication через RAFT?

**JetStream** использует **консенсус RAFT** для репликации.

```bash
nats stream add ORDERS --replicas 3
```

**3 реплики:** кворум = 2. Переживает 1 отказ.

**Устойчивость:**
- 1 реплика: нет HA
- 3 реплики: переживает 1 отказ
- 5 реплик: переживают 2 отказа

**Запись:** кворумная запись (большинство должно сохранить данные) до отправки ACK.

## Q14. Key-Value store (built-in)?

**JetStream KV** — простое key-value хранилище поверх JetStream streams.

```bash
nats kv add my_kv
nats kv put my_kv config '{"timeout": 30}'
nats kv get my_kv config
nats kv watch my_kv  # subscribe to changes
```

**Сценарии применения:**
- Распределённая конфигурация
- Service discovery
- Feature flags
- Сессии

**Watch API** — обновления в реальном времени (аналог watch в etcd).

## Q15. Object Store?

**JetStream Object Store** — для **более крупных blob'ов** (файлы, изображения).

```bash
nats object add my_objects
nats object put my_objects ./photo.jpg
nats object get my_objects photo.jpg
```

Разбивает объекты на **chunks** (по умолчанию 128 KB) и хранит их в JetStream.

**Сценарии применения:**
- Передача файлов между сервисами
- Распределённый слой хранения
- Edge-кеширование

**Не замена** S3 — для объектов поменьше, интегрированных с обменом сообщениями.

## Q16. NATS Mirroring и Sourcing?

**Mirror** — точная реплика другого stream'а.

```bash
nats stream add ORDERS_MIRROR --mirror ORDERS
```

**Source** — объединяет сообщения из нескольких stream'ов.

```bash
nats stream add COMBINED --sources STREAM1 --sources STREAM2
```

**Сценарии применения:**
- Репликация между регионами
- Disaster recovery
- Агрегация stream'ов

## Q17. (!) NATS clustering?

**Cluster** — несколько экземпляров NATS Server, соединённых в **full mesh**.

```yaml
cluster {
  name: my-cluster
  listen: 0.0.0.0:6222
  routes: ["nats://node1:6222", "nats://node2:6222"]
}
```

**Auto-discovery:** клиенты подключаются к ЛЮБОЙ ноде, маршрутизация происходит автоматически.

Семантика **единого виртуального брокера** — публикация на одной ноде → подписчики на других нодах получают сообщения.

**Масштаб:** до десятков нод на кластер.

## Q18. Leaf nodes (edge)?

**Leaf node** — NATS Server, подключённый к основному кластеру как односторонний leaf.

```yaml
leafnodes {
  remotes: [
    { url: "nats://central-cluster:7422" }
  ]
}
```

**Сценарий применения:**
- **Edge-развёртывания** — IoT-устройства, филиалы
- Локальный NATS ради низкой latency, leaf — к центральному
- Возможна работа в отключённом (disconnected) режиме

**Subjects ограничены областью видимости** — leaf-нода может работать только с subjects из разрешённых accounts.

## Q19. Super-cluster (multi-region)?

**Super-cluster** = несколько кластеров, соединённых в **mesh**.

```
Cluster A (us-east)  ←→  Cluster B (eu-west)
        ↓                       ↓
   leaf nodes              leaf nodes
   (edge in US)           (edge in EU)
```

**Gateway-соединения** между кластерами. Клиенты подключаются локально → сообщения маршрутизируются глобально.

Обмен сообщениями **между регионами** без центрального брокера.

## Q20. Authentication (NATS auth, JWT)?

**Методы аутентификации:**
- **Token** — простой общий токен
- **User/password**
- **NKEY** (на основе публичного ключа, похоже на SSH-ключи)
- **JWT** — децентрализованная аутентификация (рекомендуется)
- **mTLS**

**На основе JWT:**
```yaml
operator: <operator-jwt>
resolver: URL  # or memory
```

**Децентрализованность:** operator → accounts → users. Серверам не нужен список пользователей — они проверяют подписи JWT.

**Лучшая практика для production** — на основе JWT с инструментом NSC для управления.

## Q21. Accounts (multi-tenancy)?

**Account** = изолированное пространство имён (subjects, streams, KV).

```yaml
accounts: {
  TENANT_A: {
    users: [...],
    exports: [...]  # subjects exposed
  },
  TENANT_B: {...}
}
```

**Коммуникация между accounts** через **exports/imports**.

**Сценарий применения:** multi-tenant SaaS — каждый клиент = отдельный изолированный account.

## Q22. (!) Когда выбрать NATS?

**Выбирай когда:**
- **Внутренняя коммуникация микросервисов** (замена HTTP/gRPC)
- **IoT** — миллионы устройств, низкое потребление ресурсов
- **Edge-вычисления** — leaf nodes
- Критична **низкая latency** (игры, real-time)
- **Простота эксплуатации** предпочтительнее сложности Kafka
- **Несколько регионов** без дорогого Kafka MirrorMaker
- Нужны **request-reply + pub/sub + персистентные streams** в одном стеке

## Q23. Когда не выбирать NATS?

**Не выбирай когда:**
- Нужна **сложная потоковая обработка** (Kafka Streams лучше)
- Нужны интеграции с **богатой экосистемой** (она есть у Kafka)
- **Долгосрочное хранение** (годы) — Kafka дешевле
- **Обработка Big Data** — стандарт Kafka + Spark/Flink
- Команда **уже вложилась** в Kafka / RabbitMQ
- Нужны **enterprise-фичи**, которых у NATS нет (часть фич Kafka Enterprise)

## Q24. Какие частые проблемы?

1. **Конфигурация JetStream** — неверный тип хранилища (memory) → потеря данных
2. **Недостаточно реплик** — единая точка отказа
3. **Ошибки в retention-политике** — данные удаляются до того, как их прочитали
4. **Нет backpressure** — consumer'ы не успевают, очереди растут
5. **Split кластера** — обработка сетевых разделений (network partition)
6. **Ошибки в конфигурации аутентификации** — риски безопасности
7. **Нет мониторинга** — непонятно, в каком состоянии система
8. **Неправильное развёртывание** (Core NATS, когда нужен JetStream)
9. **Дизайн subjects** — плоское пространство имён без иерархии
10. **Недооценка кривой обучения** — новые паттерны по сравнению с Kafka/RabbitMQ

В **2025** NATS быстро растёт для cloud-native, edge и IoT-сценариев.

---

## See also

- [Apache Kafka](kafka-interview.md) — main конкурент
- [RabbitMQ](rabbitmq-interview.md) — другой конкурент
- [Message Brokers Comparison](message-brokers-comparison-interview.md) — overview
- [Apache Pulsar](pulsar-interview.md) — другая alternative
- [Event-driven Patterns](../architecture/event-driven-patterns-interview.md) — context
- [Микросервисы](../architecture/microservices-interview.md) — primary use case
- [Stream Processing](../data-engineering/stream-processing-interview.md) — context
- [Распределённые системы](../architecture/distributed-systems-interview.md) — RAFT, consensus
- [CAP Theorem](../architecture/cap-theorem-interview.md) — JetStream tradeoffs
- [gRPC](../api/grpc-interview.md) — alternative для RPC
- [Saga Pattern](../architecture/saga-pattern-interview.md) — NATS для sagas
- [Caching](../architecture/caching-strategies-interview.md) — NATS KV

- [Apache Kafka](kafka-interview.md)
- [Сравнение Message Brokers](message-brokers-comparison-interview.md)
- [Apache Pulsar](pulsar-interview.md)
- [RabbitMQ](rabbitmq-interview.md)
- [Redpanda](redpanda-interview.md)
- [Шпаргалка: NATS](../../development/messaging/nats/nats.md) — теория
