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

`Chat System` (WhatsApp, Slack, Telegram, Messenger) — популярная задача на system design. Ключевые темы: **stateful-соединения** (WebSockets), упорядочивание (ordering), гарантии доставки, масштаб. Обсуждаются presence, групповые чаты, шифрование, хранение, push-уведомления.

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

**Функциональные:**
- Переписка 1:1
- Групповой чат (обычно 10–100 человек, до 500)
- Статус online/offline (presence)
- Доставка сообщений (real-time если online, push если offline)
- Read receipts (отметки о прочтении)
- История сообщений
- Индикаторы набора текста (typing indicators)
- Обмен медиа (фото, файлы)

**Нефункциональные:**
- **Низкая задержка** (доставка сообщения < 500 мс)
- **Высокая надёжность** (сообщения не теряются)
- **Доступность** (99.99%)
- **Масштабируемость** (миллиарды пользователей — у WhatsApp 2B+)
- **Упорядочивание** (сообщения в правильной последовательности)
- **Шифрование** (E2E для приватности)

## Q2. (!) Capacity estimation?

**Допущения:**
- 1B активных пользователей (масштаб WhatsApp)
- В среднем 50 сообщений на пользователя в день
- 500M одновременно online (пик)

**Сообщения:**
- 1B × 50 = **50B сообщений/день**
- 50B / 86400 = **580K msg/sec в среднем**
- Пик ~2M msg/sec

**Хранилище:**
- 100B на сообщение (текст + метаданные)
- 50B × 100B = **5 TB/день**
- 5 лет → 9 PB
- С медиа: в 10–100 раз больше

**Пропускная способность:**
- Среднее: 50K/s × 1KB = 50 MB/s
- Пик: 200 MB/s — скромно

**Число соединений:**
- 500M одновременных WebSocket-соединений
- ~1M соединений на сервер (после тюнинга) → 500 серверов для connection-яруса
- Консервативнее: 50K на сервер → 10K серверов

**Память:**
- Presence: карта user_id → server_id → 1B × 16B = 16GB
- Помещается в Redis

## Q3. (!) WebSockets vs long polling vs SSE?

**WebSockets:** двунаправленное постоянное соединение.
- **Лучший выбор** для чата (сообщения и туда, и обратно)
- Одно TCP-соединение
- Низкие накладные расходы

**Long polling:** клиент делает запрос, сервер удерживает его до наступления события.
- Запасной вариант для окружений, где WS заблокирован
- Выше накладные расходы (HTTP-оверхед на каждое событие)

**SSE (Server-Sent Events):** односторонний канал server → client.
- Не двунаправленный (для отправки нужен отдельный POST)
- Работает по HTTP/2 для масштаба

**Push (mobile):** APNs (iOS), FCM (Android) для уведомлений в offline.
- Будят устройство, показывают уведомление
- Само приложение забирает реальные сообщения при открытии

**Гибрид (как в реальности):**
- Основной канал: WebSocket, пока приложение активно
- Запасной: polling там, где WS не работает
- Offline: push-уведомление → при следующем открытии происходит синхронизация

**Обработка разрывов:**
- Heartbeat (ping) каждые 30 с
- Обнаружение мёртвого соединения → переподключение
- Экспоненциальный backoff

## Q4. (!) Connection routing и load balancing?

**Сложность:** WebSockets **stateful** — соединение пользователя живёт на одном конкретном сервере. Сообщения для пользователя X нужно маршрутизировать именно на этот сервер.

**Архитектура:**

```
Client → LB → WebSocket server (holds connection)
                    ↓
              Message broker (Kafka/Redis pub-sub)
                    ↑
Other user's WebSocket server publishes message for X
```

**Маршрутизация:**
- Пользователь X подключается → назначается на WS-сервер S1
- Presence-сервис: user_id:X → server:S1 (Redis)
- Когда Y пишет X:
  - Сообщение приходит на WS-сервер пользователя Y
  - Публикуется в канал либо ищется через presence
  - Сервер X пушит сообщение в WebSocket пользователя X

