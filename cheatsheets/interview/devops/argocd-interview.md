---
title: "Вопросы на собеседовании: ArgoCD и GitOps"
description: "Вопросы и ответы по ArgoCD и GitOps: архитектура, Application CRD, sync стратегии, ApplicationSet, App of Apps, Sync Waves, Hooks, RBAC, multi-cluster, secrets management, Image Updater, сравнение с Flux."
tags:
  - interview
  - devops
  - argocd-interview
aliases:
  - "ArgoCD interview"
  - "ArgoCD собеседование"
  - "GitOps вопросы"
  - "ArgoCD Kubernetes CD"
difficulty: "intermediate"
updated: "2026-04-13"
---
# Вопросы на собеседовании: `ArgoCD` и `GitOps`

Вопросы и ответы по `ArgoCD` и `GitOps`: принципы GitOps, архитектура `ArgoCD`, `Application` CRD, стратегии синхронизации, `ApplicationSet`, `App of Apps` паттерн, `Sync Waves`, хуки, `RBAC`, multi-cluster деплой, управление секретами, `ArgoCD Image Updater`, сравнение с `Flux`.

Дата последнего обновления: 2026-04-13

**`ArgoCD`** — декларативный GitOps-контроллер для `Kubernetes`, реализующий непрерывную доставку на основе `Git` как единственного источника истины. Является частью экосистемы `Argo Project` (наряду с `Argo Workflows`, `Argo Events`, `Argo Rollouts`) и входит в состав CNCF. `ArgoCD` следит за состоянием кластера и автоматически синхронизирует его с желаемым состоянием, описанным в репозитории.

## Q1. Что такое GitOps? Назовите его ключевые принципы.

**GitOps** — практика управления инфраструктурой и приложениями, при которой `Git`-репозиторий является единственным источником истины (`Single Source of Truth`) для декларативной конфигурации системы.

Четыре принципа GitOps (по спецификации OpenGitOps):
1. **Declarative** — желаемое состояние системы описывается декларативно (YAML-манифесты, Helm-чарты, Kustomize)
2. **Versioned and immutable** — конфигурация хранится в `Git` с историей изменений; каждый коммит — неизменяемый снимок состояния
3. **Pulled automatically** — агент в кластере самостоятельно вытягивает (`pull`) желаемое состояние и применяет его
4. **Continuously reconciled** — агент непрерывно сверяет реальное состояние с желаемым и устраняет расхождения

Преимущества: аудит изменений через `git log`, rollback через `git revert`, разграничение CI и CD, уменьшение attack surface (не нужен `kubectl` вне кластера).

## Q2. Чем GitOps отличается от традиционного CI/CD подхода (push-based)?

| Критерий | Push-based (традиционный) | GitOps (pull-based) |
|---|---|---|
| Инициатор деплоя | CI/CD пайплайн | Агент внутри кластера |
| Доступ к кластеру | CI/CD сервер имеет `kubeconfig` | Только агент внутри кластера |
| Источник истины | Пайплайн + скрипты | `Git`-репозиторий |
| Drift detection | Отсутствует | Встроен |
| Rollback | Повторный запуск пайплайна | `git revert` + автосинхронизация |
| Аудит | Логи CI | `git log` |

В push-подходе CI-сервер (Jenkins, GitHub Actions) напрямую вызывает `kubectl apply` или `helm upgrade`. В GitOps агент (`ArgoCD`, `Flux`) сам следит за репозиторием и синхронизирует кластер.

## Q3. Опишите архитектуру ArgoCD. Из каких компонентов она состоит? (!)

`ArgoCD` состоит из нескольких ключевых компонентов:

