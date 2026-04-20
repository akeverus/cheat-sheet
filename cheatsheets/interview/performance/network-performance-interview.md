---
title: "Вопросы на собеседовании: Network Performance"
description: "Network performance: latency, bandwidth, TCP tuning, HTTP/2, HTTP/3 QUIC, CDN, compression, connection reuse, keepalive, gRPC, WebSockets, tail latency"
tags:
  - interview
  - performance
  - network-performance-interview
aliases:
  - "Network Performance interview"
  - "Network tuning"
  - "HTTP Performance"
  - "Network Performance собеседование"
difficulty: "intermediate"
updated: "2026-04-19"
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

**Latency** — задержка (time for packet to travel A→B).
- Измеряется в ms
- RTT (round-trip time) = 2× one-way

**Bandwidth** — пропускная способность (data per unit time).
- Измеряется в bps / Gbps

**Аналогия:** truck from LA to NY
- Bandwidth: capacity of truck (how much cargo)
- Latency: time to drive cross-country

**Key insight:** latency и bandwidth **независимы**.

**Improve bandwidth:**
- Upgrade fiber / NIC
- More parallel links

**Improve latency:**
- **NOT possible to beat speed of light** (~20 km/ms fiber)
- Geographic proximity = only real fix
- CDN = deploy edge closer к user

**Modern network:** bandwidth continues to improve (10 Gbps → 100 Gbps); latency **physical limit**.

**Impact на apps:**
- Chatty protocols (many small requests) → latency-bound
- Bulk transfer → bandwidth-bound
- Web page load: tons of small requests → latency typically dominates

**Example LA ↔ NY:**
- Speed of light: ~20ms each way; RTT ≥ 40ms
- Actual: ~70ms (routing, queuing)
- For 100 requests sequentially: 7 seconds just для network
- Parallelize (HTTP/2 multiplexing) → all within 1 RTT + transfer time

## Q2. (!) Типичные RTT-значения?

**Localhost:** 0.05 - 0.2 ms

**Same data center (intra-DC):** 0.5 - 2 ms

**Same region (intra-region):** 2 - 10 ms

**Cross-region (same continent):**
- US east ↔ west: ~70 ms
- Europe west ↔ east: ~30 ms

**Cross-continent:**
- US ↔ Europe: ~80-100 ms
- US ↔ Asia: ~120-180 ms

**Submarine cable ↔ peer:** ~150-250 ms

**Satellite:** 500+ ms (geostationary); Starlink ~50 ms (LEO)

**Mobile 4G:** 20-50 ms first hop
**5G:** 5-15 ms first hop
**Wi-Fi:** 2-10 ms to router

**Impact на апликации:**

**5 sequential queries (5× RTT):**
- Local: 0.5ms → 2.5ms (fast)
- Cross-region: 70ms × 5 = 350ms (slow)
- Cross-continent: 180ms × 5 = 900ms (painful)

**Design implication:**
- Batch requests (reduce round trips)
- Parallelize independent calls
- Deploy multi-region when user-facing

**Measurement tools:**
- `ping <host>` — ICMP RTT
- `traceroute <host>` — per-hop latency
- `mtr <host>` — continuous

## Q3. (!) Что такое bandwidth-delay product?

**BDP = bandwidth × round-trip time**

**Интерпретация:** сколько data "in flight" fitting в pipe.

**Example:**
- 1 Gbps link, 50 ms RTT
- BDP = 1e9 bits/s × 0.05 s = 5e7 bits = 6.25 MB

**Impact:** TCP needs **sufficient receive window** to keep pipe full.

If receive window (rwnd) < BDP:
- Sender blocks on ACK
- Effective throughput < available bandwidth

**TCP window scaling:** 16-bit window → 64KB max; extension RFC 1323 allows larger (up to GB).

**Default TCP buffers:** Linux default usually 4MB auto-scaled. For long fat networks (high BDP) — тюнить:
```
net.ipv4.tcp_rmem = 4096 87380 16777216
net.ipv4.tcp_wmem = 4096 65536 16777216
```

**Long Fat Networks (LFN):**
- High bandwidth + high latency (satellite, cross-continent high-speed)
- BDP large → need big buffers
- Without tuning, throughput limited by buffer, not bandwidth

**Check:**
- `ss -ti` — shows current congestion window (cwnd), send/recv buffers
- `iperf3 -c server -w <window>` — test with specific window

**Rule of thumb:** для 10 Gbps intercontinental → receive buffer ≥ 10MB.

## Q4. (!) TCP slow start и congestion control?

**TCP не шлёт full bandwidth на старте** — avoids congestion.