**Балансировка нагрузки:**
- Consistent hashing (user_id → server) — sticky, но масштабируемо
- Либо round-robin + центральная маршрутизация через брокер

**Лимиты соединений:**
- 500K–1M одновременных на сервер (после тюнинга)
- Горизонтальное масштабирование

**Failover:**
- Сервер падает → WebSockets рвутся
- Клиенты переподключаются → маршрутизируются на новый сервер
- Обновляется реестр presence

## Q5. Sticky session проблема?

**Проблема:** пользователь переподключается → должен попасть на тот же сервер? Или подойдёт любой?

**Вариант A: sticky (consistent hash):**
- Пользователь → сервер по `hash(user_id) % N`
- Тот же сервер при переподключениях
- Плюс: предсказуемо
- Минус: ребалансировка при масштабировании (сервер пользователя меняется)

**Вариант B: любой сервер (маршрутизация через брокер):**
- Пользователь → любой сервер (round-robin)
- Сообщения для пользователя маршрутизируются через брокер (pub-sub на канале пользователя)
- Плюс: гибкое масштабирование
- Минус: лишний hop на каждое сообщение

**Как в реальности:** вариант B встречается чаще (Slack, Discord) — он гибче.

**Обновление presence при переподключении:**
```
On connect: SET user:X server:S2
On disconnect: DEL user:X
```

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

**Компоненты:**

- **WS Gateway:** терминирует WebSocket; управляет жизненным циклом соединения
- **Chat Service:** запись и выборка сообщений
- **Presence Service:** статус online
- **User Service:** профиль, аутентификация
- **Group Service:** управление группами
- **Notification Service:** отправка push
- **Search Service:** Elasticsearch для поиска по истории

## Q7. (!) Message delivery flow?

**Чат 1:1, оба online:**

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

**Задержка:** обычно < 200 мс end-to-end.

**Доставка в offline:**
- Presence: B offline → не маршрутизируем через WS
- Пишем в очередь сообщений пользователя B (DB / Redis)
- Триггерим push-уведомление (FCM/APNs)
- B открывает приложение → WS-соединение → забирает недоставленные сообщения

**Устойчивость:**
- ACK на каждом hop
- Брокер сообщений делает retry при сбое
- Сначала персист, потом push — если push упал, сообщение всё равно в хранилище

## Q8. Message broker между серверами?

**Зачем брокер:**
- Развязывает сервер отправителя и сервер получателя
- Масштабируемость (публикуем, а не ищем напрямую)
- Retry при временном сбое

**Варианты:**

**Redis pub/sub:**
- Очень быстро (микросекунды)
- Fire-and-forget (без персистентности)
- Хорошо для presence + сигналинга
- Доставка не гарантируется (если подписчик на мгновение отвалился)

**Kafka:**
- Durable (возможен replay)
- Выше задержка (10–100 мс)
- Лучше для асинхронных пайплайнов (аналитика, уведомления)

**NATS:**
- Нечто среднее (быстро + at-least-once)

**Гибрид:**
- Redis pub/sub для real-time сигналинга
- Kafka для персистентности + аналитики

**Паттерн:**
```
Each user has a channel: user:{id}
S1 publishes: PUBLISH user:X "msg"
S3 (where X connected) SUBSCRIBE user:X → push to X's WS
```

## Q9. (!) Schema для messages?

**Простая схема (Cassandra):**

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

- Partition: conversation — все сообщения на одной node
- Cluster: time → эффективные range-запросы (свежие сообщения)

**Беседы (conversations):**
```sql
CREATE TABLE conversations (
    conversation_id UUID,
    participant_ids SET<UUID>,
    type TEXT,  -- 'direct' or 'group'
    created_at TIMESTAMP,
    last_message_id TIMEUUID
);
```

**Индекс «пользователь → беседы»:**
```sql
CREATE TABLE user_conversations (
    user_id UUID,
    last_message_at TIMESTAMP,
    conversation_id UUID,
    PRIMARY KEY (user_id, last_message_at, conversation_id)
) WITH CLUSTERING ORDER BY (last_message_at DESC);
```