- **API Server** (`argocd-server`) — gRPC/REST API сервер, обрабатывает запросы от UI, CLI и внешних систем. Управляет аутентификацией, авторизацией, проксирует запросы к Application Controller
- **Application Controller** (`argocd-application-controller`) — Kubernetes-контроллер, реализующий control loop. Следит за состоянием `Application` CR, сравнивает desired state (Git) и live state (кластер), вычисляет `OutOfSync` статус, выполняет синхронизацию
- **Repo Server** (`argocd-repo-server`) — сервис для работы с Git-репозиториями. Клонирует репозитории, рендерит манифесты (Helm, Kustomize, Jsonnet, plain YAML), кеширует результаты
- **Redis** — кеш для хранения состояния приложений, git-объектов, результатов рендеринга манифестов. Снижает нагрузку на API-сервер
- **ApplicationSet Controller** — контроллер для `ApplicationSet` CRD, генерирует множество `Application` объектов по шаблону
- **Dex** (опционально) — встроенный OpenID Connect провайдер для SSO-интеграции (LDAP, GitHub, Google, SAML)
- **Notifications Controller** — отправляет уведомления о событиях в Slack, Teams, Email, PagerDuty и т.д.

## Q4. Что такое Application CRD в ArgoCD? Опишите его ключевые поля.

`Application` — основной Custom Resource в `ArgoCD`, описывающий, что и куда деплоить:

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
- `source` — откуда брать манифесты (Git repo + path, или Helm chart + values)
- `destination` — в какой кластер и namespace деплоить
- `syncPolicy` — ручная или автоматическая синхронизация
- `ignoreDifferences` — поля, расхождения в которых игнорируются

## Q5. Какие статусы синхронизации и здоровья имеет Application в ArgoCD? (!)

**Sync Status** — сравнение desired state (Git) с live state (кластер):
- `Synced` — кластер соответствует Git
- `OutOfSync` — есть расхождения между Git и кластером
- `Unknown` — не удалось получить состояние

**Health Status** — состояние ресурсов в кластере:
- `Healthy` — все ресурсы работают нормально
- `Progressing` — ресурсы обновляются (Deployment rolling update)
- `Degraded` — ресурсы в плохом состоянии (Pod CrashLoopBackOff)
- `Suspended` — ресурс приостановлен (CronJob suspended)
- `Missing` — ресурс отсутствует в кластере
- `Unknown` — состояние здоровья не определено

**Operation State** — статус последней операции синхронизации:
- `Running` — синхронизация выполняется
- `Succeeded` — синхронизация успешна
- `Failed` — синхронизация завершилась с ошибкой
- `Error` — внутренняя ошибка `ArgoCD`

## Q6. Что такое Sync стратегия в ArgoCD? Чем отличается manual от automated?

**Manual sync** (по умолчанию): `ArgoCD` обнаруживает расхождения (OutOfSync), но НЕ применяет изменения автоматически. Синхронизацию инициирует пользователь через UI, CLI или API.

**Automated sync**: `ArgoCD` автоматически применяет изменения при обнаружении расхождений.

```yaml
syncPolicy:
  automated:
    prune: false    # НЕ удалять ресурсы, которых нет в Git (осторожно!)
    selfHeal: false # НЕ исправлять ручные изменения
```

Параметры автоматической синхронизации:
- **`prune: true`** — удалять из кластера ресурсы, которые были удалены из Git. По умолчанию `false` — безопасная настройка, предотвращающая случайное удаление
- **`selfHeal: true`** — при обнаружении drift (ручного изменения через `kubectl`) автоматически откатывать до состояния в Git. Без этого параметра ручные изменения будут существовать до следующего коммита в Git

## Q7. Что такое prune и selfHeal в ArgoCD? В чём риски включения prune?

**`prune`** — флаг, разрешающий `ArgoCD` удалять ресурсы из кластера, если они были удалены из Git-репозитория. Без `prune: true` удалённые из Git ресурсы остаются в кластере, и `Application` будет в статусе `OutOfSync`.

Риски `prune: true`:
- Случайное удаление ресурсов при опечатке в пути `path` или `targetRevision`
- Удаление ресурсов, созданных вручную (migrations, debug объекты)
- Проблемы при переименовании ресурсов (удаление старого + создание нового)

**`selfHeal`** — флаг, разрешающий `ArgoCD` автоматически исправлять изменения, внесённые в кластер в обход Git. Полезен для обеспечения соответствия prod-кластера только тому, что описано в Git.

