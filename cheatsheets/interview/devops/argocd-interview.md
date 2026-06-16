---
title: "Вопросы на собеседовании: ArgoCD и GitOps"
description: "Вопросы и ответы по ArgoCD и GitOps: архитектура, Application CRD, sync стратегии, ApplicationSet, App of Apps, Sync Waves, Hooks, RBAC, multi-cluster, secrets management, Image Updater, сравнение с Flux."
tags:
  - interview
  - devops
  - argocd-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "ArgoCD и GitOps"
  - "ArgoCD interview"
  - "ArgoCD собеседование"
prerequisites:
  - "[[argocd]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `ArgoCD` и `GitOps`

Вопросы и ответы по `ArgoCD` и `GitOps`: принципы GitOps, архитектура `ArgoCD`, `Application` CRD, стратегии синхронизации, `ApplicationSet`, `App of Apps` паттерн, `Sync Waves`, хуки, `RBAC`, multi-cluster деплой, управление секретами, `ArgoCD Image Updater`, сравнение с `Flux`.

**`ArgoCD`** — декларативный GitOps-контроллер для `Kubernetes`, реализующий непрерывную доставку на основе `Git` как единственного источника истины. Является частью экосистемы `Argo Project` (наряду с `Argo Workflows`, `Argo Events`, `Argo Rollouts`) и входит в состав CNCF. `ArgoCD` следит за состоянием кластера и автоматически синхронизирует его с желаемым состоянием, описанным в репозитории.

## Q1. Что такое GitOps? Назовите его ключевые принципы.

**GitOps** — практика, в которой `Git`-репозиторий становится единственным источником истины (`Single Source of Truth`) для всей конфигурации системы: что развёрнуто в кластере, определяет не человек с `kubectl`, а содержимое репозитория. Изменить состояние можно только одним способом — коммитом в Git.

Четыре принципа GitOps (по спецификации OpenGitOps) описывают замкнутый цикл от описания до фактического состояния:

1. **Декларативность** — описывается *что* должно быть (желаемое состояние), а не *как* этого добиться (последовательность команд). Форматы: YAML-манифесты, Helm-чарты, Kustomize.
2. **Версионирование и неизменяемость** — конфигурация лежит в Git, поэтому каждый коммит — неизменяемый снимок состояния с полной историей. Откатиться можно к любой точке.
3. **Автоматический pull** — агент *внутри* кластера сам вытягивает (`pull`) желаемое состояние и применяет его. Никто не пушит изменения в кластер снаружи.
4. **Непрерывная сверка (reconcile)** — агент в цикле сравнивает реальное состояние с желаемым и устраняет любые расхождения, даже если их внесли вручную.

**Что это даёт на практике:**
- Аудит изменений — это `git log`: кто, когда и что задеплоил, видно из истории коммитов.
- Откат — это `git revert`: возврат к предыдущему состоянию через обычный Git-механизм.
- Чёткое разделение CI (сборка) и CD (доставка): они работают с разными репозиториями и правами.
- Меньше attack surface: `kubeconfig` с правами на кластер не нужен вне кластера, секрет не «утекает» через CI-сервер.

## Q2. Чем GitOps отличается от традиционного CI/CD подхода (push-based)?

Главное различие — **кто инициирует деплой и в какую сторону движутся изменения**. В push-модели CI-сервер активно «толкает» изменения в кластер; в GitOps кластер сам «тянет» их из Git.

| Критерий | Push-based (традиционный) | GitOps (pull-based) |
|---|---|---|
| Инициатор деплоя | CI/CD пайплайн | Агент внутри кластера |
| Доступ к кластеру | CI/CD сервер имеет `kubeconfig` | Только агент внутри кластера |
| Источник истины | Пайплайн + скрипты | `Git`-репозиторий |
| Drift detection | Отсутствует | Встроен |
| Rollback | Повторный запуск пайплайна | `git revert` + автосинхронизация |
| Аудит | Логи CI | `git log` |

В push-подходе CI-сервер (Jenkins, GitHub Actions) держит у себя `kubeconfig` и напрямую вызывает `kubectl apply` или `helm upgrade`. Это значит: учётка с полными правами на кластер лежит во внешней системе, а реальное состояние кластера нигде не отслеживается — если кто-то поправил ресурс руками, никто об этом не узнает (drift не виден).

В GitOps агент (`ArgoCD`, `Flux`) живёт внутри кластера, сам следит за репозиторием и непрерывно сводит кластер к описанному состоянию. Поэтому drift обнаруживается автоматически, а внешний доступ к кластеру не требуется вовсе.

## Q3. Опишите архитектуру ArgoCD. Из каких компонентов она состоит? (!)

`ArgoCD` — это набор кооперирующихся микросервисов, а не один процесс. Ключевая тройка — **Application Controller** (мозг, делает reconcile), **Repo Server** (рендерит манифесты из Git) и **API Server** (фасад для UI/CLI); остальные компоненты их обслуживают.

- **API Server** (`argocd-server`) — gRPC/REST-фасад для UI, CLI и внешних систем. Отвечает за аутентификацию и авторизацию, проксирует запросы к остальным компонентам. Сам ничего не деплоит — это входная точка.
- **Application Controller** (`argocd-application-controller`) — сердце системы, Kubernetes-контроллер с control loop. Для каждого `Application` сравнивает desired state (из Git) с live state (из кластера), вычисляет статус `OutOfSync` и выполняет синхронизацию.
- **Repo Server** (`argocd-repo-server`) — работает с Git-репозиториями: клонирует их и рендерит итоговые манифесты из любого формата (Helm, Kustomize, Jsonnet, plain YAML), кешируя результат. Именно он превращает чарт/оверлей в плоский YAML.
- **Redis** — кеш состояния приложений, git-объектов и результатов рендеринга. Чисто для производительности: снимает повторную нагрузку с Repo Server и контроллера. Это кеш, а не источник истины — при потере данные восстанавливаются.
- **ApplicationSet Controller** — генерирует множество `Application` по одному шаблону (см. Q12). Отдельный компонент, отвечающий за `ApplicationSet` CRD.
- **Dex** (опционально) — встроенный OpenID Connect-провайдер для SSO (LDAP, GitHub, Google, SAML). Нужен, если у вас нет собственного OIDC-провайдера.
- **Notifications Controller** — рассылает уведомления о событиях (sync, health) в Slack, Teams, Email, PagerDuty и т.д.

## Q4. Что такое Application CRD в ArgoCD? Опишите его ключевые поля.

`Application` — основной Custom Resource в `ArgoCD`. Он отвечает на три вопроса: **что** деплоить (`source`), **куда** (`destination`) и **как** синхронизировать (`syncPolicy`). По сути это «единица развёртывания» — связь между точкой в Git и местом в кластере.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: my-app
  namespace: argocd
spec:
  project: default                    # AppProject для RBAC
  source:
    repoURL: https://github.com/org/repo
    targetRevision: HEAD              # ветка, тег, или SHA
    path: k8s/overlays/prod           # путь к манифестам
    # Для Helm:
    # chart: my-chart
    # helm:
    #   valueFiles: [values-prod.yaml]
  destination:
    server: https://kubernetes.default.svc  # целевой кластер
    namespace: production
  syncPolicy:
    automated:
      prune: true       # удалять ресурсы, которых нет в Git
      selfHeal: true    # исправлять ручные изменения в кластере
    syncOptions:
      - CreateNamespace=true
      - PrunePropagationPolicy=foreground
  ignoreDifferences:
    - group: apps
      kind: Deployment
      jsonPointers: ["/spec/replicas"]  # игнорировать autoscaler
