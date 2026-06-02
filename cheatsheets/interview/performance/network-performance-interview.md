---
title: "Вопросы на собеседовании: Network Performance"
description: "Network performance: latency, bandwidth, TCP tuning, HTTP/2, HTTP/3 QUIC, CDN, compression, connection reuse, keepalive, gRPC, WebSockets, tail latency"
tags:
  - interview
  - performance
  - network-performance-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Network Performance"
  - "Network Performance interview"
  - "Network tuning"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Network Performance`

`Network Performance` — сетевой слой часто скрытый bottleneck: **RTT**, TCP slow start, TLS handshake, DNS lookup, HTTP версия (1.1 vs 2 vs 3), compression, keep-alive. Особенно на distributed/cross-region/mobile.

## Полезные ссылки

### Официальная документация

- [High Performance Browser Networking](https://hpbn.co/) — Ilya Grigorik (обязательно!)
- [HTTP/2 spec](https://httpwg.org/specs/rfc7540.html)
- [HTTP/3 (QUIC) spec](https://datatracker.ietf.org/doc/html/rfc9114)
- [Brendan Gregg — Network performance](https://www.brendangregg.com/linuxperf.html)
- [Cloudflare blog](https://blog.cloudflare.com/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Latency vs Bandwidth — разница?](#q1--latency-vs-bandwidth--разница)
- [Q2. (!) Типичные RTT-значения?](#q2--типичные-rtt-значения)
- [Q3. (!) Что такое bandwidth-delay product?](#q3--что-такое-bandwidth-delay-product)

**TCP**
- [Q4. (!) TCP slow start и congestion control?](#q4--tcp-slow-start-и-congestion-control)
- [Q5. (!) TCP handshake overhead?](#q5--tcp-handshake-overhead)
- [Q6. Nagle algorithm и delayed ACK?](#q6-nagle-algorithm-и-delayed-ack)
- [Q7. BBR vs CUBIC congestion control?](#q7-bbr-vs-cubic-congestion-control)

**TLS**
- [Q8. (!) TLS handshake overhead?](#q8--tls-handshake-overhead)
- [Q9. TLS session resumption, 0-RTT?](#q9-tls-session-resumption-0-rtt)

**HTTP**
- [Q10. (!) HTTP/1.1 vs HTTP/2 vs HTTP/3?](#q10--http11-vs-http2-vs-http3)
- [Q11. (!) Head-of-line blocking в HTTP/1.1 и HTTP/2?](#q11--head-of-line-blocking-в-http11-и-http2)
- [Q12. (!) Keep-alive и connection reuse?](#q12--keep-alive-и-connection-reuse)
- [Q13. HTTP/2 server push (и почему deprecated)?](#q13-http2-server-push-и-почему-deprecated)
- [Q14. QUIC — почему быстрее TCP?](#q14-quic--почему-быстрее-tcp)

**DNS**
- [Q15. (!) DNS lookup как latency source?](#q15--dns-lookup-как-latency-source)

**Compression**
- [Q16. (!) gzip vs brotli vs zstd?](#q16--gzip-vs-brotli-vs-zstd)
- [Q17. Content-Encoding negotiation?](#q17-content-encoding-negotiation)

**CDN и edge**
- [Q18. (!) Зачем CDN — latency math?](#q18--зачем-cdn--latency-math)

**Application protocols**
- [Q19. (!) gRPC vs REST performance?](#q19--grpc-vs-rest-performance)
- [Q20. WebSockets — overhead и use cases?](#q20-websockets--overhead-и-use-cases)
- [Q21. Server-Sent Events (SSE)?](#q21-server-sent-events-sse)

**Tuning и troubleshooting**
- [Q22. (!) Tail latency — причины и борьба?](#q22--tail-latency--причины-и-борьба)
- [Q23. (!) Linux sysctl tuning для high-throughput?](#q23--linux-sysctl-tuning-для-high-throughput)
- [Q24. Debugging slow networks (tools)?](#q24-debugging-slow-networks-tools)

## Q1. (!) Latency vs Bandwidth — разница?

(!) Latency vs Bandwidth — разница?

**Latency (задержка)** — время прохождения пакета из точки A в точку B.
- Измеряется в ms
- RTT (round-trip time) = 2× в одну сторону

**Bandwidth (пропускная способность)** — объём данных в единицу времени.
- Измеряется в bps / Gbps

**Аналогия:** грузовик из Лос-Анджелеса в Нью-Йорк
- Bandwidth: вместимость грузовика (сколько груза увезёт)
- Latency: время доехать через всю страну

**Ключевая мысль:** latency и bandwidth **независимы**.

**Как улучшить bandwidth:**
- Апгрейд оптики / NIC
- Больше параллельных линков

**Как улучшить latency:**
- **Скорость света не обойти** (~20 км/мс в оптике)
- Географическая близость — единственное реальное лекарство
- CDN — размещаем edge ближе к пользователю

**Современные сети:** bandwidth продолжает расти (10 Gbps → 100 Gbps); latency упирается в **физический предел**.

**Влияние на приложения:**
- Болтливые протоколы (много мелких запросов) → упор в latency
- Массовая передача данных → упор в bandwidth
- Загрузка веб-страницы: куча мелких запросов → обычно доминирует latency

**Пример LA ↔ NY:**
- Скорость света: ~20 ms в каждую сторону; RTT ≥ 40 ms
- На практике: ~70 ms (маршрутизация, очереди)
- Для 100 запросов последовательно: 7 секунд только на сеть
- Распараллелить (мультиплексирование HTTP/2) → всё укладывается в 1 RTT + время передачи

## Q2. (!) Типичные RTT-значения?

**Localhost:** 0.05 - 0.2 ms

**Внутри дата-центра (intra-DC):** 0.5 - 2 ms

**Внутри региона (intra-region):** 2 - 10 ms

**Между регионами (один континент):**
- US east ↔ west: ~70 ms
- Europe west ↔ east: ~30 ms

**Межконтинентально:**
- US ↔ Europe: ~80-100 ms
- US ↔ Asia: ~120-180 ms

**Подводный кабель ↔ peer:** ~150-250 ms

**Спутник:** 500+ ms (геостационарный); Starlink ~50 ms (LEO)

**Mobile 4G:** 20-50 ms на первом хопе
**5G:** 5-15 ms на первом хопе
**Wi-Fi:** 2-10 ms до роутера

**Влияние на приложения:**

**5 последовательных запросов (5× RTT):**
- Локально: 0.5 ms → 2.5 ms (быстро)
- Между регионами: 70 ms × 5 = 350 ms (медленно)
- Межконтинентально: 180 ms × 5 = 900 ms (больно)

**Следствие для дизайна:**
- Батчить запросы (меньше round-trip-ов)
- Распараллеливать независимые вызовы
- Деплоить multi-region, когда сервис user-facing

**Инструменты измерения:**
- `ping <host>` — ICMP RTT
- `traceroute <host>` — latency по хопам
- `mtr <host>` — непрерывный замер

## Q3. (!) Что такое bandwidth-delay product?

**BDP = bandwidth × round-trip time**

**Интерпретация:** сколько данных «in flight» помещается в pipe (канал).

**Пример:**
- Линк 1 Gbps, RTT 50 ms
- BDP = 1e9 bit/s × 0.05 s = 5e7 бит = 6.25 MB

**Влияние:** TCP нужно **достаточное receive window**, чтобы держать канал заполненным.

Если receive window (rwnd) < BDP:
- Отправитель блокируется в ожидании ACK
- Реальный throughput < доступной пропускной способности

**TCP window scaling:** 16-битное окно → максимум 64KB; расширение RFC 1323 позволяет больше (вплоть до GB).

**TCP-буферы по умолчанию:** в Linux обычно 4MB с авто-масштабированием. Для long fat networks (большой BDP) — тюнить:
```
net.ipv4.tcp_rmem = 4096 87380 16777216
net.ipv4.tcp_wmem = 4096 65536 16777216
```

**Long Fat Networks (LFN):**
- Высокий bandwidth + высокая latency (спутник, скоростные межконтинентальные линки)
- BDP большой → нужны крупные буферы
- Без тюнинга throughput ограничен буфером, а не bandwidth

**Проверить:**
- `ss -ti` — показывает текущее congestion window (cwnd), send/recv-буферы
- `iperf3 -c server -w <window>` — тест с конкретным окном

**Правило большого пальца:** для 10 Gbps межконтинентально → receive buffer ≥ 10MB.

## Q4. (!) TCP slow start и congestion control?

**TCP не шлёт сразу на полной пропускной способности** — это позволяет избежать перегрузки сети.

**Slow start:**
- Начальный cwnd = 10 MSS (~14KB) по умолчанию в Linux
- Каждый ACK → cwnd += 1 MSS → экспоненциальный рост
- Продолжается до потери пакета или достижения ssthresh

**Congestion avoidance:**
- После ssthresh: линейный рост (cwnd += 1 MSS за RTT)
- Потеря пакета → cwnd делится пополам (multiplicative decrease)

**Влияние:**
- Короткоживущее соединение никогда не доходит до полной пропускной способности
- Пример: линк 1 Gbps, RTT 50 ms
  - После 1 RTT: cwnd = 20 MSS = 28KB → реально 4.5 Mbps
  - После 5 RTT: cwnd = 320 MSS = 450KB → всё ещё 70 Mbps
  - Чтобы насытить гигабит, нужны секунды

**Алгоритмы:**
- **Reno / CUBIC (дефолт Linux с 2.6+):** loss-based; cwnd растёт по кубической функции
- **BBR (Google 2016):** model-based (bandwidth × RTT); не опирается на потери
- **Westwood, Veno, HSTCP:** альтернативы

**Следствие для приложений:**
- **Переиспользуйте соединения** (HTTP keep-alive) — cwnd остаётся высоким между запросами
- Новое соединение = slow start с нуля
- Cross-region + короткий запрос = большой «налог» на slow start

**Увеличить начальный cwnd:**
```
ip route change default via <gw> initcwnd 30
```
В Linux 3.10+ дефолт 10; иногда поднимают выше (20-40) ради ускорения загрузки страницы.

## Q5. (!) TCP handshake overhead?

**3-way handshake:**
```
Client → SYN → Server     (0.5 RTT)
Client ← SYN-ACK ← Server (1 RTT)
Client → ACK → Server     (1.5 RTT; но data piggyback возможно)
```

**Overhead:** **1 RTT** до того, как можно отправить данные.

**Цена:**
- Локально: 1-2 ms (пренебрежимо)
- Cross-region: 70 ms мёртвого времени
- Mobile: плюс латентность сотовой сети

**Много запросов без keep-alive:**
- Каждый запрос — новый handshake
- Загрузка 50 картинок на странице = 50 handshake-ов

**Решения:**

**1. Keep-alive (Q12):**
- Переиспользуем соединение — handshake один раз

**2. TCP Fast Open (TFO):**
- Второе и последующие соединения экономят 1 RTT (данные в SYN)
- RFC 7413; нужна поддержка клиента и сервера + cookie
- Слабо распространён (middleboxes ломают)

**3. QUIC (HTTP/3):**
- 0-RTT при переподключении (см. Q14)

**4. Connection pooling:**
- На стороне сервера: переиспользуем исходящие соединения

**TLS добавляет сверху:**
- TCP handshake (1 RTT) + TLS handshake (1-2 RTT) = 2-3 RTT до данных
- Cross-region: 200-300 ms пустого времени — заметно для мелких запросов

**SO_REUSEPORT:** балансирует входящие соединения между несколькими процессами; повышает throughput на accept.

## Q6. Nagle algorithm и delayed ACK?

**Алгоритм Нейгла (Nagle, 1984):**
- Буферизует мелкие записи до прихода ACK или набора полного сегмента
- Снижает накладные расходы на пакеты у болтливых приложений
- Включён по умолчанию (TCP_NODELAY=0)

**Delayed ACK (дефолт в Linux):**
- Не подтверждает сразу; ждёт ~200 ms на случай, если придут ещё данные и можно подтвердить вместе
- Снижает «спам» из ACK

**Nagle + delayed ACK = почти deadlock:**
- Мелкая запись → Nagle держит (ждёт ACK)
- Получатель → delayed ACK (ждёт ещё данных)
- Пауза в 200 ms на ровном месте

**Фикс:** отключить Nagle для latency-чувствительных протоколов.

```c
int flag = 1;
setsockopt(fd, IPPROTO_TCP, TCP_NODELAY, &flag, sizeof(int));
```

**In Java:**
```java
socket.setTcpNoDelay(true);
```

**Когда включать / отключать:**
- Отключить Nagle (TCP_NODELAY=true): интерактивные нагрузки (SSH, игры, RPC); мелкие latency-чувствительные записи
- Оставить Nagle (дефолт): массовая передача (загрузка файла) — экономит bandwidth

**Большинство RPC-фреймворков (gRPC, Netty по умолчанию) отключают Nagle.**

**HTTP-клиенты:** обычно отключают Nagle ради низкой латентности; для bulk download разница неважна.

## Q7. BBR vs CUBIC congestion control?

**CUBIC (дефолт Linux с 2.6.19):**
- Loss-based: замедляется при потере пакета
- Рост по кубической функции → агрессивно, затем плато
- Честный, хорошо обкатанный
- Проблемы на bufferbloated-линках (заполняет буферы до потери → высокая latency)

**BBR (Bottleneck Bandwidth and RTT — Google 2016):**
- Model-based: оценивает bandwidth + минимальный RTT
- Шлёт со скоростью bandwidth × (1 − buffer_fill)
- **Не заполняет буферы** → избегает bufferbloat
- **Лучший throughput на lossy-линках** (сотовая связь, WiFi)

**Включить BBR:**
```bash
sudo sysctl net.core.default_qdisc=fq
sudo sysctl net.ipv4.tcp_congestion_control=bbr
```

**Плюсы BBR:**
- YouTube, сервисы Google — внедрён в масштабе
- **Google сообщал о росте throughput до 2700%** на некоторых путях (сотовая связь)
- Ниже queueing latency (лучше для видео, игр)

**Минусы:**
- BBRv1 — агрессивен; может «зажимать» CUBIC (нечестен в миксе)
- BBRv2 (2019+) — улучшенная честность
- Требует аккуратного внедрения

**Когда применять:**
- Высокий bandwidth, lossy или bufferbloated сети
- Сотовая связь, спутник
- Cross-region с джиттером

**Когда нет:**
- Low-bandwidth, low-loss локалка (CUBIC и так норм)
- Где критична честность (микс с CUBIC-отправителями — планировать заранее)

**Проверить:**
```bash
sysctl net.ipv4.tcp_congestion_control
ss -ti  # shows per-connection congestion info
```

## Q8. (!) TLS handshake overhead?

**TLS 1.2 handshake:**
- 2 RTT (после TCP handshake)
- Итого: 3 RTT до данных

**TLS 1.3 (2018):**
- handshake за 1 RTT
- Обязательная forward secrecy
- Лучшие cipher suites по умолчанию

**Разбивка по стоимости:**
- TCP: 1 RTT
- TLS 1.2: 2 RTT → 3 RTT суммарно
- TLS 1.3: 1 RTT → 2 RTT суммарно

**Влияние на cross-region:**
- RTT 100 ms
- TLS 1.2: 300 ms мёртвого времени
- TLS 1.3: 200 ms мёртвого времени

**Нагрузка на CPU:**
- Асимметричная криптография (RSA/ECDSA) дорога, но раз на handshake
- Симметричное шифрование после: на современных CPU с AES-NI — пренебрежимо (несколько % оверхеда)
- ECDSA дешевле RSA по CPU
- Session tickets / IDs — в следующий раз пропускаем асимметрику

**0-RTT (TLS 1.3):**
- Возобновлённое соединение → данные в первом пакете
- 0 RTT до данных (!)
- **Риск replay-атаки** — данные могут быть переотправлены; использовать только для идемпотентных GET-ов

**Тюнинг:**
- **Session resumption** (tickets/IDs) — переиспользуем предыдущую сессию (без асимметрики)
- **OCSP stapling** — сервер отдаёт OCSP-ответ inline; клиенту не надо самому ходить за OCSP
- **Короткая цепочка сертификатов** — меньше байт = меньше сегментов = быстрее

**Измерить:**
```bash
curl -o /dev/null -s -w "%{time_connect} %{time_appconnect} %{time_starttransfer}\n" https://site.com
# time_connect — TCP handshake; time_appconnect — TLS handshake; time_starttransfer — first byte
```

## Q9. TLS session resumption, 0-RTT?

**Методы TLS 1.2:**

**Session ID:**
- Сервер хранит состояние; клиент при переподключении присылает ID
- Сервер находит состояние → resume
- **Проблема масштабирования:** нужно состояние на сервере (sticky LB)

**Session Tickets (RFC 5077):**
- Сервер шифрует состояние сессии → отдаёт клиенту «тикет»
- Клиент при переподключении присылает тикет
- Сервер расшифровывает → возобновляет
- **Stateless** — работает за любым LB

**TLS 1.3 PSK (Pre-Shared Key):**
- Заменяет оба; использует «resumption key» из прошлой сессии

**0-RTT:**
- Клиент шлёт данные в первом пакете (с тикетом)
- Сервер принимает, если тикет валиден
- **Экономит целый RTT**

**Риски 0-RTT:**
- **Replay-атака:** атакующий записал и переотправил → действие выполнится повторно
- Митигация: только идемпотентные запросы (GET); никогда 0-RTT для POST /payment
- На стороне клиента: большинство браузеров ограничивают 0-RTT только GET-ами

**Поведение браузеров:**
- Chrome, Safari поддерживают 0-RTT
- Nginx/Envoy/Apache — `ssl_early_data on`

**Измерение:**
- DevTools Network: «Resumed TLS connection: yes»
- Лог Nginx: `$ssl_session_reused`

**Практический эффект:**
- Cross-region API-вызовы: экономия 50-100 ms на запрос
- Можно держать latency под 100 ms для статики по всему миру

## Q10. (!) HTTP/1.1 vs HTTP/2 vs HTTP/3?

**HTTP/1.1 (1997):**
- Текстовый протокол
- **1 запрос за раз на соединение** (pipelining почти не поддержан)
- Keep-alive по умолчанию
- Браузеры открывают **6 параллельных соединений** на origin

**HTTP/2 (2015):**
- **Бинарный** фрейминг
- **Мультиплексирование:** много запросов в одном соединении
- **Сжатие заголовков** (HPACK)
- Server Push (по большей части deprecated)
- Тот же стек TCP + TLS

**HTTP/3 (2022):**
- Транспорт **QUIC** (поверх UDP, не TCP)
- Встроенный TLS 1.3
- **Нет head-of-line blocking** на транспорте (стримы независимы)
- **Возобновление соединения за 0-RTT** (нативно в QUIC)
- Миграция между сетями (ноутбук WiFi → mobile)

**Сравнение:**

| Аспект | 1.1 | 2 | 3 |
|--------|-----|---|---|
| Протокол | TCP | TCP | UDP (QUIC) |
| Формат | Текст | Бинарный | Бинарный |
| Мультиплексирование | Нет | Да | Да |
| HoL blocking | Да | Да (на уровне TCP) | Нет |
| Handshake | TCP+TLS (3 RTT) | TCP+TLS (3 RTT) | 1 RTT (или 0-RTT) |
| Сжатие заголовков | Нет | HPACK | QPACK |
| Server Push | Нет | Да (deprecated) | Да |
| Мобильность | Нет | Нет | Да |

**Когда применять:**
- HTTP/1.1: legacy; фоллбэк
- HTTP/2: мейнстрим; поддержан большинством бэкендов/клиентов
- HTTP/3: мобильные клиенты, latency-чувствительные сценарии; Cloudflare, Google, Meta внедряют широко

**Подводные камни:**
- HTTP/2 — одно соединение → если оно падает, падает всё (против 6 соединений у 1.1)
- UDP заблокирован в некоторых сетях → HTTP/3 откатывается на HTTP/2
- Server Push — браузеры отключили к 2022 (сложность, незначительный выигрыш)

## Q11. (!) Head-of-line blocking в HTTP/1.1 и HTTP/2?

**HTTP/1.1:**
- Запросы сериализованы: нужно завершить ответ до следующего запроса в этом соединении
- Pipelining (в теории — очередь из нескольких запросов) — на практике сломан
- **Обходной путь:** 6 параллельных соединений на origin

**HTTP/2:**
- Мультиплексирование: много стримов в одном соединении
- **HoL на уровне приложения решён**
- НО: **HoL на уровне TCP** — потеря пакета в стриме A блокирует стрим B (оба в одном TCP-соединении)
- При 5% потерь пакетов HTTP/2 **медленнее** 1.1 (у которой было 6 независимых TCP)

**HTTP/3 (QUIC):**
- Стримы независимы на транспортном уровне
- Потерянный пакет в стриме A НЕ блокирует стрим B
- **По-настоящему без HoL**

**Когда HoL важен:**
- Lossy-сети (mobile, перегруженный WiFi)
- Много мелких ресурсов (страница со 100 картинками)

**Когда нет:**
- Сеть с малыми потерями (DC, хорошая оптика)
- Один большой запрос (стриминг видео)

**Решения:**
- HTTP/2 работает нормально, если потери пакетов < 2%
- HTTP/3 выгоден там, где потери > 2%
- Фоллбэк на HTTP/2, если UDP заблокирован

**На практике:**
- Google: HTTP/3 измеримо улучшает мобильный опыт search/YouTube
- Cloudflare: HTTP/3 по умолчанию там, где поддерживается

## Q12. (!) Keep-alive и connection reuse?

**Keep-alive:** после ответа соединение остаётся открытым для следующего запроса.

**Поведение по умолчанию:**
- HTTP/1.1: keep-alive по умолчанию (`Connection: keep-alive`)
- HTTP/2: одно постоянное соединение (мультиплексированное)
- HTTP/3: одно постоянное QUIC-соединение

**Почему важно:**
- Избегаем TCP + TLS handshake-ов (2-3 RTT на запрос)
- TCP cwnd уже раскачан (нет slow start)
- Быстрее последующие запросы

**Пример выигрыша:**
- RTT 100 ms, 10 запросов
- Новое соединение каждый раз: 10 × (TCP + TLS + req) = 10 × 200 ms+ = 2000 ms
- С переиспользованием: 10 × 100 ms = 1000 ms → **в 2 раза быстрее**

**Заголовки:**
- `Connection: keep-alive` — дефолт HTTP/1.1
- `Keep-Alive: timeout=60, max=100` — таймаут + максимум запросов
- `Connection: close` — отказ

**Настройки клиента (HTTP-клиентской библиотеки):**
```java
// Apache HttpClient
HttpClientBuilder.create()
    .setConnectionManagerShared(true)
    .setMaxConnTotal(200)
    .setMaxConnPerRoute(50)
    .setConnectionTimeToLive(60, TimeUnit.SECONDS)
    .build();
