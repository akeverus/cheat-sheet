---
title: "Вопросы на собеседовании: Design Dropbox"
description: "System design Dropbox: chunking (fixed vs CDC), Content-Addressable Storage (CAS), delta sync (rsync algorithm), Magic Pocket exabyte storage, conflict resolution, Smart Sync, multi-region, encryption."
tags:
  - interview
  - system-design
  - design-dropbox
type: "interview"
difficulty: "advanced"
aliases:
  - "Design Dropbox interview"
  - "File sync system design"
  - "Magic Pocket storage"
  - "rsync delta sync"
updated: "2026-05-26"
---

# Вопросы на собеседовании: `Design Dropbox`

`Dropbox` (Google Drive, OneDrive, iCloud Drive) — cloud file sync на 700M+ пользователей, эксабайты данных, миллиарды файлов. Кейс проверяет умение комбинировать chunking, content-addressable storage (CAS), delta sync через rolling hash (rsync), conflict resolution на mobile/desktop, multi-region replication, и собственный block storage уровня S3. Standalone сложный senior-кейс.

## Полезные ссылки

- [Dropbox Tech Blog](https://dropbox.tech/)
- [Magic Pocket — Dropbox exabyte storage (2016)](https://dropbox.tech/infrastructure/magic-pocket-infrastructure)
- [Magic Pocket follow-up (2020+)](https://dropbox.tech/infrastructure/extending-magic-pocket-innovation-with-the-first-petabyte-scale-smr-drive-deployment)
- [The rsync algorithm (Andrew Tridgell, 1996)](https://rsync.samba.org/tech_report/)
- [Content-Addressable Storage (Wikipedia)](https://en.wikipedia.org/wiki/Content-addressable_storage)
- [How Dropbox Sync works (Tech Blog)](https://dropbox.tech/infrastructure/streaming-file-synchronization)
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [LSM trees (Cassandra/RocksDB)](https://en.wikipedia.org/wiki/Log-structured_merge-tree)

## Содержание

**Requirements и capacity**
- [Q1. (!) Functional и non-functional requirements?](#q1--functional-и-non-functional-requirements)
- [Q2. (!) Capacity estimation (700M users, exabyte storage)?](#q2--capacity-estimation-700m-users-exabyte-storage)
- [Q3. SLA и SLO для sync pipeline?](#q3-sla-и-slo-для-sync-pipeline)

**Chunking и storage**
- [Q4. (!) Chunking: fixed 4MB vs content-defined (CDC)?](#q4--chunking-fixed-4mb-vs-content-defined-cdc)
- [Q5. (!) Content-Addressable Storage (CAS) — SHA256 → location?](#q5--content-addressable-storage-cas--sha256--location)
- [Q6. Metadata vs blob separation (Postgres + Magic Pocket)?](#q6-metadata-vs-blob-separation-postgres--magic-pocket)
- [Q7. (!) Magic Pocket — Dropbox proprietary exabyte storage?](#q7--magic-pocket--dropbox-proprietary-exabyte-storage)
- [Q8. Compression перед upload (LZ4 vs Zstandard)?](#q8-compression-перед-upload-lz4-vs-zstandard)

**Sync engine**
- [Q9. (!) Delta sync — rsync algorithm (rolling + strong hash)?](#q9--delta-sync--rsync-algorithm-rolling--strong-hash)
- [Q10. (!) Sync engine architecture (watcher → diff → upload → notify)?](#q10--sync-engine-architecture-watcher--diff--upload--notify)
- [Q11. File system events (inotify / FSEvents / ReadDirectoryChangesW)?](#q11-file-system-events-inotify--fsevents--readdirectorychangesw)
- [Q12. Offline mode и local LSM journal?](#q12-offline-mode-и-local-lsm-journal)
- [Q13. (!) Notification service (long-polling vs WebSocket)?](#q13--notification-service-long-polling-vs-websocket)
- [Q14. Selective sync и Smart Sync (lazy fetch)?](#q14-selective-sync-и-smart-sync-lazy-fetch)

**Conflict и versioning**
- [Q15. (!) Conflict resolution при concurrent edits?](#q15--conflict-resolution-при-concurrent-edits)
- [Q16. Versioning (30-day history, point-in-time restore)?](#q16-versioning-30-day-history-point-in-time-restore)
- [Q17. Trash and retention (30-day soft delete)?](#q17-trash-and-retention-30-day-soft-delete)

**Архитектура**
- [Q18. (!) High-level architecture (block + metadata + notification)?](#q18--high-level-architecture-block--metadata--notification)
- [Q19. (!) Multi-region (data residency EU, replication)?](#q19--multi-region-data-residency-eu-replication)
- [Q20. Disaster recovery (cross-region + erasure coding)?](#q20-disaster-recovery-cross-region--erasure-coding)

**Features**
- [Q21. Sharing model (folders, links, ACL)?](#q21-sharing-model-folders-links-acl)
- [Q22. Search (Elasticsearch metadata + content extraction OCR)?](#q22-search-elasticsearch-metadata--content-extraction-ocr)
- [Q23. Mobile uploads (camera roll, battery-aware)?](#q23-mobile-uploads-camera-roll-battery-aware)

**Безопасность**
- [Q24. (!) Encryption at rest и in transit?](#q24--encryption-at-rest-и-in-transit)
- [Q25. End-to-end encryption (E2EE) — ограничения?](#q25-end-to-end-encryption-e2ee--ограничения)
- [Q26. Anti-abuse (DMCA, malware scanning)?](#q26-anti-abuse-dmca-malware-scanning)

**Production**
- [Q27. (!) Monitoring metrics обязательные?](#q27--monitoring-metrics-обязательные)
- [Q28. Quota и throttling per-user?](#q28-quota-и-throttling-per-user)
- [Q29. (!) Cost optimization (Magic Pocket vs S3, dedup)?](#q29--cost-optimization-magic-pocket-vs-s3-dedup)
- [Q30. (!) Антипаттерны и подводные камни?](#q30--антипаттерны-и-подводные-камни)

## Q1. (!) Functional и non-functional requirements?

**Функциональные:**
- Загрузка / скачивание файлов любого размера.
- Синхронизация между N устройствами (desktop, mobile, web).
- Шаринг (папки, ссылки с правами read/edit).
- Версионирование + восстановление прежних версий.
- Офлайн-доступ — локальный кэш.
- Selective sync — выбрать, что скачивать.
- Корзина + хранение 30 дней.
- Поиск по метаданным + содержимому файлов.

**Нефункциональные:**
- Пользователи: 700M+ (Dropbox 2024).
- Файлы: 1B+ загрузок ежедневно.
- Хранилище: эксабайты (Magic Pocket > 1 EB с 2016).
- Лаг синхронизации: p95 < 5 сек (мелкие изменения файлов).
- Throughput загрузки: зависит от сети, батчевые чанки.
- Доступность: 99.99%.
- Надёжность хранения: 99.999999999% (11 девяток, как у S3).
- Мульти-регион: EU data residency (GDPR), US, APAC.
- Шифрование: at-rest + in-transit обязательно.

**Что исключаем из scope (типично):**
- Совместное редактирование в реальном времени (Google Docs / Paper — отдельный продукт).
- Транскодирование видео (в Dropbox это расширение, не ядро).
- Голосовой чат / мессенджер.

**Совет:** senior отличается тем, что сразу проговаривает `dedup как первоклассная фича` — Dropbox экономит 80%+ хранилища на дубликатах через CAS.

## Q2. (!) Capacity estimation (700M users, exabyte storage)?

**Исходные допущения:**

| Параметр | Значение |
|---|---|
| Всего пользователей | 700M |
| Активных пользователей | ~200M MAU |
| Среднее хранилище на пользователя | 5 GB (микс free 2 GB + платные планы) |
| Всего сырого хранилища | 3.5 EB raw |
| Коэффициент дедупликации | 80% (industry estimate) |
| После дедупликации | ~700 PB уникальных блоков |
| Темп загрузки | 1B+ файлов/день |

**Расчёт хранилища:**
- Без дедупликации: 700M × 5 GB = 3.5 EB.
- С дедупликацией (популярные расшаренные файлы) → 700 PB уникальных чанков.
- × 3 фактор erasure coding → 2.1 EB физических (Magic Pocket).

**Метаданные:**
- Запись о файле: ~500 B (путь + размер + список чанков + права).
- 1B файлов × 500 B = 500 GB метаданных.
- + история (30-дневные версии) ×10 = 5 TB.

**Полоса (bandwidth):**
- Загрузка: 1B файлов/день × в среднем 1 MB = 1 PB/день = ~12 GB/сек устойчиво.
- Скачивание (промах Smart Sync): ~10× от загрузки = 120 GB/сек.
- Пик (рабочий день, пересечение 9-17 US/EU) ×3 = 360 GB/сек.

**QPS на metadata DB:**
- 200M MAU × 100 операций/день = 20B операций/день ≈ 230K операций/сек.
- Пик ×5 = ~1M операций/сек — нужен sharded Postgres / Vitess.

**Вывод по capacity:**
- Хранилище доминирует в стоимости.
- Дедупликация критична — без неё стоимость хранилища × 5.
- Экономия Magic Pocket vs S3 ~30-50% (Q7).

## Q3. SLA и SLO для sync pipeline?

| Метрика | Цель | Alert |
|---|---|---|
| `sync_lag_seconds_p95` | < 5 сек (мелкие файлы) | > 30 сек 10 минут |
| `upload_success_rate` | > 99% | < 97% 5 минут |
| `download_latency_p99` | < 200 ms (из кэша) | > 1 сек |
| `metadata_query_p99` | < 50 ms | > 200 ms |
| `dedup_ratio` | > 80% | < 60% (падение эффективности) |
| `availability` | 99.99% | < 99.9% за месяц |
| `durability` | 99.999999999% (11 девяток) | событие потери данных |

**Сценарии отказов:**
- Network partition: очередь синхронизации растёт; возобновление при reconnect.
- Падение metadata DB: клиент буферизует изменения в локальном журнале (Q12).
- Падение block storage: чтение через реплику, запись с retries.

## Q4. (!) Chunking: fixed 4MB vs content-defined (CDC)?

**Зачем chunking:**
- Большой файл (1 GB) — нельзя загрузить одним куском (длинная блокировка, дорогие retries).
- Дедупликация на уровне чанков (если 2 файла отличаются на 1%, нужно загрузить только diff).

**Фиксированный размер чанков (4 MB):**
- Режем файл на блоки по 4 MB начиная с offset 0.
- Плюсы: просто, предсказуемо.
- Минусы: **проблема вставки** — добавил байт в начало → все чанки сдвинулись → 100% повторная загрузка вместо diff.

**Content-Defined Chunking (CDC):**
- Используем **rolling hash** (Rabin fingerprint, Buzhash, Gear, FastCDC).
- На каждой позиции вычисляем hash скользящего окна (например 48 байт).
- Граница чанка = когда hash mod N == 0 (чанк завершается).
- Граница зависит от содержимого, а не от offset.

**Свойства CDC:**
- Вставка в начало → меняется только первый чанк; границы остальных те же (rolling hash их восстанавливает).
- Средний размер чанка ~4 MB (настраивается через mod N).
- Ограничения min/max размера (1-16 MB) против вырождения.

**Пример rolling hash (Buzhash):**
```
hash(i) = (hash(i-1) >> 1) ^ T[byte_at(i + window_size)]
```
- O(1) обновление на каждый сдвинутый байт.

**Реальность Dropbox:**
- Исторически фиксированные чанки 4 MB.
- Современные системы (Restic, BorgBackup) — CDC через Buzhash.
- Tradeoff: простота vs эффективность дедупликации.

**Гибрид:** возможно, фиксированный для метаданных, CDC для blob-файлов, где проблема вставки критична.

## Q5. (!) Content-Addressable Storage (CAS) — SHA256 → location?

**Идея:** ключ = hash(содержимого), а не произвольный ID.

```
chunk = bytes (4 MB)
sha256_hex = SHA256(chunk)  // 32 bytes = 64 hex chars
storage_key = sha256_hex
storage.put(sha256_hex, chunk)
```

**Свойства:**

**1. Бесплатная дедупликация:**
- Два юзера загружают один и тот же файл → одинаковый SHA256 → одна запись в хранилище.
- При commit метаданные просто ссылаются на существующий чанк.
- Экономия 80%+ на популярных файлах (шаблоны Microsoft Office, иконки, PDF-книги).

**2. Проверка целостности:**
- При чтении: hash(скачанного) == storage_key? Да → целостность в порядке.
- Bit rot обнаруживается сразу.

**3. Неизменяемость:**
- Коллизия хэшей = одинаковое содержимое (для SHA256 криптографически невозможна).
- Изменение содержимого = новый хэш = новая запись. Старые чанки никогда не модифицируются.

**4. Сборка мусора:**
- Когда нет ссылок → можно безопасно удалить (отслеживание refcount).

**Схема:**
```sql
CREATE TABLE blocks (
  sha256 CHAR(64) PRIMARY KEY,
  size INT,
  location_id BIGINT,
  refcount INT,
  created_at TIMESTAMP
);

CREATE TABLE file_chunks (
  file_id BIGINT,
  chunk_idx INT,
  sha256 CHAR(64) REFERENCES blocks(sha256),
  PRIMARY KEY (file_id, chunk_idx)
);
```

**Поток загрузки:**
1. Клиент режет файл на чанки (Q4).
2. Вычисляет SHA256 каждого чанка.
3. Отправляет список SHA256 на сервер — `какие из них у тебя уже есть?`.
4. Сервер возвращает список недостающих чанков.
5. Клиент загружает только недостающие чанки.
6. Сервер обновляет метаданные: `file F = [sha1, sha2, sha3, ...]`.

**Production:** внутри Dropbox, Git (каталог objects), IPFS, бэкап Restic.

**Edge case:** коллизия SHA256 — теоретически 2^256 попыток; на практике не наблюдалась ни разу.

## Q6. Metadata vs blob separation (Postgres + Magic Pocket)?

**Двухуровневое хранилище:**

**Уровень метаданных (Postgres / Vitess, шардированный):**
- Путь к файлу, имя, размер, modified_at, владелец, права, ссылки на чанки.
- История версий (ограниченного объёма).
- Расшаренные ссылки, ACL.
- Шардирование по user_id.

**Уровень блобов (Magic Pocket):**
- Само содержимое файлов (чанки).
- Append-only, неизменяемое.
- Дёшево за гигабайт ($/GB в месяц).

**Зачем разделять:**
- Разные паттерны доступа: метаданные = OLTP с интенсивными чтениями/записями, блобы = последовательные bulk-операции.
- Разные типы хранилищ: метаданные на SSD ($$$), блобы на HDD/SMR ($).
- Независимое масштабирование.

**Объём:**
- Метаданные: 5-10 TB всего (шардированы по 100 PG-нодам).
- Блобы: 700 PB (Magic Pocket).
- Соотношение: 1:70 000.

**Слой кэширования:**
- EdgeCache (типа memcached) для горячих метаданных.
- 95%+ запросов к метаданным обслуживается из кэша.

**Согласованность между уровнями:**
- Commit метаданных — ПОСЛЕ успешной загрузки блоба.
- Если блоб осиротел (загружен, но commit метаданных упал) → собирается мусорщиком через N дней (refcount = 0).

**Паттерн:** аналог Git — `objects/` (блобы) + `refs/` (указатели метаданных).

## Q7. (!) Magic Pocket — Dropbox proprietary exabyte storage?

**Контекст:** до 2015 Dropbox хранил всё в AWS S3 ($$$). 2015-2016 — миграция на собственный Magic Pocket.

**Зачем собственное хранилище:**
- Стоимость AWS S3 на эксабайтном масштабе = $1B+/год.
- Magic Pocket снизил стоимость на 75% (открытые цифры Dropbox).
- Контроль над tiering, erasure coding, железом.

**Архитектура Magic Pocket:**

```
Cell (rack) → Zone (multiple racks) → Region (multiple zones)
```

**Компоненты:**
- **Storage nodes:** кастомное железо с SMR-дисками (Shingled Magnetic Recording — плотнее, чуть медленнее seek).
- **OSD (Object Storage Daemon):** хранит сырые байты.
- **Pocket Master:** сервер метаданных для cell.
- **Volume Manager:** координирует erasure coding.
- **Garbage Collector:** освобождает удалённые блоки.

**Erasure coding:**
- Reed-Solomon (10, 4): 10 data shards + 4 parity → можно потерять 4 shard без потери данных.
- Накладные расходы хранилища 1.4× (vs 3× при полной репликации) — экономия 50%.
- Tradeoff: при отказе восстановление идёт медленнее.

**Раскладка данных:**
- Чанк файла → разбивается на 10 shards → вычисляются 4 parity shards.
- 14 shards распределяются по 14 разным cells.
- Чтение: достаточно любых 10 из 14.

**Tiering:**
- Hot tier: SSD для недавно загруженного (последние 30 дней).
- Warm tier: HDD для того, что читают раз в неделю.
- Cold tier: SMR-диски для архива.

**Инновации:**
- SMR-диски с кастомной прошивкой (host-managed SMR).
- Кастомная сеть 100 Gbps между cells.
- Координация ремонта на петабайтном масштабе.

**Сравнимые системы:**
- Facebook f4 (BLOB storage).
- Backblaze Vaults.
- Google Colossus.

## Q8. Compression перед upload (LZ4 vs Zstandard)?

**Принцип:** сжимаем на клиенте перед загрузкой — экономим полосу + хранилище.

**Кодеки:**

| Кодек | Степень | Скорость (encode) | CPU | Применение |
|---|---|---|---|---|
| LZ4 | 2.1× | 500 MB/s | низкая | реальное время, low-latency |
| Snappy | 2.0× | 250 MB/s | низкая | внутри Google |
| Zstandard | 2.8× | 400 MB/s | средняя | сбалансированный (современный дефолт) |
| gzip | 2.9× | 50 MB/s | высокая | legacy, медленный |
| Brotli | 3.5× | 30 MB/s | высокая | веб-ресурсы (статика) |
| LZMA / xz | 4.0× | 5 MB/s | очень высокая | архив |

**Выбор Dropbox:**
- Zstandard (с ~2018) — лучший баланс.
- Дёшев на декодировании (decode 800 MB/s) — латентность чтения не страдает.
- Encode 400 MB/s — приемлемо на клиентском desktop / mobile.

**Уже сжатые файлы:**
- JPEG, PNG, MP4, ZIP, PDF (сжаты внутри) — повторное сжатие не помогает.
- Определяем по magic bytes или MIME → пропускаем сжатие.

**Сжатие до vs после чанкинга:**
- До чанкинга: сжимаем весь файл, потом режем → теряем дедупликацию (сжатые байты != сырые байты).
- После чанкинга: сначала режем, потом сжимаем каждый чанк отдельно → дедупликация сохраняется.
- Dropbox: сжатие после чанкинга.

**Словарь сжатия на чанк:**
- Zstandard поддерживает кастомный словарь, обученный на типичных типах данных.
- Office-файлы, PDF — выигрыш доп. 10-20% сжатия.

## Q9. (!) Delta sync — rsync algorithm (rolling + strong hash)?

**Сценарий:** пользователь меняет 1 KB в файле на 1 GB → надо загрузить только diff, а не 1 GB.

**Наивный подход (diff на уровне блоков):**
- Фиксированные чанки 4 MB → изменение 1 KB в одном чанке → повторная загрузка 4 MB.
- В 4000× меньше, чем весь файл, но всё равно много.

**Алгоритм rsync (Andrew Tridgell, 1996):**

**Отправитель (со старым файлом):**
1. Режет старый файл на блоки размера `B` (~700 байт).
2. На каждый блок вычисляет: rolling hash (быстрый) + strong hash (MD5/SHA1).
3. Отправляет получателю словарь `{strong_hash → block_index}`.

**Получатель (с новым файлом):**
1. Вычисляет rolling hash в скользящем окне ширины `B` начиная с offset 0.
2. На каждой позиции: проверяет, есть ли rolling hash в словаре.
3. Если попадание: верифицирует strong hash (защита от коллизии rolling hash).
4. Если совпало: выдаёт ссылку "use block N"; перепрыгивает на B байт.
5. Если промах: выдаёт сырой байт; сдвигает окно на 1 байт.

**На выходе:** список ссылок + сырые байты.

**Rolling hash (упрощённый Adler-32):**
```
hash(i+1) = hash(i) - byte_at(i) + byte_at(i + B)
```
- O(1) обновление на сдвинутый байт → O(file_size) суммарно.

**Свойства:**
- Вставка в начало: только маленькая дельта (байт спереди + ссылка на остальное).
- Правка в середине: маленькая дельта вокруг правки + ссылки на неизменённое.
- Дозапись: передаются только новые байты.

**Реалистичный выигрыш:**
- Office-файл 10 MB, правка 1 абзаца → загрузка ~30 KB дельты вместо полной перезаливки.
- Видеофайл 1 GB, правка метаданных → аналогично — изменился только блок метаданных.

**Реализация Dropbox:**
- Streaming sync: чанки определяются через CDC (Q4) → уже воплощает rsync-подобную идею.
- Дедупликация на уровне блоков через CAS даёт экономию на дельтах бесплатно.

## Q10. (!) Sync engine architecture (watcher → diff → upload → notify)?

```mermaid
graph LR
    FS[File System]
    FS -->|inotify/FSEvents| W[Watcher]
    W -->|change events| Q[Local queue]
    Q --> D[Diff engine<br/>chunk + hash]
    D -->|new SHA256s| Check[Server check<br/>which missing?]
    Check -->|miss list| Up[Uploader<br/>parallel]
    Up -->|chunks| Block[Block Server]
    Up -->|commit| Meta[Metadata Server]
    Meta -->|notify| N[Notification Service]
    N -->|push| Peers[Peer devices]
```

**Компоненты:**

**1. File watcher:**
- Слушает события файловой системы ОС (Q11).
- Батчит быстрые изменения (debounce 100 ms).

**2. Локальная очередь (SQLite):**
- Незавершённые загрузки сохраняются локально.
- Переживает падения / рестарты.

**3. Diff engine:**
- Для каждого изменённого файла:
  - Перерезает на чанки (Q4).
  - Вычисляет SHA256 каждого чанка.
  - Сравнивает с последним известным состоянием (серверный манифест).
- На выходе: список новых/изменённых чанков.

**4. Проверка на сервере:**
- Отправляем список SHA256.
- Сервер: какие уже есть? Возвращает список недостающих.
- Избегаем повторной загрузки дубликатов (экономим полосу).

**5. Uploader:**
- Параллельная загрузка (10-50 одновременных соединений).
- Мультиплексирование HTTP/2.
- Возобновление при частичном сбое.

**6. Commit метаданных:**
- После загрузки всех чанков → commit метаданных.
- "File F теперь указывает на чанки [s1, s2, s3]".

**7. Уведомление:**
- Другие устройства получают push-уведомление.
- Они скачивают новые чанки (лениво или сразу — зависит от настройки Smart Sync).

**Бюджет латентности:**
- Обнаружение: 10-100 ms (inotify).
- Чанкинг + хэш: 100 ms - 1 сек (упирается в CPU).
- Загрузка: зависит от размера файла и сети.
- Commit на сервере: 50 ms.
- Push на устройства-пиры: 100 ms - 1 сек (уведомление в реальном времени).
- Итого для мелкого файла: ~1-2 сек до синхронизации с другим устройством.

## Q11. File system events (inotify / FSEvents / ReadDirectoryChangesW)?

**API событий по ОС:**

| ОС | API | Гранулярность | Лимиты |
|---|---|---|---|
| Linux | inotify | события на файл | `fs.inotify.max_user_watches` (дефолт 8K) |
| macOS | FSEvents | события на каталог (грубее) | без лимита, батчевые |
| Windows | ReadDirectoryChangesW | события на файл | на handle |

**inotify (Linux):**
- События: IN_CREATE, IN_MODIFY, IN_DELETE, IN_MOVED_FROM, IN_MOVED_TO.
- Нужно рекурсивно навешивать watch на подкаталоги.
- Лимит: `/proc/sys/fs/inotify/max_user_watches` — увеличить до 1M для большой папки Dropbox.

**FSEvents (macOS):**
- Высокоуровневое: "этот каталог изменился". Не точное событие.
- Латентность 1-5 сек, батчево.
- Stream API через `FSEventStreamCreate`.

**ReadDirectoryChangesW (Windows):**
- Overlapped I/O, асинхронный callback.
- Отслеживает создание, изменение, переименование, смену атрибутов файла.

**Edge cases:**
- Атомарное сохранение в текстовых редакторах (запись во временный + rename) → 2 события: CREATE временного, RENAME → файл. Sync engine должен трактовать это как модификацию.
- Массовый импорт (10K файлов) → лавина событий; батчить + дедуплицировать.
- Отключение внешнего (USB) диска → watchers инвалидируются; перезапуск при reconnect.

**Кросс-платформенная абстракция:**
- Клиент Dropbox использует кастомный слой watch поверх API ОС.
- На выходе: унифицированный поток событий → diff engine.

## Q12. Offline mode и local LSM journal?

**Сценарий:** пользователь меняет файлы офлайн (самолёт), потом подключается — должны синхронизироваться без потерь.

**Локальный журнал (SQLite или LSM):**
- Очередь незавершённых изменений: упорядоченный список операций.
- Переживает падения, перезагрузки.
- Идемпотентные операции.

**Операции в очереди:**
- Создать файл F (путь, чанки).
- Обновить файл F.
- Удалить файл F.
- Переименовать A → B.

**Схема (SQLite):**
```sql
CREATE TABLE pending_ops (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  op_type TEXT,
  file_path TEXT,
  chunk_list TEXT,  -- JSON array
  created_at INTEGER,
  retry_count INT DEFAULT 0
);
```

**Поток возобновления при reconnect:**
1. Забираем состояние сервера (начиная с last_sync_token).
2. Сравниваем локальный журнал с изменениями сервера:
   a. Только локальные → загрузка.
   b. Только серверные → скачивание.
   c. Оба изменили один файл → конфликт (Q15).
3. Применяем по порядку: локальные создания → загрузки → серверные обновления → локальные удаления.

**Edge cases:**
- Долгий офлайн (месяцы) → журнал большой → батчить по приоритету (сначала недавние файлы).
- Локальный диск заполнен → отклоняем новые записи; предупреждаем пользователя.

**Паттерн:** аналог Git commit log + push при reconnect.

## Q13. (!) Notification service (long-polling vs WebSocket)?

**Цель:** когда пользователь меняет файл на устройстве A, устройство B узнаёт об этом за 1-5 сек.

**Подходы:**

**Polling (наивный):**
- Клиент каждые 30 сек: `GET /changes?since=T`.
- Минусы: 200M MAU × 2 опроса/мин = 400M запросов/мин = 7M QPS просто на polling.
- Запросы впустую, когда ничего не менялось.

**Long-polling:**
- Клиент открывает соединение: `GET /changes?since=T` — сервер УДЕРЖИВАЕТ запрос.
- Сервер держит 30-60 сек; при изменении → отвечает немедленно.
- Ниже QPS, но каждое соединение держится 30 сек.

**WebSocket / Server-Sent Events:**
- Постоянное двунаправленное соединение.
- Server push при изменении.
- Латентность меньше секунды.
- Минусы: держит миллионы соединений — серверный ресурс.

**Реализация Dropbox:**
- Long-polling (исторически) — Notification Service.
- Гибрид с WebSocket для desktop-приложений (постоянное соединение).

**Масштабирование:**
- 200M MAU одновременных соединений.
- Каждое соединение держится в среднем 60 сек → 200M / 60s × 1 conn = непрерывная нагрузка.
- Распределяем по тысячам notification-серверов (шардирование по user_id).
- Каждый notification-сервер держит ~50K-100K соединений.

**Push на мобильные:**
- FCM (Firebase Cloud Messaging) для Android.
- APNs (Apple Push Notification) для iOS.
- Держать соединение не нужно — доставку берёт на себя ОС.

**Payload пуша:**
- Минимальный: `{ "user_id": ..., "since_cursor": ... }`.
- Получатель подтягивает сами изменения уже после уведомления.

## Q14. Selective sync и Smart Sync (lazy fetch)?

**Selective sync:**
- Пользователь выбирает, какие папки скачивать локально.
- Остальные папки доступны только через web/mobile.
- Фича Dropbox до 2017 года.

**Smart Sync (2017+, Pro-фича):**
- Все файлы видны в файловой системе как обычные (stub-файлы).
- Содержимое скачивается по требованию (открыл → подтянулось).
- Фоновое вытеснение (LRU), когда диск заполнен.

**Реализация Smart Sync:**

**Linux / macOS:** виртуальная ФС типа FUSE.

**Windows:** Cloud Files API (Windows 10+) — первоклассная поддержка "online-only" файлов на уровне ОС.

**macOS (современный):** API File Provider Extension.

**Поток stub-файла:**
1. Файл создан на сервере → метаданные синхронизируются на все клиенты.
2. Локальная ОС показывает stub (видны имя, размер, дата изменения — но 0 байт на диске).
3. Пользователь делает двойной клик → Smart Sync подтягивает реальное содержимое с сервера.
4. После N дней без обращений → фоновое вытеснение (обратно в stub).

**Поведение:**
- "Always available" — закреплённые файлы никогда не вытесняются.
- "Online-only" — никогда не хранятся локально.
- Авто: LRU-вытеснение, когда свободного места < порога.

**Оптимизация полосы:**
- Предзагрузка вероятных файлов (на основе паттернов доступа).
- Фоновое скачивание при простое сети.

## Q15. (!) Conflict resolution при concurrent edits?

**Сценарий:** Alice правит `report.docx` на ноутбуке офлайн. Bob правит тот же `report.docx` на телефоне. Оба синхронизируются. Что происходит?

**Подходы:**

**1. Last-Writer-Wins (LWW):**
- Самый простой. Побеждает тот, чья синхронизация пришла последней.
- Другая версия перезаписывается.
- Минусы: тихая потеря данных.

**2. .conflict-файлы (подход Dropbox):**
- Сохраняем обе версии:
  - `report.docx` (версия Alice, закоммичена первой).
  - `report (Bob's conflicted copy 2026-05-26).docx` (версия Bob).
- Пользователь решает вручную.
- Потери данных нет, но есть неудобство для пользователя.

**3. Векторные часы (как в Git):**
- Каждое устройство ведёт векторные часы на файл.
- Конфликт обнаруживается, когда векторы конкурентны (ни один не предок другого).
- Приложение может смержить или форкнуть.

**4. CRDT (Conflict-Free Replicated Data Types):**
- Для конкретных типов документов (текст, списки).
- Автоматический мерж без участия пользователя.
- Используется в Google Docs, Figma, Notion.
- Dropbox Paper использует Y.js (CRDT-библиотека).

**Реальность Dropbox:**
- Для бинарных файлов (.docx, .pdf): `.conflict`-файлы (вариант 2).
- Для совместных документов (Paper): CRDT.

**Алгоритм обнаружения конфликта:**
- У каждого файла есть `parent_revision_id`.
- Загрузка включает `expected_parent_revision`.
- Сервер проверяет: current_revision == expected_parent?
  - Да → fast-forward commit.
  - Нет → конфликт → создаём .conflict-файл.

**Edge case:**
- Трёхсторонний конфликт (3 устройства правят одновременно) → несколько .conflict-файлов.

## Q16. Versioning (30-day history, point-in-time restore)?

**Функциональность:**
- Все версии файла хранятся N дней (30 по умолчанию, 1 год+ для платных планов).
- Пользователь может восстановить любую версию.
- "Rewind" — откат всей папки к прежнему состоянию.

**Реализация:**
- CAS + история метаданных.
- Каждая ревизия файла = новая запись метаданных, указывающая на (возможно новые) чанки.
- Старые чанки НЕ удаляются, пока на них ссылаются исторические версии.
- Сборка мусора после истечения срока хранения.

**Схема:**
```sql
CREATE TABLE file_revisions (
  file_id BIGINT,
  revision_id BIGINT,
  chunk_list JSONB,
  size BIGINT,
  modified_at TIMESTAMP,
  modified_by BIGINT,
  PRIMARY KEY (file_id, revision_id)
);
```

**Стоимость хранения:**
- Версионирование = ×N хранилища всех файлов.
- Спасает CAS: дублируются только изменённые чанки. Неизменённые чанки общие для всех версий.
- Реалистичные накладные расходы 1.5-2× (vs ×30 дней, если хранить каждый байт).

**Политика хранения:**
- Free / Plus: 30 дней.
- Business: 180 дней.
- Enterprise: без лимита (с поправкой на стоимость).

**Поток восстановления:**
- Пользователь выбирает ревизию → метаданные создают новую "текущую" ревизию = копию старой.
- Другие устройства синхронизируют это как обычное обновление файла.

**Массовое восстановление (Rewind):**
- Проигрываем все изменения до timestamp → получаем снапшот.
- Тяжёлая операция; ограничивается по частоте.

## Q17. Trash and retention (30-day soft delete)?

**Мягкое удаление:**
- Пользователь удаляет файл → перемещается в Корзину.
- Выставляется флаг метаданных `deleted_at`; физически не удаляется.
- Восстанавливается из UI Корзины.

**Хранение:**
- 30 дней в Корзине (по умолчанию).
- Через 30 дней → окончательное удаление.
- "Очистить корзину" позволяет вычистить сразу.

**Фоновый GC:**
- Ежедневная задача: ищет `deleted_at < NOW - 30 days`.
- Помечает чанки на уменьшение refcount.
- Когда refcount чанка = 0 → его можно удалить из хранилища.
- Реальное удаление чанков: недельным батчем (избегаем горячих операций).

**Схема:**
```sql
ALTER TABLE files ADD COLUMN deleted_at TIMESTAMP;
CREATE INDEX idx_deleted ON files (deleted_at) WHERE deleted_at IS NOT NULL;
```

**Edge cases:**
- Файл восстановлен из Корзины → флаг сброшен, возобновляется обычный поток.
- Шаринг из Корзины: расшаренная ссылка ломается? Да — мягко удалённые файлы недоступны по share.

**Compliance:**
- GDPR right-to-erasure: явный endpoint полной очистки (минует 30-дневное хранение).
- Флаг legal hold блокирует удаление (сценарии судебных разбирательств).

## Q18. (!) High-level architecture (block + metadata + notification)?

```mermaid
graph LR
    Client[Desktop/Mobile/Web]
    Edge[Edge POP<br/>connection LB]
    Auth[Auth Service]
    Meta[Metadata Service<br/>sharded Postgres/Vitess]
    Block[Block Service]
    MP[(Magic Pocket<br/>exabyte storage)]
    Notif[Notification Service<br/>long-polling]
    Search[Search Service<br/>Elasticsearch]
    Cache[(EdgeCache<br/>metadata)]
    Kafka[(Kafka<br/>events)]
    Analytics[Analytics Pipeline]

    Client --> Edge
    Edge --> Auth
    Edge --> Meta
    Edge --> Block
    Edge -.long poll.-> Notif
    Meta --> Cache
    Meta -->|events| Kafka
    Block --> MP
    Kafka --> Notif
    Kafka --> Search
    Kafka --> Analytics
```

**Сервисы:**

**Edge POP:**
- Географические точки входа (US-East, US-West, EU, APAC, India).
- Терминация TLS.
- Пулинг соединений.

**Auth Service:**
- OAuth-токены, управление сессиями.
- Проверка 2FA.

**Metadata Service:**
- Иерархия файлов, ACL, версии.
- Шардированный Postgres или Vitess.
- Партиция на пользователя.

**Block Service:**
- API к Magic Pocket.
- Загрузка / скачивание чанков.
- Управление refcount.
- Координация сборки мусора.

**Notification Service:**
- Соединения long-polling (или WebSocket).
- Push-события на подключённые устройства.

**Search:**
- Индекс Elasticsearch по метаданным.
- Извлечение содержимого файлов (PDF, Word) → индексируемый текст.

**Kafka:**
- Шина событий: file_created, file_modified, file_deleted, sharing_changed.
- Консьюмеры: индексатор поиска, аналитика, диспетчер уведомлений.

**Мульти-регион:**
- Каждый регион — полный стек.
- Межрегиональная асинхронная репликация для HA + data residency.

## Q19. (!) Multi-region (data residency EU, replication)?

**Драйверы:**
- Латентность: EU-пользователь → EU-датацентр < 50 ms.
- GDPR: данные EU-клиентов должны оставаться в EU.
- DR: региональный сбой = автоматический failover.

**Архитектура:**
- US-East-1 (Virginia) — основной US.
- EU-West-1 (Ireland) — основной EU.
- AP-Northeast-1 (Tokyo) — основной APAC.
- ap-south-1 (Mumbai) — Индия.

**На регион:**
- Полный Metadata Service.
- Локальные cells Magic Pocket.
- Notification Service.

**Межрегиональная репликация:**
- Метаданные: асинхронная репликация (лаг 5-30 сек).
- Блобы: на регион (без авто-репликации между регионами; контент пользователя остаётся в домашнем регионе).

**Маршрутизация:**
- Пользователь привязан к домашнему региону по стране регистрации.
- Edge POP-ы закрывают латентность для просмотра.

**Межрегиональный шаринг:**
- US-пользователь делится папкой с EU-пользователем → EU-пользователь получает доступ; блоб тянется межрегионально при первом обращении, далее кэшируется локально.
- Добавляет латентность при первом чтении.

**Data residency:**
- EU-пользователи — блобы и метаданные строго в EU (DC Frankfurt / Dublin).
- Китаю нужен отдельный стек (compliance) — Dropbox исключён с китайского рынка.

**Failover:**
- Регион упал → DNS переключает на соседний.
- Чтения метаданных продолжаются с реплики.
- Записи приостановлены до восстановления primary (CP-модель для записей).

## Q20. Disaster recovery (cross-region + erasure coding)?

**Сценарии отказов:**
- Отказ одного диска: erasure coding (Q7) обрабатывает прозрачно.
- Отказ стойки: избыточность на уровне cell.
- Отказ датацентра: межрегиональная асинхронная репликация.
- Региональная катастрофа: межрегиональный failover.

**Erasure coding (Reed-Solomon 10+4):**
- Данные разбиваются на 10 shards + 4 parity.
- Можно потерять 4 shard → всё равно восстановимо.
- Накладные расходы хранилища 1.4× (vs 3× при репликации).

**Межрегиональная репликация:**
- Метаданные: каждый commit → асинхронная репликация в 1 резервный регион.
- Блобы: НЕ реплицируются между регионами (на эксабайтном масштабе слишком дорого).
- Tradeoff: потеря региона = потеря blob-данных (редкое событие).

**Бэкап:**
- Ежедневный снапшот метаданных в S3 Glacier.
- Восстановление из снапшота в экстремальных сценариях.

**RTO/RPO:**
- RPO (потеря данных): ~30 сек (лаг асинхронной репликации).
- RTO (время восстановления): минуты для метаданных; часы для блобов (если блобы реплицированы межрегионально).

**Учения по DR:**
- Ежеквартальный chaos-тестинг (аналогично Netflix Q23 в design-netflix).
- Game days с имитацией отказов регионов.

## Q21. Sharing model (folders, links, ACL)?

**Типы шаринга:**

**Шаринг папки:**
- Приглашаем пользователя → он присоединяется как участник.
- Права: viewer, editor, owner.
- Наследование подпапками.

**Шаринг по ссылке:**
- Генерируем URL вида `dropbox.com/sh/abc123/file.docx`.
- Права: view, download, edit (Pro).
- Опционально: пароль, дата истечения.

**Модель ACL:**
- На папку: список пользователей + права.
- Наследуется от родителя, если не переопределено.
- Публичные ссылки обходят ACL (любой, у кого есть ссылка).

**Схема:**
```sql
CREATE TABLE folder_acl (
  folder_id BIGINT,
  principal_type TEXT,  -- 'user', 'group', 'link'
  principal_id BIGINT,
  permission TEXT,  -- 'view', 'edit', 'owner'
  inherited_from BIGINT,
  PRIMARY KEY (folder_id, principal_type, principal_id)
);

CREATE TABLE shared_links (
  link_token CHAR(32) PRIMARY KEY,
  folder_id BIGINT,
  permission TEXT,
  password_hash TEXT,
  expires_at TIMESTAMP,
  created_at TIMESTAMP
);
```

**Поток авторизации:**
- Запрос доступа к папке F.
- Проверяем ACL: пользователь в участниках? → уровень прав.
- Либо: проверяем токен расшаренной ссылки в запросе.

**Edge cases:**
- Пользователя убрали из расшаренной папки → теряет доступ немедленно; текущие загрузки получают невалидный токен на следующем запросе.
- Ссылку отозвали → 404 при обращении.
- Удаление расшаренной папки → уведомить всех участников.

**Квота:**
- Расшаренные папки засчитываются в квоту владельца.
- Участники видят папку, но она не списывается с их квоты.

## Q22. Search (Elasticsearch metadata + content extraction OCR)?

**Уровни:**

**1. Поиск по метаданным:**
- Имя файла, путь, владелец, modified_at.
- Фильтр по типу (PDF, изображения, документы).
- Быстро (< 100 ms).

**2. Поиск по содержимому:**
- Полнотекстовый по телу документа.
- PDF, Office-файлы, обычный текст.

**Пайплайн извлечения содержимого:**
- Событие загрузки → Kafka → воркер извлечения.
- Воркер скачивает чанк → извлекает текст:
  - PDF: pdfminer / Apache Tika.
  - Word/Excel: Apache Tika.
  - Изображения: OCR (Tesseract, Google Cloud Vision).
- Извлечённый текст → Elasticsearch.

**OCR по изображениям:**
- Фото чека → извлечённый текст становится доступен поиску.
- Tradeoff: стоимость (OCR на каждое изображение), точность.

**Индекс:**
- Elasticsearch шардирован по user_id.
- Изоляция индекса на пользователя (приватность).
- Многоязычные анализаторы.

**Латентность:**
- Лаг индексации: 30 сек - 5 минут после загрузки.
- Поисковый запрос: < 200 ms.

**Приватность:**
- Извлечение выполняется в защищённом окружении.
- Извлечённый текст шифруется at-rest.
- Не индексируем zero-knowledge зашифрованные файлы (Q25).

## Q23. Mobile uploads (camera roll, battery-aware)?

**Фича Camera Upload:**
- Автозагрузка фото / видео с телефона в Dropbox.
- Фоновый процесс.

**Сложности:**
- Мобильная сеть дорогая / медленная.
- Расход батареи.
- Ограниченное локальное хранилище на телефоне.

**Лучшие практики:**

**С учётом сети:**
- По умолчанию: только Wi-Fi (пропускаем сотовую).
- Опционально: сотовая с предупреждением.
- Пауза при тарифицируемой сети.

**С учётом батареи:**
- Пропускаем, когда заряд < 20%.
- Возобновляем при зарядке.

**Чанки + возобновляемость:**
- Каждое фото режется на чанки (Q4).
- Возобновление при обрыве сети.
- Большие видео: продолжаются за несколько сессий.

**Сохранение EXIF:**
- Сохраняем метаданные: дата съёмки, GPS, модель камеры.
- Удаление чувствительных данных опционально (приватность).

**Фоновое планирование:**
- iOS: Background App Refresh (ограниченные окна).
- Android: WorkManager с условиями (Wi-Fi + зарядка).

**Дедупликация фото:**
- Одно фото на разных устройствах → дедупликация через CAS.
- Экономит полосу (повторная загрузка бесплатна) и хранилище.

**Оригинальное качество vs оптимизированное:**
- По умолчанию: полное оригинальное качество.
- Pro-опция: уменьшенные фото для экономии квоты.

## Q24. (!) Encryption at rest и in transit?

**In transit:**
- TLS 1.3 (с 2020).
- Mutual TLS для внутренних сервисов.
- Certificate pinning в мобильных приложениях (защита от MITM).

**At rest:**
- Шифрование AES-256-GCM на каждый чанк.
- Каждый чанк зашифрован уникальным ключом (per-chunk key).
- Сами per-chunk-ключи зашифрованы master-ключом.
- Master-ключи в HSM (Hardware Security Module).

**Иерархия ключей:**
```
Root Key (HSM, never exits)
    ↓ encrypts
Master Keys (per-region, rotated yearly)
    ↓ encrypts
Per-chunk Data Keys (random per chunk)
    ↓ encrypts
Chunk content
```

**Хранение:**
- Зашифрованный чанк: AES-256-GCM(data, per_chunk_key, nonce).
- Хранилище per-chunk-ключей: зашифровано master-ключом, лежит рядом с метаданными.

**Производительность:**
- Аппаратное ускорение AES-NI → 1-2 GB/s на ядро.
- Влияние на throughput пренебрежимо мало.

**Compliance:**
- Аудит SOC 2, HIPAA, GDPR.
- Процедуры ротации ключей задокументированы.

**Ограничения:**
- Ключи держит Dropbox (не пользователь) → может расшифровать по судебному запросу.
- Решение: E2EE (Q25).

## Q25. End-to-end encryption (E2EE) — ограничения?

**Зачем:** даже Dropbox не может расшифровать данные. Ключи держит клиент.

**Реализация:**
- Клиент шифрует файл ключом под контролем пользователя перед загрузкой.
- Сервер хранит зашифрованные байты, не знает содержимого.

**Реальность Dropbox:**
- Обычный Dropbox: серверное шифрование (Q24) — ключи держит Dropbox.
- Dropbox Vault (2020+): опция E2EE для чувствительных папок.
- Boxcryptor (сторонний) — E2EE-обёртка, куплена Dropbox в 2022.

**Компромиссы E2EE:**

| Возможность | Серверное шифр. | E2EE |
|---|---|---|
| Доступ сервера к расшифровке | Да | Нет |
| Поиск по содержимому | Да | Нет (зашифровано) |
| Превью-миниатюры | Да | Нет |
| OCR / поиск | Да | Нет |
| Шаринг по ссылке | Да | Ограничен (обмен ключами) |
| Восстановление при забытом пароле | Да | Безвозвратная потеря |
| Compliance (SOC, HIPAA) | Просто | Проще (меньше ответственности) |

**Управление ключами:**
- Master-ключ на пользователя выводится из пароля (PBKDF2 / Argon2).
- Опционально: ключ восстановления (seed-фраза из 24 слов).
- Потерял ключ = потерял данные (Dropbox не восстановит).

**Сценарии применения E2EE:**
- Юристы, врачи с PII.
- Активисты / журналисты.
- Чувствительные госдокументы.

**Применимо не всегда** — большинство обычных пользователей предпочитают удобство (поиск, шаринг) шифрованию E2EE.

## Q26. Anti-abuse (DMCA, malware scanning)?

**DMCA (авторское право США):**
- Правообладатели подают DMCA-нотис → Dropbox обязан удалить.
- Автодетект через сопоставление хэшей (если хэш copyright-файла совпал в CAS).
- Хэш файла в списке takedown → загрузка отклоняется либо существующая копия удаляется.
- Пользователь уведомлён, может подать контр-претензию.

**Сканирование на malware:**
- Все загрузки сканируются (ClamAV или коммерческий AV).
- Известные хэши malware → отклонить + алерт.
- Запуск в sandbox для подозрительных файлов (редко, дорого).

**Фишинг / скам:**
- Расшаренная ссылка с вредоносным файлом → детектируется по репутации URL + содержимому.
- Подозрительные паттерны: широко расшарено + новый аккаунт → флаг.

**CSAM (материалы сексуального насилия над детьми):**
- Сопоставление хэшей с базой NCMEC.
- Обязательное информирование органов.
- Широко используется технология Microsoft PhotoDNA.

**Rate limiting:**
- Загрузки на пользователя (Q28).
- Лимит на IP для анонимных эндпоинтов.

**Блокировка аккаунта:**
- Повторные нарушения TOS → временная блокировка.
- Перманентный бан за тяжёлые нарушения (CSAM, преступная активность).

**Баланс авто-DMCA:**
- Возможны ложные срабатывания (схожие коллизии хэшей, fair use).
- Нужен процесс апелляции.

## Q27. (!) Monitoring metrics обязательные?

**Пайплайн синхронизации:**
- `sync_lag_seconds_p95` — от изменения файла до видимости на устройстве-пире (цель < 5 сек).
- `upload_success_rate` — % чанков, загруженных с первой попытки (цель > 99%).
- `upload_throughput_bytes_per_sec` — эффективность использования полосы.
- `pending_uploads_queue_depth` — индикатор backlog.

**Хранилище:**
- `dedup_ratio` — экономия от CAS (цель > 80%).
- `chunk_refcount_distribution` — здоровье системы refcount.
- `gc_lag_seconds` — backlog сборки мусора.
- `magic_pocket_usable_capacity_pct` — алерт при > 80%.

**Латентность:**
- `metadata_query_latency_p99` — < 50 ms.
- `block_download_latency_p99` — < 200 ms (из кэша).
- `notification_delivery_lag_seconds` — < 5 сек.

**Сценарии отказов:**
- `cross_region_replication_lag_seconds` — цель < 30 сек.
- `erasure_coding_repair_jobs_running` — слишком много = проблема.
- `client_offline_journal_size_bytes` — большой = проблемы синхронизации.

**Бизнес:**
- `daily_active_users` — вовлечённость.
- `storage_per_user_distribution` — сайзинг.
- `share_create_rate` — использование коллаборации.

**Алертинг:**
- Звонок дежурному: sync_lag_p95 > 30 сек 10 минут.
- Slack: upload_failure_rate > 3% 5 минут.
- Email: падение dedup_ratio > 10% (регрессия эффективности).

**Трейсинг:**
- OpenTelemetry; trace ID через client → server → storage.
- Видимость: `Upload took 2 sec = 100 ms chunking + 800 ms upload + 50 ms commit`.

## Q28. Quota и throttling per-user?

**Квоты (планы Dropbox):**
- Basic (free): 2 GB.
- Plus: 2 TB.
- Family: 2 TB на всех.
- Professional: 3 TB.
- Business Standard: 5 TB.
- Business Advanced: сколько нужно.

**Контроль:**
- При загрузке: проверяем квоту → отклоняем при превышении.
- Мягкое предупреждение на 90%.
- Жёсткая остановка на 100%.

**Троттлинг:**
- Лимит полосы загрузки на пользователя (настраивается в клиенте).
- На стороне сервера: лимит на API-вызовы на пользователя.

**Лимиты по тарифу:**
- API-запросов в час.
- Операций шаринга в день.
- Одновременных соединений.

**Допуск на всплески (burst):**
- Первичная загрузка большой библиотеки — временно повышенный лимит.
- Возврат к норме после завершения backfill.

**Анти-абьюз:**
- Агрессивные загрузки (10× от обычного пользователя) → флаг.
- Много шарингов в подозрительные адреса → на ревью.

**Схема (в Postgres):**
```sql
CREATE TABLE user_quota (
  user_id BIGINT PRIMARY KEY,
  plan TEXT,
  used_bytes BIGINT,
  limit_bytes BIGINT,
  last_calculated_at TIMESTAMP
);
```

**Расчёт квоты:**
- Сумма байтов всех НЕ удалённых файлов, принадлежащих пользователю.
- Исключает расшаренные папки, принадлежащие другим.
- Пересчитывается периодически (по событиям + по расписанию).

## Q29. (!) Cost optimization (Magic Pocket vs S3, dedup)?

**Драйверы стоимости:**
- Хранилище: эксабайты × $/GB в месяц.
- Полоса: egress (скачивания).
- Вычисления (metadata DB, sync engine).
- Сеть (межрегиональная репликация).

**Главные оптимизации:**

**1. Magic Pocket vs AWS S3:**
- Стоимость S3: $0.023/GB в месяц (standard).
- Внутренняя стоимость Magic Pocket: ~$0.005/GB в месяц (оценка).
- На 700 PB: $3M/мес vs $16M/мес → экономия $13M/мес.

**2. Дедупликация через CAS:**
- 80% дедупликации → экономия хранилища 5×.
- Без CAS стоимость хранилища Dropbox была бы в 5× выше.

**3. SMR-диски (Shingled Magnetic Recording):**
- На 20-30% плотнее CMR-дисков.
- Чуть медленнее seek (предпочтительна последовательная запись).
- Magic Pocket спроектирован под SMR.

**4. Erasure coding 10+4 vs репликация 3×:**
- Накладные расходы 1.4× vs 3× → экономия хранилища 53%.

**5. Сжатие (Q8) Zstandard:**
- Сокращение в 2-3× для сжимаемого контента.
- На 700 PB raw → 250-350 PB сжатых → экономия ~$1M/мес.

**6. Tiering:**
- Hot: SSD (недавнее + часто используемое).
- Cold: SMR HDD.
- Archive: лента или аналог Glacier (редко).

**7. CDN для скачиваний:**
- Кэшируем популярные расшаренные файлы на edge.
- Снижает egress-стоимость origin.

**8. Управление жизненным циклом:**
- Корзина → архивный tier через 7 дней.
- Версии старше 30 дней → холодное хранилище.

**Суммарная экономия:**
- Только миграция на Magic Pocket: снижение стоимости ~75% (по заявлению Dropbox).
- + дедупликация + erasure coding + сжатие: в совокупности ~10× дешевле, чем наивный S3.

## Q30. (!) Антипаттерны и подводные камни?

**1. Синхронная загрузка в потоке клиента.**
- Сохранение файла блокируется до завершения загрузки.
- Фикс: асинхронная очередь загрузки с обновлением UI по завершении.

**2. Нет чанкинга — повторная загрузка всего файла при правке.**
- Файл 1 GB, правка 1 KB → загрузка 1 GB.
- Фикс: чанкинг + delta sync (Q4, Q9).

**3. Нет content-addressable storage.**
- Один файл загружен N раз → хранится N копий.
- Фикс: CAS через SHA256 (Q5).

**4. Одноуровневое хранилище (всё на SSD).**
- $$$ непозволительно дорого на эксабайтном масштабе.
- Фикс: многоуровневое хранилище (hot SSD / cold HDD / archive).

**5. Синхронное уведомление на каждое изменение.**
- 1B загрузок в день × N пиров = взрыв уведомлений.
- Фикс: батчинг + long-polling/WebSocket (Q13).

**6. Опрос БД на предмет изменений.**
- 200M MAU × опрос каждые 30 сек = нагрузка впустую.
- Фикс: push-уведомления + событийность (Q13).

**7. Клиент постоянно опрашивает «есть что-то новое?».**
- DDoS на собственный бэкенд.
- Фикс: long-polling или WebSocket.

**8. Нет обнаружения конфликтов.**
- Конкурентные правки тихо перезаписывают друг друга.
- Фикс: проверка revision_id + .conflict-файлы (Q15).

**9. Полная синхронизация при reconnect после офлайна.**
- Папка 1 GB, был офлайн 1 минуту, синхронизирует ВСЕ файлы.
- Фикс: diff-синхронизация через revision-токены (Q12).

**10. Нет erasure coding.**
- 3× репликация ради надёжности → 3× стоимость.
- Фикс: Reed-Solomon 10+4 → накладные расходы 1.4×.

**11. Хранилище в одном регионе.**
- Сбой региона = сервис лежит + потенциальная потеря данных.
- Фикс: мульти-региональная асинхронная репликация (Q19).

**12. Нет шифрования.**
- Утечка данных = катастрофа.
- Фикс: at-rest AES-256-GCM + in-transit TLS 1.3 (Q24).

**13. Сжатие до чанкинга.**
- Теряется выгода дедупликации (сжатые байты не дедуплицируются).
- Фикс: сначала чанкинг, потом сжатие каждого чанка (Q8).

**14. Захардкоженные лимиты синхронизации.**
- «Максимум 10K файлов в папке» — ломается на пользователях с фотогалереями.
- Фикс: динамические лимиты по тарифу пользователя, постепенное масштабирование.

**15. Нет GC для осиротевших чанков.**
- Чанки с refcount = 0 копятся вечно.
- Фикс: фоновый GC + безопасная политика удаления с grace-периодом.

---

## See also

- [Design Netflix](design-netflix-interview.md) — video streaming parallels (CDN, multi-region)
- [Design YouTube](design-youtube-interview.md) — large blob storage parallels
- [Design URL Shortener](design-url-shortener-interview.md) — multi-tier cache parallels
- [System Design Interview](system-design-interview.md) — общая методология
- [Caching Strategies](../architecture/caching-strategies-interview.md) — EdgeCache, CDN
- [Distributed Systems](../architecture/distributed-systems-interview.md) — eventual consistency, CAP
- [Database Replication](../databases/database-replication-interview.md) — multi-region async
- [Database Sharding](../databases/database-sharding-interview.md) — metadata partitioning
- [Resilience Patterns](../architecture/resilience-patterns-interview.md) — circuit breaker, retry
- [CDN](../architecture/cdn-interview.md) — edge POPs для downloads
- [Kafka](../messaging/kafka-interview.md) — event backbone
- [Elasticsearch](../databases/elasticsearch-interview.md) — search, file content indexing
- [Cassandra](../databases/cassandra-interview.md) — alternative metadata store
