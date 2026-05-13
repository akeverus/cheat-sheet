---
title: "Вопросы на собеседовании: Edge Computing"
description: "Edge computing: Cloudflare Workers, Lambda@Edge, CDN edge functions, global latency, use cases, architecture, cold starts, state management"
tags:
  - interview
  - architecture
  - edge-computing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Edge Computing"
  - "Edge Computing interview"
  - "Edge functions"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Edge Computing`

`Edge Computing` — run logic **близко к users** (CDN edge locations, IoT gateways). Cloudflare Workers, AWS Lambda@Edge, Fastly Compute@Edge: latency 10-50ms (vs origin 100-300ms), global scale, serverless. Growing importance: API gateway, personalization, A/B testing, image optimization, auth at edge.

## Полезные ссылки

- [Cloudflare Workers docs](https://developers.cloudflare.com/workers/)
- [AWS Lambda@Edge](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/lambda-at-the-edge.html)
- [Fastly Compute@Edge](https://www.fastly.com/products/edge-compute)
- [Vercel Edge Functions](https://vercel.com/docs/functions/edge-functions)
- [Cloudflare Durable Objects](https://developers.cloudflare.com/durable-objects/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Edge Computing?](#q1--что-такое-edge-computing)
- [Q2. (!) Edge vs CDN vs серверlessless?](#q2--edge-vs-cdn-vs-серверlessless)
- [Q3. (!) Benefits и когда применять?](#q3--benefits-и-когда-применять)

**Platforms**
- [Q4. (!) Cloudflare Workers — как работает?](#q4--cloudflare-workers--как-работает)
- [Q5. (!) Lambda@Edge vs CloudFront Functions?](#q5--lambdaedge-vs-cloudfront-functions)
- [Q6. V8 isolates vs containers?](#q6-v8-isolates-vs-containers)

**Use cases**
- [Q7. (!) Typical use cases?](#q7--typical-use-cases)
- [Q8. Auth/JWT validation at edge?](#q8-authjwt-validation-at-edge)
- [Q9. Image optimization, resize?](#q9-image-optimization-resize)
- [Q10. A/B testing, personalization?](#q10-ab-testing-personalization)

**State и data**
- [Q11. (!) Как handle state at edge?](#q11--как-handle-state-at-edge)
- [Q12. Cloudflare Durable Objects?](#q12-cloudflare-durable-objects)
- [Q13. KV stores (Workers KV, DynamoDB Global)?](#q13-kv-stores-workers-kv-dynamodb-global)

**Ограничения**
- [Q14. (!) Ограничения edge runtime?](#q14--ограничения-edge-runtime)
- [Q15. Cold starts?](#q15-cold-starts)
- [Q16. Compute cost vs traditional?](#q16-compute-cost-vs-traditional)

**Production**
- [Q17. (!) Debugging и observability?](#q17--debugging-и-observability)
- [Q18. Deployment strategies?](#q18-deployment-strategies)

## Q1. (!) Что такое Edge Computing?

**Edge Computing** — run application logic **at edge of network** (close to users), not central DC/cloud.

**Origin (traditional):**
- Centralized: 1-few regions
- User → 150ms RTT globally

**Edge:**
- Distributed: 100-300+ points of presence (POPs)
- User → 10-50ms RTT from any location

**Types:**
1. **CDN edge functions** (Cloudflare Workers, Lambda@Edge)
2. **IoT edge** (device-level processing, AWS Greengrass)
3. **Telco edge / MEC** (5G base station compute)

**Focus here:** CDN edge functions — most relevant для backend interviews.

**Architecture:**
```
User → CDN Edge (runs edge function) → optionally → Origin
         ↓ 10ms                             ↓ 150ms
