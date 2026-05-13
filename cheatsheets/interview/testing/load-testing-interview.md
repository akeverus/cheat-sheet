---
title: "Вопросы на собеседовании: Load Testing"
description: "Load testing: tools (JMeter, k6, Gatling, Locust), типы (load, stress, spike, soak), метрики (RPS, latency, error rate), capacity planning, distributed load testing"
tags:
  - interview
  - testing
  - load-testing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Load Testing"
  - "Load testing interview"
  - "JMeter interview"
prerequisites: []
next: []
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
> - [ ] Найти все баги в коде приложения перед релизом | ❌ ПОСЛЕДСТВИЕ: load testing не заменяет unit/code review — логические баги не проявляются под нагрузкой
> - [ ] Проверить работу системы при экстремальной нагрузке выше production | ❌ ПОСЛЕДСТВИЕ: это stress testing — ищет breaking point, а не SLA compliance при ожидаемой нагрузке
> - [x] Проверить выполнение SLA и найти bottlenecks при ожидаемой production нагрузке | ✓ ПРИМЕНЯТЬ: перед запуском, после рефакторинга, как regression gate 📋 ПРАВИЛО: Load = expected load → SLA verification 🔗 См. Q2
> - [ ] Обнаружить утечки памяти при длительной работе | ❌ ПОСЛЕДСТВИЕ: это soak/endurance testing — memory leaks видны после часов работы, не кратким load тестом

## Q2. (!) Load vs stress vs spike vs soak vs scalability testing?

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
> - [ ] Stress testing — нагружает до поломки, выявляет breaking point | ❌ ПОСЛЕДСТВИЕ: stress не учитывает длительность — memory leaks за 10 минут не покажет
> - [ ] Load testing — обычная production нагрузка в течение нескольких минут | ❌ ПОСЛЕДСТВИЕ: кратковременный load тест не выявит утечки памяти — нужны часы работы
> - [ ] Spike testing — резкий burst нагрузки для проверки resilience | ❌ ПОСЛЕДСТВИЕ: spike short по времени, после пика нагрузка спадает — утечки не копятся
> - [x] Soak (endurance) testing — sustained нагрузка 8-24 часа выявляет постепенные деградации | ✓ ПРИМЕНЯТЬ: обнаружение memory leaks, connection pool exhaustion, file descriptor leaks 📋 ПРАВИЛО: Soak = продолжительность → утечки ресурсов 🔗 См. Q3

## Q3. Зачем load testing?

1. **Production-like behavior** detection (memory leaks, connection exhaustion)
2. **Validate SLAs** before launch
3. **Catch regressions** в performance
4. **Right-sizing** infrastructure
5. **Confidence** для deploy
6. **Debugging** под load (different bugs than dev env)
7. **Tuning** opportunities (DB indexes, caching)

**Without load testing** → production failures, customer impact.


> [!mcq]
> - [ ] Баги в бизнес-логике останутся незамеченными | ❌ ПОСЛЕДСТВИЕ: баги логики — зона unit/integration тестов; load testing не проверяет correctness
> - [x] Connection pool exhaustion или memory leak под нагрузкой выведет сервис из строя в production | ✓ ПРИМЕНЯТЬ: capacity planning + production-like нагрузка перед peak events 📋 ПРАВИЛО: без load tests — production failures в prime time 🔗 См. Q1
> - [ ] Документация API будет неверной | ❌ ПОСЛЕДСТВИЕ: документация не зависит от нагрузочных тестов — это API contract issue
> - [ ] Тесты в CI pipeline будут медленными | ❌ ПОСЛЕДСТВИЕ: скорость CI не связана с production производительностью — разные проблемы