```

Ключевые секции:
- `source` — откуда брать манифесты: Git-репозиторий (`repoURL` + `path` + `targetRevision`) либо Helm-чарт с values.
- `destination` — в какой кластер (`server`) и namespace деплоить.
- `syncPolicy` — ручная или автоматическая синхронизация; здесь же `prune` и `selfHeal` (см. Q6–Q7).
- `ignoreDifferences` — поля, расхождения в которых `ArgoCD` не считает за drift (см. Q23). В примере выше — `spec.replicas`, чтобы не воевать с autoscaler.

## Q5. Какие статусы синхронизации и здоровья имеет Application в ArgoCD? (!)

У `Application` три независимых измерения статуса, и их важно не путать: **Sync** отвечает «совпадает ли кластер с Git», **Health** — «всё ли работает», **Operation** — «чем закончилась последняя операция sync». Приложение может быть `Synced`, но `Degraded` (задеплоили ровно то, что в Git, но под падает в CrashLoop).

**Sync Status** — совпадает ли live state (кластер) с desired state (Git):
- `Synced` — кластер соответствует Git.
- `OutOfSync` — есть расхождения (изменили Git или поправили кластер руками).
- `Unknown` — не удалось получить состояние для сравнения.

**Health Status** — в каком состоянии сами ресурсы в кластере:
- `Healthy` — всё работает нормально.
- `Progressing` — идёт изменение (например, rolling update `Deployment`).
- `Degraded` — ресурс в плохом состоянии (под в `CrashLoopBackOff`).
- `Suspended` — ресурс приостановлен (suspended `CronJob`).
- `Missing` — ресурс описан, но отсутствует в кластере.
- `Unknown` — здоровье определить не удалось.

**Operation State** — итог последней операции синхронизации:
- `Running` — синхронизация выполняется.
- `Succeeded` — завершилась успешно.
- `Failed` — завершилась с ошибкой применения.
- `Error` — внутренняя ошибка `ArgoCD` (не самой синхронизации).

## Q6. Что такое Sync стратегия в ArgoCD? Чем отличается manual от automated?

Sync-стратегия определяет, **кто нажимает на «применить»** при расхождении кластера с Git. В обоих режимах `ArgoCD` одинаково *обнаруживает* drift — разница только в том, применяется ли он сам.

**Manual sync** (по умолчанию): `ArgoCD` видит расхождение (`OutOfSync`), но изменения НЕ применяет — ждёт человека. Синхронизацию запускает пользователь через UI, CLI или API. Подходит для prod, где нужен явный контроль над моментом выката.

**Automated sync**: `ArgoCD` сам применяет изменения, как только обнаружит расхождение. Удобно для dev/staging и для полностью автоматизированного GitOps-конвейера.

```yaml
syncPolicy:
  automated:
    prune: false    # НЕ удалять ресурсы, которых нет в Git (осторожно!)
    selfHeal: false # НЕ исправлять ручные изменения
```

Два важных параметра автоматической синхронизации (по умолчанию оба `false` — это безопасно):
- **`prune: true`** — удалять из кластера ресурсы, которых уже нет в Git. Без него удаление из Git не доходит до кластера, и приложение «зависает» в `OutOfSync`. Включают осознанно, потому что опечатка в `path` может снести лишнее (см. Q7).
- **`selfHeal: true`** — при ручном изменении в кластере (`kubectl edit`) откатывать его до состояния в Git. Без `selfHeal` ручная правка живёт до следующего коммита и сама не исправляется — `ArgoCD` лишь покажет `OutOfSync`.

## Q7. Что такое prune и selfHeal в ArgoCD? В чём риски включения prune?

Оба флага делают синхронизацию «жёстче»: `prune` приводит кластер к Git **в плане удаления**, `selfHeal` — **в плане ручных правок**. Оба удобны, но у каждого есть характерный способ выстрелить себе в ногу.

**`prune`** разрешает `ArgoCD` удалять из кластера ресурсы, исчезнувшие из Git. Без него удалённый из Git ресурс остаётся в кластере, а `Application` висит в `OutOfSync`.

Чем опасен `prune: true`:
- **Случайное массовое удаление** при опечатке в `path` или `targetRevision`: если `ArgoCD` отрендерил «пустой» набор манифестов, он решит, что всё надо снести.
- **Удаление ресурсов, созданных в обход Git** — отладочных объектов, ручных миграций.
- **Сбои при переименовании**: переименование = удаление старого ресурса + создание нового, что может вызвать кратковременный простой.

**`selfHeal`** разрешает `ArgoCD` автоматически откатывать любые изменения, внесённые в кластер в обход Git. Гарантирует, что в prod живёт ровно то, что описано в репозитории.

Чем опасен `selfHeal: true`:
- **Откатывает экстренные hotfix-ы**, наспех применённые через `kubectl` во время инцидента.
- **Конфликтует с HPA** (Horizontal Pod Autoscaler): HPA меняет `spec.replicas`, `selfHeal` тут же возвращает значение из Git — и они начинают бесконечно бороться.

**Решение для HPA:** добавить `spec.replicas` в `ignoreDifferences`, чтобы `ArgoCD` не считал это поле за drift (см. Q23).

## Q8. Что такое Sync Waves в ArgoCD? Как они работают? (!)

**Sync Waves** задают **порядок** применения ресурсов внутри одной синхронизации. Это нужно, потому что у ресурсов есть зависимости: бессмысленно поднимать `Deployment`, пока не накатилась миграция БД, а миграция не запустится без `ConfigMap` с настройками подключения.

**Как работает:** каждому ресурсу через аннотацию назначается числовой вес (wave). `ArgoCD` применяет ресурсы по возрастанию wave и **не переходит к следующей волне, пока все ресурсы текущей не станут `Healthy`**. Так получается детерминированный порядок: сначала инфраструктура, потом данные, потом приложение.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
  annotations:
    argocd.argoproj.io/sync-wave: "2"   # применить второй
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: config
  annotations:
    argocd.argoproj.io/sync-wave: "0"   # применить первым
---
apiVersion: batch/v1
kind: Job
metadata:
  name: db-migration
  annotations:
    argocd.argoproj.io/sync-wave: "1"   # применить после ConfigMap
```

Типичный порядок волн отражает зависимости «снизу вверх»:
- Wave -1: `Namespace`, `CRD`, `PersistentVolumeClaim` — то, на что опирается всё остальное.
- Wave 0: `ConfigMap`, `Secret` (значение по умолчанию для ресурсов без аннотации) — конфигурация для приложений.
- Wave 1: `Job` для миграций БД — схема должна быть готова до старта приложения.
- Wave 2: основные `Deployment`, `StatefulSet` — само приложение.
- Wave 3: `Ingress`, `HorizontalPodAutoscaler` — то, что навешивается поверх работающих подов.

## Q9. Что такое Sync Hooks в ArgoCD? Опишите фазы.

**Sync Hooks** — это ресурсы (обычно `Job` или `Workflow`), которые `ArgoCD` запускает в определённый момент синхронизации, чтобы выполнить вспомогательную работу: миграцию, smoke-тест, оповещение. В отличие от Sync Waves (порядок *обычных* ресурсов), хуки — это *дополнительные* действия, привязанные к фазе. Задаются аннотацией `argocd.argoproj.io/hook`.

Фазы хуков (в порядке выполнения):
- **`PreSync`** — *перед* применением манифестов. Классика — миграции БД и проверки предусловий: если хук упал, основной деплой не пойдёт.
- **`Sync`** — *вместе* с основными ресурсами (используется редко).
- **`PostSync`** — *после* успешной синхронизации. Smoke-тесты, прогрев кеша, уведомление об успехе.
- **`SyncFail`** — при *неудачной* синхронизации. Оповещения, логика отката.
- **`Skip`** — помечает ресурс как пропускаемый при синхронизации.

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: db-migration
  annotations:
    argocd.argoproj.io/hook: PreSync
    argocd.argoproj.io/hook-delete-policy: HookSucceeded
spec:
  template:
    spec:
      containers:
        - name: migrate
          image: myapp:latest
          command: ["./migrate.sh"]
      restartPolicy: Never
```

**Hook Delete Policy** (`hook-delete-policy`) определяет, когда удалять `Job`-хука, чтобы он не копился в кластере:
- `HookSucceeded` — удалять при успехе (логи неуспешного остаются для разбора).
- `HookFailed` — удалять при неудаче.
- `BeforeHookCreation` — удалять старый хук прямо перед созданием нового (по умолчанию): объект всегда один и свежий.

## Q10. Как ArgoCD определяет источник манифестов? Какие инструменты поддерживаются?

`ArgoCD` сам по себе не «умеет» Helm или Kustomize — он делегирует рендеринг соответствующему инструменту в Repo Server, а на выходе получает плоский YAML, который и применяет. Поддерживаемые инструменты рендеринга:

- **Helm** — чарты из Git или Helm registry; поддерживает values-файлы и override параметров.
- **Kustomize** — `kustomization.yaml` с overlays; умеет переопределять теги образов через `images`.
- **Jsonnet** — `*.jsonnet`/`*.libsonnet` файлы.
- **Plain YAML/JSON** — готовые Kubernetes-манифесты без шаблонизации.
- **Config Management Plugins (CMP)** — кастомные плагины для всего, чего нет из коробки (Vault agent, cdk8s, Helm с доп. инструментами).

**Как выбирается инструмент:** по умолчанию `ArgoCD` определяет тип автоматически — по характерным файлам в `path`:
- `Chart.yaml` → Helm
- `kustomization.yaml` → Kustomize
- `*.jsonnet` → Jsonnet
- ничего из перечисленного → plain YAML

Тип можно задать и явно в `spec.source` (например, блок `helm:` или `kustomize:`), если автоопределение не подходит.

## Q11. Что такое ApplicationSet? Зачем он нужен?

**`ApplicationSet`** — контроллер и CRD, который генерирует множество `Application` из одного шаблона. Решает проблему дублирования: без него для 20 кластеров пришлось бы вручную поддерживать 20 почти одинаковых `Application`-манифестов, отличающихся одним-двумя полями. `ApplicationSet` описывает шаблон один раз, а конкретные значения подставляет **генератор** (список, кластеры, директории Git — см. Q12).

Главные сценарии: multi-cluster (одно приложение на каждый кластер), multi-env (dev/staging/prod) и multi-tenant.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: my-apps
  namespace: argocd
spec:
  generators:
    - list:
        elements:
          - cluster: dev
            url: https://dev-cluster.example.com
          - cluster: prod
            url: https://prod-cluster.example.com
  template:
    metadata:
      name: "{{cluster}}-my-app"
    spec:
      project: default
      source:
        repoURL: https://github.com/org/repo
        targetRevision: HEAD
        path: "overlays/{{cluster}}"
      destination:
        server: "{{url}}"
        namespace: my-app
```