```

**Key point:** edge function может answer без going к origin. Full roundtrip avoided.


> [!mcq]
> - [ ] Edge Computing — это про edge нод в IoT (sensors/gateways), не имеет отношения к CDN | ❌ ПОСЛЕДСТВИЕ: IoT edge — один из типов, но в контексте backend-собеседований Edge Computing = CDN edge functions (Cloudflare Workers, Lambda@Edge); IoT — отдельная область
> - [ ] Edge functions выполняются ПОСЛЕ origin response (post-processing) | ❌ ПОСЛЕДСТВИЕ: главная идея — отвечать на запрос БЕЗ обращения к origin (full roundtrip avoided); функция может выполняться и до request, и до response, но цель — снять нагрузку с origin
> - [ ] Edge всегда быстрее origin потому что использует HTTP/3 | ❌ ПОСЛЕДСТВИЕ: latency edge'а ниже из-за физической близости к user (10-50ms vs 100-300ms), а не из-за протокола; HTTP/3 ортогонален
> - [x] Edge Computing — запуск application logic близко к users (CDN POPs, 100-300+ локаций); 10-50ms RTT vs origin 150ms; edge function может ответить без обращения к origin → меньше latency и нагрузка | ✓ ПРИМЕНЯТЬ: для glob-distributed apps с low-latency требованиями; auth/personalization/routing at edge 📋 ПРАВИЛО: edge = compute близко к user, не центральный DC 🔗 См. Q2

## Q2. (!) Edge vs CDN vs серверlessless?

**CDN:** static content caching at edge.
- Stored objects (images, CSS, JS)
- No logic execution

**Edge compute:** CDN + code execution at edge.
- Modify request/response
- Dynamic personalization
- Auth, routing decisions

**Serverless (Lambda, Cloud Functions):**
- Centralized regions (us-east-1 etc.)
- Full runtime (containers)
- Longer cold starts, less distributed

**Edge serverless (Workers, Lambda@Edge):**
- Serverless + at edge
- V8 isolates (Workers) — microsecond startup
- Full platform: KV store, crons, durable objects

**Comparison:**

| Aspect | CDN | Lambda | Edge Worker |
|--------|-----|--------|-------------|
| Location | Edge | Region | Edge |
| Logic | No | Full | Limited |
| Startup | — | 100s ms | ~0 ms |
| Runtime | — | Full OS | JS/Wasm only |
| State | Static | External | KV / DO |


> [!mcq]
> - [ ] CDN и Edge Worker — синонимы | ❌ ПОСЛЕДСТВИЕ: CDN — только кэширование статических объектов; Edge Worker — выполнение кода (logic, modify request/response); CDN не запускает функции
> - [ ] Lambda работает на edge и имеет microsecond startup | ❌ ПОСЛЕДСТВИЕ: AWS Lambda — централизована в regions (us-east-1...); cold start 100ms-1s; для edge — Lambda@Edge или CloudFront Functions; Workers — V8 isolates ~0ms
> - [ ] Edge Workers имеют полный Node.js runtime с поддержкой всех npm-пакетов | ❌ ПОСЛЕДСТВИЕ: Cloudflare Workers — V8 isolate (subset Web APIs + Fetch); нет fs/net Node modules; ограниченное API; native modules не работают
> - [x] CDN: статический cache, без logic. Edge compute: CDN + code (modify req/res, auth, personalization). Lambda (centralized): полный runtime, container, longer cold start. Edge serverless (Workers): V8 isolates ~0ms startup, JS/Wasm only, ограниченный runtime | ✓ ПРИМЕНЯТЬ: статика → CDN; lightweight logic at edge → Workers; heavy compute → Lambda regional 📋 ПРАВИЛО: edge = trade полнота runtime на низкую latency 🔗 См. Q3

## Q3. (!) Benefits и когда применять?

**Benefits:**

**1. Low latency:**
- 10-50ms vs 100-300ms
- Critical for perceived speed (LCP < 2.5s)

**2. Offload origin:**
- Edge handles logic → origin handles less traffic → cheaper, more stable

**3. Global scale instant:**
- Deploy once; runs в 300 POPs
- No multi-region deployment complexity

**4. Cost (often):**
- Pay per invocation; no idle cost
- Cloudflare Workers: $5 / 10M requests

**5. DDoS absorption:**
- Cloudflare network absorbs 71 Tbps attacks
- Edge functions run post-mitigation

**When применять:**
- Static site personalization (inject user-specific data)
- Auth at edge (reject unauthorized before origin)
- A/B testing, feature flags
- Image/video transformations
- API proxying / aggregation (BFF at edge)
- IoT ingestion

**When NOT:**
- Heavy compute (ML inference at scale) — CPU limits
- Long-running (> 30s) — not supported
- Stateful applications с high consistency — hard


> [!mcq]
> - [ ] Edge подходит для ML inference и тяжёлых вычислений | ❌ ПОСЛЕДСТВИЕ: edge runtime имеет CPU limits (50ms-1s wall time, ~10MB RAM); тяжёлый compute и ML на edge — не подходит, нужен regional GPU
> - [ ] Edge заменяет origin полностью — не нужно бекенд-сервера | ❌ ПОСЛЕДСТВИЕ: edge handle часть запросов, но для DB-writes, complex business logic, large compute всё ещё нужен origin; edge — это слой ускорения, не замена
> - [ ] Главное преимущество edge — снижение compute cost | ❌ ПОСЛЕДСТВИЕ: главное — low latency и origin offload; cost иногда ниже (Workers $5/10M req), но не всегда; LCP/UX выигрыш важнее экономии
> - [x] Benefits: low latency (10-50ms vs 100-300ms), origin offload, global scale (deploy once → 300 POPs), pay-per-invocation, DDoS absorption. Применять: personalization, auth at edge, A/B testing, image optimization, API proxy/BFF. НЕ применять: heavy compute, long-running (>30s), strict-consistency stateful | ✓ ПРИМЕНЯТЬ: для read-heavy/personalization путей с latency-критичными UX-метриками (LCP, FID) 📋 ПРАВИЛО: edge для lightweight latency-sensitive logic, не для heavy compute 🔗 См. Q4

## Q4. (!) Cloudflare Workers — как работает?

**V8 isolate** — lightweight JavaScript sandbox (same as Chrome tabs).

**Architecture:**
- Worker code (JS/TS/Wasm)
- Deployed to all Cloudflare POPs (300+)
- First request to POP → load worker (~1ms)
- Subsequent requests → reuse isolate (microseconds)

**Example:**
```js
export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (url.pathname === '/api/hello') {
      return new Response(JSON.stringify({ msg: 'Hello from ' + request.cf.city }));
    }
    return fetch(request);  // passthrough к origin
  }
}
```

**Features:**
- Fetch API (web standard)
- KV store (eventually consistent key-value)
- Durable Objects (strongly consistent, stateful)
- Cron triggers
- D1 (SQLite at edge)
- R2 (S3-compatible storage, zero egress)
- Queues, AI

**Runtime limits:**
- CPU time: 10ms free / 30s paid
- Memory: 128MB
- Request size: 100MB

**Languages:** JS, TS, WASM (Rust, C, Go → wasm)


> [!mcq]
> - [ ] Cloudflare Workers — это Docker-контейнеры на CDN-нодах | ❌ ПОСЛЕДСТВИЕ: Workers — V8 isolates (как Chrome tabs), не Docker; именно поэтому startup ~0ms (microseconds), а Docker занимает 100ms+
> - [ ] Workers поддерживают только синхронный JavaScript без async/await | ❌ ПОСЛЕДСТВИЕ: full async/await поддерживается; основной API — Fetch с Promise; синхронный код избегают (CPU time limit)
> - [ ] CPU limit Workers — несколько часов, можно запустить ML training | ❌ ПОСЛЕДСТВИЕ: CPU time 10ms free / 30s paid plan; для long-running compute — Lambda regional или dedicated GPU; edge — для short-lived tasks
> - [x] Worker = V8 isolate (lightweight JS sandbox, microsecond startup) deployed на все Cloudflare POPs (300+); first request loads isolate, subsequent reuse; Fetch API + KV + Durable Objects + Cron + D1/R2; JS/TS/Wasm | ✓ ПРИМЕНЯТЬ: для personalization/auth/routing; KV для eventually consistent state; Durable Objects для strongly consistent (per-key serialized actor) 📋 ПРАВИЛО: Workers = V8 isolate, не container; ~0ms startup на cost of runtime limitations 🔗 См. Q5

## Q5. (!) Lambda@Edge vs CloudFront Functions?

**CloudFront Functions:**
- Newer (2021)
- JavaScript only
- < 1 ms execution
- Viewer request/response only
- Simple use cases (header manipulation, redirects)
- Cheap ($0.10 / million)

**Lambda@Edge:**
- Older, more powerful
- Node.js, Python
- Up to 5s (viewer) / 30s (origin) execution
- Full Lambda capabilities (call services, longer)
- 4 trigger points:
  - Viewer request (before cache)
  - Origin request (after cache miss)
  - Origin response (before cache)
  - Viewer response (after cache)
- More expensive ($0.6 / million)

**Use cases:**
- CloudFront Functions: URL rewrite, header manipulation, simple auth
- Lambda@Edge: personalization, complex routing, image resize

**Cold start:** Lambda@Edge slower (50-200ms cold); Functions instant.

**Deployment propagation:**
- Lambda@Edge: 2-5 min worldwide
- CF Functions: faster


> [!mcq]
> - [ ] CloudFront Functions поддерживают вызов AWS-сервисов (DynamoDB, S3) | ❌ ПОСЛЕДСТВИЕ: CloudFront Functions — ограниченный JS, без AWS SDK; для вызовов сервисов нужен Lambda@Edge; CF Functions для simple header/URL manipulation
> - [ ] Lambda@Edge запускается только на viewer request | ❌ ПОСЛЕДСТВИЕ: 4 точки triggering — viewer request, origin request, origin response, viewer response; viewer = синхронно с клиентом (5s limit), origin = после/до cache (30s limit)
> - [ ] CloudFront Functions дороже Lambda@Edge | ❌ ПОСЛЕДСТВИЕ: наоборот — CF Functions $0.10/M, Lambda@Edge $0.60/M (~6x); CF Functions — для high-volume cheap operations
> - [x] CloudFront Functions: JS only, <1ms, viewer only, $0.10/M, для URL rewrite/headers/simple auth. Lambda@Edge: Node/Python, до 5s viewer / 30s origin, 4 triggers, $0.60/M, для personalization/complex routing/image resize | ✓ ПРИМЕНЯТЬ: CF Functions для hot path operations (headers, rewrites); Lambda@Edge для logic требующего external calls 📋 ПРАВИЛО: CF Functions = cheap/simple/fast; Lambda@Edge = capable/expensive 🔗 См. Q6

## Q6. V8 isolates vs containers?

**V8 isolate (Workers):**
- Light sandbox inside V8 engine
- Memory-isolated, not process-isolated
- Multiple isolates share one V8 process
- Startup: microseconds
- Good for short, JS/WASM code

**Container (Lambda):**
- Full OS / runtime
- Process isolation, VM-level separation (Firecracker)
- Startup: 100ms-few seconds
- Supports any language / binary

**Trade-offs:**

| Aspect | Isolate | Container |
|--------|---------|-----------|
| Startup | μs | 100ms+ |
| Density | 1000s / machine | 10s |
| Security | Process-shared (V8) | VM-level |
| Languages | JS, WASM | Any |
| Runtime | Limited | Full |

**Security:** isolates rely on V8 correctness. V8 exploits → cross-tenant. Rare but exists.

**Modern trend:** hybrid — Workers for hot paths, Lambda для complex.


> [!mcq]
> - [ ] V8 isolates обеспечивают VM-level изоляцию как контейнеры | ❌ ПОСЛЕДSTVIE: isolates имеют memory-isolation внутри V8 process, НЕ VM-level (как Firecracker для Lambda); V8 exploit → cross-tenant breakout (редко, но возможно)
> - [ ] Container startup быстрее isolate потому что pre-warmed | ❌ ПОСЛЕДСТВИЕ: V8 isolate startup ~microseconds (JS context init); container startup 100ms+ (OS + runtime init); container даже с pre-warming на порядки медленнее
> - [ ] Isolates поддерживают любой язык (Python, Go, Java) | ❌ ПОСЛЕДСТВИЕ: V8 = JavaScript engine; поддерживает JS, TypeScript, WebAssembly (Rust/C/Go → wasm); Python/Java/Go binaries не работают
> - [x] V8 isolate: memory-isolated sandbox внутри V8, μs startup, 1000s isolates/machine, JS/Wasm only, process-shared security. Container: VM-level (Firecracker), 100ms+ startup, 10s/machine, любой язык, full runtime; trade-off скорость vs полнота | ✓ ПРИМЕНЯТЬ: hot paths (auth, rewrite) → isolates; complex with native libs → containers; гибрид Workers+Lambda — типовой паттерн 📋 ПРАВИЛО: isolate trade-off — startup vs language flexibility и security boundary 🔗 См. Q7

## Q7. (!) Typical use cases?

**1. Auth / JWT validation:**
- Reject unauthorized requests at edge
- Save origin load (not waste compute on 401s)

**2. URL rewriting / routing:**
- A/B test: 50% users → v2 backend
- Geographic routing: EU users → EU origin

**3. Personalization:**
- Inject user-specific snippets into HTML
- Cached base page + dynamic injection at edge

**4. Image optimization:**
- Resize on fly based on device/screen
- WebP/AVIF conversion
- Cloudflare Image Resizing, AWS Lambda@Edge

**5. Bot detection / WAF:**
- Analyze request patterns
- Block bots without origin hit

**6. API gateway / BFF at edge:**
- Aggregate multiple backends
- Transform responses for client
- See [BFF pattern](bff-pattern-interview.md)

**7. Geo-blocking / compliance:**
- Block specific countries
- Serve GDPR-consent banners only в EU

**8. Caching с custom logic:**
- Cache key customization
- Stale-while-revalidate
- Edge purge on demand

**9. Real-time features:**
- WebSocket termination at edge
- Pub/sub (Cloudflare Realtime)

**10. AI inference:**
- Lightweight models at edge (Cloudflare AI, Vercel AI)
- Low-latency completions


> [!mcq]
> - [ ] Edge подходит для batch ML training с GPU | ❌ ПОСЛЕДСТВИЕ: edge — для inference lightweight моделей (Cloudflare AI, Vercel AI); training требует GPU, длительных runs, dedicated infra — не edge
> - [ ] OAuth-flow с full token issuance — типичный edge use case | ❌ ПОСЛЕДСТВИЕ: edge validate JWT (verify signature), не issues tokens; token issuance требует доступ к user DB и identity provider — это origin/IdP, не edge
> - [ ] Image resize не подходит для edge — слишком CPU-тяжело | ❌ ПОСЛЕДСТВИЕ: image resize — классический edge use case (Cloudflare Image Resizing, AWS Lambda@Edge); CPU limit ~50ms для simple resize/format conversion укладывается
> - [x] Use cases: JWT auth at edge, URL rewriting/routing/A-B test, personalization (HTML injection), image optimization (resize/WebP), bot detection/WAF, API gateway/BFF, geo-blocking/GDPR, custom caching, WebSocket termination, lightweight AI inference | ✓ ПРИМЕНЯТЬ: для read-heavy/static-base + dynamic-injection workloads; auth-rejection ДО origin экономит compute 📋 ПРАВИЛО: edge use case = lightweight stateless logic перед heavy origin 🔗 См. Q8

## Q8. Auth/JWT validation at edge?

**Flow:**
```
Request → Edge Worker
  ├─ No token → 401
  ├─ Invalid token → 401
  ├─ Valid → forward to origin with verified claims