Риски `selfHeal: true`:
- Откат экстренных hotfix-ов, внесённых через `kubectl`
- Конфликт с HPA (Horizontal Pod Autoscaler), изменяющим `spec.replicas`

Решение для HPA: использовать `ignoreDifferences` для поля `spec.replicas`.

## Q8. Что такое Sync Waves в ArgoCD? Как они работают? (!)

**Sync Waves** — механизм управления порядком применения ресурсов во время синхронизации. Каждому ресурсу назначается числовой вес (wave), и `ArgoCD` применяет ресурсы в порядке возрастания wave, ожидая, пока ресурсы текущей волны не станут `Healthy`, прежде чем перейти к следующей.

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

Типичный порядок волн:
- Wave -1: `Namespace`, `CRD`, `PersistentVolumeClaim`
- Wave 0: `ConfigMap`, `Secret` (значение по умолчанию)
- Wave 1: `Job` для миграций БД
- Wave 2: основные `Deployment`, `StatefulSet`
- Wave 3: `Ingress`, `HorizontalPodAutoscaler`

## Q9. Что такое Sync Hooks в ArgoCD? Опишите фазы.

**Sync Hooks** — ресурсы (обычно `Job` или `Workflow`), выполняемые в определённые фазы синхронизации. Задаются аннотацией `argocd.argoproj.io/hook`.

Фазы хуков:
- **`PreSync`** — до применения манифестов. Используется для миграций БД, проверок pre-condition
- **`Sync`** — вместе с основными ресурсами (обычно не используется)
- **`PostSync`** — после успешной синхронизации. Используется для smoke-тестов, уведомлений, прогрева кеша
- **`SyncFail`** — при неудачной синхронизации. Используется для оповещений, rollback логики
- **`Skip`** — ресурс пропускается при синхронизации

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

**Hook Delete Policy** управляет удалением хука после выполнения:
- `HookSucceeded` — удалять при успехе
- `HookFailed` — удалять при неудаче
- `BeforeHookCreation` — удалять перед созданием нового хука (по умолчанию)

## Q10. Как ArgoCD определяет источник манифестов? Какие инструменты поддерживаются?

`ArgoCD` поддерживает несколько источников (`source tools`) для рендеринга манифестов:

- **Helm** — чарты из Git или Helm registry. Поддерживает values-файлы, override параметры
- **Kustomize** — kustomization.yaml с overlays. Поддерживает `images` override для тегов
- **Jsonnet** — jsonnet/libsonnet файлы
- **Plain YAML/JSON** — обычные Kubernetes манифесты без шаблонизации
- **Config Management Plugins (CMP)** — кастомные плагины (Helm с дополнительными инструментами, Vault agent, cdk8s и т.д.)

`ArgoCD` автоматически определяет тип по наличию файлов:
- `Chart.yaml` → Helm
- `kustomization.yaml` → Kustomize
- `*.jsonnet` → Jsonnet
- Иначе → plain YAML

## Q11. Что такое ApplicationSet? Зачем он нужен?

**`ApplicationSet`** — контроллер и CRD, позволяющий генерировать множество `Application` объектов по единому шаблону. Решает проблему управления большим количеством похожих приложений (multi-cluster, multi-env, multi-tenant).

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

Преимущества: единая точка управления, исключение дублирования, автоматическое создание приложений при добавлении нового кластера/среды.

## Q12. Какие generators поддерживает ApplicationSet? (!)

**List generator** — статический список элементов с произвольными параметрами:
```yaml
generators:
  - list:
      elements:
        - env: dev
          replica_count: "1"
        - env: prod
          replica_count: "3"
```

**Cluster generator** — генерирует `Application` для каждого зарегистрированного в `ArgoCD` кластера:
```yaml
generators:
  - clusters:
      selector:
        matchLabels:
          environment: production
```

**Git generator** — обходит директории или файлы в Git-репозитории:
```yaml
generators:
  - git:
      repoURL: https://github.com/org/repo
      revision: HEAD
      directories:
        - path: apps/*   # одно Application на каждую директорию
```

**Matrix generator** — декартово произведение двух генераторов:
```yaml
generators:
  - matrix:
      generators:
        - clusters: {}       # все кластеры
        - git:
            directories: [{path: "apps/*"}]  # все директории
```

