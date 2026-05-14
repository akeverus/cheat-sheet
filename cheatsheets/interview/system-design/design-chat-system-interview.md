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
updated: "2026-05-15"
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
> - [ ] SSE лучше WebSocket для чата — меньше overhead | ❌ ПОСЛЕДСТВИЕ: SSE = server→client only (unidirectional); для чата нужен bidirectional поток; client messages шли бы через отдельный REST → двойной overhead
> - [x] WebSocket = bidirectional persistent; best для chat; SSE = server-push only (notifications); long polling = fallback для legacy browsers; чат = WebSocket | ✓ ПРИМЕНЯТЬ: real-time chat → WebSocket; live dashboard → SSE; старые браузеры → long polling 📋 ПРАВИЛО: WebSocket = duplex = chat; SSE = simplex = notifications 🔗 См. Q4
> - [ ] Long polling надёжнее WebSocket — проще реализовать в proxy | ❌ ПОСЛЕДСТВИЕ: long polling: каждый request = новый TCP/HTTP; при 500M users = 500M × 2M req/s = невозможная нагрузка; WebSocket держит 1 connection
> - [ ] WebSocket не поддерживается в мобильных браузерах | ❌ ПОСЛЕДСТВИЕ: WebSocket поддерживается во всех современных браузерах и мобильных приложениях с 2012 года; WhatsApp/Telegram используют WebSocket-подобные протоколы

## Q4. (!) Connection routing и load balancing?

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
> - [ ] WebSocket LB работает так же как HTTP LB — статичная round-robin | ❌ ПОСЛЕДСТВИЕ: WebSocket stateful: после handshake connection закреплена за сервером; round-robin LB разрушит соединение при каждом reconnect; нужен sticky или presence-based routing
> - [ ] Presence service не нужен если есть consistent hashing | ❌ ПОСЛЕДСТВИЕ: consistent hashing маппит user→server статично; при failover (сервер упал) нужно перегнать mapping в presence service; без него router не знает где user после failover
> - [x] WebSocket stateful: user_id→server маппинг в presence Redis; при отправке сообщения lookup → route к нужному WS серверу через Kafka; failover = reconnect + update presence | ✓ ПРИМЕНЯТЬ: 1B-scale routing через присутствие service + message broker 📋 ПРАВИЛО: WS routing = presence lookup + broker fan-out 🔗 См. Q5
> - [ ] Message broker не нужен если есть consistent hashing — прямой dispatch | ❌ ПОСЛЕДСТВИЕ: без broker: отправитель должен знать адрес сервера получателя напрямую; при rolling deploy/failover адреса меняются; broker decouples и обеспечивает delivery guarantee

## Q5. Sticky session проблема?

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
>
> **Вопрос:** Sticky sessions vs broker-routed routing для WebSocket-чата на 500M пользователей: какой подход реально используют Slack/Discord и почему?
>
> ---
>
> #### A) Sticky session по `hash(user_id) % N` — единственный валидный подход на 500M масштабе — ❌ Неверно
>
> **Что на самом деле:** sticky-routing работает, но имеет фундаментальный недостаток для динамического кластера: при добавлении/удалении WS-серверов (`N` меняется) ключи переезжают, и при reconnect клиент попадает на другой сервер. Это ломает локальный state (батчинг, типинг-индикаторы), требует rebalance presence-registry и реплеев. Slack/Discord выбрали non-sticky (Option B), потому что динамический scale-out важнее предсказуемости.
>
> **Откуда путаница:** sticky session — стандартный паттерн для stateful HTTP (session-cookie), и кажется естественным переносом на WebSocket. В литературе по chat-системам действительно описаны sticky-варианты, но они подходят для статичного кластера или с consistent-hashing + virtual nodes для смягчения rebalance.
>
> **Если бы это было правдой:** каждый rolling deploy WS-tier вызывал бы reshuffle для половины 500M соединений = 250M reconnect-штормов; latency spike до 30s; presence-flapping; «приходят с задержкой» жалобы в support.
>
> ---
>
> #### B) Non-sticky LB + broker-routed (pub/sub на `user:{id}`) — гибкий scale, extra hop per message — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> В non-sticky модели пользователь подключается на любой WS-сервер (round-robin/least-conn). При connect сервер регистрирует presence (`SET user:X server:S2 EX 60`) и подписывается на канал `user:X` в Redis pub/sub или Kafka. Когда другой сервер хочет доставить сообщение пользователю X, он публикует в `user:X` — broker делает routing, и сервер S2 (подписчик) пушит в WebSocket. Это decouples отправителя от получателя: rolling deploy, autoscale, failover не требуют сложного миграционного протокола — клиент просто переподключается, обновляет presence.
>
> **Пример:**
> ```text
> # Connect: User X → LB → WS server S2 (any)
> S2: SET user:X server:S2 EX 60
> S2: SUBSCRIBE user:X
>
> # Send: User Y on S1 → message to X
> S1: GET user:X         → server:S2 (но это hint, не required)
> S1: PUBLISH user:X "{msg}"
> Redis: → S2 (subscriber)
> S2: WebSocket.send(X, msg)
>
> # Disconnect/Reconnect: X falls off S2, lands on S5
> S5: SET user:X server:S5 EX 60   # обновляется presence
> S5: SUBSCRIBE user:X
> # старый subscribe на S2 истечёт по TTL или при close
> ```
>
> **Когда применять:**
> - Динамические кластеры с autoscale (Slack, Discord, Twitch chat).
> - Multi-region active-active deployments — broker (Kafka) переносится между регионами.
> - Когда WS-серверы регулярно деплоятся (CI/CD каждый день) — sticky привёл бы к постоянным reshuffle.
>
> **Подводные камни:**
> - Extra hop через broker добавляет 1-5ms latency — для 99% сообщений это ок, но критично для voice/typing.
> - Pub/sub fan-out: если pubsub-кластер недоступен — весь чат стоит; нужен fallback/circuit breaker.
> - Presence TTL должен быть длиннее heartbeat interval — иначе race: presence истекает между heartbeat-ами.
>
> **Связанные вопросы:** [[Q4]] — connection routing и presence service; [[Q6]] — high-level architecture с broker-tier; [[Q8]] — выбор broker (Redis vs Kafka).
>
> ---
>
> #### C) Sticky по IP клиента — самый простой и быстрый подход — ❌ Неверно
>
> **Что на самом деле:** IP-based sticky плохо работает для мобильных клиентов: IP меняется при переключении Wi-Fi/4G/5G каждые несколько минут. NAT и CGNAT (мобильные операторы) дают одинаковый IP миллионам пользователей — sticky схлопывает их на 1 сервер. И обратный кейс: один пользователь с разных IP попадает на разные серверы, дублируя WS-соединения.
>
> **Откуда путаница:** IP-hashing — стандартная опция в nginx/HAProxy/AWS ALB; новички видят галочку «sticky by IP» и считают это решением. Для bare HTTP это часто ок, но WebSocket долго-живёт и пересекает несколько NAT-границ.
>
> **Если бы это было правдой:** CGNAT-региона (Москва, Бангалор) — миллион пользователей на одном IP → все на один WS-сервер → instant overload. И переключение Wi-Fi на 4G в метро = выкинуло из чата с разрывом соединения.
>
> ---
>
> #### D) Хранить полные сообщения в presence-сервисе — не нужны Cassandra и broker — ❌ Неверно
>
> **Что на самом деле:** presence service хранит метаданные о подключениях (user_id → server_id + TTL), не содержание сообщений. Redis-presence на 500M записей × 16 байт ≈ 8GB, что реалистично; но 50B messages/day × 1KB = 50TB/day сообщений — это не presence-объём, это hot tier (Cassandra) + cold (S3).
>
> **Откуда путаница:** Redis может хранить и presence, и messages теоретически (в-память KV); легко начать с «давайте всё в Redis», пока не упрётесь в RAM/persistence/recovery time.
>
> **Если бы это было правдой:** при перезапуске Redis (или его падении) — теряются все сообщения, не только presence; recovery 50TB hot data в RAM = часы downtime; стоимость RAM × 50TB вместо disk = десятки раз дороже.

## Q6. (!) High-level architecture?

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
> - [ ] Sticky LB достаточно для routing — consistent hashing без presence service | ❌ ПОСЛЕДСТВИЕ: consistent hashing статичен; при server failover маппинг не обновится автоматически; presence service нужен для динамического routing после reconnect
> - [ ] Non-sticky LB с центральным routing через DB достаточно | ❌ ПОСЛЕДСТВИЕ: DB lookup на каждое сообщение = bottleneck при 2M msg/sec; Redis presence service с O(1) lookup — обязателен
> - [x] WS Gateway tier (stateful, millions conn) + Message Broker (Kafka/Redis) + Service Tier (Chat/Presence/Group) + Cassandra (messages) + Redis (presence) | ✓ ПРИМЕНЯТЬ: стандартная high-level архитектура WhatsApp/Slack scale chat 📋 ПРАВИЛО: WS tier stateful → broker → services → storage 🔗 См. Q7
> - [ ] Presence service хранит данные в PostgreSQL для consistency | ❌ ПОСЛЕДСТВИЕ: presence данные обновляются при каждом heartbeat (30s × 500M users = 16M updates/min); Redis TTL = идеальный fit; PostgreSQL → write bottleneck

## Q7. (!) Message delivery flow?

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
> - [ ] Нужно persist после push — если persist fails, message теряется | ❌ ПОСЛЕДСТВИЕ: нужно НАОБОРОТ: persist ПЕРЕД push; если push fails — данные в Cassandra; retry доставки из storage; если persist fails — reject сразу
> - [ ] Offline delivery: хранить в Redis до reconnect пользователя | ❌ ПОСЛЕДСТВИЕ: Redis in-memory; если Redis restarted — сообщения потеряны; для offline очереди нужна persistent storage (Cassandra + message_queue table)
> - [x] Persist в Cassandra → lookup presence → если online: publish user:X channel → WS push; если offline: trigger FCM/APNs + запись в undelivered queue | ✓ ПРИМЕНЯТЬ: deliver guarantee через persist-first + ACK pattern 📋 ПРАВИЛО: persist first, push second, retry from storage 🔗 См. Q8
> - [ ] ACK от клиента не нужен — достаточно TCP ACK | ❌ ПОСЛЕДСТВИЕ: TCP ACK = network delivery; application ACK нужен для read receipt и delivery confirmation; WhatsApp показывает ✓ (sent) / ✓✓ (delivered) / ✓✓ blue (read)

