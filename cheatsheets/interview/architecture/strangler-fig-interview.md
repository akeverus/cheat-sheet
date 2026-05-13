---
title: "Вопросы на собеседовании: Strangler Fig Pattern"
description: "Strangler Fig pattern для gradual monolith→microservices migration: proxy, routing, Martin Fowler, incremental, rollback, feature flags, real examples"
tags:
  - interview
  - architecture
  - strangler-fig-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Strangler Fig Pattern"
  - "Strangler Application"
  - "Monolith migration"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Strangler Fig Pattern`

`Strangler Fig` — pattern для **gradual migration** монолита → microservices (или legacy → modern). Вместо "big bang rewrite" — new functionality wraps old, progressively replacing. Name от Martin Fowler (2004), inspired by strangler fig trees. Industry standard для risk-managed modernization.

## Полезные ссылки

- [Martin Fowler — Strangler Fig Application](https://martinfowler.com/bliki/StranglerFigApplication.html)
- [Microsoft Azure — Strangler Fig pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/strangler-fig)
- [Monolith to Microservices book — Sam Newman](https://samnewman.io/books/monolith-to-microservices/)
- [AWS prescriptive guidance — Strangler](https://docs.aws.amazon.com/prescriptive-guidance/latest/modernization-aspnet-web-services/fifth-phase-strangler.html)
- [ThoughtWorks — Strangler application](https://www.thoughtworks.com/insights/blog/evolving-beyond-strangler)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Strangler Fig pattern?](#q1--что-такое-strangler-fig-pattern)
- [Q2. (!) Зачем Strangler вместо big bang rewrite?](#q2--зачем-strangler-вместо-big-bang-rewrite)
- [Q3. (!) Как работает механика?](#q3--как-работает-механика)

**Process**
- [Q4. (!) С чего начинать strangling?](#q4--с-чего-начинать-strangling)
- [Q5. (!) Какие функции выделять первыми?](#q5--какие-функции-выделять-первыми)
- [Q6. Data migration и shared DB?](#q6-data-migration-и-shared-db)
- [Q7. Dual-write и consistency?](#q7-dual-write-и-consistency)

**Proxy / Facade**
- [Q8. (!) Proxy / facade layer?](#q8--proxy--facade-layer)
- [Q9. Где жить proxy (gateway, reverse proxy)?](#q9-где-жить-proxy-gateway-reverse-proxy)
- [Q10. Feature flags для routing?](#q10-feature-flags-для-routing)

**Trade-offs**
- [Q11. (!) Преимущества?](#q11--преимущества)
- [Q12. (!) Недостатки и риски?](#q12--недостатки-и-риски)

**Production**
- [Q13. (!) Как rollback если не работает?](#q13--как-rollback-если-не-работает)
- [Q14. Testing parallel run (shadow)?](#q14-testing-parallel-run-shadow)
- [Q15. Как долго занимает migration?](#q15-как-долго-занимает-migration)
- [Q16. Когда считать migration complete?](#q16-когда-считать-migration-complete)

**Related patterns**
- [Q17. Anti-corruption layer?](#q17-anti-corruption-layer)
- [Q18. Branch by abstraction?](#q18-branch-by-abstraction)

## Q1. (!) Что такое Strangler Fig pattern?

**Strangler Fig Application** (Martin Fowler, 2004) — **incremental migration pattern**:
- New system **gradually replaces** old, piece by piece
- Old system **strangled** out over time
- No "big bang" switch

**Named** after strangler fig trees (in rainforest):
- Seeds germinate on host tree
- Grow around trunk
- Eventually original tree dies inside; fig remains

**Migration analog:**
- New services grow around legacy monolith
- One piece at a time replaced
- Legacy retires gradually

**End state:** legacy fully replaced; new system stands alone.

**Diagram (progression):**
```
Stage 1: [Monolith 100%]
Stage 2: [New Service A | Monolith 80%]
Stage 3: [Service A | Service B | Monolith 60%]
...
Stage N: [Service A | Service B | Service C | Service D]  (monolith gone)
```


> [!mcq]
> - [ ] Полный переписывание системы с нуля и единовременный cutover в production | ❌ ПОСЛЕДСТВИЕ: big bang rewrite с 50%+ fail rate, месяцы без value, moving target.
> - [ ] Wrap legacy в API без замены — facade навсегда, монолит не уходит | ❌ ПОСЛЕДСТВИЕ: получаем lipstick on a pig, технический долг растёт под красивым интерфейсом.
> - [x] Постепенная замена legacy кусок-за-куском через proxy/facade; новые сервисы растут вокруг монолита, старый код умирает по частям | ✓ ПРИМЕНЯТЬ: миграция legacy → microservices, рискованные модернизации. 📋 ПРАВИЛО: «душит как фикус — медленно, но насмерть». 🔗 См. Q2
> - [ ] Параллельная разработка двух систем с финальным голосованием за лучшую | ❌ ПОСЛЕДСТВИЕ: двойные затраты, нет реального progress, политические войны команд.

## Q2. (!) Зачем Strangler вместо big bang rewrite?

**Big bang rewrite problems:**

**1. Huge risk:**
- Months/years of work before any value
- Often never ships ("Netscape rewrite" cautionary tale)
- 50%+ fail rate для large rewrites

**2. Feature parity:**
- New must match all old features before switching
- Old continues evolving — moving target

**3. No incremental value:**
- Zero benefit until 100% done
- Can't learn, can't adjust

**4. Team morale:**
- Long tunnel без wins
- Business impatient

**5. Hard to test:**
- Until fully replaced, can't integrate-test
- Reality divergence от spec

**Strangler advantages:**
- Incremental value delivery
- Risk per piece small
- Early feedback / learning
- Rollback per-piece possible
- Business sees progress
- Dev team retains momentum

**Industry consensus (Sam Newman, Fowler, Netflix blog):** strangler beats big bang in 95%+ cases.


> [!mcq]
> - [ ] Big bang быстрее — экономит на двойной поддержке и proxy infra | ❌ ПОСЛЕДСТВИЕ: 50%+ rewrite failures (Netscape), zero value до 100%, feature parity недостижим.
> - [x] Strangler даёт incremental value, low risk per step, rollback per slice; big bang — годы без отдачи, moving target и часто не доезжает | ✓ ПРИМЕНЯТЬ: всё legacy modernization, особенно при живом бизнесе. 📋 ПРАВИЛО: «маленькие победы > великая мечта». 🔗 См. Q3
> - [ ] Big bang предпочтительнее для small teams: меньше координации | ❌ ПОСЛЕДСТВИЕ: small team тонет в feature parity, бизнес теряет терпение на 6-м месяце.
> - [ ] Strangler нужен только когда нет backup плана — fallback strategy | ❌ ПОСЛЕДСТВИЕ: смешивание понятий, strangler — это основная стратегия, а не план B.

## Q3. (!) Как работает механика?

**Essential components:**

**1. Proxy / Facade layer** (entry point для traffic):
- Routes requests к old or new system
- Based on URL, feature flag, user segment

**2. New service** (replacement):
- Implements subset of old functionality
- Deployed independently

**3. Old monolith:**
- Still running; handles non-migrated paths

**Flow:**
```
Client → Proxy
         ├─ /users (migrated) → [New User Service]
         ├─ /orders (in progress) → [Shadow: both]
         └─ /rest → [Old Monolith]
