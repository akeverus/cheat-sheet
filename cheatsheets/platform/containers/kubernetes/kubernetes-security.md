---
title: "Kubernetes Security"
description: "Безопасность в Kubernetes - это многоуровневая дисциплина, охватывающая все аспекты платформы: от сетевой изоляции до управления доступом и защиты workloads. Kubernetes предоставляет встроенные механизмы безопасности, но их правильная конфигурация критически важна для production "
tags:
  - platform
  - containers
  - kubernetes-security
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Kubernetes Security**

Безопасность в **Kubernetes** — это многоуровневая дисциплина, охватывающая все аспекты платформы: от сетевой изоляции до управления доступом и защиты **workloads**. **Kubernetes** предоставляет встроенные механизмы безопасности, но их правильная конфигурация критически важна для **production** сред.

## Полезные ссылки
- [Kubernetes Security](https://kubernetes.io/docs/concepts/security/)
- [RBAC Authorization](https://kubernetes.io/docs/reference/access-authn-authz/rbac/)
- [Pod Security Standards](https://kubernetes.io/docs/concepts/security/pod-security-standards/)
- [Network Policies](https://kubernetes.io/docs/concepts/services-networking/network-policies/)
- [Admission Controllers](https://kubernetes.io/docs/reference/access-authn-authz/admission-controllers/)
- [OPA Gatekeeper](https://open-policy-agent.github.io/gatekeeper/website/docs/)
- [Kyverno](https://kyverno.io/docs/)
- [Falco](https://falco.org/docs/) — обнаружение аномалий

## Содержание

- [Authentication и Authorization](#authentication-и-authorization)
  - [Service Accounts](#service-accounts)
  - [RBAC (Role-Based Access Control)](#rbac-role-based-access-control)
  - [Certificate Management](#certificate-management)
- [Pod Security](#pod-security)
  - [Pod Security Standards](#pod-security-standards)
  - [Security Context](#security-context)
  - [AppArmor и SELinux](#apparmor-и-selinux)
- [Network Security](#network-security)
  - [Network Policies](#network-policies)
- [Image Security](#image-security)
  - [Image Scanning и Policies](#image-scanning-и-policies)
  - [Image Pull Secrets](#image-pull-secrets)
- [Runtime Security](#runtime-security)
  - [Falco для Runtime Detection](#falco-для-runtime-detection)
  - [Falco Rules](#falco-rules)
- [Secrets Management](#secrets-management)
  - [Kubernetes Secrets](#kubernetes-secrets)
  - [External Secrets Operator](#external-secrets-operator)
- [API Server Security](#api-server-security)
  - [API Server Configuration](#api-server-configuration)
  - [Admission Controllers](#admission-controllers)
- [Audit Logging](#audit-logging)
  - [API Server Audit](#api-server-audit)
- [Compliance и Governance](#compliance-и-governance)
  - [OPA Gatekeeper](#opa-gatekeeper)
  - [Kyverno Policies](#kyverno-policies)
- [Incident Response](#incident-response)
  - [Security Monitoring](#security-monitoring)
  - [Forensic Analysis](#forensic-analysis)
- [Лучшие практики](#лучшие-практики)
  - [Defense in Depth](#defense-in-depth)
  - [Security Scanning Pipeline](#security-scanning-pipeline)
  - [Compliance Automation](#compliance-automation)
- [См. также](#см-также)

## **Authentication** и **Authorization**

### **Service Accounts**

Ниже — пример **Service Account** и **Pod** с ним (**YAML**).
```yaml
# Создание Service Account
apiVersion: v1
kind: ServiceAccount
metadata:
  name: my-service-account
  namespace: production
automountServiceAccountToken: false  # Не монтировать токен автоматически

# Использование Service Account в Pod
apiVersion: v1
kind: Pod
metadata:
  name: secure-pod
spec:
  serviceAccountName: my-service-account
  automountServiceAccountToken: false
  containers:
  - name: app
    image: myapp:latest

# Проверка токена Service Account
kubectl get secrets -n production
kubectl describe secret my-service-account-token-xxxxx
```

### **RBAC** (**Role-`Based Access` Control**)
```yaml
# ClusterRole для cluster-wide доступа
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: cluster-admin
rules:
- apiGroups: ["*"]
  resources: ["*"]
  verbs: ["*"]

# Role для namespace-scoped доступа
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: pod-reader
  namespace: production
rules:
- apiGroups: [""]
  resources: ["pods", "pods/log"]
  verbs: ["get", "list", "watch"]

# ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: cluster-admin-binding
subjects:
- kind: User
  name: admin-user
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: ClusterRole
  name: cluster-admin
  apiGroup: rbac.authorization.k8s.io

# RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: pod-reader-binding
  namespace: production
subjects:
- kind: ServiceAccount
  name: my-service-account
  namespace: production
roleRef:
  kind: Role
  name: pod-reader
  apiGroup: rbac.authorization.k8s.io
```

### **Certificate Management**
```yaml
# Certificate Signing Request
apiVersion: certificates.k8s.io/v1
kind: CertificateSigningRequest
metadata:
  name: my-user
spec:
  request: LS0tLS1CRUdJTi...  # base64 encoded CSR
  signerName: kubernetes.io/kube-apiserver-client
  usages:
  - client auth

# Одобрение CSR
kubectl certificate approve my-user

# Получение сертификата
kubectl get csr my-user -o jsonpath='{.status.certificate}' | base64 -d > user.crt
```

## **Pod Security**

### **Pod Security Standards**
```yaml
# Применение Privileged PSS
apiVersion: v1
kind: Namespace
metadata:
  name: privileged-namespace
  labels:
    pod-security.kubernetes.io/enforce: privileged
    pod-security.kubernetes.io/enforce-version: v1.24
    pod-security.kubernetes.io/warn: privileged
    pod-security.kubernetes.io/warn-version: v1.24

# Baseline PSS для стандартных приложений
apiVersion: v1
kind: Namespace
metadata:
  name: baseline-namespace
  labels:
    pod-security.kubernetes.io/enforce: baseline
    pod-security.kubernetes.io/enforce-version: v1.24
    pod-security.kubernetes.io/audit: baseline
    pod-security.kubernetes.io/audit-version: v1.24
    pod-security.kubernetes.io/warn: baseline
    pod-security.kubernetes.io/warn-version: v1.24

# Restricted PSS для максимальной безопасности
apiVersion: v1
kind: Namespace
metadata:
  name: restricted-namespace
  labels:
    pod-security.kubernetes.io/enforce: restricted
    pod-security.kubernetes.io/enforce-version: v1.24
```

### **Security Context**
```yaml
# Pod-level Security Context
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
        add:
        - NET_BIND_SERVICE
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

### **AppArmor** и **SELinux**
```yaml
# AppArmor профиль
apiVersion: v1
kind: Pod
metadata:
  name: apparmor-pod
  annotations:
    container.apparmor.security.beta.kubernetes.io/app: localhost/my-app-profile
spec:
  containers:
  - name: app
    image: myapp:latest

# SELinux контекст
apiVersion: v1
kind: Pod
metadata:
  name: selinux-pod
spec:
  securityContext:
    seLinuxOptions:
      level: "s0:c123,c456"
  containers:
  - name: app
    image: myapp:latest
    securityContext:
      seLinuxOptions:
        level: "s0:c123,c456"
```

## **Network Security**

### **Network Policies**
```yaml
# Default deny all traffic
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: default-deny-all
  namespace: production
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress

# Allow internal communication
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-internal
  namespace: production
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          environment: production
    ports:
    - protocol: TCP
      port: 80
    - protocol: TCP
      port: 443

# Restrict database access
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: db-access
  namespace: production
spec:
  podSelector:
    matchLabels:
      app: postgres
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          tier: api
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

## **Image Security**

### **Image Scanning** и **Policies**
```yaml
# Kyverno ClusterPolicy для image validation
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata:
  name: check-image
spec:
  validationFailureAction: enforce
  background: false
  rules:
  - name: check-image-registry
    match:
      any:
      - resources:
          kinds:
          - Pod
    validate:
      message: "Images must be from trusted registry"
      pattern:
        spec:
          containers:
          - image: "trusted-registry.com/*"
  - name: check-image-signature
    match:
      any:
      - resources:
          kinds:
          - Pod
    verifyImages:
    - image: "*"
      key: |-
        -----BEGIN PUBLIC KEY-----
        MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...
        -----END PUBLIC KEY-----

# Admission Controller для image scanning
apiVersion: v1
kind: ConfigMap
metadata:
  name: image-scan-webhook
  namespace: kube-system
data:
  config.yaml: |
    admissionControllers:
    - ImagePolicyWebhook
    imagePolicy:
      kubeConfigFile: /etc/kubernetes/webhook.kubeconfig
      allowTTL: 50
      denyTTL: 50
      retryBackoff: 500
      defaultAllow: false
```

### **Image Pull Secrets**
```yaml
# Создание Docker registry secret
kubectl create secret docker-registry my-registry-secret \
  --docker-server=registry.example.com \
  --docker-username=myuser \
  --docker-password=mypass \
  --docker-email=myemail@example.com

# Использование в Service Account
apiVersion: v1
kind: ServiceAccount
metadata:
  name: build-service-account
  namespace: ci
imagePullSecrets:
- name: my-registry-secret

# Использование в Pod
apiVersion: v1
kind: Pod
metadata:
  name: private-image-pod
spec:
  containers:
  - name: app
    image: registry.example.com/myapp:latest
  imagePullSecrets:
  - name: my-registry-secret
```

## **Runtime Security**

### **Falco** для **Runtime Detection**
```yaml
# Falco DaemonSet
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: falco
  namespace: falco
spec:
  selector:
    matchLabels:
      app: falco
  template:
    metadata:
      labels:
        app: falco
    spec:
      serviceAccountName: falco
      containers:
      - name: falco
        image: falcosecurity/falco:0.35.1
        securityContext:
          privileged: true
        volumeMounts:
        - mountPath: /host/var/run/docker.sock
          name: docker-socket
        - mountPath: /host/dev
          name: dev-fs
          readOnly: true
        - mountPath: /host/proc
          name: proc-fs
          readOnly: true
        - mountPath: /host/boot
          name: boot-fs
          readOnly: true
        - mountPath: /host/lib/modules
          name: lib-modules
          readOnly: true
        - mountPath: /host/usr
          name: usr-fs
          readOnly: true
        - mountPath: /etc/falco
          name: config-volume
      volumes:
      - name: docker-socket
        hostPath:
          path: /var/run/docker.sock
      - name: dev-fs
        hostPath:
          path: /dev
      - name: proc-fs
        hostPath:
          path: /proc
      - name: boot-fs
        hostPath:
          path: /boot
      - name: lib-modules
        hostPath:
          path: /lib/modules
      - name: usr-fs
        hostPath:
          path: /usr
      - name: config-volume
        configMap:
          name: falco-config
```

### **Falco Rules**
```yaml
# Custom Falco rules
- rule: Unauthorized process
  desc: Detect unauthorized processes
  condition: spawned_process and not (proc.name in (allowed_processes))
  output: Unauthorized process %proc.name started by user %user.name
  priority: WARNING

- rule: Shell in container
  desc: Detect shell execution in container
  condition: spawned_process and container and proc.name = bash
  output: Shell spawned in container %container.name
  priority: WARNING

- rule: Network connection to suspicious IP
  desc: Detect connections to suspicious IPs
  condition: outbound and fd.sip in (suspicious_ips)
  output: Outbound connection to suspicious IP %fd.sip
  priority: CRITICAL

- rule: File access in sensitive directory
  desc: Detect file access in sensitive directories
  condition: open_read and container and (fd.name startswith /etc/passwd or fd.name startswith /etc/shadow)
  output: Sensitive file accessed %fd.name
  priority: WARNING
```

## **Secrets Management**

### **Kubernetes Secrets**
```yaml
# Opaque secret
apiVersion: v1
kind: Secret
metadata:
  name: my-secret
  namespace: production
type: Opaque
data:
  username: bXlzcWx1c2Vy  # base64 encoded
  password: bXlzcWxwYXNz  # base64 encoded

# TLS secret
apiVersion: v1
kind: Secret
metadata:
  name: tls-secret
type: kubernetes.io/tls
data:
  tls.crt: LS0tLS1CRUdJTi...  # base64 encoded certificate
  tls.key: LS0tLS1CRUdJTi...  # base64 encoded private key

# Использование секретов в Pod
apiVersion: v1
kind: Pod
metadata:
  name: secret-pod
spec:
  containers:
  - name: app
    image: myapp:latest
    env:
    - name: DB_USERNAME
      valueFrom:
        secretKeyRef:
          name: my-secret
          key: username
    - name: DB_PASSWORD
      valueFrom:
        secretKeyRef:
          name: my-secret
          key: password
    volumeMounts:
    - name: tls-certs
      mountPath: /etc/ssl/certs
      readOnly: true
  volumes:
  - name: tls-certs
    secret:
      secretName: tls-secret
```

### **External Secrets Operator**
```yaml
# ExternalSecret для AWS Secrets Manager
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: aws-secrets
  namespace: production
spec:
  refreshInterval: 15s
  secretStoreRef:
    name: aws-secretsmanager
    kind: SecretStore
  target:
    name: aws-secret
    creationPolicy: Owner
  data:
  - secretKey: db-password
    remoteRef:
      key: prod/database
      property: password

# SecretStore
apiVersion: external-secrets.io/v1beta1
kind: SecretStore
metadata:
  name: aws-secretsmanager
  namespace: production
spec:
  provider:
    aws:
      service: SecretsManager
      region: us-east-1
      auth:
        jwt:
          serviceAccountRef:
            name: external-secrets-sa
```

## **API Server Security**

### **API Server Configuration**
```yaml
# Secure API Server flags
apiVersion: v1
kind: Pod
metadata:
  name: kube-apiserver
  namespace: kube-system
spec:
  containers:
  - command:
    - kube-apiserver
    - --advertise-address=192.168.1.100
    - --allow-privileged=true
    - --authorization-mode=Node,RBAC
    - --client-ca-file=/etc/kubernetes/pki/ca.crt
    - --enable-admission-plugins=NamespaceLifecycle,LimitRanger,ServiceAccount,DefaultStorageClass,DefaultTolerationSeconds,MutatingAdmissionWebhook,ValidatingAdmissionWebhook,ResourceQuota,PodSecurity
    - --etcd-cafile=/etc/kubernetes/pki/etcd/ca.crt
    - --etcd-certfile=/etc/kubernetes/pki/apiserver-etcd-client.crt
    - --etcd-keyfile=/etc/kubernetes/pki/apiserver-etcd-client.key
    - --etcd-servers=https://127.0.0.1:2379
    - --kubelet-client-certificate=/etc/kubernetes/pki/apiserver-kubelet-client.crt
    - --kubelet-client-key=/etc/kubernetes/pki/apiserver-kubelet-client.key
    - --kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname
    - --requestheader-allowed-names=front-proxy-client
    - --requestheader-client-ca-file=/etc/kubernetes/pki/front-proxy-ca.crt
    - --requestheader-extra-headers-prefix=X-Remote-Extra-
    - --requestheader-group-headers=X-Remote-Group
    - --requestheader-username-headers=X-Remote-User
    - --secure-port=6443
    - --service-account-issuer=https://kubernetes.default.svc.cluster.local
    - --service-account-key-file=/etc/kubernetes/pki/sa.pub
    - --service-account-signing-key-file=/etc/kubernetes/pki/sa.key
    - --service-cluster-ip-range=10.96.0.0/12
    - --tls-cert-file=/etc/kubernetes/pki/apiserver.crt
    - --tls-private-key-file=/etc/kubernetes/pki/apiserver.key
```

### **Admission Controllers**
```yaml
# Custom Admission Controller
apiVersion: admissionregistration.k8s.io/v1
kind: ValidatingWebhookConfiguration
metadata:
  name: pod-validation-webhook
webhooks:
- name: pod-validation.example.com
  clientConfig:
    service:
      name: pod-validator
      namespace: security
      path: "/validate"
    caBundle: LS0tLS1CRUdJTi...
  rules:
  - operations: ["CREATE", "UPDATE"]
    apiGroups: [""]
    apiVersions: ["v1"]
    resources: ["pods"]
  admissionReviewVersions: ["v1", "v1beta1"]
  sideEffects: None

# Mutating Admission Webhook
apiVersion: admissionregistration.k8s.io/v1
kind: MutatingWebhookConfiguration
metadata:
  name: pod-mutating-webhook
webhooks:
- name: pod-mutator.example.com
  clientConfig:
    service:
      name: pod-mutator
      namespace: security
      path: "/mutate"
    caBundle: LS0tLS1CRUdJTi...
  rules:
  - operations: ["CREATE"]
    apiGroups: [""]
    apiVersions: ["v1"]
    resources: ["pods"]
  admissionReviewVersions: ["v1", "v1beta1"]
  sideEffects: None
```

## **Audit Logging**

### **API Server Audit**
```yaml
# Audit policy
apiVersion: audit.k8s.io/v1
kind: Policy
rules:
- level: Metadata
  verbs: ["get", "list", "watch"]
  resources:
  - group: ""
    resources: ["secrets"]
- level: RequestResponse
  verbs: ["create", "update", "patch", "delete"]
  resources:
  - group: ""
    resources: ["secrets"]
- level: Metadata
  verbs: ["get", "list", "watch"]
  resources:
  - group: ""
    resources: ["pods", "deployments", "services"]
- level: None
  userGroups: ["system:serviceaccounts"]
  verbs: ["get"]
  resources:
  - group: ""
    resources: ["configmaps"]

# Audit webhook
apiVersion: v1
kind: ConfigMap
metadata:
  name: audit-webhook
  namespace: kube-system
data:
  audit-webhook-config: |
    apiVersion: v1
    kind: Config
    clusters:
    - cluster:
        certificate-authority: /etc/kubernetes/pki/ca.crt
        server: https://audit-server.security.svc.cluster.local:443
      name: audit-server
    contexts:
    - context:
        cluster: audit-server
        user: audit-webhook
      name: audit-webhook
    current-context: audit-webhook
    users:
    - name: audit-webhook
      user:
        client-certificate: /etc/kubernetes/pki/audit-webhook.crt
        client-key: /etc/kubernetes/pki/audit-webhook.key
```

## **Compliance** и **Governance**

### **OPA Gatekeeper**
```yaml
# Constraint Template
apiVersion: templates.gatekeeper.sh/v1beta1
kind: ConstraintTemplate
metadata:
  name: k8srequiredlabels
spec:
  crd:
    spec:
      names:
        kind: K8sRequiredLabels
  targets:
  - target: admission.k8s.gatekeeper.sh
    rego: |
      package k8srequiredlabels

      violation[{"msg": msg, "details": {"missing_labels": missing}}] {
        provided := {label | input.review.object.metadata.labels[label]}
        required := {label | label := input.parameters.labels[_]}
        missing := required - provided
        count(missing) > 0
        msg := sprintf("you must provide labels: %v", [missing])
      }

# Constraint
apiVersion: constraints.gatekeeper.sh/v1beta1
kind: K8sRequiredLabels
metadata:
  name: ns-must-have-owner
spec:
  match:
    kinds:
    - apiGroups: [""]
      kinds: ["Namespace"]
  parameters:
    labels:
    - owner
```

### **Kyverno Policies**
```yaml
# Kyverno ClusterPolicy
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata:
  name: restrict-image-registries
spec:
  validationFailureAction: enforce
  rules:
  - name: restrict-image-registries
    match:
      any:
      - resources:
          kinds:
          - Pod
    validate:
      message: "Images must be from approved registries"
      pattern:
        spec:
          containers:
          - image: "registry.example.com/*"
  - name: require-resource-limits
    match:
      any:
      - resources:
          kinds:
          - Pod
    validate:
      message: "All containers must have resource limits"
      pattern:
        spec:
          containers:
          - resources:
              limits:
                memory: "?*"
                cpu: "?*"
```

## **Incident Response**

### **Security Monitoring**
```yaml
# Prometheus rules для security alerts
groups:
- name: security_alerts
  rules:
  - alert: PodSecurityViolation
    expr: rate(kube_pod_container_status_restarts_total[5m]) > 5
    for: 10m
    labels:
      severity: warning
    annotations:
      summary: "Pod restarting frequently"
      description: "Pod {{ $labels.pod }} is restarting more than 5 times per 5 minutes"

  - alert: UnauthorizedAPIAccess
    expr: rate(apiserver_request_total{code="403"}[5m]) > 10
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "High rate of unauthorized API requests"
      description: "Unauthorized API access rate is {{ $value }} per second"

  - alert: SuspiciousNetworkTraffic
    expr: rate(kube_network_traffic[5m]) > 1000000
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: "Suspicious network traffic detected"
      description: "Network traffic exceeds normal threshold"
```

### **Forensic Analysis**
```bash
# Сбор forensic данных
kubectl get events --all-namespaces --sort-by=.metadata.creationTimestamp > security-events.log
kubectl get pods --all-namespaces -o json > pod-state.json
kubectl get networkpolicies --all-namespaces -o yaml > network-policies.yaml

# Анализ audit logs
kubectl logs -n kube-system -l k8s-app=kube-apiserver --tail=1000 | grep -i security

# Проверка network flows
kubectl get networkpolicies --all-namespaces
kubectl describe networkpolicy suspicious-policy

# Container forensics
kubectl exec suspicious-pod -- find / -name "*.log" -exec cat {} \;
kubectl exec suspicious-pod -- netstat -tlnp
kubectl exec suspicious-pod -- ps aux
```

## Лучшие практики

### **Defense in Depth**
```yaml
# Multi-layer security approach
apiVersion: v1
kind: Namespace
metadata:
  name: secure-app
  labels:
    pod-security.kubernetes.io/enforce: restricted
    pod-security.kubernetes.io/enforce-version: v1.24
---
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: app-isolation
  namespace: secure-app
spec:
  podSelector:
    matchLabels:
      app: secure-app
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          role: ingress
    ports:
    - protocol: TCP
      port: 80
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: app-role
  namespace: secure-app
rules:
- apiGroups: [""]
  resources: ["pods", "configmaps"]
  verbs: ["get", "list"]
---
apiVersion: v1
kind: Pod
metadata:
  name: secure-pod
spec:
  serviceAccountName: restricted-sa
  securityContext:
    runAsUser: 1000
    runAsNonRoot: true
    fsGroup: 1000
  containers:
  - name: app
    image: secure-registry.com/app:v1.0
    securityContext:
      allowPrivilegeEscalation: false
      readOnlyRootFilesystem: true
      capabilities:
        drop: ["ALL"]
    resources:
      limits:
        memory: 256Mi
        cpu: 200m
```

### **Security Scanning Pipeline**
```yaml
# GitHub Actions security pipeline
name: Security Scan

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  security:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Run Trivy vulnerability scanner
      uses: aquasecurity/trivy-action@master
      with:
        scan-type: 'config'
        scan-ref: './k8s'
        format: 'sarif'
        output: 'trivy-results.sarif'

    - name: Run Kube-bench
      uses: aquasecurity/kube-bench-action@v1.0.0
      with:
        kubeconfig: ${{ secrets.KUBECONFIG }}

    - name: Upload Trivy scan results
      uses: github/codeql-action/upload-sarif@v2
      if: always()
      with:
        sarif_file: 'trivy-results.sarif'

    - name: Security audit
      run: |
        # Check for security misconfigurations
        kubeconform -kubernetes-version 1.24.0 k8s/
        conftest test k8s/ --policy ./policies
```

### **Compliance Automation**
```yaml
# CIS Kubernetes Benchmark automation
apiVersion: batch/v1
kind: CronJob
metadata:
  name: cis-compliance-check
  namespace: security
spec:
  schedule: "0 2 * * 1"  # Every Monday at 2 AM
  jobTemplate:
    spec:
      template:
        spec:
          serviceAccountName: compliance-sa
          containers:
          - name: kube-bench
            image: aquasecurity/kube-bench:latest
            command: ["kube-bench", "--json"]
            volumeMounts:
            - name: var-lib-kubelet
              mountPath: /var/lib/kubelet
              readOnly: true
            - name: etc-systemd
              mountPath: /etc/systemd
              readOnly: true
          volumes:
          - name: var-lib-kubelet
            hostPath:
              path: /var/lib/kubelet
          - name: etc-systemd
            hostPath:
              path: /etc/systemd
          restartPolicy: Never
```
## См. также
- [[kubernetes-advanced|Kubernetes Advanced]] — продвинутые концепции **K8s**
- [[kubernetes-networking|Kubernetes Networking]] — сетевая подсистема
- [[kubernetes-storage|Kubernetes Storage]] — хранение данных
- [[prometheus|Prometheus]] — мониторинг
- [[terraform|Terraform]] — **Infrastructure as Code**
