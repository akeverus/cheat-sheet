---
title: "Вопросы на собеседовании: Chaos Engineering"
description: "Полное покрытие Chaos Engineering: принципы (steady state, hypothesis, blast radius), эксперименты, Netflix Simian Army, Chaos Monkey Spring Boot, Gremlin, Litmus, Chaos Mesh, Pumba, Toxiproxy, GameDay, maturity model, интеграция с SRE и CI/CD."
tags:
  - interview
  - testing
  - chaos-engineering-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Chaos Engineering"
  - "Chaos Engineering interview"
  - "Хаос-инжиниринг вопросы"
prerequisites:
  - "[[chaos-engineering]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Chaos Engineering`

Краткие ответы по дисциплине хаос-инжиниринга: как превратить непредсказуемые сбои в управляемые эксперименты, какие инструменты выбрать (`Chaos Monkey`, `Gremlin`, `Litmus`, `Chaos Mesh`), как организовать `GameDay` и встроить эксперименты в CI/CD без ущерба для пользователей.

**Chaos Engineering** — это дисциплина проведения экспериментов на распределённой системе с целью повысить уверенность в её способности выдерживать турбулентные условия в продакшене. Родилась в Netflix в 2011 году (`Chaos Monkey`), сейчас формализована в `principlesofchaos.org` и поддерживается экосистемой инструментов для VM, контейнеров и Kubernetes.

## Полезные ссылки

### Официальная документация