## Q8. Message broker между серверами?

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
>
> **Вопрос:** Redis pub/sub vs Kafka vs hybrid как message broker между WS-серверами: какой подход правильно описывает trade-offs для chat-системы?
>
> ---
>
> #### A) Kafka — лучший выбор для всех сообщений чата: persistence + replay — ❌ Неверно
>
> **Что на самом деле:** Kafka даёт durability и replay, но добавляет 10-100ms latency vs Redis pub/sub (микросекунды). Для real-time signaling (typing, presence, presence-change) Kafka слишком медленный — пользователь чувствует лаг. Kafka хорош для аналитики, audit-log, persistent fan-out — но не для горячей доставки.
>
> **Откуда путаница:** Kafka — «king of messaging» в современной архитектуре; естественно тянуть его везде. На chat-конфах часто упоминают Kafka для message ingestion, и middle-разработчик может обобщить «всё через Kafka».
>
> **Если бы это было правдой:** typing indicator появлялся бы с задержкой 50-100ms — UX ощущается как «отстаёт»; presence-flicker; для 2M msg/sec нужно ~50 Kafka brokers + huge tuning; стоимость инфры в 10× выше Redis для signaling-объёма.
>
> ---
>
> #### B) Redis pub/sub — единственный нужный broker, Kafka избыточен — ❌ Неверно
>
> **Что на самом деле:** Redis pub/sub fire-and-forget: если subscriber на момент publish был disconnected (network blip, GC pause) — сообщение потеряно. Для chat-доставки это неприемлемо (NFR: no message loss). Redis pub/sub отлично для ephemeral signaling, но persistent messages нужны в durable storage (Cassandra/Kafka).
>
> **Откуда путаница:** Redis pub/sub — самый простой и быстрый брокер; легко начать прототип и забыть про durability. В небольших чатах действительно хватает Redis + DB.
>
> **Если бы это было правдой:** при kubernetes pod-restart WS-сервера во время publish — сообщения, направленные пользователям этого сервера, теряются молча; нет retry; жалобы «сообщение не дошло» без следов в логах.
>
> ---
>
> #### C) Hybrid: Redis pub/sub для real-time signaling (typing/presence) + Kafka для persistent message stream (audit, analytics, offline delivery) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Реальные chat-системы используют разные брокеры для разных классов событий. Redis pub/sub держит fan-out горячих сообщений к WS-серверам (typing, presence, ephemeral signal) с микросекундной latency. Kafka параллельно получает каждое сообщение для downstream: analytics, ML, audit, retry-очередь для offline. Cassandra (или другой write-DB) — primary persistence; Kafka — change-data-capture stream для read-models. Такой разделённый pipeline даёт горячий путь без durability-tax + холодный путь с replay.
>
> **Пример:**
> ```mermaid
> graph LR
>   WSx[WS Server X] --> Persist[Cassandra]
>   WSx --> Redis[Redis pub/sub<br/>signaling]
>   WSx --> Kafka[Kafka<br/>messages.events]
>   Redis --> WSy[WS Server Y subscribers]
>   Kafka --> Analytics[Analytics]
>   Kafka --> Notif[Notification Service<br/>FCM/APNs]
>   Kafka --> Search[Elasticsearch indexer]
> ```
>
> **Когда применять:**
> - Slack: Redis для presence + WS signaling, Kafka для message-events.
> - WhatsApp: custom Erlang-based broker + Mnesia (in-house equivalent).
> - LinkedIn messaging: Kafka обязателен из-за их Kafka-first culture.
> - Когда нужен offline-delivery + replay (новый клиент joined channel — pull history через Kafka offset).
>
> **Подводные камни:**
> - Согласованность между двумя путями: что если Kafka получил, а Redis не дошёл? Cassandra-first как source of truth + idempotent consumers.
> - Дублирование: пользователь онлайн → Redis push + Kafka → notification service видит как offline-event → дубль push. Решение: notification service сверяется с presence до отправки push.
> - Operational complexity: 2 broker-кластера = 2× operational burden, мониторинг, backups.
>
> **Связанные вопросы:** [[Q5]] — pub/sub каналы `user:{id}` для broker-routed delivery; [[Q7]] — message delivery flow с persistence-first; [[Q18]] — push notifications для offline через Kafka consumer.
>
> ---
>
> #### D) RabbitMQ — единственный broker для chat: AMQP, queues per user — ❌ Неверно
>
> **Что на самом деле:** RabbitMQ — отличный broker, но queue-per-user-per-server модель не масштабируется на 500M пользователей: 500M очередей с metadata overhead в Erlang VM = десятки GB только под management; queue create/delete при connect/disconnect = операционный шторм. Pub/sub topology в RabbitMQ есть (exchange = topic), но scale ограничен ~50K queues на ноду эффективно.
>
> **Откуда путаница:** RabbitMQ — популярный AMQP-broker с богатыми routing-возможностями (topic, fanout, direct exchanges); кажется естественным для messaging. Для enterprise integration RabbitMQ королевен, но для real-time chat с миллиардами пользователей он не tuned.
>
> **Если бы это было правдой:** WhatsApp/Slack использовали бы RabbitMQ как primary broker — но они выбрали custom Erlang (WhatsApp) и Kafka/Redis (Slack), что и подтверждает: RabbitMQ не для этого scale.

## Q9. (!) Schema для messages?

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
>
> **Вопрос:** Какая schema для messages в Cassandra корректна для chat-системы с PK и clustering order?
>
> ---
>
> #### A) `PRIMARY KEY (message_id)` — каждое сообщение — отдельная партиция — ❌ Неверно
>
> **Что на самом деле:** в Cassandra partition key = единица распределения по нодам. Если каждое сообщение — собственная партиция, то range query «дай мне последние 50 сообщений в этом chat» превращается в **scatter-gather по всему кластеру** — Cassandra сканирует 10K+ нод, агрегирует, медленно. Правильно: partition by `conversation_id`, чтобы все сообщения чата были на одной ноде, читались O(1) seek + sequential.
>
> **Откуда путаница:** в SQL примитивно ставят PK на «уникальное id» — это нормально для нормализованной таблицы. В Cassandra PK = (partition key, clustering keys), и partition выбирают по data locality, не по уникальности.
>
> **Если бы это было правдой:** загрузка чата (50 msgs) занимала бы 500ms-2s из-за scatter-gather; tombstones расползались бы по кластеру; compaction не помогал бы консолидировать данные.
>
> ---
>
> #### B) `PRIMARY KEY (sender_id, message_id)` — партиция по отправителю — ❌ Неверно
>
> **Что на самом деле:** партиция по `sender_id` означает: все сообщения от user A на одной ноде. Но запросы в чате — «дай сообщения в conversation X», независимо от отправителя. С такой schema каждый запрос — scatter по всем участникам беседы (для группы 100 человек = 100 партиций).
>
> **Откуда путаница:** sender_id кажется естественным `owner` ключом — «человек владеет своими сообщениями». В Twitter feed-системах partition по author работает для outbox-модели. В чате access pattern другой: читаем по conversation, не по author.
>
> **Если бы это было правдой:** чтение группового чата на 100 участников = 100 партиций = N+1 storm; latency p99 в секундах; load на конкретные ноды (активные spammers) — hot partition.
>
> ---
>
> #### C) `PRIMARY KEY (conversation_id, sent_at)` без message_id и без TIMEUUID — ❌ Неверно
>
> **Что на самом деле:** `sent_at TIMESTAMP` теряет точность ниже миллисекунды; если два сообщения отправлены одновременно — конфликт clustering key (одно перезатрёт другое или будет undefined order). TIMEUUID комбинирует timestamp + 60-bit random + node-id → guaranteed unique + sortable. И без message_id отдельным колонком теряется ID для ACK, dedup, идемпотентности.
>
> **Откуда путаница:** в SQL принято использовать `TIMESTAMP` для сортировки. Cassandra TIMEUUID — специфика, новички могут не знать.
>
> **Если бы это было правдой:** при пиковой нагрузке два сообщения с одной timestamp → второе LOST (Cassandra Last-Write-Wins); дубликаты при retry неотличимы без uniq ID; невозможен delivery ACK по message_id.
>
> ---
>
> #### D) `PRIMARY KEY ((conversation_id), message_id) WITH CLUSTERING ORDER BY (message_id DESC)`, где `message_id` — TIMEUUID — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Partition key `conversation_id` локализует все сообщения одного чата на одной replica-group; clustering key `message_id` (TIMEUUID) обеспечивает сортировку внутри партиции + уникальность. `DESC` order ускоряет «последние 50 сообщений» — это самый частый read-paten в чате. TIMEUUID v1 = 60-bit timestamp + 14-bit clock sequence + 48-bit MAC/node — даёт уникальность даже при одинаковой ms; сортируемость по времени; легко конвертируется обратно в timestamp.
>
> **Пример:**
> ```sql
> CREATE TABLE messages (
>     conversation_id UUID,
>     message_id      TIMEUUID,
>     sender_id       UUID,
>     content         TEXT,
>     sent_at         TIMESTAMP,
>     PRIMARY KEY ((conversation_id), message_id)
> ) WITH CLUSTERING ORDER BY (message_id DESC)
>   AND default_time_to_live = 7776000;  -- 90 days TTL
>
> -- Запрос «50 последних сообщений в чате»:
> SELECT * FROM messages
> WHERE conversation_id = ?
> LIMIT 50;
> -- Cassandra: 1 partition, sequential scan от head, O(50) seek
>
> -- Дополнительная таблица для inbox-listing:
> CREATE TABLE user_conversations (
>     user_id          UUID,
>     last_message_at  TIMESTAMP,
>     conversation_id  UUID,
>     PRIMARY KEY ((user_id), last_message_at, conversation_id)
> ) WITH CLUSTERING ORDER BY (last_message_at DESC);
> ```
>
> **Когда применять:**
> - Cassandra/ScyllaDB для chat: WhatsApp custom, Discord migrated to ScyllaDB в 2022, Instagram DM на Cassandra.
> - DynamoDB-эквивалент: partition key = `conversation_id`, sort key = `message_id` TIMEUUID или ULID.
> - Любое time-series по «логическому контейнеру» (conversation, room, session).
>
> **Подводные камни:**
> - Hot partition: chat с celeb (Илон Маск ответил в группе 500K) — все пишут в одну партицию, replica overload. Mitigation: bucket'ing partition key (`conversation_id::bucket=YYYYMM`).
> - Wide partition: 10M+ сообщений в одной conversation → партиция > 100MB, Cassandra warns/throttles. Mitigation: bucket по дате/году, archive в S3 (cold tier).
> - TTL и tombstones: при тяжёлой retention-политике количество tombstone-марки растёт; нужен tuned compaction strategy (TimeWindowCompactionStrategy для time-series).
>
> **Связанные вопросы:** [[Q10]] — почему NoSQL подходит chat-write-heavy patterns; [[Q11]] — sharding strategy и hot partition mitigation; [[Q14]] — ordering через TIMEUUID + per-conversation sequence.

