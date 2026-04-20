---
title: "Kubernetes: основы"
description: "Кратко: сравнение с Mesos, Java-клиент, paging/async/watches, namespaces/selectors, CRUD ресурсов, Deployment vs StatefulSet/ReplicaSet, kind/k3s/minikube/Knative, jobs/cron, ingress vs LB, PV/PVC, сервисы, дамп heap из pod."
tags:
  - platform
  - containers
  - kubernetes-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kubernetes: основы

Кратко: сравнение с **Mesos**, **Java**-клиент, **paging**/**async**/**watches**, **namespaces**/**selectors**, **CRUD** ресурсов, **Deployment** vs **StatefulSet**/**ReplicaSet**, **kind**/**k3s**/**minikube**/**Knative**, **jobs**/**cron**, **ingress** vs `LB`, `PV`/**PVC**, сервисы, дамп **heap** из **pod**.

> **Примечание:** материал по `Kubernetes` поддерживается напрямую в этом документе; дополнительные примеры и пояснения переносятся в рабочем порядке при следующем тематическом обновлении.

## Полезные ссылки

### Официальная документация

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Kubernetes API Reference](https://kubernetes.io/docs/reference/kubernetes-api/)

### Обучающие материалы

- [Kubernetes Tutorial](https://kubernetes.io/docs/tutorials/)



### См. также
- [[containerization-overview|Containerization — обзор]]
## Содержание

- [Mesos vs Kubernetes](#mesos-vs-kubernetes)
- [Java-клиент: введение](#java-клиент-введение)
- [Пейджинг и асинхронные вызовы](#пейджинг-и-асинхронные-вызовы)
- [Watch API](#watch-api)
- [Namespaces и selectors](#namespaces-и-selectors)
- [CRUD ресурсов через API](#crud-ресурсов-через-api)
- [Deployment vs StatefulSet](#deployment-vs-statefulset)
- [Access Controller](#access-controller)
- [kind](#kind)
- [Knative (serverless)](#knative-serverless)
- [Cron/Job](#cronjob)
- [Ingress vs LoadBalancer](#ingress-vs-loadbalancer)
- [k3s](#k3s)
- [Heap dump из pod](#heap-dump-из-pod)
- [Deployment vs ReplicaSet](#deployment-vs-replicaset)
- [Pod vs контейнер](#pod-vs-контейнер)
- [Легковесные дистрибутивы](#легковесные-дистрибутивы)
- [minikube](#minikube)
- [Декодирование секретов](#декодирование-секретов)
- [PV vs PVC](#pv-vs-pvc)
- [Типы сервисов: ClusterIP/NodePort/LB](#типы-сервисов-clusteripnodeportlb)
- [targetPort vs port](#targetport-vs-port)
- [Список подов и узлов](#список-подов-и-узлов)
- [Логи пода](#логи-пода)
- [Переключение namespace](#переключение-namespace)
- [Получение YAML объекта](#получение-yaml-объекта)
- [Reload image](#reload-image)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также)

## Mesos vs Kubernetes

**Apache Mesos** и **Kubernetes** — это две популярные системы оркестрации контейнеров, используемые для автоматизации развертывания, масштабирования и управления контейнерными приложениями. Хотя они имеют схожие цели, они различаются по архитектуре и подходам.

**Apache `Mesos`:**

**Apache Mesos** — это распределенная система управления кластером, которая обеспечивает эффективное изоляцию и совместное использование ресурсов для распределенных приложений.

**Характеристики `Mesos`:**

- Двухуровневая архитектура планировщика
- Поддержка различных фреймворков (Marathon, `Chronos`, Aurora)
- Эффективное использование ресурсов
- Масштабируемость на десятки тысяч узлов

**Kubernetes:**

**Kubernetes** — это система оркестрации контейнеров с открытым исходным кодом для автоматизации развертывания, масштабирования и управления контейнерными приложениями.

**Характеристики `Kubernetes`:**

- Единообразная архитектура **API**
- Встроенная поддержка контейнеров
- Автоматическое масштабирование и самовосстановление
- Обширная экосистема инструментов и плагинов

**Сравнение:**

| Характеристика | **Apache Mesos** | **Kubernetes** |
|----------------|--------------|------------|
| **Архитектура** | Двухуровневая | Единообразная **API** |
| **Фокус** | Управление ресурсами | Оркестрация контейнеров |
| **Сложность** | Высокая | Средняя |
| **Поддержка** | **Apache Foundation** | **CNCF** |
| **Сообщество** | Меньше | Больше |

**Рекомендации:**

- Используйте **Mesos** для управления большими кластерами с разнообразными рабочими нагрузками
- Используйте **Kubernetes** для оркестрации контейнеров и современной разработки
- **Kubernetes** более популярен и имеет больше поддержки в экосистеме

## Java-клиент: введение

**Kubernetes** предоставляет официальный **Java**-клиент для взаимодействия с **API Kubernetes** из **Java**-приложений. Это позволяет программно управлять ресурсами **Kubernetes**.

**Зависимости:**

Для использования **Java**-клиента **Kubernetes** необходимо добавить зависимости в `pom.xml`.

Ниже — **Maven**-зависимость **Java**-клиента **Kubernetes** (XML).
```xml
<!-- Зависимость Java-клиента Kubernetes (io.kubernetes) для Maven -->
<dependency>
    <groupId>io.kubernetes</groupId>
    <artifactId>client-java</artifactId>
    <version>17.0.0</version>
</dependency>
```

**Базовая настройка:**

```java
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;

// Загрузка конфигурации из kubeconfig
ApiClient client = Config.defaultClient();
Configuration.setDefaultApiClient(client);

// Создание API клиента
CoreV1Api api = new CoreV1Api();
```

**Пример использования:**

```java
// Получение списка подов
V1PodList podList = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
for (V1Pod item : podList.getItems()) {
    System.out.println(item.getMetadata().getName());
}
```

**Рекомендации:**

- Используйте официальный **Java**-клиент для интеграции с **Kubernetes**
- Настраивайте правильную аутентификацию и авторизацию
- Обрабатывайте ошибки и исключения
- Используйте **async API** для лучшей производительности

## Пейджинг и асинхронные вызовы

При работе с большими наборами ресурсов в **Kubernetes** важно использовать пейджинг и асинхронные вызовы для эффективного получения данных.

**Пейджинг:**

**Kubernetes API** поддерживает пейджинг для больших наборов ресурсов. Используйте параметры `limit` и `continue`:**

```java
String continueToken = null;
do {
    V1PodList podList = api.listPodForAllNamespaces(
        null, null, null, continueToken, 100, null, null, null, null, null
    );

    for (V1Pod pod : podList.getItems()) {
        // Обработка пода
    }

    continueToken = podList.getMetadata().getContinue();
} while (continueToken != null);
```

**Асинхронные вызовы:**

**Используйте **async API** для неблокирующих операций:**

```java
ApiCallback<V1PodList> callback = new ApiCallback<V1PodList>() {
    @Override
    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
        // Обработка ошибки
    }

    @Override
    public void onSuccess(V1PodList result, int statusCode, Map<String, List<String>> responseHeaders) {
        // Обработка результата
    }

    @Override
    public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {}

    @Override
    public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {}
};

api.listPodForAllNamespacesAsync(null, null, null, null, null, null, null, null, null, null, callback);
```

**Рекомендации:**

- Используйте пейджинг для больших наборов ресурсов
- Используйте **async API** для неблокирующих операций
- Настраивайте разумные лимиты для пейджинга
- Обрабатывайте токены продолжения правильно

## Watch API

**Watch API** позволяет получать уведомления об изменениях ресурсов в реальном времени, без необходимости постоянного опроса **API**.

**Использование `Watch API`:**

```java
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Watch;

CoreV1Api api = new CoreV1Api();
Watch<V1Pod> watch = Watch.createWatch(
    client,
    api.listPodForAllNamespacesCall(null, null, null, null, null, null, null, null, null, null, true, null),
    new TypeToken<Watch.Response<V1Pod>>() {}.getType()
);

try {
    for (Watch.Response<V1Pod> item : watch) {
        V1Pod pod = item.object;
        String eventType = item.type;
        System.out.println("Event: " + eventType + " Pod: " + pod.getMetadata().getName());
    }
} finally {
    watch.close();
}
```

**Типы событий:**

- `ADDED` — ресурс добавлен
- `MODIFIED` — ресурс изменен
- `DELETED` — ресурс удален
- `ERROR` — произошла ошибка

**Рекомендации:**

- Используйте **Watch API** для мониторинга изменений в реальном времени
- Обрабатывайте все типы событий
- Правильно закрывайте **Watch** соединения
- Обрабатывайте ошибки и переподключения

## Namespaces и selectors

В **Kubernetes namespaces** и **selectors** используются для организации и фильтрации ресурсов.

**Namespaces:**

**Namespace** — это виртуальный кластер внутри физического кластера, который позволяет изолировать ресурсы.

**Список namespaces:**

```bash
$ kubectl get namespaces

NAME              STATUS   AGE
default           Active   3d
kube-system       Active   3d
kube-public       Active   3d
kube-node-lease   Active   3d
```

**Создание namespace:**

```bash
$ kubectl create namespace my-namespace
```

**Или через **YAML**:**

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: my-namespace
```

**Selectors:**

**Selectors** используются для выбора ресурсов на основе меток (labels).

**Пример использования labels:**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-pod
  labels:
    app: my-app
    environment: production
spec:
  containers:
    - name: my-container
      image: nginx
```

**Фильтрация по labels:**

```bash
$ kubectl get pods -l app=my-app
$ kubectl get pods -l environment=production
$ kubectl get pods -l 'app=my-app,environment=production'
```

**Использование в `Java`-клиенте:**

```java
String labelSelector = "app=my-app";
V1PodList podList = api.listNamespacedPod(
    "default", null, null, null, null, labelSelector, null, null, null, null, null
);
```

**Рекомендации:**

- Используйте **namespaces** для изоляции ресурсов
- Применяйте **consistent naming conventions** для **labels**
- Используйте **selectors** для фильтрации ресурсов
- Документируйте используемые **labels** и их значения

## CRUD ресурсов через API

**Kubernetes API** позволяет выполнять **CRUD** операции (Create, `Read`, `Update`, Delete) над всеми ресурсами.

**Create (Создание):**

```java
V1Pod pod = new V1Pod()
    .metadata(new V1ObjectMeta().name("my-pod"))
    .spec(new V1PodSpec()
        .containers(Arrays.asList(
            new V1Container()
                .name("my-container")
                .image("nginx:latest")
        ))
    );

V1Pod createdPod = api.createNamespacedPod("default", pod, null, null, null, null);
```

**Read (Чтение):**

```java
// Получение одного пода
V1Pod pod = api.readNamespacedPod("my-pod", "default", null, null, null);

// Список всех подов в namespace
V1PodList podList = api.listNamespacedPod("default", null, null, null, null, null, null, null, null, null, null);
```

**Update (Обновление):**

```java
// Получить текущий pod
V1Pod pod = api.readNamespacedPod("my-pod", "default", null, null, null);

// Изменить
pod.getSpec().getContainers().get(0).setImage("nginx:1.21");

// Обновить
V1Pod updatedPod = api.replaceNamespacedPod("my-pod", "default", pod, null, null, null, null);
```

**Delete (Удаление):**

```java
// Удаление пода
V1Status status = api.deleteNamespacedPod("my-pod", "default", null, null, null, null, null, null);

// Удаление с grace period
V1DeleteOptions deleteOptions = new V1DeleteOptions().gracePeriodSeconds(30L);
V1Status status = api.deleteNamespacedPod("my-pod", "default", null, null, null, null, null, deleteOptions);
```

**Рекомендации:**

- Используйте правильные методы **API** для операций
- Обрабатывайте ошибки и исключения
- Используйте патчи для частичных обновлений
- Настраивайте **grace period** для удаления ресурсов

## Deployment vs StatefulSet

**Kubernetes** (K8s) — это система оркестрации контейнеров с открытым исходным кодом. Это позволяет нам автоматизировать развертывание, масштабирование и управление контейнерными приложениями.

**Deployment:**

**Deployment Kubernetes** предоставляет средства для управления набором модулей. Это может быть один или несколько запущенных контейнеров или группа дублирующих модулей, известная как **ReplicaSets**.

**Характеристики `Deployment`:**

- Используется для приложений без сохранения состояния (stateless)
- Поды взаимозаменяемы
- Имена подов уникальны, но не предсказуемы
- Использует **ReplicaSet** для управления подами
- Поддерживает **rolling update** и **rollback**

**Пример `Deployment`:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: web-app-deployment
spec:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 2
      maxUnavailable: 1
  selector:
    matchLabels:
      app: web-app
  replicas: 3
  template:
    metadata:
      labels:
        app: web-app
    spec:
      containers:
        - name: web-app
          image: nginx:latest
          ports:
            - containerPort: 80
```

**StatefulSet:**

**StatefulSet** предоставляет каждому поду два стабильных уникальных идентификатора: **Network Identity** и **Storage Identity**.

**Характеристики `StatefulSet`:**

- Используется для приложений с сохранением состояния (stateful)
- Поды не взаимозаменяемы
- Имена подов идут в последовательном порядке (web-0, `web-1`, web-2)
- Каждый под получает свой уникальный **PVC**
- Требует **Headless Service** для сетевой идентичности
- Не создает **ReplicaSet**

**Пример `StatefulSet`:**

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  serviceName: "nginx"
  selector:
    matchLabels:
      app: nginx
  replicas: 3
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
        - name: nginx
          image: nginx:latest
          volumeMounts:
            - name: www
              mountPath: /usr/share/nginx/html
  volumeClaimTemplates:
    - metadata:
        name: www
      spec:
        accessModes: [ "ReadWriteOnce" ]
        resources:
          requests:
            storage: 1Gi
```

**Headless `Service` для `StatefulSet`:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx
spec:
  clusterIP: None
  selector:
    app: nginx
  ports:
    - port: 80
      name: web
```

**Основные различия:**

| Характеристика | **Deployment** | **StatefulSet** |
|----------------|------------|-------------|
| **Тип приложения** | **Stateless** | **Stateful** |
| **Взаимозаменяемость подов** | Да | Нет |
| **Имена подов** | Уникальные, непредсказуемые | Последовательные (app-0, app-1) |
| **ReplicaSet** | Создает | Не создает |
| **Хранилище** | Общий **PVC** (если используется) | Уникальный **PVC** для каждого пода |
| **Сервис** | Обычный **Service** | **Headless Service** |
| **Rollback** | Поддерживается | Не поддерживается |
| **Масштабирование** | Мгновенное | Последовательное |
| **Использование** | Веб-приложения, **API** | Базы данных, очереди, кластеры |

**Рекомендации:**

- Используйте **Deployment** для **stateless** приложений
- Используйте **StatefulSet** для **stateful** приложений (БД, очереди)
- **StatefulSet** подходит для **Kafka**, **MySQL**, **Redis**, **ZooKeeper**
- **Deployment** подходит для веб-серверов, **API**, микросервисов
- Используйте правильный тип **Service** для каждого типа

## Access Controller

Поработав какое-то время с **Kubernetes**, мы скоро поймем, что здесь задействовано много шаблонного кода. Контроллеры доступа позволяют нам придерживаться принципа **DRY** и избежать повторяющегося кода.

**Что такое `Access Controller`?**

Контроллеры доступа — это механизм, используемый **Kubernetes** для предварительной обработки запросов **API** после их аутентификации, но до их выполнения.

**Типы контроллеров:**

1. **Mutating Admission Controllers** — могут изменять объекты перед их сохранением
2. **Validating Admission Controllers** — могут проверять объекты, но не могут их изменять

**Примеры встроенных контроллеров:**

- `NamespaceLifecycle` — управление жизненным циклом **namespace**
- `LimitRanger` — установка ограничений ресурсов
- `ResourceQuota` — управление квотами ресурсов
- `PodSecurityPolicy` — политики безопасности подов

**Создание собственного контроллера:**

**Для создания собственного контроллера доступа используется **Kubernetes Admission Webhooks**:**

1. Создайте веб-сервер, который обрабатывает запросы **AdmissionReview**
2. Настройте **ValidatingWebhookConfiguration** или **MutatingWebhookConfiguration**
3. Зарегистрируйте **webhook** в **Kubernetes**

**Рекомендации:**

- Используйте встроенные контроллеры когда возможно
- Создавайте **custom webhooks** для специфической логики
- Тестируйте **webhooks** тщательно перед продакшеном
- Используйте **validating webhooks** для проверки
- Используйте **mutating webhooks** для изменения объектов

## kind

При работе с **Kubernetes** нам не хватает инструмента, помогающего в локальной разработке — инструмента, который может запускать локальные кластеры **Kubernetes**, используя контейнеры **Docker** в качестве узлов.

**Что такое kind?**

**kind** (`Kubernetes in Docker`) — это инструмент для запуска локальных кластеров **Kubernetes** с использованием **Docker** контейнеров в качестве узлов.

**Установка kind:**

**macOS:**

```bash
brew install kind
```

**Linux/`Windows`:**

```bash
curl -Lo kind https://kind.sigs.k8s.io/dl/v0.20.0/kind-linux-amd64
chmod +x kind
sudo mv kind /usr/local/bin/
```

**Создание кластера:**

**Простой кластер:**

```bash
$ kind create cluster

Creating cluster "kind"...
✓ Ensuring node image (kindest/node:v1.27.3)
✓ Preparing nodes
✓ Writing configuration
✓ Starting control-plane
✓ Installing CNI
✓ Installing StorageClass
```

**Кластер с именем:**

```bash
$ kind create cluster --name my-cluster
```

**Кластер с конфигурацией:**

**Создайте файл `kind-config.yaml`:**

```yaml
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
name: my-cluster
nodes:
  - role: control-plane
    kubeadmConfigPatches:
      - |
        kind: InitConfiguration
        nodeRegistration:
          kubeletExtraArgs:
            node-labels: "ingress-ready=true"
    extraPortMappings:
      - containerPort: 80
        hostPort: 80
        protocol: TCP
      - containerPort: 443
        hostPort: 443
        protocol: TCP
```

**Создайте кластер:**

```bash
$ kind create cluster --config kind-config.yaml
```

**Работа с кластером:**

```bash
# Список кластеров
$ kind get clusters

# Получение информации о кластере
$ kubectl cluster-info --context kind-my-cluster

# Использование кластера
$ kubectl get nodes --context kind-my-cluster
```

**Удаление кластера:**

```bash
$ kind delete cluster --name my-cluster
```

**Установка `Ingress Controller`:**

```bash
$ kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
```

**Рекомендации:**

- Используйте **kind** для локальной разработки
- Настраивайте порты для доступа к сервисам
- Используйте конфигурационные файлы для сложных кластеров
- Регулярно обновляйте образы **kind**

## Knative (serverless)

**Knative** — это платформа с открытым исходным кодом для построения бессерверных приложений на **Kubernetes**. Она предоставляет абстракции для разработки современных, управляемых источниками и облачных приложений.

**Компоненты `Knative`:**

1. **Knative Serving** — для управления жизненным циклом серверных приложений
2. **Knative Eventing** — для управления событиями
3. **Knative Functions** — для разработки функций

**Knative `Serving`:**

**Knative Serving** позволяет развертывать серверные приложения с автоматическим масштабированием до нуля и обратно.

**Основные концепции:**

- **Service** — управляет полным жизненным циклом рабочей нагрузки
- **Revision** — неизменяемый снимок кода и конфигурации
- **Route** — сопоставляет входящий трафик с ревизиями
- **Configuration** — поддерживает желаемое состояние для развертывания

**Пример `Knative Service`:**

```yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: helloworld-go
spec:
  template:
    metadata:
      annotations:
        autoscaling.knative.dev/minScale: "1"
        autoscaling.knative.dev/maxScale: "10"
    spec:
      containers:
        - image: gcr.io/knative-samples/helloworld-go
          env:
            - name: TARGET
              value: "World"
```

**Автоматическое масштабирование:**

**Knative** автоматически масштабирует приложения:**
- **Scale to Zero** — когда нет трафика, поды удаляются
- **Auto-scaling** — поды создаются при поступлении трафика
- **Concurrency** — управление количеством запросов на под

**Knative `Eventing`:**

**Knative Eventing** позволяет связывать приложения через события.

**Рекомендации:**

- Используйте **Knative** для **serverless** приложений на **Kubernetes**
- Настраивайте правильные лимиты масштабирования
- Используйте метрики для мониторинга
- Оптимизируйте холодный старт для критических приложений

## Cron/Job

В **Kubernetes** мы можем запускать задания через планировщик используя **CronJob**. Это позволяет выполнять задачи по расписанию, аналогично **cron** в **Unix**-системах.

**CronJob:**

**CronJob** создает **Jobs** на основе расписания, написанного в формате **Cron**.

**Формат `Cron`:**

```text
МИНУТЫ ЧАСЫ ДЕНЬ_МЕСЯЦА МЕСЯЦ ДЕНЬ_НЕДЕЛИ
```

**Примеры:**

- `*/5 * * * *` — каждые 5 минут
- `0 */2 * * *` — каждые 2 часа
- `0 0 * * *` — каждый день в полночь
- `0 9 * * 1-5` — каждый рабочий день в 9:00

**Пример `CronJob`:**

```yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: backup-job
spec:
  schedule: "0 2 * * *"  # Каждый день в 2:00
  jobTemplate:
    spec:
      template:
        spec:
          containers:
            - name: backup
              image: backup:latest
              command:
                - /bin/sh
                - -c
                - ./backup.sh
          restartPolicy: OnFailure
  successfulJobsHistoryLimit: 3
  failedJobsHistoryLimit: 1
