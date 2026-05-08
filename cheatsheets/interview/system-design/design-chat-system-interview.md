---
title: "Вопросы на собеседовании: Design Chat System"
description: "System design chat (WhatsApp, Slack): WebSockets, message delivery, presence, group chats, encryption, storage, push notifications, read receipts, typing indicators"
tags:
  - interview
  - system-design
  - design-chat-system-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Design Chat System"
  - "Chat System design"
  - "WhatsApp design"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Design Chat System`

`Chat System` (WhatsApp, Slack, Telegram, Messenger) — популярный system design. **Stateful connections** (WebSockets), ordering, delivery guarantees, scale. Обсуждается presence, groups, encryption, storage, push notifications.

## Полезные ссылки

- [Building WhatsApp (Erlang)](https://blog.whatsapp.com/)
- [How Slack built real-time messaging](https://slack.engineering/)
- [High Scalability — WhatsApp architecture](http://highscalability.com/blog/2014/2/26/the-whatsapp-architecture-facebook-bought-for-19-billion.html)
- [Signal protocol (E2E encryption)](https://signal.org/docs/)
- [System Design Primer](https://github.com/donnemartin/system-design-primer)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Requirements**
- [Q1. (!) Functional и non-functional requirements?](#q1--functional-и-non-functional-requirements)
- [Q2. (!) Capacity estimation?](#q2--capacity-estimation)

**Real-time transport**
- [Q3. (!) WebSockets vs long polling vs SSE?](#q3--websockets-vs-long-polling-vs-sse)
- [Q4. (!) Connection routing и load balancing?](#q4--connection-routing-и-load-balancing)
- [Q5. Sticky session проблема?](#q5-sticky-session-проблема)

**Architecture**
- [Q6. (!) High-level architecture?](#q6--high-level-architecture)
- [Q7. (!) Message delivery flow?](#q7--message-delivery-flow)
- [Q8. Message broker между серверами?](#q8-message-broker-между-серверами)

**Storage**
- [Q9. (!) Schema для messages?](#q9--schema-для-messages)
- [Q10. (!) SQL vs NoSQL для chat?](#q10--sql-vs-nosql-для-chat)
- [Q11. Shard strategy?](#q11-shard-strategy)

**Delivery semantics**
- [Q12. (!) At-most-once, at-least-once, exactly-once?](#q12--at-most-once-at-least-once-exactly-once)
- [Q13. (!) Read receipts и delivery receipts?](#q13--read-receipts-и-delivery-receipts)
- [Q14. Ordering guarantees?](#q14-ordering-guarantees)

**Features**
- [Q15. (!) Online status / presence?](#q15--online-status--presence)
- [Q16. Typing indicators?](#q16-typing-indicators)
- [Q17. Group chat design?](#q17-group-chat-design)
- [Q18. Push notifications для offline?](#q18-push-notifications-для-offline)

**Advanced**
- [Q19. (!) End-to-end encryption?](#q19--end-to-end-encryption)
- [Q20. Media (images, video) handling?](#q20-media-images-video-handling)
- [Q21. Search in chat history?](#q21-search-in-chat-history)

## Q1. (!) Functional и non-functional requirements?

**Functional:**
- 1:1 messaging
- Group chat (10-100 users typical, up to 500)
- Online/offline presence
- Message delivery (real-time если online, push если offline)
- Read receipts
- Message history
- Typing indicators
- Media sharing (photos, files)

**Non-functional:**
- **Low latency** (< 500ms message delivery)
- **High reliability** (no messages lost)
- **Availability** (99.99%)
- **Scalability** (billions users — WhatsApp 2B+)
- **Ordering** (messages in correct sequence)
- **Encryption** (E2E для privacy)


> [!mcq]
> - [ ] Ordering можно игнорировать — клиент сам разберётся | ❌ ПОСЛЕДСТВИЕ: без sequence ID или timestamp гарантии сообщения приходят в случайном порядке; в группах из 50+ участников это критично
> - [ ] High reliability = synchronous replication с ACK от всех реплик | ❌ ПОСЛЕДСТВИЕ: synchronous replication убивает latency (< 500ms требование); достаточно quorum write + async fan-out
> - [x] Ключевые NFR чата: latency < 500ms, reliability (no msg loss), ordering (seq_id), E2E encryption, 99.99% availability | ✓ ПРИМЕНЯТЬ: называть эти 5 NFR в начале system design interview 📋 ПРАВИЛО: chat NFR = latency + reliability + ordering + encryption + availability 🔗 См. Q2
> - [ ] Availability 99.99% требует 5 дата-центров в разных регионах | ❌ ПОСЛЕДСТВИЕ: 99.99% = 52 мин downtime/год; достигается через active-active 2-3 DC + graceful degradation, не 5 DC

## Q2. (!) Capacity estimation?

**Assumptions:**
- 1B active users (WhatsApp-scale)
- Avg 50 messages/user/day
- 500M simultaneously online (peak)

**Messages:**
- 1B × 50 = **50B messages/day**
- 50B / 86400 = **580K msg/sec average**
- Peak ~2M msg/sec

**Storage:**
- 100B per message (text + metadata)
- 50B × 100B = **5 TB/day**
- 5 years → 9 PB
- With media: 10-100x more

**Bandwidth:**
- Avg: 50K/s × 1KB = 50 MB/s
- Peak: 200 MB/s — modest

**Connection count:**
- 500M concurrent WebSocket connections
- ~1M connections per server (tuned) → 500 servers for connection tier
- More conservative: 50K/server → 10K servers

**Memory:**
- Presence: user_id → server_id map → 1B × 16B = 16GB
- Redis fits


> [!mcq]
> - [ ] 1B users × 50 msg/day = 5TB/day storage — не нужно учитывать медиа отдельно | ❌ ПОСЛЕДСТВИЕ: медиа увеличивает хранение в 10-100x; 5TB/day для текста = 50-500TB/day с фото; S3 для media отдельно от message DB
> - [ ] 500M concurrent connections = 500 серверов по 1M conn каждый — реалистично | ❌ ПОСЛЕДСТВИЕ: 1M conn/server требует kernel tuning (ulimit, epoll, SO_REUSEPORT); realistically 50-100K/server → 5K-10K servers в connection tier
> - [ ] Presence map 1B users в Redis = 16GB — не поместится | ❌ ПОСЛЕДСТВИЕ: Redis хранит 16B/entry × 1B = 16GB; Redis легко держит 100GB+ с кластеризацией; это реалистичная оценка
> - [x] Peak: 2M msg/sec, storage: 5TB/day текст + 500TB/day медиа, connection tier: 500M WebSocket = 5-10K серверов при 50-100K conn/srv | ✓ ПРИМЕНЯТЬ: capacity estimation для 1B-scale chat 📋 ПРАВИЛО: msg/day = users × 50; storage = msg × 100B; conn tier = online × (1/concurrency_per_server) 🔗 См. Q3

## Q3. (!) WebSockets vs long polling vs SSE?

**WebSockets:** bidirectional persistent.
- **Best** для chat (messages in+out)
- Single TCP connection
- Low overhead

**Long polling:** client requests, server holds until event.
- Fallback для environments где WS blocked
- Higher overhead (HTTP overhead per event)

**SSE (Server-Sent Events):** server → client one-way.
- Not bidirectional (need separate POST для sending)
- Works с HTTP/2 for scale

**Push (mobile):** APNs (iOS), FCM (Android) для offline notifications.
- Device wake, show notification
- App fetches actual messages when opened

**Hybrid (real-world):**
- Primary: WebSocket when app active
- Fallback: polling where WS fails
- Offline: push notification → next open syncs

**Disconnect handling:**
- Heartbeat (ping) every 30s
- Detect dead connection → reconnect
- Exponential backoff


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q4. (!) Connection routing и load balancing? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

**Challenge:** WebSockets are **stateful** — user's connection lives on one server. Messages для user X must route to that server.

**Architecture:**

```
Client → LB → WebSocket server (holds connection)
                    ↓
              Message broker (Kafka/Redis pub-sub)
                    ↑
