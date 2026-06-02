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

`Load testing` — проверка системы под ожидаемой/пиковой нагрузкой. Отличается от **stress testing** (поиск точки отказа), **spike testing** (резкие скачки), **soak testing** (длительная нагрузка часами/днями). Главные инструменты: **JMeter** (legacy, GUI), **k6** (современный, на JS), **Gatling** (Scala/Java), **Locust** (Python). Критично для capacity planning.

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

**Load testing** — проверка системы под **ожидаемой нагрузкой** для верификации производительности.

**Цели:**
- **Проверить SLA** (99% запросов < 200ms)
- **Найти узкие места** (БД, сеть, код)
- **Capacity planning** (сколько серверов нужно для X RPS)
- **Обнаружить регрессии** (производительность просела?)
- **Тюнинг** (эффект от изменения конфигурации)

**Сценарии применения:**
- Перед запуском (подготовка к Black Friday)
- После крупного рефакторинга
- Валидация нового деплоя
- Периодическое regression-тестирование

## Q2. (!) Load vs stress vs spike vs soak vs scalability testing?

| Тип | Цель | Паттерн |
|------|------|---------|
| **Load** | Проверить производительность под ожидаемой нагрузкой | Устойчивый нормальный трафик |
| **Stress** | Найти точку отказа | Растущая нагрузка до сбоя |
| **Spike** | Проверить устойчивость к резким всплескам | Резкий рост и спад |
| **Soak (endurance)** | Найти проблемы на длинной дистанции | Устойчивая нагрузка часами/днями |
| **Scalability** | Проверить масштабирование | Постепенный рост нагрузки + scale |
| **Capacity** | Определить максимальную нагрузку | Найти максимальный RPS, при котором SLA держится |

**Типичная практика в production:** **все из перечисленного**, периодически.

## Q3. Зачем load testing?

1. **Обнаружение production-подобного поведения** (утечки памяти, исчерпание соединений)
2. **Валидация SLA** до запуска
3. **Отлов регрессий** в производительности
4. **Right-sizing** инфраструктуры
5. **Уверенность** при деплое
6. **Отладка** под нагрузкой (другие баги, чем в dev-окружении)
7. **Возможности тюнинга** (индексы БД, кэширование)

**Без load testing** → отказы в production, ущерб для пользователей.

## Q4. (!) Главные метрики (RPS, latency, errors)?

**RPS (Requests per Second):** пропускная способность (throughput).
**Latency:** время отклика (на каждый запрос).
**Error rate:** % неудачных запросов.
**Concurrent users (VUs):** имитируемые пользователи.
**Bandwidth:** использование сети.
**CPU/Memory:** потребление ресурсов сервера (коррелирует с request rate).

**Отраслевой стандарт «RED metrics»:**
- **R**ate — RPS
- **E**rrors — error rate
- **D**uration — latency

**USE metrics** (для ресурсов):
- **U**tilization — % занятости
- **S**aturation — глубина очереди
- **E**rrors

## Q5. (!) Percentiles (p50, p95, p99) — почему важны?

**Средняя latency вводит в заблуждение.** Несколько медленных запросов могут «спрятаться».

**Перцентили** показывают распределение:
- **p50 (медиана)** — типичный пользовательский опыт
- **p95** — 5% худших случаев
- **p99** — 1% худших случаев
- **p99.9** — экстремальный хвост распределения

**Пример:**
```
1000 requests
99% complete в 100ms
1% take 5000ms (timeout, GC pause)

Avg = ~150ms
p99 = 5000ms ← real user pain
```

**Tail latency** (хвостовая задержка) важна для UX.

**SLA обычно задаётся в перцентилях:** «p95 < 200ms».

## Q6. Throughput vs latency trade-off?

**Выше throughput** часто = **выше latency** (из-за очередей).