```

**Job:**

**Job** создает один или несколько подов и гарантирует, что указанное количество из них успешно завершится.

**Пример Job:**

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: batch-job
spec:
  completions: 5      # Выполнить 5 раз
  parallelism: 2      # Параллельно 2 пода
  template:
    spec:
      containers:
        - name: batch
          image: batch-processor:latest
          command:
            - /bin/sh
            - -c
            - ./process.sh
      restartPolicy: Never
```

**Параметры `CronJob`:**

- `schedule` — **cron**-выражение для расписания
- `successfulJobsHistoryLimit` — количество успешных **jobs** для хранения
- `failedJobsHistoryLimit` — количество неудачных **jobs** для хранения
- `startingDeadlineSeconds` — таймаут для запуска **job**

**Параметры Job:**

- `completions` — количество успешных завершений
- `parallelism` — количество параллельных подов
- `activeDeadlineSeconds` — максимальное время выполнения
- `backoffLimit` — количество повторных попыток

**Просмотр `CronJobs` и `Jobs`:**

```bash
# Список CronJobs
$ kubectl get cronjobs

# Список Jobs
$ kubectl get jobs

# Просмотр логов Job
$ kubectl logs job/batch-job
```

**Рекомендации:**

- Используйте **CronJob** для периодических задач
- Используйте **Job** для разовых задач
- Настраивайте правильные лимиты истории **jobs**
- Мониторьте выполнение **jobs**
- Обрабатывайте ошибки и таймауты