**Merge generator** — объединяет параметры нескольких генераторов по ключу.

**SCM Provider generator** — обходит репозитории в GitHub/GitLab организации.

**Pull Request generator** — создаёт ephemeral-окружения для каждого PR в репозитории.

## Q13. Что такое паттерн App of Apps в ArgoCD?

**App of Apps** — паттерн, при котором один `Application` ("root app") управляет другими `Application` объектами. Root app указывает на директорию с YAML-манифестами других приложений.

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

Паттерн позволяет единым синхом развернуть всю платформу, управлять приложениями декларативно, использовать разные sync-стратегии для разных компонентов. В современных проектах `ApplicationSet` часто предпочтительнее App of Apps, так как не требует дублирования Application-манифестов.

## Q14. Как работает health check в ArgoCD? Как написать кастомный Lua скрипт?

`ArgoCD` поставляется со встроенными health checks для стандартных Kubernetes ресурсов (`Deployment`, `StatefulSet`, `DaemonSet`, `Service`, `Ingress` и т.д.).

Для кастомных CRD можно написать **Lua-скрипт** в конфигурации `ArgoCD`:

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

Возможные возвращаемые статусы из Lua: `Healthy`, `Progressing`, `Degraded`, `Suspended`, `Missing`.

## Q15. Что такое RBAC в ArgoCD? Как настроить права доступа? (!)

`ArgoCD` использует двухуровневую систему авторизации:

**AppProject** — ресурс, ограничивающий, какие репозитории, кластеры и namespace может использовать приложение:

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

**RBAC политики** в `argocd-rbac-cm` ConfigMap:
```
# Формат: p, <role/user>, <resource>, <action>, <object>, <effect>
p, role:developer, applications, get, */*, allow
p, role:developer, applications, sync, dev-*/*, allow
p, role:developer, applications, sync, prod-*/*, deny

g, org:backend-team, role:developer
```

Встроенные роли: `role:readonly`, `role:admin`.

## Q16. Как ArgoCD поддерживает multi-cluster деплой?

`ArgoCD` устанавливается в один "управляющий" кластер и может управлять множеством целевых кластеров.

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

`ArgoCD` использует `ServiceAccount` с необходимыми RBAC-правами в целевом кластере. Для Hub-and-Spoke топологии рекомендуется использовать `ArgoCD` с `ApplicationSet` и `Cluster` generator для автоматического деплоя на все зарегистрированные кластеры.

## Q17. Какие подходы к управлению секретами используются в ArgoCD? (!)

`ArgoCD` не хранит секреты — это принципиальное архитектурное решение. Несколько подходов:

**1. Sealed Secrets (Bitnami)** — шифрование секретов прямо в Git:
```bash
kubeseal --format yaml < secret.yaml > sealed-secret.yaml
# sealed-secret.yaml безопасно коммитить в Git
```
`SealedSecret` CRD расшифровывается только контроллером в кластере.

**2. External Secrets Operator (ESO)** — синхронизация из внешних хранилищ (Vault, AWS SSM, GCP Secret Manager):
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

**3. ArgoCD Vault Plugin (AVP)** — Config Management Plugin, подставляющий значения из Vault в манифесты во время рендеринга:
```yaml
# В манифесте используются placeholders:
apiVersion: v1
kind: Secret
stringData:
  password: <path:secret/data/prod/db#password>
```

**4. SOPS** — шифрование YAML/JSON файлов с ключами из age, PGP, AWS KMS. Интегрируется через `ArgoCD` CMP.

Рекомендуется ESO или Sealed Secrets как наиболее зрелые решения.

## Q18. Что такое ArgoCD Image Updater?

**`ArgoCD Image Updater`** — дополнение к `ArgoCD`, автоматически обновляющее теги образов в `Application` при появлении новых версий в container registry.

Работает в двух режимах:
- **`argocd` write-back** — обновляет параметры `Application` объекта напрямую
- **`git` write-back** — коммитит изменение тега в Git-репозиторий (настоящий GitOps)

