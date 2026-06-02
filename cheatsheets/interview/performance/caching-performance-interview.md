---
title: "Вопросы на собеседовании: Caching Performance"
description: "Cache performance tuning: hit ratio, eviction, TTL, stampede, warming, Redis/Memcached tuning, CDN, multi-level, Caffeine, metrics, troubleshooting"
tags:
  - interview
  - performance
  - caching-performance-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Caching Performance"
  - "Caching Performance interview"
  - "Cache tuning"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Caching Performance`

`Caching Performance` — метрики и tuning cache: **hit ratio**, eviction, TTL, thundering herd. Отличие от [стратегий кэширования](../architecture/caching-strategies-interview.md) — здесь фокус на **measurement и troubleshooting** производительности. Ошибки: low hit ratio, stampede, cache bloat, stale serving.

## Полезные ссылки

### Официальная документация

- [Redis performance](https://redis.io/docs/manual/performance/)
- [Memcached wiki](https://github.com/memcached/memcached/wiki)
- [Caffeine benchmarks](https://github.com/ben-manes/caffeine/wiki/Benchmarks)
- [Netflix EVCache](https://netflixtechblog.com/ephemeral-volatile-caching-in-the-cloud-8eabc6acbf05)
- [Google SRE Book — caching](https://sre.google/sre-book/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Метрики**
- [Q1. (!) Ключевые метрики cache?](#q1--ключевые-метрики-cache)
- [Q2. (!) Hit ratio — что считается "хорошо"?](#q2--hit-ratio--что-считается-хорошо)
- [Q3. (!) Как измерить cache impact на latency?](#q3--как-измерить-cache-impact-на-latency)

**Eviction и memory**
- [Q4. (!) LRU / LFU / TinyLFU — performance разница?](#q4--lru--lfu--tinylfu--performance-разница)
- [Q5. (!) Redis `maxmemory-policy` — выбор?](#q5--redis-maxmemory-policy--выбор)
- [Q6. Memory fragmentation Redis?](#q6-memory-fragmentation-redis)

**Cache stampede**
- [Q7. (!) Thundering herd / cache stampede?](#q7--thundering-herd--cache-stampede)
- [Q8. (!) Защита: mutex, probabilistic early expiration?](#q8--защита-mutex-probabilistic-early-expiration)
- [Q9. Request coalescing?](#q9-request-coalescing)

**TTL strategies**
- [Q10. (!) Как выбрать TTL?](#q10--как-выбрать-ttl)
- [Q11. Jittered TTL (prevent mass expiration)?](#q11-jittered-ttl-prevent-mass-expiration)

**Warming и invalidation**
- [Q12. (!) Cold start — cache warming strategies?](#q12--cold-start--cache-warming-strategies)
- [Q13. Invalidation performance (patterns)?](#q13-invalidation-performance-patterns)

**Multi-level**
- [Q14. (!) L1 (in-proc) + L2 (Redis) — зачем?](#q14--l1-in-proc--l2-redis--зачем)
- [Q15. Caffeine tuning?](#q15-caffeine-tuning)

**Network и serialization**
- [Q16. (!) Redis cluster overhead vs single node?](#q16--redis-cluster-overhead-vs-single-node)
- [Q17. Pipeline / MGET batching?](#q17-pipeline--mget-batching)
- [Q18. Serialization overhead (JSON vs MessagePack vs protobuf)?](#q18-serialization-overhead-json-vs-messagepack-vs-protobuf)

**CDN performance**
- [Q19. (!) CDN hit ratio и cache headers?](#q19--cdn-hit-ratio-и-cache-headers)
- [Q20. CDN tiered caching?](#q20-cdn-tiered-caching)

**Troubleshooting**
- [Q21. (!) Hot key problem?](#q21--hot-key-problem)
- [Q22. (!) Big keys problem?](#q22--big-keys-problem)
- [Q23. Cache not scaling — что проверить?](#q23-cache-not-scaling--что-проверить)

## Q1. (!) Ключевые метрики cache?

(!) Ключевые метрики cache?

**Обязательные метрики:**

**Hit ratio:**
```
hit_ratio = hits / (hits + misses)
```
- Главная метрика; целевое значение зависит от workload (80-99%)
- Низкое значение = cache бесполезен

**Eviction rate (частота вытеснения):**
- Сколько ключей вытесняется в секунду (LRU/LFU)
- Высокая = память под давлением → увеличить размер или подстроить TTL

**Latency:**
- Время Get / Set по перцентилям p50/p95/p99
- Для Redis норма — единицы миллисекунд; > 10ms = проблема (сеть, big keys)

**Throughput (ops/sec):**
- GET/SET в секунду
- Следить ради планирования ёмкости

**Использование памяти:**
- `used_memory` vs `maxmemory`
- > 80% = давление на вытеснение

**Число соединений:**
- Открытые соединения (лимит на стороне сервера)
- Слишком много = исчерпание пула соединений

**Пропускная способность сети:**
- Можно упереться в лимиты NIC раньше, чем в CPU

**Error rate:**
- Сбойные операции (connection refused, timeout, OOM)

**Специфика Redis:**
- `instantaneous_ops_per_sec`
- `keyspace_hits` / `keyspace_misses`
- `evicted_keys`
- `expired_keys`
- `blocked_clients`

**Memcached:**
- `get_hits` / `get_misses`
- `bytes` / `limit_maxbytes`
- `evictions`

**Экспорт метрик:** `redis_exporter` / `memcached_exporter` → Prometheus → Grafana.

## Q2. (!) Hit ratio — что считается "хорошо"?

**Зависит от сценария использования:**

**Tier 1 (hot data — сессии, конфиг):** **99%+**
- Почти всё закэшировано; miss редок
- Короткий TTL, прогрев заранее

**Tier 2 (кэш DB-запросов, ответы API):** **90-95%**
- Большинство запросов из кэша; есть некоторая инвалидация
- Типичное веб-приложение

**Tier 3 (CDN, статика):** **95-99%**
- Статика меняется редко

**Tier 4 (long-tail данные, например пользовательский контент):** **50-80%**
- Много уникальных ключей, мало повторов (распределение Zipf)
- Кэш всё ещё полезен, но не доминирует

**Расчёт метрики:**
- **Мгновенное значение:** за последнюю 1 минуту — волатильно
- **Среднее за 5-15 мин** — стабильно для алертинга
- **За сутки** — анализ тренда

**Причины низкого hit ratio:**
- TTL слишком короткий (вытеснение до повторного использования)
- Размер кэша слишком мал (вытеснение по LRU)
- Много уникальных ключей (long tail, кэш бесполезен)
- Слишком агрессивная инвалидация
- Cold start (после деплоя / рестарта)

**Порядок устранения:**
1. Измерить текущий hit ratio по каждому кэшу
2. Если < целевого → разбираться
3. Посмотреть top-N промахов (если возможно) → искать паттерны
4. Подстроить: размер, TTL или реструктурировать ключи

**Hit ratio ≠ всегда «чем выше, тем лучше»:**
- 100% попаданий можно получить, вечно кэшируя мусор
- Нужен баланс со свежестью данных (риск отдачи устаревшего)

## Q3. (!) Как измерить cache impact на latency?

**Метод 1: прямое измерение:**
```java
long start = System.nanoTime();
Value v = cache.get(key);
long cacheLatency = System.nanoTime() - start;

