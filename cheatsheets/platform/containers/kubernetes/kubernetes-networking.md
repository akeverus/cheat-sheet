---
title: "Kubernetes Networking"
description: "Kubernetes networking - это сложная и важная часть платформы, обеспечивающая коммуникацию между подами, сервисами и внешними системами. Сеть в Kubernetes построена на принципах плоскости управления (control plane) и плоскости данных (data plane), обеспечивая масштабируемость, без"
tags:
  - platform
  - containers
  - kubernetes-networking
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# **Kubernetes Networking**

**Kubernetes networking** - это сложная и важная часть платформы, обеспечивающая коммуникацию между подами, сервисами и внешними системами. Сеть в **Kubernetes** построена на принципах плоскости управления (**control plane**) и плоскости данных (**data plane**), обеспечивая масштабируемость, безопасность и гибкость.

## Полезные ссылки
- [Kubernetes Networking](https://kubernetes.io/docs/concepts/cluster-administration/networking/)
- [Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/) — **Ingress** и контроллеры
- [Network Policies](https://kubernetes.io/docs/concepts/services-networking/network-policies/)
- [Service Mesh (Istio)](https://istio.io/latest/docs/) — **Service Mesh**
- [CNI (Container Network Interface)](https://github.com/containernetworking/cni) — плагины **CNI**

## Содержание

- [Основные концепции сети](#основные-концепции-сети)
  - [Pod networking](#pod-networking)
  - [Service networking](#service-networking)
- [Ingress контроллеры](#ingress-контроллеры)
  - [Nginx Ingress Controller](#nginx-ingress-controller)
  - [Traefik Ingress](#traefik-ingress)
- [Network Policies](#network-policies)
  - [Основы Network Policies](#основы-network-policies)
  - [Продвинутые Network Policies](#продвинутые-network-policies)
- [Service Mesh (Istio)](#service-mesh-istio)
  - [Установка Istio](#установка-istio)
  - [Traffic Management](#traffic-management)
- [DNS в Kubernetes](#dns-в-kubernetes)
  - [CoreDNS конфигурация](#coredns-конфигурация)
  - [External DNS](#external-dns)
- [Load Balancing](#load-balancing)
  - [MetalLB для bare metal](#metallb-для-bare-metal)
  - [HAProxy Ingress](#haproxy-ingress)
- [Network Troubleshooting](#network-troubleshooting)
  - [Диагностика сетевых проблем](#диагностика-сетевых-проблем)
  - [Network debugging tools](#network-debugging-tools)
- [CNI плагины](#cni-плагины)
  - [Calico](#calico)
  - [Flannel](#flannel)
  - [Cilium](#cilium)
- [IPv6 поддержка](#ipv6-поддержка)
  - [Dual-stack конфигурация](#dual-stack-конфигурация)
- [Service Discovery](#service-discovery)
  - [EndpointSlices](#endpointslices)
  - [Headless Services](#headless-services)
- [Performance и масштабирование](#performance-и-масштабирование)
  - [Network performance tuning](#network-performance-tuning)
  - [Load balancing optimization](#load-balancing-optimization)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также)

## Основные концепции сети

### **Pod networking**

Ниже — пример **Pod** с сетевым доступом (**YAML**).
```yaml
# Каждый под получает уникальный IP адрес в кластерной сети
apiVersion: v1
kind: Pod
metadata:
  name: nginx-pod
  labels:
    app: nginx
spec:
  containers:
  - name: nginx
    image: nginx:alpine
    ports:
    - containerPort: 80
      protocol: TCP

# Проверка IP подов
kubectl get pods -o wide
kubectl describe pod nginx-pod | grep IP

# Коммуникация между подами
kubectl run busybox --image=busybox --rm -it -- wget -O- http://nginx-pod:80
```

### **Service networking**
```yaml
# ClusterIP Service - внутренний доступ
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
  labels:
    app: nginx
spec:
  selector:
    app: nginx
  ports:
  - port: 80
    targetPort: 80
    protocol: TCP
  type: ClusterIP

# NodePort Service - внешний доступ через порт узла
apiVersion: v1
kind: Service
metadata:
  name: nginx-nodeport
spec:
  selector:
    app: nginx
  ports:
  - port: 80
    targetPort: 80
    nodePort: 30080
    protocol: TCP
  type: NodePort

# LoadBalancer Service - облачный балансировщик
apiVersion: v1
kind: Service
metadata:
  name: nginx-lb
  annotations:
    service.beta.kubernetes.io/aws-load-balancer-type: nlb
spec:
  selector:
    app: nginx
  ports:
  - port: 80
    targetPort: 80
    protocol: TCP
  type: LoadBalancer
  loadBalancerIP: 192.168.1.100  # Опционально для on-prem

# ExternalName Service - CNAME redirect
apiVersion: v1
kind: Service
metadata:
  name: external-db
spec:
  type: ExternalName
  externalName: database.example.com
```

## **Ingress** контроллеры

### **Nginx Ingress Controller**
```yaml
# Установка Nginx Ingress Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.7.0/deploy/static/provider/cloud/deploy.yaml

# Проверка установки
kubectl get pods -n ingress-nginx
kubectl get svc -n ingress-nginx

# Базовый Ingress
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: nginx-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  ingressClassName: nginx
  rules:
  - host: myapp.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: nginx-service
            port:
              number: 80

# Ingress с TLS
apiVersion: v1
kind: Secret
metadata:
  name: tls-secret
type: kubernetes.io/tls
data:
  tls.crt: LS0tLS1CRUdJTi...  # base64 encoded certificate
  tls.key: LS0tLS1CRUdJTi...  # base64 encoded private key

---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: tls-ingress
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  ingressClassName: nginx
  tls:
  - hosts:
    - secure.example.com
    secretName: tls-secret
  rules:
  - host: secure.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: secure-app
            port:
              number: 443
```

### **Traefik Ingress**
```yaml
# Установка Traefik через Helm
helm repo add traefik https://helm.traefik.io/traefik
helm install traefik traefik/traefik

# Traefik IngressRoute
apiVersion: traefik.containo.us/v1alpha1
kind: IngressRoute
metadata:
  name: myapp-route
spec:
  entryPoints:
  - websecure
  routes:
  - match: Host(`myapp.example.com`) && PathPrefix(`/api`)
    kind: Rule
    services:
    - name: api-service
      port: 80
    middlewares:
    - name: api-stripprefix
  - match: Host(`myapp.example.com`)
    kind: Rule
    services:
    - name: web-service
      port: 80
  tls:
    certResolver: letsencrypt

# Middleware для Traefik
apiVersion: traefik.containo.us/v1alpha1
kind: Middleware
metadata:
  name: api-stripprefix
spec:
  stripPrefix:
    prefixes:
    - /api

---
apiVersion: traefik.containo.us/v1alpha1
kind: Middleware
metadata:
  name: rate-limit
spec:
  rateLimit:
    burst: 100
    average: 50
```

## **Network Policies**

### Основы **Network Policies**
```yaml
# Базовая изоляция - запретить весь трафик
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

# Разрешить трафик между подами в namespace
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
    - podSelector: {}
    ports:
    - protocol: TCP
      port: 80
    - protocol: TCP
      port: 443

# Доступ к внешним сервисам
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-external
  namespace: production
spec:
  podSelector:
    matchLabels:
      app: web
  policyTypes:
  - Egress
  egress:
  - to:
    - ipBlock:
        cidr: 10.0.0.0/8
    - ipBlock:
        cidr: 172.16.0.0/12
    - ipBlock:
        cidr: 192.168.0.0/16
    ports:
    - protocol: TCP
      port: 443
  - to: []
    ports:
    - protocol: UDP
      port: 53  # DNS
```

### Продвинутые **Network Policies**
```yaml
# Multi-tier приложение
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: web-to-api
  namespace: production
spec:
  podSelector:
    matchLabels:
      tier: api
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          tier: web
    ports:
    - protocol: TCP
      port: 8080

---
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: api-to-db
  namespace: production
spec:
  podSelector:
    matchLabels:
      tier: database
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

# Изоляция namespace
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: namespace-isolation
  namespace: production
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          environment: production
  egress:
  - to:
    - namespaceSelector:
        matchLabels:
          name: kube-system
    ports:
    - protocol: UDP
      port: 53
  - to:
    - ipBlock:
        cidr: 0.0.0.0/0
    ports:
    - protocol: TCP
      port: 443

# Network Policy для LoadBalancer
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: lb-access
spec:
  podSelector:
    matchLabels:
      app: nginx
  policyTypes:
  - Ingress
  ingress:
  - from:
    - ipBlock:
        cidr: 192.168.1.0/24  # Load balancer subnet
    ports:
    - protocol: TCP
      port: 80
```

## **Service Mesh** (**Istio**)

### Установка **Istio**
```bash
# Скачивание Istio
curl -L https://istio.io/downloadIstio | sh -
cd istio-1.17.0

# Установка через istioctl
istioctl install --set profile=default -y

# Включение injection для namespace
kubectl label namespace default istio-injection=enabled

# Установка Kiali, Jaeger, Prometheus
kubectl apply -f samples/addons/
```

### **Traffic Management**
```yaml
# Virtual Service для маршрутизации
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: reviews-route
spec:
  hosts:
  - reviews
  http:
  - match:
    - headers:
        end-user:
          exact: jason
    route:
    - destination:
        host: reviews
        subset: v2
  - route:
    - destination:
        host: reviews
        subset: v1

# Destination Rule для subsets
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: reviews-destination
spec:
  host: reviews
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2
    trafficPolicy:
      loadBalancer:
        simple: RANDOM

# Gateway для внешнего трафика
apiVersion: networking.istio.io/v1beta1
kind: Gateway
metadata:
  name: bookinfo-gateway
spec:
  selector:
    istio: ingressgateway
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - "*"

# Peer Authentication для mTLS
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
  namespace: production
spec:
  mtls:
    mode: STRICT
```

## **DNS** в **Kubernetes**

### **CoreDNS** конфигурация
```yaml
# ConfigMap для CoreDNS
apiVersion: v1
kind: ConfigMap
metadata:
  name: coredns
  namespace: kube-system
data:
  Corefile: |
    .:53 {
        errors
        health {
            lameduck 5s
        }
        ready
        kubernetes cluster.local in-addr.arpa ip6.arpa {
            pods insecure
            fallthrough in-addr.arpa ip6.arpa
            ttl 30
        }
        prometheus :9153
        forward . /etc/resolv.conf {
            max_concurrent 1000
        }
        cache 30
        loop
        reload
        loadbalance
    }
    staging.cluster.local:53 {
        errors
        cache 30
        forward . 10.0.0.10
    }
```

### **External DNS**
```yaml
# Установка External DNS
kubectl apply -f https://raw.githubusercontent.com/kubernetes-sigs/external-dns/master/docs/tutorials/aws.md

# Service с external-dns аннотациями
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
  annotations:
    external-dns.alpha.kubernetes.io/hostname: myapp.example.com
    external-dns.alpha.kubernetes.io/ttl: "300"
spec:
  selector:
    app: nginx
  ports:
  - port: 80
    targetPort: 80
  type: LoadBalancer

# Ingress с external-dns
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: nginx-ingress
  annotations:
    external-dns.alpha.kubernetes.io/hostname: api.example.com
spec:
  rules:
  - host: api.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: api-service
            port:
              number: 80
```

## **Load Balancing**

### **MetalLB** для **bare metal**
```yaml
# Установка MetalLB
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.13.7/config/manifests/metallb-native.yaml

# Конфигурация IP pool
apiVersion: metallb.io/v1beta1
kind: IPAddressPool
metadata:
  name: first-pool
  namespace: metallb-system
spec:
  addresses:
  - 192.168.1.240-192.168.1.250

---
apiVersion: metallb.io/v1beta1
kind: L2Advertisement
metadata:
  name: l2-advertisement
  namespace: metallb-system
spec:
  ipAddressPools:
  - first-pool
```

### **HAProxy Ingress**
```yaml
# Установка HAProxy Ingress
helm repo add haproxytech https://haproxytech.github.io/helm-charts
helm install haproxy haproxytech/kubernetes-ingress \
  --set controller.service.type=LoadBalancer \
  --set controller.service.loadBalancerIP=192.168.1.100

# Custom конфигурация
apiVersion: v1
kind: ConfigMap
metadata:
  name: haproxy-config
  namespace: haproxy-controller
data:
  global: |
    tune.ssl.default-dh-param 2048
  defaults: |
    timeout connect 5s
    timeout client 30s
    timeout server 30s
```

## **Network Troubleshooting**

### Диагностика сетевых проблем
```bash
# Проверка сетевых плагинов
kubectl get pods -n kube-system -l k8s-app=kube-proxy
kubectl get pods -n kube-system -l k8s-app=flannel
kubectl get pods -n kube-system -l k8s-app=calico-node

# Проверка DNS
kubectl run busybox --image=busybox --rm -it -- nslookup kubernetes.default

# Проверка сетевой connectivity
kubectl run busybox --image=busybox --rm -it -- ping 10.96.0.1

# Проверка iptables правил
kubectl run busybox --image=busybox --rm -it -- iptables -L -n

# Сетевые логи
kubectl logs -n kube-system kube-proxy-xxxxx
kubectl logs -n kube-flannel flannel-xxxxx
```

### **Network debugging tools**
```yaml
# Debug pod с сетевыми инструментами
apiVersion: v1
kind: Pod
metadata:
  name: network-debug
spec:
  containers:
  - name: debug
    image: nicolaka/netshoot
    command:
    - sleep
    - infinity
  nodeSelector:
    kubernetes.io/os: linux

# Использование debug container
kubectl exec -it network-debug -- sh

# Внутри debug container
# Проверка маршрутов
ip route
ip addr

# Проверка DNS
nslookup kubernetes.default.svc.cluster.local

# Traceroute
traceroute 10.96.0.1

# TCP dump
tcpdump -i eth0 -n port 80

# Netcat для тестирования
nc -zv service-name.namespace 80
```

## **CNI** плагины

### **Calico**
```yaml
# Установка Calico
kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml

# Конфигурация Calico
apiVersion: projectcalico.org/v3
kind: IPPool
metadata:
  name: default-ipv4
spec:
  cidr: 10.244.0.0/16
  ipipMode: Always
  natOutgoing: true

# BGP конфигурация
apiVersion: projectcalico.org/v3
kind: BGPConfiguration
metadata:
  name: default
spec:
  logSeverityScreen: Info
  nodeToNodeMeshEnabled: true
  asNumber: 64512

# Network Policy в Calico
apiVersion: projectcalico.org/v3
kind: NetworkPolicy
metadata:
  name: allow-tcp-6379
  namespace: production
spec:
  selector: role == 'database'
  types:
  - Ingress
  - Egress
  ingress:
  - action: Allow
    protocol: TCP
    source:
      selector: role == 'frontend'
    destination:
      ports:
      - 6379
  egress:
  - action: Allow
```

### **Flannel**
```yaml
# Установка Flannel
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

# Кастомная конфигурация Flannel
apiVersion: v1
kind: ConfigMap
metadata:
  name: kube-flannel-cfg
  namespace: kube-system
data:
  cni-conf.json: |
    {
      "name": "cbr0",
      "cniVersion": "0.3.1",
      "plugins": [
        {
          "type": "flannel",
          "delegate": {
            "hairpinMode": true,
            "isDefaultGateway": true
          }
        },
        {
          "type": "portmap",
          "capabilities": {
            "portMappings": true
          }
        }
      ]
    }
  net-conf.json: |
    {
      "Network": "10.244.0.0/16",
      "Backend": {
        "Type": "vxlan"
      }
    }
```

### **Cilium**
```bash
# Установка Cilium через Helm
helm repo add cilium https://helm.cilium.io/
helm install cilium cilium/cilium --version 1.12.0 \
  --namespace kube-system \
  --set kubeProxyReplacement=partial \
  --set hostServices.enabled=false \
  --set externalIPs.enabled=true \
  --set nodePort.enabled=true \
  --set hostPort.enabled=true \
  --set bpf.masquerade=false \
  --set image.pullPolicy=IfNotPresent \
  --set ipam.mode=kubernetes

# Cilium Network Policy
apiVersion: cilium.io/v2
kind: CiliumNetworkPolicy
metadata:
  name: allow-http
spec:
  endpointSelector:
    matchLabels:
      app: web
  ingress:
  - fromEndpoints:
    - matchLabels:
        app: client
    toPorts:
    - ports:
      - port: "80"
        protocol: TCP
```

## **IPv6** поддержка

### **Dual-stack** конфигурация
```yaml
# kube-apiserver с IPv6
--bind-address=::
--advertise-address=2001:db8::1
--service-cluster-ip-range=10.96.0.0/12,2001:db8:1::/108

# kube-controller-manager
--cluster-cidr=10.244.0.0/16,2001:db8:2::/56
--service-cluster-ip-range=10.96.0.0/12,2001:db8:1::/108

# kubelet
--node-ip=2001:db8::10

# Service с IPv6
apiVersion: v1
kind: Service
metadata:
  name: nginx-ipv6
spec:
  ipFamilyPolicy: PreferDualStack
  ipFamilies:
  - IPv6
  - IPv4
  selector:
    app: nginx
  ports:
  - port: 80
    targetPort: 80
  type: ClusterIP

# Pod с IPv6
apiVersion: v1
kind: Pod
metadata:
  name: ipv6-pod
spec:
  containers:
  - name: nginx
    image: nginx
  hostNetwork: true  # Для IPv6 host networking
```

## **Service Discovery**

### **EndpointSlices**
```yaml
# EndpointSlice для большого количества endpoints
apiVersion: discovery.k8s.io/v1
kind: EndpointSlice
metadata:
  name: my-service-abc
  labels:
    kubernetes.io/service-name: my-service
addressType: IPv4
ports:
- name: http
  protocol: TCP
  port: 80
endpoints:
- addresses:
  - "10.1.2.3"
  - "10.1.2.4"
  - "10.1.2.5"
- addresses:
  - "10.1.3.7"
  conditions:
    ready: true
  hostname: pod-2
  nodeName: node-2
  zone: us-west2-a

# Проверка EndpointSlices
kubectl get endpointslices
kubectl describe endpointslice my-service-abc
```

### **Headless Services**
```yaml
# Headless Service для прямого доступа к подам
apiVersion: v1
kind: Service
metadata:
  name: headless-service
spec:
  clusterIP: None  # Headless
  selector:
    app: myapp
  ports:
  - port: 80
    targetPort: 8080

# StatefulSet с headless service
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: web
spec:
  serviceName: headless-service
  replicas: 3
  selector:
    matchLabels:
      app: myapp
  template:
    metadata:
      labels:
        app: myapp
    spec:
      containers:
      - name: nginx
        image: nginx
        ports:
        - containerPort: 80
          name: web

# Доступ к конкретным подам
kubectl exec -it web-0 -- curl http://web-0.headless-service:80
kubectl exec -it web-1 -- curl http://web-1.headless-service:80
```

## **Performance** и масштабирование

### **Network performance tuning**
```bash
# Оптимизация sysctl параметров
kubectl apply -f - <<EOF
apiVersion: v1
kind: ConfigMap
metadata:
  name: sysctl-config
  namespace: kube-system
data:
  net.core.somaxconn: "1024"
  net.core.netdev_max_backlog: "5000"
  net.ipv4.tcp_max_syn_backlog: "1024"
  net.ipv4.ip_local_port_range: "1024 65535"
  net.ipv4.tcp_tw_reuse: "1"
  net.ipv4.tcp_fin_timeout: "15"
  net.core.rmem_max: "16777216"
  net.core.wmem_max: "16777216"
  net.ipv4.tcp_rmem: "4096 87380 16777216"
  net.ipv4.tcp_wmem: "4096 65536 16777216"
EOF

# Применение к узлам
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: sysctl-tuning
  namespace: kube-system
spec:
  selector:
    matchLabels:
      app: sysctl-tuning
  template:
    metadata:
      labels:
        app: sysctl-tuning
    spec:
      hostPID: true
      containers:
      - name: sysctl
        image: alpine
        securityContext:
          privileged: true
        command: ["/bin/sh", "-c"]
        args:
        - |
          sysctl -w net.core.somaxconn=1024
          sysctl -w net.core.netdev_max_backlog=5000
          tail -f /dev/null
EOF
```

### **Load balancing optimization**
```yaml
# MetalLB с BGP
apiVersion: metallb.io/v1beta1
kind: BGPAdvertisement
metadata:
  name: bgp-advertisement
  namespace: metallb-system
spec:
  aggregationLength: 32
  aggregationLengthV6: 128
  communities:
  - "64512:100"
  ipAddressPools:
  - first-pool
  localPref: 100

# Nginx Ingress tuning
apiVersion: v1
kind: ConfigMap
metadata:
  name: nginx-config
  namespace: ingress-nginx
data:
  use-forwarded-headers: "true"
  worker-processes: "auto"
  worker-connections: "1024"
  keep-alive: "75"
  upstream-keepalive-connections: "100"
  client-max-body-size: "50m"
```

## Лучшие практики

- **Сеть подов:** каждый под получает уникальный `IP`; не полагайтесь на `IP` подов для постоянной связи — используйте **Services** и **DNS**.
- **Network `Policies`:** включайте по умолчанию **deny** и разрешайте только нужный трафик; тестируйте политики в **non-prod** перед применением.
- **Ingress:** один **Ingress**-контроллер на кластер (**или по namespace**); **TLS termination** на **Ingress**; ограничение размера тела и **rate limiting**.
- **Service `Mesh`:** внедряйте **Istio**/**Linkerd** при необходимости **mTLS**, **observability** и продвинутой маршрутизации; учитывайте накладные расходы.
- **DNS и `Load Balancing`:** используйте внутренние **DNS**-имена сервисов; настройте **readiness**/**liveness** для корректного исключения подов из балансировки.
## См. также
- [[kubernetes-advanced|Kubernetes Advanced]] — продвинутые концепции **K8s**
- [[docker-basics|Docker Basics]] — контейнеризация
- [[kubernetes-storage|Kubernetes Storage]] — хранение данных в **K8s**
- [[prometheus|Prometheus]] — мониторинг