```

**Code (Cloudflare Workers):**
```js
export default {
  async fetch(request, env) {
    const token = request.headers.get('Authorization')?.replace('Bearer ', '');
    if (!token) return new Response('Unauthorized', { status: 401 });
    
    try {
      const payload = await verifyJWT(token, env.JWT_SECRET);
      const headers = new Headers(request.headers);
      headers.set('X-User-ID', payload.sub);
      return fetch(new Request(request.url, { method: request.method, headers, body: request.body }));
    } catch (e) {
      return new Response('Invalid token', { status: 401 });
    }
  }
};
```

**Benefits:**
- Origin always receives pre-authenticated requests
- Attack traffic doesn't hit origin
- Simplifies backend (trust header from edge)

**Pitfalls:**
- Secret management (distribute к edge safely) — use Cloudflare Secrets
- Token revocation hard (cache per edge) — short TTLs
- Key rotation coordination


> [!mcq]
> - [ ] Edge can revoke individual tokens мгновенно (revocation list) | ❌ ПОСЛЕДСТВИЕ: revocation hard на edge — нужно либо short TTL JWT (1-5min), либо distribute revocation list ко всем POPs (eventually consistent KV); мгновенная revocation требует origin lookup
> - [ ] JWT-secret hardcoded в Workers коде | ❌ ПОСЛЕДСТВИЕ: secret в коде → утечка через source maps / git; использовать Cloudflare Secrets (encrypted env vars); rotation через wrangler без redeploy
> - [ ] При valid JWT edge просто пропускает request к origin без изменений | ❌ ПОСЛЕДСТВИЕ: правильный паттерн — set `X-User-ID`/claims headers и passthrough; origin trusts headers и не делает повторную validation (зато сохраняет audit-info)
> - [x] Flow: token из `Authorization` header → verify signature через `JWT_SECRET` из Cloudflare Secrets → set `X-User-ID`/claims в forward headers → fetch к origin; origin trust'ит pre-auth headers | ✓ ПРИМЕНЯТЬ: short-TTL access tokens (5-15min) + refresh tokens у origin; JWKS endpoint для public-key rotation 📋 ПРАВИЛО: edge auth = signature verify + claim forward; revocation/issuance — у origin 🔗 См. Q9

## Q9. Image optimization, resize?

**Requirement:** serve right size/format per device/connection.

**Without edge:** client downloads large file → wastes bandwidth, time.

**With edge:**
```
Request /image.jpg?w=500&format=webp
  ↓
