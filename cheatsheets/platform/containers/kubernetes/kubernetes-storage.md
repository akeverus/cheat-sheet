---
title: "Kubernetes Storage"
description: "Хранение данных в Kubernetes - это критически важная часть платформы, обеспечивающая persistence для stateful приложений. Kubernetes предоставляет абстракции для различных типов storage, от локальных дисков до облачных решений, с возможностью динамического provisioning и управлен"
tags:
  - platform
  - containers
  - kubernetes-storage
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Kubernetes Storage**

Хранение данных в **Kubernetes** — это критически важная часть платформы, обеспечивающая **persistence** для **stateful** приложений. **Kubernetes** предоставляет абстракции для различных типов **storage**, от локальных дисков до облачных решений, с возможностью динамического **provisioning** и управления жизненным циклом.

## Полезные ссылки
- [Kubernetes Storage](https://kubernetes.io/docs/concepts/storage/)
- [Persistent Volumes](https://kubernetes.io/docs/concepts/storage/persistent-volumes/)
- [Storage Classes](https://kubernetes.io/docs/concepts/storage/storage-classes/)
- [CSI Specification](https://kubernetes-csi.github.io/docs/) — **Container Storage Interface**
- [StatefulSets](https://kubernetes.io/docs/concepts/workloads/controllers/statefulset/)
- [Volume Snapshots](https://kubernetes.io/docs/concepts/storage/volume-snapshots/)

## Содержание

- [Volumes](#volumes)
  - [EmptyDir](#emptydir)
  - [HostPath](#hostpath)
  - [Persistent Volumes и Claims](#persistent-volumes-и-claims)
- [Storage Classes](#storage-classes)
  - [AWS EBS](#aws-ebs)
  - [GCP Persistent Disk](#gcp-persistent-disk)
  - [Azure Disk](#azure-disk)
  - [NFS](#nfs)
- [StatefulSets](#statefulsets)
  - [Базовый StatefulSet](#базовый-statefulset)
  - [StatefulSet с Headless Service](#statefulset-с-headless-service)
  - [Rolling Updates в StatefulSet](#rolling-updates-в-statefulset)
- [Volume Snapshots](#volume-snapshots)
  - [CSI Volume Snapshots](#csi-volume-snapshots)
- [Storage для разных workload паттернов](#storage-для-разных-workload-паттернов)
  - [Database Storage Pattern](#database-storage-pattern)
  - [Shared Storage Pattern](#shared-storage-pattern)
  - [Local Storage Pattern](#local-storage-pattern)
- [CSI Drivers](#csi-drivers)
  - [CSI Architecture](#csi-architecture)
  - [Custom CSI Driver](#custom-csi-driver)
- [Storage Security](#storage-security)
  - [Volume Encryption](#volume-encryption)
  - [RBAC для Storage](#rbac-для-storage)
- [Monitoring и Troubleshooting](#monitoring-и-troubleshooting)
  - [Storage Metrics](#storage-metrics)
  - [Диагностика проблем storage](#диагностика-проблем-storage)
- [Performance Optimization](#performance-optimization)
  - [Storage Performance Tuning](#storage-performance-tuning)
  - [Caching Strategies](#caching-strategies)
- [Backup и Disaster Recovery](#backup-и-disaster-recovery)
  - [Backup стратегии](#backup-стратегии)
  - [Disaster Recovery](#disaster-recovery)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также)

## **Volumes**

### **EmptyDir**

Ниже — пример **Pod** с **EmptyDir volume** (**YAML**).
```yaml
# EmptyDir — временное хранилище на узле (жизненный цикл пода)
apiVersion: v1
kind: Pod
metadata:
  name: emptydir-pod
spec:
  containers:
  - name: app
    image: busybox
    command: ["/bin/sh", "-c", "echo 'Hello' > /cache/hello.txt && sleep 3600"]
    volumeMounts:
    - name: cache-volume
      mountPath: /cache
  volumes:
  - name: cache-volume
    emptyDir: {}

# EmptyDir с memory medium (tmpfs)
apiVersion: v1
kind: Pod
metadata:
  name: memory-pod
spec:
  containers:
  - name: app
    image: nginx
    volumeMounts:
    - name: memory-volume
      mountPath: /tmp
  volumes:
  - name: memory-volume
    emptyDir:
      medium: Memory
      sizeLimit: 1Gi  # Ограничение размера
```

### **HostPath**
```yaml
# HostPath - монтирование директории с узла
apiVersion: v1
kind: Pod
metadata:
  name: hostpath-pod
spec:
  containers:
  - name: app
    image: nginx
    volumeMounts:
    - name: host-volume
      mountPath: /host-data
  volumes:
  - name: host-volume
    hostPath:
      path: /tmp/host-data
      type: DirectoryOrCreate  # Создать если не существует

# Типы HostPath
hostPath:
  path: /tmp/data
  type: Directory      # Должна существовать директория
hostPath:
  path: /tmp/data
  type: DirectoryOrCreate  # Создать директорию если нет
hostPath:
  path: /dev/sda1
  type: BlockDevice    # Блочное устройство
hostPath:
  path: /proc
  type: File           # Файл (не директория)
```

### **Persistent Volumes** и **Claims**

#### **Static Provisioning**
```yaml
# PersistentVolume (PV)
apiVersion: v1
kind: PersistentVolume
metadata:
  name: static-pv
  labels:
    type: local
spec:
  capacity:
    storage: 10Gi
  volumeMode: Filesystem  # Или Block
  accessModes:
    - ReadWriteOnce  # RWO, RWX, ROX
  persistentVolumeReclaimPolicy: Retain
  storageClassName: manual
  local:
    path: /mnt/data
  nodeAffinity:
    required:
      nodeSelectorTerms:
      - matchExpressions:
        - key: kubernetes.io/os
          operator: In
          values:
          - linux

# PersistentVolumeClaim (PVC)
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: static-pvc
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: manual
  resources:
    requests:
      storage: 5Gi

# Использование PVC в Pod
apiVersion: v1
kind: Pod
metadata:
  name: pvc-pod
spec:
  containers:
  - name: app
    image: nginx
    volumeMounts:
    - name: data
      mountPath: /usr/share/nginx/html
  volumes:
  - name: data
    persistentVolumeClaim:
      claimName: static-pvc
```

#### **Dynamic Provisioning**
```yaml
# StorageClass для dynamic provisioning
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: fast-ssd
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp3
  fsType: ext4
  encrypted: "true"
reclaimPolicy: Delete
allowVolumeExpansion: true
volumeBindingMode: WaitForFirstConsumer

# PVC с dynamic provisioning
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: dynamic-pvc
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: fast-ssd
  resources:
    requests:
      storage: 50Gi

# Проверка созданного PV
kubectl get pv
kubectl get pvc
kubectl describe pvc dynamic-pvc
```

## **Storage Classes**

### **AWS EBS**
```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: aws-ebs-gp3
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp3
  fsType: ext4
  encrypted: "true"
  kmsKeyId: alias/aws/ebs
  iops: "3000"
  throughput: "125"
reclaimPolicy: Delete
allowVolumeExpansion: true
volumeBindingMode: WaitForFirstConsumer
```

### **GCP Persistent Disk**
```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: gcp-pd-ssd
provisioner: kubernetes.io/gce-pd
parameters:
  type: pd-ssd
  fsType: ext4
  replication-type: none
reclaimPolicy: Delete
allowVolumeExpansion: true
volumeBindingMode: Immediate
```

### **Azure Disk**
```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: azure-disk-premium
provisioner: kubernetes.io/azure-disk
parameters:
  storageaccounttype: Premium_LRS
  kind: Managed
  fsType: ext4
reclaimPolicy: Delete
allowVolumeExpansion: true
volumeBindingMode: Immediate
```

### **NFS**
```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: nfs-storage
provisioner: kubernetes.io/nfs
parameters:
  server: nfs-server.example.com
  path: /exports
reclaimPolicy: Delete
allowVolumeExpansion: false
```

## **StatefulSets**

### Базовый **StatefulSet**
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  serviceName: web
  replicas: 3
  selector:
    matchLabels:
      app: web
  template:
    metadata:
      labels:
        app: web
    spec:
      containers:
      - name: nginx
        image: nginx:1.21
        ports:
        - containerPort: 80
          name: web
        volumeMounts:
        - name: www
          mountPath: /usr/share/nginx/html
  volumeClaimTemplates:
  - metadata:
    name: www
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: fast-ssd
      resources:
        requests:
          storage: 10Gi
```

### **StatefulSet** с **Headless Service**
```yaml
# Headless Service для StatefulSet
apiVersion: v1
kind: Service
metadata:
  name: web
  labels:
    app: web
spec:
  ports:
  - port: 80
    name: web
  clusterIP: None  # Headless service
  selector:
    app: web

# StatefulSet с Headless Service
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql
spec:
  serviceName: mysql
  replicas: 3
  selector:
    matchLabels:
      app: mysql
  template:
    metadata:
      labels:
        app: mysql
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        env:
        - name: MYSQL_ROOT_PASSWORD
          value: password
        ports:
        - containerPort: 3306
          name: mysql
        volumeMounts:
        - name: data
          mountPath: /var/lib/mysql
        - name: conf
          mountPath: /etc/mysql/conf.d
  volumeClaimTemplates:
  - metadata:
    name: data
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: fast-ssd
      resources:
        requests:
          storage: 100Gi
  - metadata:
    name: conf
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: standard
      resources:
        requests:
          storage: 1Gi
```

### **Rolling Updates** в **StatefulSet**
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  serviceName: web
  replicas: 3
  updateStrategy:
    type: RollingUpdate
    rollingUpdate:
      partition: 2  # Обновлять только replicas > partition
  selector:
    matchLabels:
      app: web
  template:
    metadata:
      labels:
        app: web
    spec:
      containers:
      - name: nginx
        image: nginx:1.21
        ports:
        - containerPort: 80
          name: web
```

## **Volume Snapshots**

### **CSI Volume Snapshots**
```yaml
# VolumeSnapshotClass
apiVersion: snapshot.storage.k8s.io/v1
kind: VolumeSnapshotClass
metadata:
  name: csi-aws-vsc
driver: ebs.csi.aws.com
deletionPolicy: Delete
parameters:
  csi.storage.k8s.io/snapshotter-secret-name: aws-secret
  csi.storage.k8s.io/snapshotter-secret-namespace: kube-system

# VolumeSnapshot
apiVersion: snapshot.storage.k8s.io/v1
kind: VolumeSnapshot
metadata:
  name: mysql-backup
spec:
  volumeSnapshotClassName: csi-aws-vsc
  source:
    persistentVolumeClaimName: mysql-data

# Восстановление из snapshot
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: mysql-restore
spec:
  storageClassName: fast-ssd
  dataSource:
    name: mysql-backup
    kind: VolumeSnapshot
    apiGroup: snapshot.storage.k8s.io
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 100Gi
```

## **Storage** для разных **workload** паттернов

### **Database Storage Pattern**
```yaml
# PostgreSQL с persistent storage
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
spec:
  serviceName: postgres
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15
        env:
        - name: POSTGRES_DB
          value: myapp
        - name: POSTGRES_USER
          value: app
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: postgres-secret
              key: password
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: data
          mountPath: /var/lib/postgresql/data
        - name: wal
          mountPath: /var/lib/postgresql/wal
        - name: backups
          mountPath: /backups
        livenessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - app
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - app
          initialDelaySeconds: 5
          periodSeconds: 5
  volumeClaimTemplates:
  - metadata:
    name: data
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: fast-ssd
      resources:
        requests:
          storage: 100Gi
  - metadata:
    name: wal
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: fast-ssd
      resources:
        requests:
          storage: 50Gi
  - metadata:
    name: backups
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: standard
      resources:
        requests:
          storage: 500Gi
```

### **Shared Storage Pattern**
```yaml
# NFS для shared storage
apiVersion: v1
kind: PersistentVolume
metadata:
  name: nfs-pv
spec:
  capacity:
    storage: 100Gi
  volumeMode: Filesystem
  accessModes:
    - ReadWriteMany
  persistentVolumeReclaimPolicy: Retain
  storageClassName: nfs
  nfs:
    server: nfs-server.example.com
    path: /exports/shared

# Использование ReadWriteMany
apiVersion: apps/v1
kind: Deployment
metadata:
  name: shared-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: shared-app
  template:
    metadata:
      labels:
        app: shared-app
    spec:
      containers:
      - name: app
        image: myapp:latest
        volumeMounts:
        - name: shared-data
          mountPath: /shared
      volumes:
      - name: shared-data
        persistentVolumeClaim:
          claimName: shared-pvc
```

### **Local Storage Pattern**
```yaml
# Local volumes для high-performance
apiVersion: v1
kind: PersistentVolume
metadata:
  name: local-pv-1
  labels:
    type: local
spec:
  capacity:
    storage: 500Gi
  volumeMode: Filesystem
  accessModes:
  - ReadWriteOnce
  persistentVolumeReclaimPolicy: Delete
  storageClassName: local-storage
  local:
    path: /mnt/disks/ssd1
  nodeAffinity:
    required:
      nodeSelectorTerms:
      - matchExpressions:
        - key: kubernetes.io/hostname
          operator: In
          values:
          - worker-node-1

# DaemonSet для подготовки local storage
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: local-storage-provisioner
  namespace: kube-system
spec:
  selector:
    matchLabels:
      app: local-storage-provisioner
  template:
    metadata:
      labels:
        app: local-storage-provisioner
    spec:
      containers:
      - name: provisioner
        image: k8s.gcr.io/sig-storage/local-volume-provisioner:v2.5.0
        env:
        - name: MY_NODE_NAME
          valueFrom:
            fieldRef:
              fieldPath: spec.nodeName
        volumeMounts:
        - name: local-storage
          mountPath: /mnt/disks
      volumes:
      - name: local-storage
        hostPath:
          path: /mnt/disks
```

## **CSI Drivers**

### **CSI Architecture**
```yaml
# CSI Driver deployment
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: csi-driver
  namespace: kube-system
spec:
  serviceName: csi-driver
  replicas: 1
  selector:
    matchLabels:
      app: csi-driver
  template:
    metadata:
      labels:
        app: csi-driver
    spec:
      serviceAccountName: csi-driver-sa
      containers:
      - name: csi-provisioner
        image: k8s.gcr.io/sig-storage/csi-provisioner:v3.3.0
        args:
        - --csi-address=$(ADDRESS)
        - --v=5
        env:
        - name: ADDRESS
          value: /csi/csi.sock
        volumeMounts:
        - name: socket-dir
          mountPath: /csi
      - name: csi-driver
        image: my-csi-driver:latest
        args:
        - --endpoint=$(ADDRESS)
        env:
        - name: ADDRESS
          value: unix:///csi/csi.sock
        volumeMounts:
        - name: socket-dir
          mountPath: /csi
        securityContext:
          privileged: true
      volumes:
      - name: socket-dir
        hostPath:
          path: /var/lib/kubelet/plugins/my-csi-driver
          type: DirectoryOrCreate
```

### **Custom CSI Driver**
```go
// Пример CSI driver на Go
package main

import (
    "context"
    "fmt"
    "path/filepath"

    "github.com/container-storage-interface/spec/lib/go/csi"
    "google.golang.org/grpc"
)

type MyDriver struct {
    name    string
    version string
}

func (d *MyDriver) CreateVolume(ctx context.Context, req *csi.CreateVolumeRequest) (*csi.CreateVolumeResponse, error) {
    volumeID := fmt.Sprintf("vol-%s", req.Name)

    // Логика создания volume
    // ...

    return &csi.CreateVolumeResponse{
        Volume: &csi.Volume{
            VolumeId:      volumeID,
            CapacityBytes: req.CapacityRange.RequiredBytes,
            VolumeContext: req.Parameters,
        },
    }, nil
}

func (d *MyDriver) DeleteVolume(ctx context.Context, req *csi.DeleteVolumeRequest) (*csi.DeleteVolumeResponse, error) {
    // Логика удаления volume
    return &csi.DeleteVolumeResponse{}, nil
}

func (d *MyDriver) ControllerPublishVolume(ctx context.Context, req *csi.ControllerPublishVolumeRequest) (*csi.ControllerPublishVolumeResponse, error) {
    // Логика attach volume к node
    return &csi.ControllerPublishVolumeResponse{}, nil
}

func (d *MyDriver) ControllerUnpublishVolume(ctx context.Context, req *csi.ControllerUnpublishVolumeRequest) (*csi.ControllerUnpublishVolumeResponse, error) {
    // Логика detach volume от node
    return &csi.ControllerUnpublishVolumeResponse{}, nil
}

func main() {
    driver := &MyDriver{
        name:    "my.csi.driver",
        version: "1.0.0",
    }

    server := grpc.NewServer()
    csi.RegisterIdentityServer(server, driver)
    csi.RegisterControllerServer(server, driver)

    // Запуск gRPC сервера
}
```

## **Storage Security**

### **Volume Encryption**
```yaml
# StorageClass с encryption
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: encrypted-storage
provisioner: kubernetes.io/aws-ebs
parameters:
  type: gp3
  fsType: ext4
  encrypted: "true"
  kmsKeyId: arn:aws:kms:us-east-1:123456789012:key/12345678-1234-1234-1234-123456789012

# PVC с encryption
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: encrypted-pvc
spec:
  storageClassName: encrypted-storage
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 50Gi
```

### **RBAC** для **Storage**
```yaml
# Role для storage operations
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: storage-admin
  namespace: production
rules:
- apiGroups: [""]
  resources: ["persistentvolumes", "persistentvolumeclaims"]
  verbs: ["get", "list", "watch", "create", "update", "patch", "delete"]
- apiGroups: ["storage.k8s.io"]
  resources: ["storageclasses"]
  verbs: ["get", "list", "watch"]
- apiGroups: ["snapshot.storage.k8s.io"]
  resources: ["volumesnapshots", "volumesnapshotcontents"]
  verbs: ["get", "list", "watch", "create", "update", "patch", "delete"]

# RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: storage-admin-binding
  namespace: production
subjects:
- kind: User
  name: storage-admin
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role
  name: storage-admin
  apiGroup: rbac.authorization.k8s.io
```

## **Monitoring** и **Troubleshooting**

### **Storage Metrics**
```yaml
# Prometheus metrics для storage
apiVersion: v1
kind: ConfigMap
metadata:
  name: storage-metrics
  namespace: monitoring
data:
  prometheus.yml: |
    scrape_configs:
    - job_name: 'kubernetes-pvcs'
      kubernetes_sd_configs:
      - role: endpoints
      relabel_configs:
      - source_labels: [__meta_kubernetes_service_name]
        regex: kubelet
        action: keep
      - source_labels: [__meta_kubernetes_endpoint_port_name]
        regex: http-metrics
        action: keep
      - source_labels: [__meta_kubernetes_endpoint_address_target_kind, __meta_kubernetes_endpoint_address_target_name]
        regex: Node;.+
        action: replace
        target_label: node
      - source_labels: [__meta_kubernetes_endpoint_address_target_name]
        regex: (.+)
        action: replace
        target_label: instance

# Grafana dashboard для storage
{
  "dashboard": {
    "title": "Kubernetes Storage",
    "panels": [
      {
        "title": "PVC Usage",
        "type": "table",
        "targets": [
          {
            "expr": "kubelet_volume_stats_used_bytes / kubelet_volume_stats_capacity_bytes",
            "legendFormat": "{{persistentvolumeclaim}}"
          }
        ]
      },
      {
        "title": "Storage Operations",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(kubelet_volume_stats_inodes_used[5m])",
            "legendFormat": "Inodes used - {{persistentvolumeclaim}}"
          }
        ]
      }
    ]
  }
}
```

### Диагностика проблем **storage**
```bash
# Проверка состояния PV/PVC
kubectl get pv,pvc
kubectl describe pvc my-pvc
kubectl describe pv my-pv

# Проверка storage classes
kubectl get storageclass
kubectl describe storageclass fast-ssd

# Логи CSI drivers
kubectl logs -n kube-system csi-provisioner-xxxxx
kubectl logs -n kube-system csi-driver-xxxxx

# Проверка mounts в pod
kubectl exec -it my-pod -- df -h
kubectl exec -it my-pod -- mount | grep /data

# Тестирование permissions
kubectl exec -it my-pod -- touch /data/test.txt
kubectl exec -it my-pod -- ls -la /data/

# Проверка events
kubectl get events --field-selector involvedObject.name=my-pvc
kubectl get events --field-selector involvedObject.kind=PersistentVolume

# Debug storage provisioning
kubectl describe pod csi-provisioner-xxxxx
kubectl logs -n kube-system csi-provisioner-xxxxx --previous
```

## **Performance Optimization**

### **Storage Performance Tuning**
```yaml
# StorageClass для high-performance
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: high-performance
provisioner: kubernetes.io/aws-ebs
parameters:
  type: io1
  iopsPerGB: "50"
  fsType: ext4
reclaimPolicy: Retain
volumeBindingMode: WaitForFirstConsumer

# Pod с storage performance optimizations
apiVersion: v1
kind: Pod
metadata:
  name: high-perf-pod
spec:
  containers:
  - name: app
    image: myapp:latest
    volumeMounts:
    - name: data
      mountPath: /data
    resources:
      requests:
        memory: 4Gi
        cpu: 2
      limits:
        memory: 8Gi
        cpu: 4
  volumes:
  - name: data
    persistentVolumeClaim:
      claimName: high-perf-pvc
  nodeSelector:
    storage: high-performance
  tolerations:
  - key: storage-type
    operator: Equal
    value: high-performance
    effect: NoSchedule
```

### **Caching Strategies**
```yaml
# Redis cache с persistent storage
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis-cache
spec:
  replicas: 1
  selector:
    matchLabels:
      app: redis-cache
  template:
    metadata:
      labels:
        app: redis-cache
    spec:
      containers:
      - name: redis
        image: redis:7-alpine
        command: ["redis-server", "--appendonly", "yes", "--save", "60", "1000"]
        ports:
        - containerPort: 6379
        volumeMounts:
        - name: data
          mountPath: /data
        - name: config
          mountPath: /etc/redis
        resources:
          requests:
            memory: 1Gi
            cpu: 500m
          limits:
            memory: 2Gi
            cpu: 1000m
      volumes:
      - name: data
        persistentVolumeClaim:
          claimName: redis-data
      - name: config
        configMap:
          name: redis-config

---
# PVC для Redis
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: redis-data
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: fast-ssd
  resources:
    requests:
      storage: 50Gi
```

## **Backup** и **Disaster Recovery**

### **Backup** стратегии
```yaml
# Velero для backup Kubernetes resources
apiVersion: velero.io/v1
kind: Backup
metadata:
  name: daily-backup
  namespace: velero
spec:
  includedNamespaces:
  - production
  includedResources:
  - persistentvolumeclaims
  - persistentvolumes
  - pods
  - deployments
  labelSelector:
    matchLabels:
      backup: daily
  ttl: 720h0m0s
  storageLocation: aws-backup-location
  volumeSnapshotLocations:
  - aws-snapshot-location

# CronJob для automated backups
apiVersion: batch/v1
kind: CronJob
metadata:
  name: daily-backup-job
spec:
  schedule: "0 2 * * *"  # Каждый день в 2:00
  jobTemplate:
    spec:
      template:
        spec:
          serviceAccountName: backup-sa
          containers:
          - name: backup
            image: velero/velero:v1.9.0
            command:
            - velero
            - backup
            - create
            - daily-$(date +%Y%m%d-%H%M%S)
            - --include-namespaces
            - production
            env:
            - name: VELERO_NAMESPACE
              value: velero
          restartPolicy: OnFailure
```

### **Disaster Recovery**
```yaml
# Multi-region backup strategy
apiVersion: velero.io/v1
kind: BackupStorageLocation
metadata:
  name: cross-region-backup
  namespace: velero
spec:
  provider: aws
  objectStorage:
    bucket: my-cross-region-backups
    prefix: velero
  config:
    region: us-west-2  # Другая region

---
# Restore procedure
apiVersion: velero.io/v1
kind: Restore
metadata:
  name: disaster-recovery
  namespace: velero
spec:
  backupName: daily-backup-20231201
  restorePVs: true
  includedNamespaces:
  - production
  namespaceMapping:
    production: production-dr
```

## Лучшие практики

- **Выбор типа тома: EmptyDir** для кэша и временных данных; **PVC** для постоянных данных; **ConfigMap**/**Secret** для конфигурации; не храните секреты в обычных томах.
- **StorageClass:** задавайте **default StorageClass**; используйте **reclaim policy** (**Retain для критичных данных**); подбирайте **provisioner** под облако или **on-premise**.
- **StatefulSet:** используйте для БД и **stateful**-приложений; именованные тома и стабильная сетевая идентичность; аккуратно с удалением **PVC** при удалении **StatefulSet**.
- **Бэкапы:** регулярные снапшоты **Volume Snapshot**; тесты восстановления; для критичных данных — репликация на другой кластер или облако.
- **Безопасность:** ограничение доступа к **CSI** и томам по **RBAC**; шифрование томов (**at-rest**) где возможно; не монтировать чувствительные тома в **read-only** где не нужно.
## См. также
- [[kubernetes-advanced|Kubernetes Advanced]] — продвинутые концепции **K8s**
- [[kubernetes-networking|Kubernetes Networking]] — сетевая подсистема
- [[terraform|Terraform]] — **Infrastructure as Code**
- [[docker-basics|Docker Basics]] — контейнеризация