## Q4. (!) Главные метрики (RPS, latency, errors)?

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
> - [ ] Response time, Error rate, Data throughput | ❌ ПОСЛЕДСТВИЕ: Data throughput не входит в RED — правильно Rate (RPS), Errors, Duration (latency)
> - [x] Rate (RPS), Errors (error rate), Duration (latency) — RED метрики для мониторинга сервисов | ✓ ПРИМЕНЯТЬ: SLA alerting + Grafana dashboards 📋 ПРАВИЛО: RED = Rate/Errors/Duration для каждого сервиса 🔗 См. Q5
> - [ ] Requests, Events, Delays | ❌ ПОСЛЕДСТВИЕ: нестандартная аббревиатура — RED = Rate/Errors/Duration; "Events" и "Delays" не стандартные термины
> - [ ] Resources, Execution time, Dependencies | ❌ ПОСЛЕДСТВИЕ: это смешение USE метрик (Resources) и нестандартных терминов — путаница ведёт к неправильному мониторингу

## Q5. (!) Percentiles (p50, p95, p99) — почему важны?

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
> - [ ] Average latency — самая важная метрика, по ней оценивают SLA | ❌ ПОСЛЕДСТВИЕ: average маскирует tail latency — 1% requests с 5000ms «растворяются» в среднем 150ms; реальная боль пользователя в хвосте, не в среднем
> - [ ] p50 (median) — отражает worst case, по нему пишут SLA | ❌ ПОСЛЕДСТВИЕ: p50 = typical user experience, а не worst case; путаница приведёт к SLA, который игнорирует 50% худших запросов
> - [ ] p99 показывает производительность лучших 1% запросов | ❌ ПОСЛЕДСТВИЕ: ровно наоборот — p99 = 1% ХУДШИХ запросов; путаница в направлении приведёт к ложному ощущению что система быстрая
> - [x] Percentiles показывают распределение latency: p50 — typical UX, p95 — 5% худших, p99 — 1% худших, p99.9 — extreme tail; SLA формулируются как «p95 < 200ms», потому что average скрывает tail latency (1% с 5000ms растворяется в среднем 150ms) | ✓ ПРИМЕНЯТЬ: при формулировке SLA, при мониторинге production, при capacity planning 📋 ПРАВИЛО: SLA → percentiles, не average; tail latency = real UX pain 🔗 См. Q6

## Q6. Throughput vs latency trade-off?

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
> - [x] Little's Law: Concurrency = Throughput × Latency; рост throughput при фиксированной concurrency требует снижения latency (или наоборот); capacity = max throughput где latency остаётся acceptable | ✓ ПРИМЕНЯТЬ: для оценки concurrency при заданных SLA и RPS, для capacity planning 📋 ПРАВИЛО: L = λ × W → выбираем 2 параметра, третий вычисляется 🔗 См. Q7
> - [ ] Throughput и latency независимы — можно максимизировать оба одновременно | ❌ ПОСЛЕДСТВИЕ: игнорирование queuing theory; в реальности рост throughput → насыщение → queueing → рост latency; «hockey stick» график неизбежен
> - [ ] Higher latency всегда означает higher throughput | ❌ ПОСЛЕДСТВИЕ: ложная корреляция; высокая latency может быть симптомом bottleneck при низком throughput (slow DB, GC pauses)
> - [ ] Little's Law применим только в синхронных системах | ❌ ПОСЛЕДСТВИЕ: Little's Law — общий закон queuing theory, работает и в async (event-loop, reactive); ограничение приведёт к отказу от полезного инструмента анализа

