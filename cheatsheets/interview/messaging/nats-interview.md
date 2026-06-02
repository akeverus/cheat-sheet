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

`NATS` — lightweight, high-performance messaging system. CNCF incubating project (2018). Создан Derek Collison (ex-TIBCO, RabbitMQ creator). Известен **скоростью** (миллионы msg/sec на single node), простотой, и low resource usage. **Core NATS** = pub/sub, **JetStream** = persistence (с 2020).

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

**NATS** — open-source messaging system (CNCF Incubating). Designed для:
- **Очень низкой latency** (microseconds)
- **Высокой throughput** (millions msg/sec)
- **Простоты** (~30 MB binary)
- **Cloud-native, edge** deployments

**Two layers:**
- **Core NATS** — pub/sub, lightweight, **at-most-once** delivery
- **JetStream** — persistence layer для **at-least-once / exactly-once**

**Применения:**
- Microservices communication
- IoT (millions devices)
- Edge computing
- Real-time apps (chat, gaming)
- Event-driven architectures

## Q2. (!) NATS vs Kafka vs RabbitMQ?

| Критерий | NATS | Kafka | RabbitMQ |
|----------|------|-------|----------|
| Latency | **Microseconds** | ms | ms |
| Throughput per node | Millions msg/sec | Hundreds of K/sec | Tens of K/sec |
| Persistence | JetStream optional | Always | Optional |
| Resource usage | **Very low** (30 MB) | High (JVM, GBs) | Medium |
| Setup complexity | Simple | Complex | Medium |
| Stream replay | JetStream (limited) | **First-class** | Limited |
| Routing patterns | Subjects + queue groups | Topics + partitions | Exchanges, bindings |
| Best for | Microservices, IoT, edge | Event streaming, big data | Enterprise messaging |

**Когда NATS** vs other:
- **Low latency** — NATS wins
- **Stream processing на huge scale** — Kafka
- **Complex routing** — RabbitMQ
- **Lightweight, edge** — NATS

## Q3. Архитектура NATS Server?

**NATS Server** — single Go binary (~30 MB).

**Modes:**
- **Standalone** — single node
- **Cluster** — peers full mesh
- **Super-cluster** — multiple clusters (regions)
- **Leaf nodes** — edge deployments

**Built-in:**
- Authentication (multiple methods)
- Authorization (subject-based)
- Monitoring HTTP endpoint
- TLS

Никаких external dependencies (Apache Kafka требует ZooKeeper, etc.).

## Q4. (!) Subjects (вместо topics)?

**Subject** — name для message routing. **Hierarchical**, dot-separated.

```
orders.created
orders.updated
orders.cancelled

user.123.profile
user.123.orders
user.456.profile
```

**Vs Kafka topics:**
- NATS subjects more granular (millions cheap)
- Wildcards для multi-subject matching
- No partitions concept (для Core NATS)

## Q5. Wildcards в subjects (`*`, `>`)?

**`*`** — single-token wildcard.
**`>`** — multi-token wildcard (must be last).

```
orders.*           — matches orders.created, orders.updated (но не orders.shipping.scheduled)
orders.>           — matches orders.created, orders.shipping.scheduled, всё под orders
user.*.profile     — matches user.123.profile, user.456.profile
*.events.>         — matches user.events.X, system.events.X.Y
```

**Use case:** subscribers можно подписаться на широкий range subjects.

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

**Все subscribers** matching subject get message (broadcast).

**Core NATS:** if no subscribers — message **dropped** (at-most-once).

## Q7. (!) Queue groups (load balancing)?

**Queue group** — multiple consumers share same subject, only **ONE** receives each message (round-robin).

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

**Эффективное load balancing** для work queues.

**Combined с broadcast:** subscribers без queue + queue groups одновременно — каждая queue group получает 1 копию + non-queue subscribers получают каждое message.

## Q8. Request-Reply?

**RPC-like pattern** через NATS:

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

NATS uses **temporary subjects** для replies — auto-managed.

**Sub-millisecond** latency (vs HTTP).

## Q9. (!) Что такое JetStream?

**JetStream** (с 2020) — persistence layer на NATS.

**Adds:**
- **Persistent storage** (memory или disk)
- **Replay** messages
- **At-least-once** / **exactly-once** delivery
- **Replication** (RAFT)
- **Consumer state tracking**

**Backwards-compatible** с Core NATS — same protocol, добавлены commands.

JetStream нужен для **persistent messaging** workloads. Без JetStream — NATS лучше для realtime / fire-and-forget.

## Q10. Streams в JetStream?

**Stream** — persistent storage для messages matching subjects.

```bash
nats stream add ORDERS \
  --subjects "orders.>" \
  --storage file \
  --replicas 3 \
  --max-age 7d \
  --max-msgs 10000000
```

Все messages publishing к `orders.>` — saved в `ORDERS` stream.

**Storage:**
- **File** — disk-based, durable
- **Memory** — fast, ephemeral

## Q11. (!) Consumers (durable, ephemeral)?

**Consumer** = view на stream messages.

**Durable consumer** — survives restarts, NATS tracks position (last consumed sequence).

```bash
nats consumer add ORDERS order-processor \
  --target orders.process \
  --deliver all \
  --replay instant
```

**Ephemeral** — auto-removed when no subscribers.

**Pull vs Push:**
- **Push** consumer — NATS sends messages к subscriber
- **Pull** consumer — subscriber requests messages (better для batch processing)

## Q12. Retention policies?

**Limits-based** (default):
```
max_age: 7 days
max_msgs: 10M
max_bytes: 100GB
```

