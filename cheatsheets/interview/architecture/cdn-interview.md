---
title: "Вопросы на собеседовании: CDN (Content Delivery Network)"
description: "Push/Pull CDN, edge cache, invalidation, TLS, HTTP/3, image/video acceleration, WAF/DDoS, edge compute — для system design интервью."
tags:
  - interview
  - architecture
  - cdn
type: "interview"
difficulty: "intermediate"
aliases:
  - "CDN interview"
  - "Content Delivery Network собеседование"
  - "Push Pull CDN"
  - "Edge caching"
  - "Anycast CDN"
updated: "2026-05-21"
---

# Вопросы на собеседовании: `CDN (Content Delivery Network)`

`CDN` — географически распределённая сеть кэширующих серверов (`edge`/`PoP`), которая отдаёт контент клиенту с ближайшей точки присутствия вместо origin-сервера. Снижает latency, разгружает egress на origin, повышает доступность и поглощает DDoS. На system design интервью CDN — почти всегда первый слой между пользователем и backend для любого крупного web/мобильного продукта.

Дата последнего обновления: 2026-05-21

**Ключевые игроки рынка:** `Cloudflare`, `Akamai`, `AWS CloudFront`, `Fastly`, `Google Cloud CDN`, `Bunny.net`. Все строятся вокруг трёх идей: **anycast routing к ближайшему PoP**, **многоуровневый кэш** и **edge compute для динамики**.

## Полезные ссылки

### Официальная документация