## Ingress vs LoadBalancer

В **Kubernetes Ingress** и **LoadBalancer** используются для предоставления внешнего доступа к сервисам, но они решают разные задачи.

**LoadBalancer:**

**LoadBalancer** — это тип **Service**, который создает внешний балансировщик нагрузки (если поддерживается облачным провайдером).

**Характеристики `LoadBalancer`:**

- Создает внешний балансировщик нагрузки
- Получает внешний `IP`-адрес
- Работает на уровне 4 (L4) — **TCP**/**UDP**
- Требует поддержку от облачного провайдера

**Пример `LoadBalancer`:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  type: LoadBalancer
  selector:
    app: my-app
  ports:
    - port: 80
      targetPort: 8080
```

**Ingress:**

**Ingress** — это объект **API**, который управляет внешним доступом к сервисам в кластере, обычно **HTTP**/**HTTPS**.

**Характеристики `Ingress`:**

- Работает на уровне 7 (L7) — **HTTP**/**HTTPS**
- Поддерживает маршрутизацию на основе путей и хостов
- Поддерживает **SSL**/**TLS termination**
- Требует **Ingress Controller**

**Пример `Ingress`:**

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: my-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
    - host: example.com
      http:
        paths:
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: api-service
                port:
                  number: 80
          - path: /
            pathType: Prefix
            backend:
              service:
                name: web-service
                port:
                  number: 80
  tls:
    - hosts:
        - example.com
      secretName: tls-secret