```

**Progressive steps:**
1. Deploy proxy (transparent initially)
2. Build new service for slice (e.g., user profile endpoint)
3. Route small % traffic к new
4. Monitor, fix issues
5. Ramp to 100%
6. Remove code from monolith
7. Repeat for next slice


> [!mcq]
> - [x] Proxy/facade на entry point routes requests к old или new system по URL/flag/segment; новый сервис обрабатывает migrated slice, монолит — остальное; постепенный ramp traffic | ✓ ПРИМЕНЯТЬ: при наличии чёткого entry point и возможности routing. 📋 ПРАВИЛО: «proxy решает кто живёт». 🔗 См. Q4
> - [ ] Монолит сам решает что делегировать новому сервису через внутренний switch | ❌ ПОСЛЕДСТВИЕ: монолит остаётся coupling-центром, проблемы routing на бизнес-логике.
> - [ ] DNS-based routing: разные сабдомены для разных версий | ❌ ПОСЛЕДСТВИЕ: ломается user session, кеш браузера приклеивает к старой версии, A/B недоступен.
> - [ ] Все клиенты переключают endpoint вручную при готовности | ❌ ПОСЛЕДСТВИЕ: невозможный coordination, mobile apps застревают на старой версии годами.

## Q4. (!) С чего начинать strangling?

**Pick first slice carefully:**

**Criteria:**

**1. Loosely coupled:**
- Few dependencies
- Clean interface (can wrap behind API)

**2. High value / high pain:**
- Has bugs or scalability issues in monolith
- Business priority

**3. Isolated data:**
- Own tables / minimal joins к other domains
- DB migration simpler

**4. Good test coverage:**
- Verify equivalence easier

**Typical first candidates:**
- Read-heavy endpoints (low risk to read replication)
- Edge endpoints (not core transaction path)
- Authentication (well-bounded)
- Notifications (async, recoverable)
- Reporting (separate data store)

**Avoid first:**
- Core business transactions
- Highly coupled (needs half the monolith's DB)
- Compliance-critical (payment, identity — high risk early)

**Strategy:** deliver small early win → build team confidence + pattern.


> [!mcq]
> - [ ] Начинать с core business transactions для максимального impact | ❌ ПОСЛЕДСТВИЕ: огромный risk на старте, любой баг = потеря денег, команда без опыта.
> - [ ] Брать самый сложный модуль — справимся, тренировка | ❌ ПОСЛЕДСТВИЕ: год без result, команда выгорает, потеря momentum и доверия бизнеса.
> - [x] Loosely coupled + high value + isolated data + good test coverage; первые кандидаты — read-heavy endpoints, auth, notifications, reporting | ✓ ПРИМЕНЯТЬ: первая итерация миграции для накопления опыта. 📋 ПРАВИЛО: «low-hanging fruit сначала». 🔗 См. Q5
> - [ ] Случайный выбор для objectivity | ❌ ПОСЛЕДСТВИЕ: попадание на core domain в первой итерации = провал миграции.

## Q5. (!) Какие функции выделять первыми?

**By coupling:**
- Low: external-facing search → own service
- Medium: user profile (shared с many but discrete data)
- High: order processing (touches payment, inventory, shipping)

**Bounded contexts (DDD):**
- Identify natural boundaries в domain
- Each bounded context → candidate service
- Event Storming workshop helps map

**Order of decomposition:**
1. Peripheral (notification, reports, search)
2. Supporting (auth, user management)
3. Core domain (after team experienced)

**Tip:** start с service where team has lowest risk tolerance. Perfect the migration process first on less critical stuff.


> [!mcq]
> - [ ] По размеру кода: маленькие классы первыми | ❌ ПОСЛЕДСТВИЕ: low-value migrations, бизнес не видит improvement, теряем поддержку.
> - [x] По coupling: low coupling (search, notifications) → medium (user profile) → high (orders); bounded contexts по DDD задают boundaries | ✓ ПРИМЕНЯТЬ: планирование decomposition roadmap. 📋 ПРАВИЛО: «от периферии к ядру». 🔗 См. Q6
> - [ ] Алфавитный порядок модулей monolith для предсказуемости | ❌ ПОСЛЕДСТВИЕ: random complexity, могут зайти на critical path в первой итерации.
> - [ ] Только то что чаще всего падает в production | ❌ ПОСЛЕДСТВИЕ: чинить под огнём вместо проектирования — risk компаундится.

## Q6. Data migration и shared DB?

**Hardest part.** Shared DB = tight coupling.

**Strategies:**

**1. Shared DB phase (temporary):**
- Both monolith и new service read/write same DB
- Fast to start; coupling remains
- Must evolve carefully

**2. Database views / APIs для isolation:**
- New service reads монолith's DB через view (not direct tables)
- Easier to eventually split

**3. Data duplication (sync):**
- New service owns own DB
- Monolith writes also replicated (CDC, events)
- Eventually consistent

**4. Full ownership migration:**
- New service owns data (authoritative)
- Monolith reads через API / event stream
- Most work; cleanest end state

**Pattern: expand-contract:**
- **Expand:** add columns / tables for new service; both read
- **Move writes:** new service writes; monolith catches via events
- **Migrate reads:** monolith stops reading old path; reads from new
- **Contract:** remove old columns/tables

**Tools:**
- Debezium (CDC)
- Kafka Connect
- Custom ETL


> [!mcq]
> - [ ] Скопировать DB одним SQL dump и переключить cutover ночью | ❌ ПОСЛЕДСТВИЕ: big bang DB cutover; даунтайм, нет rollback, drift в данных.
> - [ ] Shared DB навсегда — пусть оба сервиса пишут в одну таблицу | ❌ ПОСЛЕДСТВИЕ: tight coupling на уровне схемы, distributed monolith, любая migration ломает обоих.
> - [ ] Прокинуть direct DB connection из нового сервиса к таблицам monolith | ❌ ПОСЛЕДСТВИЕ: монолит и сервис связаны через DDL, schema evolution блокируется.
> - [x] Expand-contract: shared DB → views → CDC duplication (Debezium) → full ownership; новый сервис постепенно перенимает данные через outbox + events | ✓ ПРИМЕНЯТЬ: data migration в strangler. 📋 ПРАВИЛО: «сначала расширяй, потом сжимай». 🔗 См. Q7

## Q7. Dual-write и consistency?

**Dual-write:** write к both old и new data store during migration.

**Risks:**
- Ordering (write to A succeeds, B fails → inconsistent)
- Latency (two writes)
- Complexity (if one fails, retry where?)

**Patterns:**

**1. Write к old, CDC к new:**
- Old DB is source of truth
- Changes streamed к new via Debezium
- Eventually consistent

**2. Write к new, replicate к old:**
- New DB authoritative
- Monolith reads new via API or sees replica
- When monolith no longer needs → stop replication

**3. Transactional outbox:**
- Write к primary DB + outbox table в single tx
- Event publisher reads outbox, publishes
- Other side consumes

**4. Saga/2PC (rare):**
- Distributed tx — expensive, avoid

**Reconciliation:**
- Periodic audit: compare records in old vs new
- Fix drift automatically where safe; alert для significant

**Consistency level decision:**
- Eventual OK для most
- Strong required only для money/inventory — use careful tx patterns


> [!mcq]
> - [ ] Dual-write всегда безопасен — просто пишем в обе БД в одной транзакции | ❌ ПОСЛЕДСТВИЕ: distributed tx (2PC) дорогой, ordering bugs, latency растёт x2.
> - [x] Dual-write рискован (ordering, partial failure); use CDC из старой к новой, transactional outbox для events, reconciliation periodic audit для drift | ✓ ПРИМЕНЯТЬ: миграция данных без даунтайма. 📋 ПРАВИЛО: «один writer, остальные через outbox». 🔗 См. Q8
> - [ ] Достаточно retry на каждый write — eventual consistency сама исправит | ❌ ПОСЛЕДСТВИЕ: silent data corruption, retry без идемпотентности дублирует записи.
> - [ ] Запускать 2PC между legacy и новым сервисом для атомарности | ❌ ПОСЛЕДСТВИЕ: блокировки, perf drop, любой network blip = stuck transaction.

## Q8. (!) Proxy / facade layer?

**Router/proxy sits в front** of both systems, dispatches.

**Types:**

**1. Reverse proxy (Nginx, Envoy, HAProxy):**
- URL/path-based routing
- Configuration-driven
- Low compute
- Good for simple path splits

**2. API Gateway (Kong, AWS API Gateway):**
- More features (auth, rate, transform)
- Per-route plugins

**3. Application-level (Spring Gateway, custom):**
- Code control
- Complex logic (header-based, A/B)

**4. Service mesh (Istio, Linkerd):**
- Sidecar proxies
- Great for microservice-to-microservice

**Configuration example (Envoy):**
```yaml
routes:
  - match: { prefix: "/api/v2/users" }
    route: { cluster: new_user_service }
  - match: { prefix: "/" }
    route: { cluster: old_monolith }