**Что это даёт:** единая точка управления, отсутствие копипасты и — ключевой момент — приложения появляются и исчезают *автоматически*. Зарегистрировали новый кластер или добавили директорию в Git — соответствующий `Application` создаётся сам, без правки манифестов.

## Q12. Какие generators поддерживает ApplicationSet? (!)

Генератор — это «источник параметров» для шаблона: он выдаёт набор словарей, и для каждого создаётся отдельный `Application`. Выбор генератора зависит от того, *откуда* берётся список целей.

**List generator** — статический список элементов с произвольными параметрами. Самый простой: список целей задан прямо в манифесте.
```yaml
generators:
  - list:
      elements:
        - env: dev
          replica_count: "1"
        - env: prod
          replica_count: "3"
```

**Cluster generator** — по одному `Application` на каждый зарегистрированный в `ArgoCD` кластер (с фильтром по label). Список целей берётся динамически из cluster-секретов — добавили кластер, приложение развернулось само.
```yaml
generators:
  - clusters:
      selector:
        matchLabels:
          environment: production
```

**Git generator** — обходит директории или файлы в Git-репозитории и создаёт `Application` на каждую найденную (например, на каждую папку под `apps/`). Источник целей — структура самого репозитория.
```yaml
generators:
  - git:
      repoURL: https://github.com/org/repo
      revision: HEAD
      directories:
        - path: apps/*   # одно Application на каждую директорию
```

**Matrix generator** — декартово произведение двух генераторов. Пример ниже разворачивает «каждое приложение на каждом кластере» (все кластеры × все директории).
```yaml
generators:
  - matrix:
      generators:
        - clusters: {}       # все кластеры
        - git:
            directories: [{path: "apps/*"}]  # все директории
```

**Merge generator** — объединяет параметры нескольких генераторов по общему ключу (в отличие от matrix — не произведение, а «склейка» по совпадающему полю; полезно, чтобы переопределить часть параметров).

**SCM Provider generator** — обходит все репозитории в организации GitHub/GitLab (для платформ, где приложения = репозитории).

**Pull Request generator** — создаёт временное (ephemeral) окружение на каждый открытый PR и удаляет его при закрытии. Так получаются preview-стенды на каждую ветку.

## Q13. Что такое паттерн App of Apps в ArgoCD?

**App of Apps** — паттерн, в котором один корневой `Application` («root app») деплоит не Kubernetes-ресурсы, а другие `Application`-объекты. Идея простая: вместо того чтобы вручную создавать десяток приложений, вы создаёте один root app, который указывает на директорию с их манифестами — и `ArgoCD` поднимает всю платформу рекурсивно, одним sync.

```
repo/
  apps/
    app-of-apps.yaml      # root Application
    applications/
      backend.yaml        # Application для backend
      frontend.yaml       # Application для frontend
      database.yaml       # Application для БД
```

Root app (app-of-apps.yaml):
```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: app-of-apps
spec:
  source:
    path: applications/   # директория с другими Application
  destination:
    server: https://kubernetes.default.svc
    namespace: argocd     # Applications создаются в namespace argocd
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
```

**Когда применять:** паттерн хорош, когда набор приложений *известен и описан явно* в Git, а компонентам нужны разные sync-стратегии. Минус — каждое приложение всё равно требует своего `Application`-манифеста.

**App of Apps vs ApplicationSet:** в современных проектах для однотипных приложений чаще выбирают `ApplicationSet` — он генерирует `Application` из шаблона и не требует ручного дублирования манифестов. App of Apps остаётся удобным для разнородного набора приложений, которые проще описать по отдельности.

## Q14. Как работает health check в ArgoCD? Как написать кастомный Lua скрипт?

Health check отвечает на вопрос «ресурс реально работает или нет» — отдельно от sync-статуса (см. Q5). Для стандартных ресурсов (`Deployment`, `StatefulSet`, `DaemonSet`, `Service`, `Ingress`) `ArgoCD` знает логику здоровья из коробки: например, `Deployment` считается `Healthy`, когда все реплики доступны.

Проблема возникает с кастомными CRD: `ArgoCD` не знает, что значит «здоровый» `Certificate` или `Rollout`. Для этого пишут **Lua-скрипт** прямо в `ConfigMap` `ArgoCD` — он разбирает `status` ресурса и возвращает вердикт. Пример для `cert-manager` `Certificate`: смотрим condition `Ready` и мапим его в health-статус.

```yaml
# argocd-cm ConfigMap
data:
  resource.customizations.health.certmanager.io_Certificate: |
    hs = {}
    if obj.status ~= nil then
      if obj.status.conditions ~= nil then
        for i, condition in ipairs(obj.status.conditions) do
          if condition.type == "Ready" then
            if condition.status == "True" then
              hs.status = "Healthy"
              hs.message = "Certificate is ready"
              return hs
            end
            if condition.status == "False" then
              hs.status = "Degraded"
              hs.message = condition.message
              return hs
            end
          end
        end
      end
    end
    hs.status = "Progressing"
    hs.message = "Waiting for certificate"
    return hs
```

Скрипт должен вернуть `hs.status` — один из: `Healthy`, `Progressing`, `Degraded`, `Suspended`, `Missing` (те же значения, что и health-статус из Q5). Поле `hs.message` отображается в UI и помогает понять, *почему* ресурс в таком состоянии.

## Q15. Что такое RBAC в ArgoCD? Как настроить права доступа? (!)

Авторизация в `ArgoCD` двухуровневая, и эти уровни отвечают на разные вопросы. **AppProject** ограничивает, *куда* приложения вообще могут деплоить (репозитории, кластеры, namespace). **RBAC-политики** определяют, *что* пользователь может делать с приложениями (sync, get, delete). Первое — про границы для самих `Application`, второе — про права людей.

**AppProject** — «песочница» для группы приложений: задаёт разрешённые источники и назначения. Даже если кто-то создаст `Application` с чужим репозиторием или кластером, проект его не пропустит.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: AppProject
metadata:
  name: team-payments
  namespace: argocd
spec:
  description: "Payment team project"
  sourceRepos:
    - https://github.com/org/payments-*   # разрешённые репозитории
  destinations:
    - namespace: payments-*               # разрешённые namespace
      server: https://kubernetes.default.svc
  clusterResourceWhitelist:
    - group: ""
      kind: Namespace                     # разрешённые cluster-level ресурсы
  namespaceResourceBlacklist:
    - group: ""
      kind: ResourceQuota                 # запрещённые namespace-level ресурсы
  roles:
    - name: developer
      description: Developer role
      policies:
        - p, proj:team-payments:developer, applications, sync, team-payments/*, allow
        - p, proj:team-payments:developer, applications, get, team-payments/*, allow
      groups:
        - org:payments-team               # GitHub/LDAP группа
```

**RBAC-политики** в `argocd-rbac-cm` ConfigMap описываются в формате Casbin. Строки `p` задают разрешения (permission), строки `g` связывают группу с ролью (grant). В примере ниже разработчик может синхронизировать только `dev-*`, а sync `prod-*` явно запрещён (`deny` приоритетнее `allow`):
```
# Формат: p, <role/user>, <resource>, <action>, <object>, <effect>
p, role:developer, applications, get, */*, allow
p, role:developer, applications, sync, dev-*/*, allow
p, role:developer, applications, sync, prod-*/*, deny

g, org:backend-team, role:developer
```

Встроенные роли: `role:readonly` (только чтение) и `role:admin` (полный доступ). На них удобно опираться, постепенно добавляя более узкие кастомные роли.

## Q16. Как ArgoCD поддерживает multi-cluster деплой?

Модель `ArgoCD` — **hub-and-spoke**: один экземпляр ставится в управляющий кластер (hub) и оттуда деплоит в множество целевых (spoke). Своего агента в целевых кластерах не нужно — `ArgoCD` обращается к их API напрямую, используя сохранённые credentials.

**Регистрация внешнего кластера:**
```bash
argocd cluster add my-prod-cluster \
  --kubeconfig ~/.kube/config \
  --kube-context prod-context \
  --name production
```

Под капотом создаётся `Secret` в namespace `argocd`:
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mycluster-secret
  labels:
    argocd.argoproj.io/secret-type: cluster
stringData:
  name: production
  server: https://prod-cluster.example.com
  config: |
    {
      "bearerToken": "<token>",
      "tlsClientConfig": {
        "caData": "<base64-ca>"
      }
    }
```

