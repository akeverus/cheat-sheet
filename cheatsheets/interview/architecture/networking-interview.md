---
title: "Вопросы на собеседовании: Сетевые протоколы и сети"
description: "Ответы по сетевым протоколам для backend-разработчиков: модель OSI, TCP/IP, TCP vs UDP, HTTP/1.1 vs HTTP/2 vs HTTP/3, TLS, DNS, WebSocket, SSE, балансировка L4/L7, CDN, reverse proxy, NAT, CORS, сокеты в Java."
tags:
  - interview
  - architecture
  - networking-interview
aliases:
  - "Networking interview"
  - "Сетевые протоколы собеседование"
  - "TCP UDP interview"
  - "HTTP/2 HTTP/3 interview"
  - "TLS DNS WebSocket interview"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `Сетевые протоколы и сети`

Ответы по сетевым протоколам для backend-разработчиков: модель `OSI`, стек `TCP/IP`, `TCP` vs `UDP`, эволюция `HTTP`, `TLS`, `DNS`, `WebSocket`, `SSE`, балансировка на уровнях L4/L7, `CDN`, `reverse proxy`, `NAT`, `CORS`, основы сокетов в Java.

Дата последнего обновления: 2026-04-13

**Сетевые протоколы** — фундамент любого backend-приложения. На собеседованиях ожидают, что разработчик понимает, как данные передаются от клиента к серверу, какие гарантии предоставляет каждый уровень стека, чем отличаются протоколы транспортного и прикладного уровней, и как инфраструктурные компоненты (`CDN`, `reverse proxy`, `load balancer`) влияют на архитектуру системы.

## Полезные ссылки

### Официальная документация