```

**Ingress `Controller`:**

**Для работы **Ingress** необходим **Ingress Controller**. Популярные контроллеры:**

- **NGINX Ingress Controller** — самый популярный
- **Traefik** — простой и легкий
- **HAProxy** — для высокой производительности
- **Istio Gateway** — часть **service mesh**

**Сравнение:**

| Характеристика | **LoadBalancer** | **Ingress** |
|----------------|--------------|---------|
| **Уровень** | **L4** (TCP/UDP) | **L7** (HTTP/HTTPS) |
| **Маршрутизация** | Простая | На основе путей/хостов |
| **SSL/TLS** | На уровне балансировщика | На уровне **Ingress** |
| **Стоимость** | За каждый **Service** | Один **LoadBalancer** на кластер |
| **Гибкость** | Ограниченная | Высокая |
| **Использование** | Для **TCP**/**UDP** | Для **HTTP**/**HTTPS** |

**Рекомендации:**

- Используйте **LoadBalancer** для **TCP**/**UDP** сервисов
- Используйте **Ingress** для **HTTP**/**HTTPS** сервисов
- Используйте **Ingress** для управления множеством сервисов через один `IP`
- Настраивайте **SSL**/**TLS** сертификаты правильно
- Выбирайте подходящий **Ingress Controller** для ваших потребностей

## k3s

Мы знаем **Kubernetes** (или K8s) как портативную платформу с открытым исходным кодом для управления контейнерными рабочими нагрузками и службами. Его мастерство связано с облачной автоматизацией, и его использование для локальной разработки или сред с ограниченными ресурсами еще менее применимо.

**Что такое k3s?**

**K3s** — это сертифицированный **CNCF** дистрибутив **Kubernetes** и проект **Sandbox**, разработанный для сред с низким уровнем ресурсов. **Rancher Labs** поддерживает **K3s**.

**Характеристики k3s:**

**Вот что делает **K3s** легким дистрибутивом:**

1. Упакован как один бинарный файл с минимальными внешними зависимостями
2. Низкие требования к оборудованию и объем памяти
3. Возможность работы как одиночного, так и высокодоступного сервера

**Требования:**

**K3** должен работать в ОС **Linux** с не менее чем `512` МБ ОЗУ (хотя рекомендуется 1 ГБ) и одним процессором.

**Установка k3s:**

**Основная команда установки проста:**

```bash
curl -sfL https://get.k3s.io | sh -
```

Это выполняет скрипт с **https**://**get.k3s.io**/ и запускает **K3s** как сервис на нашем хосте **Linux**.

**Доступ к кластеру:**

**По умолчанию K3s установит файл конфигурации в каталог `/etc/rancher/k3s/`. Настройте доступ:**

```bash
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml

# Или скопируйте в стандартное место
mkdir -p ~/.kube
sudo k3s kubectl config view --raw | tee ~/.kube/config
chmod 600 ~/.kube/config
```

**Проверка кластера:**

```bash
$ kubectl get nodes

NAME   STATUS   ROLES                  AGE   VERSION
node1  Ready    control-plane,master   4d3h  v1.25.6+k3s1
```

**Добавление узлов:**

**Чтобы добавить узлы в наш кластер:**

```bash
$ curl -sfL https://get.k3s.io | K3S_URL=https://<master-host>:6443 K3S_TOKEN=<token> sh -
```

**Токен хранится на **master** узле:**

```bash
$ cat /var/lib/rancher/k3s/server/node-token
```

**Развертывание приложения:**

```bash
# Создание deployment
$ kubectl create deployment nginx --image=nginx --port=80 --replicas=3

# Создание service
$ kubectl create service clusterip nginx --tcp=80:80

# Проверка подов
$ kubectl get pods
```

**Компоненты k3s:**

**K3s** включает в себя:
1. **Traefik** — как входной контроллер
2. **CoreDNS** — для управления **DNS**
3. **Local Path Provisioner** — для локального хранилища
4. **Helm** — для управления пакетами

**Рекомендации:**

- Используйте **k3s** для локальной разработки и тестирования
- Используйте **k3s** для **edge computing** и **IoT** устройств
- Настраивайте достаточное количество ресурсов (минимум 1 ГБ RAM)
- Регулярно обновляйте **k3s** для безопасности

## Heap dump из pod

В **Kubernetes** мы можем получить дамп кучи **Java** из модуля для анализа проблем с памятью и производительностью.

**Метод 1: Использование jmap внутри пода:**

**Если в поде установлен **JDK**, можно использовать **jmap**:**

```bash
# Получить PID процесса Java
$ kubectl exec my-pod -- ps aux | grep java

# Создать heap dump
$ kubectl exec my-pod -- jmap -dump:format=b,file=/tmp/heap.hprof <PID>

# Скопировать dump из пода
$ kubectl cp my-pod:/tmp/heap.hprof ./heap.hprof
```

**Метод 2: Использование jcmd:**

```bash
# Создать heap dump
$ kubectl exec my-pod -- jcmd <PID> GC.run_finalization
$ kubectl exec my-pod -- jcmd <PID> VM.run_finalization
$ kubectl exec my-pod -- jcmd <PID> GC.heap_dump /tmp/heap.hprof

# Скопировать dump
$ kubectl cp my-pod:/tmp/heap.hprof ./heap.hprof
```

**Метод 3: Через `JVM` параметры:**

**Настройте **JVM** для создания **heap dump** при **OutOfMemoryError**:**

```bash
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/tmp/heap.hprof
```

**Метод 4: Использование init container:**

**Создайте **init container** с **JDK** для создания **dump**:**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: debug-pod
spec:
  initContainers:
    - name: debug
      image: openjdk:11-jdk
      command:
        - /bin/sh
        - -c
        - |
          # Логика создания dump
      volumeMounts:
        - name: dump-volume
          mountPath: /tmp
  containers:
    - name: app
      image: my-app:latest
      volumeMounts:
        - name: dump-volume
          mountPath: /tmp
```

**Анализ heap dump:**

**После получения **heap dump** можно проанализировать его с помощью инструментов:**

- **Eclipse MAT** (`Memory Analyzer Tool`) — популярный инструмент
- **VisualVM** — встроенный в **JDK**
- **jhat** — командная строка анализа

**Рекомендации:**

- Настраивайте автоматическое создание **dump** при **OOM**
- Храните **dumps** в **persistent storage**
- Анализируйте **dumps** регулярно для выявления проблем
- Используйте правильные **JVM** параметры для мониторинга памяти
- Ограничивайте размер **heap dumps** для экономии места

## Deployment vs ReplicaSet

В **Kubernetes Deployment** и **ReplicaSet** тесно связаны, но выполняют разные роли в управлении подами.

**ReplicaSet:**

**ReplicaSet** обеспечивает, что определенное количество реплик пода работает в любое время.

**Характеристики `ReplicaSet`:**

- Управляет набором идентичных подов
- Обеспечивает заданное количество реплик
- Использует **selectors** для идентификации подов
- Создается автоматически **Deployment**'ом

**Пример `ReplicaSet`:**

```yaml
apiVersion: apps/v1
kind: ReplicaSet
metadata:
  name: nginx-replicaset
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
        - name: nginx
          image: nginx:latest
```

**Deployment:**

**Deployment** — это высокоуровневый объект, который управляет **ReplicaSet**'ами и обеспечивает декларативные обновления для подов.

**Характеристики `Deployment`:**

- Управляет **ReplicaSet**'ами
- Поддерживает **rolling updates** и **rollbacks**
- Хранит историю ревизий
- Обеспечивает стратегии развертывания

**Пример `Deployment`:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
        - name: nginx
          image: nginx:1.21
```

**Взаимосвязь:**

В **Kubernetes Deployment** с репликой 1 контроллер проверит, равно ли текущее состояние желаемому состоянию **ReplicaSet**, т.е. 1. Если текущее состояние равно 0, он создаст **ReplicaSet**. **ReplicaSet** дополнительно создаст поды.

Когда мы создаем развертывание **Kubernetes** с именем **web-app**, оно создаст **ReplicaSet** с именем **web-app**-<**replica-set-id**>. Эта реплика далее создаст под с именем **web-app**-<**replica-set-id**>-<**pod-id**>.

**Rolling `Update`:**

**При обновлении **Deployment**:**
1. Создается новый **ReplicaSet** с обновленной конфигурацией
2. Постепенно создаются новые поды
3. Старые поды постепенно удаляются
4. Старый **ReplicaSet** сохраняется для возможности **rollback**

**Откат (Rollback):**

```bash
# Просмотр истории ревизий
$ kubectl rollout history deployment/nginx-deployment