**Slow start:**
- Initial cwnd = 10 MSS (~14KB) Linux default
- Each ACK → cwnd += 1 MSS → exponential growth
- Continues до packet loss или ssthresh

**Congestion avoidance:**
- After ssthresh: linear growth (cwnd += 1 MSS per RTT)
- Packet loss → cwnd halved (multiplicative decrease)

**Impact:**
- Short-lived connection never reaches full bandwidth
- Example: 1 Gbps link, 50ms RTT
  - After 1 RTT: cwnd = 20 MSS = 28KB → 4.5 Mbps effective
  - After 5 RTT: cwnd = 320 MSS = 450KB → still 70 Mbps
  - Needs seconds to saturate gigabit

**Algorithms:**
- **Reno / CUBIC (Linux default 2.6+):** loss-based; cwnd grows cubic function
- **BBR (Google 2016):** model-based (bandwidth * RTT); no reliance on loss
- **Westwood, Veno, HSTCP:** alternatives

**Implication для apps:**
- **Reuse connections** (HTTP keep-alive) — cwnd remains high across requests
- New connection = slow start from zero
- Cross-region + short request = long slow start tax

**Increase initial cwnd:**
```
ip route change default via <gw> initcwnd 30
```
Linux 3.10+ default 10; some tune higher (20-40) для better page load.

## Q5. (!) TCP handshake overhead?

**3-way handshake:**
```
Client → SYN → Server     (0.5 RTT)
Client ← SYN-ACK ← Server (1 RTT)
Client → ACK → Server     (1.5 RTT; но data piggyback возможно)
```

**Overhead:** **1 RTT** перед data can send.

**Cost:**
- Local: 1-2ms (negligible)
- Cross-region: 70ms dead time
- Mobile: add cellular latency

**Multi-request без keep-alive:**
- Each request new handshake
- Fetching 50 images on page = 50 handshakes

**Solutions:**

**1. Keep-alive (Q12):**
- Reuse connection — handshake once

**2. TCP Fast Open (TFO):**
- Second+ connections skip 1 RTT (data в SYN)
- RFC 7413; require client+server support + cookie
- Limited adoption (middleboxes break)

**3. QUIC (HTTP/3):**
- 0-RTT on reconnect (see Q14)

**4. Connection pooling:**
- Server-side: reuse outbound connections

**TLS compounds:**
- TCP handshake (1 RTT) + TLS handshake (1-2 RTT) = 2-3 RTT before data
- Cross-region: 200-300ms empty time — noticeable for small queries

**SO_REUSEPORT:** load balance incoming connections across multiple processes; improves accept throughput.

## Q6. Nagle algorithm и delayed ACK?

**Nagle's algorithm (1984):**
- Buffer small writes until ACK received or full segment
- Reduces packet overhead для chatty apps
- Enabled by default (TCP_NODELAY=0)

**Delayed ACK (Linux default):**
- Don't ACK immediately; wait ~200ms in case more data to ACK together
- Reduces ACK spam

**Nagle + delayed ACK = deadlock-ish:**
- Small write → Nagle holds (waits ACK)
- Receiver → delayed ACK (waits more data)
- 200ms pause для no reason

**Fix:** disable Nagle for latency-sensitive protocols.

```c
int flag = 1;
setsockopt(fd, IPPROTO_TCP, TCP_NODELAY, &flag, sizeof(int));
```

**In Java:**
```java
socket.setTcpNoDelay(true);
```

**When to enable / disable:**
- Disable Nagle (TCP_NODELAY=true): interactive (SSH, games, RPC); small latency-sensitive writes
- Leave Nagle (default): bulk transfer (file upload) — saves bandwidth

**Most RPC frameworks (gRPC, Netty default) disable Nagle.**

**HTTP clients:** usually disable Nagle для low-latency; bulk download OK either way.

## Q7. BBR vs CUBIC congestion control?

**CUBIC (Linux default since 2.6.19):**
- Loss-based: slow down on packet drop
- Cubic function growth → aggressive then plateau
- Fair, well-tested
- Problems on bufferbloated links (fills buffers before loss → high latency)

**BBR (Bottleneck Bandwidth and RTT — Google 2016):**
- Model-based: estimate bandwidth + min RTT
- Send at bandwidth × (1 - buffer_fill)
- **Doesn't fill buffers** → avoids bufferbloat
- **Better throughput on lossy links** (cellular, WiFi)

**Enable BBR:**
```bash
sudo sysctl net.core.default_qdisc=fq
sudo sysctl net.ipv4.tcp_congestion_control=bbr
```