## Q10. (!) SQL vs NoSQL для chat?

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
>
> **Вопрос:** Polyglot persistence для chat: какое распределение SQL/NoSQL/Redis по типам данных правильно для WhatsApp-scale?
>
> ---
>
> #### A) Cassandra/Scylla для messages (write-heavy time-series); PostgreSQL для users/friends (relational, ACID); Redis для presence (ephemeral KV + TTL); S3 для media — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Polyglot persistence — это явное матчинг storage'а к access pattern. Messages: huge write throughput (2M msg/sec), time-series, partition by conversation → Cassandra (LSM-tree, masterless replication, linear scale). Users/Friends: small relational dataset (10-100GB), нужны JOIN, FK, ACID transactions для friend-requests/blocks → PostgreSQL. Presence: 500M ephemeral entries, TTL-based, sub-ms latency → Redis. Media: blob-storage, CDN-friendly, lifecycle policies → S3 (или GCS/Azure Blob). Это правило «right tool for right job» — не «один DB для всего».
>
> **Пример:**
> ```text
> # Storage map
>
> Messages (write-heavy, time-series, 50TB+):
>   → Cassandra/ScyllaDB
>   → partition by conversation_id
>   → TTL 90 days; archive to S3 cold tier
>
> Users (relational, 10GB):
>   → PostgreSQL (sharded by user_id range)
>   → fields: id, name, phone, public_keys, settings
>   → FK from `friends`, `blocks`
>
> Friends/Blocks (relational, small):
>   → PostgreSQL (same shard as users)
>
> Group membership (relational, medium):
>   → PostgreSQL OR Cassandra denormalized
>
> Presence (ephemeral, 500M × ~50B = 25GB):
>   → Redis cluster (sharded by user_id hash)
>   → SET user:X server:S2 EX 60 (TTL renewed by heartbeat)
>
> Media (photos, video, blobs):
>   → S3 / GCS
>   → presigned URLs; CDN in front (CloudFront)
>   → server-side or client-side encryption
>
> Search (E2E challenge):
>   → client-side SQLite FTS (E2E systems)
>   → Elasticsearch (non-E2E like Slack)
> ```
>
> **Когда применять:**
> - Любой messaging at scale: WhatsApp (Mnesia + custom), Slack (MySQL sharded + Solr), Discord (Cassandra → ScyllaDB), Instagram DM (Cassandra).
> - Когда разные классы данных имеют разные SLO: messages need throughput, users need consistency, presence needs latency.
> - Команда имеет capacity управлять несколькими storage-системами (это не для small team).
>
> **Подводные камни:**
> - Operational complexity растёт линейно с количеством storage: бэкапы, monitoring, on-call, migrations. Discord потратили 9 месяцев на Cassandra → ScyllaDB миграцию.
> - Cross-store transactions невозможны: что если user удалён в Postgres, но messages в Cassandra остались? Нужны eventual consistency + cleanup jobs.
> - Search-сценарии: full-text по messages требует ETL в Elasticsearch (для non-E2E) — добавляет lag.
>
> **Связанные вопросы:** [[Q9]] — детали Cassandra schema для messages; [[Q11]] — sharding strategy в Cassandra; [[Q15]] — Redis для presence storage детально.
>
> ---
>
> #### B) PostgreSQL для всего: messages, users, presence — простота операций важнее scale — ❌ Неверно
>
> **Что на самом деле:** PostgreSQL отлично масштабируется до ~100K writes/sec на хорошо тюненом инстансе с sharding (Citus, Postgres-XL), но 2M msg/sec в реальном чате потребует 20-50 shards с консистентной cross-shard логикой. Это возможно, но сложнее, чем Cassandra (которая designed для этого нативно). Presence в PostgreSQL — 500M rows с UPDATE каждые 30 секунд = constant WAL pressure, vacuum-storm.
>
> **Откуда путаница:** «одна база — одна боль» — операционно проще. Для проекта на 1M пользователей PostgreSQL действительно покрывает всё. Но on 1B scale накапливается долг.
>
> **Если бы это было правдой:** Slack использует MySQL для messages (с heroic engineering: 1000+ shards + Vitess-like proxy); WhatsApp не использует никакую SQL для core message path — они выбрали custom Erlang Mnesia. Реальность показывает: 1 SQL для всего не масштабируется без heroics.
>
> ---
>
> #### C) MongoDB для всего: schema-less даёт гибкость для эволюции features — ❌ Неверно
>
> **Что на самом деле:** MongoDB документная БД с хорошим horizontal scaling, но для chat-time-series она не оптимальна: write amplification из-за документной модели (BSON encoding), нет clustering-order как в Cassandra (нужны индексы → больше write cost), и для presence/ephemeral данных Redis быстрее в 10× по latency.
>
> **Откуда путаница:** MongoDB популярна, schema-less удобна для прототипа. Старые статьи описывали Foursquare/Craigslist на Mongo как success-stories.
>
> **Если бы это было правдой:** WhatsApp/Slack/Discord использовали бы Mongo — но они выбрали другие решения. Discord даже мигрировал с Cassandra на ScyllaDB (не на Mongo) для лучшей tail latency.
>
> ---
>
> #### D) Только Redis для всего: in-memory быстрее любой disk-DB — ❌ Неверно
>
> **Что на самом деле:** Redis = RAM-storage. 50TB hot messages × $10/GB RAM (cloud) = $500K/месяц только за память + 3× для replication. Disk storage в Cassandra: $0.1/GB/месяц = в 100× дешевле. Plus Redis recovery после краша — десятки минут на загрузку snapshot/AOF; Cassandra masterless кластер не имеет single-node recovery problem.
>
> **Откуда путаница:** Redis subjectively «быстрее» — в-память. Но «быстрее» значит latency, а не throughput или durability. Для 50TB data объёма Redis нерационален.
>
> **Если бы это было правдой:** Redis Labs/Aiven использовали бы Redis-cluster для PB-scale storage — но они продают managed Redis именно для hot KV-кейсов, не для bulk-data. Архитектурные blueprints Discord/WhatsApp подтверждают: Redis для presence/cache, не для primary storage.

## Q11. Shard strategy?

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
>
> **Вопрос:** Sharding для chat messages: по `user_id` или по `conversation_id`? И как обрабатывать hot partition (групповой чат с celebrity)?
>
> ---
>
> #### A) Sharding по `user_id` — каждый user имеет outbox-партицию со своими отправленными сообщениями — ❌ Неверно
>
> **Что на самом деле:** outbox-модель (sharding по author/sender) хорошо работает для **feed-систем** (Twitter, Instagram), где каждый пишет свои посты, а читают по follow-graph (с fan-out). Для chat access pattern другой: «прочитать conversation» = собрать сообщения от всех участников. Sharding по user_id заставляет cross-shard query на каждое чтение (для группы 100 человек = 100 shards).
>
> **Откуда путаница:** в курсах по DDD и event-sourcing часто упоминается «outbox per aggregate root». Для пользователя как aggregate это user-id sharding. Но chat aggregate root — conversation, не user.
>
> **Если бы это было правдой:** загрузка чата с N участниками = N запросов параллельно к N shards = scatter-gather; latency = max(shard_latency); 100-member group = 100 RPC, p99 = катастрофа.
>
> ---
>
> #### B) Sharding по `conversation_id` (consistent hash в Cassandra/Scylla token ring) + bucket'ing для hot partition (group:viral_id::bucket=2026Q2) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Conversation_id как partition key даёт data locality: все сообщения одного чата на одной replica-group. Cassandra token ring + virtual nodes (256-1024 vnodes per physical node) обеспечивает равномерное распределение по кластеру без manual sharding. Для горячих партиций (celebrity ответил в группе 500K участников — все пишут параллельно) применяют **bucket'ing**: расширяют partition key до `(conversation_id, bucket)`, где bucket = month/week/hour, чтобы шарить write-нагрузку по времени.
>
> **Пример:**
> ```sql
> -- Базовая schema: shard by conversation
> CREATE TABLE messages (
>     conversation_id  UUID,
>     message_id       TIMEUUID,
>     sender_id        UUID,
>     content          TEXT,
>     PRIMARY KEY ((conversation_id), message_id)
> ) WITH CLUSTERING ORDER BY (message_id DESC);
>
> -- Для горячих conversations (celebrity group, viral chat):
> -- Bucket'ing: расширяем partition key
> CREATE TABLE messages_bucketed (
>     conversation_id  UUID,
>     bucket           TEXT,           -- '2026-05' или hash bucket
>     message_id       TIMEUUID,
>     sender_id        UUID,
>     content          TEXT,
>     PRIMARY KEY ((conversation_id, bucket), message_id)
> ) WITH CLUSTERING ORDER BY (message_id DESC);
>
> -- Чтение: расширить запрос на нужные buckets
> SELECT * FROM messages_bucketed
> WHERE conversation_id = ? AND bucket IN ('2026-05', '2026-04')
> LIMIT 50;
> ```
>
> **Когда применять:**
> - Cassandra/ScyllaDB chat-store: partition by conversation — стандарт. Discord, Instagram DM.
> - Bucket'ing для известных hot conversations: viral group chats, broadcast channels, support tickets.
> - DynamoDB: composite partition key `(conversation_id, bucket)` — обходим 10MB/partition limit.
>
> **Подводные камни:**
> - Bucket choice critical: слишком крупный (year) — bucket остаётся горячим; слишком мелкий (hour) — слишком много партиций при чтении.
> - Range queries усложняются: чтобы прочитать «last 50» при bucket'ing нужно знать активные buckets — обычно metadata table.
> - Tombstones и compaction: bucket по дате хорошо для TimeWindowCompactionStrategy; per-conversation TTL drift делает compaction менее предсказуемым.
>
> **Связанные вопросы:** [[Q9]] — Cassandra schema с partition by conversation_id; [[Q10]] — почему NoSQL и polyglot storage; [[Q17]] — group chat fan-out для очень больших групп.
>
> ---
>
> #### C) Sharding по `message_id` (random hash) — равномерное распределение, нет hot partition — ❌ Неверно
>
> **Что на самом деле:** sharding по случайному message_id рассыпает сообщения одной conversation по всему кластеру. Чтение «последние 50 в чате» превращается в scatter-gather с фильтрацией по conversation_id — Cassandra не оптимизирована для такого, нужны secondary indexes (плохо масштабируются) или full scan.
>
> **Откуда путаница:** «random sharding» избегает hot partition — это правильно для write-only datasets без сложных reads. Но chat read pattern сильно завязан на conversation.
>
> **Если бы это было правдой:** каждое открытие чата сканировало бы весь кластер; нагрузка на coordinator-ноду огромная; p99 чтения секунды.
>
> ---
>
> #### D) Sharding по `sent_at` (time-based partitions) — естественное для time-series — ❌ Неверно
>
> **Что на самом деле:** time-based sharding (партиция = день/час) создаёт hot-write-partition «сегодня»: 100% writes идут в одну партицию = single replica-group overload. И чтение `чат X за весь период` = scan всех time-partitions.
>
> **Откуда путаница:** time-based partitioning отлично работает для аналитики/metrics (Prometheus, InfluxDB), где writes растекаются по разным метрикам. Для chat single hot conversation концентрирует writes.
>
> **Если бы это было правдой:** Cassandra TTL и compaction по таблице, ориентированные на time-bucket, не работали бы для chat-моделей; пиковый трафик «сегодня» убивал бы одну реплику.