if (v == null) {
    start = System.nanoTime();
    v = db.query(key);  // slow path
    long dbLatency = System.nanoTime() - start;
    cache.put(key, v);
}
```

Логировать/собирать метрики по обоим путям.

**Метод 2: A/B / feature flag:**
- Отключить кэш для части трафика
- Сравнить end-to-end latency
- Показывает «реальный» эффект (включая накладные расходы самого кэша)

**Метод 3: анализ p99:**
- Отсортировать запросы по latency
- P99 = самый медленный 1% — часто это промахи кэша
- Дельта (P99 vs P50) = эффективность кэша

**Расчёт теоретического максимума:**
```
avg_latency = hit_ratio × cache_lat + (1 - hit_ratio) × miss_lat
```

Пример:
- Попадание в кэш = 1ms
- DB = 100ms
- Hit ratio 95% → avg = 0.95×1 + 0.05×100 = 5.95ms
- Hit ratio 90% → avg = 10.9ms (в 2 раза медленнее!)
- **Падение hit ratio на 5% почти удвоило latency**

**Это и показывает, почему hit ratio — король метрик.**

**Инструментирование:**
- Спаны OpenTelemetry с атрибутом `cache.hit=true/false`
- Разбить p99 по этому атрибуту → ясная картина

**Внимание:** сам кэш тоже может добавлять latency:
- Сетевой RTT до Redis (1-2ms)
- Сериализация
- In-proc кэш (Caffeine) — микросекунды, по сути бесплатно

## Q4. (!) LRU / LFU / TinyLFU — performance разница?

**LRU (Least Recently Used):**
- Вытесняет давно не использованное
- Плюсы: простота, хорош для последовательных workload-ов
- Минусы: «однодневки» (one-hit wonders) засоряют кэш — свежие, но малоценные

**LFU (Least Frequently Used):**
- Вытесняет с наименьшим счётчиком обращений
- Плюсы: удерживает популярные элементы
- Минусы: «классический» LFU ничего не забывает → устаревшие популярные вытесняют новые

**TinyLFU (Caffeine, современный Redis `allkeys-lfu`):**
- Оценщик частоты (Count-Min Sketch) + оконный LRU
- Аппроксимирует LFU при малом расходе памяти
- Учитывает и свежесть, и частоту
- **Победитель в бенчмарках** на реалистичных workload-ах

**Сравнение hit ratio (реалистичный workload, например база/поиск):**
- LRU: ~70-80% hit ratio
- LFU: ~75-82%
- **TinyLFU: 85-95%** — заметно лучше

**Admission filter (фильтр допуска):**
- Новый элемент вытесняет текущего жильца только если оценочная частота нового выше
- Не даёт холодному попаданию вымыть прогретый кэш

**Режимы Redis:**
- `allkeys-lru` (дефолт во многих конфигах)
- `allkeys-lfu` (с 4.0) — приближённый LFU
- Комбинируется с `maxmemory-samples` (дефолт 5; больше = точнее приближение, но больше CPU)

**Caffeine:** использует TinyLFU — обычно верный выбор для in-proc на JVM.

**Вердикт:** на типичном веб-workload с распределением Zipf — **TinyLFU превосходит** LRU на 5-15% по hit ratio.

## Q5. (!) Redis `maxmemory-policy` — выбор?

**Политики:**

- **`noeviction`** — возвращать ошибку при OOM (дефолт в некоторых конфигах)
- **`allkeys-lru`** — вытеснять по LRU среди всех ключей
- **`allkeys-lfu`** — вытеснять по LFU среди всех ключей (4.0+)
- **`allkeys-random`** — случайное вытеснение
- **`volatile-lru`** — LRU только среди ключей с TTL
- **`volatile-lfu`** — LFU среди ключей с TTL
- **`volatile-random`** — случайное среди ключей с TTL
- **`volatile-ttl`** — сначала с наименьшим TTL

**Выбор:**

**Чистый кэш (все данные кэшируемы, без различий):**
- `allkeys-lfu` — обычно лучший hit ratio
- `allkeys-lru` — проще, примерно так же хорош

**Кэш + хранилище сессий (смешанно):**
- `volatile-lru` или `volatile-lfu` — у сессий есть TTL; вытесняем кэш, сохраняем сессию
- Но! Постоянные ключи (без TTL) никогда не вытесняются → могут заполнить память — следить!

**Сквозная запись (write-through) с персистентностью:**
- `noeviction` — приложение само управляет; миритесь с ошибками OOM
- Redis используется как **хранилище**, а не кэш

**Настройка:**
```
maxmemory 10gb
maxmemory-policy allkeys-lfu
maxmemory-samples 10  # default 5; higher = better approximation
```

**Антипаттерн:** `noeviction` с неограниченным TTL — растёт до OOM → падение.

**Мониторинг:** счётчик `evicted_keys`; всплески указывают на нехватку ресурсов.

## Q6. Memory fragmentation Redis?

**Коэффициент фрагментации:**
```
mem_fragmentation_ratio = used_memory_rss / used_memory
```

- 1.0 = фрагментации нет
- 1.0-1.5 = норма
- \> 1.5 = высокая фрагментация (расход впустую)
- < 1.0 = свопинг (ПЛОХО!) — ОС выгрузила память Redis в swap → ужасная latency

**Причины:**
- Ключи/значения переменного размера (арены jemalloc)
- Удаления оставляют «дыры»
- Долгоживущий процесс с большим числом записей

**Как исправить:**

**Активная дефрагментация (Redis 4.0+):**
```
activedefrag yes
active-defrag-ignore-bytes 100mb
active-defrag-threshold-lower 10
active-defrag-threshold-upper 100
active-defrag-cycle-min 5
active-defrag-cycle-max 75
```
- Фоновый процесс перемещает значения
- Накладные расходы по CPU (регулируется через cycle-max)

**Рестарт:**
- Крайняя мера; очищает кэш
- Сделать failover на реплику, перезапустить primary

**Аллокатор:** jemalloc (дефолт Redis) — наименьшая фрагментация; не используйте libc malloc.

**Мониторинг:** алерт, если `mem_fragmentation_ratio > 1.5` держится продолжительное время.

## Q7. (!) Thundering herd / cache stampede?

**Сценарий:**
1. Истекает hot key
2. 1000 одновременных запросов промахиваются мимо кэша
3. Все 1000 перестраивают значение (бьют в DB / пересчитывают)
4. DB раздавлена; latency взлетает

**Проблема:** кэш отдавал на 1000 RPS; промах → 1000 RPS обрушиваются на DB.

**Последствия:**
- Перегрузка DB
- Каскадный отказ
- Всплеск latency
- Хуже, если перестройка занимает секунды

**Защита:**

**1. Распределённый лок (mutex):**
- Первый промах берёт лок; остальные ждут
- Один перестраивает; остальные читают свежее значение
- Набросок кода:
```java
if (cache.get(key) == null) {
    if (lock.tryLock(key, 5s)) {
        try {
            val = db.load();
            cache.put(key, val);
        } finally { lock.unlock(); }
    } else {
        waitAndRetry();
    }
}
```

**2. Вероятностное досрочное истечение:**
- Перестраивать вероятностно ещё до истечения
- Ближе к истечению → выше шанс перестроить
- Формула (Vattani et al. 2015):
```
now - (delta × β × ln(random())) >= expiry
```
- **Плюсы:** нет лока, сглаживает нагрузку на перестройку

**3. Stale-while-revalidate:**
- Отдавать устаревшее, асинхронно запускать перестройку
- Подходит, если допустима лёгкая несвежесть (обычно так)

**4. Request coalescing (внутри процесса):**
- Single-flight: дедупликация одновременных запросов
- В Go — `singleflight`, в Java — кэш на `CompletableFuture`

**5. Прогрев (warmup):**
- Заполнить кэш заранее, до подачи трафика

## Q8. (!) Защита: mutex, probabilistic early expiration?

**Распределённый mutex (Redis):**

```python
def get_with_lock(key):
    val = redis.get(key)
    if val:
        return val
    
    lock_key = f"lock:{key}"
    if redis.set(lock_key, 1, nx=True, ex=10):  # 10s lock
        try:
            val = db.query(key)
            redis.set(key, val, ex=300)
            return val
        finally:
            redis.delete(lock_key)
    else:
        # Another is rebuilding; wait or serve stale
        time.sleep(0.1)
        return redis.get(key) or get_with_lock(key)  # recursion limit
