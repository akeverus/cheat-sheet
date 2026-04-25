---
title: "Вопросы на собеседовании: Load Testing"
description: "Load testing: tools (JMeter, k6, Gatling, Locust), типы (load, stress, spike, soak), метрики (RPS, latency, error rate), capacity planning, distributed load testing"
tags:
  - interview
  - testing
  - load-testing-interview
aliases:
  - "Load testing interview"
  - "JMeter interview"
  - "k6 interview"
  - "Gatling interview"
  - "Locust interview"
  - "Performance testing tools"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Load Testing`

`Load testing` — testing system под expected/peak load. Отличается от **stress testing** (find breaking point), **spike testing** (sudden jumps), **soak testing** (sustained over hours/days). Главные tools: **JMeter** (legacy, GUI), **k6** (modern, JS-based), **Gatling** (Scala/Java), **Locust** (Python). Critical для capacity planning.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Apache JMeter](https://jmeter.apache.org/)
- [k6 Documentation](https://k6.io/docs/)
- [Gatling Documentation](https://gatling.io/docs/)
- [Locust Documentation](https://docs.locust.io/)
- [Grafana k6 Cloud](https://grafana.com/products/cloud/k6/)
- [Performance Testing — Baeldung](https://www.baeldung.com/load-testing-with-jmeter)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое load testing?](#q1--что-такое-load-testing)
- [Q2. (!) Load vs stress vs spike vs soak vs scalability testing?](#q2--load-vs-stress-vs-spike-vs-soak-vs-scalability-testing)
- [Q3. Зачем load testing?](#q3-зачем-load-testing)

**Метрики**
- [Q4. (!) Главные метрики (RPS, latency, errors)?](#q4--главные-метрики-rps-latency-errors)
- [Q5. (!) Percentiles (p50, p95, p99) — почему важны?](#q5--percentiles-p50-p95-p99--почему-важны)
- [Q6. Throughput vs latency trade-off?](#q6-throughput-vs-latency-trade-off)

**Tools**
- [Q7. (!) JMeter (legacy gold standard)?](#q7--jmeter-legacy-gold-standard)
- [Q8. (!) k6 (modern)?](#q8--k6-modern)
- [Q9. (!) Gatling (Scala/Java)?](#q9--gatling-scalajava)
- [Q10. Locust (Python)?](#q10-locust-python)
- [Q11. Сравнение JMeter vs k6 vs Gatling vs Locust?](#q11-сравнение-jmeter-vs-k6-vs-gatling-vs-locust)

**Тестирование**
- [Q12. (!) Как написать k6 test?](#q12--как-написать-k6-test)
- [Q13. Stages (ramp-up, hold, ramp-down)?](#q13-stages-ramp-up-hold-ramp-down)
- [Q14. Thresholds (pass/fail criteria)?](#q14-thresholds-passfail-criteria)
- [Q15. Distributed load testing?](#q15-distributed-load-testing)

**Capacity planning**
- [Q16. (!) Capacity planning через load testing?](#q16--capacity-planning-через-load-testing)
- [Q17. Bottleneck analysis?](#q17-bottleneck-analysis)

**Production**
- [Q18. (!) Тестировать в production?](#q18--тестировать-в-production)
- [Q19. Realistic scenarios?](#q19-realistic-scenarios)
- [Q20. CI/CD integration?](#q20-cicd-integration)

**Подводные камни**
- [Q21. (!) Common mistakes?](#q21--common-mistakes)
- [Q22. Coordinated omission?](#q22-coordinated-omission)

## Q1. (!) Что такое load testing?

**Load testing** — testing system под **expected load** для verify performance.

**Cели:**
- **Verify SLAs** (99% requests < 200ms)
- **Find bottlenecks** (DB, network, code)
- **Capacity planning** (how many servers for X RPS)
- **Regression detection** (perf got worse?)
- **Tuning** (config changes effect)

**Use cases:**
- Перед launch (Black Friday prep)
- After major refactor
- New deployment validation
- Periodic regression testing


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Load vs stress vs spike vs soak vs scalability testing? Частая ошибка в реальном коде.

| Type | Goal | Pattern |
|------|------|---------|
| **Load** | Verify performance under expected load | Sustained normal traffic |
| **Stress** | Find breaking point | Increasing load until failure |
| **Spike** | Verify resilience к sudden bursts | Sharp increase then drop |
| **Soak (endurance)** | Find issues over long time | Sustained для hours/days |
| **Scalability** | Verify scaling | Gradually increase load + scale |
| **Capacity** | Determine max load | Find max RPS where SLA holds |

**Common pattern в production:** **all of these**, periodically.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Зачем load testing? Частая ошибка в реальном коде.

1. **Production-like behavior** detection (memory leaks, connection exhaustion)
2. **Validate SLAs** before launch
3. **Catch regressions** в performance
4. **Right-sizing** infrastructure
5. **Confidence** для deploy
6. **Debugging** под load (different bugs than dev env)
7. **Tuning** opportunities (DB indexes, caching)

**Without load testing** → production failures, customer impact.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. (!) Главные метрики (RPS, latency, errors)? Частая ошибка в реальном коде.

**RPS (Requests per Second):** throughput.
**Latency:** response time (per request).
**Error rate:** % failed requests.
**Concurrent users (VUs):** simulated users.
**Bandwidth:** network usage.
**CPU/Memory:** server resource usage (correlate с request rate).

**Industry-standard "RED metrics":**
- **R**ate — RPS
- **E**rrors — error rate
- **D**uration — latency

**USE metrics** (для resources):
- **U**tilization — % busy
- **S**aturation — queue depth
- **E**rrors


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) Percentiles (p50, p95, p99) — почему важны? Частая ошибка в реальном коде.

**Average latency misleading.** Few slow requests can be hidden.

**Percentiles** show distribution:
- **p50 (median)** — typical user experience
- **p95** — 5% worst case
- **p99** — 1% worst case
- **p99.9** — extreme tail

**Example:**
```
1000 requests
99% complete в 100ms
1% take 5000ms (timeout, GC pause)