# Откат к предыдущей версии
$ kubectl rollout undo deployment/nginx-deployment

# Откат к конкретной ревизии
$ kubectl rollout undo deployment/nginx-deployment --to-revision=2
```

**Сравнение:**

| Характеристика | **ReplicaSet** | **Deployment** |
|----------------|------------|------------|
| **Уровень** | Низкоуровневый | Высокоуровневый |
| **Обновления** | Не поддерживает | **Rolling update** |
| **Rollback** | Нет | Да |
| **История** | Не хранит | Хранит ревизии |
| **Стратегии** | Нет | Да (RollingUpdate, Recreate) |
| **Использование** | Прямое использование редко | Рекомендуется |

**Рекомендации:**

- Используйте **Deployment** вместо прямого использования **ReplicaSet**
- Настраивайте правильные стратегии обновления
- Используйте **rollback** при проблемах
- Мониторьте процесс обновления
- Настраивайте **maxSurge** и **maxUnavailable** правильно

## Pod vs контейнер

В **Kubernetes Pod** и контейнер — это разные концепции, которые часто путают. Понимание различий важно для правильной работы с **Kubernetes**.

**Контейнер:**

Контейнер — это легковесная, изолированная единица выполнения приложения. Контейнер инкапсулирует приложение со всеми его зависимостями.

**Характеристики контейнера:**

- Изолированная среда выполнения
- Содержит приложение и зависимости
- Может быть запущен на различных платформах
- Основная единица в **Docker**

**Pod:**

**Pod** — это наименьшая единица развертывания в **Kubernetes**. **Pod** содержит один или несколько контейнеров, которые совместно используют сеть и хранилище.

**Характеристики Pod:**

- Может содержать один или несколько контейнеров
- Контейнеры в поде разделяют сетевой **namespace**
- Контейнеры в поде разделяют хранилище
- Контейнеры в поде запускаются и останавливаются вместе
- **Pod** — это единица масштабирования в **Kubernetes**

**Отношения:**

**Pod** — это обертка вокруг контейнеров. **Kubernetes** управляет подами, а не напрямую контейнерами.

**Один контейнер в поде:**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: simple-pod
spec:
  containers:
    - name: nginx
      image: nginx:latest
```

**Несколько контейнеров в поде:**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: multi-container-pod
spec:
  containers:
    - name: nginx
      image: nginx:latest
    - name: sidecar
      image: busybox:latest
      command: ['sh', '-c', 'sleep 3600']
```

**Общие ресурсы в поде:**

**Контейнеры в поде разделяют:**

1. **Сеть** — один `IP`-адрес, порты не могут конфликтовать
2. **Хранилище** — **volumes** монтируются в под и доступны всем контейнерам
3. **Имена** — локальный **hostname** для контейнеров

**Sidecar паттерн:**

**Часто используется паттерн **sidecar**, где основной контейнер работает вместе со вспомогательным:**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: app-with-logger
spec:
  containers:
    - name: app
      image: my-app:latest
      volumeMounts:
        - name: shared-logs
          mountPath: /var/log
    - name: logger
      image: fluentd:latest
      volumeMounts:
        - name: shared-logs
          mountPath: /var/log
  volumes:
    - name: shared-logs
      emptyDir: {}
```

**Сравнение:**

| Характеристика | Контейнер | **Pod** |
|----------------|-----------|-----|
| **Уровень** | Низкоуровневый | Высокоуровневый (Kubernetes) |
| **Масштабирование** | Индивидуально | По подам |
| **Сеть** | Собственный **namespace** | Общий для всех контейнеров |
| **Хранилище** | Собственное | Общее для всех контейнеров |
| **Жизненный цикл** | Независимый | Зависит от пода |
| **Использование** | **Docker**, **Container Runtime** | **Kubernetes** |

**Рекомендации:**

- Используйте один контейнер в поде для простых случаев
- Используйте несколько контейнеров для **sidecar** паттерна
- Не размещайте несколько независимых приложений в одном поде
- Используйте **init containers** для инициализации
- Понимайте, что под — это единица масштабирования

## Легковесные дистрибутивы

Легковесные дистрибутивы **Kubernetes** предназначены для использования в средах с ограниченными ресурсами, таких как **edge computing**, **IoT** устройства или локальная разработка.

**Популярные легковесные дистрибутивы:**

1. **k3s** — самый популярный легковесный дистрибутив
2. **k0s** — полностью автономный, без зависимостей
3. **MicroK8s** — от **Canonical**, простой в установке
4. **kind** — **Kubernetes** in **Docker**, для тестирования
5. **minikube** — для локальной разработки

**Характеристики легковесных дистрибутивов:**

- Минимальные требования к ресурсам
- Быстрая установка и запуск
- Упрощенная архитектура
- Оптимизированные для определенных случаев использования

**Сравнение:**

| Дистрибутив | Размер | **RAM** | **CPU** | Использование |
|-------------|--------|-----|-----|---------------|
| **k3s** | <100 `MB` | `512 MB`+ | 1 **core** | **Edge**, **IoT**, разработка |
| **k0s** | <100 `MB` | `512 MB`+ | 1 **core** | **Edge**, облако |
| **MicroK8s** | ~200 `MB` | 1 `GB`+ | 1 **core** | Разработка, **IoT** |
| **kind** | Зависит от образа | 2 `GB`+ | 2 **cores** | Тестирование, CI/CD |
| **minikube** | ~500 `MB` | 2 `GB`+ | 2 **cores** | Локальная разработка |

**Рекомендации:**

- Используйте **k3s** для **edge computing** и **IoT**
- Используйте **kind** для тестирования и CI/CD
- Используйте **minikube** для локальной разработки
- Выбирайте дистрибутив на основе ваших требований к ресурсам
- Проверяйте совместимость с вашими приложениями

## minikube

В этом руководстве мы научимся использовать **minikube** для быстрой настройки локального кластера **Kubernetes** на вашей машине.

**Что такое minikube?**

**minikube** — это инструмент, который позволяет запускать локальный кластер **Kubernetes** с одним узлом. Он предназначен для локальной разработки и тестирования.

**Установка minikube:**

**macOS:**

```bash
brew install minikube
```

**Linux:**

```bash
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
sudo install minikube-linux-amd64 /usr/local/bin/minikube
```

**Windows:**

```powershell
# Скачать minikube.exe и добавить в PATH
# Или использовать Chocolatey
choco install minikube
```

**Запуск minikube:**

**Базовая установка:**

```bash
$ minikube start

😄  minikube v1.31.1 on Darwin 13.5
✨  Automatically selected the docker driver
📦  Downloading Kubernetes v1.28.3 preload ...
    > preloaded-images-k8s-v18-v1...:  403 B / 403 B   [---------] 100.00% ? p/s 0s
    > gcr.io/k8s-minikube/kicbase...:  417.30 MiB / 417.30 MiB   [-----] 100.00% 4.93 MiB p/s 1m25s
    > k8s.gcr.io/kube-proxy:v1.28...:  37.92 MiB / 37.92 MiB   [-----------] 100.00% 98.43 MiB p/s 0s
    > k8s.gcr.io/kube-controller...:  243.82 MiB / 243.82 MiB   [-----------] 100.00% 202.66 MiB p/s 1s
🚀  Starting control plane node minikube in cluster minikube
💾  Downloading Kubernetes v1.28.3 preload ...
    > preloaded-images-k8s-v18-v1...:  403 B / 403 B   [---------] 100.00% ? p/s 0s
🐳  Preparing Kubernetes v1.28.3 on Docker 24.0.7 ...
    ▪ Generating certificates and keys ...
    ▪ Booting control plane ...
    ▪ Configuring RBAC ...
    ▪ Using image gcr.io/k8s-minikube/storage-provisioner:v5
    ▪ Using image gcr.io/k8s-minikube/dashboard:v2.7.0
    ▪ Verifying Kubernetes components...
```

**Настройка ресурсов:**

```bash
# Запуск с определенным количеством ресурсов
$ minikube start --memory=4096 --cpus=2

# Использование определенного драйвера
$ minikube start --driver=virtualbox
```