## Q7. (!) JMeter (legacy gold standard)?

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
> - [ ] JMeter — самый быстрый load testing tool из-за Java | ❌ ПОСЛЕДСТВИЕ: JMeter использует thread-per-VU (один Java thread = много памяти ~1MB stack), что даёт LOWER performance чем k6/Gatling (event-loop, async I/O); неверный выбор приведёт к нехватке VUs на машине
> - [ ] JMeter поддерживает только HTTP — для JDBC/JMS нужны другие инструменты | ❌ ПОСЛЕДСТВИЕ: JMeter изначально multi-protocol: HTTP, JDBC, JMS, gRPC, FTP, SOAP, MQTT через samplers; выбор другого инструмента — лишняя интеграция
> - [x] Apache JMeter (с 1998) — most popular OSS load tool: GUI для test design (drag-drop XML test plans .jmx), huge plugin ecosystem, multi-protocol (HTTP/JDBC/JMS/gRPC), distributed mode (master+slaves); cons — heavy Java GUI, verbose XML, lower per-VU performance (thread-based) | ✓ ПРИМЕНЯТЬ: enterprise legacy, no-code teams (GUI), multi-protocol tests, существующий .jmx investment 📋 ПРАВИЛО: GUI + plugins + multi-protocol → JMeter; performance-критично → k6/Gatling 🔗 См. Q8
> - [ ] JMeter не поддерживает CLI режим — только GUI | ❌ ПОСЛЕДСТВИЕ: в CI/CD JMeter запускается через `jmeter -n -t test.jmx -l results.jtl` (non-GUI mode); вера в GUI-only приведёт к отказу от JMeter в pipeline

## Q8. (!) k6 (modern)?

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
> - [ ] k6 — это просто rebrand JMeter с новым GUI | ❌ ПОСЛЕДСТВИЕ: k6 — независимый Go-based runtime от Grafana (изначально Load Impact), без GUI для test design; путаница приведёт к ожиданию JMeter-фич которых нет
> - [ ] k6 написан на Python для скриптов | ❌ ПОСЛЕДСТВИЕ: тесты пишутся на JavaScript/TypeScript, runtime — Go; путаница с Locust (Python); Python-скрипт не запустится
> - [x] k6 (Grafana) — modern load tool с JS/TS скриптами, Go-based runtime (very high performance), CI/CD-friendly через `k6 run script.js`, thresholds для pass/fail в pipeline, options.vus + options.duration для базовых сценариев; integration с Grafana для reporting; в 2025 — fastest-growing tool | ✓ ПРИМЕНЯТЬ: modern stack, CI/CD автоматизация, JavaScript-команды, code-as-config 📋 ПРАВИЛО: code-based + CI/CD + JS → k6 (не GUI инструмент) 🔗 См. Q9
> - [ ] k6 требует JVM как JMeter и Gatling | ❌ ПОСЛЕДСТВИЕ: k6 — single Go binary без JVM, что упрощает Docker-образы (~30MB vs 200MB для JMeter); ложное требование JVM приведёт к лишним зависимостям

## Q9. (!) Gatling (Scala/Java)?

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
> - [x] Gatling — Scala-based load tool (DSL также Java/Kotlin с 3.7), очень высокая производительность через async non-blocking I/O (Akka), code-based simulations с rampUsers/atOnceUsers/constantUsersPerSec, beautiful HTML reports из коробки; closely matches k6 по performance; выбор часто по language preference (Scala/Java shops) | ✓ ПРИМЕНЯТЬ: Java/Scala/Kotlin команды, нужны красивые HTML-отчёты для PR, async-heavy targets 📋 ПРАВИЛО: JVM stack + code-based → Gatling; JS stack → k6 🔗 См. Q10
> - [ ] Gatling использует thread-per-user модель как JMeter | ❌ ПОСЛЕДСТВИЕ: Gatling построен на Akka actors (async, non-blocking I/O); это и даёт high performance vs JMeter thread-based; путаница приведёт к недоиспользованию async-возможностей
> - [ ] Gatling доступен только для Scala — Java/Kotlin не поддерживаются | ❌ ПОСЛЕДСТВИЕ: с Gatling 3.7 (2021) есть полноценные Java и Kotlin DSL; ложное ограничение приведёт к отказу от инструмента в Java-проектах
> - [ ] Gatling требует GUI (Gatling Studio) для написания тестов | ❌ ПОСЛЕДСТВИЕ: Gatling — code-based, simulations пишутся в `.scala`/`.java`/`.kt`; нет обязательного GUI; ожидание GUI приведёт к фрустрации