Avg = ~150ms
p99 = 5000ms ← real user pain
```

**Tail latency** matters для UX.

**SLA usually defined в percentiles:** "p95 < 200ms".


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Throughput vs latency trade-off? Частая ошибка в реальном коде.

**Higher throughput** often = **higher latency** (queuing).

**Little's Law:**
```
Concurrency = Throughput × Latency
```

Example:
- Latency 10ms, Throughput 1000 RPS → Concurrency 10
- Latency 100ms, Throughput 100 RPS → Concurrency 10

**Capacity = max throughput где latency acceptable.**


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. (!) JMeter (legacy gold standard)? Частая ошибка в реальном коде.

**Apache JMeter** — most popular OSS load testing (с 1998!).

**Pros:**
- **GUI** для test design (drag-drop)
- **HUGE plugin ecosystem**
- Multiple protocols (HTTP, JDBC, JMS, gRPC)
- **Distributed mode** (master + slaves)
- **Industry standard** в enterprise

**Cons:**
- **Heavy** (Java GUI sluggish)
- **XML-based** test plans (verbose, hard к diff)
- **Lower performance** than k6/Gatling
- **Threads** — high resource usage per virtual user

**Still widely used** — legacy investments, enterprise familiarity.

```bash
jmeter -n -t test-plan.jmx -l results.jtl  # CLI mode
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) k6 (modern)? Частая ошибка в реальном коде.

**k6** (by Grafana, formerly Load Impact) — modern load testing tool.

**Pros:**
- **JavaScript/TypeScript** test scripts
- **Very high performance** (Go runtime)
- **CI/CD-friendly**
- **Cloud + Open Source** versions
- **Modern reporting** (Grafana integration)

**Cons:**
- **Less mature** than JMeter
- **JS limitations** для complex scenarios

**Example:**
```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 100,
  duration: '5m',
  thresholds: {
    'http_req_duration': ['p(95)<200'],
    'http_req_failed': ['rate<0.01'],
  },
};

export default function () {
  const res = http.get('https://api.example.com/users');
  check(res, { 'status 200': (r) => r.status === 200 });
  sleep(1);
}
```

```bash
k6 run script.js
```

В **2025** — k6 fastest-growing load testing tool.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) Gatling (Scala/Java)? Частая ошибка в реальном коде.

**Gatling** — Scala-based (DSL также Java/Kotlin since 3.7).

**Pros:**
- **Very high performance** (async I/O)
- **Beautiful HTML reports**
- **Code-based** tests (Scala/Java/Kotlin DSL)
- Async, non-blocking

**Cons:**
- Scala learning curve
- Less plugin ecosystem than JMeter

**Example (Scala):**
```scala
class BasicSimulation extends Simulation {
  val httpProtocol = http.baseUrl("https://api.example.com")

  val scn = scenario("Get Users")
    .exec(http("get_users").get("/users").check(status.is(200)))

  setUp(scn.inject(rampUsers(100) during 60.seconds))
    .protocols(httpProtocol)
    .assertions(global.responseTime.percentile(95).lte(200))
}
```