```

**На стороне сервера:**
- Nginx `keepalive_timeout 65s;`
- Тюнить под профиль трафика
- Больше = эффективнее; но расходует файловые дескрипторы / сокеты

**Мониторинг:**
- Пул соединений исчерпан → рост latency
- Следить за: `connections_active`, `connections_reused`

**Антипаттерн:** создавать новый `HttpClient` на каждый запрос (забыли про пулинг) → каждый = новые TCP + TLS. Классическая ловушка для синьора в Java.

## Q13. HTTP/2 server push (и почему deprecated)?

**Server Push (HTTP/2):**
- Сервер проактивно шлёт ресурсы без запроса клиента
- Идея: например, странице нужен `style.css` → пушим его вместе с HTML

**Теория:** экономит RTT (клиент не запрашивает явно).

**Проблемы на практике:**
- **Over-pushing:** ресурс уже был в кэше браузера → потраченный впустую bandwidth
- **Незнание кэша:** сервер не знает состояние кэша клиента
- **Сложность:** нужна аккуратная парная настройка (что пушить и когда)
- **Проблемы с приоритетами:** запушенный стрим может конкурировать с нужным
- **По факту:** на реальных сайтах эффект незначительный или отрицательный

**Chrome убрал server push** (2022).

**Замены:**
- **Early Hints (HTTP 103):** отправить preload-подсказки до ответа 200
  ```
  103 Early Hints
  Link: </style.css>; rel=preload
  ```
- **HTML `<link rel=preload>`:** клиентская preload-директива (про кэш решает клиент)

**103 Early Hints:**
- Сейчас широко поддержан (Chrome, Fastly, Cloudflare, Vercel)
- Проще, чем HTTP/2 push; работает с HTTP/2 и HTTP/3
- Cloudflare сообщает об ускорении LCP на 20-30%

**Вердикт:**
- Не полагайтесь на HTTP/2 Push (deprecated)
- Используйте Early Hints или link preload

## Q14. QUIC — почему быстрее TCP?

**QUIC (Quick UDP Internet Connections) — транспорт HTTP/3.**

**Преимущества:**

**1. handshake за 1 RTT (против 3-RTT TCP+TLS):**
- Объединяет транспорт + TLS в одно
- 0-RTT при возобновлении (данные в первом пакете)

**2. Нет TCP HoL blocking:**
- Стримы независимы на транспорте
- Потерянный пакет в стриме A не блокирует стрим B
- Огромный выигрыш на lossy-сетях (mobile)

**3. Миграция соединения:**
- Идентификатор по Connection ID, а не по IP/port
- Ноутбук роумится WiFi → mobile — то же QUIC-соединение
- TCP бы порвалось (новый IP = новое соединение)

**4. Лучший congestion control:**
- BBR, CUBIC — реализации в userland (легко итерировать)
- Новые алгоритмы быстрее выкатываются

**5. Обязательное шифрование:**
- Каждый пакет зашифрован (payload + частично заголовки)
- Middleboxes не могут вмешиваться (избегаем оссификации)

**6. Быстрее восстановление после потерь:**
- Номера пакетов монотонны (sequence numbers в TCP переиспользуются при ретрансмитах → неоднозначность)
- Точнее оценка RTT

**Сложности:**
- UDP заблокирован в части сетей (обычно 0.5-2%) → фоллбэк на HTTP/2
- Выше нагрузка на CPU (крипто в userland, обработка по пакетам) — улучшается с hardware offload
- Менее зрелый тулинг (против TCP)

**Распространение (2024):**
- 28% веб-трафика (статистика Cloudflare)
- Gmail, YouTube, Meta, Instagram, Spotify
- Включён по умолчанию у большинства крупных CDN

**Поддержка клиентами:**
- Chrome, Firefox, Safari — включён
- curl, браузеры — стандарт
- Мобильные приложения — зависит от библиотеки

## Q15. (!) DNS lookup как latency source?

**DNS lookup:** резолв `api.example.com` → IP до подключения.

**Overhead:**
- Попадание в кэш: 0 ms (локальный / кэш ОС)
- Промах кэша: 20-200 ms (запрос к рекурсивному резолверу)

**Путь холодного lookup-а:**
- Браузер → DNS-клиент ОС
- ОС → настроенный резолвер (1.1.1.1, 8.8.8.8, ISP)
- Резолвер → root → TLD → authoritative
- Потенциально 3-4 запроса

**Влияние TTL:**
- TTL записи = 300s → кэшируется 5 минут
- Короткий TTL (30s) → частые lookup-ы
- Длинный TTL (1h+) → менее гибко для failover

**Митигации:**

**1. DNS prefetch (HTML-подсказка):**
```html
<link rel="dns-prefetch" href="//api.example.com">
```
Браузер резолвит заранее; экономит RTT в момент реального запроса.

**2. Preconnect (ещё лучше):**
```html
<link rel="preconnect" href="//api.example.com">
```
DNS + TCP + TLS сделаны заранее.

**3. DNS over HTTPS (DoH) / DNS over TLS (DoT):**
- Шифруют DNS (приватность)
- Могут добавить latency (TLS handshake с резолвером); используйте прокси рядом с пользователем
- Cloudflare 1.1.1.1 быстрый

**4. DNS-кэширование на уровне приложения:**
- Многие HTTP-клиенты кэшируют резолвнутые IP
- Следите за устаревшим кэшем при failover (настройте соблюдение TTL)

**5. Anycast DNS:**
- Резолверы географически распределены; обслуживает ближайший POP
- Cloudflare DNS, Google DNS

**Ловушка (Java):**
- Старые JVM кэшировали DNS навсегда — пропускали failover-ы!
- `networkaddress.cache.ttl` — выставить значение не `-1` (например, 30s)
- Современные JVM: дефолт 30s

**Мониторинг:** разбивка end-to-end latency должна включать время DNS; инструменты вроде `curl -w`:
```bash
curl -o /dev/null -s -w "dns:%{time_namelookup} connect:%{time_connect} start:%{time_starttransfer}\n" https://api.site.com
```

## Q16. (!) gzip vs brotli vs zstd?

**Сжатие HTTP-ответов:**

**gzip:**
- Повсеместный (с 1990-х)
- Экономия ~5-10% против несжатого plaintext (на JSON — до 80%)
- Быстрые сжатие + распаковка
- Низкая нагрузка на CPU
- Дефолтное качество 6 (из 9)

**brotli (Google 2013):**
- Лучше степень сжатия, чем gzip (~15-25% меньше)
- Медленнее сжатие (на высоких уровнях качества)
- Быстрая распаковка
- Поддержка браузерами: все современные (широко включён на CDN)
- Уровни качества 0-11; типично 4-6 для динамики, 11 для статики (предварительно сжатой)

**zstd (Facebook 2016):**
- Быстрый + хорошая степень сжатия (часто бьёт gzip, близок к brotli)
- **Широко не стандартизирован для HTTP `Content-Encoding`** (2024: RFC 8878 есть, но распространение ограничено)
- Используется внутри приложений (сжатие в Kafka, файловые системы)

**Согласование Content-Encoding:**
- Клиент: `Accept-Encoding: gzip, br, zstd`
- Сервер выбирает лучший поддерживаемый

**Best practice:**
- Статика: предварительно сжать brotli level 11 + gzip level 9 → отдавать заранее вычисленное
- Динамика: brotli level 4-6 (баланс CPU и степени сжатия)
- Порог по минимальному размеру (< 1KB — сжимать не стоит)
- Не сжимать уже сжатое (JPEG, PNG, MP4) — CPU тратится впустую

**Исключения — атака BREACH:**
- Сжатие + HTTPS + секреты в ответе + контролируемый пользователем ввод = атакующий может извлечь секреты по изменениям размера
- **Митигация:** не сжимать чувствительные ответы; либо гарантировать отсутствие пользовательского ввода в ответе с секретами

**Обработка на CDN:**
- Часто отдаётся предварительно сжатым (платим за сжатие один раз при деплое)
- Некоторые CDN автоматически сжимают ответ origin-а

**Измерить:** `Content-Length` против сырого размера = коэффициент сжатия.

## Q17. Content-Encoding negotiation?

**Клиент сообщает о поддержке:**
```
Accept-Encoding: gzip, deflate, br, zstd
```

Со значениями качества:
```
Accept-Encoding: gzip;q=0.5, br;q=1.0
```

**Сервер выбирает один:**
```
Content-Encoding: br
```

**Сжимать можно и запрос, и ответ:**
- Запрос: `Content-Encoding: gzip` (редко, при POST сжатых данных)
- Ответ: типичный случай

**Identity:**
- `Accept-Encoding: identity` — явно попросить несжатое
- По умолчанию identity включён, если его не исключили

**Подводные камни:**

**`Vary: Accept-Encoding`** обязателен для CDN:
- Без него CDN может отдать brotli-ответ клиенту, понимающему только gzip → поломка
- С ним CDN кэширует отдельный вариант на каждую кодировку

**Middleboxes:**
- Старые прокси вырезают `Accept-Encoding` → сервер не видит поддержки
- HTTPS это предотвращает (шифрование)

**`Content-Length`:**
- Отражает сжатый размер
- Должен быть выставлен корректно

**Ограничения brotli:**
- Только HTTPS (браузеры не примут brotli поверх HTTP)

**Проверка инструментами:**
```bash
curl -H "Accept-Encoding: br, gzip" -I https://site.com/page
# check Content-Encoding header
```

## Q18. (!) Зачем CDN — latency math?

**Без CDN:**
- Origin в us-east-1
- Пользователь в Токио
- RTT ~130 ms
- Каждый запрос ассета: минимум 130 ms + передача

**С CDN:**
- Edge в Токио (~5 ms от пользователя)
- Первый запрос: пользователь → edge в Токио (miss) → origin → ответ (140 ms)
- Последующие: пользователь → edge в Токио (hit) → ответ (5-10 ms)

**Для типичной веб-страницы (50 запросов):**
- Без CDN: 130 ms × некоторая последовательная цепочка = 3-5 секунд
- С CDN (95% из кэша): в основном 5-10 ms → 0.5-1 секунда

**Плюсы помимо latency:**

**1. Разгрузка origin-а:**
- 95% cache hit → 95% трафика не доходит до origin
- Ниже счёт за bandwidth, меньше origin-кластер

**2. Поглощение DDoS:**
- У CDN глобальная ёмкость; атаки размазываются

**3. Терминация TLS на edge:**
- TLS handshake рядом с пользователем (несколько ms) против origin (150 ms)
- Огромный выигрыш по latency даже для некэшируемого контента

**4. Edge compute (Workers, Lambda@Edge):**
- Логика рядом с пользователем → быстрее ответы для динамики

**5. Отказоустойчивость:**
- Origin лежит → CDN отдаёт stale
- Multi-region failover origin-а

**Анализ затрат:**
- CDN egress ~$0.03-0.08 за GB
- Origin egress (AWS us-east-1 в интернет): $0.09/GB
- CDN часто **дешевле** прямой отдачи (и UX лучше)

**Не всё выигрывает:**
- Сильно персонализированное (уникально на пользователя) — всегда cache miss
- Real-time (низкий TTL) — выигрыш меньше
- Но терминация TLS на edge всё равно помогает

## Q19. (!) gRPC vs REST performance?

**gRPC:** RPC-фреймворк; protobuf + HTTP/2.

**REST:** JSON поверх HTTP (1.1 или 2).

**Различия по производительности:**

**1. Сериализация:**
- Protobuf: компактнее (на 30-50% меньше байт), быстрее парсинг
- JSON: универсальный, человекочитаемый

**2. Транспорт:**
- gRPC: всегда HTTP/2 — мультиплексирование, бинарный
- REST: часто HTTP/1.1 или 2

**3. Стриминг:**
- gRPC: двунаправленный стриминг нативно
- REST: для стриминга SSE или WebSocket

**4. Паттерн соединений:**
- gRPC: долгоживущие HTTP/2-соединения (пул 1-2)
- REST: много соединений или пул HTTP/2

**5. Язык/платформа:**
- gRPC: кодогенерация из .proto → типобезопасные stub-ы
- REST: рукописные клиенты или сгенерированные из OpenAPI

**Бенчмарки (грубо):**
- gRPC часто в 2-5 раз быстрее на запрос при высоком throughput
- Меньше payload-ы + скорость парсинга
- На той же машине: gRPC ~10k req/s, REST-JSON ~3k req/s (Java)

**Когда лучше REST:**
- Публичные API (браузеры зовут напрямую)
- Простота, тулинг (curl, Postman)
- Кэширование (HTTP-семантика)

**Когда лучше gRPC:**
- Внутренние микросервисы
- Высокий throughput
- Стриминг
- Строгая типизация / кодогенерация

**Минусы gRPC:**
- Плохая поддержка браузерами (нужен gRPC-Web прокси)
- Сложнее отладка (бинарный формат)
- Меньше экосистема тулинга
- L7-фичи middleware / балансировщика могут быть ограничены

**gRPC-Web:**
- Подмножество для браузеров (только unary + server streaming)
- Нужен Envoy-прокси для трансляции

**Миграция REST → gRPC:**
- Сначала замерьте; часто REST не настоящий bottleneck
- Внутренние горячие пути — высокий ROI
- Публичное API — редко стоит ломать контракт

## Q20. WebSockets — overhead и use cases?

**WebSocket (RFC 6455):**
- Полнодуплексное постоянное соединение поверх TCP
- Апгрейдится из HTTP/1.1 handshake
- Небольшой оверхед на фрейминг (~2-14 байт на сообщение)

**Сценарии использования:**
- **Real-time двунаправленный:** чат, совместное редактирование, игры
- **Server push:** уведомления, биржевые тикеры
- **Стриминг обновлений:** дашборды, live-спорт

**Overhead:**
- Handshake: HTTP/1.1-запрос + Upgrade-ответ (1 RTT)
- Постоянное соединение: нет handshake на каждое сообщение
- На фрейм: минимальный

**Против альтернатив:**

**WebSocket против SSE:**
- WebSocket: двунаправленный
- SSE: только сервер → клиент (проще)
- SSE поверх HTTP; дружелюбнее к прокси, переподключение встроено

**WebSocket против long polling:**
- Long polling: новый запрос после каждого события — большой оверхед
- WebSocket: одно соединение, много событий — гораздо эффективнее

**WebSocket против gRPC-стриминга:**
- WebSocket: дружелюбен к браузеру, простой протокол
- gRPC bidi streaming: типизирован, но нужен gRPC-Web

**Сложности:**
- Stateful — нужна sticky-сессия (LB всегда шлёт пользователя на тот же WS-сервер)
- Сложно масштабировать горизонтально (привязка к соединению)
- Прокси/файрволы могут дропнуть простаивающий WS → нужны ping/keepalive
- Нет HTTP-кэширования (после upgrade это семантически не HTTP)

**Масштабирование:**
- Redis pub/sub для broadcasting между инстансами сервера
- Централизованный брокер соединений (EMQX, Centrifugo, MQTT)

**Производительность:**
- Современные серверы держат 100K+ одновременных WS на инстанс (Netty, Node.js, Go)
- Память на соединение — ключевой ресурс; тюньте socket-буферы

## Q21. Server-Sent Events (SSE)?

**SSE:** однонаправленный поток сервер→клиент поверх HTTP.

**Формат:**
```
Content-Type: text/event-stream

