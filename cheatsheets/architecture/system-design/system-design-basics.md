---
title: "Основы проектирования систем (System Design)"
description: "Фреймворк для проектирования масштабируемых backend-систем: формулы оценки нагрузки, выбор компонентов, CAP/PACELC, разбор узких мест и сквозной пример URL Shortener с цифрами."
tags:
  - architecture
  - system-design
  - system-design-basics
  - capacity-planning
  - cap-theorem
type: "overview"
difficulty: "intermediate"
aliases:
  - "Основы проектирования систем"
  - "System Design"
  - "system design basics"
prerequisites:
  - "[[postgres-basics]]"
  - "[[redis-basics]]"
related:
  - "[[architecture-patterns]]"
  - "[[distributed-systems-interview]]"
next:
  - "[[microservices]]"
  - "[[cap-theorem-interview]]"
updated: "2026-04-26"
---
# Основы проектирования систем (System Design)

System Design — это инженерная работа по разложению задачи на требования,
оценке нагрузки в цифрах, выбору компонентов и фиксации компромиссов.
Цель — спроектировать сервис, который выдержит заявленный QPS, переживёт
типовые отказы и даст команде понятные точки развития.

Этот документ — каркас процесса. Раздел `## Оценка масштаба` даёт
формулы, `## CAP и PACELC` — модель консистентности, `## Пример: URL
Shortener` — сквозной разбор от требований до цифр capacity и БД-схемы.

## Полезные ссылки

- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [High Scalability](http://highscalability.com/)
- [Designing Data-Intensive Applications (Kleppmann)](https://dataintensive.net/) — фундамент по распределённым системам
- [The System Design Interview, Vol. 1-2 (Alex Xu)](https://www.amazon.com/System-Design-Interview-insiders-Second/dp/B08CMF2CQF)
- [AWS Well-Architected Framework](https://aws.amazon.com/architecture/well-architected/)
- [Google SRE Book](https://sre.google/sre-book/table-of-contents/) — SLO, error budgets, on-call

### См. также

- [Архитектурные паттерны](../architecture-patterns.md)
- [CAP теорема (interview)](../../interview/architecture/cap-theorem-interview.md)
- [Distributed Systems (interview)](../../interview/architecture/distributed-systems-interview.md)
- [Caching strategies (interview)](../../interview/architecture/caching-strategies-interview.md)

## Содержание

- [Что такое System Design](#что-такое-system-design)
- [Процесс проектирования](#процесс-проектирования)
- [Оценка масштаба](#оценка-масштаба)
  - [Формулы](#формулы)
  - [Полезные константы](#полезные-константы)
  - [Пример расчёта](#пример-расчёта)
- [Основные принципы](#основные-принципы)
  - [Масштабируемость (Scalability)](#масштабируемость-scalability)
  - [Надёжность (Reliability)](#надёжность-reliability)
  - [Производительность (Performance)](#производительность-performance)
- [CAP и PACELC](#cap-и-pacelc)
  - [CAP теорема](#cap-теорема)
  - [PACELC расширение](#pacelc-расширение)
  - [Когда что выбирать](#когда-что-выбирать)
- [Компоненты системы](#компоненты-системы)
  - [Load Balancer](#load-balancer)
  - [API Gateway](#api-gateway)
  - [Database](#database)
  - [Cache](#cache)
  - [Message Queue](#message-queue)
  - [CDN](#cdn)
- [Типичные узкие места](#типичные-узкие-места)
- [Пример: URL Shortener](#пример-url-shortener)
  - [Требования](#требования)
  - [Оценка масштаба](#оценка-масштаба-1)
  - [API контракт](#api-контракт)
  - [Схема БД](#схема-бд)
  - [Архитектура и поток](#архитектура-и-поток)
  - [Алгоритм генерации короткого URL](#алгоритм-генерации-короткого-url)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также-1)

## Что такое System Design

System Design отвечает на четыре вопроса:

- **Что строим.** Список функций и стейкхолдеров.
- **Какая нагрузка.** QPS, объём данных, latency, доступность.
- **Из чего собираем.** Хранилища, кэши, очереди, балансеры, gateway.
- **Какие компромиссы принимаем.** Консистентность vs доступность,
  latency vs стоимость, простота vs гибкость.

Это итеративный процесс: первый набросок — кубики «клиент → LB → API →
БД». Дальше углубляешь по самому уязвимому месту, пока не покрыты все
требования или пока время на дизайн не вышло.

## Процесс проектирования

Стандартный цикл — 6 шагов. На собеседовании каждый занимает ~5-10 минут.

1. **Требования.** Функциональные (что делает) и нефункциональные (RPS,
   latency p95, доступность 99.9 / 99.99, бюджет потерь данных).
2. **Оценка масштаба.** Формулы из `## Оценка масштаба`. Получаешь QPS,
   storage за 5 лет, bandwidth, peak-to-average ratio.
3. **API контракт.** REST/gRPC endpoints, формат запроса/ответа,
   error codes, идемпотентность критичных операций.
4. **Схема данных.** ER-диаграмма, выбор SQL/NoSQL, индексы, стратегия
   шардирования и репликации.
5. **Высокоуровневая архитектура.** Клиент → LB → API → БД/Cache/Queue
   → CDN. Где какой компонент и почему.
6. **Узкие места и trade-offs.** Что ломается первым при росте нагрузки,
   как мы это знаем (метрики), что делаем дальше.

> Принцип: каждое решение — это компромисс. Если на вопрос «почему
> именно так?» нет ответа в терминах «потому что вот это требование», —
> решение преждевременное.

## Оценка масштаба

Без цифр дизайн — вкусовщина. Capacity planning превращает «много
пользователей» в конкретные `RAM`, диск, сеть и количество инстансов.

### Формулы

```text
QPS_avg     = DAU × actions_per_user / 86400
QPS_peak    = QPS_avg × peak_factor       (peak_factor ~ 2-5)
Storage     = items × avg_item_size × replication × retention_factor
Bandwidth   = QPS × avg_response_size
Connections = QPS × avg_response_time
```

`peak_factor` — отношение пиковой нагрузки к средней. Для пользовательских
сервисов обычно 2-3, для офисных рабочих часов — до 10. На тренировке
приёма пиковую нагрузку считают всегда: средняя нагрузка ничего не
говорит о размере кластера.

`Connections = QPS × avg_response_time` — закон Литтла. При 1000 QPS
и 100 мс отклика нужно держать ~100 одновременных соединений. Это
определяет размер `connectionPool` БД, тред-пула приложения и
file descriptor лимиты.

### Полезные константы

| Размер | Эквивалент |
|---|---|
| 1 KB | короткий пост, JSON-ответ среднего endpoint |
| 1 MB | средняя картинка, страница HTML с ассетами |
| 1 GB | ~1 миллион записей по 1 KB |
| 1 TB | ~1 миллиард записей по 1 KB |

| Latency | Источник |
|---|---|
| 0.5 ns | L1 cache CPU |
| 100 ns | RAM |
| 0.1 ms | SSD random read |
| 1 ms | LAN round-trip |
| 30-100 ms | межрегиональный round-trip |
| 100-300 ms | мобильная сеть |

`1M секунд ~ 11.5 дней`, `1B секунд ~ 31 год` — пригождается, когда
оцениваешь storage за период.

### Пример расчёта

Сервис ленты постов, 10M `DAU`, в среднем 5 чтений + 0.5 поста в день,
средний пост 2 KB, хранение 5 лет, репликация ×3.

```text
QPS_avg_read  = 10M × 5 / 86400          ~ 580 QPS
QPS_avg_write = 10M × 0.5 / 86400        ~ 58 QPS
QPS_peak_read = 580 × 3                  ~ 1740 QPS

Posts_per_year = 10M × 0.5 × 365         ~ 1.8B постов/год
Storage_5y     = 1.8B × 2 KB × 5 × 3     ~ 54 TB
                                         (репликация ×3 → 1 шард не справится)

Bandwidth_read = 1740 × 2 KB             ~ 3.5 MB/s
Connections    = 1740 × 50 ms            ~ 87 одновременных
```

Вывод: 87 соединений — поместятся в один app-инстанс с пулом 200, но
54 TB требуют шардирования. Один PostgreSQL хранит до 5-10 TB на ноду
комфортно — нужно ~10 шардов или хранилище с авто-шардированием
(Cassandra, ScyllaDB).

## Основные принципы

### Масштабируемость (Scalability)

**Vertical scaling (scale up).** Увеличение ресурсов одного сервера:
больше CPU, RAM, NVMe. Просто, но упирается в физические лимиты и в
SPOF (single point of failure) — один сервер не даёт `HA`.

**Horizontal scaling (scale out).** Добавление инстансов за `LB`. Сложнее
(нужно распределить состояние), но масштабируется почти линейно и даёт
отказоустойчивость.

```java
class LoadBalancer {
    private final List<Server> servers;
    private final LoadBalancingStrategy strategy;

    Server selectServer(Request request) {
        return strategy.select(servers, request);
    }
}

interface LoadBalancingStrategy {
    Server select(List<Server> servers, Request request);
}

class RoundRobinStrategy implements LoadBalancingStrategy {
    private int current = 0;

    public synchronized Server select(List<Server> servers, Request request) {
        Server server = servers.get(current);
        current = (current + 1) % servers.size();
        return server;
    }
}
```

Горизонтальное масштабирование почти всегда предпочтительнее. Vertical
оставляют для stateful-компонентов, которые сложно шардировать
(primary БД, пока ёмкости хватает).

### Надёжность (Reliability)

Считается через `SLO` — целевую доступность за период. 99.9% («три
девятки») = 8.76 часов простоя в год. 99.99% — уже 52 минуты, требует
автоматического failover, multi-AZ и хорошего runbook.

| Уровень | Допустимый downtime/год |
|---|---|
| 99.0% | 3.65 дня |
| 99.9% | 8.76 часа |
| 99.99% | 52.6 минуты |
| 99.999% | 5.26 минуты |

Базовые приёмы:

- **Репликация.** Стандарт: primary + N реплик, `RF=3` для устойчивости
  к потере одного узла без деградации записи.
- **Резервное копирование.** Бэкап != HA. Бэкап защищает от логических
  ошибок (DROP TABLE), HA — от аппаратных.
- **Health checks + automatic failover.** Без автомата failover SLO
  выше 99.9% невозможен — человек не успевает реагировать.
- **Идемпотентность.** Operation must be safe to retry. Без этого
  retry-логика на уровне сети превращается в дубли заказов.

```java
class DatabaseReplication {
    private final Database primary;
    private final List<Database> replicas;

    void write(Data data) {
        primary.write(data);
        // Асинхронная репликация — eventual consistency у читателей
        replicas.forEach(replica -> replica.writeAsync(data));
    }

    Data read() {
        // Read-from-replica для распределения нагрузки
        return selectReplica().read();
    }
}
```

### Производительность (Performance)

Performance оценивается в latency (p50, p95, p99) и throughput (QPS).
Для пользовательских сервисов целятся в p95 < 200 мс, p99 < 500 мс.

Базовые рычаги:

- **Кэширование.** Снижает нагрузку на БД и latency. Cache-aside
  (приложение управляет кэшем) и read-through (кэш сам подгружает) —
  типовые паттерны.
- **CDN.** Раздача статики и edge-cache для динамики (CloudFront,
  Cloudflare). Сокращает latency через географическую близость.
- **Индексы и денормализация.** B-tree индекс ускоряет SELECT с
  предикатом по колонке. Денормализация уменьшает количество JOIN
  ценой дублирования данных.
- **Async обработка.** Тяжёлую работу (отправка email, генерация
  отчёта) выносят в очередь — клиент получает ответ быстро.

```java
class CacheService {
    private final Cache<String, Object> l1Cache; // in-process
    private final Cache<String, Object> l2Cache; // Redis
    private final Database database;

    Object get(String key) {
        Object value = l1Cache.get(key);
        if (value != null) return value;

        value = l2Cache.get(key);
        if (value != null) {
            l1Cache.put(key, value);
            return value;
        }

        value = database.get(key);
        if (value != null) {
            l2Cache.put(key, value);
            l1Cache.put(key, value);
        }
        return value;
    }
}
```

Cache-инвалидация — отдельная боль. Минимально: TTL + версионирование
ключа при изменении исходных данных.

## CAP и PACELC

Распределённая система не может одновременно гарантировать
консистентность, доступность и устойчивость к сетевым сбоям. CAP и
PACELC формализуют этот компромисс.

### CAP теорема

При сетевом разделе (`Partition`) система может выбрать только одно из:

- **`C` — Consistency.** Все узлы видят одни и те же данные. Запись на
  меньшей половине отклоняется → читатели всегда видят актуальное.
- **`A` — Availability.** Каждый узел отвечает на запрос. На меньшей
  половине читатели видят устаревшие данные.

`P` (`Partition tolerance`) — обязательно: разделы случаются всегда,
пакеты теряются. Поэтому реальный выбор — `CP` или `AP`.

| Класс | Что выбрано | Примеры |
|---|---|---|
| `CP` | Consistency, отклоняет запись при partition | etcd, Consul, MongoDB (по умолчанию primary), HBase |
| `AP` | Availability, разные узлы могут отдать разные значения | Cassandra, DynamoDB (eventual), Riak |

### PACELC расширение

CAP описывает только поведение при partition. PACELC учитывает обычный
режим: при `partition` выбираем `A` или `C`, `Else` (нормально) —
`L`atency или `C`onsistency.

| Система | PACELC | Объяснение |
|---|---|---|
| MongoDB (по умолчанию) | PA/EC | при partition доступность, в норме консистентность |
| Cassandra | PA/EL | оптимизирована под latency, eventual в обоих режимах |
| etcd | PC/EC | строгая консистентность всегда (Raft quorum) |
| Spanner | PC/EC | TrueTime + Paxos гарантирует консистентность ценой latency |

### Когда что выбирать

| Сценарий | Класс | Причина |
|---|---|---|
| Платежи, остатки на счёте | CP / EC | Двойная трата хуже даунтайма |
| Лента постов, лайки | AP / EL | Stale данные простительны, latency критична |
| Distributed lock | CP | Без consensus два процесса возьмут «один» лок |
| Сессии пользователей | AP | Лучше показать чуть устаревшую сессию, чем 503 |
| Конфигурация (feature flags) | CP | Все инстансы должны видеть одно состояние |

## Компоненты системы

### Load Balancer

Распределяет трафик между инстансами. L4 (TCP/UDP, по IP/порту) —
быстрее, не понимает протокол. L7 (HTTP/HTTPS) — может маршрутизировать
по path, header, cookie.

Алгоритмы: `round-robin`, `least-connections`, `consistent hashing`
(когда нужна привязка пользователя к инстансу для кэша).

Реализации: `nginx`, `HAProxy`, `Envoy`, AWS ALB, GCP Cloud Load Balancing.

### API Gateway

Единая точка входа для микросервисов. Делает: auth (JWT, mTLS),
rate-limiting, routing, request/response transformation, кэширование,
сбор метрик.

Реализации: `Kong`, `Traefik`, `AWS API Gateway`, `Spring Cloud Gateway`.

> Не путать LB и API Gateway: LB про распределение трафика, Gateway —
> про обработку запроса (auth, transform). На практике часто стоит
> LB → Gateway → сервисы.

### Database

| Тип | Когда брать | Реализации |
|---|---|---|
| OLTP SQL | Транзакции, сложные JOIN, ACID | PostgreSQL, MySQL |
| OLAP SQL | Аналитика, агрегации по миллиардам строк | ClickHouse, BigQuery, Snowflake |
| Document | Гибкая схема, агрегаты | MongoDB, CouchDB |
| Key-Value | Простой lookup, горячие данные | Redis, DynamoDB |
| Wide-column | Запись с временным рядом, большие объёмы | Cassandra, ScyllaDB |
| Graph | Связи, рекомендации | Neo4j, JanusGraph |
| Search | Полнотекстовый поиск, фасеты | Elasticsearch, OpenSearch |
| Time-series | Метрики, телеметрия | InfluxDB, TimescaleDB, Prometheus |

Шардирование (по hash, range, geo) — когда один узел не справляется по
объёму или QPS. Репликация — когда нужна `HA` или read-сallability.

### Cache

- **Redis** — `key-value` + структуры (list, set, sorted-set, hash),
  Lua-скрипты, pub/sub. Универсальный.
- **Memcached** — проще, только key-value, многопоточный (Redis
  однопоточный по операции).
- **In-process (Caffeine, Guava)** — нулевой network hop, но не
  шарится между инстансами.

Стратегии: `cache-aside`, `read-through`, `write-through`,
`write-behind`. Подробнее — в
[caching strategies interview](../../interview/architecture/caching-strategies-interview.md).

### Message Queue

| Технология | Модель | Когда |
|---|---|---|
| Kafka | append-only log, consumer group | event sourcing, аналитика, высокий throughput (1M+ msg/s) |
| RabbitMQ | broker с exchange + queue | task queue, RPC, гибкая маршрутизация |
| AWS SQS | managed queue, at-least-once | простой fan-out без operation-нагрузки |
| Redis Streams | log внутри Redis | если уже есть Redis и нагрузка скромная |

Ключевая разница Kafka vs RabbitMQ: Kafka — это лог (consumer читает
позицию, может перечитать), RabbitMQ — очередь (сообщение удаляется
после ack).

### CDN

Edge-кэш ближе к пользователю. Раздаёт статику (CSS, JS, картинки,
видео), может кэшировать и динамику по cache-control header.

Реализации: `CloudFront`, `Cloudflare`, `Akamai`, `Fastly`. Для
внутреннего/корпоративного — `Varnish`, `nginx + proxy_cache`.

## Типичные узкие места

| Симптом | Метрика | Причина | Лечение |
|---|---|---|---|
| Высокий p99 у БД | `pg_stat_statements`, EXPLAIN ANALYZE | Нет индекса, full scan | Индекс, переписать запрос, денормализация |
| CPU 100% на app | profiler, flame graph | Тяжёлая логика на горячем пути | Кэш, async-обработка, оптимизация алгоритма |
| Всплески latency раз в N минут | GC log | GC pauses (G1 mixed GC) | Tune heap, перейти на ZGC/Shenandoah |
| Connection pool exhausted | HikariCP metrics | Слишком долгие транзакции, утечка соединений | Сократить транзакции, поймать утечку |
| Out of memory | container metrics | Неограниченный кэш, утечка | Ограничение размера, weak references |
| Файлы дескрипторов кончаются | `lsof`, `ulimit` | Не закрытые соединения | `try-with-resources`, проверка close в стеке |
| Очередь растёт быстрее, чем обработчик | lag | Producer быстрее consumer | Шкалировать consumer, увеличить partitions, batch processing |

Принцип диагностики: **сначала метрики, потом код**. Без метрик ты
лечишь не ту проблему.

## Пример: URL Shortener

Сквозной разбор: от требований до цифр и схемы. Принцип — не «как
работает», а «как обоснованно выбрать».

### Требования

**Функциональные:**

- Принимает длинный URL, возвращает короткий (`https://short.ly/aB3xZ`).
- Редирект с короткого на длинный.
- TTL для ссылок (опционально, у одних бесплатных тарифов есть, у
  бизнеса часто бессрочно).

**Нефункциональные:**

- 100M новых ссылок в день (~1.2K writes/sec средние).
- Read:Write ~ 100:1 (редирект — основная нагрузка).
- Latency: p99 редиректа < 50 мс.
- Доступность 99.99% (это публичный сервис).
- Хранение 5 лет.

### Оценка масштаба

```text
Writes_avg = 100M / 86400               ~ 1160 writes/sec
Writes_peak = 1160 × 3                  ~ 3500 writes/sec
Reads_avg  = 1160 × 100                 ~ 116K reads/sec
Reads_peak = 116K × 3                   ~ 350K reads/sec

Storage_5y = 100M × 365 × 5 × 500 B × 3 ~ 270 TB
            (record ~500 B: ID, long_url, short_code, ts)
            (×3 — replication factor)

Cache_hot  = 20% × 100M × 5y × 500 B    ~ 18 TB
            (если кэшируем 20% самых горячих → не помещается в RAM
             одной машины, нужен Redis cluster или sharded cache)
```

Числа диктуют решения: 350K reads/sec через одну ноду PostgreSQL не
пройдут (предел ~50-80K на хороший сервер). Нужны: read replicas + кэш
+ возможно sharding.

### API контракт

```text
POST /api/shorten
  body: { "url": "https://example.com/very/long/path?q=1" }
  201:  { "short_url": "https://short.ly/aB3xZ", "code": "aB3xZ" }
  400:  invalid URL
  429:  rate limit exceeded

GET /{code}
  302:  Location: https://example.com/very/long/path?q=1
  404:  unknown code
  410:  expired (если TTL истёк)
```

`POST /api/shorten` идемпотентен по `Idempotency-Key` header — повтор
запроса с тем же ключом не создаст вторую запись.

### Схема БД

```sql
CREATE TABLE urls (
    id          BIGINT PRIMARY KEY,
    short_code  VARCHAR(10) NOT NULL UNIQUE,
    long_url    TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMP NULL,
    user_id     BIGINT NULL,
    click_count BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_urls_created_at ON urls(created_at);
CREATE INDEX idx_urls_user_id ON urls(user_id) WHERE user_id IS NOT NULL;
```

`short_code` — уникальный, хеш-индекс быстрее B-tree для точного
lookup. `id BIGINT` нужен для распределённого ID-генератора (Snowflake,
ULID), чтобы избежать конкуренции на sequence.

### Архитектура и поток

```text
                          ┌──── Redis Cluster (hot codes)
                          │
Client → CDN → LB → API ──┤
                          │
                          └──── PostgreSQL (primary + 5 read replicas)
                                          │
                                          └─── шардирование по hash(short_code) % N
```

**Поток редиректа:**

1. CDN не помогает (302 редирект уникален), сразу LB.
2. API смотрит в Redis по `short_code`. Hit → 302 (95% запросов).
3. Miss → читает из read replica, кладёт в Redis с TTL 1 час.
4. Возвращает 302.

**Поток создания:**

1. API проверяет idempotency-key.
2. Генерирует ID через Snowflake.
3. Кодирует в base62 → `short_code`.
4. Запись в primary БД.
5. Ответ клиенту.

### Алгоритм генерации короткого URL

```java
class URLShortener {
    private static final String BASE62 =
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final IdGenerator idGenerator; // Snowflake ID, 64 бита
    private final UrlRepository repository;
    private final Cache cache;

    String shorten(String longUrl, String idempotencyKey) {
        String existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing != null) return existing;

        long id = idGenerator.next();
        String shortCode = encodeBase62(id);
        repository.save(id, shortCode, longUrl, idempotencyKey);
        return "https://short.ly/" + shortCode;
    }

    Optional<String> resolve(String shortCode) {
        String cached = cache.get(shortCode);
        if (cached != null) return Optional.of(cached);

        Optional<String> longUrl = repository.findByCode(shortCode);
        longUrl.ifPresent(url -> cache.put(shortCode, url, Duration.ofHours(1)));
        return longUrl;
    }

    private static String encodeBase62(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(BASE62.charAt((int)(id % 62)));
            id /= 62;
        }
        return sb.reverse().toString();
    }
}
```

База `62^7 = 3.5 трлн` уникальных кодов — хватит на десятилетия даже
при 100M в день.

## Лучшие практики

- Начинай с цифр: без QPS, storage и latency требований дизайн —
  фантазии. Получишь цифры — выбор компонентов становится почти
  механическим.
- Один документ на сервис — `RFC` или `ADR`. В нём: требования,
  capacity, схема, trade-offs. Без этого через полгода никто не
  вспомнит, почему выбрали Cassandra.
- Закладывай метрики, логи и трейсинг с первого спринта. Добавлять их
  в готовый legacy дольше, чем писать код заново.
- Идемпотентность критичных операций — обязательна. Без этого retry
  на уровне сети превращается в баги бизнес-логики.
- Горизонтальное масштабирование по умолчанию. Vertical — только для
  компонентов, которые сложно шардировать.
- Health checks + автоматический failover — без автомата SLO выше
  99.9% не достичь.
- При выборе БД сначала закрой ACID-требования (нужны ли транзакции),
  потом думай о схеме и масштабе.
- Любая «временная заплатка» имеет шанс прожить десять лет — пиши
  даже временное так, чтобы не было стыдно.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---|---|---|
| Узкое место БД при росте трафика | Нет кэша или индекса; один узел | Кэш `Redis`; индексы по горячим запросам; read replicas; шардирование |
| `503` под нагрузкой | App упирается в connection pool / тред-пул | Увеличить пул; уменьшить продолжительность транзакций; backpressure |
| Latency p99 в разы больше p50 | GC pauses, контеншн на блокировках, медленные запросы | Профилировка; tune GC; найти long-running query; разделить hot path |
| Каскадный отказ | Нет circuit breaker, retry-storm | Resilience4j / Hystrix; retry с jitter; bulkhead — изолировать пулы |
| Данные в реплике устаревают на минуты | Async replication; consumer lag | Streaming replication (PG); semi-sync; читать с primary для критичных запросов |
| Очередь растёт | Consumer медленнее producer | Шкалировать consumers; увеличить partitions; batch processing; dead-letter queue для poison messages |
| Один пользователь съел весь rate-limit | Нет per-tenant изоляции | Token bucket с лимитом per user/IP; 429 Too Many Requests |

## Частые вопросы

**С чего начать проектирование системы?** С требований: функциональных
и нефункциональных. Затем оценка масштаба в цифрах. Только после этого —
выбор компонентов. Без цифр выбор будет вкусовщиной.

**Когда нужен кэш?** При высоком read:write ratio (≥10:1) и допустимой
задержке инвалидации. Размещай ближе к потребителю (in-process →
Redis → CDN). Сразу продумай инвалидацию — это сложнее самой кэш-логики.

**Как обеспечить консистентность в распределённой системе?**
Строгая — ограничь репликацию (primary + sync replicas, consensus
через Raft). Для масштаба часто берут eventual consistency и компенсируют
бизнес-логикой: идемпотентность, версионирование, сверки.

**SQL или NoSQL?** Если нужны транзакции, JOIN и стабильная схема — SQL.
Если данные слабо связаны, схема меняется или объёмы > десятков TB —
NoSQL по соответствующей модели (KV, document, wide-column).

**Что делать при 100K+ QPS?** Кэш агрессивный, шардирование БД,
horizontal scale за LB, async-обработка тяжёлых операций, CDN для
статики. Каждый компонент проверь на SPOF.

**Как считать «сколько нужно серверов»?** `N = QPS_peak ×
avg_response_time / target_concurrency_per_server`. Запас 20-50% на
случай отказа узлов. Для пика добавь ещё 30% буфер для авто-скейлинга.

## См. также

- [Architectural Decision Records](../architectural-decision-records/adr-template.md) — шаблон ADR
- [Design Principles](../design-principles/design-principles.md) — SOLID, DRY, KISS
- [Enterprise Patterns](../enterprise-patterns/enterprise-patterns-overview.md) — обзор enterprise-паттернов
- [Microservices](../software-architecture/microservices.md) — микросервисная архитектура
- [Architecture Patterns](../architecture-patterns.md) — каталог архитектурных паттернов
- [CAP теорема (interview)](../../interview/architecture/cap-theorem-interview.md) — углублённый разбор
- [Distributed Systems (interview)](../../interview/architecture/distributed-systems-interview.md) — Raft, Paxos, leader election