Под капотом `ArgoCD` обращается к целевому кластеру через `ServiceAccount` с минимально необходимыми RBAC-правами — токен и CA хранятся в том самом cluster-секрете. Чтобы не плодить `Application` руками на каждый кластер, связку дополняют `ApplicationSet` + Cluster generator (см. Q12): новый зарегистрированный кластер автоматически получает нужные приложения.

## Q17. Какие подходы к управлению секретами используются в ArgoCD? (!)

Корень проблемы: GitOps требует хранить всё в Git, но класть туда секреты в открытом виде нельзя. `ArgoCD` сознательно **не управляет секретами сам** — вместо этого используют один из паттернов, который либо шифрует секрет перед коммитом, либо вообще держит его вне Git. Четыре подхода различаются тем, *где* живёт настоящее значение секрета.

**1. Sealed Secrets (Bitnami)** — секрет шифруется *перед* коммитом и спокойно лежит в Git в зашифрованном виде. Расшифровать его может только контроллер внутри кластера.
```bash
kubeseal --format yaml < secret.yaml > sealed-secret.yaml
# sealed-secret.yaml безопасно коммитить в Git
```
Настоящее значение зашифровано в Git; расшифровка происходит только внутри кластера контроллером Sealed Secrets.

**2. External Secrets Operator (ESO)** — секрет вообще не попадает в Git. В Git лежит лишь *ссылка* (`ExternalSecret`), а реальное значение хранится во внешнем хранилище (Vault, AWS SSM, GCP Secret Manager), откуда ESO его подтягивает и создаёт обычный `Secret`.
```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: db-credentials
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: vault-backend
    kind: ClusterSecretStore
  target:
    name: db-secret
  data:
    - secretKey: password
      remoteRef:
        key: secret/prod/database
        property: password
```

**3. ArgoCD Vault Plugin (AVP)** — Config Management Plugin: в Git лежат манифесты с плейсхолдерами, а реальные значения из Vault подставляются *в момент рендеринга* в Repo Server. Секрет не хранится в кластере как объект — он инжектится при каждой синхронизации.
```yaml
# В манифесте используются placeholders:
apiVersion: v1
kind: Secret
stringData:
  password: <path:secret/data/prod/db#password>
```

**4. SOPS** — шифрует YAML/JSON-файлы целиком ключами из age, PGP или AWS KMS; в Git лежит зашифрованный файл. Подключается к `ArgoCD` через CMP.

**Рекомендация:** для большинства команд берут ESO (если уже есть Vault/облачное хранилище) или Sealed Secrets (если хочется хранить всё в Git без внешних зависимостей) — это два наиболее зрелых решения.

## Q18. Что такое ArgoCD Image Updater?

**`ArgoCD Image Updater`** — отдельное дополнение, которое решает разрыв в GitOps-конвейере: CI собрал новый образ, но кто-то должен прописать новый тег в конфигурацию. Image Updater следит за container registry и при появлении новой версии сам обновляет тег образа — так выкат не требует ручного шага.

Ключевой вопрос — *куда* он записывает новый тег (write-back):
- **`git` write-back** — коммитит новый тег прямо в Git-репозиторий. Это «настоящий GitOps»: Git остаётся источником истины, изменение видно в истории.
- **`argocd` write-back** — меняет параметр `Application` напрямую, минуя Git. Проще, но менее GitOps-совместимо: в Git нет следа того, какой тег реально задеплоен.

```yaml
# Аннотации на Application объекте
annotations:
  argocd-image-updater.argoproj.io/image-list: myapp=registry.example.com/myapp
  argocd-image-updater.argoproj.io/myapp.update-strategy: semver
  argocd-image-updater.argoproj.io/myapp.allow-tags: ">=1.0.0"
  argocd-image-updater.argoproj.io/write-back-method: git
  argocd-image-updater.argoproj.io/git-branch: main
```

Стратегии обновления задают, какую новую версию считать «той самой»: `semver` (брать наибольшую по semver — обычно для prod), `latest` (самый свежий по времени тег), `name` (последний в алфавитном порядке), `digest` (отслеживать смену digest у фиксированного тега вроде `latest`).

## Q19. Как выполнить rollback в ArgoCD?

`ArgoCD` хранит историю деплоев (по умолчанию 10 последних ревизий, глубина задаётся полем `spec.revisionHistoryLimit` в манифесте `Application`), поэтому откатиться можно несколькими способами. Но важно понимать разницу между откатом *в ArgoCD* и откатом *в Git* — это ключевой нюанс вопроса (см. ниже).

**Через UI**: История → выбрать нужную ревизию → Rollback.

**Через CLI:**
```bash
# Посмотреть историю
argocd app history my-app

# Откатиться к ревизии 5
argocd app rollback my-app 5

# После rollback приложение переходит в режим suspended (auto-sync отключён)
# Чтобы вернуть auto-sync:
argocd app set my-app --sync-policy automated
```

**Главный нюанс: rollback в `ArgoCD` ≠ rollback в Git.** Команда `rollback` лишь деплоит манифесты из старой Git-ревизии, но сам Git при этом не меняется — HEAD остаётся «битым». Поэтому `ArgoCD` после отката отключает auto-sync (suspended): иначе на следующем reconcile он увидел бы расхождение с HEAD и тут же накатил обратно ту самую проблемную версию.

**Правильный GitOps-откат:** не использовать команду rollback в проде, а сделать `git revert` + push. Тогда HEAD снова указывает на рабочее состояние, и `ArgoCD` сам синхронизирует кластер — откат проходит штатным путём и остаётся в истории Git.

## Q20. Как организовать разделение CI и CD в GitOps pipeline с ArgoCD? (!)

Ключевая идея — **два репозитория**: код приложения (`app-repo`) и конфигурация деплоя (`config-repo`). CI отвечает за сборку образа и живёт в `app-repo`, CD (`ArgoCD`) следит за `config-repo`. Это разделяет зоны ответственности: разработчики правят код, а изменения инфраструктуры проходят через отдельный репозиторий со своими правами и ревью.

```
app-repo (код приложения)           config-repo (конфигурация)
┌────────────────────┐              ┌────────────────────────┐
│ src/               │              │ overlays/              │
│ Dockerfile         │  CI builds   │   dev/                 │
│ ...                │ ──────────►  │     kustomization.yaml │
└────────────────────┘  image +     │   prod/                │
                        update tag  │     kustomization.yaml │
                                    └────────────────────────┘
                                              ▲
                                              │ ArgoCD watches
                                              │
                                    ┌─────────┴──────────┐
                                    │   Kubernetes        │
                                    │   Cluster           │
                                    └────────────────────┘
```

Процесс по шагам (где заканчивается CI и начинается CD):
1. Разработчик делает `push` в `app-repo`.
2. CI (GitHub Actions, GitLab CI) собирает образ и пушит его в registry с новым тегом. **На этом зона CI заканчивается** — он не трогает кластер.
3. CI (или Image Updater) обновляет тег образа в `config-repo` — `kustomize edit set image`.
4. `ArgoCD` видит изменение в `config-repo` и синхронизирует кластер. **Это уже CD** — отдельный процесс с pull-моделью.

**Что даёт разделение репозиториев:**
- Разные права доступа: к коду — разработчики, к конфигурации деплоя — платформенная команда.
- Независимые ревью: изменение кода и изменение того, что катится в прод, проходят отдельную проверку.
- Чистая история деплоев: в `config-repo` каждый коммит — это факт выката, не замусоренный коммитами по коду.

## Q21. Как ArgoCD обнаруживает изменения в Git репозитории?

Два режима, и они не взаимоисключающие — webhook ускоряет реакцию, а polling остаётся страховкой на случай пропущенного webhook.

**Polling** (по умолчанию): `ArgoCD` сам опрашивает Git каждые 3 минуты (`timeout.reconciliation` в `argocd-cm`). Просто и надёжно, но между коммитом и выкатом проходит до 3 минут.

**Webhooks** (рекомендуется): Git-провайдер (GitHub, GitLab, Bitbucket) при push сам дёргает `ArgoCD`, и тот реагирует за секунды вместо минут. Polling при этом оставляют включённым как fallback.

Настройка webhook в GitHub:
- URL: `https://argocd.example.com/api/webhook`
- Content type: `application/json`
- Secret: генерируется и сохраняется в `argocd-secret`

```yaml
# argocd-secret
stringData:
  webhook.github.secret: "my-webhook-secret"
```

## Q22. Как настроить notifications в ArgoCD?

`ArgoCD Notifications` рассылает оповещения о событиях приложений (sync прошёл, health деградировал) во внешние каналы. Конфигурация состоит из трёх частей: **триггер** (`когда` слать — условие на состоянии приложения), **шаблон** (`что` слать — текст сообщения) и **сервис** (`куда` — Slack, Email и т.д.). Приложение подписывается на триггеры через аннотации.