```

**Плюсы:** просто; гарантирует единственного, кто перестраивает.
**Минусы:** ждущие блокируются; критично освобождение лока (использовать короткий TTL).

**Redlock (Redis multi-master):** более надёжный распределённый лок; избыточен для большинства кэш-сценариев.

**Вероятностный алгоритм (XFetch):**
```python
def xfetch(key, ttl, compute_fn):
    data, expiry, delta = redis.get_with_meta(key)
    now = time.time()
    if data and now - delta * BETA * math.log(random.random()) < expiry:
        return data  # serve cached
    
    # Probabilistically refresh early
    start = time.time()
    data = compute_fn()
    delta = time.time() - start
    redis.set_with_meta(key, data, ttl, delta)
    return data
```

- `delta` = время на пересчёт (хранится рядом)
- `BETA` = настроечная константа (обычно 1.0)
- Чем ближе к истечению → тем выше вероятность перестройки → стремится к 1
- Нет «стада»: запросы размазаны по окну перестройки

**Caffeine** (Java in-proc) — встроенный `refreshAfterWrite`: асинхронное обновление с одновременной отдачей закэшированного.

**Сравнение:**

| Подход | Простота | Эффективность | Latency |
|----------|-----------|---------------|---------|
| Mutex | Средняя | Высокая | Ждущие заблокированы |
| Вероятностный | Сложно | Высокая | Никто не блокируется |
| Stale-while-revalidate | Легко | Высокая (для толерантных приложений) | Никто не блокируется |
| Прогрев | Легко | Высокая (если реализуемо) | Разовый |

## Q9. Request coalescing?

**Coalescing** — дедупликация **одновременных идентичных запросов** внутри одного процесса.

**Без coalescing:**
- Запрос A: промах, запускает DB-запрос
- Запрос B (через 1ms): промах, запускает ещё один такой же DB-запрос
- 2 DB-запроса ради одного результата

**С coalescing:**
- Запрос A запускает задачу
- Запрос B видит уже выполняющуюся задачу A, **ждёт тот же future**
- 1 DB-запрос; оба получают результат

**Go `singleflight`:**
```go
var group singleflight.Group
result, _, _ := group.Do(key, func() (interface{}, error) {
    return db.Query(key)
})
```

**Java (CompletableFuture):**
```java
ConcurrentHashMap<String, CompletableFuture<Value>> inflight = new ConcurrentHashMap<>();