**BBR benefits:**
- YouTube, Google services — adoption at scale
- **Google reported 2700% throughput increase** on some paths (cellular)
- Lower queueing latency (better for video, gaming)

**Drawbacks:**
- BBRv1 — aggressive; can starve CUBIC (unfair в mixed)
- BBRv2 (2019+) — improved fairness
- Requires careful deployment

**When use:**
- High-bandwidth, lossy or bufferbloated networks
- Cellular, satellite
- Cross-region с jitter

**When not:**
- Low-bandwidth, low-loss local (CUBIC fine)
- Where fairness critical (mixed with CUBIC senders — plan)

**Check:**
```bash
sysctl net.ipv4.tcp_congestion_control
ss -ti  # shows per-connection congestion info
```

## Q8. (!) TLS handshake overhead?

**TLS 1.2 handshake:**
- 2 RTT (after TCP handshake)
- Total: 3 RTT before data

**TLS 1.3 (2018):**
- 1 RTT handshake
- Mandatory forward secrecy
- Better default cipher suites

**Cost breakdown:**
- TCP: 1 RTT
- TLS 1.2: 2 RTT → 3 RTT total
- TLS 1.3: 1 RTT → 2 RTT total

**Cross-region impact:**
- 100ms RTT
- TLS 1.2: 300ms dead time
- TLS 1.3: 200ms dead time

**CPU overhead:**
- Asymmetric crypto (RSA/ECDSA) expensive once per handshake
- Symmetric encryption after: modern CPUs with AES-NI — negligible (few % overhead)
- ECDSA < RSA for CPU
- Session tickets / IDs — skip asymmetric next time

**0-RTT (TLS 1.3):**
- Resumed connection → data в first packet
- 0 RTT before data (!)
- **Replay attack risk** — data can be replayed; only use for idempotent GETs

**Tuning:**
- **Session resumption** (tickets/IDs) — reuse prior session (no asymmetric)
- **OCSP stapling** — server provides OCSP response inline; avoids client OCSP fetch
- **Short certificate chain** — fewer bytes = fewer segments = faster

**Measure:**
```bash
curl -o /dev/null -s -w "%{time_connect} %{time_appconnect} %{time_starttransfer}\n" https://site.com
# time_connect — TCP handshake; time_appconnect — TLS handshake; time_starttransfer — first byte
```

## Q9. TLS session resumption, 0-RTT?

**TLS 1.2 methods:**

**Session ID:**
- Server stores state; client sends ID on reconnect
- Server lookups state → resume
- **Scale issue:** requires server-side state (sticky LB)

**Session Tickets (RFC 5077):**
- Server encrypts session state → gives client "ticket"
- Client sends ticket on reconnect
- Server decrypts → resumes
- **Stateless** — works behind any LB

**TLS 1.3 PSK (Pre-Shared Key):**
- Replaces both; uses "resumption key" from prior session

**0-RTT:**
- Client sends data on first packet (with ticket)
- Server accepts if ticket valid
- **Saves entire RTT**

**0-RTT risks:**
- **Replay attack:** attacker records, replays → same action executed
- Mitigation: only idempotent requests (GET); never 0-RTT для POST /payment
- Client controls: most browsers limit 0-RTT to GET

**Browser behavior:**
- Chrome, Safari support 0-RTT
- Nginx/Envoy/Apache — `ssl_early_data on`

**Measurement:**
- DevTools Network: "Resumed TLS connection: yes"
- Nginx log: `$ssl_session_reused`

**Practical impact:**
- Cross-region API calls: 50-100ms savings per request
- Can push latency under 100ms for static content globally

## Q10. (!) HTTP/1.1 vs HTTP/2 vs HTTP/3?

**HTTP/1.1 (1997):**
- Text-based
- **1 request at a time per connection** (pipelining barely supported)
- Keep-alive default
- Browsers open **6 parallel connections** per origin

**HTTP/2 (2015):**
- **Binary** framing
- **Multiplexing:** many requests on single connection
- **Header compression** (HPACK)
- Server Push (largely deprecated)
- Same TCP + TLS stack

**HTTP/3 (2022):**
- **QUIC** transport (UDP-based, not TCP)
- Built-in TLS 1.3
- **No head-of-line blocking** at transport (streams independent)
- **0-RTT connection resumption** (QUIC native)
- Migration between networks (laptop WiFi → mobile)

**Comparison:**