```

**Gotchas:**
- Session affinity (if monolith has sessions)
- Auth token compatibility
- Tracing propagation (same user, cross-system)


> [!mcq]
> - [ ] Proxy = просто DNS — сразу даёт routing без infrastructure | ❌ ПОСЛЕДСТВИЕ: DNS не умеет URL-based routing, нет per-user, нет canary, propagation 24-48ч.
> - [x] Router в front обоих систем dispatches по URL/path/flag; типы — reverse proxy (Nginx/Envoy), API Gateway (Kong), app-level (Spring), service mesh (Istio); session affinity, auth compatibility, tracing — gotchas | ✓ ПРИМЕНЯТЬ: routing layer в strangler. 📋 ПРАВИЛО: «proxy — это switchboard». 🔗 См. Q9
> - [ ] Использовать legacy load balancer — он и так стоит на пути | ❌ ПОСЛЕДСТВИЕ: LB не умеет content-based routing, no feature flags, no per-user logic.
> - [ ] Application server сам решает кому форвардить запрос | ❌ ПОСЛЕДСТВИЕ: разрушает single responsibility, монолит знает про новые сервисы.

## Q9. Где жить proxy (gateway, reverse proxy)?

**Options:**

**1. Edge proxy (Cloudflare, AWS CloudFront):**
- Nearest к user
- Early rejection / routing
- Limited complexity

**2. API Gateway (AWS API Gateway, Kong):**
- Centralized policy
- Per-route configs
- Typical home для strangler routing

**3. Load balancer (NGINX, HAProxy):**
- Simple path-based
- Fast, minimal features

**4. Application (custom Node.js / Spring):**
- Maximum flexibility
- Business logic routing

**5. Service mesh (Istio):**
- Internal traffic (within cluster)
- Gradual traffic shifting (via VirtualService)

**Typical:** API Gateway для public; service mesh для internal.


> [!mcq]
> - [x] API Gateway для public traffic + service mesh (Istio) для internal; edge proxy (CloudFront) для early routing, LB для simple path-based, application-level для max flexibility | ✓ ПРИМЕНЯТЬ: layered approach по типу трафика. 📋 ПРАВИЛО: «edge для public, mesh для internal». 🔗 См. Q10
> - [ ] Только один proxy на edge — централизация всего | ❌ ПОСЛЕДСТВИЕ: SPOF, не масштабируется, internal traffic тоже идёт через edge — latency.
> - [ ] Proxy не нужен — клиент сам выбирает endpoint | ❌ ПОСЛЕДСТВИЕ: невозможно сделать canary, нет single point для observability/auth.
> - [ ] Базы данных делают routing через triggers | ❌ ПОСЛЕДСТВИЕ: смешивание DB и application concerns, debugging — ад.

## Q10. Feature flags для routing?

**Use feature flags** для per-request routing decisions:

```javascript
if (featureFlag('new-user-service', user)) {
  return callNewService(req);
} else {
  return callMonolith(req);
}
```

**Benefits:**
- Instant rollback (flip flag)
- Per-user / per-region ramp
- A/B testing
- Kill switch

**Tools:**
- LaunchDarkly, Split, Unleash, Statsig
- Flagsmith (OSS)

**Rollout strategy:**
1. Flag OFF: 0% new service
2. Canary: 1% select users
3. Ramp: 10% → 50% → 100%
4. Monitor: errors, latency, business metrics
5. Any regression → flip OFF instantly

**Flag management:**
- Config UI (non-engineers can toggle)
- Audit log (who changed what)
- Scheduled removal (flag cleanup policy — don't accumulate)


> [!mcq]
> - [ ] Hardcode routing в коде — быстрее, без runtime overhead | ❌ ПОСЛЕДСТВИЕ: каждое изменение route = redeploy, no instant rollback, no canary.
> - [ ] Flags только в новом сервисе — монолит ничего не знает | ❌ ПОСЛЕДСТВИЕ: routing decision на стороне нового сервиса = он получает ВСЕ запросы, defeats the purpose.
> - [x] Feature flags (LaunchDarkly, Unleash, Split) на proxy уровне делают per-request decision; instant rollback flip OFF, per-user/region ramp, A/B testing, kill switch; rollout 0→1%→10%→50%→100% с мониторингом | ✓ ПРИМЕНЯТЬ: gradual traffic shifting. 📋 ПРАВИЛО: «flag = switch без redeploy». 🔗 См. Q11
> - [ ] Использовать конфиг-файл в git и merge для смены роутинга | ❌ ПОСЛЕДСТВИЕ: ребут сервиса для применения, нет live rollback, audit log = git blame.

## Q11. (!) Преимущества?

**1. Incremental progress:**
- Value delivered continuously
- Business supportive (visible wins)

**2. Low risk per step:**
- Small chunks easy to validate
- Issues localized

**3. Rollback easy:**
- Route traffic back
- No "point of no return"

**4. Team learning:**
- Build expertise with microservices gradually
- Mistakes cheap

**5. Parallel development:**
- New team works on services while old team maintains monolith
- Fewer merge conflicts

**6. Business continuity:**
- Monolith keeps serving; no downtime
- Revenue не interrupted

**7. Tech debt retirement:**
- Old code removed as replaced
- Cleaner codebase over time


> [!mcq]
> - [ ] Главное преимущество — экономия денег за счёт переиспользования инфраструктуры | ❌ ПОСЛЕДСТВИЕ: подмена тезиса, strangler дороже big bang short-term из-за двойной поддержки.
> - [x] Incremental progress + low risk per step + easy rollback + team learning + parallel development + business continuity + technical debt retirement | ✓ ПРИМЕНЯТЬ: argumentation для бизнеса. 📋 ПРАВИЛО: «маленькие победы складываются». 🔗 См. Q12
> - [ ] Strangler гарантирует ускорение latency на 10x | ❌ ПОСЛЕДСТВИЕ: фикция, proxy добавляет hop, до завершения миграции — даже медленнее.
> - [ ] Strangler избавляет от тестов — новый сервис покрыт старыми | ❌ ПОСЛЕДСТВИЕ: новые тесты обязательны, старые не покрывают новый код.

## Q12. (!) Недостатки и риски?

**1. Long timeline:**
- Years typically (not months)
- Sustained commitment needed

**2. Double maintenance:**
- Old + new both running
- Bug fixes в two places often

**3. Integration complexity:**
- Services call monolith; monolith calls services
- Careful interface design

**4. Data consistency:**
- Dual-write or CDC — complex
- Bugs → data corruption

**5. Proxy бoтleneck:**
- All traffic through proxy
- SPOF если not HA

**6. Dependency tangles:**
- Slice B needs A migrated first
- Ordering matters

**7. Change freeze resistance:**
- Adding features к monolith discouraged → team tension
- Business may push back on "feature freeze"

**8. Never finish:**
- "Last 20%" stalls
- Remaining monolith = core domain; hardest

**9. Culture change:**
- Team needs to adopt microservices mindset
- Operations complexity grows


> [!mcq]
> - [ ] У strangler нет недостатков — это серебряная пуля | ❌ ПОСЛЕДСТВИЕ: переоценка pattern, команда не готовится к long timeline.
> - [ ] Главный риск — это performance hit от proxy | ❌ ПОСЛЕДСТВИЕ: фокус не там, реальные риски — data consistency, последние 20%, dependency tangles.
> - [x] Long timeline (годы), double maintenance, integration complexity, data consistency, proxy bottleneck SPOF, dependency tangles, «последние 20%» застревают, change freeze пушбэк, culture change | ✓ ПРИМЕНЯТЬ: risk management планирование. 📋 ПРАВИЛО: «90% готово ≠ готово». 🔗 См. Q13
> - [ ] Strangler опасен только из-за rollback complexity | ❌ ПОСЛЕДСТВИЕ: rollback как раз простой (flip flag), реальные риски в data и timeline.

## Q13. (!) Как rollback если не работает?

**Rollback per slice:**

**1. Proxy config change:**
- Flip routing back к monolith
- Seconds-minutes deploy

**2. Feature flag:**
- Toggle OFF
- Instant (no deploy)

**3. Data sync direction:**
- If new service wrote data, replicate back к monolith
- Or dual-read (monolith reads its own + new)

**Challenges:**
- If monolith stopped writing, its data stale — plan for this
- Avoid burning bridges too early

**Best practice:**
- Keep monolith code functional for X weeks after cutover
- Only delete after confidence

**Data rollback:**
- Dual-write reversible if both DBs kept in sync
- If switched fully, rollback = migrate data back

**Emergency plan:**
- Documented step-by-step
- Dry-run tested (chaos game days)
- On-call practiced


> [!mcq]
> - [ ] Rollback невозможен после миграции — только forward | ❌ ПОСЛЕДСТВИЕ: команда не готова к проблемам, panic mode при production issue.
> - [ ] Делать DB restore из бэкапа и редеплоить старую версию | ❌ ПОСЛЕДСТВИЕ: даунтайм часы, потеря данных между snapshot и now, не подходит для slice rollback.
> - [x] Per-slice: proxy config flip (минуты) или feature flag OFF (мгновенно); data — dual-write reversible если синхронизация, или migrate back; держать монолит functional X weeks после cutover; emergency plan + chaos game days | ✓ ПРИМЕНЯТЬ: rollback strategy. 📋 ПРАВИЛО: «не сжигать мосты слишком рано». 🔗 См. Q14
> - [ ] Менять обратно DNS — все клиенты перейдут | ❌ ПОСЛЕДСТВИЕ: DNS propagation часы-дни, мобильные приложения кешируют, частичный rollback.

## Q14. Testing parallel run (shadow)?

**Shadow mode:** send requests к both old и new; compare responses.

**Flow:**
1. Request arrives at proxy
2. Forward к monolith (primary, returns response)
3. Also forward к new service (shadow, response logged)
4. Compare responses; log differences
5. Real user sees monolith's response (safe)

**Benefits:**
- Test new service с real traffic
- Find discrepancies без user impact
- Build confidence before cutover

**Tools:**
- **Scientist (GitHub)** — library для Ruby/Python/Java
- **Diffy** (Twitter — archived)
- Envoy shadow routing
- Custom middleware

**Gotchas:**
- Side effects duplicated (write operations execute twice!)
- Only shadow reads / idempotent writes
- Non-idempotent: use test tenant or sanitize

**Diff analysis:**
- Response body diff
- Latency comparison
- Error rate


> [!mcq]
> - [ ] Запустить нагрузочный тест на dev — этого достаточно | ❌ ПОСЛЕДСТВИЕ: dev ≠ prod, real traffic patterns не покрыты, edge cases пропущены.
> - [x] Shadow mode: forward request к monolith (primary, response к user) И к new service (shadow, logged); compare responses; находит discrepancies без user impact; only idempotent / sanitize side effects; tools — GitHub Scientist, Envoy shadow | ✓ ПРИМЕНЯТЬ: верификация перед cutover. 📋 ПРАВИЛО: «тестируй на проде, но в тени». 🔗 См. Q15
> - [ ] A/B test с 50/50 traffic — половина юзеров на старом, половина на новом | ❌ ПОСЛЕДСТВИЕ: 50% юзеров получают баги нового сервиса, user-visible regression.
> - [ ] Полагаться на unit-тесты — они покроют edge cases | ❌ ПОСЛЕДСТВИЕ: unit-тесты не отражают реальные prod данные и interaction patterns.

## Q15. Как долго занимает migration?

**Varies wildly:**

**Small (10-person team, simple app):** 6-12 months

**Medium (50+ eng, medium monolith):** 1-2 years

**Large (Netflix, Amazon):** 5+ years
- Amazon famously took 5+ years monolith → microservices in early 2000s
- Netflix: ~7 years (completed ~2015)

**Factors:**
- Domain complexity
- Team size dedicated
- Data complexity
- Regulatory constraints
- Business urgency

**Realistic expectations:**
- 80% migrated в year 1-2
- Last 20% can take as long as first 80%
- "90% done" common trap

**Recommendation:**
- Start with cleanest piece
- Learn, document, refine process
- Apply accelerated к rest


> [!mcq]
> - [ ] Strangler — это 3-6 месяцев максимум, иначе плохая команда | ❌ ПОСЛЕДСТВИЕ: нереалистичные ожидания, бизнес теряет терпение, миграция бросается на полпути.
> - [x] Small (10 чел): 6-12 мес; medium (50+ инженеров): 1-2 года; large (Netflix, Amazon): 5+ лет (Netflix ~7 лет до 2015, Amazon 5+ лет в начале 2000-х); зависит от domain complexity, team size, data, regulations | ✓ ПРИМЕНЯТЬ: планирование roadmap. 📋 ПРАВИЛО: «последние 20% = первые 80%». 🔗 См. Q16
> - [ ] 100% параллельно от размера — миграция всегда 6 месяцев | ❌ ПОСЛЕДСТВИЕ: команда измеряет в человеко-часах, игнорирует data/regulatory complexity.
> - [ ] Strangler не имеет фиксированного срока — пока не наскучит | ❌ ПОСЛЕДСТВИЕ: нет milestones, нет бюджета, нет accountability.

## Q16. Когда считать migration complete?

**Signals:**

- Monolith handles < 5% of traffic
- Remaining functionality rarely changes
- Team no longer deploys monolith frequently
- Infrastructure cost for monolith small

**Choices at "mostly done":**

**1. Strangle the rest:**
- Continue migrating
- Fully retire monolith

**2. Freeze monolith:**
- Leave running for rare edge cases
- Stop actively developing
- Eventually sunset (business decision)

**3. Extract remaining в "legacy service":**
- Rewrite wrapper, but don't decompose further
- Pragmatic для low-traffic admin tools

**Decommissioning:**
- Final plans для data archiving
- Customer comms (if any interfaces change)
- Disable / delete infrastructure

**Post-migration:**
- Document what was learned
- Reflect on decomposition (microservices right?)
- Avoid over-decomposing (distributed monolith)


> [!mcq]
> - [x] Монолит обрабатывает < 5% трафика, редкие изменения, infra cost маленький; варианты — продолжить strangle, freeze monolith, или extract в legacy service для admin tools; sunset через business decision | ✓ ПРИМЕНЯТЬ: end-of-migration decision. 📋 ПРАВИЛО: «pragmatic > perfect». 🔗 См. Q17
> - [ ] Migration complete = 0% монолит. Точка | ❌ ПОСЛЕДСТВИЕ: годы на последние 1-2%, ROI отрицательный, "perfect — враг good".
> - [ ] Когда CTO решит — пишите ему | ❌ ПОСЛЕДСТВИЕ: subjective decision без metrics, политические игры.
> - [ ] Migration complete = тимлид сказал «всё» | ❌ ПОСЛЕДСТВИЕ: opinion-based, нет measurable criteria, бизнес не верит.

## Q17. Anti-corruption layer?

**Anti-corruption layer (ACL) — DDD term** — shield новой системы от legacy's bad model.

**Zachyy:** legacy might have weird data shapes, inconsistencies. Don't let new service absorb that.

**Pattern:**
```
New Service ←→ ACL (translator) ←→ Legacy API / DB
```

ACL:
- Maps legacy types к new domain types
- Hides legacy naming, schemas
- Single point to update when legacy changes

**Example:**
- Legacy: `CUST_RECORD` с fields `CUST_NM`, `CUST_BRTH_DT_YR`
- New: `Customer` с `name`, `birthYear`
- ACL translates

**Benefits:**
- New service clean
- Migration later doesn't break internals
- Decouples dependency

**Overuse:**
- Adds layer; not always necessary
- Use только when legacy genuinely ugly


> [!mcq]
> - [ ] ACL = тот же strangler proxy на уровне HTTP | ❌ ПОСЛЕДСТВИЕ: подмена понятий, ACL — это translation layer внутри домена, не routing.
> - [ ] ACL не нужен — пусть новый сервис использует legacy schema | ❌ ПОСЛЕДСТВИЕ: legacy badness переползает в новый код, рефакторинг невозможен.
> - [x] ACL (DDD) — translator между legacy и new domain types; скрывает legacy naming/schemas (CUST_NM → name); single point обновления при изменении legacy; decouple, но не abuse, добавляет layer | ✓ ПРИМЕНЯТЬ: когда legacy model уродлива. 📋 ПРАВИЛО: «прокладка против заражения». 🔗 См. Q18
> - [ ] ACL = это просто DTO mapping в одном направлении | ❌ ПОСЛЕДСТВИЕ: тривиализация паттерна, ACL изолирует bounded contexts двусторонне.

## Q18. Branch by abstraction?

**Alternative/complementary pattern:**

**Branch by abstraction:**
- Inside code, introduce abstraction (interface)
- Old impl + new impl both exist
- Feature flag switches
- After switch → remove old impl

**Vs Strangler:**
- Strangler: external split (HTTP proxy routes)
- Branch by abstraction: internal split (code branch)

**When use:**
- Can't decompose externally (tight coupling)
- Want to refactor within monolith before split

**Example:**
```java
interface PaymentProvider {
    void charge(...);
}