Other user's WebSocket server publishes message for X
```

**Routing:**
- User X connects → assigned to WS server S1
- Presence service: user_id:X → server:S1 (Redis)
- When Y sends to X:
  - Message arrives at Y's WS server
  - Publish to channel or lookup presence
  - X's server pushes to X's WebSocket

**Load balancing:**
- Consistent hashing (user_id → server) — sticky but scalable
- Or round-robin + central routing via broker

**Connection limits:**
- 500K-1M concurrent per server (with tuning)
- Horizontal scale

**Failover:**
- Server dies → WebSockets drop
- Clients reconnect → routed to new server
- Update presence registry


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Sticky session проблема? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Problem:** user reconnects → must go to same server? Or any?

**Option A: sticky (consistent hash):**
- User → server by `hash(user_id) % N`
- Same server across reconnects
- Pro: predictable
- Con: rebalance when scale (user's server changes)

**Option B: any server (broker-routed):**
- User → any server (round-robin)
- Messages for user routed via broker (pub-sub on user's channel)
- Pro: flexible scaling
- Con: extra hop per message

**Real-world:** Option B more common (Slack, Discord) — more flexible.

**Presence update on reconnect:**
```
On connect: SET user:X server:S2
On disconnect: DEL user:X
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. (!) High-level architecture? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```
Clients (mobile/web)
    ↓ WebSocket
[Load Balancer]
    ↓
[WebSocket Service Tier] ← (millions of persistent connections)
    ↓ ↑
[Message Broker (Kafka/Redis pub/sub)]
    ↓ ↑
[Service Tier] — Chat API, Presence, User, Group services
    ↓
[Storage Tier] — Cassandra (messages), Postgres (users), Redis (presence, cache)
    ↓
[Media Store] — S3 для photos/videos
    ↓
[Push Service] — APNs, FCM integration
```