Edge worker:
  - Fetch original from origin
  - Resize to 500px width
  - Convert to WebP
  - Cache result at edge
  - Return
```

**Tools:**
- Cloudflare Image Resizing (managed)
- AWS Lambda@Edge + Sharp
- Fastly Image Optimizer
- Vercel Image Optimization

**Benefits:**
- Bandwidth savings (10-50% smaller)
- Faster page loads
- Device-appropriate (retina vs 1x)

**Caching:**
- Cache per variant (URL includes params)
- CDN cache handles hit ratio

**Format negotiation:**
- `Accept: image/webp` → edge serves WebP
- `Accept: image/avif` → AVIF (even smaller)
- Fall back to JPEG for old browsers


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. A/B testing, personalization? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Problem:** A/B tests require dynamic content, but caching breaks that.

**Edge approach:**
1. Cache base HTML at edge
2. Edge function determines variant (user cookie, random)
3. Injects variant-specific content into response
4. Returns modified HTML

**Code:**
```js
export default {
  async fetch(request, env) {
    let variant = getCookie(request, 'ab_variant');
    if (!variant) {
      variant = Math.random() < 0.5 ? 'A' : 'B';
    }
    
    const origResp = await fetch(request);
    const html = await origResp.text();
    const modified = html.replace('__CTA__', variant === 'A' ? 'Buy Now' : 'Get Started');
    
    const resp = new Response(modified, origResp);
    resp.headers.append('Set-Cookie', `ab_variant=${variant}; Max-Age=86400`);
    return resp;
  }
};
```

**HTMLRewriter** (Cloudflare): DOM-like streaming API, more performant than replace.

**Feature flags:**
- Edge checks flag service (cached)
- Toggles features per-user / per-region

**Personalization without flash:**
- No client-side fetch needed (content ready on arrival)
- No FOUC (flash of unstyled content)


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. (!) Как handle state at edge? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Challenge:** edge nodes stateless by default; 300+ POPs.

**Options:**

**1. No state:**
- Everything through origin
- Simplest

**2. Cache (ephemeral):**
- Edge cache (CDN-level)
- TTL-based
- Multi-POP: each POP has own cache → redundant but fast

**3. KV store (eventually consistent):**
- Cloudflare Workers KV, DynamoDB global tables
- Write → eventually replicates to all POPs
- Read latency ~1ms
- Low write throughput (per-key limits)

**4. Durable Objects (strongly consistent):**
- Single instance per object, anywhere in network
- Pinning to specific POP
- Useful for coordination (chat room, counter)

**5. External DB:**
- Call Postgres, DynamoDB from worker
- Adds round-trip (edge→DB)

**Pick by use case:**
- User session tokens: KV (read-heavy, eventual OK)
- Chat room state: Durable Objects (strong consistency)
- Counter across all nodes: Durable Objects or Redis atomic


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Cloudflare Durable Objects? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Durable Object:** single-instance stateful service pinned к one POP.

**Model:**
- Named object (`getDurableObject("chat-room-123")`)
- One instance globally; all writes go there
- Persists в storage (key-value, transactional)
- WebSocket support

**Use cases:**
- Chat rooms (shared state)
- Real-time collaboration (Google Docs-like)
- Atomic counters (race-free)
- Coordination (leader election)

**Code:**
```js
export class ChatRoom {
  constructor(state, env) {
    this.state = state;
    this.sessions = [];
  }
  