## Q10. Locust (Python)?

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
> - [ ] Locust — самый высокопроизводительный load tool | ❌ ПОСЛЕДСТВИЕ: Python GIL ограничивает per-process concurrency; Locust имеет lower performance чем k6/Gatling; для high RPS нужно много worker процессов
> - [x] Locust — Python-based code-driven load tool: `HttpUser`/`@task`/`@task(weight)` для определения сценариев, `wait_time = between(1,5)` для think time, Web UI для контроля; pros — Python familiarity, distributed mode, программная гибкость; cons — Python GIL limits per-VU performance, менее зрелые отчёты | ✓ ПРИМЕНЯТЬ: Python-команды, существующий Python ecosystem, сценарии требующие сложной логики на Python 📋 ПРАВИЛО: Python stack + flexibility > raw performance → Locust 🔗 См. Q11
> - [ ] Locust не имеет distributed mode и GUI | ❌ ПОСЛЕДСТВИЕ: Locust имеет встроенный distributed (master + workers) и Web UI; ложное ограничение приведёт к выбору JMeter там где Locust справился бы
> - [ ] Locust автоматически избегает GIL через async | ❌ ПОСЛЕДСТВИЕ: классический Locust использует gevent (greenlets), но всё равно ограничен GIL для CPU-bound; для масштабирования нужны несколько worker процессов

## Q11. Сравнение JMeter vs k6 vs Gatling vs Locust?

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
> - [ ] Один инструмент подходит для всех команд — выбор не важен | ❌ ПОСЛЕДСТВИЕ: tool choice сильно влияет: language fit (Java vs JS vs Python), performance (thread vs event-loop), maturity vs modern features; неправильный выбор приведёт к высоким costs обучения и поддержки
> - [ ] JMeter всегда лучше, потому что самый старый | ❌ ПОСЛЕДСТВИЕ: возраст ≠ качество; JMeter имеет lower per-VU performance, verbose XML тест-планы трудно diff-ить в git; для современных проектов k6/Gatling часто лучше
> - [x] Выбор зависит от стека и приоритетов: modern + JS-stack → k6 (high perf, CI-friendly); JVM/Scala shops → Gatling (high perf, beautiful reports); Python shops → Locust (familiar, flexible); enterprise legacy + no-code GUI → JMeter (mature, plugin ecosystem) | ✓ ПРИМЕНЯТЬ: при выборе tool — оценить language fit, performance budget, существующие investments, GUI-vs-code предпочтения 📋 ПРАВИЛО: tool follows stack — выбираем инструмент под команду, не наоборот 🔗 См. Q12
> - [ ] Performance важна только для k6, остальные одинаковы | ❌ ПОСЛЕДСТВИЕ: Gatling имеет сравнимую с k6 производительность (Akka async), JMeter и Locust заметно медленнее на VU; ложное упрощение приведёт к неверному capacity planning load-машин

## Q12. (!) Как написать k6 test?

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
> - [x] k6 test структурирован: `import` модулей (`k6/http`, `k6/check`), `export const options = { scenarios, thresholds }` для конфигурации, `export function setup()` для one-time подготовки (login, токены), `export default function (data)` — основной VU loop с `http.get/post` + `check()` + `group()`, `export function teardown(data)` для cleanup; custom метрики через `new Rate('errors')` | ✓ ПРИМЕНЯТЬ: для realistic flow — setup для логина, default для bulk операций, thresholds для CI gating 📋 ПРАВИЛО: options + setup + default + teardown — стандартная структура k6 test 🔗 См. Q13
> - [ ] k6 требует main() функцию как обычный JavaScript | ❌ ПОСЛЕДСТВИЕ: k6 использует ES module exports (default, options, setup, teardown), не CommonJS main; попытка `function main()` не запустится — k6 не найдёт entry point
> - [ ] Thresholds и scenarios нужно задавать через CLI флаги, не в коде | ❌ ПОСЛЕДСТВИЕ: можно через флаги (`--vus`, `--duration`), но это для quick runs; для CI рекомендуется code-as-config через export const options для воспроизводимости и version control
> - [ ] check() автоматически завершает тест при первом failure | ❌ ПОСЛЕДСТВИЕ: check() — non-blocking, лишь записывает результат в метрику; для блокировки сценария нужен fail() или if-условие; ожидание автозавершения приведёт к ложным сценариям

