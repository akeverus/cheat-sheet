---
title: "Вопросы на собеседовании: Design Feed System"
description: "System design news feed (Twitter/Facebook/Instagram): fan-out on write vs read, celebrity problem, ranking, caching, Redis sorted sets, viral posts."
tags:
  - interview
  - system-design
  - design-feed-system
type: "interview"
difficulty: "advanced"
aliases:
  - "Design Feed System interview"
  - "News Feed design"
  - "Twitter timeline"
  - "Facebook News Feed architecture"
updated: "2026-05-26"
---

# Вопросы на собеседовании: `Design Feed System`

`Feed System` (Twitter timeline, Facebook News Feed, Instagram) — классический system design кейс. Главный trade-off: `fan-out on write` vs `fan-out on read`. Дополнительные сложности — celebrity problem, ranking, кэширование, viral posts (hot keys). Стандарт для middle/senior ролей.

## Полезные ссылки

- [Twitter — Timelines at Scale (InfoQ)](https://www.infoq.com/presentations/Twitter-Timeline-Scalability/)
- [Facebook News Feed engineering](https://engineering.fb.com/category/data-infrastructure/)
- [Instagram Engineering blog](https://instagram-engineering.com/)
- [System Design Primer — design-twitter](https://github.com/donnemartin/system-design-primer/blob/master/solutions/system_design/twitter/README.md)
- [High Scalability — Twitter architecture](http://highscalability.com/blog/2013/7/8/the-architecture-twitter-uses-to-deal-with-150m-active-users.html)
- [Designing Data-Intensive Applications, гл. 1 (Twitter case study)](https://www.oreilly.com/library/view/designing-data-intensive-applications/9781491903063/)

## Содержание

**Requirements и capacity**
- [Q1. (!) Functional и non-functional requirements?](#q1--functional-и-non-functional-requirements)
- [Q2. (!) Как оценить нагрузку и ёмкость (capacity estimation)?](#q2--как-оценить-нагрузку-и-ёмкость-capacity-estimation)
- [Q3. Какие операции считаем «read-heavy» vs «write-heavy» и почему это важно?](#q3-какие-операции-считаем-read-heavy-vs-write-heavy-и-почему-это-важно)

**Fan-out**
- [Q4. (!) Чем отличаются fan-out on write и fan-out on read?](#q4--чем-отличаются-fan-out-on-write-и-fan-out-on-read)
- [Q5. (!) Celebrity problem и как его решают?](#q5--celebrity-problem-и-как-его-решают)
- [Q6. (!) Hybrid (push+pull) подход на проде?](#q6--hybrid-pushpull-подход-на-проде)
- [Q7. Active vs inactive followers — оптимизация fanout?](#q7-active-vs-inactive-followers--оптимизация-fanout)

**Timeline storage**
- [Q8. (!) Storage для user timeline?](#q8--storage-для-user-timeline)
- [Q9. Redis sorted set для timeline?](#q9-redis-sorted-set-для-timeline)
- [Q10. Где хранить master-таблицу постов — Cassandra, DynamoDB или Postgres?](#q10-где-хранить-master-таблицу-постов--cassandra-dynamodb-или-postgres)

**Ranking и personalization**
- [Q11. (!) Хронологическая лента или алгоритмическая (chronological vs algorithmic)?](#q11--хронологическая-лента-или-алгоритмическая-chronological-vs-algorithmic)
- [Q12. Ranking features и signals?](#q12-ranking-features-и-signals)
- [Q13. (!) ML pipeline для ranking?](#q13--ml-pipeline-для-ranking)
- [Q14. Cold start для нового пользователя?](#q14-cold-start-для-нового-пользователя)
- [Q15. Cold start для нового поста (нет engagement signals)?](#q15-cold-start-для-нового-поста-нет-engagement-signals)

**Architecture**
- [Q16. (!) Как выглядит архитектура верхнего уровня (high-level)?](#q16--как-выглядит-архитектура-верхнего-уровня-high-level)
- [Q17. Как устроен flow создания поста?](#q17-как-устроен-flow-создания-поста)
- [Q18. Read (timeline fetch) flow и latency budget?](#q18-read-timeline-fetch-flow-и-latency-budget)
- [Q19. Real-time-обновления: long-polling, SSE или WebSocket?](#q19-real-time-обновления-long-polling-sse-или-websocket)

**Scalability**
- [Q20. (!) Как обрабатывать аккаунты с миллионами подписчиков?](#q20--как-обрабатывать-аккаунты-с-миллионами-подписчиков)
- [Q21. Какой выстроить стратегию кэширования (L1/L2/L3)?](#q21-какой-выстроить-стратегию-кэширования-l1l2l3)
- [Q22. DB sharding для posts/users/timelines/graph?](#q22-db-sharding-для-postsuserstimelinesgraph)

**Production**
- [Q23. (!) Вирусные посты — проблема hot key?](#q23--вирусные-посты--проблема-hot-key)
- [Q24. Feed freshness vs latency trade-off и SLO?](#q24-feed-freshness-vs-latency-trade-off-и-slo)
- [Q25. Block/mute/hide — как влияют на feed?](#q25-blockmutehide--как-влияют-на-feed)
- [Q26. (!) Как развернуть систему в нескольких регионах (multi-region)?](#q26--как-развернуть-систему-в-нескольких-регионах-multi-region)
- [Q27. A/B testing платформа для feed changes?](#q27-ab-testing-платформа-для-feed-changes)
- [Q28. Throttling и backpressure при spike?](#q28-throttling-и-backpressure-при-spike)
- [Q29. Стоимость инфры: на чём экономим?](#q29-стоимость-инфры-на-чём-экономим)
- [Q30. (!) Антипаттерны и подводные камни?](#q30--антипаттерны-и-подводные-камни)

## Q1. (!) Functional и non-functional requirements?

С требований начинается любой system design: они задают границы и оправдывают каждое последующее решение. Сначала проговорите, ЧТО система делает (функциональные требования), затем — НАСКОЛЬКО хорошо (нефункциональные), и явно очертите, что в скоуп не входит.

**Функциональные (ядро скоупа):**
- Пользователь публикует текст/медиа.
- Пользователь видит home feed — посты от тех, на кого подписан.
- Лайки, комментарии, репосты (engagement).
- Ранжирование: хронологическое или алгоритмическое.
- Бесконечный скролл + pull-to-refresh.
- Push-уведомления на упоминание/лайк.

**Нефункциональные:**
- Read-heavy (~100:1, чтения к записям).
- p99 latency загрузки feed < 200 ms.
- Доступность 99.99% (≈ 53 минуты простоя в год).
- Согласованность: достаточно `eventual` (лаг 5-10 секунд на feed допустим).
- Масштабируемость: миллиарды пользователей, сотни миллионов DAU.
- Долговечность: посты — `nines of 9`, потерю timeline cache допускаем.

**Что вне скоупа (проговорить явно):**
- Личные сообщения (отдельный кейс).
- Live-видеостриминг.
- Платформа рекламы/монетизации.
- Полная модерация контента.

**Совет:** список скоупа — первое, что слушает интервьюер. Без него capacity estimation повисает в воздухе.

## Q2. (!) Как оценить нагрузку и ёмкость (capacity estimation)?

Цель прикидки — превратить «миллиарды пользователей» в конкретные QPS, гигабайты и доллары, чтобы обосновать выбор технологий. Метод простой: берёте 2-3 базовых допущения (DAU, постов в день, среднее число подписок) и из них выводите throughput, хранилище и сеть.

Допущения масштаба Twitter/X (2026):

| Параметр | Значение |
|---|---|
| DAU | 500M |
| Постов в день | 200M (записи) |
| Среднее число подписок на пользователя | 200 |
| Среднее число чтений | 10 загрузок feed на пользователя в день → 5B чтений/день |

**Пропускная способность:**
- Записи: 200M / 86 400 ≈ **2 300 постов/сек**.
- Чтения: 5B / 86 400 ≈ **58 000 загрузок/сек** (пик ×3 — 175 K/сек).

**Влияние fan-out (push-модель):**
- 2 300 постов/сек × 200 подписчиков = **460 000 записей в timeline/сек** — то есть одна запись поста порождает сотни записей в кэш.
- Это средняя цифра: реальное распределение перекошено celebrities (миллионы подписчиков на пост), поэтому хвост нагрузки куда тяжелее среднего (см. Q5).

**Хранилище:**
- Посты: 200M × 300 B = 60 GB/день сырых данных.
- За 5 лет: ~110 TB сырых; + индексы + 3× репликация = 300-400 TB.
- Timeline cache (top 1M активных × 1000 ID × 80 B на запись sorted-set) ≈ **80 GB Redis**.

**Пропускная способность сети:**
- Загрузка feed: 58 K/s × 100 постов × 200 B = ~1.2 GB/s исходящего трафика (без медиа).
- Медиа отдаются через CDN — это отдельный вопрос.

**Стоимость (порядок):**
- Redis cluster ~$50K/месяц, Cassandra ~$100K/месяц, CDN — $0.5-1M/месяц (трафик доминирует).

## Q3. Какие операции считаем «read-heavy» vs «write-heavy» и почему это важно?

Feed-система — резко read-heavy: чтений ленты примерно в 100 раз больше, чем записей постов. Это важно, потому что профиль нагрузки диктует архитектуру: под чтения оптимизируют (кэш, реплики, предвычисленные timeline), а записи можно делать дороже. Разнесём операции по типам:

| Операция | Тип | QPS (порядок) |
|---|---|---|
| Создание поста | запись | 2 K/сек |
| Лайк/комментарий | запись | 50-100 K/сек (пик) |
| Загрузка feed | чтение | 60-200 K/сек |
| Просмотр профиля | чтение | 30 K/сек |
| Поиск | чтение | 10 K/сек |

**Вывод:** read-heavy примерно в 100 раз. Из этого вытекают решения:
- Интенсивное кэширование (Redis + CDN) — стандарт.
- Read-реплики Cassandra (`LOCAL_QUORUM` на запись, `ONE` на чтение).
- Fan-out on write — оптимизирует чтение за счёт записи (если запись дешевле — выгодно).

**Контрпример:** чат Slack — write-heavy, fan-out не нужен, fetch отдаёт хронологию из одного потока канала.

## Q4. (!) Чем отличаются fan-out on write и fan-out on read?

Это центральный trade-off feed-системы: вопрос в том, КОГДА собирать ленту — в момент публикации поста или в момент его чтения. Push (fan-out on write) раскладывает пост по timeline всех подписчиков сразу при записи; pull (fan-out on read) ничего заранее не раскладывает и собирает ленту на лету при запросе. По сути это перенос работы между записью и чтением: push делает чтение дешёвым ценой дорогой записи, pull — наоборот.

**Push (fan-out on write):**
- Пользователь A публикует пост → fanout worker записывает `post_id` в timeline каждого подписчика.
- Чтение: `LRANGE user:42:timeline 0 49` — O(1).

```
A posts → kafka(posts_created)
              ↓
         fanout worker
              ↓
   ZADD follower_1:timeline <ts> post_id
   ZADD follower_2:timeline <ts> post_id
   ... (×N followers)
```

| Плюсы | Минусы |
|---|---|
| Чтение O(1), стабильные p99 | Write amplification (10M подписчиков = 10M записей) |
| Простая модель кэширования | Бесполезная работа для неактивных подписчиков |
| Легко добавить ranking offline | Storage amplification (×fan-out) |

**Pull (fan-out on read):**
- Пользователь A публикует пост → пишет в свою таблицу `user_posts`.
- Чтение: для пользователя B — fetch `user_posts` каждого из 200 подписок, merge-sort по времени.

| Плюсы | Минусы |
|---|---|
| Дешёвые записи (O(1)) | Дорогое чтение (200 запросов + merge) |
| Нет amplification | Высокая latency для активных пользователей |
| Проблема celebrity исчезает | Сложно применять ranking online |

**Вывод:** ни один подход не масштабируется в чистом виде — на проде это гибрид (Q6).

## Q5. (!) Celebrity problem и как его решают?

Celebrity problem — это место, где чистый push ломается: один аккаунт с миллионами подписчиков превращает каждый пост в лавину записей. Суть решения везде одна — для таких аккаунтов push выключают и переходят на pull (подписчик подтягивает их посты при чтении). Ниже — почему это нужно и как именно делается.

**Проблема:** пользователь с 10M+ подписчиков (`@elonmusk`, бренд).
- Fan-out on write: каждый пост = 10M вставок в timeline.
- Всплеск: пост за минуту → 10M записей/мин = 167 K записей/сек — ради одного поста.
- Hot key в Redis cluster, очередь fanout распухает.

**Решения:**

**1. Определение по порогу.** `followers_count > 100 000` → пользователь-celebrity, полностью пропускаем write-fanout.

**2. Pull на чтении.** Подписчик при загрузке feed дополнительно пуллит из `celebrity_posts` (отдельное хранилище, высокий cache hit), сливает с push-timeline.

**3. Async с приоритетами.** Сначала fanout к активным подписчикам (заходили < 24 ч), неактивные обрабатываются позже или вообще не получают fanout.

**4. CDN/edge cache.** Тело поста кладётся в CDN — миллион подписчиков читают его не из БД.

**5. Прогрев заранее.** На пост celebrity сразу прогревается edge-кэш в каждом регионе.

**Twitter (исторически):**
- Сервис `Timelines` (Scala) делал fanout, для топовых аккаунтов применялось правило «skip and pull».
- На чтении merge-sort объединял push-timeline + спулленные celebrity posts.

## Q6. (!) Hybrid (push+pull) подход на проде?

На проде не выбирают push ИЛИ pull — берут оба и переключаются по типу автора. Обычных пользователей раскладывают push'ем (дешёвое чтение), celebrities обслуживают pull'ом (без write-амплификации), а на чтении лента собирается слиянием обоих источников. Граница между «обычным» и «celebrity» — это настраиваемый порог по числу подписчиков.

**Сторона записи:**
- Обычные пользователи (< 10 K подписчиков) → push: fanout в timeline подписчиков.
- Celebrities (> 100 K подписчиков) → без push: только в собственный `user_posts`.
- Граница (10-100 K) — настраивается через A/B.

**Сторона чтения:**
```
GET /feed?cursor=<ts> ─► Feed Service
   ├─► fetch push-timeline (Redis ZREVRANGEBYSCORE)
   ├─► fetch celebrity posts user follows (per-celebrity cache)
   ├─► merge + rank
   ├─► batch fetch post bodies (MGET)
   └─► enrich (author, media URL) → return
```

**Настройка:**
- Дробный fanout: на 1% подписчиков сразу, остальное async с retry.
- `inactive_threshold` (90 дней без входа) → не делаем fanout; на входе делаем backfill timeline.

**Матрица решений:**
| Сценарий | Подход |
|---|---|
| Стартап < 1M MAU | Чистый push, без определения celebrity |
| Соцсеть 100M+ MAU | Гибрид (push для обычных + pull для celebs) |
| Масштаб Twitter/X | Гибрид + multi-region + edge caching |

## Q7. Active vs inactive followers — оптимизация fanout?

Идея оптимизации: не делать fan-out тем подписчикам, которые всё равно не зайдут смотреть ленту. Большая доля подписчиков в любой момент неактивна, и запись timeline для них — выброшенная работа и память. Поэтому подписчиков делят на tier'ы по давности визита и обслуживают по-разному: активным push'им сразу, неактивным — собираем ленту лениво на следующем входе.

**Метрика активности:** `last_login_at` (или `last_feed_fetch_at`).

**Стратегии:**
- **Hot tier** (< 7 дней без визита) → fanout всегда, высокий приоритет в Kafka.
- **Warm tier** (7-30 дней) → fanout с задержкой 30-60 сек, в batch-режиме.
- **Cold tier** (> 30 дней) → без fanout; на следующем визите делаем `lazy backfill` — пуллим последние N постов от каждой подписки.

**Эффект:** Twitter в докладах раскрывал — 50-70% подписчиков целевой аудитории в данный момент неактивны, fanout к ним = бесполезные записи.

**Поток lazy backfill:**
1. Пользователь входит после долгого отсутствия → флаг `timeline_stale=true`.
2. Async-задача собирает посты от всех подписок за последний месяц.
3. Заполняет ZSET timeline, снимает флаг.
4. UI показывает skeleton/loading 1-2 сек.

## Q8. (!) Storage для user timeline?

Timeline хранят в два уровня: горячий кэш в Redis (sorted set с ID постов, отдаёт ленту за миллисекунды) и долговременный бэкенд в Cassandra (на случай потери кэша). Ключевой принцип — timeline хранит только `post_id`, а тела постов лежат отдельно; это убирает дублирование контента и развязывает редактирование поста от ленты.

**Timeline cache на пользователя (Redis):**
```
ZADD user:42:timeline <timestamp> <post_id>
ZREVRANGE user:42:timeline 0 49
ZREMRANGEBYRANK user:42:timeline 0 -1001  # keep top 1000
```
- Sorted set, score = timestamp.
- Вставка O(log N), range-fetch O(log N + M).
- 80 B на запись × 1000 × 1M пользователей = 80 GB.

**Постоянное хранилище-бэкенд (Cassandra):**
```
CREATE TABLE timelines (
  user_id bigint,
  ts timeuuid,
  post_id bigint,
  PRIMARY KEY (user_id, ts)
) WITH CLUSTERING ORDER BY (ts DESC);
```
- Partition по `user_id`, clustering по `ts`.
- Range-scan по пользователю: один partition.
- Настройка compaction (TWCS — Time Window Compaction Strategy) для time-series.

**Тела постов — отдельное хранилище:**
- Таблица `posts`: partition по `post_id`, контент + media URL.
- Timeline хранит только `post_id`; на чтении — batch MGET.

**Зачем разделение:**
- Изменение поста (edit/delete) — одна запись в `posts`, timeline не трогаем.
- Дедупликация: один пост × N подписчиков = N записей в timeline, но 1 тело.

## Q9. Redis sorted set для timeline?

Sorted set идеально ложится на timeline, потому что лента — это упорядоченный по времени набор ID: score = timestamp, member = `post_id`. Это даёт вставку и чтение диапазона за O(log N), пагинацию по курсору и тривиальную подрезку старого хвоста — ровно те операции, которые нужны ленте.

**API:**
```
ZADD user:42:timeline <ts_score> <post_id>      # вставка
ZREVRANGE user:42:timeline 0 49 WITHSCORES      # top 50 по времени
ZREVRANGEBYSCORE user:42:timeline <cursor> -inf LIMIT 0 50  # пагинация по курсору
ZREMRANGEBYRANK user:42:timeline 0 -1001        # trim до 1000
```

**Память:**
- Одна запись skiplist + ziplist ≈ 64-80 B.
- 1000 × 80 B = 80 KB на пользователя.
- 1M активных × 80 KB = 80 GB → один шард `r6gd.4xlarge` (128 GB).
- 100M активных → нужен Redis cluster (16-32 master-шарда).

**Вытеснение (eviction):**
- `maxmemory-policy allkeys-lru` глобально.
- Или TTL на конкретный ZSET (24-72 ч); неактивный timeline регенерируется при входе.

**Persistence:**
- AOF раз в 1 сек — допустимая потеря (timeline восстанавливается из `posts` за минуты).
- RDB snapshot 1×/час — для disaster recovery.

**Тонкость:** при `ZADD` поверх миллионов sorted-set-ов pipelining обязателен, иначе RTT убивает throughput.

## Q10. Где хранить master-таблицу постов — Cassandra, DynamoDB или Postgres?

Доступ к постам простой — почти всегда чтение по `post_id`, без сложных join'ов, — поэтому выбор сводится к write-throughput и операционной модели, а не к богатству запросов. На больших масштабах побеждает Cassandra (огромный write-throughput, multi-DC из коробки); DynamoDB берут, когда не хотят сами админить кластер; Postgres — разумный старт для стартапа до миграции.

| Свойство | Cassandra | DynamoDB | Postgres (sharded) |
|---|---|---|---|
| Write throughput | очень высокий | очень высокий (RCU/WCU) | средний |
| Latency p99 на запись | 5-10 ms | 5-15 ms | 10-30 ms |
| Операционная стоимость | админят сами | managed | managed (RDS) |
| Гибкость схемы | средняя | средняя | низкая (нужны миграции) |
| Модель запросов | по PK + clustering | по PK + sort key + GSI | SQL |
| Multi-region | active-active (`LOCAL_QUORUM`) | global tables | logical replication, сложно |

**Выбор:**
- **Twitter, Discord** → Cassandra (write-throughput + проверено временем).
- **Lyft, неядровые системы Stripe** → DynamoDB (когда нет команды под Cassandra).
- **Стартап до 10M MAU** → Postgres (CitusData/Aurora), позже миграция.

**Схема (Cassandra):**
```sql
CREATE TABLE posts (
  post_id bigint PRIMARY KEY,
  user_id bigint,
  body text,
  media_ids list<bigint>,
  created_at timestamp,
  ...
);
```
Один partition = один пост, идеально для произвольного доступа.

## Q11. (!) Хронологическая лента или алгоритмическая (chronological vs algorithmic)?

Это выбор между предсказуемостью и engagement. Хронологическая лента просто показывает свежее сверху — честно и понятно, но при сотнях подписок интересное тонет. Алгоритмическая ранжирует посты ML-моделью по релевантности — поднимает engagement, но порождает filter bubble и недоверие («почему я это вижу?»). Технически разница в одном шаге: алгоритмическая добавляет стадию ранжирования (Q13), хронологическая обходится `ZREVRANGE` без неё.

**Хронологический (reverse-chrono):**
- Самые свежие сверху.
- Простая модель, предсказуемо, пользователь сам контролирует.
- Минус: при 500 подписках пост от 09:00 утра «утонет» к вечеру.

**Алгоритмический:**
- ML-ранжирование по сигналам релевантности (engagement, affinity, recency decay).
- Выше engagement (Instagram добавил +20% time-on-app, 2016).
- Минусы: filter bubble, фрустрация «почему я это вижу?».

**Реальные продукты:**
- Instagram (с 2016), Facebook (EdgeRank → ML), TikTok (For You) — алгоритмические.
- Twitter/X — гибрид: `For You` (алго) + `Following` (хроно).
- Threads, Bluesky — хроно по умолчанию.

**Реализация:**
- Алгоритмический = генерация кандидатов + ranking-модель (Q13).
- Хроно = просто `ZREVRANGE` без стадии ранжирования.

## Q12. Ranking features и signals?

Сигналы ранжирования отвечают на вопрос «насколько этот пост релевантен именно этому пользователю прямо сейчас». Их группируют по источнику: свежесть поста, его engagement, близость пользователя к автору (affinity), история самого пользователя, тип медиа и — отдельно важные — негативные сигналы. Модель комбинирует их в один score.

**Категории признаков:**

**Свежесть (recency):**
- `age_minutes`, `exp(-age/decay)`.

**Engagement (на пост):**
- Лайки, комментарии, репосты, click-through.
- Нормализовано по возрасту поста и числу подписчиков автора.

**Affinity (пользователь × автор):**
- Прошлые взаимодействия: лайки, ответы, визиты в профиль, личные чаты.
- `pmi(user, author)` — pointwise mutual information.

**История пользователя:**
- Эмбеддинги по последним 100 просмотренным постам.
- Тематические предпочтения (спорт, технологии, политика).

**Вес по типу медиа:**
- Видео > изображение > текст (по engagement).
- Dwell time (длительность просмотра).

**Негативные сигналы:**
- Скрытие, «не интересно», отписка, mute.
- Сильный отрицательный вес.

**Feature store:** Feast / Tecton / Michelangelo (Uber) — централизованное хранилище для согласованности offline + online.

**Пример EdgeRank (упрощённо):**
```
score = Σ_e (affinity_e × weight_e × time_decay_e)
```
где `e` — ребро (лайк, комментарий, репост, ...).

## Q13. (!) ML pipeline для ranking?

Пайплайн делится на две части. Offline (асинхронно, не в запросе): логи взаимодействий → фичи → обучение → eval → выкладка модели в registry. Online (внутри запроса, под latency-бюджетом): сгенерировать кандидатов → достать фичи → проскорить моделью → вернуть top-K. Критичный приём — генерация кандидатов: она сужает входы ranker'а с сотен тысяч до ~1000 постов, иначе инференс не уложится в latency.

```
┌──────────── OFFLINE ────────────┐
│ 1. Engagement logs → Kafka      │
│ 2. Spark/Flink → feature store  │
│ 3. Train (TensorFlow/PyTorch)   │
│ 4. Eval (offline AUC, NDCG)     │
│ 5. Push model → registry        │
└──────────────────────────────────┘
            ↓ model artifact
┌──────────── ONLINE ─────────────┐
│ Feed request                    │
│   ├─► candidate generation      │
│   │     (1000 posts: push       │
│   │      timeline + celebs +    │
│   │      trending + ads)        │
│   ├─► featurize (online store)  │
│   ├─► ranking model (top-K)     │
│   └─► return top 50             │
│       ↓                         │
│   client interaction → kafka    │
└──────────────────────────────────┘
```

**Генерация кандидатов:** ~1 K кандидатов из push-timeline + celebrity-pull + trending + внедрённой рекламы. Без этого шага ranker обрабатывал бы 100K+ постов — не уложится в latency.

**Ranking-модель:**
- Глубокая модель (DLRM, Wide&Deep, transformer).
- Инференс: 1-5 ms на запрос, батчами.
- Serving: TensorFlow Serving / TorchServe / Triton, GPU для тяжёлых моделей.

**A/B:**
- Новая модель → 0.5-1% трафика → метрики (DAU, session_time, complaint_rate).
- Холдаут-группа `control` всегда есть.

**Непрерывное обучение:**
- Онлайн-обновления через streaming (Flink) для свежих сигналов (горячие тренды).
- Полный rebuild — ежедневно.

## Q14. Cold start для нового пользователя?

**Проблема:** новый пользователь, подписок мало, сигналов для персонализации нет.

**Стратегии:**
- **Редакционные дефолты.** Команда кураторов выбирает 50 «качественных» аккаунтов (новости, авторитетные блоги) → onboarding-визард.
- **Онбординг по темам.** При регистрации просим выбрать темы (спорт, технологии, музыка) → feed = trending-посты из этих тем.
- **По геолокации.** Geo-IP → trending в стране/городе.
- **По демографии.** Возраст/пол → коллаборативно похожие профили.
- **Trending feed.** Просто top-N глобального trending — пока не накопятся сигналы.
- **Рекламные / discoverable-строки.** «Кого вам подписаться» в feed.

**Метрика успеха:** D1 retention (вернулся ли пользователь через сутки). Cold-start-стратегия = главный рычаг.

## Q15. Cold start для нового поста (нет engagement signals)?

**Проблема:** пост только опубликован, лайков 0, ranker не знает, что с ним делать.

**Стратегии:**
- **Оценка качества автора.** Средний engagement автора → стартовый score нового поста.
- **Только контентные признаки.** Эмбеддинг текста + медиа → схожесть с прошлыми «заходящими» постами.
- **Бонус за исследование.** Multi-armed bandit: бустим новые посты на ~5% показов, чтобы собрать сигналы.
- **Неявные сигналы.** Time-to-first-like, dwell time, scroll-past rate — собираем за первые 10 минут.
- **Бейзлайн по когорте.** Похожие посты того же автора в прошлом → ожидаемый engagement.

**Ловушка:** если ranker полностью отвергает новые посты, появляется «rich get richer» — старые посты доминируют, новые не получают шанса. Нужен `epsilon-greedy` или Thompson sampling.

## Q16. (!) Как выглядит архитектура верхнего уровня (high-level)?

Систему удобно представить как два пути, разделённых через Kafka. Путь записи: клиент → API Gateway → Post Service → Posts DB + эмит события в Kafka → fanout-воркеры раскладывают timeline. Путь чтения: клиент → Feed Service, который собирает push-timeline из Redis, подтягивает celebrity-посты, ранжирует и обогащает. Kafka между ними развязывает запись от чтения и даёт async-fanout.

```mermaid
graph LR
  C[Mobile / Web Clients]
  LB[Load Balancer / CDN]
  API[API Gateway]
  Feed[Feed Service]
  Post[Post Service]
  User[User Service]
  Rank[Ranking Service]
  FO[Fanout Workers]
  K[(Kafka)]
  Posts[(Posts DB Cassandra)]
  UserDB[(User DB Postgres)]
  Graph[(Graph DB)]
  Redis[(Redis Cluster Timelines)]
  Media[(S3 Media)]
  ES[(Elasticsearch Search)]
  Flink[Flink Stream Aggregator]

  C --> LB --> API
  API --> Feed
  API --> Post
  Post --> Posts
  Post --> K
  K --> FO
  FO --> Redis
  FO --> ES
  Feed --> Redis
  Feed --> Rank
  Feed --> Posts
  Feed --> User
  User --> UserDB
  User --> Graph
  K --> Flink
  Flink --> Posts
```

**Ключевые сервисы:**
- `Feed Service` — путь чтения, объединяет push-timeline + celebrity-pull + ранжирование.
- `Post Service` — путь записи, валидация, сохранение, эмит в Kafka.
- `Fanout Workers` — async-консьюмеры, заполняют Redis-timeline-ы.
- `Ranking Service` — gRPC, выдаёт scores; модель из registry.
- `Graph DB` — граф подписок (Neo4j или шардированная MySQL-таблица `edges`).

## Q17. Как устроен flow создания поста?

Главный принцип flow — синхронно делаем только минимум (валидация + запись поста + ответ клиенту), а всё тяжёлое (fanout, индексация, модерация, аналитика) выносим в async-консьюмеры Kafka. Поэтому клиент получает `201` мгновенно и видит свой пост (оптимистичный UI), а подписчики — через несколько секунд, когда отработает fanout.

```
1. POST /posts {body, media_ids}
2. Auth check → API Gateway → Post Service.
3. Post Service:
   a. Validate (length, banned content basic check).
   b. INSERT INTO posts (...).
   c. Publish to Kafka `posts.created`.
   d. Respond 201 to client (optimistic UI).
4. Async consumers (parallel):
   - Fanout worker → ZADD в timeline активных followers (skip celebrity).
   - Search indexer → POST в Elasticsearch.
   - Moderation pipeline → ML toxicity check.
   - Analytics → BigQuery via Flink.
5. Через 1-5 сек пост виден в feed подписчиков.
```

**Бюджет latency:**
- Пользователь видит свой пост сразу (оптимистичный UI).
- Подписчики видят: SLO p95 < 5 сек, p99 < 30 сек.

**Идемпотентность:**
- Клиент отправляет `Idempotency-Key` (UUID v4) — защита от двойной отправки.
- На Post Service — `INSERT ... ON CONFLICT DO NOTHING`.

**Backpressure:**
- Если lag Kafka > N сек, переключаем fanout в degraded-режим (только активный hot tier).

## Q18. Read (timeline fetch) flow и latency budget?

Чтение ленты — это конвейер из ~9 шагов, и каждый обязан уложиться в свою долю latency-бюджета (здесь — 200 ms p99). Логика: достать push-timeline из Redis → подтянуть celebrity-посты → слить кандидатов → проранжировать → взять тела постов → обогатить → отфильтровать заблокированных → отдать. Расписав бюджет по шагам, сразу видно, что ранжирование — самая дорогая стадия и первый кандидат на отключение под нагрузкой.

```
GET /feed?cursor=<ts_ms>
   ↓
API Gateway → Feed Service:
   1. ZREVRANGEBYSCORE user:42:timeline <cursor> -inf LIMIT 0 200  (Redis ~5ms)
   2. fetch celebrity posts followed by user (per-celeb cache ~30ms)
   3. merge candidates (in-memory)
   4. ranking RPC (gRPC to Ranking Service ~50ms, GPU inference batched)
   5. select top-50
   6. MGET posts:<id1>,<id2>,... (Cassandra ~15ms via cache)
   7. enrich (author, media URLs) — batch ~10ms
   8. apply blocked/muted filter
   9. return JSON
```

**Бюджет latency 200 ms p99:**
| Этап | ms |
|---|---|
| TLS + auth | 5 |
| Чтение timeline | 5 |
| Celebrity pull | 30 |
| Ранжирование | 50 |
| Тела постов | 15 |
| Обогащение (enrichment) | 20 |
| Фильтр | 5 |
| Сериализация + egress | 20 |
| Буфер | 50 |
| **Итого** | **200** |

**Что съедает бюджет:**
- Ранжирование — самое тяжёлое. Без него можно отдавать хроно за 30 ms.
- Холодный кэш: пагинация далеко в прошлое → чтение Cassandra 50-100 ms.

## Q19. Real-time-обновления: long-polling, SSE или WebSocket?

Три транспорта различаются направленностью и стоимостью соединения. Long polling прост, но не тянет сотни миллионов коннектов; SSE даёт односторонний push сервер→клиент поверх обычного HTTP; WebSocket — полноценное двустороннее соединение, но дорогое в удержании. Для feed на проде типична связка: push-уведомления через FCM/APNs, а внутри приложения — WebSocket, который шлёт лёгкий сигнал «есть N новых», после чего клиент тянет саму ленту обычным запросом (Q18).

**Long polling:**
- Клиент → GET `/feed/updates?since=<ts>` → сервер держит соединение до новых данных / таймаута.
- Плюсы: простая инфраструктура, работает через любой прокси.
- Минусы: HTTP-оверхед, не масштабируется на 100M-1B соединений.

**Server-Sent Events (SSE):**
- Односторонний push (сервер → клиент) поверх обычного HTTP.
- Плюсы: проще WebSocket, нативный browser API, работает через прокси.
- Минусы: только текст, без двусторонней связи.

**WebSocket:**
- Двустороннее, постоянное соединение.
- Плюсы: низкий оверхед, real-time лайки/комментарии.
- Минусы: дорого держать миллионы коннектов; нужен sticky LB; нюансы переподключения.

**Продакшен-стек:**
- Push-уведомления (mobile) — FCM / APNs.
- Real-time внутри приложения — WebSocket (Phoenix, Centrifugo, Soketi или собственный Go-сервер).
- Один сервер держит 100K-1M простаивающих WebSocket-ов при правильном тюнинге (`ulimit`, `SO_REUSEPORT`).

**Паттерн:**
- WebSocket-уведомления приходят как «у тебя 5 новых постов» (badge).
- Пользователь тапает → GET `/feed` обычным потоком (Q18).

## Q20. (!) Как обрабатывать аккаунты с миллионами подписчиков?

Базовый ответ — гибрид push+pull (Q5) и отсечение неактивных (Q7). Но даже когда celebrity вынесены на pull, у обычного популярного автора остаются миллионы активных подписчиков, и сам fanout нужно масштабировать инженерно. Ключевые приёмы — параллелить воркеры по партициям Kafka, батчить записи в Redis и держать timeline подрезанным.

См. также Q5 (celebrity), Q7 (активные/неактивные). Дополнительно:

**1. Шардированный fanout.**
- Kafka-топик с N=1000 партициями, key=`follower_id`.
- N воркеров консьюмят параллельно, каждый отвечает за свою «полосу» подписчиков.
- Линейное масштабирование.

**2. Batch-записи в Redis.**
- Один воркер аккумулирует 1000 `ZADD` в pipeline → одна RTT.
- Throughput ×1000.

**3. Async + retry с идемпотентностью.**
- При сбое Redis воркер делает retry — `ZADD` идемпотентен (тот же score+member).

**4. Подрезка timeline.**
- ZSET ограничен top-1000; при вставке → `ZREMRANGEBYRANK 0 -1001`.
- Неактивная часть сама вылетает.

**5. Fanout с учётом региона.**
- Подписчики распределены по регионам — fanout локально внутри региона.
- Cross-region async через Kafka MirrorMaker.

**6. Предвычисление в непиковое время.**
- Часть fanout откладываем на 30-60 сек (низкий приоритет); если пост стал viral — мгновенно бустим всех активных.

## Q21. Какой выстроить стратегию кэширования (L1/L2/L3)?

Кэш в feed-системе многоуровневый: каждый слой отрезает часть трафика, не доводя его до БД. Чем ближе слой к пользователю, тем дешевле обслуживание и короче TTL. Снизу вверх по нагрузке: клиент → CDN/edge → Redis → in-process-кэш приложения → БД как источник правды. Отдельно важна стратегия инвалидации и защита от cache stampede.

**L1 — Клиент (mobile/web).**
- Недавно просмотренные посты, профили.
- Размер кэша: ~5-10 MB.
- TTL: 5-15 минут.

**L2 — CDN / edge (статика + публичный контент).**
- Публичные профили, превью медиа.
- TTL: 1-6 часов.
- Инвалидация: cache-buster в URL (`?v=<hash>`).

**L3 — Redis (горячие данные).**
- Timeline пользователя (ZSET).
- Тела постов (hash, TTL 1 ч).
- Профили пользователей (hash, TTL 1 ч).
- Счётчики (лайки/комментарии) — write-through + периодический flush.

**L4 — Локальный кэш приложения (in-process).**
- In-memory кэш ranking-модели (декодированные признаки).
- Caffeine / Guava cache на JVM.

**L5 — БД (источник правды).**
- Cassandra (посты), Postgres (пользователи).

**Инвалидация:**
- Редактирование поста → публикуем `post.updated` → fanout-инвалидация кэша поста + re-push в затронутые timeline-ы.
- Изменение подписки → инвалидация timeline-кэша пользователя (регенерация при следующем визите).

**Cache stampede:**
- `singleflight` (Go) / Caffeine `Loader` — один поток грузит, остальные ждут.
- Флаг `XX` на Redis SET (обновляем только если ключ уже есть).

## Q22. DB sharding для posts/users/timelines/graph?

Ключ шардирования выбирают так, чтобы самый частый запрос попадал в один шард, а нагрузка распределялась ровно. Отсюда разные ключи для разных сущностей: посты шардируют по `post_id` (произвольный доступ + ровное распределение), а «все посты/timeline/уведомления пользователя X» — по `user_id`, чтобы запрос не разлетался по шардам. Граф подписок денормализуют в обе стороны, потому что нужны и подписчики, и подписки.

| Сущность | Ключ шардирования | Почему |
|---|---|---|
| `posts` | `post_id` (hash) | Произвольный доступ; ровное распределение |
| `posts_by_user` (денорм.) | `user_id` | «Все посты пользователя X» — один shard |
| `users` | `user_id` (hash) | Идентификатор-PK |
| `timelines` (Redis) | `user_id` | Все операции с timeline одного пользователя — один shard |
| `follows` (edges) | `follower_id` ИЛИ `followed_id` | Часто нужны обе стороны — два денорм-индекса |
| `notifications` | `recipient_id` | Все уведомления пользователя — один shard |

**Граничные случаи:**
- Cross-shard-запрос (например, «топ постов по миру») → MapReduce / стрим из Kafka в OLAP (BigQuery).
- Решардинг (когда shard переполнен) → consistent hashing или Vitess `Reshard`.

**Инструменты:**
- Vitess (YouTube), Citus (Postgres), ProxySQL.
- Cassandra/Dynamo — шардирование встроено (token ring / partition key).

## Q23. (!) Вирусные посты — проблема hot key?

Hot key — это когда один ключ (вирусный `post_id` или его счётчик лайков) собирает непропорционально весь трафик и упирается в один Redis-шард: горизонтальное масштабирование не помогает, потому что нагрузка не распределяется. Лечат это размазыванием ключа: реплицируют hot key по нескольким шардам на чтение, шардируют счётчик на запись, а тяжёлый подсчёт выносят в async-агрегацию ценой небольшой устаревшести цифры.

**Симптомы:**
- Один `post_id` читают 1M+ раз/мин → Redis-shard на пределе.
- Счётчик лайков `INCR post:42:likes` — 100 K записей/сек в один ключ → contention.

**Митигации:**

**1. Реплицировать hot key.**
- Распознали viral (counter > threshold) → копируем в N=10 шардов.
- Клиент случайно выбирает shard для чтения.

**2. Шардирование счётчика.**
- `INCR post:42:likes:shard_<rand 0..15>` (запись на любой из 16).
- Чтение = `SUM(post:42:likes:shard_*)`.

**3. Async-агрегация.**
- Лайки → Kafka → оконная агрегация Flink → финальный счёт в Redis 1×/сек.
- Пользователь видит слегка устаревшее значение, но система не падает.

**4. CDN/edge cache для содержимого поста.**
- Тело статично → TTL 60 сек, миллионы чтений идут на edge, а не на origin.

**5. Вероятностный подсчёт.**
- HyperLogLog для уникальных просмотров («1.2M people viewed»).
- 12 KB вместо 1M записей.

**6. Rate-limit на стороне записи.**
- Спам лайками от ботов — ограничен per-user-per-post.

**Антипаттерн:**
- Класть список лайкнувших в один Redis HASH `post:42:likers` — при viral виден рост latency. Лучше — отдельный partition строки в Cassandra.

## Q24. Feed freshness vs latency trade-off и SLO?

Это две разные метрики, которые легко спутать, но они тянут систему в разные стороны. **Freshness** — время от создания поста до его появления в ленте активного подписчика. **Latency** — время загрузки самой ленты клиентом. Хитрость в том, что улучшение одной обычно ухудшает другую, поэтому их фиксируют отдельными SLO.

**Компромисс:**
- Push-fanout → freshness 1-5 сек, latency feed ~50 ms (платим записью ради быстрого чтения).
- Чистый pull → freshness мгновенный (на момент чтения), latency feed 200-500 ms (свежо, но медленно).
- Устаревший кэш → latency 10 ms, freshness 30 сек (быстро, но видим вчерашнее).

**Примеры SLO (в стиле Twitter):**
| Метрика | Цель |
|---|---|
| `p50 freshness` (пост→feed) | < 2 сек |
| `p95 freshness` | < 10 сек |
| `p99 freshness` | < 60 сек |
| `p50 feed load` | < 100 ms |
| `p99 feed load` | < 500 ms |
| `feed_error_rate` | < 0.01% |

**Ручки настройки:**
- Число fanout-воркеров → freshness.
- Cache TTL → latency vs устаревание.
- Сложность ранжирования → latency.

**Мониторинг:**
- `feed_freshness_seconds_bucket` — гистограмма, мониторим p95/p99.
- Алерт: p95 > 30 сек 5 минут подряд.

## Q25. Block/mute/hide — как влияют на feed?

Все три механизма убирают нежелательный контент из ленты, но различаются симметрией и силой сигнала: block двусторонний (оба не видят друг друга), mute односторонний (только инициатор перестаёт видеть), hide — точечное скрытие одного поста. Главное архитектурное решение — где фильтровать: дешевле на чтении (фильтр после ранжирования), чем удалять посты из уже разложенных timeline.

**Block** (двусторонний):
- A блокирует B → B не видит постов A, A не видит постов B.
- Применяется на чтении (фильтр после ранжирования).
- Хранится в таблице `user_blocks`, partition по `blocker_id`.
- Кэш в памяти (per-request lookup за миллисекунды).

**Mute** (односторонний):
- A замьютил B → A не видит постов B, но B не знает, что A его заблокировал бы.
- Фильтрация аналогична.

**Hide post** (на отдельный пост):
- Пользователь скрывает конкретный пост → не показывать.
- Сильный негативный сигнал для ranking-модели.

**Где фильтровать:**
- Перед ранжированием: исключаем заблокированных авторов из пула кандидатов.
- Дополнительно после ранжирования: дешёвая защита от гонки (block добавлен между шагами).

**Компромисс:**
- Фильтр в Cassandra при write-fanout — экономит фильтр на чтении, но при событии блокировки надо удалять из timeline (`ZREM`) → дорого.
- Фильтр на чтении — дешевле; неконсистентность некритична (пост от заблокированного может мелькнуть на 5 сек).

**Приватность:**
- Список блокировок приватный; в API не светим.

## Q26. (!) Как развернуть систему в нескольких регионах (multi-region)?

Multi-region решает две задачи: низкую latency для пользователей по всему миру и устойчивость к падению целого региона. Подход — несколько полных стеков (каждый регион самодостаточен), пользователя закрепляют за home-регионом и обслуживают локально, а данные реплицируют между регионами eventually. Это работает, потому что feed терпит небольшую рассинхронизацию (5-30 сек) — в отличие от биллинга, который выносят в отдельную CP-систему.

**Цели:**
- Latency < 100 ms из любой точки мира.
- Disaster recovery: падение одного региона → переключение за 5 минут.

**Архитектура:**
- 3-5 регионов: US-East, US-West, EU-West, APAC-Singapore, APAC-Tokyo.
- Каждый — полный стек (Feed Service, Post Service, Redis, Cassandra ring).
- Cassandra: multi-DC репликация, `LOCAL_QUORUM` на запись, `LOCAL_QUORUM` на чтение.
- Kafka MirrorMaker → cross-region поток постов.

**Маршрутизация:**
- GeoDNS / latency-based routing на AWS Route53.
- Пользователь закреплён за home-регионом (hash по user_id или географически).

**Согласованность:**
- Посты → eventually consistent между регионами (5-30 сек).
- Профиль пользователя, подписки → тоже eventually; нормально.
- Деньги/биллинг → отдельная CP-система со строгой согласованностью.

**Аварийное переключение (failover):**
- Health-чеки в каждом регионе.
- Авто-failover в Route53: TTL 60 сек.
- DR-учения: ежемесячный chaos-тест.

**Подводные камни:**
- Cross-region запись для celebrity-fanout → дорого; делаем fanout локально по регионам, индекс реплицируем async.
- Ранжирование с учётом часового пояса: для APAC веса иные, чем для US.

## Q27. A/B testing платформа для feed changes?

Любое изменение ленты (новая ranking-модель, другой TTL, иной вес сигнала) выкатывают через A/B, потому что эффект нельзя предсказать заранее — его измеряют на живом трафике. Платформа стабильно делит пользователей на control и treatment (hash по `user_id` + `experiment_id`), сравнивает метрики и автоматически откатывает изменение при деградации KPI. Специфика feed — сетевые эффекты: действия treatment-пользователей просачиваются к control через социальный граф.

**Эксперимент = бакет пользователей + контроль + treatment + метрики.**

**Платформа:**
- Сервис бакетинга (hash по user_id + experiment_id) → стабильное распределение.
- Конфиг эксперимента в системе feature-флагов (Unleash, LaunchDarkly, собственная).
- Сервис `Feed` читает флаг → выбирает model_version / cache_strategy / ranking_weight.

**Метрики:**
- Engagement: лайки/репосты/комментарии за сессию.
- Time-on-app, число сессий.
- Retention D1/D7/D30.
- Негативные: report rate, hide rate, unfollow rate.

**Статистика:**
- Sequential testing (mSPRT) для ранней остановки.
- Холдаут-группа 1% всегда (долгосрочные эффекты).

**Раскатка:**
- 1% → 5% → 10% → 50% → 100% при положительных метриках.
- Авто-откат, если KPI деградирует > 1%.

**Подводные камни:**
- Сетевые эффекты: A/B на социальном графе — действия treatment-пользователей влияют на control-пользователей (нужны cluster-randomized эксперименты).
- Novelty bias: новая фича сначала растёт, потом возвращается к baseline.

## Q28. Throttling и backpressure при spike?

Цель защиты от всплесков — не упасть целиком, а контролируемо деградировать: лучше отдать хронологическую ленту, чем уронить сервис. Защита эшелонированная — каждый рубеж (edge/WAF → API Gateway → приложение → Kafka) срезает свою часть нагрузки, а внизу всегда есть graceful degradation: сломалось ранжирование → хроно, упал Redis → pull из Cassandra.

**Источники всплесков:**
- Кризис / новостное событие → 10× от обычного числа постов/сек.
- Событие с участием celebrity → 100× fanout.
- DDoS / bot storm.

**Эшелонированная защита:**

**1. Edge / WAF.**
- Rate limiting на CDN по IP.
- Детекция ботов (CAPTCHA, JS-челленджи).

**2. API Gateway.**
- Per-user rate limit (token bucket): 100 запросов/мин на загрузку feed.
- Per-endpoint rate limit глобально.

**3. Приложение:**
- Bulkhead: у ranking-service отдельный пул потоков от feed-service.
- Circuit breaker (Resilience4j): при падении ranking → деградация в хроно-режим.

**4. Backpressure от Kafka.**
- Посты → растёт consumer lag → реактивный throttle на Post Service (медленнее принимаем записи).

**5. Graceful degradation.**
- Ранжирование сломано → отдаём хроно.
- Redis недоступен → fallback на pull из Cassandra (медленнее, но работает).
- Медиа недоступны → только текст.

**Мониторинг:**
- `consumer_lag` Kafka, `redis_evicted_keys`, `circuit_breaker_state`.
- Пороги алертов + автомасштабирование (HPA Kubernetes).

## Q29. Стоимость инфры: на чём экономим?

Доминирующая статья расходов feed-системы — не compute и не БД, а CDN egress на медиа (видео и картинки). Поэтому главная экономия идёт там же: уменьшать размер отдаваемого контента (per-title-кодирование, WebP/AVIF) и резать трафик на origin длинными edge-TTL. Остальное — стандартный FinOps: reserved/spot-инстансы, холодное хранилище для старых постов, сжатие в Kafka.

**Главные статьи расходов (масштаб Twitter/X):**
1. **CDN egress** — $0.5-2M/месяц (видео/медиа).
2. **Compute (k8s-ноды)** — $200-500K/месяц.
3. **Cassandra cluster + хранилище** — $100-300K/месяц.
4. **Redis cluster** — $50-150K/месяц.
5. **Kafka cluster + хранилище** — $30-100K/месяц.

**Оптимизации:**
- **Per-title video encoding** (как у Netflix) — снижение bandwidth до 50%.
- **Изображения в WebP/AVIF** вместо JPEG/PNG (на 30-50% меньше).
- **Edge cache TTL** — длиннее = меньше трафика на origin.
- **Reserved Instances / Savings Plans** на AWS — скидка 30-60%.
- **Spot-инстансы** для batch-задач (обучение ranking, индексация).
- **Холодное хранилище** для постов старше 1 года → S3 IA / Glacier.
- **Многоуровневый кэш** — горячее в Redis, тёплое в Memcached, холодное в Cassandra.
- **Сжатие** на Kafka (`compression.type=zstd`) — в 4× меньше хранилища и bandwidth.

**Trade-off-ы:**
- Меньше реплик → дешевле, но риски для DR.
- Длинный cache TTL → дешевле, но устаревший feed.
- Слабее ranking-модель → дешевле GPU, но ниже engagement.

## Q30. (!) Антипаттерны и подводные камни?

Большинство провалов feed-системы — это нарушение тех же принципов, что разбирались выше: смешали запись с чтением, не размазали hot key, не вынесли тяжёлое в async. Ниже — типичные грабли, по которым интервьюер проверяет, понимаете ли вы их симптомы и исправление.

**1. Синхронный fanout в потоке запроса.**
- Симптом: POST `/posts` ждёт 5 сек, пока пишет в timeline 10K подписчиков.
- Исправление: async через Kafka (Q17).

**2. Один большой ZSET на глобальный feed.**
- Симптом: hot key, все пишут и читают один Redis-shard.
- Исправление: ZSET на пользователя (Q9).

**3. Синхронное чтение всех подписок.**
- Симптом: 200 последовательных запросов на одну загрузку feed → 10× latency.
- Исправление: batch-fetch (MGET), параллельные RPC.

**4. Счётчик в одной строке для лайков viral-поста.**
- Симптом: lock contention, 100K записей/сек в один partition.
- Исправление: шардирование счётчика или async-агрегация (Q23).

**5. Строгая согласованность на timeline.**
- Симптом: попытка записи `QUORUM` в 100 timeline-ов подписчиков → 10× latency.
- Исправление: eventual consistency, async-fanout.

**6. Без кэша пользовательских данных в feed-пайплайне.**
- Симптом: 50 lookup-ов пользователей на один feed = 50 RTT.
- Исправление: кэш Redis + bulk-fetch.

**7. Без обработки celebrity.**
- Симптом: один пост @elonmusk подвешивает систему fanout.
- Исправление: гибрид push+pull (Q6).

**8. Без backpressure на Kafka-producer.**
- Симптом: producer заваливает Kafka → OOM брокера.
- Исправление: `linger.ms`, `batch.size`, `acks=1`, лимит in-flight-запросов.

**9. Без A/B-инфраструктуры.**
- Симптом: выкатили новый ranking → engagement упал на 20%, неделю никто не замечал.
- Исправление: ML-пайплайн + A/B + авто-откат (Q27).

**10. Без observability по freshness.**
- Симптом: «у нас всё ок» — а пользователи видят посты с задержкой 30 минут.
- Исправление: сквозная метрика `freshness_seconds` (Q24).

**11. Single-region-деплой.**
- Симптом: региональный сбой → весь продукт лежит 4 часа.
- Исправление: multi-region active-active (Q26).

**12. Хранение медиа как BLOB-ов в БД.**
- Симптом: IO/хранилище БД растут с фотографиями → дорого и медленно.
- Исправление: S3 + CDN, в БД только URL.

---

## See also

- [Design Twitter](design-twitter-interview.md) — каноничный case фид-системы
- [Design Instagram](design-instagram-interview.md) — фото-feed + Stories
- [Design YouTube](design-youtube-interview.md) — video feed + recommendations
- [Design Chat System](design-chat-system-interview.md) — параллельная fanout-механика для чатов
- [System Design Interview](system-design-interview.md) — общие принципы кейсов
- [Caching Strategies](../architecture/caching-strategies-interview.md) — multi-layer caching, hot-key mitigation
- [Cassandra](../databases/cassandra-interview.md) — posts store, multi-DC replication
- [Redis](../databases/redis-interview.md) — sorted sets, cluster, eviction
- [Kafka](../messaging/kafka-interview.md) — async fanout, backpressure
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — fan-out, bulkheads, replication
- [Resilience Patterns](../architecture/resilience-patterns-interview.md) — circuit breaker, graceful degradation
- [Database Sharding](../databases/database-sharding-interview.md) — partition strategies