  async fetch(request) {
    // WebSocket upgrade
    const [client, server] = Object.values(new WebSocketPair());
    this.sessions.push(server);
    server.accept();
    server.addEventListener('message', (e) => {
      for (const s of this.sessions) s.send(e.data);
    });
    return new Response(null, { status: 101, webSocket: client });
  }
}
```

**Trade-off:** DO pins к single POP → higher latency для users в other regions, but strong consistency.

**Pricing:** $0.20 per million requests.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. KV stores (Workers KV, DynamoDB Global)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Workers KV:**
- Eventually consistent key-value
- Reads: ~1ms (cached at edge)
- Writes: ~1 min to propagate globally
- Read-optimized (tolerate stale)
- 100 writes/sec per key limit

**Use cases:**
- Feature flags
- Config
- Static content
- Route mappings

**DynamoDB Global Tables:**
- Multi-region replication
- Eventually consistent cross-region
- Heavier API (slower than KV)
- Used from Lambda@Edge

**FaunaDB, PlanetScale:**
- Global SQL databases
- Strong consistency regional, eventual cross-region

**Choose by:**
- Consistency needed?
- Write frequency?
- Complexity (KV vs SQL)?


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. (!) Ограничения edge runtime? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Cloudflare Workers:**
- CPU: 10-30s (depending tier)
- Memory: 128MB
- No access to filesystem (beyond bundled assets)
- No native Node APIs (workerd — subset)
- Some npm packages unsupported

**Lambda@Edge:**
- CPU: 5-30s
- Memory: 128MB-10GB
- Viewer request/response: 5s, 1MB body

**Runtime features (Workers):**
- Fetch API ✅
- Web Streams ✅
- Crypto (SubtleCrypto) ✅
- TextEncoder/Decoder ✅
- No `fs`, no direct TCP (fetch only)
- No child processes

**Languages:**
- JS/TS primarily
- WASM (Rust, Go, C++) — but limited bindings
- Python (newer, via Pyodide in Workers)

**Deployment size:**
- Workers: 10MB compressed
- Lambda@Edge: 50MB

**Known limitations:**
- No long-polling / long-lived HTTP (30s max)
- No disk — must use KV/DO/R2
- Regional compliance (data residency)


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Cold starts? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Workers:**
- Essentially zero (V8 isolate = μs)
- First request to POP may have ~1-5ms load

**Lambda@Edge:**
- 50-500ms cold start
- Node.js warmer than Java/.NET
- Reduce: smaller package, Provisioned Concurrency

**Mitigation:**
- Keep packages small
- Share runtime (don't dynamically import large modules)
- Workers don't need this concern

**Warm instance:**
- After first request, POP keeps isolate/container warm ~5-15 min
- Frequent traffic = always warm


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Compute cost vs traditional? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Cloudflare Workers (2024):**
- Paid: $5/mo, 10M requests included
- Extra: $0.50 / million
- CPU: 30s max, 50ms median billable

**AWS Lambda@Edge:**
- $0.60 / million requests
- + $0.0000125128 / GB-second

**Comparison (100M requests/month):**
- Workers: ~$50
- Lambda@Edge: ~$60 + compute
- EC2 (always-on): depends on size ($100s-$1000s)

**Edge cheaper за:**
- Traffic that's 90%+ edge-cacheable
- Spiky workloads (scales to 0)
- Globally distributed (vs deploying multi-region EC2)

**Edge expensive для:**
- Heavy compute (CPU-bound)
- Long-running requests
- Stateful apps


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. (!) Debugging и observability? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Logs:**
- Cloudflare: `wrangler tail` live stream
- Lambda@Edge: CloudWatch (from region где executed — may be many!)

**Metrics:**
- Cloudflare dashboard: requests, errors, CPU time per worker
- CloudFront metrics: cache hit ratio, errors

**Tracing:**
- Harder than traditional — cross-region
- OpenTelemetry export to Honeycomb, Datadog
- Workers Trace Events API

**Local dev:**
- `wrangler dev` — runs locally с miniflare (workerd emulator)
- `sam local` for Lambda@Edge — limited

**Testing:**
- Unit: mock Fetch, KV
- Integration: deployed to staging worker

**Error handling:**
- Try/catch in worker; report to Sentry
- Return graceful error response (don't 5xx)


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. Deployment strategies? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Cloudflare Workers:**
- `wrangler deploy` — instant, global (5-30s propagation)
- Gradual rollout (canary) via % routing:
```
routes: [
  { pattern: "example.com/*", script: "worker-v2", percent: 10 }
]
```
- Rollback fast (redeploy previous version)

**Lambda@Edge:**
- Version pinning (1 version per distribution path)
- Propagation 2-5 min worldwide
- Rollback: update CloudFront к prior version

**CI/CD:**
- GitHub Actions → `wrangler deploy`
- Tests in preview environment first

**Environment variables / secrets:**
- Per-environment configs
- `wrangler secret put` encrypted storage

**A/B deploy (feature flag):**
- Same worker, branching on flag from KV
- Instant toggle без deploy

---

## See also

- [BFF Pattern](bff-pattern-interview.md) — API aggregation at edge
- [API Gateway](api-gateway-interview.md) — edge gateway
- [Caching Strategies](caching-strategies-interview.md) — CDN edge cache
- [Caching Performance](../performance/caching-performance-interview.md) — CDN hit ratio
- [Network Performance](../performance/network-performance-interview.md) — latency, CDN
- [Serverless](../cloud/serverless-interview.md) — Lambda, related model
- [Microservices](microservices-interview.md) — distribution patterns
- [Scalability Patterns](scalability-patterns-interview.md) — global scaling
- [Distributed Systems](distributed-systems-interview.md) — edge = distributed
- [Observability](../monitoring/observability-interview.md) — debug edge workloads


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [API Gateway](api-gateway-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [BFF Pattern](bff-pattern-interview.md)
- [Стратегии кэширования](caching-strategies-interview.md)
- [CAP-теорема](cap-theorem-interview.md)
- [Clean Architecture](clean-architecture-interview.md)
- [Паттерны согласованности](consistency-patterns-interview.md)