event: update
data: {"price": 100}

event: heartbeat
data: ok
```

**Клиент (браузер):**
```js
const es = new EventSource('/events');
es.onmessage = (e) => console.log(e.data);
```

**Против WebSocket:**

| Аспект | SSE | WebSocket |
|--------|-----|-----------|
| Направление | Сервер → Клиент | Двунаправленный |
| Протокол | HTTP | WebSocket (после upgrade) |
| Переподключение | Автоматическое | Ручное |
| Event ID | Нативно (Last-Event-ID) | Вручную |
| Бинарные данные | Только текст | Текст + бинарь |
| Дружелюбность к прокси | Да (просто HTTP) | Может быть дропнут |
| Сложность | Проще | Гибче |

**Когда SSE:**
- Односторонние обновления (дашборды, уведомления, стриминг логов)
- Нужна HTTP-семантика (заголовки, авторизация, прокси)
- Проще деплой

**Когда WebSocket:**
- Настоящий двунаправленный обмен (чат, игры)
- Бинарные данные
- Ниже оверхед на сообщение (хотя разница незначительная)

**HTTP/2 + SSE:**
- Мультиплексирование позволяет многим SSE-стримам делить одно соединение → лучше масштабируется
- HTTP/1.1 блокирует один стрим на соединение

**HTTP/3 + SSE:**
- Нет HoL blocking → ещё лучше

**Частая ошибка:** считать, что «WebSocket всегда лучше». SSE часто идеально подходит и даёт более простой код.

## Q22. (!) Tail latency — причины и борьба?

**Tail latency:** задержки p99, p99.9 — худшие несколько процентов запросов.

**Почему важно:**
- Одна страница = 50 вызовов бэкенда (fan-out)
- Если у каждого p99 = 100 ms, p50 = 20 ms
- p50 страницы — где-то посередине, но **p99 страницы ≈ 100 ms** почти всегда
- **Доминирует самый медленный компонент**

**Причины:**

**1. Паузы GC:**
- GC старого поколения → паузы 100 ms+
- Фикс: тюнинг (G1, ZGC, Shenandoah); снизить аллокацию

**2. Очереди:**
- Очередь пула потоков забивается → часть запросов ждёт
- Закон Литтла: L = λ × W

**3. Сеть:**
- Потеря пакета + ретрансмит (~300 ms)
- TCP congestion backoff

**4. Дисковый I/O:**
- Сброс из БД, всплеск fsync при записи лога

**5. Борьба за CPU:**
- Шумный сосед (соседняя VM на той же машине)
- Фоновая задача выедает ядра

**6. Bufferbloat:**
- Глубокие сетевые буферы → queuing latency

**7. Промах кэша:**
- Промах L1/L2 → поход в БД (в 10-100 раз медленнее)

**Фиксы:**

**Hedged requests:**
- Слать на 2 реплики; берём ту, что быстрее
- Паттерны Google Spanner, MapReduce
- Тратит ~5-10% ресурсов; заметно режет p99

**Tied requests:**
- Отменяем медленную реплику, когда быстрая ответила
- Меньше потраченной впустую работы

**Circuit breaking:**
- Быстро падаем на плохом бэкенде; не добавляем в хвост

**Load shedding:**
- Отклоняем запросы вблизи насыщения
- Не даём медленности каскадировать

**Мониторинг:**
- Отслеживать p50, p90, p95, p99, p99.9
- **Не отслеживайте только p50!**
- Алерты на рост p99

## Q23. (!) Linux sysctl tuning для high-throughput?

**Тюнинг сетевого стека:**

```bash
# Larger socket buffers
net.core.rmem_max = 134217728  # 128MB
net.core.wmem_max = 134217728
net.ipv4.tcp_rmem = 4096 87380 134217728
net.ipv4.tcp_wmem = 4096 65536 134217728