| Aspect | 1.1 | 2 | 3 |
|--------|-----|---|---|
| Protocol | TCP | TCP | UDP (QUIC) |
| Format | Text | Binary | Binary |
| Multiplexing | No | Yes | Yes |
| HoL blocking | Yes | Yes (at TCP) | No |
| Handshake | TCP+TLS (3 RTT) | TCP+TLS (3 RTT) | 1 RTT (or 0-RTT) |
| Header compression | No | HPACK | QPACK |
| Server Push | No | Yes (deprecated) | Yes |
| Mobility | No | No | Yes |

**When use:**
- HTTP/1.1: legacy; fallback
- HTTP/2: mainstream; most backends/clients support
- HTTP/3: mobile clients, latency-sensitive; Cloudflare, Google, Meta deploy broadly

**Gotchas:**
- HTTP/2 one connection → if connection drops, everything drops (vs 1.1 6 connections)
- UDP blocked на некоторых networks → HTTP/3 falls back to HTTP/2
- Server Push — browsers disabled by 2022 (complexity, marginal benefit)

## Q11. (!) Head-of-line blocking в HTTP/1.1 и HTTP/2?

**HTTP/1.1:**
- Request serialized: must finish response before next request on connection
- Pipelining (theoretical: queue multiple requests) — broken в practice
- **Workaround:** 6 parallel connections per origin

**HTTP/2:**
- Multiplexing: many streams one connection
- **Application layer HoL solved**
- BUT: **TCP layer HoL** — packet loss on stream A blocks stream B (both on same TCP connection)
- If 5% packet loss, HTTP/2 **slower** than 1.1 (которая had 6 independent TCP)

**HTTP/3 (QUIC):**
- Streams independent at transport layer
- Lost packet on stream A does NOT block stream B
- **Truly HoL-free**

**When HoL matters:**
- Lossy networks (mobile, congested WiFi)
- Many small resources (page with 100 images)

**When doesn't:**
- Low loss network (DC, good fiber)
- Single large request (streaming video)

**Solutions:**
- HTTP/2 works fine if packet loss < 2%
- HTTP/3 advantageous где loss > 2%
- Fallback to HTTP/2 если UDP blocked

**Practical:**
- Google: HTTP/3 improves search/YouTube mobile experience measurably
- Cloudflare: default HTTP/3 where supported

## Q12. (!) Keep-alive и connection reuse?

**Keep-alive:** после response, connection stays open для next request.

**Default behavior:**
- HTTP/1.1: keep-alive default (`Connection: keep-alive`)
- HTTP/2: single persistent connection (multiplexed)
- HTTP/3: single persistent QUIC connection

**Why matters:**
- Avoid TCP + TLS handshakes (2-3 RTT per request)
- TCP cwnd grown (no slow start)
- Faster subsequent requests

**Example benefit:**
- 100ms RTT, 10 requests
- New connection each: 10 × (TCP + TLS + req) = 10 × 200ms+ = 2000ms
- Reused: 10 × 100ms = 1000ms → **2x faster**

**Headers:**
- `Connection: keep-alive` — HTTP/1.1 default
- `Keep-Alive: timeout=60, max=100` — timeout + max requests
- `Connection: close` — opt out

**Client (HTTP client library) settings:**
```java
// Apache HttpClient
HttpClientBuilder.create()
    .setConnectionManagerShared(true)
    .setMaxConnTotal(200)
    .setMaxConnPerRoute(50)
    .setConnectionTimeToLive(60, TimeUnit.SECONDS)
    .build();
```

**Server-side:**
- Nginx `keepalive_timeout 65s;`
- Tune based on traffic pattern
- Higher = more efficient; but consumes file descriptors / sockets

**Monitoring:**
- Connection pool exhausted → latency rise
- Monitor: `connections_active`, `connections_reused`

**Anti-pattern:** creating new `HttpClient` per request (forgot pooling) → each = new TCP + TLS. Senior gotcha в Java.

## Q13. HTTP/2 server push (и почему deprecated)?

**Server Push (HTTP/2):**
- Server proactively sends resources без client request
- Idea: скажем page needs `style.css` → push along with HTML