- [System Design Primer — Content Delivery Network](https://github.com/donnemartin/system-design-primer#content-delivery-network) — Push/Pull CDN, базовая теория
- [Cloudflare Learning — What is a CDN?](https://www.cloudflare.com/learning/cdn/what-is-a-cdn/) — концепции CDN от Cloudflare
- [AWS CloudFront — Developer Guide](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/) — поведение кэша, behaviors, invalidation
- [Fastly — Surrogate Keys](https://docs.fastly.com/en/guides/working-with-surrogate-keys) — purge by tag
- [Cloudflare Cache Tags](https://developers.cloudflare.com/cache/how-to/cache-keys/) — cache tags и purge by tag
- [MDN — HTTP caching](https://developer.mozilla.org/en-US/docs/Web/HTTP/Caching) — Cache-Control, ETag, Vary
- [RFC 9111 — HTTP Caching](https://www.rfc-editor.org/rfc/rfc9111) — каноническая семантика
- [HLS spec (RFC 8216)](https://www.rfc-editor.org/rfc/rfc8216) — HTTP Live Streaming
- [Web Cache Deception — 2017/2020](https://www.akamai.com/blog/security/web-cache-deception-attack) — оригинальная атака на Akamai
- [Cloudflare 2019 BGP leak — post-mortem](https://blog.cloudflare.com/how-verizon-and-a-bgp-optimizer-knocked-large-parts-of-the-internet-offline-today/) — incident
- [Fastly 2021 global outage — post-mortem](https://www.fastly.com/blog/summary-of-june-8-outage) — 1 час, Amazon/Reddit/UK gov
- [Akamai 2021 DNS outage](https://www.akamai.com/blog/news/Akamai-Summary-of-July-22-DNS-Disruption) — Steam/PSN/банки

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и архитектура**
- [Q1. (!) Что такое CDN и зачем он нужен?](#q1--что-такое-cdn-и-зачем-он-нужен)
- [Q2. (!) Архитектура CDN: PoP, edge, regional, origin shield, origin](#q2--архитектура-cdn-pop-edge-regional-origin-shield-origin)
- [Q3. Anycast routing — как CDN выбирает ближайший PoP?](#q3-anycast-routing--как-cdn-выбирает-ближайший-pop)
- [Q4. GeoDNS vs Anycast — что и когда?](#q4-geodns-vs-anycast--что-и-когда)
- [Q5. Иерархия кэша: edge → regional → origin shield](#q5-иерархия-кэша-edge--regional--origin-shield)

**Push vs Pull, кеширование**
- [Q6. (!) Push CDN vs Pull CDN — когда какой выбрать?](#q6--push-cdn-vs-pull-cdn--когда-какой-выбрать)
- [Q7. (!) Cache-Control, ETag, Last-Modified — как работают на CDN?](#q7--cache-control-etag-last-modified--как-работают-на-cdn)
- [Q8. Vary header и cache key — нормализация запроса](#q8-vary-header-и-cache-key--нормализация-запроса)
- [Q9. (!) Инвалидация кэша: purge by URL, purge by tag, версионирование](#q9--инвалидация-кэша-purge-by-url-purge-by-tag-версионирование)
- [Q10. stale-while-revalidate и stale-if-error](#q10-stale-while-revalidate-и-stale-if-error)
- [Q11. Surrogate-Control против Cache-Control — в чём разница?](#q11-surrogate-control-против-cache-control--в-чём-разница)
- [Q12. Range requests и кэширование больших файлов](#q12-range-requests-и-кэширование-больших-файлов)

**TLS / HTTP-протоколы**
- [Q13. (!) TLS termination на edge: что даёт и какие риски?](#q13--tls-termination-на-edge-что-даёт-и-какие-риски)
- [Q14. HTTP/2 и HTTP/3 (QUIC) на edge](#q14-http2-и-http3-quic-на-edge)
- [Q15. 0-RTT в TLS 1.3 — выигрыш и replay-риск](#q15-0-rtt-в-tls-13--выигрыш-и-replay-риск)

**Image / Video / Edge Compute**
- [Q16. (!) Оптимизация изображений: AVIF/WebP, srcset, ресайз на лету](#q16--оптимизация-изображений-avifwebp-srcset-ресайз-на-лету)
- [Q17. Видеостриминг: HLS, DASH, ABR, CMAF, LL-HLS](#q17-видеостриминг-hls-dash-abr-cmaf-ll-hls)
- [Q18. Dynamic Site Acceleration (DSA) — как кэшировать «динамику»](#q18-dynamic-site-acceleration-dsa--как-кэшировать-динамику)
- [Q19. (!) Edge compute — код на CDN: Cloudflare Workers, Lambda@Edge, Compute@Edge](#q19--edge-compute--код-на-cdn-cloudflare-workers-lambdaedge-computeedge)

**Безопасность (WAF, DDoS, cache poisoning)**
- [Q20. (!) Защита от DDoS: scrubbing, rate limiting, bot management](#q20--защита-от-ddos-scrubbing-rate-limiting-bot-management)
- [Q21. WAF на CDN: Cloudflare WAF, AWS WAF, Imperva](#q21-waf-на-cdn-cloudflare-waf-aws-waf-imperva)
- [Q22. (!) Cache poisoning и Web Cache Deception — как защищаться?](#q22--cache-poisoning-и-web-cache-deception--как-защищаться)
- [Q23. Cache-key smuggling и нормализация заголовков](#q23-cache-key-smuggling-и-нормализация-заголовков)

**Cost / Multi-CDN / Operations**
- [Q24. (!) Cost-модель CDN: egress, requests, edge CPU-ms](#q24--cost-модель-cdn-egress-requests-edge-cpu-ms)
- [Q25. Сравнение игроков: Cloudflare/Akamai/CloudFront/Fastly/Bunny](#q25-сравнение-игроков-cloudflareakamaicloudfrontfastlybunny)
- [Q26. (!) Multi-CDN — несколько CDN сразу: active-active, traffic steering, NS1/Cedexis](#q26--multi-cdn--несколько-cdn-сразу-active-active-traffic-steering-ns1cedexis)
- [Q27. Наблюдаемость CDN: логи, аналитика, RUM](#q27-наблюдаемость-cdn-логи-аналитика-rum)
- [Q28. Cold start cache MISS и pre-warming](#q28-cold-start-cache-miss-и-pre-warming)
- [Q29. Реальные инциденты: Fastly 2021, Akamai 2021, Cloudflare 2019](#q29-реальные-инциденты-fastly-2021-akamai-2021-cloudflare-2019)
- [Q30. Конкретные конфиги: CloudFront Behavior и Cloudflare Page Rules](#q30-конкретные-конфиги-cloudfront-behavior-и-cloudflare-page-rules)

## Q1. (!) Что такое CDN и зачем он нужен?

**CDN (Content Delivery Network)** — сеть распределённых кэширующих серверов (`edge`, `PoP`), которая отдаёт контент с ближайшей к пользователю точки и тем самым ставит слой кэша между клиентом и origin. Главная идея: не гонять каждый запрос через полконтинента до твоего сервера, а отвечать с узла в том же городе.

**Зачем нужен** — пять выгод, каждая вытекает из «кэш близко к клиенту»:
- **Задержка (latency)** — `edge` в 5-50 мс от пользователя вместо 100-300 мс до origin в другом регионе. TCP/TLS handshake тоже завершается на edge, поэтому медленный первый коннект сокращается в разы.
- **Разгрузка канала (bandwidth offload)** — 80-99% запросов закрываются кэшем edge и до origin вообще не доходят, так что origin платит меньше за egress и держит меньше нагрузки.
- **Доступность** — пока кэш свежий, origin может лежать, а контент всё равно отдаётся (`stale-if-error`). CDN превращается в буфер, переживающий падение бэкенда.
- **Поглощение DDoS** — у Cloudflare и Akamai ёмкость 100+ Tbps anycast; атака размазывается по сотням PoP, и до origin доходят крохи, которые убили бы его напрямую.
- **TLS на edge** — handshake близко к клиенту экономит 1-2 RTT на каждом новом соединении.

**Чего CDN не делает** (частый уточняющий вопрос на интервью):
- Не ускоряет запись — `POST`/`PUT` всё равно идут на origin, который их и обрабатывает.
- Не кэширует динамику «магически»: без правильных `Cache-Control`/`Vary` персонализированный ответ либо не кэшируется, либо кэшируется неверно.

**Архитектурно CDN это:**
```
Client ──anycast──► Edge PoP ──fill──► Regional cache ──► Origin shield ──► Origin
         (5-50 ms)                       (50-150 ms)                       (full)
```

**Итог:** для любого продукта с географически распределённой аудиторией и заметной долей статики/медиа CDN — практически обязательный первый слой между пользователем и backend.

## Q2. (!) Архитектура CDN: PoP, edge, regional, origin shield, origin

CDN — это не плоская «толпа edge-серверов», а каскад кэшей: запрос при промахе спускается на уровень глубже, и до origin доходит лишь то, что не нашлось нигде выше. Чем дальше уровень от клиента, тем меньше узлов и больше кэш.

**Уровни иерархии (от ближайшего к клиенту к origin):**

| Слой | Что это | Сколько узлов | RTT до клиента |
|------|---------|----------------|-----------------|
| **Edge PoP** | Точка присутствия в городе/IX | 200-300+ | 5-50 мс |
| **Regional cache** | Региональный кэш (CloudFront Regional Edge) | 10-20 | 30-100 мс |
| **Origin shield** | Один singleton-кэш перед origin | 1 на регион | — |
| **Origin** | Исходный сервер (S3, ALB, app) | 1-несколько | 100-300 мс |

**Зачем три уровня, а не один:** каждый следующий уровень отфильтровывает часть промахов предыдущего, поэтому origin видит лишь малую долю исходного трафика.
- **Edge PoP** — отдаёт hot-объекты. При cache MISS идёт не сразу в origin, а в regional — ближе и дешевле.
- **Regional cache** — собирает MISS-трафик десятков edge PoP в одном месте. То, что один edge не нашёл, другой уже мог сюда положить → второй шанс на cache HIT.
- **Origin shield** — единственная точка, которая ходит в origin. Без неё каждый regional при cache MISS дёргал бы origin сам, и редкий объект на холодную вызвал бы залп одновременных запросов — **thundering herd на origin**. Shield схлопывает этот залп в один запрос.

**Пример CloudFront:**
```
Client → Edge (CF PoP) → Regional Edge Cache → Origin Shield (опц.) → S3/ALB
```

**Origin shield** включается отдельно (CloudFront — `OriginShield`, Cloudflare — `Tiered Cache`). Особенно важно при больших каталогах (Netflix, e-commerce) — иначе при cache MISS на 200 PoP получаете 200 одновременных GET-ов на origin.

Путь запроса по каскаду (на каждом уровне — либо HIT и ответ наверх, либо MISS и спуск глубже):

- `Client` --anycast--> `Edge PoP` (5-50 мс).
- `Edge PoP` при MISS --> `Regional Cache` (30-100 мс).
- `Regional Cache` при MISS --> `Origin Shield` (1 на регион).
- `Origin Shield` при MISS --> `Origin` (S3 / ALB).
- `Edge PoP` при HIT отдаёт ответ прямо `Client` — не спускаясь ни на один уровень глубже.

**Итог:** PoP принимает запрос, regional агрегирует MISS-ы, shield защищает origin от thundering herd.

## Q3. Anycast routing — как CDN выбирает ближайший PoP?

**Anycast** — это когда один IP-адрес одновременно «живёт» во множестве дата-центров: CDN объявляет его через BGP из всех PoP сразу, а маршрутизаторы интернета сами выбирают ближайший путь по числу AS-hops и BGP-метрикам. Выбор узла происходит не в DNS, а на сетевом уровне (L3), и потому он прозрачен для клиента.

**Как это работает у CDN, по шагам:**
1. У CDN есть своя автономная система (AS) — например, AS13335 у Cloudflare.
2. Один и тот же `/24`-префикс анонсируется через BGP сразу из 300 PoP.
3. ISP клиента видит несколько маршрутов к этому префиксу и выбирает с самым коротким AS-path / лучшей метрикой.
4. Пакеты клиента сами «скатываются» в ближайший PoP — DNS-трюков и геолокации не требуется.

**Плюсы anycast** — все следуют из того, что выбор делает сеть, а не DNS:
- Один IP — клиенту не нужно DNS-разрешение под регион, меньше движущихся частей.
- DDoS распределяется по 300 PoP естественным образом: атака на «один IP» физически приземляется в сотнях точек.
- Failover автоматический — если PoP падает, он перестаёт анонсировать префикс, BGP пересходится за секунды и трафик уходит на соседний узел.

**Минусы** — обратная сторона «решает BGP, а не ты»:
- Меньше контроля: можно попасть не в физически ближайший, а в «лучший по BGP» PoP. Например, из РФ маршрут иногда уходит через Германию, хотя локальный PoP ближе.
- Stateful-протоколы (TCP) могут «перепрыгнуть» на другой PoP при смене маршрута и порвать соединение — Cloudflare лечит это consistent hashing, привязывая поток к одному узлу.

**Пример:** клиент с IP `213.x.x.x` (Москва) делает запрос к `1.1.1.1`:
- BGP-таблица Ростелекома: маршрут через MSK-IX → ближайший Cloudflare PoP в Москве.
- RTT: 5-10 мс.

## Q4. GeoDNS vs Anycast — что и когда?

Это два способа направить клиента к ближайшему узлу, но работают они на разных уровнях: GeoDNS выбирает узел при разрешении имени, Anycast — при маршрутизации пакетов. Отсюда и все их различия.

**GeoDNS** — авторитативный DNS возвращает разный A-record в зависимости от региона клиента (определяется по IP резолвера или точнее через EDNS Client Subnet). Выбор делается один раз, на этапе DNS-запроса.

**Anycast** — один IP анонсируется через BGP, и узел выбирает сама сеть на каждом соединении (см. Q3).

**Сравнение:**

| Аспект | GeoDNS | Anycast |
|--------|---------|---------|
| Уровень | DNS (L7) | BGP (L3) |
| Failover | минуты (TTL DNS) | секунды (BGP) |
| Гранулярность | по country/ASN | по AS-path |
| Стоимость | дешёво (NS1, Route53) | дорого (нужны AS+IP блоки) |
| Контроль | высокий (override на уровне страны) | низкий (определяется BGP) |
| Привязка (sticky) | нет (resolver кэширует) | на каждое соединение |

**На практике их сочетают.** Это не «или-или»: anycast рулит трафиком внутри одного CDN, а GeoDNS — между разными CDN.
- Базовый routing внутри одного провайдера — его anycast IP.
- GeoDNS поверх (NS1, Cedexis) выбирает сам провайдера (Cloudflare vs Akamai) по региону и здоровью — это уже multi-CDN (Q26).

**Пример:** ozon.ru ходит на Cloudflare через anycast, но NS1 GeoDNS сверху может направить 10% трафика из Урала на резервный Akamai при сбое — здесь GeoDNS делает то, чего anycast не умеет: выбор между вендорами.

## Q5. Иерархия кэша: edge → regional → origin shield

Иерархия кэша — это каскад из нескольких уровней, где каждый отсекает часть промахов предыдущего, так что вниз, к origin, доходит лишь остаток (архитектура уровней — в Q2). Чем ближе к клиенту, тем меньше кэш и короче TTL; чем глубже — тем больше ёмкость и реже промахи.

**Уровни и их роль:**
- **Edge cache** — `~1-10 GB` на PoP, держит только горячий рабочий набор; TTL для статики обычно 1 мин - 1 час.
- **Regional cache** — `~10-100 TB`, агрегирует промахи десятков edge и потому ловит то, что у одного edge ещё не осело.
- **Origin shield** — один singleton-кэш; его главная задача не вместимость, а **защита от лавины (анти-stampede)**.

**Как тают промахи (числа CloudFront)** — каждый уровень режет поток в разы:
- На edge промахивается 5-20% запросов (для хорошо кэшируемого контента).
- Из этих промахов на regional промахивается ещё 50-80%.
- Из них на origin shield промахивается 80-95% — до origin доходят только по-настоящему холодные объекты.

**Итоговый эффект**: при 100k QPS от клиентов origin видит лишь 50-500 QPS — нагрузка падает в 200-2000 раз. Именно ради этого множителя и нужны три уровня вместо одного.

## Q6. (!) Push CDN vs Pull CDN — когда какой выбрать?

Разница в одном: **кто и когда кладёт контент на edge**. В Pull его подтягивает сам CDN по факту первого запроса; в Push ты заливаешь его заранее. Всё остальное (стоимость, свежесть, cold-start) — следствия этого выбора.

**Pull CDN (Pull-through, ленивый):** контент попадает на edge по требованию.
- Origin содержит мастер-копию.
- На первый запрос CDN ловит cache MISS, тянет файл из origin и кладёт в кэш.
- Повторные запросы — уже cache HIT, origin не трогается.
- Свежесть управляется TTL и заголовками (`Cache-Control`, инвалидация).

**Push CDN (Push-on-publish, упреждающий):** контент попадает на edge заранее.
- Контент **заливается на CDN до запросов** — например, шагом CI/CD после публикации.
- Origin после публикации можно даже выключить — он больше не нужен для отдачи.
- CDN становится единственным источником правды для пользователя.

**Сравнение:**

| Параметр | Pull CDN | Push CDN |
|----------|----------|----------|
| Настройка | минуты — поменять DNS | сложнее — нужен publish-pipeline |
| Каталог | любой размер (lazy) | ограничен ёмкостью CDN |
| Свежесть | TTL/инвалидация | контролируется публикацией |
| Стоимость | egress origin × доля MISS | egress только при публикации |
| Холодный старт | MISS-задержка для первого пользователя | нет MISS — всё уже в кэше |
| Идеален для | большого динамического каталога | маленького статического набора |

**Когда Pull:**
- E-commerce (миллионы SKU) — большая часть товаров запрашивается редко.
- Ответы API, динамическая персонализация.
- Пользовательский контент (Instagram, YouTube — частично push для вирусного).

**Когда Push:**
- Маркетинговые лендинги — известный набор файлов, важна моментальная доступность.
- Релизы ПО (`.exe`/`.dmg`) — пик нагрузки в первые часы, MISS-задержка недопустима.
- Игровые патчи (Steam, PSN) — Akamai/CloudFront раскладывают (push) на сотни PoP заранее.
- Ассеты для live-событий — заранее разложить плеер/тексты, чтобы при старте трансляции PoP-ы уже были «горячие».

**Гибрид:** большинство prod-систем — Pull CDN + ручной прогрев (prewarm) для известных горячих объектов (новый релиз, рекламная кампания).

Поток запросов (участники: `Client`, `Edge PoP`, `Origin`):

**Pull CDN:**
1. `Client → Edge PoP`: `GET /img/x.jpg`.
2. `Edge PoP → Origin`: MISS — fetch (на edge копии нет, идёт в origin).
3. `Origin → Edge PoP`: `200` + `Cache-Control`.
4. `Edge PoP → Client`: `200`.
5. `Client → Edge PoP`: `GET /img/x.jpg` (повторно).
6. `Edge PoP → Client`: HIT (без обращения к origin).

**Push CDN:**
1. `Origin → Edge PoP`: `PUT /img/x.jpg` (publish pipeline — контент заливается заранее).
2. Примечание: после публикации origin может быть offline.
3. `Client → Edge PoP`: `GET /img/x.jpg`.
4. `Edge PoP → Client`: `200` (no origin — отдаётся из кэша без origin).

**Итог:** Pull — default для большинства случаев; Push — для известного небольшого hot-set с критичной cold-start latency.

## Q7. (!) Cache-Control, ETag, Last-Modified — как работают на CDN?

Эти три заголовка задают origin'у два разных вопроса: «сколько хранить копию, не переспрашивая» (`Cache-Control`) и «как дёшево проверить, не протухла ли копия» (`ETag`/`Last-Modified`). Первый управляет TTL, вторые — ревалидацией.

**Cache-Control** — главный заголовок, задаёт TTL и поведение кэша. Ключевой нюанс — он адресует *два разных кэша* одновременно: браузерный (`max-age`) и общий CDN (`s-maxage`).
- `max-age=3600` — браузер хранит копию 1 час.
- `s-maxage=86400` — общий кэш (CDN) хранит 24 часа; для CDN важнее именно он.
- `public` / `private` — `private` запрещает кэшировать на CDN (только в браузере пользователя).
- `no-cache` — кэшировать можно, но перед отдачей надо переспросить origin (ревалидация через ETag). Это не «не кэшировать».
- `no-store` — не кэшировать вообще, нигде.
- `immutable` — браузер/CDN не ревалидирует даже после reload; ставится только для версионированных URL, которые никогда не меняются под тем же именем.
- `stale-while-revalidate=60`, `stale-if-error=600` — см. Q10.

**ETag** — отпечаток версии ресурса (хэш). Origin отдаёт его, браузер при ревалидации шлёт обратно в `If-None-Match`; если совпало — CDN/origin отвечает `304 Not Modified` без тела, экономя трафик.

**Last-Modified** — время последнего изменения; клиент шлёт `If-Modified-Since`. Запасной механизм ревалидации, когда `ETag` нет.

**Когда что:**

| Заголовок | Назначение | На что влияет |
|-----------|------------|---------------|
| `Cache-Control: s-maxage` | TTL для CDN | сколько edge держит без revalidate |
| `Cache-Control: max-age` | TTL для браузера | сколько браузер не идёт даже в CDN |
| `ETag` | Ревалидация | 304 без тела при совпадении |
| `Last-Modified` | Ревалидация | запасной вариант, если нет ETag |

**Тонкости:**
- `s-maxage` переопределяет `max-age` для общего кэша (CDN). Это позволяет давать CDN 24h, а браузеру 5 минут.
- Если origin не шлёт `Cache-Control`, CDN использует свои дефолтные TTL (CloudFront — 24h, Cloudflare — по типу контента).
- `ETag` weak (`W/"..."`) vs strong — weak допускает семантически эквивалентные отличия.

**Пример:**
```http
Cache-Control: public, max-age=300, s-maxage=86400, stale-while-revalidate=3600
ETag: "abc123"
Last-Modified: Mon, 21 May 2026 10:00:00 GMT
```
- Браузер кэширует 5 минут.
- CDN кэширует 24 часа.
- При истечении s-maxage — отдаёт stale ещё час, фоном идёт за свежим.

## Q8. Vary header и cache key — нормализация запроса

**Cache key** — это «отпечаток запроса», по которому CDN решает, считать два запроса одним и тем же объектом или разными. По умолчанию ключ — `URL` (path + query string): один URL = одна копия в кэше.

**Vary header** добавляет в ключ значения перечисленных заголовков, чтобы один URL мог иметь несколько вариантов ответа:
```http
Vary: Accept-Encoding, Accept-Language
```
Теперь для одного URL CDN держит отдельные копии под каждую комбинацию: `gzip+ru`, `br+ru`, `gzip+en`… Это нужно, когда ответ реально зависит от заголовка (например, сжатие или язык).

**Опасность `Vary` — фрагментация кэша.** Каждое уникальное значение заголовка плодит новую копию, а копии конкурируют за место и «размывают» статистику попаданий:
- `Vary: User-Agent` при миллионе разных UA = миллион копий одного URL → почти каждый запрос промахивается, доля HIT падает к нулю, а смысла в этом нет — ответ от UA обычно не зависит.
- `Vary: *` — указание никогда не кэшировать.

**Cache key customization:**
- CloudFront: `CachePolicy` определяет какие headers/query string включать в ключ.
- Cloudflare: `Cache Rules` или legacy `Page Rules` — что игнорировать (например, `?utm_source=...`).
- Fastly: VCL `vcl_hash` — полный контроль.

**Рекомендации:**
- Игнорировать tracking-параметры в query string (`utm_*`, `fbclid`, `gclid`) — иначе каждая рекламная ссылка с уникальным `utm_*` создаёт новый cache MISS, хотя контент тот же.
- `Vary: Accept-Encoding` почти всегда уместен (разные кодировки gzip vs br — реально разные тела).
- `Vary: Accept-Language` — только если на одном URL живёт несколько локализаций.
- НЕ ставить `Vary: User-Agent` — кэш фрагментируется в ноль без всякой пользы.

**Пример нормализации (CloudFront CachePolicy):**
```json
{
  "QueryStringsConfig": { "QueryStringBehavior": "whitelist", "QueryStrings": ["id", "size"] },
  "HeadersConfig": { "HeaderBehavior": "whitelist", "Headers": ["Accept-Encoding"] },
  "CookiesConfig": { "CookieBehavior": "none" }
}
```

## Q9. (!) Инвалидация кэша: purge by URL, purge by tag, версионирование

Инвалидация решает главную боль кэша: контент изменился, а edge всё ещё отдаёт старую копию. Есть четыре способа её устранить, и они идут по нарастанию «точечности» — от грубого удаления по URL до полного обхода проблемы через версионирование.

**1. Purge by URL** — удалить конкретный URL из кэша.
- CloudFront: `aws cloudfront create-invalidation --paths /img/x.jpg` (стоит $0.005 за path после 1000 free).
- Cloudflare: API `POST /zones/<id>/purge_cache` с `{"files": ["..."]}`.
- Время propagation: 30 сек - 5 мин.
- Минусы: дорого при тысячах URL, нельзя purge wildcard на CloudFront (только prefix).

**2. Purge by tag (Surrogate-Key / Cache Tags)**:
- Origin отдаёт заголовок `Cache-Tag: product-42, category-shoes` (Cloudflare) или `Surrogate-Key: product-42 category-shoes` (Fastly).
- Purge: `POST /zones/<id>/purge_cache {"tags": ["product-42"]}` — все объекты с этим тегом инвалидируются.
- **Time-to-purge: <150 мс глобально (Fastly).**
- Идеально для e-commerce: изменился product → purge tag → инвалидируется product page + listing + search results.

**3. Versioning через URL (cache busting)** — не инвалидировать, а обойти инвалидацию вовсе:
- `/static/main.abc123.js` — хеш контента прямо в имени файла.
- Изменился контент → изменился хеш → это уже **другой URL**, который никогда не был в кэше; старый никто не запрашивает и он спокойно истекает по TTL.
- Не требует API-вызовов и работает на любом CDN — поэтому это идеал для статики.
- Так делают webpack/vite/rollup для JS/CSS.

**4. TTL refresh** — ничего не делать и подождать, пока истечёт `s-maxage`. Годится для данных, где задержка обновления некритична.

**Сравнение:**

| Метод | Задержка | Стоимость | Сложность |
|-------|---------|------|-----------|
| Purge by URL | 30 сек - 5 мин | $$ | Низкая |
| Purge by tag | <1 сек | $ | Средняя (теги на origin) |
| Версионирование URL | мгновенно (новый URL) | 0 | Низкая (build-инструмент) |
| Истечение TTL | TTL | 0 | 0 |

**Что использовать:**
- **Статические ассеты (JS/CSS)** → версионирование + `immutable`.
- **Динамический контент (HTML, API)** → purge by tag (Fastly), `s-maxage=0 + must-revalidate` для критичных.
- **Контент конкретного пользователя** → `private` + не кэшировать на CDN.

**Пример Fastly Surrogate-Key:**
```http
Cache-Control: public, max-age=86400
Surrogate-Key: product-42 category-shoes brand-nike
```
Когда обновляется товар 42:
```bash
curl -X POST https://api.fastly.com/service/SID/purge \
  -H "Surrogate-Key: product-42"
```

## Q10. stale-while-revalidate и stale-if-error

Обе директивы решают одну проблему: что показать клиенту, когда кэш протух. Базовый кэш в этот момент заставил бы клиента ждать поход в origin (а при падении origin — отдал бы ошибку). Эти директивы разрешают на короткое окно отдать слегка устаревшую копию вместо ожидания или ошибки.

**`stale-while-revalidate=<sec>`** — после истечения `max-age`/`s-maxage` CDN немедленно отдаёт клиенту **устаревший (stale)** ответ, а **в фоне** идёт за свежим и обновляет кэш.
- Зачем: клиент не платит MISS-задержку за обновление — за неё «платит» фоновый запрос.
- Компромисс: в течение `<sec>` секунд после истечения часть пользователей видит слегка устаревшую версию.

**`stale-if-error=<sec>`** — если origin ответил 5xx или таймаутом, CDN до `<sec>` секунд продолжает отдавать последнюю устаревшую копию.
- Зачем: продукт остаётся живым даже при упавшем origin — кэш работает как аварийный буфер.
- Компромисс: пользователь не узнаёт, что origin сломан, и может действовать на старых данных.

**Пример:**
```http
Cache-Control: max-age=60, stale-while-revalidate=600, stale-if-error=86400
```
- 60 сек — свежий ответ.
- 600 сек после — устаревший ответ + фоновая ревалидация.
- 86400 сек после — устаревший ответ при ошибке origin.

**Поддержка:**
- CloudFront: с 2023 года.
- Cloudflare: `Always Online` (похоже).
- Fastly: VCL `restart`/`pass`.
- Браузеры: Chrome 75+.

**Когда применять:**
- Новостные сайты, листинги — статья с устареванием в 5 минут лучше, чем 5xx.
- API с терпимыми к устареванию клиентами — для пользователя актуальность некритична.

**Когда НЕ применять:**
- Банкинг, платежи — финансовый риск при устаревших данных.
- Данные в реальном времени (биржа, спортивный счёт).

## Q11. Surrogate-Control против Cache-Control — в чём разница?

Оба заголовка решают одну задачу — задать CDN и браузеру **разные** TTL. Разница в том, кто их видит: `Cache-Control` уезжает и до браузера, и до CDN, а `Surrogate-Control` перехватывается edge и до клиента не доходит.

**Проблема:** одного `Cache-Control` мало, когда нужно дать CDN сутки, а браузеру — 5 минут (чтобы пользователь быстрее увидел обновление, а origin при этом был разгружен).

**Два решения:**

**1. `s-maxage` в Cache-Control** (стандарт RFC 7234):
```http
Cache-Control: max-age=300, s-maxage=86400
```
- Браузер: 5 минут (`max-age`).
- CDN (shared): 24 часа (`s-maxage`).
- Работает на CloudFront, Cloudflare, любом RFC-совместимом.

**2. `Surrogate-Control`** (Akamai/Fastly расширение):
```http
Cache-Control: max-age=300
Surrogate-Control: max-age=86400
```
- Surrogate-Control видят только CDN-edge (Akamai, Fastly), удаляют его перед отдачей клиенту.
- Браузер видит только Cache-Control.
- Удобно: можно ставить max-age для CDN и независимо для клиента, и клиент не видит CDN-специфичных директив.

**Когда использовать Surrogate-Control:**
- Хочешь чтобы клиент не знал TTL CDN.
- Используются Surrogate-специфичные директивы (например, Fastly `stale-while-revalidate`, `stale-if-error` исторически).
- Multi-CDN с разными TTL.

**В целом**: `s-maxage` универсальнее и достаточно для 95% случаев.

## Q12. Range requests и кэширование больших файлов

**Range request** — это запрос не всего файла, а его куска по диапазону байт:
```http
GET /video/big.mp4
Range: bytes=0-1048575
```
Origin/CDN отвечают `206 Partial Content` и отдают только запрошенный фрагмент.

**Зачем:** перемотка видео, докачка после обрыва (apt, brew) и работа с большими файлами (>100 MB) — клиенту не нужно тянуть весь файл, чтобы получить начало или продолжить с места обрыва.

**В чём сложность для CDN:** один файл запрашивается разными клиентами с разными диапазонами. Кэшировать каждый диапазон отдельной копией — путь к 1000 копий одного файла и нулевой доле HIT. Поэтому CDN кэшируют не диапазоны, а сам объект.

**Как CDN обрабатывают:**
- **CloudFront:** кэширует **целый объект** при первом полном fetch из origin (через `Origin Response Timeout`), потом отдаёт диапазоны из кэша. Если первый запрос — диапазон, CloudFront может тянуть полный объект фоном или не тянуть (зависит от поведения).
- **Cloudflare:** аналогично — `Range Request Cache` (Enterprise) кэширует чанки.
- **Akamai:** `Object Caching Behavior` — настройка, как обрабатывать диапазоны.
- **Fastly:** `vcl_fetch` с `restart` для полного fetch.

**Лучшие практики для больших файлов:**
- **Предварительная нарезка** на стороне origin: разрезать на чанки 1-4 MB заранее (сегменты HLS, DASH — Q17).
- **Origin shield** обязателен — иначе при холодном старте все edge тянут полный файл одновременно при MISS.
- Хранить файлы в S3 с `Multipart Upload` — origin поддерживает диапазоны естественно.

**Видео-стриминг** обычно использует HLS/DASH — там файл уже разрезан на чанки (.ts/.m4s), range request не нужен, каждый чанк — это обычный GET (см. Q17).

## Q13. (!) TLS termination на edge: что даёт и какие риски?

**TLS termination на edge** — это когда шифрованное соединение клиента расшифровывается не на твоём сервере, а на ближайшем PoP: там завершается handshake (`ClientHello`, обмен сертификатами), а до origin идёт отдельное, обычно долгоживущее TLS-соединение. Смысл — перенести дорогой handshake ближе к клиенту и переиспользовать связь с origin.

**Что даёт** — почти всё выигрывает от того, что handshake происходит в 5-50 мс от клиента, а не за 200 мс:
- **Задержка**: TLS 1.2 handshake = 2 RTT, TLS 1.3 = 1 RTT, 0-RTT возможно. На edge RTT 5-50 мс против 200 мс до origin → handshake в 10-40 раз быстрее.
- **Переиспользование соединений**: 100 клиентов делают handshake на edge, edge держит один пул к origin (`keep-alive` + HTTP/2 multiplexing).
- **OCSP stapling**: edge каждые 5-10 мин запрашивает OCSP-response у CA и прикладывает к ServerHello — клиент не идёт в CA сам. Экономит 100-300 мс на первое соединение.
- **SNI**: edge обслуживает тысячи сертификатов на одном IP по `Server Name Indication`. У Cloudflare один anycast IP = миллионы доменов.
- **Управление сертификатами**: автоматический Let's Encrypt у Cloudflare, ACM у CloudFront — без ручного продления.

**Риски** — все из одного факта: трафик расшифровывается у провайдера, а не у тебя:
- **Соответствие/PCI-DSS** — CDN видит открытый текст (Cloudflare расшифровывает весь трафик клиента). Для PCI-данных нужен либо `Keyless SSL` (приватный ключ остаётся у клиента), либо сквозной (end-to-end) TLS без termination.
- **Ошибочно выпущенные сертификаты** — скомпрометированный CDN способен выпустить сертификат на твой домен и выдавать себя за тебя.
- **Доступ государства** — провайдера по закону его юрисдикции могут обязать выдать расшифрованный трафик.

**Способы снизить риск:**
- **Authenticated origin pulls** — origin принимает только TLS-запросы с клиентским сертификатом от CDN. Если кто-то обходит CDN — он не достучится до origin.
- **Cloudflare Keyless SSL / AWS Certificate Manager Private CA** — приватный ключ держится у клиента; CDN дёргает Keyless-сервер для подписи.
- **Сквозной TLS** — CDN пропускает TLS насквозь, не терминируя (но теряешь edge-оптимизации).

## Q14. HTTP/2 и HTTP/3 (QUIC) на edge

Ключевая разница между ними — транспорт. HTTP/2 живёт поверх TCP и потому страдает от head-of-line blocking на уровне соединения; HTTP/3 переехал на QUIC поверх UDP и эту проблему снял. Edge поддерживает оба и сам выбирает лучший доступный.

**HTTP/2** (RFC 7540, 2015) — мультиплексирование поверх одного TCP:
- Много потоков (streams) в одном TCP-соединении, но потеря одного пакета тормозит все потоки сразу — head-of-line blocking на L4 остаётся.
- Server Push (фактически устарел, почти не использовали).
- Сжатие заголовков (HPACK).
- TLS обязателен на практике.

**HTTP/3** (RFC 9114, 2022) — тот же мультиплекс, но на новом транспорте:
- Транспорт — **QUIC** поверх UDP (RFC 9000), с интегрированным TLS 1.3.
- 0-RTT и 1-RTT handshake — соединение устанавливается за один круг или вовсе без него.
- **Нет head-of-line blocking на транспорте** — потерянный пакет тормозит только свой поток, остальные едут дальше. Это и есть главный выигрыш над HTTP/2.
- Миграция соединений — при смене IP (Wi-Fi → LTE) сессия не рвётся, потому что QUIC идентифицирует её по Connection ID, а не по паре адресов.

**Преимущества HTTP/3 на edge:**
- Особенно заметно на мобильных и теряющих пакеты сетях (3G, метро, поезда) — задержка на 20-40% ниже.
- Миграция соединений важна для long-poll/streaming.
- 0-RTT даёт handshake менее 50 мс (против 200 мс TCP+TLS 1.2).

**Поддержка:**
- Cloudflare — с 2019 (одни из первых).
- CloudFront — с 2022.
- Fastly — с 2022.
- Chrome, Firefox, Safari — все поддерживают.

**Включение:**
```
Cloudflare → SSL/TLS → Edge Certificates → HTTP/3 (with QUIC) → ON
CloudFront → Distribution → Edit → Supported HTTP versions → HTTP/2 and HTTP/3
```

**HTTP/3 alt-svc:**
Сервер отдаёт `Alt-Svc: h3=":443"` — клиент в следующий раз пробует HTTP/3.

**Тонкости:**
- HTTP/3 = UDP. Некоторые корпоративные firewall блокируют UDP/443 — клиент откатывается на HTTP/2.
- HTTP/3 ещё не везде оптимально настроен в ядрах Linux (нужен io_uring/XDP для полной производительности).

## Q15. 0-RTT в TLS 1.3 — выигрыш и replay-риск

**0-RTT** (Zero Round-Trip Time) — оптимизация TLS 1.3, при которой клиент шлёт данные **в первом же пакете**, не дожидаясь завершения handshake. Это убирает целый круговой обмен при повторном подключении — но ценой риска replay-атаки.

**Как это работает** (возможно только при повторном коннекте):
1. Клиент уже соединялся раньше и сохранил `session ticket` от сервера.
2. При новом соединении он шифрует данные ключом `PSK` (Pre-Shared Key) из этого тикета и отправляет их сразу вместе с `ClientHello`.
3. Сервер достаёт PSK, расшифровывает, обрабатывает запрос и отвечает — лишний RTT на установку не потрачен.

**Выигрыш:**
- TLS 1.2 handshake = 2 RTT (~50-200 мс).
- TLS 1.3 = 1 RTT.
- TLS 1.3 + 0-RTT = 0 RTT (sub-millisecond «handshake» — данные сразу с handshake пакетом).

**Почему есть риск replay-атаки:**
- 0-RTT-данные летят до того, как сервер подтвердил «свежесть» соединения, поэтому **защиты от повтора у них нет** — перехваченный 0-RTT-запрос можно отправить снова.
- Расшифровать его атакующий не может (PSK секретный), но способен вслепую переотправить тот же `POST /api/transfer` несколько раз — и если эндпоинт неидемпотентен, перевод уйдёт дважды.

**Способы снизить риск:**
- 0-RTT использовать **только для идемпотентных GET** (картинки, JS) — не для POST/PUT/DELETE.
- Сервер хранит nonce/кэш защиты от повтора (короткое окно, 5-10 сек).
- Cloudflare: 0-RTT по умолчанию включено только для GET.
- CloudFront: 0-RTT с 2023, по запросу.

**Когда применять:**
- Статический контент, картинки, JS — выигрыш 50-200 мс на первом запросе после повторного коннекта.
- Мобильные приложения — пользователь постоянно переключается между сетями.

**Когда НЕ применять:**
- Любые неидемпотентные эндпоинты (`/api/order/create`, `/api/payment`).
- Финансовые операции.

## Q16. (!) Оптимизация изображений: AVIF/WebP, srcset, ресайз на лету

Оптимизация изображений на CDN сводится к трём независимым рычагам: отдавать картинку нужного **размера**, в современном **формате** и генерировать оба варианта **на лету** на edge с кэшем. Применяют их вместе.

**Проблема, которую решаем:** оригинал — 5-20 MB (4K, RAW), а браузер рисует его в блоке 400×400 px. До 95% переданных байт пользователь физически не видит — это чистый перерасход трафика и времени загрузки.

**Три рычага:**

**1. Адаптивный размер через `<img srcset>`** — пусть браузер сам выбирает разрешение под экран:
```html
<img srcset="cat-400.webp 400w, cat-800.webp 800w, cat-1600.webp 1600w"
     sizes="(max-width: 600px) 400px, (max-width: 1200px) 800px, 1600px"
     src="cat-800.webp" alt="cat" />
```
Браузер выбирает версию по ширине viewport и плотности пикселей (DPR), не качая лишних мегабайт.

**2. Современные форматы** — те же пиксели в разы меньшего веса:

| Формат | Сжатие против JPEG | Поддержка |
|--------|---------------------|------------|
| **WebP** | -25-35% | 96% браузеров (с 2020) |
| **AVIF** | -50% | 90% браузеров (с 2022) |
| **JPEG XL** | -60% | ограниченная (Chrome убрал в 2023) |

**3. Ресайз и конвертация формата «на лету» на CDN** — не плодить N×M версий заранее, а генерировать нужную по первому запросу и кэшировать:
- **Cloudflare Images** — `https://imagedelivery.net/<account>/<image>/w=400,format=auto` — ресайз + определение AVIF/WebP на основе заголовка Accept.
- **Akamai Image Manager** — на основе политик, $$$.
- **CloudFront + Lambda@Edge** — `sharp` на lambda, ресайз по требованию, кэш в CloudFront.
- **imgproxy** (open-source) — self-hosted перед CDN.
- **Vercel Image Optimization** — встроено в Next.js.

**Конвейер обработки:**
```
Origin (master image 4K) → CDN edge (cache resized versions) → Client
                            ↑ генерирует на 1-й запрос
```

**Лучшие практики:**
- Хранить оригинал **без потерь (lossless)** (PNG/TIFF/AVIF).
- Ресайз/сжатие на CDN с кэшем.
- Учитывать `Accept: image/avif,image/webp` → отдавать оптимальный формат через `format=auto`.
- `Cache-Control: public, max-age=31536000, immutable` — версионированный URL.

**Числа Cloudflare Images:**
- $5/мес за хранение 100k изображений.
- Безлимит операций ресайза.
- Замена для self-hosted thumbor/imgix.

## Q17. Видеостриминг: HLS, DASH, ABR, CMAF, LL-HLS

Современный веб-стриминг устроен одинаково у всех: видео заранее режут на короткие сегменты, а плейлист перечисляет их и доступные качества. Это и есть причина, почему стриминг отлично дружит с CDN — каждый сегмент превращается в обычный кэшируемый GET. Ниже — кто как этот общий принцип реализует.

**HLS** (HTTP Live Streaming, RFC 8216) — Apple, 2009. Стандарт на iOS/Safari, де-факто на web.
- Playlist `.m3u8` + сегменты `.ts` (MPEG-TS) или `.m4s` (fMP4).
- Сегменты 2-10 секунд.

**DASH** (Dynamic Adaptive Streaming over HTTP, ISO/IEC 23009-1) — MPEG, 2012.
- Playlist `.mpd` (XML) + сегменты `.m4s` (fragmented MP4).
- Используется YouTube, Netflix.
- Не поддерживается нативно на Safari (нужен MSE).

**CMAF** (Common Media Application Format) — общий контейнер `.cmfv`/`.cmfa`, поддерживается и HLS, и DASH. Снижает storage cost в 2 раза (один файл — два playlist).

**ABR** (Adaptive Bitrate) — механизм, которым плеер на лету подстраивает качество под текущую полосу, чтобы видео не вставало на буферизацию:
- Плейлист содержит несколько дорожек: 360p@500kbps, 720p@2Mbps, 1080p@5Mbps.
- Канал просел или буфер пустеет → плеер берёт следующий сегмент в качестве ниже; полоса вернулась → поднимает обратно.

**LL-HLS** (Low-Latency HLS) — Apple, 2019. Сокращает задержку «от камеры до экрана» (glass-to-glass) с 20-30 сек до 2-5 сек.
- Сегменты 0.2-2 сек + `EXT-X-PART` (части сегментов).
- HTTP/2 push для предзагрузки (устарел в Safari 16).

**Специфика CDN:**
- Каждый сегмент — обычный GET, идеально для кэша CDN.
- `s-maxage` для VOD — максимум (неизменяемые файлы).
- Для live — короткий TTL (5-30 сек) или инвалидация в реальном времени.
- Origin shield обязателен — иначе при вирусной live-трансляции тысячи edge тянут один сегмент.

**Стек:**
```
Encoder (FFmpeg/AWS Elemental) →
    S3/origin (segments) →
        Origin Shield →
            CDN edge (cache) →
                Player (hls.js, dash.js, Shaka Player)
```

**Числа Netflix Open Connect:**
- 100% видео через CDN edge (Open Connect Appliances в дата-центрах ISP).
- Заранее раскладывают популярный контент на OCA (Push CDN — Q6).
- 200 Tbps в пике.

## Q18. Dynamic Site Acceleration (DSA) — как кэшировать «динамику»

**DSA** — это набор приёмов, которыми CDN ускоряет даже тот контент, который кэшировать нельзя: персонализированный HTML, ответы API, real-time данные. Идея в том, что выигрыш даёт не только кэш — близкий TLS, готовый пул к origin и оптимальный маршрут ускоряют запрос, даже когда он каждый раз идёт до бэкенда.

**Что нельзя закэшировать «как есть»:**
- HTML, зависящий от пользователя (профиль, корзина).
- API, возвращающий данные конкретного пользователя.
- Данные реального времени.

**Как ускорять без кэша** (по нарастанию вовлечённости):

**1. TLS termination на edge** (Q13) — экономия 100-300 мс на handshake.

**2. Пул соединений edge→origin**: edge держит постоянный мультиплексированный HTTP/2-пул. Origin не делает TLS handshake на каждый запрос.

**3. Оптимизация маршрута**: CDN использует свой backbone (Cloudflare Argo Smart Routing, AWS Global Accelerator) — пакеты идут через приватную сеть CDN вместо публичного интернета, AS-hop-ов меньше.

**4. Edge compute** (Q19): кэширование персонализированного контента на edge через Cloudflare Workers Cache API или KV.

**5. Микро-кэширование**: `s-maxage=1-5` секунд. Даже секундный кэш на новостном сайте при 1000 RPS снижает QPS на origin в 100-1000 раз. Stale-while-revalidate=60 даёт хороший UX без MISS-задержки.

**6. Edge Side Includes (ESI)**: разбить HTML на фрагменты с разным TTL.
```html
<esi:include src="/shared/header" /> <!-- TTL=24h -->
<esi:include src="/cart" />          <!-- TTL=0, user-specific -->
```
Edge собирает страницу из кэшированных и некэшированных фрагментов.

**7. Сжатие на edge**: edge сжимает ответ в Brotli/gzip перед отдачей клиенту. Origin может отдавать несжатый ответ → edge его сжимает.

**Пример микро-кэша Cloudflare:**
```
Cache Rule: /api/feed/* → Cache Eligibility: Eligible for cache, s-maxage=2, stale-while-revalidate=60
```
- 1000 RPS на ленту → origin видит 0.5 RPS (доля HIT 99.95%).
- Пользователь видит данные с задержкой ≤2 сек.

## Q19. (!) Edge compute — код на CDN: Cloudflare Workers, Lambda@Edge, Compute@Edge

**Edge compute** — запуск твоего кода прямо на edge-узлах CDN, в миллисекундах от пользователя, вместо похода до origin. Платформы различаются по runtime и, главное, по холодному старту: где он нулевой (V8 isolates), туда можно вешать код на каждый запрос; где он сотни миллисекунд (Lambda@Edge) — нет. Подробно — в [edge-computing](edge-computing-interview.md).

**Основные платформы:**

| Платформа | Runtime | Холодный старт | Лимиты CPU/mem | Стоимость |
|-----------|---------|-------------|----------------|-----------|
| **Cloudflare Workers** | V8 isolates | 0 мс | 50 мс CPU, 128 MB | $5/мес + $0.50/M requests |
| **Lambda@Edge** | Node/Python | 100-500 мс | 5 сек, 128 MB | $0.60/M requests + $0.00005/GB-sec |
| **CloudFront Functions** | JS minimal | 0 мс | 1 мс CPU, 2 MB | $0.10/M requests |
| **Fastly Compute@Edge** | WASM (Rust/JS/Go) | 35 мкс | 50 мс CPU | $$$ |
| **Vercel Edge Functions** | V8 (на CF) | 0 мс | 50 мс CPU | по плану |

**Сценарии для edge compute:**
- **Проверка Auth/JWT** — отклонять анонимных до origin.
- **A/B-тестирование** — ответ с учётом варианта (50% пользователей — вариант A).
- **Персонализация** — менять HTML по гео/cookie.
- **Ресайз изображений** — Cloudflare Workers + Cache API (Q16).
- **Детект ботов / rate limiting** — Cloudflare Bot Management.
- **Агрегация API** — собирать данные из нескольких origin в один ответ.
- **Геофенсинг** — блокировать страны без обращения к origin.

**Пример Cloudflare Worker (auth):**
```js
addEventListener('fetch', event => {
  const url = new URL(event.request.url);
  const token = event.request.headers.get('Authorization');
  if (!token && url.pathname.startsWith('/api/')) {
    event.respondWith(new Response('Unauthorized', { status: 401 }));
    return;
  }
  event.respondWith(fetch(event.request));
});
```

**Компромиссы:**
- **Workers** (V8 isolates): нулевой холодный старт, но ограничения: 50 мс CPU, нет полного Node API, только Web API.
- **Lambda@Edge**: полный Node.js, но холодный старт 100-500 мс; не подходит для всех запросов.
- **Compute@Edge**: на базе WASM, поддерживает Rust/Go/JS, средние характеристики.

**Когда edge compute, когда нет:**
- Подходит: лёгкие преобразования (auth, A/B, ресайз изображений).
- Подходит: маршрутизация с учётом кэша.
- Не подходит: тяжёлые вычисления, ML-инференс (хотя Workers AI меняет правила).
- Не подходит: логика с интенсивной работой с БД (нет низколатентной БД на edge, кроме Workers KV).

## Q20. (!) Защита от DDoS: scrubbing, rate limiting, bot management

CDN — естественный щит от DDoS, потому что он и так стоит перед origin и обладает ёмкостью в сотни Tbps. Защита многослойна и должна соответствовать типу атаки: объёмную глушит сама ёмкость anycast, протокольную — фильтры, прикладную (L7) — rate limiting, bot management и WAF. Понимать соответствие «тип атаки → слой защиты» — то, что проверяют на интервью.

**Типы DDoS — по уровню, на котором бьют:**
- **Объёмные (volumetric, L3/L4)**: UDP flood, SYN flood, amplification — просто забивают полосу мусором.
- **Протокольные (L3/L4)**: исчерпание TCP-состояний, фрагментированные пакеты — выматывают ресурсы стека, а не канал.
- **Прикладные (application, L7)**: HTTP flood, slowloris, прицельные удары по «дорогим» эндпоинтам — выглядят как легитимные запросы, поэтому ловятся сложнее всего.

**Защита на стороне CDN (от полосы к приложению):**

**1. Поглощение через anycast**:
- Сеть Cloudflare = 250+ Tbps ёмкости. Атака на 5 Tbps распределяется по 300 PoP — каждый получает 16 Gbps, легко.
- Akamai/AWS Shield Advanced — аналогично.

**2. Очистка трафика (scrubbing)**:
- Подозрительный трафик направляется в центр очистки, где L3/L4-фильтры (Arbor, Radware) отсеивают атаки.
- AWS Shield Advanced, Cloudflare Magic Transit (BGP-redirect).

**3. Rate limiting**:
- Cloudflare Rate Limiting Rules: `IF requests > 100/min FROM same IP THEN block`.
- На edge — не доходит до origin.
- На уровне эндпоинта (`/login` 10 req/min) против глобального.

**4. Управление ботами (bot management)**:
- Cloudflare Bot Management — ML-скоринг каждого запроса (1-99). Боты помечаются даже без явных паттернов.
- Akamai Bot Manager — аналогично.
- Использует TLS-фингерпринтинг (JA3/JA4), поведение браузера, репутацию IP.

**5. CAPTCHA-челлендж**:
- Подозрительные запросы получают `cf-mitigated: challenge` → JS-челлендж или CAPTCHA.
- Cloudflare Turnstile — невидимая CAPTCHA.

**6. Правила WAF** (Q21): блокируют SQLi, XSS, OWASP Top 10 на edge.

**Числа атак:**
- Cloudflare 2024: поглотили атаку 3.8 Tbps (максимум на тот момент).
- AWS Shield 2020: 2.3 Tbps (максимум на тот момент).
- Krebs on Security 2016: 620 Gbps от ботнета Mirai — пришлось мигрировать с Akamai на Project Shield (Google).

**Стоимость:**
- Cloudflare Pro/Business: защита от DDoS включена (без лимита).
- AWS Shield Standard: бесплатно; Advanced — $3000/мес.
- Akamai Prolexic: enterprise-прайсинг.

Как трафик атаки проходит через CDN:

- `DDoS Source` (100k ботов) --attack 5 Tbps--> `CDN` (300 PoP, 250 Tbps ёмкости).
- `CDN` --filtered 99.9%--> `Origin` (до origin доходит порядка 50 Gbps).
- Параллельно `CDN` --scrubbing--> `Scrubbing Center` (подозрительный трафик уходит на очистку).
- `Scrubbing Center` --clean traffic--> `Origin` (очищенный трафик возвращается к origin).

## Q21. WAF на CDN: Cloudflare WAF, AWS WAF, Imperva

**WAF (Web Application Firewall)** — фильтр уровня приложения (L7), который инспектирует каждый HTTP-запрос и блокирует подозрительные по сигнатурам и правилам. На CDN он живёт на edge, поэтому отсекает атаку ещё до origin. В отличие от DDoS-защиты (Q20), которая борется с объёмом, WAF ловит *содержательно вредные* запросы — SQLi, XSS и прочий OWASP Top 10.

**Игроки:**
- **Cloudflare WAF** — Managed Rules (OWASP, Cloudflare proprietary) + custom rules. От $20/мес.
- **AWS WAF** — Managed Rules (AWS, Imperva, Fortinet rule groups), $5/мес + $0.60 per rule + $0.60/M requests.
- **Imperva** — enterprise, $$$.
- **Akamai Kona Site Defender** — bundle с WAF + DDoS.
- **F5 Big-IP** — on-prem/cloud.

**Что защищает:**
- **OWASP Top 10**: SQLi, XSS, CSRF, path traversal, инъекция команд.
- **Боты / скраперы** (см. Q20).
- **Гео-блокировка**: запретить страны.
- **Атаки по частоте**: перебор учётных данных (credential stuffing).

**Правило AWS WAF (JSON):**
```json
{
  "Name": "BlockBadUserAgents",
  "Statement": {
    "ByteMatchStatement": {
      "FieldToMatch": { "SingleHeader": { "Name": "user-agent" } },
      "SearchString": "sqlmap",
      "PositionalConstraint": "CONTAINS"
    }
  },
  "Action": { "Block": {} }
}
```

**Ложные срабатывания:**
- Managed rules часто блокируют легитимные запросы (например, regex-совпадение на `union select` в комментарии).
- **Режим Count** перед `Block` — мониторить 1-2 недели, потом включать.

**WAF + Edge Compute:**
- WAF — декларативные правила.
- Edge compute — программный подход (Cloudflare Workers до/после WAF).

**Режимы Cloudflare WAF:**
- **Off** — нет правил.
- **Essentially Off** — только критичные.
- **Low / Medium / High** — уровни чувствительности.

## Q22. (!) Cache poisoning и Web Cache Deception — как защищаться?

**Cache poisoning** — класс атак, где злоумышленник обманом заставляет CDN закэшировать «отравленный» ответ под легитимным URL, после чего этот ответ раздаётся всем остальным пользователям. Корень всех векторов один: **CDN и origin по-разному понимают, что считать одним и тем же запросом** — расходятся в том, какие заголовки и части URL входят в cache key. Атакующий и эксплуатирует этот зазор.

**Векторы:**

**1. Инъекция неключевого заголовка (unkeyed header injection)**:
- Origin использует `X-Forwarded-Host` в ответе, но CDN не включает его в cache key.
- Атакующий шлёт `X-Forwarded-Host: evil.com`, получает ответ с `<script src="evil.com/x.js">`, CDN кэширует его для всех.
- Защита: cache key должен включать все заголовки, влияющие на ответ. WAF блокирует попытки.

**2. Загрязнение HTTP-параметров (HPP)**:
- `/page?lang=ru&lang=en` — origin берёт первый, CDN берёт второй.

**3. Web Cache Deception** (Omer Gil, 2017, повторно Akamai 2020):
- URL `/account.json/non-existent.css` — origin отдаёт `/account.json` (игнорируя `non-existent.css`), но CDN видит `.css` и кэширует **как публичный статический ресурс**.
- Атакующий стучится в `/account.json/x.css`, кэшируются персональные данные, потом любой может их прочитать.
- **Akamai 2020**: уязвимость на десятках сайтов (PayPal, Trello), позволяла читать данные других пользователей.
- Защита:
  - Origin должен возвращать 404 на `non-existent.css`.
  - CDN не должен кэшировать content-type `application/json` вне зависимости от расширения в URL.
  - Cloudflare добавил защиту в 2021.

**4. Cache key smuggling** (см. Q23).

**Принципы защиты:**
- **Ключевой кэш** — все заголовки и query-параметры, влияющие на ответ, должны входить в cache key.
- **Правильный Vary** — особенно `Vary: Cookie` для персонализированного контента.
- **Кэширование с учётом Content-Type** — не кэшировать JSON/HTML по расширению в URL.
- **Правила WAF** — блокировать подозрительные инъекции заголовков.
- **Аудит** — логи Cloudflare/CloudFront показывают cache HIT-ы; смотри на необычные.

**Реальные инциденты:**
- **Web Cache Deception (2017/2020)** — массовая уязвимость, Akamai/PayPal/Trello.
- **Cloudflare 2017 Cloudbleed** — утечка origin через кэш (не классическое poisoning, но раскрытие памяти).
- **2018 HTTP request smuggling** — рассинхрон парсеров фронта и бэка, позволяет отравить кэш (James Kettle, Black Hat).

## Q23. Cache-key smuggling и нормализация заголовков

**Cache-key smuggling** — подсемейство cache poisoning (Q22), где зазор между CDN и origin проявляется именно в **парсинге URL и заголовков**: один и тот же байтовый запрос они нормализуют по-разному, и атакующий через это подменяет cache key. Суть везде одна — два парсера, две интерпретации, один кэш.

**Векторы:**

**1. Расхождение в нормализации пути**:
- CDN видит `/api/user/123/../admin` и нормализует в `/api/admin`.
- Origin видит путь как есть и обрабатывает другой эндпоинт.
- Cache key — `/api/admin`, но ответ — от admin-эндпоинта.

**2. Порядок query-параметров**:
- `/?a=1&b=2` против `/?b=2&a=1` — для origin одинаково, для CDN разный cache key (если нормализация выключена).

**3. Завершающий слеш**:
- `/page` против `/page/` — origin отдаёт одинаково, CDN кэширует отдельно. Не уязвимость, но фрагментация кэша.

**4. Парсинг значения заголовка**:
- CDN парсит `X-Forwarded-For: 1.1.1.1, 2.2.2.2` и берёт первый.
- Origin берёт последний.
- Если cache key включает X-Forwarded-For — рассинхрон.

**5. HTTP Request Smuggling** (James Kettle, 2019):
- Рассинхрон парсеров фронта и бэка (CL.TE, TE.CL): CDN видит один запрос, origin — другой.
- Можно отравить кэш, обойти WAF, угнать admin-сессии.

**Защита:**
- **Строгая нормализация пути** одинаково на CDN и origin.
- **Отклонять неоднозначные запросы** — Cloudflare/CloudFront отклоняют запросы с конфликтующими Content-Length и Transfer-Encoding.
- **Cache key — явный whitelist** — не «всё, кроме списка», а «только список».
- **Логи аудита** — cache HIT для admin-эндпоинтов — это сигнал.

**Инструменты:**
- Burp Suite + HTTP Request Smuggler (PortSwigger).
- Smuggler (открытый сканер).

## Q24. (!) Cost-модель CDN: egress, requests, edge CPU-ms

Счёт за CDN складывается из нескольких независимых статей, и доминирует обычно одна — **egress** (отданные клиенту байты). Поэтому модель ценообразования сводится к вопросу «как у вендора устроен egress»: у CloudFront он платный за каждый гигабайт, у Cloudflare включён в фикс-план, у Bunny стоит копейки. Остальные статьи (запросы, edge-compute, инвалидация) вторичны для большинства проектов.

**Из чего складывается счёт:**

**1. Egress (отдача байт клиенту) — главная статья:**
- CloudFront: $0.085/GB (US/EU), $0.114 (asia-pacific). С порогами: от 10TB+ цена снижается.
- Cloudflare: **без лимита, включён** в Pro/Business (одна из основных причин выбора).
- Fastly: $0.12/GB (NA/EU), $0.19 Азия.
- Bunny.net: $0.005/GB (Volume tier) — самый дешёвый.

**2. Запросы:**
- CloudFront: $0.0075 / 10k HTTP, $0.01 / 10k HTTPS.
- Cloudflare: включено.
- Fastly: $0.0075 / 10k.

**3. Edge compute (CPU-ms):**
- Cloudflare Workers: $0.50/M requests + $12.50/M CPU-ms (Bundled).
- Lambda@Edge: $0.60/M requests + $0.00005/GB-sec.
- CloudFront Functions: $0.10/M requests (no CPU-ms billing — ограничение 1 мс).

**4. Инвалидация:**
- CloudFront: 1000 путей/мес бесплатно, потом $0.005/путь.
- Cloudflare: без лимита, включено.

**5. SSL-сертификаты:**
- CloudFront ACM: бесплатно.
- Cloudflare Universal SSL: бесплатно.
- Выделенный SSL (CloudFront): $600/мес за сертификат.

**Пример расчёта (e-commerce 100 TB/мес, 5B requests):**

| Метрика | CloudFront | Cloudflare Business | Bunny.net |
|---------|-----------|---------------------|------------|
| Egress | 100 TB × $0.085 = $8500 | $200 (фикс) | $500 |
| Запросы | 5B × $0.0075/10k = $3750 | включено | $1500 |
| Инвалидация | ~$50 | включено | $10 |
| **Итого** | **$12300** | **$200** | **$2010** |

**Cloudflare экономичнее при большом egress** (но enterprise-SLA ограничен), CloudFront — для интеграции с AWS, Bunny — для бюджета.

**Почему CDN дешевле S3 egress:**
- S3 → egress в интернет: $0.09/GB (us-east-1).
- S3 → CloudFront egress: $0 (бесплатно, внутренний).
- CloudFront → интернет: $0.085/GB.
- Экономия: $0.005/GB × 100 TB = $500/мес.
- Cloudflare R2 + Workers: **полностью бесплатный egress** → $0 при любом объёме.

## Q25. Сравнение игроков: Cloudflare/Akamai/CloudFront/Fastly/Bunny

Короткая шпаргалка по выбору: Cloudflare — egress без лимита и безопасность, Akamai — максимум PoP и enterprise/видео, CloudFront — для тех, кто уже в AWS, Fastly — гибкость на VCL и свежие технологии, Bunny — самый дешёвый. Таблица ниже раскрывает эти акценты по строкам.

| Параметр | Cloudflare | Akamai | CloudFront | Fastly | Bunny.net |
|----------|------------|--------|-------------|---------|-----------|
| Число PoP | 310+ | 4100+ (крупнейшая) | 600+ | 84 | 117 |
| Цена egress | без лимита (по плану) | $$$ | $0.085/GB | $0.12/GB | $0.005/GB |
| Edge compute | Workers (V8) | EdgeWorkers (V8) | Lambda@Edge / CFF | Compute@Edge (WASM) | Edge Scripting (Lua) |
| DDoS | без лимита, включён | enterprise | Shield Standard бесплатно | включён | базовый |
| WAF | включён в Pro+ | Kona Site Defender | AWS WAF отдельно | включён | базовый |
| Оптимизация изображений | Cloudflare Images | Image Manager | через Lambda@Edge | через CE Image | Bunny Optimizer |
| Стриминг | Stream | Adaptive Media Delivery | MediaPackage | Live Streaming | Bunny Stream |
| Модель цены | фиксированные планы | enterprise-контракты | по потреблению | по потреблению | по потреблению, дёшево |
| Сильная сторона | egress без лимита, безопасность | больше всего PoP, видео, банкинг | интеграция с AWS | гибкость (VCL), свежие технологии | стоимость |
| Слабая сторона | enterprise-SLA ограничен | дорого, сложно | цена egress, нет фикс-плана | меньше сеть | меньше сеть |

**Когда что:**
- **Cloudflare** — стартапы, медиа, всё с большим egress. Discord, Shopify, GitHub, Replit.
- **Akamai** — enterprise (банки, видео, госсектор). Netflix частично, HSBC, US gov.
- **CloudFront** — AWS-стек, интеграция с S3/Lambda. Amazon.com, Hulu (был), Pinterest.
- **Fastly** — глубокая кастомизация на VCL, новости. NYT, Reddit, Stripe, Spotify, GitHub Pages.
- **Bunny.net** — бюджетные стартапы, раздача игр, видео. Indie-студии.
- **Google Cloud CDN** — только GCP-стек, простые сценарии.

**Multi-CDN** часто используют (Q26): Cloudflare как основной + CloudFront/Fastly как резерв.

## Q26. (!) Multi-CDN — несколько CDN сразу: active-active, traffic steering, NS1/Cedexis

**Multi-CDN** — это когда перед origin стоит не один, а два и более CDN, между которыми распределяется трафик. Главный мотив — убрать единую точку отказа: реальные сбои (Fastly 2021, Akamai 2021 — Q29) показали, что падение одного провайдера кладёт половину интернета. Остальные выгоды идут довеском.

**Зачем платить за два CDN вместо одного:**
- **Доступность**: один CDN лёг → трафик автоматически уходит на второй.
- **Производительность**: один провайдер быстрее в Европе, другой — в Азии; берёшь лучший в каждом регионе.
- **Переговорная позиция**: вендоры знают, что ты можешь переключиться, и сговорчивее по цене.
- **Соответствие требованиям**: суверенитет данных (РФ — локальный CDN, Cloudflare для остального мира).

**Архитектуры:**

**1. Active-passive (DNS-failover)**:
- Основной CDN всегда; при падении — DNS переключает на резервный.
- Переключение: 5-30 секунд (TTL DNS).
- Простота.

**2. Active-active (распределение трафика)**:
- Трафик распределяется между CDN по правилам: по региону, по проценту, с учётом здоровья.
- **NS1 Pulsar / Cedexis (Citrix ITM)** — распределение на уровне DNS на основе RUM (Real User Monitoring).
- **AWS Route53 Latency-Based Routing** — простой вариант.
- **Cloudflare Load Balancer** + внешние origin-ы (можно подключить Akamai/CloudFront как origin).

**3. Гибрид (географическое разделение)**:
- РФ → CDN77 / Ngenix (локальные).
- EU → Cloudflare.
- US → CloudFront.

**Trade-offs:**

| Параметр | Один CDN | Multi-CDN |
|----------|------------|------------|
| Стоимость | $X | $X × 1.5-2 (накладные расходы) |
| Задержка | только лучший CDN | лучший из нескольких в каждом регионе |
| Доступность | один провайдер | переживает сбой одного провайдера |
| Сложность | низкая | высокая (инвалидация кэша на N CDN) |
| Привязка к вендору | высокая | низкая |

**Кто использует multi-CDN:**
- **Netflix** — Akamai + Level 3 + Limelight + Open Connect.
- **Apple** — Akamai + CloudFront + собственный.
- **Hulu** — Akamai + CloudFront + Fastly.
- **Wikipedia** — Cloudflare + Fastly.

**Инвалидация кэша в multi-CDN:**
- **Каждый CDN надо очищать (purge) отдельно** — единого API нет.
- Используют **координатор purge-ов** (свой скрипт, Mux Mile-High или Datacake).
- Cache tags облегчают (Fastly Surrogate-Key, Cloudflare Cache Tags) — один tag → purge на всех CDN сразу.

**Пример распределения (NS1 Pulsar):**
```
RUM data → NS1 → DNS response
EU users → Cloudflare PoP IP (best by RUM RTT)
US users → CloudFront PoP IP
Asia users → CloudFront PoP IP (RUM показал что Cloudflare там медленнее)
```

## Q27. Наблюдаемость CDN: логи, аналитика, RUM

Наблюдаемость CDN строится на четырёх источниках, которые дополняют друг друга: сырые **логи доступа** (что именно произошло, постфактум), **метрики реального времени** (агрегаты прямо сейчас), **RUM** (как быстро реальным пользователям, с их устройств) и **синтетика** (проверки из вне, видят даунтайм даже без трафика). Вместе они отвечают на «что сломалось», «насколько плохо» и «кому именно».

**1. Логи доступа** — детальная запись каждого запроса:
- CloudFront → S3 (сырые) или CloudWatch Logs.
- Cloudflare → Logpush (S3/GCS/Azure/Datadog).
- Fastly → Real-time Streaming (Splunk, Datadog).
- Поля: timestamp, IP клиента, URL, статус, байты, cache HIT/MISS, edge PoP, время ответа.

**2. Метрики в реальном времени**:
- Cloudflare Analytics — запросов/сек, полоса, доля cache HIT, доля ошибок.
- CloudFront → метрики CloudWatch (гранулярность 1 мин).
- Fastly — дашборды в реальном времени.

**3. RUM (Real User Monitoring)**:
- Cloudflare Web Analytics (бесплатно, дружелюбно к приватности).
- CloudFront с CloudWatch RUM.
- RUM SDK от Sentry/New Relic/Datadog встраивается на странице.
- Метрики: TTFB, LCP, FCP, CLS, INP — Web Vitals.

**4. Синтетический мониторинг**:
- Pingdom, Datadog Synthetic, Catchpoint — периодические проверки из глобальных локаций.
- Видит простой даже когда никто не пользуется сервисом.

**Что мониторить:**
- **Доля cache HIT** — целевая 85-95% для статики, 60-80% для динамики с микро-кэшем.
- **Время ответа edge** — p95 < 50 мс.
- **Время ответа origin** — p95 < 200 мс (только при cache MISS).
- **Доля 5xx** — < 0.1%.
- **Попытки DDoS** — Cloudflare Security Events, AWS Shield Insights.
- **Топ URL по полосе** — найти горячие точки для оптимизации.

**Алерты:**
- Доля cache HIT < 70% → разбор инвалидации/конфига.
- Доля 5xx > 1% → потенциальный простой origin.
- Всплеск полосы → DDoS или вирусный контент.

**Управление стоимостью:**
- Семплировать логи, если >100 GB/мес — Cloudflare Logpush sampling, CloudFront sampling 1-100%.

## Q28. Cold start cache MISS и pre-warming

**Холодный старт** — ситуация, когда кэш edge пуст (PoP только что развернули, перезапустили или домасштабировали, либо прошла массовая инвалидация), и первый запрос каждого URL неизбежно промахивается и уходит в origin. Опасность не в одном промахе, а в их **синхронности**: сотни edge холодны одновременно, и origin внезапно получает кратный всплеск нагрузки.

**Симптомы:**
- Всплески p99-задержки после деплоя / автоскейлинга.
- QPS на origin вырастает в 5-20 раз.
- При массовой инвалидации по тегу — глобальный холодный старт.

**Стратегии прогрева:**

**1. Синтетический прогрев**:
- Скрипт делает GET на топ-N URL из 300 PoP (через TCP-к-PoP или через VPN).
- Применяется перед вирусным событием (Black Friday, новый релиз).
- Требует платных API на 300 локаций (но Cloudflare даёт бесплатно через Workers).

**2. Push CDN** (Q6) — заранее раскладывать контент.

**3. Origin shield** (Q2/Q5) — singleton-кэш гарантирует один MISS на весь глобальный кластер, а не N.

**4. stale-while-revalidate** (Q10) — после первого прогрева у клиента не бывает MISS-задержки.

**5. Tiered cache** (Cloudflare) — между edge и origin есть кэш «tier-1» в крупном PoP, который поглощает MISS от мелких.

**Лавина (stampede) при холодном старте:**
- 1000 клиентов одновременно делают MISS → 1000 запросов на origin?
- Origin shield → 1 запрос на origin, 999 ждут.
- Без shield → 1000 одновременных запросов на origin = гибель от thundering herd.

**Объединение запросов (coalescing)**:
- Cloudflare: объединение запросов «в полёте» — несколько одновременных MISS на один URL объединяются.
- CloudFront: `Origin Coalescing` (с 2023).
- Fastly: `vcl_recv → return(pass)` для опционального объединения.

**Метрика:**
- «Время до первого HIT» — через сколько после деплоя появляется первый HIT.
- QPS на origin сразу после деплоя → должен быть как до него.

## Q29. Реальные инциденты: Fastly 2021, Akamai 2021, Cloudflare 2019

Эти инциденты стоит знать для system design интервью: каждый — это конкретный урок про то, чего боится CDN-архитектура (single point of failure, незащищённый BGP, границы доверия CDN↔origin). Ссылайся на них, когда обосновываешь multi-CDN, RPKI или mTLS — это сразу показывает зрелость.

**1. Cloudflare 2019, утечка BGP (Verizon)**:
- 24 июня 2019, ~3 часа.
- Небольшой ISP в Пенсильвании (DQE Communications) с BGP-оптимизатором (Noction) объявил тысячи специфических префиксов Cloudflare в сторону Verizon (AS701).
- Verizon принял их и распространил → трафик многих клиентов Cloudflare уехал через ISP в Пенсильвании, а потом был отброшен.
- Cloudflare, Amazon, Linode частично недоступны.
- Урок: BGP по умолчанию не имеет аутентификации; RPKI помогает (но Verizon не валидировал ROA).

**2. Fastly 2021, глобальный сбой**:
- 8 июня 2021, **49 минут** полного простоя.
- Баг в ПО Fastly, спровоцированный валидным изменением конфига клиента.
- Затронуты: Amazon.com, Reddit, NYT, Stack Overflow, Twitch, правительство UK (gov.uk), eBay, Shopify, частично PayPal.
- Зависимость от одного CDN ударила больно.
- Урок: стратегия multi-CDN для критичных сервисов.

**3. Akamai 2021, сбой DNS**:
- 22 июля 2021, **1 час**.
- Баг в новом обновлении конфигурации DNS.
- Затронуты: Steam, PSN, банки (Lloyds, Capital One UK), DraftKings, FedEx, UPS.
- Критичная инфраструктура (банки) лежала час.

**4. Cloudflare 2022, заголовок cf-connecting-ip**:
- 25 июля 2022.
- Баг в Cloudflare передавал заголовок `Cf-Connecting-Ip` от недоверенного CF-трафика к origin, позволяя подделку IP.
- Затронуты все клиенты, использующие этот заголовок для rate limiting/auth.
- Урок: границы доверия между CDN и origin, mTLS / Authenticated Origin Pulls.

**5. Web Cache Deception (Akamai 2020)**:
- Omer Gil обновил исследование 2017 года.
- PayPal, Trello и многие другие — `/account.json/x.css` кэшировался публично.
- Утечка PII на десятках сайтов до фиксов.

**6. Slack 2021 (январь)**:
- Сбой инвалидации кэша + автоскейлинга → thundering herd на AWS.
- 5-часовой простой.
- Урок: фиксировать инвалидацию кэша как часть процедуры деплоя.

**7. KrebsOnSecurity 2016 (Mirai)**:
- 620 Gbps DDoS от IoT-ботнета Mirai (камеры/DVR).
- Akamai сбросил pro-bono клиента после потенциальных расходов на защиту $1M+.
- Приютил Project Shield (Google).
- Урок: pro-bono ≠ навсегда; для критичной защиты — платный тариф.

**Что отвечать на интервью:**
- «Знаю про Fastly 2021 — пример SPOF без multi-CDN».
- «Cloudflare 2019 BGP — повод изучить RPKI».
- «Akamai 2021 — банки оказались зависимы от одного CDN; multi-CDN критичен для финансов».

## Q30. Конкретные конфиги: CloudFront Behavior и Cloudflare Page Rules

Конфиг любого CDN строится вокруг одной мысли: **разные пути — разные политики кэша**. Статику кэшируем агрессивно и надолго, API не кэшируем (или микро-кэш), персональные/admin-пути держим private. Ниже — как одна и та же идея записывается в CloudFront, Cloudflare и Fastly.

**CloudFront Behavior** (правило на путь внутри distribution):
```json
{
  "PathPattern": "/api/*",
  "TargetOriginId": "api-origin",
  "ViewerProtocolPolicy": "redirect-to-https",
  "AllowedMethods": ["GET", "HEAD", "OPTIONS", "POST", "PUT", "DELETE"],
  "CachePolicyId": "658327ea-f89d-4fab-a63d-7e88639e58f6",  // Managed-CachingDisabled для API
  "OriginRequestPolicyId": "216adef6-5c7f-47e4-b989-5492eafa07d3",  // Managed-AllViewer
  "Compress": true
}
```

**CloudFront Cache Policy** (custom):
```json
{
  "Name": "ProductImagesCachePolicy",
  "MinTTL": 86400,
  "DefaultTTL": 86400,
  "MaxTTL": 31536000,
  "ParametersInCacheKeyAndForwardedToOrigin": {
    "EnableAcceptEncodingGzip": true,
    "EnableAcceptEncodingBrotli": true,
    "HeadersConfig": { "HeaderBehavior": "whitelist", "Headers": { "Items": ["Accept"] } },
    "QueryStringsConfig": { "QueryStringBehavior": "whitelist", "QueryStrings": { "Items": ["w", "h", "format"] } },
    "CookiesConfig": { "CookieBehavior": "none" }
  }
}
```

**Cloudflare Page Rules / Cache Rules** (per-domain):
```
URL: example.com/static/*
Settings:
  - Cache Level: Cache Everything
  - Edge Cache TTL: 1 month
  - Browser Cache TTL: 1 hour

URL: example.com/api/*
Settings:
  - Cache Level: Bypass

URL: example.com/*.json/*.css  # WCD protection
Settings:
  - Cache Level: Bypass
```

**Cloudflare Cache Rules (новый формат, 2023+)**:
```
When: URI Path matches "/static/*"
Then:
  - Cache Eligibility: Eligible for cache
  - Edge TTL: 1 month
  - Cache Key:
    - Query String: include only ["v"]
    - Cookie: ignore
  - Cache by status code: 200, 301, 404 (for 1 min)
```

**Fastly VCL example**:
```vcl
sub vcl_recv {
  # WCD protection
  if (req.url ~ "\.json/.*\.css$") {
    return(pass);
  }
  # Ignore tracking params
  set req.url = querystring.filter(req.url, "utm_source");
  set req.url = querystring.filter(req.url, "fbclid");
}

sub vcl_fetch {
  if (beresp.http.Content-Type ~ "image/") {
    set beresp.http.Cache-Control = "public, max-age=31536000, immutable";
  }
}
```

**Лучшие практики для конфигов:**
- **Whitelist** заголовков/query/cookies в cache key, никогда blacklist.
- **Сжатие на edge** (gzip/brotli) — `Compress: true` всегда.
- **Отдельные behaviors** для `/static/*` (большой TTL), `/api/*` (без кэша или микро-кэш), `/admin/*` (private, без кэша).
- **Тестировать в режиме Count/Monitor** перед переключением на Block для правил безопасности.

---

## See also

- [Стратегии кэширования](caching-strategies-interview.md) — общие паттерны кэширования: Cache-Aside, Write-Through, TTL, инвалидация, многоуровневый кэш
- [Edge Computing](edge-computing-interview.md) — детально про Cloudflare Workers, Lambda@Edge, Compute@Edge и V8 isolates
- [Load Balancing](load-balancing-interview.md) — балансировка трафика, anycast, GeoDNS, L4/L7
- [Distributed Systems](distributed-systems-interview.md) — общие принципы распределённых систем, eventual consistency
- [Scalability Patterns](scalability-patterns-interview.md) — горизонтальное масштабирование, partitioning, replication
- [DNS](dns-interview.md) — авторитативный DNS, GeoDNS, anycast — фундамент для CDN routing
- [Networking](networking-interview.md) — TCP/UDP, TLS, HTTP/2/3, BGP
- [Network Performance](../performance/network-performance-interview.md) — latency, throughput, TCP tuning, congestion control
- [HTTP / REST](../api/http-rest-interview.md) — заголовки, методы, статус-коды, semantics caching
- [System Design Interview](../system-design/system-design-interview.md) — общий процесс system design, capacity estimation
- [Resilience Patterns](resilience-patterns-interview.md) — circuit breaker, retry, timeout — что нужно вокруг CDN
- [Microservices](microservices-interview.md) — API gateway vs CDN, service mesh