## Q13. Stages (ramp-up, hold, ramp-down)?

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
> - [ ] Sudden jump от 0 к target VUs наиболее реалистично | ❌ ПОСЛЕДСТВИЕ: реальный трафик растёт постепенно (за исключением Black Friday); sudden 0→1000 VUs создаёт thundering herd на cold cache, connection pools, что не отражает realistic load
> - [ ] Stages нужны только для spike testing | ❌ ПОСЛЕДСТВИЕ: stages применяются и для load (ramp-up + hold), и для capacity (gradual increase), и для soak; ограничение spike тестами лишит инструмента для других типов
> - [ ] Ramp-down не нужен — после теста можно просто остановить | ❌ ПОСЛЕДСТВИЕ: ramp-down проверяет recovery — освобождение connection pools, completion in-flight requests, GC stabilization; пропуск пропустит баги в graceful shutdown
> - [x] Stages — массив `{ duration, target }` определяющий профиль нагрузки: ramp-up (2m to 100 VUs), hold (5m at 100), ramp to higher (2m to 200), hold (5m at 200), ramp-down (2m to 0); каждый stage = realistic phase реального трафика | ✓ ПРИМЕНЯТЬ: для realistic load profile, для capacity testing (постепенное увеличение), для spike (резкие изменения target) 📋 ПРАВИЛО: ramp-up → hold → ramp-up → hold → ramp-down, не sudden jumps 🔗 См. Q14

## Q14. Thresholds (pass/fail criteria)?

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
> - [ ] Threshold проверяет только статус-коды HTTP, latency нельзя | ❌ ПОСЛЕДСТВИЕ: k6 thresholds работают с любыми метриками: `http_req_duration`, `http_req_failed`, custom rates/counters; ложное ограничение приведёт к проверке только error rate
> - [x] Thresholds — массив правил pass/fail на метриках: `http_req_duration: ['p(95)<500']` для latency SLA, `http_req_failed: ['rate<0.01']` для error rate, `http_req_duration{name:GetUser}: ['p(99)<200']` для tag-specific, `iterations: ['count>1000']` для completeness; если threshold не met — тест fail и CI/CD breaks build (performance budget enforced) | ✓ ПРИМЕНЯТЬ: для performance budget в CI, для tag-specific SLA (разные endpoint имеют разные пороги) 📋 ПРАВИЛО: thresholds = автоматический performance gate в CI 🔗 См. Q15
> - [ ] Если threshold не выполнен, тест продолжается с warning | ❌ ПОСЛЕДСТВИЕ: по умолчанию k6 возвращает non-zero exit code при threshold failure, что валит CI build; вера в warning приведёт к пропуску регрессий
> - [ ] Threshold синтаксис работает только для p50 и p95, p99 не поддерживается | ❌ ПОСЛЕДСТВИЕ: ложное ограничение; синтаксис `p(99)`, `p(99.9)`, `p(99.99)` поддерживается — важно для tail latency SLA