**Работа с кластером:**

```bash
# Проверка статуса
$ minikube status

# Информация о кластере
$ kubectl cluster-info

# Просмотр узлов
$ kubectl get nodes

# Доступ к Dashboard
$ minikube dashboard
```

**Остановка и удаление:**

```bash
# Остановка кластера
$ minikube stop

# Удаление кластера
$ minikube delete

# Очистка всех данных
$ minikube delete --all
```

**Настройка `Addons`:**

```bash
# Список доступных addons
$ minikube addons list

# Включение addon
$ minikube addons enable ingress
$ minikube addons enable metrics-server
$ minikube addons enable dashboard

# Отключение addon
$ minikube addons disable ingress
```

**Получение `IP` адреса:**

```bash
$ minikube ip

192.168.49.2
```

**Туннелирование сервисов:**

**Для доступа к **LoadBalancer** сервисам:**

```bash
$ minikube tunnel
```

**Рекомендации:**

- Используйте **minikube** для локальной разработки
- Настраивайте достаточные ресурсы для ваших приложений
- Используйте **addons** для расширенной функциональности
- Регулярно обновляйте **minikube**
- Используйте правильный драйвер для вашей системы

## Декодирование секретов

В **Kubernetes** секреты хранятся в закодированном формате **base64**. Иногда нам нужно декодировать их для просмотра или использования.

**Просмотр секрета:**

```bash
$ kubectl get secret my-secret -o yaml

apiVersion: v1
kind: Secret
metadata:
  name: my-secret
type: Opaque
data:
  username: YWRtaW4=        # base64 закодировано
  password: cGFzc3dvcmQxMjM= # base64 закодировано
```

**Декодирование в командной строке:**

**Одно значение:**

```bash
$ kubectl get secret my-secret -o jsonpath='{.data.username}' | base64 -d

admin
```

**Все значения:**

```bash
$ kubectl get secret my-secret -o json | jq -r '.data | to_entries[] | "\(.key): \(.value | @base64d)"'

username: admin
password: password123
```

**Использование kubectl decode:**

```bash
$ kubectl get secret my-secret -o jsonpath='{.data.username}' | base64 --decode

admin
```

**Декодирование через `YAML`:**

**Можно использовать **kubectl** для автоматического декодирования:**

```bash
$ kubectl get secret my-secret -o go-template='{{range $k,$v := .data}}{{printf "%s: " $k}}{{if not $v}}{{$v}}{{else}}{{$v | base64decode}}{{end}}{{"\n"}}{{end}}'
```

**Использование в переменных окружения:**

**Секреты автоматически декодируются при использовании в переменных окружения:**

```yaml
apiVersion: v1
kind: Pod
spec:
  containers:
    - name: my-container
      image: nginx
      env:
        - name: USERNAME
          valueFrom:
            secretKeyRef:
              name: my-secret
              key: username
```

**Рекомендации:**

- Не храните секреты в открытом виде в репозитории
- Используйте внешние системы управления секретами для продакшена
- Регулярно ротируйте секреты
- Ограничивайте доступ к секретам через **RBAC**
- Используйте инструменты для безопасной работы с секретами

## PV vs PVC

В **Kubernetes Persistent Volume** (PV) и **Persistent Volume Claim** (PVC) используются для управления хранением данных в кластере. Понимание различий между ними необходимо для правильного управления хранилищем.

**Persistent Volume (PV):**

**Persistent Volume** — это ресурс в кластере, который предоставляет хранилище, созданное администратором кластера или динамически через **StorageClass**.

**Характеристики PV:**

- Создается администратором кластера
- Имеет определенную емкость хранилища
- Поддерживает различные типы хранилища (NFS, `iSCSI`, `AWS EBS`, `GCE Persistent Disk` и т.д.)
- Имеет режимы доступа (ReadWriteOnce, `ReadOnlyMany`, ReadWriteMany)

**Пример `PV`:**

```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: my-pv
spec:
  capacity:
    storage: 10Gi
  accessModes:
    - ReadWriteOnce
  persistentVolumeReclaimPolicy: Retain
  storageClassName: slow
  nfs:
    path: /tmp
    server: 172.17.0.2
```

**Persistent `Volume Claim` (PVC):**

**Persistent Volume Claim** — это запрос на хранилище от пользователя. Пользователи создают **PVC**, и **Kubernetes** находит подходящий `PV` для удовлетворения запроса.

**Характеристики `PVC`:**

- Создается пользователем или приложением
- Запрашивает определенную емкость хранилища
- Может запрашивать определенный класс хранилища (StorageClass)
- Связывается с подходящим `PV`

**Пример `PVC`:**

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: my-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
  storageClassName: slow
```

**Связь между `PV` и `PVC`:**

**Kubernetes** автоматически связывает **PVC** с подходящим `PV` на основе:**
- Запрошенной емкости
- Режимов доступа
- Класса хранилища (StorageClass)

**Режимы доступа:**

1. **ReadWriteOnce (RWO)** — том может быть смонтирован как **read-write** одной нодой
2. **ReadOnlyMany (ROX)** — том может быть смонтирован **read-only** многими нодами
3. **ReadWriteMany (RWX)** — том может быть смонтирован как **read-write** многими нодами

**Политики рекламации:**

1. **Retain** — том сохраняется при удалении **PVC**
2. **Recycle** — данные удаляются, том готов к повторному использованию (устарело)
3. **Delete** — том удаляется автоматически при удалении **PVC**

**StorageClass:**

**StorageClass** позволяет динамически создавать `PV` при создании **PVC**:**

```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: fast-ssd
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp3
  fsType: ext4
```

**Использование `PVC` в Pod:**

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-pod
spec:
  containers:
    - name: my-container
      image: nginx
      volumeMounts:
        - name: my-storage
          mountPath: /data
  volumes:
    - name: my-storage
      persistentVolumeClaim:
        claimName: my-pvc
```

**Сравнение `PV` и `PVC`:**

| Характеристика | **Persistent Volume** (PV) | **Persistent Volume Claim** (PVC) |
|----------------|------------------------|-------------------------------|
| **Создается** | Администратором | Пользователем |
| **Назначение** | Предоставляет хранилище | Запрашивает хранилище |
| **Уровень** | Кластер | **Namespace** |
| **Связь** | Связывается с **PVC** | Связывается с `PV` |
| **Жизненный цикл** | Независимый | Зависит от `PV` |

**Рекомендации:**

- Используйте **StorageClass** для динамического создания `PV`
- Используйте **PVC** в **Pod** манифестах вместо прямого указания `PV`
- Настройте правильные политики рекламации для ваших данных
- Используйте подходящие режимы доступа для ваших приложений
- Регулярно проверяйте использование хранилища

## Типы сервисов: ClusterIP/NodePort/`LB`

В **Kubernetes Service** обеспечивает стабильную конечную точку для доступа к подам, работающим в кластере. Существует несколько типов сервисов, каждый из которых предназначен для разных сценариев использования.

**ClusterIP:**

**ClusterIP** — это тип сервиса по умолчанию. Он предоставляет внутренний `IP`-адрес, доступный только внутри кластера.

**Характеристики `ClusterIP`:**

- Доступен только внутри кластера
- Внутренний балансировщик нагрузки
- Используется для межсервисного взаимодействия
- Недоступен извне кластера

**Пример `ClusterIP`:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  type: ClusterIP
  selector:
    app: my-app
  ports:
    - port: 80
      targetPort: 8080
```

**NodePort:**

**NodePort** открывает порт на каждой ноде кластера и направляет трафик с этого порта на сервис.

**Характеристики `NodePort`:**

- Доступен извне кластера
- Порт открыт на всех нодах (30000-32767)
- Используется для доступа извне без **LoadBalancer**
- Может быть небезопасным в продакшене

**Пример `NodePort`:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  type: NodePort
  selector:
    app: my-app
  ports:
    - port: 80
      targetPort: 8080
      nodePort: 30080
```

**LoadBalancer:**

**LoadBalancer** создает внешний балансировщик нагрузки (если поддерживается облачным провайдером) и направляет трафик на сервис.

**Характеристики `LoadBalancer`:**

- Создает внешний балансировщик нагрузки
- Доступен извне кластера
- Автоматически получает внешний `IP`-адрес
- Используется в облачных средах (AWS, `GCP`, Azure)