```yaml
# argocd-notifications-cm ConfigMap
data:
  trigger.on-sync-succeeded: |
    - description: Application is synced
      send: [app-sync-succeeded]
      when: app.status.operationState.phase in ['Succeeded']
  template.app-sync-succeeded: |
    message: |
      Application {{.app.metadata.name}} successfully synced to {{.app.status.sync.revision}}
  service.slack: |
    token: $slack-token
```

Аннотации на `Application`:
```yaml
annotations:
  notifications.argoproj.io/subscribe.on-sync-succeeded.slack: my-channel
  notifications.argoproj.io/subscribe.on-sync-failed.slack: alerts-channel
```

Поддерживаемые каналы: Slack, Microsoft Teams, Email, OpsGenie, PagerDuty, GitHub (статусы PR), Telegram, Webhook.

## Q23. Что такое ignoreDifferences в ArgoCD? Когда это нужно?

`ignoreDifferences` — список полей, расхождения в которых `ArgoCD` не считает за drift при сравнении Git и кластера. Нужен он там, где **поле в кластере на законных основаниях меняет не Git, а кто-то другой**: autoscaler, mutating webhook, контроллер. Без этого приложение вечно болталось бы в `OutOfSync`, а с включённым `selfHeal` ещё и воевало бы с тем контроллером.

Типичные случаи:

```yaml
spec:
  ignoreDifferences:
    # HPA изменяет spec.replicas — игнорировать
    - group: apps
      kind: Deployment
      jsonPointers: ["/spec/replicas"]

    # Мутирующий webhook добавляет поле — игнорировать
    - group: ""
      kind: ServiceAccount
      jsonPointers: ["/secrets"]

    # Cert-manager заполняет caBundle — игнорировать
    - group: admissionregistration.k8s.io
      kind: MutatingWebhookConfiguration
      jqPathExpressions: [".webhooks[].clientConfig.caBundle"]

    # Игнорировать конкретный label
    - group: apps
      kind: Deployment
      managedFieldsManagers: ["kube-controller-manager"]
```

Указать путь к полю можно двумя способами: `jsonPointers` (RFC 6901, для конкретного поля по точному пути) или `jqPathExpressions` (jq-синтаксис, когда нужно выбрать поле в массиве или по условию — как `caBundle` во всех элементах `webhooks[]` выше).

## Q24. Как ArgoCD сравнивается с Flux CD? (!)

Оба — CNCF-проекты для GitOps, и оба решают одну задачу. Принципиальное различие в философии: `ArgoCD` — **централизованный, с богатым UI**, тяготеет к модели «push-кнопкой» поверх GitOps; `Flux` — **минималистичный набор контроллеров без UI**, чистый pull-GitOps, который ставится в каждый кластер.

| Критерий | ArgoCD | Flux CD |
|---|---|---|
| UI | Встроенный веб-интерфейс | Нет (только сторонние: Weave GitOps) |
| Модель | Push-кнопочный деплой + GitOps | Чисто GitOps (pull-only) |
| ApplicationSet | Есть | ImageUpdateAutomation + HelmRelease |
| Мультикластер | Централизованный (один ArgoCD) | Federated (Flux в каждом кластере) |
| Secrets | Плагины (Vault, ESO, Sealed Secrets) | SOPS нативно, ESO |
| OCI | ArgoCD 2.6+ | Flux 0.31+ |
| Helm | Application + helm source | HelmRelease CRD |
| Kustomize | Нативно | Kustomization CRD |
| SSO | Встроенный Dex | Только через внешний provider |
| Уведомления | ArgoCD Notifications | Alert + Provider CRD |
| Сложность | Более сложная установка | Более простой bootstrap (flux bootstrap) |
| Популярность | Доминирует в enterprise | Популярен в cloud-native community |

`ArgoCD` лучше подходит для команд, которым нужен UI и централизованное управление несколькими кластерами. `Flux` предпочтителен для строгого GitOps без UI и более гибкой multi-tenancy модели.

## Q25. Как работает SSO и аутентификация в ArgoCD?

В основе SSO `ArgoCD` лежит OIDC, а дальше есть два пути в зависимости от того, есть ли у вас собственный OIDC-провайдер. **Dex** — встроенный «переходник», который нужен, если ваш источник (LDAP, GitHub, SAML) не говорит на OIDC напрямую. Если же есть готовый OIDC-провайдер (Keycloak, Okta, Azure AD) — подключаются к нему напрямую, без Dex.

**Встроенный Dex** — OpenID Connect-провайдер, выступающий мостом к upstream-источникам: LDAP, GitHub, GitLab, Google, SAML, OIDC.

Настройка GitHub OAuth (через Dex):
```yaml
# argocd-cm ConfigMap
data:
  url: https://argocd.example.com
  dex.config: |
    connectors:
      - type: github
        id: github
        name: GitHub
        config:
          clientID: $dex.github.clientID
          clientSecret: $dex.github.clientSecret
          orgs:
            - name: my-org
              teams:
                - platform-team
                - backend-team
```

**Внешний OIDC** (без Dex): прямая интеграция с уже готовым провайдером — Keycloak, Okta, Azure AD. Запрашивается scope `groups`, чтобы получить членство пользователя в группах.
```yaml
data:
  oidc.config: |
    name: Keycloak
    issuer: https://keycloak.example.com/realms/myrealm
    clientID: argocd
    clientSecret: $oidc.keycloak.clientSecret
    requestedScopes: [openid, profile, email, groups]
```

**Связь с RBAC:** аутентификация лишь подтверждает, *кто* пользователь и в каких он группах. Что ему *разрешено*, решает RBAC (см. Q15): группы из токена маппятся на роли через строки `g, <группа>, <роль>`.

## Q26. Что такое syncOptions в ArgoCD? Опишите основные опции.

`syncOptions` — набор флагов, тонко настраивающих *как именно* `ArgoCD` применяет манифесты (тогда как `prune`/`selfHeal` из Q7 решают, *что* применять). Это второй уровень контроля над процессом синхронизации.

```yaml
syncPolicy:
  syncOptions:
    - CreateNamespace=true            # создать namespace если не существует
    - PrunePropagationPolicy=foreground  # foreground/background/orphan
    - PruneLast=true                  # удалять ресурсы после применения новых
    - Replace=true                    # использовать kubectl replace вместо apply
    - ApplyOutOfSyncOnly=true         # применять только OutOfSync ресурсы
    - ServerSideApply=true            # использовать Server-Side Apply (SSA)
    - FailOnSharedResource=true       # ошибка если ресурс принадлежит другому App
    - Validate=false                  # отключить kubectl validation
    - RespectIgnoreDifferences=true   # учитывать ignoreDifferences при auto-sync
```

Наиболее важные на практике:
- **`ServerSideApply=true`** — применяет манифесты через Server-Side Apply с field ownership; спасает от ошибки «слишком большая аннотация `last-applied-configuration`» на крупных ресурсах и CRD.
- **`ApplyOutOfSyncOnly=true`** — обрабатывает только реально изменившиеся ресурсы, не трогая уже синхронизированные; заметно ускоряет sync больших приложений.
- **`PruneLast=true`** — удаляет старые ресурсы *после* создания новых, а не до; снижает риск кратковременного простоя при обновлении.

## Q27. Как отлаживать проблемы синхронизации в ArgoCD?

Общий принцип отладки: сначала посмотреть **что именно расходится** (`argocd app diff`), затем — **какой манифест сгенерировался** (`argocd app manifests`), и только потом лезть в **логи компонентов**. Это движение от симптома к причине. Ниже — типичные проблемы по этой схеме.

**OutOfSync без видимых причин:**
```bash
argocd app diff my-app          # показать расхождения
argocd app manifests my-app     # показать сгенерированные манифесты
kubectl get events -n my-namespace  # события в namespace
```

**Sync Failed:**
```bash
argocd app get my-app           # статус и последняя операция
argocd app sync my-app --dry-run  # пробный прогон без применения
kubectl logs -n argocd deployment/argocd-application-controller
```

**Ресурс всегда OutOfSync (неправильный ignoreDifferences):**
```bash
# Получить реальный diff в json
argocd app diff my-app --hard-refresh
```

**Проблемы с рендерингом Helm:**
```bash
argocd app manifests my-app --source-position 1  # для multi-source
helm template . -f values.yaml  # локальная проверка
```

**Логи компонентов:**
```bash
kubectl logs -n argocd -l app.kubernetes.io/name=argocd-repo-server
kubectl logs -n argocd -l app.kubernetes.io/name=argocd-server
```

## Q28. Что такое multi-source Applications в ArgoCD?

**Multi-source** (`ArgoCD` 2.6+) позволяет одному `Application` собирать манифесты из нескольких источников сразу. Главная задача, которую это решает: взять *чужой* Helm-чарт (например, публичный `postgresql`) и подложить к нему *свои* values из отдельного репозитория — не форкая чарт. Источники связываются через `ref`: один объявляет алиас (`ref: values`), другой ссылается на него (`$values/...`).