public Value get(String key) {
    Value v = cache.get(key);
    if (v != null) return v;
    
    return inflight.computeIfAbsent(key, k -> 
        CompletableFuture.supplyAsync(() -> {
            Value val = db.query(k);
            cache.put(k, val);
            inflight.remove(k);
            return val;
        })
    ).join();
}
```

**Caffeine** делает coalescing автоматически при использовании `.build(CacheLoader)`.

**Ограничения:**
- Только в пределах процесса (не поможет, если 100 подов промахиваются одновременно)
- Комбинируется с распределённым mutex для межпроцессного случая

**Подвох:** исключение в одном запросе → падают все ждущие, разделяющие future. Часто это приемлемо, но нужно об этом помнить.

## Q10. (!) Как выбрать TTL?

**Факторы:**

**1. Требование к свежести:**
- Реальное время (биржевые котировки) → секунды
- Детали товара → минуты-часы
- Профиль пользователя → часы-дни
- Конфиг → часы-сутки

**2. Стоимость промаха на бэкенде:**
- Дёшево (in-memory приложение) → короткий TTL ок
- Дорого (DB-запрос с кучей join-ов, 500ms) → длинный TTL

**3. Частота изменений:**
- Меняется редко → длинный TTL (дни)
- Меняется часто → короткий TTL или event-driven инвалидация

**4. Возможность инвалидации:**
- Если умеете инвалидировать (pub/sub, шина событий) → длинный TTL допустим
- Без инвалидации → TTL = допустимая несвежесть

**Прагматичные дефолты:**
- Ответы API: 5-60 секунд
- Кэш DB-запросов: 1-5 мин
- Конфиг / справочные данные: 1ч
- Сессии: 24ч

**Расчёт:**
```
hit_ratio = TTL_length / (TTL_length + request_interval)
```

Пример:
- Ключ запрашивается каждые 100ms
- TTL = 5s → ratio = 5 / 5.1 ≈ 98%
- TTL = 1s → ratio = 1 / 1.1 ≈ 91%
- Увеличение TTL в 5 раз → hit ratio +7 пунктов

**Подстраивать под workload:**
- Измерить hit ratio при разных TTL
- Построить график → найти точку перегиба

**Подвох:** слишком длинный TTL + нет инвалидации → пользователь видит устаревшее. Проверка: нормально ли, что данные устарели на час?

## Q11. Jittered TTL (prevent mass expiration)?

**Проблема:**
- 10 000 ключей выставлены одновременно с TTL=60s
- Все истекают разом
- 10 000 промахов → stampede по DB

**Решение:** добавить случайный джиттер к TTL.

**Реализация:**
```python
TTL_BASE = 60
TTL_JITTER = 10  # ±10s
ttl = TTL_BASE + random.randint(-TTL_JITTER, TTL_JITTER)
redis.set(key, val, ex=ttl)
```

- Теперь ключи истекают в окне 50-70s
- Плавная нагрузка вместо всплеска

**Почему часто нужно:**
- Пакетный прогрев / построение кэша → все TTL синхронизированы
- Ежедневный дамп / почасовое обновление → синхронные TTL
- Событие деплоя (все поды прогревают кэш одновременно)

**Caffeine:**
- `expireAfterWrite` через интерфейс `Expiry` может возвращать случайную длительность
- Или добавить джиттер: `Duration.ofSeconds(60 + random())`

**Ещё один паттерн — обновление до истечения:**
- Обновлять на 80% TTL (асинхронно)
- Работает как естественный джиттер + избегает массового промаха

**Эффект:** плавный темп перестройки вместо всплеска — график нагрузки на DB превращается из «пилы» в ровную линию.

## Q12. (!) Cold start — cache warming strategies?

**Холодный кэш бывает после:**
- Деплоя / рестарта
- Failover кэш-кластера
- Масштабирования (новый инстанс)
- Массового истечения TTL

**Эффект:** часть (или все) запросов бьют в DB → всплеск latency, возможна перегрузка.

**Стратегии:**

**1. Жадный прогрев (предзагрузка):**
- До подачи трафика на новый инстанс заполнить кэш самыми запрашиваемыми ключами
- Взять top-N из логов доступа
- Readiness probe проходит только после завершения прогрева

**2. Постепенный набор трафика:**
- Балансировщик: новый инстанс получает 1%, 5%, 10%... трафика за несколько минут
- Кэш заполняется естественно, без перегрузки

**3. Репликация:**
- Реплика кэша рядом с primary (Redis replica)
- При рестарте primary → промоутить реплику (она уже прогрета)
- Удалённый L2-кэш (Redis) + локальный L1 (Caffeine): L2 переживает рестарт L1

**4. Персистентный кэш:**
- Redis AOF / RDB — переживает рестарт (хотя загрузка занимает время)
- Memcached — непостоянный; всегда холодный

**5. Фоновое обновление из источников истины:**
- ETL/batch-задача пишет в кэш
- Поток событий (Kafka) → обновление кэша

**6. Кэшировать не всё:**
- Смириться с медлительностью первого запроса
- Если SLO позволяет — возможно, нормально

**Измерение:**
- Время достижения целевого hit ratio (например 95%)
- «Время прогрева» должно быть < максимально допустимого холодного периода

**K8s:** readiness probe возвращает healthy только после прогрева. Иначе балансировщик шлёт трафик на холодный под.

## Q13. Invalidation performance (patterns)?

**«В Computer Science есть только две сложные вещи: инвалидация кэша и придумывание имён».** — Phil Karlton.

**Паттерны и производительность:**

**1. На основе TTL:**
- Простейший вариант, без явной инвалидации
- Миримся с несвежестью (в пределах TTL)
- **Стоимость:** нулевые накладные расходы; **рассогласование:** в пределах окна TTL

**2. Write-through:**
- При записи в DB → синхронно обновить кэш
- **Стоимость:** latency записи = max(DB, кэш); сбой, если кэш недоступен
- **Согласованность:** строгая

**3. Write-behind:**
- Обновить кэш; запись в DB асинхронно
- **Стоимость:** дёшево; **риск:** потеря данных при падении

**4. Событийная (pub/sub):**
- Изменение в DB → событие → подписчики инвалидируют кэши
- **Стоимость:** инфраструктура (Kafka/Redis pub/sub); **согласованность:** секунды
- Хорошо ложится на CQRS, event sourcing

**5. CDC (Change Data Capture):**
- Debezium читает WAL базы → топик → консьюмер инвалидации кэша
- **Стоимость:** сложно; **согласованность:** почти в реальном времени
- Хорошо масштабируется

**6. По тегам (Varnish, Fastly):**
- Группируем ключи по тегу; инвалидация тега = инвалидация всех
- Пример: тег "user:42" на всех страницах пользователя 42; запись → инвалидация тега → все кэши сбрасывают

**7. По версии:**
- Включить версию в ключ кэша: `product:v5:123`
- Обновление DB → инкремент версии → фактически новая запись в кэше
- Старые ключи со временем истекут

**Что учесть по производительности:**
- Broadcast-инвалидация = N×M сообщений (N кэшей × M ключей) — масштабируется ли?
- Лаг инвалидации = отдача устаревшего
- Слишком агрессивная = низкий hit ratio

**Лучшая практика:**
- Начинать с TTL (просто)
- Добавлять инвалидацию там, где несвежесть недопустима
- Не «инвалидировать всё при записи» — действовать прицельно

## Q14. (!) L1 (in-proc) + L2 (Redis) — зачем?

**L1: внутрипроцессный** (Caffeine, Guava, локальный HashMap):
- Latency: наносекунды-микросекунды
- Ёмкость: ограничена heap JVM (обычно 100MB-1GB)
- Без сети

**L2: распределённый** (Redis, Memcached):
- Latency: 0.5-2ms (сеть)
- Ёмкость: терабайты
- Общий для всех инстансов приложения

**Иерархия:**
```
Request → L1 (check in-proc) → L2 (check Redis) → DB
         hit → return (μs)    hit → return (ms)    miss → rebuild