**Interest-based:** delete after all consumers received.

**Work queue:** delete after one consumer received (queue semantics).

```yaml
retention: limits | interest | workqueue
```

## Q13. Replication через RAFT?

**JetStream** uses **RAFT consensus** для replication.

```bash
nats stream add ORDERS --replicas 3
```

**3 replicas:** quorum = 2. Survive 1 failure.

**Tolerance:**
- 1 replica: no HA
- 3 replicas: tolerate 1 failure
- 5 replicas: tolerate 2 failures

**Storage:** quorum write (majority must persist) before ACK.

## Q14. Key-Value store (built-in)?

**JetStream KV** — simple key-value store на JetStream streams.

```bash
nats kv add my_kv
nats kv put my_kv config '{"timeout": 30}'
nats kv get my_kv config
nats kv watch my_kv  # subscribe to changes
```

**Use cases:**
- Distributed configuration
- Service discovery
- Feature flags
- Sessions

**Watch API** — real-time updates (analog etcd watch).

## Q15. Object Store?

**JetStream Object Store** — для **larger blobs** (files, images).

```bash
nats object add my_objects
nats object put my_objects ./photo.jpg
nats object get my_objects photo.jpg
```

Splits objects в **chunks** (default 128 KB), stores в JetStream.

**Use cases:**
- File transfer between services
- Distributed storage layer
- Edge caching

**Не replacement** для S3 — для smaller objects, integrated с messaging.

## Q16. NATS Mirroring и Sourcing?

**Mirror** — exact replica другого stream.

```bash
nats stream add ORDERS_MIRROR --mirror ORDERS
```

**Source** — combine messages from multiple streams.

```bash
nats stream add COMBINED --sources STREAM1 --sources STREAM2
```

**Use cases:**
- Multi-region replication
- Disaster recovery
- Stream aggregation

## Q17. (!) NATS clustering?

**Cluster** — multiple NATS Server instances connected как **full mesh**.

```yaml
cluster {
  name: my-cluster
  listen: 0.0.0.0:6222
  routes: ["nats://node1:6222", "nats://node2:6222"]
}
```

**Auto-discovery:** clients connect к ANY node, automatically routed.

**Single virtual broker** semantics — pub в одном node → subscribers на other nodes получают messages.

**Scale:** до tens of nodes per cluster.

## Q18. Leaf nodes (edge)?

**Leaf node** — NATS Server connected к main cluster as one-way leaf.

```yaml
leafnodes {
  remotes: [
    { url: "nats://central-cluster:7422" }
  ]
}
```

**Use case:**
- **Edge deployments** — IoT devices, branch offices
- Local NATS для low latency, leaf к central
- Disconnected operation possible

**Subjects scoped** — leaf нодa может только subjects из allowed accounts.

## Q19. Super-cluster (multi-region)?

**Super-cluster** = multiple clusters connected в **mesh**.

```
Cluster A (us-east)  ←→  Cluster B (eu-west)
        ↓                       ↓
   leaf nodes              leaf nodes
   (edge in US)           (edge in EU)
```

**Gateway connections** между clusters. Clients connect locally → messages routed globally.

**Multi-region** messaging без central broker.

## Q20. Authentication (NATS auth, JWT)?

**Auth methods:**
- **Token** — simple shared token
- **User/password**
- **NKEY** (public key based, similar SSH keys)
- **JWT** — decentralized auth (recommended)
- **mTLS**

**JWT-based:**
```yaml
operator: <operator-jwt>
resolver: URL  # or memory
```

**Decentralized:** operator → accounts → users. Servers don't need user list — they verify JWT signatures.

**Production best practice** — JWT-based с NSC tool для management.

## Q21. Accounts (multi-tenancy)?

**Account** = isolated namespace (subjects, streams, KVs).

```yaml
accounts: {
  TENANT_A: {
    users: [...],
    exports: [...]  # subjects exposed
  },
  TENANT_B: {...}
}
```

**Cross-account communication** через **exports/imports**.

**Use case:** SaaS multi-tenant — каждый customer = separate account, isolated.

## Q22. (!) Когда выбрать NATS?

**Выбирай когда:**
- **Microservices** internal communication (replace HTTP/gRPC)
- **IoT** — millions devices, low resource usage
- **Edge computing** — leaf nodes
- **Low latency** critical (gaming, real-time)
- **Simple ops** preferred over Kafka complexity
- **Multi-region** without expensive Kafka MirrorMaker
- Need **request-reply + pub/sub + persistent streams** в одном stack

## Q23. Когда не выбирать NATS?

**Не выбирай когда:**
- Need **complex stream processing** (Kafka Streams better)
- Need **rich ecosystem** integrations (Kafka has it)
- **Long-term retention** (years) — Kafka cheaper
- **Big Data processing** — Kafka + Spark/Flink standard
- Team **already invested** в Kafka / RabbitMQ
- Need **enterprise features** что NATS не имеет (some Kafka Enterprise features)

## Q24. Какие частые проблемы?

1. **JetStream config** — wrong storage type (memory) → data loss
2. **Insufficient replicas** — single point failure
3. **Retention policy mistakes** — data deleted before consumed
4. **No backpressure** — consumers can't keep up, queues grow
5. **Cluster split** — network partition handling
6. **Authentication misconfig** — security risks
7. **No monitoring** — don't know status
8. **Wrong deployment** (Core NATS когда нужен JetStream)
9. **Subject design** — flat намespace без hierarchy
10. **Underestimating learning curve** — new patterns vs Kafka/RabbitMQ

В **2025** NATS — fast-growing для cloud-native, edge, IoT use cases.

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
