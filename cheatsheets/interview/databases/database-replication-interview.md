---
title: "Вопросы на собеседовании: Database Replication"
description: "Репликация БД: single-leader, multi-leader, leaderless; sync/async, logical/physical; MySQL/PostgreSQL/Cassandra/MongoDB; failover, split-brain, conflict resolution."
tags:
  - interview
  - databases
  - database-replication
type: "interview"
difficulty: "intermediate"
aliases:
  - "Database Replication interview"
  - "Репликация БД собеседование"
  - "Master-slave master-master"
  - "MySQL PostgreSQL replication"
updated: "2026-05-21"
---
# Вопросы на собеседовании: `Database Replication`

Репликация БД: `single-leader` (`primary-replica`), `multi-leader`, `leaderless`; `sync` vs `async`; `logical` vs `physical`; конкретика по `MySQL`, `PostgreSQL`, `Cassandra`, `MongoDB`; failover, split-brain, conflict resolution, CDC.

**Database Replication** — это поддержание копий одних и тех же данных на нескольких узлах. Цели: высокая доступность (`HA`), масштабирование чтений (`read scaling`), географическое распределение (`geo-distribution`), disaster recovery, разгрузка аналитики. На собеседованиях спрашивают про модели репликации, trade-off `sync` vs `async`, обработку конфликтов, реплик-лаг, failover и split-brain.

## Полезные ссылки

### Книги и теория