**Theory:** saves RTT (client doesn't request explicitly).

**Reality problems:**
- **Over-pushing:** browser already had resource in cache → wasted bandwidth
- **Cache awareness:** server doesn't know client cache state
- **Complexity:** requires careful pair tuning (what to push, when)
- **Priority issues:** pushed stream might compete with needed stream
- **Observed:** minor/negative gains в real websites

**Chrome removed server push** (2022).

**Replacements:**
- **Early Hints (HTTP 103):** send preload hints before 200 response
  ```
  103 Early Hints
  Link: </style.css>; rel=preload
  ```
- **HTML `<link rel=preload>`:** client-side preload directive (client decides cache)

**103 Early Hints:**
- Widely supported now (Chrome, Fastly, Cloudflare, Vercel)
- Simpler than HTTP/2 push; works with HTTP/2 and HTTP/3
- Cloudflare reports 20-30% faster LCP

**Verdict:**
- Don't rely на HTTP/2 Push (deprecated)
- Use Early Hints or link preload

## Q14. QUIC — почему быстрее TCP?

**QUIC (Quick UDP Internet Connections) — HTTP/3 transport.**

**Преимущества:**

**1. 1-RTT handshake (vs 3-RTT TCP+TLS):**
- Combines transport + TLS в one
- 0-RTT on resumption (данные в first packet)

**2. No TCP HoL blocking:**
- Streams independent at transport
- Lost packet на stream A не блокирует stream B
- Huge on lossy networks (mobile)

**3. Connection migration:**
- Connection ID vs IP/port
- Laptop роуминг WiFi → mobile — same QUIC connection
- TCP would break (new IP = new connection)

**4. Better congestion control:**
- BBR, CUBIC — userland implementations (easy to iterate)
- New algorithms faster to deploy

**5. Encryption required:**
- Every packet encrypted (payload + headers частично)
- Middleboxes can't interfere (ossification avoided)

**6. Packet loss recovery faster:**
- Packet numbers monotonic (TCP sequence numbers reused across retransmits → ambiguity)
- Better RTT estimation

**Challenges:**
- UDP blocked on some networks (usually 0.5-2%) → fallback to HTTP/2
- Higher CPU (userland crypto, per-packet processing) — improving с hardware offload
- Immature tooling (vs TCP)

**Adoption (2024):**
- 28% of web traffic (Cloudflare stats)
- Gmail, YouTube, Meta, Instagram, Spotify
- Default enable most major CDNs

**Client support:**
- Chrome, Firefox, Safari — enabled
- curl, browsers — standard
- Mobile apps — library-dependent

## Q15. (!) DNS lookup как latency source?

**DNS lookup:** resolve `api.example.com` → IP before connecting.

**Overhead:**
- Cache hit: 0 ms (local / OS cache)
- Cache miss: 20-200 ms (query recursive resolver)

**Cold lookup path:**
- Browser → OS DNS client
- OS → configured resolver (1.1.1.1, 8.8.8.8, ISP)
- Resolver → root → TLD → authoritative
- 3-4 queries potentially

**TTL impact:**
- Record TTL = 300s → cached 5 min
- Short TTL (30s) → frequent lookups
- Long TTL (1h+) → less agile для failover

**Mitigations:**

**1. DNS prefetch (HTML hint):**
```html
<link rel="dns-prefetch" href="//api.example.com">
```
Browser resolves early; saves RTT at actual request time.

**2. Preconnect (even better):**
```html
<link rel="preconnect" href="//api.example.com">
```
DNS + TCP + TLS done upfront.

**3. DNS over HTTPS (DoH) / DNS over TLS (DoT):**
- Encrypt DNS (privacy)
- May add latency (TLS handshake с resolver); use proxies near user
- Cloudflare 1.1.1.1 fast

**4. Application-level DNS caching:**
- Many HTTP clients cache resolved IPs
- Watch для stale cache на failover (tune TTL honor)

**5. Anycast DNS:**
- Resolvers geographically distributed; closest POP handles
- Cloudflare DNS, Google DNS

**Gotcha (Java):**
- Old JVMs cached DNS forever — missed failovers!
- `networkaddress.cache.ttl` — set non-`-1` (e.g., 30s)
- Modern JVMs: 30s default

**Monitor:** end-to-end latency breakdown should include DNS time; tools like `curl -w`:
```bash
curl -o /dev/null -s -w "dns:%{time_namelookup} connect:%{time_connect} start:%{time_starttransfer}\n" https://api.site.com
```

## Q16. (!) gzip vs brotli vs zstd?

**Compression для HTTP responses:**

**gzip:**
- Ubiquitous (since 1990s)
- ~5-10% savings vs uncompressed plaintext (80% JSON)
- Fast compression + decompression
- CPU cost low
- Default quality 6 (of 9)

**brotli (Google 2013):**
- Better ratio than gzip (~15-25% smaller)
- Slower compression (higher quality levels)
- Fast decompression
- Browser support: all modern (widely enabled on CDNs)
- Quality levels 0-11; typical 4-6 for dynamic, 11 для static (pre-compressed)

**zstd (Facebook 2016):**
- Fast + good ratio (often beats gzip, close to brotli)
- **Not standardized for HTTP `Content-Encoding`** widely (2024: RFC 8878 exists, но adoption limited)
- Used inside apps (Kafka compression, filesystems)

**Content-Encoding negotiation:**
- Client: `Accept-Encoding: gzip, br, zstd`
- Server picks best supported

**Best practice:**
- Static assets: pre-compress with brotli level 11 + gzip level 9 → serve precomputed
- Dynamic: brotli level 4-6 (balance CPU vs ratio)
- Minimum size threshold (< 1KB — not worth compressing)
- Don't compress already-compressed (JPEG, PNG, MP4) — CPU for zero win

**Exceptions — BREACH attack:**
- Compression + HTTPS + secrets in response + user-controlled input = attacker can extract secrets via size changes
- **Mitigation:** don't compress sensitive responses; или ensure no user input in sensitive-containing response

**CDN handling:**
- Often served pre-compressed (pay cost once at deploy)
- Some CDNs auto-compress origin response

**Measure:** `Content-Length` vs raw size = compression ratio.

## Q17. Content-Encoding negotiation?

**Client indicates support:**
```
Accept-Encoding: gzip, deflate, br, zstd
```

With quality values:
```
Accept-Encoding: gzip;q=0.5, br;q=1.0
```

**Server picks one:**
```
Content-Encoding: br
```

**Both request и response can be compressed:**
- Request: `Content-Encoding: gzip` (rare, POSTing compressed data)
- Response: typical

**Identity:**
- `Accept-Encoding: identity` — ask explicitly uncompressed
- Defaults include identity unless excluded

**Gotchas:**

**`Vary: Accept-Encoding`** required for CDN:
- Without it, CDN may serve brotli response to gzip-only client → broken
- With it, CDN caches per encoding variant

**Middleboxes:**
- Legacy proxies strip `Accept-Encoding` → server sees no support
- HTTPS prevents this (encrypted)

**`Content-Length`:**
- Reflects compressed size
- Must be set correctly

**Brotli limitations:**
- HTTPS only (browsers won't accept brotli over HTTP)

**Tools test:**
```bash
curl -H "Accept-Encoding: br, gzip" -I https://site.com/page
# check Content-Encoding header
```

## Q18. (!) Зачем CDN — latency math?

**Без CDN:**
- Origin in us-east-1
- User в Tokyo
- RTT ~130ms
- Each asset request: minimum 130ms + transfer

**С CDN:**
- Edge в Tokyo (~5ms from user)
- First request: user → Tokyo edge (miss) → origin → response (140ms)
- Subsequent: user → Tokyo edge (hit) → response (5-10ms)

**For typical webpage (50 requests):**
- Without CDN: 130ms × some sequential chain = 3-5 seconds
- With CDN (95% cached): mostly 5-10ms → 0.5-1 second

**Benefits beyond latency:**

**1. Origin offload:**
- 95% cache hit → 95% traffic doesn't touch origin
- Lower bandwidth bill, smaller origin cluster

**2. DDoS absorption:**
- CDN has global capacity; attacks spread

**3. TLS termination at edge:**
- TLS handshake near user (few ms) vs origin (150ms)
- Huge latency win even для uncacheable content

**4. Edge compute (Workers, Lambda@Edge):**
- Logic near user → faster responses for dynamic

**5. Resilience:**
- Origin down → CDN serves stale
- Multi-region origin failover

**Cost analysis:**
- CDN egress ~$0.03-0.08 per GB
- Origin egress (AWS us-east-1 out to internet): $0.09/GB
- CDN often **cheaper** than direct serving (and better UX)

**Not everything benefits:**
- Highly personalized (unique per user) — cache miss always
- Real-time (low TTL) — reduced benefit
- But edge TLS termination still helps

## Q19. (!) gRPC vs REST performance?

**gRPC:** RPC framework; protobuf + HTTP/2.

**REST:** JSON over HTTP (1.1 or 2).

**Performance differences:**

**1. Serialization:**
- Protobuf: smaller (30-50% less bytes), faster parse
- JSON: universal, human-readable

**2. Transport:**
- gRPC: HTTP/2 always — multiplexing, binary
- REST: often HTTP/1.1 или 2

**3. Streaming:**
- gRPC: bidirectional streaming native
- REST: SSE или WebSocket for streaming

**4. Connection pattern:**
- gRPC: long-lived HTTP/2 connections (pool 1-2)
- REST: many connections or HTTP/2 pool

**5. Language/platform:**
- gRPC: code gen from .proto → type-safe stubs
- REST: hand-written clients or generated from OpenAPI

**Benchmarks (rough):**
- gRPC often 2-5x faster per request at high throughput
- Smaller payloads + parse speed
- Same machine: gRPC ~10k req/s, REST-JSON ~3k req/s (Java)

**When REST better:**
- Public APIs (browsers can call directly)
- Simplicity, tooling (curl, Postman)
- Caching (HTTP semantics)

**When gRPC better:**
- Internal microservices
- High throughput
- Streaming
- Strong typing / code gen

**Drawbacks gRPC:**
- Browser support poor (needs gRPC-Web proxy)
- Harder debugging (binary format)
- Less tooling ecosystem
- Middleware / load balancer L7 features may be limited

**gRPC-Web:**
- Subset for browsers (unary + server streaming only)
- Needs Envoy proxy to translate

**REST → gRPC migration:**
- Measure first; often REST not actual bottleneck
- Internal hot paths — high ROI
- Public API — rarely worth breaking contract

## Q20. WebSockets — overhead и use cases?

**WebSocket (RFC 6455):**
- Full-duplex persistent connection over TCP
- Upgraded from HTTP/1.1 handshake
- Small framing overhead (~2-14 bytes per message)

**Use cases:**
- **Real-time bidirectional:** chat, collaborative editing, gaming
- **Server push:** notifications, stock tickers
- **Streaming updates:** dashboards, live sports

**Overhead:**
- Handshake: HTTP/1.1 request + Upgrade response (1 RTT)
- Persistent: no per-message handshake
- Per-frame: minimal

**Vs alternatives:**

**WebSocket vs SSE:**
- WebSocket: bidirectional
- SSE: server → client only (simpler)
- SSE over HTTP; friendlier с proxies, reconnect built-in

**WebSocket vs long polling:**
- Long polling: new request after each event — high overhead
- WebSocket: single connection, many events — much more efficient

**WebSocket vs gRPC streaming:**
- WebSocket: browser-friendly, simple protocol
- gRPC bidi streaming: typed, but needs gRPC-Web

**Challenges:**
- Stateful — sticky session needed (LB send user always to same WS server)
- Hard to scale horizontally (connection affinity)
- Proxies/firewalls может drop idle WS → ping/keepalive needed
- No HTTP caching (it's not HTTP semantically after upgrade)

**Scaling:**
- Redis pub/sub для broadcasting across server instances
- Centralized connection broker (EMQX, Centrifugo, MQTT)

**Performance:**
- Modern servers handle 100K+ concurrent WS per instance (Netty, Node.js, Go)
- Memory per connection key; tune socket buffers

## Q21. Server-Sent Events (SSE)?

**SSE:** unidirectional server→client over HTTP.

**Format:**
```
Content-Type: text/event-stream

event: update
data: {"price": 100}

event: heartbeat
data: ok
```

**Client (browser):**
```js
const es = new EventSource('/events');
es.onmessage = (e) => console.log(e.data);
```

**Vs WebSocket:**

| Aspect | SSE | WebSocket |
|--------|-----|-----------|
| Direction | Server → Client | Bidirectional |
| Protocol | HTTP | WebSocket (post-upgrade) |
| Reconnect | Automatic | Manual |
| Event IDs | Native (Last-Event-ID) | Manual |
| Binary | Text only | Text + binary |
| Proxy friendly | Yes (just HTTP) | Can be dropped |
| Complexity | Simpler | More flexible |

**When SSE:**
- One-way updates (dashboards, notifications, log streaming)
- Need HTTP semantics (headers, auth, proxies)
- Simpler deployment

**When WebSocket:**
- True bidirectional (chat, games)
- Binary data
- Lower overhead per message (marginal though)

**HTTP/2 + SSE:**
- Multiplexing means many SSE streams share one connection → scales better
- HTTP/1.1 blocks one stream per connection

**HTTP/3 + SSE:**
- No HoL blocking → even better

**Common mistake:** assume "WebSocket always better." SSE often fits perfectly, simpler code.

## Q22. (!) Tail latency — причины и борьба?

**Tail latency:** p99, p99.9 latencies — worst few percent of requests.

**Why matters:**
- Single page = 50 backend calls (fan-out)
- If each has p99 = 100ms, p50 = 20ms
- P50 of page = somewhere in middle but **p99 of page ≈ 100ms** almost always
- **Slowest component dominates**

**Причины:**

**1. GC pauses:**
- Old-gen GC → 100ms+ pauses
- Fix: tune (G1, ZGC, Shenandoah); reduce allocation

**2. Queueing:**
- Thread pool queue backs up → some requests wait
- Little's law: L = λ × W

**3. Networking:**
- Packet loss + retransmit (~300ms)
- TCP congestion backoff

**4. Disk I/O:**
- DB spill, log write fsync spike

**5. CPU contention:**
- Noisy neighbor (co-hosted VM)
- Background task eating cores

**6. Bufferbloat:**
- Network buffers deep → queuing latency

**7. Cache miss:**
- L1/L2 cache miss → DB fetch (10-100x slower)

**Fixes:**

**Hedged requests:**
- Send to 2 replicas; use faster
- Google Spanner, MapReduce patterns
- Wastes ~5-10% resources; cuts p99 значительно

**Tied requests:**
- Cancel slow replica когда fast one returns
- Less wasted work

**Circuit breaking:**
- Fail fast on bad backend; don't add to tail

**Load shedding:**
- Reject requests when near saturation
- Avoid slowness cascading

**Monitoring:**
- Track p50, p90, p95, p99, p99.9
- **Don't just track p50!**
- Alerts on p99 rises

## Q23. (!) Linux sysctl tuning для high-throughput?

**Network stack tuning:**

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

**File descriptor limits:**
```bash
# /etc/security/limits.conf
* soft nofile 1000000
* hard nofile 1000000
```

**Connection tracking (if using netfilter/iptables):**
```
net.netfilter.nf_conntrack_max = 1000000
```

**Don't blindly copy:** measure before/after. Some settings only help specific workloads.

**`tcp_tw_recycle`:** **removed в Linux 4.12+** — never use (NAT-breaker).

**Monitor:**
- `ss -s` — socket summary
- `netstat -s` — stats (retransmits, drops)
- `nstat` — network stats

**Application-level:**
- HTTP server workers / event loop count matching CPU
- Thread pool sizing
- Connection pool limits

## Q24. Debugging slow networks (tools)?

**Latency and throughput:**
- `ping <host>` — RTT, packet loss
- `traceroute` / `mtr` — per-hop latency
- `iperf3` — bandwidth test
- `tcpdump` / `wireshark` — packet capture

**Connection state:**
- `ss -tn` — TCP connections
- `ss -ti` — with congestion info (cwnd, rtt, retransmits)
- `ss -ltn` — listening sockets

**Stats:**
- `netstat -s` / `nstat` — cumulative counters
  - `tcpExtTCPRcvCoalesce`, `tcpExtTCPRetransFail` — look for anomalies
- `ip -s link` — interface stats (drops, errors)

**HTTP specific:**
```bash
curl -o /dev/null -s -w "dns:%{time_namelookup} conn:%{time_connect} tls:%{time_appconnect} first:%{time_starttransfer} total:%{time_total}\n" https://site.com
```

**DNS:**
- `dig example.com` — DNS query
- `dig +trace` — full trace

**Low-level:**
- `bpftrace` / `eBPF` — kernel-level tracing
- `tcpdump -i eth0 port 443 -w capture.pcap` → Wireshark

**Service mesh:**
- Envoy / Istio: access log `%RESPONSE_FLAGS%` — upstream timeouts etc.

**Synthetic monitoring:**
- Pingdom, Datadog Synthetics — continuous checks from global locations

**Workflow для "service X slow":**
1. Is DNS resolving fast? (`dig` time)
2. Is TCP handshake fast? (`curl -w time_connect`)
3. Is TLS handshake fast? (`curl -w time_appconnect`)
4. Is server responding fast? (`curl -w time_starttransfer`)
5. Is payload transfer slow? (bandwidth issue?)

**Each stage isolates different culprit.**

---

## See also

- [[database-performance-interview|Database Performance]] — client-server network matters
- [[caching-performance-interview|Caching Performance]] — Redis RTT, CDN
- [[jvm-performance-tuning-interview|JVM Performance Tuning]] — GC causes tail latency
- [[application-profiling-interview|Application Profiling]] — measure time in network calls
- [[load-balancing-interview|Load Balancing]] — network path impacts
- [[api-gateway-interview|API Gateway]] — edge network layer
- [[http-rest-interview|HTTP/REST]] — protocol details
- [[grpc-interview|gRPC]] — HTTP/2, streaming
- [[resilience-patterns-interview|Resilience Patterns]] — timeouts, retries
- [[observability-interview|Observability]] — network metrics и tracing

- [[application-profiling-interview|Application Profiling]]
- [[caching-performance-interview|Caching Performance]]
- [[database-performance-interview|Database Performance]]
- [[jvm-performance-tuning-interview|JVM Performance Tuning]]
- [[memory-management-interview|Memory Management]]
- [[performance-testing-interview|Performance Testing]]