## Q15. Distributed load testing?

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
> - [x] Distributed load testing — генерация нагрузки с нескольких машин когда один load generator не справляется: JMeter (master-slaves), k6 (Cloud или OSS k8s-operator), Gatling (Enterprise или manual OSS setup); для geo-distribution — запуск из разных регионов (Cloud) | ✓ ПРИМЕНЯТЬ: при targeting >10k VUs, при тестах с реалистичной geo-задержкой, при ограничениях сети/CPU одной машины 📋 ПРАВИЛО: один generator → bottleneck в самом тесте; распределяем по машинам и регионам 🔗 См. Q16
> - [ ] Один мощный load generator всегда лучше distributed | ❌ ПОСЛЕДСТВИЕ: одна машина ограничена ~10-50k VUs, network bandwidth, CPU; geo-distribution в принципе невозможна с одной машины — тесты не отразят реальную latency пользователей
> - [ ] Distributed mode означает только запуск тестов на multiple cores | ❌ ПОСЛЕДСТВИЕ: путаница с multi-threading; distributed — несколько физических/виртуальных машин, координированных через master, для масштабирования за пределы одной machine
> - [ ] k6 OSS не поддерживает distributed — нужна только Cloud версия | ❌ ПОСЛЕДСТВИЕ: k6 OSS distributed можно сделать через k6-operator на Kubernetes; Cloud упрощает, но не единственный вариант

## Q16. (!) Capacity planning через load testing?

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
> - [ ] Provision максимум возможной capacity — лучше всегда иметь запас | ❌ ПОСЛЕДСТВИЕ: бесконечные costs; правильный подход — headroom 30-50% над expected peak, не «максимум»
> - [ ] Достаточно один раз протестировать на peak RPS и масштабировать | ❌ ПОСЛЕДСТВИЕ: без gradual increase нельзя найти точку где SLA breaks; «один peak test» не покажет деградацию p95 при росте нагрузки
> - [ ] Capacity = max RPS до 100% CPU | ❌ ПОСЛЕДСТВИЕ: 100% CPU = уже degraded SLA (latency растёт); capacity = max RPS где SLA (p95, error rate) ещё держится, обычно на 60-80% CPU
> - [x] Capacity planning процесс: (1) Define SLA — p95 < 200ms, error rate < 1%; (2) Run gradually increasing load — 10/50/100/200/500 RPS; (3) Find max RPS где SLA holds; (4) Calculate headroom — target 70% (запас 30%); (5) Provision: например single instance = 100 RPS, expected peak = 1000, headroom 50% → 1500 RPS / 100 = 15 instances | ✓ ПРИМЕНЯТЬ: перед Black Friday/launch, при autoscaling configuration, для обоснования cost infrastructure 📋 ПРАВИЛО: SLA → max RPS per instance → / target headroom → required instances 🔗 См. Q17

## Q17. Bottleneck analysis?

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
> - [ ] Bottleneck — это всегда CPU; смотреть только на CPU usage | ❌ ПОСЛЕДСТВИЕ: bottleneck может быть в любой ресурсе (DB, network, disk I/O, external API); фиксация на CPU пропустит реальную проблему — например slow DB queries при низком CPU
> - [ ] Достаточно посмотреть только результаты load testing инструмента | ❌ ПОСЛЕДСТВИЕ: load tool показывает client-side метрики (latency, error); для bottleneck analysis нужны server-side данные — APM, profiling, DB EXPLAIN, network metrics
> - [x] Итеративный процесс: load test → находим bottleneck через APM (Datadog/New Relic), profiling (pprof, async-profiler), DB analysis (EXPLAIN ANALYZE), network monitoring → оптимизируем (CPU scale, индексы, connection pool, caching, async) → repeat; ресурсы для проверки: CPU, RAM/GC, network bandwidth, DB queries, disk I/O, external API dependencies | ✓ ПРИМЕНЯТЬ: после каждого load test для поиска "next bottleneck"; после оптимизации обязательно re-test 📋 ПРАВИЛО: bottleneck может быть везде → check resources holistically, итеративно 🔗 См. Q18
> - [ ] Если найден один bottleneck — система оптимизирована | ❌ ПОСЛЕДСТВИЕ: после fix одного bottleneck появится следующий (закон амдала); процесс итеративный — устранение DB bottleneck может выявить network bottleneck