class LegacyPaymentProvider implements ... { ... }  // old code
class StripePaymentProvider implements ... { ... }  // new

@Autowired PaymentProvider provider;  // flag-selected
```

**Transition:**
1. Introduce interface (no behavior change)
2. Implement new
3. Flag switch
4. Remove old impl

**Prerequisite для strangler often** — refactor internals first, then extract.


> [!mcq]
> - [ ] Branch by abstraction = то же самое что strangler — синонимы | ❌ ПОСЛЕДСТВИЕ: смешивание понятий, branch — внутри кода, strangler — внешний HTTP split.
> - [ ] Branch by abstraction требует двух деплоев одновременно | ❌ ПОСЛЕДСТВИЕ: путаница с blue-green, на самом деле один процесс с feature flag.
> - [x] Внутренний рефакторинг: interface + старая impl + новая impl в одном коде, flag switch выбирает; после переключения старая impl удаляется; альтернатива/комплемент strangler когда нельзя decompose externally | ✓ ПРИМЕНЯТЬ: tight coupling не даёт extract, нужен refactor сначала. 📋 ПРАВИЛО: «branch внутри, strangler снаружи». 🔗 См. See also
> - [ ] Branch by abstraction — это git workflow для legacy кода | ❌ ПОСЛЕДСТВИЕ: путаница с git branching, паттерн про runtime polymorphism, не VCS.

---

## See also

- [Microservices](microservices-interview.md) — migration destination
- [API Gateway](api-gateway-interview.md) — often hosts routing
- [BFF Pattern](bff-pattern-interview.md) — related architecture pattern
- [Event-Driven Patterns](event-driven-patterns-interview.md) — CDC, events
- [Saga Pattern](saga-pattern-interview.md) — distributed transactions
- [DDD](ddd-interview.md) — bounded contexts guide decomposition
- [Technical Debt](../code-quality/technical-debt-interview.md) — strangler as debt retirement
- [Refactoring Patterns](../code-quality/refactoring-patterns-interview.md) — branch by abstraction
- [Deployment Strategies](../cicd/deployment-strategies-interview.md) — canary, feature flags
- [Consistency Patterns](consistency-patterns-interview.md) — during migration