```yaml
spec:
  sources:
    - repoURL: https://charts.bitnami.com/bitnami
      chart: postgresql
      targetRevision: 12.x.x
      helm:
        valueFiles:
          - $values/charts/postgresql/values.yaml
    - repoURL: https://github.com/org/config-repo
      targetRevision: HEAD
      ref: values                           # алиас для ссылки из другого source
```

**Сценарий применения:** Helm-чарт из одного репозитория + values из другого. Так чарт и конфигурацию можно версионировать независимо: обновление values не требует трогать репозиторий с чартом, и наоборот.

## Q29. Как настроить resource exclusions в ArgoCD?

`Resource Exclusions` глобально выводят определённые виды ресурсов из-под наблюдения `ArgoCD`. По умолчанию `ArgoCD` следит за *всеми* ресурсами в кластере — и на шумных типах вроде `Event` это создаёт лишнюю нагрузку на контроллер и Redis. Exclusions говорят «эти ресурсы игнорируй полностью».

```yaml
# argocd-cm ConfigMap
data:
  resource.exclusions: |
    - apiGroups: ["velero.io"]
      kinds: ["Backup"]
      clusters: ["*"]
    - apiGroups: [""]
      kinds: ["Event"]
      clusters: ["*"]
```

`Resource Inclusions` — зеркальный подход: явный whitelist, при котором `ArgoCD` следит *только* за перечисленными видами, а всё остальное игнорирует. Это «по умолчанию запрещено» вместо «по умолчанию разрешено» — жёстче, но требует поддерживать список.
```yaml
data:
  resource.inclusions: |
    - apiGroups: ["*"]
      kinds: ["Deployment", "Service", "ConfigMap"]
      clusters: ["*"]
```

**Главный практический эффект** — исключение `Event` (их в кластере тысячи) заметно снижает нагрузку на контроллер и Redis.

## Q30. Как работает ArgoCD в высоконагруженных окружениях? Как масштабировать?

Узкое место при росте — **Application Controller**: он держит control loop по всем приложениям, и при сотнях `Application` или десятках кластеров один экземпляр перестаёт справляться. Поэтому масштабирование идёт по двум линиям: шардирование контроллера и тюнинг частоты/параллелизма reconcile. Остальные компоненты (API/Repo Server) stateless и масштабируются проще.

**Шардирование Application Controller** — кластеры распределяются между несколькими экземплярами контроллера, каждый обслуживает свой шард:
```yaml
# argocd-cm
data:
  application.instanceLabelKey: argocd.argoproj.io/app-name
  # Разбить Application Controller на несколько шардов:

# Запустить несколько реплик с указанием номера шарда:
# --shard 0 --shards 3
# --shard 1 --shards 3
# --shard 2 --shards 3
```

`ArgoCD` 2.9+ умеет распределять кластеры между шардами автоматически (`dynamic` шардирование), без ручной привязки.

**Настройки производительности** (компромисс «свежесть vs нагрузка»):
```yaml
# argocd-cm
data:
  timeout.reconciliation: 180s      # частота опроса Git (увеличить при нагрузке)
  timeout.hard.reconciliation: 0s   # отключить принудительный reconcile
  app.sync.concurrency: 20          # параллельные синхронизации
  resource.comparisons.cached.duration: 10s
```

**Redis в режиме HA** — чтобы кеш не стал единой точкой отказа: `argocd-redis-ha` (встроенный) или внешний Redis Sentinel/Cluster.

**Репликация API Server и Repo Server** — это stateless-компоненты, поэтому масштабируются простым увеличением числа реплик (особенно полезно для Repo Server при тяжёлом рендеринге Helm/Kustomize).

## Q31. Что такое Argo Rollouts? Как он интегрируется с ArgoCD?

**`Argo Rollouts`** — отдельный Kubernetes-контроллер, добавляющий продвинутые стратегии выката (`Canary`, `Blue/Green`) с анализом метрик и автоматическим откатом. Он закрывает то, чего нет у обычного `Deployment`: тот умеет только rolling update, без постепенного перевода трафика и без автоматического решения «откатываться или нет» по метрикам.

**Как делятся роли с `ArgoCD`:** `ArgoCD` отвечает за *доставку* (что задеплоено = что в Git), а `Argo Rollouts` — за *стратегию выката* самого приложения. На практике:
1. В Git лежит `Rollout` CRD вместо `Deployment`; `ArgoCD` деплоит его как любой ресурс.
2. `ArgoCD` понимает health-статус `Rollout` (через встроенную поддержку — см. Q14) и показывает прогресс канарейки в UI.
3. Операциями `Rollout` (promote/abort) можно управлять, в том числе через `ArgoCD`.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Rollout
metadata:
  name: my-app
spec:
  strategy:
    canary:
      steps:
        - setWeight: 10     # 10% трафика на новую версию
        - pause: {}         # ручное подтверждение
        - setWeight: 50
        - pause: {duration: 5m}
        - setWeight: 100
      analysis:
        templates:
          - templateName: success-rate
        args:
          - name: service-name
            value: my-app-canary
```

Вместе `ArgoCD` + `Argo Rollouts` дают полный GitOps-конвейер с progressive delivery: коммит в Git запускает канареечный выкат, метрики решают, продвигать его или откатить.

## Q32. Как настроить disaster recovery для ArgoCD? (!)

DR для `ArgoCD` упрощается тем, что почти всё его состояние **декларативно и лежит в Kubernetes** (`Application`, `AppProject`, репозитории, cluster-секреты) — а сам GitOps означает, что желаемое состояние приложений всё равно хранится в Git. Поэтому стратегия двойная: регулярный экспорт объектов `ArgoCD` + HA-установка, чтобы падение пода не останавливало деплои.

**Бэкап критичных данных** (хранятся в Kubernetes secrets/configmaps) — одной командой экспорта:
```bash
# Экспорт всех Application, AppProject, репозиториев
argocd admin export > argocd-backup.yaml

# Импорт при восстановлении
argocd admin import - < argocd-backup.yaml
```

**Что попадает в backup:**
- `Application` и `ApplicationSet` объекты.
- `AppProject` объекты.
- Конфигурация репозиториев (без паролей — только адреса и настройки).
- **Важно:** `Secret` с credentials к кластерам и репозиториям экспорт *не* выгружает в открытом виде, поэтому их бэкапят отдельно (иначе при восстановлении `ArgoCD` не сможет достучаться до кластеров).

**High Availability-установка** — чтобы DR не сводился к восстановлению из бэкапа после каждого сбоя пода:
```yaml
# Использовать HA-чарт:
helm install argocd argo/argo-cd \
  --set redis-ha.enabled=true \
  --set controller.replicas=1 \
  --set server.replicas=2 \
  --set repoServer.replicas=2 \
  --set applicationSet.replicas=2
```

**Автоматизация:** CronJob, запускающий `argocd admin export` по расписанию с выгрузкой в S3/GCS.

## Q33. Как управлять конфигурацией самого ArgoCD в стиле GitOps?

Логичный приём — **bootstrapping** (он же «App that manages itself»): `ArgoCD` управляет собственной установкой через `Application`. Иначе получается несостыковка — все приложения под GitOps, а конфигурация самого инструмента (репозитории, RBAC, плагины) правится руками через `kubectl`. Bootstrapping убирает это исключение: один `Application` деплоит сам `ArgoCD` из Helm-чарта.

```yaml
# Один Application деплоит сам ArgoCD через Helm
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: argocd
  namespace: argocd
spec:
  source:
    repoURL: https://argoproj.github.io/argo-helm
    chart: argo-cd
    targetRevision: 6.x.x
    helm:
      valueFiles: [values.yaml]  # из того же или отдельного репо
  destination:
    server: https://kubernetes.default.svc
    namespace: argocd
  syncPolicy:
    automated:
      selfHeal: true
```

**Что это даёт:** любое изменение конфигурации `ArgoCD` — добавить репозиторий, поправить RBAC — проходит тот же путь, что и обычный деплой: Git PR → review → merge → auto-sync. Конфигурация инструмента становится такой же аудируемой и откатываемой, как и приложения.

## Q34. Как защитить ArgoCD от несанкционированного доступа?

`ArgoCD` — крайне привилегированная система: он держит доступ к нескольким кластерам сразу, поэтому его компрометация = компрометация всего парка. Защита строится по принципу глубокой обороны: ограничить *сеть*, ограничить *права* и защитить *секреты/каналы*.

**Сетевая изоляция** — уменьшить поверхность атаки:
- API Server — только за VPN или bastion, не «наружу».
- Внутренние компоненты (`Redis`, `Repo Server`) не должны быть доступны извне вообще.
- `NetworkPolicy` ограничивает трафик между самими компонентами.

**Минимальные привилегии** — даже при взломе учётки ущерб ограничен её ролью:
- `AppProject` ограничивает, в какие namespace/кластеры приложение вообще может деплоить (см. Q15).
- `role:readonly` по умолчанию для большинства пользователей.
- В целевых кластерах — отдельные `ServiceAccount` с минимальными RBAC-правами, а не cluster-admin.

**Защита секретов:**
```bash
# Ротация admin пароля
argocd admin initial-password -n argocd  # только при первоначальной настройке
argocd account update-password           # изменить пароль
```

**Аудит**: `ArgoCD` логирует все операции — эти audit-логи нужно отправлять в SIEM, чтобы факт несанкционированного sync был заметен.

**TLS**: API Server только по TLS; нельзя отключать проверку сертификатов (`--insecure`) — иначе канал управления кластерами открыт для MITM.

## Q35. Опишите типичный workflow деплоя с ArgoCD на production.

Сквозной пример, связывающий вместе механизмы из предыдущих вопросов: путь изменения от PR разработчика до канареечного выката и автоотката. Идея — на каждом шаге собрать кусочки (CI → config-repo → webhook → hooks → waves → Rollouts → notifications).

```
1. Developer PR → code review → merge в main
   └─ CI: build image, push to registry with tag (git SHA)