- [Principles of Chaos Engineering](https://principlesofchaos.org/) — канонический манифест дисциплины
- [Introduction to Chaos Monkey for Spring Boot — Baeldung](https://www.baeldung.com/spring-boot-chaos-monkey) — практический старт с `chaos-monkey-spring-boot`
- [Chaos Monkey Spring Boot — GitHub](https://github.com/codecentric/chaos-monkey-spring-boot) — исходники и документация
- [Chaos Mesh Docs](https://chaos-mesh.org/docs/) — платформа хаоса для Kubernetes
- [Litmus Chaos Docs](https://docs.litmuschaos.io/) — CNCF-incubating проект для Kubernetes
- [Gremlin Docs](https://www.gremlin.com/docs/) — коммерческая SaaS-платформа
- [Netflix Tech Blog — Chaos Engineering](https://netflixtechblog.com/tagged/chaos-engineering) — первоисточники практики
- [Chaos Engineering (O'Reilly, Rosenthal & Jones)](https://www.oreilly.com/library/view/chaos-engineering/9781492043850/) — книга от авторов практики в Netflix

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и принципы**
- [Q1. (!) Что такое Chaos Engineering и зачем он нужен?](#q1--что-такое-chaos-engineering-и-зачем-он-нужен)
- [Q2. (!) Какие 5 принципов Chaos Engineering?](#q2--какие-5-принципов-chaos-engineering)
- [Q3. (!) Что такое Steady State Hypothesis?](#q3--что-такое-steady-state-hypothesis)
- [Q4. (!) Что такое Blast Radius и как его контролировать?](#q4--что-такое-blast-radius-и-как-его-контролировать)
- [Q5. Chaos Engineering vs Testing vs Fault Injection?](#q5-chaos-engineering-vs-testing-vs-fault-injection)
- [Q6. Запускать эксперименты в проде или на staging?](#q6-запускать-эксперименты-в-проде-или-на-staging)

**История и Netflix Simian Army**
- [Q7. (!) Что такое Netflix Simian Army и какие инструменты в него входят?](#q7--что-такое-netflix-simian-army-и-какие-инструменты-в-него-входят)
- [Q8. Как появился Chaos Monkey и какую задачу он решает?](#q8-как-появился-chaos-monkey-и-какую-задачу-он-решает)
- [Q9. Что такое Chaos Kong и Chaos Gorilla?](#q9-что-такое-chaos-kong-и-chaos-gorilla)

**Виды экспериментов**
- [Q10. (!) Какие типы хаос-экспериментов существуют?](#q10--какие-типы-хаос-экспериментов-существуют)
- [Q11. Как устроен эксперимент с network latency?](#q11-как-устроен-эксперимент-с-network-latency)
- [Q12. Что такое DNS chaos и как его тестировать?](#q12-что-такое-dns-chaos-и-как-его-тестировать)
- [Q13. Как тестировать disk fill и IO chaos?](#q13-как-тестировать-disk-fill-и-io-chaos)
- [Q14. Как проводить CPU/memory stress-эксперименты?](#q14-как-проводить-cpumemory-stress-эксперименты)
- [Q15. Что такое dependency failure и как её инжектировать?](#q15-что-такое-dependency-failure-и-как-её-инжектировать)

**Chaos Monkey для Spring Boot**
- [Q16. (!) Как подключить Chaos Monkey к Spring Boot приложению?](#q16--как-подключить-chaos-monkey-к-spring-boot-приложению)
- [Q17. (!) Что такое Assaults и Watchers в Chaos Monkey?](#q17--что-такое-assaults-и-watchers-в-chaos-monkey)
- [Q18. Как настроить Latency Assault и Exception Assault?](#q18-как-настроить-latency-assault-и-exception-assault)
- [Q19. Как управлять Chaos Monkey через Actuator в runtime?](#q19-как-управлять-chaos-monkey-через-actuator-в-runtime)
- [Q20. Когда использовать @ChaosMonkeyAnnotation?](#q20-когда-использовать-chaosmonkeyannotation)

**Kubernetes-инструменты: Litmus и Chaos Mesh**
- [Q21. (!) Что такое Chaos Mesh и какие fault types он поддерживает?](#q21--что-такое-chaos-mesh-и-какие-fault-types-он-поддерживает)
- [Q22. (!) Как написать PodChaos эксперимент на Chaos Mesh?](#q22--как-написать-podchaos-эксперимент-на-chaos-mesh)
- [Q23. Как сделать NetworkChaos для симуляции задержек?](#q23-как-сделать-networkchaos-для-симуляции-задержек)
- [Q24. Что такое Litmus и чем он отличается от Chaos Mesh?](#q24-что-такое-litmus-и-чем-он-отличается-от-chaos-mesh)
- [Q25. Как устроен ChaosEngine и ChaosExperiment в Litmus?](#q25-как-устроен-chaosengine-и-chaosexperiment-в-litmus)

**Другие инструменты**
- [Q26. Что такое Gremlin и какие у него категории атак?](#q26-что-такое-gremlin-и-какие-у-него-категории-атак)
- [Q27. Что такое Pumba и когда его использовать?](#q27-что-такое-pumba-и-когда-его-использовать)
- [Q28. (!) Что такое Toxiproxy и чем он полезен в интеграционных тестах?](#q28--что-такое-toxiproxy-и-чем-он-полезен-в-интеграционных-тестах)
- [Q29. Сравнение инструментов: что когда выбирать?](#q29-сравнение-инструментов-что-когда-выбирать)

**GameDay и процесс**
- [Q30. (!) Что такое GameDay и как его проводить?](#q30--что-такое-gameday-и-как-его-проводить)
- [Q31. Что должно быть в runbook для chaos-эксперимента?](#q31-что-должно-быть-в-runbook-для-chaos-эксперимента)
- [Q32. Как устроен post-mortem после эксперимента?](#q32-как-устроен-post-mortem-после-эксперимента)
- [Q33. (!) Что такое abort condition и когда останавливать эксперимент?](#q33--что-такое-abort-condition-и-когда-останавливать-эксперимент)

**Observability и SRE**
- [Q34. (!) Какие требования к observability для Chaos Engineering?](#q34--какие-требования-к-observability-для-chaos-engineering)
- [Q35. Как Chaos Engineering связан с error budget и SLO?](#q35-как-chaos-engineering-связан-с-error-budget-и-slo)
- [Q36. Как встроить Chaos Engineering в CI/CD pipeline?](#q36-как-встроить-chaos-engineering-в-cicd-pipeline)

**Maturity и организация**
- [Q37. (!) Что такое Chaos Maturity Model (CMM)?](#q37--что-такое-chaos-maturity-model-cmm)
- [Q38. Какие роли участвуют в Chaos Engineering?](#q38-какие-роли-участвуют-в-chaos-engineering)
- [Q39. Как убедить бизнес и руководство внедрять Chaos Engineering?](#q39-как-убедить-бизнес-и-руководство-внедрять-chaos-engineering)

**Антипаттерны и практики**
- [Q40. (!) Какие антипаттерны в Chaos Engineering?](#q40--какие-антипаттерны-в-chaos-engineering)
- [Q41. Что значит "не делать chaos ради chaos"?](#q41-что-значит-не-делать-chaos-ради-chaos)
- [Q42. Какие prerequisites должны быть перед первым экспериментом?](#q42-какие-prerequisites-должны-быть-перед-первым-экспериментом)
- [Q43. Как комбинировать Chaos Engineering с resilience-паттернами?](#q43-как-комбинировать-chaos-engineering-с-resilience-паттернами)
- [Q44. (!) Best practices и чек-лист перед экспериментом](#q44--best-practices-и-чек-лист-перед-экспериментом)

---

## Q1. (!) Что такое `Chaos Engineering` и зачем он нужен?

**Chaos Engineering** — это дисциплина экспериментов на распределённой системе для повышения уверенности в её способности переносить турбулентные условия в проде. Формулировка принадлежит Netflix (2011, Greg Orzell) и формализована в [principlesofchaos.org](https://principlesofchaos.org/).

### Проблема, которую решает

В распределённых системах отказы **неизбежны**: падают инстансы, теряются пакеты, тормозит сеть, ломаются зависимости. Обычное тестирование проверяет известные сценарии; хаос проверяет **unknown unknowns** — эффекты, которые проявляются только при реальном взаимодействии сервисов под нагрузкой.

### Отличие от обычного тестирования

| Тестирование | Chaos Engineering |
|--------------|-------------------|
| Проверяем известные требования | Ищем неизвестные слабые места |
| Функциональные сценарии | Отказы инфраструктуры и зависимостей |
| Пишем один раз | Повторяем регулярно (continuous) |
| CI / staging | Часто в проде с ограниченным blast radius |

### Зачем это бизнесу

- Снижение MTTR и частоты инцидентов
- Валидация resilience-паттернов ([circuit breaker, retry, bulkhead](../architecture/resilience-patterns-interview.md))
- Тренировка команды реагирования на инциденты
- Проверка runbooks и алертинга

На собеседовании: важно противопоставлять "testing" (ищем баги) и "chaos engineering" (строим уверенность в поведении системы при сбоях).


> [!mcq]
> - [ ] Chaos Engineering — это stress testing под высокой нагрузкой для нахождения performance bottlenecks | ❌ ПОСЛЕДСТВИЕ: load testing ≠ chaos; load testing проверяет capacity, chaos проверяет поведение при отказах зависимостей
> - [ ] Chaos Engineering — это регрессионное тестирование prod-окружения перед релизом | ❌ ПОСЛЕДСТВИЕ: регрессия проверяет известные сценарии; chaos ищет unknown unknowns — эффекты реальных сбоев под нагрузкой
> - [x] Disciplined experimentation on a distributed system to build confidence in its behavior under turbulent conditions; цель — unknown unknowns, не поиск багов | ✓ ПРИМЕНЯТЬ: когда нужно валидировать resilience-паттерны в prod-подобных условиях 📋 ПРАВИЛО: Chaos = confidence building through controlled failure injection 🔗 См. Q2
> - [ ] Chaos Engineering — это GameDay один раз в год с командой | ❌ ПОСЛЕДСТВИЕ: одноразовый GameDay без автоматизации даёт одноразовую уверенность; принцип 4 требует continuous автоматизации

## Q2. (!) Какие 5 принципов `Chaos Engineering`?

Канонический список с [principlesofchaos.org](https://principlesofchaos.org()):

1. **Build a Hypothesis around Steady State Behavior** — формулируем гипотезу о нормальном поведении через измеримые метрики (throughput, error rate, latency p99), а не через внутренние детали реализации.
2. **Vary Real-world Events** — инжектируем реальные события: crash инстансов, сеть, DNS, всплески трафика. Приоритизируем по частоте и потенциальному импакту.
3. **Run Experiments in Production** — только прод даёт настоящие условия: трафик, конфигурации, данные. Staging не воспроизводит все особенности.
4. **Automate Experiments to Run Continuously** — одноразовый ручной эксперимент даёт одноразовую уверенность. Автоматизация встраивает chaos в CI/CD.
5. **Minimize Blast Radius** — эксперимент должен затрагивать минимальную часть пользователей; есть kill switch для немедленной остановки.

### Мнемоника

```mermaid
graph LR
    A[Steady State<br/>Hypothesis] --> B[Vary Real<br/>Events]
    B --> C[Run in<br/>Production]
    C --> D[Automate<br/>Continuously]
    D --> E[Minimize<br/>Blast Radius]
    E -.feedback.-> A
```

На собеседовании часто спрашивают именно эти 5 пунктов — стоит знать их наизусть.


> [!mcq]
> - [ ] Hypothesize → Inject → Observe → Repeat — без Steady State определения | ❌ ПОСЛЕДСТВИЕ: без измеримого baseline невозможно определить что считать «отказом»; experiment вывод будет субъективным
> - [x] Steady State Hypothesis: формулируем измеримые метрики нормального поведения (p95 latency, error rate, throughput), затем проверяем что они сохранятся при инжектировании хаоса | ✓ ПРИМЕНЯТЬ: перед каждым chaos экспериментом — определить baseline metrics + acceptable deviation 📋 ПРАВИЛО: Steady State = измеримый baseline → эксперимент нарушает его → observe deviation 🔗 См. Q1
> - [ ] Steady State — внутренние состояния приложения (threads, connections) не внешние метрики | ❌ ПОСЛЕДСТВИЕ: принцип 1 требует бизнес-метрики (checkouts/min, error rate) а не internal state; internal metrics меняются при scaling и дают false positives
> - [ ] Steady State определяется после эксперимента по факту | ❌ ПОСЛЕДСТВИЕ: определение baseline после experiment = post-hoc rationalization; нет pre-defined hypothesis = нет valid experiment

## Q3. (!) Что такое `Steady State Hypothesis`?

**Steady State** — измеримое поведение системы в нормальных условиях, выраженное через бизнес- и технические метрики. **Hypothesis** — гипотеза, что эта устойчивость сохранится при инжектировании отказа.

### Пример формулировки

> При отключении одного из трёх инстансов `order-service` за 5 минут:
> - p95 latency остаётся < 300 ms (было 180 ms)
> - error rate не превышает 0.5%
> - throughput не падает больше чем на 5%

### Метрики для steady state

| Уровень | Примеры метрик |
|---------|----------------|
| Бизнес | Checkouts/min, signups/hour, revenue/sec |
| SLI | Request success rate, p99 latency |
| Infra | CPU, memory, network RTT |

### Ключевая идея

Мы пытаемся **опровергнуть** гипотезу: если steady state не сохранился — нашли слабое место. Если сохранился — повышаем уверенность. Чем труднее нарушить steady state, тем резилентнее система.

### Антипаттерн

Гипотеза "приложение не упадёт" — слишком расплывчата. Нужны числа и временные окна. Без steady state нельзя отличить успешный эксперимент от неудачного.


> [!mcq]
> - [ ] Steady State — это скриншот метрик за последнюю минуту перед экспериментом | ❌ ПОСЛЕДСТВИЕ: одна минута — слишком мало для baseline; нужна история за часы/дни чтобы исключить variance
> - [ ] Steady State достаточно описывать словесно ("система работает нормально") | ❌ ПОСЛЕДСТВИЕ: без числовых thresholds невозможно автоматически определить breach; CI/CD не сможет остановить эксперимент при отклонении
> - [ ] Steady State измеряется только технически (CPU, memory) | ❌ ПОСЛЕДСТВИЕ: principlesofchaos.org требуют business metrics (checkouts/min); CPU может расти без ухудшения user experience
> - [x] Steady State Hypothesis = конкретные бизнес- и технические метрики с пороговыми значениями до эксперимента; после — верифицировать что метрики остаются в пределах | ✓ ПРИМЕНЯТЬ: p95 latency < 300ms + error rate < 0.5% + throughput drop < 5% — конкретные thresholds 📋 ПРАВИЛО: hypothesis = pre-defined measurable outcome с acceptable deviation range 🔗 См. Q1

## Q4. (!) Что такое `Blast Radius` и как его контролировать?

**Blast Radius** — область воздействия эксперимента: сколько пользователей, запросов, инстансов, регионов он затронет. Минимизация blast radius — главный контракт безопасности хаос-инжиниринга.

### Стратегии минимизации

```mermaid
graph TB
    A[Blast Radius<br/>стратегии] --> B[Географическое<br/>ограничение]
    A --> C[Процентное<br/>ограничение]
    A --> D[Временное<br/>ограничение]
    A --> E[Таргетинг<br/>по атрибутам]

    B --> B1[один AZ / shard / cell]
    C --> C1[1% трафика → 5% → 25%]
    D --> D1[duration 30s, не часы]
    E --> E1[canary group<br/>internal users]
```

### Практические приёмы

- **Start small**: сначала 1 pod, потом namespace, потом кластер
- **Kill switch**: возможность немедленной остановки (`chaosctl stop`, удаление CR)
- **Time box**: эксперимент длится минуты, не часы
- **Feature flag**: завернуть chaos-инъекцию в флаг для включения/выключения
- **Canary**: направлять хаос на отдельную группу пользователей ([canary deployment](../cicd/deployment-strategies-interview.md))

### Пример в Chaos Mesh

```yaml
mode: fixed-percent
value: "10"   # только 10% pods с селектором
duration: "60s"
```

Пренебрежение blast radius — главная причина, почему хаос-эксперименты рушат прод и вызывают недоверие к практике.


> [!mcq]
> - [ ] Blast Radius = максимально широкий охват эксперимента для реалистичности | ❌ ПОСЛЕДСТВИЕ: широкий охват без контроля = инцидент в prod; принцип 5 требует minimize blast radius
> - [x] Blast Radius = область воздействия; контроль через: fixed-percent (10% pods), time box (60s duration), kill switch, canary group | ✓ ПРИМЕНЯТЬ: start 1% → 5% → 25% по мере набора уверенности; всегда иметь kill switch 📋 ПРАВИЛО: Blast Radius → minimize → start small → kill switch → time box 🔗 См. Q2
> - [ ] Blast Radius контролируется только в staging, в prod не нужен контроль | ❌ ПОСЛЕДСТВИЕ: prod эксперименты без blast radius control = риск P1 инцидента; принцип 5 требует minimize в prod особенно
> - [ ] Kill switch = автоматическая остановка при любом изменении метрик | ❌ ПОСЛЕДСТВИЕ: kill switch = ручная немедленная остановка; автоматическая остановка — это separate circuit breaker, kill switch должен быть доступен оператору

## Q5. `Chaos Engineering` vs `Testing` vs `Fault Injection`?

| Характеристика | Testing | Fault Injection | Chaos Engineering |
|----------------|---------|-----------------|-------------------|
| Цель | Проверить известное поведение | Проверить обработку известного сбоя | Найти неизвестные слабости |
| Среда | Dev/CI | Test/Staging | Staging + Production |
| Гипотеза | "Фича работает" | "Ошибка обрабатывается" | "Steady state сохраняется" |
| Масштаб | Unit, integration | Компонент | Вся система |
| Время | Детерминированное | Контролируемое | Продолжительное, continuous |

**Fault Injection** — подмножество техник, используемых в Chaos Engineering, но без формализма принципов (гипотеза, steady state, blast radius).

`Chaos Engineering` — это **процесс и дисциплина**, fault injection — **инструмент**. Можно использовать fault injection в unit-тестах ([unit-тесты](unit-testing-interview.md)) без всякого хаоса.


> [!mcq]
> - [ ] Chaos Engineering — это синоним Fault Injection (inject known fault → check handler) | ❌ ПОСЛЕДСТВИЕ: Fault Injection = проверка обработки ИЗВЕСТНОГО сбоя; Chaos = поиск неизвестных слабостей системы в целом
> - [ ] Chaos Engineering = Testing с нестабильным окружением | ❌ ПОСЛЕДСТВИЕ: Testing проверяет функциональные требования против known scenarios; Chaos строит уверенность через гипотезы о steady state
> - [ ] Fault Injection лучше Chaos Engineering — меньше риска | ❌ ПОСЛЕДСТВИЕ: это разные инструменты; Fault Injection не обнаруживает системные эффекты и cascading failures которые выявляет только Chaos в prod
> - [x] Testing = known requirements; Fault Injection = known fault handling; Chaos = unknown weaknesses через steady state hypothesis в реальных условиях | ✓ ПРИМЕНЯТЬ: Chaos дополняет, не заменяет Testing и Fault Injection 📋 ПРАВИЛО: Testing→CI; FaultInjection→staging; Chaos→prod с blast radius control 🔗 См. Q1

## Q6. Запускать эксперименты в проде или на staging?

Канон `principlesofchaos.org` требует продакшена, но на практике зрелость растёт постепенно.

### Уровни сред

```mermaid
graph LR
    A[Dev / Local] --> B[Staging]
    B --> C[Production Canary]
    C --> D[Production]
```

### Когда staging достаточен

- Первые эксперименты (команда ещё не доверяет процессу)
- Эксперименты с высоким риском (Chaos Kong на регион)
- Валидация новых типов атак перед выкаткой в прод

### Почему прод важен

- Реальный трафик (шум выявляет race conditions)
- Реальные конфигурации (секреты, лимиты, пулы)
- Реальные зависимости и их реакция
- Реальные пользователи и их поведение

### Компромисс: shadow traffic

Часть трафика зеркалится на эксперимент без воздействия на реальные ответы — близко к проду, но без риска.

Неверно: "у нас staging как прод". Почти никогда не так. Верно: "начинаем со staging, постепенно переходим в прод с малым blast radius".


> [!mcq]
> - [ ] Всегда запускать только в prod — staging бесполезен | ❌ ПОСЛЕДСТВИЕ: первые эксперименты без staging = слишком высокий риск; принцип «minimize blast radius» означает начать с безопасного окружения
> - [x] Начинать со staging для новых типов атак; постепенно переходить в prod с canary (малый % трафика); prod обязателен для реальных условий (трафик, зависимости, конфигурации) | ✓ ПРИМЕНЯТЬ: Dev→Staging→Prod Canary 1%→Prod 10%→Full prod матurity path 📋 ПРАВИЛО: staging → prod canary → prod; never skip blast radius control 🔗 См. Q4
> - [ ] Staging идентичен prod и экспериментов в prod вообще не нужно | ❌ ПОСЛЕДСТВИЕ: staging почти никогда не воспроизводит реальный трафик, конфигурации, зависимости; prod эксперименты с blast radius control необходимы
> - [ ] Запускать эксперименты в dev окружении — это достаточно | ❌ ПОСЛЕДСТВИЕ: dev не имеет real traffic patterns, реальных зависимостей, production-grade конфигураций; результаты не транслируются на prod

## Q7. (!) Что такое Netflix `Simian Army` и какие инструменты в него входят?

**Simian Army** — набор инструментов, созданный Netflix в 2011-2012 для симуляции отказов разных уровней. Chaos Monkey был первым; остальные "обезьяны" добавляли новые измерения хаоса.

### Основные инструменты

| Инструмент | Что делает |
|------------|------------|
| `Chaos Monkey` | Случайно убивает один EC2-инстанс в бизнес-часы |
| `Chaos Gorilla` | Отключает целую `Availability Zone` |
| `Chaos Kong` | Симулирует падение целого AWS-региона |
| `Latency Monkey` | Вносит искусственные задержки в REST-вызовы |
| `Doctor Monkey` | Выявляет нездоровые инстансы (CPU, memory) и выводит из сервиса |
| `Janitor Monkey` | Находит и убивает неиспользуемые ресурсы |
| `Conformity Monkey` | Проверяет нарушения правил конфигурации |
| `Security Monkey` | Ищет уязвимости в конфигурации безопасности |
| `10-18 Monkey` | Проверяет локализацию (i18n/l10n) |

### Статус

Часть Simian Army заморожена/устарела. Netflix перешёл к внутренней платформе **ChAP** (Chaos Automation Platform). Публичные активные проекты: [Chaos Monkey 2.0](https://github.com/Netflix/chaosmonkey) (интеграция со Spinnaker).

На собеседовании важно: Simian Army — это **исторический контекст** и источник терминологии. Сегодня для похожих задач используют Gremlin, Chaos Mesh, Litmus.


> [!mcq]
> - [ ] Simian Army = набор тестов для staging, не для prod | ❌ ПОСЛЕДСТВИЕ: Simian Army создан именно для prod; Chaos Monkey работал в prod в бизнес-часы чтобы команда могла реагировать
> - [ ] Chaos Monkey = Simian Army, это одно и то же | ❌ ПОСЛЕДСТВИЕ: Chaos Monkey — первый и базовый инструмент; Simian Army = коллекция из 7+ инструментов разных уровней (Latency Monkey, Security Monkey, etc.)
> - [x] Simian Army — набор Netflix-инструментов 2011-2012: Chaos Monkey (инстансы), Latency Monkey (задержки), Security Monkey (уязвимости), Janitor Monkey (ресурсы) | ✓ ПРИМЕНЯТЬ: исторический контекст; сегодня используют Gremlin/Chaos Mesh/Litmus 📋 ПРАВИЛО: Simian Army = Netflix chaos toolset; каждая «обезьяна» = отдельный failure domain 🔗 См. Q8
> - [ ] Simian Army активно развивается Netflix сегодня | ❌ ПОСЛЕДСТВИЕ: большинство Simian Army инструментов заморожены/устарели; Netflix перешёл на ChAP и интегрировал Chaos Monkey 2.0 со Spinnaker

## Q8. Как появился `Chaos Monkey` и какую задачу он решает?

В 2010-2011 Netflix мигрировал на AWS. Команда быстро поняла: инстансы в облаке пропадают **без предупреждения**, и код, не готовый к этому, ломается в проде. Вместо того чтобы "надеяться на лучшее", инженеры решили: пусть инстансы падают **каждый день** — так код придётся писать резистентным изначально.

### Механика

- Работает в бизнес-часы (чтобы команда могла реагировать)
- Случайно выбирает один инстанс в AWS Auto Scaling Group
- Терминирует его
- Следит за восстановлением (replica в ASG создаст замену)

### Чему учит

1. Все инстансы должны быть **stateless** (или корректно восстанавливать state).
2. Пулы соединений должны переоткрываться при потере.
3. Балансировщик должен быстро исключать мёртвый инстанс.
4. Клиенты должны делать retry с jitter.

### Эволюция

- **Chaos Monkey 1.0** (2012, открыт по Apache 2.0): attack на AWS ASG.
- **Chaos Monkey 2.0** (2016): переработан на Go, интеграция со Spinnaker.
- **Chaos Monkey for Spring Boot** (codecentric): не связан с Netflix, приложение-уровневый хаос в Java (Q16).

На интервью: "в чём суть Chaos Monkey" — в **тренировке кода быть готовым к падению инстансов**, а не просто в "убийстве серверов".


> [!mcq]
> - [ ] Chaos Monkey создан для testing known failures заранее определённых инстансов | ❌ ПОСЛЕДСТВИЕ: Chaos Monkey случайно выбирает инстанс в ASG — именно случайность заставляет код быть resilient в general
> - [ ] Chaos Monkey работает только в dev/staging чтобы не аффектить prod | ❌ ПОСЛЕДСТВИЕ: Chaos Monkey работает в prod в бизнес-часы (когда команда может реагировать) — это принципиально для реальных условий
> - [ ] Chaos Monkey — инструмент для load testing; убивает инстансы под нагрузкой | ❌ ПОСЛЕДСТВИЕ: Chaos Monkey = instance termination для проверки resilience; load testing = отдельная практика с другим инструментарием
> - [x] Chaos Monkey случайно терминирует один инстанс в ASG в бизнес-часы; цель — заставить код быть stateless и resilient к instance loss by design | ✓ ПРИМЕНЯТЬ: когда нужно проверить что ASG auto-healing, connection pools и retry работают 📋 ПРАВИЛО: Chaos Monkey = random instance kill → команда ДОЛЖНА быть готова 🔗 См. Q7

## Q9. Что такое `Chaos Kong` и `Chaos Gorilla`?

Это тяжёлая артиллерия Netflix Simian Army, действующая на уровнях **AZ** и **region**.

### Chaos Gorilla

- Выключает целую `Availability Zone` (AZ) в AWS
- Проверяет: умеет ли сервис переносить потерю AZ (multi-AZ deployment, cross-AZ routing)
- Типичная проблема: `master` базы в упавшей AZ, нет автоматического failover

### Chaos Kong

- Выключает целый `Region` (например, `us-east-1`)
- Проверяет: работает ли глобальный failover, DNS-routing, cross-region replication
- Запускается редко (ежеквартально у Netflix) — большой blast radius и сложная подготовка

### Эскалация

```mermaid
graph BT
    A[Chaos Monkey<br/>1 instance] --> B[Chaos Gorilla<br/>1 AZ]
    B --> C[Chaos Kong<br/>1 Region]
```

Каждый уровень требует своего уровня зрелости архитектуры: Chaos Monkey — резилентные сервисы; Gorilla — multi-AZ; Kong — multi-region active-active. Смотри также [распределённые системы](../architecture/distributed-systems-interview.md).


> [!mcq]
> - [ ] Chaos Gorilla = убивает 1 instance (как Chaos Monkey) | ❌ ПОСЛЕДСТВИЕ: Chaos Gorilla убивает целую Availability Zone — это на порядок выше по blast radius; Chaos Monkey = 1 instance
> - [ ] Chaos Kong = тест одного инстанса Kafka | ❌ ПОСЛЕДСТВИЕ: Chaos Kong = симуляция отказа целого AWS regional; Kafka instance kill = уровень Chaos Monkey
> - [x] Chaos Gorilla = отключает одну AZ; Chaos Kong = симулирует отказ целого региона; иерархия: Monkey→Gorilla→Kong по масштабу blast radius | ✓ ПРИМЕНЯТЬ: Gorilla — multi-AZ failover тест; Kong — multi-region active-active 📋 ПРАВИЛО: Monkey=instance, Gorilla=AZ, Kong=region — blast radius hierarchy 🔗 См. Q4
> - [ ] Chaos Kong и Chaos Gorilla — синонимы для крупных экспериментов | ❌ ПОСЛЕДСТВИЕ: разный масштаб: Gorilla = 1 AZ (один датацентр), Kong = 1 region (несколько AZ); принципиально разные требования к архитектуре

## Q10. (!) Какие типы хаос-экспериментов существуют?

Эксперименты классифицируют по слою инфраструктуры, на который действует инъекция.

### Таксономия

| Категория | Примеры |
|-----------|---------|
| **Infrastructure / State** | kill pod/VM, restart, disk full, clock skew |
| **Network** | latency, packet loss, partition, DNS failure, bandwidth limit |
| **Resource / Stress** | CPU stress, memory stress, IO stress |
| **Application** | exception injection, slow responses, invalid payload |
| **Dependency** | DB slow down, cache miss, 3rd-party API down |
| **Data** | corruption, schema mismatch, eventual consistency lag |
| **Security** | certificate expiry, IAM role revocation |
| **Regional** | AZ down, region down, cross-region partition |

### Матрица выбора

```mermaid
graph TB
    A[Что хочу проверить?] --> B{Категория}
    B -->|Resilience к падению| C[Infrastructure]
    B -->|Reaction на lag| D[Network / Latency]
    B -->|Graceful degradation| E[Dependency]
    B -->|Autoscaling| F[Resource]
    B -->|Retry logic| G[Application]
```

Хороший план экспериментов содержит смесь из нескольких категорий, покрывая разные сценарии отказов.


> [!mcq]
> - [ ] Хаос-эксперименты — только kill pod/VM, остальные типы не считаются chaos | ❌ ПОСЛЕДСТВИЕ: таксономия включает 7+ категорий: Network, Resource, Application, Dependency, Data, Security, Regional
> - [ ] Все типы экспериментов одинаково полезны — начинать можно с любого | ❌ ПОСЛЕДСТВИЕ: нужно приоритизировать по вероятности и потенциальному импакту; обычно начинают с Network Latency как самого частого реального сбоя
> - [x] Infrastructure (kill pod), Network (latency/partition), Resource (CPU/memory stress), Application (exception injection), Dependency (DB slowdown), Regional (AZ down) | ✓ ПРИМЕНЯТЬ: начинать с Network Latency — самый частый источник cascading failures 📋 ПРАВИЛО: таксономия = Infrastructure→Network→Resource→App→Dependency→Data→Regional 🔗 См. Q4
> - [ ] Data corruption — не chaos эксперимент, это ошибка | ❌ ПОСЛЕДСТВИЕ: Data chaos (corruption, schema mismatch, eventual consistency lag) = отдельная категория для проверки data resilience и validation layers

## Q11. Как устроен эксперимент с `network latency`?

Цель — проверить, как сервис реагирует на медленные ответы зависимостей (DB, внешние API, соседние сервисы). Это один из самых полезных экспериментов: **таймауты и retry** — главный источник каскадных отказов.

### Инструменты

- `tc` (Linux traffic control, `tc qdisc add dev eth0 root netem delay 200ms`)
- `Toxiproxy` (см. Q28)
- `Pumba` (для Docker, Q27)
- `Chaos Mesh NetworkChaos` (K8s, см. Q23)
- `Gremlin Latency` attack

### Что измеряем

- p95/p99 latency downstream-сервиса до и после инъекции
- Срабатывание таймаутов (должны быть короче, чем latency инъекции)
- Активация [circuit breaker](../architecture/resilience-patterns-interview.md)
- Успешный fallback в кэш или default value

### Типичные находки

- Таймаут больше, чем `SLA` → запросы ждут долго и копятся
- Нет circuit breaker → каскадный отказ
- Retry без jitter → retry storm
- Нет fallback → полный blackout фичи

Эксперименты с latency часто выявляют проблемы, которые не видны при падении сервиса: деградация хуже, чем полная недоступность, потому что ломает тайминги.


> [!mcq]
> - [ ] Network latency chaos = packet loss; latency и loss — одно и то же | ❌ ПОСЛЕДСТВИЕ: latency = задержка (tc netem delay 200ms); packet loss = потеря пакетов (tc netem loss 10%); разные эффекты на timeout и retry поведение
> - [ ] Network latency инжектируется только через TCP-уровень, не HTTP | ❌ ПОСЛЕДСТВИЕ: tc/Toxiproxy работает на network level аффектя все протоколы; Chaos Mesh NetworkChaos — Kubernetes-native уровень
> - [ ] Latency chaos и timeout chaos — одно и то же | ❌ ПОСЛЕДСТВИЕ: latency chaos = инжектируем задержку; timeout chaos = блокируем ответ полностью; разные сценарии: медленный сервис vs. зависший сервис
> - [x] Network latency chaos: инжектировать 200-500ms задержку в вызовы зависимостей; проверить срабатывание таймаутов, circuit breaker, fallback и retry с backoff | ✓ ПРИМЕНЯТЬ: начать с latency — самый частый паттерн cascading failure 📋 ПРАВИЛО: inject latency > timeout threshold → должен срабатывать circuit breaker 🔗 См. Q10

## Q12. Что такое `DNS chaos` и как его тестировать?

**DNS chaos** инжектирует сбои в резолвинге доменных имён: замедление, failure, неправильные ответы. Это важный класс экспериментов, потому что DNS — часто **единая точка отказа**, а приложения плохо его кэшируют.

### Сценарии

- DNS resolver недоступен → как ведёт себя `nslookup`-вызов в коде
- TTL истёк и свежий запрос падает
- Возвращается NXDOMAIN или wrong IP
- Latency в ответах DNS

### Инструменты

```yaml
# Chaos Mesh DNSChaos
apiVersion: chaos-mesh.org/v1alpha1
kind: DNSChaos
metadata:
  name: dns-chaos-example
spec:
  action: error
  mode: all
  patterns:
    - "payment-service.*"
  selector:
    namespaces:
      - production
```

### Что проверяем

- Наличие DNS-кэша в приложении (например, JVM `networkaddress.cache.ttl`)
- Таймауты на DNS-резолвинг
- Graceful degradation при недоступности некритичных зависимостей
- Алерты на рост DNS-ошибок

В JVM частый баг: `networkaddress.cache.ttl=-1` (кэш навсегда) приводит к тому, что после failover зависимости приложение всё ещё стучится в старый IP.


> [!mcq]
> - [ ] DNS chaos = только проверка что DNS отвечает; сбои резолвинга редки в prod | ❌ ПОСЛЕДСТВИЕ: DNS — часто единая точка отказа в cloud; DNS failure вызывает полный blackout зависимостей если нет fallback
> - [ ] JVM кэширует DNS автоматически с правильным TTL — проблем нет | ❌ ПОСЛЕДСТВИЕ: JVM по умолчанию `networkaddress.cache.ttl=-1` (кэш навсегда); после failover зависимости JVM продолжает обращаться на старый IP
> - [x] DNS chaos: проверить DNS-кэш в приложении, таймауты на резолвинг, graceful degradation при NXDOMAIN; Chaos Mesh DNSChaos, tc, dnsmasq | ✓ ПРИМЕНЯТЬ: перед каждым failover тестом — убедиться что JVM `networkaddress.cache.ttl` настроен (60s) 📋 ПРАВИЛО: DNS chaos = verify cache TTL + timeout + fallback при resolver failure 🔗 См. Q10
> - [ ] DNS chaos = network packet loss на порт 53 | ❌ ПОСЛЕДСТВИЕ: packet loss на DNS port = один из методов; DNS chaos шире: NXDOMAIN, wrong IP, latency, SERVFAIL — разные failure modes с разным поведением

## Q13. Как тестировать `disk fill` и IO chaos?

Цель — проверить поведение при **исчерпании диска** и **медленных IO-операциях**: логи, временные файлы, БД, swap.

### Типы IO-экспериментов

| Тип | Эффект |
|-----|--------|
| Disk fill | Заполнить диск до порога (95%, 100%) |
| IO latency | Задержка чтения/записи |
| IO error | Возврат `EIO` на операциях |
| Read-only | Монтируем raffle-файловую систему как RO |

### Chaos Mesh пример

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: IOChaos
metadata:
  name: io-latency
spec:
  action: latency
  mode: one
  selector:
    labelSelectors:
      app: orders
  volumePath: /var/lib/orders
  path: /var/lib/orders/**/*
  delay: "100ms"
  percent: 50
  duration: "60s"
```

### Что проверяем

- Лог-ротация работает (иначе `/var/log` переполнится первым)
- БД умеет gracefully отключать writes при полном диске
- Метрики и алерты срабатывают заранее (порог 80%, не 99%)
- Приложение не падает при невозможности записать лог

Типичная находка: при `disk full` сервис падает с `OutOfDiskSpaceException`, потому что не может логировать стек-трейс самой ошибки.


> [!mcq]
> - [ ] Disk fill chaos = заполнить диск на 100% — иначе нет эффекта | ❌ ПОСЛЕДСТВИЕ: 100% disk fill часто крэшит БД и OS; нужно тестировать при 80-90% и убедиться что алерты срабатывают до катастрофы
> - [ ] IO chaos = disk fill; это одно и то же | ❌ ПОСЛЕДСТВИЕ: IO chaos = медленные операции чтения/записи (IO delay, throttling); disk fill = нет свободного места; разные failure modes
> - [x] Disk fill: проверить log rotation, алерты при 80% disk usage, graceful degradation при невозможности записать; IO delay: проверить таймауты и backpressure при медленных дисках | ✓ ПРИМЕНЯТЬ: IOChaos в Chaos Mesh, fallocate для disk fill, stress-ng для IO throughput limit 📋 ПРАВИЛО: disk chaos = fill 80% + verify alerts + verify app survives 🔗 См. Q10
> - [ ] Приложение всегда корректно обрабатывает disk full — стандартная JVM гарантирует это | ❌ ПОСЛЕДСТВИЕ: типичная находка: JVM падает с OutOfDiskSpaceException пытаясь записать лог об ошибке — circular failure

## Q14. Как проводить CPU/memory stress-эксперименты?

**Stress-эксперименты** нагружают ресурсы узла, чтобы проверить:
1. Реакцию `autoscaling` (HPA в K8s, ASG в AWS)
2. Корректность `resource limits` и `requests`
3. Поведение `OOM killer` и graceful degradation
4. Работу `backpressure` в reactive-системах

### Chaos Mesh StressChaos

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: StressChaos
metadata:
  name: memory-stress
spec:
  mode: one
  selector:
    labelSelectors:
      app: api
  stressors:
    memory:
      workers: 4
      size: "1GB"
    cpu:
      workers: 2
      load: 80
  duration: "120s"
```

### Что смотрим

- HPA добавляет реплики или нет?
- Если нет, срабатывает ли `PodDisruptionBudget`?
- JVM `GC` начинает молотить — приложение замирает?
- Соседние pod-ы на том же ноде не затронуты (`resource limits`)?
- `readinessProbe` снимает трафик с перегруженного pod-а?

### Типичные находки

- Нет limits → один pod съедает ресурсы ноды, сыпятся все соседи (noisy neighbour)
- `HPA` по CPU, а bottleneck — memory → не масштабирует
- GC pause > readiness timeout → pod помечается как unhealthy и рестартится циклически


> [!mcq]
> - [ ] CPU stress = load testing; это одно и то же | ❌ ПОСЛЕДСТВИЕ: load testing = реальный трафик; CPU stress chaos = искусственный ресурс stress для проверки autoscaling и graceful degradation без трафика
> - [x] CPU/memory stress: проверить HPA реакцию, resource limits (noisy neighbour), OOM killer поведение, readinessProbe снятие трафика с перегруженного pod | ✓ ПРИМЕНЯТЬ: Chaos Mesh StressChaos; убедиться limits/requests выставлены, HPA настроен по правильной метрике 📋 ПРАВИЛО: stress chaos = autoscaling trigger test + noisy neighbour isolation check 🔗 См. Q10
> - [ ] Memory stress нужен только для JVM приложений | ❌ ПОСЛЕДСТВИЕ: memory stress важен для любых сервисов; Go/Rust/Node тоже имеют memory limits и могут быть OOM killed
> - [ ] Если HPA настроен, memory stress всегда завершается безопасно | ❌ ПОСЛЕДСТВИЕ: HPA по CPU не масштабирует при memory bottleneck; частая находка: HPA по CPU metric, сервис падает по memory — HPA не срабатывает

## Q15. Что такое `dependency failure` и как её инжектировать?

**Dependency failure** — отказ внешней зависимости (DB, cache, message broker, 3rd-party API). Самый бизнес-релевантный класс экспериментов, потому что микросервисы живут в паутине зависимостей.

### Способы инжектировать

1. **На клиенте**: `Toxiproxy` между приложением и зависимостью
2. **На сервере**: остановить real dependency (kill pod Redis)
3. **На уровне сети**: `iptables DROP` к порту зависимости
4. **Mock на границе**: WireMock, возвращающий 500

### Пример с Toxiproxy в Spring Boot тесте

```java
@Test
void whenRedisDown_fallbackToDatabase() {
    // Toxiproxy перекрывает трафик к Redis
    redisProxy.toxics().timeout("down", ToxicDirection.DOWNSTREAM, 0);

    UserDto user = userService.findById(42L);

    assertThat(user).isNotNull();
    // метрика fallback должна был инкремент
    assertThat(fallbackCounter.count()).isEqualTo(1.0);
}
```

### Что проверяем

- Graceful degradation: если кэш упал — читаем из БД
- Circuit breaker срабатывает за разумное время
- Bulkhead не даёт одной зависимости утянуть весь пул потоков
- Клиент использует retry с jitter и exponential backoff

Смотри [resilience-паттерны](../architecture/resilience-patterns-interview.md) для подробностей по circuit breaker, bulkhead, timeout.


> [!mcq]
> - [ ] Dependency failure = unit test с mock зависимости | ❌ ПОСЛЕДСТВИЕ: unit test с mock проверяет known error path; chaos dependency failure проверяет неизвестные эффекты в реальных условиях: timeout cascade, thread pool exhaustion
> - [x] Dependency failure: инжектировать через Toxiproxy/WireMock/NetworkChaos; проверить circuit breaker, bulkhead изоляцию, fallback, retry с backoff | ✓ ПРИМЕНЯТЬ: Redis down → fallback to DB; третья сторона 500 → cached response / degraded mode 📋 ПРАВИЛО: dependency chaos = inject failure + verify isolation + verify graceful degradation 🔗 См. Q11
> - [ ] Dependency failure = отключить весь service mesh — самый realistic тест | ❌ ПОСЛЕДСТВИЕ: отключение всего service mesh = слишком большой blast radius; нужно инжектировать одну зависимость at a time с blast radius control
> - [ ] Если есть circuit breaker — dependency failure эксперименты не нужны | ❌ ПОСЛЕДСТВИЕ: circuit breaker нужно тестировать: правильно ли настроен threshold, срабатывает ли timeout, работает ли fallback; код без chaos тестирования не даёт уверенности

## Q16. (!) Как подключить `Chaos Monkey` к Spring Boot приложению?

`Chaos Monkey for Spring Boot` (`codecentric/chaos-monkey-spring-boot`) — библиотека application-level хаоса в Java: инжектирует задержки, исключения, убивает приложение.

### Подключение

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>chaos-monkey-spring-boot</artifactId>
    <version>3.0.2</version>
</dependency>
```

### Активация профилем

```bash
java -jar app.jar --spring.profiles.active=chaos-monkey
```

### Базовый `application.yml`

```yaml
chaos:
  monkey:
    enabled: true
    watcher:
      controller: false
      restController: true
      service: true
      repository: false
      component: false
    assaults:
      level: 5            # каждый 5-й вызов
      latencyActive: true
      latencyRangeStart: 2000
      latencyRangeEnd: 5000
      exceptionsActive: false
      killApplicationActive: false

management:
  endpoint:
    chaosmonkey:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,chaosmonkey
```

### Отличие от Netflix Chaos Monkey

| Netflix Chaos Monkey | Codecentric CM for Spring Boot |
|-----------------------|---------------------------------|
| Infrastructure-level (EC2 kill) | Application-level (методы, latency) |
| Интеграция со Spinnaker/AWS | Интеграция со Spring Boot |
| Go | Java |

Для integration/load-тестов Spring Boot-приложения — идеально, потому что хаос инжектируется внутрь JVM без внешних инструментов.


> [!mcq]
> - [ ] Chaos Monkey for Spring Boot = Netflix Chaos Monkey; это одна библиотека | ❌ ПОСЛЕДСТВИЕ: разные проекты: Netflix CM = infrastructure-level (EC2 kill, Go); Codecentric CM = application-level (JVM/Spring methods, Java)
> - [ ] Chaos Monkey для Spring Boot активируется любым профилем Spring | ❌ ПОСЛЕДСТВИЕ: требует активации профиля `chaos-monkey` (`--spring.profiles.active=chaos-monkey`) — без него библиотека пассивна
> - [x] Chaos Monkey for Spring Boot: application-level хаос в JVM; активируется профилем chaos-monkey; настраивается watchers (где) + assaults (что: latency/exception/kill) через YAML или Actuator | ✓ ПРИМЕНЯТЬ: для integration тестов внутри JVM без внешних инструментов 📋 ПРАВИЛО: CM4SB = watchers(где) + assaults(что) + level(частота) 🔗 См. Q16
> - [ ] Kill Application Assault корректен для production хаоса — убивает процесс | ❌ ПОСЛЕДСТВИЕ: killApplicationActive=true в prod = намеренный crash; использовать только в dev/staging с пониманием последствий и kill switch

## Q17. (!) Что такое `Assaults` и `Watchers` в Chaos Monkey?

**Watchers** определяют **где** срабатывает хаос — какие типы Spring-бинов будут атакованы. **Assaults** определяют **что** произойдёт — какой тип атаки применится.

### Watchers

| Watcher | Бины, которые он отлавливает |
|---------|-------------------------------|
| `@Controller` | контроллеры MVC |
| `@RestController` | REST-контроллеры |
| `@Service` | сервисный слой |
| `@Repository` | репозитории / DAO |
| `@Component` | общие компоненты |
| Custom `@ChaosMonkeyAnnotation` | любые методы, помеченные аннотацией |
| Actuator Watcher | endpoints, включая custom |

### Assaults

| Assault | Эффект |
|---------|--------|
| `Latency Assault` | Добавляет задержку (min-max ms) перед возвратом |
| `Exception Assault` | Выбрасывает RuntimeException (настраиваемый) |
| `Kill Application Assault` | Вызывает `System.exit(1)` — убивает процесс |
| `Memory Assault` | Заполняет heap до percentage |
| `CPU Assault` | Нагружает CPU до percentage |

### Концепция "level"

`assaults.level: 5` означает "атакуй каждый 5-й вызов метода, подпадающего под watcher". Можно задать `deterministic: true` (каждый N-й) или случайный выбор.

```mermaid
graph LR
    A[HTTP Request] --> B[RestController<br/>@watcher]
    B --> C{Chaos<br/>Monkey<br/>level=5?}
    C -->|каждый 5-й| D[Assault<br/>latency/exception]
    C -->|остальные| E[Обычная<br/>обработка]
    D --> F[Response]
    E --> F
```


> [!mcq]
> - [ ] Watchers = что будет происходить (action); Assaults = где будет происходить (target) | ❌ ПОСЛЕДСТВИЕ: наоборот: Watchers = где (какие бины); Assaults = что (latency/exception/kill/memory/CPU)
> - [x] Watchers: @RestController, @Service, @Repository, custom beans — определяют где; Assaults: Latency, Exception, Kill, Memory, CPU — определяют что; level = каждый N-й вызов | ✓ ПРИМЕНЯТЬ: включить watcher только на @Service для точечного blast radius; level=10 для low-impact 📋 ПРАВИЛО: Watcher=target, Assault=action, Level=frequency 🔗 См. Q16
> - [ ] level=1 безопаснее чем level=5 | ❌ ПОСЛЕДСТВИЕ: level=1 атакует КАЖДЫЙ вызов — максимальный impact; level=5 атакует каждый 5-й; меньший level = больший chaos blast
> - [ ] Exception Assault выбрасывает только checked exceptions | ❌ ПОСЛЕДСТВИЕ: Exception Assault по умолчанию RuntimeException (unchecked); можно настроить любой тип исключения через exception.type в YAML

## Q18. Как настроить `Latency Assault` и `Exception Assault`?

### Latency Assault

Вносит случайную задержку в диапазоне `[latencyRangeStart, latencyRangeEnd]` ms перед выполнением метода.

```yaml
chaos:
  monkey:
    assaults:
      level: 3
      latencyActive: true
      latencyRangeStart: 1000
      latencyRangeEnd: 3000
    watcher:
      restController: true
```

Эффект: каждый 3-й HTTP запрос "тормозит" на 1-3 секунды. Полезно для тестирования таймаутов на upstream-клиентах.

### Exception Assault

Выбрасывает исключение вместо выполнения метода.

```yaml
chaos:
  monkey:
    assaults:
      level: 10
      exceptionsActive: true
      exception:
        type: "java.lang.RuntimeException"
        arguments:
          - className: "java.lang.String"
            value: "Chaos Monkey - RuntimeException"
```

Эффект: каждый 10-й вызов service-слоя кидает RuntimeException. Проверяет обработку ошибок, error handlers, алерты.

### Комбинация

Можно активировать несколько assaults одновременно — выбор случайный из активных.

### Watcher для конкретных бинов

С версии 2.4+ можно ограничить watchers списком бинов:

```yaml
chaos:
  monkey:
    watcher:
      beans:
        - "com.example.OrderService#placeOrder"
```

Это критично для точечных экспериментов и управления blast radius внутри приложения.


> [!mcq]
> - [ ] Latency Assault добавляет фиксированную задержку для всех вызовов | ❌ ПОСЛЕДСТВИЕ: задержка случайна в диапазоне [latencyRangeStart, latencyRangeEnd]; фиксированная задержка = неправдоподобна для production
> - [ ] Exception Assault всегда выбрасывает один тип исключения — RuntimeException | ❌ ПОСЛЕДСТВИЕ: тип настраивается через exception.type в YAML; можно задать любой Throwable включая кастомные
> - [ ] Комбинирование нескольких assaults невозможно | ❌ ПОСЛЕДСТВИЕ: можно активировать несколько assaults одновременно (latencyActive+exceptionsActive=true); выбор между активными — случайный при каждом вызове
> - [x] Latency Assault: случайная задержка [start, end] ms перед методом; Exception Assault: выброс configurable exception; оба активируются через assaults config + watcher | ✓ ПРИМЕНЯТЬ: Latency для timeout тестов upstream; Exception для error handler валидации 📋 ПРАВИЛО: latencyActive + exceptionsActive = оба активны, random выбор 🔗 См. Q17

## Q19. Как управлять `Chaos Monkey` через Actuator в runtime?

Chaos Monkey предоставляет Spring Boot Actuator endpoint `/actuator/chaosmonkey` для управления без рестарта приложения.

### Ключевые endpoints

```bash
# статус
GET /actuator/chaosmonkey/status

# включить
POST /actuator/chaosmonkey/enable

# выключить
POST /actuator/chaosmonkey/disable

# текущая конфигурация
GET /actuator/chaosmonkey

# обновить assaults
POST /actuator/chaosmonkey/assaults
Content-Type: application/json
{
  "level": 5,
  "latencyActive": true,
  "latencyRangeStart": 500,
  "latencyRangeEnd": 1500,
  "exceptionsActive": false
}
```

### Почему это важно

- Эксперимент в **runtime**: включили, понаблюдали, выключили
- Kill switch для немедленной остановки при выходе за abort condition
- Интеграция с внешним оркестратором (CI, GameDay tooling)

### Безопасность

⚠️ Actuator endpoint должен быть защищён: в проде — Basic Auth или mTLS. Публично открытый `/chaosmonkey/enable` — это готовый DoS.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,chaosmonkey
  security:
    enabled: true
```


> [!mcq]
> - [ ] Actuator endpoint `/chaosmonkey` можно открыть публично — это только для тестирования | ❌ ПОСЛЕДСТВИЕ: публично открытый `/chaosmonkey/enable` = готовый DoS вектор; в prod защищать Basic Auth или mTLS
> - [ ] Actuator для Chaos Monkey работает только в dev профиле | ❌ ПОСЛЕДСТВИЕ: Actuator endpoint работает в любом профиле если chaos-monkey активен; именно поэтому нужна security конфигурация в prod
> - [x] Actuator `/chaosmonkey` позволяет enable/disable, изменять assaults и watchers в runtime без рестарта; это kill switch для немедленной остановки | ✓ ПРИМЕНЯТЬ: runtime management во время GameDay; защитить Auth; интегрировать в CI pipeline 📋 ПРАВИЛО: Actuator = runtime kill switch + dynamic assault config; always secure in prod 🔗 См. Q17
> - [ ] Через Actuator можно менять только latency настройки, не watchers | ❌ ПОСЛЕДСТВИЕ: /chaosmonkey/assaults и /chaosmonkey/watchers — оба endpoints доступны для runtime изменений

## Q20. Когда использовать `@ChaosMonkeyAnnotation`?

Стандартные watchers работают по стереотипам Spring (`@Service`, `@Repository`). Если нужен **точечный контроль** над конкретными методами, используем `@ChaosMonkeyAnnotation`.

### Создание кастомной аннотации

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ChaosMonkeyAnnotation
public @interface FaultyPayment {
}
```

### Использование

```java
@Service
public class PaymentService {

    @FaultyPayment
    public PaymentResult charge(Order order) {
        // ...
    }
}
```

### Активация watcher

```yaml
chaos:
  monkey:
    watcher:
      customAnnotations:
        - "com.example.chaos.FaultyPayment"
```

### Когда это критично

- Атаковать только один метод из большого сервиса
- Смешивать уровни атаки (разные level для разных методов — через несколько кастомных аннотаций)
- Документировать явно: "этот метод участвует в хаос-экспериментах"
- Точно контролировать blast radius

Антипаттерн: включать watcher на весь `@Service` слой в большом монолите — атака расползается непредсказуемо.


> [!mcq]
> - [ ] @ChaosMonkeyAnnotation нужна только для @Component бинов | ❌ ПОСЛЕДСТВИЕ: аннотация работает на любых Spring-managed бинах и методах; основная цель — точечный blast radius для конкретных методов в любых бинах
> - [ ] Включить watcher @Service + @Repository одновременно безопасно в монолите | ❌ ПОСЛЕДСТВИЕ: включение всего Service+Repository слоя в большом монолите = непредсказуемый blast radius; chaos расползается на все методы
> - [x] @ChaosMonkeyAnnotation на конкретный метод — когда нужен точечный blast radius; документирует участие метода в chaos экспериментах | ✓ ПРИМЕНЯТЬ: атаковать только PaymentService#charge без затрагивания всего @Service слоя 📋 ПРАВИЛО: custom annotation = surgical blast radius control = explicit chaos documentation 🔗 См. Q17
> - [ ] @ChaosMonkeyAnnotation заменяет стандартные watchers — не нужны оба | ❌ ПОСЛЕДСТВИЕ: custom annotations — дополнение к watcher стереотипам; можно комбинировать, каждый имеет своё назначение

## Q21. (!) Что такое `Chaos Mesh` и какие fault types он поддерживает?

**Chaos Mesh** — open-source платформа хаос-инжиниринга для Kubernetes, созданная PingCAP. CNCF Incubating проект. Описывает эксперименты как Custom Resources (CR), что позволяет хранить их в Git и применять через `kubectl apply`.

### Fault types

| Тип | Описание |
|-----|----------|
| `PodChaos` | pod-failure, pod-kill, container-kill |
| `NetworkChaos` | delay, loss, duplicate, corrupt, partition, bandwidth |
| `IOChaos` | latency, fault, attrOverride, mistake (на filesystem) |
| `StressChaos` | CPU и memory stressors |
| `TimeChaos` | искажение системного времени |
| `DNSChaos` | error, random на DNS-запросах |
| `HTTPChaos` | abort, delay, replace на HTTP-трафике |
| `JVMChaos` | exception, latency, GC, return на уровне JVM |
| `KernelChaos` | fault injection в системные вызовы |
| `AWSChaos` / `GCPChaos` | операции на облачных ресурсах |

### Архитектура

```mermaid
graph TB
    Dashboard[Chaos Dashboard] --> Controller[Chaos Controller<br/>Manager]
    Controller --> Daemon1[Chaos Daemon<br/>Node 1]
    Controller --> Daemon2[Chaos Daemon<br/>Node 2]
    Daemon1 --> Pod1[Target Pod]
    Daemon2 --> Pod2[Target Pod]
    CR[ChaosMesh CR<br/>YAML] --> Controller
```

### Преимущества

- Rich UI dashboard для визуализации
- Workflow (цепочки экспериментов)
- Schedule (periodic chaos)
- RBAC и multi-tenancy


> [!mcq]
>
> **Что такое Chaos Mesh и какие fault types он поддерживает?**
>
> ---
>
> #### A) Chaos Mesh — это набор bash-скриптов, запускаемых по cron — ❌ Неверно
>
> **Что на самом деле:** Chaos Mesh — это Kubernetes-нативная платформа от PingCAP, построенная на CRD (Custom Resource Definitions). Эксперименты описываются декларативными YAML-манифестами, которые контроллер применяет как обычные ресурсы кластера (`kubectl apply -f podchaos.yaml`). Никаких bash, никаких внешних cron-демонов: всё интегрировано в lifecycle Kubernetes, контроллер сам отслеживает состояние и применяет fault.
>
> **Откуда путаница:** в pre-K8s эпоху chaos-инструменты часто действительно были набором скриптов (Chaos Monkey изначально — Spinnaker pipeline с shell). Если перенести эту модель в современный кластер, теряется RBAC, audit, declarative state, и эксперимент становится «сторонним процессом» вместо first-class объекта инфраструктуры.
>
> ---
>
> #### B) Chaos Mesh поддерживает только pod-kill — ❌ Неверно
>
> **Что на самом деле:** Chaos Mesh покрывает 10+ типов сбоев на разных уровнях:
>
> | Уровень | CRD | Что инжектирует |
> |---|---|---|
> | Pod | `PodChaos` | kill / failure / container-kill |
> | Сеть | `NetworkChaos` | latency / loss / corrupt / partition / bandwidth |
> | Диск/IO | `IOChaos` | задержка чтения/записи, ошибки |
> | CPU/Memory | `StressChaos` | искусственная нагрузка |
> | DNS | `DNSChaos` | подмена / задержка резолва |
> | Время | `TimeChaos` | сдвиг системного времени в pod |
> | Ядро | `KernelChaos` | инъекция ошибок в syscalls |
> | HTTP | `HTTPChaos` | задержки / abort на HTTP уровне |
> | JVM | `JVMChaos` | byteman-based injection (exception, latency) |
> | AWS | `AWSChaos` | EC2 stop, EBS detach |
>
> **Откуда путаница:** многие знают Chaos Mesh по статьям о pod-kill (самый зрелищный пример). На практике сценарии deeper failures — DNS, time skew, JVM exception — выявляют тоньше скрытые баги, чем pod kill.
>
> ---
>
> #### C) Chaos Mesh работает только на bare-metal Kubernetes, не в облаке — ❌ Неверно
>
> **Что на самом деле:** Chaos Mesh cloud-agnostic. Работает на любом K8s-дистрибутиве — EKS, GKE, AKS, OpenShift, K3s, on-prem kubeadm. Единственное требование — Linux-узлы (chaos-daemon — это DaemonSet, который использует Linux namespaces, cgroups, tc/iptables для инжекции).
>
> **Откуда путаница:** в managed-кластерах (EKS, GKE) часть привилегированных операций может быть ограничена. Chaos Mesh требует `privileged: true` для chaos-daemon, что иногда требует адаптации Pod Security Policies — но это не отсутствие поддержки, а вопрос конфигурации.
>
> ---
>
> #### D) Chaos Mesh — Kubernetes-нативная платформа от PingCAP, CNCF Incubating; архитектура Controller Manager + Chaos Daemon per node + CRDs; покрывает Pod / Network / IO / Stress / DNS / Time / Kernel / HTTP / JVM / AWS сбои — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Chaos Mesh состоит из трёх главных компонентов:
> 1. **Controller Manager** — Deployment, который наблюдает за CRD-объектами (PodChaos, NetworkChaos и т.д.), валидирует, шедулит, отслеживает lifecycle экспериментов.
> 2. **Chaos Daemon** — DaemonSet на каждой ноде. Получает команды от Controller Manager и применяет fault внутри namespace pod-а (через nsenter, tc, iptables, BPF).
> 3. **CRDs** — `PodChaos`, `NetworkChaos`, `Schedule`, `Workflow` и т.д. Описывают эксперимент декларативно.
>
> ```mermaid
> graph TB
>   Dashboard[Chaos Dashboard] --> Controller[Chaos Controller Manager]
>   CR[ChaosMesh CR YAML] --> Controller
>   Controller --> Daemon1[Chaos Daemon Node 1]
>   Controller --> Daemon2[Chaos Daemon Node 2]
>   Daemon1 --> Pod1[Target Pod]
>   Daemon2 --> Pod2[Target Pod]
> ```
>
> **Когда применять:**
> - Кластер на Kubernetes как primary platform (а не VM/bare-metal).
> - Нужна визуализация и dashboard (Chaos Dashboard) для команд без deep CLI-привычек.
> - Нужны Workflows — последовательность fault-ов с условиями (Argo-подобный DAG).
> - Multi-tenancy: разные команды экспериментируют в своих namespace, RBAC изолирует.
>
> **Подводные камни:**
> - Chaos Daemon требует `privileged` контейнер — обсудите с security team до установки.
> - В GKE Autopilot могут быть ограничения на privileged workloads — проверьте режим кластера.
> - Не использовать для full-cluster experiments в продакшене без согласования: один неверный selector (`mode: all`) — и эксперимент уносит весь сервис.
>
> **Связанные вопросы:**
> - [[chaos-engineering-interview#Q22]] — конкретный пример PodChaos с разбором mode/selector/duration.
> - [[chaos-engineering-interview#Q23]] — NetworkChaos для симуляции latency и partition.
> - [[chaos-engineering-interview#Q24]] — сравнение с Litmus и выбор между ними.
> - [[chaos-engineering-interview#Q33]] — abort conditions, без которых эксперимент превращается в реальный инцидент.

## Q22. (!) Как написать `PodChaos` эксперимент на Chaos Mesh?

`PodChaos` — самый распространённый тип: убить или вывести из строя pod.

### pod-failure (pod временно недоступен)

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: PodChaos
metadata:
  name: pod-failure-example
  namespace: chaos-testing
spec:
  action: pod-failure
  mode: one
  duration: '30s'
  selector:
    namespaces:
      - production
    labelSelectors:
      'app.kubernetes.io/component': 'api'
```

### pod-kill (pod уничтожается один раз)

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: PodChaos
metadata:
  name: pod-kill-example
spec:
  action: pod-kill
  mode: one
  selector:
    namespaces:
      - production
    labelSelectors:
      app: orders
```

### container-kill (убиваем один контейнер внутри pod)

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: PodChaos
metadata:
  name: container-kill-example
spec:
  action: container-kill
  mode: one
  containerNames: ['sidecar']
  selector:
    labelSelectors:
      app: orders
```

### Параметр `mode`

| Значение | Эффект |
|----------|--------|
| `one` | один случайный pod |
| `all` | все pod-ы, подпадающие под selector |
| `fixed` | фиксированное число (указать `value`) |
| `fixed-percent` | процент (контроль blast radius) |
| `random-max-percent` | случайно от 0 до N% |

### Запуск

```bash
kubectl apply -f pod-chaos.yaml
kubectl describe podchaos pod-failure-example
kubectl delete podchaos pod-failure-example   # kill switch
```

Смотри [Kubernetes-вопросы](../devops/kubernetes-interview.md) про pod lifecycle и readiness probes.


> [!mcq]
> - [ ] `mode: one` означает «всегда первый pod в списке» | ❌ ПОСЛЕДСТВИЕ: `mode: one` — случайный pod из selector; «всегда первый» = deterministic, не chaos; для random но controlled — use `fixed-percent`
> - [ ] `pod-kill` и `pod-failure` — синонимы | ❌ ПОСЛЕДСТВИЕ: pod-kill = удалить pod permanently (k8s создаст новый); pod-failure = pod ставится в Failed state на duration; разные effects (timing, recovery)
> - [ ] Selector работает только по namespace, не по labels | ❌ ПОСЛЕДСТВИЕ: selector поддерживает namespaces + labelSelectors + annotationSelectors + fieldSelectors + expressionSelectors; combinable для precise targeting
> - [x] PodChaos с action (pod-failure / pod-kill / container-kill), mode (one/all/fixed-percent для blast radius control), selector (namespaces + labelSelectors), duration; kill switch через `kubectl delete podchaos <name>` | ✓ ПРИМЕНЯТЬ: начинать с mode=one в staging, потом fixed-percent (5-10%) в prod; всегда duration < observed recovery time 📋 ПРАВИЛО: PodChaos = action + mode (blast radius) + selector + duration 🔗 См. Q23

## Q23. Как сделать `NetworkChaos` для симуляции задержек?

`NetworkChaos` инжектирует сетевые эффекты через `tc` (Linux traffic control) на уровне pod-а.

### Delay (задержка)

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: NetworkChaos
metadata:
  name: network-delay
spec:
  action: delay
  mode: one
  selector:
    labelSelectors:
      app: orders
  delay:
    latency: "200ms"
    correlation: "25"
    jitter: "50ms"
  direction: to
  target:
    selector:
      labelSelectors:
        app: payments
    mode: one
  duration: "60s"
```

Сеть `orders → payments` получит +200ms ± 50ms задержки на 60 секунд.

### Partition (сетевая изоляция)

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: NetworkChaos
metadata:
  name: network-partition
spec:
  action: partition
  mode: all
  selector:
    labelSelectors:
      app: orders
  direction: both
  target:
    selector:
      labelSelectors:
        app: payments
    mode: all
  duration: "30s"
```

Тест на split-brain: `orders` и `payments` не видят друг друга.

### Другие actions

- `loss` — packet loss (процент)
- `duplicate` — дублирование пакетов
- `corrupt` — коррупция пакетов
- `bandwidth` — ограничение полосы

### Где это важно

- Проверить таймауты и retry-стратегию
- Тестировать [eventual consistency](../architecture/consistency-patterns-interview.md) при partition
- Валидировать health checks и readiness probes под latency
- Отладить race conditions в распределённых алгоритмах


> [!mcq]
> - [ ] NetworkChaos требует sidecar-инжекции как Istio | ❌ ПОСЛЕДСТВИЕ: NetworkChaos использует Linux tc (traffic control) через chaos-daemon на node; не требует sidecar mesh; работает на pod-уровне через netns
> - [ ] `partition` block one-way — `direction: from` блокирует ВЕСЬ outbound | ❌ ПОСЛЕДСТВИЕ: direction = from/to/both и работает только между source/target selectors; не блокирует весь outbound, только селективную пару
> - [ ] `delay.jitter` обязателен иначе delay не применится | ❌ ПОСЛЕДСТВИЕ: jitter опциональный (добавляет случайность); только `latency` обязателен; jitter useful для realism (real network имеет jitter)
> - [x] NetworkChaos actions: delay (latency + jitter + correlation), partition (split-brain), loss (packet loss %), duplicate, corrupt, bandwidth; direction (from/to/both) между source и target selectors; tc-based, не sidecar | ✓ ПРИМЕНЯТЬ: для тестирования timeouts/retry/circuit breakers; split-brain для consistency проверки; начинать с small latency (50-100ms) 📋 ПРАВИЛО: NetworkChaos = realistic network conditions через tc 🔗 См. Q24

## Q24. Что такое `Litmus` и чем он отличается от Chaos Mesh?

**Litmus** — open-source платформа хаос-инжиниринга для Kubernetes, CNCF Incubating, основанная MayaData. Фокус на **experiment hub**: каталог готовых экспериментов, которые можно собирать в workflows.

### Сравнение

| Характеристика | Chaos Mesh | Litmus |
|----------------|-----------|--------|
| Архитектура | Controller + Daemon per node | ChaosOperator + Hub |
| CR | `PodChaos`, `NetworkChaos`, ... | `ChaosEngine`, `ChaosExperiment`, `ChaosResult` |
| Каталог | Built-in types | ChaosHub (публичный + приватный) |
| Dashboard | Chaos Dashboard | ChaosCenter |
| Workflows | Chaos Workflow | Argo Workflows integration |
| Эксперименты | Fault-first | Experiment-first (reusable) |

### Философия Litmus

Эксперимент = переиспользуемая сущность в Hub. Команда не пишет YAML с нуля, а берёт "pod-delete", "node-drain", "pod-network-latency" из каталога и применяет к своим workloads.

### Когда выбирать что

- **Chaos Mesh**: больше fault types из коробки, удобнее для TiDB-like инфраструктуры, развитая визуализация
- **Litmus**: нужен каталог переиспользуемых экспериментов, интеграция с Argo, фокус на cloud-native end-to-end

Оба — CNCF Incubating, активно развиваются, оба подходят для прод-уровня хаоса в Kubernetes.


> [!mcq]
> - [ ] Litmus = fork от Chaos Mesh с минимальными изменениями | ❌ ПОСЛЕДСТВИЕ: Litmus и Chaos Mesh — независимые проекты от разных авторов; Litmus — MayaData, Chaos Mesh — PingCAP; разные архитектуры и философии
> - [ ] Litmus поддерживает только pod-level chaos | ❌ ПОСЛЕДСТВИЕ: Litmus имеет 50+ experiments в ChaosHub: pod-delete, node-drain, network-latency, disk-fill, cassandra-pod-delete, k8s-application-pod-delete и т.д.
> - [ ] Litmus и Chaos Mesh — proprietary commercial платформы | ❌ ПОСЛЕДСТВИЕ: оба — CNCF Incubating open-source; Litmus от MayaData, Chaos Mesh от PingCAP; commercial — Gremlin (отдельный продукт)
> - [x] Litmus — CNCF Incubating, experiment-first философия (ChaosHub каталог переиспользуемых экспериментов: pod-delete, node-drain, pod-network-latency); ChaosEngine + ChaosExperiment + ChaosResult CRs; Argo Workflows integration; vs Chaos Mesh — больше «fault-first» с rich UI | ✓ ПРИМЕНЯТЬ: Litmus для команд хотящих готовый каталог + Argo workflows; Chaos Mesh для visual dashboard и fine-grained fault types 📋 ПРАВИЛО: Litmus = hub-driven experiments, Chaos Mesh = CRD-driven faults 🔗 См. Q25

## Q25. Как устроен `ChaosEngine` и `ChaosExperiment` в Litmus?

Litmus использует три ключевых CR:

### ChaosExperiment

Определение эксперимента (переиспользуемый шаблон): что делать, какие env-переменные принимает.

```yaml
apiVersion: litmuschaos.io/v1alpha1
kind: ChaosExperiment
metadata:
  name: pod-delete
spec:
  definition:
    scope: Namespaced
    permissions:
      - apiGroups: ["", "apps", "batch"]
        resources: ["pods", "deployments", "jobs"]
        verbs: ["create", "delete", "get", "list"]
    image: "litmuschaos/go-runner:latest"
    args:
      - -c
      - ./experiments -name pod-delete
    env:
      - name: TOTAL_CHAOS_DURATION
        value: '15'
      - name: CHAOS_INTERVAL
        value: '5'
```

### ChaosEngine

Применение эксперимента к конкретному workload.

```yaml
apiVersion: litmuschaos.io/v1alpha1
kind: ChaosEngine
metadata:
  name: orders-pod-delete
  namespace: production
spec:
  appinfo:
    appns: production
    applabel: 'app=orders'
    appkind: deployment
  chaosServiceAccount: litmus-admin
  experiments:
    - name: pod-delete
      spec:
        components:
          env:
            - name: TOTAL_CHAOS_DURATION
              value: '60'
            - name: PODS_AFFECTED_PERC
              value: '33'
```

### ChaosResult

Результат выполнения (создаётся автоматически) — pass/fail + метрики, используется в Prometheus.

### Workflow

```mermaid
graph LR
    A[ChaosHub] --> B[ChaosExperiment<br/>template]
    B --> C[ChaosEngine<br/>application]
    C --> D[Chaos Runner<br/>Pod]
    D --> E[ChaosResult]
    E --> F[Prometheus<br/>metrics]
```


> [!mcq]
> - [ ] ChaosExperiment и ChaosEngine — синонимы, можно использовать любой | ❌ ПОСЛЕДСТВИЕ: ChaosExperiment = template (переиспользуемый шаблон); ChaosEngine = application к конкретному workload; они разделены умышленно для reuse
> - [ ] ChaosResult создаётся вручную после эксперимента | ❌ ПОСЛЕДСТВИЕ: ChaosResult создаётся автоматически Chaos Runner pod'ом; содержит pass/fail и метрики; используется Prometheus для аналитики
> - [ ] ChaosEngine может применять только один experiment | ❌ ПОСЛЕДСТВИЕ: `spec.experiments` — array; один ChaosEngine может orchestrate несколько experiments последовательно или параллельно
> - [x] ChaosExperiment = template (image, args, env, permissions); ChaosEngine = application к target workload (appinfo + chaosServiceAccount + experiments array с overrides); ChaosResult — автоматический результат с pass/fail и метриками для Prometheus | ✓ ПРИМЕНЯТЬ: переиспользуйте ChaosExperiment из ChaosHub; ChaosEngine кастомизируйте per environment; `PODS_AFFECTED_PERC` для blast radius 📋 ПРАВИЛО: Litmus = experiment templates × workload applications 🔗 См. Q26

## Q26. Что такое `Gremlin` и какие у него категории атак?

**Gremlin** — коммерческая SaaS-платформа хаос-инжиниринга (failure-as-a-service). Поддерживает hosts, containers, Kubernetes-ресурсы через агент. Основная ценность — **"halt button"**: любая атака мгновенно откатывается.

### Категории атак

| Категория | Атаки |
|-----------|-------|
| **Resource** | CPU, Memory, IO, Disk |
| **State** | Shutdown, Reboot, Process Killer, Time Travel |
| **Network** | Blackhole, Latency, Packet Loss, DNS |
| **Custom** | Через Gremlin API |

### Пример атаки (через UI/API)

```json
{
  "type": "latency",
  "args": ["-d", "60", "-m", "200", "-p", "443"],
  "targets": {
    "hosts": {
      "ids": ["host-123"]
    }
  }
}
```

### Сильные стороны

- **Production-safe**: halt button, автоматический cleanup
- **Scenarios**: серии атак (escalating chaos)
- **RBAC**: для больших организаций
- **Status Checks**: автоматическая остановка при нарушении SLO
- **ALFI (Application Level Fault Injection)**: инъекции в код на уровне JVM/.NET

### Когда Gremlin

- Нужна enterprise-поддержка и compliance (SOC2, HIPAA)
- Команда не хочет разворачивать и поддерживать open-source (Chaos Mesh, Litmus)
- Нужны scenarios и интеграция с PagerDuty, Datadog, Slack из коробки

Минус — коммерческая лицензия; для startup бюджета опен-сорс дешевле.


> [!mcq]
> - [ ] Gremlin — open-source альтернатива Chaos Mesh | ❌ ПОСЛЕДСТВИЕ: Gremlin — commercial SaaS-платформа (failure-as-a-service); open-source аналоги — Chaos Mesh, Litmus, Chaos Toolkit
> - [ ] У Gremlin нет автоматического rollback | ❌ ПОСЛЕДСТВИЕ: «halt button» — ключевая фича Gremlin; любая атака откатывается мгновенно; для prod-readiness обязательно
> - [ ] Gremlin работает только в AWS | ❌ ПОСЛЕДСТВИЕ: cloud-agnostic — поддерживает hosts, containers, K8s в любом окружении (AWS/GCP/Azure/on-prem); через установку Gremlin agent
> - [x] Gremlin = commercial SaaS chaos-as-a-service; категории атак: Resource (CPU/Mem/IO/Disk), State (Shutdown/Reboot/ProcessKill/TimeTravel), Network (Blackhole/Latency/PacketLoss/DNS); halt button, scenarios, RBAC, status checks, ALFI; integration с PagerDuty/Datadog | ✓ ПРИМЕНЯТЬ: для enterprise compliance (SOC2/HIPAA); когда команда не хочет maintain open-source; full integration tooling 📋 ПРАВИЛО: Gremlin = managed chaos с halt button и enterprise SLA 🔗 См. Q27

## Q27. Что такое `Pumba` и когда его использовать?

**Pumba** — CLI-инструмент для хаоса на уровне Docker и containerd. Работает без Kubernetes — идеален для docker-compose окружений, локальной разработки, интеграционных тестов.

### Возможности

- Kill, stop, pause, remove контейнеры
- Network delay, loss, corrupt (через `tc netem`)
- Bandwidth limit
- Stress на CPU/memory через `stress-ng`

### Примеры команд

```bash
# убить случайный контейнер каждые 10 секунд
pumba --interval 10s kill --signal SIGKILL "re2:myapp.*"

# добавить 500ms задержки на eth0 контейнера orders на 1 минуту
pumba netem --duration 1m --tc-image gaiadocker/iproute2 \
  delay --time 500 orders

# packet loss 20%
pumba netem --duration 30s loss --percent 20 orders

# pause контейнер на 10 секунд
pumba pause --duration 10s orders
```

### Когда выбирать Pumba

- Docker-compose/Docker Swarm окружение (нет K8s)
- Локальные integration-тесты
- Легковесный CI-пайплайн с docker-контейнерами
- Быстрое тестирование без деплоя целой платформы

Pumba — это "kubectl для контейнерного хаоса", простой и без зависимостей.


> [!mcq]
> - [ ] Pumba требует Kubernetes для работы | ❌ ПОСЛЕДСТВИЕ: Pumba — CLI для Docker/containerd, работает без K8s; идеален для docker-compose окружений и локальной разработки
> - [ ] Pumba и Chaos Monkey — синонимы | ❌ ПОСЛЕДСТВИЕ: разные scope — Chaos Monkey (Netflix) для VMs/AWS; Pumba — для Docker контейнеров; разные платформы
> - [ ] Pumba поддерживает только kill контейнеров | ❌ ПОСЛЕДСТВИЕ: поддерживает kill/stop/pause/remove + network delay/loss/corrupt (через tc netem) + bandwidth limit + stress (через stress-ng)
> - [x] Pumba = CLI для Docker/containerd хаоса; kill/stop/pause/remove containers + network effects через tc + stress через stress-ng; идеален для docker-compose, локальной разработки, integration-тестов без K8s; lightweight CI pipelines | ✓ ПРИМЕНЯТЬ: для тестирования docker-compose stack'ов; `pumba kill --signal SIGKILL "re2:myapp.*"`; CI с docker-only без K8s 📋 ПРАВИЛО: Pumba = «kubectl для container chaos» 🔗 См. Q28

## Q28. (!) Что такое `Toxiproxy` и чем он полезен в интеграционных тестах?

**Toxiproxy** (Shopify) — TCP-прокси, который стоит между клиентом и сервером и позволяет программно инжектировать "toxics": latency, bandwidth limit, timeout, slice, reset peer. Основное применение — **integration-тесты** с реалистичной сетевой нестабильностью.

### Архитектура

```mermaid
graph LR
    App[App] --> Proxy[Toxiproxy<br/>localhost:8474]
    Proxy --> Real[Real Dependency<br/>Postgres, Redis, Kafka]
    App -.admin API.-> API[Toxiproxy<br/>API :8474]
    Test[Test Code] --> API
```

### Пример с Testcontainers + Java client

```java
@Container
static ToxiproxyContainer toxiproxy = new ToxiproxyContainer()
    .withNetwork(NETWORK);

@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
    .withNetwork(NETWORK)
    .withNetworkAliases("postgres");

@BeforeAll
static void setup() throws IOException {
    ToxiproxyClient client = new ToxiproxyClient(
        toxiproxy.getHost(), toxiproxy.getControlPort());
    Proxy proxy = client.createProxy("postgres", "0.0.0.0:8666", "postgres:5432");

    // inject 2 секунды latency
    proxy.toxics().latency("slow_pg", ToxicDirection.DOWNSTREAM, 2000);
}

@Test
void whenDbSlow_circuitBreakerOpens() {
    assertThrows(CallNotPermittedException.class, () -> orderService.list());
}
```

### Поддерживаемые toxics

- `latency` — добавить задержку (с jitter)
- `bandwidth` — ограничить полосу
- `slow_close` — медленное закрытие соединения
- `timeout` — keep-alive без ответа (таймауты пула соединений)
- `slicer` — разбить data на маленькие чанки
- `limit_data` / `reset_peer` — разрыв соединения

### Когда использовать

- Integration-тесты клиентов БД, кешей, очередей
- Проверка circuit breaker, timeout, retry в realistic сценариях
- CI-пайплайны (можно крутить тесты с chaos без Kubernetes)

Toxiproxy — мост между "обычными тестами" и настоящим chaos engineering: запускается локально, пишется детерминированно, ловит те же баги, что и прод-хаос.


> [!mcq]
> - [ ] Toxiproxy — это open-source альтернатива Istio service mesh | ❌ ПОСЛЕДСТВИЕ: Toxiproxy — TCP-proxy для testing, не service mesh; не делает routing, только injection toxics; работает на TCP уровне, не sidecar pattern
> - [ ] Toxiproxy работает только с HTTP, не с binary protocols | ❌ ПОСЛЕДСТВИЕ: Toxiproxy — TCP-level, работает с ANY TCP protocol (Postgres, Redis, Kafka, gRPC, MySQL); protocol-agnostic
> - [ ] Toxics применяются только при startup, нельзя менять in runtime | ❌ ПОСЛЕДСТВИЕ: ToxiproxyClient API позволяет добавлять/удалять/изменять toxics во время теста; идеально для тестирования различных degradation scenarios
> - [x] Toxiproxy = TCP-прокси между client и dependency (Postgres/Redis/Kafka); toxics: latency, bandwidth, slow_close, timeout, slicer, reset_peer, limit_data; programmatic API для runtime control; интеграция с Testcontainers + JUnit | ✓ ПРИМЕНЯТЬ: для integration-тестов circuit breaker/timeout/retry в realistic conditions; CI без K8s; детерминированные тесты с network chaos 📋 ПРАВИЛО: Toxiproxy = TCP toxics в integration тестах, между unit и full chaos 🔗 См. Q29

## Q29. Сравнение инструментов: что когда выбирать?

| Инструмент | Слой | Платформа | Когда выбрать |
|------------|------|-----------|---------------|
| Netflix Chaos Monkey | Infrastructure (EC2) | AWS + Spinnaker | AWS VM-based архитектура |
| Chaos Monkey Spring Boot | Application | Spring Boot | Java-монолит, точечный хаос в коде |
| Gremlin | Infra + App | Hosts, K8s, containers | Enterprise, нужен SaaS |
| Litmus | Infra | Kubernetes | CNCF-экосистема, Argo workflows |
| Chaos Mesh | Infra | Kubernetes | Много fault types, dashboard |
| Pumba | Container | Docker | docker-compose, локальные тесты |
| Toxiproxy | Network | Любая (TCP) | Integration-тесты, client libs |
| AWS FIS | Cloud | AWS | AWS-native без агентов |
| Azure Chaos Studio | Cloud | Azure | Azure-native |

### Дерево принятия решений

```mermaid
graph TB
    A[Нужен chaos] --> B{Платформа?}
    B -->|Kubernetes| C{Приоритет?}
    C -->|Fault types| D[Chaos Mesh]
    C -->|Experiment hub| E[Litmus]
    B -->|Docker без K8s| F[Pumba]
    B -->|Spring Boot приложение| G[Chaos Monkey SB]
    B -->|TCP-зависимости в тестах| H[Toxiproxy]
    B -->|AWS VM| I[Netflix CM или AWS FIS]
    B -->|Enterprise SaaS| J[Gremlin]
```

В крупных командах чаще мультистэк: Toxiproxy в unit/integration, Chaos Mesh в staging/prod K8s, Gremlin для compliance-критичных сценариев.


> [!mcq]
> - [ ] Один универсальный chaos-инструмент покрывает все случаи | ❌ ПОСЛЕДСТВИЕ: разные слои (network/infra/app) и платформы (K8s/Docker/VM) требуют разных инструментов; one-size-fits-all не работает в больших командах
> - [ ] Toxiproxy подходит для chaos в production | ❌ ПОСЛЕДСТВИЕ: Toxiproxy для integration-тестов; в prod нужен инструмент уровня pod/node (Chaos Mesh, Litmus, Gremlin)
> - [ ] AWS FIS — open-source как Chaos Monkey | ❌ ПОСЛЕДСТВИЕ: AWS FIS (Fault Injection Service) — AWS-native managed service, не open-source; commercial AWS feature; для AWS-only workloads
> - [x] Multi-stack типичен в крупных командах: Toxiproxy (unit/integration), Chaos Mesh (staging/prod K8s), Gremlin (compliance-критичные); выбор по платформе (K8s/Docker/VM/Cloud) + слою (network/infra/app) + scope (test/prod) | ✓ ПРИМЕНЯТЬ: дерево решений по платформе → K8s = Chaos Mesh/Litmus, Docker-only = Pumba, AWS VM = AWS FIS, Spring Boot inline = Chaos Monkey SB 📋 ПРАВИЛО: инструмент = платформа + слой + scope, не единый ответ 🔗 См. Q30

## Q30. (!) Что такое `GameDay` и как его проводить?

**GameDay** — запланированное мероприятие, на котором команда проводит серию хаос-экспериментов в реальном (часто продовом) окружении, наблюдает реакцию системы и своих процессов, документирует находки. Формат придуман Jesse Robbins в Amazon (2003), вдохновлён пожарными учениями.

### Фазы GameDay

```mermaid
graph LR
    A[1. Prepare<br/>неделя] --> B[2. Execute<br/>2-4 часа]
    B --> C[3. Learn<br/>1-2 часа<br/>post-mortem]
    C --> D[4. Follow-up<br/>action items]
```

### 1. Подготовка

- Выбрать scope (какие сервисы, какие отказы)
- Сформулировать hypothesis для каждого эксперимента
- Определить steady state метрики и abort conditions
- Подготовить runbook (Q31)
- Нотифицировать smежные команды и on-call
- Убедиться в наличии observability

### 2. Выполнение

- Kick-off: все участники на митинге или в чате
- Запускаем эксперимент (оркестратор оглашает шаги)
- Наблюдатели фиксируют поведение
- Incident responders действуют как на настоящем инциденте
- Все события в timeline

### 3. Post-mortem

- Что сработало / что сломалось
- Было ли alerts и через какое время
- Runbook был корректен?
- Action items с owner и deadline

### 4. Follow-up

Через 1-2 недели — check, что action items закрыты. Иначе GameDay превращается в "покатушки без последствий".

### Роли

| Роль | Обязанность |
|------|-------------|
| Orchestrator | Ведёт эксперимент, тайминги |
| Incident Responders | Действуют как на реальном инциденте |
| Observers | Фиксируют метрики, логи, timeline |
| Scribe | Ведёт протокол |
| Safety Officer | Следит за abort conditions, имеет право остановить |

GameDay — это тренировка не только системы, но и **команды**: runbooks, коммуникация, алерты, процесс принятия решений.


> [!mcq]
> - [ ] GameDay — это spontaneous chaos без планирования | ❌ ПОСЛЕДСТВИЕ: spontaneous chaos без plan = реальный инцидент; GameDay = planned exercise с hypothesis, runbook, abort conditions, observers; spontaneous = ANTIPATTERN
> - [ ] GameDay проводится только в staging | ❌ ПОСЛЕДСТВИЕ: GameDay часто в production (controlled blast radius) — staging не имеет real traffic patterns; принцип 3 (run in production) поощряет prod GameDays с safeguards
> - [ ] GameDay — soло-упражнение для SRE | ❌ ПОСЛЕДСТВИЕ: GameDay — командное; roles: Orchestrator, Incident Responders, Observers, Scribe, Safety Officer; цель — тренировка команды + системы вместе
> - [x] GameDay = planned event (Jesse Robbins, Amazon 2003); 4 фазы — Prepare (неделя: scope/hypothesis/runbook/notify) → Execute (2-4ч: live chaos с observers) → Learn (post-mortem 1-2ч) → Follow-up (action items в 1-2 недели); тренировка системы И команды | ✓ ПРИМЕНЯТЬ: квартально или после major changes; начинать с staging GameDay, затем prod c controlled blast radius 📋 ПРАВИЛО: GameDay = fire drill для distributed systems 🔗 См. Q31

## Q31. Что должно быть в `runbook` для chaos-эксперимента?

**Runbook** — документ эксперимента, который служит и планом, и артефактом для post-mortem.

### Структура

```markdown
# Chaos Experiment: Pod Kill Payment Service

## Metadata
- Date: 2026-04-20
- Owner: @sre-alice
- Severity: medium
- Environment: production

## Hypothesis
При убийстве одного из 5 pod `payment-service`:
- p95 latency /api/pay остаётся < 500 ms
- error rate < 1%
- recovery time < 30 s

## Steady State Metrics
- p95 latency: Grafana dashboard X
- error rate: Prometheus query Y
- throughput: Kibana panel Z

## Blast Radius
- 1 pod из 5 (20%)
- Duration: 60 s
- Only production-us-east cluster

## Abort Conditions
- error rate > 5% в течение 30 s
- p99 latency > 2 s в течение 60 s
- ручное решение Safety Officer

## Procedure
1. Confirm all stakeholders on-call
2. Check baseline metrics (screenshot Grafana)
3. kubectl apply -f pod-kill.yaml
4. Monitor 60 s
5. Wait for recovery (ASG replaces pod)
6. kubectl delete podchaos pod-kill-payment

## Rollback
- kubectl delete podchaos ... (immediate)
- kubectl rollout restart deployment payment-service

## Observability Links
- Dashboard: ...
- Alerts channel: ...
- Tracing: ...
```

Runbook обязателен: без него эксперимент превращается в импровизацию, post-mortem — в догадки.


> [!mcq]
> - [ ] Runbook можно написать на лету во время эксперимента | ❌ ПОСЛЕДСТВИЕ: «на лету» = импровизация; без pre-defined hypothesis и abort conditions experiment становится не-научным; post-mortem превращается в догадки
> - [ ] Достаточно name эксперимента и hypothesis в runbook | ❌ ПОСЛЕДСТВИЕ: minimum нужны hypothesis + steady state metrics + blast radius + abort conditions + rollback procedure + observability links; без них — slop-experiment
> - [ ] Rollback procedure опционален если используем Chaos Mesh | ❌ ПОСЛЕДСТВИЕ: rollback ОБЯЗАТЕЛЕН, даже если Chaos Mesh откатывает сам; на случай если CR-удаление не сработало, нужны manual recovery steps
> - [x] Runbook = metadata (date/owner/severity/env) + hypothesis + steady state metrics + blast radius + abort conditions + procedure + rollback + observability links; служит и планом, и артефактом для post-mortem | ✓ ПРИМЕНЯТЬ: template в git, версионируется; обновлять после каждого эксперимента; review с SRE/manager перед prod 📋 ПРАВИЛО: runbook = plan + safety + audit-trail 🔗 См. Q32

## Q32. Как устроен post-mortem после эксперимента?

Структура post-mortem после chaos-эксперимента похожа на incident post-mortem, но с фокусом на **обучение**, а не на "вину".

### Шаблон

1. **Summary**: что запускали, к какой гипотезе
2. **Timeline**: события с метками времени
3. **Hypothesis outcome**: подтверждена / опровергнута
4. **What went well**: что сработало (alert, failover, runbook)
5. **What went wrong**: находки (MTTR выше ожидаемого, алерт не сработал, деградация фичи)
6. **Action Items**: owner + deadline для каждого
7. **Unknown unknowns**: что мы не предвидели

### Принцип blameless

Фокус на **системе и процессах**, а не на людях. "Алерт не сработал потому что metric не экспортировался" — полезно. "Петя забыл настроить алерт" — нет.

### Метрики post-mortem

- **MTTD** (Mean Time To Detect) — как быстро увидели проблему
- **MTTR** (Mean Time To Recover) — как быстро восстановились
- **Customer Impact** — сколько пользователей / запросов затронуто
- **SLO Burn Rate** — на сколько сгорел error budget

Хороший post-mortem становится знанием команды, плохой — бюрократией. Смотри также [blameless culture](../behavioral/behavioral-interview.md) в поведенческих вопросах.


> [!mcq]
> - [ ] Post-mortem ищет виновного для дисциплинарных мер | ❌ ПОСЛЕДСТВИЕ: blame culture убивает chaos engineering; команда перестаёт делиться находками, скрывает баги; правильно — blameless post-mortem с фокусом на system/process
> - [ ] Action items могут не иметь deadline — главное наблюдения | ❌ ПОСЛЕДСТВИЕ: без owner + deadline action items не выполняются; post-mortem становится бюрократией без улучшений; «GameDay покатушки без последствий»
> - [ ] MTTD и MTTR — одно и то же | ❌ ПОСЛЕДСТВИЕ: MTTD = Mean Time To Detect (alert latency); MTTR = Mean Time To Recover (recovery latency); разные phases of incident response, разные improvements
> - [x] Post-mortem структура: Summary + Timeline + Hypothesis outcome + What went well + What went wrong + Action Items (owner + deadline) + Unknown unknowns; blameless фокус на system/process; метрики MTTD/MTTR/Customer Impact/SLO Burn Rate | ✓ ПРИМЕНЯТЬ: blameless template — «alert не сработал т.к. metric не экспортировался» (полезно) vs «Петя забыл» (не); review action items через 1-2 недели 📋 ПРАВИЛО: post-mortem = learning, не blame 🔗 См. Q33

## Q33. (!) Что такое `abort condition` и когда останавливать эксперимент?

**Abort condition** — автоматический или ручной триггер немедленной остановки эксперимента, когда система выходит за допустимые границы деградации. Это **обязательный** элемент любого chaos-эксперимента.

### Типы abort conditions

| Тип | Пример |
|-----|--------|
| Metric-based | error rate > 5% |
| Latency-based | p99 > 2 s в течение 60 s |
| Business-based | checkout/min упал на 10% |
| SLO-based | burn rate > 2x за окно |
| Manual | Safety Officer решает |

### Как реализовать

- **Gremlin Status Checks**: автоматическая остановка при нарушении health-check
- **Chaos Mesh**: удаление CR (`kubectl delete`) мгновенно откатывает большинство fault types
- **Script-based**: cron/watcher на Prometheus, который удаляет эксперимент при нарушении

### Пример Prometheus abort

```yaml
# PrometheusRule
- alert: ChaosAbortCondition
  expr: |
    sum(rate(http_requests_total{status=~"5.."}[1m]))
    / sum(rate(http_requests_total[1m])) > 0.05
  for: 30s
  annotations:
    action: "kubectl delete chaos --all -n chaos-testing"
```

### Философия

Лучше остановить эксперимент **раньше** времени, чем продолжать и получить реальный инцидент. Цель — **учиться**, а не ронять прод. Готовность остановить эксперимент — признак зрелости практики.


> [!mcq]
> - [ ] Abort condition опционален — Chaos Mesh сам остановится при проблеме | ❌ ПОСЛЕДСТВИЕ: Chaos Mesh выполняет ЧТО задано в CR; не имеет понятия о SLO/business metrics; abort condition обязателен — это и есть kill switch
> - [ ] Лучше дать эксперименту завершиться чтобы получить полные данные | ❌ ПОСЛЕДСТВИЕ: продолжение experiment при customer impact = real incident; principle: better stop early и сохранить SLO, чем «полные данные» с outage
> - [ ] Manual abort by Safety Officer достаточен — automation не нужна | ❌ ПОСЛЕДСТВИЕ: human reaction time ~10-30s слишком медленна для быстрых degradations; нужен automation: Prometheus rule → автоматическое kubectl delete chaos
> - [x] Abort condition = автоматический/ручной trigger остановки при degradation; типы — Metric-based (error rate > 5%), Latency (p99 > 2s), Business (checkouts -10%), SLO burn rate > 2x, Manual; реализация — Gremlin Status Checks, Chaos Mesh kubectl delete, Prometheus rule | ✓ ПРИМЕНЯТЬ: всегда множественные abort conditions (metric + business + manual); test the kill switch перед prod 📋 ПРАВИЛО: abort early > learn slowly через outage 🔗 См. Q34

## Q34. (!) Какие требования к observability для `Chaos Engineering`?

Без observability chaos engineering **слепой** — нельзя подтвердить или опровергнуть гипотезу, нельзя увидеть abort condition, нельзя написать post-mortem. Observability — **prerequisite**, не "хорошо бы".

### Три столпа

```mermaid
graph TB
    A[Observability<br/>Requirements] --> M[Metrics]
    A --> L[Logs]
    A --> T[Traces]

    M --> M1[SLO / SLI dashboards]
    M --> M2[Infra: CPU, memory, network]
    M --> M3[Business: orders, revenue]

    L --> L1[Структурированные JSON]
    L --> L2[Correlation ID]
    L --> L3[Agregation: ELK, Loki]

    T --> T1[Distributed tracing]
    T --> T2[Span attributes]
    T --> T3[Sampling с усилением]
```

### Обязательный минимум

- **Dashboard** со steady state метриками (p50, p95, p99, error rate, throughput)
- **Alerts** на SLO-burn
- **Logs aggregation** (ELK / Loki) с correlation ID
- **Distributed tracing** ([OpenTelemetry](../monitoring/observability-interview.md)): чтобы видеть, как latency одного сервиса влияет на всю цепочку
- **Event marker** на графиках: "в 14:00 запустили chaos experiment X"

### Типичный провал

"Запустили chaos, приложение вроде не упало" — но без метрик нельзя сказать, что произошло на самом деле. Возможно, `p99` вырос в 10 раз, но никто не смотрел именно на него.

### Chaos Engineering как драйвер observability

Эксперимент, который нельзя проанализировать, — бессмысленный. Поэтому подготовка к первому GameDay часто включает доработку observability. Это полезный побочный эффект практики.

Смотри подробнее [метрики и трейсинг](../monitoring/metrics-tracing-interview.md) и [observability](../monitoring/observability-interview.md).


> [!mcq]
> - [ ] Observability — это nice-to-have для chaos engineering | ❌ ПОСЛЕДСТВИЕ: observability — PREREQUISITE; без metrics/logs/traces невозможно подтвердить hypothesis, abort на triggers, написать post-mortem; chaos без observability = выкл glазами
> - [ ] Достаточно basic monitoring (CPU/memory) | ❌ ПОСЛЕДСТВИЕ: infrastructure metrics не показывают business impact; нужны SLI/SLO dashboards + business metrics (orders, revenue) для понимания пользовательского эффекта
> - [ ] Event markers на графиках не важны — можно вспомнить когда был эксперимент | ❌ ПОСЛЕДСТВИЕ: при множественных experiments в день путаются; event markers («14:00 chaos X started») — обязательны для post-mortem analysis correlation
> - [x] Три столпа: Metrics (SLO dashboards, infra, business) + Logs (структурированные JSON, correlation ID, aggregation ELK/Loki) + Traces (distributed OTel, span attrs, sampling); + event markers на graphs «started chaos X в 14:00»; observability — drive для практики, не side effect | ✓ ПРИМЕНЯТЬ: подготовка к первому GameDay часто начинается с улучшения observability; полезный side-effect chaos практики 📋 ПРАВИЛО: chaos без observability = blind; observability — prerequisite 🔗 См. Q35

## Q35. Как `Chaos Engineering` связан с `error budget` и SLO?

**SLO** (Service Level Objective) задаёт цель надёжности (например, 99.9% success rate за 30 дней). **Error budget** = 100% - SLO = допустимый процент ошибок. Chaos Engineering **тратит** error budget намеренно, чтобы получить информацию.

### Математика

- 99.9% SLO → error budget 43.2 минуты downtime в месяц
- Chaos experiment = контролируемое расходование budget
- Трата на эксперимент: например, 5 минут — 12% от месячного бюджета

### Принципы

1. **Нет error budget** → chaos откладывается, сначала стабилизация
2. **Budget есть** → можно проводить эксперименты в этом квартале
3. **Abort condition = SLO burn rate**: если эксперимент выжирает бюджет быстрее, останавливаем
4. **Budget-based prioritization**: чем больше бюджета, тем агрессивнее эксперименты

### Интеграция с SRE

```mermaid
graph LR
    A[SLO 99.9%] --> B[Error Budget<br/>43 min/month]
    B --> C{Budget<br/>осталось?}
    C -->|да| D[Chaos Experiments]
    C -->|нет| E[Freeze:<br/>стабилизация]
    D --> F[SLI metrics]
    F --> A
```

### Пример политики

```yaml
chaos_policy:
  minimum_error_budget: 50%  # не запускаем если <50% осталось
  experiment_budget_consumption: 5%  # один эксперимент < 5% бюджета
  auto_halt_on_slo_burn: 2x  # burn rate > 2x → abort
```

Без SLO/error budget chaos engineering работает "на ощупь"; с ними — превращается в **дисциплину управления надёжностью**.


> [!mcq]
> - [ ] Error budget — это бюджет на исправление багов | ❌ ПОСЛЕДСТВИЕ: error budget = 100% - SLO = допустимый процент неработоспособности (43.2min/month при 99.9%); расходуется на planned outages, chaos, releases
> - [ ] Если SLO нарушен, всё равно надо проводить chaos для обучения | ❌ ПОСЛЕДСТВИЕ: SLO violation = freeze chaos и stabilize first; chaos requires budget headroom; иначе experiment усугубит существующую проблему
> - [ ] Chaos experiment должен потратить весь error budget на один раз | ❌ ПОСЛЕДСТВИЕ: рекомендация — single experiment <5% месячного budget; чтобы оставалось место для других experiments + actual incidents; не all-in
> - [x] SLO задаёт цель (99.9%); error budget = 100% - SLO (43.2min/month при 99.9%); chaos тратит budget намеренно для информации; принципы: no budget → freeze, budget есть → experiments в этом квартале, abort при burn rate > 2x; budget-based prioritization | ✓ ПРИМЕНЯТЬ: policy as code — `minimum_error_budget: 50%`, `experiment_budget_consumption: 5%`, `auto_halt_on_slo_burn: 2x` 📋 ПРАВИЛО: budget = currency for chaos experiments 🔗 См. Q36

## Q36. Как встроить `Chaos Engineering` в CI/CD pipeline?

Автоматизация экспериментов — 4-й принцип Chaos Engineering. Ручные GameDays дают разовую уверенность; CI/CD — **непрерывную**.

### Три уровня интеграции

| Уровень | Где | Что |
|---------|-----|-----|
| 1. Smoke chaos | Pre-deploy CI | Toxiproxy в integration tests |
| 2. Staging chaos | Post-deploy staging | Chaos Mesh jobs после smoke |
| 3. Prod chaos | Scheduled in prod | Chaos Mesh Schedule, Gremlin scenarios |

### Пример GitHub Actions

```yaml
jobs:
  chaos-staging:
    needs: deploy-staging
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Apply PodChaos
        run: kubectl apply -f chaos/pod-failure.yaml
      - name: Wait and validate
        run: |
          sleep 60
          ./scripts/check-slo.sh
      - name: Cleanup
        if: always()
        run: kubectl delete -f chaos/pod-failure.yaml
```

### Pattern: `ChaosSchedule`

```yaml
apiVersion: chaos-mesh.org/v1alpha1
kind: Schedule
metadata:
  name: daily-pod-kill
spec:
  schedule: "0 10 * * 1-5"
  type: "PodChaos"
  historyLimit: 5
  concurrencyPolicy: "Forbid"
  podChaos:
    action: pod-kill
    mode: one
    selector:
      labelSelectors:
        app: demo
```

### Guardrails в CI

- Blocker: emergency freeze flag (никакого chaos во время инцидентов)
- SLO burn rate check перед стартом
- Maintenance windows (no chaos в black Friday)
- Notifications в Slack перед стартом

### Что даёт CI/CD интеграция

- Регрессия чаос-устойчивости: если вчера выдерживали pod-kill, а сегодня нет — найдём через 24 часа, не через квартал
- Новые deploy проверяются против того же набора экспериментов
- Zero-maintenance chaos (не зависит от доступности людей)

Смотри [дизайн pipeline](../cicd/pipeline-design-interview.md) про этапы CI/CD.


> [!mcq]
> - [ ] CI/CD chaos = ручной GameDay раз в квартал | ❌ ПОСЛЕДСТВИЕ: GameDay — manual practice; CI/CD integration — automation, 4-й принцип; одноразовые GameDays дают одноразовую уверенность, не continuous
> - [ ] Chaos нужен только в prod environment | ❌ ПОСЛЕДСТВИЕ: 3 уровня — Smoke chaos в CI (Toxiproxy), Staging chaos (Chaos Mesh jobs), Prod chaos (Schedule); каждый уровень catches different issues
> - [ ] Chaos jobs можно запускать без guardrails в CI | ❌ ПОСЛЕДСТВИЕ: нужны emergency freeze flag, SLO burn rate check, maintenance windows (no chaos в Black Friday), Slack notifications; без них — chaos станет cause of outage
> - [x] 3 уровня — Smoke (Toxiproxy в integration tests, pre-deploy), Staging (Chaos Mesh jobs post-deploy), Prod (ChaosSchedule cron-based); guardrails — freeze flag, SLO burn check, maintenance windows; даёт continuous chaos-устойчивость + regression detection | ✓ ПРИМЕНЯТЬ: начать с Smoke (Toxiproxy в JUnit), затем staging Schedule, prod — только после maturity 📋 ПРАВИЛО: chaos в CI = regression test для resilience 🔗 См. Q37

## Q37. (!) Что такое `Chaos Maturity Model` (CMM)?

**Chaos Maturity Model** (Netflix, Rosenthal & Jones, книга "Chaos Engineering") оценивает зрелость практики по двум осям: **sophistication** (глубина экспериментов) и **adoption** (широта внедрения).

### Две оси

```mermaid
graph TB
    subgraph "Adoption / принятие"
        A1[In the Shadows]
        A2[Investment]
        A3[Adoption]
        A4[Cultural Expectation]
    end

    subgraph "Sophistication / сложность"
        S1[Elementary]
        S2[Simple]
        S3[Advanced]
        S4[Sophisticated]
    end
```

### Уровни sophistication

| Уровень | Что есть |
|---------|----------|
| **Elementary** | Разовые ручные эксперименты на staging, нет hypothesis, нет автоматизации |
| **Simple** | Эксперименты на staging, ручной анализ, базовый runbook |
| **Advanced** | Автоматизация, эксперименты на staging и canary, hypothesis + metrics |
| **Sophisticated** | Автоматизация в проде, интеграция с CD и business metrics, dynamic blast radius |

### Уровни adoption

| Уровень | Что есть |
|---------|----------|
| **In the Shadows** | Один инженер делает у себя, команда не знает |
| **Investment** | Организация выделяет бюджет, есть owner |
| **Adoption** | Большинство команд инженерии участвует |
| **Cultural Expectation** | Все новые сервисы должны проходить chaos, это как unit-тесты |

### Цель

Не все команды должны быть на Sophisticated+Cultural — для стартапа с 3 сервисами это оверкилл. Цель — **осознанное продвижение** по матрице в зависимости от бизнес-потребностей.

### Использование на собеседовании

Если спрашивают "как начать chaos в команде", отвечаем: Elementary на staging → Simple с runbooks и post-mortem → Advanced с автоматизацией в CI → Sophisticated с интеграцией в SLO и прод.


> [!mcq]
> - [ ] CMM имеет 5 чётких уровней как CMMI | ❌ ПОСЛЕДСТВИЕ: CMM (Rosenthal & Jones, Netflix) — 2D matrix с sophistication (4 levels) × adoption (4 levels), не linear ladder; 16 ячеек, не 5 levels
> - [ ] «Sophisticated + Cultural Expectation» — обязательный target для всех | ❌ ПОСЛЕДСТВИЕ: для стартапа с 3 сервисами — overkill; цель — осознанное продвижение по матрице based on business needs, не максимум
> - [ ] CMM измеряет только тулинг (Chaos Monkey vs Chaos Mesh) | ❌ ПОСЛЕДСТВИЕ: измеряет practice maturity (hypothesis discipline, blast radius control, automation, organizational adoption), не только tooling
> - [x] CMM = 2D matrix; sophistication (Elementary → Simple → Advanced → Sophisticated) × adoption (In the Shadows → Investment → Adoption → Cultural Expectation); цель — осознанное продвижение по матрице based on business needs; путь — staging → CI/CD → prod with SLO integration | ✓ ПРИМЕНЯТЬ: оценить current state по обеим осям; на интервью «как начать chaos» = Elementary staging → Simple runbooks → Advanced CI → Sophisticated prod 📋 ПРАВИЛО: maturity = sophistication × adoption, осознанный рост 🔗 См. Q38

## Q38. Какие роли участвуют в Chaos Engineering?

В зрелой практике chaos распределён между несколькими ролями:

### Роли

| Роль | Обязанности |
|------|-------------|
| **Chaos Engineer / SRE** | Проектирует эксперименты, развивает платформу |
| **Service Owner / Dev Lead** | Отвечает за hypothesis своих сервисов, участвует в GameDay |
| **Incident Responder (on-call)** | Реагирует на abort conditions, тренируется на GameDay |
| **Safety Officer** | Останавливает эксперимент при необходимости |
| **Observability Engineer** | Обеспечивает метрики, логи, трейсинг |
| **Platform Team** | Поддерживает chaos-инфраструктуру (Chaos Mesh operator) |
| **Leadership / Engineering Manager** | Выделяет время и бюджет, защищает error budget |

### Модели организации

```mermaid
graph TB
    A[Модели] --> B[Centralized]
    A --> C[Federated]
    A --> D[Embedded]

    B --> B1[SRE-team владеет платформой<br/>и проводит GameDays]
    C --> C1[Платформа централизована<br/>эксперименты пишут команды]
    D --> D1[Каждая команда имеет<br/>своего chaos engineer]
```

Маленькая компания: один SRE + вовлечённые service owners. Крупная: платформенная команда + "chaos champions" в каждом squad. Смотри также [вопросы по лидерству](../behavioral/behavioral-interview.md).


> [!mcq]
> - [ ] Chaos — только SRE responsibility, dev teams не участвуют | ❌ ПОСЛЕДСТВИЕ: ownership разделён — Service Owner отвечает за hypothesis своих сервисов; SRE проектирует, devs участвуют как Incident Responders; «only SRE» antipattern
> - [ ] Один Chaos Engineer на всю компанию покрывает все | ❌ ПОСЛЕДСТВИЕ: bottleneck; модели — Centralized (SRE team), Federated (platform + team experiments), Embedded (chaos engineer в каждой команде); зависит от размера организации
> - [ ] Safety Officer = совещательный голос без права остановки | ❌ ПОСЛЕДСТВИЕ: Safety Officer — это authority на abort; должен иметь право мгновенно остановить experiment; advisory-only — anti-pattern
> - [x] Распределённые роли: Chaos Engineer/SRE (платформа), Service Owner (hypothesis), Incident Responder/on-call (тренировка), Safety Officer (abort authority), Observability Engineer (метрики), Platform Team (Chaos Mesh operator), Leadership (бюджет/budget); модели — Centralized/Federated/Embedded | ✓ ПРИМЕНЯТЬ: для small team — один SRE + service owners; для large — Federated с chaos champions в каждом squad 📋 ПРАВИЛО: chaos = cross-functional, не SRE-silo 🔗 См. Q39

## Q39. Как убедить бизнес и руководство внедрять Chaos Engineering?

Сопротивление — частая проблема: "вы хотите специально ломать прод?!". Нужны аргументы в деньгах и рисках.

### Аргументы

1. **ROI на инцидентах**: один prevent-ed инцидент масштаба Azure-like стоит $$$ — chaos стоит меньше
2. **MTTR**: тренированная команда реагирует быстрее, каждая минута downtime = стоимость
3. **Compliance**: PCI, SOC2, финрег требуют disaster recovery testing — GameDay закрывает
4. **Confidence for releases**: проверенный на chaos код → меньше страха релизов → выше скорость доставки
5. **Attract talent**: engineering culture с SRE и chaos привлекает сильных инженеров

### Пример сторителлинга

> "В прошлом квартале у нас было 3 инцидента из-за flaky DB. Если мы бы симулировали эти отказы в GameDay, мы бы нашли и починили их до прода. Один прод-инцидент = 4 часа × 30 инженеров = 120 человеко-часов. Chaos-эксперимент = 2 часа × 5 инженеров = 10 человеко-часов. ROI 12x."

### Антипаттерн пресейла

- "Мы хотим ронять прод, потому что это модно" — нет
- "Netflix делает, и мы должны" — нет
- "Повысит resilience" — слишком абстрактно

Нужны конкретные метрики: MTTR, incident frequency, SLO, customer impact.

### Постепенность

Начать со staging → показать hypothesis/finding → накопить кейсы → повысить зрелость. Никогда не стартовать "давайте сразу Chaos Kong на прод".


> [!mcq]
> - [ ] «Netflix делает chaos, поэтому и мы должны» — главный аргумент | ❌ ПОСЛЕДСТВИЕ: cargo culting не убеждает бизнес; нужны specific аргументы про MTTR/SLO/incident frequency в вашей компании
> - [ ] Достаточно сказать «повысит resilience» — это очевидно | ❌ ПОСЛЕДСТВИЕ: «resilience» слишком abstract; бизнес мыслит ROI в $/времени; нужны конкретные incident costs avoided
> - [ ] Запустить большой chaos на проде чтобы показать ценность | ❌ ПОСЛЕДСТВИЕ: high-stakes demo может cause outage и навсегда дискредитировать chaos в компании; начинать со staging
> - [x] Конкретные аргументы: ROI (incident cost × frequency vs experiment cost), MTTR improvement, compliance requirements (PCI/SOC2/disaster recovery), confidence для releases, attract talent; storytelling — «3 инцидента в прошлом квартале × 120 человеко-часов vs 10 часов experiment = ROI 12x»; постепенное внедрение | ✓ ПРИМЕНЯТЬ: data-driven pitch с metrics из вашей системы; постепенно — staging → CI → prod; накопить case studies перед prod 📋 ПРАВИЛО: business case = ROI на конкретных incidents, не abstract resilience 🔗 См. Q40

## Q40. (!) Какие антипаттерны в `Chaos Engineering`?

Список типичных ошибок, которые встречаются на практике:

### 1. "Chaos ради chaos"

Эксперимент без hypothesis и steady state — просто "давайте что-нибудь сломаем". Нельзя сказать, успех или провал.

### 2. Нет abort condition

Запустили и смотрим "что будет". Если всё плохо — уже поздно. Abort должен быть автоматическим.

### 3. Chaos без observability

Невозможно проанализировать эксперимент без dashboards, alerts, tracing. Сначала — observability, потом — chaos.

### 4. Blast radius не контролируется

Pod-kill на `mode: all` в проде = самоDDoS. Всегда начинаем с `one` или `fixed-percent: 10`.

### 5. Chaos во время инцидента

В момент реального incident'а категорически нельзя запускать эксперименты. Нужен "chaos freeze" механизм.

### 6. Игнорирование post-mortem

Провели эксперимент, нашли проблему — и не зафиксировали owner/deadline. Через квартал повторили тот же эксперимент с тем же failure.

### 7. Эксперименты "по настроению"

Нет расписания, приоритизации, планов. Chaos случается когда у команды есть время → никогда.

### 8. Chaos вместо тестов

Chaos не заменяет [unit](unit-testing-interview.md) и [integration-тесты](integration-testing-interview.md). Это **дополнение** для distributed-специфики.

### 9. Полагаться только на Chaos Monkey

Только pod-kill → не проверяются network, disk, DNS, dependency failures. Нужен **портфель** экспериментов.

### 10. Одноразовый GameDay

Провели раз, поставили галочку. Chaos Engineering — **континуальная** практика, одноразовые упражнения дают одноразовую уверенность.


> [!mcq]
> - [ ] Запустить pod-kill на mode=all без abort condition | ❌ ПОСЛЕДСТВИЕ: само-DDoS; все pods убиты одновременно; нет kill switch → real incident; нарушает blast radius + abort condition принципы
> - [ ] Использовать только Chaos Monkey без других tools | ❌ ПОСЛЕДСТВИЕ: pod-kill only — не покрывает network/disk/DNS/dependency failures; нужен portfolio экспериментов
> - [ ] Делать post-mortem только если эксперимент cause outage | ❌ ПОСЛЕДСТВИЕ: каждый experiment требует post-mortem (с outcome, actions); только при outage = упускается learning от «successful» experiments
> - [x] Антипаттерны: chaos без hypothesis/steady state, без abort condition, без observability, неконтролируемый blast radius (mode=all), chaos во время incident, игнорирование post-mortem, без расписания, замена тестов, только pod-kill, одноразовый GameDay | ✓ ПРИМЕНЯТЬ: pre-flight checklist; chaos freeze flag во время incidents; postmortem template обязателен; chaos = дисциплина, не «давайте ломать» 📋 ПРАВИЛО: chaos antipatterns = всё что нарушает 5 принципов 🔗 См. Q41

## Q41. Что значит "не делать chaos ради chaos"?

Это базовый антипаттерн: команда узнала про chaos, подключила Chaos Monkey, запускает "что-нибудь" — и не получает пользы. Реальный chaos — **дисциплина с научным методом**.

### Что отличает дисциплинированный chaos

1. **Гипотеза**: "При X произойдёт Y, и steady state Z сохранится"
2. **Измеримость**: метрики, по которым можно подтвердить/опровергнуть гипотезу
3. **Blast radius**: явно ограниченный, продуманный
4. **Abort condition**: тригер остановки
5. **Outcome**: action items, post-mortem, улучшения

### Формула "хорошего" эксперимента

```
Problem → Hypothesis → Experiment → Result → Learning → Action
```

Если любой из этапов отсутствует — это "chaos ради chaos". Лучше **не делать эксперимент**, чем делать без методологии.

### Примеры

❌ "Давайте запустим pod-kill на проде и посмотрим" — нет hypothesis
✅ "Мы думаем, что при убийстве 1 из 5 pod payment-service p95 latency не превысит 500 ms. Проверим это в четверг в 14:00."


> [!mcq]
> - [ ] Любой запуск Chaos Monkey считается дисциплинированным chaos | ❌ ПОСЛЕДСТВИЕ: tool ≠ practice; «запустили tool без hypothesis» = chaos ради chaos; нужны hypothesis + abort + action items
> - [ ] Hypothesis опциональна если есть мониторинг | ❌ ПОСЛЕДСТВИЕ: без pre-defined hypothesis нет verifiable outcome; «мы смотрим dashboards и решим» = post-hoc rationalization
> - [ ] «Давайте посмотрим что будет» — валидное начало experiment | ❌ ПОСЛЕДСТВИЕ: explicit пример «chaos ради chaos»; «посмотрим что будет» означает no hypothesis, no measurable outcome, no learning
> - [x] Дисциплинированный chaos = scientific method: Problem → Hypothesis → Experiment → Result → Learning → Action; обязательны hypothesis, измеримость, blast radius, abort condition, outcome (post-mortem + action items); без любого = «chaos ради chaos» | ✓ ПРИМЕНЯТЬ: explicit формулировка перед experiment; «при X произойдёт Y, и steady state Z сохранится»; не «давайте сломаем» 📋 ПРАВИЛО: experiment = hypothesis-driven, не curiosity-driven 🔗 См. Q42

## Q42. Какие prerequisites должны быть перед первым экспериментом?

Чтобы первый эксперимент был **продуктивен**, а не **разрушителен**, нужна подготовка инфраструктуры и процессов.

### Чек-лист prerequisites

| Категория | Что нужно |
|-----------|-----------|
| **Observability** | Dashboards, alerts на SLO, log aggregation, tracing |
| **SLO / error budget** | Определены, согласованы, отслеживаются |
| **Runbooks** | На типичные инциденты, проверены |
| **On-call** | Есть rotation, paging работает |
| **Resilience-паттерны** | Circuit breaker, retry, timeout в коде |
| **Deployment** | Быстрый rollback (< 5 мин) |
| **Автоскейлинг / self-healing** | K8s deployments, HPA, ASG |
| **Communication** | Slack-канал для incidents, war-room формат |

### Признаки незрелости (chaos рано)

- Деплой вручную и занимает часы
- Метрики смотрят только после алерта от пользователей
- Нет staging, есть только prod
- Единая БД без replica / failover
- Нет tests → chaos найдёт baseline bugs, а не distributed-specific

### Что делать, если prerequisites не готовы

Chaos Engineering **помогает подсветить** пробелы, но **не решает их**. Сначала 2-3 квартала на observability и resilience, потом chaos.


> [!mcq]
> - [ ] Достаточно иметь любую CI/CD pipeline для chaos | ❌ ПОСЛЕДСТВИЕ: CI/CD необходимый но не достаточный prerequisite; нужны также observability + SLO + on-call + resilience patterns в коде + быстрый rollback (<5min)
> - [ ] Можно начинать chaos без observability и накатить её позже | ❌ ПОСЛЕДСТВИЕ: chaos без observability = blind experiments; не можешь подтвердить hypothesis; observability — мастхэв prerequisite, не «нагоним позже»
> - [ ] SLO не нужны для первого эксперимента | ❌ ПОСЛЕДСТВИЕ: без SLO нет error budget → нет budget для chaos; нет abort condition (SLO burn rate); первый experiment без SLO — random стресс-тест
> - [x] Prerequisites: Observability (dashboards/alerts/aggregation/tracing) + SLO/error budget + Runbooks + On-call rotation + Resilience patterns в коде (CB/retry/timeout) + Fast rollback < 5min + Auto-scaling/self-healing + Communication channels; без них chaos высветит pre-existing проблемы а не distributed-specific | ✓ ПРИМЕНЯТЬ: если prerequisites не готовы — 2-3 квартала на observability+resilience, потом chaos; не наоборот 📋 ПРАВИЛО: chaos после foundation, не вместо неё 🔗 См. Q43

## Q43. Как комбинировать Chaos Engineering с resilience-паттернами?

Chaos — это **валидация** resilience-паттернов. Паттерн без chaos = надежда; chaos без паттерна = падение.

### Маппинг

| Паттерн | Эксперимент, который его валидирует |
|---------|-------------------------------------|
| [Circuit Breaker](../architecture/resilience-patterns-interview.md) | Latency injection в dependency |
| Retry с backoff | 5xx на 50% запросов к зависимости |
| Timeout | Slow downstream (Toxiproxy latency 10s) |
| Bulkhead | Thread pool saturation в одной зависимости |
| Rate limiting | Traffic burst через `tc` или loadgen |
| Failover / replication | Kill master DB |
| Graceful degradation | Full dependency outage (partition) |
| Idempotency | Duplicate requests (network duplicate) |

### Процесс

```mermaid
graph LR
    A[Design<br/>resilience pattern] --> B[Implement]
    B --> C[Chaos<br/>experiment]
    C --> D{Pattern<br/>сработал?}
    D -->|нет| B
    D -->|да| E[Регрессия<br/>в CI]
```

### Антипример

"У нас есть circuit breaker" → "мы не проверяли, как он переходит в half-open в продакшене". Теоретический паттерн без chaos — это паттерн, в который нельзя верить.

Смотри [паттерны надёжности](../architecture/resilience-patterns-interview.md) и [микросервисные паттерны](../architecture/microservices-interview.md) для полного обзора.


> [!mcq]
> - [ ] Resilience-паттерны заменяют chaos engineering | ❌ ПОСЛЕДСТВИЕ: паттерн без chaos = надежда что работает; нужна валидация в realistic conditions; «у нас есть circuit breaker» ≠ «он работает в production»
> - [ ] Circuit Breaker валидируется через unit-тесты | ❌ ПОСЛЕДСТВИЕ: unit-тесты проверяют изолированно; CB-state transitions (closed→open→half-open) под реальной latency валидируются только через chaos (Toxiproxy latency injection)
> - [ ] Если паттерн в коде — chaos для него не нужен | ❌ ПОСЛЕДСТВИЕ: implementation ≠ behavior; «we have retry» — конфигурация retries может быть неправильной для real failure modes; только chaos verify
> - [x] Маппинг паттерн → experiment: CB → latency injection, Retry с backoff → 5xx 50%, Timeout → slow downstream, Bulkhead → thread pool saturation, Failover → kill master DB, Graceful degradation → full dependency outage, Idempotency → duplicate requests; процесс — Design → Implement → Chaos → Validate → Regression в CI | ✓ ПРИМЕНЯТЬ: каждый resilience pattern должен иметь сопровождающий chaos experiment в CI; «паттерн без chaos» = unverified hope 📋 ПРАВИЛО: pattern + chaos = trust; pattern alone = hope 🔗 См. Q44

## Q44. (!) Best practices и чек-лист перед экспериментом

Сводный чек-лист успешного хаос-эксперимента:

### Pre-flight чек-лист

```markdown
## Подготовка
- [ ] Hypothesis сформулирована в формате "при X → Y, steady state Z"
- [ ] Steady state metrics выбраны и мониторятся
- [ ] Blast radius явно ограничен (процент / регион / время)
- [ ] Abort condition автоматизировано
- [ ] Kill switch проверен (dry run cleanup)
- [ ] Runbook написан и review-ed
- [ ] On-call команда предупреждена
- [ ] Нет incident freeze (прод стабилен последние 24ч)
- [ ] Error budget в наличии

## Observability
- [ ] Dashboard с SLI открыт
- [ ] Alerts проверены (working as intended)
- [ ] Trace ID/correlation ID работает
- [ ] Event marker будет поставлен (annotation на графике)

## Выполнение
- [ ] Baseline метрики зафиксированы (скриншот)
- [ ] Start experiment + мониторинг 5-10 мин
- [ ] Stop experiment + наблюдение восстановления
- [ ] Post-incident screenshot метрик

## Post
- [ ] Post-mortem написан в течение 48 часов
- [ ] Action items созданы с owner + due date
- [ ] Learnings в wiki / runbooks
- [ ] Follow-up через 1-2 недели на action items
```

### Принципы выбора экспериментов

1. **Start small**: simple pod-kill до Chaos Kong
2. **Learn from incidents**: постмортемы → chaos-сценарии
3. **Prioritize by risk**: самые критичные зависимости первыми
4. **Diversify portfolio**: infrastructure + network + dependency + data
5. **Automate what works**: успешные ручные эксперименты → CI jobs

### Культурные практики

- Blameless post-mortems (смотри [культура](../behavioral/behavioral-interview.md))
- Chaos Champion в каждой команде
- Ежемесячные GameDays
- Learning-oriented, а не audit-oriented

### Что отличает senior-уровень

На собеседовании senior показывает, что chaos — не инструмент, а **способ мышления**: мы не верим, что система работает — мы **доказываем** это экспериментами. Тесты показывают, что код делает то, что мы хотим; chaos показывает, что система **держится**, когда этого не происходит.

---

## See also

- [Стратегии тестирования](test-strategies-interview.md) — куда chaos вписывается в общий ландшафт тестирования
- [Автоматизация тестов](test-automation-interview.md) — интеграция chaos в CI/CD
- [Integration Testing](integration-testing-interview.md) — Toxiproxy и fault injection в интеграционных тестах
- [Unit Testing](unit-testing-interview.md) — разница между unit-уровнем моков и chaos
- [Resilience Patterns](../architecture/resilience-patterns-interview.md) — circuit breaker, retry, bulkhead, которые валидирует chaos
- [Distributed Systems](../architecture/distributed-systems-interview.md) — теоретическая база проблем, которые chaos воспроизводит
- [Микросервисы](../architecture/microservices-interview.md) — основной контекст применения chaos
- [Observability](../monitoring/observability-interview.md) — обязательный prerequisite для chaos
- [Метрики и трейсинг](../monitoring/metrics-tracing-interview.md) — без них невозможно анализировать эксперименты
- [Kubernetes](../devops/kubernetes-interview.md) — платформа для Chaos Mesh и Litmus
- [Стратегии деплоя](../cicd/deployment-strategies-interview.md) — canary/blue-green как способ ограничить blast radius
- [Pipeline Design](../cicd/pipeline-design-interview.md) — встраивание chaos в CI/CD