Позволяет «вывести список бесед по недавней активности».

**Сообщения не индексируются по content в операционной БД** — для этого отдельный Elasticsearch.

## Q10. (!) SQL vs NoSQL для chat?

**Сообщения чата:**
- **Write-heavy** (каждое сообщение — запись)
- Доступ как к **time-series** (свежие сообщения)
- Идеально **партиционировать по беседе**

→ **NoSQL (Cassandra, DynamoDB) подходит:**
- Горизонтальное масштабирование
- Высокая пропускная способность на запись
- Низкая задержка при масштабе

**Данные пользователя (профиль, аутентификация):**
- Реляционные (users, friends)
- Важна согласованность
- **SQL** подходит (Postgres)

**Presence:**
- Эфемерные, маленькие
- **Redis** (in-memory)

**Членство в группах:**
- Небольшие реляционные данные
- SQL или document-БД

**Как в реальности:**
- WhatsApp: своё решение (Erlang + Mnesia)
- Messenger: HBase
- Slack: MySQL (шардированный)

## Q11. Shard strategy?

**Сообщения шардируются по conversation_id:**
- Все сообщения одной беседы → 1 partition
- Чтения: одна node
- Записи: туда же
- Горячая беседа = горячий шард (для обычного использования редкость; чат знаменитости — может случиться)

**Пользователи/группы шардируются по user_id.**

**Масштабирование:**
- Token ring в Cassandra справляется
- Virtual nodes для равномерности

**Кросс-шардовые запросы (например, поиск по всем чатам пользователя):**
- Fan-out: запрос ко всем релевантным шардам
- Либо предвычисленный timeline

**Tombstones в Cassandra:**
- Удалённые сообщения → tombstones
- Хранение по TTL (держим 90 дней, авто-истечение)

## Q12. (!) At-most-once, at-least-once, exactly-once?

**At-most-once:**
- Отправили, при сбое не повторяем
- Сообщения могут теряться
- Для чата неприемлемо

**At-least-once:**
- Отправили, повторяем до получения ACK
- **Возможны дубликаты** (потерян ACK → retry)
- Типично для чата

**Exactly-once:**
- Идемпотентность + дедупликация
- Сложнее, но достижимо

**Реализация в чате:**
- Доставка **at-least-once**
- **Идемпотентная обработка** (дедупликация по message_id):
  - Клиент генерирует UUID на каждое сообщение
  - Сервер: `INSERT IF NOT EXISTS` (ключ идемпотентности)
  - Повтор-дубликат → no-op

**Итог:** фактически exactly-once.

**End-to-end ACK:**
- Клиент → сервер: сообщение с ID
- Сервер сохраняет, отвечает ACK
- Сервер доставляет получателю
- Получатель шлёт ACK → отправитель видит «доставлено»

## Q13. (!) Read receipts и delivery receipts?

**События:**
- **Sent:** сообщение дошло до сервера (одна галочка ✓)
- **Delivered:** устройство получателя приняло (две галочки ✓✓)
- **Read:** получатель открыл чат (синие галочки ✓✓)

**Реализация:**
- Поле статуса на каждое сообщение + получателя
- Обновления возвращаются отправителю

**Для групп:**
- Статус на каждого получателя
- UI отправителя показывает: прочитано N из M

**Приватность:**
- Пользователи могут отключить read receipts (двусторонне)

**Хранение:**
```
message_status: (message_id, user_id, status, timestamp)
```

**Распространение обновлений:**
- Асинхронно (не блокирует основную доставку)
- Пакетные обновления (прочитали 10 сообщений разом)

**Масштаб:**
- Сообщения × участники = кратно больше записей статусов
- Группа из 100 человек → 100× статусов на сообщение

## Q14. Ordering guarantees?

**Упорядочивание в рамках беседы:**
- Сообщения одного чата должны быть упорядочены одинаково для всех участников
- Используем timestamps + tiebreaker (message_id UUID)
- Cassandra TIMEUUID: сортируемый + уникальный