2. Automated или manual update config-repo:
   └─ kustomize edit set image myapp:abc1234
   └─ PR в config-repo → review infrastructure team → merge

3. ArgoCD обнаруживает изменение:
   └─ Webhook → OutOfSync статус

4. Sync процесс:
   └─ PreSync hooks: db migrations job
   └─ Sync wave 0: ConfigMaps, Secrets
   └─ Sync wave 1: Deployments (Argo Rollouts)
   └─ PostSync hooks: smoke tests job

5. Мониторинг:
   └─ Rollouts: canary 10% → анализ метрик → 100%
   └─ Notifications: Slack сообщение о деплое

6. В случае проблем:
   └─ Argo Rollouts: автоматический rollback по метрикам
   └─ или: git revert + merge → ArgoCD синхронизирует откат
```

**Принципы, на которых это держится:** любое изменение проходит через Git PR; прод-деплои требуют явного approve; история деплоев = история коммитов в `config-repo`; откат — это `git revert`, а не ручное вмешательство в кластер (см. Q19).

---

## Q36. Как ArgoCD интегрируется с Sealed Secrets и External Secrets Operator?

Базовый конфликт: GitOps хочет хранить всё в Git, но открытые секреты туда класть нельзя. Sealed Secrets и ESO решают это по-разному — и эта разница определяет выбор. **Sealed Secrets** держит секрет *в Git, но зашифрованным*. **ESO** держит секрет *вне Git*, оставляя в репозитории только ссылку. `ArgoCD` в обоих случаях применяет обычный CR и не видит самого секрета.

### Sealed Secrets (Bitnami)

`SealedSecret` — CRD, зашифрованный *открытым* ключом кластера (асимметрично). Зашифровать секрет может кто угодно, а расшифровать — только контроллер внутри кластера, у которого есть приватный ключ. Поэтому зашифрованный файл безопасно лежит в публичном Git.

```bash
# Зашифровать секрет (открытый ключ скачивается автоматически)
kubeseal --format yaml < secret.yaml > sealed-secret.yaml

# Результат безопасно коммитить в Git
```

```yaml
apiVersion: bitnami.com/v1alpha1
kind: SealedSecret
metadata:
  name: db-credentials
  namespace: production
spec:
  encryptedData:
    password: AgBl3m...  # зашифровано
  template:
    metadata:
      name: db-credentials
    type: Opaque
```

**Поток:** `ArgoCD` применяет `SealedSecret` как обычный ресурс → контроллер Sealed Secrets его расшифровывает → в кластере появляется обычный `Secret`.

**Подводный камень:** приватный ключ привязан к кластеру. При его ротации или восстановлении кластера в другом месте все ранее зашифрованные секреты придётся перешифровать заново.

### External Secrets Operator (ESO)

**ESO** синхронизирует секреты из внешнего хранилища (Vault, AWS Secrets Manager, GCP Secret Manager) в Kubernetes Secrets. В Git хранится не секрет, а *указатель* на него — поэтому ротация значения в Vault подхватывается без коммита в Git.

```yaml
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: db-credentials
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: vault-backend
    kind: SecretStore
  target:
    name: db-credentials
  data:
    - secretKey: password
      remoteRef:
        key: secret/production/db
        property: password
```

**Поток с `ArgoCD`:** в Git лежат только `ExternalSecret` CR (без значений) → `ArgoCD` их применяет → ESO читает реальные значения из Vault/AWS и создаёт Kubernetes Secrets. `ArgoCD` управляет «ссылками», ESO — содержимым.

**Сравнение подходов** (главный критерий выбора — где живёт значение и как ротируется):

| Подход | Хранилище | Сложность | Ротация |
|--------|-----------|-----------|---------|
| Sealed Secrets | Git (зашифр.) | Низкая | Ручная |
| ESO + Vault | Vault/AWS/GCP | Средняя | Автоматическая |
| ArgoCD Vault Plugin | Vault | Высокая | При sync |

---

## Q37. Что такое ArgoCD Image Updater и как он работает?

**ArgoCD Image Updater** автоматизирует один конкретный шаг GitOps-конвейера: после того как CI собрал и запушил новый образ, кто-то должен обновить тег в конфигурации деплоя. Без Image Updater это либо ручная правка, либо отдельный шаг в CI. Image Updater сам следит за registry и обновляет тег — детали write-back и стратегий разобраны ниже.

### Установка и настройка

```bash
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj-labs/argocd-image-updater/stable/manifests/install.yaml
```

### Аннотации на Application

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: my-app
  annotations:
    argocd-image-updater.argoproj.io/image-list: myapp=registry.io/org/myapp
    argocd-image-updater.argoproj.io/myapp.update-strategy: semver
    argocd-image-updater.argoproj.io/myapp.allow-tags: regexp:^v[0-9]+\.[0-9]+\.[0-9]+$
    argocd-image-updater.argoproj.io/write-back-method: git
    argocd-image-updater.argoproj.io/git-branch: main
```

### Стратегии обновления

| Стратегия | Описание |
|-----------|----------|
| `semver` | Обновляет до последней semver-версии (рекомендуется) |
| `latest` | Всегда берёт самый свежий тег (не для prod!) |
| `name` | Алфавитно последний тег |
| `digest` | Отслеживает digest образа (для `latest` тега) |

### Write-back режимы

Главный архитектурный выбор — *куда* записывается новый тег:
- **`git`** — Image Updater коммитит изменение тега прямо в репозиторий, дальше `ArgoCD` подхватывает его обычным sync. Git остаётся источником истины, выкат виден в истории — это правильный GitOps.
- **`argocd`** — меняет параметр `Application` через API `ArgoCD`, минуя Git. Проще в настройке, но в Git не остаётся следа реального тега, поэтому менее GitOps-совместимо.

**Типичный CI/CD flow с Image Updater:**
```
CI build → push image:v1.2.3 → Image Updater обнаруживает → 
коммит в config-repo → ArgoCD sync → деплой v1.2.3
```

---

## Q38. Как ArgoCD работает в multi-cluster окружении — hub-spoke модель?

**Hub-spoke** — архитектура, где один центральный `ArgoCD` («хаб») управляет деплоями во множество целевых кластеров («спицы»). Спицам не нужен свой агент `ArgoCD` — хаб обращается к их API напрямую по сохранённым credentials. Это даёт единую панель управления и единый источник конфигурации на весь парк кластеров.

```
ArgoCD (hub)
├── prod-cluster-eu    (spoke)
├── prod-cluster-us    (spoke)
├── staging-cluster    (spoke)
└── dev-cluster-01     (spoke)
```

### Регистрация внешних кластеров

```bash
# Добавить кластер через CLI
argocd cluster add prod-eu --name prod-eu \
  --kubeconfig ~/.kube/prod-eu.yaml

# Список зарегистрированных кластеров
argocd cluster list
```

Cluster secret (создаётся автоматически через CLI):
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: prod-eu-cluster
  namespace: argocd
  labels:
    argocd.argoproj.io/secret-type: cluster
type: Opaque
stringData:
  name: prod-eu
  server: https://prod-eu.k8s.example.com
  config: |
    {
      "bearerToken": "...",
      "tlsClientConfig": { "caData": "..." }
    }
```

### ApplicationSet для multi-cluster деплоя

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
spec:
  generators:
    - clusters:
        selector:
          matchLabels:
            environment: production  # все production-кластеры
  template:
    spec:
      destination:
        server: "{{server}}"  # URL кластера из cluster secret
        namespace: my-app
```

**Рекомендации:**
- Разграничивать доступ к кластерам через `AppProject` — чтобы команда деплоила только в свои кластеры (см. Q15).
- Один экземпляр `ArgoCD` способен управлять 100+ кластерами; при таком масштабе подключают шардирование контроллера (см. Q30).
- Не плодить `Application` вручную: `ApplicationSet` + Cluster generator создают приложения на новые кластеры автоматически.