**Components:**

- **WS Gateway:** terminates WebSocket; handles connection lifecycle
- **Chat Service:** message persist, lookup
- **Presence Service:** online status
- **User Service:** profile, auth
- **Group Service:** group management
- **Notification Service:** push sender
- **Search Service:** Elasticsearch for history search


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. (!) Message delivery flow? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**1:1 chat, both online:**

```
User A writes → A's WebSocket
    ↓
WS server → Chat service → Persist (Cassandra)
    ↓
WS server → lookup Presence(B) → B on server S3
    ↓
Publish to channel user:B (Kafka/Redis)
    ↓
S3 subscribed → pushes via WebSocket to B
    ↓
B ACKs → read receipt back to A
```

**Latency:** typically < 200ms end-to-end.

**Offline delivery:**
- Presence: B offline → don't route via WS
- Write to B's message queue (DB / Redis)
- Trigger push notification (FCM/APNs)
- B opens app → WS connect → fetch undelivered messages

**Resilience:**
- ACK at each hop
- Message broker retries on failure
- Persist before push — if push fails, still in storage


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Message broker между серверами? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Why broker:**
- Decouples sender server from recipient server
- Scalability (publish, don't lookup)
- Retry on transient failure

**Options:**

**Redis pub/sub:**
- Super fast (microseconds)
- Fire-and-forget (no persistence)
- Good for presence + signaling
- Not guaranteed delivery (если subscriber momentarily disconnects)

**Kafka:**
- Durable (replay possible)
- Higher latency (10-100ms)
- Better for async pipelines (analytics, notifications)

**NATS:**
- In-between (fast + at-least-once)

**Hybrid:**
- Redis pub/sub для real-time signaling
- Kafka для persistence + analytics

**Pattern:**
```
Each user has a channel: user:{id}
S1 publishes: PUBLISH user:X "msg"
S3 (where X connected) SUBSCRIBE user:X → push to X's WS
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. (!) Schema для messages? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Simple (Cassandra):**

```sql
CREATE TABLE messages (
    conversation_id UUID,
    message_id TIMEUUID,   -- ordered by time
    sender_id UUID,
    content TEXT,
    sent_at TIMESTAMP,
    PRIMARY KEY (conversation_id, message_id)
) WITH CLUSTERING ORDER BY (message_id DESC);
```

- Partition: conversation — all messages на одном node
- Cluster: time → efficient range queries (recent messages)

**Conversations:**
```sql
CREATE TABLE conversations (
    conversation_id UUID,
    participant_ids SET<UUID>,
    type TEXT,  -- 'direct' or 'group'
    created_at TIMESTAMP,
    last_message_id TIMEUUID
);
```

**User → conversations index:**
```sql
CREATE TABLE user_conversations (
    user_id UUID,
    last_message_at TIMESTAMP,
    conversation_id UUID,
    PRIMARY KEY (user_id, last_message_at, conversation_id)
) WITH CLUSTERING ORDER BY (last_message_at DESC);
```

Allows "list conversations by recent activity."

**Messages не индексируются по content в operational DB** — separate Elasticsearch.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. (!) SQL vs NoSQL для chat? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Chat messages:**
- **Write-heavy** (every msg)
- **Time-series** access (recent msgs)
- **Partition by conversation** ideal

→ **NoSQL (Cassandra, DynamoDB) fits:**
- Horizontal scale
- High write throughput
- Low latency at scale

**User data (profile, auth):**
- Relational (users, friends)
- Consistency matters
- **SQL** OK (Postgres)

**Presence:**
- Ephemeral, small
- **Redis** (in-memory)

**Group memberships:**
- Small relational data
- SQL or document

**Real world:**
- WhatsApp: custom (Erlang + Mnesia)
- Messenger: HBase
- Slack: MySQL (sharded)


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Shard strategy? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Messages sharded by conversation_id:**
- All messages 1 conversation → 1 partition
- Reads: one node
- Writes: same
- Hot conversation = hot shard (rare for normal use; celeb chat может be)

**Users/groups sharded by user_id.**

**Scaling:**
- Cassandra token ring handles
- Virtual nodes для evenness

**Cross-shard queries (e.g., search across all user's chats):**
- Fan-out: query все relevant shards
- Or pre-compute timeline

**Tombstones in Cassandra:**
- Deleted messages → tombstones
- TTL-based retention (keep 90 days, auto-expire)


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. (!) At-most-once, at-least-once, exactly-once? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**At-most-once:**
- Send, don't retry на failure
- Message могут lost
- Unacceptable для chat

**At-least-once:**
- Send, retry until ACK
- **May duplicate** (ACK lost → retry)
- Typical для chat

**Exactly-once:**
- Idempotency + dedup
- Harder, but achievable

**Chat implementation:**
- **At-least-once** delivery
- **Idempotent processing** (dedup on message_id):
  - Client generates UUID per message
  - Server: `INSERT IF NOT EXISTS` (idempotency key)
  - Duplicate retry → no-op

**Result:** effective exactly-once.

**End-to-end ACK:**
- Client → server: msg with ID
- Server persists, ACK back
- Server delivers to recipient
- Recipient ACK → sender "delivered"


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. (!) Read receipts и delivery receipts? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Events:**
- **Sent:** message arrived at server (single check ✓)
- **Delivered:** recipient device received (double check ✓✓)
- **Read:** recipient opened chat (blue ticks ✓✓)

**Implementation:**
- Status field per message + recipient
- Updates flow back to sender

**For groups:**
- Status per recipient
- Sender UI shows: read by N of M

**Privacy:**
- Users can disable read receipts (bilateral)

**Storage:**
```
message_status: (message_id, user_id, status, timestamp)
```

**Update propagation:**
- Async (doesn't block main delivery)
- Batch updates (read 10 messages at once)

**Scale:**
- Multiply messages × participants = N-fold status records
- Groups с 100 members → 100x status per message


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Ordering guarantees? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Per-conversation ordering:**
- Messages в один chat must be ordered consistently для all participants
- Use timestamps + tiebreaker (message_id UUID)
- Cassandra TIMEUUID: sortable + unique

**Clock skew:** different devices различные clocks.
- Server timestamp authoritative (when received)
- Client shows local estimated

**Out-of-order arrivals:**
- Client receives message M2 before M1 (network)
- Reorder on client by timestamp

**Global ordering:** unnecessary; per-conversation enough.

**Group chat:**
- Shared sequence (conversation_id partition)
- Everyone sees same order


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. (!) Online status / presence? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Data:**
- user_id → status (online/away/offline) + last_seen

**Storage:**
- Redis (ephemeral)
- `SET user:X "online" EX 60`
- TTL 60s — heartbeat keeps alive
- TTL expires → considered offline

**Heartbeat:**
- Client sends ping every 30s via WebSocket
- Server renews Redis TTL

**Change notifications:**
- User X online/offline → pub/sub event
- Friends subscribe → UI updates

**Scale:**
- 500M users × few bytes = few GB Redis
- Sharded по user_id

**Privacy:**
- Users can hide online status
- WhatsApp: last seen visible/hidden setting

**Away:**
- No activity 5 min → "away"
- Client reports activity (mouse move, typing)


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Typing indicators? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Event:**
- User typing → ephemeral event to conversation members
- Not persistent (discard after few seconds)

**Implementation:**
- WebSocket `typing` event
- Broadcast to chat participants (via broker)
- Client shows "John is typing..."
- Timeout: if no follow-up in 5s → hide

**Rate limit:**
- Don't send event per keystroke
- Throttle: one event per 3s max

**Broker:**
- Fire-and-forget (Redis pub/sub)
- No persistence; OK to drop


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. Group chat design? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Challenges beyond 1:1:**
- 100 recipients → 100x fan-out
- Member list management
- Ordering shared

**Fan-out approaches:**

**1. Write-time fan-out:**
- Sender's server publishes to all group members' channels
- Each member's server pushes via WS
- Works well < 1000 members

**2. Read-time fan-out (pull):**
- Write message once (group-level storage)
- Recipients pull on poll / open
- Works for large groups (10k+)

**3. Hybrid:**
- Small groups: push
- Large groups: pull (or broadcast channel)

**WhatsApp:**
- Small groups (256): server fans out
- Larger ("broadcast lists"): different model

**Slack channels:**
- Can have thousands of members
- Channel = topic in broker; subscribers fan-out at edge

**Membership:**
- Group service manages add/remove
- Cache group members list


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. Push notifications для offline? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Offline detected:** presence shows offline → send push.

**Flow:**
1. Message persisted
2. Presence lookup: offline
3. Notification service → APNs (iOS) / FCM (Android)
4. Provider delivers to device
5. Device wakes, shows notification
6. User opens app → WebSocket connects → fetch pending

**APNs / FCM:**
- App must register token с push service
- Token stored per device
- Server sends POST to APNs/FCM with token + payload

**Deduplication:**
- If user reconnects before push arrives → still get push + WS message
- Client dedups by message_id

**Content:**
- Sender name + preview
- Privacy: iOS lock screen preview toggleable

**Silent push:**
- iOS background-only notification
- Wakes app briefly to fetch; no banner

**Rate limits:**
- APNs/FCM throttle per-app
- Consolidation: one push "5 new messages" vs 5 pushes


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. (!) End-to-end encryption? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**E2E:** only sender + recipient can read; server can't.

**Signal Protocol** (WhatsApp, Signal, Messenger optional):

**Key concepts:**
- Each user: long-term identity key + ephemeral keys
- Message encrypted with **per-session** key
- **Forward secrecy:** key rotates each message; past messages safe if current key compromised
- **Deniability:** can't prove who sent (no signature by long-term key)

**Flow:**
1. User A registers: uploads public keys (identity + pre-keys) к server
2. A wants to message B: fetches B's public keys
3. A does X3DH key agreement → session keys
4. Messages encrypted AES-GCM + authenticated
5. Server sees only ciphertext

**Group E2E:**
- "Sender Keys" protocol: symmetric key per sender, encrypted pairwise to members
- Rekey on membership change

**Trade-offs:**
- **Server can't:** search content, compute stats, backup messages directly
- **Client-side backup:** encrypted blob, user holds key
- **Metadata still visible:** who's messaging whom, when, size

**WhatsApp:** E2E since 2016.
**Telegram:** only "secret chats" E2E; regular chats server-side encrypted but readable.
**Signal:** all E2E.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. Media (images, video) handling? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Not sent inline** (too big):

**Upload flow:**
1. Client requests presigned S3 URL from server
2. Client uploads directly to S3
3. Client sends message: `{type: "image", url: "s3://..."}`
4. Recipient downloads directly from S3 (or CDN in front)

**Benefits:**
- No proxy through chat servers (they'd saturate bandwidth)
- CDN cache
- Parallel uploads/downloads

**Thumbnails:**
- Generated server-side (Lambda, ffmpeg)
- Multiple sizes (thumb, medium, full)

**E2E encryption challenges:**
- File encrypted client-side before upload
- Recipient downloads encrypted blob + decrypts locally
- CDN caches ciphertext (no useful inspection)

**Video streaming:**
- Not chat messages typical; offload к separate service

**Retention:**
- S3 lifecycle policies
- Delete после N years


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. Search in chat history? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Challenge:** E2E encryption → server can't search.

**Options:**

**1. Client-side search:**
- Download messages, index locally (SQLite FTS)
- Works for small-medium history
- Slow для very long histories

**2. Server-side (non-E2E systems):**
- Elasticsearch индекс
- Per-user shards
- Real-time updates from message stream

**3. E2E + encrypted search:**
- Encrypted search indexes (client generates encrypted tokens)
- Server performs encrypted search operations
- Complex cryptography (Signal hasn't implemented fully)

**Slack / Messenger (not E2E by default):**
- Full-text search via Elasticsearch
- User's channels shards

**WhatsApp:**
- Client-side only (E2E)
- "Chat backup" feature: encrypted blob в iCloud/Drive; user can search after download

---

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [System Design](system-design-interview.md) — general principles ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Design Feed System](design-feed-system-interview.md) — similar fan-out patterns
- [[websockets-interview|WebSockets]] — transport layer
- [[messaging-interview|Messaging]] — Kafka, brokers
- [Cassandra](../databases/cassandra-interview.md) — message storage
- [Redis](../databases/redis-interview.md) — presence, pub/sub
- [Distributed Systems](../architecture/distributed-systems-interview.md) — ordering, consistency
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — horizontal scaling
- [Load Balancing](../architecture/load-balancing-interview.md) — sticky sessions
- [Caching](../architecture/caching-strategies-interview.md) — presence, contact info