```

**Преимущества:**
- **Сверхбыстро** для hot keys (попадания в L1)
- **Разгрузка Redis** (освобождается CPU Redis)
- **Экономия сети** (меньше GET-ов в Redis)
- **Устойчивость** к отказу Redis (L1 продолжает отдавать)

**Компромисс: согласованность:**
- L1 и L2 могут рассинхронизироваться
- L2 обновлён → L1 устарел (на свой TTL)
- Приемлемо для большинства сценариев (короткий TTL у L1)

**TTL для L1:** короче, чем у L2 (например L1=1мин, L2=10мин).

**Инвалидация:**
- Можно публиковать событие инвалидации (Redis pub/sub) → все L1 сбрасывают ключ
- Или просто мириться с кратковременной несвежестью L1

**Когда применять:**
- Read-heavy (10 000+ RPS на один ключ) → L1 критичен
- Hot keys (распределение Парето) — 20% ключей = 80% запросов
- Чувствительность к latency (SLO в долях миллисекунды)

**Когда нет:**
- Низкий RPS (накладные расходы L1 > выгоды)
- Каждое чтение должно отдавать точно последнее значение

## Q15. Caffeine tuning?

**Caffeine** — высокопроизводительная Java-библиотека кэширования (простота в духе SLF4J).

**Конфигурация:**
```java
Cache<K, V> cache = Caffeine.newBuilder()
    .maximumSize(10_000)         // LFU-based eviction
    .expireAfterWrite(5, MINUTES) // TTL
    .expireAfterAccess(10, MINUTES) // TTI (time-to-idle)
    .refreshAfterWrite(1, MINUTES)  // async refresh for freshness
    .recordStats()                  // enable metrics
    .build(key -> db.load(key));
