---
title: "Вопросы на собеседовании: Redpanda"
description: "Redpanda: Kafka-compatible streaming platform на C++, no JVM, no ZooKeeper, lower latency, easier ops, Raft replication, built-in HTTP proxy, vs Kafka"
tags:
  - interview
  - messaging
  - redpanda-interview
aliases:
  - "Redpanda interview"
  - "Redpanda собеседование"
  - "Redpanda vs Kafka"
  - "Kafka-compatible C++ streaming"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Redpanda`

`Redpanda` — Kafka-compatible streaming platform, написанный на **C++** (vs Kafka Java). Created by **Vectorized.io** (now Redpanda Data, 2019). **No JVM, no ZooKeeper**, single binary. Promises **lower latency** и **easier ops**. Использует **Seastar** framework (как ScyllaDB) для shard-per-core architecture.

Дата последнего обновления: 2026-04-19

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Redpanda Documentation](https://docs.redpanda.com/)
- [Redpanda GitHub](https://github.com/redpanda-data/redpanda)
- [Redpanda vs Kafka Benchmarks](https://redpanda.com/blog/redpanda-vs-kafka-performance-benchmark)
- [Redpanda Cloud](https://redpanda.com/redpanda-cloud)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/) — same wire protocol

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Redpanda?](#q1--что-такое-redpanda)
- [Q2. (!) Redpanda vs Kafka — отличия?](#q2--redpanda-vs-kafka--отличия)
- [Q3. Single binary — что значит?](#q3-single-binary--что-значит)

**Архитектура**
- [Q4. (!) C++ + Seastar (shard-per-core)?](#q4--c--seastar-shard-per-core)
- [Q5. No JVM, no GC pauses?](#q5-no-jvm-no-gc-pauses)
- [Q6. (!) No ZooKeeper — Raft консенсус?](#q6--no-zookeeper--raft-консенсус)
- [Q7. Tiered storage?](#q7-tiered-storage)

**Compatibility с Kafka**
- [Q8. (!) Kafka wire protocol compatibility?](#q8--kafka-wire-protocol-compatibility)
- [Q9. Kafka clients работают?](#q9-kafka-clients-работают)
- [Q10. Schema Registry, Connect?](#q10-schema-registry-connect)

**Performance**
- [Q11. (!) Performance claims (latency, throughput)?](#q11--performance-claims-latency-throughput)
- [Q12. (!) Why faster than Kafka?](#q12--why-faster-than-kafka)

**Features**
- [Q13. WASM transforms?](#q13-wasm-transforms)
- [Q14. Built-in HTTP proxy?](#q14-built-in-http-proxy)
- [Q15. Console (UI)?](#q15-console-ui)

**Editions**
- [Q16. (!) Open source vs Enterprise vs Cloud?](#q16--open-source-vs-enterprise-vs-cloud)
- [Q17. Source available license (BSL)?](#q17-source-available-license-bsl)

**Production**
- [Q18. (!) Когда выбрать Redpanda над Kafka?](#q18--когда-выбрать-redpanda-над-kafka)
- [Q19. Когда не выбирать Redpanda?](#q19-когда-не-выбирать-redpanda)
- [Q20. Migration Kafka → Redpanda?](#q20-migration-kafka--redpanda)

## Q1. (!) Что такое Redpanda?

**Redpanda** — Kafka-compatible streaming platform.

**Created** by Vectorized.io (now Redpanda Data) в 2019.

**Key value props:**
- **Kafka API compatible** (drop-in replacement)
- **No JVM, no ZooKeeper, no Kafka Streams JVM dependency**
- **Single binary** — easier ops
- **C++ + Seastar** — performance
- **Lower latency** (claims 10x lower p99)
- **Same wire protocol** — existing Kafka clients work

**Применения:** same as Kafka — event streaming, microservices, real-time analytics, log aggregation.

## Q2. (!) Redpanda vs Kafka — отличия?

| Критерий | Apache Kafka | Redpanda |
|----------|--------------|----------|
| Language | Java (JVM) | C++ |
| ZooKeeper | Required (KRaft new) | **None** |
| Architecture | Thread pools | **Shard-per-core** |
| GC pauses | Yes | **No** |
| Components | Brokers + ZK + Connect + Schema Registry | **Single binary** |
| Latency p99 | 10-100 ms | **2-10 ms** (claimed) |
| Throughput | High | **Higher** на same hardware |
| Memory | Heavy (JVM heap) | **Lower** |
| Setup complexity | Complex | **Simple** |
| Maturity | 2011+, very mature | 2019+, less mature |
| Adoption | Massive | Growing |
| Ecosystem | Huge | Compatible но smaller native |

## Q3. Single binary — что значит?

**Apache Kafka deployment:**
- Kafka brokers (Java)
- ZooKeeper (or KRaft Controllers)
- Schema Registry (separate Java process)
- Kafka Connect (separate)
- MirrorMaker (separate)

**Redpanda deployment:**
- Just **`redpanda` binary** + config
- HTTP proxy, Schema Registry **built-in**
- One process per node

**Effect:** **massively simpler ops**. Container deployments easier. Less moving parts.

## Q4. (!) C++ + Seastar (shard-per-core)?

**Seastar** — same framework как **ScyllaDB**.

**Shard-per-core:**
- One shard per CPU core
- **No locks** между cores (no contention)
- **No shared memory** между cores
- Async I/O via Seastar
- Linear scaling с CPU cores

**vs Kafka thread pools:**
- Kafka uses thread pool model — locks, contention
- Redpanda — shared-nothing per core

**Result:** better CPU utilization, lower latency.

## Q5. No JVM, no GC pauses?

**Kafka** — JVM:
- GC pauses (10-500 ms)
- Spike в p99 latency
- Hard tuning

**Redpanda** — C++:
- Manual memory management
- **No GC pauses**
- Predictable latency
- p99 latency **5-10x lower**

Same advantage как **ScyllaDB vs Cassandra**.

## Q6. (!) No ZooKeeper — Raft консенсус?

**Kafka historically** require ZooKeeper для:
- Cluster metadata
- Controller election
- Configuration

**Kafka KRaft** (с 2.8+, GA в 3.3) — replaces ZK с built-in Raft. **In transition period**.

**Redpanda from day 1** — no ZooKeeper. **Raft per partition** — каждая partition имеет own Raft group для replication and consensus.

**Эффект:**
- Simpler ops
- Fewer components
- Faster failover

## Q7. Tiered storage?

**Tiered storage** в Redpanda — old data offloaded к **object storage (S3, GCS, Azure Blob)**.

```yaml
cloud_storage_enabled: true
cloud_storage_bucket: my-redpanda-bucket
cloud_storage_region: us-east-1
```

**Hot data:** local disk (fast)
**Cold data:** S3 (cheap)

**Reads** from S3 transparent — slower but cheap.

**Cost saving** для long retention (months, years).

Same idea как **Pulsar tiered storage**, **Kafka Tiered Storage** (KIP-405).

## Q8. (!) Kafka wire protocol compatibility?

**Redpanda implements** Kafka wire protocol.

**Kafka clients** (any language) talk к Redpanda **without changes**.

```python
# Same Kafka Python client
from kafka import KafkaProducer
producer = KafkaProducer(bootstrap_servers='redpanda:9092')
producer.send('my-topic', b'message')
```

**Compatibility level:** очень high. Most Kafka APIs supported.

**Some advanced features** не supported (Kafka transactions on Redpanda — supported недавно).

## Q9. Kafka clients работают?

**Да** — все mainstream Kafka clients:
- Java (kafka-clients)
- Python (kafka-python, confluent-kafka-python)
- Go (sarama, confluent-kafka-go, segmentio/kafka-go)
- Node.js (kafkajs)
- .NET, Ruby, etc.

**Confluent CLI tools** работают с Redpanda.

**ORM/connectors** (Debezium, Kafka Connect) — supported.

## Q10. Schema Registry, Connect?

**Schema Registry** — built into Redpanda (Avro, JSON Schema, Protobuf).

```bash
# Compatible с Confluent Schema Registry API
curl http://redpanda:8081/subjects
```

**Kafka Connect:** Redpanda не имеет own version. Use **standard Kafka Connect** против Redpanda — works.

**Redpanda Console** — UI для browsing topics, schemas, consumers.

## Q11. (!) Performance claims (latency, throughput)?

**Redpanda Data benchmark** results (vary):
- **p50 latency:** 2-3 ms (vs Kafka 10-15 ms)
- **p99 latency:** 5-10 ms (vs Kafka 50-100 ms)
- **Throughput:** 10x higher per CPU core
- **Lower CPU/memory** для same workload

**Caveats:**
- Vendor benchmarks (Redpanda Data) — may be biased
- Real-world results vary
- Kafka tuning matters

**Independent benchmarks** generally confirm Redpanda is **faster** but margins less than vendor claims.

## Q12. (!) Why faster than Kafka?

1. **C++ vs Java** — no JVM overhead
2. **No GC pauses** — predictable latency
3. **Shard-per-core** — no lock contention
4. **Direct I/O** — bypass kernel buffers
5. **DPDK option** — bypass kernel TCP stack
6. **No ZooKeeper roundtrips** for metadata
7. **Optimized memory layout** для CPU caches
8. **Async everything** через Seastar

**Result:** typically 3-10x lower latency, higher throughput per CPU.

## Q13. WASM transforms?

**Redpanda WASM Data Transforms** (с 2023) — execute WebAssembly functions inside broker.

```rust
#[redpanda_transform_sdk::on_record_written]
fn process(event: WriteEvent, writer: RecordWriter) -> Result<()> {
    // Transform record before write
    let transformed = transform(event.record);
    writer.write(transformed)?;
    Ok(())
}
```

**Use cases:**
- Schema migration (transform old → new)
- Filtering / routing
- Lightweight enrichment

**Languages:** Rust, Go, JavaScript (via WASM).

Похоже на **Kafka Streams**, но **inside broker** (no separate process).

## Q14. Built-in HTTP proxy?

**Pandaproxy** — HTTP REST API для Kafka topics.

```bash
# Produce via HTTP
curl -X POST http://redpanda:8082/topics/my-topic \
  -H "Content-Type: application/vnd.kafka.json.v2+json" \
  -d '{"records":[{"value":{"hello":"world"}}]}'