```yaml
# Аннотации на Application объекте
annotations:
  argocd-image-updater.argoproj.io/image-list: myapp=registry.example.com/myapp
  argocd-image-updater.argoproj.io/myapp.update-strategy: semver
  argocd-image-updater.argoproj.io/myapp.allow-tags: ">=1.0.0"
  argocd-image-updater.argoproj.io/write-back-method: git
  argocd-image-updater.argoproj.io/git-branch: main
```

Стратегии обновления: `semver` (семантическое версионирование), `latest` (последний тег), `name` (алфавитный порядок), `digest` (по digest образа).

## Q19. Как выполнить rollback в ArgoCD?

`ArgoCD` хранит историю деплоев (по умолчанию 10 последних ревизий, настраивается через `app.kubernetes.io/revision`).

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

Важно: rollback в `ArgoCD` ≠ rollback в Git. `ArgoCD` деплоит манифесты из конкретной Git-ревизии, но сам Git при этом не меняется. При следующем auto-sync приложение снова обновится до HEAD.

Правильный GitOps rollback: `git revert` + push → `ArgoCD` автоматически применит изменение.

## Q20. Как организовать разделение CI и CD в GitOps pipeline с ArgoCD? (!)

Классический GitOps pipeline разделяет app-репозиторий и config-репозиторий:

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

Процесс:
1. Разработчик делает `push` в `app-repo`
2. CI (GitHub Actions, GitLab CI) собирает образ, пушит в registry с новым тегом
3. CI обновляет тег в `config-repo` (`kustomize edit set image` или `Image Updater`)
4. `ArgoCD` обнаруживает изменение в `config-repo` и синхронизирует кластер

Разделение репозиториев обеспечивает: разные права доступа к коду и конфигурации, независимые ревью app-кода и инфраструктурных изменений, чистую историю деплоев в `config-repo`.

## Q21. Как ArgoCD обнаруживает изменения в Git репозитории?

`ArgoCD` поддерживает два режима обнаружения изменений:

**Polling** (по умолчанию): `Repo Server` опрашивает Git-репозиторий каждые 3 минуты (настраивается параметром `timeout.reconciliation` в `argocd-cm`).

**Webhooks** (рекомендуется): Git-провайдер (GitHub, GitLab, Bitbucket) отправляет webhook при push. Значительно ускоряет реакцию на изменения (секунды вместо минут).

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

`ArgoCD Notifications` — компонент для отправки уведомлений о событиях приложений.

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

`ignoreDifferences` — список полей в ресурсах, расхождения в которых `ArgoCD` будет игнорировать при сравнении desired и live state.

Типичные случаи использования:

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

Поддерживаются два формата: `jsonPointers` (RFC 6901) и `jqPathExpressions` (jq-синтаксис).

## Q24. Как ArgoCD сравнивается с Flux CD? (!)

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

`ArgoCD` поддерживает несколько методов аутентификации:

**Встроенный Dex** — OpenID Connect провайдер, поддерживающий LDAP, GitHub, GitLab, Google, SAML, OIDC как upstream.

Настройка GitHub OAuth:
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

**Внешний OIDC** (без Dex): прямая интеграция с Keycloak, Okta, Azure AD:
```yaml
data:
  oidc.config: |
    name: Keycloak
    issuer: https://keycloak.example.com/realms/myrealm
    clientID: argocd
    clientSecret: $oidc.keycloak.clientSecret
    requestedScopes: [openid, profile, email, groups]
```

После аутентификации группы пользователя маппятся на роли через RBAC политики.

## Q26. Что такое syncOptions в ArgoCD? Опишите основные опции.

`syncOptions` — список флагов, управляющих поведением синхронизации:

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

- **`ServerSideApply=true`** — рекомендуется для больших ресурсов и работы с CRD, использует field ownership
- **`ApplyOutOfSyncOnly=true`** — ускоряет синхронизацию, не обрабатывая уже синхронизированные ресурсы
- **`PruneLast=true`** — безопаснее при обновлениях, сначала создаёт новые ресурсы

## Q27. Как отлаживать проблемы синхронизации в ArgoCD?

Типичные проблемы и методы диагностики:

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