## Q18. (!) Тестировать в production?

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
> - [ ] Никогда нельзя тестировать в production — слишком опасно | ❌ ПОСЛЕДСТВИЕ: staging environment никогда не воспроизводит реальную нагрузку (трафик, данные, dependencies); отказ от production testing приведёт к скрытым проблемам видимым только при peak load
> - [ ] Production testing = просто запустить полный load test на боевой системе | ❌ ПОСЛЕДСТВИЕ: full load test на prod без контроля = outage для пользователей; нужны safety mechanisms (canary %, off-hours, kill switch, blast radius)
> - [x] Да, но carefully через несколько подходов: shadow traffic (mirror prod traffic в staging для validation), canary load tests (постепенно увеличиваем % реального трафика), off-hours testing (когда impact ниже), chaos engineering integration; Netflix постоянно гоняет load tests в проде — реальные условия trump staging | ✓ ПРИМЕНЯТЬ: когда staging заметно отличается от prod (data, traffic patterns, infrastructure), для true SLA validation, перед peak events 📋 ПРАВИЛО: prod testing = essential для true validation; safety через canary + blast radius + kill switch 🔗 См. Q19
> - [ ] Достаточно один раз в год прогнать load test на staging — этого хватит | ❌ ПОСЛЕДСТВИЕ: годовой staging test не отражает изменения в prod (data growth, infra changes, новые dependencies); регрессии накапливаются и обнаружатся в worst time

## Q19. Realistic scenarios?

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
> - [x] Realistic scenario: multiple endpoints proportional к real usage (не один endpoint repeatedly), realistic data (varied user IDs, randomized payloads), realistic think times через sleep(), full user flows (login → browse → action), session state per VU; источники — HTTP recorder (JMeter), production logs analytics, user journey data | ✓ ПРИМЕНЯТЬ: при дизайне load теста — изучить реальный traffic profile, не выдумывать; рандомизировать payloads, моделировать sessions 📋 ПРАВИЛО: модель = traffic mix (% per endpoint) + think times + realistic data, не one-endpoint hammering 🔗 См. Q20
> - [ ] Достаточно отправлять много запросов на один endpoint — это и есть load test | ❌ ПОСЛЕДСТВИЕ: bad scenario — все VUs на один endpoint не отражает реальную систему; пропустит проблемы в других endpoints, caching skew, неправильно оценит DB load
> - [ ] Think time не нужен — больше нагрузки лучше | ❌ ПОСЛЕДСТВИЕ: без think time VU бомбит сервер 1000+ RPS — нереалистично; в реальности пользователь думает 1-10s между actions; нагрузка получится в 100х выше реальной, что приведёт к ложным выводам о capacity
> - [ ] Один user ID для всех VUs упрощает тест | ❌ ПОСЛЕДСТВИЕ: same user ID = same cache key, same DB row → все запросы попадают в hot cache, не отражая реальный workload; результаты будут слишком оптимистичными

## Q20. CI/CD integration?

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
> - [ ] Прогонять full load test на каждом коммите | ❌ ПОСЛЕДСТВИЕ: full load test занимает 30+ минут — PR pipeline станет невыносимо медленным; правильно — quick smoke (5-10 min) на PR, nightly full, pre-release comprehensive
> - [ ] Load testing нельзя интегрировать в CI — это только локальный инструмент | ❌ ПОСЛЕДСТВИЕ: ложное ограничение; k6 (`grafana/k6-action`), JMeter, Gatling имеют CI-friendly CLI режимы; пропуск CI лишает performance regression detection
> - [x] CI/CD интеграция multi-tier: PR builds → quick smoke (5-10 min, малое количество VUs, базовые thresholds); nightly → full load test (правильные масштабы, все scenarios); pre-release → comprehensive performance suite; production → periodic synthetic monitoring; k6 в GitHub Actions через `grafana/k6-action@v0.3.0` + thresholds для pass/fail | ✓ ПРИМЕНЯТЬ: для performance regression detection в pipeline, разные depths на разных стадиях release cycle 📋 ПРАВИЛО: PR = smoke, nightly = full, pre-release = comprehensive, prod = synthetic 🔗 См. Q21
> - [ ] Один load test для всего pipeline достаточен | ❌ ПОСЛЕДСТВИЕ: разные стадии нуждаются в разной глубине — smoke на PR (быстро), comprehensive перед release (детально); один тест либо слишком долгий для PR, либо слишком слабый для release validation