**Clock skew:** у разных устройств разные часы.
- Авторитетный timestamp — серверный (момент получения)
- Клиент показывает локальную оценку

**Приход не по порядку:**
- Клиент получает сообщение M2 раньше M1 (сеть)
- Клиент пересортировывает по timestamp

**Глобальное упорядочивание:** не нужно; достаточно в рамках беседы.

**Групповой чат:**
- Общая последовательность (partition по conversation_id)
- Все видят один и тот же порядок

## Q15. (!) Online status / presence?

**Данные:**
- user_id → status (online/away/offline) + last_seen

**Хранение:**
- Redis (эфемерно)
- `SET user:X "online" EX 60`
- TTL 60 с — heartbeat поддерживает запись живой
- TTL истёк → считаем offline

**Heartbeat:**
- Клиент шлёт ping каждые 30 с по WebSocket
- Сервер продлевает TTL в Redis

**Уведомления об изменениях:**
- X стал online/offline → событие в pub/sub
- Друзья подписаны → UI обновляется

**Масштаб:**
- 500M пользователей × несколько байт = несколько GB в Redis
- Шардировано по user_id

**Приватность:**
- Пользователи могут скрыть статус online
- WhatsApp: настройка видимости last seen

**Away:**
- Нет активности 5 минут → «away»
- Клиент сообщает об активности (движение мыши, набор текста)

## Q16. Typing indicators?

**Событие:**
- Пользователь печатает → эфемерное событие участникам беседы
- Не персистится (отбрасывается через несколько секунд)

**Реализация:**
- WebSocket-событие `typing`
- Рассылка участникам чата (через брокер)
- Клиент показывает «John is typing...»
- Таймаут: если нет продолжения в течение 5 с → скрываем

**Ограничение частоты:**
- Не слать событие на каждое нажатие клавиши
- Throttle: максимум одно событие в 3 с

**Брокер:**
- Fire-and-forget (Redis pub/sub)
- Без персистентности; потерять не страшно

## Q17. Group chat design?

**Сложности сверх 1:1:**
- 100 получателей → 100× fan-out
- Управление списком участников
- Общее упорядочивание

**Подходы к fan-out:**

**1. Fan-out на запись (write-time):**
- Сервер отправителя публикует во все каналы участников группы
- Сервер каждого участника пушит через WS
- Хорошо работает при < 1000 участников

**2. Fan-out на чтение (read-time, pull):**
- Записываем сообщение один раз (хранилище на уровне группы)
- Получатели подтягивают при опросе / открытии
- Подходит для больших групп (10k+)

**3. Гибрид:**
- Маленькие группы: push
- Большие группы: pull (или broadcast-канал)

**WhatsApp:**
- Маленькие группы (256): сервер делает fan-out
- Крупнее («broadcast lists»): другая модель

**Каналы Slack:**
- Могут насчитывать тысячи участников
- Канал = topic в брокере; подписчики получают fan-out на краю (at edge)

**Членство:**
- Group-сервис управляет добавлением/удалением
- Кэшируем список участников группы

## Q18. Push notifications для offline?

**Обнаружен offline:** presence показывает offline → шлём push.

**Поток:**
1. Сообщение сохранено
2. Запрос presence: offline
3. Notification-сервис → APNs (iOS) / FCM (Android)
4. Провайдер доставляет на устройство
5. Устройство просыпается, показывает уведомление
6. Пользователь открывает приложение → WebSocket подключается → забирает ожидающие сообщения

**APNs / FCM:**
- Приложение должно зарегистрировать токен в push-сервисе
- Токен хранится на каждое устройство
- Сервер шлёт POST в APNs/FCM с токеном + payload

**Дедупликация:**
- Если пользователь переподключился до прихода push → получит и push, и WS-сообщение
- Клиент дедуплицирует по message_id

**Содержимое:**
- Имя отправителя + превью
- Приватность: превью на lock screen в iOS можно переключать