## Q12. (!) At-most-once, at-least-once, exactly-once?

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
>
> **Вопрос:** Какая стратегия доставки сообщений правильна для chat и как достичь «effective exactly-once» без 2PC?
>
> ---
>
> #### A) At-most-once: send-and-forget без retry — приемлемо для чата, потеря 1% сообщений терпима — ❌ Неверно
>
> **Что на самом деле:** at-most-once значит «отправили один раз, не подтверждаем, не ретраим». Любая network blip = lost message. NFR чата явно требует «no message loss» (Q1) — это краеугольный камень UX. Пользователь, отправивший сообщение, ожидает 100% доставки.
>
> **Откуда путаница:** at-most-once вижу в UDP-протоколах (DNS query, metrics, gaming) — где потеря приемлема. Новички могут перенести эту модель на чат.
>
> **Если бы это было правдой:** «Сообщение не пришло» — самая распространённая жалоба; критические сообщения (банковские OTP, business chat) теряются → пользователи уходят с платформы.
>
> ---
>
> #### B) Exactly-once через distributed two-phase commit (2PC) между client → server → recipient — ❌ Неверно
>
> **Что на самом деле:** 2PC в распределённой системе известен как «coordinator-as-SPOF» и блокирует ресурсы на время phase 1 + phase 2. Для chat с 2M msg/sec 2PC задушит latency до 100ms+; coordinator crash в середине = locks висят. Sage Kelvin: «true exactly-once over network is impossible» (Two Generals Problem). Реальное «exactly-once» — это at-least-once + idempotent consumer = effective exactly-once.
>
> **Откуда путаница:** «exactly-once delivery» — маркетинговый термин, который Kafka, Pulsar, Flink активно продвигают. Но они достигают его именно через idempotency + transactions, не через 2PC сети.
>
> **Если бы это было правдой:** WhatsApp/Telegram/Slack использовали бы 2PC — но они используют at-least-once + idempotency. 2PC не масштабируется ни в одной известной chat-системе.
>
> ---
>
> #### C) At-least-once delivery + idempotent processing через client-generated UUID + `INSERT IF NOT EXISTS`: retry безопасен, дубликаты — no-op — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Клиент генерирует UUID (или ULID/Snowflake ID) для каждого сообщения **перед отправкой**. Сервер использует этот UUID как primary key (или unique constraint) — `INSERT IF NOT EXISTS` (Cassandra LWT) или `INSERT ... ON CONFLICT DO NOTHING` (Postgres). Если клиент не получил ACK и retry — сервер видит тот же UUID и пропускает (idempotent). End-to-end ACK chain: client→server→recipient→ACK back → sender видит ✓✓. Результат: effectively exactly-once, без сетевых блокировок.
>
> **Пример:**
> ```text
> # Client side
> message_id = UUID()  # или ULID для sortable
> retry_count = 0
> while retry_count < MAX:
>     try:
>         response = ws.send({
>             "message_id": message_id,
>             "conversation_id": conv_id,
>             "content": text,
>             "client_timestamp": now()
>         })
>         if response.ack: break
>     except (NetworkError, Timeout):
>         retry_count += 1
>         sleep(exponential_backoff(retry_count))
>
> # Server side (Cassandra LWT)
> INSERT INTO messages (
>     conversation_id, message_id, sender_id, content, sent_at
> ) VALUES (?, ?, ?, ?, toTimestamp(now()))
> IF NOT EXISTS;
> -- Если запись уже есть → applied=false, treat as success (idempotent)
> -- Если новая → applied=true, persisted
>
> # ACK обратно
> response = {"ack": true, "server_timestamp": ..., "message_id": ...}
>
> # End-to-end delivery chain
> Client A: ✓ sent (server ACK)
> Client A: ✓✓ delivered (recipient device ACK)
> Client A: ✓✓ blue (recipient opened chat, read receipt)
> ```
>
> **Когда применять:**
> - Любое messaging с no-loss требованием: WhatsApp, Slack, Telegram, Discord, Signal.
> - Kafka producers с `enable.idempotence=true` + transactional consumers = тот же паттерн.
> - Payment systems с idempotency-key в HTTP headers (Stripe, PayPal).
>
> **Подводные камни:**
> - Идемпотентность работает только если **client сохраняет message_id между retry**: если клиент сгенерил новый UUID при retry — дубль будет сохранён.
> - Cassandra LWT (lightweight transactions) дороже обычного INSERT в 4× (paxos round); для hot path лучше `INSERT` + асинхронный dedup job, либо использовать `IF NOT EXISTS` точечно.
> - Out-of-order delivery всё ещё возможен: at-least-once гарантирует доставку, но не порядок — ordering решается отдельно через TIMEUUID/sequence.
>
> **Связанные вопросы:** [[Q7]] — message delivery flow с persist-first + ACK; [[Q13]] — delivery vs read receipts; [[Q14]] — ordering guarantees отдельно от delivery.
>
> ---
>
> #### D) Server-generated message_id вместо client-generated — единый источник truth — ❌ Неверно
>
> **Что на самом деле:** если сервер генерирует ID после получения сообщения, то клиент при retry не имеет idempotency key для дедупа. Network: client отправил → server INSERT → server упал до ACK → client retry → server делает второй INSERT с новым server-id = дубликат, который клиент не сможет распознать.
>
> **Откуда путаница:** server-side ID-generation выглядит логично для авторитетности (autoincrement, snowflake from server). Это работает для не-retry-сценариев (HTTP POST с at-most-once), но для chat с at-least-once + idempotency нужен client-side ID.
>
> **Если бы это было правдой:** при флакающем connection (метро, лифт) каждое сообщение могло бы дублироваться 2-3 раза; recipient видел бы «привет», «привет», «привет» — критический UX-bug.

## Q13. (!) Read receipts и delivery receipts?

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
>
> **Вопрос:** В групповом чате 100 человек посылается одно сообщение — как правильно хранить read receipts и почему?
>
> ---
>
> #### A) Один булев флаг `is_read` в таблице `messages` — обновлять при чтении любым recipient'ом — ❌ Неверно
>
> **Что на самом деле:** read receipts нужны per-recipient: «прочитано Алисой, не прочитано Бобом». Один булев флаг не различает, кто прочитал. Для группового чата UI показывает «Прочитано 7 из 10» — это требует знания статуса каждого recipient.
>
> **Откуда путаница:** в простом 1:1 чате (учебный пример) можно обойтись `is_read` на сообщении. Но реальность — групповые чаты, где этого недостаточно.
>
> **Если бы это было правдой:** в групповом чате `is_read=true` после первого читателя — для остальных 99 сообщение «прочитано», хотя они даже не открыли приложение; невозможно показать «не доставлено Бобу».
>
> ---
>
> #### B) Synchronous UPDATE статуса в main message-table при каждом ACK от recipient — ❌ Неверно
>
> **Что на самом деле:** UPDATE на messages table при каждом read-event = огромная write-amplification. Группа на 100 человек: 1 message × 100 read-events = 100 UPDATE-ов на один message-row. В Cassandra UPDATE = INSERT (с tombstone old value) — copmaction-storm. И synchronous блокирует delivery main path.
>
> **Откуда путаница:** «обновим статус сообщения» — естественное SQL-мышление. В Cassandra UPDATE на partitioned column — дорого.
>
> **Если бы это было правдой:** при 50B сообщений/день и avg группе 20 человек = 1T status-updates/день на main messages table; WAL/commitlog overflow; compaction никогда не догоняет.
>
> ---
>
> #### C) Bilateral privacy: серверу не отправлять read receipts вообще — приватность важнее UI — ❌ Неверно
>
> **Что на самом деле:** read receipts — фундаментальная фича UX чата (WhatsApp ✓✓ blue, iMessage «Delivered/Read»). Их можно сделать opt-in/opt-out per user (WhatsApp settings), но not-by-default disabled. Bilateral privacy = «если ты отключил — я не вижу твоё чтение, ты не видишь моё». Это feature, а не отсутствие read receipts.
>
> **Откуда путаница:** некоторые мессенджеры (Telegram, Signal) делают акцент на приватности; «отключи read receipts» — настройка, но не дизайн default.
>
> **Если бы это было правдой:** WhatsApp/iMessage не имели бы ✓✓ blue feature — но это одна из самых популярных и UX-ценных особенностей, продукт без неё невыполним.
>
> ---
>
> #### D) Отдельная таблица `message_status (message_id, user_id, status, ts)` с async batch-update; для групп 100 человек = 100 status-rows на message — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Read/delivery receipts хранятся в отдельной таблице, где partition key включает recipient (или conversation + message), а row per (message_id, user_id). Это даёт O(1) lookup статуса конкретного recipient и не загрязняет main message-table. Update идёт асинхронно через Kafka/queue: recipient WS-client отправляет batch read-events (например 10 сообщений «прочитано»), сервер пишет их в `message_status` через 1 batch-INSERT, потом notify-update sender через pub/sub. Sender UI агрегирует: «Прочитано N из M».
>
> **Пример:**
> ```sql
> -- Cassandra schema
> CREATE TABLE message_status (
>     message_id   TIMEUUID,        -- partition key
>     user_id      UUID,            -- clustering key
>     status       TEXT,            -- 'delivered' | 'read'
>     ts           TIMESTAMP,
>     PRIMARY KEY ((message_id), user_id)
> );
> -- Lookup «кто прочитал сообщение X» — 1 partition, full scan клика
>
> -- Альтернативная denormalization для inbox-view:
> CREATE TABLE unread_count_per_user (
>     user_id          UUID,
>     conversation_id  UUID,
>     unread_count     COUNTER,
>     PRIMARY KEY ((user_id), conversation_id)
> );
> -- При read-event: UPDATE ... SET unread_count = unread_count - delta
>
> -- WebSocket client batch event:
> {
>   "type": "read_batch",
>   "conversation_id": "abc",
>   "message_ids": ["msg1", "msg2", ..., "msg10"],
>   "read_at": "2026-05-15T10:30:00Z"
> }
>
> -- Async processing: 10 INSERTs in batch + 1 pub/sub event to sender
> PUBLISH user:sender_id "read_event:{recipient: bob, msg_ids: [...]}"
> ```
>
> **Когда применять:**
> - WhatsApp/iMessage/Slack: все используют separate status tables.
> - Группы > 10 человек: per-recipient статус критичен для UI «N of M read».
> - Когда нужны метрики engagement: «сколько % сообщений прочитано в час» — отдельная таблица легко аналитизируется.
>
> **Подводные камни:**
> - Storage amplification: 1 group message × 100 members = 100 status rows; для группы 500 человек × 1000 messages/day = 500K rows только на статусы.
> - Eventual consistency: read-event может прийти позже delivery-event (out-of-order from network); нужна логика «read implies delivered», обновляет оба статуса atomically.
> - Privacy bilateral: если user отключил read receipts, server должен **не публиковать** read-event к sender'у, даже если статус сохранён локально (для unread count).
>
> **Связанные вопросы:** [[Q12]] — delivery semantics через end-to-end ACK chain; [[Q14]] — ordering для message_id уникальности; [[Q17]] — groups и fan-out для status updates.