**Пример `LoadBalancer`:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  type: LoadBalancer
  selector:
    app: my-app
  ports:
    - port: 80
      targetPort: 8080
```

**ExternalName:**

**ExternalName** создает сервис, который указывает на внешнее **DNS**-имя.

**Пример `ExternalName`:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: external-service
spec:
  type: ExternalName
  externalName: external.example.com
```

**Сравнение типов сервисов:**

| Характеристика | **ClusterIP** | **NodePort** | **LoadBalancer** |
|----------------|-----------|----------|--------------|
| **Доступность** | Только внутри кластера | Извне (через порт ноды) | Извне (через балансировщик) |
| **IP-адрес** | Внутренний | Внутренний | Внешний |
| **Порт** | Внутренний | Открыт на нодах | Внешний балансировщик |
| **Использование** | Межсервисное взаимодействие | Тестирование, разработка | Продакшен |
| **Стоимость** | Бесплатно | Бесплатно | Зависит от провайдера |

**Рекомендации:**

- Используйте **ClusterIP** для внутренних сервисов
- Используйте **NodePort** для разработки и тестирования
- Используйте **LoadBalancer** для продакшена в облачных средах
- Используйте **Ingress** вместе с **ClusterIP** для маршрутизации **HTTP**/**HTTPS** трафика
- Настраивайте правильные **selectors** для подключения к подам
- Используйте **SessionAffinity** для **sticky sessions** при необходимости

## targetPort vs port

**Kubernetes** — это популярная система оркестрации контейнеров, используемая для развертывания контейнерных приложений и управления ими. Являясь важным компонентом этой экосистемы, службы **Kubernetes** обеспечивают стабильную конечную точку для доступа к модулям, работающим в кластере **Kubernetes**.

Служба устраняет необходимость знать `IP`-адреса или порты. Однако, чтобы обеспечить правильную маршрутизацию трафика к ответственным модулям, важно понимать разницу между полями `port` и `targetPort` при определении службы **Kubernetes**.

**Поле port:**

Определение службы указывает номер порта, на котором служба будет прослушивать входящий трафик, используя поле `port`. Служба использует этот порт для маршрутизации трафика к модулям, за которые она отвечает.

**Пример с port:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: my-app
  ports:
    - name: http
      port: 8080
      protocol: TCP
      targetPort: 8080
```

**Поле `targetPort`:**

В определении службы поле `targetPort` играет решающую роль в маршрутизации трафика к модулям службы. В частности, в этом поле указывается номер порта модуля, на который Служба отвечает за маршрутизацию трафика.

**Пример с разными port и `targetPort`:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: my-app
  ports:
    - name: http
      port: 80
      protocol: TCP
      targetPort: 8080
```

**В этом примере:**
- Поле `targetPort` направляет трафик на порт `8080` в модулях службы
- Поле `port` направляет входящий трафик на порт 80 службы

**Основные различия:**

| Характеристика | **port** | **targetPort** |
|----------------|------|------------|
| **Назначение** | Порт прослушивания службы | Порт пода для маршрутизации |
| **Доступность** | Внешний порт для клиентов | Внутренний порт пода |
| **Пример** | 80, `443` | `8080`, `3000` |

**Рекомендации:**

- `port` должен быть установлен на доступный клиенту внешний порт
- `targetPort` должен быть установлен на номер порта модуля
- Используйте имена портов для удобства управления
- Убедитесь, что `targetPort` соответствует порту приложения в поде

## Список подов и узлов

В **Kubernetes** крайне важно понимать текущее состояние модулей и назначенных им узлов. Эта информация очень полезна для мониторинга системы, масштабируемости и устранения неполадок.

**Список всех подов:**

**Команда `kubectl get pods` выводит список всех модулей в **Kubernetes**:**

```bash
$ kubectl get pods

NAME                        READY   STATUS    RESTARTS   AGE
nginx-deployment-7b4f8f4c4f 1/1     Running   0          10m
nginx-deployment-7b4f8f4c5f 1/1     Running   0          10m
nginx-deployment-7b4f8f4c6f 1/1     Running   0          10m
```

Эта команда отобразит таблицу с информацией обо всех подах. Это включает в себя имя модуля, текущий статус и возраст.

**Список всех узлов:**

**Чтобы получить информацию обо всех узлах в кластере **Kubernetes**, мы можем использовать команду `kubectl get nodes`:**

```bash
$ kubectl get nodes

NAME   STATUS   ROLES                  AGE   VERSION
node1  Ready    control-plane,master   3d    v1.22.2
node2  Ready    <none>                 3d    v1.22.2
node3  Ready    <none>                 2d    v1.22.2
```

**Детальная информация с -o wide:**

**Используйте флаг `-o wide` для отображения дополнительной информации:**

**Для узлов:**

```bash
$ kubectl get nodes -o wide

NAME   STATUS   ROLES                  AGE   VERSION   INTERNAL-IP     EXTERNAL-IP   OS-IMAGE         KERNAL-VERSION                CONTAINER-RUNTIME
node1  Ready    control-plane,master   3d    v1.22.2   10.56.179.63    <none>        Amazon Linux 2   5.4.237-143.346.amzn2.x86_64   docker://20.10.17
```

**Для подов:**

```bash
$ kubectl get pods -o wide

NAME                        READY   STATUS    RESTARTS   AGE   IP            NODE   NOMINATED NODE   READINESS GATES
nginx-deployment-7b4f8f4c4f 1/1     Running   0          10m   10.244.1.225  node2  <none>           <none>
nginx-deployment-7b4f8f4c5f 1/1     Running   0          10m   10.244.2.245  node1  <none>           <none>
nginx-deployment-7b4f8f4c6f 1/1     Running   0          10m   10.244.3.167  node3  <none>           <none>
```

**Фильтрация по узлу:**

**Чтобы отфильтровать поды по узлу, на котором они выполняются:**

```bash
$ kubectl get pods --field-selector spec.nodeName=node1

NAME                        READY   STATUS    RESTARTS   AGE
nginx-deployment-7b4f8f4c5f 1/1     Running   0          10m
```

**Отображение меток:**

**Используйте флаг `--show-labels` для отображения меток:**

```bash
$ kubectl get pods -o wide --show-labels

NAME                        READY   STATUS    RESTARTS   AGE   IP            NODE   LABELS
nginx-deployment-7b4f8f4c4f 1/1     Running   0          10m   10.244.1.225  node2  app=myapp
```

**Рекомендации:**

- Используйте `-o wide` для получения детальной информации
- Фильтруйте результаты для анализа конкретных ресурсов
- Используйте метки для организации и фильтрации подов
- Регулярно проверяйте состояние узлов и подов

## Логи пода

**Kubernetes** — это мощная платформа для оркестрации контейнеров, которая позволяет нам управлять контейнерными приложениями и развертывать их в любом масштабе. Просмотр журналов является важной частью обеспечения надежности и доступности наших приложений.

**Базовое использование:**

**Для пода с одним контейнером:**

```bash
$ kubectl logs <pod-name>
```

**Для пода с несколькими контейнерами:**

```bash
$ kubectl logs <pod-name> -c <container-name>
```

**Пример:**

```bash
$ kubectl logs my-pod -c httpd-server

[Wed Apr 27 10:12:29.000000 2022] [core:notice] [pid 1:tid 140028840174080] AH00094: Command line: 'httpd -D FOREGROUND'
[Wed Apr 27 10:12:29.000000 2022] [mpm_prefork:notice][pid 1:tid 140028840174080] AH00163: Apache/2.4.41 (Unix) configured resuming normal operations
```

**Последние N строк:**

**Используйте флаг `--tail` для получения только последних строк:**

```bash
$ kubectl logs <pod-name> -c <container-name> --tail=100
```

**Пример:**

```bash
$ kubectl logs my-pod -c httpd-server --tail=100
```

Эта команда извлекает последние `100` строк журналов контейнера.

**Просмотр в реальном времени:**

**Используйте флаг `--follow` (или `-f`) для потоковой передачи логов:**

```bash
$ kubectl logs <pod-name> -c <container-name> --follow
```

Использование параметра `--follow` позволяет нам просматривать журналы в режиме реального времени по мере их создания, что делает его особенно полезным при устранении неполадок.

**Просмотр через `Dashboard`:**

1. **Запустите `kubectl proxy`:**
   ```bash
   $ kubectl proxy
   Starting to serve on 127.0.0.1:8001
   ```

2. **Откройте **Dashboard** в браузере:**
   ```text
   http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/
   ```

3. Найдите нужный под и перейдите на вкладку "**Logs**"

**Рекомендации по логированию:**

1. Использование структурированного ведения журнала
2. Логирование в формате **JSON**
3. Избегайте регистрации конфиденциальной информации
4. Настройка уровней журнала

**Рекомендации:**

- Используйте `--follow` для мониторинга в реальном времени
- Используйте `--tail` для быстрого доступа к последним логам
- Указывайте контейнер при работе с несколькими контейнерами
- Используйте **Dashboard** для визуального просмотра логов
- Настраивайте централизованное логирование для продакшена

## Переключение namespace

В кластере **Kubernetes** пространства имен позволяют разделять ресурсы и изолировать рабочие нагрузки. Они действуют как виртуальные кластеры внутри физического кластера, позволяя нескольким командам или проектам работать независимо друг от друга.

**Установка namespace по умолчанию:**

**Основным интерфейсом для взаимодействия с кластерами **Kubernetes** является инструмент командной строки `kubectl`. Чтобы переключиться на определенное пространство имен:**

```bash
$ kubectl config set-context --current --namespace=<namespace-name>
```

**Пример:**

```bash
$ kubectl config set-context --current --namespace=kube-public

Context "minikube" modified.
```

Как только мы выполним эту команду, контекст нашей конфигурации `kubectl` обновится до нового пространства имен. Последующие команды будут работать в этом пространстве имен по умолчанию.

**Использование флага --namespace:**

**Мы можем использовать флаг `--namespace` (или `-n`) непосредственно с командой `kubectl`, чтобы переключать пространства имен для конкретной команды:**

```bash
$ kubectl get pods --namespace=kube-public

No resources found in kube-public namespace.
```

Этот подход полезен, когда мы хотим запускать изолированные команды в другом пространстве имен без изменения контекста по умолчанию.

**Настройка в файле конфигурации:**

**Чтобы установить пространство имён по умолчанию, откройте файл конфигурации Kubernetes (обычно `~/.kube/config`) и найдите раздел `contexts`:**

```yaml
contexts:
  - context:
      cluster: my-cluster
      namespace: my-namespace
      user: my-user
    name: my-context
```

**Список всех namespace:**

```bash
$ kubectl get namespaces

NAME              STATUS   AGE
default           Active   3d
kube-system       Active   3d
kube-public       Active   3d
kube-node-lease   Active   3d
```

**Создание нового namespace:**

```bash
$ kubectl create namespace my-namespace
```

**Рекомендации:**

- Используйте флаг `--namespace` для временных операций
- Устанавливайте контекст по умолчанию для часто используемых **namespace**
- Создавайте отдельные **namespace** для разных окружений (dev, staging, prod)
- Используйте метки для дополнительной организации ресурсов
- Регулярно проверяйте и очищайте неиспользуемые **namespace**

## Получение YAML объекта

В **Kubernetes** мы можем получить **YAML**-определение любого объекта для просмотра или редактирования. Это полезно для анализа конфигурации, создания резервных копий или репликации объектов.

**Базовое использование:**

**Команда `kubectl get` с флагом `-o yaml` позволяет получить **YAML**-определение объекта:**

```bash
$ kubectl get <resource-type> <resource-name> -o yaml
```

**Примеры:**

**Получение `YAML` пода:**

```bash
$ kubectl get pod my-pod -o yaml
```

**Получение `YAML Deployment`:**

```bash
$ kubectl get deployment my-deployment -o yaml
```

**Получение `YAML Service`:**

```bash
$ kubectl get service my-service -o yaml
```

**Сохранение в файл:**

**Мы можем сохранить **YAML** в файл для дальнейшего использования:**

```bash
$ kubectl get pod my-pod -o yaml > pod-backup.yaml
```

**Редактирование и применение:**

**Мы можем получить **YAML**, отредактировать его и применить обратно:**

```bash
$ kubectl get pod my-pod -o yaml | kubectl replace -f -
```

**Или используя `kubectl edit`:**

```bash
$ kubectl edit pod my-pod
```

**Экспорт без служебных полей:**

**Используйте `kubectl get` с `-o yaml` и удалите служебные поля:**

```bash
$ kubectl get pod my-pod -o yaml | grep -v "creationTimestamp\|resourceVersion\|uid\|status"
```

**Получение из другого namespace:**

```bash
$ kubectl get pod my-pod -n my-namespace -o yaml
```

**Рекомендации:**

- Используйте `-o yaml` для получения полной конфигурации
- Сохраняйте **YAML**-файлы для резервного копирования
- Используйте `kubectl edit` для быстрого редактирования
- Удаляйте служебные поля при экспорте для применения в другом кластере
- Версионируйте **YAML**-файлы в системе контроля версий

## Reload image

В **Kubernetes** иногда нужно перезагрузить образ контейнера в поде без пересоздания всего **Deployment** или **Pod**. Это может быть полезно при разработке или для применения обновлений.

**Метод 1: Удаление пода (для Deployment):**

**Для **Deployment** можно удалить под, и **Kubernetes** автоматически создаст новый с обновленным образом:**

```bash
$ kubectl delete pod <pod-name>
```

Если используется **Deployment**, **Kubernetes** автоматически создаст новый под с обновленным образом (если он был обновлен в манифесте).

**Метод 2: Принудительное обновление через kubectl rollout:**

```bash
$ kubectl rollout restart deployment <deployment-name>
```

Эта команда перезапускает все поды в **Deployment**, что заставит их использовать обновленный образ.

**Метод 3: Обновление образа в `Deployment`:**

**Обновите образ в **Deployment**:**

```bash
$ kubectl set image deployment/<deployment-name> <container-name>=<new-image>:<tag>
```

**Пример:**

```bash
$ kubectl set image deployment/my-app app-container=my-app:v2.0.0
```

**Метод 4: Редактирование `Deployment`:**

**Используйте `kubectl edit` для редактирования **Deployment**:**

```bash
$ kubectl edit deployment <deployment-name>
```

Измените поле `image` в разделе `containers` и сохраните файл.

**Метод 5: Применение обновленного `YAML`:**

1. **Получите текущий **YAML**:**
   ```bash
   $ kubectl get deployment <deployment-name> -o yaml > deployment.yaml
   ```

2. Отредактируйте файл и обновите поле `image`

3. **Примените изменения:**
   ```bash
   $ kubectl apply -f deployment.yaml
   ```

**Проверка статуса обновления:**

```bash
$ kubectl rollout status deployment/<deployment-name>
```

**Откат обновления:**

**Если что-то пошло не так, можно откатить обновление:**

```bash
$ kubectl rollout undo deployment/<deployment-name>
```

**Рекомендации:**

- Используйте теги версий для образов вместо `latest`
- Проверяйте статус обновления после применения изменений
- Используйте стратегию развертывания (rolling update) для нулевого простоя
- Тестируйте обновления в **staging** окружении перед продакшеном
- Используйте **readiness** и **liveness probes** для контроля работоспособности

## Лучшие практики

- **Ресурсы и лимиты:** задавайте **requests** и **limits** для **CPU**/памяти у подов; избегайте **overcommit** на узлах; используйте **namespace quotas** для ограничения потребления.
- **Безопасность:** не запускайте поды от **root**; используйте **Pod Security Standards**/**Admission**; храните секреты в **Secret**, не в переменных окружения в **plain text**.
- **Надёжность:** настройте **readiness** и **liveness probes**; используйте **Deployment** с **replicas** > 1 для критичных приложений; **PDB** для защиты от нежелательного **eviction**.
- **Сеть и сервисы:** предпочитайте **ClusterIP** для внутренней связи; **Ingress** для **HTTP**/**HTTPS**; не публикуйте **NodePort** без необходимости.
- **Управление конфигурацией:** используйте **ConfigMap** и **Secret**; не храните конфиг в образах; версионируйте манифесты в **Git** и применяйте через CI/CD.

## См. также

- [[kubernetes-advanced|Kubernetes Advanced]]
- [[kubernetes-networking|Kubernetes Networking]]
- [[kubernetes-security|Kubernetes Security]]
- [[kubernetes-storage|Kubernetes Storage]]