**Silent push:**
- Фоновое уведомление iOS (только в фоне)
- Ненадолго будит приложение для подгрузки; без баннера

**Лимиты частоты:**
- APNs/FCM троттлят по приложению
- Консолидация: один push «5 новых сообщений» вместо 5 пушей

## Q19. (!) End-to-end encryption?

**E2E:** прочитать могут только отправитель + получатель; сервер не может.

**Signal Protocol** (WhatsApp, Signal, опционально Messenger):

**Ключевые понятия:**
- У каждого пользователя: долговременный identity key + эфемерные ключи
- Сообщение шифруется **посессионным** ключом
- **Forward secrecy:** ключ ротируется на каждое сообщение; прошлые сообщения в безопасности, даже если текущий ключ скомпрометирован
- **Deniability:** нельзя доказать, кто отправил (нет подписи долговременным ключом)

**Поток:**
1. Пользователь A регистрируется: загружает публичные ключи (identity + pre-keys) на сервер
2. A хочет написать B: забирает публичные ключи B
3. A выполняет согласование ключей X3DH → сессионные ключи
4. Сообщения шифруются AES-GCM + аутентифицируются
5. Сервер видит только ciphertext

**Групповое E2E:**
- Протокол «Sender Keys»: симметричный ключ на каждого отправителя, зашифрованный попарно для участников
- Rekey при изменении состава

**Компромиссы:**
- **Сервер не может:** искать по content, считать статистику, бэкапить сообщения напрямую
- **Бэкап на стороне клиента:** зашифрованный blob, ключ держит пользователь
- **Метаданные всё равно видны:** кто кому пишет, когда, какого размера

**WhatsApp:** E2E с 2016 года.
**Telegram:** E2E только в «секретных чатах»; обычные чаты шифруются на стороне сервера, но читаемы.
**Signal:** всё E2E.

## Q20. Media (images, video) handling?

**Не передаётся inline** (слишком большое):

**Поток загрузки:**
1. Клиент запрашивает у сервера presigned S3 URL
2. Клиент загружает напрямую в S3
3. Клиент шлёт сообщение: `{type: "image", url: "s3://..."}`
4. Получатель скачивает напрямую из S3 (или через CDN перед ним)

**Преимущества:**
- Нет проксирования через чат-серверы (иначе они забьют пропускную способность)
- Кэш CDN
- Параллельные загрузки/скачивания

**Превью (thumbnails):**
- Генерируются на стороне сервера (Lambda, ffmpeg)
- Несколько размеров (thumb, medium, full)

**Сложности с E2E-шифрованием:**
- Файл шифруется на клиенте перед загрузкой
- Получатель скачивает зашифрованный blob и расшифровывает локально
- CDN кэширует ciphertext (полезной инспекции нет)

**Видеостриминг:**
- Обычно не как сообщения чата; выносится в отдельный сервис

**Хранение (retention):**
- S3 lifecycle policies
- Удаление через N лет

## Q21. Search in chat history?

**Сложность:** E2E-шифрование → сервер не может искать.

**Варианты:**

**1. Поиск на клиенте:**
- Скачиваем сообщения, индексируем локально (SQLite FTS)
- Подходит для небольшой-средней истории
- Медленно для очень длинной истории

**2. На стороне сервера (системы без E2E):**
- Индекс Elasticsearch
- Шарды на каждого пользователя
- Обновления в реальном времени из потока сообщений

**3. E2E + зашифрованный поиск:**
- Зашифрованные поисковые индексы (клиент генерирует зашифрованные токены)
- Сервер выполняет операции зашифрованного поиска
- Сложная криптография (в Signal полностью не реализовано)

**Slack / Messenger (по умолчанию не E2E):**
- Полнотекстовый поиск через Elasticsearch
- Шарды по каналам пользователя

**WhatsApp:**
- Только на клиенте (E2E)
- Функция «Chat backup»: зашифрованный blob в iCloud/Drive; пользователь может искать после скачивания

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