**Закон Литтла (Little's Law):**
```
Concurrency = Throughput × Latency
```

Пример:
- Latency 10ms, Throughput 1000 RPS → Concurrency 10
- Latency 100ms, Throughput 100 RPS → Concurrency 10

**Capacity = максимальный throughput, при котором latency приемлема.**

## Q7. (!) JMeter (legacy gold standard)?

**Apache JMeter** — самый популярный OSS-инструмент load testing (с 1998 года!).

**Плюсы:**
- **GUI** для проектирования тестов (drag-drop)
- **ОГРОМНАЯ экосистема плагинов**
- Множество протоколов (HTTP, JDBC, JMS, gRPC)
- **Distributed mode** (master + slaves)
- **Отраслевой стандарт** в enterprise

**Минусы:**
- **Тяжёлый** (Java GUI тормозит)
- **Тест-планы на XML** (многословные, тяжело диффить)
- **Ниже производительность**, чем у k6/Gatling
- **Потоки** — высокое потребление ресурсов на одного виртуального пользователя

**По-прежнему широко используется** — наследие инвестиций, привычка в enterprise.

```bash
jmeter -n -t test-plan.jmx -l results.jtl  # CLI mode
```

## Q8. (!) k6 (modern)?

**k6** (от Grafana, ранее Load Impact) — современный инструмент load testing.

**Плюсы:**
- Тест-скрипты на **JavaScript/TypeScript**
- **Очень высокая производительность** (Go runtime)
- **Дружелюбен к CI/CD**
- Версии **Cloud + Open Source**
- **Современная отчётность** (интеграция с Grafana)

**Минусы:**
- **Менее зрелый**, чем JMeter
- **Ограничения JS** для сложных сценариев

**Пример:**
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

В **2025** — k6 наиболее быстрорастущий инструмент load testing.

## Q9. (!) Gatling (Scala/Java)?

**Gatling** — на базе Scala (DSL также Java/Kotlin начиная с 3.7).

**Плюсы:**
- **Очень высокая производительность** (async I/O)
- **Красивые HTML-отчёты**
- Тесты **в виде кода** (DSL на Scala/Java/Kotlin)
- Асинхронный, неблокирующий

**Минусы:**
- Кривая обучения Scala
- Экосистема плагинов меньше, чем у JMeter

**Пример (Scala):**
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

**Gatling vs k6** — близки по возможностям. Выбор часто диктуется предпочтением языка.

## Q10. Locust (Python)?

**Locust** — на Python, управляемый кодом.

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

**Плюсы:**
- Python (легко освоить)
- Web UI
- Distributed mode

**Минусы:**
- **Ниже производительность**, чем у k6/Gatling (Python GIL)
- Менее зрелая отчётность

## Q11. Сравнение JMeter vs k6 vs Gatling vs Locust?

| Критерий | JMeter | k6 | Gatling | Locust |
|-----------|--------|-----|---------|--------|
| Язык | XML/GUI | JS/TS | Scala/Java | Python |
| Производительность | Средняя | **Очень высокая** | **Очень высокая** | Средняя |
| Отчёты | Нормально | Хорошо (Grafana) | **Отлично** | Нормально |
| Проектирование тестов в GUI | **Да** | Нет | Нет | Нет |
| Cloud-версия | Разные | k6 Cloud | Gatling Enterprise | — |
| Зрелость | **Наивысшая** | Растёт | Высокая | Средняя |
| Распространённость | Крупнейшее наследие | Быстро растёт | Растёт | Нишевый |

**Выбор (2025):**
- **Современные проекты** — k6 (привлекательность JavaScript, скорость)
- **Java-команды** — Gatling
- **Python-команды** — Locust
- **Enterprise-наследие** — JMeter
- **No-code GUI** — JMeter

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

**Реалистично** — постепенный рост (не резкий 0 → 1000).

**Паттерны:**
- **Ramp-up:** найти зависимость производительности от нагрузки
- **Steady load:** soak-тест
- **Spike:** резкий рост
- **Ramp-down:** проверить восстановление

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

**Тест падает**, если threshold не выполнен. В CI/CD это ломает билд.

**Сценарий применения:** **performance budget** (perf-SLA проверяются автоматически).

## Q15. Distributed load testing?

**Зачем:** один генератор нагрузки не способен создать достаточную нагрузку.

**JMeter distributed:**
- Master управляет slaves
- Slaves генерируют нагрузку
- Результаты агрегируются

**k6:**
- **Cloud-версия** — управляемое распределение
- **OSS distributed** через оператор (K8s)

**Gatling:**
- Gatling Enterprise — распределённый режим
- OSS — настройка вручную

**Типичная практика:** генерировать нагрузку из нескольких регионов для реалистичной гео-распределённости.

## Q16. (!) Capacity planning через load testing?

**Процесс:**
1. Определить **SLA** (например, p95 < 200ms, error rate < 1%)
2. Запустить **постепенно растущую нагрузку** (10, 50, 100, 200, 500 RPS)
3. Найти **максимальный RPS**, при котором SLA держится
4. Рассчитать **запас (headroom)** (например, целиться в 70% от capacity)
5. Спровизионить инфраструктуру соответственно

**Пример:**
```
Single instance: 100 RPS sustainable
Expected peak: 1000 RPS
Headroom 50%: needs 1500 RPS capacity
Required: 15 instances
```

## Q17. Bottleneck analysis?

**Найти**, что ограничивает производительность:
- CPU? → масштабировать CPU или оптимизировать код
- Память? → увеличить RAM, тюнинг GC
- Сеть? → bandwidth, размер connection pool
- БД? → индексы, запросы, connection pool
- Disk I/O? → SSD, асинхронная запись
- Внешние API? → кэширование, асинхронность

**Инструменты:**
- APM (Datadog, New Relic, Honeycomb)
- Профилирование (`pprof`, async-profiler)
- Анализ запросов к БД (`EXPLAIN ANALYZE`)
- Мониторинг сети

**Итеративно:** load-тест → выявить узкое место → оптимизировать → повторить.

## Q18. (!) Тестировать в production?

**Да** — но осторожно.

**Подходы:**
- **Shadow traffic** (зеркалирование production-трафика на staging)
- **Canary load tests** (небольшой % production-трафика)
- **Тестирование вне часов пик** (когда влияние меньше)
- В сочетании с **chaos engineering**

**Риск:** воздействие на реальных пользователей.

**Production-нагрузочные тесты** необходимы для настоящей валидации (staging никогда не совпадает с prod).

**Примеры:** Netflix постоянно гоняет load-тесты в production (chaos engineering).

## Q19. Realistic scenarios?

**Плохой сценарий:** все VU долбят один endpoint по кругу.

**Хороший сценарий:**
- Несколько endpoint-ов пропорционально реальному использованию
- Реалистичные данные (разнообразные user ID, payload-ы)
- Реалистичные think time (`sleep`)
- Потоки login → просмотр → действие
- Состояние сессии на каждый VU

**Инструменты:**
- **Запись реального трафика** → воспроизведение (JMeter HTTP recorder)
- **Production-логи** → вывод паттернов нагрузки
- **Аналитика пользовательских путей** → скриптование типовых сценариев

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

**Паттерны:**
- **PR-билды** — быстрый smoke-тест (5–10 мин)
- **Nightly** — полный load-тест
- **Pre-release** — комплексный набор performance-тестов
- **Production** — периодический synthetic-мониторинг

## Q21. (!) Common mistakes?

1. **Тестирование только happy path** — error-сценарии тоже важны
2. **Нереалистичные данные** (один и тот же user ID для всех)
3. **Нет think time** — нереалистичные всплески
4. **Тест из того же региона**, что и сервер (нет сетевой latency)
5. **Кэширование искажает** результаты (dev прогрет, prod холодный)
6. **Недостаточный прогрев** (JIT, connection pool-ы)
7. **Нет baseline** — а с чем сравнивать?
8. **Разовые тесты** — нужна стабильность во времени
9. **Игнорирование перцентилей** (смотрят только средние)
10. **Coordinated omission** (Q22)

## Q22. Coordinated omission?

**Coordinated omission** — инструмент load testing **замедляется**, когда система тормозит → занижает latency в отчётах.

**Пример:**
- План: 1000 RPS (интервал 1 ms)
- Система тормозит → запрос занимает 1 секунду
- Инструмент ждёт → следующий запрос идёт на 1.001 секунды позже
- **Пропускает всплеск** latency

**Итог:** перцентильные latency выглядят лучше, чем на самом деле.

**Решения:**
- Инструменты с **постоянной частотой поступления (constant arrival rate)** (executor `constant-arrival-rate` в k6)
- Коррекции HdrHistogram
- **Синтетическая инъекция нагрузки** (не ждать завершения предыдущего запроса)

**JMeter, Gatling** — по умолчанию Gatling лучше справляется с coordinated omission.

В **2025** — k6 популярен для новых проектов из-за простоты + производительности. JMeter — legacy-enterprise.

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