```

**`maximumSize` vs `maximumWeight`:**
- Size: считает число записей
- Weight: своя функция-весовщик (`entry.size()` в байтах)
- Выбор зависит от того, сильно ли различаются размеры значений

**`expireAfterWrite` vs `expireAfterAccess`:**
- Write: фиксированный TTL от момента создания
- Access: сбрасывается при чтении (hot keys остаются)
- Комбинируется с `expireAfter(Expiry)` для своей логики

**`refreshAfterWrite`:**
- Спустя X — асинхронная фоновая перезагрузка
- Отдаёт устаревшее, пока грузит → нет штрафа за промах
- Критично для защиты от stampede

**Статистика:**
```java
CacheStats stats = cache.stats();
stats.hitRate();       // e.g., 0.94
stats.evictionCount();
stats.missRate();
```

Экспорт через Micrometer в Prometheus:
```java
CaffeineCacheMetrics.monitor(registry, cache, "user_cache");
```

**Подбор размера:**
- Начать с 10x от ожидаемого числа уникальных ключей
- Следить за hit ratio → корректировать
- Слишком большой → давление на GC (заполняется old-gen)

**Загрузка кэша:**
- `CacheLoader` — синхронная загрузка при промахе
- `AsyncCacheLoader` — неблокирующая
- Объединяет одновременные запросы по одному ключу (нет «стада»)

**Spring Boot:**
```yaml
spring.cache.type: caffeine
spring.cache.caffeine.spec: maximumSize=1000,expireAfterWrite=10m
```

## Q16. (!) Redis cluster overhead vs single node?

**Одноузловой Redis:**
- Минимальная latency
- Просто
- Лимит: (в основном) 1 ядро CPU + RAM одной машины

**Redis Cluster:**
- Шардирование по N мастерам
- 16384 хэш-слота
- Горизонтальное масштабирование (данные + записи)

**Накладные расходы:**

**1. Сетевые хопы:**
- Клиент → попал не на тот узел? → редирект (MOVED) → нужный узел
- Умный клиент кэширует карту слотов; редиректов нет

**2. Кросс-слотовые операции:**
- `MGET key1 key2` — падает, если ключи в разных слотах
- Решение: **hash tags** `{user:42}:session`, `{user:42}:orders` → один слот
- Или Lua / пакетирование по слотам через pipeline

**3. Решардинг:**
- Перенос слотов = кратковременные MOVED-редиректы
- Хорошо обрабатывается cluster-aware клиентами (Lettuce, Jedis JedisCluster)

**4. Многоключевые транзакции (MULTI/EXEC):**
- Кросс-слот = невозможно
- Планировать колокацию данных

**Измерение:**
- p99 одного узла: ~1ms (localhost) / 2-3ms (внутри VPC)
- p99 кластера: сопоставимо при закэшированной карте слотов; хуже без неё

**Sentinel (HA, не шардирование):**
- Авто-failover на реплику
- Производительность одного мастера + HA
- Ёмкость всё равно одного узла

**Кластер не поможет:**
- Один hot key (один слот = один узел)
- Малый объём данных, помещающийся на одну машину

**Кластер помогает:**
- Весь датасет > лимита RAM
- Пропускная способность записи > CPU одного узла
- Распределить нагрузку

**Альтернативы:**
- Партиционирование на уровне приложения (consistent hashing по инстансам Redis)
- Redis Enterprise (коммерческий) — прозрачное шардирование

## Q17. Pipeline / MGET batching?

**Проблема:** 100 GET-ов в Redis = 100 RTT (500ms+ при cross-region).

**Pipeline:**
```python
p = redis.pipeline()
for key in keys:
    p.get(key)
