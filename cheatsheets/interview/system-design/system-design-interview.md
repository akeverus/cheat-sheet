---
title: "Вопросы на собеседовании: System Design"
description: "Комплексное руководство по вопросам собеседования на тему System Design для Senior Java Developer. Включает детальные объяснения концепций, практические примеры, best practices и trade-offs."
tags: ["interview", "system-design", "system-design-interview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Вопросы на собеседовании: `System Design`

Комплексное руководство по вопросам собеседования на тему `System Design` для `Senior Java Developer`. Включает детальные объяснения концепций, практические примеры, best practices и `trade-offs`.

Дата последнего обновления: 2026-02-04

Краткое введение: комплексное руководство по вопросам собеседования на тему System Design для Senior Java Developer.

## Полезные ссылки

### Официальная документация

- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [AWS Architecture Center](https://aws.amazon.com/architecture/)
- [Google Cloud Architecture Framework](https://cloud.google.com/architecture/framework)
- "Designing Data-Intensive Applications" by Martin Kleppmann
- "System Design Interview" by Alex Xu
- "Building Microservices" by Sam Newman

### См. также

- [`../../architecture/README.md`](../../architecture/) — архитектурные паттерны
- [`../architecture/microservices-interview.md`](../architecture/microservices-interview.md) — микросервисы
- [`../architecture/scalability-patterns-interview.md`](../architecture/scalability-patterns-interview.md) — масштабирование

## Содержание

- [Полезные ссылки](#полезные-ссылки)

**Классические задачи System Design**
- [Q1. Как спроектировать URL Shortener (bit.ly)?](#q1-как-спроектировать-url-shortener-bitly)
- [Q2. Как спроектировать систему хранения файлов (Dropbox, Google Drive)?](#q2-как-спроектировать-систему-хранения-файлов-dropbox-google-drive)
- [Q3. Как спроектировать социальную сеть (Twitter, Instagram)?](#q3-как-спроектировать-социальную-сеть-twitter-instagram)
- [Q4. Как спроектировать систему чата (WhatsApp, Telegram)?](#q4-как-спроектировать-систему-чата-whatsapp-telegram)
- [Q5. Как спроектировать систему рекомендаций (Netflix, YouTube)?](#q5-как-спроектировать-систему-рекомендаций-netflix-youtube)

**Best Practices**
- [Q6. Best practices для System Design интервью](#q6-best-practices-для-system-design-интервью)

- [Q7. Как правильно собирать требования на System Design интервью?](#q7-как-правильно-собирать-требования-на-system-design-интервью)
- [Q8. Как делать capacity planning и оценки нагрузки?](#q8-как-делать-capacity-planning-и-оценки-нагрузки)
- [Q9. Как выбирать хранилище: SQL, NoSQL, search?](#q9-как-выбирать-хранилище-sql-nosql-search)
- [Q10. Как проектировать кэширование и CDN-слой?](#q10-как-проектировать-кэширование-и-cdn-слой)
- [Q11. Когда использовать очереди и event-driven подход?](#q11-когда-использовать-очереди-и-event-driven-подход)
- [Q12. Как обсуждать консистентность и CAP trade-offs?](#q12-как-обсуждать-консистентность-и-cap-trade-offs)
- [Q13. Какие аспекты безопасности обязательно покрывать в дизайне?](#q13-какие-аспекты-безопасности-обязательно-покрывать-в-дизайне)
- [Q14. Как спроектировать observability для системы?](#q14-как-спроектировать-observability-для-системы)
- [Q15. Как завершать решение и презентовать trade-offs интервьюеру?](#q15-как-завершать-решение-и-презентовать-trade-offs-интервьюеру)

## Q1. Как спроектировать `URL Shortener` (`bit.ly`)?

### Требования

Функциональные:
- Создание короткой ссылки из длинной `URL`
- Редирект по короткой ссылке на оригинальный `URL`
- Опционально: custom aliases, expiration, аналитика

Нефункциональные:
- Высокая доступность (99.99%)
- Низкая latency (<100ms)
- Масштабируемость (миллионы `URL` в день)

### Оценка масштаба

- 100M новых `URL` в месяц → ~40 `URL`/сек (write)
- `100 / 4000` redirects/сек
- Хранение: 100M × `500` bytes × 12 месяцев × 5 лет = ~3TB

### Высокоуровневый дизайн

Компоненты:
1. `API Gateway` — прием запросов
2. `Shorten Service` — генерация короткого `URL`
3. `Redirect Service` — редирект по короткому `URL`
4. `Database` — хранение маппинга (`shortURL` → `longURL`)
5. `Cache` (`Redis`) — кэширование популярных `URL`

### Генерация короткого `URL`

Подходы:
1. `Hash` (`MD5`, `SHA256`) — хеш от `longURL`; коллизии; длинный результат (нужно обрезать)
2. **Base62** encoding — `ID` из БД конвертировать в base62 (символы `a-z`, `A-Z`, `0-9`); 7 символов = 62^7 ≈ 3.5 трлн `URL`
3. `Distributed ID` generator (`Snowflake`) — уникальный `ID`, затем base62

Выбор: `Base62` с `auto-increment ID` или `Snowflake` для distributed setup.

### Схема БД

```sql
CREATE TABLE urls (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  short_url VARCHAR(10) UNIQUE NOT NULL,
  long_url TEXT NOT NULL,
  user_id BIGINT,
  created_at TIMESTAMP,
  expires_at TIMESTAMP,
  INDEX idx_short_url (short_url)
);
```

### `API`

`POST /api/shorten`
```json
Request: { "longUrl": "https://example.com/very/long/url", "customAlias": "mylink", "expiresIn": 3600 }
Response: { "shortUrl": "https://short.ly/abc123" }
```

`GET /shortUrl`
- Редирект `301` (permanent) или `302` (temporary)

### Масштабирование

- `Sharding` БД по hash(`shortURL`) или range (`ID` ranges)
- `Cache` (`Redis`) для популярных `URL`; `TTL` для expiration
- `CDN` для статики
- `Rate` limiting по `IP`/user

### `Trade-offs`

- `301 vs 302`: `301` кэшируется браузером (нет аналитики); `302` — каждый раз запрос к серверу
- `Custom` aliases: проверка уникальности; race conditions (optimistic locking)

## Q2. Как спроектировать систему хранения файлов (`Dropbox`, `Google Drive`)?

### Требования

Функциональные:
- `Upload`/download файлов
- Синхронизация между устройствами
- Шаринг файлов (permissions)
- Версионирование

Нефункциональные:
- Надёжность (no data loss)
- Доступность
- Масштабируемость (петабайты данных)
- Консистентность (eventual consistency `OK` для синхронизации)

### Оценка масштаба

- 100M пользователей
- 10GB на пользователя в среднем → 1 exabyte
- `Upload`:download = 1:3

### Высокоуровневый дизайн

Компоненты:
1. `Client` (desktop/mobile app)
2. `API Gateway`
3. `Metadata Service` — информация о файлах (name, size, path, permissions)
4. `Block Storage` — хранение файлов (`S3`, `Google Cloud Storage`)
5. `Sync Service` — синхронизация между устройствами
6. `Notification Service` — уведомления об изменениях

### Хранение файлов

`Chunking`:
- Разбивать файлы на блоки (4MB)
- Преимущества: дедупликация (одинаковые блоки), incremental sync (только изменённые блоки), параллельная загрузка

`Metadata`:
```json
{
  "fileId": "uuid",
  "name": "document.pdf",
  "size": 10485760,
  "chunks": ["chunk1_hash", "chunk2_hash", ...],
  "version": 3,
  "createdAt": "2026-01-29T10:00:00Z",
  "modifiedAt": "2026-01-29T12:00:00Z",
  "owner": "userId",
  "permissions": [{"userId": "user2", "role": "viewer"}]
}
```

### Синхронизация

Подходы:
1. `Polling` — клиент периодически спрашивает сервер об изменениях (неэффективно)
2. `Long` polling — клиент держит соединение открытым до изменений
3. `WebSocket` — двусторонняя связь; сервер пушит изменения клиенту

`Conflict` resolution:
- `Last-write-wins` (`LWW`) — простой, но может потерять данные
- Версионирование — сохранять обе версии; пользователь выбирает
- `Operational Transformation` (`OT / CRDT` — для collaborative editing

### Схема БД

`Metadata DB` (`PostgreSQL`):
```sql
CREATE TABLE files (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  path TEXT,
  size BIGINT,
  owner_id UUID,
  parent_folder_id UUID,
  created_at TIMESTAMP,
  modified_at TIMESTAMP,
  is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE chunks (
  hash VARCHAR(64) PRIMARY KEY,
  size INT,
  storage_location TEXT
);

CREATE TABLE file_chunks (
  file_id UUID,
  chunk_hash VARCHAR(64),
  chunk_order INT,
  PRIMARY KEY (file_id, chunk_order)
);

CREATE TABLE file_versions (
  id UUID PRIMARY KEY,
  file_id UUID,
  version INT,
  chunks JSONB,
  created_at TIMESTAMP
);
```

### Масштабирование

- `Metadata` sharding по `userId`
- `Block` storage — `S3` с репликацией
- `CDN` для популярных файлов
- `Cache` метаданных (`Redis`)

## Q3. Как спроектировать социальную сеть (`Twitter`, `Instagram`)?

### Требования

Функциональные:
- Публикация постов (текст, фото, видео)
- Подписки (follow/unfollow)
- Лента новостей (feed)
- Лайки, комментарии, репосты

Нефункциональные:
- Высокая доступность
- Низкая latency для feed (<200ms)
- Масштабируемость (миллиарды постов)
- `Eventual consistency` OK

### Оценка масштаба

- 1B пользователей
- 100M активных в день (`DAU`)
- 10 постов в день на активного → 1B постов/день
- `Feed / QPS`

### Высокоуровневый дизайн

Компоненты:
1. `Post Service` — создание/удаление постов
2. `Follow Service` — подписки
3. `Feed Service` — генерация ленты
4. `Timeline Service` — хранение timeline пользователя
5. `Notification Service` — уведомления
6. `Media Service` — хранение фото/видео

### Генерация `Feed`

Подходы:

1. `Fan-out` on `Write` (`Push`):
- При публикации поста добавить его в timeline всех подписчиков
- Преимущества: быстрое чтение feed (уже готов)
- Недостатки: медленная публикация для популярных пользователей (миллионы подписчиков); много записей

2. `Fan-out` on `Read` (`Pull`):
- При запросе feed собрать посты от всех подписок
- Преимущества: быстрая публикация
- Недостатки: медленное чтение feed (нужно собирать из многих источников)

3. `Hybrid`:
- `Fan-out` on write для обычных пользователей
- `Fan-out` on read для знаменитостей (>1M подписчиков)
- При запросе feed: готовый timeline + посты от знаменитостей

### Схема БД

`Posts`:
```sql
CREATE TABLE posts (
  id BIGINT PRIMARY KEY,
  user_id BIGINT,
  content TEXT,
  media_urls JSONB,
  created_at TIMESTAMP,
  INDEX idx_user_created (user_id, created_at DESC)
);
```

`Follows`:
```sql
CREATE TABLE follows (
  follower_id BIGINT,
  followee_id BIGINT,
  created_at TIMESTAMP,
  PRIMARY KEY (follower_id, followee_id),
  INDEX idx_followee (followee_id)
);
```

`Timeline` (`Cache` в `Redis`):

```text
Key: timeline:{userId}
Value: List of post IDs (sorted by timestamp)
```

### Масштабирование

- `Sharding` постов по `postId`; follows по `followerId`
- `Cache` (`Redis`) для timelines, популярных постов
- `CDN` для медиа
- `Message Queue` (`Kafka`) для асинхронной обработки `fan-out`

## Q4. Как спроектировать систему чата (`WhatsApp`, `Telegram`)?

### Требования

Функциональные:
- 1-`to-1` и групповые чаты
- Отправка текста, медиа
- Статусы доставки (sent, delivered, read)
- История сообщений
- Онлайн-статус

Нефункциональные:
- Низкая latency (<100ms)
- Высокая доступность
- Надёжность (no message loss)
- `End-to-end` encryption (опционально)

### Оценка масштаба

- 1B пользователей
- 100M активных одновременно
- 50 сообщений в день на пользователя → 5B сообщений/день

### Высокоуровневый дизайн

Компоненты:
1. `WebSocket Server` — `real-time` соединение с клиентами
2. `Message Service` — обработка сообщений
3. `Message DB` — хранение истории
4. `Presence Service` — онлайн-статус
5. `Push Notification Service` — уведомления для offline пользователей
6. `Media Service` — хранение фото/видео

### `Real-time` коммуникация

`WebSocket`:
- Клиент устанавливает `WebSocket` соединение с сервером
- Сервер держит mapping: `userId` → `WebSocket` connection
- При отправке сообщения: sender → `WebSocket Server` → receiver (если online)

Масштабирование `WebSocket`:
- Множество `WebSocket` серверов
- `Service Discovery` (`Consul`, etcd) для нахождения сервера пользователя
- `Message Queue` (`Kafka`) для маршрутизации сообщений между серверами

### Схема БД

`Messages`:
```sql
CREATE TABLE messages (
  id BIGINT PRIMARY KEY,
  chat_id BIGINT,
  sender_id BIGINT,
  content TEXT,
  media_url TEXT,
  created_at TIMESTAMP,
  INDEX idx_chat_created (chat_id, created_at DESC)
);

CREATE TABLE chats (
  id BIGINT PRIMARY KEY,
  type ENUM('direct', 'group'),
  created_at TIMESTAMP
);

CREATE TABLE chat_members (
  chat_id BIGINT,
  user_id BIGINT,
  joined_at TIMESTAMP,
  PRIMARY KEY (chat_id, user_id)
);
```

### Статусы доставки

Подходы:
1. `ACK` от сервера — сервер отправляет `ACK` отправителю при получении
2. `ACK` от получателя — получатель отправляет `ACK` при доставке/прочтении
3. Хранить статусы в БД или в памяти (`Redis`)

### Групповые чаты

`Fan-out`:
- При отправке сообщения в группу разослать всем участникам
- Для больших групп (>100 участников) — асинхронная обработка через `Message Queue`

### Масштабирование

- `Sharding` сообщений по `chatId`
- `Cache` (`Redis`) для недавних сообщений
- `CDN` для медиа
- `Horizontal scaling` `WebSocket` серверов

## Q5. Как спроектировать систему рекомендаций (`Netflix`, `YouTube`)?

### Требования

Функциональные:
- Персонализированные рекомендации контента
- Учёт истории просмотров, рейтингов
- `Real-time` и batch рекомендации

Нефункциональные:
- Точность рекомендаций
- Низкая latency (<200ms)
- Масштабируемость (миллиарды взаимодействий)

### Подходы к рекомендациям

1. `Content-Based Filtering`:
- Рекомендовать похожий контент на то, что пользователь смотрел
- Признаки: жанр, актёры, режиссёр, теги
- Алгоритм: cosine similarity, `TF-IDF`

2. `Collaborative Filtering`:
- Рекомендовать то, что нравится похожим пользователям
- `User-based`: найти похожих пользователей, рекомендовать их предпочтения
- `Item-based`: найти похожие items, рекомендовать их
- Алгоритм: `Matrix Factorization` (`SVD`, `ALS`), `KNN`

3. `Hybrid`:
- Комбинация `content-based` и collaborative
- `Deep Learning` (`Neural Collaborative Filtering`)

### Высокоуровневый дизайн

Компоненты:
1. `Data Collection` — сбор взаимодействий (views, ratings, clicks)
2. `Feature Engineering` — извлечение признаков (user features, item features)
3. `Model Training` — обучение моделей (batch, offline)
4. `Model Serving` — предсказание рекомендаций (online)
5. `Ranking` — ранжирование рекомендаций
6. A/B `Testing` — тестирование моделей

### Схема данных

`User` interactions:
```sql
CREATE TABLE interactions (
  user_id BIGINT,
  item_id BIGINT,
  interaction_type ENUM('view', 'like', 'rate'),
  rating FLOAT,
  timestamp TIMESTAMP,
  INDEX idx_user_time (user_id, timestamp DESC)
);
```

`Item` features:
```sql
CREATE TABLE items (
  id BIGINT PRIMARY KEY,
  title VARCHAR(255),
  genre VARCHAR(100),
  tags JSONB,
  embedding VECTOR(128) -- для similarity search
);
```

### `Pipeline`

`Offline` (`Batch`):
1. Собрать данные взаимодействий за период
2. Обучить модель (`Spark`, `TensorFlow`)
3. Сгенерировать рекомендации для всех пользователей
4. Сохранить в `Cache` (`Redis`) или БД

`Online` (`Real-time`):
1. При запросе пользователя получить рекомендации из `Cache`
2. Если нет — вычислить `on-the-fly` (fallback)
3. Ранжировать с учётом контекста (время суток, устройство)

### Масштабирование

- `Distributed` training (`Spark` MLlib, `TensorFlow Distributed`)
- `Approximate Nearest Neighbors` (`ANN`) для similarity search (`FAISS`, `Annoy`)
- `Cache` рекомендаций (`Redis`)
- `Sharding` данных по `userId`

### Метрики

- Precision`@K`, Recall`@K` — точность топ-K рекомендаций
- `CTR` (`Click-Through Rate`) — процент кликов
- `Engagement` — время просмотра, лайки

## Q6. Best practices для System Design интервью

### Подход к решению

1. `Clarify Requirements` (5 минут):
- Функциональные требования (что система должна делать)
- Нефункциональные требования (масштаб, latency, availability)
- `Constraints` (бюджет, технологии)

2. **Back-of-the-envelope Estimation** (5 минут):
- `QPS` (queries per second)
- `Storage` (сколько данных)
- `Bandwidth` (network throughput)

3. `High-Level Design` (10–15 минут):
- Основные компоненты (`API Gateway`, `Services`, `DB`, `Cache`)
- Диаграмма архитектуры
- `API` endpoints

4. `Deep Dive` (15–20 минут):
- Детали критичных компонентов
- Схема БД
- Алгоритмы (например, consistent hashing, sharding)
- `Trade-offs`

5. `Wrap` Up (5 минут):
- `Bottlenecks` и как их решить
- Мониторинг и алерты
- Что можно улучшить

### Ключевые концепции

Масштабирование:
- `Vertical` scaling (увеличение ресурсов сервера) — ограничено
- `Horizontal` scaling (добавление серверов) — предпочтительно
- `Load Balancer` (`Round Robin`, `Least Connections`, `Consistent Hashing`)
- `Sharding` (по `userId`, по hash, по range)
- `Replication` (`master-slave`, `multi-master`)

Кэширование:
- `Cache-aside` (lazy loading)
- `Write-through` (пишем в `cache` и `DB` одновременно)
- `Write-behind` (пишем в `cache`, асинхронно в `DB`)
- `Eviction` policies (`LRU`, `LFU`, `TTL`)

Консистентность:
- `Strong` consistency (все читают последнюю запись)
- `Eventual` consistency (со временем все узлы синхронизируются)
- `CAP` theorem (`Consistency`, `Availability`, `Partition` tolerance — выбрать 2 из 3)

Доступность:
- `Redundancy` (резервные компоненты)
- `Failover` (автоматическое переключение на резерв)
- `Health` checks и `Circuit` breaker

Паттерны:
- `CQRS` (`Command Query Responsibility Segregation`)
- `Event Sourcing`
- `Saga` (для distributed transactions)
- `API Gateway`
- `Service Mesh`

### `Trade-offs`

- `Latency` vs `Throughput` — оптимизация для одного может ухудшить другое
- `Consistency` vs `Availability` — `CAP` theorem
- `Normalization` vs `Denormalization` — нормализация экономит место, но медленнее; денормализация быстрее, но дублирование
- `SQL` vs `NoSQL` — `SQL` для `ACID`, `NoSQL` для масштабируемости
- `Sync` vs `Async` — синхронный проще, асинхронный масштабируемее

### Коммуникация

- Думать вслух — объяснять свои мысли
- Задавать вопросы — уточнять требования
- Рисовать диаграммы — визуализация помогает
- Обсуждать `trade-offs` — показывает глубину понимания
- Не бояться ошибок — интервьюер хочет видеть процесс мышления

### Типичные ошибки

- Сразу прыгать в детали без `high-level` дизайна
- Не уточнять требования
- Игнорировать масштаб (проектировать для `100` пользователей вместо 100M)
- Не обсуждать `trade-offs`
- Молчать — интервьюер не знает, о чём вы думаете


## Troubleshooting

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Медленные операции или таймауты | Неоптимальная конфигурация, нагрузка, сеть | Профилировать; проверить лимиты и настройки; документацию по производительности в начале документа |
| Ошибки подключения или недоступность | Неверная конфигурация, сеть, версия | Проверить host/port, credentials, совместимость версий; логи и мониторинг |
| Неожиданное поведение | Неверное использование API или формата | Сверить с официальной документацией; разделы Best Practices и примеры в документе |

## FAQ

**Когда использовать эту технологию?** См. раздел «Введение» или «Когда использовать» в начале документа; выбор зависит от сценария и требований проекта.

**Как настроить под production?** См. разделы по настройке, безопасности и best practices в документе; актуальные рекомендации — в официальной документации из блока «Полезные ссылки».

**Где искать актуальную документацию?** В блоке «Полезные ссылки» в начале документа указаны официальные источники и смежные разделы.


---

[⬆️ Наверх](../)

*Обновлено: 2026-01-29*

## Q7. Как правильно собирать требования на System Design интервью?

Первый шаг — уточнить функциональные требования и границы задачи: кто пользователи, какие ключевые сценарии, что обязательно в MVP, а что можно отложить. Без этого легко спроектировать «не ту» систему и потерять время на детали, которые не проверяются в интервью.

Параллельно нужно быстро зафиксировать нефункциональные требования: latency, availability, consistency, RPS, объёмы данных и ограничения по бюджету/срокам. Именно эти параметры определяют архитектурные компромиссы в следующих шагах.

## Q8. Как делать capacity planning и оценки нагрузки?

Оценки начинают с order-of-magnitude: DAU/MAU, средний и пиковый RPS, размер объекта, рост данных в день/месяц. Это позволяет выбрать порядок масштабирования и понять, где будет первое узкое место (CPU, сеть, диск, база, очередь).

Даже грубые числа полезнее их отсутствия: интервьюер видит, что решение не «в вакууме». После оценок проще обосновать шардирование, кэширование, выбор типа БД и количество реплик.

## Q9. Как выбирать хранилище: SQL, NoSQL, search?

SQL обычно выбирают, когда важны строгие транзакции, сложные связи и согласованность. NoSQL — когда приоритет на горизонтальном масштабировании, гибкой схеме и high-throughput под конкретные access patterns.

Поисковые движки (например, Elasticsearch/OpenSearch) добавляют для полнотекстового поиска и аналитических запросов, а не как единственный source of truth. Часто в зрелой системе используется полиглотное хранение: OLTP + cache + search индекс.

## Q10. Как проектировать кэширование и CDN-слой?

Кэширование начинают с read-heavy сценариев: горячие ключи, карточки объектов, агрегаты. Выбирают стратегию (`cache-aside`, `write-through`, TTL + invalidation) и явно обсуждают риски stale-данных и stampede.

CDN добавляют для статики и edge-контента, чтобы снизить latency и разгрузить origin. Важно сразу описать ключи кэша, политику инвалидации и метрики hit ratio — иначе кэш быстро превращается в источник инцидентов.

## Q11. Когда использовать очереди и event-driven подход?

Очереди и события уместны, когда нужно развязать компоненты по времени и нагрузке: асинхронная обработка, ретраи, буферизация пиков, fan-out в несколько потребителей. Это повышает устойчивость и масштабируемость, но добавляет операционную сложность.

На интервью важно назвать последствия: at-least-once доставка, необходимость идемпотентности, DLQ, мониторинг lag и схем сообщений. То есть не только «добавить Kafka», но и как безопасно это эксплуатировать.

## Q12. Как обсуждать консистентность и CAP trade-offs?

Нужно явно обозначить, где нужна strong consistency, а где допустима eventual consistency. Например, платежи и балансы требуют строгих гарантий, а ленты и рекомендации обычно терпимы к задержке синхронизации.

Дальше проговаривают механизмы: quorum, idempotency, saga/outbox, retry + deduplication. Хороший ответ показывает, что компромисс выбран осознанно под бизнес-требования, а не «по умолчанию».

## Q13. Какие аспекты безопасности обязательно покрывать в дизайне?

Базовый минимум: аутентификация, авторизация, шифрование in transit/at rest, rate limiting, защита секретов и аудит. Для внешних API добавляют WAF/бот-защиту, для внутренних сервисов — mTLS и принцип least privilege.

Также важно упомянуть безопасность данных: PII-маскирование, ретеншн, соответствие требованиям комплаенса. На интервью ценится практический подход: какие именно контуры и точки риска вы защищаете.

## Q14. Как спроектировать observability для системы?

Observability проектируют с самого начала: метрики (RED/USE), структурированные логи с correlation id, распределённый трейсинг с traceId/spanId. Это позволяет расследовать инциденты по цепочке «алерт → трейс → логи».

Нужно предусмотреть алерты, SLO/SLI, дашборды и runbook-и. Без этого даже хорошая архитектура в проде становится «чёрным ящиком», где сложно быстро найти причину деградации.

## Q15. Как завершать решение и презентовать trade-offs интервьюеру?

В финале полезно коротко резюмировать: требования, выбранная архитектура, основные компромиссы и точки масштабирования. Это показывает структурное мышление и умение не терять контекст после погружения в детали.

Отдельно стоит назвать риски и план эволюции: что станет bottleneck при росте x10, какие улучшения будут следующими шагами. Такой финал обычно даёт сильный сигнал senior-уровня коммуникации.