## Q14. Ordering guarantees?

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
>
> **Вопрос:** Какие ordering guarantees нужны и достаточны для chat-системы, и почему global ordering — overkill?
>
> ---
>
> #### A) Per-conversation ordering через TIMEUUID + server-assigned timestamp authoritative; global ordering не требуется — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Chat-семантика требует, чтобы **внутри одной conversation** все участники видели сообщения в одном порядке. Между разными conversations порядок не важен — никто не сравнивает «сообщение в чате с мамой пришло раньше, чем в чате с друзьями». TIMEUUID v1 (60-bit time + 14-bit clock-seq + 48-bit node-id) даёт sortable + globally unique IDs. Server присваивает timestamp при receive — это **authoritative source of truth**; client-side timestamp untrusted (clock skew). Cassandra clustering order by message_id обеспечивает order внутри partition (conversation).
>
> **Пример:**
> ```text
> # Client A пишет at 10:00:01.234
> client_ts = 10:00:01.234  # local clock (untrusted)
> ws.send({message_id: clientUUID, content: "...", client_ts})
>
> # Server processes at 10:00:01.456 (network delay)
> server_message_id = TIMEUUID(now())  # canonical ID
> server_ts = 10:00:01.456
> INSERT INTO messages (
>     conversation_id, message_id, sender_id, content, sent_at, client_ts
> ) VALUES (?, server_message_id, ..., server_ts, client_ts);
>
> # Все recipients получают message_id = server_message_id
> # Сортировка везде одинаковая
>
> # Edge case: A и B пишут одновременно в group
> A: server_ts=10:00:01.456, message_id=TIMEUUID_A
> B: server_ts=10:00:01.456, message_id=TIMEUUID_B
> # TIMEUUID разные по clock-seq + node-id → deterministic order
> # Все participants видят (A, B) или (B, A) — но одинаково
> ```
>
> **Когда применять:**
> - Любой chat per-conversation ordering: WhatsApp, Slack, Telegram, Discord.
> - Любой time-series где partition имеет logical isolation: per-user activity stream, per-device telemetry.
> - DynamoDB sort key с ULID/TIMEUUID — тот же паттерн.
>
> **Подводные камни:**
> - Clock skew между serverами в кластере: NTP может drift на 10-50ms; если 2 messages пришли на разные WS-серверы с разными clocks — TIMEUUID v1 sortable globally, но логика «A пришло раньше B» может быть неверна на скорости latency. Mitigation: synchronized NTP + PTP в DC.
> - Out-of-order delivery: client может получить M3 перед M2 из-за network paths; client-side reorder by message_id обязателен.
> - Per-conversation ordering не даёт «causally consistent» order между разными чатами — если важно (rare), нужны Lamport timestamps.
>
> **Связанные вопросы:** [[Q9]] — TIMEUUID в Cassandra schema; [[Q12]] — delivery guarantees отдельно от ordering; [[Q17]] — group ordering — shared sequence per group.
>
> ---
>
> #### B) Lamport timestamps на client-side с monotonic counter — каждое сообщение увеличивает счётчик — ❌ Неверно
>
> **Что на самом деле:** Lamport timestamps работают для **causally related events** в distributed system. В chat client-side counter — это исключительно local view; два разных клиента имеют разные счётчики, не согласованные. Чтобы сравнить (Lamport_A=5, Lamport_B=3) нужна causality-info, которой просто нет: пользователи независимы.
>
> **Откуда путаница:** Lamport timestamps — caнonical answer в курсах по distributed systems; их вспоминают для «ordering». Но они применимы к causally-dependent events (request → response), не к независимым writes.
>
> **Если бы это было правдой:** клиент с быстрым набором текста (high counter) всегда показывался бы «после» клиента с медленным; UI rendering зависел бы от пользовательского typing-speed.
>
> ---
>
> #### C) Vector clocks для total ordering между всеми участниками chat — ❌ Неверно
>
> **Что на самом деле:** vector clocks решают partial ordering в distributed datastores (DynamoDB old, Riak). Размер vector clock растёт с числом writers — для группы 100 человек vector clock = 100 entries × 16 bytes = 1.6KB на каждое сообщение overhead. Это огромный bloat для текста размером 100B. И vector clocks дают partial order, не total — нужен tiebreaker (типа node-id), что и делает TIMEUUID одной операцией.
>
> **Откуда путаница:** vector clocks упоминают в одних учебниках с Lamport; кажется «более продвинутый» вариант. На практике они применимы к multi-master eventually-consistent KV (Riak/Dynamo), не к chat-доставке.
>
> **Если бы это было правдой:** WhatsApp/Slack использовали бы vector clocks — но в их инженерных blog vector clocks не упоминаются для message-ordering.
>
> ---
>
> #### D) Global ordering across all conversations через distributed sequencer (Zookeeper / etcd) — ❌ Неверно
>
> **Что на самом деле:** global sequencer становится узким горлышком (2M msg/sec через один counter невозможен) и SPOF. И global order не нужен: пользователь не сравнивает порядок между разными чатами. Per-conversation order достаточен и масштабируется линейно.
>
> **Откуда путаница:** «total order» звучит академически правильно. В системах типа Kafka (single partition) total order возможен через single-writer, но это ограничивает throughput.
>
> **Если бы это было правдой:** Zookeeper-counter на 2M msg/sec задушил бы весь кластер; latency для каждой записи +5ms на atomic increment; SPOF при cluster maintenance.

## Q15. (!) Online status / presence?

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
>
> **Вопрос:** Как правильно реализовать presence service для 500M пользователей с heartbeat-механизмом и pub/sub нотификациями?
>
> ---
>
> #### A) PostgreSQL с UPDATE user.is_online = true при connect и =false при disconnect — ACID гарантия — ❌ Неверно
>
> **Что на самом деле:** presence — это **ephemeral**, high-frequency data: каждые 30 секунд × 500M users = 16.6M UPDATE/min. PostgreSQL получает WAL overflow, vacuum-storm, MVCC table bloat. И ACID для «онлайн/оффлайн» не критичен — это soft data, не транзакция. Redis с TTL — natural fit: SET с EX=60s, отсутствие записи = offline.
>
> **Откуда путаница:** PostgreSQL — default reach DB; tempting use «one DB for all». ACID — credibility-стандарт. Но trade-off latency/throughput здесь критичен.
>
> **Если бы это было правдой:** vacuum никогда не догонял бы; index bloat; replication lag; presence-flicker. Slack/WhatsApp использовали бы PostgreSQL для presence — но они выбрали Redis/Mnesia.
>
> ---
>
> #### B) Redis с `SET user:X "online" EX 60` + heartbeat каждые 30s + pub/sub `presence.changed` для подписчиков-друзей — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Redis даёт sub-ms latency для presence-lookups и встроенный TTL для auto-expiration. Heartbeat-протокол: client отправляет ping каждые 30s через WebSocket → server делает `SET user:X "online" EX 60` (renews TTL). Если client отключается без graceful close, TTL истекает через 60s → key исчезает → считается offline. Pub/sub канал `presence.changed:X` нотифицирует subscribers (например, друзей user X) о смене статуса; их WS-серверы пушат event клиентам для real-time UI-update.
>
> **Пример:**
> ```text
> # Connect
> WS server S2 ← client X connect
> S2: SET presence:X "{server:S2, status:online}" EX 60
> S2: PUBLISH presence.changed:X "online"
>
> # Heartbeat loop (every 30s while connected)
> S2: SET presence:X "{server:S2, status:online}" EX 60   # renew TTL
>
> # Disconnect (graceful)
> S2: DEL presence:X
> S2: PUBLISH presence.changed:X "offline"
>
> # Disconnect (ungraceful, network drop)
> # ничего не происходит; TTL истекает через ≤ 60s
> # Subscribers получат timeout-detection через separate "stale" job:
>
> # Lookup (другой сервер хочет знать)
> S1: GET presence:X
>   → "{server:S2, status:online}"  # or nil → offline
>
> # Friends subscribers
> S2: SUBSCRIBE presence.changed:friends_of_X  # на friend connect
>   → push event to X's WebSocket: "friend Y is now online"
> ```
>
> **Когда применять:**
> - Любая presence/heartbeat-система: chat, gaming, collaborative tools (Figma, Notion).
> - Когда latency lookup критичен (< 5ms): friends list, typing-target, group online count.
> - Когда eventual consistency приемлема (10-60s TTL drift) — не банковский баланс.
>
> **Подводные камни:**
> - TTL должен быть > heartbeat interval (60s > 30s) с буфером для GC/network spikes — иначе false-offline во время heartbeat in-flight.
> - Privacy: WhatsApp «hide last seen» — нужно скрывать presence из public API даже если хранится в Redis. Чтение через abstraction-layer.
> - Redis-cluster sharding by user_id hash; pub/sub каналы должны быть на одной shard как key (либо использовать Redis Streams для cross-shard).
> - Heartbeat traffic: 500M × 1 ping/30s × 50B = 800MB/s gateway-traffic — нужно учитывать в capacity planning.
>
> **Связанные вопросы:** [[Q4]] — presence в connection routing; [[Q5]] — broker-routed delivery использует presence для lookup; [[Q16]] — typing indicators похожий fire-and-forget pattern.
>
> ---
>
> #### C) Хранить presence только локально в WS-сервере — каждый сервер знает только своих connected users — ❌ Неверно
>
> **Что на самом деле:** локальный presence на WS-сервере не доступен другим серверам. Когда user Y на server S1 хочет знать, online ли его друг X (на server S2), без centralized presence registry нужно broadcast'ить запрос — N×N traffic.
>
> **Откуда путаница:** локальная state кажется проще, без centralized dependency. Но для cross-server presence нужен shared store.
>
> **Если бы это было правдой:** «друг online или нет?» требовал бы broadcast на 10K серверов; presence list для группы 100 человек = 100 lookups через broadcast — невозможно.
>
> ---
>
> #### D) WebSocket heartbeat каждую секунду — иначе presence неточный — ❌ Неверно
>
> **Что на самом деле:** 1 ping/sec × 500M users = 500M req/sec в presence-store — это в 30× больше, чем нужно. 30-second heartbeat достаточно для UX («online/offline» в чате не критичен на секунду). И мобильные клиенты экономят батарею, поэтому ping частый недопустим.
>
> **Откуда путаница:** «чем чаще ping, тем точнее presence» — true; но trade-off с battery, network, store-load. Industry standard = 25-45 секунд.
>
> **Если бы это было правдой:** WhatsApp на мобильном жрал бы батарею за 2 часа; presence-Redis получал бы 500M ops/sec — нужно 1000+ shards.