`ArgoCD` 2.6+ поддерживает **multi-source** `Application` — один `Application` может ссылаться на несколько источников:

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

Типичный use case: Helm-чарт из одного репозитория + values-файлы из другого репозитория. Это позволяет версионировать чарты и конфигурацию независимо.

## Q29. Как настроить resource exclusions в ArgoCD?

`Resource Exclusions` — глобальная настройка для исключения определённых ресурсов из управления `ArgoCD`:

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

Resource Inclusions — наоборот, явный whitelist ресурсов (всё остальное игнорируется):
```yaml
data:
  resource.inclusions: |
    - apiGroups: ["*"]
      kinds: ["Deployment", "Service", "ConfigMap"]
      clusters: ["*"]
```

Исключение `Event` ресурсов снижает нагрузку на `ArgoCD` и Redis.

## Q30. Как работает ArgoCD в высоконагруженных окружениях? Как масштабировать?

Для production-окружений с большим количеством `Application` объектов:

**Шардирование Application Controller:**
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

`ArgoCD` 2.9+ поддерживает автоматическое шардирование (`dynamic` шардирование).

**Настройки производительности:**
```yaml
# argocd-cm
data:
  timeout.reconciliation: 180s      # частота опроса Git (увеличить при нагрузке)
  timeout.hard.reconciliation: 0s   # отключить принудительный reconcile
  app.sync.concurrency: 20          # параллельные синхронизации
  resource.comparisons.cached.duration: 10s
```

**Redis кластер** для высокой доступности: `argocd-redis-ha` (встроенный) или внешний Redis Sentinel/Cluster.

**Replica для API Server и Repo Server**: stateless компоненты, легко масштабируются горизонтально.

## Q31. Что такое Argo Rollouts? Как он интегрируется с ArgoCD?

**`Argo Rollouts`** — Kubernetes контроллер для продвинутых стратегий деплоя (`Canary`, `Blue/Green`) с поддержкой анализа метрик и автоматического rollback.

Интеграция с `ArgoCD`:
1. `ArgoCD` деплоит `Rollout` CRD вместо стандартного `Deployment`
2. `ArgoCD` умеет отображать health status `Rollout` объектов
3. `ArgoCD` может управлять `Rollout` операциями через API

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

`ArgoCD` + `Argo Rollouts` = полный GitOps pipeline с progressive delivery.

## Q32. Как настроить disaster recovery для ArgoCD? (!)

Компоненты `ArgoCD` которые нужно бэкапить:

**Критичные данные** (хранятся в Kubernetes secrets/configmaps):
```bash
# Экспорт всех Application, AppProject, репозиториев
argocd admin export > argocd-backup.yaml

# Импорт при восстановлении
argocd admin import - < argocd-backup.yaml
```

**Что содержит backup:**
- `Application` и `ApplicationSet` объекты
- `AppProject` объекты
- Репозитории (без паролей — только конфигурация)
- `Secret` с кластерными credentials (нужен отдельный backup)

**High Availability установка:**
```yaml
# Использовать HA-чарт:
helm install argocd argo/argo-cd \
  --set redis-ha.enabled=true \
  --set controller.replicas=1 \
  --set server.replicas=2 \
  --set repoServer.replicas=2 \
  --set applicationSet.replicas=2
```

**Автоматический backup** через CronJob + `argocd admin export` в S3/GCS.

## Q33. Как управлять конфигурацией самого ArgoCD в стиле GitOps?

Рекомендуемый подход — **bootstrapping**: `ArgoCD` управляет сам собой через `Application`:

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

При этом подходе любое изменение конфигурации `ArgoCD` (добавление репозитория, изменение RBAC) делается через Git PR → review → merge → auto-sync.

## Q34. Как защитить ArgoCD от несанкционированного доступа?

Основные практики безопасности:

**Сетевая изоляция:**
- `ArgoCD` API Server — только за VPN или bastion host
- Внутренние компоненты (`Redis`, `Repo Server`) не должны быть доступны снаружи
- `NetworkPolicy` для ограничения трафика между компонентами