**Gatling vs k6** — closely matched. Choice often по language preference.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Locust (Python)? Частая ошибка в реальном коде.

**Locust** — Python-based, code-driven.

```python
from locust import HttpUser, task, between

class WebsiteUser(HttpUser):
    wait_time = between(1, 5)

    @task
    def load_users(self):
        self.client.get("/users")

    @task(3)  # 3x more frequent
    def get_user(self):
        self.client.get(f"/users/{random.randint(1, 100)}")
```

**Pros:**
- Python (easy adoption)
- Web UI
- Distributed mode

**Cons:**
- **Lower performance** than k6/Gatling (Python GIL)
- Less mature reporting


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Сравнение JMeter vs k6 vs Gatling vs Locust? Частая ошибка в реальном коде.

| Critterion | JMeter | k6 | Gatling | Locust |
|-----------|--------|-----|---------|--------|
| Language | XML/GUI | JS/TS | Scala/Java | Python |
| Performance | Medium | **Very High** | **Very High** | Medium |
| Reports | OK | Good (Grafana) | **Excellent** | OK |
| GUI test design | **Yes** | No | No | No |
| Cloud version | Various | k6 Cloud | Gatling Enterprise | — |
| Maturity | **Highest** | Growing | High | Medium |
| Adoption | Largest legacy | Growing rapidly | Growing | Niche |

**Choice (2025):**
- **Modern projects** — k6 (JavaScript appeal, fast)
- **Java shops** — Gatling
- **Python shops** — Locust
- **Enterprise legacy** — JMeter
- **No-code GUI** — JMeter


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Как написать k6 test? Частая ошибка в реальном коде.

```javascript
import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

export const options = {
  scenarios: {
    constant_load: {
      executor: 'constant-vus',
      vus: 100,
      duration: '5m',
    },
  },
  thresholds: {
    'http_req_duration': ['p(95)<200', 'p(99)<500'],
    'http_req_failed': ['rate<0.01'],
    'errors': ['rate<0.05'],
  },
};

export function setup() {
  // login, get token
  const res = http.post('https://api.example.com/login', { ... });
  return { token: res.json('token') };
}

export default function (data) {
  const headers = { Authorization: `Bearer ${data.token}` };

  group('User flow', function () {
    let res = http.get('https://api.example.com/users', { headers });
    check(res, { 'status 200': (r) => r.status === 200 });
    errorRate.add(res.status !== 200);

    res = http.post('https://api.example.com/orders', { ... }, { headers });
    check(res, { 'status 201': (r) => r.status === 201 });
  });

  sleep(1);
}

export function teardown(data) {
  // cleanup
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Stages (ramp-up, hold, ramp-down)? Частая ошибка в реальном коде.

```javascript
options = {
  stages: [
    { duration: '2m', target: 100 },   // ramp up к 100 VUs
    { duration: '5m', target: 100 },   // hold 5 minutes
    { duration: '2m', target: 200 },   // ramp к 200 VUs
    { duration: '5m', target: 200 },   // hold
    { duration: '2m', target: 0 },     // ramp down
  ],
};
```

**Realistic** — gradual increase (не sudden 0 → 1000).

**Patterns:**
- **Ramp-up:** find performance vs load
- **Steady load:** soak test
- **Spike:** sudden increase
- **Ramp-down:** verify recovery


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Thresholds (pass/fail criteria)? Частая ошибка в реальном коде.

```javascript
options = {
  thresholds: {
    'http_req_duration': ['p(95)<500'],   // p95 < 500ms
    'http_req_failed': ['rate<0.01'],     // error rate < 1%
    'http_req_duration{name:GetUser}': ['p(99)<200'],  // tag-specific
    'iterations': ['count>1000'],         // min iterations
  },
};
```

**Test fails** если threshold не met. CI/CD integration breaks build.

**Use case:** **performance budget** (perf SLAs enforced automatically).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Distributed load testing? Частая ошибка в реальном коде.

**Why:** single load generator can't produce enough load.

**JMeter distributed:**
- Master controls slaves
- Slaves generate load
- Aggregated results

**k6:**
- **Cloud version** — managed distributed
- **OSS distributed** через operator (K8s)

**Gatling:**
- Gatling Enterprise — distributed
- OSS — manual setup

**Common pattern:** generate load from multiple regions для realistic geo-distribution.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. (!) Capacity planning через load testing? Частая ошибка в реальном коде.

**Process:**
1. Define **SLA** (e.g., p95 < 200ms, error rate < 1%)
2. Run **gradually increasing load** (10, 50, 100, 200, 500 RPS)
3. Find **max RPS** где SLA holds
4. Calculate **headroom** (e.g., target 70% capacity)
5. Provision accordingly

**Example:**
```
Single instance: 100 RPS sustainable
Expected peak: 1000 RPS
Headroom 50%: needs 1500 RPS capacity
Required: 15 instances
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. Bottleneck analysis? Частая ошибка в реальном коде.