## Q16. Typing indicators?

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
>
> **Вопрос:** Typing indicators в чате: какие требования к persistence, rate limit и broker и какое реализационное решение правильное?
>
> ---
>
> #### A) Хранить typing events в Cassandra с TTL=5s — для аудита и потенциальной аналитики — ❌ Неверно
>
> **Что на самом деле:** typing — ephemeral signal, ценность исключительно в моменте рендеринга. Persistence создаёт overhead: 500M users × keystrokes/sec = миллиарды writes/sec ради 5-секундной ценности. И TTL-storm в Cassandra (massive tombstones) ломает compaction.
>
> **Откуда путаница:** «давайте всё хранить, может пригодится» — distractor для junior. Real chat не хранит typing.
>
> **Если бы это было правдой:** Cassandra compaction никогда не догоняет; storage растёт линейно с typing-trafic; стоимость инфры — миллионы $/месяц за бесполезные данные.
>
> ---
>
> #### B) Broadcast каждое keystroke через Kafka durable topic — гарантированная доставка typing-events — ❌ Неверно
>
> **Что на самом деле:** Kafka — durable, ~10-100ms latency, требует persistence. Typing-indicator имеет UX-tolerance 1-2 секунды: «отображается с задержкой 100ms» = ощущается как лаг. И keystroke-per-event = безумный трафик: 100 wpm × 500M concurrent typers × 5 keys = 250M events/sec на Kafka — не масштабируется.
>
> **Откуда путаница:** Kafka — default modern broker; «давайте всё через Kafka». Для typing-as-data это overkill.
>
> **Если бы это было правдой:** Kafka cluster на 1000+ brokers только для typing-events; storage 100PB/день эфемерных данных; latency убил бы UX.
>
> ---
>
> #### C) Throttled (1 event per 3-5s) fire-and-forget через Redis pub/sub, без persistence; client timeout hides indicator after 5s — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Typing — ephemeral signal без durability требований. Реализация: client debounces keystrokes (отправляет 1 event при первом keystroke, повторяет максимум каждые 3 секунды), сервер publishes в Redis pub/sub канал `typing:conversation:X`. Subscribers (другие WS-серверы с participant'ами этого чата) делают push в WebSocket. Client-side: при receive starts 5s timer; следующий typing-event сбрасывает timer; expiry → скрывает «typing». Нет persistence, потеря OK, latency микросекундная.
>
> **Пример:**
> ```text
> # Client A starts typing
> # Local debounce: 1st keystroke triggers immediately, then throttle 3s
> last_sent_at = now()
> if now() - last_typing_send > 3000ms:
>     ws.send({type: "typing_start", conversation_id: X})
>     last_typing_send = now()
>
> # Server side
> S1 receives typing_start
> S1: PUBLISH typing:conversation:X "{user: A, ts: now}"
>
> # Other servers with X-participants subscribed
> S2: SUBSCRIBE typing:conversation:X
> S2: ws.send(participant_B, {type: "typing", user: A})
> S2: ws.send(participant_C, {type: "typing", user: A})
>
> # Client B/C side
> on_receive_typing(user=A):
>     show("A is typing...")
>     clear_old_timer()
>     start_timer(5s, on_expire=hide_typing)
>
> # Rate limit на сервере (defense-in-depth):
> rate_limit_per_user = 1 event / 2s
> # Если abuse — drop excessive events silently
> ```
>
> **Когда применять:**
> - WhatsApp/Slack/Telegram typing indicators — fire-and-forget pattern.
> - Любые ephemeral signals: «X is recording voice», «Y is uploading file», «Z is online but idle».
> - Webrtc presence/availability signaling — тот же подход.
>
> **Подводные камни:**
> - Stop-event важен: client closes app без typing_stop → indicator висит 5s; нужна автоматическая expire на client-side.
> - Group chat большой (100+ участников): 100 typing events fan-out — допустим, но bigger groups (1000+) лучше throttle на сервере (один event per «X people typing» аггрегат).
> - Privacy: showing typing раскрывает activity user'а; настройка `disable typing visibility` нужна для privacy-focused users.
>
> **Связанные вопросы:** [[Q8]] — Redis pub/sub vs Kafka выбор брокера; [[Q15]] — presence — похожий ephemeral pattern с TTL; [[Q17]] — group chat fan-out для typing на участников.
>
> ---
>
> #### D) Server рассчитывает typing pattern из keystroke timing analytics и пушит computed «is_typing_smart» — ❌ Неверно
>
> **Что на самом деле:** over-engineering. UX problem простой: показать «X пишет», когда X нажал клавишу за последние 5 секунд. ML-вывод pattern не нужен; добавляет latency, computation cost, и opaque behavior («почему indicator пропал?»). Industry-стандарт — explicit start/stop events от клиента.
>
> **Откуда путаница:** «давайте используем ML где можно» — текущий тренд. Но для simple UX-feature это инженерный overkill.
>
> **Если бы это было правдой:** Slack/WhatsApp описывали бы typing-indicator как ML feature в blog — но они не описывают, потому что это просто event.

## Q17. Group chat design?

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
>
> **Вопрос:** Fan-out стратегии для group chat: какой подход правильно различает small (≤ 500) vs huge (10K+) groups и почему?
>
> ---
>
> #### A) Always write-time fan-out: при отправке писать копию каждому участнику в его inbox-table — ❌ Неверно
>
> **Что на самом деле:** write-amplification = N (members). Группа 10K = 10K INSERTs на каждое сообщение; channel Slack с 30K members + 100 msg/sec = 3M writes/sec только на этот channel. Storage = N × messages_volume — 30K member channel × 50TB messages = 1.5PB. Не масштабируется на large groups.
>
> **Откуда путаница:** outbox / fan-out-on-write — стандарт для feed-систем (Twitter, Facebook) с малым avg follower count. Для chat-системы с large channels иначе.
>
> **Если бы это было правдой:** Slack public channels с 100K subscribers (large company workspaces) убивали бы storage; latency для каждого write = max(N member INSERTs).
>
> ---
>
> #### B) Always read-time fan-out: writing один раз, recipient pull при open — ❌ Неверно
>
> **Что на самом деле:** read-time fan-out = polling. Recipient должен спрашивать «новые сообщения?» каждые N секунд. Для real-time UX это убивает latency: «увидел через 30s polling interval» вместо мгновенно. И scale: 500M users × poll/30s = 16M poll/sec на API — huge load.
>
> **Откуда путаница:** read-time fan-out экономит storage write-amplification, выглядит чистой архитектурой. Для feeds (Twitter timeline) это часто используется в hybrid. Для real-time chat — нет.
>
> **Если бы это было правдой:** WhatsApp/Telegram не могли бы быть real-time чатами; iMessage потерял бы свою core UX.
>
> ---
>
> #### C) Always WebSocket pub/sub channel per group без storage fan-out — recipients подписываются — ❌ Неверно
>
> **Что на самом деле:** pub/sub channel per group хорошо для realtime delivery, но без storage fan-out (или single canonical storage) есть проблема offline recipients: они должны pull history при reconnect. И «inbox view» — список всех чатов, сортированных по last_message_at — требует either fan-out или index table.
>
> **Откуда путаница:** Redis pub/sub удобен для broadcast — natural для group; но это только transport, не storage. Pure pub/sub теряет offline-delivery, dedup, history.
>
> **Если бы это было правдой:** offline пользователи не получали бы сообщений после reconnect; «непрочитанные» бы не работали.
>
> ---
>
> #### D) Hybrid: small groups (≤ 500) — write-time fan-out на канал каждого участника (real-time WS push); large groups/channels (10K+) — read-time fan-out с единым storage и broadcast Redis channel; ordering shared per group; group membership cached — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Реальные chat-системы выбирают fan-out стратегию по размеру группы. **Small groups** (1:1, family chat, work team): write-time — sender publishes to each member's channel; latency мгновенная, storage amplification приемлем (N ≤ 500). **Large groups / channels** (Slack #general 30K, Discord large server 500K): read-time — пишем один раз в group-level storage; online subscribers получают через broadcast Redis channel; offline pull history при open. Edge: real-world WhatsApp limits groups до 1024 members именно из-за fan-out cost; Slack channels не имеют hard limit но pull-based для read history.
>
> **Пример:**
> ```text
> # Small group (≤ 500 members) — write-time
> User A sends to group G (N=20)
> Persist: 1 row in messages (partition by conversation_id=G)
> Lookup members: [B, C, ..., T]  # cached
> For each member M_i in parallel:
>     PUBLISH user:M_i "msg"
> # Total: 1 storage write + N pub/sub publishes
>
> # Large group / channel (10K+ members)
> User A posts in channel C (30K members)
> Persist: 1 row in messages (partition by conversation_id=C)
> PUBLISH channel:C "new_message:msg_id"
> # All currently-connected subscribers receive notification
> # Offline members: don't broadcast — pull when they open
>
> # Channel read flow (lazy)
> User M opens channel C
> Client: GET /channel/C/messages?after=last_seen_msg_id
> Server: Cassandra range query partition=C, message_id > last_seen
>         Return up to 50 latest
> Client: Display, mark as read, update last_seen
>
> # Membership lookup caching
> Group service → Redis cache
> GET group:G:members → Set<UUID>  # SREMS на membership change
> If miss: PostgreSQL query + Redis populate (TTL 5 min)
> ```
>
> **Когда применять:**
> - WhatsApp groups (256-1024 members): write-time, в WhatsApp blog описано.
> - Slack channels (any size, но often huge): read-time + broadcast.
> - Discord servers (millions members): hybrid с lazy load + scroll-based history.
> - Facebook Messenger group threads: write-time для small (default 100).
>
> **Подводные камни:**
> - Membership cache invalidation: добавили человека в группу → нужен немедленный invalidate Redis cache; race с in-flight send может дать «не получил первое сообщение».
> - Threshold между small/large groups (например 500) — soft boundary; нужна smooth transition (admin opts in для большой группы).
> - Read-time fan-out требует более sophisticated offline-state: «непрочитанные счётчики» нужно хранить per-user-per-channel (Q13 statuses table).
> - Cross-region groups: members в US + EU + Asia → write-once but replicate to all regions для local-read latency.
>
> **Связанные вопросы:** [[Q9]] — partition by conversation_id для group storage; [[Q13]] — per-recipient status в группах; [[Q18]] — push notifications для offline group members.

