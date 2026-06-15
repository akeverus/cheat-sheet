---
title: "Вопросы на собеседовании: Стратегии деплоя"
description: "Полное руководство по стратегиям деплоя: Blue-Green, Canary, Rolling Update, Recreate, GitOps, Argo Rollouts, Helm, CI/CD пайплайны"
tags:
  - interview
  - cicd
  - deployment-strategies-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Стратегии деплоя"
  - "Стратегии деплоя собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Стратегии деплоя`

Комплексное руководство по вопросам собеседования на тему стратегий деплоя для Senior Java Developer. Охватывает основные стратегии (`Blue-Green`, `Canary`, `Rolling Update`, `Recreate`), инструменты (`Argo Rollouts`, `Helm`, `Istio`), CI/CD пайплайны и best practices.

## Полезные ссылки

### Официальная документация

- [Kubernetes Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) — стратегии обновления подов
- [Argo Rollouts](https://argoproj.github.io/argo-rollouts/) — прогрессивный деплой в Kubernetes
- [Argo CD](https://argo-cd.readthedocs.io/) — GitOps для Kubernetes
- [Helm](https://helm.sh/docs/) — менеджер пакетов Kubernetes
- [Istio Traffic Management](https://istio.io/latest/docs/concepts/traffic-management/) — управление трафиком
- [Flagger](https://docs.flagger.app/) — автоматизация Canary/Blue-Green
- [Deployment Strategies (Martin Fowler)](https://martinfowler.com/bliki/BlueGreenDeployment.html) — Blue-Green паттерн
- [Canary Deployments (Martin Fowler)](https://martinfowler.com/bliki/CanaryRelease.html) — Canary паттерн
- [Deployment Strategies](https://www.baeldung.com/ops/deployment-strategies) — обзор всех стратегий деплоя на Baeldung

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основные стратегии деплоя**
- [Q1. (!) Что такое Blue-Green деплой и когда его использовать?](#q1--что-такое-blue-green-деплой-и-когда-его-использовать)
- [Q2. (!) Что такое Canary деплой и чем он отличается от Blue-Green?](#q2--что-такое-canary-деплой-и-чем-он-отличается-от-blue-green)
- [Q3. (!) Что такое Rolling Update и как он работает в Kubernetes?](#q3--что-такое-rolling-update-и-как-он-работает-в-kubernetes)
- [Q4. Что такое Recreate стратегия и когда её применять?](#q4-что-такое-recreate-стратегия-и-когда-её-применять)
- [Q8. Что такое A/B деплой и когда его использовать?](#q8-что-такое-ab-деплой-и-когда-его-использовать)
- [Q11. Что такое immutable deployment и чем он лучше in-place update?](#q11-что-такое-immutable-deployment-и-чем-он-лучше-in-place-update)
- [Q16. Что такое dark launch и когда его применять?](#q16-что-такое-dark-launch-и-когда-его-применять)

**Zero-downtime и Rollback**
- [Q5. (!) Как обеспечить zero-downtime при деплое?](#q5--как-обеспечить-zero-downtime-при-деплое)
- [Q6. Что такое rollback и как его выполнить в Kubernetes?](#q6-что-такое-rollback-и-как-его-выполнить-в-kubernetes)
- [Q15. Как обеспечить откат (rollback) при проблемах после деплоя?](#q15-как-обеспечить-откат-rollback-при-проблемах-после-деплоя)
- [Q27. Как организовать деплой с нулевым даунтаймом для stateful приложений?](#q27-как-организовать-деплой-с-нулевым-даунтаймом-для-stateful-приложений)

**Pipeline, окружения и тестирование**
- [Q10. (!) Что такое deployment pipeline и какие этапы в него входят?](#q10--что-такое-deployment-pipeline-и-какие-этапы-в-него-входят)
- [Q12. Как организовать деплой в несколько окружений (dev, staging, prod)?](#q12-как-организовать-деплой-в-несколько-окружений-dev-staging-prod)
- [Q14. Что такое smoke test и когда его запускать при деплое?](#q14-что-такое-smoke-test-и-когда-его-запускать-при-деплое)
- [Q17. Как деплой связан с версионированием артефактов (semantic versioning)?](#q17-как-деплой-связан-с-версионированием-артефактов-semantic-versioning)
- [Q18. Что такое deployment approval и когда его требовать?](#q18-что-такое-deployment-approval-и-когда-его-требовать)

**Kubernetes и инфраструктура**
- [Q9. (!) Как настроить readiness и liveness probe для безопасного деплоя?](#q9--как-настроить-readiness-и-liveness-probe-для-безопасного-деплоя)
- [Q21. Как настроить постепенный Canary в Kubernetes (Istio, Flagger)?](#q21-как-настроить-постепенный-canary-в-kubernetes-istio-flagger)
- [Q22. Что такое deployment slots (Azure) и аналог в Kubernetes?](#q22-что-такое-deployment-slots-azure-и-аналог-в-kubernetes)
- [Q23. Как обеспечить консистентность конфигурации при деплое?](#q23-как-обеспечить-консистентность-конфигурации-при-деплое)
- [Q24. (!) Что такое deployment strategies в GitOps (Argo CD, Flux)?](#q24--что-такое-deployment-strategies-в-gitops-argo-cd-flux)

**Feature flags и эксперименты**
- [Q7. Что такое feature flags и как они связаны с деплоем?](#q7-что-такое-feature-flags-и-как-они-связаны-с-деплоем)
- [Q29. Как деплой связан с feature toggles и экспериментированием?](#q29-как-деплой-связан-с-feature-toggles-и-экспериментированием)

**База данных и зависимости**
- [Q13. Что такое database migration при деплое и как её выполнять?](#q13-что-такое-database-migration-при-деплое-и-как-её-выполнять)
- [Q19. Как деплоить приложение с зависимостями от внешних сервисов?](#q19-как-деплоить-приложение-с-зависимостями-от-внешних-сервисов)
- [Q20. Что такое blue-green для баз данных и в чём сложность?](#q20-что-такое-blue-green-для-баз-данных-и-в-чём-сложность)
- [Q28. Что такое backward/forward compatibility при деплое API?](#q28-что-такое-backwardforward-compatibility-при-деплое-api)

**Мониторинг, планирование и best practices**
- [Q25. Как мониторить успешность деплоя и когда считать деплой неудачным?](#q25-как-мониторить-успешность-деплоя-и-когда-считать-деплой-неудачным)
- [Q26. Что такое deployment window и как планировать деплой в production?](#q26-что-такое-deployment-window-и-как-планировать-деплой-в-production)
- [Q30. Как обеспечить идемпотентность и повторяемость деплоя?](#q30-как-обеспечить-идемпотентность-и-повторяемость-деплоя)

**Argo Rollouts и Helm**
- [Q31. (!) Как настроить Canary-деплой через Argo Rollouts?](#q31--как-настроить-canary-деплой-через-argo-rollouts)
- [Q32. (!) Как настроить Blue-Green деплой через Argo Rollouts?](#q32--как-настроить-blue-green-деплой-через-argo-rollouts)
- [Q33. (!) Как использовать Helm для управления деплоями?](#q33--как-использовать-helm-для-управления-деплоями)
- [Q34. Как настроить деплой через GitHub Actions в Kubernetes?](#q34-как-настроить-деплой-через-github-actions-в-kubernetes)
- [Q35. Как настроить деплой через GitLab CI в Kubernetes?](#q35-как-настроить-деплой-через-gitlab-ci-в-kubernetes)

**Feature flags, GitOps и Jenkins**
- [Q36. (!) Как реализовать feature flags с помощью Unleash или LaunchDarkly?](#q36--как-реализовать-feature-flags-с-помощью-unleash-или-launchdarkly)
- [Q37. Как работает GitOps-деплой через Argo CD на практике?](#q37-как-работает-gitops-деплой-через-argo-cd-на-практике)
- [Q38. Как настроить деплой через Jenkins Pipeline (Declarative)?](#q38-как-настроить-деплой-через-jenkins-pipeline-declarative)
- [Q39. Как реализовать DORA-метрики для оценки процесса деплоя?](#q39-как-реализовать-dora-метрики-для-оценки-процесса-деплоя)

**Стратегии деплоя** определяют, как новая версия вводится в эксплуатацию с минимальным риском и простоем. На собеседовании ожидают понимание: Blue-Green, Canary, Rolling Update, Recreate; zero-downtime и rollback; связь с [Kubernetes](../devops/kubernetes-interview.md), CI/CD, feature flags; миграции БД и [мониторинг деплоя](../monitoring/metrics-tracing-interview.md).

## Q1. (!) Что такое Blue-Green деплой и когда его использовать?

**Blue-Green** — это стратегия с двумя идентичными окружениями: в любой момент трафик пользователей идёт только на одно из них (например, blue), а второе (green) стоит в резерве. Новую версию полностью разворачивают и прогревают на резервном окружении, проверяют его в изоляции, и только потом одной операцией переключают весь трафик на green. Если после переключения всплывают проблемы — так же мгновенно возвращают трафик на blue.

Ключевая идея: **деплой и переключение трафика разнесены во времени**. Пока вы устанавливаете и проверяете green, ни один реальный запрос на него не идёт — поэтому ошибки сборки или конфигурации не задевают пользователей. Решение «переключать или нет» принимается по готовому, прогретому окружению.

Схема трафика:

- Пользователи → `Load Balancer`.
- `Load Balancer` → **Blue (`v1.0`)** — активное окружение (весь трафик идёт сюда).
- `Load Balancer` ⇢ **Green (`v1.1`)** — standby (трафик не идёт, окружение в резерве).

**Плюсы:** мгновенный откат (одно переключение назад); полная изоляция новой версии до того, как её увидят пользователи; простая ментальная модель — «или старое, или новое».

**Минусы:** нужны двойные ресурсы (два полных окружения работают одновременно); переключение по принципу «всё или ничего» — нельзя выкатить версию на 5% аудитории и понаблюдать; разделяемое состояние (БД, кэш) усложняет картину, потому что обе версии работают с одними данными.

**Когда применять:** когда допустимы двойные ресурсы и критичен быстрый, предсказуемый откат — например, для платёжных и других бизнес-критичных сервисов, где постепенная выкатка по метрикам не нужна, а цена ошибки высока.

**Как переключают трафик:** на уровне балансировщика (смена upstream), через `Ingress` (смена `backend Service`) или через DNS. Перед переключением на green обязательно прогоняют smoke-тесты, а при необходимости — ручную проверку. После переключения blue не удаляют: он остаётся развёрнутым с предыдущей версией, чтобы при инциденте вернуть на него трафик одной командой.

**Реализация в Kubernetes** — два `Deployment` с разными версиями образа и переключение selector у `Service`:

```yaml
# deployment-blue.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-blue
  labels:
    app: myapp
    version: blue
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
      version: blue
  template:
    metadata:
      labels:
        app: myapp
        version: blue
    spec:
      containers:
        - name: myapp
          image: registry.example.com/myapp:1.0.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /ready
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
---
# deployment-green.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp-green
  labels:
    app: myapp
    version: green
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
      version: green
  template:
    metadata:
      labels:
        app: myapp
        version: green
    spec:
      containers:
        - name: myapp
          image: registry.example.com/myapp:1.1.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /ready
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
---
# service.yaml — переключение selector
apiVersion: v1
kind: Service
metadata:
  name: myapp
spec:
  selector:
    app: myapp
    version: green  # переключить на blue для отката
  ports:
    - port: 80
      targetPort: 8080
```

**Практика:** образ тегировать по версии (`myapp:1.2.3`); перед переключением проверить readiness всех подов green и smoke-тесты. При откате — одна команда смены selector: `kubectl patch svc myapp -p '{"spec":{"selector":{"version":"blue"}}}'`; не удалять blue до стабилизации green.

## Q2. (!) Что такое Canary деплой и чем он отличается от Blue-Green?

**Canary** — это стратегия постепенной выкатки: новую версию запускают рядом со старой и направляют на неё небольшую долю трафика (например, 5%), а остальное по-прежнему идёт на стабильную версию. Если метрики новой версии в норме (ошибки, latency), долю шаг за шагом увеличивают (5% → 25% → 50% → 100%); при первых признаках деградации — трафик возвращают на старую.

Смысл названия — «канарейка в шахте»: малая группа реальных пользователей выступает ранним детектором проблем. Так вы ограничиваете радиус поражения: если версия дефектна, её увидят 5% аудитории, а не все.

Схема трафика и обратной связи:

- Пользователи → `Load Balancer / Ingress`.
- `Load Balancer` направляет 95% трафика на **`v1.0` (stable)** и 5% на **`v1.1` (canary)**.
- Canary-версия `v1.1` отдаёт **`Prometheus` метрики**.
- По метрикам решают: если **ОК** — увеличить долю canary (балансировщик повышает процент); если **ошибки** — откатить трафик обратно на `v1.0`.

**Чем отличается от Blue-Green.** Blue-Green переключает 100% трафика мгновенно и проверяет новую версию *до* того, как она увидит пользователей; Canary впускает реальный трафик *постепенно* и проверяет версию *в бою*, по живым метрикам. Отсюда три практических различия: Canary вводит версию плавно, требует меньше ресурсов (достаточно 1–2 canary-подов вместо второго полного окружения), но сложнее технически — нужен механизм взвешенного распределения трафика по версиям. Выбирают Canary, когда важно минимизировать риск и есть возможность наращивать долю по метрикам.

| Характеристика | Blue-Green | Canary |
|---|---|---|
| Переключение трафика | Всё сразу (100%) | Постепенно (5% → 25% → 100%) |
| Ресурсы | Двойные | Минимальные (1-2 canary-пода) |
| Откат | Мгновенный (switch back) | Убрать canary-поды |
| Сложность | Низкая | Высокая (нужен traffic splitting) |
| Валидация | До переключения | В процессе, по метрикам |

Реализация в [Kubernetes](../devops/kubernetes-interview.md): два `Deployment` (старая и новая версия) и `Service / Ingress` с правилами веса. С `Istio VirtualService`:

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: myapp
spec:
  hosts:
    - myapp.example.com
  http:
    - route:
        - destination:
            host: myapp
            subset: stable
          weight: 95
        - destination:
            host: myapp
            subset: canary
          weight: 5
---
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: myapp
spec:
  host: myapp
  subsets:
    - name: stable
      labels:
        version: v1
    - name: canary
      labels:
        version: v2
```

При стабильных метриках долю v2 увеличивают до 50%, затем до 100%; при росте ошибок или latency трафик возвращают на v1. Вручную следить за метриками на каждом шаге неудобно, поэтому процесс автоматизируют: `Flagger` и `Argo Rollouts` сами читают метрики из [Prometheus](../monitoring/metrics-tracing-interview.md), сравнивают их с заданными порогами и либо продвигают версию на следующий шаг, либо автоматически откатывают её — без участия человека.

## Q3. (!) Что такое Rolling Update и как он работает в Kubernetes?

`Rolling Update` — это стратегия по умолчанию в Kubernetes: новая версия выкатывается постепенно, под за подом. Kubernetes создаёт под новой версии, ждёт, пока он пройдёт `readinessProbe` и начнёт получать трафик, затем gracefully завершает один под старой версии — и так по кругу, пока все поды не обновятся. В любой момент часть подов уже на новой версии, часть ещё на старой, а суммарная ёмкость не проседает. Задаётся в `Deployment` через `strategy.type: RollingUpdate`.

Порядок выкатки по шагам (участники: `Kubernetes`, поды `v1`, поды `v2`, `Service`):

1. `Kubernetes` создаёт под `v2` #1.
2. Под `v2` #1 проходит `readinessProbe` (OK) и сообщает об этом `Service`.
3. `Service` направляет трафик на под `v2` #1.
4. `Kubernetes` завершает под `v1` #1 (graceful).
5. `Kubernetes` создаёт под `v2` #2.
6. Под `v2` #2 проходит `readinessProbe` (OK) → `Service` направляет на него трафик.
7. `Kubernetes` завершает под `v1` #2 (graceful).
8. `Kubernetes` создаёт под `v2` #3.
9. Под `v2` #3 проходит `readinessProbe` (OK).
10. `Kubernetes` завершает под `v1` #3 (graceful).

Итог: обновление завершено, все поды на `v2`.

Темп выкатки регулируют два параметра — они задают компромисс между скоростью и доступностью:

- **`maxSurge`** — сколько подов разрешено создать *сверх* желаемого числа реплик. Чем больше, тем быстрее идёт обновление (можно поднимать новые поды, не дожидаясь удаления старых).
- **`maxUnavailable`** — сколько подов разрешено держать недоступными во время выкатки. `maxUnavailable: 0` означает, что число доступных подов никогда не опускается ниже желаемого — это и даёт нулевой простой при достаточной ёмкости кластера.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1        # макс 4 пода (3 + 1)
      maxUnavailable: 0   # всегда минимум 3 пода доступны
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
        - name: myapp
          image: registry.example.com/myapp:1.1.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /ready
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
          livenessProbe:
            httpGet:
              path: /health
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
      terminationGracePeriodSeconds: 30
```

**Плюсы:** не нужны двойные ресурсы (в отличие от Blue-Green); обновление постепенное и безопасное за счёт readiness-проверок.

**Минусы:** во время выкатки старая и новая версии работают одновременно — приложение и схема БД должны быть к этому готовы (обратная совместимость); откат — это не мгновенное переключение, а такой же постепенный rolling обратно к предыдущей версии.

**Компромисс скорость/доступность:** при `maxUnavailable: 0` и `maxSurge: 1` обновление идёт медленнее, зато гарантирован нулевой простой. При значениях по умолчанию (`maxSurge: 25%`, `maxUnavailable: 25%`) выкатка быстрее, но часть подов может быть временно недоступна.

## Q4. Что такое Recreate стратегия и когда её применять?

**Recreate** — самая простая стратегия: Kubernetes сначала останавливает *все* поды старой версии, и только потом создаёт поды новой. Между этими шагами рабочих подов нет совсем, поэтому стратегия осознанно допускает простой на время перезапуска. Это плата за главное свойство: старая и новая версии никогда не работают одновременно.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 3
  strategy:
    type: Recreate  # все поды убиваются, затем создаются заново
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
        - name: myapp
          image: registry.example.com/myapp:2.0.0
          ports:
            - containerPort: 8080
```

**Когда применять.** Recreate уместен ровно в двух ситуациях: либо приложение принципиально не выдерживает одновременной работы двух версий (например, миграция БД с breaking change, где старая и новая схемы несовместимы), либо краткий простой допустим (внутренние сервисы, согласованное окно обслуживания). При обновлении образа все поды текущей ревизии завершаются с учётом `terminationGracePeriodSeconds`, и только затем поднимаются поды с новым образом.

Именно отсутствие сосуществования версий — главное отличие от `RollingUpdate` — делает Recreate подходящим для `StatefulSet` с общим хранилищем и для случаев несовместимой миграции схемы. Окно простоя минимизируют быстрым стартом приложения и readiness probe с коротким `initialDelaySeconds`.

**Практика:** планировать `Recreate` в deployment window; перед применением выполнить миграцию БД (если нужна), затем обновить образ. Для минимизации простоя — быстрый старт приложения (Spring Boot с `spring.main.lazy-initialization=true` для dev).

## Q5. (!) Как обеспечить zero-downtime при деплое?

Zero-downtime — это деплой, при котором ни один пользовательский запрос не падает с ошибкой. Любая стратегия обеспечивает его одинаковым принципом: **новый под начинает получать трафик только после подтверждённой готовности, а старый прекращает — только после того, как корректно дослужит текущие запросы.** Конкретные реализации:

1. **`Rolling Update` с `maxUnavailable: 0`** и readiness probe — новые поды получают трафик только когда готовы, и старые не выводятся, пока нет замены.
2. **`Blue-Green`** — трафик переключают только после того, как новое окружение полностью прогрето и проверено.
3. **`Canary`** — трафик переводят постепенно, наблюдая за метриками.

**Zero-Downtime деплой** строится на трёх стратегиях, каждая опирается на свой набор механизмов:

- **`Rolling Update`** (`maxUnavailable: 0`) → `readinessProbe`, graceful shutdown, connection draining.
- **`Blue-Green`** (мгновенное переключение) → `readinessProbe`, smoke tests.
- **`Canary`** (постепенный трафик) → `readinessProbe`, анализ метрик.

Самой стратегии мало — без перечисленного ниже даже Rolling Update будет ронять запросы при каждом обновлении. Обязательные компоненты:

- **readiness probe** — под не включается в `Service` (не получает трафик), пока не отрапортует о готовности; так клиенты не попадают на ещё не прогретый под.
- **graceful shutdown** — при остановке под дослуживает уже начатые запросы и только потом завершается (`terminationGracePeriodSeconds`); иначе активные соединения оборвутся.
- **connection draining** — балансировщик перестаёт слать новые запросы на выводимый под, но даёт завершиться текущим.
- **preStop hook** — короткая задержка перед остановкой, чтобы под успел де-регистрироваться из `Service` и эндпоинты разошлись по кластеру; закрывает гонку между удалением пода и обновлением списка эндпоинтов.

```yaml
# Пример пода с graceful shutdown
spec:
  terminationGracePeriodSeconds: 30
  containers:
    - name: myapp
      lifecycle:
        preStop:
          exec:
            command: ["sh", "-c", "sleep 5"]  # даём время на де-регистрацию
      readinessProbe:
        httpGet:
          path: /ready
          port: 8080
        initialDelaySeconds: 10
        periodSeconds: 5
        failureThreshold: 3
```

Для Spring Boot приложения — включить graceful shutdown:

```yaml
# application.yml
server:
  shutdown: graceful
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
```

## Q6. Что такое rollback и как его выполнить в Kubernetes?

**Rollback** — это возврат приложения к предыдущей (или конкретно заданной) рабочей версии после неудачного деплоя. В Kubernetes для этого не нужно пересобирать или передеплоивать вручную: контроллер хранит историю ревизий `Deployment`, и откат — это переход на одну из них.

```bash
# Посмотреть историю ревизий
kubectl rollout history deployment/myapp

# Откат к предыдущей ревизии
kubectl rollout undo deployment/myapp

# Откат к конкретной ревизии
kubectl rollout undo deployment/myapp --to-revision=3

# Дождаться завершения
kubectl rollout status deployment/myapp
```

Сколько ревизий хранить, задаёт `revisionHistoryLimit` (по умолчанию 10). Важный нюанс: откат — это не отдельный механизм, а тот же `RollingUpdate`, только применённый к старому шаблону пода; значит, он наследует те же гарантии нулевого простоя.

Главная ловушка — **откат кода не откатывает БД**. Если перед инцидентом вы выполнили миграцию схемы, то после `rollout undo` старая версия приложения встретит уже изменённую БД. Поэтому либо миграции делают обратно совместимыми (старая версия работает с новой схемой), либо откатывают и миграции тоже (`Flyway`/`Liquibase`).

**Практика:** `revisionHistoryLimit` держать достаточным (например, 10); образы предыдущих версий не удалять из registry до истечения политики хранения.

## Q7. Что такое feature flags и как они связаны с деплоем?

**Feature flags** — это переключатели в коде или конфигурации, включающие и выключающие функциональность без нового деплоя. Их главная ценность для деплоя — они **разделяют деплой и релиз**: код можно вывести в прод с выключенной фичей (никто её не видит), а включить позже и независимо — одним щелчком флага. Это снижает риск: вы развёртываете код в спокойный момент, а «релизите» функцию контролируемо. И откат становится дешевле — вместо rollback всего деплоя достаточно выключить флаг.

```java
// Spring Boot: feature flag через конфигурацию
@RestController
@RequiredArgsConstructor
public class CheckoutController {

    @Value("${feature.new-checkout:false}")
    private boolean newCheckoutEnabled;

    @GetMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request) {
        if (newCheckoutEnabled) {
            return newCheckoutService.process(request);
        }
        return legacyCheckoutService.process(request);
    }
}
```

```yaml
# application.yml — переключение через Config Server или env var
feature:
  new-checkout: false  # включить: true
```

**Типичный поток:** деплой новой версии с фичей за флагом (off) → smoke test → включение флага для части пользователей → мониторинг → полное включение либо откат флага. При инциденте флаг отключают, деплой при этом трогать не нужно.

**Инструменты:** `LaunchDarkly`, `Unleash`, `Spring Cloud Config`, кастомные флаги в БД или конфиге. Для аварийного отключения важна скорость распространения флага: чтобы переключение срабатывало за секунды, используют внешний сервис с низкой задержкой или `Redis` с TTL 10–30 с.

## Q8. Что такое A/B деплой и когда его использовать?

**A/B деплой** — это одновременная работа двух вариантов: часть пользователей получает версию A, часть — версию B, а решение принимают по сравнению бизнес-метрик (конверсия, ошибки, latency).

Технически A/B похож на Canary, но цель у него другая. **Canary отвечает на вопрос «безопасна ли новая версия» — это про надёжность; A/B отвечает на вопрос «какой вариант лучше для бизнеса» — это про продуктовую гипотезу.** Поэтому в A/B трафик часто делят не случайно, а по сегментам (регион, тип устройства, когорта), и наблюдают не минуты, а недели. Реализуют через Canary-маршрутизацию (трафик по версиям) или через feature flags (оба варианта в одной версии). Применяют для экспериментов — новый UI, алгоритм ранжирования — чтобы принять решение на данных, а не на догадках.

**Реализация через Ingress с routing по header:**

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: myapp-canary
  annotations:
    nginx.ingress.kubernetes.io/canary: "true"
    nginx.ingress.kubernetes.io/canary-by-header: "X-Experiment"
    nginx.ingress.kubernetes.io/canary-by-header-value: "variant-b"
spec:
  rules:
    - host: myapp.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: myapp-variant-b
                port:
                  number: 80
```

**Практика:** метрики помечать лейблом варианта (A/B) в [Prometheus / Grafana](../monitoring/metrics-tracing-interview.md); решение о полном переходе — по статистической значимости и минимальному времени эксперимента (1-2 недели). При регрессии по ошибкам или latency — откат варианта B.

## Q9. (!) Как настроить readiness и liveness probe для безопасного деплоя?

У трёх probe — три разные роли, и для безопасного деплоя важно их не путать:

- **`readinessProbe`** отвечает на вопрос «можно ли слать на под трафик». Пока probe не успешен, `Service` не включает под в список эндпоинтов. При деплое это ключ к нулевому простою: новый под не получит ни одного запроса, пока не прогреется.
- **`livenessProbe`** отвечает на вопрос «жив ли контейнер». Если probe стабильно падает (приложение зависло, дедлок), Kubernetes перезапускает контейнер.
- **`startupProbe`** прикрывает медленный старт: пока он не прошёл, readiness и liveness не запускаются, и приложение не убивают за то, что оно «слишком долго» поднимается.

Опасная ошибка — повесить liveness на тот же тяжёлый эндпоинт, что и readiness: при временной перегрузке БД liveness начнёт валиться и Kubernetes устроит каскад перезапусков вместо того, чтобы просто на время убрать под из ротации.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  template:
    spec:
      containers:
        - name: myapp
          image: registry.example.com/myapp:1.0.0
          ports:
            - containerPort: 8080
          # Готовность принимать трафик
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 15
            periodSeconds: 5
            timeoutSeconds: 3
            failureThreshold: 3
            successThreshold: 1
          # Жив ли контейнер
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
            timeoutSeconds: 5
            failureThreshold: 3
          # Стартовый probe (Kubernetes 1.20+)
          startupProbe:
            httpGet:
              path: /actuator/health
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 5
            failureThreshold: 30  # даёт до 155 сек на старт
```

Для Spring Boot не нужно писать health-эндпоинты вручную: Actuator из коробки (с версии 2.3) отдаёт `/actuator/health/readiness` и `/actuator/health/liveness`, причём readiness уже учитывает фазы запуска приложения. `startupProbe` (Kubernetes 1.20+) держит readiness и liveness выключенными до завершения старта — благодаря большому `failureThreshold` он даёт медленно стартующему приложению (здесь до 155 секунд) подняться, не рискуя быть убитым liveness-проверкой.

## Q10. (!) Что такое deployment pipeline и какие этапы в него входят?

**Deployment pipeline** — это автоматизированная цепочка этапов, проводящая изменение от коммита до продакшена. Центральный принцип — **build once, deploy many**: артефакт (образ с тегом по git SHA) собирается ровно один раз, а дальше один и тот же неизменный артефакт промотируется по окружениям. Так вы тестируете и катите в прод *именно тот бинарник*, что прошёл проверки, а не пересобранную копию, которая теоретически может отличаться.

Этапы пайплайна по порядку:

Коммит → Build → Unit Tests → Docker Build → Push Registry → Deploy Dev → Integration Tests → Deploy Staging → E2E Tests → **Approval** → (при «ОК») Deploy Prod → Smoke Tests → Мониторинг → (при ошибках) Rollback.

Ключевые ветвления: на этапе **Approval** деплой в prod идёт только после подтверждения («ОК»); на этапе **Мониторинг** при обнаружении ошибок запускается **Rollback**.

Раз артефакт неизменен, то всё, что различается между окружениями, выносят наружу: конфигурация, переменные и секреты подставляются по окружению, а образ остаётся тем же. Этапы выстраивают по принципу «fail fast» — самые быстрые и дешёвые проверки (unit-тесты) идут первыми, дорогие (e2e, ручной approval) — ближе к проду. Подробнее о проектировании пайплайнов — в [вопросах по CI/CD пайплайнам](pipeline-design-interview.md).

**Практика:** один и тот же образ (`myapp:${GIT_SHA}` или semver) промотировать по окружениям; конфигурация — `ConfigMap / Secrets` по окружению. После деплоя в prod — этап проверки метрик (error rate, latency); при деградации — автоматический rollback (`Flagger`, `Argo Rollouts`) или алерт.

## Q11. Что такое immutable deployment и чем он лучше in-place update?

**Immutable deployment** — это деплой, при котором работающий сервер или контейнер никогда не правят «на месте»: новая версия — это всегда новый образ или инстанс, а старый целиком уничтожается. Противоположность — **in-place update**, когда код и конфигурацию обновляют на том же инстансе (зашли по SSH, подменили jar, перезапустили).

Преимущество immutable вытекает из одной идеи: **раз инстанс никто не трогает руками, его состояние полностью определяется образом**. Отсюда предсказуемость, простой откат и отсутствие «дрейфа конфигурации» — расхождения между серверами, которое накапливается, когда их годами правят по отдельности.

| Характеристика | Immutable | In-place |
|---|---|---|
| Предсказуемость | Высокая (образ = артефакт) | Низкая (дрейф конфигурации) |
| Откат | Развернуть предыдущий образ | Сложно воспроизвести |
| Одинаковость инстансов | Гарантирована | Может расходиться |
| Скорость деплоя | Зависит от образа | Быстрее (замена файлов) |
| Аудит | Полный (тег образа → версия кода) | Сложно отследить |

В [Kubernetes](../devops/kubernetes-interview.md) деплой по сути immutable: при обновлении образа создаются новые поды с новым образом, старые удаляются; конфигурация инжектируется через `ConfigMap / Secrets`, не меняя образ. Откат — `kubectl rollout undo`.

**Практика:** не менять образ «на месте» (не exec в под и не заменять бинарник); конфигурация только через `ConfigMap / Secrets` или переменные при старте. Образ собирать в CI из кода; тег образа = версия для трассируемости и отката.

## Q12. Как организовать деплой в несколько окружений (dev, staging, prod)?

Базовая идея — **один артефакт, разная конфигурация**: образ собирается один раз и проходит через окружения, а отличается между ними только конфигурация (URL БД, лимиты ресурсов, feature flags). Окружения образуют «лестницу» уверенности: чем ближе к проду, тем строже допуск. На практике это собирают так:

1. **Один pipeline с этапами по окружениям** — в dev катит автоматически, в staging после прохождения тестов, в prod после ручного approval.
2. **Промоушен одного и того же образа** по окружениям (а не пересборка под каждое).
3. **GitOps** — отдельные каталоги (overlays) под dev, staging, prod, где хранятся различия конфигурации.

**Структура Kustomize overlays для GitOps:**

```
manifests/
├── base/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── kustomization.yaml
├── overlays/
│   ├── dev/
│   │   ├── kustomization.yaml
│   │   ├── configmap.yaml
│   │   └── replicas-patch.yaml
│   ├── staging/
│   │   ├── kustomization.yaml
│   │   └── configmap.yaml
│   └── prod/
│       ├── kustomization.yaml
│       ├── configmap.yaml
│       └── hpa.yaml
```

```yaml
# overlays/prod/kustomization.yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - ../../base
patchesStrategicMerge:
  - replicas-patch.yaml
configMapGenerator:
  - name: myapp-config
    literals:
      - SPRING_PROFILES_ACTIVE=prod
      - LOG_LEVEL=WARN
images:
  - name: registry.example.com/myapp
    newTag: 1.2.3  # обновляется CI/CD или Flux
```

**Практика:** образ один (`myapp:${GIT_SHA}`); в каждом окружении свои `ConfigMap`/`Secrets` (DB URL, feature flags). В GitOps — [Argo CD или Flux](pipeline-design-interview.md) синхронизируют кластер с выбранным overlay.

## Q13. Что такое database migration при деплое и как её выполнять?

Миграция БД — это изменение схемы или данных, которое сопровождает выход новой версии приложения; версионируют и применяют такие изменения инструментами `Flyway` или `Liquibase`.

Сложность в том, что при Rolling Update схема меняется, **пока старая и новая версии работают одновременно**. Значит, миграция не должна ломать ни одну из них. Отсюда главное правило — менять схему обратно совместимыми шагами, а не «одним махом».

**Безопасная последовательность при Rolling Update:**

Последовательность по шагам (участники: `CI/CD Pipeline`, база данных, приложение `v1`, приложение `v2`):

1. **Шаг 1 — backward-compatible миграция.** `CI/CD` применяет к БД `ALTER TABLE ADD COLUMN new_col` (nullable). С такой схемой работают и `v1`, и `v2`.
2. **Шаг 2 — Rolling Update.** `CI/CD` деплоит `v2` (использует `new_col`). При этом `v1` продолжает работать с БД, игнорируя `new_col`, а `v2` работает, используя `new_col`.
3. **Шаг 3 — cleanup миграция (следующий релиз).** `CI/CD` применяет к БД `ALTER TABLE DROP COLUMN old_col`.

**Подходы:**
1. **Backward-compatible миграции** — изменение схемы устроено так, что и старая, и новая версия приложения работают с ней; сначала применяют миграцию, потом катят код. Самый безопасный вариант.
2. **Миграция в момент деплоя** (на старте приложения) — проще, но рискованнее: при откате кода схема уже изменена, и нужны обратные миграции.

**Expand-contract pattern** — каноничный способ сделать даже «опасное» изменение совместимым, разбив его на релизы: *expand* (добавить новый столбец/таблицу, ничего не ломая) → мигрировать данные → переключить код → *contract* (удалить старое только в следующем релизе, когда старой версии в проде уже нет). Так на каждом шаге схема совместима с обеими версиями кода.

**Рекомендация:** миграции писать идемпотентными (`IF NOT EXISTS`) и отдельно тестировать откат схемы, а не только накат.

## Q14. Что такое smoke test и когда его запускать при деплое?

**Smoke test** — это минимальный набор быстрых проверок сразу после деплоя: приложение отвечает, health-эндпоинт зелёный, один-два критичных API работают. Цель не в полном покрытии, а в том, чтобы за секунды поймать грубую поломку — приложение не стартовало, не подключилось к БД, отдаёт 500 на главном пути. Запускают его сразу после выкатки в окружение, и при падении срабатывает автоматический rollback или алерт.

Название из инженерной практики: «включить устройство и посмотреть, не пошёл ли дым». Smoke test проверяет «работает ли вообще», а не «работает ли правильно во всех сценариях».

```bash
#!/bin/bash
# smoke-test.sh — запуск после деплоя
set -e

APP_URL="${1:-http://myapp.example.com}"
MAX_RETRIES=5
RETRY_INTERVAL=10

for i in $(seq 1 $MAX_RETRIES); do
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$APP_URL/actuator/health")
  if [ "$HTTP_CODE" = "200" ]; then
    echo "Health check passed"
    break
  fi
  echo "Attempt $i/$MAX_RETRIES: HTTP $HTTP_CODE, retrying in ${RETRY_INTERVAL}s..."
  sleep $RETRY_INTERVAL
done

# Проверка критичного API
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$APP_URL/api/v1/status")
if [ "$HTTP_CODE" != "200" ]; then
  echo "FAIL: /api/v1/status returned $HTTP_CODE"
  kubectl rollout undo deployment/myapp
  exit 1
fi

echo "All smoke tests passed"
```

**Что проверять:** health endpoint (200) и один-два критичных для бизнеса API. Время выполнения — секунды, не минуты. Важно не путать ответственность: smoke test не заменяет интеграционные и e2e-тесты — те более тяжёлые и гоняются *до* деплоя в prod (на staging), а smoke лишь подтверждает, что выкаченная версия жива.

## Q15. Как обеспечить откат (rollback) при проблемах после деплоя?

Готовность к откату — это не одна команда, а несколько уровней защиты, выстроенных заранее. Чем больше из них на месте, тем быстрее и спокойнее проходит инцидент:

1. **Хранить предыдущие ревизии и образы** — без истории откатывать не к чему (Kubernetes rollout history, образы в registry).
2. **Автоматический rollback по [метрикам](../monitoring/metrics-tracing-interview.md)** (ошибки, latency) — `Argo Rollouts`, `Flagger` откатывают без участия человека, что критично для скорости реакции.
3. **Ручной rollback одной командой** (`kubectl rollout undo`) — на случай, когда автоматика порог не поймала.
4. **Feature flags** — самый дешёвый откат: выключить фичу, не трогая деплой.
5. **Документированная процедура и права на откат** без долгого согласования — иначе техническая возможность есть, а откатить в три ночи некому.

**Практика:** образы предыдущих версий не удалять из registry до истечения политики хранения; `revisionHistoryLimit` в `Deployment` держать достаточным (например, 10). `Flagger / Argo Rollouts` при Canary анализируют метрики (error rate, latency) из Prometheus; при превышении порога откатывают трафик на старую версию.

Команды: `kubectl rollout undo` — откат на предыдущую ревизию; `kubectl rollout undo --to-revision=3` — к конкретной ревизии; `kubectl rollout status` — дождаться завершения. После отката проверить readiness и smoke; при миграциях БД убедиться, что старая версия приложения совместима с текущей схемой.

## Q16. Что такое dark launch и когда его применять?

**Dark launch** — это выкатка новой функциональности, которая уже развёрнута и обрабатывает реальный трафик, но её результат пользователю не показывается (или показывается лишь частично). Близкий приём — **shadow traffic** (теневой трафик): копия боевых запросов зеркалируется на новую версию, но клиент всегда получает ответ от старой.

Смысл — **проверить новый код под настоящей нагрузкой и на настоящих данных, ничем не рискуя для пользователя**. Вы видите реальные ошибки, latency и поведение под пиком ещё до того, как новая версия начнёт влиять на чей-либо опыт.

Схема маршрутизации:

- Пользователи → `Proxy / Ingress`.
- `Proxy` отправляет **основной запрос** на **`v1` (production)**, и `v1` возвращает **ответ** пользователю.
- Одновременно `Proxy` зеркалирует **shadow copy** запроса на **`v2` (dark launch)**.
- `v2` не отвечает пользователю — её результат идёт **только в логи и метрики** (мониторинг).

**Сценарий применения:** проверка нагрузки, ошибок и метрик новой версии без изменения пользовательского опыта — особенно когда переписан критичный путь (рекомендации, расчёт цены) и нужна уверенность под боевым трафиком. **Реализация:** feature flag «выполнить новый путь, но не показывать результат» либо зеркалирование запросов на уровне прокси (двойной вызов в коде или mirror в service mesh).

**Подводный камень:** теневой код всё равно ходит в БД и внешние сервисы. Если он пишет данные, отправляет письма или дёргает платёжный шлюз, такие побочные эффекты надо отключать — иначе «тень» начнёт менять реальное состояние.

```java
// Dark launch через параллельный вызов
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final LegacyRecommendationEngine legacy;
    private final NewRecommendationEngine newEngine;
    private final MeterRegistry meterRegistry;

    @Value("${feature.dark-launch-recommendations:false}")
    private boolean darkLaunchEnabled;

    public List<Product> getRecommendations(User user) {
        List<Product> result = legacy.recommend(user);

        if (darkLaunchEnabled) {
            CompletableFuture.runAsync(() -> {
                try {
                    Timer.Sample sample = Timer.start(meterRegistry);
                    newEngine.recommend(user);
                    sample.stop(meterRegistry.timer("recommendations.dark_launch"));
                } catch (Exception e) {
                    meterRegistry.counter("recommendations.dark_launch.errors").increment();
                }
            });
        }

        return result;  // всегда от legacy
    }
}
```

**Shadow traffic через Istio:**

```yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: myapp
spec:
  hosts:
    - myapp.example.com
  http:
    - route:
        - destination:
            host: myapp-v1
      mirror:
        host: myapp-v2
      mirrorPercentage:
        value: 100.0
```

## Q17. Как деплой связан с версионированием артефактов (semantic versioning)?

**Semantic versioning** (`MAJOR.MINOR.PATCH`) присваивает артефакту (образу, jar) осмысленную версию, где номер сам сообщает характер изменений: `MAJOR` — несовместимые изменения, `MINOR` — новая функциональность с обратной совместимостью, `PATCH` — багфиксы. Деплой при этом всегда разворачивает *конкретную* версию (точный тег образа), а откат — это смена тега на предыдущий. Связь с деплоем прямая: **точная версия делает деплой воспроизводимым и трассируемым** — всегда понятно, какой именно код сейчас в проде.

**Практика:** в проде использовать стабильные теги (`1.2.3`) и избегать `latest` — он плавающий, и непонятно, что реально развёрнуто, а Kubernetes может не перетянуть обновлённый образ. Для трассируемости теги привязывают к git SHA. Pipeline записывает версию в аннотации пода или метрики, чтобы на вопрос «какая версия в проде» отвечал не человек, а система.

```yaml
# Пример с версией в аннотациях
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
  annotations:
    app.kubernetes.io/version: "1.2.3"
    deploy.source/commit: "abc1234"
spec:
  template:
    metadata:
      labels:
        app.kubernetes.io/version: "1.2.3"
    spec:
      containers:
        - name: myapp
          image: registry.example.com/myapp:1.2.3  # НЕ latest
```

## Q18. Что такое deployment approval и когда его требовать?

**Deployment approval** — это ручное (или управляемое правилами) подтверждение перехода к следующему этапу пайплайна, чаще всего перед деплоем в prod. Это сознательный «человеческий gate» в автоматизированном процессе: его ставят, когда политика компании или регуляторика требуют, чтобы выкатку в продакшен явно одобрил уполномоченный человек.

**Где настраивается:** в `GitLab` — protected environments с required approvals; в `GitHub Actions` — environment с reviewers; в `Jenkins` — `input` step.

**Компромисс.** Approval повышает контроль, но замедляет доставку. При высокой частоте деплоев (несколько раз в день) ручное подтверждение на *каждый* выкат превращается в узкое место и бьёт по DORA-метрикам. Поэтому approval сужают до по-настоящему рискованных изменений (схема БД, инфраструктура), а остальное катят автоматически — но с жёсткими проверками в [pipeline](pipeline-design-interview.md) и автоматическим откатом по метрикам, который заменяет ручной контроль.

## Q19. Как деплоить приложение с зависимостями от внешних сервисов?

Корень проблемы: в распределённой системе вы не деплоите всё одновременно, поэтому в момент выкатки сервисы временно оказываются на *разных* версиях. Значит, релиз должен быть безопасен при любой комбинации версий, которая может встретиться. Отсюда подходы:

1. **Контракты и совместимость API** — новая версия обязана работать со старыми версиями зависимостей (backward compatibility), чтобы рассинхрон во время выкатки не ломал систему.
2. **Feature flags или Canary** — включать вызовы нового сервиса постепенно, а не разом.
3. **Версионирование API зависимостей** — деплоить в порядке совместимости.
4. **Интеграционные тесты в pipeline** — на моках или тестовых инстансах зависимостей, чтобы поймать несовместимость до прода.

**Практика:** contract testing (`Pact`) проверяет совместимость *до* деплоя — потребитель и провайдер согласуют контракт, и pipeline падает, если новая версия его нарушает. При breaking change порядок строгий: сначала провайдер с поддержкой обеих версий API, потом потребители.

Порядок деплоя: при обратно совместимых изменениях (новые поля, старые не удалены) можно деплоить потребителей и провайдеров в любом порядке. При breaking change — сначала деплой провайдера с поддержкой старого и нового контракта, затем потребителей на новый контракт, затем удаление старого в провайдере.

## Q20. Что такое blue-green для баз данных и в чём сложность?

**Blue-Green для БД** — это перенос идеи двух окружений на базу данных: поднимают вторую БД (green), применяют к ней миграции, а затем переключают на неё приложение. Звучит так же просто, как Blue-Green для stateless-сервисов, но **на деле гораздо сложнее, потому что у БД есть состояние, которое непрерывно меняется**.

Откуда сложность:

- **Синхронизация данных** — пока приложение пишет в blue, green должна получать те же изменения, иначе после переключения часть данных потеряется.
- **Миграции схемы на green** должны быть совместимы с потоком репликации из blue.
- **Переключение соединений** без потери незавершённых транзакций.
- **Откат не симметричен переключению вперёд:** если на green уже успели записать, простым возвратом на blue эти данные не вернуть — нужен обратный перенос.

Схема:

- Приложение → **`DB Proxy / PgBouncer`**.
- Прокси активно указывает на **Blue DB (`v1` schema)**.
- Прокси готов переключиться на **Green DB (`v2` schema)** — реплику, к которой уже применены миграции.
- Между базами идёт **логическая репликация**: Blue DB → Green DB.

Типичный сценарий: blue — текущая prod БД; green — копия (реплика или дамп + репликация). На green выполняют миграции; приложение переключают на green (смена connection string или переключение прокси). Риски: расхождение данных за время репликации; откат приложения требует отката и данных (если на green уже писали). Для нулевого простоя используют логическую репликацию (`pg_logical`) и переключение с минимальным окном.

## Q21. Как настроить постепенный Canary в Kubernetes (Istio, Flagger)?

Здесь два уровня. **Istio** даёт сам механизм — `VirtualService` распределяет трафик по версиям в заданных процентах. Но Istio только *исполняет* веса; решать, когда их менять, по умолчанию приходится человеку. **Flagger** добавляет автоматику поверх: это контроллер, который сам создаёт Canary-копию Deployment, на каждом шаге увеличивает её долю трафика, проверяет метрики и при росте ошибок откатывает выкатку — то есть превращает ручную процедуру в управляемую по метрикам.

**Настройка Flagger:**

```yaml
apiVersion: flagger.app/v1beta1
kind: Canary
metadata:
  name: myapp
  namespace: production
spec:
  targetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp
  service:
    port: 80
    targetPort: 8080
  analysis:
    interval: 30s           # интервал проверки метрик
    threshold: 5             # макс неудачных проверок до отката
    maxWeight: 50            # макс % трафика на canary
    stepWeight: 10           # шаг увеличения (10% → 20% → ... → 50%)
    metrics:
      - name: request-success-rate
        thresholdRange:
          min: 99            # минимум 99% успешных запросов
        interval: 1m
      - name: request-duration
        thresholdRange:
          max: 500           # макс latency p99 = 500ms
        interval: 1m
    webhooks:
      - name: smoke-test
        type: pre-rollout
        url: http://flagger-loadtester/
        metadata:
          cmd: "curl -s http://myapp-canary/health"
```

**Практика:** шаги (10% → 20% → 50% → 100%) и интервалы между шагами; пороги (допустимый рост error rate, p99). При превышении порога Flagger откатывает трафик на старую версию автоматически.

## Q22. Что такое deployment slots (Azure) и аналог в Kubernetes?

**Deployment slots** в Azure App Service — это отдельные слоты приложения (например, staging и production), между которыми можно сделать **swap** — мгновенно поменять их местами. По сути это управляемый Blue-Green «из коробки»: новую версию выкатывают и прогревают в staging-слоте, а затем одной операцией swap делают его продакшеном.

В Kubernetes прямого аналога нет, но идея воспроизводится через Blue-Green: два `Deployment` и переключение `Service` (через selector) либо трафика через `Ingress` / Istio. То есть «swap» в Kubernetes — это смена того, на какие поды указывает `Service`.

**Практика:** в Kubernetes два `Deployment` — `myapp-staging` и `myapp-prod`; один `Service` с selector по label `version: prod`. Для «swap» меняют selector на `version: staging` (теперь трафик идёт на staging-поды).

## Q23. Как обеспечить консистентность конфигурации при деплое?

Враг консистентности — конфигурация, размазанная по местам и правленная вручную: со временем окружения расходятся, и поведение становится непредсказуемым. Лекарство — **единый источник правды на каждое окружение и никакого ручного копирования.** Подходы:

1. **Конфигурация в репозитории** (GitOps) или в едином хранилище (`ConfigMap`, `Vault`, `Spring Cloud Config`) — один источник вместо разрозненных файлов.
2. **Один набор конфигов на окружение**, а не копипаст между серверами.
3. **Секреты вне кода и образа** — инжектируются при старте (`Kubernetes Secrets`, `Vault`); это и безопасность, и тот же принцип единого источника.
4. **Проверка конфигурации в pipeline** — валидация до выката, чтобы битый конфиг не доехал до прода.

```yaml
# Sealed Secrets для безопасного хранения секретов в Git
apiVersion: bitnami.com/v1alpha1
kind: SealedSecret
metadata:
  name: myapp-secrets
  namespace: production
spec:
  encryptedData:
    DB_PASSWORD: AgBy3i4OJSWK+PiTySYZZA9...  # зашифровано публичным ключом
    API_KEY: AgCtr84KLQWP+QiSzZRRA7...
```

**Практика:** в GitOps (Argo CD, Flux) конфигурация хранится в [Git](../devops/git-interview.md); смена коммита триггерит синхронизацию. Один источник правды на окружение (каталог `overlays/prod/`) устраняет расхождения.

## Q24. (!) Что такое deployment strategies в GitOps (Argo CD, Flux)?

В GitOps желаемое состояние кластера целиком описано в Git, а инструмент (Argo CD, Flux) непрерывно подтягивает кластер к этому описанию. Принципиально важно: **GitOps — это не ещё одна стратегия деплоя, а способ доставки**. Сами стратегии (Rolling, Blue-Green, Canary) остаются прежними, меняется лишь то, *как* они запускаются — не командой `kubectl apply` от человека или CI, а коммитом в Git, который контроллер применяет сам.

Из этого вытекает ключевое свойство: раз источник правды — Git, то **деплой = коммит, а откат = `git revert`**, и вся история изменений кластера лежит в репозитории.

Поток GitOps:

- Разработчик делает **push** в **`Git Repo`**.
- `Argo CD` выполняет **sync** с `Git Repo`.
- `Argo CD` делает **apply** в **`Kubernetes Cluster`**.
- Кластер возвращает **status** в `Argo CD`.
- `Argo CD` сверяет **diff / status** с `Git Repo` (контроль соответствия желаемого состояния).

**Argo CD Application:**

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: myapp-prod
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://gitlab.example.com/team/manifests.git
    targetRevision: main
    path: overlays/prod
  destination:
    server: https://kubernetes.default.svc
    namespace: production
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
```

Для Canary / Blue-Green используют `Argo Rollouts` — отдельный CRD `Rollout` вместо `Deployment`; в Git хранят `Rollout` с шагами Canary. Flux использует `Kustomization` с указанием на репо и путь; при изменении образа в репо Flux обновляет `Deployment`.

## Q25. Как мониторить успешность деплоя и когда считать деплой неудачным?

Ключевая мысль: «деплой прошёл» не значит «деплой успешен». Под завёлся и smoke зелёный — это только старт; реальный критерий успеха — **поведение под боевым трафиком относительно того, что было до выката**. Поэтому смотрят на error rate, latency (p50, p99) и throughput и сравнивают их с периодом до деплоя или с baseline. Деплой признают неудачным, когда:

- упал smoke test;
- в окне наблюдения после деплоя (5–15 минут) превышен порог по ошибкам или latency;
- поступил ручной репорт об инциденте.

Инструменты: [Prometheus + Grafana](../monitoring/observability-interview.md), Datadog, Argo Rollouts / Flagger с автоматическим rollback. Дополнительно: [трассировка запросов](../monitoring/metrics-tracing-interview.md) (`Jaeger`, `Zipkin`) — сравнить latency до и после; логи ошибок — новые стектрейсы после деплоя.

В pipeline — этап «мониторинг после деплоя» (5-10 минут) с автоматическим откатом при нарушении условий. Окно наблюдения задают по опыту; слишком короткое может пропустить постепенную деградацию, слишком длинное — задержать откат.

## Q26. Что такое deployment window и как планировать деплой в production?

**Deployment window** — это заранее согласованное временное окно (например, ночь или выходные), в которое разрешён деплой в production. Логика проста: если выкатка несёт риск простоя, её планируют на время минимальной нагрузки, чтобы при инциденте задеть как можно меньше пользователей.

**Планирование сводится к подготовке к худшему сценарию:** выбрать окно по графику нагрузки и доступности дежурных; уведомить стейкхолдеров; заранее иметь готовый rollback-план; при необходимости автоматизировать выкат внутри окна.

Стоит понимать: deployment window — это **признак того, что деплой ещё рискован**. Команды, инвестировавшие в zero-downtime, Canary и автооткат, стремятся избавиться от окон вовсе — перейти на continuous deployment, где безопасный выкат возможен в любое время, потому что риск снят технически, а не календарём.

**Практика:** зафиксировать окно в runbook (например, «вторник/четверг 02:00-04:00 UTC»); перед окном — чек-лист (образ собран, тесты зелёные, rollback-план готов). При CD без окон — деплой в любое время с Canary + [Prometheus](../monitoring/metrics-tracing-interview.md).

## Q27. Как организовать деплой с нулевым даунтаймом для stateful приложений?

Сложность stateful-приложений в том, что они держат состояние локально (сессии, кэш в памяти, лидерство), а деплой убивает поды — вместе с этим состоянием. Поэтому главная стратегия — **сделать поды как можно более stateless, вынеся состояние наружу**, и тогда обычный Rolling Update снова работает. Подходы по убыванию важности:

1. **Вынести состояние во внешнее хранилище** (`Redis`, БД) — после этого потеря пода не означает потерю данных, и поды становятся взаимозаменяемыми.
2. **Graceful shutdown** — под перестаёт принимать новый трафик и дослуживает текущие запросы перед остановкой, чтобы не оборвать активные операции.
3. **Rolling Update с достаточным числом реплик и readiness probe** — чтобы во время выкатки оставалась рабочая ёмкость.

```yaml
# Deployment с graceful shutdown для stateful приложения
apiVersion: apps/v1
kind: Deployment
metadata:
  name: myapp
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    spec:
      terminationGracePeriodSeconds: 60
      containers:
        - name: myapp
          image: registry.example.com/myapp:1.0.0
          env:
            - name: SPRING_SESSION_STORE_TYPE
              value: redis  # сессии в Redis, не в памяти
          lifecycle:
            preStop:
              exec:
                command:
                  - sh
                  - -c
                  - "sleep 5 && curl -X POST localhost:8080/actuator/shutdown"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
```

Отдельные случаи требуют аккуратного завершения. При **leader election** уходящий под должен явно передать лидерство, иначе кластер потеряет время на перевыборы. Для **`Kafka` consumer group** под обязан корректно отработать `SIGTERM`: выйти из группы, закоммитить офсеты и только потом завершиться — иначе после ребаланса часть сообщений обработается повторно.

## Q28. Что такое backward/forward compatibility при деплое API?

Эти два свойства описывают, выдержит ли API рассинхрон версий, который неизбежен во время деплоя (клиенты и серверы обновляются не одновременно):

- **Backward compatibility** — новая версия сервера понимает запросы старых клиентов: новые поля делают опциональными, старые не удаляют сразу. То есть «новый код дружит со старыми запросами».
- **Forward compatibility** — старый сервер не падает на неизвестных полях, а игнорирует лишнее. То есть «старый код переживает запросы из будущего».

Почему это важно для деплоя: при Rolling Update в один момент в проде есть и старая, и новая версия, и трафик ходит между ними в обе стороны. Совместимость в обе стороны — это то, что позволяет выкатывать сервисы по очереди, не координируя «big bang».

При деплое микросервисов — трёхэтапный подход:

1. **Этап 1:** деплой сервера `v2` (поддержка контракта `v1` + `v2`).
2. **Этап 2:** деплой клиентов (переход на контракт `v2`).
3. **Этап 3:** деплой сервера `v3` (удаление контракта `v1`).

Нарушение совместимости требует версионирования API или координации «big bang» деплоя. Contract testing (`Pact`) проверяет совместимость до деплоя.

## Q29. Как деплой связан с feature toggles и экспериментированием?

**Feature toggles** (они же feature flags) позволяют включать и выключать функциональность без нового деплоя. Главное следствие — **деплой и релиз перестают быть одним событием**: код едет в прод за выключенным флагом и никого не задевает, а собственно «релиз» — это отдельное действие, включение флага, которое можно сделать в удобный момент, для части пользователей и мгновенно отменить.

```java
// Интеграция с Unleash (open-source feature toggle)
@Component
@RequiredArgsConstructor
public class FeatureToggleService {

    private final Unleash unleash;

    public boolean isEnabled(String featureName) {
        return unleash.isEnabled(featureName);
    }

    public boolean isEnabled(String featureName, UnleashContext context) {
        return unleash.isEnabled(featureName, context);
    }
}

// Использование в контроллере
@GetMapping("/checkout")
public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request) {
    UnleashContext context = UnleashContext.builder()
        .userId(request.getUserId())
        .build();

    if (featureToggleService.isEnabled("new-checkout", context)) {
        return newCheckoutService.process(request);
    }
    return legacyCheckoutService.process(request);
}
```

Экспериментирование: постепенное включение по процентам трафика или по сегментам; метрики (конверсия, ошибки) определяют успех; затем полное включение или откат. При инциденте отключить флаг в UI (`LaunchDarkly`, `Unleash`).

## Q30. Как обеспечить идемпотентность и повторяемость деплоя?

**Идемпотентность деплоя** означает, что повторный запуск с теми же артефактами даёт тот же результат: ресурсы не дублируются, состояние не ломается. Это снимает страх «а что будет, если запустить ещё раз» — после сбоя или ретрая pipeline можно спокойно прогнать заново.

Достигается это **декларативным подходом — описываем желаемое состояние, а не последовательность команд**. Инструмент сам приводит систему к описанию, сколько бы раз его ни запускали:

- **Декларативные манифесты** — [Kubernetes](../devops/kubernetes-interview.md) `apply` идемпотентен: применяет desired state, а не императивные шаги.
- **Terraform** — `plan + apply` воспроизводим: применяет только разницу между текущим и желаемым.
- **Helm** — `helm upgrade --install` идемпотентен: ставит релиз, если его нет, обновляет, если есть.
- **Миграции БД** — идемпотентные скрипты (`IF NOT EXISTS`), безопасные к повторному прогону.
- **CI/CD pipeline** — одинаковый результат при повторном запуске с тем же коммитом.

```yaml
# Helm upgrade --install — идемпотентная команда
# Если релиз не существует — установит, если существует — обновит
helm upgrade --install myapp ./charts/myapp \
  --namespace production \
  --set image.tag=1.2.3 \
  --wait \
  --timeout 5m
```

**Практика:** не использовать императивные команды (`kubectl create`) в CI — только `kubectl apply` или `helm upgrade --install`. Образ с фиксированным тегом (не `latest`). Pipeline должен давать одинаковый результат при re-run.

## Q31. (!) Как настроить Canary-деплой через Argo Rollouts?

**Argo Rollouts** — это Kubernetes-контроллер для прогрессивного деплоя. Идея в том, что встроенный `Deployment` умеет только Rolling Update и Recreate, а полноценные Canary и Blue-Green с автоматическим анализом метрик в нём не выразить. Argo Rollouts решает это, заменяя `Deployment` на свой CRD `Rollout`: в нём прямо в манифесте описываются шаги выкатки (`setWeight`, `pause`, `analysis`), а контроллер сам ведёт версию по этим шагам, сверяясь с метриками из Prometheus.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Rollout
metadata:
  name: myapp
  namespace: production
spec:
  replicas: 5
  revisionHistoryLimit: 3
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
        - name: myapp
          image: registry.example.com/myapp:1.2.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5
  strategy:
    canary:
      canaryService: myapp-canary    # Service для canary-подов
      stableService: myapp-stable    # Service для stable-подов
      trafficRouting:
        istio:
          virtualServices:
            - name: myapp-vsvc
              routes:
                - primary
      steps:
        - setWeight: 10              # 10% трафика на canary
        - pause: { duration: 2m }    # ждём 2 минуты
        - analysis:                   # анализ метрик
            templates:
              - templateName: success-rate
            args:
              - name: service-name
                value: myapp-canary
        - setWeight: 30
        - pause: { duration: 2m }
        - analysis:
            templates:
              - templateName: success-rate
        - setWeight: 60
        - pause: { duration: 5m }
        - setWeight: 100              # полное переключение
---
# AnalysisTemplate — анализ метрик из Prometheus
apiVersion: argoproj.io/v1alpha1
kind: AnalysisTemplate
metadata:
  name: success-rate
spec:
  args:
    - name: service-name
  metrics:
    - name: success-rate
      interval: 30s
      count: 3
      successCondition: result[0] >= 0.99
      failureLimit: 1
      provider:
        prometheus:
          address: http://prometheus.monitoring:9090
          query: |
            sum(rate(http_server_requests_seconds_count{
              service="{{args.service-name}}",
              status!~"5.*"
            }[2m])) /
            sum(rate(http_server_requests_seconds_count{
              service="{{args.service-name}}"
            }[2m]))
```

Шаги выкатки и ветвления:

1. **Rollout обновлён** → выставляется **10% canary**.
2. Через 2 минуты — **анализ метрик**: при **OK** переход к **30% canary**; при **FAIL** — **откат**.
3. Через 2 минуты — **анализ метрик**: при **OK** переход к **60% canary**; при **FAIL** — **откат**.
4. Через 5 минут — **100% (promotion)**: версия полностью продвинута.

Управление:

```bash
# Просмотр статуса
kubectl argo rollouts get rollout myapp -w

# Ручное продвижение (если pause без duration)
kubectl argo rollouts promote myapp

# Принудительный откат
kubectl argo rollouts abort myapp

# Повторная попытка после отката
kubectl argo rollouts retry rollout myapp
```

## Q32. (!) Как настроить Blue-Green деплой через Argo Rollouts?

`Argo Rollouts` реализует Blue-Green через тот же CRD `Rollout`, но с двумя сервисами: `activeService` получает боевой трафик, `previewService` указывает на новую версию. При обновлении контроллер поднимает новые (preview) поды рядом со старыми, не переключая на них трафик; вы проверяете их через preview-сервис, и только потом — вручную (`promote`) или автоматически — `activeService` переключается на новую версию.

Ценность поверх «ручного» Blue-Green из Q1 — встроенные хуки анализа: `prePromotionAnalysis` гоняет проверки на preview *до* переключения, `postPromotionAnalysis` следит за метриками *после*, а `scaleDownDelaySeconds` держит старые поды живыми ещё какое-то время, чтобы откат был мгновенным.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Rollout
metadata:
  name: myapp
  namespace: production
spec:
  replicas: 3
  revisionHistoryLimit: 3
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
        - name: myapp
          image: registry.example.com/myapp:2.0.0
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
  strategy:
    blueGreen:
      activeService: myapp-active      # Service, который получает prod-трафик
      previewService: myapp-preview    # Service для preview (новая версия)
      autoPromotionEnabled: false      # ручное подтверждение перед switch
      previewReplicaCount: 3           # число реплик preview
      scaleDownDelaySeconds: 300       # 5 мин до удаления старых подов
      prePromotionAnalysis:            # анализ ДО переключения
        templates:
          - templateName: smoke-tests
        args:
          - name: service-url
            value: http://myapp-preview
      postPromotionAnalysis:           # анализ ПОСЛЕ переключения
        templates:
          - templateName: success-rate
---
apiVersion: v1
kind: Service
metadata:
  name: myapp-active
spec:
  selector:
    app: myapp  # Argo Rollouts автоматически управляет selector
  ports:
    - port: 80
      targetPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: myapp-preview
spec:
  selector:
    app: myapp
  ports:
    - port: 80
      targetPort: 8080
```

Workflow: обновление образа → Argo Rollouts создаёт preview-поды → prePromotionAnalysis (smoke tests на preview Service) → ручное `kubectl argo rollouts promote myapp` → трафик переключается → postPromotionAnalysis → старые поды удаляются через `scaleDownDelaySeconds`.

## Q33. (!) Как использовать Helm для управления деплоями?

**Helm** — это менеджер пакетов для Kubernetes, решающий три задачи разом. **Шаблонизация:** вместо копирования почти одинаковых YAML под dev/staging/prod вы держите один набор шаблонов и подставляете значения через `values.yaml`. **Версионирование релизов:** Helm помнит историю установок чарта, поэтому видно, что и когда выкатывалось. **Откат:** `helm rollback` возвращает релиз к предыдущей версии одной командой. Единицей управления выступает релиз — установленный в кластер экземпляр чарта.

**Структура Helm chart:**

```
charts/myapp/
├── Chart.yaml
├── values.yaml
├── values-prod.yaml
├── values-staging.yaml
└── templates/
    ├── deployment.yaml
    ├── service.yaml
    ├── ingress.yaml
    ├── configmap.yaml
    ├── hpa.yaml
    └── _helpers.tpl
```

```yaml
# Chart.yaml
apiVersion: v2
name: myapp
description: My Spring Boot application
version: 0.1.0
appVersion: "1.2.3"
```

```yaml
# values.yaml — значения по умолчанию
replicaCount: 2
image:
  repository: registry.example.com/myapp
  tag: "1.2.3"
  pullPolicy: IfNotPresent
service:
  type: ClusterIP
  port: 80
ingress:
  enabled: true
  host: myapp.example.com
resources:
  requests:
    cpu: 250m
    memory: 512Mi
  limits:
    cpu: 500m
    memory: 1Gi
readinessProbe:
  path: /actuator/health/readiness
  initialDelaySeconds: 15
livenessProbe:
  path: /actuator/health/liveness
  initialDelaySeconds: 30
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1
    maxUnavailable: 0
```

```yaml
# templates/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include "myapp.fullname" . }}
  labels:
    {{- include "myapp.labels" . | nindent 4 }}
spec:
  replicas: {{ .Values.replicaCount }}
  strategy:
    type: {{ .Values.strategy.type }}
    {{- if eq .Values.strategy.type "RollingUpdate" }}
    rollingUpdate:
      maxSurge: {{ .Values.strategy.rollingUpdate.maxSurge }}
      maxUnavailable: {{ .Values.strategy.rollingUpdate.maxUnavailable }}
    {{- end }}
  selector:
    matchLabels:
      {{- include "myapp.selectorLabels" . | nindent 6 }}
  template:
    metadata:
      labels:
        {{- include "myapp.selectorLabels" . | nindent 8 }}
    spec:
      containers:
        - name: {{ .Chart.Name }}
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: {{ .Values.readinessProbe.path }}
              port: 8080
            initialDelaySeconds: {{ .Values.readinessProbe.initialDelaySeconds }}
          resources:
            {{- toYaml .Values.resources | nindent 12 }}
```

**Команды деплоя и отката:**

```bash
# Установка / обновление (идемпотентно)
helm upgrade --install myapp ./charts/myapp \
  -f values-prod.yaml \
  --namespace production \
  --set image.tag=1.2.3 \
  --wait --timeout 5m

# Откат к предыдущему релизу
helm rollback myapp 0 --namespace production

# История релизов
helm history myapp --namespace production

# Dry-run перед деплоем
helm upgrade --install myapp ./charts/myapp \
  --dry-run --debug
```

## Q34. Как настроить деплой через GitHub Actions в Kubernetes?

Деплой в Kubernetes через **GitHub Actions** строят как многоэтапный workflow: один job собирает и пушит образ, отдельные jobs катят его в окружения по цепочке `build → deploy-staging → deploy-prod`. Ключевые приёмы здесь — переиспользование собранного образа между job-ами (через `outputs`), привязка job к `environment` для approval-гейта и smoke-тест с автоматическим `helm rollback` при провале. Пример workflow:

```yaml
# .github/workflows/deploy.yml
name: Build and Deploy

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

env:
  REGISTRY: registry.example.com
  IMAGE_NAME: myapp

jobs:
  build:
    runs-on: ubuntu-latest
    outputs:
      image-tag: ${{ steps.meta.outputs.version }}
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Build with Gradle
        run: ./gradlew build -x test

      - name: Run tests
        run: ./gradlew test

      - name: Docker meta
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=sha,prefix=
            type=semver,pattern={{version}}

      - name: Build and push Docker image
        uses: docker/build-push-action@v5
        with:
          context: .
          push: ${{ github.event_name == 'push' }}
          tags: ${{ steps.meta.outputs.tags }}

  deploy-staging:
    needs: build
    runs-on: ubuntu-latest
    environment: staging
    steps:
      - uses: actions/checkout@v4

      - name: Deploy to staging
        run: |
          helm upgrade --install myapp ./charts/myapp \
            -f values-staging.yaml \
            --set image.tag=${{ needs.build.outputs.image-tag }} \
            --namespace staging \
            --wait --timeout 5m

      - name: Smoke test
        run: |
          chmod +x ./scripts/smoke-test.sh
          ./scripts/smoke-test.sh https://myapp-staging.example.com

  deploy-prod:
    needs: [build, deploy-staging]
    runs-on: ubuntu-latest
    environment: production  # требует approval от reviewers
    steps:
      - uses: actions/checkout@v4

      - name: Deploy to production
        run: |
          helm upgrade --install myapp ./charts/myapp \
            -f values-prod.yaml \
            --set image.tag=${{ needs.build.outputs.image-tag }} \
            --namespace production \
            --wait --timeout 5m

      - name: Smoke test production
        run: ./scripts/smoke-test.sh https://myapp.example.com

      - name: Rollback on failure
        if: failure()
        run: helm rollback myapp 0 --namespace production
```

**Практика:** environment `production` с required reviewers обеспечивает approval gate; `--wait` ждёт готовности всех подов; при падении smoke test — автоматический `helm rollback`.

## Q35. Как настроить деплой через GitLab CI в Kubernetes?

В **GitLab CI** деплой описывают через `stages`, где каждое окружение — отдельная стадия, а один и тот же образ (тег по `CI_COMMIT_SHORT_SHA`) промотируется по цепочке. Характерные для GitLab элементы: `environment` связывает job с окружением и даёт URL прямо в UI, `when: manual` ставит ручной approval-гейт перед prod, а `after_script` с проверкой `CI_JOB_STATUS` выполняет автоматический rollback при ошибке. Пример pipeline:

```yaml
# .gitlab-ci.yml
stages:
  - build
  - test
  - docker
  - deploy-dev
  - deploy-staging
  - deploy-prod

variables:
  REGISTRY: registry.example.com
  IMAGE_NAME: myapp

build:
  stage: build
  image: gradle:8-jdk21
  script:
    - gradle build -x test
  artifacts:
    paths:
      - build/libs/*.jar

test:
  stage: test
  image: gradle:8-jdk21
  script:
    - gradle test
  artifacts:
    reports:
      junit: build/test-results/test/*.xml

docker:
  stage: docker
  image: docker:24
  services:
    - docker:24-dind
  script:
    - docker build -t $REGISTRY/$IMAGE_NAME:$CI_COMMIT_SHORT_SHA .
    - docker push $REGISTRY/$IMAGE_NAME:$CI_COMMIT_SHORT_SHA
  only:
    - main

deploy-dev:
  stage: deploy-dev
  image: alpine/helm:3.14
  script:
    - helm upgrade --install $IMAGE_NAME ./charts/myapp
        -f values-dev.yaml
        --set image.tag=$CI_COMMIT_SHORT_SHA
        --namespace dev
        --wait --timeout 5m
  environment:
    name: dev
    url: https://myapp-dev.example.com
  only:
    - main

deploy-staging:
  stage: deploy-staging
  image: alpine/helm:3.14
  script:
    - helm upgrade --install $IMAGE_NAME ./charts/myapp
        -f values-staging.yaml
        --set image.tag=$CI_COMMIT_SHORT_SHA
        --namespace staging
        --wait --timeout 5m
    - ./scripts/smoke-test.sh https://myapp-staging.example.com
  environment:
    name: staging
    url: https://myapp-staging.example.com
  only:
    - main

deploy-prod:
  stage: deploy-prod
  image: alpine/helm:3.14
  script:
    - helm upgrade --install $IMAGE_NAME ./charts/myapp
        -f values-prod.yaml
        --set image.tag=$CI_COMMIT_SHORT_SHA
        --namespace production
        --wait --timeout 5m
    - ./scripts/smoke-test.sh https://myapp.example.com
  environment:
    name: production
    url: https://myapp.example.com
  when: manual  # ручной approval
  only:
    - main
  after_script:
    - |
      if [ "$CI_JOB_STATUS" == "failed" ]; then
        helm rollback $IMAGE_NAME 0 --namespace production
      fi
```

**Практика:** один и тот же образ (тег по git SHA) промотируется по окружениям; `when: manual` для prod обеспечивает ручной approval; `after_script` с проверкой статуса позволяет автоматический rollback при ошибке.

## Q36. (!) Как реализовать feature flags с помощью Unleash или LaunchDarkly?

**Feature flags** (feature toggles) — переключатели функциональности, которыми управляют отдельно от деплоя: код можно вывести в прод неактивным, а включить и выключить фичу без rollback деплоя. Простой флаг можно сделать и через конфиг (`@ConditionalOnProperty`), но специализированные платформы вроде `Unleash` и `LaunchDarkly` дают то, чего конфиг не умеет: **изменение флага без рестарта, таргетинг по сегментам и процентам, аудит и единый UI управления**. Это превращает флаг из булевой настройки в инструмент постепенного rollout и аварийного отключения.

**Типы feature flags:**

| Тип | Назначение | Жизненный цикл |
|-----|-----------|----------------|
| `Release toggle` | Скрыть незавершённую фичу | Удалить после GA |
| `Ops toggle` | Аварийное отключение | Долгосрочный |
| `Experiment toggle` | A/B тест | На время эксперимента |
| `Permission toggle` | Доступ для отдельных пользователей | Зависит от бизнеса |

**Пример с Unleash (Spring Boot):**

```java
// build.gradle.kts
implementation("io.getunleash:unleash-client-java:9.+")

// UnleashConfig
@Configuration
public class UnleashConfig {

    @Bean
    public Unleash unleash(
            @Value("${unleash.url}") String url,
            @Value("${unleash.token}") String token,
            @Value("${spring.application.name}") String appName) {
        UnleashConfig config = UnleashConfig.newBuilder()
            .appName(appName)
            .instanceId(UUID.randomUUID().toString())
            .unleashAPI(url)
            .customHttpHeader("Authorization", token)
            .build();
        return new DefaultUnleash(config);
    }
}

// Использование в сервисе
@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final Unleash unleash;
    private final LegacyCheckout legacyCheckout;
    private final NewCheckout newCheckout;

    public CheckoutResult process(CheckoutRequest request, String userId) {
        // Контекст: можно включить флаг для конкретного userId
        UnleashContext ctx = UnleashContext.builder()
            .userId(userId)
            .build();

        if (unleash.isEnabled("new-checkout", ctx)) {
            return newCheckout.process(request);
        }
        return legacyCheckout.process(request);
    }
}
```

**Стратегии Unleash для постепенного rollout:**

Постепенный rollout по стратегии Unleash:

- Весь трафик (100%) проходит через **стратегию Unleash**.
- Стратегия `gradualRollout` направляет 10% на **новую фичу**, остальные 90% — на **старую логику**.
- Новая фича подключена к **мониторингу ошибок и метрик**.
- По результатам мониторинга: при **OK** — увеличить процент (возврат к стратегии Unleash с большей долей); при **проблемах** — сбросить долю до 0% (весь трафик на старую логику).

**Пример с `@ConditionalOnProperty` (простой вариант без Unleash):**

```yaml
# application-prod.yml
feature:
  new-checkout: false  # Включить после валидации на staging
  new-payment: true
```

```java
@RestController
@ConditionalOnProperty(name = "feature.new-checkout", havingValue = "true")
public class NewCheckoutController { ... }

@RestController
@ConditionalOnProperty(name = "feature.new-checkout",
    havingValue = "false", matchIfMissing = true)
public class LegacyCheckoutController { ... }
```

**Правила работы с feature flags:**
- Флаги — временные; создавать тикет на удаление сразу
- Тестировать оба пути (flag on / flag off)
- Не вкладывать флаги друг в друга — exponential complexity
- Документировать: имя, цель, ответственный, дата удаления

## Q37. Как работает GitOps-деплой через Argo CD на практике?

**GitOps** на практике — это подход, где Git служит единственным источником истины о желаемом состоянии инфраструктуры, а Argo CD непрерывно следит за репозиторием и приводит кластер к описанному в нём состоянию. Принципиальный момент реализации — **разделение двух репозиториев**: app-репо с кодом, где работает CI (сборка, тесты, push образа), и config-репо с манифестами, за которым следит Argo CD. CI не катит в кластер напрямую — он лишь делает коммит с новым тегом образа в config-репо, а дальше выкатку выполняет Argo CD. Так доставка остаётся декларативной и проходит через PR-ревью.

**Архитектура GitOps-деплоя:**

Порядок шагов (участники: `Developer`, App Repo с CI, Config Repo, `Argo CD`, `Kubernetes`):

1. `Developer` делает `git push feature` в App Repo.
2. App Repo (CI): build + test.
3. App Repo (CI): docker build + push.
4. App Repo создаёт PR в Config Repo — обновить `image.tag` в `values.yaml`.
5. В Config Repo: Review + Merge.
6. `Argo CD` опрашивает Config Repo — Poll (30s) или Webhook.
7. `Argo CD` применяет изменения в `Kubernetes` (sync, Apply).
8. `Kubernetes` возвращает статус ресурсов в `Argo CD`.
9. `Argo CD` шлёт `Developer` уведомление о статусе.

**Структура config-репозитория:**

```
gitops-config/
├── apps/
│   ├── myapp/
│   │   ├── base/
│   │   │   ├── deployment.yaml
│   │   │   └── service.yaml
│   │   └── overlays/
│   │       ├── dev/
│   │       │   └── kustomization.yaml  # image.tag: dev-abc123
│   │       ├── staging/
│   │       │   └── kustomization.yaml  # image.tag: 1.5.0-rc1
│   │       └── prod/
│   │           └── kustomization.yaml  # image.tag: 1.4.9
```

**Пример Application в Argo CD:**

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: myapp-prod
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/org/gitops-config
    targetRevision: main
    path: apps/myapp/overlays/prod
  destination:
    server: https://kubernetes.default.svc
    namespace: production
  syncPolicy:
    automated:          # Автосинхронизация
      prune: true       # Удалять ресурсы без манифеста
      selfHeal: true    # Восстанавливать drift
    syncOptions:
      - CreateNamespace=true
```

**CI обновляет image tag в config-репо:**

```bash
# В CI pipeline (после docker push)
NEW_TAG="${CI_COMMIT_SHORT_SHA}"

# Обновить kustomization.yaml в config-репо
git clone https://github.com/org/gitops-config
cd gitops-config
kustomize edit set image myapp=registry.example.com/myapp:${NEW_TAG} \
  --kustomization apps/myapp/overlays/staging/kustomization.yaml
git add -A
git commit -m "ci: update myapp staging to ${NEW_TAG}"
git push
```

**Преимущества GitOps:**
- Полная история изменений состояния кластера в Git
- Rollback = `git revert` в config-репо
- Drift detection: Argo CD видит расхождение cluster vs Git
- Ревью изменений через PR в config-репо

## Q38. Как настроить деплой через Jenkins Pipeline (Declarative)?

**Jenkins Declarative Pipeline** описывает деплой как набор `stages` с предсказуемой структурой и встроенной обработкой результата. Для деплоя Spring Boot в Kubernetes здесь важны несколько элементов: `input` со `submitter` — ручной approval-гейт, который вправе нажать только указанные люди; блок `post { failure { ... } }` — автоматический `helm rollback` при сбое деплоя; `withKubeConfig` — подстановка нужного kubeconfig на каждое окружение. Пример:

```groovy
pipeline {
    agent any

    environment {
        REGISTRY = 'registry.example.com'
        IMAGE_NAME = 'myapp'
        KUBE_CONFIG = credentials('kubeconfig-prod')
    }

    stages {
        stage('Build & Test') {
            steps {
                sh './gradlew clean build'
            }
            post {
                always {
                    junit 'build/test-results/**/*.xml'
                    jacoco(
                        execPattern: 'build/jacoco/*.exec',
                        minimumLineCoverage: '80'
                    )
                }
            }
        }

        stage('Docker Build & Push') {
            when { branch 'main' }
            steps {
                script {
                    def tag = "${env.GIT_COMMIT[0..7]}"
                    docker.withRegistry("https://${REGISTRY}", 'registry-creds') {
                        def img = docker.build("${REGISTRY}/${IMAGE_NAME}:${tag}")
                        img.push()
                        img.push('latest')
                    }
                    env.IMAGE_TAG = tag
                }
            }
        }

        stage('Deploy to Staging') {
            when { branch 'main' }
            steps {
                withKubeConfig([credentialsId: 'kubeconfig-staging']) {
                    sh """
                        helm upgrade --install ${IMAGE_NAME} ./charts/${IMAGE_NAME} \\
                          --set image.tag=${IMAGE_TAG} \\
                          --namespace staging --wait --timeout 5m
                    """
                }
            }
        }

        stage('Smoke Test') {
            when { branch 'main' }
            steps {
                sh './scripts/smoke-test.sh https://myapp-staging.example.com'
            }
        }

        stage('Deploy to Production') {
            when { branch 'main' }
            input {
                message 'Деплой в production?'
                ok 'Да, деплоить'
                submitter 'admin,teamlead'
            }
            steps {
                withKubeConfig([credentialsId: 'kubeconfig-prod']) {
                    sh """
                        helm upgrade --install ${IMAGE_NAME} ./charts/${IMAGE_NAME} \\
                          --set image.tag=${IMAGE_TAG} \\
                          --namespace production --wait --timeout 10m
                    """
                }
            }
            post {
                failure {
                    withKubeConfig([credentialsId: 'kubeconfig-prod']) {
                        sh "helm rollback ${IMAGE_NAME} 0 --namespace production"
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            slackSend(color: 'good',
                message: "Деплой ${IMAGE_NAME}:${IMAGE_TAG} в prod — SUCCESS")
        }
        failure {
            slackSend(color: 'danger',
                message: "Деплой ${IMAGE_NAME}:${IMAGE_TAG} — FAILED")
        }
    }
}
```

**Ключевые практики:**
- `input` с `submitter` — только уполномоченные могут нажать деплой
- `post { failure { } }` — автоматический rollback при ошибке
- Тег по git SHA — трассируемость артефакта
- `cleanWs()` — чистить workspace после каждого запуска

## Q39. Как реализовать DORA-метрики для оценки процесса деплоя?

**DORA metrics** (DevOps Research and Assessment) — четыре метрики, которыми измеряют зрелость процесса доставки. Их сила в балансе: две измеряют **скорость** (как часто и как быстро вы доставляете), две — **стабильность** (как часто ломаете и как быстро чините). Их смотрят вместе, потому что скорость без стабильности — это «быстро ломаем», а стабильность без скорости — «не ломаем, потому что почти не катим». Хорошие команды улучшают и то, и другое одновременно.

| Метрика | Elite | High | Medium | Low |
|---------|-------|------|--------|-----|
| **Deployment Frequency** | On demand (многократно в день) | 1 раз в день – неделю | 1 раз в неделю – месяц | Реже месяца |
| **Lead Time for Changes** | < 1 часа | 1 день – неделя | 1 неделя – месяц | > 6 месяцев |
| **Change Failure Rate** | 0–5% | 0–15% | — | 16–30% |
| **Time to Restore Service** | < 1 часа | < 1 дня | < 1 недели | > 1 недели |

**Как собирать метрики:**

Где на пути изменения собираются метрики:

- `git commit` — **начало Lead Time**.
- → PR/MR создан → CI pipeline.
- → **деплой в prod** — **конец Lead Time**.
- → мониторинг — здесь считается **Change Failure Rate**.
- При **инциденте** → отсчитывается **Time to Restore**.
- После того как сервис **восстановлен** → фиксируется **DORA snapshot**.

**Примеры инструментов сбора:**

```yaml
# GitHub Actions: автоматически собирать deployment frequency
# через Deployment events API
- name: Create GitHub Deployment
  uses: actions/github-script@v7
  with:
    script: |
      await github.rest.repos.createDeployment({
        owner: context.repo.owner,
        repo: context.repo.repo,
        ref: context.sha,
        environment: 'production',
        auto_merge: false,
        required_contexts: []
      });
```

```sql
-- Пример запроса для Deployment Frequency за последние 30 дней
-- (если деплои логируются в БД)
SELECT
  date_trunc('day', deployed_at) AS deploy_day,
  COUNT(*) AS deployments_count
FROM deployments
WHERE environment = 'production'
  AND deployed_at >= NOW() - INTERVAL '30 days'
GROUP BY 1
ORDER BY 1;
```

**Связь DORA с практиками:**

| DORA метрика | Что улучшает метрику |
|---|---|
| Deployment Frequency | Малые PR, trunk-based development, feature flags |
| Lead Time | Автоматизация pipeline, параллельные этапы |
| Change Failure Rate | Качество тестов, canary/blue-green деплой |
| Time to Restore | Мониторинг, rollback автоматизация, on-call процесс |

**Практика:** использовать инструменты вроде `Four Keys` (Google), `Faros CE`, `LinearB`, `Cortex` для автоматического сбора. Не использовать метрики как KPI для отдельных людей — только для процесса в целом.

---

## See also

- [Проектирование CI/CD пайплайнов](pipeline-design-interview.md) — этапы, инструменты, fail fast
- [Kubernetes](../devops/kubernetes-interview.md) — Deployment, rollout, HPA и ArgoCD
- [Docker](../devops/docker-interview.md) — образы, multi-stage build, registry
- [Observability](../monitoring/observability-interview.md) — мониторинг canary и метрики деплоя
- [Метрики и трассировка](../monitoring/metrics-tracing-interview.md) — health checks и алерты при деплое
- [Git](../devops/git-interview.md) — trunk-based development и feature flags
- [Test Automation](../testing/test-automation-interview.md) — smoke-тесты и acceptance-тесты после деплоя
- [AI Agents](../ai-ml/ai-agents-interview.md)
- [Embeddings](../ai-ml/embeddings-interview.md)
- [LLM Basics](../ai-ml/llm-basics-interview.md)
- [LLM Integration Patterns](../ai-ml/llm-integration-patterns-interview.md)
- [MLOps](../ai-ml/mlops-interview.md)