## Q21. (!) Common mistakes?

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
> - [x] Common mistakes: testing only happy path (нет error scenarios), no realistic data (same user ID), no think time (unrealistic burst), test from same region as server (no network latency), caching skews (cold prod vs cached dev), insufficient warm-up (JIT, connection pools), no baselines (comparing to what?), one-shot tests (нет consistency), ignoring percentiles (only average), coordinated omission | ✓ ПРИМЕНЯТЬ: чеклист перед каждым load test — варьировать data, добавить think time, тестировать из другого региона, прогревать систему, проверять percentiles 📋 ПРАВИЛО: avoid the 10 — realistic data + think time + warm-up + percentiles + multiple regions 🔗 См. Q22
> - [ ] Testing happy path достаточно — error scenarios это unit testing | ❌ ПОСЛЕДСТВИЕ: error paths под нагрузкой ведут себя иначе — exception handling, retries, circuit breakers; пропуск приведёт к багам в error handling видимым только в проде
> - [ ] Average latency — самая важная метрика, percentiles вторичны | ❌ ПОСЛЕДСТВИЕ: average маскирует tail latency; tail = real UX pain; ignoring percentiles приведёт к SLA violation в production несмотря на «хороший average» в тестах
> - [ ] Достаточно тестировать с одного региона — geo distribution не важна | ❌ ПОСЛЕДСТВИЕ: тест из datacenter = 1ms RTT до сервера; реальные пользователи в 100+ ms; результаты слишком оптимистичные относительно реального UX

## Q22. Coordinated omission?

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


> [!mcq]
> - [ ] Coordinated omission — это когда несколько load testing инструментов координируются между собой | ❌ ПОСЛЕДСТВИЕ: путаница с distributed coordination; реальное coordinated omission — баг измерения, не координация инструментов
> - [ ] Coordinated omission неактуально, если SLA не очень жёсткие | ❌ ПОСЛЕДСТВИЕ: эффект искажает percentiles в 5-10x; даже мягкие SLA становятся «выполнены» в тесте и «нарушены» в проде
> - [ ] Достаточно увеличить duration теста, чтобы избежать coordinated omission | ❌ ПОСЛЕДСТВИЕ: проблема не в длительности, а в логике waiting — длинный тест с omission всё равно даёт искажённые percentiles
> - [x] Coordinated omission — load tool «замедляется» вместе с системой: запланировано 1000 RPS (1ms apart), но запрос занял 1s → tool ждёт окончания → следующий запрос через 1.001s, пропуская latency spike. Percentile latencies выглядят лучше реальности. Solutions: tools с constant arrival rate (k6 `constant-arrival-rate` executor), HdrHistogram corrections, synthetic load injection (не ждать предыдущий). Gatling справляется лучше JMeter by default | ✓ ПРИМЕНЯТЬ: для accurate percentile measurement, особенно при tail latency SLA; выбирать `constant-arrival-rate` вместо `constant-vus` 📋 ПРАВИЛО: closed-loop (wait for response) → coordinated omission; open-loop (constant arrival) → correct measurement 🔗 См. See also

---

## See also

- [Performance Testing](../performance/performance-testing-interview.md) — общая концепция
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