- [Designing Data-Intensive Applications, ch. 5 (Replication)](https://dataintensive.net/) — Martin Kleppmann, базовая глава
- [Database Internals, ch. 11-12](https://www.databass.dev/) — Alex Petrov

### Официальная документация

- [PostgreSQL: High Availability, Load Balancing, and Replication](https://www.postgresql.org/docs/current/high-availability.html)
- [PostgreSQL: Streaming Replication Protocol](https://www.postgresql.org/docs/current/protocol-replication.html)
- [PostgreSQL: Logical Replication](https://www.postgresql.org/docs/current/logical-replication.html)
- [MySQL: Replication Overview](https://dev.mysql.com/doc/refman/8.0/en/replication.html)
- [MySQL: GTID-Based Replication](https://dev.mysql.com/doc/refman/8.0/en/replication-gtids.html)
- [MySQL: Semisynchronous Replication](https://dev.mysql.com/doc/refman/8.0/en/replication-semisync.html)
- [MongoDB: Replica Sets](https://www.mongodb.com/docs/manual/replication/)
- [Cassandra: Data Replication](https://cassandra.apache.org/doc/latest/cassandra/architecture/dynamo.html)

### Инструменты и CDC

- [Debezium Documentation](https://debezium.io/documentation/) — CDC через replication log
- [Patroni — PostgreSQL HA](https://patroni.readthedocs.io/)
- [Orchestrator — MySQL HA](https://github.com/openark/orchestrator)
- [AWS RDS Read Replicas](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_ReadRepl.html)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Зачем нужна репликация и какие задачи она решает?](#q1--зачем-нужна-репликация-и-какие-задачи-она-решает)
- [Q2. (!) Какие модели репликации существуют?](#q2--какие-модели-репликации-существуют)
- [Q3. (!) Sync vs Async vs Semi-sync репликация — в чём разница?](#q3--sync-vs-async-vs-semi-sync-репликация--в-чём-разница)
- [Q4. (!) Logical vs Physical репликация — отличия и применимость](#q4--logical-vs-physical-репликация--отличия-и-применимость)
- [Q5. Replication vs Backup — в чём разница?](#q5-replication-vs-backup--в-чём-разница)

**Single-leader (master-slave / primary-replica)**
- [Q6. (!) Как устроена single-leader репликация?](#q6--как-устроена-single-leader-репликация)
- [Q7. Что такое replication lag и как его измерять?](#q7-что-такое-replication-lag-и-как-его-измерять)
- [Q8. Read-your-writes и monotonic reads consistency](#q8-read-your-writes-и-monotonic-reads-consistency)
- [Q9. Какие проблемы у async репликации?](#q9-какие-проблемы-у-async-репликации)

**Multi-leader**
- [Q10. (!) Когда оправдан multi-leader (master-master)?](#q10--когда-оправдан-multi-leader-master-master)
- [Q11. (!) Conflict resolution: LWW, version vectors, CRDT](#q11--conflict-resolution-lww-version-vectors-crdt)

**Leaderless**
- [Q12. (!) Как работает leaderless репликация (Cassandra, Dynamo)?](#q12--как-работает-leaderless-репликация-cassandra-dynamo)
- [Q13. Что такое quorum (R + W > N) и зачем он нужен?](#q13-что-такое-quorum-r--w--n-и-зачем-он-нужен)
- [Q14. Hinted handoff и anti-entropy repair](#q14-hinted-handoff-и-anti-entropy-repair)

**MySQL specifics**
- [Q15. (!) Как устроена репликация в MySQL?](#q15--как-устроена-репликация-в-mysql)
- [Q16. Statement-based vs Row-based vs Mixed binlog](#q16-statement-based-vs-row-based-vs-mixed-binlog)
- [Q17. Что такое GTID и зачем он нужен?](#q17-что-такое-gtid-и-зачем-он-нужен)
- [Q18. Semi-sync replication в MySQL — как настроить?](#q18-semi-sync-replication-в-mysql--как-настроить)

**PostgreSQL specifics**
- [Q19. (!) Как устроена streaming replication в PostgreSQL?](#q19--как-устроена-streaming-replication-в-postgresql)
- [Q20. Replication slots — зачем нужны?](#q20-replication-slots--зачем-нужны)
- [Q21. Logical replication через publications/subscriptions](#q21-logical-replication-через-publicationssubscriptions)
- [Q22. Synchronous_commit и synchronous_standby_names](#q22-synchronous_commit-и-synchronous_standby_names)

**MongoDB и Cassandra**
- [Q23. Как устроен replica set в MongoDB?](#q23-как-устроен-replica-set-в-mongodb)
- [Q24. Replication factor и snitches в Cassandra](#q24-replication-factor-и-snitches-в-cassandra)

**Failover и Split-brain**
- [Q25. (!) Как работает автоматический failover?](#q25--как-работает-автоматический-failover)
- [Q26. (!) Что такое split-brain и как его избегать?](#q26--что-такое-split-brain-и-как-его-избегать)
- [Q27. Read replicas в облаке: RDS, Aurora, Cloud SQL](#q27-read-replicas-в-облаке-rds-aurora-cloud-sql)

**CDC и Cross-region**
- [Q28. (!) Что такое Change Data Capture (CDC)?](#q28--что-такое-change-data-capture-cdc)
- [Q29. Репликация через Kafka и Debezium](#q29-репликация-через-kafka-и-debezium)
- [Q30. Cross-region репликация: trade-off и подводные камни](#q30-cross-region-репликация-trade-off-и-подводные-камни)
- [Q31. Anti-patterns репликации](#q31-anti-patterns-репликации)

---

## Q1. (!) Зачем нужна репликация и какие задачи она решает?

**Репликация** — хранение копий данных на нескольких узлах для устойчивости и масштабирования.

**Основные цели:**

| Цель | Что даёт | Пример |
|---|---|---|
| **High Availability** | работа при падении узла | replica становится primary при отказе |
| **Read scaling** | разгрузка чтений | аналитика читает с replica, OLTP пишет в primary |
| **Geo-distribution** | низкая латентность для пользователей | replica в каждом регионе |
| **Disaster recovery** | RPO/RTO для катастроф | replica в другом ДЦ |
| **Analytics offload** | тяжёлые запросы не блокируют OLTP | BI-отчёты с read-replica |
| **Rolling upgrades** | апгрейд без даунтайма | по очереди обновляем реплики |

**Что НЕ решает репликация:**
- Не заменяет backup (логические ошибки `DROP TABLE` улетят на реплики).
- Не масштабирует записи в single-leader (все writes идут в один узел).
- Не даёт автоматически strong consistency (нужны дополнительные механизмы).

**Метрики:**
- `RPO` (Recovery Point Objective) — допустимая потеря данных в секундах.
- `RTO` (Recovery Time Objective) — допустимое время восстановления.
- `Replication lag` — задержка между primary и replica.

---

## Q2. (!) Какие модели репликации существуют?

Три фундаментальные модели:

```mermaid
flowchart LR
  subgraph SL["Single-leader"]
    C1[Client] --> L1[Leader]
    L1 --> R1[Replica 1]
    L1 --> R2[Replica 2]
  end

  subgraph ML["Multi-leader"]
    C2[Client] --> L2A[Leader A]
    C2 --> L2B[Leader B]
    L2A <--> L2B
    L2A --> R3[Replica]
    L2B --> R4[Replica]
  end

  subgraph LL["Leaderless"]
    C3[Client] --> N1[Node 1]
    C3 --> N2[Node 2]
    C3 --> N3[Node 3]
    N1 <--> N2
    N2 <--> N3
    N1 <--> N3
  end
```

| Модель | Writes | Reads | Примеры | Конфликты |
|---|---|---|---|---|
| **Single-leader** | только primary | primary + replicas | `PostgreSQL`, `MySQL`, `MongoDB` (replica set) | нет |
| **Multi-leader** | любой leader | любой узел | `MySQL Group Replication`, `PostgreSQL BDR`, `CouchDB` | да, нужен resolution |
| **Leaderless** | любой узел (через клиент/координатор) | любой узел | `Cassandra`, `DynamoDB`, `Riak` | да, через quorum + version vectors |

**Когда что выбирать:**
- `Single-leader` — дефолт для OLTP, простая консистентность.
- `Multi-leader` — multi-region writes с локальной латентностью.
- `Leaderless` — экстремальная доступность, write-heavy workload, eventual consistency допустима.

---

## Q3. (!) Sync vs Async vs Semi-sync репликация — в чём разница?

Это **trade-off между durability и latency**.

| Режим | Поведение | Durability | Latency | Доступность |
|---|---|---|---|---|
| **Sync** | primary ждёт `ACK` от всех реплик | максимальная | высокая | падает при отказе реплики |
| **Async** | primary не ждёт реплик | низкая (возможна потеря) | минимальная | высокая |
| **Semi-sync** | primary ждёт `ACK` от ≥1 реплики | средняя | средняя | средняя |

**Sync replication:**
- Все реплики подтвердили запись → клиент получает `OK`.
- Гарантирует zero data loss при failover.
- Если хоть одна реплика тормозит — тормозит всё.
- Редко используется в чистом виде на > 2 узлах.

**Async replication:**
- Primary пишет в свой WAL и сразу отвечает клиенту.
- Реплики догоняют асинхронно.
- При падении primary можно потерять последние транзакции.
- Дефолт в `MySQL`, `PostgreSQL` (можно включить sync).

**Semi-sync (компромисс):**
- Primary ждёт `ACK` от **минимум одной** реплики (не всех).
- Реализован в `MySQL` (`rpl_semi_sync_master_enabled`).
- В `PostgreSQL` через `synchronous_commit = on` + `synchronous_standby_names = 'ANY 1 (replica1, replica2)'`.
- Если ни одна реплика не отвечает за `rpl_semi_sync_master_timeout` — деградирует до `async`.

**Правило:** один sync replica + N async replicas — типовая боевая конфигурация.

---

## Q4. (!) Logical vs Physical репликация — отличия и применимость

| Аспект | Physical | Logical |
|---|---|---|
| **Что передаётся** | байты страниц / WAL-записи | логические операции (`INSERT`/`UPDATE`/`DELETE`) |
| **Версии** | требуется одинаковая major-версия | можно реплицировать между разными версиями |
| **Архитектура** | одинаковая (x86 vs ARM) | любая |
| **Гранулярность** | вся БД | таблицы/схемы выборочно |
| **Реплика writable** | read-only | writable |
| **Производительность** | быстрее | медленнее (overhead на decode) |
| **Применимость** | HA, hot standby | upgrade, миграция, ETL, CDC |

**Physical (PostgreSQL streaming):**
- WAL передаётся побайтово через `pg_basebackup` + streaming.
- Replica — бит-в-бит копия primary.
- Не работает между разными PG-версиями.
- Read-only, но позволяет hot standby queries.

**Logical (PostgreSQL publications):**
- `pg_logical_decoding` парсит WAL в логические события.
- Реплика хранит данные в своих файлах, может иметь свои индексы.
- Поддерживает selective replication (только нужные таблицы).
- Не реплицирует `DDL` (нужны hooks или `event triggers`).

**MySQL:** репликация по умолчанию **logical** (binlog), но row-based binlog (`RBR`) приближается к физическому копированию изменённых строк.

---

## Q5. Replication vs Backup — в чём разница?

Часто путают, но это **разные инструменты для разных угроз**.

| Аспект | Replication | Backup |
|---|---|---|
| **Что защищает** | сбой железа / сети / узла | логические ошибки, ransomware, человеческий фактор |
| **Real-time** | да | нет (точки во времени) |
| **Восстановление** | failover за секунды/минуты | restore за часы |
| **Защищает от `DROP TABLE`** | нет (ошибка реплицируется) | да |
| **Хранение** | online, дорогое | offline/cold storage, дешёвое |
| **Тестирование** | в составе HA-тестов | отдельные restore-drill |

**Правило:** реплики **не заменяют** бэкапы. Если разработчик сделает `DELETE FROM users` без `WHERE` — DELETE улетит на все реплики за миллисекунды.

**Boring но обязательно:**
- `PostgreSQL`: `pg_basebackup` + WAL archiving + `pg_dump` для логических.
- `MySQL`: `mysqldump`/`mysqlpump`/`Xtrabackup` + binlog для PITR.
- Тестировать восстановление **регулярно** (не реже раза в квартал).

---

## Q6. (!) Как устроена single-leader репликация?

**Single-leader (primary-replica, master-slave):**

1. Клиенты пишут **только в primary** (leader).
2. Primary логирует изменения в **replication log** (WAL в PG, binlog в MySQL, oplog в MongoDB).
3. Replicas (followers) применяют этот лог в своём порядке.
4. Чтения могут идти на primary или replicas.

**Поток данных:**

```
Client → Primary → [write to WAL] → [send to replicas] → Replicas apply
                                                       ↓
                                                  ACK to client (sync only)
```

**Ключевые свойства:**
- Нет конфликтов (один источник истины).
- Простая модель консистентности.
- Primary — bottleneck для записей.
- При отказе primary нужен **failover**.

**Stateful компоненты:**
- Position в WAL/binlog: `pg_lsn` (`0/3000060`), `binlog file:position`, `GTID`.
- Replication slots (PG) — гарантия, что WAL не удалится пока replica не догнала.

**Типичные топологии:**
- 1 primary + 2 replicas (HA + read scaling).
- 1 primary + 1 sync replica + N async replicas.
- Cascade replication: replica → replica (снижает нагрузку на primary).

---

## Q7. Что такое replication lag и как его измерять?

**Replication lag** — задержка между моментом коммита на primary и применением изменения на replica.

**Причины:**
- Большие транзакции (long-running `UPDATE`/`DELETE`).
- Slow network между ДЦ.
- Слабое железо replica (IOPS, CPU).
- Single-threaded apply (исторически в MySQL до multi-thread replication).
- Lock contention на replica при read queries.
- Тяжёлые DDL на primary.

**Как мерить:**

`PostgreSQL`:
```sql
-- На primary:
SELECT client_addr, state, sent_lsn, write_lsn, flush_lsn, replay_lsn,
       pg_wal_lsn_diff(sent_lsn, replay_lsn) AS bytes_behind
FROM pg_stat_replication;

-- На replica:
SELECT now() - pg_last_xact_replay_timestamp() AS lag;
```

`MySQL`:
```sql
SHOW REPLICA STATUS\G
-- Seconds_Behind_Source: 0
-- Replica_IO_Running: Yes
-- Replica_SQL_Running: Yes
```

`MongoDB`:
```javascript
rs.printSecondaryReplicationInfo()
// или
db.getReplicationInfo()
```

**Как лечить:**
- Дробить большие транзакции на батчи.
- Multi-threaded apply (`replica_parallel_workers` в MySQL 8, `max_parallel_apply_workers_per_subscription` в PG 16).
- Вынести аналитику на отдельную replica с большим лагом.
- Мониторить алерты на `lag > X seconds`.

**Anti-pattern:** строить логику «прочитать после записи» с replica без проверки lag.

---

## Q8. Read-your-writes и monotonic reads consistency

**Проблема async реплик:** клиент записал → читает с replica → не видит свою запись (replica ещё не догнала).

**Read-your-writes consistency** — клиент **всегда видит свои собственные** записи.

**Реализация:**
- **Sticky sessions** — пользователь читает с того же узла, куда писал.
- **Read after own write идёт на primary** — для X секунд после write.
- **LSN tracking** — клиент хранит `last_write_lsn`, replica ждёт пока её `replay_lsn >= last_write_lsn`.
  - PostgreSQL: `pg_wal_replay_wait` (PG 18) или application-level.
- **Causal consistency tokens** (`MongoDB`):
  ```javascript
  const session = client.startSession({ causalConsistency: true });
  ```

**Monotonic reads consistency** — пользователь не видит «откат во времени» (то, что увидел раз — увидит и впредь).

**Реализация:**
- Hash user_id → конкретная replica (`consistent hashing`).
- Версионирование на клиенте.

**Anti-pattern:**
```java
userRepo.save(user);              // primary
User u = userRepo.findById(id);   // replica — может вернуть старое!
return u;
```
**Fix:** force primary для read после save, или используй causal consistency token.

---

## Q9. Какие проблемы у async репликации?

**1. Data loss при failover.**
Если primary упал, не успев отреплицировать последние транзакции — они **потеряны навсегда** (или останутся в "ghost data" на старом primary).

**2. Replication lag inconsistency.**
- Клиент пишет → читает → видит старое.
- Особенно болезненно: dashboard показывает «save successful», но f5 показывает старые данные.

**3. Cascade failover проблемы.**
- При выборе нового primary из реплик с разным lag — выбирается тот, у кого минимальный lag.
- Реплики с большим lag нужно «перематывать» (`pg_rewind`, `mysqlbinlog`) или пересоздавать.

**4. Split-brain после неудачного failover.**
- Старый primary поднимается → думает, что он primary → принимает writes.
- Новый primary тоже принимает writes.
- Расходящиеся истории нельзя слить автоматически.

**5. Длинные транзакции «душат» реплики.**
- Long-running `VACUUM`/`UPDATE` на primary → replicas не могут применить, накапливается lag.

**Митигации:**
- Мониторинг lag и алерты.
- Semi-sync для критичных данных.
- Fencing/STONITH старого primary при failover.
- Идемпотентные операции с дедупликацией на стороне приложения.

---

## Q10. (!) Когда оправдан multi-leader (master-master)?

**Multi-leader** — несколько узлов принимают writes, изменения распространяются между ними.

**Когда оправдан:**

| Use case | Причина |
|---|---|
| **Multi-region writes** | пользователи в разных регионах пишут локально с низкой латентностью |
| **Offline-first apps** | мобильные клиенты пишут локально, синхронизируются позже (CouchDB/PouchDB) |
| **Collaborative editing** | Google Docs, Figma — одновременные правки разных пользователей |
| **HA с возможностью писать в любую ноду** | при отказе одного leader-а writes идут в другой |

**Когда НЕ оправдан:**
- Простой OLTP с одним ДЦ — добавляет сложность conflict resolution без выгоды.
- Финансовые транзакции с strong consistency — конфликты опасны.
- Если нет понятной стратегии разрешения конфликтов.

**Реализации:**
- `MySQL Group Replication` + ProxySQL.
- `PostgreSQL BDR` (Bi-Directional Replication, EDB).
- `pglogical` для PG.
- `CouchDB` (изначально multi-master).
- `Galera Cluster` для MySQL/MariaDB (sync multi-master).

**Главная сложность — conflicts.** Когда два leader одновременно меняют одну строку.

---

## Q11. (!) Conflict resolution: LWW, version vectors, CRDT

В multi-leader и leaderless конфликты неизбежны. Стратегии разрешения:

| Стратегия | Как работает | Pros | Cons |
|---|---|---|---|
| **Last-Write-Wins (LWW)** | побеждает запись с большим timestamp | просто | потеря данных при clock skew |
| **Version vectors** | каждый узел инкрементит свой счётчик | детектирует concurrent updates | сложнее, нужно application merge |
| **CRDT** | структуры данных, у которых merge всегда работает | автоматический merge | ограниченный набор операций |
| **Application-level merge** | приложение решает | гибко | сложно, ошибочно |
| **Reject conflicts** | вторая запись отклоняется | строго | плохой UX |

**LWW (Last-Write-Wins):**
- Cassandra, DynamoDB по умолчанию.
- Опасность: clocks между узлами расходятся (NTP может ошибиться на сотни мс).
- При concurrent writes одна запись **тихо теряется**.

**Version vectors:**
- Каждый узел A,B,C ведёт счётчик: `{A:3, B:5, C:2}`.
- При write инкрементит свой счётчик.
- Если вектор `V1` ≤ `V2` поэлементно → `V2` новее.
- Если несравнимы (`V1={A:3,B:5}`, `V2={A:5,B:3}`) → **concurrent**, нужен merge.

**CRDT (Conflict-free Replicated Data Types):**
- Структуры с математически доказанной свойством convergence.
- Примеры: G-Counter, OR-Set, LWW-Element-Set, RGA (текст).
- Используются в Redis Enterprise (CRDB), Riak, Yjs/Automerge для collab apps.

```java
// G-Counter (grow-only counter)
class GCounter {
    Map<String, Long> nodeCounts = new HashMap<>();
    void increment(String nodeId) { nodeCounts.merge(nodeId, 1L, Long::sum); }
    long value() { return nodeCounts.values().stream().mapToLong(Long::longValue).sum(); }
    void merge(GCounter other) {
        other.nodeCounts.forEach((k, v) -> nodeCounts.merge(k, v, Math::max));
    }
}
```

---

## Q12. (!) Как работает leaderless репликация (Cassandra, Dynamo)?

**Leaderless (Dynamo-style):** нет понятия leader, любой узел принимает reads/writes.

**Ключевые концепции:**
- **Replication factor (RF)** — сколько копий каждого ключа (`RF=3` стандарт).
- **Consistent hashing** — ключ маппится на токен в ring, копии на следующих узлах.
- **Quorum reads/writes** — клиент ждёт `W` подтверждений на write, `R` ответов на read.
- **Hinted handoff** — если узел недоступен, его сосед хранит «подсказку» и доставит позже.
- **Read repair** — при чтении расхождения координатор пишет актуальную версию.
- **Anti-entropy repair** — периодический фоновой `nodetool repair` сверяет реплики через Merkle trees.

**Поток write:**
```
Client → Coordinator (любой узел) → forward to RF=3 replicas
                                  ← W ACKs (например, W=2)
        ← OK to client
```

**Поток read:**
```
Client → Coordinator → query R=2 replicas
                    ← responses (возможно, с разными версиями)
                    → return latest (по timestamp) + async read repair
```

**Примеры:**
- `Apache Cassandra` — Dynamo-inspired.
- `Amazon DynamoDB` — оригинал Dynamo paper.
- `Riak` — также Dynamo-style.
- `ScyllaDB` — Cassandra-compatible.

---

## Q13. Что такое quorum (R + W > N) и зачем он нужен?

**Quorum** — кворумные правила для чтений и записей в leaderless системах.

**Параметры:**
- `N` — replication factor (сколько копий хранится).
- `W` — сколько узлов должны подтвердить write.
- `R` — сколько узлов должны ответить на read.

**Правило strong consistency:** `R + W > N`.

**Почему работает:**
- Если `W` копий подтвердили запись, и `R` копий участвуют в чтении → как минимум одна копия пересечётся → клиент увидит свежую версию.

**Типовые настройки для N=3:**

| W | R | Свойства |
|---|---|---|
| 3 | 1 | sync writes, fast reads (R=ONE, W=ALL) |
| 1 | 3 | fast writes, sync reads |
| **2** | **2** | **баланс (QUORUM)** |
| 1 | 1 | eventual consistency, max throughput |
| 3 | 3 | максимальная strong consistency, нет fault tolerance |

**Cassandra consistency levels:**
- `ONE`, `TWO`, `THREE` — N узлов.
- `QUORUM` — `floor(RF/2) + 1`.
- `LOCAL_QUORUM` — quorum в локальном ДЦ.
- `EACH_QUORUM` — quorum в каждом ДЦ.
- `ALL` — все реплики.

**Caveats:**
- При partial failure write (записал W=2, но один умер до confirmation) — sloppy quorum может записать на "wrong" узлы → hinted handoff.
- Strict quorum: writes на правильные узлы или fail. Sloppy quorum: writes куда угодно.

---

## Q14. Hinted handoff и anti-entropy repair

**Hinted handoff:**
- Узел A пишет → координатор должен реплицировать на B, но B недоступен.
- Координатор сохраняет «подсказку» (hint) локально: «когда B вернётся, отдай ему этот write».
- Когда B возвращается → hints передаются ему → B применяет.
- В Cassandra: `max_hint_window_in_ms = 3 hours` (по умолчанию).
- **Если B отсутствует дольше hint window — hints удаляются**, реплика остаётся stale.

**Read repair:**
- При read с `R=QUORUM` координатор получает разные версии.
- Возвращает свежую клиенту.
- Асинхронно пишет свежую версию на устаревшие реплики.

**Anti-entropy repair (`nodetool repair`):**
- Полная сверка между репликами через **Merkle trees**.
- Узлы строят хеш-деревья своих данных, сравнивают, передают только различающиеся ranges.
- Обязательно запускать **раз в `gc_grace_seconds`** (default 10 дней).
- Если не запустить — tombstones удалятся, удалённые данные могут «воскреснуть».

**Anti-pattern:** забыть `nodetool repair` → zombie data (удалённые записи возвращаются).

---

## Q15. (!) Как устроена репликация в MySQL?

**Архитектура:**
- **Binary log (binlog)** на master — последовательный лог всех изменений.
- **IO thread** на replica — читает binlog с master и сохраняет в **relay log**.
- **SQL thread** (или workers) на replica — применяет relay log.

```mermaid
sequenceDiagram
    participant App as Client
    participant M as MySQL Master
    participant R as MySQL Replica

    App->>M: INSERT / UPDATE
    M->>M: write to binlog
    M-->>App: OK (async) или ждёт ACK (semi-sync)
    M->>R: stream binlog events
    R->>R: write to relay log
    R->>R: apply via SQL thread
    R-->>M: ACK position
```

**Конфиг master (`my.cnf`):**
```ini
[mysqld]
server-id = 1
log-bin = mysql-bin
binlog-format = ROW
binlog-row-image = MINIMAL
sync-binlog = 1
innodb-flush-log-at-trx-commit = 1
gtid-mode = ON
enforce-gtid-consistency = ON
```

**Конфиг replica:**
```ini
[mysqld]
server-id = 2
relay-log = relay-bin
read-only = 1
super-read-only = 1
gtid-mode = ON
enforce-gtid-consistency = ON
log-bin = mysql-bin      # обязательно для chain replication
log-slave-updates = ON
```

**Запуск репликации:**
```sql
CHANGE REPLICATION SOURCE TO
  SOURCE_HOST='master.example.com',
  SOURCE_USER='repl',
  SOURCE_PASSWORD='secret',
  SOURCE_AUTO_POSITION=1;  -- использовать GTID
START REPLICA;
SHOW REPLICA STATUS\G
```

**Multi-threaded apply** (MySQL 8):
```sql
SET GLOBAL replica_parallel_workers = 8;
SET GLOBAL replica_parallel_type = 'LOGICAL_CLOCK';
```

---

## Q16. Statement-based vs Row-based vs Mixed binlog

**Три формата binlog в MySQL:**

| Формат | Что пишется | Pros | Cons |
|---|---|---|---|
| **STATEMENT (SBR)** | SQL-стейтменты целиком | компактный, читаемый | unsafe для `NOW()`, `UUID()`, `LIMIT` без `ORDER BY` |
| **ROW (RBR)** | измененные строки | детерминированный, безопасный | объёмный, сложно читать |
| **MIXED** | автоматический выбор | компромисс | сложнее дебажить |

**Statement-based (SBR):**
```sql
-- В binlog:
UPDATE users SET last_login = NOW() WHERE id = 42;
```
- Replica выполнит `NOW()` со своим временем → разные значения!
- Unsafe для non-deterministic функций.

**Row-based (RBR, default с MySQL 5.7):**
```
-- В binlog (бинарный, упрощённо):
UPDATE users
  WHERE id = 42 AND last_login = '2026-05-01 10:00:00'
  SET last_login = '2026-05-21 14:32:11';
```
- Записывает конкретные значения «до» и «после».
- Детерминированно, безопасно.
- Больше объём (особенно для bulk-операций).
- Используй `binlog_row_image = MINIMAL` для уменьшения.

**Mixed:**
- Дефолтно SBR, переключается на RBR для unsafe statements.
- Сложнее для CDC-инструментов (Debezium, Maxwell).

**Рекомендация для production:** `binlog-format = ROW` всегда (безопасность > размер).

---

## Q17. Что такое GTID и зачем он нужен?

**GTID (Global Transaction Identifier)** — глобально уникальный идентификатор транзакции в MySQL.

**Формат:** `SERVER_UUID:TRANSACTION_NUMBER`
```
3E11FA47-71CA-11E1-9E33-C80AA9429562:23
```

**До GTID** (binlog file + position):
- При failover нужно вручную искать «где остановилась replica» через `SHOW SLAVE STATUS`.
- При смене master нужно пересчитывать позицию.
- Ошибки приводили к потере или дублированию транзакций.

**С GTID:**
- Replica знает, какие GTID уже применила.
- `CHANGE REPLICATION SOURCE TO ... SOURCE_AUTO_POSITION = 1` — автоматический resume.
- Failover упрощается: новый master продолжит с следующего GTID.
- Поддерживается инструментами автофейловера (`Orchestrator`, `MHA`).

**Включение:**
```ini
gtid-mode = ON
enforce-gtid-consistency = ON
```

**Проверка:**
```sql
SELECT @@gtid_executed;       -- какие GTID применены
SELECT @@gtid_purged;         -- какие GTID удалены из binlog
SHOW REPLICA STATUS\G         -- Retrieved_Gtid_Set, Executed_Gtid_Set
```

**Ограничения:**
- Некоторые операции запрещены при `enforce-gtid-consistency = ON`: `CREATE TABLE ... SELECT`, non-transactional updates внутри transactions.

---

## Q18. Semi-sync replication в MySQL — как настроить?

**Semi-sync** — primary ждёт `ACK` от ≥1 реплики перед commit.

**Установка плагина:**
```sql
-- На master:
INSTALL PLUGIN rpl_semi_sync_source SONAME 'semisync_source.so';

-- На replica:
INSTALL PLUGIN rpl_semi_sync_replica SONAME 'semisync_replica.so';
```

**Конфиг master:**
```ini
[mysqld]
rpl_semi_sync_source_enabled = 1
rpl_semi_sync_source_timeout = 10000           # 10 секунд
rpl_semi_sync_source_wait_for_replica_count = 1
rpl_semi_sync_source_wait_point = AFTER_SYNC   # после flush в binlog
```

**Конфиг replica:**
```ini
[mysqld]
rpl_semi_sync_replica_enabled = 1
```
Перезапустить IO thread на replica: `STOP REPLICA IO_THREAD; START REPLICA IO_THREAD;`

**Поведение:**
- Master ждёт `ACK` от replica до возврата клиенту `OK`.
- Если ни одна replica не ответила за `rpl_semi_sync_source_timeout` → **деградирует до async** (есть транзакция, но без подтверждения).
- При возвращении replica → возвращается в semi-sync mode.

**Мониторинг:**
```sql
SHOW STATUS LIKE 'Rpl_semi_sync_source%';
-- Rpl_semi_sync_source_status: ON
-- Rpl_semi_sync_source_clients: 2
-- Rpl_semi_sync_source_tx_avg_wait_time: 1234 (мкс)
```

**Альтернатива — Group Replication** (MySQL InnoDB Cluster): синхронный multi-primary через Paxos-like consensus.

---

## Q19. (!) Как устроена streaming replication в PostgreSQL?

**Архитектура:**
- **WAL (Write-Ahead Log)** на primary — последовательный журнал изменений страниц.
- **walsender** на primary — отправляет WAL по сети.
- **walreceiver** на replica — принимает WAL.
- **startup process** на replica — применяет WAL.

```
Primary:
  Backend → WAL buffer → fsync → walsender → network
                                            ↓
Replica:
  walreceiver → WAL on disk → startup process replays → pages
```

**Создание replica:**
```bash
# 1. На primary в pg_hba.conf:
# host replication repl_user replica.ip/32 scram-sha-256

# 2. На primary в postgresql.conf:
wal_level = replica            # или 'logical' для logical replication
max_wal_senders = 10
max_replication_slots = 10
wal_keep_size = 1GB            # сохранять WAL для отставших реплик

# 3. На replica — pg_basebackup:
pg_basebackup -h primary -U repl_user -D /var/lib/postgresql/data \
              -X stream -R -C -S replica1_slot

# pg_basebackup создаст standby.signal и пропишет primary_conninfo
```

**postgresql.conf на replica:**
```ini
hot_standby = on                  # позволяет read queries
hot_standby_feedback = on         # предотвращает recovery conflicts
max_standby_streaming_delay = 30s # сколько ждать long queries
primary_conninfo = 'host=primary user=repl_user application_name=replica1'
primary_slot_name = 'replica1_slot'
```

**Cascade replication:** replica → replica → replica (снимает нагрузку с primary).

**Проверка:**
```sql
-- На primary:
SELECT * FROM pg_stat_replication;
-- pid | state | sent_lsn | write_lsn | flush_lsn | replay_lsn

-- На replica:
SELECT pg_is_in_recovery();        -- true
SELECT pg_last_wal_replay_lsn();
```

---

## Q20. Replication slots — зачем нужны?

**Проблема без slots:** primary удаляет WAL по `wal_keep_size` или после checkpoint. Если replica отстала больше — WAL потерян, replica нужно пересоздавать.

**Replication slot** — сущность на primary, которая гарантирует, что WAL не будет удалён, пока конкретная replica его не применила.

**Типы:**

| Тип | Применение |
|---|---|
| **Physical** | для streaming replication |
| **Logical** | для logical decoding (Debezium, pglogical) |

**Создание physical slot:**
```sql
SELECT pg_create_physical_replication_slot('replica1_slot');
SELECT * FROM pg_replication_slots;
```

**На replica указать слот:**
```ini
primary_slot_name = 'replica1_slot'
```

**Создание logical slot:**
```sql
SELECT pg_create_logical_replication_slot('debezium_slot', 'pgoutput');
-- или 'wal2json', 'test_decoding'
```

**Опасность slots:** если consumer **остановится** (например, упал Debezium), slot будет держать WAL **бесконечно** → диск primary заполнится → primary упадёт.

**Митигации:**
- `max_slot_wal_keep_size` (PG 13+) — лимит на сколько WAL может удерживать слот.
- Мониторинг `pg_replication_slots.confirmed_flush_lsn` и retention size.
- Алерт на отстающие/inactive slots.

```sql
-- Размер удерживаемого WAL по слотам:
SELECT slot_name, active,
       pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), restart_lsn)) AS retained
FROM pg_replication_slots;
```

---

## Q21. Logical replication через publications/subscriptions

**Logical replication** в PostgreSQL — реплицирует **логические события** (`INSERT`/`UPDATE`/`DELETE`) выборочно по таблицам.

**Зачем:**
- Upgrade между major versions без даунтайма.
- Миграция данных между разными PG-инстансами.
- Реплика подмножества таблиц (multi-tenant).
- ETL в DWH (через Debezium → Kafka).
- Multi-master с pglogical/BDR.

**Настройка:**

На **publisher** (источник):
```ini
wal_level = logical
max_replication_slots = 10
max_wal_senders = 10
```

```sql
CREATE PUBLICATION my_pub FOR TABLE users, orders;
-- или для всех таблиц:
CREATE PUBLICATION all_pub FOR ALL TABLES;
-- с фильтром (PG 15+):
CREATE PUBLICATION ru_pub FOR TABLE users WHERE (country = 'RU');
```

На **subscriber** (получатель):
```sql
CREATE SUBSCRIPTION my_sub
  CONNECTION 'host=publisher.example.com user=repl_user dbname=app'
  PUBLICATION my_pub;
```

**Ограничения logical replication:**
- Не реплицирует `DDL` (`CREATE TABLE`, `ALTER`).
- Не реплицирует `TRUNCATE` до PG 11.
- Не реплицирует `sequences` (до PG 16 вообще, в PG 16 опционально).
- Не реплицирует large objects.
- Subscriber таблица должна существовать заранее.
- Каждая таблица должна иметь PK (или `REPLICA IDENTITY FULL`).

**Мониторинг:**
```sql
SELECT * FROM pg_stat_subscription;
SELECT * FROM pg_stat_replication;        -- на publisher
```

---

## Q22. Synchronous_commit и synchronous_standby_names

PostgreSQL предоставляет несколько уровней синхронности commit:

**`synchronous_commit` параметр:**

| Значение | Поведение | Durability |
|---|---|---|
| `off` | commit возвращается до fsync | можно потерять последние < 200ms |
| `local` | wait local WAL flush | crash-safe на этом узле |
| `remote_write` | wait WAL flushed на replica | crash-safe + replica file system buffer |
| `remote_apply` | wait WAL applied на replica | read-your-write через replica |
| `on` (default) | wait local + sync standbys | максимальная durability |

**`synchronous_standby_names`** — определяет sync replicas:

```ini
# Одна конкретная replica:
synchronous_standby_names = 'replica1'

# ANY N из списка (semi-sync style):
synchronous_standby_names = 'ANY 1 (replica1, replica2, replica3)'

# FIRST N (приоритетный порядок):
synchronous_standby_names = 'FIRST 2 (replica1, replica2, replica3)'

# Кворум:
synchronous_standby_names = 'ANY 2 (r1, r2, r3, r4)'
```

**Application name** на replica должен совпадать:
```ini
primary_conninfo = 'host=primary application_name=replica1 ...'
```

**Опасность:** если все sync replicas упали → primary **зависнет на commit** (clients таймаут).

**Митигация:**
- `ANY 1` вместо `FIRST 1` (любая из нескольких — устойчиво).
- Мониторинг — алерт когда нет sync replicas.
- Готовность временно переключить на `synchronous_commit = local` при инцидентах.

---

## Q23. Как устроен replica set в MongoDB?

**Replica set** — группа MongoDB-инстансов, поддерживающих одинаковый dataset.

**Состав:**
- **Primary** — единственный, принимает writes.
- **Secondaries** — реплицируют primary, обслуживают reads (опционально).
- **Arbiter** — голосует на выборах, но не хранит данные (для нечётного числа voting members).

**Oplog (operations log):**
- Capped collection в `local` БД (`local.oplog.rs`).
- Все операции primary → пишутся в oplog → secondaries читают и применяют.
- Размер по умолчанию ~5% от диска.

**Инициализация:**
```javascript
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "node1:27017", priority: 2 },
    { _id: 1, host: "node2:27017", priority: 1 },
    { _id: 2, host: "node3:27017", priority: 1 }
  ]
})
rs.status()
rs.conf()
```

**Primary election:**
- Через Raft-like протокол.
- При недоступности primary > `electionTimeoutMillis` (default 10s) — secondaries запускают выборы.
- Побеждает тот, у кого свежее oplog + больший `priority`.
- Нужен **majority** voting members.

**Read preferences:**
```javascript
db.users.find().readPref("secondary")
// primary | primaryPreferred | secondary | secondaryPreferred | nearest
```

**Write concerns:**
```javascript
db.users.insertOne({ name: "Alice" }, {
  writeConcern: { w: "majority", wtimeout: 5000, j: true }
})
// w: 1 | majority | N — сколько узлов подтвердило
// j: true — wait journal flush
```

**Causal consistency** (read-your-writes через session):
```javascript
const session = client.startSession({ causalConsistency: true });
```

---

## Q24. Replication factor и snitches в Cassandra

**Cassandra** — leaderless, репликация настраивается на уровне **keyspace**.

**Replication strategies:**

```cql
-- Для разработки (single DC):
CREATE KEYSPACE app WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 3
};

-- Для production (multi-DC):
CREATE KEYSPACE app WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'dc_east': 3,
  'dc_west': 3
};
```

**Snitches** — определяют топологию кластера (какой узел в каком ДЦ/rack).

| Snitch | Применение |
|---|---|
| `SimpleSnitch` | single datacenter, dev |
| `GossipingPropertyFileSnitch` | production, конфиг в `cassandra-rackdc.properties` |
| `Ec2Snitch` | AWS single region |
| `Ec2MultiRegionSnitch` | AWS multi region |
| `GoogleCloudSnitch` | GCP |
| `RackInferringSnitch` | по октетам IP |

**Конфиг `cassandra-rackdc.properties`:**
```properties
dc=dc_east
rack=rack1
```

**Replica placement:** Cassandra размещает реплики **в разные racks** в пределах ДЦ — это обеспечивает доступность при отказе целого rack.

**Consistency levels:**
- `LOCAL_QUORUM` — quorum только в локальном ДЦ (быстрее, без cross-DC latency).
- `EACH_QUORUM` — quorum в каждом ДЦ (надёжнее, медленнее).
- `LOCAL_ONE` — один узел в локальном ДЦ (eventual consistency).

**Rule of thumb:** для multi-DC использовать `LOCAL_QUORUM` для большинства запросов.

---

## Q25. (!) Как работает автоматический failover?

**Автоматический failover** — переключение primary при его отказе без вмешательства человека.

**Шаги:**
1. **Detection** — обнаружить, что primary недоступен (health checks, timeouts).
2. **Election** — выбрать новый primary из реплик.
3. **Promotion** — promote replica → новый primary.
4. **Reconfiguration** — переключить клиентов и оставшиеся replicas на нового primary.
5. **Fencing** — изолировать старый primary (если он жив, но недоступен сети).

**Инструменты по системам:**

| СУБД | Инструмент |
|---|---|
| **PostgreSQL** | `Patroni` + `etcd`/`Consul`/`ZooKeeper` |
| **MySQL** | `Orchestrator`, `MHA`, `MySQL Router` + Group Replication |
| **MongoDB** | встроенный election в replica set |
| **Cassandra** | не нужен — leaderless |
| **AWS RDS** | Multi-AZ, автоматический failover |

**Patroni (PostgreSQL HA):**
- Хранит state кластера в `etcd`/`Consul`.
- Каждый узел держит «leader lock» с TTL.
- Если primary не обновил lock → начинаются выборы.
- Promote через `pg_promote()` или `pg_ctl promote`.
- Запускает `pg_rewind` на старом primary при возврате.

**Конфиг Patroni (выдержка):**
```yaml
scope: postgres-cluster
namespace: /service/
name: pg-01

restapi:
  listen: 0.0.0.0:8008

etcd:
  hosts: etcd1:2379,etcd2:2379,etcd3:2379

bootstrap:
  dcs:
    ttl: 30
    loop_wait: 10
    retry_timeout: 10
    maximum_lag_on_failover: 1048576  # 1 MB
    synchronous_mode: true
```

**Caveats:**
- Слишком чувствительный health check → ложные failovers.
- Слишком терпимый → долгий downtime.
- Без fencing — split-brain.

---

## Q26. (!) Что такое split-brain и как его избегать?

**Split-brain** — ситуация, когда два узла одновременно считают себя primary и принимают writes.

**Как возникает:**
1. Сеть между primary и replicas «расщепилась» (network partition).
2. Replicas не видят primary → выбирают новый.
3. Старый primary продолжает принимать writes от клиентов своего сегмента.
4. Получаем два расходящихся стейта → потеря данных, конфликты, ад при merge.

**Способы избегать:**

**1. Quorum (majority voting).**
- Failover происходит только если **большинство** узлов согласны.
- Кластер из 3 узлов: нужно 2 для решения.
- При partition узел в меньшинстве сам себя «отключает» (step down).

**2. Fencing / STONITH (Shoot The Other Node In The Head).**
- Перед promote — физически или сетево отключить старый primary.
- В AWS: revoke security group, detach EBS.
- На bare-metal: IPMI/iLO для power off.
- Patroni: `watchdog` (linux kernel watchdog убивает узел при потере lock).

**3. External consensus store.**
- `etcd`/`ZooKeeper`/`Consul` хранят «кто primary».
- Они сами используют Raft/Paxos для consistency.
- Узел перед write проверяет: «я ещё leader?».

**4. Нечётное число узлов.**
- 3 или 5 узлов вместо 2 или 4.
- При partition (2-1) большинство имеет 2 узла.
- При (1-1-1) нет primary — но это лучше, чем split-brain.

**5. Read-only при потере quorum.**
- Если узел не видит большинство → переходит в read-only.
- В MongoDB: secondary остаётся secondary, primary step down.

**Last resort — application-level fix:**
- Если split-brain произошёл — нужно merge'ить руками или выбрать «winner DC» и отбросить второй (boring downtime).

---

## Q27. Read replicas в облаке: RDS, Aurora, Cloud SQL

**AWS RDS Read Replicas:**
- Async streaming replication.
- До 15 read replicas (для PostgreSQL/MySQL).
- Можно cross-region.
- Multi-AZ — это **standby** (не используется для reads), failover автоматический.
- Read replica можно promote в standalone (для blue-green deploy).

**AWS Aurora:**
- Shared storage между primary и replicas (не WAL streaming).
- Latency репликации **< 10 ms** (storage-level).
- До 15 Aurora Replicas + 1 writer.
- Aurora Global Database — cross-region <1s lag.
- Aurora Serverless v2 — autoscaling read replicas.

**Google Cloud SQL Read Replicas:**
- Похоже на RDS: async streaming.
- Cross-region replica.
- Cascading replication поддерживается.

**Azure Database for PostgreSQL:**
- Hyperscale (Citus) — multi-node + read replicas.
- Single Server: до 5 read replicas.

**Anti-patterns в облаке:**
- Использовать `db.t3.micro` для read replica при write-heavy primary → постоянный lag.
- Запускать heavy analytics (`SELECT *`) без `hot_standby_feedback` → recovery conflicts.
- Промоутить read replica и забыть обновить connection strings → split-brain или потеря writes.

**Failover при Multi-AZ:**
- RDS делает DNS-флип на standby (60-120 секунд).
- Aurora — порядка 30 секунд (новый writer из replicas).
- Клиенты должны переподключиться и retry.

---

## Q28. (!) Что такое Change Data Capture (CDC)?

**CDC** — паттерн извлечения изменений из БД и публикации их как event stream.

**Зачем:**
- Real-time синхронизация с search index (Elasticsearch).
- Заполнение DWH/data lake (Snowflake, BigQuery).
- Event-driven architecture (микросервисы реагируют на изменения).
- Cache invalidation.
- Audit log.
- Outbox pattern.

**Подходы:**

| Подход | Как работает | Pros | Cons |
|---|---|---|---|
| **Query-based** | поллим `updated_at > last_check` | просто | пропускает deletes, нагружает БД |
| **Trigger-based** | `AFTER INSERT/UPDATE` → пишем в audit-таблицу | работает везде | overhead на каждом write |
| **Log-based** | читаем binlog/WAL | низкий overhead, real-time, ничего не теряет | сложнее настроить |

**Log-based — стандарт де-факто.** Использует тот же механизм, что и replication.

**Инструменты:**
- **Debezium** — open-source CDC платформа на Kafka Connect.
- **Maxwell** — MySQL → Kafka.
- **Airbyte** — ELT, поддерживает CDC.
- **AWS DMS** — managed CDC.
- **Striim**, **HVR** — commercial.

**Outbox pattern** для надёжной публикации событий:
```sql
BEGIN;
  INSERT INTO orders (...) VALUES (...);
  INSERT INTO outbox (event_type, payload) VALUES ('OrderCreated', '{...}');
COMMIT;
-- Debezium ловит изменения в outbox → публикует в Kafka
```

---

## Q29. Репликация через Kafka и Debezium

**Debezium** — CDC на основе Kafka Connect.

**Архитектура:**
```
PostgreSQL/MySQL/MongoDB ←─ Debezium Connector ─→ Kafka topics
                                                       ↓
                                          Consumers: Elasticsearch, ClickHouse, services
```

**Как работает:**
1. Connector подключается к replication log БД.
2. Для PostgreSQL — создаёт logical replication slot + publication.
3. Для MySQL — читает binlog с GTID.
4. Для MongoDB — читает oplog или change streams.
5. Каждое изменение → message в Kafka topic (`db.schema.table`).
6. Snapshot при первом запуске + incremental updates.

**Конфиг Debezium для PostgreSQL:**
```json
{
  "name": "postgres-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres.example.com",
    "database.port": "5432",
    "database.user": "debezium",
    "database.password": "***",
    "database.dbname": "app",
    "topic.prefix": "app",
    "plugin.name": "pgoutput",
    "publication.name": "debezium_pub",
    "slot.name": "debezium_slot",
    "snapshot.mode": "initial",
    "table.include.list": "public.users,public.orders"
  }
}
```

**Структура события (Debezium):**
```json
{
  "before": { "id": 42, "name": "Alice", "email": "a@x.com" },
  "after":  { "id": 42, "name": "Alice", "email": "alice@x.com" },
  "source": { "version": "2.4.0", "ts_ms": 1716285600000, "lsn": "0/30000A0" },
  "op": "u",
  "ts_ms": 1716285600100
}
```

**Pitfalls:**
- Logical slot держит WAL — если Debezium упал на сутки, диск primary заполнится.
- Schema changes (`ALTER TABLE`) — нужна Schema Registry или специальная обработка.
- Reordering между partitions — внутри partition порядок гарантирован, между — нет.

---

## Q30. Cross-region репликация: trade-off и подводные камни

**Cross-region replication** — реплики в географически удалённых ДЦ.

**Зачем:**
- Disaster recovery (целый регион упал).
- Низкая латентность для глобальных пользователей.
- Регуляторные требования (data residency).

**Trade-off:**

| Аспект | Single-region | Cross-region |
|---|---|---|
| **Latency replication** | < 10 ms | 50-300 ms |
| **Sync replication** | возможна | практически нет |
| **Cost** | трафик внутри AZ дёшев | inter-region трафик дорог ($) |
| **RPO** | < 1 second | секунды-минуты |
| **Read latency для global users** | плохая | хорошая (local replica) |
| **Consistency** | strong возможна | обычно eventual |

**Подводные камни:**

**1. Latency = wagging tail.**
- Sync cross-region = commit может ждать 300 ms.
- Async cross-region = data loss при региональной катастрофе.

**2. Cost.**
- AWS: data transfer между регионами ~ $0.02/GB.
- 1 TB/day = $600/month только за трафик.

**3. Regulatory.**
- GDPR: персональные данные граждан ЕС не должны покидать ЕС.
- Россия: 152-ФЗ требует обработки на территории РФ.

**4. Schema/version drift.**
- Сложно одновременно делать `ALTER TABLE` во всех регионах.

**5. Conflict resolution в multi-region multi-master.**
- DynamoDB Global Tables — LWW (последний коннектор по timestamp).
- Spanner / CockroachDB — TrueTime / HLC для consistency.
- Aurora Global Database — read-only replicas в других регионах (single writer).

**Паттерны:**
- **Active-passive cross-region** — primary в одном регионе, DR в другом.
- **Active-active с partitioning** — пользователи EU → EU primary, US → US primary.
- **Global tables (DynamoDB)** — multi-region multi-master с LWW.

---

## Q31. Anti-patterns репликации

**1. Writes на replica «just because».**
- Replica работает в read-only режиме → write fails.
- В leaderless системах это работает, но в primary-replica — нет.

**2. Использование replica для critical reads без проверки lag.**
- «Сохранил → прочитал → не вижу» — классика.
- Fix: read after write идёт на primary, или ждём lag = 0, или causal consistency.

**3. Игнорирование lag в мониторинге.**
- Replica отстаёт на час → аналитика показывает старые цифры → принимаются неправильные решения.

**4. Забытая read-replica после failover.**
- Promote replica1 → primary, а replica2 продолжает реплицироваться со **старого** primary (если ему сделали restart).
- Нужно `CHANGE REPLICATION SOURCE` всех replicas на новый primary.

**5. Replication = backup.**
- `DROP TABLE` на primary улетит на все replicas за миллисекунды.
- Делайте бэкапы отдельно.

**6. Один sync replica = SPOF.**
- Если есть только один sync standby и он умер → primary зависнет на commit.
- Используйте `ANY 1 (replica1, replica2, replica3)`.

**7. Включить `wal_level = logical` без logical consumers.**
- Лишний оверхед на write throughput без пользы.

**8. Не мониторить replication slots.**
- Inactive slot → удерживает WAL → диск primary заполняется → primary падает.

**9. Failover без fencing.**
- Старый primary поднимается, принимает writes → split-brain.

**10. Race с clocks при LWW.**
- NTP может ошибиться на сотни мс.
- В Cassandra: использовать монотонный `now()` или client-side TimeUUID.

**11. Тяжёлая аналитика на single replica.**
- Долгий `SELECT *` на replica с `hot_standby_feedback = on` → primary не может вакуумить → bloat.

**12. «Я просто промоучу replica и поправлю потом».**
- Промотированная replica имеет свой timeline.
- Без `pg_rewind` старого primary к новой — невозможно вернуть его как replica.

**13. Игнорировать `gc_grace_seconds` в Cassandra.**
- Не делать `nodetool repair` каждые 10 дней → zombie data (удалённые записи возвращаются).

**14. CDC без outbox для критичных событий.**
- Прямой CDC может пропустить события при сбое consumer.
- Outbox pattern — гарантия at-least-once.

---

## See also

- [PostgreSQL](postgresql-interview.md) — встроенные механизмы репликации PostgreSQL: WAL, streaming, replication slots
- [MongoDB](mongodb-interview.md) — replica set, election, oplog, read preferences и write concerns
- [Cassandra](cassandra-interview.md) — leaderless, replication factor, consistency levels, snitches, anti-entropy repair
- [Архитектура БД](database-architecture-interview.md) — где репликация вписывается в общую архитектуру БД и распределённых систем
- [Транзакции БД](database-transactions-interview.md) — связка с репликацией: visibility и durability при sync/async commit
- [Распределённые системы](../architecture/distributed-systems-interview.md) — consensus, failure detection, причины split-brain
- [CAP-теорема](../architecture/cap-theorem-interview.md) — теоретическая база trade-off между consistency и availability при репликации
- [Паттерны согласованности](../architecture/consistency-patterns-interview.md) — read-your-writes, monotonic reads, causal consistency
- [Kafka](../messaging/kafka-interview.md) — Debezium → Kafka как стандартный паттерн CDC
- [System Design](../system-design/system-design-interview.md) — как выбирать модель репликации под нагрузку в system design интервью