**Минимальные привилегии:**
- Использовать `AppProject` для ограничения доступа к namespace/кластерам
- Роль `role:readonly` для большинства пользователей
- Отдельные `ServiceAccount` в целевых кластерах с минимальными RBAC

**Защита секретов:**
```bash
# Ротация admin пароля
argocd admin initial-password -n argocd  # только при первоначальной настройке
argocd account update-password           # изменить пароль
```

**Аудит**: `ArgoCD` логирует все операции. Интегрировать с SIEM через audit log.

**TLS**: обязательно использовать TLS для API Server, не отключать certificate verification.

## Q35. Опишите типичный workflow деплоя с ArgoCD на production.

Пример workflow для команды с GitOps:

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

Ключевые принципы: всё через Git PR, прод-деплои требуют approve, история деплоев = история коммитов, rollback = `git revert`.

---

## Q36. Как ArgoCD интегрируется с Sealed Secrets и External Secrets Operator?

Хранить секреты в Git напрямую нельзя — GitOps требует альтернативных подходов.

### Sealed Secrets (Bitnami)

`SealedSecret` — CRD, зашифрованный открытым ключом кластера. Только контроллер внутри кластера может расшифровать:

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

**ArgoCD** применяет `SealedSecret` как обычный ресурс, контроллер расшифровывает его и создаёт обычный `Secret`.

**Ограничение:** при ротации ключей кластера нужно перешифровать все секреты.

### External Secrets Operator (ESO)

**ESO** — синхронизирует секреты из внешних хранилищ (Vault, AWS Secrets Manager, GCP Secret Manager) в Kubernetes Secrets:

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

**ArgoCD + ESO:** в Git хранятся только `ExternalSecret` CR (не содержат секретов), ESO читает реальные значения из Vault/AWS и создаёт Kubernetes Secrets.

| Подход | Хранилище | Сложность | Ротация |
|--------|-----------|-----------|---------|
| Sealed Secrets | Git (зашифр.) | Низкая | Ручная |
| ESO + Vault | Vault/AWS/GCP | Средняя | Автоматическая |
| ArgoCD Vault Plugin | Vault | Высокая | При sync |

---

## Q37. Что такое ArgoCD Image Updater и как он работает?

**ArgoCD Image Updater** — компонент, автоматически обновляющий теги образов в GitOps-репозитории при появлении новых образов в container registry.

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

- **`git`** — Image Updater коммитит обновление тега прямо в репозиторий → ArgoCD подхватывает изменение
- **`argocd`** — обновляет параметр Application через ArgoCD API (не коммитит в Git, менее GitOps-совместимо)

**Типичный CI/CD flow с Image Updater:**
```
CI build → push image:v1.2.3 → Image Updater обнаруживает → 
коммит в config-repo → ArgoCD sync → деплой v1.2.3
```

---

## Q38. Как ArgoCD работает в multi-cluster окружении — hub-spoke модель?

**Hub-spoke** — архитектура, где один ArgoCD управляет несколькими кластерами:

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
- Использовать `AppProject` для разграничения доступа к кластерам
- Один ArgoCD instance может управлять 100+ кластерами
- Для очень большого числа кластеров — `ApplicationSet` + `ClusterGenerator`

---

## Q39. Как ArgoCD работает с Kustomize overlays?

**Kustomize** — нативный инструмент кастомизации Kubernetes манифестов без шаблонизации. ArgoCD поддерживает его из коробки.

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

**ArgoCD + Kustomize best practices:**
- `base/` — общие ресурсы, без env-специфики
- `overlays/<env>/kustomization.yaml` — только патчи и overrides
- Image tags переопределяются через `kustomize.images` (не hardcode в base)
- Не использовать `vars` (deprecated) — использовать `replacements`

---

## Q40. Как устроен RBAC в ArgoCD — Projects, AppProject, роли?

**ArgoCD RBAC** строится на двух уровнях: **AppProject** (изоляция) и **RBAC политики** (разрешения).

### AppProject — изоляция приложений

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

### RBAC политики (argocd-rbac-cm)

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

**ArgoCD Notifications** — компонент для отправки уведомлений о событиях (sync, health, deploy).

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