## Q18. Push notifications для offline?

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
>
> **Вопрос:** Как правильно реализовать push notifications для offline users через APNs/FCM с дедупликацией и rate limit?
>
> ---
>
> #### A) Persist message → presence check (offline) → notification service вызывает APNs/FCM с device token → device wake → user opens app → WS connect → pull undelivered + dedup by message_id — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Push notifications — это **out-of-band** канал для offline-доставки. Pipeline: (1) сообщение всегда сначала persists в Cassandra (durable); (2) check presence — если recipient offline → send push; (3) Notification Service шлёт через APNs (iOS) или FCM (Android) с device-token; (4) device wakes, показывает banner; (5) user opens app → WebSocket connects → fetch pending messages из persistent queue → client deduplicates по message_id. Push содержит только preview (title + content snippet) + message_id; actual full message приходит через WS.
>
> **Пример:**
> ```text
> # Send flow с offline-fallback
> 1. User A: ws.send({message_id, content, recipient: B})
> 2. WS server S1: INSERT INTO messages (...) IF NOT EXISTS  # persist
> 3. S1: GET presence:B
>    → nil (offline) или {server:S5}
>
> # Branch: B online
> 4a. S1: PUBLISH user:B "msg"
> 4a. S5 (where B connected): WS push to B
>
> # Branch: B offline
> 4b. S1: INSERT INTO undelivered_queue (user_id=B, message_id, ts)
> 4b. S1: KAFKA → notification.events {user: B, msg_id, preview}
>
> 5b. Notification Service consumer:
>    GET device_tokens for B → ["apns:abc...", "fcm:xyz..."]
>    For each token:
>        POST APNs/FCM {
>            "token": "...",
>            "alert": {
>                "title": "Alice",
>                "body": "Hey, how are you?"
>            },
>            "custom_data": {"message_id": "..."}
>        }
>
> # Device wakes, shows notification
> # User taps → app opens
>
> 6. Client: WebSocket connect
> 7. Client: GET /undelivered?since=last_seen_id
>    → list of pending messages
> 8. Client: dedup by message_id (Set already-seen)
> 9. Client: ACK to server → remove from undelivered_queue
> ```
>
> **Когда применять:**
> - Все mobile-first messaging: WhatsApp, Telegram, Signal, iMessage, Messenger.
> - Любая система с async-доставкой на мобильное устройство.
> - Когда device может быть offline недели (старый телефон в столе) — нужен persistent queue + retention policy.
>
> **Подводные камни:**
> - APNs/FCM rate limits: 5-15K notifications/sec per app per provider; для 500M users в peak это узкое горлышко — нужна consolidation («5 new messages from Alice»).
> - Token rotation: APNs/FCM tokens expire (~30 days inactive); сервер должен handling 410 Gone и удалять stale tokens, иначе wasted traffic.
> - Silent push для иOS: тип notification, который wakes app без banner — используется для pre-fetch; iOS лимитирует количество в час.
> - Privacy: lock-screen preview настраивается user'ом; для sensitive chats показывать «You have new message» без content.
> - E2E encryption: push содержит только ciphertext-preview или generic «new message» — app decrypt'ит после open.
>
> **Связанные вопросы:** [[Q7]] — persist-first delivery pattern; [[Q15]] — presence check как branching point; [[Q19]] — E2E encryption и push preview privacy.
>
> ---
>
> #### B) Push notification = full message content в payload, no need to fetch via WS after open — ❌ Неверно
>
> **Что на самом деле:** APNs/FCM лимитируют payload до ~4KB; реальные сообщения (особенно с E2E ciphertext + attachments metadata) превышают. И privacy: lock-screen показывает только preview по design — full content неуместно. И E2E: server не имеет plaintext message, только ciphertext — recipient decrypt'ит после открытия приложения.
>
> **Откуда путаница:** «зачем второй round-trip — сразу полный текст?» — premature optimization. Реальные limits и privacy diktat'ят preview-only.
>
> **Если бы это было правдой:** sensitive messages (банковский OTP, intimate texts) появлялись бы полностью на lock-screen; E2E систему было бы невозможно построить.
>
> ---
>
> #### C) Skip persist, send push directly from WS-server — экономия storage write — ❌ Неверно
>
> **Что на самом деле:** если push fails (APNs timeout, FCM down) и сообщение не persisted — оно потеряно. Persist-first гарантия: даже без push сообщение в storage, доставится при следующем reconnect.
>
> **Откуда путаница:** «зачем persist, если doзlвстерим через push?» — premature optimization. Push — best-effort transport, не replacement для persistent storage.
>
> **Если бы это было правдой:** при отказе APNs (известные incidents несколько раз в год) — сотни тысяч сообщений потеряны навсегда; «no message loss» NFR нарушается.
>
> ---
>
> #### D) Использовать только WebSocket reconnect для offline — push не нужен — ❌ Неверно
>
> **Что на самом деле:** WebSocket не работает на закрытом приложении. iOS/Android killing background connections для экономии battery; user должен open app, чтобы WS reconnect. Push — единственный способ wake-up the app для async notification.
>
> **Откуда путаница:** «WebSocket persistent» — true когда app в foreground; в background OS убивает.
>
> **Если бы это было правдой:** WhatsApp не работал бы с закрытым приложением — но он работает, потому что использует APNs/FCM.

