---
title: "System Design: Дизайн Instagram"
description: "Полный дизайн Instagram: media pipeline, feed (push/pull), stories (TTL), DMs, explore (ML), search, sharding, capacity planning."
tags:
  - interview
  - system-design
  - design-instagram
type: "interview"
difficulty: "advanced"
aliases:
  - "Design Instagram interview"
  - "Instagram system design"
  - "Дизайн Instagram собеседование"
  - "Photo sharing system"
updated: "2026-05-22"
---

# System Design: Дизайн `Instagram`

`Instagram` — сервис обмена фото и видео с 2B пользователей, 500M DAU, 100M фото/день. Преобладает чтение (соотношение чтение/запись ≈ 100:1), огромный объём медиа (петабайты хранилища), нужна лента с низкой задержкой и сложный ML для explore page. Один из самых популярных system design вопросов уровня senior/staff.

## Полезные ссылки

- [Instagram Engineering Blog](https://instagram-engineering.com/)
- [Scaling Instagram Infrastructure (F8)](https://www.youtube.com/watch?v=hnpzNAPiC0E)
- [Designing Instagram — System Design Primer](https://github.com/donnemartin/system-design-primer/blob/master/solutions/system_design/instagram/README.md)
- [Cassandra at Instagram](https://instagram-engineering.com/open-sourcing-pgbouncer-rr-replication-and-routing-modifications-150549a8b7c2)
- [Instagram Stories — backend story (High Scalability)](http://highscalability.com/blog/2017/12/11/how-instagram-stories-uses-machine-learning-to-build-better.html)
- [CloudFront image optimization at edge](https://aws.amazon.com/cloudfront/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Requirements и capacity**
- [Q1. (!) Functional и non-functional requirements?](#q1--functional-и-non-functional-requirements)
- [Q2. (!) Оценка нагрузки: QPS, объём хранилища, bandwidth?](#q2--оценка-нагрузки-qps-объём-хранилища-bandwidth)
- [Q3. API design: основные endpoints?](#q3-api-design-основные-endpoints)

**Media upload pipeline**
- [Q4. (!) Upload flow: presigned URL и почему НЕ через app server?](#q4--upload-flow-presigned-url-и-почему-не-через-app-server)
- [Q5. Асинхронная обработка медиа: thumbnails, transcoding, модерация?](#q5-асинхронная-обработка-медиа-thumbnails-transcoding-модерация)
- [Q6. Resumable upload для больших видео?](#q6-resumable-upload-для-больших-видео)

**Storage и sharding**
- [Q7. (!) Storage architecture: метаданные vs media files?](#q7--storage-architecture-метаданные-vs-media-files)
- [Q8. (!) Sharding strategy для photos и feed?](#q8--sharding-strategy-для-photos-и-feed)
- [Q9. Схема таблиц: User, Photo, Follow, Like, Comment?](#q9-схема-таблиц-user-photo-follow-like-comment)

**Feed и Stories**
- [Q10. (!) High-level архитектура?](#q10--high-level-архитектура)
- [Q11. (!) Генерация ленты: fan-out on write или гибрид?](#q11--генерация-ленты-fan-out-on-write-или-гибрид)
- [Q12. Stories: 24h TTL и read-heavy паттерн?](#q12-stories-24h-ttl-и-read-heavy-паттерн)
- [Q13. Likes counter: hot photos и write-behind?](#q13-likes-counter-hot-photos-и-write-behind)

**DMs и Notifications**
- [Q14. (!) Direct Messages: WebSocket и Cassandra?](#q14--direct-messages-websocket-и-cassandra)
- [Q15. Push notifications: APNs/FCM и Kafka pipeline?](#q15-push-notifications-apnsfcm-и-kafka-pipeline)

**Search и Explore (ML)**
- [Q16. (!) Поиск: hashtag, username, автодополнение?](#q16--поиск-hashtag-username-автодополнение)
- [Q17. (!) Explore page: ML pipeline и offline batch?](#q17--explore-page-ml-pipeline-и-offline-batch)
- [Q18. Модерация контента: ML + human-in-loop?](#q18-модерация-контента-ml--human-in-loop)

**CDN и optimization**
- [Q19. (!) Стратегия CDN: CloudFront, edge resize, HTTP/3?](#q19--стратегия-cdn-cloudfront-edge-resize-http3)
- [Q20. Viral photo: hot key и prefetch?](#q20-viral-photo-hot-key-и-prefetch)
- [Q21. Cache invalidation при delete photo?](#q21-cache-invalidation-при-delete-photo)

**Edge cases и trade-offs**
- [Q22. (!) Multi-region deployment и consistency?](#q22--multi-region-deployment-и-consistency)
- [Q23. Rate limiting: загрузки и API?](#q23-rate-limiting-загрузки-и-api)
- [Q24. Failed uploads и idempotency?](#q24-failed-uploads-и-idempotency)
- [Q25. AP vs CP: какой trade-off выбирает Instagram?](#q25-ap-vs-cp-какой-trade-off-выбирает-instagram)
- [Q26. Capacity planning: число серверов и рост?](#q26-capacity-planning-число-серверов-и-рост)
- [Q27. Главные lessons и pitfalls дизайна?](#q27-главные-lessons-и-pitfalls-дизайна)

---

## Q1. (!) Functional и non-functional requirements?

**Функциональные**:

- Загрузка фото/видео (одиночное или carousel до 10 медиа).
- Просмотр ленты (home timeline отсортирован по релевантности/времени).
- Follow/unfollow пользователей → асимметричный social graph (followers != following).
- Лайки, комментарии, сохранение поста.
- Stories — эфемерный контент с TTL 24 часа.
- Direct Messages (1:1 и групповые чаты).
- Поиск: hashtag, username, локация.
- Explore page — персонализированные рекомендации.
- Уведомления: лайк, комментарий, новый follower, DM.

**Нефункциональные**:

| Параметр | Значение |
|----------|----------|
| Всего пользователей | 2B |
| DAU | 500M |
| Загрузки | 100M фото/день |
| Чтения (просмотры ленты) | ~50B/день (чтение:запись ≈ 100:1) |
| Доступность | 99.95% (≈ 4.5 часа простоя/год) |
| Задержка чтения (лента) | p99 < 200ms |
| Задержка загрузки | p99 < 2s (до presigned URL) |
| Консистентность | eventual для ленты, strong для лайков того же пользователя |
| Долговечность | 11 девяток для медиа (S3) |

**Вне области охвата** (типичный объём для интервью): реалтайм-аналитика, реклама, специфика IGTV/Reels, Shop.

---

## Q2. (!) Оценка нагрузки: QPS, объём хранилища, bandwidth?

Считаем «на салфетке», чтобы обосновать архитектуру числами. Главный вывод — нагрузка резко read-heavy (~290k чтений/сек против ~1.2k записей/сек) и медиа растёт на десятки PB в год. Отсюда и приоритеты: агрессивное кеширование на чтении, CDN, дешёвое object storage. Множитель пиковости ×3 — запас на всплески.

**QPS на запись**:

```
100M uploads/day = 100_000_000 / 86_400 ≈ 1_157 uploads/sec
Peak (×3) ≈ 3_500 uploads/sec
```

**QPS на чтение** (лента):

```
500M DAU × ~50 photos/day = 25B feed-photo-views/day
≈ 290_000 reads/sec
Peak (×3) ≈ 870_000 reads/sec
```

**Хранилище** (за год, только медиа):

```
Original photo (compressed JPEG): ~200 KB
Thumbnails (3 sizes: 150/320/1080): ~150 KB
Total per photo: ~350 KB

Per day: 100M × 350 KB ≈ 35 TB/day
Per year: 35 TB × 365 ≈ 12.7 PB/year (только новый контент)

С video (≈ 20% от загрузок, ~3 MB средний): ещё +20 TB/day = +7 PB/year
Total: ≈ 20 PB/year
```

С учётом репликации (3× в S3) и multi-region: фактический объём ≈ 60-80 PB/year.

**Метаданные** (PostgreSQL):

```
~1 KB per photo metadata × 100M = 100 GB/day = 36 TB/year
```

**Исходящий трафик** (через CDN):

```
500M DAU × 50 фото × ~50 KB (среднее, многие в маленьком разрешении)
= 1.25 PB/day = ~120 Gbps среднее
Peak: ~300-400 Gbps
```

**Серверы**:

- App tier (stateless): ~10_000 инстансов при 5K QPS на инстанс.
- Кэш (Redis): ~1_000 нод × 100 GB = 100 TB горячего кэша.
- БД (sharded Postgres): ~500 шардов.

---

## Q3. API design: основные endpoints?

API REST-style и группируется по доменам: загрузка (двухэтапная — сначала presigned URL, потом finalize), лента, stories, социальные действия, DM (с WebSocket для realtime), поиск и explore. Два сквозных решения: пагинация всегда cursor-based (стабильна при меняющемся наборе), аутентификация — OAuth 2.0 bearer-token.

```http
# Upload (2-этапный: получить presigned URL, потом upload в S3)
POST /api/v1/photos/upload-url
  Body: { contentType: "image/jpeg", sizeBytes: 204800 }
  Response: { uploadUrl: "https://s3...?X-Amz-Signature=...", photoId: "uuid" }

POST /api/v1/photos/{photoId}/finalize
  Body: { caption, location, tags: ["@user1", "#hashtag"] }
  Response: 201 { photoId, urls: {small, medium, large} }

# Feed
GET /api/v1/feed?cursor=<token>&limit=20
  Response: { items: [...], nextCursor: "..." }

# Stories
POST /api/v1/stories  (multipart, обычно <5MB)
GET  /api/v1/stories/feed                       # stories друзей
POST /api/v1/stories/{storyId}/view             # mark watched

# Social
POST /api/v1/users/{userId}/follow
DELETE /api/v1/users/{userId}/follow
POST /api/v1/photos/{photoId}/like
POST /api/v1/photos/{photoId}/comments

# DM
POST /api/v1/conversations/{convId}/messages
GET  /api/v1/conversations/{convId}/messages?cursor=...
WS   /ws/dm                                     # WebSocket для realtime

# Search и Explore
GET  /api/v1/search?q=<term>&type=user|hashtag|place
GET  /api/v1/explore                            # персонализированная лента
```

**Пагинация**: cursor-based (не offset) — стабильно при изменяющемся наборе данных.

**Аутентификация**: OAuth 2.0 bearer token, refresh token в HttpOnly cookie. Rate limit на токен и IP.

---

## Q4. (!) Upload flow: presigned URL и почему НЕ через app server?

Клиент грузит медиа **напрямую в S3** по короткоживущему presigned URL, а app server лишь подписывает запрос и сохраняет метаданные. Байты файла через бэкенд не проходят — это снимает с него весь media-трафик.

**Почему не гнать файл через app server** (клиент → app server → S3):

- App server становится узким местом по media-трафику (терабайты в час).
- Удваивается исходящий трафик: client→server + server→S3.
- App server тратит CPU просто на перекачку байт.

**Как работает presigned URL** — клиент напрямую в S3. Участники: `Client`, `Upload Service` (API), `S3 Bucket`, `Kafka`, `Media Worker`, `CloudFront` (CDN). По порядку:

1. `Client → Upload Service`: `POST /upload-url` (contentType, size).
2. `Upload Service` валидирует пользователя, применяет rate-limit, выделяет `photoId`.
3. `Upload Service → S3`: `getSignedUrl(PUT, photoId.bin, exp=10min)`.
4. `Upload Service → Client`: `{ uploadUrl, photoId }`.
5. `Client → S3`: `PUT uploadUrl` + binary body.
6. `S3 → Client`: `200 OK`.
7. `Client → Upload Service`: `POST /photos/{photoId}/finalize` (caption, tags).
8. `Upload Service` вставляет метаданные (status=PROCESSING).
9. `Upload Service → Kafka`: emit `PhotoUploaded(photoId, s3Key)`.
10. `Upload Service → Client`: `202 Accepted` (photoId).
11. `Media Worker → Kafka`: consume `PhotoUploaded`.
12. `Media Worker → S3`: GET original.
13. `Media Worker` генерирует thumbnails (150, 320, 1080).
14. `Media Worker` выполняет `HEIC → JPEG`, EXIF strip, content moderation.
15. `Media Worker → S3`: PUT thumbnails (photoId_150.jpg, ...).
16. `Media Worker → Upload Service`: status=READY.
17. `Media Worker → CloudFront`: warm cache (опциональный prefetch).

Пример генерации presigned URL (Java, AWS SDK v2):

```java
@Service
@RequiredArgsConstructor
public class PresignedUrlService {
    private final S3Presigner presigner;

    public PresignedUploadResponse generate(String userId, String contentType, long size) {
        String photoId = UUID.randomUUID().toString();
        String key = "uploads/%s/%s.bin".formatted(userId, photoId);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket("instagram-uploads")
            .key(key)
            .contentType(contentType)
            .contentLength(size)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(objectRequest)
            .build();

        URL url = presigner.presignPutObject(presignRequest).url();
        return new PresignedUploadResponse(url.toString(), photoId);
    }
}
```

**Преимущества**:

- App server обрабатывает только небольшие JSON-метаданные → 100× меньше CPU/RAM.
- S3 масштабирует загрузку параллельно без вмешательства.
- Multipart upload работает прозрачно для клиента (для файлов > 5 MB).

---

## Q5. Асинхронная обработка медиа: thumbnails, transcoding, модерация?

Обработка медиа вынесена из upload-запроса: загрузка завершается сразу, а тяжёлые этапы (ресайз, транскодинг, модерация) идут асинхронно. После загрузки app server кладёт событие в Kafka, и **media-worker** выполняет конвейер шаг за шагом — пользователь не ждёт.

**Конвейер обработки фото**:

| Этап | Инструмент | Назначение |
|------|------|------------|
| Декодирование | libjpeg/libheif | Поддержка HEIC из iOS |
| Ресайз | ImageMagick / libvips (быстрее в 5×) | 4 размера: 150, 320, 720, 1080 |
| Формат | Кодирование в JPEG/WebP/AVIF | AVIF для современных браузеров (на 50% меньше) |
| EXIF | exiftool | Вырезать GPS, серийный номер камеры |
| ML | TensorFlow Serving | NSFW, насилие, спам, классификация контента |
| OCR | Tesseract | Текст на картинках для модерации/поиска |
| Эмбеддинги | CLIP | Вектор для explore page и поиска похожих изображений |
| Детекция лиц | RetinaFace | Подсказки для tag friends |

**Видео** (более тяжёлый pipeline):

```bash
# Transcoding в несколько битрейтов для adaptive streaming (HLS)
ffmpeg -i input.mp4 \
  -c:v libx264 -profile:v main -level 3.1 \
  -b:v:0 400k -s:v:0 426x240 \
  -b:v:1 800k -s:v:1 640x360 \
  -b:v:2 1500k -s:v:2 854x480 \
  -b:v:3 3000k -s:v:3 1280x720 \
  -hls_time 4 -hls_playlist_type vod \
  -master_pl_name master.m3u8 \
  output_%v.m3u8
```

**Пропускная способность**:

- 100M фото/день → ~1200/sec → каждый worker 5 фото/sec → ~240 workers.
- Видео: в 10× дороже по CPU → отдельный пул GPU-нод для transcoding.

**Идемпотентность**: каждый job ключуется по `photoId`. Worker сначала проверяет `status` в БД — если `READY`, пропускает.

---

## Q6. Resumable upload для больших видео?

Большой файл бьётся на части, и каждая загружается отдельным запросом — при обрыве сети докачиваются только недостающие части, а не весь файл заново. Стандартные механизмы: **S3 Multipart Upload** или **протокол TUS**.

```java
// S3 multipart upload — клиент-сайд
CreateMultipartUploadResponse init = s3.createMultipartUpload(b -> b
    .bucket("uploads").key(key));
String uploadId = init.uploadId();

// Каждая часть >= 5MB (кроме последней)
for (int partNum = 1; partNum <= totalParts; partNum++) {
    UploadPartRequest req = UploadPartRequest.builder()
        .bucket("uploads").key(key)
        .uploadId(uploadId).partNumber(partNum).build();
    UploadPartResponse resp = s3.uploadPart(req, RequestBody.fromBytes(chunk));
    completedParts.add(CompletedPart.builder()
        .partNumber(partNum).eTag(resp.eTag()).build());
}

s3.completeMultipartUpload(b -> b
    .bucket("uploads").key(key).uploadId(uploadId)
    .multipartUpload(m -> m.parts(completedParts)));
```

**Что даёт**:

- При обрыве WiFi клиент дозагружает оставшиеся parts (списком ETag из локального состояния).
- Параллельная загрузка частей → быстрее полная загрузка.
- TTL для незавершённых загрузок: 7 дней (lifecycle rule в S3), потом авто-очистка.

**Компромисс**: минимум 5 MB на part → не подходит для маленьких фото; для них одиночный PUT.

---

## Q7. (!) Storage architecture: метаданные vs media files?

Единого хранилища нет: под каждый тип данных выбирается своё, исходя из паттерна доступа. Бинарное медиа лежит в object storage (S3), структурированные метаданные — в реляционной БД, лента и счётчики — в Redis. Так каждое хранилище решает ровно одну задачу и масштабируется независимо.

| Данные | Хранилище | Почему |
|--------|---------|--------|
| Метаданные фото (id, user, caption, tags, location, timestamp) | PostgreSQL (sharded) | ACID, индексы, joins |
| Оригинал + транскодированные медиа (binary) | S3 + CloudFront | дешёвое object storage, 11 девяток долговечности |
| Лента (предрассчитанный timeline) | Redis (sorted set ZADD) | range-запросы O(log N), задержка в мс |
| Stories | Redis (TTL 24h) | авто-истечение, эфемерность |
| Direct Messages | Cassandra | много записей, partition по conversation_id |
| Social graph (follows) | Sharded Postgres ИЛИ Cassandra | много чтений, простой поиск по ключу |
| Поисковый индекс | Elasticsearch | full-text, edge n-grams для автодополнения |
| Эмбеддинги (ML) | Faiss / Milvus / PG pgvector | ANN-поиск для explore |
| Счётчик лайков | Redis INCR + Kafka → Postgres async | избегает конкуренции за горячую строку |
| Горячий кэш (популярные фото) | Redis + репликация на edge | ответ < 1 мс |

**Принцип**: разделять хранилища по **паттерну доступа**, а не по domain entity.

---

## Q8. (!) Sharding strategy для photos и feed?

Ключ шардирования выбирается под главный запрос: фото шардируются по `user_id` (профиль = один shard), а лента — по `follower_id` (чтение ленты = один Redis-node). Разные сущности шардируются по разным ключам, потому что у них разные паттерны доступа.

**Фото** — шардирование по `user_id`:

```sql
-- shard_id = hash(user_id) % NUM_SHARDS
-- N = 1024 shards (16 physical hosts × 64 logical = легко rebalance)

-- Routing layer:
SELECT * FROM photos WHERE user_id = ? AND photo_id = ?
-- → идёт в shard_N
```

Почему по `user_id`:

- Запросы «фото пользователя X» (профиль) → один shard.
- Лента пользователя обращается ко всем shards followee → решается предрасчётом ленты.

**Лента** — шардирование по `follower_id`:

```
Если хранить feed для каждого user в Redis ZSET timeline:{follower_id}
→ shard by hash(follower_id) → один Redis node на user
→ GET feed = ZREVRANGE на одном ноде
```

Подробнее про сравнение стратегий см. [Database sharding](../databases/database-sharding-interview.md).

**Проблема горячего шарда** (hot shard): один пост знаменитости порождает миллионы записей в ленты подписчиков, разбросанные по всем shards.

- Знаменитость (например, @cristiano с 600M followers) → не делаем fan-out по всем shards.
- Решение: для top-1000 пользователей — pull-модель (на чтении ленты обращаемся к их inbox), для остальных — push (fan-out на запись).

**Решардинг**: consistent hashing с 1024 виртуальными bucket-ами → при добавлении ноды перемещается всего 1/1024 данных, а не половина, как при `hash % N`.

---

## Q9. Схема таблиц: User, Photo, Follow, Like, Comment?

Ядро — пять сущностей в PostgreSQL. Главные приёмы: социальный граф асимметричен (две строки нужны для взаимной подписки, поэтому ключ `(follower_id, followee_id)` плюс обратный индекс по `followee_id`), а связи many-to-many (хештеги) выносятся в junction-таблицу. Под каждый частый запрос заводится составной индекс.

PostgreSQL (упрощённо), полные определения ниже:

```sql
CREATE TABLE users (
    user_id      BIGINT PRIMARY KEY,
    username     VARCHAR(50) UNIQUE NOT NULL,
    email_hash   BYTEA,
    created_at   TIMESTAMPTZ DEFAULT now(),
    profile_pic  TEXT,
    bio          TEXT,
    is_private   BOOLEAN DEFAULT false,
    is_verified  BOOLEAN DEFAULT false
);

CREATE TABLE photos (
    photo_id     UUID PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    s3_key       TEXT NOT NULL,
    caption      TEXT,
    location_id  BIGINT,
    created_at   TIMESTAMPTZ DEFAULT now(),
    status       VARCHAR(16) DEFAULT 'PROCESSING',  -- PROCESSING, READY, FAILED, DELETED
    width        INT,
    height       INT,
    media_type   VARCHAR(8)                          -- PHOTO, VIDEO, CAROUSEL
);
CREATE INDEX idx_photos_user_ts ON photos(user_id, created_at DESC);

-- Hashtag — many-to-many через junction
CREATE TABLE photo_hashtags (
    photo_id  UUID,
    hashtag   VARCHAR(140),
    PRIMARY KEY (photo_id, hashtag)
);
CREATE INDEX idx_hashtag_photo ON photo_hashtags(hashtag, photo_id);

-- Follow: асимметрично (A может следить за B, но не наоборот)
CREATE TABLE follows (
    follower_id  BIGINT,
    followee_id  BIGINT,
    created_at   TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (follower_id, followee_id)
);
CREATE INDEX idx_follows_followee ON follows(followee_id, follower_id);

-- Like — write-heavy → отдельный shard ключ
CREATE TABLE likes (
    photo_id    UUID,
    user_id     BIGINT,
    created_at  TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (photo_id, user_id)
);

-- Comments
CREATE TABLE comments (
    comment_id  BIGINT PRIMARY KEY,
    photo_id    UUID NOT NULL,
    user_id     BIGINT NOT NULL,
    parent_id   BIGINT,                    -- nested
    text        TEXT,
    created_at  TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_comments_photo_ts ON comments(photo_id, created_at);
```

Схема Cassandra для DMs (Q14):

```cql
CREATE TABLE messages (
    conversation_id  UUID,
    message_id       TIMEUUID,           -- bucket by time, sorted DESC
    sender_id        BIGINT,
    body             TEXT,
    media_url        TEXT,
    delivered        BOOLEAN,
    PRIMARY KEY (conversation_id, message_id)
) WITH CLUSTERING ORDER BY (message_id DESC);
```

---

## Q10. (!) High-level архитектура?

Система делится на пять слоёв: edge (DNS + CDN), API gateway (auth, rate-limit), stateless-микросервисы по доменам, асинхронная шина Kafka с воркерами (media, ML, fan-out) и гетерогенное хранилище. Главный приём — синхронный путь делает минимум (валидация + запись метаданных), а всё тяжёлое уходит в Kafka и обрабатывается воркерами в фоне.

Компоненты и связи (стрелка `→` = направление вызова/потока, пунктир обозначен как «асинхронно»):

- **Client** — `iOS App`, `Android App`, `Web`. Все три → `Global Load Balancer + Anycast DNS` (LB).
- `LB` → `CloudFront CDN` (edge image resize).
- `LB` → `API Gateway` (auth, rate-limit, routing).
- `API Gateway` → все микросервисы: `Upload Service`, `Feed Service`, `Story Service`, `Social Graph Service`, `Search Service`, `Explore Service`, `Notification Service`, `DM Service` (WebSocket).
- **Async-слой**: `Kafka`, `Media Workers` (thumbnails, transcoding), `ML Workers` (moderation, embeddings), `Fan-out Workers` (feed pre-compute).
- **Storage**: `Sharded Postgres` (1024 shards, metadata, social graph), `S3 Object Storage` (media), `Redis Cluster` (feed, stories, counters), `Cassandra` (DMs), `Elasticsearch` (search index), `Faiss/pgvector` (embeddings).

Потоки данных:

- `Upload Service → S3` (presigned URL).
- `Upload Service ⇢ Kafka` (асинхронно, finalize).
- `Kafka → Media Workers → S3`.
- `Kafka → ML Workers → Faiss/pgvector`.
- `Kafka → Fan-out Workers → Redis`.
- `Feed Service → Redis` и `Feed Service → Postgres`.
- `Story Service → Redis`.
- `Social Graph Service → Postgres`.
- `Search Service → Elasticsearch`.
- `Explore Service → Faiss/pgvector` и `Explore Service → Redis`.
- `DM Service → Cassandra`.
- `Notification Service → Kafka`.
- `S3 → CloudFront CDN`.

**Слои**:

1. **Edge** — Anycast DNS, GeoDNS направляет в ближайший регион. CloudFront раздаёт медиа.
2. **API gateway** — аутентификация (OAuth2), rate-limit, A/B-маршрутизация.
3. **Микросервисы** — stateless, разделены по доменам.
4. **Kafka** — основная асинхронная шина (PhotoUploaded, UserFollowed, MessageSent).
5. **Хранилище** — гетерогенное: каждое под свой паттерн доступа.

---

## Q11. (!) Генерация ленты: fan-out on write или гибрид?

Instagram использует **гибрид**: для обычных авторов лента собирается на запись (push), а посты знаменитостей подмешиваются на чтении (pull). Чистый push разваливается на аккаунтах с миллионами подписчиков, чистый pull — на дорогом чтении; гибрид берёт сильную сторону каждого.

Базовые подходы (детально в [Design Feed System](design-feed-system-interview.md)):

| Подход | Плюсы | Минусы |
|--------|------|------|
| **Push (fan-out on write)** | чтение O(1), быстро | дорого для знаменитостей (write × N followers) |
| **Pull (fan-out on read)** | дешёвая запись | дорогое чтение: join по N followees |
| **Hybrid** | лучшее из обоих | сложнее логика |

**Гибрид Instagram** (упрощённо):

```
WHEN user_X публикует photo:
    IF followers_count(X) < 100_000:
        // fan-out on write
        FOR each follower F of X:
            ZADD feed:{F} timestamp photo_id  // push
    ELSE:
        // celebrity — pull on read
        celebrity_inbox:{X}.append(photo_id)

WHEN follower F открывает feed:
    base = ZREVRANGE feed:{F} 0 N        // push-приходы
    celeb_posts = []
    FOR each celebrity C followed by F:
        celeb_posts += recent_inbox:{C}    // pull
    return merge_and_rank(base + celeb_posts)
```

Полный поток. Участники: `User upload`, `Feed Service`, `Fan-out Worker`, `Kafka`, `Redis ZSET feed:{follower}`, `Viewer`. По порядку:

1. `User upload → Feed Service`: `POST /photos finalize`.
2. `Feed Service → Kafka`: `PhotoPublished(photoId, authorId)`.
3. `Fan-out Worker → Kafka`: consume.
4. `Fan-out Worker` загружает `followers(authorId)`.
5. Ветвление по числу подписчиков:
   - если `followers < 100k` — для каждого follower `F`: `Fan-out Worker → Redis`: `ZADD feed:{F} ts photoId`;
   - иначе (celebrity) — `Fan-out Worker → Redis`: `ZADD celeb_inbox:{authorId} ts photoId`.
6. `Viewer → Feed Service`: `GET /feed`.
7. `Feed Service → Redis`: `ZREVRANGE feed:{viewerId} 0 20`.
8. `Feed Service → Redis`: для celebrities — `ZREVRANGE celeb_inbox:{C}`.
9. `Feed Service` выполняет merge + ML rank.
10. `Feed Service → Viewer`: feed items.

Структура Redis ZSET:

```
KEY: feed:{viewerId}
VALUE: ZSET, score = ts_micros, member = photo_id
TTL:   30 days (старее уходит в paginated DB fetch)

CACHE SIZE: 500M users × 500 items × 50 bytes ≈ 12 TB hot
SHARDING: hash(viewerId) % redis_clusters
```

**Обрезка (trim)**: фид в Redis ограничен ~1000 последними элементами, иначе ZSET рос бы безгранично; что старее — догружается пагинацией через Postgres.

---

## Q12. Stories: 24h TTL и read-heavy паттерн?

Stories — эфемерный контент: живёт 24 часа и читается на порядок чаще, чем пишется. Ключевая идея — переложить истечение на хранилище: всё кладётся в Redis с TTL 86400 сек, и просроченные записи исчезают сами, без отдельного фонового сборщика.

**Нагрузка и особенности**:

- 500M пользователей × ~5 просмотров stories/день → ~2.5B чтений/день на stories.
- TTL ровно 24 часа от момента публикации.
- Отслеживание просмотров: каждый просмотр — отдельная запись (для «кто смотрел?»).

**Хранилище**:

```redis
# Story metadata
SET story:{storyId} '{"userId":..., "s3Key":..., "createdAt":...}' EX 86400

# User active stories (sorted by ts)
ZADD user_stories:{userId} <ts> <storyId>      # с TTL 24h на ZSET члена
EXPIRE user_stories:{userId} 86400

# Stories feed (для просмотра): какие из followees имеют активные stories
# Считается lazy на read из user_stories:{followee} for each followee
```

**Флаг просмотра (watched)**:

```
SADD story_viewers:{storyId} <viewerId>
EXPIRE story_viewers:{storyId} 86400 * 2       # TTL дольше, чем сама story
```

Если зрителей > 100k (знаменитость) — переход на HyperLogLog для приблизительного подсчёта:

```
PFADD story_viewers_hll:{storyId} <viewerId>
PFCOUNT story_viewers_hll:{storyId}            # ≈ count, 0.81% error
```

**Поток чтения**:

```java
public StoriesFeed getStoriesFeed(long viewerId) {
    List<Long> followees = socialGraph.followees(viewerId);
    return followees.parallelStream()
        .map(f -> redis.zrevrange("user_stories:" + f, 0, -1))
        .flatMap(List::stream)
        .map(this::fetchStoryMeta)
        .filter(Objects::nonNull)              // expired стало null
        .sorted(comparing(Story::ts).reversed())
        .collect(toList());
}
```

**Граничные случаи**:

- Расхождение времени между нодами Redis → TTL может «съесть» story раньше 24h. Решение: хранить явный `expires_at` и фильтровать на чтении (не полагаться только на TTL).
- Повторный просмотр: после прочтения, при повторном открытии — story показывается с тёмным кругом (без отметки непросмотренного).

---

## Q13. Likes counter: hot photos и write-behind?

Счётчик лайков ведётся в Redis (`INCR`), а в Postgres сбрасывается батчами раз в несколько секунд (write-behind). Так тысячи лайков в секунду на одно фото не упираются в блокировку одной строки БД.

**Проблема**: вирусное фото набирает 10_000 лайков/сек → UPDATE одной строки в Postgres = конкуренция за блокировку (row lock), лаг репликации.

**Решение** — write-behind через Redis. Поток (стрелка `→` = вызов, пунктир = асинхронно):

- `User → API`: `POST /like`.
- `API → Redis`: `INCR like_count:photo`.
- `API → Redis`: `SADD likers:photo userId`.
- `Redis ⇢ Flusher`: асинхронно через Kafka stream, каждые N ms.
- `Flusher → Postgres`: batch UPDATE.

```java
public void like(UUID photoId, long userId) {
    if (!redis.sadd("likers:" + photoId, userId)) {
        return;                                // уже лайкнул, idempotent
    }
    long newCount = redis.incr("like_count:" + photoId);
    kafka.send("photo-likes", new LikeEvent(photoId, userId, newCount));
}
```

Flusher (сброс раз в 5 сек или каждые 100 событий):

```java
@KafkaListener(topics = "photo-likes")
public void flush(List<LikeEvent> batch) {
    Map<UUID, Long> latestCount = batch.stream()
        .collect(toMap(LikeEvent::photoId, LikeEvent::count,
                       (a, b) -> Math.max(a, b)));
    jdbc.batchUpdate("UPDATE photos SET like_count = ? WHERE photo_id = ?",
        latestCount.entrySet().stream()
            .map(e -> new Object[]{e.getValue(), e.getKey()})
            .toList());
}
```

**Компромисс**:

- Eventual consistency: счётчик в Postgres отстаёт от Redis на ~5 сек.
- Приемлемо: пользователи читают счётчик из Redis (он свежий), Postgres нужен лишь как долговечный бэкап.
- При сбоях Redis счётчик теряется, но восстанавливается из Postgres + повтор событий из Kafka.

Для приблизительного подсчёта (топ-хештеги, вирусные счётчики) — HyperLogLog или Count-Min Sketch: точное число там не нужно, зато память константна.

---

## Q14. (!) Direct Messages: WebSocket и Cassandra?

Доставка сообщений в реальном времени держится на двух опорах: **WebSocket** даёт постоянное двунаправленное соединение для мгновенного push, а **Cassandra** хранит историю — её модель «много записей, partition по conversation_id» идеальна под чат. Между ними Kafka разносит сообщение на тот gateway, к которому подключён получатель.

Архитектура чата (детально в [Design Chat System](design-chat-system-interview.md)). Полный flow. Участники: `User A`, `Connection LB`, `Gateway-1` (WebSocket), `Gateway-2`, `Redis` (sessions map), `Kafka` (messages), `Cassandra` (persist), `User B`. По порядку:

1. `User A → Connection LB`: WS Upgrade.
2. `Connection LB → Gateway-1`: route by `hash(userA)`.
3. `Gateway-1 → Redis`: `SET session:A → GW1`.
4. `User A → Gateway-1`: `send msg(convId, to=B, body)`.
5. `Gateway-1 → Kafka`: produce `ChatMessage`.
6. `Gateway-1 → Cassandra`: `INSERT (convId, msgId, ...)`.
7. `Gateway-1 → User A`: `ack(msgId)`.
8. `Kafka → Gateway-2`: consume (gateway-shard получателя B).
9. `Gateway-2 → Redis`: `LOOKUP session:B → GW2`.
10. `Gateway-2 → User B`: push via WS.
11. `User B → Gateway-2`: ack delivered.
12. `Gateway-2 → Cassandra`: `UPDATE delivered=true`.

**Схема Cassandra**:

```cql
CREATE TABLE conversation_messages (
    conv_id     UUID,
    bucket      INT,                  -- день, для time-bucketing
    msg_id      TIMEUUID,
    sender_id   BIGINT,
    body        TEXT,
    media_url   TEXT,
    PRIMARY KEY ((conv_id, bucket), msg_id)
) WITH CLUSTERING ORDER BY (msg_id DESC);

CREATE TABLE user_conversations (
    user_id     BIGINT,
    last_msg_ts TIMESTAMP,
    conv_id     UUID,
    unread_count INT,
    PRIMARY KEY (user_id, last_msg_ts, conv_id)
) WITH CLUSTERING ORDER BY (last_msg_ts DESC);
```

**Почему Cassandra**:

- Много записей: миллионы сообщений/сек.
- Partition по `(conv_id, bucket)` распределяет нагрузку.
- TTL: можно задать retention (например, 1 год).
- Multi-DC репликация для гео-доступности.

**Масштабирование WebSocket**:

- 500M DAU × ~30% активных одновременно = ~150M одновременных соединений.
- 100k соединений на инстанс → 1500 gateway-нод.
- Sticky-маршрутизация через consistent hash по `userId`.
- При смерти gateway-ноды — клиент переподключается (auto-reconnect), экспоненциальный backoff.

---

## Q15. Push notifications: APNs/FCM и Kafka pipeline?

События (лайк, комментарий, follow, DM) проходят через Kafka в NotificationService, который фильтрует их по настройкам пользователя и батчит, а затем отдаёт в платформенные шлюзы — APNs для iOS и FCM для Android. Асинхронный конвейер нужен, чтобы всплеск событий не ронял отправку и позволял схлопывать спам в одно уведомление.

**Конвейер**:

```
Event (like/comment/follow/dm)
    ↓ Kafka topic "user-events"
NotificationService
    ↓ filter (user preferences, mute, batching)
Kafka topic "outgoing-push"
    ↓
Push-Dispatcher
    ↓
APNs (iOS) / FCM (Android) / WebPush
```

```java
@KafkaListener(topics = "user-events")
public void handle(UserEvent event) {
    UserPrefs prefs = prefs.load(event.targetUserId());
    if (prefs.muted(event.type())) return;

    if (event.type() == LIKE) {
        // batch: «Eva и ещё 5 человек лайкнули ваше фото»
        likeBatcher.add(event);
        return;
    }

    PushPayload payload = renderPayload(event, prefs.locale());
    kafka.send("outgoing-push", payload);
}
```

**Батчинг** для лайков: не отправлять 1000 уведомлений за 1 минуту — агрегировать в одно «N человек лайкнули».

**APNs**:

- HTTP/2, мультиплексированное постоянное соединение.
- 100-1000 нотификаций/сек на соединение → пул соединений.
- Истёк push-токен → очистка.

**Сбои**:

- APNs возвращает `BadDeviceToken` → удалить токен из БД, не ретраить.
- Rate limit от Apple → экспоненциальный backoff.

---

## Q16. (!) Поиск: hashtag, username, автодополнение?

Поиск строится на **Elasticsearch**: full-text по username/bio/хештегам, а автодополнение «по мере набора» — на edge n-grams, которые заранее режут `cristiano` на префиксы `cr`, `cri`, `cris`… Релевантность подкручивается популярностью аккаунта (число подписчиков). Индекс наполняется из Postgres через CDC, отставая на ~1 секунду.

**Elasticsearch** (см. [Elasticsearch](../databases/elasticsearch-interview.md)):

Индекс для users:

```json
PUT /users
{
  "mappings": {
    "properties": {
      "username":      { "type": "text", "analyzer": "username_analyzer" },
      "username_kw":   { "type": "keyword" },
      "display_name":  { "type": "text" },
      "bio":           { "type": "text" },
      "followers":     { "type": "long" }
    }
  },
  "settings": {
    "analysis": {
      "analyzer": {
        "username_analyzer": {
          "tokenizer": "edge_ngram_tokenizer",
          "filter": ["lowercase"]
        }
      },
      "tokenizer": {
        "edge_ngram_tokenizer": {
          "type": "edge_ngram",
          "min_gram": 2,
          "max_gram": 20
        }
      }
    }
  }
}
```

**Запрос автодополнения**:

```json
GET /users/_search
{
  "query": {
    "function_score": {
      "query": { "match": { "username": "crist" } },
      "field_value_factor": {
        "field": "followers",
        "modifier": "log1p"
      }
    }
  },
  "size": 10
}
```

Здесь `function_score` × log(followers) — повышение релевантности для популярных аккаунтов.

Здесь `username_analyzer` на edge n-grams позволяет находить аккаунт уже по первым 2 символам ввода.

**Поиск по хештегам**:

- Индекс `hashtags` со счётчиком постов.
- Trending: поток через Kafka + Count-Min Sketch для top-K за последний час (точный подсчёт по всем хештегам не нужен — важен лишь топ).

**Конвейер индексации**:

```
Postgres CDC (Debezium) → Kafka → Elasticsearch indexer
Latency: ~1 sec eventually consistent
```

---

## Q17. (!) Explore page: ML pipeline и offline batch?

Explore работает в две ступени: **ночной batch** заранее отбирает топ-500 кандидатов на каждого пользователя (collaborative filtering + content-based по эмбеддингам), а **онлайн-ранжирование** на каждый запрос пересортировывает их моделью и отдаёт топ-30. Тяжёлый отбор кандидатов вынесен в офлайн, чтобы на чтении оставалась лишь быстрая дешёвая пересортировка.

Архитектура и алгоритм. Компоненты: `User events` (like, comment, save, dwell time), `Data Lake` (S3 Parquet), `Spark/Flink batch jobs` (nightly), `Embedding model` (collaborative + content), `Vector store` (Faiss/Milvus), `Pre-compute candidates per user`, `Redis` (`explore:user_id`), `Explore API`, `User device`. Потоки (стрелка `→`, пунктир = асинхронно):

- **Offline-ветка**: `User events → Data Lake → Spark/Flink batch jobs → Embedding model → Vector store → Pre-compute candidates per user → Redis`.
- **Online-ветка**: `User device → Explore API`; `Explore API → Redis` (готовые кандидаты) и `Explore API → Embedding model`.
- `Embedding model ⇢ Explore API`: real-time rerank (асинхронно).

**Двухступенчатая модель**:

1. **Генерация кандидатов** (offline, ночью):
   - Collaborative filtering: ALS / matrix factorization → эмбеддинг пользователя.
   - Content-based: CLIP/ResNet эмбеддинг фото → ANN-поиск «похожих на ранее залайканные».
   - Топ-500 кандидатов на пользователя сохраняем в `explore:{userId}`.

2. **Онлайн-ранжирование** (real-time на каждый GET /explore):
   - GBDT (XGBoost/LightGBM) или DNN.
   - Признаки: свежесть, engagement rate фото, история взаимодействия пользователь-автор, локация, время суток, устройство.
   - Сортирует 500 кандидатов → топ-30 в ответ.

Подробнее про эмбеддинги — [Embeddings](../ai-ml/embeddings-interview.md).

**Холодный старт**: новый пользователь → trending-контент в его регионе + по языку.

**Разнообразие**: после ранжирования применяется reranker (MMR — Maximal Marginal Relevance), чтобы не показывать 20 фото кошек подряд.

---

## Q18. Модерация контента: ML + human-in-loop?

Модерация — каскад из четырёх уровней, отсортированный по скорости и стоимости: сначала мгновенный hash-match по известному запрещённому контенту, затем ML-классификаторы, OCR-проверка текста на картинке и лишь в пограничных случаях (confidence 0.5–0.9) — живой ревьюер. ML фильтрует основной поток за миллисекунды, человек разбирает только спорное.

**Уровни**:

| Уровень | Что | Задержка |
|---------|-----|---------|
| L1: hash-match | PhotoDNA hash для известного CSAM, террористического контента | < 1 сек |
| L2: ML-классификатор | NSFW, насилие, hate speech, спам | 100-500 мс |
| L3: OCR + текстовый классификатор | Текст на картинках → токсичные формулировки | 1-2 сек |
| L4: Человек-ревьюер | Попадает в очередь ревьюеров, если confidence 0.5-0.9 | минуты—часы |

**Pipeline**:

```
PhotoUploaded → ML worker
    ↓ (parallel)
    score_nsfw, score_violence, score_spam
    ↓
    IF max_score > 0.95:
        BLOCK + notify user
    ELIF max_score > 0.5:
        SHADOW (показывать только автору) + send to human queue
    ELSE:
        PUBLISH
```

**Очередь на ручную проверку**:

- Kafka topic `moderation-queue`.
- 50_000+ модераторов по всему миру (через подрядчиков).
- SLA: 95% проверено в течение 24h.

**Ложные срабатывания**: процесс апелляций — пользователь может оспорить, второй ревьюер пересматривает.

---

## Q19. (!) Стратегия CDN: CloudFront, edge resize, HTTP/3?

Раздача медиа полностью лежит на CDN: фото кешируются на edge-POP-ах рядом с пользователем, отдаются за десятки миллисекунд, а origin (S3) трогается только при cache miss. Ключевые приёмы — ресайз и выбор формата (AVIF/WebP) прямо на edge и HTTP/3 для медленных мобильных сетей.

Поток получения фото:

```
Client request: GET /p/abc123_320.jpg
    ↓
CloudFront edge (POP в 300+ городах)
    ├─ HIT: ответ за ~20-50 мс
    └─ MISS:
        ↓ Origin Shield (regional cache)
        ↓ S3
```

**Оптимизация изображений на edge**:

- CloudFront Functions / Lambda@Edge перехватывают запрос.
- При URL `?w=320` → ресайз на edge через слой обработки изображений.
- Кеширует результат: один URL = один ресайз.

```javascript
// CloudFront Function (Viewer Request)
function handler(event) {
    var req = event.request;
    var w = req.querystring.w && req.querystring.w.value;
    if (w) {
        req.uri = req.uri.replace('.jpg', '_' + w + '.jpg');
    }
    // Accept: image/avif → перенаправить на .avif вариант
    var accept = req.headers.accept && req.headers.accept.value;
    if (accept && accept.indexOf('image/avif') >= 0) {
        req.uri = req.uri.replace('.jpg', '.avif');
    }
    return req;
}
```

**HTTP/3 (QUIC)**:

- Уменьшает head-of-line blocking на мобильных (где плохой WiFi).
- Более быстрый handshake (0-RTT для возвращающихся клиентов).

**Cache key**: `Host + URI + Accept-Header (для avif/webp)`. Cookie игнорируются (картинки не персонализированы).

**TTL**:

- Медиафайлы: 30 дней (immutable, URL содержит hash).
- HTML/JSON-ответы API: NO-CACHE или 5-30 сек (через CloudFront для GET).

Подробнее — [CDN interview](../architecture/cdn-interview.md).

---

## Q20. Viral photo: hot key и prefetch?

Вирусное фото создаёт hot key — концентрированную нагрузку на один S3-prefix, один регион origin и метаданные в Redis. Лечится распределением (рандомный суффикс ключа, multi-region origin) и упреждающим прогревом: при публикации знаменитости фото проактивно заливается на все POP-ы, пока трафик ещё не пришёл.

**Проблемы**:

1. Один S3 prefix → throttling (лимит S3: 5500 GET/sec на prefix).
2. Origin Shield (один регион) перегружен.
3. Холодные POP-ы далеко от региона публикации.

**Решения**:

- **Случайный суффикс** на S3-ключах → распределение по физическим shards внутри S3.
- **CloudFront Origin Shield** + multi-region origins.
- **Предиктивный прогрев**: при публикации знаменитости → фоновый worker делает GET по всем 300+ POP-ам (curl с разных IP или через CloudFront API).
- **Адаптивное кеширование**: фото с > 1000 RPS получает более длинный TTL и репликацию по соседним POP-ам.

```java
@EventListener(PhotoPublishedEvent.class)
public void prewarm(PhotoPublishedEvent event) {
    if (event.authorFollowers() > 1_000_000) {
        edgeWarmer.prewarmAllPops(event.photoId());
    }
}
```

**Горячий путь в Redis** (для метаданных): репликация горячих ключей через `READONLY`-команды к read-репликам, плюс локальный L1-кэш в app server (Caffeine, TTL 30 сек).

Подробнее — [Caching strategies](../architecture/caching-strategies-interview.md).

---

## Q21. Cache invalidation при delete photo?

Глобально инвалидировать CDN дорого и медленно, поэтому удаление решают на уровне источника, а не CDN: фото помечается `deleted=true` в метаданных, перестаёт показываться в ленте/профиле, а сам файл удаляется из S3 фоновым job-ом через 30 дней. Прямой `create-invalidation` оставляют на редкие исключения.

**Стратегии**:

1. **Immutable URLs** (best practice): URL содержит хеш контента, при удалении файл просто перестают раздавать. CDN отдаёт 403 после того, как origin помечает объект удалённым, либо контент остаётся (для удалённого пользователя нечего показывать).

2. **CloudFront Invalidation**:
   - `aws cloudfront create-invalidation --paths "/p/abc123*"` → дорого ($0.005 за path сверх 1000/месяц) и медленно (10-15 минут).
   - Подходит для редких случаев.

3. **Soft delete на стороне origin**: помечаем в метаданных `deleted=true`, файл S3 остаётся, но при GET через app — 404. CDN кеширует ответ 404 на короткий TTL (5 минут).

4. **Версионированный путь**: `/v2/p/abc123.jpg` — на удалении инкрементим версию.

**Что Instagram делает в реальности** (предположение):

- Soft delete в метаданных.
- CDN сохраняет контент для cache hit, но в профиле/ленте он не показывается.
- Полное удаление из S3 через batch lifecycle job спустя 30 дней (GDPR-compliant).

**GDPR, право быть забытым**:

```sql
UPDATE users SET deleted_at = now(), pii_redacted = true WHERE user_id = ?;
-- Тёгер: cascade soft-delete всех photos + invalidate CDN paths
-- Через 30 дней: hard delete с S3
```

---

## Q22. (!) Multi-region deployment и consistency?

Каждый пользователь привязан к home-региону (по IP при регистрации): туда идут все его записи, чтения берутся из локальной реплики. Между регионами — асинхронная репликация с лагом ~100ms–1s, поэтому консистентность eventual. Локальная задержка важнее мгновенной глобальной согласованности.

Архитектура. Три региона, в каждом свой стек:

- **us-east**: `App tier`, `Postgres primary`, `Redis cluster`.
- **eu-west**: `App tier`, `Postgres primary` (для EU users), `Redis cluster`.
- **apac**: `App tier`, `Postgres primary` (для APAC users), `Redis cluster`.

Связи:

- Кольцевая async-репликация Postgres cross-region: `us-east ↔ eu-west`, `eu-west ↔ apac`, `apac ↔ us-east` (двунаправленная, async repl).
- `S3 with cross-region replication` — общий; `App tier` каждого региона (us-east, eu-west, apac) → этот S3.

**Подход** — geo-partitioning по home-региону пользователя:

- Каждый пользователь приписан к региону при регистрации (по IP).
- Все записи пользователя идут в его home-регион (низкая задержка).
- Чтения предпочитают локальную реплику.
- Cross-region асинхронная репликация (Postgres logical replication, лаг ~100ms-1s).

**Модель консистентности**:

- **AP** (eventual): лента может отставать на 1-2 секунды между регионами. Приемлемо.
- **CP-like** для лайков одного пользователя: read-your-own-writes через привязку сессии к home-региону.
- **Strong** для финансовых операций (если есть, например Instagram Shop): один регион на транзакцию.

**Failover**:

- При падении us-east — DNS переключает на us-west (health check Route53).
- Записи ставятся в очередь Kafka до возврата primary, либо переключаемся на реплику → promote.

См. также [CAP theorem](../architecture/cap-theorem-interview.md) и [Consistency patterns](../architecture/consistency-patterns-interview.md).

---

## Q23. Rate limiting: загрузки и API?

Лимиты накладываются на нескольких уровнях — по IP, по пользователю и по конкретному действию — потому что разные злоупотребления требуют разных ограничений: DDoS режется на IP/edge, спам-подписки — на пользователе. Счётчики живут в Redis с TTL, алгоритм (token bucket / sliding window) подбирается под уровень.

Многоуровневое (см. [Rate limiter](design-rate-limiter-interview.md)):

| Уровень | Лимит | Алгоритм |
|---------|-------|----------|
| Глобально на IP | 1000 req/min | Token bucket в Redis |
| Загрузка фото на пользователя | 100/day | Counter + TTL |
| Загрузка story на пользователя | 50/day | |
| Лайки на пользователя | 60/min | Sliding window |
| Подписки на пользователя | 200/day (антиспам) | |
| Поиск на IP | 100/min | Edge (CloudFront) |

```java
public boolean allow(String key, int limit, Duration window) {
    String redisKey = "rl:" + key + ":" + Instant.now().getEpochSecond() / window.toSeconds();
    long count = redis.incr(redisKey);
    if (count == 1) {
        redis.expire(redisKey, window.toSeconds());
    }
    return count <= limit;
}
```

**Защита от ботов**:

- ML-классификатор поведения (скорость лайков/подписок, паттерн UA).
- CAPTCHA при повторных нарушениях.
- Репутация IP (через GuardDuty, Cloudflare).

**Мягкий vs жёсткий лимит**:

- Мягкий: показать предупреждение, троттлинг на 1 сек — не блокируем, лишь притормаживаем.
- Жёсткий: 429 Too Many Requests, временный shadow ban.

---

## Q24. Failed uploads и idempotency?

В распределённой загрузке ретраи и сбои неизбежны, поэтому каждый шаг сделан идемпотентным: повторный `finalize` не создаёт дубль (Idempotency-Key + `ON CONFLICT DO NOTHING`), оборванная загрузка докачивается через multipart, а недозавершённые файлы в S3 чистит lifecycle-rule. Цель — чтобы повтор любой операции давал тот же результат, что и первый вызов.

**Сценарии**:

1. **Обрыв сети посреди загрузки** → возобновление multipart (см. Q6).
2. **Падение worker при обработке** → exactly-once через Kafka offset + идемпотентный INSERT (с `ON CONFLICT DO NOTHING`).
3. **Двойной вызов finalize (retry)** → idempotency key:

```java
@PostMapping("/photos/{photoId}/finalize")
public Photo finalize(
    @PathVariable UUID photoId,
    @RequestHeader("Idempotency-Key") String idempKey,
    @RequestBody FinalizeRequest req
) {
    // INSERT ... ON CONFLICT (photo_id) DO NOTHING
    // если уже есть, возвращаем существующую запись
    return photoService.finalizeIdempotent(photoId, idempKey, req);
}
```

4. **Повторная загрузка одного и того же фото** → опционально хеш контента (perceptual hash):

```java
String pHash = perceptualHash(photoBytes);
Optional<Photo> existing = photoRepo.findByUserAndPHash(userId, pHash);
if (existing.isPresent()) {
    return existing.get();   // duplicate detection
}
```

5. **Очистка «осиротевших» загрузок**: фото загружено в S3, но finalize не пришёл → S3 lifecycle rule удаляет файлы с префиксом `uploads/...` старше 24 часов.

---

## Q25. AP vs CP: какой trade-off выбирает Instagram?

Instagram выбирает **AP** (Availability + Partition tolerance): соцсеть должна оставаться доступной всегда, а несколько секунд рассинхрона ленты или лайков пользователь даже не заметит. Strong consistency включают точечно — там, где цена ошибки высока (пароль, уникальность username, платежи).

**Instagram — AP**:

- Лента может показать устаревшие данные на несколько секунд → OK.
- Лайки видны не мгновенно во всех регионах → OK.
- Счётчик может быть приблизительным → OK для UI.

**Когда CP всё-таки нужен**:

- Изменение пароля / 2FA → строгая консистентность (один регион).
- Уникальность username → распределённая блокировка либо запись в один регион.
- Финансовые операции (Instagram Shop, реклама) → ACID-транзакции.

**Практический микс**:

```
read-your-own-writes: session sticky на home region pgreplica
monotonic reads:      кэширование меток в Redis (last_write_ts per user)
causal consistency:   через vector clock или dependency tracking в metadata
```

Trade-offs:

| Решение | Плюс | Минусы |
|---------|-----|------|
| Денормализация ленты | чтение O(1) | хранилище в 1000× больше, fan-out на запись дорогой |
| Eventual consistency | высокая доступность | сбивает с толку пользователей (только что лайкнул — не видно) |
| Активное кеширование | низкая задержка | сложная инвалидация кэша |
| Уникальность username в одном регионе | простой constraint | задержка для дальних регионов |

---

## Q26. Capacity planning: число серверов и рост?

Это перевод нагрузки из Q2 в железо: stateless app-tier масштабируется горизонтально (~10k инстансов в Kubernetes), под медиа и ML — отдельные CPU/GPU-пулы, под хранилища — sharded Postgres, Cassandra, Redis, ES. Главные рычаги стоимости — многоуровневое хранилище (hot S3 → cold Glacier) и сжатие на edge; запас по мощности держат +50% от пика под вирусные всплески.

**Сейчас** (приближение):

| Компонент | Кол-во | Заметка |
|-----------|--------|---------|
| App tier (stateless) | 10_000 | автомасштабирование в Kubernetes |
| Media workers | 500 | ресайз/транскодинг с большой нагрузкой на CPU |
| ML workers (GPU) | 200 | модерация + эмбеддинги |
| Postgres shards | 1_024 логических / 64 физических хоста | |
| Cassandra nodes (DMs) | 500 | Multi-DC RF=3 |
| Redis nodes | 1_000 | 100 TB горячих данных |
| Elasticsearch | 200 нод | поисковый индекс |
| Kafka brokers | 100 | RF=3, retention 30 дней |
| S3 / Glacier | 500+ PB | lifecycle: hot 90d, cold > 90d |
| CDN POPs | 300+ | CloudFront |

**Рост**:

- Пользователи: ~10% в год.
- Хранилище: ~30% в год (видео растёт быстрее фото).
- Вычисления: ~25% в год.

**Оптимизация стоимости**:

- Многоуровневое хранилище: hot → S3 Standard, cold (> 90 дней) → Glacier Deep Archive (на 90% дешевле).
- Spot-инстансы для асинхронных workers.
- Reserved-инстансы для базового app tier.
- Сжатие на edge (WebP/AVIF) экономит 30-50% исходящего трафика.

**Запас по мощности**: всегда +50% от пика, чтобы пережить вирусные события (например, публикация знаменитости → 10× обычной нагрузки).

См. [Latency numbers](../architecture/latency-numbers-interview.md) для оценки реалистичных времён.

---

## Q27. Главные lessons и pitfalls дизайна?

**Уроки**:

1. **Чтение и запись — разные системы**. Не пытаться сделать одну БД на всё.
2. **Асинхронность по умолчанию**. Любое тяжёлое действие (миниатюры, модерация, fan-out) → Kafka + worker.
3. **Предзагрузка важнее инвалидации кэша**. Толкать свежие данные в Redis при записи, а не гнаться за ними на чтении.
4. **Гео-близость важнее консистентности**. Eventual consistency между регионами — приемлемая цена.
5. **Идемпотентность везде**. На каждой точке входа — Idempotency-Key или естественный ключ.

**Подводные камни** при проектировании на интервью:

- Забыть про **проблему знаменитостей** — наивный fan-out не масштабируется.
- Положить медиафайлы в Postgres BLOB → убийца I/O.
- Одно хранилище на всё → не работает при смешанной нагрузке: много чтений + много записей + поиск.
- Игнорировать **холодный старт** (новые пользователи, новые регионы).
- Не учесть стоимость — полная репликация S3 3× в трёх регионах = 9× затрат.
- Строгая консистентность для лайков → не выдерживает QPS.
- Игнорировать модерацию — продукт не пройдёт legal/compliance.

**Что обязательно проговорить на интервью**:

- Дизайн API (хотя бы 5 эндпоинтов).
- Расчёт мощностей (числа, а не «много»).
- Один глубокий deep-dive (обычно лента или media pipeline).
- Trade-off хотя бы 2-3 решений.
- Режимы отказа (что если упадёт X?).
- Multi-region (если есть время).

---

## See also

- [System Design](system-design-interview.md) — общие подходы и фреймворк
- [Design Twitter](design-twitter-interview.md) — похожий социальный продукт, меньше акцент на media
- [Design Feed System](design-feed-system-interview.md) — детально про fan-out
- [Design Search](design-search-interview.md) — Elasticsearch и autocomplete
- [Design Chat System](design-chat-system-interview.md) — WebSocket архитектура для DM
- [Design Rate Limiter](design-rate-limiter-interview.md) — алгоритмы и реализация
- [CDN](../architecture/cdn-interview.md) — раздача media через edge
- [Caching strategies](../architecture/caching-strategies-interview.md) — write-behind, hot-key
- [Scalability patterns](../architecture/scalability-patterns-interview.md) — sharding, replication
- [Latency numbers](../architecture/latency-numbers-interview.md) — реальные времена операций
- [Database sharding](../databases/database-sharding-interview.md) — by user_id и follower_id
- [Database replication](../databases/database-replication-interview.md) — async cross-region
- [Cassandra](../databases/cassandra-interview.md) — для DMs
- [Elasticsearch](../databases/elasticsearch-interview.md) — для search
- [Redis](../databases/redis-interview.md) — feed cache, counters, stories TTL
- [Embeddings](../ai-ml/embeddings-interview.md) — для explore page
- [Kafka](../messaging/kafka-interview.md) — async event bus
- [CAP theorem](../architecture/cap-theorem-interview.md) — почему Instagram AP
- [Consistency patterns](../architecture/consistency-patterns-interview.md) — eventual, read-your-writes