---

## Q39. Как ArgoCD работает с Kustomize overlays?

**Kustomize** кастомизирует Kubernetes-манифесты *без шаблонизации*: есть общая база (`base/`) и оверлеи (`overlays/<env>/`), которые накладывают на неё патчи под конкретную среду. `ArgoCD` поддерживает Kustomize из коробки — определяет его по `kustomization.yaml` и сам вызывает `kustomize build`.

### Типичная структура репозитория

```
k8s/
  base/
    deployment.yaml
    service.yaml
    kustomization.yaml
  overlays/
    dev/
      kustomization.yaml   # patches для dev
    staging/
      kustomization.yaml
    prod/
      kustomization.yaml   # patches для prod
```

### Application с Kustomize overlay

```yaml
spec:
  source:
    repoURL: https://github.com/org/repo
    path: k8s/overlays/prod    # ArgoCD автоопределяет Kustomize
    kustomize:
      images:
        - myapp=registry.io/org/myapp:v1.2.3  # override image tag
      commonLabels:
        version: v1.2.3
```

### Kustomize override image в ArgoCD

```bash
# Через CLI обновить тег образа в Application
argocd app set my-app \
  --kustomize-image myapp=registry.io/org/myapp:v1.3.0
```

**Рекомендации для связки ArgoCD + Kustomize:**
- `base/` хранит только общие ресурсы, без специфики среды — иначе оверлеи теряют смысл.
- `overlays/<env>/kustomization.yaml` содержит лишь патчи и overrides конкретной среды.
- Теги образов переопределять через `kustomize.images`, а не хардкодить в `base` — тогда обновление тега не трогает общую базу (с этим же полем работает Image Updater из Q37).
- Не использовать `vars` (deprecated) — вместо них `replacements`.

---

## Q40. Как устроен RBAC в ArgoCD — Projects, AppProject, роли?

RBAC `ArgoCD` работает на двух уровнях, и их легко спутать. **AppProject** — это изоляция: он ограничивает, *куда* приложения проекта вообще могут деплоить (репозитории, кластеры, виды ресурсов). **RBAC-политики** (`argocd-rbac-cm`) — это разрешения для людей: *что* пользователь может делать с приложениями (sync, get, delete). Первое — про границы для `Application`, второе — про права субъектов.

### AppProject — изоляция приложений

Задаёт «песочницу»: разрешённые источники, назначения и допустимые виды ресурсов. Создать `Application` за пределами этих границ нельзя, даже имея права.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: AppProject
metadata:
  name: team-backend
  namespace: argocd
spec:
  description: "Backend team applications"
  # Разрешённые source репозитории
  sourceRepos:
    - https://github.com/org/backend-*
  # Разрешённые destination кластеры и namespace
  destinations:
    - namespace: backend-*
      server: https://kubernetes.default.svc
    - namespace: backend-*
      server: https://prod-cluster.example.com
  # Запрещённые виды ресурсов
  clusterResourceBlacklist:
    - group: ""
      kind: PersistentVolume
  # Разрешённые namespace-ресурсы
  namespaceResourceWhitelist:
    - group: "apps"
      kind: Deployment
    - group: ""
      kind: Service
  # Роли внутри проекта
  roles:
    - name: developer
      policies:
        - p, proj:team-backend:developer, applications, sync, team-backend/*, allow
        - p, proj:team-backend:developer, applications, get, team-backend/*, allow
      groups:
        - github-org:backend-team
```

### RBAC-политики (argocd-rbac-cm)

Формат Casbin: строки `p` задают разрешения роли, строки `g` привязывают группу (из SSO-токена, см. Q25) к роли. `policy.default` определяет права для всех, кто не подпал ни под одно правило.

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: argocd-rbac-cm
  namespace: argocd
data:
  policy.default: role:readonly
  policy.csv: |
    # Роль admin — полный доступ
    p, role:admin, applications, *, */*, allow
    p, role:admin, clusters, get, *, allow
    
    # Роль developer — только sync и get
    p, role:developer, applications, get, */*, allow
    p, role:developer, applications, sync, */*, allow
    
    # Привязка групп к ролям
    g, github-org:devops, role:admin
    g, github-org:developers, role:developer
```

**Встроенные роли:**
- `role:admin` — полный доступ
- `role:readonly` — только чтение

---

## Q41. Как настроить ArgoCD Notifications?

**ArgoCD Notifications** оповещает о событиях приложений (sync, health, deploy) во внешние каналы. Конфигурация собирается из трёх кирпичиков: **сервис** (куда слать — Slack, Email), **триггер** (когда слать — условие на статусе приложения) и **шаблон** (что слать — текст). Приложение подключает нужные триггеры через аннотации.

### Установка

```bash
kubectl apply -n argocd -f \
  https://raw.githubusercontent.com/argoproj-labs/argocd-notifications/stable/manifests/install.yaml
```

### Конфигурация (ConfigMap)

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: argocd-notifications-cm
  namespace: argocd
data:
  # Канал Slack
  service.slack: |
    token: $slack-token
    
  # Триггер: уведомить при успешном sync
  trigger.on-sync-succeeded: |
    - when: app.status.operationState.phase in ['Succeeded']
      send: [app-sync-succeeded]
      
  # Триггер: уведомить при деградации
  trigger.on-health-degraded: |
    - when: app.status.health.status == 'Degraded'
      send: [app-health-degraded]
      
  # Шаблон сообщения
  template.app-sync-succeeded: |
    message: |
      Application {{.app.metadata.name}} synced successfully.
      Revision: {{.app.status.sync.revision}}
    slack:
      attachments: |
        [{
          "color": "#18be52",
          "title": "✅ {{.app.metadata.name}} deployed",
          "text": "Revision: {{.app.status.sync.revision}}"
        }]
```

### Подписка на уведомления (аннотация Application)

```yaml
metadata:
  annotations:
    notifications.argoproj.io/subscribe.on-sync-succeeded.slack: deployments-channel
    notifications.argoproj.io/subscribe.on-health-degraded.slack: alerts-channel
```

**Поддерживаемые каналы:** Slack, Teams, Email, PagerDuty, OpsGenie, Telegram, GitHub, Webhook.

---

## Q42. ArgoCD vs Flux CD — сравнение подходов GitOps

Оба инструмента — CNCF Graduated и решают одну задачу, поэтому в ответе важно показать не список различий, а *философию*. `ArgoCD` — **монолитный, application-centric, с богатым UI**: всё крутится вокруг ресурса `Application`, есть наглядная визуализация. `Flux` — **модульный, source-centric, без UI**: набор независимых контроллеров (`GitRepository`, `Kustomization`, `HelmRelease`), которые удобно встраивать как GitOps Toolkit.

| Критерий | ArgoCD | Flux CD |
|----------|--------|---------|
| **UI** | Богатый веб-интерфейс | Нет (только CLI) |
| **Установка** | Монолитная (один Deployment) | Модульная (отдельные контроллеры) |
| **Подход** | Application-centric | Source-centric |
| **Multi-tenancy** | AppProject + RBAC | Kustomization + RBAC |
| **Image Updater** | Отдельный компонент | Встроен (image-reflector) |
| **Notifications** | Отдельный контроллер | Встроен (notification-controller) |
| **Drift detection** | Встроен, видно в UI | Встроен, только в логах/Prometheus |
| **Multi-cluster** | Hub-spoke (один ArgoCD) | Hub-spoke (fleet shard) |
| **Helm** | Поддержка через source | Helm controller |
| **Kustomize** | Поддержка через source | Kustomize controller |
| **CRD** | `Application`, `ApplicationSet` | `GitRepository`, `Kustomization`, `HelmRelease` |
| **Bootstrap** | Helm/YAML | `flux bootstrap` |
| **Зрелость** | CNCF Graduated | CNCF Graduated |

### Когда выбрать ArgoCD:
- Нужен визуальный UI для команды
- Multi-cluster с централизованным управлением
- Важна видимость sync status через браузер
- Команда привыкла к Application-centric модели

### Когда выбрать Flux:
- Предпочтительна минималистичная, модульная архитектура
- Flux устанавливается в managed-кластере (меньше footprint)
- Нужны GitOps Toolkit примитивы для построения кастомных решений
- Многие команды уже используют Flux в организации

**На практике:** ArgoCD доминирует в enterprise из-за UI; Flux популярен в cloud-native стартапах и проектах с сильной DevOps культурой.

---

## See also

- [Kubernetes](kubernetes-interview.md)
- [Helm](helm-interview.md)
- [Terraform](terraform-interview.md)
- [CI/CD пайплайны](../cicd/pipeline-design-interview.md)
- [Стратегии деплоя](../cicd/deployment-strategies-interview.md)
- [Docker](docker-interview.md)
- [Ansible](ansible-interview.md)
- [HashiCorp Consul](consul-interview.md)
- [Git](git-interview.md)
- [Gradle и Maven](gradle-maven-interview.md)