**Find** что limiting performance:
- CPU? → scale CPU или optimize code
- Memory? → scale RAM, GC tuning
- Network? → bandwidth, connection pool size
- DB? → indexes, queries, connection pool
- Disk I/O? → SSD, async writes
- External APIs? → caching, async

**Tools:**
- APM (Datadog, New Relic, Honeycomb)
- Profiling (`pprof`, async-profiler)
- DB query analysis (`EXPLAIN ANALYZE`)
- Network monitoring

**Iteratively:** load test → identify bottleneck → optimize → repeat.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. (!) Тестировать в production? Частая ошибка в реальном коде.

**Yes** — но carefully.

**Approaches:**
- **Shadow traffic** (mirror production traffic к staging)
- **Canary load tests** (small % production traffic)
- **Off-hours testing** (when impact lower)
- **Chaos engineering** combined

**Risk:** affecting real users.

**Production load tests** essential для true validation (staging never matches prod).

**Examples:** Netflix runs constant load tests в production (chaos engineering).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. Realistic scenarios? Частая ошибка в реальном коде.

**Bad scenario:** all VUs hitting one endpoint repeatedly.

**Good scenario:**
- Multiple endpoints proportional к real usage
- Realistic data (varied user IDs, payloads)
- Realistic think times (`sleep`)
- Login → browse → action flows
- Session state per VU

**Tools:**
- **Record real traffic** → replay (JMeter HTTP recorder)
- **Production logs** → derive workload patterns
- **User journey analytics** → script common flows


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. CI/CD integration? Частая ошибка в реальном коде.

```yaml
# GitHub Actions
- name: Run k6 load test
  uses: grafana/k6-action@v0.3.0
  with:
    filename: tests/api-load-test.js
    flags: --vus 50 --duration 5m

- name: Check thresholds
  if: failure()
  run: echo "Performance regression detected"
```

**Patterns:**
- **PR builds** — quick smoke test (5-10 min)
- **Nightly** — full load test
- **Pre-release** — comprehensive performance suite
- **Production** — periodic synthetic monitoring


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. (!) Common mistakes? Частая ошибка в реальном коде.

1. **Testing only happy path** — error scenarios важны
2. **No realistic data** (same user ID for всех)
3. **No think time** — unrealistic burst
4. **Test from same region** as server (no network latency)
5. **Caching skews** results (dev cached, prod cold)
6. **Insufficient warm-up** (JIT, connection pools)
7. **No baselines** — comparing against what?
8. **One-shot tests** — need consistency over time
9. **Ignoring percentiles** (only checking averages)
10. **Coordinated omission** (Q22)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. Coordinated omission? Частая ошибка в реальном коде.

**Coordinated omission** — load testing tool **slows down** when system slow → underreports latency.

**Example:**
- Plan: 1000 RPS (1 ms apart)
- System slow → request takes 1 second
- Tool waits → next request 1.001 seconds later
- **Misses spike** в latency

**Result:** percentile latencies look better than reality.

**Solutions:**
- Tools with **constant arrival rate** (k6 `constant-arrival-rate` executor)
- HdrHistogram corrections
- **Synthetic load injection** (don't wait for previous)

**JMeter, Gatling** — by default, Gatling handles coordinated omission better.

В **2025** — k6 popular для new projects из-за simplicity + performance. JMeter — legacy enterprise.

---

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Performance Testing](../performance/performance-testing-interview.md) — общая концепция Это антипаттерн или неправильный выбор в production.
- [Unit Testing](unit-testing-interview.md) — context
- [Mutation Testing](mutation-testing-interview.md)
- [Property-based Testing](property-based-testing-interview.md)
- [Chaos Engineering](chaos-engineering-interview.md) — production resilience
- [Application Profiling](../performance/application-profiling-interview.md) — find bottlenecks
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — context
- [Микросервисы](../architecture/microservices-interview.md) — testing distributed systems
- [Observability](../monitoring/observability-interview.md) — monitor under load
- [[cicd-interview|CI/CD]] — automation
- [Caching](../architecture/caching-strategies-interview.md) — для performance
