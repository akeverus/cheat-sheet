---
title: "Kubernetes Advanced"
description: "Kubernetes (K8s) - это платформа для оркестрации контейнеров с открытым исходным кодом, которая автоматизирует развертывание, масштабирование и управление контейнеризованными приложениями. Этот документ охватывает продвинутые концепции и best practices для работы с Kubernetes."
tags:
  - platform
  - containers
  - kubernetes-advanced
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kubernetes Advanced

**Kubernetes** (K8s) — это платформа для оркестрации контейнеров с открытым исходным кодом, которая автоматизирует развертывание, масштабирование и управление контейнеризованными приложениями. Этот документ охватывает продвинутые концепции и **best practices** для работы с **Kubernetes**.

## Полезные ссылки
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Istio Service Mesh](https://istio.io/latest/docs/)
- [Argo CD](https://argo-cd.readthedocs.io/) — **GitOps** для **Kubernetes**
- [Prometheus Operator](https://github.com/prometheus-operator/prometheus-operator) — оператор **Prometheus**
- [Kubernetes Security Best Practices](https://kubernetes.io/docs/concepts/security/security-checklist/) — чек-лист безопасности

## Содержание

- [Архитектура кластера](#архитектура-кластера)
  - [Control Plane компоненты](#control-plane-компоненты)
  - [Scheduler (kube-scheduler)](#scheduler-kube-scheduler)
- [Продвинутые workload паттерны](#продвинутые-workload-паттерны)
  - [StatefulSets с продвинутой конфигурацией](#statefulsets-с-продвинутой-конфигурацией)
  - [DaemonSets для node-level сервисов](#daemonsets-для-node-level-сервисов)
- [Service Mesh (Istio)](#service-mesh-istio)
  - [Установка Istio](#установка-istio)
  - [Virtual Services и Gateway](#virtual-services-и-gateway)
  - [Destination Rules](#destination-rules)
  - [Circuit Breaker](#circuit-breaker)
- [Network Policies](#network-policies)
  - [Продвинутые Network Policies](#продвинутые-network-policies)
- [Security](#security)
  - [Pod Security Standards](#pod-security-standards)
  - [Service Accounts и RBAC](#service-accounts-и-rbac)
- [Storage](#storage)
  - [Advanced Persistent Volumes](#advanced-persistent-volumes)
- [Operators](#operators)
  - [Создание Custom Resource](#создание-custom-resource)
  - [Custom Controller (Go)](#custom-controller-go)
- [GitOps (ArgoCD)](#gitops-argocd)
  - [Установка ArgoCD](#установка-argocd)
  - [Application Manifest](#application-manifest)
- [Monitoring и Observability](#monitoring-и-observability)
  - [Metrics Server](#metrics-server)
  - [Prometheus Operator](#prometheus-operator)
  - [Service Monitors](#service-monitors)
- [Лучшие практики](#лучшие-практики)
  - [Multi-tenancy](#multi-tenancy)
  - [High Availability](#high-availability)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [Performance Tuning](#performance-tuning)
  - [Backup и Recovery](#backup-и-recovery)
- [См. также](#см-также)

## Архитектура кластера

### Control Plane компоненты

Ниже — пример манифеста **Pod** для **API Server** (YAML).
```yaml
# API Server (kube-apiserver)
# - Единственная точка входа для всех операций
# - Валидация и аутентификация запросов
# - Обработка REST операций
apiVersion: v1
kind: Pod
metadata:
  name: api-server-pod
spec:
  containers:
  - name: api-server
    image: k8s.gcr.io/kube-apiserver:v1.25.0
    command:
    - kube-apiserver
    - --advertise-address=192.168.1.100
    - --allow-privileged=true
    - --authorization-mode=Node,RBAC
    - --enable-admission-plugins=NamespaceLifecycle,LimitRanger,ServiceAccount,DefaultStorageClass,DefaultTolerationSeconds,MutatingAdmissionWebhook,ValidatingAdmissionWebhook,ResourceQuota
    - --etcd-servers=http://127.0.0.1:2379
    - --service-cluster-ip-range=10.96.0.0/12
```

```yaml
# etcd - распределенное key-value хранилище
# - Хранит все данные кластера
# - Consensus через Raft алгоритм
# - Высокая доступность и консистентность
apiVersion: v1
kind: Pod
metadata:
  name: etcd-pod
spec:
  containers:
  - name: etcd
    image: k8s.gcr.io/etcd:3.5.4-0
    command:
    - etcd
    - --advertise-client-urls=https://192.168.1.100:2379
    - --cert-file=/etc/kubernetes/pki/etcd/server.crt
    - --client-cert-auth=true
    - --data-dir=/var/lib/etcd
    - --initial-advertise-peer-urls=https://192.168.1.100:2380
    - --initial-cluster=etcd-1=https://192.168.1.100:2380
    - --key-file=/etc/kubernetes/pki/etcd/server.key
    - --listen-client-urls=https://192.168.1.100:2379,https://127.0.0.1:2379
    - --listen-peer-urls=https://192.168.1.100:2380
    - --peer-cert-file=/etc/kubernetes/pki/etcd/peer.crt
    - --peer-key-file=/etc/kubernetes/pki/etcd/peer.key
    - --peer-trusted-ca-file=/etc/kubernetes/pki/etcd/ca.crt
    - --snapshot-count=10000
    - --trusted-ca-file=/etc/kubernetes/pki/etcd/ca.crt
```

### Scheduler (kube-scheduler)
```yaml
# Планировщик подов
# - Выбирает подходящий узел для пода
# - Учитывает ресурсы, affinity/anti-affinity, constraints
apiVersion: v1
kind: ConfigMap
metadata:
  name: scheduler-config
  namespace: kube-system
data:
  config.yaml: |
    apiVersion: kubescheduler.config.k8s.io/v1beta3
    kind: KubeSchedulerConfiguration
    profiles:
    - schedulerName: default-scheduler
      plugins:
        score:
          disabled:
          - name: NodeResourcesLeastAllocated
          enabled:
          - name: NodeResourcesMostAllocated
            weight: 1
        filter:
          disabled:
          - name: TaintToleration
      pluginConfig:
      - name: NodeResourcesMostAllocated
        args:
          apiVersion: kubescheduler.config.k8s.io/v1beta3
          kind: NodeResourcesMostAllocatedArgs
          resources:
          - name: cpu
            weight: 1
          - name: memory
            weight: 1
```

## Продвинутые workload паттерны

### StatefulSets с продвинутой конфигурацией
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres-cluster
  namespace: database
spec:
  serviceName: postgres
  replicas: 3
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      affinity:
        podAntiAffinity:
          requiredDuringSchedulingIgnoredDuringExecution:
          - labelSelector:
              matchExpressions:
              - key: app
                operator: In
                values:
                - postgres
            topologyKey: kubernetes.io/hostname
        nodeAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
          - weight: 100
            preference:
              matchExpressions:
              - key: node-type
                operator: In
                values:
                - database
      containers:
      - name: postgres
        image: postgres:15
        env:
        - name: POSTGRES_DB
          value: myapp
        - name: POSTGRES_USER
          value: postgres
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: postgres-secret
              key: password
        ports:
        - containerPort: 5432
          name: postgres
        volumeMounts:
        - name: postgres-data
          mountPath: /var/lib/postgresql/data
        - name: postgres-config
          mountPath: /etc/postgresql
        livenessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - postgres
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - postgres
          initialDelaySeconds: 5
          periodSeconds: 5
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
  volumeClaimTemplates:
  - metadata:
      name: postgres-data
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: fast-ssd
      resources:
        requests:
          storage: 100Gi
  - metadata:
      name: postgres-config
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: standard
      resources:
        requests:
          storage: 1Gi
```

### DaemonSets для node-level сервисов
```yaml
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: node-exporter
  namespace: monitoring
  labels:
    app: node-exporter
spec:
  selector:
    matchLabels:
      app: node-exporter
  template:
    metadata:
      labels:
        app: node-exporter
    spec:
      hostNetwork: true
      hostPID: true
      hostIPC: true
      serviceAccountName: node-exporter
      tolerations:
      - effect: NoSchedule
        operator: Exists
      - effect: NoExecute
        operator: Exists
      containers:
      - name: node-exporter
        image: prom/node-exporter:v1.5.0
        ports:
        - containerPort: 9100
          hostPort: 9100
          name: metrics
        args:
        - --path.rootfs=/host
        - --collector.filesystem.mount-points-exclude=^/(sys|proc|dev|host|etc)($$|/)
        - --collector.filesystem.fs-types-exclude=^(autofs|binfmt_misc|cgroup|configfs|debugfs|devpts|devtmpfs|fusectl|hugetlbfs|mqueue|overlay|proc|procfs|pstore|rpc_pipefs|securityfs|sysfs|tracefs)$$
        volumeMounts:
        - name: rootfs
          mountPath: /host
          readOnly: true
        - name: proc
          mountPath: /host/proc
          readOnly: true
        - name: sys
          mountPath: /host/sys
          readOnly: true
        resources:
          requests:
            memory: 30Mi
            cpu: 100m
          limits:
            memory: 50Mi
            cpu: 200m
        securityContext:
          runAsNonRoot: true
          runAsUser: 65534
          allowPrivilegeEscalation: false
          readOnlyRootFilesystem: true
          capabilities:
            drop: ["ALL"]
      volumes:
      - name: rootfs
        hostPath:
          path: /
      - name: proc
        hostPath:
          path: /proc
      - name: sys
        hostPath:
          path: /sys
```

## Service Mesh (Istio)

### Установка Istio
```bash
# Скачивание Istio
curl -L https://istio.io/downloadIstio | sh -
cd istio-1.17.0

# Добавление istioctl в PATH
export PATH=$PWD/bin:$PATH

# Установка демо профиля
istioctl install --set profile=demo -y

# Включение injection для namespace
kubectl label namespace default istio-injection=enabled
```

### Virtual Services и Gateway
```yaml
# Gateway для внешнего трафика
apiVersion: networking.istio.io/v1beta1
kind: Gateway
metadata:
  name: myapp-gateway
  namespace: default
spec:
  selector:
    istio: ingressgateway
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - myapp.example.com
  - port:
      number: 443
      name: https
      protocol: HTTPS
    tls:
      mode: SIMPLE
      credentialName: myapp-tls
    hosts:
    - myapp.example.com

---
# Virtual Service для маршрутизации
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: myapp-routing
  namespace: default
spec:
  hosts:
  - myapp.example.com
  gateways:
  - myapp-gateway
  http:
  - match:
    - uri:
        prefix: /api/v1
    route:
    - destination:
        host: api-service
        subset: v1
    timeout: 10s
    retries:
      attempts: 3
      perTryTimeout: 2s
  - match:
    - uri:
        prefix: /api/v2
    route:
    - destination:
        host: api-service
        subset: v2
    mirror:
      host: api-service
      subset: v1
      percentage:
        value: 100.0
  - route:
    - destination:
        host: web-service
    timeout: 30s
```

### Destination Rules
```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: api-service-dr
  namespace: default
spec:
  host: api-service
  trafficPolicy:
    loadBalancer:
      simple: ROUND_ROBIN
    connectionPool:
      tcp:
        maxConnections: 100
      http:
        http1MaxPendingRequests: 10
        http2MaxRequests: 100
        maxRequestsPerConnection: 10
        maxRetries: 3
    outlierDetection:
      consecutiveLocalOriginFailure: 5
      interval: 10s
      baseEjectionTime: 30s
      maxEjectionPercent: 50
  subsets:
  - name: v1
    labels:
      version: v1
    trafficPolicy:
      loadBalancer:
        simple: RANDOM
  - name: v2
    labels:
      version: v2
    trafficPolicy:
      loadBalancer:
        simple: LEAST_REQUEST
```

### Circuit Breaker
```yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: circuit-breaker
spec:
  host: httpbin
  trafficPolicy:
    connectionPool:
      tcp:
        maxConnections: 100
      http:
        http1MaxPendingRequests: 10
        http2MaxRequests: 100
        maxRequestsPerConnection: 10
        maxRetries: 3
    outlierDetection:
      consecutive5xxErrors: 3
      interval: 10s
      baseEjectionTime: 30s
      maxEjectionPercent: 50
```

## Network Policies

### Продвинутые Network Policies
```yaml
# Ограничение трафика между namespaces
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: restrict-cross-namespace
  namespace: production
spec:
  podSelector: {}  # Применяется ко всем подам
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          environment: production
    - podSelector:
        matchLabels:
          role: ingress-controller
  egress:
  - to:
    - namespaceSelector:
        matchLabels:
          name: kube-system
    ports:
    - protocol: UDP
      port: 53  # DNS
  - to:
    - namespaceSelector:
        matchLabels:
          environment: production
    - ipBlock:
        cidr: 10.0.0.0/8  # Internal network
  - to:
    - ipBlock:
        cidr: 0.0.0.0/0
        except:
        - 10.0.0.0/8
        - 172.16.0.0/12
        - 192.168.0.0/16
    ports:
    - protocol: TCP
      port: 443  # HTTPS outbound only

---
# Изоляция database трафика
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: database-isolation
  namespace: database
spec:
  podSelector:
    matchLabels:
      app: postgres
  policyTypes:
  - Ingress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          environment: production
    - podSelector:
        matchLabels:
          app: api-server
    ports:
    - protocol: TCP
      port: 5432
  - from:
    - namespaceSelector:
        matchLabels:
          name: monitoring
    - podSelector:
        matchLabels:
          app: postgres-exporter
    ports:
    - protocol: TCP
      port: 5432
```

## Security

### Pod Security Standards
```yaml
# Применение restricted PSS
apiVersion: v1
kind: Namespace
metadata:
  name: restricted-namespace
  labels:
    pod-security.kubernetes.io/enforce: restricted
    pod-security.kubernetes.io/enforce-version: v1.24
    pod-security.kubernetes.io/warn: restricted
    pod-security.kubernetes.io/warn-version: v1.24
    pod-security.kubernetes.io/audit: restricted
    pod-security.kubernetes.io/audit-version: v1.24

---
# Security Context для подов
apiVersion: v1
kind: Pod
metadata:
  name: secure-pod
spec:
  securityContext:
    runAsUser: 1000
    runAsGroup: 1000
    fsGroup: 1000
    runAsNonRoot: true
    seccompProfile:
      type: RuntimeDefault
  containers:
  - name: app
    image: myapp:latest
    securityContext:
      allowPrivilegeEscalation: false
      readOnlyRootFilesystem: true
      runAsNonRoot: true
      runAsUser: 1000
      capabilities:
        drop:
        - ALL
      seccompProfile:
        type: RuntimeDefault
    volumeMounts:
    - name: tmp
      mountPath: /tmp
    - name: cache
      mountPath: /app/cache
  volumes:
  - name: tmp
    emptyDir: {}
  - name: cache
    emptyDir: {}
```

### Service Accounts и RBAC
```yaml
# Service Account для приложения
apiVersion: v1
kind: ServiceAccount
metadata:
  name: myapp-sa
  namespace: production
automountServiceAccountToken: false

---
# Role для приложения
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: myapp-role
  namespace: production
rules:
- apiGroups: [""]
  resources: ["pods", "services", "configmaps", "secrets"]
  verbs: ["get", "list", "watch", "create", "update", "patch", "delete"]
- apiGroups: ["apps"]
  resources: ["deployments", "replicasets", "statefulsets"]
  verbs: ["get", "list", "watch", "create", "update", "patch", "delete"]
- apiGroups: ["networking.k8s.io"]
  resources: ["networkpolicies"]
  verbs: ["get", "list", "watch"]

---
# RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: myapp-rolebinding
  namespace: production
subjects:
- kind: ServiceAccount
  name: myapp-sa
  namespace: production
roleRef:
  kind: Role
  name: myapp-role
  apiGroup: rbac.authorization.k8s.io

---
# ClusterRole для cluster-wide доступа
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: cluster-admin-view
rules:
- apiGroups: [""]
  resources: ["nodes", "namespaces"]
  verbs: ["get", "list", "watch"]
- apiGroups: ["metrics.k8s.io"]
  resources: ["nodes", "pods"]
  verbs: ["get", "list"]
```

## Storage

### Advanced Persistent Volumes
```yaml
# StorageClass с продвинутыми опциями
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: fast-ssd
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp3
  fsType: ext4
  encrypted: "true"
  kmsKeyId: alias/aws/ebs
reclaimPolicy: Retain
allowVolumeExpansion: true
volumeBindingMode: WaitForFirstConsumer
allowedTopologies:
- matchLabelExpressions:
  - key: topology.kubernetes.io/zone
    values:
    - us-east-1a
    - us-east-1b

---
# PVC с data source для клонирования
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: cloned-volume
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: fast-ssd
  resources:
    requests:
      storage: 50Gi
  dataSource:
    kind: PersistentVolumeClaim
    name: source-volume

---
# Volume Snapshot
apiVersion: snapshot.storage.k8s.io/v1
kind: VolumeSnapshot
metadata:
  name: myapp-backup
spec:
  volumeSnapshotClassName: csi-aws-vsc
  source:
    persistentVolumeClaimName: myapp-data
```

## Operators

### Создание Custom Resource
```yaml
# Custom Resource Definition
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: myapps.mycompany.com
spec:
  group: mycompany.com
  versions:
  - name: v1
    served: true
    storage: true
    schema:
      openAPIV3Schema:
        type: object
        properties:
          spec:
            type: object
            properties:
              replicas:
                type: integer
                minimum: 1
                maximum: 10
              image:
                type: string
              config:
                type: object
                additionalProperties:
                  type: string
            required:
            - replicas
            - image
  scope: Namespaced
  names:
    plural: myapps
    singular: myapp
    kind: MyApp
    shortNames:
    - ma
```

### Custom Controller (Go)
```go
package main

import (
    "context"
    "fmt"
    "time"

    appsv1 "k8s.io/api/apps/v1"
    corev1 "k8s.io/api/core/v1"
    "k8s.io/apimachinery/pkg/api/errors"
    metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
    "k8s.io/apimachinery/pkg/runtime"
    "k8s.io/apimachinery/pkg/types"
    ctrl "sigs.k8s.io/controller-runtime"
    "sigs.k8s.io/controller-runtime/pkg/client"
    "sigs.k8s.io/controller-runtime/pkg/log"
)

type MyAppReconciler struct {
    client.Client
    Scheme *runtime.Scheme
}

func (r *MyAppReconciler) Reconcile(ctx context.Context, req ctrl.Request) (ctrl.Result, error) {
    log := log.FromContext(ctx)

    // Получить MyApp resource
    var myapp MyApp
    if err := r.Get(ctx, req.NamespacedName, &myapp); err != nil {
        if errors.IsNotFound(err) {
            return ctrl.Result{}, nil
        }
        return ctrl.Result{}, err
    }

    // Создать или обновить Deployment
    deployment := &appsv1.Deployment{
        ObjectMeta: metav1.ObjectMeta{
            Name:      myapp.Name + "-deployment",
            Namespace: myapp.Namespace,
        },
        Spec: appsv1.DeploymentSpec{
            Replicas: &myapp.Spec.Replicas,
            Selector: &metav1.LabelSelector{
                MatchLabels: map[string]string{"app": myapp.Name},
            },
            Template: corev1.PodTemplateSpec{
                ObjectMeta: metav1.ObjectMeta{
                    Labels: map[string]string{"app": myapp.Name},
                },
                Spec: corev1.PodSpec{
                    Containers: []corev1.Container{
                        {
                            Name:  "app",
                            Image: myapp.Spec.Image,
                            Ports: []corev1.ContainerPort{
                                {ContainerPort: 8080},
                            },
                        },
                    },
                },
            },
        },
    }

    if err := r.Create(ctx, deployment); err != nil && !errors.IsAlreadyExists(err) {
        log.Error(err, "Failed to create deployment")
        return ctrl.Result{}, err
    }

    return ctrl.Result{RequeueAfter: time.Minute * 5}, nil
}
```

## GitOps (ArgoCD)

### Установка ArgoCD
```bash
# Создание namespace
kubectl create namespace argocd

# Установка ArgoCD
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# Получение пароля admin
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d

# Port forward для доступа к UI
kubectl port-forward svc/argocd-server -n argocd 8080:443
```

### Application Manifest
```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: myapp-production
  namespace: argocd
  finalizers:
  - resources-finalizer.argocd.argoproj.io
spec:
  project: default
  source:
    repoURL: https://github.com/myorg/myapp-deploy
    targetRevision: HEAD
    path: k8s/production
    helm:
      valueFiles:
      - values.yaml
      - values-production.yaml
      parameters:
      - name: image.tag
        value: v1.2.3
  destination:
    server: https://kubernetes.default.svc
    namespace: production
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
    - CreateNamespace=true
    - PrunePropagationPolicy=foreground
    - PruneLast=true
  revisionHistoryLimit: 10
```

## Monitoring и Observability

### Metrics Server
```yaml
# Установка Metrics Server
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

# Кастомная конфигурация для нестандартных сертификатов
apiVersion: v1
kind: ServiceAccount
metadata:
  name: metrics-server
  namespace: kube-system
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: metrics-server
  namespace: kube-system
spec:
  template:
    spec:
      containers:
      - name: metrics-server
        image: k8s.gcr.io/metrics-server/metrics-server:v0.6.3
        args:
        - --cert-dir=/tmp
        - --secure-port=4443
        - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
        - --kubelet-use-node-status-port
        - --metric-resolution=15s
        - --kubelet-insecure-tls
```

### Prometheus Operator
```yaml
# Установка Prometheus Operator
kubectl apply -f https://raw.githubusercontent.com/prometheus-operator/prometheus-operator/v0.63.0/bundle.yaml

# Prometheus Instance
apiVersion: monitoring.coreos.com/v1
kind: Prometheus
metadata:
  name: k8s
  namespace: monitoring
spec:
  replicas: 2
  retention: 30d
  securityContext:
    fsGroup: 2000
    runAsNonRoot: true
    runAsUser: 1000
  serviceAccountName: prometheus
  serviceMonitorSelector:
    matchLabels:
      team: backend
  ruleSelector:
    matchLabels:
      team: backend
      prometheus: k8s
  securityContext:
    fsGroup: 2000
    runAsNonRoot: true
    runAsUser: 1000
```

### Service Monitors
```yaml
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: myapp-monitor
  namespace: monitoring
  labels:
    team: backend
spec:
  selector:
    matchLabels:
      app: myapp
  endpoints:
  - port: metrics
    path: /actuator/prometheus
    interval: 30s
    scrapeTimeout: 10s
    relabelings:
    - sourceLabels: [__meta_kubernetes_pod_name]
      targetLabel: pod_name
    - sourceLabels: [__meta_kubernetes_namespace]
      targetLabel: namespace
  namespaceSelector:
    matchNames:
    - production
    - staging
```

## Лучшие практики

### Multi-tenancy
```yaml
# Resource Quota для namespaces
apiVersion: v1
kind: ResourceQuota
metadata:
  name: compute-quota
  namespace: team-a
spec:
  hard:
    requests.cpu: "4"
    requests.memory: 8Gi
    limits.cpu: "8"
    limits.memory: 16Gi
    persistentvolumeclaims: "10"
    pods: "20"
    services: "5"
    secrets: "20"
    configmaps: "25"

---
# Network Policies для изоляции
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: tenant-isolation
  namespace: team-a
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          tenant: team-a
  egress:
  - to:
    - namespaceSelector:
        matchLabels:
          name: kube-system
    ports:
    - protocol: UDP
      port: 53
  - to: []
    ports:
    - protocol: TCP
      port: 443

---
# Pod Security Standards
apiVersion: v1
kind: Namespace
metadata:
  name: team-a
  labels:
    pod-security.kubernetes.io/enforce: baseline
    pod-security.kubernetes.io/enforce-version: v1.24
```

### High Availability
```yaml
# Multi-zone кластер
apiVersion: v1
kind: Pod
metadata:
  name: multi-zone-pod
spec:
  affinity:
    nodeAffinity:
      preferredDuringSchedulingIgnoredDuringExecution:
      - weight: 100
        preference:
          matchExpressions:
          - key: topology.kubernetes.io/zone
            operator: In
            values:
            - us-east-1a
      - weight: 50
        preference:
          matchExpressions:
          - key: topology.kubernetes.io/zone
            operator: In
            values:
            - us-east-1b
  topologySpreadConstraints:
  - maxSkew: 1
    topologyKey: topology.kubernetes.io/zone
    whenUnsatisfiable: DoNotSchedule
    labelSelector:
      matchLabels:
        app: myapp

---
# Pod Disruption Budget
apiVersion: policy/v1
kind: PodDisruptionBudget
metadata:
  name: myapp-pdb
spec:
  minAvailable: 2
  selector:
    matchLabels:
      app: myapp
```

## Решение проблем

### Распространенные проблемы
```bash
# Проверка состояния кластера
kubectl get nodes
kubectl get pods --all-namespaces
kubectl cluster-info

# Логи компонентов
kubectl logs -n kube-system kube-apiserver-control-plane
kubectl logs -n kube-system etcd-control-plane

# Проверка сетевых политик
kubectl get networkpolicies
kubectl describe networkpolicy <name>

# Диагностика DNS
kubectl run -it --rm debug --image=busybox -- nslookup kubernetes.default

# Проверка ресурсов
kubectl top nodes
kubectl top pods

# Events
kubectl get events --sort-by=.metadata.creationTimestamp
kubectl get events -n kube-system --field-selector type=Warning

# CrashLoopBackOff диагностика
kubectl describe pod <pod-name>
kubectl logs <pod-name> --previous

# Pending pods диагностика
kubectl describe pod <pod-name>
kubectl get nodes --show-labels
```

### Performance Tuning
```yaml
# Оптимизация API Server
apiVersion: v1
kind: ConfigMap
metadata:
  name: kube-apiserver-config
  namespace: kube-system
data:
  config.yaml: |
    apiVersion: apiserver.config.k8s.io/v1beta1
    kind: AdmissionConfiguration
    plugins:
    - name: PodSecurity
      configuration:
        apiVersion: pod-security.admission.config.k8s.io/v1beta1
        kind: PodSecurityConfiguration
        defaults:
          enforce: "baseline"
          enforce-version: "v1.24"
          audit: "baseline"
          audit-version: "v1.24"
          warn: "baseline"
          warn-version: "v1.24"

---
# Оптимизация kubelet
apiVersion: v1
kind: ConfigMap
metadata:
  name: kubelet-config
  namespace: kube-system
data:
  kubelet: |
    apiVersion: kubelet.config.k8s.io/v1beta1
    kind: KubeletConfiguration
    maxPods: 110
    maxOpenFiles: 1000000
    maxParallelImagePulls: 5
    serializeImagePulls: false
    eventRecordQPS: 10
    eventBurst: 20
    kubeAPIQPS: 50
    kubeAPIBurst: 100
    registryPullQPS: 10
    registryBurst: 20
```

### Backup и Recovery
```bash
# etcd backup
ETCDCTL_API=3 etcdctl --endpoints=https://127.0.0.1:2379 \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key \
  snapshot save /backup/etcd-snapshot.db

# etcd restore
ETCDCTL_API=3 etcdctl --endpoints=https://127.0.0.1:2379 \
  --cacert=/etc/kubernetes/pki/etcd/ca.crt \
  --cert=/etc/kubernetes/pki/etcd/server.crt \
  --key=/etc/kubernetes/pki/etcd/server.key \
  snapshot restore /backup/etcd-snapshot.db \
  --data-dir /var/lib/etcd-restore

# Velero для backup приложений
velero install \
  --provider aws \
  --plugins velero/velero-plugin-for-aws:v1.6.0 \
  --bucket velero-backups \
  --backup-location-config region=us-east-1 \
  --snapshot-location-config region=us-east-1

# Создание backup
velero backup create myapp-backup --include-namespaces production

# Restore
velero restore create --from-backup myapp-backup
```
## См. также
- [[docker-basics|Docker Basics]] — контейнеризация
- [[terraform|Terraform]] — инфраструктура как код
- [[ansible|Ansible]] — конфигурационное управление
- [[prometheus|Prometheus]] — мониторинг