results = p.execute()  # 1 RTT for all
```

**MGET:**
```python
results = redis.mget(keys)  # single command, 1 RTT
```

**Различия:**

| Аспект | Pipeline | MGET |
|--------|----------|------|
| RTT | 1 (все команды пакетом) | 1 |
| Типы команд | Любые (GET, SET, ...) | Только GET |
| Атомарность | Нет (если не обёрнуто в MULTI) | Одна команда |
| Поддержка кластера | Нужен cluster-aware клиент | Ключи в одном слоте |

**Pipeline для смешанных команд:**
```python
p = redis.pipeline()
p.get(key1)
p.set(key2, val)
p.incr(counter)
res1, _, counter = p.execute()
```

**Pipeline в кластере:**
- Ключи распределяются по узлам согласно слотам
- Lettuce: `RedisClusterAsyncCommands.mget(keys...)` разбивает автоматически
- Jedis: разбивать вручную

**Выигрыш в производительности:**
- 100 операций, RTT 1ms → 100ms последовательно vs 2ms через pipeline = **в 50 раз быстрее**
- Выигрыш растёт при cross-region (RTT 20+ ms)

**Правило большого пальца:** пакетировать, когда несколько ключей известны заранее; latency определяется RTT.

**Ограничение:** большие pipeline-ы потребляют память (на клиенте и сервере); держать разумный размер пакета (100-1000).

## Q18. Serialization overhead (JSON vs MessagePack vs protobuf)?

**Замеры** (объект 1KB):

| Формат | Размер | Скорость сериализации | Скорость десериализации | Человекочитаемость |
|--------|------|-----------|-------------|----------------|
| JSON | 1000B | Быстро | Быстро | Да |
| MessagePack | 700B | Быстро | Быстро | Нет |
| Protobuf | 600B | Быстро | Быстро | Нет |
| Kryo (Java) | 550B | Быстрее всех на JVM | Быстрее всех на JVM | Нет |
| Avro | 600B | Средне | Средне | Нет (схема) |
| Fury (Apache) | 500B | Быстрее всех | Быстрее всех | Нет |
| Gzip(JSON) | 400B | Медленно | Медленно | Нет |
| Java Serialization | 1500B | Медленно | Медленно | Нет |

**Компромиссы:**
- JSON: универсален, читаем, но самый большой и медленный
- MessagePack: бинарный JSON, легко внедрить
- Protobuf: схема заранее → безопасность версий + компактность
- Kryo: только для JVM; быстрый; вопросы к обратной совместимости
- Fury: лидер бенчмарков 2020-х
- **Избегать:** Java Serialization (медленно, подвержена CVE, раздута)

**Специфика для кэша:**
- Маленькое значение (< 1KB): доминируют накладные расходы сериализации
- Большое значение (> 10KB): доминирует передача по сети → помогает сжатие
- **Hot key:** сериализовать один раз, кэшировать сериализованные байты

**Специфика Redis:**
- Хранить в бинарном виде (байты)
- Сжатие при сериализации (gzip, lz4, zstd) — обмен CPU на сеть
- LZ4 — быстрая распаковка; Zstd — лучший коэффициент сжатия

**Бенчмарк своего workload:**
```java
// ~ 5k ops/sec JSON vs 15k ops/sec protobuf типично
```

**Не переоптимизируйте:**
- Если кэш занимает 0.5% времени запроса, ускорение сериализации в 2× = выигрыш 0.25%
- Сначала профилировать, потом менять

## Q19. (!) CDN hit ratio и cache headers?

**CDN hit ratio** — % запросов, отданных с edge, а не с origin.

**Цель:** обычно 90%+; для статики >98%.

**Влияющие заголовки:**

**`Cache-Control`:**
- `max-age=3600` — кэшировать 1 час
- `s-maxage=7200` — TTL именно для CDN (отдельно от браузера)
- `public` / `private` — кэшируется CDN или нет
- `no-cache` — ревалидировать каждый раз
- `no-store` — не кэшировать вовсе
- `must-revalidate` — строго после истечения
- `stale-while-revalidate=60` — отдавать устаревшее 60s, пока идёт ревалидация

**`Vary`:**
- Перечисляет заголовки, от которых зависит кэшированный ответ
- `Vary: Accept-Encoding` — отдельный кэш для gzip/br
- `Vary: User-Agent` — катастрофа (по записи на каждый UA) — избегать!
- `Vary: Cookie` — тоже часто катастрофа

**`ETag` / `Last-Modified`:**
- Ревалидация: `If-None-Match: "etag"` → 304 Not Modified (без тела)
- Экономит трафик, но не latency

**Частые ошибки:**
- `Set-Cookie` — по умолчанию большинство CDN не кэшируют ответы с Set-Cookie
- Query-строки (CDN может считать `?ts=1` и `?ts=2` разными объектами)
- `Cache-Control: private` — пропускает CDN; нужно `public`

**Оптимизации:**
- **Нормализация query-строк:** убирать аналитические параметры (`utm_*`)
- **Проектирование ключа кэша:** включать только значимые параметры
- **Многоуровневое кэширование:** см. следующий вопрос

**Измерение:**
- Дашборд CDN (CloudFront, Fastly) показывает hit ratio
- Низкий hit ratio → проверить заголовки, конфиг ключа кэша, истечение

**Лучшие практики Cache-Control:**
- Статика (JS/CSS/изображения): `max-age=31536000, immutable` + имя файла с фингерпринтом
- HTML: `max-age=0, s-maxage=60` (клиенты всегда перезапрашивают, CDN кэширует ненадолго)
- API: по эндпоинтам; часто `private, max-age=0`

## Q20. CDN tiered caching?

**Без многоуровневого:**
```
100 edge POPs → origin
All 100 miss same object → origin slammed with 100 requests
```

**Многоуровневое / shield POP:**
```
Edge POPs → shield POP (regional) → origin
First edge miss fetches from shield; shield fetches from origin ONCE
Other edges hit shield (shield pre-populated)
```

**Преимущества:**
- **Разгрузка origin** (снижение в 9-100 раз)
- Быстрее разрешается промах для последующих edge (shield ближе, чем origin)

**Вендоры:**
- **Fastly:** shielding — назначить POP как shield
- **CloudFront:** Origin Shield — включается на distribution
- **Cloudflare:** Argo Tiered Cache — авто-выбор лучшего shield

**Конфигурация:**
- Выбрать shield ближе к origin, чем edge (например, origin в us-east-1 → shield в us-east-1)
- Или ближе к пользователям (зависит от паттерна)

**Когда помогает:**
- Большое число edge POPs (100+)
- Long-tail контент (популярно не всё)
- Большие счета за нагрузку на origin

**Когда нет:**
- Малый охват CDN (мало POPs)
- TTL кэша очень короткий (shield тоже часто промахивается)

**Стоимость:** включение стоит чуть дороже (запрос к shield тарифицируется), но окупается экономией на origin.

## Q21. (!) Hot key problem?

**Hot key:** один ключ получает непропорциональную нагрузку (например, 80% GET-ов на один товар).

**Проблема:**
- Узел Redis с этим ключом упирается в CPU
- Сетевой NIC к этому узлу — бутылочное горлышко
- Ребалансировка кластера не поможет (ключ не разделить)

**Обнаружение:**
- `redis-cli --hotkeys` (на основе сэмплинга)
- `MONITOR` — захват команд (осторожно, высокие накладные расходы)
- Метрики приложения: счётчик попаданий по ключу

**Решения:**

**1. Внутрипроцессный L1-кэш (Caffeine):**
- Hot key отдаётся в основном из памяти процесса
- Нагрузка на Redis резко падает

**2. Read-реплики:**
- Маршрутизировать чтения этого ключа на реплику
- Чтение с реплик в Redis Cluster (только RE) — приложение включает явно

**3. Шардирование ключа (multi-key):**
- Дублировать ключ по N экземплярам: `product:123:shard0`...`product:123:shard9`
- Приложение выбирает случайный shard для чтения; запись обновляет все
- Хорошо для read-heavy статики

**4. Предрасчёт / денормализация:**
- Если hot key = дорогой запрос → материализовать в другом месте

**5. Сэмплинг на стороне клиента:**
- 1% чтений идёт в Redis; 99% отдаётся из L1-кэша в процессе
- Инвалидировать L1 через Redis pub/sub

**Мониторинг:** алерт, если один ключ > 5% всех операций.

**Из практики:** «проблема Джастина Бибера» у Twitter — таймлайн одного пользователя создавал горячий шард; решение — кастомное шардирование.

## Q22. (!) Big keys problem?

**Big key:** одно значение очень большое (> 100KB, особенно мегабайты).

**Проблемы:**
- **Всплеск latency:** GET на ключ 10MB = передача 10MB по сети на каждый запрос
- **Блокировка:** Redis однопоточный; чтение big key блокирует другие операции
- **Фрагментация памяти:** боль с выделением/освобождением
- **Лаг репликации:** запись big key шлёт 10MB на реплики
- **Дисбаланс хэш-слотов:** если большой ключ в одном слоте, этот узел толще

**Обнаружение:**
- `redis-cli --bigkeys` (сэмплинг)
- `MEMORY USAGE key` (точный размер)
- `DEBUG OBJECT key` (детально)

**Частые причины:**
- Большой list (миллионы элементов)
- Большой hash (тысячи полей)
- Большой set
- Большой сериализованный blob

**Решения:**

**1. Разбить на меньшие части:**
- Вместо `SET user:42 {big_json}` → `HSET user:42 field1 val1` (паттерны доступа могут улучшиться)
- Или шардировать вручную: `chunk1`, `chunk2`...

**2. Использовать коллекции вместо blob-ов:**
- Вместо JSON-списка → Redis List (RPUSH/LRANGE) — O(1) на элемент

**3. Пагинация:**
- `LRANGE list 0 99` вместо `LRANGE list 0 -1` (весь список)

**4. Сжатие:**
- Хранить сжатый blob; распаковывать на стороне клиента

**5. Перенести в настоящую БД / объектное хранилище:**
- Redis не хранилище для больших blob-ов; использовать S3 / DB + указатель в кэше

**6. Сканировать вместо MEMBERS:**
- `SMEMBERS huge_set` → блокирует; использовать курсор `SSCAN`

**Мониторинг:** алерт на `MEMORY USAGE > 1MB` для любого ключа.

## Q23. Cache not scaling — что проверить?

**Симптомы:**
- Latency растёт с нагрузкой
- Hit ratio падает
- Насыщение CPU / памяти

**Чек-лист отладки:**

**1. Бутылочное горлышко одного узла:**
- Redis однопоточный — ядро CPU насыщается на ~100k ops/sec
- Решение: кластер, чтения с реплик

**2. Сетевой NIC:**
- 1 Gbps насыщен; есть ли 10 Gbps?
- Большие значения → RPS × размер = потребность в полосе

**3. Лимит соединений:**
- `CONFIG GET maxclients` — упёрлись в лимит?
- Слишком маленький пул соединений на стороне приложения → очереди

**4. Hot key (см. Q21):**
- Один ключ перегружает
- Размазать через L1

**5. Big keys (см. Q22):**
- Блокирующие операции

**6. Медленные команды:**
- `SLOWLOG GET 10` — недавние медленные команды
- `KEYS *` (никогда не используйте в проде!), `HGETALL` на большом hash, `SMEMBERS`

**7. Персистентность:**
- Всплеск `BGSAVE`? Синхронизация `RDB`? Перезапись `AOF`?
- Fork() на большом heap: давление Copy-on-Write
- Отключить персистентность на чистом кэше

**8. Фрагментация памяти:**
- `mem_fragmentation_ratio > 1.5` — см. Q6

**9. Swap:**
- `mem_fragmentation_ratio < 1` — выгружено в swap; ПЛОХО; отключить swap

**10. Проблема на стороне клиента:**
- Слишком маленький пул соединений
- Сериализация упирается в CPU
- Паузы GC в приложении (обращения к кэшу заблокированы)

**Инструменты:**
- `redis-cli --latency`, `redis-cli --latency-history`
- Дашборд Grafana для Redis

**Варианты масштабирования:**
- Вертикальное: машина мощнее (ограничено однопоточностью)
- Горизонтальное: кластер (шардирование данных) или репликация (масштабирование чтений)
- Слои кэширования (L1, CDN, больше уровней)

## See also

- [Caching Strategies](../architecture/caching-strategies-interview.md) — patterns (write-through, aside)
- [Database Performance](database-performance-interview.md) — cache reduces DB load
- [Redis](../databases/redis-interview.md) — deeper Redis internals
- [JVM Performance Tuning](jvm-performance-tuning-interview.md) — L1 cache memory
- [Memory Management](memory-management-interview.md) — in-proc cache GC impact
- [Application Profiling](application-profiling-interview.md) — how to measure cache overhead
- [Network Performance](network-performance-interview.md) — RTT к Redis
- [Performance Testing](performance-testing-interview.md) — load test with cache
- [Load Balancing](../architecture/load-balancing-interview.md) — session affinity, cache locality
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — caching as scale tool

- [Database Performance](database-performance-interview.md)
- [JVM Performance Tuning](jvm-performance-tuning-interview.md)
- [Memory Management](memory-management-interview.md)
- [Network Performance](network-performance-interview.md)
- [Performance Testing](performance-testing-interview.md)