- [RFC 9293 — TCP](https://www.rfc-editor.org/rfc/rfc9293) — актуальная спецификация `TCP`
- [RFC 9000 — QUIC](https://www.rfc-editor.org/rfc/rfc9000) — транспортный протокол `QUIC`
- [RFC 9114 — HTTP/3](https://www.rfc-editor.org/rfc/rfc9114) — спецификация `HTTP/3`
- [RFC 7540 — HTTP/2](https://www.rfc-editor.org/rfc/rfc7540) — спецификация `HTTP/2`
- [RFC 8446 — TLS 1.3](https://www.rfc-editor.org/rfc/rfc8446) — спецификация `TLS 1.3`
- [What is DNS? | Cloudflare](https://www.cloudflare.com/learning/dns/what-is-dns/) — наглядное объяснение `DNS`
- [A Guide to Java Sockets (Baeldung)](https://www.baeldung.com/a-guide-to-java-sockets) — сокеты в Java
- [Exploring the New HTTP Client in Java (Baeldung)](https://www.baeldung.com/java-9-http-client) — `HttpClient` в Java 11+
- [Connection Timeout vs. Read Timeout for Java Sockets (Baeldung)](https://www.baeldung.com/java-socket-connection-read-timeout) — таймауты соединений в Java
- [Java HttpClient Connection Management (Baeldung)](https://www.baeldung.com/java-httpclient-connection-management) — connection pooling и keepalive в Java 11+
- [Set a Timeout in Spring WebClient (Baeldung)](https://www.baeldung.com/spring-webflux-timeout) — настройка таймаутов в Spring WebClient

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Модель OSI и стек TCP/IP**
- [Q1. (!) Что такое модель OSI и зачем она нужна?](#q1--что-такое-модель-osi-и-зачем-она-нужна)
- [Q2. Чем стек TCP/IP отличается от модели OSI?](#q2-чем-стек-tcpip-отличается-от-модели-osi)
- [Q3. Какие протоколы работают на каждом уровне стека TCP/IP?](#q3-какие-протоколы-работают-на-каждом-уровне-стека-tcpip)

**TCP и UDP**
- [Q4. (!) В чём разница между TCP и UDP?](#q4--в-чём-разница-между-tcp-и-udp)
- [Q5. (!) Как работает TCP three-way handshake?](#q5--как-работает-tcp-three-way-handshake)
- [Q6. Что такое TCP congestion control и какие алгоритмы используются?](#q6-что-такое-tcp-congestion-control-и-какие-алгоритмы-используются)
- [Q7. Что такое TCP keep-alive и зачем он нужен?](#q7-что-такое-tcp-keep-alive-и-зачем-он-нужен)
- [Q8. Что такое TCP window size и flow control?](#q8-что-такое-tcp-window-size-и-flow-control)

**HTTP: эволюция протокола**
- [Q9. (!) Чем отличаются HTTP/1.1, HTTP/2 и HTTP/3?](#q9--чем-отличаются-http11-http2-и-http3)
- [Q10. Что такое HTTP keep-alive и connection pooling?](#q10-что-такое-http-keep-alive-и-connection-pooling)
- [Q11. Что такое head-of-line blocking и как HTTP/2 и HTTP/3 решают эту проблему?](#q11-что-такое-head-of-line-blocking-и-как-http2-и-http3-решают-эту-проблему)
- [Q12. Что такое QUIC и почему HTTP/3 использует его вместо TCP?](#q12-что-такое-quic-и-почему-http3-использует-его-вместо-tcp)

**HTTPS и TLS**
- [Q13. (!) Как работает TLS handshake?](#q13--как-работает-tls-handshake)
- [Q14. Чем отличается TLS 1.2 от TLS 1.3?](#q14-чем-отличается-tls-12-от-tls-13)
- [Q15. Что такое сертификат X.509 и цепочка доверия?](#q15-что-такое-сертификат-x509-и-цепочка-доверия)

**DNS**
- [Q16. (!) Как работает DNS-резолвинг?](#q16--как-работает-dns-резолвинг)
- [Q17. Какие типы DNS-записей существуют и для чего они используются?](#q17-какие-типы-dns-записей-существуют-и-для-чего-они-используются)
- [Q18. Что такое DNS TTL и как он влияет на кэширование?](#q18-что-такое-dns-ttl-и-как-он-влияет-на-кэширование)

**WebSocket и SSE**
- [Q19. (!) Что такое WebSocket и чем он отличается от HTTP?](#q19--что-такое-websocket-и-чем-он-отличается-от-http)
- [Q20. Что такое Server-Sent Events (SSE) и когда их использовать вместо WebSocket?](#q20-что-такое-server-sent-events-sse-и-когда-их-использовать-вместо-websocket)

**Инфраструктура: балансировка, CDN, proxy**
- [Q21. (!) В чём разница между балансировкой на L4 и L7?](#q21--в-чём-разница-между-балансировкой-на-l4-и-l7)
- [Q22. Что такое reverse proxy и зачем он нужен?](#q22-что-такое-reverse-proxy-и-зачем-он-нужен)
- [Q23. Что такое CDN и как она работает?](#q23-что-такое-cdn-и-как-она-работает)
- [Q24. Что такое NAT и какие виды NAT существуют?](#q24-что-такое-nat-и-какие-виды-nat-существуют)

**CORS и безопасность сетевого уровня**
- [Q25. (!) Что такое CORS и зачем он нужен?](#q25--что-такое-cors-и-зачем-он-нужен)
- [Q26. Что такое preflight-запрос в CORS?](#q26-что-такое-preflight-запрос-в-cors)

**IP-адресация и подсети**
- [Q27. Что такое IP-адрес и чем отличаются IPv4 и IPv6?](#q27-что-такое-ip-адрес-и-чем-отличаются-ipv4-и-ipv6)
- [Q28. Что такое подсети (subnets) и маска подсети?](#q28-что-такое-подсети-subnets-и-маска-подсети)

**Сокеты и сетевое программирование в Java**
- [Q29. (!) Как работают сокеты в Java?](#q29--как-работают-сокеты-в-java)
- [Q30. Как использовать Java HttpClient для HTTP-запросов?](#q30-как-использовать-java-httpclient-для-http-запросов)
- [Q31. Что такое connection pooling на уровне HTTP-клиента и зачем он нужен?](#q31-что-такое-connection-pooling-на-уровне-http-клиента-и-зачем-он-нужен)

**Дополнительные темы**
- [Q32. Что происходит, когда вы вводите URL в браузер?](#q32-что-происходит-когда-вы-вводите-url-в-браузер)
- [Q33. (!) Какие основные метрики сетевой производительности должен знать backend-разработчик?](#q33--какие-основные-метрики-сетевой-производительности-должен-знать-backend-разработчик)

**Продвинутые темы**
- [Q34. (!) Что такое mTLS и как он отличается от TLS?](#q34--что-такое-mtls-и-как-он-отличается-от-tls)
- [Q35. Как работает HTTP/2 Server Push и почему от него отказываются?](#q35-как-работает-http2-server-push-и-почему-от-него-отказываются)
- [Q36. Что такое Service Mesh и как Istio использует mTLS?](#q36-что-такое-service-mesh-и-как-istio-использует-mtls)

**Kubernetes и облачные сети**
- [Q37. DNS в Kubernetes — CoreDNS, service discovery, headless services](#q37-dns-в-kubernetes--coredns-service-discovery-headless-services)
- [Q38. Network Policies в Kubernetes — Ingress/Egress правила](#q38-network-policies-в-kubernetes--ingressegress-правила)
- [Q39. NAT и SNAT в облачных окружениях](#q39-nat-и-snat-в-облачных-окружениях)

**Современные технологии**
- [Q40. Что такое eBPF и как он применяется в networking?](#q40-что-такое-ebpf-и-как-он-применяется-в-networking)
- [Q41. Certificate Pinning — что это и зачем нужен?](#q41-certificate-pinning--что-это-и-зачем-нужен)

**TCP vs UDP: практика**
- [Q42. TCP vs UDP — когда что выбирать для реальных сервисов?](#q42-tcp-vs-udp--когда-что-выбирать-для-реальных-сервисов)
- [Q43. Istio vs Linkerd — сравнение Service Mesh решений](#q43-istio-vs-linkerd--сравнение-service-mesh-решений)

---

## Q1. (!) Что такое модель OSI и зачем она нужна?

**Модель OSI** (Open Systems Interconnection) — эталонная модель, разделяющая сетевое взаимодействие на 7 уровней. Каждый уровень решает свою задачу и предоставляет интерфейс для уровня выше.

```mermaid
graph TB
    L7["7 — Application<br/>HTTP, FTP, DNS, SMTP"] --> L6
    L6["6 — Presentation<br/>SSL/TLS, сжатие, кодировка"] --> L5
    L5["5 — Session<br/>управление сессиями, RPC"] --> L4
    L4["4 — Transport<br/>TCP, UDP, QUIC"] --> L3
    L3["3 — Network<br/>IP, ICMP, маршрутизация"] --> L2
    L2["2 — Data Link<br/>Ethernet, MAC-адреса"] --> L1
    L1["1 — Physical<br/>кабели, радиоволны, электрика"]

    style L7 fill:#4CAF50,color:#fff
    style L4 fill:#2196F3,color:#fff
    style L3 fill:#FF9800,color:#fff
```

**Зачем нужна:**
- **Абстракция** — разработчик HTTP-сервиса не думает об электрических сигналах
- **Стандартизация** — оборудование разных вендоров может взаимодействовать
- **Диагностика** — при проблемах можно изолировать уровень: «проблема на L3 (маршрутизация) или на L7 (приложение)?»

> На собеседовании достаточно знать 4-уровневую модель `TCP/IP` и понимать, на каком уровне работает каждый протокол.

---

## Q2. Чем стек TCP/IP отличается от модели OSI?

Модель `TCP/IP` — практическая модель, используемая в реальных сетях. Она объединяет некоторые уровни `OSI`:

| TCP/IP (4 уровня) | OSI (7 уровней) | Примеры протоколов |
|--------------------|------------------|--------------------|
| Application | Application + Presentation + Session | `HTTP`, `DNS`, `FTP`, `SMTP` |
| Transport | Transport | `TCP`, `UDP`, `QUIC` |
| Internet | Network | `IP`, `ICMP`, `ARP` |
| Network Access | Data Link + Physical | `Ethernet`, `Wi-Fi` |

**Ключевое отличие:** `OSI` — теоретическая модель «как должно быть», `TCP/IP` — практическая модель «как есть». В реальном интернете используется `TCP/IP`.

---

## Q3. Какие протоколы работают на каждом уровне стека TCP/IP?

| Уровень | Протоколы | Назначение |
|---------|-----------|------------|
| Application | `HTTP/1.1`, `HTTP/2`, `HTTP/3`, `DNS`, `FTP`, `SMTP`, `SSH`, `WebSocket` | Логика приложений и представление данных |
| Transport | `TCP`, `UDP`, `QUIC` | Доставка данных между процессами, управление потоком |
| Internet | `IPv4`, `IPv6`, `ICMP`, `IGMP` | Маршрутизация пакетов между хостами |
| Network Access | `Ethernet`, `Wi-Fi (802.11)`, `ARP` | Передача фреймов в локальной сети |

Важно понимать **инкапсуляцию**: данные приложения заворачиваются в `TCP`-сегмент, тот — в `IP`-пакет, а тот — в `Ethernet`-фрейм. На каждом уровне добавляется свой заголовок.

---

## Q4. (!) В чём разница между TCP и UDP?

| Характеристика | `TCP` | `UDP` |
|---------------|-------|-------|
| Соединение | Connection-oriented (handshake) | Connectionless |
| Надёжность | Гарантирует доставку, retransmit | Best-effort, потери возможны |
| Порядок | Гарантирует порядок пакетов | Не гарантирует |
| Flow control | Да (sliding window) | Нет |
| Congestion control | Да (slow start, AIMD) | Нет |
| Overhead заголовка | 20+ байт | 8 байт |
| Скорость | Медленнее из-за гарантий | Быстрее |

**Когда использовать `TCP`:**
- Веб-приложения (`HTTP`), email (`SMTP`), передача файлов (`FTP`)
- Любые сценарии, где важна целостность данных

**Когда использовать `UDP`:**
- Видеостриминг, VoIP, онлайн-игры
- `DNS`-запросы (короткие, один пакет)
- Приложения, где допустима потеря пакетов, но важна скорость

> На собеседовании часто спрашивают: «Почему `DNS` использует `UDP`?» — потому что запрос и ответ помещаются в один пакет, overhead `TCP`-handshake неоправдан. Но `DNS` переключается на `TCP` для ответов > 512 байт (zone transfer).

---

## Q5. (!) Как работает TCP three-way handshake?

**Three-way handshake** — процесс установления `TCP`-соединения между клиентом и сервером:

```mermaid
sequenceDiagram
    participant C as Клиент
    participant S as Сервер

    C->>S: 1. SYN (seq=x)
    Note right of S: Клиент хочет соединение
    S->>C: 2. SYN-ACK (seq=y, ack=x+1)
    Note left of C: Сервер согласен
    C->>S: 3. ACK (ack=y+1)
    Note right of S: Соединение установлено

    C->>S: Данные
    S->>C: Данные
```

**Шаги:**

1. **SYN** — клиент отправляет пакет с флагом `SYN` и случайным начальным номером последовательности `seq=x`
2. **SYN-ACK** — сервер отвечает пакетом `SYN-ACK`: своим `seq=y` и подтверждением `ack=x+1`
3. **ACK** — клиент подтверждает: `ack=y+1`. Соединение установлено, можно передавать данные

**Закрытие соединения** — четырёхэтапный процесс (four-way handshake): `FIN` → `ACK` → `FIN` → `ACK`. Каждая сторона независимо закрывает свой поток.

> Важно знать про **SYN flood** — атака, при которой злоумышленник отправляет множество `SYN`-пакетов без завершения handshake. Защита: `SYN cookies`, ограничение backlog.

---

## Q6. Что такое TCP congestion control и какие алгоритмы используются?

**Congestion control** — механизм `TCP`, предотвращающий перегрузку сети. Отправитель адаптирует скорость передачи данных на основе обратной связи от сети.

**Основные фазы:**

1. **Slow Start** — окно растёт экспоненциально (удваивается каждый RTT) до порога `ssthresh`
2. **Congestion Avoidance** — после порога окно растёт линейно (+1 MSS за RTT)
3. **Fast Retransmit** — при получении 3 дублирующих `ACK` пакет ретранслируется, не дожидаясь тайм-аута
4. **Fast Recovery** — после fast retransmit окно уменьшается вдвое, а не до 1 (как при тайм-ауте)

**Популярные алгоритмы:**

| Алгоритм | Особенности |
|----------|-------------|
| `Reno` | Классический, fast retransmit + fast recovery |
| `Cubic` | По умолчанию в Linux, агрессивнее на быстрых каналах |
| `BBR` (Google) | Основан на оценке bandwidth и RTT, не на потерях |

> Для backend-разработчика важно понимать, что `TCP`-соединение не может мгновенно использовать весь канал — оно «разгоняется». Это объясняет, почему `HTTP keep-alive` и connection pooling так важны.

---

## Q7. Что такое TCP keep-alive и зачем он нужен?

**TCP keep-alive** — механизм обнаружения «мёртвых» соединений. Если по `TCP`-соединению не передаются данные в течение заданного времени, одна из сторон отправляет probe-пакет.

**Параметры (Linux):**
- `tcp_keepalive_time` — время бездействия до первого probe (по умолчанию 7200 секунд = 2 часа)
- `tcp_keepalive_intvl` — интервал между probe-пакетами (по умолчанию 75 секунд)
- `tcp_keepalive_probes` — количество probe без ответа, после которого соединение закрывается (по умолчанию 9)

**Зачем нужен:**
- Обнаружение сбоев на удалённой стороне (crash, сеть упала)
- Поддержание соединения через `NAT`/firewall (они сбрасывают idle-соединения)
- Освобождение ресурсов сервера от zombie-соединений

> Не путать с **HTTP keep-alive** — это механизм переиспользования `TCP`-соединения для нескольких HTTP-запросов (заголовок `Connection: keep-alive`).

---

## Q8. Что такое TCP window size и flow control?

**Flow control** — механизм, при котором получатель сообщает отправителю, сколько данных он готов принять. Это предотвращает переполнение буфера получателя.

- **Receive window (rwnd)** — размер свободного места в буфере получателя, передаётся в каждом `ACK`-пакете
- **Window scaling** — расширение `RFC 7323`, позволяющее окну превышать 65535 байт (до 1 ГБ)

**Отличие от congestion control:**
- **Flow control** — защищает получателя от переполнения
- **Congestion control** — защищает сеть от перегрузки
- Эффективное окно = `min(rwnd, cwnd)`, где `cwnd` — congestion window

---

## Q9. (!) Чем отличаются HTTP/1.1, HTTP/2 и HTTP/3?

| Характеристика | `HTTP/1.1` | `HTTP/2` | `HTTP/3` |
|---------------|------------|----------|----------|
| Транспорт | `TCP` | `TCP` | `QUIC` (поверх `UDP`) |
| Мультиплексирование | Нет (один запрос за раз) | Да (множество потоков в одном соединении) | Да (нативное, без HOL blocking) |
| Сжатие заголовков | Нет | `HPACK` | `QPACK` |
| Server Push | Нет | Да | Да (ограниченно) |
| Шифрование | Опционально | Де-факто обязательно | Обязательно (встроено в `QUIC`) |
| HOL blocking | На уровне соединения | На уровне `TCP` | Решено — потери в одном потоке не блокируют другие |
| Установка соединения | 1 RTT (TCP) + 2 RTT (TLS) | 1 RTT (TCP) + 1-2 RTT (TLS) | 1 RTT (0-RTT при повторном) |

**`HTTP/1.1`** — текстовый протокол, одно соединение = один запрос в момент времени. Браузеры открывают 6-8 параллельных соединений для обхода ограничения.

**`HTTP/2`** — бинарный протокол с мультиплексированием. Один `TCP`-connection, много потоков. Но потеря одного пакета на уровне `TCP` блокирует все потоки.

**`HTTP/3`** — использует `QUIC` вместо `TCP`. Каждый поток независим — потеря пакета в одном не влияет на остальные.

---

## Q10. Что такое HTTP keep-alive и connection pooling?

**HTTP keep-alive** (`Connection: keep-alive`) — механизм переиспользования `TCP`-соединения для нескольких HTTP-запросов:

- В `HTTP/1.0` каждый запрос создавал новое `TCP`-соединение
- В `HTTP/1.1` keep-alive включён по умолчанию
- В `HTTP/2` одно соединение используется для всех запросов к хосту

**Connection pooling** — паттерн, при котором приложение поддерживает пул готовых `TCP`-соединений и переиспользует их:

```java
// Java HttpClient — connection pool управляется автоматически
HttpClient client = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(5))
    .version(HttpClient.Version.HTTP_2)
    .build();

// Все запросы через этот клиент используют общий пул
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .GET()
    .build();

HttpResponse<String> response = client.send(request,
    HttpResponse.BodyHandlers.ofString());
```

**Почему важен connection pooling:**
- Экономия на `TCP` handshake (1 RTT) и `TLS` handshake (1-2 RTT)
- Экономия на `TCP` slow start — переиспользуемое соединение уже «разогрето»
- Снижение нагрузки на ОС (каждое соединение = file descriptor)

---

## Q11. Что такое head-of-line blocking и как HTTP/2 и HTTP/3 решают эту проблему?

**Head-of-line (HOL) blocking** — ситуация, когда медленный запрос блокирует обработку последующих запросов.

**HOL blocking в `HTTP/1.1`:**
- Запросы в одном соединении обрабатываются последовательно
- Если первый запрос медленный, все последующие ждут
- Обходное решение — открывать несколько соединений (6-8 в браузерах)

**`HTTP/2` частично решает проблему:**
- Мультиплексирование позволяет передавать много потоков по одному `TCP`-соединению
- Но потеря **одного TCP-пакета** блокирует **все потоки** — это HOL blocking на уровне `TCP`

**`HTTP/3` полностью решает проблему:**
- `QUIC` реализует мультиплексирование на транспортном уровне
- Каждый поток независим — потеря пакета в одном потоке не влияет на другие
- Нет HOL blocking ни на уровне приложения, ни на уровне транспорта

---

## Q12. Что такое QUIC и почему HTTP/3 использует его вместо TCP?

**`QUIC`** (Quick UDP Internet Connections) — транспортный протокол, разработанный Google и стандартизированный IETF (RFC 9000). Работает поверх `UDP`.

**Преимущества QUIC над TCP:**

| Аспект | `TCP` | `QUIC` |
|--------|-------|--------|
| Handshake | TCP handshake + TLS handshake (2-3 RTT) | Совмещённый (1 RTT, 0-RTT при повторном) |
| HOL blocking | Все потоки блокируются при потере | Потеря влияет только на затронутый поток |
| Шифрование | Отдельный слой (TLS) | Встроено, включая метаданные |
| Миграция соединения | Привязка к IP:port (при смене Wi-Fi → 4G соединение теряется) | Connection ID — соединение сохраняется при смене сети |
| Реализация | В ядре ОС, обновления медленные | В user space, легко обновляется |

> `QUIC` работает поверх `UDP`, но сам реализует надёжную доставку, контроль перегрузки и шифрование. Это не «ненадёжный UDP» — это полноценный транспортный протокол, который использует `UDP` как «обёртку» для прохождения через `NAT` и firewall.

---

## Q13. (!) Как работает TLS handshake?

**`TLS` handshake** — процесс установления зашифрованного соединения между клиентом и сервером.

```mermaid
sequenceDiagram
    participant C as Клиент
    participant S as Сервер

    Note over C,S: TLS 1.2 (2 RTT)
    C->>S: ClientHello (версии, cipher suites, random)
    S->>C: ServerHello (выбранный cipher, random)
    S->>C: Certificate (сертификат сервера)
    S->>C: ServerKeyExchange + ServerHelloDone
    C->>C: Проверка сертификата через CA
    C->>S: ClientKeyExchange (pre-master secret)
    C->>S: ChangeCipherSpec + Finished
    S->>C: ChangeCipherSpec + Finished
    Note over C,S: Зашифрованное соединение установлено
```

**Основные шаги:**

1. **ClientHello** — клиент отправляет поддерживаемые версии `TLS`, cipher suites и случайное число
2. **ServerHello** — сервер выбирает версию и cipher suite, отправляет свой random
3. **Certificate** — сервер отправляет свой сертификат `X.509`
4. **Key Exchange** — стороны обмениваются данными для генерации общего секрета (pre-master secret)
5. **Verification** — клиент проверяет сертификат сервера по цепочке доверия (CA)
6. **Finished** — обе стороны генерируют session keys из pre-master secret и переключаются на шифрование

> В `TLS 1.3` handshake сокращён до 1 RTT, а при повторном соединении возможен **0-RTT** — данные отправляются в первом же пакете.

---

## Q14. Чем отличается TLS 1.2 от TLS 1.3?

| Характеристика | `TLS 1.2` | `TLS 1.3` |
|---------------|-----------|-----------|
| Handshake | 2 RTT | 1 RTT (0-RTT при возобновлении) |
| Cipher suites | Множество, включая слабые (RSA, CBC) | Только 5 безопасных (все с AEAD) |
| Key exchange | RSA или (EC)DHE | Только (EC)DHE (forward secrecy обязательна) |
| Совместимость | Широкая | Требует современных клиентов |
| Производительность | Медленнее | Быстрее за счёт меньшего числа round-trip |

**Forward secrecy** в `TLS 1.3` обязательна: даже если долгосрочный ключ сервера будет скомпрометирован, прошлые сессии нельзя расшифровать, потому что для каждой сессии генерируются уникальные эфемерные ключи.

---

## Q15. Что такое сертификат X.509 и цепочка доверия?

**Сертификат `X.509`** — стандартный формат цифрового сертификата, содержащий:
- Публичный ключ владельца
- Доменное имя (Common Name / Subject Alternative Name)
- Имя издателя (Certificate Authority)
- Срок действия
- Цифровую подпись CA

**Цепочка доверия:**

1. **Root CA** — корневой сертификат, предустановлен в ОС/браузере (самоподписанный)
2. **Intermediate CA** — промежуточный сертификат, подписан корневым
3. **Leaf certificate** — сертификат сервера, подписан промежуточным CA

Клиент проверяет подпись от leaf до root. Если root CA есть в доверенном хранилище — сертификат валиден.

> Для backend-разработчика важно: в Java truststore (`cacerts`) содержит root CA. Если вы работаете с self-signed сертификатами или внутренними CA, их нужно добавить в truststore, иначе `SSLHandshakeException`.

---

## Q16. (!) Как работает DNS-резолвинг?

**`DNS`** (Domain Name System) — преобразует доменные имена в IP-адреса.

```mermaid
sequenceDiagram
    participant B as Браузер
    participant R as Recursive Resolver
    participant Root as Root DNS
    participant TLD as TLD DNS (.com)
    participant Auth as Authoritative DNS

    B->>B: 1. Проверка локального кэша
    B->>R: 2. Запрос: example.com → ?
    R->>Root: 3. Кто отвечает за .com?
    Root->>R: 4. Обратись к TLD-серверу .com
    R->>TLD: 5. Кто отвечает за example.com?
    TLD->>R: 6. Обратись к ns1.example.com
    R->>Auth: 7. Какой IP у example.com?
    Auth->>R: 8. A-запись: 93.184.216.34
    R->>B: 9. IP: 93.184.216.34 (кэшируется на TTL)
```

**Этапы резолвинга:**

1. **Локальный кэш** — браузер, ОС, `/etc/hosts`
2. **Recursive resolver** — обычно предоставляется ISP или это `8.8.8.8` (Google), `1.1.1.1` (Cloudflare)
3. **Root servers** — 13 кластеров (A–M), знают адреса TLD-серверов
4. **TLD servers** — серверы верхнего уровня (`.com`, `.org`, `.ru`), знают авторитативные серверы
5. **Authoritative server** — хранит конечные записи для домена

Весь процесс обычно занимает 20-120 мс. Благодаря кэшированию на каждом уровне повторные запросы разрешаются мгновенно.

---

## Q17. Какие типы DNS-записей существуют и для чего они используются?

| Тип записи | Назначение | Пример |
|-----------|------------|--------|
| `A` | Доменное имя → IPv4-адрес | `example.com → 93.184.216.34` |
| `AAAA` | Доменное имя → IPv6-адрес | `example.com → 2606:2800:220:1:...` |
| `CNAME` | Алиас на другое доменное имя | `www.example.com → example.com` |
| `MX` | Почтовый сервер для домена | `example.com → mail.example.com` |
| `NS` | Авторитативный DNS-сервер | `example.com → ns1.example.com` |
| `TXT` | Произвольный текст (SPF, DKIM, верификация) | `v=spf1 include:_spf.google.com ~all` |
| `SRV` | Сервис + порт (используется в service discovery) | `_http._tcp.example.com → 8080 web.example.com` |
| `PTR` | Обратный DNS (IP → имя) | `34.216.184.93 → example.com` |

> `SRV`-записи особенно важны для backend: `Kubernetes`, `Consul` и другие системы service discovery используют их для автоматического обнаружения сервисов (подробнее в [вопросах по распределённым системам](distributed-systems-interview.md)).

---

## Q18. Что такое DNS TTL и как он влияет на кэширование?

**TTL** (Time to Live) — время в секундах, в течение которого DNS-запись может кэшироваться.

**Типичные значения:**
- `300` секунд (5 минут) — для сервисов, где важна быстрая смена IP (failover)
- `3600` секунд (1 час) — стандартное значение
- `86400` секунд (24 часа) — для стабильных записей

**Влияние на backend:**
- **Низкий TTL** — быстрый failover при аварии, но больше нагрузки на DNS
- **Высокий TTL** — меньше DNS-запросов, но долгое переключение при сбоях
- **При миграциях** — перед сменой IP снижают TTL заранее (например, за 24 часа до миграции ставят TTL=300)

> Важно помнить, что JVM по умолчанию кэширует DNS-записи «навечно» (`networkaddress.cache.ttl` в `java.security`). Для микросервисов в облаке это критично — IP меняются, а Java держит старый адрес. Решение: установить `networkaddress.cache.ttl=30`.

---

## Q19. (!) Что такое WebSocket и чем он отличается от HTTP?

**`WebSocket`** — протокол полнодуплексной двунаправленной связи поверх одного `TCP`-соединения (RFC 6455).

**Отличия от `HTTP`:**

| Аспект | `HTTP` | `WebSocket` |
|--------|--------|-------------|
| Направление | Request-response (клиент инициирует) | Полный дуплекс (обе стороны в любой момент) |
| Соединение | Короткоживущее или keep-alive | Постоянное, долгоживущее |
| Overhead | Заголовки в каждом запросе | Минимальный фрейминг (2-14 байт) |
| Протокол | `http://` / `https://` | `ws://` / `wss://` |

**Установка соединения** — через HTTP Upgrade:

```
GET /chat HTTP/1.1
Host: server.example.com
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
Sec-WebSocket-Version: 13
```

Сервер отвечает `101 Switching Protocols`, после чего соединение переключается на `WebSocket`.

**Применение:** чаты, уведомления в реальном времени, онлайн-игры, совместное редактирование, торговые платформы.

---

## Q20. Что такое Server-Sent Events (SSE) и когда их использовать вместо WebSocket?

**`SSE`** (Server-Sent Events) — механизм однонаправленной потоковой передачи данных от сервера к клиенту по стандартному `HTTP`.

| Аспект | `SSE` | `WebSocket` |
|--------|-------|-------------|
| Направление | Только сервер → клиент | Двунаправленный |
| Протокол | Стандартный `HTTP`/`HTTPS` | Свой протокол (`ws://`) |
| Формат данных | Только текст (UTF-8) | Текст и бинарные данные |
| Переподключение | Автоматическое (встроено в API) | Нужно реализовывать вручную |
| Лимит соединений | 6 на домен (`HTTP/1.1`), нет лимита в `HTTP/2` | Нет лимита |
| Прокси/CDN | Проходит через любые HTTP-прокси | Может потребовать настройки |

**Когда использовать `SSE`:**
- Live-ленты, новостные обновления
- Стриминг ответов от AI/LLM (подробнее в [HTTP & REST](../api/http-rest-interview.md))
- Dashboards с обновлениями в реальном времени
- Уведомления

**Когда использовать `WebSocket`:**
- Чаты (клиент тоже отправляет данные)
- Онлайн-игры
- Совместное редактирование

> Правило: если клиенту не нужно отправлять данные серверу в рамках этого канала — используйте `SSE`. Он проще, работает через стандартный `HTTP`, автоматически переподключается и не требует специальной настройки proxy.

---

## Q21. (!) В чём разница между балансировкой на L4 и L7?

| Характеристика | L4 (Transport) | L7 (Application) |
|---------------|----------------|-------------------|
| Уровень OSI | Транспортный | Прикладной |
| Данные для решения | IP-адрес, порт, протокол | URL, заголовки, cookies, тело запроса |
| Производительность | Высокая (50-100 мкс задержки) | Ниже (инспекция содержимого) |
| Терминация TLS | Нет (passthrough) | Да (может расшифровывать и инспектировать) |
| Sticky sessions | По IP-адресу | По cookie, заголовку, URL |
| Content-based routing | Нет | Да (по URL, host, header) |
| Примеры | `HAProxy` (TCP mode), AWS NLB | `Nginx`, `HAProxy` (HTTP mode), AWS ALB |

**Когда использовать L4:**
- Максимальная производительность, минимальная задержка
- TCP/UDP-трафик, не HTTP (базы данных, `gRPC`, игровые серверы)

**Когда использовать L7:**
- Маршрутизация по URL (`/api/v1` → Service A, `/api/v2` → Service B)
- A/B-тестирование (по cookie или header)
- SSL termination
- Канареечные деплои (подробнее в [вопросах по балансировке](load-balancing-interview.md))

---

## Q22. Что такое reverse proxy и зачем он нужен?

**Reverse proxy** — сервер, который принимает запросы от клиентов и перенаправляет их на backend-серверы. Клиент не знает о существовании backend-серверов.

**Отличие от forward proxy:**
- **Forward proxy** — прокси для клиентов (клиент знает о прокси, сервер — нет)
- **Reverse proxy** — прокси для серверов (клиент не знает о прокси)

**Задачи reverse proxy:**
- **Load balancing** — распределение нагрузки между серверами
- **SSL termination** — расшифровка `TLS` на прокси, backend получает plain HTTP
- **Кэширование** — кэширование статики и ответов API
- **Сжатие** — gzip/brotli-сжатие ответов
- **Rate limiting** — ограничение запросов от одного клиента
- **Защита** — скрытие IP и топологии backend, WAF

**Популярные reverse proxy:** `Nginx`, `HAProxy`, `Envoy`, `Traefik`, `Caddy`.

---

## Q23. Что такое CDN и как она работает?

**`CDN`** (Content Delivery Network) — географически распределённая сеть серверов, кэширующих контент ближе к пользователям.

**Принцип работы:**

1. Пользователь запрашивает `https://cdn.example.com/image.png`
2. `DNS` возвращает IP ближайшего edge-сервера (через anycast или GeoDNS)
3. Edge-сервер проверяет кэш:
   - **Cache hit** → возвращает контент (минимальная задержка)
   - **Cache miss** → запрашивает у origin-сервера, кэширует, возвращает клиенту

**Что кэширует CDN:**
- Статические файлы: CSS, JS, изображения, видео
- API-ответы с заголовком `Cache-Control`
- HTML-страницы (для статических сайтов)

**Важные HTTP-заголовки для CDN:**
- `Cache-Control: public, max-age=31536000` — кэшировать на год
- `Cache-Control: no-cache` — проверять свежесть перед использованием
- `Cache-Control: private` — не кэшировать на CDN (только в браузере)

**Популярные CDN:** Cloudflare, AWS CloudFront, Akamai, Fastly.

> Для backend-разработчика важно правильно настраивать `Cache-Control` заголовки (подробнее в [стратегиях кэширования](caching-strategies-interview.md)).

---

## Q24. Что такое NAT и какие виды NAT существуют?

**`NAT`** (Network Address Translation) — механизм трансляции IP-адресов при прохождении пакетов через маршрутизатор. Позволяет множеству устройств в локальной сети использовать один публичный IP.

**Виды NAT:**

| Вид | Описание |
|-----|----------|
| **Static NAT** | 1:1 маппинг private → public IP |
| **Dynamic NAT** | Пул public IP, назначается динамически |
| **PAT (Port Address Translation)** | Один public IP, разные порты для разных устройств (наиболее распространён) |

**Проблемы NAT для backend:**
- Peer-to-peer соединения затруднены (нужен NAT traversal: STUN, TURN)
- `WebSocket` и долгоживущие соединения могут разрываться при тайм-ауте NAT-таблицы
- IP-адрес клиента за NAT не уникален — для идентификации нужны другие механизмы

> В контексте backend: если ваш сервис за `NAT`, необходимо корректно обрабатывать заголовок `X-Forwarded-For` для получения реального IP клиента.

---

## Q25. (!) Что такое CORS и зачем он нужен?

**`CORS`** (Cross-Origin Resource Sharing) — механизм, позволяющий серверу указать, какие источники (origin) могут обращаться к его ресурсам из браузера.

**Проблема:** Same-Origin Policy запрещает JavaScript-коду на `https://frontend.com` делать запросы к `https://api.backend.com`. `CORS` — контролируемый способ ослабить это ограничение.

**Ключевые заголовки:**

| Заголовок | Назначение |
|-----------|------------|
| `Access-Control-Allow-Origin` | Разрешённые origins (`*` или конкретный) |
| `Access-Control-Allow-Methods` | Разрешённые HTTP-методы |
| `Access-Control-Allow-Headers` | Разрешённые заголовки запроса |
| `Access-Control-Allow-Credentials` | Разрешить cookies/авторизацию |
| `Access-Control-Max-Age` | Время кэширования preflight-ответа |

**Конфигурация в Spring Boot:**

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://frontend.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

> Распространённая ошибка: `Access-Control-Allow-Origin: *` с `Access-Control-Allow-Credentials: true` — это запрещено спецификацией. Если нужны credentials, origin должен быть конкретным (подробнее в [Application Security](../security/application-security-interview.md)).

---

## Q26. Что такое preflight-запрос в CORS?

**Preflight** — предварительный `OPTIONS`-запрос, который браузер автоматически отправляет перед «непростым» запросом, чтобы проверить, разрешает ли сервер такой запрос.

**Запрос считается «непростым», если:**
- Метод отличается от `GET`, `HEAD`, `POST`
- Заголовки выходят за пределы «простых» (`Accept`, `Content-Type`, `Content-Language`)
- `Content-Type` отличается от `application/x-www-form-urlencoded`, `multipart/form-data`, `text/plain`

**Пример preflight:**

```
OPTIONS /api/users HTTP/1.1
Host: api.backend.com
Origin: https://frontend.com
Access-Control-Request-Method: PUT
Access-Control-Request-Headers: Content-Type, Authorization
```

**Ответ сервера:**

```
HTTP/1.1 204 No Content
Access-Control-Allow-Origin: https://frontend.com
Access-Control-Allow-Methods: GET, POST, PUT, DELETE
Access-Control-Allow-Headers: Content-Type, Authorization
Access-Control-Max-Age: 86400
```

> **Оптимизация:** `Access-Control-Max-Age` позволяет кэшировать preflight-ответ. Без него каждый «непростой» запрос будет сопровождаться дополнительным `OPTIONS`-запросом.

---

## Q27. Что такое IP-адрес и чем отличаются IPv4 и IPv6?

| Характеристика | `IPv4` | `IPv6` |
|---------------|--------|--------|
| Длина адреса | 32 бита (4 октета) | 128 бит (8 групп по 16 бит) |
| Формат | `192.168.1.1` | `2001:0db8:85a3::8a2e:0370:7334` |
| Пространство адресов | ~4.3 млрд | ~3.4 × 10^38 |
| NAT | Необходим (адресов мало) | Не нужен (адресов достаточно) |
| Заголовок | Переменной длины, сложный | Фиксированный, упрощённый |
| IPsec | Опционально | Встроено |

**Зарезервированные диапазоны IPv4:**

| Диапазон | Назначение |
|----------|------------|
| `10.0.0.0/8` | Private (класс A) |
| `172.16.0.0/12` | Private (класс B) |
| `192.168.0.0/16` | Private (класс C) |
| `127.0.0.0/8` | Loopback |
| `0.0.0.0` | Все интерфейсы (bind) |

> Для backend-разработчика: при конфигурации сервисов в Docker/Kubernetes важно понимать разницу между `0.0.0.0` (слушать на всех интерфейсах) и `127.0.0.1` (только localhost).

---

## Q28. Что такое подсети (subnets) и маска подсети?

**Подсеть** — логическое деление IP-сети. Маска подсети определяет, какая часть IP-адреса относится к сети, а какая — к хосту.

**CIDR-нотация:**
- `192.168.1.0/24` — первые 24 бита = сеть, последние 8 = хост
- `/24` = маска `255.255.255.0` = 254 хоста
- `/16` = маска `255.255.0.0` = 65534 хоста
- `/32` = один конкретный хост

**Пример расчёта:**
- Адрес `192.168.1.100/24`
- Сетевой адрес: `192.168.1.0`
- Broadcast: `192.168.1.255`
- Доступные хосты: `192.168.1.1` — `192.168.1.254`

> В контексте Kubernetes и облаков: при создании VPC нужно правильно планировать CIDR-блоки. Слишком маленькая подсеть `/28` (14 хостов) может закончиться при масштабировании, слишком большая `/16` (65K хостов) — расточительна.

---

## Q29. (!) Как работают сокеты в Java?

**Сокет** — конечная точка двунаправленного канала связи между двумя программами в сети. В Java сокеты реализованы в пакете `java.net`.

**TCP-сервер:**

```java
try (ServerSocket serverSocket = new ServerSocket(8080)) {
    System.out.println("Сервер запущен на порту 8080");

    while (true) {
        // accept() блокирует поток до подключения клиента
        Socket clientSocket = serverSocket.accept();

        // Обработка в отдельном потоке
        new Thread(() -> handleClient(clientSocket)).start();
    }
}

private void handleClient(Socket socket) {
    try (
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
            socket.getOutputStream(), true)
    ) {
        String request = in.readLine();
        out.println("Echo: " + request);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

**TCP-клиент:**

```java
try (Socket socket = new Socket("localhost", 8080);
     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
     BufferedReader in = new BufferedReader(
         new InputStreamReader(socket.getInputStream()))) {

    out.println("Hello, Server!");
    String response = in.readLine();
    System.out.println("Ответ: " + response);
}
```

**Важные моменты:**
- `ServerSocket.accept()` — блокирующий вызов, для масштабируемости используют `NIO` (`Selector`, `Channel`) или виртуальные потоки (Java 21+)
- Каждый сокет занимает file descriptor — при большом числе соединений возможен лимит (`ulimit -n`)
- В продакшене raw-сокеты почти не используются — вместо них `Netty`, `Undertow` или Spring WebFlux

---

## Q30. Как использовать Java HttpClient для HTTP-запросов?

`HttpClient` появился в Java 11 и заменяет устаревший `HttpURLConnection`:

```java
// Создание клиента (потокобезопасен, переиспользуйте!)
HttpClient client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)    // предпочитать HTTP/2
    .connectTimeout(Duration.ofSeconds(5))
    .followRedirects(HttpClient.Redirect.NORMAL)
    .build();

// Синхронный GET-запрос
HttpRequest getRequest = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users/1"))
    .header("Accept", "application/json")
    .GET()
    .build();

HttpResponse<String> response = client.send(getRequest,
    HttpResponse.BodyHandlers.ofString());

System.out.println("Status: " + response.statusCode());
System.out.println("Body: " + response.body());

// Асинхронный POST-запрос
HttpRequest postRequest = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/users"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(
        """
        {"name": "John", "email": "john@example.com"}
        """))
    .build();

client.sendAsync(postRequest, HttpResponse.BodyHandlers.ofString())
    .thenApply(HttpResponse::body)
    .thenAccept(System.out::println)
    .join();
```

**Особенности `HttpClient`:**
- Поддерживает `HTTP/1.1` и `HTTP/2` с автоматическим fallback
- Connection pool управляется автоматически (размер по умолчанию неограничен)
- Поддерживает `WebSocket` через `HttpClient.newWebSocketBuilder()`
- Потокобезопасен — создайте один экземпляр и переиспользуйте

---

## Q31. Что такое connection pooling на уровне HTTP-клиента и зачем он нужен?

**Connection pooling** — паттерн, при котором клиент поддерживает пул открытых `TCP`-соединений и переиспользует их для последующих запросов.

**Без пула (naive подход):**
```
Запрос 1: DNS → TCP handshake → TLS handshake → Request/Response → Close
Запрос 2: DNS → TCP handshake → TLS handshake → Request/Response → Close
         ~300ms overhead на каждый запрос
```

**С пулом:**
```
Запрос 1: DNS → TCP → TLS → Request/Response (соединение остаётся в пуле)
Запрос 2: Request/Response (переиспользуем соединение)
         ~0ms overhead
```

**Настройка в популярных клиентах:**

| Клиент | Параметр | По умолчанию |
|--------|----------|-------------|
| Java `HttpClient` | Автоматический пул | Неограничен |
| Apache `HttpClient` | `maxConnPerRoute` / `maxConnTotal` | 5 / 25 |
| OkHttp | `connectionPool(maxIdleConnections, keepAliveDuration)` | 5 / 5 мин |
| Spring `RestClient` | Зависит от underlying клиента | Зависит |

> Правило: при межсервисной коммуникации в микросервисах connection pool — обязательный компонент. Без него каждый HTTP-запрос тратит 100-300мс на установление соединения (подробнее в [микросервисной архитектуре](microservices-interview.md)).

---

## Q32. Что происходит, когда вы вводите URL в браузер?

Классический вопрос на собеседовании, проверяющий понимание всего сетевого стека:

1. **Парсинг URL** — браузер определяет протокол (`https`), хост (`example.com`), порт (`443`), путь (`/page`)

2. **DNS-резолвинг** — браузер проверяет кэш → рекурсивный резолвер → root → TLD → authoritative → получает IP-адрес (подробнее в [Q16](#q16--как-работает-dns-резолвинг))

3. **TCP handshake** — three-way handshake с сервером: `SYN` → `SYN-ACK` → `ACK` (1 RTT)

4. **TLS handshake** — согласование шифрования: ClientHello → ServerHello → Certificate → Key Exchange → Finished (1-2 RTT для TLS 1.2, 1 RTT для TLS 1.3)

5. **HTTP-запрос** — браузер отправляет `GET /page HTTP/2` с заголовками (`Host`, `User-Agent`, `Accept`, cookies)

6. **Обработка на сервере** — reverse proxy → load balancer → application server → бизнес-логика → ответ

7. **HTTP-ответ** — сервер возвращает `200 OK` с HTML-содержимым и заголовками (`Content-Type`, `Cache-Control`)

8. **Рендеринг** — браузер парсит HTML, запрашивает CSS/JS/изображения (параллельно), строит DOM, рендерит страницу

> Глубина ответа на этот вопрос показывает уровень кандидата. Junior назовёт 3-4 шага, Senior раскроет нюансы каждого уровня.

---

## Q33. (!) Какие основные метрики сетевой производительности должен знать backend-разработчик?

| Метрика | Описание | Типичные значения |
|---------|----------|-------------------|
| **Latency (задержка)** | Время от отправки запроса до получения первого байта ответа | LAN: < 1мс, same region: 1-5мс, cross-region: 50-200мс |
| **RTT (Round-Trip Time)** | Время туда-обратно для одного пакета | LAN: < 1мс, интернет: 20-300мс |
| **Throughput (пропускная способность)** | Объём данных за единицу времени | Мбит/с или запросов/сек |
| **Bandwidth** | Максимальная пропускная способность канала | 1 Гбит/с, 10 Гбит/с |
| **Packet loss** | Процент потерянных пакетов | < 0.1% — норма, > 1% — проблемы |
| **Jitter** | Вариация задержки между пакетами | Критично для VoIP/видео |
| **TTFB (Time to First Byte)** | Время до первого байта ответа | < 200мс — хорошо, > 600мс — плохо |

**Инструменты диагностики:**
- `ping` — RTT и packet loss
- `traceroute` / `mtr` — маршрут и задержка на каждом hop
- `curl -w` — детальная разбивка по фазам HTTP-запроса
- `ss` / `netstat` — состояние TCP-соединений
- `tcpdump` / `Wireshark` — анализ пакетов

```bash
# Детальная разбивка HTTP-запроса
curl -w "\nDNS: %{time_namelookup}s\nConnect: %{time_connect}s\nTLS: %{time_appconnect}s\nTTFB: %{time_starttransfer}s\nTotal: %{time_total}s\n" \
     -o /dev/null -s https://example.com
```

## Q34. (!) Что такое mTLS и как он отличается от TLS?

**TLS** (Transport Layer Security) — односторонняя аутентификация: клиент проверяет сертификат **сервера**, но сервер не проверяет клиента.

**mTLS** (mutual TLS) — двусторонняя аутентификация: **оба** участника предъявляют сертификаты и проверяют друг друга.

```mermaid
sequenceDiagram
    participant Client
    participant Server

    Note over Client,Server: TLS Handshake (стандартный)
    Client->>Server: ClientHello
    Server->>Client: ServerHello + Certificate
    Client->>Server: (опционально) CertificateRequest
    Client->>Server: Finished (сессионный ключ)

    Note over Client,Server: mTLS Handshake (дополнительно)
    Server->>Client: CertificateRequest ← ОБЯЗАТЕЛЬНО
    Client->>Server: Client Certificate
    Server->>Client: Verified ✓ (или закрывает соединение)
```

**Где применяется mTLS:**
- **Service Mesh (Istio, Linkerd)** — автоматический mTLS между всеми pod-ами
- **API между партнёрами** — банковские API (PCI DSS требует mTLS)
- **IoT-устройства** — аутентификация устройств без паролей

**Настройка mTLS в Spring Boot:**

```yaml
# application.yml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    # mTLS: требуем клиентский сертификат
    client-auth: need          # need | want | none
    trust-store: classpath:truststore.p12
    trust-store-password: ${SSL_TRUSTSTORE_PASSWORD}
```

**Java HTTP-клиент с клиентским сертификатом:**

```java
SSLContext sslContext = SSLContext.getInstance("TLS");
KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
KeyStore keyStore = KeyStore.getInstance("PKCS12");
keyStore.load(new FileInputStream("client.p12"), password);
kmf.init(keyStore, password);
sslContext.init(kmf.getKeyManagers(), trustManagers, null);

HttpClient client = HttpClient.newBuilder()
    .sslContext(sslContext)
    .build();
```

**mTLS vs API Key vs OAuth2:**

| Механизм | Аутентификация | Управление | Производительность |
|----------|---------------|------------|-------------------|
| mTLS | Криптографическая | PKI, сложная | Высокая (рукопожатие 1 раз) |
| API Key | Секретный токен | Простая | Высокая |
| OAuth2 | Токен + scopes | Гибкая | Средняя (проверка токена) |

## Q35. Как работает HTTP/2 Server Push и почему от него отказываются?

**HTTP/2 Server Push** — механизм, позволяющий серверу отправить ресурсы клиенту **до того**, как клиент их запросит, предугадав зависимости.

**Как работает:**

```mermaid
sequenceDiagram
    participant Browser
    participant Server

    Browser->>Server: GET /index.html
    Server-->>Browser: PUSH_PROMISE: /style.css
    Server-->>Browser: PUSH_PROMISE: /app.js
    Server-->>Browser: Response: index.html
    Server-->>Browser: Push: style.css
    Server-->>Browser: Push: app.js
    Note over Browser: Браузер уже имеет CSS и JS\nдо завершения парсинга HTML
```

**Почему от Server Push отказываются:**

1. **Chrome удалил поддержку Server Push** в Chrome 106 (2022): исследования показали, что в большинстве случаев Push либо не нужен (ресурс уже в кэше), либо конкурирует с реальными запросами
2. **HTTP/3 / QUIC не поддерживает Server Push** в стандартном режиме
3. **Альтернатива:** заголовок `Link: </style.css>; rel=preload` + Early Hints (103 статус) работают эффективнее

**Заголовок `103 Early Hints` (замена Server Push):**

```
HTTP/1.1 103 Early Hints
Link: </style.css>; rel=preload; as=style
Link: </app.js>; rel=preload; as=script

... (сервер обрабатывает запрос) ...

HTTP/1.1 200 OK
Content-Type: text/html
```

Браузер начинает загрузку ресурсов, пока сервер генерирует HTML — без сложности PUSH_PROMISE.

## Q36. Что такое Service Mesh и как Istio использует mTLS?

**Service Mesh** — инфраструктурный слой, управляющий коммуникацией между микросервисами через sidecar-прокси, без изменения кода приложений.

```mermaid
graph TB
    subgraph "Pod A"
        AppA[Order Service] <-->|localhost| PA[Envoy Proxy]
    end
    subgraph "Pod B"
        AppB[Payment Service] <-->|localhost| PB[Envoy Proxy]
    end

    PA <-->|mTLS + metrics + tracing| PB
    PA -->|telemetry| CP[Control Plane\nIstiod]
    PB -->|telemetry| CP
```

**Что даёт Istio Service Mesh:**

| Функция | Описание |
|---------|----------|
| **mTLS автоматически** | Все pod-ы получают SPIFFE-сертификаты; трафик шифруется без изменения кода |
| **Traffic management** | Canary, circuit breaker, retry, timeout через `VirtualService` / `DestinationRule` |
| **Observability** | Distributed tracing, metrics, access logs — из коробки |
| **Authorization** | `AuthorizationPolicy`: "Order Service может вызывать только Payment Service" |

**Пример: mTLS политика в Istio:**

```yaml
# Требуем mTLS для всех сервисов в namespace
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
  namespace: production
spec:
  mtls:
    mode: STRICT  # STRICT | PERMISSIVE | DISABLE
```

**Пример: AuthorizationPolicy:**

```yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: payment-service-policy
spec:
  selector:
    matchLabels:
      app: payment-service
  rules:
  - from:
    - source:
        principals:
        - "cluster.local/ns/production/sa/order-service"
    to:
    - operation:
        methods: ["POST"]
        paths: ["/payments/*"]
```

**Когда Service Mesh не нужен:**
- Небольшое количество сервисов (< 10)
- Команда не готова к операционной сложности Istio
- Простой вариант: Linkerd (lighter weight) или реализация mTLS вручную в Spring Boot

> Для backend-разработчика ключевые метрики — **TTFB**, **p99 latency** и **throughput**. Мониторинг этих метрик помогает вовремя обнаружить деградацию производительности (подробнее в [паттернах масштабируемости](scalability-patterns-interview.md)).

## Q37. DNS в Kubernetes — CoreDNS, service discovery, headless services

В Kubernetes DNS — основной механизм service discovery. Все pod-ы могут обращаться к сервисам по имени.

**CoreDNS** — DNS-сервер по умолчанию в Kubernetes (заменил kube-dns в k8s 1.13+). Работает как Deployment в namespace `kube-system`.

**Структура DNS-имён:**

```
<service>.<namespace>.svc.<cluster-domain>

# Примеры:
payment-service.production.svc.cluster.local  # полное имя
payment-service.production                    # из другого namespace
payment-service                               # из того же namespace
```

**Типы DNS-записей для сервисов:**

| Тип сервиса | DNS-запись | Что возвращает |
|---|---|---|
| ClusterIP | A/AAAA | IP сервиса (виртуальный) |
| Headless (`clusterIP: None`) | A/AAAA | IP каждого pod-а |
| ExternalName | CNAME | Внешнее DNS-имя |
| NodePort / LoadBalancer | A/AAAA | IP сервиса |

**Headless Services** — когда нужен прямой доступ к pod-ам (без load balancing):

```yaml
apiVersion: v1
kind: Service
metadata:
  name: cassandra
spec:
  clusterIP: None  # headless!
  selector:
    app: cassandra
  ports:
    - port: 9042
---
# DNS вернёт IP всех pod-ов, а не виртуальный IP сервиса
# nslookup cassandra → 10.0.0.1, 10.0.0.2, 10.0.0.3 (по одному на pod)
```

**Когда headless нужен:**
- StatefulSet (Kafka, Cassandra, Redis Cluster) — прямая адресация pod-ов
- Клиентский load balancing (Consul, gRPC)
- Service discovery через DNS SRV-записи

**Настройка DNS в pod-е:**

```yaml
spec:
  dnsPolicy: ClusterFirst  # по умолчанию
  dnsConfig:
    options:
      - name: ndots
        value: "5"  # количество точек для определения FQDN
```

> `ndots: 5` — стандарт Kubernetes. Означает: если имя содержит < 5 точек, сначала ищем через search domains (`.svc.cluster.local` и т.д.), потом как глобальное имя.

## Q38. Network Policies в Kubernetes — Ingress/Egress правила

**Network Policy** — Kubernetes-ресурс, определяющий правила входящего (Ingress) и исходящего (Egress) трафика для pod-ов. По умолчанию все pod-ы доступны друг другу без ограничений.

**Важно:** Network Policy работает только с CNI-плагинами, поддерживающими их (Calico, Cilium, Weave). Flannel — не поддерживает.

**Пример: разрешить входящий трафик только от order-service:**

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-order-to-payment
  namespace: production
spec:
  podSelector:
    matchLabels:
      app: payment-service     # к каким pod-ам применяется
  policyTypes:
    - Ingress
    - Egress
  ingress:
    - from:
        - podSelector:
            matchLabels:
              app: order-service  # только от order-service
        - namespaceSelector:
            matchLabels:
              name: production    # только из namespace production
      ports:
        - protocol: TCP
          port: 8080
  egress:
    - to:
        - podSelector:
            matchLabels:
              app: postgres
      ports:
        - protocol: TCP
          port: 5432
    - to:                        # разрешить DNS (обязательно!)
        - namespaceSelector:
            matchLabels:
              kubernetes.io/metadata.name: kube-system
      ports:
        - protocol: UDP
          port: 53
```

**Default deny all — базовый паттерн безопасности:**

```yaml
# Запретить весь входящий трафик в namespace
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny-ingress
  namespace: production
spec:
  podSelector: {}  # все pod-ы
  policyTypes:
    - Ingress
```

**Типичные ошибки:**
- Забыть разрешить UDP 53 (DNS) в egress → pod не может резолвить имена
- Селекторы AND vs OR: `from` с несколькими элементами → OR, внутри одного элемента → AND
- Network Policy применяется на уровне namespace — нужно создавать в каждом namespace

## Q39. NAT и SNAT в облачных окружениях

**NAT (Network Address Translation)** — преобразование IP-адресов при прохождении пакетов через маршрутизатор.

**Типы NAT:**

| Тип | Направление | Что меняется | Применение |
|---|---|---|---|
| **SNAT** (Source NAT) | Исходящий | IP источника | Pod → интернет |
| **DNAT** (Destination NAT) | Входящий | IP назначения | Интернет → сервис |
| **Masquerading** | Исходящий | IP на IP маршрутизатора | Домашний роутер |
| **Full NAT** | Оба направления | Оба IP | Load balancer |

**SNAT в Kubernetes:**

Когда pod обращается к внешнему ресурсу (интернет), кластер выполняет SNAT — подменяет IP pod-а на IP ноды. Это нужно потому что IP pod-ов (`10.0.0.x`) — приватные, недостижимы снаружи.

```
Pod (10.0.0.5) → SNAT (node IP 192.168.1.10) → Интернет
Интернет → DNAT (192.168.1.10) → Pod (10.0.0.5)
```

**Проблема SNAT в Kubernetes:**
- Внешний сервис видит IP ноды, не pod-а → трудно audit trail
- При масштабировании node-pool IP меняются

**Решения:**
- **NAT Gateway** (AWS, GCP, Azure) — статический выходной IP для всего кластера
- **Cilium Egress Gateway** — SNAT через выделенный gateway pod
- **kube-proxy iptables rules** — Kubernetes управляет NAT правилами через iptables

**DNAT в Kubernetes (Service → Pod):**

```
Клиент → Service IP (10.96.0.1:80) → iptables DNAT → Pod IP (10.0.0.5:8080)
```

Kubernetes использует iptables (kube-proxy) или eBPF (Cilium) для реализации DNAT при обращении к Service.

**В облаке (AWS):**
- `NAT Gateway` — управляемый SNAT для приватных подсетей
- `Internet Gateway` — для публичных подсетей (нет NAT, публичный IP напрямую)
- `Elastic IP` — статический публичный IP для NAT Gateway

> Понимание NAT критично для диагностики сетевых проблем: «почему сервис видит IP 10.x.x.x вместо реального IP клиента?» — это SNAT на уровне load balancer.

## Q40. Что такое eBPF и как он применяется в networking?

**eBPF** (extended Berkeley Packet Filter) — технология Linux ядра, позволяющая запускать sandbox-программы в пространстве ядра без изменения исходного кода ядра и без загрузки модулей.

**Архитектура eBPF:**

```
User Space:
  Приложение → compiles eBPF program → loads into kernel

Kernel Space:
  eBPF verifier → JIT compilation → eBPF VM
  Hooks: kprobes, tracepoints, XDP, TC, socket filters
```

**Применение в networking:**

| Применение | Как | Инструмент |
|---|---|---|
| **Packet filtering** | XDP hook на сетевом интерфейсе (до network stack) | Cilium, XDP-based firewall |
| **Load balancing** | DNAT без iptables, на уровне NIC | Cilium, Facebook Katran |
| **Network policies** | Замена iptables на eBPF maps | Cilium |
| **Observability** | Трейсинг сетевых вызовов без overhead | Pixie, Hubble |
| **DDoS protection** | XDP drop на 100Gbps скорости | Cloudflare |

**Cilium** — CNI-плагин для Kubernetes на базе eBPF:

```
Традиционный путь:  NIC → iptables (5000+ правил) → network stack → pod
Cilium eBPF:        NIC → eBPF map lookup (O(1)) → pod
```

Преимущества Cilium перед iptables:
- Производительность: O(1) lookup через eBPF maps вместо O(n) iptables rules
- Масштабируемость: iptables деградирует при 10000+ правил
- Видимость: Hubble — built-in observability без дополнительного overhead

**Почему eBPF важен для backend-разработчика:**

```bash
# Инструменты диагностики на eBPF:
bpftrace  # трейсинг системных вызовов
tcpdump + eBPF filters
pixie     # production observability без кода в приложении
```

> eBPF — фундаментальная технология следующего поколения сетевой инфраструктуры. Google, Meta, Cloudflare используют eBPF в production. На собеседованиях достаточно объяснить суть и знать Cilium как практическое применение.

## Q41. Certificate Pinning — что это и зачем нужен?

**Certificate Pinning** — техника безопасности, при которой клиент принимает только конкретный сертификат или публичный ключ сервера, игнорируя системный trust store.

**Проблема без pinning:**

```
Стандартный TLS:
Клиент доверяет ЛЮБОМУ сертификату, подписанному ЛЮБЫМ доверенным CA.
Если CA скомпрометирован → man-in-the-middle возможен.
```

**С certificate pinning:**

```
Клиент знает заранее: "Сервер api.myapp.com должен предъявить
публичный ключ с fingerprint = SHA256:abc123..."
Любой другой сертификат → соединение разрывается.
```

**Типы pinning:**

| Тип | Что пинируется | Гибкость при ротации |
|---|---|---|
| Certificate pinning | Весь сертификат | Низкая (менять при каждом обновлении) |
| Public key pinning | Публичный ключ | Средняя (ключ живёт дольше сертификата) |
| CA pinning | Конкретный CA | Высокая |

**Реализация в Java (OkHttp):**

```java
OkHttpClient client = new OkHttpClient.Builder()
    .certificatePinner(new CertificatePinner.Builder()
        .add("api.myapp.com",
             "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
        .build())
    .build();
```

**Минусы certificate pinning:**
- При ротации сертификата — нужно обновить все клиенты
- Устаревшие клиенты перестают работать
- Сложнее отлаживать (инструменты вроде Charles Proxy не работают)

**Альтернативы:**
- **mTLS** — сервер проверяет клиента, взаимная аутентификация
- **Certificate Transparency** — публичный лог выданных сертификатов
- **HPKP** (deprecated) — HTTP-заголовок для pinning в браузерах

> Certificate pinning актуален для мобильных приложений и high-security API (банки, платёжные системы). В Kubernetes service-to-service коммуникации его заменяет mTLS через Istio/cert-manager.

## Q42. TCP vs UDP — когда что выбирать для реальных сервисов?

Выбор между TCP и UDP определяется требованиями к надёжности, latency и порядку доставки.

**Ключевые различия:**

| Характеристика | TCP | UDP |
|---|---|---|
| Гарантия доставки | Да (ACK + retry) | Нет |
| Порядок пакетов | Гарантирован | Нет |
| Соединение | Connection-oriented | Connectionless |
| Overhead | Высокий (заголовок 20+ байт) | Низкий (заголовок 8 байт) |
| Congestion control | Да | Нет |
| Latency | Выше (handshake, ACK) | Ниже |
| HOL blocking | Есть | Нет |

**Когда TCP:**

```
✓ HTTP/REST API — надёжность важнее latency
✓ Базы данных (PostgreSQL, MySQL) — нельзя потерять транзакцию
✓ Kafka, RabbitMQ — гарантия доставки сообщений
✓ SSH, SFTP — управляющий трафик
✓ Email (SMTP) — нельзя потерять письмо
✓ Файловая передача — нужна целостность
```

**Когда UDP:**

```
✓ DNS — короткий запрос/ответ, потеря = повтор запроса
✓ VoIP / видеозвонки — важна latency, пропуск кадра лучше паузы
✓ Online gaming — старый пакет = устаревшие данные, не нужен
✓ Live streaming — задержка хуже потери кадра
✓ QUIC/HTTP/3 — UDP с собственной надёжностью поверх
✓ Service discovery (mDNS) — broadcast/multicast
✓ Мониторинг (StatsD, syslog) — потеря метрик некритична
```

**Гибридный подход — UDP с надёжностью поверх:**

```
QUIC = UDP + надёжность + TLS 1.3
WebRTC = UDP + SRTP/DTLS для медиа
SCTP = надёжность UDP-style + мультипоток
```

**Практический пример — выбор для real-time системы:**

```
Позиции трейдеров (stock trading):
- Рыночные данные (market data feed) → UDP multicast
  (потеря старого тика не критична, latency важна)
- Ордера (order placement) → TCP / FIX protocol
  (нельзя потерять ордер)
```

> На собеседовании: UDP выбирают когда latency критична, данные быстро устаревают и приложение само обрабатывает потери. TCP — когда нужна гарантия доставки и порядок. QUIC — когда нужно лучшее из обоих миров.

## Q43. Istio vs Linkerd — сравнение Service Mesh решений

Оба инструмента реализуют Service Mesh через sidecar-паттерн, но имеют разные приоритеты.

**Сравнение:**

| Характеристика | Istio | Linkerd |
|---|---|---|
| Data plane | Envoy proxy (Rust/C++) | linkerd2-proxy (Rust) |
| Control plane | Istiod | control plane (Go) |
| Размер sidecar | ~100MB | ~10MB |
| CPU overhead | Средний-высокий | Низкий |
| Функциональность | Максимальная | Умеренная |
| Сложность | Высокая | Средняя |
| Поддержка протоколов | HTTP/1.1, HTTP/2, gRPC, TCP | HTTP/1.1, HTTP/2, gRPC |
| WASM расширения | Да (Envoy WASM) | Нет |
| Бесплатная версия | CNCF graduated | CNCF graduated |

**Архитектура (оба похожи):**

```
┌─────────────────────────────┐
│  Control Plane              │
│  Istiod / Linkerd CP        │ ← политики, сертификаты, конфигурация
└─────────────────────────────┘
           │ xDS API / gRPC
┌────────────────────────────┐
│  Pod                       │
│  [App] ←→ [Sidecar Proxy]  │ ← перехватывает весь трафик
└────────────────────────────┘
```

**Когда выбирать Istio:**
- Нужны расширенные возможности: traffic management (canary, A/B), fault injection, advanced authorization
- Большая команда с DevOps-экспертизой
- Multi-cluster federation
- Интеграция с external auth (OIDC, JWT validation на mesh-уровне)

**Когда выбирать Linkerd:**
- Приоритет простоты и низкого overhead
- Небольшая команда, нет deep Kubernetes expertise
- Достаточно mTLS + observability + basic load balancing
- Production-ready быстро, без кривой обучения Istio

**Минимальный Istio пример:**

```bash
# Установка через Helm
helm install istio-base istio/base -n istio-system
helm install istiod istio/istiod -n istio-system

# Включить sidecar injection для namespace
kubectl label namespace production istio-injection=enabled

# Теперь все pod-ы получают sidecar автоматически
```

**Минимальный Linkerd пример:**

```bash
linkerd install | kubectl apply -f -
linkerd inject deployment.yaml | kubectl apply -f -
# Проще, быстрее, меньше конфигурации
```

**Альтернативы:**
- **Cilium Service Mesh** — eBPF-based, без sidecar overhead
- **Consul Connect** — от HashiCorp, интеграция с Vault
- **AWS App Mesh** — managed service mesh для EKS

> Для большинства компаний Linkerd — правильный старт: меньше сложности при достаточной функциональности. Istio оправдан когда нужны продвинутые возможности traffic management или мультикластерные сценарии.

---

## See also

- [HTTP & REST](../api/http-rest-interview.md) — методы, заголовки, коды ответов и REST-принципы поверх HTTP
- [Балансировка нагрузки](load-balancing-interview.md) — L4 vs L7 балансировка, алгоритмы распределения трафика
- [Распределённые системы](distributed-systems-interview.md) — сетевые партиции, CAP и latency в распределённых системах
- [API Gateway](api-gateway-interview.md) — маршрутизация и трансформация на уровне прикладного протокола
- [Микросервисная архитектура](microservices-interview.md) — service mesh, Istio и межсервисная сеть
- [Паттерны масштабируемости](scalability-patterns-interview.md) — CDN, шардирование и горизонтальное масштабирование
- [gRPC](../api/grpc-interview.md) — высокопроизводительный транспорт на базе HTTP/2 и Protocol Buffers
- [Docker](../devops/docker-interview.md) — сетевые режимы контейнеров: bridge, host, overlay