# Higher connection backlog
net.core.somaxconn = 65535
net.core.netdev_max_backlog = 30000

# More ephemeral ports
net.ipv4.ip_local_port_range = 1024 65535

# TIME_WAIT reuse (client side)
net.ipv4.tcp_tw_reuse = 1

# FIN timeout
net.ipv4.tcp_fin_timeout = 15

# SYN backlog
net.ipv4.tcp_max_syn_backlog = 65535

# TCP fast open
net.ipv4.tcp_fastopen = 3

# Congestion control
net.ipv4.tcp_congestion_control = bbr
net.core.default_qdisc = fq
```

**Лимиты файловых дескрипторов:**
```bash
# /etc/security/limits.conf
* soft nofile 1000000
* hard nofile 1000000
```

**Connection tracking (если используется netfilter/iptables):**
```
net.netfilter.nf_conntrack_max = 1000000
```

**Не копируйте вслепую:** замеряйте до/после. Часть настроек помогает только конкретным нагрузкам.

**`tcp_tw_recycle`:** **удалён в Linux 4.12+** — никогда не использовать (ломает NAT).

**Мониторинг:**
- `ss -s` — сводка по сокетам
- `netstat -s` — статистика (ретрансмиты, дропы)
- `nstat` — сетевая статистика

**На уровне приложения:**
- Число воркеров / event loop-ов HTTP-сервера под количество CPU
- Размер пула потоков
- Лимиты пула соединений

## Q24. Debugging slow networks (tools)?

**Latency и throughput:**
- `ping <host>` — RTT, потери пакетов
- `traceroute` / `mtr` — latency по хопам
- `iperf3` — тест пропускной способности
- `tcpdump` / `wireshark` — захват пакетов

**Состояние соединений:**
- `ss -tn` — TCP-соединения
- `ss -ti` — с информацией о congestion (cwnd, rtt, ретрансмиты)
- `ss -ltn` — слушающие сокеты

**Статистика:**
- `netstat -s` / `nstat` — накопительные счётчики
  - `tcpExtTCPRcvCoalesce`, `tcpExtTCPRetransFail` — ищите аномалии
- `ip -s link` — статистика интерфейса (дропы, ошибки)

**Специфично для HTTP:**
```bash
curl -o /dev/null -s -w "dns:%{time_namelookup} conn:%{time_connect} tls:%{time_appconnect} first:%{time_starttransfer} total:%{time_total}\n" https://site.com
```

**DNS:**
- `dig example.com` — DNS-запрос
- `dig +trace` — полная трассировка

**Низкоуровневое:**
- `bpftrace` / `eBPF` — трассировка на уровне ядра
- `tcpdump -i eth0 port 443 -w capture.pcap` → Wireshark

**Service mesh:**
- Envoy / Istio: access-лог `%RESPONSE_FLAGS%` — таймауты upstream и т.п.

**Синтетический мониторинг:**
- Pingdom, Datadog Synthetics — непрерывные проверки из локаций по всему миру

**Workflow для «сервис X тормозит»:**
1. Быстро ли резолвится DNS? (время `dig`)
2. Быстрый ли TCP handshake? (`curl -w time_connect`)
3. Быстрый ли TLS handshake? (`curl -w time_appconnect`)
4. Быстро ли отвечает сервер? (`curl -w time_starttransfer`)
5. Медленно ли передаётся payload? (проблема bandwidth?)

**Каждый этап изолирует свою причину.**

## See also

- [Database Performance](database-performance-interview.md) — client-server network matters
- [Caching Performance](caching-performance-interview.md) — Redis RTT, CDN
- [JVM Performance Tuning](jvm-performance-tuning-interview.md) — GC causes tail latency
- [Application Profiling](application-profiling-interview.md) — measure time in network calls
- [Load Balancing](../architecture/load-balancing-interview.md) — network path impacts
- [API Gateway](../architecture/api-gateway-interview.md) — edge network layer
- [HTTP/REST](../api/http-rest-interview.md) — protocol details
- [gRPC](../api/grpc-interview.md) — HTTP/2, streaming
- [Resilience Patterns](../architecture/resilience-patterns-interview.md) — timeouts, retries
- [Observability](../monitoring/observability-interview.md) — network metrics и tracing

- [Caching Performance](caching-performance-interview.md)
- [Database Performance](database-performance-interview.md)
- [JVM Performance Tuning](jvm-performance-tuning-interview.md)
- [Memory Management](memory-management-interview.md)
- [Performance Testing](performance-testing-interview.md)