# Consume via HTTP
curl http://redpanda:8082/consumers/my-group/instances/my-instance/records
```

**Use cases:**
- IoT devices без Kafka client
- Browser-side producers
- Simple integrations

Same as **Confluent REST Proxy**, но built-in.

## Q15. Console (UI)?

**Redpanda Console** — web UI для:
- Browse topics, partitions
- Inspect messages
- Manage consumer groups
- View schemas
- Connect cluster管理
- Roles, ACLs

```bash
docker run -p 8080:8080 -e KAFKA_BROKERS=redpanda:9092 \
  docker.redpanda.com/redpandadata/console:latest
```

Аналог **AKHQ, Kafdrop, Kowl** (предыдущая версия Console).

## Q16. (!) Open source vs Enterprise vs Cloud?

**Open source (Free):**
- Source available (BSL license)
- Core streaming features
- Tiered storage

**Enterprise (paid):**
- Audit logging
- Advanced security (SASL/OAuthbearer)
- Configurable per-cluster role-based access (more granular)
- 24/7 support

**Redpanda Cloud (managed):**
- Fully managed на AWS, GCP, Azure
- BYOC option (Bring Your Own Cloud) — runs в **your** AWS account
- Multi-region

В **2025** — растущая популярность managed Redpanda Cloud (alternative Confluent Cloud).

## Q17. Source available license (BSL)?

**Business Source License (BSL)** — same как CockroachDB.

**Restrictions:**
- Cannot **offer Redpanda as a service** к third parties без commercial license
- Otherwise — free для self-host, modify

**Converts к Apache 2.0 после 4 years** (older versions become fully open).

**Effect:** AWS / GCP не могут offer "Redpanda as a Service". Redpanda Data sells managed.

Похоже на **Elastic License**, **Cockroach License**.

## Q18. (!) Когда выбрать Redpanda над Kafka?

**Выбирай Redpanda когда:**
- **Lower latency** critical (financial, gaming, real-time)
- **Simpler ops** matters (single binary)
- **Fewer nodes** to handle workload (cost saving)
- Cloud-native deployments
- Want **predictable p99** (no GC)
- **Smaller team** to manage messaging
- Modern stack, no legacy Kafka dependencies

**Stay с Kafka:**
- Already invested deeply
- Need **ecosystem maturity** (some integrations Kafka-specific)
- Need **specific Kafka features** Redpanda lacks
- Risk-averse organization

## Q19. Когда не выбирать Redpanda?

1. **Bleeding edge needs** — Kafka has more features earlier
2. **Specific tools** только Kafka (rare)
3. **Massive existing Kafka deployment** — migration risk
4. **Less battle-tested** at extreme scale
5. **Smaller community** — fewer answers, blogs
6. **License concerns** (BSL вызывает opinions)
7. **Want completely free, Apache** project (use Kafka)

## Q20. Migration Kafka → Redpanda?

**Approaches:**

1. **MirrorMaker 2** — replicate Kafka → Redpanda continuously, then switch clients
2. **Dual-write** — apps write к both, gradually switch reads
3. **Cut-over** — stop, copy data (rpk import), start

**Most common:** MirrorMaker 2.

**Tools:** `rpk` (Redpanda CLI) для cluster management.

```bash
# Mirror Kafka → Redpanda
rpk topic create my-topic
# Configure MirrorMaker 2 to mirror Kafka к Redpanda
```

**Test thoroughly** перед production cutover.

В **2025** — Redpanda **growing alternative** к Kafka. Especially attractive для **cloud-native** new deployments.

---

## See also

- [Apache Kafka](kafka-interview.md) — main conkurent (Redpanda compatible)
- [Kafka Streams](../data-engineering/kafka-streams-interview.md) — works against Redpanda
- [NATS](nats-interview.md) — another lightweight alternative
- [Apache Pulsar](pulsar-interview.md) — another alternative
- [Message Brokers Comparison](message-brokers-comparison-interview.md) — overview
- [Event-driven Patterns](../architecture/event-driven-patterns-interview.md) — context
- [Микросервисы](../architecture/microservices-interview.md) — primary use case
- [Stream Processing](../data-engineering/stream-processing-interview.md) — context
- [ScyllaDB](../databases/scylladb-interview.md) — same Seastar framework
- [Распределённые системы](../architecture/distributed-systems-interview.md) — Raft, consensus
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — shard-per-core
- [Performance Testing](../performance/performance-testing-interview.md) — benchmarking
- [OpenTelemetry](../monitoring/opentelemetry-interview.md) — Redpanda metrics