## Q19. (!) End-to-end encryption?

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
>
> **Вопрос:** Signal Protocol для E2E encryption в чате: какие гарантии он даёт и что server видит/не видит?
>
> ---
>
> #### A) Signal Protocol = simple AES-GCM symmetric key shared между sender и recipient на всё время — ❌ Неверно
>
> **Что на самом деле:** static shared key не даёт **forward secrecy**. Если key компрометирован сегодня, attacker может decrypt все прошлые messages. Signal protocol использует **Double Ratchet**: новая key derived для каждого сообщения через DH ratchet (асимметричный) + chain ratchet (симметричный). Past messages safe даже если current key leaked.
>
> **Откуда путаница:** AES-GCM с shared key — стандарт для file encryption (Tink, libsodium); для chat кажется применимым. Но он не handles temporal compromise.
>
> **Если бы это было правдой:** компрометация устройства пользователя (украли телефон) → доступ ко всей истории, ко всем будущим сообщениям. Signal/WhatsApp специально проектировались избежать этого.
>
> ---
>
> #### B) Signal Protocol: X3DH key agreement при первой связи + Double Ratchet (DH + chain) per-message rotation; forward secrecy + post-compromise security; server видит только ciphertext + metadata (sender, recipient, timestamp, size) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Signal Protocol (Open Whisper Systems, 2013) — gold-standard для secure messaging. Состоит из двух фаз: (1) **X3DH** (Extended Triple Diffie-Hellman) — initial key agreement, обменивается identity keys + signed prekey + one-time prekeys через сервер; вычисляет shared root key. (2) **Double Ratchet** — derives new message key для каждого outgoing message: chain ratchet (HKDF из previous chain key) для consecutive messages + DH ratchet (новый DH-pair при receive нового incoming) для forward secrecy. **Forward secrecy**: даже compromise current key не раскрывает past; **post-compromise security** (self-healing): новый DH-ratchet восстанавливает confidentiality после compromise. Server хранит только ciphertext + envelope metadata.
>
> **Пример:**
> ```text
> # Phase 1: X3DH (one-time per pair)
>
> Alice generates: IK_A (identity), EK_A (ephemeral)
> Bob has uploaded к server: IK_B, SPK_B (signed prekey), OPK_B (one-time prekey)
>
> Alice fetches Bob's keys from server.
>
> Alice computes:
>     DH1 = DH(IK_A, SPK_B)
>     DH2 = DH(EK_A, IK_B)
>     DH3 = DH(EK_A, SPK_B)
>     DH4 = DH(EK_A, OPK_B)   # optional, increases security
>     SK = HKDF(DH1 || DH2 || DH3 || DH4)   # root secret
>
> Alice sends initial msg: {IK_A, EK_A, OPK_B_id, ciphertext(SK)} → server → Bob
>
> Bob computes same SK from his private keys.
>
> # Phase 2: Double Ratchet per-message
>
> For each outgoing message:
>     chain_key = HKDF(chain_key)   # chain ratchet
>     message_key = HKDF(chain_key, "MK")
>     ciphertext = AES-GCM(message_key, plaintext)
>     send {ciphertext, header}
>
> For each new DH-receive:
>     new_dh_pair = generate DH key
>     root_key, chain_key = HKDF(root_key, DH(new_dh, peer_dh))
>     # forward secrecy: old chain_key больше не выводится
>
> # Server's view
> {
>   "sender": "Alice",
>   "recipient": "Bob",
>   "timestamp": "2026-05-15T10:00:00Z",
>   "size": 348,
>   "ciphertext": "0x4f8a...",   # opaque blob
>   "header": "0x..."            # contains DH pub key, message counter
> }
> # Server cannot read content; routes by recipient.
> ```
>
> **Когда применять:**
> - Signal (canonical reference implementation).
> - WhatsApp (since 2016): all chats E2E using Signal protocol.
> - Facebook Messenger «secret conversations» (opt-in E2E).
> - iMessage (Apple's similar protocol with PKI).
> - Любая система где «server cannot read» — критическое требование.
>
> **Подводные камни:**
> - Group E2E: «Sender Keys» protocol — per-sender symmetric key, encrypted pairwise to each member; rekey при membership change (huge cost для больших групп).
> - Backup challenge: cloud-backup messages нужен encrypted blob с user-derived key; recovery без password = плохо для UX, известное trade-off (WhatsApp end-to-end encrypted backup введён 2021).
> - Metadata still visible: server видит who-talks-to-whom, when, how big; **traffic analysis** возможен. Signal mitigates через «sealed sender» feature.
> - Key verification: nobody verifies safety numbers — vulnerable to MITM (server подменяет prekey). UI «scan QR» rarely used.
> - Server-side search/sync невозможен: search history клиент должен делать локально (Q21).
>
> **Связанные вопросы:** [[Q18]] — push notifications и E2E preview privacy; [[Q20]] — media encryption: client-side encrypt before upload; [[Q21]] — search невозможен server-side при E2E.
>
> ---
>
> #### C) E2E = TLS между client и server, no need for application-level encryption — ❌ Неверно
>
> **Что на самом деле:** TLS — это **transport encryption** (server видит plaintext, который потом encrypted в transit). E2E — **application encryption**: server никогда не видит plaintext, только ciphertext. Это разные слои. TLS не защищает от server-compromise или government request.
>
> **Откуда путаница:** TLS — синоним «encryption in transit»; новички могут смешать E2E и TLS. Маркетинг некоторых сервисов нечестно использует «end-to-end» для TLS.
>
> **Если бы это было правдой:** правительственные запросы (subpoena) к Signal получали бы все сообщения — но Signal предоставляет минимум metadata, потому что **не имеет** plaintext.
>
> ---
>
> #### D) Server должен иметь backup-копию ключей шифрования для recovery при потере пароля — ❌ Неверно
>
> **Что на самом деле:** если server имеет ключи — это не E2E. По определению E2E значит «only endpoints have keys». «Key escrow» (server holds backup) — компромисс E2E, противоречие design. Modern E2E systems делают user-controlled backup (encrypted blob c user-password, hosted в S3/iCloud).
>
> **Откуда путаница:** «потерял password — потерял ключи — потерял chat» — реальная проблема UX. Tempting сделать server-side recovery. Но это убивает E2E security model.
>
> **Если бы это было правдой:** Signal не был бы E2E; в случае server compromise (legal subpoena, breach) — все сообщения восстанавливаемы через server keys. Это противоречит whitepaper Signal protocol.

## Q20. Media (images, video) handling?

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
>
> **Вопрос:** Как правильно реализовать media handling (photos, video) в чате на 500M+ пользователей с CDN и E2E encryption?
>
> ---
>
> #### A) Отправлять media inline через WebSocket вместе с текстом для атомарности — ❌ Неверно
>
> **Что на самом деле:** WebSocket frame размер: WhatsApp ограничивает ~64KB, fundamentally плохо для media (фото ~2-5MB, video > 50MB). Inline media через WS = saturate WS-tier bandwidth, latency spike, frame fragmentation, OOM на сервере. Реальная архитектура: media uploads idут directly client → S3/blob-store; в WS-сообщение кладётся только URL/reference.
>
> **Откуда путаница:** атомарность звучит логично («сообщение + фото вместе»). Но WS не оптимизирован для blob transfer; HTTP/S3 — да.
>
> **Если бы это было правдой:** 500M users × upload 100MB/day = 50PB через WS-tier = saturated bandwidth; WS-серверы стали бы proxy для blob, не для signaling.
>
> ---
>
> #### B) Хранить media base64-encoded прямо в Cassandra messages.content — единый storage — ❌ Неверно
>
> **Что на самом деле:** Cassandra warns при rows > 1MB (плохо для row cache, compaction, repair); base64 раздувает binary на 33%. Photos ~2-5MB → 3-7MB base64 → миллиарды rows × 5MB = exabyte-class storage в Cassandra (которая сделана для time-series, не blob).
>
> **Откуда путаница:** «давайте всё в одной БД» — операционная простота. Но Cassandra не designed для blob storage.
>
> **Если бы это было правдой:** Cassandra cluster size на 50× больше нужного; read amplification гигантский; compaction никогда не догоняет.
>
> ---
>
> #### C) Client requests presigned S3 URL → direct upload to S3 (bypassing chat-tier) → message содержит S3 reference + thumbnail meta → recipient downloads directly via CDN → E2E: client encrypts blob before upload, recipient decrypts after download — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Media flow разделён на signaling (chat-tier) и blob transfer (object store + CDN). Upload: (1) client запрашивает presigned PUT URL у backend; (2) client уплоадит directly в S3 (или GCS, R2) — chat-tier не проксирует bytes; (3) backend генерирует thumbnails async (Lambda + ffmpeg); (4) client посылает chat-message `{type: image, url: s3://..., thumb: s3://..., width: ..., height: ...}`. Download: recipient получает URL через chat → fetches directly from S3/CDN (CloudFront в front для cache + geo-distribution). E2E: client encrypts blob locally (random AES key) before upload; key передаётся в encrypted message; recipient downloads ciphertext, decrypts. CDN кэширует ciphertext — bonus: cache не утекает content.
>
> **Пример:**
> ```text
> # Upload flow
> 1. Client A: POST /upload-url
>    → response: {
>          "url": "https://s3.../uploads/uuid?signature=...",
>          "asset_id": "asset_xyz",
>          "expires_at": "..."
>      }
>
> 2. Client A (E2E): encrypt blob with random AES-256-GCM key
>    blob_ct = AES-GCM(file_bytes, media_key)
>
> 3. Client A: PUT https://s3.../uploads/uuid?signature=...
>    Content-Type: application/octet-stream
>    Body: blob_ct
>    → 200 OK
>
> 4. Backend: Lambda triggered on S3 PUT
>    - Generate thumbnails (only for non-E2E; for E2E — client uploads pre-encrypted thumbs)
>    - Mark asset_id as ready
>
> 5. Client A: WS send chat message
>    {
>      "type": "image",
>      "asset_id": "asset_xyz",
>      "media_key": E2E_encrypt(media_key, recipient_pubkey),
>      "width": 1920, "height": 1080,
>      "thumb_asset_id": "asset_thumb_xyz"
>    }
>
> 6. Client B receives message via WS
> 7. Client B: GET https://cdn.../asset_xyz
>    → CloudFront serves cached ciphertext
> 8. Client B: decrypt media_key with own privkey
> 9. Client B: AES-GCM-decrypt blob_ct → original file
>
> # S3 lifecycle policy
> Bucket: chat-media
>   - 0-30 days: Standard
>   - 30-90 days: Standard-IA (cheaper, slower retrieval)
>   - 90 days+: Glacier (very cold, manual restore)
>   - DELETE after N years (retention policy)
> ```
>
> **Когда применять:**
> - WhatsApp/iMessage media: S3-like blob storage + CDN + E2E.
> - Slack file uploads: presigned URLs к S3, тот же pattern.
> - Любой large-blob transfer: voice messages, video, documents.
> - WebRTC offer/answer signaling через chat + media bytes через P2P или TURN.
>
> **Подводные камни:**
> - Orphan media: если message-send fails после upload — asset в S3, но никто не reference; нужен cleanup job (delete orphans older than 7 days).
> - CDN cache invalidation: E2E ciphertext immutable — cache forever (вечный TTL); но если media-key компрометирован, нужно re-encrypt + re-upload + invalidate references.
> - Thumbnail generation в E2E невозможна server-side (server не видит plaintext) — client должен upload pre-generated thumbnails отдельно; или клиент делает on-device thumbnail при download.
> - Bandwidth costs: S3 egress dominates бюджет; CDN cache hit ratio критичен (target 90%+).
> - Privacy: presigned URLs истекают; не делай долгожителей (≤ 1 час).
>
> **Связанные вопросы:** [[Q9]] — message schema только с reference, не blob; [[Q19]] — E2E encryption применяется к media так же как к тексту; [[Q11]] — sharding не относится к media (S3 native distributed).
>
> ---
>
> #### D) Прокинуть media через chat-tier для unified encryption и access control — ❌ Неверно
>
> **Что на самом деле:** chat-tier (Spring/Erlang/Go services) не designed для streaming gigabytes — это application-tier с request/response, OOM при big bodies, thread starvation. Direct-to-S3 pattern существует именно для отгрузки blob traffic от application servers.
>
> **Откуда путаница:** «единый канал = unified auth/encryption» — academic clean. Реальность: разделение signaling и data — индустриальный стандарт.
>
> **Если бы это было правдой:** WhatsApp servers были бы saturated by media traffic; latency для signaling упала бы; OOM при больших файлах.

## Q21. Search in chat history?

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


> [!mcq]
>
> **Вопрос:** Search в chat history: какой подход правильно учитывает trade-off между E2E encryption и server-side full-text search?
>
> ---
>
> #### A) Client-side search через локальный SQLite FTS5 индекс для E2E систем; Elasticsearch для non-E2E (Slack, Messenger без secret chats); encrypted search index для E2E на исследовательской стадии — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Существует фундаментальный конфликт E2E encryption и server-side search: если server не имеет plaintext, он не может построить inverted index для full-text search. Реальность бьёт по двум сценариям. **E2E systems** (WhatsApp, Signal): search только client-side — клиент скачивает messages при первом open, строит local SQLite FTS5 index, query локально. Slow на больших историях (1M+ messages), но работает. **Non-E2E systems** (Slack, Messenger по умолчанию): server видит plaintext → server-side Elasticsearch index per user/channel; query через REST API. **Encrypted search** (Searchable Symmetric Encryption, SSE) — research area, частичные реализации (Tahoe-LAFS, MongoDB CSFLE), но в production не зрело для большого scale.
>
> **Пример:**
> ```text
> # E2E client-side (WhatsApp)
> # Client downloads messages, builds local index
>
> SQLite (on device):
>   CREATE VIRTUAL TABLE messages_fts USING fts5(
>     message_id UNINDEXED,
>     content,
>     sender,
>     conversation_id UNINDEXED
>   );
>
> # On message receive (after decrypt):
> INSERT INTO messages_fts VALUES (?, ?, ?, ?);
>
> # Search query:
> SELECT message_id, conversation_id, snippet(messages_fts, 1, '<b>', '</b>', '...', 32)
> FROM messages_fts
> WHERE messages_fts MATCH 'pizza OR sushi'
> ORDER BY rank
> LIMIT 50;
>
> # WhatsApp encrypted backup (iCloud/Drive)
> # On restore: decrypt blob → re-import messages → rebuild FTS index
>
> # Non-E2E (Slack)
> POST /search/messages?q=quarterly+report&channel=engineering
> # Server queries Elasticsearch:
> GET /messages/_search
> {
>   "query": {
>     "bool": {
>       "must": {"match": {"content": "quarterly report"}},
>       "filter": {"terms": {"channel_id": [..user's channels..]}}
>     }
>   }
> }
>
> # Index maintenance
> Kafka → ES indexer consumer
> consumer.on("message.created"):
>   ES.index({...message fields including content...})
> consumer.on("message.deleted"):
>   ES.delete(message_id)
> ```
>
> **Когда применять:**
> - WhatsApp, Signal, Threema: client-side search, encrypted backup.
> - Slack, Messenger (default), Discord: server-side Elasticsearch.
> - iMessage: hybrid — client-side primary, server-side для cross-device sync.
> - Hybrid E2E + searchable encryption — research (Encrypted Search by Apple Knowledge Search, in progress).
>
> **Подводные камни:**
> - Client-side FTS index растёт линейно с history: 1M messages × 200B avg = 200MB local index — для старых телефонов проблема storage.
> - Cross-device sync для E2E search: каждое устройство строит свой index независимо — может различаться (incomplete sync).
> - Elasticsearch eventual consistency: search-результат может не показать сообщение, отправленное 100ms назад (index lag).
> - Privacy в non-E2E: company admin может search все channels — это feature Slack Enterprise, но обсуждается с employees.
> - Multi-language search: stemming, tokenization для рус/кит/ара — нужны специфические analyzers в ES.
>
> **Связанные вопросы:** [[Q9]] — message storage в Cassandra не имеет full-text index by default; [[Q10]] — Elasticsearch добавляется как отдельный storage в polyglot setup; [[Q19]] — E2E encryption блокирует server-side search.
>
> ---
>
> #### B) Server-side Elasticsearch index для всех чатов независимо от encryption — ❌ Неверно
>
> **Что на самом деле:** для E2E систем сервер физически не имеет plaintext — ничего не может проиндексировать кроме metadata (sender, recipient, timestamp). Elasticsearch требует tokenized text для inverted index.
>
> **Откуда путаница:** Elasticsearch — default решение для search; «давайте ES для всего». Но E2E принципиально не совместима с server-side text index.
>
> **Если бы это было правдой:** Signal/WhatsApp хвалились бы server-side search — но они не делают, потому что это нарушает E2E. WhatsApp explicit гарантирует: «We can't read your messages.»
>
> ---
>
> #### C) Сделать E2E «слабую» — server держит ключи для возможности search — ❌ Неверно
>
> **Что на самом деле:** если server имеет ключи decrypt, это не E2E (см. Q19). «Слабая E2E» — оксюморон; либо есть, либо нет. Сильные claims (WhatsApp, Signal) о неосведомлённости сервера legally significant — subpoena получит только metadata.
>
> **Откуда путаница:** UX-проблема client-side search (slow, big index) — реальна. Tempting compromise. Но business model и user-trust требует чёткой границы.
>
> **Если бы это было правдой:** Signal/WhatsApp могли бы делать server search — но они не делают, и это conscious design choice ради trust.
>
> ---
>
> #### D) Не предоставлять search в E2E чатах — это нерешаемая проблема — ❌ Неверно
>
> **Что на самом деле:** WhatsApp/Signal предоставляют search; он работает client-side. Это медленнее, чем server-side, но usable для большинства scenarios.
>
> **Откуда путаница:** academic «E2E + search невозможно» = строгая trade-off без compromise. Но client-side — рабочий compromise.
>
> **Если бы это было правдой:** не работало бы «найти сообщение» в WhatsApp — но работает; пользователи активно пользуются.

---

## See also

- [System Design](system-design-interview.md) — general principles
- [Design Feed System](design-feed-system-interview.md) — similar fan-out patterns
- [[websockets-interview|WebSockets]] — transport layer
- [[messaging-interview|Messaging]] — Kafka, brokers
- [Cassandra](../databases/cassandra-interview.md) — message storage
- [Redis](../databases/redis-interview.md) — presence, pub/sub
- [Distributed Systems](../architecture/distributed-systems-interview.md) — ordering, consistency
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — horizontal scaling
- [Load Balancing](../architecture/load-balancing-interview.md) — sticky sessions
- [Caching](../architecture/caching-strategies-interview.md) — presence, contact info
